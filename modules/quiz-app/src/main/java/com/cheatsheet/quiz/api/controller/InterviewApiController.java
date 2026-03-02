package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.api.dto.request.HintRequest;
import com.cheatsheet.quiz.api.dto.request.QuestionIdRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.dto.response.AnswerResponse;
import com.cheatsheet.quiz.api.dto.response.CodeTraceResponse;
import com.cheatsheet.quiz.api.dto.response.ComparisonResponse;
import com.cheatsheet.quiz.api.dto.response.ConfidenceResponse;
import com.cheatsheet.quiz.api.dto.response.FavoriteResponse;
import com.cheatsheet.quiz.api.dto.response.HintResponse;
import com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse;
import com.cheatsheet.quiz.api.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.api.dto.response.StreakResponse;
import com.cheatsheet.quiz.api.dto.response.TakeawayResponse;
import com.cheatsheet.quiz.api.dto.response.TopicStatsResponse;
import com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse;
import com.cheatsheet.quiz.api.mapper.ApiRequestMapper;
import com.cheatsheet.quiz.service.AnswerApiService;
import com.cheatsheet.quiz.service.ConfidenceApiService;
import com.cheatsheet.quiz.service.FavoriteApiService;
import com.cheatsheet.quiz.service.HintApiService;
import com.cheatsheet.quiz.service.NextQuestionApiService;
import com.cheatsheet.quiz.service.QuestionInsightsApiService;
import com.cheatsheet.quiz.service.RegenerateEndpointService;
import com.cheatsheet.quiz.service.StatsApiService;
import com.cheatsheet.quiz.service.StreakApiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API-контроллер интерактивного режима интервью.
 */
@RestController
@Validated
public class InterviewApiController {

    private final AnswerApiService answerApiService;
    private final HintApiService hintApiService;
    private final ConfidenceApiService confidenceApiService;
    private final NextQuestionApiService nextQuestionApiService;
    private final QuestionInsightsApiService questionInsightsApiService;
    private final RegenerateEndpointService regenerateEndpointService;
    private final FavoriteApiService favoriteApiService;
    private final StreakApiService streakApiService;
    private final StatsApiService statsApiService;
    private final ApiRequestMapper apiRequestMapper;

    public InterviewApiController(
            AnswerApiService answerApiService,
            HintApiService hintApiService,
            ConfidenceApiService confidenceApiService,
            NextQuestionApiService nextQuestionApiService,
            QuestionInsightsApiService questionInsightsApiService,
            RegenerateEndpointService regenerateEndpointService,
            FavoriteApiService favoriteApiService,
            StreakApiService streakApiService,
            StatsApiService statsApiService,
            ApiRequestMapper apiRequestMapper
    ) {
        this.answerApiService = answerApiService;
        this.hintApiService = hintApiService;
        this.confidenceApiService = confidenceApiService;
        this.nextQuestionApiService = nextQuestionApiService;
        this.questionInsightsApiService = questionInsightsApiService;
        this.regenerateEndpointService = regenerateEndpointService;
        this.favoriteApiService = favoriteApiService;
        this.streakApiService = streakApiService;
        this.statsApiService = statsApiService;
        this.apiRequestMapper = apiRequestMapper;
    }

    @PostMapping("/api/answer")
    public ResponseEntity<AnswerResponse> answerApi(
            @Valid @ModelAttribute SubmitAnswerRequest request,
            HttpSession session
    ) {
        AnswerApiService.AnswerCommand command = apiRequestMapper.toAnswerCommand(request);
        return answerApiService.toHttpResponse(command, session);
    }

    @PostMapping("/api/regenerate")
    /**
     * Принудительно сбрасывает AI-артефакты вопроса и запускает их повторную генерацию при следующем показе.
     */
    public ResponseEntity<?> regenerateOptions(
            @Valid @ModelAttribute QuestionIdRequest request,
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            HttpServletRequest httpRequest
    ) {
        RegenerateEndpointService.RegenerateResult result =
                regenerateEndpointService.execute(request.getQuestionId(), token, httpRequest);
        return regenerateEndpointService.toHttpResponse(result);
    }

    @PostMapping("/api/hint")
    public ResponseEntity<HintResponse> getHint(
            @Valid @ModelAttribute HintRequest request
    ) {
        HintApiService.HintCommand command = apiRequestMapper.toHintCommand(request);
        return hintApiService.toHttpResponse(command);
    }

    @PostMapping("/api/confidence")
    /**
     * Обновляет оценку уверенности пользователя по вопросу (шкала 1..5).
     */
    public ResponseEntity<ConfidenceResponse> updateConfidence(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("grade") @Min(1) @Max(5) int grade
    ) {
        return confidenceApiService.toHttpResponse(questionId, grade);
    }

    @PostMapping("/api/wrong-feedback")
    /**
     * Возвращает объяснение, почему выбранный вариант ответа неверный.
     */
    public ResponseEntity<WrongFeedbackResponse> getWrongAnswerFeedback(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("optionId") @Positive long optionId
    ) {
        return questionInsightsApiService.toWrongFeedbackHttpResponse(questionId, optionId);
    }

    @GetMapping("/api/streak")
    public ResponseEntity<StreakResponse> getStreak() {
        return streakApiService.toHttpResponse();
    }

    @GetMapping("/api/stats")
    public ResponseEntity<InterviewStatsResponse> getStats(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "ordered", required = false) Boolean ordered
    ) {
        StatsApiService.StatsCommand command =
                apiRequestMapper.toStatsCommand(topic, group, important, onlyWrong, shuffle, ordered);
        return statsApiService.toStatsHttpResponse(command);
    }

    @GetMapping("/api/topic-stats")
    public ResponseEntity<List<TopicStatsResponse>> getTopicStats() {
        return statsApiService.toTopicStatsHttpResponse();
    }

    @GetMapping("/api/next")
    /**
     * Возвращает следующий вопрос по активным фильтрам или 204, если кандидатов нет.
     */
    public ResponseEntity<NextQuestionResponse> getNextQuestion(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "important", required = false) Boolean important,
            @RequestParam(value = "onlyWrong", required = false) Boolean onlyWrong,
            @RequestParam(value = "shuffle", required = false) Boolean shuffle,
            @RequestParam(value = "weakTopics", required = false) Boolean weakTopics,
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            @RequestParam(value = "excludeQuestionId", required = false) Long excludeQuestionId
    ) {
        NextQuestionApiService.NextQuestionCommand command = apiRequestMapper.toNextQuestionCommand(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId
        );
        return nextQuestionApiService.toHttpResponse(command);
    }

    @PostMapping("/api/favorite")
    public ResponseEntity<FavoriteResponse> toggleFavorite(
            @Valid @ModelAttribute QuestionIdRequest request
    ) {
        return favoriteApiService.toHttpResponse(request.getQuestionId());
    }

    @GetMapping("/api/takeaway")
    public ResponseEntity<TakeawayResponse> getTakeaway(@RequestParam("questionId") @Positive long questionId) {
        return questionInsightsApiService.toTakeawayHttpResponse(questionId);
    }

    @GetMapping("/api/comparison")
    public ResponseEntity<ComparisonResponse> getComparison(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("selectedOptionId") @Positive long selectedOptionId
    ) {
        return questionInsightsApiService.toComparisonHttpResponse(questionId, selectedOptionId);
    }

    @GetMapping("/api/code-trace")
    public ResponseEntity<CodeTraceResponse> getCodeTrace(@RequestParam("questionId") @Positive long questionId) {
        return questionInsightsApiService.toCodeTraceHttpResponse(questionId);
    }

}
