# ROUND 9 EXECUTION NOTE

The true reset and inventory were completed on 2026-07-17. Do not carry forward any ROUND 8 acceptance. Do not repeat the reset if `docs/mcq-quality/reset-manifest.json` has `reset_complete: true`; begin with the next clean content-review unit.

/loop 15m

ROUND-9 FULL MCQ CORPUS RESET AND QUALITY SWEEP

Каждый тик выполняй ровно одну атомарную единицу полного повторного аудита
корпуса вопросов для собеседований и связанных MCQ-ответов.

Главная цель: заново перепроверить ВСЕ современные JSON MCQ-файлы и связанную
теорию, улучшить правильные и неправильные ответы, объяснения, примеры,
edge cases, источники заблуждений, related-ссылки и version-sensitive факты.

После завершения каждый MCQ-блок обязан:

1. Иметь ровно один фактически правильный и однозначно защитимый ответ.
2. Иметь три правдоподобных дистрактора с разными mental models.
3. Не позволять угадать correct по длине, форме, структуре, тону,
   пунктуации, `backticks`, позиции или уровню редакторской аккуратности.
4. Быть написан естественным русским языком.
5. Иметь содержательные и согласованные `sections`.
6. Соответствовать параллельной теории в interview Markdown.
7. Проходить schema, factual, answer-parity, structure, stamp,
   caricature и blind-review gates.
8. Не возвращать inline MCQ в Markdown.
9. Не создавать новый legacy-контент.
10. Иметь независимую покритериальную запись проверки для каждого блока.
11. Пройти **четырёхстороннюю LLM-проверку** всех A/B/C/D (логика, факты, форма) —
    скрипты сами по себе приёмку **не закрывают** (см. §2.1, §31).

---

# 0. Жёсткий RESET перед началом

Первый тик нового ROUND выполняет только reset и inventory.

1. Прочитай текущий `PLAN_INTERVIEW.md`.
2. Удали из рабочего плана:
   - старую generated-таблицу;
   - старые `DONE/WIP/Golden`;
   - старые проценты готовности;
   - старый progress log как источник приёмки.
3. Git-историю и существующие commits НЕ удаляй.
4. Сохрани старые findings только как исторические notes.
5. Замени рабочий план новым ROUND-9 планом:
   - `PLAN_INTERVIEW_ROUND9_RESET.md`, если он уже лежит в проекте;
   - либо создай эквивалент по правилам этого prompt.
6. Все файлы корпуса выставь в `⬜` по ВСЕМ критериям.
7. Ни один старый `✅` не переносить.
8. Старые review-sidecars:
   - архивировать;
   - либо сбросить в `TODO`;
   - не считать доказательством нового раунда.
9. Создай каталог:

```text
docs/mcq-quality/reviews/
```

10. Создай отдельный атомарный reset commit.
11. Только со следующего тика начинай исправление контента.

Reset считается завершённым, только если:

```text
Old PASS carried forward = 0
FINAL ✅ = 0
All corpus files = TODO/RECHECK
```

---

# 1. Авторитетные skills и документы

В начале КАЖДОГО тика прочитай:

```text
~/.claude/skills/mcq-quality-fixer/SKILL.md
~/.claude/skills/interview-writer/SKILL.md
~/.claude/skills/interview-options-writer/SKILL.md
```

Если существуют, прочитай полностью:

```text
~/.claude/skills/mcq-quality-fixer/references/full-guide.md
~/.claude/skills/interview-writer/references/full-guide.md
~/.claude/skills/interview-options-writer/references/full-guide.md
```

Также обязательно прочитай:

```text
docs/golden-examples.md
PLAN_INTERVIEW.md
```

Для выбранной темы прочитай полностью:

```text
modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json
cheatsheets/interview/<category>/<topic>-interview.md
docs/mcq-quality/reviews/<topic_slug>.json
docs/mcq-quality/fact-freshness/<topic_slug>.json
```

Если sidecar отсутствует, создай его до правки контента.

Приоритет правил:

1. Фактическая корректность.
2. Ровно один защитимый ответ.
3. Соответствие stem и theory.
4. Правдоподобность дистракторов.
5. Естественный русский язык.
6. Visual Format / Structural Parity.
7. Числовые метрики.
8. Баланс позиции correct.

Никогда не ухудшай факты или читаемость ради одной метрики.

---

# 2. Разделение ответственности

## `mcq-quality-fixer`

Основной skill текущего loop.

Владеет:

```text
modules/quiz-app/src/main/resources/seed/mcq/**/*.json
```

Используется для:

- `question_text`;
- correct;
- distractors;
- всех `sections`;
- mental-model-first;
- Single-Delta;
- Option Parity;
- Plausibility Parity;
- Russian Readability Parity;
- Visual Format Parity;
- factual validation;
- schema;
- answer parity;
- freshness;
- blind review.

## `interview-writer`

Владеет theory:

```text
cheatsheets/interview/**/*.md
```

Используется, когда:

- theory противоречит JSON;
- correct оказался фактически неверным;
- изменился смысл вопроса;
- отсутствует соответствующий `Q<N>`;
- stem неоднозначен;
- изменился version/provider/config scope;
- пример или механизм в theory устарел;
- нужно обновить links/TOC/See also.

Markdown содержит только theory.

Никогда не добавляй:

```markdown
> [!mcq]
```

## `interview-options-writer`

Legacy-only.

Применяется только к:

- SQLite `answer_options`;
- `interview.db`;
- `insert-options.py`;
- `seed-options.py`;
- `option.txt`;
- `:quiz-app:seedOptions`.

Новый legacy-контент не создавать.

JSON MCQ является современной основной поверхностью.

Legacy обрабатывать только после завершения JSON-корпуса либо как отдельный
audit/manual-review.

## 2.1. Скрипты vs LLM — кто что решает

```text
Скрипты  = детерминированный pre-check (schema, пороги, smoke-тесты)
LLM      = обязательная приёмка логики, фактов, SKEL и визуальной невыделяемости
```

