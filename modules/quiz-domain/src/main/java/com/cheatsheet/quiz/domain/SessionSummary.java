package com.cheatsheet.quiz.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
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
    int plannedQuestions;
    int correctCount;
    int wrongCount;
    int unknownCount;
    int dueCount;
    double accuracy;
    Duration duration;
    Instant completedAt;
    InterviewMode mode;
    List<TopicResult> topicResults;
    List<MistakeDetail> mistakes;
    List<String> recommendations;
    List<NextAction> nextActions;
    Gamification gamification;

    private SessionSummary(Builder builder) {
        this.totalQuestions = builder.totalQuestions;
        this.plannedQuestions = builder.plannedQuestions != null ? builder.plannedQuestions : builder.totalQuestions;
        this.correctCount = builder.correctCount;
        this.wrongCount = builder.wrongCount;
        this.unknownCount = builder.unknownCount;
        this.dueCount = builder.dueCount;
        // Знаменатель точности — число ОТВЕЧЕННЫХ вопросов (correct+wrong+unknown),
        // а не запланированный totalQuestions. Так знаменатель согласован с
        // LearningMetrics.overallAccuracy (Фаза 4) и честен, если сессия завершилась
        // не на всех вопросах (пауза) или total вырос от штрафных (EXAM): точность
        // не «размывается» неотвеченными. При полном прохождении answered == total,
        // поэтому существующие итоги не меняются.
        int answered = builder.correctCount + builder.wrongCount + builder.unknownCount;
        this.accuracy = answered > 0
                ? (builder.correctCount * 100.0) / answered : 0.0;
        this.duration = builder.duration;
        this.completedAt = builder.completedAt;
        this.mode = builder.mode;
        this.topicResults = List.copyOf(builder.topicResults);
        this.mistakes = List.copyOf(builder.mistakes);
        this.recommendations = List.copyOf(builder.recommendations);
        this.nextActions = List.copyOf(builder.nextActions);
        this.gamification = builder.gamification;
    }

    public int getTotalQuestions() { return totalQuestions; }
    public boolean isPartial() { return totalQuestions < plannedQuestions; }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount() { return wrongCount; }
    public int getUnknownCount() { return unknownCount; }
    public int getDueCount() { return dueCount; }
    public double getAccuracy() { return accuracy; }
    public Duration getDuration() { return duration; }
    public Instant getCompletedAt() { return completedAt; }
    public InterviewMode getMode() { return mode; }
    public List<TopicResult> getTopicResults() { return topicResults; }
    public List<MistakeDetail> getMistakes() { return mistakes; }
    public List<String> getRecommendations() { return recommendations; }
    public List<NextAction> getNextActions() { return nextActions; }
    public Gamification getGamification() { return gamification; }

    public String getFormattedDuration() {
        if (duration == null) return "—";
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        return minutes > 0 ? minutes + " мин " + seconds + " сек" : seconds + " сек";
    }

    /**
     * Длительность для тайла фиксированной ширины — таймерным «м:сс» / «ч:мм:сс».
     *
     * <p>{@link #getFormattedDuration()} («12 мин 30 сек») писалось под свободную
     * строку метрик и в KPI-тайл не влезает: 152px при бюджете тайла ~101px. Тайл —
     * колонка 1fr четырёхколоночной сетки, его ширина задана раскладкой, а не
     * содержимым, поэтому длинную форму там нельзя ни перенести (перенос между
     * числом и единицей читается как два разных показателя), ни оставить как есть.
     *
     * <p>Формат выбран не ради краткости: подпись под этим же значением уже показывает
     * «Среднее 0:24» — то есть таймерная запись на экране итогов уже канон, и тайл
     * теперь читается с ней в одном ключе. Секунды сохранены при любой длительности:
     * это единственное место, где видно реальное время сессии.
     */
    public String getCompactDuration() {
        if (duration == null) return "—";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, seconds)
                : String.format("%d:%02d", minutes, seconds);
    }

    public String getAccuracyFormatted() {
        return String.format("%.1f", accuracy);
    }

    /**
     * Точность для кольца-гейджа — целые проценты, без десятичной доли.
     *
     * <p>Кольцо на итогах — фиксированный круг, и число живёт не в его габарите, а во
     * внутреннем просвете (80 % от размера). Десятичная доля этот бюджет ломала:
     * «100,0%» display-шрифтом занимает 129 px при просвете 102 px, поэтому крайние
     * знаки ложились прямо на дугу. Цвет числа и цвет дуги — один и тот же токен
     * состояния, так что наложение не выглядело как ошибка: идеальный результат просто
     * читался как «00,0%». Точность при этом не теряется — рядом стоит «N из M верных
     * ответов», а колонка в таблице тем даёт долю с десятыми.
     *
     * <p>Округление к ближайшему, но с двумя запретами: 100 — только при настоящих 100,
     * 0 — только при настоящих 0. Иначе 99,6 % превратилось бы в «100%» ровно на том
     * экране, чей смысл — отличить сессию без ошибок от сессии с ошибкой.
     */
    public int getAccuracyRounded() {
        long rounded = Math.round(accuracy);
        if (rounded >= 100 && accuracy < 100.0) return 99;
        if (rounded <= 0 && accuracy > 0.0) return 1;
        return (int) rounded;
    }

    /** Результат по одной теме. */
    @lombok.Builder(toBuilder = true)
    public record TopicResult(String topic, int total, int correct, int wrong, double accuracy) implements Serializable {
        public TopicResult {
            Objects.requireNonNull(topic, "topic must not be null");
        }
    }

    /** Детали ошибки. {@code unknown} — вопрос закрыт честным «не знаю», а не
     *  промахом при попытке (VIS-14: различие несёт бейдж в списке разбора). */
    @lombok.Builder(toBuilder = true)
    public record MistakeDetail(long questionId, String questionText, String topic, boolean unknown)
            implements Serializable {
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

    /**
     * Геймификация итогов (Этап 5, handoff-3): опыт за сессию и серия дней. Все
     * значения — реальные (sessionXp суммируется из истории ответов той же
     * {@code xpFor}-функцией, что и начисление; streak/record — из
     * ExperienceService). {@code null} когда геймификация недоступна — шаблон тогда
     * не рендерит KPI «Опыт»/«Серия», не фабрикуя чисел.
     *
     * @param sessionXp      опыт, заработанный в ЭТОЙ сессии
     * @param totalXp        общий накопленный опыт
     * @param level          текущий уровень
     * @param xpIntoLevel    сколько опыта набрано ВНУТРИ текущего уровня
     * @param xpToNextLevel  сколько опыта отделяет текущий уровень от следующего
     * @param currentStreak  текущая серия активных дней
     * @param bestStreak     рекорд серии
     * @param newStreakRecord серия достигла/обновила рекорд в этой сессии
     */
    @lombok.Builder(toBuilder = true)
    public record Gamification(long sessionXp, long totalXp, int level,
                               long xpIntoLevel, long xpToNextLevel,
                               int currentStreak, int bestStreak,
                               boolean newStreakRecord) implements Serializable {

        /**
         * Сколько опыта осталось до следующего уровня.
         *
         * <p>Шкала нужна затем же, зачем она нужна любому числу на экране итогов:
         * «+42 XP» без неё не отвечает ни на один вопрос — много это или мало,
         * приблизило к чему-нибудь или нет. Уровень и остаток до следующего у нас
         * уже посчитаны ({@code UserExperience}), их просто не показывали.
         */
        public long xpLeftToNextLevel() {
            return Math.max(0, xpToNextLevel - xpIntoLevel);
        }

        /** Ступень гильдии, на которой человек стоит сейчас. */
        public GuildRank rank() {
            return GuildRank.forLevel(level);
        }

        /**
         * Сколько уровней осталось до следующей ступени; 0 — если ступень последняя.
         *
         * <p>Расстояние измеряется в уровнях, а не в XP: XP до следующей СТУПЕНИ — число
         * в тысячах, которое ничего не проясняет, а «ещё три уровня» человек соотносит с
         * тем, что видит на полосе рядом.</p>
         */
        public int levelsToNextRank() {
            return rank().next().map(next -> Math.max(0, next.minLevel() - level)).orElse(0);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalQuestions;
        private Integer plannedQuestions;
        private int correctCount;
        private int wrongCount;
        private int unknownCount;
        private int dueCount;
        private Duration duration;
        private Instant completedAt;
        private InterviewMode mode;
        private final List<TopicResult> topicResults = new ArrayList<>();
        private final List<MistakeDetail> mistakes = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();
        private final List<NextAction> nextActions = new ArrayList<>();
        private Gamification gamification;

        public Builder totalQuestions(int val) { this.totalQuestions = val; return this; }
        public Builder plannedQuestions(int val) { this.plannedQuestions = val; return this; }
        public Builder correctCount(int val) { this.correctCount = val; return this; }
        public Builder wrongCount(int val) { this.wrongCount = val; return this; }
        public Builder unknownCount(int val) { this.unknownCount = val; return this; }
        public Builder dueCount(int val) { this.dueCount = val; return this; }
        public Builder duration(Duration val) { this.duration = val; return this; }
        public Builder completedAt(Instant val) { this.completedAt = val; return this; }
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

        public Builder gamification(Gamification val) { this.gamification = val; return this; }

        public SessionSummary build() {
            Objects.requireNonNull(mode, "mode");
            Objects.requireNonNull(duration, "duration");
            if (totalQuestions < 0 || (plannedQuestions != null && plannedQuestions < 0)
                    || correctCount < 0 || wrongCount < 0 || unknownCount < 0 || dueCount < 0) {
                throw new IllegalArgumentException(
                        "totalQuestions, correctCount, wrongCount, unknownCount and dueCount must be >= 0");
            }
            return new SessionSummary(this);
        }
    }
}
