package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * В фазе изучения ширину получает ЧТЕНИЕ: колонка текста равна мере, а не остатку.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Пропорции фазы чтения задавались шириной колонки ЗАДАНИЯ (26rem), а тексту доставался
 * остаток полосы — то есть длина строки зависела от ширины окна, а не от того, сколько
 * знаков читается удобно. Замер длины строки (Chromium, один и тот же вопрос):
 *
 * <pre>
 *   вьюпорт     1280   1366   1440   1512   1680   1920   2560
 *   знаков       41     46     52     57     70     72     58
 * </pre>
 *
 * Нижняя граница читаемости — 60 знаков; на всём ноутбучном диапазоне строка короче. На
 * 1920 колонка, наоборот, ШИРЕ прозы: 992px колонки при 818px текста — 174px мёртвой
 * полосы внутри колонки.
 *
 * <h2>Замер после</h2>
 * <pre>
 *   вьюпорт   знаков (Chromium/WebKit)   документ
 *   1280        51 / 52                   3342 → 2945
 *   1366        57 / 59                   3067 → 2883
 *   1440        63 / 64                   2924 → 2802
 *   1512        68 / 70                   2863 → 2802
 *   1920        72 / 72                   2801
 *   2560        72 / 72                   2862 → 2801
 * </pre>
 *
 * Документ короче именно потому, что строка длиннее: тот же текст занимает меньше строк.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>колонка чтения — это мера ПЛЮС поля карточки, объявленные слагаемыми;</li>
 *   <li>пол колонки задания зависит от содержимого: с материалами прежние 26rem, без
 *       них 18rem (18, а не 16: в 256px самое длинное слово заголовка не помещалось и
 *       рвалось без дефиса);</li>
 *   <li>правила фазы чтения не слабее общего правила рабочего места — иначе они молча
 *       не действуют;</li>
 *   <li>полоса страницы считается ТЕМИ ЖЕ слагаемыми, что и колонки.</li>
 * </ol>
 */
class StudyReadingContractTest {

    private static final String СЕЛЕКТОР_РАБОЧЕГО_МЕСТА = ".workspace-surface:not(.hidden)";

    private List<CssRules.Rule> focus() throws IOException {
        return CssRules.parse("static/css/pages-focus.css");
    }

    private List<CssRules.Rule> studyGrids(List<CssRules.Rule> rules) {
        List<CssRules.Rule> found = rules.stream()
                .filter(r -> r.selectors().contains(".study-learn-phase"))
                .filter(r -> r.value("grid-template-columns") != null)
                .toList();
        assertThat(found).as("правил сетки фазы чтения нет вовсе").isNotEmpty();
        return found;
    }

    @Test
    void theReadingColumnIsTheMeasurePlusTheCardPadding() throws IOException {
        List<CssRules.Rule> rules = focus();
        String token = rules.stream()
                .map(r -> r.value("--reading-column"))
                .filter(java.util.Objects::nonNull)
                .findFirst().orElse(null);
        assertThat(token)
                .as("токена колонки чтения нет: ширина текста снова станет остатком полосы")
                .isNotNull();
        assertThat(token)
                .as("колонка чтения перестала считаться от меры — тогда длина строки зависит "
                        + "от ширины окна, а не от читаемости")
                .contains("var(--measure)");
        assertThat(token)
                .as("поля карточки разбора не учтены: замер 1920×1200 показывал колонку 818 "
                        + "при прозе 768 — мера теряла 50px и строка выходила 68 знаков вместо 72")
                .containsPattern("var\\(--space-\\d\\)");

        for (CssRules.Rule grid : studyGrids(rules)) {
            assertThat(grid.value("grid-template-columns"))
                    .as("колонка чтения в сетке задана не мерой, а чем-то ещё: %s", grid.selectors())
                    .contains("var(--reading-column)");
        }
    }

    @Test
    void theBriefFloorFollowsWhatTheColumnHolds() throws IOException {
        List<CssRules.Rule> grids = studyGrids(focus());
        List<CssRules.Rule> сМатериалом = grids.stream()
                .filter(r -> r.selectors().contains(":has(.question-materials.is-ready)")).toList();
        List<CssRules.Rule> безМатериала = grids.stream()
                .filter(r -> !r.selectors().contains(":has(.question-materials.is-ready)")).toList();
        assertThat(сМатериалом).as("правила для колонки С материалами нет").isNotEmpty();
        assertThat(безМатериала).as("правила для колонки без материалов нет").isNotEmpty();

        // Проверяем КАЖДОЕ правило, а не последнее: у фазы чтения их два (две зоны и
        // три), и укус, поставленный в первое, прошёл бы мимо проверки по последнему.
        for (CssRules.Rule rule : безМатериала) {
            assertThat(floorRem(rule))
                    .as("пол колонки задания опущен ниже 18rem (%s): в 256px самое длинное "
                            + "слово заголовка («алгоритмическому», 260px при кегле этой "
                            + "колонки) не помещается и рвётся без дефиса", rule.media())
                    .isGreaterThanOrEqualTo(18);
        }
        double безМат = безМатериала.stream().mapToDouble(this::floorRem).max().orElseThrow();
        for (CssRules.Rule rule : сМатериалом) {
            assertThat(floorRem(rule))
                    .as("пол колонки С материалами (%s) не больше, чем без них: листинг и "
                            + "схема ужимаются ради прозы, которая и так получила свою меру",
                            rule.media())
                    .isGreaterThan(безМат);
        }
    }

    /** Слабое правило — это правило, которого нет: оно молча проигрывает общему. */
    @Test
    void theReadingRulesOutweighTheGenericWorkspaceRule() throws IOException {
        List<CssRules.Rule> rules = focus();
        for (CssRules.Rule grid : studyGrids(rules)) {
            assertThat(grid.selectors())
                    .as("правило фазы чтения слабее общего правила рабочего места "
                            + "(`:has(%s)`) и молча не действует: замер первой сборки — сетка "
                            + "оставалась 416 + 512 при вьюпорте 1512", СЕЛЕКТОР_РАБОЧЕГО_МЕСТА)
                    .contains(СЕЛЕКТОР_РАБОЧЕГО_МЕСТА);
        }
        List<CssRules.Rule> полосы = rules.stream()
                .filter(r -> r.selectors().contains(".study-learn-phase"))
                .filter(r -> r.value("--focus-main-measure") != null)
                .toList();
        assertThat(полосы).as("формулы полосы для фазы чтения нет").isNotEmpty();
        for (CssRules.Rule band : полосы) {
            assertThat(band.selectors())
                    .as("формула полосы фазы чтения слабее общей: замер — колонки 288 + 832 + 352 "
                            + "вместо 288 + 868 + 352, мера чтения теряла 36px")
                    .contains(СЕЛЕКТОР_РАБОЧЕГО_МЕСТА);
            assertThat(band.value("--focus-main-measure"))
                    .as("полоса считается не теми слагаемыми, что колонки: она окажется уже "
                            + "своей композиции, и первой схлопнется мера чтения")
                    .contains("var(--reading-column)")
                    .contains("var(--workspace-companions)");
        }
    }

    private double floorRem(CssRules.Rule grid) {
        Matcher m = Pattern.compile("minmax\\(\\s*([\\d.]+)rem").matcher(grid.value("grid-template-columns"));
        assertThat(m.find()).as("в сетке нет пола первой колонки: %s", grid.value("grid-template-columns")).isTrue();
        return Double.parseDouble(m.group(1));
    }
}
