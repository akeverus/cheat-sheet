package com.cheatsheet.quiz.service.diagram;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Валидация структурной корректности Mermaid-кода.
 */
@Component
public class MermaidValidator {

    /** Допустимые типы Mermaid-диаграмм (первая строка). */
    public static final Set<String> VALID_DIAGRAM_TYPES = Set.of(
            "flowchart", "graph", "sequencediagram", "classdiagram",
            "statediagram", "statediagram-v2", "erdiagram", "pie", "gantt"
    );

    /** Минимальное количество строк (объявление + хотя бы 2 узла/связи). */
    public static final int MIN_LINES = 3;

    /** Максимальное количество строк (ограничение сложности). */
    public static final int MAX_LINES = 30;
    private static final int MIN_CONTEXT_TOKEN_OVERLAP = 2;
    private static final Pattern NODE_LABEL_PATTERN = Pattern.compile("\\[(.*?)\\]");
    private static final Pattern EDGE_LABEL_PATTERN = Pattern.compile("\\|([^|]{2,})\\|");
    private static final Pattern NON_WORD = Pattern.compile("[^a-zA-Z0-9а-яА-Я_+#]+");
    private static final Set<String> GENERIC_LABEL_TOKENS = Set.of(
            "обработка", "логика", "этап", "процесс", "данные", "ответ", "запрос", "шаг"
    );

    /**
     * Проверяет структурную корректность Mermaid-кода.
     *
     * @param code очищенный Mermaid-код
     * @return true, если код выглядит структурно корректным
     */
    public boolean isValidMermaid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return validateLineCount(code) && validateDiagramType(code) && validateBalance(code);
    }

    /**
     * Проверяет, что диаграмма не только синтаксически корректна, но и
     * отражает предметный контекст вопроса.
     */
    public boolean isRelevantMermaid(String code, String questionText, String answerText) {
        if (!isValidMermaid(code)) {
            return false;
        }
        Set<String> contextTokens = tokenize(questionText + " " + answerText);
        if (contextTokens.isEmpty()) {
            return true;
        }
        Set<String> diagramTokens = tokenize(extractDiagramLabels(code));
        if (diagramTokens.isEmpty()) {
            return false;
        }

        int overlap = 0;
        for (String token : diagramTokens) {
            if (contextTokens.contains(token)) {
                overlap++;
            }
        }

        int genericOnly = 0;
        for (String token : diagramTokens) {
            if (GENERIC_LABEL_TOKENS.contains(token)) {
                genericOnly++;
            }
        }
        boolean mostlyGeneric = genericOnly > 0 && genericOnly == diagramTokens.size();
        return overlap >= MIN_CONTEXT_TOKEN_OVERLAP && !mostlyGeneric;
    }

    /**
     * Проверяет, что количество непустых строк в пределах допустимого.
     */
    public boolean validateLineCount(String code) {
        String[] lines = code.split("\n");
        long nonEmptyLines = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                nonEmptyLines++;
            }
        }
        return nonEmptyLines >= MIN_LINES && nonEmptyLines <= MAX_LINES;
    }

    /**
     * Проверяет, что первая непустая строка начинается с валидного типа диаграммы.
     */
    public boolean validateDiagramType(String code) {
        String firstLine = getFirstNonEmptyLine(code);
        if (firstLine.isEmpty()) {
            return false;
        }
        String lower = firstLine.trim().toLowerCase();
        for (String type : VALID_DIAGRAM_TYPES) {
            if (lower.startsWith(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет сбалансированность кавычек и квадратных скобок.
     */
    public boolean validateBalance(String code) {
        int quotes = 0;
        int brackets = 0;
        for (char c : code.toCharArray()) {
            if (c == '"') {
                quotes++;
            }
            if (c == '[') {
                brackets++;
            }
            if (c == ']') {
                brackets--;
            }
        }
        return quotes % 2 == 0 && brackets == 0;
    }

    private static String getFirstNonEmptyLine(String code) {
        for (String line : code.split("\n")) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return "";
    }

    private static String extractDiagramLabels(String code) {
        StringBuilder labels = new StringBuilder();
        Matcher nodeMatcher = NODE_LABEL_PATTERN.matcher(code);
        while (nodeMatcher.find()) {
            labels.append(' ').append(nodeMatcher.group(1));
        }
        Matcher edgeMatcher = EDGE_LABEL_PATTERN.matcher(code);
        while (edgeMatcher.find()) {
            labels.append(' ').append(edgeMatcher.group(1));
        }
        return labels.toString();
    }

    private static Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        String normalized = NON_WORD.matcher(text.toLowerCase(Locale.ROOT)).replaceAll(" ").trim();
        if (normalized.isBlank()) {
            return Set.of();
        }
        String[] parts = normalized.split("\\s+");
        Set<String> tokens = new HashSet<>();
        for (String part : parts) {
            if (part.length() >= 4) {
                tokens.add(part);
            }
        }
        return tokens;
    }
}
