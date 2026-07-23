package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Difficulty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты чистой формулы {@link ExperienceService#xpFor(boolean, Difficulty)} — единого
 * источника XP и для начисления, и для «+N XP» в баннере ответа. Раньше её исполнение
 * покрывалось только через Mockito-стабы у потребителей (реальное тело не вызывалось),
 * поэтому опечатка в таблице весов или в округлении прошла бы мимо зелёного прогона.
 */
class ExperienceServiceTest {

    // xpFor не обращается к репозиториям/часам → зависимости в конструкторе не нужны.
    private final ExperienceService service = new ExperienceService(null, null, null, null);

    @ParameterizedTest
    @CsvSource({
            "EASY,10",
            "MEDIUM,15",
            "HARD,20"
    })
    void correctAnswerAwardsBaseXpTimesDifficultyWeight(Difficulty difficulty, long expected) {
        // BASE_XP=10 × вес (EASY 1.0 / MEDIUM 1.5 / HARD 2.0), округлённо.
        assertThat(service.xpFor(true, difficulty)).isEqualTo(expected);
    }

    @Test
    void correctAnswerWithNullDifficultyFallsBackToMedium() {
        assertThat(service.xpFor(true, null)).isEqualTo(15L);
    }

    @Test
    void incorrectAnswerAlwaysAwardsPartialXp() {
        // Награда за попытку, а не за сложность — вес не применяется.
        assertThat(service.xpFor(false, Difficulty.HARD)).isEqualTo(2L);
        assertThat(service.xpFor(false, Difficulty.EASY)).isEqualTo(2L);
        assertThat(service.xpFor(false, null)).isEqualTo(2L);
    }
}