**Скрипты умеют:** длину слов, число `` ` ``-span, абсолютные маркеры, позиции,
дубликаты n-gram, schema, caricature-heuristics.

**Скрипты НЕ умеют:**

- понять, защитим ли distractor под конкретным stem;
- увидеть «частично правду» и multiple-defensible answer;
- оценить, выглядит ли correct «учебником» на фоне трёх заглушек;
- проверить **единый surface skeleton** всех 4 `option.text` (колонка `SKEL`);
- проверить согласованность `text` ↔ `sections` по смыслу;
- заметить зеркальные пары, contra-hints, meta-подсказки между A/B/C/D;
- отличить правдоподобный wrong от карикатуры в контексте темы.

**Запрещено:**

- закрывать блок/chunk только потому, что `mcq-answer-parity-gate.py` зелёный;
- считать `mcq-form-balance.py` или `mcq-skel-gate.py` заменой blind/style
  review **или** LLM-`SKEL`;
- ставить `C-FRM=✅`, `SKEL=✅`, `BST=✅`, `BSM=✅`, `PAR=✅`, `FINAL=✅`
  без §31 на **каждый** блок (включая ручную LLM-проверку скелета);
- ставить `PAR=✅` / `FINAL=✅` / `skel: true` только потому, что
  `mcq-skel-gate.py` зелёный;
- ставить `PAR=✅` / `FINAL=✅` при `SKEL≠✅`;
- массово проставлять `true` в review-sidecar без фактического чтения 4 options.

**Обязательный порядок на блок:**

```text
1. Прочитать stem + все 4 option.text + sections (LLM)
2. §31 Four-option review (LLM, покритериально)
3. Правки text/sections при необходимости
4. Скрипты (schema + parity gates)
5. §31 повтор на изменённых блоках
6. Независимый reviewer-subagent (BST + BSM), если правил другой агент
7. Запись в review-sidecar
```

---

# 3. Основные поверхности

Современный MCQ:

```text
modules/quiz-app/src/main/resources/seed/mcq/**/*.json
```

Theory:

```text
cheatsheets/interview/**/*-interview.md
```

Schema:

```text
modules/quiz-app/src/main/resources/seed/mcq-schema.json
```

Review-sidecars:

```text
docs/mcq-quality/reviews/<topic_slug>.json
```

Fact freshness:

```text
docs/mcq-quality/fact-freshness/<topic_slug>.json
```

Legacy:

```text
modules/quiz-app/data/db/interview.db
modules/quiz-app/src/main/resources/prompts/option.txt
```

---

# 4. Мастер-план и критерии по каждому файлу

`PLAN_INTERVIEW.md` должен содержать отдельные колонки.

## Целостность и связь

- `SCH` — schema и структурные инварианты.
- `TH` — theory существует и прочитана.
- `QS` — `q_number` синхронизирован.
- `STEM` — качество вопроса.
- `FR` — freshness version-sensitive фактов.
- `LINK` — `related` и theory-links.
- `POS` — позиции correct не создают подсказку.

## Правильные ответы

- `C-FCT` — correct фактически верен.
- `C-CMP` — correct достаточно полный для stem.
- `C-SCP` — scope/version/provider/config корректны.
- `C-UNI` — correct единственный защитимый.
- `C-FRM` — correct не выделяется длиной/тоном/code-density (**структурный скелет — `SKEL`**).
- `C-SEC` — correct sections точны и согласованы.
- `C-RU` — correct написан естественным русским.

## Неправильные ответы

- `W-PLS` — каждый distractor правдоподобен.
- `W-1ER` — одна основная ошибка.
- `W-API` — реальные API/понятия.
- `W-FLS` — distractor ложен под stem.
- `W-DIV` — разные misconceptions.
- `W-CAR` — нет карикатуры, абсурда и negation echo.
- `W-FRM` — format/density parity (**единый скелет — `SKEL`**).
- `SKEL` — HARD GATE (LLM): все 4 `option.text` делят один surface skeleton
  (зачин, число предложений/клауз, плотность `;`/`:`/`—`/скобок, класс
  backticks/перечислений). Correct нельзя отличить от wrong по структуре одной
  глазами. Pre-check: `scripts/mcq-skel-gate.py` (грубые mismatch). Зелёный
  скрипт **не** даёт `SKEL=✅`; приёмка только LLM. Без `SKEL=✅` запрещены `PAR=✅`
  и `FINAL=✅`.
- `W-SEC` — wrong sections согласованы.
- `W-RU` — естественный русский.

## Итоговые gates

- `PAR` — общий parity (**требует `SKEL=✅`**).
- `BST` — blind style.
- `BSM` — blind semantic.
- `STAMP` — нет stamped clone.
- `VAL` — все validators.
- `FINAL` — файл полностью принят (**требует `SKEL=✅` на весь файл**).

Не заменяй эти критерии одной колонкой `Golden`. Не сливай `SKEL` в `C-FRM`/`W-FRM`.

---

# 5. Review-sidecar на каждый файл

До правки создай или обнови:

```text
docs/mcq-quality/reviews/<topic_slug>.json
```

Sidecar обязан содержать запись на каждый:

```text
q_number
block_idx
```

Минимальная структура:

```json
{
  "topic_slug": "example-interview",
  "round": 8,
  "reviewed_commit": null,
  "status": "TODO",
  "summary": {
    "questions": 0,
    "blocks": 0,
    "blocks_done": 0,
    "critical": 0,
    "high": 0,
    "medium": 0,
    "low": 0
  },
  "blocks": [
    {
      "q_number": 1,
      "block_idx": 0,
      "status": "TODO",
      "stem": {
        "clear": null,
        "single_concept": null,
        "no_answer_hint": null,
        "version_scope_explicit": null
      },
      "correct": {
        "factually_true": null,
        "complete_for_stem": null,
        "scope_correct": null,
        "unique_defensible": null,
        "not_visually_distinct": null,
        "sections_consistent": null
      },
      "distractors": [
        {
          "label": "A",
          "plausible": null,
          "one_primary_error": null,
          "real_api_or_concept": null,
          "false_under_stem": null,
          "distinct_misconception": null,
          "not_caricature": null,
          "sections_consistent": null
        }
      ],
      "parity": {
        "option_parity": null,
        "visual_format_parity": null,
        "russian_readability": null,
        "no_stamped_clone": null,
        "blind_style_pass": null,
        "blind_semantic_pass": null
      },
      "notes": []
    }
  ]
}
```

`null` означает «не проверено».

Нельзя:

- массово заменить все `null` на `true`;
- считать schema PASS доказательством semantic quality;
- ставить `DONE`, если хотя бы один блок не заполнен.

---

# 6. Статусы

```text
⬜ TODO
🔄 IN_PROGRESS
◐ PARTIAL
🟠 MANUAL_REVIEW
⏸ BLOCKED
❌ FAIL
✅ PASS
```

`FINAL=✅` только когда:

- все обязательные колонки файла `✅`;
- sidecar не содержит `null`;
- нет `FAIL`;
- нет unresolved `MANUAL_REVIEW`;
- все блоки прошли полный review;
- commit hash записан.

---

# 7. Inventory и baseline

Если plan отсутствует или устарел:

1. Найди весь JSON-корпус:

```bash
find modules/quiz-app/src/main/resources/seed/mcq \
  -type f -name '*.json' | sort
