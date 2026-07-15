package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.NextActionType;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import lombok.AccessLevel;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
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
    private final QuestionStatsRepository questionStatsRepository;
    private final Clock clock;

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

        int dueCount = countDue(session);

        SessionSummary.Builder builder = SessionSummary.builder()
                .totalQuestions(session.getCorrect() + session.getWrong() + session.getUnknown())
                .correctCount(session.getCorrect())
                .wrongCount(session.getWrong())
                .unknownCount(session.getUnknown())
                .dueCount(dueCount)
                .duration(duration)
                .mode(session.getMode());

        addTopicResultsAndWeakRecommendations(builder, topicResults);
        addMistakes(builder, mistakes);
        addFinalRecommendationIfAllGood(builder, topicResults);
        addNextActions(builder, topicResults, mistakes, dueCount);

        return builder.build();
    }

    /**
     * Считает, сколько вопросов в скоупе сессии подошло к повторению по SM-2 (due).
     * Служит headline-числом «N ждут повторения» в итогах. Ошибка запроса → 0
     * (итоги не должны падать из-за счётчика).
     */
    private int countDue(InterviewSession session) {
        try {
            long now = clock.instant().getEpochSecond();
            long due = questionStatsRepository.countDue(
                    session.getTopic(), session.getImportantOnly(), session.getOnlyWrong(), now);
            return (int) Math.min(due, Integer.MAX_VALUE);
        } catch (RuntimeException e) {
            log.warn("Не удалось посчитать due для итогов сессии: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Формирует типизированный «короткий план» (FLOW-SUMMARY): разобрать ошибки →
     * повторить слабые темы → пройти due → (если всё хорошо) поддержать темп.
     * Дублирует источники строк-рекомендаций, но в машиночитаемом виде — шаблон
     * рендерит кнопки по {@link NextActionType}, не парся текст.
     */
    private void addNextActions(SessionSummary.Builder builder,
                                List<SessionSummary.TopicResult> topicResults,
                                List<SessionSummary.MistakeDetail> mistakes,
                                int dueCount) {
        boolean anyAnswered = topicResults.stream().anyMatch(tr -> tr.total() > 0);

        if (!mistakes.isEmpty()) {
            builder.addNextAction(new SessionSummary.NextAction(
                    NextActionType.REVIEW_MISTAKES,
                    "Разобрать ошибки: " + mistakes.size(),
                    null,
                    mistakes.size()));
        }

        for (SessionSummary.TopicResult tr : topicResults) {
            if (tr.accuracy() < QuizConstants.WEAK_TOPIC_THRESHOLD && tr.total() > 0) {
                builder.addNextAction(new SessionSummary.NextAction(
                        NextActionType.PRACTICE_WEAK_TOPIC,
                        "Повторить тему: " + shortTopic(tr.topic()),
                        tr.topic(),
                        tr.wrong()));
            }
        }

        if (dueCount > 0) {
            builder.addNextAction(new SessionSummary.NextAction(
                    NextActionType.REVIEW_DUE,
                    "Ждут повторения: " + dueCount,
                    null,
                    dueCount));
        }

        boolean allGood = topicResults.stream().allMatch(tr ->
                tr.total() == 0 || tr.accuracy() >= QuizConstants.WEAK_TOPIC_THRESHOLD);
        if (mistakes.isEmpty() && anyAnswered && allGood) {
            builder.addNextAction(new SessionSummary.NextAction(
                    NextActionType.KEEP_GOING,
                    "Отличный темп — продолжайте",
                    null,
                    0));
        }
    }

    /** Короткое имя темы: последний сегмент пути без суффикса {@code -interview}. */
    private String shortTopic(String topic) {
        String shortTopic = topic.contains("/")
                ? topic.substring(topic.lastIndexOf('/') + 1)
                : topic;
        return shortTopic.replace("-interview", "");
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
                builder.addRecommendation(
                        "Тема \"" + shortTopic(tr.topic()) + "\" — точность "
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
        // anyAnswered ОБЯЗАТЕЛЕН: allMatch на пустом потоке возвращает true
        // (vacuous truth), из-за чего сессия без ответов (0/0) ложно показывала
        // «Отличный результат! Все темы выше порога 70%» при точности 0%.
        // Хвалим только когда реально были отвечены вопросы И все темы ≥ порога.
        boolean anyAnswered = topicResults.stream().anyMatch(tr -> tr.total() > 0);
        boolean allGood = topicResults.stream().allMatch(tr ->
                tr.total() == 0 || tr.accuracy() >= QuizConstants.WEAK_TOPIC_THRESHOLD);
        if (anyAnswered && allGood) {
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
