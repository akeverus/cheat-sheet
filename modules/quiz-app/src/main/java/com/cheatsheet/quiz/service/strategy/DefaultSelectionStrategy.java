package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Стратегия по умолчанию: выбирает due-вопросы, затем следующие по теме.
 *
 * <p>Извлечена из {@code InterviewService.nextQuestion()} для соответствия
 * принципу единой ответственности.</p>
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultSelectionStrategy implements QuestionSelectionStrategy {

    private final QuestionRepository questionRepository;
    private final TopicCatalogService topicCatalogService;

    @Override
    public Optional<Long> selectNextQuestionId(InterviewFilter filter, long nowEpoch) {
        String topic = filter.effectiveTopic();
        if (topic != null) {
            List<Question> due = questionRepository.findDueQuestions(
                    topic, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1);
            if (!due.isEmpty()) {
                return Optional.of(due.get(0).id());
            }
            return questionRepository.findNextQuestions(
                            topic, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1)
                    .stream().findFirst().map(Question::id);
        }

        List<String> topics = topicCatalogService.topicsForFilter(filter, questionRepository.findTopics());
        if (topics.isEmpty()) {
            return Optional.empty();
        }
        List<Question> due = questionRepository.findDueQuestionsByTopics(
                topics, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1);
        if (!due.isEmpty()) {
            return Optional.of(due.get(0).id());
        }
        return questionRepository.findNextQuestionsByTopics(
                        topics, filter.importantOnly(), filter.onlyWrong(), nowEpoch, 1)
                .stream().findFirst().map(Question::id);
    }
}
