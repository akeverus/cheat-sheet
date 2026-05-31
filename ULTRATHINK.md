---
title: "ULTRATHINK v5 — exhaustive per-file audit plan (всё с нуля)"
description: "Полный план аудита всех 304 interview-файлов. Прогресс сброшен в ноль. Детализированные критерии по 8 группам (A-H) с конкретными метриками."
updated: "2026-05-29"
status: "active"
audit_version: 5
progress: "MCQ-сидеры: 195/306 .md имеют выровненный JSON; 111 NO-JSON в работе (Phase 2). Готовы кластеры: Spring (28), Security (10/10), Architecture (17/17), testing 12/14 (только property-based+test-automation DEFER-md), databases 12/12 ✓, devops 12/12 ✓ (vault+terraform ✓). Дальше: java SEVERE-regen + programming-languages, api (6), monitoring (9), cloud (6)."
---

# ULTRATHINK v5 — exhaustive per-file audit plan

> **Текущий фокус (2026-05-28):** Phase 2 — генерация выровненных MCQ-JSON-сидеров для 201 NO-JSON файла. Инвариант: JSON `q_number` = `## Q<N>` в .md (опции привязываются по номеру, `question_text` не отображается). Каждый сидер проходит gate: schema-valid + alignment 1.0 (0 orphans/missing) + строгая ротация `ABCD[(N-1)%4]` + 0 дубликатов дистракторов + 0 self-contradiction + info-ratio ≤ 4 + adversarial семантическая сверка + BUILD SUCCESSFUL. Метод генерации — чанкованный Python-генератор (sub-agent/workflow), иначе падение на 32k output-лимите.
>
> Полный per-file checklist A-H (ниже) применяется к каждому .md независимо от истории.

## Зачем v5

v4 содержал per-file таблицу, но критерии были general. **v5** — каждый критерий имеет:
- Конкретное условие проверки (regex / numeric threshold / shell command).
- Эталонный пример pass / fail.
- Конкретное действие при fail.

## CHECKLIST A — Frontmatter (6 обязательных полей)

### A1. `title` — "Вопросы на собеседовании: \`<Topic>\`"
- **Pass:** `title: "Вопросы на собеседовании: \`PostgreSQL\`"`.
- **Fail:** missing `title:`, or value не начинается с `"Вопросы на собеседовании:"`.
- **Fix:** добавить/исправить frontmatter top of file.

### A2. `description` — одна строка, 80-200 символов, summary темы
- **Pass:** `description: "Основы Java concurrency: threads, synchronized, volatile, ExecutorService, Future, virtual threads (JEP 444)."`.
- **Fail:** пустая, > 250 символов, multiline, не отражает содержимого.

### A3. `tags` — YAML list, минимум 3 элемента
- **Pass:**
  ```yaml
  tags:
    - interview
    - <category>
    - <topic>
  ```
- **Fail:** inline JSON `tags: [a, b]`, < 3 элементов.

### A4. `aliases` — массив ≥ 3 терминов, mix english + russian
- **Pass:**
  ```yaml
  aliases:
    - "PostgreSQL interview"
    - "Postgres вопросы"
    - "Базы данных Postgres"
  ```
- **Fail:** < 3 алиаса, только английский, только русский.

### A5. `difficulty` — enum: `intermediate` | `advanced`
- **Pass:** `difficulty: "advanced"`.
- **Fail:** другое значение / отсутствует.

### A6. `updated` — ISO-8601 YYYY-MM-DD
- **Pass:** `updated: "2026-05-27"`.
- **Fail:** missing / другой формат / в будущем.

### Verification A
```bash
python3 -c "
import re
text = open('FILE.md').read()
if not text.startswith('---'): exit(1)
fm = text[:2000]
for k in ['title:', 'description:', 'tags:', 'aliases:', 'difficulty:', 'updated:']:
    if k not in fm: print(f'MISSING {k}'); exit(1)
print('FM ok')
"
```

## CHECKLIST B — Структура файла

### B1. `## Полезные ссылки` сразу после intro (h1 + 1-3 параграфа)
### B2. `## Содержание` TOC после Полезные ссылки
### B3. TOC anchors соответствуют heading slugs (GitHub-style: lowercase, spaces → hyphens, спецсимволы strip)
### B4. Заголовки вопросов: `## Q<N>. <text>?` (точка после номера, опционально `(!)` для важных)
- Pass: `## Q5. (!) Почему BM25 лучше TF-IDF?`
- Fail: `## Q5 Почему...` (нет точки), `## Q 5.`, `## 5. ...`.
### B5. Q-нумерация без gaps (Q1, Q2, ..., QN sequential)
### B6. `## See also` standalone top-level section в конце (после `---` separator)
- **CRITICAL:** не nested как `### See also` под `## Полезные ссылки`.
### B7. ≥ 5 markdown-ссылок в See also, relative paths c `.md` extension

### Verification B
```bash
grep -c '^## Q[0-9]' FILE.md  # должно быть ≥ 20
awk '/^## See also$/,EOF' FILE.md | grep -c '\]([^)]*\.md)'  # ≥ 5
```

## CHECKLIST C — Content quality (человекочитаемость)

