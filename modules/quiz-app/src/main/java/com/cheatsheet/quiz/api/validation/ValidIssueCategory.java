package com.cheatsheet.quiz.api.validation;

import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BE-005: неизвестная категория жалобы → 400, а не silent OTHER.
 * {@code null}/blank допустимы (сервис подставит {@link QuestionIssueCategory#OTHER}).
 */
@Documented
@Constraint(validatedBy = ValidIssueCategory.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIssueCategory {

    String message() default "category must be one of: INCORRECT_ANSWER, TYPO, UNCLEAR, OUTDATED, OTHER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidIssueCategory, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isBlank()) {
                return true;
            }
            try {
                QuestionIssueCategory.valueOf(value.strip().toUpperCase());
                return true;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
    }
}
