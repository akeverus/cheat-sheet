---
title: "Вопросы на собеседовании: Observability"
description: "Практичные вопросы и ответы по observability для Senior Java Developer: три столпа, OpenTelemetry, Prometheus, Grafana, distributed tracing, structured logging, SLI/SLO/SLA, алертинг, cardinality, ELK/EFK, RED/USE методы."
tags:
  - interview
  - monitoring
  - observability-interview
aliases:
  - "Observability"
  - "Наблюдаемость систем"
  - "Observability interview"
  - "Три столпа observability"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Observability`

Практичные вопросы и ответы по observability для `Senior Java Developer`: три столпа (логи, метрики, трейсы), `OpenTelemetry`, `Prometheus`, `Grafana`, distributed tracing, structured logging, `SLI`/`SLO`/`SLA`, алертинг и работа в production.

## Полезные ссылки

### Официальная документация

- [OpenTelemetry](https://opentelemetry.io/docs/) — спецификация и SDK для телеметрии
- [OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/) — Java SDK и auto-instrumentation
- [Prometheus](https://prometheus.io/docs/introduction/overview/) — система мониторинга и TSDB
- [Grafana](https://grafana.com/docs/) — визуализация и дашборды
- [Micrometer](https://micrometer.io/docs) — фасад метрик для JVM-приложений
- [Elastic Stack (ELK)](https://www.elastic.co/guide/index.html) — агрегация и поиск логов
- [Jaeger](https://www.jaegertracing.io/docs/) — distributed tracing backend
- [Grafana Loki](https://grafana.com/docs/loki/latest/) — лёгкая система агрегации логов
- [Grafana Tempo](https://grafana.com/docs/tempo/latest/) — backend для трейсов
- [Observability With Spring Boot](https://www.baeldung.com/spring-boot-3-observability) — observability в Spring Boot 3 через Micrometer
- [OpenTelemetry Setup in Spring Boot Application](https://www.baeldung.com/spring-boot-opentelemetry-setup) — настройка OpenTelemetry в Spring Boot
- [Observability in Distributed Systems](https://www.baeldung.com/distributed-systems-observability) — концепции observability для микросервисов
- [Working With OpenTelemetry Collector](https://www.baeldung.com/java-opentelemetry-collector) — работа с OpenTelemetry Collector

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и философия**
- [Q1. (!) Как коротко объяснить observability и отличие от мониторинга?](#q1-как-коротко-объяснить-observability-и-отличие-от-мониторинга)
- [Q2. Как построить сильный ответ на вопрос по observability?](#q2-как-построить-сильный-ответ-на-вопрос-по-observability)
- [Q3. (!) Как связаны три столпа observability?](#q3-как-связаны-три-столпа-observability)

**Метрики и Prometheus**
- [Q4. (!) Какие метрики критичны для backend-сервиса?](#q4-какие-метрики-критичны-для-backend-сервиса)
- [Q5. (!) Что такое RED и USE методы и когда какой применять?](#q5-что-такое-red-и-use-методы-и-когда-какой-применять)
- [Q6. Как устроен Prometheus и его модель данных?](#q6-как-устроен-prometheus-и-его-модель-данных)
- [Q7. Какие типы метрик есть в Prometheus и когда какой использовать?](#q7-какие-типы-метрик-есть-в-prometheus-и-когда-какой-использовать)
- [Q8. (!) Что такое cardinality и почему она опасна?](#q8-что-такое-cardinality-и-почему-она-опасна)
- [Q9. Как подключить Prometheus-метрики в Spring Boot приложении?](#q9-как-подключить-prometheus-метрики-в-spring-boot-приложении)

**Логи и агрегация**
- [Q10. (!) Что такое structured logging и зачем он нужен?](#q10-что-такое-structured-logging-и-зачем-он-нужен)
- [Q11. Как устроен ELK-стек и чем отличается EFK?](#q11-как-устроен-elk-стек-и-чем-отличается-efk)
- [Q12. Как настроить structured logging в Spring Boot?](#q12-как-настроить-structured-logging-в-spring-boot)

**Distributed Tracing**
- [Q13. (!) Как работает distributed tracing и из чего состоит трейс?](#q13-как-работает-distributed-tracing-и-из-чего-состоит-трейс)
- [Q14. Чем отличаются Jaeger и Zipkin?](#q14-чем-отличаются-jaeger-и-zipkin)
- [Q15. Как организовать связку traceId между логами и трейсами?](#q15-как-организовать-связку-traceid-между-логами-и-трейсами)
- [Q16. Что такое exemplars и когда они реально полезны?](#q16-что-такое-exemplars-и-когда-они-реально-полезны)

**OpenTelemetry**
- [Q17. (!) Что такое OpenTelemetry и почему это стандарт де-факто?](#q17-что-такое-opentelemetry-и-почему-это-стандарт-де-факто)
- [Q18. Как устроена архитектура OpenTelemetry?](#q18-как-устроена-архитектура-opentelemetry)
- [Q19. Как инструментировать Java-приложение с помощью OpenTelemetry?](#q19-как-инструментировать-java-приложение-с-помощью-opentelemetry)
- [Q20. Что такое OpenTelemetry Collector и зачем он нужен?](#q20-что-такое-opentelemetry-collector-и-зачем-он-нужен)

**Grafana и визуализация**
- [Q21. Как устроен стек Grafana и что входит в LGTM?](#q21-как-устроен-стек-grafana-и-что-входит-в-lgtm)
- [Q22. Как проектировать эффективные дашборды?](#q22-как-проектировать-эффективные-дашборды)

**SLO/SLI/SLA и алертинг**
- [Q23. (!) Как объяснить SLI, SLO, SLA и error budget на практике?](#q23-как-объяснить-sli-slo-sla-и-error-budget-на-практике)
- [Q24. (!) Как проектировать алертинг без alert fatigue?](#q24-как-проектировать-алертинг-без-alert-fatigue)
- [Q25. Что такое burn rate alerting и чем оно лучше threshold-алертов?](#q25-что-такое-burn-rate-alerting-и-чем-оно-лучше-threshold-алертов)

**Sampling и стоимость**
- [Q26. Какие стратегии sampling существуют для трейсов?](#q26-какие-стратегии-sampling-существуют-для-трейсов)
- [Q27. Как управлять стоимостью логов и трейсов?](#q27-как-управлять-стоимостью-логов-и-трейсов)

**Production и операционные практики**
- [Q28. Как запускать observability в production без перегруза системы?](#q28-как-запускать-observability-в-production-без-перегруза-системы)
- [Q29. Как выбрать стек observability под команду и бюджет?](#q29-как-выбрать-стек-observability-под-команду-и-бюджет)
- [Q30. Как observability помогает в инциденте и постмортеме?](#q30-как-observability-помогает-в-инциденте-и-постмортеме)
- [Q31. Как внедрять observability в CI/CD и релизный процесс?](#q31-как-внедрять-observability-в-cicd-и-релизный-процесс)

**Anti-patterns и зрелость**
- [Q32. (!) Какие anti-patterns в observability встречаются чаще всего?](#q32-какие-anti-patterns-в-observability-встречаются-чаще-всего)
- [Q33. Как оценить зрелость observability в команде?](#q33-как-оценить-зрелость-observability-в-команде)

**OpenTelemetry Collector и профилирование**
- [Q34. OpenTelemetry Collector: архитектура, pipeline и processors?](#q34-opentelemetry-collector-архитектура-pipeline-и-processors)
- [Q35. Continuous Profiling: Pyroscope, Grafana Phlare, eBPF?](#q35-continuous-profiling-pyroscope-grafana-phlare-ebpf)

**Chaos Engineering и Alert Fatigue**
- [Q36. Chaos Engineering и Observability: как связаны?](#q36-chaos-engineering-и-observability-как-связаны)
- [Q37. Alert Fatigue: причины, как бороться, стратегии silencing?](#q37-alert-fatigue-причины-как-бороться-стратегии-silencing)

**Runbook и FinOps**
- [Q38. Что включать в Runbook для автоматизации реакции на инцидент?](#q38-что-включать-в-runbook-для-автоматизации-реакции-на-инцидент)
- [Q39. FinOps и Observability: стоимость телеметрии и sampling для снижения затрат?](#q39-finops-и-observability-стоимость-телеметрии-и-sampling-для-снижения-затрат)
- [Q40. Synthetic Monitoring: что это и когда нужно?](#q40-synthetic-monitoring-что-это-и-когда-нужно)

## Q1. (!) Как коротко объяснить observability и отличие от мониторинга?

`Monitoring` отвечает на заранее известные вопросы: "вышли ли мы за порог?".
`Observability` позволяет расследовать неизвестные заранее сценарии: "почему конкретный поток запросов деградировал?".

Хорошая короткая формула:
- мониторинг = detection,
- observability = detection + diagnosis.

Ключевое свойство observable-системы — возможность задавать произвольные вопросы о внутреннем состоянии, не деплоя новый код. Термин пришёл из теории управления: система observable, если по её выходам можно восстановить внутреннее состояние.

```mermaid
graph LR
    subgraph Monitoring
        A[Заранее определённые метрики] --> B[Пороговые алерты]
        B --> C["Известные проблемы ✓"]
    end
    subgraph Observability
        D[Метрики + Логи + Трейсы] --> E[Ad-hoc запросы]
        E --> F["Неизвестные проблемы ✓"]
    end
    C -.-> |"Недостаточно для"| F
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Как построить сильный ответ на вопрос по observability?

