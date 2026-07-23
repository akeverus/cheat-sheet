package com.cheatsheet.quiz.common.web;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link CorrelationIdFilter} — фильтра на {@code HIGHEST_PRECEDENCE},
 * присваивающего correlation-id каждому запросу. Покрывают все ветки {@code resolve}:
 * эхо валидного заголовка (с trim), генерация UUID для null/blank и защитный cap
 * длины (>128 → UUID, а не отражение произвольно длинного значения в лог/заголовок),
 * а также очистку MDC после запроса.
 */
class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    private MockHttpServletResponse run(String incomingHeader) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (incomingHeader != null) {
            request.addHeader(CorrelationIdFilter.HEADER, incomingHeader);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        FilterChain chain = (req, res) -> chainCalled.set(true);

        filter.doFilterInternal(request, response, chain);

        assertThat(chainCalled).as("цепочка фильтров должна быть вызвана").isTrue();
        // Значение в атрибуте запроса и в заголовке ответа — всегда одно и то же.
        assertThat(request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE))
                .isEqualTo(response.getHeader(CorrelationIdFilter.HEADER));
        return response;
    }

    private static boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Test
    void echoesValidIncomingHeader() throws Exception {
        MockHttpServletResponse response = run("client-trace-42");
        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("client-trace-42");
    }

    @Test
    void stripsSurroundingWhitespaceFromIncomingHeader() throws Exception {
        MockHttpServletResponse response = run("  trace-77  ");
        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("trace-77");
    }

    @Test
    void generatesUuidWhenHeaderMissing() throws Exception {
        MockHttpServletResponse response = run(null);
        assertThat(isUuid(response.getHeader(CorrelationIdFilter.HEADER))).isTrue();
    }

    @Test
    void generatesUuidWhenHeaderBlank() throws Exception {
        MockHttpServletResponse response = run("   ");
        assertThat(isUuid(response.getHeader(CorrelationIdFilter.HEADER))).isTrue();
    }

    @Test
    void generatesUuidWhenHeaderExceeds128Chars() throws Exception {
        String overlong = "x".repeat(129);
        String value = run(overlong).getHeader(CorrelationIdFilter.HEADER);
        // Слишком длинный входящий id НЕ отражается, заменяется на UUID.
        assertThat(value).isNotEqualTo(overlong);
        assertThat(isUuid(value)).isTrue();
    }

    @Test
    void echoesHeaderAtExactly128CharBoundary() throws Exception {
        String maxLen = "y".repeat(128);
        assertThat(run(maxLen).getHeader(CorrelationIdFilter.HEADER)).isEqualTo(maxLen);
    }

    @Test
    void removesMdcKeyAfterRequestCompletes() throws Exception {
        run("trace-mdc");
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }
}
