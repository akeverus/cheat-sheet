package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт декоративного пиксельного слоя ({@code static/img/pixel/}).
 *
 * <p>Последний набор ассетов, у которого не было гейта, — и замер показал, что
 * он успел разъехаться ровно тем же способом, что секционные стикеры до
 * {@link SectionStickerPaletteTest}. На 66 спрайтов приходилось 28 цветов, и
 * восемь из них были почти-дублями уже существующих: белый {@code #ffffff} в 26
 * файлах против продуктового {@code #eef6f5} в трёх, тёмная плитка
 * {@code #0a1a20} в семнадцати против токенной {@code #071116} в одном.
 * Расхождение — 6–22 единицы суммарно по каналам: на ревью не ловится, а
 * набор при этом держит вторую палитру.
 *
 * <p>Ключевое решение — сверять <b>по именам токенов</b>, а не по списку HEX.
 * Список HEX в тесте был бы второй копией палитры: он разъехался бы с
 * {@code tokens.css} молча, и гейт начал бы защищать неверное значение.
 *
 * <p>Нормализация набора — {@code scripts/normalize-pixel-palette.py}.
 */
class PixelSpriteContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path SPRITES = RESOURCES.resolve("static/img/pixel");
    private static final Path TOKENS = RESOURCES.resolve("static/css/tokens.css");

    /**
     * Интерфейсные токены, которым разрешено появляться в декоративном спрайте.
     * Список именно перечислен, а не «любой {@code --color-*}»: спрайт, залитый
     * {@code --color-text-tertiary}, формально был бы «из палитры продукта», но
     * роль у него бессмысленная. Каждое имя ниже отвечает конкретной роли в
     * растре — ink, плитка, рамка, акцент, статус.
     */
    private static final List<String> PRODUCT_TOKENS = List.of(
            "--color-text-primary",          // ink, блики
            "--color-bg-primary",            // плитка спрайта
            "--color-surface-soft",          // заливка чекбокса
            "--color-border-primary",        // рамка
            "--color-accent-primary",        // акцент
            "--color-accent-primary-active", // тень акцента
            "--color-accent-secondary",      // амбер
            "--color-status-success",
            "--color-status-error",
            "--color-status-info",
            "--color-status-purple"
    );

    /**
     * Потолок цветов на файл. Максимум по набору — семь ({@code portal-sheet},
     * {@code confetti}); восемь оставляет ровно один шаг запаса. Смысл потолка
     * не в экономии: за десятком оттенков на 16×16 отличить замысел от дрейфа
     * уже нельзя ни глазом, ни этим тестом, потому что любой новый цвет
     * выглядит как ещё одна ступень градиента.
     */
    private static final int MAX_COLORS_PER_SPRITE = 8;

    /**
     * Главный контракт: каждый непрозрачный цвет набора обязан иметь имя.
     *
     * <p>Безымянный цвет — не эстетическая придирка. Пока имени нет, ничто не
     * отличает «новый оттенок золота» от «промах мимо старого золота на
     * двадцать единиц», и набор набирает дубли по одному за спрайт.
     */
    @Test
    void everySpriteColorHasANameInTokens() throws IOException {
        Map<Integer, String> named = namedRamp();
        List<String> unnamed = new ArrayList<>();

        for (Path sprite : sprites()) {
            Set<String> foreign = new LinkedHashSet<>();
            for (int rgb : opaqueColors(ImageIO.read(sprite.toFile()))) {
                if (!named.containsKey(rgb)) {
                    foreign.add(hex(rgb));
                }
            }
            if (!foreign.isEmpty()) {
                unnamed.add(sprite.getFileName() + ": " + foreign);
            }
        }

        assertThat(unnamed)
                .as("цвета без имени в tokens.css. Либо снять спрайт на существующий токен "
                        + "(scripts/normalize-pixel-palette.py), либо — если роль правда новая — "
                        + "завести --pixel-* и назвать её")
                .isEmpty();
    }

    /**
     * Обратная сторона: объявленная роль обязана кем-то использоваться.
     *
     * <p>Раньше потребитель у {@code --pixel-*} был только растровый, и проверка искала
     * цвет роли в спрайтах. В iter-118 восемнадцать растровых значков заменены
     * одноцветными масками, которые красятся токеном через {@code var()} — например,
     * огонёк серии берёт {@code var(--pixel-flame)}. Роль стала живой, не встречаясь ни в
     * одном PNG.</p>
     *
     * <p>Поэтому потребитель теперь любой из двух: пиксель в спрайте ИЛИ ссылка в CSS.
     * Это не ослабление — роль без обоих потребителей по-прежнему падает, а именно она и
     * опасна: следующий автор увидит имя, решит, что роль существует, и заведёт под неё
     * цвет.</p>
     */
    @Test
    void everyDecorativeRampTokenIsUsedBySomeSpriteOrStylesheet() throws IOException {
        Set<Integer> used = new LinkedHashSet<>();
        for (Path sprite : sprites()) {
            used.addAll(opaqueColors(ImageIO.read(sprite.toFile())));
        }
        StringBuilder css = new StringBuilder();
        for (Path file : Files.walk(Path.of("src/main/resources/static/css"))
                .filter(p -> p.toString().endsWith(".css")).toList()) {
            css.append(Files.readString(file, StandardCharsets.UTF_8));
        }
        String stylesheets = css.toString();

        List<String> dead = new ArrayList<>();
        decorativeRamp().forEach((name, rgb) -> {
            if (!used.contains(rgb) && !stylesheets.contains("var(" + name + ")")) {
                dead.add(name + " (#" + hex(rgb) + ")");
            }
        });

        assertThat(dead)
                .as("роли --pixel-*, которых нет ни в спрайте, ни в CSS: объявление без "
                        + "потребителя — приглашение завести под него цвет")
                .isEmpty();
    }

    /**
     * Декоративная рампа не имеет права зависеть от темы: файл PNG растровый,
     * один и тот же байт лежит и под тёмной, и под светлой. Переопределение
     * {@code --pixel-*} в светлой ветке означало бы обещание, которое картинка
     * физически не может выполнить, — и увело бы этот тест на значение, которого
     * в спрайтах нет.
     */
    @Test
    void decorativeRampIsNotRedefinedPerTheme() throws IOException {
        String css = rules();
        int light = css.indexOf(":root[data-theme=\"light\"]");
        assertThat(light).as("светлая ветка в tokens.css").isGreaterThan(0);

        Matcher matcher = Pattern.compile("(--pixel-[a-z-]+):").matcher(css.substring(light));
        Set<String> redefined = new LinkedHashSet<>();
        while (matcher.find()) {
            redefined.add(matcher.group(1));
        }

        assertThat(redefined)
                .as("--pixel-* переопределены под тему, хотя PNG на тему не реагирует")
                .isEmpty();
    }

    /**
     * Сглаживание даёт полупрозрачный ореол вокруг пиксель-арта, а служебные
     * чанки PNG выдают экспорт из редактора. Проверяется и то, и другое: на
     * структуре {@code IHDR/IDAT/IEND} держится заявление о происхождении
     * графики в {@code THIRD_PARTY_ASSETS.md}, и без теста оно превращается в
     * необеспеченное обещание.
     */
    @Test
    void everySpriteKeepsGeneratedPixelArtSignature() throws IOException {
        List<String> antialiased = new ArrayList<>();
        List<String> unexpectedChunks = new ArrayList<>();

        for (Path sprite : sprites()) {
            if (hasPartialAlpha(ImageIO.read(sprite.toFile()))) {
                antialiased.add(sprite.getFileName().toString());
            }
            List<String> distinct = chunkTypes(Files.readAllBytes(sprite)).stream().distinct().toList();
            if (!distinct.equals(List.of("IHDR", "IDAT", "IEND"))) {
                unexpectedChunks.add(sprite.getFileName() + ": " + distinct);
            }
        }

        assertThat(antialiased).as("полупрозрачные пиксели — сглаженный растр вместо пиксель-арта").isEmpty();
        assertThat(unexpectedChunks)
                .as("служебные чанки PNG — признак экспорта из редактора, а не отрисовки в репозитории")
                .isEmpty();
    }

    /** Потолок оттенков на спрайт — см. {@link #MAX_COLORS_PER_SPRITE}. */
    @Test
    void noSpriteExceedsTheColorCeiling() throws IOException {
        List<String> tooRich = new ArrayList<>();
        for (Path sprite : sprites()) {
            int colors = opaqueColors(ImageIO.read(sprite.toFile())).size();
            if (colors > MAX_COLORS_PER_SPRITE) {
                tooRich.add(sprite.getFileName() + ": " + colors);
            }
        }
        assertThat(tooRich).as("больше %d оттенков на спрайт", MAX_COLORS_PER_SPRITE).isEmpty();
    }

    /**
     * Только правила: комментарии вырезаны.
     *
     * <p>Шапка {@code tokens.css} перечисляет ветки тем прозой, в том числе строкой
     * {@code :root[data-theme="light"]}. Поиск по сырому тексту находит это упоминание
     * на 17-й строке и объявляет «светлой веткой» весь файл — тест краснеет на
     * объявлениях, которые лежат в тёмной. Ровно на этом же спотыкается любой
     * замер палитры скриптом, поэтому комментарии снимаются один раз здесь.
     */
    private String rules() throws IOException {
        return Files.readString(TOKENS, StandardCharsets.UTF_8).replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /** Имя токена → цвет; берётся ПЕРВОЕ объявление, то есть тёмная ветка (она в файле первая). */
    private Map<String, Integer> resolve(List<String> names) throws IOException {
        String css = rules();
        Map<String, Integer> resolved = new LinkedHashMap<>();
        for (String name : names) {
            Matcher matcher = Pattern.compile(Pattern.quote(name) + ":\\s*#([0-9a-fA-F]{6})\\b").matcher(css);
            assertThat(matcher.find()).as("токен %s в tokens.css", name).isTrue();
            resolved.put(name, Integer.parseInt(matcher.group(1), 16));
        }
        return resolved;
    }

    /** Декоративные роли — открытый список: имена вычитываются из tokens.css, а не дублируются здесь. */
    private Map<String, Integer> decorativeRamp() throws IOException {
        Matcher matcher = Pattern.compile("(--pixel-[a-z-]+):\\s*#([0-9a-fA-F]{6})\\b").matcher(rules());
        Map<String, Integer> ramp = new LinkedHashMap<>();
        while (matcher.find()) {
            ramp.put(matcher.group(1), Integer.parseInt(matcher.group(2), 16));
        }
        assertThat(ramp).as("декоративная рампа --pixel-* в tokens.css").isNotEmpty();
        return ramp;
    }

    private Map<Integer, String> namedRamp() throws IOException {
        Map<String, Integer> all = new LinkedHashMap<>(resolve(PRODUCT_TOKENS));
        all.putAll(decorativeRamp());

        Map<Integer, String> byColor = new LinkedHashMap<>();
        List<String> collisions = new ArrayList<>();
        all.forEach((name, rgb) -> {
            String previous = byColor.putIfAbsent(rgb, name);
            if (previous != null) {
                collisions.add(previous + " и " + name + " → #" + hex(rgb));
            }
        });

        assertThat(collisions)
                .as("две роли на один цвет: у спрайта не будет однозначного имени, "
                        + "а значит и способа отличить дрейф от замысла")
                .isEmpty();
        return byColor;
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
        return String.format(Locale.ROOT, "%06x", rgb);
    }

    private List<Path> sprites() throws IOException {
        try (Stream<Path> paths = Files.list(SPRITES)) {
            List<Path> all = paths
                    .filter(path -> path.getFileName().toString().endsWith(".png"))
                    .sorted()
                    .toList();
            assertThat(all).as("спрайты в static/img/pixel").isNotEmpty();
            return all;
        }
    }
}
