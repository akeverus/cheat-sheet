package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Проверяет, что служебный question_text не попадает в сохранённые данные тренажёра. */
class McqVisibleLinkageContractTest {

    @Test
    void questionTextIsNeverPersistedSoItCannotBeTheVisibleStem() throws IOException {
        QuestionRepository questionRepository = mock(QuestionRepository.class);
        AnswerOptionRepository answerOptionRepository = mock(AnswerOptionRepository.class);
        McqJsonLoader loader = new McqJsonLoader(
                new ObjectMapper(), questionRepository, answerOptionRepository, new SimpleMeterRegistry());
        when(questionRepository.findIdByTopicAndQuestionNumber("test/sample-interview", 1))
                .thenReturn(Optional.of(42L));

        String questionText;
        try (var in = getClass().getResourceAsStream("/seed/mcq/test/sample-interview.json")) {
            JsonNode tree = new ObjectMapper().readTree(in);
            questionText = tree.path("questions").get(0).path("blocks").get(0).path("question_text").asText();
        }
        assertThat(questionText).as("фикстура обязана содержать непустой question_text").isNotBlank();

        loader.loadForTopic("test", "sample-interview");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerOptionCreate>> captor = ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).insertAll(eq(42L), captor.capture());

        assertThat(captor.getValue())
                .as("question_text не должен попадать в БД: пользователь его не видит, "
                        + "поэтому он не является stem'ом и не может оправдывать вариант")
                .allSatisfy(create -> {
                    assertThat(create.optionText()).doesNotContain(questionText);
                    assertThat(create.explanation()).doesNotContain(questionText);
                });
    }
}
