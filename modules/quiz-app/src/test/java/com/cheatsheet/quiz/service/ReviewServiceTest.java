package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
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
import static org.mockito.ArgumentMatchers.eq;
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
    private UserTopicStatsRepository userTopicStatsRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-02T00:00:00Z"), ZoneOffset.UTC);
        reviewService = new ReviewService(
                reviewStateRepository,
                spacedRepetitionService,
                userTopicStatsRepository,
                questionRepository,
                eventPublisher,
                fixedClock
        );
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
