---
title: "Вопросы на собеседовании: Метрики и трейсинг"
description: "Комплексное руководство по вопросам собеседования на тему метрик и трейсинга для Senior Java Developer."
tags:
  - interview
  - monitoring
  - metrics-tracing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Метрики и трейсинг"
  - "Metrics and tracing interview"
prerequisites: []
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: Метрики и трейсинг

Комплексное руководство по вопросам собеседования на тему метрик и трейсинга для `Senior Java Developer`.

**Метрики и трейсинг** — два из трёх столпов наблюдаемости (observability) наряду с логированием. Вопросы по этой теме регулярно встречаются на собеседованиях для senior-позиций, так как работа с `Prometheus`, `Micrometer`, `OpenTelemetry`, `Jaeger` и `Grafana` — повседневная задача в микросервисной архитектуре. Подробнее о наблюдаемости в целом — в [вопросах по Observability](observability-interview.md).

## Полезные ссылки

### Официальная документация

- [OpenTelemetry Documentation](https://opentelemetry.io/docs/) — стандарт инструментирования
- [Prometheus Documentation](https://prometheus.io/docs/) — система мониторинга
- [Micrometer Documentation](https://micrometer.io/docs) — фасад метрик для Java
- [Grafana Documentation](https://grafana.com/docs/) — визуализация и дашборды
- [Jaeger Documentation](https://www.jaegertracing.io/docs/) — распределённая трассировка
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) — эндпоинты мониторинга
- [Monitor a Spring Boot App Using Prometheus](https://www.baeldung.com/spring-boot-prometheus) — интеграция Spring Boot с Prometheus
- [Quick Guide to Micrometer](https://www.baeldung.com/micrometer) — обзор Micrometer: метрики для JVM
- [Spring Cloud - Tracing Services with Zipkin](https://www.baeldung.com/tracing-services-with-zipkin) — трассировка с Zipkin и Spring Cloud
- [OpenTelemetry Setup in Spring Boot Application](https://www.baeldung.com/spring-boot-opentelemetry-setup) — настройка OpenTelemetry в Spring Boot

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы: метрики vs логи vs трейсы**
- [Q1. (!) Чем метрики отличаются от логов и трейсов?](#q1--чем-метрики-отличаются-от-логов-и-трейсов)
- [Q19. Что такое RED и USE методологии для метрик?](#q19-что-такое-red-и-use-методологии-для-метрик)
- [Q25. Как выбрать между метриками и трейсами для отладки?](#q25-как-выбрать-между-метриками-и-трейсами-для-отладки)
- [Q26. Что такое RED-метрики и USE-метрики?](#q26-что-такое-red-метрики-и-use-метрики)

**Prometheus и Micrometer**
- [Q2. (!) Что такое Prometheus и как он собирает метрики?](#q2--что-такое-prometheus-и-как-он-собирает-метрики)
- [Q3. (!) Что такое Micrometer и как он связан с Prometheus?](#q3--что-такое-micrometer-и-как-он-связан-с-prometheus)
- [Q4. (!) Какие типы метрик бывают (counter, gauge, histogram, summary)?](#q4--какие-типы-метрик-бывают-counter-gauge-histogram-summary)
- [Q10. Как интегрировать Micrometer с Spring Boot?](#q10-как-интегрировать-micrometer-с-spring-boot)
- [Q11. Что такое экспоненциальные гистограммы в метриках?](#q11-что-такое-экспоненциальные-гистограммы-в-метриках)
- [Q12. Как настроить метрики JVM (память, потоки, GC)?](#q12-как-настроить-метрики-jvm-память-потоки-gc)
- [Q13. Что такое custom metrics и когда их добавлять?](#q13-что-такое-custom-metrics-и-когда-их-добавлять)
- [Q17. (!) Что такое cardinality метрик и почему она важна?](#q17--что-такое-cardinality-метрик-и-почему-она-важна)
- [Q20. Как экспортировать метрики в Prometheus из Java?](#q20-как-экспортировать-метрики-в-prometheus-из-java)

**Распределённая трассировка**
- [Q5. (!) Что такое распределённая трассировка и зачем она нужна?](#q5--что-такое-распределённая-трассировка-и-зачем-она-нужна)
- [Q6. (!) Что такое OpenTelemetry и как он связан с трассировкой?](#q6--что-такое-opentelemetry-и-как-он-связан-с-трассировкой)
- [Q7. Что такое span и trace в трассировке?](#q7-что-такое-span-и-trace-в-трассировке)
- [Q8. Как передать контекст трассировки между микросервисами?](#q8-как-передать-контекст-трассировки-между-микросервисами)
- [Q15. Что такое Jaeger и Zipkin и чем они отличаются?](#q15-что-такое-jaeger-и-zipkin-и-чем-они-отличаются)
- [Q16. Как настроить трассировку в Spring Boot (OpenTelemetry)?](#q16-как-настроить-трассировку-в-spring-boot-opentelemetry)
- [Q23. Что такое traceId и spanId и как они связаны?](#q23-что-такое-traceid-и-spanid-и-как-они-связаны)

**Sampling и оптимизация**
- [Q9. Что такое sampling в трассировке и когда его применять?](#q9-что-такое-sampling-в-трассировке-и-когда-его-применять)
- [Q24. Как уменьшить поток данных трассировки до хранилища?](#q24-как-уменьшить-поток-данных-трассировки-до-хранилища)
- [Q27. Как уменьшить объём и стоимость хранения трейсов в production?](#q27-как-уменьшить-объём-и-стоимость-хранения-трейсов-в-production)
- [Q28. Как выбрать head-based и tail-based sampling для production?](#q28-как-выбрать-head-based-и-tail-based-sampling-для-production)

**Связь метрик и трейсов, алертинг**
- [Q14. (!) Как связать метрики и трейсы (например, в Grafana)?](#q14--как-связать-метрики-и-трейсы-например-в-grafana)
- [Q18. Как метрики и трейсы помогают при инцидентах?](#q18-как-метрики-и-трейсы-помогают-при-инцидентах)
- [Q21. Что такое exemplars и зачем они нужны?](#q21-что-такое-exemplars-и-зачем-они-нужны)
- [Q22. Как настроить алерты по метрикам (Prometheus, Alertmanager)?](#q22-как-настроить-алерты-по-метрикам-prometheus-alertmanager)
- [Q29. Как связать метрики приложения с бизнес-метриками?](#q29-как-связать-метрики-приложения-с-бизнес-метриками)
- [Q30. Как организовать алертинг по метрикам и трейсам?](#q30-как-организовать-алертинг-по-метрикам-и-трейсам)

**W3C TraceContext, Zipkin, бизнес-метрики**
- [Q31. (!) Что такое W3C TraceContext и зачем это стандарт?](#q31--что-такое-w3c-tracecontext-и-зачем-это-стандарт)
- [Q32. Как настроить Micrometer с кастомными тегами для бизнес-метрик?](#q32-как-настроить-micrometer-с-кастомными-тегами-для-бизнес-метрик)
- [Q33. Как интегрировать Zipkin в Spring Boot приложение?](#q33-как-интегрировать-zipkin-в-spring-boot-приложение)

**Расширенные темы: Micrometer, OpenTelemetry, SLI/SLO**
- [Q34. (!) Какие типы инструментов предоставляет Micrometer — Counter, Gauge, Timer, DistributionSummary?](#q34--какие-типы-инструментов-предоставляет-micrometer--counter-gauge-timer-distributionsummary)
- [Q35. Что такое OpenTelemetry Collector и как его использовать?](#q35-что-такое-opentelemetry-collector-и-как-его-использовать)
- [Q36. (!) Что такое SLI, SLO и SLA — определения, расчёт, error budget?](#q36--что-такое-sli-slo-и-sla--определения-расчёт-error-budget)
- [Q37. Как работает tail-based sampling в трассировке?](#q37-как-работает-tail-based-sampling-в-трассировке)
- [Q38. Что такое Exemplars и как они связывают метрики с трейсами?](#q38-что-такое-exemplars-и-как-они-связывают-метрики-с-трейсами)
- [Q39. (!) Что такое проблема высокой кардинальности в метриках и как её избежать?](#q39--что-такое-проблема-высокой-кардинальности-в-метриках-и-как-её-избежать)
- [Q40. Как реализовать context propagation в distributed tracing — W3C TraceContext и B3?](#q40-как-реализовать-context-propagation-в-distributed-tracing--w3c-tracecontext-и-b3)
- [Q41. Какие SLI использовать для разных типов сервисов?](#q41-какие-sli-использовать-для-разных-типов-сервисов)

## Метрики

## Q1. (!) Чем метрики отличаются от логов и трейсов?

Это три разных сигнала наблюдаемости, и различаются они тем, на какой вопрос отвечают. Коротко: метрики говорят «что происходит в целом», трейсы — «где в одном запросе ушло время», логи — «что именно случилось в конкретный момент».

- **Метрики** — агрегированные числа во времени (счётчики, gauge, гистограммы). Малый объём, дёшево хранить долго; основа для алертов и дашбордов. Отвечают на «насколько плохо», но не «почему».
- **Логи** — дискретные события с текстом и контекстом. Большой объём; для отладки и аудита конкретных случаев. Дают детали, но по ним тяжело увидеть общую картину.
- **Трейсы** — цепочка операций (spans) одного запроса по мере прохождения через сервисы. Показывают задержки по узлам и зависимости между вызовами. Отвечают на «где именно медленно».

Сигналы не заменяют, а дополняют друг друга и связаны общим `traceId`: по метрике видишь проблему, по трейсу — какой участок виноват, по логам (отфильтрованным по тому же `traceId`) — детали.

```mermaid
graph TD
    subgraph "Три столпа Observability"
        M["Метрики<br/>Агрегированные числа<br/>Counter, Gauge, Histogram"]
        L["Логи<br/>Дискретные события<br/>Текст + контекст"]
        T["Трейсы<br/>Цепочки операций<br/>Spans + traceId"]
    end

    M -- "exemplar → traceId" --> T
    T -- "traceId → фильтрация" --> L
    L -- "агрегация → метрики" --> M

    A["Алерт:<br/>rate(errors) > 0.01"] --> M
    M --> D["Дашборд Grafana"]
    T --> J["Jaeger / Tempo"]
    L --> E["ELK / Loki"]
```

**Типичный сценарий расследования инцидента:** алерт по метрике (например, рост ошибок) -> открыть трейсы за период -> найти медленные/ошибочные запросы по `traceId` -> по `traceId` отфильтровать логи и локализовать причину. Подробнее о стратегиях логирования — в [вопросах по логированию](logging-strategies-interview.md).

## Q2. (!) Что такое Prometheus и как он собирает метрики?

**Prometheus** собирает и хранит временные ряды и выполняет запросы `PromQL`. Обычная схема сбора — **pull**: сервер периодически опрашивает HTTP-endpoints приложения или exporter. Scrape получает снимок экспонируемых измерений, а не отдельное сетевое сообщение на каждое изменение счётчика. Формат согласуется при опросе: поддерживаются текстовые варианты и protobuf, а не единственный текстовый протокол.

Неудачный scrape даёт `up=0` для цели, но не доказывает остановку процесса: причиной могут быть сеть, timeout, путь endpoint, TLS или авторизация. Проверяйте ошибку опроса и состояние цели отдельно. [Jobs and instances](https://prometheus.io/docs/concepts/jobs_instances/).

```mermaid
graph LR
    P["Prometheus"] -- "HTTP scrape" --> A["Приложение / exporter"]
    G["Grafana"] -- "PromQL-запросы" --> P
    P -- "Сработавшие правила" --> AM["Alertmanager"]
    AM -- "Уведомления" --> N["Получатель"]
```

**Минимальный пример scrape-конфигурации**; 15 секунд — выбранный интервал примера, endpoint приложения настраивается отдельно:

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-apps'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['order-service:8080', 'payment-service:8080']
        labels:
          env: 'production'
```

Service discovery помогает находить динамические targets. `ServiceMonitor`/`PodMonitor` обрабатывает **Prometheus Operator**; сами аннотации Kubernetes не меняют конфигурацию обычного сервера без соответствующей интеграции или relabeling. Короткая задача может завершиться между scrape даже при корректном обнаружении. Для подходящих service-level batch jobs рассматривают Pushgateway. В Prometheus также есть отдельно включаемые remote-write/OTLP receivers; совместимость и ограничения приёма проверяют для выбранной версии.

[Prometheus overview](https://prometheus.io/docs/introduction/overview/), [receiver API](https://prometheus.io/docs/prometheus/latest/querying/api/#remote-write-receiver), [content negotiation](https://prometheus.io/docs/instrumenting/content_negotiation/), [Prometheus Operator](https://prometheus-operator.dev/docs/developer/getting-started/), [Pushgateway use cases](https://prometheus.io/docs/practices/pushing/).

## Q3. (!) Что такое Micrometer и как он связан с Prometheus?

`Micrometer` — API измерений JVM-приложения: код регистрирует counters, gauges, timers через `MeterRegistry`, а registry адаптирует их к backend экспорта. Registry хранит состояние измерений в процессе; долговременная TSDB и интерфейс дашбордов — другие компоненты. Смена backend часто сохраняет код инструментации, но требует проверить конфигурацию экспорта, единицы и поддерживаемые статистики.

`micrometer-registry-prometheus` подготавливает scrape-экспозицию. **Сам registry не открывает HTTP endpoint.** В Spring Boot с Actuator нужны соответствующие зависимости, включённый endpoint, web exposure и доступ scrape-клиента. При стандартном base path это `/actuator/prometheus`. Ниже пример для Boot 3.3.x; версии зависимостей задаёт BOM Boot:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
}
```

```yaml
spring:
  application:
    name: order-service
management:
  endpoints:
    web:
      exposure:
        include: health, prometheus
  metrics:
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms, 500ms, 1s, 5s
```

Здесь `slo` задаёт границы дополнительных buckets для целей задержки. В Boot 3.3.x по умолчанию экспонирован только health; наличие registry ещё не означает доступный scrape URL. Настройки security, management port и base path тоже влияют на доступ.

**Кастомный счётчик** с переданным приложению `MeterRegistry registry`:

```java
Counter completed = registry.counter("orders.completed", "result", "success");
completed.increment();
```

Наличие автоматических JVM/HTTP/pool meters зависит от binders, библиотек, бинов и конфигурации, а не гарантируется для любого приложения. Бюджет labels и нормализация значений разобраны в [Q17](#q17--что-такое-cardinality-метрик-и-почему-она-важна).

[Spring Boot 3.3 metrics](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html), [endpoint exposure](https://docs.spring.io/spring-boot/3.3/reference/actuator/endpoints.html#actuator.endpoints.exposing), [Micrometer Prometheus](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html).

## Q4. (!) Какие типы метрик бывают (counter, gauge, histogram, summary)?

Тип выбирают по смыслу измерения и запросам к данным. Название API Micrometer и тип экспорта Prometheus не всегда соответствуют друг другу один к одному.

| Тип Prometheus | Что представляет | Пример / API Micrometer |
|----------------|------------------|-------------------------|
| **Counter** | Накопление событий; обычное уменьшение не моделирует, но возможен reset | Завершённые запросы — `Counter` |
| **Gauge** | Текущее состояние, способное расти и падать | Размер очереди — `Gauge` |
| **Histogram** | Агрегированное распределение и связанные count/sum | Длительности — `Timer`, размеры — `DistributionSummary`, с histogram-настройками |
| **Summary** | Count/sum и, если настроены, локальные квантили | `Timer`/`DistributionSummary` с client-side percentiles |

Пример с заранее переданным `MeterRegistry registry` (imports опущены):

```java
Counter requests = registry.counter("http.requests", "endpoint", "/orders");
requests.increment();

AtomicInteger queueSize = new AtomicInteger(3);
Gauge.builder("queue.size", queueSize, AtomicInteger::get).register(registry);

// Histogram для серверной оценки распределения
Timer duration = Timer.builder("request.duration")
    .publishPercentileHistogram()
    .register(registry);
duration.record(12, TimeUnit.MILLISECONDS);

DistributionSummary payload = DistributionSummary.builder("response.size")
    .baseUnit("bytes")
    .publishPercentileHistogram()
    .register(registry);
payload.record(512);

// Отдельный meter с локальными percentile-оценками
Timer localDuration = Timer.builder("request.local.duration")
    .publishPercentiles(0.5, 0.95)
    .register(registry);
localDuration.record(12, TimeUnit.MILLISECONDS);
```

Источник gauge вроде `queueSize` должен оставаться сильно достижимым в приложении; meter обычно удерживает его слабо. Gauge показывает состояние при наблюдении и может не сохранить промежуточный пик. Counter после рестарта может начать отсчёт заново — это отличается от обычного уменьшения gauge.

**Агрегируемость:** локальные p99 двух инстансов в общем случае нельзя усреднить и получить p99 сервиса. При этом `sum` и `count` Summary можно складывать при совместимых единицах и scope. Название `DistributionSummary` в Micrometer не означает, что экспорт всегда будет Prometheus Summary: результат зависит от статистик и registry.

Histogram не хранит доступный список исходных наблюдений. У classic histogram экспонируются bucket-ряды; native histogram представляет составной sample. Совместимые histogram-данные можно агрегировать, но `histogram_quantile` обычно даёт **оценку**, точность которой зависит от представления и разрешения. При объединении проверяют единицы, bucket-схему и корректный учёт resets; это не восстановление исходных значений «без потерь».

[Prometheus histograms and summaries](https://prometheus.io/docs/practices/histograms/), [Micrometer histograms/percentiles](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html), [gauges](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html).

## Q5. (!) Что такое распределённая трассировка и зачем она нужна?

Распределённая трассировка связывает **наблюдаемые операции** запроса между сервисами. Span описывает операцию, её временные границы и атрибуты; trace ID и связи parent/child помогают восстановить путь. Это позволяет локализовать участок задержки в конкретном запросе, но не гарантирует полное покрытие каждого вызова или единственную корневую причину.

```mermaid
sequenceDiagram
    participant G as Gateway
    participant O as Order Service
    participant P as Payment Service
    participant B as Bank API
    G->>O: Запрос и trace context
    O->>P: Вызов и trace context
    P->>B: Вызов и trace context
    B-->>P: Результат
    P-->>O: Результат
    O-->>G: Результат
```

Это иллюстрация синхронной цепочки. Асинхронные операции могут связываться через **span links**, в том числе между разными traces; реальное представление не обязано быть одним простым деревом. Длительности вложенных/параллельных spans могут перекрываться, поэтому их механическая сумма не равна задержке запроса.

**При расследовании:** выберите доступный trace известного запроса либо найдите медленные traces за период; сопоставьте наблюдаемые внутренние spans с общей задержкой. Для объяснения причины могут понадобиться логи, метрики или профиль. Фильтр по trace ID выбирает конкретный trace, а фильтр по длительности — медленные запросы.

**Граница:** неполная инструментация, разрыв propagation, sampling и потери доставки делают trace неполным. Отсутствие span не доказывает отсутствие вызова. Полноту и охват проверяют отдельно от красивого дерева в UI.

Примеры backends хранения и просмотра — Jaeger, Zipkin, Tempo; OpenTelemetry предоставляет API/SDK и механизмы сбора, а не обязательное собственное хранилище.

[OpenTelemetry traces](https://opentelemetry.io/docs/concepts/signals/traces/), [context propagation](https://opentelemetry.io/docs/concepts/context-propagation/), [sampling](https://opentelemetry.io/docs/concepts/sampling/).

## Q6. (!) Что такое OpenTelemetry и как он связан с трассировкой?

`OpenTelemetry` (OTel) — открытый стандарт и набор `SDK / API` для всех трёх сигналов: трассировки, метрик и логов. Его идея — инструментировать код один раз через вендоронезависимый API, а конкретный бэкенд выбирать на этапе экспорта. Меняешь бэкенд (`Jaeger`, `Zipkin`, облачный сервис) — код не трогаешь.

Что `OpenTelemetry` делает для трассировки:

- генерирует spans и trace context (`traceId`, `spanId`);
- передаёт контекст между сервисами — через `HTTP`-заголовки, `gRPC` metadata, заголовки сообщений;
- экспортирует собранные трейсы в бэкенд (`Jaeger`, `Zipkin` и др.).

```mermaid
graph TB
    subgraph "Приложение (Spring Boot)"
        SDK["OTel SDK / Agent"]
        AUTO["Авто-инструментация<br/>HTTP, JDBC, Kafka"]
        MANUAL["Ручные spans<br/>@WithSpan, Tracer API"]
    end

    SDK --> OTLP["OTLP Exporter"]
    AUTO --> SDK
    MANUAL --> SDK

    OTLP --> COLL["OTel Collector"]

    COLL --> J["Jaeger / Tempo<br/>(трейсы)"]
    COLL --> P["Prometheus<br/>(метрики)"]
    COLL --> L["Loki / ELK<br/>(логи)"]
```

**Пример кастомного span через OpenTelemetry API:**

```java
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final Tracer tracer;

    public boolean checkAvailability(String productId, int quantity) {
        Span span = tracer.spanBuilder("inventory.checkAvailability")
                .setAttribute("product.id", productId)
                .setAttribute("requested.quantity", quantity)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            boolean available = queryWarehouse(productId, quantity);
            span.setAttribute("result.available", available);
            return available;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

**Пример с аннотацией `@WithSpan` (Spring Boot + OTel):**

```java
@Service
public class NotificationService {

    @WithSpan("notification.send")
    public void sendNotification(
            @SpanAttribute("notification.type") String type,
            @SpanAttribute("notification.recipient") String recipient) {
        // Span создаётся автоматически с указанными атрибутами
        doSend(type, recipient);
    }
}
```

## Q7. Что такое span и trace в трассировке?

`Span` описывает одну операцию: HTTP-вызов, запрос к БД или обработку сообщения. У него есть имя, время начала и окончания, `spanId`, атрибуты и контекст с `traceId`; родитель необязателен у корневого span. `Trace` объединяет связанные spans по общему `traceId`. Корень может описывать HTTP-запрос, запуск задачи или другую операцию. [OpenTelemetry, Traces](https://opentelemetry.io/docs/concepts/signals/traces/).

Связи parent/child обычно изображают деревом. Дополнительные **span links** позволяют связать операции, в том числе из разных traces, например при асинхронной обработке. На схеме ниже показаны связи операций, а не их последовательность во времени:

```mermaid
graph TD
    ROOT["API Gateway: 350 ms"] --> ORDER["Order Service: 300 ms"]
    ORDER --> DB["DB INSERT: 80 ms"]
    ORDER --> PAYMENT["Payment Service: 150 ms"]
    PAYMENT --> BANK["Bank API: 70 ms"]
    ORDER --> KAFKA["Kafka produce: 5 ms"]
```

**Сценарий:** по `traceId` в Jaeger/Zipkin изучают длительности, отношения операций, ошибки и атрибуты. Долгий span помогает выбрать направление расследования, но сам по себе не доказывает причину задержки. Вложенные и параллельные интервалы перекрываются, поэтому их длительности нельзя просто сложить. Неполная инструментация, sampling и потери при доставке могут оставить пробелы в видимом trace — полного пути каждого запроса такая схема не гарантирует.


## Q8. Как передать контекст трассировки между микросервисами?

В HTTP для W3C Trace Context вызывающая сторона **inject**-ит контекст текущего span в заголовки, принимающая — **extract**-ит его и использует как удалённого родителя нового span. Так сохраняется `traceId`, а новый span получает собственный `spanId`. `traceparent` несёт основной контекст; `tracestate` необязателен и предназначен для дополнительного вендорского состояния. Вызывающий span не обязан быть корневым. [W3C Trace Context](https://www.w3.org/TR/trace-context/).

**Валидный пример version `00`:**

```text
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```

Поля значения: версия `00`, `trace-id` из 32 lowercase hex-символов, `parent-id` из 16 и flags из двух. Оба ID не могут состоять только из нулей. Здесь `parent-id` — ID вызывающего span, а не ID создаваемого span получателя. Младший бит flags (`01` в примере) — sampled: он передаёт решение о записи, но не гарантирует, что данные сохранятся в backend.

```mermaid
sequenceDiagram
    participant A as Service A
    participant B as Service B
    Note over A: Текущий вызывающий span
    A->>B: HTTP + traceparent из примера
    Note over B: Extract remote parent<br/>Создать span с новым spanId<br/>и тем же traceId
    B-->>A: HTTP response
```

**Пример настроек Spring Boot 3.3** при подключённых Micrometer Tracing, поддерживаемом bridge/exporter и нужной инструментации:

```yaml
management:
  tracing:
    propagation:
      type: w3c
    sampling:
      probability: 0.1  # пример политики приложения, не универсальная норма
```

Для автоматической HTTP-пропагации Boot рекомендует создавать клиенты через автоконфигурированные `RestTemplateBuilder`, `RestClient.Builder` или `WebClient.Builder`. Произвольно созданный клиент, собственный executor и другие транспорты требуют соответствующей инструментации и переноса контекста; наличие OpenTelemetry само по себе не подключает любой клиент. Локальный `ThreadLocal` не переносит контекст в другой процесс. [Spring Boot 3.3, Propagating Traces](https://docs.spring.io/spring-boot/3.3/reference/actuator/tracing.html#actuator.micrometer-tracing.propagating-traces).


## Q9. Что такое sampling в трассировке и когда его применять?

`Sampling` — выбор трейсов для записи или экспорта. Он помогает ограничить поток и стоимость телеметрии. Вероятностная выборка может представлять общий трафик, а выборка по ошибкам и задержке намеренно смещена к редким случаям; результаты анализа нужно интерпретировать с учётом политики отбора.

Три основных стратегии различаются тем, в какой момент и по какому критерию принимается решение:

| Тип | Когда решение | Плюсы | Минусы |
|-----|---------------|-------|--------|
| **Head-based** | В начале трейса | Простой, предсказуемый overhead | Можно пропустить ошибочные трейсы |
| **Tail-based** | После получения достаточной части трейса | Может отбирать ошибки и выбросы | Нужен компонент с состоянием, буферизация и маршрутизация span'ов одного trace |
| **Rate-limiting** | По квоте (N/сек) | Контролируемая нагрузка | Может пропустить важное; конкретная реализация зависит от SDK или Collector |

```java
// Конфигурация sampling в OpenTelemetry Java SDK
@Configuration
public class TracingConfig {

    @Bean
    public SdkTracerProvider tracerProvider() {
        return SdkTracerProvider.builder()
                // Head-based: 10% трейсов в production
                .setSampler(Sampler.traceIdRatioBased(0.1))
                // Или ParentBased — уважает решение родителя
                // .setSampler(Sampler.parentBased(Sampler.traceIdRatioBased(0.1)))
                .addSpanProcessor(BatchSpanProcessor.builder(
                        OtlpGrpcSpanExporter.builder()
                                .setEndpoint("http://otel-collector:4317")
                                .build()
                ).build())
                .build();
    }
}
```

Долю выбирают по нагрузке, бюджету и целям диагностики; универсальных 1–10% или обязательных 100% для dev нет. Повышение доли во время инцидента помогает увидеть больше будущих трейсов, но не возвращает уже отброшенные. Выбор head/tail для production — Q28, работа tail-sampler — Q37. [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/).

## Q10. Как интегрировать Micrometer с Spring Boot?

Для **Spring Boot 3.3** добавьте `spring-boot-starter-actuator` и `io.micrometer:micrometer-registry-prometheus`, используя версии из dependency management Boot. Автоконфигурация подключает подходящие binders и registry; доступность JVM/HTTP/пуловых метрик зависит также от используемых компонентов и настроек. Затем опубликуйте endpoint и обеспечьте доступ сборщика согласно настройкам сети и безопасности приложения. [Spring Boot 3.3, Metrics](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html).

**Пример `application.yml`:**

```yaml
spring:
  application:
    name: order-service
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,500ms,1s
```

В этой версии свойство называется `management.metrics.distribution.slo`. Настройки распределения применяются к зарегистрированным meters; они не создают события измерения сами. Для Prometheus настраивают scrape `/actuator/prometheus`. Диагностический `/actuator/metrics`, если его отдельно открыть, возвращает сведения о meters и не заменяет scrape endpoint.

**`@Timed` требует работающего перехвата.** Например, для Spring AOP подключают AOP-инфраструктуру (`spring-boot-starter-aop`) и регистрируют аспект:

```java
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MetricsConfig {
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
```

Метод с `@Timed("orders.create")` должен вызываться через соответствующий proxy. При обычном Spring proxy-варианте self-invocation через `this` обходит аспект. Одна аннотация или только объявление bean не гарантирует измерение каждого вызова; допустимы и другие механизмы инструментации, а `Timer` можно записывать программно, как в Q4. [Micrometer, Timers / TimedAspect](https://docs.micrometer.io/micrometer/reference/concepts/timers.html).


## Q11. Что такое экспоненциальные гистограммы в метриках?

У такой гистограммы границы бакетов идут по геометрической шкале: близкие по отношению значения попадают в сопоставимые по относительной ширине интервалы. Это удобно для длительностей с большим диапазоном. Экспоненциальной является **шкала значений**, а не рост числа событий во времени.

Например, в **OpenTelemetry ExponentialHistogram** разрешение задаёт `scale`:

```text
base = 2 ** (2 ** (-scale))
положительный бакет i: (base ** i, base ** (i + 1)]
```

| scale | base | Пример положительных бакетов |
|---|---|---|
| 0 | 2 | (1, 2], (2, 4], (4, 8] |
| 1 | √2 ≈ 1.4142 | (1, √2], (√2, 2], (2, 2√2] |

Чем больше `scale`, тем мельче интервалы. Формат отдельно описывает положительную, отрицательную и нулевую области; таблица иллюстрирует только положительные значения. Выбор разрешения и ограничения числа бакетов определяют компромисс памяти и ошибки. [OTel Metrics Data Model, ExponentialHistogram](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exponentialhistogram).

**Не смешивайте алгоритм и API конфигурации.** Micrometer `publishPercentileHistogram()` публикует бакеты для вычисления квантилей в поддерживающем backend; `minimumExpectedValue`/`maximumExpectedValue` ограничивают диапазон используемых бакетов. Эти вызовы сами по себе не означают выбор OTel ExponentialHistogram или Prometheus native histogram. Обычный `Timer` также не обещает такую экспозицию по умолчанию. Проверяйте конкретные версии SDK/registry, формат экспорта и поддержку backend. Явно заданные SLO-границы могут быть полезнее для конкретного порога; универсального выигрыша любого формата в памяти и точности нет. [Micrometer, Histograms and Percentiles](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html).


## Q12. Как настроить метрики JVM (память, потоки, GC)?

В **Spring Boot 3.3** автоконфигурация JVM binders Micrometer предоставляет метрики памяти, потоков, классов и GC. Для HTTP-экспозиции всё равно нужны подходящий registry, открытый и доступный endpoint из Q10; сам binder не публикует HTTP endpoint. Имена и наличие рядов проверяют в экспозиции выбранной версии и JVM. [Spring Boot 3.3, JVM Metrics](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html#actuator.metrics.supported.jvm).

| Что наблюдать | Примеры в Prometheus-экспозиции | Как интерпретировать |
|---|---|---|
| Память | `jvm_memory_used_bytes`, `jvm_memory_max_bytes` | Различать `area`, pool `id` и instance; max может быть не определён. |
| Потоки | `jvm_threads_live_threads`, `jvm_threads_states_threads` | Текущие количества и состояния потоков, а не доказательство дедлока. |
| GC | `jvm_gc_pause_seconds_sum`, `jvm_gc_pause_seconds_count` | По приростам получать среднее за окно; для хвоста распределения нужны подходящие дополнительные данные. |

В `JvmGcMetrics` **1.13.2** Timer `jvm.gc.pause` создаётся при обработке уведомления GC: до первого подходящего события ряда может ещё не быть. Это не то же самое, что нулевая длительность паузы. [JvmGcMetrics v1.13.2](https://raw.githubusercontent.com/micrometer-metrics/micrometer/v1.13.2/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmGcMetrics.java).

**Примеры PromQL** предполагают наличие соответствующих рядов и одинаковый набор labels у числителя и знаменателя:

```promql
# Заполнение конкретного heap pool > 85%, только при определённом max > 0
jvm_memory_used_bytes{area="heap"}
  / (jvm_memory_max_bytes{area="heap"} > 0) > 0.85

# Средняя длительность GC-паузы за 5 минут > 500 ms, при наличии событий
rate(jvm_gc_pause_seconds_sum[5m])
  / (rate(jvm_gc_pause_seconds_count[5m]) > 0) > 0.5
```

Первое выражение сохраняет разбивку по pool и instance: это не доля заполнения всей heap. Не складывайте used одних pools с max других и не усредняйте отношения разных JVM. Второе выражение показывает **среднюю**, а не максимальную/каждую паузу; отсутствие событий либо данных нельзя объявлять доказательством здоровья. Для алерта отдельно задают окно, длительность срабатывания и проверку отсутствующих данных.

В Grafana панель used/max в байтах должна иметь единицу bytes и осмысленную легенду по instance/pool. Пороги 0.8/0.9 относятся к отношению в диапазоне 0–1, а не к байтам; для такой панели нужны отдельное выражение отношения и согласованная единица.

**Дедлоки:** в исходнике `JvmThreadMetrics` версии **1.13.2** регистрируются peak/daemon/live/started/states, но нет `jvm.threads.deadlocked`. Поэтому алерт `jvm_threads_deadlocked_threads > 0` нельзя считать работающим по умолчанию для этой конфигурации. Отдельную диагностику или пользовательскую метрику нужно реализовать и проверить; это ограниченный вывод по указанной версии. [JvmThreadMetrics v1.13.2](https://raw.githubusercontent.com/micrometer-metrics/micrometer/v1.13.2/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmThreadMetrics.java).

Подробнее о профилировании — в [JVM Performance Tuning](../performance/jvm-performance-tuning-interview.md).


## Q13. Что такое custom metrics и когда их добавлять?

`Custom` metrics — это метрики, которые приложение определяет само под свою предметную область: счётчик заказов, размер очереди задач, бизнес-события. Стандартные метрики (`JVM`, `HTTP`) отвечают на вопрос «здоров ли процесс», но ничего не знают о бизнес-логике. Свои метрики добавляют ровно тогда, когда стандартных не хватает, чтобы поставить алерт или сделать анализ — например, нужно следить за долей отклонённых платежей или ростом очереди.

```java
@Component
@RequiredArgsConstructor
public class BusinessMetrics {

    private final MeterRegistry registry;

    // Счётчик бизнес-событий с лейблами
    public void recordOrderCreated(String region, String paymentMethod) {
        registry.counter("business.orders.created",
                "region", region,
                "payment_method", paymentMethod
        ).increment();
    }

    // Gauge для отслеживания размера очереди
    public void registerQueueMetrics(BlockingQueue<?> queue, String queueName) {
        Gauge.builder("queue.size", queue, BlockingQueue::size)
                .tag("queue", queueName)
                .description("Текущий размер очереди " + queueName)
                .register(registry);
    }

    // FunctionCounter — для значений, которые только растут
    public void registerProcessedMessages(AtomicLong counter, String topic) {
        FunctionCounter.builder("kafka.messages.processed", counter, AtomicLong::get)
                .tag("topic", topic)
                .register(registry);
    }

    // Timer с SLO-бакетами
    public Timer createSloTimer(String operation) {
        return Timer.builder("business." + operation + ".duration")
                .publishPercentiles(0.5, 0.95, 0.99)
                .serviceLevelObjectives(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .register(registry);
    }
}
```

**Регистрация и срок жизни:** создавайте meter один раз для каждого задуманного сочетания имени и tags и сохраняйте источник измерений достижимым. Gauge обычно держит слабую ссылку на наблюдаемый объект; приложение должно владеть очередью дольше регистрации. Он видит состояние в момент наблюдения и может пропустить промежуточный пик. Для `FunctionCounter` источник обязан монотонно расти в течение своего жизненного цикла; произвольное уменьшение `AtomicLong` нарушит это предположение. [Micrometer Gauges](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html), [Function-tracking counters](https://docs.micrometer.io/micrometer/reference/concepts/counters.html).

**Cardinality:** ограничивайте и tags, и переменную часть имени (`operation` в примере). Пользовательские/запросные ID обычно непригодны для меток метрик. Даже конечные `status`, `region`, `payment_method` требуют бюджета всех реально возникающих сочетаний и числа рядов по репликам. Проверяйте размер очереди и исход счётчика, а также экспозицию конкретного registry; регистрация объекта не гарантирует публикацию любого формата.

## Q14. (!) Как связать метрики и трейсы (например, в Grafana)?

Первый способ — сопоставлять **время, сервис и другие атрибуты**: агрегат Prometheus показывает область ухудшения, а поиск в Jaeger/Tempo помогает выбрать отдельные запросы за тот же интервал. Общая TSDB для метрик и traces не обязательна.

Второй способ — **exemplar**, связанный с отдельным наблюдением и содержащий, например, trace ID. При настроенной цепочке instrumentation → export → Prometheus → Grafana он позволяет открыть соответствующий trace. Exemplar — пример наблюдения, а p99 — оценка квантиля по распределению за окно: соседняя точка не доказывает, что именно этот запрос определил p99 или причину всплеска. [Grafana exemplars](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/).

```mermaid
graph LR
    PANEL["Агрегат latency за окно"] --> E["Доступный exemplar наблюдения"]
    E --> T["Поиск trace по ID"]
    T --> L["Логи по контексту, если настроены"]
```

**Пример provisioning связи Prometheus → Tempo:**

```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    uid: prometheus
    type: prometheus
    url: http://prometheus:9090
    jsonData:
      exemplarTraceIdDestinations:
        - name: trace_id
          datasourceUid: tempo
  - name: Tempo
    uid: tempo
    type: tempo
    url: http://tempo:3200
```

Здесь `trace_id` — **пример имени label у exemplar**: замените его на фактическое имя из полученных данных (`traceID`, `trace_id` и другие варианты не взаимозаменяемы по написанию). `datasourceUid` должен совпасть с UID настроенного trace datasource. Это не инструкция добавлять уникальный trace ID в обычные labels временного ряда. Экспозиция, сбор exemplars и хранение traces включаются отдельно. [Grafana Prometheus configuration](https://grafana.com/docs/grafana/latest/datasources/prometheus/configure/).

Для связи с логами отдельно настройте datasource, UID и извлечение ID под реальный формат логов. Ссылка не гарантирует наличие trace: он мог не сохраниться из-за sampling/сбоя или истечь по retention. При отсутствии exemplar используйте поиск по времени и атрибутам; причинную гипотезу проверяйте дополнительными данными.


## Q15. Что такое Jaeger и Zipkin и чем они отличаются?

Это системы для приёма, хранения, поиска и просмотра **traces**. Сравнивайте конкретные версии, поддерживаемые протоколы, storage, запросы/UI, sampling и эксплуатационные требования; универсальный рейтинг «легче/богаче» мало помогает выбрать конфигурацию.

Например, **Jaeger v2** построен на OpenTelemetry Collector framework и дополняет его компонентами хранения и query/UI. В документации **2.22** описан приём traces по OTLP gRPC/HTTP; это не означает хранение всех типов OTel telemetry. Для Zipkin-приёмника можно использовать соответствующий exporter Collector, который отправляет spans в Zipkin API, например `/api/v2/spans`. [Jaeger 2.22 architecture](https://www.jaegertracing.io/docs/2.22/architecture/), [APIs](https://www.jaegertracing.io/docs/2.22/architecture/apis/), [Zipkin exporter](https://raw.githubusercontent.com/open-telemetry/opentelemetry-collector-contrib/main/exporter/zipkinexporter/README.md).

```mermaid
graph LR
    APP["Приложение: OTLP"] --> COL["Collector: receiver + pipeline"]
    COL -->|"согласованный OTLP export"| J["Jaeger"]
    COL -->|"Zipkin exporter"| Z["Zipkin API"]
```

**Пример сетевой топологии:** Collector и Jaeger в разных контейнерах общей bridge-сети могут оба слушать внутренний TCP 4317. Collector экспортирует на `jaeger:4317`; для отправки с host публикуют, например, host 4317 только у Collector, а UI Jaeger — на 16686. Два одинаковых `4317:4317` на одном host IP/TCP одновременно конфликтуют: внутренний listener и публикация host-порта — разные уровни. [Docker port publishing](https://docs.docker.com/engine/network/port-publishing/).

OTel помогает отделить инструментацию от backend, но при смене назначения нужно согласовать exporter, receiver, protocol, endpoint/path, TLS/auth и нужные преобразования данных. Одной замены URL достаточно лишь при совместимых контрактах. Для реального развёртывания отдельно выбирают и проверяют версию образов, конфигурацию, storage и отказоустойчивость; схема выше описывает поток данных.


## Q16. Как настроить трассировку в Spring Boot (OpenTelemetry)?

Есть два распространённых пути: **OpenTelemetry Java Agent** инструментирует поддерживаемые библиотеки при запуске JVM, а **Micrometer Tracing с OTel bridge** использует интеграции приложения/Boot. Выбор зависит от требуемого контроля и поддерживаемых библиотек. Отдельно согласуйте пропагацию, sampling и экспорт; две параллельные инструментирующие системы могут потребовать настройки, чтобы не создавать повторные spans.

**Пример Java Agent 2.x, экспорт только traces по OTLP/HTTP:** файл агента заранее установлен в указанном пути, Collector имеет HTTP receiver на 4318.

```sh
export OTEL_SERVICE_NAME=order-service
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
export OTEL_TRACES_SAMPLER=parentbased_traceidratio
export OTEL_TRACES_SAMPLER_ARG=0.1
java -javaagent:/opt/otel/opentelemetry-javaagent.jar -jar /app/app.jar
```

Agent **2.0+** использует `http/protobuf` по умолчанию; здесь протокол указан явно. Для gRPC надо согласовать `grpc` и gRPC receiver, обычно на 4317: номер порта сам не выбирает протокол. Общий HTTP endpoint — базовый URL; у signal-specific `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` указывают полный путь, обычно `/v1/traces`. Sampling 0.1 — пример политики, не универсальная доля production. [Agent configuration](https://opentelemetry.io/docs/zero-code/java/agent/configuration/), [OTLP exporter configuration](https://opentelemetry.io/docs/languages/sdk-configuration/otlp-exporter/).

**Пример приложения Spring Boot 3.3**, версии зависимостей — из его dependency management:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-tracing-bridge-otel'
    implementation 'io.opentelemetry:opentelemetry-exporter-otlp'
}
```

```yaml
management:
  tracing:
    sampling:
      probability: 0.1
    propagation:
      type: w3c
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces
```

`management.*` управляет указанной интеграцией Boot и не является автоматически конфигурацией отдельного Java Agent. В Boot при работающем tracing handler можно создать свой span через Observation:

```java
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

final class OrderObservations {
    private final ObservationRegistry registry;

    OrderObservations(ObservationRegistry registry) {
        this.registry = registry;
    }

    void create(String orderType, String orderId, Runnable action) {
        Observation.createNotStarted("order.create", registry)
                .lowCardinalityKeyValue("order.type", orderType)
                .highCardinalityKeyValue("order.id", orderId)
                .observe(action);
    }
}
```

Сам вызов Observation не гарантирует экспорт span без соответствующих handlers/exporter. Ограничивайте набор `order.type`; уникальный order ID не добавляйте в обычные labels метрик. Автоматическую propagation HTTP-клиентов подключают с условиями Q8. [Spring Boot 3.3 tracing и custom spans](https://docs.spring.io/spring-boot/3.3/reference/actuator/tracing.html).


## Q17. (!) Что такое cardinality метрик и почему она важна?

`Cardinality` — число различных наборов значений лейблов для фиксированного имени метрики. Каждый фактически наблюдавшийся набор определяет отдельный временной ряд Prometheus. Новые значения `user_id` или `request_id` могут резко увеличить число рядов, расходы памяти, диска и стоимость запросов. Перегрузка и OOM возможны, но зависят от нагрузки и ресурсов; затраты и отказы измеряют на своей системе.

```java
// ПЛОХО: высокая cardinality — user_id может иметь миллионы значений
registry.counter("api.requests", "user_id", userId); // НЕ ДЕЛАТЬ!

// ПЛОХО: URL с path-параметрами
registry.counter("http.requests", "uri", "/api/users/12345"); // НЕ ДЕЛАТЬ!

// ХОРОШО: ограниченное множество значений
registry.counter("api.requests",
    "endpoint", "/api/orders",   // ограниченный набор эндпоинтов
    "method", "POST",            // GET, POST, PUT, DELETE
    "status", "200"              // ~10 статусов
);

// ХОРОШО: использовать MeterFilter для нормализации URI
@Bean
MeterRegistryCustomizer<MeterRegistry> uriNormalizer() {
    return registry -> registry.config().meterFilter(
        MeterFilter.replaceTagValues("uri", actualUri -> {
            // /api/users/123 → /api/users/{id}
            if (actualUri.matches("/api/users/\\d+")) {
                return "/api/users/{id}";
            }
            return actualUri;
        })
    );
}
```

**Оценка перед добавлением метрики:** для фиксированного имени и набора ключей labels произведение `V1 * V2 * ... * Vn` задаёт верхнюю оценку числа сочетаний, где `Vi` — число возможных значений i-го label. Равенство достигается, только если встретились все сочетания. Например, `(GET, /a)`, `(GET, /b)`, `(POST, /a)` дают три ряда при верхней оценке `2 * 2 = 4`. Уточняйте scope и окно наблюдения; учитывайте также labels `job`/`instance` и дополнительные ряды представления метрики.

У classic histogram bucket-значения `le` создают дополнительные ряды; native histogram хранит составной sample в одном ряду для набора labels. Это отличается от точности квантилей. Лучше использовать шаблон маршрута из инструментации; показанный URI-фильтр нормализует только `/api/users/<число>`, остальные значения требуют отдельного решения. [Prometheus data model](https://prometheus.io/docs/concepts/data_model/), [histograms](https://prometheus.io/docs/practices/histograms/), [Micrometer Meter Filters](https://docs.micrometer.io/micrometer/reference/concepts/meter-filters.html).

## Q18. Как метрики и трейсы помогают при инцидентах?

Метрики помогают оценить **масштаб и динамику**: долю отказов, распределение задержки, затронутые сервисы и ресурсы. Trace показывает наблюдаемые операции отдельного выполнения: интервалы, ошибки и отношения spans. Долгий span сужает область поиска, но не доказывает причину и не указывает автоматически строку кода; нужны логи, профили, состояние ресурсов или другие свидетельства.

```mermaid
graph TD
    A["Сигнал: алерт или жалоба на запрос"] --> B["Оценить масштаб по метрикам"]
    B --> C["Выбрать trace: поиск или доступный exemplar"]
    C --> D["Сопоставить операции и интервалы"]
    D --> E["Проверить гипотезу: логи, ресурсы, профиль"]
    E --> F["Изменение и проверка результата на нагрузке"]
```

Exemplar из Q14 связывает пример измерения с trace ID при настроенной цепочке и наличии данных. Он не является вычисленным p99 и не доказывает причину агрегатного всплеска. Trace может быть неполным из-за инструментации, sampling и доставки; один удачный запрос после исправления не доказывает восстановление всей нагрузки. Расследование может начинаться и с известного trace/лога, после чего метрики покажут распространённость проблемы. [Grafana exemplars](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/), [OpenTelemetry traces](https://opentelemetry.io/docs/concepts/signals/traces/).

Подробнее о процессе расследования — в [Observability](observability-interview.md).


## Q19. Что такое RED и USE методологии для метрик?

Это способы выбрать диагностические измерения. **RED** смотрит на запросы сервиса: `Rate` — частота, `Errors` — неуспешные запросы (число/частота либо согласованная доля), `Duration` — распределение длительности. **USE** рассматривает каждый ресурс: `Utilization` — занятость/использование ёмкости, `Saturation` — работа, которую ресурс пока не может обслужить, `Errors` — ошибки ресурса. Их используют вместе, но набор метрик сам по себе ещё не устанавливает причину инцидента. [RED, Tom Wilkie](https://grafana.com/blog/the-red-method-how-to-instrument-your-services/), [USE, Brendan Gregg](https://www.brendangregg.com/usemethod.html).

**Пример RED для `order-service`**, окно 5 минут:

```promql
# Rate — общая частота запросов выбранного сервиса
sum(rate(http_server_requests_seconds_count{application="order-service"}[5m]))

# Errors — доля 5xx в той же области, если за окно были запросы
sum(rate(http_server_requests_seconds_count{application="order-service",status=~"5.."}[5m]))
/
(sum(rate(http_server_requests_seconds_count{application="order-service"}[5m])) > 0)

# Duration — оценка p99 по совместимым classic histogram buckets
histogram_quantile(0.99,
  sum by (le) (rate(http_server_requests_seconds_bucket{application="order-service"}[5m]))
)
```

Это примеры при наличии указанных рядов: Timer без включённой экспозиции buckets не обеспечит последний запрос. Для бизнес-операции критерий ошибки может быть шире HTTP 5xx; задавайте его явно. Отсутствие событий/рядов не подменяйте доказанным нулём ошибок. При разрезе по endpoint сохраняйте те же группировки у числителя и знаменателя.

**Примеры USE:**

| Ресурс | Utilization | Saturation | Errors |
|---|---|---|---|
| CPU | Доля времени busy в выбранной области | Run queue или scheduler latency | Доступные счётчики аппаратных/ресурсных ошибок |
| Дисковый I/O | Доля busy time устройства | Очередь и ожидание I/O | Ошибки устройства/операций |
| Пул потоков | Занятые threads относительно доступной ёмкости | Число ожидающих задач или время ожидания | Отказы выделения/приёма работы по определённой политике |

`busy/max` пула описывает занятость, **не длину очереди**. Обычный major GC — событие сборки, **не ошибка ресурса**; его частоту нельзя подписывать как USE Errors. Измерения и ограничения зависят от конкретного ресурса, JVM/OS и экспортера; недоступную метрику помечают как неизвестную, а не заменяют похожим по имени счётчиком.


## Q20. Как экспортировать метрики в Prometheus из Java?

Для scrape приложение публикует зарегистрированные meters через **доступный HTTP endpoint**, а Prometheus опрашивает его по своей конфигурации. В Java можно использовать registry/экспозицию клиентской библиотеки; сериализация и HTTP-публикация — разные обязанности. В Spring Boot 3.3 нужны Actuator, подходящий Prometheus registry, exposure и доступ сборщика (см. Q3/Q10). Один registry не создаёт универсальный HTTP endpoint. Стороны согласуют поддерживаемый формат scrape: возможны текстовые форматы и Protobuf, поэтому правило «scrape всегда только текст» неверно. [Prometheus content negotiation](https://prometheus.io/docs/instrumenting/content_negotiation/).

**Синтетический пример classic text exposition** (имена/labels зависят от конфигурации):

```
# HELP http_server_requests_seconds Duration of HTTP server request handling
# TYPE http_server_requests_seconds histogram
http_server_requests_seconds_bucket{method="GET",uri="/api/orders",status="200",le="0.1"} 1523
http_server_requests_seconds_bucket{method="GET",uri="/api/orders",status="200",le="0.5"} 1891
http_server_requests_seconds_bucket{method="GET",uri="/api/orders",status="200",le="1.0"} 1900
http_server_requests_seconds_bucket{method="GET",uri="/api/orders",status="200",le="+Inf"} 1905
http_server_requests_seconds_count{method="GET",uri="/api/orders",status="200"} 1905
http_server_requests_seconds_sum{method="GET",uri="/api/orders",status="200"} 234.56

# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Eden Space"} 52428800
jvm_memory_used_bytes{area="heap",id="G1 Old Gen"} 104857600
```

**ServiceMonitor для Kubernetes (Prometheus Operator):**

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: order-service
  labels:
    release: prometheus
spec:
  selector:
    matchLabels:
      app: order-service
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
```

Для этого ServiceMonitor должны быть установлены Prometheus Operator/CRD; Prometheus выбирает сам ServiceMonitor по labels/namespace, а его selector — нужный **Service**, не непосредственно Pods. `endpoints.port: http` ссылается на имя порта Service; также проверяют namespace, доступ и RBAC. Аннотации Pods работают только при соответствующем scrape/relabel config, а не автоматически у любого Prometheus. [Operator ServiceMonitors](https://prometheus-operator.dev/docs/developer/getting-started/).

**Короткие задачи:** Pushgateway полезен для некоторых service-level batch jobs, результат которых трудно успеть собрать обычным scrape. Это не универсальная замена pull для любого cron/процесса: pushed series живут отдельно от процесса и не удаляются автоматически, поэтому нужен управляемый жизненный цикл данных. Prometheus опрашивает gateway, а не исходную задачу; успешный scrape gateway не доказывает здоровье этой задачи. [When to use Pushgateway](https://prometheus.io/docs/practices/pushing/).

## Q21. Что такое exemplars и зачем они нужны?

`Exemplar` сохраняет **пример отдельного наблюдения с контекстом** рядом с агрегированными данными метрики. В модели OpenTelemetry он содержит значение, время и дополнительные filtered attributes; связанные `trace_id`/`span_id` **необязательны**. Для histogram/sum значение exemplar уже участвует в агрегате: его не прибавляют повторно к count, sum или buckets. [OTel Metrics Data Model, Exemplars](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exemplars).

Если exemplar содержит trace ID и цепочка сбора/хранения/визуализации настроена, по нему можно открыть trace для расследования. Контекст exemplar отделён от labels, определяющих временной ряд. Exemplar не заменяет распределение и не доказывает причину скачка или принадлежность запроса вычисленному p99; это пример для проверки гипотезы, как в Q14.

**Синтетический фрагмент OpenMetrics 1.0** — один bucket с exemplar, не полная экспозиция:

```text
http_server_requests_seconds_bucket{method="POST",uri="/api/orders",le="0.5"} 120 # {trace_id="4bf92f3577b34da6a3ce929d0e0e4736",span_id="00f067aa0ba902b7"} 0.48 1712345678.000
```

`120` — cumulative bucket count, `0.48` — значение выбранного наблюдения в секундах, далее его timestamp. Дополнительные labels после `#` не создают ряд по trace ID. OpenMetrics определяет exemplars также для counter total; правило «exemplar бывает только у latency histogram» неверно. Формат этой строки не следует выдавать за Prometheus text 0.0.4. [OpenMetrics 1.0](https://prometheus.io/docs/specs/om/open_metrics_spec/).

**Подключение зависит от версий.** Для Boot 3.3 документация различает `SpanContext` нового Prometheus client и `SpanContextSupplier` legacy simpleclient; при подходящей Micrometer Tracing интеграции соответствующая связка автоконфигурируется. Нельзя смешивать конструкторы этих registry или считать один список зависимостей доказательством работы. Проверяйте фактическую экспозицию с exemplar, выбранный формат, сбор/хранение Prometheus и datasource-связь Grafana. [Boot 3.3 Prometheus exemplars](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html).

Сам ID ещё не гарантирует доступный trace: возможны sampling, потери и различный retention. При отсутствии trace-контекста exemplar может оставаться допустимым примером наблюдения, но прямой переход к trace недоступен.


## Q22. Как настроить алерты по метрикам (Prometheus, Alertmanager)?

**Prometheus** вычисляет PromQL-правила и состояние каждого alert instance по его набору labels. **Alertmanager** получает alerts и управляет группировкой, дедупликацией, маршрутизацией и подавлением уведомлений. [Alerting overview](https://prometheus.io/docs/alerting/latest/overview/).

| Состояние правила | Что произошло |
|---|---|
| inactive | Для этого набора labels нет активного элемента результата. |
| pending | Элемент есть, но заданный `for` ещё не выдержан. |
| firing | Условие оставалось активным на последовательных оценках в течение `for`; без `for` нет этой выдержки. |

`for` не суммирует разрозненные всплески: исчезновение элемента до firing прерывает pending, а новый набор labels — другая alert instance. Опциональный `keep_firing_for` может удерживать уже firing alert после исчезновения условия. Ошибки evaluation и потерю данных рассматривают отдельно. [Prometheus alerting rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/).

**Важно: активность определяется наличием элемента вектора, даже со значением 0.** Например, `up == 0` фильтрует здоровые targets и оставляет неуспешные scrape со значением 0. `up == bool 0` сохраняет также нулевые элементы для здоровых targets, поэтому не подходит как замена этого фильтра в alert expression. [PromQL comparisons](https://prometheus.io/docs/prometheus/latest/querying/operators/#comparison-binary-operators).

**Иллюстративный файл правил:**

```yaml
groups:
  - name: application-alerts
    rules:
      - alert: TargetScrapeFailing
        expr: up{job="order-service"} == 0
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Неуспешный scrape {{ $labels.instance }}"
      - alert: HighHeapPoolUsage
        expr: |
          jvm_memory_used_bytes{area="heap"}
          / (jvm_memory_max_bytes{area="heap"} > 0)
          > 0.85
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Высокое заполнение pool {{ $labels.id }} на {{ $labels.instance }}"
```

Первое правило не доказывает падение процесса и не замечает исчезнувший из discovery target. Второе проверяет конкретный pool только при определённом положительном max, не заполнение всей heap (Q12). Правила и пороги подбирают под измерения приложения; отсутствие ряда не равно нулевой ошибке и требует отдельной проверки missing data.

В Alertmanager `group_wait` задерживает первое уведомление группы, `group_interval` и `repeat_interval` управляют последующими отправками. Эти таймеры не заменяют `for`. Silence и inhibition подавляют уведомления, но не меняют PromQL и состояние правила в Prometheus. Маршрут/receiver, доставка и resolved-уведомления проверяются отдельно; firing не означает, что сообщение уже получил дежурный. [Alertmanager](https://prometheus.io/docs/alerting/latest/alertmanager/).


## Q23. Что такое traceId и spanId и как они связаны?

`traceId` объединяет spans одного trace, `spanId` идентифицирует отдельную операцию; для указания конкретного span используют пару `traceId`/`spanId`. Отношение к родителю хранится отдельно: у root родителя нет, а root не обязан быть входящим HTTP-запросом. Дополнительные links могут связывать spans, в том числе из разных traces. Это контекст операций, а не список всех операций внутри одного ID. [OpenTelemetry traces](https://opentelemetry.io/docs/concepts/signals/traces/).

Для **W3C Trace Context version 00** `trace-id` содержит 32 lowercase hex-символа, `parent-id` — 16; полностью нулевые IDs недопустимы. В переданном `traceparent` поле parent-id обозначает span вызывающей стороны, а получатель создаёт span со своим ID (Q8). Это правило wire-формата; внешний correlation ID приложения может иметь другой формат. [W3C Trace Context](https://www.w3.org/TR/trace-context/).

**Пример текстового Logback config**, если трассировочная интеграция действительно заполняет MDC:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} [traceId=%X{traceId}, spanId=%X{spanId}] - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

**Синтетические строки двух операций одного trace:**

```text
14:23:45.123 INFO  c.e.OrderController [traceId=4bf92f3577b34da6a3ce929d0e0e4736, spanId=00f067aa0ba902b7] - Creating order
14:23:45.234 INFO  c.e.PaymentService [traceId=4bf92f3577b34da6a3ce929d0e0e4736, spanId=a3ce929d0e0e4736] - Processing payment
```

Общий trace ID одинаков, собственные span IDs различаются; родительскую связь эти строки сами не показывают. Один Logback-шаблон не заполняет MDC: нужен активный контекст и соответствующая интеграция. Boot 3.3 с Micrometer Tracing использует MDC для correlation IDs; перенос в собственные executor/async-пути тоже нужно проверить. Поиск по ID связывает доступные логи, но не гарантирует полного trace или всех логов запроса. [Boot 3.3 logging correlation](https://docs.spring.io/spring-boot/3.3/reference/actuator/tracing.html), [логирование](logging-strategies-interview.md).


## Q24. Как уменьшить поток данных трассировки до хранилища?

Уменьшайте данные там, где они появляются, и перед записью в хранилище. Выбор места важен: head sampling в SDK снижает исходящий поток приложения, а tail sampling в Collector позволяет выбрать поступившие трейсы по результату запроса, но требует принять и временно удержать их span'ы.

1. **Sampling** — снижать число сохраняемых трейсов; выбор head/tail разобран в [Q28](#q28-как-выбрать-head-based-и-tail-based-sampling-для-production), работа tail sampler — в [Q37](#q37-как-работает-tail-based-sampling-в-трассировке).
2. **Ограничения в SDK** — задать лимит числа и длины атрибутов, событий и ссылок, не записывать чувствительные тела запросов и чрезмерно длинные значения.
3. **Фильтрация перед хранилищем** — удалить ненужные атрибуты или исключить малоценные span'ы. Фильтрация в Collector не уменьшает трафик между приложением и Collector; удаление отдельного span'а может сделать трейс неполным.
4. **Выборочная инструментация** — не создавать span'ы для малоценных операций, сохраняя данные, нужные для диагностики.

**Конфигурация tail-based sampling в OTel Collector:**

```yaml
processors:
  tail_sampling:
    decision_wait: 10s
    num_traces: 100000
    expected_new_traces_per_sec: 1000
    policies:
      # Отбирать поступившие трейсы со статусом ERROR
      - name: errors-policy
        type: status_code
        status_code:
          status_codes: [ERROR]

      # Отбирать поступившие медленные трейсы (> 1s)
      - name: latency-policy
        type: latency
        latency:
          threshold_ms: 1000

      # Пример базовой выборки 5% для остальных
      - name: probabilistic-policy
        type: probabilistic
        probabilistic:
          sampling_percentage: 5

  # Удалить ненужный атрибут перед хранилищем
  attributes:
    actions:
      - key: http.request.body
        action: delete
```

Лимиты числа атрибутов и событий span'а задают в SDK. Показанный `attributes` processor удаляет атрибут уже после получения данных Collector. Например, Java SDK поддерживает `otel.span.attribute.count.limit` и `otel.span.event.count.limit`. Числа `decision_wait`, `num_traces` и 5% в примере иллюстративны: их подбирают по нагрузке и проверяют по потерям и полноте трейсов. Tail sampler не восстановит ранее отброшенные span'ы и не гарантирует сохранение каждой ошибки. Срок хранения — отдельное решение в [Q27](#q27-как-уменьшить-объём-и-стоимость-хранения-трейсов-в-production). [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/), [Java SDK configuration](https://opentelemetry.io/docs/languages/java/configuration/), [Tail Sampling Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md).

## Q25. Как выбрать между метриками и трейсами для отладки?

Правило простое: метрики отвечают на агрегированные вопросы «что и насколько» (какой сервис деградировал, какой тренд), трейсы — на точечные «почему именно этот запрос» (где в цепочке вызовов задержка или ошибка). Метрики — точка входа в расследование, трейсы — углубление в конкретный случай.

| Вопрос | Инструмент |
|--------|-----------|
| Какой сервис деградировал? | Метрики (дашборд, алерт) |
| Какой эндпоинт медленный? | Метрики (p99 по endpoint) |
| Почему конкретный запрос медленный? | Трейс (дерево spans) |
| Где в цепочке вызовов ошибка? | Трейс (span с ошибкой) |
| Каков тренд за неделю? | Метрики (PromQL range query) |
| Что произошло с запросом пользователя X? | Трейс + логи (по traceId) |

**Типичный поток:** метрики показывают рост латентности на сервисе A -> открываем трейсы за период -> в медленных трейсах видим, что задержка в вызове к БД или к сервису B. `Exemplars` сокращают этот путь — позволяют перейти к конкретному трейсу прямо с точки на графике.

## Q26. Что такое RED-метрики и USE-метрики?

Это совместимые методологии выбора сигналов с разными **объектами наблюдения**:

| Метод | Объект | Какие сигналы выбрать |
|---|---|---|
| RED | Запросы сервиса | Rate — частота запросов, Errors — их ошибки, Duration — длительность/распределение задержки. |
| USE | Конкретный ресурс | Utilization — занятость/использование ёмкости, Saturation — работа в ожидании обслуживания, Errors — ошибки ресурса. |

Общие определения и примеры запросов приведены в [Q19](#q19-что-такое-red-и-use-методологии-для-метрик). Это не обязательный исчерпывающий минимум и не гарантия нахождения корневой причины. Различие определяется смыслом измерений; одна система экспорта может передавать оба набора. [RED, Tom Wilkie](https://grafana.com/blog/the-red-method-how-to-instrument-your-services/), [USE, Brendan Gregg](https://www.brendangregg.com/usemethod.html).

**Пример применения:** у API выросла latency. RED помогает проверить частоту, долю ошибок и распределение задержки в согласованной области запросов. USE пула потоков добавляет занятость, ожидающие задания и отказы приёма. Рост занятости сам по себе ещё не устанавливает причину: сопоставьте окна, нагрузку и ожидание зависимостей, затем проверьте гипотезу логами, traces или профилем. Доменный сбой может потребовать дополнительных сигналов.

**Выбор инструмента — отдельный шаг.** Rate/Errors можно получать из подходящих counters/Timer с определённым критерием ошибки; Duration не ограничена одним видом инструмента. Для p99 нужны достаточные данные о распределении, а средняя длительность его не заменяет. Наличие JVM binders не даёт автоматически очередь/ошибки каждого ресурса; GC-событие не становится USE Errors, а busy/max — очередью. Проверяйте конкретные meters и область измерений.

RED пересекается с тремя SRE golden signals — traffic, errors, latency; четвёртый, saturation, рассматривается отдельно. USE помогает исследовать ресурсы, но не делает эти два подхода тождественными всем golden signals. [Google SRE, Monitoring Distributed Systems](https://sre.google/sre-book/monitoring-distributed-systems/).


## Q27. Как уменьшить объём и стоимость хранения трейсов в production?

Стоимость хранилища зависит от объёма, срока хранения и числа индексируемых атрибутов. После сокращения входящего потока из [Q24](#q24-как-уменьшить-поток-данных-трассировки-до-хранилища) управляйте тем, что и как долго сохраняется:

1. **Sampling и фильтрация** — снизить число и размер сохраняемых трейсов, не обещая сохранить все ошибки.
2. **Retention** — назначить срок хранения по задачам расследования, требованиям и бюджету; единого срока вроде 7–14 дней нет.
3. **Span metrics** — агрегировать поступающие span'ы в счётчики и гистограммы для трендов. Это не обратимое сжатие старых трейсов: дерево вызовов из метрик не восстановить.

**Фрагмент маршрутизации в OTel Collector:** один приёмник передаёт поступившие span'ы в две ветки. `span_metrics` получает их до tail sampling, а в хранилище идёт отобранный поток. Компоненты `otlp`, `tail_sampling` и exporters нужно определить отдельно.

```yaml
connectors:
  span_metrics:
    histogram:
      explicit:
        buckets: [10ms, 50ms, 100ms, 500ms, 1s, 5s]
service:
  pipelines:
    traces/metrics:
      receivers: [otlp]
      exporters: [span_metrics]
    traces/storage:
      receivers: [otlp]
      processors: [tail_sampling]
      exporters: [otlp/traces]
    metrics/span_metrics:
      receivers: [span_metrics]
      exporters: [otlp/metrics]
```

Если SDK уже выполнил head sampling, ветка `span_metrics` увидит только оставшиеся span'ы; её показатели нельзя без проверки считать полными метриками трафика. Tail sampling по ошибкам и задержке после генерации метрик сохраняет больше нужных для отладки трейсов, но расходует память Collector и тоже не гарантирует каждую аномалию. Точность метрик, кардинальность измерений и стоимость хранения проверяют отдельно. [Collector architecture](https://opentelemetry.io/docs/collector/architecture/), [Span Metrics Connector](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/connector/spanmetricsconnector/README.md), [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/).

## Q28. Как выбрать head-based и tail-based sampling для production?

Для production сначала определите, что важнее: ограничить поток span'ов до экспорта или отбирать трейсы по результату запроса. `Head-based` принимает решение рано и снижает объём передаваемых данных, но может отбросить ошибочный запрос. `Tail-based` оценивает поступившие span'ы по статусу, задержке и другим признакам, зато требует памяти, времени ожидания и маршрутизации всех span'ов одного trace к одному экземпляру sampler.

```mermaid
graph LR
    subgraph "Head-based Sampling"
        REQ1["Запрос 1"] --> |"sampled=true (10%)"| SAVE1["Сохранить"]
        REQ2["Запрос 2"] --> |"sampled=false"| DROP1["Отбросить"]
        REQ3["Запрос 3"] --> |"sampled=false"| DROP2["Отбросить"]
    end

    subgraph "Tail-based Sampling"
        REQ4["Запрос 4<br/>200 OK, 50ms"] --> COLL["OTel Collector<br/>буфер 10s"]
        REQ5["Запрос 5<br/>500 Error"] --> COLL
        REQ6["Запрос 6<br/>200 OK, 3000ms"] --> COLL
        COLL --> |"error"| SAVE2["Сохранить"]
        COLL --> |"latency > 1s"| SAVE3["Сохранить"]
        COLL --> |"нормальный"| DROP3["Отбросить"]
    end
```

**Выбор:** начните с head-based, если достаточно вероятностной выборки и нужно уменьшить экспортный трафик уже в приложении. Выберите tail-based, если важнее отбор по ошибкам или задержке и есть ресурс на stateful Collector. Tail-sampler видит только пришедшие span'ы: ранее отброшенный head sampler'ом trace он восстановить не сможет. При потере span'ов, вытеснении из буфера или неверной маршрутизации нельзя обещать сохранение каждой ошибки. Долю и правила проверяйте по стоимости, полноте данных и нагрузке Collector. Q37 показывает настройки и границы. [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/), [Tail Sampling Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md).

## Q29. Как связать метрики приложения с бизнес-метриками?

Технические метрики (`RPS`, latency, ошибки) показывают здоровье системы, бизнес-метрики (конверсия, заказы, отказы платежей) — здоровье продукта. Связывать их полезно, потому что технический сбой почти всегда бьёт по бизнесу, и видеть это на одном экране быстрее, чем сопоставлять два дашборда вручную. Связь делают через:

- **общие теги** (`region`, `payment_method`) — чтобы фильтровать обе группы метрик одинаково;
- **совмещённые дашборды** «технические + бизнес» — рост latency и падение конверсии видны рядом;
- **алерты по бизнес-метрикам** — иногда падение заказов замечаешь раньше, чем рост 5xx.

```java
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final MeterRegistry registry;

    public CheckoutResult checkout(Cart cart) {
        Timer.Sample sample = Timer.start(registry);
        try {
            CheckoutResult result = processCheckout(cart);

            // Бизнес-метрики
            registry.counter("business.checkout.completed",
                    "region", cart.getRegion(),
                    "payment_method", cart.getPaymentMethod()
            ).increment();

            registry.summary("business.order.amount",
                    "region", cart.getRegion()
            ).record(result.getTotalAmount());

            return result;
        } catch (PaymentException e) {
            // Бизнес-метрика: отказ платежа
            registry.counter("business.checkout.failed",
                    "region", cart.getRegion(),
                    "reason", e.getReason()
            ).increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("business.checkout.duration")
                    .tag("region", cart.getRegion())
                    .register(registry));
        }
    }
}
```

**PromQL для бизнес-дашборда:**

```promql
# Конверсия за последний час
sum(rate(business_checkout_completed_total[1h]))
/
sum(rate(business_cart_created_total[1h]))

# Revenue rate (рублей в минуту)
sum(rate(business_order_amount_sum[5m])) by (region)
```

## Q30. Как организовать алертинг по метрикам и трейсам?

Алертят почти всегда по метрикам — это пороги по latency, error rate и доступности. Трейсы для алертов используют редко (например, рост доли медленных трейсов), потому что они дороги и точечны: их роль — не поднять тревогу, а объяснить уже поднятую. Поэтому стандартная схема — алерт по метрике, а в теле алерта ссылка на пример трейса. Каналы доставки: `Alertmanager` -> `PagerDuty`, `Slack`, email.

```mermaid
graph TD
    subgraph "Prometheus"
        RULES["Alert Rules<br/>PromQL условия"]
    end

    RULES --> |"firing"| AM["Alertmanager"]

    AM --> |"group_by: alertname"| ROUTE["Routing"]

    ROUTE --> |"severity: critical"| PD["PagerDuty<br/>Звонок дежурному"]
    ROUTE --> |"severity: warning"| SLACK["Slack #alerts<br/>Уведомление"]
    ROUTE --> |"severity: info"| EMAIL["Email<br/>Сводка"]

    subgraph "В алерте"
        LINK1["Ссылка на Grafana дашборд"]
        LINK2["Ссылка на Runbook"]
        LINK3["Exemplar → traceId"]
    end

    AM --> LINK1 & LINK2 & LINK3
```

**Рекомендации:** в алерт класть ссылку на дашборд и пример трейса (exemplar), а в описание — `Runbook`, чтобы дежурный начал чинить, а не разбираться. Главный враг — шум: его глушат малым числом порогов, агрегацией и условием подтверждения (`for`, например 2 из 3 точек), иначе на алерты перестают реагировать. На стороне `Alertmanager` помогают группировка по `alertname` и лейблам, inhibition (не слать вторичные при сработавшем корневом) и silence на время плановых работ.

## Q31. (!) Что такое W3C TraceContext и зачем это стандарт?

**W3C TraceContext** — это стандарт HTTP-заголовков для передачи контекста трассировки между сервисами. Проблема, которую он решает: до него каждая система носила контекст в своих заголовках — `X-B3-TraceId` (Zipkin), `X-Amzn-Trace-Id` (AWS), `uber-trace-id` (Jaeger). Как только запрос пересекал границу между системами с разными форматами, контекст терялся, и трейс обрывался. Единый стандарт даёт общий язык, на котором любой сервис понимает контекст любого другого.

Стандарт определяет два заголовка:

```
# traceparent: version-traceId-parentSpanId-flags
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01

# tracestate: vendor-specific data (опционально)
tracestate: vendor1=opaqueValue1,vendor2=opaqueValue2
```

Разбивка `traceparent`:
- `00` — версия (всегда 00)
- `4bf92f3577b34da6a3ce929d0e0e4736` — 128-bit trace ID
- `00f067aa0ba902b7` — parent span ID
- `01` — флаги (`01` = sampled, `00` = не sampled)

**Как OpenTelemetry работает с W3C TraceContext в Java:**

```java
// OpenTelemetry автоматически инжектирует и извлекает traceparent
// при использовании HTTP-инструментирования

// Вручную: inject context в исходящий запрос
TextMapPropagator propagator = openTelemetry.getPropagators().getTextMapPropagator();
propagator.inject(Context.current(), headers, (h, key, value) -> h.add(key, value));

// Вручную: extract context из входящего запроса
Context extractedContext = propagator.extract(Context.current(), request, 
    (req, key) -> req.getHeader(key));
```

**Конфигурация Spring Boot с OpenTelemetry agent:**
```yaml
# application.yml
management:
  tracing:
    propagation:
      type: w3c   # или b3, b3_multi
```

**Почему это важно:** при смешивании вендоров (AWS -> Kubernetes -> on-prem) именно `W3C TraceContext` обеспечивает сквозную трассировку без обрывов на стыках. `OpenTelemetry` использует `W3C` по умолчанию начиная с Java SDK 1.x, поэтому в современном стеке стандарт работает «из коробки».

## Q32. Как настроить Micrometer с кастомными тегами для бизнес-метрик?

Стандартных метрик `JVM` и `HTTP` хватает для здоровья процесса, но SLO часто формулируются в бизнес-терминах (доля успешных заказов, время оформления), а такие метрики приложение заводит само. `Micrometer` даёт для этого простой API — `Counter`, `Timer`, `DistributionSummary` с произвольными тегами:

```java
@Component
@RequiredArgsConstructor
public class OrderMetrics {
    private final MeterRegistry registry;

    // Counter — монотонно растущий счётчик
    private Counter orderPlaced;
    private Counter orderFailed;

    // DistributionSummary — распределение значений (например, сумма заказа)
    private DistributionSummary orderAmount;

    // Timer — время выполнения с гистограммой
    private Timer checkoutTimer;

    @PostConstruct
    public void init() {
        orderPlaced = Counter.builder("orders.placed")
            .tag("region", "ru")
            .description("Количество размещённых заказов")
            .register(registry);

        orderFailed = Counter.builder("orders.failed")
            .tag("region", "ru")
            .tag("reason", "payment_declined")
            .register(registry);

        orderAmount = DistributionSummary.builder("orders.amount.rub")
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .register(registry);

        checkoutTimer = Timer.builder("checkout.duration")
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .sla(Duration.ofMillis(200), Duration.ofMillis(500))
            .register(registry);
    }

    public void recordOrder(double amount) {
        orderPlaced.increment();
        orderAmount.record(amount);
    }

    public <T> T timeCheckout(Supplier<T> supplier) {
        return checkoutTimer.record(supplier);
    }
}
```

**Динамические теги (Gauge с lambda):**
```java
// Gauge — текущее значение (очередь, активные сессии)
Gauge.builder("orders.queue.size", orderQueue, Queue::size)
    .tag("priority", "high")
    .register(registry);
```

**PromQL для бизнес-метрик:**
```promql
# Error rate заказов за последние 5 минут
rate(orders_failed_total[5m]) / rate(orders_placed_total[5m])

# p99 времени оформления заказа
histogram_quantile(0.99, rate(checkout_duration_seconds_bucket[5m]))

# Средняя сумма заказа
rate(orders_amount_rub_sum[5m]) / rate(orders_amount_rub_count[5m])
```

**Ключевой принцип:** не использовать высококардинальные теги (`userId`, `orderId`) — они взорвут TSDB (см. Q17, Q39). В теги идут только статические или enum-подобные значения: `region`, `status`, `product_category`. Если нужна аналитика по конкретному пользователю — это уровень логов или событий, а не метрик.

## Q33. Как интегрировать Zipkin в Spring Boot приложение?

**Zipkin** — один из первых open-source бэкендов для распределённой трассировки (Twitter, 2012). Сегодня его редко подключают «напрямую»: чаще приложение инструментируют через `OpenTelemetry`, а в `Zipkin` отправляют через `Zipkin exporter`. Ниже — оба пути для `Spring Boot 3`.

**Spring Boot 3 + Micrometer Tracing + Zipkin:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0   # 100% в dev, 0.1 в prod
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

**OpenTelemetry → Zipkin exporter (альтернатива):**
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-zipkin</artifactId>
</dependency>
```

```java
ZipkinSpanExporter zipkinExporter = ZipkinSpanExporter.builder()
    .setEndpoint("http://zipkin:9411/api/v2/spans")
    .build();

SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
    .addSpanProcessor(BatchSpanProcessor.builder(zipkinExporter).build())
    .setSampler(Sampler.traceIdRatioBased(0.1))  // 10% sampling
    .build();
```

**Что проверять в Zipkin UI:**
- Service dependency graph — кто кого вызывает
- Waterfall view — где время теряется (DB? HTTP? serialization?)
- Error traces — `tags["error"] = true`
- Медленные трейсы: сортировка по duration

**Zipkin vs Jaeger:** `Zipkin` проще в настройке и легче, `Jaeger` богаче по UI и поддерживает adaptive sampling. Если стек строится вокруг `OpenTelemetry`, предпочтительнее `Jaeger` или `Grafana Tempo` — они лучше интегрированы с OTel-экосистемой.

## Q34. (!) Какие типы инструментов предоставляет Micrometer — Counter, Gauge, Timer, DistributionSummary?

**Micrometer** — фасад метрик для JVM-приложений (аналог `SLF4J` для логов), абстрагирующий код от конкретного бэкенда (`Prometheus`, `Datadog`, `InfluxDB`). Выбор инструмента определяется природой величины: монотонно она растёт или может падать, нужно ли одно число или распределение, и измеряем мы время или произвольное значение.

**Counter — монотонно возрастающий счётчик:**

```java
// Счётчик запросов, ошибок, обработанных сообщений
Counter ordersPlaced = Counter.builder("orders.placed.total")
    .tag("region", "ru")
    .tag("channel", "web")
    .description("Total number of placed orders")
    .register(registry);

ordersPlaced.increment();          // +1
ordersPlaced.increment(5.0);       // +5

// PromQL: rate(orders_placed_total[5m]) — скорость создания заказов
```

**Gauge — текущее значение (может убывать):**

```java
// Размер очереди, число активных соединений, объём памяти
Gauge.builder("orders.queue.size", orderQueue, Queue::size)
    .tag("priority", "high")
    .register(registry);

// Для volatile значений — AtomicInteger
AtomicInteger activeUsers = registry.gauge(
    "users.active",
    new AtomicInteger(0)
);
activeUsers.incrementAndGet();  // при входе
activeUsers.decrementAndGet();  // при выходе
```

**Timer — измерение времени и количества вызовов:**

```java
Timer checkoutTimer = Timer.builder("checkout.duration")
    .tag("payment_method", "card")
    .description("Time to complete checkout")
    .publishPercentiles(0.5, 0.95, 0.99)       // перцентили в памяти
    .publishPercentileHistogram()                // для Prometheus histogram
    .sla(Duration.ofMillis(200), Duration.ofSeconds(1))  // SLO buckets
    .register(registry);

// Использование
checkoutTimer.record(() -> processCheckout());              // sync
checkoutTimer.record(Duration.ofMillis(150));               // manual

// Или через аннотацию
@Timed(value = "checkout.duration", percentiles = {0.95, 0.99})
public void checkout() { ... }

// PromQL: histogram_quantile(0.99, rate(checkout_duration_seconds_bucket[5m]))
```

**DistributionSummary — распределение числовых значений (не время):**

```java
// Размер запросов/ответов, суммы заказов, размеры файлов
DistributionSummary orderAmount = DistributionSummary
    .builder("orders.amount")
    .tag("currency", "RUB")
    .baseUnit("rubles")
    .publishPercentiles(0.5, 0.95)
    .register(registry);

orderAmount.record(1500.0);   // сумма конкретного заказа
orderAmount.record(350.0);

// PromQL:
// rate(orders_amount_rubles_sum[5m]) / rate(orders_amount_rubles_count[5m])
// → средняя сумма заказа за 5 минут
```

**LongTaskTimer — для длительных задач (batch jobs):**

```java
// Для задач, которые могут длиться минуты/часы
LongTaskTimer batchTimer = LongTaskTimer.builder("batch.import.active")
    .register(registry);

LongTaskTimer.Sample sample = batchTimer.start();
try {
    runBatchImport();
} finally {
    sample.stop();
}
// Gauge: batch_import_active_tasks_current — сколько batch задач активно
```

## Q35. Что такое OpenTelemetry Collector и как его использовать?

**OpenTelemetry Collector** — это отдельный процесс-посредник между приложениями и бэкендами мониторинга, работающий по схеме receivers -> processors -> exporters: принимает телеметрию (трейсы, метрики, логи), обрабатывает её и экспортирует в нужные системы. Смысл этой прослойки в том, чтобы приложения не знали о конкретных бэкендах: они шлют всё в один Collector, а маршрутизацию, батчинг, sampling и фильтрацию он берёт на себя — и менять их можно без передеплоя приложений.

**Архитектура:**

```mermaid
graph LR
    A[Java App + OTel SDK] -->|OTLP gRPC/HTTP| C[OTel Collector]
    B[Python App] -->|OTLP| C
    C -->|Prometheus scrape| P[Prometheus]
    C -->|Jaeger export| J[Jaeger]
    C -->|OTLP| G[Grafana Tempo]
    C -->|Loki| L[Grafana Loki]
```

**Конфигурация Collector (otel-collector-config.yaml):**

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318
  prometheus:              # также может скрейпить Prometheus метрики
    config:
      scrape_configs:
        - job_name: 'my-app'
          static_configs:
            - targets: ['app:8080']

processors:
  batch:                   # буферизует для эффективной отправки
    timeout: 5s
    send_batch_size: 512
  memory_limiter:          # защита от OOM
    limit_mib: 512
  resource:                # добавляем атрибуты ко всем сигналам
    attributes:
      - action: insert
        key: deployment.environment
        value: production

exporters:
  jaeger:
    endpoint: jaeger:14250
    tls:
      insecure: true
  prometheusremotewrite:
    endpoint: http://prometheus:9090/api/v1/write
  loki:
    endpoint: http://loki:3100/loki/api/v1/push

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [memory_limiter, batch, resource]
      exporters: [jaeger]
    metrics:
      receivers: [otlp, prometheus]
      processors: [batch]
      exporters: [prometheusremotewrite]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [loki]
```

**Spring Boot → OTel Collector:**

```yaml
# application.yml — Java Agent автоматически отправляет в Collector
management:
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces
    metrics:
      export:
        url: http://otel-collector:4318/v1/metrics
```

**Преимущества Collector vs прямая отправка:**
- Один endpoint для всех сигналов (трейсы + метрики + логи)
- Можно менять бэкенды без перекомпиляции приложения
- Батчинг, сжатие, retry встроены
- Tail-based sampling реализуется на Collector уровне
- Фильтрация чувствительных данных (PII scrubbing)

## Q36. (!) Что такое SLI, SLO и SLA — определения, расчёт, error budget?

Это три уровня одной и той же идеи «насколько надёжен сервис», от измерения к цели и к обязательству:

- **SLI (Service Level Indicator)** — измеримый показатель качества: конкретная метрика, например доля успешных запросов. Это **факт**, что сервис показывает сейчас.
- **SLO (Service Level Objective)** — целевое значение SLI, внутреннее обязательство команды («availability >= 99.9%»). Это **цель**.
- **SLA (Service Level Agreement)** — юридическое соглашение с клиентом, обычно слабее SLO с запасом. Это **обещание наружу** с последствиями за нарушение.

SLO ставят строже SLA намеренно: команда должна узнать о деградации и среагировать раньше, чем нарушит договор с клиентом.

**Типичная связь:** SLA ≤ SLO ≤ 100%

```
SLI: availability = успешные_запросы / все_запросы = 99.95%
SLO: availability >= 99.9%  (внутренняя цель)
SLA: availability >= 99.5%  (договор с клиентом)
```

**Расчёт availability SLI в PromQL:**

```promql
# SLI: доля успешных HTTP запросов (не 5xx) за 30 дней
(
  sum(rate(http_requests_total{status!~"5.."}[30d]))
  /
  sum(rate(http_requests_total[30d]))
) * 100

# Текущий SLO burn rate (скорость сжигания error budget)
sum(rate(http_requests_total{status=~"5.."}[1h]))
/
sum(rate(http_requests_total[1h]))
```

**Error Budget — бюджет ошибок.** Это «допустимое количество ненадёжности»: разница между 100% и SLO. Если SLO = 99.9%, то 0.1% времени сервис имеет право быть недоступным — и это нормально, а не повод для героизма. Пока бюджет не исчерпан, команда спокойно катит фичи; как только исчерпан — приоритет смещается на надёжность. Бюджет превращает спор «деплоить или не деплоить» в объективное число.

```
SLO = 99.9%  →  Error Budget = 0.1% от времени в месяц

Минут в месяце: 30 * 24 * 60 = 43 200 минут
Error Budget (минуты): 43 200 * 0.001 = 43.2 минуты

Если сервис упал на 50 минут → бюджет превышен →
нельзя выпускать новые фичи до следующего месяца
```

**Error Budget Policy:**

| Статус бюджета | Действие |
|---------------|----------|
| > 50% осталось | Нормальный режим разработки |
| 10-50% осталось | Тормозим рискованные деплои |
| < 10% осталось | Только hotfix, reliability работа |
| 0% (исчерпан) | Freeze деплоев до конца периода |

**Многоуровневые SLO:**

```yaml
# Разные SLO для разных сценариев
slos:
  - name: checkout_availability
    sli: rate(http_requests_total{endpoint="/checkout",status!~"5.."}[28d])
        / rate(http_requests_total{endpoint="/checkout"}[28d])
    objective: 99.9    # 99.9% availability

  - name: checkout_latency
    sli: histogram_quantile(0.99,
           rate(http_request_duration_seconds_bucket{endpoint="/checkout"}[28d]))
    objective: 2.0     # p99 < 2 секунды (в секундах)

  - name: catalog_freshness
    sli: time() - catalog_last_updated_timestamp_seconds
    objective: 300     # данные не старше 5 минут
```

**Alerting на burn rate (Google SRE подход):**

```yaml
# Алерт: сжигаем бюджет в 14x быстрее нормы (1 час окно)
alert: HighErrorBudgetBurnRate
expr: |
  (
    sum(rate(http_requests_total{status=~"5.."}[1h]))
    / sum(rate(http_requests_total[1h]))
  ) > 14 * (1 - 0.999)
for: 2m
labels:
  severity: critical
```

## Q37. Как работает tail-based sampling в трассировке?

`Tail-based sampling` в OpenTelemetry Collector группирует поступившие span'ы по `trace_id` и применяет политики к накопленной части трейса. В стандартной стратегии `trace-complete` решение обычно принимается после `decision_wait`; ненулевой `decision_wait_after_root_received` может ускорить его после получения root span. Эти интервалы ожидания не гарантируют, что пришли все span'ы или завершился весь trace. Другие стратегии процессора могут принимать некоторые решения раньше. Для корректной оценки все span'ы одного trace должны попасть на один экземпляр Collector. [Tail Sampling Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md).

**Реализация через OTel Collector:**

```yaml
# Tail Sampling Processor в OTel Collector
processors:
  tail_sampling:
    decision_wait: 10s        # ждём до решения; полнота трейса не гарантирована
    num_traces: 100000        # буфер трейсов в памяти
    expected_new_traces_per_sec: 1000
    policies:
      # Политика 1: отбираем полученные трейсы с ERROR
      - name: errors-policy
        type: status_code
        status_code: {status_codes: [ERROR]}

      # Политика 2: отбираем полученные медленные трейсы > 500ms
      - name: slow-traces-policy
        type: latency
        latency: {threshold_ms: 500}

      # Политика 3: базовая вероятностная выборка 5%
      - name: baseline-policy
        type: probabilistic
        probabilistic: {sampling_percentage: 5}
```

**Сравнение head vs tail sampling:**

| Аспект | Head-based | Tail-based |
|--------|-----------|------------|
| Когда решаем | В начале запроса | После накопления поступивших span'ов |
| Ошибки | Могут быть отброшены | Можно отбирать среди поступивших трейсов |
| Медленные запросы | Могут быть отброшены | Можно отбирать при достаточной полноте span'ов |
| Память | Не нужен буфер полного трейса для решения | Нужен буфер; размер зависит от нагрузки и времени ожидания |
| Задержка решения | Ранняя | Зависит от стратегии и интервалов ожидания, включая ускорение после root span |
| Размещение | SDK или ранний этап конвейера | Stateful Collector или другой tail-sampler |

**Когда использовать tail-based sampling:**
- Нужно отбирать полученные трейсы по исходу запроса, а не только по ранней вероятности.
- Collector получает span'ы одного trace на одном экземпляре, и хватает памяти на ожидающее множество трейсов.
- Мониторятся вытеснения, неполные трейсы и нагрузка; абсолютной гарантии сохранить каждую аномалию нет.

## Q38. Что такое Exemplars и как они связывают метрики с трейсами?

Определение exemplar и его отличие от распределения — в [Q21](#q21-что-такое-exemplars-и-зачем-они-нужны). Для перехода **метрика → конкретный trace** exemplar должен содержать пригодный trace reference; общий exemplar не обязан иметь trace ID. Даже доступный trace остаётся примером запроса, а не доказательством причины вычисленного p99.

Практически проверяют всю цепочку, а не только наличие кнопки в Grafana:

| Этап | Что проверить |
|---|---|
| Создание наблюдения | Инструмент и exemplar sampler действительно сохраняют контекст нужного наблюдения. Имя метрики само не порождает trace ID. |
| Экспозиция приложения | В выбранном OpenMetrics scrape-пути ответ содержит exemplar labels/value, отделённые от labels ряда. Нужны поддержка registry/client и согласованный формат ответа. |
| Сбор и хранение | Версия Prometheus поддерживает выбранный scrape protocol, принимает exemplars и имеет нужную конфигурацию их хранения. Список scrape protocols сам по себе не доказывает работу storage. |
| Grafana | Имя label exemplar совпадает с данными, а datasource UID ведёт к реальному trace backend; пример настройки — Q14. |
| Trace backend | Указанный trace действительно записан и ещё доступен по retention; переданный ID этого не гарантирует. |

Для **Boot 3.3** проверяйте связку выбранного Prometheus client и Micrometer Tracing: новый client использует `SpanContext`, legacy simpleclient — другой API. `publishPercentileHistogram()` настраивает histogram buckets, но не создаёт tracing context и не является универсальным условием exemplar: counters также поддерживаются. Не переносите `management.prometheus.metrics.export.exemplars-sampling: true` в конфигурацию без подтверждения выбранной версии: в проверенной metadata **Boot 3.3.2** такого свойства нет. [Boot 3.3 Prometheus integration](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html), [OpenMetrics 1.0](https://prometheus.io/docs/specs/om/open_metrics_spec/).

**Диагностика:** сначала найдите exemplar в фактической экспозиции приложения, затем — в данных Prometheus, и только после этого проверяйте ссылку Grafana. У Prometheus документирован experimental API `/api/v1/query_exemplars` для запроса exemplars по выражению и интервалу времени; интерфейс и поддержку проверяют для своей версии. Описанный здесь путь относится к OpenMetrics scrape, а не ко всем возможным транспортам телеметрии. [Prometheus HTTP API](https://prometheus.io/docs/prometheus/latest/querying/api/#querying-exemplars), [Grafana exemplars](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/).

Если цепочка не даёт нужного trace, используйте доступный поиск по времени/атрибутам и проверьте sampling, доставку и сроки хранения. Отсутствие ссылки не доказывает отсутствие запроса; один открытый exemplar не характеризует всю нагрузку.


## Q39. (!) Что такое проблема высокой кардинальности в метриках и как её избежать?

Определение и оценка сочетаний разобраны в [Q17](#q17--что-такое-cardinality-метрик-и-почему-она-важна). Произведение числа значений labels — верхняя оценка, а не обязательное число рядов: часть сочетаний может не встречаться. Для защиты production нужны бюджет рядов, нормализация источника и наблюдение за фактическим ростом.

**Начните с источника тегов.** Неограниченные `userId`, `orderId`, `requestId` или полный URL обычно не подходят для labels операционных метрик. Используйте шаблон endpoint, метод и ограниченную категорию статуса. Конечность каждого набора ещё не гарантирует дешёвую метрику: комбинации умножаются на число instances и других labels, а histogram может добавлять bucket-ряды. Бюджет выбирают по нагрузке, памяти, retention и стоимости запросов, а не по универсальным пределам «500 endpoints допустимо».

```java
Counter.builder("api.requests")
    .tag("endpoint", "/api/orders")
    .tag("method", "POST")
    .tag("status_class", "2xx")
    .register(registry);
```

Детали отдельных пользователей и запросов можно хранить в событиях/логах или трассах, сохраняя ограниченный набор labels для агрегатов. У этих сигналов тоже есть собственные затраты и ограничения.

**Различайте число рядов и число значений одного label.** Примеры для существующей метрики `http_requests_total`:

```promql
# Число выбранных рядов по имени метрики
topk(10, count by (__name__) ({__name__=~".+"}))

# Число рядов для каждого endpoint: учитывает сочетания остальных labels
count by (endpoint) (http_requests_total)

# Число различных непустых endpoint среди выбранных рядов
count(count by (endpoint) (http_requests_total{endpoint!=""}))
```

Это instant-запросы к выбранным данным, а не число всех рядов за retention или точный объём TSDB в памяти. Задавайте нужный scope (например, `job`) и сравнивайте результат со своим бюджетом. Всплески новых series и rejected samples наблюдайте отдельно.

**Ограничитель Micrometer — защитный отказ, а не нормализация.** Установите фильтр при инициализации registry до регистрации meters:

```java
registry.config().meterFilter(
    MeterFilter.maximumAllowableTags(
        "http.server.requests", "uri", 100,
        // Новое сверхлимитное значение uri: DENY регистрации meter
        MeterFilter.deny()
    )
);
```

`DENY` возвращает NOOP meter: записанные в него измерения отбрасываются. Значение не заменяется на `other`, и итоговые агрегаты могут стать неполными. Пример ограничивает разнообразие `uri` для подходящих имён meters в этом registry; это не общий предел всех рядов Prometheus. Число 100 иллюстративно. Если нужна агрегация в шаблон маршрута или `other`, задайте отдельное преобразование через `MeterFilter.replaceTagValues`/`map` либо нормализуйте тег до регистрации. Не подменяйте потерю измерений корректной агрегацией.

[Micrometer Meter Filters](https://docs.micrometer.io/micrometer/reference/concepts/meter-filters.html), [Prometheus naming/cardinality](https://prometheus.io/docs/practices/naming/), [PromQL aggregation operators](https://prometheus.io/docs/prometheus/latest/querying/operators/#aggregation-operators).

## Q40. Как реализовать context propagation в distributed tracing — W3C TraceContext и B3?

**Context propagation** — это механизм, который переносит trace context (`traceId`, `spanId` родителя, решение о sampling) между сервисами через HTTP-заголовки или заголовки сообщений. Без него каждый сервис начинал бы новый, не связанный трейс, и склеить путь запроса было бы невозможно. Существуют два основных формата заголовков — современный `W3C TraceContext` и старый `B3` (Zipkin); ниже — оба, а также Baggage и типичные ошибки.

**W3C TraceContext (рекомендуемый стандарт, W3C Recommendation):**

```http
# HTTP заголовки W3C TraceContext
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
             ^^                                                        
             version (00)                                              
                ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^                       
                traceId (128-bit hex)                                  
                                        ^^^^^^^^^^^^^^^^               
                                        parentSpanId (64-bit hex)      
                                                                 ^^    
                                                                 flags (01 = sampled)

tracestate: vendor1=value1,vendor2=value2   # vendor-specific данные
```

**B3 Propagation (Zipkin формат):**

```http
# Multi-header B3 (старый формат, Zipkin, Brave)
X-B3-TraceId: 4bf92f3577b34da6a3ce929d0e0e4736
X-B3-SpanId: 00f067aa0ba902b7
X-B3-ParentSpanId: 00f067aa0ba902b7
X-B3-Sampled: 1

# Single-header B3 (компактный)
b3: 4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-1
```

**Настройка propagation в Spring Boot + OTel:**

```yaml
# application.yml
management:
  tracing:
    propagation:
      type: w3c    # или b3, b3_multi, aws_xray
```

```java
// Ручная настройка через Java SDK
OpenTelemetrySdk otel = OpenTelemetrySdk.builder()
    .setPropagators(ContextPropagators.create(
        TextMapPropagator.composite(
            W3CTraceContextPropagator.getInstance(),     // W3C (основной)
            W3CBaggagePropagator.getInstance(),          // W3C Baggage
            B3Propagator.injectingSingleHeader()          // B3 для legacy систем
        )
    ))
    .build();
```

**Baggage — передача прикладных пар ключ-значение рядом с trace context.** Значение доступно нижележащим сервисам, если установлен и используется `W3CBaggagePropagator`. Инструментация может отправить его и во внешний API: поэтому передавайте только нужные несекретные короткие значения, ограничивайте набор ключей и очищайте baggage на границе доверия. `userId`, токены и персональные данные нельзя без оценки раскрытия класть туда «для удобства». Входящий baggage не имеет встроенной проверки целостности, поэтому он не является основанием для авторизации. Он также не становится атрибутом спана, метрики или лога сам по себе — для этого требуется явное копирование либо специальный processor. [OpenTelemetry Baggage](https://opentelemetry.io/docs/concepts/signals/baggage/).

```java
// Небольшое несекретное значение, нужное далее по цепочке
Baggage baggage = Baggage.builder()
    .put("traffic.class", "standard")
    .build();
try (Scope scope = baggage.makeCurrent()) {
    // Внутри scope Baggage.current() видит значение; исходящий вызов
    // передаст его лишь при настроенном propagator и инструментации.
    String trafficClass = Baggage.current().getEntryValue("traffic.class");
    callDownstream();
}
```

**HTTP заголовок Baggage:**

```http
baggage: traffic.class=standard
```

W3C Baggage требует сохранять весь заголовок в пределах **64 элементов и 8192 байт**; сверх этих границ реализация может отбросить элементы. Это нижний порог совместимости спецификации, а не полезный размер для каждого запроса. Держите своё содержимое намного меньше и не рассчитывайте, что каждый ключ дойдёт до получателя. [W3C Baggage, limits](https://www.w3.org/TR/baggage/#limits).

**Подводные камни.** Чаще всего трейс «обрывается» там, где контекст не передаётся автоматически — при уходе в свой пул потоков или нестандартный транспорт:

| Проблема | Причина | Решение |
|---------|---------|---------|
| Трейс обрывается | Async executor не копирует контекст | `ContextPropagatingExecutorService` |
| Трейс обрывается на Kafka | Producer не добавляет заголовки | OTel Kafka instrumentation |
| Смешанные форматы | Legacy B3, новые W3C | Composite propagator |

```java
// Проброс контекста через Executor (Spring ThreadPool)
@Bean
public Executor tracingExecutor(Tracer tracer) {
    return new ContextPropagatingExecutorService(
        Executors.newFixedThreadPool(10)
    );
}
```

## Q41. Какие SLI использовать для разных типов сервисов?

Универсального набора SLI нет: показатель должен отражать то, что важно пользователю именно этого сервиса. Для синхронного API это доступность и латентность, а для конвейера данных эти метрики бессмысленны — там важна свежесть данных. Поэтому Google SRE выделяет несколько категорий сервисов, и для каждой — свой набор SLI.

**1. Request-driven (REST/gRPC сервисы):**

```promql
# Availability SLI
sum(rate(http_requests_total{status!~"5.."}[5m]))
/ sum(rate(http_requests_total[5m]))

# Latency SLI (p99 < 500ms)
histogram_quantile(0.99,
  rate(http_request_duration_seconds_bucket[5m])) < 0.5

# Error rate SLI
rate(http_requests_total{status=~"5.."}[5m])
/ rate(http_requests_total[5m]) < 0.001  # < 0.1% ошибок
```

**2. Data pipeline (Kafka consumers, batch jobs):**

```promql
# Freshness SLI — свежесть данных
time() - kafka_consumer_last_poll_timestamp_seconds < 30  # < 30 сек отставание

# Throughput SLI — успешная обработка сообщений
rate(messages_processed_total{status="success"}[5m])
/ rate(messages_received_total[5m]) > 0.999

# Correctness SLI — процент верно обработанных записей
1 - (rate(records_failed_validation_total[1h])
     / rate(records_processed_total[1h]))
```

**3. Storage (БД, кэш):**

```promql
# Durability SLI — процент успешных записей
rate(db_write_operations_total{status="success"}[5m])
/ rate(db_write_operations_total[5m])

# Read latency SLI
histogram_quantile(0.99,
  rate(db_query_duration_seconds_bucket[5m])) < 0.1  # < 100ms

# Cache hit rate SLI
rate(cache_hits_total[5m])
/ (rate(cache_hits_total[5m]) + rate(cache_misses_total[5m])) > 0.9
```

**4. Background workers (отчёты, нотификации):**

```promql
# Completion SLI — завершаем задачи в срок
sum(batch_jobs_completed_on_time_total)
/ sum(batch_jobs_total) > 0.95  # 95% задач выполнены вовремя

# Email delivery SLI
rate(notifications_delivered_total[1h])
/ rate(notifications_sent_total[1h]) > 0.98
```

**Матрица SLI по типам сервисов:**

| Тип сервиса | Основные SLI | Типичные SLO |
|-------------|-------------|--------------|
| REST API | Availability, Latency p99 | 99.9%, < 500ms |
| GraphQL | Availability, Latency p95, Error rate | 99.5%, < 1s |
| Kafka Consumer | Freshness, Throughput | lag < 30s, > 99.9% |
| Batch Job | Completion rate, Duration | > 95%, < 2h |
| Кэш (Redis) | Hit rate, Latency | > 90%, < 10ms |
| БД | Durability, Read latency | 99.999%, < 100ms |

**Ключевые принципы выбора SLI:**
1. SLI должен отражать пользовательский опыт, а не внутренние метрики
2. Не более 3-5 SLI на сервис — иначе сложно приоритизировать
3. SLI должен быть измерим прямо сейчас (не "когда-нибудь настроим")
4. Синтетические мониторы (blackbox probes) дополняют SLI, но не заменяют их

## See also

- [Observability](observability-interview.md) — три столпа наблюдаемости, `SLI`/`SLO`/`SLA`, алертинг, `RED`/`USE` методы, `OpenTelemetry` Collector
- [Стратегии логирования](logging-strategies-interview.md) — как связать логи с метриками: sampling, retention, централизованная агрегация
- [Logging](../logging/logging-interview.md) — инструментальный слой: `MDC`, `traceId` в логах, связка логов с трейсами через `Logback`
- [Микросервисная архитектура](../architecture/microservices-interview.md) — паттерны, где метрики и трейсы обязательны: circuit breaker, bulkhead, saga
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — `Micrometer`, `Actuator`, `/actuator/prometheus`, интеграция с `OpenTelemetry` Java agent
- [Kubernetes](../devops/kubernetes-interview.md) — `Prometheus Operator`, `kube-state-metrics`, `ServiceMonitor`, сбор метрик в кластере

- [Jaeger и Zipkin](jaeger-zipkin-interview.md)
- [Стратегии логирования](logging-strategies-interview.md)
- [Loki и Grafana](loki-grafana-interview.md)
- [Observability](observability-interview.md)
- [OpenTelemetry](opentelemetry-interview.md)
