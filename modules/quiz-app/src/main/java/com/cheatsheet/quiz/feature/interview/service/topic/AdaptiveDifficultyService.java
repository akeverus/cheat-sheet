package com.cheatsheet.quiz.feature.interview.service.topic;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.persistence.model.UserTopicStats;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdaptiveDifficultyService {

    private final UserTopicStatsRepository userTopicStatsRepository;

    public Difficulty resolveDifficulty(String topic) {
        UserTopicStats stats = userTopicStatsRepository.findByTopic(topic).orElse(null);
        if (stats == null) {
            return Difficulty.MEDIUM;
        }
        double mastery = stats.getMastery();
        if (mastery < 0.4) {
            return Difficulty.EASY;
        }
        if (mastery < 0.75) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    public double resolveTopicAccuracy(String topic) {
        UserTopicStats stats = userTopicStatsRepository.findByTopic(topic).orElse(null);
        if (stats == null) {
            return -1;
        }
        return Math.max(0.0, Math.min(100.0, stats.getMastery() * 100.0));
    }
}
