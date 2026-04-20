package com.cheatsheet.quiz.feature.interview.service.core;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.infrastructure.diagram.DiagramService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.option.AIQuestionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис фоновой предзагрузки вариантов ответов и Mermaid-диаграмм.
 *
 * <p>Работает асинхронно через {@link TaskExecutor}: заранее генерирует варианты
 * для следующих вопросов (по фильтру темы/важности), чтобы при переходе к следующему
 * вопросу варианты уже были в кэше или БД.</p>
 *
 * <p>Очереди предзагрузки ограничены по размеру ({@code app.preload.max-queue-size})
 * и количеству очередей ({@code app.preload.max-queues}); при превышении старые очереди
 * очищаются. Пауза между запросами к AI — {@code app.preload.sleep-ms}.</p>
 *
 * <p>{@link #warmupAll()} при старте приложения генерирует варианты для всех вопросов,
 * у которых их ещё нет; прогресс логируется каждые {@code app.preload.log-progress-every} вопросов.</p>
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PreloadService {
    static final int TEST_MODE_DEFAULT_WARMUP_LIMIT = 10;

    AppProperties props;
    QuestionRepository questionRepository;
    AIQuestionService aiQuestionService;
    DiagramService diagramService;
    TopicCatalogService topicCatalogService;
    TaskExecutor preloadExecutor;
    TaskExecutor warmupExecutor;

    /** Максимальный размер одной очереди (из конфигурации). */
    int maxQueueSize;

    /** Максимальное количество одновременных очередей (из конфигурации). */
    int maxQueues;

    /** Пауза между AI-запросами при предзагрузке, мс (из конфигурации). */
    long sleepMs;

    /**
     * Bounded map очередей предзагрузки: ключ = строка фильтра.
     * При превышении лимита — очереди очищаются.
     */
    ConcurrentMap<String, PreloadQueue> queues = new ConcurrentHashMap<>();

    /**
     * Конструктор с внедрением зависимостей и конфигурации предзагрузки.
     */
    public PreloadService(
            AppProperties props,
            QuestionRepository questionRepository,
            AIQuestionService aiQuestionService,
            DiagramService diagramService,
            TopicCatalogService topicCatalogService,
            @Qualifier("preloadExecutor") TaskExecutor preloadExecutor,
            @Qualifier("warmupExecutor") TaskExecutor warmupExecutor
    ) {
        this.props = props;
        this.questionRepository = questionRepository;
        this.aiQuestionService = aiQuestionService;
        this.diagramService = diagramService;
        this.topicCatalogService = topicCatalogService;
        this.preloadExecutor = preloadExecutor;
        this.warmupExecutor = warmupExecutor;
        this.maxQueueSize = props.getPreload().getMaxQueueSize();
        this.maxQueues = props.getPreload().getMaxQueues();
        this.sleepMs = props.getPreload().getSleepMs();
    }

    /**
     * Запускает фоновую предзагрузку вариантов для следующей пачки вопросов.
     *
     * @param filter текущий фильтр тестирования (тема, важные, только с ошибками)
     */
    public void preloadNext(InterviewFilter filter) {
        if (!props.isAiEnabled()) {
            log.info("Preload/warmup skipped: no AI keys configured");
            return;
        }
        int batch = props.getPreload().getBatchSize();
        preloadExecutor.execute(() -> {
            try {
                List<Long> ids = questionRepository.findQuestionIdsForPreload(
                        filter.effectiveTopic(), filter.importantOnly(), filter.onlyWrong(),
                        batch);
                if (filter.effectiveTopic() == null && (filter.effectiveGroup() != null || filter.isOrdered())) {
                    List<String> topics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
                    ids = questionRepository.findQuestionIdsForPreloadByTopics(
                            topics, filter.importantOnly(), filter.onlyWrong(),
                            batch);
                }
                evictQueuesIfNeeded();
                PreloadQueue queue = queues.computeIfAbsent(queueKey(filter), key -> new PreloadQueue());
                List<Question> questions = loadQuestionsPreservingOrder(ids);
                PreloadStats stats = ensureOptionsSinglePass(questions, queue, false);
                log.info("Preload batch completed: requested={}, saved={}, failed={}",
                        questions.size(), stats.saved(), stats.failed());
            } catch (Exception e) {
                log.error("Ошибка предзагрузки: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Полный прогрев: генерирует AI-варианты и диаграммы для всех вопросов, у которых их ещё нет.
     *
     * <p>Запускается в фоне при старте (если включено в конфиге). Прогресс логируется
     * каждые {@code app.preload.log-progress-every} вопросов.</p>
     */
    public void warmupAll() {
        if (!props.isAiEnabled()) {
            log.info("Preload/warmup skipped: no AI keys configured");
            return;
        }
        warmupExecutor.execute(() -> {
            try {
                List<Long> allIds = questionRepository.findAllQuestionIdsWithoutOptions();
                if (allIds.isEmpty()) {
                    log.info("Полный прогрев: все вопросы уже имеют варианты ответов");
                    return;
                }
                int warmupLimit = props.getPreload().getWarmupLimit();
                if (props.getPreload().isTestMode() && warmupLimit <= 0) {
                    warmupLimit = TEST_MODE_DEFAULT_WARMUP_LIMIT;
                }
                List<Long> ids = allIds;
                if (warmupLimit > 0 && allIds.size() > warmupLimit) {
                    ids = pickWarmupSubset(allIds, warmupLimit);
                    log.info("Тестовый режим прогрева: ограничение warmup-limit={}, выбрано {} из {} вопросов (random subset)",
                            warmupLimit, ids.size(), allIds.size());
                }
                log.info("Полный прогрев: {} вопросов без вариантов, начинаю генерацию...", ids.size());
                long startMs = System.currentTimeMillis();
                List<Question> questions = loadQuestionsPreservingOrder(ids);
                PreloadStats stats = ensureOptionsSinglePass(questions, null, true);
                int done = stats.saved() + stats.failed();
                if (done > 0) {
                    int logEvery = props.getPreload().getLogProgressEvery();
                    if (done % logEvery == 0 || done == questions.size()) {
                        long elapsedMs = System.currentTimeMillis() - startMs;
                        double avgMs = (double) elapsedMs / done;
                        long etaMs = (long) (avgMs * Math.max(questions.size() - done, 0));
                        log.info("Полный прогрев: {}/{} ({} %), ETA: {} сек",
                                done, questions.size(),
                                Math.round((double) done / questions.size() * 100),
                                etaMs / 1000);
                    }
                }
                long totalSec = (System.currentTimeMillis() - startMs) / 1000;
                log.info("Полный прогрев завершён: total={}, saved={}, failed={}, elapsed={} сек",
                        questions.size(), stats.saved(), stats.failed(), totalSec);
            } catch (Exception e) {
                log.error("Ошибка полного прогрева: {}", e.getMessage(), e);
            }
        });
    }

    private List<Long> pickWarmupSubset(List<Long> allIds, int warmupLimit) {
        if (allIds == null || allIds.isEmpty() || warmupLimit <= 0 || allIds.size() <= warmupLimit) {
            return allIds;
        }
        List<Long> shuffled = new ArrayList<>(allIds);
        long seed = props.getPreload().getWarmupRandomSeed();
        if (seed >= 0) {
            Collections.shuffle(shuffled, new Random(seed));
        } else {
            Collections.shuffle(shuffled);
        }
        return List.copyOf(shuffled.subList(0, warmupLimit));
    }

    /**
     * Извлекает предзагруженный вопрос из очереди (poll).
     *
     * @param filter фильтр
     * @return ID вопроса или пустой Optional
     */
    public Optional<Long> pollPreloaded(InterviewFilter filter) {
        PreloadQueue queue = queues.get(queueKey(filter));
        if (queue == null) {
            return Optional.empty();
        }
        return queue.poll();
    }

    /**
     * Запускает фоновую предзагрузку вариантов для списка конкретных вопросов.
     *
     * @param ids ID вопросов
     */
    public void preloadQuestions(List<Long> ids) {
        preloadExecutor.execute(() -> {
            List<Question> questions = loadQuestionsPreservingOrder(ids);
            PreloadStats stats = ensureOptionsSinglePass(questions, null, false);
            log.info("Preload specific questions completed: requested={}, saved={}, failed={}",
                    questions.size(), stats.saved(), stats.failed());
        });
    }

    private PreloadStats ensureOptionsSinglePass(List<Question> questions, PreloadQueue queue, boolean generateDiagram) {
        int saved = 0;
        int failed = 0;
        for (Question question : questions) {
            try {
                aiQuestionService.getOrCreateOptions(question);
                if (queue != null) {
                    queue.offer(question.id(), maxQueueSize);
                } else if (generateDiagram) {
                    try {
                        diagramService.getOrGenerateDiagram(question);
                    } catch (Exception e) {
                        log.debug("Диаграмма для вопроса {} пропущена: {}", question.id(), e.getMessage());
                    }
                }
                saved++;
            } catch (AiGenerationException e) {
                failed++;
                log.error("Question {} failed in single-shot preload: {}", question.id(), e.getMessage());
            } catch (Exception e) {
                failed++;
                log.error("Неожиданная ошибка предзагрузки для question {}: {}", question.id(), e.getMessage(), e);
            }
            if (sleepMs > 0) {
                sleepBetweenRequests();
            }
        }
        return new PreloadStats(saved, failed);
    }

    private List<Question> loadQuestionsPreservingOrder(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Map<Long, Question> byId = questionRepository.findByIds(ids).stream()
                .collect(Collectors.toMap(Question::id, Function.identity()));
        return ids.stream()
                .map(byId::get)
                .filter(item -> item != null)
                .toList();
    }

    /**
     * Пауза между AI-запросами (конфигурируется через {@code app.preload.sleep-ms}).
     */
    private void sleepBetweenRequests() {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Удаляет лишние очереди при превышении лимита.
     *
     * <p>Сначала удаляет пустые, при недостаточности — очищает все.</p>
     */
    private void evictQueuesIfNeeded() {
        if (queues.size() >= maxQueues) {
            queues.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        }
        if (queues.size() >= maxQueues) {
            log.info("Очистка всех очередей предзагрузки (size={})", queues.size());
            queues.clear();
        }
    }

    /**
     * Формирует ключ очереди из параметров фильтра.
     */
    private String queueKey(InterviewFilter filter) {
        return (filter.effectiveTopic() == null ? "" : filter.effectiveTopic()) + "|" +
                (filter.effectiveGroup() == null ? "" : filter.effectiveGroup()) + "|" +
                (filter.importantOnly() == null ? "" : filter.importantOnly()) + "|" +
                (filter.onlyWrong() == null ? "" : filter.onlyWrong()) + "|" +
                (filter.isShuffled() ? "shuffle" : "") + "|" +
                (filter.isOrdered() ? "ordered" : "unordered");
    }

    /**
     * Потокобезопасная очередь предзагрузки с дедупликацией.
     *
     * <p>Использует {@link LinkedList} для FIFO-порядка
     * и {@link HashSet} для проверки дубликатов.
     * Все операции выполняются под {@code synchronized(lock)},
     * поэтому concurrent-коллекции не нужны.</p>
     */
    private static class PreloadQueue {
        private final LinkedList<Long> queue = new LinkedList<>();
        private final HashSet<Long> set = new HashSet<>();
        private final Object lock = new Object();

        /**
         * Добавляет ID вопроса в очередь (с ограничением размера).
         *
         * @param id       ID вопроса
         * @param maxSize  максимальный размер очереди
         */
        void offer(Long id, int maxSize) {
            synchronized (lock) {
                if (set.size() >= maxSize) {
                    return;
                }
                if (set.add(id)) {
                    queue.offer(id);
                }
            }
        }

        /**
         * Извлекает следующий ID из очереди (FIFO).
         *
         * @return ID вопроса или пустой Optional
         */
        Optional<Long> poll() {
            synchronized (lock) {
                Long id = queue.poll();
                if (id == null) {
                    return Optional.empty();
                }
                set.remove(id);
                return Optional.of(id);
            }
        }

        boolean isEmpty() {
            synchronized (lock) {
                return queue.isEmpty();
            }
        }
    }

    @Builder(toBuilder = true)
    private record PreloadStats(int saved, int failed) {}
}
