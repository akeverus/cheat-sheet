package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

import java.util.List;

/**
 * Ответ API на отправку ответа (POST /api/answer).
 *
 * <p>Помимо вердикта и объяснений несёт (хендофф-3): следующий интервал повторения
 * ({@code nextReviewAt}/{@code intervalDays}, UI-013) и начисленный опыт
 * ({@code xpAwarded}/{@code xpTotal}/{@code level}) для «+N XP» в баннере.</p>
 */
@Builder(toBuilder = true)
public record AnswerResponse(
        boolean correct,
        long correctOptionId,
        long selectedOptionId,
        String correctOptionLetter,
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
    /** Обратная совместимость: без полей интервала/XP (хендофф-3). */
    public AnswerResponse(
            boolean correct,
            long correctOptionId,
            long selectedOptionId,
            String correctOptionLetter,
            String answerHtml,
            String answerLeadHtml,
            String answerRestHtml,
            List<OptionExplanationDto> optionExplanations,
            String answerDisplayMode,
            int repetitions,
            List<RelatedQuestionDto> relatedQuestions,
            SessionInfoDto session
    ) {
        this(correct, correctOptionId, selectedOptionId, correctOptionLetter, answerHtml,
                answerLeadHtml, answerRestHtml, optionExplanations, answerDisplayMode, repetitions,
                relatedQuestions, session, 0L, 0, 0L, 0L, 0);
    }
}
