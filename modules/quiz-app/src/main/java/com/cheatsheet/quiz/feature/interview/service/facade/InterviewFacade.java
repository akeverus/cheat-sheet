package com.cheatsheet.quiz.feature.interview.service.facade;

import com.cheatsheet.quiz.domain.*;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.insight.RelatedQuestionsService;
import com.cheatsheet.quiz.infrastructure.render.MarkdownRenderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Фасад, объединяющий часто используемые interview-сервисы.
 *
 * <p>Снижает количество зависимостей контроллеров, делегируя вызовы
 * соответствующим сервисам.</p>
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewFacade {
    InterviewService interviewService;
    MarkdownRenderService markdownRenderService;
    RelatedQuestionsService relatedQuestionsService;

    public Optional<InterviewQuestion> nextQuestion(InterviewFilter filter, boolean weakTopicsPriority) {
        return interviewService.nextQuestion(filter, weakTopicsPriority);
    }

    public Optional<InterviewQuestion> nextQuestion(InterviewFilter filter, boolean weakTopicsPriority, Long excludeQuestionId) {
        return interviewService.nextQuestion(filter, weakTopicsPriority, excludeQuestionId);
    }

    public Optional<InterviewQuestion> questionForSession(InterviewSession session) {
        return interviewService.questionForSession(session);
    }

    public InterviewStats getStats(InterviewFilter filter) {
        return interviewService.getStats(filter);
    }

    public void ensureQuestionExists(long questionId) {
        interviewService.ensureQuestionExists(questionId);
    }

    public InterviewSession startSession(InterviewMode mode, int total, InterviewFilter filter) {
        return interviewService.startSession(mode, total, filter);
    }

    public void submitFlashcardGrade(long questionId, boolean correct, int sm2Grade) {
        interviewService.submitFlashcardGrade(questionId, correct, sm2Grade);
    }

    public void updateConfidence(long questionId, int grade) {
        interviewService.updateConfidence(questionId, grade);
    }

    public String renderMarkdown(String markdown) {
        return markdownRenderService.toHtml(markdown);
    }

    public List<RelatedQuestion> findRelated(long questionId, String topic) {
        return relatedQuestionsService.findRelated(questionId, topic);
    }

    public List<TopicStats> getTopicStats() {
        return interviewService.getTopicStats();
    }
}
