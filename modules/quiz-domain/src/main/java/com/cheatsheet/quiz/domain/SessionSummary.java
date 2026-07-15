package com.cheatsheet.quiz.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сводка завершённой сессии (Builder pattern).
 *
 * <p>Содержит общий результат, разбивку по темам, список ошибок
 * и рекомендации для дальнейшего обучения.</p>
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionSummary implements Serializable {

    int totalQuestions;
    int correctCount;
    int wrongCount;
    int unknownCount;
    int dueCount;
    double accuracy;
    Duration duration;
    InterviewMode mode;
    List<TopicResult> topicResults;
    List<MistakeDetail> mistakes;
    List<String> recommendations;
    List<NextAction> nextActions;

    private SessionSummary(Builder builder) {
        this.totalQuestions = builder.totalQuestions;
        this.correctCount = builder.correctCount;
        this.wrongCount = builder.wrongCount;
        this.unknownCount = builder.unknownCount;
        this.dueCount = builder.dueCount;
        this.accuracy = builder.totalQuestions > 0
                ? (builder.correctCount * 100.0) / builder.totalQuestions : 0.0;
        this.duration = builder.duration;
        this.mode = builder.mode;
        this.topicResults = List.copyOf(builder.topicResults);
        this.mistakes = List.copyOf(builder.mistakes);
        this.recommendations = List.copyOf(builder.recommendations);
        this.nextActions = List.copyOf(builder.nextActions);
    }

    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount() { return wrongCount; }
    public int getUnknownCount() { return unknownCount; }
    public int getDueCount() { return dueCount; }
    public double getAccuracy() { return accuracy; }
    public Duration getDuration() { return duration; }
    public InterviewMode getMode() { return mode; }
    public List<TopicResult> getTopicResults() { return topicResults; }
    public List<MistakeDetail> getMistakes() { return mistakes; }
    public List<String> getRecommendations() { return recommendations; }
    public List<NextAction> getNextActions() { return nextActions; }

    public String getFormattedDuration() {
        if (duration == null) return "—";
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        return minutes > 0 ? minutes + " мин " + seconds + " сек" : seconds + " сек";
    }

    public String getAccuracyFormatted() {
        return String.format("%.1f", accuracy);
    }

    /** Результат по одной теме. */
    @lombok.Builder(toBuilder = true)
    public record TopicResult(String topic, int total, int correct, int wrong, double accuracy) implements Serializable {
        public TopicResult {
            Objects.requireNonNull(topic, "topic must not be null");
        }
    }

    /** Детали ошибки. */
    @lombok.Builder(toBuilder = true)
    public record MistakeDetail(long questionId, String questionText, String topic) implements Serializable {
        public MistakeDetail {
            Objects.requireNonNull(questionText, "questionText must not be null");
            Objects.requireNonNull(topic, "topic must not be null");
        }
    }

    /**
     * Типизированное следующее действие «короткого плана» (FLOW-SUMMARY).
     *
     * @param type   тип действия (для иконки/кнопки в шаблоне)
     * @param label  человекочитаемая подпись
     * @param target опциональная цель (например, тема для повторения) — может быть {@code null}
     * @param count  связанное число (ошибок/due/вопросов темы), 0 если неприменимо
     */
    @lombok.Builder(toBuilder = true)
    public record NextAction(NextActionType type, String label, String target, int count) implements Serializable {
        public NextAction {
            Objects.requireNonNull(type, "type must not be null");
            Objects.requireNonNull(label, "label must not be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalQuestions;
        private int correctCount;
        private int wrongCount;
        private int unknownCount;
        private int dueCount;
        private Duration duration;
        private InterviewMode mode;
        private final List<TopicResult> topicResults = new ArrayList<>();
        private final List<MistakeDetail> mistakes = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();
        private final List<NextAction> nextActions = new ArrayList<>();

        public Builder totalQuestions(int val) { this.totalQuestions = val; return this; }
        public Builder correctCount(int val) { this.correctCount = val; return this; }
        public Builder wrongCount(int val) { this.wrongCount = val; return this; }
        public Builder unknownCount(int val) { this.unknownCount = val; return this; }
        public Builder dueCount(int val) { this.dueCount = val; return this; }
        public Builder duration(Duration val) { this.duration = val; return this; }
        public Builder mode(InterviewMode val) { this.mode = val; return this; }

        public Builder addTopicResult(TopicResult result) {
            this.topicResults.add(result);
            return this;
        }

        public Builder addMistake(MistakeDetail mistake) {
            this.mistakes.add(mistake);
            return this;
        }

        public Builder addRecommendation(String recommendation) {
            this.recommendations.add(recommendation);
            return this;
        }

        public Builder addNextAction(NextAction action) {
            this.nextActions.add(action);
            return this;
        }

        public SessionSummary build() {
            Objects.requireNonNull(mode, "mode");
            Objects.requireNonNull(duration, "duration");
            if (totalQuestions < 0 || correctCount < 0 || wrongCount < 0 || unknownCount < 0 || dueCount < 0) {
                throw new IllegalArgumentException(
                        "totalQuestions, correctCount, wrongCount, unknownCount and dueCount must be >= 0");
            }
            return new SessionSummary(this);
        }
    }
}
