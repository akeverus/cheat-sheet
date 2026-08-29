package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Язык поверхностей FOLIO: форма — короткая токен-шкала, контур контрола — признак
 * интерактивности, пассивная плита держится тоном и волоском.
 *
 * <h2>История гейта</h2>
 * Гейт родился в pixel-эру из замера: карточка и кнопка несли один контур 3px и были
 * неразличимы, а «прямоугольность всего» держалась на девяти {@code !important}-затираниях
 * в декоративном слое при 176 скруглённых элементах на живых страницах. Форма обязана
 * правиться в ОДНОМ месте. Миссия 2026-08-29 сменила саму форму (квадрат → мягкая шкала),
 * но не инвариант: источник правды о радиусе — токены, литералы мимо токена запрещены.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>шкала радиусов — ровно шесть токенов с ролями xs/sm/md/lg/xl/full;</li>
 *   <li>литеральный радиус мимо токена запрещён — вторая точка правды о форме;</li>
 *   <li>затирания {@code border-radius: 0 !important} не возвращаются;</li>
 *   <li>пассивная плита не носит контур контрола; контур остаётся у интерактивного;</li>
 *   <li>токены снятого pixel-слоя ({@code --px-*}) не возвращаются.</li>
 * </ol>
 */
class SurfaceLanguageContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static String stripComments(String css) {
        return CSS_COMMENT.matcher(css).replaceAll("\n");
    }

    /**
     * Форма продукта — одна короткая шкала, и её значения названы здесь по ролям.
     * Дрейф любого значения — осознанное решение об идентичности, а не побочный
     * эффект правки: гейт заставляет проговорить его в двух местах сразу.
     */
    @Test
    void shapeIsOneShortTokenScale() throws IOException {
        String tokens = stripComments(read("static/css/tokens.css"));

        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("--border-radius-xs", "2px");
        expected.put("--border-radius-sm", "4px");
        expected.put("--border-radius-md", "8px");
        expected.put("--border-radius-lg", "12px");
        expected.put("--border-radius-xl", "16px");
        expected.put("--border-radius-full", "999px");

        Map<String, String> declared = new LinkedHashMap<>();
        Matcher radius = Pattern.compile("(--border-radius-[a-z]+):\\s*([^;]+);").matcher(tokens);
        while (radius.find()) {
            declared.put(radius.group(1), radius.group(2).trim());
        }
        assertThat(declared)
                .as("шкала радиусов разъехалась с контрактом формы FOLIO: правишь форму — "
                        + "правь её здесь же, одной парой изменений")
                .containsExactlyInAnyOrderEntriesOf(expected);
    }

    /**
     * Литеральный радиус мимо токена — вторая точка правды о форме.
     *
     * <p>Пойман замером в iter-119: `.avatar` бренд-знака держал {@code border-radius: 9px}
     * прямо в shell.css, и смена токенов формы его не достала.</p>
     */
    @Test
    void radiusIsNeverWrittenAsALiteral() {
        List<String> offenders = new ArrayList<>();
        for (Path css : cssFiles()) {
            String[] lines = stripComments(readFile(css)).split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                Matcher radius = Pattern.compile("border-radius:\\s*([^;]+);").matcher(lines[i]);
                while (radius.find()) {
                    String value = radius.group(1).trim();
                    boolean fromToken = value.startsWith("var(--border-radius")
                            || value.equals("0") || value.equals("inherit");
                    // 50% — легитимная идиома «идеальный круг» для квадратных боксов
                    // (маркер точки, кольцо счёта): токен full=999px даёт то же самое
                    // только для боксов с равными сторонами, а 50% честнее выражает
                    // намерение и не зависит от размеров.
                    boolean circleIdiom = value.equals("50%");
                    if (!fromToken && !circleIdiom) {
                        offenders.add(css.getFileName() + ":" + (i + 1) + " → " + value);
                    }
                }
            }
        }
        assertThat(offenders)
                .as("радиус записан числом мимо токена. Так форма получает вторую точку "
                        + "правды, и смена шкалы её не достанет")
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
                .as("радиус снова стирают через !important — форма держится на затирании, "
                        + "а не на токене")
                .isEmpty();
    }

    /**
     * Контур контрола — признак того, на что можно нажать. Пассивная плита (.card)
     * держится тоном и волоском {@code border-secondary}; рамку контрола
     * ({@code --color-control-border}) и сильную рамку ({@code border-strong}) она
     * не носит — иначе кнопка и карточка снова неразличимы по границе.
     */
    @Test
    void passiveSurfacesDoNotWearTheControlEdge() throws IOException {
        String cards = stripComments(read("static/css/components-card.css"));

        Matcher rule = Pattern.compile("html\\[data-design]\\s+\\.card\\s*\\{([^}]*)}").matcher(cards);
        assertThat(rule.find()).as("базовое правило .card исчезло из components-card.css").isTrue();
        String body = rule.group(1);
        assertThat(body)
                .as("плита надела рамку контрола — она снова неотличима от кнопки")
                .doesNotContain("--color-control-border")
                .doesNotContain("--color-border-strong");
        assertThat(body)
                .as("плита осталась без кромки: в светлой теме лист на бумаге даёт 1.03 "
                        + "тонального перепада и без волоска исчезает")
                .contains("var(--color-border-secondary)");

        // Контролы контур сохраняют: граница компонента считается по WCAG 1.4.11.
        String controls = stripComments(read("static/css/components-controls.css"));
        assertThat(controls)
                .as("поле ввода лишилось рамки контрола — граница единственное, что "
                        + "отделяет его от фона (WCAG 1.4.11)")
                .contains("var(--color-control-border)");
    }

    /** Pixel-слой снят целиком; его токены не возвращаются ни объявлением, ни чтением. */
    @Test
    void pixelLayerTokensStayGone() {
        List<String> offenders = new ArrayList<>();
        for (Path css : cssFiles()) {
            String source = stripComments(readFile(css));
            Matcher px = Pattern.compile("--px-[a-z-]+").matcher(source);
            while (px.find()) {
                offenders.add(css.getFileName() + " → " + px.group());
            }
        }
        assertThat(offenders)
                .as("токен снятого pixel-слоя вернулся в CSS — слой удалён миссией "
                        + "2026-08-29 вместе со своей палитрой и кромками")
                .isEmpty();
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
