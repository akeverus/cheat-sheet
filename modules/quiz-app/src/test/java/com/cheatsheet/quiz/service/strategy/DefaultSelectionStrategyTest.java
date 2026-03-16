package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultSelectionStrategyTest {

    @Mock
    QuestionRepository questionRepository;
    @Mock
    TopicCatalogService topicCatalogService;

    @InjectMocks
    DefaultSelectionStrategy strategy;

    private static final long NOW = 1_000_000L;

    private Question q(long id) {
        return new Question(id, "slug-" + id, "slug-" + id, "file.md",
                "java", "Question " + id, "Answer", false, "hash",
                QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void returnsDueQuestionWhenAvailable() {
        InterviewFilter filter = new InterviewFilter("java", false, false);
        when(questionRepository.findDueQuestions("java", false, false, NOW, 1))
                .thenReturn(List.of(q(42)));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(42L);
        verify(questionRepository, never()).findNextQuestions(any(), any(), any(), anyLong(), anyInt());
    }

    @Test
    void fallsBackToNextQuestionsWhenNoDue() {
        InterviewFilter filter = new InterviewFilter("spring", false, false);
        when(questionRepository.findDueQuestions("spring", false, false, NOW, 1))
                .thenReturn(List.of());
        when(questionRepository.findNextQuestions("spring", false, false, NOW, 1))
                .thenReturn(List.of(q(7)));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(7L);
    }

    @Test
    void returnsEmptyWhenNoQuestionsAtAll() {
        InterviewFilter filter = new InterviewFilter("unknown", false, false);
        when(questionRepository.findDueQuestions("unknown", false, false, NOW, 1))
                .thenReturn(List.of());
        when(questionRepository.findNextQuestions("unknown", false, false, NOW, 1))
                .thenReturn(List.of());

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).isEmpty();
    }

    @Test
    void usesEffectiveTopicFromFilter() {
        InterviewFilter filter = new InterviewFilter("kotlin", true, true, true);
        // shuffle=true → effectiveTopic() returns null
        when(questionRepository.findTopics()).thenReturn(List.of("kotlin"));
        when(topicCatalogService.topicsForFilter(filter, List.of("kotlin"))).thenReturn(List.of("kotlin"));
        when(questionRepository.findDueQuestionsByTopics(List.of("kotlin"), true, true, NOW, 1))
                .thenReturn(List.of(q(99)));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(99L);
    }

    @Test
    void usesCatalogOrderedTopicsWhenTopicNotSelected() {
        InterviewFilter filter = new InterviewFilter(null, "languages", false, false, false, true);
        List<String> dbTopics = List.of(
                "programming-languages/kotlin/kotlin-interview",
                "programming-languages/java/java-core-interview"
        );
        List<String> orderedTopics = List.of(
                "programming-languages/java/java-core-interview",
                "programming-languages/kotlin/kotlin-interview"
        );
        when(questionRepository.findTopics()).thenReturn(dbTopics);
        when(topicCatalogService.topicsForFilter(filter, dbTopics)).thenReturn(orderedTopics);
        when(questionRepository.findDueQuestionsByTopics(orderedTopics, false, false, NOW, 1))
                .thenReturn(List.of(q(15)));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(15L);
        verify(questionRepository).findDueQuestionsByTopics(orderedTopics, false, false, NOW, 1);
    }
}
