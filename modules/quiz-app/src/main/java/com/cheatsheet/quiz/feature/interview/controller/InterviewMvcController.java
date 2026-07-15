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
import jakarta.servlet.http.HttpSession;
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

/**
 * MVC-контроллер test flow: старт/завершение сессии, ответы и статистика.
 */
@Controller
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewMvcController {
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
                mode,
                session,
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
        return renderTrainingEntry(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId,
                mode,
                session,
                model
        );
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
        return interviewPageMvcService.review(
                topic,
                group,
                important,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId,
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
                mode,
                session,
                model
        );
    }

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
            Model model
    ) {
        return interviewPageMvcService.stats(
                topic,
                group,
                important,
                onlyWrong,
                ordered,
                query,
                model
        );
    }

}
