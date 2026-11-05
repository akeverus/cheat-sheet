# Metrics Q7/Q8/Q10–Q13 — context, histogram и JVM

Дата: 2026-11-05. Подготовлена авторская редакция шести блоков B0 / 24 вариантов. Прочитаны видимые вопросы, вся соответствующая теория, A/B/C/D и sections. Ключи A/D/C/D/C/A, номера, block_idx, question_text и порядок сохранены. Автор работал с доступным ключом: это не независимая приёмка, не Phase A/B и не FINAL.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `201748123f79b384e6a4654671feca687dad49df0f41056075d1bbc1879c27a2`.

Прочитанная теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `03a8b6eba467c3df61b6e7b6c37345ac98a114682fe6bc16b0f76d80848db398`. Markdown не редактировался. При записи проверены хеши именно шести секций; посторонние параллельные изменения не перезаписывались. Остальные 35 блоков не принимаются этим отчётом.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q7 | операция и связанный набор span’ов | span/trace переставлены | trace сводится к итоговому времени | отношения parent/links отрицаются |
| Q8 | parent/flags восстанавливаются по timestamp | новый trace ID на каждом сервисе и связь по времени | локальный ThreadLocal доступен другому процессу | согласованные inject/extract HTTP headers |
| Q10 | диагностический JSON принят за scrape-экспозицию | аннотация без исполняющей интеграции | Actuator, registry, exposure и meters | exposure только health публикует prometheus |
| Q11 | сырые значения вместо агрегации | локальные квантили вместо бакетов | экспонента относится к counts во времени | геометрическая шкала и scale |
| Q12 | внешний JMX-exporter объявлен обязательным | live threads требуют JFR | JVM binders и проверенный экспорт | GC не содержит данных длительности |
| Q13 | предметное измерение под диагностическую задачу | ID не увеличивают число рядов | custom требует отдельной TSDB | только переименование JVM meters |

## Содержательные исправления

Q7: не обещается полный путь каждого запроса и обязательный HTTP root. Parent и span links различаются; links могут связывать разные traces. Перекрывающиеся интервалы не предлагается механически складывать в задержку запроса.

Q8: старый explanation содержал невалидное значение `00-abc123-span1-01`. Заменён валидным примером version 00; различены remote parent и собственный span ID нового span, необязательный tracestate и sampled flag без гарантии хранения. Все альтернативы теперь относятся к HTTP/W3C, поэтому имя стандарта не выделяет только ключ. Автоматическая propagation не обещана для любого клиента без соответствующей инструментации.

Q10: registry отделён от публикации endpoint, /actuator/metrics — от scrape-экспозиции. Исправлено sla → slo для обсуждаемого Boot 3.x. @Timed сам не исполняет измерение; TimedAspect требует действующего механизма AOP/инструментации, а proxy может обходиться при self-invocation. Эти условия не подаются как единственный возможный способ программно измерять Timer.

Q11: объяснена шкала OTel с base = 2**(2**(-scale)), а не экспоненциальный рост counts. Micrometer publishPercentileHistogram с min/max expected values не выдан за автоматический выбор OTel ExponentialHistogram или Prometheus native histogram. Поддержка формата и разрешение зависят от SDK/exporter/backend; универсальные выигрыши в памяти/точности не обещаны.

Q12: проверен исходник Micrometer 1.13.2 JvmThreadMetrics и Boot 3.3.1 JvmMetricsAutoConfiguration. В прочитанном binder регистрируются peak, daemon, live, started и state metrics, но нет jvm.threads.deadlocked; поэтому прежний alert не назван гарантированным default. Это ограниченный вывод по указанным исходникам, не запрет отдельной пользовательской метрики дедлоков. Среднее GC отделено от максимума/отдельной паузы; у heap max нужны определённое значение, согласованные pools и scope.

Q13: пример gauge теперь завершается register(registry), оговорены достижимость источника, дискретность наблюдения и монотонность FunctionCounter. Неограниченные labels создают расходы и риск, а не обязательный OOM. Конечность одного label не гарантирует допустимость всех сочетаний.

## Передать владельцу теории — OPEN