Рабочий шаблон на 40-60 секунд:
1. **Симптом:** "растёт p99 latency и error rate".
2. **Путь диагностики:** "дашборд RED -> trace -> логи по traceId".
3. **Решение:** "ограничили cardinality метрик, поправили retry, снизили tail latency".
4. **Валидация:** "p99 с 900ms до 280ms, 5xx с 2.8% до 0.4%".

Именно измеримый результат отличает сильный senior-ответ от теории.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Как связаны три столпа observability?

Три столпа — это три complementary-сигнала телеметрии:

| Столп | Отвечает на вопрос | Примеры |
|-------|-------------------|---------|
| **Метрики** | "Что происходит массово?" | `request_rate`, `error_rate`, `cpu_usage` |
| **Трейсы** | "Где в цепочке задержка?" | `Span`, `TraceId`, waterfall |
| **Логи** | "Почему это произошло?" | stack trace, бизнес-события |

Практика: по алерту на метрику переходите к trace, затем фильтруете логи по `traceId`.

```mermaid
graph TD
    Alert["🔔 Алерт: p99 > 500ms"] --> Metrics["📊 Метрики: RED дашборд"]
    Metrics --> |"Какой endpoint?"| Traces["🔗 Трейсы: waterfall view"]
    Traces --> |"Какой span медленный?"| Logs["📝 Логи: фильтр по traceId"]
    Logs --> |"Root cause"| Fix["🔧 Исправление"]

    style Alert fill:#f66,color:#fff
    style Fix fill:#6c6,color:#fff
```

Связка работает только при наличии единого контекста (`traceId`), который пронизывает все три сигнала.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Какие метрики критичны для backend-сервиса?

Минимальный базовый набор:
- `RED`: `request_rate`, `error_rate`, `request_duration` (p50/p95/p99),
- saturation: `cpu`, `memory`, queue depth, pool utilization,
- бизнес-метрики: успешные оплаты, конверсия, отказ транзакций.

```java
// Пример кастомной бизнес-метрики с Micrometer
@Component
public class PaymentMetrics {
    private final Counter successfulPayments;
    private final Counter failedPayments;
    private final Timer paymentDuration;

    public PaymentMetrics(MeterRegistry registry) {
        this.successfulPayments = Counter.builder("payments.success")
            .description("Number of successful payments")
            .tag("currency", "RUB")
            .register(registry);
        this.failedPayments = Counter.builder("payments.failed")
            .description("Number of failed payments")
            .tag("reason", "unknown")
            .register(registry);
        this.paymentDuration = Timer.builder("payments.duration")
            .description("Payment processing time")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
    }

    public void recordSuccess(Duration duration) {
        successfulPayments.increment();
        paymentDuration.record(duration);
    }
}
```

Антипаттерн: только технические метрики без бизнес-контекста — невозможно оценить реальный impact на пользователей.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Что такое RED и USE методы и когда какой применять?

**RED** (Tom Wilkie, Weaveworks) — для сервисов, обрабатывающих запросы:
- **R**ate — количество запросов в секунду
- **E**rrors — количество/доля ошибок
- **D**uration — распределение latency (гистограмма)

**USE** (Brendan Gregg) — для инфраструктурных ресурсов (CPU, память, диск, сеть):
- **U**tilization — процент использования ресурса
- **S**aturation — очередь ожидающих (queue depth)
- **E**rrors — ошибки ресурса (disk I/O errors, packet drops)

| Метод | Когда применять | Пример |
|-------|----------------|--------|
| `RED` | Application-level мониторинг | HTTP endpoints, gRPC сервисы |
| `USE` | Infrastructure-level мониторинг | CPU, memory, connection pools |

Практический совет: начинайте с `RED` для сервисов, добавляйте `USE` для ресурсов, подключайте бизнес-метрики для product-контекста.

```java
// RED-метрики автоматически через Spring Boot Actuator + Micrometer
// application.yml
// management:
//   endpoints:
//     web:
//       exposure:
//         include: prometheus,health,info
//   metrics:
//     distribution:
//       percentiles-histogram:
//         http.server.requests: true    # гистограмма latency
//       slo:
//         http.server.requests: 50ms,100ms,200ms,500ms
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как устроен Prometheus и его модель данных?

`Prometheus` — это pull-based система мониторинга с встроенной TSDB (time-series database).

Ключевые компоненты:
- **Prometheus Server** — скрейпит метрики по HTTP, хранит в TSDB, выполняет `PromQL`-запросы
- **Exporters** — адаптеры для систем без нативной поддержки (JMX exporter, node_exporter)
- **Alertmanager** — маршрутизация и дедупликация алертов
- **Push Gateway** — для short-lived jobs (batch, cron)

```mermaid
graph LR
    App1["Spring Boot /actuator/prometheus"] -->|pull| Prom[Prometheus Server]
    App2["Node Exporter"] -->|pull| Prom
    App3["JMX Exporter"] -->|pull| Prom
    Batch["Batch Job"] -->|push| PGW[Push Gateway]
    PGW -->|pull| Prom
    Prom --> AM[Alertmanager]
    Prom --> Grafana[Grafana]
    AM --> PD[PagerDuty/Slack]
