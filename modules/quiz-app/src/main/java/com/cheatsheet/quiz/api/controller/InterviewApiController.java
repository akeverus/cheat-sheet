package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.mapper.StatsApiMapper;
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
import com.cheatsheet.quiz.api.dto.response.NextQuestionOptionDto;
import com.cheatsheet.quiz.api.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.api.dto.response.OptionExplanationDto;
import com.cheatsheet.quiz.api.dto.response.RegenerateResponse;
import com.cheatsheet.quiz.api.dto.response.RelatedQuestionDto;
import com.cheatsheet.quiz.api.dto.response.SessionInfoDto;
import com.cheatsheet.quiz.api.dto.response.StreakResponse;
import com.cheatsheet.quiz.api.dto.response.TakeawayResponse;
import com.cheatsheet.quiz.api.dto.response.TopicStatsResponse;
import com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.service.FavoriteService;
import com.cheatsheet.quiz.service.HintService;
import com.cheatsheet.quiz.service.InterviewFacade;
import com.cheatsheet.quiz.service.RegenerateService;
import com.cheatsheet.quiz.service.DailyStreakService;
import com.cheatsheet.quiz.util.FilterUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API-контроллер интерактивного режима интервью.
 */
@RestController
@Validated
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewApiController {

    InterviewSessionSupport sessionSupport;
    InterviewFacade facade;
    RegenerateService regenerateService;
    FavoriteService favoriteService;
    SensitiveEndpointAccessService accessService;
    DailyStreakService dailyStreakService;
    StatsApiMapper statsApiMapper;

    public InterviewApiController(
            InterviewSessionSupport sessionSupport,
            InterviewFacade facade,
            RegenerateService regenerateService,
            FavoriteService favoriteService,
            SensitiveEndpointAccessService accessService,
            DailyStreakService dailyStreakService,
            StatsApiMapper statsApiMapper
    ) {
        this.sessionSupport = sessionSupport;
        this.facade = facade;
        this.regenerateService = regenerateService;
        this.favoriteService = favoriteService;
        this.accessService = accessService;
        this.dailyStreakService = dailyStreakService;
        this.statsApiMapper = statsApiMapper;
    }

    @PostMapping("/api/answer")
    public ResponseEntity<AnswerResponse> answerApi(
            @Valid @ModelAttribute SubmitAnswerRequest request,
            HttpSession session
    ) {
        InterviewSessionSupport.AnswerContext ctx = sessionSupport.processAnswer(
                request.getQuestionId(),
                request.getOptionId(),
                request.getTopic(),
                request.getGroup(),
                request.getImportant(),
                request.getOnlyWrong(),
                request.getShuffle(),
                request.getOrdered(),
                request.getConfidence(),
                session
        );

        List<OptionExplanationDto> optionExplanations = ctx.result().options().stream()
                .map(opt -> new OptionExplanationDto(opt.id(), opt.explanation(), opt.correct()))
                .toList();

        List<RelatedQuestion> related = facade.findRelated(
                request.getQuestionId(), ctx.result().question().topic());
        List<RelatedQuestionDto> relatedQuestions = related.stream()
                .map(rq -> new RelatedQuestionDto(rq.id(), rq.questionText(), rq.topic()))
                .toList();

        SessionInfoDto sessionInfo = null;
        if (ctx.interviewSession() != null) {
            var s = ctx.interviewSession();
            sessionInfo = new SessionInfoDto(s.getIndex(), s.getTotal(), s.getCorrect(), s.getWrong(), s.isFinished());
        }

        AnswerResponse body = new AnswerResponse(
                ctx.result().correctAnswer(),
                ctx.result().correct().id(),
                ctx.result().selected().id(),
                facade.renderMarkdown(ctx.result().question().answerMarkdown()),
                optionExplanations,
                ctx.result().answerDisplayMode().name(),
                ctx.result().updatedState().repetitions(),
                relatedQuestions,
                sessionInfo
        );
        return ResponseEntity.ok(body);
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
        ResponseEntity<ApiError> forbidden = accessService.forbiddenIfUnauthorized(token, "regenerate вариантов");
        if (forbidden != null) {
            return forbidden;
        }
        if (!accessService.allowRegenerate(httpRequest)) {
            long retryAfter = accessService.regenerateRetryAfterSeconds();
            ApiError error = new ApiError(429, ApiErrorTypes.RATE_LIMIT_EXCEEDED,
                    "Слишком много запросов к /api/regenerate, повторите позже",
                    Map.of("retryAfterSeconds", retryAfter));
            return ResponseEntity.status(429)
                    .header("Retry-After", String.valueOf(retryAfter))
                    .body(error);
        }

        long questionId = request.getQuestionId();
        regenerateService.regenerateQuestion(questionId);

        return ResponseEntity.ok(new RegenerateResponse(
                true,
                questionId,
                "Варианты, подсказки и диаграмма удалены. При следующем показе будут сгенерированы заново."
        ));
    }

    @PostMapping("/api/hint")
    public ResponseEntity<HintResponse> getHint(
            @Valid @ModelAttribute HintRequest request
    ) {
        facade.ensureQuestionExists(request.getQuestionId());
        Optional<Hint> optHint = facade.getHint(request.getQuestionId(), request.levelOrDefault());
        long questionId = request.getQuestionId();
        int maxLevel = HintService.getMaxLevel();
        if (optHint.isEmpty()) {
            return ResponseEntity.ok(new HintResponse(questionId, maxLevel, null, null));
        }
        Hint hint = optHint.get();
        return ResponseEntity.ok(new HintResponse(questionId, maxLevel, hint.hintText(), hint.level()));
    }

    @PostMapping("/api/confidence")
    /**
     * Обновляет оценку уверенности пользователя по вопросу (шкала 1..5).
     */
    public ResponseEntity<ConfidenceResponse> updateConfidence(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("grade") @Min(1) @Max(5) int grade
    ) {
        facade.updateConfidence(questionId, grade);
        return ResponseEntity.ok(new ConfidenceResponse(true, questionId, grade));
    }

    @PostMapping("/api/wrong-feedback")
    /**
     * Возвращает объяснение, почему выбранный вариант ответа неверный.
     */
    public ResponseEntity<WrongFeedbackResponse> getWrongAnswerFeedback(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("optionId") @Positive long optionId
    ) {
        facade.ensureQuestionExists(questionId);
        Optional<String> feedback = facade.getWrongFeedback(questionId, optionId);
        if (feedback.isPresent()) {
            log.info("wrong_feedback_generated questionId={} optionId={}", questionId, optionId);
        } else {
            log.info("wrong_feedback_unavailable questionId={} optionId={}", questionId, optionId);
        }
        return ResponseEntity.ok(new WrongFeedbackResponse(
                questionId, optionId, feedback.orElse(null), feedback.isPresent()));
    }

    @GetMapping("/api/streak")
    public ResponseEntity<StreakResponse> getStreak() {
        return ResponseEntity.ok(dailyStreakService.getTodayProgress());
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
        InterviewFilter filter = new InterviewFilter(
                FilterUtils.normalizeTopic(topic),
                FilterUtils.normalizeGroup(group),
                important,
                onlyWrong,
                shuffle,
                ordered
        );
        return ResponseEntity.ok(statsApiMapper.toResponse(facade.getStats(filter)));
    }

    @GetMapping("/api/topic-stats")
    public ResponseEntity<List<TopicStatsResponse>> getTopicStats() {
        return ResponseEntity.ok(statsApiMapper.toTopicResponses(facade.getTopicStats()));
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
        InterviewFilter filter = new InterviewFilter(
                FilterUtils.normalizeTopic(topic),
                FilterUtils.normalizeGroup(group),
                important,
                onlyWrong,
                shuffle,
                ordered
        );
        boolean weakTopicsPriority = Boolean.TRUE.equals(weakTopics);
        Optional<com.cheatsheet.quiz.domain.InterviewQuestion> next = facade.nextQuestion(
                filter,
                weakTopicsPriority,
                excludeQuestionId
        );
        if (next.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var question = next.get();
        List<NextQuestionOptionDto> options = question.options().stream()
                .map(opt -> new NextQuestionOptionDto(opt.id(), opt.optionText()))
                .toList();
        return ResponseEntity.ok(new NextQuestionResponse(
                question.question().id(),
                question.question().questionText(),
                question.question().topic(),
                question.question().questionType() == null ? "TEXT" : question.question().questionType().name(),
                question.question().codeSnippet(),
                question.question().diagramMermaid(),
                options,
                question.reviewState().repetitions(),
                question.reviewState().correctCount(),
                question.reviewState().wrongCount()
        ));
    }

    @PostMapping("/api/favorite")
    public ResponseEntity<FavoriteResponse> toggleFavorite(
            @Valid @ModelAttribute QuestionIdRequest request
    ) {
        FavoriteService.FavoriteResult result = favoriteService.toggleFavorite(request.getQuestionId());
        return ResponseEntity.ok(new FavoriteResponse(result.favorite(), result.synced(), result.questionId()));
    }

    @GetMapping("/api/takeaway")
    public ResponseEntity<TakeawayResponse> getTakeaway(@RequestParam("questionId") @Positive long questionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> takeaway = facade.getTakeaway(questionId);
        return ResponseEntity.ok(new TakeawayResponse(takeaway.orElse(null)));
    }

    @GetMapping("/api/comparison")
    public ResponseEntity<ComparisonResponse> getComparison(
            @RequestParam("questionId") @Positive long questionId,
            @RequestParam("selectedOptionId") @Positive long selectedOptionId
    ) {
        facade.ensureQuestionExists(questionId);
        Optional<String> comparison = facade.generateComparison(questionId, selectedOptionId);
        return ResponseEntity.ok(new ComparisonResponse(comparison.orElse(null)));
    }

    @GetMapping("/api/code-trace")
    public ResponseEntity<CodeTraceResponse> getCodeTrace(@RequestParam("questionId") @Positive long questionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> trace = facade.getCodeTrace(questionId);
        return ResponseEntity.ok(new CodeTraceResponse(trace.orElse(null)));
    }

}
