package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Схема вопроса рисуется в читаемом масштабе, а не «сколько влезет по ширине».
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Схема вписывалась в ширину колонки: {@code svg.style.width = '100%'}. У широкого графа
 * (mermaid `graph LR`, 568 таких в корпусе из 1260) это давало масштаб в разы меньше
 * единицы. Замер (Chromium, 1512×982, случайная выборка 40 схем из корпуса, окно 550px):
 *
 * <pre>
 *   масштаб: мин 0.19  p25 0.42  медиана 0.58  макс 1.00
 *   высота подписи: мин 6.0px  медиана 13.4px
 *   у 68% схем подпись была мельче 12px, у 40% — мельче 8px
 * </pre>
 *
 * На телефоне тот же граф давал масштаб 0.14 и подпись 3.4px. При этом ПОД окном схемы
 * пустовало 497px на 1512×982 и 807px на 2560×1440: место было, материал был нечитаем.
 *
 * <h2>Замер после</h2>
 * Самая мелкая подпись по тем же 40 схемам — не ниже 11px (медиана 15.1). На экране
 * вопроса и в разборе, в Chromium и WebKit, на 390/1024/1512/2560: масштаб 0.50,
 * подпись 12px, окно прокручивается (окно 325…766 ← содержимое 995), варианты остаются
 * в первом экране, горизонтальной прокрутки страницы нет.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>масштаб — раскладка (ширина в пикселях), а не `transform`: трансформ рисует
 *       крупно поверх коробки прежнего размера, и окно не узнаёт, что содержимое шире;</li>
 *   <li>порог читаемости объявлен числом и не опускает схему ниже 11px подписи;</li>
 *   <li>схему не раздувают выше натуральной величины;</li>
 *   <li>прокручиваемая коробка ОДНА: носитель схемы — `<pre>`, а общее правило кода
 *       делает любой `<pre>` прокручиваемым, и прокрутку съедала внутренняя коробка;</li>
 *   <li>`max-width: 100%` не возвращает «вписать по ширине» поверх посчитанного масштаба;</li>
 *   <li>у прокручиваемой области есть остановка клавиатуры, имя для читалки и видимое
 *       кольцо фокуса (WCAG 2.1.1, 2.4.7).</li>
 * </ol>
 */
class DiagramLegibilityContractTest {

    private static final Pattern LINE_COMMENT = Pattern.compile("^\\s*//.*$", Pattern.MULTILINE);

    private String renderer() throws IOException {
        String js = CssRules.read("static/js/content/mermaid-renderer.js");
        return LINE_COMMENT.matcher(CssRules.stripComments(js)).replaceAll("");
    }

    @Test
    void theScaleIsLayoutAndNotATransform() throws IOException {
        String js = renderer();
        assertThat(js)
                .as("масштаб снова задаётся `transform`: схема рисуется крупно поверх коробки "
                        + "прежнего размера, окно не знает, что содержимое шире, и прокрутки нет")
                .doesNotContain("style.transform");
        assertThat(js)
                .as("ширина схемы должна считаться от натуральной величины (`viewBox`) и "
                        + "ставиться в пикселях")
                .containsPattern("style\\.width = Math\\.round\\(natural\\.width \\* scale\\) \\+ 'px'");
    }

    @Test
    void theLegibilityFloorIsDeclaredAndUsed() throws IOException {
        String js = renderer();
        Matcher m = Pattern.compile("const MIN_LABEL_PX = (\\d+)").matcher(js);
        assertThat(m.find()).as("порога читаемости подписи больше нет — масштаб снова «сколько влезет»").isTrue();
        int floor = Integer.parseInt(m.group(1));
        assertThat(floor)
                .as("порог читаемости опущен до %dpx: при 8px подпись схемы уже узор, "
                        + "замер давал 3.4px на телефоне", floor)
                .isBetween(11, 16);
        assertThat(js)
                .as("порог объявлен, но не участвует в расчёте масштаба")
                .containsPattern("MIN_LABEL_PX / smallest");
        assertThat(js)
                .as("схему нельзя раздувать выше натуральной величины — потолок `Math.min(1, …)`")
                .containsPattern("Math\\.min\\(1, Math\\.max\\(state\\.fit,");
    }

    @Test
    void thereIsExactlyOneScrollBoxPerDiagram() throws IOException {
        List<CssRules.Rule> focus = CssRules.parse("static/css/pages-focus.css");
        CssRules.Rule host = lastWithSelector(focus, "html[data-design] .focus-page .question-diagram .mermaid");
        String overflow = firstNonNull(host.value("overflow"), host.value("overflow-x"));
        assertThat(overflow)
                .as("носитель схемы снова прокручивается сам: он `<pre>`, общее правило кода "
                        + "делает его прокручиваемым, и окно материала перестаёт видеть, что "
                        + "схема шире него (замер: SVG 1435px, scrollWidth окна 600 при "
                        + "clientWidth 600, кромки и остановки клавиатуры нет)")
                .isNotNull()
                .isEqualTo("visible");
    }

    @Test
    void noStylesheetCapsTheComputedWidth() throws IOException {
        CssRules.Rule question = lastWithSelector(CssRules.parse("static/css/pages-focus.css"),
                "html[data-design] .focus-page .question-diagram .mermaid svg");
        CssRules.Rule prose = lastWithSelector(CssRules.parse("static/css/components-prose.css"),
                "html[data-design] .markdown-content .mermaid svg");
        for (CssRules.Rule rule : List.of(question, prose)) {
            assertThat(rule.value("max-width"))
                    .as("`max-width` у SVG молча возвращает «вписать по ширине» поверх "
                            + "посчитанного масштаба — правило читаемости перестаёт действовать")
                    .isEqualTo("none");
        }
    }

