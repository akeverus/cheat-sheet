---
title: "ULTRATHINK v5 — exhaustive per-file audit plan (всё с нуля)"
description: "Полный аудит всех 305 interview-файлов на 2026-06-01: оформление + человекочитаемость + наличие выровненных human-readable MCQ-json. Детальные критерии A-H + пофайловая таблица + findings."
updated: "2026-06-01"
status: "active"
audit_version: 5
progress: "Lane 1 (callout-strip 151) + Lane 2 A/B (55 single-blob restore+json, e1f74dd0→1cafd2df) + Lane 3 7/8 (одиночные зарытые заголовки, 3f4a09df) ГОТОВЫ. ПОПРАВКА: углублённый TOC-vs-headings аудит вскрыл НОВЫЙ класс дефекта — 13 файлов с зарытыми ХВОСТОВЫМИ вопросами (TOC>заголовков; gate пропустил, т.к. и md, и json обрезаны до K). 199 зарытых вопросов. observability (25) — агент режет блоб сейчас (валидация подхода). Lane 4 — 12 файлов round-2 (logging 32, kotlin-coroutines 20, …, backtracking 3 = 188 вопросов) в очереди на wf-restore-blob.js. Цель «человекочитаемый mcq-json по КАЖДОМУ файлу» = TOC-полнота, не только gate-PASS. Completeness-gate: TOC==## Q==json. Snapshot ниже."
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

## Snapshot аудита — обновлено 2026-06-02 (после Lane 1/2/3 + найден новый класс дефектов)

> Прогон: `/tmp/final_audit.py` + НОВЫЙ TOC-vs-headings аудит. **ВАЖНАЯ ПОПРАВКА: «305/305 gate-PASS» верно ТОЛЬКО для существующих заголовков. Углублённый аудит (число TOC-записей vs число `## Q`-заголовков) вскрыл 13 файлов с ЗАРЫТЫМИ хвостовыми вопросами — gate их пропустил, т.к. И md-заголовки, И json обрезаны до одного K. Это и есть дефект полноты, который требует цель.**

| Метрика | Значение |
|---|---|
| Total interview .md | 305 |
| 🟢 Имеют json gate-PASS (для существующих `## Q`) | 305 / 305 |
| 🟢 Legacy `> [!mcq]` callouts в .md (корпусно) | **0** (было 207 файлов / 6979 callouts) |
| 🟢 NO-JSON | **0** |
| 🔴 **TOC-полнота: файлы с зарытыми вопросами (TOC > `## Q`-заголовков)** | **13** (199 зарытых вопросов) |
| 🟢→ Lane 3 restore-buried (1 зарытый): 7 готово + observability | 7 ✅ (commit 3f4a09df) + 1 🔄 |
| 🔴 Lane 4 round-2 single-blob (много зарытых хвостовых): 12 файлов | 188 вопросов, в очереди |
| Frontmatter incomplete (A1) | 0 |
| Нет H1 / нет See-also | 0 / 0 |
| Запрещённые Tier1 эмодзи-маркеры | 0 |

### Ключевые выводы аудита (2026-06-02)

1. **🟢 Lane 1/2/3(part) ГОТОВЫ.** Lane 1: 151 файл очищен от 5282 callouts (1f9d11bc). Lane 2 A+B: 55 single-blob восстановлены + json (c0059df8…1cafd2df). Lane 3: 7 одиночных зарытых заголовков (3f4a09df).
2. **🔴 НОВЫЙ КЛАСС ДЕФЕКТА — «зарытые хвостовые вопросы» (single-blob round 2).** У 13 файлов TOC обещает Q1..Qmax, но заголовки идут чистым префиксом Q1..QK, а Q(K+1)..Qmax зарыты в блобе ПОСЛЕДНЕГО заголовка. Прошлые проверки пропустили: (а) seq-чек видел заголовки 1..K как «sequential»; (б) gate видел json==заголовки (оба обрезаны до K). Контент зарытых вопросов ПРИСУТСТВУЕТ в блобе (проверено: nats Q8 содержит JetStream/Consumers/RAFT; backtracking Q24 — сложность/production/iterative). **Главный вывод: «наличие человекочитаемого mcq-json по КАЖДОМУ файлу» требует TOC-полноты, а не только gate-PASS существующих заголовков.**
3. **Lane 3 — observability (25 зарытых, частичный single-blob)** — выделенный агент сейчас режет 910-строчный блоб Q1 на Q2–Q26 + регенерирует полный json (40 вопросов). Это и валидация подхода перед фан-аутом Lane 4.
4. **Lane 4 — 12 файлов round-2:** logging(32 зарытых), kotlin-coroutines(20), static-analysis(20), opentelemetry(19), kotlin(18), aws(18), cloud-native-patterns(16), nats(16), consistency-patterns(11), openapi-swagger(9), divide-and-conquer(6), backtracking(3). Фикс (как observability): split блоба последнего заголовка на зарытые вопросы (вставка заголовков, 0 потери прозы) → регенерация полного выровненного json (1..Qmax) → gate → commit.
5. **Новый completeness-gate:** файл «полон» ⟺ `TOC-count == ## Q-count == json-count` И gate-PASS. Старый gate (`## Q == json`) этого НЕ ловит — нужно сверять с TOC.

### Полосы работ (после ре-авторизации 2026-06-01 — Claude правит и .md, и json)

- **Lane 1 — callout-strip (ГОТОВО):** 151 .md очищен от legacy `> [!mcq]`.
- **Lane 2 Phase A/B (ГОТОВО):** 55 single-blob восстановлены + сгенерирован json (батчи 1–5).
- **Lane 3 — restore-buried, 1 зарытый (7/8 ГОТОВО):** 7 файлов закоммичено (3f4a09df); observability (25 зарытых) — агент в работе.
- **Lane 4 — round-2 single-blob, много хвостовых зарытых (В ОЧЕРЕДИ):** 12 файлов, 188 вопросов; workflow `.claude/wf-restore-blob.js`; данные `/tmp/restore_blob_data.json`. Дождаться observability (валидация подхода) → фан-аут → gate completeness → commit.
- **После Lane 4 — финальный completeness re-audit:** TOC == `## Q` == json по всем 305.

