package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///mcq_block_runtime")
class McqBlockRuntimeIntegrationTest {
    @DynamicPropertySource
    static void interviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired TestRestTemplate http;
    @Autowired QuestionRepository questions;
    @Autowired AnswerOptionRepository options;
    @Autowired ReviewStateRepository reviews;
    @Autowired OptionCache cache;
    @Autowired InterviewService interview;
    @Autowired PauseService pause;

    long questionId;

    @BeforeEach
    void prepareTwoBlocksInTheIsolatedDatabase() {
        questionId = questions.findIdByTopicAndQuestionNumber("programming/java-strings", 1).orElseThrow();
        options.deleteByQuestionId(questionId);
        options.insertAll(questionId, IntStream.range(0, 8).mapToObj(i -> {
            int block = i / 4;
            int order = i % 4;
            return new AnswerOptionRepository.AnswerOptionCreate(
                    "Block " + block + " option " + order,
                    block == 0 ? order == 0 : order == 2, order, "MARKDOWN",
                    "Explanation for block " + block, 1, 1, block);
        }).toList());
        cache.invalidate(questionId);
        reviews.update(new ReviewState(questionId, 0, 1, 2.3, 0L, ReviewResult.WRONG, 0, 1));
    }

    @Test
    void apiAndMvcShowOnlyFourOptionsFromTheSameBlock() {
        ResponseEntity<Map> api = http.getForEntity(
                "/api/next?topic=programming/java-strings&onlyWrong=true&ordered=true", Map.class);
        assertThat(api.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Number) api.getBody().get("questionId")).longValue()).isEqualTo(questionId);
        assertThat((List<?>) api.getBody().get("options")).hasSize(4);

        var page = http.getForEntity("/practice?questionId=" + questionId, String.class);
        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
        var displayed = Jsoup.parse(page.getBody()).select("input[name=optionId]").eachAttr("value");
        assertThat(displayed).containsExactlyElementsOf(blockOne().stream()
                .map(option -> Long.toString(option.id())).toList());
    }

    @Test
    void httpAnswerAndIdempotentReplayKeepTheSubmittedBlock() {
        var page = http.getForEntity("/practice?questionId=" + questionId, String.class);
        List<String> cookies = page.getHeaders().getOrEmpty(HttpHeaders.SET_COOKIE).stream()
                .map(cookie -> cookie.split(";", 2)[0]).toList();
        String csrf = cookies.stream().filter(cookie -> cookie.startsWith("XSRF-TOKEN="))
                .findFirst().orElseThrow().substring("XSRF-TOKEN=".length());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-XSRF-TOKEN", URLDecoder.decode(csrf, StandardCharsets.UTF_8));
        headers.set(HttpHeaders.COOKIE, String.join("; ", cookies));
        AnswerOption selected = blockOne().get(0);
        long correctId = blockOne().stream().filter(AnswerOption::correct).findFirst().orElseThrow().id();
        var request = new HttpEntity<>(Map.of("questionId", questionId, "optionId", selected.id(),
                "clientAttemptId", UUID.randomUUID().toString()), headers);

        var first = http.postForEntity("/api/answer", request, Map.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(first.getBody().get("correct")).isEqualTo(false);
        assertThat(((Number) first.getBody().get("correctOptionId")).longValue()).isEqualTo(correctId);
        assertThat((List<?>) first.getBody().get("optionExplanations")).hasSize(4);
        ReviewState applied = reviews.findByQuestionId(questionId).orElseThrow();

        var replay = http.postForEntity("/api/answer", request, Map.class);
        assertThat(replay.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(replay.getBody().get("correctOptionId")).isEqualTo(first.getBody().get("correctOptionId"));
        assertThat((List<?>) replay.getBody().get("optionExplanations")).hasSize(4);
        assertThat(reviews.findByQuestionId(questionId)).contains(applied);
    }

    @Test
    void unknownKeepsTheShownBlockAfterTheReviewCounterChanges() {
        InterviewSession session = new InterviewSession(InterviewMode.EXAM,
                List.of(questionId), "programming/java-strings", null, null, false);
        var shown = interview.questionForSession(session).orElseThrow();
        assertThat(shown.options()).allMatch(option -> option.mcqBlockIdx() == 1);

        interview.submitUnknown(questionId);
        session.registerUnknown(questionId, "programming/java-strings");
        var result = interview.evaluateUnknown(questionId, session.mcqBlockIdxForQuestion(questionId)).orElseThrow();

        assertThat(result.options()).hasSize(4).allMatch(option -> option.mcqBlockIdx() == 1);
        assertThat(result.correct().mcqBlockIdx()).isEqualTo(1);
        assertThat(result.updatedState().wrongCount()).isEqualTo(2);
    }

    @Test
    void pauseAndResumeKeepTheBlockDespiteAnotherAnswer() {
        InterviewSession session = new InterviewSession(InterviewMode.EXAM,
                List.of(questionId), "programming/java-strings", null, null, false);
        interview.questionForSession(session).orElseThrow();
        pause.pause(session);
        interview.submitAnswer(questionId, blockOne().get(0).id(), null);

        InterviewSession resumed = pause.resume().orElseThrow();
        var shown = interview.questionForSession(resumed).orElseThrow();

        assertThat(shown.options()).hasSize(4).allMatch(option -> option.mcqBlockIdx() == 1);
    }

    private List<AnswerOption> blockOne() {
        return options.findByQuestionId(questionId).stream().filter(option -> option.mcqBlockIdx() == 1).toList();
    }
}
