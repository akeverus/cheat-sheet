package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.FavoriteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/favorite`.
 */
@Service
public class FavoriteApiService {

    private final FavoriteService favoriteService;

    public FavoriteApiService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public FavoriteResponse toggleFavorite(long questionId) {
        FavoriteService.FavoriteResult result = favoriteService.toggleFavorite(questionId);
        return new FavoriteResponse(result.favorite(), result.synced(), result.questionId());
    }

    public ResponseEntity<FavoriteResponse> toHttpResponse(long questionId) {
        return ResponseEntity.ok(toggleFavorite(questionId));
    }
}
