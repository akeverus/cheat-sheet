package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сторожит сжатие статики на реальном Tomcat.
 *
 * <p>Проверять конфиг чтением списка mime-типов бессмысленно: список был
 * «правильным» и при этом не сжимал ни одного скрипта. Tomcat отдаёт {@code .js}
 * с типом {@code text/javascript} (RFC 9239 сделал его актуальным, а
 * {@code application/javascript} — устаревшим синонимом), сверка идёт по строке,
 * и одного отсутствующего синонима хватило, чтобы 3.2 МБ JS уходили по сети
 * несжатыми. Поэтому тест ходит по HTTP и смотрит на фактический ответ.</p>
 *
 * <p>{@link HttpClient} здесь принципиален: {@code TestRestTemplate} на
 * {@code HttpURLConnection} сам добавляет {@code Accept-Encoding} и прозрачно
 * распаковывает ответ, пряча ровно тот заголовок, ради которого тест написан.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StaticAssetCompressionTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @LocalServerPort
    private int port;

    /** Скрипт заведомо крупнее {@code min-response-size}, иначе сжатие не сработает по размеру. */
    @Test
    void compressesJavaScript() throws IOException, InterruptedException {
        HttpResponse<byte[]> response = get("/js/app-entry.js");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type"))
                .hasValueSatisfying(type -> assertThat(type).contains("javascript"));
        assertThat(response.headers().firstValue("Content-Encoding")).hasValue("gzip");
    }

    @Test
    void compressesStylesheets() throws IOException, InterruptedException {
        HttpResponse<byte[]> response = get("/css/shell.css");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Encoding")).hasValue("gzip");
    }

    private HttpResponse<byte[]> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Accept-Encoding", "gzip")
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
