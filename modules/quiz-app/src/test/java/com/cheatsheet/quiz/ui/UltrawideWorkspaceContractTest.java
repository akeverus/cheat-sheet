package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сверхширокий экран: третья зона вместо жёлобов.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер полосы и жёлобов (Chromium и WebKit, экзамен, один вопрос):
 *
 * <pre>
 *   вьюпорт   оболочка  полоса  жёлоба      доля полосы
 *   1512      1281      1160    60 / 60     91 %
 *   1920      1672      1456    108 / 108   87 %
 *   2560      2309      1456    427 / 427   63 %
 *   3440      3189      1456    867 / 867   46 %
 * </pre>
 *
 * С 1920 полоса упиралась в кап и больше не росла: на 3440 почти половина рабочей
 * области — фон по краям (§13.1 брифа). «Поднять max-width» запрещено и бессмысленно:
 * мера полосы сложена из меры вариантов (72ch) и окна листинга.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>ширину забирает ТРЕТЬЯ ЗОНА (спутники вопроса), а не длина строки;</li>
 *   <li>мера полосы — сумма колонок, а не новое магическое число;</li>
 *   <li>полоса страницы следует за композицией, а не за собственным капом;</li>
 *   <li>колонка задания широка ради листинга и сжимается, когда листинга нет;</li>
 *   <li>пейн остаётся ОДНИМ элементом сетки: иначе вопрос падает в ряд высотой
 *       со список вариантов (замер первой редакции: вопрос на 603-м пикселе при
 *       спутниках на 108-м).</li>
 * </ol>
 */
class UltrawideWorkspaceContractTest {

    /** Порог третьей зоны: полосе нужно 1856px плюс жёлоба страницы. */
    private static final String THRESHOLD = "@media (min-width: 2240px)";

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusCss() throws IOException {
        return read("static/css/pages-focus.css").replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /** Тело медиазапроса порога — по балансу скобок. */
    private String ultrawideBlock() throws IOException {
        String css = focusCss();
        int at = css.indexOf(THRESHOLD);
        assertThat(at).as("порога третьей зоны нет в файле").isNotNegative();
        int open = css.indexOf('{', at);
        int depth = 0;
        for (int i = open; i < css.length(); i++) {
            char c = css.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return css.substring(open, i);
            }
        }
        throw new AssertionError("медиазапрос порога не закрыт");
    }

    @Test
    void theThirdZoneAppearsOnlyWhereItFits() throws IOException {
        String block = ultrawideBlock();
        assertThat(block)
                .as("третья колонка обязана быть объявлена именно здесь")
                .contains("var(--workspace-companions)");
        assertThat(block).contains("grid-column: 3");
        assertThat(focusCss())
                .as("ширина третьей колонки — токен, а не число в правиле")
                .contains("--workspace-companions:");
    }

    @Test
    void theBandMeasureIsASumOfItsColumns() throws IOException {
        String block = ultrawideBlock();
        assertThat(block)
                .as("мера полосы, записанная числом, молча разъедется с колонками")
                .contains("--focus-main-measure: calc(var(--workspace-brief)")
                .contains("var(--workspace-companions)");
    }

    @Test
    void thePageBandFollowsTheComposition() throws IOException {
        String block = ultrawideBlock();
        assertThat(block)
                .as("страница, капнутая уже своей композиции, отнимает ширину у колонок: "
                        + "замер первой редакции — 672/512/352 вместо 768/640/352")
                .contains("max-width: min(100%, calc(var(--focus-main-measure) + var(--page-gutter) * 2))");
    }

    @Test
    void theTaskColumnShrinksWithoutAListing() throws IOException {
        String block = ultrawideBlock();
        assertThat(block)
                .as("гейт по содержимому: без материалов колонка задания держит только заголовок")
                .contains(":not(:has(.question-materials))")
                .contains("--workspace-brief: 30rem");
    }

    @Test
    void thePaneStaysOneGridItem() throws IOException {
        String block = ultrawideBlock();
        assertThat(block)
                .as("расступается УЗЕЛ задания, а не пейн: иначе части вопроса делят ряды с работой")
                .contains("> .focus-task {")
                .contains("display: contents");
        assertThat(block)
                .as("пейн обязан остаться одним элементом сетки на всю высоту")
                .contains("> .focus-task > .focus-question-pane")
                .contains("grid-row: 1 / -1");

        String markup = read("templates/focus-training.html");
        int task = markup.indexOf("class=\"focus-task\"");
        int pane = markup.indexOf("class=\"focus-question-pane\"");
        int companions = markup.indexOf("class=\"focus-companions\"");
        assertThat(List.of(task, pane, companions))
                .as("узел задания обязан охватывать пейн и спутники именно в этом порядке")
                .isSorted();
        assertThat(task).isNotNegative();
    }
}
