package com.cheatsheet.quiz.service.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие, публикуемое после каждого ответа пользователя.
 *
 * <p>Подписчики (Observer pattern):</p>
 * <ul>
 *   <li>{@code WrongAnswerFeedbackService} — персональный фидбэк при ошибке;</li>
 *   <li>{@code DailyStreakService} — обновление ежедневного счётчика;</li>
 *   <li>{@code SessionSummaryService} — может обогащать данные сессии.</li>
 * </ul>
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerEvent extends ApplicationEvent {

    long questionId;
    long selectedOptionId;
    long correctOptionId;
    boolean correct;
    String questionText;
    String selectedOptionText;
    String correctOptionText;
    String answerMarkdown;
    String topic;

    public AnswerEvent(Object source,
                       long questionId,
                       long selectedOptionId,
                       long correctOptionId,
                       boolean correct,
                       String questionText,
                       String selectedOptionText,
                       String correctOptionText,
                       String answerMarkdown,
                       String topic) {
        super(source);
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.correctOptionId = correctOptionId;
        this.correct = correct;
        this.questionText = questionText;
        this.selectedOptionText = selectedOptionText;
        this.correctOptionText = correctOptionText;
        this.answerMarkdown = answerMarkdown;
        this.topic = topic;
    }

}