```

2. Найди theory:

```bash
find cheatsheets/interview \
  -type f -name '*-interview.md' | sort
```

3. Найди legacy:

```bash
find . -type f \( \
  -name 'interview.db' -o \
  -name 'option.txt' -o \
  -name 'insert-options.py' -o \
  -name 'seed-options.py' \
\) | sort
```

4. Найди validators:

```bash
find scripts .claude/skills -type f | sort | \
  rg 'mcq|interview|parity|structure|stamp|caricature|schema|freshness|blind|markdown'
```

5. Для каждого JSON зафиксируй:

- число questions;
- число blocks;
- theory path;
- freshness sidecar;
- schema baseline;
- parity baseline;
- structure baseline;
- legacy analog;
- dirty status;
- old tell только как diagnostic signal.

Schema PASS не означает quality PASS.

---

# 8. Выбор следующей единицы

Один тик обрабатывает:

- один файл до 20 блоков;
- один chunk 10–20 блоков;
- один manual-review block;
- один factual/theory sync issue.

Порядок worst-first:

1. Schema failure.
2. Фактически неверный correct.
3. Несколько защитимых ответов.
4. Theory/JSON mismatch.
5. Частично правильные wrong.
6. Очевидные/caricature distractors.
7. Sections противоречат text.
8. Correct выделяется формой.
9. Missing freshness.
10. Readability, links, position distribution.

Не выбирай файл только по старому `Tell`.

Для giant-файлов:

- chunk по логической группе;
- либо диапазон `q_number`;
- JSON должен оставаться полностью валидным;
- file status остаётся `◐` до полного прохода.

---

# 9. Collision guard

Перед чтением и изменением:

```bash
git status --short
git status --porcelain -- \
  <json-path> \
  <md-path> \
  <review-sidecar-path> \
  <freshness-sidecar-path> \
  PLAN_INTERVIEW.md
```

Если target dirty:

- не трогать;
- поставить `⏸ BLOCKED`;
- выбрать следующий clean target.

Запрещено:

```text
git reset --hard
git checkout -- .
git restore .
git add -A
```

Не откатывай чужие изменения.

---

# 10. Baseline перед правкой

Для выбранной единицы:

1. Прочитай JSON целиком.
2. Прочитай theory целиком.
3. Прочитай sidecars.
4. Найди inbound/outbound references.
5. Запусти доступные validators (**pre-check**, не приёмка).
6. Сохрани baseline в review-sidecar и plan notes.
7. **Не начинай правки**, пока не выполнишь §31 preview-read на выбранный chunk.

Минимум (скрипты — после LLM-read, см. §2.1; зелёный gate ≠ DONE):

```bash
bash scripts/verify-mcq-json.sh <json>
python3 scripts/mcq-answer-parity-gate.py <json>
python3 scripts/mcq-form-balance.py <json>          # если form-tell gates красные (§18.1)
python3 scripts/mcq-answer-parity-gate.py <json>   # повтор после баланса
python3 scripts/mcq-skel-gate.py <json>            # SKEL pre-check; PASS ≠ SKEL=✅ (нужен LLM)
python3 scripts/mcq-structure-parity.py --file <json>
```

Когда существуют:

```bash
python3 scripts/mcq-stamp-audit.py --file <json>
python3 scripts/audit-mcq-parity.py --caricature <json>
```

Перед запуском незнакомого script прочитай `--help` или код.

Не выдумывай CLI.

---

# 11. Проверка theory sync

Для каждого блока:

- существует ли `## Q<N>.` в Markdown;
- совпадает ли tested concept;
- не проверяет ли JSON другой факт;
- не устарела ли theory;
- не противоречат ли examples и sections;
- не ссылается ли JSON на удалённый вопрос;
- не изменился ли version/provider scope.

Если `q_number` отсутствует:

- не придумывай theory без анализа;
- исправь через `interview-writer`;
- либо поставь manual review.

---

# 12. Проверка question_text

Каждый stem обязан:

- задавать один ясный вопрос;
- проверять один concept;
- иметь ровно один защитимый ответ;
- не содержать подсказку;
- не объединять несколько независимых задач;
- указывать version/provider/config, если это влияет;
- быть естественным русским;
- соответствовать theory.

Не лечи неоднозначный stem только вариантами.

---

# 13. Проверка correct

Не доверяй существующему:

```json
"correct": true
```

Проверку correct выполняй **в контексте §31** — сразу сравнивая все 4 options.
Изолированная проверка correct без cross-option review **не засчитывается**.

Проверь:

1. Вариант фактически верен.
2. Он верен именно под stem.
3. Scope/version/provider/config корректны.
4. Не перепутаны compile-time и runtime.
5. Не перепутаны contract и implementation.
6. Нет скрытого исключения.
7. Ответ достаточно полный.
8. Text не превращён в лекцию.
9. Он соответствует theory.
10. Он единственный защитимый.

Если correct неверен:

- исправь text;
- при необходимости переставь `correct`;
- обнови sections;
- синхронизируй theory;
- обнови freshness;
- запиши factual delta.

Нельзя менять correct ради position balance.

---

# 14. Mental-model-first

До переписывания A/B/C/D сформулируй:

```text
Correct mental model
Wrong mental model 1
Wrong mental model 2
Wrong mental model 3
```

Подходящие misconception types:

