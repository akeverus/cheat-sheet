---
title: "ULTRATHINK v5 — exhaustive per-file audit plan (всё с нуля)"
description: "Полный план аудита всех 304 interview-файлов. Прогресс сброшен в ноль. Детализированные критерии по 8 группам (A-H) с конкретными метриками."
updated: "2026-05-27"
status: "active"
audit_version: 5
progress: "RESET — 0/304 проверено"
---

# ULTRATHINK v5 — exhaustive per-file audit plan

> **Прогресс сброшен.** Все 304 interview-файла помечены `⬜ TODO`. Каждый должен пройти полный checklist A-H **с нуля**, независимо от истории работы в любой сессии.

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

| # | Category | File | Q in MD | JSON Q | Size KB | Status |
|---|---|---|---|---|---|---|
| 1 | `ai-ml` | `agentic-patterns` | 30 | 30 | 168 | ⬜ TODO |
| 2 | `ai-ml` | `ai-agents` | 38 | 38 | 280 | ⬜ TODO |
| 3 | `ai-ml` | `ai-application-architecture` | 32 | 32 | 134 | ⬜ TODO |
| 4 | `ai-ml` | `ai-compliance-governance` | 32 | 32 | 155 | ⬜ TODO |
| 5 | `ai-ml` | `ai-observability` | 28 | 28 | 163 | ⬜ TODO |
| 6 | `ai-ml` | `ai-safety-guardrails` | 32 | 32 | 154 | ⬜ TODO |
| 7 | `ai-ml` | `code-agents` | 31 | 31 | 183 | ⬜ TODO |
| 8 | `ai-ml` | `embeddings` | 39 | 39 | 288 | ⬜ TODO |
| 9 | `ai-ml` | `fine-tuning-llm` | 35 | 35 | 157 | ⬜ TODO |
| 10 | `ai-ml` | `function-calling` | 30 | 30 | 170 | ⬜ TODO |
| 11 | `ai-ml` | `inference-optimization` | 36 | 36 | 177 | ⬜ TODO |
| 12 | `ai-ml` | `llm-basics` | 30 | 30 | 168 | ⬜ TODO |
| 13 | `ai-ml` | `llm-evaluation` | 30 | 30 | 161 | ⬜ TODO |
| 14 | `ai-ml` | `llm-integration-patterns` | 38 | 38 | 303 | ⬜ TODO |
| 15 | `ai-ml` | `long-context-vs-rag` | 30 | 30 | 180 | ⬜ TODO |
| 16 | `ai-ml` | `mcp` | 30 | 30 | 149 | ⬜ TODO |
| 17 | `ai-ml` | `mlops` | 38 | 38 | 270 | ⬜ TODO |
| 18 | `ai-ml` | `model-serving` | 38 | 38 | 267 | ⬜ TODO |
| 19 | `ai-ml` | `multi-agent-orchestration` | 30 | 30 | 190 | ⬜ TODO |
| 20 | `ai-ml` | `multimodal-ai` | 31 | 31 | 140 | ⬜ TODO |
| 21 | `ai-ml` | `open-source-llms` | 34 | 34 | 171 | ⬜ TODO |
| 22 | `ai-ml` | `prompt-engineering` | 38 | 38 | 262 | ⬜ TODO |
| 23 | `ai-ml` | `rag` | 40 | 40 | 290 | ⬜ TODO |
| 24 | `ai-ml` | `reasoning-models` | 30 | 30 | 121 | ⬜ TODO |
| 25 | `ai-ml` | `vector-databases` | 28 | 28 | 159 | ⬜ TODO |
| 26 | `algorithms` | `algorithms` | 16 | 16 | 101 | ⬜ TODO |
| 27 | `algorithms/algorithmic-paradigms` | `backtracking` | 24 | 23 | 166 | ⬜ TODO |
| 28 | `algorithms/algorithmic-paradigms` | `divide-and-conquer` | 21 | 21 | 152 | ⬜ TODO |
| 29 | `algorithms/algorithmic-paradigms` | `dynamic-programming` | 33 | 33 | 231 | ⬜ TODO |
| 30 | `algorithms/algorithmic-paradigms` | `greedy-algorithms` | 1 | — | — | ⬜ TODO |
| 31 | `algorithms/algorithmic-paradigms` | `recursion` | 27 | — | — | ⬜ TODO |
| 32 | `algorithms/algorithmic-paradigms` | `two-pointers-sliding-window` | 1 | — | — | ⬜ TODO |
| 33 | `algorithms/complexity` | `complexity-analysis` | 31 | 31 | 222 | ⬜ TODO |
| 34 | `algorithms/data-structures` | `arrays-strings` | 36 | 36 | 238 | ⬜ TODO |
| 35 | `algorithms/data-structures` | `graphs` | 33 | 31 | 263 | ⬜ TODO |
| 36 | `algorithms/data-structures` | `hash-tables` | 34 | 32 | 247 | ⬜ TODO |
| 37 | `algorithms/data-structures` | `heaps` | 29 | 29 | 198 | ⬜ TODO |
| 38 | `algorithms/data-structures` | `linked-lists` | 32 | 32 | 233 | ⬜ TODO |
| 39 | `algorithms/data-structures` | `stacks-queues` | 25 | 25 | 234 | ⬜ TODO |
| 40 | `algorithms/data-structures` | `trees` | 34 | 34 | 258 | ⬜ TODO |
| 41 | `algorithms/data-structures` | `tries` | 28 | 28 | 199 | ⬜ TODO |
| 42 | `algorithms/sorting-searching` | `searching-algorithms` | 8 | — | — | ⬜ TODO |
| 43 | `algorithms/sorting-searching` | `sorting-algorithms` | 1 | — | — | ⬜ TODO |
| 44 | `api` | `api-design-best-practices` | 30 | — | — | ⬜ TODO |
| 45 | `api` | `api-versioning` | 20 | 20 | 166 | ⬜ TODO |
| 46 | `api` | `graphql` | 40 | — | — | ⬜ TODO |
| 47 | `api` | `grpc` | 40 | — | — | ⬜ TODO |
| 48 | `api` | `http-rest` | 43 | — | — | ⬜ TODO |
| 49 | `api` | `openapi-swagger` | 33 | — | — | ⬜ TODO |
| 50 | `api` | `rest-maturity` | 17 | 17 | 109 | ⬜ TODO |
| 51 | `api` | `websocket` | 38 | — | — | ⬜ TODO |
| 52 | `architecture` | `api-gateway` | 38 | — | — | ⬜ TODO |
| 53 | `architecture` | `bff-pattern` | 17 | — | — | ⬜ TODO |
| 54 | `architecture` | `caching-strategies` | 42 | — | — | ⬜ TODO |
| 55 | `architecture` | `cap-theorem` | 41 | — | — | ⬜ TODO |
| 56 | `architecture` | `cdn` | 30 | 30 | 168 | ⬜ TODO |
| 57 | `architecture` | `clean-architecture` | 41 | — | — | ⬜ TODO |
| 58 | `architecture` | `consistency-patterns` | 31 | — | — | ⬜ TODO |
| 59 | `architecture` | `cqrs-event-sourcing` | 41 | — | — | ⬜ TODO |
| 60 | `architecture` | `ddd` | 38 | — | — | ⬜ TODO |
| 61 | `architecture` | `distributed-systems` | 40 | — | — | ⬜ TODO |
| 62 | `architecture` | `dns` | 30 | 30 | 151 | ⬜ TODO |
| 63 | `architecture` | `edge-computing` | 18 | — | — | ⬜ TODO |
| 64 | `architecture` | `event-driven-patterns` | 40 | — | — | ⬜ TODO |
| 65 | `architecture` | `hexagonal-architecture` | 45 | — | — | ⬜ TODO |
| 66 | `architecture` | `latency-numbers` | 24 | 24 | 150 | ⬜ TODO |
| 67 | `architecture` | `load-balancing` | 40 | — | — | ⬜ TODO |
| 68 | `architecture` | `microservices` | 42 | — | — | ⬜ TODO |
| 69 | `architecture` | `networking` | 43 | — | — | ⬜ TODO |
| 70 | `architecture` | `resilience-patterns` | 43 | — | — | ⬜ TODO |
| 71 | `architecture` | `reverse-proxy` | 30 | 30 | 172 | ⬜ TODO |
| 72 | `architecture` | `saga-pattern` | 43 | — | — | ⬜ TODO |
| 73 | `architecture` | `scalability-patterns` | 41 | — | — | ⬜ TODO |
| 74 | `architecture` | `service-discovery` | 30 | 30 | 173 | ⬜ TODO |
| 75 | `architecture` | `strangler-fig` | 18 | — | — | ⬜ TODO |
| 76 | `behavioral` | `behavioral` | 38 | — | — | ⬜ TODO |
| 77 | `behavioral` | `conflict-stories` | 22 | 22 | 181 | ⬜ TODO |
| 78 | `behavioral` | `culture-fit` | 22 | 22 | 153 | ⬜ TODO |
| 79 | `behavioral` | `failure-stories` | 22 | 22 | 179 | ⬜ TODO |
| 80 | `behavioral` | `leadership-stories` | 22 | 22 | 184 | ⬜ TODO |
| 81 | `behavioral` | `star-method` | 22 | 22 | 183 | ⬜ TODO |
| 82 | `cicd` | `deployment-strategies` | 39 | — | — | ⬜ TODO |
| 83 | `cicd` | `pipeline-design` | 38 | — | — | ⬜ TODO |
| 84 | `cloud` | `aws` | 16 | — | — | ⬜ TODO |
| 85 | `cloud` | `aws-lambda` | 1 | — | — | ⬜ TODO |
| 86 | `cloud` | `azure` | 25 | — | — | ⬜ TODO |
| 87 | `cloud` | `cloud-native-patterns` | 14 | — | — | ⬜ TODO |
| 88 | `cloud` | `gcp` | 1 | — | — | ⬜ TODO |
| 89 | `cloud` | `serverless` | 1 | — | — | ⬜ TODO |
| 90 | `code-quality` | `clean-code-practices` | 1 | — | — | ⬜ TODO |
| 91 | `code-quality` | `code-coverage` | 25 | — | — | ⬜ TODO |
| 92 | `code-quality` | `code-review` | 1 | — | — | ⬜ TODO |
| 93 | `code-quality` | `code-smells` | 1 | — | — | ⬜ TODO |
| 94 | `code-quality` | `refactoring-patterns` | 1 | — | — | ⬜ TODO |
| 95 | `code-quality` | `static-analysis` | 6 | — | — | ⬜ TODO |
| 96 | `code-quality` | `technical-debt` | 1 | — | — | ⬜ TODO |
| 97 | `data-engineering` | `apache-airflow` | 28 | — | — | ⬜ TODO |
| 98 | `data-engineering` | `apache-flink` | 1 | — | — | ⬜ TODO |
| 99 | `data-engineering` | `apache-spark` | 1 | — | — | ⬜ TODO |
| 100 | `data-engineering` | `data-lake-lakehouse` | 28 | — | — | ⬜ TODO |
| 101 | `data-engineering` | `data-warehousing` | 30 | — | — | ⬜ TODO |
| 102 | `data-engineering` | `dbt` | 1 | — | — | ⬜ TODO |
| 103 | `data-engineering` | `kafka-streams` | 1 | — | — | ⬜ TODO |
| 104 | `data-engineering` | `stream-processing` | 1 | — | — | ⬜ TODO |
| 105 | `databases` | `cassandra` | 44 | — | — | ⬜ TODO |
| 106 | `databases` | `clickhouse` | 28 | — | — | ⬜ TODO |
| 107 | `databases` | `cockroachdb` | 24 | — | — | ⬜ TODO |
| 108 | `databases` | `database-architecture` | 41 | — | — | ⬜ TODO |
| 109 | `databases` | `database-replication` | 31 | 31 | 191 | ⬜ TODO |
| 110 | `databases` | `database-sharding` | 34 | 34 | 209 | ⬜ TODO |
| 111 | `databases` | `database-transactions` | 42 | — | — | ⬜ TODO |
| 112 | `databases` | `dynamodb` | 30 | — | — | ⬜ TODO |
| 113 | `databases` | `elasticsearch` | 44 | — | — | ⬜ TODO |
| 114 | `databases` | `flyway-liquibase` | 42 | — | — | ⬜ TODO |
| 115 | `databases` | `hibernate-caching` | 15 | — | — | ⬜ TODO |
| 116 | `databases` | `hibernate` | 48 | — | — | ⬜ TODO |
| 117 | `databases` | `hibernate-jpql-criteria` | 15 | — | — | ⬜ TODO |
| 118 | `databases` | `hibernate-relationships` | 15 | — | — | ⬜ TODO |
| 119 | `databases` | `mongodb` | 46 | — | — | ⬜ TODO |
| 120 | `databases` | `neo4j` | 30 | — | — | ⬜ TODO |
| 121 | `databases` | `postgresql` | 55 | — | — | ⬜ TODO |
| 122 | `databases` | `redis` | 43 | — | — | ⬜ TODO |
| 123 | `databases` | `scylladb` | 23 | — | — | ⬜ TODO |
| 124 | `databases` | `sql` | 53 | — | — | ⬜ TODO |
| 125 | `design-patterns` | `design-patterns` | 48 | — | — | ⬜ TODO |
| 126 | `devops` | `ansible` | 25 | — | — | ⬜ TODO |
| 127 | `devops` | `argocd` | 42 | — | — | ⬜ TODO |
| 128 | `devops` | `consul` | 24 | — | — | ⬜ TODO |
| 129 | `devops` | `docker` | 41 | — | — | ⬜ TODO |
| 130 | `devops` | `git` | 43 | — | — | ⬜ TODO |
| 131 | `devops` | `gradle-maven` | 38 | — | — | ⬜ TODO |
| 132 | `devops` | `helm` | 43 | — | — | ⬜ TODO |
| 133 | `devops` | `istio-service-mesh` | 26 | — | — | ⬜ TODO |
| 134 | `devops` | `kubernetes` | 45 | — | — | ⬜ TODO |
| 135 | `devops` | `linkerd` | 20 | — | — | ⬜ TODO |
| 136 | `devops` | `linux` | 33 | — | — | ⬜ TODO |
| 137 | `devops` | `terraform` | 42 | — | — | ⬜ TODO |
| 138 | `devops` | `vault` | 26 | — | — | ⬜ TODO |
| 139 | `frameworks/jvm-alternatives` | `ktor` | 1 | — | — | ⬜ TODO |
| 140 | `frameworks/jvm-alternatives` | `micronaut` | 25 | — | — | ⬜ TODO |
| 141 | `frameworks/jvm-alternatives` | `quarkus` | 1 | — | — | ⬜ TODO |
| 142 | `frameworks/jvm-alternatives` | `vertx` | 1 | — | — | ⬜ TODO |
| 143 | `frameworks/spring` | `resilience4j` | 20 | — | — | ⬜ TODO |
| 144 | `frameworks/spring` | `spring-ai` | 15 | — | — | ⬜ TODO |
| 145 | `frameworks/spring` | `spring-aop` | 22 | — | — | ⬜ TODO |
| 146 | `frameworks/spring` | `spring-async` | 15 | — | — | ⬜ TODO |
| 147 | `frameworks/spring` | `spring-batch` | 43 | — | — | ⬜ TODO |
| 148 | `frameworks/spring` | `spring-boot-3-migration` | 15 | — | — | ⬜ TODO |
| 149 | `frameworks/spring` | `spring-boot-actuator` | 43 | — | — | ⬜ TODO |
| 150 | `frameworks/spring` | `spring-boot` | 42 | — | — | ⬜ TODO |
| 151 | `frameworks/spring` | `spring-cache` | 17 | — | — | ⬜ TODO |
| 152 | `frameworks/spring` | `spring-cloud` | 43 | — | — | ⬜ TODO |
| 153 | `frameworks/spring` | `spring-data-jdbc` | 16 | — | — | ⬜ TODO |
| 154 | `frameworks/spring` | `spring-data-jpa` | 42 | — | — | ⬜ TODO |
| 155 | `frameworks/spring` | `spring-events` | 16 | — | — | ⬜ TODO |
| 156 | `frameworks/spring` | `spring-framework` | 40 | — | — | ⬜ TODO |
| 157 | `frameworks/spring` | `spring-graphql` | 15 | — | — | ⬜ TODO |
| 158 | `frameworks/spring` | `spring-integration` | 15 | — | — | ⬜ TODO |
| 159 | `frameworks/spring` | `spring-kafka` | 15 | — | — | ⬜ TODO |
| 160 | `frameworks/spring` | `spring-messaging` | 15 | — | — | ⬜ TODO |
| 161 | `frameworks/spring` | `spring-modulith` | 2 | 15 | 146 | ⬜ TODO |
| 162 | `frameworks/spring` | `spring-mvc` | 43 | — | — | ⬜ TODO |
| 163 | `frameworks/spring` | `spring-r2dbc` | 15 | — | — | ⬜ TODO |
| 164 | `frameworks/spring` | `spring-rest-client` | 13 | — | — | ⬜ TODO |
| 165 | `frameworks/spring` | `spring-retry` | 17 | — | — | ⬜ TODO |
| 166 | `frameworks/spring` | `spring-scheduling` | 16 | — | — | ⬜ TODO |
| 167 | `frameworks/spring` | `spring-security` | 43 | — | — | ⬜ TODO |
| 168 | `frameworks/spring` | `spring-session` | 15 | — | — | ⬜ TODO |
| 169 | `frameworks/spring` | `spring-state-machine` | 15 | — | — | ⬜ TODO |
| 170 | `frameworks/spring` | `spring-testing` | 1 | — | — | ⬜ TODO |
| 171 | `frameworks/spring` | `spring-transaction` | 15 | — | — | ⬜ TODO |
| 172 | `frameworks/spring` | `spring-validation` | 16 | — | — | ⬜ TODO |
| 173 | `frameworks/spring` | `spring-vault` | 15 | — | — | ⬜ TODO |
| 174 | `frameworks/spring` | `spring-webflux` | 43 | — | — | ⬜ TODO |
| 175 | `jvm` | `graalvm-native` | 15 | — | — | ⬜ TODO |
| 176 | `jvm` | `jvm` | 40 | — | — | ⬜ TODO |
| 177 | `leadership` | `code-review-practices` | 1 | — | — | ⬜ TODO |
| 178 | `leadership` | `conflict-resolution` | 20 | 20 | 181 | ⬜ TODO |
| 179 | `leadership` | `estimations-planning` | 20 | 20 | 162 | ⬜ TODO |
| 180 | `leadership` | `mentoring` | 1 | 25 | 207 | ⬜ TODO |
| 181 | `leadership` | `team-leadership` | 1 | — | — | ⬜ TODO |
| 182 | `leadership` | `teching` | 20 | 20 | 172 | ⬜ TODO |
| 183 | `leadership` | `technical-decisions` | 1 | 22 | 195 | ⬜ TODO |
| 184 | `logging` | `logging` | 9 | — | — | ⬜ TODO |
| 185 | `messaging` | `aws-sqs-sns` | 1 | — | — | ⬜ TODO |
| 186 | `messaging` | `kafka` | 50 | — | — | ⬜ TODO |
| 187 | `messaging` | `message-brokers-comparison` | 1 | — | — | ⬜ TODO |
| 188 | `messaging` | `nats` | 8 | — | — | ⬜ TODO |
| 189 | `messaging` | `pulsar` | 1 | — | — | ⬜ TODO |
| 190 | `messaging` | `rabbitmq` | 41 | — | — | ⬜ TODO |
| 191 | `messaging` | `redpanda` | 20 | 20 | 141 | ⬜ TODO |
| 192 | `monitoring` | `elk-stack` | 5 | — | — | ⬜ TODO |
| 193 | `monitoring` | `jaeger-zipkin` | 1 | — | — | ⬜ TODO |
| 194 | `monitoring` | `logging-strategies` | 1 | — | — | ⬜ TODO |
| 195 | `monitoring` | `loki-grafana` | 1 | — | — | ⬜ TODO |
| 196 | `monitoring` | `metrics-tracing` | 6 | — | — | ⬜ TODO |
| 197 | `monitoring` | `micrometer` | 1 | — | — | ⬜ TODO |
| 198 | `monitoring` | `observability` | 15 | — | — | ⬜ TODO |
| 199 | `monitoring` | `opentelemetry` | 9 | — | — | ⬜ TODO |
| 200 | `monitoring` | `prometheus-grafana` | 39 | — | — | ⬜ TODO |
| 201 | `performance` | `application-profiling` | 42 | — | — | ⬜ TODO |
| 202 | `performance` | `caching-performance` | 1 | — | — | ⬜ TODO |
| 203 | `performance` | `database-performance` | 1 | — | — | ⬜ TODO |
| 204 | `performance` | `jvm-performance-tuning` | 1 | — | — | ⬜ TODO |
| 205 | `performance` | `memory-management` | 1 | — | — | ⬜ TODO |
| 206 | `performance` | `network-performance` | 1 | — | — | ⬜ TODO |
| 207 | `performance` | `performance-testing` | 1 | — | — | ⬜ TODO |
| 208 | `programming-languages/go` | `go-concurrency` | 35 | — | — | ⬜ TODO |
| 209 | `programming-languages/go` | `go-generics` | 1 | — | — | ⬜ TODO |
| 210 | `programming-languages/go` | `go` | 1 | — | — | ⬜ TODO |
| 211 | `programming-languages/go` | `go-memory-gc` | 1 | — | — | ⬜ TODO |
| 212 | `programming-languages/go` | `go-modules` | 27 | — | — | ⬜ TODO |
| 213 | `programming-languages/go` | `go-stdlib` | 30 | — | — | ⬜ TODO |
| 214 | `programming-languages/go` | `go-testing` | 1 | — | — | ⬜ TODO |
| 215 | `programming-languages/java` | `java-17-21` | 42 | — | — | ⬜ TODO |
| 216 | `programming-languages/java` | `java-8` | 41 | 31 | 304 | ⬜ TODO |
| 217 | `programming-languages/java` | `java-annotations` | 43 | — | — | ⬜ TODO |
| 218 | `programming-languages/java` | `java-collections` | 46 | 34 | 307 | ⬜ TODO |
| 219 | `programming-languages/java` | `java-completable-future` | 13 | 13 | 135 | ⬜ TODO |
| 220 | `programming-languages/java` | `java-concurrency` | 56 | 56 | 246 | ⬜ TODO |
| 221 | `programming-languages/java` | `java-conditional-statements` | 1 | — | — | ⬜ TODO |
| 222 | `programming-languages/java` | `java-core` | 39 | 28 | 288 | ⬜ TODO |
| 223 | `programming-languages/java` | `java-exceptions` | 42 | 7 | 78 | ⬜ TODO |
| 224 | `programming-languages/java` | `java-functional-interface` | 14 | — | — | ⬜ TODO |
| 225 | `programming-languages/java` | `java-generics` | 40 | 7 | 99 | ⬜ TODO |
| 226 | `programming-languages/java` | `java-initialization` | 1 | — | — | ⬜ TODO |
| 227 | `programming-languages/java` | `java-io-nio` | 40 | — | — | ⬜ TODO |
| 228 | `programming-languages/java` | `java-jackson` | 1 | — | — | ⬜ TODO |
| 229 | `programming-languages/java` | `java-lombok` | 5 | — | — | ⬜ TODO |
| 230 | `programming-languages/java` | `java-mapstruct` | 1 | — | — | ⬜ TODO |
| 231 | `programming-languages/java` | `java-modules` | 38 | — | — | ⬜ TODO |
| 232 | `programming-languages/java` | `java-oop` | 43 | — | — | ⬜ TODO |
| 233 | `programming-languages/java` | `java-optional` | 15 | 15 | 121 | ⬜ TODO |
| 234 | `programming-languages/java` | `java-pattern-matching` | 15 | — | — | ⬜ TODO |
| 235 | `programming-languages/java` | `java-records` | 15 | — | — | ⬜ TODO |
| 236 | `programming-languages/java` | `java-reflection` | 1 | — | — | ⬜ TODO |
| 237 | `programming-languages/java` | `java-serialization` | 40 | — | — | ⬜ TODO |
| 238 | `programming-languages/java` | `java-stream` | 42 | 6 | 82 | ⬜ TODO |
| 239 | `programming-languages/java` | `java-string` | 39 | — | — | ⬜ TODO |
| 240 | `programming-languages/java` | `java-types` | 38 | — | — | ⬜ TODO |
| 241 | `programming-languages/java` | `java-virtual-threads` | 15 | — | — | ⬜ TODO |
| 242 | `programming-languages/kotlin` | `kotlin-collections` | 42 | — | — | ⬜ TODO |
| 243 | `programming-languages/kotlin` | `kotlin-coroutines` | 19 | — | — | ⬜ TODO |
| 244 | `programming-languages/kotlin` | `kotlin-dsl` | 1 | — | — | ⬜ TODO |
| 245 | `programming-languages/kotlin` | `kotlin-exceptions` | 7 | — | — | ⬜ TODO |
| 246 | `programming-languages/kotlin` | `kotlin-flow` | 17 | — | — | ⬜ TODO |
| 247 | `programming-languages/kotlin` | `kotlin-interop-java` | 38 | — | — | ⬜ TODO |
| 248 | `programming-languages/kotlin` | `kotlin` | 27 | — | — | ⬜ TODO |
| 249 | `programming-languages/kotlin` | `kotlin-sealed-classes` | 15 | — | — | ⬜ TODO |
| 250 | `programming-languages/kotlin` | `kotlin-serialization` | 1 | — | — | ⬜ TODO |
| 251 | `programming-languages/kotlin` | `kotlin-spring` | 15 | — | — | ⬜ TODO |
| 252 | `programming-languages/kotlin` | `kotlin-value-classes` | 15 | — | — | ⬜ TODO |
| 253 | `programming-languages/scala` | `scala` | 1 | — | — | ⬜ TODO |
| 254 | `reactive` | `project-reactor` | 47 | — | — | ⬜ TODO |
| 255 | `reactive` | `reactive-patterns` | 26 | — | — | ⬜ TODO |
| 256 | `reactive` | `reactive-streams` | 30 | — | — | ⬜ TODO |
| 257 | `reactive` | `reactive-testing` | 28 | — | — | ⬜ TODO |
| 258 | `reactive` | `rxjava` | 46 | — | — | ⬜ TODO |
| 259 | `reactive` | `webflux` | 28 | — | — | ⬜ TODO |
| 260 | `security` | `application-security` | 45 | — | — | ⬜ TODO |
| 261 | `security` | `authentication-authorization-patterns` | 45 | — | — | ⬜ TODO |
| 262 | `security` | `jwt` | 43 | — | — | ⬜ TODO |
| 263 | `security` | `mtls` | 20 | — | — | ⬜ TODO |
| 264 | `security` | `oauth2` | 42 | — | — | ⬜ TODO |
| 265 | `security` | `owasp-top10` | 45 | — | — | ⬜ TODO |
| 266 | `security` | `secrets-management` | 22 | — | — | ⬜ TODO |
| 267 | `security` | `supply-chain-security` | 24 | — | — | ⬜ TODO |
| 268 | `security` | `tls-ssl` | 45 | — | — | ⬜ TODO |
| 269 | `security` | `zero-trust` | 19 | — | — | ⬜ TODO |
| 270 | `system-design` | `design-chat-system` | 21 | — | — | ⬜ TODO |
| 271 | `system-design` | `design-dropbox` | 30 | 30 | 160 | ⬜ TODO |
| 272 | `system-design` | `design-elevator-oo` | 26 | 26 | 106 | ⬜ TODO |
| 273 | `system-design` | `design-feed-system` | 30 | 30 | 193 | ⬜ TODO |
| 274 | `system-design` | `design-google-maps` | 30 | 30 | 171 | ⬜ TODO |
| 275 | `system-design` | `design-instagram` | 27 | 27 | 95 | ⬜ TODO |
| 276 | `system-design` | `design-key-value-store` | 30 | 30 | 111 | ⬜ TODO |
| 277 | `system-design` | `design-netflix` | 30 | 30 | 179 | ⬜ TODO |
| 278 | `system-design` | `design-parking-lot-oo` | 28 | 28 | 114 | ⬜ TODO |
| 279 | `system-design` | `design-pastebin` | 30 | 30 | 111 | ⬜ TODO |
| 280 | `system-design` | `design-payment-system` | 30 | 30 | 201 | ⬜ TODO |
| 281 | `system-design` | `design-rate-limiter` | 30 | 30 | 167 | ⬜ TODO |
| 282 | `system-design` | `design-search` | 30 | 30 | 153 | ⬜ TODO |
| 283 | `system-design` | `design-twitter` | 27 | 27 | 146 | ⬜ TODO |
| 284 | `system-design` | `design-typeahead` | 30 | 30 | 129 | ⬜ TODO |
| 285 | `system-design` | `design-uber` | 30 | 30 | 150 | ⬜ TODO |
| 286 | `system-design` | `design-url-shortener` | 30 | 30 | 174 | ⬜ TODO |
| 287 | `system-design` | `design-vending-machine-oo` | 24 | 24 | 99 | ⬜ TODO |
| 288 | `system-design` | `design-web-crawler` | 26 | 26 | 98 | ⬜ TODO |
| 289 | `system-design` | `design-youtube` | 28 | 28 | 101 | ⬜ TODO |
| 290 | `system-design` | `system-design` | 1 | — | — | ⬜ TODO |
| 291 | `testing` | `chaos-engineering` | 44 | — | — | ⬜ TODO |
| 292 | `testing` | `contract-testing` | 20 | — | — | ⬜ TODO |
| 293 | `testing` | `integration-testing` | 39 | — | — | ⬜ TODO |
| 294 | `testing` | `junit` | 15 | — | — | ⬜ TODO |
| 295 | `testing` | `load-testing` | 22 | — | — | ⬜ TODO |
| 296 | `testing` | `mockito` | 45 | — | — | ⬜ TODO |
| 297 | `testing` | `mutation-testing` | 20 | — | — | ⬜ TODO |
| 298 | `testing` | `property-based-testing` | 1 | — | — | ⬜ TODO |
| 299 | `testing` | `rest-assured` | 15 | — | — | ⬜ TODO |
| 300 | `testing` | `selenium` | 15 | — | — | ⬜ TODO |
| 301 | `testing` | `test-automation` | 7 | — | — | ⬜ TODO |
| 302 | `testing` | `test-strategies` | 45 | — | — | ⬜ TODO |
| 303 | `testing` | `testcontainers` | 40 | — | — | ⬜ TODO |
| 304 | `testing` | `unit-testing` | 45 | — | — | ⬜ TODO |

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
