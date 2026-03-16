package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerPageServiceTest {

    @Mock
    InterviewFacade facade;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    TopicCatalogService topicCatalogService;

    AnswerPageService service;

    @BeforeEach
    void setUp() {
        service = new AnswerPageService(facade, questionRepository, topicCatalogService);
    }

    @Test
    void buildsAnswerStateWithTrainingModeWhenSessionMissing() {
        Question question = new Question(
                1L, "slug", "source", "file", "java", "q", "answer-md",
                false, "hash", QuestionType.TEXT, null, "   ", 0, null,
                Difficulty.MEDIUM, "short explanation long enough",
                "detailed explanation long enough for state building",
                "common mistake", List.of("java"), List.of()
        );
        AnswerOption option = new AnswerOption(10L, 1L, "opt", true, 0, "OPENAI", null);
        AnswerResult result = new AnswerResult(
                question, List.of(option), option, option, true,
                new ReviewState(1L, 0, 1, 2.5, 0L, ReviewResult.NEW, 0, 0),
                AnswerDisplayMode.FULL
        );
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.renderMarkdown("answer-md")).thenReturn("<p>answer</p>");
        when(facade.findRelated(anyLong(), org.mockito.ArgumentMatchers.eq("java"))).thenReturn(List.of());

        AnswerPageService.AnswerPageState state = service.build(result, filter, null);

        assertThat(state.mode()).isEqualTo(InterviewMode.TRAINING);
        assertThat(state.diagram()).isNull();
        assertThat(state.answerHtml()).isEqualTo("<p>answer</p>");
        assertThat(state.stats().total()).isEqualTo(10);
    }
}
