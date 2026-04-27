package com.cheatsheet.quiz.api.mapper.response;

import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatsApiMapperTest {

    private final StatsApiMapper mapper = new StatsApiMapper();

    @Test
    void mapsInterviewStatsToResponse() {
        InterviewStats stats = new InterviewStats(100L, 25L, 50L, 80L, 20L);

        InterviewStatsResponse response = mapper.toResponse(stats);

        assertThat(response.total()).isEqualTo(100L);
        assertThat(response.due()).isEqualTo(25L);
        assertThat(response.learned()).isEqualTo(50L);
        assertThat(response.correct()).isEqualTo(80L);
        assertThat(response.wrong()).isEqualTo(20L);
    }

    @Test
    void mapsTopicStatsListPreservingOrder() {
        TopicStats java = new TopicStats("java", 50L, 10L, 20L, 30L, 5L, 2L, 0.6);
        TopicStats spring = new TopicStats("spring", 30L, 5L, 15L, 25L, 3L, 1L, 0.8);

        List<TopicStatsResponse> result = mapper.toTopicResponses(List.of(java, spring));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).topic()).isEqualTo("java");
        assertThat(result.get(1).topic()).isEqualTo("spring");
    }

    @Test
    void mapsTopicStatsToResponse() {
        TopicStats stats = new TopicStats("databases", 200L, 50L, 100L, 180L, 20L, 5L, 0.9);

        TopicStatsResponse response = mapper.toTopicResponse(stats);

        assertThat(response.topic()).isEqualTo("databases");
        assertThat(response.total()).isEqualTo(200L);
        assertThat(response.due()).isEqualTo(50L);
        assertThat(response.learned()).isEqualTo(100L);
        assertThat(response.correct()).isEqualTo(180L);
        assertThat(response.wrong()).isEqualTo(20L);
        assertThat(response.regenSum()).isEqualTo(5L);
    }

    @Test
    void mapsEmptyTopicListToEmptyList() {
        assertThat(mapper.toTopicResponses(List.of())).isEmpty();
    }
}
