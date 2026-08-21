package com.cheatsheet.quiz.ui;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Разбор таблицы стилей в плоский список правил — чтобы гейты проверяли ПРАВИЛО, а не
 * его написание.
 *
 * <p>Две ловушки, найденные укусами и замером, зашиты здесь:</p>
 * <ol>
 *   <li>файл целиком лежит в `@layer`, и разбор обязан спускаться внутрь любых
 *       блочных at-правил — иначе не находится вообще ничего;</li>
 *   <li>список селекторов делится запятыми ВЕРХНЕГО уровня: у `:not(:has(a, b, c))`
 *       запятые внутри скобок, и наивный `split(",")` рвёт селектор на куски.</li>
 * </ol>
 */
final class CssRules {

    private static final Pattern COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /** Правило вместе с медиаусловием, в котором оно лежит, и позицией в файле. */
    record Rule(int start, String media, String selectors, String body) {

        List<String> selectorList() {
            List<String> parts = new ArrayList<>();
            int depth = 0;
            StringBuilder current = new StringBuilder();
            for (char c : selectors.toCharArray()) {
                if (c == '(') depth++;
                if (c == ')') depth--;
                if (c == ',' && depth == 0) {
                    parts.add(current.toString().trim());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
            parts.add(current.toString().trim());
            return parts;
        }

        boolean hasSelector(String exact) {
            return selectorList().stream().anyMatch(s -> s.equals(exact));
        }

        /** Значение свойства в теле правила или {@code null}. Последнее объявление побеждает. */
        String value(String property) {
            java.util.regex.Matcher m = Pattern.compile("(?:^|;)\\s*" + Pattern.quote(property) + "\\s*:([^;]+)")
                    .matcher(body);
            String found = null;
            while (m.find()) found = m.group(1).trim();
            return found;
        }
    }

    private CssRules() {
    }

    static String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    static String stripComments(String css) {
        return COMMENT.matcher(css).replaceAll("");
    }

    static List<Rule> parse(String classpathLocation) throws IOException {
        String css = stripComments(read(classpathLocation));
        List<Rule> rules = new ArrayList<>();
        collect(css, 0, css.length(), null, rules);
        return rules;
    }

    private static void collect(String css, int from, int to, String media, List<Rule> out) {
        int i = from;
        while (i < to) {
            int brace = css.indexOf('{', i);
            if (brace < 0 || brace >= to) return;
            int end = closing(css, brace);
            String prelude = css.substring(i, brace).trim();
            if (prelude.startsWith("@")) {
                collect(css, brace + 1, end, prelude.startsWith("@media") ? prelude : media, out);
            } else {
                out.add(new Rule(brace, media, prelude, css.substring(brace + 1, end)));
            }
            i = end + 1;
        }
    }

    private static int closing(String css, int open) {
        int depth = 0;
        for (int i = open; i < css.length(); i++) {
            if (css.charAt(i) == '{') depth++;
            if (css.charAt(i) == '}' && --depth == 0) return i;
        }
        throw new AssertionError("незакрытый блок в таблице стилей");
    }
}
