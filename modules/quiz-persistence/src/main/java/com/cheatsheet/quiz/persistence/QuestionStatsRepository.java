package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.domain.QuestionDifficulty;
import com.cheatsheet.quiz.domain.TopicMap;
import com.cheatsheet.quiz.domain.TopicStats;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для статистических запросов по вопросам и прогрессу обучения.
 *
 * <p>Выделен из {@link QuestionRepository} в отдельный класс
 * для соблюдения принципа единой ответственности (SRP).</p>
 *
 * <p><b>Единица счёта — вопрос, а не строка прогресса.</b> Запросы «сколько всего»,
 * «сколько к повтору», «сколько выучено» и разрез по темам идут от {@code questions}
 * через LEFT JOIN: у вопроса, на который ещё не отвечали, строки в
 * {@code review_state} нет, и INNER JOIN просто вычёркивал его из всех чисел. Каталог
 * тем на {@code /stats} из-за этого пустел до «нет данных» на полном банке вопросов, а
 * счётчик на главной показывал размер прогресса вместо размера банка.</p>
 *
 * <p>Исключение — метрики, которые по определению требуют ответов: точность темы,
 * слабые темы, разбор ошибок, прогноз расписания и экспорт прогресса. Там INNER JOIN
 * не потеря, а сама постановка вопроса.</p>
 *
 * @see InterviewFilterSql
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionStatsRepository {

    /**
     * Список колонок сводки (total / due / fresh / learned / correct_sum / wrong_sum).
     * Ждёт двух параметров в порядке следования: «сейчас» и порог освоения; сама
     * таблица прогресса подключается вызывающим через
     * {@link InterviewFilterSql#appendReviewStateJoin}.
     *
     * <p>Каждое обращение к {@code rs} обёрнуто в {@code COALESCE}, потому что JOIN
     * внешний: у вопроса без ответов колонки приходят как {@code NULL}, а
     * {@code NULL >= ?} в {@code CASE} уходит в {@code ELSE} — молча и без ошибки.
     * Срок повторения подставляется как «сейчас»: непройденный вопрос готов к показу,
     * и {@code due} обязан совпадать с тем, что вернёт выборка сессии.
     */
    private static final String AGGREGATE_COLUMNS =
            "SELECT COUNT(*) AS total, "
                    + "SUM(CASE WHEN COALESCE(rs.next_review_at, ?) <= ? THEN 1 ELSE 0 END) AS due, "
                    + "SUM(CASE WHEN rs.question_id IS NULL THEN 1 ELSE 0 END) AS fresh, "
                    + "SUM(CASE WHEN COALESCE(rs.repetitions, 0) >= ? THEN 1 ELSE 0 END) AS learned, "
                    + "COALESCE(SUM(rs.correct_count), 0) AS correct_sum, "
                    + "COALESCE(SUM(rs.wrong_count), 0) AS wrong_sum";

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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM questions q ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM questions q ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE ").append(InterviewFilterSql.READY_FOR_REVIEW).append(" ");
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM questions q ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE COALESCE(rs.repetitions, 0) >= ? ");
        params.add(minRepetitions);
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Суммирует количество правильных ответов по фильтру.
     */
    public long sumCorrect(String topic, Boolean important, Boolean onlyWrong) {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(rs.correct_count), 0) FROM questions q ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        Long value = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return value == null ? 0L : value;
    }

    /**
     * Суммирует количество неправильных ответов по фильтру.
     */
    public long sumWrong(String topic, Boolean important, Boolean onlyWrong) {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(rs.wrong_count), 0) FROM questions q ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
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
        StringBuilder sql = new StringBuilder(AGGREGATE_COLUMNS + " FROM questions q ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        params.add(nowEpoch);
        params.add(minRepetitions);
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);

        List<InterviewStats> result = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new InterviewStats(
                rs.getLong("total"),
                rs.getLong("due"),
                rs.getLong("fresh"),
                rs.getLong("learned"),
                rs.getLong("correct_sum"),
                rs.getLong("wrong_sum")
        ), params.toArray());
        return result.isEmpty() ? new InterviewStats(0, 0, 0, 0, 0, 0) : result.get(0);
    }

    /**
     * Агрегат карточек и расписания (глобально, без фильтра) для
     * {@link com.cheatsheet.quiz.domain.LearningMetrics}. Разводит {@code dueNow}
     * (готовы к показу сейчас) и {@code overdue} (просрочены — запланированы раньше
     * {@code overdueCutoffEpoch}, обычно now − сутки).
     *
     * <p>Считается от {@code questions}, а не от {@code review_state}:
     * {@code cardsTotal} — размер каталога, и рядом с ним живёт {@code cardsSeen}
     * («хотя бы одна попытка»). Если бы {@code cardsTotal} считался по строкам
     * прогресса, обе метрики означали бы одно и то же и их отношение всегда
     * равнялось бы 100%.
     *
     * <p>{@code overdue} — единственная величина здесь без {@code COALESCE}, и это
     * не пропуск: просрочить можно только назначенный срок. У вопроса, который ни
     * разу не показывали, срока не было — он новый, а не пропущенный.
     *
     * @param nowEpoch          текущее время (epoch-секунды)
     * @param overdueCutoffEpoch граница «просрочки» (обычно now − 86400)
     * @param minRepetitions    порог «выучено»
     */
    public ScheduleAggregate getScheduleAggregate(long nowEpoch, long overdueCutoffEpoch, int minRepetitions) {
        List<ScheduleAggregate> rows = jdbcTemplate.query(
                "SELECT COUNT(*) AS cards_total, "
                        + "COALESCE(SUM(CASE WHEN COALESCE(rs.repetitions, 0) >= ? THEN 1 ELSE 0 END), 0) "
                        + "AS cards_mastered, "
                        + "COALESCE(SUM(CASE WHEN COALESCE(rs.next_review_at, ?) <= ? THEN 1 ELSE 0 END), 0) "
                        + "AS due_now, "
                        + "COALESCE(SUM(CASE WHEN rs.next_review_at <= ? THEN 1 ELSE 0 END), 0) AS overdue, "
                        + "COALESCE(SUM(CASE WHEN rs.question_id IS NULL THEN 1 ELSE 0 END), 0) AS due_fresh "
                        + "FROM questions q "
                        + "LEFT JOIN review_state rs ON rs.question_id = q.id AND rs.owner_key = ?",
                (rs, rowNum) -> new ScheduleAggregate(
                        rs.getLong("cards_total"),
                        rs.getLong("cards_mastered"),
                        rs.getLong("due_now"),
                        rs.getLong("overdue"),
                        rs.getLong("due_fresh")),
                minRepetitions, nowEpoch, nowEpoch, overdueCutoffEpoch, OwnerContext.current());
        return rows.isEmpty() ? ScheduleAggregate.EMPTY : rows.get(0);
    }

    /**
     * Агрегат карточек и расписания. {@code dueFresh} — часть {@code dueNow},
     * у которой ещё нет расписания: вопрос готов к показу не потому, что подошёл
     * срок, а потому что его ни разу не открывали.
     */
    public record ScheduleAggregate(long cardsTotal, long cardsMastered, long dueNow, long overdue,
                                    long dueFresh) {
        public static final ScheduleAggregate EMPTY = new ScheduleAggregate(0, 0, 0, 0, 0);
    }

    /**
     * Возвращает агрегированную статистику по списку тем.
     */
    public InterviewStats getAggregatedStatsByTopics(List<String> topics, Boolean important, Boolean onlyWrong,
                                                     long nowEpoch, int minRepetitions) {
        StringBuilder sql = new StringBuilder(AGGREGATE_COLUMNS + " FROM questions q ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        params.add(nowEpoch);
        params.add(minRepetitions);
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);

        List<InterviewStats> result = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new InterviewStats(
                rs.getLong("total"),
                rs.getLong("due"),
                rs.getLong("fresh"),
                rs.getLong("learned"),
                rs.getLong("correct_sum"),
                rs.getLong("wrong_sum")
        ), params.toArray());
        return result.isEmpty() ? new InterviewStats(0, 0, 0, 0, 0, 0) : result.get(0);
    }

    private static final String TOPIC_STATS_COLUMNS =
            "SELECT q.topic AS topic, COUNT(*) AS total, "
                    + "SUM(CASE WHEN COALESCE(rs.next_review_at, ?) <= ? THEN 1 ELSE 0 END) AS due, "
                    + "SUM(CASE WHEN rs.question_id IS NULL THEN 1 ELSE 0 END) AS fresh, "
                    + "SUM(CASE WHEN COALESCE(rs.repetitions, 0) >= ? THEN 1 ELSE 0 END) AS learned, "
                    + "COALESCE(SUM(rs.correct_count), 0) AS correct, "
                    + "COALESCE(SUM(rs.wrong_count), 0) AS wrong, "
                    // COALESCE внутри AVG, а не снаружи: снаружи среднее считалось бы
                    // только по отвеченным вопросам, и тема, где выучен один вопрос из ста,
                    // выглядела бы зрелой. Непройденный вопрос — это зрелость 0, а не
                    // «строка не участвует».
                    + "AVG(COALESCE(rs.ease_factor, 0) * COALESCE(rs.interval_days, 0)) AS maturity_score "
                    + "FROM questions q ";

    private static final String TOPIC_STATS_TAIL = " GROUP BY q.topic ORDER BY q.topic";

    private static final RowMapper<TopicStats> TOPIC_STATS_MAPPER = (rs, rowNum) -> new TopicStats(
            rs.getString("topic"), rs.getLong("total"),
            rs.getLong("due"), rs.getLong("fresh"), rs.getLong("learned"),
            rs.getLong("correct"), rs.getLong("wrong"),
            rs.getDouble("maturity_score"));

    /**
     * Возвращает статистику по каждой теме полного банка (REST API `/api/stats/topics`).
     *
     * @param nowEpoch         текущее время (для подсчёта due)
     * @param learnedThreshold порог повторений для «выучено»
     */
    public List<TopicStats> findTopicStats(long nowEpoch, int learnedThreshold) {
        return jdbcTemplate.query(
                TOPIC_STATS_COLUMNS
                        + "LEFT JOIN review_state rs ON rs.question_id = q.id AND rs.owner_key = ? "
                        + TOPIC_STATS_TAIL,
                TOPIC_STATS_MAPPER,
                nowEpoch, nowEpoch, learnedThreshold, OwnerContext.current());
    }

    /**
     * Та же статистика по темам, но по <b>выборке</b>, а не по всему банку: те же
     * предикаты, которыми {@link #getAggregatedStatsByTopics} считает сводку страницы
     * `/stats`. Одна выборка — один набор условий: иначе сводка наверху говорила бы про
     * отобранное, а таблица под ней — про весь банк (замер iter-73: 319 строк при любом
     * отборе, включая `?topic=…`).
     *
     * <p>Строки с нулём вопросов не возникают: {@code GROUP BY} строит их из самих
     * вопросов, а не из списка тем, поэтому тема, у которой под отбор не подошёл ни один
     * вопрос, просто не попадает в результат — это и есть честный пустой срез.
     *
     * @param topics           темы выборки (пустой список → пустой результат)
     * @param important        только избранные (null/false — не фильтровать)
     * @param onlyWrong        только с ошибками (null/false — не фильтровать)
     * @param nowEpoch         текущее время (для подсчёта due)
     * @param learnedThreshold порог повторений для «выучено»
     */
    public List<TopicStats> findTopicStatsByTopics(List<String> topics, Boolean important, Boolean onlyWrong,
                                                   long nowEpoch, int learnedThreshold) {
        StringBuilder sql = new StringBuilder(TOPIC_STATS_COLUMNS);
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        params.add(nowEpoch);
        params.add(learnedThreshold);
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(TOPIC_STATS_TAIL);
        return jdbcTemplate.query(sql.toString(), TOPIC_STATS_MAPPER, params.toArray());
    }

    /**
     * Состояния вопросов ОДНОЙ темы — по одному значению на вопрос, в порядке банка.
     *
     * <p>Нужно карте темы на экране вопроса: она отвечает на «где я внутри этой темы»
     * не числом, а формой — сколько клеток уже закрыто, сколько ждёт повторения,
     * сколько не тронуто. Числа рядом говорят то же прозой (WCAG 1.4.1).</p>
     *
     * <p>Один запрос на рендер и только по текущей теме: полный
     * {@code findTopicStats} читает все 321 тему, и звать его ради одной строки на
     * каждом вопросе значило бы платить за банк целиком.</p>
     *
     * @param topic            тема как она хранится в {@code questions.topic}
     * @param nowEpoch         момент отсчёта срока повторения
     * @param learnedThreshold число повторений, с которого вопрос считается освоенным
     * @param limit            потолок числа клеток; тема крупнее просто обрезается
     */
    public List<TopicMap.QuestionProgress> findTopicQuestionStates(String topic, long nowEpoch,
                                                                   int learnedThreshold, int limit) {
        if (topic == null || topic.isBlank() || limit <= 0) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT CASE "
                        + "WHEN rs.question_id IS NULL THEN 'FRESH' "
                        + "WHEN COALESCE(rs.repetitions, 0) >= ? THEN 'LEARNED' "
                        + "WHEN COALESCE(rs.next_review_at, ?) <= ? THEN 'DUE' "
                        + "ELSE 'IN_PROGRESS' END AS state FROM questions q ");
        var params = new ArrayList<Object>();
        params.add(learnedThreshold);
        params.add(nowEpoch);
        params.add(nowEpoch);
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE q.topic = ? ORDER BY q.id LIMIT ?");
        params.add(topic);
        params.add(limit);
        return jdbcTemplate.query(sql.toString(),
                (rs, rowNum) -> TopicMap.QuestionProgress.valueOf(rs.getString("state")), params.toArray());
    }


    /**
     * Находит тему с наименьшей точностью (accuracy &lt; threshold) среди тем с ответами.
     *
     * <p>Это ЕДИНСТВЕННОЕ определение «самой слабой темы» в продукте: по нему и режим
     * «Слабые темы» выбирает вопрос, и карточка на /stats рисует подпись со ссылкой.
     * Разъехавшись, они обещали бы одну тему, а тренировали другую.
     *
     * <p>Тай-брейк детерминированный: при равной точности сильнее сигнал там, где
     * больше попыток, а при равенстве и попыток — по имени темы. Без него СУБД вправе
     * вернуть любую из равных строк, и подпись карточки менялась бы от перезагрузки.
     *
     * @param accuracyThreshold порог точности (например, 70.0)
     * @return тема с её точностью или null, если слабых тем нет
     */
    public WeakestTopic findWeakestTopic(double accuracyThreshold) {
        return findWeakestTopic(null, null, null, accuracyThreshold);
    }

    /**
     * То же правило «самой слабой темы», но применённое к выборке, а не ко всему банку.
     *
     * <p>Нужно /stats: страница объявляет «Источник: выбранная часть банка», и карточка
     * «Тренировать слабые темы» обязана называть тему ИЗ этой выборки. До iter-73 она
     * звала глобально слабейшую — при отборе по одной теме подпись рекомендовала чужую
     * тему, а ссылка вела прочь из выборки, которую человек только что набрал.
     *
     * <p>Правило и тай-брейк те же, что в {@link #findWeakestTopic(double)}: оба метода
     * строят один и тот же SQL, отличаясь только предикатами выборки. Второй формулировки
     * «слабой темы» в продукте нет и заводить нельзя — режим «Слабые темы» сверяется с
     * этим же определением.
     *
     * @param topics список тем выборки; {@code null} — не ограничивать (весь банк)
     */
    public WeakestTopic findWeakestTopic(List<String> topics, Boolean important, Boolean onlyWrong,
                                         double accuracyThreshold) {
        var params = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("SELECT q.topic, ")
                .append(WEAK_ATTEMPTS_EXPR).append(" AS attempts, ")
                .append(WEAK_ACCURACY_EXPR).append(" AS accuracy FROM questions q ");
        appendWeakTopicScope(sql, params, topics, important, onlyWrong);
        appendWeakTopicHaving(sql, params, accuracyThreshold);
        sql.append(" ORDER BY accuracy ASC, attempts DESC, q.topic ASC LIMIT 1");
        List<WeakestTopic> result = jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new WeakestTopic(
                        rs.getString("topic"),
                        (int) Math.round(rs.getDouble("accuracy"))),
                params.toArray());
        return result.isEmpty() ? null : result.get(0);
    }

    private static final String WEAK_ATTEMPTS_EXPR =
            "(SUM(rs.correct_count) + SUM(rs.wrong_count))";

    private static final String WEAK_ACCURACY_EXPR =
            "CASE WHEN " + WEAK_ATTEMPTS_EXPR + " > 0 "
                    + "THEN CAST(SUM(rs.correct_count) AS REAL) * 100.0 / " + WEAK_ATTEMPTS_EXPR + " "
                    + "ELSE 100 END";

    /**
     * Общая часть обоих запросов о слабых темах: JOIN прогресса и предикаты выборки.
     *
     * <p>JOIN здесь LEFT (через {@link InterviewFilterSql#appendReviewStateJoin}), а не
     * INNER, как было до iter-73. Результат тот же: тему без единого ответа отсекает
     * {@code HAVING attempts &gt; 0} — сумма по отсутствующим строкам даёт NULL, а не 0.
     * Но фильтры выборки (в частности «только ошибки») читают {@code rs} и обязаны
     * получать его по тем же правилам, что и остальная страница; INNER JOIN тихо менял
     * бы смысл фильтра «только ошибки» на «есть прогресс И есть ошибки».
     */
    private static void appendWeakTopicScope(StringBuilder sql, List<Object> params, List<String> topics,
                                             Boolean important, Boolean onlyWrong) {
        InterviewFilterSql.appendReviewStateJoin(sql, params);
        sql.append(" WHERE 1=1 ");
        if (topics != null) {
            InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        }
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" GROUP BY q.topic ");
    }

    private static void appendWeakTopicHaving(StringBuilder sql, List<Object> params, double accuracyThreshold) {
        sql.append("HAVING ").append(WEAK_ATTEMPTS_EXPR).append(" > 0 AND (")
                .append(WEAK_ACCURACY_EXPR).append(") < ? ");
        params.add(accuracyThreshold);
    }

    /**
     * Самая слабая тема: ключ темы (он же параметр ссылки на тренировку) и округлённая
     * точность в процентах — ровно то, что показывает подпись карточки на /stats.
     */
    public record WeakestTopic(String topic, int accuracyPercent) {}

    /**
     * Число тем с точностью ниже порога (есть хотя бы одна попытка).
     *
     * @param accuracyThreshold порог точности (например, 70.0)
     * @return число слабых тем
     */
    public long countWeakTopics(double accuracyThreshold) {
        return countWeakTopics(null, null, null, accuracyThreshold);
    }

    /**
     * Число слабых тем в выборке — счётчик той же карточки, что и
     * {@link #findWeakestTopic(List, Boolean, Boolean, double)}, и по тому же правилу.
     *
     * @param topics список тем выборки; {@code null} — не ограничивать (весь банк)
     */
    public long countWeakTopics(List<String> topics, Boolean important, Boolean onlyWrong,
                                double accuracyThreshold) {
        var params = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM (SELECT q.topic FROM questions q ");
        appendWeakTopicScope(sql, params, topics, important, onlyWrong);
        appendWeakTopicHaving(sql, params, accuracyThreshold);
        sql.append(") weak");
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
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
     * Возвращает темы с числом вопросов ниже порога (для подсказки «банк вопросов бедный»)
     * — по выборке страницы, а не по всему банку. Строка этой таблицы — тема, поэтому она
     * обязана подчиняться тому же отбору, что и «Детализация по темам» рядом: отобрав
     * раздел, пользователь спрашивает про пробелы этого раздела.
     *
     * @param topics    темы выборки (пустой список → пустой результат)
     * @param threshold порог (обычно 5)
     * @param limit     максимальное число тем
     */
    public List<TopicCoverage> findTopicCoverageGaps(List<String> topics, int threshold, int limit) {
        StringBuilder sql = new StringBuilder("SELECT q.topic AS topic, COUNT(*) AS total FROM questions q WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        sql.append("GROUP BY q.topic HAVING COUNT(*) < ? ORDER BY total ASC, topic ASC LIMIT ?");
        params.add(threshold);
        params.add(limit);
        return jdbcTemplate.query(sql.toString(),
                (rs, rowNum) -> new TopicCoverage(rs.getString("topic"), rs.getLong("total")),
                params.toArray());
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
     * <p>Отбор страницы сюда доходит списком тем: карточка ошибки подписана темой и ведёт
     * в тему, поэтому при отобранном разделе список обязан говорить про этот раздел.
     * Фильтр «только избранные» приходит отдельным флагом; «только ошибки» здесь не нужен —
     * условие {@code rs.wrong_count > 0} и есть этот фильтр, всегда включённый.
     *
     * @param topics    темы выборки (пустой список → пустой результат)
     * @param important только избранные (null/false — не фильтровать)
     * @param limit     максимум строк (обычно 30)
     */
    public List<WrongQuestionRow> findRecentWrongQuestions(List<String> topics, Boolean important, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        String owner = OwnerContext.current();
        StringBuilder sql = new StringBuilder("""
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
                """);
        var params = new ArrayList<Object>();
        params.add(owner);
        params.add(owner);
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, null);
        sql.append(" ORDER BY COALESCE(latest.created_at, 0) DESC, rs.wrong_count DESC, q.id ASC LIMIT ?");
        params.add(safeLimit);
        return jdbcTemplate.query(sql.toString(),
                (rs, rowNum) -> new WrongQuestionRow(
                        rs.getLong("question_id"),
                        rs.getString("topic"),
                        rs.getString("question_text"),
                        rs.getInt("wrong_count"),
                        rs.getInt("correct_count"),
                        rs.getLong("next_review_at"),
                        rs.getObject("last_wrong_at") == null ? null : rs.getLong("last_wrong_at")),
                params.toArray());
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
