package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.List;

/**
 * Сервис подготовки модели страницы результата ответа (`result`).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerPageService {
    InterviewFacade facade;
    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;
    ReviewStateRepository reviewStateRepository;

    /**
     * Строит state для отображения страницы результата ответа.
     */
    public AnswerPageState build(AnswerResult result, InterviewFilter filter, InterviewSession interviewSession) {
        InterviewStats stats = facade.getStats(filter);
        List<String> availableTopics = questionRepository.findTopics();
        String selectedGroup = topicCatalogService.normalizeGroup(filter.group());
        List<String> topics = topicCatalogService.filterAndSortTopics(
                availableTopics,
                selectedGroup,
                filter.isOrdered()
        );
        String diagram = result.question().diagramMermaid();
        if (diagram != null && diagram.isBlank()) {
            diagram = null;
        }
        ReviewState reviewState = reviewStateRepository.findByQuestionId(result.question().id()).orElse(null);
        log.info("answer_page_state_built questionId={} mode={} topic={} group={}",
                result.question().id(),
                interviewSession != null ? interviewSession.getMode() : InterviewMode.TRAINING,
                filter.topic(),
                selectedGroup);
        return new AnswerPageState(
                result,
                facade.renderMarkdown(result.question().answerMarkdown()),
                result.answerDisplayMode().name(),
                stats,
                topics,
                topicCatalogService.groupOptions(availableTopics),
                selectedGroup,
                filter,
                interviewSession != null ? interviewSession.getMode() : InterviewMode.TRAINING,
                interviewSession,
                diagram,
                facade.findRelated(result.question().id(), result.question().topic()),
                reviewState
        );
    }

    @Builder(toBuilder = true)
    public record AnswerPageState(
            com.cheatsheet.quiz.domain.AnswerResult result,
            String answerHtml,
            String answerDisplayMode,
            InterviewStats stats,
            List<String> topics,
            List<?> groups,
            String selectedGroup,
            InterviewFilter filter,
            InterviewMode mode,
            InterviewSession interviewSession,
            String diagram,
            List<com.cheatsheet.quiz.domain.RelatedQuestion> relatedQuestions,
            ReviewState reviewState
    ) {
        public AnswerPageState(com.cheatsheet.quiz.domain.AnswerResult result,
                               String answerHtml, String answerDisplayMode,
                               InterviewStats stats, List<String> topics, List<?> groups,
                               String selectedGroup, InterviewFilter filter, InterviewMode mode,
                               InterviewSession interviewSession, String diagram,
                               List<com.cheatsheet.quiz.domain.RelatedQuestion> relatedQuestions) {
            this(result, answerHtml, answerDisplayMode, stats, topics, groups, selectedGroup,
                    filter, mode, interviewSession, diagram, relatedQuestions, null);
        }
    }
}
