package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ReviewReason;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewReasonService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.domain.QuestionDifficulty;
import com.cheatsheet.quiz.domain.FlashcardPhase;
import com.cheatsheet.quiz.domain.StudyPhase;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис подготовки данных страницы фокус-тренировки.
 *
 * <p>Собирает состояние сессии, текущий вопрос, surface-метаданные, режимы и индикаторы UX,
 * чтобы контроллер оставался thin и не содержал orchestration-логики.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FocusTrainingPageService {
    InterviewFacade facade;
    QuestionRepository questionRepository;
    QuestionStatsRepository questionStatsRepository;
    TopicCatalogService topicCatalogService;
    AppProperties appProperties;
    ReviewStateRepository reviewStateRepository;
    ReviewReasonService reviewReasonService;

    /**
     * Строит view-model для главной страницы тренировки.
     */
    public FocusPageState buildFocusPageState(
            InterviewSession interviewSession,
            InterviewFilter filter,
            InterviewMode selectedMode,
            boolean weakTopicsPriority,
            Long excludeQuestionId
    ) {
        Optional<InterviewQuestion> current = interviewSession != null
                ? facade.questionForSession(interviewSession)
                : facade.nextQuestion(filter, weakTopicsPriority, excludeQuestionId);

        SurfaceState surface = buildSurfaceState(filter, selectedMode, interviewSession, weakTopicsPriority);

        boolean studyLearnPhase = interviewSession != null
                && interviewSession.getMode() == InterviewMode.STUDY
                && interviewSession.getStudyPhase() == StudyPhase.LEARN;
        boolean sessionFlashcardMode = interviewSession != null
                && interviewSession.getMode() == InterviewMode.FLASHCARD;
        // AI недоступен: вопрос без seed-вариантов → всегда режим флешкарты.
        boolean noAiFlashcard = current.isPresent()
                && current.get().options().isEmpty();
        boolean flashcardMode = sessionFlashcardMode || noAiFlashcard;
        boolean flashcardRevealed = sessionFlashcardMode
                && interviewSession.getFlashcardPhase() == FlashcardPhase.REVEALED;

        String diagram = null;
        String studyAnswerHtml = null;
        QuestionDifficulty difficulty = null;
        ReviewReason reviewReason = null;
        if (current.isPresent()) {
            InterviewQuestion question = current.get();
            if (question.question().diagramMermaid() != null && !question.question().diagramMermaid().isBlank()) {
                diagram = question.question().diagramMermaid();
            }
            // studyAnswerHtml нужен также для client-side reveal флешкарты вне
            // FLASHCARD-сессии (browse по /?topic=X или вопрос без вариантов):
            // там серверный POST /flashcard-reveal не сработает (нет сессии), и
            // ответ раскрывается на клиенте — значит HTML должен быть в странице.
            boolean clientReveal = flashcardMode && !sessionFlashcardMode;
            if (studyLearnPhase || flashcardRevealed || clientReveal) {
                studyAnswerHtml = facade.renderMarkdown(question.question().answerMarkdown());
            }
            long questionId = question.question().id();
            difficulty = questionStatsRepository.getDifficulty(questionId);
            // Explainable-причина повторения (FSRS/SM-2): почему вопрос перед
            // тобой сейчас. Считаем из уже накопленных сигналов состояния.
            double topicAccuracy = questionStatsRepository.getTopicAccuracy(question.question().topic());
            reviewReason = reviewReasonService.classify(
                    reviewStateRepository.findByQuestionId(questionId),
                    reviewStateRepository.findFsrsState(questionId),
                    topicAccuracy
            ).orElse(null);
        }

        return new FocusPageState(
                current,
                studyLearnPhase,
                flashcardMode,
                flashcardRevealed,
                diagram,
                studyAnswerHtml,
                difficulty,
                progressPercent(interviewSession),
                surface,
                reviewReason
        );
    }

    /**
     * Строит shared surface-модель (фильтры/топики/статистика) для экранов MVC.
     */
    public SurfaceState buildSurfaceState(
            InterviewFilter filter,
            InterviewMode mode,
            InterviewSession interviewSession,
            boolean weakTopicsPriority
    ) {
        InterviewStats stats = facade.getStats(filter);
        List<String> availableTopics = questionRepository.findTopics();
        String selectedGroup = topicCatalogService.normalizeGroup(filter.group());
        List<String> topics = topicCatalogService.filterAndSortTopics(availableTopics, selectedGroup, filter.isOrdered());
        if (filter.topic() != null && !topics.contains(filter.topic())) {
            List<String> withCurrentTopic = new ArrayList<>();
            withCurrentTopic.add(filter.topic());
            withCurrentTopic.addAll(topics);
            topics = withCurrentTopic;
        }
        return new SurfaceState(
                stats,
                topics,
                topicCatalogService.groupOptions(availableTopics),
                selectedGroup,
                filter,
                mode,
                interviewSession,
                weakTopicsPriority
        );
    }

    private static double progressPercent(InterviewSession interviewSession) {
        if (interviewSession == null || interviewSession.getTotal() <= 0) {
            return 0.0;
        }
        return ((interviewSession.getIndex() + 1) * 100.0) / interviewSession.getTotal();
    }

    @Builder(toBuilder = true)
    public record FocusPageState(
            Optional<InterviewQuestion> current,
            boolean studyLearnPhase,
            boolean flashcardMode,
            boolean flashcardRevealed,
            String diagram,
            String studyAnswerHtml,
            QuestionDifficulty difficulty,
            double progressPercent,
            SurfaceState surface,
            ReviewReason reviewReason
    ) {
    }

    @Builder(toBuilder = true)
    public record SurfaceState(
            InterviewStats stats,
            List<String> topics,
            List<?> groups,
            String selectedGroup,
            InterviewFilter filter,
            InterviewMode mode,
            InterviewSession interviewSession,
            boolean weakTopics
    ) {
    }
}
