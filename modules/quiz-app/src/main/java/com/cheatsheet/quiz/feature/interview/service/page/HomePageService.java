package com.cheatsheet.quiz.feature.interview.service.page;

import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import com.cheatsheet.quiz.domain.Progression;
import com.cheatsheet.quiz.domain.SessionRecord;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.progress.DailyStreakService;
import com.cheatsheet.quiz.feature.interview.service.progress.ExperienceService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Состояние хаба «Сегодня» ({@code GET /}): что делать дальше и краткий срез прогресса.
 *
 * <p>Приоритет primary CTA: живая сессия → пауза → очередь повторов → ошибки →
 * слабая тема → собрать сессию. Secondary — оставшиеся из «ошибки / слабые / собрать»,
 * не больше двух.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomePageService {

    private static final int RECENT_SESSIONS_LIMIT = 2;
    private static final int SECONDARY_LIMIT = 2;

    LearningMetricsService learningMetricsService;
    DailyStreakService dailyStreakService;
    ExperienceService experienceService;
    SessionHistoryService sessionHistoryService;
    QuestionStatsRepository questionStatsRepository;
    BookmarkService bookmarkService;
    TopicUtils topicUtils;

    /**
     * Собирает state хаба по активной HTTP-сессии и (опционально) паузе в БД.
     */
    public HomePageState build(Optional<InterviewSession> activeSession,
                               Optional<PausedSessionInfo> paused) {
        InterviewStats stats = InterviewStats.from(learningMetricsService.compute());
        StreakResponse streak = dailyStreakService.getTodayProgress();
        Progression progression = Progression.of(
                experienceService.snapshot(), experienceService.currentStreak());

        QuestionStatsRepository.WeakestTopic weakest =
                questionStatsRepository.findWeakestTopic(QuizConstants.WEAK_TOPIC_THRESHOLD);
        long weakTopicCount = questionStatsRepository.countWeakTopics(QuizConstants.WEAK_TOPIC_THRESHOLD);
        String weakestTopic = weakest == null ? null : topicUtils.displayName(weakest.topic());
        String weakestTopicSlug = weakest == null ? null : weakest.topic();

        List<SessionRecord> recentSessions = sessionHistoryService.recent().stream()
                .limit(RECENT_SESSIONS_LIMIT)
                .toList();
        long bookmarksCount = bookmarkService.count();

        InterviewSession live = activeSession
                .filter(s -> s != null && !s.isFinished())
                .orElse(null);
        PausedSessionInfo pausedInfo = (live == null) ? paused.orElse(null) : null;

        // Те же ошибочные ВОПРОСЫ, что сайдбар (countAllFiltered), а не сумма попыток stats.wrong().
        long wrongQuestions = questionStatsRepository.countAllFiltered(null, null, true);

        Actions actions = resolveActions(
                live, pausedInfo, stats, wrongQuestions, weakTopicCount, weakestTopicSlug, weakestTopic);

        return new HomePageState(
                stats,
                streak,
                streakLead(streak),
                progression,
                Optional.ofNullable(pausedInfo),
                weakestTopic,
                weakTopicCount,
                recentSessions,
                bookmarksCount,
                actions.primary(),
                actions.secondary()
        );
    }

    /**
     * Одна честная фраза дня: «40 из 10» при закрытой цели читается как баг.
     */
    public static String streakLead(StreakResponse streak) {
        if (streak == null) {
            return "Что сделать дальше.";
        }
        if (streak.today() >= streak.goal()) {
            return "Цель на сегодня закрыта · серия " + streak.streakLabel();
        }
        return streak.today() + " из " + streak.goal() + " сегодня · серия " + streak.streakLabel();
    }

    private Actions resolveActions(InterviewSession live,
                                   PausedSessionInfo pausedInfo,
                                   InterviewStats stats,
                                   long wrongQuestions,
                                   long weakTopicCount,
                                   String weakestTopicSlug,
                                   String weakestTopicLabel) {
        long due = stats.dueRepeats();
        long wrong = wrongQuestions;

        HomeAction primary;
        if (live != null) {
            int remaining = Math.max(0, live.getTotal() - live.getIndex());
            primary = new HomeAction(HomeAction.Type.CONTINUE_SESSION,
                    "Продолжить сессию", "/practice", remaining,
                    new HomeSession(live.getMode(), sessionTopic(live.getTopic()),
                            live.getCorrect() + live.getWrong() + live.getUnknown(), live.getTotal()));
        } else if (pausedInfo != null) {
            int remaining = Math.max(0, pausedInfo.total() - pausedInfo.answered());
            primary = new HomeAction(HomeAction.Type.RESUME_PAUSED,
                    "Продолжить сессию", "/resume", remaining,
                    new HomeSession(pausedInfo.mode(), sessionTopic(pausedInfo.topic()),
                            pausedInfo.answered(), pausedInfo.total()));
        } else if (due > 0) {
            primary = new HomeAction(HomeAction.Type.REVIEW_DUE,
                    "Повторить", "/practice", (int) Math.min(due, Integer.MAX_VALUE));
        } else if (wrong > 0) {
            primary = new HomeAction(HomeAction.Type.REVIEW_WRONG,
                    "Разобрать ошибки", "/practice?onlyWrong=true&ordered=true",
                    (int) Math.min(wrong, Integer.MAX_VALUE));
        } else if (weakTopicCount > 0 && weakestTopicSlug != null) {
            primary = weakTopicAction(weakestTopicSlug, weakestTopicLabel);
        } else {
            primary = new HomeAction(HomeAction.Type.START_BUILD, "Собрать сессию", "/train", 0);
        }

        List<HomeAction> secondary = new ArrayList<>(SECONDARY_LIMIT);
        maybeAddSecondary(secondary, primary, HomeAction.Type.REVIEW_WRONG, wrong > 0,
                () -> new HomeAction(HomeAction.Type.REVIEW_WRONG,
                        "Разобрать ошибки", "/practice?onlyWrong=true&ordered=true",
                        (int) Math.min(wrong, Integer.MAX_VALUE)));
        // Конструктор всегда достижим с главной: ошибки и слабая тема не должны
        // занимать оба запасных выхода и прятать выбор режима.
        maybeAddSecondary(secondary, primary, HomeAction.Type.START_BUILD, true,
                () -> new HomeAction(HomeAction.Type.START_BUILD, "Собрать сессию", "/train", 0));
        maybeAddSecondary(secondary, primary, HomeAction.Type.WEAK_TOPIC,
                weakTopicCount > 0 && weakestTopicSlug != null,
                () -> weakTopicAction(weakestTopicSlug, weakestTopicLabel));
        // Если primary уже «собрать», а secondary пуст — открытая тренировка как запасной путь.
        if (secondary.isEmpty() && primary.type() == HomeAction.Type.START_BUILD) {
            secondary.add(new HomeAction(HomeAction.Type.KEEP_GOING, "Тренироваться", "/practice", 0));
        }

        return new Actions(primary, List.copyOf(secondary));
    }

    private static void maybeAddSecondary(List<HomeAction> secondary,
                                          HomeAction primary,
                                          HomeAction.Type type,
                                          boolean eligible,
                                          java.util.function.Supplier<HomeAction> factory) {
        if (secondary.size() >= SECONDARY_LIMIT || !eligible || primary.type() == type) {
            return;
        }
        secondary.add(factory.get());
    }

    private HomeAction weakTopicAction(String slug, String label) {
        String href = UriComponentsBuilder.fromPath("/practice")
                .queryParam("weakTopics", "true")
                .queryParam("ordered", "true")
                .queryParam("topic", slug)
                .build()
                .encode()
                .toUriString();
        String display = (label == null || label.isBlank()) ? slug : label;
        return new HomeAction(HomeAction.Type.WEAK_TOPIC,
                "Слабая тема: " + display, href,
                0);
    }

    private String sessionTopic(String topic) {
        return topic == null || topic.isBlank() ? "Подборка тем" : topicUtils.displayName(topic);
    }

    public record HomeSession(InterviewMode mode, String topic, int answered, int total) {}

    private record Actions(HomeAction primary, List<HomeAction> secondary) {}

    /**
     * Состояние хаба «Сегодня».
     *
     * @param stats            сводка банка (due/wrong/…)
     * @param streak           дневная цель и серия
     * @param streakLead       одна фраза дня (без «N из M», если цель уже закрыта)
     * @param progression      ступень и уровень
     * @param paused           метаданные паузы (если нет живой сессии)
     * @param weakestTopic     отображаемое имя самой слабой темы или {@code null}
     * @param weakTopicCount   число слабых тем
     * @param recentSessions   до трёх последних сессий
     * @param bookmarksCount   число закладок
     * @param primary          главное действие дня
     * @param secondary        до двух вторичных действий
     */
    public record HomePageState(
            InterviewStats stats,
            StreakResponse streak,
            String streakLead,
            Progression progression,
            Optional<PausedSessionInfo> paused,
            String weakestTopic,
            long weakTopicCount,
            List<SessionRecord> recentSessions,
            long bookmarksCount,
            HomeAction primary,
            List<HomeAction> secondary
    ) {}

    /**
     * CTA хаба: тип, подпись, адрес и счётчик (0 — без числа).
     *
     * <p>{@link Type#RESUME_PAUSED} — POST {@code /resume}; шаблон рисует форму,
     * а не ссылку. Остальные типы — обычный GET.</p>
     */
    public record HomeAction(Type type, String label, String href, int count, HomeSession session) {
        public HomeAction(Type type, String label, String href, int count) {
            this(type, label, href, count, null);
        }

        public String guidance() {
            return switch (type) {
                case REVIEW_DUE -> "Подошло время повторить знакомые вопросы. Иди в своём темпе.";
                case START_BUILD -> "Выбери тему и способ проверки. Параметры можно настроить перед началом.";
                default -> "";
            };
        }

        public enum Type {
            CONTINUE_SESSION,
            RESUME_PAUSED,
            REVIEW_DUE,
            REVIEW_WRONG,
            WEAK_TOPIC,
            START_BUILD,
            KEEP_GOING
        }

        /** Нужно ли слать POST (пауза), а не GET. */
        public boolean post() {
            return type == Type.RESUME_PAUSED;
        }
    }
}
