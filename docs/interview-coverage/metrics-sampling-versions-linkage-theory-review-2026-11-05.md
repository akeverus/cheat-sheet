# Metrics — версии sampling, span metrics и открытый linkage

Дата: 2026-11-05. SEMANTIC_UPDATE: **Q24/Q25/Q27/Q28/Q37**. Дополнительно без изменения текста проверены **Q9/Q31/Q32** и их linkage-сигналы. Прочитаны восемь разделов и все 32 варианта B0 с sections; ключи были видны. Это авторская source-проверка, не independent blind, Phase A/B или FINAL.

Теория: [metrics-tracing-interview.md](../../cheatsheets/interview/monitoring/metrics-tracing-interview.md), SHA-256 `9846393eb2ecff76bf883f6166078fdb2e8dbfa475e6de3f9190e010e205e226`. База — коммит `6d7e7ddaa659fcc196d748f72885b7af0638ebc6`, исходный Markdown SHA-256 `3cf74ac5a7a274688e046ff6b345875e9b0ceb2f1fed6edc5d28b226f7162822`. Изменены ровно пять Q; остальные 36 Q, все IDs/заголовки/порядок, frontmatter/TOC/See also сохранены побайтово.

JSON только чтение: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, снимок SHA-256 `988277ca14bbe7eafd2bbbb8d7498a35a7395960e22e3bb8d8f98b0331b9cef3`. Копия и per-Q хеши сохранены локально. JSON, общие реестры/планы и индекс автором этого пакета не менялись. Индекс передан работе MCQ; пакет подготовлен без staging и коммита.

## Содержательные исправления

