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
import com.cheatsheet.quiz.feature.interview.service.page.BookmarksPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.service.page.TopicsPageService;
import com.cheatsheet.quiz.feature.interview.usecase.BookmarkApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    TopicsPageService topicsPageService;
    BookmarksPageService bookmarksPageService;
    BookmarkApiService bookmarkApiService;
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
            Long questionId,
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
                questionId,
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
            Long questionId,
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
                questionId,
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
            String sort,
            String dir,
            String rows,
            Model model
    ) {
        MvcRequestMapper.StatsRequestContext context = requestMapper.resolveStatsContext(
                topic, group, important, onlyWrong, ordered, query
        );
        StatsPageService.StatsPageState state = statsPageService.build(
                context.filter(),
                context.query(),
                appProperties.getSearch().getStatsResultsLimit(),
                StatsPageService.TopicTableQuery.of(sort, dir, rows));
        modelAttributeMapper.applyStatsPageState(model, state);
        return navigationService.statsView();
    }

    /** Каталог тем: разделы банка и темы внутри них с состоянием освоения. */
    public String topics(String query, String topicState, Model model) {
        TopicsPageService.TopicsQuery catalogQuery = TopicsPageService.TopicsQuery.of(query, topicState);
        TopicsPageService.TopicsPageState state = topicsPageService.build(catalogQuery);
        model.addAttribute("catalogSections", state.sections());
        model.addAttribute("catalogTopicCount", state.topicCount());
        model.addAttribute("catalogQuestionCount", state.questionCount());
        model.addAttribute("catalogMatchedCount", state.matchedTopicCount());
        model.addAttribute("catalogQuery", state.query().q());
        model.addAttribute("catalogState", state.query().state());
        model.addAttribute("catalogFilterActive", state.query().active());
        return navigationService.topicsView();
    }

    public String bookmarks(String query, String group, String note, String sort, Model model) {
        BookmarksPageService.BookmarksPageState state = bookmarksPageService.build(
                new BookmarksPageService.BookmarksQuery(query, group, note, sort));
        modelAttributeMapper.applyBookmarksPageState(model, state);
        return navigationService.bookmarksView();
    }

    public String saveBookmarkNote(long questionId, String note, RedirectAttributes redirectAttributes) {
        bookmarkApiService.saveNote(questionId, note);
        redirectAttributes.addFlashAttribute("bookmarkSaved", true);
        return "redirect:/bookmarks#q-" + questionId;
    }

    /**
     * Заметка, написанная НА ЭКРАНЕ ВОПРОСА.
     *
     * <p>Хранилище то же самое, что у заметок закладок ({@code bookmark.note}), и это
     * не совпадение: заметка к вопросу и есть причина отложить вопрос. Поэтому
     * сохранение помечает вопрос избранным — ровно как на странице закладок; UI об
     * этом говорит прямо, а не делает молча.
     *
     * <p>Отличие от {@link #saveBookmarkNote} ровно одно — куда возвращаемся. Там
     * человек в списке закладок и остаётся в нём; здесь он в тренировке, и увести
     * его в список значило бы прервать сессию ради одной строки текста.
     */
    public String saveQuestionNote(long questionId, String note, RedirectAttributes redirectAttributes) {
        bookmarkApiService.saveNote(questionId, note);
        redirectAttributes.addFlashAttribute("questionNoteSaved", true);
        return navigationService.focusQuestionRedirect(questionId);
    }

    public String removeBookmark(long questionId, RedirectAttributes redirectAttributes) {
        bookmarkApiService.remove(questionId);
        redirectAttributes.addFlashAttribute("bookmarkRemoved", true);
        return "redirect:/bookmarks";
    }

    /**
     * Сколько тем показать в пустом разборе. Пять — это указатель «иди туда», а не
     * оглавление: длинный список на пустом экране сам становится работой.
     */
    private static final int REVIEW_HOT_TOPICS_LIMIT = 5;

    private String renderFocusPage(
            InterviewSession interviewSession,
            InterviewFilter filter,
            InterviewMode selectedMode,
            Boolean weakTopics,
            Long excludeQuestionId,
            Long questionId,
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
                excludeQuestionId,
                questionId
        );
        modelAttributeMapper.applyFocusPageState(model, pageState, reviewMode);
        // ПУСТОЙ РАЗБОР ОТВЕЧАЕТ «А ГДЕ ОШИБКИ ЕСТЬ». Экран без вопроса — единственное
        // место, где этот вопрос вообще возникает, и единственное, где за него не
        // платит показ вопроса: страница ничем другим не занята. В остальных случаях
        // список пуст и в разметку не попадает.
        model.addAttribute("reviewHotTopics",
                reviewMode && (pageState.current() == null || pageState.current().isEmpty())
                        ? focusTrainingPageService.topicsWithMistakes(REVIEW_HOT_TOPICS_LIMIT)
                        : java.util.List.of());
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
