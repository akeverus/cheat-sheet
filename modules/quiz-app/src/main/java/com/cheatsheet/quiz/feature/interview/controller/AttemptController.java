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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Каноничный REST-ресурс попыток сессии (хендофф-3, Этап 9):
 * {@code POST /api/sessions/{sessionId}/attempts}.
 *
 * <p>Тонкий фасад: делегирует в {@link AnswerApiService} — ту же application-логику,
 * что обслуживает {@code /api/answer}. Так соблюдён REST-контракт хендоффа без
 * дублирования бизнес-логики; идемпотентность по {@code clientAttemptId}, начисление
 * XP и следующий интервал повторения приходят «из коробки». Ошибки этого пути
 * ({@code /api/**}) отдаются как {@code ProblemDetail} с {@code correlationId}
 * (см. {@code GlobalExceptionHandler}). {@code sessionId} в пути — RESTful-идентификатор
 * сессии; состоянием тренажёра управляет HTTP-сессия.</p>
 */
@RestController
@RequestMapping("/api/sessions/{sessionId}/attempts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptController {

    AnswerApiService answerApiService;

    @PostMapping
    public ResponseEntity<AnswerResponse> submit(@PathVariable String sessionId,
                                                 @Valid @ModelAttribute SubmitAttemptRequest request,
                                                 HttpSession session) {
        AnswerApiService.AnswerCommand command = new AnswerApiService.AnswerCommand(
                request.getQuestionId(),
                request.getSelectedOptionId(),
                null, null, null, null, null, null,
                request.getConfidence(),
                request.getClientAttemptId(),
                request.getOpenedContentBlockIds());
        return answerApiService.toHttpResponse(command, session);
    }
}
