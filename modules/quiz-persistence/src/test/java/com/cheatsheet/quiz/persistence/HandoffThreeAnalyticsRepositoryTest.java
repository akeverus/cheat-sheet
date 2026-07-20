package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HandoffThreeAnalyticsRepositoryTest extends AbstractPostgresRepositoryTest {

    private AttemptRepository attemptRepository;
    private QuestionRepository questionRepository;
    private long questionId;
    private long wrongOptionId;

    @BeforeEach
    void setUpRepositories() {
        attemptRepository = new AttemptRepository(jdbcTemplate);
        questionRepository = new QuestionRepository(jdbcTemplate);
        questionId = jdbcTemplate.queryForObject("""
                INSERT INTO questions
                    (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash, difficulty)
                VALUES ('handoff.md#Q1', 'handoff.md#Q1', 'handoff.md', 'Java',
                        'Что вернёт код?', 'Объяснение', 'handoff-hash', 'HARD')
                RETURNING id
                """, Long.class);
        wrongOptionId = jdbcTemplate.queryForObject("""
                INSERT INTO answer_options (question_id, option_text, is_correct, display_order, source)
                VALUES (?, 'Неверный вариант', 0, 0, 'SEED') RETURNING id
                """, Long.class, questionId);
        jdbcTemplate.update("""
                INSERT INTO answer_options (question_id, option_text, is_correct, display_order, source)
                VALUES (?, 'Верный вариант', 1, 1, 'SEED')
                """, questionId);
        jdbcTemplate.update("""
                INSERT INTO review_state
                    (question_id, repetitions, interval_days, ease_factor, next_review_at,
                     last_result, correct_count, wrong_count)
                VALUES (?, 0, 0, 2.5, 0, 'NEW', 0, 0)
                """, questionId);
    }

    @Test
    void analyticsReturnsDailyAccuracyAndRecentMistakeDetails() {
        long firstDay = Instant.parse("2026-07-18T10:00:00Z").getEpochSecond();
        long secondDay = Instant.parse("2026-07-19T11:00:00Z").getEpochSecond();
        attemptRepository.insert(attempt(AttemptOutcome.WRONG, firstDay, wrongOptionId));
        attemptRepository.insert(attempt(AttemptOutcome.CORRECT, firstDay + 60, null));
        attemptRepository.insert(attempt(AttemptOutcome.CORRECT, secondDay, null));

        List<AttemptRepository.DailyAccuracy> trend = attemptRepository.findDailyAccuracy(firstDay - 1);
        List<AttemptRepository.MistakeAttempt> mistakes = attemptRepository.findRecentMistakes(20);

        assertThat(trend).extracting(AttemptRepository.DailyAccuracy::day)
                .containsExactly("2026-07-18", "2026-07-19");
        assertThat(trend.get(0).percent()).isEqualTo(50.0);
        assertThat(mistakes).singleElement().satisfies(mistake -> {
            assertThat(mistake.questionText()).isEqualTo("Что вернёт код?");
            assertThat(mistake.selectedOptionText()).isEqualTo("Неверный вариант");
            assertThat(mistake.correctOptionText()).isEqualTo("Верный вариант");
        });
    }

    @Test
    void sessionQuestionQueryFiltersByDifficulty() {
        long easyQuestionId = jdbcTemplate.queryForObject("""
                INSERT INTO questions
                    (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash, difficulty)
                VALUES ('handoff.md#Q2', 'handoff.md#Q2', 'handoff.md', 'Java',
                        'Простой вопрос', 'Ответ', 'handoff-hash-2', 'EASY')
                RETURNING id
                """, Long.class);
        jdbcTemplate.update("""
                INSERT INTO review_state
                    (question_id, repetitions, interval_days, ease_factor, next_review_at,
                     last_result, correct_count, wrong_count)
                VALUES (?, 0, 0, 2.5, 0, 'NEW', 0, 0)
                """, easyQuestionId);

        List<Long> ids = questionRepository.findQuestionIdsForSession(
                "Java", false, false, Difficulty.HARD, 1_000L, 10);

        assertThat(ids).containsExactly(questionId);
    }

    private Attempt attempt(AttemptOutcome outcome, long createdAt, Long selectedOptionId) {
        return Attempt.builder()
                .questionId(questionId)
                .selectedOptionId(selectedOptionId)
                .outcome(outcome)
                .firstAttempt(false)
                .createdAt(createdAt)
                .build();
    }
}
