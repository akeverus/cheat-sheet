package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
 * Контракт кадровых спрайт-анимаций ({@code static/img/animations}).
 *
 * <p>Мотив — конкретный замер. Набор состоял из 21 листа при том, что атрибут
 * {@code data-sprite-anim} код выставляет ровно для одного имени
 * ({@code incorrect-subtle} в {@code pixel-fx.js}). Остальные двадцать были
 * мертвы на уровень выше, чем умеет заметить
 * {@link StaticAssetInventoryTest}: CSS-правило ссылалось на файл через
 * {@code url()}, поэтому «ссылка есть» — а селектор правила не совпадал ни с
 * одним элементом за всё время жизни приложения. Инвентарь ассетов такое
 * пропускает по устройству: он сверяет файл со ссылкой, а не ссылку с
 * активацией.
 *
 * <p>Источник истины для листа — {@code scripts/gen-sprite-sheets.py}; PNG и
 * метаданные производны.
 */
class SpriteAnimationContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path SHEETS = RESOURCES.resolve("static/img/animations/sheets");
    private static final Path SPRITE_CSS = RESOURCES.resolve("static/css/pixel/sprite-anim.css");
    private static final Path TOKENS = RESOURCES.resolve("static/css/tokens.css");

    /** Где код может включить анимацию: выставить атрибут в JS или проставить его в разметке. */
    private static final List<String> ACTIVATION_ROOTS = List.of(
            "src/main/resources/static/js",
            "src/main/resources/templates",
            "src/main/java"
    );

    /**
     * Две роли листа, заданные <b>именами токенов</b>, а не копией HEX. Копия уже
     * подводила: лист жил на почти-совпадающей палитре — плитка {@code #10242d}
     * против токенной {@code #071116}, красный {@code #fb5f5f} против
     * {@code #ff715f}. Для этого спрайта расхождение заметнее, чем для любого
     * другого ассета: он всплывает поверх экрана вопроса в момент ответа, то
     * есть гарантированно попадает в один кадр с интерфейсом.
     */
    private static final List<String> RAMP_TOKENS = List.of(
            "--color-bg-primary",  // непрозрачная плитка
            "--color-status-error" // рамка и глиф «неверно»
    );

    /**
     * Главный контракт: именованное правило обязано кем-то включаться.
     *
     * <p>Правило без активации не падает, не мигает и не видно в UI — оно просто
     * тянет за собой PNG, метаданные и строки стилей. Обнаруживается только
     * сверкой «правила против кода», поэтому она здесь, а не в ревью.
     */
    @Test
    void everySpriteRuleIsActivatedBySomeCode() throws IOException {
        String code = readCorpus(ACTIVATION_ROOTS);

        List<String> dead = new ArrayList<>();
        for (String name : ruleNames()) {
            if (!code.contains("'" + name + "'") && !code.contains("\"" + name + "\"")) {
                dead.add(name);
            }
        }

        assertThat(dead)
                .as("правила [data-sprite-anim=…], которые никто не включает. Инвентарь "
                        + "ассетов такое пропускает: файл считается живым из-за url() в самом "
                        + "правиле. Либо включить, либо удалить правило вместе с листом и "
                        + "метаданными")
                .isEmpty();
    }

    /** Обратная сторона: активация без правила — атрибут выставлен, а стиля нет, кадр не поедет. */
    @Test
    void everyActivatedSpriteHasARule() throws IOException {
        Set<String> rules = ruleNames();
        Matcher matcher = Pattern.compile("data-sprite-anim['\"]?\\s*[,=:]\\s*['\"]([a-z0-9-]+)['\"]")
                .matcher(readCorpus(ACTIVATION_ROOTS));

        Set<String> missing = new LinkedHashSet<>();
        while (matcher.find()) {
            if (!rules.contains(matcher.group(1))) {
                missing.add(matcher.group(1));
            }
        }

        assertThat(missing).as("код включает спрайт, для которого нет правила в sprite-anim.css").isEmpty();
    }

    /**
     * Палитра листа против токенов продукта. Читается по именам — тех же, что
     * использует генератор, поэтому второй копии палитры в репозитории нет.
     * Берётся тёмная ветка {@code tokens.css} (она в файле первая): плитка
     * непрозрачна, спрайт одинаков в обеих темах.
     */
    @Test
    void everySheetUsesOnlyTheProductRamp() throws IOException {
        Set<Integer> ramp = ramp();
        List<String> offRamp = new ArrayList<>();

        for (Path sheet : sheets()) {
            Set<String> foreign = new LinkedHashSet<>();
            for (int rgb : opaqueColors(ImageIO.read(sheet.toFile()))) {
                if (!ramp.contains(rgb)) {
                    foreign.add(hex(rgb));
                }
            }
            if (!foreign.isEmpty()) {
                offRamp.add(sheet.getFileName() + ": " + foreign);
            }
        }

        assertThat(offRamp)
                .as("цвета вне токенов продукта — перегенерировать scripts/gen-sprite-sheets.py")
                .isEmpty();
    }

    /**
     * Сглаживание на пиксель-арте даёт ореол, а служебные чанки PNG выдают
     * экспорт из редактора мимо генератора. И то, и другое проверяется, потому
     * что заявление о происхождении в {@code THIRD_PARTY_ASSETS.md} держится
     * именно на структуре {@code IHDR/IDAT/IEND}.
     */
    @Test
    void everySheetKeepsGeneratedPixelArtSignature() throws IOException {
        List<String> antialiased = new ArrayList<>();
        List<String> unexpectedChunks = new ArrayList<>();

        for (Path sheet : sheets()) {
            if (hasPartialAlpha(ImageIO.read(sheet.toFile()))) {
                antialiased.add(sheet.getFileName().toString());
            }
            List<String> distinct = chunkTypes(Files.readAllBytes(sheet)).stream().distinct().toList();
            if (!distinct.equals(List.of("IHDR", "IDAT", "IEND"))) {
                unexpectedChunks.add(sheet.getFileName() + ": " + distinct);
            }
        }

        assertThat(antialiased).as("полупрозрачные пиксели — сглаженный растр вместо пиксель-арта").isEmpty();
        assertThat(unexpectedChunks)
                .as("служебные чанки PNG — признак экспорта из редактора, а не генерации в репозитории")
                .isEmpty();
    }

    /**
     * Ширина листа обязана быть ровно {@code frames × frameWidth}. Лишний
     * пиксель по краю не ломает сборку — он проявляется сползанием кадра к
     * концу цикла, потому что {@code steps()} делит именно эту ширину.
     */
    @Test
    void everySheetWidthMatchesItsFrameCount() throws IOException {
        List<String> mismatches = new ArrayList<>();

        for (Path sheet : sheets()) {
            String name = sheet.getFileName().toString().replace("-sheet.png", "");
            String meta = Files.readString(
                    RESOURCES.resolve("static/img/animations/metadata/" + name + ".json"), StandardCharsets.UTF_8);
            int frames = intField(meta, "frames");
            int frameWidth = intField(meta, "frameWidth");
            int frameHeight = intField(meta, "frameHeight");

            BufferedImage image = ImageIO.read(sheet.toFile());
            if (image.getWidth() != frames * frameWidth || image.getHeight() != frameHeight) {
                mismatches.add(name + ": лист " + image.getWidth() + "x" + image.getHeight()
                        + ", по метаданным " + (frames * frameWidth) + "x" + frameHeight);
            }
        }

        assertThat(mismatches).as("размер листа против кадровки").isEmpty();
    }

    private int intField(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\"\\s*:\\s*(\\d+)").matcher(json);
        assertThat(matcher.find()).as("поле %s в метаданных", field).isTrue();
        return Integer.parseInt(matcher.group(1));
    }

    private Set<String> ruleNames() throws IOException {
        String css = Files.readString(SPRITE_CSS, StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("\\[data-sprite-anim=\"([a-z0-9-]+)\"]").matcher(css);
        Set<String> names = new LinkedHashSet<>();
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        assertThat(names).as("именованные правила спрайт-анимаций").isNotEmpty();
        return names;
    }

    private Set<Integer> ramp() throws IOException {
        String css = Files.readString(TOKENS, StandardCharsets.UTF_8);
        Set<Integer> ramp = new LinkedHashSet<>();
        for (String token : RAMP_TOKENS) {
            Matcher matcher = Pattern.compile(Pattern.quote(token) + ":\\s*#([0-9a-fA-F]{6})\\b").matcher(css);
            assertThat(matcher.find()).as("токен %s в tokens.css", token).isTrue();
            ramp.add(Integer.parseInt(matcher.group(1), 16));
        }
        assertThat(ramp).as("две различные роли рампы").hasSize(RAMP_TOKENS.size());
        return ramp;
    }

    private Set<Integer> opaqueColors(BufferedImage image) {
        Set<Integer> colors = new LinkedHashSet<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                if ((argb >>> 24) != 0) {
                    colors.add(argb & 0xFFFFFF);
                }
            }
        }
        return colors;
    }

    private boolean hasPartialAlpha(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int alpha = image.getRGB(x, y) >>> 24;
                if (alpha != 0 && alpha != 255) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> chunkTypes(byte[] png) {
        List<String> types = new ArrayList<>();
        int pos = 8; // сигнатура PNG
        while (pos + 8 <= png.length) {
            int length = ((png[pos] & 0xFF) << 24) | ((png[pos + 1] & 0xFF) << 16)
                    | ((png[pos + 2] & 0xFF) << 8) | (png[pos + 3] & 0xFF);
            types.add(new String(png, pos + 4, 4, StandardCharsets.US_ASCII));
            pos += 12 + length;
        }
        return types;
    }

    private String hex(int rgb) {
        return String.format(Locale.ROOT, "#%06x", rgb);
    }

    private List<Path> sheets() throws IOException {
        try (Stream<Path> paths = Files.list(SHEETS)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".png")).sorted().toList();
        }
    }

    private String readCorpus(List<String> roots) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String root : roots) {
            Path path = Path.of(root);
            if (!Files.exists(path)) {
                continue;
            }
            try (Stream<Path> paths = Files.walk(path)) {
                for (Path file : paths.filter(Files::isRegularFile).sorted().toList()) {
                    sb.append(Files.readString(file, StandardCharsets.UTF_8)).append('\n');
                }
            } catch (UncheckedIOException e) {
                throw new IOException(e);
            }
        }
        return sb.toString();
    }
}
