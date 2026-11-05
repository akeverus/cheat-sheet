# Практики проектирования API — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q30, 30 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/api-design-best-practices-interview.json`; SHA-256 `2a3c0d706c5393e591df811f121d95419f36380d30ab11d9bec885e489aca6ee`.
- Парный Markdown: `cheatsheets/interview/api/api-design-best-practices-interview.md`; SHA-256 `79cc3bedb966264a1ca364d68449922d15cb215d3f160dbd516ea2f632aacfe7`.

## Что исправлено

Прочитаны все 30 блоков. В Q2, Q5, Q6 и Q8 убраны универсальные правила для адресов ресурсов там, где выбор зависит от контракта. В Q9–Q10 разведены offset, page, keyset и непрозрачный cursor; указаны условия устойчивой сортировки. В Q14 и Q15 уточнены необязательные поля Problem Details и статусы ошибок валидации. В Q16 ключ идемпотентности ограничен сроком хранения и связан с атомарной регистрацией запроса и результата.

Q18 различает учётные данные, формат токена, делегированный доступ и аутентификацию. Q20 учитывает текущий статус проекта заголовков RateLimit. Q21 формулирует разницу через задержку и отказ, не закрепляя термины за единственной трактовкой. Q22 допускает несколько связанных файлов OpenAPI. Q25 разделяет валидацию, параметризацию SQL и контекстное кодирование вывода; Q26 описывает роль браузера в CORS. В Markdown исправлены соответствующие разделы, оглавление и якоря Q1–Q30; редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Первичные источники: [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html) для Problem Details, [RFC 9110](https://datatracker.ietf.org/doc/html/rfc9110/) для HTTP, [OIDC Core](https://openid.net/specs/openid-connect-core-1_0.html), [проект IETF RateLimit](https://datatracker.ietf.org/doc/html/draft-ietf-httpapi-ratelimit-headers) и [OpenAPI 3.1.1](https://spec.openapis.org/oas/v3.1.1.html).

Самопроверка: 30 вопросов JSON совпадают с заголовками Markdown, у каждого есть якорь и ссылка из оглавления; в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/api-design-best-practices-interview.json` → `PASS: files=1 questions=30 blocks=30; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