- **Q7:** старые «всё дерево», «полный путь» и «длительность сразу показывает узкое место» согласовать с границами Q5: неполная инструментация/доставка, overlap и дополнительные links. Root не обязан быть HTTP; parent не обязателен у root.
- **Q8:** исправить схематический traceparent с abc123/span1 либо явно отделить её от wire-формата. 32/16 hex IDs не могут быть нулевыми; sampled flag не гарантирует запись. Убрать обещание автопропагации для всех перечисленных клиентов без конфигурации. Доли sampling в dev/prod — пример, не универсальная норма.
- **Q10:** sla заменить на актуальное для выбранной Boot версии slo; пояснить условия регистрации meters и перехвата @Timed, не объявляя один bean достаточной гарантией любого вызова.
- **Q11 — приоритет:** текущий Java пример настраивает percentile histogram и диапазон expected values, а не доказывает экспорт ExponentialHistogram/native. Переписать пример под явно выбранную версию и registry либо оставить алгоритмический пример scale/base. Не выдавать Timer default за обещание нативной экспоненциальной экспозиции.
- **Q12 — приоритет:** не обещать jvm_threads_deadlocked_threads как стандартный meter без проверки версии/конфигурации. Алерт на отношение GC sum/count назвать средним по окну, не каждой паузой. Уточнить heap pools, undefined max, отсутствие данных и настройку endpoint. JSON Grafana сравнивает байтовую метрику с порогами 0.8/0.9 без явного процентного режима: согласовать единицу, expr и semantics threshold перед объявлением панели heap utilization.
- **Q13:** API examples предполагают регистрацию и срок жизни источников измерений; конечные labels всё равно требуют бюджета сочетаний. Обязательный OOM не заявлять.

Пока эти расхождения открыты, полная учебная согласованность пакета не принята. Markdown и runtime этим автором не менялись.

## Проверки и пределы

verify-mcq-json PASS: 41 Q / 41 блок; semantic review NOT_RUN. У шести текущих блоков нет findings anti-tell/SKEL. Linkage сохраняет errors Q7 → Q5 и Q12 → Q2, а также NOVEL-TERM-WRONG ThreadLocal в Q8. Автор сопоставил каждый вариант с видимым вопросом: Q7 проверяет роли span/trace; Q12 — именно memory/thread/GC binders и публикацию. Предварительная настройка экспорта изложена в Q10/Q3, но старый текст Q12 обещает автоэкспорт; это дополнительная причина синхронизировать теорию, а не копировать неверное обещание ради лексического gate. Модель локального ThreadLocal опровергается межпроцессным inject/extract, а не только узнаванием имени. Независимый linkage-review остаётся открытым; errors не переименованы в PASS.

По теме: anti-tell 6/41; SKEL hard_fails=13, warn_blocks=16; linkage errors=3, warnings=11. Answer-parity exit 1: OPTION_LENGTH_RATIO=2.67 (Q32, 11 блоков выше порога). Остальные проверки gate проходят, включая ABSOLUTE_MARKER_GAP=0.244 и STYLE_GUESSABILITY=0.394. Поверхностная неугадываемость не доказана; struct-scan продолжает отмечать несколько осей. Пороги не менялись.

Формат примера traceparent статически проверен Python: 55 символов, заданные длины hex ID, ненулевые IDs и sampled bit. Это не полный W3C conformance suite и не запуск propagation. Вычисления base для scale 0/1 проверены Python, а не исполнением Histogram SDK. Точный JSON SHA и логи: metrics-context-histogram-jvm-final-checks.json и check-0..6.log в рабочем пространстве. Java/JVM binders, AOP, Grafana, PromQL, exporter и Collector не запускались. Независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN. Подготовлено без staging/коммитов; резервирование индекса за шпаргалками сохраняется.

Первичные источники прочитаны 2026-11-05: [OpenTelemetry traces/links](https://opentelemetry.io/docs/concepts/signals/traces/), [W3C Trace Context Recommendation](https://www.w3.org/TR/trace-context/), [OTel ExponentialHistogram](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exponentialhistogram), [Micrometer histograms](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html), [TimedAspect](https://docs.micrometer.io/micrometer/reference/concepts/timers.html), [JvmThreadMetrics v1.13.2](https://raw.githubusercontent.com/micrometer-metrics/micrometer/v1.13.2/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/jvm/JvmThreadMetrics.java), [Boot v3.3.1 JVM auto-configuration](https://raw.githubusercontent.com/spring-projects/spring-boot/v3.3.1/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/metrics/JvmMetricsAutoConfiguration.java). Открытая спецификация OTel маркирована 1.62.0, Micrometer docs — 1.17.2; исходники 1.13.2/3.3.1 проверены отдельно и не объявляются исполнением текущей документации.
