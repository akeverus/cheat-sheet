package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExportIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${app.admin-token}")
    private String adminToken;

    private HttpEntity<Void> authorizedRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, adminToken);
        return new HttpEntity<>(headers);
    }

    @Test
    void exportJsonReturnsOkAndArray() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/export?format=json", HttpMethod.GET, authorizedRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).startsWith("[");
        assertThat(response.getBody()).endsWith("]");
    }

    @Test
    void exportCsvReturnsOkAndHeader() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/export?format=csv", HttpMethod.GET, authorizedRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("slug,topic,correct_count,wrong_count,next_review_at,repetitions");
    }

    @Test
    void exportUnknownFormatReturns400() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/export?format=html", HttpMethod.GET, authorizedRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(ApiErrorTypes.UNSUPPORTED_FORMAT);
        assertThat(response.getBody()).contains("details");
    }
}
