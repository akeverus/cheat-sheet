package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.config.web.JsPreloadService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
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
import java.util.stream.Collectors;
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
     * Комментарии разметки, включая парсерную форму thymeleaf. Та же
     * ловушка, что у {@link #CSS_COMMENT}, и она сработала на iter-74: гейт
     * «в строках списка нет акцентной кнопки» падал на комментарии, который
     * объяснял, почему её там нет.
     */
    private static final Pattern TEMPLATE_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

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
        String noticeSlot = readTemplate("templates/fragments/notice.html");

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

        // Слот сообщения. `data-notice-host` — контракт поиска хоста из ui/alerts.js:
        // страничный слот (`scope='page'`) выигрывает у общего (`scope='shell'`).
        // До iter-82 скрипт искал `#interview-alert`, который печатали ровно две
        // страницы, и сообщения об ошибке с /bookmarks не доезжали никуда.
        assertThat(noticeSlot).contains("data-ui-fragment=\"notice-slot\"");
        assertThat(noticeSlot).contains("data-notice-host=${scope}");
        // Дефолт разметки — ошибка (assertive); успех переключает role/aria-live
        // в скрипте, тон известен только в момент вызова.
        assertThat(noticeSlot).contains("role=\"alert\"");
        assertThat(noticeSlot).contains("aria-live=\"assertive\"");
        // Слот приезжает скрытым и классом, и атрибутом: без JS он не должен
        // занимать место даже до загрузки скинов.
        assertThat(noticeSlot).contains("class=\"notice hidden\"");
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

        assertThat(focusTraining).contains("<body class=\"focus-page\"");
        assertThat(stats).contains("<body class=\"stats-page\">");
        assertThat(settings).contains("<body class=\"settings-page\">");
        assertThat(result).contains("<body class=\"result-page\">");
        assertThat(stats).contains("filter-bar-form");

        assertThat(focusTraining).contains("fragments/training-actions :: training-actions");
        // Редизайн (ui-redesign-handoff): masthead заменён каркасом shell —
        // sidebar (рельс) + shell-head + bottom-nav. Все страницы подключают их.
        assertThat(focusTraining).contains("fragments/sidebar :: sidebar");
        assertThat(focusTraining).contains("fragments/shell-head :: shell-head");
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
        assertThat(result).contains("fragments/notice :: notice-slot");
    }

    @Test
    void settingsTemplateUsesTabStructure() throws IOException {
        String settings = readTemplate("templates/settings.html");
        // Редизайн: 4-карточная стопка заменена вкладками (Сессия | Оформление |
        // Данные). Один раздел за раз вместо ~3.6 экрана скролла. PE-гейт полосы
        // с iter-80 живёт в CSS (`:not([data-js]) [role="tablist"]`), а не в
        // разметке: утилита `.hidden` с полосы снята — см. TabPanelContractTest.
        assertThat(settings).contains("id=\"main-content\"");
        assertThat(settings).contains("id=\"settings-tablist\"");
        assertThat(settings).contains("role=\"tablist\"");
        assertThat(settings).contains("class=\"settings-tablist\"");
        assertThat(settings).contains("type=\"button\"");
        // Три панели-tabpanel.
        assertThat(settings).contains("id=\"panel-session\"");
        assertThat(settings).contains("id=\"panel-appearance\"");
        assertThat(settings).contains("id=\"panel-data\"");
        assertThat(settings).contains("role=\"tabpanel\"");
        // Функциональные ID форм/контролов сохранены (relayout, не смена поведения).
        assertThat(settings).contains("id=\"filters-form\"");
        assertThat(settings).contains("id=\"session-form\"");
        // Режим — группа радиокнопок (карточки), а не <select>: сторожим носитель
        // выбора, а не тег. Контракт прежний — поле формы называется mode и уходит
        // на сервер без JS.
        assertThat(settings).contains("id=\"session-mode-choice\"");
        assertThat(settings).contains("type=\"radio\" name=\"mode\"");
        assertThat(settings).doesNotContain("id=\"session-mode-select\"");
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

    /**
     * FRD-025 / iter-73: на панели «Темы» карточка «Отбор» обязана стоять ПЕРЕД
     * каталогом разделов.
     *
     * <p>С iter-73 отбор правит всю панель: таблицу, оба графика, пробелы, разбор
     * ошибок и карточку слабой темы. Каталог — единственный блок, который
     * намеренно остался по полному банку. Пока каталог стоял первым, страница
     * при активном отборе сначала показывала 876px цифр по всему банку и лишь
     * потом сообщала, что показанное ниже — не весь банк (замер WebKit 1512×982:
     * строка «N тем в выборке» на 1985px при сгибе 982).</p>
     *
     * <p>Гейт держит именно порядок в РАЗМЕТКЕ, а не в CSS: перестановка через
     * {@code order} развела бы видимый порядок с клавиатурным обходом
     * (WCAG 2.4.3). С iter-95 гейт вдобавок стережёт, чтобы каталог разделов не
     * вернулся на /stats: он отвечал на вопрос /topics, а не аналитики.</p>
     */
    @Test
    void statsSelectionCardPrecedesFullBankCatalog() throws IOException {
        String stats = readTemplate("templates/stats.html");

        int panel = stats.indexOf("id=\"stats-panel-topics\"");
        int selection = stats.indexOf("class=\"card filter-bar\"");

        assertThat(panel).isNotNegative();
        assertThat(selection).isGreaterThan(panel);

        // Полоса отбора не переставлена обратно средствами CSS.
        assertThat(readStructuralCss()).doesNotContain(".filter-bar { order");

        // Каталог разделов со /stats СНЯТ: он отвечал на вопрос страницы /topics
        // («что есть и куда пойти»), а не аналитики («как я иду»), и занимал
        // 2349px из 9377px мобильной страницы. Гейт стережёт, чтобы он не вернулся
        // — ни разметкой, ни правилом порядка в CSS.
        assertThat(stats).doesNotContain("class=\"card topic-catalog\"");
        assertThat(stats).doesNotContain("Счётчики разделов — по всему банку");
        assertThat(readStructuralCss()).doesNotContain(".topic-catalog-grid {");
    }

    /**
     * iter-74: у /bookmarks есть отбор, и он подчиняется тем же правилам, что
     * полоса /stats.
     *
     * <p>До iter-74 коллекция разворачивалась одним списком: замер WebKit на 28
     * закладках дал 4774px (6.6 экрана) на 1280×720 и 11324px (14.5) на 360×780,
     * а найти нужную можно было только прокруткой. Ширина тут не помогает —
     * удвоение с 1280 до 2560 срезало лишь 31 % высоты, ограничение в объёме.</p>
     *
     * <p>Гейт держит пять вещей, каждая из которых уже ломалась. Заголовок,
     * отбор и список стоят ОДНОЙ карточкой: двумя (заголовок отдельно, «Отбор»
     * отдельно) первая закладка на 1280×720 оказывалась на 481-м пикселе из
     * 720, а на 390×844 — на 870-м, то есть в первый экран не попадало ни
     * одной (§8). Строка состояния СТОИТ НАД формой (iter-73: состояние читают
     * до контролов, которыми его меняют). Полоса — общий компонент, а не вторая
     * копия правил под своим именем (§24). Отбор — одна форма на один эндпоинт
     * (FRD-024). Счётчик выборки назван вместе со знаменателем, иначе «1» не
     * отличает «нашлась одна» от «всего одна».</p>
     */
    @Test
    void bookmarksSelectionBandStatesSelectionAboveItsControls() throws IOException {
        String bookmarks = readTemplate("templates/bookmarks.html");

        int head = bookmarks.indexOf("class=\"bookmarks-list-head\"");
        int band = bookmarks.indexOf("class=\"filter-bar\"");
        int state = bookmarks.indexOf("class=\"filter-bar-state\"");
        int form = bookmarks.indexOf("class=\"filter-bar-form\"");
        int list = bookmarks.indexOf("class=\"bookmark-list\"");

        assertThat(head).isNotNegative();
        assertThat(band).isGreaterThan(head);
        assertThat(state).isGreaterThan(band);
        assertThat(form).isGreaterThan(state);
        assertThat(list).isGreaterThan(form);
        // Одна карточка на всё: между заголовком и списком карточка не
        // закрывается. Именно этот шов и стоил экрана — заголовок, «Отбор» и
        // список тремя секциями подряд.
        assertThat(bookmarks.substring(head, list)).doesNotContain("</section>");
        assertThat(bookmarks).doesNotContain("class=\"card filter-bar\"");

        // Полоса — общий компонент с /stats, а не вторая копия правил под своим
        // именем (§24). Гейт на КЛАСС, а не на подстроку: собственных имён вида
        // `bookmarks-filter-*` у страницы быть не должно, а id-шники в неё не
        // засчитываются — они копий правил не создают.
        assertThat(bookmarks).doesNotContain("class=\"bookmarks-filter");
        assertThat(readStructuralCss()).doesNotContain(".bookmarks-filter");
        // Одна форма на один GET: скрытых копий чужих полей нет — все четыре
        // условия отбора живут в ней самой.
        assertThat(bookmarks).containsOnlyOnce("<form method=\"get\"");
        assertThat(bookmarks).contains("name=\"q\"")
                .contains("name=\"group\"")
                .contains("name=\"note\"")
                .contains("name=\"sort\"");
        assertThat(bookmarks).contains("${bookmarksShown} + ' из ' + ${bookmarksTotal}");
        // Полоса не рендерится над пустой коллекцией: отбирать там нечего.
        assertThat(bookmarks).contains("th:if=\"${bookmarksTotal != null and bookmarksTotal > 0}\"");
    }

    /**
     * §27: «закладок ещё нет» и «под отбор ничего не подошло» — РАЗНЫЕ состояния
     * с противоположным советом.
     *
     * <p>Одна карточка на оба случая предлагала бы «Начать тренировку» человеку,
     * у которого 28 закладок, и молчала бы о том, что их скрыл его же запрос.
     * Гейт держит и взаимоисключающие условия (два пустых экрана разом — это
     * противоречие на странице), и разные изображения: одинаковая картинка
     * читается как одно состояние независимо от текста.</p>
     *
     * <p>Пустая выборка живёт ВНУТРИ карточки отбора, сразу под формой, которая
     * её и дала. Отдельной секцией она уезжала под полосу вторым блоком, и на
     * узком экране «ничего не нашлось» оказывалось за пределами первого экрана:
     * человек видел форму и пустоту, не понимая, что случилось.</p>
     */
    @Test
    void bookmarksSeparatesEmptyCollectionFromEmptySelection() throws IOException {
        String bookmarks = readTemplate("templates/bookmarks.html");

        assertThat(bookmarks).contains("th:if=\"${bookmarksTotal == null or bookmarksTotal == 0}\"");
        int form = bookmarks.indexOf("class=\"filter-bar-form\"");
        int nothing = bookmarks.indexOf("th:if=\"${bookmarks == null or bookmarks.isEmpty()}\"");
        assertThat(nothing).isGreaterThan(form);
        assertThat(bookmarks.substring(form, nothing)).doesNotContain("</section>");
        assertThat(bookmarks).contains("Пока нет закладок").contains("Под отбор ничего не подошло");
        // Глифы разные и оба — из спрайта состояний (iter-83). До него здесь
        // стояли предметные иконки Lucide `#bookmark` и `#search`: они называли
        // СТРАНИЦУ, а не положение человека, и жили в другом языке, чем глифы
        // тех же состояний на /stats и на экране вопроса.
        assertThat(bookmarks).contains("<use href=\"#px-chest\">").contains("<use href=\"#px-spyglass\">");
        // Из отобранной пустоты есть выход, и он не «иди тренируйся».
        assertThat(bookmarks).contains("Показать все закладки");
        // Запрос повторяется дословно и ломается по месту: 160 символов без
        // пробела иначе дают странице горизонтальный скролл (замер iter-72).
        assertThat(bookmarks).contains("class=\"query-echo\"");
    }

    /**
     * iter-74: на /bookmarks ровно одна акцентная заливка в рабочей части —
     * у главного действия, а не на каждой строке списка.
     *
     * <p>Раньше «Открыть» в каждой карточке было {@code next-btn}: при 28
     * закладках страница показывала 28 одинаковых бирюзовых плашек, и
     * собственный акцент страницы тонул среди них. Отдельной карточки действий
     * над списком тоже больше нет — она тратила 124px на 1280 и 186px на 360
     * ради двух ссылок, вторая из которых дублировала пункт бокового рельса.</p>
     *
     * <p>Подписи о действующем порядке в заголовке тоже нет: селект «Порядок»
     * стоит в той же карточке шестью десятками пикселей ниже и говорит ровно
     * то же. Две формулировки одного факта расходятся при первой же правке, а
     * на телефоне подпись стоила пятидесяти пикселей первого экрана.</p>
     */
    @Test
    void bookmarksKeepsSinglePrimaryActionWithoutRestatingTheOrder() throws IOException {
        String bookmarks = readTemplate("templates/bookmarks.html");

        assertThat(bookmarks).doesNotContain("bookmarks-toolbar");
        assertThat(bookmarks).contains("class=\"bookmarks-list-head\"");
        assertThat(bookmarks).contains("class=\"btn next-btn primary-btn\"");

        // Строки списка — вторичные кнопки; акцент в них не возвращается.
        // Комментарии вырезаем: объяснение «secondary, а не next-btn» само
        // содержит запрещённое имя и воскрешало бы правило для теста — ровно
        // та ловушка, что описана у CSS_COMMENT.
        int listStart = bookmarks.indexOf("class=\"bookmark-list\"");
        int listEnd = bookmarks.indexOf("</ul>", listStart);
        assertThat(listStart).isNotNegative();
        String rows = TEMPLATE_COMMENT.matcher(bookmarks.substring(listStart, listEnd)).replaceAll("\n");
        assertThat(rows).doesNotContain("next-btn");

        // Порядок назван ровно один раз — селектом. Подпись в заголовке была бы
        // вторым источником правды о том же. Комментарии снова вырезаем:
        // объяснение, почему подписи нет, само её цитирует.
        assertThat(TEMPLATE_COMMENT.matcher(bookmarks).replaceAll("\n")).doesNotContain("Новые сверху.");
        assertThat(readStructuralCss()).doesNotContain(".bookmarks-toolbar");
    }

    /**
     * iter-74: кнопка полосы отбора — тихая на ОБЕИХ страницах, где эта полоса
     * едет.
     *
     * <p>{@code .next-btn} заливает кнопку акцентом ровно так же, как
     * {@code .primary-btn} (components-controls.css: {@code background:
     * var(--color-accent-primary)}). После слияния карточек на /bookmarks
     * «Показать» встало в сорока пикселях под «Тренировать избранные», и в одной
     * карточке оказались два одинаково залитых прямоугольника — замер WebKit@2
     * на 1512×982 давал две кнопки с фоном {@code rgb(57,208,199)}. По такой паре
     * нельзя прочитать, где главное действие страницы.</p>
     *
     * <p>Отбор — средство, а не цель: он перерисовывает тот же список, ничего не
     * меняя в данных. Поэтому кнопка понижена и на /stats тоже: полоса одна на
     * две страницы, и вес её кнопки не должен зависеть от того, куда её
     * поставили. Тест целится в блок действий полосы, а не в глобальный
     * {@code .btn}, — как и правила самого компонента.</p>
     */
    @Test
    void filterBarSubmitStaysQuietOnBothPagesThatCarryTheBar() throws IOException {
        for (String page : new String[] {"templates/bookmarks.html", "templates/stats.html"}) {
            String html = TEMPLATE_COMMENT.matcher(readTemplate(page)).replaceAll("\n");
            int actions = html.indexOf("class=\"filter-bar-actions\"");
            int formEnd = html.indexOf("</form>", actions);
            assertThat(actions).as("%s: блок действий полосы отбора", page).isNotNegative();

            String block = html.substring(actions, formEnd);
            assertThat(block).as("%s: отправка отбора не носит акцентную заливку", page)
                    .doesNotContain("next-btn")
                    .doesNotContain("primary-btn");
            assertThat(block).as("%s: отправка отбора остаётся кнопкой", page)
                    .contains("type=\"submit\"")
                    .contains("secondary-btn");
        }
    }

    /**
     * Правило «одна акцентная заливка на экран» обязано держаться во ВСЕХ состояниях,
     * иначе это не правило, а описание одного скриншота.
     *
     * <p>В состоянии «под отбор ничего не подошло» секция списка продолжает
     * рендериться (коллекция не пуста — её скрыл отбор), а вместе с ней остаётся
     * «Тренировать избранные». Замер WebKit@2 на 1512×982 в этом состоянии давал
     * ДВЕ залитых кнопки из трёх, что на экране есть. Выход из отбора ведёт по тому
     * же адресу, что и ссылка «Сбросить» в строке состояния выше, поэтому акцент ему
     * не принадлежит — кнопка тихая.</p>
     */
    @Test
    void filteredEmptyStateDoesNotAddSecondAccentBesideTheListAction() throws IOException {
        String html = TEMPLATE_COMMENT.matcher(readTemplate("templates/bookmarks.html")).replaceAll("\n");
        int title = html.indexOf("bookmarks-nothing-title");
        assertThat(title).as("состояние «под отбор ничего не подошло»").isNotNegative();

        String block = html.substring(title, html.indexOf("</section>", title));
        assertThat(block).as("выход из отбора не носит акцентную заливку")
                .doesNotContain("next-btn")
                .doesNotContain("primary-btn");
        assertThat(block).as("выход из отбора остаётся видимой кнопкой")
                .contains("secondary-btn");
    }

    /**
     * Объём и порядок таблицы тем решает СЕРВЕР, а не скрипт за таблицей (iter-81,
     * FRD-048).
     *
     * <p>До правки и то и другое считал инлайновый {@code <script>} после
     * {@code </table>}. В WebKit он стабильно не успевал до первого кадра: замер
     * дал FCP 238–255 мс с отрисованными всеми 319 строками (документ 21957px) и
     * схлопывание до 3939px уже ПОСЛЕ отрисовки — тот же класс дефекта, что
     * iter-80 закрыл для вкладок. Chrome гонку выигрывал (12 строк на FCP,
     * CLS 0), то есть механизм работал только в одном движке — прямой случай
     * §11.</p>
     *
     * <p>Гейт держит три условия, каждое из которых по отдельности возвращает
     * дефект: строки приходят из {@code topicTable.rows()} (а не из полного
     * {@code topicStats}), {@code aria-sort} рендерит сервер (иначе первый кадр
     * объявляет скринридеру «не отсортирована», будучи отсортированной), а сама
     * сортировка — ССЫЛКА с адресом, а не кнопка со скриптом. Отдельно
     * фиксируется отсутствие обоих снятых механизмов: {@code table-sort.js} и
     * инлайнового свёртывания по {@code tr[hidden]} — по §24 две конкурирующие
     * системы не оставляем.</p>
     */
    @Test
    void statsTopicTableTakesVolumeAndOrderFromServer() throws IOException {
        String stats = readTemplate("templates/stats.html");
        String topicChartsJs = readTemplate("static/js/stats/topic-charts.js");

        assertThat(stats).as("строки таблицы — серверное окно, а не весь topicStats")
                .contains("th:each=\"item : ${topicTable.rows()}\"");
        assertThat(stats).as("состояние сортировки в первом кадре")
                .contains("aria-sort=${col.ariaSort}");
        assertThat(stats).as("сортировка — адрес страницы, работает без JS")
                .contains("class=\"col-sort\"")
                .contains("th:href=\"${col.href}\"");
        assertThat(stats).as("объём выборки назван явно")
                .contains("topicTable.totalRows()")
                .contains("topicTable.expandHref()")
                .contains("topicTable.collapseHref()");

        // Адрес заголовка собирает Java, а не выражение ссылки в шаблоне: @{/stats(...)}
        // печатает и ПУСТЫЕ параметры, и ссылка каждой колонки тянула их шесть штук
        // (?topic=&group=&important=&onlyWrong=&ordered=true&q=…), пока соседние чипы
        // групп на той же странице показывали чистое ?group=foundation. Одно место
        // сборки — TopicTableView.href + keepFilterQuery.
        assertThat(stats).as("ссылки колонок не собираются выражениями в разметке")
                .doesNotContain("@{/stats(sort=");

        assertThat(stats).as("инлайновое свёртывание снято целиком")
                .doesNotContain("data-collapse-rows")
                .doesNotContain("table-sort-status")
                .doesNotContain("row.hidden = collapsed");
        assertThat(new ClassPathResource("static/js/stats/table-sort.js").exists())
                .as("клиентская сортировка удалена, а не оставлена рядом").isFalse();
        assertThat(readTemplate("static/js/stats/index.js")).doesNotContain("table-sort");
        assertThat(readStructuralCss()).as("правила для скрытых строк больше некому применять")
                .doesNotContain(".stats-page .topic-table tr[hidden]");

        assertThat(stats).contains("id=\"topicProgressChartFallback\"");
        assertThat(stats).contains("id=\"topicAccuracyChartFallback\"");
        assertThat(stats).contains("class=\"chart-fallback hidden\"");
        assertThat(stats).contains("role=\"status\"");
        assertThat(stats).contains("aria-live=\"polite\"");
        assertThat(stats).contains("class=\"forecast-count\" aria-hidden=\"true\"");
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
        // высота → длина подписи. Саму коробку мерит chartBox (общий с осью дат,
        // контракт замера — в canvasChartsDeriveTickBudgetFromTheBoxTheyAreBuiltWith).
        assertThat(js).contains("const box = chartBox(canvas);");
        assertThat(js).contains("box.width - Y_AXIS_RESERVE_PX) / MIN_BAR_SLOT_PX");
        assertThat(js).contains("box.height * X_AXIS_MAX_SHARE");

        // Промах виден: обрезка многоточием, а не тихий клип канваса.
        assertThat(js).contains("const ELLIPSIS = '…';");
        assertThat(js).contains("shortenToWidth(width, labels[index]");

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

    /**
     * Всё, что рисуется в канвасе, обязано считать бюджет подписей от коробки — и
     * от ТОЙ коробки, с которой график построен.
     *
     * <p>Гейт выше держит ось тем. Этот — общее правило, потому что «молча удалить
     * непомещающееся» у Chart.js не свойство одной оси: любой {@code ticks} без
     * записанного бюджета отдан на откуп {@code autoSkip}. Ось дат «Динамики
     * точности» это подтвердила константой {@code maxTicksLimit: 7}, не связанной
     * ни с шириной графика, ни с шириной подписи: на 1440 она выбрасывала 7 дат из
     * 14, имея 981px, а на 390 держала те же 7 с зазором 5.4px до столкновения.</p>
     *
     * <p>Три условия, каждое из которых по отдельности возвращает молчаливую
     * потерю:</p>
     * <ol>
     *   <li>мерим ОБЁРТКУ, а не канвас: канвас без атрибутов {@code width}/
     *       {@code height} отдаёт интринсик 300×150 — правдоподобное число, тихо
     *       портящее любую формулу от размера;</li>
     *   <li>коробки нет (панель вкладки скрыта, 0×0) → бюджет снимается, а не
     *       считается нулевым: посчитанный по такой коробке предел застрял бы
     *       навсегда;</li>
     *   <li>перерисовка сверяет ДВА БЮДЖЕТА — {@code current()} против
     *       {@code applied()}, — а не бюджет со снимком «как было при подписке».
     *       Снимок врёт: Chart.js в конструкторе сам проставляет канвасу размеры,
     *       и коробка от этого меняется. Замер поймал ровно это: ось дат строилась
     *       по коробке 585px (11 подписей из 14), а «эталон» записывался с
     *       устоявшихся 1027px (14) — расхождение с первой же секунды считалось
     *       нормой, и ни поворот экрана, ни переключение вкладки его не чинили.</li>
     * </ol>
     */
    @Test
    void canvasChartsDeriveTickBudgetFromTheBoxTheyAreBuiltWith() throws IOException {
        String theme = readTemplate("static/js/stats/chart-theme.js");
        String trend = readTemplate("static/js/stats/accuracy-trend.js");
        String topics = readTemplate("static/js/stats/topic-charts.js");

        // (1) Замер — по обёртке, и нулевая коробка отдаётся как «размер неизвестен».
        assertThat(theme).contains("(canvas?.parentElement ?? canvas)?.getBoundingClientRect()");
        assertThat(theme).contains("box.width > 0 && box.height > 0 ? box : null");

        // (2) Предел оси дат считается от коробки. Возврат к константе снова
        // оторвал бы число подписей и от ширины графика, и от ширины подписи.
        assertThat(trend).contains("maxTicksLimit: tickBudget(canvas, labels)");
        // Без комментариев: докблок цитирует прежнюю константу, и цитата не должна
        // ни ронять гейт, ни быть поводом выкинуть саму историю дефекта.
        assertThat(CSS_COMMENT.matcher(trend).replaceAll("\n"))
            .as("предел оси дат — из коробки, а не числовая константа")
            .doesNotContainPattern("maxTicksLimit:\\s*\\d");
        assertThat(trend).contains("const box = chartBox(canvas);");
        assertThat(trend).contains("box.width - Y_AXIS_RESERVE_PX");
        // Ширину подписи меряем фактическую (dayLabel при неожиданном формате даты
        // отдаёт строку как есть), а не образец «31.07».
        assertThat(trend).contains("labels.reduce((max, label) => Math.max(max, width(label)), 0)");
        // Коробки нет → ограничение снимается целиком.
        assertThat(trend).contains("if (!box) return undefined;");

        // (3) Контракт пересчёта: сравниваются два бюджета, а не снимок.
        assertThat(theme).contains("if (current() !== applied()) redraw();");
        assertThat(theme).doesNotContain("let last = key()");
        // applied снимается ДО постройки — иначе он опишет уже другую коробку.
        assertThat(theme).contains("applied = resizeKey ? resizeKey() : null;");
        assertThat(theme.indexOf("applied = resizeKey"))
            .as("applied снимается до destroy+build, иначе описывает уже другую коробку")
            .isLessThan(theme.indexOf("chart = build(chartPalette()"));
        assertThat(topics).contains("appliedKey = `${progressBudget.bars}");
        assertThat(topics.indexOf("appliedKey = `${progressBudget.bars}"))
            .as("appliedKey снимается до new Chart, иначе описывает уже другую коробку")
            .isLessThan(topics.indexOf("new Chart(progressCanvas"));
        assertThat(topics).contains("applied: () => appliedKey");

        // Слушаем КОРОБКУ: window.resize не покрывает ни раскрытие скрытой панели
        // (0×0 → настоящий размер), ни догрузку шрифта, ни устаканивание layout.
        assertThat(theme).contains("new ResizeObserver(schedule)");
        assertThat(theme).contains("boxes.filter(Boolean).forEach((el) => observer.observe(el));");
        assertThat(trend).contains("[canvas.parentElement]");
        assertThat(topics).contains("boxes: [progressCanvas?.parentElement, accuracyCanvas?.parentElement]");
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
        // tokens.css — токен-слой. SINGLE-SOURCE + DARK-DEFAULT: значения живут ПРЯМО
        // в :root / :root[data-theme="light"], без html[data-design] override-слоя.
        // Дефолт — тёмная тема (решение пользователя), светлая — явный тумблер.
        // Контракт:
        // (1) :root несёт тёмную ветку арт-направления «мастерская гильдии»
        //     (тёплый камень #14120f + латунь #d0a33c),
        // (2) :root[data-theme="light"] несёт пергаментную светлую (#ece4d3),
        // (3) НИ ОДНОГО html[data-design="X"] scoped-цветоблока,
        // (4) снятые палитры не возвращаются ЗНАЧЕНИЯМИ токенов: ни warm ivory/clay
        //     editorial (#FAF9F5 / #D97757), ни терминальная бирюза (#071116 / #39d0c7).
        //     Последняя добавлена в список именно потому, что действовала до этой
        //     итерации: гейт, который сторожит только позапрошлое направление,
        //     молча пропускает откат к прошлому.
        String css = readTemplate("static/css/tokens.css");

        // База: :root с тёмной веткой направления (тёплый камень + латунь).
        assertThat(css).contains(":root");
        assertThat(css).contains("--color-bg-primary");
        assertThat(css).contains("--color-accent-primary");
        assertThat(css)
            .as(":root несёт тёмный canvas направления (тёплый камень #14120f)")
            .contains("--color-bg-primary:   #14120f");
        assertThat(css)
            .as(":root[data-theme=\"light\"] несёт вторичную светлую тему")
            .contains(":root[data-theme=\"light\"]");
        assertThat(css)
            .as("светлая канва — пергамент, а не офисный белый")
            .contains("--color-bg-primary:   #ece4d3");
        assertThat(css)
            .as("латунь — единственный интерактивный акцент")
            .contains("--color-accent-primary:        #d0a33c");

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
        // Снятая терминальная палитра: сторожим ЗНАЧЕНИЯ, а не упоминания —
        // шапка файла объясняет, что и почему заменено, и обязана иметь право
        // назвать старые цвета прозой.
        assertThat(css)
            .as("бирюзовый акцент снятого направления не должен быть значением токена")
            .doesNotContain(": #39d0c7").doesNotContain(": #58d4d0").doesNotContain(": #7ce6df");
        assertThat(css)
            .as("сине-зелёная канва снятого направления не должна быть значением токена")
            .doesNotContain(":   #071116").doesNotContain(":   #f5f8f8");
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
        // SUM-9 (пересмотрен iter-63): h1 остаётся единственным якорем H-навигации, но
        // больше не visually-hidden. Скрытый заголовок делал итоги единственным из шести
        // экранов без видимого названия — зрячий пользователь не получал того контекста,
        // который получал SR. Заголовок переехал в канонический .page-head (FRD-011),
        // режим сессии стал надзаголовком .eyebrow вместо чипа внутри карточки счёта.
        assertThat(summary).contains("<div class=\"page-head\">");
        assertThat(summary).contains("<h1>Итоги сессии</h1>");
        assertThat(summary)
            .as("скрытый h1 счёта заменён видимым заголовком страницы")
            .doesNotContain("<h1 class=\"summary-section-title visually-hidden\">");
        assertThat(summary).contains("class=\"summary-score-value\"");
        // Пустая сессия (0 ответов) не имитирует результат: счёт, KPI и метрики гейтятся
        // noAnswers, вместо них — честное объяснение. «0 из 0 верных», «Новый рекорд» и
        // «Без ошибок» на нулевых данных читались как достижение.
        assertThat(summary).contains("th:unless=\"${noAnswers}\"");
        assertThat(summary).contains("class=\"summary-empty-note\"");
        // SUM-3: dual-path действия (повтор ошибок / продолжить) в nav с меткой.
        assertThat(summary).contains("class=\"summary-actions\"");
        assertThat(summary).contains("aria-label=\"Что дальше\"");
        assertThat(summary).contains("class=\"summary-more\"");
        assertThat(summary).contains("Ещё");
        assertThat(summary).contains("Продолжить тренировку");
        // Таблица по темам: ниже 760px CSS раскладывает её карточками (display:block на
        // table/thead/tbody/tr/td), а это в Safari/VoiceOver сбрасывает нативную семантику
        // → явные ARIA-роли её восстанавливают. Роли нужны и после iter-63.
        // iter-76: таблица носит классы общего компонента и лежит в .table-scroll —
        // единственном ответе продукта на «таблица шире полосы». tabindex НЕ в шаблоне:
        // его ставит table-scroll.js и только при реальном переполнении, иначе на
        // широком экране была бы вечная пустая остановка табуляции.
        assertThat(summary).contains("<div class=\"table-scroll\"");
        assertThat(summary).contains("<table class=\"data-table summary-table\" role=\"table\">");
        assertThat(summary)
            .as("остановку табуляции ставит JS по факту переполнения, не шаблон")
            .doesNotContain("summary-table\" role=\"table\" tabindex=\"0\"");
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
        // sidebar — nav-landmark с меткой + aria-current на активном пункте
        //           плюс футер глобальных действий (тоггл темы, «Выйти»);
        // shell-head — mobile-header с icon-only формой тех же действий;
        // mobile-nav — nav-landmark нижней навигации.
        //
        // Разметку сверяем БЕЗ комментариев. Пояснения в этих фрагментах длинные
        // и цитируют удалённые атрибуты — до FRD-021 здесь стояла проверка
        // `topbar.contains("role=\"progressbar\"")`, которая была зелёной ровно
        // потому, что строка встречалась в комментарии «прогресс отсюда убран»:
        // гвард сторожил собственный некролог.
        String sidebar = stripHtmlComments(readTemplate("templates/fragments/sidebar.html"));
        String shellHead = stripHtmlComments(readTemplate("templates/fragments/shell-head.html"));
        String mobileNav = stripHtmlComments(readTemplate("templates/fragments/mobile-nav.html"));

        assertThat(sidebar).contains("th:fragment=\"sidebar(active)\"");
        // Сам <aside> тоже обязан быть подписан, не только вложенный <nav>. На
        // экране фокуса рядом стоит второй complementary (focus-session-rail):
        // безымянный сайдбар давал бы SR два неразличимых «дополнения» подряд.
        assertThat(sidebar).contains("class=\"sidebar\" aria-label=\"Боковая панель\"");
        assertThat(sidebar).contains("<nav class=\"nav\" aria-label=\"Основная навигация\">");
        // Активный пункт помечается aria-current. Проверка идёт по МЕХАНИЗМУ, а не по
        // литералу 'focus': пункты больше не написаны руками — оба фрагмента печатают их
        // циклом по единственному источнику первого уровня (NavSection), потому что два
        // рукописных списка уже дважды разошлись (см. NavigationSourceContractTest).
        assertThat(sidebar).contains("aria-current=${active == section.id()} ? 'page' : null");
        // Глобальные действия рельса лежат ЗА пределами <nav>: иначе «Тема» и
        // «Выйти» попадают в список пунктов навигации, хотя никуда не ведут.
        assertThat(sidebar).containsSubsequence("</nav>", "class=\"sidebar-footer\"");
        // Ровно ОДИН тоггл темы на фрагмент. Гвард «в сайдбаре тоггла быть не
        // должно» (FRD-021 отменил) ставился против ДУБЛЯ: контрол одновременно
        // жил в футере рельса и в topbar, SR объявлял тему дважды. Полосы topbar
        // больше нет, дублировать стало нечем — но само требование единственности
        // осталось, теперь в виде счётчика на каждой из двух шапок.
        assertThat(countOccurrences(sidebar, "data-theme-toggle")).isEqualTo(1);
        assertThat(countOccurrences(shellHead, "data-theme-toggle")).isEqualTo(1);
        // PE: тоггл раскрывается только при JS (класс hidden снимает ui/shell.js).
        assertThat(sidebar).contains("class=\"side-action hidden\" data-theme-toggle");
        assertThat(shellHead).contains("class=\"icon-button hidden\" data-theme-toggle");
        // В рельсе у тоггла есть ВИДИМАЯ подпись, и её текст держит тот же
        // shell.js, что и aria-label (WCAG 2.5.3 Label in Name). Без якоря
        // [data-theme-label] подпись осталась бы серверной «Переключить тему»,
        // разойдясь с именем «Включить тёмную тему».
        assertThat(sidebar).contains("data-theme-label");

        assertThat(shellHead).contains("th:fragment=\"shell-head\"");
        // Десктопной полосы .topbar в шапке больше нет (FRD-021): 64px sticky на
        // все семь страниц ради одной кнопки 38×38.
        assertThat(shellHead).doesNotContain("class=\"topbar\"");
        assertThat(shellHead).contains("class=\"mobile-header\"");
        // aria-pressed на тоггле темы быть НЕ должно: светлая и тёмная — два
        // равноправных режима, а не вкл/выкл одного состояния. «Не нажата» на
        // кнопке в светлой теме SR читает как «тема выключена», что бессмысленно.
        // Актуальный режим объявляет aria-label, который shell.js держит в
        // согласии с темой.
        assertThat(shellHead).doesNotContain("aria-pressed=");
        assertThat(sidebar).doesNotContain("aria-pressed=");

        assertThat(mobileNav).contains("th:fragment=\"mobile-nav(active)\"");
        assertThat(mobileNav).contains("class=\"mobile-nav\" aria-label=\"Мобильная навигация\"");
        assertThat(mobileNav).contains("aria-current=${active == section.id()} ? 'page' : null");
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
            .contains("head", "sprite", "sidebar", "shell-head", "mobile-nav", "shell-scripts",
                      "notice-slot", "recoverable-banners", "post-answer-controls",
                      // «stats-grid-content» снят вместе со своим единственным носителем:
                      // сводку банка из конструктора сессии убрали как третью копию
                      // тех же шести чисел (/stats и /topics), и фрагмент осиротел.
                      "result-zone-head",
                      "today-chip", "training-actions", "mermaid-init");

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
    void statsTopicTableColumnsHaveMatchingMobileLabels() throws IOException {
        // .topic-table — ЕДИНСТВЕННАЯ таблица с card-view на мобиле (base.css
        // td[data-label]::before { content: attr(data-label) } — строка становится
        // карточкой, подпись столбца берётся из data-label на td). Инвариант: подпись
        // каждой колонки обязана иметь парный td data-label, иначе на узком экране
        // ячейка теряет имя своего столбца. Гвард против STA-20-класса рассинхрона
        // (R0.128 снял колонку «Сброшено» и сдвинул нумерацию — правка структуры
        // таблицы легко рвёт пары label↔header). App не нужен.
        //
        // iter-81: источник подписей переехал из разметки (data-sort-label на th, он
        // существовал ради снятого table-sort.js) в StatsPageService.columns() —
        // вместе с порядком колонок и состоянием сортировки. Поэтому подписи
        // спрашиваются у самой модели, а не выскабливаются регуляркой из шаблона:
        // сверяются две стороны контракта, а не шаблон сам с собой.
        //
        // summary-table (итоги) тоже card-view на мобиле, но её колонки моделью не
        // описаны — она в инвариант не входит. .data-table (coverage-gaps) обычная
        // узкая, data-label ей не нужен.
        Set<String> columnLabels = StatsPageService.TopicTableView.of(List.of()).columns().stream()
            .map(StatsPageService.TopicTableView.Column::label)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        String stats = readTemplate("templates/stats.html");
        Matcher dataLabelMatcher = Pattern.compile("data-label=\"([^\"]+)\"").matcher(stats);
        Set<String> dataLabels = new LinkedHashSet<>();
        while (dataLabelMatcher.find()) {
            dataLabels.add(dataLabelMatcher.group(1));
        }

        // Санити: модель действительно описывает те колонки, о которых идёт речь.
        assertThat(columnLabels).contains("Тема", "Всего", "Прогресс", "Точность", "Зрелость", "Действие");
        assertThat(dataLabels)
            .as("колонка таблицы тем без парного data-label теряет подпись в card-view на узком экране")
            .containsAll(columnLabels);
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

    /**
     * Оси персонализации со значением «auto» разрешаются системной настройкой. Прочитать
     * её на загрузке недостаточно: настройку меняют при открытой странице, и ось, которая
     * только спросила `matchMedia(...).matches`, остаётся с решением, принятым один раз.
     * CSS-медиа перерисуется сама, а JS-потребители (pixel-fx, графики) читают значение
     * вживую, но узнают о смене только из события — значит подписка обязательна.
     */
    @Test
    void osResolvedAxesSubscribeToTheMediaQueryTheyRead() throws IOException {
        String head = readTemplate("templates/fragments/head.html");

        Matcher declarations = Pattern
                .compile("var\\s+(\\w+)\\s*=\\s*window\\.matchMedia\\s*\\?\\s*window\\.matchMedia\\('([^']+)'\\)")
                .matcher(head);
        Map<String, String> queryByVar = new LinkedHashMap<>();
        while (declarations.find()) {
            queryByVar.put(declarations.group(1), declarations.group(2));
        }

        assertThat(queryByVar.values())
                .as("head.html разрешает «auto» по системным настройкам темы и движения")
                .contains("(prefers-color-scheme: light)", "(prefers-reduced-motion: reduce)");

        for (Map.Entry<String, String> axis : queryByVar.entrySet()) {
            assertThat(head)
                    .as("ось %s читает %s, но не подписана на её смену — значение, прочитанное "
                            + "на загрузке, доживёт до перезагрузки страницы", axis.getKey(), axis.getValue())
                    .containsPattern(Pattern.compile(Pattern.quote(axis.getKey()) + "\\.addEventListener\\('change'"));
        }

        // Подписка бесполезна без адресата: сигнал для оси «движение» единый — motionchange.
        assertThat(head).contains("new CustomEvent('motionchange'");
        assertThat(readTemplate("static/js/pixel-fx.js"))
                .as("pixel-fx решает про декор и подписку на pointermove — он обязан слышать motionchange")
                .contains("addEventListener('motionchange'");
        assertThat(readTemplate("static/js/stats/chart-theme.js"))
                .as("анимация графиков включается флагом на постройке — без motionchange она не пересоберётся")
                .contains("addEventListener('motionchange'");
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

    /** Убирает HTML- и Thymeleaf-комментарии: сверять контракт нужно с разметкой, а не с пояснениями к ней. */
    private static String stripHtmlComments(String html) {
        return html.replaceAll("(?s)<!--.*?-->", "");
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + needle.length())) {
            count++;
        }
        return count;
    }
}
