# Saga Pattern — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Статус: **LLM_READY**.

Пара и SHA-256:

- `cheatsheets/interview/architecture/saga-pattern-interview.md` — `e4e1ed9daa8157064a41a36663faca03183b23dbba36633fb7d828bec669c005`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/saga-pattern-interview.json` — `31d42a5a08c9f343d7f1e5a77b6f76133b14d3841ee7cb377f59650e0908b531`.
- `docs/mcq-quality/fact-freshness/saga-pattern-interview.json` — `57821148250372734851228d1443ae67dd2b03ce0b9b622c3cacbc9ccbb9f251`.

Прочитаны пролог, оглавление, видимые Q1–Q43, парная теория, 172 варианта A–D и пояснения. Идентификаторы B0 и ключи сохранены. Короткие дистракторы расширены до полных ошибочных моделей; пояснения исправлены по текущим вариантам. Самопроверка не заменяет независимое ревью.

Главные смысловые исправления: Saga не даёт общей ACID-атомарности; компенсация может сама завершиться ошибкой и не возвращает точное исходное состояние. Порог числа шагов не определяет выбор оркестрации или хореографии. Уточнены pivot и retryable шаги, by value, ограничения 2PC и видимость резервов TCC. Пример Spring Boot переведён на атомарную запись state и Outbox; отдельно показаны дубли relay и атомарная идемпотентность потребителя. Разделены Axon Framework 4 и 5, Camunda 7 и 8, sagaId и traceId. Для потерянного reply таймаут больше не трактуется как доказательство отказа участника. Убраны универсальные пороги времени, числа шагов и оценки производительности.

Проверенные первичные источники:

- [Saga](https://microservices.io/patterns/data/saga.html) и [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html) от Chris Richardson; [Saga](https://learn.microsoft.com/en-us/azure/architecture/reference-architectures/saga/saga) и [Compensating Transaction](https://learn.microsoft.com/en-us/azure/architecture/patterns/compensating-transaction) от Microsoft.
- [Axon Framework 4 associations](https://docs.axoniq.io/axon-framework-reference/4.11/sagas/associations/) и [миграция Saga в Axon 5](https://docs.axoniq.io/axon-framework-reference/5.3/migration/paths/sagas/); [Eventuate Tram Sagas](https://eventuate.io/docs/manual/eventuate-tram/latest/getting-started-eventuate-tram-sagas.html).
- [Camunda 8 compensation events](https://docs.camunda.io/docs/components/modeler/bpmn/compensation-events/), [compensation handler](https://docs.camunda.io/docs/components/modeler/bpmn/compensation-handler/), [Temporal Worker Versioning](https://docs.temporal.io/best-practices/worker) и [OpenTelemetry context propagation](https://opentelemetry.io/docs/concepts/context-propagation/).

Открытых критических сомнений по ключам нет. Иллюстративные фрагменты Java и DSL не компилировались в конкретном модуле; перед применением нужно сверить версии библиотек, поддержку модулей и инварианты домена.

Структурная проверка: `PASS: files=1 questions=43 blocks=43; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и прежний `FINAL PASS` не проводились.
