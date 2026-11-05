# Metrics Q21/Q22/Q23/Q26 — exemplars, алерты и идентификаторы

Дата: 2026-11-05. Авторская редакция четырёх B0 / 16 вариантов после чтения видимых вопросов, полной соответствующей теории и всех sections. Ключи A/C/C/C, question_text, block_idx, номера и порядок сохранены. Изменены только Q21/Q22/Q23/Q26; остальные 37 вопросов сохранены. Автор видел ключ: независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN. Это не полная приёмка темы.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, итоговый SHA-256 `4cf17933ecc319b53fdd4f19ef9c8d184e518e9fed4a7c7fde63db426897f496`. Baseline `ac0daac52bec83815a17b8bb7f995dc25641d5793aafc4c3d2cd5c1d91ceb7bf` — уже закоммиченный предыдущий пакет. Прочитанная теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `8fa9eed72e9f3ee14c0a71f4a2cc0d891a64b45dd63115dedfdea40a5f9b8829`. Перед применением проверены хеши четырёх секций. Теория и индекс не изменялись этим автором.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q21 | пример наблюдения с контекстом, trace ID необязателен | exemplar заменяет распределение | измерение прибавляется второй раз | контекст exemplar создаёт новый ряд |
| Q22 | for суммирует разрозненные всплески | роли Prometheus/Alertmanager переставлены | правила и состояния отдельно от обработки уведомлений | нулевой элемент вектора принят за неактивность |
| Q23 | spanId хранит список операций | назначения ID переставлены | traceId группирует spans, spanId идентифицирует операцию | root является собственным parent |
| Q26 | совместные методологии гарантируют причину | сервис и ресурс переставлены | RED запросов, USE ресурсов | методологии отличаются только экспортом |

## Содержательные исправления и источники

