package com.cheatsheet.quiz.feature.interview.service.core;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import com.cheatsheet.quiz.service.strategy.DefaultSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.ShuffleSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.WeakTopicsSelectionStrategy;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/**
 * Основной сервис тестирования перед интервью: управление вопросами, сессиями и ответами.
 *
 * <p>Координирует работу всех компонентов:</p>
 * <ul>
 *   <li>{@link QuestionRepository} — загрузка вопросов;</li>
 *   <li>{@link OptionLookupService} — резолвер вариантов ответа (seed);</li>
 *   <li>{@link SpacedRepetitionService} — алгоритм SM-2;</li>
 *   <li>{@link QuestionStatsRepository} — статистика.</li>
 * </ul>
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewService {
    QuestionRepository questionRepository;
    QuestionStatsRepository questionStatsRepository;
    AnswerOptionRepository answerOptionRepository;
    ReviewStateRepository reviewStateRepository;
    OptionLookupService optionLookupService;
    ApplicationEventPublisher eventPublisher;
    DefaultSelectionStrategy defaultSelectionStrategy;
    ShuffleSelectionStrategy shuffleSelectionStrategy;
    WeakTopicsSelectionStrategy weakTopicsSelectionStrategy;
    TopicCatalogService topicCatalogService;
    TrainingSessionService trainingSessionService;
    ReviewService reviewService;
    Clock clock;

    int learnedRepetitions;

    public InterviewService(
            QuestionRepository questionRepository,
            QuestionStatsRepository questionStatsRepository,
            AnswerOptionRepository answerOptionRepository,
            ReviewStateRepository reviewStateRepository,
            OptionLookupService optionLookupService,
            ApplicationEventPublisher eventPublisher,
            DefaultSelectionStrategy defaultSelectionStrategy,
            ShuffleSelectionStrategy shuffleSelectionStrategy,
            WeakTopicsSelectionStrategy weakTopicsSelectionStrategy,
            TopicCatalogService topicCatalogService,
            TrainingSessionService trainingSessionService,
            ReviewService reviewService,
            Clock clock,
            AppProperties appProperties
    ) {
        this.questionRepository = questionRepository;
        this.questionStatsRepository = questionStatsRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.reviewStateRepository = reviewStateRepository;
        this.optionLookupService = optionLookupService;
        this.eventPublisher = eventPublisher;
        this.defaultSelectionStrategy = defaultSelectionStrategy;
        this.shuffleSelectionStrategy = shuffleSelectionStrategy;
        this.weakTopicsSelectionStrategy = weakTopicsSelectionStrategy;
        this.topicCatalogService = topicCatalogService;
        this.trainingSessionService = trainingSessionService;
        this.reviewService = reviewService;
        this.clock = clock;
        this.learnedRepetitions = appProperties.getInterview().getLearnedRepetitions();
    }

    /**
     * Возвращает следующий вопрос для тестирования с учётом фильтра и интервального повторения.
     *
     * <p>Выбирает due/next вопрос по теме и фильтрам через стратегию отбора.</p>
     *
     * @param filter фильтр (тема, только важные, только с ошибками, перемешивание)
     * @return вопрос с вариантами ответов и состоянием повторения или пустой Optional
     */
    public Optional<InterviewQuestion> nextQuestion(InterviewFilter filter) {
        return nextQuestion(filter, false);
    }

    /**
     * Возвращает следующий вопрос с учётом фильтра и опциональной приоритизации слабых тем.
     *
     * @param filter            фильтр
     * @param weakTopicsPriority true — приоритет слабым темам (Strategy pattern)
     * @return вопрос или пустой Optional
     */
    public Optional<InterviewQuestion> nextQuestion(InterviewFilter filter, boolean weakTopicsPriority) {
        return nextQuestion(filter, weakTopicsPriority, null);
    }

    public Optional<InterviewQuestion> nextQuestion(InterviewFilter filter, boolean weakTopicsPriority, Long excludeQuestionId) {
        long now = clock.instant().getEpochSecond();
        var strategy = filter.isShuffled()
                ? shuffleSelectionStrategy
                : (weakTopicsPriority ? weakTopicsSelectionStrategy : defaultSelectionStrategy);
        Question question = strategy.selectNextQuestionId(filter, now)
                .flatMap(questionRepository::findById)
                .orElse(null);

        if (question == null) {
            return Optional.empty();
        }
        if (excludeQuestionId != null && question.id() == excludeQuestionId) {
            question = findAlternativeQuestion(filter, now, List.of(excludeQuestionId)).orElse(null);
            if (question == null) {
                return Optional.empty();
            }
        }
        return buildInterviewQuestion(question, now);
    }

    private Optional<Question> findAlternativeQuestion(InterviewFilter filter, long now, List<Long> excludeIds) {
        List<Long> candidateIds;
        if (filter.effectiveTopic() != null) {
            candidateIds = questionRepository.findQuestionIdsExcluding(
                    excludeIds, filter.effectiveTopic(), filter.importantOnly(), filter.onlyWrong(), now, 1);
        } else if (filter.effectiveGroup() != null) {
            List<String> topics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
            candidateIds = questionRepository.findQuestionIdsExcludingByTopics(
                    excludeIds, topics, filter.importantOnly(), filter.onlyWrong(), now, 1);
        } else {
            candidateIds = questionRepository.findQuestionIdsExcluding(
                    excludeIds, null, filter.importantOnly(), filter.onlyWrong(), now, 1);
        }
        return candidateIds.stream().findFirst().flatMap(questionRepository::findById);
    }

    /**
     * Возвращает текущий вопрос сессии (по индексу в списке questionIds).
     *
     * @param session активная сессия экзамена/марафона
     * @return текущий вопрос или пустой Optional, если сессия завершена
     */
    public Optional<InterviewQuestion> questionForSession(InterviewSession session) {
        Long id = session.currentQuestionId();
        if (id == null) {
            return Optional.empty();
        }
        return questionRepository.findById(id)
                .flatMap(question -> buildInterviewQuestion(question, clock.instant().getEpochSecond()));
    }

    /**
     * Проверяет существование вопроса и выбрасывает исключение, если вопрос не найден.
     */
    public void ensureQuestionExists(long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
    }

    /**
     * Запускает новую сессию тестирования (экзамен или марафон): формирует список ID вопросов и предзагружает варианты.
     *
     * @param mode   режим (EXAM, MARATHON)
     * @param count  количество вопросов
     * @param filter фильтр (тема, важные, только с ошибками, перемешивание)
     * @return сессия с списком questionIds и параметрами фильтра
     */
    public InterviewSession startSession(InterviewMode mode, int count, InterviewFilter filter) {
        return trainingSessionService.startSession(mode, count, filter);
    }

    /**
     * Добавляет штрафные вопросы в сессию экзамена после неправильного ответа (модифицирует сессию).
     *
     * @param session активная сессия экзамена
     */
    public void addExamPenaltyQuestions(InterviewSession session) {
        trainingSessionService.addExamPenaltyQuestions(session);
    }

    /**
     * Обрабатывает ответ пользователя: проверяет правильность, обновляет интервальное повторение (SM-2), запускает предзагрузку.
     *
     * @param questionId       ID вопроса
     * @param selectedOptionId ID выбранного варианта ответа
     * @param filter           текущий фильтр (для предзагрузки следующего вопроса)
     * @param confidence       оценка уверенности 3-5 (nullable — если null, используется бинарная оценка)
     * @return результат с вопросом, вариантами, выбранным/правильным ответом и обновлённым состоянием повторения
     * @throws QuestionNotFoundException если вопрос не найден
     * @throws OptionNotFoundException   если выбранный вариант не найден
     */
    @Transactional
    public AnswerResult submitAnswer(long questionId, long selectedOptionId, InterviewFilter filter, Integer confidence) {
        ResolvedQuestion resolved = resolveQuestionAndOptions(questionId, selectedOptionId);
        ReviewState updated = reviewService.applyAnswer(resolved.question(), resolved.isCorrect(), confidence);

        AnswerDisplayMode displayMode = AnswerDisplayMode.FULL;

        publishAnswerEvent(questionId, selectedOptionId, resolved);

        return new AnswerResult(
                resolved.question(),
                resolved.options(),
                resolved.selected(),
                resolved.correct(),
                resolved.isCorrect(),
                updated,
                displayMode
        );
    }

    /**
     * Загружает вопрос и находит выбранный и правильный варианты ответа.
     */
    private ResolvedQuestion resolveQuestionAndOptions(long questionId, long optionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(questionId);
        AnswerOption selected = options.stream()
                .filter(option -> option.id() == optionId)
                .findFirst()
                .orElseThrow(() -> new OptionNotFoundException("Вариант не найден: id=" + optionId));
        AnswerOption correct = options.stream()
                .filter(AnswerOption::correct)
                .findFirst()
                .orElse(selected);
        return new ResolvedQuestion(question, options, selected, correct, selected.correct());
    }

    private void publishAnswerEvent(long questionId, long selectedOptionId, ResolvedQuestion resolved) {
        try {
            eventPublisher.publishEvent(new AnswerEvent(
                    this,
                    questionId,
                    selectedOptionId,
                    resolved.correct().id(),
                    resolved.isCorrect(),
                    resolved.question().questionText(),
                    resolved.selected().optionText(),
                    resolved.correct().optionText(),
                    resolved.question().answerMarkdown(),
                    resolved.question().topic()
            ));
        } catch (Exception e) {
            log.warn("Не удалось опубликовать AnswerEvent для вопроса id={}: {}", questionId, e.getMessage());
        }
    }

    @Builder(toBuilder = true)
    private record ResolvedQuestion(
            Question question,
            List<AnswerOption> options,
            AnswerOption selected,
            AnswerOption correct,
            boolean isCorrect
    ) {}

    /**
     * Корректирует ease factor после выбора уверенности пользователем.
     *
     * <p>При первичном ответе SM-2 применяется с grade=5. Если пользователь выбрал
     * иную уверенность (3 или 4), ease factor корректируется на разницу.</p>
     *
     * @param questionId ID вопроса
     * @param grade      оценка уверенности (3-5)
     */
    @Transactional
    public void updateConfidence(long questionId, int grade) {
        reviewService.updateConfidence(questionId, grade);
    }

    /**
     * Обрабатывает самооценку в режиме Flashcard: обновляет SM-2 по заданному grade.
     *
     * @param questionId ID вопроса
     * @param correct    считается ли ответ правильным (grade >= 3)
     * @param sm2Grade   оценка SM-2 (1-5)
     */
    @Transactional
    public void submitFlashcardGrade(long questionId, boolean correct, int sm2Grade) {
        reviewService.submitFlashcardGrade(questionId, correct, sm2Grade);
    }

    private Optional<InterviewQuestion> buildInterviewQuestion(Question question, long nowEpoch) {
        reviewStateRepository.insertIfAbsent(question.id(), nowEpoch);
        ReviewState reviewState = reviewStateRepository.findByQuestionId(question.id())
                .orElse(ReviewDefaults.initialState(question.id(), nowEpoch));
        List<AnswerOption> options = optionLookupService.getOrCreateOptions(question);
        return Optional.of(new InterviewQuestion(question, options, reviewState));
    }

    /**
     * Возвращает агрегированную статистику по вопросам с учётом фильтра (всего, к повторению, выучено, правильные/неправильные).
     *
     * <p>Использует один агрегирующий SQL-запрос вместо 5 отдельных.</p>
     *
     * @param filter фильтр (тема, только важные, только с ошибками)
     * @return статистика
     */
    public InterviewStats getStats(InterviewFilter filter) {
        long now = clock.instant().getEpochSecond();
        String topic = filter.effectiveTopic();
        if (topic == null && filter.effectiveGroup() != null) {
            List<String> topics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
            return questionStatsRepository.getAggregatedStatsByTopics(
                    topics, filter.importantOnly(), filter.onlyWrong(), now, learnedRepetitions);
        }
        return questionStatsRepository.getAggregatedStats(
                topic, filter.importantOnly(), filter.onlyWrong(), now, learnedRepetitions);
    }

    /**
     * Возвращает статистику по темам (всего, выучено, к повторению и т.д. по каждой теме).
     *
     * @return список статистики по темам
     */
    public List<TopicStats> getTopicStats() {
        long now = clock.instant().getEpochSecond();
        return questionStatsRepository.findTopicStats(now, learnedRepetitions);
    }

}