## Пофайловая таблица (305 строк) — оформление + json + читаемость

Колонки: **## Q** реальных заголовков, **json** наличие сида, **gate** результат gate_clean, **callouts** число legacy `> [!mcq]`, **blob** single-blob defect, **seq** последовательность нумерации, **ru** доля кириллицы в прозе, **вердикт** оформления.

| # | файл | ## Q | json | gate | callouts | blob | seq | ru | вердикт |
|---|------|------|------|------|----------|------|-----|----|---------|
| 1 | ai-ml/agentic-patterns-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.531 | 🟢 clean |
| 2 | ai-ml/ai-agents-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.221 | 🟢 clean |
| 3 | ai-ml/ai-application-architecture-interview | 32 | ✓ | PASS | 0 |  | ✓ | 0.409 | 🟢 clean |
| 4 | ai-ml/ai-compliance-governance-interview | 32 | ✓ | PASS | 0 |  | ✓ | 0.215 | 🟢 clean |
| 5 | ai-ml/ai-observability-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.481 | 🟢 clean |
| 6 | ai-ml/ai-safety-guardrails-interview | 32 | ✓ | PASS | 0 |  | ✓ | 0.47 | 🟢 clean |
| 7 | ai-ml/code-agents-interview | 31 | ✓ | PASS | 0 |  | ✓ | 0.64 | 🟢 clean |
| 8 | ai-ml/embeddings-interview | 29 | ✓ | PASS | 0 |  | ✓ | 0.258 | 🟢 clean |
| 9 | ai-ml/fine-tuning-llm-interview | 35 | ✓ | PASS | 0 |  | ✓ | 0.495 | 🟢 clean |
| 10 | ai-ml/function-calling-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.523 | 🟢 clean |
| 11 | ai-ml/inference-optimization-interview | 36 | ✓ | PASS | 0 |  | ✓ | 0.377 | 🟢 clean |
| 12 | ai-ml/llm-basics-interview | 30 | ✓ | PASS | 27 |  | ✓ | 0.433 | 🟡 callouts-in-md |
| 13 | ai-ml/llm-evaluation-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.52 | 🟢 clean |
| 14 | ai-ml/llm-integration-patterns-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.191 | 🟢 clean |
| 15 | ai-ml/long-context-vs-rag-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.41 | 🟢 clean |
| 16 | ai-ml/mcp-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.541 | 🟢 clean |
| 17 | ai-ml/mlops-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.203 | 🟢 clean |
| 18 | ai-ml/model-serving-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.207 | 🟢 clean |
| 19 | ai-ml/multi-agent-orchestration-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.492 | 🟢 clean |
| 20 | ai-ml/multimodal-ai-interview | 31 | ✓ | PASS | 0 |  | ✓ | 0.603 | 🟢 clean |
| 21 | ai-ml/open-source-llms-interview | 34 | ✓ | PASS | 0 |  | ✓ | 0.443 | 🟢 clean |
| 22 | ai-ml/prompt-engineering-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.31 | 🟢 clean |
| 23 | ai-ml/rag-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.373 | 🟢 clean |
| 24 | ai-ml/reasoning-models-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.472 | 🟢 clean |
| 25 | ai-ml/vector-databases-interview | 28 | ✓ | PASS | 28 |  | ✓ | 0.357 | 🟡 callouts-in-md |
| 26 | algorithms/algorithmic-paradigms/backtracking-interview | 24 | ✓ | PASS | 0 |  | ✓ | 0.542 | 🟢 clean |
| 27 | algorithms/algorithmic-paradigms/divide-and-conquer-interview | 21 | ✓ | PASS | 0 |  | ✓ | 0.567 | 🟢 clean |
| 28 | algorithms/algorithmic-paradigms/dynamic-programming-interview | 33 | ✓ | PASS | 0 |  | ✓ | 0.583 | 🟢 clean |
| 29 | algorithms/algorithmic-paradigms/greedy-algorithms-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.642 | 🔴 NO-JSON single-blob |
| 30 | algorithms/algorithmic-paradigms/recursion-interview | 27 | ✓ | PASS | 0 |  | ✓ | 0.593 | 🟢 clean |
| 31 | algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview | 1 | — | NA | 33 | ⚠ | ✓ | 0.576 | 🔴 NO-JSON single-blob |
| 32 | algorithms/algorithms-interview | 16 | ✓ | PASS | 0 |  | ✓ | 0.435 | 🟢 clean |
| 33 | algorithms/complexity/complexity-analysis-interview | 31 | ✓ | PASS | 0 |  | ✓ | 0.704 | 🟢 clean |
| 34 | algorithms/data-structures/arrays-strings-interview | 36 | ✓ | PASS | 0 |  | ✓ | 0.682 | 🟢 clean |
| 35 | algorithms/data-structures/graphs-interview | 33 | ✓ | PASS | 0 |  | ✗ | 0.673 | 🟠 ## Q gap |
| 36 | algorithms/data-structures/hash-tables-interview | 34 | ✓ | PASS | 0 |  | ✓ | 0.58 | 🟢 clean |
| 37 | algorithms/data-structures/heaps-interview | 29 | ✓ | PASS | 0 |  | ✓ | 0.56 | 🟢 clean |
| 38 | algorithms/data-structures/linked-lists-interview | 32 | ✓ | PASS | 0 |  | ✓ | 0.646 | 🟢 clean |
| 39 | algorithms/data-structures/stacks-queues-interview | 25 | ✓ | PASS | 0 |  | ✓ | 0.563 | 🟢 clean |
| 40 | algorithms/data-structures/trees-interview | 34 | ✓ | PASS | 0 |  | ✓ | 0.643 | 🟢 clean |
| 41 | algorithms/data-structures/tries-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.544 | 🟢 clean |
| 42 | algorithms/sorting-searching/searching-algorithms-interview | 8 | — | NA | 31 |  | ✓ | 0.558 | 🟠 NO-JSON (generatable) |
| 43 | algorithms/sorting-searching/sorting-algorithms-interview | 1 | — | NA | 31 | ⚠ | ✓ | 0.565 | 🔴 NO-JSON single-blob |
| 44 | api/api-design-best-practices-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.043 | 🟡 callouts-in-md |
| 45 | api/api-versioning-interview | 20 | ✓ | PASS | 0 |  | ✓ | 0.071 | 🟢 clean |
| 46 | api/graphql-interview | 40 | ✓ | PASS | 40 |  | ✓ | 0.721 | 🟡 callouts-in-md |
| 47 | api/grpc-interview | 40 | ✓ | PASS | 40 |  | ✓ | 0.666 | 🟡 callouts-in-md |
| 48 | api/http-rest-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.657 | 🟡 callouts-in-md |
| 49 | api/openapi-swagger-interview | 33 | ✓ | PASS | 33 |  | ✓ | 0.613 | 🟡 callouts-in-md |
| 50 | api/rest-maturity-interview | 17 | ✓ | PASS | 0 |  | ✓ | 0.631 | 🟢 clean |
| 51 | api/websocket-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.599 | 🟡 callouts-in-md |
| 52 | architecture/api-gateway-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.642 | 🟡 callouts-in-md |
| 53 | architecture/bff-pattern-interview | 17 | ✓ | PASS | 17 |  | ✓ | 0.038 | 🟡 callouts-in-md |
| 54 | architecture/caching-strategies-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.707 | 🟡 callouts-in-md |
| 55 | architecture/cap-theorem-interview | 41 | ✓ | PASS | 42 |  | ✗ | 0.743 | 🟡 callouts-in-md |
| 56 | architecture/cdn-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.411 | 🟢 clean |
| 57 | architecture/clean-architecture-interview | 41 | ✓ | PASS | 41 |  | ✓ | 0.669 | 🟡 callouts-in-md |
| 58 | architecture/consistency-patterns-interview | 31 | ✓ | PASS | 42 |  | ✓ | 0.728 | 🟡 callouts-in-md |
| 59 | architecture/cqrs-event-sourcing-interview | 41 | ✓ | PASS | 41 |  | ✓ | 0.766 | 🟡 callouts-in-md |
| 60 | architecture/ddd-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.717 | 🟡 callouts-in-md |
| 61 | architecture/distributed-systems-interview | 40 | ✓ | PASS | 50 |  | ✓ | 0.738 | 🟡 callouts-in-md |
| 62 | architecture/dns-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.496 | 🟢 clean |
| 63 | architecture/edge-computing-interview | 18 | ✓ | PASS | 18 |  | ✓ | 0.02 | 🟡 callouts-in-md |
| 64 | architecture/event-driven-patterns-interview | 40 | ✓ | PASS | 40 |  | ✓ | 0.722 | 🟡 callouts-in-md |
| 65 | architecture/hexagonal-architecture-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.716 | 🟡 callouts-in-md |
| 66 | architecture/latency-numbers-interview | 24 | ✓ | PASS | 0 |  | ✓ | 0.436 | 🟢 clean |
| 67 | architecture/load-balancing-interview | 40 | ✓ | PASS | 40 |  | ✓ | 0.667 | 🟡 callouts-in-md |
| 68 | architecture/microservices-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.74 | 🟡 callouts-in-md |
| 69 | architecture/networking-interview | 43 | ✓ | PASS | 41 |  | ✓ | 0.672 | 🟡 callouts-in-md |
| 70 | architecture/resilience-patterns-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.674 | 🟡 callouts-in-md |
| 71 | architecture/reverse-proxy-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.486 | 🟢 clean |
| 72 | architecture/saga-pattern-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.658 | 🟡 callouts-in-md |
| 73 | architecture/scalability-patterns-interview | 41 | ✓ | PASS | 41 |  | ✓ | 0.749 | 🟡 callouts-in-md |
| 74 | architecture/service-discovery-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.463 | 🟢 clean |
| 75 | architecture/strangler-fig-interview | 18 | ✓ | PASS | 18 |  | ✓ | 0.044 | 🟡 callouts-in-md |
| 76 | behavioral/behavioral-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.911 | 🟡 callouts-in-md |
| 77 | behavioral/conflict-stories-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.943 | 🟢 clean |
| 78 | behavioral/culture-fit-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.941 | 🟢 clean |
| 79 | behavioral/failure-stories-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.926 | 🟢 clean |
| 80 | behavioral/leadership-stories-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.945 | 🟢 clean |
| 81 | behavioral/star-method-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.893 | 🟢 clean |
| 82 | cicd/deployment-strategies-interview | 39 | ✓ | PASS | 39 |  | ✓ | 0.675 | 🟡 callouts-in-md |
| 83 | cicd/pipeline-design-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.619 | 🟡 callouts-in-md |
| 84 | cloud/aws-interview | 16 | ✓ | PASS | 34 |  | ✓ | 0.148 | 🟡 callouts-in-md |
| 85 | cloud/aws-lambda-interview | 1 | — | NA | 32 | ⚠ | ✓ | 0.16 | 🔴 NO-JSON single-blob |
| 86 | cloud/azure-interview | 25 | ✓ | PASS | 25 |  | ✓ | 0.105 | 🟡 callouts-in-md |
| 87 | cloud/cloud-native-patterns-interview | 14 | ✓ | PASS | 30 |  | ✓ | 0.077 | 🟡 callouts-in-md |
| 88 | cloud/gcp-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.146 | 🔴 NO-JSON single-blob |
| 89 | cloud/serverless-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.114 | 🔴 NO-JSON single-blob |
| 90 | code-quality/clean-code-practices-interview | 1 | — | NA | 27 | ⚠ | ✓ | 0.846 | 🔴 NO-JSON single-blob |
| 91 | code-quality/code-coverage-interview | 25 | ✓ | PASS | 25 |  | ✓ | 0.681 | 🟡 callouts-in-md |
| 92 | code-quality/code-review-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.808 | 🔴 NO-JSON single-blob |
| 93 | code-quality/code-smells-interview | 1 | — | NA | 27 | ⚠ | ✓ | 0.746 | 🔴 NO-JSON single-blob |
| 94 | code-quality/refactoring-patterns-interview | 1 | — | NA | 42 | ⚠ | ✓ | 0.763 | 🔴 NO-JSON single-blob |
| 95 | code-quality/static-analysis-interview | 6 | ✓ | PASS | 26 |  | ✓ | 0.643 | 🟡 callouts-in-md |
| 96 | code-quality/technical-debt-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.853 | 🔴 NO-JSON single-blob |
| 97 | data-engineering/apache-airflow-interview | 28 | ✓ | PASS | 28 |  | ✓ | 0.397 | 🟡 callouts-in-md |
| 98 | data-engineering/apache-flink-interview | 1 | — | NA | 31 | ⚠ | ✓ | 0.376 | 🔴 NO-JSON single-blob |
| 99 | data-engineering/apache-spark-interview | 1 | — | NA | 35 | ⚠ | ✓ | 0.42 | 🔴 NO-JSON single-blob |
| 100 | data-engineering/data-lake-lakehouse-interview | 28 | ✓ | PASS | 28 |  | ✓ | 0.312 | 🟡 callouts-in-md |
| 101 | data-engineering/data-warehousing-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.391 | 🟡 callouts-in-md |
| 102 | data-engineering/dbt-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.384 | 🔴 NO-JSON single-blob |
| 103 | data-engineering/kafka-streams-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.373 | 🔴 NO-JSON single-blob |
| 104 | data-engineering/stream-processing-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.316 | 🔴 NO-JSON single-blob |
| 105 | databases/cassandra-interview | 44 | ✓ | PASS | 68 |  | ✓ | 0.683 | 🟡 callouts-in-md |
| 106 | databases/clickhouse-interview | 28 | ✓ | PASS | 38 |  | ✓ | 0.109 | 🟡 callouts-in-md |
| 107 | databases/cockroachdb-interview | 24 | ✓ | PASS | 37 |  | ✓ | 0.084 | 🟡 callouts-in-md |
| 108 | databases/database-architecture-interview | 41 | ✓ | PASS | 41 |  | ✓ | 0.714 | 🟡 callouts-in-md |
| 109 | databases/database-replication-interview | 31 | ✓ | PASS | 0 |  | ✓ | 0.487 | 🟢 clean |
| 110 | databases/database-sharding-interview | 34 | ✓ | PASS | 0 |  | ✓ | 0.495 | 🟢 clean |
| 111 | databases/database-transactions-interview | 42 | ✓ | PASS | 53 |  | ✓ | 0.707 | 🟡 callouts-in-md |
| 112 | databases/dynamodb-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.104 | 🟡 callouts-in-md |
| 113 | databases/elasticsearch-interview | 44 | ✓ | PASS | 50 |  | ✓ | 0.714 | 🟡 callouts-in-md |
| 114 | databases/flyway-liquibase-interview | 42 | ✓ | PASS | 52 |  | ✓ | 0.686 | 🟡 callouts-in-md |
| 115 | databases/hibernate-caching-interview | 15 | ✓ | PASS | 21 |  | ✓ | 0.509 | 🟡 callouts-in-md |
| 116 | databases/hibernate-interview | 48 | ✓ | PASS | 49 |  | ✓ | 0.635 | 🟡 callouts-in-md |
| 117 | databases/hibernate-jpql-criteria-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.487 | 🟢 clean |
| 118 | databases/hibernate-relationships-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.494 | 🟢 clean |
| 119 | databases/mongodb-interview | 46 | ✓ | PASS | 46 |  | ✓ | 0.692 | 🟡 callouts-in-md |
| 120 | databases/neo4j-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.095 | 🟡 callouts-in-md |
| 121 | databases/postgresql-interview | 55 | ✓ | PASS | 55 |  | ✓ | 0.722 | 🟡 callouts-in-md |
| 122 | databases/redis-interview | 43 | ✓ | PASS | 53 |  | ✓ | 0.689 | 🟡 callouts-in-md |
| 123 | databases/scylladb-interview | 23 | ✓ | PASS | 23 |  | ✓ | 0.072 | 🟡 callouts-in-md |
| 124 | databases/sql-interview | 53 | ✓ | PASS | 55 |  | ✓ | 0.7 | 🟡 callouts-in-md |
| 125 | design-patterns/design-patterns-interview | 48 | ✓ | PASS | 56 |  | ✓ | 0.64 | 🟡 callouts-in-md |
| 126 | devops/ansible-interview | 25 | ✓ | PASS | 25 |  | ✓ | 0.073 | 🟡 callouts-in-md |
| 127 | devops/argocd-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.643 | 🟡 callouts-in-md |
| 128 | devops/consul-interview | 24 | ✓ | PASS | 24 |  | ✓ | 0.069 | 🟡 callouts-in-md |
| 129 | devops/docker-interview | 41 | ✓ | PASS | 41 |  | ✓ | 0.662 | 🟡 callouts-in-md |
| 130 | devops/git-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.687 | 🟡 callouts-in-md |
| 131 | devops/gradle-maven-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.664 | 🟡 callouts-in-md |
| 132 | devops/helm-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.631 | 🟡 callouts-in-md |
| 133 | devops/istio-service-mesh-interview | 26 | ✓ | PASS | 26 |  | ✓ | 0.06 | 🟡 callouts-in-md |
| 134 | devops/kubernetes-interview | 45 | ✓ | PASS | 48 |  | ✓ | 0.629 | 🟡 callouts-in-md |
| 135 | devops/linkerd-interview | 20 | ✓ | PASS | 20 |  | ✓ | 0.061 | 🟡 callouts-in-md |
| 136 | devops/linux-interview | 33 | ✓ | PASS | 33 |  | ✓ | 0.619 | 🟡 callouts-in-md |
| 137 | devops/terraform-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.646 | 🟡 callouts-in-md |
| 138 | devops/vault-interview | 26 | ✓ | PASS | 26 |  | ✓ | 0.041 | 🟡 callouts-in-md |
| 139 | frameworks/jvm-alternatives/ktor-interview | 1 | — | NA | 32 | ⚠ | ✓ | 0.513 | 🔴 NO-JSON single-blob |
| 140 | frameworks/jvm-alternatives/micronaut-interview | 25 | ✓ | PASS | 26 |  | ✗ | 0.464 | 🟡 callouts-in-md |
| 141 | frameworks/jvm-alternatives/quarkus-interview | 1 | — | NA | 31 | ⚠ | ✓ | 0.47 | 🔴 NO-JSON single-blob |
| 142 | frameworks/jvm-alternatives/vertx-interview | 1 | — | NA | 31 | ⚠ | ✓ | 0.434 | 🔴 NO-JSON single-blob |
| 143 | frameworks/spring/resilience4j-interview | 20 | ✓ | PASS | 20 |  | ✓ | 0.519 | 🟡 callouts-in-md |
| 144 | frameworks/spring/spring-ai-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.551 | 🟢 clean |
| 145 | frameworks/spring/spring-aop-interview | 22 | ✓ | PASS | 22 |  | ✓ | 0.567 | 🟡 callouts-in-md |
| 146 | frameworks/spring/spring-async-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.52 | 🟢 clean |
| 147 | frameworks/spring/spring-batch-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.598 | 🟡 callouts-in-md |
| 148 | frameworks/spring/spring-boot-3-migration-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.399 | 🟢 clean |
| 149 | frameworks/spring/spring-boot-actuator-interview | 43 | ✓ | PASS | 54 |  | ✓ | 0.59 | 🟡 callouts-in-md |
| 150 | frameworks/spring/spring-boot-interview | 42 | ✓ | PASS | 44 |  | ✓ | 0.583 | 🟡 callouts-in-md |
| 151 | frameworks/spring/spring-cache-interview | 17 | ✓ | PASS | 18 |  | ✗ | 0.597 | 🟡 callouts-in-md |
| 152 | frameworks/spring/spring-cloud-interview | 43 | ✓ | PASS | 44 |  | ✓ | 0.545 | 🟡 callouts-in-md |
| 153 | frameworks/spring/spring-data-jdbc-interview | 16 | ✓ | PASS | 16 |  | ✓ | 0.63 | 🟡 callouts-in-md |
| 154 | frameworks/spring/spring-data-jpa-interview | 42 | ✓ | PASS | 48 |  | ✓ | 0.639 | 🟡 callouts-in-md |
| 155 | frameworks/spring/spring-events-interview | 16 | ✓ | PASS | 16 |  | ✓ | 0.59 | 🟡 callouts-in-md |
| 156 | frameworks/spring/spring-framework-interview | 40 | ✓ | PASS | 49 |  | ✓ | 0.61 | 🟡 callouts-in-md |
| 157 | frameworks/spring/spring-graphql-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.449 | 🟢 clean |
| 158 | frameworks/spring/spring-integration-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.521 | 🟢 clean |
| 159 | frameworks/spring/spring-kafka-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.539 | 🟢 clean |
| 160 | frameworks/spring/spring-messaging-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.422 | 🟢 clean |
| 161 | frameworks/spring/spring-modulith-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.68 | 🟢 clean |
| 162 | frameworks/spring/spring-mvc-interview | 43 | ✓ | PASS | 57 |  | ✓ | 0.601 | 🟡 callouts-in-md |
| 163 | frameworks/spring/spring-r2dbc-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.51 | 🟢 clean |
| 164 | frameworks/spring/spring-rest-client-interview | 13 | ✓ | PASS | 14 |  | ✓ | 0.546 | 🟡 callouts-in-md |
| 165 | frameworks/spring/spring-retry-interview | 17 | ✓ | PASS | 17 |  | ✓ | 0.574 | 🟡 callouts-in-md |
| 166 | frameworks/spring/spring-scheduling-interview | 16 | ✓ | PASS | 15 |  | ✓ | 0.583 | 🟡 callouts-in-md |
| 167 | frameworks/spring/spring-security-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.556 | 🟡 callouts-in-md |
| 168 | frameworks/spring/spring-session-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.522 | 🟢 clean |
| 169 | frameworks/spring/spring-state-machine-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.613 | 🟡 callouts-in-md |
| 170 | frameworks/spring/spring-testing-interview | 1 | — | NA | 15 | ⚠ | ✓ | 0.513 | 🔴 NO-JSON single-blob |
| 171 | frameworks/spring/spring-transaction-interview | 15 | ✓ | PASS | 22 |  | ✓ | 0.551 | 🟡 callouts-in-md |
| 172 | frameworks/spring/spring-validation-interview | 16 | ✓ | PASS | 15 |  | ✓ | 0.509 | 🟡 callouts-in-md |
| 173 | frameworks/spring/spring-vault-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.601 | 🟡 callouts-in-md |
| 174 | frameworks/spring/spring-webflux-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.733 | 🟡 callouts-in-md |
| 175 | jvm/graalvm-native-interview | 15 | ✓ | PASS | 14 |  | ✓ | 0.549 | 🟡 callouts-in-md |
| 176 | jvm/jvm-interview | 40 | ✓ | PASS | 53 |  | ✓ | 0.654 | 🟡 callouts-in-md |
| 177 | leadership/code-review-practices-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.801 | 🔴 NO-JSON single-blob |
| 178 | leadership/conflict-resolution-interview | 20 | ✓ | PASS | 0 |  | ✓ | 0.916 | 🟢 clean |
| 179 | leadership/estimations-planning-interview | 20 | ✓ | PASS | 0 |  | ✓ | 0.868 | 🟢 clean |
| 180 | leadership/mentoring-interview | 25 | ✓ | PASS | 0 |  | ✓ | 0.915 | 🟢 clean |
| 181 | leadership/team-leadership-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.917 | 🔴 NO-JSON single-blob |
| 182 | leadership/tech-interviewing-interview | 20 | ✓ | PASS | 0 |  | ✓ | 0.897 | 🟢 clean |
| 183 | leadership/technical-decisions-interview | 22 | ✓ | PASS | 0 |  | ✓ | 0.879 | 🟢 clean |
| 184 | logging/logging-interview | 9 | ✓ | PASS | 41 |  | ✓ | 0.647 | 🟡 callouts-in-md |
| 185 | messaging/aws-sqs-sns-interview | 1 | — | NA | 22 | ⚠ | ✓ | 0.074 | 🔴 NO-JSON single-blob |
| 186 | messaging/kafka-interview | 50 | ✓ | PASS | 50 |  | ✓ | 0.587 | 🟡 callouts-in-md |
| 187 | messaging/message-brokers-comparison-interview | 1 | — | NA | 26 | ⚠ | ✓ | 0.107 | 🔴 NO-JSON single-blob |
| 188 | messaging/nats-interview | 8 | ✓ | PASS | 24 |  | ✓ | 0.107 | 🟡 callouts-in-md |
| 189 | messaging/pulsar-interview | 1 | — | NA | 22 | ⚠ | ✓ | 0.078 | 🔴 NO-JSON single-blob |
| 190 | messaging/rabbitmq-interview | 41 | ✓ | PASS | 60 |  | ✓ | 0.604 | 🟡 callouts-in-md |
| 191 | messaging/redpanda-interview | 20 | ✓ | PASS | 0 |  | ✓ | 0.074 | 🟢 clean |
| 192 | monitoring/elk-stack-interview | 5 | — | NA | 26 |  | ✓ | 0.129 | 🔴 NO-JSON single-blob |
| 193 | monitoring/jaeger-zipkin-interview | 1 | — | NA | 23 | ⚠ | ✓ | 0.137 | 🔴 NO-JSON single-blob |
| 194 | monitoring/logging-strategies-interview | 1 | — | NA | 38 | ⚠ | ✓ | 0.708 | 🔴 NO-JSON single-blob |
| 195 | monitoring/loki-grafana-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.115 | 🔴 NO-JSON single-blob |
| 196 | monitoring/metrics-tracing-interview | 6 | — | NA | 41 |  | ✓ | 0.653 | 🔴 NO-JSON single-blob |
| 197 | monitoring/micrometer-interview | 1 | — | NA | 20 | ⚠ | ✓ | 0.549 | 🔴 NO-JSON single-blob |
| 198 | monitoring/observability-interview | 15 | ✓ | PASS | 40 |  | ✗ | 0.62 | 🟡 callouts-in-md |
| 199 | monitoring/opentelemetry-interview | 9 | ✓ | PASS | 28 |  | ✓ | 0.126 | 🟡 callouts-in-md |
| 200 | monitoring/prometheus-grafana-interview | 39 | ✓ | PASS | 39 |  | ✓ | 0.631 | 🟡 callouts-in-md |
| 201 | performance/application-profiling-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.608 | 🟡 callouts-in-md |
| 202 | performance/caching-performance-interview | 1 | — | NA | 23 | ⚠ | ✓ | 0.033 | 🔴 NO-JSON single-blob |
| 203 | performance/database-performance-interview | 1 | — | NA | 27 | ⚠ | ✓ | 0.046 | 🔴 NO-JSON single-blob |
| 204 | performance/jvm-performance-tuning-interview | 1 | — | NA | 38 | ⚠ | ✓ | 0.62 | 🔴 NO-JSON single-blob |
| 205 | performance/memory-management-interview | 1 | — | NA | 39 | ⚠ | ✓ | 0.611 | 🔴 NO-JSON single-blob |
| 206 | performance/network-performance-interview | 1 | — | NA | 24 | ⚠ | ✓ | 0.028 | 🔴 NO-JSON single-blob |
| 207 | performance/performance-testing-interview | 1 | — | NA | 42 | ⚠ | ✓ | 0.598 | 🔴 NO-JSON single-blob |
| 208 | programming-languages/go/go-concurrency-interview | 35 | ✓ | PASS | 35 |  | ✓ | 0.497 | 🟡 callouts-in-md |
| 209 | programming-languages/go/go-generics-interview | 1 | — | NA | 26 | ⚠ | ✓ | 0.471 | 🔴 NO-JSON single-blob |
| 210 | programming-languages/go/go-interview | 1 | — | NA | 36 | ⚠ | ✓ | 0.503 | 🔴 NO-JSON single-blob |
| 211 | programming-languages/go/go-memory-gc-interview | 1 | — | NA | 27 | ⚠ | ✓ | 0.52 | 🔴 NO-JSON single-blob |
| 212 | programming-languages/go/go-modules-interview | 27 | ✓ | PASS | 27 |  | ✓ | 0.587 | 🟡 callouts-in-md |
| 213 | programming-languages/go/go-stdlib-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.444 | 🟡 callouts-in-md |
| 214 | programming-languages/go/go-testing-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.497 | 🔴 NO-JSON single-blob |
| 215 | programming-languages/java/java-17-21-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.618 | 🟡 callouts-in-md |
| 216 | programming-languages/java/java-8-interview | 41 | ✓ | PASS | 0 |  | ✗ | 0.702 | 🟠 ## Q gap |
| 217 | programming-languages/java/java-annotations-interview | 43 | ✓ | PASS | 53 |  | ✓ | 0.65 | 🟡 callouts-in-md |
| 218 | programming-languages/java/java-collections-interview | 46 | ✓ | PASS | 0 |  | ✓ | 0.641 | 🟢 clean |
| 219 | programming-languages/java/java-completable-future-interview | 13 | ✓ | PASS | 0 |  | ✓ | 0.53 | 🟢 clean |
| 220 | programming-languages/java/java-concurrency-interview | 56 | ✓ | PASS | 57 |  | ✓ | 0.635 | 🟡 callouts-in-md |
| 221 | programming-languages/java/java-conditional-statements-interview | 1 | — | NA | 42 | ⚠ | ✓ | 0.643 | 🔴 NO-JSON single-blob |
| 222 | programming-languages/java/java-core-interview | 39 | ✓ | PASS | 0 |  | ✓ | 0.67 | 🟢 clean |
| 223 | programming-languages/java/java-exceptions-interview | 42 | ✓ | PASS | 0 |  | ✓ | 0.65 | 🟢 clean |
| 224 | programming-languages/java/java-functional-interface-interview | 14 | ✓ | PASS | 0 |  | ✓ | 0.645 | 🟢 clean |
| 225 | programming-languages/java/java-generics-interview | 40 | ✓ | PASS | 0 |  | ✓ | 0.676 | 🟢 clean |
| 226 | programming-languages/java/java-initialization-interview | 1 | — | NA | 27 | ⚠ | ✓ | 0.681 | 🔴 NO-JSON single-blob |
| 227 | programming-languages/java/java-io-nio-interview | 40 | ✓ | PASS | 51 |  | ✓ | 0.666 | 🟡 callouts-in-md |
| 228 | programming-languages/java/java-jackson-interview | 1 | — | NA | 31 | ⚠ | ✓ | 0.547 | 🔴 NO-JSON single-blob |
| 229 | programming-languages/java/java-lombok-interview | 5 | — | NA | 27 |  | ✗ | 0.554 | 🔴 NO-JSON single-blob |
| 230 | programming-languages/java/java-mapstruct-interview | 1 | — | NA | 28 | ⚠ | ✓ | 0.616 | 🔴 NO-JSON single-blob |
| 231 | programming-languages/java/java-modules-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.682 | 🟡 callouts-in-md |
| 232 | programming-languages/java/java-oop-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.742 | 🟡 callouts-in-md |
| 233 | programming-languages/java/java-optional-interview | 15 | ✓ | PASS | 0 |  | ✓ | 0.606 | 🟢 clean |
| 234 | programming-languages/java/java-pattern-matching-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.47 | 🟡 callouts-in-md |
| 235 | programming-languages/java/java-records-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.52 | 🟡 callouts-in-md |
| 236 | programming-languages/java/java-reflection-interview | 1 | — | NA | 16 | ⚠ | ✓ | 0.549 | 🔴 NO-JSON single-blob |
| 237 | programming-languages/java/java-serialization-interview | 40 | ✓ | PASS | 39 |  | ✓ | 0.697 | 🟡 callouts-in-md |
| 238 | programming-languages/java/java-stream-interview | 42 | ✓ | PASS | 0 |  | ✓ | 0.632 | 🟢 clean |
| 239 | programming-languages/java/java-string-interview | 39 | ✓ | PASS | 43 |  | ✓ | 0.669 | 🟡 callouts-in-md |
| 240 | programming-languages/java/java-types-interview | 38 | ✓ | PASS | 37 |  | ✓ | 0.736 | 🟡 callouts-in-md |
| 241 | programming-languages/java/java-virtual-threads-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.587 | 🟡 callouts-in-md |
| 242 | programming-languages/kotlin/kotlin-collections-interview | 42 | ✓ | PASS | 43 |  | ✗ | 0.639 | 🟡 callouts-in-md |
| 243 | programming-languages/kotlin/kotlin-coroutines-interview | 19 | ✓ | PASS | 39 |  | ✓ | 0.676 | 🟡 callouts-in-md |
| 244 | programming-languages/kotlin/kotlin-dsl-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.666 | 🔴 NO-JSON single-blob |
| 245 | programming-languages/kotlin/kotlin-exceptions-interview | 7 | — | NA | 40 |  | ✓ | 0.682 | 🔴 NO-JSON single-blob |
| 246 | programming-languages/kotlin/kotlin-flow-interview | 17 | ✓ | PASS | 17 |  | ✓ | 0.552 | 🟡 callouts-in-md |
| 247 | programming-languages/kotlin/kotlin-interop-java-interview | 38 | ✓ | PASS | 38 |  | ✓ | 0.636 | 🟡 callouts-in-md |
| 248 | programming-languages/kotlin/kotlin-interview | 27 | ✓ | PASS | 45 |  | ✓ | 0.611 | 🟡 callouts-in-md |
| 249 | programming-languages/kotlin/kotlin-sealed-classes-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.532 | 🟡 callouts-in-md |
| 250 | programming-languages/kotlin/kotlin-serialization-interview | 1 | — | NA | 43 | ⚠ | ✓ | 0.638 | 🔴 NO-JSON single-blob |
| 251 | programming-languages/kotlin/kotlin-spring-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.501 | 🟡 callouts-in-md |
| 252 | programming-languages/kotlin/kotlin-value-classes-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.542 | 🟡 callouts-in-md |
| 253 | programming-languages/scala/scala-interview | 1 | — | NA | 40 | ⚠ | ✓ | 0.467 | 🔴 NO-JSON single-blob |
| 254 | reactive/project-reactor-interview | 47 | ✓ | PASS | 47 |  | ✓ | 0.634 | 🟡 callouts-in-md |
| 255 | reactive/reactive-patterns-interview | 26 | ✓ | PASS | 26 |  | ✓ | 0.651 | 🟡 callouts-in-md |
| 256 | reactive/reactive-streams-interview | 30 | ✓ | PASS | 30 |  | ✓ | 0.582 | 🟡 callouts-in-md |
| 257 | reactive/reactive-testing-interview | 28 | ✓ | PASS | 28 |  | ✓ | 0.572 | 🟡 callouts-in-md |
| 258 | reactive/rxjava-interview | 46 | ✓ | PASS | 46 |  | ✓ | 0.645 | 🟡 callouts-in-md |
| 259 | reactive/webflux-interview | 28 | ✓ | PASS | 28 |  | ✓ | 0.563 | 🟡 callouts-in-md |
| 260 | security/application-security-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.66 | 🟡 callouts-in-md |
| 261 | security/authentication-authorization-patterns-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.618 | 🟡 callouts-in-md |
| 262 | security/jwt-interview | 43 | ✓ | PASS | 43 |  | ✓ | 0.654 | 🟡 callouts-in-md |
| 263 | security/mtls-interview | 20 | ✓ | PASS | 20 |  | ✓ | 0.039 | 🟡 callouts-in-md |
| 264 | security/oauth2-interview | 42 | ✓ | PASS | 42 |  | ✓ | 0.556 | 🟡 callouts-in-md |
| 265 | security/owasp-top10-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.675 | 🟡 callouts-in-md |
| 266 | security/secrets-management-interview | 22 | ✓ | PASS | 22 |  | ✓ | 0.042 | 🟡 callouts-in-md |
| 267 | security/supply-chain-security-interview | 24 | ✓ | PASS | 24 |  | ✓ | 0.048 | 🟡 callouts-in-md |
| 268 | security/tls-ssl-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.638 | 🟡 callouts-in-md |
| 269 | security/zero-trust-interview | 19 | ✓ | PASS | 19 |  | ✓ | 0.036 | 🟡 callouts-in-md |
| 270 | system-design/design-chat-system-interview | 21 | ✓ | PASS | 21 |  | ✓ | 0.021 | 🟡 callouts-in-md |
| 271 | system-design/design-dropbox-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.078 | 🟢 clean |
| 272 | system-design/design-ecommerce-delivery-interview | 36 | ✓ | PASS | 0 |  | ✓ | 0.751 | 🟢 clean |
| 273 | system-design/design-elevator-oo-interview | 26 | ✓ | PASS | 0 |  | ✓ | 0.475 | 🟢 clean |
| 274 | system-design/design-feed-system-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.315 | 🟢 clean |
| 275 | system-design/design-google-maps-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.105 | 🟢 clean |
| 276 | system-design/design-instagram-interview | 27 | ✓ | PASS | 0 |  | ✓ | 0.349 | 🟢 clean |
| 277 | system-design/design-key-value-store-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.594 | 🟢 clean |
| 278 | system-design/design-netflix-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.117 | 🟢 clean |
| 279 | system-design/design-parking-lot-oo-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.606 | 🟢 clean |
| 280 | system-design/design-pastebin-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.102 | 🟢 clean |
| 281 | system-design/design-payment-system-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.266 | 🟢 clean |
| 282 | system-design/design-rate-limiter-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.245 | 🟢 clean |
| 283 | system-design/design-search-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.169 | 🟢 clean |
| 284 | system-design/design-twitter-interview | 27 | ✓ | PASS | 0 |  | ✓ | 0.412 | 🟢 clean |
| 285 | system-design/design-typeahead-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.382 | 🟢 clean |
| 286 | system-design/design-uber-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.466 | 🟢 clean |
| 287 | system-design/design-url-shortener-interview | 30 | ✓ | PASS | 0 |  | ✓ | 0.162 | 🟢 clean |
| 288 | system-design/design-vending-machine-oo-interview | 24 | ✓ | PASS | 0 |  | ✓ | 0.381 | 🟢 clean |
| 289 | system-design/design-web-crawler-interview | 26 | ✓ | PASS | 0 |  | ✓ | 0.441 | 🟢 clean |
| 290 | system-design/design-youtube-interview | 28 | ✓ | PASS | 0 |  | ✓ | 0.281 | 🟢 clean |
| 291 | system-design/system-design-interview | 1 | — | NA | 41 | ⚠ | ✓ | 0.611 | 🔴 NO-JSON single-blob |
| 292 | testing/chaos-engineering-interview | 44 | ✓ | PASS | 43 |  | ✓ | 0.639 | 🟡 callouts-in-md |
| 293 | testing/contract-testing-interview | 42 | ✓ | PASS | 0 |  | ✓ | 0.657 | 🟢 clean |
| 294 | testing/integration-testing-interview | 39 | ✓ | PASS | 40 |  | ✗ | 0.666 | 🟡 callouts-in-md |
| 295 | testing/junit-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.564 | 🟡 callouts-in-md |
| 296 | testing/load-testing-interview | 22 | ✓ | PASS | 22 |  | ✓ | 0.052 | 🟡 callouts-in-md |
| 297 | testing/mockito-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.67 | 🟡 callouts-in-md |
| 298 | testing/mutation-testing-interview | 20 | ✓ | PASS | 19 |  | ✓ | 0.084 | 🟡 callouts-in-md |
| 299 | testing/property-based-testing-interview | 1 | — | NA | 21 | ⚠ | ✓ | 0.053 | 🔴 NO-JSON single-blob |
| 300 | testing/rest-assured-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.5 | 🟡 callouts-in-md |
| 301 | testing/selenium-interview | 15 | ✓ | PASS | 15 |  | ✓ | 0.569 | 🟡 callouts-in-md |
| 302 | testing/test-automation-interview | 7 | — | NA | 50 |  | ✓ | 0.676 | 🔴 NO-JSON single-blob |
| 303 | testing/test-strategies-interview | 45 | ✓ | PASS | 45 |  | ✓ | 0.656 | 🟡 callouts-in-md |
| 304 | testing/testcontainers-interview | 40 | ✓ | PASS | 39 |  | ✓ | 0.659 | 🟡 callouts-in-md |
| 305 | testing/unit-testing-interview | 45 | ✓ | PASS | 44 |  | ✓ | 0.683 | 🟡 callouts-in-md |

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
