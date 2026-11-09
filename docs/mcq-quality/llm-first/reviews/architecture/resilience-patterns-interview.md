# Resilience Patterns — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Статус: **LLM_READY**.

Пара и SHA-256:

- `cheatsheets/interview/architecture/resilience-patterns-interview.md` — `6a065953d8b9772ab3f6aded057837eae0bd2a142a408feae38eb0616358ae90`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/resilience-patterns-interview.json` — `e2d7b48beb2f5a04a1ee985c3c6f934b038cbaccd92b2ec000a12cba38b1f3d5`.
- `docs/mcq-quality/fact-freshness/resilience-patterns-interview.json` — `2eec9fe9b740084a46698aa64276b431c20d85cb7c74b9d8b9017c304c956c1d`.

Прочитаны пролог, оглавление, видимые Q1–Q43, парная теория, 172 варианта A–D и пояснения. Идентификаторы B0 и ключи сохранены. Правильные варианты сокращены до сопоставимого с неверными объёма; слабые дистракторы переписаны как полные ошибочные модели. Самопроверка не заменяет независимое ревью. Отдельная ревизия визуальной симметрии записана в `resilience-patterns-visual-parity-2026-11-09.md`.

Смысловые исправления: `maxAttempts` включает первый вызов; порядок аспектов Resilience4j настраивается; Hystrix поддерживал и семафорную, и потоковую изоляцию; Resilience4j 2.x не требует Vavr. Исправлены метрика состояния Circuit Breaker с тегом `state`, границы TimeLimiter и отмены удалённой работы, идемпотентность с атомарным ключом, readiness при общем сбое зависимости, graceful shutdown Spring Boot и Kubernetes, hedged requests, приоритетный сброс нагрузки и динамические feature flags. Убраны универсальные пороги без измерений и примеры, которые обещали успешную мутацию без подтверждения.

Проверенные первичные источники:

- [Resilience4j CircuitBreaker](https://resilience4j.readme.io/docs/circuitbreaker), [Retry](https://resilience4j.readme.io/docs/retry), [Bulkhead](https://resilience4j.readme.io/docs/bulkhead), [TimeLimiter](https://resilience4j.readme.io/docs/timeout), [Spring Boot integration](https://resilience4j.readme.io/docs/getting-started-3), [Micrometer](https://resilience4j.readme.io/docs/micrometer) и [release notes](https://github.com/resilience4j/resilience4j/releases).
- [Spring Cloud Circuit Breaker](https://docs.spring.io/spring-cloud-circuitbreaker/reference/index.html), [Spring Boot graceful shutdown](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html) и [Actuator probes](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html).
- [Kubernetes probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/), [Hystrix configuration](https://github.com/netflix/hystrix/wiki/configuration) и [maintenance status](https://github.com/Netflix/Hystrix).
- [Chaos Monkey for Spring Boot 4.0.0](https://codecentric.github.io/chaos-monkey-spring-boot/latest/) и [Toxiproxy](https://github.com/Shopify/toxiproxy).

Открытых критических сомнений по ключам нет. Примеры Resilience4j, Spring Boot, Kubernetes и Chaos Monkey не исполнялись в конкретном проекте; перед переносом нужно сверить версии, зависимости и конфигурацию. Пороги retry, hedging и load shedding нужно выбирать по измерениям.

Структурная проверка: `PASS: files=1 questions=43 blocks=43; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и прежний `FINAL PASS` не проводились.