```

Модель данных: каждая time series — это уникальная комбинация имени метрики и набора лейблов:

```
http_requests_total{method="GET", handler="/api/users", status="200"} 12345
```

Пример конфигурации `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alerts/*.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    static_configs:
      - targets: ['app1:8080', 'app2:8080']
    # В Kubernetes — используйте kubernetes_sd_configs
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Какие типы метрик есть в Prometheus и когда какой использовать?

| Тип | Описание | Когда использовать | Пример |
|-----|----------|-------------------|--------|
| `Counter` | Монотонно растущее значение | Количество запросов, ошибок | `http_requests_total` |
| `Gauge` | Значение, которое может расти и падать | Температура, текущие соединения | `jvm_memory_used_bytes` |
| `Histogram` | Распределение значений по бакетам | Latency, размер ответа | `http_request_duration_seconds` |
| `Summary` | Клиент-side квантили | Когда нужны точные квантили для одного инстанса | `rpc_duration_seconds` |

**`Histogram` vs `Summary`** — частый вопрос на собеседовании:

| Аспект | `Histogram` | `Summary` |
|--------|------------|-----------|
| Агрегация | Можно агрегировать между инстансами | Нельзя агрегировать квантили |
| Вычисление | Квантили считает сервер (PromQL) | Квантили считает клиент |
| Точность | Зависит от бакетов | Точные для одного инстанса |
| Рекомендация | **Предпочтительнее** в большинстве случаев | Редко нужен |

```java
// Histogram в Micrometer — автоматически создаёт бакеты
Timer.builder("order.processing.time")
    .publishPercentileHistogram()          // Prometheus histogram
    .minimumExpectedValue(Duration.ofMillis(1))
    .maximumExpectedValue(Duration.ofSeconds(10))
    .register(registry);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Что такое cardinality и почему она опасна?

**Cardinality** — это количество уникальных комбинаций лейблов для метрики. Каждая уникальная комбинация — это отдельная time series в TSDB.

Пример взрыва cardinality:

```
# Безопасно: ~20 уникальных time series
http_requests_total{method="GET", status="200", handler="/api/users"}

# ОПАСНО: миллионы time series!
http_requests_total{method="GET", status="200", user_id="12345678"}
```

Почему это критично:
- Память `Prometheus` растёт линейно с числом time series
- Запросы по метрикам с высокой cardinality тормозят или OOM-ят сервер
- На практике ≥ 100K time series на один инстанс уже проблема

Правила контроля cardinality:
1. **Никогда** не используйте в лейблах: `userId`, `requestId`, `sessionId`, `email`, `IP`
2. Проверяйте cardinality заранее: `count by (__name__)({__name__=~".+"})` в PromQL
3. Используйте `relabel_configs` для дропа ненужных лейблов на этапе scrape
4. Установите `sample_limit` в `scrape_configs`

```yaml
# prometheus.yml — защита от cardinality explosion
scrape_configs:
  - job_name: 'app'
    sample_limit: 10000   # максимум time series с одного target
    metric_relabel_configs:
      - source_labels: [__name__]
        regex: 'go_.*'    # дропаем ненужные go-метрики
        action: drop
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как подключить Prometheus-метрики в Spring Boot приложении?

Минимальная настройка в три шага:

```groovy
// build.gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info,metrics
  metrics:
    tags:
      application: ${spring.application.name}  # общий лейбл для всех метрик
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 50ms,100ms,250ms,500ms,1s
```

Что получаете автоматически (out of the box):
- `http_server_requests_seconds` — RED-метрики для всех HTTP endpoints
- `jvm_memory_*`, `jvm_gc_*` — JVM метрики
- `hikaricp_*` — метрики connection pool
- `spring_data_repository_*` — метрики репозиториев (если Spring Data)

Кастомные метрики добавляются через `MeterRegistry`:

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final MeterRegistry meterRegistry;

    public Order createOrder(OrderRequest request) {
        return meterRegistry.timer("orders.create",
                "type", request.getType().name())
            .record(() -> doCreateOrder(request));
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Что такое structured logging и зачем он нужен?

**Structured logging** — это запись логов в машиночитаемом формате (обычно `JSON`), вместо произвольного текста.

Сравнение:

```
# Неструктурированный лог — сложно парсить, невозможно фильтровать
2026-04-11 10:15:32 ERROR OrderService - Failed to create order for user 12345: timeout

# Структурированный лог — легко искать, фильтровать, агрегировать
{
  "timestamp": "2026-04-11T10:15:32.456Z",
  "level": "ERROR",
  "logger": "com.app.OrderService",
  "message": "Failed to create order",
  "userId": 12345,
  "orderId": "ORD-789",
  "error": "timeout",
  "traceId": "abc123def456",
  "spanId": "span789",
  "service": "order-service",
  "duration_ms": 5023
}
```

Преимущества structured logging:
- **Поиск и фильтрация** в `Elasticsearch`/`Loki` по конкретным полям
- **Корреляция** с трейсами через `traceId`/`spanId`
- **Агрегация** — COUNT по `error`, GROUP BY `userId`
- **Алертинг** на конкретные паттерны

Ключевые правила:
1. Всегда включайте `traceId` и `spanId` в лог-контекст
2. Не логируйте PII (Personal Identifiable Information) в открытом виде
3. Используйте фиксированное сообщение + поля, а не string interpolation
4. Уровни: `ERROR` — требует действия, `WARN` — внимание, `INFO` — бизнес-события, `DEBUG` — только локально


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как устроен ELK-стек и чем отличается EFK?

**ELK** = `Elasticsearch` + `Logstash` + `Kibana`:

```mermaid
graph LR
    App1[Приложение 1] -->|stdout/file| Logstash
    App2[Приложение 2] -->|stdout/file| Logstash
    Logstash -->|index| ES[Elasticsearch]
    ES --> Kibana
```

**EFK** = `Elasticsearch` + `Fluentd`/`Fluent Bit` + `Kibana`:

| Компонент | ELK (`Logstash`) | EFK (`Fluentd`/`Fluent Bit`) |
|-----------|-----------------|------------------------------|
| Язык | JRuby (JVM) | C + Ruby / чистый C |
| Потребление RAM | 500MB-1GB+ | 30-50MB (`Fluent Bit`) |
| Плагины | Богатая экосистема | Богатая экосистема |
| Kubernetes | Тяжеловат | Де-факто стандарт в K8s |

Альтернатива обоим стекам — **Grafana Loki**:
- Не индексирует содержимое логов (только лейблы) — дешевле в 10-50x
- Использует тот же язык запросов `LogQL` (похож на `PromQL`)
- Нативная интеграция с `Grafana`

Когда что выбирать:
- `ELK` — нужен полнотекстовый поиск по логам, большие объёмы аналитики
- `Loki` — бюджет ограничен, логи нужны для troubleshooting, а не аналитики
- `EFK` — `Kubernetes`-native окружение, нужен лёгкий агент


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как настроить structured logging в Spring Boot?

Начиная со `Spring Boot 3.4` есть встроенная поддержка structured logging:

```yaml
# application.yml (Spring Boot 3.4+)
logging:
  structured:
    format:
      console: ecs   # Elastic Common Schema
    # Альтернативы: logstash, gelf
```

Для более ранних версий — через `Logback` + `logstash-logback-encoder`:

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdcKeyName>traceId</includeMdcKeyName>
            <includeMdcKeyName>spanId</includeMdcKeyName>
            <customFields>
                {"service":"order-service","env":"${ENV:-dev}"}
            </customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

Добавление контекста через `MDC` (Mapped Diagnostic Context):

```java
import org.slf4j.MDC;

@RestController
public class OrderController {

    @PostMapping("/orders")
    public Order createOrder(@RequestBody OrderRequest request) {
        MDC.put("userId", request.getUserId());
        MDC.put("orderType", request.getType().name());
        try {
            log.info("Creating order");  // userId и orderType попадут в JSON
            return orderService.create(request);
        } finally {
            MDC.clear();
        }
    }
}
```

При использовании `OpenTelemetry` — `traceId`/`spanId` добавляются в `MDC` автоматически через `opentelemetry-logback-mdc` или Java agent.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) Как работает distributed tracing и из чего состоит трейс?

**Distributed tracing** отслеживает путь запроса через множество сервисов. Основные понятия:

- **Trace** — полный путь одного запроса через систему (дерево span'ов)
- **Span** — единица работы в одном сервисе (HTTP-вызов, запрос к БД, обращение к кэшу)
- **TraceId** — уникальный идентификатор трейса (128 бит в W3C формате)
- **SpanId** — уникальный идентификатор span'а
- **Parent SpanId** — ссылка на родительский span
- **Baggage** — данные, пробрасываемые через все сервисы

```mermaid
gantt
    title Distributed Trace: POST /api/orders
    dateFormat X
    axisFormat %L ms

    section API Gateway
    gateway.request           :0, 350

    section Order Service
    order.createOrder         :20, 300
    order.validateRequest     :30, 50

    section Payment Service
    payment.charge            :90, 150

    section Database
    db.insertOrder            :250, 70
```

Формат контекста `W3C Trace Context` (стандарт):

```
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
              |  |                                |                  |
              v  v                                v                  v
           version  trace-id (128 bit)        parent-id (64 bit)   flags
```

Этот заголовок автоматически передаётся между сервисами через HTTP headers, Kafka headers, gRPC metadata.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Чем отличаются Jaeger и Zipkin?

Оба — open-source distributed tracing backends. Основные отличия:

| Аспект | `Jaeger` | `Zipkin` |
|--------|---------|---------|
| Автор | Uber → CNCF | Twitter |
| Язык | Go | Java |
| Хранение | `Elasticsearch`, `Cassandra`, `Kafka`, `Badger` | `Elasticsearch`, `Cassandra`, MySQL, in-memory |
| Adaptive sampling | Да (встроенный) | Нет (только фиксированный) |
| UI | Более функциональный, DAG view | Простой, удобный |
| OpenTelemetry | Нативная поддержка OTLP | Требует Zipkin exporter |
| Масштабирование | Лучше для крупных систем | Проще для малых |

На практике сейчас оба уступают место `Grafana Tempo`, который:
- Принимает данные через `OTLP` (протокол `OpenTelemetry`)
- Не требует индексации (поиск по `traceId`)
- Значительно дешевле в хранении (object storage: S3, GCS)
- Нативно интегрирован с `Grafana`


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Как организовать связку traceId между логами и трейсами?

Для полноценной корреляции нужно:

1. Включить `OpenTelemetry` auto/manual instrumentation
2. Пробрасывать контекст между сервисами и async-цепочками
3. Писать `traceId` и `spanId` в structured logs (`JSON`)
4. В UI (`Grafana`/`Kibana`) обеспечить переход лог <-> trace

```java
// С OpenTelemetry Java Agent — traceId/spanId добавляются в MDC автоматически
// Запуск: java -javaagent:opentelemetry-javaagent.jar -jar app.jar

// Логи автоматически содержат:
// {"message":"Order created","traceId":"abc123","spanId":"def456",...}

// Для ручного добавления (без agent):
import io.opentelemetry.api.trace.Span;

public void processOrder(Order order) {
    Span currentSpan = Span.current();
    MDC.put("traceId", currentSpan.getSpanContext().getTraceId());
    MDC.put("spanId", currentSpan.getSpanContext().getSpanId());

    log.info("Processing order {}", order.getId());
}
```

В `Grafana` настраивается переход Logs → Traces через derived fields:

```
# В datasource Loki → Derived Fields:
Name: traceId
Regex: "traceId":"(\w+)"
Internal link → Tempo datasource
```

Проверка готовности: можно за 1-2 клика перейти от алерта к конкретному stack trace.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Что такое exemplars и когда они реально полезны?

`Exemplar` связывает точку на графике метрики с конкретным трейсом.
Это особенно полезно при редких всплесках latency, когда нужно быстро найти "плохой" запрос.

```java
// Micrometer с поддержкой exemplars (Spring Boot 3+)
// Exemplar автоматически привязывает traceId к каждому наблюдению гистограммы

// В Prometheus это выглядит так:
// http_server_requests_seconds_bucket{method="GET",uri="/api/orders",le="0.5"} 1234
//   # {trace_id="abc123"} 0.487 1617802000.000
```

Когда польза максимальна:
- Есть гистограммы latency
- Есть трассировка с достаточным sampling
- Команда реально расследует инциденты через метрики
- Используете `Grafana` 9+ с поддержкой exemplars на графиках

В `Grafana` exemplars отображаются как кликабельные точки на графике — клик открывает трейс в `Tempo`/`Jaeger`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Что такое OpenTelemetry и почему это стандарт де-факто?

`OpenTelemetry` (`OTel`) — это vendor-neutral фреймворк для сбора, обработки и экспорта телеметрии (метрики, логи, трейсы). Результат слияния `OpenTracing` и `OpenCensus` под эгидой `CNCF`.

Почему это стандарт:
- **Vendor-neutral** — один SDK, экспорт в любой backend (`Prometheus`, `Jaeger`, `Datadog`, `New Relic`)
- **Единый контекст** — `traceId` связывает метрики, логи и трейсы
- **Автоинструментация** — Java agent без изменения кода
- **CNCF Graduated** — поддержка от всех major vendors
- **W3C Trace Context** — стандартизированный формат передачи контекста

```mermaid
graph TB
    subgraph "Приложение (Java)"
        SDK["OTel SDK / Java Agent"]
        Auto["Auto-instrumentation"]
        Manual["Manual instrumentation"]
        Auto --> SDK
        Manual --> SDK
    end

    SDK -->|OTLP| Collector["OTel Collector"]

    Collector -->|metrics| Prometheus
    Collector -->|traces| Tempo["Grafana Tempo"]
    Collector -->|logs| Loki["Grafana Loki"]

    Prometheus --> Grafana
    Tempo --> Grafana
    Loki --> Grafana
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Как устроена архитектура OpenTelemetry?

`OpenTelemetry` состоит из нескольких компонентов:

**API** — стабильный интерфейс для инструментации кода:
- `Tracer` — создание span'ов
- `Meter` — создание метрик
- `Logger` — структурированные логи (в разработке)

**SDK** — реализация API с конфигурацией экспорта, sampling, обработки:
- `SpanProcessor` — батчинг и отправка span'ов
- `SpanExporter` — куда отправлять (OTLP, Jaeger, Zipkin)
- `Sampler` — какой процент трейсов сохранять

**Auto-instrumentation** — Java agent, который инструментирует:
- HTTP-клиенты (`HttpClient`, `OkHttp`, `RestTemplate`, `WebClient`)
- JDBC-драйверы
- Kafka producer/consumer
- gRPC, Redis, MongoDB и другие
- Servlet/Spring MVC контроллеры

**Протокол `OTLP`** (OpenTelemetry Protocol) — единый бинарный протокол для отправки всех типов телеметрии. Поддерживает gRPC и HTTP/protobuf.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Как инструментировать Java-приложение с помощью OpenTelemetry?

**Вариант 1: Java Agent (zero-code)**

```bash
# Скачиваем agent
curl -LO https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# Запускаем приложение
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=order-service \
  -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
  -Dotel.metrics.exporter=prometheus \
  -Dotel.logs.exporter=otlp \
  -jar app.jar
```

**Вариант 2: Spring Boot Starter (конфигурация через application.yml)**

```groovy
// build.gradle
dependencies {
    implementation 'io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:2.12.0'
}
```

```yaml
# application.yml
otel:
  service:
    name: order-service
  exporter:
    otlp:
      endpoint: http://otel-collector:4317
```

**Вариант 3: Manual instrumentation (для кастомных span'ов)**

```java
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@Service
public class OrderService {
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("order-service");

    public Order processOrder(OrderRequest request) {
        Span span = tracer.spanBuilder("processOrder")
            .setAttribute("order.type", request.getType().name())
            .setAttribute("order.items.count", request.getItems().size())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            Order order = validateAndCreate(request);
            span.setAttribute("order.id", order.getId());
            return order;
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

С `@WithSpan` аннотацией (требует agent или Spring starter):

```java
@Service
public class PaymentService {

    @WithSpan("chargePayment")
    public PaymentResult charge(
            @SpanAttribute("payment.amount") BigDecimal amount,
            @SpanAttribute("payment.currency") String currency) {
        // span создаётся автоматически
        return gateway.charge(amount, currency);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. Что такое OpenTelemetry Collector и зачем он нужен?

`OTel Collector` — это прокси/агрегатор телеметрии, стоящий между приложениями и backends.

```mermaid
graph LR
    App1[Service A] -->|OTLP| Collector
    App2[Service B] -->|OTLP| Collector
    App3[Service C] -->|OTLP| Collector

    subgraph "OTel Collector"
        R[Receivers] --> P[Processors]
        P --> E[Exporters]
    end

    Collector -->|remote write| Prometheus
    Collector -->|OTLP| Tempo
    Collector -->|OTLP| Loki
    Collector -->|OTLP| Datadog
```

Пайплайн Collector: **Receivers → Processors → Exporters**

Пример конфигурации:

```yaml
# otel-collector-config.yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:
    timeout: 5s
    send_batch_size: 1024
  memory_limiter:
    check_interval: 1s
    limit_mib: 512
  attributes:
    actions:
      - key: environment
        value: production
        action: upsert

exporters:
  otlp/tempo:
    endpoint: tempo:4317
    tls:
      insecure: true
  prometheus:
    endpoint: 0.0.0.0:8889
  loki:
    endpoint: http://loki:3100/loki/api/v1/push

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [memory_limiter, batch]
      exporters: [otlp/tempo]
    metrics:
      receivers: [otlp]
      processors: [memory_limiter, batch]
      exporters: [prometheus]
    logs:
      receivers: [otlp]
      processors: [memory_limiter, attributes, batch]
      exporters: [loki]
```

Зачем Collector, а не прямой экспорт:
- **Буферизация** — приложение не блокируется при недоступности backend
- **Обработка** — фильтрация, добавление атрибутов, tail-based sampling
- **Роутинг** — одна точка для отправки в несколько backends
- **Изоляция** — смена backend не требует передеплоя приложений


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Как устроен стек Grafana и что входит в LGTM?

**LGTM** — это open-source observability stack от `Grafana Labs`:

| Компонент | Назначение | Язык запросов |
|-----------|-----------|---------------|
| **L**oki | Агрегация логов | `LogQL` |
| **G**rafana | Визуализация и дашборды | — |
| **T**empo | Хранение трейсов | `TraceQL` |
| **M**imir | Long-term storage метрик (Prometheus-совместимый) | `PromQL` |

```mermaid
graph TB
    subgraph "LGTM Stack"
        Mimir["Mimir (Метрики)"]
        Loki["Loki (Логи)"]
        Tempo["Tempo (Трейсы)"]
        Grafana["Grafana (UI)"]
    end

    OTel["OTel Collector"] --> Mimir
    OTel --> Loki
    OTel --> Tempo

    Mimir --> Grafana
    Loki --> Grafana
    Tempo --> Grafana

    Grafana --> Alert["Alerting"]
    Grafana --> Dash["Dashboards"]
```

Преимущества `LGTM` над `ELK`:
- Единый UI для всех сигналов
- `Loki` значительно дешевле `Elasticsearch` (индексирует только лейблы)
- Нативная корреляция между метриками, логами и трейсами
- `Tempo` не требует индексации — хранит трейсы в object storage


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Как проектировать эффективные дашборды?

Принципы проектирования дашбордов (по Brad Buehler / Grafana Labs):

**Уровни дашбордов:**
1. **Overview / Service Map** — общая здоровье системы, SLO compliance
2. **Service-level** — RED-метрики конкретного сервиса
3. **Debug** — детальные метрики для расследования

**Правила:**
- Каждый дашборд отвечает на один вопрос
- Первый ряд — ключевые SLI (зелёный/красный статус)
- Time range по умолчанию — последние 1-6 часов (не "last 24h")
- Используйте template variables (`$service`, `$environment`)
- Добавляйте ссылки на runbook и связанные дашборды

**Типичная структура service-дашборда:**

```
┌─────────────────────────────────────────────┐
│  SLO Status: 99.95% ✓    Error Budget: 72%  │
├──────────────┬──────────────┬───────────────┤
│ Request Rate │  Error Rate  │   Latency     │
│   (R.E.D.)   │              │  p50/p95/p99  │
├──────────────┴──────────────┴───────────────┤
│         Saturation: CPU / Memory / Pools    │
├─────────────────────────────────────────────┤
│         Business Metrics: Orders / Revenue  │
└─────────────────────────────────────────────┘
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) Как объяснить SLI, SLO, SLA и error budget на практике?

| Термин | Определение | Пример |
|--------|------------|--------|
| `SLI` (Service Level Indicator) | Измеритель качества сервиса | Доля успешных запросов (2xx/3xx) |
| `SLO` (Service Level Objective) | Внутренняя цель по SLI | 99.9% успешности за 30 дней |
| `SLA` (Service Level Agreement) | Контракт с клиентом (юридический) | 99.5% uptime, иначе кредиты |
| `Error budget` | Допустимая доля деградации | 0.1% = 43.2 мин/мес downtime |

Ключевая разница `SLO` vs `SLA`:
- `SLO` — внутренний ориентир, строже `SLA`
- `SLA` — внешний контракт с финансовыми последствиями
- Типично: `SLO` = 99.9%, `SLA` = 99.5%

Практический смысл error budget: когда бюджет исчерпан, команда замораживает risky-релизы и инвестирует в надёжность.

Типичные `SLI` для backend-сервиса:
- **Availability** — доля успешных запросов
- **Latency** — доля запросов быстрее порога (p99 < 500ms)
- **Correctness** — доля запросов с корректным результатом
- **Freshness** — данные не старше N секунд

```
# PromQL: SLI — доля успешных запросов за 30 дней
sum(rate(http_requests_total{status=~"2.."}[30d]))
/
sum(rate(http_requests_total[30d]))
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Как проектировать алертинг без alert fatigue?

Принципы:
- Алертить по **пользовательскому impact**, а не по шумным низкоуровневым метрикам
- Разделять severity: `page` (разбудить) / `ticket` (рабочий день) / `info` (FYI)
- Добавлять **runbook** к каждому алерту
- Делать **dedup** и **suppression** (группировка связанных алертов)

**Правило: каждый page-алерт должен требовать немедленного человеческого действия.** Если на алерт можно не реагировать — это не page.

Структура хорошего алерта:
```yaml
# Prometheus alerting rule
groups:
  - name: slo-alerts
    rules:
      - alert: HighErrorRate
        expr: |
          sum(rate(http_requests_total{status=~"5.."}[5m]))
          /
          sum(rate(http_requests_total[5m])) > 0.01
        for: 5m
        labels:
          severity: page
          team: backend
        annotations:
          summary: "Error rate > 1% для {{ $labels.service }}"
          description: "Текущий error rate: {{ $value | humanizePercentage }}"
          runbook: "https://wiki.example.com/runbooks/high-error-rate"
          dashboard: "https://grafana.example.com/d/svc-overview"
```

Anti-pattern: сотни алертов "на всякий случай", которые никто не обслуживает. Каждый неработающий алерт снижает доверие ко всему алертингу.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Что такое burn rate alerting и чем оно лучше threshold-алертов?

**Burn rate** — скорость расходования error budget. Если burn rate = 1, бюджет закончится ровно к концу окна. Если burn rate = 10, бюджет закончится в 10 раз быстрее.

Преимущество перед threshold-алертами:
- Учитывает **скорость деградации**, а не абсолютное значение
- Меньше false positives — кратковременный всплеск ошибок не вызывает page
- Привязан к `SLO` — алерт срабатывает, когда бюджет реально под угрозой

Типичная multi-window стратегия (Google SRE Book):

| Burn rate | Окно | Severity | Смысл |
|-----------|------|----------|-------|
| 14.4x | 1 час (5 мин short) | Page | Бюджет кончится за 2 дня |
| 6x | 6 часов (30 мин short) | Page | Бюджет кончится за 5 дней |
| 1x | 3 дня (6 часов short) | Ticket | Расходуем бюджет нормально |

```
# PromQL: burn rate для SLO 99.9% за 30 дней
# Быстрый burn (1-часовое окно)
(
  1 - (sum(rate(http_requests_total{status=~"2.."}[1h]))
       / sum(rate(http_requests_total[1h])))
) / 0.001 > 14.4
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Какие стратегии sampling существуют для трейсов?

100% трейсов в production — это слишком дорого. Sampling решает эту проблему:

| Стратегия | Описание | Плюсы | Минусы |
|-----------|----------|-------|--------|
| **Head-based** | Решение на входе (вероятностный %) | Простота, предсказуемость | Может потерять интересные трейсы |
| **Tail-based** | Решение после завершения трейса | Сохраняет ошибки и медленные | Требует буферизации всех span'ов |
| **Rate limiting** | N трейсов в секунду | Предсказуемый объём | Теряет данные при всплесках |
| **Adaptive** | Динамический % на основе нагрузки | Балансирует стоимость и покрытие | Сложность настройки |

Рекомендация для production:
1. **Head-based** с 10-20% для обычного трафика
2. **Always sample** ошибки (`status_code >= 500`) и медленные запросы (> SLO)
3. **Tail-based** в `OTel Collector` для финальной фильтрации

```yaml
# OTel Collector — tail-based sampling
processors:
  tail_sampling:
    decision_wait: 10s
    policies:
      - name: errors
        type: status_code
        status_code: {status_codes: [ERROR]}
      - name: slow-requests
        type: latency
        latency: {threshold_ms: 1000}
      - name: probabilistic
        type: probabilistic
        probabilistic: {sampling_percentage: 10}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. Как управлять стоимостью логов и трейсов?

Рабочие рычаги:

1. **Tiered retention** — горячее/тёплое/холодное хранение:
   - Hot (SSD): последние 3-7 дней — быстрый поиск
   - Warm (HDD): 30 дней — медленнее, дешевле
   - Cold (S3/GCS): 90-365 дней — архив, compliance

2. **Фильтрация на уровне агента**:
   - Drop health-check логов (`/actuator/health`, `/healthz`)
   - Не собирать `DEBUG`/`TRACE` в production
   - Фильтрация по severity в `OTel Collector`

3. **Sampling стратегии** (см. Q26)

4. **Оптимизация индексации**:
   - `Loki`: индексирует только лейблы, не содержимое
   - `Elasticsearch`: используйте `ILM` (Index Lifecycle Management)

5. **Отчётность**: стоимость наблюдаемости должна быть видна рядом с SLA/SLO, иначе оптимизация не приоритизируется.

Практический ориентир: стоимость observability — 5-15% от стоимости инфраструктуры приложения. Если больше — пора оптимизировать.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. Как запускать observability в production без перегруза системы?

Ключевые практики:

- **Sampling** для трейсов (база + приоритет ошибок)
- **Разумные уровни логирования**: `INFO`/`WARN` по умолчанию, `DEBUG` — через dynamic log level (runtime)
- **Контроль cardinality** меток (см. Q8)
- **Асинхронная отправка** телеметрии с backpressure
- **Отдельный пул потоков** для экспорта телеметрии
- **Circuit breaker** на отправку — при недоступности backend телеметрия дропается, а не копится

```java
// Пример: динамическое изменение уровня логирования через Actuator
// POST /actuator/loggers/com.app.OrderService
// {"configuredLevel": "DEBUG"}

// Через 30 минут вернуть обратно:
// POST /actuator/loggers/com.app.OrderService
// {"configuredLevel": "INFO"}
```

Нужен баланс: слишком мало данных ломает расследования, слишком много данных ломает бюджет и latency.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. Как выбрать стек observability под команду и бюджет?

| Сценарий | Рекомендация |
|----------|-------------|
| Small team, low budget | `Prometheus` + `Grafana` + `Loki` + `Tempo` (LGTM) |
| Enterprise, multi-team | Managed: `Datadog`, `New Relic`, `Dynatrace` |
| Гибрид | Метрики self-hosted (`Prometheus`/`Mimir`), логи/трейсы managed |
| Kubernetes-native | `LGTM` + `OTel Collector` + `Grafana Alloy` |
| Legacy + Cloud | `ELK` для логов + `Prometheus` для метрик |

Критерии выбора:
- **Стоимость владения** (TCO): self-hosted требует FTE на поддержку
- **Зрелость команды**: managed проще, но дороже
- **Интеграции**: `OTel`-совместимость — обязательна
- **Vendor lock-in**: `OpenTelemetry` минимизирует зависимость от backend
- **Требования по хранению**: compliance, retention period


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. Как observability помогает в инциденте и постмортеме?

**Во время инцидента:**
1. Фиксируем impact по SLI/бизнес-метрикам — "сколько пользователей затронуто"
2. Локализуем деградацию по trace/service map — "какой сервис виноват"
3. Подтверждаем причину логами — "конкретная ошибка"
4. Проверяем корреляцию с деплоем/изменением конфигурации

**В постмортеме:**
- Формируем **timeline** инцидента по данным observability
- Добавляем контрольные метрики и алерты, чтобы ловить проблему раньше
- Закрываем пробелы в runbook
- Фиксируем preventative actions с дедлайнами

Формат timeline:
```
10:15 — Алерт: error rate > 1% (order-service)
10:17 — Дашборд: p99 latency 2.3s (обычно 200ms)
10:19 — Trace: span payment.charge → timeout 5s
10:22 — Логи: "Connection refused: payment-gateway:443"
10:25 — Причина: certificate expired на payment-gateway
10:28 — Fix: обновлён сертификат, restart
10:32 — Recovery: error rate < 0.1%, p99 = 180ms
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. Как внедрять observability в CI/CD и релизный процесс?

- На **pre-prod** проверять обязательные метрики/логи/трейсы для новых endpoints
- При **canary** сравнивать SLI новой и старой версии автоматически
- **Rollback** по guardrail-метрикам (error rate, latency)
- Хранить связь release → dashboard → trace samples

```mermaid
graph LR
    Deploy["Deploy (Canary 5%)"] --> Compare["Сравнить SLI"]
    Compare -->|"SLI OK"| Promote["Promote → 100%"]
    Compare -->|"SLI degraded"| Rollback["Auto Rollback"]

    Promote --> Validate["Post-deploy validation"]
    Validate -->|"30 min OK"| Done["✓ Release complete"]
    Validate -->|"Degradation"| Rollback
```

Пример guardrail-проверки:

```yaml
# Argo Rollouts — analysis template
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
spec:
  metrics:
    - name: error-rate
      provider:
        prometheus:
          address: http://prometheus:9090
          query: |
            sum(rate(http_requests_total{status=~"5..",app="{{args.service}}",version="{{args.version}}"}[5m]))
            /
            sum(rate(http_requests_total{app="{{args.service}}",version="{{args.version}}"}[5m]))
      successCondition: result[0] < 0.01
      interval: 60s
      count: 5
```

Так observability становится частью качества релиза, а не "послерелизной задачей".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q32. (!) Какие anti-patterns в observability встречаются чаще всего?

| Anti-pattern | Проблема | Решение |
|-------------|----------|---------|
| "Собираем всё подряд" | Огромные расходы, шум | Определить SLI, собирать целенаправленно |
| Логи без структуры | Невозможно искать и фильтровать | Structured logging (JSON) |
| Логи без `traceId` | Нет корреляции с трейсами | `OpenTelemetry` + MDC |
| High cardinality лейблы | OOM Prometheus, медленные запросы | Никогда `userId`/`requestId` в лейблах |
| Алертинг без runbook | Инцидент = паника, долгий MTTR | Runbook обязателен для page-алертов |
| Нет бизнес-метрик | Непонятен impact на пользователей | Добавить: конверсия, оплаты, отказы |
| Copy-paste дашборды | Дрифт, никто не поддерживает | Dashboard-as-code (Grafonnet, Terraform) |
| `DEBUG` в production | Огромный объём логов, деградация I/O | Dynamic log levels через Actuator |
| Все метрики — `Gauge` | Потеря данных при scrape gaps | Использовать `Counter` для rate-метрик |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q33. Как оценить зрелость observability в команде?

Краткая шкала:

| Уровень | Описание | Признаки |
|---------|----------|----------|
| **L1** | Базовый мониторинг | Есть графики, нет actionable алертов |
| **L2** | SLO-driven | Определены SLI/SLO, есть runbooks |
| **L3** | Корреляция | Кросс-функциональная диагностика метрики+логи+трейсы |
| **L4** | Встроенная | Observability в SDLC, CI/CD guardrails, auto-rollback |
| **L5** | Проактивная | Anomaly detection, capacity planning, chaos engineering |

Практический критерий зрелости: **MTTD** (Mean Time to Detect) и **MTTR** (Mean Time to Resolve) снижаются, а не растут вместе с масштабом системы.

Чек-лист для самооценки:
- [ ] Все сервисы экспортируют RED-метрики
- [ ] Structured logging с `traceId` во всех сервисах
- [ ] Distributed tracing покрывает все inter-service вызовы
- [ ] SLO определены для критичных сервисов
- [ ] Алерты имеют runbooks и severity levels
- [ ] Дашборды организованы по уровням (overview → service → debug)
- [ ] Observability проверяется в CI/CD pipeline
- [ ] Команда проводит регулярные "observability reviews"

---

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q34. OpenTelemetry Collector: архитектура, pipeline и processors?

**OpenTelemetry Collector** — независимый компонент для получения, обработки и экспорта телеметрии (метрики, логи, трейсы). Избавляет от необходимости конфигурировать каждый SDK для каждого backend.

**Архитектура Collector:**

```
[Receivers]  →  [Processors]  →  [Exporters]
    ↑                                 ↓
OTLP, Jaeger,              Prometheus, Jaeger,
Prometheus,                OTLP, Loki, Tempo,
Fluent Bit, ...            Elasticsearch, ...
```

**Pipeline в конфигурации:**

```yaml
# otel-collector-config.yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318
  prometheus:
    config:
      scrape_configs:
        - job_name: 'myapp'
          static_configs:
            - targets: ['localhost:8080']

processors:
  batch:              # Буферизация для снижения нагрузки на backend
    timeout: 5s
    send_batch_size: 1000
  memory_limiter:     # Ограничение памяти Collector
    limit_mib: 512
  filter/errors:      # Фильтрация трейсов только с ошибками (tail sampling)
    traces:
      span:
        - 'status.code == STATUS_CODE_ERROR'
  resource:           # Добавление атрибутов ко всем данным
    attributes:
      - key: environment
        value: production
        action: insert

exporters:
  otlp:
    endpoint: tempo:4317  # Grafana Tempo
  prometheus:
    endpoint: "0.0.0.0:8889"
  loki:
    endpoint: http://loki:3100/loki/api/v1/push

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [memory_limiter, batch, filter/errors]
      exporters: [otlp]
    metrics:
      receivers: [otlp, prometheus]
      processors: [memory_limiter, batch, resource]
      exporters: [prometheus]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [loki]
```

**Режимы деплоя:**

| Режим | Описание | Когда использовать |
|-------|---------|-------------------|
| **Agent** (sidecar) | Рядом с каждым сервисом | Низкая задержка, изоляция |
| **Gateway** (централизованный) | Один Collector на кластер | Упрощение конфигурации, tail-sampling |
| **Комбинированный** | Agent → Gateway | Production с тысячами сервисов |

**Ключевые processors:**
- `batch` — буферизация (обязательно для performance)
- `memory_limiter` — защита от OOM Collector
- `tail_sampling` — решение о sampling после получения всего трейса
- `probabilistic_sampler` — статистический sampling (head-based)
- `filter` — удаление ненужных данных

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q35. Continuous Profiling: Pyroscope, Grafana Phlare, eBPF?

**Continuous Profiling** — постоянный сбор профилей производительности в production, в отличие от разового профилирования при расследовании. Четвёртый столп observability.

**Что даёт:**
- Видит, где CPU/memory тратится на уровне функций и строк кода
- В отличие от трейсов — видит все вызовы, не только сэмплированные
- Позволяет сравнивать профили между релизами (regression detection)

**Pyroscope** (eBPF + языковые агенты):

```java
// Java агент через JVMTI (без изменения кода)
// Запуск: -javaagent:pyroscope.jar
// application.properties:
pyroscope.application.name=orders-service
pyroscope.server.address=http://pyroscope:4040
pyroscope.format=jfr
pyroscope.profiler.event=cpu,alloc,lock
```

**Типы профилей:**

| Тип | Что показывает |
|-----|---------------|
| CPU | Горячие методы, где тратится время CPU |
| Heap Allocation | Объекты, создаваемые в памяти (источник GC-давления) |
| Lock Contention | Конкуренция за мьютексы и monitors |
| Wall Clock | Время, включая ожидание IO |

**eBPF (extended Berkeley Packet Filter):**
- Работает на уровне ядра Linux — нет агента в приложении
- Профилирует любой процесс без изменения кода
- Grafana Beyla — eBPF-агент для auto-instrumentation (HTTP metrics, traces)

```bash
# Phlare / Pyroscope: корреляция профилей с трейсами
# profileID добавляется к span — из Grafana можно перейти от трейса к профилю
```

**Grafana Phlare** — open-source continuous profiling backend (интегрируется в LGTM-стек):
- Хранит профили как time series
- Корреляция с Tempo (трейсы) и Grafana Explore

**Практические use cases:**
- «Latency spike каждые 10 минут» → профиль выявляет GC stop-the-world
- «Deploy увеличил CPU на 20%» → сравнение профилей before/after

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q36. Chaos Engineering и Observability: как связаны?

**Chaos Engineering** — намеренное внесение сбоев в систему для проверки её устойчивости и обнаружения слабых мест до того, как они проявятся в production.

**Связь с Observability:**

Chaos Engineering без observability — «стрельба вслепую». Чтобы понять, как система реагирует на сбой, нужно видеть её внутреннее состояние.

```
Chaos Experiment:
  1. Гипотеза: "При потере 30% запросов к БД, Circuit Breaker сработает и
     пользователи получат graceful degradation"
  
  2. Observability во время эксперимента:
     - Метрики: error rate, latency, circuit breaker state
     - Трейсы: видим конкретные падающие запросы
     - Логи: сообщения circuit breaker, fallback
  
  3. Результат: подтверждаем или опровергаем гипотезу данными
```

**Chaos → обнаружение gaps в observability:**

Типичный результат chaos experiment — «мы не можем объяснить, что произошло, потому что нет метрик/логов для этого компонента». Chaos Engineering показывает слепые зоны observability.

**Инструменты:**

| Инструмент | Тип chaos |
|-----------|-----------|
| Chaos Monkey (Netflix) | Random instance termination |
| Gremlin | Latency injection, CPU stress, network |
| LitmusChaos (k8s) | Pod kill, node drain, network partition |
| Toxiproxy | Сетевые сбои для тестирования |

**Цикл Chaos Engineering + Observability:**
```
Observe → Hypothesize → Experiment → Observe results → Fix gaps → Repeat
```

**Принципы безопасного проведения:**
- Начинай в staging/dev, не в production
- Определи blast radius (максимальный ущерб)
- Останови эксперимент если метрики выходят за границы
- Document все гипотезы и результаты (runbook)

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q37. Alert Fatigue: причины, как бороться, стратегии silencing?

**Alert Fatigue** — состояние, при котором дежурный перестаёт реагировать на алерты из-за их количества, ложных срабатываний или нерелевантности.

**Причины:**

| Причина | Пример |
|---------|--------|
| Threshold без контекста | «CPU > 80%» — всегда срабатывает, но не влияет на пользователей |
| Слишком низкий порог | Алерт на 1 ошибку из 10000 запросов |
| Дублирование алертов | Один инцидент → 15 алертов из разных систем |
| Нет routing | Алерты о БД приходят фронтенд-разработчику |
| Алерты без runbook | «Что мне делать?» — нет ответа |

**Как бороться:**

1. **SLO-based alerting вместо threshold:**
```yaml
# Алерт на burn rate (сгорание error budget) — более сигнальный
- alert: ErrorBudgetBurnTooFast
  expr: |
    (sum(rate(http_requests_total{status=~"5.."}[1h])) /
     sum(rate(http_requests_total[1h]))) > 14.4 * (1 - 0.999)
  # 14.4x быстрее нормального burn rate = весь budget сгорит за 2 дня
```

2. **Severity levels с чёткими определениями:**

| Severity | Требует действия | Время ответа |
|----------|-----------------|-------------|
| P1 (Critical) | Немедленно, звонок | < 5 мин |
| P2 (High) | В течение часа | < 1 часа |
| P3 (Medium) | В рабочее время | < 8 часов |
| P4 (Low) | В бэклог | Следующий спринт |

3. **Дедупликация и группировка** (Alertmanager):
```yaml
# alertmanager.yml
route:
  group_by: ['alertname', 'cluster', 'service']
  group_wait: 30s      # Подождать, агрегировать связанные алерты
  group_interval: 5m
  repeat_interval: 4h  # Не спамить одним алертом чаще
```

4. **Silencing стратегии:**
- **Maintenance windows** — заглушить во время планового деплоя
- **Dependency silencing** — если DB down, заглушить все зависимые алерты
- **Time-based silencing** — ночью P3 не будить
- **Alert review meeting** — раз в месяц удалять ненужные алерты

5. **Регулярный audit:** подсчитай алерты за месяц — те, на которые никто не реагирует, удалить или понизить severity.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q38. Что включать в Runbook для автоматизации реакции на инцидент?

**Runbook** — документ с инструкциями по реагированию на конкретный алерт или инцидент. Цель: дежурный без знания системы может следовать runbook и решить проблему.

**Структура Runbook:**

```markdown
# Runbook: OrderService — High Error Rate

## Severity: P1
## Алерт: `OrderServiceErrorRateHigh`
## Владелец: @team-orders

## Описание
Процент 5xx ошибок на /api/v1/orders превысил 5% за последние 5 минут.

## Влияние
Пользователи не могут оформлять заказы. Прямые потери выручки.

## Диагностика (шаги)

### 1. Проверить логи
```bash
# Kibana: последние ошибки
GET /orders-*/_search?q=level:ERROR&sort=@timestamp:desc&size=50
```

### 2. Проверить метрики
- [Дашборд Orders Service](https://grafana/d/orders)
- Смотреть: `http_server_requests_seconds_count{status="500"}`

### 3. Проверить зависимости
- [БД Orders](https://grafana/d/postgres): connection pool, latency
- [Kafka](http://akhq/orders-topic): lag потребителей

### 4. Проверить последний деплой
```bash
kubectl rollout history deployment/orders-service -n production
```

## Действия

### Если проблема в БД: откат connection pool
```bash
kubectl set env deployment/orders-service -n production \
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5
```

### Если проблема в коде после деплоя: rollback
```bash
kubectl rollout undo deployment/orders-service -n production
```

## Эскалация
Если не решено за 15 минут → уведомить @orders-lead и @sre-on-call

## Post-Incident
Создать постмортем в Confluence: [Шаблон](https://confluence/postmortem-template)
```

