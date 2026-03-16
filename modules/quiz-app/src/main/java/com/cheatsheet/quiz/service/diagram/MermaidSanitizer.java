package com.cheatsheet.quiz.service.diagram;

import com.cheatsheet.quiz.service.ai.util.CodeFenceConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Очистка и исправление типичных ошибок в Mermaid-коде, сгенерированном AI.
 */
@Component
public class MermaidSanitizer {

    /** Спецсимволы, требующие кавычек внутри [...] node-label в Mermaid. */
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[(),:;|<>{}]");

    /**
     * Regex для поиска node-label вида {@code ID[текст без кавычек]}.
     * Группа 1 — идентификатор ноды, группа 2 — содержимое [...].
     * Не трогает уже экранированные {@code ["..."]}.
     */
    private static final Pattern NODE_LABEL = Pattern.compile(
            "([A-Za-z_][A-Za-z0-9_]*)\\[([^\"\\]]+)]");

    /** Пустые edge-labels: {@code -->||}  или  {@code -->| |}. */
    private static final Pattern EMPTY_EDGE_LABEL = Pattern.compile("-->\\|\\s*\\|");

    /**
     * Очищает и исправляет типичные ошибки в Mermaid-коде, сгенерированном AI.
     *
     * <ol>
     *   <li>Удаляет markdown code-fences ({@code ```mermaid ... ```})</li>
     *   <li>Разбивает однострочный код по {@code ;} на отдельные строки</li>
     *   <li>Убирает пустые edge-labels ({@code -->||})</li>
     *   <li>Оборачивает node-labels с спецсимволами в кавычки: {@code A[O(n)]} -> {@code A["O(n)"]}</li>
     * </ol>
     *
     * @param raw сырой Mermaid-код от AI
     * @return очищенный код
     */
    public String sanitizeMermaid(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }

        String code = raw.trim();

        // 1. Убрать markdown code-fences
        if (code.startsWith(CodeFenceConstants.CODE_FENCE)) {
            code = code.replaceAll(CodeFenceConstants.REGEX_CODE_FENCE_LEADING, StringUtils.EMPTY)
                    .replaceAll(CodeFenceConstants.REGEX_CODE_FENCE_TRAILING, StringUtils.EMPTY)
                    .trim();
        }

        // 2. Если код на одной строке — разбить по `;`
        if (!code.contains("\n") && code.contains(";")) {
            code = code.replace(";", "\n");
        }

        // 3. Удалить пустые edge-labels: -->|| -> -->
        code = EMPTY_EDGE_LABEL.matcher(code).replaceAll("-->");

        // 4. Оборачивать node-labels со спецсимволами в кавычки
        StringBuilder sb = new StringBuilder();
        for (String line : code.split("\n")) {
            sb.append(quoteSpecialLabels(line)).append("\n");
        }
        code = sb.toString().trim();

        return code;
    }

    /**
     * Для одной строки Mermaid: находит {@code ID[label]} где label содержит спецсимволы,
     * и заменяет на {@code ID["label"]}.
     */
    public String quoteSpecialLabels(String line) {
        Matcher m = NODE_LABEL.matcher(line);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            String id = m.group(1);
            String label = m.group(2);
            if (SPECIAL_CHARS.matcher(label).find()) {
                // Экранируем: удаляем вложенные кавычки, оборачиваем в ["..."]
                String safe = label.replace("\"", "'");
                m.appendReplacement(result, Matcher.quoteReplacement(id + "[\"" + safe + "\"]"));
            } else {
                m.appendReplacement(result, Matcher.quoteReplacement(m.group()));
            }
        }
        m.appendTail(result);
        return result.toString();
    }
}