**Q25:** убран обязательный порядок «метрики → трейсы» и обещание найти причину по одному trace. Таблица различает агрегаты, конкретный запрос, параллельные spans и реально сохранённые связанные логи. Exemplar не объявлен конкретным запросом p99 и не гарантирует доступный trace. Агрегаты из трасс зависят от охвата и sampling. [OTel metrics](https://opentelemetry.io/docs/concepts/signals/metrics/), [traces](https://opentelemetry.io/docs/concepts/signals/traces/), [exemplars](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exemplars).

**Q24/Q27/Q28/Q37:** подвижные ссылки main для примеров заменены привязкой к **otelcol-contrib 0.151.0**. Q35 остаётся отдельным примером 0.111.0. Сравнение metadata подтверждает `span_metrics`/устаревший `spanmetrics` в 0.151.0 против `spanmetrics` в 0.111.0. В старом config нет sampling_strategy и decision_wait_after_root_received; переносить новые поля в старую поставку нельзя. [Metadata 0.151.0](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.151.0/connector/spanmetricsconnector/internal/metadata/generated_status.go), [metadata 0.111.0](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.111.0/connector/spanmetricsconnector/internal/metadata/generated_status.go), [old tail config](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.111.0/processor/tailsamplingprocessor/config.go).

В Q27 явно задан histogram.unit=s; calls считает наблюдаемые spans по измерениям, а не автоматически внешние запросы. Разветвление до tail sampling сохранено. Наличие connector, его traces→metrics тип и config поля сверены с pinned metadata/factory/config и manifest. В этой версии exporter otlp — поддерживаемый устаревший alias otlp_grpc; receiver по-прежнему otlp. Поэтому существующие otlp/traces и otlp/metrics не объявлены отсутствующими компонентами. [Span connector 0.151.0](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.151.0/connector/spanmetricsconnector/README.md), [exporter factory](https://github.com/open-telemetry/opentelemetry-collector/blob/v0.151.0/exporter/otlpexporter/factory.go), [manifest](https://github.com/open-telemetry/opentelemetry-collector-releases/blob/v0.151.0/distributions/otelcol-contrib/manifest.yaml).

В Q24/Q37 явно выбран trace-complete. Q37 отдельно описывает span-ingest, очистку ожидающих решений, запрет stateful policies и пределы поздней доставки/кешей. 500ms проходит latency threshold без upper bound; схема Q28 теперь использует >=1s и span status ERROR вместо автоматического отождествления HTTP500 со span ERROR. Это source-вывод, не запущенный evaluator. [Tail config](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.151.0/processor/tailsamplingprocessor/config.go), [processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.151.0/processor/tailsamplingprocessor/processor.go), [latency evaluator](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/v0.151.0/processor/tailsamplingprocessor/internal/sampling/latency.go).

## Сверка MCQ и открытых сигналов

Ключи **Q9 C / Q24 B / Q25 B / Q27 A / Q28 A / Q31 B / Q32 B / Q37 A** совместимы с прочитанной теорией. Новые версии и уточнения не меняют смысл вопросов/ключей. Противоречий, требующих JSON-правки, автор не выявил; полная тема и неугадываемость не принимаются.

| Сигнал | Авторская содержательная проверка | Состояние |
|---|---|---|
| Q9→Q24 | Собственный Q9 определяет sampling и его применение; Q24 сочетает меры сокращения потока. A путает отбор со сжатием; B запрещает production; C определяет отбор; D подменяет отбор trace удалением всех дочерних spans. Опровержения не требуют учить соседний YAML. | OPEN для независимого linkage-review. |
| Q31 SpanId/TraceId | Имена находятся в ложном варианте X-B3-*. Свой Q31 прямо задаёт W3C traceparent/tracestate; для опровержения D не нужно узнавать новое имя SDK. Форматы подробно сопоставлены в Q40. | WARN сохранён; автор не выявил нарушения. |
| Q32 order_id/yml | Формула failure/total и ограниченные теги опровергают необходимость уникального ID; собственный текст/API опровергают невозможность задать теги в Micrometer. Имя config-файла не нужно для решения. | WARN сохранён; автор не выявил нарушения. |

Диагностика неизменённого scanner показывает, что преимущество Q24 для Q9 дают, в частности, основы «стоим», «хране», «число», «удаля» и разное написание trace/трейс. Это объяснение механики лексического сигнала, а не доказательство независимой приёмки. Q9/Q31/Q32 побайтово сохранены; padding, allowlist и пороги не менялись. Сканер остаётся **exit1/errors1/warnings2**: Q9→Q24 0.586 против 0.414.

## Проверки и пределы

- PASS: точный scope пяти Q, структура, остальные 36 Q, fences, локальные ссылки и whitespace diff.
- PASS: Ruby разбирает три точных YAML-блока Q24/Q27/Q37. После правки русского текста эти блоки побайтово прежние. Разводка traces→span_metrics→metrics и отдельная ветка tail sampling сверены статически; фрагменты намеренно неполные.
- PASS verify-mcq-json.sh: 41 Q / 41 B, semantic review NOT_RUN. Первый вызов ошибочно указывал несуществующий .py wrapper; этот процесс не выполнял валидацию. Путь уточнён через rg, штатный .sh завершился 0; оба лога сохранены.
- JSON SHA и per-Q хеши стабильны на проверенном снимке. Linkage FAIL не скрыт.

**NOT_RUN:** Collector CLI/Validate/process, Go evaluator, реальные spans/export/storage, PromQL/Prometheus, нагрузка/потери, Mermaid render и независимые SFPL/ANSC/RU/Phase A/B/FINAL. Go/otelcol/otelcol-contrib/promtool/amtool локально не найдены. Docker не вызывался, прежний отказ не обходился; установки не выполнялись.

Прямое скачивание raw snapshots через локальный Python не удалось из-за DNS resolution. Первичные pinned страницы доступны через web tool и прочитаны; локально сохранены web extracts, а не полные побайтовые копии исходников. Это не блокирует текстовую source-проверку, но локальная сборка/исполнение не заявляются.

Артефакты: `/tmp/metrics-sampling-pinned-theory-20261105/` — before/state, seed snapshot, static-checks.json, source-review.json, web-extracts-round1/2/3.txt, source-downloads.json с DNS-ошибками, три YAML/JSON, q9-lexical-diagnostics.json и linkage/verify logs. Все источники прочитаны 2026-11-05; версии 0.151.0, 0.111.0 и online OTel spec 1.62.0 различены.