**Ключевые принципы хорошего Runbook:**
- Конкретные команды, не абстрактные инструкции
- Ссылки на дашборды, логи, инструменты
- Указание времени до эскалации
- Признаки успешного разрешения
- Дата последнего обновления и ответственный

**Runbook Automation:** в зрелых командах runbook частично автоматизируется (PagerDuty Runbook Automation, AWS SSM Automation) — нажатие кнопки выполняет диагностические шаги автоматически.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q39. FinOps и Observability: стоимость телеметрии и sampling для снижения затрат?

**Проблема:** телеметрия дорогая. В высоконагруженной системе:
- 1 млн RPS × 1 трейс/запрос × 10 KB/трейс = **10 GB/час** трейсов
- Хранение в Datadog/Honeycomb: $0.1–1 за GB — тысячи долларов в месяц

**Стратегии снижения стоимости:**

**1. Head-based sampling** (решение на входе):
```yaml
# OpenTelemetry SDK: сэмплировать 10% трейсов
sampler:
  type: parentbased_traceidratio
  ratio: 0.1  # 10%
```
Минус: теряем 90% трейсов, включая редкие ошибки.

**2. Tail-based sampling** (решение после получения трейса):
```yaml
# OTel Collector: tail_sampling processor
processors:
  tail_sampling:
    decision_wait: 10s
    policies:
      - name: errors-policy
        type: status_code
        status_code: {status_codes: [ERROR]}  # 100% ошибок
      - name: slow-policy
        type: latency
        latency: {threshold_ms: 1000}         # 100% медленных
      - name: sample-otherwise
        type: probabilistic
        probabilistic: {sampling_percentage: 5} # 5% остальных
```

