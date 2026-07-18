package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        // Редизайн (ui-redesign-handoff): masthead заменён каркасом shell —
        // sidebar (левая нав) + topbar + bottom-nav. Все страницы подключают их.
        assertThat(focusTraining).contains("fragments/sidebar :: sidebar");
        assertThat(focusTraining).contains("fragments/topbar :: topbar");
        assertThat(focusTraining).contains("fragments/mobile-nav :: mobile-nav");
        assertThat(focusTraining).contains("href=\"#main-content\"");
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
        assertThat(result).contains("fragments/sidebar :: sidebar");
        assertThat(result).contains("class=\"question-main flow-stack-md\"");
        assertThat(result).contains("class=\"result-actions\"");
        assertThat(result).contains("th:if=\"${interviewSession != null and interviewSession.finished}\"");
        // Кнопка «доп. анализ» (#extra-analysis-toggle-result) удалена вместе с
        // AI-провайдерами (AIR-1) — на result.html остаётся только серверный блок
        // «Похожие вопросы» (#result-related-questions). Проверка порядка
        // next-btn ↔ вторичная кнопка больше неприменима.
        assertThat(result).doesNotContain("id=\"extra-analysis-toggle-result\"");
        assertThat(result).contains("id=\"result-related-questions\"");
        // RES-16 (R0.145): список «Похожих вопросов» = role=list › role=listitem-обёртка › <a>.
        // role=listitem НЕ на самом <a> — иначе ARIA-роль перекрывает нативную link-роль, и SR
        // анонсирует «элемент списка» вместо «ссылка» + не находит в rotor'е ссылок (WCAG 4.1.2
        // role≠function). Гард против регресса к паттерну a[role=listitem] (был до R0.145).
        Matcher relatedNesting = Pattern.compile(
                "related-questions-list\"\\s+role=\"list\">\\s*<div[^>]*role=\"listitem\"[^>]*>\\s*<a",
                Pattern.DOTALL).matcher(result);
        assertThat(relatedNesting.find())
                .as("related-list: role=list › listitem-обёртка › <a>").isTrue();
        Matcher relatedAnchor = Pattern.compile("<a[^>]*class=\"related-question-item\"[^>]*>", Pattern.DOTALL)
                .matcher(result);
        assertThat(relatedAnchor.find()).as("related-question-item <a> присутствует").isTrue();
        assertThat(relatedAnchor.group())
                .as("link-роль сохранена — нет ARIA role= на <a>").doesNotContain("role=");
        assertThat(stats).contains("fragments/sidebar :: sidebar");
        assertThat(settings).contains("fragments/sidebar :: sidebar");
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
    void tokensCssDefinesSingleInstrumentDesign() throws IOException {
        // tokens.css — токен-слой. SINGLE-SOURCE (DEADCODE-1, 2026-07-16): Instrument-
        // значения живут ПРЯМО в :root/:root[data-theme="dark"], без html[data-design]
        // override-слоя. Контракт:
        // (1) :root несёт Instrument light OKLCH-значения (не warm editorial-базу),
        // (2) :root[data-theme="dark"] несёт Instrument dark,
        // (3) НИ ОДНОГО html[data-design="X"] scoped-цветоблока (промоут удалил и
        //     instrument-override, и роестр удалённых дизайнов),
        // (4) warm ivory/clay editorial-наследие (#FAF9F5 / #D97757) не рендерится.
        String css = readTemplate("static/css/tokens.css");

        // База: :root с Instrument-значениями (cool paper + teal-signal, OKLCH).
        assertThat(css).contains(":root");
        assertThat(css).contains("--color-bg-primary");
        assertThat(css).contains("--color-accent-primary");
        assertThat(css)
            .as(":root несёт синтезированную светлую палитру редизайна (cool near-white)")
            .contains("--color-bg-primary:   #eef2f2");
        assertThat(css)
            .as(":root[data-theme=\"dark\"] несёт тёмный canvas редизайна (#071016)")
            .contains(":root[data-theme=\"dark\"]");
        assertThat(css)
            .as("dark canvas — точный из ui-redesign-handoff")
            .contains("--color-bg-primary:   #071016");
        assertThat(css)
            .as("teal-accent редизайна присутствует")
            .contains("#58d4d0");

        // SINGLE-SOURCE: НИ ОДНОГО html[data-design="X"] цвето-override-блока —
        // включая instrument (промоут DEADCODE-1 перенёс его в :root).
        for (String dead : new String[] { "instrument", "editorial", "linear", "swiss", "notion",
                                          "mintlify", "broadsheet", "superhuman", "stripe", "claude", "theverge" }) {
            assertThat(css)
                .as("дизайн " + dead + " не должен иметь scoped-цветоблок в tokens.css (single-source :root)")
                .doesNotContain("html[data-design=\"" + dead + "\"] {");
        }

        // Guard против реинтродукции warm editorial-цветобазы как ЖИВЫХ значений
        // (комментарии-упоминания допустимы — проверяем именно объявления токенов).
        assertThat(css)
            .as("warm ivory #FAF9F5 не должен быть значением токена (только в комментарии-истории)")
            .doesNotContain(": #FAF9F5");
        assertThat(css)
            .as("warm Clay #D97757 не должен быть значением токена")
            .doesNotContain(": #D97757");
    }

    @Test
    void errorPageExposesAccessibleHeadingAndActions() throws IOException {
        String error = readTemplate("templates/error.html");

        assertThat(error).contains("<body class=\"error-page\">");
        // skip-link клавиатурно обходит шапку к сообщению об ошибке.
        assertThat(error).contains("href=\"#main-content\"");
        assertThat(error).contains("id=\"main-content\"");
        // Секция ошибки подписана своим заголовком: aria-labelledby ↔ h2 id
        // (реальный заголовок — h2, а не декоративный код-номер).
        assertThat(error).contains("aria-labelledby=\"error-heading\"");
        assertThat(error).contains("id=\"error-heading\"");
        // Декоративные эйбрау и статус-номер скрыты от SR.
        assertThat(error).contains("class=\"error-eyebrow\" aria-hidden=\"true\"");
        assertThat(error).contains("class=\"error-code\"");
        // Явные пути дальше — nav с меткой (тупик IA исправлен).
        assertThat(error).contains("<nav class=\"error-actions\" aria-label=\"Что дальше\">");
    }

    @Test
    void sessionSummaryExposesScoreAnchorAndTableSemantics() throws IOException {
        String summary = readTemplate("templates/session-summary.html");

        assertThat(summary).contains("<body class=\"summary-page\">");
        assertThat(summary).contains("href=\"#main-content\"");
        assertThat(summary).contains("id=\"main-content\"");
        // SUM-9: визуально скрытый h2 счёта — якорь H-навигации на главный
        // результат (паритет с h2 секций-сестёр «по темам»/«ошибки»/«рекомендации»).
        assertThat(summary).contains("<h2 class=\"summary-section-title visually-hidden\">Результат сессии</h2>");
        assertThat(summary).contains("class=\"summary-score-value\"");
        // SUM-3: dual-path действия (повтор ошибок / продолжить) в nav с меткой.
        assertThat(summary).contains("<nav class=\"summary-actions\" aria-label=\"Что дальше\">");
        // Таблица по темам: CSS display:block сбрасывает нативную семантику в
        // Safari/VoiceOver → явные ARIA-роли её восстанавливают; tabindex=0 делает
        // скролл-контейнер доступным с клавиатуры (WCAG 2.1.11). Аддитивно.
        assertThat(summary).contains("<table class=\"summary-table\" role=\"table\" tabindex=\"0\">");
        assertThat(summary).contains("<caption class=\"visually-hidden\">");
        assertThat(summary).contains("scope=\"col\" role=\"columnheader\"");
        assertThat(summary).contains("role=\"rowheader\"");
        // Share/print-тулбар инжектится PE-скриптом; статус-регион вежливый.
        assertThat(summary).contains("'role', 'status'");
        assertThat(summary).contains("'aria-live', 'polite'");
    }

    @Test
    void headFragmentAppliesPersonalizationAxesBeforeFirstPaint() throws IOException {
        String head = readTemplate("templates/fragments/head.html");

        assertThat(head).contains("th:fragment=\"head(title, includeMermaid, includeChart)\"");
        assertThat(head).contains("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        assertThat(head).contains("<title th:text=\"${title}\">");
        // Оси персонализации применяются на <html> ДО первого кадра (анти-FOUC):
        // design/theme/layout выставляются, motion/reading-width/density —
        // set-или-remove (дефолт = отсутствие атрибута).
        assertThat(head).contains("setAttribute('data-design'");
        assertThat(head).contains("setAttribute('data-theme'");
        assertThat(head).contains("setAttribute('data-layout'");
        assertThat(head).contains("setAttribute('data-motion'");
        assertThat(head).contains("setAttribute('data-reading-width'");
        assertThat(head).contains("setAttribute('data-density'");
        // CDN-ресурсы пиннятся SRI (integrity, sha512) + crossorigin — защита от
        // компрометации CDN. Префикс sha512- устойчив к бампу версии библиотеки.
        assertThat(head).contains("integrity=\"sha512-");
        assertThat(head).contains("crossorigin=\"anonymous\"");
    }

    @Test
    void shellFragmentsExposeNavLandmarksAndActiveContract() throws IOException {
        // Редизайн (ui-redesign-handoff): masthead → каркас shell. Контракт:
        // sidebar — nav-landmark с меткой + aria-current на активном пункте;
        // topbar — прогресс-progressbar сессии + тоггл темы (aria-pressed);
        // mobile-nav — nav-landmark нижней навигации.
        String sidebar = readTemplate("templates/fragments/sidebar.html");
        String topbar = readTemplate("templates/fragments/topbar.html");
        String mobileNav = readTemplate("templates/fragments/mobile-nav.html");

        assertThat(sidebar).contains("th:fragment=\"sidebar(active)\"");
        assertThat(sidebar).contains("<nav class=\"nav\" aria-label=\"Основная навигация\">");
        assertThat(sidebar).contains("aria-current=${active == 'focus'} ? 'page' : null");
        // Тоггл темы — aria-pressed; PE: раскрывается только при JS (класс hidden).
        assertThat(sidebar).contains("data-theme-toggle");
        assertThat(sidebar).contains("aria-pressed=\"false\"");

        assertThat(topbar).contains("th:fragment=\"topbar\"");
        assertThat(topbar).contains("role=\"progressbar\"");

        assertThat(mobileNav).contains("th:fragment=\"mobile-nav(active)\"");
        assertThat(mobileNav).contains("class=\"mobile-nav\" aria-label=\"Мобильная навигация\"");
        assertThat(mobileNav).contains("aria-current=${active == 'focus'} ? 'page' : null");
    }

    @Test
    void everyDefinedTemplateFragmentIsReferencedNoOrphans() throws IOException {
        // Регресс-гвард против «сироты-фрагмента»: каждый th:fragment, объявленный
        // где-либо под templates/, ДОЛЖЕН подключаться хотя бы одним
        // th:replace/th:insert (~{... :: name}) в корпусе шаблонов. Иначе фрагмент —
        // мёртвый код (определён, но не рендерится). Тест сам перечисляет фрагменты
        // (не хардкод-роестр) → авто-покрывает будущие фрагменты. App не нужен:
        // чистое чтение ресурсов с classpath. Ручной orphan-аудит R0.136 → инвариант.
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();

        StringBuilder corpusBuilder = new StringBuilder();
        try (Stream<Path> walk = Files.walk(templatesDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".html")).sorted()::iterator) {
                corpusBuilder.append(new String(Files.readAllBytes(p), StandardCharsets.UTF_8)).append('\n');
            }
        }
        String corpus = corpusBuilder.toString();

        Matcher matcher = Pattern.compile("th:fragment=\"([A-Za-z0-9_-]+)").matcher(corpus);
        Set<String> fragmentNames = new LinkedHashSet<>();
        while (matcher.find()) {
            fragmentNames.add(matcher.group(1));
        }

        // Санити: известный минимум действительно найден (иначе разметка/путь
        // изменились и тест молча пустой — false-negative на orphan).
        assertThat(fragmentNames)
            .as("объявленные th:fragment под templates/")
            .contains("head", "sprite", "sidebar", "topbar", "mobile-nav", "shell-scripts",
                      "inline-alert", "post-answer-controls",
                      "result-zone-head", "stats-grid", "stats-grid-content",
                      "today-hero", "today-chip", "training-actions", "mermaid-init");

        for (String name : fragmentNames) {
            // Ссылка — либо параметризованная `:: name(`, либо bare `:: name}`.
            // Граница (`(`/`}`) не даёт `stats-grid` ложно засчитаться через
            // `stats-grid-content`, а `head` — через `header`.
            boolean referenced = corpus.contains(":: " + name + "(")
                              || corpus.contains(":: " + name + "}");
            assertThat(referenced)
                .as("фрагмент '%s' обязан подключаться th:replace/th:insert (иначе orphan/dead-code)", name)
                .isTrue();
        }
    }

    @Test
    void iconSpriteHasNoOrphanSymbols() throws IOException {
        // Регресс-гвард против «сироты-иконки»: каждый <symbol id="i-X"> в спрайте
        // (fragments/icons.html) обязан использоваться хотя бы одной ссылкой — либо
        // <use href="#i-X"> в шаблоне, либо JS-хелпером icon('X')/svgIcon('X')
        // (аргумент БЕЗ префикса i-, хелпер сам добавляет #i-, см. app.js:38/1876).
        // Неиспользуемый символ грузится на КАЖДОЙ странице (спрайт инлайнится через
        // header-фрагмент) = мёртвый payload. App не нужен: чтение ресурсов.
        // Ручной orphan-аудит R0.137 (снял i-bot/chart/lightbulb/refresh/search,
        // осиротевшие после AI/regenerate-removal) → инвариант.
        String sprite = readTemplate("templates/fragments/icons.html");

        Matcher symbolMatcher = Pattern.compile("<symbol id=\"(i-[a-z0-9-]+)\"").matcher(sprite);
        Set<String> declared = new LinkedHashSet<>();
        while (symbolMatcher.find()) {
            declared.add(symbolMatcher.group(1));
        }

        // Корпус ссылок = все шаблоны + весь JS.
        StringBuilder corpusBuilder = new StringBuilder();
        for (String dir : new String[] { "templates", "static/js" }) {
            Path root = new ClassPathResource(dir).getFile().toPath();
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path p : (Iterable<Path>) walk
                        .filter(p -> p.toString().endsWith(".html") || p.toString().endsWith(".js"))
                        .sorted()::iterator) {
                    corpusBuilder.append(new String(Files.readAllBytes(p), StandardCharsets.UTF_8)).append('\n');
                }
            }
        }
        String refs = corpusBuilder.toString();

        // Использованные = прямые #i-X (шаблонный <use>) + аргументы хелперов
        // icon('X')/svgIcon('X') (в них имя без i-, добавляем префикс).
        Set<String> referenced = new LinkedHashSet<>();
        Matcher hrefMatcher = Pattern.compile("#(i-[a-z0-9-]+)").matcher(refs);
        while (hrefMatcher.find()) {
            referenced.add(hrefMatcher.group(1));
        }
        Matcher helperMatcher = Pattern.compile("(?:svgIcon|icon)\\([\"']([a-z0-9-]+)").matcher(refs);
        while (helperMatcher.find()) {
            referenced.add("i-" + helperMatcher.group(1));
        }

        // Санити: спрайт непустой и известные живые иконки на месте.
        assertThat(declared).contains("i-star", "i-check", "i-copy", "i-flag", "i-download");

        for (String id : declared) {
            assertThat(referenced)
                .as("иконка '%s' объявлена в спрайте, но не используется (<use href=#%s> или icon('%s')) — orphan/dead payload", id, id, id.substring(2))
                .contains(id);
        }
    }

    @Test
    void statsTopicTableSortableColumnsHaveMatchingMobileLabels() throws IOException {
        // .topic-table — ЕДИНСТВЕННАЯ таблица с card-view на мобиле (base.css
        // td[data-label]::before { content: attr(data-label) } — заголовки схлопнуты,
        // подпись столбца берётся из data-label на td). Инвариант: каждый сортируемый
        // столбец (th data-sort-label="X") обязан иметь парный td data-label="X",
        // иначе на узком экране ячейка теряет подпись столбца. Гвард против
        // STA-20-класса рассинхрона (R0.128 снял колонку «Сброшено», сдвинул data-col
        // 5→4 — правка структуры таблицы легко рвёт пары label↔header). App не нужен.
        // summary-table (итоги) теперь ТОЖЕ card-view на мобиле (#6, data-label на
        // td), но без сортируемых столбцов — этот инвариант про пары th↔td у
        // сортировки topic-table, поэтому summary-table в него не входит. .data-table
        // (coverage-gaps) — обычная узкая, data-label не нужен. Тест читает stats.html.
        String stats = readTemplate("templates/stats.html");

        Matcher sortLabelMatcher = Pattern.compile("data-sort-label=\"([^\"]+)\"").matcher(stats);
        Set<String> sortLabels = new LinkedHashSet<>();
        while (sortLabelMatcher.find()) {
            sortLabels.add(sortLabelMatcher.group(1));
        }
        Matcher dataLabelMatcher = Pattern.compile("data-label=\"([^\"]+)\"").matcher(stats);
        Set<String> dataLabels = new LinkedHashSet<>();
        while (dataLabelMatcher.find()) {
            dataLabels.add(dataLabelMatcher.group(1));
        }

        // Санити: сортируемые столбцы topic-table реально извлеклись.
        assertThat(sortLabels).contains("Тема", "Всего", "Точность", "Зрелость");
        // Каждый сортируемый заголовок имеет парную мобильную подпись (card-view).
        assertThat(dataLabels).containsAll(sortLabels);
    }

    @Test
    void ariaIdRefsResolveWithinEachRenderedPage() throws IOException {
        // A11y-инвариант (WCAG 1.3.1/4.1.2): каждый aria-labelledby/aria-controls/
        // aria-describedby/aria-activedescendant/for="X" обязан указывать на
        // существующий id="X" в ОТРЕНДЕРЕННОМ документе = страница + все её
        // th:replace-фрагменты. Битый idref = SR не находит цель (пустое объявление /
        // разорванная связь). Аудит R0.139: 0 динамич. th:id, ни один idref не
        // указывает на JS-инъекцию → всё резолвится статически. App не нужен.
        String[] pages = { "error", "focus-training", "result", "session-summary", "settings", "stats" };
        Pattern includePattern = Pattern.compile("~\\{fragments/([a-z-]+)");
        Pattern idPattern = Pattern.compile("\\bid=\"([a-zA-Z][a-zA-Z0-9_-]*)\"");
        // Значение без " и без $ → чисто статические idref (Thymeleaf ${...} исключены
        // самим классом: значение, начинающееся с $, не даёт совпадения).
        Pattern refPattern = Pattern.compile(
            "\\b(?:aria-labelledby|aria-controls|aria-describedby|aria-activedescendant|for)=\"([^\"$]+)\"");

        int checkedRefs = 0;
        for (String page : pages) {
            String pageHtml = readTemplate("templates/" + page + ".html");
            StringBuilder doc = new StringBuilder(pageHtml);
            Matcher inc = includePattern.matcher(pageHtml);
            Set<String> frags = new LinkedHashSet<>();
            while (inc.find()) {
                frags.add(inc.group(1));
            }
            for (String frag : frags) {
                doc.append('\n').append(readTemplate("templates/fragments/" + frag + ".html"));
            }
            String rendered = doc.toString();

            Set<String> ids = new LinkedHashSet<>();
            Matcher idm = idPattern.matcher(rendered);
            while (idm.find()) {
                ids.add(idm.group(1));
            }

            Matcher refm = refPattern.matcher(rendered);
            while (refm.find()) {
                for (String token : refm.group(1).trim().split("\\s+")) {
                    if (token.isEmpty()) {
                        continue;
                    }
                    checkedRefs++;
                    assertThat(ids)
                        .as("страница %s: ARIA-idref '%s' не резолвится в id в пределах документа (страница+фрагменты) — битая связь", page, token)
                        .contains(token);
                }
            }
        }
        // Санити: аудит реально нашёл idref (иначе regex сломан → false-negative).
        assertThat(checkedRefs).as("проверенных ARIA-idref").isGreaterThanOrEqualTo(25);
    }

    @Test
    void versionedAssetsAreConsistentAcrossTemplates() throws IOException {
        // Cache-busting контракт (HEAD-1/APP-7/FT-17/SET-12): ассет, подключённый
        // из НЕСКОЛЬКИХ шаблонов через @{/js|css/NAME(v=N)}, обязан нести ОДНУ версию
        // во всех точках. Дрейф (result.html app.js=v=64, focus=v=63) = устаревший JS
        // на части страниц после правки. PERF-01: app.js вынесен в единый фрагмент
        // fragments/scripts.html (focus/result/settings подключают через th:replace),
        // поэтому версия структурно одна. CSS — единый head.html (per-file версии
        // независимы: tokens и base не обязаны быть равны). App не нужен.
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();
        Pattern assetPattern = Pattern.compile("/(?:js|css)/([a-zA-Z._-]+\\.(?:js|css))\\(v=(\\d+)\\)");

        Map<String, Set<String>> versionsByAsset = new LinkedHashMap<>();
        try (Stream<Path> walk = Files.walk(templatesDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".html")).sorted()::iterator) {
                String html = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                Matcher m = assetPattern.matcher(html);
                while (m.find()) {
                    versionsByAsset.computeIfAbsent(m.group(1), k -> new LinkedHashSet<>()).add(m.group(2));
                }
            }
        }

        // Санити: контракт реально нашёл версионированные ассеты (app.js в 3 шаблонах).
        assertThat(versionsByAsset).containsKey("app.js");

        for (Map.Entry<String, Set<String>> e : versionsByAsset.entrySet()) {
            assertThat(e.getValue())
                .as("ассет '%s' должен нести одну версию во всех шаблонах, найдено: %s", e.getKey(), e.getValue())
                .hasSize(1);
        }
    }

    @Test
    void appJsIsSingleSourcedInFragment() throws IOException {
        // PERF-01: тег <script src="/js/app.js(v=N)"> должен встречаться в дереве шаблонов
        // РОВНО один раз — в fragments/scripts.html; страницы подключают его через
        // th:replace="~{fragments/scripts :: app-script}". Это делает дрейф версии
        // структурно невозможным (нет второй точки правки). Инлайн-возврат тега в любую
        // страницу «отвяжет» версию — ассерт ловит регресс.
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();
        Pattern includePattern = Pattern.compile("/js/app\\.js\\(v=\\d+\\)");

        List<Path> filesWithInclude = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(templatesDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".html")).sorted()::iterator) {
                String html = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                if (includePattern.matcher(html).find()) {
                    filesWithInclude.add(templatesDir.relativize(p));
                }
            }
        }

        assertThat(filesWithInclude)
            .as("app.js должен подключаться из единственного файла (fragments/scripts.html), найдено: %s", filesWithInclude)
            .hasSize(1);
        assertThat(filesWithInclude.get(0).toString().replace('\\', '/'))
            .isEqualTo("fragments/scripts.html");
    }
}
