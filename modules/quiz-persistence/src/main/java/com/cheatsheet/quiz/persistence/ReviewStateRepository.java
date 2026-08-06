package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.FsrsState;
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
 * Репозиторий состояния интервального повторения ({@code review_state}), scoped by {@link OwnerContext}.
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewStateRepository {

    JdbcTemplate jdbcTemplate;

    public Optional<ReviewState> findByQuestionId(long questionId) {
        List<ReviewState> items = jdbcTemplate.query(
                "SELECT question_id, repetitions, interval_days, ease_factor, next_review_at, last_result, " +
                        "correct_count, wrong_count FROM review_state WHERE question_id = ? AND owner_key = ?",
                (rs, rowNum) -> new ReviewState(
                        rs.getLong("question_id"),
                        rs.getInt("repetitions"),
                        rs.getInt("interval_days"),
                        rs.getDouble("ease_factor"),
                        rs.getLong("next_review_at"),
                        ReviewResult.fromString(rs.getString("last_result")),
                        rs.getInt("correct_count"),
                        rs.getInt("wrong_count")
                ),
                questionId,
                OwnerContext.current()
        );
        return items.stream().findFirst();
    }

    /**
     * Сохраняет состояние SM-2 — <b>upsert</b>, а не UPDATE.
     *
     * <p>Строка прогресса создаётся первым ответом, и только им. Раньше её заводил
     * показ вопроса (метод {@code insertIfAbsent}, удалён) — из-за этого «просмотрел»
     * и «ответил» были для базы неотличимы, и ни разу не отвеченный вопрос числился
     * просроченным повторением. Показ больше не пишет ничего, поэтому первый ответ
     * обязан уметь вставлять: голый UPDATE молча обновил бы ноль строк и потерял
     * бы результат.
     */
    @Transactional
    public void update(ReviewState state) {
        jdbcTemplate.update(
                "INSERT INTO review_state (owner_key, question_id, repetitions, interval_days, ease_factor, " +
                        "next_review_at, last_result, correct_count, wrong_count) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (owner_key, question_id) DO UPDATE SET " +
                        "repetitions = EXCLUDED.repetitions, interval_days = EXCLUDED.interval_days, " +
                        "ease_factor = EXCLUDED.ease_factor, next_review_at = EXCLUDED.next_review_at, " +
                        "last_result = EXCLUDED.last_result, correct_count = EXCLUDED.correct_count, " +
                        "wrong_count = EXCLUDED.wrong_count",
                OwnerContext.current(),
                state.questionId(),
                state.repetitions(),
                state.intervalDays(),
                state.easeFactor(),
                state.nextReviewAt(),
                state.lastResult().name(),
                state.correctCount(),
                state.wrongCount()
        );
    }

    public Optional<FsrsState> findFsrsState(long questionId) {
        List<FsrsState> items = jdbcTemplate.query(
                "SELECT question_id, stability, difficulty, lapses, algo_version, last_reviewed_at " +
                        "FROM review_state WHERE question_id = ? AND owner_key = ?",
                (rs, rowNum) -> new FsrsState(
                        rs.getLong("question_id"),
                        getNullableDouble(rs, "stability"),
                        getNullableDouble(rs, "difficulty"),
                        rs.getInt("lapses"),
                        rs.getString("algo_version"),
                        getNullableLong(rs, "last_reviewed_at")
                ),
                questionId,
                OwnerContext.current()
        );
        return items.stream().findFirst();
    }

    /**
     * Сохраняет состояние FSRS — upsert по той же причине, что и {@link #update}.
     *
     * <p>Поля SM-2 (repetitions / interval_days / ease_factor) в списке колонок
     * отсутствуют намеренно: при вставке их проставляет схема значениями по
     * умолчанию, при конфликте они остаются нетронутыми. FSRS ведёт собственную
     * память (stability / difficulty / lapses) и не имеет права затирать
     * прогресс, накопленный другим планировщиком, — переключение алгоритма
     * туда-обратно не должно стоить пользователю выученного.
     */
    @Transactional
    public void updateFsrs(FsrsState state, long nextReviewAt, ReviewResult lastResult,
                           int correctCount, int wrongCount) {
        jdbcTemplate.update(
                "INSERT INTO review_state (owner_key, question_id, stability, difficulty, lapses, algo_version, " +
                        "last_reviewed_at, next_review_at, last_result, correct_count, wrong_count) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (owner_key, question_id) DO UPDATE SET " +
                        "stability = EXCLUDED.stability, difficulty = EXCLUDED.difficulty, " +
                        "lapses = EXCLUDED.lapses, algo_version = EXCLUDED.algo_version, " +
                        "last_reviewed_at = EXCLUDED.last_reviewed_at, next_review_at = EXCLUDED.next_review_at, " +
                        "last_result = EXCLUDED.last_result, correct_count = EXCLUDED.correct_count, " +
                        "wrong_count = EXCLUDED.wrong_count",
                OwnerContext.current(),
                state.questionId(),
                state.stability(),
                state.difficulty(),
                state.lapses(),
                state.algoVersion(),
                state.lastReviewedAt(),
                nextReviewAt,
                lastResult.name(),
                correctCount,
                wrongCount
        );
    }

    private static Double getNullableDouble(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private static Long getNullableLong(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    @Transactional
    public void reset(long questionId, long nowEpoch) {
        // Контент вопроса изменился → забываем весь выученный прогресс. Раньше сбрасывались
        // только SM-2-поля, а FSRS-память (stability/difficulty/lapses/algo_version) переживала
        // reset — в FSRS-режиме изменённый вопрос сохранял устаревшую mastery. Обнуляем FSRS в
        // «свежее» состояние (null-память, lapses=0), идентичное только что вставленной строке.
        jdbcTemplate.update(
                "UPDATE review_state SET repetitions = 0, interval_days = 0, ease_factor = ?, " +
                        "next_review_at = ?, last_result = ?, " +
                        "stability = NULL, difficulty = NULL, lapses = 0, algo_version = NULL " +
                        "WHERE question_id = ? AND owner_key = ?",
                ReviewDefaults.DEFAULT_EASE_FACTOR,
                nowEpoch,
                ReviewResult.RESET.name(),
                questionId,
                OwnerContext.current()
        );
    }
}
