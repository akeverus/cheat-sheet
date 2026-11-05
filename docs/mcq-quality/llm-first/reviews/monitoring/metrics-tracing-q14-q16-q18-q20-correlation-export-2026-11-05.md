# Metrics Q14–Q16/Q18–Q20 — корреляция, экспорт и интерпретация

Дата: 2026-11-05. Авторская редакция шести B0 / 24 вариантов. Прочитаны видимые вопросы, соответствующая теория и все A/B/C/D с sections. Номера, question_text, block_idx, порядок и ключи D/B/A/C/B/D сохранены. Автор видел ключ; независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN. Это не приёмка всей темы.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `ac0daac52bec83815a17b8bb7f995dc25641d5793aafc4c3d2cd5c1d91ceb7bf`. Исходный SHA `201748123f79b384e6a4654671feca687dad49df0f41056075d1bbc1879c27a2`; изменены только Q14/Q15/Q16/Q18/Q19/Q20. Остальные 35 вопросов сохранены.

Прочитанная теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `8fa9eed72e9f3ee14c0a71f4a2cc0d891a64b45dd63115dedfdea40a5f9b8829`. При применении сверены хеши шести секций. Теория не изменялась этим автором; открытые расхождения перечислены ниже.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q14 | общая TSDB объявлена необходимой | trace ID превращён в label ряда | exemplar выдан за точную причину p99 | время/атрибуты и настроенная exemplar-связь |
| Q15 | любой backend объявлен взаимозаменяемым OTLP endpoint | tracing backends, сравнение версий | spans заменены логами | backends перепутаны с API |
| Q16 | два пути, согласованные настройки | ручной trace ID обязателен | management.* выдан за конфигурацию агента | sampling в приложении отрицается |
| Q18 | роли агрегатов и отдельного trace переставлены | p99 принят за идентификатор запроса | масштаб по метрикам, операции по traces | один успех выдан за восстановление всей нагрузки |
| Q19 | сервисные сигналы выданы за USE | RED сервисов и USE ресурсов | методологии выданы за функции | Duration вынесен из RED |
| Q20 | каждое изменение требует push | текстовые scrape-форматы отрицаются | готовая сериализация отрицается | HTTP exposition и настроенный scrape |

## Содержательная проверка

