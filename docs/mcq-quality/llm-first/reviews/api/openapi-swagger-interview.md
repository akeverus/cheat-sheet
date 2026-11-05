# OpenAPI и Swagger — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q42, 42 блока B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/openapi-swagger-interview.json`; SHA-256 `9b74e096f4ebe44e60d1f0537a858b88fbf871e58f39a66d484ca5fcfcaa9e01`.
- Парный Markdown: `cheatsheets/interview/api/openapi-swagger-interview.md`; SHA-256 `7fd7bb6191ec9348c51a6e3bb0f9ccc7232b82a0af8cb0131f7468c27d754c82`.

## Что исправлено

Прочитаны все 42 вопроса и варианта. Q1 описывает HTTP API шире REST. Q2 не называет OAS 3.1 последней версией и отделяет возможности спецификации от поддержки инструментом. Q4 учитывает разные обязательные разделы OAS 3.0 и 3.1. Q13 отделяет объявление тегов от порядка их показа в Swagger UI. Q15 связывает доступ к документации с действующей конфигурацией Spring Security. Q17–Q18 снимают ложную гарантию точности с code-first и абсолютный запрет параллельной разработки. Q20 и Q35 ограничивают гарантию компилятора Java-сигнатурами.

Q27 уточняет семантику `allOf`; Q31 разделяет исходный HTTP-статус и возможное изменение посредником, а также уточняет поддержку `ProblemDetail` в Spring. Q36 показывает, что регулярная регенерация возможна и с `interfaceOnly`. Q38 учитывает поведение строгих клиентов и ограничения автоматического diff. Q39 различает карту примеров media type и массив `schema.examples` в OAS 3.1. В Markdown уточнены рекомендации Q33 и описание бинарных данных Q42. Добавлены явные якоря и ссылки содержания Q1–Q42, удалены метки `(!)`.

## Проверка и открытые вопросы

Первичные источники: [OpenAPI Specification 3.1.1](https://spec.openapis.org/oas/v3.1.1.html), [каталог версий OAS](https://spec.openapis.org/oas/), [springdoc-openapi](https://springdoc.org/), [openapi-generator spring](https://openapi-generator.tech/docs/generators/spring/), [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).

Самопроверка: 42 вопроса JSON совпадают с заголовками Markdown; у каждого есть якорь и ссылка из содержания; у каждого блока один верный вариант, все поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/openapi-swagger-interview.json` → `PASS: files=1 questions=42 blocks=42; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
