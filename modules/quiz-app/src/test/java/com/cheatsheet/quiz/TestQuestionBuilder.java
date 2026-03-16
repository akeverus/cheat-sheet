package com.cheatsheet.quiz;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;

/**
 * Builder for {@link Question} with sensible defaults.
 * Reduces boilerplate in test code.
 */
public class TestQuestionBuilder {

    private long id = 1L;
    private String slug = "test-slug";
    private String sourceSlug = "test-slug";
    private String filePath = "test.md";
    private String topic = "java";
    private String questionText = "What is Java?";
    private String answerMarkdown = "Java is a programming language.";
    private boolean important = false;
    private String sourceHash = "abc123";
    private QuestionType questionType = QuestionType.TEXT;
    private String codeSnippet = null;
    private String diagramMermaid = null;
    private int regenCount = 0;
    private String takeaway = null;

    public static TestQuestionBuilder aQuestion() {
        return new TestQuestionBuilder();
    }

    public TestQuestionBuilder withId(long id) { this.id = id; return this; }
    public TestQuestionBuilder withSlug(String slug) { this.slug = slug; this.sourceSlug = slug; return this; }
    public TestQuestionBuilder withTopic(String topic) { this.topic = topic; return this; }
    public TestQuestionBuilder withQuestionText(String text) { this.questionText = text; return this; }
    public TestQuestionBuilder withAnswerMarkdown(String md) { this.answerMarkdown = md; return this; }
    public TestQuestionBuilder withImportant(boolean important) { this.important = important; return this; }
    public TestQuestionBuilder withQuestionType(QuestionType type) { this.questionType = type; return this; }
    public TestQuestionBuilder withCodeSnippet(String code) { this.codeSnippet = code; return this; }
    public TestQuestionBuilder withDiagramMermaid(String diagram) { this.diagramMermaid = diagram; return this; }
    public TestQuestionBuilder withRegenCount(int count) { this.regenCount = count; return this; }
    public TestQuestionBuilder withTakeaway(String takeaway) { this.takeaway = takeaway; return this; }
    public TestQuestionBuilder withSourceHash(String hash) { this.sourceHash = hash; return this; }
    public TestQuestionBuilder withFilePath(String path) { this.filePath = path; return this; }

    public Question build() {
        return new Question(id, slug, sourceSlug, filePath, topic, questionText,
                answerMarkdown, important, sourceHash, questionType, codeSnippet,
                diagramMermaid, regenCount, takeaway);
    }
}
