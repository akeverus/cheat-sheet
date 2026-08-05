package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

import java.util.List;

/**
 * Ответ API на отправку ответа (POST /api/answer).
 *
 * <p>Помимо вердикта и объяснений несёт следующий интервал повторения
 * ({@code nextReviewAt}/{@code intervalDays}) и начисленный опыт
 * ({@code xpAwarded}/{@code xpTotal}/{@code level}) для «+N XP» в разборе.</p>
 *
 * <p>Букв вариантов (A/B/C) в ответе нет: они были сырьём ровно для одной
 * строки — вердикта, — и клиент собирал её сам. Теперь строку собирает сервер.</p>
 *
 * <p>{@code verdictText} и {@code nextReviewText} — готовые к печати строки из
 * {@code AnswerCopy}, а не сырьё для сборки на клиенте: ровно те же строки
 * кладёт в модель no-JS-путь, и расходиться им нельзя.</p>
 */
@Builder(toBuilder = true)
public record AnswerResponse(
        boolean correct,
        long correctOptionId,
        long selectedOptionId,
        String verdictText,
        String nextReviewText,
        String answerHtml,
        String answerLeadHtml,
        String answerRestHtml,
        List<OptionExplanationDto> optionExplanations,
        String answerDisplayMode,
        int repetitions,
        List<RelatedQuestionDto> relatedQuestions,
        SessionInfoDto session,
        long nextReviewAt,
        int intervalDays,
        long xpAwarded,
        long xpTotal,
        int level
) {
}
