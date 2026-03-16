package com.cheatsheet.quiz.domain;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

/**
 * Вопрос для квиза (импортированный из markdown-файла).
 *
 * @param id              автоинкрементный идентификатор (PK)
 * @param slug            уникальный ключ (путь к файлу + номер вопроса, например {@code "java.md#Q1"})
 * @param sourceSlug      общий идентификатор источника (для вариантов из одного ParsedQuestion)
 * @param filePath        относительный путь к markdown-файлу
 * @param topic           тема (определяется из имени файла)
 * @param questionText    текст вопроса
 * @param answerMarkdown  полный текст ответа в markdown
 * @param important       {@code true} если вопрос помечен как ВАЖНО в markdown
 * @param sourceHash      SHA-256 хеш контента (для обнаружения изменений при реимпорте)
 * @param questionType    тип вопроса (TEXT или CODE)
 * @param codeSnippet     фрагмент кода для типа CODE (nullable)
 * @param diagramMermaid  Mermaid-код диаграммы для визуализации (nullable)
 * @param regenCount      счётчик перезагрузок вариантов ответа (для адаптивной AI-сложности)
 * @param takeaway        ключевой вывод — "Главное, что нужно запомнить" (nullable, AI-generated)
 * @param difficulty      целевой уровень сложности
 * @param shortExplanation краткое объяснение (2-4 предложения)
 * @param detailedExplanation подробный разбор
 * @param commonMistake   типичная ошибка
 * @param tags            теги вопроса
 * @param options         варианты ответа для LLM-сгенерированного вопроса
 */
@Builder(toBuilder = true)
public record Question(
        long id,
        String slug,
        String sourceSlug,
        String filePath,
        String topic,
        String questionText,
        String answerMarkdown,
        boolean important,
        String sourceHash,
        QuestionType questionType,
        String codeSnippet,
        String diagramMermaid,
        int regenCount,
        String takeaway,
        Difficulty difficulty,
        String shortExplanation,
        String detailedExplanation,
        String commonMistake,
        List<String> tags,
        List<QuestionOption> options
) {
    /**
     * Compact-конструктор с валидацией обязательных полей.
     */
    public Question {
        Objects.requireNonNull(slug, "slug не может быть null");
        Objects.requireNonNull(questionText, "questionText не может быть null");
        difficulty = difficulty == null ? Difficulty.MEDIUM : difficulty;
        tags = tags == null ? List.of() : List.copyOf(tags);
        options = options == null ? List.of() : List.copyOf(options);
        if (regenCount < 0) {
            throw new IllegalArgumentException("regenCount не может быть отрицательным: " + regenCount);
        }
    }

    /**
     * Обратная совместимость: старый конструктор без полей v2.
     */
    public Question(
            long id,
            String slug,
            String sourceSlug,
            String filePath,
            String topic,
            String questionText,
            String answerMarkdown,
            boolean important,
            String sourceHash,
            QuestionType questionType,
            String codeSnippet,
            String diagramMermaid,
            int regenCount,
            String takeaway
    ) {
        this(
                id,
                slug,
                sourceSlug,
                filePath,
                topic,
                questionText,
                answerMarkdown,
                important,
                sourceHash,
                questionType,
                codeSnippet,
                diagramMermaid,
                regenCount,
                takeaway,
                Difficulty.MEDIUM,
                null,
                null,
                null,
                List.of(),
                List.of()
        );
    }

    /**
     * Синоним для нового доменного контракта type -> questionType.
     */
    public QuestionType type() {
        return questionType;
    }

    /**
     * Static factory for import: common subset of parameters with defaults for nullable fields.
     * Use when creating a new question from imported content (id=0, sourceSlug=slug,
     * diagramMermaid=null, regenCount=0, takeaway=null).
     *
     * @param slug           unique key (e.g. path + "#Q1")
     * @param filePath       relative path to markdown file
     * @param topic          topic (from file name)
     * @param questionText   question text
     * @param answerMarkdown full answer in markdown
     * @param important      whether marked as important
     * @param sourceHash     SHA-256 of content
     * @param questionType   TEXT or CODE
     * @param codeSnippet    code snippet for CODE type (nullable)
     * @return new Question with id=0 and default nullable fields
     */
    public static Question forImport(
            String slug,
            String filePath,
            String topic,
            String questionText,
            String answerMarkdown,
            boolean important,
            String sourceHash,
            QuestionType questionType,
            String codeSnippet
    ) {
        return new Question(
                0L,
                slug,
                slug,
                filePath,
                topic,
                questionText,
                answerMarkdown,
                important,
                sourceHash,
                questionType,
                codeSnippet,
                null,
                0,
                null,
                Difficulty.MEDIUM,
                null,
                null,
                null,
                List.of(),
                List.of()
        );
    }
}
