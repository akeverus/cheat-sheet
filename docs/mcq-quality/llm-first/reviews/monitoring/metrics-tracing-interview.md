# Metrics / tracing — завершённый авторский проход

- Дата: 2026-11-05; исполнитель/модель: Codex, точный deployment ID не раскрыт.
- Статус: `LLM_READY`. Авторский результат, не независимая приёмка.
- Охват: Q1–Q41, 41 блок B0, все варианты A–D и sections.

Дата: 2026-11-05. Исполнитель: Codex, автор MCQ-пакетов, ключи были доступны. Охват **41 Q / 41 B0**, все A/B/C/D и sections. Статус **LLM_READY (авторский)** после синхронизации теории в `6d7e7ddaa659fcc196d748f72885b7af0638ebc6`. Это не independent acceptance, AUDITED или FINAL PASS.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `988277ca14bbe7eafd2bbbb8d7498a35a7395960e22e3bb8d8f98b0331b9cef3`.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `9846393eb2ecff76bf883f6166078fdb2e8dbfa475e6de3f9190e010e205e226`.

Ранее пройдены Q1–Q28/Q36–Q39/Q41; завершающий пакет Q29–Q35/Q40 закоммичен в `38ffbd5de4dbdedcd248428184b38ac3a0edf11c`. Подробности источников и предыдущего состояния — в `metrics-tracing-q29-q35-q40-final-author-pass-2026-11-05.md` и остальных scoped reports этой папки. Прежний NEEDS_THEORY_SYNC относился к старому снимку Markdown, не является актуальным блокером перечисленных ниже разделов.

После новой редакции полностью перечитаны Q33/Q34/Q35 и сопоставлены с A/B/C/D и sections MCQ. Q33 теперь явно задаёт Actuator, одну связку bridge/exporter и Boot 3.3.2, различает instrumented calls и один созданный SDK object. Q34 различает семантику четырёх инструментов, count, active LongTaskTimer, histogram и клиентские quantiles. Q35 содержит versioned contrib 0.111.0, pipeline enablement, реальные ограничения memory_limiter/batch и отдельную настройку Boot/standalone Agent. Известные расхождения завершающего пакета закрыты этим чтением; JSON менять повторно не потребовалось.

Первичные источники проверены для соответствующих версий: [Boot v3.3.2 tracing](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-docs/src/docs/antora/modules/reference/pages/actuator/tracing.adoc), [Micrometer meters](https://docs.micrometer.io/micrometer/reference/concepts/meters.html), [gauges](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html), [Timer 1.13.2](https://github.com/micrometer-metrics/micrometer/blob/v1.13.2/micrometer-core/src/main/java/io/micrometer/core/instrument/Timer.java), [contrib manifest 0.111.0](https://github.com/open-telemetry/opentelemetry-collector-releases/blob/v0.111.0/distributions/otelcol-contrib/manifest.yaml), [memory limiter 0.111.0](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/processor/memorylimiterprocessor/README.md), [batch config 0.111.0](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.111.0/processor/batchprocessor/config.go), [Agent configuration](https://opentelemetry.io/docs/zero-code/java/agent/configuration/). В частности check_interval не оставлен нулевым, send_batch_size не назван максимумом, а loki exporter не объявлен отсутствующим в 0.111.0.

Структура: **PASS 41/41**, semantic review NOT_RUN в валидаторе. Anti-tell **0/41**, SKEL **hard 0 / warn 1**. Parity остаётся **FAIL max word ratio 1.55, Q32**, 2 блока выше 1.35, style guessability 0.238. Различия API/русских слов и поверхностная сопоставимость рассмотрены по содержанию; нет padding или изменения порогов. Это не независимое доказательство неугадываемости.

Linkage: **1 error / 2 warnings**. Открыт независимый разбор Q9→Q24; автор различает общий sampling и конкретную политику/этапы. Warnings Q31 (B3 IDs) и Q32 (order_id/yml) относятся к опровержению чужого формата и способа задания tags. Q34→Q4 после смысловой редакции теории больше не выдаётся сканером, что само по себе не доказывает приёмку. Общая диагностика FAIL сохранена.

Локальные примеры Q32/Q34 ранее исполнены на cached Micrometer 1.13.2: Counter, Gauge, Timer, DistributionSummary, SLO API. Проверка синхронизации — перечитывание теории и структурные/диагностические сканеры; Collector, Zipkin, PromQL, Agent и доставка уведомлений сейчас не запускались. Новые обещания runtime PASS не добавлены. Работающий сервис не менялся.

Логи/хеши task-2: `metrics-theory-synchronized-checks.json`, `metrics-theory-synchronized-check-0..6.log`, `metrics-synchronized-snapshot.json`, `metrics-finish-registry-check.json`. Подготовлен свежий пакет без ключей `metrics-synchronized-blind-input.json` с manifest `metrics-synchronized-blind-manifest.json`, привязанный к указанному снимку теории; reviewer не назначен, пакет не отправлен. Это не официальный run или review evidence. Для строгого prepare/reveal нужен реальный свежий reviewer. **Независимые SFPL/ANSC/RU, слепые фазы A/B и FINAL остаются NOT_RUN.**
