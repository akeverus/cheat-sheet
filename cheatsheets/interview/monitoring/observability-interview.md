---
title: "Вопросы на собеседовании: Observability"
description: "Практичные вопросы и ответы по observability для Senior Java Developer: три столпа, OpenTelemetry, Prometheus, Grafana, distributed tracing, structured logging, SLI/SLO/SLA, алертинг, cardinality, ELK/EFK, RED/USE методы."
tags:
  - interview
  - monitoring
  - observability-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Observability"
  - "Наблюдаемость систем"
  - "Observability interview"
prerequisites: []
next: []
updated: "2026-05-15"
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
> - [ ] `Observability` — это просто красивое название для `monitoring` с дашбордами в `Grafana` | Маркетинговая подмена терминов; добавление дашбордов не даёт ad-hoc диагностики неизвестных сценариев. ❌ ПОСЛЕДСТВИЕ: команда покупает `Datadog`, но при инциденте всё равно «не видит» причину — MTTR не падает.
> - [x] `Monitoring` отвечает на known-unknowns по заранее заданным метрикам, `observability` позволяет задавать ad-hoc вопросы о внутреннем состоянии без редеплоя | Термин из теории управления: систему называют `observable`, если по выходам можно восстановить состояние. ✓ ПРИМЕНЯТЬ: `Honeycomb` (Charity Majors) построила бизнес именно на ad-hoc query по high-cardinality событиям. 📋 ПРАВИЛО: «monitoring = detection, observability = detection + diagnosis». 🔗 См. Q3, Q32.
> - [ ] `Observability` = `metrics` + `logs` + `traces`, любые три инструмента автоматически дают наблюдаемость | Три столпа — это сигналы, а не сама observability; без корреляции через `traceId` они изолированы. ❌ ПОСЛЕДСТВИЕ: купили `Prometheus` + `ELK` + `Jaeger`, но без общего контекста инцидент в `payment-service` диагностируется 3 часа.
> - [ ] `Observability` нужна только в Kubernetes-окружениях с микросервисами | Узкое vendor-определение; observability нужна и монолиту, если в нём нетривиальные failure modes. ❌ ПОСЛЕДСТВИЕ: монолит банка падает, error budget сгорает, команда узнаёт об этом из звонка клиента, а не из алерта.

Рабочий шаблон на 40-60 секунд:
1. **Симптом:** "растёт p99 latency и error rate".
2. **Путь диагностики:** "дашборд RED -> trace -> логи по traceId".
3. **Решение:** "ограничили cardinality метрик, поправили retry, снизили tail latency".
4. **Валидация:** "p99 с 900ms до 280ms, 5xx с 2.8% до 0.4%".

Именно измеримый результат отличает сильный senior-ответ от теории.


