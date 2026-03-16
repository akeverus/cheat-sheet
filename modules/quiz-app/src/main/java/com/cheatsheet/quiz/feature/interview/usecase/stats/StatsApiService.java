package com.cheatsheet.quiz.feature.interview.usecase.stats;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse;
import com.cheatsheet.quiz.api.mapper.response.StatsApiMapper;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.common.util.FilterUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.List;

/**
 * Use-case orchestration для API endpoint-ов статистики.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsApiService {
    InterviewFacade facade;
    StatsApiMapper statsApiMapper;

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

    public ResponseEntity<InterviewStatsResponse> toStatsHttpResponse(StatsCommand command) {
        return ResponseEntity.ok(buildStatsResponse(command));
    }

    public ResponseEntity<List<TopicStatsResponse>> toTopicStatsHttpResponse() {
        return ResponseEntity.ok(buildTopicStatsResponse());
    }

    @Builder(toBuilder = true)
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
