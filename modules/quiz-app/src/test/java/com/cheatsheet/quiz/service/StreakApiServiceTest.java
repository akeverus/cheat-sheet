package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.StreakResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreakApiServiceTest {

    @Mock
    private DailyStreakService dailyStreakService;

    private StreakApiService service;

    @BeforeEach
    void setUp() {
        service = new StreakApiService(dailyStreakService);
    }

    @Test
    void buildStreakResponseDelegatesToDailyService() {
        StreakResponse response = new StreakResponse(7, 10, 3, false, 5);
        when(dailyStreakService.getTodayProgress()).thenReturn(response);

        StreakResponse result = service.buildStreakResponse();

        assertThat(result).isEqualTo(response);
        verify(dailyStreakService).getTodayProgress();
    }

    @Test
    void toHttpResponseWrapsStreakPayloadWithOkStatus() {
        StreakResponse payload = new StreakResponse(4, 10, 2, false, 3);
        when(dailyStreakService.getTodayProgress()).thenReturn(payload);

        ResponseEntity<StreakResponse> response = service.toHttpResponse();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(payload);
        verify(dailyStreakService).getTodayProgress();
    }
}
