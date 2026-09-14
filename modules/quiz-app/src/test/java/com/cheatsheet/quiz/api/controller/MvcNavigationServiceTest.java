package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MvcNavigationServiceTest {

    private final MvcNavigationService service = new MvcNavigationService();

    @Test
    void returnsStableRedirectsAndViews() {
        assertThat(service.focusRedirect()).isEqualTo("redirect:/practice");
        assertThat(service.focusQuestionRedirect(3L)).isEqualTo("redirect:/practice?questionId=3");
        assertThat(service.homeRedirect()).isEqualTo("redirect:/");
        assertThat(service.homeView()).isEqualTo("home");
        assertThat(service.sessionSummaryRedirect()).isEqualTo("redirect:/session-summary");
        assertThat(service.focusView()).isEqualTo("focus-training");
        assertThat(service.settingsView()).isEqualTo("settings");
        assertThat(service.resultView()).isEqualTo("result");
        assertThat(service.statsView()).isEqualTo("stats");
        assertThat(service.bookmarksView()).isEqualTo("bookmarks");
        assertThat(service.sessionSummaryView()).isEqualTo("session-summary");
    }
}
