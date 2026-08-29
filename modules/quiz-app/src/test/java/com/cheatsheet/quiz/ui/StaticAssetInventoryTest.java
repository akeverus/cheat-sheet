package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт статических графических ассетов после снятия пиксельного слоя:
 * каталога {@code static/img} больше нет, вся графика продукта — векторные
 * символы двух спрайтов ({@code fragments/icons.html},
 * {@code fragments/state-illustrations.html}) плюс {@code favicon.svg}.
 *
 * <p>Мотив прежний — не гигиена ради гигиены. Мёртвый или оборванный ассет не
 * падает в рантайме и не виден в UI: иконка раздела в {@code topic-groups.yaml}
 * декоративна, и опечатка в id символа деградирует молча — {@code <use>} просто
 * рисует пустоту. Обнаруживается такое только замером; этот тест и есть замер.
 *
 * <p>Прежний двусторонний обход «ассет без ссылок / ссылка без ассета» по
 * {@code static/img} снят вместе с самим каталогом: его наследники — проверка
 * «ссылок /img/ не осталось» здесь и стражи живости символов спрайтов
 * ({@code StateIllustrationContractTest#spriteHasNoDeadGlyphs}).
 */
class StaticAssetInventoryTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path STATIC = RESOURCES.resolve("static");

    /**
     * Где ищутся ссылки на графику. Кроме шаблонов, стилей, скриптов и Java сюда
     * входит {@code topic-groups.yaml}: знаки разделов заданы в конфиге рядом с
     * заголовками, и это такая же настоящая ссылка, как {@code url()} в CSS.
     */
    private static final List<String> REFERENCE_ROOTS = List.of(
            "src/main/resources/templates",
            "src/main/resources/static/css",
            "src/main/resources/static/js",
            "src/main/resources/topic-groups.yaml",
            "src/main/java"
    );

    /**
     * Каждая иконка раздела из {@code topic-groups.yaml} существует как
     * {@code <symbol id="...">} в спрайте {@code fragments/icons.html}.
     * Поле {@code icon} — id символа спрайта, а не путь к файлу; отсутствующий
     * символ не даёт ни 404, ни лога — карточка каталога молча теряет знак.
     */
    @Test
    void everyTopicGroupIconExistsInTheIconSprite() throws IOException {
        String yaml = Files.readString(RESOURCES.resolve("topic-groups.yaml"), StandardCharsets.UTF_8);
        String sprite = Files.readString(
                RESOURCES.resolve("templates/fragments/icons.html"), StandardCharsets.UTF_8);

        Matcher matcher = Pattern.compile("(?m)^\\s*icon:\\s*(\\S+)\\s*$").matcher(yaml);
        Set<String> missing = new LinkedHashSet<>();
        int declared = 0;
        while (matcher.find()) {
            declared++;
            if (!sprite.contains("<symbol id=\"" + matcher.group(1) + "\"")) {
                missing.add(matcher.group(1));
            }
        }

        assertThat(declared).as("объявленные знаки разделов").isPositive();
        assertThat(missing)
                .as("знак раздела объявлен в topic-groups.yaml, но символа нет в спрайте — "
                        + "`<use>` нарисует пустоту, и карточка каталога молча останется без знака")
                .isEmpty();
    }

    /**
     * В {@code static/} нет ни одного растрового файла. Растр не наследует тему
     * через {@code currentColor} и заводит второй графический язык рядом со
     * спрайтами — ровно тот, который снят вместе с {@code static/img}.
     */
    @Test
    void noRasterFilesRemainInStatic() throws IOException {
        List<String> rasters = new ArrayList<>();
        for (Path file : listFiles(STATIC)) {
            String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".webp")) {
                rasters.add(STATIC.relativize(file).toString());
            }
        }

        assertThat(rasters)
                .as("растровые файлы в static/ — возврат снятого графического языка; "
                        + "вся графика продукта векторная: спрайты и favicon.svg")
                .isEmpty();
    }

    /**
     * Ссылок на удалённый каталог {@code /img/} не осталось. Такая ссылка — это
     * гарантированный 404 и пустое место в интерфейсе — молча, потому что
     * фон-картинка не ломает разметку.
     */
    @Test
    void noReferenceToTheRemovedImageCatalogRemains() throws IOException {
        Pattern imageReference = Pattern.compile("/img/([A-Za-z0-9/_-]+\\.(?:png|svg|gif|webp|jpg|jpeg))");
        Matcher matcher = imageReference.matcher(readReferenceCorpus());

        Set<String> dead = new LinkedHashSet<>();
        while (matcher.find()) {
            dead.add(matcher.group(0));
        }

        assertThat(dead)
                .as("ссылки на удалённый каталог static/img — каждая из них 404")
                .isEmpty();
    }

    private String readReferenceCorpus() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String root : REFERENCE_ROOTS) {
            for (Path file : listFiles(Path.of(root))) {
                sb.append(Files.readString(file, StandardCharsets.UTF_8)).append('\n');
            }
        }
        return sb.toString();
    }

    /** Каталог обходится рекурсивно, одиночный файл возвращается как есть. */
    private List<Path> listFiles(Path root) throws IOException {
        if (!Files.exists(root)) {
            return List.of();
        }
        if (Files.isRegularFile(root)) {
            return List.of(root);
        }
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile).sorted().toList();
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }
    }
}
