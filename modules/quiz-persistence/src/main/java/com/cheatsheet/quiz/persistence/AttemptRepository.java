package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий построчного лога попыток ответа ({@code attempt}).
 *
 * <p>{@link #insert} пишет одну попытку и возвращает её id. Nullable-поля
 * (ревизия, вариант, время ответа, грейд, токены) передаются как {@code null}.
 * {@code created_at} — epoch-секунды, задаётся вызывающим для детерминизма.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptRepository {

    JdbcTemplate jdbcTemplate;

    private static final String COLUMNS =
            "id, question_id, question_revision_id, selected_option_id, outcome, " +
                    "is_first_attempt, response_time_ms, memory_grade, session_token, " +
                    "idempotency_key, opened_content_block_ids, created_at";

    /**
     * Сохраняет попытку и возвращает присвоенный идентификатор.
     */
    public long insert(Attempt attempt) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO attempt " +
                        "(question_id, question_revision_id, selected_option_id, outcome, " +
                        "is_first_attempt, response_time_ms, memory_grade, session_token, " +
                        "idempotency_key, opened_content_block_ids, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                attempt.questionId(),
                attempt.questionRevisionId(),
                attempt.selectedOptionId(),
                attempt.outcome().name(),
                attempt.firstAttempt(),
                attempt.responseTimeMs(),
                attempt.memoryGrade(),
                attempt.sessionToken(),
                attempt.idempotencyKey(),
                attempt.openedContentBlockIds(),
                attempt.createdAt());
        return id == null ? 0L : id;
    }

    /**
     * Все попытки по вопросу, новые первыми.
     */
    public List<Attempt> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM attempt WHERE question_id = ? " +
                        "ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs),
                questionId);
    }

    /**
     * Была ли уже хоть одна попытка по вопросу (для {@code is_first_attempt}).
     */
    public boolean existsByQuestionId(long questionId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM attempt WHERE question_id = ?",
                Long.class, questionId);
        return count != null && count > 0;
    }

    /**
     * Попытка по ключу идемпотентности (дедуп двойного POST), если есть.
     */
    public Optional<Attempt> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        List<Attempt> rows = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM attempt WHERE idempotency_key = ? LIMIT 1",
                (rs, rowNum) -> mapRow(rs),
                idempotencyKey);
        return rows.stream().findFirst();
    }

    /**
     * Агрегаты по таблице {@code attempt} для семантической аналитики
     * ({@link com.cheatsheet.quiz.domain.LearningMetrics}). Один проход по логу
     * попыток: разводит первую попытку (честная память) от повторений, считает
     * удержание в окнах и среднее время ответа.
     *
     * @param sevenDayCutoffEpoch  epoch-секунды границы окна 7 дней (now − 7д)
     * @param thirtyDayCutoffEpoch epoch-секунды границы окна 30 дней (now − 30д)
     * @return сырой агрегат (проценты вычисляет сервис)
     */
    public AttemptAggregate aggregate(long sevenDayCutoffEpoch, long thirtyDayCutoffEpoch) {
        List<AttemptAggregate> rows = jdbcTemplate.query(
                "SELECT " +
                        "COUNT(DISTINCT question_id) AS cards_seen, " +
                        "COUNT(*) AS attempts_total, " +
                        "COALESCE(SUM(CASE WHEN outcome = 'CORRECT' THEN 1 ELSE 0 END), 0) AS attempts_correct, " +
                        "COALESCE(SUM(CASE WHEN outcome IN ('WRONG', 'UNKNOWN') THEN 1 ELSE 0 END), 0) AS attempts_incorrect, " +
                        "COALESCE(SUM(CASE WHEN is_first_attempt THEN 1 ELSE 0 END), 0) AS first_total, " +
                        "COALESCE(SUM(CASE WHEN is_first_attempt AND outcome = 'CORRECT' THEN 1 ELSE 0 END), 0) AS first_correct, " +
                        "COALESCE(SUM(CASE WHEN NOT is_first_attempt AND outcome IN ('CORRECT','WRONG','UNKNOWN') AND created_at >= ? THEN 1 ELSE 0 END), 0) AS rev7_total, " +
                        "COALESCE(SUM(CASE WHEN NOT is_first_attempt AND outcome = 'CORRECT' AND created_at >= ? THEN 1 ELSE 0 END), 0) AS rev7_correct, " +
                        "COALESCE(SUM(CASE WHEN NOT is_first_attempt AND outcome IN ('CORRECT','WRONG','UNKNOWN') AND created_at >= ? THEN 1 ELSE 0 END), 0) AS rev30_total, " +
                        "COALESCE(SUM(CASE WHEN NOT is_first_attempt AND outcome = 'CORRECT' AND created_at >= ? THEN 1 ELSE 0 END), 0) AS rev30_correct, " +
                        "AVG(response_time_ms) AS avg_response_ms " +
                        "FROM attempt",
                (rs, rowNum) -> {
                    double avg = rs.getDouble("avg_response_ms");
                    double avgResponse = rs.wasNull() ? -1.0 : avg;
                    return new AttemptAggregate(
                            rs.getLong("cards_seen"),
                            rs.getLong("attempts_total"),
                            rs.getLong("attempts_correct"),
                            rs.getLong("attempts_incorrect"),
                            rs.getLong("first_total"),
                            rs.getLong("first_correct"),
                            rs.getLong("rev7_total"),
                            rs.getLong("rev7_correct"),
                            rs.getLong("rev30_total"),
                            rs.getLong("rev30_correct"),
                            avgResponse);
                },
                sevenDayCutoffEpoch, sevenDayCutoffEpoch, thirtyDayCutoffEpoch, thirtyDayCutoffEpoch);
        return rows.isEmpty() ? AttemptAggregate.EMPTY : rows.get(0);
    }

    /**
     * Сырой агрегат попыток: числители/знаменатели для долей считает
     * {@code LearningMetricsService}. {@code averageResponseMs = -1} — нет данных.
     */
    public record AttemptAggregate(
            long cardsSeen,
            long attemptsTotal,
            long attemptsCorrect,
            long attemptsIncorrect,
            long firstTotal,
            long firstCorrect,
            long rev7Total,
            long rev7Correct,
            long rev30Total,
            long rev30Correct,
            double averageResponseMs
    ) {
        public static final AttemptAggregate EMPTY =
                new AttemptAggregate(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1.0);
    }

    /**
     * Последние ошибочные попытки ({@code WRONG}/{@code UNKNOWN}) с текстом вопроса,
     * темой и вариантами — для drill-down в аналитике (UI-012, хендофф-3). Верный
     * вариант берётся как первый {@code is_correct = 1} по вопросу (LATERAL).
     *
     * @param limit максимум строк
     */
    public List<MistakeAttempt> findRecentMistakes(int limit) {
        return jdbcTemplate.query(
                "SELECT a.question_id, a.created_at, a.response_time_ms, a.outcome, " +
                        "q.question_text, q.topic, " +
                        "sel.option_text AS selected_text, cor.option_text AS correct_text " +
                        "FROM attempt a " +
                        "JOIN questions q ON q.id = a.question_id " +
                        "LEFT JOIN answer_options sel ON sel.id = a.selected_option_id " +
                        "LEFT JOIN LATERAL (" +
                        "  SELECT option_text FROM answer_options " +
                        "  WHERE question_id = a.question_id AND is_correct = 1 LIMIT 1" +
                        ") cor ON true " +
                        "WHERE a.outcome IN ('WRONG', 'UNKNOWN') " +
                        "ORDER BY a.created_at DESC, a.id DESC LIMIT ?",
                (rs, rowNum) -> new MistakeAttempt(
                        rs.getLong("question_id"),
                        rs.getString("question_text"),
                        rs.getString("topic"),
                        rs.getString("selected_text"),
                        rs.getString("correct_text"),
                        rs.getString("outcome"),
                        getNullableInt(rs, "response_time_ms"),
                        rs.getLong("created_at")),
                limit);
    }

    /** Дневная точность попыток в окне, старые дни первыми. */
    public List<DailyAccuracy> findDailyAccuracy(long cutoffEpoch) {
        return jdbcTemplate.query(
                "SELECT to_char(to_timestamp(created_at) AT TIME ZONE 'UTC', 'YYYY-MM-DD') AS day, " +
                        "COUNT(*) AS total, " +
                        "SUM(CASE WHEN outcome = 'CORRECT' THEN 1 ELSE 0 END) AS correct " +
                        "FROM attempt " +
                        "WHERE created_at >= ? AND outcome IN ('CORRECT', 'WRONG', 'UNKNOWN') " +
                        "GROUP BY day ORDER BY day",
                (rs, rowNum) -> new DailyAccuracy(
                        rs.getString("day"),
                        rs.getLong("correct"),
                        rs.getLong("total")),
                cutoffEpoch);
    }

    /** Строка drill-down ошибок: вопрос, тема, выбранный/верный вариант, время. */
    public record MistakeAttempt(
            long questionId,
            String questionText,
            String topic,
            String selectedOptionText,
            String correctOptionText,
            String outcome,
            Integer responseTimeMs,
            long createdAt
    ) {}

    public record DailyAccuracy(String day, long correct, long total) {
        public double percent() {
            return total == 0 ? 0.0 : correct * 100.0 / total;
        }
    }

    private static Attempt mapRow(ResultSet rs) throws SQLException {
        return new Attempt(
                rs.getLong("id"),
                rs.getLong("question_id"),
                getNullableLong(rs, "question_revision_id"),
                getNullableLong(rs, "selected_option_id"),
                AttemptOutcome.valueOf(rs.getString("outcome")),
                rs.getBoolean("is_first_attempt"),
                getNullableInt(rs, "response_time_ms"),
                getNullableInt(rs, "memory_grade"),
                rs.getString("session_token"),
                rs.getString("idempotency_key"),
                rs.getString("opened_content_block_ids"),
                rs.getLong("created_at"));
    }

    private static Long getNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