- adjacent API confusion;
- scope confusion;
- overgeneralization;
- wrong condition;
- lifecycle inversion;
- cardinality confusion;
- outdated model;
- causal inversion;
- wrong trade-off;
- partial truth with false conclusion;
- plausible implementation bug;
- provider/version confusion.

Три wrong должны проверять разные mental models.

---

# 15. Качество каждого distractor

Каждый wrong обязан:

- быть правдоподобным;
- содержать одну основную ошибку;
- использовать реальные API и термины;
- быть ложным именно под stem;
- не быть частично правильным;
- отвечать на тот же вопрос;
- не быть прямым отрицанием correct;
- не быть карикатурой;
- не содержать несколько независимых ошибок;
- иметь согласованные sections;
- звучать как ответ middle/senior.

Формула:

```text
реальное понятие
+ зёрно правды
+ одна неверная предпосылка
+ конкретный ошибочный вывод
```

Запрещены inflated caricatures:

```text
любой ценой
не спрашивая
заставить
угрожать
публично и жёстко
просто игнорировать
просто перезапустить всё
```

`всегда` и `никогда` не запрещены автоматически, но не должны быть
style-tell только у wrong options.

---

# 16. Single-Delta

Используй Single-Delta, когда он естественен:

- сигнатура;
- параметр;
- кардинальность;
- порядок;
- условие;
- lifecycle trigger;
- API;
- результат кода.

Single-Delta означает одну смысловую ошибку.

Он не требует буквальной замены одного слова.

Не превращай options в механические carbon copies.

---

# 17. Option Parity

Correct нельзя угадывать по:

- длине;
- числу предложений;
- количеству фактов;
- technical vocabulary;
- examples;
- confidence;
- conditions;
- causal explanation;
- code density;
- overall polish.

Исправление:

1. Перенести пост-ответные детали из `text` в `sections`.
2. Усилить wrong правдоподобными параллельными деталями.
3. Сохранить одну основную ошибку.
4. Повторно проверить unique answer.

Не сокращай correct до пустой заглушки.

---

# 18. Visual Format Parity

Если убрать `correct`, правильный ответ нельзя угадать глазами.

## HARD: единый surface skeleton (`SKEL`)

**Жёсткое правило без исключений.** Все четыре `option.text` в блоке обязаны
делить **один и тот же surface skeleton**. Это отдельная колонка `SKEL` в
`PLAN_INTERVIEW.md` и отдельная ось §31 — не «желательно», не «в среднем по
файлу».

Перед написанием/правкой options зафиксируй skeleton блока одной строкой, затем
пиши **все четыре** текста только в нём.

Skeleton включает (совпадение по классу, не посимвольный clone):

