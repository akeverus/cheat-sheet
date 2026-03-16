package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты построения SQL-фильтров {@link InterviewFilterSql}.
 */
class InterviewFilterSqlTest {

    @Test
    void noFiltersAppendsNothing() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, null);

        assertThat(sql.toString()).isEqualTo("WHERE 1=1");
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

    @Test
    void onlyWrongFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, true);

        assertThat(sql.toString()).contains("rs.wrong_count > 0");
        assertThat(params).isEmpty();
    }

    @Test
    void allFiltersAppendedTogether() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "Spring", true, true);

        assertThat(sql.toString()).contains("q.topic = ?");
        assertThat(sql.toString()).contains("q.is_important = 1");
        assertThat(sql.toString()).contains("rs.wrong_count > 0");
        assertThat(params).containsExactly("Spring");
    }

    @Test
    void blankTopicIsIgnored() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "   ", null, null);

        assertThat(sql.toString()).isEqualTo("WHERE 1=1");
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
