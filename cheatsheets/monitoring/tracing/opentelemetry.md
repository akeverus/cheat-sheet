---
title: "OpenTelemetry"
description: "Кратко: OpenTelemetry — единый открытый стандарт телеметрии: трейсы (traces), метрики (metrics), логи (logs). SDK, авто- и ручная инструментация, экспортеры в Jaeger, Prometheus, Zipkin и др., контекст и baggage."
tags:
  - monitoring
  - tracing
  - opentelemetry
difficulty: "intermediate"
prerequisites: []
next:
  - go-observability
updated: "2026-04-20"
---
# OpenTelemetry

Кратко: **OpenTelemetry** — единый открытый стандарт телеметрии: трейсы (traces), метрики (metrics), логи (logs). SDK, авто- и ручная инструментация, экспортеры в Jaeger, Prometheus, Zipkin и др., контекст и baggage.

## Полезные ссылки

### Официальная документация
- [OpenTelemetry Documentation](https://opentelemetry.io/docs/) — основная документация
- [OpenTelemetry Java](https://opentelemetry.io/docs/instrumentation/java/) — инструментация для Java
- [OpenTelemetry Specification](https://opentelemetry.io/docs/specs/otel/) — спецификация

### Ресурсы
- [OpenTelemetry Best Practices](https://opentelemetry.io/docs/best-practices/)
- [Jaeger — OpenTelemetry](https://www.jaegertracing.io/docs/latest/deployment/#collector) — экспорт в Jaeger

### См. также
- [Jaeger](jaeger.md) — Jaeger и интеграция с OpenTelemetry
- [Distributed Tracing](../) — раздел трейсинга
- [Prometheus](../metrics/prometheus.md) — метрики
- [Monitoring README](../) — обзор мониторинга

- [Распределённое трассирование](distributed-tracing.md)
- [Zipkin](zipkin.md)
- [Go: наблюдаемость](../../languages/go/go-observability.md)
- [Вопросы на собеседовании](../../interview/monitoring/opentelemetry-interview.md) — подготовка к интервью
## Содержание

- [Введение](#введение)
- [Архитектура и компоненты](#архитектура-и-компоненты)
- [Трейсы (Traces)](#трейсы-traces)
- [Метрики (Metrics)](#метрики-metrics)
- [Логи (Logs)](#логи-logs)
- [Инструментация (Java)](#инструментация-java)
- [Экспортеры и бэкенды](#экспортеры-и-бэкенды)
- [Контекст и Baggage](#контекст-и-baggage)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)

## Введение

**OpenTelemetry** — проект CNCF, объединяющий сбор телеметрии (трейсы, метрики, логи) в едином API и SDK для многих языков. Вместо привязки к конкретному бэкенду (Jaeger, Zipkin, Prometheus) приложение экспортирует данные в формате OTLP или через экспортеры в нужную систему. Это снижает vendor lock-in и упрощает смену бэкендов.

Зачем использовать: единая инструментация для трейсов, метрик и логов; автоинструментация для популярных фреймворков (Java, Spring, HTTP, gRPC, Kafka); ручная инструментация через API; экспорт в Jaeger, Zipkin, Prometheus, Grafana Loki и др.


## Архитектура и компоненты

**API** — абстракция для создания span, метрик, логов. **SDK** — реализация API с буферизацией, батчингом, экспортом. **Экспортеры** — отправка данных в бэкенд (OTLP, Jaeger, Zipkin, Prometheus). **Автоинструментация** — агент или библиотеки, подключающие инструментацию к HTTP, JDBC, Kafka и т.д. без изменения кода. **Collector** (опционально) — прием OTLP, обработка (фильтрация, батчинг) и пересылка в бэкенды.


## Трейсы (Traces)

**Trace** — дерево span'ов; каждый span — операция с именем, временем начала/окончания, атрибутами и ссылкой на родителя. **Context propagation** — передача trace context (trace ID, span ID) между сервисами через заголовки (W3C Trace Context). Инструментация: создание span при обработке запроса, при вызове БД, при отправке в Kafka и т.д. Сэмплирование — ограничение объёма трейсов (head или tail sampling).


## Метрики (Metrics)

**OpenTelemetry Metrics API** — создание счётчиков (Counter), гистограмм (Histogram), датчиков (Gauge). Экспорт в Prometheus или OTLP. Связь с Micrometer: Micrometer может использовать OpenTelemetry как бэкенд или наоборот — в Spring Boot 3 часто Micrometer + Micrometer Tracing (совместимость с OpenTelemetry).


## Логи (Logs)

**Logs API** — структурированные логи с привязкой к trace (trace ID, span ID в полях лога). Экспорт в OTLP или в системы логов (Loki, Elasticsearch). Корреляция логов и трейсов по trace ID для отладки.


## Инструментация (Java)

**Автоинструментация:** OpenTelemetry Java Agent — JAR, подключаемый через `-javaagent`; автоматически инструментирует HTTP клиент/сервер, JDBC, Kafka, Redis и др. **Ручная инструментация:** @WithSpan или Span API в коде для кастомных операций. **Spring Boot:** зависимость opentelemetry-java-instrumentation-spring-boot-starter или Micrometer Tracing с OpenTelemetry bridge.


## Экспортеры и бэкенды

**OTLP** (gRPC или HTTP) — стандартный протокол; OpenTelemetry Collector принимает OTLP и пересылает в Jaeger, Zipkin, Prometheus, облачные сервисы. **Jaeger exporter** — прямая отправка в Jaeger (формат Jaeger). **Zipkin exporter** — отправка в Zipkin. **Prometheus exporter** — экспорт метрик для scrape Prometheus. Выбор бэкенда не меняет код приложения при использовании OTLP и Collector.


## Контекст и Baggage

**Trace context** — передача trace ID и span ID между сервисами (заголовки traceparent, tracestate). **Baggage** — пары ключ-значение, распространяемые по цепочке запросов; используются для передачи контекста (например, user_id, request_id) без изменения бизнес-логики. Baggage не должен содержать чувствительных данных (передаётся в заголовках).


## Лучшие практики

- Использовать автоинструментацию где возможно; ручные span — для ключевых бизнес-операций.
- Именование span и атрибуты — единообразные; не логировать секреты в атрибутах.
- Сэмплирование в production для ограничения объёма (например, 10% или tail-based по ошибкам).
- Collector для централизованной маршрутизации и буферизации; снижение нагрузки на приложение.


## Решение проблем

| Проблема | Возможная причина | Действие |
|----------|-------------------|----------|
| Трейсы не появляются в Jaeger | Неверный endpoint экспортера или Collector недоступен | Проверить OTLP endpoint и сеть; логи экспортера |
| Нет контекста между сервисами | Заголовки не передаются | Проверить W3C Trace Context в HTTP клиенте/сервере |
| Высокая нагрузка от телеметрии | Слишком много span или метрик, нет сэмплирования | Включить сэмплирование; сократить атрибуты |


## Частые вопросы

**Чем OpenTelemetry отличается от Jaeger?** Jaeger — бэкенд для хранения и отображения трейсов. OpenTelemetry — стандарт инструментации и экспорта; приложение может экспортировать в Jaeger, Zipkin или другой бэкенд. Jaeger принимает OTLP и нативный формат Jaeger.

**Нужен ли OpenTelemetry Collector?** Не обязателен: приложение может экспортировать напрямую в Jaeger/Zipkin. Collector полезен для централизованной маршрутизации, батчинга, фильтрации и отправки в несколько бэкендов.


## Глоссарий

| Термин | Описание |
|--------|----------|
| OTLP | OpenTelemetry Protocol — протокол экспорта телеметрии |
| Span | Элемент трейса — одна операция с временем и атрибутами |
| Trace | Дерево span'ов для одного запроса |
| Baggage | Ключ-значение, распространяемые по цепочке запросов |
| W3C Trace Context | Стандарт заголовков для передачи trace context |

См. [jaeger](jaeger.md), [Distributed Tracing](../), [Monitoring README](../).