| Ось скелета | Что должно совпадать у A/B/C/D |
|---|---|
| Зачин | одинаковый тип открытия (термин в `` ` `` / «В API…» / тезис / сравнение) |
| Число предложений | одинаково (или max−min ≤ 0 при однопредложном шаблоне) |
| Число клауз | сопоставимо; не «correct — 3 клаузы, wrong — 1» |
| Пунктуационный каркас | одинаковый класс `;` / `:` / `—` / скобок |
| Backticks / списки | один класс плотности (каталог vs проза — запрещено) |
| Ритм ответа | сравнение↔сравнение, определение↔определение, API-пара↔API-пара |

**Пример PASS (один скелет):**

```text
В API JEP 453 (Java 21): ShutdownOnFailure — …; ShutdownOnSuccess — ….
В API JEP 453 (Java 21): ShutdownOnFailure — …; ShutdownOnSuccess — ….
В API JEP 453 (Java 21): ShutdownOnFailure — …; ShutdownOnSuccess — ….
В API JEP 453 (Java 21): ShutdownOnFailure — …; ShutdownOnSuccess — ….
```

(смысл разный; каркас один)

**Пример FAIL:** correct — двуклаузовый каталог с `;` и двумя `` `API` ``;
wrong — короткие прозаические тезисы без того же каркаса.

**Не путать с stamped clone:** скелет общий, mental models и факты разные.
Пустое клонирование пунктуации без смысла — `STAMP` FAIL, не `SKEL` PASS.

Без `SKEL=✅` на **каждый** блок файла запрещены: `C-FRM=✅`, `W-FRM=✅`,
`PAR=✅`, `FINAL=✅`.

### Pre-check скрипт (не замена LLM)

```bash
python3 scripts/mcq-skel-gate.py <json>
```

`mcq-skel-gate.py` — **детерминированный pre-check** грубых mismatch
(зачин / предложения / `;` / code-bucket).

| Результат скрипта | Что делать |
|---|---|
| FAIL | чинить каркас или объяснить в notes; LLM всё равно читает блок |
| PASS | **всё равно** LLM вручную подтверждает единый skeleton (§31 `SKEL`) |

Зелёный `mcq-skel-gate.py` **не** даёт `SKEL=✅` / `llm_review.skel=true`.
Красный — сигнал, не автоматический приговор без чтения глазами.

Сравни также:

- opening shape;
- число предложений;
- grammar;
- punctuation;
- colon/semicolon/dash;
- parentheses;
- quotes;
- `backticks`;
- lists;
- numbering;
- arrows;
- code spans;
- cause/effect shape;
- tone;
- rhythm;
- information density;
- editorial quality.

Correct не должен быть единственным, который:

- выглядит как готовый конспект;
- содержит два предложения;
- использует API в `backticks`;
- содержит example;
- имеет colon;
- объясняет reason;
- звучит профессионально;
- содержит precise caveat.

Используй общий **surface + semantic** skeleton, а не буквальное клонирование.

---

# 18.1. Form-Tell Prevention (обязательно перед commit chunk)

Цель: correct нельзя угадать по форме, wrong не выделяются как «явно ложные по
тону», **и все 4 варианта на одном surface skeleton (`SKEL`)**.

## Hard gates (блокируют chunk без исключений)

После правки **каждого chunk**:

```bash
python3 scripts/mcq-answer-parity-gate.py <json>
```

Должны быть **PASS**:

| Gate | Порог | Что ловит |
|---|---|---|
| `STYLE_GUESSABILITY` | ≤ 0.40 | угадывание correct по длине или числу `` `code` `` |
| `ABSOLUTE_MARKER_GAP` | ≤ 0.25 | wrong насыщены «всегда/только/полностью», correct — нет |
| `CORRECT_LONGEST_RATE` | ≤ 0.40 | correct систематически самый длинный |
| `CORRECT_WRONG_AVG_RATIO` | 0.90–1.10 | средняя длина correct vs wrong |
| `SENTENCE_PARITY` | max-min ≤ 1 | correct — единственный двухпредложный «конспект» |
| `DETAIL_PARITY` | ≤ 0.40 | correct — единственный лидер по code/числам/скобкам |

`OPTION_LENGTH_RATIO` может быть FAIL только как **documented accepted-exception**
(конфликт с anti-stamp CV≥0.14). Это не отменяет обязанность пройти gates выше
**и** §31 LLM-review на каждый блок.

## Типовые form-tells и fix

**A. Code-span parity:** correct — каталог `` `API` ``, distractors — проза.
→ Сжать correct до 2–3 терминов; поднять `` ` `` на wrong; убрать `` `Type` (`full.name`) ``.

**B. Absolute-marker gap:** wrong с «всегда/только», correct нейтральный.
→ Смягчить wrong («часто», «преимущественно») или один обоснованный маркер в correct.

**C. Catalog tell:** correct с `;` и 4+ перечислениями.
→ Одна мысль в text, списки в `sections`.

**D. Hedge-only correct:** только correct с «обычно/может».
→ Hedge в 1–2 distractor или убрать из correct.

**E. Skeleton mismatch (`SKEL` FAIL):** correct и wrong на разных каркасах
(разный зачин / число клауз / `;` vs проза / каталог vs тезис).
→ Выбрать один surface template блока; переписать **все четыре** `option.text`
в нём; смысл/ошибки сохранить. Pre-check: `mcq-skel-gate.py`. Приёмка `SKEL` —
только LLM; `mcq-form-balance.py` / зелёный skel-gate **не** закрывают `SKEL`.

## Автобалансировка (вспомогательная)

После LLM-§31 и правок, если hard gate красный:

```bash
python3 scripts/mcq-form-balance.py <json>
python3 scripts/mcq-answer-parity-gate.py <json>
```

Скript меняет только `option.text`. После него — **обязательны** повтор §31
и reviewer-subagent (BST/BSM); скрипт не подтверждает логику.

## Чеклист блока

```text
[ ] §31 Four-option review (LLM, все 4 варианта в одном проходе)
[ ] mcq-skel-gate.py pre-check (грубые mismatch; не замена LLM)
[ ] SKEL: все 4 option.text на одном surface skeleton (LLM вручную, HARD)
[ ] correct не единственный лидер по backticks (LLM + gate)
[ ] ABSOLUTE_MARKER_GAP ≤ 0.25 (gate)
[ ] STYLE_GUESSABILITY ≤ 0.40 (gate)
[ ] BST: reviewer не угадывает correct по форме
[ ] BSM: ни один wrong не защитим под stem
```

Без **LLM**-`SKEL` блок не получает `SKEL=✅` / `C-FRM=✅` / `W-FRM=✅` /
`PAR=✅` — даже при зелёном `mcq-skel-gate.py`.
Без `SKEL=✅` на весь файл — нет `FINAL=✅`.

---

# 19. Защита от stamped clone

Низкий `structure-tell` не означает высокое качество.

Проверь:

- нет ли одинаковой искусственной фразы;
- не выровнены ли тексты механически;
- нет ли длинных повторяющихся n-grams;
- не стал ли русский деревянным;
- не отличается ли вариант одним очевидным token;
- не добавлены ли бессмысленные punctuation и `backticks`.

Цель:

```text
единый естественный формат
+ разные правдоподобные mental models
```

Не оптимизируй `structure-tell` до нуля.

---

# 20. Russian Readability Parity

Все четыре option text должны:

- читаться без спотыкания;
- быть естественным русским;
- не выглядеть машинным переводом;
- не содержать канцелярит;
- не быть искусственно раздутыми;
- не перегружаться clauses;
- не злоупотреблять `;`, `→`, скобками;
- использовать единые термины;
- не смешивать язык без причины.

Лучше небольшой естественный разброс длины, чем четыре деревянных клона.

---

# 21. Correct sections

Correct должен иметь:

```text
explanation
example
when_to_apply
edge_cases
related
```

## `explanation`

- объясняет механизм;
- не повторяет text;
- не вводит противоречие;
- учитывает scope;
- не говорит «это правильный ответ».

## `example`

- конкретный;
- фактически корректный;
- соответствует утверждению;
- использует реальный API;
- не содержит выдуманный incident.

## `when_to_apply`

- конкретные условия;
- trade-offs;
- не generic filler.

## `edge_cases`

- реальные ограничения;
- не копирует wrong options;
- не превращается в новую лекцию.

## `related`

- target существует;
- `Q<N>` существует;
- topic slug корректен;
- связь содержательная;
- формат соответствует schema.

---

# 22. Wrong sections

Wrong должен иметь:

```text
what_actually
source_of_confusion
if_it_were_true
how_it_should_be
```

## `what_actually`

Исправляет именно основную ошибку.

## `source_of_confusion`

Называет реалистичный источник:

- похожее API;
- старая версия;
- перенос свойства;
- неверный scope;
- частный случай, принятый за общий.

Запрещено:

```text
Разработчик просто не знает тему.
```

## `if_it_were_true`

Даёт наблюдаемое следствие:

- output;
- SQL;
- число запросов;
- state;
- exception;
- lifecycle;
- production symptom.

Не выдумывай компанию и ущерб.

## `how_it_should_be`

Коротко строит мост к correct mental model.

После изменения text старые sections нельзя оставлять без проверки.

---

# 23. Единственность ответа

Для каждого блока докажи внутренне:

1. Почему correct верен.
2. Почему wrong A неверен.
3. Почему wrong B неверен.
4. Почему wrong C неверен.
5. Не становится ли wrong верным:
   - в другой версии;
   - у другого provider;
   - при другой конфигурации;
   - в другом scope;
   - для другого API;
   - при скрытом условии.

Если wrong можно разумно защитить:

- сузь stem;
- измени misconception;
- уточни context;
- либо поставь `MANUAL_REVIEW`.

---

# 24. Fact freshness

Version-sensitive темы:

- Java/Kotlin/Spring/Hibernate versions;
- CLI;
- licenses;
- cloud policies;
- defaults;
- deprecations;
- API availability;
- benchmark numbers;
- security recommendations;
- protocol/library behavior.

Для них:

1. Проверь primary source.
2. Зафиксируй версию.
3. Зафиксируй checked date.
4. Обнови freshness sidecar.
5. Не используй vague claims:

```text
latest
current
сейчас
в настоящее время
по умолчанию в новых версиях
```

без версии.

Benchmark допустим только с:

- environment;
- mode;
- version;
- source.

---

# 25. Theory synchronization

Markdown менять только при semantic/factual change.

Используй `interview-writer`.

Правила:

- сохранять stable `Q<N>`;
- не массово перенумеровывать;
- не удалять полезную theory;
- обновить TOC/links при необходимости;
- не добавлять inline MCQ;
- style-only JSON change не требует theory change.

Если theory требует крупной переработки, в текущем тике исправляй только
затронутые `Q<N>`.

---

# 26. Разрешённые изменения JSON

Разрешено:

- `question_text`;
- `options[].text`;
- `options[].sections`;
- перестановка options целиком;
- `label`/`order` при перестановке;
- `correct` только при доказанной factual ошибке.

Без lifecycle-задачи запрещено менять:

- `topic_slug`;
- `q_number`;
- `block_idx`;
- удалять вопросы;
- создавать duplicate q_number;
- переносить вопрос в другой topic.

Инварианты:

- ровно 4 options;
- ровно 1 correct;
- `order = 0..3`;
- `label = A..D`;
- exact section keys;
- schema valid.

При перестановке перемещай целиком:

```text
text
correct
sections
```

---

# 27. Legacy phase

Legacy обрабатывай только после JSON-корпуса либо как отдельный legacy-only target.

Перед legacy work:

1. Прочитай `interview-options-writer`.
2. Найди современный JSON analog.
3. Не создавай новые SQLite options.
4. Не делай двустороннюю автоматическую sync.
5. Проверь running app/seeder.
6. Если JSON существует:
   - считать его современной истиной;
   - legacy отметить deprecated duplicate;
   - подготовить migration/manual-review note.
7. Если legacy единственный:
   - backup DB;
   - transaction;
   - 4 options;
   - 1 correct;
   - orders;
   - re-read persisted rows.

---

# 28. Применение изменений

Используй идемпотентный Python harness или точечный editor.

До записи сохрани snapshot:

- `topic_slug`;
- `q_number`;
- `block_idx`;
- число options;
- labels/orders;
- correct count;
- section keys;
- theory Q IDs.

После записи сравни:

- IDs не изменились;
- структура не дрейфовала;
- 1 correct;
- labels/orders согласованы;
- блоки не удалены;
- Markdown не получил inline MCQ;
- diff ограничен выбранной единицей.

JSON писать:

```python
json.dumps(data, ensure_ascii=False, indent=2)
```

Сохраняй текущую policy trailing newline репозитория.

---

# 29. Обязательные validation gates

После изменения JSON:

```bash
bash scripts/verify-mcq-json.sh <json>
python3 scripts/mcq-answer-parity-gate.py <json>
python3 scripts/mcq-form-balance.py <json>          # если form-tell gates красные (§18.1)
python3 scripts/mcq-answer-parity-gate.py <json>   # повтор после баланса
python3 scripts/mcq-skel-gate.py <json>            # SKEL pre-check; PASS ≠ SKEL=✅ (нужен LLM)
python3 scripts/mcq-structure-parity.py --file <json>
```

Когда доступны:

```bash
python3 scripts/mcq-stamp-audit.py --file <json>
python3 scripts/audit-mcq-parity.py --caricature <json>
```

Для Markdown:

- heading/parser check;
- duplicate Q IDs;
- inline MCQ ban;
- TOC;
- links;
- q-number sync.

Inline MCQ ban:

```bash
if rg -n '^> \[!mcq\]' <md>; then
  echo "Legacy inline MCQ found"
  exit 1
fi
```

При наличии integration tests:

```bash
./gradlew :quiz-app:test \
  --tests "*McqJsonLoader*" \
  --tests "*ImportService*"
```

Не запускай full suite каждый тик без необходимости.

---

# 30. Answer-parity gates (скрипты — pre-check)

Скриптовые gates **необходимы, но недостаточны**. Зелёный
`mcq-answer-parity-gate.py` / `mcq-skel-gate.py` без §31 LLM на каждый блок
**не даёт** права ставить `SKEL=✅`, `PAR=✅`, `C-FRM=✅`, `FINAL=✅`.

`mcq-skel-gate.py` ловит только грубые mismatch; semantic/«почти тот же каркас»
— только LLM.

Все обязательные gates текущего `mcq-quality-fixer` должны быть `ok`.

## Hard gates (блокируют chunk/file без исключений)

- `STYLE_GUESSABILITY` ≤ 0.40;
- `ABSOLUTE_MARKER_GAP` ≤ 0.25;
- `CORRECT_LONGEST_RATE` ≤ 0.40;
- `CORRECT_WRONG_AVG_RATIO` в [0.90, 1.10];
- `SENTENCE_PARITY` — нет блоков с разрывом > 1;
- `DETAIL_PARITY` ≤ 0.40;
- `CORRECT_POSITION_SEQUENCE` ≤ 5;
- `DUPLICATE_NGRAMS` ≤ 3;
- `SOURCE_COVERAGE` — для version-sensitive файлов.

Подробные правила снятия form-tells — §18.1. Автопроход: `scripts/mcq-form-balance.py`.

## Soft / documented exceptions

- `OPTION_LENGTH_RATIO` > 1.35 допустим **только** при documented accepted-exception
  (конфликт с anti-stamp CV≥0.14) и при условии, что все hard gates выше — PASS
  **и** §31 LLM-review закрыт на каждый блок chunk.

Ожидаемые проверки гейта могут также включать:

- `CORRECT_LONGEST_RATE`;
- `OPTION_LENGTH_RATIO`;
- `CORRECT_WRONG_AVG_RATIO`;
- `SENTENCE_PARITY`;
- `DETAIL_PARITY`;
- `ABSOLUTE_MARKER_GAP`;
- `CORRECT_POSITION_DISTRIBUTION`;
- `CORRECT_POSITION_SEQUENCE`;
- `DUPLICATE_NGRAMS`;
- `STYLE_GUESSABILITY`;
- `SOURCE_COVERAGE`.

Пороги брать из текущего skill/script.

Если script и skill расходятся:

- не обходить;
- записать conflict;
- определить текущий реальный contract;
- при необходимости поставить manual review.

---

# 31. Четырёхсторонняя LLM-проверка (обязательная)

**Каждый** MCQ-блок проходит полную сверку всех четырёх вариантов в **одном**
логическом проходе. Нельзя проверять correct и distractors по отдельности без
сравнения друг с другом.

Минимум: **один LLM-проход на блок** до правок; **повтор** после любого изменения
`question_text` / `option.text` / `sections`.

## 31.1. Входные данные

Для блока `Q<N>` прочитай целиком:

```text
question_text
options[A].text, options[B].text, options[C].text, options[D].text
options[*].sections (все ключи)
параллельный фрагмент theory ## Q<N>
```

Пометь, какой label сейчас `correct: true` — но при form-check (BST) временно
игнорируй эту метку.

## 31.2. Покритериальная таблица (заполняется в sidecar)

Для **каждого** из четырёх вариантов ответь явно (не `null`, не «в целом ок»):

| Ось | Correct | Каждый distractor |
|---|---|---|
| **FCT** | фактически верен под stem? | ложен под stem (не «частично верен»)? |
| **UNI** | единственный защитимый? | — |
| **PLS** | — | правдоподобен middle/senior? |
| **1ER** | — | ровно одна главная ошибка? |
| **SEC** | text ↔ sections согласованы? | text ↔ sections согласованы? |
| **FRM** | не выделяется длиной/тоном/code? | сопоставим с correct по форме? |
| **SKEL** | на том же surface skeleton, что и остальные 3? | на том же skeleton, что correct? |
| **DIV** | — | три разных misconception (не дубль)? |

**`SKEL` — HARD:** если хотя бы один вариант на другом каркасе (зачин / число
предложений-клауз / класс `;`:`—`/скобок / backticks-vs-проза) — блок FAIL,
даже при зелёных скриптах и «примерно похожей» длине.

Если любой пункт FAIL — блок остаётся `TODO` / `❌`, даже при зелёных скриптах.

## 31.3. Cross-option сравнение (только LLM)

Смотри на **набор из 4 options** одновременно:

1. **Зеркальные пары** — два distractor спорят друг с другом, выдавая correct
   (напр. «ON раньше» vs «WHERE раньше»).
2. **Meta-hint** — один wrong отсекает другой, сужая выбор до 2 вариантов.
3. **Contra-pair** — A и D взаимоисключающие формулировки одного факта.
4. **Catalog tell** — только один вариант перечисляет 4+ API/оператора.
5. **Tone tell** — только correct без absolutes / только wrong с absolutes.
6. **Length/code tell** — correct единственный лидер по словам или `` ` `` (gate
   может не поймать контекстный tell — LLM обязан).
7. **Skeleton tell (`SKEL`)** — варианты на разных surface templates (разный
   зачин, клаузы, пунктуационный каркас, класс backticks/списков).
8. **Partial truth** — wrong верен в первой половине фразы, ложен в выводе.
9. **Second defensible** — ещё один вариант можно защитить под stem.

При нахождении — правка **до** commit; тип fix записать в sidecar `notes`.

## 31.4. Blind sub-checks (отдельный reviewer-subagent)

После LLM-прохода и правок запусти **независимого** subagent (не того, кто правил):

### Style blind (BST)

Скрыть: stem, correct-метку, sections. Показать только 4 текста A/B/C/D.

Reviewer: «Какой вариант выглядит правильным **только по форме**?»

- угадывает correct → `BST = FAIL`
- «два кандидата» → `BST = FAIL` (meta-hint)

### Semantic defense (BSM)

Показать: stem + 4 options без correct-метки.

Reviewer пытается **защитить каждый** distractor как correct.

- любой wrong защитим → `BSM = FAIL`

Subagent обязателен на **каждый chunk** (10–20 блоков). Spot-check на 2–3 блока
**не** заменяет полный прогон chunk.

## 31.5. Запись в review-sidecar

На каждый блок — запись с флагами §31.2 + итог:

```json
{
  "q_number": 12,
  "llm_review": {
    "four_option_pass": true,
    "skel": true,
    "cross_option_issues": [],
    "bst": true,
    "bsm": true,
    "reviewed_by": "agent|subagent",
    "notes": []
  }
}
```

`skel: true` только если все 4 `option.text` на одном surface skeleton (§18).
`four_option_pass: true` только если FCT/UNI/PLS/1ER/SEC/FRM/**SKEL**/DIV
закрыты **и** BST/BSM PASS. Без `skel: true` — `four_option_pass` запрещён.

> Blind review (BST/BSM) — см. §31.4. Отдельный reviewer-subagent обязателен.

---

# 33. Финиш файла или chunk

Единица готова, только когда:

1. Все выбранные blocks проверены **§31 (LLM four-option review)**.
2. Correct фактически верен (FCT).
3. Correct единственный (UNI).
4. Wrong правдоподобны (PLS).
5. Wrong однозначно ложны (BSM PASS).
6. Misconceptions различаются (DIV).
7. Sections согласованы (SEC).
8. **Единый surface skeleton (`SKEL=✅`, `llm_review.skel=true`) на каждый блок.**
9. Visual parity пройдена (FRM + BST PASS) — только после SKEL.
10. Русский естественный.
11. Нет stamped clones.
12. Theory синхронизирована.
13. Freshness обновлена.
14. Скриптовые validators проходят (pre-check, §30).
15. Diff bounded.
16. Sidecar обновлён (`llm_review.skel=true` и `four_option_pass=true` на каждый блок).
17. Plan обновлён (колонка `SKEL` и остальные).

Для chunk:

- JSON остаётся валидным;
- обработанный диапазон записан;
- file status `◐ PARTIAL`;
- `FINAL=✅` только после полного файла.

---

# 34. Обновление мастер-плана

После review обновляй каждую колонку отдельно.

Запрещено:

- массово ставить все `✅`;
- ставить `FINAL=✅` при `⬜`, `◐`, `❌`, `🟠`;
- ставить `PAR=✅` / `FINAL=✅` при `SKEL≠✅`;
- переносить старые statuses;
- считать schema PASS или зелёный form-balance достаточным для `SKEL`;
- закрывать полный файл после одного chunk.

Если файл изменился после `reviewed_commit`, сбрось затронутые criteria в `RECHECK`.

---

# 35. Commit protocol

Один тик — один атомарный commit.

Разрешённый набор:

```text
target JSON
parallel theory, если semantic sync
review-sidecar
freshness sidecar
PLAN_INTERVIEW.md
targeted tests, если добавлены
```

Используй explicit pathspec.

Пример:

```bash
git commit -m "pedago(mcq): <topic> — ROUND-9 full quality audit Q<X>-Q<Y>" -- \
  <json-path> \
  <optional-md-path> \
  <review-sidecar-path> \
  <optional-freshness-sidecar-path> \
  PLAN_INTERVIEW.md
```

Не использовать `git add -A`.

Не push.

---

# 36. Отчёт каждого тика

```markdown
## MCQ corpus sweep tick

### Unit

- JSON:
- Theory:
- q_number / blocks:
- Mode:
- Reason selected:

### Baseline (скрипты = pre-check)

- Schema:
- Answer parity (gate):
- Structure:
- Stamp:
- Caricature:
- Theory sync:
- Freshness:

### LLM four-option review (§31 — обязательно)

- Blocks reviewed LLM:
- Cross-option issues (mirrors, meta-hints, catalog tells):
- FCT/UNI/PLS/1ER/SEC/FRM/DIV fails:
- BST (subagent):
- BSM (subagent):
- Blocks with `four_option_pass: false`:

### Correct findings

- Fact errors:
- Scope/version:
- Completeness:
- Unique answer:
- Visual tells:
- Correct sections:

### Distractor findings

- Plausibility:
- Multiple errors:
- Partial truths:
- Duplicate misconceptions:
- Caricatures:
- Format/readability:
- Wrong sections:

### Changes

- question_text:
- correct:
- distractors:
- correct sections:
- wrong sections:
- position:
- theory:
- freshness:
- legacy:

### Validation

- verify-mcq-json:
- answer-parity:
- structure-parity:
- stamp:
- caricature:
- blind style:
- blind semantic:
- Markdown/parser:
- q-number sync:
- tests:

### Commit

- Hash:
- Message:

### Plan

- Columns changed:
- Final state:
- Remaining blocks:
- Manual review:
- Next candidate:
```

Не скрывай failed checks.

---

# 37. Глобальные запреты

Никогда:

- не доверять `correct:true` без semantic review;
- не генерировать A/B/C/D до mental models;
- не оставлять correct единственным качественным вариантом;
- не делать wrong очевидной ерундой;
- не раздувать wrong filler-словами;
- не оптимизировать structure metric к нулю;
- не оставлять sections от старой misconception;
- не выдумывать API;
- не выдумывать incidents;
- не использовать непроверенные benchmarks;
- не добавлять inline MCQ;
- не массово перенумеровывать `Q<N>`;
- не создавать новый legacy content;
- не поддерживать JSON и SQLite как две современные истины;
- не менять unrelated theory;
- не удалять полезный контент;
- не делать broad formatter по корпусу;
- не игнорировать dirty collision;
- не обходить validators;
- не объявлять DONE по одному schema check;
- **не закрывать блок/chunk только зелёным `mcq-answer-parity-gate.py`** — без §31 LLM-review;
- **не считать `mcq-form-balance.py` / `mcq-skel-gate.py` заменой BST/BSM,
  LLM-`SKEL` и логической сверки 4 options**;
- **не ставить `skel: true` / `SKEL=✅` только по зелёному `mcq-skel-gate.py`**;
- **не ставить `PAR=✅` / `FINAL=✅` / `four_option_pass: true` при `SKEL≠✅`**;
- **не оставлять correct и wrong на разных surface skeletons**;
- не ставить `four_option_pass: true` без чтения всех A/B/C/D и без `skel: true`;
- не использовать `git add -A`;
- не откатывать чужие изменения;
- не push.

---

# 38. Stop condition полного sweep

Loop останавливается, только когда:

1. Все JSON MCQ представлены в plan.
2. Для каждого файла существует review-sidecar.
3. Каждый block имеет заполненные checks, `llm_review.skel: true` **и**
   `llm_review.four_option_pass: true`.
4. Все обязательные file criteria `✅` (включая `SKEL`, BST/BSM через §31.4).
5. Фактически неверных correct = 0.
6. Multiple defensible answers = 0.
7. Partially true distractors = 0.
8. Obvious/caricature distractors = 0.
9. Visually Distinguishable Correct Option = 0.
9a. Skeleton mismatch (`SKEL≠✅`) = 0 на каждый блок каждого файла.
10. Stamped clones = 0.
11. Sections согласованы.
12. Theory dangling = 0.
13. Schema failures = 0.
14. Blocking parity/stamp failures = 0.
15. Freshness coverage complete.
16. Legacy отделён от современной JSON-истины.
17. Unresolved manual review = 0 либо явно разрешён пользователем.
18. Reviewed commit записан для каждого `FINAL=✅`.
19. Финальные targeted/integration tests проходят.
20. Рабочее дерево чисто от изменений loop.

Финальная секция `PLAN_INTERVIEW.md`:

```markdown
# Final MCQ Corpus Sweep

Status: DONE

| Metric | Result |
|---|---:|
| JSON files reviewed | |
| Questions reviewed | |
| Blocks reviewed | |
| Correct answers fixed | |
| Distractors rewritten | |
| Sections rewritten | |
| Question stems fixed | |
| Theory sections synced | |
| Freshness sidecars updated | |
| Legacy-only items | |
| Manual-review items | |

## Validation

...

## Final commit

...
```

После выполнения stop condition закоммить финальную сводку и останови loop.
