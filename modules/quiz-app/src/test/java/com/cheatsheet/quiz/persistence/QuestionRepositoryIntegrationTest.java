package com.cheatsheet.quiz.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Интеграционные тесты QuestionRepository, в т.ч. null-safety для questionType.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class QuestionRepositoryIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        com.cheatsheet.quiz.TestInterviewPath.register(registry);
    }

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Long insertedQuestionId;

    @AfterEach
    void cleanup() {
        if (insertedQuestionId != null) {
            jdbcTemplate.update("DELETE FROM questions WHERE id = ?", insertedQuestionId);
            insertedQuestionId = null;
        }
    }

    @Test
    void insertWithNullQuestionTypeStoresAsText() {
        String uniqueSlug = "zzz-null-questiontype-test#" + System.currentTimeMillis();
        Question question = new Question(
                0L, uniqueSlug, uniqueSlug, "test.md", "topic",
                "Question?", "Answer", false, "hash",
                null,  // questionType = null
                null, null, 0, null
        );
        long id = questionRepository.insert(question);
        insertedQuestionId = id;
        assertThat(id).isPositive();

        String stored = jdbcTemplate.queryForObject(
                "SELECT question_type FROM questions WHERE id = ?",
                String.class, id);
        assertThat(stored).isEqualTo("TEXT");
    }
}
