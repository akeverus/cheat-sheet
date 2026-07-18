package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
 * /api/answer, /api/favorite, /api/streak, /api/confidence, /api/stats, /api/topic-stats.
 *
 * <p>AIR-cleanup: тесты /api/takeaway, /api/comparison, /api/code-trace,
 * /api/wrong-feedback удалены — эти AI-разбор эндпоинты вырезаны вместе с
 * провайдерами (OpenAI/DeepSeek), маппингов в InterviewApiController больше нет,
 * а запросы к ним отдавали 404 → тесты были мёртвыми.
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

    /**
     * Возвращает первый доступный вопрос или скипает тест, если вопросов нет.
     */
    private Question firstQuestionOrSkip() {
        // Ищем вопрос через findById начиная с 1
        for (long id = 1; id <= 100; id++) {
            Optional<Question> q = questionRepository.findById(id);
            if (q.isPresent()) return q.get();
        }
        // Можно также попробовать через findAllQuestionIdsWithoutOptions()
        List<Long> ids = questionRepository.findAllQuestionIdsWithoutOptions();
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
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
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
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void apiConfidenceWithNonNumericGradeReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/confidence")
                        .param("questionId", String.valueOf(q.id()))
                        .param("grade", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void apiConfidenceWithNonNumericQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/confidence")
                        .param("questionId", "abc")
                        .param("grade", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void apiConfidenceWithInvalidQuestionIdReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/confidence")
                        .param("questionId", "0")
                        .param("grade", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void apiAnswerWithInvalidQuestionIdReturns404() throws Exception {
        mockMvc.perform(post("/api/answer")
                        .param("questionId", "999999")
                        .param("optionId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("QUESTION_NOT_FOUND"));
    }

    @Test
    void apiAnswerWithInvalidOptionIdReturnsValidationError() throws Exception {
        Question q = firstQuestionOrSkip();

        mockMvc.perform(post("/api/answer")
                        .param("questionId", String.valueOf(q.id()))
                        .param("optionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.errors").isArray());
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
