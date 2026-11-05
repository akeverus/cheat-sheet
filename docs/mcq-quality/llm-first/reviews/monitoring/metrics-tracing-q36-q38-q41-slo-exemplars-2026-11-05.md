# Metrics Q36/Q38/Q41 и Q9 — SLO, exemplars и границы данных

Дата: 2026-11-05. Прочитаны видимые вопросы, актуальные секции теории, A/B/C/D и sections трёх новых B0. Q9 уже проходил авторскую проверку; сейчас изменён только A.sections.how_it_should_be. Ключи Q36 D / Q38 C / Q41 A и все номера, block_idx, question_text и порядок сохранены. Автор видел ключ; независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `305d2904954697faed321b3c572e8adebdb53089a7f13750cbe09694c46fc393`; baseline `4cf17933ecc319b53fdd4f19ef9c8d184e518e9fed4a7c7fde63db426897f496`. Изменены только Q9/Q36/Q38/Q41; остальные 37 вопросов сохранены.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, прочитанный SHA-256 `ed2cbb4941da7421658e2a8e9dcb40f13385b138c510878735c59c97b1e12063`. Перед записью сверены хеши четырёх секций. Markdown не изменялся.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q36 | универсальное SLA≤SLO≤100% | показатель и цель переставлены | бюджет принят за денежные расходы | показатель, цель с окном, обязательства и допустимая ненадёжность |
| Q38 | exemplar принят за среднее | trace ID якобы выводится из имени метрики | exemplar с reference и настроенный переход | формат якобы включает storage и запись traces |
| Q41 | возможные направления по пользовательскому результату | poll принят за возраст результата | успешные retry подменяют логические jobs | успешный write принят за durability |

## Содержательные результаты

Q36: убрана универсальная процентная иерархия SLA/SLO; сравниваются конкретные показатели, окна и исключения. Для good/total цель s даёт bad budget total*(1-s). Бюджет миллиона запросов при 99.9% равен 1000 плохих запросов. 43.2 минуты получаются только для отдельно определённого временного SLI за ровно 30 суток. Burn rate нормирован на допустимую долю bad; freeze — решение согласованной policy, а не автоматическое следствие определения. Порог 14 не объявлен ошибкой только потому, что в Workbook приведён 14.4. [SRE definitions](https://sre.google/sre-book/service-level-objectives/), [burn rate](https://sre.google/workbook/alerting-on-slos/), [пример policy](https://sre.google/workbook/error-budget-policy/).

Q38: общий exemplar не обязан иметь trace ID, а для перехода к trace нужна пригодная ссылка. Histogram не объявляется необходимым для всех exemplars; counters тоже поддерживаются. Exemplar не является доказательством причины p99. Формат передачи отделён от storage, настройки Grafana и сохранения trace. Пример назван фрагментом OpenMetrics, trace/span IDs согласованы с W3C. Неподтверждённая универсальная настройка exemplars-sampling убрана. [OTel model](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exemplars), [OpenMetrics 1.0](https://prometheus.io/docs/specs/om/open_metrics_spec/), [Grafana](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/).

Q41: варианты проверяют именно пользовательский результат и разные ошибки измерения. Last poll и offset lag не подменяют возраст обработанного результата; retry не удваивает логические задания; успешный write не доказывает сохранность позже. Throughput в записях/сек отделён от доли обработанного входа. Рекомендация небольшого набора типов SLI не превращается в жёсткий лимит 3–5; blackbox-проба может реализовывать SLI. [Kafka 3.7.2](https://kafka.apache.org/37/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html), [SRE implementations](https://sre.google/workbook/implementing-slos/).

Q9 A: заменено безусловное «отбрасывает трейсы целиком». Отбор данных и сжатие различаются; полнота выбранного trace зависит также от инструментации, политики и доставки. Тексты вариантов Q9 и ключ C не менялись.

Источники прочитаны 2026-11-05. Отдельно из локального cached jar `spring-boot-actuator-autoconfigure-3.3.2.jar` прочитана META-INF/spring-configuration-metadata.json: exemplar properties не найдены. Это ограниченный вывод по Boot 3.3.2, не заявление о всех версиях или невозможности собственного binder. SHA jar/metadata сохранены в `metrics-slo-exemplars-boot-metadata-check.json`. ПО не устанавливалось.

## Проверки и пределы

verify-mcq-json PASS: 41 Q / 41 B, semantic review NOT_RUN. У новых Q36/Q38/Q41 нет anti-tell/SKEL findings; surface char ratios 1.08–1.12. Все варианты каждого блока имеют сопоставимую грамматику и объём; неверные требуют предметного различения показателя, контекста, состояния данных или единиц. Проверка выполнена с доступным ключом и не доказывает независимую неугадываемость.

Общие gates темы остаются FAIL: parity OPTION_LENGTH_RATIO=2.67 (Q32; 6 блоков выше порога), anti-tell 1/41 (Q32), SKEL hard=6/warn=6. Linkage errors=1/warnings=3: Q9→Q24 остаётся открытым для независимой проверки; предупреждения относятся к Q29/Q32/Q35. Остальные проверки parity проходят, style guessability=0.297. Struct-scan указывает несколько осей, его exit 0 не равен приёмке. Пороги не менялись и padding не добавлялся.

Decimal-проверка подтвердила бюджет 1000 запросов, 43.2 минуты в отдельной временной модели и burn rate 10. IDs проверены на длины 32/16 lowercase hex и ненулевые значения. Это не запуск PromQL/promtool, registry/Collector, exemplars storage, Kafka или Grafana. Логи `metrics-slo-exemplars-checks.json` и check-0..6.log, статическая арифметика `metrics-slo-exemplars-static-checks.json` сохранены в task-2. Работающий сервис не менялся.

Совокупный авторский охват metrics — **33/41**: Q1–Q28, Q36–Q39 и Q41. Остались Q29–Q35 и Q40. Полная тема, независимая приёмка и FINAL не объявляются завершёнными. Пакет подготовлен отдельно от runtime-исправления block question_text.
