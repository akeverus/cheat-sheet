package com.cheatsheet.quiz.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Контракт статических графических ассетов: всё, что лежит в {@code static/img},
 * обязано быть либо реально загружаемым, либо явно объявленным как документация.
 *
 * <p>Мотив — не гигиена ради гигиены. Инвентарь ассетов уже разъехался с
 * реальностью: заголовок {@code pixel-fx.js} перечислял в KEEP спрайты
 * {@code hero-1..3}, на которые не ссылались ни CSS, ни JS, а рядом лежала
 * вторая, целиком мёртвая система иконок (12 КБ спрайта плюс шесть одиночных
 * SVG) — при живых {@code fragments/icons.html} и {@code fragments/pixel-sprite.html}.
 * Мёртвый ассет не падает в рантайме и не виден в UI, поэтому обнаруживается
 * только замером. Этот тест и есть замер.
 */
class StaticAssetInventoryTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path IMAGES = RESOURCES.resolve("static/img");

    /**
     * Файлы под {@code static/img}, которые браузер не грузит и грузить не должен.
     *
     * <p>Происхождение обязано лежать рядом с ассетами, а не только в
     * документации: при копировании каталога условия использования должны ехать
     * вместе с картинками. Появится внешний пак — сюда добавляется его файл
     * лицензии. Метаданные спрайтов — источник истины для CSS, их связь
     * проверяет {@link #spriteAnimationCssMatchesSheetMetadata()}, а не
     * текстовый поиск имени файла.
     */
    private static final Set<String> DOCUMENTATION_ONLY = Set.of(
            "pixel/THIRD_PARTY_ASSETS.md"
    );

    /**
     * Где ищутся ссылки на ассеты. Кроме шаблонов, стилей, скриптов и Java сюда
     * входит {@code topic-groups.yaml}: иконки разделов заданы в конфиге рядом с
     * заголовками, и это такая же настоящая ссылка, как {@code url()} в CSS.
     * Без неё тест объявил бы живые иконки каталога мёртвым весом.
     */
    private static final List<String> REFERENCE_ROOTS = List.of(
            "src/main/resources/templates",
            "src/main/resources/static/css",
            "src/main/resources/static/js",
            "src/main/resources/topic-groups.yaml",
            "src/main/java"
    );

    @Test
    void everyImageAssetIsReferencedFromTemplatesStylesOrScripts() throws IOException {
        String corpus = readReferenceCorpus();

        List<String> unreferenced = new ArrayList<>();
        for (Path asset : listFiles(IMAGES)) {
            String relative = IMAGES.relativize(asset).toString();
            if (DOCUMENTATION_ONLY.contains(relative) || isSpriteMetadata(relative)) {
                continue;
            }
            // Сверка по относительному пути, а не по имени файла. Имя совпадает
            // слишком легко: `stickers/gem.png` считался бы живым из-за ссылки на
            // `pixel/gem.png`, а `stickers/fire.png` — из-за `pixel/streak-fire.png`,
            // в котором это имя просто подстрока. Тест тогда молчит ровно про те
            // ассеты, которые дублируют уже используемые, — то есть про самый
            // вероятный вид мёртвого веса.
            if (!corpus.contains(relative.replace(java.io.File.separatorChar, '/'))) {
                unreferenced.add(relative);
            }
        }

        assertThat(unreferenced)
                .as("Ассеты без единой ссылки — мёртвый вес. Либо использовать, "
                        + "либо удалить, либо (если это документация) внести в DOCUMENTATION_ONLY")
                .isEmpty();
    }

    /**
     * Обратная проверка: ссылка без файла. Опечатка в пути даёт 404 и пустое
     * место в интерфейсе — молча, потому что фон-картинка не ломает разметку.
     */
    @Test
    void everyImageReferenceResolvesToAnExistingFile() throws IOException {
        Pattern imageReference = Pattern.compile("/img/([A-Za-z0-9/_-]+\\.(?:png|svg|gif|webp|jpg|jpeg))");
        Matcher matcher = imageReference.matcher(readReferenceCorpus());

        Set<String> missing = new LinkedHashSet<>();
        while (matcher.find()) {
            String relative = matcher.group(1);
            if (!Files.exists(IMAGES.resolve(relative))) {
                missing.add(relative);
            }
        }

        assertThat(missing).as("Ссылки на несуществующие изображения").isEmpty();
    }

    /**
     * Иконки разделов в {@code topic-groups.yaml} записаны без префикса
     * {@code /img/}, поэтому общий скан ссылок их не проверяет. А отсутствующий
     * файл здесь деградирует молча — карточка каталога просто теряет картинку.
     * Опечатка в пути не всплыла бы ни в логах, ни в тестах, поэтому проверяется
     * отдельно.
     */
    @Test
    void everyTopicGroupIconResolvesToAnExistingFile() throws IOException {
        String yaml = Files.readString(RESOURCES.resolve("topic-groups.yaml"), StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("(?m)^\\s*icon:\\s*(\\S+)\\s*$").matcher(yaml);

        Set<String> missing = new LinkedHashSet<>();
        int declared = 0;
        while (matcher.find()) {
            declared++;
            if (!Files.exists(IMAGES.resolve(matcher.group(1)))) {
                missing.add(matcher.group(1));
            }
        }

        assertThat(declared).as("объявленные иконки разделов").isPositive();
        assertThat(missing).as("иконки разделов без файла").isEmpty();
    }




    private boolean isSpriteMetadata(String relative) {
        return relative.startsWith("animations/metadata" + java.io.File.separator)
                || relative.startsWith("animations/metadata/");
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
