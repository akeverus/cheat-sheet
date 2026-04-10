---
type: component
title: "Database Schema"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - database
  - schema
status: mature
related:
  - "[[Quiz Persistence]]"
  - "[[Flyway]]"
  - "[[ADR-002 SQLite Default]]"
---

# Database Schema

SQLite (default) / PostgreSQL. 14 Flyway-миграций (V1–V14).

## Таблицы

### questions

```sql
id              INTEGER PRIMARY KEY AUTOINCREMENT
slug            TEXT UNIQUE
source_slug     TEXT          -- FK к self (для вариантов, V3)
file_path       TEXT
topic           TEXT
question_text   TEXT
answer_markdown TEXT
is_important    INTEGER (0|1, default 0)
source_hash     TEXT
question_type   TEXT (default 'TEXT', V4)
code_snippet    TEXT (V4)
diagram_mermaid TEXT (V6)
regen_count     INTEGER (default 0, V6)
takeaway        TEXT (V11)
difficulty      TEXT (default 'MEDIUM', V14)
short_explanation   TEXT (V14)
detailed_explanation TEXT (V14)
common_mistake  TEXT (V14)
tags            TEXT (V14, comma-separated)
```

Индексы: `idx_questions_topic`, `idx_questions_is_important`

### answer_options

```sql
id                      INTEGER PRIMARY KEY AUTOINCREMENT
question_id             INTEGER FK → questions(id) ON DELETE CASCADE
option_text             TEXT
is_correct              INTEGER (0|1, default 0)
display_order           INTEGER
source                  TEXT (OPENAI|DEEPSEEK)
created_at              TEXT (default CURRENT_TIMESTAMP)
explanation             TEXT (V9)
prompt_version          INTEGER (default 1, V12)
quality_profile_version INTEGER (default 1, V12)
```

Индексы: `idx_answer_options_question_id`

### review_state

```sql
question_id   INTEGER PK, FK → questions(id) ON DELETE CASCADE
repetitions   INTEGER (default 0)
interval_days INTEGER (default 0)
ease_factor   REAL (default 2.5)
next_review_at INTEGER (epoch-seconds)
last_result   TEXT (default 'NEW': NEW|CORRECT|WRONG|RESET)
correct_count INTEGER (default 0)
wrong_count   INTEGER (default 0)
```

Индексы: `idx_review_state_next_review_at`

### question_hints (V5)

```sql
id          INTEGER PRIMARY KEY AUTOINCREMENT
question_id INTEGER FK → questions(id) ON DELETE CASCADE
level       INTEGER (1-3)
hint_text   TEXT
```

### daily_activity (V10)

Дневная активность для streak tracking.

### user_topic_stats (V14)

```sql
id        INTEGER PRIMARY KEY AUTOINCREMENT
topic     TEXT UNIQUE
correct   INTEGER (default 0)
incorrect INTEGER (default 0)
mastery   REAL (default 0)
last_seen TEXT
```

### questions_fts (V2)

SQLite FTS5 virtual table для полнотекстового поиска.

## FK Relationships

```
answer_options.question_id → questions.id (CASCADE)
review_state.question_id   → questions.id (CASCADE)
question_hints.question_id → questions.id (CASCADE)
questions.source_slug      → questions.slug (self-ref)
```

## Паттерны

- **Tags**: comma-separated в TEXT, парсятся через split/stream
- **Enum storage**: TEXT (ReviewResult.name(), QuestionType.name())
- **Timestamps**: epoch-seconds (review_state), CURRENT_TIMESTAMP (answer_options)
- **CASCADE**: удаление вопроса каскадно удаляет ответы, hints, review_state
