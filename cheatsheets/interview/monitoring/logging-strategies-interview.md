---
title: "Вопросы на собеседовании: Стратегии логирования"
description: "Комплексное руководство по вопросам собеседования на тему стратегий логирования для Senior Java Developer."
tags:
  - interview
  - monitoring
  - logging-strategies-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Стратегии логирования"
  - "Logging strategies interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: Стратегии логирования

Комплексное руководство по вопросам собеседования на тему стратегий логирования для `Senior Java Developer`.

## Полезные ссылки

### Официальная документация

- [SLF4J](https://www.slf4j.org/)
- [Logback Documentation](https://logback.qos.ch/documentation.html)
- [Spring Boot Logging Reference](https://docs.spring.io/spring-boot/reference/features/logging.html)
- [Logstash Logback Encoder](https://github.com/logfellow/logstash-logback-encoder)
- [OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/)
- [Introduction to Java Logging](https://www.baeldung.com/java-logging-intro) — обзор фреймворков логирования в Java
- [Send the Logs of a Java App to the Elastic Stack (ELK)](https://www.baeldung.com/java-application-logs-to-elastic-stack) — отправка логов в ELK
- [Structured Logging in Spring Boot](https://www.baeldung.com/spring-boot-structured-logging) — структурированные логи в Spring Boot
- [An Introduction to ELK Stack](https://www.baeldung.com/ops/elk) — введение в ELK Stack

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [Роль документа в связке interview](#роль-документа-в-связке-interview)
- [See also](#see-also)

**Основы стратегий логирования**
- [Q1. (!) Что такое стратегия логирования и из чего она состоит?](#q1--что-такое-стратегия-логирования-и-из-чего-она-состоит)
- [Q2. Как выбрать уровни логирования по окружению (dev vs prod)?](#q2-как-выбрать-уровни-логирования-по-окружению-dev-vs-prod)
- [Q19. Как настроить логирование по компонентам (пакетам)?](#q19-как-настроить-логирование-по-компонентам-пакетам)
- [Q25. (!) Как выбрать стратегию логирования для нового микросервиса?](#q25--как-выбрать-стратегию-логирования-для-нового-микросервиса)

**Структурированное логирование и формат**
- [Q3. (!) Что такое структурированное логирование (JSON) и когда его применять?](#q3--что-такое-структурированное-логирование-json-и-когда-его-применять)
- [Q11. Как обеспечить консистентность формата логов в микросервисах?](#q11-как-обеспечить-консистентность-формата-логов-в-микросервисах)
- [Q17. Как логировать исключения правильно (stack trace, контекст)?](#q17-как-логировать-исключения-правильно-stack-trace-контекст)
- [Q18. Что такое Markers в SLF4J/Logback и когда их использовать?](#q18-что-такое-markers-в-slf4jlogback-и-когда-их-использовать)
- [Q26. Что такое структурированное логирование и зачем JSON?](#q26-что-такое-структурированное-логирование-и-зачем-json)

**Correlation ID и трассировка**
- [Q5. (!) Что такое correlation id и как его использовать в микросервисах?](#q5--что-такое-correlation-id-и-как-его-использовать-в-микросервисах)
- [Q6. Как связать логи с распределённой трассировкой (traceId, spanId)?](#q6-как-связать-логи-с-распределённой-трассировкой-traceid-spanid)
- [Q7. (!) Что такое MDC и как его применять в многопоточном коде?](#q7--что-такое-mdc-и-как-его-применять-в-многопоточном-коде)
- [Q29. Что такое correlation ID и зачем он в логах?](#q29-что-такое-correlation-id-и-зачем-он-в-логах)
- [Q30. Как связать логи с метриками и трейсами для расследования?](#q30-как-связать-логи-с-метриками-и-трейсами-для-расследования)

**Ротация, хранение и агрегация**
- [Q4. Как организовать ротацию и хранение лог-файлов?](#q4-как-организовать-ротацию-и-хранение-лог-файлов)
- [Q12. (!) Что такое log aggregation и зачем он нужен?](#q12--что-такое-log-aggregation-и-зачем-он-нужен)
- [Q13. (!) Как интегрировать логи с ELK (Elasticsearch, Logstash, Kibana)?](#q13--как-интегрировать-логи-с-elk-elasticsearch-logstash-kibana)
- [Q16. Что такое retention политика для логов и как её задать?](#q16-что-такое-retention-политика-для-логов-и-как-её-задать)
- [Q24. Что такое централизованное логирование и какие риски?](#q24-что-такое-централизованное-логирование-и-какие-риски)
- [Q27. Как организовать ротацию и хранение логов?](#q27-как-организовать-ротацию-и-хранение-логов)

**Безопасность и оптимизация**
- [Q8. Как не логировать чувствительные данные (пароли, токены)?](#q8-как-не-логировать-чувствительные-данные-пароли-токены)
- [Q9. Что такое log sampling и когда его применять?](#q9-что-такое-log-sampling-и-когда-его-применять)
- [Q10. Как уменьшить объём логов от сторонних библиотек?](#q10-как-уменьшить-объём-логов-от-сторонних-библиотек)
- [Q20. Как не раздувать логи при высокой нагрузке?](#q20-как-не-раздувать-логи-при-высокой-нагрузке)
- [Q28. Как не логировать чувствительные данные (PII, пароли)?](#q28-как-не-логировать-чувствительные-данные-pii-пароли)

**Асинхронность, реактивность и интеграция**
- [Q14. Что такое асинхронный аппендер и когда его использовать?](#q14-что-такое-асинхронный-аппендер-и-когда-его-использовать)
- [Q15. Как логировать в реактивных стеках (WebFlux, Project Reactor)?](#q15-как-логировать-в-реактивных-стеках-webflux-project-reactor)
- [Q21. Что такое audit log и чем он отличается от application log?](#q21-что-такое-audit-log-и-чем-он-отличается-от-application-log)
- [Q22. Как логировать в многопоточном и асинхронном коде?](#q22-как-логировать-в-многопоточном-и-асинхронном-коде)
- [Q23. Как интегрировать логи с метриками (Micrometer, Prometheus)?](#q23-как-интегрировать-логи-с-метриками-micrometer-prometheus)

**Kubernetes, Loki и продвинутые темы**
- [Q31. Как собирать логи в Kubernetes с Fluentd / Fluent Bit?](#q31-как-собирать-логи-в-kubernetes-с-fluentd--fluent-bit)
- [Q32. Что такое Grafana Loki и чем он отличается от ELK?](#q32-что-такое-grafana-loki-и-чем-он-отличается-от-elk)
- [Q33. Как динамически менять уровень логирования без рестарта (Actuator, JMX)?](#q33-как-динамически-менять-уровень-логирования-без-рестарта-actuator-jmx)
- [Q34. Как реализовать rate-limiting логов при шторме ошибок?](#q34-как-реализовать-rate-limiting-логов-при-шторме-ошибок)
- [Q35. Как связать distributed tracing с логами через traceId в OpenTelemetry?](#q35-как-связать-distributed-tracing-с-логами-через-traceid-в-opentelemetry)
- [Q36. Как логировать производительность (performance logging) без деградации?](#q36-как-логировать-производительность-performance-logging-без-деградации)
- [Q37. Как соблюдать GDPR при логировании (PII, маскирование, аудит)?](#q37-как-соблюдать-gdpr-при-логировании-pii-маскирование-аудит)
- [Q38. Как тестировать стратегию логирования в Spring Boot?](#q38-как-тестировать-стратегию-логирования-в-spring-boot)

## Роль документа в связке interview

Этот файл фокусируется на стратегии: как выбирать формат логов, политику хранения, sampling, безопасность и стоимость.
Для инструментальных вопросов и быстрых «как настроить» используйте [файл по логированию](../logging/logging-interview.md).
Для связи логов с метриками и трейсами смотрите [Метрики и трейсинг](metrics-tracing-interview.md) и [Observability](observability-interview.md).

## Как отвечать на вопросы про logging strategy

Хороший ответ обычно состоит из трёх частей: **когда применять**, **какой trade-off**, **как проверять в проде**.
Для проверки называйте конкретику: p95 latency сервиса, объём логов/день, доля потерянных логов, время расследования инцидента.

## Стратегии и уровни

## Q1. (!) Что такое стратегия логирования и из чего она состоит?

Стратегия логирования — это набор осознанных решений о том, **что, как и куда** писать в логи, чтобы они помогали в проде, а не превращались в шум и статью расходов. Не «как настроить аппендер», а «по каким правилам команда логирует во всех сервисах».

Стратегия отвечает на шесть вопросов:

- **Уровни (что логировать)** — какие события и на каком уровне (`DEBUG`, `INFO`, `WARN`, `ERROR`), по-разному в dev и prod.
- **Формат** — человекочитаемый текст для разработки, `JSON` для prod (чтобы агрегатор парсил поля).
- **Назначение (куда писать)** — консоль, файл, stdout, централизованное хранилище (`ELK`, `Loki`).
- **Контекст** — `MDC`, `correlationId`, `traceId`: чтобы по одному запросу можно было собрать логи всех сервисов.
- **Хранение** — ротация, retention по окружениям, sampling при высокой нагрузке.
- **Безопасность** — маскирование PII, паролей, токенов.

Почему это важно сформулировать явно: без стратегии каждый сервис логирует по-своему, поля называются по-разному, в prod утекают пароли, а объём логов растёт неконтролируемо. Единые правила превращают логи в инструмент расследования инцидентов.

Стратегия логирования раскладывается на шесть составляющих, у каждой — свои конкретные значения:

- **Уровни** → `DEBUG` / `INFO` / `WARN` / `ERROR`.
- **Формат** → `Text` для dev, `JSON` для prod.
- **Назначение** → `Console` / `File` / `ELK` / `Loki`.
- **Контекст** → `MDC` / `Correlation ID` / `TraceId`.
- **Безопасность** → маскирование PII.
- **Retention** → ротация / архив / удаление.

**Рекомендация.** Зафиксировать стратегию в `README` или отдельном документе: уровни по умолчанию для prod (`INFO / WARN`), формат (`JSON` в prod), список обязательных полей структурированного лога (service, `traceId`, timestamp, level, message), правила маскирования (пароли, токены), retention по окружениям. При онбординге разработчик получает готовый конфиг `Logback` и соглашения по именованию сообщений — а не изобретает свои.

## Q2. Как выбрать уровни логирования по окружению (dev vs prod)?

Принцип простой: **в dev — подробно и читаемо, в prod — лаконично и машиночитаемо**. Уровень в проде поднимают не из лени, а потому что лишние логи стоят денег (хранилище, ingestion) и маскируют важные события шумом.

- **dev** — `DEBUG`/`INFO` для детальной отладки, вывод в консоль, человекочитаемый текстовый формат.
- **prod** — `INFO`/`WARN` по умолчанию, `JSON` для парсинга агрегатором; объём минимизируем ради снижения затрат и шума. `DEBUG` включают точечно по пакету и только на время расследования (см. Q33).

Конфигурация `Spring Boot` через `application.yml`:

```yaml
# application-dev.yml
logging:
  level:
    root: INFO
    com.myapp: DEBUG
    org.springframework: INFO
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# application-prod.yml
logging:
  level:
    root: WARN
    com.myapp: INFO
    org.springframework: WARN
    org.hibernate: WARN
```

В `logback-spring.xml` — разделение по профилю:

```xml
<configuration>
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>

    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="JSON_FILE" />
        </root>
        <logger name="org.springframework" level="WARN" />
        <logger name="org.hibernate" level="WARN" />
    </springProfile>
</configuration>
```

В [Spring Boot](../frameworks/spring/spring-boot-interview.md) профили активируются через `spring.profiles.active` или переменную окружения `SPRING_PROFILES_ACTIVE`.

## Q3. (!) Что такое структурированное логирование (JSON) и когда его применять?

Структурированное логирование — это запись лога как набора именованных полей (`JSON`), а не как одной текстовой строки, которую потом надо разбирать регулярками. Каждое значимое значение (`orderId`, `userId`, `traceId`) становится отдельным полем.

Зачем это нужно: агрегатор (`ELK`, `Loki`) индексирует поля, и вы пишете запросы вроде `orderId=12345 AND level=ERROR` вместо grep по тексту. Отсюда плюсы:

- **Точный поиск и фильтрация** по любому полю, а не по подстроке.
- **Агрегация** — графики и алерты по значениям полей.
- **Связь с метриками и трейсами** через общие поля (`traceId`, `service`).

Пример неструктурированного лога:

```
2026-04-11 10:15:32 INFO  OrderService - Order created: orderId=12345, userId=67890
```

Тот же лог в структурированном `JSON` формате:

```json
{
  "@timestamp": "2026-04-11T10:15:32.456Z",
  "level": "INFO",
  "logger": "com.myapp.service.OrderService",
  "message": "Order created",
  "service": "order-service",
  "traceId": "abc123def456",
  "spanId": "789xyz",
  "orderId": 12345,
  "userId": 67890,
  "env": "prod"
}
```

Настройка в `logback-spring.xml` с `LogstashEncoder`:

```xml
<configuration>
    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>
                {"service":"order-service","env":"${SPRING_PROFILES_ACTIVE}"}
            </customFields>
            <includeMdcKeyName>traceId</includeMdcKeyName>
            <includeMdcKeyName>spanId</includeMdcKeyName>
            <includeMdcKeyName>correlationId</includeMdcKeyName>
        </encoder>
    </appender>

    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="JSON_CONSOLE" />
        </root>
    </springProfile>
</configuration>
```

Зависимость в `build.gradle`:

```groovy
implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
```

**Когда применять.** В prod при централизованном сборе логов — это основной сценарий. В dev обычно оставляют текстовый формат: глазами `JSON` читать неудобно, а локально агрегатор не нужен.

## Q4. Как организовать ротацию и хранение лог-файлов?

Ротация — это автоматическая смена лог-файла по достижении лимита (по размеру или по времени), чтобы один файл не рос бесконечно и не забил диск. В `Logback` за это отвечает `RollingFileAppender`: старые файлы архивируются (`.gz`), а самые древние удаляются по политике хранения.

Конфигурация `logback-spring.xml` с ротацией:

```xml
<appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/var/log/myapp/application.log</file>

    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <!-- Ротация по дате и размеру -->
        <fileNamePattern>/var/log/myapp/application.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
        <!-- Максимальный размер одного файла -->
        <maxFileSize>100MB</maxFileSize>
        <!-- Сколько дней хранить -->
        <maxHistory>30</maxHistory>
        <!-- Общий лимит на все файлы -->
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>

    <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
</appender>
```

**Где хранятся логи.** На диске узла файл — это лишь временный буфер с ограничением по месту. Основное хранилище — центральный агрегатор (`ELK`, `Loki`, облако), куда логи отправляются и где удаляются по своей политике. Важно: `maxHistory`/`totalSizeCap` в `Logback` управляют только локальными файлами; retention в агрегаторе задаётся отдельно (индексы `Elasticsearch` через ILM, политики в `Loki`).

**В контейнерах ротацией файлов заниматься не нужно.** В [Docker](../devops/docker-interview.md) и [Kubernetes](../devops/kubernetes-interview.md) приложение пишет в stdout, а сбор и ротацию берёт на себя платформа (`Fluentd`, `Filebeat` как DaemonSet). `RollingFileAppender` актуален в основном для классического деплоя на VM.

## Q5. (!) Что такое correlation id и как его использовать в микросервисах?

`Correlation ID` — уникальный идентификатор запроса, который генерируется один раз (обычно на шлюзе) и передаётся дальше по всей цепочке вызовов. Каждый сервис кладёт этот id в `MDC` и пишет в свои логи, поэтому в агрегаторе одним фильтром по `correlationId` собираются логи всех сервисов, участвовавших в обработке одного запроса.

Проблема, которую он решает: в монолите stack trace показывает весь путь запроса, а в микросервисах запрос проходит через 5-10 сервисов и в каждом — свои разрозненные логи. Без сквозного id связать их невозможно. Correlation ID — это «нить», которая прошивает распределённый запрос.

Ключевые моменты реализации:

- **Генерация** — если входящий запрос принёс заголовок `X-Correlation-Id`, переиспользуем его; если нет — генерируем `UUID`.
- **Хранение** — кладём в `MDC`, чтобы id автоматически попадал в каждую запись лога.
- **Проброс** — при вызове другого сервиса добавляем заголовок с тем же id.
- **Очистка** — обязательно убираем из `MDC` в `finally`, иначе id «протечёт» в следующий запрос на том же потоке (пул потоков переиспользуется).

Поток запроса по сервисам (`Client` → `Gateway` → `ServiceA` → `ServiceB`/`ServiceC`):

1. `Client` отправляет `POST /order` на `Gateway`.
2. `Gateway` генерирует id: `correlationId = UUID.randomUUID()`.
3. `Gateway` вызывает `ServiceA`, передавая заголовок `X-Correlation-Id: abc-123`.
4. `ServiceA` кладёт id в свой контекст: `MDC.put("correlationId", "abc-123")`.
5. `ServiceA` вызывает `ServiceB` с тем же заголовком `X-Correlation-Id: abc-123`; `ServiceB` делает `MDC.put("correlationId", "abc-123")`.
6. `ServiceA` вызывает `ServiceC` с тем же заголовком `X-Correlation-Id: abc-123`; `ServiceC` делает `MDC.put("correlationId", "abc-123")`.

Один и тот же `abc-123` проходит через всю цепочку — по нему в агрегаторе собираются логи всех сервисов.

Реализация фильтра для `Spring Boot`:

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

Передача при вызове другого сервиса через `RestClient`:

```java
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
            .requestInterceptor((request, body, execution) -> {
                String correlationId = MDC.get("correlationId");
                if (correlationId != null) {
                    request.getHeaders().set("X-Correlation-Id", correlationId);
                }
                return execution.execute(request, body);
            })
            .build();
    }
}
```

## Q6. Как связать логи с распределённой трассировкой (traceId, spanId)?

Связка делается через `MDC`: `OpenTelemetry` и `Micrometer Tracing` (замена `Spring Cloud Sleuth` в `Spring Boot 3`) сами кладут текущие `traceId` и `spanId` в `MDC` на время обработки запроса. Дальше любой вывод лога подхватывает эти поля — вручную ничего прокидывать не нужно.

Разница с correlation id из Q5: `traceId` обычно генерирует трассировочная библиотека и он связан с полноценными span'ами в трейс-системе (Jaeger, Tempo), тогда как `correlationId` — это «ручной» сквозной id. Если у вас уже настроена трассировка, отдельный correlation id чаще всего не нужен — `traceId` его заменяет.

Конфигурация `Spring Boot 3` + `Micrometer Tracing`:

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% сэмплирование для dev, снизить в prod

logging:
  pattern:
    # traceId и spanId автоматически в MDC
    console: "%d{HH:mm:ss.SSS} [%thread] [%X{traceId:-},%X{spanId:-}] %-5level %logger{36} - %msg%n"
```

Зависимости в `build.gradle`:

```groovy
implementation 'io.micrometer:micrometer-tracing-bridge-otel'
implementation 'io.opentelemetry:opentelemetry-exporter-otlp'
```

Главная выгода: в `ELK` или `Grafana` один и тот же `traceId` связывает трейсы и логи — из проблемного span'а переходите к логам по `traceId` и наоборот. Это и есть «navigation» между двумя из трёх столпов observability. Подробнее о трассировке в [Метрики и трейсинг](metrics-tracing-interview.md).

## Q7. (!) Что такое MDC и как его применять в многопоточном коде?

`MDC` (`Mapped Diagnostic Context`) — это карта «ключ-значение», привязанная к текущему потоку (под капотом `ThreadLocal`). Вы кладёте туда контекст один раз (`correlationId`, `traceId`, `userId`), и он автоматически добавляется в каждую запись лога этого потока — не нужно передавать его параметром в каждый вызов `log.info(...)`.

Главный нюанс вынесен в название — context **привязан к потоку**. Из этого следуют две вещи, на которых валятся на собеседовании:

1. **Обязательная очистка.** Потоки в пуле переиспользуются. Если не очистить `MDC` в `finally`, значения «протекут» в следующий запрос на том же потоке — и в логах появятся чужие `userId`/`correlationId`.
2. **Не копируется при смене потока.** При `@Async`, `CompletableFuture`, `ExecutorService` работа уходит в другой поток, где `MDC` пуст. Решение — захватить контекст в исходном потоке и восстановить в рабочем (см. `TaskDecorator` ниже и Q22).

Базовое использование `MDC`:

```java
@Slf4j
public class OrderService {

    public void processOrder(String orderId, String userId) {
        MDC.put("orderId", orderId);
        MDC.put("userId", userId);
        try {
            log.info("Processing order");        // orderId и userId попадут в лог
            validateOrder(orderId);
            log.info("Order validated");
        } finally {
            MDC.clear();                         // обязательно очищать
        }
    }
}
```

Как перенести `MDC` в другой поток через `TaskDecorator` (идея: снять копию контекста в вызывающем потоке, поставить её в рабочем):

```java
@Component
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Захватываем MDC-контекст в вызывающем потоке
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);  // Восстанавливаем в рабочем потоке
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor(MdcTaskDecorator decorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setTaskDecorator(decorator);   // Применяем декоратор
        executor.initialize();
        return executor;
    }
}
```

В `logback-spring.xml` добавляем поля `MDC` в паттерн:

```xml
<pattern>%d{ISO8601} [%thread] [%X{correlationId}] [%X{userId}] %-5level %logger{36} - %msg%n</pattern>
```

С `LogstashEncoder` все ключи `MDC` попадают в `JSON` автоматически.

## Q8. Как не логировать чувствительные данные (пароли, токены)?

Главный принцип — **защита эшелонированная**: ни один механизм не ловит всё, поэтому их комбинируют. Самый надёжный уровень — вообще не пускать секрет в лог; маскирование — это страховка от человеческой ошибки.

1. **Не передавать секрет в лог вообще** (основной приём) — логировать факт, а не данные: «авторизация выполнена», а не пароль.
2. **Маскирование** — кастомный `Converter` в `Logback` режет секреты по паттернам, даже если кто-то их случайно залогировал.
3. **Структурированные поля** — в `JSON`-логе просто не включать чувствительные ключи.
4. **Код-ревью и линтеры** — ловить паттерны типа `log...password` до прода.

Кастомный `Converter` для маскирования в `Logback` (страховочный слой — режет номера карт и значения вида `token=...`):

```java
public class MaskingConverter extends ClassicConverter {

    private static final Pattern CARD_PATTERN =
        Pattern.compile("\\b(\\d{4})\\d{8,12}(\\d{4})\\b");
    private static final Pattern TOKEN_PATTERN =
        Pattern.compile("(token|password|secret|apiKey)=([^\\s,}]+)", Pattern.CASE_INSENSITIVE);

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        message = CARD_PATTERN.matcher(message).replaceAll("$1****$2");
        message = TOKEN_PATTERN.matcher(message).replaceAll("$1=***");
        return message;
    }
}
```

Подключение в `logback-spring.xml`:

```xml
<configuration>
    <conversionRule conversionWord="maskedMsg"
                    converterClass="com.myapp.logging.MaskingConverter" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{ISO8601} %-5level %logger{36} - %maskedMsg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

## Q9. Что такое log sampling и когда его применять?

`Log sampling` — это запись только части однотипных сообщений (например, каждого 100-го `DEBUG` или 1% запросов) для снижения объёма при высокой нагрузке. Идея: для статистики и понимания поведения системы редко нужны *все* записи — достаточно репрезентативной выборки.

Критично делать sampling правильно: **`ERROR` и `WARN` никогда не сэмплируют** — это редкие и ценные события, терять их нельзя. Сэмплируют только высокочастотный шум (`DEBUG`/`INFO`), как в фильтре ниже.

Кастомный `TurboFilter` в `Logback` для сэмплирования:

```java
public class SamplingTurboFilter extends TurboFilter {

    private int sampleRate = 100;  // логировать каждый N-й
    private final AtomicLong counter = new AtomicLong();

    @Override
    public FilterReply decide(Marker marker, Logger logger,
                               Level level, String format,
                               Object[] params, Throwable t) {
        // ERROR и WARN всегда записываем
        if (level.isGreaterOrEqual(Level.WARN)) {
            return FilterReply.NEUTRAL;
        }
        // DEBUG/INFO — сэмплируем
        if (counter.incrementAndGet() % sampleRate == 0) {
            return FilterReply.NEUTRAL;
        }
        return FilterReply.DENY;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
}
```

```xml
<configuration>
    <turboFilter class="com.myapp.logging.SamplingTurboFilter">
        <sampleRate>100</sampleRate>
    </turboFilter>
</configuration>
```

**Когда применять.** Когда полный объём `DEBUG`/`INFO` не нужен или слишком дорог (хранилище, ingestion в `ELK`).

**Подводный камень.** Сэмплирование может «съесть» именно ту запись, которая нужна для расследования конкретного редкого случая. Поэтому всегда комбинируйте: `ERROR`/`WARN` — гарантированно, `INFO`/`DEBUG` — выборочно.

## Q10. Как уменьшить объём логов от сторонних библиотек?

Сторонние библиотеки (Spring, Hibernate, Kafka, Netty) на `DEBUG`/`INFO` генерируют огромный шум, который перекрывает логи вашего бизнес-кода. Решение основано на том, что в `Logback`/`SLF4J` логгеры **иерархичны по имени пакета**: можно прицельно поднять порог для чужого пакета, не трогая свой код.

В `logback-spring.xml` задают уровень по имени пакета:

```xml
<configuration>
    <!-- Свой код — INFO (DEBUG при отладке) -->
    <logger name="com.myapp" level="INFO" />

    <!-- Spring Framework — только предупреждения -->
    <logger name="org.springframework" level="WARN" />
    <logger name="org.springframework.web" level="WARN" />

    <!-- Hibernate — только ошибки и SQL при отладке -->
    <logger name="org.hibernate" level="WARN" />
    <logger name="org.hibernate.SQL" level="WARN" />

    <!-- Apache HTTP Client -->
    <logger name="org.apache.http" level="WARN" />

    <!-- Netty -->
    <logger name="io.netty" level="WARN" />

    <!-- Kafka -->
    <logger name="org.apache.kafka" level="WARN" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Эквивалент в `application.yml` для [Spring Boot](../frameworks/spring/spring-boot-interview.md):

```yaml
logging:
  level:
    root: INFO
    com.myapp: INFO
    org.springframework: WARN
    org.hibernate: WARN
    org.apache.kafka: WARN
    io.netty: WARN
```

**Эмпирическое правило.** В prod держать `WARN` для сторонних библиотек по умолчанию; опускать до `DEBUG` точечно (например, `org.hibernate.SQL` для отладки запросов) и только на время расследования.

## Q11. Как обеспечить консистентность формата логов в микросервисах?

Зачем нужна консистентность: если в одном сервисе поле зовётся `traceId`, в другом `trace_id`, а в третьем `tid`, то единый запрос или дашборд по всем сервисам построить нельзя. Цель — одинаковые имена и типы полей везде. Достигается это не уговорами, а технически — общим конфигом, который сервисы не пишут руками.

Пример общей `JSON`-схемы (контракт полей для всех сервисов):

```json
{
  "@timestamp": "ISO8601",
  "level": "INFO|WARN|ERROR|DEBUG",
  "logger": "fully.qualified.ClassName",
  "message": "Human-readable message",
  "service": "order-service",
  "version": "1.2.3",
  "env": "prod",
  "traceId": "hex-string",
  "spanId": "hex-string",
  "correlationId": "uuid",
  "userId": "optional",
  "stack_trace": "optional, only for errors"
}
```

Подходы к распространению конфига и их связи:

- **Общий logging-starter** раздаёт формат во все сервисы: `Service A`, `Service B`, `Service C`.
- **Spring Cloud Config** аналогично централизованно питает те же `Service A`, `Service B`, `Service C`.
- **Документация** → **Code Review**: соглашения проверяются вручную на ревью.

1. **Общий Spring Boot Starter** (самый надёжный) — артефакт с `logback-spring.xml` и `LogstashEncoder`; сервис подключает зависимость и получает формат «из коробки», ничего не настраивая.
2. **Spring Cloud Config** — централизованная раздача конфига всем сервисам.
3. **Документированные соглашения** — имена полей и уровни для типовых событий, на которые опираются разработчики.
4. **Code Review** — последний рубеж: проверка соответствия стандарту вручную.

## Q12. (!) Что такое log aggregation и зачем он нужен?

`Log aggregation` — это сбор логов со всех узлов и сервисов в единое хранилище, где их можно искать, анализировать и строить алерты централизованно.

Зачем: в распределённой системе логи разбросаны по десяткам подов на разных узлах, поды эфемерны (умер под — пропали его файлы), а инцидент почти всегда задевает несколько сервисов. Заходить по SSH на каждый узел и грепать файлы невозможно. Агрегатор решает это: один интерфейс, один запрос по `traceId` показывает всю картину, логи переживают смерть пода.

Схема агрегации по слоям:

- **Applications** — `Service A`, `Service B`, `Service C` пишут через stdout/file, каждый — своему `Agent`.
- **Collection** — все агенты доставляют (`ship`) данные в `Logstash` / `Fluentd`.
- **Storage & UI** — `Logstash`/`Fluentd` индексирует (`index`) в `Elasticsearch` / `Loki`, а оттуда данные идут в `Kibana` / `Grafana`.

Основные стеки:

- **ELK** (`Elasticsearch` + `Logstash` + `Kibana`) — классика, мощный полнотекстовый поиск, но дороже по хранилищу.
- **PLG** (`Promtail` + `Loki` + `Grafana`) — легче и дешевле, индексирует только метки, а не содержимое (детали в Q32).
- **Облачные** (`CloudWatch`, `Datadog`, `Splunk`) — managed-решения без своей инфраструктуры, но за деньги.

Общая схема одна: агент (`Filebeat`, `Fluentd`, `Promtail`) собирает логи с узлов и шлёт в агрегатор, либо приложение пишет в stdout, а сбором занимается платформа. Подробнее о связи с метриками в [Метрики и трейсинг](metrics-tracing-interview.md).

## Q13. (!) Как интегрировать логи с ELK (Elasticsearch, Logstash, Kibana)?

Пайплайн ELK состоит из четырёх звеньев: приложение пишет `JSON` → агент (`Filebeat`) собирает и доставляет → `Logstash` парсит и обогащает → `Elasticsearch` индексирует → `Kibana` визуализирует. Каждое звено отвечает за свою задачу, поэтому их можно масштабировать и менять независимо.

Полный пайплайн от приложения до дашборда, по звеньям:

- `Spring Boot App` → (JSON stdout) → `Filebeat`.
- `Filebeat` → (ship) → `Logstash`.
- `Logstash` → (filter/enrich) → `Elasticsearch`.
- `Elasticsearch` → (query) → `Kibana`.
- Другой `Spring Boot App` может отдавать логи в тот же `Filebeat` через JSON file.

Конфигурация `Filebeat` (`filebeat.yml`):

```yaml
filebeat.inputs:
  - type: container
    paths:
      - /var/lib/docker/containers/*/*.log
    processors:
      - decode_json_fields:
          fields: ["message"]
          target: ""
          overwrite_keys: true

output.logstash:
  hosts: ["logstash:5044"]
```

Конфигурация `Logstash` (pipeline):

```ruby
input {
  beats {
    port => 5044
  }
}

filter {
  # Парсинг JSON если не распарсен Filebeat
  if ![level] {
    json {
      source => "message"
    }
  }

  # Добавление geo-данных, обогащение
  mutate {
    remove_field => ["agent", "ecs", "host"]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "logs-%{[service]}-%{+YYYY.MM.dd}"
  }
}
```

Альтернативный подход — **Loki** (легче для `Kubernetes`):

```yaml
# promtail-config.yml
server:
  http_listen_port: 9080

clients:
  - url: http://loki:3100/loki/api/v1/push

scrape_configs:
  - job_name: containers
    static_configs:
      - targets: [localhost]
        labels:
          job: app-logs
          __path__: /var/log/myapp/*.log
    pipeline_stages:
      - json:
          expressions:
            level: level
            service: service
            traceId: traceId
      - labels:
          level:
          service:
```

**Рекомендация.** Приложение должно писать в stdout в формате `JSON`, а сбор отдавать агенту. Так приложение не знает про инфраструктуру логирования (можно сменить ELK на Loki без правок кода), а агент даёт буферизацию и переживает кратковременную недоступность агрегатора.

## Q14. Что такое асинхронный аппендер и когда его использовать?

Асинхронный аппендер (`AsyncAppender`) кладёт лог в очередь в памяти и сразу возвращает управление, а отдельный фоновый поток уже сбрасывает записи в целевой аппендер (файл, сеть). Бизнес-поток не ждёт медленный I/O.

Зачем: запись лога в файл или по сети — блокирующая операция (единицы–десятки миллисекунд). На hot path это напрямую увеличивает latency запроса. Async разрывает эту связь: поток приложения тратит микросекунды на постановку в очередь, а реальный I/O происходит вне его.

Ключевые параметры (в таблице ниже) определяют поведение при переполнении очереди — это и есть главный компромисс async-логирования.

```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/myapp/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/myapp/application.%d{yyyy-MM-dd}.log.gz</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
    </appender>

    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <!-- Размер очереди (по умолчанию 256) -->
        <queueSize>1024</queueSize>
        <!-- При переполнении: 0 = не отбрасывать ничего -->
        <discardingThreshold>0</discardingThreshold>
        <!-- Не блокировать при полной очереди -->
        <neverBlock>true</neverBlock>
        <!-- Включить caller data (дороже, но полезно для отладки) -->
        <includeCallerData>false</includeCallerData>
        <appender-ref ref="FILE" />
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC_FILE" />
    </root>
</configuration>
```

| Параметр | Описание | Рекомендация prod |
|---|---|---|
| `queueSize` | Размер буфера | 1024-4096 |
| `discardingThreshold` | Порог отбрасывания | 0 для критичных логов |
| `neverBlock` | Не блокировать при полной очереди | `true` |
| `includeCallerData` | Включать информацию о вызывающем | `false` (дорого) |

**Компромисс.** Очередь живёт в памяти процесса, поэтому при аварийном падении JVM логи, ещё не сброшенные на диск, теряются. Плюс при `neverBlock=true` под пиковой нагрузкой часть записей отбрасывается, чтобы не блокировать приложение. Это осознанная плата: производительность приложения важнее полноты логов в редких пиках. Для критичных событий (`audit`) async не используют — пишут синхронно.

## Q15. Как логировать в реактивных стеках (WebFlux, Project Reactor)?

Корень проблемы: `MDC` работает через `ThreadLocal`, а в реактивном стеке (`WebFlux`, `Project Reactor`) обработка одного запроса свободно перескакивает между потоками event-loop. Кладёшь значение в `MDC` на одном потоке — на следующем операторе оно уже потеряно.

Решение: хранить контекст не в `ThreadLocal`, а в `Reactor Context` (`contextWrite`), который путешествует вместе с реактивной цепочкой независимо от потоков. А в момент самого логирования временно переносить значения из контекста в `MDC` (`doOnEach`), потому что сам логгер по-прежнему читает из `MDC`.

Решение через `contextWrite` в `Reactor`:

```java
@Slf4j
@RestController
public class OrderController {

    @GetMapping("/orders/{id}")
    public Mono<Order> getOrder(@PathVariable String id) {
        return orderService.findById(id)
            .doOnEach(signal -> {
                // Извлекаем контекст и устанавливаем в MDC
                if (!signal.isOnComplete()) {
                    signal.getContextView()
                        .getOrEmpty("correlationId")
                        .ifPresent(cid -> MDC.put("correlationId", (String) cid));
                    log.info("Processing order {}", id);
                    MDC.clear();
                }
            })
            .contextWrite(ctx ->
                ctx.put("correlationId", UUID.randomUUID().toString()));
    }
}
```

Утилитный класс для удобства:

```java
public class ReactiveLogUtils {

    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> action) {
        return signal -> {
            if (signal.isOnNext()) {
                try (var ignored = setMdcFromContext(signal.getContextView())) {
                    action.accept(signal.get());
                }
            }
        };
    }

    private static MDC.MDCCloseable setMdcFromContext(ContextView ctx) {
        ctx.getOrEmpty("traceId")
            .ifPresent(v -> MDC.put("traceId", v.toString()));
        return MDC.putCloseable("reactive", "true");
    }
}
```

Подробнее о реактивном программировании в [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md).

## Q16. Что такое retention политика для логов и как её задать?

`Retention` — политика того, **как долго хранить логи**, прежде чем их автоматически удалить. Это всегда баланс трёх сил: расследование инцидентов (хочется хранить дольше), стоимость хранилища (логи дороги в объёме) и регуляторные требования (часть данных обязаны хранить N лет, часть — наоборот, нельзя долго).

Важно понимать, что retention задаётся **на нескольких уровнях независимо** — локальный файл, индекс агрегатора, облако. Чаще всего реальная политика живёт в агрегаторе, а локальный файл — лишь короткий буфер:

| Уровень | Механизм | Пример |
|---|---|---|
| Локально (узел) | `Logback maxHistory`, `totalSizeCap` | 7 дней, 3 GB макс |
| `Elasticsearch` | `ILM` (Index Lifecycle Management) | hot → warm → cold → delete |
| `Loki` | `retention_period` | 30 дней |
| Облако | Политики сервиса | `CloudWatch` retention |

Пример `Elasticsearch ILM` политики:

```json
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_size": "50gb",
            "max_age": "1d"
          }
        }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "shrink": { "number_of_shards": 1 },
          "forcemerge": { "max_num_segments": 1 }
        }
      },
      "delete": {
        "min_age": "30d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

**Эмпирическое правило.** Разделяйте логи по типу: технические — дни/недели (нужны для свежей отладки), audit и compliance-логи — месяцы/годы (регуляторные требования). Подход `hot → warm → cold → delete` в ILM (как в примере) позволяет хранить долго дёшево: свежие данные на быстрых дисках, старые — на холодных, потом удаление.

## Q17. Как логировать исключения правильно (stack trace, контекст)?

Главное правило одно: **передавайте исключение последним аргументом в `log.error(...)`**, а не вклеивайте `e.getMessage()` в строку. `SLF4J` распознаёт `Throwable` в хвосте и печатает полный stack trace со всеми вложенными причинами (`caused by`). Если же вы пишете только `getMessage()`, теряется тип исключения, причина и место — а это и есть самое ценное для отладки.

Сравнение правильного и двух типичных ошибочных подходов:

```java
// Правильно — stack trace попадёт в лог
log.error("Failed to process order orderId={}", orderId, exception);

// Неправильно — теряется stack trace
log.error("Failed: " + exception.getMessage());

// Неправильно — stack trace в отдельной строке, ломает парсинг
log.error("Failed to process order");
exception.printStackTrace();
```

Добавление контекста через `MDC` перед логированием:

```java
@Slf4j
public class PaymentService {

    public void processPayment(PaymentRequest request) {
        MDC.put("orderId", request.getOrderId());
        MDC.put("userId", request.getUserId());
        MDC.put("amount", String.valueOf(request.getAmount()));
        try {
            gateway.charge(request);
        } catch (PaymentGatewayException e) {
            // Контекст из MDC автоматически попадёт в JSON лог
            log.error("Payment failed for gateway={}", request.getGateway(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

**Плюс структурированного формата.** `LogstashEncoder` кладёт stack trace в отдельное поле `stack_trace`, а не размазывает по многим строкам. Благодаря этому в `Kibana` одна ошибка — это одна запись с полным трейсом внутри, по которой удобно искать и фильтровать (многострочный текстовый stack trace, наоборот, ломает парсинг — см. третий «неправильный» пример выше).

## Q18. Что такое Markers в SLF4J/Logback и когда их использовать?

`Markers` — это именованные метки, которые навешиваются на отдельную запись лога, чтобы потом её можно было выделить из общего потока. Маркер не зависит от уровня и логгера: запись `INFO` с маркером `AUDIT` можно отправить в отдельный файл или индекс, не меняя её уровень.

Главный сценарий — маршрутизация. Например, audit-события надо хранить год в отдельном файле, security-события — слать в SIEM. Логировать их через отдельные классы-логгеры неудобно, а маркер позволяет пометить запись прямо на месте и развести потоки на уровне конфигурации `Logback` (фильтр `OnMarkerEvaluator`) или запросом в `ELK`.

```java
@Slf4j
public class AuditService {

    private static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");
    private static final Marker SECURITY = MarkerFactory.getMarker("SECURITY");

    public void logUserLogin(String userId, String ip) {
        log.info(AUDIT, "User login userId={} ip={}", userId, ip);
    }

    public void logAccessDenied(String userId, String resource) {
        log.warn(SECURITY, "Access denied userId={} resource={}", userId, resource);
    }
}
```

Маршрутизация по маркеру в `logback-spring.xml`:

```xml
<configuration>
    <appender name="AUDIT_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/myapp/audit.log</file>
        <!-- Фильтр: только записи с маркером AUDIT -->
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="ch.qos.logback.classic.boolex.OnMarkerEvaluator">
                <marker>AUDIT</marker>
            </evaluator>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/myapp/audit.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>365</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="AUDIT_FILE" />
    </root>
</configuration>
```

Trade-off: чем больше маркеров, тем выше сложность конфигов; начинайте с 2-3 категорий (`AUDIT`, `SECURITY`, `BUSINESS`).

## Q19. Как настроить логирование по компонентам (пакетам)?

В `Logback` уровень задаётся не на весь сервис, а на конкретный логгер — обычно это имя пакета или класса. Работает это благодаря иерархии: логгеры образуют дерево по точкам в имени, и дочерний пакет наследует уровень родителя, если для него не задано своё значение.

Что это даёт на практике: можно поднять детализацию точечно — `DEBUG` на один проблемный класс `com.myapp.service.PaymentService`, оставив остальное на `INFO`. Корневой логгер (`root`) задаёт уровень по умолчанию для всего, что не переопределено.

```xml
<configuration>
    <!-- Корневой уровень -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>

    <!-- Свой код — можно DEBUG при отладке -->
    <logger name="com.myapp.service" level="INFO" />
    <logger name="com.myapp.controller" level="INFO" />

    <!-- Конкретный класс — точечная отладка -->
    <logger name="com.myapp.service.PaymentService" level="DEBUG" />

    <!-- Сторонние библиотеки — минимум -->
    <logger name="org.springframework" level="WARN" />
    <logger name="org.hibernate" level="WARN" />
</configuration>
```

Динамическая смена уровня в рантайме через `Spring Boot Actuator`:

```bash
# Просмотр текущего уровня
curl http://localhost:8080/actuator/loggers/com.myapp.service

# Установка DEBUG для конкретного пакета
curl -X POST http://localhost:8080/actuator/loggers/com.myapp.service \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'
```

**Подводный камень.** Включать `DEBUG` на корневом логгере в prod ради одной проблемы — антипаттерн: это лавина шума со всех библиотек, риск утечки данных и перегруз хранилища. Всегда опускайте уровень точечно по пакету, который реально расследуете, и через `Actuator` (Q33), а не правкой конфига с рестартом.

## Q20. Как не раздувать логи при высокой нагрузке?

Объём логов под нагрузкой бьёт по двум фронтам: по latency приложения (каждый лог — это работа на hot path) и по стоимости хранилища. Меры ниже даны по приоритету — от «бесплатных и всегда» до ситуативных.

Самая частая ошибка джуниоров — конкатенация строк: `log.debug("..." + obj)` собирает строку и вызывает `toString()` **всегда**, даже когда `DEBUG` выключен и запись никуда не пойдёт. Параметризованный вызов `log.debug("...", obj)` откладывает форматирование до момента, когда уровень реально активен.

```java
// 1. Параметризованные вызовы — НЕ конкатенация
log.debug("User {} ordered {}", userId, orderId);        // правильно
log.debug("User " + userId + " ordered " + orderId);     // неправильно — строка создаётся всегда

// 2. Guard-условие для дорогих вычислений
if (log.isDebugEnabled()) {
    log.debug("Full payload: {}", serializeToJson(payload));  // сериализация только если DEBUG
}

// 3. Rate-limited логирование для повторяющихся ошибок
private final RateLimiter logLimiter = RateLimiter.create(1.0);  // 1 раз/сек

public void handleEvent(Event event) {
    try {
        process(event);
    } catch (Exception e) {
        if (logLimiter.tryAcquire()) {
            log.error("Processing failed eventType={}", event.getType(), e);
        }
    }
}
```

| Мера | Эффект | Когда применять |
|---|---|---|
| Параметризация | Избежать аллокации строк | Всегда |
| Sampling | Снизить объём в N раз | Высокая нагрузка, `DEBUG`/`INFO` |
| Async appender | Не блокировать бизнес-поток | Запись в файл/сеть |
| Уровни библиотек | Убрать шум | Всегда в prod |
| Rate limiting | Не заливать при шторме ошибок | Error storms |

Операционный критерий: контролируйте расход хранилища и `ingestion lag` до и после включения sampling/async.

## Q21. Что такое audit log и чем он отличается от application log?

`Audit log` — это юридически значимая запись о том, **кто, что и когда** сделал в системе (создал заказ, удалил пользователя, изменил права). В отличие от application log, который существует для отладки и который не жалко потерять, audit log существует для compliance и расследований инцидентов безопасности — поэтому к нему совсем другие требования.

Ключевые отличия, из которых вытекает всё остальное (таблица ниже): audit log **неизменяемый** (append-only, нельзя удалять/править — иначе теряется доверие), хранится **годами**, имеет **строгую схему** и **ограниченный доступ**. Поэтому его выносят в отдельный логгер/файл/хранилище, а не мешают с обычными логами.

| Аспект | Application Log | Audit Log |
|---|---|---|
| Цель | Отладка, мониторинг | Соответствие, расследования |
| Что записывать | Всё для диагностики | Действия и изменения данных |
| Retention | 7-30 дней | Месяцы-годы |
| Формат | Свободный | Строгая схема |
| Доступ | Разработчики, ops | Security, compliance |
| Изменяемость | Можно удалять | Immutable (append-only) |

Реализация через отдельный логгер с маркером:

```java
@Slf4j
public class AuditLogger {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    public static void log(String userId, String action, String resource, String result) {
        auditLog.info("userId={} action={} resource={} result={} timestamp={}",
            userId, action, resource, result, Instant.now());
    }
}

// Использование
AuditLogger.log(currentUser.getId(), "DELETE", "order/12345", "SUCCESS");
```

## Q22. Как логировать в многопоточном и асинхронном коде?

Проблема та же, что в Q7: `MDC` живёт в `ThreadLocal`, поэтому при передаче работы в другой поток (`@Async`, `CompletableFuture`, `ExecutorService`) контекст в новом потоке пуст — `correlationId` и `traceId` пропадают из логов асинхронной части.

Принцип решения везде одинаков: **снять копию `MDC` в исходном потоке → восстановить в рабочем → очистить после выполнения**. Различается только то, куда вставить эту обёртку. Три уровня — от самого автоматического к самому ручному:

- **`TaskDecorator`** — для `@Async` и Spring-исполнителей, прозрачно для бизнес-кода (см. Q7).
- **Ручная передача** — для разовых `CompletableFuture`, где декоратора нет.
- **Обёртка `Executor`** — когда хотите один раз обернуть пул и забыть.

```java
// 1. TaskDecorator для @Async (см. Q7)

// 2. CompletableFuture — ручная передача MDC
public CompletableFuture<Result> processAsync(Request request) {
    Map<String, String> mdcContext = MDC.getCopyOfContextMap();
    return CompletableFuture.supplyAsync(() -> {
        try {
            if (mdcContext != null) MDC.setContextMap(mdcContext);
            return doProcess(request);
        } finally {
            MDC.clear();
        }
    }, executor);
}

// 3. Обёртка для ExecutorService
public class MdcAwareExecutor implements Executor {
    private final Executor delegate;

    public MdcAwareExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable command) {
        Map<String, String> ctx = MDC.getCopyOfContextMap();
        delegate.execute(() -> {
            try {
                if (ctx != null) MDC.setContextMap(ctx);
                command.run();
            } finally {
                MDC.clear();
            }
        });
    }
}
```

Критично для надёжности: любое решение по `MDC` в async-коде должно сопровождаться тестом на потерю контекста.

## Q23. Как интегрировать логи с метриками (Micrometer, Prometheus)?

Логи и метрики — два разных столпа observability с разным назначением: метрики отвечают на «сколько и как часто» (агрегаты, дёшево хранить, годятся для алертов), логи — на «что именно произошло» (детали конкретного события). Их не сливают в один канал, а **связывают**, и есть два способа связи.

Первый — общие поля (`traceId`, `service`): из метрики-аномалии вы переходите к логам по тому же `traceId`. Второй — генерировать метрику прямо из потока логов: например, считать `ERROR`-записи кастомным аппендером, чтобы алертить по росту ошибок, не парся логи отдельно.

Счётчик ошибок через кастомный `Appender` (превращает поток логов в метрику `log.events`):

```java
@Component
public class MetricsAppender extends AppenderBase<ILoggingEvent> {

    private final Counter errorCounter;
    private final Counter warnCounter;

    public MetricsAppender(MeterRegistry registry) {
        this.errorCounter = Counter.builder("log.events")
            .tag("level", "ERROR")
            .register(registry);
        this.warnCounter = Counter.builder("log.events")
            .tag("level", "WARN")
            .register(registry);
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLevel() == Level.ERROR) {
            errorCounter.increment();
        } else if (event.getLevel() == Level.WARN) {
            warnCounter.increment();
        }
    }
}
```

В `Grafana` — запросы к логам (`Loki`) и метрикам (`Prometheus`) в одном дашборде:

```promql
# Количество ERROR логов за 5 минут
rate(log_events_total{level="ERROR", service="order-service"}[5m])
```

```logql
# LogQL запрос в Loki
{service="order-service"} |= "ERROR" | json | traceId != ""
```

Бизнес-события: логировать и увеличивать метрику для единого представления. Подробнее в [Метрики и трейсинг](metrics-tracing-interview.md).

## Q24. Что такое централизованное логирование и какие риски?

Централизованное логирование — это сбор логов со всех узлов в одно общее хранилище (`ELK`, `Loki`). Это необходимость для микросервисов (см. Q12), но «одно хранилище для всего» создаёт новые риски, про которые и спрашивают на собеседовании.

Четыре главных риска и меры против каждого:

- **Сетевые потери** — при разрыве связи логи не доезжают. Лечится буфером на агенте, который помнит позицию чтения и дочитывает после восстановления.
- **Стоимость хранения** — единый поток со всех сервисов растёт лавинообразно. Лечится retention + sampling.
- **SPOF (точка отказа)** — упал агрегатор, и наблюдаемость пропала у всех сразу. Лечится HA-кластером с репликами.
- **Безопасность** — в одном месте собраны логи всех систем, включая чувствительные. Лечится шифрованием (in transit + at rest) и RBAC.

Каждый риск централизованного логирования закрывается своей мерой:

- Сетевые потери → буфер на агенте.
- Стоимость хранения → retention + sampling.
- SPOF агрегатора → HA-кластер + реплики.
- Безопасность данных → шифрование + RBAC.

Эти же меры в развёрнутом виде:

1. **Буферизация на агенте** — `Filebeat` хранит позицию чтения и при восстановлении сети дочитывает с того места, где остановился.
2. **Retention политики** — автоматическое удаление старых данных (см. Q16).
3. **High Availability** — кластер `Elasticsearch` с репликами, чтобы падение одного узла не останавливало приём логов.
4. **Контроль доступа** — `RBAC` в `Kibana`, шифрование in transit и at rest.

**Эмпирическое правило.** Заранее решите, что делает система при недоступности агрегатора: буферизует на диск (надёжно, но ест место), деградирует молча или теряет логи. Это проектное решение, а не то, что вы хотите обнаружить во время инцидента.

## Q25. (!) Как выбрать стратегию логирования для нового микросервиса?

Это синтезирующий вопрос: интервьюер проверяет, сложилась ли у вас целостная картина из отдельных тем. Сильный ответ — это короткий чеклист, который вы проходите при заведении любого сервиса, а не импровизация.

Чеклист стартует с главного вопроса — **есть ли общий logging-starter** (Q11)? Если да — подключаете и почти всё работает из коробки; если нет — заводите `logback-spring.xml` по образцу. Дальше по шагам: имя сервиса → `JSON` в prod → `MDC` с `traceId`+`correlationId` → уровни (`INFO` prod / `DEBUG` dev) → маскирование PII → async appender в prod → интеграция с агрегатором.

Чеклист для нового микросервиса по шагам:

1. **Есть общий starter?**
   - Да → подключить logging-starter.
   - Нет → создать `logback-spring.xml`.
2. Обе ветки сходятся: настроить service name.
3. Формат: `JSON` в prod.
4. `MDC`: `traceId` + `correlationId`.
5. Уровни: `INFO` prod / `DEBUG` dev.
6. Маскирование PII.
7. Async appender для prod.
8. Интеграция с агрегатором.

Пример полного `logback-spring.xml` для нового сервиса:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProperty scope="context" name="APP_NAME" source="spring.application.name" />
    <springProperty scope="context" name="APP_ENV" source="spring.profiles.active" defaultValue="dev" />

    <!-- Консольный аппендер для dev -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [%X{traceId:-}] [%X{correlationId:-}] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- JSON аппендер для prod -->
    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"${APP_NAME}","env":"${APP_ENV}"}</customFields>
            <fieldNames>
                <timestamp>@timestamp</timestamp>
                <version>[ignore]</version>
            </fieldNames>
        </encoder>
    </appender>

    <!-- Async обёртка -->
    <appender name="ASYNC_JSON" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>2048</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <neverBlock>true</neverBlock>
        <appender-ref ref="JSON_CONSOLE" />
    </appender>

    <!-- Dev профиль -->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE" />
        </root>
        <logger name="com.myapp" level="DEBUG" />
    </springProfile>

    <!-- Prod профиль -->
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="ASYNC_JSON" />
        </root>
        <logger name="org.springframework" level="WARN" />
        <logger name="org.hibernate" level="WARN" />
        <logger name="org.apache.kafka" level="WARN" />
    </springProfile>
</configuration>
```

Практический выбор стратегии подтверждают метриками: объём логов/день, latency сервиса и MTTR по инцидентам.

## Q26. Что такое структурированное логирование и зачем JSON?

Базовое определение — в Q3; здесь акцент на **внедрении в существующий ландшафт**. Ключевая мысль: `JSON` ценен не сам по себе, а как носитель управляемой схемы полей между сервисами — одинаковые ключи, одинаковые типы, и как следствие предсказуемые дашборды и алерты, которые работают для всех сервисов сразу.

Переводить весь зоопарк сервисов на `JSON` разом рискованно (можно сломать парсинг и дашборды), поэтому делают поэтапно — пилот, стандарт, масштабирование:

Этапность миграции на `JSON` (по порядку): **Этап 1: Пилот** → **Этап 2: Критичные сервисы** → **Этап 3: Все сервисы**. Содержимое каждого этапа:

- Этап 1 (Пилот) — 1 сервис + `JSON` + `Kibana`.
- Этап 2 (Критичные сервисы) — стандарт полей + Starter.
- Этап 3 (Все сервисы) — линтер + мониторинг.

1. **Пилот** — один сервис переводим на `JSON`, проверяем парсинг в `Kibana`/`Grafana`
2. **Стандарт** — фиксируем обязательные поля (`service`, `traceId`, `env`, `version`), создаём starter
3. **Масштабирование** — все сервисы, мониторинг доли непарсимых событий

Пример добавления кастомных бизнес-полей через `StructuredArgument`:

```java
import static net.logstash.logback.argument.StructuredArguments.*;

log.info("Order created {}  {} {}",
    keyValue("orderId", orderId),
    keyValue("amount", amount),
    keyValue("currency", "RUB"));
```

Результат в `JSON`:

```json
{
  "message": "Order created orderId=12345 amount=999.99 currency=RUB",
  "orderId": "12345",
  "amount": 999.99,
  "currency": "RUB"
}
```

## Q27. Как организовать ротацию и хранение логов?

Где именно ротировать — зависит от способа деплоя, и это главное, что надо сказать. На классическом узле (VM) ротацией файла занимается само приложение через `Logback` `RollingFileAppender`. В контейнерах приложение **не** пишет в файл — оно пишет в stdout, а ротацией лог-файла контейнера управляет платформа (`Docker` log driver или `Kubernetes`). Смешивать эти подходы не нужно.

Конфигурация ротации на уровне `Docker` (демон сам режет json-file по размеру):

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "5"
  }
}
```

Конфигурация `Loki` retention:

```yaml
# loki-config.yml
compactor:
  retention_enabled: true

limits_config:
  retention_period: 720h  # 30 дней

schema_config:
  configs:
    - from: 2024-01-01
      store: tsdb
      object_store: s3
      schema: v13
```

**Итог.** Локальная ротация (файл/контейнер) защищает диск узла от переполнения; retention в агрегаторе (`Loki retention_period`, ILM в `Elasticsearch`) управляет долгосрочным хранением и стоимостью. Дополнительно объём снижают sampling (Q9) и понижение уровня детализации для старых данных.

## Q28. Как не логировать чувствительные данные (PII, пароли)?

Дополняет Q8 на уровне структурированных логов. Базовый принцип тот же — лучшая защита в том, чтобы секрет вообще не попал в сообщение. Но в `JSON`-логах есть удобный второй рубеж: маскировать значения по имени поля прямо в энкодере, так что даже случайно залогированный `password` уедет как `***`.

Маскирование с `LogstashEncoder` через `MaskingJsonGeneratorDecorator` (режет по имени ключа и по пути в `JSON`):

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <jsonGeneratorDecorator class="net.logstash.logback.mask.MaskingJsonGeneratorDecorator">
        <!-- Маскировать значения по имени поля -->
        <valueMasker class="net.logstash.logback.mask.RegexValueMasker">
            <regex>password|token|secret|apiKey|authorization</regex>
            <mask>***</mask>
        </valueMasker>
        <!-- Маскировать по пути JSON -->
        <pathMasker>
            <path>creditCard</path>
            <mask>****</mask>
        </pathMasker>
    </jsonGeneratorDecorator>
</encoder>
```

Аннотация `@ToString.Exclude` для Lombok:

```java
@Data
public class UserRequest {
    private String username;

    @ToString.Exclude  // Не попадёт в toString() → не попадёт в лог
    private String password;

    @ToString.Exclude
    private String creditCard;
}
```

Политика и обучение команды обязательны — технические меры не заменяют осознанного подхода к тому, что писать в лог.

## Q29. Что такое correlation ID и зачем он в логах?

Суть та же, что в Q5: сквозной идентификатор запроса, по которому собираются логи всех сервисов. Здесь акцент на главном практическом выводе: **в `Spring Boot 3` свой `correlationId` чаще всего не нужен** — `Micrometer Tracing` сам генерирует `traceId`, кладёт его в `MDC` и пробрасывает между сервисами. Достаточно настроить паттерн лога, чтобы `traceId` в нём появился.

Настройка `Spring Boot 3` с `Micrometer Tracing` (`traceId` уже в `MDC`, остаётся вывести его в лог):

```yaml
# application.yml
spring:
  application:
    name: order-service

management:
  tracing:
    sampling:
      probability: 1.0

logging:
  pattern:
    level: "%5p [${spring.application.name},%X{traceId:-},%X{spanId:-}]"
```

Результат в логах:

```
2026-04-11 10:15:32  INFO [order-service,abc123def456,789xyz] OrderService - Order created
2026-04-11 10:15:32  INFO [order-service,abc123def456,111aaa] PaymentService - Payment processed
```

Для кастомного `Correlation ID` (если `traceId` не подходит) — см. фильтр в Q5.

## Q30. Как связать логи с метриками и трейсами для расследования?

Связующее звено — один и тот же `traceId`, который присутствует в логах, в трейсах и (через exemplars) в метриках. Именно общий идентификатор превращает три отдельных инструмента в единую систему расследования: из любого можно «перепрыгнуть» в другой по одному клику.

Логика расследования всегда идёт **от общего к частному**: метрики замечают, что что-то не так (аномалия), трейсы показывают, *где* в цепочке вызовов проблема, логи объясняют, *что именно* произошло. Зная этот маршрут, on-call инженер доходит от алерта до корневой причины за минуты.

Три инструмента связаны в замкнутый цикл навигации:

- **Метрики** (`Prometheus`/`VictoriaMetrics`) → (exemplar с `traceId`) → **Трейсы** (`Jaeger`/`Tempo`).
- **Трейсы** → (`traceId`) → **Логи** (`Loki`/`Elasticsearch`).
- **Логи** → (timestamp + service) → обратно к **Метрикам**.

Последовательность расследования инцидента:

1. **Метрики** показывают аномалию (рост p99 latency, увеличение error rate)
2. **Трейсы** за аномальный период — находим проблемный трейс
3. **Логи** по `traceId` — видим полную картину по всем сервисам

Пример в `Grafana` — `LogQL` запрос по `traceId` из трейса:

```logql
{service="order-service"} | json | traceId="abc123def456"
```

Exemplars в `Micrometer` для связи метрик с трейсами:

```java
@Bean
public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
}

@Timed(value = "order.processing.time", extraTags = {"type", "create"})
public Order createOrder(OrderRequest request) {
    // exemplar с traceId добавляется автоматически
    return orderRepository.save(mapToEntity(request));
}
```

Финальная проверка стратегии: переход «метрика -> трейс -> лог» должен занимать минуты и быть воспроизводимым для on-call. Подробнее о полной [наблюдаемости систем](observability-interview.md).

## Q31. Как собирать логи в Kubernetes с Fluentd / Fluent Bit?

В `Kubernetes` действует жёсткое разделение ответственности: **приложение пишет только в `stdout`/`stderr` и ничего не знает про сбор логов**, а вытаскивает их с узла, обогащает метаданными и доставляет в хранилище отдельный агент. Так и должно быть — поды эфемерны, файлы внутри них умирают вместе с подом, поэтому собирать логи изнутри приложения бессмысленно.

Механика: container runtime пишет stdout каждого пода в файлы `/var/log/containers/*.log` на узле. Агент `Fluent Bit`/`Fluentd`, развёрнутый как `DaemonSet` (по одному на узел), читает эти файлы, через `kubernetes` filter добавляет namespace, pod name и labels, и шлёт в `Elasticsearch`/`Loki`.

Путь логов внутри узла (`Node`) и наружу:

- `Pod A` (stdout JSON) и `Pod B` (stdout JSON) → `Container Runtime`.
- `Container Runtime` → файлы `/var/log/containers/*.log`.
- Эти файлы → `Fluent Bit DaemonSet` (он живёт на узле).
- `Fluent Bit DaemonSet` доставляет логи дальше — в `Elasticsearch` и в `Loki`.

Паттерны развёртывания агентов:

| Паттерн | Когда | Инструмент |
|---------|-------|------------|
| `DaemonSet` | Стандарт, один агент на узел | `Fluent Bit`, `Filebeat` |
| `Sidecar` | Нужна своя конфигурация на pod | `Fluent Bit` sidecar |
| Прямая отправка | Простые сценарии | `LogstashEncoder` → `Logstash` TCP |

Пример `DaemonSet` конфигурации `Fluent Bit` для `Kubernetes`:

```yaml
# fluent-bit-config.yaml (ConfigMap)
[INPUT]
    Name             tail
    Path             /var/log/containers/*.log
    Parser           docker
    Tag              kube.<namespace_name>.<pod_name>.<container_name>
    Refresh_Interval 5
    Mem_Buf_Limit    50MB

[FILTER]
    Name                kubernetes
    Match               kube.*
    Kube_URL            https://kubernetes.default.svc:443
    Merge_Log           On
    Keep_Log            Off
    # Добавляет поля: kubernetes.namespace, kubernetes.pod_name, kubernetes.labels
    K8S-Logging.Exclude On

[OUTPUT]
    Name            loki
    Match           *
    Host            loki.monitoring.svc
    Port            3100
    Labels          job=fluentbit, namespace=$kubernetes['namespace_name']
    Label_Keys      $level,$service
    Remove_Keys     kubernetes,stream
```

Рекомендация: приложение пишет только в `stdout` в формате `JSON`; агент (`Fluent Bit`) делает обогащение (добавляет namespace, pod name, labels) — разделение ответственности.

## Q32. Что такое Grafana Loki и чем он отличается от ELK?

`Grafana Loki` — это система хранения логов с принципиально иной архитектурой, чем `Elasticsearch`: она индексирует **только метки** (`labels` — `service`, `level`, namespace), а само содержимое логов хранит сжатыми чанками без полнотекстового индекса. Слоган разработчиков — «как Prometheus, но для логов».

Отсюда вытекает весь компромисс. Полнотекстовый индекс в `Elasticsearch` — самая дорогая часть: он раздувает хранилище и требует мощного железа. Loki этот индекс не строит, поэтому хранение получается примерно в 10 раз дешевле, агент (`Promtail`) лёгкий, а интеграция с `Grafana` нативная. Плата за это — поиск по содержимому медленнее: Loki сначала отбирает чанки по меткам, а потом grep'ает их линейно.

Два стека по звеньям:

- **ELK**: `Filebeat` → `Logstash` → `Elasticsearch` (полнотекстовый индекс) → `Kibana`.
- **PLG**: `Promtail` → `Loki` (индексирует только labels) → `Grafana`.

Сравнение стеков:

| Критерий | `ELK` (Elasticsearch) | `PLG` (Loki) |
|----------|----------------------|-------------|
| Индексирование | Полнотекстовое | Только labels |
| Стоимость хранения | Высокая | Низкая (~10x дешевле) |
| Поиск по содержимому | Быстрый | Медленнее (grep по чанкам) |
| Интеграция с Grafana | Через плагин | Нативная |
| Память агента | ~500 MB (Logstash) | ~1-10 MB (Promtail) |
| Идеален для | Аудит, сложные запросы | Kubernetes, микросервисы |

Конфигурация `Loki` retention + storage:

```yaml
# loki-config.yml
compactor:
  retention_enabled: true

limits_config:
  retention_period: 720h   # 30 дней по умолчанию
  # Дольше для audit-логов — задаётся per-stream через stream selectors

schema_config:
  configs:
    - from: 2024-01-01
      store: tsdb
      object_store: s3
      schema: v13
      index:
        prefix: loki_index_
        period: 24h
```

`LogQL` запросы в `Grafana`:

```logql
# Все ERROR за последний час с traceId
{service="order-service", env="prod"} |= "ERROR" | json | traceId != ""

# Rate ошибок по сервису
rate({env="prod"} |= "ERROR" [5m])
```

Выбор: `ELK` — если нужен полнотекстовый поиск по payload и сложные агрегации; `Loki` — если приоритет cost-эффективность и интеграция с `Grafana`/`Prometheus`.

## Q33. Как динамически менять уровень логирования без рестарта (Actuator, JMX)?

Зачем это вообще нужно: уровень логирования в `Logback` обычно фиксируется в конфиге на старте, но в prod перезапускать сервис ради включения `DEBUG` для разовой отладки — недопустимо (теряем трафик, рестарт может скрыть саму проблему). Поэтому уровень меняют в рантайме. Три способа, от рекомендуемого к крайнему:

**1. Spring Boot Actuator (рекомендуется):**

```bash
# Просмотр текущего уровня пакета
curl http://localhost:8080/actuator/loggers/com.myapp.service

# Установка DEBUG без рестарта
curl -X POST http://localhost:8080/actuator/loggers/com.myapp.service \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'

# Сброс к исходному значению
curl -X POST http://localhost:8080/actuator/loggers/com.myapp.service \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": null}'
```

Требуется открыть endpoint в конфигурации:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: loggers
  endpoint:
    loggers:
      enabled: true
```

**2. JMX (для не-HTTP приложений):**

```java
// LoggerContext через JMX
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
// Logback регистрирует MBean: ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator
// Инструменты: jconsole, jmxterm
```

**3. Программное изменение (крайний случай):**

```java
@RestController
@RequestMapping("/admin/logging")
public class LogLevelController {

    @PostMapping
    public void setLevel(@RequestParam String logger, @RequestParam String level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(logger).setLevel(Level.toLevel(level));
    }
}
```

Важно: изменение через `Actuator` не сохраняется при рестарте — это временная мера для расследования. После инцидента уровень возвращают обратно.

## Q34. Как реализовать rate-limiting логов при шторме ошибок?

Error storm — это когда одна сбойная зависимость (упал внешний API, отвалилась БД) порождает сотни тысяч **одинаковых** `ERROR` в секунду. Парадокс: логи, которые должны помочь, делают хуже — забивают хранилище, раздувают счёт и прячут другие, действительно новые ошибки в потоке дублей.

Ключевая идея всех приёмов — **не терять информацию, а схлопывать повторы**: первое вхождение ошибки логируем полностью со stack trace, остальные за окно времени либо считаем счётчиком, либо превращаем в метрику. Подходы от простого к надёжному:

**1. `RateLimiter` из Guava (простой счётчик):**

```java
@Slf4j
@Service
public class ExternalApiClient {

    // Не более 1 ERROR-лога в секунду для одного типа ошибки
    private final RateLimiter errorLogLimiter = RateLimiter.create(1.0);

    public Response callApi(Request req) {
        try {
            return httpClient.send(req);
        } catch (IOException e) {
            if (errorLogLimiter.tryAcquire()) {
                log.error("External API unavailable: host={}", req.getHost(), e);
            } else {
                // Счётчик без stack trace — не теряем информацию
                log.warn("External API error (rate-limited): host={}", req.getHost());
            }
            throw new ServiceUnavailableException(e);
        }
    }
}
```

**2. Дедупликация через `TurboFilter` в Logback:**

```java
public class DeduplicatingTurboFilter extends TurboFilter {

    private final Cache<String, Long> recentMessages =
        Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    @Override
    public FilterReply decide(Marker marker, Logger logger,
                               Level level, String format,
                               Object[] params, Throwable t) {
        if (!level.isGreaterOrEqual(Level.ERROR)) {
            return FilterReply.NEUTRAL;
        }
        // Ключ дедупликации — logger + format
        String key = logger.getName() + "|" + format;
        Long lastSeen = recentMessages.getIfPresent(key);
        if (lastSeen != null) {
            return FilterReply.DENY;  // уже логировали недавно
        }
        recentMessages.put(key, System.currentTimeMillis());
        return FilterReply.NEUTRAL;
    }
}
```

Регистрация в `logback-spring.xml`:

```xml
<turboFilter class="com.myapp.logging.DeduplicatingTurboFilter"/>
```

**3. Метрика вместо лога для высокочастотных событий:**

```java
// Не логируем каждую ошибку — считаем метрику
meterRegistry.counter("api.errors", "host", host).increment();
// Раз в минуту — один агрегированный лог
```

Правило: `ERROR` всегда без rate-limiting, если это уникальное событие; rate-limiting только для повторяющихся ошибок одного типа.

## Q35. Как связать distributed tracing с логами через traceId в OpenTelemetry?

`OpenTelemetry` (OTel) — это вендоронезависимый стандарт сбора всех трёх сигналов observability (трейсы, метрики, логи) и их propagation между сервисами. Связь логов с трассировкой работает за счёт двух механизмов: внутри сервиса OTel/`Micrometer` кладёт текущий `traceId` в `MDC` (и он автоматически попадает в каждый лог), а между сервисами `traceId` пробрасывается через стандартный заголовок `W3C traceparent` — поэтому в распределённом запросе у всех сервисов один общий `traceId`.

Итог: в `Grafana` из проблемного трейса в `Tempo` по `traceId` мгновенно переходите к логам в `Loki` за тот же запрос. Главное преимущество перед `Sleuth`/`Zipkin` — OTel не привязывает к конкретному бэкенду: один и тот же экспорт через OTLP можно направить в Tempo, Jaeger или коммерческий APM.

Поток сигналов в OTel-сетапе:

- `Service A` → (`traceId` propagation через `W3C traceparent`) → `Service B`.
- `Service A` и `Service B` оба экспортируют по `OTLP` в `OTel Collector`.
- `OTel Collector` → `Grafana Tempo` (traces) и `Loki` (logs).
- `Grafana Tempo` → (`traceId` link) → `Loki`: из трейса по `traceId` переходим к логам.

Настройка `Spring Boot 3` + `Micrometer Tracing` + `OpenTelemetry`:

```groovy
// build.gradle
implementation 'io.micrometer:micrometer-tracing-bridge-otel'
implementation 'io.opentelemetry:opentelemetry-exporter-otlp'
```

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 0.1   # 10% в prod, 100% в dev

spring:
  application:
    name: order-service

logging:
  pattern:
    # traceId и spanId автоматически в MDC через Micrometer
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

`traceId` в MDC автоматически попадает в `JSON`-лог через `LogstashEncoder`:

```json
{
  "@timestamp": "2026-04-13T12:00:00Z",
  "level": "INFO",
  "message": "Order created",
  "traceId": "4bf92f3577b34da6a3ce929d0e0e4736",
  "spanId": "00f067aa0ba902b7",
  "service": "order-service"
}
```

В `Grafana`: переход от трейса к логам — через кнопку «Logs» в `Tempo`, запрос `{service="order-service"} | json | traceId="4bf92f3577b34da6a3ce929d0e0e4736"`.

Заголовки propagation:
- `W3C Trace Context` (`traceparent`, `tracestate`) — современный стандарт
- `B3` (`X-B3-TraceId`, `X-B3-SpanId`) — исторический формат `Zipkin`/`Sleuth`

## Q36. Как логировать производительность (performance logging) без деградации?

Парадокс performance logging: вы добавляете логи, чтобы измерить производительность, и этими же логами её ухудшаете. Каждый вызов на hot path — это форматирование строки, постановка в буфер и потенциальный I/O. Поэтому ключевой принцип — **писать в логи как можно меньше, а измерения отдавать метрикам**, которые для этого и созданы.

Три безопасных паттерна, упорядоченных по предпочтительности:

- **Логировать только аномалии** (latency threshold) — пишем `WARN` лишь когда операция превысила порог, а не каждый вызов.
- **Метрика вместо лога** (`@Timed`) — перцентили latency уходят в `Micrometer`/`Prometheus`, а не в текст.
- **Batch logging** — агрегируем в памяти и сбрасываем сводку раз в минуту, а не пишем каждое событие.

**1. Логирование только медленных операций (latency threshold):**

```java
@Slf4j
@Component
public class SlowQueryLogger {

    private static final long SLOW_QUERY_THRESHOLD_MS = 200;

    public <T> T executeWithLogging(String operation, Supplier<T> action) {
        long start = System.currentTimeMillis();
        try {
            return action.get();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > SLOW_QUERY_THRESHOLD_MS) {
                log.warn("Slow operation: name={} elapsed={}ms", operation, elapsed);
            } else if (log.isDebugEnabled()) {
                log.debug("Operation: name={} elapsed={}ms", operation, elapsed);
            }
        }
    }
}
```

**2. Аннотация `@Timed` + метрика вместо лога:**

```java
@Timed(value = "payment.process", percentiles = {0.5, 0.95, 0.99})
public PaymentResult processPayment(PaymentRequest req) {
    // Детали производительности — в метрики, не в логи
    return gateway.charge(req);
}
```

**3. Batch logging — агрегация в памяти:**

```java
@Scheduled(fixedDelay = 60_000)  // каждую минуту
public void flushStats() {
    log.info("Processing stats: count={} avgMs={} p99Ms={}",
        counter.getAndSet(0), avgLatency.get(), p99Latency.get());
}
```

**Измеримые показатели влияния логирования:**

| Сценарий | Влияние на latency |
|----------|--------------------|
| `log.debug(...)` при отключённом DEBUG | < 10 нс (только проверка уровня) |
| Конкатенация строк при отключённом DEBUG | 50-500 нс (toString() выполняется) |
| Синхронная запись в файл | 1-10 мс (блокирующий I/O) |
| Async appender, очередь не полна | < 1 мкс |
| Async appender, очередь полна | блокировка до 100 мс |

Ключевое правило: параметризованные вызовы + async appender + `DEBUG` отключён в prod = нулевой overhead от логирования.

## Q37. Как соблюдать GDPR при логировании (PII, маскирование, аудит)?

`GDPR` (General Data Protection Regulation) рассматривает логи как обычное хранилище персональных данных (PII) — имена, email, IP, номера карт. Из этого следуют три практических требования к логированию: **минимизация** (не писать PII без явной необходимости), **защита** (маскировать то, что всё же попало), **управляемое хранение и удаление** (retention + право на стирание). Грубо: чем меньше PII в логах, тем меньше юридических рисков.

Стратегии по этим требованиям:

**1. Allowlist полей для структурированных логов:**

```java
// Никогда не логировать: password, email, phone, SSN, creditCard, token
// Логировать безопасно: userId (внутренний ID), orderId, traceId, action

// НЕЛЬЗЯ:
log.info("User login: email={} password={}", email, password);

// МОЖНО:
log.info("User login: userId={} success={}", user.getId(), success);
```

**2. `@ToString.Exclude` и `@JsonIgnore` для защиты на уровне класса:**

```java
@Data
@Builder
public class UserProfile {
    private Long id;
    private String username;

    @ToString.Exclude   // не попадёт в log.info("User: {}", user)
    private String email;

    @ToString.Exclude
    private String phoneNumber;
}
```

**3. Маскирование через `MaskingJsonGeneratorDecorator` (logstash-logback-encoder):**

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <jsonGeneratorDecorator
        class="net.logstash.logback.mask.MaskingJsonGeneratorDecorator">
        <valueMasker class="net.logstash.logback.mask.RegexValueMasker">
            <!-- Маскировать по именам полей -->
            <regex>(?i)(password|token|secret|apiKey|authorization)</regex>
            <mask>***MASKED***</mask>
        </valueMasker>
        <!-- Маскировать номера карт -->
        <valueMasker class="net.logstash.logback.mask.RegexValueMasker">
            <regex>\b\d{13,19}\b</regex>
            <mask>****-****-****-XXXX</mask>
        </valueMasker>
    </jsonGeneratorDecorator>
</encoder>
```

**4. Retention и право на удаление:**

| Тип данных | Retention | Основание |
|-----------|-----------|-----------|
| Технические логи (без PII) | 30 дней | Внутренняя политика |
| Audit logs (userId, action) | 1 год | Compliance |
| Логи с PII | Минимально необходимо | GDPR Art. 5(e) |

Правило: если сомневаетесь, логировать ли поле — не логируйте. Регулятор штрафует за наличие, а не за отсутствие данных.

## Q38. Как тестировать стратегию логирования в Spring Boot?

Логи воспринимают как «не код», который не нужно тестировать, — и зря. Для двух классов событий логирование является **частью контракта сервиса** и должно покрываться тестами: критичные события (создание заказа, платёж, вход) обязаны логироваться, а чувствительные данные (пароли, номера карт, email) — гарантированно НЕ попадать в логи. Тест ловит регресс, когда кто-то случайно добавил `log.info("...", user)` с PII внутри.

Инструменты — от unit к интеграционному:

- **`MemoryAppender`** (`ListAppender`) — перехватывает события конкретного логгера в памяти, удобно для unit-тестов: проверяем, что нужное сообщение записано, а карта/пароль — нет.
- **`OutputCaptureExtension`** — захватывает реальный stdout в `@SpringBootTest`, проверяет сквозные сценарии (например, что `traceId` действительно доезжает до лога).

**1. `MemoryAppender` для unit-тестов:**

```java
public class MemoryAppender extends ListAppender<ILoggingEvent> {

    public void reset() {
        this.list.clear();
    }

    public boolean contains(String string, Level level) {
        return this.list.stream()
            .anyMatch(event -> event.getMessage().contains(string)
                && event.getLevel().equals(level));
    }

    public int countEventsForLogger(String loggerName) {
        return (int) this.list.stream()
            .filter(event -> event.getLoggerName().contains(loggerName))
            .count();
    }
}

@ExtendWith(MockitoExtension.class)
class OrderServiceLoggingTest {

    private MemoryAppender memoryAppender;

    @BeforeEach
    void setUp() {
        memoryAppender = new MemoryAppender();
        memoryAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(OrderService.class);
        logger.addAppender(memoryAppender);
        logger.setLevel(Level.DEBUG);
    }

    @Test
    void shouldLogOrderCreation() {
        orderService.createOrder(new OrderRequest("user-123", 99.99));
        assertThat(memoryAppender.contains("Order created", Level.INFO)).isTrue();
    }

    @Test
    void shouldNotLogSensitiveData() {
        orderService.processPayment(new PaymentRequest("user-123", "4111111111111111"));
        boolean cardInLogs = memoryAppender.list.stream()
            .anyMatch(e -> e.getFormattedMessage().contains("4111111111111111"));
        assertThat(cardInLogs).isFalse();
    }
}
```

**2. `@SpringBootTest` с `OutputCaptureExtension`:**

```java
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class OrderControllerLoggingIntegrationTest {

    @Test
    void shouldIncludeTraceIdInLogs(CapturedOutput output) {
        webTestClient.post().uri("/orders")
            .header("X-Trace-Id", "test-trace-123")
            .bodyValue(orderRequest)
            .exchange()
            .expectStatus().isCreated();

        assertThat(output.getOut()).contains("test-trace-123");
    }
}
```

**3. Проверка отсутствия чувствительных полей в JSON-логах:**

```java
@Test
void jsonLogShouldNotContainPassword(CapturedOutput output) {
    authService.login("user@example.com", "my-secret-password");
    assertThat(output.getOut()).doesNotContain("my-secret-password");
    assertThat(output.getOut()).doesNotContain("user@example.com");
}
```

Тестирование логирования добавляют в регрессионный набор — особенно для аутентификации, платежей и персональных данных.

## See also

- [Logging](../logging/logging-interview.md) — инструментальные вопросы: `SLF4J`, `Logback`, `MDC`, настройка аппендеров и энкодеров
- [Метрики и трейсинг](metrics-tracing-interview.md) — как метрики и трейсы дополняют логи в полной observability-картине
- [Observability](observability-interview.md) — три столпа (логи, метрики, трейсы), `SLI`/`SLO`/`SLA`, алертинг
- [Распределённые системы](../architecture/distributed-systems-interview.md) — correlation ID, fault tolerance, сценарии потери сообщений
- [Микросервисы](../architecture/microservices-interview.md) — где централизованное логирование обязательно и как его организовать
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — конфигурация `logback-spring.xml`, профили, `spring-boot-starter-logging`
- [Kubernetes](../devops/kubernetes-interview.md) — `stdout`/`stderr` стратегия, Fluentd/Fluent Bit, агрегация логов в кластере

- [Jaeger и Zipkin](jaeger-zipkin-interview.md)
- [Loki и Grafana](loki-grafana-interview.md)
- [Метрики и трейсинг](metrics-tracing-interview.md)
- [Observability](observability-interview.md)
- [OpenTelemetry](opentelemetry-interview.md)
