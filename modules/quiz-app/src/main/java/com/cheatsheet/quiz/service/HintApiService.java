package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.HintResponse;
import com.cheatsheet.quiz.domain.Hint;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use-case orchestration для API endpoint `/api/hint`.
 */
@Service
public class HintApiService {

    private final InterviewFacade facade;

    public HintApiService(InterviewFacade facade) {
        this.facade = facade;
    }

    public HintResponse buildHintResponse(HintCommand command) {
        long questionId = command.questionId();
        facade.ensureQuestionExists(questionId);
        Optional<Hint> hint = facade.getHint(questionId, command.level());
        int maxLevel = HintService.getMaxLevel();
        if (hint.isEmpty()) {
            return new HintResponse(questionId, maxLevel, null, null);
        }
        Hint value = hint.get();
        return new HintResponse(questionId, maxLevel, value.hintText(), value.level());
    }

    public record HintCommand(long questionId, int level) {
    }
}
