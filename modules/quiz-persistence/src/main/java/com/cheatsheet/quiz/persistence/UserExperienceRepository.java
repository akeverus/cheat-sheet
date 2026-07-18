package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.UserExperience;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий синглтон-строки {@code user_experience} (id = 1).
 *
 * <p>Строка гарантированно создана миграцией V19, поэтому все изменения — через
 * UPDATE без предварительной проверки существования. {@code level} не хранится:
 * выводится доменом {@link UserExperience#level()}.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserExperienceRepository {

    JdbcTemplate jdbcTemplate;

    /** Текущий опыт; {@link UserExperience#EMPTY} если строки почему-то нет. */
    public UserExperience find() {
        List<UserExperience> rows = jdbcTemplate.query(
                "SELECT xp, best_streak, updated_at FROM user_experience WHERE id = 1",
                (rs, rowNum) -> new UserExperience(
                        rs.getLong("xp"),
                        rs.getInt("best_streak"),
                        rs.getLong("updated_at")));
        return rows.isEmpty() ? UserExperience.EMPTY : rows.get(0);
    }

    /**
     * Начисляет {@code delta} XP (может быть 0). Возвращает опыт после начисления.
     */
    public UserExperience addXp(long delta, long now) {
        jdbcTemplate.update(
                "UPDATE user_experience SET xp = xp + ?, updated_at = ? WHERE id = 1",
                delta, now);
        return find();
    }

    /**
     * Поднимает рекорд серии до {@code streak}, если он больше текущего (иначе no-op).
     */
    public void raiseBestStreak(int streak, long now) {
        jdbcTemplate.update(
                "UPDATE user_experience SET best_streak = ?, updated_at = ? " +
                        "WHERE id = 1 AND best_streak < ?",
                streak, now, streak);
    }
}
