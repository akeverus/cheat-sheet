package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link PausedSessionRepository} на реальном PostgreSQL (FLOW-01).
 * Проверяют round-trip блоба, чтение метаданных без блоба, singleton-перезапись и очистку.
 */
class PausedSessionRepositoryTest extends AbstractPostgresRepositoryTest {

    private PausedSessionRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new PausedSessionRepository(jdbcTemplate);
    }

    @Test
    void findReturnsEmptyWhenNoPause() {
        assertThat(repository.findBlob()).isEmpty();
        assertThat(repository.findInfo()).isEmpty();
        assertThat(repository.exists()).isFalse();
    }

    @Test
    void saveThenFindBlobReturnsSameBytes() {
        byte[] blob = {10, 20, 30, 40};
        repository.save(blob, InterviewMode.EXAM, "java", 12, 3, Instant.parse("2026-07-15T10:15:30Z"));

        assertThat(repository.exists()).isTrue();
        assertThat(repository.findBlob()).isPresent().get().isEqualTo(blob);
    }

    @Test
    void saveThenFindInfoReturnsMetadata() {
        Instant pausedAt = Instant.parse("2026-07-15T10:15:30Z");
        repository.save(new byte[]{1}, InterviewMode.STUDY, "kafka", 20, 7, pausedAt);

        Optional<PausedSessionInfo> info = repository.findInfo();
        assertThat(info).isPresent();
        PausedSessionInfo value = info.get();
        assertThat(value.mode()).isEqualTo(InterviewMode.STUDY);
        assertThat(value.topic()).isEqualTo("kafka");
        assertThat(value.total()).isEqualTo(20);
        assertThat(value.answered()).isEqualTo(7);
        assertThat(value.pausedAt()).isEqualTo(pausedAt);
    }

    @Test
    void saveAllowsNullTopic() {
        repository.save(new byte[]{2}, InterviewMode.MARATHON, null, 50, 0, Instant.parse("2026-07-15T10:15:30Z"));

        assertThat(repository.findInfo()).isPresent().get()
                .satisfies(info -> assertThat(info.topic()).isNull());
    }

    @Test
    void saveTwiceOverwritesSingletonRow() {
        repository.save(new byte[]{1}, InterviewMode.EXAM, "java", 10, 1, Instant.parse("2026-07-15T10:00:00Z"));
        repository.save(new byte[]{9, 9}, InterviewMode.MARATHON, "kotlin", 30, 5, Instant.parse("2026-07-15T11:00:00Z"));

        // Осталась ровно одна строка — вторая пауза.
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM paused_session", Integer.class);
        assertThat(count).isEqualTo(1);
        assertThat(repository.findBlob()).get().isEqualTo(new byte[]{9, 9});
        assertThat(repository.findInfo()).get().satisfies(info -> {
            assertThat(info.mode()).isEqualTo(InterviewMode.MARATHON);
            assertThat(info.topic()).isEqualTo("kotlin");
            assertThat(info.total()).isEqualTo(30);
            assertThat(info.answered()).isEqualTo(5);
        });
    }

    @Test
    void clearRemovesPause() {
        repository.save(new byte[]{1}, InterviewMode.EXAM, "java", 10, 1, Instant.parse("2026-07-15T10:00:00Z"));
        repository.clear();

        assertThat(repository.exists()).isFalse();
        assertThat(repository.findBlob()).isEmpty();
    }
}
