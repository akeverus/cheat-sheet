# LangChain4j — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `LLM_READY`; независимое слепое ревью и `FINAL PASS` не проводились.
- Дополнительная [визуальная ревизия A–D](langchain4j-visual-parity-2026-11-09.md): `VISUAL_PARITY_REVIEWED`, без повышения статуса до FINAL.
- Охват: видимые Q1–Q18, 18 блоков B0, все 72 варианта A–D и их `sections`, парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/langchain4j-interview.json`; SHA-256 `04de0bea7cbaa4846719296b51ae7629451ba5054d8b6f3ef9d306db7ee9f5fd`.
- Markdown: `cheatsheets/interview/ai-ml/langchain4j-interview.md`; SHA-256 `b3479a2a14fd73c48a883e5f3d2f48162b2c3454ab4b6ff762c2bd0c9927a928`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/langchain4j-interview.json`; SHA-256 `886ac44360e96a0b3d50c5ac78ec61b0f9a88ca6fd896e2dc7cf7ccf810beef2`.

## Смысловые правки

Скорректирован выбор между LangChain4j и Spring AI: оба применимы в Spring Boot, а возможности сравнивают на своих версиях. Уточнены роли `@SystemMessage`, `@UserMessage` и необязательность `@V` при сохранённых именах параметров. Для structured output отделены преобразование в Java-тип, поддержка JSON Schema и бизнес-валидация. Память модели отделена от полной истории диалога; для общего AI Service описаны отдельные ID бесед, удаление памяти и риск одновременных вызовов с одним ID.

В вопросах RAG разделены загрузка, парсинг, разбиение, хранение и retrieval. Убраны универсальные численные настройки overlap и `minScore`. Исправлены различия стартеров Spring Boot 3.5+/4 и расширения Quarkus. Для streaming, тестирования и наблюдаемости добавлены границы итогового usage, поддержки listener и приватности метрик. Q18 приведён к текущему API: guardrails доступны в ядре для AI Services, а output-проверка может отклонить, переписать или ограниченно повторить запрос; при streaming она задерживает фрагменты до проверки полного вывода. Правильные буквы и Q/B-идентификаторы сохранены.

## Источники и проверка

Сверены первичные материалы: [AI Services](https://docs.langchain4j.dev/tutorials/ai-services/), [Structured Outputs](https://docs.langchain4j.dev/tutorials/structured-outputs/), [Chat Memory](https://docs.langchain4j.dev/tutorials/chat-memory/), [Tools](https://docs.langchain4j.dev/tutorials/tools/), [RAG](https://docs.langchain4j.dev/tutorials/rag/), [Spring Boot Integration](https://docs.langchain4j.dev/tutorials/spring-boot-integration/), [Observability](https://docs.langchain4j.dev/tutorials/observability/), [Guardrails](https://docs.langchain4j.dev/tutorials/guardrails/), [описание @V](https://docs.langchain4j.dev/apidocs/dev/langchain4j/service/V.html) и [Quarkus LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/).

Критических открытых сомнений по ключам нет. Версионный риск: API guardrails экспериментален, а стартеры и поддержка функций моделей меняются; перед внедрением примеров их проверяют на версии зависимостей приложения.

Адресный валидатор: `PASS: files=1 questions=18 blocks=18; semantic review NOT_RUN`. Заголовки, ключи и непустые `sections` сверены; `git diff --check` прошёл. Для `FINAL` требуется независимое слепое ревью Q/A–D и пояснений на указанных SHA.
