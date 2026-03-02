package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.dto.response.AnswerResponse;
import com.cheatsheet.quiz.api.dto.response.OptionExplanationDto;
import com.cheatsheet.quiz.api.dto.response.RelatedQuestionDto;
import com.cheatsheet.quiz.api.dto.response.SessionInfoDto;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Use-case orchestration для API-ответа `/api/answer`.
 */
@Service
public class AnswerApiService {

    private final InterviewSessionSupport sessionSupport;
    private final InterviewFacade facade;

    public AnswerApiService(
            InterviewSessionSupport sessionSupport,
            InterviewFacade facade
    ) {
        this.sessionSupport = sessionSupport;
        this.facade = facade;
    }

    public AnswerResponse buildAnswerResponse(AnswerCommand command, HttpSession session) {
        InterviewSessionSupport.AnswerSubmission submission = toSubmission(command);
        InterviewSessionSupport.AnswerContext ctx = sessionSupport.processAnswer(submission, session);
        List<OptionExplanationDto> optionExplanations = toOptionExplanations(ctx);
        List<RelatedQuestionDto> relatedQuestions = toRelatedQuestions(command.questionId(), ctx);
        SessionInfoDto sessionInfo = toSessionInfo(ctx);

        return new AnswerResponse(
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
    }

    public ResponseEntity<AnswerResponse> toHttpResponse(AnswerCommand command, HttpSession session) {
        return ResponseEntity.ok(buildAnswerResponse(command, session));
    }

    private InterviewSessionSupport.AnswerSubmission toSubmission(AnswerCommand command) {
        return new InterviewSessionSupport.AnswerSubmission(
                command.questionId(),
                command.optionId(),
                command.topic(),
                command.group(),
                command.important(),
                command.onlyWrong(),
                command.shuffle(),
                command.ordered(),
                command.confidence()
        );
    }

    private List<OptionExplanationDto> toOptionExplanations(InterviewSessionSupport.AnswerContext ctx) {
        return ctx.result().options().stream()
                .map(opt -> new OptionExplanationDto(opt.id(), opt.explanation(), opt.correct()))
                .toList();
    }

    private List<RelatedQuestionDto> toRelatedQuestions(long questionId, InterviewSessionSupport.AnswerContext ctx) {
        List<RelatedQuestion> related = facade.findRelated(questionId, ctx.result().question().topic());
        return related.stream()
                .map(rq -> new RelatedQuestionDto(rq.id(), rq.questionText(), rq.topic()))
                .toList();
    }

    private SessionInfoDto toSessionInfo(InterviewSessionSupport.AnswerContext ctx) {
        return Optional.ofNullable(ctx.interviewSession())
                .map(session -> new SessionInfoDto(
                        session.getIndex(),
                        session.getTotal(),
                        session.getCorrect(),
                        session.getWrong(),
                        session.isFinished()
                ))
                .orElse(null);
    }

    public record AnswerCommand(
            long questionId,
            long optionId,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered,
            Integer confidence
    ) {
    }
}
