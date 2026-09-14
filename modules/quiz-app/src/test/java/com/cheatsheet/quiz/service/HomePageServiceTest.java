package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import com.cheatsheet.quiz.domain.UserExperience;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService;
import com.cheatsheet.quiz.feature.interview.service.page.HomePageService;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.progress.DailyStreakService;
import com.cheatsheet.quiz.feature.interview.service.progress.ExperienceService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomePageServiceTest {

    @Mock
    private LearningMetricsService learningMetricsService;
    @Mock
    private DailyStreakService dailyStreakService;
    @Mock
    private ExperienceService experienceService;
    @Mock
    private SessionHistoryService sessionHistoryService;
    @Mock
    private QuestionStatsRepository questionStatsRepository;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private TopicUtils topicUtils;

    private HomePageService service;

    @BeforeEach
    void setUp() {
        service = new HomePageService(
                learningMetricsService,
                dailyStreakService,
                experienceService,
                sessionHistoryService,
                questionStatsRepository,
                bookmarkService,
                topicUtils);
    }

    @Test
    void reviewWrongUsesQuestionCountNotAttemptSum() {
        when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY.toBuilder()
                .dueNow(10)
                .dueFresh(10)
                .attemptsIncorrect(2862)
                .build());
        when(dailyStreakService.getTodayProgress()).thenReturn(
                new StreakResponse(3, 10, 5, false, 3, "5 дней"));
        when(experienceService.snapshot()).thenReturn(UserExperience.EMPTY);
        when(experienceService.currentStreak()).thenReturn(5);
        when(questionStatsRepository.findWeakestTopic(anyDouble())).thenReturn(null);
        when(questionStatsRepository.countWeakTopics(anyDouble())).thenReturn(0L);
        when(questionStatsRepository.countAllFiltered(isNull(), isNull(), eq(true))).thenReturn(225L);
        when(sessionHistoryService.recent()).thenReturn(List.of());
        when(bookmarkService.count()).thenReturn(0L);

        HomePageService.HomePageState state = service.build(Optional.empty(), Optional.empty());

        assertThat(state.primary().type()).isEqualTo(HomePageService.HomeAction.Type.REVIEW_WRONG);
        assertThat(state.primary().count()).isEqualTo(225);
        assertThat(state.primary().count()).isNotEqualTo(2862);
    }

    @Test
    void streakLeadDoesNotShowTodayOverGoal() {
        assertThat(HomePageService.streakLead(new StreakResponse(40, 10, 5, true, 40, "5 дней")))
                .isEqualTo("Цель на сегодня закрыта · серия 5 дней")
                .doesNotContain("40 из 10");
        assertThat(HomePageService.streakLead(new StreakResponse(3, 10, 2, false, 3, "2 дня")))
                .isEqualTo("3 из 10 сегодня · серия 2 дня");
    }

    @Test
    void dueRepeatsStayPrimaryWhenQueueExists() {
        when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY.toBuilder()
                .dueNow(213)
                .dueFresh(10)
                .attemptsIncorrect(2862)
                .build());
        when(dailyStreakService.getTodayProgress()).thenReturn(
                new StreakResponse(40, 10, 5, true, 40, "5 дней"));
        when(experienceService.snapshot()).thenReturn(UserExperience.EMPTY);
        when(experienceService.currentStreak()).thenReturn(5);
        when(questionStatsRepository.findWeakestTopic(anyDouble()))
                .thenReturn(new QuestionStatsRepository.WeakestTopic("java-concurrency", 40));
        when(questionStatsRepository.countWeakTopics(anyDouble())).thenReturn(23L);
        when(questionStatsRepository.countAllFiltered(isNull(), isNull(), eq(true))).thenReturn(225L);
        when(topicUtils.displayName(anyString())).thenReturn("Многопоточность в Java");
        when(sessionHistoryService.recent()).thenReturn(List.of());
        when(bookmarkService.count()).thenReturn(0L);

        HomePageService.HomePageState state = service.build(Optional.empty(), Optional.empty());

        assertThat(state.primary().type()).isEqualTo(HomePageService.HomeAction.Type.REVIEW_DUE);
        assertThat(state.primary().count()).isEqualTo(203);
        assertThat(state.secondary())
                .anyMatch(a -> a.type() == HomePageService.HomeAction.Type.REVIEW_WRONG && a.count() == 225);
        assertThat(state.streakLead()).contains("закрыта");
    }

    @Test
    void weakTopicActionDoesNotPresentTopicCountAsQuestionCount() {
        when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY);
        when(dailyStreakService.getTodayProgress()).thenReturn(
                new StreakResponse(0, 10, 0, false, 0, "0 дней"));
        when(experienceService.snapshot()).thenReturn(UserExperience.EMPTY);
        when(experienceService.currentStreak()).thenReturn(0);
        when(questionStatsRepository.findWeakestTopic(anyDouble()))
                .thenReturn(new QuestionStatsRepository.WeakestTopic("java-concurrency", 40));
        when(questionStatsRepository.countWeakTopics(anyDouble())).thenReturn(23L);
        when(questionStatsRepository.countAllFiltered(isNull(), isNull(), eq(true))).thenReturn(0L);
        when(topicUtils.displayName(anyString())).thenReturn("Многопоточность в Java");
        when(sessionHistoryService.recent()).thenReturn(List.of());
        when(bookmarkService.count()).thenReturn(0L);

        HomePageService.HomePageState state = service.build(Optional.empty(), Optional.empty());

        assertThat(state.weakTopicCount()).isEqualTo(23L);
        assertThat(state.primary().type()).isEqualTo(HomePageService.HomeAction.Type.WEAK_TOPIC);
        assertThat(state.primary().label()).isEqualTo("Слабая тема: Многопоточность в Java");
        assertThat(state.primary().count())
                .as("23 — число слабых тем, а действие открывает одну тему")
                .isZero();
    }

    @Test
    void continueNamesTheLiveSessionAndItsAnsweredProgress() {
        when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY);
        when(experienceService.snapshot()).thenReturn(UserExperience.EMPTY);
        when(sessionHistoryService.recent()).thenReturn(List.of());
        when(topicUtils.displayName("spring-graphql")).thenReturn("Spring for GraphQL");
        InterviewSession live = new InterviewSession(InterviewMode.MARATHON, List.of(1L, 2L, 3L),
                "spring-graphql", null, false, false, false, true);
        live.registerAnswer(false);
        var stalePause = new PausedSessionInfo(InterviewMode.EXAM, "old-topic", 10, 5, Instant.now());

        var action = service.build(Optional.of(live), Optional.of(stalePause)).primary();

        assertThat(action.post()).isFalse();
        assertThat(action.session()).isEqualTo(new HomePageService.HomeSession(
                InterviewMode.MARATHON, "Spring for GraphQL", 1, 3));
    }

    @Test
    void resumeNamesPausedSelectionWithoutInventingAnAllBankScope() {
        when(learningMetricsService.compute()).thenReturn(LearningMetrics.EMPTY);
        when(experienceService.snapshot()).thenReturn(UserExperience.EMPTY);
        when(sessionHistoryService.recent()).thenReturn(List.of());
        var pause = new PausedSessionInfo(InterviewMode.EXAM, null, 20, 4, Instant.now());

        var action = service.build(Optional.empty(), Optional.of(pause)).primary();

        assertThat(action.post()).isTrue();
        assertThat(action.session()).isEqualTo(new HomePageService.HomeSession(
                InterviewMode.EXAM, "Подборка тем", 4, 20));
    }
}
