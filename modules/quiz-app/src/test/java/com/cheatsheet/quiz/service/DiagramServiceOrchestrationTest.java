package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.infrastructure.diagram.DiagramService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.diagram.MermaidSanitizer;
import com.cheatsheet.quiz.service.diagram.MermaidValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagramServiceOrchestrationTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AiQuestionClient aiQuestionClient;
    @Mock
    private MermaidValidator mermaidValidator;
    @Mock
    private MermaidSanitizer mermaidSanitizer;

    private DiagramService service;

    @BeforeEach
    void setUp() {
        service = new DiagramService(
                questionRepository,
                aiQuestionClient,
                mermaidValidator,
                mermaidSanitizer
        );
    }

    @Test
    void returnsCachedDiagramWithoutGeneration() {
        Question question = questionWithDiagram(501L, "graph TD\nA-->B");

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).contains("graph TD\nA-->B");
        verify(aiQuestionClient, never()).generateDiagram(anyString(), anyString(), anyString());
        verify(questionRepository, never()).updateDiagram(question.id(), "graph TD\nA-->B");
    }

    @Test
    void skipsPersistenceWhenSingleAttemptDiagramIsInvalid() {
        Question question = questionWithDiagram(777L, null);
        when(aiQuestionClient.generateDiagram(question.questionText(), question.answerMarkdown(), question.topic()))
                .thenReturn(Optional.of("raw-first"));
        when(mermaidSanitizer.sanitizeMermaid("raw-first")).thenReturn("sanitized-first");
        when(mermaidValidator.isRelevantMermaid("sanitized-first", question.questionText(), question.answerMarkdown()))
                .thenReturn(false);

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).isEmpty();
        verify(aiQuestionClient).generateDiagram(question.questionText(), question.answerMarkdown(), question.topic());
        verify(questionRepository, never()).updateDiagram(question.id(), "sanitized-first");
    }

    @Test
    void persistsValidatedDiagramWhenAiReturnsRelevant() {
        Question question = questionWithDiagram(123L, null);
        when(aiQuestionClient.generateDiagram(question.questionText(), question.answerMarkdown(), question.topic()))
                .thenReturn(Optional.of("raw-mermaid"));
        when(mermaidSanitizer.sanitizeMermaid("raw-mermaid")).thenReturn("graph TD\nA-->B");
        when(mermaidValidator.isRelevantMermaid("graph TD\nA-->B",
                question.questionText(), question.answerMarkdown()))
                .thenReturn(true);

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).contains("graph TD\nA-->B");
        verify(questionRepository).updateDiagram(123L, "graph TD\nA-->B");
    }

    @Test
    void returnsEmptyWhenAiReturnsEmpty() {
        Question question = questionWithDiagram(456L, null);
        when(aiQuestionClient.generateDiagram(question.questionText(), question.answerMarkdown(), question.topic()))
                .thenReturn(Optional.empty());

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).isEmpty();
        verify(questionRepository, never()).updateDiagram(org.mockito.ArgumentMatchers.anyLong(), anyString());
    }

    @Test
    void returnsEmptyAndLogsWhenGenerationThrows() {
        Question question = questionWithDiagram(999L, null);
        when(aiQuestionClient.generateDiagram(question.questionText(), question.answerMarkdown(), question.topic()))
                .thenThrow(new RuntimeException("AI down"));

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).isEmpty();
        verify(questionRepository, never()).updateDiagram(org.mockito.ArgumentMatchers.anyLong(), anyString());
    }

    private Question questionWithDiagram(long id, String diagramMermaid) {
        return new Question(
                id,
                "java#q" + id,
                "java#q" + id,
                "java.md",
                "java",
                "Почему деградирует HashMap?",
                "Из-за коллизий.",
                false,
                "hash",
                QuestionType.CONCEPT,
                null,
                diagramMermaid,
                0,
                null
        );
    }
}
