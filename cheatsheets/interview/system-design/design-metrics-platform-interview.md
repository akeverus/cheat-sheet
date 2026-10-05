---
title: "Вопросы на собеседовании: Платформа метрик и алертов"
description: "Многопользовательский сбор метрик, ограничения, долговременное хранение, запросы и правила алертинга"
tags:
  - interview
  - system-design
  - design-metrics-platform-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Design Metrics Monitoring and Alerting System"
  - "Проектирование платформы метрик и алертов"
prerequisites: []
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: Платформа метрик и алертов

[Prometheus и Grafana](../monitoring/prometheus-grafana-interview.md) уже разбирают scrape, cardinality, remote write и Alertmanager; [System Design Q23](system-design-interview.md#q23-как-спроектировать-observability-для-системы) — наблюдаемость отдельного сервиса. Здесь проектируем **общую платформу для многих команд**: как не дать одному tenant перегрузить сбор, запросы или вычисление правил других.

## Полезные ссылки

- [Prometheus: remote write tuning](https://prometheus.io/docs/practices/remote_write/) — WAL, очередь, отставание и риск потери при длительном сбое получателя.
- [Grafana Mimir: distributor](https://grafana.com/docs/mimir/latest/references/architecture/components/distributor/) и [runtime overrides](https://grafana.com/docs/mimir/latest/configure/about-runtime-configuration/) — tenant limits на ingest и активные ряды.
- [Grafana Mimir: query frontend](https://grafana.com/docs/mimir/latest/references/architecture/components/query-frontend/), [scheduler](https://grafana.com/docs/mimir/latest/references/architecture/components/query-scheduler/) и [ruler](https://grafana.com/docs/mimir/latest/references/architecture/components/ruler/) — разделение чтения и вычисления правил.
- [Thanos Compactor: retention и downsampling](https://thanos.io/tip/components/compact.md/) — долгие интервалы и разрешение исторических данных.

## Содержание

- [Q1. Как принять метрики многих команд без взаимного влияния?](#q1-как-принять-метрики-многих-команд-без-взаимного-влияния)
- [Q2. Как совместить короткие алерты и длинную историю метрик?](#q2-как-совместить-короткие-алерты-и-длинную-историю-метрик)
- [Q3. Как защитить запросы одного tenant от тяжёлых запросов другого?](#q3-как-защитить-запросы-одного-tenant-от-тяжёлых-запросов-другого)
- [Q4. Как изолировать вычисление правил и доставку алертов?](#q4-как-изолировать-вычисление-правил-и-доставку-алертов)
- [See also](#see-also)

## Q1. Как принять метрики многих команд без взаимного влияния?

На входе отделите **аутентифицированный tenant** от присланных клиентом labels. Tenant ID должен назначать доверенный gateway из проверенной учётной записи; нельзя позволять отправителю выбрать чужой tenant простым заголовком. В Mimir запросы используют `X-Scope-OrgID`, поэтому проверку и установку этого заголовка нужно организовать перед входом в платформу. Разделение в хранилище и проверки прав обязательны также на чтении. [Mimir HTTP API](https://grafana.com/docs/mimir/latest/references/http-api/).

Путь записи: Prometheus/агент → remote write → gateway/distributor → приём и валидация samples → долговечный ingest/TSDB. Ограничивайте по tenant скорость samples, burst, число активных series и число новых series за интервал; отдельно защищайте физический ingester от суммарной нагрузки. Высокая cardinality часто вызвана `user_id`, `request_id` или иным безграничным label. Отказ по лимиту должен быть наблюдаемым для отправителя и оператора: метрики rejected samples, lag, age очереди, активные series и доля ошибок по tenant. Mimir документирует per-tenant overrides и instance limits; это конкретный пример механизма, а не требование писать свои лимитеры поверх продукта. [Mimir distributor](https://grafana.com/docs/mimir/latest/references/architecture/components/distributor/), [runtime configuration](https://grafana.com/docs/mimir/latest/configure/about-runtime-configuration/).

Remote write отправителя читает WAL и буферизует данные в очередях. Если получатель долго недоступен, очередь и WAL не дают бесконечной гарантии сохранения: Prometheus описывает риск потери после длительного отставания. Поэтому определите SLO на задержку ingest, предел недоступности, сигнал приближения к нему и политику отбрасывания/восстановления. [Prometheus remote write](https://prometheus.io/docs/practices/remote_write/).

## Q2. Как совместить короткие алерты и длинную историю метрик?

Разделите **горячие свежие samples** для правил и недавних дашбордов от долговременных блоков истории. Точный короткий запрос должен видеть достаточно свежие данные для алерта; месячный график допускает чтение блоков из object storage и более грубое разрешение. На входе хранят raw samples и индексы series; затем compaction уменьшает количество мелких блоков, а downsampling делает длинные диапазоны дешевле читать. Это архитектурный выбор: raw, агрегированные ряды и время хранения назначаются по потребностям разных tenant. [Thanos Compactor](https://thanos.io/tip/components/compact.md/).

Не путайте **downsampling с автоматической экономией места**. Thanos хранит дополнительные представления разрешения и прямо предупреждает, что они могут увеличить объём хранилища, если raw тоже сохранены. Если raw удалён раньше, чем создаётся нужное укрупнённое представление, история может исчезнуть. Если raw удалён после downsampling, пользователь теряет возможность «приблизить» старый график до исходных samples. Поэтому фиксируйте договор на разрешение исторических данных и проверяйте compactor, задержку загрузки блоков и фактическое соблюдение retention. [Thanos retention/downsampling](https://thanos.io/tip/components/compact.md/).

## Q3. Как защитить запросы одного tenant от тяжёлых запросов другого?

Вынесите путь чтения из пути ingest. Query frontend принимает запрос, ограничивает диапазон/число рядов/время выполнения, при необходимости делит долгий диапазон и использует cache; scheduler ставит работу в очереди, querier читает свежие данные и исторические блоки. Это не устраняет нагрузку само по себе: один запрос после дробления может породить много подзапросов. Ставьте per-tenant пределы параллелизма, объёма выборки и числа активных запросов; добавьте общий предел для узлов. [Mimir query frontend](https://grafana.com/docs/mimir/latest/references/architecture/components/query-frontend/), [configuration](https://grafana.com/docs/mimir/latest/configure/configuration-parameters/).

Планировщик должен не позволять одному tenant занять все queriers. В Mimir query-scheduler использует round-robin по tenant с активными запросами; shuffle sharding может дополнительно ограничить набор queriers для tenant. При перегрузке лучше вернуть явный отказ/timeout с измеримым лимитом, чем незаметно сделать алерты других команд медленнее. Отдельно измеряйте queue wait, execution time, cache hit и число отказов по tenant. [Mimir query scheduler](https://grafana.com/docs/mimir/latest/references/architecture/components/query-scheduler/), [shuffle sharding](https://grafana.com/docs/mimir/latest/configure/configure-shuffle-sharding/).

## Q4. Как изолировать вычисление правил и доставку алертов?

У каждого tenant храните собственные recording/alerting rules с версией, владельцем и namespace. Ruler регулярно вычисляет выражения, а Alertmanager группирует и маршрутизирует уже сработавшие алерты. Число правил, параллельных вычислений, объём запроса и длительность оценки требуют отдельных лимитов; тяжёлая recording rule способна породить новые series и ударить по ingest. В Mimir ruler правила разделены по tenant и могут оцениваться локально или через query frontend; документация предупреждает, что тяжёлая оценка может занять больше интервала и пропустить точки. [Mimir ruler](https://grafana.com/docs/mimir/latest/references/architecture/components/ruler/).

Определите поведение при сбое: `pending` и `firing` относятся к **вычисленному условию**, а отсутствие свежих samples или сбой ruler — отдельное состояние «данных нет/оценка не выполнена». Его нельзя выдавать за норму. Держите метрики пропущенных оценок, ошибок правил, задержки данных и успешности отправки уведомлений, плюс отдельный платформенный алерт на отказ самой системы мониторинга. Для уведомлений используйте штатные grouping, inhibition и silences Alertmanager; см. [Prometheus Q19–Q24](../monitoring/prometheus-grafana-interview.md). Изоляция маршрутов и секретов получателей разных tenant — часть доступа, а не только удобство UI.

---

## See also

- [Prometheus и Grafana](../monitoring/prometheus-grafana-interview.md) — команды, cardinality, Alertmanager, remote write.
- [Observability](../monitoring/observability-interview.md) — сигналы сервиса, SLO и правила полезного алертинга.
- [System Design Q23](system-design-interview.md#q23-как-спроектировать-observability-для-системы) — наблюдаемость в дизайне одного сервиса.
