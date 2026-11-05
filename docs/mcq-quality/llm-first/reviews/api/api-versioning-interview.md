# Версионирование API — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q20, 20 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/api-versioning-interview.json`; SHA-256 `9d35779fdaefb0e637c558c16294d7ddac71d0592b89ad50cfde43b297f9950a`.
- Парный Markdown: `cheatsheets/interview/api/api-versioning-interview.md`; SHA-256 `b782000d76e85c4bba0f5c94841c4ea5424c129d52fc48146277728e1ce5f320`.

## Что исправлено

Прочитаны все 20 блоков. В Q1 и Q3 совместимость больше не обещана навсегда или без проверки клиентов. Q4, Q8 и Q9 описывают выбор способа версионирования без универсальных правил и без автоматической физической изоляции по хосту. Q11–Q13 исправлены по текущей модели Stripe: дата и серия выпуска, версия аккаунта по умолчанию, выбор версии запроса и отдельная версия webhook endpoint. Убраны неподтверждённые утверждения о бессрочной поддержке всех выпусков.

Q17 различает `Deprecation` и `Sunset` по назначению и формату даты; `Sunset` сообщает ожидаемый срок недоступности, а не гарантированную дату удаления. Q18–Q20 привязывают вывод версии и стоимость поддержки к политике сервиса. Парный Markdown согласован с ответами; оглавление и якоря Q1–Q20 обновлены, редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Первичные источники: [Stripe API Versioning](https://docs.stripe.com/api/versioning), [Stripe Events](https://docs.stripe.com/api/events/retrieve), [GraphQL Schema Change Management](https://graphql.org/learn/governance-versioning/), [RFC 8594 Sunset](https://www.rfc-editor.org/rfc/rfc8594.html) и [RFC 9745 Deprecation](https://www.rfc-editor.org/rfc/rfc9745.html).

Самопроверка: 20 вопросов JSON совпадают с заголовками Markdown, у каждого есть якорь и ссылка из оглавления; в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/api-versioning-interview.json` → `PASS: files=1 questions=20 blocks=20; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
