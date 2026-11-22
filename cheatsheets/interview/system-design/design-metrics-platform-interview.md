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
updated: "2026-11-05"
---
# Вопросы на собеседовании: Платформа метрик и алертов

[Prometheus и Grafana](../monitoring/prometheus-grafana-interview.md) уже разбирают scrape, cardinality, remote write и Alertmanager; [System Design Q23](system-design-interview.md#q23-как-спроектировать-observability-для-системы) — наблюдаемость отдельного сервиса. Здесь проектируем **общую платформу для многих команд**: как не дать одному tenant перегрузить сбор, запросы или вычисление правил других.

## Полезные ссылки

- [Prometheus: remote write tuning](https://prometheus.io/docs/practices/remote_write/) — WAL, очередь, отставание и риск потери при длительном сбое получателя.
- [Grafana Mimir: distributor](https://grafana.com/docs/mimir/latest/references/architecture/components/distributor/) и [runtime overrides](https://grafana.com/docs/mimir/latest/configure/about-runtime-configuration/) — tenant limits на ingest и активные ряды.
- [Grafana Mimir: authentication and authorization](https://grafana.com/docs/mimir/latest/manage/secure/authentication-and-authorization/) — доверенная установка tenant ID перед Mimir.
- [Grafana Mimir: query frontend](https://grafana.com/docs/mimir/latest/references/architecture/components/query-frontend/), [scheduler](https://grafana.com/docs/mimir/latest/references/architecture/components/query-scheduler/) и [ruler](https://grafana.com/docs/mimir/latest/references/architecture/components/ruler/) — разделение чтения и вычисления правил.
- [Thanos Compactor: retention и downsampling](https://thanos.io/v41.0/components/compact.md/) — долгие интервалы, разрешение истории и отказ компактора.

## Содержание

- [Q1. Как принять метрики многих команд без взаимного влияния?](#q1-как-принять-метрики-многих-команд-без-взаимного-влияния)
- [Q2. Как совместить короткие алерты и длинную историю метрик?](#q2-как-совместить-короткие-алерты-и-длинную-историю-метрик)
- [Q3. Как защитить запросы одного tenant от тяжёлых запросов другого?](#q3-как-защитить-запросы-одного-tenant-от-тяжёлых-запросов-другого)
- [Q4. Как изолировать вычисление правил и доставку алертов?](#q4-как-изолировать-вычисление-правил-и-доставку-алертов)
- [See also](#see-also)

## Q1. Как принять метрики многих команд без взаимного влияния?

На входе отделите **аутентифицированный tenant** от присланных клиентом labels. В Mimir `X-Scope-OrgID` определяет tenant, но сам заголовок не подтверждает личность отправителя. Доверенный reverse proxy должен аутентифицировать запрос, убрать клиентское значение заголовка и установить tenant ID из проверенной учётной записи. Применяйте эту границу доверия и к записи, и к чтению; иначе отправитель сможет выбрать чужой tenant. [Mimir authentication and authorization](https://grafana.com/docs/mimir/latest/manage/secure/authentication-and-authorization/).

Путь записи: Prometheus/агент → remote write → gateway/distributor → приём и валидация samples → долговечный ingest/TSDB. Ограничивайте по tenant скорость samples, burst и число рядов в памяти (`in-memory series`); отдельно наблюдайте активные ряды и скорость появления новых рядов (`churn`). Это разные показатели: в Mimir проверьте выбранный режим подсчёта in-memory/owned series и архитектуру, а не подставляйте число active series в любой лимит. Защищайте физический ingester и от суммарной нагрузки. Высокая cardinality часто вызвана `user_id`, `request_id` или иным безграничным label. Отказ по лимиту должен быть наблюдаемым для отправителя и оператора: метрики rejected samples, lag, age очереди, активные series и доля ошибок по tenant. Mimir документирует per-tenant overrides и instance limits; это конкретный пример механизма, а не требование писать свои лимитеры поверх продукта. [Mimir distributor](https://grafana.com/docs/mimir/latest/references/architecture/components/distributor/), [runtime configuration](https://grafana.com/docs/mimir/latest/configure/about-runtime-configuration/), [series limits](https://grafana.com/docs/mimir/latest/configure/configuration-parameters/), [owned series tracking](https://grafana.com/docs/mimir/latest/configure/configure-owned-series/).

**Две реплики сборщика.** Если два Prometheus собирают одни и те же цели и отправляют метрики в Mimir, настройте HA tracker. Для пары реплик в одном tenant задайте одинаковый `cluster` и разные `__replica__`. Distributor принимает samples выбранной реплики и отбрасывает samples второй. После таймаута переключения резервная реплика может стать основной. На графике возможен пропуск сбора во время переключения. Независимым парам сборщиков внутри tenant нужны разные значения `cluster`, иначе tracker может отбросить данные одной пары как дубли другой. При обычной дедупликации Mimir определяет пару меток по первому ряду всего remote-write запроса. Если запросы собирает промежуточный прокси, группируйте ряды по tenant, cluster и replica перед отправкой. [Mimir — дедупликация HA-реплик](https://grafana.com/docs/mimir/latest/configure/configure-high-availability-deduplication/).

Remote write отправителя читает WAL и буферизует данные в очередях. Если получатель долго недоступен, очередь и WAL не дают бесконечной гарантии сохранения: Prometheus описывает риск потери после длительного отставания. Поэтому определите SLO на задержку ingest, предел недоступности, сигнал приближения к нему и политику отбрасывания/восстановления. [Prometheus remote write](https://prometheus.io/docs/practices/remote_write/).
**Что делать при отказе записи.** Для Remote Write 1.0 код 5xx требует повтора с backoff. При 429 повтор допустим, но в Prometheus настройка `queue_config.retry_on_http_429` по умолчанию выключена. Если всплеск кратковременный и получатель успевает разобрать очередь после него, повтор может сохранить точки. При устойчивом превышении квоты повтор увеличивает отставание. Следите за возрастом очереди и устраняйте источник перегрузки, например сокращайте объём отправляемых рядов. Остальные 4xx протокол предписывает не повторять. Если запрос отклонён из-за невалидных меток, исправьте источник и проверьте метрики потерь. Получатель может принять валидные ряды того же запроса и всё равно вернуть 400. [Спецификация Remote Write 1.0](https://prometheus.io/docs/specs/prw/remote_write_spec/#retries--backoff), [настройки Prometheus](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#remote_write).


## Q2. Как совместить короткие алерты и длинную историю метрик?

Разделите **горячие свежие samples** для правил и недавних дашбордов от долговременных блоков истории. Точный короткий запрос должен видеть достаточно свежие данные для алерта; месячный график допускает чтение блоков из object storage и более грубое разрешение. На входе хранят raw samples и индексы series; затем compaction уменьшает количество мелких блоков, а downsampling делает длинные диапазоны дешевле читать. Это архитектурный выбор: raw, агрегированные ряды и время хранения назначаются по потребностям разных tenant. [Thanos Compactor](https://thanos.io/v41.0/components/compact.md/).

Не путайте **downsampling с автоматической экономией места**. Thanos хранит дополнительные представления разрешения и прямо предупреждает, что они могут увеличить объём хранилища, если raw тоже сохранены. Если raw удалён раньше, чем создаётся нужное укрупнённое представление, история может исчезнуть. Если raw удалён после downsampling, пользователь теряет возможность «приблизить» старый график до исходных samples. Поэтому фиксируйте договор на разрешение исторических данных и проверяйте задержку загрузки блоков и фактическое соблюдение retention. [Thanos retention/downsampling](https://thanos.io/v41.0/components/compact.md/).

**Шаг графика и разрешение источника.** В Thanos Query `step` задаёт расстояние между вычислениями PromQL на графике. Отдельный параметр `max_source_resolution` ограничивает разрешение читаемых данных: `0` выбирает только raw, `5m` допускает пятиминутный downsampling. Например, при `step=1h` и `max_source_resolution=0` график вернёт точку на каждый час, но выражение будет вычисляться по исходным данным. Если raw за тот месяц уже удалены, для исторического графика нужно разрешить сохранённое разрешение источника. [Thanos Query 41.0 — Auto downsampling](https://thanos.io/v41.0/components/query.md/#auto-downsampling).

**Выберите, какие запросы должна поддерживать история.** Например, для отчёта о доле запросов быстрее 300 мс храните по часовым окнам прирост числа запросов длительностью до 300 мс включительно и прирост общего числа запросов. За месяц суммируйте числители и знаменатели отдельно, затем делите. При 95 из 100 и 1 из 10 результат равен 96/110, хотя простое среднее долей даёт 52,5%. Если запросов за период не было, покажите отсутствие наблюдений. Для классических гистограмм Prometheus этот расчёт требует границы `le="0.3"` у всех источников, а приросты должны учитывать сбросы счётчиков до суммирования по экземплярам.

Если через месяц нужно запросить произвольный перцентиль, сохраните приросты всех совместимых бакетов за каждый интервал и вычисляйте квантиль после объединения распределений. Готовые часовые p95 не позволяют восстановить p95 за месяц. Часовые агрегаты подходят для объединения целых окон, а запрос по части окна требует более подробных данных либо явной оговорки о точности. Такой rollup проектируется под конкретный отчёт. Его состав и семантику проверяйте отдельно от встроенного downsampling хранилища. [Prometheus: Histograms and summaries](https://prometheus.io/docs/practices/histograms/).

В Thanos не запускайте два активных Compactor на одном потоке блоков в одном object storage: конкурентная компакция может создать перекрывающиеся блоки и остановить обработку. Для восстановления одного потока нужен один активный экземпляр; масштабировать компакторы можно разделением непересекающихся потоков. Следите за backlog и `thanos_compact_halted`: при ошибке Compactor может оставаться запущенным с этим значением `1`, а downsampling и retention перестанут выполняться. [Thanos Compactor: streams, halting и retention](https://thanos.io/v41.0/components/compact.md/).

## Q3. Как защитить запросы одного tenant от тяжёлых запросов другого?

Вынесите путь чтения из пути ingest. Query frontend принимает запрос, ограничивает диапазон/число рядов/время выполнения, при необходимости делит долгий диапазон и использует cache; scheduler ставит работу в очереди, querier читает свежие данные и исторические блоки. Это не устраняет нагрузку само по себе: один запрос после дробления может породить много подзапросов. Ставьте per-tenant пределы параллелизма, объёма выборки и числа активных запросов; добавьте общий предел для узлов. [Mimir query frontend](https://grafana.com/docs/mimir/latest/references/architecture/components/query-frontend/), [configuration](https://grafana.com/docs/mimir/latest/configure/configuration-parameters/).

Планировщик должен не позволять одному tenant занять все queriers. В Mimir query-scheduler использует round-robin по tenant с активными запросами; shuffle sharding может дополнительно ограничить набор queriers для tenant. При перегрузке лучше вернуть явный отказ/timeout с измеримым лимитом, чем незаметно сделать алерты других команд медленнее. Отдельно измеряйте queue wait, execution time, cache hit и число отказов по tenant. [Mimir query scheduler](https://grafana.com/docs/mimir/latest/references/architecture/components/query-scheduler/), [shuffle sharding](https://grafana.com/docs/mimir/latest/configure/configure-shuffle-sharding/).

**Где поставить лимит?** Допустим, один отчёт после дробления нуждается в 30 подзапросах. Если для tenant в Mimir задать `max_query_parallelism: 4`, frontend ограничит параллельное планирование четырьмя частями на исходный запрос. Если отчётов много, каждый получает собственный предел, поэтому нужен отдельный лимит нагрузки на tenant. На каждом query-scheduler `max_outstanding_requests_per_tenant` ограничивает число незавершённых запросов этого tenant. Превышение даёт HTTP 429. При нескольких scheduler это предел на экземпляр. Очередь распределяет допущенную работу, а стоимость уже выполняющегося запроса ограничивают отдельно, например по объёму прочитанных чанков. [Grafana Mimir: параметры конфигурации](https://grafana.com/docs/mimir/latest/configure/configuration-parameters/).

**Неполный ответ при отказе источника.** В Thanos Query запрос с `partial_response=true` может завершиться успешно с предупреждением, если один StoreAPI недоступен. Например, глобальный график покажет данные доступных регионов, а глобальная сумма окажется заниженной, если выпавший регион вносил вклад. Для обзорного дашборда такой результат можно показать с явной пометкой. Для отчёта, которому нужны все регионы, используйте `partial_response=false` и обработайте отказ запроса. Для контроля полноты также сверяйте состав ожидаемых источников: пропавший из обнаружения StoreAPI может вообще не участвовать в запросе. [Thanos Query 41.0 — Partial Response Strategy](https://thanos.io/v41.0/components/query.md/#partial-response-strategy).

## Q4. Как изолировать вычисление правил и доставку алертов?

У каждого tenant храните собственные recording/alerting rules с версией, владельцем и namespace. Ruler регулярно вычисляет выражения, а Alertmanager группирует и маршрутизирует уже сработавшие алерты. Число правил, параллельных вычислений, объём запроса и длительность оценки требуют отдельных лимитов; тяжёлая recording rule способна породить новые series и ударить по ingest. В Mimir ruler правила разделены по tenant и могут оцениваться локально или через query frontend; документация предупреждает, что тяжёлая оценка может занять больше интервала и пропустить точки. [Mimir ruler](https://grafana.com/docs/mimir/latest/references/architecture/components/ruler/).

Если ruler в remote-режиме использует тот же query frontend и пул исполнения, что и дашборды, дорогие интерактивные запросы могут задержать правила даже внутри одного tenant. Для критичных правил выделите отдельный путь запросов с собственным бюджетом исполнения. Отдельный frontend без выделенной ёмкости в общем пуле сам по себе не обеспечивает изоляцию. Проверяйте время ожидания и длительность оценки относительно интервала группы. [Mimir ruler: operational modes](https://grafana.com/docs/mimir/latest/references/architecture/components/ruler/#operational-modes).

**Два разных лимита в Prometheus.** При интервале группы 30 секунд вычисление длительностью 45 секунд пропустит запуск на 30-й секунде. Это видно по росту счётчика `rule_group_iterations_missed_total`. Даже быстрая оценка может вернуть слишком много рядов. Параметр группы `limit` задаёт предел результата для каждого правила. Если при `limit: 1000` recording rule вернула 1200 рядов, Prometheus отбросит весь результат этого правила и зарегистрирует ошибку оценки. Для alerting rule превышение также очищает состояние всех её алертов. Ограничение результата срабатывает после вычисления выражения. Для ограничения стоимости запроса нужен отдельный бюджет исполнения. [Prometheus — ограничения и пропуски вычислений](https://prometheus.io/docs/prometheus/latest/configuration/recording_rules/#limiting-alerts-and-series).

**Различайте пустой результат и сбой вычисления.** Например, пороговое правило по задержке успешно вычислилось и вернуло пустой вектор. Возможны как нормальная задержка, так и отсутствие входной метрики. Если метрика должна поступать регулярно, отдельно контролируйте её отсутствие, например через `absent_over_time(service_heartbeat{job="checkout"}[5m])`. Такое выражение срабатывает, когда за окно нет ни одного подходящего sample. Если у job несколько экземпляров и хотя бы один продолжает отправлять метрику, это выражение не покажет исчезнувший экземпляр. Для проверки каждого экземпляра сравнивайте поступившие данные со списком ожидаемых источников. [Prometheus alerting rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/), [`absent_over_time`](https://prometheus.io/docs/prometheus/latest/querying/functions/#absent_over_time).

**Поздние точки при штатной доставке.** Допустим, точки попадают в хранилище через 20–40 секунд после сбора. При расчёте правила на текущее время правый край окна может содержать ещё не все данные. В Prometheus настройка группы `query_offset: 45s` при запуске в 12:00:00 сдвигает время запроса на 11:59:15. Интервал запуска группы сохраняется, а обнаружение свежих изменений задерживается. Подбирайте сдвиг по измеренной задержке появления точек в запросах и бюджету времени обнаружения. Отдельно следите за отставанием доставки: если оно превысит выбранный сдвиг, окно снова может оказаться неполным. [Prometheus — Rule query offset](https://prometheus.io/docs/prometheus/latest/configuration/recording_rules/#rule-query-offset).

При timeout или остановке ruler результат оценки вообще не получен. Правило на отсутствие данных в этом же ruler также может не выполниться. Контролируйте ошибки и пропуски оценок отдельно от задержки поступления samples. Для проверки всего пути используйте контрольный сигнал, который должен регулярно доходить до внешнего получателя, и сигнализируйте снаружи, если он перестал приходить. Для обычных уведомлений используйте grouping, inhibition и silences Alertmanager. Изолируйте маршруты и секреты получателей по tenant. См. [Prometheus Q19–Q24](../monitoring/prometheus-grafana-interview.md).

**Доставка при сетевом разделении.** В HA-кластере Prometheus Alertmanager отправляйте алерты на все экземпляры, чтобы каждый получил их даже при отказе соседа. Экземпляры обмениваются журналом уведомлений и состоянием silences через gossip. Если кластер разделится, две изолированные стороны могут отправить уведомление об одном алерте. Это поведение fail-open: получатель должен быть готов к дубликатам. Например, интеграция с системой инцидентов может обновлять уже открытый инцидент по ключу из tenant, маршрута и идентификатора группы. Новый эпизод после закрытия должен открывать новый инцидент. Проверяйте получение уведомлений отдельно от их дедупликации. [Prometheus Alertmanager — High Availability](https://prometheus.io/docs/alerting/latest/high_availability/).

---

## See also

- [Prometheus и Grafana](../monitoring/prometheus-grafana-interview.md) — команды, cardinality, Alertmanager, remote write.
- [Observability](../monitoring/observability-interview.md) — сигналы сервиса, SLO и правила полезного алертинга.
- [System Design Q23](system-design-interview.md#q23-как-спроектировать-observability-для-системы) — наблюдаемость в дизайне одного сервиса.
