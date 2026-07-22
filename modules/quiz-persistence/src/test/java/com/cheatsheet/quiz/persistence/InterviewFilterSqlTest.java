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

    @Test
    void alwaysAppendsOwnerScope() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, null);

        assertThat(sql.toString()).contains("rs.owner_key = ?");
        assertThat(params).containsExactly("local");
    }

    @Test
    void topicFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "Java", null, null);

        assertThat(sql.toString()).contains("q.topic = ?");
        assertThat(params).containsExactly("local", "Java");
    }

    @Test
    void importantFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, true, null);

        assertThat(sql.toString()).contains("q.is_important = 1");
        assertThat(params).containsExactly("local");
    }

    @Test
    void onlyWrongFilterAppendedCorrectly() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, null, true);

        assertThat(sql.toString()).contains("rs.wrong_count > 0");
        assertThat(params).containsExactly("local");
    }

    @Test
    void allFiltersAppendedTogether() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "Spring", true, true);

        assertThat(sql.toString()).contains("q.topic = ?");
        assertThat(sql.toString()).contains("q.is_important = 1");
        assertThat(sql.toString()).contains("rs.wrong_count > 0");
        assertThat(params).containsExactly("local", "Spring");
    }

    @Test
    void blankTopicIsIgnoredButOwnerRemains() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "   ", null, null);

        assertThat(sql.toString()).contains("rs.owner_key = ?");
        assertThat(sql.toString()).doesNotContain("q.topic");
        assertThat(params).containsExactly("local");
    }

    @Test
    void falseImportantAndOnlyWrongAreIgnoredButOwnerRemains() {
        StringBuilder sql = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, false, false);

        assertThat(sql.toString()).contains("rs.owner_key = ?");
        assertThat(params).containsExactly("local");
    }
}
