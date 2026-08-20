package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Первый экран коллекции показывает КОЛЛЕКЦИЮ, а не форму отбора над ней.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер (Chromium и WebKit, 30 закладок, 390×844): в первый экран не попадала
 * НИ ОДНА закладка. Бюджет до коллекции — 468px:
 *
 * <pre>
 *   шапка страницы          142
 *   заголовок «Список»       41  (антиква 36px, ноль сведений)
 *   «Тренировать избранные»  44
 *   форма отбора            241
 *   первая закладка начиналась на 661-м пикселе при сгибе 844
 * </pre>
 *
 * После правки первая закладка начинается на 327-м, в сгиб попадают две, документ
 * 6241 → 5908.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>форма отбора живёт под раскрытием и сворачивается на телефоне скриптом,
 *       но в разметке стоит открытой — без JS страница остаётся рабочей;</li>
 *   <li>пометку сворачивания ставит сервер и только при пустом отборе;</li>
 *   <li>строка состояния отбора остаётся ВНЕ раскрытия: свёрнутая форма не должна
 *       прятать то, что уже отобрано;</li>
 *   <li>заголовок карточки остаётся доступным именем, но не занимает экран;</li>
 *   <li>шапка раскрытия не рисуется там, где сворачивать нечего;</li>
 *   <li>в `th:attr` нет пустых значений: атрибут с пустой строкой Thymeleaf
 *       СНИМАЕТ, и правило молча перестаёт существовать.</li>
 * </ol>
 */
class BookmarksFilterContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String bookmarksMarkup() throws IOException {
        return COMMENT.matcher(read("templates/bookmarks.html")).replaceAll("");
    }

    @Test
    void theFilterLivesInsideADisclosureThatWorksWithoutScripts() throws IOException {
        String markup = bookmarksMarkup();
        assertThat(markup)
                .as("форма отбора снова стоит открытой лентой — на 390 это 241px "
                        + "между шапкой и коллекцией")
                .contains("<details class=\"filter-bar-disclosure\" open");
        assertThat(markup)
                .as("раскрытие обязано быть открытым в разметке: закрытое сервером, "
                        + "без JS оно не откроется ничем, кроме клика — и отбор исчезнет")
                .containsPattern("<details class=\"filter-bar-disclosure\" open[^>]*>");
        int details = markup.indexOf("<details class=\"filter-bar-disclosure\"");
        int form = markup.indexOf("class=\"filter-bar-form\"");
        assertThat(form).as("формы отбора нет").isNotNegative();
        assertThat(details).as("форма обязана лежать ВНУТРИ раскрытия").isLessThan(form);
    }

    /**
     * Пустая строка в {@code th:attr} — это снятие атрибута, а не пустое значение.
     * Первая редакция писала {@code ? null : ''}, и пометка не появлялась вообще;
     * страница при этом отдавалась целиком, то есть отказ был тихим.
     */
    @Test
    void theCollapseMarkerIsServerSideAndNeverEmpty() throws IOException {
        String markup = bookmarksMarkup();
        assertThat(markup)
                .as("пометку сворачивания ставит сервер — и только когда отбор пуст: "
                        + "в середине поиска захлопывать контролы нельзя")
                .containsPattern("th:attr=\"data-mobile-collapsed=\\$\\{bookmarksFilterActive \\? null : '[^']+'}\"");

        for (Path template : templateFiles()) {
            String body = COMMENT.matcher(Files.readString(template)).replaceAll("");
            assertThat(body)
                    .as("в %s есть `th:attr` с пустым значением: Thymeleaf снимает такой "
                            + "атрибут целиком, и правило молча перестаёт существовать",
                            template.getFileName())
                    .doesNotContainPattern("th:attr=\"[^\"]*=''");
        }
    }

    @Test
    void theFilterStateStaysOutsideTheDisclosure() throws IOException {
        String markup = bookmarksMarkup();
        int state = markup.indexOf("class=\"filter-bar-state\"");
        int details = markup.indexOf("<details class=\"filter-bar-disclosure\"");
        assertThat(state).as("строки состояния отбора нет").isNotNegative();
        assertThat(state)
                .as("строка «Отобрано: …» уехала под раскрытие — свёрнутая форма стала бы "
                        + "прятать то, что уже отобрано, вместе с кнопкой «Сбросить»")
                .isLessThan(details);
    }

    @Test
    void theListHeadingStaysAnAccessibleNameOnly() throws IOException {
        String markup = bookmarksMarkup();
        assertThat(markup)
                .as("карточка списка потеряла доступное имя")
                .contains("aria-labelledby=\"bookmarks-list-title\"");
        assertThat(markup)
                .as("заголовок «Список» снова печатается на экране: карточка на этой "
                        + "странице одна и она И ЕСТЬ список, а название даёт шапка страницы")
                .containsPattern("id=\"bookmarks-list-title\"[^>]*class=\"[^\"]*visually-hidden");
    }

    @Test
    void theDisclosureHeaderIsDrawnOnlyWhereThereIsSomethingToCollapse() throws IOException {
        String css = read("static/css/pages-bookmarks.css");
        assertThat(css)
                .as("шапка раскрытия рисуется на широком экране — это возвращает подпись "
                        + "над очевидным, снятую отсюда в iter-74 ценой 57px")
                .containsPattern("\\.bookmarks-page \\.filter-bar-summary \\{\\s*display: none");
        assertThat(css)
                .as("на телефоне шапка обязана проявиться: без неё раскрытие нечем открыть")
                .containsPattern("@media \\(max-width: 767px\\) \\{[\\s\\S]*?\\.filter-bar-summary \\{[\\s\\S]*?display: flex");
    }

    /** Селектор без носителя — это правило, которое никогда не сработает. */
    @Test
    void theStylesheetHasNoSelectorsWithoutCarriers() throws IOException {
        // Комментарии снимаем: удалённый селектор назван в тексте объяснения, и
        // проверять надо ПРАВИЛА, а не рассказ о них. Первая редакция гейта падала
        // именно на собственном комментарии.
        String css = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)
                .matcher(read("static/css/pages-bookmarks.css")).replaceAll("");
        assertThat(css)
                .as("`.bookmark-actions` вернулся в CSS: узла с таким классом в "
                        + "bookmarks.html нет ни одного")
                .doesNotContain(".bookmark-actions");
    }

    @Test
    void theCollapseModuleIsDeclaredForThisPageToo() throws IOException {
        String manifest = read("js-preload-manifest.txt");
        assertThat(manifest)
                .as("модуль сворачивания не объявлен для закладок — браузер узнает о нём "
                        + "лишь исполнив app-entry и уедет в отдельную волну")
                .containsPattern("focus,bookmarks /js/ui/mobile-disclosures\\.js");
    }

    private List<Path> templateFiles() throws IOException {
        Path root = Path.of("src/main/resources/templates");
        Path base = Files.exists(root) ? root : Path.of("modules/quiz-app/src/main/resources/templates");
        try (Stream<Path> paths = Files.walk(base)) {
            return paths.filter(p -> p.toString().endsWith(".html")).toList();
        }
    }
}
