# Metrics — Zipkin, инструменты Micrometer и Collector

Дата: 2026-11-05. Режим SEMANTIC_UPDATE, новый scope **Q33/Q34/Q35**. Прочитана теория, все 12 вариантов B0 и sections; ключи A/B/D видны. Это авторская source-проверка и ограниченный Micrometer runtime, не independent blind, Phase A/B или FINAL.

Теория: [metrics-tracing-interview.md](../../cheatsheets/interview/monitoring/metrics-tracing-interview.md), общий SHA-256 `3cf74ac5a7a274688e046ff6b345875e9b0ceb2f1fed6edc5d28b226f7162822`. Новые три Q добавлены к ранее проверенным Q29/Q30/Q31/Q32/Q40, сохранив их текст побайтово. Общий diff от исходной теории на 41ace82b3 охватывает **восемь Q**; остальные 33 Q, header/TOC/frontmatter/See also и все 41 ID/заголовок/порядок сохранены.

MCQ только чтение: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `988277ca14bbe7eafd2bbbb8d7498a35a7395960e22e3bb8d8f98b0331b9cef3`; per-Q хеши и снимок в локальных артефактах. Автор теории JSON, общие реестры/планы не менял. Изменения MCQ уже в HEAD `38ffbd5de4dbdedcd248428184b38ac3a0edf11c`.

## Q33 — Boot/Zipkin

Указаны **Boot 3.3.2**, Actuator как общий prerequisite и две альтернативные связки Brave/reporter или OTel/exporter. XML явно является dependencies fragment для существующего Boot web-приложения с согласованным dependency management. Убраны неподтверждённая частота прямого подключения Zipkin и субъективный выбор предпочтительного backend. Sampling 0.1 назван примером, не production-нормой.

