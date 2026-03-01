package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.ai.OptionGenerationService;
import com.cheatsheet.quiz.service.strategy.DefaultSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.ShuffleSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.WeakTopicsSelectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock QuestionStatsRepository questionStatsRepository;
    @Mock AnswerOptionRepository answerOptionRepository;
    @Mock ReviewStateRepository reviewStateRepository;
    @Mock OptionGenerationService optionGenerationService;
    @Mock PreloadService preloadService;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock DefaultSelectionStrategy defaultSelectionStrategy;
    @Mock ShuffleSelectionStrategy shuffleSelectionStrategy;
    @Mock WeakTopicsSelectionStrategy weakTopicsSelectionStrategy;
    @Mock TopicCatalogService topicCatalogService;
    @Mock TrainingSessionService trainingSessionService;
    @Mock ReviewService reviewService;

    private final Clock clock = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
    private InterviewService interviewService;

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties();
        props.getInterview().setLearnedRepetitions(3);
        props.getInterview().setExamPenaltyQuestions(2);
        interviewService = new InterviewService(
                questionRepository,
                questionStatsRepository,
                answerOptionRepository,
                reviewStateRepository,
                optionGenerationService,
                preloadService,
                eventPublisher,
                defaultSelectionStrategy,
                shuffleSelectionStrategy,
                weakTopicsSelectionStrategy,
                topicCatalogService,
                trainingSessionService,
                reviewService,
                clock,
                props
        );
    }

    @Test
    void startSessionPreloadsSelectedQuestions() {
        InterviewFilter filter = new InterviewFilter("java", true, false, false);
        InterviewSession created = new InterviewSession(InterviewMode.EXAM, List.of(10L, 20L, 30L), "java", null, true, false, false, true);
        when(trainingSessionService.startSession(InterviewMode.EXAM, 3, filter)).thenReturn(created);

        InterviewSession session = interviewService.startSession(InterviewMode.EXAM, 3, filter);

        assertThat(session.getMode()).isEqualTo(InterviewMode.EXAM);
        assertThat(session.getQuestionIds()).containsExactly(10L, 20L, 30L);
        verify(trainingSessionService).startSession(InterviewMode.EXAM, 3, filter);
    }

    @Test
    void startSessionUsesOrderedTopicsForGroupFilter() {
        InterviewFilter filter = new InterviewFilter(null, "languages", false, false, false, true);
        InterviewSession created = new InterviewSession(InterviewMode.MARATHON, List.of(101L, 202L), null, "languages", false, false, false, true);
        when(trainingSessionService.startSession(InterviewMode.MARATHON, 2, filter)).thenReturn(created);

        InterviewSession session = interviewService.startSession(InterviewMode.MARATHON, 2, filter);

        assertThat(session.getQuestionIds()).containsExactly(101L, 202L);
        verify(trainingSessionService).startSession(InterviewMode.MARATHON, 2, filter);
    }

    @Test
    void submitAnswerUpdatesReviewStateAndReturnsResult() {
        InterviewFilter filter = new InterviewFilter(null, null, null, false);
        Question question = sampleQuestion(1L);
        List<AnswerOption> options = List.of(
                new AnswerOption(100L, 1L, "Correct", true, 0, "OPENAI", null),
                new AnswerOption(101L, 1L, "Wrong", false, 1, "OPENAI", null)
        );
        ReviewState updated = new ReviewState(1L, 1, 1, 2.6, 1_700_086_400L, ReviewResult.CORRECT, 1, 0);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(options);
        when(reviewService.applyAnswer(question, true, null)).thenReturn(updated);

        AnswerResult result = interviewService.submitAnswer(1L, 100L, filter, null);

        assertThat(result.correctAnswer()).isTrue();
        assertThat(result.correct().id()).isEqualTo(100L);
        assertThat(result.updatedState()).isEqualTo(updated);
        verify(reviewService).applyAnswer(question, true, null);
        verify(preloadService).preloadNext(filter);
    }

    @Test
    void submitAnswerThrowsWhenSelectedOptionMissing() {
        InterviewFilter filter = new InterviewFilter(null, null, null, false);
        Question question = sampleQuestion(1L);
        List<AnswerOption> options = List.of(
                new AnswerOption(201L, 1L, "Correct", true, 0, "OPENAI", null),
                new AnswerOption(202L, 1L, "Wrong", false, 1, "OPENAI", null)
        );

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(options);

        assertThatThrownBy(() -> interviewService.submitAnswer(1L, 999L, filter, null))
                .isInstanceOf(OptionNotFoundException.class);
        verify(reviewService, never()).applyAnswer(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyBoolean(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void submitAnswerThrowsWhenNoCorrectOptionExists() {
        InterviewFilter filter = new InterviewFilter(null, null, null, false);
        Question question = sampleQuestion(1L);
        List<AnswerOption> options = List.of(
                new AnswerOption(301L, 1L, "Wrong A", false, 0, "OPENAI", null),
                new AnswerOption(302L, 1L, "Wrong B", false, 1, "OPENAI", null)
        );

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(options);

        assertThatThrownBy(() -> interviewService.submitAnswer(1L, 301L, filter, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Нет правильного варианта ответа");
    }

    @Test
    void submitAnswerThrowsWhenMultipleCorrectOptionsExist() {
        InterviewFilter filter = new InterviewFilter(null, null, null, false);
        Question question = sampleQuestion(1L);
        List<AnswerOption> options = List.of(
                new AnswerOption(401L, 1L, "Correct A", true, 0, "OPENAI", null),
                new AnswerOption(402L, 1L, "Correct B", true, 1, "OPENAI", null)
        );

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(options);

        assertThatThrownBy(() -> interviewService.submitAnswer(1L, 401L, filter, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("несколько правильных вариантов");
    }

    @Test
    void nextQuestionSkipsExcludedQuestionId() {
        InterviewFilter filter = new InterviewFilter("topic", null, null, false);
        Question excluded = sampleQuestion(10L);
        Question alternative = sampleQuestion(11L);
        List<AnswerOption> options = List.of(
                new AnswerOption(1000L, 11L, "Correct", true, 0, "OPENAI", null),
                new AnswerOption(1001L, 11L, "Wrong", false, 1, "OPENAI", null)
        );

        when(preloadService.pollPreloaded(filter)).thenReturn(Optional.empty());
        when(defaultSelectionStrategy.selectNextQuestionId(filter, 1_700_000_000L)).thenReturn(Optional.of(10L));
        when(questionRepository.findById(10L)).thenReturn(Optional.of(excluded));
        when(questionRepository.findQuestionIdsExcluding(
                List.of(10L), "topic", null, null, 1_700_000_000L, 1
        )).thenReturn(List.of(11L));
        when(questionRepository.findById(11L)).thenReturn(Optional.of(alternative));
        when(reviewStateRepository.findByQuestionId(11L)).thenReturn(Optional.empty());
        when(optionGenerationService.getOrCreateOptions(alternative)).thenReturn(options);

        Optional<InterviewQuestion> result = interviewService.nextQuestion(filter, false, 10L);

        assertThat(result).isPresent();
        assertThat(result.get().question().id()).isEqualTo(11L);
    }

    @Test
    void nextQuestionSkipsExcludedQuestionIdForGroupFilter() {
        InterviewFilter filter = new InterviewFilter(null, "backend", null, false, false, true);
        Question excluded = sampleQuestion(21L);
        Question alternative = sampleQuestion(22L);
        List<AnswerOption> options = List.of(
                new AnswerOption(2200L, 22L, "Correct", true, 0, "OPENAI", null),
                new AnswerOption(2201L, 22L, "Wrong", false, 1, "OPENAI", null)
        );

        when(preloadService.pollPreloaded(filter)).thenReturn(Optional.empty());
        when(defaultSelectionStrategy.selectNextQuestionId(filter, 1_700_000_000L)).thenReturn(Optional.of(21L));
        when(questionRepository.findById(21L)).thenReturn(Optional.of(excluded));
        when(questionRepository.findTopics()).thenReturn(List.of("java", "spring"));
        when(topicCatalogService.topicsForFilter(filter, List.of("java", "spring")))
                .thenReturn(List.of("java", "spring"));
        when(questionRepository.findQuestionIdsExcludingByTopics(
                List.of(21L), List.of("java", "spring"), null, false, 1_700_000_000L, 1
        )).thenReturn(List.of(22L));
        when(questionRepository.findById(22L)).thenReturn(Optional.of(alternative));
        when(reviewStateRepository.findByQuestionId(22L)).thenReturn(Optional.empty());
        when(optionGenerationService.getOrCreateOptions(alternative)).thenReturn(options);

        Optional<InterviewQuestion> result = interviewService.nextQuestion(filter, false, 21L);

        assertThat(result).isPresent();
        assertThat(result.get().question().id()).isEqualTo(22L);
    }

    private Question sampleQuestion(long id) {
        return new Question(
                id,
                "slug-" + id,
                "source-" + id,
                "topic.md",
                "topic",
                "What is Java?",
                "Java is a language",
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
