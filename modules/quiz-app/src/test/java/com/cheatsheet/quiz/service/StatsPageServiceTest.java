package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsPageServiceTest {

    @Mock
    InterviewFacade facade;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    TopicCatalogService topicCatalogService;
    @Mock
    SearchService searchService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    QuestionStatsRepository questionStatsRepository;
    @Mock
    LearningMetricsService learningMetricsService;
    @Mock
    TopicUtils topicUtils;
    @Mock
    DailyActivityRepository dailyActivityRepository;

    StatsPageService service;

    @BeforeEach
    void setUp() {
        service = new StatsPageService(facade, questionRepository, topicCatalogService,
                searchService, objectMapper, questionStatsRepository, learningMetricsService,
                topicUtils, dailyActivityRepository);
        lenient().when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY);
        lenient().when(dailyActivityRepository.findRecentDays(anyInt())).thenReturn(List.of());
        lenient().when(questionStatsRepository.findRecentWrongQuestions(anyInt())).thenReturn(List.of());
        lenient().when(questionStatsRepository.findDifficultyDistribution()).thenReturn(List.of());
        lenient().when(questionStatsRepository.findTopicCoverageGaps(anyInt(), anyInt())).thenReturn(List.of());
        lenient().when(questionStatsRepository.findReviewForecast(org.mockito.ArgumentMatchers.anyLong(), anyInt()))
                .thenReturn(List.of());
    }

    @Test
    void fallsBackToEmptyJsonWhenSerializationFails() throws Exception {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        List<TopicStats> topicStats = List.of(new TopicStats("java", 10, 4, 3, 6, 1, 2));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.getTopicStats()).thenReturn(topicStats);
        when(searchService.search(eq("hashmap"), anyInt())).thenReturn(List.of());
        when(topicUtils.displayName("java")).thenReturn("Java");
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenThrow(new JsonProcessingException("bad json") {});

        StatsPageService.StatsPageState state = service.build(filter, "hashmap", 20);

        assertThat(state.topicStatsJson()).isEqualTo("[]");
        assertThat(state.searchQuery()).isEqualTo("hashmap");
        assertThat(state.stats().correct()).isEqualTo(7);
        // Метрики обучения (Фаза 4) прокидываются в state страницы.
        assertThat(state.metrics()).isEqualTo(LearningMetrics.EMPTY);
        assertThat(state.weakestTopic()).isEqualTo("java");
    }

    @Test
    void findWeakestTopicPrefersLowestAccuracyThenMoreAttempts() {
        List<TopicStats> topicStats = List.of(
                new TopicStats("strong", 10, 0, 8, 9, 1, 0),
                new TopicStats("weak-a", 10, 2, 1, 1, 3, 0),
                new TopicStats("weak-b", 10, 2, 1, 2, 6, 0),
                new TopicStats("untouched", 10, 0, 0, 0, 0, 0)
        );
        // weak-a: 25%, weak-b: 25% but more attempts → weak-b
        assertThat(StatsPageService.findWeakestTopic(topicStats)).isEqualTo("weak-b");
    }

    @Test
    void findWeakestTopicReturnsNullWithoutAttempts() {
        assertThat(StatsPageService.findWeakestTopic(List.of(
                new TopicStats("untouched", 10, 0, 0, 0, 0, 0)
        ))).isNull();
    }

    @Test
    void buildIncludesWrongDrilldownAndAccuracyTrend() throws Exception {
        InterviewFilter filter = new InterviewFilter(null, null, false, false, false, true);
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup(null)).thenReturn(null);
        when(topicCatalogService.filterAndSortTopics(List.of("java"), null, true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.getTopicStats()).thenReturn(List.of());
        when(searchService.search(eq(""), anyInt())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenReturn("[]");
        when(topicUtils.displayName("java")).thenReturn("Java");
        when(questionStatsRepository.findRecentWrongQuestions(anyInt())).thenReturn(List.of(
                new QuestionStatsRepository.WrongQuestionRow(42L, "java", "What is Spring?", 2, 1, 1_800_000_000L, 1_700_000_000L)
        ));
        when(dailyActivityRepository.findRecentDays(anyInt())).thenReturn(List.of(
                new DailyActivityRepository.DayAccuracy(java.time.LocalDate.now().toString(), 4, 3)
        ));

        StatsPageService.StatsPageState state = service.build(filter, "", 20);

        assertThat(state.recentWrongQuestions()).hasSize(1);
        assertThat(state.recentWrongQuestions().get(0).questionId()).isEqualTo(42L);
        assertThat(state.recentWrongQuestions().get(0).topicDisplayName()).isEqualTo("Java");
        assertThat(state.recentWrongQuestions().get(0).questionPreview()).contains("Spring");
        assertThat(state.accuracyTrend()).hasSize(14);
        assertThat(state.accuracyTrend().stream().anyMatch(d -> d.answered() == 4 && d.correct() == 3)).isTrue();
        assertThat(state.accuracyTrendJson()).isEqualTo("[]");
    }

    @Test
    void escapesScriptBreakingCharsForHtmlEmbedding() throws Exception {
        // C35: topicStatsJson встраивается literal-ом в <script id="topic-stats-data">.
        // Имя темы с подстрокой "</script>" закрыло бы тег → пробой в HTML-контекст.
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        List<TopicStats> topicStats = List.of(new TopicStats("java", 10, 4, 3, 6, 1, 2));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.getTopicStats()).thenReturn(topicStats);
        when(searchService.search(eq("hashmap"), anyInt())).thenReturn(List.of());
        when(topicUtils.displayName("java")).thenReturn("Java");
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenReturn("[{\"topic\":\"a</script><script>alert(1)&x\"}]");

        StatsPageService.StatsPageState state = service.build(filter, "hashmap", 20);

        String json = state.topicStatsJson();
        // Ни одного literal-символа, способного закрыть/открыть <script> или внести
        // HTML-сущность — всё заменено на юникод-эскейпы (JSON остаётся валидным).
        assertThat(json).doesNotContain("<");
        assertThat(json).doesNotContain(">");
        assertThat(json).doesNotContain("</script>");
        assertThat(json).contains("\\u003C");
        assertThat(json).contains("\\u003E");
        assertThat(json).contains("\\u0026");
    }
}
