package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.persistence.model.UserTopicStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class UserTopicStatsRepositoryTest extends AbstractPostgresRepositoryTest {

    private UserTopicStatsRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new UserTopicStatsRepository(jdbcTemplate);
    }

    @Test
    void findByTopicReturnsEmptyWhenAbsent() {
        assertThat(repository.findByTopic("unknown")).isEmpty();
    }

    @Test
    void findByTopicReturnsEmptyForNullOrBlank() {
        assertThat(repository.findByTopic(null)).isEmpty();
        assertThat(repository.findByTopic("  ")).isEmpty();
    }

    @Test
    void recordAnswerCreatesRowOnFirstCallAndUpdatesMastery() {
        repository.recordAnswer("java/strings", true);
        repository.recordAnswer("java/strings", true);
        repository.recordAnswer("java/strings", false);

        Optional<UserTopicStats> found = repository.findByTopic("java/strings");
        assertThat(found).isPresent();
        UserTopicStats s = found.get();
        assertThat(s.getCorrect()).isEqualTo(2);
        assertThat(s.getIncorrect()).isEqualTo(1);
        // mastery = 2/(2+1) = 0.666...
        assertThat(s.getMastery()).isCloseTo(0.6667, within(0.001));
    }

    @Test
    void recordAnswerWithNullOrBlankTopicIsNoOp() {
        repository.recordAnswer(null, true);
        repository.recordAnswer(" ", false);
        // Никаких exceptions, никаких записей.
        assertThat(repository.findByTopic("anything")).isEmpty();
    }

    @Test
    void recordAnswerSeparatesTopics() {
        repository.recordAnswer("topic-a", true);
        repository.recordAnswer("topic-b", false);

        assertThat(repository.findByTopic("topic-a")).hasValueSatisfying(s -> {
            assertThat(s.getCorrect()).isEqualTo(1);
            assertThat(s.getIncorrect()).isZero();
        });
        assertThat(repository.findByTopic("topic-b")).hasValueSatisfying(s -> {
            assertThat(s.getCorrect()).isZero();
            assertThat(s.getIncorrect()).isEqualTo(1);
        });
    }
}
