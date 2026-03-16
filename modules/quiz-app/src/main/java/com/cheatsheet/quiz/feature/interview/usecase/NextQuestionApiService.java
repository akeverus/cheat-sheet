package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.feature.interview.mapper.NextQuestionResponseMapper;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.common.util.FilterUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use-case orchestration для API endpoint `/api/next`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NextQuestionApiService {
    InterviewFacade facade;
    NextQuestionResponseMapper nextQuestionResponseMapper;

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
        return facade.nextQuestion(filter, weakTopicsPriority, command.excludeQuestionId())
                .map(nextQuestionResponseMapper::toResponse);
    }

    public ResponseEntity<NextQuestionResponse> toHttpResponse(NextQuestionCommand command) {
        return getNextQuestion(command)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Builder(toBuilder = true)
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
