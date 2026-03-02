package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.domain.TopicStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewFacadeTest {

    @Mock
    private InterviewService interviewService;
    @Mock
    private MarkdownRenderService markdownRenderService;
    @Mock
    private HintService hintService;
    @Mock
    private RelatedQuestionsService relatedQuestionsService;
    @Mock
    private TakeawayService takeawayService;
    @Mock
    private WrongAnswerFeedbackService wrongAnswerFeedbackService;
    @Mock
    private CodeTraceService codeTraceService;

    private InterviewFacade facade;

    @BeforeEach
    void setUp() {
        facade = new InterviewFacade(
                interviewService,
                markdownRenderService,
                hintService,
                relatedQuestionsService,
                takeawayService,
                wrongAnswerFeedbackService,
                codeTraceService
        );
    }

    @Test
    void nextQuestionDelegatesToInterviewService() {
        InterviewFilter filter = new InterviewFilter("java", "core", true, false, true, false);
        Optional<InterviewQuestion> expected = Optional.empty();
        when(interviewService.nextQuestion(filter, true, 77L)).thenReturn(expected);

        Optional<InterviewQuestion> result = facade.nextQuestion(filter, true, 77L);

        assertThat(result).isEqualTo(expected);
        verify(interviewService).nextQuestion(filter, true, 77L);
    }

    @Test
    void renderAndInsightMethodsDelegateToSpecificServices() {
        Hint hint = new Hint(1L, 1L, 2, "hint", 1_700_000_000L);
        when(markdownRenderService.toHtml("md")).thenReturn("<p>md</p>");
        when(hintService.getHint(1L, 2)).thenReturn(Optional.of(hint));
        when(takeawayService.getOrGenerate(3L)).thenReturn(Optional.of("takeaway"));
        when(codeTraceService.getOrGenerate(4L)).thenReturn(Optional.of("trace"));

        assertThat(facade.renderMarkdown("md")).isEqualTo("<p>md</p>");
        assertThat(facade.getHint(1L, 2)).contains(hint);
        assertThat(facade.getTakeaway(3L)).contains("takeaway");
        assertThat(facade.getCodeTrace(4L)).contains("trace");

        verify(markdownRenderService).toHtml("md");
        verify(hintService).getHint(1L, 2);
        verify(takeawayService).getOrGenerate(3L);
        verify(codeTraceService).getOrGenerate(4L);
    }

    @Test
    void statsAndSessionOperationsDelegateToInterviewService() {
        InterviewFilter filter = new InterviewFilter("java", null, false, false, false, true);
        InterviewSession session = new InterviewSession(InterviewMode.EXAM, List.of(1L), "java", null, false, false, false, true);
        InterviewStats stats = org.mockito.Mockito.mock(InterviewStats.class);
        List<TopicStats> topicStats = List.of(org.mockito.Mockito.mock(TopicStats.class));
        when(interviewService.startSession(InterviewMode.EXAM, 10, filter)).thenReturn(session);
        when(interviewService.getStats(filter)).thenReturn(stats);
        when(interviewService.getTopicStats()).thenReturn(topicStats);

        InterviewSession started = facade.startSession(InterviewMode.EXAM, 10, filter);
        InterviewStats actualStats = facade.getStats(filter);
        List<TopicStats> actualTopicStats = facade.getTopicStats();

        assertThat(started).isSameAs(session);
        assertThat(actualStats).isSameAs(stats);
        assertThat(actualTopicStats).isEqualTo(topicStats);
        verify(interviewService).startSession(InterviewMode.EXAM, 10, filter);
        verify(interviewService).getStats(filter);
        verify(interviewService).getTopicStats();
    }

    @Test
    void relatedAndFeedbackOperationsDelegateToDedicatedServices() {
        List<RelatedQuestion> related = List.of(new RelatedQuestion(10L, "q", "java", 90.0));
        when(relatedQuestionsService.findRelated(10L, "java")).thenReturn(related);
        when(wrongAnswerFeedbackService.getFeedback(10L, 1L)).thenReturn(Optional.of("feedback"));
        when(wrongAnswerFeedbackService.generateComparison(10L, 1L)).thenReturn(Optional.of("comparison"));

        assertThat(facade.findRelated(10L, "java")).isEqualTo(related);
        assertThat(facade.getWrongFeedback(10L, 1L)).contains("feedback");
        assertThat(facade.generateComparison(10L, 1L)).contains("comparison");

        verify(relatedQuestionsService).findRelated(10L, "java");
        verify(wrongAnswerFeedbackService).getFeedback(10L, 1L);
        verify(wrongAnswerFeedbackService).generateComparison(10L, 1L);
    }
}
