# MCQ LLM-Batch Automation — Design Document

**Статус:** черновик (2026-05-17), pending реализации
**Связанные артефакты:** `~/.claude/skills/mcq-quality-fixer/SKILL.md`, `~/.claude/skills/interview-options-writer/SKILL.md`, `docs/mcq-quality-improvement-plan.md`

## Проблема

Сейчас исправление и расширение MCQ-блоков в `cheatsheets/interview/` (~100 файлов, ~1500 Q) делается одним из двух способов:

1. **Ручной режим** в чате Claude — 1 Q за раз, медленно, не масштабируется.
2. **Batch subagent-диспатч** через `/loop 15m` — каждый цикл 5–10 subagent-ов параллельно. Производительность ~30 Q / цикл; ошибки накапливаются, нет валидации между циклами, цена $ растёт.

Цель этого документа — описать целевую архитектуру для процесса который:

- Обрабатывает все ~1500 Q за один проход (включая повторы при failed validation)
- Чётко разделяет генерацию (LLM) от валидации (детерминированную)
- Имеет точечный re-run по failed Q (а не по файлу)
- Не зависит от python/auto-скриптов — оркестрируется субагентами Claude

## Текущее состояние (что уже есть)

| Компонент | Статус | Назначение |
|---|---|---|
| `MarkdownQuestionParser` | прод | Парсит MCQ из `.md` (включая multi-block после V15) |
| `scripts/verify-mcq.sh` | прод | Pre-commit валидатор: запрещённые фразы, `#### A)` структура, bold-leak |
| `mcq-quality-fixer` SKILL | прод | Конвенция формата для subagent-а: 4 секции на wrong, 5 на correct |
| `interview-options-writer` SKILL | прод | Генератор distractor-ов с Single-Delta principle |
| Pre-commit hook | прод | Блокирует bad commits локально |
| V15 миграция БД | прод | `(question_id, mcq_block_idx)` partial unique |
| ArchUnit `LayeredArchitectureTest` | прод | Защита от deps между слоями (не про MCQ) |

## Целевая архитектура

### 1. Stage-based pipeline

```
┌────────────────┐    ┌────────────────┐    ┌────────────────┐    ┌────────────────┐
│  Stage A:      │    │  Stage B:      │    │  Stage C:      │    │  Stage D:      │
│  Triage        │───▶│  Generate      │───▶│  Validate      │───▶│  Commit        │
│  (parser only) │    │  (LLM subagent)│    │  (bash + AST)  │    │  (git, hooks)  │
└────────────────┘    └────────────────┘    └────────────────┘    └────────────────┘
```

| Stage | Инструмент | Чел-в-минуту | LLM-стоимость |
|---|---|---|---|
| A. Triage | `MarkdownQuestionParser` + список pending Q | ~50 файлов/с | 0 |
| B. Generate | `interview-options-writer` subagent | 1 файл/мин | $0.10–0.30/файл |
| C. Validate | `verify-mcq.sh` + парсер-juнит-тесты | <1с/файл | 0 |
| D. Commit | git + pre-commit hook | сек | 0 |

### 2. Triage (Stage A)

Цель: за один проход найти все Q которые требуют доработки. Без LLM.

**Критерии для попадания в очередь:**

- Q без `> [!mcq]` блока (нулевое покрытие)
- Q с MCQ но без обязательных Tier 1 маркеров (`📋 ПРАВИЛО`, `❌ ПОСЛЕДСТВИЕ`, `✓ ПРИМЕНЯТЬ`)
- Q где `verify-mcq.sh` находит запрещённые фразы или старый формат `#### A)`
- Q с одним MCQ, помеченные `(!)` — кандидаты на расширение вторым MCQ

**Артефакт:** `triage-report.md` со списком вида:
```
- [ ] cheatsheets/interview/databases/redis-interview.md:Q3 — missing 📋 ПРАВИЛО
- [ ] cheatsheets/interview/databases/redis-interview.md:Q7 — only 1 MCQ, marked (!)
- [ ] cheatsheets/interview/messaging/kafka-interview.md:Q1 — no MCQ block
```