> [!mcq]
> - [ ] Перечислить все инструменты: `Prometheus`, `Grafana`, `Jaeger`, `OpenTelemetry`, `ELK`, `Loki`, `Tempo` | Сеньорный ответ — это не каталог инструментов, а способность связать симптом → диагностику → решение → метрику успеха. ❌ ПОСЛЕДСТВИЕ: интервьюер ставит «толстый middle, не senior»; кандидат не показал владения процессом.
> - [ ] Рассказать историю «однажды у нас был инцидент» без чисел | Без измеримого результата ответ звучит как байка; senior-ответ требует before/after. ❌ ПОСЛЕДСТВИЕ: интервьюер просит «а на сколько улучшилось?», кандидат плывёт.
> - [x] Симптом → путь диагностики (метрика → trace → логи) → решение → измеримый результат (`p99 900ms → 280ms`) | Шаблон 40-60 секунд: симптом, путь, фикс, цифры. Именно цифры отделяют senior от middle. ✓ ПРИМЕНЯТЬ: SRE-интервью в `Google`, `Booking.com`, `Yandex` — кандидата просят STAR с метриками. 📋 ПРАВИЛО: «нет цифр — нет senior». 🔗 См. Q23, Q30.
> - [ ] Объяснить теорию: «observability — это про метрики, логи и трейсы» и закончить | Чистая теория без production-кейса не отличает senior от middle с конспектом. ❌ ПОСЛЕДСТВИЕ: следующий вопрос «как вы это делали в проде?» — кандидат не готов, оффер не выдают.

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
> - [ ] Метрики, логи и трейсы — три независимые системы, выбираешь одну достаточную | Три столпа complementary, а не alternatives; одна метрика не отвечает на «почему». ❌ ПОСЛЕДСТВИЕ: команда поставила только `Prometheus`, при `5xx` спайке нет stack trace и нет цепочки вызовов — root cause ищут руками 4 часа.
> - [ ] Логи — главное, метрики и трейсы можно опционально добавить | Логи без агрегации и без корреляции — не observability; без метрик нет SLO, без трейсов нет cross-service причины. ❌ ПОСЛЕДСТВИЕ: tail-tail-`grep` по 50 GB логов в день, дежурный не успевает локализовать инцидент за SLA.
> - [ ] Метрики, логи и трейсы должны храниться в одной БД, иначе корреляции нет | Хранилища разные (TSDB / log-store / trace-store) — связывает их `traceId`, а не общая БД. ❌ ПОСЛЕДСТВИЕ: команда пытается сложить трейсы в `Prometheus` → cardinality explosion, OOM на 2 неделю.
> - [x] Метрики отвечают «что массово», трейсы — «где задержка», логи — «почему», связаны через единый `traceId` | По алерту на метрику переходишь в trace, потом фильтруешь логи по `traceId`. Цепочка работает только при сквозном context propagation. ✓ ПРИМЕНЯТЬ: `Grafana LGTM`-стек делает derived field `traceId` для перехода `Loki` → `Tempo` за один клик. 📋 ПРАВИЛО: «метрика → trace → логи по traceId». 🔗 См. Q15, Q21.

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
> - [x] `RED` (rate, errors, duration) + saturation (`cpu`, `memory`, pool utilization) + бизнес-метрики (оплаты, конверсия) | Технические + бизнес-сигналы вместе показывают impact на пользователя, а не только нагрузку на CPU. ✓ ПРИМЕНЯТЬ: `Spring Boot Actuator` + `Micrometer` отдают `http.server.requests` (RED) и `hikaricp.*` (saturation) из коробки. 📋 ПРАВИЛО: «RED + saturation + business — иначе не senior». 🔗 См. Q5, Q9.
> - [ ] Только `cpu_usage`, `memory_usage`, `disk_io` — классика sysadmin-мониторинга | Это `USE` для инфраструктуры, application-level (`rate`, `errors`, `duration`) полностью отсутствует. ❌ ПОСЛЕДСТВИЕ: CPU 30%, всё «зелёное», но `p99 = 5s` и пользователи отваливаются — алерты молчат.
> - [ ] Только `http.server.requests.count` — главное знать число запросов | Без `errors` и `duration` нельзя оценить ни качество, ни SLO. ❌ ПОСЛЕДСТВИЕ: rate растёт, кажется всё ок; на самом деле 30% запросов 5xx — узнаём из тикета поддержки.
> - [ ] Только бизнес-метрики (оплаты, конверсия) без технических | При деградации не видно root cause: непонятно — БД, GC или внешний API. ❌ ПОСЛЕДСТВИЕ: «оплаты упали», но ни `pool utilization`, ни `gc pause` не собираются — диагностика наугад.

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
> - [ ] `RED` для всего: и для HTTP-сервиса, и для CPU/диска | `RED` — application-level, для ресурсов он не имеет смысла (у диска нет `requests/sec`). ❌ ПОСЛЕДСТВИЕ: метрика `disk_request_rate` бессмысленна, дашборд вводит в заблуждение, инженер смотрит не туда при I/O насыщении.
> - [ ] `USE` для всего: применять `utilization/saturation/errors` к HTTP-эндпоинтам | `USE` — для физических ресурсов (Brendan Gregg); для API-эндпоинта `saturation` не определён нативно. ❌ ПОСЛЕДСТВИЕ: команда строит «`saturation` API», не понимает что считает, теряет 2 спринта на бесполезный дашборд.
> - [x] `RED` (rate/errors/duration) — для сервисов, обрабатывающих запросы; `USE` (utilization/saturation/errors) — для инфраструктурных ресурсов | `RED` — Tom Wilkie / Weaveworks, `USE` — Brendan Gregg; уровни разные, поэтому не подменяют друг друга. ✓ ПРИМЕНЯТЬ: `Netflix` смотрит `RED` на каждом сервисе, `USE` — на nodes и connection pools. 📋 ПРАВИЛО: «RED для запросов, USE для ресурсов». 🔗 См. Q4, Q9.
> - [ ] `RED` и `USE` — это синонимы, можно использовать любую аббревиатуру | Разные авторы, разные сигналы (`rate` vs `utilization`, `duration` vs `saturation`). ❌ ПОСЛЕДСТВИЕ: на интервью кандидат путается, intern-уровень; не получает offer на senior.

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
> - [ ] `Prometheus` — push-based, приложение шлёт метрики на сервер каждые 15 секунд | `Prometheus` — pull-based, server скрейпит `/metrics`; путают с `StatsD`/`Graphite`. ❌ ПОСЛЕДСТВИЕ: команда настраивает push в `Prometheus`, ничего не приходит, теряют 2 дня на диагностику конфига.
> - [ ] `Prometheus` хранит метрики в реляционной БД (PostgreSQL), доступ через SQL | Хранение в собственной TSDB, запросы через `PromQL`, не SQL. ❌ ПОСЛЕДСТВИЕ: попытка положить метрики в `Postgres` — рост таблиц на 50 GB/день, query timeout, downgrade БД.
> - [x] `Prometheus` — pull-based: сервер скрейпит HTTP-эндпоинт `/metrics`, хранит в собственной TSDB, запрашивается через `PromQL`; для short-lived jobs есть `Push Gateway` | Уникальный time series — комбинация имени метрики и набора лейблов. ✓ ПРИМЕНЯТЬ: `Spring Boot Actuator` отдаёт `/actuator/prometheus`, `Prometheus Operator` в Kubernetes автоматически находит targets через `ServiceMonitor`. 📋 ПРАВИЛО: «pull + TSDB + PromQL». 🔗 См. Q7, Q9.
> - [ ] `Prometheus` сам отправляет алерты в Slack, `Alertmanager` не нужен | `Prometheus` evaluates rules, но маршрутизация/дедупликация — это `Alertmanager`. ❌ ПОСЛЕДСТВИЕ: 50 одинаковых алертов в Slack за минуту, дежурный mute канал, реальный page пропущен.

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
> - [ ] `Counter` для текущего значения памяти, `Gauge` для подсчёта запросов | Перепутаны: `Counter` монотонно растёт (запросы), `Gauge` может расти и падать (память). ❌ ПОСЛЕДСТВИЕ: `rate(memory_used_total[5m])` ничего не значит — `Counter` сбрасывается при рестарте, реальная память не отслеживается.
> - [ ] `Histogram` и `Summary` взаимозаменяемы, выбирай любой | Принципиальная разница: `Histogram` агрегируется между инстансами на сервере, `Summary` считает квантили на клиенте и НЕ агрегируется. ❌ ПОСЛЕДСТВИЕ: команда выбирает `Summary` для p99 кластера из 20 подов — квантили нельзя сложить, дашборд врёт.
> - [ ] `Summary` всегда лучше `Histogram` — точнее считает квантили | Да, точнее на одном инстансе, но кластерные квантили вычислить нельзя (математически). ❌ ПОСЛЕДСТВИЕ: SLO `p99 < 500ms` на 20 инстансах — невозможно посчитать корректно, аудит не проходит.
> - [x] `Counter` (монотонный рост: запросы, ошибки) → `rate()`; `Gauge` (current value: память, активные соединения); `Histogram` (распределение, агрегируемо) → `histogram_quantile()`; `Summary` (квантили клиента, не агрегируется) — редко | `Histogram` предпочтительнее в большинстве случаев именно из-за агрегируемости. ✓ ПРИМЕНЯТЬ: `Micrometer` `Timer` с `publishPercentileHistogram()` отдаёт histogram-бакеты, `Prometheus` считает p99 серверной стороной. 📋 ПРАВИЛО: «Histogram агрегирует, Summary — нет». 🔗 См. Q6, Q8.

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
> - [x] `Cardinality` — число уникальных комбинаций лейблов; каждая комбинация = отдельный time series в TSDB; добавление `userId`/`requestId` в лейбл взрывает память `Prometheus` линейно | Правило: ≥ 100K series на инстанс — уже проблема; защита через `sample_limit` и `metric_relabel_configs` drop. ✓ ПРИМЕНЯТЬ: `Grafana Mimir` и `Cortex` ставят `max_global_series_per_user` лимит для multi-tenant защиты. 📋 ПРАВИЛО: «никогда userId/requestId/email/IP в labels». 🔗 См. Q7, Q27.
> - [ ] `Cardinality` — это сколько метрик у тебя всего (имён метрик) | Путают с количеством имён; каждый уникальный набор labels тоже отдельный series. ❌ ПОСЛЕДСТВИЕ: «у нас всего 50 метрик», но из-за `userId` в `tag` — 5 млн time series, OOM `Prometheus` за 2 часа.
> - [ ] Высокая cardinality безопасна, если много RAM — `Prometheus` сам справится | Линейный рост памяти + замедление запросов; вертикальное масштабирование не лечит. ❌ ПОСЛЕДСТВИЕ: 64 GB RAM закончились за неделю, query 30+ секунд, переход на `Thanos`/`Mimir` экстренно за 100K USD.
> - [ ] Cardinality важна только в managed-сервисах (Datadog), self-hosted Prometheus не считает | Биллинг в `Datadog` чувствительный, но `Prometheus` физически кладёт OOM ровно так же. ❌ ПОСЛЕДСТВИЕ: «у нас self-hosted, не страшно», `Prometheus` падает каждую ночь, дежурный рестартует руками.

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
> - [ ] Достаточно `spring-boot-starter-actuator`, метрики появятся автоматически на `/actuator/prometheus` | Без `micrometer-registry-prometheus` эндпоинт не активируется — только generic `actuator/metrics`. ❌ ПОСЛЕДСТВИЕ: `Prometheus` скрейпит, получает 404, сервис «без метрик» в дашборде; неделю никто не замечает.
> - [x] Подключить `spring-boot-starter-actuator` + `micrometer-registry-prometheus`, открыть эндпоинт `prometheus` через `management.endpoints.web.exposure.include`, кастом через `MeterRegistry.timer()` | Получаешь out-of-the-box `http_server_requests_seconds`, `jvm_*`, `hikaricp_*`. ✓ ПРИМЕНЯТЬ: `Spring Boot 3` + `Micrometer Tracing` — стандарт стека `Detmir` / `Wildberries` для production-сервисов. 📋 ПРАВИЛО: «actuator + micrometer-prometheus + expose». 🔗 См. Q6, Q15.
> - [ ] Писать кастомный `Filter`, парсить request, складывать в `ConcurrentHashMap`, отдавать вручную | Велосипед поверх `Micrometer`, без histogram-бакетов и без интеграции с Spring. ❌ ПОСЛЕДСТВИЕ: race condition в `HashMap`, утечка памяти, p99 квантили неверные — два месяца на собственный bug-fix.
> - [ ] Использовать `prometheus.io/scrape: true` аннотацию в pod без actuator | Аннотация — для Kubernetes service discovery, но без эндпоинта `/metrics` нечего скрейпить. ❌ ПОСЛЕДСТВИЕ: `Prometheus` пытается скрейпить, ошибка, `up == 0` алерт спамит дежурного каждые 5 минут.

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
> - [ ] Structured logging — это просто красиво форматировать текстовые логи (выровнять колонки) | Подмена концепции; structured = машиночитаемый формат с полями (`JSON`/`logfmt`), а не визуальное форматирование. ❌ ПОСЛЕДСТВИЕ: `Kibana` не может фильтровать по `userId` — нет полей, только `message: "..."`, поиск только regex.
> - [ ] Structured logging — это `String.format("user=%s order=%s", userId, orderId)` | Форматирование строки оставляет всё в одном `message`-поле; парсинг в `Logstash` через grok хрупкий и дорогой. ❌ ПОСЛЕДСТВИЕ: изменили шаблон сообщения — `grok` падает, поиск по `userId` теряется, расследование инцидента слепое.
> - [x] Запись логов в машиночитаемом `JSON` с фиксированными полями (`level`, `traceId`, `userId`, `message`); включает `traceId`/`spanId` для корреляции с трейсами; не пишет PII | Поля позволяют `Elasticsearch`/`Loki` искать, фильтровать, агрегировать без regex-парсинга. ✓ ПРИМЕНЯТЬ: `Spring Boot 3.4` нативно поддерживает `logging.structured.format.console: ecs` (Elastic Common Schema). 📋 ПРАВИЛО: «JSON + traceId + без PII». 🔗 См. Q12, Q15.
> - [ ] Structured logging обязательно требует отдельной библиотеки и переписывания `log.info()` на специальный API | `Logback` + `logstash-logback-encoder` или Spring Boot 3.4 встроенный — без изменений в коде. ❌ ПОСЛЕДСТВИЕ: команда боится миграции, остаётся на plain text, MTTR растёт, бюджет на observability впустую.

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
> - [ ] `EFK` = `Elasticsearch` + `Fluentd` + `Kafka` — Kafka используется для индексации | `K` в `EFK` — это `Kibana`; `Kafka` сюда не входит, путаница. ❌ ПОСЛЕДСТВИЕ: на интервью кандидат рассказывает про Kafka в EFK — интервьюер «не senior», offer не выдают.
> - [ ] `Logstash` и `Fluent Bit` потребляют одинаково мало памяти | `Logstash` — JRuby на JVM (500MB-1GB), `Fluent Bit` — pure C (30-50MB). ❌ ПОСЛЕДСТВИЕ: команда ставит `Logstash` как DaemonSet на 100 нод — лишние 50 GB RAM на кластер, миграция на `Fluent Bit` через 3 месяца.
> - [ ] `ELK` дешевле `Loki` для всех use-case | `Loki` индексирует только лейблы, поэтому в 10-50× дешевле для troubleshooting; `ELK` выигрывает только при full-text-аналитике. ❌ ПОСЛЕДСТВИЕ: bill `Elasticsearch` $50K/месяц, миграция на `Loki` снизила бы до $3K — но решение приняли «потому что все используют ELK».
> - [x] `ELK` = `Elasticsearch` + `Logstash` (JVM, тяжёлый) + `Kibana`; `EFK` = `Elasticsearch` + `Fluentd`/`Fluent Bit` (C, лёгкий) + `Kibana`; `Loki` — альтернатива, индексирует только лейблы (дешевле в 10-50×) | `EFK` — де-факто стандарт в Kubernetes из-за лёгкого агента. ✓ ПРИМЕНЯТЬ: `EFK` в `Kubernetes`-окружениях `Spotify` / `Wolt`; `Loki` — в `Grafana Cloud` + on-prem `Yandex`. 📋 ПРАВИЛО: «ELK для аналитики, EFK для k8s, Loki для бюджета». 🔗 См. Q12, Q21.

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
> - [x] `Spring Boot 3.4+`: `logging.structured.format.console: ecs` (Elastic Common Schema); раньше — `Logback` + `logstash-logback-encoder` с `LogstashEncoder`; `MDC.put("traceId", ...)` для контекста; OpenTelemetry-agent кладёт `traceId`/`spanId` в `MDC` автоматически | `MDC` пробрасывается в JSON-encoder, поля попадают в `Elasticsearch`/`Loki`. ✓ ПРИМЕНЯТЬ: `Spring Boot 3.4` ECS-формат + `opentelemetry-javaagent` — нулевая интеграция, заполняет `trace.id` автоматически. 📋 ПРАВИЛО: «logback-encoder + MDC + OTel-agent». 🔗 См. Q10, Q15.
> - [ ] Самостоятельно собирать `Map<String,Object>` и сериализовать через `ObjectMapper.writeValueAsString()` в `log.info()` | Велосипед: нет уровней, нет MDC, нет thread-safety; `Logback` уже это делает. ❌ ПОСЛЕДСТВИЕ: race condition в кастомном encoder, перемешанные строки JSON, `Kibana` не парсит часть логов, расследование слепое.
> - [ ] Просто включить `log.info("user=" + userId)` — это уже structured | Конкатенация — это plain text; парсинг в `Logstash` через `grok` не масштабируется. ❌ ПОСЛЕДСТВИЕ: при изменении формата сообщения `grok` падает, индекс ломается, alert на `error rate` молчит — incident не виден.
> - [ ] `MDC.put()` вызывать в `@PostConstruct` один раз на старте приложения | `MDC` — `ThreadLocal`, ставится per-request; в `@PostConstruct` он бесполезен и протекает между потоками. ❌ ПОСЛЕДСТВИЕ: один `userId` оказывается в логах всех пользователей, GDPR-инцидент, штраф €20M.

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
> - [ ] `Trace` — это один `span` со списком вложенных HTTP-запросов | Путаница терминов: `trace` — это дерево спанов, а `span` — единица работы внутри одного сервиса. ❌ ПОСЛЕДСТВИЕ: на интервью кандидат рассказывает про «trace = большой span», воспринимается как middle, оффер на senior не выдают.
> - [x] `Trace` — дерево из `span`-ов (единиц работы), связанных через `traceId` (128 бит) и `parentSpanId`; контекст пробрасывается через `traceparent` header (W3C Trace Context); `baggage` — данные через все сервисы | Стандарт W3C делает контекст переносимым между HTTP / Kafka / gRPC. ✓ ПРИМЕНЯТЬ: `Uber` (создатели `Jaeger`) — оригинальный кейс с миллиардами span-ов в день; `OpenTelemetry` имплементирует ровно W3C. 📋 ПРАВИЛО: «trace = дерево, span = узел, traceparent = связь». 🔗 См. Q15, Q17.
> - [ ] `TraceId` генерируется на каждом сервисе заново для изоляции | Это убивает всю идею distributed trace; `traceId` должен быть один на весь путь запроса. ❌ ПОСЛЕДСТВИЕ: команда видит 5 несвязанных трейсов вместо одного, причину timeout не находит, расследование 6 часов.
> - [ ] `Baggage` — это синоним `traceId`, нужны для одного и того же | `Baggage` — произвольные KV-данные (`tenantId`, `userId`), отдельные от `traceId`. ❌ ПОСЛЕДСТВИЕ: разработчик кладёт PII в `traceId`, попадает в `Tempo`/`Jaeger`-индекс, аудит безопасности фейлит.

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
> - [ ] `Jaeger` написан на Java, `Zipkin` — на Go | Перепутано: `Jaeger` — Go (Uber → CNCF), `Zipkin` — Java (Twitter). ❌ ПОСЛЕДСТВИЕ: на интервью кандидат путает языки, теряет очки на культурных вопросах об экосистеме CNCF.
> - [ ] `Jaeger` и `Zipkin` несовместимы с `OpenTelemetry`, нужен только нативный SDK | Оба принимают данные через OpenTelemetry: `Jaeger` нативно через `OTLP`, `Zipkin` через `Zipkin exporter`. ❌ ПОСЛЕДСТВИЕ: команда переписывает инструментацию под нативный SDK, теряет 2 спринта вместо включения `OTLP exporter`.
> - [x] `Jaeger` (Go, Uber → CNCF) — нативный `OTLP`, adaptive sampling, DAG view; `Zipkin` (Java, Twitter) — проще, требует Zipkin exporter, без adaptive sampling; оба уступают `Grafana Tempo` (object storage S3, дешевле) | `Tempo` стал де-факто заменой обоим в LGTM-стеке. ✓ ПРИМЕНЯТЬ: `Uber` использует `Jaeger`; `Yandex` мигрировал с `Zipkin` на `Tempo` ради object-storage цены. 📋 ПРАВИЛО: «Jaeger — Go/CNCF, Zipkin — Java/проще, Tempo — будущее». 🔗 См. Q13, Q21.
> - [ ] `Tempo` индексирует full-text как `Elasticsearch`, поэтому дорогой | `Tempo` ищет только по `traceId`, не индексирует — поэтому дешёвый и хранит в S3/GCS. ❌ ПОСЛЕДСТВИЕ: команда выбирает `Elasticsearch` вместо `Tempo` «потому что full-text», bill х10, ROI отрицательный.

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
> - [ ] Достаточно вызвать `log.info("traceId=" + traceId)` руками в каждом методе | Хрупко, дублирует логику OTel-agent, не работает в async-контексте. ❌ ПОСЛЕДСТВИЕ: разработчик забыл `traceId` в catch-блоке — error без trace context, расследование слепое.
> - [ ] `traceId` генерируется в `Logback` сам по себе, ничего настраивать не надо | `Logback` сам не знает про OpenTelemetry; нужен `MDC` + agent или ручная установка. ❌ ПОСЛЕДСТВИЕ: логи без `traceId`, корреляции с `Tempo` нет, инцидент диагностируется по timestamp вручную.
> - [ ] `Logstash` сам добавит `traceId` при индексации | Logstash работает на уровне ingestion, не знает про runtime-контекст приложения. ❌ ПОСЛЕДСТВИЕ: команда ждёт «волшебной» корреляции, её нет, тратят месяц на ручное расследование.
> - [x] OpenTelemetry Java agent или `opentelemetry-logback-mdc` кладут `traceId`/`spanId` в `MDC` автоматически; structured-encoder включает их в `JSON`; в `Grafana` derived field `traceId` делает one-click переход `Loki` → `Tempo` | Цепочка: agent → MDC → encoder → лог-индекс → Grafana link. ✓ ПРИМЕНЯТЬ: `Grafana Cloud` стандартно настраивает derived fields для `traceID` regex `"traceId":"(\\w+)"`. 📋 ПРАВИЛО: «agent + MDC + derived field». 🔗 См. Q12, Q17.

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
> - [x] `Exemplar` — точка на гистограмме (latency-bucket) с прикреплённым `traceId`; в `Grafana 9+` это кликабельная точка → переход в `Tempo`/`Jaeger` к конкретному медленному запросу; полезен при редких latency-всплесках | Без exemplar нужно искать «плохой» trace вручную по timestamp. ✓ ПРИМЕНЯТЬ: `Spring Boot 3` + `Micrometer Tracing` автоматически прикрепляет exemplars к histogram-bucket'ам через OpenMetrics экспозицию. 📋 ПРАВИЛО: «exemplar = метрика → конкретный trace в один клик». 🔗 См. Q15, Q16.
> - [ ] `Exemplar` — это пример конфигурации `Prometheus` из документации | Слово созвучное, но это отдельный механизм связи метрики с трейсом, не пример конфига. ❌ ПОСЛЕДСТВИЕ: на интервью кандидат путает термин, теряет очки на observability-секции.
> - [ ] `Exemplar` — это синоним `Counter`-метрики | Принципиально разное: `Counter` — тип метрики, `exemplar` — ссылка из метрики на trace. ❌ ПОСЛЕДСТВИЕ: разработчик не понимает зачем нужны exemplars, не настраивает их, диагностика P1 latency спайков занимает часы.
> - [ ] Exemplars работают только с `Datadog`, в open-source их нет | Нативная поддержка в `Prometheus` (OpenMetrics формат), `Grafana 9+`, `Tempo`. ❌ ПОСЛЕДСТВИЕ: команда покупает `Datadog` ради exemplars, не зная что в Grafana они есть бесплатно — лишние $30K/год.

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
> - [ ] `OpenTelemetry` — это backend для хранения трейсов, конкурент `Jaeger`/`Tempo` | OTel — это SDK + протокол, а НЕ backend; данные отправляются в backend по выбору. ❌ ПОСЛЕДСТВИЕ: команда ставит «OpenTelemetry» как backend, не находит UI, теряет неделю на архитектурное недопонимание.
> - [x] `OpenTelemetry` (CNCF Graduated) — vendor-neutral SDK для метрик/логов/трейсов; результат слияния `OpenTracing` + `OpenCensus`; единый протокол `OTLP`; W3C Trace Context для context propagation; auto-instrumentation через Java agent | Один SDK, любой backend (Prometheus, Jaeger, Datadog, New Relic). ✓ ПРИМЕНЯТЬ: `Microsoft Azure`, `AWS`, `Google Cloud` — все три гипергейта имеют нативные OTLP-приёмники. 📋 ПРАВИЛО: «OTel = SDK + OTLP + W3C, vendor-neutral». 🔗 См. Q18, Q20.
> - [ ] `OpenTelemetry` поддерживает только Java и Go, для других языков нужен `OpenCensus` | OTel объединил OpenTracing + OpenCensus и поддерживает 15+ языков; OpenCensus — устаревший. ❌ ПОСЛЕДСТВИЕ: Python-команда продолжает на `OpenCensus`, теряет поддержку, в 2024 проект архивирован — экстренная миграция.
> - [ ] OpenTelemetry заменяет `Prometheus` и `Grafana`, после внедрения они не нужны | OTel — слой инструментации, `Prometheus` хранит метрики, `Grafana` визуализирует — разные роли. ❌ ПОСЛЕДСТВИЕ: команда выключает `Prometheus`, метрики пропадают, дашборды пустые, экстренный rollback.

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
> - [ ] `API` и `SDK` — один и тот же артефакт, импорт в коде идентичен | Разные модули: `opentelemetry-api` (только интерфейсы) vs `opentelemetry-sdk` (реализация); код инструментации зависит только от `api`. ❌ ПОСЛЕДСТВИЕ: библиотека тащит `sdk` транзитивно, у клиента два конфликтующих SDK, ошибки `NoSuchMethodError` в runtime.
> - [ ] `OTLP` — это устаревший формат, `Jaeger thrift` современнее | Наоборот: `OTLP` — это новый стандарт, `Jaeger thrift` — legacy. ❌ ПОСЛЕДСТВИЕ: команда настраивает `Jaeger thrift exporter`, через год его дропают из `Jaeger` v2, экстренная миграция.
> - [x] `API` (Tracer/Meter/Logger interfaces, стабильные) ↔ `SDK` (`SpanProcessor`, `Sampler`, `Exporter`) ↔ Auto-instrumentation (Java agent для HTTP-клиентов, JDBC, Kafka, Spring MVC) ↔ `OTLP` (gRPC/HTTP бинарный протокол для всех сигналов) | Чистое разделение позволяет менять backend без изменения кода. ✓ ПРИМЕНЯТЬ: `opentelemetry-javaagent` от CNCF — стандарт zero-code инструментации в Spring Boot. 📋 ПРАВИЛО: «API stable, SDK pluggable, agent zero-code». 🔗 См. Q17, Q19.
> - [ ] `Sampler` решает где хранить трейсы (S3 или локально) | `Sampler` решает СОХРАНЯТЬ ли трейс (например, 10%); хранилище — забота backend. ❌ ПОСЛЕДСТВИЕ: команда меняет `Sampler` ожидая оптимизации хранения, проблема не уходит, бюджет горит.

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
> - [ ] Только manual через `tracer.spanBuilder()` в каждом методе — agent не нужен | Manual для всех HTTP/JDBC/Kafka вызовов — это сотни классов кода и регрессии. ❌ ПОСЛЕДСТВИЕ: команда оборачивает каждый репозиторий вручную, через 2 спринта половина методов без span-ов, blind spots в трейсах.
> - [ ] Самописная `@Aspect` + `ThreadLocal` для context propagation вместо OTel | Велосипед без W3C Trace Context, не работает через границы (Kafka, async). ❌ ПОСЛЕДСТВИЕ: trace обрывается на первом async-вызове, диагностика cross-service деградации невозможна.
> - [ ] Использовать только Spring AOP `@Around` без OpenTelemetry SDK | Метрики и spans живут отдельно, нет единого `traceId`, нет экспорта в backend. ❌ ПОСЛЕДСТВИЕ: «свои метрики», но в `Tempo`/`Jaeger` ничего не приходит — observability stack фактически не работает.
> - [x] Java agent (zero-code, `-javaagent:opentelemetry-javaagent.jar`) — авто HTTP/JDBC/Kafka/Spring; либо `opentelemetry-spring-boot-starter` (конфиг через `application.yml`); manual через `@WithSpan`/`tracer.spanBuilder()` для бизнес-кода | Комбинация: agent для инфраструктуры + аннотации для бизнес-операций. ✓ ПРИМЕНЯТЬ: `Detmir` / `Wildberries` / `Yandex` ставят `opentelemetry-javaagent` в Dockerfile через ENTRYPOINT, бизнес-код инструментируется `@WithSpan`. 📋 ПРАВИЛО: «agent для всего, @WithSpan для бизнеса». 🔗 См. Q18, Q20.

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
> - [x] OTel Collector — прокси/агрегатор между приложениями и backends; pipeline `Receivers → Processors → Exporters`; даёт буферизацию (приложение не блокируется), tail-based sampling, добавление атрибутов, роутинг в разные backends; смена backend без передеплоя | Без Collector каждое приложение знает endpoint backend и блокируется при недоступности. ✓ ПРИМЕНЯТЬ: `Grafana Alloy` (форк OTel Collector от `Grafana Labs`) — стандартный sidecar/gateway в `Kubernetes`. 📋 ПРАВИЛО: «Collector = буфер + processors + роутинг». 🔗 См. Q19, Q26.
> - [ ] Collector — это backend, заменяет `Tempo` и `Prometheus` | Collector — proxy, он отправляет данные дальше; не хранит метрики/трейсы. ❌ ПОСЛЕДСТВИЕ: команда не настраивает backend, данные приходят в Collector и теряются — observability «зелёное», но данных нет.
> - [ ] Collector нужен только в очень больших системах, для одного сервиса избыточен | Даже один сервис выигрывает от буферизации и центрального tail-sampling. ❌ ПОСЛЕДСТВИЕ: при недоступности `Tempo` приложение блокируется на send, p99 растёт с 200ms до 5s.
> - [ ] Через Collector можно отправлять только трейсы, метрики идут напрямую | Collector обрабатывает все три сигнала (metrics, logs, traces) через раздельные pipelines. ❌ ПОСЛЕДСТВИЕ: команда дублирует архитектуру для метрик отдельно, удваивает поддержку без причины.

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
> - [ ] `LGTM` = `Linux` + `Grafana` + `Tempo` + `Mimir` | `L` — это `Loki` (логи), не Linux; путаница с самой первой буквой. ❌ ПОСЛЕДСТВИЕ: на интервью кандидат не помнит, что L = Loki, теряет очки на observability-секции.
> - [x] `LGTM` от `Grafana Labs` — `Loki` (логи, `LogQL`) + `Grafana` (UI) + `Tempo` (трейсы, `TraceQL`, S3-storage) + `Mimir` (long-term Prometheus-совместимое хранилище метрик, `PromQL`); единый UI для всех сигналов и нативная корреляция | `Loki` индексирует только лейблы (как `Prometheus` для логов) — поэтому в 10-50× дешевле `Elasticsearch`. ✓ ПРИМЕНЯТЬ: `Grafana Cloud` — managed-LGTM, в SaaS отдают всё сразу с derived fields. 📋 ПРАВИЛО: «Loki + Grafana + Tempo + Mimir = единая корреляция». 🔗 См. Q11, Q14.
> - [ ] `Tempo` — это форк `Prometheus` для трейсов | Архитектура трейсов принципиально другая (tree-структура, `traceId`-lookup); не форк `Prometheus`. ❌ ПОСЛЕДСТВИЕ: команда ожидает PromQL для трейсов, путается, теряет 2 спринта на интеграцию.
> - [ ] `Mimir` обязательно требует `Loki` для работы | `Mimir` — самостоятельный long-term store для метрик, может работать без Loki/Tempo. ❌ ПОСЛЕДСТВИЕ: команда не разворачивает `Mimir` «потому что Loki ещё не готов», retention метрик 15 дней — старые SLO-репорты невозможны.

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
> - [ ] Один большой дашборд с 80 панелями для всего сервиса | Нет фокуса, ничего не находишь за 30 секунд при инциденте. ❌ ПОСЛЕДСТВИЕ: при page-алерте дежурный 5 минут ищет нужную панель, MTTR растёт, error budget сгорает.
> - [ ] Скопировать дашборды между сервисами через `Save As` без шаблонизации | Drift через 3 месяца: каждый дашборд развивается отдельно, никто не поддерживает. ❌ ПОСЛЕДСТВИЕ: новые метрики добавлены в 3 из 20 дашбордов, у остальных пустые панели — теряем доверие к observability.
> - [x] Уровни (overview → service → debug); первый ряд — ключевые SLI с цвето-статусом; template variables `$service`/`$environment`; ссылки на runbook; default time range 1-6 часов; dashboard-as-code (Grafonnet/Terraform) | Каждый дашборд отвечает на ОДИН вопрос; навигация работает по drill-down. ✓ ПРИМЕНЯТЬ: `Grafana Labs` публикует USE/RED-method дашборды как Jsonnet-templates; `SoundCloud` использует Grafonnet для всех своих 500+ дашбордов. 📋 ПРАВИЛО: «один дашборд — один вопрос». 🔗 См. Q22, Q24.
> - [ ] Time range по умолчанию `last 24h` для исторического контекста | Слишком грубое разрешение, инцидентные пики смазаны 24-часовой агрегацией. ❌ ПОСЛЕДСТВИЕ: кратковременная деградация на 5 минут невидима в 24-часовом окне, дежурный её пропускает.

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
> - [ ] `SLI`, `SLO`, `SLA` — это разные названия одного и того же | Принципиально разные: `SLI` — измеритель, `SLO` — внутренняя цель, `SLA` — внешний контракт. ❌ ПОСЛЕДСТВИЕ: команда подписывает `SLA` 99.99% не зная, что текущий `SLI` 99.5% — штрафы $100K/месяц.
> - [ ] `SLO` всегда мягче `SLA`, чтобы было «легче выполнить» | Наоборот: `SLO` СТРОЖЕ SLA (например SLO 99.9%, SLA 99.5%), чтобы успевать реагировать до нарушения контракта. ❌ ПОСЛЕДСТВИЕ: SLO 99.0% при SLA 99.9% — нарушение SLA случается раньше, чем команда узнаёт; кредиты клиентам.
> - [ ] `Error budget` — это фиксированная сумма $X, которую можно потратить на инциденты | Error budget — это допустимая ДОЛЯ деградации (0.1% = 43.2 мин/месяц), не деньги. ❌ ПОСЛЕДСТВИЕ: финдиректор требует «не тратить error budget» как деньги, команда замораживает релизы зря — bottleneck.
> - [x] `SLI` (Service Level Indicator) — что измеряем (доля 2xx); `SLO` — внутренняя цель (99.9% за 30 дней, строже SLA); `SLA` — внешний контракт с финансовыми последствиями (99.5%); `error budget` (1 - SLO) — допустимая деградация (0.1% = 43.2 мин/мес); при исчерпании freeze risky-релизов | Концепция Google SRE Book: ошибки можно «расходовать», но осознанно. ✓ ПРИМЕНЯТЬ: `Google` SRE-команды используют error budget как gate для релизов — превысил → freeze; `Slack` публично описал свой SLO-процесс. 📋 ПРАВИЛО: «SLI измеряет, SLO целит, SLA контракт, budget = 1-SLO». 🔗 См. Q24, Q25.

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
> - [x] Алертить только по user-impact (`5xx`, p99 > SLO, business-метрики); три severity (`page` — будит, `ticket` — рабочий день, `info` — FYI); каждый page-алерт ОБЯЗАН иметь runbook; dedup и группировка через `Alertmanager`; правило: если на алерт можно не реагировать — это не page | Alertmanager `group_by: [alertname, cluster, service]` объединяет связанные. ✓ ПРИМЕНЯТЬ: `Google SRE Book` (глава Practical Alerting) — стандарт user-impact + symptom-based + runbook. 📋 ПРАВИЛО: «нет действия — нет page». 🔗 См. Q25, Q32.
> - [ ] Алертить на всё «на всякий случай», лучше шум, чем пропустить инцидент | Это ускоренный путь к alert fatigue: дежурный mute-ит канал, реальный page пропущен. ❌ ПОСЛЕДСТВИЕ: 200 алертов за ночь, утром Slack mute, P1 инцидент с 30-минутным SLA замечен через 3 часа.
> - [ ] Алертить по CPU > 80%, memory > 90%, disk > 70% — классические thresholds | Не связано с user impact: CPU 95% при нормальной latency — это нормальная работа; алерт ложный. ❌ ПОСЛЕДСТВИЕ: дежурного будят ночью на «CPU 85%», пользователи не страдают, моральный ущерб → текучка SRE.
> - [ ] Один уровень severity для всего — упрощает on-call процесс | Без разделения page/ticket дежурного будят на всё, выгорание неизбежно. ❌ ПОСЛЕДСТВИЕ: SRE увольняется через 3 месяца, новый набор каждые полгода, бюджет на найм $100K/год.

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
> - [ ] `Burn rate` — это синоним обычного error rate; никакой разницы | `Burn rate` — это error rate / SLO-budget rate; учитывает скорость относительно бюджета, а не абсолют. ❌ ПОСЛЕДСТВИЕ: команда настраивает «burn rate alert» через обычный threshold, false positives на каждом всплеске.
> - [x] `Burn rate` = скорость расходования error budget; multi-window strategy (Google SRE): `14.4× за 1ч` → page (бюджет за 2 дня), `6× за 6ч` → page (5 дней), `1×` → ticket; учитывает скорость деградации, меньше false positives, привязан к SLO | Multi-window short window подтверждает «деградация всё ещё идёт». ✓ ПРИМЕНЯТЬ: `Google SRE Book` (Implementing SLOs) — каноническая multi-window/multi-burn-rate стратегия; `Cloudflare` опубликовал точно такую же конфигурацию. 📋 ПРАВИЛО: «multi-window: 14.4×/6×/1× — page/page/ticket». 🔗 См. Q23, Q24.
> - [ ] `Burn rate` срабатывает мгновенно при превышении порога без learning periods | Multi-window требует подтверждения short window — иначе кратковременный spike → false page. ❌ ПОСЛЕДСТВИЕ: 30-секундный spike будит дежурного, через минуту восстанавливается; alert fatigue растёт.
> - [ ] `Burn rate` не нуждается в SLO, можно настроить без них | `Burn rate` математически = `error rate / (1 - SLO)`; без SLO формула неопределена. ❌ ПОСЛЕДСТВИЕ: команда «настроила burn rate без SLO», на самом деле это обычный threshold; ничего не выиграли.

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
>
> **Вопрос:** Какая стратегия sampling трейсов наиболее устойчива для production: сохраняет редкие ошибки и медленные запросы, не теряет интересные кейсы при низком общем проценте sampling?
>
> ---
>
> #### A) Head-based sampling с фиксированным `ratio: 0.1` (10%) для всех запросов — ❌ Неверно
>
> **Что на самом деле:** Head-based решает на входе по `traceId hash` — без знания исхода трейса. Это даёт предсказуемый объём, но равномерно теряет 90% всего, включая ошибки и медленные запросы. Для SLO-диагностики этого недостаточно: при error rate 0.5% и 10% sampling реально сохранится ~0.05% запросов — статистики на расследование почти нет.
>
> **Откуда путаница:** `OpenTelemetry SDK` по умолчанию предлагает `parentbased_traceidratio` — это документированный простой вариант, многие думают, что он production-ready.
>
> **Если бы это было правдой:** SRE открывает Tempo при инциденте «p99 = 5s, error rate 1%», находит 2-3 случайных трейса вместо паттерна — root cause не локализуется, MTTR растёт.
>
> ---
>
> #### B) Rate limiting — N трейсов в секунду на каждый сервис — ❌ Неверно
>
> **Что на самом деле:** Rate limiting (head-based) ограничивает RPS трейсов, что хорошо для предсказуемости стоимости. Но при всплеске трафика (5x normal) теряется именно тот период, который интересен SRE — момент инцидента. Также нет приоритезации ошибок: успешный `GET /healthz` и `500 POST /payment` имеют равные шансы попасть в выборку.
>
> **Откуда путаница:** `Jaeger` исторически популяризовал rate-limit sampler как защиту от перегрузки backend — это валидно как защита, но не как основная стратегия.
>
> **Если бы это было правдой:** в момент инцидента (всплеск 5xx) sampler дропает 80% ошибок — расследование становится невозможным; ирония в том, что чем хуже система, тем меньше данных для диагностики.
>
> ---
>
> #### C) Sample 100% всех трейсов без фильтрации — наиболее полное покрытие — ❌ Неверно
>
> **Что на самом деле:** 100% sampling в production невозможен экономически: при 1M RPS × 10 KB/трейс получится ~10 GB/час, ~$1000–10000/мес в managed (Datadog/Honeycomb). Кроме стоимости — overhead на network/CPU экспортёра, риск backpressure на приложение. 100% sampling уместен только в dev/staging или для очень низконагруженных сервисов.
>
> **Откуда путаница:** «больше данных — лучше» — интуитивно, но неверно на production-масштабе. Также некоторые vendor-доклады («Honeycomb pioneered events not samples») воспринимают буквально.
>
> **Если бы это было правдой:** observability бюджет = 30–50% от стоимости инфраструктуры, FinOps кричит, sampling всё равно вводят — но поспешно и без процессов.
>
> ---
>
> #### D) Комбинация: head-based 10–20% baseline + always-sample ошибки/медленные + tail-based в OTel Collector для финальной фильтрации — ✓ Верно
>
> **Развёрнутое объяснение:** Production-ready стратегия — двухуровневая. На уровне SDK (head) сохраняем baseline trace для статистики (`parentbased_traceidratio: 0.1`). На уровне OTel Collector (tail) применяем `tail_sampling processor`, который буферизует все span'ы трейса на `decision_wait` (5–30s) и принимает решение постфактум: 100% ошибок (`status_code: ERROR`), 100% медленных (`latency > 1s`), 5–10% остальных. Это даёт **полное покрытие интересных случаев** при общем sampling 5–15%. Минус: tail-sampling требует памяти на буферизацию (~1 GB / 50k RPS) и не работает корректно при разнесённых Collector instances (нужен `loadbalancing exporter` для group-by-traceId).
>
> **Пример:**
> ```yaml
> # OTel Collector — production tail sampling
> processors:
>   tail_sampling:
>     decision_wait: 10s
>     num_traces: 100000
>     policies:
>       - name: errors
>         type: status_code
>         status_code: {status_codes: [ERROR]}
>       - name: slow-requests
>         type: latency
>         latency: {threshold_ms: 1000}
>       - name: probabilistic
>         type: probabilistic
>         probabilistic: {sampling_percentage: 10}
> ```
>
> **Когда применять:** любая production-система с >1k RPS. Grafana Labs, Shopify, Uber используют такую двухуровневую схему. Для микросервисов на Kubernetes — OTel Collector в gateway-режиме.
>
> **Подводные камни:** tail-sampling требует, чтобы **все spans одного trace** попали на один Collector instance — нужен loadbalancing exporter с `routing_key: traceID`. Без этого Collector видит фрагменты трейса и принимает неверные решения. Также `decision_wait` должен быть больше максимального трейса — иначе trim long traces.
>
> **Связанные вопросы:** [[observability-interview#Q27]] — управление стоимостью телеметрии; [[observability-interview#Q34]] — OTel Collector processors; [[observability-interview#Q39]] — FinOps и sampling.

## Q27. Как управлять стоимостью логов и трейсов?

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
>
> **Вопрос:** Какой подход к управлению стоимостью observability работает в production и одновременно сохраняет диагностическую ценность данных?
>
> ---
>
> #### A) Хранить все логи и трейсы в hot-storage (SSD) 90 дней — иначе при инциденте недостаточно данных — ✓ Верно? Нет, это ❌ Неверно
>
> **Что на самом деле:** 90 дней hot-storage экономически разорителен: для 100 GB/день логов это 9 TB на SSD ≈ $200–1000/мес только за диски, плюс indexing overhead (Elasticsearch удваивает место). Реально 95% расследований используют данные **первых 24–72 часов**. Стандартная tiered-схема: hot 3–7 дней (SSD), warm 30 дней (HDD/cold S3), cold 90–365 дней (S3 Glacier для compliance).
>
> **Откуда путаница:** SRE-команды боятся «недостаточно данных при инциденте», поэтому склонны хранить всё в hot. Также аудит/compliance требует длинного retention — но он не требует hot-access.
>
> **Если бы это было правдой:** observability budget = 30–50% инфраструктуры; FinOps вынуждает резко резать retention с потерей всех данных, а не только hot-уровня.
>
> ---
>
> #### B) Снизить retention до 1 дня для всех логов — самый дешёвый вариант — ❌ Неверно
>
> **Что на самом деле:** 1 день retention ломает базовые workflow: weekly trend analysis, recurring incidents («каждый понедельник 9:00 alert»), compliance (часто 90+ дней обязательны), post-mortem (инциденты часто расследуются через 2–3 дня). 1 день уместен только для DEBUG-логов или для синтетических трейсов в нагрузочном тесте.
>
> **Откуда путаница:** при выходе из бюджета первая мысль — резать retention равномерно. Но это даёт линейную экономию и нелинейную потерю ценности.
>
> **Если бы это было правдой:** при инциденте в понедельник нет данных за пятницу — recurring patterns не видны; команда «летит» вслепую.
>
> ---
>
> #### C) Tiered retention (hot/warm/cold) + фильтрация на уровне агента + sampling + cardinality control + видимость стоимости — ✓ Верно
>
> **Развёрнутое объяснение:** Многоуровневая стратегия — единственный устойчивый подход. **Tiered retention**: hot 3–7 дней (SSD, indexed), warm 30 дней (HDD/object storage, lazy index), cold 90–365 дней (S3 Glacier, compliance only). **Фильтрация на агенте**: drop health-check логов (`/actuator/health`), не собирать DEBUG в production, severity-filter в `OTel Collector`. **Sampling** (см. Q26): 10% baseline + 100% errors через tail-sampling. **Cardinality control**: запрет `user_id`/`request_id` в Prometheus labels (cardinality explosion → OOM). **Видимость**: dashboard «cost-per-service» рядом с SLO-дашбордом — без этого оптимизация не приоритизируется. Эталон зрелости: observability = 5–15% от стоимости инфраструктуры приложения.
>
> **Пример:**
> ```yaml
> # Loki — tiered retention через retention_period + S3
> limits_config:
>   retention_period: 168h     # 7 дней hot
> compactor:
>   retention_enabled: true
>   retention_delete_delay: 2h
> storage_config:
>   aws:
>     s3: s3://logs-bucket
>     # lifecycle policy: переход в Glacier через 30 дней
> ```
>
> **Когда применять:** любая production-система, где observability bill стал заметен в FinOps-отчёте (обычно >$10k/мес). Grafana Labs, Datadog публикуют подробные cost-tier-гайды.
>
> **Подводные камни:** **cold storage retrieval cost** — S3 Glacier дешевле хранит, но дорогая выборка (~$0.03/GB). Если инциденты часто требуют warm-данные — лучше HDD, а не Glacier. Также **drop health-checks** ломает uptime monitoring, если он строится по логам — нужны отдельные synthetic probes.
>
> **Связанные вопросы:** [[observability-interview#Q26]] — sampling стратегии; [[observability-interview#Q28]] — observability без перегруза; [[observability-interview#Q39]] — FinOps детально.
>
> ---
>
> #### D) Перейти полностью на managed-решение (Datadog) — vendor оптимизирует за нас — ❌ Неверно
>
> **Что на самом деле:** Managed-решения **дороже** self-hosted при том же volume (Datadog Logs ~$1.27/GB ingest + $0.10/GB retention vs self-hosted Loki ~$0.10/GB total на S3). Vendor не оптимизирует — он биллит по тарифу. Managed выгоден когда у команды нет SRE-ресурсов на поддержку self-hosted стека.
>
> **Откуда путаница:** «managed = меньше операционных забот» путают с «managed = дешевле». Сравнение TCO нужно делать с учётом FTE на self-hosted.
>
> **Если бы это было правдой:** компании Netflix/Uber/Shopify не строили бы свои observability-стеки на open-source (LGTM, Mantis) — они делают это именно ради cost control.
>
> ---

## Q28. Как запускать observability в production без перегруза системы?

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
>
> **Вопрос:** Какая комбинация практик защищает приложение от того, что observability сама становится причиной деградации (latency overhead, OOM, backpressure)?
>
> ---
>
> #### A) Использовать synchronous экспорт телеметрии — это даёт максимальную целостность данных — ❌ Неверно
>
> **Что на самом деле:** Synchronous экспорт (блокирующий) добавляет network round-trip к каждой операции — для трейсов это 5–50 ms на span, что превращает быстрый endpoint в медленный. Также при недоступности backend поток приложения зависает, что ведёт к thread pool exhaustion и каскадному отказу. Production-стандарт — асинхронная отправка через batch processor с in-memory queue.
>
> **Откуда путаница:** в туториалах для простоты часто показывают synchronous-экспорт. Также есть legacy-инструменты (OpenTracing с inproc-exporter), которые работают синхронно.
>
> **Если бы это было правдой:** при сбое OTel Collector все pods приложения замирают → cascading failure всего сервиса.
>
> ---
>
> #### B) Логировать всё на DEBUG в production — данных будет много, можно потом отфильтровать — ❌ Неверно
>
> **Что на самом деле:** DEBUG-логирование в production создаёт три проблемы: (1) I/O bottleneck — write throughput logs > disk throughput → log buffer переполнен → потеря данных, (2) cost — DEBUG-логи увеличивают volume в 10–100x, (3) latency — synchronous appenders (logback default) блокируют request thread. Production-стандарт: INFO/WARN по умолчанию, DEBUG включается **временно** через dynamic log level (`POST /actuator/loggers/com.app.X` с `{"configuredLevel": "DEBUG"}`) на 10–30 минут для конкретной диагностики.
>
> **Откуда путаница:** «больше данных = легче расследовать» путают с production-реальностью, где данных слишком много, чтобы в них что-то найти.
>
> **Если бы это было правдой:** при включении DEBUG на всём — p99 latency растёт в 3–5x, диск заполняется за часы.
>
> ---
>
> #### C) Async export + sampling + контроль cardinality + dynamic log levels + circuit breaker на telemetry pipeline — ✓ Верно
>
> **Развёрнутое объяснение:** Защитный стек состоит из 5 практик. **Async export**: batch processor с in-memory queue + отдельный thread pool (OTel SDK default). **Sampling трейсов**: 10% baseline + 100% errors (см. Q26). **Cardinality control**: запрет high-cardinality labels (userId, requestId) — иначе Prometheus OOM. **Dynamic log levels** через Spring Actuator: INFO в normal mode, DEBUG включается per-class через REST API на короткое время. **Circuit breaker на export**: при недоступности backend телеметрия **дропается**, а не копится в queue — иначе OOM в самом приложении. Также: **separate thread pool** для экспортёра, **memory limit** на queue с drop-on-full стратегией.
>
> **Пример:**
> ```java
> // OTel SDK — async batch с memory limit и drop-on-full
> BatchSpanProcessor processor = BatchSpanProcessor.builder(otlpExporter)
>     .setMaxQueueSize(2048)              // drop при переполнении
>     .setMaxExportBatchSize(512)
>     .setScheduleDelay(Duration.ofSeconds(5))
>     .setExporterTimeout(Duration.ofSeconds(30))
>     .build();
>
> // Dynamic log level через Actuator
> // POST /actuator/loggers/com.app.OrderService
> // {"configuredLevel": "DEBUG"}
> // через 30 мин: {"configuredLevel": null}  // вернуть default
> ```
>
> **Когда применять:** любая production-система с SLO на latency. Особенно критично для high-throughput (>10k RPS) и low-latency (<100 ms p99) сервисов.
>
> **Подводные камни:** **дроп при переполнении queue** означает потерю данных в момент перегрузки — именно когда они нужны. Нужно alerting на `otel_exporter_dropped_spans_total`. Также **dynamic log level** не работает для loggers, инициализированных до Spring context (например, static init блоки).
>
> **Связанные вопросы:** [[observability-interview#Q8]] — cardinality control; [[observability-interview#Q26]] — sampling; [[observability-interview#Q34]] — memory_limiter в OTel Collector.
>
> ---
>
> #### D) Отключить трейсы и оставить только метрики — метрики лёгкие и достаточны для всего — ❌ Неверно
>
> **Что на самом деле:** Метрики действительно дешевле трейсов (агрегированные time series), но **не заменяют** их для cross-service диагностики. Метрика `http_requests{status="500"}` говорит «есть ошибки», но не показывает **где** в цепочке `user → API → service-A → service-B → DB` они произошли. Без трейсов MTTR для распределённых багов растёт в разы.
>
> **Откуда путаница:** трейсы дорого хранить — реакция «удалим вообще». Правильная реакция — sampling, а не отключение.
>
> **Если бы это было правдой:** при инциденте «50% запросов медленные» нет данных, какой именно сервис в цепочке тормозит — расследование руками через логи занимает часы.
>
> ---

## Q29. Как выбрать стек observability под команду и бюджет?

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
>
> **Вопрос:** Команда из 5 разработчиков (нет dedicated SRE) запускает Kubernetes-микросервисы с бюджетом до $500/мес на observability. Какой стек выбрать?
>
> ---
>
> #### A) Datadog Enterprise — стандарт индустрии, всё в одном вендоре — ❌ Неверно
>
> **Что на самом деле:** Datadog Enterprise стоит $15–23 на host/мес + per-GB логи + per-trace billing. Для 10 хостов + умеренный трафик легко выходит $2k–5k/мес — в 10x превышает бюджет. Datadog уместен для enterprise с командой devops и multi-million budget, не для стартапов.
>
> **Откуда путаница:** «стандарт индустрии» — маркетинговое заявление; реально в стартапах чаще self-hosted LGTM или Grafana Cloud Free tier.
>
> **Если бы это было правдой:** observability bill съел бы инфраструктурный бюджет — пришлось бы выключать сервисы для оплаты мониторинга.
>
> ---
>
> #### B) Self-hosted ELK (Elasticsearch + Logstash + Kibana) + Jaeger — open-source, всё бесплатно — ❌ Неверно
>
> **Что на самом деле:** Self-hosted ELK на production-нагрузке требует значительных ресурсов: Elasticsearch cluster ≥3 nodes (16 GB RAM каждая) + операционная поддержка (re-sharding, version upgrades, performance tuning). Для команды из 5 разработчиков без SRE это 0.5–1 FTE на поддержку — дороже managed-решения. Jaeger требует Cassandra/ES storage с тем же overhead.
>
> **Откуда путаница:** «open-source = бесплатно» забывает про TCO (operating cost = FTE × salary).
>
> **Если бы это было правдой:** команда тратит 2–3 дня в месяц на «починку Elasticsearch», вместо разработки фич.
>
> ---
>
> #### C) Grafana LGTM стек (Loki + Grafana + Tempo + Mimir) + OTel Collector, deployed через Helm в том же кластере — ✓ Верно
>
> **Развёрнутое объяснение:** LGTM-стек — оптимальный выбор для Kubernetes-native команд с ограниченным бюджетом. **Loki** для логов хранит индекс только по лейблам (cheap storage в S3, $0.02/GB), **Tempo** для трейсов работает аналогично (object storage, не нужна полная индексация). **Mimir** для метрик — масштабируемая версия Prometheus. **Grafana** unified UI для всего. **OTel Collector** — единая точка сбора. Деплой через `kube-prometheus-stack` + `loki-stack` + `tempo-distributed` Helm charts. Стоимость: ~$100/мес на S3 storage + compute в существующем кластере. **Альтернатива** — Grafana Cloud Free (10k series, 50 GB logs, 50 GB traces бесплатно) — покрывает small startup без self-hosting.
>
> **Пример:**
> ```yaml
> # values.yaml для loki + tempo + mimir на S3
> loki:
>   storage:
>     bucketNames: {chunks: logs, ruler: rules}
>     s3: {endpoint: s3.amazonaws.com, region: eu-west-1}
> tempo:
>   storage:
>     trace: {backend: s3, s3: {bucket: traces}}
> # OTel Collector — gateway mode
> exporters:
>   loki: {endpoint: http://loki:3100/loki/api/v1/push}
>   otlp/tempo: {endpoint: tempo:4317}
>   prometheusremotewrite: {endpoint: http://mimir:9009/api/v1/push}
> ```
>
> **Когда применять:** Kubernetes-native startups/scale-ups (5–50 engineers), open-source-friendly culture, есть навыки Helm/k8s. Также: cost-sensitive команды с SLO на observability spend.
>
> **Подводные камни:** **Loki не для full-text search** — он индексирует только labels, поиск по `|=` или regex медленный на больших объёмах. Если нужен ELK-style search — лучше OpenSearch. **Tempo требует traceID для запросов** — нельзя «найти все медленные трейсы за час» без external index (TraceQL частично решает). **Vendor lock-in минимален** — OTel обеспечивает миграцию.
>
> **Связанные вопросы:** [[observability-interview#Q27]] — управление стоимостью; [[observability-interview#Q34]] — OTel Collector; [[observability-interview#Q39]] — FinOps.
>
> ---
>
> #### D) Только Prometheus + Grafana — метрик хватит на всё, логи в `kubectl logs` — ❌ Неверно
>
> **Что на самом деле:** Метрики без логов и трейсов покрывают только «detection», но не «diagnosis». При инциденте «error rate 5%» Prometheus покажет график, но не root cause. `kubectl logs` работает только на живых pods — после restart данные теряются. Production-минимум: метрики + persistent logs (хотя бы Loki).
>
> **Откуда путаница:** в учебных туториалах часто показывают только Prometheus — он самый известный из стека. Реально это лишь один pillar из трёх.
>
> **Если бы это было правдой:** при OOMKilled pod логи теряются с рестартом — невозможно понять, в каком месте кода была проблема.
>
> ---

## Q30. Как observability помогает в инциденте и постмортеме?

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
>
> **Вопрос:** Что должен включать качественный post-mortem после production-инцидента, чтобы максимально использовать observability-данные и предотвратить повторение?
>
> ---
>
> #### A) Виновники, дисциплинарные меры и приказ «больше не ломать» — ❌ Неверно
>
> **Что на самом деле:** Blame culture — главный антипаттерн post-mortem. Когда команда боится наказания, инциденты скрываются или замалчиваются, а реальные причины (системные баги, плохая observability, неполный runbook) не устраняются. Industry standard — **blameless post-mortem** (Google SRE Book, Etsy). Цель — улучшить систему, не наказать людей.
>
> **Откуда путаница:** в традиционных корпорациях руководство ожидает «найти виноватого». Это краткосрочно успокаивает менеджмент, но долгосрочно ухудшает MTTR.
>
> **Если бы это было правдой:** команда скрывает инциденты, post-mortems пишут формально, паттерны повторяются — инцидент того же типа происходит снова через месяц.
>
> ---
>
> #### B) Только текстовое описание «что произошло» без timeline и метрик — ❌ Неверно
>
> **Что на самом деле:** Без точного timeline (с timestamps до секунд) и metric snapshots невозможно установить корреляции и понять caustic chain. «Сервис упал, мы починили» — это не post-mortem, это твит. Качественный post-mortem требует timeline из observability-данных, метрик до/после, screenshot дашбордов.
>
> **Откуда путаница:** под пресс «быстрее закрыть тикет» команды пишут краткие текстовые отчёты вместо полноценных post-mortems.
>
> **Если бы это было правдой:** через 3 месяца никто не помнит деталей; preventative actions не выполняются, потому что неясно, что именно предотвращать.
>
> ---
>
> #### C) Точный timeline на основе observability, root cause analysis, impact assessment, action items с дедлайнами и owner, contributing factors — ✓ Верно
>
> **Развёрнутое объяснение:** Качественный post-mortem (формат Google SRE Book) включает: **(1) Timeline** из observability — каждое событие с timestamp, цитатами из логов, screenshot дашбордов. **(2) Impact**: сколько пользователей затронуто (по SLI), revenue impact, продолжительность. **(3) Root cause** — техническая причина (e.g., «certificate expired» — НЕ «человек забыл обновить»). **(4) Contributing factors** — почему мониторинг не предупредил, почему runbook не помог, что было сложно диагностировать. **(5) Action items** с owner, дедлайном, jira-ticket: добавить метрику X, обновить runbook Y, fix bug Z. **(6) Lessons learned** — что повторно использовать. **(7) Blameless tone** — описываем систему, не людей. Регулярные «post-mortem reviews» команды раз в квартал — выявление паттернов между инцидентами.
>
> **Пример:**
> ```markdown
> # Post-Mortem: payment-service outage 2026-04-15
>
> ## Timeline
> | Time (UTC) | Event | Source |
> |------------|-------|--------|
> | 10:15:23 | Alert: error_rate>1% on payment-service | Alertmanager |
> | 10:17:01 | p99 latency = 2.3s (baseline 200ms) | Grafana dashboard |
> | 10:19:42 | Trace: `payment.charge` span timeout 5s | Tempo trace |
> | 10:22:15 | Log: "Connection refused: payment-gateway:443" | Loki |
> | 10:25:00 | Root cause identified: TLS cert expired | Manual diag |
> | 10:28:30 | Fix deployed: cert renewed via ACME | git commit |
> | 10:32:00 | Recovery: error_rate<0.1%, p99=180ms | Dashboard |
>
> ## Impact
> - Duration: 17 minutes (10:15–10:32)
> - Failed payments: ~2,400 transactions
> - Revenue impact: ~$45k
> - SLO budget consumed: 8% of monthly budget
>
> ## Root Cause
> Let's Encrypt cert on payment-gateway expired. ACME auto-renewal cron
> failed silently for 14 days (DNS provider rate-limited).
>
> ## Action Items
> - [ ] @alice — Add `cert_expiry_days` metric + alert at 14 days (2026-04-22)
> - [ ] @bob — Fix ACME cron error handling (2026-04-29)
> - [ ] @carol — Runbook: cert rotation procedure (2026-05-06)
> ```
>
> **Когда применять:** обязательно после P1/P2 инцидентов, опционально для P3 с recurring pattern. Проводить в течение 3 рабочих дней — данные ещё свежие.
>
> **Подводные камни:** **action items без дедлайнов** — главная причина повторения инцидентов. Нужен tracker (Jira label `post-mortem`) и monthly review нерешённых items. **Скрытые root causes** — если первая обнаруженная причина (cert) лежит поверх системной (cron silent failure), нужно «Five Whys» до системной.
>
> **Связанные вопросы:** [[observability-interview#Q31]] — observability в CI/CD; [[observability-interview#Q33]] — maturity model; [[observability-interview#Q38]] — Runbook.
>
> ---
>
> #### D) Записать в Confluence «всё работает, инцидент закрыт» — это формальность для compliance — ❌ Неверно
>
> **Что на самом deal:** Минимальный post-mortem без анализа — формальность, которая создаёт ложное чувство «процесс есть». Через 6 месяцев такой документ не помогает: невозможно восстановить timeline, action items не были созданы.
>
> **Откуда путаница:** ISO/SOC2 compliance иногда требует «incident reports», и команды воспринимают это как paperwork.
>
> **Если бы это было правдой:** учиться на инцидентах невозможно — каждый инцидент уникален вместо того, чтобы быть частью паттерна.
>
> ---

## Q31. Как внедрять observability в CI/CD и релизный процесс?

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
>
> **Вопрос:** Как наиболее эффективно встроить observability в release pipeline для автоматической защиты от плохих релизов?
>
> ---
>
> #### A) Деплоить сразу 100% и руками проверять Grafana в течение часа — ❌ Неверно
>
> **Что на самом деле:** Big-bang deploy + ручная проверка не масштабируется. (1) При плохом релизе 100% пользователей сразу страдают — нет canary buffer. (2) Ручная проверка субъективна и зависит от того, кто смотрит дашборд. (3) Нет automation для rollback — счёт идёт на минуты. Industry standard: progressive delivery (canary / blue-green) + automated guardrails.
>
> **Откуда путаница:** в небольших проектах легче деплоить «как раньше». Но как только сервис достигает SLO, big-bang становится недопустимым.
>
> **Если бы это было правдой:** при плохом релизе MTTR = время на детект (человеческое восприятие) + время на rollback. Это часы, не минуты.
>
> ---
>
> #### B) Покрыть unit-тестами 100% кода — этого достаточно перед deploy в production — ❌ Неверно
>
> **Что на самом деле:** Unit-тесты проверяют корректность кода в изоляции, но не ловят integration-баги: проблемы с конфигурацией prod, latency в реальных сервисах, breaking changes в API партнёров, deadlocks под реальным concurrency. 100% coverage не защищает от 50% production-проблем. Observability в release pipeline — независимый слой защиты.
>
> **Откуда путаница:** «больше тестов = надёжнее» — частично правда, но тесты и observability дополняют, а не заменяют друг друга.
>
> **Если бы это было правдой:** компании Netflix/Google/Amazon с миллионами тестов не нуждались бы в canary deployments — но они активно их используют.
>
> ---
>
> #### C) Canary deployment + automated guardrails (SLI comparison new vs old) + auto-rollback по метрикам + связь release→trace→dashboard — ✓ Верно
>
> **Развёрнутое объяснение:** Полный pipeline: **(1) Canary deployment** — деплоить новую версию на 5–10% traffic (через Argo Rollouts / Flagger / Istio). **(2) Automated SLI comparison**: AnalysisTemplate сравнивает error_rate и latency между новой и старой версией каждые 60 секунд. **(3) Pass/Fail gates**: если новая версия хуже на 1% — auto-rollback, без человека. **(4) Post-deploy validation**: 30 минут наблюдение на 100% трафике, затем «release complete». **(5) Release annotations** в Grafana — связь deploy time с метрикой деградации. **(6) Trace sampling at deploy**: высокий sampling в первые часы релиза для диагностики. **(7) Pre-prod gates**: на staging проверять обязательные метрики/логи/трейсы для новых endpoints — структурные тесты observability-конфига.
>
> **Пример:**
> ```yaml
> # Argo Rollouts — automated canary with guardrails
> apiVersion: argoproj.io/v1alpha1
> kind: AnalysisTemplate
> metadata:
>   name: success-rate
> spec:
>   metrics:
>     - name: error-rate
>       interval: 60s
>       count: 5
>       successCondition: result[0] < 0.01    # <1% error rate
>       failureLimit: 2                       # 2 failures → rollback
>       provider:
>         prometheus:
>           address: http://prometheus:9090
>           query: |
>             sum(rate(http_requests_total{
>               app="{{args.service}}",
>               version="{{args.version}}",
>               status=~"5.."
>             }[5m]))
>             /
>             sum(rate(http_requests_total{
>               app="{{args.service}}",
>               version="{{args.version}}"
>             }[5m]))
>     - name: p99-latency
>       successCondition: result[0] < 0.5
>       provider:
>         prometheus:
>           query: |
>             histogram_quantile(0.99,
>               sum(rate(http_request_duration_seconds_bucket{
>                 app="{{args.service}}",
>                 version="{{args.version}}"
>               }[5m])) by (le))
> ```
>
> **Когда применять:** обязательно для сервисов с SLO (любой пользовательский сервис), для high-traffic API. Особенно критично для финансовых/payment-сервисов, где даже минута деградации = большие потери.
>
> **Подводные камни:** **Low-traffic canary** — при 5% трафика на сервис с 100 RPS получаем 5 RPS на новой версии, статистики на 60 секунд может быть недостаточно (false positives). Решение: увеличить window до 5–10 минут или использовать synthetic load. **Метрики до окончания warm-up** — первые 30 секунд после старта pod показывают спайки латентности (JVM warmup, connection pool init); нужен `initialDelay` в analysis.
>
> **Связанные вопросы:** [[observability-interview#Q30]] — post-mortem; [[observability-interview#Q23]] — SLO/burn rate; [[observability-interview#Q33]] — maturity model.
>
> ---
>
> #### D) Только полные нагрузочные тесты перед каждым релизом — этого достаточно — ❌ Неверно
>
> **Что на самом деле:** Load testing в staging не репродуцирует production-условия: реальный traffic pattern, реальные данные, реальные external dependencies (за исключением chaos testing с production-shadow trafic). Многие баги проявляются только в prod (data corner cases, third-party API quirks). Load testing полезен, но не заменяет canary + observability.
>
> **Откуда путаница:** load testing видим (есть отчёт), canary deployment — процесс (нет одного артефакта).
>
> **Если бы это было правдой:** компании не использовали бы canary — но Amazon, Google, Netflix используют его именно потому, что load testing недостаточно.
>
> ---

## Q32. (!) Какие anti-patterns в observability встречаются чаще всего?

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
>
> **Вопрос:** Какой из подходов является фундаментальным anti-pattern в observability — повторяется в большинстве команд и приводит к самым серьёзным последствиям в production?
>
> ---
>
> #### A) High-cardinality labels (userId, requestId, sessionId в Prometheus labels) — ✓ Верно
>
> **Развёрнутое объяснение:** Cardinality explosion — главный anti-pattern, потому что (1) **последствия катастрофические**: OOM Prometheus, остановка всего мониторинга. (2) **Часто встречается**: разработчик думает «добавлю userId в label для дебага», не понимая time series модели. (3) **Сложно откатить**: time series уже созданы, retention длинный, удаление требует tombstones. Prometheus хранит одну time series на каждую уникальную комбинацию labels. С 1M users × 100 endpoints × 5 status codes = 500M time series ≈ 100+ GB RAM, что взрывает Prometheus. Правило: **labels должны иметь ограниченное и предсказуемое множество значений** (HTTP method, status code, endpoint, region — да; userId, requestId, traceId — НЕТ). Для high-cardinality данных используются traces (Tempo) и logs (Loki), которые индексируют иначе.
>
> **Пример:**
> ```java
> // BAD: cardinality explosion
> Counter.builder("http.requests")
>     .tag("user_id", userId)        // 1M+ unique values
>     .tag("request_id", reqId)      // unique per request!
>     .register(registry);
>
> // GOOD: predictable cardinality
> Counter.builder("http.requests")
>     .tag("method", method)         // GET, POST, PUT, DELETE (~10)
>     .tag("status", String.valueOf(status))  // 2xx, 4xx, 5xx (~20)
>     .tag("endpoint", normalizeEndpoint(uri))// /api/users/{id} (~100)
>     .register(registry);
> // Total cardinality: 10 × 20 × 100 = 20,000 — manageable
> ```
>
> Защитные механизмы: (1) `metric_relabel_configs` в Prometheus для drop high-cardinality labels; (2) `--storage.tsdb.max-block-duration` лимиты; (3) alert на `prometheus_tsdb_head_series` (sudden growth = cardinality bug); (4) Mimir/VictoriaMetrics поддерживают `max_series_per_user`.
>
> **Когда применять:** правило enforced **всегда**, на любой Prometheus-based мониторинг. Особенно критично для multi-tenant сервисов.
>
> **Подводные камни:** **postmortem-расследование cardinality** — Prometheus уже упал, нужно понять, какой именно label виноват. Использовать `prometheus_tsdb_head_series_created_total` per metric перед падением. **Auto-instrumentation** (Spring Boot Actuator) иногда добавляет `uri` label с path variables — нужен URI normalization.
>
> **Связанные вопросы:** [[observability-interview#Q8]] — cardinality в Prometheus; [[observability-interview#Q26]] — sampling трейсов для high-cardinality; [[observability-interview#Q33]] — зрелость observability.
>
> ---
>
> #### B) Использовать Grafana вместо самописных HTML-дашбордов — ❌ Неверно
>
> **Что на самом деле:** Использование Grafana — это **best practice**, не anti-pattern. Grafana — индустриальный стандарт для визуализации метрик. Самописные дашборды на HTML/JS — это анти-паттерн (drift, нет versioning, нет sharing).
>
> **Откуда путаница:** перевернутая логика. Реальный anti-pattern в дашбордах — это «copy-paste без maintenance» и «dashboard-as-art», а не выбор инструмента.
>
> **Если бы это было правдой:** Grafana — основной инструмент в LGTM, Datadog, New Relic — все они интегрируют с Grafana или копируют её UX.
>
> ---
>
> #### C) Хранить логи в Elasticsearch вместо в plain text — ❌ Неверно
>
> **Что на самом деле:** Elasticsearch для логов — стандартное и обоснованное решение (ELK-стек). Plain text logs (`tail -f` на файлы) — это анти-паттерн, не наоборот. Реальный pattern: structured JSON logs в indexed log store (Elasticsearch / Loki / Splunk).
>
> **Откуда путаница:** Elasticsearch дорогой, кто-то может назвать его «не нужным», но это не делает его anti-pattern.
>
> **Если бы это было правдой:** SRE-команды использовали бы `grep` на файлах — но это работает только для single-host, ломается в distributed system.
>
> ---
>
> #### D) Добавлять traceId во все логи — это лишняя нагрузка на logger — ❌ Неверно
>
> **Что на самом деле:** TraceId в логах — **обязательное** требование для корреляции трёх pillars (см. Q3). Overhead на logger — несколько байт на сообщение, что незаметно. Без traceId логи изолированы от трейсов — невозможна cross-pillar диагностика.
>
> **Откуда путаница:** иногда оптимизируют log size, не понимая, что traceId — это ROI > 100x на расследовании инцидентов.
>
> **Если бы это было правдой:** при инциденте «traceId = abc123, latency 5s» SRE не может найти соответствующие логи — диагностика растягивается на часы.
>
> ---

## Q33. Как оценить зрелость observability в команде?

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
>
> **Вопрос:** Команда заявляет «у нас есть observability — мы поставили Prometheus и Grafana». Какой набор критериев позволяет объективно оценить реальную зрелость observability в команде?
>
> ---
>
> #### A) Только количество метрик в Prometheus и количество дашбордов в Grafana — ❌ Неверно
>
> **Что на самом деле:** Метрики vanity (количество дашбордов) не отражают зрелость. Команда может иметь 500 дашбордов и ноль actionable алертов — это «много шума, мало сигнала». Зрелость измеряется **MTTD / MTTR** и **процентом инцидентов, обнаруженных мониторингом** (не клиентами).
>
> **Откуда путаница:** руководству легко продать «1000 метрик» как достижение; но это вход, а не результат.
>
> **Если бы это было правдой:** команда оптимизировала бы под «больше графиков», игнорируя actionable nature алертов.
>
> ---
>
> #### B) Многоуровневая модель: L1 базовый мониторинг → L2 SLO-driven → L3 корреляция трёх pillars → L4 встроенная в SDLC → L5 проактивная (anomaly detection, chaos) + измерение MTTD/MTTR — ✓ Верно
>
> **Развёрнутое объяснение:** Industry-standard maturity model для observability имеет 5 уровней. **L1 (Basic Monitoring)**: есть Prometheus + Grafana, есть базовые алерты, но они threshold-based и часто false-positive. **L2 (SLO-driven)**: определены SLI/SLO для критичных сервисов, алерты переведены на burn rate, есть runbooks для каждого page-алерта. **L3 (Correlated)**: три pillars связаны через traceId, переход metric→trace→log за 1 клик, structured logging повсеместно. **L4 (Embedded in SDLC)**: observability в CI/CD pipeline (canary guardrails, auto-rollback), pre-deploy checks обязательных метрик, release annotations. **L5 (Proactive)**: anomaly detection (ML-based), capacity planning по метрикам, chaos engineering как regular practice, continuous profiling. **Главный критерий**: MTTD и MTTR **снижаются** или хотя бы держатся постоянными при росте сложности системы. Если они растут — observability отстаёт от роста системы.
>
> **Пример checklist для self-assessment:**
> ```markdown
> ## Observability Maturity Checklist (target: L3+)
> - [ ] Все сервисы экспортируют RED-метрики (Rate, Errors, Duration)
> - [ ] Structured JSON logging с traceId/spanId во всех сервисах
> - [ ] Distributed tracing покрывает 100% inter-service вызовов
> - [ ] SLO определены для критичных user-facing сервисов
> - [ ] Алерты имеют runbooks и severity levels (P1-P4)
> - [ ] Дашборды организованы трёхуровнево: overview → service → debug
> - [ ] Observability проверяется в CI/CD pipeline (canary analysis)
> - [ ] Команда проводит регулярные "observability reviews" (monthly)
> - [ ] MTTD < 5 min для P1, MTTR < 30 min для P1
> - [ ] % инцидентов, обнаруженных monitoring (не клиентами) > 90%
> ```
>
> **Когда применять:** ежеквартально (observability review), при онбординге нового сервиса, при росте команды > 20 разработчиков.
>
> **Подводные камни:** **L5 без L1-L4** — команды покупают AI-based anomaly detection, не имея structured logs. Это «решение в поисках проблемы». Развитие должно быть последовательным. **MTTD сложно мерить точно**: нужно знать, когда инцидент *реально* начался — это требует post-mortem дисциплины.
>
> **Связанные вопросы:** [[observability-interview#Q30]] — post-mortem; [[observability-interview#Q31]] — CI/CD observability; [[observability-interview#Q32]] — anti-patterns.
>
> ---
>
> #### C) Только наличие 24/7 on-call rotation — без него зрелости нет — ❌ Неверно
>
> **Что на самом деле:** On-call — это operational практика, а не observability maturity. Можно иметь on-call без хорошей observability (тогда дежурный страдает) и наоборот — иметь зрелую observability в B2B-сервисе без 24/7 (бизнес-часы достаточно).
>
> **Откуда путаница:** on-call часто упоминается рядом с observability, но это разные дисциплины.
>
> **Если бы это было правдой:** маленькие startups никогда не могли бы иметь зрелую observability — но многие имеют, без формального 24/7.
>
> ---
>
> #### D) Сертификация по ISO/SOC2 — это объективная мера зрелости — ❌ Неверно
>
> **Что на самом деле:** Compliance сертификации проверяют **наличие процессов** (logging есть, retention есть), но не **качество** observability. Можно пройти SOC2 с зрелостью L1, имея только базовые логи и аудит-трейлы.
>
> **Откуда путаница:** compliance audits дают «галочку» руководству, но не отражают эффективность диагностики инцидентов.
>
> **Если бы это было правдой:** SOC2-certified компании никогда не имели бы громких outage — но Cloudflare, AWS, Google имеют outage регулярно, при всех сертификациях.
>
> ---

## Q34. OpenTelemetry Collector: архитектура, pipeline и processors?

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
>
> **Вопрос:** В чём ключевая роль OpenTelemetry Collector в production-архитектуре и какой режим деплоя подходит для большого Kubernetes-кластера с тысячами подов?
>
> ---
>
> #### A) OTel Collector — это только агент для отправки данных в один backend (например, Jaeger) — ❌ Неверно
>
> **Что на самом деле:** OTel Collector — **vendor-neutral pipeline** для приёма, обработки и многократного экспорта телеметрии. Один Collector может одновременно отправлять в Prometheus (metrics), Loki (logs), Tempo (traces), Datadog (mirror), S3 (archive). Это **fan-out** abstraction, которая делает миграцию между бэкендами тривиальной (только в exporter config).
>
> **Откуда путаница:** часто в туториалах показывают один экспортёр для простоты, отсюда восприятие «Collector = агент».
>
> **Если бы это было правдой:** vendor lock-in был бы неизбежен, переход с Jaeger на Tempo требовал бы пересборки приложений.
>
> ---
>
> #### B) OTel Collector — это просто proxy без обработки данных — ❌ Неверно
>
> **Что на самом деле:** Collector — это не proxy, а **stateful pipeline** с processors. Ключевые processors: `batch` (буферизация), `memory_limiter` (защита от OOM), `tail_sampling` (решение о sampling после получения трейса), `filter` (drop unwanted data), `resource` (add attributes), `transform` (rewrite fields). Это активная обработка данных, не пассивная пересылка.
>
> **Откуда путаница:** слово «collector» намекает на passive role, но реальная роль — pipeline orchestrator.
>
> **Если бы это было правдой:** не было бы tail-sampling, не было бы cost optimization — приложение слало бы 100% сырых данных в backend.
>
> ---
>
> #### C) Vendor-neutral pipeline для приёма/обработки/экспорта телеметрии; в большом k8s — комбинированный режим: Agent (DaemonSet/sidecar) → Gateway (Deployment), где Agent делает local collection, Gateway — tail-sampling и forwarding в backend — ✓ Верно
>
> **Развёрнутое объяснение:** Архитектура: `[Receivers] → [Processors] → [Exporters]`. Receivers принимают данные (OTLP, Prometheus scrape, Fluent Forward), processors их обрабатывают, exporters отправляют в backend. **Режимы деплоя:** (1) **Agent mode** — DaemonSet (один Collector на ноду) или sidecar (в каждом pod) — низкая задержка, изоляция; (2) **Gateway mode** — централизованный Deployment (3–10 instances), упрощает конфигурацию, позволяет tail-sampling по полному трейсу; (3) **Combined mode** — Agent (DaemonSet) собирает локально → Gateway (Deployment) делает tail-sampling и форвардит в backend. Combined — production-стандарт для k8s с >1000 подов: Agent даёт buffering и resilience при сбое Gateway, Gateway — global view трейсов для sampling. Между Agent и Gateway используется `loadbalancing exporter` с `routing_key: traceID` — гарантирует, что все spans одного трейса попадут на один Gateway instance.
>
> **Пример:**
> ```yaml
> # Agent (DaemonSet) — local collection + basic processing
> # values-agent.yaml
> mode: daemonset
> config:
>   receivers:
>     otlp: {protocols: {grpc: {endpoint: 0.0.0.0:4317}}}
>   processors:
>     batch: {timeout: 1s, send_batch_size: 1024}
>     memory_limiter: {limit_mib: 256, check_interval: 1s}
>   exporters:
>     loadbalancing:
>       routing_key: traceID
>       protocol:
>         otlp: {tls: {insecure: true}}
>       resolver:
>         k8s: {service: otel-gateway.observability}
>   service:
>     pipelines:
>       traces:
>         receivers: [otlp]
>         processors: [memory_limiter, batch]
>         exporters: [loadbalancing]
>
> # Gateway (Deployment) — tail-sampling + final export
> # values-gateway.yaml
> mode: deployment
> replicaCount: 5
> config:
>   processors:
>     tail_sampling:
>       decision_wait: 10s
>       policies:
>         - {name: errors, type: status_code, status_code: {status_codes: [ERROR]}}
>         - {name: slow, type: latency, latency: {threshold_ms: 1000}}
>         - {name: probabilistic, type: probabilistic, probabilistic: {sampling_percentage: 10}}
>   exporters:
>     otlp/tempo: {endpoint: tempo:4317}
>     prometheusremotewrite: {endpoint: http://mimir:9009/api/v1/push}
> ```
>
> **Когда применять:** Kubernetes-кластер с >50 сервисами или >1000 подов; multi-cluster setup; необходимость tail-sampling. Для small setup (<10 сервисов) достаточно Gateway mode.
>
> **Подводные камни:** **Loadbalancing exporter и pod restarts** — при перезапуске Gateway pod трейсы могут разорваться (spans на разных Gateway). Решение: использовать `sticky session` через `headless service`. **Memory pressure в Gateway** при tail-sampling — нужен `memory_limiter` обязательно, иначе OOM. **Версионная совместимость** OTel Collector меняется быстро — pinned version в Helm chart.
>
> **Связанные вопросы:** [[observability-interview#Q26]] — sampling стратегии; [[observability-interview#Q28]] — observability без перегруза; [[observability-interview#Q39]] — FinOps и sampling.
>
> ---
>
> #### D) Использовать sidecar pattern для каждого приложения — это даёт максимальную изоляцию — ❌ Неверно
>
> **Что на самом деле:** Sidecar в каждом pod создаёт огромные накладные расходы: 1000 pods × Collector overhead (50–200 MB RAM каждый) = 50–200 GB кластерной памяти. DaemonSet (один Collector на ноду) даёт ту же изоляцию по нодам с в 10–100x меньшим overhead. Sidecar уместен только для critical isolation requirements (multi-tenant с строгой изоляцией).
>
> **Откуда путаница:** sidecar pattern популярен (Istio, Envoy), но не всегда оптимален.
>
> **Если бы это было правдой:** kube-prometheus-stack и популярные Helm charts использовали бы sidecar, но они используют DaemonSet/Deployment.
>
> ---

## Q35. Continuous Profiling: Pyroscope, Grafana Phlare, eBPF?

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
>
> **Вопрос:** Чем continuous profiling принципиально отличается от distributed tracing и почему его называют «четвёртым столпом» observability?
>
> ---
>
> #### A) Continuous profiling — это просто более частая версия traditional CPU profiling (jstack, async-profiler) — ❌ Неверно
>
> **Что на самом деле:** Continuous profiling — это **постоянный, low-overhead** сбор профилей **в production**, в отличие от traditional profiling, который запускается разово при расследовании. Ключевые отличия: (1) **overhead < 1% CPU** (vs 5–30% для traditional), (2) **корреляция с traces/metrics через timestamps**, (3) **persistent storage** профилей как time series (можно сравнивать v1 vs v2 релизы), (4) **continuous sampling** даёт ретроспективу — «что было 2 дня назад в 14:35».
>
> **Откуда путаница:** инструменты похожи (async-profiler в обоих случаях), но режим работы и хранение принципиально разные.
>
> **Если бы это было правдой:** не было бы отдельной категории инструментов (Pyroscope, Grafana Phlare, Polar Signals) — все использовали бы async-profiler руками.
>
> ---
>
> #### B) Continuous profiling показывает только CPU usage — для memory нужны другие инструменты — ❌ Неверно
>
> **Что на самом деле:** Современный continuous profiling покрывает несколько типов профилей: **CPU** (где тратится время), **Heap Allocation** (источник GC pressure), **Lock Contention** (deadlocks, мьютексы), **Wall Clock** (включая IO wait), **GPU** (для ML workloads). Pyroscope agent для Java через JFR собирает `cpu,alloc,lock` одновременно.
>
> **Откуда путаница:** CPU profiling — самый известный тип, часто упрощают до «profiling = CPU».
>
> **Если бы это было правдой:** не было бы возможности диагностировать GC-проблемы или lock contention через profiling — но это базовая функция Pyroscope/Phlare.
>
> ---
>
> #### C) Постоянный low-overhead сбор профилей CPU/heap/lock на уровне функций кода в production, корреляция с traces через profileID, ретроспективный анализ деградации — ✓ Верно
>
> **Развёрнутое объяснение:** Continuous profiling — четвёртый столп observability, потому что отвечает на вопрос **«что именно делает CPU/memory в этот момент?»** — на уровне функций и строк кода. Distributed tracing показывает, **какой span медленный** (e.g., `payment.charge` = 500ms), но не **что внутри него происходит** (sleep? CPU-bound loop? lock wait?). Profiling даёт **flame graph** до строки кода. Корреляция: при включённой интеграции (Pyroscope + Tempo), каждый span получает `profileID` attribute, в Grafana из trace можно одним кликом перейти к профилю. Технологии: **Pyroscope** (теперь Grafana Pyroscope) — primary OSS choice, поддерживает Java (через JFR), Go (pprof), Python, Ruby, Rust, eBPF. **eBPF-based profiling** (Grafana Beyla, Parca) — работает без agent, на уровне kernel, профилирует **любой** процесс. **Use case 1**: «latency spike каждые 10 минут» — flame graph показывает GC stop-the-world (long allocation). **Use case 2**: «release v2 увеличил CPU на 20%» — diff flame graph между v1 и v2 показывает, какая функция стала медленнее.
>
> **Пример:**
> ```java
> // application.properties — Pyroscope agent для Java
> // Запуск: java -javaagent:pyroscope.jar -jar app.jar
> pyroscope.application.name=orders-service
> pyroscope.server.address=http://pyroscope:4040
> pyroscope.format=jfr
> pyroscope.profiler.event=cpu,alloc,lock
> pyroscope.profiler.upload.interval=10s
> pyroscope.labels=region=eu-west-1,version=2.3.1
>
> // OpenTelemetry integration — profileID в spans
> // tracer adds profile.id attribute to each span
> // Grafana: click "View Profile" from trace view
> ```
>
> **Когда применять:** обязательно для performance-critical сервисов (payment, search, recommendation). Для других — опционально. Стоимость: ~1% CPU overhead, ~10–50 GB storage в месяц для среднего сервиса.
>
> **Подводные камни:** **JFR overhead не всегда 1%** — при включении `alloc` profiling может вырасти до 3–5% на allocation-heavy сервисах. **eBPF требует privileged container** или `CAP_BPF` capability — security implications в multi-tenant clusters. **Profiles большие** — flame graph для часа работы = 100+ MB, нужны компрессия и retention policy.
>
> **Связанные вопросы:** [[observability-interview#Q34]] — OTel Collector и profiling pipeline; [[observability-interview#Q36]] — chaos engineering; [[observability-interview#Q39]] — FinOps.
>
> ---
>
> #### D) Profiling нужен только для local development, в production это слишком дорого — ❌ Неверно
>
> **Что на самом деле:** Это устаревшее представление. Современный continuous profiling (Pyroscope с eBPF/JFR) специально спроектирован для production: low-overhead, low-storage, корреляция с traces. Компании Netflix, Uber, Shopify используют production profiling.
>
> **Откуда путаница:** старые инструменты (JProfiler в interactive mode) действительно вызывали серьёзный overhead. Continuous profiling — новое поколение.
>
> **Если бы это было правдой:** Grafana не приобретала бы Pyroscope, Polar Signals не основывалась бы — но profiling-сегмент быстро растёт именно для production.
>
> ---

## Q36. Chaos Engineering и Observability: как связаны?

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
>
> **Вопрос:** Почему chaos engineering без observability бесполезен и как они дополняют друг друга в практике?
>
> ---
>
> #### A) Chaos engineering — это про резилиентность, observability — про мониторинг. Они независимы — ❌ Неверно
>
> **Что на самом деле:** Chaos engineering **без** observability — это «стрельба вслепую». Цель chaos experiment — проверить гипотезу о поведении системы при сбое. Без observability вы не можете **подтвердить** или **опровергнуть** гипотезу — только наблюдать, что «всё упало». Они тесно связаны: chaos предоставляет controlled failure, observability предоставляет инструменты измерения.
>
> **Откуда путаница:** дисциплины развивались параллельно, поэтому их часто путают как независимые. Но Netflix (создатель Chaos Monkey) и Honeycomb всегда подчёркивали их взаимозависимость.
>
> **Если бы это было правдой:** Chaos Monkey запускали бы без мониторинга — но даже в первой статье Netflix о Chaos Monkey (2011) observability была частью протокола.
>
> ---
>
> #### B) Chaos engineering можно применять только в staging — в production это слишком опасно — ❌ Неверно
>
> **Что на самом деле:** Chaos engineering **в production** — это industry-standard practice (Netflix, Amazon, Google). Принципы безопасности: **(1) Blast radius** — ограничить scope (один pod, не весь сервис), **(2) Hypothesis-driven** — есть гипотеза «при X сработает Y», **(3) Auto-abort** — если metrics выходят за границы, эксперимент останавливается автоматически. Staging-only chaos упускает реальные failure modes, которые видны только в production (real traffic patterns, real data, real third-party APIs).
>
> **Откуда путаница:** «production = опасно» — естественная осторожность. Но без production chaos нет уверенности, что система устойчива.
>
> **Если бы это было правдой:** Netflix не запускал бы Chaos Monkey в production — но он работает там более 10 лет.
>
> ---
>
> #### C) Chaos engineering без observability невозможен — observability даёт измерение гипотезы; chaos выявляет gaps в observability (что не видно при сбое) — циклическая взаимозависимость — ✓ Верно
>
> **Развёрнутое объяснение:** Цикл **Chaos + Observability**: (1) **Hypothesize** — «при потере 30% запросов к БД, Circuit Breaker сработает и пользователи получат graceful degradation». (2) **Define metrics** — что измеряем: error_rate, p99_latency, circuit_breaker_state. (3) **Run experiment** — Gremlin/LitmusChaos вносит latency или packet loss. (4) **Observe via three pillars** — metrics показывают error rate, traces показывают, какие именно span'ы падают, logs показывают сообщения circuit breaker. (5) **Validate** — гипотеза подтверждена или опровергнута данными. (6) **Discover gaps** — типичный результат: «мы не можем объяснить, что произошло на X компоненте, потому что нет метрик для него» — chaos выявил **blind spots в observability**. (7) **Iterate** — добавить недостающую observability, повторить эксперимент. **Принципы безопасности**: **blast radius** (ограниченный scope), **steady-state hypothesis** (определить «нормальное» состояние), **automated abort** (auto-stop при превышении SLO), **runbook** для отката.
>
> **Пример:**
> ```yaml
> # LitmusChaos experiment с automated abort через Prometheus
> apiVersion: litmuschaos.io/v1alpha1
> kind: ChaosEngine
> metadata:
>   name: payment-service-network-loss
> spec:
>   appinfo:
>     appns: production
>     applabel: app=payment-service
>     appkind: deployment
>   chaosServiceAccount: litmus-admin
>   experiments:
>     - name: pod-network-loss
>       spec:
>         components:
>           env:
>             - name: NETWORK_PACKET_LOSS_PERCENTAGE
>               value: '30'
>             - name: TOTAL_CHAOS_DURATION
>               value: '60'  # 60 секунд
>         probe:
>           - name: error-rate-check
>             type: promProbe
>             promProbe/inputs:
>               endpoint: http://prometheus:9090
>               query: |
>                 sum(rate(http_requests_total{
>                   service="payment-service", status=~"5.."
>                 }[1m])) /
>                 sum(rate(http_requests_total{
>                   service="payment-service"
>                 }[1m]))
>               comparator:
>                 type: float
>                 criteria: '<'    # error rate должен быть < 5%
>                 value: '0.05'
>             mode: Continuous     # проверка каждые 5 секунд
> ```
>
> **Когда применять:** **GameDays** (раз в квартал — все команды), **regular chaos** (раз в неделю — автоматизировано), **pre-release chaos** (перед major release). Начинать с staging, потом — production с blast radius.
>
> **Подводные камни:** **Chaos в peak hours** — может вызвать реальную деградацию для пользователей. Решение: запускать в low-traffic окнах. **Compound failures** — если одновременно chaos + реальный инцидент, сложно отделить. Решение: **chaos calendar** и интеграция с alerting (не алертить во время запланированного chaos). **Stakeholder communication** — бизнес должен знать, что chaos = намеренные сбои, иначе panic.
>
> **Связанные вопросы:** [[observability-interview#Q33]] — maturity model (L5 = proactive); [[observability-interview#Q35]] — profiling под нагрузкой; [[observability-interview#Q37]] — alert fatigue.
>
> ---
>
> #### D) Chaos engineering = Chaos Monkey — random instance termination, ничего больше — ❌ Неверно
>
> **Что на самом деле:** Chaos Monkey (2011) — пионер, но современный chaos engineering — гораздо шире: **latency injection** (Toxiproxy, Gremlin), **CPU/memory stress**, **network partitions** (split-brain testing), **disk failures**, **time skew** (NTP drift), **certificate expiry simulation**. Это целая дисциплина с principles и hypothesis-driven подходом, не «random kill».
>
> **Откуда путаница:** Chaos Monkey — самый известный инструмент, отсюда упрощение.
>
> **Если бы это было правдой:** не было бы LitmusChaos, Gremlin, Chaos Mesh — но эти платформы активно развиваются.
>
> ---

## Q37. Alert Fatigue: причины, как бороться, стратегии silencing?

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
>
> **Вопрос:** Команда жалуется на alert fatigue (50+ алертов в день, дежурные игнорируют). Какая комбинация изменений даёт устойчивый эффект, а не временное затыкание?
>
> ---
>
> #### A) Просто отключить половину алертов — оставить только самые громкие — ❌ Неверно
>
> **Что на самом деле:** Случайное отключение «громких» алертов — это ложь себе и игнорирование реальных проблем. Если алерт срабатывает часто, есть два варианта: (1) это **реальная проблема** — её надо чинить, не игнорировать; (2) это **неактуальный алерт** — его надо удалить осознанно после анализа. Просто silence — это «scotch-tape over check-engine light».
>
> **Откуда путаница:** под давлением «надо снизить количество алертов в Jira» команды часто делают именно это.
>
> **Если бы это было правдой:** через 3 месяца окажется, что отключили алерт на реальную проблему — она проявится как outage, замеченный пользователями.
>
> ---
>
> #### B) Купить более дорогую alerting platform (PagerDuty Enterprise) — она «умнее» — ❌ Неверно
>
> **Что на самом деле:** Tool не решает проблему **плохих алертов**. PagerDuty Enterprise добавляет ML-based grouping, routing, escalation — но если базовые алерты бессмысленны (threshold без контекста, нет runbook), tool не поможет. Сначала надо чинить алерты, потом улучшать routing.
>
> **Откуда путаница:** vendor-маркетинг продаёт «AI-powered noise reduction» как silver bullet.
>
> **Если бы это было правдой:** компании покупали бы PagerDuty Enterprise и забывали бы про alert fatigue — но проблема остаётся даже с лучшими tools.
>
> ---
>
> #### C) Переход с threshold на SLO/burn-rate alerting + severity levels с runbooks + дедупликация в Alertmanager + monthly alert review + silencing maintenance windows + удаление dead alerts — ✓ Верно
>
> **Развёрнутое объяснение:** Системное решение alert fatigue — multi-pronged подход. **(1) SLO-based alerting** заменяет threshold-based: вместо «CPU > 80%» (всегда срабатывает, не значит проблема) использовать burn rate — «error budget сгорает в 14.4x быстрее нормы за 1 час → page» (Google SRE multi-window strategy). **(2) Severity levels** с чёткими определениями: P1 (требует немедленной реакции, звонок, < 5 мин), P2 (час), P3 (рабочее время), P4 (бэклог). **(3) Runbooks обязательны для page-алертов** — без runbook алерт удаляется или понижается до P3. **(4) Дедупликация в Alertmanager**: `group_by: [alertname, cluster, service]`, `group_wait: 30s`, `repeat_interval: 4h` (не спамить). **(5) Dependency silencing**: если БД down — заглушить все зависимые алерты автоматически. **(6) Monthly alert review** — команда смотрит топ-20 алертов: те, на которые никто не реагировал → удалить или понизить severity. **(7) Maintenance windows** — silence во время planned deploys. **(8) Time-based silencing** — P3 ночью не будит, идёт в утренний ticket.
>
> **Пример:**
> ```yaml
> # Alertmanager — продвинутая конфигурация
> route:
>   group_by: ['alertname', 'cluster', 'service']
>   group_wait: 30s              # подождать собрать связанные
>   group_interval: 5m
>   repeat_interval: 4h          # не спамить чаще
>   receiver: 'default'
>   routes:
>     # P1 — звонок, любое время
>     - matchers: [severity="P1"]
>       receiver: 'pagerduty-critical'
>       continue: true
>     # P3 — не будить ночью (00:00-08:00 MSK)
>     - matchers: [severity="P3"]
>       receiver: 'slack-team'
>       active_time_intervals: [work-hours]
>     # Dependency silence — если БД down, заглушить app алерты
>     - matchers: [alertname="DatabaseDown"]
>       receiver: 'pagerduty-critical'
>       routes:
>         - matchers: [service=~"orders|payments"]
>           receiver: 'silent'  # blackhole
>
> time_intervals:
>   - name: work-hours
>     time_intervals:
>       - weekdays: [monday:friday]
>         times: [{start_time: 08:00, end_time: 20:00}]
>         location: Europe/Moscow
>
> # SLO-based burn rate alert (вместо threshold)
> groups:
>   - name: slo-burn-rate
>     rules:
>       - alert: ErrorBudgetBurnTooFast
>         expr: |
>           (sum(rate(http_requests_total{status=~"5.."}[1h])) /
>            sum(rate(http_requests_total[1h]))) > 14.4 * (1 - 0.999)
>         for: 5m
>         labels: {severity: P1}
>         annotations:
>           summary: "Error budget burning 14.4x — будет исчерпан за 2 дня"
>           runbook_url: "https://runbooks/error-budget-burn"
> ```
>
> **Когда применять:** обязательно при >20 алертах в день на команду; критично для on-call rotation health. Регулярный alert review — каждый месяц.
>
> **Подводные камни:** **Silence-all-the-things** — можно перестараться и заглушить реально важное. Решение: silence только с TTL (1 неделя max), требовать justification. **Burn rate alerts требуют SLO** — без определённых SLI/SLO формула не работает (см. Q23). **Dependency silencing** требует знания graph зависимостей сервисов — нужен service map в Grafana / Istio.
>
> **Связанные вопросы:** [[observability-interview#Q23]] — SLO/burn rate; [[observability-interview#Q24]] — multi-window strategy; [[observability-interview#Q38]] — Runbook.
>
> ---
>
> #### D) Алерты в Slack-канал, дежурный сам решает, на что реагировать — это самый гибкий подход — ❌ Неверно
>
> **Что на самом деле:** Без severity levels и routing «всё в один канал» — это рецепт alert fatigue. Дежурный получает 100+ сообщений за смену, реальные P1 теряются. Slack — это **дополнительный канал** для P3/P4, но не основной для P1/P2 (там нужен phone call через PagerDuty / OpsGenie).
>
> **Откуда путаница:** маленькие команды действительно используют только Slack — это работает до определённого scale.
>
> **Если бы это было правдой:** не существовало бы PagerDuty с миллиардной валуацией — но on-call platforms активно растут.
>
> ---

## Q38. Что включать в Runbook для автоматизации реакции на инцидент?

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
>
> **Вопрос:** Что отличает качественный actionable runbook от формального документа «для галочки», и почему это критично для MTTR?
>
> ---
>
> #### A) Главное — общее описание проблемы и кому звонить в случае непонимания — ❌ Неверно
>
> **Что на самом деле:** «Общее описание» и эскалация — это часть runbook, но **не его суть**. Если дежурный получает page в 3 ночи и читает «что-то с базой данных, звони @db-team», MTTR растягивается на час ожидания эксперта. Качественный runbook позволяет **дежурному без специальных знаний** выполнить базовые диагностические и mitigation шаги.
>
> **Откуда путаница:** runbook часто пишется как «теоретический документ», а не операционный инструмент.
>
> **Если бы это было правдой:** все инциденты эскалировались бы экспертам — это разрушает on-call rotation, эксперты выгорают.
>
> ---
>
> #### B) Только описание severity и владельца — детали в коде/документации — ❌ Неверно
>
> **Что на самом деле:** «Детали в коде» означает, что дежурному надо в 3 ночи читать кодовую базу, чтобы понять, как отлаживать payment-service. Это невозможно для большой системы и неэффективно даже для маленькой. Runbook **должен быть самодостаточным** для базовой диагностики.
>
> **Откуда путаница:** разработчики, которые знают код, думают «всё понятно», и не пишут runbook. Но дежурный — не автор кода.
>
> **Если бы это было правдой:** новые дежурные после онбординга не могли бы реагировать на инциденты — но это и есть случай в командах без runbooks.
>
> ---
>
> #### C) Severity + impact + конкретные команды диагностики (kubectl, queries, dashboard URL) + actionable mitigation шаги + ссылки на observability + signs of recovery + время эскалации + дата последнего обновления — ✓ Верно
>
> **Развёрнутое объяснение:** Качественный runbook — это **operational tool**, который позволяет дежурному за 5–15 минут локализовать и mitigate проблему. Структура: **(1) Метаданные**: severity (P1/P2), алерт, владелец команды, дата последнего обновления. **(2) Описание и impact**: что произошло и какое бизнес-влияние (revenue, users, SLO budget). **(3) Диагностика — конкретные команды**: `kubectl logs -l app=orders --tail=100`, Kibana query `q=level:ERROR&sort=@timestamp:desc`, ссылка на Grafana dashboard с pre-filtered time range. **(4) Зависимости проверить**: БД (link to dashboard), Kafka (link), upstream services. **(5) Последний deploy**: `kubectl rollout history deployment/X` — release часто причина. **(6) Mitigation actions с конкретными командами**: «rollback: `kubectl rollout undo deployment/X`», «scale up: `kubectl scale --replicas=10`». **(7) Signs of recovery**: какие метрики должны вернуться к baseline. **(8) Эскалация**: «если не решено за 15 минут → @team-lead, @sre-on-call». **(9) Post-incident**: ссылка на post-mortem template. **Runbook Automation** (продвинутый уровень): PagerDuty Runbook Automation, AWS SSM — кнопка выполняет диагностические шаги; ChatOps в Slack с slash-команд «/runbook orders-error-rate».
>
> **Пример:**
> ```markdown
> # Runbook: OrderService High Error Rate
> **Severity:** P1 | **Алерт:** OrderServiceErrorRateHigh | **Owner:** @team-orders
> **Updated:** 2026-04-15
>
> ## Impact
> Пользователи не могут оформить заказы. Revenue loss ~$1000/min.
>
> ## Диагностика (выполнить последовательно)
>
> ### 1. Проверить дашборд (за 30 сек)
> [Orders Service Dashboard](https://grafana/d/orders?from=now-30m)
> Смотреть: error_rate, p99_latency, request_rate
>
> ### 2. Последние ошибки в логах
> ```bash
> # Kibana
> GET /orders-*/_search?q=level:ERROR&sort=@timestamp:desc&size=50
> ```
>
> ### 3. Зависимости
> - БД: [Postgres Dashboard](https://grafana/d/postgres-orders) — connection pool?
> - Kafka: `kafkactl get cg orders-consumer` — lag?
> - Payment: [Payment Service Health](https://grafana/d/payment) — upstream healthy?
>
> ### 4. Последний deploy
> ```bash
> kubectl rollout history deployment/orders-service -n production
> ```
>
> ## Mitigation
>
> ### Если проблема после deploy → rollback
> ```bash
> kubectl rollout undo deployment/orders-service -n production
> # Подтверждение: error_rate должен упасть < 0.5% за 2 минуты
> ```
>
> ### Если перегружен connection pool
> ```bash
> kubectl set env deployment/orders-service -n production \
>   SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
> ```
>
> ## Signs of Recovery
> - error_rate < 0.1% (5 минут sustained)
> - p99 latency < 300 ms
> - alert ErrorRateHigh = resolved
>
> ## Эскалация
> Не решено за 15 минут → @orders-lead (+7-XXX-XXX), @sre-on-call
>
> ## Post-Incident
> Создать post-mortem: [Template](https://confluence/pm-template)
> ```
>
> **Когда применять:** для **каждого** page-алерта обязательно. Без runbook алерт не должен быть P1/P2.
>
> **Подводные камни:** **Runbook drift** — система меняется, runbook устаревает. Решение: указывать `Updated date`, monthly review, тестирование runbook в GameDays. **Слишком длинный runbook** — никто не читает. Сохранять под 1 экран для диагностики, mitigation — отдельная секция. **Hardcoded URLs/commands** — при миграции (cluster rename, service mesh migration) ломаются ссылки.
>
> **Связанные вопросы:** [[observability-interview#Q30]] — post-mortem; [[observability-interview#Q37]] — alert fatigue; [[observability-interview#Q33]] — maturity model.
>
> ---
>
> #### D) Только текстовое описание архитектуры — это даёт контекст для диагностики — ❌ Неверно
>
> **Что на самом деле:** Архитектурное описание полезно для **онбординга**, но не для **on-call response**. Дежурный в 3 ночи не будет читать архитектурный документ — ему нужны actionable шаги. Архитектура должна быть в отдельной wiki, runbook ссылается на неё опционально.
>
> **Откуда путаница:** разработчики любят писать про архитектуру; runbook воспринимают как «расширенная архитектура».
>
> **Если бы это было правдой:** runbook был бы 10+ страниц теории — никто не читает в момент инцидента.
>
> ---

## Q39. FinOps и Observability: стоимость телеметрии и sampling для снижения затрат?

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
>
> **Вопрос:** Observability bill вырос до $50k/мес и FinOps требует сокращения на 50%. Какой подход сохранит диагностическую ценность данных при максимальном сокращении затрат?
>
> ---
>
> #### A) Снизить retention всех данных до 7 дней — простое и предсказуемое решение — ❌ Неверно
>
> **Что на самом деле:** Равномерное снижение retention теряет ценность непропорционально стоимости. Многие use-cases требуют 30+ дней (weekly trends, recurring incidents, compliance). Также cost driver не всегда retention — часто это **ingest** (storage cheap, processing expensive). Нужен **selective** подход, не blanket cuts.
>
> **Откуда путаница:** retention — самый видимый параметр (один slider), но снижение даёт линейную экономию с нелинейной потерей ценности.
>
> **Если бы это было правдой:** через месяц инцидентов «у нас нет данных за прошлый понедельник» — recurring patterns пропадают.
>
> ---
>
> #### B) Перейти на head-based sampling 1% всех трейсов — резко снижает trace cost — ❌ Неверно
>
> **Что на самом деле:** Head-based 1% теряет 99% данных равномерно — включая редкие ошибки. При error rate 0.1% и 1% sampling реально сохранится 0.001% запросов — статистики на расследование почти нет. Правильный подход: **tail-based** с 100% ошибок и медленных + probabilistic 5% остальных = ~5–10% общий sampling при сохранении 100% интересных кейсов.
>
> **Откуда путаница:** head-based — самый простой sampler (SDK config), tail-based требует OTel Collector setup.
>
> **Если бы это было правдой:** при инциденте «p99 = 5s» в Tempo находим 2 случайных трейса вместо паттерна — root cause не локализуется.
>
> ---
>
> #### C) Cost analytics per service → tail-sampling (100% errors + 10% baseline) + cardinality cleanup + log level optimization (drop DEBUG) + tiered retention (hot/warm/cold) + drop high-volume low-value events (health checks) + per-service spending caps — ✓ Верно
>
> **Развёрнутое объяснение:** Системный FinOps approach для observability: **(1) Cost analytics per service** — dashboard «$/month per service» — без этого оптимизация не приоритизируется. Top-3 сервиса обычно дают 70% bill. **(2) Tail-sampling для трейсов** (см. Q26) — 100% ошибок + 100% медленных + 10% baseline = общий sampling 5–15% при сохранении 95% диагностической ценности. **(3) Cardinality cleanup** — найти high-cardinality metrics через `topk(20, count by (__name__)({__name__=~".+"}))`, удалить unused labels. Часто 5–10% метрик дают 80% time series. **(4) Log level optimization**: drop DEBUG в production через `OTel Collector filter processor`; sampling редких DEBUG для специфичной диагностики. **(5) Tiered retention**: hot 3–7d (SSD), warm 30d (HDD/object storage), cold 90–365d (S3 Glacier). **(6) Drop high-volume low-value events**: health checks (`/actuator/health`, `/healthz`), Kubernetes liveness probes — drop на agent уровне. **(7) Per-service spending caps** в budget alerting — алерт на «service X превысил $X/мес». **(8) Metrics vs traces tradeoff**: для SLO достаточно агрегированных метрик, трейсы — только для диагностики.
>
> **Пример:**
> ```yaml
> # OTel Collector — comprehensive cost optimization
> processors:
>   # 1. Drop health checks (high volume, low value)
>   filter/healthchecks:
>     spans:
>       exclude:
>         match_type: regexp
>         attributes:
>           - {key: http.target, value: '^(/healthz|/actuator/health|/metrics)$'}
>
>   # 2. Tail sampling — keep all errors + slow + 10% baseline
>   tail_sampling:
>     decision_wait: 10s
>     num_traces: 100000
>     policies:
>       - {name: errors, type: status_code, status_code: {status_codes: [ERROR]}}
>       - {name: slow, type: latency, latency: {threshold_ms: 1000}}
>       - {name: probabilistic, type: probabilistic, probabilistic: {sampling_percentage: 10}}
>
>   # 3. Drop high-cardinality labels from metrics
>   transform/cardinality:
>     metric_statements:
>       - context: datapoint
>         statements:
>           - delete_key(attributes, "user_id")
>           - delete_key(attributes, "request_id")
>
>   # 4. Log severity filter — drop DEBUG in production
>   filter/loglevels:
>     logs:
>       exclude:
>         match_type: strict
>         severity_text: ["DEBUG", "TRACE"]
>
> # Loki — tiered retention
> limits_config:
>   retention_period: 168h    # 7 days hot
> compactor:
>   retention_enabled: true
> # Lifecycle policy в S3 — переход в Glacier через 30 дней
> ```
>
> **Когда применять:** обязательно при observability bill > 5–10% от total infra cost, при росте на 30%+ MoM, при FinOps audit. Регулярный review — раз в квартал.
>
> **Подводные камни:** **Drop health checks** может сломать uptime monitoring, если он строится на этих логах — нужны отдельные synthetic probes. **Per-service caps** при exceeded → drop telemetry → инцидент не диагностируется. Caps должны быть с alerting, не hard stop. **Cardinality cleanup** требует понимания, какие labels используются в queries — нужен grep по Grafana dashboards перед удалением.
>
> **Связанные вопросы:** [[observability-interview#Q26]] — sampling детально; [[observability-interview#Q27]] — управление стоимостью логов; [[observability-interview#Q32]] — anti-patterns (cardinality).
>
> ---
>
> #### D) Перейти на полностью managed-решение (Datadog) — оно дешевле open-source — ❌ Неверно
>
> **Что на самом деле:** Managed-решения **дороже** self-hosted при том же volume (Datadog Logs $1.27/GB ingest vs Loki ~$0.10/GB). Managed выгоден когда нет SRE-ресурсов на поддержку self-hosted, но не «дешевле» по чистой стоимости.
>
> **Откуда путаница:** managed экономит operational time (FTE × salary), что иногда забывают учесть. Но monetary cost обычно выше.
>
> **Если бы это было правдой:** Netflix, Uber, Shopify использовали бы Datadog — но они инвестируют в свои observability-платформы именно для cost control.
>
> ---

## Q40. Synthetic Monitoring: что это и когда нужно?

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
>
> **Вопрос:** Когда synthetic monitoring критично необходимо в дополнение к Real User Monitoring (RUM) и стандартным метрикам?
>
> ---
>
> #### A) Synthetic monitoring заменяет RUM — оно «лучше», потому что контролируемо — ❌ Неверно
>
> **Что на самом деле:** Synthetic и RUM — **complementary**, не альтернативы. Synthetic — это **proactive** мониторинг с предсказуемым сценарием, RUM — **reactive** с реальным поведением. Synthetic не видит реальных проблем пользователей (specific data, browsers, regions), RUM не видит проблем в low-traffic окнах (3 AM). Полная observability требует обоих.
>
> **Откуда путаница:** маркетинг synthetic-инструментов (Datadog Synthetics) иногда подаёт его как «полную замену».
>
> **Если бы это было правдой:** синтетический сценарий «login + checkout» работает, но реальный пользователь с конкретным promo code получает ошибку — RUM это видит, synthetic — нет.
>
> ---
>
> #### B) Synthetic — это только uptime monitoring (ping/HTTP check), ничего больше — ❌ Неверно
>
> **Что на самом деле:** Современный synthetic monitoring включает: **(1) Uptime checks** (Blackbox Exporter, Pingdom), **(2) Multi-step transactions** (Playwright/Selenium scenarios — checkout flow), **(3) API monitoring** (POST /payment with valid payload), **(4) Multi-region checks** (из 5–10 географических локаций для CDN/DNS issues), **(5) Performance baseline** (page load time с разных регионов).
>
> **Откуда путаница:** простейшие use-cases — uptime, отсюда упрощение восприятия.
>
> **Если бы это было правдой:** не существовало бы Checkly, Datadog Synthetics с их сложным scenario language.
>
> ---
>
> #### C) Critical user journeys monitoring 24/7 (включая low-traffic окна) + multi-region проверки + API contract testing + SLO baseline в staging — обязательно для critical user-facing services — ✓ Верно
>
> **Развёрнутое объяснение:** Synthetic monitoring критично в нескольких сценариях. **(1) 24/7 uptime для low-traffic окон**: B2B сервис в 3 AM не имеет пользователей, но должен работать; RUM ничего не покажет, synthetic — да. **(2) Critical user journeys**: login → search → checkout — каждые 5 минут синтетический сценарий проходит весь flow; деградация любого шага видна сразу. **(3) Multi-region monitoring**: запуск из 10+ географических точек обнаруживает CDN/DNS issues — «работает в Москве, не работает в Лондоне». **(4) API contract monitoring**: synthetic POST с реальным payload проверяет не только статус, но и response schema (breaking changes detection). **(5) SLO baseline в staging/canary**: synthetic нагрузка проверяет, что новая версия соответствует SLO, прежде чем получит реальный трафик. **(6) Pre-incident detection**: synthetic быстрее обнаруживает problem (60s intervals), чем накапливающаяся ошибка в RUM (нужны минуты для статистической значимости).
>
> **Пример:**
> ```javascript
> // Playwright — synthetic critical user journey
> // Запуск каждые 5 минут из 5 регионов через Grafana Synthetic Monitoring
> import { test, expect } from '@playwright/test';
>
> test('checkout journey', async ({ page }) => {
>   // 1. Login
>   await page.goto('https://example.com/login');
>   await page.fill('#email', 'synthetic-user@example.com');
>   await page.fill('#password', process.env.SYNTHETIC_PWD);
>   await page.click('button[type="submit"]');
>   await expect(page).toHaveURL('/dashboard');
>
>   // 2. Add to cart
>   await page.goto('/products/SKU-12345');
>   await page.click('#add-to-cart');
>   await expect(page.locator('.cart-count')).toHaveText('1');
>
>   // 3. Checkout
>   await page.goto('/checkout');
>   await page.click('#proceed-payment');
>   await expect(page).toHaveURL('/confirmation', { timeout: 30000 });
>
>   // 4. Performance assertion
>   const metrics = await page.evaluate(() => JSON.stringify(performance.timing));
>   const parsed = JSON.parse(metrics);
>   expect(parsed.loadEventEnd - parsed.navigationStart).toBeLessThan(3000);
> });
> ```
>
> ```yaml
> # Blackbox Exporter (Prometheus) — простой HTTP probe
> modules:
>   http_2xx:
>     prober: http
>     timeout: 5s
>     http:
>       valid_status_codes: [200]
>       fail_if_not_ssl: true
>       fail_if_body_not_matches_regexp: ['"status":"ok"']
> ```
>
> **Когда применять:** обязательно для **user-facing critical paths** (login, checkout, payment), **SLA-bound services** (B2B с финансовыми штрафами за downtime), **public APIs** с contract guarantees. Минимум для production — HTTP health check каждые 60 секунд с P1 alert.
>
> **Подводные камни:** **Synthetic scripts maintenance** — при изменении UI/API скрипты ломаются (false alerts). Решение: synthetic scripts — это код, нужен code review и обновление при UI changes. **Cost** — multi-region synthetic за 100+ check/min может стоить $100–500/мес. **False positives от CDN/network jitter** — нужен `for: 2m` в alert и multi-region quorum (3 из 5). **Synthetic users в production** — нужно фильтровать в analytics, чтобы не искажать business metrics.
>
> **Связанные вопросы:** [[observability-interview#Q23]] — SLO/SLI; [[observability-interview#Q31]] — CI/CD observability; [[observability-interview#Q37]] — alert fatigue.
>
> ---
>
> #### D) Synthetic monitoring нужен только для веб-сайтов с пользовательским UI — ❌ Неверно
>
> **Что на самом деле:** Synthetic применим к **любым** сервисам: REST APIs, gRPC, Kafka producers/consumers (synthetic publish/subscribe), даже database connectivity. Веб-UI — самый известный случай, но не единственный.
>
> **Откуда путаница:** Playwright/Selenium ассоциируются с UI testing, отсюда восприятие «synthetic = web».
>
> **Если бы это было правдой:** API-only сервисы (backend microservices) не имели бы synthetic monitoring — но они активно его используют через API probes.
>
> ---

- [ELK Stack](elk-stack-interview.md)
- [Jaeger и Zipkin](jaeger-zipkin-interview.md)
- [Стратегии логирования](logging-strategies-interview.md)
- [Loki и Grafana](loki-grafana-interview.md)
- [Метрики и трейсинг](metrics-tracing-interview.md)
- [OpenTelemetry](opentelemetry-interview.md)
