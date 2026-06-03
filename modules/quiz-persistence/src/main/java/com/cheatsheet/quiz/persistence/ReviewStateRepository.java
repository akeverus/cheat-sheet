package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с состоянием интервального повторения ({@code review_state}).
 *
 * <p>Каждый вопрос имеет одну запись в таблице {@code review_state},
 * хранящую параметры алгоритма SM-2 и статистику ответов.</p>
 *
 * @see ReviewState
 * @see ReviewResult
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewStateRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Находит состояние повторения по ID вопроса.
     *
     * @param questionId идентификатор вопроса
     * @return состояние или пустой Optional, если запись ещё не создана
     */
    public Optional<ReviewState> findByQuestionId(long questionId) {
        List<ReviewState> items = jdbcTemplate.query(
                "SELECT question_id, repetitions, interval_days, ease_factor, next_review_at, last_result, " +
                        "correct_count, wrong_count FROM review_state WHERE question_id = ?",
                (rs, rowNum) -> new ReviewState(
                        rs.getLong("question_id"),
                        rs.getInt("repetitions"),
                        rs.getInt("interval_days"),
                        rs.getDouble("ease_factor"),
                        rs.getLong("next_review_at"),
                        // Конвертация строки из БД в enum (безопасный парсинг)
                        ReviewResult.fromString(rs.getString("last_result")),
                        rs.getInt("correct_count"),
                        rs.getInt("wrong_count")
                ),
                questionId
        );
        return items.stream().findFirst();
    }

    /**
     * Создаёт запись review_state, если она ещё не существует.
     * PostgreSQL: ON CONFLICT DO NOTHING.
     *
     * @param questionId идентификатор вопроса
     * @param nowEpoch   текущее время в epoch-секундах
     */
    @Transactional
    public void insertIfAbsent(long questionId, long nowEpoch) {
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, next_review_at) VALUES (?, ?) " +
                        "ON CONFLICT (question_id) DO NOTHING",
                questionId,
                nowEpoch
        );
    }

    /**
     * Обновляет состояние повторения после ответа на вопрос.
     *
     * @param state обновлённое состояние (из {@link com.cheatsheet.quiz.service.SpacedRepetitionService})
     */
    @Transactional
    public void update(ReviewState state) {
        jdbcTemplate.update(
                "UPDATE review_state SET repetitions = ?, interval_days = ?, ease_factor = ?, " +
                        "next_review_at = ?, last_result = ?, correct_count = ?, wrong_count = ? " +
                        "WHERE question_id = ?",
                state.repetitions(),
                state.intervalDays(),
                state.easeFactor(),
                state.nextReviewAt(),
                // Enum -> строка для хранения в БД
                state.lastResult().name(),
                state.correctCount(),
                state.wrongCount(),
                state.questionId()
        );
    }

    /**
     * Сбрасывает прогресс по вопросу (при обновлении контента).
     *
     * <p>Обнуляет repetitions и interval, восстанавливает ease factor,
     * устанавливает last_result = {@link ReviewResult#RESET}.</p>
     *
     * @param questionId идентификатор вопроса
     * @param nowEpoch   текущее время (next_review_at)
     */
    @Transactional
    public void reset(long questionId, long nowEpoch) {
        jdbcTemplate.update(
                "UPDATE review_state SET repetitions = 0, interval_days = 0, ease_factor = ?, " +
                        "next_review_at = ?, last_result = ? WHERE question_id = ?",
                ReviewDefaults.DEFAULT_EASE_FACTOR,
                nowEpoch,
                ReviewResult.RESET.name(),
                questionId
        );
    }
}
