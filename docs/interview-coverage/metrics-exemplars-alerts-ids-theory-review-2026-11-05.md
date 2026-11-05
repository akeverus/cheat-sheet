# Metrics — exemplars, alert states, IDs и применение RED/USE

Дата: 2026-11-05. Область **Q21/Q22/Q23/Q26**, расширенная на **Q38** после нового linkage-сигнала и чтения его теории/вариантов. Прочитаны пять разделов и все 20 MCQ-вариантов B0 с sections, с видимыми ключами. Это авторская проверка по источникам и ограниченный runtime, не независимая blind-приёмка, Phase A/B или FINAL. Другие 36 Q не принимаются этим отчётом.

Теория: [metrics-tracing-interview.md](../../cheatsheets/interview/monitoring/metrics-tracing-interview.md), SHA-256 `217611679ed2c353060dc7be02cb99fd9a731285c0293f3e1ca6d1f2ac280ae4`.
MCQ только чтение: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `4cf17933ecc319b53fdd4f19ef9c8d184e518e9fed4a7c7fde63db426897f496`. Полный снимок и нормализованные хеши пяти вопросов сохранены в локальном манифесте. JSON не изменялся.

## Исправления

**Q21:** exemplar — пример наблюдения с контекстом; в OTel trace/span IDs optional. Его значение уже включено в histogram/sum, повторно не прибавляется. Убрана гарантия причины p99. Синтетическая строка названа фрагментом OpenMetrics 1.0 и согласована с W3C IDs; не объявляется полной экспозицией. Удалён неполный Java config с необъявленным spanContextSupplier и смешением client APIs. Пояснены version-specific SpanContext / legacy SpanContextSupplier и необходимость проверки всей цепочки. [OTel data model](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exemplars), [OpenMetrics 1.0](https://prometheus.io/docs/specs/om/open_metrics_spec/), [Boot 3.3 metrics](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html).

**Q22:** добавлены inactive/pending/firing, непрерывность for по label set, keep_firing_for и различие с Alertmanager group_wait. Активность определяется наличием элемента вектора, в том числе со значением 0; показана ловушка up == bool 0. Удалена непроверенная интеграция Slack/PagerDuty с фиктивными адресами; оставлен короткий YAML правил и разделение состояния/маршрутизации/доставки. Heap alert согласован с Q12: положительный max, pool scope. Missing data не означает здоровое состояние. [Alerting rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/), [operators](https://prometheus.io/docs/prometheus/latest/querying/operators/), [Alertmanager](https://prometheus.io/docs/alerting/latest/alertmanager/).

**Q23:** trace ID в лог-примере исправлен с 16 на 32 lowercase hex для W3C; span IDs — 16 и ненулевые. Сохранены parent/links и отдельность собственного span ID от переданного parent-id. XML упрощён до одного действующего ConsoleAppender без необъявленной Logstash-зависимости. Шаблон не заполняет MDC сам и не гарантирует полноту логов/trace; перенос в async требует проверки интеграции. [W3C](https://www.w3.org/TR/trace-context/), [OTel traces](https://opentelemetry.io/docs/concepts/signals/traces/), [Boot logging correlation](https://docs.spring.io/spring-boot/3.3/reference/actuator/tracing.html).

**Q26:** RED описывает запросы сервиса, USE — ресурс. Убраны обязательность исчерпывающего минимума, гарантия корневой причины, привязка Duration только к histogram и обещание всех USE-сигналов от JVM binders. Добавлены прикладной пример, ограничения данных и отличие от всех четырёх golden signals SRE; ссылка на определения/PromQL Q19 сохранена. [RED](https://grafana.com/blog/the-red-method-how-to-instrument-your-services/), [USE](https://www.brendangregg.com/usemethod.html), [Google SRE](https://sre.google/sre-book/monitoring-distributed-systems/).

**Q38:** вместо повторной универсальной «магии p99» показана практическая проверка producer → exposition → сбор/storage → Grafana → trace backend. Убраны histogram как обязательное условие каждого exemplar, выдуманная универсальная настройка exemplars-sampling и обещание прямого извлечения причины. В metadata конкретного Boot **3.3.2** свойства management.prometheus.metrics.export.exemplars-sampling нет; это ограниченный вывод по прочитанному jar, не утверждение о всех будущих версиях. В текущих docs query_exemplars помечен experimental; список scrape protocols не включает storage автоматически. [Boot](https://docs.spring.io/spring-boot/3.3/reference/actuator/metrics.html), [Prometheus query_exemplars](https://prometheus.io/docs/prometheus/latest/querying/api/#querying-exemplars), [Grafana](https://grafana.com/docs/grafana/latest/fundamentals/exemplars/).

## Передать MCQ — Q38 OPEN

Q21 A / Q22 C / Q23 C / Q26 C сопоставлены с обновлённой теорией; фактических противоречий этих ключей не выявлено. Неугадываемость и независимое linkage-review не принимались.

В старом **Q38 B0**, который ещё не проходил текущую авторскую серию MCQ, остаются реальные расхождения:

- **A.what_actually / if_it_were_true / how_it_should_be** определяют любой exemplar через обязательный trace ID; общий exemplar этого не требует.
- **B.how_it_should_be** требует и активную трассировку, и histogram для всех exemplars; counter подтверждён отдельной локальной проверкой. Для перехода к trace контекст нужен, но это не общее определение любого exemplar.
- **C.explanation** обещает, что exemplar открывает запрос и его причину по p99. **C.example** следует назвать выбранным фрагментом OpenMetrics; короткие IDs не годятся как пример W3C, хотя сами произвольные значения labels не нарушают общую грамматику OpenMetrics.
- **C.edge_cases** содержит неподтверждённую настройку exemplars-sampling и ложное обязательное percentiles-histogram. **D.sections** распространяют требования конкретного OpenMetrics text scrape-пути на все случаи; scope и storage нужно разделить. Сам тезис D.text «всегда 0.0.4» остаётся неверным.

Нужна содержательная правка Q38 JSON владельцем MCQ. Ключ C не менялся автором теории; его применимость следует формулировать для связи с trace, а не копировать старые sections.

## Проверки и пределы

IDs/порядок 41 Q, header/TOC, явные anchors и 36 остальных разделов сохранены. YAML/XML, парность fences, локальные ссылки/anchors и whitespace PASS. IDs/значение exemplar проверены статически: это не W3C/OpenMetrics conformance suite. verify-mcq-json: 41 Q / 41 блок PASS, semantic review NOT_RUN.

**Runtime 1:** Corretto **22.0.2**, Micrometer **1.13.2**, Prometheus Java Client **1.2.1**, существующий Gradle cache. Синтетический sampled SpanContext с фиксированными IDs передан registry: OpenMetrics содержит exemplars у counter и histogram; count не увеличен второй раз; trace/span IDs отдельно от labels рядов; classic default scrape не содержит этих ID. PASS. Это не настоящий SDK span, HTTP endpoint, Prometheus storage или проверка sampling/retention.

**Runtime 2:** точный XML Q23 извлечён без изменений, Logback **1.5.7** / SLF4J **2.0.13** / Corretto **22.0.2**. При вручную заполненном MDC IDs отображаются, после MDC.clear поля пусты. PASS. Автоматическое MDC/tracing/async propagation этим не проверялось. После добавления Q38 XML остался побайтово прежним.

**Статика Boot:** прочитана META-INF/spring-configuration-metadata.json из cached actuator-autoconfigure **3.3.2**, сохранены jar SHA и список соответствующих properties. Не запускалась проверка binding неизвестного свойства. Promtool/amtool не установлены; PromQL/alert states/routing/delivery runtime NOT_RUN.

Linkage на текущем Markdown: **exit 1, errors=1, warnings=4**. После фактических правок исчезли Q21→Q14, Q26→Q19 и новый промежуточный Q38→Q21. Остался прежний Q9→Q24; warnings Q29/Q32/Q35/Q41. Лексический scanner не заменяет смысловую проверку; пороги и алгоритм не менялись, padding не добавлялся. Исчезновение сигнала Q38 не закрывает реальные ошибки его старого JSON, перечисленные выше.

Артефакты: `/tmp/metrics-exemplars-alerts-ids-theory-20261105/` — before, static-checks.json, boot-metadata-check.json, linkage.log/.json и runtime (ExemplarExportCheck.java, openmetrics.txt/classic.txt, exemplar-execution.json, точный logback-example.xml, LogbackMdcCheck.java, mdc-output.txt, mdc-execution.json). Первичные источники прочитаны 2026-11-05; online OTel spec маркирован 1.62.0, Boot docs — 3.3.13, что отличается от версий локального runtime/metadata.

**NOT_RUN:** Prometheus/Alertmanager/Grafana серверы, реальный HTTP scrape/storage/trace backend, tracing bridge/Agent, async propagation и уведомления. Docker не вызывался и прежний отказ не обходился. SFPL/ANSC/RU, Phase A/B и FINAL не выполнялись; полная тема не принята.
