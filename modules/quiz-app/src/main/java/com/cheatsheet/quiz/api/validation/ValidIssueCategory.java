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
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * BE-005: неизвестная категория жалобы → 400, а не silent OTHER.
 * {@code null}/blank допустимы (сервис подставит {@link QuestionIssueCategory#OTHER}).
 *
 * <p>Перечень допустимых значений в тексте ошибки не выписан руками: такой список —
 * ещё одна копия перечисления, которую компилятор не сверяет, и после добавления
 * категории сообщение начинает врать. Он собирается из
 * {@link QuestionIssueCategory#values()} в {@link Validator}.</p>
 */
@Documented
@Constraint(validatedBy = ValidIssueCategory.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIssueCategory {

    String message() default "unknown issue category";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidIssueCategory, String> {

        /**
         * Имена констант — UPPER_SNAKE без {@code {}} и {@code $}, поэтому шаблон
         * сообщения безопасен для EL-интерполяции.
         */
        private static final String ALLOWED = Arrays.stream(QuestionIssueCategory.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isBlank()) {
                return true;
            }
            try {
                QuestionIssueCategory.valueOf(value.strip().toUpperCase());
                return true;
            } catch (IllegalArgumentException ex) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("category must be one of: " + ALLOWED)
                        .addConstraintViolation();
                return false;
            }
        }
    }
}