### C1. Только **русский** для повествования; english термины ТОЛЬКО в `backticks`, code blocks, пути.
- Pass: «`PostgreSQL` использует `MVCC` для изоляции транзакций».
- Fail: «PostgreSQL uses MVCC for transaction isolation».

### C2. Короткие абзацы — ≤ 4 предложения каждый
- Pass: 3-4 коротких параграфа на ответ.
- Fail: один параграф из 200+ слов.

### C3. Bullet-списки для 3+ пунктов
- Pass: `- item1\n- item2\n- item3`.
- Fail: «Есть три варианта: первый, второй, третий, и каждый из них имеет свои особенности» — должен быть список.

### C4. Таблицы для `vs` сравнений
- Pass:
  ```markdown
  | Подход | Pros | Cons |
  |---|---|---|
  | A | ... | ... |
  ```
- Fail: словесное сравнение «A быстрее, B надёжнее, C дешевле».

### C5. Код в блоках с language tag
- Pass: ` ```java\nclass Foo {}\n``` `.
- Fail: ` ``` ` без language.

### C6. Mermaid diagrams для архитектуры / flow (1-3 на файл)
- Pass: ` ```mermaid\ngraph LR\n  A --> B\n``` `.
- Fail: ASCII art `A → B → C`.

### C7. **Жирное** для ключевых выводов, ≤ 2 раз на абзац
- Pass: «**Verdict:** используйте BM25 в production».
- Fail: «**Очень** **важно** **помнить** **что** ...» (over-bold).

### C8. Примеры обязательны в каждом концептуальном ответе
- Pass: упомянуты конкретные числа (5 000 QPS), компании (Netflix, Stripe), коды (`@Transactional(propagation=...)`).
- Fail: только теоретическое описание без чисел/имён.

### C9. Без воды
- Banned: «важно отметить что», «стоит упомянуть», «необходимо понимать», «хочется отметить».

### C10. Без маркетинга
- Banned: «мощный инструмент», «гибкое решение», «современный подход».

### C11. Tone — инженер инженеру
- Banned: академический tone («рассмотрим», «таким образом, мы видим что»).

## CHECKLIST D — Чистота markdown

### D1. 0 `> [!mcq]` callout-блоков
- **Banned since 2026-05-20**, ломают pre-commit hook.
- Check: `grep -c '^> \[!mcq\]' FILE.md` → 0.

### D2. 0 Tier 1 эмодзи-маркеров: `❌ ПОСЛЕДСТВИЕ:`, `✓ ПРИМЕНЯТЬ:`, `📋 ПРАВИЛО:`, `🔗 См. Q<N>`
- Legacy v1 contrastive learning markers, заменены JSON sections.
- Check: `grep -cE '❌ ПОСЛЕДСТВИЕ|✓ ПРИМЕНЯТЬ|📋 ПРАВИЛО|🔗 См\.' FILE.md` → 0.

### D3. 0 `[[wikilinks]]` (только markdown `[text](path.md)`)
- Check: `grep -cE '\[\[[a-z][a-z0-9-]+(?:-interview)?(?:#Q[0-9]+)?(?:\|[^\]]+)?\]\]' FILE.md` → 0.

### D4. `bash scripts/verify-md-no-mcq.sh <file>` exit 0

## CHECKLIST E — Q count и важность

### E1. Минимум 20 Q
- Базовая тема (узкий topic): ≥ 20.
- Широкая тема (Spring, Java collections): ≥ 25.
- System design: ≥ 30.
- Mega-topic (postgresql, java-concurrency): 50+.

### E2. 20-30% Q помечены `(!)` как важные
- Pass: 6-9 `(!)` на 30 Q файл.
- Fail: 0 important или 25 (over-marking).

### Verification E
```bash
total=$(grep -c '^## Q[0-9]' FILE.md)
important=$(grep -cE '^## Q[0-9]+\. \(!\)' FILE.md)
echo "$total Q, $important important ($(( important*100/total ))%)"
```

## CHECKLIST F — Парный JSON-сидер (MCQ)

### F1. Файл существует
- Путь: `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json`.
- Pass: file exists.
- Fail: NO-JSON.

### F2. `topic_slug` matches md basename
- Pass: `"topic_slug": "postgresql-interview"` для `postgresql-interview.md`.
- Fail: mismatch.

### F3. Q count в JSON == Q count в MD
- Check: `python3 -c "..."` (см. Verification ниже).
- Fail: JSON-MISMATCH.

### F4. JSON Schema draft-07 валиден (0 errors)
- Check: `jsonschema.Draft7Validator(schema).iter_errors(data)` → empty.

### F5. Структура каждого Q
- `q_number`: integer.
- `blocks`: list ≥ 1.
- Каждый block: `block_idx`, `question_text`, `options` (ровно 4).
- Каждая option: `order` (0-3), `label` (A/B/C/D), `text`, `correct` (bool), `sections`.

### F6. Rotation `correct` позиций A/B/C/D балансирован
- max/min ≤ 2.0.
- Идеал: ratio 1.0-1.2 (строгое A→B→C→D cycle).

### Verification F
```bash
python3 -c "
import json, jsonschema, collections
from jsonschema import Draft7Validator
schema = json.load(open('modules/quiz-app/src/main/resources/seed/mcq-schema.json'))
data = json.load(open('PATH/to/json'))
errors = list(Draft7Validator(schema).iter_errors(data))
print('errors:', len(errors))
pos = collections.Counter()
for q in data['questions']:
  for b in q['blocks']:
    for o in b['options']:
      if o['correct']: pos[o['label']] += 1
