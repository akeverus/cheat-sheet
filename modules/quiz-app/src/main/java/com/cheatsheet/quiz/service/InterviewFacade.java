package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.*;
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
public class InterviewFacade {

    private final InterviewService interviewService;
    private final MarkdownRenderService markdownRenderService;
    private final HintService hintService;
    private final RelatedQuestionsService relatedQuestionsService;
    private final TakeawayService takeawayService;
    private final WrongAnswerFeedbackService wrongAnswerFeedbackService;
    private final CodeTraceService codeTraceService;

    public InterviewFacade(
            InterviewService interviewService,
            MarkdownRenderService markdownRenderService,
            HintService hintService,
            RelatedQuestionsService relatedQuestionsService,
            TakeawayService takeawayService,
            WrongAnswerFeedbackService wrongAnswerFeedbackService,
            CodeTraceService codeTraceService
    ) {
        this.interviewService = interviewService;
        this.markdownRenderService = markdownRenderService;
        this.hintService = hintService;
        this.relatedQuestionsService = relatedQuestionsService;
        this.takeawayService = takeawayService;
        this.wrongAnswerFeedbackService = wrongAnswerFeedbackService;
        this.codeTraceService = codeTraceService;
    }

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

    public Optional<Hint> getHint(long questionId, int level) {
        return hintService.getHint(questionId, level);
    }

    public List<RelatedQuestion> findRelated(long questionId, String topic) {
        return relatedQuestionsService.findRelated(questionId, topic);
    }

    public Optional<String> getTakeaway(long questionId) {
        return takeawayService.getOrGenerate(questionId);
    }

    public Optional<String> getWrongFeedback(long questionId, long optionId) {
        return wrongAnswerFeedbackService.getFeedback(questionId, optionId);
    }

    public Optional<String> generateComparison(long questionId, long selectedOptionId) {
        return wrongAnswerFeedbackService.generateComparison(questionId, selectedOptionId);
    }

    public Optional<String> getCodeTrace(long questionId) {
        return codeTraceService.getOrGenerate(questionId);
    }

    public List<TopicStats> getTopicStats() {
        return interviewService.getTopicStats();
    }
}
