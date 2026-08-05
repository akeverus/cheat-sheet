package com.cheatsheet.quiz.api.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import com.cheatsheet.quiz.api.dto.request.interview.ReportIssueRequest;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ValidIssueCategoryTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsKnownCategoryCaseInsensitive() {
        ReportIssueRequest request = ReportIssueRequest.builder()
                .questionId(1L)
                .category("outdated")
                .build();

        Set<ConstraintViolation<ReportIssueRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void rejectsUnknownCategory() {
        ReportIssueRequest request = ReportIssueRequest.builder()
                .questionId(1L)
                .category("gibberish")
                .build();

        Set<ConstraintViolation<ReportIssueRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> "category".equals(v.getPropertyPath().toString()));
    }

    @Test
    void allowsBlankCategoryForOtherFallback() {
        ReportIssueRequest request = ReportIssueRequest.builder()
                .questionId(1L)
                .category("  ")
                .build();

        Set<ConstraintViolation<ReportIssueRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    /**
     * Перечень в тексте ошибки раньше был выписан руками — то есть был ещё одной
     * копией {@link QuestionIssueCategory}, которую компилятор не сверяет: добавленная
     * категория молча выпадала бы из сообщения, и клиент узнавал бы о ней последним.
     */
    @Test
    void messageListsEveryCategoryStraightFromTheEnum() {
        ReportIssueRequest request = ReportIssueRequest.builder()
                .questionId(1L)
                .category("gibberish")
                .build();

        String message = validator.validate(request).iterator().next().getMessage();

        assertThat(message).startsWith("category must be one of: ");
        for (QuestionIssueCategory category : QuestionIssueCategory.values()) {
            assertThat(message)
                    .describedAs("категория " + category + " должна быть названа в тексте ошибки")
                    .contains(category.name());
        }
    }
}
