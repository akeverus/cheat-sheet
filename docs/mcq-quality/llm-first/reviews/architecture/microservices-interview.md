# Microservices — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Статус: **LLM_READY**.

Пара и SHA-256:

- `cheatsheets/interview/architecture/microservices-interview.md` — `18bd28144a71d3ebc50adac847c0d1b1331e53a7aaf4986cfcd5fcba3f7865d3`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/microservices-interview.json` — `359a2fec5c986a37e78a0e70fb93deb140e4c75ba16d0a13876e678a6706c805`.
- `docs/mcq-quality/fact-freshness/microservices-interview.json` — `500e959d59a9777077cd0260a255d00539ff3def55d570a14497dab4fd099340`.

Прочитаны пролог, оглавление, видимые Q1–Q42, парная теория, 168 вариантов A–D и пояснения. Идентификаторы B0 и ключи сохранены. Правки затронули формулировки верных и неверных вариантов, объяснения и теорию. Отдельная проверка визуальной симметрии записана в `microservices-visual-parity-2026-11-09.md`. Самопроверка не заменяет независимое ревью.

Смысловые исправления: владение данными отделено от требования отдельного физического сервера БД; Bounded Context не приравнен автоматически к одному сервису или команде; размер команды и число сервисов убраны как универсальные пороги. Монолит может масштабироваться горизонтально. Saga, 2PC и Outbox описаны с разными гарантиями: компенсация не даёт ACID-откат, Outbox не даёт мгновенную доставку. Уточнены CAP, CQRS, Event Sourcing, изоляция Service Mesh, идемпотентность повторов, безопасность за Gateway, эволюция контрактов и совместимость версий при развёртывании. Исправлен пример повторного вызова POST без ключа идемпотентности и пример проверки ключа с гонкой. Для Spring Boot 3.5 указаны зависимости OTLP и условие создания клиента через автоконфигурированный builder. OpenFeign не представлен обязательным выбором для новых проектов.

Проверенные первичные источники:

- [Martin Fowler, Microservices](https://martinfowler.com/articles/microservices.html) и [Monolith First](https://martinfowler.com/bliki/MonolithFirst.html).
- [Database per Service](https://microservices.io/patterns/data/database-per-service.html), [Saga](https://microservices.io/patterns/data/saga.html) и [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html).
- [Spring Boot 3.5 tracing](https://docs.spring.io/spring-boot/3.5/reference/actuator/tracing.html) и [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/reference/).
- [OpenTelemetry traces](https://opentelemetry.io/docs/concepts/signals/traces/) и [context propagation](https://opentelemetry.io/docs/concepts/context-propagation/).
- [Istio ambient mode](https://istio.io/latest/docs/ambient/overview/).

Открытых критических сомнений по ключам нет. Примеры Spring, Istio и Jaeger не исполнялись в конкретном проекте; перед переносом в код следует сверить версии, зависимости и конфигурацию. Универсальные пороги sampling, числа команд и производительности намеренно не заданы.

Структурная проверка: `PASS: files=1 questions=42 blocks=42; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и прежний `FINAL PASS` не проводились.
