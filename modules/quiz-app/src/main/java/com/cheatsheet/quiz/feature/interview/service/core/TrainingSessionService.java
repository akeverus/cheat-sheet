package com.cheatsheet.quiz.feature.interview.service.core;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис жизненного цикла тренировочной сессии: старт, штрафные вопросы, предзагрузка.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrainingSessionService {

    private final QuestionRepository questionRepository;
    private final UserTopicStatsRepository userTopicStatsRepository;
    private final TopicCatalogService topicCatalogService;
    private final Clock clock;
    private final AppProperties appProperties;

    /**
     * Запускает новую сессию в указанном режиме и инициирует предзагрузку вопросов.
     *
     * @param mode режим сессии
     * @param count количество вопросов
     * @param filter активный фильтр
     * @return созданная сессия
     */
    public InterviewSession startSession(InterviewMode mode, int count, InterviewFilter filter) {
        int limit = Math.max(1, count);
        long now = clock.instant().getEpochSecond();
        List<Long> ids;

        if (filter.isShuffled()) {
            ids = questionRepository.findShuffledQuestionIds(
                    filter.importantOnly(), filter.onlyWrong(), limit);
        } else if (filter.effectiveTopic() != null) {
            ids = questionRepository.findQuestionIdsForSession(
                    filter.effectiveTopic(), filter.importantOnly(), filter.onlyWrong(), now, limit);
        } else {
            List<String> topics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
            List<String> prioritizedTopics = prioritizeTopicsByMastery(topics);
            ids = questionRepository.findQuestionIdsForSessionByTopics(
                    prioritizedTopics, filter.importantOnly(), filter.onlyWrong(), now, limit);
        }

        log.info("training_session_started mode={} requestedCount={} loadedQuestions={} topic={} group={} onlyWrong={}",
                mode, limit, ids.size(), filter.topic(), filter.group(), filter.onlyWrong());
        return new InterviewSession(mode, ids, filter.effectiveTopic(), filter.effectiveGroup(),
                filter.importantOnly(), filter.onlyWrong(), filter.shuffle(), filter.isOrdered());
    }

    /**
     * Добавляет штрафные вопросы в EXAM-сессию после ошибки и предзагружает их.
     *
     * <p>Пул ОГРАНИЧЕН (iter-181): сессия не растёт выше удвоенного стартового
     * размера (и никогда — выше maxSessionCount). До потолка каждый неверный
     * ответ добавлял +examPenaltyQuestions без границы: живой прогон EXAM на
     * 3 вопроса дал рост тотала 3 → 8 → 13 от двух «Не знаю», а при точности
     * банка ~9% двадцативопросный экзамен эскалировал бы до исчерпания банка —
     * прогресс-бар превращался в убегающую цель. Комментарий focus-training
     * обещал «ограниченный штрафной пул» — теперь это правда.</p>
     *
     * @param session активная сессия
     */
    public void addExamPenaltyQuestions(InterviewSession session) {
        if (session.getMode() != InterviewMode.EXAM) {
            return;
        }
        int cap = Math.min(session.getInitialQuestionCount() * 2,
                appProperties.getInterview().getMaxSessionCount());
        int room = cap - session.getQuestionIds().size();
        if (room <= 0) {
            log.info("exam_penalty_pool_exhausted cap={} size={}", cap, session.getQuestionIds().size());
            return;
        }
        List<Long> penaltyIds = questionRepository.findQuestionIdsExcluding(
                session.getQuestionIds(),
                session.getTopic(),
                session.getImportantOnly(),
                session.getOnlyWrong(),
                clock.instant().getEpochSecond(),
                appProperties.getInterview().getExamPenaltyQuestions()
        );
        if ((session.getTopic() == null || session.getTopic().isBlank()) && session.getGroup() != null) {
            InterviewFilter sessionFilter = new InterviewFilter(
                    session.getTopic(),
                    session.getGroup(),
                    session.getImportantOnly(),
                    session.getOnlyWrong(),
                    session.getShuffle(),
                    session.getOrdered()
            );
            List<String> topics = topicCatalogService.topicsForFilter(sessionFilter, questionRepository.findTopics());
            penaltyIds = questionRepository.findQuestionIdsExcludingByTopics(
                    session.getQuestionIds(),
                    topics,
                    session.getImportantOnly(),
                    session.getOnlyWrong(),
                    clock.instant().getEpochSecond(),
                    appProperties.getInterview().getExamPenaltyQuestions()
            );
        }
        if (penaltyIds.size() > room) {
            penaltyIds = penaltyIds.subList(0, room);
        }
        if (!penaltyIds.isEmpty()) {
            session.addPenaltyQuestions(penaltyIds);
            log.info("exam_penalty_questions_added sessionMode={} added={} cap={}",
                    session.getMode(), penaltyIds.size(), cap);
        }
    }

    private List<String> prioritizeTopicsByMastery(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        Map<String, Double> masteryByTopic = topics.stream()
                .collect(Collectors.toMap(
                        topic -> topic,
                        topic -> userTopicStatsRepository.findByTopic(topic)
                                .map(stats -> stats.getMastery())
                                .orElse(0.5d)
                ));
        return topics.stream()
                .sorted(Comparator.comparingDouble(topic -> masteryByTopic.getOrDefault(topic, 0.5d)))
                .toList();
    }
}
