package com.cheatsheet.quiz.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Присваивает каждому запросу correlation-id (RFC-совместимая диагностика,
 * хендофф-3 Этап 9). Значение кладётся в:
 * <ul>
 *   <li>MDC (ключ {@link #MDC_KEY}) — попадает в структурные логи;</li>
 *   <li>атрибут запроса {@link #REQUEST_ATTRIBUTE} — читается
 *       {@code GlobalExceptionHandler} для {@code correlationId} в ProblemDetail;</li>
 *   <li>заголовок ответа {@link #HEADER} — клиент может показать его в отчёте об ошибке.</li>
 * </ul>
 * Входящий {@code X-Correlation-Id} уважается (сквозная трассировка), иначе
 * генерируется новый UUID.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";
    public static final String REQUEST_ATTRIBUTE = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolve(request.getHeader(HEADER));
        MDC.put(MDC_KEY, correlationId);
        request.setAttribute(REQUEST_ATTRIBUTE, correlationId);
        response.setHeader(HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private String resolve(String incoming) {
        if (incoming != null && !incoming.isBlank() && incoming.length() <= 128) {
            return incoming.strip();
        }
        return UUID.randomUUID().toString();
    }
}
