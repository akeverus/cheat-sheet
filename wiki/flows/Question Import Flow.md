---
type: concept
title: "Question Import Flow"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - import
  - markdown
status: developing
related:
  - "[[Quiz App]]"
  - "[[Quiz Persistence]]"
---

# Question Import Flow

Импорт вопросов из markdown-файлов в базу данных.

## Поток

1. Markdown-файлы с вопросами лежат на диске (путь из `app.interview-path`)
2. `MarkdownQuestionParser` парсит файлы:
   - Заголовок → тема
   - Блоки → вопросы с кодом
   - Метаданные → сложность, тип
3. `QuestionImportService` загружает в БД через `QuestionRepository`
4. `QuestionExpansionService` создаёт варианты вопросов
5. `HashingService` вычисляет хэш для отслеживания изменений
6. `StartupRunner` запускает импорт при старте приложения

## Связи

- Результат попадает в [[Quiz Domain]] модель `Question`
- Хранится через [[Quiz Persistence]]
- Используется в [[Interview Flow]]
