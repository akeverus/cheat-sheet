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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * У иконок продукта ОДИН язык, и он пиксельный.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * До iter-112 словарей было три: линейный Lucide-спрайт ({@code fragments/icons.html},
 * viewBox 24, stroke-width 2), «пиксельный» {@code fragments/pixel-sprite.html} — который
 * пиксельным не был, 15 из 25 его символов содержали дуги и диагонали, — и настоящий
 * пиксель 16×16 в {@code fragments/state-illustrations.html}. Замер по всем шаблонам, JS и
 * {@code NavSection}: из 25 символов второго спрайта живыми были ВОСЕМЬ, 17 не
 * использовались нигде. Шесть понятий имели по два глифа сразу (code, diagram, star, check,
 * copy, close/x), и во всех шести парах жил линейный, а пиксельный был мёртв. Каждая
 * страница при этом грузила оба спрайта.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>спрайт интерфейсных иконок ровно один;</li>
 *   <li>каждый его символ кто-то использует — склад мёртвых глифов не копится;</li>
 *   <li>каждая ссылка {@code <use href="#…">} резолвится в существующий символ;</li>
 *   <li>язык пиксельный: только прямоугольники по клеткам, никаких путей с дугами;</li>
 *   <li>размер целочисленный: у сетки 16×16 дробный масштаб — это мыло.</li>
 * </ol>
 */
class IconLanguageContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path TEMPLATES = RESOURCES.resolve("templates");
    private static final Path FRAGMENTS = TEMPLATES.resolve("fragments");
    private static final Path ICONS = FRAGMENTS.resolve("icons.html");
    private static final Path STATE = FRAGMENTS.resolve("state-illustrations.html");
    private static final Path NAV_SECTION =
            Path.of("src/main/java/com/cheatsheet/quiz/config/web/NavSection.java");
    /** Каталог знаков отличия — второй источник имён глифов, живущий в Java. */
    private static final Path ACHIEVEMENTS =
            Path.of("../quiz-domain/src/main/java/com/cheatsheet/quiz/domain/Achievement.java");

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern SYMBOL = Pattern.compile("<symbol id=\"([^\"]+)\"");

    /**
     * Спрайтов-словарей ровно два, и роли у них разные: иконка при тексте (16px) и
     * hero-глиф состояния (64px). Третий файл с {@code <symbol>} — это третий язык.
     */
    @Test
    void thereIsExactlyOneSpriteOfInterfaceIcons() {
        List<String> sprites = new ArrayList<>();
        for (Path fragment : list(FRAGMENTS)) {
            String source = COMMENT.matcher(read(fragment)).replaceAll("\n");
            if (source.contains("<symbol id=")) {
                sprites.add(fragment.getFileName().toString());
            }
        }
        assertThat(sprites)
                .as("появился ещё один словарь символов. Два языка на одно понятие — это не "
                        + "разнообразие: в iter-112 таких пар нашлось шесть, и в каждой один "
                        + "глиф был мёртв")
                .containsExactlyInAnyOrder("icons.html", "state-illustrations.html");
    }

    /** Мёртвый символ не падает в рантайме и не виден в UI — он обнаруживается только замером. */
    @Test
    void everyIconSymbolIsActuallyUsed() {
        Set<String> declared = symbols(ICONS);
        Set<String> referenced = references();

        List<String> dead = declared.stream().filter(s -> !referenced.contains(s)).sorted().toList();
        assertThat(dead)
                .as("в спрайте копится склад: эти символы не использует ни шаблон, ни JS, ни "
                        + "NavSection. До чистки iter-112 таких было 17 из 25")
                .isEmpty();
    }

    /** Ссылка на несуществующий символ — пустое место на экране, а не ошибка в консоли. */
    @Test
    void everyIconReferenceResolves() {
        Set<String> declared = new LinkedHashSet<>(symbols(ICONS));
        declared.addAll(symbols(STATE));

        List<String> broken = new ArrayList<>();
        for (Path file : sources()) {
            String source = COMMENT.matcher(read(file)).replaceAll("\n");
            Matcher use = Pattern.compile("<use[^>]*href=\"#([a-z][a-z0-9-]*)\"").matcher(source);
            while (use.find()) {
                if (!declared.contains(use.group(1))) {
                    broken.add(file.getFileName() + " → #" + use.group(1));
                }
            }
        }
        assertThat(broken).as("ссылка на несуществующий символ: на экране будет пустое место, "
                + "а консоль промолчит").isEmpty();
    }

    /**
     * Пиксельный язык проверяется по разметке, а не по названию файла. Прошлый спрайт
     * назывался «pixel», но 15 его символов рисовались путями с дугами: {@code clock} был
     * окружностью, {@code settings} — шестернёй. Имя файла ничего не гарантирует.
     */
    @Test
    void everySymbolIsDrawnOnThePixelGrid() {
        String source = COMMENT.matcher(read(ICONS)).replaceAll("\n");

        List<String> offenders = new ArrayList<>();
        Matcher symbol = Pattern.compile("<symbol id=\"([^\"]+)\"(.*?)</symbol>", Pattern.DOTALL)
                .matcher(source);
        int checked = 0;
        while (symbol.find()) {
            checked++;
            String name = symbol.group(1);
            String body = symbol.group(2);
            if (body.contains("<path") || body.contains("<circle") || body.contains("<line")) {
                offenders.add(name + ": не прямоугольники");
            }
            if (!body.contains("shape-rendering=\"crispEdges\"")) {
                offenders.add(name + ": без crispEdges — сглаженные клетки");
            }
            if (!body.contains("viewBox=\"0 0 16 16\"") && !symbol.group(0).contains("viewBox=\"0 0 16 16\"")) {
                offenders.add(name + ": сетка не 16×16");
            }
            Matcher rect = Pattern.compile("<rect ([^/>]*)/>").matcher(body);
            while (rect.find()) {
                if (rect.group(1).contains(".")) {
                    offenders.add(name + ": дробная координата");
                }
            }
        }
        assertThat(checked).as("символы в спрайте").isPositive();
        assertThat(offenders).as("символ нарисован не по клеткам — пиксельный язык держится "
                + "разметкой, а не названием файла").isEmpty();
    }

    /**
     * Размер иконки — целое число пикселей. При кегле 18px прежняя база {@code 1em} давала
     * 1.125 device-пикселя на клетку сетки, то есть мыло ровно там, где пиксельность и есть
     * смысл. Заодно ловится возврат снятых контекстных 14/15px.
     */
    @Test
    void iconSizesStayOnWholePixels() {
        List<String> offenders = new ArrayList<>();
        for (Path css : list(RESOURCES.resolve("static/css"))) {
            String source = css.getFileName().toString().endsWith(".css")
                    ? read(css).replaceAll("(?s)/\\*.*?\\*/", "\n")
                    : "";
            Matcher rule = Pattern.compile("([^{}]*\\.px-icon[^{}]*)\\{([^}]*)}").matcher(source);
            while (rule.find()) {
                Matcher size = Pattern.compile("(width|height)\\s*:\\s*([^;]+);").matcher(rule.group(2));
                while (size.find()) {
                    String value = size.group(2).trim();
                    boolean wholePixels = value.matches("\\d+px")
                            || value.startsWith("var(--px-");
                    if (!wholePixels) {
                        offenders.add(css.getFileName() + ": " + rule.group(1).strip()
                                .replaceAll("\\s+", " ") + " → " + size.group(1) + ": " + value);
                    }
                }
            }
        }
        assertThat(offenders).as("размер иконки задан дробно (em/rem/проценты). Сетка 16×16 не "
                + "масштабируется дробно — это мыло, а не «чуть крупнее»").isEmpty();
    }

    /**
     * Значок интерфейса не бывает растровым.
     *
     * <p>После iter-112 язык иконок стал один — пиксельный SVG, — но мелкие значки,
     * которые рисует не разметка, а псевдоэлемент, остались растровыми: восемнадцать PNG
     * в CSS. Растр не наследует тему, и замер показал это буквально: один и тот же
     * {@code checkbox-off.png} рисовался в обеих темах, поэтому в СВЕТЛОЙ теме маркер
     * невыбранного чипа выглядел чёрным залитым квадратом — «не выбрано» читалось как
     * «выбрано». Состояние врало.</p>
     *
     * <p>Теперь такие значки — маски ({@code mask-image} + {@code currentColor}), и цвет
     * им даёт тема. Растровыми остаются только две вещи, которые значками не являются:
     * бренд ({@code mascot.png}) и сцена страницы ошибки ({@code portal.png}).</p>
     */
    @Test
    void interfaceGlyphsAreNeverRaster() {
        List<String> offenders = new ArrayList<>();
        for (Path css : list(RESOURCES.resolve("static/css"))) {
            if (!css.toString().endsWith(".css")) {
                continue;
            }
            String source = read(css).replaceAll("(?s)/\\*.*?\\*/", "\n");
            Matcher raster = Pattern.compile("url\\(\"?/img/pixel/([a-z0-9-]+\\.png)").matcher(source);
            while (raster.find()) {
                offenders.add(css.getFileName() + " → " + raster.group(1));
            }
        }
        assertThat(offenders)
                .as("растровый значок вернулся в CSS. PNG не знает темы: именно так маркер "
                        + "невыбранного чипа в светлой теме выглядел закрашенным")
                .isEmpty();
    }

    /**
     * Знак продукта — символ спрайта, а не растр, и он один.
     *
     * <p>До iter-119 бренд рисовал {@code mascot.png} 24×24: растр не знал темы (на
     * пергаменте оставался тёмным пятном из тёмной темы) и не ложился на сетку — 24 это
     * ×1.5 от 16×16. Он же стоял на странице ошибки для кодов, кроме 404, — «лицо
     * продукта» на экране сбоя ничего не объясняло.</p>
     *
     * <p>Теперь знак — символ {@code #crest} (щит с раскрытой книгой), 32px = ×2 от
     * сетки, цвет от темы. Растровых ассетов в продукте не осталось вовсе.</p>
     */
    @Test
    void theBrandMarkIsASpriteSymbolAndRasterAssetsAreGone() {
        String sidebar = COMMENT.matcher(read(FRAGMENTS.resolve("sidebar.html"))).replaceAll("\n");
        String header = COMMENT.matcher(read(FRAGMENTS.resolve("shell-head.html"))).replaceAll("\n");
        for (String markup : List.of(sidebar, header)) {
            assertThat(markup).as("знак бренда снова растровый").doesNotContain("mascot.png");
            assertThat(markup).as("знак бренда не берёт символ спрайта")
                    .contains("<use href=\"#crest\">");
        }
        assertThat(symbols(ICONS)).as("герб исчез из спрайта").contains("crest");

        Path raster = RESOURCES.resolve("static/img/pixel");
        List<String> files = list(raster).stream()
                .map(p -> p.getFileName().toString())
                .filter(name -> name.endsWith(".png"))
                .toList();
        assertThat(files)
                .as("в продукт вернулся растровый пиксельный ассет: PNG не знает темы и не "
                        + "тянется целочисленно от сетки 16×16")
                .isEmpty();
    }

    private static Set<String> symbols(Path sprite) {
        Set<String> found = new LinkedHashSet<>();
        Matcher m = SYMBOL.matcher(COMMENT.matcher(read(sprite)).replaceAll("\n"));
        while (m.find()) {
            found.add(m.group(1));
        }
        assertThat(found).as("символы в %s", sprite.getFileName()).isNotEmpty();
        return found;
    }

    /** Всё, чем продукт ссылается на символы: разметка, JS-хелпер, источник навигации. */
    private static Set<String> references() {
        Set<String> found = new LinkedHashSet<>();
        for (Path file : sources()) {
            String source = COMMENT.matcher(read(file)).replaceAll("\n");
            Matcher use = Pattern.compile("href=\"#([a-z][a-z0-9-]*)\"").matcher(source);
            while (use.find()) {
                found.add(use.group(1));
            }
            Matcher js = Pattern.compile("icon\\('([a-z-]+)'").matcher(source);
            while (js.find()) {
                found.add(js.group(1));
            }
            Matcher thymeleaf = Pattern.compile("'#([a-z][a-z0-9-]*)'").matcher(source);
            while (thymeleaf.find()) {
                found.add(thymeleaf.group(1));
            }
        }
        String nav = read(NAV_SECTION);
        Matcher icons = Pattern.compile("\"[a-z]+\", \"[^\"]*\", \"[^\"]*\", \"([a-z-]+)\"").matcher(nav);
        while (icons.find()) {
            found.add(icons.group(1));
        }

        // Знаки отличия ссылаются на свои глифы ДИНАМИЧЕСКИ («#» + id из каталога), поэтому
        // прямой строки `#badge-…` в разметке нет — источник имени здесь Java, как и у
        // навигации. Не читать его значило бы объявить шесть живых глифов мёртвыми.
        Matcher badges = Pattern.compile("new Achievement\\(\"([a-z-]+)\"").matcher(read(ACHIEVEMENTS));
        while (badges.find()) {
            found.add(badges.group(1));
        }
        return found;
    }

    private static List<Path> sources() {
        List<Path> files = new ArrayList<>();
        files.addAll(list(TEMPLATES).stream()
                .filter(p -> p.toString().endsWith(".html"))
                .filter(p -> !p.endsWith("icons.html") && !p.endsWith("state-illustrations.html"))
                .toList());
        files.addAll(list(RESOURCES.resolve("static/js")).stream()
                .filter(p -> p.toString().endsWith(".js")).toList());
        return files;
    }

    private static List<Path> list(Path root) {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile).sorted().toList();
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
