package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    private FavoriteService service;

    @BeforeEach
    void setUp() {
        service = new FavoriteService(questionRepository);
    }

    @Test
    void toggleFavoriteMarksQuestionAsImportantWhenCurrentlyNotFavorite() {
        long questionId = 301L;
        Question question = question(questionId, false);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        FavoriteService.FavoriteResult result = service.toggleFavorite(questionId);

        assertThat(result.questionId()).isEqualTo(questionId);
        assertThat(result.favorite()).isTrue();
        verify(questionRepository).updateImportant(eq(questionId), eq(true));
    }

    @Test
    void toggleFavoriteUnmarksQuestionWhenCurrentlyFavorite() {
        long questionId = 302L;
        Question question = question(questionId, true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        FavoriteService.FavoriteResult result = service.toggleFavorite(questionId);

        assertThat(result.questionId()).isEqualTo(questionId);
        assertThat(result.favorite()).isFalse();
        verify(questionRepository).updateImportant(eq(questionId), eq(false));
    }

    @Test
    void toggleFavoriteThrowsWhenQuestionMissing() {
        when(questionRepository.findById(303L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleFavorite(303L))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("id=303");
        verify(questionRepository, never()).updateImportant(303L, true);
    }

    private Question question(long id, boolean important) {
        return new Question(
                id,
                "topic.md#Q" + id,
                "topic.md#Q" + id,
                "topic.md",
                "topic",
                "Question " + id + "?",
                "Answer " + id,
                important,
                "hash-" + id,
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
