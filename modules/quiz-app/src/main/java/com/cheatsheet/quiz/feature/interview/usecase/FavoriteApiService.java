package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse;
import com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/favorite`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteApiService {

    FavoriteService favoriteService;

    public FavoriteResponse toggleFavorite(long questionId) {
        FavoriteService.FavoriteResult result = favoriteService.toggleFavorite(questionId);
        return new FavoriteResponse(result.favorite(), result.synced(), result.questionId());
    }

    public ResponseEntity<FavoriteResponse> toHttpResponse(long questionId) {
        return ResponseEntity.ok(toggleFavorite(questionId));
    }
}
