package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewReasonService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FocusTrainingPageServiceTest {

    @Mock
    InterviewFacade facade;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    QuestionRevisionRepository questionRevisionRepository;
    @Mock
    QuestionStatsRepository questionStatsRepository;
    @Mock
    TopicCatalogService topicCatalogService;
    @Mock
    ReviewStateRepository reviewStateRepository;
    @Mock
    ReviewReasonService reviewReasonService;
    @Mock
    com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService learningMetricsService;
    @Mock
    com.cheatsheet.quiz.feature.interview.mapper.ContentBlockMapper contentBlockMapper;
    @Mock
    InterviewQuestion interviewQuestion;

    FocusTrainingPageService service;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        service = new FocusTrainingPageService(
                facade,
                questionRepository,
                questionRevisionRepository,
                questionStatsRepository,
                topicCatalogService,
                appProperties,
                reviewStateRepository,
                reviewReasonService,
                learningMetricsService,
                contentBlockMapper
        );
    }

    @Test
    void returnsEmptyStateWhenFacadeHasNoQuestion() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        when(facade.nextQuestion(any(), anyBoolean(), anyLong())).thenReturn(Optional.empty());
        when(learningMetricsService.compute()).thenReturn(new com.cheatsheet.quiz.domain.LearningMetrics(
                10, 5, 2, 10, 7, 3, 70.0, 70.0, -1.0, -1.0, 3, 0, 1000.0));
        when(facade.getStats(any())).thenReturn(new InterviewStats(10, 3, 2, 7, 3));
        when(questionStatsRepository.countAllFiltered(null, true, null)).thenReturn(4L);
        when(questionStatsRepository.countAllFiltered(null, null, true)).thenReturn(6L);
        when(questionStatsRepository.countWeakTopics(any(Double.class))).thenReturn(2L);
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(any(), any(), anyBoolean())).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(any())).thenReturn(List.of());

        FocusTrainingPageService.FocusPageState state = service.buildFocusPageState(
                null,
                filter,
                InterviewMode.TRAINING,
                false,
                1L
        );

        assertThat(state.current()).isEmpty();
        assertThat(state.surface().dueFiltered()).isEqualTo(3L);
        assertThat(state.surface().filterFavoritesCount()).isEqualTo(4L);
        assertThat(state.surface().filterWrongCount()).isEqualTo(6L);
        assertThat(state.surface().filterWeakTopicsCount()).isEqualTo(2L);
    }

    @Test
    void keepsCurrentQuestionWhenFacadeReturnsIt() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        when(facade.nextQuestion(any(), anyBoolean(), anyLong())).thenReturn(Optional.of(interviewQuestion));
        when(learningMetricsService.compute()).thenReturn(new com.cheatsheet.quiz.domain.LearningMetrics(
                10, 5, 2, 10, 7, 3, 70.0, 70.0, -1.0, -1.0, 3, 0, 1000.0));
        when(facade.getStats(any())).thenReturn(new InterviewStats(10, 3, 2, 7, 3));
        when(questionStatsRepository.countAllFiltered(null, true, null)).thenReturn(0L);
        when(questionStatsRepository.countAllFiltered(null, null, true)).thenReturn(0L);
        when(questionStatsRepository.countWeakTopics(any(Double.class))).thenReturn(0L);
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(any(), any(), anyBoolean())).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(any())).thenReturn(List.of());
        when(interviewQuestion.question()).thenReturn(new Question(
                1L,
                "slug",
                "source",
                "file",
                "java",
                "question",
                "answer",
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null,
                Difficulty.MEDIUM,
                "short explanation for generation",
                "detailed explanation with enough content for validation and rendering",
                "common mistake",
                List.of("java"),
                List.of()
        ));

        FocusTrainingPageService.FocusPageState state = service.buildFocusPageState(
                null,
                filter,
                InterviewMode.TRAINING,
                false,
                1L
        );

        assertThat(state.current()).contains(interviewQuestion);
    }
}
