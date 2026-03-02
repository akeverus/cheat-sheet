package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class QuestionValidationService {
    private static final int SHORT_EXPLANATION_MIN_LENGTH = 30;
    private static final int DETAILED_EXPLANATION_MIN_LENGTH = 80;
    private static final Set<String> TRIVIAL_PATTERNS = Set.of(
            "what is",
            "что такое",
            "define",
            "определите",
            "выберите правильный ответ",
            "верный ответ"
    );
    private static final Set<String> REQUIRED_OPTION_IDS = Set.of("A", "B", "C", "D");

    /**
     * Валидирует сгенерированный вопрос по структурным и качественным правилам Question v2.
     *
     * @param question вопрос для проверки
     * @return список нарушений; пустой список означает валидный вопрос
     */
    public List<String> validate(Question question) {
        List<String> violations = new ArrayList<>();
        if (question == null) {
            violations.add("Question is null");
            return violations;
        }
        if (isBlank(question.topic())) {
            violations.add("Topic is required");
        }
        if (isBlank(question.questionText())) {
            violations.add("Question text is required");
        }
        if (question.difficulty() == null) {
            violations.add("Difficulty is required");
        }
        if (question.type() == null) {
            violations.add("Question type is required");
        }
        if (isBlank(question.shortExplanation()) || question.shortExplanation().trim().length() < SHORT_EXPLANATION_MIN_LENGTH) {
            violations.add("shortExplanation must be at least 30 characters");
        }
        if (isBlank(question.detailedExplanation()) || question.detailedExplanation().trim().length() < DETAILED_EXPLANATION_MIN_LENGTH) {
            violations.add("detailedExplanation must be at least 80 characters");
        }
        if (isBlank(question.commonMistake())) {
            violations.add("commonMistake is required");
        }
        if (containsTrivialPattern(question.questionText())) {
            violations.add("Question text is trivial and does not require technical reasoning");
        }
        if (question.options() == null || question.options().isEmpty()) {
            violations.add("Options are required");
            return violations;
        }
        if (question.options().size() != 4) {
            violations.add("Exactly 4 options are required");
        }

        int correctCount = 0;
        Set<String> normalizedTexts = new HashSet<>();
        Set<String> optionIds = new HashSet<>();
        for (QuestionOption option : question.options()) {
            if (option == null) {
                violations.add("Option item is null");
                continue;
            }
            if (isBlank(option.id()) || !REQUIRED_OPTION_IDS.contains(option.id().trim().toUpperCase(Locale.ROOT))) {
                violations.add("Option id must be one of A, B, C, D");
            } else {
                optionIds.add(option.id().trim().toUpperCase(Locale.ROOT));
            }
            if (option.correct()) {
                correctCount++;
            }
            if (isBlank(option.text())) {
                violations.add("Option text is required");
            } else {
                String normalized = option.text().trim().toLowerCase(Locale.ROOT);
                if (!normalizedTexts.add(normalized)) {
                    violations.add("Duplicate option text found");
                }
            }
            if (isBlank(option.explanation()) || option.explanation().trim().length() < SHORT_EXPLANATION_MIN_LENGTH) {
                violations.add("Each option explanation must be at least 30 characters");
            }
        }
        if (correctCount != 1) {
            violations.add("Exactly one correct option is required");
        }
        if (!optionIds.equals(REQUIRED_OPTION_IDS)) {
            violations.add("Option ids must contain exactly A, B, C, D");
        }
        if (!isDifficultyValidForType(question.difficulty(), question.type())) {
            violations.add("Difficulty does not match question type");
        }
        return violations;
    }

    /**
     * Быстрый булевый предикат валидности Question v2.
     *
     * @param question вопрос для проверки
     * @return {@code true}, если нарушений нет
     */
    public boolean isValid(Question question) {
        return validate(question).isEmpty();
    }

    private static boolean isDifficultyValidForType(Difficulty difficulty, QuestionType type) {
        if (difficulty == null || type == null) {
            return false;
        }
        return switch (type) {
            case ARCHITECTURE -> difficulty == Difficulty.MEDIUM || difficulty == Difficulty.HARD;
            case CODE_ANALYSIS, DEBUGGING -> difficulty != Difficulty.EASY;
            default -> true;
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean containsTrivialPattern(String questionText) {
        if (isBlank(questionText)) {
            return false;
        }
        String normalized = questionText.trim().toLowerCase(Locale.ROOT);
        for (String trivialPattern : TRIVIAL_PATTERNS) {
            if (normalized.contains(trivialPattern)) {
                return true;
            }
        }
        return false;
    }
}
