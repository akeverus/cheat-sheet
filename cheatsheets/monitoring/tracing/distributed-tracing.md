---
title: "Распределённое трассирование"
description: "Распределённое трассирование (Distributed Tracing) — методология отслеживания запросов в распределённых системах. Позволяет наблюдать поток запроса через сервисы, выявлять узкие места и проблемы производительности."
tags:
  - monitoring
  - tracing
  - distributed-tracing
difficulty: "intermediate"
prerequisites: []
next:
  - go-observability
updated: "2026-04-20"
---
# Распределённое трассирование

Распределённое трассирование (Distributed Tracing) — методология отслеживания запросов в распределённых системах. Позволяет наблюдать поток запроса через сервисы, выявлять узкие места и проблемы производительности.

## Полезные ссылки

- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [Zipkin](https://zipkin.io/pages/documentation.html)
- [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)


### См. также
- [[jaeger|Jaeger для Java]]
- [[zipkin|Zipkin]]
## Содержание

- [Основные концепции](#основные-концепции)
  - [Span (отрезок)](#span-отрезок)
  - [Trace (трейс)](#trace-трейс)
  - [Trace Context Propagation](#trace-context-propagation)
- [Jaeger](#jaeger)
  - [Базовая настройка](#базовая-настройка)
  - [Spring Boot](#spring-boot)
  - [Spans в сервисах](#spans-в-сервисах)
  - [HTTP-клиент с трассировкой](#http-клиент-с-трассировкой)
- [Zipkin](#zipkin)
  - [Клиент](#клиент)
  - [Spring Cloud Sleuth](#spring-cloud-sleuth)
- [OpenTelemetry](#opentelemetry)
  - [Настройка](#настройка)
  - [Создание spans](#создание-spans)
  - [Распространение контекста](#распространение-контекста)
- [Baggage (контекстные данные)](#baggage-контекстные-данные)
- [Сэмплирование](#сэмплирование)
- [Интеграция с логированием](#интеграция-с-логированием)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Основные концепции

### Span (отрезок)

Span — базовая единица трассировки, одна операция в рамках трейса.

```java
Span span = tracer.buildSpan("operation-name")
    .withTag("component", "service-name")
    .withTag("http.method", "GET")
    .start();
try {
    doSomeWork();
} finally {
    span.finish();
}
```

### Trace (трейс)

Трейс — полный путь запроса через систему, цепочка связанных spans. Дочерний span создаётся через `asChildOf(parentSpan)` или `asChildOf(extractedContext)`.

### Trace Context Propagation

Передача контекста между сервисами через HTTP-заголовки: `tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, carrier)` при отправке; `tracer.extract(Format.Builtin.HTTP_HEADERS, carrier)` при приёме запроса для продолжения трейса.

## Jaeger

### Базовая настройка

```java
// Зависимости: jaeger-client, opentracing-api

@Configuration
public class JaegerConfig {
    @Bean
    public Tracer jaegerTracer() {
        return Configuration.fromEnv("service-name")
            .withSampler(Samplers.newConstSampler(true))
            .withReporter(ReporterConfiguration.fromEnv().withLogSpans(true))
            .getTracer();
    }
}
```

### Spring Boot

Бин `Tracer` с `Configuration.fromEnv("my-service")`, при необходимости `Samplers.newProbabilisticSampler(0.1)`. Для автоматической трассировки HTTP можно использовать готовые инструментации (например, OpenTelemetry Java или Jaeger Spring Boot).

### Spans в сервисах

В каждом значимом методе: `tracer.buildSpan("operationName").withTag(...).start()`, `try (Scope scope = tracer.activateSpan(span)) { ... }`, в `finally` — `span.finish()`. При ошибке: `span.setTag("error", true)`, `span.log(...)`.

### HTTP-клиент с трассировкой

Перед вызовом другого сервиса инжектировать заголовки: `tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(headers))`. Передавать эти заголовки в запросе (RestTemplate, WebClient). На стороне вызываемого сервиса — `extract` и создание span как дочернего.

## Zipkin

### Клиент

Использовать Brave или официальный Zipkin Java: отправка spans на `http://localhost:9411/api/v2/spans`. Конфигурация: localServiceName, sampler, span handler (HTTP sender).

### Spring Cloud Sleuth

Зависимости: `spring-cloud-starter-sleuth`, `spring-cloud-sleuth-zipkin`. В `application.yml`: `spring.zipkin.base-url`, `spring.sleuth.sampler.probability`. Sleuth автоматически создаёт spans для MVC и вызовов через счётчики; при необходимости добавлять ручные spans через `Tracer`.

## OpenTelemetry

### Настройка

Зависимости: `opentelemetry-api`, `opentelemetry-sdk`, экспортер (jaeger или zipkin). Создать `OpenTelemetrySdk` с `SdkTracerProvider`, добавить `BatchSpanProcessor` с экспортером, задать `Resource` (service.name, service.version). Бин `Tracer` из `openTelemetry.getTracer("service-name", "1.0.0")`.

### Создание spans

`Span span = tracer.spanBuilder("operationName").setAttribute("key", value).startSpan();` затем `try (Scope scope = span.makeCurrent()) { ... } finally { span.end(); }`. События: `span.addEvent("name", Attributes...)`. Ошибки: `span.setStatus(StatusCode.ERROR, message)`, `span.recordException(e)`.

### Распространение контекста

В фильтре/интерцепторе HTTP: извлечь контекст из заголовков через `TextMapPropagator`, создать корневой или дочерний span, вызвать `span.makeCurrent()` и передать управление в цепочку. В асинхронном коде контекст нужно передавать явно (например, захватить `Context.current()` и восстановить в другом потоке).

## Baggage (контекстные данные)

Baggage — пары ключ-значение, передаваемые по всей цепочке трейса. Устанавливаются в span/baggage API и доступны во всех дочерних spans и сервисах. Использовать для передачи request id, tenant id и т.п.; не класть большие или чувствительные данные.

## Сэмплирование

Чтобы не перегружать бэкенд, сэмплируют долю трейсов. Примеры: постоянный сэмплер (100% в dev, 1–10% в prod), probabilistic по trace id, кастомный (например, 100% для ошибок или определённых операций). В Jaeger: `Samplers.newProbabilisticSampler(0.1)`. В OpenTelemetry: `TraceIdRatioBased`, `ParentBased`, кастомный `Sampler`.

## Интеграция с логированием

**MDC:** записывать в Mapped Diagnostic Context `traceId` и `spanId` из текущего span, чтобы они попадали в каждую строку лога. В logback pattern: `[%X{traceId:-}] [%X{spanId:-}]`.

**Структурированные логи:** при логировании ошибки или важного события добавлять в span `addEvent` или атрибуты, чтобы в трейсе и логах была одна и та же идентификация запроса.

## Мониторинг и алертинг

На основе трассировок можно строить метрики: латентность по сервисам/операциям, error rate, throughput. Экспортеры и бэкенды (Jaeger, Zipkin, Tempo) часто интегрируются с Prometheus/Grafana. Алерты строят по метрикам, производным от трейсов (например, p99 latency по сервису).

## Лучшие практики

1. Создавать spans на границах сервисов (входящий HTTP, вызовы к БД и другим API).
2. В асинхронных и очерединых сценариях явно передавать контекст (заголовки сообщений, контекст потока).
3. Использовать сэмплирование в production; при необходимости увеличивать долю для критичных операций или при ошибках.
4. Не закладывать в метки и baggage высокую cardinality (например, user id в каждой метке); использовать ограниченный набор атрибутов для группировки.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Трейс обрывается между сервисами | Контекст не передаётся в заголовках | Проверить, что вызывающий сервис делает inject в заголовки, а вызываемый — extract и создаёт дочерний span; проверить формат заголовков (W3C, B3, Jaeger) |
| Слишком много данных в бэкенде | Высокий процент сэмплирования или много спанов на запрос | Снизить probability; не создавать spans для каждой мелкой операции; отфильтровать ненужные атрибуты в экспортере |
| Потеря контекста в асинхронном коде | Контекст привязан к потоку и не переносится в другой поток | В новом потоке/колбэке восстановить Context (например, Context.current().with(span).makeCurrent()) перед созданием дочерних spans |

Проверка: логи экспортера (ошибки отправки в Jaeger/Zipkin); сетевая доступность коллектора; совпадение формата propagation на всех сервисах.

## Частые вопросы

**Jaeger, Zipkin или OpenTelemetry?** OpenTelemetry — единый API и SDK, экспорт в Jaeger/Zipkin и другие бэкенды; предпочтителен для новых проектов. Jaeger и Zipkin — готовые бэкенды; Spring Cloud Sleuth интегрирован с Zipkin. Выбор бэкенда по уже используемому стеку и возможностям (поиск, метрики, алерты).

**Как трассировать вызовы через Kafka/RabbitMQ?** Передавать trace context в заголовках сообщения при отправке; при потреблении извлечь контекст из заголовков и создать span как дочерний от извлечённого контекста. Инструментации для Kafka/RabbitMQ делают это автоматически при использовании OpenTelemetry или соответствующих библиотек.

**Нужно ли трассировать все запросы?** Нет. В production обычно сэмплируют 1–10%; при необходимости всегда сэмплировать запросы с ошибками или к критичным операциям через кастомный sampler.
## См. также

- [[prometheus|Prometheus]] — метрики и мониторинг
- [[grafana|Grafana]] — визуализация
- [[java-micrometer|Micrometer]] — метрики JVM
