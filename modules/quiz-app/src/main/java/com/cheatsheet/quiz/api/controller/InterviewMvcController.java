package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.api.dto.request.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.MvcModelAttributeMapper;
import com.cheatsheet.quiz.api.mapper.MvcRequestMapper;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.service.FocusTrainingPageService;
import com.cheatsheet.quiz.service.InterviewFlowMvcService;
import com.cheatsheet.quiz.service.ReviewModeService;
import com.cheatsheet.quiz.service.StatsPageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;

/**
 * MVC-контроллер test flow: старт/завершение сессии, ответы и статистика.
 */
@Controller
@Validated
public class InterviewMvcController {

    private final InterviewSessionSupport sessionSupport;
    private final FocusTrainingPageService focusTrainingPageService;
    private final StatsPageService statsPageService;
    private final ReviewModeService reviewModeService;
    private final MvcNavigationService navigationService;
    private final MvcModelAttributeMapper modelAttributeMapper;
    private final MvcRequestMapper requestMapper;
    private final AppProperties appProperties;
    private final InterviewFlowMvcService interviewFlowMvcService;

    public InterviewMvcController(
            InterviewSessionSupport sessionSupport,
            FocusTrainingPageService focusTrainingPageService,
            StatsPageService statsPageService,
            ReviewModeService reviewModeService,
            MvcNavigationService navigationService,
            MvcModelAttributeMapper modelAttributeMapper,
            MvcRequestMapper requestMapper,
            AppProperties appProperties,
            InterviewFlowMvcService interviewFlowMvcService
    ) {
        this.sessionSupport = sessionSupport;
        this.focusTrainingPageService = focusTrainingPageService;
        this.statsPageService = statsPageService;
        this.reviewModeService = reviewModeService;
        this.navigationService = navigationService;
        this.modelAttributeMapper = modelAttributeMapper;
        this.requestMapper = requestMapper;
        this.appProperties = appProperties;
        this.interviewFlowMvcService = interviewFlowMvcService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "weakTopics", required = false) Boolean weakTopics,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "excludeQuestionId", required = false) Long excludeQuestionId,
            @RequestParam(value = "mode", required = false) String mode,
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

    @GetMapping("/training")
    public String training(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "weakTopics", required = false) Boolean weakTopics,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "excludeQuestionId", required = false) Long excludeQuestionId,
            @RequestParam(value = "mode", required = false) String mode,
            HttpSession session,
            Model model
    ) {
        return index(topic, group, important, onlyWrong, shuffle, weakTopics, ordered, excludeQuestionId, mode, session, model);
    }

    @GetMapping("/review")
    public String review(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "weakTopics", required = false) Boolean weakTopics,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "excludeQuestionId", required = false) Long excludeQuestionId,
            @RequestParam(value = "mode", required = false) String mode,
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

    @GetMapping("/settings")
    public String settings(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "weakTopics", required = false) Boolean weakTopics,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "mode", required = false) String mode,
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

    @PostMapping("/start")
    public String start(
            @Valid @ModelAttribute StartSessionRequest request,
            HttpSession session
    ) {
        return interviewFlowMvcService.start(request, session);
    }

    @PostMapping("/study-confirm")
    public String studyConfirm(HttpSession session) {
        return interviewFlowMvcService.studyConfirm(session);
    }

    @PostMapping("/flashcard-reveal")
    public String flashcardReveal(HttpSession session) {
        return interviewFlowMvcService.flashcardReveal(session);
    }

    @PostMapping("/flashcard-grade")
    public String flashcardGrade(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("grade") @Min(0) @Max(5) int grade,
            HttpSession session
    ) {
        return interviewFlowMvcService.flashcardGrade(questionId, grade, session);
    }

    @PostMapping("/finish")
    public String finish(HttpSession session) {
        return interviewFlowMvcService.finish(session);
    }

    @GetMapping("/session-summary")
    public String sessionSummary(HttpSession session, Model model) {
        return interviewFlowMvcService.sessionSummary(session, model);
    }

    @PostMapping("/answer")
    public String answer(
            @Valid @ModelAttribute SubmitAnswerRequest request,
            HttpSession session,
            Model model
    ) {
        return interviewFlowMvcService.answer(request, session, model);
    }

    @GetMapping("/stats")
    public String stats(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "q", required = false) String query,
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
