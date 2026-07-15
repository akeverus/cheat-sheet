package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.TestQuestionBuilder;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.domain.NextActionType;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionSummaryService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static com.cheatsheet.quiz.TestQuestionBuilder.aQuestion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionSummaryServiceTest {

    @Mock
    QuestionRepository questionRepository;

    @Mock
    QuestionStatsRepository questionStatsRepository;

    private final Clock clock = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
    SessionSummaryService service;

    @BeforeEach
    void setUp() {
        service = new SessionSummaryService(questionRepository, questionStatsRepository, clock);
    }

    @Test
    void buildSummaryWithAllCorrectAnswers() {
        Question q1 = aQuestion().withId(1).withTopic("java").build();
        Question q2 = aQuestion().withId(2).withTopic("java").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1, q2));

        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L, 2L), "java", false, false, false);
        session.registerAnswer(true, "java");
        session.registerAnswer(true, "java");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getTotalQuestions()).isEqualTo(2);
        assertThat(summary.getCorrectCount()).isEqualTo(2);
        assertThat(summary.getWrongCount()).isEqualTo(0);
        assertThat(summary.getAccuracy()).isEqualTo(100.0);
        assertThat(summary.getTopicResults()).hasSize(1);
        assertThat(summary.getTopicResults().get(0).topic()).isEqualTo("java");
        assertThat(summary.getMistakes()).isEmpty();
        assertThat(summary.getRecommendations()).anyMatch(r -> r.contains("Отличный результат"));
    }

    @Test
    void buildSummaryWithMixedAnswersGeneratesMistakesAndRecommendations() {
        Question q1 = aQuestion().withId(1).withTopic("java").withQuestionText("Q1").build();
        Question q2 = aQuestion().withId(2).withTopic("spring").withQuestionText("Q2").build();
        Question q3 = aQuestion().withId(3).withTopic("spring").withQuestionText("Q3").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1, q2, q3));

        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L, 3L), null, false, false, false);
        session.registerAnswer(true, "java");
        session.registerAnswer(false, "spring");
        session.registerAnswer(false, "spring");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getTotalQuestions()).isEqualTo(3);
        assertThat(summary.getCorrectCount()).isEqualTo(1);
        assertThat(summary.getWrongCount()).isEqualTo(2);
        assertThat(summary.getMistakes()).hasSize(2);
        assertThat(summary.getTopicResults()).hasSize(2);

        SessionSummary.TopicResult springResult = summary.getTopicResults().stream()
                .filter(tr -> "spring".equals(tr.topic()))
                .findFirst().orElseThrow();
        assertThat(springResult.correct()).isEqualTo(0);
        assertThat(springResult.wrong()).isEqualTo(2);
        assertThat(springResult.accuracy()).isEqualTo(0.0);

        assertThat(summary.getRecommendations()).anyMatch(r -> r.contains("spring") && r.contains("рекомендуется повторить"));
    }

    @Test
    void buildSummaryWithEmptySession() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(), null, false, false, false);

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getTotalQuestions()).isEqualTo(0);
        assertThat(summary.getCorrectCount()).isEqualTo(0);
        assertThat(summary.getTopicResults()).isEmpty();
        assertThat(summary.getMistakes()).isEmpty();
        // Регресс-гард: сессия без ответов (0/0, точность 0%) НЕ должна хвалить
        // «Отличный результат!». Раньше allMatch на пустом потоке темрезультатов
        // возвращал true (vacuous truth) → ложная похвала. Рекомендаций быть не должно.
        assertThat(summary.getRecommendations()).noneMatch(r -> r.contains("Отличный результат"));
        assertThat(summary.getRecommendations()).isEmpty();
    }

    @Test
    void buildSummaryCountsUnknownInTotalAndHeadline() {
        // FLOW-03: «не знаю» продвигает сессию, но не в correct/wrong — отдельный
        // счётчик unknown. Он входит в total (снижая accuracy), но НЕ попадает в
        // разбор ошибок (registerUnknown не пишет в answerHistory).
        Question q1 = aQuestion().withId(1).withTopic("java").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1));

        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L), "java", false, false, false);
        session.registerAnswer(true, "java");
        session.registerUnknown("java");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getCorrectCount()).isEqualTo(1);
        assertThat(summary.getWrongCount()).isEqualTo(0);
        assertThat(summary.getUnknownCount()).isEqualTo(1);
        assertThat(summary.getTotalQuestions()).isEqualTo(2);
        assertThat(summary.getAccuracy()).isEqualTo(50.0);
        assertThat(summary.getMistakes()).isEmpty();
    }

    @Test
    void buildSummaryPopulatesDueCountAndTypedNextActions() {
        // FLOW-SUMMARY: dueCount из countDue + типизированный «короткий план»
        // (разобрать ошибки → повторить слабую тему → пройти due).
        Question q1 = aQuestion().withId(1).withTopic("java").withQuestionText("Q1").build();
        Question q2 = aQuestion().withId(2).withTopic("spring").withQuestionText("Q2").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1, q2));
        when(questionStatsRepository.countDue(any(), any(), any(), anyLong())).thenReturn(7L);

        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L), null, false, false, false);
        session.registerAnswer(true, "java");
        session.registerAnswer(false, "spring");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getDueCount()).isEqualTo(7);
        assertThat(summary.getNextActions()).extracting(SessionSummary.NextAction::type)
                .containsExactly(
                        NextActionType.REVIEW_MISTAKES,
                        NextActionType.PRACTICE_WEAK_TOPIC,
                        NextActionType.REVIEW_DUE);
        assertThat(summary.getNextActions().get(0).count()).isEqualTo(1);
        assertThat(summary.getNextActions().get(1).target()).isEqualTo("spring");
        assertThat(summary.getNextActions().get(2).count()).isEqualTo(7);
    }

    @Test
    void buildSummaryAllCorrectProducesKeepGoingNextAction() {
        Question q1 = aQuestion().withId(1).withTopic("java").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1));
        // countDue не застабан → 0 → нет REVIEW_DUE

        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L), "java", false, false, false);
        session.registerAnswer(true, "java");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getDueCount()).isZero();
        assertThat(summary.getNextActions()).extracting(SessionSummary.NextAction::type)
                .containsExactly(NextActionType.KEEP_GOING);
    }

    @Test
    void buildSummaryDueCountFailureIsSwallowed() {
        Question q1 = aQuestion().withId(1).withTopic("java").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1));
        when(questionStatsRepository.countDue(any(), any(), any(), anyLong()))
                .thenThrow(new RuntimeException("db down"));

        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L), "java", false, false, false);
        session.registerAnswer(true, "java");

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getDueCount()).isZero();
        assertThat(summary.getNextActions()).extracting(SessionSummary.NextAction::type)
                .doesNotContain(NextActionType.REVIEW_DUE);
    }

    @Test
    void buildSummaryUsesQuestionTopicWhenAnswerTopicIsNull() {
        Question q1 = aQuestion().withId(1).withTopic("kotlin").withQuestionText("Q about Kotlin").build();
        when(questionRepository.findByIds(anyList())).thenReturn(List.of(q1));

        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L), null, false, false, false);
        session.registerAnswer(false);

        SessionSummary summary = service.buildSummary(session);

        assertThat(summary.getMistakes()).hasSize(1);
        assertThat(summary.getMistakes().get(0).topic()).isEqualTo("kotlin");
        assertThat(summary.getMistakes().get(0).questionText()).isEqualTo("Q about Kotlin");
    }
}
