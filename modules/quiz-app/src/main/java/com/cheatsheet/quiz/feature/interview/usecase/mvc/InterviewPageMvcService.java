package com.cheatsheet.quiz.feature.interview.usecase.mvc;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.mapper.request.MvcRequestMapper;
import com.cheatsheet.quiz.api.mapper.view.MvcModelAttributeMapper;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import com.cheatsheet.quiz.feature.interview.service.flow.ReviewModeService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Locale;

/**
 * Use-case orchestration для MVC page endpoint-ов.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewPageMvcService {
    InterviewSessionSupport sessionSupport;
    FocusTrainingPageService focusTrainingPageService;
    StatsPageService statsPageService;
    PauseService pauseService;
    ReviewModeService reviewModeService;
    MvcNavigationService navigationService;
    MvcModelAttributeMapper modelAttributeMapper;
    MvcRequestMapper requestMapper;
    AppProperties appProperties;

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
                session,
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
                session,
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
            String difficulty,
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
                difficulty,
                mode
        );
        return renderSettings(context, interviewSession, difficulty, model);
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
                interviewSession, topic, group, important, onlyWrong, shuffle, ordered, weakTopics, mode
        );
        return renderSettings(context, interviewSession, null, model);
    }

    private String renderSettings(
            MvcRequestMapper.SettingsRequestContext context,
            InterviewSession interviewSession,
            String difficulty,
            Model model
    ) {
        FocusTrainingPageService.SurfaceState surfaceState =
                focusTrainingPageService.buildSurfaceState(
                        context.filter(),
                        context.selectedMode(),
                        interviewSession,
                        context.weakTopicsPriority()
        );
        modelAttributeMapper.applySurfaceState(model, surfaceState);
        String selectedDifficulty = difficulty == null
                ? context.filter().difficulty() == null ? "MEDIUM" : context.filter().difficulty().name()
                : difficulty.isBlank() ? "ANY" : difficulty.strip().toUpperCase(Locale.ROOT);
        model.addAttribute("selectedDifficulty", selectedDifficulty);
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
            HttpSession session,
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
        // Метка показа вопроса для измерения времени ответа (Фаза 4): только когда
        // есть активная сессия и реальный вопрос на экране. Reload страницы
        // переставляет метку — think-time считается от последнего показа.
        if (interviewSession != null && pageState.current() != null && pageState.current().isPresent()) {
            sessionSupport.markQuestionServed(session, interviewSession);
        }
        model.addAttribute("aiEnabled", false);
        // FLOW-01b: приостановленная сессия для resume-баннера. Баннер показываем
        // только когда активной сессии нет (interviewSession == null) — гейт в шаблоне.
        model.addAttribute("pausedInfo", interviewSession != null ? null : pauseService.pausedInfo().orElse(null));
        return navigationService.focusView();
    }
}
