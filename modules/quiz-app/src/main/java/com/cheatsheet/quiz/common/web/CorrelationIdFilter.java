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
import java.util.regex.Pattern;

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
 *
 * <p>Значение клиента <b>валидируется по allowlist</b> {@link #SAFE_ID}
 * (token-safe: буквы/цифры/{@code . _ -}, 1–128 символов) прежде чем попасть
 * в MDC/лог/заголовок. Иначе управляемый атакующим заголовок мог бы пронести
 * управляющие байты (ESC, {@code \b} и т.п.) в audit-логи и вьюверы —
 * подделка/спуфинг лог-строк (CWE-117). Не прошедшее проверку значение
 * заменяется сгенерированным UUID.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";
    public static final String REQUEST_ATTRIBUTE = "correlationId";

    /** Token-safe allowlist: покрывает UUID (hex+дефисы) и типовые trace-id. */
    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9._-]{1,128}");

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
        if (incoming != null) {
            String candidate = incoming.strip();
            // Allowlist субсумирует прежние проверки: пустое → нет матча,
            // длина > 128 → нет матча, управляющие/спец-символы → нет матча.
            if (SAFE_ID.matcher(candidate).matches()) {
                return candidate;
            }
        }
        return UUID.randomUUID().toString();
    }
}
