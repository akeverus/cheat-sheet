package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

/**
 * Контракт интеграции с LLM для генерации учебного контента.
 */
public interface AiQuestionClient {

    @Builder(toBuilder = true)
    record CanonicalQuestion(
            String questionText,
            String answerMarkdown,
            String questionType,
            String codeSnippet
    ) {}

    OptionSource sourceId();

    Optional<GeneratedOptions> generateOptions(String questionText, String codeSnippet);

    Optional<List<String>> generateAlternativeQuestions(String questionText, String answerMarkdown, int count);

    default Optional<List<String>> generateHints(String questionText, String answerMarkdown, List<String> wrongOptions) {
        return Optional.empty();
    }

    default Optional<String> generateDiagram(String questionText, String answerMarkdown, String topic) {
        return Optional.empty();
    }

    default Optional<String> cleanCodeSnippet(String code, String questionText) {
        return Optional.empty();
    }

    default Optional<String> generateWrongAnswerFeedback(String questionText, String selectedOptionText,
                                                         String correctOptionText, String answerMarkdown) {
        return Optional.empty();
    }

    default Optional<String> generateTakeaway(String questionText, String answerMarkdown) {
        return Optional.empty();
    }

    default Optional<String> generateComparison(String questionText, String selectedOptionText,
                                                String correctOptionText) {
        return Optional.empty();
    }

    default Optional<String> generateCodeTrace(String questionText, String codeSnippet) {
        return Optional.empty();
    }

    default Optional<CanonicalQuestion> canonicalizeQuestion(
            String rawQuestionTitle,
            String rawAnswerMarkdown,
            String sourcePath
    ) {
        return Optional.empty();
    }

    default Optional<String> generateStructuredJson(String prompt) {
        return Optional.empty();
    }
}
