package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
 * Контракт семейства опознавательных знаков разделов ({@code static/img/stickers}).
 *
 * <p>Мотив — замер, а не вкусовщина. Десять иконок каталога успели разъехаться:
 * на всё семейство приходилось 62 уникальных цвета, {@code question.png} был
 * сглаженным растром на 51 цвет среди девяти жёстких пиксельных, а
 * {@code rocket.png} и {@code target.png} тянули красный и оранжевый, которых
 * нет в палитре продукта. Разъезд не ломает ни сборку, ни рантайм: иконки
 * декоративны, лежат на разных карточках и рядом никогда не видны — поэтому
 * расхождение обнаруживается только измерением всего семейства сразу.
 *
 * <p>Источник истины для этих PNG — {@code scripts/gen-section-stickers.py};
 * сами файлы производны. Тест сторожит границу: правка мимо генератора
 * (экспорт из редактора, «поправлю один пиксель») выдаст себя либо чужим
 * цветом, либо сглаживанием, либо служебными чанками.
 */
class SectionStickerPaletteTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path STICKERS = RESOURCES.resolve("static/img/stickers");

    /**
     * Рампа из {@code gen-section-stickers.py}. Тёплого акцента в ней нет
     * намеренно: продукт обходится холодной парой «бирюза + голубой» плюс
     * зелёный статуса, поэтому пламя ракеты зелёное, а не оранжевое.
     */
    private static final Set<Integer> RAMP = Set.of(
            0x10242D, // плитка
            0x264A57, // вторичная поверхность
            0x22D3C5, // рамка и акцент
            0x38BDF8, // холодный вторичный акцент
            0x22C55E, // «готово/запуск»
            0xF8FAFC  // свет
    );

    /** Иконка рисуется в сетке 32×32 с двукратным увеличением. */
    private static final int SIZE = 64;

    /**
     * Потолок на файл. Шесть — размер рампы; смысл ограничения в том, что
     * иконка обязана читаться силуэтом, а не количеством оттенков.
     */
    private static final int MAX_COLORS_PER_ICON = 6;

    @Test
    void everyStickerUsesOnlyTheProductRamp() throws IOException {
        List<String> offRamp = new ArrayList<>();

        for (Path sticker : stickers()) {
            BufferedImage image = ImageIO.read(sticker.toFile());
            Set<Integer> colors = opaqueColors(image);

            Set<String> foreign = new LinkedHashSet<>();
            colors.stream().filter(rgb -> !RAMP.contains(rgb)).forEach(rgb -> foreign.add(hex(rgb)));
            if (!foreign.isEmpty()) {
                offRamp.add(sticker.getFileName() + ": " + foreign);
            }
        }

        assertThat(offRamp)
                .as("цвета вне рампы. Иконки семейства видны не рядом, поэтому расхождение "
                        + "замечается только замером — править через scripts/gen-section-stickers.py")
                .isEmpty();
    }

    /**
     * Сглаживание — не косметика, а смена техники. Полупрозрачный край даёт
     * ореол на светлой карточке и мгновенно выбивает иконку из ряда жёстких
     * пиксельных соседей. Альфа допускается ровно двух значений.
     */
    @Test
    void everyStickerIsHardEdgedPixelArt() throws IOException {
        List<String> antialiased = new ArrayList<>();
        List<String> tooManyColors = new ArrayList<>();
        List<String> wrongSize = new ArrayList<>();

        for (Path sticker : stickers()) {
            BufferedImage image = ImageIO.read(sticker.toFile());
            if (image.getWidth() != SIZE || image.getHeight() != SIZE) {
                wrongSize.add(sticker.getFileName() + ": " + image.getWidth() + "x" + image.getHeight());
            }
            if (hasPartialAlpha(image)) {
                antialiased.add(sticker.getFileName().toString());
            }
            int colors = opaqueColors(image).size();
            if (colors > MAX_COLORS_PER_ICON) {
                tooManyColors.add(sticker.getFileName() + ": " + colors);
            }
        }

        assertThat(wrongSize).as("размер иконки раздела").isEmpty();
        assertThat(antialiased).as("полупрозрачные пиксели — сглаженный растр вместо пиксель-арта").isEmpty();
        assertThat(tooManyColors).as("цветов на иконку, потолок " + MAX_COLORS_PER_ICON).isEmpty();
    }

    /**
     * Происхождение проверяется структурой файла: писатель на stdlib
     * {@code zlib}/{@code struct} выдаёт ровно три чанка, а любой редактор или
     * готовый пак почти всегда добавляет {@code pHYs}, {@code sRGB},
     * {@code gAMA} или {@code tEXt}. Заявление в {@code THIRD_PARTY_ASSETS.md}
     * держится на этом признаке, поэтому он проверяется, а не подразумевается.
     */
    @Test
    void everyStickerKeepsGeneratedPngSignature() throws IOException {
        List<String> unexpected = new ArrayList<>();

        for (Path sticker : stickers()) {
            List<String> chunks = chunkTypes(Files.readAllBytes(sticker));
            List<String> distinct = chunks.stream().distinct().toList();
            if (!distinct.equals(List.of("IHDR", "IDAT", "IEND"))) {
                unexpected.add(sticker.getFileName() + ": " + distinct);
            }
        }

        assertThat(unexpected)
                .as("служебные чанки PNG — признак экспорта из редактора, а не генерации в репозитории")
                .isEmpty();
    }

    /**
     * Семейство закрыто: ровно по одной иконке на раздел каталога и ни одной
     * лишней. Осиротевшая иконка — мёртвый вес, недостающая — молча пустая
     * карточка раздела.
     */
    @Test
    void stickerFamilyMatchesTopicGroupsExactly() throws IOException {
        String yaml = Files.readString(RESOURCES.resolve("topic-groups.yaml"), StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("(?m)^\\s*icon:\\s*stickers/(\\S+)\\s*$").matcher(yaml);

        Set<String> declared = new LinkedHashSet<>();
        while (matcher.find()) {
            declared.add(matcher.group(1));
        }

        Set<String> onDisk = new LinkedHashSet<>();
        for (Path sticker : stickers()) {
            onDisk.add(sticker.getFileName().toString());
        }

        assertThat(declared).as("иконки, объявленные в topic-groups.yaml").isNotEmpty();
        assertThat(onDisk).as("файлы семейства и объявленные иконки должны совпадать").isEqualTo(declared);
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

    private List<Path> stickers() throws IOException {
        try (Stream<Path> paths = Files.list(STICKERS)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".png")).sorted().toList();
        }
    }
}
