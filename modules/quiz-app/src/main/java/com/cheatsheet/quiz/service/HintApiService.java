package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.HintResponse;
import com.cheatsheet.quiz.domain.Hint;
import org.springframework.http.ResponseEntity;
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
        int normalizedLevel = normalizeHintLevel(command.level());
        Optional<Hint> hint = facade.getHint(questionId, normalizedLevel);
        int maxLevel = HintService.getMaxLevel();
        return toHintResponse(questionId, maxLevel, hint);
    }

    public ResponseEntity<HintResponse> toHttpResponse(HintCommand command) {
        return ResponseEntity.ok(buildHintResponse(command));
    }

    public record HintCommand(long questionId, int level) {
    }

    private int normalizeHintLevel(int level) {
        return Math.max(1, Math.min(HintService.getMaxLevel(), level));
    }

    private HintResponse toHintResponse(long questionId, int maxLevel, Optional<Hint> hint) {
        if (hint.isEmpty()) {
            return new HintResponse(questionId, maxLevel, null, null);
        }
        Hint value = hint.get();
        return new HintResponse(questionId, maxLevel, value.hintText(), value.level());
    }
}
