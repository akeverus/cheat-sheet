package com.cheatsheet.quiz.api.mapper;

import com.cheatsheet.quiz.api.dto.request.HintRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.service.AnswerApiService;
import com.cheatsheet.quiz.service.HintApiService;
import com.cheatsheet.quiz.service.NextQuestionApiService;
import com.cheatsheet.quiz.service.StatsApiService;
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
                request.getConfidence()
        );
    }

    public HintApiService.HintCommand toHintCommand(HintRequest request) {
        return new HintApiService.HintCommand(request.getQuestionId(), request.levelOrDefault());
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
