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

        assertThat(trainingActions).contains("data-ui-fragment=\"training-actions\"");
        assertThat(trainingActions).contains("id=\"action-footer\"");
        assertThat(trainingActions).contains("id=\"question-timer\"");
        assertThat(trainingActions).contains("class=\"keyboard-hint\"");
        // Намеренно БЕЗ aria-live-атрибута в этом фрагменте: таймер озвучивался бы
        // каждую секунду, а подсказка статична — live-region был бы шумом (критика
        // D13). Проверяем именно атрибут (aria-live="), а не слово — в комментарии
        // фрагмента «Без aria-live» строка aria-live встречается легально.
        assertThat(trainingActions).doesNotContain("aria-live=\"");
        assertThat(trainingActions).contains("id=\"next-question\"");
        // Кнопки активируются Enter (Space — нативное поведение <button>, в
        // aria-keyshortcuts не дублируется).
        assertThat(trainingActions).contains("aria-keyshortcuts=\"Enter\"");

        assertThat(resultZoneHead).contains("data-ui-fragment=\"result-zone-head\"");
        assertThat(resultZoneHead).contains("result-zone-head");

        assertThat(postAnswerControls).contains("data-ui-fragment=\"post-answer-controls\"");
        assertThat(postAnswerControls).contains("id=\"result-feedback\"");
        assertThat(postAnswerControls).contains("aria-atomic=\"true\"");
        // Кнопка «доп. анализ» раскрывает sink #extra-analysis-content,
        // стоящий ПОСЛЕ неё (appendAnalysisBlock складывает блоки под кнопку).
        assertThat(postAnswerControls).contains("aria-controls=\"extra-analysis-content\"");
        assertThat(postAnswerControls).contains("id=\"extra-analysis-content\"");

        assertThat(inlineAlert).contains("data-ui-fragment=\"inline-alert\"");
        assertThat(inlineAlert).contains("role=\"alert\"");
        assertThat(inlineAlert).contains("aria-live=\"assertive\"");
    }

    @Test
    void keyPagesReferenceSharedFragmentsInsteadOfLocalCopies() throws IOException {
        String focusTraining = readTemplate("templates/focus-training.html");
        String stats = readTemplate("templates/stats.html");
        String result = readTemplate("templates/result.html");
        String settings = readTemplate("templates/settings.html");

        assertThat(focusTraining).contains("<body class=\"focus-page\">");
        assertThat(stats).contains("<body class=\"stats-page\">");
        assertThat(settings).contains("<body class=\"settings-page\">");
        assertThat(result).contains("<body class=\"result-page\">");
        assertThat(stats).contains("stats-filters-form");
        assertThat(stats).contains("stats-search-form");
        assertThat(stats).contains("stats-action-btn");
        assertThat(stats).contains("stats-apply-action");
        assertThat(stats).contains("stats-search-action");

        assertThat(focusTraining).contains("fragments/training-actions :: training-actions");
        // Editorial IA: единая навигация в шапке на всех страницах (showPrimaryNav=true),
        // дубль-навигация focus-surface-tabs удалена.
        assertThat(focusTraining).contains("showPrimaryNav=true");
        assertThat(focusTraining).contains("fragments/result-zone-head :: result-zone-head");
        assertThat(focusTraining).contains("fragments/post-answer-controls :: post-answer-controls");
        assertThat(focusTraining).doesNotContain("focus-surface-tabs");
        assertThat(focusTraining).contains("chipText='Пост-разбор'");
        assertThat(focusTraining).contains("hintText='Сначала итог, затем объяснение и дополнительные блоки'");
        assertThat(focusTraining).contains("extraButtonText='Похожие вопросы'");
        // Трек гейтится th:if="${interviewSession != null}" (вне сессии пустой
        // progressbar 0/0 не рендерится) → null-ветки тернарников не нужны.
        assertThat(focusTraining).contains("aria-valuemax=${interviewSession.getTotal()}");
        assertThat(focusTraining).contains("aria-valuenow=${interviewSession.getIndex() + 1}");
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
        // Эмодзи 🏁 заменён монохромной Lucide-иконкой #i-flag (система иконок).
        assertThat(focusTraining).contains("#i-flag");
        assertThat(focusTraining).contains("Сессия завершена.");
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
        // Кнопка «доп. анализ» (#extra-analysis-toggle-result) удалена вместе с
        // AI-провайдерами (AIR-1) — на result.html остаётся только серверный блок
        // «Похожие вопросы» (#result-related-questions). Проверка порядка
        // next-btn ↔ вторичная кнопка больше неприменима.
        assertThat(result).doesNotContain("id=\"extra-analysis-toggle-result\"");
        assertThat(result).contains("id=\"result-related-questions\"");
        assertThat(stats).contains("showPrimaryNav=true");
        assertThat(settings).contains("showPrimaryNav=true");
        assertThat(result).contains("fragments/inline-alert :: inline-alert");
    }

    @Test
    void settingsTemplateUsesTabStructure() throws IOException {
        String settings = readTemplate("templates/settings.html");
        // Редизайн: 4-карточная стопка заменена вкладками (Сессия | Оформление |
        // Данные). Один раздел за раз вместо ~3.6 экрана скролла. PE: tablist скрыт
        // без JS, app.js (initSettingsTabs) раскрывает + ставит .js-tabs.
        assertThat(settings).contains("id=\"main-content\"");
        assertThat(settings).contains("id=\"settings-tablist\"");
        assertThat(settings).contains("role=\"tablist\"");
        assertThat(settings).contains("class=\"settings-tablist hidden\"");
        assertThat(settings).contains("type=\"button\"");
        // Три панели-tabpanel.
        assertThat(settings).contains("id=\"panel-session\"");
        assertThat(settings).contains("id=\"panel-appearance\"");
        assertThat(settings).contains("id=\"panel-data\"");
        assertThat(settings).contains("role=\"tabpanel\"");
        // Функциональные ID форм/контролов сохранены (relayout, не смена поведения).
        assertThat(settings).contains("id=\"filters-form\"");
        assertThat(settings).contains("id=\"session-form\"");
        assertThat(settings).contains("id=\"session-mode-select\"");
        assertThat(settings).contains("id=\"personalization-card\"");
        assertThat(settings).contains("id=\"data-export-block\"");
        // Старый sidebar-каркас и его мёртвый тоггл сворачивания убраны редизайном.
        assertThat(settings).doesNotContain("id=\"left-sidebar-card\"");
        assertThat(settings).doesNotContain("id=\"sidebar-collapse-toggle\"");
    }

    @Test
    void settingsResetOptionsExplainsActualRecoveryFlow() throws IOException {
        String settings = readTemplate("templates/settings.html");

        assertThat(settings).contains("<h3>Экспорт данных</h3>");
        assertThat(settings).contains("Удалить варианты ответов у всех вопросов");
        assertThat(settings).contains("До перезапуска приложения");
        assertThat(settings).contains("при запуске варианты восстановятся из JSON-сидеров");
        assertThat(settings).doesNotContain("сгенерированные AI варианты ответов");
        assertThat(settings).doesNotContain("сгенерированы заново при следующем показе");
        assertThat(settings).doesNotContain("будут перегенерированы заново");
    }

    @Test
    void settingsPersonalizationAnnouncesPersistedChanges() throws IOException {
        String settings = readTemplate("templates/settings.html");
        String appJs = readTemplate("static/js/app.js");

        assertThat(settings).contains("id=\"personalization-status\"");
        assertThat(settings).contains("role=\"status\" aria-live=\"polite\" aria-atomic=\"true\"");
        assertThat(appJs).contains("Сохранено на этом устройстве: ");
        assertThat(appJs).contains("if (getCurrent() === next) return;");
        assertThat(appJs).contains("announceSaved('Размер шрифта'");
    }

    @Test
    void settingsFontStepperExposesPercentAndResetExplicitly() throws IOException {
        String settings = readTemplate("templates/settings.html");
        String appJs = readTemplate("static/js/app.js");

        assertThat(settings).contains("id=\"font-scale-value\"");
        assertThat(settings).contains(">100%</span>");
        assertThat(settings).contains("id=\"font-reset\">Сбросить</button>");
        assertThat(appJs).contains("valEl.textContent = Math.round(s * 100) + '%'");
        assertThat(appJs).contains("resetBtn.setAttribute('aria-disabled', s === 1 ? 'true' : 'false')");
    }

    @Test
    void focusPageMastheadYieldsToQuestionOnMobile() throws IOException {
        String css = readTemplate("static/css/base.css");

        // Desktop focus: title capped at 2xl so question (3xl) dominates.
        assertThat(css).contains("html[data-design] .focus-page .ed-masthead-title {");
        assertThat(css).contains("var(--font-size-2xl)");
        // Mobile focus must restate the compact clamp — otherwise the desktop
        // focus rule (higher specificity) blocks the generic mobile title rule.
        assertThat(css).contains("html[data-design] .focus-page .ed-masthead-title {\n    font-size: clamp(1.15rem, 5vw, 1.35rem);");
        // Session-only compact padding; never sticky (FNO).
        assertThat(css).contains(".focus-page .ed-masthead:has(.ed-masthead-progress) .ed-masthead-inner");
        assertThat(css).doesNotContain(".ed-masthead { position: sticky");
        assertThat(css).doesNotContain(".ed-masthead{\n  position: sticky");
    }

    @Test
    void statsTemplateKeepsSortableLiveRegionAccessibilityContract() throws IOException {
        String stats = readTemplate("templates/stats.html");
        String statsJs = readTemplate("static/js/stats.js");
        assertThat(stats).contains("id=\"table-sort-status\"");
        assertThat(stats).contains("aria-live=\"polite\"");
        assertThat(stats).contains("aria-atomic=\"true\"");
        // aria-keyshortcuts сортируемым th проставляет stats.js (прогрессивное
        // улучшение: без JS сортировки нет — атрибут в шаблоне был бы враньём).
        assertThat(statsJs).contains("aria-keyshortcuts', 'Enter Space'");
        assertThat(stats).contains("role=\"columnheader\"");
        assertThat(stats).contains("id=\"topicProgressChartFallback\"");
        assertThat(stats).contains("id=\"topicAccuracyChartFallback\"");
        assertThat(stats).contains("class=\"chart-fallback hidden\"");
        assertThat(stats).contains("role=\"status\"");
        assertThat(stats).contains("class=\"forecast-count\" aria-hidden=\"true\"");
        // Свёрнутые строки исключаются из layout/accessibility tree, а после
        // сортировки лимит применяется заново к новому DOM-порядку.
        assertThat(stats).contains("row.hidden = collapsed && index >= limit");
        assertThat(stats).contains("new MutationObserver(sync).observe(tbody, { childList: true })");
        assertThat(readTemplate("static/css/base.css"))
            .contains(".stats-page .topic-table tr[hidden] { display: none !important; }")
            .contains(".stats-page .topic-table tr[hidden] { display: table-row !important; }");
        // STA-5/STJ-1: читаемые labels + без декоративной x-сетки.
        assertThat(statsJs).contains("font: { size: 12 }");
        assertThat(statsJs).contains("grid: { display: false }");
        assertThat(statsJs).contains("tooltip: { enabled: true }");
    }

    @Test
    void baseCssScopesPageOverridesToBodyClassAndIsSelfContained() throws IOException {
        // Двухслойная архитектура switchable-дизайнов: tokens.css определяет ВСЕ
        // токены, base.css — design-agnostic структуру (потребляет только
        // var(--token)). Контракт base.css:
        // (1) страничные оверрайды неймспейснуты body-классом под нейтральным
        //     html[data-design] (не протекают между страницами, не цепляют чужой
        //     каркас, работают под любым дизайном),
        // (2) самодостаточен: bare-reset + утилиты, на которые опираются шаблоны.
        String css = readTemplate("static/css/base.css");

        assertThat(css).contains("html[data-design] .focus-page");
        assertThat(css).contains("html[data-design] .result-page");
        assertThat(css).contains("html[data-design] .stats-page");
        assertThat(css).contains("html[data-design] .settings-page");
        assertThat(css).contains("html[data-design] .summary-page");
        assertThat(css).contains("html[data-design] .error-page");

        // Самодостаточность: глобальный reset + утилиты, на которые опираются шаблоны.
        assertThat(css).contains("box-sizing: border-box");
        assertThat(css).contains(".d-inline");
        assertThat(css).contains(".flex-shrink-0");
        assertThat(css).contains(".flex-1");
        assertThat(css).contains(".mt-3");
        assertThat(css).contains(".overflow-x-auto");
        assertThat(css).contains(".hidden");
    }

    @Test
    void tokensCssDefinesDefaultDesignAndSwitchableRoster() throws IOException {
        // tokens.css — токен-слой switchable-дизайнов. Контракт:
        // (1) дефолт-дизайн editorial живёт в :root (без [data-design] обёртки),
        // (2) каждый не-дефолтный дизайн роестра имеет свой scoped-блок,
        // (3) у каждого есть dark-вариант (полный набор цветов под [data-theme=dark]).
        String css = readTemplate("static/css/tokens.css");

        // Дефолт: editorial = :root, ключевые семантические токены определены.
        assertThat(css).contains(":root");
        assertThat(css).contains("--color-bg-primary");
        assertThat(css).contains("--color-accent-primary");

        // Роестр switchable-дизайнов: light + dark на каждый.
        for (String design : new String[] { "linear", "swiss", "notion", "mintlify", "broadsheet" }) {
            assertThat(css)
                .as("light-блок дизайна " + design)
                .contains("html[data-design=\"" + design + "\"] {");
            assertThat(css)
                .as("dark-блок дизайна " + design)
                .contains("html[data-design=\"" + design + "\"][data-theme=\"dark\"]");
        }
    }
}
