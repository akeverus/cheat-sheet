package com.cheatsheet.quiz.feature.interview.dto.response.session;

import lombok.Builder;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionOptionDto;

import java.util.List;

/**
 * API DTO вопроса для выдачи в тренировочном потоке.
 *
 * @param questionId идентификатор вопроса
 * @param questionText текст вопроса
 * @param topic тема вопроса
 * @param questionType тип вопроса
 * @param codeSnippet фрагмент кода (если есть)
 * @param diagramMermaid mermaid-диаграмма (если есть)
 * @param options варианты ответа
 */
@Builder(toBuilder = true)
public record QuestionResponseDTO(
        long questionId,
        String questionText,
        String topic,
        String questionType,
        String codeSnippet,
        String diagramMermaid,
        List<NextQuestionOptionDto> options
) {
}
