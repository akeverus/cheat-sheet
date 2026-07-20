package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HandoffThreeParityContractTest {

    private String resource(String path) throws IOException {
        return new String(
                new ClassPathResource(path).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void allProductPagesUseTheSinglePixelSprite() throws IOException {
        for (String page : new String[] {
                "focus-training", "result", "session-summary", "settings", "stats", "error"
        }) {
            String html = resource("templates/" + page + ".html");
            assertThat(html).as(page).contains("fragments/pixel-sprite :: sprite");
            assertThat(html).as(page).doesNotContain("fragments/icons :: sprite");
            assertThat(html).as(page).doesNotContain("href=\"#i-");
        }

        String sprite = resource("templates/fragments/pixel-sprite.html");
        assertThat(sprite).contains(
                "id=\"flame\"", "id=\"target\"", "id=\"more\"", "id=\"fullscreen\"",
                "id=\"search\"", "id=\"filter\"", "id=\"clock\"", "id=\"pause\"",
                "id=\"face-unknown\"", "id=\"face-guess\"", "id=\"face-hard\"",
                "id=\"face-remembered\"", "id=\"face-great\"");
    }

    @Test
    void questionAndAnsweredStatesExposeTheHandoffWorkspace() throws IOException {
        String training = resource("templates/focus-training.html");
        String result = resource("templates/result.html");
        String appJs = resource("static/js/app.js");

        assertThat(training).contains("class=\"question-progress\"");
        assertThat(training).contains("class=\"answer-actions\"");
        assertThat(result).contains("correct-answer-panel");
        assertThat(result).contains("data-static-confidence");
        assertThat(appJs).contains("dynamic-review-grid", "buildCorrectAnswerPanel");
        assertThat(appJs).contains("data-grade=\"5\"");
    }

    @Test
    void settingsAndAnalyticsExposeNewControlsAndDrillDown() throws IOException {
        String settings = resource("templates/settings.html");
        String stats = resource("templates/stats.html");
        String head = resource("templates/fragments/head.html");

        assertThat(settings).contains("name=\"difficulty\"");
        assertThat(settings).contains("data-topic-preset");
        assertThat(settings).doesNotContain("type=\"submit\" name=\"topic\"");
        assertThat(settings).contains("name=\"timerSeconds\"");
        assertThat(settings).contains("data-learning-pref=\"explanations\"");
        assertThat(stats).contains("id=\"overviewAccuracyChart\"");
        assertThat(stats).contains("class=\"attempt-list\"");
        assertThat(stats).contains("id=\"accuracy-trend-data\"");
        assertThat(head).contains("/css/handoff-three.css");
    }

    @Test
    void summaryDistinguishesAnEarlyFinishFromAPerfectResult() throws IOException {
        String summary = resource("templates/session-summary.html");

        assertThat(summary).contains("Сессия завершена без ответов");
        assertThat(summary).contains("ring-neutral", "Ответов не было");
    }

    @Test
    void pixelMotionIsSteppedAndRespectsReducedMotion() throws IOException {
        String css = resource("static/css/handoff-three.css");
        String shell = resource("templates/fragments/shell-scripts.html");

        assertThat(css).contains(
                "shape-rendering: crispEdges", "@keyframes pixel-reveal",
                "@keyframes pixel-spin", "steps(4, end)",
                "@media (prefers-reduced-motion: reduce)", "data-motion=\"off\"");
        assertThat(shell).contains(
                "function motionReduced()", "pixel-motion-ready",
                "is-pixel-pressed", "motionchange");
    }
}
