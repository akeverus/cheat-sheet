package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerScopedXpActivityTest extends AbstractPostgresRepositoryTest {

    private UserExperienceRepository experienceRepository;
    private DailyActivityRepository dailyActivityRepository;
    private UserTopicStatsRepository topicStatsRepository;

    @BeforeEach
    void initRepos() {
        experienceRepository = new UserExperienceRepository(jdbcTemplate);
        dailyActivityRepository = new DailyActivityRepository(jdbcTemplate);
        topicStatsRepository = new UserTopicStatsRepository(jdbcTemplate);
    }

    @AfterEach
    void clearOwner() {
        OwnerContext.clear();
    }

    @Test
    void experienceIsIsolatedPerOwner() {
        OwnerContext.set("alice");
        experienceRepository.addXp(50, 1000L);
        assertThat(experienceRepository.find().xp()).isEqualTo(50L);

        OwnerContext.set("bob");
        assertThat(experienceRepository.find().xp()).isZero();
        experienceRepository.addXp(10, 1000L);
        assertThat(experienceRepository.find().xp()).isEqualTo(10L);

        OwnerContext.set("alice");
        assertThat(experienceRepository.find().xp()).isEqualTo(50L);
    }

    @Test
    void dailyActivityIsIsolatedPerOwner() {
        OwnerContext.set("alice");
        dailyActivityRepository.incrementToday(true, 10);
        assertThat(dailyActivityRepository.findToday().questionsAnswered()).isEqualTo(1);

        OwnerContext.set("bob");
        assertThat(dailyActivityRepository.findToday()).isNull();
        dailyActivityRepository.incrementToday(false, 10);
        assertThat(dailyActivityRepository.findToday().questionsAnswered()).isEqualTo(1);
        assertThat(dailyActivityRepository.findToday().correctCount()).isZero();

        OwnerContext.set("alice");
        assertThat(dailyActivityRepository.findToday().correctCount()).isEqualTo(1);
    }

    @Test
    void topicStatsAreIsolatedPerOwner() {
        OwnerContext.set("alice");
        topicStatsRepository.recordAnswer("java", true);
        assertThat(topicStatsRepository.findByTopic("java")).isPresent();
        assertThat(topicStatsRepository.findByTopic("java").orElseThrow().getCorrect()).isEqualTo(1);

        OwnerContext.set("bob");
        assertThat(topicStatsRepository.findByTopic("java")).isEmpty();
        topicStatsRepository.recordAnswer("java", false);
        assertThat(topicStatsRepository.findByTopic("java").orElseThrow().getIncorrect()).isEqualTo(1);

        OwnerContext.set("alice");
        assertThat(topicStatsRepository.findByTopic("java").orElseThrow().getIncorrect()).isZero();
    }
}
