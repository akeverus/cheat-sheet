package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.service.ai.parser.AiResponseParser;
import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import lombok.experimental.UtilityClass;

/**
 * Собирает компактный prompt для генерации question options.
 */
@UtilityClass
public class QuestionPromptBuilder {

    public static String buildOptionsPrompt(String questionText, String codeSnippet, int maxInputLength) {
        String safeQuestion = AiPrompts.wrapUserInput(questionText);
        String safeCodeSnippet = AiResponseParser.truncate(codeSnippet, maxInputLength);
        if (!safeCodeSnippet.isEmpty()) {
            return escapeNonPlaceholderPercents(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE)
                    .formatted(safeQuestion, AiPrompts.wrapUserInput(safeCodeSnippet));
        }
        return escapeNonPlaceholderPercents(AiPrompts.USER_PROMPT_TEMPLATE).formatted(safeQuestion);
    }

    private static String escapeNonPlaceholderPercents(String template) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        StringBuilder escaped = new StringBuilder(template.length() + 16);
        for (int i = 0; i < template.length(); i++) {
            char ch = template.charAt(i);
            if (ch != '%') {
                escaped.append(ch);
                continue;
            }
            if (i + 1 < template.length()) {
                char next = template.charAt(i + 1);
                if (next == '%' || next == 's' || next == 'd' || next == 'f' || next == 'n') {
                    escaped.append('%');
                    continue;
                }
            }
            escaped.append("%%");
        }
        return escaped.toString();
    }
}
