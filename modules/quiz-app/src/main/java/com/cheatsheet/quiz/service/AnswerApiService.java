package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.dto.response.AnswerResponse;
import com.cheatsheet.quiz.api.dto.response.OptionExplanationDto;
import com.cheatsheet.quiz.api.dto.response.RelatedQuestionDto;
import com.cheatsheet.quiz.api.dto.response.SessionInfoDto;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

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
        InterviewSessionSupport.AnswerContext ctx = sessionSupport.processAnswer(
                command.questionId(),
                command.optionId(),
                command.topic(),
                command.group(),
                command.important(),
                command.onlyWrong(),
                command.shuffle(),
                command.ordered(),
                command.confidence(),
                session
        );

        List<OptionExplanationDto> optionExplanations = ctx.result().options().stream()
                .map(opt -> new OptionExplanationDto(opt.id(), opt.explanation(), opt.correct()))
                .toList();

        List<RelatedQuestion> related = facade.findRelated(command.questionId(), ctx.result().question().topic());
        List<RelatedQuestionDto> relatedQuestions = related.stream()
                .map(rq -> new RelatedQuestionDto(rq.id(), rq.questionText(), rq.topic()))
                .toList();

        SessionInfoDto sessionInfo = null;
        if (ctx.interviewSession() != null) {
            var s = ctx.interviewSession();
            sessionInfo = new SessionInfoDto(s.getIndex(), s.getTotal(), s.getCorrect(), s.getWrong(), s.isFinished());
        }

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
