package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewFilterSqlTest {

    @Test
    void appendFiltersAddsTopicImportantAndOnlyWrongConditions() {
        StringBuilder sql = new StringBuilder("SELECT q.id FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, "java", true, true);

        assertThat(sql.toString()).contains("AND q.topic = ?");
        assertThat(sql.toString()).contains("AND q.is_important = 1");
        assertThat(sql.toString()).contains("AND rs.wrong_count > 0");
        assertThat(params).containsExactly("java");
    }

    @Test
    void appendFiltersSkipsOptionalConditionsWhenFlagsAreFalse() {
        StringBuilder sql = new StringBuilder("SELECT q.id FROM questions q JOIN review_state rs ON rs.question_id = q.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        InterviewFilterSql.appendFilters(sql, params, null, false, false);

        assertThat(sql.toString()).doesNotContain("q.topic = ?");
        assertThat(sql.toString()).doesNotContain("q.is_important = 1");
        assertThat(sql.toString()).doesNotContain("rs.wrong_count > 0");
        assertThat(params).isEmpty();
    }
}
