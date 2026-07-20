package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HandoffThreeSessionFilterTest {

    private final MvcRequestMapper mapper = new MvcRequestMapper();

    @Test
    void startFilterCarriesSelectedDifficultyToTheDomain() {
        StartSessionRequest request = new StartSessionRequest();
        request.setDifficulty("hard");

        InterviewFilter filter = mapper.resolveStartFilter(request);

        assertThat(filter.difficulty()).isEqualTo(Difficulty.HARD);
    }

    @Test
    void blankDifficultyKeepsTheExistingAllLevelsBehavior() {
        StartSessionRequest request = new StartSessionRequest();
        request.setDifficulty("  ");

        assertThat(mapper.resolveStartFilter(request).difficulty()).isNull();
    }

    @Test
    void settingsDefaultToTheTenQuestionMarathonAndPreserveDifficulty() {
        MvcRequestMapper.SettingsRequestContext context = mapper.resolveSettingsContext(
                null, null, null, null, null, null, null, null, "hard", null);

        assertThat(context.selectedMode()).isEqualTo(InterviewMode.MARATHON);
        assertThat(context.filter().difficulty()).isEqualTo(Difficulty.HARD);
    }
}
