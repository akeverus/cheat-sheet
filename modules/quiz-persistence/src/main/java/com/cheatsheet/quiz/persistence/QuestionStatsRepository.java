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
                        "GROUP BY q.topic ORDER BY q.topic",
                (rs, rowNum) -> new TopicStats(
                        rs.getString("topic"), rs.getLong("total"),
                        rs.getLong("due"), rs.getLong("learned"),
                        rs.getLong("correct"), rs.getLong("wrong"),
                        rs.getLong("regen_sum"),
                        rs.getDouble("maturity_score")),
                nowEpoch, learnedThreshold);
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
                        "GROUP BY q.topic " +
                        "HAVING (SUM(rs.correct_count) + SUM(rs.wrong_count)) > 0 AND (" + accuracyExpr + ") < ? " +
                        "ORDER BY accuracy ASC LIMIT 1",
                (rs, rowNum) -> rs.getString("topic"),
                accuracyThreshold
        );
        return result.isEmpty() ? null : result.get(0);
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
                        "FROM review_state rs WHERE rs.question_id = ?",
                (rs, rowNum) -> QuestionDifficulty.fromStats(rs.getInt("c"), rs.getInt("w")),
                questionId);
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
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE q.topic = ?",
                (rs, rowNum) -> rs.getDouble("accuracy"),
                topic);
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
        return jdbcTemplate.query(
                "SELECT DATE(next_review_at, 'unixepoch') AS day, COUNT(*) AS cnt " +
                        "FROM review_state " +
                        "WHERE next_review_at >= ? AND next_review_at < ? " +
                        "GROUP BY day ORDER BY day",
                (rs, rowNum) -> new ForecastDay(rs.getString("day"), rs.getLong("cnt")),
                nowEpoch, endEpoch);
    }

    public record ForecastDay(String day, long count) {}

    /**
     * Возвращает полный дамп прогресса для экспорта (JSON/CSV).
     */
    public List<ProgressExportRow> findProgressForExport() {
        return jdbcTemplate.query(
                "SELECT q.slug, q.topic, rs.correct_count, rs.wrong_count, rs.next_review_at, rs.repetitions " +
                        "FROM questions q JOIN review_state rs ON rs.question_id = q.id ORDER BY q.topic, q.slug",
                (rs, rowNum) -> new ProgressExportRow(
                        rs.getString("slug"), rs.getString("topic"),
                        rs.getInt("correct_count"), rs.getInt("wrong_count"),
                        rs.getLong("next_review_at"), rs.getInt("repetitions")));
    }
}
