/loop 15m

ROUND-8 FULL MCQ CORPUS RESET AND QUALITY SWEEP

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
5. Замени рабочий план новым ROUND-8 планом:
   - `PLAN_INTERVIEW_ROUND8_RESET.md`, если он уже лежит в проекте;
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
- `C-FRM` — correct не выделяется визуально.
- `C-SEC` — correct sections точны и согласованы.
- `C-RU` — correct написан естественным русским.

## Неправильные ответы

- `W-PLS` — каждый distractor правдоподобен.
- `W-1ER` — одна основная ошибка.
- `W-API` — реальные API/понятия.
- `W-FLS` — distractor ложен под stem.
- `W-DIV` — разные misconceptions.
- `W-CAR` — нет карикатуры, абсурда и negation echo.
- `W-FRM` — format/density parity.
- `W-SEC` — wrong sections согласованы.
- `W-RU` — естественный русский.

## Итоговые gates

- `PAR` — общий parity.
- `BST` — blind style.
- `BSM` — blind semantic.
- `STAMP` — нет stamped clone.
- `VAL` — все validators.
- `FINAL` — файл полностью принят.

Не заменяй эти критерии одной колонкой `Golden`.

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
5. Запусти доступные validators.
6. Сохрани baseline в review-sidecar и plan notes.

Минимум:

```bash
bash scripts/verify-mcq-json.sh <json>
python3 scripts/mcq-answer-parity-gate.py <json>
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

Сравни:

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

Используй общий semantic skeleton, а не буквальное клонирование.

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

# 30. Answer-parity gates

Все обязательные gates текущего `mcq-quality-fixer` должны быть `ok`.

Ожидаемые проверки могут включать:

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

# 31. Blind review

## Style blind check

Скрыть:

- stem;
- correct;
- sections.

Оставить только options.

Независимый reviewer/subagent пытается выбрать вариант, который выглядит correct
по форме.

Если correct угадывается:

```text
BST = FAIL
```

## Semantic defense check

Показать stem и options без correct.

Reviewer пытается защитить каждый wrong.

Если wrong разумно защитим:

```text
BSM = FAIL
```

Blind checks должен выполнять отдельный reviewer/subagent, не тот же генератор.

---

# 32. Финиш файла или chunk

Единица готова, только когда:

1. Все выбранные blocks проверены.
2. Correct фактически верен.
3. Correct единственный.
4. Wrong правдоподобны.
5. Wrong однозначно ложны.
6. Misconceptions различаются.
7. Sections согласованы.
8. Visual parity пройдена.
9. Русский естественный.
10. Нет stamped clones.
11. Theory синхронизирована.
12. Freshness обновлена.
13. Validators проходят.
14. Diff bounded.
15. Sidecar обновлён.
16. Plan обновлён.

Для chunk:

- JSON остаётся валидным;
- обработанный диапазон записан;
- file status `◐ PARTIAL`;
- `FINAL=✅` только после полного файла.

---

# 33. Обновление мастер-плана

После review обновляй каждую колонку отдельно.

Запрещено:

- массово ставить все `✅`;
- ставить `FINAL=✅` при `⬜`, `◐`, `❌`, `🟠`;
- переносить старые statuses;
- считать schema PASS достаточным;
- закрывать полный файл после одного chunk.

Если файл изменился после `reviewed_commit`, сбрось затронутые criteria в `RECHECK`.

---

# 34. Commit protocol

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
git commit -m "pedago(mcq): <topic> — ROUND-8 full quality audit Q<X>-Q<Y>" -- \
  <json-path> \
  <optional-md-path> \
  <review-sidecar-path> \
  <optional-freshness-sidecar-path> \
  PLAN_INTERVIEW.md
```

Не использовать `git add -A`.

Не push.

---

# 35. Отчёт каждого тика

```markdown
## MCQ corpus sweep tick

### Unit

- JSON:
- Theory:
- q_number / blocks:
- Mode:
- Reason selected:

### Baseline

- Schema:
- Answer parity:
- Structure:
- Stamp:
- Caricature:
- Theory sync:
- Freshness:

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

# 36. Глобальные запреты

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
- не использовать `git add -A`;
- не откатывать чужие изменения;
- не push.

---

# 37. Stop condition полного sweep

Loop останавливается, только когда:

1. Все JSON MCQ представлены в plan.
2. Для каждого файла существует review-sidecar.
3. Каждый block имеет заполненные checks.
4. Все обязательные file criteria `✅`.
5. Фактически неверных correct = 0.
6. Multiple defensible answers = 0.
7. Partially true distractors = 0.
8. Obvious/caricature distractors = 0.
9. Visually Distinguishable Correct Option = 0.
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
