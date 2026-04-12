package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatedQuestionsServiceTest {

    @Mock private QuestionRepository questionRepository;
    private RelatedQuestionsService service;

    @BeforeEach
    void setUp() {
        service = new RelatedQuestionsService(questionRepository);
    }

    @Test
    void returnsRelatedQuestionsFromRepository() {
        List<RelatedQuestion> expected = List.of(
                new RelatedQuestion(2L, "Что такое volatile?", "java", 0.0),
                new RelatedQuestion(3L, "Чем отличается synchronized от Lock?", "java", 75.0)
        );
        when(questionRepository.findRelatedByTopic(1L, "java", 3)).thenReturn(expected);

        List<RelatedQuestion> result = service.findRelated(1L, "java");

        assertThat(result).hasSize(2);
        verify(questionRepository).findRelatedByTopic(1L, "java", 3);
    }

    @Test
    void returnsEmptyForNullTopic() {
        List<RelatedQuestion> result = service.findRelated(1L, null);

        assertThat(result).isEmpty();
        verifyNoInteractions(questionRepository);
    }

    @Test
    void returnsEmptyForBlankTopic() {
        List<RelatedQuestion> result = service.findRelated(1L, "   ");

        assertThat(result).isEmpty();
        verifyNoInteractions(questionRepository);
    }

    @Test
    void returnsEmptyOnRepositoryException() {
        when(questionRepository.findRelatedByTopic(1L, "java", 3))
                .thenThrow(new RuntimeException("DB error"));

        List<RelatedQuestion> result = service.findRelated(1L, "java");

        assertThat(result).isEmpty();
    }

    @Test
    void clampsLimitToMinimumOne() {
        when(questionRepository.findRelatedByTopic(1L, "java", 1)).thenReturn(List.of());

        service.findRelated(1L, "java", 0);

        verify(questionRepository).findRelatedByTopic(1L, "java", 1);
    }

    @Test
    void respectsCustomLimit() {
        when(questionRepository.findRelatedByTopic(1L, "java", 5)).thenReturn(List.of());

        service.findRelated(1L, "java", 5);

        verify(questionRepository).findRelatedByTopic(1L, "java", 5);
    }
}
