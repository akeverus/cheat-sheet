package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.api.dto.request.interview.QuestionIdRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.api.mapper.request.ApiRequestMapper;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import com.cheatsheet.quiz.feature.interview.usecase.ConfidenceApiService;
import com.cheatsheet.quiz.feature.interview.usecase.FavoriteApiService;
import com.cheatsheet.quiz.feature.interview.usecase.NextQuestionApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StatsApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StreakApiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API-контроллер интерактивного режима интервью.
 */
@RestController
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewApiController {
    AnswerApiService answerApiService;
    ConfidenceApiService confidenceApiService;
    NextQuestionApiService nextQuestionApiService;
    FavoriteApiService favoriteApiService;
    StreakApiService streakApiService;
    StatsApiService statsApiService;
    ApiRequestMapper apiRequestMapper;

    @PostMapping("/api/answer")
    public ResponseEntity<AnswerResponse> answerApi(
            @Valid @ModelAttribute SubmitAnswerRequest request,
            HttpSession session
    ) {
        AnswerApiService.AnswerCommand command = apiRequestMapper.toAnswerCommand(request);
        return answerApiService.toHttpResponse(command, session);
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

}
