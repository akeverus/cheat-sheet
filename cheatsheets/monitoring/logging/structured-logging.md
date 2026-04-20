---
title: "Структурированное логирование для Java"
description: "Комплексное руководство по структурированному логированию в Java: JSON logging, correlation IDs, distributed tracing, log aggregation и лучшие практики для микросервисных архитектур."
tags:
  - monitoring
  - logging
  - structured-logging
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Структурированное логирование для Java

Комплексное руководство по структурированному логированию в Java: JSON logging, correlation IDs, distributed tracing, log aggregation и лучшие практики для микросервисных архитектур.

## Полезные ссылки

### Спецификации и стандарты
- [Elastic Common Schema](https://www.elastic.co/guide/en/ecs/current/index.html)
- [OpenTelemetry Logging](https://opentelemetry.io/docs/specs/otel/logs/)
- [RFC 5424 — Syslog Protocol](https://tools.ietf.org/html/rfc5424)

### Библиотеки
- [Logstash Logback Encoder](https://github.com/logfellow/logstash-logback-encoder)
- [Jackson Structured Logging](https://github.com/FasterXML/jackson)
- [SLF4J Structured Arguments](https://www.slf4j.org/manual.html#structured-data)

### См. также
- [[logging-basics|Основы логирования]]
- [[centralized-logging|Централизованное логирование]]
- [[java-spring-cloud-sleuth|Spring Cloud Sleuth]]

## Содержание

- [Введение в структурированное логирование](#введение-в-структурированное-логирование)
- [JSON logging](#json-logging)
  - [Logback (Logstash Logback Encoder)](#logback-logstash-logback-encoder)
  - [Log4j 2 JSON](#log4j-2-json)
  - [Structured logging в коде (SLF4J)](#structured-logging-в-коде-slf4j)
- [Correlation IDs](#correlation-ids)
  - [Spring MVC interceptor](#spring-mvc-interceptor)
  - [Прокидывание в исходящие вызовы (RestTemplate, WebClient)](#прокидывание-в-исходящие-вызовы-resttemplate-webclient)
  - [Асинхронные задачи (MDC в новом потоке)](#асинхронные-задачи-mdc-в-новом-потоке)
- [Distributed tracing и context propagation](#distributed-tracing-и-context-propagation)
- [Log levels и custom fields](#log-levels-и-custom-fields)
- [Performance и log aggregation](#performance-и-log-aggregation)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в структурированное логирование

Структурированное логирование — подход, при котором лог-сообщения содержат структурированные данные (JSON, key-value), а не только текст. Это даёт эффективный поиск, агрегацию и корреляцию событий в распределённых системах.

Преимущества: поиск по полям, агрегация, корреляция по correlation ID, автоматическая обработка, единый формат, масштабируемость, наблюдаемость.

| Аспект | Традиционное | Структурированное |
|--------|--------------|-------------------|
| Формат | `User 123 logged in` | `{"event":"login","userId":123,"timestamp":"..."}` |
| Поиск | Текстовый поиск | Запросы по полям |
| Корреляция | Вручную | Встроенная (correlation ID) |

## JSON logging

### Logback (Logstash Logback Encoder)

```xml
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp><fieldName>@timestamp</fieldName><pattern>yyyy-MM-dd'T'HH:mm:ss.SSSZ</pattern></timestamp>
                <logLevel><fieldName>level</fieldName></logLevel>
                <loggerName><fieldName>logger</fieldName></loggerName>
                <message/>
                <mdc/>
                <stackTrace><fieldName>stack_trace</fieldName></stackTrace>
                <threadName><fieldName>thread</fieldName></threadName>
            </providers>
        </encoder>
    </appender>
    <root level="INFO"><appender-ref ref="JSON"/></root>
</configuration>
```

### Log4j 2 JSON

```xml
<Configuration>
    <Appenders>
        <Console name="JSON" target="SYSTEM_OUT">
            <JsonLayout compact="false" eventEol="true">
                <KeyValuePair key="appName" value="MyApplication"/>
            </JsonLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="INFO"><AppenderRef ref="JSON"/></Root>
    </Loggers>
</Configuration>
```

### Structured logging в коде (SLF4J)

```java
// Key-value пары вместо плейсхолдеров
        logger.info("User action performed",
                   KeyValuePair.of("userId", userId),
                   KeyValuePair.of("action", action),
                   KeyValuePair.of("timestamp", System.currentTimeMillis()));

// Бизнес-событие как карта
        Map<String, Object> event = Map.of(
    "eventType", eventType, "data", data,
    "timestamp", Instant.now().toString(), "service", "user-service");
        logger.info("Business event: {}", event);
```

## Correlation IDs

Используйте один идентификатор на запрос и прокидывайте его через заголовки и MDC.

### Spring MVC interceptor

```java
@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty())
            correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        request.setAttribute(CORRELATION_ID_KEY, correlationId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
```

### Прокидывание в исходящие вызовы (RestTemplate, WebClient)

```java
// RestTemplate
            restTemplate.getInterceptors().add((request, body, execution) -> {
    String id = MDC.get("correlationId");
    if (id != null) request.getHeaders().add("X-Correlation-ID", id);
                return execution.execute(request, body);
            });

// WebClient
WebClient.builder().filter((request, next) -> {
    String id = MDC.get("correlationId");
    return id != null ? next.exchange(ClientRequest.from(request).header("X-Correlation-ID", id).build()) : next.exchange(request);
}).build();
```

### Асинхронные задачи (MDC в новом потоке)

Копируйте контекст до запуска и восстанавливайте в потоке:

```java
executor.setTaskDecorator(runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
            if (contextMap != null) MDC.setContextMap(contextMap);
                    runnable.run();
        } finally { MDC.clear(); }
    };
});
```

## Distributed tracing и context propagation

С OpenTelemetry добавьте traceId/spanId в MDC для связи логов и трейсов:

```java
// В span scope
            MDC.put("traceId", span.getSpanContext().getTraceId());
            MDC.put("spanId", span.getSpanContext().getSpanId());
            logger.info("Processing request", KeyValuePair.of("operation", "request_processing"));
// ... business logic ...
MDC.remove("traceId"); MDC.remove("spanId");
```

В Kafka прокидывайте correlation ID в заголовках сообщения при отправке и восстанавливайте в consumer при обработке.

## Log levels и custom fields

- Используйте семантические уровни: отдельные логгеры для business/technical/audit при необходимости.
- В production не логируйте большие объекты целиком: только id, размер, тип; детали — на TRACE/DEBUG и по флагу (например, `detailed.logging`).
- Добавляйте в MDC глобальный контекст: application name, version, environment, hostname при старте приложения и request-scoped данные (method, URI, clientIp) в фильтре/интерцепторе.

## Performance и log aggregation

- Проверяйте `logger.isDebugEnabled()` перед построением тяжёлых структур или используйте лямбду: `logger.debug("Data: {}", () -> buildExpensiveData())`.
- Избегайте логирования целых коллекций и больших ответов API; логируйте сводку (размер, тип).
- Для отправки в Elasticsearch/CloudWatch используйте буферизацию и batch; при необходимости — обогащение и фильтрация в pipeline до записи.

## Лучшие практики

1. Единый набор имён полей: @timestamp, level, logger, message, correlationId, traceId, spanId, eventType, duration, application, environment.
2. Первый раз термин можно выделить жирным, в дальнейшем — обычный текст.
3. Ошибки: всегда включать operation, errorType, errorMessage и контекст в структурированном виде плюс stack trace.
4. Не хранить чувствительные данные (пароли, токены) в логах; маскировать при необходимости.

## Решение проблем

| Проблема | Решение |
|----------|---------|
| Объекты не сериализуются в JSON (циклические ссылки, даты) | Подключить JavaTimeModule, FAIL_ON_EMPTY_BEANS: false; логировать только нужные поля или использовать кастомный сериализатор/fallback в строку. |
| Correlation ID теряется в @Async / CompletableFuture | Декоратор задачи: копировать MDC до запуска, в колбеке/новом потоке вызывать MDC.setContextMap() и в finally MDC.clear(). Для WebFlux — contextWrite с correlationId. |
| Логирование замедляет приложение | Включить проверку уровня (isDebugEnabled); не строить большие объекты для лога без необходимости; замерить разницу и при необходимости вынести запись в отдельный поток/очередь. |

## Частые вопросы

**Как передать correlation ID в асинхронных вызовах (CompletableFuture, @Async)?** Копируйте `MDC.getCopyOfContextMap()` перед запуском задачи; в колбеке или в новом потоке вызовите `MDC.setContextMap(contextMap)` и в `finally` — `MDC.clear()`. Для WebClient/Reactor прокидывайте ID через `contextWrite`.

**Почему в JSON-логах не сериализуются некоторые поля?** Убедитесь, что объекты сериализуемы (нет циклических ссылок, даты через JavaTimeModule). Используйте `FAIL_ON_EMPTY_BEANS: false` и при необходимости логируйте только выбранные поля или добавьте кастомные сериализаторы.

**Стоит ли логировать большие объекты (ответы API, коллекции)?** Нет: логируйте идентификаторы, размер, тип. Детали — только на уровне TRACE/DEBUG и по флагу (например, `detailed.logging`), чтобы не замедлять приложение и не раздувать хранилище.
