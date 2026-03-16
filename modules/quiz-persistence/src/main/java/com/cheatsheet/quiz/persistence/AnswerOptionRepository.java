package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.AnswerOption;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Репозиторий для работы с вариантами ответов ({@code answer_options}).
 *
 * @see AnswerOption
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerOptionRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Находит все варианты ответа для вопроса, упорядоченные по {@code display_order}.
     *
     * @param questionId идентификатор вопроса
     * @return список вариантов
     */
    public List<AnswerOption> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT id, question_id, option_text, is_correct, display_order, source, explanation, prompt_version, quality_profile_version " +
                        "FROM answer_options WHERE question_id = ? ORDER BY display_order ASC",
                (rs, rowNum) -> new AnswerOption(
                        rs.getLong("id"),
                        rs.getLong("question_id"),
                        rs.getString("option_text"),
                        rs.getInt("is_correct") == 1,
                        rs.getInt("display_order"),
                        rs.getString("source"),
                        rs.getString("explanation"),
                        rs.getInt("prompt_version"),
                        rs.getInt("quality_profile_version")),
                questionId);
    }

    /**
     * Подсчитывает количество вариантов для вопроса.
     *
     * @param questionId идентификатор вопроса
     * @return количество вариантов
     */
    public long countByQuestionId(long questionId) {
        Long value = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM answer_options WHERE question_id = ?",
                Long.class, questionId);
        return value == null ? 0L : value;
    }

    /**
     * Удаляет все варианты для вопроса (перед перегенерацией).
     *
     * @param questionId идентификатор вопроса
     */
    @Transactional
    public void deleteByQuestionId(long questionId) {
        jdbcTemplate.update("DELETE FROM answer_options WHERE question_id = ?", questionId);
    }

    /**
     * Удаляет все варианты ответов из таблицы (для перегенерации при смене AI/fallback).
     *
     * <p>Используйте совместно с {@link com.cheatsheet.quiz.service.cache.OptionCache#invalidateAll()}.</p>
     *
     * @return количество удалённых строк
     */
    @Transactional
    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM answer_options");
    }

    /**
     * Пакетная вставка вариантов ответа.
     *
     * @param questionId идентификатор вопроса
     * @param options    список вариантов для вставки
     */
    @Transactional
    public void insertAll(long questionId, List<AnswerOptionCreate> options) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO answer_options (question_id, option_text, is_correct, display_order, source, explanation, prompt_version, quality_profile_version) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                options, options.size(),
                (ps, option) -> {
                    ps.setLong(1, questionId);
                    ps.setString(2, option.optionText());
                    ps.setInt(3, option.correct() ? 1 : 0);
                    ps.setInt(4, option.displayOrder());
                    ps.setString(5, option.source());
                    ps.setString(6, option.explanation());
                    ps.setInt(7, option.promptVersion());
                    ps.setInt(8, option.qualityProfileVersion());
                });
    }

    /**
     * Данные для создания варианта ответа (без id и questionId — они задаются при вставке).
     *
     * @param optionText   текст варианта
     * @param correct      {@code true} если это правильный вариант
     * @param displayOrder порядок отображения
     * @param source       источник генерации ({@link com.cheatsheet.quiz.domain.OptionSource#name()})
     * @param explanation  объяснение, почему вариант правильный/неправильный (nullable)
     * @param promptVersion версия prompt-контракта генерации
     * @param qualityProfileVersion версия quality-профиля валидации
     */
    @Builder(toBuilder = true)
    public record AnswerOptionCreate(
            String optionText,
            boolean correct,
            int displayOrder,
            String source,
            String explanation,
            int promptVersion,
            int qualityProfileVersion
    ) {
        public AnswerOptionCreate(
                String optionText,
                boolean correct,
                int displayOrder,
                String source,
                String explanation
        ) {
            this(optionText, correct, displayOrder, source, explanation, 1, 1);
        }

        public AnswerOptionCreate {
            Objects.requireNonNull(optionText, "optionText");
            Objects.requireNonNull(source, "source");
            if (displayOrder < 0) {
                throw new IllegalArgumentException("displayOrder must be >= 0");
            }
            if (promptVersion < 1) {
                throw new IllegalArgumentException("promptVersion must be >= 1");
            }
            if (qualityProfileVersion < 1) {
                throw new IllegalArgumentException("qualityProfileVersion must be >= 1");
            }
        }
    }
}
