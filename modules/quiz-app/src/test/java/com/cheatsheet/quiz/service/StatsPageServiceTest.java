package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.common.constants.QuizConstants;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
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
    @Mock
    com.cheatsheet.quiz.feature.interview.service.progress.ExperienceService experienceService;
    @Mock
    com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService sessionHistoryService;

    StatsPageService service;

    @BeforeEach
    void setUp() {
        service = new StatsPageService(facade, questionRepository, topicCatalogService,
                searchService, objectMapper, questionStatsRepository, learningMetricsService,
                topicUtils, dailyActivityRepository, experienceService, sessionHistoryService);
        lenient().when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY);
        // Прогрессия собирается на каждом построении страницы: ступень не зависит от отбора,
        // но и не бывает пустой — «Ученик, уровень 1» это тоже ответ на «где я».
        lenient().when(experienceService.snapshot())
                .thenReturn(com.cheatsheet.quiz.domain.UserExperience.EMPTY);
        lenient().when(experienceService.currentStreak()).thenReturn(0);
        // Журнал занятий читается на каждом построении страницы; на чистой базе он пуст.
        lenient().when(sessionHistoryService.recent()).thenReturn(java.util.List.of());
        lenient().when(dailyActivityRepository.findRecentDays(anyInt())).thenReturn(List.of());
        lenient().when(questionStatsRepository.findRecentWrongQuestions(anyList(), any(), anyInt()))
                .thenReturn(List.of());
        lenient().when(questionStatsRepository.findDifficultyDistribution()).thenReturn(List.of());
        lenient().when(questionStatsRepository.findTopicCoverageGaps(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of());
        lenient().when(questionStatsRepository.findReviewForecast(org.mockito.ArgumentMatchers.anyLong(), anyInt()))
                .thenReturn(List.of());
    }

    @Test
    void fallsBackToEmptyJsonWhenSerializationFails() throws Exception {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        List<TopicStats> topicStats = List.of(new TopicStats("java", 10, 4, 1, 3, 6, 1, 0.0));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.topicsForFilter(filter)).thenReturn(List.of("java"));
        when(facade.getTopicStats(filter)).thenReturn(topicStats);
        lenient().when(facade.getTopicStats()).thenReturn(topicStats);
        when(searchService.search(eq("hashmap"), anyInt())).thenReturn(List.of());
        when(topicUtils.displayName("java")).thenReturn("Java");
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenThrow(new JsonProcessingException("bad json") {});
        // Слабую тему и их число страница НЕ считает сама — берёт у репозитория, тем же
        // правилом, что и режим «Слабые темы» (см. QuestionStatsRepositoryTest), но по
        // выборке страницы (FRD-025), а не по всему банку.
        when(questionStatsRepository.findWeakestTopic(
                List.of("java"), false, false, QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn(new QuestionStatsRepository.WeakestTopic("java", 42));
        when(questionStatsRepository.countWeakTopics(
                List.of("java"), false, false, QuizConstants.WEAK_TOPIC_THRESHOLD)).thenReturn(3L);

        StatsPageService.StatsPageState state = service.build(filter, "hashmap", 20);

        assertThat(state.topicStatsJson()).isEqualTo("[]");
        assertThat(state.searchQuery()).isEqualTo("hashmap");
        assertThat(state.stats().correct()).isEqualTo(7);
        // Метрики обучения (Фаза 4) прокидываются в state страницы.
        assertThat(state.metrics()).isEqualTo(LearningMetrics.EMPTY);
        assertThat(state.weakestTopic())
                .isEqualTo(new QuestionStatsRepository.WeakestTopic("java", 42));
        assertThat(state.weakTopicCount()).isEqualTo(3L);
    }

    @Test
    void buildIncludesWrongDrilldownAndAccuracyTrend() throws Exception {
        InterviewFilter filter = new InterviewFilter(null, null, false, false, false, true);
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup(null)).thenReturn(null);
        when(topicCatalogService.filterAndSortTopics(List.of("java"), null, true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.topicsForFilter(filter)).thenReturn(List.of("java"));
        when(facade.getTopicStats(filter)).thenReturn(List.of());
        lenient().when(facade.getTopicStats()).thenReturn(List.of());
        when(searchService.search(eq(""), anyInt())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenReturn("[]");
        when(topicUtils.displayName("java")).thenReturn("Java");
        when(questionStatsRepository.findRecentWrongQuestions(anyList(), any(), anyInt())).thenReturn(List.of(
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

    /**
     * FRD-025. Страница `/stats` объявляет источник словами («Отобрано: …»), поэтому
     * каждый срез ПО ТЕМАМ обязан считаться по одной и той же выборке — той, что названа
     * в полосе отбора. До iter-73 таблица «Детализация по темам», пробелы покрытия, разбор
     * ошибок и карточка слабых тем брали полный банк: страница показывала отбор в шапке и
     * весь банк под ней (замер: 319 строк при любом `?group=`/`?topic=`).
     *
     * <p>Гейт проверяет именно РАЗДЕЛЕНИЕ источников, а не факт вызова: срезы получают
     * список тем выборки, а каталог разделов — полный банк (он и есть навигация «куда
     * пойти дальше» и, сузившись, перестал бы отвечать на свой вопрос).
     */
    @Test
    void everyTopicSliceIsScopedToSelectionWhileCatalogStaysFullBank() throws Exception {
        InterviewFilter filter = new InterviewFilter(null, "foundation", true, true, false, true);
        List<String> selected = List.of("java/java-stream-interview", "java/java-optional-interview");
        List<TopicStats> selectionStats = List.of(new TopicStats("java/java-stream-interview", 10, 4, 1, 3, 6, 1, 0.0));
        List<TopicStats> fullBankStats = List.of(
                new TopicStats("java/java-stream-interview", 10, 4, 1, 3, 6, 1, 0.0),
                new TopicStats("go/go-testing-interview", 7, 2, 5, 0, 0, 0, 0.0));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java/java-stream-interview"));
        when(topicCatalogService.normalizeGroup("foundation")).thenReturn("foundation");
        when(topicCatalogService.filterAndSortTopics(any(), eq("foundation"), eq(true))).thenReturn(selected);
        when(topicCatalogService.groupOptions(any())).thenReturn(List.of());
        when(facade.topicsForFilter(filter)).thenReturn(selected);
        when(facade.getTopicStats(filter)).thenReturn(selectionStats);
        when(facade.getTopicStats()).thenReturn(fullBankStats);
        when(searchService.search(any(), anyInt())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenReturn("[]");
        when(topicUtils.displayName("java/java-stream-interview")).thenReturn("Stream API");

        StatsPageService.StatsPageState state = service.build(filter, "", 20);

        // Таблица тем — по выборке, а не по банку.
        assertThat(state.topicStats()).isEqualTo(selectionStats);

        // Пробелы покрытия, разбор ошибок и карточка слабых тем — по ТОМУ ЖЕ списку тем
        // и с теми же флажками отбора, что и сводка наверху.
        verify(questionStatsRepository).findTopicCoverageGaps(eq(selected), anyInt(), anyInt());
        verify(questionStatsRepository).findRecentWrongQuestions(eq(selected), eq(true), anyInt());
        verify(questionStatsRepository).findWeakestTopic(
                selected, true, true, QuizConstants.WEAK_TOPIC_THRESHOLD);
        verify(questionStatsRepository).countWeakTopics(
                selected, true, true, QuizConstants.WEAK_TOPIC_THRESHOLD);

        // Каталог разделов — по полному банку: он навигация, а не срез.
        verify(topicCatalogService).groupCatalog(any(), eq(fullBankStats));
    }

    @Test
    void escapesScriptBreakingCharsForHtmlEmbedding() throws Exception {
        // C35: topicStatsJson встраивается literal-ом в <script id="topic-stats-data">.
        // Имя темы с подстрокой "</script>" закрыло бы тег → пробой в HTML-контекст.
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        List<TopicStats> topicStats = List.of(new TopicStats("java", 10, 4, 1, 3, 6, 1, 0.0));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.topicsForFilter(filter)).thenReturn(List.of("java"));
        when(facade.getTopicStats(filter)).thenReturn(topicStats);
        lenient().when(facade.getTopicStats()).thenReturn(topicStats);
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
