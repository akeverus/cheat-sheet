package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.api.dto.request.interview.SubmitAttemptRequest;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Каноничный REST-ресурс попыток (BE-003, модель B):
 * {@code POST /api/attempts} — cookie-сессия, без декоративного {@code {sessionId}} в path.
 *
 * <p>Тонкий фасад: делегирует в {@link AnswerApiService}. Идемпотентность по
 * {@code clientAttemptId}, XP и SM-2 приходят из общего пайплайна. Ошибки
 * {@code /api/**} — {@code ProblemDetail} с {@code correlationId}.</p>
 */
@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptController {

    AnswerApiService answerApiService;

    @PostMapping
    public ResponseEntity<AnswerResponse> submit(@Valid @ModelAttribute SubmitAttemptRequest request,
                                                 HttpSession session) {
        AnswerApiService.AnswerCommand command = AnswerApiService.AnswerCommand.builder()
                .questionId(request.getQuestionId())
                .optionId(request.getSelectedOptionId())
                .confidence(request.getConfidence())
                .clientAttemptId(request.getClientAttemptId())
                .openedContentBlockIds(request.getOpenedContentBlockIds())
                .responseTimeMs(request.getResponseTimeMs())
                .questionRevisionId(request.getQuestionRevisionId())
                .build();
        return answerApiService.toHttpResponse(command, session);
    }
}
