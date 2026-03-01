package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.NextQuestionOptionDto;
import com.cheatsheet.quiz.api.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.util.FilterUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Use-case orchestration для API endpoint `/api/next`.
 */
@Service
public class NextQuestionApiService {

    private final InterviewFacade facade;

    public NextQuestionApiService(InterviewFacade facade) {
        this.facade = facade;
    }

    public Optional<NextQuestionResponse> getNextQuestion(NextQuestionCommand command) {
        InterviewFilter filter = new InterviewFilter(
                FilterUtils.normalizeTopic(command.topic()),
                FilterUtils.normalizeGroup(command.group()),
                command.important(),
                command.onlyWrong(),
                command.shuffle(),
                command.ordered()
        );
        boolean weakTopicsPriority = Boolean.TRUE.equals(command.weakTopics());
        Optional<InterviewQuestion> next = facade.nextQuestion(
                filter,
                weakTopicsPriority,
                command.excludeQuestionId()
        );
        if (next.isEmpty()) {
            return Optional.empty();
        }
        InterviewQuestion question = next.get();
        List<NextQuestionOptionDto> options = question.options().stream()
                .map(opt -> new NextQuestionOptionDto(opt.id(), opt.optionText()))
                .toList();
        return Optional.of(new NextQuestionResponse(
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

    public record NextQuestionCommand(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean weakTopics,
            Boolean ordered,
            Long excludeQuestionId
    ) {
    }
}
