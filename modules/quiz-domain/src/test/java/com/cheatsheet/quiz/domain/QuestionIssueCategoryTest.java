package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт {@link QuestionIssueCategory}: подписи и снисходительный разбор.
 *
 * <p>Соседний {@link ReviewResultTest} проверяет обратное поведение —
 * {@code ReviewResult.fromString} на мусоре бросает. Здесь мусор молча уезжает
 * в {@link QuestionIssueCategory#OTHER}, и это осознанный выбор: жалоба
 * пользователя не должна теряться из-за расхождения фронта и бэкенда.</p>
 */
class QuestionIssueCategoryTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "gibberish", "INCORRECT", "typo "})
    void fromStringNeverFails(String input) {
        assertThat(QuestionIssueCategory.fromString(input)).isNotNull();
    }

    @Test
    void fromStringParsesKnownNamesCaseInsensitively() {
        assertThat(QuestionIssueCategory.fromString("TYPO")).isEqualTo(QuestionIssueCategory.TYPO);
        assertThat(QuestionIssueCategory.fromString("outdated")).isEqualTo(QuestionIssueCategory.OUTDATED);
        assertThat(QuestionIssueCategory.fromString("  Unclear  ")).isEqualTo(QuestionIssueCategory.UNCLEAR);
    }

    /**
     * Ровно та снисходительность, которая делает опасной вторую копию списка
     * категорий: расхождение имён не уронит запрос и не оставит следа в логе —
     * пользователь выберет «Опечатка», а на проверку уедет «Другое».
     */
    @Test
    void unknownNameSilentlyBecomesOther() {
        assertThat(QuestionIssueCategory.fromString("TYPOO")).isEqualTo(QuestionIssueCategory.OTHER);
        assertThat(QuestionIssueCategory.fromString(null)).isEqualTo(QuestionIssueCategory.OTHER);
    }

    @Test
    void everyCategoryHasItsOwnRussianLabel() {
        for (QuestionIssueCategory category : QuestionIssueCategory.values()) {
            assertThat(category.label())
                    .describedAs("подпись категории " + category)
                    .isNotBlank()
                    .doesNotContain(category.name());
        }
        assertThat(Arrays.stream(QuestionIssueCategory.values()).map(QuestionIssueCategory::label).toList())
                .describedAs("две одинаковые подписи — два неразличимых пункта в форме жалобы")
                .doesNotHaveDuplicates();
    }
}
