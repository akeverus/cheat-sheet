package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import lombok.AccessLevel;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Строит {@link SessionSummary} из завершённой сессии (Builder pattern).
 *
 * <p>Анализирует историю ответов, группирует по темам, определяет слабые места
 * и формирует рекомендации.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionSummaryService {

    private final QuestionRepository questionRepository;

    /**
     * Строит сводку завершённой сессии.
     *
     * @param session завершённая сессия с историей ответов
     * @return объект сводки
     */
    public SessionSummary buildSummary(InterviewSession session) {
        List<SessionSummary.TopicResult> topicResults = collectTopicResults(session);
        List<SessionSummary.MistakeDetail> mistakes = findMistakes(session);

        Duration duration = session.getStartedAt() != null
                ? Duration.between(session.getStartedAt(), Instant.now())
                : null;

        SessionSummary.Builder builder = SessionSummary.builder()
                .totalQuestions(session.getCorrect() + session.getWrong())
                .correctCount(session.getCorrect())
                .wrongCount(session.getWrong())
                .duration(duration)
                .mode(session.getMode());

        addTopicResultsAndWeakRecommendations(builder, topicResults);
        addMistakes(builder, mistakes);
        addFinalRecommendationIfAllGood(builder, topicResults);

        return builder.build();
    }

    private Map<Long, Question> loadQuestionMap(List<InterviewSession.AnswerRecord> history) {
        List<Long> questionIds = history.stream().map(InterviewSession.AnswerRecord::questionId).toList();
        Map<Long, Question> questionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            questionRepository.findByIds(questionIds).forEach(q -> questionMap.put(q.id(), q));
        }
        return questionMap;
    }

    /**
     * Группирует ответы по темам и вычисляет correct/wrong по каждой теме.
     */
    private List<SessionSummary.TopicResult> collectTopicResults(InterviewSession session) {
        List<InterviewSession.AnswerRecord> history = session.getAnswerHistory();
        Map<Long, Question> questionMap = loadQuestionMap(history);
        Map<String, int[]> topicCounts = new HashMap<>(); // topic -> [correct, wrong]
        for (var record : history) {
            String topic = record.topic();
            if (topic == null) {
                Question q = questionMap.get(record.questionId());
                topic = q != null ? q.topic() : "unknown";
            }
            topicCounts.computeIfAbsent(topic, k -> new int[2]);
            if (record.correct()) {
                topicCounts.get(topic)[0]++;
            } else {
                topicCounts.get(topic)[1]++;
            }
        }
        List<SessionSummary.TopicResult> result = new java.util.ArrayList<>();
        for (var entry : topicCounts.entrySet()) {
            String topic = entry.getKey();
            int correct = entry.getValue()[0];
            int wrong = entry.getValue()[1];
            int total = correct + wrong;
            double accuracy = computeScore(correct, wrong);
            result.add(new SessionSummary.TopicResult(topic, total, correct, wrong, accuracy));
        }
        return result;
    }

    /**
     * Извлекает вопросы, на которые пользователь ответил неправильно.
     */
    private List<SessionSummary.MistakeDetail> findMistakes(InterviewSession session) {
        List<InterviewSession.AnswerRecord> history = session.getAnswerHistory();
        Map<Long, Question> questionMap = loadQuestionMap(history);
        List<SessionSummary.MistakeDetail> result = new java.util.ArrayList<>();
        for (var record : history) {
            if (!record.correct()) {
                Question q = questionMap.get(record.questionId());
                String text = q != null ? q.questionText() : "Вопрос #" + record.questionId();
                String topic = record.topic() != null ? record.topic() : (q != null ? q.topic() : "unknown");
                result.add(new SessionSummary.MistakeDetail(record.questionId(), text, topic));
            }
        }
        return result;
    }

    private void addTopicResultsAndWeakRecommendations(SessionSummary.Builder builder,
                                                       List<SessionSummary.TopicResult> topicResults) {
        for (SessionSummary.TopicResult tr : topicResults) {
            builder.addTopicResult(tr);
            if (tr.accuracy() < QuizConstants.WEAK_TOPIC_THRESHOLD && tr.total() > 0) {
                String shortTopic = tr.topic().contains("/")
                        ? tr.topic().substring(tr.topic().lastIndexOf('/') + 1)
                        : tr.topic();
                builder.addRecommendation(
                        "Тема \"" + shortTopic.replace("-interview", "") + "\" — точность "
                                + String.format("%.0f", tr.accuracy()) + "%, рекомендуется повторить"
                );
            }
        }
    }

    private void addMistakes(SessionSummary.Builder builder, List<SessionSummary.MistakeDetail> mistakes) {
        for (SessionSummary.MistakeDetail m : mistakes) {
            builder.addMistake(m);
        }
    }

    private void addFinalRecommendationIfAllGood(SessionSummary.Builder builder,
                                                 List<SessionSummary.TopicResult> topicResults) {
        if (topicResults.stream().allMatch(tr ->
                tr.total() == 0 || tr.accuracy() >= QuizConstants.WEAK_TOPIC_THRESHOLD)) {
            builder.addRecommendation("Отличный результат! Все темы выше порога " + (int) QuizConstants.WEAK_TOPIC_THRESHOLD + "%.");
        }
    }

    /**
     * Вычисляет процент правильных ответов (0–100). При total=0 возвращает 0.
     */
    private double computeScore(int correct, int wrong) {
        int total = correct + wrong;
        return total > 0 ? (correct * 100.0) / total : 0.0;
    }
}
