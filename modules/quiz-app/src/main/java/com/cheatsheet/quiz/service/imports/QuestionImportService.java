package com.cheatsheet.quiz.service.imports;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
import org.apache.commons.lang3.StringUtils;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
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
import com.cheatsheet.quiz.domain.OptionSource;

import java.util.ArrayList;
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
    private final FullTextSearchRepository fullTextSearchRepository;
    private final OptionCache optionCache;
    private final Clock clock;
    private final MarkdownQuestionParser parser;
    private final HashingService hashingService;
    private final QuestionExpansionService questionExpansionService;
    private final AiQuestionClient aiQuestionClient;
    private final TransactionTemplate transactionTemplate;

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
        } catch (IOException e) {
            log.error("Ошибка сканирования директории с вопросами", e);
        }

        // --- Orphan cleanup: удаляем вопросы, чьи MD-файлы удалены ---
        int deletedOrphans = 0;
        List<String> existingPaths = questionRepository.findAllFilePaths();
        for (String dbPath : existingPaths) {
            if (!actualPaths.contains(dbPath)) {
                int count = questionRepository.deleteByFilePath(dbPath);
                log.info("Orphan cleanup: удалено {} вопросов из файла {}", count, dbPath);
                deletedOrphans += count;
            }
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
                Optional<MarkdownQuestionParser.ParsedQuestion> canonicalized = canonicalize(item, relativePath);
                if (canonicalized.isEmpty()) {
                    log.warn("Канонизация вопроса {} из {} не удалась — вопрос пропущен",
                            item.questionNumber(), relativePath);
                    continue;
                }
                MarkdownQuestionParser.ParsedQuestion finalQuestion = canonicalized.get();
                UpsertOutcome outcome = transactionTemplate.execute(status -> upsertQuestion(finalQuestion, relativePath, topic));
                if (outcome == null) {
                    continue;
                }
                switch (outcome) {
                    case INSERT -> {
                        inserted++;
                        inserted += ensureExpansionVariants(relativePath, finalQuestion, topic);
                    }
                    case UPDATE -> {
                        updated++;
                        inserted += ensureExpansionVariants(relativePath, finalQuestion, topic);
                    }
                    case UNCHANGED -> {
                        unchanged++;
                        inserted += ensureExpansionVariants(relativePath, finalQuestion, topic);
                    }
                }
            }
        } catch (IOException e) {
            log.warn("Ошибка парсинга файла: {}", file, e);
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

    private Optional<MarkdownQuestionParser.ParsedQuestion> canonicalize(
            MarkdownQuestionParser.ParsedQuestion parsed,
            String relativePath
    ) {
        Optional<AiQuestionClient.CanonicalQuestion> canonical = aiQuestionClient.canonicalizeQuestion(
                parsed.questionText(),
                parsed.answerMarkdown(),
                relativePath
        );
        if (canonical.isEmpty()) {
            log.info("Канонизация недоступна для {}#Q{} — используем fallback из исходного markdown",
                    relativePath, parsed.questionNumber());
            return Optional.of(parsed);
        }
        AiQuestionClient.CanonicalQuestion value = canonical.get();
        QuestionType questionType = resolveQuestionType(parsed, value, relativePath);
        return Optional.of(parsed.toBuilder()
                .questionText(value.questionText())
                .answerMarkdown(value.answerMarkdown())
                .questionType(questionType)
                .codeSnippet(questionType == QuestionType.CODE ? value.codeSnippet() : null)
                .build());
    }

    private QuestionType resolveQuestionType(
            MarkdownQuestionParser.ParsedQuestion parsed,
            AiQuestionClient.CanonicalQuestion canonical,
            String relativePath
    ) {
        QuestionType parserType = parsed.questionType() == null ? QuestionType.TEXT : parsed.questionType();
        QuestionType canonicalType = "CODE".equalsIgnoreCase(canonical.questionType()) ? QuestionType.CODE : QuestionType.TEXT;
        boolean canonicalHasCode = canonical.codeSnippet() != null && !canonical.codeSnippet().isBlank();
        if (canonicalType == QuestionType.CODE && !canonicalHasCode) {
            log.warn("Канонизация вернула CODE без codeSnippet для {}#Q{} — используем parser type={}",
                    relativePath, parsed.questionNumber(), parserType);
            return parserType;
        }
        if (parserType == QuestionType.CODE && canonicalType == QuestionType.TEXT) {
            log.warn("Type mismatch parser=CODE, canonical=TEXT для {}#Q{} — сохраняем parser type",
                    relativePath, parsed.questionNumber());
            return parserType;
        }
        return canonicalType;
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
            fullTextSearchRepository.upsert(id, question.questionText(), question.answerMarkdown());
            if (parsed.hasMcqOptions()) {
                upsertOptionsFromMd(id, parsed.options());
            }
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
            fullTextSearchRepository.upsert(current.id(), updatedQuestion.questionText(), updatedQuestion.answerMarkdown());
            if (parsed.hasMcqOptions()) {
                upsertOptionsFromMd(current.id(), parsed.options());
            }
            return UpsertOutcome.UPDATE;
        }

        // UNCHANGED: restore MD options if they were manually deleted
        if (parsed.hasMcqOptions() && answerOptionRepository.countByQuestionId(existing.get().id()) == 0) {
            upsertOptionsFromMd(existing.get().id(), parsed.options());
        }
        return UpsertOutcome.UNCHANGED;
    }

    private int ensureExpansionVariants(String relativePath, MarkdownQuestionParser.ParsedQuestion base, String topic) {
        String baseSlug = relativePath + "#Q" + base.questionNumber();
        Optional<Question> baseQuestion = questionRepository.findBySlug(baseSlug);
        if (baseQuestion.isEmpty()) {
            return 0;
        }
        int existingVariants = questionRepository.countExpandedBySourceSlug(baseQuestion.get().sourceSlug());
        if (existingVariants > 0) {
            return 0;
        }
        try {
            return questionExpansionService.expandFromBase(baseQuestion.get(), topic, relativePath);
        } catch (Exception e) {
            log.warn("Расширение вопроса {} не удалось: {}", baseQuestion.get().slug(), e.getMessage(), e);
            return 0;
        }
    }

    private void upsertOptionsFromMd(long questionId, List<MarkdownQuestionParser.ParsedOption> options) {
        List<AnswerOptionRepository.AnswerOptionCreate> creates = new ArrayList<>(options.size());
        for (int i = 0; i < options.size(); i++) {
            MarkdownQuestionParser.ParsedOption opt = options.get(i);
            creates.add(new AnswerOptionRepository.AnswerOptionCreate(
                    opt.text(), opt.correct(), i,
                    OptionSource.MARKDOWN.name(), opt.explanation(),
                    1, 2));
        }
        answerOptionRepository.insertAll(questionId, creates);
    }

    /** Результат импорта одного файла. */
    @Builder(toBuilder = true)
    private record ImportResult(int inserted, int updated, int unchanged) {}

    /** Результат вставки/обновления одного вопроса. */
    private enum UpsertOutcome { INSERT, UPDATE, UNCHANGED }
}
