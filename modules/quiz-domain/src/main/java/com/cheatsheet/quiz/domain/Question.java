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
 * @param takeaway        ключевой вывод — «Главное, что нужно запомнить» (nullable)
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
    }

    /**
     * Синоним для нового доменного контракта type -> questionType.
     */
    public QuestionType type() {
        return questionType;
    }

    /**
     * Фабрика для импорта: подмножество параметров, приходящих из markdown.
     * Остальные поля получают значения по умолчанию (id=0, sourceSlug=slug,
     * diagramMermaid=null, takeaway=null).
     *
     * @param slug           уникальный ключ (путь + «#Q1»)
     * @param filePath       относительный путь к markdown-файлу
     * @param topic          тема (из имени файла)
     * @param questionText   текст вопроса
     * @param answerMarkdown полный ответ в markdown
     * @param important      помечен ли как важный
     * @param sourceHash     SHA-256 контента
     * @param questionType   TEXT или CODE
     * @param codeSnippet    фрагмент кода для типа CODE (nullable)
     * @return новый вопрос с id=0 и дефолтами для необязательных полей
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
        return Question.builder()
                .slug(slug)
                .sourceSlug(slug)
                .filePath(filePath)
                .topic(topic)
                .questionText(questionText)
                .answerMarkdown(answerMarkdown)
                .important(important)
                .sourceHash(sourceHash)
                .questionType(questionType)
                .codeSnippet(codeSnippet)
                .build();
    }
}
