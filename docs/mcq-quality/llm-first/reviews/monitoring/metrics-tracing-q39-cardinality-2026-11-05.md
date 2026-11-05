# Metrics Q39 — ограничение кардинальности

Дата: 2026-11-05. Статус: AUTHOR_VERIFIED, Q39:B0. Прочитаны видимый вопрос, актуальная теория Q39 и связанная Q17, все четыре варианта и sections. Это авторская сверка с известным ключом, не независимая приёмка и не FINAL темы.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `d4ce557e2e771b676ae653373718211252fb02e7204082e6c7dd29948906e969`.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `f236dc3cbf301adf245cbb54f821f5c55452e66f41c6e79887f227472250dd16`. Markdown не редактировался. Сохранены ключ C, q_number, block_idx, question_text и порядок.

Все A/B/C/D сопоставляют причину роста рядов и действие. A принимает частоту scrape за определение cardinality; B считает конечность каждого label достаточной для отказа от бюджета сочетаний; C ограничивает рост нормализацией и бюджетом; D обещает уменьшение числа рядов от дополнительных histogram buckets. Варианты сформулированы по-русски с одинаковой причинной конструкцией; ошибочные тезисы объясняются собственными sections.

Исправлены notes из [проверки теории](../../../../interview-coverage/metrics-cardinality-theory-review-2026-11-05.md):

- A больше не противопоставляет перегрузку от рядов любой нагрузке частого scrape. B не обещает неизбежный OOM и не объявляет детальные логи бесплатными; вместо прежнего тезиса «опасно только для logs» проверяет практическую ошибку про конечные domains без бюджета.
- C.when_to_apply выбирает бюджет по scope и измеренным расходам, без универсальных таблиц «допустимых» чисел или «никогда» для всех возможных систем. C.edge_cases различает число рядов в группе endpoint и число различных непустых endpoint, instant-срез и historical cardinality. DENY — отказ с потерей измерений, а не преобразование в other.
- D.what_actually/how_it_should_be различают classic bucket-series с le и native histogram с составным sample. Разрешение histogram влияет на представление и расходы; оно не является заменой контроля label sets.

Формула верхней оценки, границы NOOP и риск перегрузки согласованы с обновлённой теорией. Finding о старых sections Q39 устранён авторской редакцией, независимое закрытие не заявлено. Остальные 40 блоков не принимаются этим отчётом; Q1–Q6 имеют отдельный авторский пакет.

verify-mcq-json PASS: 41 Q / 41 блок; semantic review NOT_RUN. В Q39 нет findings anti-tell/linkage/SKEL. По теме целиком остаются anti-tell 10/41, SKEL hard_fails=16 и warn_blocks=20; linkage errors=1 (Q9 → Q24), warnings=16. Answer-parity exit 1: OPTION_LENGTH_RATIO=2.67 (Q32, 15 блоков выше порога) и ABSOLUTE_MARKER_GAP=0.252. Прочие проверки gate проходят, включая STYLE_GUESSABILITY=0.398, но отсутствие статистической утечки не доказано и итоговая готовность всей темы не заявлена.

Точный SHA и логи: metrics-foundations-cardinality-final-checks.json и check-0..6.log в рабочем пространстве. Независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN. PromQL, native histogram, нагрузка и Java в этом проходе не исполнялись. [Отчёт теории](../../../../interview-coverage/metrics-cardinality-theory-review-2026-11-05.md) содержит отдельный PASS harness Micrometer 1.13.2/Corretto 22.0.2; он не выдаётся за собственный запуск автора MCQ или тест Prometheus exporter. Работающий сервис не менялся.

Первичные источники: [Prometheus data model](https://prometheus.io/docs/concepts/data_model/), [classic/native histograms](https://prometheus.io/docs/practices/histograms/), [PromQL aggregation](https://prometheus.io/docs/prometheus/latest/querying/operators/#aggregation-operators), [Micrometer Meter Filters](https://docs.micrometer.io/micrometer/reference/concepts/meter-filters.html). Прочитанная документация и notes теории относятся к проверке 2026-11-05; версия страницы Micrometer 1.17.2 отличается от версии harness 1.13.2.