    @Test
    void theScrollableBoxIsReachableFromTheKeyboard() throws IOException {
        String js = renderer();
        assertThat(js)
                .as("прокручиваемая область снова без остановки клавиатуры — содержимое "
                        + "недостижимо без мыши (WCAG 2.1.1)")
                .contains("host.setAttribute('tabindex', '0')")
                .contains("host.setAttribute('role', 'group')")
                .containsPattern("aria-label', 'Схема");
        assertThat(js)
                .as("остановка обязана СНИМАТЬСЯ, когда прокручивать нечего: лишний стоп "
                        + "в обходе — это шум")
                .contains("host.removeAttribute('tabindex')");
        assertThat(js)
                .as("стрелки должны прокручивать окно: Chromium делает это сам, WebKit — нет "
                        + "(замер: scrollLeft 0→74 против 0→0 при фокусе на окне)")
                .contains("ArrowRight").contains("ArrowLeft").contains("Home");

        List<CssRules.Rule> focus = CssRules.parse("static/css/pages-focus.css");
        assertThat(focus.stream().anyMatch(r -> r.hasSelector("html[data-design] .focus-page .diagram-viewport:focus-visible")))
                .as("у окна схемы нет видимого кольца фокуса, хотя оно принимает фокус (WCAG 2.4.7)")
                .isTrue();
        assertThat(CssRules.parse("static/css/components-prose.css").stream()
                .anyMatch(r -> r.hasSelector("html[data-design] .markdown-content .mermaid:focus-visible")))
                .as("схема в разборе принимает фокус без видимого кольца")
                .isTrue();
    }

    /**
     * С какого места открывается схема, которая шире окна, — это вопрос про граф, а не
     * про экран. У дерева сверху вниз корень стоит по центру: замер на 390×844 (окно 325,
     * содержимое 995) показывал первым в кадре лист «ArrayDeque (push/pop)», а корень
     * «Что нужно?» оставался за правым краем.
     */
    @Test
    void theFirstViewDependsOnTheDirectionOfTheGraph() throws IOException {
        String js = renderer();
        assertThat(js)
                .as("направление графа больше не читается — дерево снова открывается на листе")
                .containsPattern("function isVerticalFlow[\\s\\S]{0,300}?(flowchart\\|graph)");
        assertThat(js)
                .as("центрирование обязано зависеть от направления и работать только на "
                        + "автоматическом масштабе: после ручного зума место просмотра выбирает человек")
                .containsPattern("overflowsX && state\\.auto && !state\\.centered && isVerticalFlow\\(el\\)");
        assertThat(js)
                .as("центрируется горизонтальная прокрутка окна, а не что-то ещё")
                .contains("host.scrollLeft = Math.round((host.scrollWidth - host.clientWidth) / 2)");
    }

    /** Тач не должен блокироваться: прокрутка окна — это и есть панорама схемы. */
    @Test
    void touchIsNotTakenAwayFromTheScrollBox() throws IOException {
        List<CssRules.Rule> focus = CssRules.parse("static/css/pages-focus.css");
        CssRules.Rule viewport = lastWithSelector(focus,
                "html[data-design] .focus-page .question-diagram[data-content-type=\"MERMAID\"] .diagram-viewport");
        assertThat(viewport.value("touch-action"))
                .as("`touch-action: none` вернулся: он выключает нативную прокрутку, ради "
                        + "которой панорама и переведена на `scrollLeft`")
                .isNotEqualTo("none");
    }

    @Test
    void pendingDiagramRemainsObservableAndFailureClearsPendingState() throws IOException {
        String css = CssRules.stripComments(CssRules.read("static/css/pages-focus.css"));
        assertThat(css)
                .as("display:none на pending-предке обнуляет rect Mermaid: IntersectionObserver "
                        + "никогда не запускает ленивый рендер, а pending остаётся навсегда")
                .doesNotContainPattern("is-diagram-pending[^\\{]*\\{[^}]*display\\s*:\\s*none");

        String js = renderer();
        assertThat(js)
                .as("ошибка загрузки vendor должна снять pending и показать fallback, а не "
                        + "оставить материал в бесконечном ожидании")
                .containsPattern("if \\(!loaded\\)[\\s\\S]{0,220}failDiagram\\(el\\)[\\s\\S]{0,120}markMaterialsFailed\\(el\\)")
                .doesNotContainPattern("if \\(!loaded\\)[\\s\\S]{0,220}markMaterialsReady\\(el\\)");

        String shell = CssRules.stripComments(CssRules.read("static/css/shell.css"));
        assertThat(shell)
                .as("zoom/fit/fullscreen без SVG — мёртвые обещания; при failure видим только fallback и исходник")
                .containsPattern("question-diagram\\[data-render-failed\\][^{]*diagram-toolbar-actions[^}]*display\\s*:\\s*none");

        assertThat(css)
                .as("focus page переопределяет display Mermaid в более позднем cascade-layer; "
                        + "failure обязан повторно убрать и пустой host, и его toolbar")
                .containsPattern("question-diagram\\[data-render-failed\\][^{]*mermaid[^,{]*,[^{]*"
                        + "question-diagram\\[data-render-failed\\][^{]*diagram-toolbar-actions\\s*\\{[^}]*display\\s*:\\s*none");
    }

    private CssRules.Rule lastWithSelector(List<CssRules.Rule> rules, String selector) {
        List<CssRules.Rule> found = rules.stream().filter(r -> r.hasSelector(selector)).toList();
        assertThat(found).as("правила `%s` нет вовсе", selector).isNotEmpty();
        return found.get(found.size() - 1);
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
