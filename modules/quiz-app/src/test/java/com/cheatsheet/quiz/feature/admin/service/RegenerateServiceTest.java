package com.cheatsheet.quiz.feature.admin.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegenerateServiceTest {

    private AnswerOptionRepository answerOptions;
    private HintRepository hints;
    private OptionCache optionCache;
    private QuestionRepository questions;
    private RegenerateService service;

    @BeforeEach
    void setUp() {
        answerOptions = mock(AnswerOptionRepository.class);
        hints = mock(HintRepository.class);
        optionCache = mock(OptionCache.class);
        questions = mock(QuestionRepository.class);
        service = new RegenerateService(answerOptions, hints, optionCache, questions);
    }

    @Test
    void regenerateClearsArtifactsAndIncrementsCount() {
        long id = 42L;
        when(questions.findById(id)).thenReturn(Optional.of(stubQuestion(id)));

        service.regenerateQuestion(id);

        verify(answerOptions).deleteByQuestionId(id);
        verify(hints).deleteByQuestionId(id);
        verify(optionCache).invalidate(id);
        verify(questions).incrementRegenCount(id);
        verify(questions).updateDiagram(id, null);
    }

    @Test
    void regenerateThrowsWhenQuestionMissing() {
        long id = 99L;
        when(questions.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.regenerateQuestion(id))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("99");

        verify(answerOptions, never()).deleteByQuestionId(id);
        verify(hints, never()).deleteByQuestionId(id);
        verify(optionCache, never()).invalidate(id);
    }

    @Test
    void regeneratePropagatesRepositoryError() {
        long id = 7L;
        when(questions.findById(id)).thenReturn(Optional.of(stubQuestion(id)));
        doThrow(new RuntimeException("DB down")).when(answerOptions).deleteByQuestionId(id);

        assertThatThrownBy(() -> service.regenerateQuestion(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB down");
    }

    private Question stubQuestion(long id) {
        return new Question(
                id, "slug-" + id, "source-" + id, "path/to/file.md", "topic",
                "Question text?", "Answer markdown", false, "hash",
                QuestionType.TEXT, null, null, 0, null);
    }
}
