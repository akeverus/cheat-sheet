package com.cheatsheet.quiz.domain;

/**
 * Фаза режима изучения ({@link InterviewMode#STUDY}).
 *
 * <ul>
 *   <li>{@link #LEARN} — показ вопроса + полного ответа (учебная карточка);</li>
 *   <li>{@link #QUIZ} — проверка: тот же вопрос + варианты ответов.</li>
 * </ul>
 */
public enum StudyPhase {

    /** Фаза изучения: вопрос + полный ответ. */
    LEARN,

    /** Фаза проверки: тот же вопрос + варианты. */
    QUIZ
}
