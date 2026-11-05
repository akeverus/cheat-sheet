# REST Maturity — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q17, 17 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/rest-maturity-interview.json`; SHA-256 `3757ef740c27ece3afd23e8cfc8df40d195924e533fadf1d0f48329f71c13cea`.
- Парный Markdown: `cheatsheets/interview/api/rest-maturity-interview.md`; SHA-256 `718780f14826b64599ce82783079ce8b01ffe570c73f010a419b53f79bcb486e`.

## Что исправлено

Прочитаны все 17 вопросов и варианта. Q1–Q4 отделяют учебную последовательность RMM от обязательных свойств любого реального API: единый endpoint и POST остаются примерами, а отдельный URI сам по себе не включает кэширование. Q5 и Q11–Q14 согласованы с семантикой HTTP: безопасность относится к намеренному эффекту, идемпотентность не требует одинаковых ответов, повтор зависит от метода и операции, кэширование не возникает автоматически. Q12 убирает правило «повторять все 5xx и никогда не повторять 4xx».

Q6–Q10 уточняют, что гипермедиа снижает зависимость от конкретных URI, но клиенту всё равно нужны семантика отношений и медиатипов, а затраты есть на обеих сторонах. Q15–Q16 разделяют определение REST у Fielding, учебную роль RMM и практический выбор OpenAPI; эти механизмы могут сочетаться. Q17 ограничивает пользу выбора полей в GraphQL конкретными сценариями. В Markdown добавлены явные якоря и ссылки содержания Q1–Q17, удалены метки `(!)`.

## Проверка и открытые вопросы

Первичные источники: [Richardson Maturity Model у Martin Fowler](https://martinfowler.com/articles/richardsonMaturityModel.html), [Roy Fielding о гипертексте в REST](https://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven), [RFC 9110 HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html).

Самопроверка: 17 вопросов JSON совпадают с заголовками Markdown; у каждого есть якорь и ссылка из содержания; у каждого блока один верный вариант, все поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/rest-maturity-interview.json` → `PASS: files=1 questions=17 blocks=17; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
