package com.cheatsheet.quiz.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Сессия тестирования: хранит состояние текущей серии вопросов.
 *
 * <p>Сериализуется в HTTP-сессию для сохранения состояния между запросами.</p>
 *
 * <p>Поддерживает режимы {@link InterviewMode#TRAINING}, {@link InterviewMode#EXAM}
 * и {@link InterviewMode#MARATHON} с различной логикой (штрафные вопросы в экзамене и т.д.).</p>
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Режим тестирования. */
    final InterviewMode mode;

    /** Список ID вопросов в порядке прохождения (может расти при штрафных). */
    final List<Long> questionIds;

    /** Фильтр: тема. */
    final String topic;

    /** Фильтр: группа тем. */
    final String group;

    /** Фильтр: только важные вопросы. */
    final Boolean importantOnly;

    /** Фильтр: только вопросы с ошибками. */
    final Boolean onlyWrong;

    /** Режим перемешки: вопросы из всех тем в случайном порядке. */
    final Boolean shuffle;

    /** Режим учебного порядка тем. */
    final Boolean ordered;

    /** Момент начала сессии. */
    final Instant startedAt;

    /** Текущий индекс в списке вопросов. */
    @Getter(AccessLevel.NONE)
    int index;

    /** Количество правильных ответов в сессии. */
    @Getter(AccessLevel.NONE)
    int correct;

    /** Количество неправильных ответов в сессии. */
    @Getter(AccessLevel.NONE)
    int wrong;

    /** Количество вопросов, отмеченных «не знаю» (UNKNOWN, FLOW-03). */
    @Getter(AccessLevel.NONE)
    int unknown;

    /** Текущая фаза в режиме STUDY (LEARN или QUIZ). */
    StudyPhase studyPhase;

    /** Текущая фаза в режиме FLASHCARD (QUESTION или REVEALED). */
    FlashcardPhase flashcardPhase;

    /**
     * Момент показа текущего вопроса (epoch-миллисекунды) — ставится при рендере
     * страницы вопроса, потребляется при сабмите для измерения времени ответа
     * (think-time). {@code null}, если вопрос ещё не показывался или метка уже
     * потреблена. Не входит в конструкторы/builder — управляется только через
     * {@link #markQuestionServed(long)} / {@link #takeResponseTimeMs(long)}.
     */
    @Getter(AccessLevel.NONE)
    Long questionServedAtMillis;

    /** Верхняя граница разумного времени ответа: 30 минут. Больше — вкладку забыли открытой. */
    private static final long MAX_RESPONSE_TIME_MS = 30L * 60L * 1000L;

    /** История ответов в сессии (для итогов). */
    final List<AnswerRecord> answerHistory = new ArrayList<>();

    public InterviewSession(InterviewMode mode, List<Long> questionIds, String topic,
                            Boolean importantOnly, Boolean onlyWrong, Boolean shuffle) {
        this(mode, questionIds, topic, null, importantOnly, onlyWrong, shuffle, true, Instant.now());
    }

    public InterviewSession(InterviewMode mode, List<Long> questionIds, String topic,
                            Boolean importantOnly, Boolean onlyWrong, Boolean shuffle, Instant startedAt) {
        this(mode, questionIds, topic, null, importantOnly, onlyWrong, shuffle, true, startedAt);
    }

    @Builder
    public InterviewSession(InterviewMode mode, List<Long> questionIds, String topic, String group,
                            Boolean importantOnly, Boolean onlyWrong, Boolean shuffle, Boolean ordered) {
        this(mode, questionIds, topic, group, importantOnly, onlyWrong, shuffle, ordered, Instant.now());
    }

    public InterviewSession(InterviewMode mode, List<Long> questionIds, String topic, String group,
                            Boolean importantOnly, Boolean onlyWrong, Boolean shuffle, Boolean ordered, Instant startedAt) {
        this.mode = mode;
        this.questionIds = new ArrayList<>(questionIds);
        this.topic = topic;
        this.group = group;
        this.importantOnly = importantOnly;
        this.onlyWrong = onlyWrong;
        this.shuffle = shuffle;
        this.ordered = ordered;
        this.startedAt = startedAt;
        this.index = 0;
        this.correct = 0;
        this.wrong = 0;
        this.unknown = 0;
        this.studyPhase = (mode == InterviewMode.STUDY) ? StudyPhase.LEARN : null;
        this.flashcardPhase = (mode == InterviewMode.FLASHCARD) ? FlashcardPhase.QUESTION : null;
    }

    /**
     * Фиксирует момент показа текущего вопроса (для измерения времени ответа).
     *
     * @param epochMillis текущее время в epoch-миллисекундах
     */
    public synchronized void markQuestionServed(long epochMillis) {
        this.questionServedAtMillis = epochMillis;
    }

    /**
     * Возвращает время ответа (мс) с момента показа вопроса и сбрасывает метку.
     * {@code null}, если метки нет либо интервал вне разумных границ (отрицательный
     * из-за рассинхронизации часов, либо &gt; 30 минут — вкладку оставили открытой).
     *
     * @param nowMillis текущее время в epoch-миллисекундах
     * @return время ответа в мс или {@code null}
     */
    public synchronized Integer takeResponseTimeMs(long nowMillis) {
        if (questionServedAtMillis == null) {
            return null;
        }
        long elapsed = nowMillis - questionServedAtMillis;
        questionServedAtMillis = null;
        if (elapsed < 0 || elapsed > MAX_RESPONSE_TIME_MS) {
            return null;
        }
        return (int) elapsed;
    }

    public synchronized int getTotal() {
        return questionIds.size();
    }

    public synchronized boolean isFinished() {
        return index >= questionIds.size();
    }

    public synchronized Long currentQuestionId() {
        if (isFinished()) {
            return null;
        }
        return questionIds.get(index);
    }

    public synchronized void registerAnswer(boolean isCorrect) {
        if (isCorrect) {
            correct++;
        } else {
            wrong++;
        }
        Long qId = index < questionIds.size() ? questionIds.get(index) : null;
        if (qId != null) {
            answerHistory.add(new AnswerRecord(qId, isCorrect, null));
        }
        index++;
    }

    /** Регистрирует ответ с указанием темы (для итогов сессии). */
    public synchronized void registerAnswer(boolean isCorrect, String questionTopic) {
        if (isCorrect) {
            correct++;
        } else {
            wrong++;
        }
        Long qId = index < questionIds.size() ? questionIds.get(index) : null;
        if (qId != null) {
            answerHistory.add(new AnswerRecord(qId, isCorrect, questionTopic));
        }
        index++;
    }

    /**
     * Регистрирует исход «не знаю» (UNKNOWN, FLOW-03): продвигает сессию как ответ,
     * но НЕ в correct/wrong — отдельный счётчик {@code unknown}. В {@code answerHistory}
     * не добавляется (форма {@link AnswerRecord} неизменна ради совместимости сериализации),
     * поэтому в разбор ошибок такой вопрос не попадает — только в headline-счётчик итогов.
     */
    public synchronized void registerUnknown(String questionTopic) {
        unknown++;
        index++;
    }

    /** Количество вопросов, отмеченных «не знаю» (UNKNOWN). */
    public synchronized int getUnknown() {
        return unknown;
    }

    /** Возвращает историю ответов. */
    public synchronized List<AnswerRecord> getAnswerHistory() {
        return List.copyOf(answerHistory);
    }

    /** Запись об ответе в сессии. */
    @Builder(toBuilder = true)
    public record AnswerRecord(long questionId, boolean correct, String topic) implements java.io.Serializable {}

    public synchronized void addPenaltyQuestions(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            questionIds.addAll(ids);
        }
    }

    public synchronized int getIndex() {
        return index;
    }

    public synchronized int getCorrect() {
        return correct;
    }

    public synchronized int getWrong() {
        return wrong;
    }

    /** Возвращает текущую фазу режима STUDY (или null для других режимов). */
    public synchronized StudyPhase getStudyPhase() {
        return studyPhase;
    }

    /** Переключает фазу из LEARN в QUIZ (для режима STUDY). */
    public synchronized void switchToQuizPhase() {
        if (studyPhase == StudyPhase.LEARN) {
            studyPhase = StudyPhase.QUIZ;
        }
    }

    /** Переключает фазу обратно в LEARN для следующего вопроса (для режима STUDY). */
    public synchronized void switchToLearnPhase() {
        if (mode == InterviewMode.STUDY) {
            studyPhase = StudyPhase.LEARN;
        }
    }

    /** Возвращает текущую фазу режима FLASHCARD (или null для других режимов). */
    public synchronized FlashcardPhase getFlashcardPhase() {
        return flashcardPhase;
    }

    /** Раскрывает ответ в режиме FLASHCARD (переход QUESTION → REVEALED). */
    public synchronized void revealFlashcard() {
        if (flashcardPhase == FlashcardPhase.QUESTION) {
            flashcardPhase = FlashcardPhase.REVEALED;
        }
    }

    /** Сбрасывает flashcard-фазу на QUESTION для следующего вопроса. */
    public synchronized void resetFlashcardPhase() {
        if (mode == InterviewMode.FLASHCARD) {
            flashcardPhase = FlashcardPhase.QUESTION;
        }
    }

    /** Продвигает сессию вперёд (для FLASHCARD — без correct/wrong, просто index++). */
    public synchronized void advanceFlashcard() {
        index++;
    }

    /** Проверяет, является ли сессия режимом FLASHCARD. */
    public boolean isFlashcardMode() {
        return mode == InterviewMode.FLASHCARD;
    }
}
