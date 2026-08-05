package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт палитры пиксельного слоя со стороны CSS — двойник
 * {@link PixelSpriteContractTest}, который стережёт ту же палитру со стороны PNG.
 *
 * <p>Нужен потому, что гейт спрайтов вылечил ровно половину проблемы и тут же
 * показал вторую. Нормализатор снял с 36 файлов дрейфующий {@code #0a1a20} —
 * а в {@code pixel-*.css} тот же оттенок остался написанным руками и в светлой
 * теме клал почти чёрную грань поверх светлого фона. Тем же способом жили
 * шестнадцать вхождений {@code #1a4a48}, у которого имя {@code --pixel-teal-deep}
 * появилось в этой же итерации. Набор ассетов вылечен, копия в CSS — нет: это и
 * есть вторая палитра, только в другом файле.
 *
 * <p>Инвариант, который здесь механизируется, ровно один:
 *
 * <blockquote>HEX в {@code pixel-*.css} допустим ТОЛЬКО как акт именования —
 * то есть как всё значение объявления {@code --имя: #hex;}. Везде, где цвет
 * <b>используется</b> (обычное свойство либо составное значение вроде списка
 * теней), он обязан идти через {@code var()}.</blockquote>
 *
 * <p>Формулировка выбрана так, чтобы не запретить сам слой имён {@code --px-*}:
 * он законен и нужен. Незаконно — рассыпать по правилам литералы, каждый из
 * которых молча расходится с продуктовой рампой.
 */
class PixelCssPaletteTest {

    private static final Path CSS = Path.of("src/main/resources/static/css");
    private static final Path TOKENS = CSS.resolve("tokens.css");

    /**
     * Пиксельные чернила, СОЗНАТЕЛЬНО замороженные на тёмном значении токена.
     *
     * <p>Их нельзя заменить на {@code var(--color-*)}: пиксельные поверхности —
     * это фиксированный растр (жёлтый {@code tab-*.png}) либо фиксированная
     * заливка, одинаковые в обеих темах. Токен в светлой ветке перевернулся бы,
     * растр — нет; {@code pixel-buttons.css} хранит замер прошлой такой попытки:
     * белый по жёлтому дал 1.17:1.
     *
     * <p>Но «заморожено» не значит «оторвано». Пока значения совпадают, сдвиг
     * акцента в {@code tokens.css} обязан отражаться и здесь — иначе пиксельный
     * слой останется на палитре, которой в продукте больше нет. Тест ловит
     * расхождение по ИМЕНАМ: HEX не продублирован ни разу, обе стороны читаются
     * из файлов.
     */
    private static final Map<String, String> PINNED_TO_DARK_TOKEN = Map.of(
            "--px-teal", "--color-accent-primary",
            "--px-green", "--color-status-success",
            "--px-amber", "--color-accent-secondary",
            "--px-red", "--color-status-error",
            "--px-ink", "--color-accent-on"
    );

    /**
     * Главный контракт: цвет либо именуется, либо берётся по имени.
     *
     * <p>Проверяется не «нет HEX вообще» — так нельзя, палитре нужно место, где
     * значения всё-таки записаны. Проверяется позиция: строка вида
     * {@code --name: #hex;} — объявление роли, и она разрешена; {@code #hex}
     * внутри {@code box-shadow}, {@code color} или составного значения —
     * потребление, и оно запрещено.
     */
    @Test
    void hexLiteralsAppearOnlyWhereAColorIsNamed() throws IOException {
        Pattern namingAct = Pattern.compile("^\\s*--[a-z0-9-]+:\\s*#[0-9a-fA-F]{3,8}\\s*;?\\s*$");
        List<String> consumed = new ArrayList<>();

        for (Path file : pixelStylesheets()) {
            String[] lines = rules(file).split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (!line.matches("(?s).*#[0-9a-fA-F]{3,8}\\b.*") || namingAct.matcher(line).matches()) {
                    continue;
                }
                consumed.add(file.getFileName() + ":" + (i + 1) + ": " + line.trim());
            }
        }

        assertThat(consumed)
                .as("HEX в месте потребления цвета. Если роль уже названа в tokens.css — "
                        + "взять через var(); если роль правда новая — объявить её отдельной "
                        + "строкой --имя: #hex и потреблять по имени")
                .isEmpty();
    }

    /** Замороженные чернила обязаны совпадать с тёмным значением своего токена — см. {@link #PINNED_TO_DARK_TOKEN}. */
    @Test
    void pinnedPixelInksStillMatchTheirSemanticToken() throws IOException {
        String tokens = rules(TOKENS);
        String pixel = rules(CSS.resolve("pixel-fx.css"));
        List<String> diverged = new ArrayList<>();

        PINNED_TO_DARK_TOKEN.forEach((px, token) -> {
            String pinned = firstValue(pixel, px);
            String dark = firstValue(tokens, token);
            if (!pinned.equals(dark)) {
                diverged.add(px + " = " + pinned + ", а " + token + " (тёмная ветка) = " + dark);
            }
        });

        assertThat(diverged)
                .as("пиксельный слой отстал от продуктовой рампы: значения разошлись. "
                        + "Либо подтянуть --px-*, либо — если расхождение намеренное — "
                        + "убрать пару из PINNED_TO_DARK_TOKEN с объяснением почему")
                .isEmpty();
    }

    /**
     * Обратная сторона: объявленное {@code --px-*} обязано кем-то потребляться.
     *
     * <p>Тот же довод, что у мёртвой {@code --pixel-*} роли в гейте спрайтов.
     * Имя без потребителя читается как существующая роль, и следующий автор
     * заводит под неё цвет вместо того, чтобы взять готовый.
     */
    @Test
    void everyPixelTokenIsConsumedSomewhere() throws IOException {
        StringBuilder corpus = new StringBuilder();
        for (Path file : pixelStylesheets()) {
            corpus.append(rules(file));
        }
        String all = corpus.toString();

        Matcher declared = Pattern.compile("(--px-[a-z0-9-]+):").matcher(all);
        List<String> dead = new ArrayList<>();
        while (declared.find()) {
            String name = declared.group(1);
            if (!all.contains("var(" + name + ")") && !dead.contains(name)) {
                dead.add(name);
            }
        }

        assertThat(dead).as("объявленные --px-*, которых никто не берёт через var()").isEmpty();
    }

    /**
     * Один селектор — один хозяин: рамку элемента задаёт ровно один файл семейства.
     *
     * <p>Пойман на живом примере. Рамка MCQ-опций была объявлена дважды —
     * в {@code pixel-options.css} (свой concern) и дословной копией в
     * {@code pixel-animations.css}. Копия грузится позже, поэтому побеждала она:
     * правка оригинала не давала на странице ВООБЩЕ НИЧЕГО, и это выглядело как
     * «CSS не подхватился», а не как вторая декларация. Так распил Волны 7
     * оставил один и тот же селектор в двух файлах.
     *
     * <p>Проверяется только {@code box-shadow} — в этом слое именно он рисует
     * контур, то есть несёт вес компонента. Состояния ({@code :hover},
     * {@code .option-correct}) — это ДРУГИЕ селекторы, их никто не запрещает.
     */
    @Test
    void noSelectorGetsItsBorderFromTwoFilesAtOnce() throws IOException {
        Pattern keyframes = Pattern.compile("(?s)@keyframes[^{]*\\{(?:[^{}]*\\{[^{}]*\\})*[^{}]*\\}");
        Pattern rule = Pattern.compile("([^{}@]+)\\{([^{}]*)\\}");
        Map<String, List<String>> owners = new LinkedHashMap<>();

        for (Path file : pixelStylesheets()) {
            Matcher matcher = rule.matcher(keyframes.matcher(rules(file)).replaceAll(""));
            while (matcher.find()) {
                String selector = matcher.group(1).trim().replaceAll("\\s+", " ");
                if (selector.isEmpty() || !matcher.group(2).contains("box-shadow")) {
                    continue;
                }
                owners.computeIfAbsent(selector, k -> new ArrayList<>())
                        .add(file.getFileName().toString());
            }
        }

        List<String> shared = owners.entrySet().stream()
                .filter(e -> e.getValue().stream().distinct().count() > 1)
                .map(e -> e.getValue() + " → " + e.getKey())
                .toList();

        assertThat(shared)
                .as("один и тот же селектор получает box-shadow из нескольких файлов: "
                        + "победит тот, что грузится последним, и правка остальных будет "
                        + "молча уходить в никуда. Оставить объявление в concern-файле "
                        + "компонента, остальные удалить")
                .isEmpty();
    }

    private List<Path> pixelStylesheets() throws IOException {
        try (Stream<Path> files = Files.list(CSS)) {
            List<Path> sheets = files
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).startsWith("pixel-"))
                    .filter(p -> p.getFileName().toString().endsWith(".css"))
                    .sorted()
                    .toList();
            assertThat(sheets).as("семейство pixel-*.css").isNotEmpty();
            return sheets;
        }
    }

    /**
     * Только правила: комментарии вырезаны.
     *
     * <p>Файлы этого семейства объясняют прозой, ПОЧЕМУ выбран тот или иной
     * оттенок, и приводят замеры контраста с конкретными HEX. Без вырезания
     * комментариев тест краснел бы ровно на объяснениях того, что он стережёт.
     */
    private String rules(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8).replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /** Первое объявление токена в файле — то есть тёмная ветка: она в tokens.css идёт первой. */
    private String firstValue(String css, String name) {
        Matcher matcher = Pattern.compile(Pattern.quote(name) + ":\\s*(#[0-9a-fA-F]{3,8})\\b").matcher(css);
        assertThat(matcher.find()).as("объявление %s", name).isTrue();
        return matcher.group(1).toLowerCase(Locale.ROOT);
    }
}
