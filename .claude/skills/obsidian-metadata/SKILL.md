---
name: obsidian-metadata
description: "Стандарт Obsidian-frontmatter для всех cheatsheets. Используй когда пользователь просит проверить, нормализовать, улучшить, починить или применить метаданные/frontmatter, добавить aliases/type/related, привести шпаргалки к единому виду для Obsidian Properties и графа знаний. Триггеры: «обнови frontmatter», «нормализуй метаданные», «обсидиан граф», «obsidian metadata», «исправь aliases», «добавь related», «metadata audit», «приведи шпаргалки к стандарту»."
---

# Obsidian Metadata

Единый стандарт frontmatter для всех файлов в `cheatsheets/` (включая `interview/`),
оптимизированный под Obsidian Properties, граф знаний и Bases.

Авторитет:
- Этот скилл — операционный регламент по метаданным.
- Содержательный контракт документа описан в `cheatsheets/CHEATSHEETS_ARCHITECTURE_AND_RULES.md`. Если расходимся — правила репозитория важнее, но по полям frontmatter этот скилл — расширение, а не противоречие.

## Что Obsidian берёт из frontmatter

Понимание движка экономит время:

- **Properties panel** показывает все YAML-поля, типизируя по содержимому: `text`, `list`, `checkbox`, `date`, `datetime`, `number`. Поле с одинаковым именем должно иметь одинаковый тип во всех файлах — иначе Obsidian подсветит конфликт.
- **Graph view** строит рёбра из:
  1. markdown-ссылок и wikilinks в теле документа (это уже есть — 11k+ ссылок);
  2. **значений-wikilink в frontmatter-list-полях** (`related: ["[[saga-pattern]]"]` → ребро графа). Строка `"saga-pattern.md"` ребра не создаёт — только текстовое свойство.
- **Tags pane** агрегирует `tags:` (frontmatter) + инлайновые `#tag`. В графе цветные группы задаются по тегам.
- **Bases** (community plugin / core) фильтрует и сортирует ноты по любым свойствам — `type`, `difficulty`, `updated`. Поэтому ценность каждого нового стандартного поля прямая.
- **Aliases** улучшают `Quick switcher` и autocomplete вики-ссылок, плюс отображаются в hover-preview.

Из этого следуют правила ниже.

## Контракт frontmatter

### Обязательные поля

| Поле | Тип | Требования |
|------|-----|------------|
| `title` | text | Человекочитаемое имя темы. Совпадает с H1. В кавычках. |
| `description` | text | 1 предложение, 80–200 символов. О чём шпаргалка и какую задачу закрывает. |
| `tags` | list | 2–7 элементов. YAML-список (`- tag`), не JSON-массив. lowercase, kebab-case, без кавычек вокруг значений. |
| `type` | text | Один из: `index`, `overview`, `reference`, `how-to`, `troubleshooting`, `interview`, `rules`. |
| `updated` | date | Формат `YYYY-MM-DD`, в кавычках. Меняем при любой смысловой правке. |

### Рекомендуемые поля

| Поле | Тип | Когда ставить |
|------|-----|----------------|
| `aliases` | list | 2–4 коротких альтернативы (RU/EN, синонимы). Каждая ≤ 30 символов. Помогает Quick Switcher и hover-preview. |
| `difficulty` | text | Для содержательных файлов: `beginner` / `intermediate` / `advanced`. Не ставим на `index` и `rules`. |
| `prerequisites` | list of wikilinks | Что прочитать ДО. Формат `"[[имя-файла-без-md]]"`. Пустой `prerequisites: []` сохраняется. |
| `related` | list of wikilinks | Соседние темы. Создаёт рёбра в графе. Без значений — поле просто опускается. |
| `next` | list of wikilinks | Что читать ПОСЛЕ. Пустой `next: []` сохраняется. |

### Опциональные поля

| Поле | Когда |
|------|-------|
| `cssclasses` | Если для конкретной ноты нужен особый стиль (`cssclasses: ["wide-table"]`). Редко. |
| `cover` | URL картинки-обложки, если используется тема с превью. Не обязательно. |

### Запрещено

- Inline JSON-массивы вместо YAML-списков: `tags: [a, b]` → нет.
- Ссылки в `related/prerequisites/next` как обычные строки `"saga-pattern.md"`: не создают рёбер. Только wikilink-форма.
- Свободный набор значений `type`/`difficulty`. Опечатки (`hard` вместо `advanced`) ломают фильтры Bases.
- Дублирующие тэги вида `databases` и `database` в одном файле.
- Кавычки вокруг тегов: `tags: - "java"`. Теги — простые kebab-case-токены, кавычки не нужны.

## Канонический пример

### Содержательная шпаргалка

