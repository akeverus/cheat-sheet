package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import com.cheatsheet.quiz.feature.interview.usecase.NextQuestionApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StatsApiService;
import org.springframework.stereotype.Component;

/**
 * Mapper API request-параметров в command-объекты сервисного слоя.
 */
@Component
public class ApiRequestMapper {

    public AnswerApiService.AnswerCommand toAnswerCommand(SubmitAnswerRequest request) {
        return new AnswerApiService.AnswerCommand(
                request.getQuestionId(),
                request.getOptionId(),
                request.getTopic(),
                request.getGroup(),
                request.getImportant(),
                request.getOnlyWrong(),
                request.getShuffle(),
                request.getOrdered(),
                request.getConfidence(),
                request.getClientAttemptId(),
                request.getOpenedContentBlockIds()
        );
    }

    public StatsApiService.StatsCommand toStatsCommand(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered
    ) {
        return new StatsApiService.StatsCommand(topic, group, important, onlyWrong, shuffle, ordered);
    }

    public NextQuestionApiService.NextQuestionCommand toNextQuestionCommand(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean weakTopics,
            Boolean ordered,
            Long excludeQuestionId
    ) {
        return new NextQuestionApiService.NextQuestionCommand(
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                weakTopics,
                ordered,
                excludeQuestionId
        );
    }
}
