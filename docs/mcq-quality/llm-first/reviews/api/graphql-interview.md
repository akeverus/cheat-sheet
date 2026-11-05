# GraphQL — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q40, 40 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/graphql-interview.json`; SHA-256 `76a2fcecdf6e295aed22f70f97190c82a9521d955198ea348493f5090641c7f2`.
- Парный Markdown: `cheatsheets/interview/api/graphql-interview.md`; SHA-256 `98ac433e2daca16c257b60a99ef7bbfe0b863396cdb2dd1ba8b26117740d650b`.

## Что исправлено

Прочитаны все 40 блоков. Q1 больше не утверждает, что GraphQL обязан иметь ровно один endpoint. Q3 исправлен на шесть именованных видов типов и две обёртки. В Q5 и Q10 уточнены порядок корневых полей mutation, выполнение операции и частичные ошибки. Q8 допускает и inline, и именованный фрагмент с условием на тип. Q11–Q14 уточняют N+1, пакетную загрузку, курсорную пагинацию и отдельные соглашения Relay Connections и Global Object Identification.

Q15–Q16 различают ошибки исполнения и запроса, а доступ к интроспекции связывают с моделью угроз вместо обязательного отключения. Q18, Q24–Q25 и Q34 отделяют пользу GraphQL от гарантий производительности, а APQ — от строгого allowlist. Q27, Q36 и Q39 убирают обещания автоматического независимого деплоя и параллельного выполнения всех шагов Federation. Q33 заменяет неверное обещание ровно двух запросов на зависимость от батчей и источника данных; в парном Markdown дан простой вариант Spring `@BatchMapping`.

В Markdown обновлены соответствующие разделы, оглавление и явные якоря Q1–Q40. Редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Первичные источники: [GraphQL Specification, September 2025](https://spec.graphql.org/September2025/), [GraphQL Security](https://graphql.org/learn/security/), [GraphQL over HTTP](https://graphql.org/learn/serving-over-http/), [Relay Cursor Connections](https://relay.dev/graphql/connections.htm), [Relay Global Object Identification](https://relay.dev/graphql/objectidentification.htm), [Spring for GraphQL Controllers](https://docs.spring.io/spring-graphql/reference/controllers.html), [Spring Request Execution](https://docs.spring.io/spring-graphql/reference/2.1/request-execution.html) и [Apollo Federation Composition](https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/composition-rules).

Самопроверка: 40 вопросов JSON совпадают с заголовками Markdown, у каждого есть якорь и ссылка из оглавления; в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/graphql-interview.json` → `PASS: files=1 questions=40 blocks=40; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
