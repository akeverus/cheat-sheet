package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Конструктор сессии читается как последовательность решений.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер разметки и экрана (Chromium и WebKit, 1280–2560):
 * <ul>
 *   <li>объём сессии («сколько») был ПОСЛЕДНЕЙ строкой формы режима — за восемью
 *       карточками, то есть выглядел сноской к выбору режима;</li>
 *   <li>на вопрос «что мне достанется по этому отбору» отвечали ДВА места сразу:
 *       карточка «Повторение» с одним крупным числом справа вверху и строка
 *       «В наборе / Примерно / Фильтры» в закреплённой панели на ~350px ниже;</li>
 *   <li>две из трёх метрик этой строки без JS печатали заглушку «по числу
 *       вопросов», то есть не сообщали ничего.</li>
 * </ul>
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>порядок шагов в дереве: что тренируем → как → сколько;</li>
 *   <li>шаг «сколько» — поле той же формы запуска, без JS отправляется как раньше;</li>
 *   <li>состав набора приходит с сервера и виден без скриптов;</li>
 *   <li>то, что считает скрипт, приходит скрытым, а не заглушкой;</li>
 *   <li>раскладка знает те же четыре зоны, что и разметка.</li>
 * </ol>
 */
class SessionBuilderStepsContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String markup() throws IOException {
        // UXD-001: конструктор живёт во фрагменте поверхности /train.
        return COMMENT.matcher(read("templates/fragments/session-builder.html")).replaceAll("");
    }

    @Test
    void theStepsFollowTheDecisionOrder() throws IOException {
        String markup = markup();
        int scope = markup.indexOf("builder-scope-title\">Что тренируем");
        int mode = markup.indexOf("builder-mode-title\">Как тренируем");
        int volume = markup.indexOf("builder-volume-title\">Сколько");

        assertThat(scope).as("шаг «что тренируем» обязан быть в разметке").isNotNegative();
        assertThat(mode).as("шаг «как тренируем» обязан быть в разметке").isNotNegative();
        assertThat(volume).as("шаг «сколько» обязан быть отдельным шагом").isNotNegative();
        assertThat(List.of(scope, mode, volume))
                .as("порядок в дереве — это и порядок чтения, и порядок табуляции")
                .isSorted();

        // Ответ стоит рядом со своим вопросом В ДЕРЕВЕ, а не только на широком
        // экране. Сетка кладёт блок «что попадёт» в первый ряд справа, и если бы в
        // разметке он остался ниже режима и объёма, зрячий читал бы его вторым, а
        // скринридер — четвёртым.
        int outcome = markup.indexOf("class=\"builder-outcome\"");
        assertThat(outcome).isNotNegative();
        assertThat(outcome)
                .as("«что попадёт» — ответ на первый шаг и стоит сразу за ним")
                .isGreaterThan(scope).isLessThan(mode);
    }

    /**
     * Поле объёма стоит вне формы запуска и привязано к ней атрибутом — тем же
     * приёмом, что и сама кнопка. Иначе шаг «сколько» пришлось бы держать внутри
     * формы режима, то есть там, откуда его и убрали.
     */
    @Test
    void theVolumeFieldStillBelongsToTheLaunchForm() throws IOException {
        String markup = markup();
        int field = markup.indexOf("name=\"count\"");
        assertThat(field).isNotNegative();
        String around = markup.substring(field, Math.min(markup.length(), field + 220));
        assertThat(around)
                .as("без привязки к форме объём не уедет на сервер, и это сломает старт без JS")
                .contains("form=\"session-form\"");
        assertThat(markup)
                .as("шаг прячется целиком: заголовок с номером над пустотой — не шаг")
                .contains("id=\"builder-volume\"");
    }

    @Test
    void theSetCompositionIsRenderedByTheServer() throws IOException {
        String markup = markup();
        assertThat(markup).contains("class=\"builder-outcome\"");
        assertThat(markup)
                .as("три числа набора считает сервер — без JS они обязаны быть теми же")
                .contains("scope.dueRepeats()")
                .contains("scope.fresh()")
                .contains("scope.total()");
        // Мало объявить переменные — их надо ПЕЧАТАТЬ серверным выражением. Первая
        // редакция гейта проверяла только `th:with`, и подмена значения на
        // клиентский хук (`data-preview-repeats`) проходила мимо неё.
        assertThat(markup)
                .as("объём набора печатает сервер")
                .contains("class=\"outcome-value\" th:text=\"${inSet}\"");
        assertThat(markup)
                .as("долг по повторениям печатает сервер")
                .contains("class=\"outcome-fact-value\" th:text=\"${repeats}\"");
        assertThat(markup)
                .as("число неоткрытых печатает сервер")
                .contains("class=\"outcome-fact-value\" th:text=\"${fresh}\"");
        assertThat(markup)
                .as("карточка «Повторение» заменена, а не оставлена второй системой")
                .doesNotContain("builder-review");

        String shell = read("static/css/shell.css");
        assertThat(shell).doesNotContain("grid-area: review");
    }

    @Test
    void whatTheScriptComputesArrivesHiddenNotAsAPlaceholder() throws IOException {
        String markup = markup();
        int preview = markup.indexOf("id=\"session-preview\"");
        assertThat(preview).isNotNegative();
        String tag = markup.substring(preview, markup.indexOf('>', preview));
        assertThat(tag)
                .as("без скрипта строка сессии показывала бы «по числу вопросов» — это не число")
                .contains("hidden");
        assertThat(markup)
                .as("строка отбора живёт в блоке набора, а не второй копией в панели запуска")
                .containsOnlyOnce("data-preview-filters");
    }

    @Test
    void theLayoutKnowsTheSameFourZonesAsTheMarkup() throws IOException {
        String shell = read("static/css/shell.css");
        for (String zone : List.of("scope", "mode", "volume", "outcome", "today", "launch")) {
            assertThat(shell)
                    .as("зона раскладки без своего блока — это мёртвая область: " + zone)
                    .contains(zone);
        }
        assertThat(shell).contains("grid-area: volume;");
        assertThat(shell).contains("grid-area: outcome;");
    }
}
