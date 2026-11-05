# Связные списки — редакция MCQ

- Дата: 2026-11-04; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q32, 32 блока B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/linked-lists-interview.json`; SHA-256 `8660e4b16741e5b26be26bd401ba13a332d9d2247b5a3e41b15ae369ef1a9cdd`.
- Парный Markdown: `cheatsheets/interview/algorithms/data-structures/linked-lists-interview.md`; SHA-256 `5fc263ea1285ddb35f893ef963b6f068f7e9749ef930778dddb9d0f0070ff78b`.

## Что исправлено

Прочитаны все 32 блока. В Q6, Q7, Q9, Q16, Q17, Q20, Q21 и Q23 добавлены в видимый вопрос условия, без которых другой вариант тоже был защитимым. В Q7 прямо разделены удаление значения и идентичность переданного узла. В Q25 исправлен ответ, который задавал допустимую обратную ориентацию LRU-списка. В Q2 и Q31 убраны безусловные советы о превосходстве одного Java-списка: решение зависит от доступа, уже известной позиции и нагрузки. Объяснения изменённых вариантов согласованы с новым условием.

В Markdown обновлены теория Q2, Q7 и Q31, оглавление и явные якоря Q1–Q32; редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Самопроверка: 32 вопроса JSON совпадают с заголовками Markdown, в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/linked-lists-interview.json` → `PASS: files=1 questions=32 blocks=32; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
