---
type: component
title: "Infrastructure Services"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - infrastructure
  - rendering
  - search
status: developing
related:
  - "[[Quiz App]]"
  - "[[AI Pipeline]]"
---

# Infrastructure Services

Низкоуровневые сервисы: рендеринг, диаграммы, поиск.

## DiagramService

Генерация и кэширование Mermaid-диаграмм.

```
getOrGenerateDiagram(Question) → Optional<String>
  1. Проверка кэша в БД
  2. Если нет → AiQuestionClient.generateDiagram()
  3. Валидация: MermaidValidator.isRelevantMermaid()
  4. Санитизация: MermaidSanitizer
  5. Сохранение в БД
  6. Одна попытка, без retry
```

## MarkdownRenderService

Markdown → HTML/plaintext. Использует Flexmark + Jsoup.

| Метод | Назначение |
|-------|-----------|
| `toHtml(md)` | Markdown → HTML, sanitize (удаляет script/iframe/event-attrs). Безопасен для `th:utext` |
| `toPlainText(md)` | Чистый текст без markdown-синтаксиса |
| `toPlainTextPreview(md, maxLen)` | Обрезанный plaintext с "…" |

## SearchService

Full-text search + batch loading.

```
search(query, limit) → List<SearchItem>
  1. Валидация query, cap limit (app.search.max-limit)
  2. FullTextSearchRepository.search() — SQLite FTS5 или PostgreSQL tsvector
  3. Batch-load Questions через QuestionRepository.findByIds() (решает N+1)
  4. Return: questionId, questionText, topic, snippet, answerPreview
```

Пустой/null query → пустой список.

## Связи

- `DiagramService` вызывает [[AI Pipeline]] для генерации
- `MarkdownRenderService` используется в [[Interview Flow]] для рендеринга вопросов
- `SearchService` использует [[Quiz Persistence]] FTS-репозитории