```yaml
---
title: "Saga Pattern: распределённые транзакции"
description: "Choreography и orchestration, компенсирующие действия, Outbox/Inbox, идемпотентность, согласованность в микросервисах."
tags:
  - architecture
  - patterns
  - saga
  - microservices
  - eventual-consistency
type: "overview"
difficulty: "advanced"
aliases:
  - "Saga"
  - "Сага паттерн"
  - "saga pattern"
prerequisites:
  - "[[event-driven]]"
related:
  - "[[event-sourcing]]"
  - "[[cqrs]]"
  - "[[ddd]]"
  - "[[kafka]]"
next:
  - "[[resilience-patterns]]"
updated: "2026-04-27"
---
```

Заметь: теги без кавычек (`- architecture`), значения остальных строковых полей — в двойных кавычках.

### Index (README.md)

```yaml
---
title: "Архитектура"
description: "Раздел про архитектурные паттерны, distributed systems, enterprise integration."
tags:
  - meta
  - index
  - architecture
type: "index"
aliases:
  - "Architecture index"
  - "Архитектура: оглавление"
updated: "2026-04-27"
---
```

### Interview Q&A

```yaml
---
title: "Вопросы на собеседовании: PostgreSQL"
description: "Архитектура, индексы, EXPLAIN, MVCC, VACUUM, репликация, JSONB, блокировки, производительность."
tags:
  - interview
  - databases
  - postgresql-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "PostgreSQL interview"
  - "PostgreSQL собеседование"
  - "PostgreSQL вопросы"
  - "Postgres interview"
related:
  - "[[postgresql]]"
  - "[[database-transactions-interview]]"
updated: "2026-04-27"
---
```

## Правила построения значений

### Как выбирать `type`

| `type` | Признак |
|--------|---------|
| `index` | Файл называется `README.md` либо это TOC раздела (`interview/TOC.md`). |
| `rules` | Системный документ (есть только один: `CHEATSHEETS_ARCHITECTURE_AND_RULES.md`). |
| `overview` | Крупный обзор темы — ≥ 300 строк, тематические разделы, является «entry point» в кластер (saga, distributed-systems, observability). |
| `reference` | Справочник API/команд/опций — компактный, мало воды (gradle, maven, kubectl, postgres-types). |
| `how-to` | Пошаговая инструкция с проверяемым результатом. |
| `troubleshooting` | Симптомы → диагностика → решение. |
| `interview` | Файл лежит в `cheatsheets/interview/` и содержит Q&A. |

Если файл — гибрид (overview + reference), выбирай преобладающий жанр и при необходимости дроби документ.

### Как выбирать `difficulty`

- `beginner` — для совсем базовых тем (basics/git-basics, basics/clean-code).
- `intermediate` — дефолт. Большинство шпаргалок.
- `advanced` — distributed systems, JVM internals, продвинутые архитектурные паттерны, senior-уровень собеседования.

Никогда: `easy`, `medium`, `hard`, `expert`, `senior`. Эти значения ломают фильтры.

### Как формировать `tags`

Структура: `<section>`, `<subtopic>`, `<specific>`. 2–7 штук.

- Первый тег = имя верхней папки (`architecture`, `databases`, `frameworks`, `platform`, `interview`).
- Второй тег = подкатегория (`patterns`, `nosql`, `spring`, `kubernetes`).
- Дальше — специфика (`saga`, `redis`, `spring-cache`, `postgresql-interview`).
- Только lowercase, только kebab-case (`event-driven`, не `EventDriven`, не `event_driven`).
- Английский. Не смешивать языки в тегах.
- Дубли вида `database` и `databases` — выбираем плюрализованную форму.

### Как формировать `aliases`

Цель — короткие формы для Quick Switcher и hover-preview.

- Английское и русское название (`"PostgreSQL"`, `"Постгрес"`).
- Сокращения (`"PG"`, `"DDD"`, `"CQRS"`).
- Жаргон, по которому ищут (`"посгря"` — нет; `"Postgres"` — да).
- Для interview — короткое имя темы (например, `"PostgreSQL interview"`), не повторяй полный заголовок.

**Жёсткие правила:**

- Каждый alias ≤ **30 символов**. Длинные заголовки вида `"Saga Pattern: распределённые транзакции"` не годятся — режь на половины.
- 2–4 элемента максимум. Больше — захламляет автокомплит.
- Не дублируй `title` слово в слово.
- Если у файла `title: "X: Y"`, оставляй только короткие половины: `"X"` или `"Y"`, но не оба сразу, если соединённое больше 30.

### Как формировать wikilinks в `related/prerequisites/next`

Формат значения — строго wikilink без `.md`:

```yaml
related:
  - "[[saga-pattern]]"
  - "[[event-sourcing]]"
```

Почему именно так:

