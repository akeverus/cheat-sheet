package com.cheatsheet.quiz.service.imports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
import com.cheatsheet.quiz.service.imports.McqLoadResult;
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
    QuestionExpansionService questionExpansionService;
    @Mock
    AiQuestionClient aiQuestionClient;
    @Mock
    TransactionTemplate transactionTemplate;
    @Mock
    McqJsonLoader mcqJsonLoader;

    @Test
    void importAllUsesMarkdownFallbackWhenCanonicalizationUnavailable() throws Exception {
        Path root = Files.createTempDirectory("question-import-test");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");

        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Что такое fallback-импорт?")
                .answerMarkdown("Это путь без канонизации AI.")
                .rawAnswer("Это путь без канонизации AI.")
                .important(false)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .build();

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(aiQuestionClient.canonicalizeQuestion(any(), any(), any())).thenReturn(Optional.empty());
        when(hashingService.sha256(any())).thenReturn("hash");
        when(questionRepository.findBySlug(any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Question(
                        42L,
                        "topic/sample.md#Q1",
                        "topic/sample.md#Q1",
                        "topic/sample.md",
                        "topic/sample",
                        parsed.questionText(),
                        parsed.answerMarkdown(),
                        false,
                        "hash",
                        QuestionType.TEXT,
                        null,
                        null,
                        0,
                        null
                )));
        when(questionRepository.insert(any())).thenReturn(42L);
        when(questionRepository.findAllFilePaths()).thenReturn(List.of());
        when(questionRepository.countAll()).thenReturn(1L);
        when(questionRepository.countExpandedBySourceSlug(any())).thenReturn(0);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        Clock clock = Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC);
        QuestionImportService service = new QuestionImportService(
                interviewPathResolver,
                questionRepository,
                answerOptionRepository,
                reviewStateRepository,
                fullTextSearchRepository,
                optionCache,
                clock,
                parser,
                hashingService,
                questionExpansionService,
                aiQuestionClient,
                transactionTemplate,
                mcqJsonLoader
        );

        service.importAll();

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).insert(captor.capture());
        Question inserted = captor.getValue();
        assertThat(inserted.questionText()).isEqualTo(parsed.questionText());
        assertThat(inserted.answerMarkdown()).isEqualTo(parsed.answerMarkdown());
        assertThat(inserted.questionType()).isEqualTo(QuestionType.TEXT);
    }

    @Test
    void importAllRecoversMissingExpansionsForUnchangedQuestion() throws Exception {
        Path root = Files.createTempDirectory("question-import-recover-expansion");
        Path file = Files.createDirectories(root.resolve("topic")).resolve("sample.md");
        Files.writeString(file, "stub");
        MarkdownQuestionParser.ParsedQuestion parsed = MarkdownQuestionParser.ParsedQuestion.builder()
                .questionNumber("1")
                .questionText("Как работает compare-and-swap?")
                .answerMarkdown("CAS сравнивает ожидаемое и текущее значение атомарно.")
                .rawAnswer("CAS сравнивает ожидаемое и текущее значение атомарно.")
                .important(false)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .build();
        Question existing = new Question(
                42L,
                "topic/sample.md#Q1",
                "topic/sample.md#Q1",
                "topic/sample.md",
                "topic/sample",
                parsed.questionText(),
                parsed.answerMarkdown(),
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );

        when(interviewPathResolver.getBasePath()).thenReturn(root);
        when(parser.parse(file)).thenReturn(List.of(parsed));
        when(aiQuestionClient.canonicalizeQuestion(any(), any(), any())).thenReturn(Optional.empty());
        when(hashingService.sha256(any())).thenReturn("hash");
        when(questionRepository.findBySlug(any())).thenReturn(Optional.of(existing));
        when(questionRepository.findAllFilePaths()).thenReturn(List.of("topic/sample.md"));
        when(questionRepository.countAll()).thenReturn(1L);
        when(questionRepository.countExpandedBySourceSlug(any())).thenReturn(0);
        when(questionExpansionService.expandFromBase(any(), any(), any())).thenReturn(2);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(mcqJsonLoader.loadForTopic(any(), any())).thenReturn(McqLoadResult.notFound());

        Clock clock = Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC);
        QuestionImportService service = new QuestionImportService(
                interviewPathResolver,
                questionRepository,
                answerOptionRepository,
                reviewStateRepository,
                fullTextSearchRepository,
                optionCache,
                clock,
                parser,
                hashingService,
                questionExpansionService,
                aiQuestionClient,
                transactionTemplate,
                mcqJsonLoader
        );

        service.importAll();

        verify(questionExpansionService).expandFromBase(existing, "topic/sample", "topic/sample.md");
    }
}
