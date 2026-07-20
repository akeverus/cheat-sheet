package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HandoffThreeUiContractTest {

    private String resource(String path) throws IOException {
        return new String(
                new ClassPathResource(path).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void shellUsesPixelProductMarkAndOneSkipLinkPerPage() throws IOException {
        String sidebar = resource("templates/fragments/sidebar.html");
        String topbar = resource("templates/fragments/topbar.html");

        assertThat(sidebar).contains("class=\"brand-mark\"");
        assertThat(sidebar).doesNotContain("🧑‍💻");
        assertThat(topbar).contains("class=\"brand-mark\"");
        assertThat(topbar).doesNotContain("🧑‍💻");

        for (String page : new String[] {
                "focus-training", "result", "session-summary", "settings", "stats", "error"
        }) {
            assertThat(resource("templates/" + page + ".html").split("class=\"skip-link\"", -1))
                    .as(page + " contains one skip link")
                    .hasSize(2);
        }
    }

    @Test
    void questionWorkspaceDefaultsToSplitAndKeepsMobilePrimaryActionVisible() throws IOException {
        String head = resource("templates/fragments/head.html");
        String training = resource("templates/focus-training.html");
        String css = resource("static/css/shell.css");

        assertThat(head).contains("return LAYOUTS.indexOf(l) >= 0 ? l : 'split'");
        assertThat(training).contains("<h1 class=\"focus-question\"");
        assertThat(css).contains(".focus-page #interview-form");
        assertThat(css).contains("bottom: calc(var(--mobile-nav-height)");
    }

    @Test
    void summaryKeepsOnePrimaryRecommendationAndMovesOtherActionsToOverflow() throws IOException {
        String summary = resource("templates/session-summary.html");

        assertThat(summary).contains("class=\"summary-primary-action");
        assertThat(summary).contains("class=\"summary-more-actions\"");
        assertThat(summary).contains("Дополнительные действия");
        assertThat(summary).doesNotContain("class=\"btn secondary-btn\" th:href=\"@{/settings}\"");
    }

    @Test
    void settingsStartsFromPreviewAndHasNoSecondVisibleApplyStep() throws IOException {
        String settings = resource("templates/settings.html");

        assertThat(settings).contains("class=\"quick-start-form\"");
        assertThat(settings).contains("form=\"session-form\"");
        assertThat(settings).contains("<noscript><button type=\"submit\">Применить фильтры</button></noscript>");
        assertThat(settings.replace(
                "<noscript><button type=\"submit\">Применить фильтры</button></noscript>", ""))
                .doesNotContain("<button type=\"submit\">Применить фильтры</button>");
    }

    @Test
    void analyticsOffersOneRecommendedSessionAndAvoidsLargeChartsForSmallSets() throws IOException {
        String stats = resource("templates/stats.html");

        assertThat(stats).contains("class=\"card recommended-session\"");
        assertThat(stats).contains("Начать рекомендованную сессию");
        assertThat(stats).contains("#lists.size(topicStats) >= 4");
        assertThat(stats).contains("#lists.size(topicStats) < 4");
    }

    @Test
    void codeAndDiagramBlocksExposeRequiredLearningTools() throws IOException {
        String appJs = resource("static/js/app.js");
        String css = resource("static/css/shell.css");

        assertThat(appJs).contains("Переносить длинные строки");
        assertThat(appJs).contains("Развернуть код на весь экран");
        assertThat(appJs).contains("Увеличить схему");
        assertThat(appJs).contains("event.key === 'Escape'");
        assertThat(css).contains(".code-copy-wrap.is-fullscreen");
        assertThat(css).contains(".question-diagram.is-fullscreen");
    }

    @Test
    void notFoundPageExplainsThatTheLearningSessionIsPreserved() throws IOException {
        String error = resource("templates/error.html");

        assertThat(error).contains("class=\"pixel-computer\"");
        assertThat(error).contains("Текущая учебная сессия сохранена");
        assertThat(error).contains("Сообщить о битой ссылке");
    }
}
