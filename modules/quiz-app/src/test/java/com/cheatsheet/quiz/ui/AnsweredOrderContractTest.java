package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * После ответа порядок один и тот же — со скриптами и без них.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Серверная страница ответа (`/answer`) с iter-136 читается как результат: вердикт →
 * что это дало → куда дальше → и только потом разбор. Путь со скриптами остался
 * прежним: вердикт → вкладки → панель → мета → уверенность → «Следующий вопрос».
 * Один и тот же момент продукта имел два разных порядка.
 *
 * <p>Замер (Chromium и WebKit, тренировка, один ответ; страница сама прокручивается к
 * вердикту):</p>
 *
 * <pre>
 *   вьюпорт      «Следующий вопрос»   в первом экране
 *   390×844          @2682                 нет
 *   768×1024         @1736                 да
 *   1280×720         @1728                 нет
 *   1512×982         @2175                 да
 *   1920×1080        @2147                 да
 *   2560×1440        @1799                 нет
 * </pre>
 *
 * В WebKit из шести ширин действие не попадало в экран на четырёх. После правки —
 * в первом экране на всех двенадцати замерах обоих движков (@1135…@2172), и точка
 * клика возвращает саму ссылку.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>порядок внутри приёмника разбора: вердикт → мета → слот управления → вкладки;</li>
 *   <li>уверенность и «следующий вопрос» кладутся В СЛОТ, а не в конец зоны;</li>
 *   <li>перестановка сделана разметкой, а не `order`: обе врезки содержат
 *       фокусируемое, и CSS-перестановка развела бы обход с чтением (FRD-184).</li>
 * </ol>
 */
class AnsweredOrderContractTest {

    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern JS_LINE_COMMENT = Pattern.compile("^\\s*//.*$", Pattern.MULTILINE);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String interviewJs() throws IOException {
        return JS_LINE_COMMENT.matcher(read("static/js/quiz/interview.js")).replaceAll("");
    }

    @Test
    void theVerdictAndItsConsequenceComeBeforeTheAnalysis() throws IOException {
        String js = interviewJs();
        int verdict = js.indexOf("feedback-verdict result-banner");
        int meta = js.indexOf("<div class=\"feedback-meta\">");
        int slot = js.indexOf("id=\"feedback-controls\"");
        int tabs = js.indexOf("${tablist}");
        int panels = js.indexOf("${panelsHtml}");

        assertThat(List.of(verdict, meta, slot, tabs, panels))
                .as("разбор снова стоит между вердиктом и действием: после ответа "
                        + "«Следующий вопрос» уезжает на 1728…2682-й пиксель")
                .allSatisfy(i -> assertThat(i).isNotNegative())
                .isSorted();
    }

    @Test
    void theControlsAreAppendedIntoTheSlot() throws IOException {
        String js = interviewJs();
        assertThat(js)
                .as("уверенность снова вставляется после приёмника — то есть за разбором")
                .doesNotContain("feedbackDiv.after(confidenceDiv)");
        assertThat(js)
                .as("уверенность обязана попадать в слот управления")
                .contains("document.getElementById('feedback-controls') || feedbackDiv).appendChild(confidenceDiv)");
        assertThat(js)
                .as("«следующий вопрос» обязан целиться в слот, а не в конец пост-ответной зоны")
                .containsPattern("const controlsSlot = document\\.getElementById\\('feedback-controls'\\);[\\s\\S]{0,200}?const postAnswerZone = controlsSlot");
    }

    /**
     * Перестановка через CSS `order` здесь запрещена: во вкладках и панелях живут
     * кнопки и ссылки, и визуальный порядок разошёлся бы с порядком обхода.
     */
    @Test
    void theOrderIsNotFakedWithCss() throws IOException {
        String css = CSS_COMMENT.matcher(read("static/css/pages-focus.css")).replaceAll("");
        assertThat(css)
                .as("порядок пост-ответной зоны снова подделан `order` — обход с клавиатуры "
                        + "разойдётся с чтением")
                .doesNotContainPattern("\\.feedback-(tabs|panel|verdict|meta|controls)[^{]*\\{[^}]*order:");
        assertThat(css)
                .as("слот управления должен существовать как ряд и исчезать пустым")
                .containsPattern("\\.feedback-controls \\{[\\s\\S]{0,200}?display: flex")
                .contains(".feedback-controls:empty { display: none; }");
    }

    /**
     * Куда прокрутиться после ответа — решение страницы, а не побочный эффект фокуса.
     * Замер WebKit 390×844 на длинном вопросе (документ 4296): прокрутка вставала на
     * 2241 при вердикте на 1805 — экран уезжал ниже вердикта, а вместе с ним и
     * действие, которое теперь стоит сразу под ним.
     */
    @Test
    void theScrollTargetsTheTopOfTheAnalysisExplicitly() throws IOException {
        String js = interviewJs();
        assertThat(js)
                .as("фокус снова прокручивает страницу сам: точка остановки зависит от "
                        + "движка и высоты панели")
                .contains("feedbackDiv.focus({ preventScroll: true })");
        assertThat(js)
                .as("явной прокрутки к верху разбора нет")
                .contains("feedbackDiv.scrollIntoView({ block: 'start' })");

        String css = CSS_COMMENT.matcher(read("static/css/pages-focus.css")).replaceAll("");
        assertThat(css)
                .as("mobile-header закрывает верх приземления — пользователь получает фокус, "
                        + "но не видит вердикт")
                .containsPattern("@media \\(max-width:\\s*900px\\)[\\s\\S]*?"
                        + "\\.result-feedback\\s*\\{[^}]*scroll-margin-top:\\s*calc\\(44px");
    }

    /** Селектор без носителя — правило, которое никогда не сработает. */
    @Test
    void theFocusStylesheetHasNoSelectorWithoutACarrier() throws IOException {
        String css = CSS_COMMENT.matcher(read("static/css/pages-focus.css")).replaceAll("");
        assertThat(css)
                .as("`.topic-brief-link` вернулся: узла с таким классом нет ни в одном "
                        + "шаблоне и ни в одном скрипте (проверено поиском по всему /templates и /js)")
                .doesNotContain("topic-brief-link");
    }
}
