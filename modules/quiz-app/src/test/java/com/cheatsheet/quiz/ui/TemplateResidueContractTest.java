package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ни в одном шаблоне нет осиротевшего th:-атрибута вне тега.
 *
 * <h2>Реальный инцидент (iter-178)</h2>
 * После инструментального «укуса» гейта в head.html остался обломок строки
 * {@code th:href="@{/fonts/PROTUX.woff2}">} — хвост тега без начала. Thymeleaf
 * отдал его как текст, а текстовый узел в {@code <head>} по правилам HTML-парсинга
 * ЗАКРЫВАЕТ head: CSS-бандл, preload'ы и скрипты уехали в {@code <body>}, а мусор
 * стал первой видимой строкой КАЖДОЙ страницы. Все 2232 JVM-теста остались
 * зелёными — точечные гейты ищут свои конструкции и не видят лишнего текста.
 *
 * <h2>Как ловим класс, а не случай</h2>
 * Пофайлово: комментарии {@code <!-- ... -->} вырезаются, текст разбирается
 * посимвольно с отслеживанием «внутри тега / снаружи», и в тексте ВНЕ тегов
 * ищется th:-атрибут ({@code th:xxx="}). Легитимные многострочные теги не
 * флажатся (их атрибуты — внутри незакрытого тега), а inline-JS вроде
 * {@code readingWidth:} не матчится благодаря границе слова перед th:.
 */
class TemplateResidueContractTest {

    private static final Pattern ORPHAN_TH_ATTR = Pattern.compile("(?<![A-Za-z-])th:[a-z-]+=\"");
    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    @Test
    void noTemplateLeaksARawThymeleafAttributeOutsideATag() throws IOException {
        Resource[] templates = new PathMatchingResourcePatternResolver()
                .getResources("classpath:templates/**/*.html");
        assertThat(templates).as("шаблоны не найдены — сканер смотрит не туда").isNotEmpty();

        List<String> leaks = new ArrayList<>();
        for (Resource template : templates) {
            String raw = new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String withoutComments = HTML_COMMENT.matcher(raw).replaceAll("");
            String outsideTags = textOutsideTags(withoutComments);
            Matcher leak = ORPHAN_TH_ATTR.matcher(outsideTags);
            if (leak.find()) {
                int start = Math.max(0, leak.start() - 40);
                leaks.add(template.getFilename() + ": …"
                        + outsideTags.substring(start, Math.min(outsideTags.length(), leak.end() + 40))
                        .replace('\n', ' ') + "…");
            }
        }
        assertThat(leaks)
                .as("сырой th:-атрибут вне тега уходит в отдачу текстом и выталкивает <head> в <body>")
                .isEmpty();
    }

    private String textOutsideTags(String html) {
        StringBuilder text = new StringBuilder(html.length());
        boolean inTag = false;
        char quote = 0;
        for (int i = 0; i < html.length(); i++) {
            char c = html.charAt(i);
            if (inTag) {
                if (quote != 0) {
                    if (c == quote) quote = 0;
                } else if (c == '"' || c == '\'') {
                    quote = c;
                } else if (c == '>') {
                    inTag = false;
                }
            } else if (c == '<') {
                inTag = true;
                quote = 0;
                text.append(' ');
            } else {
                text.append(c);
            }
        }
        return text.toString();
    }
}
