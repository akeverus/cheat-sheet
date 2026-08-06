package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты построения SQL-фильтров {@link InterviewFilterSql}.
 */
class InterviewFilterSqlTest {

    @AfterEach
    void clearOwner() {
        OwnerContext.clear();
    }

    /**
     * Скоуп владельца живёт в {@code ON}, а не в {@code WHERE}.
     *
     * <p>Это не стилистика. Условие {@code rs.owner_key = ?} в {@code WHERE}
     * отбрасывает строки, где вся правая часть LEFT JOIN — NULL, то есть молча
     * превращает внешнее соединение обратно во внутреннее. Вопросы без прогресса
     * снова стали бы невидимыми — тем же дефектом, только незаметнее.
     */
    @Test
    void ownerScopeLivesInJoinCondition() {
        StringBuilder sql = new StringBuilder("SELECT q.id FROM questions q ");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendReviewStateJoin(sql, params);

        assertThat(sql.toString())
                .contains("LEFT JOIN review_state rs")
                .contains("ON rs.question_id = q.id AND rs.owner_key = ?");
        assertThat(params).containsExactly("local");
    }

    /** Фильтры не трогают владельца: его добавляет только JOIN. */
    @Test
    void filtersDoNotAppendOwnerScope() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, null);

        assertThat(sql.toString()).doesNotContain("owner_key");
        assertThat(params).isEmpty();
    }

    @Test
    void topicFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "Java", null, null);

        assertThat(sql.toString()).contains("q.topic = ?");
        assertThat(params).containsExactly("Java");
    }

    @Test
    void importantFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, true, null);

        assertThat(sql.toString()).contains("q.is_important = 1");
        assertThat(params).isEmpty();
    }

    /**
     * «Только с ошибками» считает отсутствие прогресса нулём ошибок.
     *
     * <p>Без {@code COALESCE} сравнение {@code NULL > 0} даёт UNKNOWN, и вопрос без
     * прогресса выпал бы из выборки — с виду правильно, но по случайной причине:
     * та же логика в {@code CASE} или под {@code NOT} дала бы обратный результат.
     */
    @Test
    void onlyWrongFilterTreatsMissingProgressAsZero() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, true);

        assertThat(sql.toString()).contains("COALESCE(rs.wrong_count, 0) > 0");
        assertThat(params).isEmpty();
    }

    @Test
    void allFiltersAppendedTogether() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "Spring", true, true);

        assertThat(sql.toString())
                .contains("q.topic = ?")
                .contains("q.is_important = 1")
                .contains("COALESCE(rs.wrong_count, 0) > 0");
        assertThat(params).containsExactly("Spring");
    }

    @Test
    void blankTopicIsIgnored() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "   ", null, null);

        assertThat(sql.toString()).doesNotContain("q.topic");
        assertThat(params).isEmpty();
    }

    @Test
    void falseImportantAndOnlyWrongAreIgnored() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, false, false);

        assertThat(sql.toString()).isEqualTo("WHERE 1=1");
        assertThat(params).isEmpty();
    }
}
