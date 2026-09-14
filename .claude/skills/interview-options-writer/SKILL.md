---
name: interview-options-writer
description: Указатель миграции для старых запросов о SQLite answer_options. Старый SQLite-генератор выведен из эксплуатации; новые MCQ хранятся в JSON.
---

# Миграция старого workflow

SQLite-генерация больше не поддерживается. Не создавать interview.db, option.txt
или новый legacy-контент. Текущие инструменты:

- `mcq-quality-fixer` — JSON MCQ;
- `interview-writer` — парная теория;
- `docs/mcq-quality-standard.md` — общий стандарт;
- `PROMPT_PLAN_INTERVIEW.md` — переносимый процесс.

Исторические инструкции доступны в Git. Восстановление старой среды — отдельная
явная задача; не запускать его при обычной работе с MCQ.
