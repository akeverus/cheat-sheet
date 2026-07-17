package com.cheatsheet.quiz.service.imports;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
import org.apache.commons.lang3.StringUtils;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис импорта вопросов из markdown-файлов в БД.
 *
 * <p>Сканирует директорию {@code app.interview-path} и для каждого {@code .md}-файла:</p>
 * <ul>
 *   <li>Парсит вопросы через {@link MarkdownQuestionParser};</li>
 *   <li>Вставляет новые вопросы ({@code INSERT});</li>
 *   <li>Обновляет изменённые вопросы ({@code UPDATE} + сброс прогресса).</li>
 * </ul>
 *
 * <p>Обнаружение изменений — по SHA-256 хешу контента ({@link HashingService}).</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionImportService {

    private final InterviewPathResolver interviewPathResolver;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final ReviewStateRepository reviewStateRepository;
    private final QuestionRevisionRepository questionRevisionRepository;
    private final FullTextSearchRepository fullTextSearchRepository;
    private final OptionCache optionCache;
    private final Clock clock;
    private final MarkdownQuestionParser parser;
    private final HashingService hashingService;
    private final TransactionTemplate transactionTemplate;
    private final McqJsonLoader mcqJsonLoader;

    /**
     * Импортирует все markdown-файлы из директории {@code app.interview-path}.
     *
     * <p>Держит короткие транзакции на уровне отдельного upsert вопроса, чтобы
     * не блокировать БД во время IO/AI-операций.</p>
     */
    public void importAll() {
        Path root = interviewPathResolver.getBasePath();
        if (!Files.exists(root)) {
            log.warn("Директория с вопросами не найдена: {}", root.toAbsolutePath());
            return;
        }

        int inserted = 0;
        int updated = 0;
        int unchanged = 0;
        Set<String> actualPaths = new HashSet<>();
        // Признак ПОЛНОСТЬЮ успешного скана: выставляется ТОЛЬКО после того, как
        // walk прошёл целиком и все файлы обработаны. Без него orphan-cleanup ниже
        // выполнялся безусловно: при IOException скана (например, директория
        // app.interview-path существует, но нечитаема) actualPaths оставался
        // пустым, и cleanup удалял ВЕСЬ банк вопросов, каскадом затирая
        // answer_options/question_hints/review_state (невосстановимый SM-2 прогресс).
        boolean scanCompleted = false;

        try (var fileStream = Files.walk(root)) {
            List<Path> files = fileStream
                    .filter(path -> path.toString().endsWith(MarkdownQuestionParser.MARKDOWN_FILE_SUFFIX))
                    .toList();
            for (Path file : files) {
                String relativePath = root.relativize(file).toString().replace("\\", "/");
                actualPaths.add(relativePath);
                ImportResult result = importFile(root, file);
                inserted += result.inserted();
                updated += result.updated();
                unchanged += result.unchanged();
            }
            scanCompleted = true;
        } catch (IOException e) {
            log.error("Ошибка сканирования директории с вопросами", e);
        }

        // --- Orphan cleanup: удаляем вопросы, чьи MD-файлы удалены ---
        // Гард безопасности: чистим осиротевшие вопросы ТОЛЬКО когда скан прошёл
        // целиком И нашёл хотя бы один файл. Пустой actualPaths (сбой скана или
        // случайно пустая/нечитаемая директория) НЕ должен приводить к удалению
        // всей базы — это деструктивно и почти всегда означает misconfiguration,
        // а не намеренное удаление всех вопросов.
        int deletedOrphans = 0;
        if (scanCompleted && !actualPaths.isEmpty()) {
            List<String> existingPaths = questionRepository.findAllFilePaths();
            for (String dbPath : existingPaths) {
                if (!actualPaths.contains(dbPath)) {
                    int count = questionRepository.deleteByFilePath(dbPath);
                    log.info("Orphan cleanup: удалено {} вопросов из файла {}", count, dbPath);
                    deletedOrphans += count;
                }
            }
        } else {
            log.warn("Orphan cleanup пропущен: скан не завершён успешно или не найдено ни одного файла "
                    + "(scanCompleted={}, найдено путей={}). Банк вопросов не тронут.",
                    scanCompleted, actualPaths.size());
        }

        long total = questionRepository.countAll();
        log.info("Импорт: +{} новых, ~{} обновлено, -{} удалено (orphan), ={} неизменённых. Итого: {} вопросов.",
                inserted, updated, deletedOrphans, unchanged, total);
    }

    /**
     * Импортирует вопросы из одного markdown-файла.
     *
     * @param root корневая директория
     * @param file путь к markdown-файлу
     * @return количество вставленных и обновлённых вопросов
     */
    private ImportResult importFile(Path root, Path file) {
        String relativePath = root.relativize(file).toString().replace("\\", "/");
        String topic = relativePath.replaceAll(MarkdownQuestionParser.REGEX_MARKDOWN_EXTENSION, StringUtils.EMPTY);
        int inserted = 0;
        int updated = 0;
        int unchanged = 0;

        try {
            List<MarkdownQuestionParser.ParsedQuestion> items = parseQuestionsFromFile(file);
            for (MarkdownQuestionParser.ParsedQuestion item : items) {
                if (item.answerMarkdown() == null || item.answerMarkdown().isBlank()) {
                    continue;
                }
                // Канонизация через AI удалена — используем распарсенный вопрос напрямую.
                MarkdownQuestionParser.ParsedQuestion finalQuestion = item;
                UpsertOutcome outcome = transactionTemplate.execute(status -> upsertQuestion(finalQuestion, relativePath, topic));
                if (outcome == null) {
                    continue;
                }
                switch (outcome) {
                    case INSERT -> inserted++;
                    case UPDATE -> updated++;
                    case UNCHANGED -> unchanged++;
                }
            }
        } catch (IOException e) {
            log.warn("Ошибка парсинга файла: {}", file, e);
        }

        // Загружаем MCQ-сиды из JSON один раз на файл (не на вопрос).
        Path relPath = root.relativize(file);
        String categoryPath = relPath.getParent() == null ? "" : relPath.getParent().toString().replace("\\", "/");
        String topicSlug = relPath.getFileName().toString().replaceFirst("\\.md$", "");
        try {
            McqLoadResult result = mcqJsonLoader.loadForTopic(categoryPath, topicSlug);
            if (result.found()) {
                log.info("Loaded {} options for topic {} ({} questions skipped)",
                        result.optionsInserted(), topicSlug, result.questionsSkipped());
            }
        } catch (Exception e) {
            log.warn("Ошибка загрузки MCQ-сидов для {}/{}: {}", categoryPath, topicSlug, e.getMessage(), e);
        }
        return new ImportResult(inserted, updated, unchanged);
    }

    /**
     * Читает и парсит markdown-файл с вопросами.
     *
     * @param file путь к markdown-файлу
     * @return список распарсенных вопросов
     * @throws IOException при ошибке чтения файла
     */
    private List<MarkdownQuestionParser.ParsedQuestion> parseQuestionsFromFile(Path file) throws IOException {
        return parser.parse(file);
    }

    /**
     * Вставляет новый вопрос или обновляет существующий по slug. Сравнение по SHA-256 хешу контента.
     *
     * @param parsed       распарсенный вопрос из файла
     * @param relativePath относительный путь файла
     * @param topic        тема (из пути)
     * @return INSERT — создан новый, UPDATE — обновлён, UNCHANGED — контент не изменился
     */
    private UpsertOutcome upsertQuestion(MarkdownQuestionParser.ParsedQuestion parsed, String relativePath, String topic) {
        String slug = relativePath + "#Q" + parsed.questionNumber();
        String sourceHash = hashingService.sha256(parsed.questionText() + "\n" + parsed.rawAnswer());
        Optional<Question> existing = questionRepository.findBySlug(slug);

        if (existing.isEmpty()) {
            Question question = Question.forImport(slug, relativePath, topic,
                    parsed.questionText(), parsed.answerMarkdown(), parsed.important(), sourceHash,
                    parsed.questionType(), parsed.codeSnippet());
            long id = questionRepository.insert(question);
            reviewStateRepository.insertIfAbsent(id, clock.instant().getEpochSecond());
            questionRevisionRepository.append(id, sourceHash, clock.instant().getEpochSecond());
            fullTextSearchRepository.upsert(id, question.questionText(), question.answerMarkdown());
            return UpsertOutcome.INSERT;
        }

        if (!existing.get().sourceHash().equals(sourceHash)) {
            Question current = existing.get();
            Question updatedQuestion = new Question(current.id(), current.slug(), current.sourceSlug(), relativePath, topic,
                    parsed.questionText(), parsed.answerMarkdown(), parsed.important(), sourceHash,
                    parsed.questionType(), parsed.codeSnippet(), current.diagramMermaid(), current.regenCount(), current.takeaway());
            questionRepository.update(updatedQuestion);
            answerOptionRepository.deleteByQuestionId(current.id());
            optionCache.invalidate(current.id());
            reviewStateRepository.reset(current.id(), clock.instant().getEpochSecond());
            questionRevisionRepository.append(current.id(), sourceHash, clock.instant().getEpochSecond());
            fullTextSearchRepository.upsert(current.id(), updatedQuestion.questionText(), updatedQuestion.answerMarkdown());
            return UpsertOutcome.UPDATE;
        }

        return UpsertOutcome.UNCHANGED;
    }

    /** Результат импорта одного файла. */
    @Builder(toBuilder = true)
    private record ImportResult(int inserted, int updated, int unchanged) {}

    /** Результат вставки/обновления одного вопроса. */
    private enum UpsertOutcome { INSERT, UPDATE, UNCHANGED }
}
