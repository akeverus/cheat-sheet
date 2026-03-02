package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionValidationServiceTest {

    private final QuestionValidationService validationService = new QuestionValidationService();

    @Test
    void validatesQuestionWithSingleCorrectOption() {
        Question question = new Question(
                1L,
                "slug-1",
                "slug-1",
                "topic.md",
                "java",
                "Что верно про hashCode?",
                "Ответ по контракту equals/hashCode",
                false,
                "hash",
                QuestionType.CODE_ANALYSIS,
                null,
                null,
                0,
                null,
                Difficulty.MEDIUM,
                "Краткое объяснение вопроса должно быть достаточно длинным.",
                "Подробное объяснение вопроса должно быть достаточно длинным и содержать механизм работы,"
                        + " ограничения контракта и последствия нарушения для корректности структур данных.",
                "Частая ошибка — игнорирование контракта equals/hashCode.",
                List.of("java", "collections"),
                List.of(
                        new QuestionOption("A", "hashCode и equals должны быть согласованы", true, "Это верно, иначе hash-коллекции будут находить объект в неправильной корзине."),
                        new QuestionOption("B", "hashCode никогда не влияет на HashMap", false, "Неверно, hashCode используется при выборе bucket перед сравнением по equals."),
                        new QuestionOption("C", "equals можно не переопределять при переопределении hashCode", false, "Неверно, это ломает контракт и приводит к непредсказуемому поведению коллекций."),
                        new QuestionOption("D", "hashCode должен быть уникальным для каждого объекта", false, "Неверно, коллизии допустимы, но корректность обеспечивается связкой hashCode + equals.")
                )
        );

        assertThat(validationService.validate(question)).isEmpty();
        assertThat(validationService.isValid(question)).isTrue();
    }

    @Test
    void returnsViolationsWhenQuestionInvalid() {
        Question question = new Question(
                2L,
                "slug-2",
                "slug-2",
                "topic.md",
                "",
                "",
                "answer",
                false,
                "hash",
                QuestionType.DEBUGGING,
                null,
                null,
                0,
                null,
                Difficulty.EASY,
                "short",
                "short",
                "",
                List.of(),
                List.of(
                        new QuestionOption("a", "dup", true, "too short"),
                        new QuestionOption("b", "dup", true, "too short")
                )
        );

        List<String> violations = validationService.validate(question);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.contains("Topic is required"));
        assertThat(violations).anyMatch(v -> v.contains("Exactly one correct option is required"));
        assertThat(violations).anyMatch(v -> v.contains("Duplicate option text"));
    }

}
