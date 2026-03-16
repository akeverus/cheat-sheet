package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShuffleSelectionStrategyTest {

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    ShuffleSelectionStrategy strategy;

    private static final long NOW = 1_000_000L;

    @Test
    void returnsShuffledIdWhenAvailable() {
        InterviewFilter filter = new InterviewFilter(null, false, false, true);
        when(questionRepository.findShuffledQuestionIds(false, false, 1))
                .thenReturn(List.of(15L));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(15L);
    }

    @Test
    void returnsEmptyWhenNoShuffledIds() {
        InterviewFilter filter = new InterviewFilter(null, true, false, true);
        when(questionRepository.findShuffledQuestionIds(true, false, 1))
                .thenReturn(List.of());

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).isEmpty();
    }

    @Test
    void passesImportantAndOnlyWrongFilters() {
        InterviewFilter filter = new InterviewFilter("java", true, true, true);
        when(questionRepository.findShuffledQuestionIds(true, true, 1))
                .thenReturn(List.of(33L));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(33L);
    }
}
