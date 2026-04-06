package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.feature.interview.service.topic.AdaptiveDifficultyService;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.persistence.model.UserTopicStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdaptiveDifficultyServiceTest {

    @Mock
    UserTopicStatsRepository userTopicStatsRepository;

    AdaptiveDifficultyService service;

    @BeforeEach
    void setUp() {
        service = new AdaptiveDifficultyService(userTopicStatsRepository);
    }

    @Test
    void returnsMediumWhenNoTopicStatsFound() {
        when(userTopicStatsRepository.findByTopic("spring")).thenReturn(Optional.empty());

        assertThat(service.resolveDifficulty("spring")).isEqualTo(Difficulty.MEDIUM);
    }

    @Test
    void returnsEasyWhenMasteryIsLow() {
        when(userTopicStatsRepository.findByTopic("java"))
                .thenReturn(Optional.of(new UserTopicStats(1L, "java", 2, 8, 0.39, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("java")).isEqualTo(Difficulty.EASY);
    }

    @Test
    void returnsHardWhenMasteryIsHigh() {
        when(userTopicStatsRepository.findByTopic("sql"))
                .thenReturn(Optional.of(new UserTopicStats(2L, "sql", 15, 3, 0.9, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("sql")).isEqualTo(Difficulty.HARD);
    }

    @Test
    void returnsMinusOneAccuracyWhenNoTopicStatsFound() {
        when(userTopicStatsRepository.findByTopic("spring")).thenReturn(Optional.empty());

        assertThat(service.resolveTopicAccuracy("spring")).isEqualTo(-1);
    }

    @Test
    void convertsMasteryToAccuracyPercentage() {
        when(userTopicStatsRepository.findByTopic("sql"))
                .thenReturn(Optional.of(new UserTopicStats(2L, "sql", 15, 3, 0.9, LocalDateTime.now())));

        assertThat(service.resolveTopicAccuracy("sql")).isEqualTo(90.0);
    }
}
