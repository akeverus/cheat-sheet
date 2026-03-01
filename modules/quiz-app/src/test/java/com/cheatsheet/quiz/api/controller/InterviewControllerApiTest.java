package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Интеграционные тесты для JSON-API эндпоинтов InterviewController:
 * /api/answer, /api/regenerate, /api/hint, /api/favorite,
 * /api/takeaway, /api/comparison, /api/code-trace, /api/streak, /api/confidence, /api/wrong-feedback.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InterviewControllerApiTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
        registry.add("app.regenerate-rate-limit-per-minute", () -> 1);
        registry.add("app.trust-forwarded-for-header", () -> true);
    }

    @Autowired MockMvc mockMvc;
    @Autowired QuestionRepository questionRepository;
    @Autowired AnswerOptionRepository answerOptionRepository;
    @Value("${app.admin-token}") String adminToken;

    /**
     * Возвращает первый доступный вопрос или скипает тест, если вопросов нет.
     */
    private Question firstQuestionOrSkip() {
        // Ищем вопрос через findById начиная с 1
        for (long id = 1; id <= 100; id++) {
            Optional<Question> q = questionRepository.findById(id);
            if (q.isPresent()) return q.get();
        }
        // Можно также попробовать через findAllQuestionIdsWithoutOptions(0)
        List<Long> ids = questionRepository.findAllQuestionIdsWithoutOptions(0);
        assumeTrue(!ids.isEmpty(), "Нет вопросов в тестовой БД");
        return questionRepository.findById(ids.get(0)).orElseThrow();
    }

    @Test
    void apiAnswerReturnsCorrectStructure() throws Exception {
        Question q = firstQuestionOrSkip();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q.id());
        assumeTrue(!options.isEmpty(), "Нет вариантов ответа для вопроса " + q.id());

        AnswerOption selected = options.get(0);

        MvcResult result = mockMvc.perform(post("/api/answer")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", String.valueOf(selected.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").isBoolean())
                .andExpect(jsonPath("$.correctOptionId").isNumber())
                .andExpect(jsonPath("$.selectedOptionId").value(selected.id()))
                .andExpect(jsonPath("$.answerHtml").isString())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("correct");
    }

    @Test
    void apiRegenerateReturnsSuccess() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/regenerate")
                        .header(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, adminToken)
                        .header("X-Forwarded-For", "10.10.10.1")
                        .param("questionId", String.valueOf(q.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.questionId").value(q.id()))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void apiRegenerateWithoutTokenReturnsForbidden() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/regenerate")
                        .param("questionId", String.valueOf(q.id())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }

    @Test
    void apiRegenerateReturnsRateLimitWithRetryAfterHeader() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/regenerate")
                        .header(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, adminToken)
                        .header("X-Forwarded-For", "10.10.10.2")
                        .param("questionId", String.valueOf(q.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(post("/api/regenerate")
                        .header(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, adminToken)
                        .header("X-Forwarded-For", "10.10.10.2")
                        .param("questionId", String.valueOf(q.id())))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.type").value("RATE_LIMIT_EXCEEDED"))
                .andExpect(jsonPath("$.message").value("Слишком много запросов к /api/regenerate, повторите позже"))
                .andExpect(jsonPath("$.details.retryAfterSeconds").value(60))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Retry-After", "60"));
    }

    @Test
    void apiRegenerateWithNonExistentQuestionReturns404() throws Exception {
        mockMvc.perform(post("/api/regenerate")
                        .header(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, adminToken)
                        .header("X-Forwarded-For", "10.10.10.3")
                        .param("questionId", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("QUESTION_NOT_FOUND"));
    }

    @Test
    void apiHintReturnsHintOrNotFound() throws Exception {
        Question q = firstQuestionOrSkip();

        MvcResult result = mockMvc.perform(post("/api/hint")
                        .param("questionId", String.valueOf(q.id()))
                        .param("level", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isIn(200, 503);
        String body = result.getResponse().getContentAsString();
        if (result.getResponse().getStatus() == 200) {
            assertThat(body).contains("hint");
            assertThat(body).contains("level");
            assertThat(body).contains("maxLevel");
        } else {
            assertThat(body).contains("AI_GENERATION_FAILED");
        }
    }

    @Test
    void apiHintWithNonExistentQuestionReturns404() throws Exception {
        mockMvc.perform(post("/api/hint")
                        .param("questionId", "999999")
                        .param("level", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("QUESTION_NOT_FOUND"));
    }

    @Test
    void apiHintWithInvalidLevelReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/hint")
                        .param("questionId", String.valueOf(q.id()))
                        .param("level", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiFavoriteTogglesState() throws Exception {
        Question q = firstQuestionOrSkip();

        MvcResult result = mockMvc.perform(post("/api/favorite")
                        .param("questionId", String.valueOf(q.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").isBoolean())
                .andExpect(jsonPath("$.questionId").value(q.id()))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("favorite");
    }

    @Test
    void apiFavoriteWithNonExistentQuestionReturns404() throws Exception {
        mockMvc.perform(post("/api/favorite")
                        .param("questionId", "999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiTakeawayReturns200WithTakeawayField() throws Exception {
        Question q = firstQuestionOrSkip();
        MvcResult result = mockMvc.perform(get("/api/takeaway").param("questionId", String.valueOf(q.id())))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("takeaway");
    }

    @Test
    void apiTakeawayWithInvalidQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/takeaway")
                        .param("questionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiTakeawayWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/takeaway")
                        .param("questionId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiComparisonReturns200WithComparisonField() throws Exception {
        Question q = firstQuestionOrSkip();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q.id());
        assumeTrue(!options.isEmpty(), "Нет вариантов ответа для вопроса " + q.id());
        MvcResult result = mockMvc.perform(get("/api/comparison")
                        .param("questionId", String.valueOf(q.id()))
                        .param("selectedOptionId", String.valueOf(options.get(0).id())))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("comparison");
    }

    @Test
    void apiComparisonWithInvalidSelectedOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(get("/api/comparison")
                        .param("questionId", String.valueOf(q.id()))
                        .param("selectedOptionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiComparisonWithNonNumericSelectedOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(get("/api/comparison")
                        .param("questionId", String.valueOf(q.id()))
                        .param("selectedOptionId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiComparisonWithInvalidQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/comparison")
                        .param("questionId", "0")
                        .param("selectedOptionId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiComparisonWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/comparison")
                        .param("questionId", "abc")
                        .param("selectedOptionId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiCodeTraceReturns200WithTraceField() throws Exception {
        Question q = firstQuestionOrSkip();
        MvcResult result = mockMvc.perform(get("/api/code-trace").param("questionId", String.valueOf(q.id())))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("trace");
    }

    @Test
    void apiCodeTraceWithInvalidQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/code-trace")
                        .param("questionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiCodeTraceWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/code-trace")
                        .param("questionId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiStreakReturns200WithStreakData() throws Exception {
        mockMvc.perform(get("/api/streak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streak").exists())
                .andExpect(jsonPath("$.today").exists())
                .andExpect(jsonPath("$.goal").exists())
                .andExpect(jsonPath("$.goalReached").exists())
                .andExpect(jsonPath("$.correct").exists());
    }

    @Test
    void apiStatsReturnsAggregatedCounters() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.due").isNumber())
                .andExpect(jsonPath("$.learned").isNumber())
                .andExpect(jsonPath("$.correct").isNumber())
                .andExpect(jsonPath("$.wrong").isNumber());
    }

    @Test
    void apiTopicStatsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/topic-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void apiTopicStatsWhenNotEmptyContainsExpectedFields() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/topic-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        if (!"[]".equals(body.trim())) {
            assertThat(body).contains("\"topic\"");
            assertThat(body).contains("\"total\"");
            assertThat(body).contains("\"due\"");
            assertThat(body).contains("\"learned\"");
            assertThat(body).contains("\"correct\"");
            assertThat(body).contains("\"wrong\"");
            assertThat(body).contains("\"regenSum\"");
        }
    }

    @Test
    void apiNextReturnsQuestionOrNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/next"))
                .andReturn();
        int status = result.getResponse().getStatus();
        assertThat(status).isIn(200, 204, 503);
        if (status == 200) {
            String body = result.getResponse().getContentAsString();
            assertThat(body).contains("questionId");
            assertThat(body).contains("questionText");
            assertThat(body).contains("options");
        } else if (status == 503) {
            String body = result.getResponse().getContentAsString();
            assertThat(body).contains("AI_GENERATION_FAILED");
        }
    }

    @Test
    void apiNextWithInvalidExcludeQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/next")
                        .param("excludeQuestionId", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiConfidencePostReturns200() throws Exception {
        Question q = firstQuestionOrSkip();
        mockMvc.perform(post("/api/confidence")
                        .param("questionId", String.valueOf(q.id()))
                        .param("grade", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.questionId").value(q.id()))
                .andExpect(jsonPath("$.grade").value(5));
    }

    @Test
    void apiConfidenceWithInvalidGradeReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/confidence")
                        .param("questionId", String.valueOf(q.id()))
                        .param("grade", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiConfidenceWithNonNumericGradeReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/confidence")
                        .param("questionId", String.valueOf(q.id()))
                        .param("grade", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiConfidenceWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/confidence")
                        .param("questionId", "abc")
                        .param("grade", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiConfidenceWithInvalidQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/confidence")
                        .param("questionId", "0")
                        .param("grade", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiWrongFeedbackReturns200() throws Exception {
        Question q = firstQuestionOrSkip();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q.id());
        assumeTrue(!options.isEmpty(), "Нет вариантов ответа для вопроса " + q.id());
        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", String.valueOf(options.get(0).id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(q.id()))
                .andExpect(jsonPath("$.optionId").value(options.get(0).id()))
                .andExpect(jsonPath("$.available").isBoolean());
    }

    @Test
    void apiWrongFeedbackWithInvalidOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiWrongFeedbackWithNonNumericOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiWrongFeedbackWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", "abc")
                        .param("optionId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiWrongFeedbackWithNonExistentQuestionReturns404() throws Exception {
        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", "999999")
                        .param("optionId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("QUESTION_NOT_FOUND"));
    }

    @Test
    void apiWrongFeedbackWithNonExistentOptionReturnsUnavailable() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/wrong-feedback")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(q.id()))
                .andExpect(jsonPath("$.optionId").value(999999))
                .andExpect(jsonPath("$.feedback").isEmpty())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void apiAnswerWithInvalidQuestionIdReturns404() throws Exception {
        mockMvc.perform(post("/api/answer")
                        .param("questionId", "999999")
                        .param("optionId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("QUESTION_NOT_FOUND"));
    }

    @Test
    void apiAnswerWithInvalidOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/answer")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void apiAnswerIncludesSessionCountersWhenSessionExists() throws Exception {
        Question q = firstQuestionOrSkip();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(q.id());
        assumeTrue(!options.isEmpty(), "Нет вариантов ответа для вопроса " + q.id());
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/start")
                        .session(session)
                        .param("mode", "TRAINING")
                        .param("count", "20"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/api/answer")
                        .session(session)
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", String.valueOf(options.get(0).id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session").exists())
                .andExpect(jsonPath("$.session.index").isNumber())
                .andExpect(jsonPath("$.session.total").isNumber())
                .andExpect(jsonPath("$.session.correct").isNumber())
                .andExpect(jsonPath("$.session.wrong").isNumber())
                .andExpect(jsonPath("$.session.finished").isBoolean());
    }
}
