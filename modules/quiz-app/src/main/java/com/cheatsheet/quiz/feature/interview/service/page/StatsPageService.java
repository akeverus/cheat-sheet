package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.List;

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

    InterviewFacade facade;
    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;
    SearchService searchService;
    ObjectMapper objectMapper;
    QuestionStatsRepository questionStatsRepository;

    /**
     * Строит state страницы статистики по фильтру и поисковому запросу.
     */
    public StatsPageState build(InterviewFilter filter, String query, int searchLimit) {
        InterviewStats stats = facade.getStats(filter);
        List<String> availableTopics = questionRepository.findTopics();
        String selectedGroup = topicCatalogService.normalizeGroup(filter.group());
        List<String> topics = topicCatalogService.filterAndSortTopics(availableTopics, selectedGroup, filter.isOrdered());
        List<TopicStats> topicStats = facade.getTopicStats();
        String topicStatsJson = "[]";
        try {
            // C35: JSON встраивается literal-ом в <script id="topic-stats-data"> (stats.html,
            // через [(...)] без HTML-escape). Jackson по умолчанию НЕ экранирует '<' и '/',
            // поэтому имя темы с подстрокой "</script>" закрыло бы тег и пробило бы в HTML-
            // контекст. Экранируем юникод-эскейпами (OWASP JSON-in-HTML) — см. escapeForHtmlScript.
            topicStatsJson = escapeForHtmlScript(objectMapper.writeValueAsString(topicStats));
        } catch (JsonProcessingException e) {
            log.error("stats_page_topic_stats_json_failed size={}", topicStats.size(), e);
        }
        log.info("stats_page_state_built topic={} group={} searchQueryPresent={}",
                filter.topic(), selectedGroup, query != null && !query.isBlank());
        List<QuestionStatsRepository.TopicCoverage> coverageGaps =
                questionStatsRepository.findTopicCoverageGaps(COVERAGE_GAP_THRESHOLD, COVERAGE_GAP_LIMIT);
        long nowEpoch = System.currentTimeMillis() / 1000L;
        List<QuestionStatsRepository.ForecastDay> forecast =
                questionStatsRepository.findReviewForecast(nowEpoch, FORECAST_DAYS);
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
                forecast
        );
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
        return json
                .replace("<", "\\u003C")
                .replace(">", "\\u003E")
                .replace("&", "\\u0026");
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
            List<QuestionStatsRepository.ForecastDay> reviewForecast
    ) {
        public StatsPageState(InterviewStats stats, List<String> topics, List<?> groups,
                              String selectedGroup, InterviewFilter filter, String searchQuery,
                              List<SearchService.SearchItem> searchResults,
                              List<TopicStats> topicStats, String topicStatsJson) {
            this(stats, topics, groups, selectedGroup, filter, searchQuery, searchResults,
                    topicStats, topicStatsJson, List.of(), List.of());
        }
    }
}
