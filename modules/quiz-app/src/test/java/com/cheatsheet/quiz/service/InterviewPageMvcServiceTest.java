package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.mapper.request.MvcRequestMapper;
import com.cheatsheet.quiz.api.mapper.view.MvcModelAttributeMapper;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.flow.ReviewModeService;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewPageMvcService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewPageMvcServiceTest {

    @Mock
    private InterviewSessionSupport sessionSupport;
    @Mock
    private FocusTrainingPageService focusTrainingPageService;
    @Mock
    private StatsPageService statsPageService;
    @Mock
    private com.cheatsheet.quiz.feature.interview.service.page.BookmarksPageService bookmarksPageService;
    @Mock
    private com.cheatsheet.quiz.feature.interview.usecase.BookmarkApiService bookmarkApiService;
    @Mock
    private com.cheatsheet.quiz.feature.interview.service.flow.PauseService pauseService;
    @Mock
    private ReviewModeService reviewModeService;
    @Mock
    private MvcNavigationService navigationService;
    @Mock
    private MvcModelAttributeMapper modelAttributeMapper;
    @Mock
    private MvcRequestMapper requestMapper;
    @Mock
    private HttpSession session;
    @Mock
    private com.cheatsheet.quiz.feature.interview.service.page.TopicsPageService topicsPageService;

    @Mock
    private Model model;

    private InterviewPageMvcService service;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getSearch().setStatsResultsLimit(33);
        service = new InterviewPageMvcService(
                sessionSupport,
                focusTrainingPageService,
                statsPageService,
                topicsPageService,
                bookmarksPageService,
                bookmarkApiService,
                pauseService,
                reviewModeService,
                navigationService,
                modelAttributeMapper,
                requestMapper,
                properties
        );
    }

    /**
     * «Где ошибки есть» считается ТОЛЬКО для пустого экрана разбора. На экране с
     * вопросом проход по всем темам был бы платой за весь банк ради одной строки —
     * тем же доводом карта темы считается одним запросом по текущей теме.
     */
    @Test
    void reviewHotTopicsAreQueriedOnlyWhenTheReviewScreenIsEmpty() {
        var emptyState = org.mockito.Mockito.mock(FocusTrainingPageService.FocusPageState.class);
        when(emptyState.current()).thenReturn(java.util.Optional.empty());
        when(focusTrainingPageService.buildFocusPageState(
                any(), any(), any(), anyBoolean(), any(), any())).thenReturn(emptyState);
        when(focusTrainingPageService.topicsWithMistakes(anyInt()))
                .thenReturn(java.util.List.of(
                        new com.cheatsheet.quiz.domain.TopicStats("java/streams", 10, 1, 2, 3, 3, 7, 0.5)));

        service.review(null, null, null, null, null, null, null, null, null, session, model);

        verify(focusTrainingPageService).topicsWithMistakes(anyInt());
        verify(model).addAttribute(eq("reviewHotTopics"),
                argThat(v -> v instanceof java.util.List<?> l && l.size() == 1));
    }

    /**
     * Обратная сторона того же правила: на экране С вопросом список не считается вовсе.
     * Без этой проверки гейт ловил бы только «считается, когда надо», но не
     * «не считается, когда не надо» — а цена как раз во втором.
     */
    @Test
    void reviewHotTopicsAreNotQueriedWhenAQuestionIsOnTheScreen() {
        var withQuestion = org.mockito.Mockito.mock(FocusTrainingPageService.FocusPageState.class);
        when(withQuestion.current()).thenReturn(java.util.Optional.of(
                org.mockito.Mockito.mock(com.cheatsheet.quiz.domain.InterviewQuestion.class)));
        when(focusTrainingPageService.buildFocusPageState(
                any(), any(), any(), anyBoolean(), any(), any())).thenReturn(withQuestion);

        service.review(null, null, null, null, null, null, null, null, null, session, model);

        verify(focusTrainingPageService, org.mockito.Mockito.never()).topicsWithMistakes(anyInt());
        verify(model).addAttribute(eq("reviewHotTopics"),
                argThat(v -> v instanceof java.util.List<?> l && l.isEmpty()));
    }

    @Test
    void indexBuildsFocusPageStateAndReturnsFocusView() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        InterviewFilter filter = new InterviewFilter("java", "backend", true, false, true, true);
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                null, false, false, false, null, null, null, 0.0, null, null
        );

        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(requestMapper.resolveMode(interviewSession, "TRAINING")).thenReturn(InterviewMode.TRAINING);
        when(requestMapper.resolveFilter(interviewSession, "java", "backend", true, false, true, true))
                .thenReturn(filter);
        when(focusTrainingPageService.buildFocusPageState(
                interviewSession, filter, InterviewMode.TRAINING, true, 17L, null))
                .thenReturn(pageState);
        when(navigationService.focusView()).thenReturn("focus");

        String view = service.index(
                "java", "backend", true, false, true, true, true, 17L, null, "TRAINING", session, model
        );

        assertThat(view).isEqualTo("focus");
        verify(modelAttributeMapper).applyFocusPageState(model, pageState, false);
    }

    @Test
    void reviewUsesReviewFilterAndMarksReviewMode() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        InterviewFilter baseFilter = new InterviewFilter("java", "backend", true, false, true, true);
        InterviewFilter reviewFilter = new InterviewFilter("java", "backend", true, true, true, true);
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                null, false, false, false, null, null, null, 0.0, null, null
        );

        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(requestMapper.resolveMode(interviewSession, "EXAM")).thenReturn(InterviewMode.EXAM);
        when(requestMapper.resolveReviewBaseFilter("java", "backend", true, true, true)).thenReturn(baseFilter);
        when(reviewModeService.apply(baseFilter)).thenReturn(reviewFilter);
        when(focusTrainingPageService.buildFocusPageState(
                interviewSession, reviewFilter, InterviewMode.EXAM, false, 9L, null))
                .thenReturn(pageState);
        when(navigationService.focusView()).thenReturn("focus-review");

        String view = service.review(
                "java", "backend", true, true, false, true, 9L, null, "EXAM", session, model);

        assertThat(view).isEqualTo("focus-review");
        verify(modelAttributeMapper).applyFocusPageState(model, pageState, true);
    }

    @Test
    void settingsAppliesSurfaceStateAndReturnsSettingsView() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, true, true);
        MvcRequestMapper.SettingsRequestContext context = new MvcRequestMapper.SettingsRequestContext(
                InterviewMode.FLASHCARD, filter, true
        );
        FocusTrainingPageService.SurfaceState surfaceState = FocusTrainingPageService.SurfaceState.builder()
                .topics(List.of())
                .groups(List.of())
                .filter(filter)
                .mode(InterviewMode.FLASHCARD)
                .interviewSession(interviewSession)
                .weakTopics(true)
                .build();

        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(requestMapper.resolveSettingsContext(interviewSession, "java", "backend", false, false, true, true, true, "FLASHCARD"))
                .thenReturn(context);
        when(focusTrainingPageService.buildSurfaceState(filter, InterviewMode.FLASHCARD, interviewSession, true))
                .thenReturn(surfaceState);
        when(navigationService.settingsView()).thenReturn("settings");

        String view = service.settings("java", "backend", false, false, true, true, true, "FLASHCARD", session, model);

        assertThat(view).isEqualTo("settings");
        verify(modelAttributeMapper).applySurfaceState(model, surfaceState);
    }

    @Test
    void statsUsesConfiguredLimitAndReturnsStatsView() {
        InterviewFilter filter = new InterviewFilter("java", "backend", true, false, false, true);
        MvcRequestMapper.StatsRequestContext context = new MvcRequestMapper.StatsRequestContext(filter, "hashmap");
        StatsPageService.StatsPageState state = new StatsPageService.StatsPageState(
                null, List.of(), List.of(), null, filter, "hashmap", List.of(), List.of(), "[]"
        );

        StatsPageService.TopicTableQuery tableQuery =
                StatsPageService.TopicTableQuery.of("accuracy", "desc", "all");

        when(requestMapper.resolveStatsContext("java", "backend", true, false, true, "hashmap")).thenReturn(context);
        when(statsPageService.build(filter, "hashmap", 33, tableQuery)).thenReturn(state);
        when(navigationService.statsView()).thenReturn("stats");

        String view = service.stats("java", "backend", true, false, true, "hashmap",
                "accuracy", "desc", "all", model);

        assertThat(view).isEqualTo("stats");
        verify(statsPageService).build(eq(filter), eq("hashmap"), eq(33), eq(tableQuery));
        verify(modelAttributeMapper).applyStatsPageState(model, state);
    }
}