Триагер — короткий bash-скрипт (`scripts/triage-mcq.sh`), читает .md, эмиттит markdown-чеклист. Скрипт детерминированный, не «автогенератор», поэтому укладывается в «вручную»-ограничение пользователя — это просто инвертированный grep.

### 3. Generate (Stage B)

Цель: для каждой строки triage-report сгенерировать недостающее.

**Контракт subagent-а:**

- Input: путь к файлу, номер Q, причина из triage (e.g. «missing 📋 ПРАВИЛО»)
- Output: только diff на эту секцию Q — без перетряхивания соседних
- Конвенция формата: следует `mcq-quality-fixer` SKILL.md v2 (`mcq_format_version: 2`)
- Запрещено: трогать `## Q<N>` heading, ссылки `[[Q<M>]]`, секции другие чем `> [!mcq]`

**Оркестрация:** до 8 subagent-ов параллельно (1 на файл). Если в файле >5 Q в очереди — subagent обрабатывает первые 5, остальные ждут следующего цикла. Это снимает риск конфликтующих правок одного файла.

**Контроль стоимости:** после каждой генерации subagent должен выдать `## DONE Q<N>` строку — родительский агент мониторит и считает progress в `triage-report.md` (галочки).

### 4. Validate (Stage C)

Цель: гарантировать что после генерации файлы — компилируются парсером и проходят все детерминированные проверки.

**Pipeline:**

1. `scripts/verify-mcq.sh <file>` — bash-валидатор (быстрый)
2. `./gradlew :quiz-app:test --tests "*MarkdownQuestionParserTest"` — парсер должен принять формат
3. `./gradlew :quiz-app:test --tests "*NoAiModeStartupIntegrationTest"` — БД unique-constraints не сломаны
4. Дополнительно: счётчик `> [!mcq]` per Q должен быть 1 или 2 (не 0, не 3+)

Если validate fail — Q возвращается в triage-report с пометкой `validation-failed: <reason>`, и идёт re-run в Stage B (макс 2 retry, потом ручной разбор).

### 5. Commit (Stage D)

После того как все Q в triage прошли validate:

- `git add` точечный (только изменённые файлы)
- `git commit -m "feat(mcq): batch-update <N> questions in <area>/<file>"`
- pre-commit hook прогоняет `verify-mcq.sh` для double-check
- Один commit на ~30–50 Q, не один на 1000+ (чтобы при revert можно было откатиться гранулярно)

## Метрики которые надо логировать

| Метрика | Где | Зачем |
|---|---|---|
| Q в queue | `triage-report.md` | Прогресс |
| Q обработано | git log commit count | Скорость |
| Validation-fail rate | manual count из re-run колонки | Качество промптов |
| LLM-cost за прогон | `claude --json` stderr | Бюджет |
| Среднее время цикла | wall-clock | Параллелизация |

## Что НЕ входит в scope этого документа

- Семантическое улучшение существующих MCQ (это делает `auto-improve` skill)
- Создание новых Q из voids в покрытии тем (это делает `interview-writer` skill)
- Перевод бэкенда на multi-correct семантику (V15 partial-unique это запрещает)
- Slug-based `[[Q<N>]]` навигация — отдельная задача (`#215`)

## Открытые вопросы

1. **Где хранить `triage-report.md`** — в git или в gitignore? Скорее gitignore — это эфемерный артефакт.
2. **Кто запускает Stage A** — пользователь руками, или `/loop 1h triage` после merge?
3. **Версионирование SKILL** — сейчас `mcq_format_version: 2`. Когда SKILL меняется, как ре-валидировать существующие MCQ? Возможно ввести `expected_format_version` в frontmatter каждого `.md`.
4. **Multi-block расширение** — V15 поддерживает, но `interview-options-writer` сейчас генерирует только один блок. Нужен ли отдельный prompt для «второго блока с другим ракурсом» (как в reference-файлах redis/cassandra)?

## Дальнейшие шаги

- [ ] (#215) Slug-based links — независимая задача, но связана с triage (стабильные ID для отчётов)
- [ ] (новое) Написать `scripts/triage-mcq.sh` — bash-only, ~50 строк
- [ ] (новое) Дополнить `interview-options-writer` SKILL контрактом «delta only output»
- [ ] (новое) `validation-fail` rerun-логика в `mcq-quality-fixer` или новый orchestrator skill
