package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * На бумагу не попадает интерактив, а свёрнутое «для телефона» раскрывается.
 *
 * <h2>Найденные дефекты, а не гипотезы (замер PDF, iter-165)</h2>
 * <ol>
 *   <li>печатались кнопки: баннер «Продолжить», код-тулбар (Перенос/Копировать/
 *       Экран), «Записать мысль», «Сообщить о проблеме», форма отбора и колонка
 *       «Тренировать» (28 мёртвых ссылок на лист);</li>
 *   <li>печатная раскладка A4 (~816px CSS) сама флипала phone-медиазапрос
 *       модуля раскрытий, и change-событие схлопывало «Где я сейчас» и «Знаки
 *       отличия» ПРЯМО В ЛИСТ — даже у десктопного окна, где на экране всё
 *       открыто; beforeprint стрелял раньше перестройки и ничего не находил;</li>
 *   <li>заливка XP-бара — background псевдоэлементов {@code <progress>},
 *       браузеры фоны не печатают: на бумаге «0%» вместо фактических 45%.</li>
 * </ol>
 *
 * <p>Проверено эмуляцией print-медиа в Chromium И WebKit (816×1056): разделы
 * открыты, отбор/колонка скрыты; afterprint возвращает свёрнутое; экранный
 * коллапс на 390 не задет. CDP printToPDF не диспатчит beforeprint — пробы
 * шлют его руками; реальный Cmd+P диспатчит во всех движках.</p>
 */
class PrintContractTest {

    private String printBlock() throws IOException {
        String css = CssRules.stripComments(CssRules.read("static/css/print.css"));
        int start = css.indexOf("@media print");
        assertThat(start).as("print.css обязан держать @media print").isNotNegative();
        return css.substring(start);
    }

    @Test
    void theInteractiveChromeStaysOffThePaper() throws IOException {
        String print = printBlock();
        // .builder-launch-bar — липкая панель «Начать тренировку» на /train:
        // печаталась ПОВЕРХ карточек режимов (замер PDF iter-170, после переезда
        // конструктора на /train параллельным циклом рефаундации, UXD-001).
        for (String selector : List.of(".resume-banner", ".code-toolbar-actions",
                ".question-note-disclosure", ".report-zone", ".filter-bar", ".topic-actions",
                ".builder-launch-bar")) {
            // Граница после имени обязательна: первая редакция ловила `.filter-bar`
            // ПОДСТРОКОЙ в `.filter-bar-actions`, и укус изъятием правила прошёл
            // зелёным — гейт держал не селектор, а совпадение букв.
            assertThat(print)
                    .as("%s снова печатается: мёртвый интерактив на листе", selector)
                    .containsPattern(java.util.regex.Pattern.quote(selector) + "[\\s,{]");
        }
        assertThat(print)
                .as("сохранённая заметка — данные, её прятать нельзя")
                .doesNotContain(".question-note-text {");
    }

    @Test
    void theProgressFillPrintsHonestly() throws IOException {
        assertThat(printBlock())
                .as("без print-color-adjust XP-бар печатается пустой рамкой — «0%» "
                        + "вместо фактического значения")
                .containsPattern("progress \\{[^}]*print-color-adjust: exact");
    }

    /**
     * Вкладки на листе — стопка всех панелей, как в no-JS.
     *
     * <p>Замер (FRD-217→221): печатался только активный «Темы» — 2/3 аналитики
     * (Динамика с журналом занятий и прогнозом повторений, Ошибки) не попадали
     * на бумагу вовсе. После правки PDF аналитики — 11 страниц со всеми тремя
     * панелями и видимыми заголовками (под вкладками заголовок панели спрятан
     * как дубль активной вкладки — на листе он единственная навигация).
     * Canvas-графики скрыты: их заливки не печатаются (пустая дыра), а данные
     * печатает табличное представление, стоящее ВНЕ .chart-wrapper по доктрине
     * accessible-alternative.</p>
     */
    @Test
    void theTabsPrintAsAStackWithVisibleTitles() throws IOException {
        String print = printBlock();
        assertThat(print)
                .as("стопка панелей пропала: на бумагу снова попадает только активная")
                .containsPattern("\\[role=\"tabpanel\"\\]:not\\(\\.is-active\\) \\{ display: block !important; \\}");
        assertThat(print)
                .as("полоса вкладок — мёртвый интерактив на листе")
                .containsPattern("\\[role=\"tablist\"\\] \\{ display: none !important; \\}");
        assertThat(print)
                .as("заголовок панели обязан вернуться из visually-hidden")
                .containsPattern("\\.tabpanel-title \\{\\s*position: static !important;");
        assertThat(print)
                .as("canvas-график печатается пустой дырой — данные печатает таблица-двойник")
                .containsPattern("\\.chart-wrapper \\{ display: none !important; \\}");
        assertThat(print)
                .as("th колонки действий (topic-col-actions) печатался без своих ячеек")
                .containsPattern("\\.topic-col-actions[\\s,{]");
    }

    @Test
    void theCollapsedDisclosuresOpenForPrintAndComeBack() throws IOException {
        String js = CssRules.read("static/js/ui/mobile-disclosures.js");
        assertThat(js)
                .as("beforeprint-раскрытие пропало: свёрнутое печатается одной строкой с ▾")
                .contains("addEventListener('beforeprint'");
        assertThat(js)
                .as("afterprint-возврат пропал: после печати экран остаётся раскрытым")
                .contains("addEventListener('afterprint'");
        assertThat(js)
                .as("гвард печати в apply() пропал: change-событие print-раскладки "
                        + "схлопывает разделы прямо в лист — beforeprint стреляет раньше "
                        + "перестройки и бессилен")
                .contains("if (printing.matches || !phone.matches)");
    }
}
