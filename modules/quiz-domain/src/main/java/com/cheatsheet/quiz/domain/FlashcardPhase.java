package com.cheatsheet.quiz.domain;

/**
 * Фазы режима Flashcard (State pattern).
 *
 * <p>Управляет UI-состояниями карточки:</p>
 * <ul>
 *   <li>{@link #QUESTION} — показан только вопрос, ответ скрыт;</li>
 *   <li>{@link #REVEALED} — ответ раскрыт, ожидание самооценки.</li>
 * </ul>
 */
public enum FlashcardPhase {
    QUESTION,
    REVEALED
}
