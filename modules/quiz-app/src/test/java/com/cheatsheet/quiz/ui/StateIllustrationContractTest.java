package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Пустые состояния различаются с одного взгляда.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Один глиф {@code px-empty-chart} стоял на ПЯТИ разных состояниях: «Пока нет данных по темам»,
 * «Пока нет динамики», «Пока нет ошибок» (аналитика), «Каталог пуст» и «Ничего не нашлось»
 * (каталог тем). То есть «данных ещё нет», «отбор ничего не дал», «класть было нечего» и
 * «ошибок нет» — четыре разных сообщения — выглядели одинаково. Сам принцип в проекте уже был
 * записан рядом с закладками (сундук против подзорной трубы), но за пределами закладок не
 * соблюдался.
 *
 * <p>Дефект не ловится ни одним прежним гейтом: инвентарь ассетов видит, что файл используется,
 * а не что он используется НЕ ТАМ; вёрстка валидна; контраст в норме.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>мёртвых глифов в спрайте состояний нет — объявленный символ обязан быть использован;</li>
 *   <li>каждая ситуация несёт свой глиф: таблица пар «заголовок состояния → символ» проверяется
 *       по разметке, поэтому переназначение глифа в шаблоне валит тест.</li>
 * </ol>
 */
class StateIllustrationContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path SPRITE = TEMPLATES.resolve("fragments/state-illustrations.html");
    private static final Path STATIC = Path.of("src/main/resources/static");

    /** Ситуация (заголовок пустого состояния) → глиф, который её изображает. */
    private static final Map<String, String> SITUATION_GLYPH = new LinkedHashMap<>();

    static {
        SITUATION_GLYPH.put("Каталог пуст", "px-shelf");
        SITUATION_GLYPH.put("Ничего не нашлось", "px-spyglass");
        SITUATION_GLYPH.put("Пока нет закладок", "px-chest");
        SITUATION_GLYPH.put("Под отбор ничего не подошло", "px-spyglass");
        SITUATION_GLYPH.put("Под отбор не попала ни одна тема", "px-spyglass");
        SITUATION_GLYPH.put("Пока нет данных по темам", "px-chronicle");
        SITUATION_GLYPH.put("Пока нет динамики", "px-chronicle");
        SITUATION_GLYPH.put("Пока нет ошибок", "px-clean-slate");
        SITUATION_GLYPH.put("Сейчас нет вопросов", "px-portcullis");
    }

    @Test
    void spriteHasNoDeadGlyphs() {
        Set<String> declared = new LinkedHashSet<>();
        Matcher symbols = Pattern.compile("<symbol id=\"(px-[\\w-]+)\"").matcher(read(SPRITE));
        while (symbols.find()) {
            declared.add(symbols.group(1));
        }
        assertThat(declared).as("спрайт состояний пуст").isNotEmpty();

        String consumers = consumers();
        List<String> dead = declared.stream().filter(id -> !consumers.contains("#" + id)).toList();

        assertThat(dead)
                .as("глиф объявлен, но никем не показан. Мёртвая иллюстрация — это обещание "
                        + "состояния, которого в продукте нет: так в спрайте лежали px-sync и "
                        + "px-revision-conflict, пока их не сняли")
                .isEmpty();
    }

    @Test
    void everySituationKeepsItsOwnGlyph() {
        List<String> wrong = new ArrayList<>();
        String markup = templatesMarkup();

        SITUATION_GLYPH.forEach((title, glyph) -> {
            int at = markup.indexOf(title);
            if (at < 0) {
                wrong.add("состояние «" + title + "» пропало из разметки");
                return;
            }
            // Глиф печатается ПЕРЕД заголовком внутри того же блока состояния.
            int from = Math.max(0, at - 800);
            String before = markup.substring(from, at);
            int lastUse = before.lastIndexOf("href=\"#px-");
            if (lastUse < 0) {
                wrong.add("у состояния «" + title + "» не осталось иллюстрации");
                return;
            }
            String actual = before.substring(lastUse + 7, before.indexOf('"', lastUse + 7));
            if (!glyph.equals(actual)) {
                wrong.add("«" + title + "»: ожидался " + glyph + ", стоит " + actual);
            }
        });

        assertThat(wrong)
                .as("ситуация потеряла собственный глиф. Один рисунок на несколько разных "
                        + "сообщений — это и есть дефект, ради которого гейт написан: «данных ещё "
                        + "нет», «отбор ничего не дал», «класть было нечего» и «ошибок нет» обязаны "
                        + "отличаться с одного взгляда")
                .isEmpty();
    }

    private static String consumers() {
        StringBuilder sb = new StringBuilder();
        for (Path root : List.of(TEMPLATES, STATIC)) {
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> !p.equals(SPRITE))
                        .filter(p -> p.toString().endsWith(".html")
                                || p.toString().endsWith(".css")
                                || p.toString().endsWith(".js"))
                        .forEach(p -> sb.append(read(p)));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return sb.toString();
    }

    /**
     * Разметка БЕЗ комментариев.
     *
     * <p>Первая версия сверяла сырой файл и покраснела на «Пока нет закладок»: первое вхождение
     * этой строки в bookmarks.html — внутри пояснения о том, что состояний два. Тот же класс
     * ловушки уже описан в {@code TemplateFragmentContractTest} («гвард сторожил собственный
     * некролог»): проверять надо разметку, а не рассказ о ней.
     */
    private static String templatesMarkup() {
        StringBuilder sb = new StringBuilder();
        try (Stream<Path> paths = Files.walk(TEMPLATES)) {
            paths.filter(p -> p.toString().endsWith(".html")).sorted()
                    .forEach(p -> sb.append(read(p).replaceAll("(?s)<!--.*?-->", "")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sb.toString();
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
