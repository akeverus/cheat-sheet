package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class McqJsonLoaderIntegrationTest {

    private static final String TOPIC = "programming/java-strings";

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

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void loadJavaStringsSeedInsertsAllOptions() {
        // Java-strings фикстура — 3 вопроса, по 4 опции каждый = 12 строк.
        // Чистим опции тем вопросов, т.к. стартовый seed-загрузчик уже мог
        // их вставить (тогда идемпотентная загрузка вернула бы 0 inserted).
        clearTopicOptions();

        McqLoadResult result = loader.loadForTopic("programming", "java-strings");

        assertThat(result.found()).isTrue();
        assertThat(result.questionsSkipped()).isZero();
        assertThat(result.optionsInserted()).isEqualTo(12);

        Optional<Long> q1 = questionRepository.findIdByTopicAndQuestionNumber(TOPIC, 1);
        assertThat(q1).isPresent();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q1.get());
        assertThat(options).hasSize(4);
        assertThat(options.stream().filter(AnswerOption::correct).count()).isEqualTo(1);
        assertThat(options.get(0).optionText()).startsWith("A. ");
    }

    @Test
    void reloadingIdenticalSeedIsIdempotentAndPreservesOptionIds() {
        // Регрессия: раньше upsertOptions делал delete+insert на каждом старте,
        // и id вариантов менялись (открытая страница после рестарта ловила
        // "Вариант не найден"). Теперь повторная загрузка тех же опций —
        // no-op: 0 inserted, id сохраняются.
        clearTopicOptions();

        McqLoadResult first = loader.loadForTopic("programming", "java-strings");
        assertThat(first.optionsInserted()).isEqualTo(12);

        long qId = questionRepository.findIdByTopicAndQuestionNumber(TOPIC, 1).orElseThrow();
        List<Long> idsBefore = answerOptionRepository.findByQuestionId(qId).stream()
                .map(AnswerOption::id)
                .toList();

        // Повторная загрузка без изменений в seed → ничего не вставляется.
        McqLoadResult second = loader.loadForTopic("programming", "java-strings");
        assertThat(second.found()).isTrue();
        assertThat(second.optionsInserted()).isZero();

        List<Long> idsAfter = answerOptionRepository.findByQuestionId(qId).stream()
                .map(AnswerOption::id)
                .toList();
        assertThat(idsAfter).isEqualTo(idsBefore);
    }

    @Test
    void changedSeedContentTriggersReinsert() {
        // Адверсариальная проверка обратного направления: если содержимое опции
        // в БД отличается от seed, upsertOptions ОБЯЗАН переинсёртить (иначе при
        // обновлении seed пользователь видел бы устаревшие варианты навсегда).
        // Гард против регрессии, где optionsUnchanged перестанет ловить изменение.
        clearTopicOptions();
        loader.loadForTopic("programming", "java-strings");

        long qId = questionRepository.findIdByTopicAndQuestionNumber(TOPIC, 1).orElseThrow();
        List<Long> q1IdsBefore = answerOptionRepository.findByQuestionId(qId).stream()
                .map(AnswerOption::id).toList();
        // Портим текст одной опции q1 в БД — теперь содержимое q1 != seed.
        jdbcTemplate.update(
                "UPDATE answer_options SET option_text = ? WHERE question_id = ? AND display_order = 0",
                "СТАРЫЙ УСТАРЕВШИЙ ТЕКСТ", qId);

        McqLoadResult reload = loader.loadForTopic("programming", "java-strings");

        // Контракт: изменённый вопрос ПЕРЕИНСЁРЧЕН. Проверяем q1 точечно (его id
        // вариантов сменились = delete+insert), а не точный общий счётчик —
        // @SpringBootTest-методы делят одну БД без TRUNCATE, и общий inserted
        // зависит от порядка тестов. Само число — «переинсёрчено хотя бы q1».
        assertThat(reload.optionsInserted()).isGreaterThanOrEqualTo(4);
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(qId);
        assertThat(options).hasSize(4);
        List<Long> q1IdsAfter = options.stream().map(AnswerOption::id).toList();
        assertThat(q1IdsAfter).doesNotContainAnyElementsOf(q1IdsBefore); // q1 реально переинсёрчен
        assertThat(options.stream().map(AnswerOption::optionText))
                .noneMatch(t -> t.contains("СТАРЫЙ УСТАРЕВШИЙ ТЕКСТ"));
        assertThat(options.get(0).optionText()).startsWith("A. ");
    }

    @Test
    void unknownTopicReturnsNotFound() {
        // Запрос несуществующего топика → found=false, без вставок и без падения.
        McqLoadResult result = loader.loadForTopic("programming", "no-such-topic");
        assertThat(result.found()).isFalse();
        assertThat(result.optionsInserted()).isZero();
    }

    private void clearTopicOptions() {
        for (int qNumber = 1; qNumber <= 3; qNumber++) {
            questionRepository.findIdByTopicAndQuestionNumber(TOPIC, qNumber)
                    .ifPresent(answerOptionRepository::deleteByQuestionId);
        }
    }
}
