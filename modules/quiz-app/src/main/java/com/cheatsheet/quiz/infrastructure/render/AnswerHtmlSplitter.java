package com.cheatsheet.quiz.infrastructure.render;

/**
 * Делит уже отрендеренный HTML разбора ответа на «краткий лид» и «остальное».
 *
 * <p>UX-13 (макет Instrument, {@code design/mockups/focus-question.html}, состояние
 * result): под вердиктом показывается ОДИН короткий абзац-объяснение ({@code .explain}),
 * а вся глубина — под «Подробнее». В данных нет отдельного поля «краткое почему», поэтому
 * лид деривируется на сервере как ПЕРВЫЙ параграф {@code <p>…</p>} ответа (первый абзац
 * .md-разбора — чистый, самодостаточный вывод; подтверждено на корпусе сидеров). Остаток —
 * всё, что после первого абзаца.</p>
 *
 * <p>Чисто строковая операция над безопасным (уже отсанитайзенным {@code MarkdownRenderService})
 * HTML: держим util без зависимости на domain, чтобы обе точки (API {@code AnswerApiService} и
 * SSR {@code MvcModelAttributeMapper}) звали одинаково.</p>
 */
public final class AnswerHtmlSplitter {

    private AnswerHtmlSplitter() {
    }

    /**
     * @param leadHtml первый {@code <p>…</p>} (или пустая строка, если чистого абзаца-лида нет)
     * @param restHtml остаток разбора (или весь HTML, если лид не выделился)
     */
    public record Split(String leadHtml, String restHtml) {
    }

    public static Split split(String html) {
        if (html == null || html.isBlank()) {
            return new Split("", "");
        }
        int pStart = firstParagraphStart(html);
        if (pStart < 0 || !html.substring(0, pStart).isBlank()) {
            // Разбор не начинается с абзаца (сразу <ul>/<pre>/<h3>/таблица) —
            // выделять «лид» неоткуда, а выдёргивать <p> из середины нельзя
            // (переставит контент). Всё уходит в остаток; лид покажет фолбэк
            // (весь answerHtml), вердикт при этом стоит один.
            return new Split("", html.trim());
        }
        int pEnd = indexOfIgnoreCase(html, "</p>", pStart);
        if (pEnd < 0) {
            return new Split("", html.trim());
        }
        pEnd += "</p>".length();
        String lead = html.substring(pStart, pEnd).trim();
        String rest = (html.substring(0, pStart) + html.substring(pEnd)).trim();
        return new Split(lead, rest);
    }

    /**
     * Индекс начала первого настоящего тега {@code <p>} / {@code <p …>}.
     * Пропускает {@code <pre>}, {@code <picture>} и т.п. (символ после {@code <p}
     * должен быть {@code >}, пробелом или {@code /}).
     */
    private static int firstParagraphStart(String html) {
        int from = 0;
        while (true) {
            int idx = indexOfIgnoreCase(html, "<p", from);
            if (idx < 0) {
                return -1;
            }
            int after = idx + 2;
            if (after >= html.length()) {
                return -1;
            }
            char c = html.charAt(after);
            if (c == '>' || c == '/' || Character.isWhitespace(c)) {
                return idx;
            }
            from = after;
        }
    }

    private static int indexOfIgnoreCase(String haystack, String needle, int from) {
        int max = haystack.length() - needle.length();
        for (int i = Math.max(from, 0); i <= max; i++) {
            if (haystack.regionMatches(true, i, needle, 0, needle.length())) {
                return i;
            }
        }
        return -1;
    }
}
