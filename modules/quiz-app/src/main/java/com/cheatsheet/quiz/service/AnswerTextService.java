package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.util.TextUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для извлечения текстовых ответов из markdown (без разметки).
 *
 * <p>Используется для:</p>
 * <ul>
 *   <li>генерации правильного варианта ответа (из полного markdown);</li>
 *   <li>генерации неверных вариантов (из ответов других вопросов, fallback).</li>
 * </ul>
 */
@Service
public class AnswerTextService {

    private final MarkdownRenderService markdownRenderService;
    private final AppProperties appProperties;

    public AnswerTextService(MarkdownRenderService markdownRenderService, AppProperties appProperties) {
        this.markdownRenderService = markdownRenderService;
        this.appProperties = appProperties;
    }

    /**
     * Извлекает текстовый ответ из markdown (plain text, обрезка по app.interview.max-option-length).
     *
     * @param answerMarkdown полный markdown-ответ
     * @return текст без markdown-синтаксиса
     */
    public String toShortAnswer(String answerMarkdown) {
        if (answerMarkdown == null || answerMarkdown.isBlank()) {
            return "";
        }
        String plain = markdownRenderService.toPlainText(answerMarkdown);
        int maxLen = appProperties.getInterview().getMaxOptionLength();
        if (plain.length() <= maxLen) {
            return plain;
        }
        int minDotPos = appProperties.getInterview().getMinSentenceDotPosition();
        int dotIndex = plain.indexOf('.', minDotPos);
        if (dotIndex > minDotPos && dotIndex < maxLen) {
            return plain.substring(0, dotIndex + 1);
        }
        return TextUtils.truncateAtWordBoundary(plain, maxLen);
    }

    /**
     * Извлекает короткие ответы из списка markdown-текстов.
     *
     * @param answerMarkdownList список полных markdown-ответов
     * @return список текстовых ответов без markdown (пустые исключены)
     */
    public List<String> extractShortAnswers(List<String> answerMarkdownList) {
        List<String> result = new ArrayList<>();
        for (String answer : answerMarkdownList) {
            String shortAnswer = toShortAnswer(answer);
            if (!shortAnswer.isBlank()) {
                result.add(shortAnswer);
            }
        }
        return result;
    }
}
