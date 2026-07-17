package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.FsrsRating;
import com.cheatsheet.quiz.domain.FsrsState;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.review.FsrsService;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewService;
import com.cheatsheet.quiz.feature.interview.service.review.SpacedRepetitionService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewStateRepository reviewStateRepository;
    @Mock
    private SpacedRepetitionService spacedRepetitionService;
    @Mock
    private FsrsService fsrsService;
    @Mock
    private UserTopicStatsRepository userTopicStatsRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AppProperties appProperties;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-02T00:00:00Z"), ZoneOffset.UTC);
        // По умолчанию планировщик — SM-2 (текущее поведение). lenient: не все тесты
        // ходят в applyAnswer/applyUnknown.
        lenient().when(appProperties.getScheduling()).thenReturn(new AppProperties.Scheduling());
        reviewService = new ReviewService(
                reviewStateRepository,
                spacedRepetitionService,
                fsrsService,
                userTopicStatsRepository,
                questionRepository,
                eventPublisher,
                appProperties,
                fixedClock
        );
    }

    private void useFsrsAlgorithm() {
        AppProperties.Scheduling fsrs = new AppProperties.Scheduling();
        fsrs.setAlgorithm("fsrs");
        when(appProperties.getScheduling()).thenReturn(fsrs);
    }

    @Test
    void applyAnswerUpdatesReviewStateAndTopicStats() {
        Question question = sampleQuestion(10L);
        ReviewState updated = new ReviewState(10L, 1, 1, 2.6, 1_777_777L, ReviewResult.CORRECT, 1, 0);

        when(reviewStateRepository.findByQuestionId(10L)).thenReturn(Optional.empty());
        when(spacedRepetitionService.applyAnswer(any(ReviewState.class), eq(true))).thenReturn(updated);

        ReviewState result = reviewService.applyAnswer(question, true, null);

        assertThat(result).isEqualTo(updated);
        verify(reviewStateRepository).update(updated);
        verify(userTopicStatsRepository).recordAnswer("topic", true);
    }

    @Test
    void applyAnswerWithFsrsSchedulesAndPersistsFsrsState() {
        useFsrsAlgorithm();
        Question question = sampleQuestion(20L);
        when(reviewStateRepository.findByQuestionId(20L)).thenReturn(Optional.empty());
        when(reviewStateRepository.findFsrsState(20L)).thenReturn(Optional.empty());
        FsrsService.Result scheduled = FsrsService.Result.builder()
                .stability(4.0).difficulty(5.0).lapses(0)
                .intervalDays(4).nextReviewAt(1_800_345_600L).lastReviewedAt(1_800_000_000L)
                .build();
        when(fsrsService.schedule(any(FsrsState.class), eq(FsrsRating.GOOD), anyLong())).thenReturn(scheduled);

        ReviewState display = reviewService.applyAnswer(question, true, null);

        ArgumentCaptor<FsrsState> captor = ArgumentCaptor.forClass(FsrsState.class);
        verify(reviewStateRepository).updateFsrs(captor.capture(), eq(1_800_345_600L), eq(ReviewResult.CORRECT), eq(1), eq(0));
        FsrsState persisted = captor.getValue();
        assertThat(persisted.stability()).isEqualTo(4.0);
        assertThat(persisted.difficulty()).isEqualTo(5.0);
        assertThat(persisted.algoVersion()).isEqualTo(FsrsService.ALGO_VERSION);
        // FSRS-путь НЕ трогает SM-2-колонки.
        verify(reviewStateRepository, never()).update(any());
        verify(userTopicStatsRepository).recordAnswer("topic", true);
        assertThat(display.nextReviewAt()).isEqualTo(1_800_345_600L);
        assertThat(display.intervalDays()).isEqualTo(4);
    }

    @Test
    void applyUnknownWithFsrsUsesAgainRatingAndUnknownResult() {
        useFsrsAlgorithm();
        Question question = sampleQuestion(21L);
        when(reviewStateRepository.findByQuestionId(21L)).thenReturn(Optional.empty());
        when(reviewStateRepository.findFsrsState(21L)).thenReturn(Optional.empty());
        FsrsService.Result scheduled = FsrsService.Result.builder()
                .stability(0.5).difficulty(7.0).lapses(1)
                .intervalDays(1).nextReviewAt(1_800_086_400L).lastReviewedAt(1_800_000_000L)
                .build();
        when(fsrsService.schedule(any(FsrsState.class), eq(FsrsRating.AGAIN), anyLong())).thenReturn(scheduled);

        reviewService.applyUnknown(question);

        verify(reviewStateRepository).updateFsrs(any(FsrsState.class), eq(1_800_086_400L), eq(ReviewResult.UNKNOWN), eq(0), eq(1));
        verify(userTopicStatsRepository).recordAnswer("topic", false);
    }

    @Test
    void applyUnknownUpdatesReviewStateAndRecordsIncorrectTopicStat() {
        Question question = sampleQuestion(15L);
        ReviewState updated = new ReviewState(15L, 0, 1, 2.1, 1_900_000L, ReviewResult.UNKNOWN, 0, 1);

        when(reviewStateRepository.findByQuestionId(15L)).thenReturn(Optional.empty());
        when(spacedRepetitionService.applyUnknown(any(ReviewState.class))).thenReturn(updated);

        ReviewState result = reviewService.applyUnknown(question);

        assertThat(result).isEqualTo(updated);
        verify(reviewStateRepository).update(updated);
        verify(userTopicStatsRepository).recordAnswer("topic", false);
    }

    @Test
    void updateConfidenceThrowsWhenNoPreviousAnswer() {
        when(questionRepository.findById(10L)).thenReturn(Optional.of(sampleQuestion(10L)));
        when(reviewStateRepository.findByQuestionId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateConfidence(10L, 4))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("до первого ответа");
        verify(reviewStateRepository, never()).update(any());
    }

    @Test
    void updateConfidenceAdjustsEaseForBoundedGrade() {
        ReviewState current = new ReviewState(11L, 1, 1, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 1, 0);
        when(questionRepository.findById(11L)).thenReturn(Optional.of(sampleQuestion(11L)));
        when(reviewStateRepository.findByQuestionId(11L)).thenReturn(Optional.of(current));

        reviewService.updateConfidence(11L, 3);

        ArgumentCaptor<ReviewState> stateCaptor = ArgumentCaptor.forClass(ReviewState.class);
        verify(reviewStateRepository).update(stateCaptor.capture());
        double expectedEase = Math.max(
                1.3,
                current.easeFactor() + SpacedRepetitionService.computeEaseDelta(3) - SpacedRepetitionService.computeEaseDelta(5)
        );
        assertThat(stateCaptor.getValue().easeFactor()).isEqualTo(expectedEase);
    }

    @Test
    void submitFlashcardGradePublishesAnswerEventWhenQuestionExists() {
        ReviewState updated = new ReviewState(12L, 2, 6, 2.3, 1_800_000_000L, ReviewResult.CORRECT, 2, 0);
        when(reviewStateRepository.findByQuestionId(12L)).thenReturn(Optional.empty());
        when(spacedRepetitionService.applyAnswer(any(ReviewState.class), eq(true), eq(5))).thenReturn(updated);
        when(questionRepository.findById(12L)).thenReturn(Optional.of(sampleQuestion(12L)));

        reviewService.submitFlashcardGrade(12L, true, 5);

        verify(reviewStateRepository).update(updated);
        verify(eventPublisher).publishEvent(any(AnswerEvent.class));
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
