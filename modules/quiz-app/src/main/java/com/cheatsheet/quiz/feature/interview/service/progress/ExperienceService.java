package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.UserExperience;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.UserExperienceRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * Начисление опыта (XP) и рекорда серии (хендофф-3, Этап 9).
 *
 * <p>XP не выдумывается — это детерминированная функция реального исхода попытки и
 * сложности вопроса (см. {@code METRICS_GLOSSARY.md}). Начисляется через
 * {@link AnswerEvent} (Observer, как {@link DailyStreakService}) — единый путь и для
 * MVC, и для REST-сабмита, так что двойного учёта нет. Формула {@link #xpFor} —
 * чистая и публичная, чтобы слой ответа мог показать то же «+N XP» в баннере, не
 * завися от порядка листенеров.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExperienceService {

    /** Базовый XP за верный ответ (до умножения на вес сложности). */
    static final long BASE_XP = 10;

    /** XP за неверную попытку — награда за попытку, а не за угадывание. */
    static final long PARTIAL_XP = 2;

    UserExperienceRepository experienceRepository;
    QuestionRepository questionRepository;
    DailyActivityRepository dailyActivityRepository;
    Clock clock;

    /**
     * Начисляет XP за ответ и поднимает рекорд серии. Обёрнуто в try/catch —
     * геймификация не критичный путь и не должна ронять ответ пользователя.
     */
    @EventListener
    public void onAnswer(AnswerEvent event) {
        try {
            Difficulty difficulty = questionRepository.findById(event.getQuestionId())
                    .map(Question::difficulty)
                    .orElse(Difficulty.MEDIUM);
            long now = clock.instant().getEpochSecond();
            experienceRepository.addXp(xpFor(event.isCorrect(), difficulty), now);
            int streak = safeStreak();
            if (streak > 0) {
                experienceRepository.raiseBestStreak(streak, now);
            }
        } catch (Exception e) {
            log.warn("Не удалось начислить XP [{}]: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * XP за одну попытку: верный ответ — {@link #BASE_XP} × вес сложности
     * (EASY 1.0 / MEDIUM 1.5 / HARD 2.0), неверный — {@link #PARTIAL_XP}. Чистая
     * функция: тот же результат для показа «+N XP» и для начисления.
     */
    public long xpFor(boolean correct, Difficulty difficulty) {
        if (!correct) {
            return PARTIAL_XP;
        }
        double weight = switch (difficulty == null ? Difficulty.MEDIUM : difficulty) {
            case EASY -> 1.0;
            case MEDIUM -> 1.5;
            case HARD -> 2.0;
        };
        return Math.round(BASE_XP * weight);
    }

    /**
     * Снимок опыта для показа (итоги/аналитика). Рекорд серии гарантированно не
     * меньше текущей серии, даже если листенер ещё не успел его поднять.
     */
    public UserExperience snapshot() {
        UserExperience experience = experienceRepository.find();
        int streak = safeStreak();
        return streak > experience.bestStreak()
                ? experience.toBuilder().bestStreak(streak).build()
                : experience;
    }

    private int safeStreak() {
        try {
            return dailyActivityRepository.findStreak().currentStreak();
        } catch (Exception e) {
            return 0;
        }
    }
}
