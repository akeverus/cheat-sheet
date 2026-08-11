package com.cheatsheet.quiz.common.exception;

import com.cheatsheet.quiz.common.web.CorrelationIdFilter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * Дополняет модель контейнерного error-dispatch (Spring
 * {@code BasicErrorController} → {@code error.html}) двумя полями, которых нет
 * в стандартном наборе, но без которых страница ошибки бесполезна как
 * отчёт:
 * <ul>
 *   <li>{@code correlationId} — тот же идентификатор, что уже уходит в лог
 *       ({@link CorrelationIdFilter}) и в {@code ProblemDetail} API-ответов.
 *       Без него пользовательская жалоба («упало») не соединяется с лог-строкой;</li>
 *   <li>{@code errorTime} — человекочитаемое время вместо сырого
 *       {@code Date.toString()} («Fri Aug 07 21:29:41 MSK 2026»).</li>
 * </ul>
 *
 * <p>Стандартный {@code timestamp} намеренно оставлен нетронутым: тот же
 * набор атрибутов отдаётся как JSON-тело {@code /error}, и подмена типа
 * сломала бы его форму. Новое поле — добавление, а не замена.</p>
 *
 * <p>Второй путь доставки (ошибки, перехваченные {@code GlobalExceptionHandler})
 * заполняет те же ключи вручную — см. {@code GlobalExceptionHandler#errorView}.
 * Оба пути обязаны давать один набор, иначе одна и та же страница выглядит
 * по-разному на 403 и на 500.</p>
 */
@Component
public class ErrorViewAttributes extends DefaultErrorAttributes {

    static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, options);
        Object correlationId = webRequest.getAttribute(
                CorrelationIdFilter.REQUEST_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (correlationId != null) {
            attributes.put("correlationId", correlationId.toString());
        }
        attributes.put("errorTime", formatTimestamp(attributes.get("timestamp")));
        return attributes;
    }

    /** {@code timestamp} приходит как {@link Date}; на всякий случай терпим и {@link Instant}. */
    private static String formatTimestamp(Object timestamp) {
        Instant instant;
        if (timestamp instanceof Date date) {
            instant = date.toInstant();
        } else if (timestamp instanceof Instant value) {
            instant = value;
        } else {
            instant = Instant.now();
        }
        return TIME_FORMAT.withZone(ZoneId.systemDefault()).format(instant);
    }
}
