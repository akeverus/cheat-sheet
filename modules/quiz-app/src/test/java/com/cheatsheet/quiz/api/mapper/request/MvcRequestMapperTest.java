package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MvcRequestMapperTest {

    private final MvcRequestMapper mapper = new MvcRequestMapper();

    @Test
    void resolveModeUsesSessionWhenPresent() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM,
                List.of(1L),
                "java",
                "backend",
                false,
                false,
                false,
                true
        );

        InterviewMode mode = mapper.resolveMode(session, "training");

        assertThat(mode).isEqualTo(InterviewMode.EXAM);
    }

    @Test
    void resolveFilterNormalizesWhenNoSession() {
        InterviewFilter filter = mapper.resolveFilter(
                null,
                "  java  ",
                "  backend ",
                true,
                false,
                true,
                true
        );

        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("backend");
        assertThat(filter.shuffle()).isTrue();
    }

    @Test
    void resolveStartFilterBuildsFromRequest() {
        StartSessionRequest request = new StartSessionRequest();
        request.setTopic("  spring ");
        request.setGroup("  core ");
        request.setImportant(true);
        request.setOnlyWrong(false);
        request.setShuffle(true);
        request.setOrdered(true);

        InterviewFilter filter = mapper.resolveStartFilter(request);

        assertThat(filter.topic()).isEqualTo("spring");
        assertThat(filter.group()).isEqualTo("core");
        assertThat(filter.importantOnly()).isTrue();
    }

    @Test
    void resolveStartModeParsesPayloadMode() {
        StartSessionRequest request = new StartSessionRequest();
        request.setMode("exam");

        InterviewMode mode = mapper.resolveStartMode(request);

        assertThat(mode).isEqualTo(InterviewMode.EXAM);
    }

    @Test
    void resolveSettingsContextBuildsModeFilterAndWeakTopics() {
        InterviewSession session = new InterviewSession(
                InterviewMode.FLASHCARD,
                List.of(10L, 20L),
                "spring",
                "backend",
                true,
                false,
                true,
                false
        );

        MvcRequestMapper.SettingsRequestContext context = mapper.resolveSettingsContext(
                session,
                "ignored-topic",
                "ignored-group",
                null,
                null,
                null,
                null,
                true,
                "training"
        );

        assertThat(context.selectedMode()).isEqualTo(InterviewMode.FLASHCARD);
        assertThat(context.filter().topic()).isEqualTo("spring");
        assertThat(context.filter().group()).isEqualTo("backend");
        assertThat(context.weakTopicsPriority()).isTrue();
    }

    @Test
    void resolveStatsContextNormalizesFilterAndKeepsQuery() {
        MvcRequestMapper.StatsRequestContext context = mapper.resolveStatsContext(
                "  java  ",
                "  core ",
                true,
                false,
                true,
                "hashmap"
        );

        assertThat(context.filter().topic()).isEqualTo("java");
        assertThat(context.filter().group()).isEqualTo("core");
        assertThat(context.filter().importantOnly()).isTrue();
        assertThat(context.filter().ordered()).isTrue();
        assertThat(context.query()).isEqualTo("hashmap");
    }
}
