package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse;
import com.cheatsheet.quiz.api.dto.response.TopicStatsResponse;
import com.cheatsheet.quiz.api.mapper.StatsApiMapper;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.util.FilterUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use-case orchestration для API endpoint-ов статистики.
 */
@Service
public class StatsApiService {

    private final InterviewFacade facade;
    private final StatsApiMapper statsApiMapper;

    public StatsApiService(InterviewFacade facade, StatsApiMapper statsApiMapper) {
        this.facade = facade;
        this.statsApiMapper = statsApiMapper;
    }

    public InterviewStatsResponse buildStatsResponse(StatsCommand command) {
        InterviewFilter filter = new InterviewFilter(
                FilterUtils.normalizeTopic(command.topic()),
                FilterUtils.normalizeGroup(command.group()),
                command.important(),
                command.onlyWrong(),
                command.shuffle(),
                command.ordered()
        );
        return statsApiMapper.toResponse(facade.getStats(filter));
    }

    public List<TopicStatsResponse> buildTopicStatsResponse() {
        return statsApiMapper.toTopicResponses(facade.getTopicStats());
    }

    public record StatsCommand(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered
    ) {
    }
}
