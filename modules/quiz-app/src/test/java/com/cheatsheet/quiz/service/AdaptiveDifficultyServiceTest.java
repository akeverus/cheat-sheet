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

    // --- Граничные пороги. Логика использует строгое «меньше» (`mastery < 0.4`,
    // `mastery < 0.75`), поэтому ровно на пороге вопрос попадает в ВЕРХНИЙ уровень.
    // Пины защищают от тихой замены `<` на `<=` (сдвиг всей шкалы сложности). ---

    @Test
    void lowerBoundaryExactlyPointFourIsMediumNotEasy() {
        // 0.4 — не «< 0.4», значит уже MEDIUM (нижняя граница EASY-зоны исключающая).
        when(userTopicStatsRepository.findByTopic("kafka"))
                .thenReturn(Optional.of(new UserTopicStats(3L, "kafka", 4, 6, 0.4, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("kafka")).isEqualTo(Difficulty.MEDIUM);
    }

    @Test
    void justBelowLowerBoundaryStaysEasy() {
        when(userTopicStatsRepository.findByTopic("redis"))
                .thenReturn(Optional.of(new UserTopicStats(4L, "redis", 3, 7, 0.3999, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("redis")).isEqualTo(Difficulty.EASY);
    }

    @Test
    void upperBoundaryExactlyPointSevenFiveIsHardNotMedium() {
        // 0.75 — не «< 0.75», значит уже HARD (верхняя граница MEDIUM-зоны исключающая).
        when(userTopicStatsRepository.findByTopic("docker"))
                .thenReturn(Optional.of(new UserTopicStats(5L, "docker", 15, 5, 0.75, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("docker")).isEqualTo(Difficulty.HARD);
    }

    @Test
    void justBelowUpperBoundaryStaysMedium() {
        when(userTopicStatsRepository.findByTopic("grpc"))
                .thenReturn(Optional.of(new UserTopicStats(6L, "grpc", 14, 6, 0.7499, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("grpc")).isEqualTo(Difficulty.MEDIUM);
    }

    @Test
    void zeroMasteryIsEasyFloor() {
        when(userTopicStatsRepository.findByTopic("scala"))
                .thenReturn(Optional.of(new UserTopicStats(7L, "scala", 0, 10, 0.0, LocalDateTime.now())));

        assertThat(service.resolveDifficulty("scala")).isEqualTo(Difficulty.EASY);
    }
}
