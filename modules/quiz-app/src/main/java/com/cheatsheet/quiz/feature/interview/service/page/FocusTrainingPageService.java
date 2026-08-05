package com.cheatsheet.quiz.feature.interview.service.page;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.domain.ReviewReason;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewReasonService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.domain.QuestionDifficulty;
import com.cheatsheet.quiz.domain.ContentBlock;
import com.cheatsheet.quiz.domain.FlashcardPhase;
import com.cheatsheet.quiz.domain.StudyPhase;
import com.cheatsheet.quiz.feature.interview.mapper.ContentBlockMapper;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
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
    QuestionRevisionRepository questionRevisionRepository;
    QuestionStatsRepository questionStatsRepository;
    TopicCatalogService topicCatalogService;
    AppProperties appProperties;
    ReviewStateRepository reviewStateRepository;
    ReviewReasonService reviewReasonService;
    LearningMetricsService learningMetricsService;
    ContentBlockMapper contentBlockMapper;

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
        return buildFocusPageState(
                interviewSession, filter, selectedMode, weakTopicsPriority, excludeQuestionId, null);
    }

    /**
     * Строит view-model для главной страницы тренировки.
     *
     * @param pinnedQuestionId optional deep-link на конкретный вопрос (без активной сессии)
     */
    public FocusPageState buildFocusPageState(
            InterviewSession interviewSession,
            InterviewFilter filter,
            InterviewMode selectedMode,
            boolean weakTopicsPriority,
            Long excludeQuestionId,
            Long pinnedQuestionId
    ) {
        Optional<InterviewQuestion> current;
        if (interviewSession != null) {
            current = facade.questionForSession(interviewSession);
        } else if (pinnedQuestionId != null) {
            current = facade.questionById(pinnedQuestionId);
            if (current.isEmpty()) {
                current = facade.nextQuestion(filter, weakTopicsPriority, excludeQuestionId);
            }
        } else {
            current = facade.nextQuestion(filter, weakTopicsPriority, excludeQuestionId);
        }

        SurfaceState surface = buildSurfaceState(filter, selectedMode, interviewSession, weakTopicsPriority);

        boolean studyLearnPhase = interviewSession != null
                && interviewSession.getMode() == InterviewMode.STUDY
                && interviewSession.getStudyPhase() == StudyPhase.LEARN;
        boolean sessionRecallMode = interviewSession != null
                && interviewSession.getMode().isRecallMode();
        boolean typedInputMode = interviewSession != null
                && interviewSession.getMode().usesTypedInput();
        boolean codeChallengeMode = interviewSession != null
                && interviewSession.getMode().usesCodeEditor();
        // AI недоступен: вопрос без seed-вариантов → всегда режим флешкарты.
        boolean noAiFlashcard = current.isPresent()
                && current.get().options().isEmpty();
        boolean flashcardMode = sessionRecallMode || noAiFlashcard;
        boolean flashcardRevealed = sessionRecallMode
                && interviewSession.getFlashcardPhase() == FlashcardPhase.REVEALED;

        String diagram = null;
        String studyAnswerHtml = null;
        QuestionDifficulty difficulty = null;
        ReviewReason reviewReason = null;
        List<ContentBlock> contentBlocks = List.of();
        Long questionRevisionId = null;
        if (current.isPresent()) {
            InterviewQuestion question = current.get();
            contentBlocks = contentBlockMapper.toContentBlocks(question.question());
            if (question.question().diagramMermaid() != null && !question.question().diagramMermaid().isBlank()) {
                diagram = question.question().diagramMermaid();
            }
            // studyAnswerHtml нужен также для client-side reveal флешкарты вне
            // FLASHCARD-сессии (browse по /?topic=X или вопрос без вариантов):
            // там серверный POST /flashcard-reveal не сработает (нет сессии), и
            // ответ раскрывается на клиенте — значит HTML должен быть в странице.
            boolean clientReveal = flashcardMode && !sessionRecallMode;
            if (studyLearnPhase || flashcardRevealed || clientReveal) {
                studyAnswerHtml = facade.renderMarkdown(question.question().answerMarkdown());
            }
            long questionId = question.question().id();
            questionRevisionId = questionRevisionRepository.findLatest(questionId)
                    .map(rev -> rev.id())
                    .orElse(null);
            difficulty = questionStatsRepository.getDifficulty(questionId);
            // Explainable-причина повторения (FSRS/SM-2): почему вопрос перед
            // тобой сейчас. Считаем из уже накопленных сигналов состояния.
            //
            // На экзамене причину не показываем и не считаем. Тексты причин —
            // тренерские подсказки («Недавно была ошибка», «Тема пока слабая
            // (точность 40%)»): в тренировке это и есть смысл, но перед вопросом
            // экзамена они подсказывают ответ и обесценивают итоговый балл.
            // Раньше вместо них печаталось «Часть экзаменационной сессии» — факт
            // о сессии, а не о вопросе, повторявший чип режима над ним и рельс
            // прогресса рядом; один и тот же экран сообщал про экзамен трижды.
            // Заодно экономим три запроса на каждом вопросе экзамена: их
            // результат всё равно шёл в выброшенную причину.
            boolean examMode = interviewSession != null
                    && interviewSession.getMode() == InterviewMode.EXAM;
            if (!examMode) {
                reviewReason = reviewReasonService.classify(
                        reviewStateRepository.findByQuestionId(questionId),
                        reviewStateRepository.findFsrsState(questionId),
                        questionStatsRepository.getTopicAccuracy(question.question().topic()),
                        question.question().important()
                ).orElse(null);
            }
        }

        return new FocusPageState(
                current,
                studyLearnPhase,
                flashcardMode,
                flashcardRevealed,
                sessionRecallMode,
                typedInputMode,
                codeChallengeMode,
                diagram,
                studyAnswerHtml,
                difficulty,
                progressPercent(interviewSession),
                surface,
                reviewReason,
                contentBlocks,
                questionRevisionId
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
        // KPI sidebar — только LearningMetrics (единые знаменатели).
        // Виджеты «сегодня» живут в границах фильтра и получают целую статистику,
        // а не одно число: числитель и знаменатель обязаны быть из одной выборки.
        // Пока сюда уезжал голый due, виджет писал «4 из 9818» — четыре по фильтру,
        // девять тысяч по всей базе.
        LearningMetrics metrics = learningMetricsService.compute();
        InterviewStats stats = InterviewStats.from(metrics);
        InterviewStats filteredStats = facade.getStats(filter);
        // Counts на filter-chips: размер пула под каждый быстрый фильтр (без пересечений).
        long filterFavoritesCount = questionStatsRepository.countAllFiltered(null, true, null);
        long filterWrongCount = questionStatsRepository.countAllFiltered(null, null, true);
        long filterWeakTopicsCount = questionStatsRepository.countWeakTopics(QuizConstants.WEAK_TOPIC_THRESHOLD);
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
                filteredStats,
                topics,
                topicCatalogService.groupOptions(availableTopics),
                selectedGroup,
                filter,
                mode,
                interviewSession,
                weakTopicsPriority,
                filterFavoritesCount,
                filterWrongCount,
                filterWeakTopicsCount
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
            boolean sessionRecallMode,
            boolean typedInputMode,
            boolean codeChallengeMode,
            String diagram,
            String studyAnswerHtml,
            QuestionDifficulty difficulty,
            double progressPercent,
            SurfaceState surface,
            ReviewReason reviewReason,
            List<ContentBlock> contentBlocks,
            Long questionRevisionId
    ) {
        public FocusPageState(Optional<InterviewQuestion> current,
                              boolean studyLearnPhase,
                              boolean flashcardMode,
                              boolean flashcardRevealed,
                              String diagram,
                              String studyAnswerHtml,
                              QuestionDifficulty difficulty,
                              double progressPercent,
                              SurfaceState surface,
                              ReviewReason reviewReason) {
            this(current, studyLearnPhase, flashcardMode, flashcardRevealed, false, false, false,
                    diagram, studyAnswerHtml, difficulty, progressPercent, surface, reviewReason, List.of(), null);
        }

        public FocusPageState(Optional<InterviewQuestion> current,
                              boolean studyLearnPhase,
                              boolean flashcardMode,
                              boolean flashcardRevealed,
                              String diagram,
                              String studyAnswerHtml,
                              QuestionDifficulty difficulty,
                              double progressPercent,
                              SurfaceState surface,
                              ReviewReason reviewReason,
                              List<ContentBlock> contentBlocks) {
            this(current, studyLearnPhase, flashcardMode, flashcardRevealed, false, false, false,
                    diagram, studyAnswerHtml, difficulty, progressPercent, surface, reviewReason, contentBlocks, null);
        }

        public FocusPageState {
            if (contentBlocks == null) {
                contentBlocks = List.of();
            }
        }

        /**
         * Блок «Пример кода», если у вопроса есть настоящий код. Шаблон берёт вкладки
         * отсюда, а не из полей вопроса: тип и заголовок назначает только
         * {@link ContentBlockMapper} (там же отсеиваются нарисованные псевдографикой
         * схемы, попавшие в {@code codeSnippet}).
         */
        public ContentBlock codeBlock() {
            return blockById(ContentBlockMapper.CODE_BLOCK_ID);
        }

        /** Блок схемы — mermaid или текстовый рисунок; {@code null}, если схемы нет. */
        public ContentBlock diagramBlock() {
            return blockById(ContentBlockMapper.DIAGRAM_BLOCK_ID);
        }

        private ContentBlock blockById(String id) {
            return contentBlocks.stream()
                    .filter(b -> id.equals(b.id()))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Общая обвязка экранов. Две статистики намеренно разные:
     * {@code stats} — глобальные метрики для KPI в сайдбаре (единые знаменатели
     * на всех страницах), {@code filteredStats} — та же структура в границах
     * текущего фильтра, для виджетов «сегодня».
     */
    @Builder(toBuilder = true)
    public record SurfaceState(
            InterviewStats stats,
            InterviewStats filteredStats,
            List<String> topics,
            List<?> groups,
            String selectedGroup,
            InterviewFilter filter,
            InterviewMode mode,
            InterviewSession interviewSession,
            boolean weakTopics,
            long filterFavoritesCount,
            long filterWrongCount,
            long filterWeakTopicsCount
    ) {
    }
}
