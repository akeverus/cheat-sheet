package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.TestQuestionBuilder;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.cheatsheet.quiz.TestQuestionBuilder.aQuestion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CodeTraceServiceTest {

    private QuestionRepository questionRepository;
    private AiQuestionClient aiQuestionClient;
    private CodeTraceService service;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        aiQuestionClient = mock(AiQuestionClient.class);
        service = new CodeTraceService(questionRepository, aiQuestionClient);
    }

    @Test
    void returnsEmptyWhenQuestionNotFound() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<String> result = service.getOrGenerate(99L);

        assertThat(result).isEmpty();
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void returnsEmptyForTextTypeQuestion() {
        Question textQuestion = aQuestion()
                .withId(1L)
                .withQuestionType(QuestionType.TEXT)
                .build();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(textQuestion));

        Optional<String> result = service.getOrGenerate(1L);

        assertThat(result).isEmpty();
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void returnsEmptyForCodeQuestionWithNullSnippet() {
        Question codeNoSnippet = aQuestion()
                .withId(2L)
                .withQuestionType(QuestionType.CODE)
                .withCodeSnippet(null)
                .build();
        when(questionRepository.findById(2L)).thenReturn(Optional.of(codeNoSnippet));

        Optional<String> result = service.getOrGenerate(2L);

        assertThat(result).isEmpty();
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void returnsEmptyForCodeQuestionWithBlankSnippet() {
        Question codeBlank = aQuestion()
                .withId(3L)
                .withQuestionType(QuestionType.CODE)
                .withCodeSnippet("   ")
                .build();
        when(questionRepository.findById(3L)).thenReturn(Optional.of(codeBlank));

        Optional<String> result = service.getOrGenerate(3L);

        assertThat(result).isEmpty();
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void generatesTraceForValidCodeQuestion() {
        Question codeQuestion = aQuestion()
                .withId(4L)
                .withQuestionType(QuestionType.CODE)
                .withQuestionText("Что выведет код?")
                .withCodeSnippet("int x = 1; System.out.println(x++);")
                .build();
        when(questionRepository.findById(4L)).thenReturn(Optional.of(codeQuestion));
        when(aiQuestionClient.generateCodeTrace("Что выведет код?", "int x = 1; System.out.println(x++);"))
                .thenReturn(Optional.of("[{\"step\":1,\"line\":\"int x = 1\",\"state\":{\"x\":1}}]"));

        Optional<String> result = service.getOrGenerate(4L);

        assertThat(result).isPresent();
        assertThat(result.get()).contains("step");
    }

    @Test
    void returnsCachedTraceOnSecondCall() {
        Question codeQuestion = aQuestion()
                .withId(5L)
                .withQuestionType(QuestionType.CODE)
                .withQuestionText("Q")
                .withCodeSnippet("code")
                .build();
        when(questionRepository.findById(5L)).thenReturn(Optional.of(codeQuestion));
        when(aiQuestionClient.generateCodeTrace("Q", "code"))
                .thenReturn(Optional.of("trace-data"));

        service.getOrGenerate(5L);
        Optional<String> cached = service.getOrGenerate(5L);

        assertThat(cached).contains("trace-data");
        verify(aiQuestionClient, times(1)).generateCodeTrace(anyString(), anyString());
    }

    @Test
    void returnsEmptyWhenAiReturnsEmpty() {
        Question codeQuestion = aQuestion()
                .withId(6L)
                .withQuestionType(QuestionType.CODE)
                .withQuestionText("Q")
                .withCodeSnippet("code")
                .build();
        when(questionRepository.findById(6L)).thenReturn(Optional.of(codeQuestion));
        when(aiQuestionClient.generateCodeTrace(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Optional<String> result = service.getOrGenerate(6L);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyWhenAiThrowsException() {
        Question codeQuestion = aQuestion()
                .withId(7L)
                .withQuestionType(QuestionType.CODE)
                .withQuestionText("Q")
                .withCodeSnippet("code")
                .build();
        when(questionRepository.findById(7L)).thenReturn(Optional.of(codeQuestion));
        when(aiQuestionClient.generateCodeTrace(anyString(), anyString()))
                .thenThrow(new RuntimeException("AI unavailable"));

        Optional<String> result = service.getOrGenerate(7L);

        assertThat(result).isEmpty();
    }
}
