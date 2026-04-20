package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Централизованный mapper заполнения MVC-модели атрибутами.
 *
 * <p>Убирает дублирование {@code model.addAttribute(...)} из контроллеров
 * и фиксирует единый контракт атрибутов для шаблонов.</p>
 */
@Component
@RequiredArgsConstructor
public class MvcModelAttributeMapper {

    private final AppProperties appProperties;

    /**
     * Маппит общий surface-state (фильтры, топики, статистика) в модель.
     */
    public void applySurfaceState(Model model, FocusTrainingPageService.SurfaceState surfaceState) {
        model.addAttribute("stats", surfaceState.stats());
        model.addAttribute("topics", surfaceState.topics());
        model.addAttribute("groups", surfaceState.groups());
        model.addAttribute("selectedGroup", surfaceState.selectedGroup());
        model.addAttribute("filter", surfaceState.filter());
        model.addAttribute("mode", surfaceState.mode());
        model.addAttribute("interviewSession", surfaceState.interviewSession());
        model.addAttribute("weakTopics", surfaceState.weakTopics());
    }

    /**
     * Маппит state главной страницы фокус-тренировки.
     */
    public void applyFocusPageState(Model model, FocusTrainingPageService.FocusPageState pageState, boolean reviewMode) {
        applySurfaceState(model, pageState.surface());
        model.addAttribute("current", pageState.current().orElse(null));
        model.addAttribute("generationUnavailable", pageState.generationUnavailable());
        model.addAttribute("studyLearnPhase", pageState.studyLearnPhase());
        model.addAttribute("flashcardMode", pageState.flashcardMode());
        model.addAttribute("flashcardRevealed", pageState.flashcardRevealed());
        if (pageState.diagram() != null) {
            model.addAttribute("diagram", pageState.diagram());
        }
        if (pageState.studyAnswerHtml() != null) {
            model.addAttribute("studyAnswerHtml", pageState.studyAnswerHtml());
        }
        if (pageState.difficulty() != null) {
            model.addAttribute("difficulty", pageState.difficulty());
        }
        model.addAttribute("progressPercent", pageState.progressPercent());
        model.addAttribute("reviewMode", reviewMode);
        InterviewMode mode = pageState.surface().mode();
        model.addAttribute("focusModeChipText", resolveFocusModeChipText(reviewMode, mode));
        model.addAttribute("focusModeHintText", resolveFocusModeHintText(reviewMode, mode));
        model.addAttribute("focusEmptyRetryHref", resolveFocusEmptyRetryHref(reviewMode, mode));
        model.addAttribute("focusEmptyRetryText", resolveFocusEmptyRetryText(reviewMode, mode));
    }

    private String resolveFocusModeChipText(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "Review mode";
        }
        if (mode == InterviewMode.FLASHCARD) {
            return "Flashcard mode";
        }
        if (mode == InterviewMode.STUDY) {
            return "Study mode";
        }
        return "Focus mode";
    }

    private String resolveFocusModeHintText(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "Режим review: отвечай на вопросы с ошибками.";
        }
        if (mode == InterviewMode.FLASHCARD) {
            return "Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.";
        }
        if (mode == InterviewMode.STUDY) {
            return "Изучение: разберись с материалом, затем переходи к проверке.";
        }
        return "Выбери один вариант. Проверка и разбор идут по шагам.";
    }

    private String resolveFocusEmptyRetryHref(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "/review";
        }
        if (mode == InterviewMode.FLASHCARD) {
            return "/training?mode=FLASHCARD";
        }
        if (mode == InterviewMode.STUDY) {
            return "/training?mode=STUDY";
        }
        return "/training";
    }

    private String resolveFocusEmptyRetryText(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "Обновить review";
        }
        if (mode == InterviewMode.FLASHCARD) {
            return "Обновить флешкарты";
        }
        if (mode == InterviewMode.STUDY) {
            return "Обновить изучение";
        }
        return "Обновить тренировку";
    }

    /**
     * Маппит state страницы результата ответа.
     */
    public void applyAnswerPageState(Model model, AnswerPageService.AnswerPageState state) {
        model.addAttribute("result", state.result());
        model.addAttribute("answerHtml", state.answerHtml());
        model.addAttribute("answerDisplayMode", state.answerDisplayMode());
        model.addAttribute("stats", state.stats());
        model.addAttribute("topics", state.topics());
        model.addAttribute("groups", state.groups());
        model.addAttribute("selectedGroup", state.selectedGroup());
        model.addAttribute("filter", state.filter());
        model.addAttribute("mode", state.mode());
        model.addAttribute("interviewSession", state.interviewSession());
        if (state.diagram() != null) {
            model.addAttribute("diagram", state.diagram());
        }
        model.addAttribute("relatedQuestions", state.relatedQuestions());
        model.addAttribute("aiEnabled", appProperties.isAiEnabled());
    }

    /**
     * Маппит state страницы статистики.
     */
    public void applyStatsPageState(Model model, StatsPageService.StatsPageState state) {
        model.addAttribute("stats", state.stats());
        model.addAttribute("topics", state.topics());
        model.addAttribute("groups", state.groups());
        model.addAttribute("selectedGroup", state.selectedGroup());
        model.addAttribute("filter", state.filter());
        model.addAttribute("searchQuery", state.searchQuery());
        model.addAttribute("searchResults", state.searchResults());
        model.addAttribute("topicStats", state.topicStats());
        model.addAttribute("topicStatsJson", state.topicStatsJson());
    }

    /**
     * Маппит summary завершенной сессии.
     */
    public void applySessionSummary(Model model, SessionSummary summary) {
        model.addAttribute("summary", summary);
    }
}
