package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class McqJsonLoaderTest {

    private QuestionRepository questionRepository;
    private AnswerOptionRepository answerOptionRepository;
    private McqJsonLoader loader;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        answerOptionRepository = mock(AnswerOptionRepository.class);
        loader = new McqJsonLoader(new ObjectMapper(), questionRepository, answerOptionRepository);
    }

    @Test
    void loadsValidJsonAndInsertsOptions() {
        when(questionRepository.findIdByTopicAndQuestionNumber("test/sample-interview", 1))
                .thenReturn(Optional.of(42L));

        McqLoadResult result = loader.loadForTopic("test", "sample-interview");

        assertThat(result.found()).isTrue();
        assertThat(result.optionsInserted()).isEqualTo(4);
        assertThat(result.questionsSkipped()).isEqualTo(0);

        ArgumentCaptor<List<AnswerOptionCreate>> captor = ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).deleteByQuestionId(42L);
        verify(answerOptionRepository).insertAll(eq(42L), captor.capture());
        List<AnswerOptionCreate> creates = captor.getValue();
        assertThat(creates).hasSize(4);
        assertThat(creates.get(0).correct()).isTrue();
        // Option text формируется как "<LABEL>. <text>" — проверяем только префикс,
        // чтобы тест не зависел от смысла seed-контента.
        assertThat(creates.get(0).optionText()).startsWith("A. ");
        assertThat(creates.get(0).explanation())
                .contains("**Развёрнутое объяснение.**")
                .contains("**Пример.**")
                .contains("**Связанные вопросы.**");
        assertThat(creates.get(1).correct()).isFalse();
        assertThat(creates.get(1).optionText()).startsWith("B. ");
        assertThat(creates.get(1).explanation()).contains("**Что на самом деле.**");
    }

    @Test
    void noJsonFileForTopicReturnsNotFound() {
        McqLoadResult result = loader.loadForTopic("test", "nonexistent");
        assertThat(result.found()).isFalse();
        verify(answerOptionRepository, times(0)).insertAll(anyLong(), any());
    }

    @Test
    void missingQuestionInDbIncrementsSkipped() {
        when(questionRepository.findIdByTopicAndQuestionNumber("test/sample-interview", 1))
                .thenReturn(Optional.empty());

        McqLoadResult result = loader.loadForTopic("test", "sample-interview");

        assertThat(result.found()).isTrue();
        assertThat(result.questionsSkipped()).isEqualTo(1);
        assertThat(result.optionsInserted()).isEqualTo(0);
        verify(answerOptionRepository, times(0)).insertAll(anyLong(), any());
    }

    @Test
    void topicSlugMismatchThrows() {
        assertThatThrownBy(() -> loader.loadForTopic("test", "wrong-slug"))
                .isInstanceOf(InvalidMcqSeedException.class)
                .hasMessageContaining("topic_slug mismatch");
    }
}
