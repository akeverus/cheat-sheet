package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGenerationPolicyTest {

    @Test
    void enrichViolationsAddsCognitiveLoadViolationsForVerboseCandidate() {
        QuestionGenerationPolicy policy = new QuestionGenerationPolicy(70, 40, 60);
        Question verbose = question(
                "Это очень длинный и перегруженный вопрос, который явно превышает лимит по длине для проверки когнитивной нагрузки.",
                List.of(
                        "Первый длинный вариант ответа с избыточным количеством текста.",
                        "Второй длинный вариант ответа с избыточным количеством текста.",
                        "Третий длинный вариант ответа с избыточным количеством текста.",
                        "Четвертый длинный вариант ответа с избыточным количеством текста."
                )
        );

        List<String> violations = policy.enrichViolations(verbose, List.of(), new HashSet<>());

        assertThat(violations).anyMatch(v -> v.contains("question text is too long"));
        assertThat(violations).anyMatch(v -> v.contains("options are too verbose"));
    }

    @Test
    void enrichViolationsDetectsDuplicateCandidateFingerprint() {
        QuestionGenerationPolicy policy = new QuestionGenerationPolicy(70);
        Set<String> seen = new HashSet<>();
        Question candidate = question(
                "Почему HashMap может деградировать при плохом hashCode?",
                List.of("Из-за коллизий", "Из-за GC", "Из-за JIT", "Из-за кеша CPU")
        );

        List<String> first = policy.enrichViolations(candidate, List.of(), seen);
        List<String> second = policy.enrichViolations(candidate, List.of(), seen);

        assertThat(first).isEmpty();
        assertThat(second).anyMatch(v -> v.contains("duplicates previous generation attempt"));
    }

    @Test
    void enrichViolationsDetectsNearDuplicateQuestionText() {
        QuestionGenerationPolicy policy = new QuestionGenerationPolicy(70, 280, 520, 0.60);
        Set<String> seen = new HashSet<>();
        seen.add("почему hashset теряет производительность при большом числе коллизий::a|b|c|d");

        Question candidate = question(
                "Почему HashSet может терять производительность при большом числе коллизий ключей?",
                List.of("Из-за коллизий", "Из-за GC", "Из-за JIT", "Из-за кеша CPU")
        );

        List<String> violations = policy.enrichViolations(candidate, List.of(), seen);

        assertThat(violations).anyMatch(v -> v.contains("semantically too close"));
    }

    @Test
    void enrichViolationsDetectsLowDistractorDiversity() {
        QuestionGenerationPolicy policy = new QuestionGenerationPolicy(70, 280, 520, 0.82, 0.55);
        Question candidate = question(
                "Почему в HashMap важен качественный hashCode?",
                List.of(
                        "Из-за неравномерного распределения ключей по бакетам",
                        "Из-за коллизий при распределении ключей по бакетам",
                        "Из-за коллизий при разбиении ключей по бакетам",
                        "Из-за коллизий при хеш-распределении ключей по бакетам"
                )
        );

        List<String> violations = policy.enrichViolations(candidate, List.of(), new HashSet<>());

        assertThat(violations).anyMatch(v -> v.contains("distractors are semantically too similar"));
    }

    @Test
    void enrichViolationsKeepsDiverseDistractorsWithoutViolation() {
        QuestionGenerationPolicy policy = new QuestionGenerationPolicy(70, 280, 520, 0.82, 0.75);
        Question candidate = question(
                "Почему в HashMap важен качественный hashCode?",
                List.of(
                        "Из-за коллизий в бакетах",
                        "Потому что таблица всегда сортирует ключи по compareTo",
                        "Потому что hashCode используется только при удалении",
                        "Потому что equals выбирает bucket без hashCode"
                )
        );

        List<String> violations = policy.enrichViolations(candidate, List.of(), new HashSet<>());

        assertThat(violations).noneMatch(v -> v.contains("distractors are semantically too similar"));
    }

    private Question question(String questionText, List<String> options) {
        return new Question(
                0L,
                "generated:test",
                "generated",
                "generated",
                "java",
                questionText,
                "answer",
                false,
                "generated",
                QuestionType.CONCEPT,
                null,
                null,
                0,
                null,
                Difficulty.MEDIUM,
                "Краткое объяснение достаточной длины для валидации вопроса.",
                "Подробное объяснение достаточной длины для валидации вопроса и проверки политики когнитивной нагрузки.",
                "Частая ошибка в теме.",
                List.of("java"),
                List.of(
                        new QuestionOption("A", options.get(0), true, "Пояснение первого варианта достаточной длины."),
                        new QuestionOption("B", options.get(1), false, "Пояснение второго варианта достаточной длины."),
                        new QuestionOption("C", options.get(2), false, "Пояснение третьего варианта достаточной длины."),
                        new QuestionOption("D", options.get(3), false, "Пояснение четвертого варианта достаточной длины.")
                )
        );
    }
}
