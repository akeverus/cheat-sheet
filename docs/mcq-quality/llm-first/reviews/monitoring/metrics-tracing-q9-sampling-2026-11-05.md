# Metrics/tracing Q9 — границы sampling

Дата: 2026-11-05. Статус: `AUTHOR_VERIFIED`, только Q9:B0. Прочитаны видимый вопрос, теория Q9, все A/B/C/D и sections. Проверка с известным ключом, не независимая приёмка и не FINAL темы.

JSON рабочего дерева: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `cf3e17224f34218e4554dcbed46a096c8791d2bbeccb03fe08d0e7955f4e7252`.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `947335356865c39590d36dcd2eb0a4b7a2f238635a00af7a0de472522a3b3f95`. Теория не редактировалась.

Сохранены ключ C, все тексты вариантов, question_text, номера и порядок. A смешивает отбор с компрессией, B ограничивает применение dev-средой, C описывает выбор по объёму/стоимости, D подменяет trace sampling обязательным удалением дочерних span’ов. Wrong опровергаются своими пояснениями; одинаковая тема и сопоставимая краткость вариантов сохранены.

Исправлены explanation и edge_cases правильного C: tail sampler может отбирать ошибки и задержки только по поступившим данным и своим политикам; ранее отброшенные или потерянные span’ы он не восстанавливает. Названы маршрутизация одного trace, поздние span’ы и вытеснение из буфера. Убрано безусловное обещание сохранения ошибок/выбросов, finding F-Q9-MCQ-OVERPROMISE из [review теории](../../../../interview-coverage/metrics-sampling-theory-review-2026-11-05.md) устранён авторской правкой, независимое закрытие не заявлено. Head-based не привязан к одной конкретной реализации tail-компонента.

verify-mcq-json: PASS, 41 Q / 41 блок. Это структурный прогон файла, semantic review NOT_RUN, остальные 40 вопросов не приняты. Прежние изменения Q24/Q27/Q28/Q37 сохранены в рабочем дереве вне этого адресного коммита. SFPL/ANSC/RU сопоставлены автором; blind review и независимая приёмка NOT_RUN. Collector/SDK и нагрузочные примеры не запускались, runtime не менялся.

Источники прочитаны 2026-11-05: [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/), [официальный Tail Sampling Processor README](https://raw.githubusercontent.com/open-telemetry/opentelemetry-collector-contrib/main/processor/tailsamplingprocessor/README.md). В README сверены trace routing, политики, capacity и late-arriving spans. main подвижен; конкретная установленная Collector distribution не проверялась.
