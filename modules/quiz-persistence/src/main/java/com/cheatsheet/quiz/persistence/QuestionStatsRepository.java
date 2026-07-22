package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.domain.QuestionDifficulty;
import com.cheatsheet.quiz.domain.TopicStats;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для статистических запросов по вопросам и прогрессу обучения.
 *
 * <p>Выделен из {@link QuestionRepository} в отдельный класс
 * для соблюдения принципа единой ответственности (SRP).</p>
 *
 * @see InterviewFilterSql
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionStatsRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Подсчитывает общее количество вопросов (без фильтра).
     *
     * @return количество вопросов
     */
    public long countAll() {
        Long value = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        return value == null ? 0L : value;
    }

    /**
     * Распределение вопросов банка по статической сложности ({@code EASY/MEDIUM/HARD})
     * для доната «Сложность вопросов» в аналитике (хендофф-3, Этап 6). Возвращает по
     * одной строке на встреченный уровень; нормализацию/сортировку делает слой выше.
     */
    public List<DifficultyBucket> findDifficultyDistribution() {
        return jdbcTemplate.query(
                "SELECT difficulty, COUNT(*) AS cnt FROM questions GROUP BY difficulty",
                (rs, rowNum) -> new DifficultyBucket(
                        rs.getString("difficulty"),
                        rs.getLong("cnt")));
    }

    /** Число вопросов заданной сложности. {@code difficulty} — как хранится в БД. */
    public record DifficultyBucket(String difficulty, long count) {}

    /**
     * Подсчитывает количество вопросов с применением фильтра.
     *
     * @param topic     тема (null — все)
     * @param important только важные
     * @param onlyWrong только с ошибками
     * @return количество вопросов
     */
    public long countAllFiltered(String topic, Boolean important, Boolean onlyWrong) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Подсчитывает количество просроченных (due) вопросов.
     *
     * @param nowEpoch текущее время в epoch-секундах
     */
    public long countDue(String topic, Boolean important, Boolean onlyWrong, long nowEpoch) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE rs.next_review_at <= ? ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Подсчитывает количество «выученных» вопросов (repetitions ≥ minRepetitions).
     *
     * @param minRepetitions порог повторений
     */
    public long countLearned(String topic, Boolean important, Boolean onlyWrong, int minRepetitions) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE rs.repetitions >= ? ");
        var params = new ArrayList<Object>();
        params.add(minRepetitions);
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Суммирует количество правильных ответов по фильтру.
     */
    public long sumCorrect(String topic, Boolean important, Boolean onlyWrong) {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(rs.correct_count), 0) FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Суммирует количество неправильных ответов по фильтру.
     */
    public long sumWrong(String topic, Boolean important, Boolean onlyWrong) {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(rs.wrong_count), 0) FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Возвращает агрегированную статистику одним запросом (total, due, learned, correct, wrong).
     *
     * <p>Заменяет 5 отдельных запросов ({@link #countAllFiltered}, {@link #countDue},
     * {@link #countLearned}, {@link #sumCorrect}, {@link #sumWrong}) одним SQL-запросом
     * для лучшей производительности.</p>
     *
     * @param topic            тема (null — все)
     * @param important        только важные
     * @param onlyWrong        только с ошибками
     * @param nowEpoch         текущее время в epoch-секундах
     * @param minRepetitions   порог повторений для «выучено»
     * @return агрегированная статистика
     */
    public InterviewStats getAggregatedStats(String topic, Boolean important, Boolean onlyWrong,
                                             long nowEpoch, int minRepetitions) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total, " +
                        "SUM(CASE WHEN rs.next_review_at <= ? THEN 1 ELSE 0 END) AS due, " +
                        "SUM(CASE WHEN rs.repetitions >= ? THEN 1 ELSE 0 END) AS learned, " +
                        "COALESCE(SUM(rs.correct_count), 0) AS correct_sum, " +
                        "COALESCE(SUM(rs.wrong_count), 0) AS wrong_sum " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        params.add(minRepetitions);
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);

        List<InterviewStats> result = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new InterviewStats(
                rs.getLong("total"),
                rs.getLong("due"),
                rs.getLong("learned"),
                rs.getLong("correct_sum"),
                rs.getLong("wrong_sum")
        ), params.toArray());
        return result.isEmpty() ? new InterviewStats(0, 0, 0, 0, 0) : result.get(0);
    }

    /**
     * Агрегат карточек и расписания из {@code review_state} (глобально, без
     * фильтра) для {@link com.cheatsheet.quiz.domain.LearningMetrics}. Разводит
     * {@code dueNow} (готовы к повтору сейчас) и {@code overdue} (просрочены —
     * запланированы раньше {@code overdueCutoffEpoch}, обычно now − сутки).
     *
     * @param nowEpoch          текущее время (epoch-секунды)
     * @param overdueCutoffEpoch граница «просрочки» (обычно now − 86400)
     * @param minRepetitions    порог «выучено»
     */
    public ScheduleAggregate getScheduleAggregate(long nowEpoch, long overdueCutoffEpoch, int minRepetitions) {
        List<ScheduleAggregate> rows = jdbcTemplate.query(
                "SELECT COUNT(*) AS cards_total, " +
                        "COALESCE(SUM(CASE WHEN repetitions >= ? THEN 1 ELSE 0 END), 0) AS cards_mastered, " +
                        "COALESCE(SUM(CASE WHEN next_review_at <= ? THEN 1 ELSE 0 END), 0) AS due_now, " +
                        "COALESCE(SUM(CASE WHEN next_review_at <= ? THEN 1 ELSE 0 END), 0) AS overdue " +
                        "FROM review_state WHERE owner_key = ?",
                (rs, rowNum) -> new ScheduleAggregate(
                        rs.getLong("cards_total"),
                        rs.getLong("cards_mastered"),
                        rs.getLong("due_now"),
                        rs.getLong("overdue")),
                minRepetitions, nowEpoch, overdueCutoffEpoch, OwnerContext.current());
        return rows.isEmpty() ? ScheduleAggregate.EMPTY : rows.get(0);
    }

    /** Агрегат карточек и расписания из {@code review_state}. */
    public record ScheduleAggregate(long cardsTotal, long cardsMastered, long dueNow, long overdue) {
        public static final ScheduleAggregate EMPTY = new ScheduleAggregate(0, 0, 0, 0);
    }

    /**
     * Возвращает агрегированную статистику по списку тем.
     */
    public InterviewStats getAggregatedStatsByTopics(List<String> topics, Boolean important, Boolean onlyWrong,
                                                     long nowEpoch, int minRepetitions) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total, " +
                        "SUM(CASE WHEN rs.next_review_at <= ? THEN 1 ELSE 0 END) AS due, " +
                        "SUM(CASE WHEN rs.repetitions >= ? THEN 1 ELSE 0 END) AS learned, " +
                        "COALESCE(SUM(rs.correct_count), 0) AS correct_sum, " +
                        "COALESCE(SUM(rs.wrong_count), 0) AS wrong_sum " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        params.add(minRepetitions);
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);

        List<InterviewStats> result = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new InterviewStats(
                rs.getLong("total"),
                rs.getLong("due"),
                rs.getLong("learned"),
                rs.getLong("correct_sum"),
                rs.getLong("wrong_sum")
        ), params.toArray());
        return result.isEmpty() ? new InterviewStats(0, 0, 0, 0, 0) : result.get(0);
    }

    /**
     * Возвращает статистику по каждой теме (для графиков в UI).
     *
     * @param nowEpoch         текущее время (для подсчёта due)
     * @param learnedThreshold порог повторений для «выучено»
     */
    public List<TopicStats> findTopicStats(long nowEpoch, int learnedThreshold) {
        return jdbcTemplate.query(
                "SELECT q.topic AS topic, COUNT(*) AS total, " +
                        "SUM(CASE WHEN rs.next_review_at <= ? THEN 1 ELSE 0 END) AS due, " +
                        "SUM(CASE WHEN rs.repetitions >= ? THEN 1 ELSE 0 END) AS learned, " +
                        "SUM(rs.correct_count) AS correct, SUM(rs.wrong_count) AS wrong, " +
                        "SUM(q.regen_count) AS regen_sum, " +
                        "AVG(rs.ease_factor * rs.interval_days) AS maturity_score " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id " +
                        "WHERE rs.owner_key = ? " +
                        "GROUP BY q.topic ORDER BY q.topic",
                (rs, rowNum) -> new TopicStats(
                        rs.getString("topic"), rs.getLong("total"),
                        rs.getLong("due"), rs.getLong("learned"),
                        rs.getLong("correct"), rs.getLong("wrong"),
                        rs.getLong("regen_sum"),
                        rs.getDouble("maturity_score")),
                nowEpoch, learnedThreshold, OwnerContext.current());
    }

    /**
     * Находит тему с наименьшей точностью (accuracy < threshold) среди тем с ответами.
     *
     * @param accuracyThreshold порог точности (например, 70.0)
     * @return имя темы или null, если нет слабых тем
     */
    public String findWeakestTopic(double accuracyThreshold) {
        String accuracyExpr = "CASE WHEN (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 " +
                "THEN CAST(SUM(rs.correct_count) AS REAL) * 100.0 / (SUM(rs.correct_count) + SUM(rs.wrong_count)) " +
                "ELSE 100 END";
        List<String> result = jdbcTemplate.query(
                "SELECT q.topic, SUM(rs.correct_count) AS c, SUM(rs.wrong_count) AS w, " +
                        accuracyExpr + " AS accuracy " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id " +
                        "WHERE rs.owner_key = ? " +
                        "GROUP BY q.topic " +
                        "HAVING (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 AND (" + accuracyExpr + ") < ? " +
                        "ORDER BY accuracy ASC LIMIT 1",
                (rs, rowNum) -> rs.getString("topic"),
                OwnerContext.current(),
                accuracyThreshold
        );
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Число тем с точностью ниже порога (есть хотя бы одна попытка).
     *
     * @param accuracyThreshold порог точности (например, 70.0)
     * @return число слабых тем
     */
    public long countWeakTopics(double accuracyThreshold) {
        String accuracyExpr = "CASE WHEN (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 " +
                "THEN CAST(SUM(rs.correct_count) AS REAL) * 100.0 / (SUM(rs.correct_count) + SUM(rs.wrong_count)) " +
                "ELSE 100 END";
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM (" +
                        "SELECT q.topic " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id " +
                        "WHERE rs.owner_key = ? " +
                        "GROUP BY q.topic " +
                        "HAVING (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 AND (" + accuracyExpr + ") < ?" +
                        ") weak",
                Long.class,
                OwnerContext.current(),
                accuracyThreshold
        );
        return count != null ? count : 0L;
    }

    /**
     * Определяет уровень сложности вопроса по статистике ответов.
     *
     * @param questionId идентификатор вопроса
     * @return уровень сложности
     */
    public QuestionDifficulty getDifficulty(long questionId) {
        List<QuestionDifficulty> result = jdbcTemplate.query(
                "SELECT COALESCE(rs.correct_count, 0) AS c, COALESCE(rs.wrong_count, 0) AS w " +
                        "FROM review_state rs WHERE rs.question_id = ? AND rs.owner_key = ?",
                (rs, rowNum) -> QuestionDifficulty.fromStats(rs.getInt("c"), rs.getInt("w")),
                questionId, OwnerContext.current());
        return result.isEmpty() ? QuestionDifficulty.NEW : result.get(0);
    }

    /**
     * Возвращает точность ответов по теме (для адаптивной сложности).
     *
     * @param topic тема
     * @return accuracy (0-100) или -1 если нет ответов
     */
    public double getTopicAccuracy(String topic) {
        List<Double> result = jdbcTemplate.query(
                "SELECT CASE WHEN (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 " +
                        "  THEN CAST(SUM(rs.correct_count) AS REAL) * 100.0 / (SUM(rs.correct_count) + SUM(rs.wrong_count)) " +
                        "  ELSE -1 END AS accuracy " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE q.topic = ? AND rs.owner_key = ?",
                (rs, rowNum) -> rs.getDouble("accuracy"),
                topic, OwnerContext.current());
        return result.isEmpty() ? -1 : result.get(0);
    }

    /**
     * Возвращает темы с числом вопросов ниже порога (для подсказки «банк вопросов бедный»).
     *
     * @param threshold порог (обычно 5)
     * @param limit     максимальное число тем
     */
    public List<TopicCoverage> findTopicCoverageGaps(int threshold, int limit) {
        return jdbcTemplate.query(
                "SELECT q.topic AS topic, COUNT(*) AS total " +
                        "FROM questions q " +
                        "GROUP BY q.topic HAVING COUNT(*) < ? " +
                        "ORDER BY total ASC, topic ASC LIMIT ?",
                (rs, rowNum) -> new TopicCoverage(rs.getString("topic"), rs.getLong("total")),
                threshold, limit);
    }

    public record TopicCoverage(String topic, long total) {}

    /**
     * Возвращает прогноз по дням на ближайшие N дней: сколько вопросов станет due в каждый день.
     *
     * @param nowEpoch epoch-секунды «сейчас»
     * @param days     горизонт (обычно 7)
     */
    public List<ForecastDay> findReviewForecast(long nowEpoch, int days) {
        long endEpoch = nowEpoch + (long) days * 86_400L;
        // next_review_at хранится как BIGINT (unix-epoch секунды).
        // to_timestamp(epoch)::date — конвертация epoch-секунд в дату для группировки по дням.
        return jdbcTemplate.query(
                "SELECT to_timestamp(next_review_at)::date AS day, COUNT(*) AS cnt " +
                        "FROM review_state " +
                        "WHERE owner_key = ? AND next_review_at >= ? AND next_review_at < ? " +
                        "GROUP BY day ORDER BY day",
                (rs, rowNum) -> new ForecastDay(rs.getString("day"), rs.getLong("cnt")),
                OwnerContext.current(), nowEpoch, endEpoch);
    }

    public record ForecastDay(String day, long count) {}

    /**
     * Вопросы с ошибками для drill-down вкладки «Ошибки» на /stats (UI-012).
     * Сортировка: последняя WRONG/UNKNOWN попытка (если есть), иначе по числу ошибок.
     *
     * @param limit максимум строк (обычно 30)
     */
    public List<WrongQuestionRow> findRecentWrongQuestions(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        String owner = OwnerContext.current();
        return jdbcTemplate.query(
                """
                        SELECT q.id AS question_id, q.topic, q.question_text,
                               rs.wrong_count, rs.correct_count, rs.next_review_at,
                               latest.created_at AS last_wrong_at
                        FROM questions q
                        JOIN review_state rs ON rs.question_id = q.id AND rs.owner_key = ?
                        LEFT JOIN LATERAL (
                            SELECT a.created_at
                            FROM attempt a
                            WHERE a.question_id = q.id
                              AND a.owner_key = ?
                              AND a.outcome IN ('WRONG', 'UNKNOWN')
                            ORDER BY a.created_at DESC, a.id DESC
                            LIMIT 1
                        ) latest ON true
                        WHERE rs.wrong_count > 0
                        ORDER BY COALESCE(latest.created_at, 0) DESC, rs.wrong_count DESC, q.id ASC
                        LIMIT ?
                        """,
                (rs, rowNum) -> new WrongQuestionRow(
                        rs.getLong("question_id"),
                        rs.getString("topic"),
                        rs.getString("question_text"),
                        rs.getInt("wrong_count"),
                        rs.getInt("correct_count"),
                        rs.getLong("next_review_at"),
                        rs.getObject("last_wrong_at") == null ? null : rs.getLong("last_wrong_at")),
                owner, owner, safeLimit);
    }

    /**
     * Строка drill-down ошибок: вопрос + счётчики + время последней ошибочной попытки.
     */
    public record WrongQuestionRow(
            long questionId,
            String topic,
            String questionText,
            int wrongCount,
            int correctCount,
            long nextReviewAt,
            Long lastWrongAt
    ) {}

    /**
     * Возвращает полный дамп прогресса для экспорта (JSON/CSV).
     */
    public List<ProgressExportRow> findProgressForExport() {
        return jdbcTemplate.query(
                "SELECT q.slug, q.topic, rs.correct_count, rs.wrong_count, rs.next_review_at, rs.repetitions " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id " +
                        "WHERE rs.owner_key = ? ORDER BY q.topic, q.slug",
                (rs, rowNum) -> new ProgressExportRow(
                        rs.getString("slug"), rs.getString("topic"),
                        rs.getInt("correct_count"), rs.getInt("wrong_count"),
                        rs.getLong("next_review_at"), rs.getInt("repetitions")),
                OwnerContext.current());
    }
}
