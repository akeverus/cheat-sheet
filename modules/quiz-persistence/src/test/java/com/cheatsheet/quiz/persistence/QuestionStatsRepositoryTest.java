package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cheatsheet.quiz.domain.TopicStats;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Тесты {@link QuestionStatsRepository} на реальном PostgreSQL через Testcontainers.
 */
class QuestionStatsRepositoryTest extends AbstractPostgresRepositoryTest {

    private QuestionStatsRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new QuestionStatsRepository(jdbcTemplate);
    }

    /**
     * Когда в БД нет ни вопросов, ни строк в review_state, getAggregatedStats не должен бросать исключение,
     * а должен вернуть статистику из нулей.
     */
    @Test
    void getAggregatedStatsOnEmptyDbReturnsZeroes() {
        long nowEpoch = System.currentTimeMillis() / 1000;
        int minRepetitions = 3;

        InterviewStats stats = repository.getAggregatedStats(null, null, null, nowEpoch, minRepetitions);

        assertThat(stats.total()).isZero();
        assertThat(stats.due()).isZero();
        assertThat(stats.fresh()).isZero();
        assertThat(stats.learned()).isZero();
        assertThat(stats.correct()).isZero();
        assertThat(stats.wrong()).isZero();
    }

    /**
     * Вопрос без строки прогресса — не ноль, а единица в {@code total} и {@code due}.
     *
     * <p>Раньше здесь ожидались нули: соединение с {@code review_state} было
     * внутренним, и вопрос, на который ещё не отвечали, не попадал ни в один
     * счётчик. Сводка показывала не размер банка, а размер прогресса, — на главной
     * это читалось как «3 из 6 к повтору» при девяти тысячах вопросов в базе.
     * Ответов у нового вопроса нет, поэтому {@code learned} и суммы остаются нулями.
     */
    @Test
    void getAggregatedStatsCountsQuestionsWithoutReviewState() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown,
                    is_important, source_hash, question_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "q1", "q1", "test/q1.md", "Java", "Q?", "A", 0, "hash", "TEXT");

        long nowEpoch = System.currentTimeMillis() / 1000;
        InterviewStats stats = repository.getAggregatedStats(null, null, null, nowEpoch, 3);

        assertThat(stats.total()).isEqualTo(1);
        assertThat(stats.due()).isEqualTo(1);
        // Готов к показу целиком за счёт новизны — повторять нечего.
        assertThat(stats.fresh()).isEqualTo(1);
        assertThat(stats.dueRepeats()).isZero();
        assertThat(stats.learned()).isZero();
        assertThat(stats.correct()).isZero();
        assertThat(stats.wrong()).isZero();
    }

    /**
     * Каталог тем на {@code /stats} показывает тему, в которой ещё не отвечали.
     *
     * <p>Пустой {@code topicStats} рендерится как «Пока нет данных по темам» — то есть
     * на полном банке вопросов страница статистики выглядела бы как у нового
     * пользователя, и оба графика с таблицей просто не выводились.
     */
    @Test
    void findTopicStatsIncludesTopicsWithoutAnyProgress() {
        jdbcTemplate.update(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "fresh", "fresh", "f.md", "kotlin", "Q?", "A.", "h-fresh");

        List<TopicStats> stats = repository.findTopicStats(System.currentTimeMillis() / 1000, 3);

        assertThat(stats).singleElement().satisfies(topic -> {
            assertThat(topic.topic()).isEqualTo("kotlin");
            assertThat(topic.total()).isEqualTo(1);
            assertThat(topic.due()).isEqualTo(1);
            // Вопрос готов к показу, но повторять его нечего — он новый.
            assertThat(topic.fresh()).isEqualTo(1);
            assertThat(topic.dueRepeats()).isZero();
            assertThat(topic.learned()).isZero();
            assertThat(topic.maturityScore()).isZero();
        });
    }

    @Test
    void findTopicStatsIncludesMaturityScore() {
        long nowEpoch = System.currentTimeMillis() / 1000;

        jdbcTemplate.update(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "q1", "q1", "f.md", "java", "Q?", "A.", "h1");
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
                "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                1L, 3, 10, 2.5, nowEpoch - 1, "CORRECT", 5, 1);

        List<TopicStats> stats = repository.findTopicStats(nowEpoch, 3);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).maturityScore()).isEqualTo(25.0, within(0.01));
        // 2.5 * 10 = 25.0
        // Срок прошёл, история есть — это повторение, а не новый вопрос.
        assertThat(stats.get(0).fresh()).isZero();
        assertThat(stats.get(0).dueRepeats()).isEqualTo(1);
    }

    @Test
    void getScheduleAggregateSplitsDueNowFromOverdue() {
        long now = 1_800_000_000L;
        long day = 86_400L;
        // 3 карточки: одна выучена и в будущем, одна due (полдня назад),
        // одна просрочена (2 дня назад). dueNow = due + overdue = 2, overdue = 1.
        insertCard("q1", 5, now + day, 4);      // не due, выучена (repetitions ≥ 3)
        insertCard("q2", 1, now - day / 2, 0);  // due сейчас, не просрочена (< суток)
        insertCard("q3", 0, now - 2 * day, 0);  // просрочена (≥ суток)

        QuestionStatsRepository.ScheduleAggregate agg =
                repository.getScheduleAggregate(now, now - day, 3);

        assertThat(agg.cardsTotal()).isEqualTo(3);
        assertThat(agg.cardsMastered()).isEqualTo(1);
        assertThat(agg.dueNow()).isEqualTo(2);
        assertThat(agg.overdue()).isEqualTo(1);
        // У всех трёх карточек есть расписание — новых среди них нет.
        assertThat(agg.dueFresh()).isZero();
    }

    /**
     * Вопрос без расписания попадает и в {@code dueNow}, и в {@code dueFresh}:
     * он готов к показу не потому, что подошёл срок, а потому что его не открывали.
     */
    @Test
    void getScheduleAggregateCountsQuestionsWithoutScheduleAsFresh() {
        long now = 1_800_000_000L;
        long day = 86_400L;
        insertCard("scheduled", 1, now - day / 2, 0);
        jdbcTemplate.update(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "new-one", "new-one", "n.md", "java", "Q?", "A.", "h-new");

        QuestionStatsRepository.ScheduleAggregate agg =
                repository.getScheduleAggregate(now, now - day, 3);

        assertThat(agg.cardsTotal()).isEqualTo(2);
        assertThat(agg.dueNow()).isEqualTo(2);
        assertThat(agg.dueFresh()).isEqualTo(1);
        // Новый вопрос нельзя просрочить: срока у него не было.
        assertThat(agg.overdue()).isZero();
    }

    @Test
    void getScheduleAggregateOnEmptyDbReturnsZeroes() {
        QuestionStatsRepository.ScheduleAggregate agg =
                repository.getScheduleAggregate(1_800_000_000L, 1_799_913_600L, 3);
        assertThat(agg.cardsTotal()).isZero();
        assertThat(agg.dueNow()).isZero();
        assertThat(agg.overdue()).isZero();
        assertThat(agg.dueFresh()).isZero();
    }

    @Test
    void findRecentWrongQuestionsOrdersByLastWrongAttemptThenWrongCount() {
        long qOld = insertWrongCard("wrong-old", "spring", "Old wrong?", 1, 0);
        long qFresh = insertWrongCard("wrong-fresh", "java", "Fresh wrong?", 2, 0);
        insertWrongCard("never-wrong", "java", "Never wrong?", 0, 3); // wrong_count=0 — не в списке

        long t1 = 1_700_000_000L;
        long t2 = 1_700_000_100L;
        jdbcTemplate.update(
                "INSERT INTO attempt (question_id, outcome, is_first_attempt, created_at) VALUES (?, 'WRONG', true, ?)",
                qOld, t1);
        jdbcTemplate.update(
                "INSERT INTO attempt (question_id, outcome, is_first_attempt, created_at) VALUES (?, 'WRONG', true, ?)",
                qFresh, t2);

        var rows = repository.findRecentWrongQuestions(List.of("spring", "java"), null, 10);

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).questionId()).isEqualTo(qFresh);
        assertThat(rows.get(0).topic()).isEqualTo("java");
        assertThat(rows.get(0).wrongCount()).isEqualTo(2);
        assertThat(rows.get(0).lastWrongAt()).isEqualTo(t2);
        assertThat(rows.get(1).questionId()).isEqualTo(qOld);
        assertThat(rows.get(1).lastWrongAt()).isEqualTo(t1);
    }

    @Test
    void findRecentWrongQuestionsRespectsLimit() {
        insertWrongCard("w1", "java", "Q1?", 1, 0);
        insertWrongCard("w2", "java", "Q2?", 1, 0);
        insertWrongCard("w3", "java", "Q3?", 1, 0);

        assertThat(repository.findRecentWrongQuestions(List.of("java"), null, 2)).hasSize(2);
    }

    /**
     * FRD-025: срез по темам обязан подчиняться отбору страницы. Гейт держит именно это —
     * тема вне выборки не попадает в результат ни одним из четырёх запросов страницы
     * `/stats`, а пустая выборка даёт пустой результат, а не «весь банк».
     */
    @Test
    void topicScopedQueriesReturnOnlySelectedTopics() {
        insertWrongCard("in-1", "java", "Q?", 3, 1);      // java в сумме: 1 верных из 5 → 20%
        insertWrongCard("in-2", "java", "Q2?", 1, 0);
        insertWrongCard("out-1", "go", "Q?", 9, 1);       // 10% — слабее, но вне выборки

        List<String> selection = List.of("java");

        assertThat(repository.findTopicStatsByTopics(selection, null, null, 1_800_000_000L, 3))
                .extracting(TopicStats::topic).containsExactly("java");
        assertThat(repository.findRecentWrongQuestions(selection, null, 10))
                .extracting(QuestionStatsRepository.WrongQuestionRow::topic)
                .containsOnly("java");
        assertThat(repository.findTopicCoverageGaps(selection, 5, 10))
                .extracting(QuestionStatsRepository.TopicCoverage::topic).containsExactly("java");
        // Слабейшая в выборке — java (20%), а не глобально слабейшая go (10%).
        assertThat(repository.findWeakestTopic(selection, null, null, 70.0))
                .isEqualTo(new QuestionStatsRepository.WeakestTopic("java", 20));
        assertThat(repository.countWeakTopics(selection, null, null, 70.0)).isEqualTo(1);

        // Без ограничения по темам правило то же, но банк весь: побеждает go.
        assertThat(repository.findWeakestTopic(70.0))
                .isEqualTo(new QuestionStatsRepository.WeakestTopic("go", 10));
        assertThat(repository.countWeakTopics(70.0)).isEqualTo(2);

        // Пустая выборка — пустой срез, а не молчаливый возврат к полному банку.
        assertThat(repository.findTopicStatsByTopics(List.of(), null, null, 1_800_000_000L, 3)).isEmpty();
        assertThat(repository.findRecentWrongQuestions(List.of(), null, 10)).isEmpty();
        assertThat(repository.findTopicCoverageGaps(List.of(), 5, 10)).isEmpty();
        assertThat(repository.countWeakTopics(List.of(), null, null, 70.0)).isZero();
        assertThat(repository.findWeakestTopic(List.of(), null, null, 70.0)).isNull();
    }

    /**
     * Флажки отбора («только избранные», «только ошибки») обязаны действовать на срез по
     * темам так же, как на сводку наверху: иначе таблица считала бы по другому множеству
     * вопросов, чем число над ней.
     */
    @Test
    void topicStatsBySelectionHonourImportantAndOnlyWrongFlags() {
        long plain = insertWrongCard("plain", "java", "Q?", 0, 2);   // без ошибок, не избранный
        insertWrongCard("wrongOne", "java", "Q2?", 2, 0);            // с ошибками, не избранный
        jdbcTemplate.update("UPDATE questions SET is_important = 1 WHERE id = ?", plain);

        List<String> selection = List.of("java");

        assertThat(repository.findTopicStatsByTopics(selection, null, null, 1_800_000_000L, 3))
                .singleElement().extracting(TopicStats::total).isEqualTo(2L);
        assertThat(repository.findTopicStatsByTopics(selection, true, null, 1_800_000_000L, 3))
                .singleElement().extracting(TopicStats::total).isEqualTo(1L);
        assertThat(repository.findTopicStatsByTopics(selection, null, true, 1_800_000_000L, 3))
                .singleElement().extracting(TopicStats::total).isEqualTo(1L);
        // Оба флажка вместе не пересекаются ни на одном вопросе → честный пустой срез.
        assertThat(repository.findTopicStatsByTopics(selection, true, true, 1_800_000_000L, 3)).isEmpty();
    }

    /**
     * Слабейшая тема — общее определение для карточки на /stats и для режима «Слабые
     * темы». При равной точности выигрывает тема с бо́льшим числом попыток: там сигнал
     * надёжнее. Тай-брейк обязан быть детерминированным — иначе подпись карточки
     * менялась бы от перезагрузки, а тренировка уходила бы в другую тему.
     */
    @Test
    void findWeakestTopicPrefersLowestAccuracyThenMoreAttempts() {
        insertWrongCard("s1", "strong", "Q?", 1, 9);   // 90% — выше порога
        insertWrongCard("wa", "weak-a", "Q?", 3, 1);   // 25%, 4 попытки
        insertWrongCard("wb", "weak-b", "Q?", 6, 2);   // 25%, 8 попыток

        assertThat(repository.findWeakestTopic(70.0))
                .isEqualTo(new QuestionStatsRepository.WeakestTopic("weak-b", 25));
    }

    @Test
    void findWeakestTopicIgnoresTopicsAboveThreshold() {
        insertWrongCard("s1", "strong", "Q?", 1, 9);

        assertThat(repository.findWeakestTopic(70.0)).isNull();
        assertThat(repository.countWeakTopics(70.0)).isZero();
    }

    @Test
    void countWeakTopicsCountsOnlyTopicsBelowThreshold() {
        insertWrongCard("s1", "strong", "Q?", 1, 9);
        insertWrongCard("wa", "weak-a", "Q?", 3, 1);
        insertWrongCard("wb", "weak-b", "Q?", 6, 2);

        assertThat(repository.countWeakTopics(70.0)).isEqualTo(2);
    }

    private long insertWrongCard(String slug, String topic, String text, int wrongCount, int correctCount) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class, slug, slug, slug + ".md", topic, text, "A.", "h-" + slug);
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
                        "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, 1, 1, 2.5, 1_800_000_000L, wrongCount > 0 ? "WRONG" : "CORRECT", correctCount, wrongCount);
        return id;
    }

    private void insertCard(String slug, int repetitions, long nextReviewAt, int correctCount) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class, slug, slug, slug + ".md", "java", "Q?", "A.", "h-" + slug);
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
                        "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, repetitions, 10, 2.5, nextReviewAt, "CORRECT", correctCount, 0);
    }
}
