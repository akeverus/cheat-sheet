package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.UserExperience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link UserExperienceRepository} на реальном PostgreSQL.
 *
 * <p>Дополняет {@link OwnerScopedXpActivityTest} (тот проверяет owner-изоляцию):
 * здесь фиксируются накопительная семантика {@code addXp} и — главное — строгая
 * граница {@code raiseBestStreak} ({@code UPDATE … WHERE best_streak < ?}), где
 * равный рекорд не должен перезаписывать строку.</p>
 */
class UserExperienceRepositoryTest extends AbstractPostgresRepositoryTest {

    private UserExperienceRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new UserExperienceRepository(jdbcTemplate);
    }

    @Test
    void findOnFreshDatabaseReturnsZeroExperience() {
        UserExperience experience = repository.find();
        assertThat(experience.xp()).isZero();
        assertThat(experience.bestStreak()).isZero();
    }

    @Test
    void addXpAccumulatesAcrossCallsAndTracksUpdatedAt() {
        assertThat(repository.addXp(50, 1_000L).xp()).isEqualTo(50L);

        UserExperience after = repository.addXp(30, 2_000L);
        assertThat(after.xp()).isEqualTo(80L); // 50 + 30 — ensureRow не сбрасывает строку
        assertThat(after.updatedAt()).isEqualTo(2_000L);
        assertThat(repository.find().xp()).isEqualTo(80L);
    }

    @Test
    void addXpZeroKeepsXpButAdvancesUpdatedAt() {
        repository.addXp(50, 1_000L);

        UserExperience after = repository.addXp(0, 3_000L);
        assertThat(after.xp()).isEqualTo(50L);
        assertThat(after.updatedAt()).isEqualTo(3_000L);
    }

    @Test
    void raiseBestStreakSetsRecordWhenStrictlyGreater() {
        repository.raiseBestStreak(5, 1_000L);
        assertThat(repository.find().bestStreak()).isEqualTo(5);

        repository.raiseBestStreak(8, 2_000L);
        assertThat(repository.find().bestStreak()).isEqualTo(8);
    }

    @Test
    void raiseBestStreakIsNoOpWhenLowerOrEqual() {
        repository.raiseBestStreak(5, 1_000L);

        // Меньший рекорд не понижает.
        repository.raiseBestStreak(3, 2_000L);
        assertThat(repository.find().bestStreak()).isEqualTo(5);

        // Равный рекорд — no-op (условие строго `best_streak < ?`): строка не трогается,
        // в т.ч. updated_at остаётся от первого поднятия.
        repository.raiseBestStreak(5, 9_999L);
        UserExperience unchanged = repository.find();
        assertThat(unchanged.bestStreak()).isEqualTo(5);
        assertThat(unchanged.updatedAt()).isEqualTo(1_000L);
    }
}
