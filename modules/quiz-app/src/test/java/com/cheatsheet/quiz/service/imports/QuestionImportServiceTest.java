package com.cheatsheet.quiz.service.imports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
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

    private QuestionImportService newService(Clock clock) {
        return new QuestionImportService(
                interviewPathResolver,
                questionRepository,
                answerOptionRepository,
                reviewStateRepository,
                fullTextSearchRepository,
                optionCache,
                clock,
                parser,
                hashingService,
                transactionTemplate,
                mcqJsonLoader
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
}
