package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.UserExperience;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий {@code user_experience}, scoped by {@link OwnerContext}.
 *
 * <p>{@code level} не хранится: выводится доменом {@link UserExperience#level()}.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserExperienceRepository {

    JdbcTemplate jdbcTemplate;

    /** Текущий опыт; создаёт строку владельца при первом обращении. */
    public UserExperience find() {
        ensureRow();
        List<UserExperience> rows = jdbcTemplate.query(
                "SELECT xp, best_streak, updated_at FROM user_experience WHERE owner_key = ?",
                (rs, rowNum) -> new UserExperience(
                        rs.getLong("xp"),
                        rs.getInt("best_streak"),
                        rs.getLong("updated_at")),
                OwnerContext.current());
        return rows.isEmpty() ? UserExperience.EMPTY : rows.get(0);
    }

    /**
     * Начисляет {@code delta} XP (может быть 0). Возвращает опыт после начисления.
     */
    public UserExperience addXp(long delta, long now) {
        ensureRow();
        jdbcTemplate.update(
                "UPDATE user_experience SET xp = xp + ?, updated_at = ? WHERE owner_key = ?",
                delta, now, OwnerContext.current());
        return find();
    }

    /**
     * Поднимает рекорд серии до {@code streak}, если он больше текущего (иначе no-op).
     */
    public void raiseBestStreak(int streak, long now) {
        ensureRow();
        jdbcTemplate.update(
                "UPDATE user_experience SET best_streak = ?, updated_at = ? " +
                        "WHERE owner_key = ? AND best_streak < ?",
                streak, now, OwnerContext.current(), streak);
    }

    private void ensureRow() {
        jdbcTemplate.update(
                "INSERT INTO user_experience (owner_key, xp, best_streak, updated_at) VALUES (?, 0, 0, 0) "
                        + "ON CONFLICT (owner_key) DO NOTHING",
                OwnerContext.current());
    }
}
