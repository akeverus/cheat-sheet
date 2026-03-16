package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.admin.service.RegenerateService;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegenerateServiceTest {

    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private HintRepository hintRepository;
    @Mock
    private OptionCache optionCache;
    @Mock
    private QuestionRepository questionRepository;

    private RegenerateService service;

    @BeforeEach
    void setUp() {
        service = new RegenerateService(
                answerOptionRepository,
                hintRepository,
                optionCache,
                questionRepository
        );
    }

    @Test
    void regenerateQuestionDeletesArtifactsAndInvalidatesCacheWhenQuestionExists() {
        long questionId = 42L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion(questionId)));

        service.regenerateQuestion(questionId);

        verify(answerOptionRepository).deleteByQuestionId(questionId);
        verify(hintRepository).deleteByQuestionId(questionId);
        verify(optionCache).invalidate(questionId);
        verify(questionRepository).incrementRegenCount(questionId);
        verify(questionRepository).updateDiagram(questionId, null);
    }

    @Test
    void regenerateQuestionThrowsWhenQuestionMissing() {
        long questionId = 77L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.regenerateQuestion(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);

        verify(answerOptionRepository, never()).deleteByQuestionId(questionId);
        verify(hintRepository, never()).deleteByQuestionId(questionId);
        verify(optionCache, never()).invalidate(questionId);
        verify(questionRepository, never()).incrementRegenCount(questionId);
        verify(questionRepository, never()).updateDiagram(questionId, null);
    }

    private Question existingQuestion(long id) {
        return new Question(
                id,
                "java#q" + id,
                "java#q" + id,
                "java.md",
                "java",
                "Почему HashMap может деградировать при плохом hashCode?",
                "Из-за роста коллизий в бакетах.",
                false,
                "hash",
                QuestionType.CONCEPT,
                null,
                null,
                0,
                null
        );
    }
}
