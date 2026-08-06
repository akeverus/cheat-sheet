package com.cheatsheet.quiz.service.diagram;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Переводит захардкоженные цвета Mermaid-диаграмм на семантические классы.
 *
 * <p>Диаграммы писались вручную в разное время и красились «на глаз»: в корпусе
 * набралось больше тридцати разных hex-значений, причём почти все — светлые
 * пастельные заливки в расчёте на дефолтную светлую тему Mermaid. Приложение
 * рисует диаграммы своей темой, собранной из design-токенов
 * ({@code readThemeVars()} в mermaid-renderer.js), и она переключается вместе со
 * светлой/тёмной темой. Строки {@code style A fill:#e8f5e9} этой темы не знают:
 * они перебивают её инлайн-стилем на узле. Итог до нормализации — часть диаграмм
 * радужная и не по палитре, а в тёмной теме светлая заливка остаётся светлой,
 * тогда как подпись берёт цвет из токенов и становится нечитаемой.</p>
 *
 * <p>Поэтому цвет из диаграммы убирается, но не выбрасывается: красный/зелёный/
 * жёлтый в этих графах несут смысл («ошибка», «успех», «предупреждение»), и просто
 * обесцветить диаграмму значило бы потерять информацию. Вместо этого оттенок
 * классифицируется в один из шести семантических классов, а конкретные цвета класса
 * подставляет фронтенд из живых токенов — уже с учётом текущей темы.</p>
 *
 * <p>Трогаются только операторы {@code style} и {@code classDef}: {@code #} в корпусе
 * встречается и в подписи ребра ({@code -->|"#access_token"|}), которую менять нельзя.</p>
 */
@Component
public class DiagramPaletteNormalizer {

    /** Префикс имён классов, которые определяет фронтенд из токенов темы. */
    private static final String CLASS_PREFIX = "dgm";

    /**
     * {@code style <id> <декларации>} — покраска одного узла.
     * Идентификаторы узлов в корпусе — латиница/цифры/подчёркивание.
     */
    private static final Pattern STYLE_STATEMENT = Pattern.compile(
            "^style\\s+([A-Za-z0-9_]+)\\s+(.+)$");

    /** {@code classDef <имя> <декларации>} — покраска именованного класса. */
    private static final Pattern CLASS_DEF_STATEMENT = Pattern.compile(
            "^classDef\\s+([A-Za-z0-9_-]+)\\s+(.+)$");

    /** {@code class <id>[,<id>...] <имя-класса>} — назначение класса узлам. */
    private static final Pattern CLASS_STATEMENT = Pattern.compile(
            "^class\\s+([A-Za-z0-9_,\\s]+?)\\s+([A-Za-z0-9_-]+)$");

    /** {@code fill:#rgb} / {@code fill:#rrggbb} внутри деклараций. */
    private static final Pattern FILL_COLOR = Pattern.compile(
            "fill\\s*:\\s*#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b");

    /** {@code stroke:#...} — запасной источник оттенка, если заливки нет. */
    private static final Pattern STROKE_COLOR = Pattern.compile(
            "stroke\\s*:\\s*#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b");

    /**
     * Семантические классы в порядке оттенка. Границы подобраны по фактическому
     * корпусу: например {@code #e1f5fe} (Material Light Blue 50, hue ≈ 199) должен
     * попасть в info, а бирюзовый {@code #4ecdc4} (hue ≈ 176) — в accent, поэтому
     * граница между ними стоит на 195°, а не на «круглых» 200°. Красно-оранжевая
     * граница на 15°, а не на 20°, по той же причине: коралловый {@code #f96} даёт
     * ровно 20°, и на «круглой» границе его судьбу решала бы ошибка округления.
     */
    private static final List<HueBand> HUE_BANDS = List.of(
            new HueBand(15, "Danger"),
            new HueBand(70, "Warning"),
            new HueBand(165, "Success"),
            new HueBand(195, "Accent"),
            new HueBand(265, "Info"),
            new HueBand(330, "Accent"),
            new HueBand(360, "Danger"));

    /** Ниже этой насыщенности оттенок ничего не значит: серый, белый, чёрный. */
    private static final double NEUTRAL_SATURATION = 0.15;

    /**
     * @param diagram исходный код диаграммы из markdown (может быть {@code null})
     * @return тот же код с {@code style}/{@code classDef}, заменёнными на
     *         семантические {@code class}-назначения; вход возвращается без
     *         изменений, если красить нечего
     */
    public String normalize(String diagram) {
        if (StringUtils.isBlank(diagram) || diagram.indexOf('#') < 0) {
            return diagram;
        }

        Map<String, String> renamedClasses = new HashMap<>();
        List<String> lines = diagram.lines().toList();
        StringBuilder out = new StringBuilder(diagram.length());
        boolean changed = false;

        for (String line : lines) {
            String rewritten = rewriteColorStatement(line, renamedClasses);
            changed |= rewritten != null;
            if (rewritten != null) {
                if (!rewritten.isEmpty()) {
                    out.append(rewritten).append('\n');
                }
            } else {
                out.append(line).append('\n');
            }
        }

        if (!changed) {
            return diagram;
        }

        String result = out.toString().stripTrailing();
        return renamedClasses.isEmpty() ? result : retargetClassStatements(result, renamedClasses);
    }

    /**
     * @return новая строка, пустая строка (оператор надо удалить) или {@code null},
     *         если строку трогать не нужно
     */
    private String rewriteColorStatement(String line, Map<String, String> renamedClasses) {
        String indent = line.substring(0, line.length() - StringUtils.stripStart(line, null).length());
        String body = StringUtils.stripEnd(line.strip(), ";").strip();

        Matcher style = STYLE_STATEMENT.matcher(body);
        if (style.matches()) {
            String semantic = classify(style.group(2));
            return semantic == null ? null : indent + "class " + style.group(1) + " " + semantic;
        }

        Matcher classDef = CLASS_DEF_STATEMENT.matcher(body);
        if (classDef.matches()) {
            String semantic = classify(classDef.group(2));
            if (semantic == null) {
                return null;
            }
            // Определение класса удаляем целиком, а его имя запоминаем: ссылки
            // `class X <имя>` ниже надо перенацелить на семантический класс,
            // иначе они укажут на исчезнувший classDef.
            renamedClasses.put(classDef.group(1), semantic);
            return StringUtils.EMPTY;
        }

        return null;
    }

    /** Перенацеливает {@code class X oldName} на семантический класс. */
    private String retargetClassStatements(String diagram, Map<String, String> renamedClasses) {
        StringBuilder out = new StringBuilder(diagram.length());
        for (String line : diagram.lines().toList()) {
            String indent = line.substring(0, line.length() - StringUtils.stripStart(line, null).length());
            Matcher m = CLASS_STATEMENT.matcher(StringUtils.stripEnd(line.strip(), ";").strip());
            String semantic = m.matches() ? renamedClasses.get(m.group(2)) : null;
            out.append(semantic == null ? line : indent + "class " + m.group(1) + " " + semantic)
                    .append('\n');
        }
        return out.toString().stripTrailing();
    }

    /**
     * @return имя семантического класса или {@code null}, если в декларациях нет
     *         hex-цвета (например {@code style A stroke-width:2px} — такой оператор
     *         теме не мешает и остаётся как есть)
     */
    private String classify(String declarations) {
        Matcher fill = FILL_COLOR.matcher(declarations);
        if (fill.find()) {
            return CLASS_PREFIX + bandOf(expand(fill.group(1)));
        }
        Matcher stroke = STROKE_COLOR.matcher(declarations);
        return stroke.find() ? CLASS_PREFIX + bandOf(expand(stroke.group(1))) : null;
    }

    /** {@code abc} → {@code aabbcc}; шестизначная запись возвращается как есть. */
    private String expand(String hex) {
        if (hex.length() == 6) {
            return hex.toLowerCase(Locale.ROOT);
        }
        StringBuilder sb = new StringBuilder(6);
        for (char c : hex.toLowerCase(Locale.ROOT).toCharArray()) {
            sb.append(c).append(c);
        }
        return sb.toString();
    }

    private String bandOf(String hex) {
        double r = Integer.parseInt(hex.substring(0, 2), 16) / 255.0;
        double g = Integer.parseInt(hex.substring(2, 4), 16) / 255.0;
        double b = Integer.parseInt(hex.substring(4, 6), 16) / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double lightness = (max + min) / 2;
        double saturation = delta == 0 ? 0 : delta / (1 - Math.abs(2 * lightness - 1));
        if (saturation < NEUTRAL_SATURATION) {
            return "Neutral";
        }

        double hue;
        if (max == r) {
            hue = 60 * (((g - b) / delta) % 6);
        } else if (max == g) {
            hue = 60 * ((b - r) / delta + 2);
        } else {
            hue = 60 * ((r - g) / delta + 4);
        }
        if (hue < 0) {
            hue += 360;
        }

        for (HueBand band : HUE_BANDS) {
            if (hue < band.upperBound()) {
                return band.semantic();
            }
        }
        return "Danger";
    }

    /** Полуинтервал оттенков {@code [предыдущая граница, upperBound)}. */
    private record HueBand(double upperBound, String semantic) {}
}
