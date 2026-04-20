package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateFragmentContractTest {

    private String readTemplate(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        byte[] bytes = resource.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    void extractedFragmentsExposeStableUiMarkersAndAriaContracts() throws IOException {
        String trainingActions = readTemplate("templates/fragments/training-actions.html");
        String resultZoneHead = readTemplate("templates/fragments/result-zone-head.html");
        String postAnswerControls = readTemplate("templates/fragments/post-answer-controls.html");
        String inlineAlert = readTemplate("templates/fragments/inline-alert.html");
        String focusSurfaceTabs = readTemplate("templates/fragments/focus-surface-tabs.html");

        assertThat(trainingActions).contains("data-ui-fragment=\"training-actions\"");
        assertThat(trainingActions).contains("id=\"action-footer\"");
        assertThat(trainingActions).contains("id=\"question-timer\"");
        assertThat(trainingActions).contains("class=\"keyboard-hint\"");
        assertThat(trainingActions).contains("aria-live=\"polite\"");
        assertThat(trainingActions).contains("id=\"next-question\"");
        assertThat(trainingActions).contains("aria-keyshortcuts=\"Enter Space\"");

        assertThat(resultZoneHead).contains("data-ui-fragment=\"result-zone-head\"");
        assertThat(resultZoneHead).contains("result-zone-head");

        assertThat(postAnswerControls).contains("data-ui-fragment=\"post-answer-controls\"");
        assertThat(postAnswerControls).contains("id=\"result-feedback\"");
        assertThat(postAnswerControls).contains("aria-atomic=\"true\"");
        assertThat(postAnswerControls).contains("aria-controls=\"result-feedback\"");

        assertThat(inlineAlert).contains("data-ui-fragment=\"inline-alert\"");
        assertThat(inlineAlert).contains("role=\"alert\"");
        assertThat(inlineAlert).contains("aria-live=\"assertive\"");

        assertThat(focusSurfaceTabs).contains("data-ui-fragment=\"focus-surface-tabs\"");
        assertThat(focusSurfaceTabs).contains("aria-label=\"Навигация по режимам фокуса\"");
        assertThat(focusSurfaceTabs).contains("aria-current=\"page\"");
        assertThat(focusSurfaceTabs).contains("class=\"surface-tabs\"");
        assertThat(focusSurfaceTabs).contains(">Фокус<");
        assertThat(focusSurfaceTabs).contains(">Аналитика<");
        assertThat(focusSurfaceTabs).contains(">Настройки<");
    }

    @Test
    void keyPagesReferenceSharedFragmentsInsteadOfLocalCopies() throws IOException {
        String index = readTemplate("templates/index.html");
        String focusTraining = readTemplate("templates/focus-training.html");
        String stats = readTemplate("templates/stats.html");
        String result = readTemplate("templates/result.html");
        String settings = readTemplate("templates/settings.html");

        assertThat(index).contains("<body class=\"focus-page\">");
        assertThat(focusTraining).contains("<body class=\"focus-page\">");
        assertThat(stats).contains("<body class=\"stats-page\">");
        assertThat(settings).contains("<body class=\"settings-page\">");
        assertThat(result).contains("<body class=\"result-page\">");
        assertThat(stats).contains("stats-filters-form");
        assertThat(stats).contains("stats-search-form");
        assertThat(stats).contains("stats-action-btn");
        assertThat(stats).contains("stats-apply-action");
        assertThat(stats).contains("stats-search-action");

        assertThat(index).contains("fragments/training-actions :: training-actions");
        assertThat(index).contains("showPrimaryNav=false");
        assertThat(index).contains("fragments/result-zone-head :: result-zone-head");
        assertThat(index).contains("fragments/post-answer-controls :: post-answer-controls");
        assertThat(index).contains("fragments/focus-surface-tabs :: focus-surface-tabs");
        assertThat(index).contains("chipText='Пост-разбор'");
        assertThat(index).contains("hintText='Сначала итог, затем объяснение и дополнительные блоки'");
        assertThat(index).contains("extraButtonText='Показать доп. анализ'");
        assertThat(index).contains("empty-action-settings");
        assertThat(index).contains("empty-action-retry");

        assertThat(focusTraining).contains("fragments/training-actions :: training-actions");
        assertThat(focusTraining).contains("showPrimaryNav=false");
        assertThat(focusTraining).contains("fragments/result-zone-head :: result-zone-head");
        assertThat(focusTraining).contains("fragments/post-answer-controls :: post-answer-controls");
        assertThat(focusTraining).contains("fragments/focus-surface-tabs :: focus-surface-tabs");
        assertThat(focusTraining).contains("chipText='Пост-разбор'");
        assertThat(focusTraining).contains("hintText='Сначала итог, затем объяснение и дополнительные блоки'");
        assertThat(focusTraining).contains("extraButtonText='Показать доп. анализ'");
        assertThat(focusTraining).contains("aria-valuemax=${interviewSession != null ? interviewSession.getTotal() : 0}");
        assertThat(focusTraining).contains("aria-valuenow=${interviewSession != null ? interviewSession.getIndex() + 1 : 0}");
        assertThat(focusTraining).contains("data-progress=${#numbers.formatDecimal(progressPercent, 1, 1)}");
        assertThat(focusTraining).contains("th:text=\"${focusModeChipText}\"");
        assertThat(focusTraining).contains("th:text=\"${focusModeHintText}\"");
        assertThat(focusTraining).doesNotContain("aria-valuemax=\"100\"");
        assertThat(focusTraining).doesNotContain("data-progress=\"0\"");
        assertThat(focusTraining).contains("th:href=\"${focusEmptyRetryHref}\"");
        assertThat(focusTraining).contains("th:text=\"${focusEmptyRetryText}\"");
        assertThat(focusTraining).contains("th:if=\"${!reviewMode and interviewSession == null}\"");
        assertThat(focusTraining).contains("th:if=\"${!reviewMode and interviewSession != null and !interviewSession.finished}\"");
        assertThat(focusTraining).contains("th:if=\"${!reviewMode and interviewSession != null and interviewSession.finished}\"");
        assertThat(focusTraining).contains("Сессия запущена, но вопрос пока недоступен. Попробуй обновить тренировку.");
        assertThat(focusTraining).contains("🏁 Сессия завершена.");
        assertThat(focusTraining).doesNotContain("chipText='Результат'");
        assertThat(focusTraining).doesNotContain("extraButtonText='Подробнее'");
        assertThat(focusTraining).contains("empty-action-settings");
        assertThat(focusTraining).contains("empty-action-retry");
        assertThat(focusTraining).contains("submitId='interview-submit'");
        assertThat(focusTraining).contains("id=\"options-flow-hint\"");
        assertThat(focusTraining).contains("aria-describedby=\"options-flow-hint\"");
        assertThat(focusTraining).doesNotContain("submitId='submitBtn'");
        assertThat(focusTraining).doesNotContain("role=\"radio\"");
        assertThat(focusTraining).doesNotContain("tabindex=\"0\"");

        assertThat(result).contains("fragments/result-zone-head :: result-zone-head");
        assertThat(result).contains("showPrimaryNav=true");
        assertThat(result).contains("class=\"question-main flow-stack-md\"");
        assertThat(result).contains("class=\"result-actions\"");
        assertThat(result).contains("th:if=\"${interviewSession != null and interviewSession.finished}\"");
        assertThat(result.indexOf("class=\"btn next-btn\""))
                .isLessThan(result.indexOf("id=\"extra-analysis-toggle-result\""));
        assertThat(stats).contains("showPrimaryNav=true");
        assertThat(settings).contains("showPrimaryNav=true");
        assertThat(result).contains("fragments/inline-alert :: inline-alert");
    }

    @Test
    void settingsTemplateKeepsSidebarCollapseAccessibilityWiring() throws IOException {
        String settings = readTemplate("templates/settings.html");
        assertThat(settings).contains("id=\"left-sidebar-card\"");
        assertThat(settings).contains("type=\"button\"");
        assertThat(settings).contains("id=\"sidebar-collapse-toggle\"");
        assertThat(settings).contains("aria-controls=\"left-sidebar-content\"");
        assertThat(settings).contains("aria-expanded=\"true\"");
        assertThat(settings).contains("id=\"left-sidebar-content\"");
        assertThat(settings).contains("id=\"main-content\"");
        assertThat(settings).contains("class=\"app-layout\"");
        assertThat(settings).contains("class=\"main-content settings-content\"");
    }

    @Test
    void statsTemplateKeepsSortableLiveRegionAccessibilityContract() throws IOException {
        String stats = readTemplate("templates/stats.html");
        assertThat(stats).contains("id=\"table-sort-status\"");
        assertThat(stats).contains("aria-live=\"polite\"");
        assertThat(stats).contains("aria-atomic=\"true\"");
        assertThat(stats).contains("aria-keyshortcuts=\"Enter Space\"");
        assertThat(stats).contains("role=\"columnheader\"");
        assertThat(stats).contains("id=\"topicProgressChartFallback\"");
        assertThat(stats).contains("id=\"topicAccuracyChartFallback\"");
        assertThat(stats).contains("class=\"chart-fallback hidden\"");
        assertThat(stats).contains("role=\"status\"");
    }

    @Test
    void cssScopesFocusOverridesToFocusPage() throws IOException {
        String styles = readTemplate("static/css/styles.css");
        assertThat(styles).contains(".focus-page .app-layout");
        assertThat(styles).contains(".focus-page .main-content");
        assertThat(styles).contains(".focus-page .empty-actions");
        assertThat(styles).contains(".focus-page .surface-toolbar");
        assertThat(styles).contains(".focus-page .surface-tab");
        assertThat(styles).contains(".focus-page .session-progress-track");
        assertThat(styles).contains(".focus-page .focus-progress-strip");
        assertThat(styles).contains(".focus-page .next-btn");
        assertThat(styles).contains(".result-page .result-zone-head");
        assertThat(styles).contains(".result-page .zone-hint");
        assertThat(styles).contains(".result-page .result-feedback");
        assertThat(styles).contains(".result-page .result-actions");
        assertThat(styles).contains(".result-page .next-btn");
        assertThat(styles).contains(".stats-page .charts-row");
        assertThat(styles).contains(".stats-page .chart-fallback");
        assertThat(styles).contains(".stats-page .topic-table");
        assertThat(styles).contains(".stats-page .topic-table th.sortable:focus-visible");
        assertThat(styles).contains(".stats-page .search-form");
        assertThat(styles).contains(".stats-page .stats-filters-form");
        assertThat(styles).contains(".stats-page .stats-search-form");
        assertThat(styles).contains(".stats-page .search-results > div");
        assertThat(styles).contains(".stats-page .stats-filter-row");
        assertThat(styles).contains(".stats-page .stats-action-btn");
        assertThat(styles).contains(".stats-page .stats-apply-action");
        assertThat(styles).contains(".stats-page .stats-search-action");
        assertThat(styles).contains(".stats-page .nav-export");
        assertThat(styles).contains(".stats-page .nav-export:hover");
        assertThat(styles).contains(".settings-page .settings-content");
        assertThat(styles).contains(".settings-page #main-content");
        assertThat(styles).contains(".settings-page .app-layout");
        assertThat(styles).contains(".settings-page .sidebar-toggle-btn");
        assertThat(styles).contains(".settings-page .compact-sidebar.control-card");
        assertThat(styles).contains(".settings-page .compact-sidebar.is-collapsed");
        assertThat(styles).contains(".settings-page .control-section-tips");
        assertThat(styles).contains(".settings-page .control-card .streak-bar");
        assertThat(styles).contains(".flow-stack-md");
        assertThat(styles).contains(".focus-page .training-shell .question-zone-head");
        assertThat(styles).contains(".focus-page .training-shell .focus-question");
        assertThat(styles).contains(".result-page .question-main.flow-stack-md");
        assertThat(styles).contains(".training-shell .options");
        assertThat(styles).contains(".training-shell .options label");
        assertThat(styles).contains(".training-shell .action-footer");
        assertThat(styles).contains(".training-shell #interview-submit");
        assertThat(styles).contains(".training-shell .question-timer");
        assertThat(styles).contains(".training-shell .next-btn");
    }
}
