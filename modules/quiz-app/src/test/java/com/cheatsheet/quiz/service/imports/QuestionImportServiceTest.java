package com.cheatsheet.quiz.service.imports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
import com.cheatsheet.quiz.common.util.TopicDisplayNames;
import com.cheatsheet.quiz.common.util.TopicUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class QuestionImportServiceTest {

    @Mock
    InterviewPathResolver interviewPathResolver;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    AnswerOptionRepository answerOptionRepository;
    @Mock
    ReviewStateRepository reviewStateRepository;
    @Mock
    QuestionRevisionRepository questionRevisionRepository;
    @Mock
    FullTextSearchRepository fullTextSearchRepository;
    @Mock
    OptionCache optionCache;
    @Mock
    MarkdownQuestionParser parser;
    @Mock
    HashingService hashingService;
    @Mock
    TransactionTemplate transactionTemplate;
    @Mock
    McqJsonLoader mcqJsonLoader;

    // Реестр названий и чтение frontmatter — не моки: проверяем, что импорт
    // действительно кладёт в реестр имя из файла, а не просто дёргает коллаборатора.
    private final TopicDisplayNames topicDisplayNames = new TopicDisplayNames();
    private final TopicTitleReader topicTitleReader = new TopicTitleReader();
    private final TopicUtils topicUtils = new TopicUtils(topicDisplayNames);

    private QuestionImportService newService(Clock clock) {
        return new QuestionImportService(
                interviewPathResolver,
                questionRepository,
                answerOptionRepository,
                reviewStateRepository,
                questionRevisionRepository,
                fullTextSearchRepository,
                optionCache,
                clock,
                parser,
                hashingService,
                transactionTemplate,
                mcqJsonLoader,
                topicTitleReader,
                topicDisplayNames,
                topicUtils
        );
    }

    @Test
    void importAllInsertsParsedQuestionFromMarkdown() throws Exception {
        Path root = Files.createTempDirectory("question-import-test");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");

        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Что такое seed-импорт?")
                .answerMarkdown("Это импорт вопросов из markdown без AI.")
                .rawAnswer("Это импорт вопросов из markdown без AI.")
                .important(false)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .build();

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(hashingService.sha256(any())).thenReturn("hash");
        when(questionRepository.findBySlug(any())).thenReturn(Optional.empty());
        when(questionRepository.insert(any())).thenReturn(42L);
        when(questionRepository.findAllFilePaths()).thenReturn(List.of());
        when(questionRepository.countAll()).thenReturn(1L);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        Clock clock = Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC);
        QuestionImportService service = newService(clock);

        service.importAll();

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).insert(captor.capture());
        Question inserted = captor.getValue();
        assertThat(inserted.questionText()).isEqualTo(parsed.questionText());
        assertThat(inserted.answerMarkdown()).isEqualTo(parsed.answerMarkdown());
        assertThat(inserted.questionType()).isEqualTo(QuestionType.TEXT);
        // Фаза 2: на INSERT нового вопроса создаётся первая ревизия контента.
        verify(questionRevisionRepository)
                .append(42L, "hash", Instant.parse("2026-02-28T00:00:00Z").getEpochSecond());
    }

    @Test
    void importAllAppendsRevisionWhenContentHashChanges() throws Exception {
        Path root = Files.createTempDirectory("question-import-update");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");

        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Изменённый вопрос?")
                .answerMarkdown("Изменённый ответ.")
                .rawAnswer("Изменённый ответ.")
                .important(false)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .build();

        Question existing = Question.forImport("topic/sample.md#Q1", "topic/sample.md", "topic/sample",
                "Старый вопрос?", "Старый ответ.", false, "old-hash", QuestionType.TEXT, null)
                .toBuilder().id(7L).build();

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(hashingService.sha256(any())).thenReturn("new-hash");
        when(questionRepository.findBySlug(any())).thenReturn(Optional.of(existing));
        when(questionRepository.findAllFilePaths()).thenReturn(List.of("topic/sample.md"));
        when(questionRepository.countAll()).thenReturn(1L);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        Clock clock = Clock.fixed(Instant.parse("2026-03-10T00:00:00Z"), ZoneOffset.UTC);
        QuestionImportService service = newService(clock);

        service.importAll();

        // Контент изменился (old-hash → new-hash) → новая ревизия с новым checksum.
        verify(questionRevisionRepository)
                .append(7L, "new-hash", Instant.parse("2026-03-10T00:00:00Z").getEpochSecond());
    }

    /**
     * Хеш сторожит текст вопроса, но не парсер, который его разбирает. Когда парсер
     * научился отличать {@code ```mermaid} от обычного кода, у 1230 вопросов из 1231
     * диаграмма так и осталась пустой: текст с тех пор не менялся, и ветка UNCHANGED
     * просто выходила. Схема лежала в answer_markdown, но экран её не показывал.
     */
    @Test
    void importAllRederivesDiagramWhenTextUnchangedButParserLearnedMore() throws Exception {
        Path root = Files.createTempDirectory("question-import-rederive");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");

        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Как устроен двухфазный коммит?")
                .answerMarkdown("Ответ со схемой.")
                .rawAnswer("Ответ со схемой.")
                .important(false)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid("graph TD; A-->B;")
                .build();

        // Тот же текст, тот же хеш — и пустая диаграмма: строка импортирована
        // парсером, который ещё не умел mermaid.
        Question existing = Question.forImport("topic/sample.md#Q1", "topic/sample.md", "topic/sample",
                "Как устроен двухфазный коммит?", "Ответ со схемой.", false, "same-hash",
                QuestionType.TEXT, null)
                .toBuilder().id(7L).build();

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(hashingService.sha256(any())).thenReturn("same-hash");
        when(questionRepository.findBySlug(any())).thenReturn(Optional.of(existing));
        when(questionRepository.findAllFilePaths()).thenReturn(List.of("topic/sample.md"));
        when(questionRepository.countAll()).thenReturn(1L);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        newService(Clock.fixed(Instant.parse("2026-08-05T00:00:00Z"), ZoneOffset.UTC)).importAll();

        verify(questionRepository).updateDerivedContent(7L, QuestionType.TEXT, null, "graph TD; A-->B;");
        // Переразбор — не правка контента. Пользователь ничего не терял, поэтому
        // прогресс, варианты и история ревизий обязаны остаться нетронутыми:
        // без этой половины теста прошёл бы и вариант, который зовёт update()
        // и заодно сбрасывает SM-2 всему банку на первом же старте.
        verify(questionRepository, never()).update(any());
        verify(reviewStateRepository, never()).reset(anyLong(), anyLong());
        verify(answerOptionRepository, never()).deleteByQuestionId(anyLong());
        verify(questionRevisionRepository, never()).append(anyLong(), any(), anyLong());
    }

    @Test
    void importAllWritesNothingWhenDerivedFieldsAlreadyMatch() throws Exception {
        // Сверка производных полей идёт на КАЖДОМ старте по всему банку — она не
        // имеет права генерировать запись, когда переразбирать нечего.
        Path root = Files.createTempDirectory("question-import-noop");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");

        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Вопрос?")
                .answerMarkdown("Ответ.")
                .rawAnswer("Ответ.")
                .important(false)
                .questionType(QuestionType.CODE)
                .codeSnippet("var x = 1;")
                .build();

        Question existing = Question.forImport("topic/sample.md#Q1", "topic/sample.md", "topic/sample",
                "Вопрос?", "Ответ.", false, "same-hash", QuestionType.CODE, "var x = 1;")
                .toBuilder().id(7L).build();

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(hashingService.sha256(any())).thenReturn("same-hash");
        when(questionRepository.findBySlug(any())).thenReturn(Optional.of(existing));
        when(questionRepository.findAllFilePaths()).thenReturn(List.of("topic/sample.md"));
        when(questionRepository.countAll()).thenReturn(1L);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        newService(Clock.fixed(Instant.parse("2026-08-05T00:00:00Z"), ZoneOffset.UTC)).importAll();

        verify(questionRepository, never()).updateDerivedContent(anyLong(), any(), any(), any());
        verify(questionRepository, never()).update(any());
    }

    @Test
    void importAllSkipsOrphanCleanupWhenScanFindsNoFiles() throws Exception {
        // Регресс-гард data-loss: если скан не нашёл ни одного .md (пустая или
        // нечитаемая директория, сбой обхода) — orphan-cleanup ОБЯЗАН быть
        // пропущен, иначе пустой actualPaths удалил бы весь банк вопросов
        // каскадом по FK (answer_options/question_hints/review_state).
        Path root = Files.createTempDirectory("question-import-empty");
        when(interviewPathResolver.getBasePath()).thenReturn(root);

        Clock clock = Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC);
        QuestionImportService service = newService(clock);

        service.importAll();

        // Cleanup пропущен целиком: ни запроса всех путей, ни единого удаления.
        verify(questionRepository, never()).findAllFilePaths();
        verify(questionRepository, never()).deleteByFilePath(any());
    }

    @Test
    void importAllRegistersTopicDisplayNameFromFrontmatter() throws Exception {
        // Имя темы для UI берётся из заголовка файла, а не восстанавливается из slug-а:
        // из «cockroachdb» правильное «CockroachDB» не собрать никакими правилами.
        Path root = Files.createTempDirectory("question-import-title");
        Path file = Files.createDirectories(root.resolve("databases")).resolve("cockroachdb-interview.md");
        Files.writeString(file, """
                ---
                title: "Вопросы на собеседовании: CockroachDB"
                tags:
                  - databases
                ---

                ## Q1. Что такое CockroachDB?
                Распределённая SQL-БД.
                """);

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of());
        when(questionRepository.findAllFilePaths()).thenReturn(List.of());
        when(questionRepository.countAll()).thenReturn(0L);
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        newService(Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC)).importAll();

        assertThat(topicDisplayNames.lookup("cockroachdb")).isEqualTo("CockroachDB");
        assertThat(topicUtils.displayName("databases/cockroachdb-interview.md")).isEqualTo("CockroachDB");
    }

    @Test
    void importAllLeavesTopicUnnamedWhenFileHasNoFrontmatter() throws Exception {
        // Без frontmatter регистрировать нечего — UI сам отрисует slug с заглавной.
        Path root = Files.createTempDirectory("question-import-no-title");
        Path file = Files.createDirectories(root.resolve("misc")).resolve("plain-interview.md");
        Files.writeString(file, "## Q1. Вопрос без frontmatter?\nОтвет.\n");

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of());
        when(questionRepository.findAllFilePaths()).thenReturn(List.of());
        when(questionRepository.countAll()).thenReturn(0L);
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        newService(Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC)).importAll();

        assertThat(topicDisplayNames.lookup("plain")).isNull();
        assertThat(topicUtils.displayName("misc/plain-interview.md")).isEqualTo("Plain");
    }
}
