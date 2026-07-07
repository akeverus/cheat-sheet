package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import com.cheatsheet.quiz.feature.interview.usecase.NextQuestionApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StatsApiService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiRequestMapperTest {

    private final ApiRequestMapper mapper = new ApiRequestMapper();

    @Test
    void toAnswerCommandMapsAllFields() {
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setQuestionId(10L);
        request.setOptionId(3L);
        request.setTopic("java");
        request.setGroup("core");
        request.setImportant(true);
        request.setOnlyWrong(false);
        request.setShuffle(true);
        request.setOrdered(false);
        request.setConfidence(4);

        AnswerApiService.AnswerCommand command = mapper.toAnswerCommand(request);

        assertThat(command.questionId()).isEqualTo(10L);
        assertThat(command.optionId()).isEqualTo(3L);
        assertThat(command.topic()).isEqualTo("java");
        assertThat(command.group()).isEqualTo("core");
        assertThat(command.important()).isTrue();
        assertThat(command.onlyWrong()).isFalse();
        assertThat(command.shuffle()).isTrue();
        assertThat(command.ordered()).isFalse();
        assertThat(command.confidence()).isEqualTo(4);
    }

    @Test
    void toStatsAndNextQuestionCommandsPreserveRequestParameters() {
        StatsApiService.StatsCommand stats = mapper.toStatsCommand("topic", "group", true, false, true, false);
        NextQuestionApiService.NextQuestionCommand next = mapper.toNextQuestionCommand(
                "topic", "group", true, false, true, true, false, 42L
        );

        assertThat(stats.topic()).isEqualTo("topic");
        assertThat(stats.group()).isEqualTo("group");
        assertThat(stats.important()).isTrue();
        assertThat(stats.onlyWrong()).isFalse();
        assertThat(stats.shuffle()).isTrue();
        assertThat(stats.ordered()).isFalse();

        assertThat(next.topic()).isEqualTo("topic");
        assertThat(next.group()).isEqualTo("group");
        assertThat(next.important()).isTrue();
        assertThat(next.onlyWrong()).isFalse();
        assertThat(next.shuffle()).isTrue();
        assertThat(next.weakTopics()).isTrue();
        assertThat(next.ordered()).isFalse();
        assertThat(next.excludeQuestionId()).isEqualTo(42L);
    }
}
