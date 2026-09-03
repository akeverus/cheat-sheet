package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * У чисел один тихий дом — оболочка, а не рабочий экран.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер экрана вопроса (1440×900, живые данные) до правки: над самим вопросом
 * стояла банда из шести чисел — «Пройдено по банку 241/9818», «Выучено 15»,
 * «Точность первой попытки 22%», стрик, «204 к повтору» и кнопка «Разобрать
 * ошибки 225». Все шесть — аналитика, дублирующая /stats, и все шесть звучали
 * громче вопроса: 15 информационных голосов на экране, из которых к делу
 * относились четыре (вопрос, код, варианты, кнопка проверки).
 *
 * <p>Разведка по шаблонам подтвердила системность: очередь повторений
 * печаталась в ПЯТИ местах продукта, вход «Разобрать ошибки» — на трёх экранах,
 * знаменатель банка — в четырёх.</p>
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>статус живёт в оболочке между навигацией и футером — виден отовсюду,
 *       не спорит с работой;</li>
 *   <li>это ДЕЙСТВИЯ (ссылки в тренажёр и в разбор), а не витрина чисел;</li>
 *   <li>пустых входов нет: строка появляется, только когда есть что показать;</li>
 *   <li>стрика здесь нет — второй носитель {@code data-streak-bar} на странице
 *       запрещён (см. {@code BuilderRailContractTest}), его дом — конструктор;</li>
 *   <li>числа приходят от сервера ({@code SidebarStatusAdvice}), а не считаются
 *       скриптом в браузере.</li>
 * </ol>
 */
class SidebarStatusContractTest {

    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String sidebar() throws IOException {
        return HTML_COMMENT.matcher(read("templates/fragments/sidebar.html")).replaceAll("");
    }

    @Test
    void theStatusLivesInTheShellBetweenNavigationAndFooter() throws IOException {
        String markup = sidebar();

        int nav = markup.indexOf("</nav>");
        int status = markup.indexOf("class=\"sidebar-status\"");
        int footer = markup.indexOf("class=\"sidebar-footer\"");

        assertThat(status).as("тихого статуса нет в оболочке — числа вернутся на рабочий экран")
                .isNotNegative();
        assertThat(status)
                .as("статус залез ВНУТРЬ навигации: в списке ориентиров скринридера он "
                        + "прочитается пунктом меню, которым не является")
                .isGreaterThan(nav);
        assertThat(status)
                .as("статус встал после футера — глобальные действия оболочки обязаны "
                        + "оставаться последними")
                .isLessThan(footer);
    }

    @Test
    void everyRowIsAnActionAndNeverAnEmptyOne() throws IOException {
        String markup = sidebar();

        assertThat(markup)
                .as("строки статуса перестали быть ссылками — витрина чисел в оболочке "
                        + "ничего не даёт сделать")
                .containsPattern("<a class=\"sidebar-status-row\"[\\s\\S]{0,400}?th:href=\"@\\{/}\"")
                .containsPattern("<a class=\"sidebar-status-row\"[\\s\\S]{0,400}?onlyWrong=true");
        assertThat(markup)
                .as("строка очереди показывается при нуле — ссылка в пустое состояние "
                        + "это приглашение в тупик")
                .contains("sidebarStatus.dueRepeats() > 0");
        assertThat(markup)
                .as("строка ошибок показывается при нуле")
                .contains("sidebarStatus.wrongCount() > 0");
        assertThat(markup)
                .as("блок рендерится даже когда показывать нечего — в оболочке останется "
                        + "пустая коробка")
                .contains("sidebarStatus != null and sidebarStatus.present()");
    }

    @Test
    void theStreakDoesNotMoveIntoTheShell() throws IOException {
        assertThat(sidebar())
                .as("стрик переехал в оболочку — на /train появится ВТОРОЙ носитель "
                        + "data-streak-bar, и одна серия напечатается дважды")
                .doesNotContain("data-streak-bar");
    }

    @Test
    void theNumbersComeFromTheServerAndOnlyForPageControllers() throws IOException {
        String advice = new String(Files.readAllBytes(Path.of(
                "src/main/java/com/cheatsheet/quiz/config/web/SidebarStatusAdvice.java")),
                StandardCharsets.UTF_8);

        assertThat(advice)
                .as("advice раздаёт числа всем контроллерам подряд — REST-вызовы начнут "
                        + "платить два COUNT-запроса за модель, которую не рендерят")
                .contains("@ControllerAdvice(assignableTypes = InterviewMvcController.class)");
        assertThat(advice)
                .as("модель отдаёт не тот атрибут, который читает фрагмент оболочки")
                .contains("@ModelAttribute(\"sidebarStatus\")");
        assertThat(advice)
                .as("статус считает собственные числа вместо готовых агрегатов — это "
                        + "третий источник правды о той же очереди")
                .contains("interviewFacade.getStats(")
                .contains("countAllFiltered(null, null, true)");
    }
}
