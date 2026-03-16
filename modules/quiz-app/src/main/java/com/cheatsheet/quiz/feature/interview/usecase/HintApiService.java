package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.feature.interview.dto.response.insight.HintResponse;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.feature.interview.service.insight.HintService;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use-case orchestration для API endpoint `/api/hint`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HintApiService {

    InterviewFacade facade;

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

    @Builder(toBuilder = true)
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
