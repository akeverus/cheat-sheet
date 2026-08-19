package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService bookmarkService;
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
                contentBlockMapper,
                bookmarkService,
                java.time.Clock.systemUTC());
    }

    @Test
    void returnsEmptyStateWhenFacadeHasNoQuestion() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        when(facade.nextQuestion(any(), anyBoolean(), anyLong())).thenReturn(Optional.empty());
        when(learningMetricsService.compute()).thenReturn(new com.cheatsheet.quiz.domain.LearningMetrics(
                10, 5, 2, 10, 7, 3, 70.0, 70.0, -1.0, -1.0, 3, 0, 1, 1000.0));
        // Намеренно НЕ совпадает с LearningMetrics: экран несёт две статистики из
        // разных выборок, и совпадающие фикстуры не поймали бы, если бы одна
        // затирала другую.
        when(facade.getStats(any())).thenReturn(new InterviewStats(6, 4, 3, 1, 2, 1));
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
        // KPI сайдбара — глобальные метрики, виджет «Сегодня» — границы фильтра.
        // Числитель и знаменатель у виджета обязаны быть из одной выборки, поэтому
        // сюда уезжает целая статистика, а не одно число очереди.
        assertThat(state.surface().stats()).isEqualTo(new InterviewStats(10, 3, 1, 2, 7, 3));
        assertThat(state.surface().filteredStats()).isEqualTo(new InterviewStats(6, 4, 3, 1, 2, 1));
        assertThat(state.surface().filteredStats().dueRepeats()).isEqualTo(1L);
        assertThat(state.surface().filterFavoritesCount()).isEqualTo(4L);
        assertThat(state.surface().filterWrongCount()).isEqualTo(6L);
        assertThat(state.surface().filterWeakTopicsCount()).isEqualTo(2L);
    }

    @Test
    void keepsCurrentQuestionWhenFacadeReturnsIt() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        when(facade.nextQuestion(any(), anyBoolean(), anyLong())).thenReturn(Optional.of(interviewQuestion));
        when(learningMetricsService.compute()).thenReturn(new com.cheatsheet.quiz.domain.LearningMetrics(
                10, 5, 2, 10, 7, 3, 70.0, 70.0, -1.0, -1.0, 3, 0, 1, 1000.0));
        when(facade.getStats(any())).thenReturn(new InterviewStats(10, 3, 1, 2, 7, 3));
        when(questionStatsRepository.countAllFiltered(null, true, null)).thenReturn(0L);
        when(questionStatsRepository.countAllFiltered(null, null, true)).thenReturn(0L);
        when(questionStatsRepository.countWeakTopics(any(Double.class))).thenReturn(0L);
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(any(), any(), anyBoolean())).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(any())).thenReturn(List.of());
        when(interviewQuestion.question()).thenReturn(Question.builder()
                .id(1L)
                .slug("slug")
                .sourceSlug("source")
                .filePath("file")
                .topic("java")
                .questionText("question")
                .answerMarkdown("answer")
                .sourceHash("hash")
                .questionType(QuestionType.TEXT)
                .difficulty(Difficulty.MEDIUM)
                .shortExplanation("short explanation for generation")
                .detailedExplanation("detailed explanation with enough content for validation and rendering")
                .commonMistake("common mistake")
                .tags(List.of("java"))
                .build());

        FocusTrainingPageService.FocusPageState state = service.buildFocusPageState(
                null,
                filter,
                InterviewMode.TRAINING,
                false,
                1L
        );

        assertThat(state.current()).contains(interviewQuestion);
    }

    /**
     * Тексты причин — тренерские подсказки («Недавно была ошибка», «Тема пока
     * слабая»). Перед вопросом экзамена они подсказывают ответ, поэтому на
     * экзамене причина не показывается и не считается: проверяем и пустой
     * результат, и что классификатор с репозиториями вообще не дёргались —
     * это три запроса на каждом вопросе экзамена.
     */
    @Test
    void skipsReviewReasonEntirelyInExamMode() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L), "java", "backend", false, false, false, true);
        when(facade.questionForSession(session)).thenReturn(Optional.of(interviewQuestion));
        when(learningMetricsService.compute()).thenReturn(new com.cheatsheet.quiz.domain.LearningMetrics(
                10, 5, 2, 10, 7, 3, 70.0, 70.0, -1.0, -1.0, 3, 0, 1, 1000.0));
        when(facade.getStats(any())).thenReturn(new InterviewStats(10, 3, 1, 2, 7, 3));
        when(questionStatsRepository.countAllFiltered(null, true, null)).thenReturn(0L);
        when(questionStatsRepository.countAllFiltered(null, null, true)).thenReturn(0L);
        when(questionStatsRepository.countWeakTopics(any(Double.class))).thenReturn(0L);
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(any(), any(), anyBoolean())).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(any())).thenReturn(List.of());
        when(interviewQuestion.question()).thenReturn(Question.builder()
                .id(1L)
                .slug("slug")
                .sourceSlug("source")
                .filePath("file")
                .topic("java")
                .questionText("question")
                .answerMarkdown("answer")
                .sourceHash("hash")
                .questionType(QuestionType.TEXT)
                .difficulty(Difficulty.MEDIUM)
                .shortExplanation("short explanation for generation")
                .detailedExplanation("detailed explanation with enough content for validation and rendering")
                .commonMistake("common mistake")
                .tags(List.of("java"))
                .build());

        FocusTrainingPageService.FocusPageState state = service.buildFocusPageState(
                session,
                filter,
                InterviewMode.EXAM,
                false,
                1L
        );

        assertThat(state.reviewReason()).isNull();
        verify(reviewReasonService, never()).classify(any(), any(), anyDouble(), anyBoolean());
        verify(reviewStateRepository, never()).findByQuestionId(anyLong());
        verify(reviewStateRepository, never()).findFsrsState(anyLong());
        verify(questionStatsRepository, never()).getTopicAccuracy(any());
    }
}
