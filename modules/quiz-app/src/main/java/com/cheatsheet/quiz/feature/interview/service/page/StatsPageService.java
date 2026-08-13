package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.domain.Progression;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.progress.ExperienceService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.MetricDefinitions;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Сервис подготовки модели страницы статистики (`stats`).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsPageService {
    private static final int COVERAGE_GAP_THRESHOLD = 5;
    private static final int COVERAGE_GAP_LIMIT = 10;
    private static final int FORECAST_DAYS = 7;
    private static final int WRONG_DRILLDOWN_LIMIT = 30;
    private static final int ACCURACY_TREND_DAYS = 14;
    private static final int QUESTION_PREVIEW_MAX = 160;

    /**
     * Сколько строк таблицы тем сервер отдаёт без явной просьбы показать всё.
     *
     * <p>12 — то же число, что стояло в снятом {@code data-collapse-rows}: окно,
     * дочитываемое без прокрутки рядом с графиком точности над ним. Полный список
     * (319 строк, 600 КБ разметки) остаётся доступен по ссылке {@code ?rows=all} —
     * страница больше не платит за него по умолчанию.</p>
     */
    static final int TOPIC_TABLE_WINDOW = 12;

    InterviewFacade facade;
    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;
    SearchService searchService;
    ObjectMapper objectMapper;
    QuestionStatsRepository questionStatsRepository;
    LearningMetricsService learningMetricsService;
    TopicUtils topicUtils;
    DailyActivityRepository dailyActivityRepository;
    ExperienceService experienceService;
    com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService sessionHistoryService;

    /**
     * Строит state страницы статистики по фильтру и поисковому запросу.
     */
    public StatsPageState build(InterviewFilter filter, String query, int searchLimit) {
        return build(filter, query, searchLimit, TopicTableQuery.empty());
    }

    /**
     * Строит state страницы статистики по фильтру, поисковому запросу и запросу таблицы тем.
     */
    public StatsPageState build(InterviewFilter filter, String query, int searchLimit, TopicTableQuery tableQuery) {
        InterviewStats stats = facade.getStats(filter);
        List<String> availableTopics = questionRepository.findTopics();
        String selectedGroup = topicCatalogService.normalizeGroup(filter.group());
        List<String> topics = topicCatalogService.filterAndSortTopics(availableTopics, selectedGroup, filter.isOrdered());
        // Все срезы по темам считаются по ОДНОЙ выборке — той же, по которой посчитана
        // сводка «Что делать дальше» наверху. Раньше здесь стоял `getTopicStats()` по
        // полному банку: при `?group=…`, `?topic=…`, `?important=true` и любой их
        // комбинации таблица «Детализация по темам» отдавала одни и те же 319 строк
        // (замер iter-73), то есть страница показывала отбор в шапке и весь банк под ней.
        List<String> selectedTopics = facade.topicsForFilter(filter);
        List<TopicStats> topicStats = facade.getTopicStats(filter);
        // C35: JSON встраивается literal-ом в <script id="topic-stats-data"> (stats.html,
        // через [(...)] без HTML-escape). Jackson по умолчанию НЕ экранирует '<' и '/',
        // поэтому имя темы с подстрокой "</script>" закрыло бы тег и пробило бы в HTML-
        // контекст. Экранируем юникод-эскейпами (OWASP JSON-in-HTML) — см. escapeForHtmlScript.
        String topicStatsJson = writeJsonForHtml(toChartPayload(topicStats),
                "stats_page_topic_stats_json_failed size={}", topicStats.size());
        log.info("stats_page_state_built topic={} group={} searchQueryPresent={}",
                filter.topic(), selectedGroup, query != null && !query.isBlank());
        List<QuestionStatsRepository.TopicCoverage> coverageGaps =
                questionStatsRepository.findTopicCoverageGaps(selectedTopics, COVERAGE_GAP_THRESHOLD, COVERAGE_GAP_LIMIT);
        long nowEpoch = System.currentTimeMillis() / 1000L;
        List<QuestionStatsRepository.ForecastDay> forecast =
                densifyForecast(questionStatsRepository.findReviewForecast(nowEpoch, FORECAST_DAYS));
        LearningMetrics metrics = learningMetricsService.compute();
        List<DifficultySlice> difficultyDistribution = buildDifficultyDistribution();
        String difficultyJson = writeJsonForHtml(difficultyDistribution, "stats_page_difficulty_json_failed");
        List<WrongQuestionItem> recentWrongQuestions = questionStatsRepository
                .findRecentWrongQuestions(selectedTopics, filter.importantOnly(), WRONG_DRILLDOWN_LIMIT)
                .stream()
                .map(this::toWrongQuestionItem)
                .toList();
        Progression progression = Progression.of(
                experienceService.snapshot(), experienceService.currentStreak());
        List<AccuracyTrendDay> accuracyTrend = buildAccuracyTrend(
                dailyActivityRepository.findRecentDays(ACCURACY_TREND_DAYS));
        String accuracyTrendJson = writeJsonForHtml(accuracyTrend, "stats_page_accuracy_trend_json_failed");
        return new StatsPageState(
                stats,
                topics,
                topicCatalogService.groupOptions(availableTopics),
                selectedGroup,
                filter,
                query,
                searchService.search(query, searchLimit),
                topicStats,
                buildTopicTable(topicStats, tableQuery, filter, query),
                topicStatsJson,
                coverageGaps,
                forecast,
                metrics,
                difficultyDistribution,
                difficultyJson,
                MetricDefinitions.catalog(),
                // Слабую тему и их число считает репозиторий — тем же правилом и тем же
                // порогом, по которому режим «Слабые темы» выбирает вопрос. Иначе
                // карточка обещала бы одну тему, а тренировка давала другую.
                //
                // Считаем по ВЫБОРКЕ (iter-73, FRD-025). Глобальный вариант при отборе по
                // одной теме подписывал карточку чужой темой, а ссылка «Тренировать слабые
                // темы» уводила из выборки, которую человек только что набрал: сводка
                // объявляет себя выборкой, значит и рекомендация обязана лежать внутри неё.
                questionStatsRepository.findWeakestTopic(
                        selectedTopics, filter.importantOnly(), filter.onlyWrong(), QuizConstants.WEAK_TOPIC_THRESHOLD),
                questionStatsRepository.countWeakTopics(
                        selectedTopics, filter.importantOnly(), filter.onlyWrong(), QuizConstants.WEAK_TOPIC_THRESHOLD),
                recentWrongQuestions,
                accuracyTrend,
                accuracyTrendJson,
                // Каталог считается по ПОЛНОМУ банку и полной статистике, а не по
                // отфильтрованному списку тем: он и есть навигация по банку.
                // Свернувшись до одного выбранного раздела, он перестал бы
                // отвечать на вопрос «куда пойти дальше» — ровно тот, ради
                // которого существует.
                //
                // Вторым аргументом идёт ОТДЕЛЬНЫЙ полный срез, а не `topicStats`: с
                // iter-73 тот считается по выборке, и, попав сюда, обнулил бы прогресс
                // всех невыбранных разделов — каталог показывал бы «0 из N» ровно там,
                // куда и предлагает пойти.
                topicCatalogService.groupCatalog(questionRepository.countQuestionsByTopic(), facade.getTopicStats()),
                // Прогрессия НЕ зависит от отбора: ступень — про человека, а не про срез
                // банка, который он сейчас смотрит. Считать её по выборке значило бы, что
                // фильтр «только важные» понижает человека в ранге.
                progression,
                // История не зависит от отбора по темам: сессия — это событие, а не срез
                // банка. Фильтровать её значило бы прятать занятия, которые были.
                sessionHistoryService.recent(),
                // Ни одного нового запроса: и метрики, и прогрессия уже посчитаны выше.
                com.cheatsheet.quiz.domain.Achievement.evaluate(metrics, progression)
        );
    }

    /**
     * Готовит таблицу «Детализация по темам»: порядок строк и их количество.
     *
     * <p>До iter-81 и то и другое решал браузер уже ПОСЛЕ отрисовки: сервер отдавал
     * все строки (замер: 319 строк, 600 906 байт разметки — 73% страницы), а
     * инлайновый скрипт за таблицей вешал {@code hidden} на всё кроме первых 12.
     * Комментарий у скрипта обещал «сворачивает до первой отрисовки (без FOUC)», но
     * это была гонка парсинга с первым кадром, и разные движки решали её по-разному
     * (замер iter-81, 1440×900, DPR 2):</p>
     * <ul>
     *   <li>WebKit — гонку проигрывал стабильно (4 запуска из 5): на FCP (238–255 мс)
     *       в документе стояли ВСЕ 319 строк, высота 21 957px, и уже после кадра
     *       схлопывалась до 3939px — падение в 5.6 раза;</li>
     *   <li>Chrome — выигрывал: на FCP видимых строк 12, CLS 0 (0.027 при CPU×6).</li>
     * </ul>
     *
     * <p>По §11 приёма, который работает только в Chromium, в продукте не остаётся.
     * Теперь объём и порядок — состояние URL и ответственность сервера, ровно как на
     * {@code /bookmarks} (FRD-024): одна форма — один GET. Первый кадр верен в любом
     * движке по построению, потому что верна сама разметка.</p>
     *
     * <p>Сортировка тоже переехала сюда не только ради кадра. Клиентская сравнивала
     * {@code textContent} ячеек через {@code parseFloat}: у темы без ответов там стоит
     * «—», {@code parseFloat} даёт NaN, и такие темы вставали в произвольные места —
     * то есть «сначала слабые» врало ровно про те темы, где данных нет. Здесь «нет
     * данных» — это {@code null}, и он уходит в конец при ЛЮБОМ направлении: у темы
     * без ответов нет ни лучшей, ни худшей точности.</p>
     */
    private TopicTableView buildTopicTable(List<TopicStats> topicStats, TopicTableQuery query,
                                           InterviewFilter filter, String searchQuery) {
        TopicTableQuery request = (query == null ? TopicTableQuery.empty() : query).normalized();
        List<TopicStats> sorted = new ArrayList<>(topicStats);
        sorted.sort(comparator(request));
        int total = sorted.size();
        List<TopicStats> rows = request.all() || total <= TOPIC_TABLE_WINDOW
                ? sorted
                : sorted.subList(0, TOPIC_TABLE_WINDOW);
        return new TopicTableView(List.copyOf(rows), request, total, total - rows.size(),
                keepFilterQuery(filter, searchQuery));
    }

    /**
     * Параметры отбора, которые обязана донести любая ссылка сортировки.
     *
     * <p>Собирается здесь, а не в разметке: {@code th:href="@{/stats(topic=…)}"}
     * печатает параметр и когда значение пустое, поэтому шаблонная версия давала
     * {@code /stats?topic=&group=&important=&onlyWrong=&q=&sort=name…} — шесть
     * пустых пар в КАЖДОЙ ссылке заголовка. Рядом на той же странице чипы разделов
     * ведут на чистое {@code /stats?group=foundation}, так что мусор был бы виден
     * ровно в тот момент, ради которого состояние и переехало в URL: при копировании
     * ссылки.</p>
     */
    private static String keepFilterQuery(InterviewFilter filter, String searchQuery) {
        StringBuilder sb = new StringBuilder();
        if (filter != null) {
            appendParam(sb, "topic", filter.topic());
            appendParam(sb, "group", filter.group());
            if (Boolean.TRUE.equals(filter.importantOnly())) {
                appendParam(sb, "important", "true");
            }
            if (Boolean.TRUE.equals(filter.onlyWrong())) {
                appendParam(sb, "onlyWrong", "true");
            }
            if (Boolean.FALSE.equals(filter.ordered())) {
                appendParam(sb, "ordered", "false");
            }
        }
        appendParam(sb, "q", searchQuery);
        return sb.toString();
    }

    private static void appendParam(StringBuilder sb, String name, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append('&');
        }
        sb.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    private Comparator<TopicStats> comparator(TopicTableQuery request) {
        boolean desc = TopicTableQuery.DIR_DESC.equals(request.dir());
        Comparator<TopicStats> byName = Comparator.comparing(
                ts -> topicUtils.displayName(ts.topic()), String.CASE_INSENSITIVE_ORDER);
        Comparator<TopicStats> primary = switch (request.sort()) {
            case TopicTableQuery.SORT_TOTAL -> numeric(ts -> (double) ts.total(), desc);
            case TopicTableQuery.SORT_ACCURACY -> numeric(StatsPageService::accuracyOrNull, desc);
            case TopicTableQuery.SORT_MATURITY -> numeric(StatsPageService::maturityOrNull, desc);
            default -> desc ? byName.reversed() : byName;
        };
        // Добор по имени всегда по возрастанию: у 319 тем значения массово совпадают
        // (все «0 из N», вся точность «—»), и без стабильного второго ключа порядок
        // строк менялся бы от запроса к запросу при тех же данных.
        return TopicTableQuery.SORT_NAME.equals(request.sort()) ? primary : primary.thenComparing(byName);
    }

    /**
     * Сравнение по числовой колонке: направление применяется к значениям, а «нет
     * данных» ({@code null}) уходит в конец независимо от направления.
     */
    private static Comparator<TopicStats> numeric(java.util.function.Function<TopicStats, Double> value, boolean desc) {
        Comparator<Double> values = desc ? Comparator.<Double>reverseOrder() : Comparator.<Double>naturalOrder();
        return Comparator.comparing(value, Comparator.nullsLast(values));
    }

    /** Точность темы в процентах или {@code null}, если по теме нет ни одного ответа. */
    private static Double accuracyOrNull(TopicStats ts) {
        long answered = ts.correct() + ts.wrong();
        return answered == 0 ? null : ts.correct() * 100.0 / answered;
    }

    /** Зрелость темы или {@code null}, если она нулевая — то есть замерять нечего. */
    private static Double maturityOrNull(TopicStats ts) {
        return ts.maturityScore() == 0 ? null : ts.maturityScore();
    }

    /** Порядок и русские подписи уровней сложности для доната (Этап 6). */
    private static final Map<String, String> DIFFICULTY_LABELS = new LinkedHashMap<>() {{
        put("EASY", "Лёгкие");
        put("MEDIUM", "Средние");
        put("HARD", "Сложные");
    }};

    /**
     * Нормализует сырое распределение сложности в фиксированный порядок
     * EASY→MEDIUM→HARD с русскими подписями и процентами. Неизвестные/пустые уровни
     * отбрасываются; проценты считаются от суммы известных. Пустой результат
     * (нет данных) → шаблон не рендерит донат.
     */
    private List<DifficultySlice> buildDifficultyDistribution() {
        Map<String, Long> byLevel = new LinkedHashMap<>();
        for (QuestionStatsRepository.DifficultyBucket bucket : questionStatsRepository.findDifficultyDistribution()) {
            if (bucket.difficulty() == null) {
                continue;
            }
            String level = bucket.difficulty().toUpperCase(Locale.ROOT);
            if (DIFFICULTY_LABELS.containsKey(level)) {
                byLevel.merge(level, bucket.count(), Long::sum);
            }
        }
        long total = byLevel.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            return List.of();
        }
        // Один уровень на весь банк — не распределение, а константа: донат из
        // единственной 100%-доли не несёт информации, но выглядит «распределением»,
        // которого нет (сейчас все вопросы — MEDIUM). Скрываем так же, как пустой
        // случай выше — честная аналитика не рисует мнимое распределение. Как только
        // появится реальная вариативность (≥2 уровня с данными), донат вернётся сам.
        long nonZeroLevels = byLevel.values().stream().filter(count -> count > 0).count();
        if (nonZeroLevels <= 1) {
            return List.of();
        }
        List<DifficultySlice> slices = new ArrayList<>();
        for (Map.Entry<String, String> entry : DIFFICULTY_LABELS.entrySet()) {
            long count = byLevel.getOrDefault(entry.getKey(), 0L);
            double percent = count * 100.0 / total;
            slices.add(new DifficultySlice(entry.getKey(), entry.getValue(), count, percent));
        }
        return slices;
    }

    private WrongQuestionItem toWrongQuestionItem(QuestionStatsRepository.WrongQuestionRow row) {
        return new WrongQuestionItem(
                row.questionId(),
                row.topic(),
                topicUtils.displayName(row.topic()),
                truncatePreview(row.questionText()),
                row.wrongCount(),
                row.correctCount(),
                row.nextReviewAt(),
                row.lastWrongAt());
    }

    private static String truncatePreview(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = text.replaceAll("\\s+", " ").strip();
        if (normalized.length() <= QUESTION_PREVIEW_MAX) {
            return normalized;
        }
        return normalized.substring(0, QUESTION_PREVIEW_MAX - 1).stripTrailing() + "…";
    }

    /**
     * Разворачивает прогноз повторений в непрерывный ряд из {@link #FORECAST_DAYS} дней
     * (сегодня … +6), заполняя дни без due-вопросов нулём. SQL (GROUP BY) отдаёт ТОЛЬКО дни
     * с данными → без этого при разрежённой очереди рендерилась одна строка «Завтра N» с
     * полосой во всю ширину (одинокое значение масштабируется к max=N), что читается как пик
     * нагрузки, хотя это «N вопросов завтра, дальше пусто». Пустые дни рисуются пустым треком —
     * так же честно, как «Дни без ответов — пустые столбики» в «Динамике точности».
     *
     * <p>Полностью пустая очередь (SQL вернул 0 строк) остаётся пустым списком → секция
     * скрывается через {@code th:if}, а не показывает 7 плоских строк ни о чём.
     */
    private List<QuestionStatsRepository.ForecastDay> densifyForecast(
            List<QuestionStatsRepository.ForecastDay> sparse) {
        if (sparse == null || sparse.isEmpty()) {
            return List.of();
        }
        Map<String, Long> byDay = new LinkedHashMap<>();
        for (QuestionStatsRepository.ForecastDay day : sparse) {
            byDay.merge(day.day(), day.count(), Long::sum);
        }
        List<QuestionStatsRepository.ForecastDay> dense = new ArrayList<>(FORECAST_DAYS);
        LocalDate cursor = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        for (int i = 0; i < FORECAST_DAYS; i++) {
            String day = cursor.plusDays(i).format(fmt);
            dense.add(new QuestionStatsRepository.ForecastDay(day, byDay.getOrDefault(day, 0L)));
        }
        return dense;
    }

    /**
     * Заполняет непрерывный ряд дней (включая нули) для sparkline/bars на вкладке «Динамика».
     */
    private List<AccuracyTrendDay> buildAccuracyTrend(List<DailyActivityRepository.DayAccuracy> rows) {
        Map<String, DailyActivityRepository.DayAccuracy> byDay = new LinkedHashMap<>();
        if (rows != null) {
            for (DailyActivityRepository.DayAccuracy row : rows) {
                byDay.put(row.day(), row);
            }
        }
        List<AccuracyTrendDay> trend = new ArrayList<>(ACCURACY_TREND_DAYS);
        LocalDate cursor = LocalDate.now().minusDays(ACCURACY_TREND_DAYS - 1L);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        for (int i = 0; i < ACCURACY_TREND_DAYS; i++) {
            String day = cursor.plusDays(i).format(fmt);
            DailyActivityRepository.DayAccuracy row = byDay.get(day);
            int answered = row == null ? 0 : row.answered();
            int correct = row == null ? 0 : row.correct();
            Double accuracy = answered > 0 ? (correct * 100.0 / answered) : null;
            trend.add(new AccuracyTrendDay(day, answered, correct, accuracy));
        }
        return trend;
    }

    private List<TopicStatsChartPayload> toChartPayload(List<TopicStats> topicStats) {
        return topicStats.stream()
                .map(ts -> new TopicStatsChartPayload(
                        ts.topic(),
                        topicUtils.displayName(ts.topic()),
                        ts.total(),
                        ts.learned(),
                        ts.correct(),
                        ts.wrong()))
                .toList();
    }

    /**
     * Экранирует символы, способные пробить тег &lt;script&gt; при literal-вставке JSON
     * в HTML (без th-escape): '&lt;', '&gt;', '&amp;' → юникод-эскейпы. Эти символы встречаются
     * только внутри строковых значений JSON (структурные токены — {}[]:,"), поэтому
     * замена по всей строке сохраняет валидность и семантику: JSON.parse даёт тот же
     * текст, но подстрока "&lt;/script&gt;" появиться не может. Локально, не на общий
     * бин objectMapper — чтобы не менять формат прочих JSON-ответов. См. round-01 C35.
     */
    private static String escapeForHtmlScript(String json) {
        if (json == null) {
            return "[]";
        }
        return json
                .replace("<", "\\u003C")
                .replace(">", "\\u003E")
                .replace("&", "\\u0026");
    }

    /**
     * Сериализует payload в HTML-safe JSON для literal-вставки в &lt;script&gt; ({@link
     * #escapeForHtmlScript}); при сбое Jackson логирует {@code errorKey} (SLF4J-плейсхолдеры
     * заполняются {@code logArgs}, исключение добавляется последним аргументом) и возвращает
     * {@code "[]"}-фолбэк. Централизует три идентичных try/catch-блока {@link #build}.
     */
    private String writeJsonForHtml(Object payload, String errorKey, Object... logArgs) {
        try {
            return escapeForHtmlScript(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            Object[] args = Arrays.copyOf(logArgs, logArgs.length + 1);
            args[logArgs.length] = e;
            log.error(errorKey, args);
            return "[]";
        }
    }

    @Builder(toBuilder = true)
    public record StatsPageState(
            InterviewStats stats,
            List<String> topics,
            List<?> groups,
            String selectedGroup,
            InterviewFilter filter,
            String searchQuery,
            List<SearchService.SearchItem> searchResults,
            List<TopicStats> topicStats,
            TopicTableView topicTable,
            String topicStatsJson,
            List<QuestionStatsRepository.TopicCoverage> coverageGaps,
            List<QuestionStatsRepository.ForecastDay> reviewForecast,
            LearningMetrics metrics,
            List<DifficultySlice> difficultyDistribution,
            String difficultyJson,
            Map<String, MetricDefinitions.Definition> metricDefinitions,
            QuestionStatsRepository.WeakestTopic weakestTopic,
            long weakTopicCount,
            List<WrongQuestionItem> recentWrongQuestions,
            List<AccuracyTrendDay> accuracyTrend,
            String accuracyTrendJson,
            List<TopicCatalogService.GroupProgress> groupCatalog,
            /**
             * Место в пути: ступень, уровень и заполненность уровня. Прогрессия жила ровно
             * на одном экране — в подписи под числом опыта на итогах сессии, — а до него
             * доезжают не все режимы: TRAINING работает вовсе без сессии. Аналитика
             * отвечает на вопрос «где я», и ступень — часть этого ответа.
             */
            Progression progression,
            /**
             * Последние завершённые сессии. До V29 сводка сессии жила только в
             * HttpSession и терялась при следующем старте — «сколько я занимался и как
             * шёл» продукт не отвечал нигде.
             */
            List<com.cheatsheet.quiz.domain.SessionRecord> sessionHistory,
            /**
             * Знаки отличия. Считаются из тех же метрик и прогрессии, что уже собраны
             * страницей: знак — это ФАКТ о накопленном, а не запись в отдельной таблице,
             * которая умеет разойтись с данными.
             */
            List<com.cheatsheet.quiz.domain.Achievement.Earned> achievements
    ) {
        public StatsPageState(InterviewStats stats, List<String> topics, List<?> groups,
                              String selectedGroup, InterviewFilter filter, String searchQuery,
                              List<SearchService.SearchItem> searchResults,
                              List<TopicStats> topicStats, String topicStatsJson) {
            this(stats, topics, groups, selectedGroup, filter, searchQuery, searchResults,
                    topicStats, TopicTableView.of(topicStats), topicStatsJson,
                    List.of(), List.of(), LearningMetrics.EMPTY,
                    List.of(), "[]", MetricDefinitions.catalog(), null, 0L,
                    List.of(), List.of(), "[]", List.of(),
                    Progression.of(com.cheatsheet.quiz.domain.UserExperience.EMPTY, 0), List.of(),
                    com.cheatsheet.quiz.domain.Achievement.evaluate(
                            LearningMetrics.EMPTY,
                            Progression.of(com.cheatsheet.quiz.domain.UserExperience.EMPTY, 0)));
        }
    }

    /**
     * Запрос к таблице тем: чем сортировать, в какую сторону и показывать ли всё.
     *
     * <p>Три параметра URL ({@code sort}, {@code dir}, {@code rows}) вместо трёх
     * состояний в памяти вкладки. Значение — не только адресуемость: пока порядок
     * и объём жили в JS, они не переживали перезагрузку, не копировались ссылкой и
     * не существовали до первого кадра. Нормализация повторяет идиому
     * {@code BookmarksQuery}: неизвестное значение молча становится умолчанием, а
     * не роняет страницу и не рисует пустой контрол.</p>
     *
     * @param sort {@link #SORT_NAME}/{@link #SORT_TOTAL}/{@link #SORT_ACCURACY}/{@link #SORT_MATURITY}
     * @param dir  {@link #DIR_ASC}/{@link #DIR_DESC}
     * @param all  показывать все строки выборки, а не окно из {@link #TOPIC_TABLE_WINDOW}
     */
    @Builder(toBuilder = true)
    public record TopicTableQuery(String sort, String dir, boolean all) {
        public static final String SORT_NAME = "name";
        public static final String SORT_TOTAL = "total";
        public static final String SORT_ACCURACY = "accuracy";
        public static final String SORT_MATURITY = "maturity";
        public static final String DIR_ASC = "asc";
        public static final String DIR_DESC = "desc";

        /** Значение параметра {@code rows}, включающее полный список. */
        public static final String ROWS_ALL = "all";

        public static TopicTableQuery empty() {
            return new TopicTableQuery(SORT_NAME, DIR_ASC, false);
        }

        /** Собирает запрос из сырых параметров MVC. */
        public static TopicTableQuery of(String sort, String dir, String rows) {
            return new TopicTableQuery(sort, dir, ROWS_ALL.equalsIgnoreCase(rows)).normalized();
        }

        public TopicTableQuery normalized() {
            String normalizedSort = switch (sort == null ? "" : sort.strip().toLowerCase(Locale.ROOT)) {
                case SORT_TOTAL -> SORT_TOTAL;
                case SORT_ACCURACY -> SORT_ACCURACY;
                case SORT_MATURITY -> SORT_MATURITY;
                default -> SORT_NAME;
            };
            String normalizedDir = DIR_DESC.equalsIgnoreCase(dir) ? DIR_DESC
                    : DIR_ASC.equalsIgnoreCase(dir) ? DIR_ASC
                    : defaultDir(normalizedSort);
            return new TopicTableQuery(normalizedSort, normalizedDir, all);
        }

        /**
         * Направление первого клика по колонке — то, ради которого по ней и сортируют.
         *
         * <p>«Всего» по убыванию: интересны большие темы. Точность и зрелость по
         * возрастанию: страница называется «Где чаще ошибаешься» и «Слабые темы», и
         * график над таблицей уже строит темы «от слабых к сильным» — колонка,
         * открывающаяся сильнейшими, спорила бы с обоими соседями.</p>
         */
        public static String defaultDir(String sort) {
            return SORT_TOTAL.equals(sort) ? DIR_DESC : DIR_ASC;
        }

        /** Направление, которое даст повторный клик по уже активной колонке. */
        public String flipped() {
            return DIR_DESC.equals(dir) ? DIR_ASC : DIR_DESC;
        }
    }

    /**
     * Таблица тем в том виде, в каком её отдаёт сервер: готовые строки, заголовки
     * колонок с состоянием сортировки и счётчики для копирайта под таблицей.
     *
     * @param rows       строки, попавшие в выдачу (уже отсортированы и урезаны)
     * @param query      нормализованный запрос — контролы обязаны показывать своё состояние
     * @param totalRows  сколько строк в выборке всего
     * @param hiddenRows сколько строк выборки осталось за окном
     * @param keepQuery  параметры отбора, которые переносит каждая ссылка таблицы
     */
    public record TopicTableView(
            List<TopicStats> rows,
            TopicTableQuery query,
            int totalRows,
            int hiddenRows,
            String keepQuery
    ) {
        /** Якорь: переход по заголовку возвращает к таблице, а не к началу страницы. */
        private static final String ANCHOR = "#topic-table-section";

        /** Полная выдача в порядке источника — для вызовов без запроса таблицы. */
        public static TopicTableView of(List<TopicStats> rows) {
            List<TopicStats> safe = rows == null ? List.of() : List.copyOf(rows);
            return new TopicTableView(safe, TopicTableQuery.empty(), safe.size(), 0, "");
        }

        /** Есть ли что показывать по ссылке «показать все». */
        public boolean truncated() {
            return hiddenRows > 0;
        }

        /** Развернуть выдачу до всех {@link #totalRows} строк, сохранив порядок и отбор. */
        public String expandHref() {
            return href(query.sort(), query.dir(), true);
        }

        /** Вернуться к окну из {@link #TOPIC_TABLE_WINDOW} строк. */
        public String collapseHref() {
            return href(query.sort(), query.dir(), false);
        }

        private String href(String sort, String dir, boolean all) {
            StringBuilder sb = new StringBuilder("/stats?");
            if (keepQuery != null && !keepQuery.isEmpty()) {
                sb.append(keepQuery).append('&');
            }
            sb.append("sort=").append(sort).append("&dir=").append(dir);
            if (all) {
                sb.append("&rows=").append(TopicTableQuery.ROWS_ALL);
            }
            return sb.append(ANCHOR).toString();
        }

        /**
         * Заголовки колонок таблицы. Порядок и подписи живут здесь, а не в разметке,
         * потому что вместе с подписью колонка несёт своё состояние сортировки
         * ({@code aria-sort}) и направление следующего клика — а это уже не оформление.
         * Несортируемые колонки различает {@code key == null}.
         *
         * <p>Класс колонки — её имя ({@code topic-col-*}), а не набор атомарных
         * утилит. Ширины 60/80/140px жили в {@code utilities-core.css} как
         * {@code .w-60}/{@code .w-80}/{@code .min-w-140} — это была геометрия ОДНОЙ
         * таблицы, вынесенная в общий словарь: любой их пользователь на другой
         * странице молча привязывался к решению аналитики. Теперь ширины лежат в
         * {@code pages-stats.css} рядом с остальной геометрией страницы, а утилиты
         * удалены. {@code text-center} остаётся утилитой — она про выравнивание,
         * а не про размер, и её носят ещё 16 узлов.</p>
         */
        public List<Column> columns() {
            return List.of(
                    column(TopicTableQuery.SORT_NAME, "Тема", "topic-col-name", null),
                    column(TopicTableQuery.SORT_TOTAL, "Всего", "topic-col-total text-center", null),
                    column(null, "Прогресс", "topic-col-progress", null),
                    column(TopicTableQuery.SORT_ACCURACY, "Точность", "topic-col-accuracy text-center",
                            "Доля верных ответов среди отвеченных (не из всех вопросов темы)"),
                    column(TopicTableQuery.SORT_MATURITY, "Зрелость", "topic-col-maturity text-center",
                            "Среднее ease×interval — показатель глубины запоминания"),
                    column(null, "Действие", "topic-col-actions", null));
        }

        private Column column(String key, String label, String cssClass, String title) {
            if (key == null) {
                return new Column(null, label, cssClass, title, null, null);
            }
            boolean active = key.equals(query.sort());
            String ariaSort = !active ? "none"
                    : TopicTableQuery.DIR_DESC.equals(query.dir()) ? "descending" : "ascending";
            String nextDir = active ? query.flipped() : TopicTableQuery.defaultDir(key);
            String css = active ? (cssClass == null ? "is-sorted" : cssClass + " is-sorted") : cssClass;
            // Развёрнутость выдачи переносится: пользователь, открывший все 319 строк,
            // сортирует именно их — а не возвращается к окну из 12 при первом же клике.
            return new Column(key, label, css, title, ariaSort, href(key, nextDir, query.all()));
        }

        /**
         * @param key      значение параметра {@code sort}; {@code null} — колонка не сортируется
         * @param ariaSort {@code ascending}/{@code descending}/{@code none}; {@code null} — атрибута нет
         * @param href     адрес перехода по заголовку; {@code null} — колонка не сортируется
         */
        public record Column(
                String key,
                String label,
                String cssClass,
                String title,
                String ariaSort,
                String href
        ) {}
    }

    /**
     * Слайс доната «Сложность вопросов»: уровень (как в БД), русская подпись,
     * число вопросов и доля в процентах.
     */
    public record DifficultySlice(String level, String label, long count, double percent) {}

    /**
     * Карточка ошибки для вкладки «Ошибки»: превью вопроса + счётчики + deep-link данные.
     */
    public record WrongQuestionItem(
            long questionId,
            String topic,
            String topicDisplayName,
            String questionPreview,
            int wrongCount,
            int correctCount,
            long nextReviewAt,
            Long lastWrongAt
    ) {}

    /**
     * День тренда точности: ISO-дата, ответов, верных, % или null если нет ответов.
     */
    public record AccuracyTrendDay(String day, int answered, int correct, Double accuracyPercent) {}

    /**
     * JSON для графиков на /stats: slug + displayName из TopicUtils.
     *
     * <p>Полей ровно столько, сколько читает {@code static/js/stats/topic-data.js}. Payload
     * повторяется по числу тем (сейчас ~320), поэтому каждое лишнее поле — это
     * лишний килобайт в разметке страницы за одну строку в записи.
     */
    private record TopicStatsChartPayload(
            String topic,
            String displayName,
            long total,
            long learned,
            long correct,
            long wrong
    ) {}
}