- Obsidian создаёт ребро в графе только когда видит `[[...]]` в значении list-поля. Голая строка — текст.
- Имя в `[[...]]` — basename файла без `.md`. Если у вас две ноты с одинаковым basename в разных папках, добавь относительный путь: `"[[../databases/postgresql]]"`.
- Никогда не используем wikilinks в **теле** документа — там стандарт markdown-ссылок (см. правила репозитория). Wikilinks допустимы **только** в значениях frontmatter-list-полей.

### `updated`

- Формат: `"YYYY-MM-DD"`, ISO-8601, в кавычках.
- Меняем при любой смысловой правке (новый раздел, переписанный пример, исправленная ошибка).
- Косметика (опечатка) — можно не трогать.

## Workflow

### Случай 1 — пишем новый файл

Используй cheatsheet-writer / interview-writer. Они знают про обязательные поля. Этот скилл нужен, если ты улучшаешь существующее.

### Случай 2 — правим конкретный файл

1. `Read` файла, проверь текущий frontmatter.
2. Сверь с контрактом. Подсчитай, чего не хватает.
3. `Edit` — только изменяемые блоки frontmatter (не переписывай тело).
4. Обнови `updated` сегодняшней датой (см. `currentDate` в системном контексте).

### Случай 3 — массовая нормализация (то, ради чего этот скилл)

```bash
# Проверка без записи
python3 .claude/skills/obsidian-metadata/scripts/normalize_frontmatter.py --check

# Применение изменений
python3 .claude/skills/obsidian-metadata/scripts/normalize_frontmatter.py --apply
```

Скрипт:
- читает каждый `cheatsheets/**/*.md`;
- парсит frontmatter (PyYAML не нужен — встроенный парсер);
- проставляет `type` по эвристике (interview/, README, длина файла, имя);
- нормализует `difficulty` (`hard` → `advanced`, `easy` → `beginner`, лишние пробелы);
- конвертирует `related/prerequisites/next` в wikilink-формат, если значения — пути или имена файлов;
- генерирует базовый `aliases` (только если поле отсутствует) — title + первые 1–2 синонима;
- ставит `updated` в сегодняшнюю дату только если файл был отредактирован скриптом и `updated` устарела;
- инлайн `tags: [a, b]` превращает в YAML-список;
- удаляет `next: []` и `prerequisites: []`;
- логирует все изменения в `.claude/skills/obsidian-metadata/last-run.log`.

Скрипт идемпотентен: повторный прогон без изменений не должен ничего трогать.

### Случай 4 — пере-настройка Obsidian Graph view

`.obsidian/graph.json` — конфиг визуализации. Этот скилл задаёт цветовые группы по основным тегам, чтобы граф был читаемым.

```bash
python3 .claude/skills/obsidian-metadata/scripts/configure_graph.py
```

Скрипт обновляет `colorGroups`, `showTags`, `nodeSizeMultiplier`, `linkDistance` под рекомендованный профиль. Не пересоздаёт файл — точечный merge.

## Сверка с другими скиллами

| Скилл | Связь |
|-------|-------|
| `cheatsheet-writer` | Создаёт новые шпаргалки. Должен использовать тот же контракт. Если расхождение — обновить cheatsheet-writer. |
| `interview-writer` | Делает interview/ файлы. Контракт совпадает, type фиксирован = `interview`. |
| `auto-improve` | При обходе файлов проверяет frontmatter этим скиллом. |
| `CHEATSHEETS_ARCHITECTURE_AND_RULES.md` | Авторитет по содержимому документа. По frontmatter — этот скилл расширяет минимальный контракт правил. |

## Definition of Done для метаданных

Файл считается «правильно метатегирован», если:

1. Есть валидный YAML-frontmatter (открытие/закрытие `---`).
2. Все обязательные поля присутствуют и непусты.
3. `tags` — YAML-список (а не JSON-массив), 2–7 элементов, lowercase, kebab-case.
4. `type` — из разрешённого enum.
5. `difficulty` (если есть) — из `beginner|intermediate|advanced`.
6. `related/prerequisites/next` — wikilink-форма `"[[name]]"` или отсутствуют.
7. `updated` — корректная ISO-дата, не из будущего и не старше 2024-01-01.
8. Нет дубликатов тегов.
9. Нет лишних/устаревших полей (`status`, `author`, `version` — мы их не используем).

## Антипаттерны

- Тэг-список из 15+ элементов. Граф становится грязным, фильтры теряют смысл.
- `aliases`, повторяющий `title` слово в слово.
- `related: ["saga-pattern.md"]` — текстовое свойство без графа.
- `difficulty: "expert"` — выпадает из enum.
- `updated: "сегодня"` — невалидная дата.
- Многоуровневые объекты в frontmatter (Obsidian типизирует только plain list / scalar).
- Wikilinks в теле документа.
