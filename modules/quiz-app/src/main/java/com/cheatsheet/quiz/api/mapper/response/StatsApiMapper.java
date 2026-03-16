package com.cheatsheet.quiz.api.mapper.response;

import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер доменной статистики в публичные API DTO.
 */
@Component
public class StatsApiMapper {

    /**
     * Конвертирует доменную агрегированную статистику в API DTO.
     *
     * @param stats доменная статистика
     * @return сериализуемый API DTO
     */
    public InterviewStatsResponse toResponse(InterviewStats stats) {
        return new InterviewStatsResponse(
                stats.total(),
                stats.due(),
                stats.learned(),
                stats.correct(),
                stats.wrong()
        );
    }

    /**
     * Конвертирует список доменных статистик тем в API DTO.
     *
     * @param topicStats список доменных статистик
     * @return список API DTO
     */
    public List<TopicStatsResponse> toTopicResponses(List<TopicStats> topicStats) {
        return topicStats.stream()
                .map(this::toTopicResponse)
                .toList();
    }

    /**
     * Конвертирует доменную статистику темы в API DTO.
     *
     * @param topicStats статистика темы
     * @return API DTO статистики темы
     */
    public TopicStatsResponse toTopicResponse(TopicStats topicStats) {
        return new TopicStatsResponse(
                topicStats.topic(),
                topicStats.total(),
                topicStats.due(),
                topicStats.learned(),
                topicStats.correct(),
                topicStats.wrong(),
                topicStats.regenSum()
        );
    }
}
