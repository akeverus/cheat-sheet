# Авторская проверка теории: metrics Q2–Q5

Дата: 2026-11-05. Проверены только Q2/Q3/Q4/Q5 из 41 вопроса и их
четыре B0 целиком: вопрос, A/B/C/D и sections. Остальные 37 вопросов
**NOT REVIEWED** этим пакетом. Ключи известны; независимая слепая
MCQ-приёмка, Phase A/B и `FINAL` не заявляются.

## Снимок

- MD: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`.
- MD SHA-256: `03a8b6eba467c3df61b6e7b6c37345ac98a114682fe6bc16b0f76d80848db398`.
- Исходный MD SHA-256: `f236dc3cbf301adf245cbb54f821f5c55452e66f41c6e79887f227472250dd16`.
- Seed: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`.
- Seed SHA-256: `38b3fc8454ecacd220435e781b90618f83f7be06f5a12fb803d50f0040604afe`.

Прочитан [авторский отчёт MCQ Q1–Q6](../mcq-quality/llm-first/reviews/monitoring/metrics-tracing-q1-q6-foundations-2026-11-05.md).
Этот пакет закрывает только четыре переданных замечания теории Q2–Q5;
Q1/Q6 остаются вне scope. Q2/Q3/Q4 парного JSON совпадают с авторским
снимком commit `bf5bacd3`; Q5 повторно прочитан после `471729fa6`,
включая изменённые B.example и D.if_it_were_true.

## Закрытые замечания

| Секция | Изменение и проверенная граница | Парный MCQ |
|---|---|---|
| Q2 | Обычный HTTP scrape — pull, но отдельно включаемые receivers существуют. up=0 означает неудачный опрос, а не доказанный crash. SD обнаруживает динамические targets, Operator обрабатывает ServiceMonitor/PodMonitor; короткую задачу scrape всё равно может пропустить. Упомянуто согласование форматов. | B и sections согласованы с этими условиями. |
| Q3 | Registry хранит состояние измерений и готовит экспорт, но сам не создаёт доступный HTTP endpoint. Уточнены Actuator, exposure, доступ, binders и backend-specific настройки. YAML для Boot 3.3.x использует slo вместо прежнего sla. Длинный PaymentService заменён кратким примером API, без обещания полной реализации приложения. | D и sections согласованы; автоматические meters зависят от окружения. |
| Q4 | Summary sum/count отделены от локальных quantiles; histogram quantile — оценка. Timer/DistributionSummary не тождественны одному типу экспорта. Клиентские percentiles и histogram показаны разными meters. Добавлены counter reset и удержание источника gauge. | B и объяснения поддерживаются теорией; неверные модели raw histogram, gauge/counter и объединения локальных quantiles опровергаются. |
| Q5 | Убрано обещание полной цепочки и немедленного определения единственной причины. Указаны покрытие, propagation, sampling/delivery и span links; поиск медленных запросов по duration отделён от открытия известного trace ID. Gantt с неоднозначными единицами заменён простой sequence-иллюстрацией. | B и sections согласованы. Обе поздние правки Q5 из 471729fa6 прочитаны; новый finding не открыт. |

Все четыре исходных замечания теории — **RESOLVED_AUTHOR_SOURCE_REVIEW**.
Это не принятие всего MCQ-файла или независимая оценка формы.

## Проверки примеров

Тело Java-примера Q4 извлечено без изменений и обёрнуто imports,
созданием registry и assertions. Локальные cached jars: Micrometer
**1.13.2**, Prometheus Java Client **1.2.1**, Corretto **22.0.2**.
Без загрузки зависимостей и без HTTP server выполнены проверки:

- Counter увеличивается; gauge видит 3, затем 7 из живого AtomicInteger.
- Timer хранит count=1 и duration=12ms; DistributionSummary — count=1,
  totalAmount=512.
- Histogram-настройки дают histogram/bucket экспозицию; тот же API
  DistributionSummary не обязан экспортироваться как Summary.
- Отдельный client-percentile timer экспортирует Summary и quantile 0.95.

Результат **PASS**; scrape здесь — формирование строки в памяти,
не внешний опрос Prometheus. Код, exposition и stdout:
`/tmp/metrics-core-theory-20261105/runtime/`.

Nearest-rank контрпример Python: локальные p99 1 и 1000 имеют среднее
500.5, а объединённая выборка — p99 1000. Это математическая иллюстрация,
не результат PromQL или алгоритма client-side percentile Micrometer.

В configuration metadata локального Boot **3.3.2** проверены property
`management.metrics.distribution.slo` и default web exposure `[health]`.
Это не запуск Binder/Actuator. YAML-фрагменты и frontmatter разобраны
Ruby Psych; структурная проверка двух paired файлов: **PASS**,
46 Q / 48 блоков. Остальные вопросы не принимаются этим результатом.
Сохранены номера/IDs, проверены scope, локальные ссылки/anchors,
fences и адресный diff --check.

## Первичные источники

- [Prometheus overview](https://prometheus.io/docs/introduction/overview/),
  [jobs/instances](https://prometheus.io/docs/concepts/jobs_instances/),
  [receivers](https://prometheus.io/docs/prometheus/latest/querying/api/#remote-write-receiver),
  [content negotiation](https://prometheus.io/docs/instrumenting/content_negotiation/).
- [Prometheus Operator](https://prometheus-operator.dev/docs/developer/getting-started/),
  [Pushgateway use cases](https://prometheus.io/docs/practices/pushing/).
- [Boot 3.3 metrics](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html)
  и [endpoint exposure](https://docs.spring.io/spring-boot/3.3/reference/actuator/endpoints.html).
  Страницы маркированы **3.3.13**; проектный каталог указывает Boot 3.3.2.
  Источник MCQ-автора для Boot 3.5.16 не выдан за установленный стек.
- [Micrometer histograms/percentiles](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html),
  [Prometheus registry](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html),
  [gauges](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html).
  Текущие страницы **1.17.2**, runtime harness — 1.13.2; версии различаются.
- [Prometheus histograms](https://prometheus.io/docs/practices/histograms/).
- [OTel traces](https://opentelemetry.io/docs/concepts/signals/traces/),
  [propagation](https://opentelemetry.io/docs/concepts/context-propagation/),
  [sampling](https://opentelemetry.io/docs/concepts/sampling/).

## Ограничения

Prometheus server/PromQL, Boot HTTP endpoint/security, Kubernetes
Operator, trace exporter/backend, native histogram и load tests
**NOT RUN**. Mermaid проверен чтением, не отрендерен. Источники latest
подвижны; вывод относится к чтению 2026-11-05. Docker не запускался.

Пакет: MD и этот отчёт, список с SHA в
`/tmp/prepared-metrics-foundations-theory-20261105.json`.
Старые Q17/Q39 и остальные секции сохранены. JSON, общие планы,
registries/checkpoints и чужие файлы не редактировались.