**3. Метрики vs трейсы:** агрегированные метрики дешевле, чем трейсы:
- Для SLO достаточно counter/histogram метрик
- Трейсы — для диагностики конкретных запросов

**4. Log sampling:**
```java
// Логировать DEBUG только 1% запросов
if (ThreadLocalRandom.current().nextInt(100) == 0) {
    log.debug("Request processed: {}", details);
}
```

**5. Cardinality management:**
```java
// BAD: уникальные userId в label — миллионы time series
Counter.builder("http.requests")
    .tag("user_id", userId)  // Exploding cardinality!
    
// GOOD: только предсказуемые метки
Counter.builder("http.requests")
    .tag("method", method)
    .tag("status", String.valueOf(status))
```

**FinOps чеклист для observability:**
- [ ] Tail sampling вместо 100% трейсов
- [ ] Retention policy: горячие данные (7 дней) в дорогом, архив в S3
- [ ] Cardinality ограничена (нет userId/sessionId в метриках)
- [ ] Log levels правильные (DEBUG не в production)
- [ ] Дашборд стоимости телеметрии per service

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q40. Synthetic Monitoring: что это и когда нужно?

**Synthetic Monitoring** — активная проверка работоспособности системы с помощью искусственно созданных запросов (синтетических транзакций), имитирующих поведение пользователя.

