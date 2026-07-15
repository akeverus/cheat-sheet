package com.cheatsheet.quiz.infrastructure.bootstrap;

import com.cheatsheet.quiz.domain.ReviewResult;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

/**
 * Сидер детерминированных фикстур профиля {@code qa}.
 *
 * <p>Выполняется <b>после</b> {@link StartupRunner} (вопросы уже импортированы) —
 * см. {@link Order}. Устанавливает воспроизводимое состояние прогресса для
 * browser-QA: чистит все зависящие от прогресса таблицы, затем помечает первые
 * {@code app.qa.due-fixtures} вопросов как «к повторению» (due) и следующие
 * {@code app.qa.learned-fixtures} — как «выучено» (learned). Остальные остаются
 * «новыми» (без строки в {@code review_state}).</p>
 *
 * <p>Все метки времени берутся из фиксированного {@link Clock} ({@code QaConfig}),
 * поэтому набор due/learned/new одинаков от прогона к прогону.</p>
 *
 * <p>Активен только под профилем {@code qa}; в prod/dev не участвует.</p>
 */
@Component
@Profile("qa")
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QaFixtureRunner implements ApplicationRunner {

    private static final long SECONDS_PER_DAY = 86_400L;
    private static final double DEFAULT_EASE = 2.5;

    JdbcTemplate jdbcTemplate;
    Clock clock;
    int dueFixtures;
    int learnedFixtures;

    public QaFixtureRunner(JdbcTemplate jdbcTemplate,
                           Clock clock,
                           @Value("${app.qa.due-fixtures:2}") int dueFixtures,
                           @Value("${app.qa.learned-fixtures:1}") int learnedFixtures) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
        this.dueFixtures = dueFixtures;
        this.learnedFixtures = learnedFixtures;
    }

    @Override
    public void run(ApplicationArguments args) {
        // QA-профиль владеет состоянием прогресса целиком → чистим для детерминизма.
        jdbcTemplate.execute("TRUNCATE TABLE review_state, daily_activity, user_topic_stats RESTART IDENTITY");

        long nowEpoch = clock.instant().getEpochSecond();
        int wanted = dueFixtures + learnedFixtures;
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM questions ORDER BY id LIMIT ?", Long.class, wanted);

        if (ids.size() < wanted) {
            log.warn("QA-фикстуры: доступно {} вопросов, запрошено {} (due={}, learned={}) — сею сколько есть",
                    ids.size(), wanted, dueFixtures, learnedFixtures);
        }

        int dueSeeded = 0;
        int learnedSeeded = 0;
        for (int i = 0; i < ids.size(); i++) {
            long questionId = ids.get(i);
            if (i < dueFixtures) {
                // Due: просрочен на сутки, последний ответ неверный (виден в «к повторению»).
                insertReviewState(questionId, 1, 1, DEFAULT_EASE,
                        nowEpoch - SECONDS_PER_DAY, ReviewResult.WRONG, 0, 1);
                dueSeeded++;
            } else {
                // Learned: следующее повторение через 10 дней, серия верных ответов.
                insertReviewState(questionId, 3, 10, DEFAULT_EASE + 0.1,
                        nowEpoch + 10 * SECONDS_PER_DAY, ReviewResult.CORRECT, 3, 0);
                learnedSeeded++;
            }
        }

        log.warn("QA-фикстуры засеяны: due={}, learned={}, остальные вопросы — новые (Clock зафиксирован)",
                dueSeeded, learnedSeeded);
    }

    private void insertReviewState(long questionId, int repetitions, int intervalDays, double easeFactor,
                                   long nextReviewAt, ReviewResult lastResult, int correctCount, int wrongCount) {
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
                        "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                questionId, repetitions, intervalDays, easeFactor,
                nextReviewAt, lastResult.name(), correctCount, wrongCount);
    }
}
