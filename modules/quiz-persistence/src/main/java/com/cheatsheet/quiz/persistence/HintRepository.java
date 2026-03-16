package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Hint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с таблицей {@code question_hints}.
 *
 * <p>Хранит прогрессивные AI-подсказки (3 уровня на вопрос).</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HintRepository {

    JdbcTemplate jdbcTemplate;

    static final RowMapper<Hint> HINT_MAPPER = (rs, rowNum) -> new Hint(
            rs.getLong("id"),
            rs.getLong("question_id"),
            rs.getInt("level"),
            rs.getString("hint_text"),
            rs.getLong("created_at")
    );

    /**
     * Все подсказки для вопроса, отсортированные по уровню.
     */
    public List<Hint> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT id, question_id, level, hint_text, created_at FROM question_hints WHERE question_id = ? ORDER BY level",
                HINT_MAPPER, questionId);
    }

    /**
     * Конкретная подсказка по вопросу и уровню.
     */
    public Optional<Hint> findByQuestionIdAndLevel(long questionId, int level) {
        List<Hint> hints = jdbcTemplate.query(
                "SELECT id, question_id, level, hint_text, created_at FROM question_hints WHERE question_id = ? AND level = ?",
                HINT_MAPPER, questionId, level);
        return hints.stream().findFirst();
    }

    /**
     * Batch-вставка подсказок (обычно 3 штуки за раз).
     * Использует {@link JdbcTemplate#batchUpdate} вместо поштучной вставки.
     */
    @Transactional
    public void insertAll(List<Hint> hints) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO question_hints (question_id, level, hint_text, created_at) VALUES (?, ?, ?, ?)",
                hints, hints.size(),
                (ps, hint) -> {
                    ps.setLong(1, hint.questionId());
                    ps.setInt(2, hint.level());
                    ps.setString(3, hint.hintText());
                    ps.setLong(4, hint.createdAt());
                });
    }

    /**
     * Удаляет все подсказки для вопроса (при перегенерации вариантов).
     */
    @Transactional
    public void deleteByQuestionId(long questionId) {
        jdbcTemplate.update("DELETE FROM question_hints WHERE question_id = ?", questionId);
    }

    /**
     * Удаляет все подсказки из таблицы (полный сброс).
     *
     * @return количество удалённых записей
     */
    @Transactional
    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM question_hints");
    }
}