Q14: exemplar — пример отдельного наблюдения, а p99 — оценка квантиля за окно. Их соседство не доказывает, что выбранный trace определил p99; это вывод из различия этих моделей, а не обещание Grafana. Идентификатор exemplar отделён от labels временного ряда. Имя label и UID источника должны совпадать с фактическими данными и конфигурацией. Прямая ссылка не гарантирует, что trace сохранился. [Grafana exemplars](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/), [конфигурация datasource](https://grafana.com/docs/grafana/latest/datasources/prometheus/configure/).

Q15: убраны универсальные рейтинги «легче/богаче» и обещание смены любого backend одним URL. Jaeger v2 основан на Collector framework; совместимость приёма и экспорта проверяется отдельно. Два внутренних listener на 4317 допустимы в разных контейнерах, два одинаковых host bindings — конфликт. [Jaeger 2.23](https://www.jaegertracing.io/docs/2.23/), [Zipkin exporter](https://raw.githubusercontent.com/open-telemetry/opentelemetry-collector-contrib/main/exporter/zipkinexporter/README.md).

Q16: прежний C описывал допустимое ручное создание spans, поэтому не был однозначно неверным. Новый C проверяет реальную ошибку конфигурации: свойства management.* приложения не являются автоматически свойствами отдельного агента. В примере явно согласованы http/protobuf и HTTP endpoint. У Java Agent 2.0+ default — http/protobuf; один порт 4317 не выбирает gRPC. Сам пример конфигурации статический, не запускался. [Agent configuration](https://opentelemetry.io/docs/zero-code/java/agent/configuration/), [Boot 3.3 tracing](https://docs.spring.io/spring-boot/3.3/reference/actuator/tracing.html).

Q18: варианты различают масштаб, отдельное выполнение, квантиль и доказательство восстановления. Пояснения не обещают немедленный алерт, полную трассу, установленную причину по одному span или универсальный поток расследования. Три прежних категорических запрета заменены разными ошибками интерпретации.

Q19: исправлены sections неверного D, которые раньше опровергали Disk вместо текущего тезиса о Duration. busy/max отделён от очереди, обычное событие GC — от ошибки. У числителя/знаменателя доли ошибок нужна общая область. [USE — автор метода](https://www.brendangregg.com/usemethod.html), [RED — Tom Wilkie](https://grafana.com/blog/the-red-method-how-to-instrument-your-services/).

Q20: убрано встречное ложное обобщение «Protobuf не бывает на scrape». Поддерживаются разные протоколы; конкретный endpoint согласует формат с Prometheus. Registry отделён от HTTP-публикации; ServiceMonitor — от наличия Operator и selectors; Pushgateway — от универсального решения для всех коротких задач. [Content negotiation](https://prometheus.io/docs/instrumenting/content_negotiation/), [границы Pushgateway](https://prometheus.io/docs/practices/pushing/).

Источники прочитаны 2026-11-05; актуальная Jaeger docs перенаправила на 2.23, Spring Boot docs ограничены веткой 3.3. Эти документы не являются исполнением примеров локального проекта.

## Передать владельцу теории — OPEN

- Q14: убрать гарантию «именно тот trace, который дал выброс p99». Exemplar не представляет вычисленный квантиль. В YAML связь с datasourceUid=loki требует реально заданного UID; совместимость tracesToLogs проверять по выбранной версии Grafana.
- Q15: два сервиса публикуют host 4317:4317 — конфликт. Пример с Jaeger all-in-one:1.54 не проверен как production-ready; универсальное «сменить URL» и рейтинги продуктов заменить условиями протокола и версии.
- Q16: Agent 2.2.0 + endpoint :4317 без явного grpc расходятся с default http/protobuf. Выбрать согласованную пару protocol/endpoint; отделить конфигурацию агента от Boot и не объявлять 0.1 нормой для любого production.
- Q18: не обещать точную причину и строку кода по trace; учитывать sampling, пробелы и дополнительные доказательства. Exemplar не гарантирует связь с причиной агрегата.
- Q19: busy/max подписан как длина очереди, normal major GC count — как errors. Исправить семантику; согласовать scope запросов в RED.
- Q20: одна зависимость registry не гарантирует Actuator endpoint; ServiceMonitor/annotations не работают автоматически без настроенного обнаружения. Ограничить рекомендацию Pushgateway.

До синхронизации этих разделов цепочка «видимый вопрос → теория → варианты» полностью не принята. Markdown и работающий сервис не менялись.

## Проверки и пределы

Схема PASS: 41 Q / 41 B; semantic review NOT_RUN. git diff --check PASS. Для новых шести блоков anti-tell/SKEL findings отсутствуют; surface char ratios 1.04–1.16, никакой вариант не выделяется уникальной разметкой. Авторская проверка скелета и смысла проведена с известным ключом и не заменяет независимую проверку.

Общие gates темы остаются FAIL: parity OPTION_LENGTH_RATIO=2.67 (Q32, 10 блоков выше порога), anti-tell 4/41, SKEL hard=10/warn=12. Linkage errors=2/warnings=8: Q9 → Q24 и Q20 → Q2. Прежние ошибки Q7 → Q5 и Q12 → Q2 исчезли после параллельной правки теории. Q20 по смыслу проверяет Java registry/HTTP-публикацию, а не только модель pull из Q2; старое обещание автоматического endpoint в теории требует исправления. Лексический error не переименован в PASS. Struct-scan всё ещё отмечает несколько осей; независимая неугадываемость не доказана. Пороги не менялись, padding не добавлялся.

Точные логи: `metrics-next-six-final-checks.json` и check-0..6.log в рабочем пространстве task-2. Сохранены baseline и итоговый JSON, теория и хеши секций. Docker Compose, Grafana, exporter/receiver и PromQL не запускались. Приложение и его данные не обновлялись. Staging и коммиты не выполнялись; очередь индекса остаётся у шпаргалок.
