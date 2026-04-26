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
import java.util.Collections;
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
 * > [!mcq]
 * > - [ ] Вариант A | Объяснение
 * > - [x] Вариант B (правильный) | Объяснение
 * > - [ ] Вариант C | Объяснение
 * > - [ ] Вариант D | Объяснение
 *
 * ## Q2 Что такое DI? (ВАЖНО)
 * Dependency Injection — это паттерн...
 * </pre>
 *
 * <p>Поддерживаются номера с точкой и без ({@code Q1.} и {@code Q1}).
 * Маркер {@code (ВАЖНО)} определяет приоритетность вопроса.
 * Блок {@code > [!mcq]} — варианты ответа (опционально): парсируются в {@link ParsedOption}
 * и исключаются из хранимого {@code answer_markdown}.</p>
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

    /** Regex начала MCQ callout-блока: {@code > [!mcq]}. */
    private static final Pattern MCQ_CALLOUT_START = Pattern.compile("^>\\s*\\[!mcq\\]\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * Regex строки опции MCQ:
     * {@code > - [ ] text} или {@code > - [x] text} с необязательным {@code | explanation}.
     */
    private static final Pattern MCQ_OPTION_LINE =
            Pattern.compile("^>\\s*-\\s*\\[([ xX])\\]\\s*(.+?)(?:\\s*\\|\\s*(.+?))?\\s*$");

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
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
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
     * Вариант ответа, распарсенный из {@code > [!mcq]} блока.
     *
     * @param text        текст варианта (inline markdown допустим)
     * @param correct     {@code true} если это правильный вариант {@code [x]}
     * @param explanation объяснение после {@code |} (может быть null)
     */
    public record ParsedOption(String text, boolean correct, String explanation) {}

    /**
     * Распарсенный вопрос из markdown-файла.
     *
     * @param questionNumber номер вопроса (строка, например {@code "1"})
     * @param questionText   текст вопроса (без маркеров и номера)
     * @param answerMarkdown полный текст ответа в markdown (без MCQ блока)
     * @param rawAnswer      полный текст ответа включая MCQ блок (для хеширования)
     * @param important      {@code true} если помечен как ВАЖНО
     * @param questionType   тип вопроса (TEXT или CODE)
     * @param codeSnippet    первый блок кода из ответа (для типа CODE)
     * @param options        варианты ответа из {@code > [!mcq]} блока (пустой список если нет)
     */
    @Builder(toBuilder = true)
    public record ParsedQuestion(
            String questionNumber,
            String questionText,
            String answerMarkdown,
            String rawAnswer,
            boolean important,
            QuestionType questionType,
            String codeSnippet,
            List<ParsedOption> options
    ) {
        public boolean hasMcqOptions() {
            return options != null && !options.isEmpty();
        }
    }

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

    /**
     * Извлекает первый {@code > [!mcq]} блок из rawAnswer.
     *
     * <p>Если в одном вопросе встречается несколько {@code > [!mcq]} блоков подряд,
     * учитывается только первый — остальные пропускаются с warning-логом.
     * Поддержка нескольких блоков нарушила бы партиальный unique index
     * {@code uq_answer_options_single_correct_per_question} в БД (V13). Авторам
     * cheatsheet-ов следует разнести такие наборы по отдельным {@code ## QN} заголовкам.</p>
     *
     * @param rawAnswer текст ответа с возможным MCQ блоком
     * @param contextDescription человекочитаемое описание места парсинга для warning-лога
     */
    private McqParseResult extractMcqFromAnswer(String rawAnswer, String contextDescription) {
        if (rawAnswer == null || rawAnswer.isBlank()) {
            return new McqParseResult(rawAnswer == null ? "" : rawAnswer, Collections.emptyList());
        }

        String[] lines = rawAnswer.split("\n", -1);
        List<ParsedOption> options = new ArrayList<>();
        List<String> answerLines = new ArrayList<>();
        boolean inMcq = false;
        boolean firstBlockCaptured = false;
        int extraBlocksSkipped = 0;

        for (String line : lines) {
            String trimmed = line.trim();

            if (!inMcq && MCQ_CALLOUT_START.matcher(trimmed).matches()) {
                if (firstBlockCaptured) {
                    extraBlocksSkipped++;
                }
                inMcq = true;
                continue;
            }

            if (inMcq) {
                Matcher optMatcher = MCQ_OPTION_LINE.matcher(trimmed);
                if (optMatcher.matches()) {
                    if (!firstBlockCaptured) {
                        boolean correct = optMatcher.group(1).equalsIgnoreCase("x");
                        String text = optMatcher.group(2).trim();
                        String explanation = optMatcher.group(3) != null ? optMatcher.group(3).trim() : null;
                        options.add(new ParsedOption(text, correct, explanation));
                    }
                    continue;
                }
                if (trimmed.startsWith(">")) {
                    continue;
                }
                // Non-callout line — MCQ block ended
                inMcq = false;
                if (!options.isEmpty()) {
                    firstBlockCaptured = true;
                }
                if (!trimmed.isEmpty()) {
                    answerLines.add(line);
                }
            } else {
                answerLines.add(line);
            }
        }

        if (extraBlocksSkipped > 0) {
            log.warn("{}: пропущено {} дополнительных [!mcq] блоков, учтён только первый. "
                    + "Разнесите варианты по отдельным '## QN' заголовкам.",
                    contextDescription, extraBlocksSkipped);
        }

        // Strip trailing blank lines
        while (!answerLines.isEmpty() && answerLines.get(answerLines.size() - 1).isBlank()) {
            answerLines.remove(answerLines.size() - 1);
        }

        return new McqParseResult(String.join("\n", answerLines), options);
    }

    @Builder(toBuilder = true)
    private record CodeExtractionResult(QuestionType type, String codeSnippet) {}

    private record McqParseResult(String answerWithoutMcq, List<ParsedOption> options) {}

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
            String fileName = sourceFile == null ? "<unknown>" : sourceFile.getFileName().toString();
            String context = fileName + "#Q" + questionNumber;
            McqParseResult mcq = extractMcqFromAnswer(answerMarkdown, context);
            CodeExtractionResult extraction = extractCodeAndType(mcq.answerWithoutMcq());
            return new ParsedQuestion(
                    questionNumber, questionText,
                    mcq.answerWithoutMcq(),
                    answerMarkdown,
                    important,
                    extraction.type(), extraction.codeSnippet(),
                    mcq.options());
        }
    }
}