**Отличие от Real User Monitoring (RUM):**

| Аспект | Synthetic | RUM |
|--------|-----------|-----|
| Источник данных | Скрипты, запускаемые по расписанию | Реальные пользователи |
| Доступность в 3:00 ночи | Видит (нет пользователей, но мониторинг работает) | Не видит |
| Консистентность | Всегда один и тот же сценарий | Разное поведение |
| Зависимость от трафика | Не зависит | Нет пользователей — нет данных |

**Когда нужно:**

1. **Uptime monitoring** — знать, что сайт доступен 24/7, даже в ночное время:
```yaml
# Blackbox Exporter (Prometheus)
modules:
  http_2xx:
    prober: http
    http:
      valid_status_codes: [200]
      fail_if_not_ssl: true
```

2. **Critical path monitoring** — проверить конкретный бизнес-сценарий:
```javascript
// Playwright / Selenium сценарий
await page.goto('https://example.com');
await page.fill('#email', 'test@example.com');
await page.click('#checkout');
await expect(page).toHaveURL('/confirmation');
```

3. **Географическое мониторинг** — из разных регионов:
- Datadog Synthetics, Grafana Synthetic Monitoring, Checkly, Pingdom
- Обнаруживает CDN-проблемы (работает в Москве, не работает в Лондоне)

