package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.feature.admin.service.AdminMaintenanceService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewFlowMvcService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewPageMvcService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * MVC-контроллер test flow: старт/завершение сессии, ответы и статистика.
 */
@Controller
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewMvcController {
    /** Параметры выборки, которые переносим с легаси-адреса {@code /training} на канонический {@code /}. */
    static final List<String> TRAINING_QUERY_PARAMS = List.of(
            "topic", "group", "important", "onlyWrong", "shuffle",
            "weakTopics", "ordered", "excludeQuestionId", "questionId", "mode");

    InterviewPageMvcService interviewPageMvcService;
    InterviewFlowMvcService interviewFlowMvcService;
    AdminMaintenanceService adminMaintenanceService;

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
            @RequestParam(value = "questionId", required = false) Long questionId,
            @RequestParam(value = "mode", required = false) String mode,
            HttpSession session,
            Model model
    ) {
        return renderTrainingEntry(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId,
                questionId,
                mode,
                session,
                model
        );
    }

    /**
     * Легаси-адрес тренировки. Канонический URL один — {@code /}: иначе один и
     * тот же экран живёт по двум адресам, что размывает историю браузера,
     * закладки и аналитику. Старые ссылки не ломаем — редиректим, перенося
     * известные параметры выборки (whitelist, чтобы не тащить чужой query).
     */
    @GetMapping("/training")
    public String training(HttpServletRequest request) {
        UriComponentsBuilder canonical = UriComponentsBuilder.fromPath("/");
        for (String param : TRAINING_QUERY_PARAMS) {
            String value = request.getParameter(param);
            if (value != null && !value.isBlank()) {
                canonical.queryParam(param, value);
            }
        }
        return "redirect:" + canonical.build().encode().toUriString();
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
            @RequestParam(value = "questionId", required = false) Long questionId,
            @RequestParam(value = "mode", required = false) String mode,
            HttpSession session,
            Model model
    ) {
        return interviewPageMvcService.review(
                topic,
                group,
                important,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId,
                questionId,
                mode,
                session,
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
        return interviewPageMvcService.settings(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                mode,
                session,
                model
        );
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

    /**
     * Раскрытие эталонного ответа во флешкарт-режиме. Введённый пользователем
     * черновик ({@code typedAnswer} в форме) сервер намеренно не принимает и не
     * персистит — эхо «Твой ответ» делает клиент через sessionStorage
     * ({@code quiz/flashcard.js}, progressive enhancement).
     */
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

    @PostMapping("/skip")
    public String skip(
            @RequestParam("questionId") @Positive long questionId,
            HttpSession session
    ) {
        return interviewFlowMvcService.skip(questionId, session);
    }

    @PostMapping("/pause")
    public String pause(HttpSession session) {
        return interviewFlowMvcService.pause(session);
    }

    @PostMapping("/resume")
    public String resume(HttpSession session) {
        return interviewFlowMvcService.resume(session);
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

    private String renderTrainingEntry(
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
        return interviewPageMvcService.index(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId,
                questionId,
                mode,
                session,
                model
        );
    }

    /**
     * Очистка банка вариантов. Доступ гейтит security-цепочка
     * ({@code SecurityConfig#hasSensitiveAccess}: валидный {@code X-Admin-Token}
     * или {@code ROLE_ADMIN}) — контроллер повторную проверку не делает.
     */
    @PostMapping("/settings/reset-options")
    public String resetOptions(RedirectAttributes redirectAttributes) {
        int deleted = adminMaintenanceService.clearOptions();
        redirectAttributes.addFlashAttribute("resetDeleted", deleted);
        return "redirect:/settings";
    }

    @GetMapping("/stats")
    public String stats(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            @RequestParam(value = "rows", required = false) String rows,
            Model model
    ) {
        return interviewPageMvcService.stats(
                topic,
                group,
                important,
                onlyWrong,
                ordered,
                query,
                sort,
                dir,
                rows,
                model
        );
    }

    /**
     * Каталог тем — полноценный адрес первого уровня, а не якорь внутри аналитики.
     *
     * <p>Отбор живёт в адресе, а не в скрипте: страницу с найденным набором можно
     * сохранить и переслать, а без JS форма работает обычной GET-отправкой.
     */
    @GetMapping("/topics")
    public String topics(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "state", required = false) String topicState,
            Model model) {
        return interviewPageMvcService.topics(query, topicState, model);
    }

    @GetMapping("/bookmarks")
    public String bookmarks(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "sort", required = false) String sort,
            Model model
    ) {
        return interviewPageMvcService.bookmarks(query, group, note, sort, model);
    }

    @PostMapping("/bookmarks/note")
    public String saveBookmarkNote(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam(value = "note", required = false) String note,
            RedirectAttributes redirectAttributes
    ) {
        return interviewPageMvcService.saveBookmarkNote(questionId, note, redirectAttributes);
    }

    /**
     * Заметка к вопросу с экрана тренировки: сохранить и вернуться к тому же вопросу.
     *
     * <p>Форма — обычная, без JS: на экране вопроса заметку пишут в самый неудобный
     * для скриптов момент (посреди сессии), и терять текст из-за выключенного JS
     * недопустимо. Скрипт перехватывает отправку и сохраняет через тот же API, что
     * заметки закладок, — одна реализация на два экрана.
     */
    @PostMapping("/question/note")
    public String saveQuestionNote(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam(value = "note", required = false) String note,
            RedirectAttributes redirectAttributes
    ) {
        return interviewPageMvcService.saveQuestionNote(questionId, note, redirectAttributes);
    }

    @PostMapping("/bookmarks/remove")
    public String removeBookmark(
            @RequestParam("questionId") @Positive long questionId,
            RedirectAttributes redirectAttributes
    ) {
        return interviewPageMvcService.removeBookmark(questionId, redirectAttributes);
    }

}
