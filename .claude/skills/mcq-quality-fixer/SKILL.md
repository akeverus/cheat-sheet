---
name: mcq-quality-fixer
description: Создание, точечное исправление и аудит JSON MCQ в seed/mcq. Проверка всех A/B/C/D, единственности ответа, sections, видимого Markdown-вопроса и сопоставимой формы.
---

# MCQ Quality Fixer

Этот файл — адаптер инструментальной среды. Общие правила хранятся в репозитории:

1. `docs/mcq-quality-standard.md` — единственный стандарт содержания и приёмки.
2. `PROMPT_PLAN_INTERVIEW.md` — автор, независимый и слепой ревьюер, передача работы.
3. `docs/mcq-refactoring-state.md` — состояние и ограничения.
4. `docs/mcq-quality/audit-state.json` — производный снимок; перед работой пересчитать.

Не требуются личные `~/.claude/skills`, конкретная модель, Context7 или платный API.
Для фактов использовать реально доступный инструмент чтения первичных источников.

Перед работой определить скоуп: audit, targeted repair, fact refresh, schema repair
или явно порученный full rewrite. Прочитать соответствующие Q Markdown, JSON со всеми
четырьмя опциями и sections. Видимый stem берётся из Markdown, не из question_text.

`interview-writer` отвечает за теорию; этот skill — за JSON. Стабильные Q/B сохраняются.
Не применять старые ROUND/reset/commit инструкции. Не исправлять неверный wrong в
истинный. Scripts — предпроверки; LLM проверяет логику и одинаковый скелет всех опций.

Golden examples в `docs/golden-examples.md` — иллюстрации, подлежащие той же проверке;
они не переопределяют стандарт и не освобождают от проверки фактов и объяснений.
