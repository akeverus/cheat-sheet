package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Язык поверхностей: форма одна, а контур означает интерактивность.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер на живых страницах (Chromium и WebKit, обе темы):
 * <ol>
 *   <li>карточка и КНОПКА несли один и тот же контур {@code rgb(122,110,87)} толщиной 3px —
 *       пассивная плита выглядела ровно как то, на что можно нажать, и рамка перестала
 *       что-либо означать. При этом сама плита от канвы почти не отличалась: 1.09 в тёмной
 *       теме и 1.15 в светлой, то есть без контура она исчезала;</li>
 *   <li>пиксельный слой гасил радиус карточкам и кнопкам девятью объявлениями
 *       {@code border-radius: 0 !important}, но на шести страницах оставалось
 *       <b>176 скруглённых элементов</b> в Chromium и 184 в WebKit — инлайн-код, ссылки
 *       рельса, чипы, бейджи, полосы прогресса, кнопка «наверх» (999px).</li>
 * </ol>
 * То есть половина продукта была пиксельной, половина скруглённой, и держалось это на
 * !important-затираниях в декоративном слое.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>радиус — ОДИН токен, и он нулевой: форма правится в одном месте, а не в 84;</li>
 *   <li>затирания {@code border-radius: 0 !important} не возвращаются — они означают, что
 *       язык формы снова держится на декоративном слое;</li>
 *   <li>пассивные плиты не носят контур поверхности; контур остаётся у интерактивного;</li>
 *   <li>фактура объявлена один раз — токеном, а не копией градиента по файлам.</li>
 * </ol>
 */
class SurfaceLanguageContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /** Пассивные поверхности — плиты, на которые не нажимают. */
    private static final List<String> PASSIVE_SURFACES = List.of(
            ".card", ".panel", ".explanation-panel", ".question-code-wrap",
            ".resume-banner", ".error-card", ".summary-hero", ".builder-card", ".metric");

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static String stripComments(String css) {
        return CSS_COMMENT.matcher(css).replaceAll("\n");
    }

    @Test
    void shapeIsOneTokenAndItIsSquare() throws IOException {
        String tokens = stripComments(read("static/css/tokens.css"));

        Matcher radius = Pattern.compile("--border-radius-[a-z]+:\\s*([^;]+);").matcher(tokens);
        List<String> nonZero = new ArrayList<>();
        int found = 0;
        while (radius.find()) {
            found++;
            String value = radius.group(1).trim();
            if (!value.equals("0")) {
                nonZero.add(radius.group(0));
            }
        }
        assertThat(found).as("токены радиуса исчезли из tokens.css").isPositive();
        assertThat(nonZero)
                .as("форма продукта снова стала двойной: часть экранов скруглена, часть нет. "
                        + "Замер до обнуления — 176 скруглённых элементов на шести страницах")
                .isEmpty();
    }

    @Test
    void theDecorativeLayerDoesNotEraseShapeAnymore() {
        List<String> offenders = new ArrayList<>();
        for (Path css : cssFiles()) {
            String source = stripComments(readFile(css));
            if (source.contains("border-radius: 0 !important")) {
                offenders.add(css.getFileName().toString());
            }
        }
        assertThat(offenders)
                .as("в pixel-слое снова стирают радиус через !important. Это значит, что форма "
                        + "держится на декоративном слое, а не на токене: девять таких затираний "
                        + "уже сосуществовали со 176 скруглёнными элементами")
                .isEmpty();
    }

    /**
     * Контур — признак того, на что можно нажать. Если он снова окажется на пассивной плите,
     * кнопка и карточка опять станут неразличимы по границе.
     */
    @Test
    void passiveSurfacesWearTextureAndInteractiveOnesWearTheEdge() throws IOException {
        String cards = stripComments(read("static/css/pixel-cards.css"));

        Matcher rule = Pattern.compile("([^{}]*\\.card[^{}]*)\\{([^}]*)}").matcher(cards);
        assertThat(rule.find()).as("правило плит исчезло из pixel-cards.css").isTrue();
        String selector = rule.group(1);
        String body = rule.group(2);

        for (String surface : PASSIVE_SURFACES) {
            assertThat(selector).as("плита %s выпала из общего правила поверхностей", surface)
                    .contains(surface);
        }
        assertThat(body).as("контур поверхности вернулся на пассивную плиту — она снова "
                        + "неотличима от кнопки").doesNotContain("var(--px-edge-surface)");
        assertThat(body).as("плита осталась без фактуры: сняли контур и не дали материал — "
                        + "при контрасте к канве 1.15 она попросту исчезнет")
                .contains("var(--px-grain-surface)");

        // Контролы контур сохраняют: граница компонента интерфейса считается по WCAG 1.4.11.
        String controls = stripComments(read("static/css/components-controls.css"));
        assertThat(controls).as("кнопки лишились контура — граница контрола единственная, "
                        + "что отделяет его от фона (FRD-110/111)")
                .contains("var(--px-edge-surface)");
    }

    @Test
    void textureIsDeclaredOnceAsAToken() {
        List<String> declarations = new ArrayList<>();
        List<String> literalGrain = new ArrayList<>();
        for (Path css : cssFiles()) {
            String source = stripComments(readFile(css));
            if (source.contains("--px-grain-surface:")) {
                declarations.add(css.getFileName().toString());
            }
            // Копия градиента зерна в обход токена — вторая система фактуры.
            Matcher gradient = Pattern.compile("background-image:\\s*radial-gradient\\([^;]*0\\.5px").matcher(source);
            if (gradient.find() && !css.getFileName().toString().equals("pixel-fx.css")) {
                literalGrain.add(css.getFileName().toString());
            }
        }
        assertThat(declarations).as("фактура объявлена не один раз — значения разъедутся по темам")
                .containsExactly("pixel-fx.css");
        assertThat(literalGrain).as("зерно скопировано мимо токена").isEmpty();
    }

    private static List<Path> cssFiles() {
        try (Stream<Path> paths = Files.walk(CSS_DIR)) {
            return paths.filter(p -> p.toString().endsWith(".css")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