4. **SLO baseline без реального трафика** — в staging/canary среде

**Ограничения:**
- Не обнаруживает проблемы, специфичные для конкретных данных или пользователей
- Нет информации о реальном UX (только работает/не работает)
- Требует поддержки скриптов при изменении UI/API

**Практика:** synthetic monitoring дополняет, а не заменяет RUM и метрики. Минимум для production — HTTP health check каждые 60 секунд с алертом на P1.

## See also

- [Метрики и трейсинг](metrics-tracing-interview.md) — детальные вопросы по `Prometheus`, `Micrometer`, `OpenTelemetry` и distributed tracing
- [Стратегии логирования](logging-strategies-interview.md) — архитектурные решения: sampling, retention, централизованное логирование, стоимость хранения
- [Logging](../logging/logging-interview.md) — инструментальные вопросы: `SLF4J`, `Logback`, `MDC`, `ELK`, structured logging на практике
- [Распределённые системы](../architecture/distributed-systems-interview.md) — контекст, в котором observability особенно критична: CAP, консистентность, failure modes
- [Микросервисы](../architecture/microservices-interview.md) — архитектурные паттерны, где трассировка и correlation ID обязательны
- [Kubernetes](../devops/kubernetes-interview.md) — сбор метрик и логов в кластере: `kube-state-metrics`, Fluentd, Prometheus Operator


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [ELK Stack](elk-stack-interview.md)
- [Jaeger и Zipkin](jaeger-zipkin-interview.md)
- [Стратегии логирования](logging-strategies-interview.md)
- [Loki и Grafana](loki-grafana-interview.md)
- [Метрики и трейсинг](metrics-tracing-interview.md)
- [OpenTelemetry](opentelemetry-interview.md)
