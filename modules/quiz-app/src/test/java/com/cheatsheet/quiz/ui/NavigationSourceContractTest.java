package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.config.web.NavSection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Первый уровень навигации объявлен ровно один раз.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Разделы были записаны дважды вручную — в рельсе и в нижней полосе, — и списки разошлись.
 * Замер до починки: рельс шесть пунктов, полоса пять; не хватало каталога банка, то есть на
 * телефоне в раздел «Темы» нельзя было попасть НИ ОДНОЙ ссылкой. При этом {@code topics.html}
 * передавал в полосу {@code active='topics'} — значение, которого тот фрагмент не знал, и
 * подсветка молча не срабатывала. Расходились и глифы одного раздела: {@code #training} в
 * рельсе против {@code #home} в полосе.
 *
 * <p>Это уже второе расхождение той же пары списков (первое чинили в iter-88, когда каталог
 * добавляли в рельс). Причина не в невнимательности, а в том, что источников два.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>оба фрагмента читают {@code ${@nav.sections()}} и не содержат рукописных ссылок на
 *       адреса первого уровня;</li>
 *   <li>число пунктов не записано третьим местом — в CSS полосы нет литерала колонок;</li>
 *   <li>каждое значение {@code active}, которое страницы передают фрагментам, существует
 *       в {@link NavSection}.</li>
 * </ol>
 */
class NavigationSourceContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path SIDEBAR = TEMPLATES.resolve("fragments/sidebar.html");
    private static final Path MOBILE_NAV = TEMPLATES.resolve("fragments/mobile-nav.html");
    private static final Path SHELL_CSS = Path.of("src/main/resources/static/css/shell.css");

    private static final Pattern ACTIVE_ARGUMENT =
            Pattern.compile("(?:sidebar|mobile-nav)\\s*::\\s*(?:sidebar|mobile-nav)\\(active=([^)]*)\\)");
    private static final Pattern QUOTED = Pattern.compile("'([a-z-]+)'");

    @Test
    void bothNavigationsReadTheSameSource() {
        for (Path fragment : List.of(SIDEBAR, MOBILE_NAV)) {
            String source = read(fragment);
            assertThat(source)
                    .as("%s перестал читать единый источник первого уровня. Рукописный список "
                            + "разойдётся со вторым — так уже было: шесть пунктов против пяти, "
                            + "и каталог банка стал недостижим на телефоне", fragment.getFileName())
                    .contains("${@nav.sections()}");
        }
    }

    /**
     * Проверка идёт по СОДЕРЖИМОМУ {@code <nav>}, а не по всему файлу, и это не послабление.
     * Первая версия смотрела файл целиком и покраснела на ссылке бренда
     * ({@code <a class="brand" th:href="@{/}">}) в шапке рельса — та ведёт на тот же адрес,
     * что и первый пункт, но пунктом навигации не является и в общий список не входит.
     * Инвариант формулируется про список пунктов, значит и проверяться должен на нём.
     */
    @Test
    void navigationFragmentsDoNotHandWriteFirstLevelLinks() {
        List<String> offenders = new ArrayList<>();
        for (Path fragment : List.of(SIDEBAR, MOBILE_NAV)) {
            String source = navBlock(fragment);
            for (NavSection section : NavSection.all()) {
                String literal = "@{" + section.path() + "}";
                if (source.contains(literal)) {
                    offenders.add(fragment.getFileName() + " → " + literal);
                }
            }
        }
        assertThat(offenders)
                .as("в навигации снова появилась ссылка, написанная руками: она не участвует "
                        + "в общем списке и потому не попадёт во вторую навигацию — ровно так "
                        + "каталог банка и выпал из нижней полосы")
                .isEmpty();
    }

    /**
     * Проверяются ВСЕ правила {@code .mobile-nav}, а не первое найденное. Первая версия
     * брала первое совпадение и молча смотрела не туда: в shell.css раньше раскладки стоит
     * {@code .mobile-nav { display: none }} десктопной ветки. Гейт был зелёным с возвращённым
     * литералом {@code repeat(5, …)} — то есть не ловил ровно тот дефект, ради которого
     * написан. Поймано на проверке гейта укусом.
     */
    @Test
    void bottomBarColumnCountFollowsItemsNotALiteral() {
        String css = read(SHELL_CSS);
        Matcher rule = Pattern.compile("\\.mobile-nav\\s*\\{([^}]*)}").matcher(css);

        List<String> offenders = new ArrayList<>();
        int rules = 0;
        while (rule.find()) {
            rules++;
            String body = rule.group(1);
            if (Pattern.compile("repeat\\(\\s*\\d").matcher(body).find()) {
                offenders.add(body.replaceAll("\\s+", " ").trim());
            }
        }

        assertThat(rules).as("правило .mobile-nav не найдено в shell.css").isPositive();
        assertThat(offenders)
                .as("в полосе снова записано ЧИСЛО колонок. Это третье место, где живёт состав "
                        + "первого уровня: добавленный в NavSection раздел молча уедет во второй "
                        + "ряд полосы высотой 64px, то есть за нижнюю кромку экрана")
                .isEmpty();
    }

    @Test
    void everyActiveValuePagesPassExistsInTheSource() {
        Set<String> known = new LinkedHashSet<>();
        NavSection.all().forEach(section -> known.add(section.id()));
        // Настройки сняты с первого уровня (IA Wave 1): подсветка футерного
        // side-action на /settings передаёт active='settings'.
        known.add("settings");

        List<String> unknown = new ArrayList<>();
        for (Path template : templates()) {
            String source = read(template);
            Matcher call = ACTIVE_ARGUMENT.matcher(source);
            while (call.find()) {
                Matcher literal = QUOTED.matcher(call.group(1));
                while (literal.find()) {
                    String id = literal.group(1);
                    if (!known.contains(id)) {
                        unknown.add(TEMPLATES.relativize(template) + " → active='" + id + "'");
                    }
                }
            }
        }

        assertThat(unknown)
                .as("страница передаёт навигации значение active, которого в первом уровне нет. "
                        + "Подсветка текущего раздела молча не сработает — именно так вела себя "
                        + "страница каталога на телефоне. Известные значения: %s", known)
                .isEmpty();
    }

    /**
     * Первый уровень — РАЗДЕЛЫ, а не режимы одного и того же экрана.
     *
     * <p>Шестым пунктом здесь стояли «Ошибки» → {@code /review}. Разделом он не был:
     * {@code /review} и {@code /practice} рендерят один шаблон {@code focus-training.html}, а вся
     * разница между ними — {@code ReviewModeService}, который форсит один флаг фильтра
     * ({@code onlyWrong=true}); тот же отбор человек уже включал чипом «Ошибки» в
     * конструкторе сессии. Пункт при этом ничего не сообщал о главном — есть ли что
     * разбирать: он выглядел одинаково и при 123 ошибках, и при нуле.</p>
     *
     * <p>Проверяется пара утверждений, которые обязаны держаться вместе: режима нет в
     * первом уровне И вход в него есть внутри тренировки, с числом и с обратным выходом.
     * Порознь любое из них — это либо возврат пункта-режима, либо потерянный режим.</p>
     */
    @Test
    void firstLevelHoldsSectionsNotModesAndTheModeKeepsItsOwnEntry() {
        assertThat(NavSection.all().stream().map(NavSection::path))
                .as("режим тренировки снова объявлен разделом первого уровня. /review — это тот же "
                        + "экран с одним флагом фильтра, а не отдельное место в продукте")
                .doesNotContain("/review");

        // Эволюция (аудит «тихий интерфейс», 2026-09-02): вход в разбор ошибок
        // переехал с экрана вопроса в тихий статус оболочки. На самом вопросе он
        // был кнопкой в банде из шести чисел — уводил из работы и дублировал
        // /stats; теперь это строка сайдбара, видимая на всех экранах и несущая
        // то же число. Условие «есть что разбирать» осталось (пустого входа нет),
        // обратный выход с /review — прежний.
        String sidebar = read(TEMPLATES.resolve("fragments/sidebar.html"));
        String sidebarMarkup = sidebar.replaceAll("(?s)<!--.*?-->", "\n");

        assertThat(sidebarMarkup)
                .as("вход в разбор ошибок исчез из оболочки: режим убран из навигации и "
                        + "теперь недостижим ничем, кроме сохранённой ссылки")
                .contains("onlyWrong=true");
        assertThat(sidebarMarkup)
                .as("вход в разбор показывается всегда — в том числе когда разбирать нечего. "
                        + "Постоянная ссылка в пустое состояние «Пока нет ошибок» — приглашение в тупик")
                .contains("wrongCount() > 0");

        String focus = read(TEMPLATES.resolve("focus-training.html"));
        String markup = focus.replaceAll("(?s)<!--.*?-->", "\n");

        assertThat(markup)
                .as("на самом /review нет обратного выхода: клик по активному пункту навигации как "
                        + "способ выйти из режима — догадка, а не интерфейс")
                .containsPattern("(?s)reviewMode != null and reviewMode\\}\"\\s*\\n?\\s*th:href=\"@\\{/practice\\}");
    }

    /** Содержимое элемента {@code <nav>} фрагмента — только список пунктов. */
    private static String navBlock(Path fragment) {
        String source = read(fragment);
        Matcher nav = Pattern.compile("<nav[^>]*>(.*?)</nav>", Pattern.DOTALL).matcher(source);
        assertThat(nav.find())
                .as("во фрагменте %s нет элемента <nav> — навигация перестала быть навигацией",
                        fragment.getFileName())
                .isTrue();
        return nav.group(1);
    }

    private static List<Path> templates() {
        try (Stream<Path> paths = Files.walk(TEMPLATES)) {
            return paths.filter(p -> p.toString().endsWith(".html")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
