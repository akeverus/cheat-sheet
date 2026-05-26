package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Полный путь сидера: McqJsonLoader читает {@code seed/mcq/programming/java-strings.json}
 * (test fixture), сопоставляет с вопросами из {@code test-interview/programming/java-strings.md},
 * вставляет 12 опций (3 вопроса × 4 опции) в реальную PostgreSQL через Testcontainers.
 *
 * <p>Unit-тест {@code McqJsonLoaderTest} мокает репозитории; этот тест проверяет
 * что вся цепочка (classpath load → schema validate → SQL insert → выборка)
 * работает end-to-end.
 */
@SpringBootTest
@ActiveProfiles("test")
class McqJsonLoaderIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    McqJsonLoader loader;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerOptionRepository answerOptionRepository;

    @Test
    void loadJavaStringsSeedInsertsAllOptions() {
        // Java-strings фикстура — 3 вопроса, по 4 опции каждый = 12 строк.
        McqLoadResult result = loader.loadForTopic("programming", "java-strings");

        assertThat(result.found()).isTrue();
        assertThat(result.questionsSkipped()).isZero();
        assertThat(result.optionsInserted()).isEqualTo(12);

        // Q1 = «Почему String в Java immutable?»
        Optional<Long> q1 = questionRepository.findIdByTopicAndQuestionNumber(
                "programming/java-strings", 1);
        assertThat(q1).isPresent();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q1.get());
        assertThat(options).hasSize(4);
        assertThat(options.stream().filter(AnswerOption::correct).count()).isEqualTo(1L);
        // Контракт imports: option_text = "<label>. <text>"
        assertThat(options.get(0).optionText()).startsWith("A. ");
    }

    @Test
    void loadNonExistentTopicReturnsNotFoundAndInsertsNothing() {
        McqLoadResult result = loader.loadForTopic("programming", "no-such-topic");

        assertThat(result.found()).isFalse();
        assertThat(result.optionsInserted()).isZero();
    }
}
