package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.OptionExplanationDto;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.RelatedQuestionDto;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.SessionInfoDto;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.infrastructure.render.AnswerHtmlSplitter;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

/**
 * Use-case orchestration для API-ответа `/api/answer`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerApiService {
    InterviewSessionSupport sessionSupport;
    InterviewFacade facade;

    public AnswerResponse buildAnswerResponse(AnswerCommand command, HttpSession session) {
        InterviewSessionSupport.AnswerSubmission submission = toSubmission(command);
        InterviewSessionSupport.AnswerContext ctx = sessionSupport.processAnswer(submission, session);
        List<OptionExplanationDto> optionExplanations = toOptionExplanations(ctx);
        List<RelatedQuestionDto> relatedQuestions = toRelatedQuestions(command.questionId(), ctx);
        SessionInfoDto sessionInfo = toSessionInfo(ctx);

        String answerHtml = facade.renderMarkdown(ctx.result().question().answerMarkdown());
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split(answerHtml);

        return new AnswerResponse(
                ctx.result().correctAnswer(),
                ctx.result().correct().id(),
                ctx.result().selected().id(),
                correctOptionLetter(ctx.result()),
                answerHtml,
                split.leadHtml(),
                split.restHtml(),
                optionExplanations,
                ctx.result().answerDisplayMode().name(),
                ctx.result().updatedState().repetitions(),
                relatedQuestions,
                sessionInfo
        );
    }

    /**
     * Буква правильного варианта (A/B/C/…) = 1-based позиция в списке вариантов,
     * т.е. тот же порядок, в котором фронт нумерует опции CSS-счётчиком. Пустая
     * строка — если правильный не найден в списке (не должно случаться).
     */
    private String correctOptionLetter(AnswerResult result) {
        List<AnswerOption> options = result.options();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).id() == result.correct().id()) {
                return String.valueOf((char) ('A' + i));
            }
        }
        return "";
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
                command.confidence(),
                command.clientAttemptId()
        );
    }

    private List<OptionExplanationDto> toOptionExplanations(InterviewSessionSupport.AnswerContext ctx) {
        return ctx.result().options().stream()
                .map(opt -> new OptionExplanationDto(
                        opt.id(),
                        facade.renderMarkdown(opt.explanation()),
                        opt.correct()))
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

    @Builder(toBuilder = true)
    public record AnswerCommand(
            long questionId,
            long optionId,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered,
            Integer confidence,
            String clientAttemptId
    ) {
    }
}