Q21: уточнено определение. В OTel exemplar содержит значение, время и контекст; trace/span IDs необязательны. Значение уже входит в агрегат, повторно учитывать его нельзя. Убраны обещание точной причины p99 и универсальная необходимость latency histogram. Показанная строка — фрагмент OpenMetrics, а не полная экспозиция и не Prometheus text 0.0.4. OpenMetrics допускает exemplars также у counter total. Контекст exemplar отделён от измерений временного ряда. [OTel Metrics Data Model 1.62.0](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exemplars), [OpenMetrics 1.0](https://prometheus.io/docs/specs/om/open_metrics_spec/).

Q22: for относится к непрерывному pending для конкретного набора labels на последовательных оценках. group_wait — отдельная задержка начального уведомления, silence/inhibition не изменяют состояние Prometheus rule. Active определяется наличием элемента результирующего вектора, а не его ненулевым значением. Поэтому сравнение с bool может сохранить нулевые элементы и не подходит как обычный фильтр порога. Добавлены границы missing data, keep_firing_for и доставки resolved-уведомлений. [Prometheus alerting rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/), [alerting overview](https://prometheus.io/docs/alerting/latest/overview/).

Q23: исправлен 16-символьный traceId в примере для W3C. Различены собственный spanId, необязательный parent и parent-id вызывающей стороны в переданном traceparent. Не заявлены полное дерево каждого запроса, глобальная уникальность одного spanId или автоматическое заполнение MDC одним Logback-шаблоном. Дополнительные links могут связывать traces. [W3C Trace Context Recommendation](https://www.w3.org/TR/trace-context/), [OTel traces](https://opentelemetry.io/docs/concepts/signals/traces/).

Q26: исправлены обещание полной картины и автоматического нахождения причины, обязательность конкретных exporters и вывод, что JVM binders обеспечивают все USE-сигналы. RED и USE совместимы, но не исчерпывают доменную диагностику. RED не тождественен всем четырём golden signals SRE: saturation отдельно присутствует в последних. Duration не ограничена одним видом инструмента. [RED/USE — изложение Tom Wilkie](https://grafana.com/blog/the-red-method-how-to-instrument-your-services/), [USE — Brendan Gregg](https://www.brendangregg.com/usemethod.html), [Google SRE, Monitoring Distributed Systems](https://sre.google/sre-book/monitoring-distributed-systems/).

Первичные источники прочитаны 2026-11-05. Сетевые сервисы, оплачиваемые API и агенты не запускались.

## Владельцу теории — OPEN

- **Q21:** trace/span IDs не обязательны для любого exemplar. Убрать «именно тот trace вызвал скачок и показывает причину». Включение зависит от конкретных registry/client/export/storage и версии; одного набора зависимостей недостаточно как доказательства работоспособности. Фрагмент Java вызывает не показанный spanContextSupplier(): перед учебным использованием проверить полноту и API выбранной версии. Формат строки назвать OpenMetrics, длину trace ID для W3C согласовать с Q23; не заявлять запрет exemplars для counters.
- **Q22:** добавить inactive/pending/firing, непрерывность for по label set, различие for/group_wait и отдельную проверку отсутствия данных. Непустой вектор с нулевым значением тоже активирует alert; объяснить ловушку bool. Согласовать heap-alert с ограничениями Q12: pools/undefined max, а не считать любую дробь долей всей heap. Конфигурации не запускались.
- **Q23:** пример traceId содержит 16 hex; для W3C v00 нужно 32. Уточнить контекст parent, links и фактическое заполнение MDC. Не обещать, что фильтр по traceId найдёт все логи при неполном покрытии или потере контекста.
- **Q26:** заменить «обязательный минимум» и «закрывают причину» на область применения и проверку гипотез. JVM binders не гарантируют очередь/ошибки каждого ресурса; Duration не обязана собираться только гистограммой, RED не равен всему набору golden signals.

До этой синхронизации полная цепочка «вопрос → теория → варианты» не принята. Изложение теории не исправлялось MCQ-автором.

## Проверки и пределы

verify-mcq-json PASS: 41 Q / 41 B, semantic review NOT_RUN. У новых четырёх блоков нет anti-tell/SKEL findings; surface ratios 1.05–1.15. В Q26 все варианты приведены к одинаковому сопоставлению двух тезисов, без добавления padding. Авторская проверка смысла и формы проведена с известным ключом и не является независимой.

По всей теме остаются FAIL: parity OPTION_LENGTH_RATIO=2.67 (Q32; 8 блоков выше порога), anti-tell=2/41 (Q32/Q41), SKEL hard=7/warn=9, linkage errors=2/warnings=5. Errors Q9→Q24 и Q20→Q2 остаются. Новый Q26:D отмечен LOW-COVERAGE: тезис о различии только транспортом опровергается разными объектами и сигналами RED/USE, а не знанием названия exporter. Это авторское обоснование; независимая проверка linkage открыта, warning не скрыт. Struct-scan по-прежнему указывает стилевые оси; статистика не доказывает неугадываемость.

Примеры IDs статически проверены Python: 32/16 lowercase hex и ненулевые значения; строка exemplar содержит согласованный trace ID и значение 0.48 для le=0.5. Это не parser/conformance suite OpenMetrics/W3C, не запуск MDC и не тест доставки trace. Prometheus/Alertmanager, Grafana, Java и exporters не запускались; runtime/импорт не проверялись и работающий сервис не менялся.

Точные логи: `metrics-exemplars-alerts-ids-final-checks.json` и check-0..6.log в task-2. Сохранены baseline, итоговый JSON и теория с хешами секций. Staging и коммитов нет; индекс остаётся за шпаргалками. Совокупный авторский охват metrics — 30/41 вопросов: Q1–Q28, Q37 и Q39. Оставшиеся Q29–Q36/Q38/Q40/Q41 ещё требуют содержательной проверки; независимая приёмка всей темы NOT_RUN.

## Повторная linkage-проверка после параллельного обновления теории

В ходе подготовки отчёта владелец теории обновил Q14/Q15/Q16/Q18/Q19/Q20. Новый SHA Markdown `3fd149f530cd6a1c96a59f1a01a6d211a2f1d78b45855ba97ee4c32ba015ad1d`; секции текущего пакета Q21/Q22/Q23/Q26 не изменились. На неизменном JSON повторён только linkage-scan: errors=3/warnings=5, Q20→Q2 исчез, теперь отмечены Q9→Q24, Q21→Q14 и Q26→Q19. Новые соседские ошибки не объявлены PASS: definitions exemplars и RED/USE пересекаются с обновлёнными разделами, а теория Q21/Q26 всё ещё требует указанных исправлений. Необходимо сверить эти пары после синхронизации и независимо оценить linkage. Новые логи и хеши: `metrics-exemplars-alerts-ids-posttheory-linkage.log/.json`. Прежняя статистика выше относится к явно указанному snapshot теории, не к новой редакции.
