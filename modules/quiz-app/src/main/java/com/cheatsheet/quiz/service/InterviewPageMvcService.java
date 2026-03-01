package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.mapper.MvcModelAttributeMapper;
import com.cheatsheet.quiz.api.mapper.MvcRequestMapper;
import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * Use-case orchestration для MVC page endpoint-ов.
 */
@Service
public class InterviewPageMvcService {

    private final InterviewSessionSupport sessionSupport;
    private final FocusTrainingPageService focusTrainingPageService;
    private final StatsPageService statsPageService;
    private final ReviewModeService reviewModeService;
    private final MvcNavigationService navigationService;
    private final MvcModelAttributeMapper modelAttributeMapper;
    private final MvcRequestMapper requestMapper;
    private final AppProperties appProperties;

    public InterviewPageMvcService(
            InterviewSessionSupport sessionSupport,
            FocusTrainingPageService focusTrainingPageService,
            StatsPageService statsPageService,
            ReviewModeService reviewModeService,
            MvcNavigationService navigationService,
            MvcModelAttributeMapper modelAttributeMapper,
            MvcRequestMapper requestMapper,
            AppProperties appProperties
    ) {
        this.sessionSupport = sessionSupport;
        this.focusTrainingPageService = focusTrainingPageService;
        this.statsPageService = statsPageService;
        this.reviewModeService = reviewModeService;
        this.navigationService = navigationService;
        this.modelAttributeMapper = modelAttributeMapper;
        this.requestMapper = requestMapper;
        this.appProperties = appProperties;
    }

    public String index(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean weakTopics,
            Boolean ordered,
            Long excludeQuestionId,
            String mode,
            HttpSession session,
            Model model
    ) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        InterviewMode selectedMode = requestMapper.resolveMode(interviewSession, mode);
        InterviewFilter filter = requestMapper.resolveFilter(
                interviewSession, topic, group, important, onlyWrong, shuffle, ordered
        );
        return renderFocusPage(
                interviewSession,
                filter,
                selectedMode,
                weakTopics,
                excludeQuestionId,
                false,
                model
        );
    }

    public String review(
            String topic,
            String group,
            Boolean important,
            Boolean shuffle,
            Boolean weakTopics,
            Boolean ordered,
            Long excludeQuestionId,
            String mode,
            HttpSession session,
            Model model
    ) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        InterviewMode selectedMode = requestMapper.resolveMode(interviewSession, mode);
        InterviewFilter baseFilter = requestMapper.resolveReviewBaseFilter(
                topic, group, important, shuffle, ordered
        );
        InterviewFilter reviewFilter = reviewModeService.apply(baseFilter);
        return renderFocusPage(
                interviewSession,
                reviewFilter,
                selectedMode,
                weakTopics,
                excludeQuestionId,
                true,
                model
        );
    }

    public String settings(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean weakTopics,
            Boolean ordered,
            String mode,
            HttpSession session,
            Model model
    ) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        MvcRequestMapper.SettingsRequestContext context = requestMapper.resolveSettingsContext(
                interviewSession,
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                ordered,
                weakTopics,
                mode
        );
        FocusTrainingPageService.SurfaceState surfaceState =
                focusTrainingPageService.buildSurfaceState(
                        context.filter(),
                        context.selectedMode(),
                        interviewSession,
                        context.weakTopicsPriority()
                );
        modelAttributeMapper.applySurfaceState(model, surfaceState);
        return navigationService.settingsView();
    }

    public String stats(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean ordered,
            String query,
            Model model
    ) {
        MvcRequestMapper.StatsRequestContext context = requestMapper.resolveStatsContext(
                topic, group, important, onlyWrong, ordered, query
        );
        StatsPageService.StatsPageState state =
                statsPageService.build(context.filter(), context.query(), appProperties.getSearch().getStatsResultsLimit());
        modelAttributeMapper.applyStatsPageState(model, state);
        return navigationService.statsView();
    }

    private String renderFocusPage(
            InterviewSession interviewSession,
            InterviewFilter filter,
            InterviewMode selectedMode,
            Boolean weakTopics,
            Long excludeQuestionId,
            boolean reviewMode,
            Model model
    ) {
        boolean weakTopicsPriority = Boolean.TRUE.equals(weakTopics);
        FocusTrainingPageService.FocusPageState pageState = focusTrainingPageService.buildFocusPageState(
                interviewSession,
                filter,
                selectedMode,
                weakTopicsPriority,
                excludeQuestionId
        );
        modelAttributeMapper.applyFocusPageState(model, pageState, reviewMode);
        return navigationService.focusView();
    }
}
