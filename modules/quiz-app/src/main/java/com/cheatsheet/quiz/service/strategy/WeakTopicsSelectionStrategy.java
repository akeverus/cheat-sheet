package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Стратегия приоритизации слабых тем: выбирает вопросы из темы с
 * наименьшей точностью ответов (accuracy < 70%).
 *
 * <p>Если слабых тем нет, делегирует к {@link DefaultSelectionStrategy}.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeakTopicsSelectionStrategy implements QuestionSelectionStrategy {

    private final QuestionRepository questionRepository;
    private final QuestionStatsRepository questionStatsRepository;
    private final DefaultSelectionStrategy defaultStrategy;
    private final TopicCatalogService topicCatalogService;

    @Override
    public Optional<Long> selectNextQuestionId(InterviewFilter filter, long nowEpoch) {
        String weakTopic = questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD);

        if (weakTopic == null) {
            log.debug("Нет слабых тем (accuracy >= {}%), используем стратегию по умолчанию", QuizConstants.WEAK_TOPIC_THRESHOLD);
            return defaultStrategy.selectNextQuestionId(filter, nowEpoch);
        }

        if (filter.effectiveGroup() != null) {
            var allowedTopics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
            if (!allowedTopics.contains(weakTopic)) {
                log.debug("Слабая тема '{}' вне выбранной группы '{}', используем стратегию по умолчанию",
                        weakTopic, filter.effectiveGroup());
                return defaultStrategy.selectNextQuestionId(filter, nowEpoch);
            }
        }

        log.debug("Слабая тема: '{}', выбираем due-вопрос оттуда", weakTopic);

        List<Question> due = questionRepository.findDueQuestions(
                weakTopic, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1);
        if (!due.isEmpty()) {
            return Optional.of(due.get(0).id());
        }

        // Если нет due — берём next из слабой темы
        return questionRepository.findNextQuestions(
                        weakTopic, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1)
                .stream().findFirst().map(Question::id);
    }
}
