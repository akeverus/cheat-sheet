package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.OptionGenerator;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagramServiceOrchestrationTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private OptionGenerator optionGenerator;
    @Mock
    private MermaidValidator mermaidValidator;
    @Mock
    private MermaidSanitizer mermaidSanitizer;

    private DiagramService service;

    @BeforeEach
    void setUp() {
        service = new DiagramService(
                questionRepository,
                optionGenerator,
                mermaidValidator,
                mermaidSanitizer
        );
    }

    @Test
    void returnsCachedDiagramWithoutGeneration() {
        Question question = questionWithDiagram(501L, "graph TD\nA-->B");

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).contains("graph TD\nA-->B");
        verify(optionGenerator, never()).generateDiagram(anyString(), anyString(), anyString());
        verify(questionRepository, never()).updateDiagram(question.id(), "graph TD\nA-->B");
    }

    @Test
    void retriesAfterInvalidDiagramAndPersistsValidSanitizedDiagram() {
        Question question = questionWithDiagram(777L, null);
        when(optionGenerator.generateDiagram(question.questionText(), question.answerMarkdown(), question.topic()))
                .thenReturn(Optional.of("raw-first"))
                .thenReturn(Optional.of("raw-second"));
        when(mermaidSanitizer.sanitizeMermaid("raw-first")).thenReturn("sanitized-first");
        when(mermaidSanitizer.sanitizeMermaid("raw-second")).thenReturn("sanitized-second");
        when(mermaidValidator.isRelevantMermaid("sanitized-first", question.questionText(), question.answerMarkdown()))
                .thenReturn(false);
        when(mermaidValidator.isRelevantMermaid("sanitized-second", question.questionText(), question.answerMarkdown()))
                .thenReturn(true);

        Optional<String> result = service.getOrGenerateDiagram(question);

        assertThat(result).contains("sanitized-second");
        verify(optionGenerator, times(2)).generateDiagram(question.questionText(), question.answerMarkdown(), question.topic());
        verify(questionRepository).updateDiagram(question.id(), "sanitized-second");
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
