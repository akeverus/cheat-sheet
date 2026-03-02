package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.imports.MarkdownFavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private MarkdownFavoriteService markdownFavoriteService;

    private FavoriteService service;

    @BeforeEach
    void setUp() {
        service = new FavoriteService(questionRepository, markdownFavoriteService);
    }

    @Test
    void toggleFavoriteMarksQuestionAsImportantWhenCurrentlyNotFavorite() {
        Question question = question(301L, false);
        when(questionRepository.findById(301L)).thenReturn(Optional.of(question));
        when(markdownFavoriteService.toggleInFile(question, true)).thenReturn(true);

        FavoriteService.FavoriteResult result = service.toggleFavorite(301L);

        assertThat(result.questionId()).isEqualTo(301L);
        assertThat(result.favorite()).isTrue();
        assertThat(result.synced()).isTrue();
        verify(questionRepository).updateImportant(301L, true);
        verify(markdownFavoriteService).toggleInFile(question, true);
    }

    @Test
    void toggleFavoriteUnmarksQuestionWhenCurrentlyFavorite() {
        Question question = question(302L, true);
        when(questionRepository.findById(302L)).thenReturn(Optional.of(question));
        when(markdownFavoriteService.toggleInFile(question, false)).thenReturn(false);

        FavoriteService.FavoriteResult result = service.toggleFavorite(302L);

        assertThat(result.favorite()).isFalse();
        assertThat(result.synced()).isFalse();
        verify(questionRepository).updateImportant(302L, false);
        verify(markdownFavoriteService).toggleInFile(question, false);
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
