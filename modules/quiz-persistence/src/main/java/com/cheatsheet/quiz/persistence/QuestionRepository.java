package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.QuestionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для работы с таблицей {@code questions}.
 *
 * <p>Предоставляет CRUD-операции и специализированные запросы для:</p>
 * <ul>
 *   <li>выборки вопросов по фильтрам (тема, важность, ошибки);</li>
 *   <li>выборки вопросов для сессий (with review_state JOIN);</li>
 *   <li>получения случайных ответов для fallback-генерации вариантов.</li>
 * </ul>
 *
 * @see Question
 * @see InterviewFilterSql
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionRepository {

    /** Стандартный набор столбцов для SELECT (с алиасом {@code q}). */
    static final String QUESTION_COLUMNS =
            "q.id, q.slug, q.source_slug, q.file_path, q.topic, q.question_text, q.answer_markdown, q.is_important, q.source_hash, " +
                    "q.question_type, q.code_snippet, q.diagram_mermaid, q.regen_count, q.takeaway, q.difficulty, " +
                    "q.short_explanation, q.detailed_explanation, q.common_mistake, q.tags";

    JdbcTemplate jdbcTemplate;

    /**
     * Находит вопрос по slug (уникальный ключ).
     *
     * @param slug уникальный идентификатор (например, {@code "java.md#Q1"})
     * @return вопрос или пустой Optional
     */
    public Optional<Question> findBySlug(String slug) {
        List<Question> items = jdbcTemplate.query(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q WHERE q.slug = ?",
                questionRowMapper(), slug);
        return items.stream().findFirst();
    }

    /**
     * Находит вопрос по ID.
     *
     * @param id идентификатор вопроса
     * @return вопрос или пустой Optional
     */
    public Optional<Question> findById(long id) {
        List<Question> items = jdbcTemplate.query(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q WHERE q.id = ?",
                questionRowMapper(), id);
        return items.stream().findFirst();
    }

    /**
     * Количество расширенных вариантов у базового вопроса (slug-vN),
     * сгруппированных по source_slug.
     */
    public int countExpandedBySourceSlug(String sourceSlug) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM questions WHERE source_slug = ? AND slug <> source_slug",
                Integer.class,
                sourceSlug
        );
        return count == null ? 0 : count;
    }

    /**
     * Пакетная загрузка вопросов по списку ID (решает проблему N+1).
     *
     * @param ids список идентификаторов
     * @return список найденных вопросов
     */
    public List<Question> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("SELECT " + QUESTION_COLUMNS + " FROM questions q WHERE q.id IN (");
        var params = new ArrayList<Object>();
        for (int i = 0; i < ids.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(ids.get(i));
        }
        sql.append(")");
        return jdbcTemplate.query(sql.toString(), questionRowMapper(), params.toArray());
    }

    /**
     * Вставляет новый вопрос и возвращает сгенерированный ID.
     *
     * @param question объект вопроса (id игнорируется)
     * @return сгенерированный ID
     */
    @Transactional
    public long insert(Question question) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, is_important, source_hash, question_type, code_snippet, diagram_mermaid, takeaway, difficulty, short_explanation, detailed_explanation, common_mistake, tags) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id"});
            String sourceSlug = question.sourceSlug() != null ? question.sourceSlug() : question.slug();
            ps.setString(1, question.slug());
            ps.setString(2, sourceSlug);
            ps.setString(3, question.filePath());
            ps.setString(4, question.topic());
            ps.setString(5, question.questionText());
            ps.setString(6, question.answerMarkdown());
            ps.setInt(7, question.important() ? 1 : 0);
            ps.setString(8, question.sourceHash());
            ps.setString(9, questionTypeOrDefault(question));
            ps.setString(10, question.codeSnippet());
            ps.setString(11, question.diagramMermaid());
            ps.setString(12, question.takeaway());
            ps.setString(13, question.difficulty() == null ? Difficulty.MEDIUM.name() : question.difficulty().name());
            ps.setString(14, question.shortExplanation());
            ps.setString(15, question.detailedExplanation());
            ps.setString(16, question.commonMistake());
            ps.setString(17, serializeTags(question.tags()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0L : key.longValue();
    }

    /**
     * Обновляет только поле code_snippet для вопроса (после очистки LLM от комментариев-подсказок).
     *
     * @param questionId идентификатор вопроса
     * @param cleanedSnippet очищенный фрагмент кода
     */
    @Transactional
    public void updateCodeSnippet(long questionId, String cleanedSnippet) {
        jdbcTemplate.update("UPDATE questions SET code_snippet = ? WHERE id = ?", cleanedSnippet, questionId);
    }

    /**
     * Переключает флаг «избранное» (is_important) для вопроса.
     *
     * @param questionId идентификатор вопроса
     * @param important  новое значение флага
     */
    @Transactional
    public void updateImportant(long questionId, boolean important) {
        jdbcTemplate.update("UPDATE questions SET is_important = ? WHERE id = ?",
                important ? 1 : 0, questionId);
    }

    /**
     * Обновляет существующий вопрос (при изменении контента markdown-файла).
     *
     * @param question обновлённый вопрос (id должен быть задан)
     */
    @Transactional
    public void update(Question question) {
        jdbcTemplate.update(
                "UPDATE questions SET source_slug = ?, file_path = ?, topic = ?, question_text = ?, answer_markdown = ?, " +
                        "is_important = ?, source_hash = ?, question_type = ?, code_snippet = ?, difficulty = ?, short_explanation = ?, " +
                        "detailed_explanation = ?, common_mistake = ?, tags = ? WHERE id = ?",
                question.sourceSlug() != null ? question.sourceSlug() : question.slug(),
                question.filePath(), question.topic(), question.questionText(),
                question.answerMarkdown(), question.important() ? 1 : 0,
                question.sourceHash(), questionTypeOrDefault(question), question.codeSnippet(),
                question.difficulty() == null ? Difficulty.MEDIUM.name() : question.difficulty().name(),
                question.shortExplanation(),
                question.detailedExplanation(),
                question.commonMistake(),
                serializeTags(question.tags()),
                question.id());
    }

    private static String questionTypeOrDefault(Question question) {
        if (question.questionType() == null) {
            log.warn("Question slug={} имеет null questionType, подставлен TEXT", question.slug());
            return QuestionType.TEXT.name();
        }
        return question.questionType().name();
    }

    /**
     * Находит просроченные (due) вопросы, отсортированные по приоритету.
     *
     * @param topic     фильтр по теме (null — все)
     * @param important фильтр «только важные» (null/false — все)
     * @param onlyWrong фильтр «только с ошибками» (null/false — все)
     * @param nowEpoch  текущее время в epoch-секундах
     * @param limit     максимальное количество результатов
     * @return список вопросов
     */
    public List<Question> findDueQuestions(String topic, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE rs.next_review_at <= ? ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        sql.append(" ORDER BY rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), questionRowMapper(), params.toArray());
    }

    /**
     * Находит просроченные (due) вопросы по списку тем в заданном порядке.
     */
    public List<Question> findDueQuestionsByTopics(List<String> topics, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE rs.next_review_at <= ? ");
        var params = new ArrayList<Object>();
        params.add(nowEpoch);
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" ORDER BY ");
        appendTopicOrderCase(sql, params, topics);
        sql.append(", rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), questionRowMapper(), params.toArray());
    }

    /**
     * Находит следующие вопросы для повторения (ближайшие по дате).
     */
    public List<Question> findNextQuestions(String topic, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        sql.append(" ORDER BY rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), questionRowMapper(), params.toArray());
    }

    /**
     * Находит следующие вопросы для повторения по списку тем в заданном порядке.
     */
    public List<Question> findNextQuestionsByTopics(List<String> topics, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT " + QUESTION_COLUMNS + " FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" ORDER BY ");
        appendTopicOrderCase(sql, params, topics);
        sql.append(", rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), questionRowMapper(), params.toArray());
    }

    /**
     * Находит ID вопросов для предзагрузки (только без вариантов ответов).
     */
    public List<Long> findQuestionIdsForPreload(String topic, Boolean important, Boolean onlyWrong, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT q.id FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id " +
                        "LEFT JOIN (SELECT question_id, COUNT(*) AS cnt FROM answer_options GROUP BY question_id) ao " +
                        "ON ao.question_id = q.id WHERE ao.cnt IS NULL ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        sql.append(" ORDER BY rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong("id"), params.toArray());
    }

    /**
     * Находит ID вопросов для предзагрузки по списку тем в заданном порядке.
     */
    public List<Long> findQuestionIdsForPreloadByTopics(List<String> topics, Boolean important, Boolean onlyWrong, int limit) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT q.id FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id " +
                        "LEFT JOIN (SELECT question_id, COUNT(*) AS cnt FROM answer_options GROUP BY question_id) ao " +
                        "ON ao.question_id = q.id WHERE ao.cnt IS NULL ");
        var params = new ArrayList<Object>();
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" ORDER BY ");
        appendTopicOrderCase(sql, params, topics);
        sql.append(", rs.next_review_at ASC, (rs.correct_count + rs.wrong_count) ASC, q.id ASC LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong("id"), params.toArray());
    }

    /**
     * Находит ID вопросов для новой сессии.
     */
    public List<Long> findQuestionIdsForSession(String topic, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        return findQuestionIdsExcluding(List.of(), topic, important, onlyWrong, nowEpoch, limit);
    }

    /**
     * Находит ID вопросов для новой сессии по списку тем в заданном порядке.
     */
    public List<Long> findQuestionIdsForSessionByTopics(List<String> topics, Boolean important, Boolean onlyWrong, long nowEpoch, int limit) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        return findQuestionIdsExcludingByTopics(List.of(), topics, important, onlyWrong, nowEpoch, limit);
    }

    /**
     * Находит ID вопросов, исключая уже пройденные (для штрафных вопросов экзамена).
     */
    public List<Long> findQuestionIdsExcluding(List<Long> excludeIds, String topic, Boolean important,
                                                Boolean onlyWrong, long nowEpoch, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT q.id FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        if (excludeIds != null && !excludeIds.isEmpty()) {
            sql.append(" AND q.id NOT IN (");
            for (int i = 0; i < excludeIds.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
            }
            sql.append(") ");
            params.addAll(excludeIds);
        }
        InterviewFilterSql.appendFilters(sql, params, topic, important, onlyWrong);
        sql.append(" ORDER BY CASE WHEN rs.next_review_at <= ? THEN 0 ELSE 1 END, rs.next_review_at ASC, RANDOM() LIMIT ?");
        params.add(nowEpoch);
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong("id"), params.toArray());
    }

    /**
     * Находит ID вопросов по списку тем, исключая уже пройденные.
     */
    public List<Long> findQuestionIdsExcludingByTopics(List<Long> excludeIds, List<String> topics, Boolean important,
                                                       Boolean onlyWrong, long nowEpoch, int limit) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT q.id FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        if (excludeIds != null && !excludeIds.isEmpty()) {
            sql.append(" AND q.id NOT IN (");
            for (int i = 0; i < excludeIds.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
            }
            sql.append(") ");
            params.addAll(excludeIds);
        }
        InterviewFilterSql.appendTopicsFilter(sql, params, topics);
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" ORDER BY ");
        appendTopicOrderCase(sql, params, topics);
        sql.append(", CASE WHEN rs.next_review_at <= ? THEN 0 ELSE 1 END, rs.next_review_at ASC, RANDOM() LIMIT ?");
        params.add(nowEpoch);
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong("id"), params.toArray());
    }

    /**
     * Находит ID вопросов в чисто случайном порядке (для режима «Все вперемешку»).
     * Игнорирует тему и порядок spaced repetition — полная рандомизация.
     *
     * @param important фильтр «только важные»
     * @param onlyWrong фильтр «только с ошибками»
     * @param limit     максимальное количество
     * @return список ID в случайном порядке
     */
    public List<Long> findShuffledQuestionIds(Boolean important, Boolean onlyWrong, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT q.id FROM questions q " +
                        "JOIN review_state rs ON rs.question_id = q.id WHERE 1=1 ");
        var params = new ArrayList<Object>();
        // topic = null — все темы
        InterviewFilterSql.appendFilters(sql, params, null, important, onlyWrong);
        sql.append(" ORDER BY RANDOM() LIMIT ?");
        params.add(limit);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong("id"), params.toArray());
    }

    /**
     * Возвращает список всех уникальных тем (для фильтра в UI).
     */
    public List<String> findTopics() {
        return jdbcTemplate.query(
                "SELECT DISTINCT topic FROM questions WHERE topic IS NOT NULL ORDER BY topic",
                (rs, rowNum) -> rs.getString("topic"));
    }

    /**
     * Все ID вопросов, у которых нет вариантов ответов.
     * Используется для полного прогрева при старте.
     *
     * @return полный список ID (без limit)
     */
    public List<Long> findAllQuestionIdsWithoutOptions() {
        return jdbcTemplate.query(
                "SELECT q.id FROM questions q " +
                        "LEFT JOIN (SELECT question_id, COUNT(*) AS cnt FROM answer_options GROUP BY question_id) ao " +
                        "ON ao.question_id = q.id WHERE ao.cnt IS NULL ORDER BY q.id",
                (rs, rowNum) -> rs.getLong("id"));
    }

    /**
     * Все уникальные file_path из таблицы questions.
     * Используется для orphan cleanup при импорте MD-файлов.
     */
    public List<String> findAllFilePaths() {
        return jdbcTemplate.query(
                "SELECT DISTINCT file_path FROM questions",
                (rs, rowNum) -> rs.getString("file_path"));
    }

    /**
     * Удаляет все вопросы по file_path. Связанные данные (answer_options, hints, review_state)
     * удаляются каскадно через FK.
     *
     * @param filePath путь к файлу
     * @return количество удалённых вопросов
     */
    @Transactional
    public int deleteByFilePath(String filePath) {
        return jdbcTemplate.update("DELETE FROM questions WHERE file_path = ?", filePath);
    }

    /**
     * Общее количество вопросов.
     */
    public long countAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        return count != null ? count : 0;
    }

    /**
     * Инкрементирует счётчик перезагрузок для вопроса.
     *
     * @param questionId идентификатор вопроса
     */
    @Transactional
    public void incrementRegenCount(long questionId) {
        jdbcTemplate.update("UPDATE questions SET regen_count = regen_count + 1 WHERE id = ?", questionId);
    }

    /**
     * Обновляет Mermaid-диаграмму для вопроса.
     *
     * @param questionId  идентификатор вопроса
     * @param mermaidCode код диаграммы (или null для очистки)
     */
    @Transactional
    public void updateDiagram(long questionId, String mermaidCode) {
        jdbcTemplate.update("UPDATE questions SET diagram_mermaid = ? WHERE id = ?", mermaidCode, questionId);
    }

    /**
     * Очищает Mermaid-диаграммы у всех вопросов (полный сброс).
     *
     * @return количество обновлённых записей
     */
    @Transactional
    public int clearAllDiagrams() {
        return jdbcTemplate.update("UPDATE questions SET diagram_mermaid = NULL WHERE diagram_mermaid IS NOT NULL");
    }

    /**
     * Сбрасывает regen_count у всех вопросов.
     *
     * @return количество обновлённых записей
     */
    @Transactional
    public int resetAllRegenCount() {
        return jdbcTemplate.update("UPDATE questions SET regen_count = 0 WHERE regen_count > 0");
    }

    /**
     * Находит связанные вопросы из той же темы, исключая текущий.
     * Приоритет: вопросы с ошибками (wrong_count > 0) → остальные.
     *
     * @param excludeId ID вопроса для исключения
     * @param topic     тема
     * @param limit     максимум результатов
     * @return список related вопросов
     */
    public List<com.cheatsheet.quiz.domain.RelatedQuestion> findRelatedByTopic(long excludeId, String topic, int limit) {
        return jdbcTemplate.query(
                "SELECT q.id, q.question_text, q.topic, " +
                        "CASE WHEN (COALESCE(rs.correct_count, 0) + COALESCE(rs.wrong_count, 0)) > 0 " +
                        "  THEN CAST(COALESCE(rs.correct_count, 0) AS REAL) * 100.0 / (COALESCE(rs.correct_count, 0) + COALESCE(rs.wrong_count, 0)) " +
                        "  ELSE -1 END AS accuracy " +
                        "FROM questions q LEFT JOIN review_state rs ON rs.question_id = q.id " +
                        "WHERE q.topic = ? AND q.id != ? " +
                        "ORDER BY COALESCE(rs.wrong_count, 0) DESC, RANDOM() " +
                        "LIMIT ?",
                (rs, rowNum) -> new com.cheatsheet.quiz.domain.RelatedQuestion(
                        rs.getLong("id"),
                        rs.getString("question_text"),
                        rs.getString("topic"),
                        rs.getDouble("accuracy")
                ),
                topic, excludeId, limit
        );
    }

    /** Маппер строки ResultSet → {@link Question}. */
    private static RowMapper<Question> questionRowMapper() {
        return (rs, rowNum) -> mapQuestion(rs);
    }

    private static Question mapQuestion(ResultSet rs) throws SQLException {
        String slug = rs.getString("slug");
        String sourceSlug = rs.getString("source_slug");
        if (sourceSlug == null) {
            sourceSlug = slug;
        }
        String questionTypeStr = rs.getString("question_type");
        QuestionType questionType;
        if (questionTypeStr == null || questionTypeStr.isBlank()) {
            questionType = QuestionType.TEXT;
        } else {
            try {
                questionType = QuestionType.valueOf(questionTypeStr.strip().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                        "Неизвестный question_type='" + questionTypeStr + "' для вопроса slug="
                                + rs.getString("slug") + ". Допустимые значения: TEXT, CODE", e);
            }
        }
        return new Question(
                rs.getLong("id"),
                slug,
                sourceSlug,
                rs.getString("file_path"),
                rs.getString("topic"),
                rs.getString("question_text"),
                rs.getString("answer_markdown"),
                rs.getInt("is_important") == 1,
                rs.getString("source_hash"),
                questionType,
                rs.getString("code_snippet"),
                rs.getString("diagram_mermaid"),
                rs.getInt("regen_count"),
                rs.getString("takeaway"),
                Difficulty.fromString(rs.getString("difficulty")),
                rs.getString("short_explanation"),
                rs.getString("detailed_explanation"),
                rs.getString("common_mistake"),
                parseTags(rs.getString("tags")),
                List.of()
        );
    }

    private static List<String> parseTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private static String serializeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(","));
    }

    private static void appendTopicOrderCase(StringBuilder sql, List<Object> params, List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            // Для пустого списка тем appendTopicsFilter добавляет `AND 1 = 0`,
            // поэтому сортировка не влияет на результат. Возвращаем валидное SQL-выражение.
            sql.append("q.id");
            return;
        }
        sql.append("CASE q.topic ");
        for (int i = 0; i < topics.size(); i++) {
            sql.append(" WHEN ? THEN ").append(i);
            params.add(topics.get(i));
        }
        sql.append(" ELSE ").append(FALLBACK_TOPIC_ORDER_WEIGHT).append(" END");
    }

    private static final int FALLBACK_TOPIC_ORDER_WEIGHT = 100_000;

    /**
     * Обновляет takeaway для вопроса.
     *
     * @param questionId   идентификатор вопроса
     * @param takeawayText текст takeaway
     */
    @Transactional
    public void updateTakeaway(long questionId, String takeawayText) {
        jdbcTemplate.update("UPDATE questions SET takeaway = ? WHERE id = ?", takeawayText, questionId);
    }
}
