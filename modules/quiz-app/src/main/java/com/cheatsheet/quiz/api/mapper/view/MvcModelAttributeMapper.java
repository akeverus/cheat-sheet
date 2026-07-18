package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.common.util.ModeUtils;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.infrastructure.render.AnswerHtmlSplitter;
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
    private final ModeUtils modeUtils;

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
        if (pageState.reviewReason() != null) {
            model.addAttribute("reviewReason", pageState.reviewReason());
        }
        model.addAttribute("progressPercent", pageState.progressPercent());
        model.addAttribute("reviewMode", reviewMode);
        InterviewMode mode = pageState.surface().mode();
        // flashcardMode — фактическое состояние UI: вопрос без вариантов форсит
        // флешкарту при любом session-mode. Чип/подсказка должны отражать именно
        // его, иначе над флешкардой висело «Выбери один вариант» (MCQ-копирайт).
        boolean flashcard = pageState.flashcardMode();
        model.addAttribute("focusModeChipText", resolveFocusModeChipText(reviewMode, mode, flashcard));
        model.addAttribute("focusModeHintText", resolveFocusModeHintText(reviewMode, mode, flashcard));
        model.addAttribute("focusEmptyRetryHref", resolveFocusEmptyRetryHref(reviewMode, mode));
        model.addAttribute("focusEmptyRetryText", resolveFocusEmptyRetryText(reviewMode, mode));
    }

    /**
     * Текст eyebrow-чипа фокус-страницы. Раньше возвращал англицизмы
     * («Focus mode»/«Study mode»/…) в полностью русском UI и не различал режимы:
     * EXAM/MARATHON/TRAINING все показывали «Focus mode» (критика round-01
     * C-live-3). Теперь — русское имя реального режима из {@link ModeUtils}
     * (единый словарь, как на /settings и в итогах), с приоритетом
     * review/flashcard над session-mode (flashcard форсится при вопросе без
     * вариантов независимо от режима сессии).
     */
    private String resolveFocusModeChipText(boolean reviewMode, InterviewMode mode, boolean flashcard) {
        if (reviewMode) {
            return "Повтор ошибок";
        }
        if (flashcard || mode == InterviewMode.FLASHCARD) {
            return "Флешкарты";
        }
        if (mode == null) {
            return "Тренировка";
        }
        return modeUtils.displayName(mode);
    }

    private String resolveFocusModeHintText(boolean reviewMode, InterviewMode mode, boolean flashcard) {
        if (reviewMode) {
            return "Режим review: отвечай на вопросы с ошибками.";
        }
        if (flashcard || mode == InterviewMode.FLASHCARD) {
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
        // UX-13: краткий лид (первый абзац разбора) видим под вердиктом, остаток —
        // под «Подробнее». Деривация та же, что в API-пути (AnswerApiService) —
        // общий AnswerHtmlSplitter, чтобы JS- и no-JS-разбор совпадали. В проде
        // result на странице ответа всегда есть; null-guard — на случай контрактных
        // юнит-тестов маппера с «пустым» state.
        if (state.result() != null) {
            AnswerHtmlSplitter.Split answerSplit = AnswerHtmlSplitter.split(state.answerHtml());
            boolean leadPresent = !answerSplit.leadHtml().isBlank();
            boolean restPresent = leadPresent && !answerSplit.restHtml().isBlank();
            boolean anyOptionExplanation = state.result().options().stream()
                    .anyMatch(o -> o.explanation() != null && !o.explanation().isBlank());
            // Хендофф-3, Этап 4: distractor-панель («Почему другие варианты не подходят»)
            // показываем, только если хотя бы у одного НЕверного варианта есть пояснение.
            // Флаг из Java, а не Thymeleaf-проекции (ссылка на correct.id внутри ?[] —
            // хрупкий скоуп элемента).
            long correctOptionId = state.result().correct() != null ? state.result().correct().id() : -1L;
            boolean anyDistractorExplanation = state.result().options().stream()
                    .anyMatch(o -> o.id() != correctOptionId && o.explanation() != null && !o.explanation().isBlank());
            model.addAttribute("answerLeadHtml", answerSplit.leadHtml());
            model.addAttribute("answerRestHtml", answerSplit.restHtml());
            // «Подробнее» показываем, только если под ним реально что-то есть:
            // остаток разбора (после лид-абзаца) или пояснения вариантов.
            model.addAttribute("answerHasDetail", restPresent || anyOptionExplanation);
            model.addAttribute("hasDistractorAnalysis", anyDistractorExplanation);
            model.addAttribute("correctOptionLetter", correctOptionLetter(state.result()));
        }
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
        model.addAttribute("aiEnabled", false);
        model.addAttribute("reviewState", state.reviewState());
    }

    /**
     * Буква правильного варианта (A/B/C/…) = 1-based позиция в списке вариантов
     * (тот же порядок, что нумерует опции CSS-счётчиком). Зеркалит
     * {@code AnswerApiService.correctOptionLetter} для no-JS SSR-пути.
     */
    private String correctOptionLetter(AnswerResult result) {
        java.util.List<AnswerOption> options = result.options();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).id() == result.correct().id()) {
                return String.valueOf((char) ('A' + i));
            }
        }
        return "";
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
        model.addAttribute("coverageGaps", state.coverageGaps());
        model.addAttribute("reviewForecast", state.reviewForecast());
        model.addAttribute("metrics", state.metrics());
        // Передаём max(count) уже вычисленный — Thymeleaf 3.1 запрещает
        // T(java.util.Collections) в SpEL, считать в шаблоне нельзя.
        long maxForecast = state.reviewForecast() == null ? 1L
                : state.reviewForecast().stream()
                        .mapToLong(com.cheatsheet.quiz.persistence.QuestionStatsRepository.ForecastDay::count)
                        .max().orElse(1L);
        model.addAttribute("reviewForecastMax", Math.max(maxForecast, 1L));
    }

    /**
     * Маппит summary завершенной сессии.
     */
    public void applySessionSummary(Model model, SessionSummary summary) {
        model.addAttribute("summary", summary);
    }
}
