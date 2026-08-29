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
 * У иконок продукта ОДИН язык, и он линейный (FOLIO: Lucide, 24×24, штрих 2px,
 * currentColor).
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * До iter-112 словарей было три (линейный, «пиксельный» с дугами и настоящий
 * пиксель 16×16); из 25 символов среднего живыми были ВОСЕМЬ, шесть понятий имели
 * по два глифа. Гейт остался от той чистки: язык сменился (миссия 2026-08-29,
 * пиксельная сетка снята вместе со слоем pixel), инварианты — нет.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>спрайтов-словарей ровно два: иконка при тексте и глиф состояния;</li>
 *   <li>каждый символ кто-то использует — склад мёртвых глифов не копится;</li>
 *   <li>каждая ссылка {@code <use href="#…">} резолвится в существующий символ;</li>
 *   <li>символы не несут цвета: тему даёт currentColor потребительского класса;</li>
 *   <li>растровых значков нет вовсе — ни файлом, ни url() в CSS.</li>
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
    /** Сцены режимов подставляются по имени из ModeUtils — как навигация и знаки. */
    private static final Path MODE_UTILS =
            Path.of("src/main/java/com/cheatsheet/quiz/common/util/ModeUtils.java");
    /** Знаки ступеней подставляются по имени из RankUtils — третий такой источник. */
    private static final Path RANK_UTILS =
            Path.of("src/main/java/com/cheatsheet/quiz/common/util/RankUtils.java");
    private static final Path ACHIEVEMENTS =
            Path.of("../quiz-domain/src/main/java/com/cheatsheet/quiz/domain/Achievement.java");
    /** Знаки разделов каталога подставляются по id из topic-groups.yaml. */
    private static final Path TOPIC_GROUPS = RESOURCES.resolve("topic-groups.yaml");

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern SYMBOL = Pattern.compile("<symbol id=\"([^\"]+)\"");

    /**
     * Спрайтов-словарей ровно два, и роли у них разные: иконка при тексте и глиф
     * состояния. Третий файл с {@code <symbol>} — это третий язык.
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
     * Линейный язык проверяется по разметке, а не по названию файла: сетка 24×24 и
     * НИ ОДНОГО presentation-атрибута цвета в символе. Цвет и штрих даёт
     * потребительский класс через currentColor — символ с зашитым {@code fill="#…"}
     * не знал бы темы (ровно так растровый чекбокс в светлой теме читался как
     * «выбрано», см. историю гейта).
     */
    @Test
    void everySymbolIsThemeAgnosticLineArt() {
        List<String> offenders = new ArrayList<>();
        int checked = 0;
        for (Path sprite : List.of(ICONS, STATE)) {
            String source = COMMENT.matcher(read(sprite)).replaceAll("\n");
            Matcher symbol = Pattern.compile("<symbol id=\"([^\"]+)\"([^>]*)>(.*?)</symbol>", Pattern.DOTALL)
                    .matcher(source);
            while (symbol.find()) {
                checked++;
                String name = symbol.group(1);
                String attrs = symbol.group(2);
                String body = symbol.group(3);
                if (!attrs.contains("viewBox=\"0 0 24 24\"")) {
                    offenders.add(name + ": сетка не 24×24");
                }
                if (body.matches("(?s).*\\b(fill|stroke)=\"(?!none\")[^\"]*\".*")) {
                    offenders.add(name + ": цвет зашит в символ — тема его не перекрасит");
                }
            }
        }
        assertThat(checked).as("символы в спрайтах").isPositive();
        assertThat(offenders).as("символ выпал из линейного языка").isEmpty();
    }

    /**
     * База потребительского класса живёт в components-icons.css и рисует штрихом:
     * {@code fill: none; stroke: currentColor}. Без этой базы линейные символы
     * рендерятся чёрной заливкой по умолчанию SVG — одинаково неверно в обеих темах.
     */
    @Test
    void theConsumerClassDrawsWithStroke() {
        String css = read(RESOURCES.resolve("static/css/components-icons.css"))
                .replaceAll("(?s)/\\*.*?\\*/", "\n");
        Matcher base = Pattern.compile("html\\[data-design]\\s+\\.ui-icon\\s*\\{([^}]*)}").matcher(css);
        assertThat(base.find()).as("база .ui-icon исчезла из components-icons.css").isTrue();
        String body = base.group(1);
        assertThat(body).contains("fill: none");
        assertThat(body).contains("stroke: currentColor");
    }

    /**
     * Значок интерфейса не бывает растровым: ни файла-PNG в static/, ни {@code url(/img/…)}
     * в CSS. Растр не наследует тему — см. историю гейта (чекбокс светлой темы).
     */
    @Test
    void interfaceGlyphsAreNeverRaster() {
        List<String> offenders = new ArrayList<>();
        for (Path css : list(RESOURCES.resolve("static/css"))) {
            if (!css.toString().endsWith(".css")) {
                continue;
            }
            String source = read(css).replaceAll("(?s)/\\*.*?\\*/", "\n");
            Matcher raster = Pattern.compile("url\\(\"?/img/[a-z0-9/_-]+\\.(png|gif|jpe?g|webp)").matcher(source);
            while (raster.find()) {
                offenders.add(css.getFileName() + " → " + raster.group());
            }
        }
        assertThat(offenders)
                .as("растровый значок вернулся в CSS. PNG не знает темы: именно так маркер "
                        + "невыбранного чипа в светлой теме выглядел закрашенным")
                .isEmpty();

        List<String> rasterFiles = list(RESOURCES.resolve("static")).stream()
                .map(Path::toString)
                .filter(name -> name.matches(".*\\.(png|gif|jpe?g|webp)$"))
                .toList();
        assertThat(rasterFiles)
                .as("в static/ вернулся растровый ассет — вся графика продукта векторная")
                .isEmpty();
    }

    /**
     * Знак продукта — символ спрайта {@code #crest} (книга с закладкой), и он один
     * на сайдбар и мобильную шапку. Favicon рисует тот же знак теми же путями.
     */
    @Test
    void theBrandMarkIsASpriteSymbol() {
        String sidebar = COMMENT.matcher(read(FRAGMENTS.resolve("sidebar.html"))).replaceAll("\n");
        String header = COMMENT.matcher(read(FRAGMENTS.resolve("shell-head.html"))).replaceAll("\n");
        for (String markup : List.of(sidebar, header)) {
            assertThat(markup).as("знак бренда не берёт символ спрайта")
                    .contains("<use href=\"#crest\">");
        }
        assertThat(symbols(ICONS)).as("знак исчез из спрайта").contains("crest");
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

    /** Всё, чем продукт ссылается на символы: разметка, JS-хелпер, источники имён в Java/YAML. */
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

        // Сцены режимов — тот же случай: карточка конструктора берёт имя символа из
        // ModeUtils#glyph, поэтому строки `#mode-…` в разметке тоже нет.
        Matcher modes = Pattern.compile("-> \"(mode-[a-z-]+)\";").matcher(read(MODE_UTILS));
        while (modes.find()) {
            found.add(modes.group(1));
        }

        // Знаки ступеней — источник имён в RankUtils#glyph: карточка прогрессии берёт
        // символ по текущей ступени, в разметке строки `#rank-…` нет.
        Matcher rankEmblems = Pattern.compile("\"(rank-[a-z-]+)\"").matcher(read(RANK_UTILS));
        while (rankEmblems.find()) {
            found.add(rankEmblems.group(1));
        }

        // Знаки разделов каталога — id символа в topic-groups.yaml (`icon: section-…`).
        Matcher sections = Pattern.compile("icon:\\s*([a-z][a-z0-9-]*)").matcher(read(TOPIC_GROUPS));
        while (sections.find()) {
            found.add(sections.group(1));
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
