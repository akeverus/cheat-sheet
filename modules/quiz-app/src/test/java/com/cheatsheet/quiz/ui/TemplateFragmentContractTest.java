package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.config.web.JsPreloadService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
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

    /**
     * Строка манифеста каскада: {@code <гейт> <путь>}. Комментарии и пустые строки
     * отсеиваются до применения.
     */
    private static final Pattern MANIFEST_ENTRY = Pattern.compile("^(\\S+)\\s+(\\S+)$");

    /**
     * Комментарии CSS. Вырезаются перед всеми проверками каскада: браузер их не
     * видит, а тесты — видели. На этом уже споткнулся гвард мёртвой шапки:
     * комментарий, объясняющий удаление {@code .ed-masthead}, сам содержал этот
     * селектор и «воскрешал» правило для теста. Проза о правиле не должна ломать
     * проверку правила.
     */
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /**
     * Якорь прогрессивного улучшения → модуль, который его оживляет.
     *
     * <p>Каждая строка — разметка, которую сервер отдаёт в развёрнутом,
     * работающем без JS виде: стопка панелей вместо вкладок, страница итогов без
     * кнопок «скопировать». Пока модуль не выполнится, пользователь видит именно
     * этот запасной вариант — и видит его молча. Ошибки в консоли нет, вёрстка
     * цела, 404 нет; отличить «улучшение выключено» от «улучшение сломано»
     * глазами нельзя.
     *
     * <p>Так и жили вкладки материалов вопроса: {@code initContentTabs} лежала в
     * {@code settings/tabs.js} и попадала в чанк, который грузится только на
     * {@code body.settings-page}. Разметка {@code .question-materials} есть лишь в
     * {@code focus-training.html}, поэтому функция не находила её ни разу — на
     * settings нечего активировать, а на focus её просто не было. Вкладки не
     * открылись ни у одного пользователя за всё время существования фичи.
     */
    private static final Map<String, String> PROGRESSIVE_ENHANCEMENT_ANCHORS = Map.of(
            ".question-materials", "./content/materials-tabs.js",
            "#settings-tablist", "./settings/tabs.js",
            "#stats-tablist", "./stats/index.js",
            ".summary-hero", "./summary/share-tools.js");

    private String readTemplate(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        byte[] bytes = resource.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Пути CSS в порядке каскада — так, как их склеивает {@code CssBundleService}.
     *
     * <p>Список намеренно не захардкожен в тесте: файлы дробят и переименовывают,
     * и копия молча разъезжается с реальностью (так и случилось с
     * {@code pages-layout.css}) — контракт тогда проверяет не то, что грузится.
     * Раньше источником был список {@code <link>} в {@code head.html}; после
     * склейки в одну ленту порядок живёт в манифесте, и читаем мы его.</p>
     */
    private List<String> manifestPaths() throws IOException {
        List<String> paths = new ArrayList<>();
        for (String raw : readTemplate("css-bundle-manifest.txt").split("\n")) {
            Matcher matcher = MANIFEST_ENTRY.matcher(raw.strip());
            if (matcher.matches() && !raw.strip().startsWith("#")) {
                paths.add(matcher.group(2));
            }
        }
        assertThat(paths).as("манифест каскада не должен быть пустым").isNotEmpty();
        return paths;
    }

    /**
     * Склейка всего структурного CSS в порядке каскада — ровно то, что получает
     * браузер. Фильтр {@code /css/} оставляет только наши файлы; вендорных CSS в
     * манифесте больше нет (тема подсветки своя, на токенах), но фильтр держим —
     * он же гарантирует, что вендорный CSS туда не вернётся незаметно.
     */
    private String readStructuralCss() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String path : manifestPaths()) {
            if (path.startsWith("/css/")) {
                sb.append(CSS_COMMENT.matcher(readTemplate("static" + path)).replaceAll("\n"));
            }
        }
        return sb.toString();
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
        // WCAG 4.1.3: краткий SR-анонс вердикта — отдельный live-регион #result-announce.
        // #result-feedback (полный разбор ~2900 симв.) НЕ live-регион, чтобы SR не зачитывал
        // весь блок атомарно; туда переносится focus, детали читаются в своём темпе.
        assertThat(postAnswerControls).contains("id=\"result-announce\"");
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
    void questionMaterialLabelsComeFromBlocksAndAreNotWrittenByHand() throws IOException {
        String focusTraining = readTemplate("templates/focus-training.html");

        // Подпись вкладки и заголовок её панели — одна строка из блока. Пока их
        // писали руками, у двух объектов было четыре имени: вкладка «Пример кода»
        // вела к заголовку «Код к вопросу», вкладка «Схема» — к «Диаграмме».
        assertThat(focusTraining).contains("th:text=\"${codeBlock.title()}\"");
        assertThat(focusTraining).contains("th:text=\"${diagramBlock.title()}\"");
        assertThat(focusTraining).doesNotContain(">Код к вопросу<");
        assertThat(focusTraining).doesNotContain(">Диаграмма<");

        // Тип блока назначает ContentBlockMapper; шаблон не читает codeSnippet и
        // diagramMermaid напрямую — в codeSnippet может лежать нарисованная схема.
        assertThat(focusTraining).doesNotContain("current.question.codeSnippet");
        assertThat(focusTraining).doesNotContain("current.question.diagramMermaid");
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
        assertThat(focusTraining).contains("hintText='Сначала разбор ответа, затем итог и дополнительные блоки'");
        assertThat(focusTraining).contains("focus-session-more");
        assertThat(focusTraining).contains("data-confirm-title=\"Завершить сессию?\"");
        assertThat(focusTraining).contains("extraButtonText='Похожие вопросы'");
        // Трек гейтится th:if="${interviewSession != null}" (вне сессии пустой
        // progressbar 0/0 не рендерится) → null-ветки тернарников не нужны.
        assertThat(focusTraining).contains("aria-valuemax=${interviewSession.getTotal()}");
        assertThat(focusTraining).contains("aria-valuenow=${interviewSession.getIndex() + 1}");
        assertThat(focusTraining).contains("data-progress=${#numbers.formatDecimal(progressPercent, 1, 'NONE', 1, 'POINT')}");
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
        // без JS, settings/tabs.js (initSettingsTabs) раскрывает + ставит .js-tabs.
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
        String personalizationJs = readTemplate("static/js/settings/personalization.js");

        assertThat(settings).contains("id=\"personalization-status\"");
        assertThat(settings).contains("role=\"status\" aria-live=\"polite\" aria-atomic=\"true\"");
        assertThat(personalizationJs).contains("Сохранено на этом устройстве: ");
        assertThat(personalizationJs).contains("if (getCurrent() === next) return;");
        assertThat(personalizationJs).contains("announceSaved('Размер шрифта'");
    }

    @Test
    void settingsFontStepperExposesPercentAndResetExplicitly() throws IOException {
        String settings = readTemplate("templates/settings.html");
        String personalizationJs = readTemplate("static/js/settings/personalization.js");

        assertThat(settings).contains("id=\"font-scale-value\"");
        assertThat(settings).contains(">100%</span>");
        assertThat(settings).contains("id=\"font-reset\">Сбросить</button>");
        assertThat(personalizationJs).contains("valEl.textContent = Math.round(s * 100) + '%'");
        assertThat(personalizationJs).contains("resetBtn.setAttribute('aria-disabled', s === 1 ? 'true' : 'false')");
    }

    /**
     * Мёртвой editorial-шапки .ed-masthead в каскаде быть не должно. Она пережила
     * редизайн только в CSS: разметка (topbar/sidebar) не выводит ни одного
     * класса семейств {@code ed-masthead}, {@code ed-nav} и {@code ed-theme-toggle}
     * с тех пор, как шапку заменил топбар.
     * Правила без разметки не «безвредный хвост» — они выглядят как
     * действующий контракт и приглашают чинить иерархию заголовков там, где её
     * уже нет (прежний тест ровно это и охранял). Актуальный контракт топбара —
     * {@code shellFragmentsExposeNavLandmarksAndActiveContract} ниже.
     */
    @Test
    void structuralCssKeepsNoDeadMastheadRules() throws IOException {
        String css = readStructuralCss();

        assertThat(css).doesNotContain(".ed-masthead");
        assertThat(css).doesNotContain(".ed-nav-link");
        assertThat(css).doesNotContain(".ed-theme-toggle");
    }

    @Test
    void statsTemplateKeepsSortableLiveRegionAccessibilityContract() throws IOException {
        String stats = readTemplate("templates/stats.html");
        String tableSortJs = readTemplate("static/js/stats/table-sort.js");
        String topicChartsJs = readTemplate("static/js/stats/topic-charts.js");
        assertThat(stats).contains("id=\"table-sort-status\"");
        assertThat(stats).contains("aria-live=\"polite\"");
        assertThat(stats).contains("aria-atomic=\"true\"");
        // aria-keyshortcuts сортируемым th проставляет table-sort.js (прогрессивное
        // улучшение: без JS сортировки нет — атрибут в шаблоне был бы враньём).
        assertThat(tableSortJs).contains("aria-keyshortcuts', 'Enter Space'");
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
        assertThat(readStructuralCss())
            .contains(".stats-page .topic-table tr[hidden] { display: none !important; }")
            .contains(".stats-page .topic-table tr[hidden] { display: table-row !important; }");
        // STA-5/STJ-1: читаемые labels + без декоративной x-сетки.
        assertThat(topicChartsJs).contains("font: { size: 12 }");
        assertThat(topicChartsJs).contains("grid: { display: false }");
        assertThat(topicChartsJs).contains("tooltip: { enabled: true }");
    }

    /**
     * Подписи оси X графиков «Темы» обязаны иметь записанный бюджет.
     *
     * <p>Текст подписи приходит из данных (имя темы), а коробка графика задана
     * снаружи (фикс. {@code .chart-wrapper}). Канвас на перебор реагирует молча:
     * ни переполнения в DOM, ни ошибки — Chart.js просто выбрасывает подписи
     * своим {@code autoSkip}. Замер до правки на 1440 (коробка 465×320, 25 баров):
     * 13 подписей из 25, ось 151px из 320 — подписи весили больше данных.</p>
     *
     * <p>Гейт держит три условия, каждое из которых по отдельности возвращает
     * молчаливую потерю: бюджет считается от КОРОБКИ (а не от длины пришедших
     * имён), лишнее обрезается видимым многоточием, и число баров берётся из
     * бюджета, а не из константы {@code MAX_BARS}. Полное имя обязано пережить
     * обрезку — в тултипе и в табличном дубле.</p>
     */
    @Test
    void topicChartsRecordLabelBudgetForXAxisLabels() throws IOException {
        String js = readTemplate("static/js/stats/topic-charts.js");

        // Бюджет выводится из геометрии коробки: ширина → число баров,
        // высота → длина подписи. Канвас до первой отрисовки отдаёт свой
        // интринсик 300×150, поэтому мерить обязаны родителя.
        assertThat(js).contains("canvas?.parentElement ?? canvas");
        assertThat(js).contains("box.width - Y_AXIS_RESERVE_PX) / MIN_BAR_SLOT_PX");
        assertThat(js).contains("box.height * X_AXIS_MAX_SHARE");

        // Промах виден: обрезка многоточием, а не тихий клип канваса.
        assertThat(js).contains("const ELLIPSIS = '…';");
        assertThat(js).contains("shortenToWidth(ctx, labels[index]");

        // autoSkip «решал» проблему, выбрасывая каждую вторую подпись.
        assertThat(js).contains("autoSkip: false");

        // Число баров — из бюджета. Возврат к константе снова оставит бары
        // без подписей: MAX_BARS ничего не знает о ширине коробки.
        assertThat(js).contains("progressRanked.slice(0, progressBudget.bars)");
        assertThat(js).contains("accuracyRanked.slice(0, accuracyBudget.bars)");
        assertThat(js).doesNotContain(".slice(0, MAX_BARS)");

        // Обрезка ничего не теряет: полное имя в тултипе и в таблице-дубле.
        assertThat(js).contains("title: (items) => labels[items[0]?.dataIndex]");
        assertThat(js).contains("progressData.map((t) => [t.name,");
        assertThat(js).contains("accuracyData.map((t) => [t.name,");
    }

    @Test
    void baseCssScopesPageOverridesToBodyClass() throws IOException {
        // Двухслойная архитектура: tokens.css определяет ВСЕ токены, структурный
        // CSS — design-agnostic каркас (потребляет только var(--token)). Контракт:
        // страничные оверрайды неймспейснуты body-классом под нейтральным
        // html[data-design] — не протекают между страницами, не цепляют чужой
        // каркас, работают под любым дизайном.
        String css = readStructuralCss();

        assertThat(css).contains("html[data-design] .focus-page");
        assertThat(css).contains("html[data-design] .result-page");
        assertThat(css).contains("html[data-design] .stats-page");
        assertThat(css).contains("html[data-design] .settings-page");
        assertThat(css).contains("html[data-design] .summary-page");
        assertThat(css).contains("html[data-design] .error-page");

        // Самодостаточность: глобальный reset. Набор атомарных утилит проверяет
        // тест ниже — выведением из разметки, а не списком имён.
        assertThat(css).contains("box-sizing: border-box");
    }

    /**
     * Каждая атомарная утилита обязана иметь носителя в разметке или в JS.
     *
     * <p>Раньше здесь стоял список имён ({@code .d-inline}, {@code .flex-1},
     * {@code .flex-shrink-0}…) с намерением «утилиты, на которые опираются
     * шаблоны». Список — не инвариант: {@code .flex-shrink-0} не носил ни один
     * узел, и тест не просто пропускал мёртвое правило, а ТРЕБОВАЛ его наличия —
     * то есть охранял мусор от удаления. Проверяем само намерение: утилита
     * существует ровно пока ею кто-то пользуется.
     */
    @Test
    void everyAtomicUtilityHasAUserInMarkupOrScripts() throws IOException {
        String utilities = CSS_COMMENT.matcher(readTemplate("static/css/utilities-core.css")).replaceAll("\n");
        String consumers = readTemplatesAndScriptsBlob();

        List<String> unused = new ArrayList<>();
        Matcher declared = Pattern.compile("html\\[data-design]\\s+\\.([a-z0-9-]+)\\s*\\{").matcher(utilities);
        while (declared.find()) {
            String utility = declared.group(1);
            if (!Pattern.compile("\\b" + Pattern.quote(utility) + "\\b").matcher(consumers).find()) {
                unused.add(utility);
            }
        }

        assertThat(unused)
            .as("утилиты в utilities-core.css без единого носителя в templates/ или static/js/ — "
                + "правило пережило свою разметку и должно быть удалено, а не законсервировано "
                + "тестом. Мёртвые: %s", unused)
            .isEmpty();
    }

    /** Всё, что может носить класс: шаблоны + клиентские скрипты. */
    private String readTemplatesAndScriptsBlob() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String root : List.of("templates", "static/js")) {
            Path dir = new ClassPathResource(root).getFile().toPath();
            try (Stream<Path> walk = Files.walk(dir)) {
                for (Path p : (Iterable<Path>) walk.filter(Files::isRegularFile).sorted()::iterator) {
                    sb.append(new String(Files.readAllBytes(p), StandardCharsets.UTF_8)).append('\n');
                }
            }
        }
        return sb.toString();
    }

    @Test
    void tokensCssDefinesSingleInstrumentDesign() throws IOException {
        // tokens.css — токен-слой. SINGLE-SOURCE + DARK-DEFAULT (реставр handoff-3,
        // 2026-07-18): Instrument-значения живут ПРЯМО в :root/:root[data-theme="light"],
        // без html[data-design] override-слоя. Дефолт ИНВЕРТИРОВАН — макеты handoff-3
        // все тёмные, поэтому dark в :root (безусловная база), а light — явный тумблер.
        // Контракт:
        // (1) :root несёт Instrument DARK-значения макетов (canvas #071116),
        // (2) :root[data-theme="light"] несёт light-палитру (вторичный тумблер),
        // (3) НИ ОДНОГО html[data-design="X"] scoped-цветоблока (промоут удалил и
        //     instrument-override, и реестр удалённых дизайнов),
        // (4) warm ivory/clay editorial-наследие (#FAF9F5 / #D97757) не рендерится.
        String css = readTemplate("static/css/tokens.css");

        // База: :root с Instrument DARK-значениями (canvas #071116 + teal-signal).
        assertThat(css).contains(":root");
        assertThat(css).contains("--color-bg-primary");
        assertThat(css).contains("--color-accent-primary");
        assertThat(css)
            .as(":root несёт тёмный canvas макетов handoff-3 (dark-default #071116)")
            .contains("--color-bg-primary:   #071116");
        assertThat(css)
            .as(":root[data-theme=\"light\"] несёт вторичную светлую тему")
            .contains(":root[data-theme=\"light\"]");
        assertThat(css)
            .as("light canvas — из assets/tokens/tokens.css макетов (#f5f8f8)")
            .contains("--color-bg-primary:   #f5f8f8");
        assertThat(css)
            .as("teal-accent макетов handoff-3 присутствует")
            .contains("#39d0c7");

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
        // Секция ошибки подписана своим заголовком: aria-labelledby ↔ h1 id.
        // Реальный заголовок — h1 (единственный на странице), а не декоративный
        // код-номер (aria-hidden). Даёт SR-навигации якорь верхнего уровня.
        assertThat(error).contains("aria-labelledby=\"error-heading\"");
        assertThat(error).contains("<h1 id=\"error-heading\" class=\"error-title\"");
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
        // SUM-9: визуально скрытый h1 счёта — единственный h1 страницы итогов и якорь
        // H-навигации на главный результат; секции «по темам»/«ошибки»/«рекомендации» (h2)
        // выстраиваются под ним в корректную иерархию (одна страница — один h1).
        assertThat(summary).contains("<h1 class=\"summary-section-title visually-hidden\">Результат сессии</h1>");
        assertThat(summary).contains("class=\"summary-score-value\"");
        // SUM-3: dual-path действия (повтор ошибок / продолжить) в nav с меткой.
        assertThat(summary).contains("class=\"summary-actions\"");
        assertThat(summary).contains("aria-label=\"Что дальше\"");
        assertThat(summary).contains("class=\"summary-more\"");
        assertThat(summary).contains("Ещё");
        assertThat(summary).contains("Продолжить тренировку");
        // Таблица по темам: CSS display:block сбрасывает нативную семантику в
        // Safari/VoiceOver → явные ARIA-роли её восстанавливают; tabindex=0 делает
        // скролл-контейнер доступным с клавиатуры (WCAG 2.1.11). Аддитивно.
        assertThat(summary).contains("<table class=\"summary-table\" role=\"table\" tabindex=\"0\">");
        assertThat(summary).contains("<caption class=\"visually-hidden\">");
        assertThat(summary).contains("scope=\"col\" role=\"columnheader\"");
        assertThat(summary).contains("role=\"rowheader\"");
        // Share/print-тулбар инжектится модулем (PE: без JS нет мёртвых кнопок),
        // результат копирования озвучивается вежливым статус-регионом.
        String shareToolsJs = readTemplate("static/js/summary/share-tools.js");
        assertThat(shareToolsJs).contains("'role', 'status'");
        assertThat(shareToolsJs).contains("'aria-live', 'polite'");
    }

    @Test
    void headFragmentAppliesPersonalizationAxesBeforeFirstPaint() throws IOException {
        String head = readTemplate("templates/fragments/head.html");
        String bootstrapJs = readTemplate("static/js/settings/personalization-bootstrap.js");

        assertThat(head).contains("th:fragment=\"head(title, includeMermaid, includeChart, includeHighlight, page)\"");
        // PERF: hljs гейтится includeHighlight (только страницы с кодом), как mermaid/chart.
        assertThat(head).contains("th:if=\"${includeHighlight}\"");
        // PERF: page-scoped CSS (0 утечки) едет только на своей странице; гейт переехал
        // из th:if шапки в манифест ленты, но остаётся обязательным.
        String manifest = readTemplate("css-bundle-manifest.txt");
        for (String page : List.of("bookmarks", "summary", "error", "login", "settings", "stats")) {
            assertThat(manifest)
                    .as("pages-%s.css должен быть гейтнут страницей %s", page, page)
                    .contains(page + " /css/pages-" + page + ".css");
        }
        assertThat(head).contains("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        assertThat(head).contains("<title th:text=\"${title}\">");
        // FOUC-critical оси — inline в head до первого кадра.
        assertThat(head).contains("setAttribute('data-design'");
        assertThat(head).contains("setAttribute('data-theme'");
        assertThat(head).contains("setAttribute('data-motion'");
        assertThat(head).contains("setAttribute('data-pixel'");
        // Остальные оси — personalization-bootstrap.js (module load).
        assertThat(bootstrapJs).contains("setAttribute('data-layout'");
        assertThat(bootstrapJs).contains("setAttribute('data-reading-width'");
        assertThat(bootstrapJs).contains("setAttribute('data-density'");
        // OPS-005: vendor-ассеты self-hosted, без CDN SRI. @font-face едет первой строкой ленты.
        assertThat(manifest).contains("* /css/fonts-local.css");
        // PERF: preload двух критичных Inter-сабсетов (короче swap-флэш, above-the-fold на каждой странице).
        // Cyrillic = русский блок U+0400-045F (файл ...SjIa0Z...), НЕ cyrillic-ext (...SjIa2J... = "unused preload").
        assertThat(head).contains("rel=\"preload\" as=\"font\" type=\"font/woff2\" crossorigin");
        assertThat(head).contains("th:href=\"@{/fonts/UcC73FwrK3iLTeHuS_nVMrMxCp50SjIa0ZL7W0Q5n-wU.woff2}\"");
        assertThat(head).contains("th:href=\"@{/fonts/UcC73FwrK3iLTeHuS_nVMrMxCp50SjIa1ZL7W0Q5nw.woff2}\"");
        assertThat(head).contains("th:src=\"@{/vendor/hljs/highlight.min.js}\"");
        // mermaid — единственный vendor без своего <script>: 2.7 МБ грузит
        // mermaid-renderer.js по видимости схемы, а путь (с content-hash в prod)
        // получает из этой meta. Тег скрипта здесь = возврат блокирующей загрузки.
        assertThat(head).contains("<meta name=\"mermaid-src\" th:content=\"@{/vendor/mermaid/mermaid.min.js}\">");
        assertThat(head).doesNotContain("th:src=\"@{/vendor/mermaid/mermaid.min.js}\"");
        assertThat(head).contains("th:src=\"@{/vendor/chart/chart.umd.min.js}\"");
        assertThat(head).doesNotContain("cdnjs.cloudflare.com");
        assertThat(head).doesNotContain("cdn.jsdelivr.net");
        assertThat(head).doesNotContain("fonts.googleapis.com");
        assertThat(head).doesNotContain("integrity=\"sha512-");
    }

    @Test
    void shellFragmentsExposeNavLandmarksAndActiveContract() throws IOException {
        // Редизайн (ui-redesign-handoff): masthead → каркас shell. Контракт:
        // sidebar — nav-landmark с меткой + aria-current на активном пункте;
        // topbar — прогресс-progressbar сессии + тоггл темы;
        // mobile-nav — nav-landmark нижней навигации.
        String sidebar = readTemplate("templates/fragments/sidebar.html");
        String topbar = readTemplate("templates/fragments/topbar.html");
        String mobileNav = readTemplate("templates/fragments/mobile-nav.html");

        assertThat(sidebar).contains("th:fragment=\"sidebar(active)\"");
        // Сам <aside> тоже обязан быть подписан, не только вложенный <nav>. На
        // экране фокуса рядом стоит второй complementary (focus-session-rail):
        // безымянный сайдбар давал бы SR два неразличимых «дополнения» подряд.
        assertThat(sidebar).contains("class=\"sidebar\" aria-label=\"Боковая панель\"");
        assertThat(sidebar).contains("<nav class=\"nav\" aria-label=\"Основная навигация\">");
        assertThat(sidebar).contains("aria-current=${active == 'focus'} ? 'page' : null");
        // Тоггл темы живёт ТОЛЬКО в topbar (desktop-панель + mobile-header). Раньше
        // он дублировался ещё и в футере сайдбара: два одинаковых переключателя на
        // одном экране → SR объявлял состояние темы дважды, и было непонятно, какой
        // «настоящий». Регресс-гвард: в сайдбаре его быть не должно.
        assertThat(sidebar).doesNotContain("data-theme-toggle");

        assertThat(topbar).contains("th:fragment=\"topbar\"");
        assertThat(topbar).contains("role=\"progressbar\"");
        // PE: тоггл раскрывается только при JS (класс hidden снимает ui/shell.js).
        assertThat(topbar).contains("data-theme-toggle");
        assertThat(topbar).contains("class=\"icon-button hidden\" data-theme-toggle");
        // aria-pressed на тоггле темы быть НЕ должно: светлая и тёмная — два
        // равноправных режима, а не вкл/выкл одного состояния. «Не нажата» на
        // кнопке в светлой теме SR читает как «тема выключена», что бессмысленно.
        // Актуальный режим объявляет aria-label, который shell.js держит в
        // согласии с темой.
        assertThat(topbar).doesNotContain("aria-pressed=");

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
                      "result-zone-head", "stats-grid-content",
                      "today-hero", "today-chip", "training-actions", "mermaid-init");

        for (String name : fragmentNames) {
            // Ссылка — либо параметризованная `:: name(`, либо bare `:: name}`.
            // Граница (`(`/`}`) не даёт короткому имени засчитаться через длинное
            // (`head` через `header`, `sprite` через `sprite-extra`).
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
        // <use href="#i-X"> в шаблоне, либо JS-хелпером icon('X')
        // (аргумент БЕЗ префикса i-, хелпер сам добавляет #i-, см. core/dom.js).
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
        // icon('X') (в нём имя без i-, добавляем префикс).
        Set<String> referenced = new LinkedHashSet<>();
        Matcher hrefMatcher = Pattern.compile("#(i-[a-z0-9-]+)").matcher(refs);
        while (hrefMatcher.find()) {
            referenced.add(hrefMatcher.group(1));
        }
        Matcher helperMatcher = Pattern.compile("icon\\([\"']([a-z0-9-]+)").matcher(refs);
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
    void staticEntryPointsUseThymeleafUrlsWithoutManualVersionQuery() throws IOException {
        // OPS-004: prod content-hash через @{...}; ручные ?v=N убраны с entry-point CSS/JS.
        String head = readTemplate("templates/fragments/head.html");
        String scripts = readTemplate("templates/fragments/scripts.html");

        // CSS едет одной лентой: content-hash несёт её собственное имя, а вложенные
        // url() переписывает CssBundleService через ResourceUrlProvider.
        assertThat(head).contains("@{__${@cssBundle.pathFor(page)}__}");
        assertThat(head).doesNotContain("rel=\"stylesheet\" th:href=\"@{/css/");
        assertThat(head).doesNotContain("(v=");
        assertThat(scripts).contains("th:src=\"@{/js/app-entry.js}\"");
        assertThat(scripts).doesNotContain("(v=");
    }

    /**
     * Манифест обязан покрывать ровно те CSS, что лежат в проекте.
     *
     * <p>Пока файлы перечисляла шапка, забытый файл был виден глазами при правке
     * разметки. Теперь список ушёл в манифест, и новый {@code components-*.css}
     * легко создать, подключить нигде и долго гадать, почему правила не
     * применяются. Обратная сторона — путь в манифесте без файла: лента тогда
     * падает на первом рендере.</p>
     *
     * <p>{@code base.css} — dev-агрегатор через {@code @import}, в ленту не едет
     * намеренно.</p>
     */
    @Test
    void cssBundleManifestCoversEveryProjectStylesheet() throws IOException {
        List<String> manifest = manifestPaths();
        for (String path : manifest) {
            assertThat(new ClassPathResource("static" + path).exists())
                    .as("манифест ссылается на несуществующий файл %s", path)
                    .isTrue();
        }

        Path cssDir = new ClassPathResource("static/css").getFile().toPath();
        List<String> onDisk;
        try (Stream<Path> walk = Files.walk(cssDir)) {
            onDisk = walk.filter(Files::isRegularFile)
                    .map(p -> "/css/" + cssDir.relativize(p).toString().replace('\\', '/'))
                    .filter(p -> p.endsWith(".css") && !p.equals("/css/base.css"))
                    .sorted()
                    .toList();
        }
        assertThat(manifest)
                .as("каждый CSS проекта должен быть в css-bundle-manifest.txt (иначе он не грузится ни на одной странице)")
                .containsAll(onDisk);
    }

    /**
     * Каждый динамический {@code import()} обязан быть строкой манифеста preload.
     *
     * <p>О модуле за статическим {@code import} браузер узнаёт, распарсив
     * импортёра, — цепочку {@code JsPreloadService} разворачивает сам. А модуль за
     * {@code import('./x.js')} не виден вообще ничему до момента исполнения: он
     * поедет отдельной волной ровно так, как ехали все шесть до preload. Ровно
     * поэтому такие модули перечисляются руками — и ровно поэтому забыть строку
     * здесь ничем себя не проявляет: страница работает, просто медленнее.</p>
     *
     * <p>Сверка двусторонняя. Лишняя строка манифеста так же вредна, как
     * пропущенная: preload неиспользованного модуля Chrome пишет предупреждением в
     * консоль и тратит канал на байты, которые странице не нужны.</p>
     */
    @Test
    void everyDynamicImportIsGatedInJsPreloadManifest() throws IOException {
        Path jsDir = new ClassPathResource("static/js").getFile().toPath();
        Pattern dynamicImport = Pattern.compile("\\bimport\\(\\s*['\"](\\.[^'\"]+)['\"]");

        Set<String> dynamic = new LinkedHashSet<>();
        try (Stream<Path> walk = Files.walk(jsDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".js")).sorted()::iterator) {
                String dir = "/js/" + jsDir.relativize(p).getParent();
                dir = dir.equals("/js/null") ? "/js/" : dir.replace('\\', '/') + "/";
                Matcher m = dynamicImport.matcher(new String(Files.readAllBytes(p), StandardCharsets.UTF_8));
                while (m.find()) {
                    dynamic.add(URI.create(dir + m.group(1)).normalize().getPath());
                }
            }
        }
        assertThat(dynamic).as("динамических import() в проекте не должно стать ноль незаметно").isNotEmpty();

        List<String> entries = new ArrayList<>();
        Set<String> gated = new LinkedHashSet<>();
        for (String raw : readTemplate(JsPreloadService.MANIFEST_PATH).split("\n")) {
            Matcher matcher = MANIFEST_ENTRY.matcher(raw.strip());
            if (!matcher.matches() || raw.strip().startsWith("#")) {
                continue;
            }
            entries.add(matcher.group(2));
            if (!"*".equals(matcher.group(1))) {
                gated.add(matcher.group(2));
            }
        }

        for (String path : entries) {
            assertThat(new ClassPathResource("static" + path).exists())
                    .as("манифест preload ссылается на несуществующий модуль %s", path)
                    .isTrue();
        }
        assertThat(gated)
                .as("набор постранично гейтнутых строк манифеста обязан совпадать с набором import() в /js")
                .containsExactlyInAnyOrderElementsOf(dynamic);
    }

    @Test
    void pixelAndMotionContractPresentInHeadAndCss() throws IOException {
        String head = readTemplate("templates/fragments/head.html");
        String css = readStructuralCss();

        assertThat(head).contains("setAttribute('data-pixel'");
        assertThat(head).contains("setAttribute('data-motion'");
        assertThat(head).contains("PIXEL_LEVELS = ['low', 'med', 'high']");
        // Каждый уровень, который объявляет head.html, обязан что-то менять в
        // каскаде — иначе переключатель в настройках врёт пользователю.
        // Проверяем по всему каскаду, а не по одному файлу: правила уровней
        // распределены по pixel-*.css и мигрируют между ними.
        assertThat(css).contains("[data-pixel=\"low\"]");
        assertThat(css).contains("[data-pixel=\"med\"]");
        assertThat(css).contains("[data-pixel=\"high\"]");
        assertThat(css).contains("[data-motion=\"off\"]");
    }

    @Test
    void appEntryJsIsSingleSourcedInFragment() throws IOException {
        // PERF-01: тег app-entry.js должен встречаться в дереве шаблонов
        // РОВНО один раз — в fragments/scripts.html; страницы подключают его через
        // th:replace="~{fragments/scripts :: app-script}".
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();
        Pattern includePattern = Pattern.compile("/js/app-entry\\.js");

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
            .as("app-entry.js должен подключаться из единственного файла (fragments/scripts.html), найдено: %s", filesWithInclude)
            .hasSize(1);
        assertThat(filesWithInclude.get(0).toString().replace('\\', '/'))
            .isEqualTo("fragments/scripts.html");
    }

    /**
     * Модуль обязан грузиться на той странице, чью разметку он оживляет.
     *
     * <p>{@code app-entry.js} делит код на кластеры по классу {@code body}: тяжёлые
     * модули не едут на страницы, где им нечего делать. Цена деления — новая
     * возможность ошибиться: инициализатор можно приписать к чужому кластеру, и
     * тогда он не выполнится нигде. Компилятор молчит (модуль импортирован и
     * вызван), браузер молчит (исключения нет), UI молчит (остаётся корректный
     * no-JS-вид). Единственный способ это заметить — сверить, где разметка, с тем,
     * где код.
     *
     * <p>Проверка идёт от разметки: якорь ищется в шаблонах страниц, класс
     * {@code body} читается оттуда же, и уже он сверяется с кластером в
     * {@code app-entry.js}. Поэтому переезд блока на другую страницу тест ловит
     * так же, как и переезд модуля в другой кластер.
     */
    @Test
    void progressiveEnhancementModulesLoadOnThePageThatRendersTheirMarkup() throws IOException {
        Map<String, Set<String>> clusters = readPageClusters();
        Map<String, String> pageClassByTemplate = readPageClasses();

        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, String> entry : PROGRESSIVE_ENHANCEMENT_ANCHORS.entrySet()) {
            String anchor = entry.getKey();
            String module = entry.getValue();
            String name = anchor.substring(1);

            List<String> pages = new ArrayList<>();
            for (Map.Entry<String, String> template : pageClassByTemplate.entrySet()) {
                if (readTemplate("templates/" + template.getKey()).contains(name)) {
                    pages.add(template.getValue());
                }
            }

            if (pages.isEmpty()) {
                problems.add(anchor + ": разметки нет ни в одном шаблоне страницы — "
                        + "модуль " + module + " активировать нечего");
                continue;
            }
            for (String pageClass : pages) {
                Set<String> modules = clusters.getOrDefault(pageClass, Set.of());
                if (!modules.contains(module)) {
                    problems.add(anchor + " рендерится на ." + pageClass + ", но кластер этой "
                            + "страницы грузит " + modules + " — без " + module);
                }
            }
        }

        assertThat(problems)
                .as("Прогрессивное улучшение отваливается молча: пользователь остаётся с "
                        + "рабочим no-JS-видом и не понимает, что фича сломана. Нарушения: %s", problems)
                .isEmpty();
    }

    /** Класс {@code body} → модули, которые {@code app-entry.js} грузит для этой страницы. */
    private Map<String, Set<String>> readPageClusters() throws IOException {
        String entry = Files.readString(
                new ClassPathResource("static/js/app-entry.js").getFile().toPath(), StandardCharsets.UTF_8);

        Map<String, Set<String>> modulesByLoader = new LinkedHashMap<>();
        Matcher loader = Pattern.compile("async function (\\w+)\\(\\)\\s*\\{(.*?)\\n}", Pattern.DOTALL)
                .matcher(entry);
        while (loader.find()) {
            Set<String> modules = new LinkedHashSet<>();
            Matcher imported = Pattern.compile("import\\('(\\./[\\w./-]+)'\\)").matcher(loader.group(2));
            while (imported.find()) {
                modules.add(imported.group(1));
            }
            modulesByLoader.put(loader.group(1), modules);
        }

        Map<String, Set<String>> clusters = new LinkedHashMap<>();
        Matcher wiring = Pattern.compile("pageIs\\('([\\w-]+)'\\)\\)\\s*\\{\\s*tasks\\.push\\((\\w+)\\(\\)\\)")
                .matcher(entry);
        while (wiring.find()) {
            clusters.put(wiring.group(1), modulesByLoader.getOrDefault(wiring.group(2), Set.of()));
        }

        assertThat(clusters).as("кластеры страниц в app-entry.js").isNotEmpty();
        return clusters;
    }

    /** Шаблон страницы → класс её {@code body}. Фрагменты сюда не попадают: у них нет body. */
    private Map<String, String> readPageClasses() throws IOException {
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();
        Pattern bodyClass = Pattern.compile("<body[^>]*class=\"([^\"]*?)\"");

        Map<String, String> pages = new LinkedHashMap<>();
        try (Stream<Path> walk = Files.walk(templatesDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".html")).sorted()::iterator) {
                Matcher m = bodyClass.matcher(Files.readString(p, StandardCharsets.UTF_8));
                if (!m.find()) {
                    continue;
                }
                for (String token : m.group(1).split("\\s+")) {
                    if (token.endsWith("-page")) {
                        pages.put(templatesDir.relativize(p).toString().replace('\\', '/'), token);
                    }
                }
            }
        }

        assertThat(pages).as("шаблоны страниц с классом *-page").isNotEmpty();
        return pages;
    }

    /**
     * Числа, которые читает машина, не должны зависеть от локали.
     *
     * <p>Приложение русское, и {@code #numbers.formatDecimal(x, 1, 1)} честно печатает
     * «34,8» — это правильно для {@code th:text}. Но тот же вызов стоял в атрибутах,
     * которые разбирает не человек. Для {@code data-progress} запятую молча гасил
     * {@code .replace(',', '.')} в JS-гидраторе; у {@code stroke-dasharray} SSR-дуги
     * на итогах сессии гасить было некому — а в SVG запятая такой же разделитель
     * списка, как пробел, поэтому «326,73 400» читалось как ТРИ значения, нечётный
     * список удваивался, и сплошная дуга становилась пунктиром. Отказ был бесшумным
     * и невидимым на нулевой точности.
     *
     * <p>Гвард требует явного {@code 'POINT'} в таких атрибутах. Сырое число
     * ({@code data-progress=${item.percent}}) под проверку не попадает намеренно:
     * {@code Double.toString} от локали не зависит.
     */
    @Test
    void machineReadableNumericAttributesUseExplicitPointSeparator() throws IOException {
        Path templatesDir = new ClassPathResource("templates").getFile().toPath();
        // Атрибут + formatDecimal до закрывающей `}`: ловим ровно форматированное
        // значение, а не соседние выражения в том же теге.
        Pattern formatted = Pattern.compile(
            "(data-progress|stroke-dasharray)=\\$\\{[^}]*#numbers\\.formatDecimal\\([^}]*\\}");

        List<String> localeDependent = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(templatesDir)) {
            for (Path p : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".html")).sorted()::iterator) {
                String html = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                Matcher m = formatted.matcher(html);
                while (m.find()) {
                    if (!m.group().contains("'POINT'")) {
                        localeDependent.add(templatesDir.relativize(p) + ": " + m.group());
                    }
                }
            }
        }

        assertThat(localeDependent)
            .as("data-progress / stroke-dasharray обязаны печатать разделитель дроби явно "
                + "(#numbers.formatDecimal(x, 1, 'NONE', N, 'POINT')), иначе русская локаль "
                + "подставит запятую и значение прочитается неверно. Нарушения: %s", localeDependent)
            .isEmpty();
    }
}
