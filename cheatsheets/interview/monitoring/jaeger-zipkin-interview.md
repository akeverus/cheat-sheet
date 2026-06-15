---
title: "Вопросы на собеседовании: Jaeger и Zipkin"
description: "Distributed tracing backends: Jaeger (CNCF), Zipkin (Twitter), архитектура (Agent, Collector, Storage), storage backends (Cassandra, Elasticsearch), UI, sampling, OTel migration"
tags:
  - interview
  - monitoring
  - jaeger-zipkin-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Jaeger и Zipkin"
  - "Jaeger interview"
  - "Zipkin interview"
prerequisites:
  - "[[jaeger]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Jaeger и Zipkin`

**Jaeger** (CNCF, от Uber) и **Zipkin** (от Twitter) — основные open-source бэкенды для distributed tracing. Принимают spans, хранят, визуализируют. С появлением **OpenTelemetry** оба эволюционировали к OTLP. Альтернативы: **Tempo** (Grafana), **SigNoz**, vendor SaaS (Datadog, Honeycomb).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [Zipkin Documentation](https://zipkin.io/)
- [Tempo Documentation (Grafana)](https://grafana.com/docs/tempo/)
- [Distributed Tracing in Practice (book)](https://www.oreilly.com/library/view/distributed-tracing-in/9781492056621/)
- [SigNoz Documentation](https://signoz.io/docs/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое distributed tracing?](#q1--что-такое-distributed-tracing)
- [Q2. (!) Span, trace и context — как они связаны?](#q2--span-trace-и-context--как-они-связаны)
- [Q3. Зачем нужен tracing backend?](#q3-зачем-нужен-tracing-backend)

**Jaeger**
- [Q4. (!) Что такое Jaeger?](#q4--что-такое-jaeger)
- [Q5. (!) Из каких компонентов состоит архитектура Jaeger (Agent, Collector, Query, UI)?](#q5--из-каких-компонентов-состоит-архитектура-jaeger-agent-collector-query-ui)
- [Q6. (!) Какие storage backends поддерживает Jaeger (Cassandra, Elasticsearch, Kafka)?](#q6--какие-storage-backends-поддерживает-jaeger-cassandra-elasticsearch-kafka)
- [Q7. Что нового в Jaeger v2 (на базе OTel)?](#q7-что-нового-в-jaeger-v2-на-базе-otel)

**Zipkin**
- [Q8. (!) Что такое Zipkin?](#q8--что-такое-zipkin)
- [Q9. Как устроена архитектура Zipkin?](#q9-как-устроена-архитектура-zipkin)
- [Q10. (!) Чем Jaeger отличается от Zipkin?](#q10--чем-jaeger-отличается-от-zipkin)

**Storage и scaling**
- [Q11. (!) Почему трейсинг такой дорогой по хранилищу?](#q11--почему-трейсинг-такой-дорогой-по-хранилищу)
- [Q12. Какие бывают стратегии sampling?](#q12-какие-бывают-стратегии-sampling)
- [Q13. Как выбирают retention policy для трейсов?](#q13-как-выбирают-retention-policy-для-трейсов)

**UI и querying**
- [Q14. (!) Какие views есть в Jaeger UI?](#q14--какие-views-есть-в-jaeger-ui)
- [Q15. Что такое service map (карта сервисов)?](#q15-что-такое-service-map-карта-сервисов)
- [Q16. Для чего нужен comparison view (сравнение трейсов)?](#q16-для-чего-нужен-comparison-view-сравнение-трейсов)

**Альтернативы**
- [Q17. (!) Что такое Grafana Tempo и чем он отличается от Jaeger?](#q17--что-такое-grafana-tempo-и-чем-он-отличается-от-jaeger)
- [Q18. Что представляют собой SigNoz, Aspecto, Lightstep?](#q18-что-представляют-собой-signoz-aspecto-lightstep)
- [Q19. Какие есть Cloud SaaS для трейсинга (Datadog APM, New Relic, Honeycomb)?](#q19-какие-есть-cloud-saas-для-трейсинга-datadog-apm-new-relic-honeycomb)

**Migration / OpenTelemetry**
- [Q20. (!) Как мигрировать с Jaeger client SDK на OpenTelemetry?](#q20--как-мигрировать-с-jaeger-client-sdk-на-opentelemetry)
- [Q21. Можно ли отправлять OTLP в Jaeger?](#q21-можно-ли-отправлять-otlp-в-jaeger)

**Production**
- [Q22. (!) Как выбрать tracing backend под проект?](#q22--как-выбрать-tracing-backend-под-проект)
- [Q23. Какие частые проблемы возникают в эксплуатации трейсинга?](#q23-какие-частые-проблемы-возникают-в-эксплуатации-трейсинга)

## Q1. (!) Что такое distributed tracing?

**Distributed tracing** прослеживает один запрос по всему его пути через множество сервисов и показывает, где он провёл время и где упал. В монолите достаточно одного stacktrace; в микросервисах запрос проходит через десятки процессов, и без сквозной трассировки понять «кто виноват в медленном ответе» практически невозможно.

```
User → API Gateway → Service A → Service B → Database
                  → Service C → Cache
```

Механика проста: каждый шаг обработки = **span**, а все spans одного запроса, связанные общим идентификатором, = **trace**. Сервисы передают этот идентификатор друг другу в заголовках, поэтому backend может собрать единую картину из фрагментов, пришедших с разных машин.

**Какие задачи решает:**
- **Где bottleneck** — какой именно сервис или endpoint тормозит весь запрос.
- **Где запрос упал** — на каком шаге произошла ошибка и с каким статусом.
- **Карта зависимостей** — кто кого реально вызывает (часто не совпадает с тем, что нарисовано в документации).
- **Планирование ёмкости** (capacity planning) — на какие сервисы приходится нагрузка.
- **Разбивка latency** — сколько миллисекунд съел каждый сервис в общем времени ответа.

## Q2. (!) Span, trace и context — как они связаны?

Три базовых понятия трассировки, выстроенные иерархически: **trace** состоит из **spans**, а **context** — это то, что связывает их между процессами.

- **Trace** — полная история одного запроса. Объединяет все spans, связанные общим `trace_id`.
- **Span** — одна операция внутри запроса: HTTP-вызов, обращение к БД, выполнение функции. У span есть родитель (`parent_span_id`), поэтому из плоского списка spans backend восстанавливает дерево.
- **Context** — набор идентификаторов (`trace_id`, `span_id`, `parent_span_id`) плюс решение о sampling. Именно context передаётся между сервисами в заголовках (propagation) и обеспечивает связность trace при переходе через границу процесса.

```
Trace abc123
├── Span 1 (root): GET /orders/123 (200ms total)
│   ├── Span 2: SELECT FROM orders (15ms)
│   ├── Span 3: SELECT FROM users (10ms)
│   └── Span 4: POST /payment (170ms)
│       └── Span 5: SELECT FROM accounts (5ms)
```

Из примера сразу видно, что 170 из 200 мс ушло на вызов платежей — так дерево spans локализует узкое место. Каждый span несёт **timestamps, duration, attributes, events и status**, которые и наполняют trace деталями.

Подробнее — в [OpenTelemetry](opentelemetry-interview.md).

## Q3. Зачем нужен tracing backend?

Приложения только **генерируют** spans, но не хранят их и не умеют собирать в trace. Backend — это та инфраструктура, которая принимает поток spans со всех сервисов, складывает в долговременное хранилище и даёт инструменты для поиска и визуализации.

**Что делает tracing backend:**
- **Принимает** spans по сети — через OTLP, Jaeger Thrift, Zipkin HTTP.
- **Индексирует** их для быстрого поиска: по `trace_id`, сервису, времени, атрибутам.
- **Хранит** в базе данных (Cassandra, Elasticsearch, ClickHouse), а не в памяти процесса.
- Предоставляет **Query API** для выборки trace по идентификатору или фильтру.
- Даёт **UI** для визуализации дерева spans и карты сервисов.

**Почему без backend никак:** spans, оставленные в памяти приложения, теряются при рестарте, не собираются вместе с других машин и недоступны для поиска. Backend превращает разрозненные фрагменты в единую запрашиваемую картину.

## Q4. (!) Что такое Jaeger?

**Jaeger** — open-source платформа для distributed tracing, созданная в **Uber** (2017) и достигшая статуса **CNCF graduated** в 2019. Сегодня это де-факто стандарт self-hosted трассировки в облачно-нативном мире.

**Что важно знать:**
- Архитектурно вдохновлён статьёй Google Dapper — той же, что породила всю индустрию distributed tracing.
- Production-ready: масштабируется до **миллионов spans/sec**, проверен на нагрузке Uber.
- Не привязан к одному хранилищу — можно выбрать storage-бэкенд под свои требования (Cassandra, Elasticsearch, ClickHouse).
- Богатый UI с картами сервисов и сравнением trace.
- Встроенная поддержка **OpenTelemetry** — принимает OTLP нативно.

**Когда применять:** distributed tracing для микросервисов, отладка latency (где теряются миллисекунды) и анализ реальных зависимостей между сервисами.

## Q5. (!) Из каких компонентов состоит архитектура Jaeger (Agent, Collector, Query, UI)?

Поток данных линейный: приложение отдаёт spans → они доходят до **Collector** → пишутся в **Storage** → **Query** читает их обратно → **UI** отрисовывает. Каждый компонент отвечает за свой участок этого конвейера.

Конвейер по порядку:

- `App + SDK` → по **UDP** → `Jaeger Agent`
- `Jaeger Agent` → по **gRPC** → `Jaeger Collector`
- `Jaeger Collector` → `Storage` (Cassandra / ES)
- `Storage` → `Jaeger Query`
- `Jaeger Query` → `Jaeger UI`

**Компоненты:**

**Jaeger Agent** (deprecated в Jaeger v2) — sidecar/DaemonSet рядом с приложением. Принимает spans локально через UDP (минимальный overhead для приложения — отправить и забыть) и буферизует пересылку в Collector. Смысл был в том, чтобы снять с приложения заботу о доступности Collector.

**Jaeger Collector** — точка сбора. Принимает spans по gRPC, HTTP и OTLP, валидирует и обрабатывает их (в том числе adaptive sampling), после чего пишет в хранилище. Именно сюда стекаются данные со всех сервисов.

**Jaeger Query** — сервис чтения. Достаёт trace из хранилища и отдаёт их через REST/gRPC API.

**Jaeger UI** — веб-интерфейс поверх Query: поиск, просмотр дерева trace, карта сервисов.

**Что изменилось в Jaeger v2 (2024+):** Agent объявлен **deprecated**. Приложения (через OTel SDK или Collector-агент) отправляют spans напрямую в Collector по OTLP — отдельный слой Agent больше не нужен, конвейер стал короче.

## Q6. (!) Какие storage backends поддерживает Jaeger (Cassandra, Elasticsearch, Kafka)?

Jaeger не имеет собственной базы — хранилище выбирается под нагрузку и бюджет. Главный компромисс: write-throughput против гибкости поиска. Cassandra тянет огромный поток записи, но плохо ищет по атрибутам; Elasticsearch ищет богато, но прожорлив.

| Backend | Плюсы | Минусы |
|---------|------|------|
| **Cassandra** | Высокий write-throughput, масштабируется | Сложная эксплуатация |
| **Elasticsearch** | Богатые возможности запросов | Прожорлив по памяти, дорогой |
| **OpenSearch** | То же, что и ES | То же, что и ES |
| **Kafka** | Буфер между Collector и хранилищем | Не постоянное хранилище |
| **ClickHouse** (Jaeger v2) | Быстрый, выгодный по цене | Интеграция новее |
| **Memory** | Быстрый старт, без настройки | Теряется при рестарте, только для dev |

Важный нюанс: **Kafka здесь не финальное хранилище**, а буфер между Collector и базой — он сглаживает всплески записи и развязывает приём spans от их персистентности.

**Что выбрать:** в production у большинства это Cassandra или Elasticsearch. **В 2025** заметно растёт adoption **ClickHouse** — он быстрее и дешевле для тех же объёмов, поэтому в Jaeger v2 стал первоклассной опцией.

## Q7. Что нового в Jaeger v2 (на базе OTel)?

**Jaeger v2** (2024) — это переписанный с нуля Jaeger поверх **OpenTelemetry Collector**. Главная идея релиза: перестать поддерживать собственный конвейер сбора и переиспользовать стандартный движок OTel, к которому уже подключается вся экосистема.

**Что это даёт на практике:**
- Ядро построено на фреймворке **OTel Collector** — Jaeger теперь по сути специализированная сборка Collector.
- **Agent удалён** (deprecated) — конвейер сбора стал короче.
- **OTLP — нативный протокол**, а не один из принимаемых форматов: данные не конвертируются.
- Расширяемость как у OTel Collector: новые **receivers, processors, exporters** добавляются конфигом, а не патчем кода.
- Поддержка **ClickHouse** как хранилища с лучшим соотношением цена/производительность.

**Статус:** в 2025 Jaeger v2 — рекомендуемая версия, v1 переведён в maintenance mode.

## Q8. (!) Что такое Zipkin?

**Zipkin** — open-source система трассировки, созданная в **Twitter** (2012) и ставшая одной из первых широко используемых tracing-систем. Исторически именно Zipkin сформировал многие практики, которые потом подхватили остальные.

**Что выделяет Zipkin:**
- **Простота установки** — это один JAR, который можно запустить за минуту (минимум движущихся частей).
- Инструментирование исторически строилось вокруг **HTTP**.
- **B3 propagation headers** — формат передачи context, появившийся задолго до стандарта W3C Trace Context (его до сих пор встречаешь в legacy-системах).

**Статус в 2025:** разработка идёт менее активно, чем у Jaeger, многие проекты мигрировали на Jaeger / OTel. Zipkin остаётся актуальным в основном для уже существующих установок.

## Q9. Как устроена архитектура Zipkin?

Архитектура Zipkin намеренно проще, чем у Jaeger: один монолитный сервер вместо набора компонентов.

```
App (Brave / Zipkin libs) → HTTP/Kafka → Zipkin Server → Storage
                                                              ↓
                                                            UI
```

**В чём отличие от Jaeger:**
- **Один Zipkin Server** объединяет приём, запись и чтение spans — в Jaeger эти роли разнесены по отдельным Collector и Query. Меньше компонентов = проще эксплуатировать, но хуже масштабируется независимо.
- **Хранилище:** in-memory по умолчанию (удобно для старта, но теряется при рестарте), а для production — MySQL, Cassandra или Elasticsearch.

**Brave** — основная Java-библиотека для инструментирования приложений под Zipkin.

## Q10. (!) Чем Jaeger отличается от Zipkin?

Коротко: Jaeger — более молодой, активно развивающийся CNCF-проект с современным UI и встроенной картой сервисов; Zipkin — проверенный временем минималист в режиме поддержки. Для нового проекта выбирают Jaeger, Zipkin держат ради legacy.

| Критерий | Jaeger | Zipkin |
|----------|--------|--------|
| Возраст | 2017 | 2012 |
| Создатель | Uber | Twitter |
| CNCF | Graduated | Нет |
| Активность | Активный | Maintenance |
| Хранилище | Cassandra, ES, ClickHouse | MySQL, Cassandra, ES |
| Протокол | gRPC, HTTP, OTLP | HTTP (B3) |
| UI | Современный, удобнее | Проще |
| Карта сервисов | Встроенная | Через DependencyLinker |
| Adoption | Выше (2025) | Ниже |

**В 2025** для новых проектов — **Jaeger** или **OTel + Tempo / SigNoz**. Zipkin — для legacy.

## Q11. (!) Почему трейсинг такой дорогой по хранилищу?

Проблема в умножении объёмов: один запрос порождает не одну запись, а целое дерево spans, и каждый span ещё и индексируется по `trace_id`, сервису, времени и атрибутам. Поэтому объём растёт не линейно по запросам, а кратно числу spans на запрос.

Простой расчёт показывает масштаб:

**1000 RPS × 10 spans/запрос × 1 KB/span = 10 MB/сек = 864 GB/день.**

Почти терабайт в сутки — и это с одного скромного сервиса. Хранить такие объёмы в Cassandra или Elasticsearch месяцами означает терабайты данных и счета на большие суммы.

**Как снижают расходы:**
- **Sampling** (head + tail) — главный рычаг, сокращает объём на ~99%.
- **Короткий retention** (7–30 дней вместо «хранить вечно»).
- **Сжатие** spans.
- **ClickHouse** вместо ES — примерно в 10 раз дешевле для тех же данных.
- **Tail sampling** — оставлять важное (ошибки, медленные запросы) и отбрасывать рутинные.

Поэтому **в 2025** почти все системы сэмплируют, сохраняя лишь **1–10%** trace.

## Q12. Какие бывают стратегии sampling?

Ключевое различие — **где** принимается решение оставить trace. Head sampling решает в начале запроса (дёшево, но вслепую), tail sampling — после того как trace целиком собран (умнее, но дороже).

**Head sampling** — решение в приложении, в самом начале trace:
- **Probabilistic** — случайные `1%` запросов.
- **Rate limiting** — не более 100 trace/сек, защита от всплесков.
- **Adaptive** — частота сэмплирования подстраивается под трафик.

Плюс: дёшево, ничего не нужно держать в памяти. Минус: решаем «вслепую» — можем выбросить именно тот trace, где была ошибка, ведь о ней мы узнаём только в конце.

**Tail sampling** — решение в Collector, когда весь trace уже собран:
- **Всегда сохранять ошибки** (status code 5xx).
- **Всегда сохранять медленные** (> 1 сек).
- **Сохранять 5% обычных** запросов.

Плюс: оставляем именно важное. Минус: Collector обязан буферизовать spans до завершения trace — это память и сложность.

**Лучшая практика для production — комбинация:** head-sampling сбивает грубый объём, tail-sampling гарантирует, что ошибки и аномалии не потеряются.

```yaml
# Jaeger Collector adaptive sampling
adaptive_sampling:
  default_strategy:
    probabilistic_sampling:
      sampling_rate: 0.01  # 1% baseline
  per_service_strategies:
    - service: critical-service
      probabilistic_sampling:
        sampling_rate: 0.1  # 10% для critical
```

## Q13. Как выбирают retention policy для трейсов?

Retention — это компромисс между «как глубоко в прошлое надо смотреть при разборе инцидента» и стоимостью хранения. Трейсы нужны в основном для отладки недавних проблем, поэтому держать их вечно бессмысленно.

**Ориентиры:**
- **Типичный retention:** 7–30 дней.
- **Короче:** dev-окружения (1–3 дня).
- **Дольше:** требования комплаенса (90 дней и больше).

**Tiered storage для баланса цены и доступности:**
- **Hot storage** — свежие данные, быстро доступны для запросов: ~7 дней.
- **Cold storage** — архив, запросы дорогие и медленные: 30+ дней.

Чистить старое вручную не нужно: **Cassandra TTL** и **Elasticsearch ILM** (Index Lifecycle Management) автоматически удаляют данные за пределами retention.

## Q14. (!) Какие views есть в Jaeger UI?

Jaeger UI закрывает весь цикл расследования: от «найти подозрительный запрос» до «увидеть всю систему целиком». Каждый view решает свою задачу.

**Search** — найти нужный trace: фильтры по сервису, операции, тегам, временному диапазону и минимальной длительности (так ловят медленные запросы), плюс лимит на количество trace за период.

**Trace view** — разобрать один запрос по шагам: timeline всех spans, их иерархия parent-child и детали каждого span (attributes, logs, errors). Это основной экран, где локализуют bottleneck.

**Trace comparison** — положить два trace рядом и подсветить различия (например, медленный против быстрого).

**System architecture** — карта зависимостей между сервисами, автоматически построенная из реальных trace.

**Monitor (новое)** — RED-метрики по сервисам (request rate, error rate, p95 latency) прямо из трейсов, без отдельного Prometheus.

## Q15. Что такое service map (карта сервисов)?

**Service dependency graph** — граф зависимостей между сервисами, который backend строит автоматически из реальных trace, а не из конфигов или документации. Поэтому он показывает, как система устроена на самом деле, а не как её задумывали.

Пример такого графа зависимостей:

- `Frontend` → `APIGW`
- `APIGW` → `UserService`
- `APIGW` → `OrderService`
- `OrderService` → `PaymentService`
- `OrderService` → `Database`
- `UserService` → `Database`
- `UserService` → `Redis`

**Зачем нужна:**
- **Визуализация архитектуры** — увидеть систему целиком на одном экране.
- **Неожиданные зависимости** — обнаружить связи, о которых никто не знал (сервис ходит туда, куда не должен).
- **Hot paths** — выявить наиболее нагруженные пути запросов.
- **Циклы** — найти кольцевые зависимости между сервисами.

Jaeger строит карту автоматически; так же делают Datadog и Honeycomb.

## Q16. Для чего нужен comparison view (сравнение трейсов)?

Этот view кладёт два trace рядом и подсвечивает, чем они отличаются. Типичный приём отладки производительности — взять медленный запрос и обычный и посмотреть, что пошло не так именно в медленном.

**Что показывает сравнение:**
- **Различия в структуре spans** — где в медленном trace появились лишние или более долгие шаги.
- **Сравнение latency** — какой именно span раздулся по времени.
- **Поиск регрессии** — сопоставить trace до и после релиза, чтобы поймать деградацию.

## Q17. (!) Что такое Grafana Tempo и чем он отличается от Jaeger?

**Grafana Tempo** — tracing-бэкенд от Grafana и серьёзный конкурент Jaeger. Его ключевая идея — радикально удешевить хранение трейсов, пожертвовав богатым поиском.

**Как устроен:**
- **Хранилище — object storage** (S3, GCS, Azure Blob), а не Cassandra/ES. Это **на порядки дешевле** и идеально масштабируется под миллиарды spans.
- **Индексируется только `trace_id`** — это плата за дешевизну. Поиска по атрибутам «из коробки», как в Jaeger, нет.
- OTel native, встраивается в **связку Loki + Tempo + Mimir + Grafana** (логи + трейсы + метрики в одном стеке).

**Компромисс:**
- **Плюсы:** очень дёшево и масштабируемо.
- **Минусы:** ограниченный поиск — нужно знать `trace_id`, а не искать по условиям.

Из ограничения вытекает рабочий процесс: в логах (Loki) находишь запись с `trace_id` → по нему подтягиваешь полный trace в Tempo. С 2023 года **TraceQL** добавил язык запросов и частично снял проблему «нет поиска».

В **2025** Tempo популярен именно у тех, кто уже живёт в экосистеме Grafana.

## Q18. Что представляют собой SigNoz, Aspecto, Lightstep?

Это альтернативы, выросшие вокруг OpenTelemetry. Объединяет их ставка на открытый стандарт, а различаются они моделью (open-source self-hosted vs managed) и охватом сигналов.

- **SigNoz** — open-source полноценный APM: traces + metrics + logs в одном инструменте, поверх ClickHouse. По сути self-hosted альтернатива Datadog, набирает популярность у тех, кто не хочет платить вендору.
- **Aspecto** — managed-платформа поверх OTel, ориентированная на разработчиков и их workflow.
- **Lightstep** (куплен ServiceNow в 2021) — трассировка уровня enterprise.
- **Honeycomb** — пионер подхода «wide events» с мощным языком запросов. Это другая парадигма наблюдаемости, чем классический APM: вместо предагрегированных метрик — исследование сырых событий с высокой кардинальностью.

## Q19. Какие есть Cloud SaaS для трейсинга (Datadog APM, New Relic, Honeycomb)?

SaaS-вендоры избавляют от эксплуатации backend, но взамен берут деньги пропорционально объёму данных — поэтому выбор почти всегда сводится к балансу «удобство против цены на масштабе».

| Vendor | Плюсы | Минусы |
|--------|------|------|
| **Datadog** | Полный APM, интеграции, UI | **Дорого** (~$30/host) |
| **New Relic** | Оплата за объём ingest, проще | Меньше возможностей |
| **Honeycomb** | Лучший UX для исследования данных | Менее отполированный GUI |
| **Splunk APM** | Enterprise, много возможностей | Дорого |
| **AWS X-Ray** | Дёшево, если только AWS | Ограниченный функционал |

**Плюсы SaaS:**
- Нет эксплуатации (ops) — backend держит вендор.
- Отполированный UI «из коробки».
- Авто-корреляция traces ↔ logs ↔ metrics.
- Встроенный алертинг.

**Минусы SaaS:**
- **Очень дорого** на масштабе — счёт растёт с объёмом данных.
- Vendor lock-in (хотя OTel его заметно снизил — инструментирование стало переносимым).
- Данные уходят за пределы вашего периметра.

Отсюда тренд **2025**: переход на **OTel + self-hosted (Tempo, SigNoz)** ради экономии. Распространён и **гибрид** — сэмплированные данные шлют в Datadog ради UX, а полный объём держат в self-hosted.

## Q20. (!) Как мигрировать с Jaeger client SDK на OpenTelemetry?

Суть миграции: вы меняете только то, **чем инструментируете** код (Jaeger SDK → OTel SDK), но **backend остаётся тем же** — Jaeger Collector принимает OTLP, так что переезжать на новое хранилище не нужно. Это и делает миграцию постепенной и безопасной.

**Было** — Jaeger client SDK в коде:

```java
import io.jaegertracing.Tracer;
Tracer tracer = new Configuration("my-service").getTracer();
```

**Стало** — OTel SDK, который шлёт по OTLP в тот же Jaeger Collector:

```java
import io.opentelemetry.api.trace.Tracer;
Tracer tracer = GlobalOpenTelemetry.getTracer("my-service");
```

**Порядок шагов:**
1. Заменить Jaeger client SDK на OTel SDK в приложениях.
2. Настроить OTel exporter на OTLP endpoint.
3. Включить приём OTLP в Jaeger Collector (в v2 это нативно, в v1.35+ — отдельная настройка).
4. Проверить, что trace продолжают собираться и отображаются корректно.

**Совет:** для языков с поддержкой **auto-instrumentation** (например, Java-agent) можно вообще не править код — агент перехватывает библиотеки и заменяет инструментирование Jaeger автоматически.

## Q21. Можно ли отправлять OTLP в Jaeger?

**Да.** Jaeger Collector принимает OTLP по gRPC и HTTP нативно, начиная с **Jaeger v1.35+** — отдельный «мостик» или конвертер не нужен.

```yaml
# Apps export OTLP
exporters:
  otlp:
    endpoint: jaeger-collector:4317

# Jaeger Collector listens на OTLP
```

Разница между версиями: в **v1** OTLP — один из принимаемых форматов (Jaeger конвертирует его в свою модель внутри), а в **Jaeger v2** OTLP — **нативный протокол**, поэтому конвертация и связанный с ней overhead отсутствуют. На практике это значит, что приложения можно инструментировать чистым OTel и не тащить Jaeger-специфичные форматы.

## Q22. (!) Как выбрать tracing backend под проект?

Выбор определяют три вопроса: готовы ли вы платить вендору, в какой экосистеме уже живёте и насколько важен дешёвый масштаб. Дерево ниже сводит их к конкретным рекомендациям.

**Дерево решений:**

```
Бюджет ограничен, want self-host?
  → Jaeger v2 + ClickHouse (best balance)
  → или Tempo + S3 (cheapest)
  → или SigNoz (full APM, single tool)

Already Grafana stack?
  → Tempo + Loki + Mimir + Grafana

Want full APM, ready to pay?
  → Datadog (best UI, most integrations)
  → Honeycomb (best query exploration)

Vendor-lock-in OK?
  → Cloud-native (X-Ray, Cloud Trace, Application Insights)

Legacy Zipkin already?
  → Stay or migrate к Jaeger v2
```

**Если коротко, для новых проектов в 2025:**
- **Self-host:** Jaeger v2 (зрелый, безопасный выбор по умолчанию) или SigNoz (если нужен сразу полный APM в одном инструменте).
- **SaaS:** Datadog (максимально отполированный продукт) или Honeycomb (лучший UX для исследования данных).

## Q23. Какие частые проблемы возникают в эксплуатации трейсинга?

Большинство граблей крутятся вокруг двух тем: объём данных (хранилище и сеть не выдерживают полного потока) и связность трейсов (spans теряются или не сходятся в дерево). Вот типичный список:

1. **Взрывной рост хранилища** — без sampling объём быстро уходит в терабайты.
2. **Медленные запросы** — Jaeger UI тормозит на больших датасетах.
3. **Пропавшие spans** — сломан context propagation, особенно на async-границах (очереди, потоки).
4. **Несогласованные атрибуты** — разные сервисы называют одно и то же по-разному, поиск ломается.
5. **Нет алертинга** — у самого Jaeger его нет, нужен Prometheus + метрики по трейсам.
6. **Hot partitions** в Cassandra — неудачный partition key концентрирует нагрузку на узлах.
7. **Высокая кардинальность** атрибутов убивает производительность хранилища.
8. **Нет tail sampling** в production — в backend льётся слишком много данных.
9. **Забытый retention** — не выставили TTL, старые данные копятся бесконечно.
10. **Сетевая полоса** — отправка всех трейсов без sampling дорого обходится по трафику.

## See also

- [OpenTelemetry](opentelemetry-interview.md) — современный стандарт
- [Loki + Grafana](loki-grafana-interview.md) — для логов
- [ELK Stack](elk-stack-interview.md) — альтернатива для логов
- [Prometheus + Grafana](prometheus-grafana-interview.md) — для метрик
- [Observability](observability-interview.md) — общая концепция
- [Метрики и трейсинг](metrics-tracing-interview.md) — базовые понятия
- [Микросервисы](../architecture/microservices-interview.md) — где трассировка критична
- [Kubernetes](../devops/kubernetes-interview.md) — Jaeger в K8s
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — столп observability
- [Cassandra](../databases/cassandra-interview.md) — хранилище Jaeger
- [Elasticsearch](../databases/elasticsearch-interview.md) — хранилище Jaeger
- [Performance Testing](../performance/performance-testing-interview.md) — найти медленные пути

- [Стратегии логирования](logging-strategies-interview.md)
- [Loki и Grafana](loki-grafana-interview.md)
- [Метрики и трейсинг](metrics-tracing-interview.md)
- [Observability](observability-interview.md)
- [OpenTelemetry](opentelemetry-interview.md)