vals = list(pos.values())
print('rotation:', dict(sorted(pos.items())), 'ratio:', max(vals)/max(min(vals),1))
"
```

## CHECKLIST G — MCQ content quality (человекочитаемость JSON)

### G1. Correct sections — ровно 5 ключей
`explanation`, `example`, `when_to_apply`, `edge_cases`, `related`.

- **explanation:** 3-7 предложений, **механика** ответа.
- **example:** production-кейс — компания, сервис, числа, код в backticks.
- **when_to_apply:** конкретные сценарии где correct применяется.
- **edge_cases:** 1-2 граничных кейса при правильном применении (НЕ повтор misconceptions из wrong).
- **related:** plain text refs `Q5; Q12` или `см. design-payment-system-interview Q23`. **БЕЗ `[[wikilink]]`**.

### G2. Wrong sections — ровно 4 ключа
`what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`.

- **what_actually:** 2-4 предложения, что реально произойдёт.
- **source_of_confusion:** корень misconception (устаревшая практика, путаница с похожим).
- **if_it_were_true:** конкретный technical symptom (например «OOM при 5K coнnect»), либо post-mortem (Knight Capital 2012, Equifax 2017, Capital One 2019).
- **how_it_should_be:** одно предложение — мост к correct.

### G3. Все секции на русском, термины в `backticks`

### G4. `text` опций — 1-3 предложения, info-equivalent length
- max(len(text)) / min(len(text)) ≤ 1.4 в одном блоке.

### G5. Distractors правдоподобны
- Middle-разработчик мог бы выбрать.
- НЕ очевидно бредовые («Java run on TypeScript»).

### G6. `example` содержит реальные production-кейсы
- Имена компаний (Netflix, Stripe, Uber, Yandex).
- Имена продуктов (PostgreSQL, Kafka, Resilience4j).
- Post-mortems (Knight Capital 2012 $440M, Equifax 2017 147M leak, Capital One 2019 $100M).

### G7. БЕЗ `**bold**` в `text` поле
- Палит correct.
- Pass: `"text": "Use INSERT ... ON CONFLICT DO NOTHING для idempotent inserts"`.
- Fail: `"text": "Use **INSERT ... ON CONFLICT DO NOTHING** for idempotency"`.

### G8. БЕЗ эмодзи (❌📋✓🔗) в sections
- Legacy v1 формат, заменён structured sections.

### G9. БЕЗ banned-фраз
- Banned (auto-reject если хотя бы 1 встретилось):
  - `Частая ошибка в реальном коде.`
  - `Это антипаттерн или неправильный выбор в production.`
  - `Ключевое отличие и best practice in production.`
  - `Правильный ответ описывает основную концепцию.`
  - `Это смежное, но отличное понятие.`
  - `Противоположное направление.`
  - `Неправильный вариант 1/2/3`
  - `Объяснение концепции 2-3 предложения`

## CHECKLIST H — Smoke verification

### H1. McqJsonLoader тест проходит для нового JSON
```bash
./gradlew :quiz-app:test --tests '*McqJsonLoader*' -q
```
Pass: `BUILD SUCCESSFUL`.

### H2. Pre-commit hook не блокирует
```bash
bash scripts/verify-md-no-mcq.sh FILE.md
```
Pass: `OK FILE.md`.

## Статус labels

| Метка | Значение |
|---|---|
| `⬜ TODO` | Не проверялся в текущем audit-passe. По умолчанию для всех 304. |
| `🔄 IN PROGRESS` | Работа в процессе для этого файла. |
| `✅ DONE` | Все критерии A1-H2 пройдены. |
| `⚠️ PARTIAL` | Прошёл часть критериев, нужны fixes. |
| `❌ BLOCKED` | Найдена системная проблема (например broken markdown render). |

## Workflow per-file

1. **READ** md + (если есть) json.
2. **CHECK** A1-H2 (полный 35-point checklist).
3. **FIX** нарушения через `/interview-writer` (md) и `/mcq-quality-fixer` (json).
4. **VERIFY** через `verify-md-no-mcq.sh` + `jsonschema` + `./gradlew test`.
5. **COMMIT** логический (1-3 файла на коммит).
6. **UPDATE** статус в этой таблице `⬜ TODO` → `✅ DONE`.

## Snapshot аудита (только для информации, не для preserving progress)

| Метрика | Значение |
|---|---|
| Total файлов | 304 |
| NO-JSON (нет парного сидера) | 217 |
| STUB (Q < 5) | 52 |
| THIN (5 ≤ Q < 20) | 56 |
| MCQ-callout dirty | 0 |
| Tier1 эмодзи | 0 |
| Real wikilinks (вне code blocks) | 0 |
| See-also < 5 ссылок | 1 |
| Frontmatter incomplete | 0 |
| JSON Q count mismatch с MD | 12 |

> Эти цифры не означают что файлы готовы — каждый требует проверки **всех** 35 критериев A1-H2, а не только тех, которые detected audit-скриптом.

## Пофайловая таблица (304 строки)

**Machine-audit status (critery A/B/D/E/F):**
- 🟢 STRUCT-PASS: 16 (5%) — прошли все machine-checkable; semantic review (C, G) ещё не сделан.
- ⚠️ PARTIAL: 47 (15%) — JSON есть, но E (importance markers) вне 10-45% диапазона.
- ⚠️ JSON-ISSUES: 25 (8%) — Q count mismatch или schema invalid.
- ❌ NO-JSON: 209 (69%) — нет парного JSON-сидера.
- ❌ STUB: 3 (Q < 5).
- ❌ THIN: 4 (5 ≤ Q < 20).

Колонки: **A** frontmatter, **B** структура+seealso, **D** markdown clean, **E** Q count+importance, **F** JSON-сидер. Semantic critery C (content quality) и G (MCQ content quality) **не проверяются автоматически** — требуют ручного review.

| # | Category | File | Q | JSON Q | A | B | D | E | F | Status |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | `ai-ml` | `agentic-patterns` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 2 | `ai-ml` | `ai-agents` | 38 | 28 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 3 | `ai-ml` | `ai-application-architecture` | 32 | 32 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 4 | `ai-ml` | `ai-compliance-governance` | 32 | 32 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 5 | `ai-ml` | `ai-observability` | 28 | 28 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 6 | `ai-ml` | `ai-safety-guardrails` | 32 | 32 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 7 | `ai-ml` | `code-agents` | 31 | 31 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 8 | `ai-ml` | `embeddings` | 39 | 29 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 9 | `ai-ml` | `fine-tuning-llm` | 35 | 35 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 10 | `ai-ml` | `function-calling` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 11 | `ai-ml` | `inference-optimization` | 36 | 36 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 12 | `ai-ml` | `llm-basics` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 13 | `ai-ml` | `llm-evaluation` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 14 | `ai-ml` | `llm-integration-patterns` | 38 | 28 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 15 | `ai-ml` | `long-context-vs-rag` | 30 | 30 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 16 | `ai-ml` | `mcp` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 17 | `ai-ml` | `mlops` | 38 | 28 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 18 | `ai-ml` | `model-serving` | 38 | 28 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 19 | `ai-ml` | `multi-agent-orchestration` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 20 | `ai-ml` | `multimodal-ai` | 31 | 31 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 21 | `ai-ml` | `open-source-llms` | 34 | 34 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 22 | `ai-ml` | `prompt-engineering` | 38 | 28 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 23 | `ai-ml` | `rag` | 40 | 30 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 24 | `ai-ml` | `reasoning-models` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 25 | `ai-ml` | `vector-databases` | 28 | 28 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 26 | `algorithms` | `algorithms` | 16 | 16 | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ THIN |
| 27 | `algorithms/algorithmic-paradigms` | `backtracking` | 24 | 23 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 28 | `algorithms/algorithmic-paradigms` | `divide-and-conquer` | 21 | 21 | ✅ | ❌ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 29 | `algorithms/algorithmic-paradigms` | `dynamic-programming` | 33 | 33 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 30 | `algorithms/algorithmic-paradigms` | `greedy-algorithms` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 31 | `algorithms/algorithmic-paradigms` | `recursion` | 27 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 32 | `algorithms/algorithmic-paradigms` | `two-pointers-sliding-window` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 33 | `algorithms/complexity` | `complexity-analysis` | 31 | 31 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 34 | `algorithms/data-structures` | `arrays-strings` | 36 | 36 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 35 | `algorithms/data-structures` | `graphs` | 33 | 31 | ✅ | ❌ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 36 | `algorithms/data-structures` | `hash-tables` | 34 | 32 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 37 | `algorithms/data-structures` | `heaps` | 29 | 29 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 38 | `algorithms/data-structures` | `linked-lists` | 32 | 32 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 39 | `algorithms/data-structures` | `stacks-queues` | 25 | 25 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 40 | `algorithms/data-structures` | `trees` | 34 | 34 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 41 | `algorithms/data-structures` | `tries` | 28 | 28 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 42 | `algorithms/sorting-searching` | `searching-algorithms` | 8 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 43 | `algorithms/sorting-searching` | `sorting-algorithms` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 44 | `api` | `api-design-best-practices` | 30 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 45 | `api` | `api-versioning` | 20 | 20 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 46 | `api` | `graphql` | 40 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 47 | `api` | `grpc` | 40 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 48 | `api` | `http-rest` | 43 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 49 | `api` | `openapi-swagger` | 33 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 50 | `api` | `rest-maturity` | 17 | 17 | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ THIN |
| 51 | `api` | `websocket` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 52 | `architecture` | `api-gateway` | 38 | 38 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 53 | `architecture` | `bff-pattern` | 17 | 17 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 54 | `architecture` | `caching-strategies` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 55 | `architecture` | `cap-theorem` | 41 | 41 | ✅ | ❌ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 56 | `architecture` | `cdn` | 30 | 30 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 57 | `architecture` | `clean-architecture` | 41 | 41 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 58 | `architecture` | `consistency-patterns` | 31 | 31 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 59 | `architecture` | `cqrs-event-sourcing` | 41 | 41 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 60 | `architecture` | `ddd` | 38 | 38 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 61 | `architecture` | `distributed-systems` | 40 | 40 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 62 | `architecture` | `dns` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 63 | `architecture` | `edge-computing` | 18 | 18 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 64 | `architecture` | `event-driven-patterns` | 40 | 40 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 65 | `architecture` | `hexagonal-architecture` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 66 | `architecture` | `latency-numbers` | 24 | 24 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 67 | `architecture` | `load-balancing` | 40 | 40 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 68 | `architecture` | `microservices` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 69 | `architecture` | `networking` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 70 | `architecture` | `resilience-patterns` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 71 | `architecture` | `reverse-proxy` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 72 | `architecture` | `saga-pattern` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 73 | `architecture` | `scalability-patterns` | 41 | 41 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 74 | `architecture` | `service-discovery` | 30 | 30 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 75 | `architecture` | `strangler-fig` | 18 | 18 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 76 | `behavioral` | `behavioral` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 77 | `behavioral` | `conflict-stories` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 78 | `behavioral` | `culture-fit` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 79 | `behavioral` | `failure-stories` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 80 | `behavioral` | `leadership-stories` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 81 | `behavioral` | `star-method` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 82 | `cicd` | `deployment-strategies` | 39 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 83 | `cicd` | `pipeline-design` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 84 | `cloud` | `aws` | 16 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 85 | `cloud` | `aws-lambda` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 86 | `cloud` | `azure` | 25 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 87 | `cloud` | `cloud-native-patterns` | 14 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 88 | `cloud` | `gcp` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 89 | `cloud` | `serverless` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 90 | `code-quality` | `clean-code-practices` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 91 | `code-quality` | `code-coverage` | 25 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 92 | `code-quality` | `code-review` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 93 | `code-quality` | `code-smells` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 94 | `code-quality` | `refactoring-patterns` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 95 | `code-quality` | `static-analysis` | 6 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 96 | `code-quality` | `technical-debt` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 97 | `data-engineering` | `apache-airflow` | 28 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 98 | `data-engineering` | `apache-flink` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 99 | `data-engineering` | `apache-spark` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 100 | `data-engineering` | `data-lake-lakehouse` | 28 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 101 | `data-engineering` | `data-warehousing` | 30 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 102 | `data-engineering` | `dbt` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 103 | `data-engineering` | `kafka-streams` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 104 | `data-engineering` | `stream-processing` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 105 | `databases` | `cassandra` | 44 | 44 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 106 | `databases` | `clickhouse` | 28 | 28 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 107 | `databases` | `cockroachdb` | 24 | 24 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 108 | `databases` | `database-architecture` | 41 | 41 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 109 | `databases` | `database-replication` | 31 | 31 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 110 | `databases` | `database-sharding` | 34 | 34 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 111 | `databases` | `database-transactions` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 112 | `databases` | `dynamodb` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 113 | `databases` | `elasticsearch` | 44 | 44 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 114 | `databases` | `flyway-liquibase` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 115 | `databases` | `hibernate-caching` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 116 | `databases` | `hibernate` | 48 | 48 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 117 | `databases` | `hibernate-jpql-criteria` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 118 | `databases` | `hibernate-relationships` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 119 | `databases` | `mongodb` | 46 | 46 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 120 | `databases` | `neo4j` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 121 | `databases` | `postgresql` | 55 | 55 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 122 | `databases` | `redis` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 123 | `databases` | `scylladb` | 23 | 23 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 124 | `databases` | `sql` | 53 | 0 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 125 | `design-patterns` | `design-patterns` | 48 | 48 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 126 | `devops` | `ansible` | 25 | 25 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 127 | `devops` | `argocd` | 42 | 42 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 128 | `devops` | `consul` | 24 | 24 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 129 | `devops` | `docker` | 41 | 41 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 130 | `devops` | `git` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 131 | `devops` | `gradle-maven` | 38 | 38 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 132 | `devops` | `helm` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 133 | `devops` | `istio-service-mesh` | 26 | 26 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 134 | `devops` | `kubernetes` | 45 | 45 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 135 | `devops` | `linkerd` | 20 | 20 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 136 | `devops` | `linux` | 33 | 33 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 137 | `devops` | `terraform` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 138 | `devops` | `vault` | 26 | 26 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 139 | `frameworks/jvm-alternatives` | `ktor` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 140 | `frameworks/jvm-alternatives` | `micronaut` | 25 | — | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 141 | `frameworks/jvm-alternatives` | `quarkus` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 142 | `frameworks/jvm-alternatives` | `vertx` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 143 | `frameworks/spring` | `resilience4j` | 20 | 20 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 144 | `frameworks/spring` | `spring-ai` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 145 | `frameworks/spring` | `spring-aop` | 22 | 22 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 146 | `frameworks/spring` | `spring-async` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 147 | `frameworks/spring` | `spring-batch` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 148 | `frameworks/spring` | `spring-boot-3-migration` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 149 | `frameworks/spring` | `spring-boot-actuator` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 150 | `frameworks/spring` | `spring-boot` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 151 | `frameworks/spring` | `spring-cache` | 17 | 17 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 152 | `frameworks/spring` | `spring-cloud` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 153 | `frameworks/spring` | `spring-data-jdbc` | 16 | 16 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 154 | `frameworks/spring` | `spring-data-jpa` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 155 | `frameworks/spring` | `spring-events` | 16 | 16 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 156 | `frameworks/spring` | `spring-framework` | 40 | 40 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 157 | `frameworks/spring` | `spring-graphql` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 158 | `frameworks/spring` | `spring-integration` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 159 | `frameworks/spring` | `spring-kafka` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 160 | `frameworks/spring` | `spring-messaging` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 161 | `frameworks/spring` | `spring-modulith` | 2 | 15 | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ STUB |
| 162 | `frameworks/spring` | `spring-mvc` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 163 | `frameworks/spring` | `spring-r2dbc` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 164 | `frameworks/spring` | `spring-rest-client` | 13 | 13 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 165 | `frameworks/spring` | `spring-retry` | 17 | 17 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 166 | `frameworks/spring` | `spring-scheduling` | 16 | 16 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 167 | `frameworks/spring` | `spring-security` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 168 | `frameworks/spring` | `spring-session` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 169 | `frameworks/spring` | `spring-state-machine` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 170 | `frameworks/spring` | `spring-testing` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 171 | `frameworks/spring` | `spring-transaction` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 172 | `frameworks/spring` | `spring-validation` | 16 | 16 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 173 | `frameworks/spring` | `spring-vault` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 174 | `frameworks/spring` | `spring-webflux` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 175 | `jvm` | `graalvm-native` | 15 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 176 | `jvm` | `jvm` | 40 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 177 | `leadership` | `code-review-practices` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 178 | `leadership` | `conflict-resolution` | 20 | 20 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 179 | `leadership` | `estimations-planning` | 20 | 20 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 180 | `leadership` | `mentoring` | 1 | 25 | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ STUB |
| 181 | `leadership` | `team-leadership` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 182 | `leadership` | `teching` | 20 | 20 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 183 | `leadership` | `technical-decisions` | 1 | 22 | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ STUB |
| 184 | `logging` | `logging` | 9 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 185 | `messaging` | `aws-sqs-sns` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 186 | `messaging` | `kafka` | 50 | 50 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 187 | `messaging` | `message-brokers-comparison` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 188 | `messaging` | `nats` | 8 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 189 | `messaging` | `pulsar` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 190 | `messaging` | `rabbitmq` | 41 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 191 | `messaging` | `redpanda` | 20 | 20 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 192 | `monitoring` | `elk-stack` | 5 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=5) |
| 193 | `monitoring` | `jaeger-zipkin` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 194 | `monitoring` | `logging-strategies` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 195 | `monitoring` | `loki-grafana` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 196 | `monitoring` | `metrics-tracing` | 6 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=6) |
| 197 | `monitoring` | `micrometer` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 198 | `monitoring` | `observability` | 15 | — | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 199 | `monitoring` | `opentelemetry` | 9 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 200 | `monitoring` | `prometheus-grafana` | 39 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 201 | `performance` | `application-profiling` | 42 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 202 | `performance` | `caching-performance` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 203 | `performance` | `database-performance` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 204 | `performance` | `jvm-performance-tuning` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 205 | `performance` | `memory-management` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 206 | `performance` | `network-performance` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 207 | `performance` | `performance-testing` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 208 | `programming-languages/go` | `go-concurrency` | 35 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 209 | `programming-languages/go` | `go-generics` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 210 | `programming-languages/go` | `go` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 211 | `programming-languages/go` | `go-memory-gc` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 212 | `programming-languages/go` | `go-modules` | 27 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 213 | `programming-languages/go` | `go-stdlib` | 30 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 214 | `programming-languages/go` | `go-testing` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 215 | `programming-languages/java` | `java-17-21` | 42 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 216 | `programming-languages/java` | `java-8` | 41 | 31 | ✅ | ❌ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 217 | `programming-languages/java` | `java-annotations` | 43 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 218 | `programming-languages/java` | `java-collections` | 46 | 34 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 219 | `programming-languages/java` | `java-completable-future` | 13 | 13 | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ THIN |
| 220 | `programming-languages/java` | `java-concurrency` | 56 | 56 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 221 | `programming-languages/java` | `java-conditional-statements` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 222 | `programming-languages/java` | `java-core` | 39 | 28 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 223 | `programming-languages/java` | `java-exceptions` | 42 | 7 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 224 | `programming-languages/java` | `java-functional-interface` | 14 | 14 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 225 | `programming-languages/java` | `java-generics` | 40 | 7 | ✅ | ✅ | ✅ | ❌ | ❌ | ⚠️ JSON-ISSUES |
| 226 | `programming-languages/java` | `java-initialization` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 227 | `programming-languages/java` | `java-io-nio` | 40 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 228 | `programming-languages/java` | `java-jackson` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 229 | `programming-languages/java` | `java-lombok` | 5 | — | ✅ | ❌ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=5) |
| 230 | `programming-languages/java` | `java-mapstruct` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 231 | `programming-languages/java` | `java-modules` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 232 | `programming-languages/java` | `java-oop` | 43 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 233 | `programming-languages/java` | `java-optional` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ THIN |
| 234 | `programming-languages/java` | `java-pattern-matching` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 235 | `programming-languages/java` | `java-records` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 236 | `programming-languages/java` | `java-reflection` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 237 | `programming-languages/java` | `java-serialization` | 40 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 238 | `programming-languages/java` | `java-stream` | 42 | 6 | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ JSON-ISSUES |
| 239 | `programming-languages/java` | `java-string` | 39 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 240 | `programming-languages/java` | `java-types` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 241 | `programming-languages/java` | `java-virtual-threads` | 15 | 15 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 242 | `programming-languages/kotlin` | `kotlin-collections` | 42 | — | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 243 | `programming-languages/kotlin` | `kotlin-coroutines` | 19 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 244 | `programming-languages/kotlin` | `kotlin-dsl` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 245 | `programming-languages/kotlin` | `kotlin-exceptions` | 7 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=7) |
| 246 | `programming-languages/kotlin` | `kotlin-flow` | 17 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 247 | `programming-languages/kotlin` | `kotlin-interop-java` | 38 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 248 | `programming-languages/kotlin` | `kotlin` | 27 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 249 | `programming-languages/kotlin` | `kotlin-sealed-classes` | 15 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 250 | `programming-languages/kotlin` | `kotlin-serialization` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 251 | `programming-languages/kotlin` | `kotlin-spring` | 15 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 252 | `programming-languages/kotlin` | `kotlin-value-classes` | 15 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 253 | `programming-languages/scala` | `scala` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 254 | `reactive` | `project-reactor` | 47 | 47 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 255 | `reactive` | `reactive-patterns` | 26 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 256 | `reactive` | `reactive-streams` | 30 | — | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ NO-JSON |
| 257 | `reactive` | `reactive-testing` | 28 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 258 | `reactive` | `rxjava` | 46 | 46 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 259 | `reactive` | `webflux` | 28 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 260 | `security` | `application-security` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 261 | `security` | `authentication-authorization-patterns` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 262 | `security` | `jwt` | 43 | 43 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 263 | `security` | `mtls` | 20 | 20 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 264 | `security` | `oauth2` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 265 | `security` | `owasp-top10` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 266 | `security` | `secrets-management` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 267 | `security` | `supply-chain-security` | 24 | 24 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 268 | `security` | `tls-ssl` | 45 | 45 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 269 | `security` | `zero-trust` | 19 | 19 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 270 | `system-design` | `design-chat-system` | 21 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ NO-JSON |
| 271 | `system-design` | `design-dropbox` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 272 | `system-design` | `design-elevator-oo` | 26 | 26 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 273 | `system-design` | `design-feed-system` | 30 | 30 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 274 | `system-design` | `design-google-maps` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 275 | `system-design` | `design-instagram` | 27 | 27 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 276 | `system-design` | `design-key-value-store` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 277 | `system-design` | `design-netflix` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 278 | `system-design` | `design-parking-lot-oo` | 28 | 28 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 279 | `system-design` | `design-pastebin` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 280 | `system-design` | `design-payment-system` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 281 | `system-design` | `design-rate-limiter` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 282 | `system-design` | `design-search` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 283 | `system-design` | `design-twitter` | 27 | 27 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 284 | `system-design` | `design-typeahead` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 285 | `system-design` | `design-uber` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 286 | `system-design` | `design-url-shortener` | 30 | 30 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 287 | `system-design` | `design-vending-machine-oo` | 24 | 24 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 288 | `system-design` | `design-web-crawler` | 26 | 26 | ✅ | ✅ | ✅ | ❌ | ✅ | ⚠️ PARTIAL |
| 289 | `system-design` | `design-youtube` | 28 | 28 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 STRUCT-PASS |
| 290 | `system-design` | `system-design` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER-md (h=1) |
| 291 | `testing` | `chaos-engineering` | 44 | 44 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 292 | `testing` | `contract-testing` | 42 | 42 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 293 | `testing` | `integration-testing` | 39 | 39 | ✅ | ❌ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 294 | `testing` | `junit` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 295 | `testing` | `load-testing` | 22 | 22 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 296 | `testing` | `mockito` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 297 | `testing` | `mutation-testing` | 20 | 20 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 298 | `testing` | `property-based-testing` | 1 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER (малформ: только Q1 + ~400 строк неструктур. прозы (TOC обещает Q1-21)) |
| 299 | `testing` | `rest-assured` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 300 | `testing` | `selenium` | 15 | 15 | ✅ | ✅ | ✅ | ❌ | ✅ | 🟢 ALIGNED |
| 301 | `testing` | `test-automation` | 7 | — | ✅ | ✅ | ✅ | ❌ | ❌ | ⏸ DEFER (неполный: реальны Q1-Q7, TOC обещает Q1-30 (Q8-30 отсутствуют)) |
| 302 | `testing` | `test-strategies` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 303 | `testing` | `testcontainers` | 40 | 40 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |
| 304 | `testing` | `unit-testing` | 45 | 45 | ✅ | ✅ | ✅ | ✅ | ✅ | 🟢 ALIGNED |

---

## Phase rollout (priority by impact)

### Phase 1: Phase A1-A6, B1-B7 audit (frontmatter + структура)
Скрипт verify per file: А-критерии быстрые. Fail-list для manual fix.

### Phase 2: Phase C1-C11 audit (content quality)
Самая трудоёмкая phase — требует **read each file**. Cannot full-automate (semantic checks).

### Phase 3: Phase D1-D4 (markdown cleanliness)
Mass batch — Python скрипт удаляет MCQ-callouts/Tier1/wikilinks. **Уже выполнено для всех 304 в предыдущей итерации сессии**, но critery должен быть re-verified per file.

### Phase 4: Phase E1-E2 (Q count, importance markers)
Для STUB (Q < 5) и THIN (5 ≤ Q < 20) — расширить теорию до 25-30 Q через `/interview-writer`.

### Phase 5: Phase F1-F6 + G1-G9 + H1-H2 (JSON генерация + content quality + smoke)
Для NO-JSON файлов — генерировать через `/mcq-quality-fixer`.
Для existing JSON — re-audit на G-criteria (banned phrases, bold, wikilinks в sections).

### Phase 6: JSON-MISMATCH fix (12 файлов)
JSON Q count меньше MD — extends existing JSON недостающими Q.

## QA-проход по человекочитаемости (обновлено 2026-05-28)

Сделан сплошной самореview всех сгенерированных в этой волне JSON (17 файлов).

**Что проверялось и как:**
- Обрыв прозы на полуфразе (dangling): эвристика «секция не кончается на `.?!;:)»` + фильтр код-сниппетов. Результат: **0 реальных обрывов** (trimming целыми предложениями работает).
- `**bold**` / `[[wikilinks]]` / эмодзи в JSON: bold и wikilinks — это **НЕ дефекты**. `MarkdownRenderService` (infrastructure/render) рендерит markdown в HTML и конвертит `[[topic#Qn]]` → кликабельную ссылку `/?topic=...`. Стрелки `→` тоже норм.
- Дубли-дистракторы (две wrong-опции с идентичным текстом): найдено и исправлено **11 в java-concurrency** (Q25/29/30/33/34/37/38/41/45/46/49). В остальных файлах дублей нет.
- Пустоватые `source_of_confusion` (<15 симв): расширено **11 в kubernetes**. Краткие «Похожие имена.»/«Незнание API.» в прочих — оставлены как приемлемые.

**Вывод:** wikilink-формат related — конвенция проекта (58 старых файлов используют его). Новые 17 файлов приведены к этому формату (2237 ссылок).

### Phase 2 прогресс (волна 2026-05-27/28)
Закрыто NO-JSON → STRUCT-PASS: design-pastebin, java-concurrency, mongodb, hibernate, kafka, kubernetes, rxjava, project-reactor, sql, hexagonal-architecture, design-patterns, postgresql, java-virtual-threads, redis, cassandra, spring-framework, microservices = **17 файлов**. В работе: spring-boot.

## Note про `/compact`

`/compact` — pure UI slash-command, недоступная через Skill/Tool API в текущей версии Claude Code. **Технически невозможно** выполнить из бота. Функциональный эквивалент: git commits после каждой итерации (history preserved через compaction).

## Tooling

### Полный per-file audit
```bash
python3 << 'EOF'
import re, json
from pathlib import Path
ROOT = Path('cheatsheets/interview')
JR = Path('modules/quiz-app/src/main/resources/seed/mcq')
WL_RE = re.compile(r'\[\[([a-z][a-z0-9-]{2,}(?:-interview)?(?:#Q\d+)?)(?:\|[^\]]+)?\]\]')

def audit(md_path):
    text = md_path.read_text()
    text_no_code = re.sub(r'```[\s\S]*?```', '', text)
    text_no_code = re.sub(r'`[^`]+`', '', text_no_code)
    rel = md_path.relative_to(ROOT)
    json_path = JR / rel.parent / f'{md_path.stem}.json'
    return {
        'A_fm_complete': all(f in text[:2000] for f in
            ['title:', 'description:', 'tags:', 'aliases:', 'difficulty:', 'updated:']),
        'B_q_count': len(re.findall(r'^## Q\d+\.', text, re.MULTILINE)),
        'B_see_also': len(re.findall(r'\]\([^)]+\.md\)', text[text.find('\n## See also'):])) if '\n## See also' in text else 0,
        'D_mcq': len(re.findall(r'^> \[!mcq\]', text, re.MULTILINE)),
        'D_tier1': len(re.findall(r'❌ ПОСЛЕДСТВИЕ|✓ ПРИМЕНЯТЬ|📋 ПРАВИЛО|🔗 См\.', text)),
        'D_wl': len(WL_RE.findall(text_no_code)),
        'F_has_json': json_path.exists(),
    }

# example
print(audit(Path('cheatsheets/interview/system-design/design-pastebin-interview.md')))
EOF
```

### Mass MCQ-callout strip (если потребуется)
```bash
python3 /tmp/cleanup_md.py
```

### Per-file verify
```bash
bash scripts/verify-md-no-mcq.sh FILE.md
python3 -c "import jsonschema; ..."
./gradlew :quiz-app:test --tests '*McqJsonLoader*' -q
```

---

**Текущий focus:** проходить таблицу сверху-вниз согласно critery A-H. Все строки `⬜ TODO`. Любые предыдущие достижения **не сохраняются** в этой таблице — буквальный сброс прогресса по запросу.
