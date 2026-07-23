package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    InterviewFacade facade;
    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;
    SearchService searchService;
    ObjectMapper objectMapper;
    QuestionStatsRepository questionStatsRepository;
    LearningMetricsService learningMetricsService;
    TopicUtils topicUtils;
    DailyActivityRepository dailyActivityRepository;

    /**
     * Строит state страницы статистики по фильтру и поисковому запросу.
     */
    public StatsPageState build(InterviewFilter filter, String query, int searchLimit) {
        InterviewStats stats = facade.getStats(filter);
        List<String> availableTopics = questionRepository.findTopics();
        String selectedGroup = topicCatalogService.normalizeGroup(filter.group());
        List<String> topics = topicCatalogService.filterAndSortTopics(availableTopics, selectedGroup, filter.isOrdered());
        List<TopicStats> topicStats = facade.getTopicStats();
        // C35: JSON встраивается literal-ом в <script id="topic-stats-data"> (stats.html,
        // через [(...)] без HTML-escape). Jackson по умолчанию НЕ экранирует '<' и '/',
        // поэтому имя темы с подстрокой "</script>" закрыло бы тег и пробило бы в HTML-
        // контекст. Экранируем юникод-эскейпами (OWASP JSON-in-HTML) — см. escapeForHtmlScript.
        String topicStatsJson = writeJsonForHtml(toChartPayload(topicStats),
                "stats_page_topic_stats_json_failed size={}", topicStats.size());
        log.info("stats_page_state_built topic={} group={} searchQueryPresent={}",
                filter.topic(), selectedGroup, query != null && !query.isBlank());
        List<QuestionStatsRepository.TopicCoverage> coverageGaps =
                questionStatsRepository.findTopicCoverageGaps(COVERAGE_GAP_THRESHOLD, COVERAGE_GAP_LIMIT);
        long nowEpoch = System.currentTimeMillis() / 1000L;
        List<QuestionStatsRepository.ForecastDay> forecast =
                questionStatsRepository.findReviewForecast(nowEpoch, FORECAST_DAYS);
        LearningMetrics metrics = learningMetricsService.compute();
        List<DifficultySlice> difficultyDistribution = buildDifficultyDistribution();
        String difficultyJson = writeJsonForHtml(difficultyDistribution, "stats_page_difficulty_json_failed");
        List<WrongQuestionItem> recentWrongQuestions = questionStatsRepository
                .findRecentWrongQuestions(WRONG_DRILLDOWN_LIMIT)
                .stream()
                .map(this::toWrongQuestionItem)
                .toList();
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
                topicStatsJson,
                coverageGaps,
                forecast,
                metrics,
                difficultyDistribution,
                difficultyJson,
                MetricDefinitions.catalog(),
                findWeakestTopic(topicStats),
                recentWrongQuestions,
                accuracyTrend,
                accuracyTrendJson
        );
    }

    /**
     * Topic with lowest accuracy among those that have at least one attempt.
     * Tie-break: more attempts first (stronger signal).
     */
    public static String findWeakestTopic(List<TopicStats> topicStats) {
        if (topicStats == null || topicStats.isEmpty()) {
            return null;
        }
        TopicStats weakest = null;
        double weakestAccuracy = Double.POSITIVE_INFINITY;
        long weakestAttempts = 0;
        for (TopicStats ts : topicStats) {
            long attempts = ts.correct() + ts.wrong();
            if (attempts <= 0) {
                continue;
            }
            double accuracy = (double) ts.correct() / (double) attempts;
            if (weakest == null
                    || accuracy < weakestAccuracy
                    || (Double.compare(accuracy, weakestAccuracy) == 0 && attempts > weakestAttempts)) {
                weakest = ts;
                weakestAccuracy = accuracy;
                weakestAttempts = attempts;
            }
        }
        return weakest == null ? null : weakest.topic();
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
                        ts.due(),
                        ts.learned(),
                        ts.correct(),
                        ts.wrong(),
                        ts.regenSum(),
                        ts.maturityScore()))
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
            String topicStatsJson,
            List<QuestionStatsRepository.TopicCoverage> coverageGaps,
            List<QuestionStatsRepository.ForecastDay> reviewForecast,
            LearningMetrics metrics,
            List<DifficultySlice> difficultyDistribution,
            String difficultyJson,
            Map<String, MetricDefinitions.Definition> metricDefinitions,
            String weakestTopic,
            List<WrongQuestionItem> recentWrongQuestions,
            List<AccuracyTrendDay> accuracyTrend,
            String accuracyTrendJson
    ) {
        public StatsPageState(InterviewStats stats, List<String> topics, List<?> groups,
                              String selectedGroup, InterviewFilter filter, String searchQuery,
                              List<SearchService.SearchItem> searchResults,
                              List<TopicStats> topicStats, String topicStatsJson) {
            this(stats, topics, groups, selectedGroup, filter, searchQuery, searchResults,
                    topicStats, topicStatsJson, List.of(), List.of(), LearningMetrics.EMPTY,
                    List.of(), "[]", MetricDefinitions.catalog(), findWeakestTopic(topicStats),
                    List.of(), List.of(), "[]");
        }
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

    /** JSON для Chart.js на /stats: slug + displayName из TopicUtils. */
    private record TopicStatsChartPayload(
            String topic,
            String displayName,
            long total,
            long due,
            long learned,
            long correct,
            long wrong,
            long regenSum,
            double maturityScore
    ) {}
}
