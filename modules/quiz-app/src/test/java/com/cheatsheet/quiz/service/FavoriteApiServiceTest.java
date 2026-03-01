package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.FavoriteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteApiServiceTest {

    @Mock
    private FavoriteService favoriteService;

    private FavoriteApiService service;

    @BeforeEach
    void setUp() {
        service = new FavoriteApiService(favoriteService);
    }

    @Test
    void toggleFavoriteMapsDomainResultToApiResponse() {
        long questionId = 1101L;
        FavoriteService.FavoriteResult domain = new FavoriteService.FavoriteResult(questionId, true, true);
        when(favoriteService.toggleFavorite(questionId)).thenReturn(domain);

        FavoriteResponse response = service.toggleFavorite(questionId);

        assertThat(response.questionId()).isEqualTo(questionId);
        assertThat(response.favorite()).isTrue();
        assertThat(response.synced()).isTrue();
        verify(favoriteService).toggleFavorite(questionId);
    }
}
