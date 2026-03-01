package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.persistence.model.UserTopicStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceTest {

    @Mock
    QuestionRepository questionRepository;
    @Mock
    UserTopicStatsRepository userTopicStatsRepository;
    @Mock
    TopicCatalogService topicCatalogService;
    @Mock
    PreloadService preloadService;

    TrainingSessionService service;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getInterview().setExamPenaltyQuestions(3);
        Clock clock = Clock.fixed(Instant.parse("2026-03-01T10:00:00Z"), ZoneOffset.UTC);
        service = new TrainingSessionService(
                questionRepository,
                userTopicStatsRepository,
                topicCatalogService,
                preloadService,
                clock,
                properties
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void startSessionPrioritizesWeakTopicsByMastery() {
        InterviewFilter filter = new InterviewFilter(null, null, false, false, false, true);
        when(questionRepository.findTopics()).thenReturn(List.of("hard-topic", "easy-topic"));
        when(topicCatalogService.topicsForFilter(any(), any())).thenReturn(List.of("hard-topic", "easy-topic"));
        when(userTopicStatsRepository.findByTopic("hard-topic"))
                .thenReturn(Optional.of(new UserTopicStats(1L, "hard-topic", 18, 2, 0.90, LocalDateTime.now())));
        when(userTopicStatsRepository.findByTopic("easy-topic"))
                .thenReturn(Optional.of(new UserTopicStats(2L, "easy-topic", 2, 8, 0.20, LocalDateTime.now())));
        when(questionRepository.findQuestionIdsForSessionByTopics(anyList(), anyBoolean(), anyBoolean(), anyLong(), anyInt()))
                .thenReturn(List.of(101L, 102L));

        service.startSession(InterviewMode.TRAINING, 10, filter);

        ArgumentCaptor<List<String>> topicsCaptor = ArgumentCaptor.forClass(List.class);
        verify(questionRepository).findQuestionIdsForSessionByTopics(
                topicsCaptor.capture(), anyBoolean(), anyBoolean(), anyLong(), anyInt()
        );
        assertThat(topicsCaptor.getValue()).containsExactly("easy-topic", "hard-topic");
    }

    @Test
    void addExamPenaltyQuestionsUsesGroupTopicsWhenTopicIsMissing() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM,
                List.of(10L, 20L),
                null,
                "backend",
                false,
                false,
                false,
                true
        );
        when(questionRepository.findQuestionIdsExcluding(anyList(), any(), anyBoolean(), anyBoolean(), anyLong(), anyInt()))
                .thenReturn(List.of());
        when(questionRepository.findTopics()).thenReturn(List.of("java", "spring"));
        when(topicCatalogService.topicsForFilter(any(), any())).thenReturn(List.of("java", "spring"));
        when(questionRepository.findQuestionIdsExcludingByTopics(anyList(), anyList(), anyBoolean(), anyBoolean(), anyLong(), anyInt()))
                .thenReturn(List.of(101L, 102L));

        service.addExamPenaltyQuestions(session);

        assertThat(session.getQuestionIds()).contains(101L, 102L);
        verify(preloadService).preloadQuestions(List.of(101L, 102L));
    }
}
