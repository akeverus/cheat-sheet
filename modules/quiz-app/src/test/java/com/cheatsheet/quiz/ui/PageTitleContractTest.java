package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Title экрана вопроса несёт позицию цикла, а вне сессии — сам вопрос;
 * title результата начинается с вердикта.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Цикл «ответ → следующий вопрос» — это MPA-навигации, и скринридер объявляет на
 * каждую ровно {@code <title>}. Пока он был статичен, смена вопроса была НЕСЛЫШНА:
 * замер iter-168 — шесть навигаций цикла, шесть одинаковых объявлений «Подготовка
 * к собеседованию»; вкладки двух тренировок неразличимы. Смежный замер: фокус
 * после /flashcard-grade и после «Следующий вопрос» приземляется на BODY — и это
 * ОСОЗНАННО оставлено так (новая страница цикла, где значимо начало; хоткеи
 * Space/1–9 документ-уровневые и работают с BODY — проверено в обоих движках),
 * поэтому title — единственный немедленный сигнал контекста для SR.
 *
 * <h2>Что закреплено (замер после, Chromium и WebKit)</h2>
 * <ol>
 *   <li>в сессии: «Вопрос N из M — режим — …», N сдвигается после оценки
 *       (1→2 из 50, Флешкарты);</li>
 *   <li>TRAINING без interviewSession: title несёт сам вопрос (обрезка 80,
 *       бэктики срезаны) — переход озвучивает новый вопрос;</li>
 *   <li>/answer: вердикт ПЕРВЫМ словом («Неверно — Результат — …») — SR слышит
 *       исход раньше, чем дойдёт до баннера (тот же принцип, что autofocus
 *       FRD-219).</li>
 * </ol>
 */
class PageTitleContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void theSessionTitleCarriesThePosition() throws IOException {
        String head = read("templates/focus-training.html");
        assertThat(head)
                .as("позиция сессии выпала из title: смена вопроса снова неслышна для SR")
                .contains("'Вопрос ' + (${interviewSession.getIndex() + 1}) + ' из ' "
                        + "+ ${interviewSession.getTotal()}");
        assertThat(head)
                .as("режим в title — из той же модели, что chip; elvis защищает от 'null'")
                .contains("${focusModeChipText != null} ? ${focusModeChipText}");
    }

    @Test
    void theTrainingTitleCarriesTheQuestionItself() throws IOException {
        assertThat(read("templates/focus-training.html"))
                .as("TRAINING живёт без interviewSession — без вопроса в title его "
                        + "переходы снова объявляют одно и то же")
                .contains("#strings.abbreviate(#strings.replace(current.question.questionText,'`',''),80)");
    }

    @Test
    void theResultTitleLeadsWithTheVerdict() throws IOException {
        String head = read("templates/result.html");
        assertThat(head)
                .as("вердикт обязан стоять ПЕРВЫМ словом title — SR слышит исход немедленно")
                .contains("'Верно — Результат — Подготовка к собеседованию'")
                .contains("'Неверно — Результат — Подготовка к собеседованию'")
                .contains("${result != null and result.correctAnswer}");
    }

    /**
     * Autofocus в шаблоне ровно один — на раскрытом эталоне (FRD-219). Второй
     * autofocus — невалидный HTML с непредсказуемым победителем; в частности,
     * его НЕТ на самом вопросе: загрузка / — не результат действия, и захват
     * фокуса на каждом заходе ломал бы skip-link и чтение шапки.
     */
    @Test
    void theFocusTemplateHasExactlyOneAutofocus() throws IOException {
        // Комментарии срезаются ЦЕЛИКОМ: слово в середине многострочного
        // <!--/* … */--> не начинается с «<!--», и построчный фильтр первой
        // редакции считал его атрибутом (пойман на чистом коде: 2 вместо 1).
        String template = read("templates/focus-training.html")
                .replaceAll("(?s)<!--.*?-->", "");
        long count = java.util.regex.Pattern.compile("\\bautofocus\\b")
                .matcher(template).results().count();
        assertThat(count)
                .as("autofocus обязан быть ровно один (раскрытый эталон): ноль — потеряно "
                        + "приземление FRD-219, два — невалидный HTML и гонка")
                .isEqualTo(1);
    }
}
