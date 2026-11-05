# Metrics: завершение авторской проверки Q29–Q35/Q40

Дата: 2026-11-05. Исполнитель: Codex, текущая сессия с доступными ключами. Прочитаны видимые вопросы, соответствующая теория, все A/B/C/D и sections восьми B0. Это авторская содержательная проверка, не независимый reviewer, Phase A/B или FINAL. Номера, block_idx, question_text, ключи и порядок сохранены: Q29 D, Q30 D, Q31 B, Q32 B, Q33 A, Q34 B, Q35 D, Q40 A.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `988277ca14bbe7eafd2bbbb8d7498a35a7395960e22e3bb8d8f98b0331b9cef3`; baseline `305d2904954697faed321b3c572e8adebdb53089a7f13750cbe09694c46fc393`. Изменены только варианты и sections восьми перечисленных вопросов; остальные 33 вопроса сохранены побайтово. С предыдущими пакетами авторский охват — **41/41**. Полная готовность пары пока **NEEDS_THEORY_SYNC**, независимая приёмка **NOT_RUN**.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`. Начальный прочитанный SHA `c88fd2a66add040377d4b18021e3bc643bbdec15082a56b4313b8570140e6c1a`. Во время подготовки другой автор обновил Q29/Q30/Q32: защитная проверка остановила запись до повторного чтения. Новый прочитанный снимок — `380eff25e9c4b0a2760dea539a51437376f478b7f8c40ce0f22ceaaffb81d3fd`. Секции сверены отдельно; Markdown этой задачей не редактировался.

| Вопрос | Результат содержательной проверки |
|---|---|
| Q29 | Сопоставление бизнес-результатов с техническими сигналами отделено от общего хранилища, персональной аналитики и причинности. SQL не объявлен обязательно медленным; operational counters не заменяют реестр. |
| Q30 | `for` отделён от голосования по точкам, маршруты — от trace ID, sampling bias — от репрезентативной доли ошибок. Receiver/severity являются policy команды; trace не обещает диагноз. |
| Q31 | HTTP propagation отделена от OTLP и хранения. Убраны гарантии отсутствия потерь и W3C default для любого Java SDK. Parent/sampled и необязательный tracestate описаны с ограничениями. |
| Q32 | Регистрация и идентичность meter отделены от изменения состояния Gauge. Теги не ограничены константами, но cardinality контролируют. Ошибки считают по сопоставимым событиям и labels. |
| Q33 | Для Boot 3.3.2 указаны Actuator, bridge и reporter/exporter. YAML и создание отдельного exporter не заменяют работающий tracing pipeline; русский текст заменил английские sections. |
| Q34 | Counter/Gauge и Timer/DistributionSummary различены по семантике. Timer и Summary имеют count; LongTaskTimer наблюдает активные задачи. DistributionSummary не приравнен к Prometheus summary. |
| Q35 | Collector не назван только agent или библиотекой SDK. Компоненты включают в pipelines; memory_limiter не является persistent queue и не гарантирует доставку. Версии exporters явно ограничены. |
| Q40 | Inject/extract отделены от активации и экспорта. Исправлены обещания composite merge и обязательного разрыва async/Kafka. Новый span имеет свой ID; single/multi B3, Boot и Agent не смешиваются. |

Для каждого неверного варианта разобрана предметная ошибка, а не отсутствие имени класса в теории. Вопросы решались с видимыми ключами; вывод об отсутствии второго ответа и поверхностной сопоставимости является авторским, без независимого SFPL/ANSC/RU PASS.

## Первичные источники

Прочитаны 2026-11-05. Версии примеров не распространяются на все будущие релизы.

- [Micrometer meters](https://docs.micrometer.io/micrometer/reference/concepts/meters.html), [counters](https://docs.micrometer.io/micrometer/reference/concepts/counters.html), [gauges](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html), [DistributionSummary](https://docs.micrometer.io/micrometer/reference/concepts/distribution-summaries.html), [LongTaskTimer](https://docs.micrometer.io/micrometer/reference/concepts/long-task-timers.html): роли инструментов, count, наблюдаемое состояние и strong reference.
- [Micrometer histograms/percentiles](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html), [Timer source 1.13.2](https://github.com/micrometer-metrics/micrometer/blob/v1.13.2/micrometer-core/src/main/java/io/micrometer/core/instrument/Timer.java), [Prometheus labels](https://prometheus.io/docs/practices/naming/): boundaries, неагрегируемость клиентских quantiles и расходы от комбинаций labels. Нельзя переносить поведение экспорта latest registry на 1.13.2 без проверки.
- [Prometheus alert rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/), [Alertmanager configuration](https://prometheus.io/docs/alerting/latest/configuration/): for, labels, routes, grouping и отдельные таймеры уведомлений.
- [W3C Trace Context](https://www.w3.org/TR/trace-context/), [Java SDK builder 1.37.0](https://github.com/open-telemetry/opentelemetry-java/blob/v1.37.0/sdk/all/src/main/java/io/opentelemetry/sdk/OpenTelemetrySdkBuilder.java), [SDK autoconfigure](https://opentelemetry.io/docs/languages/java/configuration/): формат, валидность IDs и различие defaults.
- [OTel Propagators API](https://opentelemetry.io/docs/specs/otel/context/api-propagators/), [B3](https://github.com/openzipkin/b3-propagation), [Context API 1.37.0](https://github.com/open-telemetry/opentelemetry-java/blob/v1.37.0/context/src/main/java/io/opentelemetry/context/Context.java), [Boot TracingProperties 3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/tracing/TracingProperties.java), [Java Agent configuration](https://opentelemetry.io/docs/zero-code/java/agent/configuration/): порядок composite, форматы и границы настройки.
- [Boot tracing docs, tag v3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-docs/src/docs/antora/modules/reference/pages/actuator/tracing.adoc): dependencies и auto-configured HTTP-client builders; online линия 3.3 сейчас показывает другой patch release.
- [Collector configuration](https://opentelemetry.io/docs/collector/configuration/), [resiliency](https://opentelemetry.io/docs/collector/resiliency/), [contrib manifest 0.111.0](https://github.com/open-telemetry/opentelemetry-collector-releases/blob/v0.111.0/distributions/otelcol-contrib/manifest.yaml): enablement pipeline, пределы очередей/доставки и состав конкретной сборки. В 0.111.0 jaeger exporter отсутствует, loki exporter ещё присутствует; нельзя объявлять оба отсутствующими во всех версиях.

## Проверки и пределы

`verify-mcq-json`: **PASS 41 Q / 41 B**, semantic review NOT_RUN. Diff whitespace и сохранность 33 неизменённых Q проверены. Anti-tell: **0/41**. SKEL pre-check: **hard=0, warnings=1**, оставшееся предупреждение — двоеточие в настоящем примере `for: 3m` Q30 A. Порогов и ключей не меняли.

Parity остаётся **FAIL**: word ratio max **1.55 Q32**, два блока выше 1.35; correct-longest **5/41**, correct/wrong average **1.008**, style guessability **0.238**, остальные gates PASS. По символам Q32 ratio **1.11**, Q29 **1.40**, Q30 **1.20**. Различия API-токенов и длины не устранялись бессодержательным padding; независимая оценка сопоставимости остаётся открытой.

Linkage: **errors=2, warnings=2**. Q9→Q24 — прежнее пересечение sampling; Q34→Q4 — общие определения типов инструментов/метрик. Прочитанный Q34 явно поддерживает свой ответ, но сканер и авторская оценка не заменяют независимый разбор. Warnings Q31 (B3 IDs) и Q32 (order_id/yml) разобраны через различие форматов и способов задания тегов. Общая окраска FAIL сохранена. Struct exit 0 не означает содержательную приёмку.

**Локальное исполнение PASS:** точные snippets из Q32/Q34 извлечены из candidate JSON и скомпилированы JDK 21 на cached Micrometer **1.13.2**. Проверены tagged Counter, изменение наблюдаемой очереди, сохранение источника Gauge при повторной регистрации того же ID, count/единицы Timer, count/sum DistributionSummary и API serviceLevelObjectives. Это SimpleMeterRegistry, не Prometheus export или полный Boot runtime. Первоначальная ошибка try-with-resources относилась к оболочке harness (SimpleMeterRegistry 1.13.2 не AutoCloseable); оболочка исправлена на explicit close, snippets не ослаблялись.

Логи и manifests в task-2: `metrics-finish-release-checks.json`, `metrics-finish-release-check-0..6.log`, `metrics-finish-registry-check.json`, `metrics-finish-prepared.json`, `metrics-finish-theory-review-current.json`. NOT_RUN: PromQL/promtool, Collector process, реальный exporter/Zipkin, SDK/Agent HTTP/Kafka spans, доставка уведомлений, независимые blind Phase A/B. Ничего не устанавливалось; работающий сервис и его данные не менялись.

## Передать владельцу теории

Q29/Q30/Q32 перечитаны после параллельной редакции и согласованы по главным исправлениям. Q31/Q40 сверены с `docs/interview-coverage/metrics-propagation-theory-review-2026-11-05.md`, замечания по JSON устранены.

Открыта синхронизация оставшихся разделов:

1. **Q35:** YAML с `jaeger` exporter не подходит для contrib 0.111.0; явно выбрать distribution/version и совместимый OTLP export. Нельзя переносить вывод на loki exporter: он есть в этом manifest. Секция `management.otlp.*` является Boot-конфигурацией, а не настройкой standalone Java Agent. Ограничение памяти не даёт гарантии отсутствия OOM/потерь, а component availability, pipeline enablement и порядок processors следует проверять отдельно. Это блокер готовности полной пары.
2. **Q33:** добавить явный prereq Actuator для Boot-пути и не выдавать ручной SdkTracerProvider за подключённую к Boot/глобальную инструментацию без wiring/lifecycle. 10% sampling — пример policy; субъективные сравнения «легче/богаче/предпочтительнее» не заменяют ограничения конкретного backend/version.
3. **Q34:** развести Micrometer DistributionSummary и Prometheus summary, явно сказать о count и наблюдении активных задач LongTaskTimer. Пример `@Timed` требует настроенной интеграции/aspect; одна аннотация не гарантирует измерение произвольного метода. Одновременное указание percentiles/histogram не объявлять автоматической выгрузкой обоих видов статистики: сверить с конкретным registry/version, как уже сделано в Q32.

После этой синхронизации нужна проверка изменённых секций и один финальный структурный проход по паре. До этого тема не повышается в LLM_READY; строгая независимая приёмка и FINAL остаются отдельной незавершённой задачей.