Для исходящих HTTP-вызовов названы auto-configured builders. Отдельный exporter/provider не объявляется автоматически подключённым к действующей Boot/global инструментации: нужны pipeline и lifecycle. [Boot tracing source v3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-docs/src/docs/antora/modules/reference/pages/actuator/tracing.adoc), [Java SDK](https://opentelemetry.io/docs/languages/java/sdk/).

XML и YAML разобраны; cached metadata Boot3.3.2 содержит Zipkin endpoint, sampling probability и OTLP tracing endpoint. Это не Maven dependency resolution и не Boot auto-configuration/runtime: Brave/Zipkin exporter jars нет в проверенном cache, ничего не устанавливалось.

## Q34 — инструменты

Разделены Counter/Gauge и Timer/DistributionSummary по природе наблюдения; Timer и Summary имеют count/суммы. DistributionSummary не назван Prometheus summary: экспорт зависит от registry/config. В новых коротких примерах нет неподключённого domain-кода или обещания автоматического @Timed. Для произвольного метода требуется соответствующая framework/aspect интеграция. [Counters](https://docs.micrometer.io/micrometer/reference/concepts/counters.html), [Timer](https://docs.micrometer.io/micrometer/reference/concepts/timers.html), [Summary](https://docs.micrometer.io/micrometer/reference/concepts/distribution-summaries.html).

LongTaskTimer измеряет **активные** задачи, не завершённую историю. Удалено неподтверждённое имя batch_import_active_tasks_current; точные имена предложено сверять с экспозицией. [Micrometer1.13 LongTaskTimer](https://docs.micrometer.io/micrometer/reference/1.13/concepts/long-task-timers.html).

**Runtime PASS:** точные Java B0/B1 Q34 извлечены без изменения и скомпилированы Corretto22.0.2 на cached Micrometer1.13.2/PrometheusJavaClient1.2.1. Counter=6; Gauge читает изменение очереди 2→1; Timer count1/150ms; Summary count2/sum768 bytes. MockClock без sleep дал LongTaskTimer active2/total8s/max5s, после stop первого — active1/total3s, после stop обоих — active0/total0/max0. В экспозиции этой версии во время двух задач наблюдались batch_import_seconds_count=2, _sum=8, _max=5: этот count относится к активным задачам. Сохранены active/stopped exposition. Реальная batch job и @Timed weaving здесь не запускались.

## Q35 — Collector

Выбрана конкретная distribution **otelcol-contrib 0.111.0**. В её pinned manifest есть OTLP receiver, memory_limiter, batch, debug и otlp exporters; jaeger exporter отсутствует, loki exporter ещё присутствует. Нельзя объявлять оба отсутствующими во всех версиях. [Manifest v0.111.0](https://github.com/open-telemetry/opentelemetry-collector-releases/blob/v0.111.0/distributions/otelcol-contrib/manifest.yaml).

Основной YAML показывает локальный OTLP→memory_limiter→batch→debug для traces/metrics/logs. Он не выдаётся за backend storage. Дополнительный YAML явно назван фрагментом: добавить otlp/trace_backend и заменить traces.exporters, сохранив receiver/processors. Указаны доступность адреса из окружения, соответствие транспорта и отсутствие петли в собственный receiver. [Debug0.111.0](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/exporter/debugexporter/README.md), [OTLP exporter0.111.0](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/exporter/otlpexporter/README.md).

Найден и исправлен дополнительный дефект старого примера: **check_interval отсутствовал**. В memory_limiter0.111.0 factory возвращает пустой Config, а Validate отклоняет CheckInterval<=0; теперь указан 1s. Положительные illustrative limits не объявлены гарантией от OOM. Limiter стоит первым, может отказывать в приёме и требует backpressure; persistent storage/queues/retries — отдельные механизмы с пределами. Batch send_batch_size — threshold, не maximum. [Factory](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/processor/memorylimiterprocessor/factory.go), [memory config Validate](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/internal/memorylimiter/config.go), [batch config](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/processor/batchprocessor/config.go), [resiliency](https://opentelemetry.io/docs/collector/resiliency/).

Разделены Boot management.otlp.tracing.endpoint с полным HTTP signal path и standalone Java Agent2.x OTEL_EXPORTER_OTLP_*; pipeline не создаёт источники всех signals в приложении. [Boot3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-docs/src/docs/antora/modules/reference/pages/actuator/tracing.adoc), [OTLP endpoint rules](https://opentelemetry.io/docs/languages/sdk-configuration/otlp-exporter/).

YAML разобран Ruby; ссылки компонентов/порядок/параметры и поддержка signals сверены статически с pinned config/factory source. Фрагмент OTLP вручную совмещён с основным config для проверки ссылок; это не тест Collector confmap merge. **Collector CLI/Validate/process NOT_RUN**: otelcol, otelcol-contrib и Go локально не найдены. Docker не вызывался и прежний отказ не обходился; установки не выполнялись.

## Согласование и границы приёмки

Ключи **Q33 A / Q34 B / Q35 D** и sections согласуются с исправленной теорией. Q33 не подменяет dependencies свойствами или объект exporter готовым pipeline. Q34 неверные варианты меняют смысл инструментов, а LongTaskTimer и count описаны отдельно. Q35 отличает component enablement, инструментацию и memory control от доставки без потерь. Старые blockers theory-sync из MCQ-отчёта по этим трём Q исправлены; это авторский вывод, не независимая готовность всей пары/FINAL.

Структура, fences, ссылки, XML/YAML и whitespace PASS. verify-mcq-json PASS41 Q/41B, semantic review NOT_RUN; SHA JSON стабилен при проверках. Linkage **exit1/errors1/warnings2**: Q9→Q24 остаётся OPEN; warnings Q31 SpanId/TraceId и Q32 order_id/yml в неверных вариантах. Q34→Q4 после содержательного уточнения собственных инструментов и активных задач исчез; определения обоих разделов и MCQ прочитаны, но scanner/автор не заменяют независимую linkage-проверку. Пороги/алгоритм не менялись, padding не добавлялся.

**NOT_RUN:** реальный Zipkin/exporter/Boot tracing, Collector/CLI/config validation, HTTP/PromQL/Prometheus server, нагрузка/отказы/OOM/persistent delivery, новая @Timed integration и независимые SFPL/ANSC/RU/PhaseA/B/FINAL. Ранее выполненные ограниченные API/binding проверки других пяти Q не расширяются на эти интеграции.

Артефакты `/tmp/metrics-zipkin-tools-collector-theory-20261105/`: before/state и JSON snapshot, static-checks.json, boot-metadata-check.json, q33 dependencies/XML+YAML, q35 main/OTLP fragment и manual-combination.json, collector-source-checks.json, linkage/verify logs, runtime/ToolsExamplesCheck.java, classpath.txt, execution.json, active/stopped-exposition.txt. Java source Q34 не менялся после runtime при добавлении отдельного Q35 OTLP-фрагмента. Первичные источники прочитаны 2026-11-05; pinned Boot3.3.2/Collector0.111.0 и локальный Micrometer1.13.2 отделены от online docs с иными patch/latest версиями.
