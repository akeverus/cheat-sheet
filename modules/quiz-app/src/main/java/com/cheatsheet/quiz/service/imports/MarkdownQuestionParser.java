package com.cheatsheet.quiz.service.imports;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import org.apache.commons.lang3.StringUtils;
import com.cheatsheet.quiz.common.util.TextUtils;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import lombok.Builder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсер markdown-файлов с вопросами для собеседований.
 *
 * <p>Ожидаемый формат файла:</p>
 * <pre>
 * ## Q1. Что такое Spring Boot?
 * Spring Boot — это фреймворк...
 *
 * ## Q2 Что такое DI? (!)
 * Dependency Injection — это паттерн...
 * </pre>
 *
 * <p>Поддерживаются номера с точкой и без ({@code Q1.} и {@code Q1}).
 * Маркер {@code (!)} (или legacy {@code (ВАЖНО)}) определяет приоритетность вопроса.</p>
 *
 * <p>MCQ-варианты ответа больше не парсятся из markdown — они загружаются из
 * seed-JSON через {@code McqJsonLoader}. Если в файле найден legacy
 * {@code > [!mcq]} callout, парсер выдаст warning-лог.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkdownQuestionParser {

    /** Паттерн заголовка вопроса: {@code ## Q<число>[.] <текст>}. */
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^##\\s+Q(\\d+)\\.?\\s*(.*)$");

    /** Regex для извлечения code block: {@code ```lang ... ```}. */
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[a-zA-Z]*\\s*([\\s\\S]*?)```", Pattern.MULTILINE);

    /** Regex для удаления устаревшего маркера важности (case-insensitive): (важно). */
    private static final String REGEX_LEGACY_IMPORTANT = "(?iu)\\(важно\\)";

    /** Суффикс markdown-файла. */
    public static final String MARKDOWN_FILE_SUFFIX = ".md";

    /** Regex: расширение .md в конце строки. */
    public static final String REGEX_MARKDOWN_EXTENSION = "\\.md$";

    /** Основной маркер важности/избранного. */
    public static final String IMPORTANT_MARKER = "(!)";

    /** Устаревший маркер важности (backward compatibility). */
    private static final String LEGACY_IMPORTANT_MARKER = "(ВАЖНО)";

    private final AppProperties appProperties;

    /**
     * Парсит markdown-файл и возвращает список вопросов.
     *
     * @param filePath путь к markdown-файлу
     * @return список распарсенных вопросов
     * @throws IOException при ошибке чтения файла
     */
    public List<ParsedQuestion> parse(Path filePath) throws IOException {
        // NUL-байты (0x00) недопустимы в PostgreSQL text/UTF8: один битый байт в одном
        // cheatsheet ронял весь импорт (а с ним и старт приложения). Чистим на входе —
        // все производные поля (вопрос, ответ, код, хеш) получаются уже без NUL.
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8)
                .stream()
                .map(TextUtils::stripNulChars)
                .toList();

        boolean hasLegacyMcq = lines.stream().anyMatch(l -> l.trim().startsWith("> [!mcq]"));
        if (hasLegacyMcq) {
            log.warn("Legacy `> [!mcq]` callout found in {} — MCQ must be in seed JSON now",
                    filePath.getFileName());
        }

        List<ParsedQuestion> questions = new ArrayList<>();

        ParsedQuestionBuilder current = null;
        StringBuilder answer = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            Matcher matcher = QUESTION_PATTERN.matcher(trimmed);

            if (matcher.matches()) {
                if (current != null) {
                    current.answerMarkdown(answer.toString().trim());
                    questions.add(current.build(filePath));
                }
                answer.setLength(0);
                String number = matcher.group(1);
                String title = matcher.group(2);
                boolean important = title.contains(IMPORTANT_MARKER)
                        || title.toUpperCase().contains(LEGACY_IMPORTANT_MARKER.toUpperCase());
                String questionText = normalizeQuestionText(title);
                current = new ParsedQuestionBuilder(number, questionText, important);
                continue;
            }

            if (current != null) {
                answer.append(line).append('\n');
            }
        }

        if (current != null) {
            current.answerMarkdown(answer.toString().trim());
            questions.add(current.build(filePath));
        }

        return questions;
    }

    /**
     * Нормализует текст вопроса: убирает маркеры {@code (!)} и {@code (ВАЖНО)},
     * markdown-разметку и лишние пробелы.
     */
    private String normalizeQuestionText(String title) {
        String normalized = title.replace(IMPORTANT_MARKER, StringUtils.EMPTY);
        normalized = normalized.replaceAll(REGEX_LEGACY_IMPORTANT, StringUtils.EMPTY);
        normalized = TextUtils.stripMarkdown(normalized);
        return normalized.replaceAll("\\s+", " ").trim();
    }

    /**
     * Распарсенный вопрос из markdown-файла.
     *
     * @param questionNumber номер вопроса (строка, например {@code "1"})
     * @param questionText   текст вопроса (без маркеров и номера)
     * @param answerMarkdown полный текст ответа в markdown
     * @param rawAnswer      сырой текст ответа (для хеширования; совпадает с answerMarkdown)
     * @param important      {@code true} если помечен как ВАЖНО
     * @param questionType   тип вопроса (TEXT или CODE)
     * @param codeSnippet    первый блок кода из ответа (для типа CODE)
     */
    @Builder(toBuilder = true)
    public record ParsedQuestion(
            String questionNumber,
            String questionText,
            String answerMarkdown,
            String rawAnswer,
            boolean important,
            QuestionType questionType,
            String codeSnippet
    ) {}

    /**
     * Определяет тип вопроса и извлекает блок кода из answer_markdown.
     * CODE — если есть блок ``` длиной > 50 символов.
     */
    private CodeExtractionResult extractCodeAndType(String answerMarkdown) {
        if (answerMarkdown == null || answerMarkdown.isBlank()) {
            return new CodeExtractionResult(QuestionType.TEXT, null);
        }
        Matcher m = CODE_BLOCK_PATTERN.matcher(answerMarkdown);
        if (m.find()) {
            String code = m.group(1).trim();
            int minLen = appProperties.getImportSettings().getMinCodeBlockLength();
            if (code.length() >= minLen) {
                return new CodeExtractionResult(QuestionType.CODE, code);
            }
        }
        return new CodeExtractionResult(QuestionType.TEXT, null);
    }

    @Builder(toBuilder = true)
    private record CodeExtractionResult(QuestionType type, String codeSnippet) {}

    /** Внутренний builder для ParsedQuestion (для накопления answer). */
    private class ParsedQuestionBuilder {
        private final String questionNumber;
        private final String questionText;
        private final boolean important;
        private String answerMarkdown;

        ParsedQuestionBuilder(String questionNumber, String questionText, boolean important) {
            this.questionNumber = questionNumber;
            this.questionText = questionText;
            this.important = important;
        }

        void answerMarkdown(String answerMarkdown) {
            this.answerMarkdown = answerMarkdown == null ? "" : answerMarkdown.trim();
        }

        ParsedQuestion build(Path sourceFile) {
            CodeExtractionResult extraction = extractCodeAndType(answerMarkdown);
            return new ParsedQuestion(
                    questionNumber, questionText,
                    answerMarkdown,
                    answerMarkdown,
                    important,
                    extraction.type(), extraction.codeSnippet());
        }
    }
}
