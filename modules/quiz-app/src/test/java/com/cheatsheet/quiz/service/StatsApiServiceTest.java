package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse;
import com.cheatsheet.quiz.api.dto.response.TopicStatsResponse;
import com.cheatsheet.quiz.api.mapper.StatsApiMapper;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsApiServiceTest {

    @Mock
    private InterviewFacade facade;
    @Mock
    private StatsApiMapper statsApiMapper;

    private StatsApiService service;

    @BeforeEach
    void setUp() {
        service = new StatsApiService(facade, statsApiMapper);
    }

    @Test
    void buildStatsResponseNormalizesFilterAndMapsResponse() {
        InterviewStats domainStats = new InterviewStats(20, 5, 9, 33, 7);
        InterviewStatsResponse mapped = new InterviewStatsResponse(20, 5, 9, 33, 7);
        StatsApiService.StatsCommand command = new StatsApiService.StatsCommand(
                "  java  ",
                "  core ",
                true,
                false,
                true,
                false
        );
        when(facade.getStats(any())).thenReturn(domainStats);
        when(statsApiMapper.toResponse(domainStats)).thenReturn(mapped);

        InterviewStatsResponse response = service.buildStatsResponse(command);

        assertThat(response).isEqualTo(mapped);
        ArgumentCaptor<InterviewFilter> captor = ArgumentCaptor.forClass(InterviewFilter.class);
        verify(facade).getStats(captor.capture());
        InterviewFilter filter = captor.getValue();
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("core");
        assertThat(filter.importantOnly()).isTrue();
        assertThat(filter.onlyWrong()).isFalse();
        assertThat(filter.shuffle()).isTrue();
        assertThat(filter.ordered()).isFalse();
        verify(statsApiMapper).toResponse(domainStats);
    }

    @Test
    void buildTopicStatsResponseMapsFacadeResult() {
        List<TopicStats> domain = List.of(
                new TopicStats("java", 10, 3, 4, 20, 5, 1),
                new TopicStats("spring", 8, 2, 3, 15, 4, 0)
        );
        List<TopicStatsResponse> mapped = List.of(
                new TopicStatsResponse("java", 10, 3, 4, 20, 5, 1),
                new TopicStatsResponse("spring", 8, 2, 3, 15, 4, 0)
        );
        when(facade.getTopicStats()).thenReturn(domain);
        when(statsApiMapper.toTopicResponses(domain)).thenReturn(mapped);

        List<TopicStatsResponse> response = service.buildTopicStatsResponse();

        assertThat(response).isEqualTo(mapped);
        verify(facade).getTopicStats();
        verify(statsApiMapper).toTopicResponses(domain);
    }
}
