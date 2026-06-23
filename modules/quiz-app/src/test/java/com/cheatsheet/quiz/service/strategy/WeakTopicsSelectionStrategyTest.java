package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.common.constants.QuizConstants;
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
class WeakTopicsSelectionStrategyTest {

    @Mock
    QuestionRepository questionRepository;
    @Mock
    QuestionStatsRepository questionStatsRepository;
    @Mock
    DefaultSelectionStrategy defaultStrategy;
    @Mock
    TopicCatalogService topicCatalogService;

    @InjectMocks
    WeakTopicsSelectionStrategy strategy;

    private static final long NOW = 1_000_000L;

    private Question q(long id, String topic) {
        return new Question(id, "slug-" + id, "slug-" + id, "file.md",
                topic, "Question " + id, "Answer", false, "hash",
                QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void delegatesToDefaultWhenNoWeakTopic() {
        InterviewFilter filter = new InterviewFilter("java", false, false);
        when(questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn(null);
        when(defaultStrategy.selectNextQuestionId(filter, NOW))
                .thenReturn(Optional.of(10L));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(10L);
        verify(defaultStrategy).selectNextQuestionId(filter, NOW);
    }

    @Test
    void returnsDueFromWeakTopic() {
        // Каноничный сценарий «Слабые темы»: тема НЕ зафиксирована (topic=null),
        // система сама уводит в глобально слабейшую. Раньше тут стоял topic="java"
        // — несовпадающий со слабой "spring" — что незаметно покрывало баг (стратегия
        // выдавала чужую тему). Конфликт topic≠weak теперь проверяет отдельный тест.
        InterviewFilter filter = new InterviewFilter(null, false, false);
        when(questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn("spring");
        when(questionRepository.findDueQuestions("spring", false, false, NOW, 1))
                .thenReturn(List.of(q(5, "spring")));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(5L);
        verify(defaultStrategy, never()).selectNextQuestionId(any(), anyLong());
    }

    @Test
    void returnsNextFromWeakTopicWhenNoDue() {
        InterviewFilter filter = new InterviewFilter(null, true, false);
        when(questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn("sql");
        when(questionRepository.findDueQuestions("sql", true, false, NOW, 1))
                .thenReturn(List.of());
        when(questionRepository.findNextQuestions("sql", true, false, NOW, 1))
                .thenReturn(List.of(q(20, "sql")));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(20L);
    }

    @Test
    void delegatesToDefaultWhenWeakTopicDiffersFromSelectedTopic() {
        // Регресс-гард: пользователь зафиксировал тему "java", но глобально слабейшая
        // — "spring". Стратегия НЕ должна подсовывать вопросы из чужой темы; она
        // делегирует в default-стратегию, которая чтит выбранную тему. Симметрично
        // существующему групповому guard'у (слабая тема вне выбранной группы → default).
        InterviewFilter filter = new InterviewFilter("java", false, false);
        when(questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn("spring");
        when(defaultStrategy.selectNextQuestionId(filter, NOW))
                .thenReturn(Optional.of(42L));

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).contains(42L);
        verify(defaultStrategy).selectNextQuestionId(filter, NOW);
        // Подтверждаем, что вопросы из слабой "spring" вообще не запрашивались.
        verify(questionRepository, never()).findDueQuestions(any(), any(), any(), anyLong(), anyInt());
        verify(questionRepository, never()).findNextQuestions(any(), any(), any(), anyLong(), anyInt());
    }

    @Test
    void returnsEmptyWhenWeakTopicHasNoQuestions() {
        InterviewFilter filter = new InterviewFilter(null, false, false);
        when(questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD))
                .thenReturn("empty-topic");
        when(questionRepository.findDueQuestions("empty-topic", false, false, NOW, 1))
                .thenReturn(List.of());
        when(questionRepository.findNextQuestions("empty-topic", false, false, NOW, 1))
                .thenReturn(List.of());

        Optional<Long> result = strategy.selectNextQuestionId(filter, NOW);

        assertThat(result).isEmpty();
    }
}
