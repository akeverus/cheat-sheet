# MCQ JSON Manual Audit Plan

Дата старта: 2026-07-01

## Цель

Вручную перепроверить все JSON MCQ seeders в
`modules/quiz-app/src/main/resources/seed/mcq/**/*.json` на соответствие правилам:

- schema correctness;
- синхронизация с parallel `.md` interview-файлом;
- Option Parity / Structural Parity;
- Plausibility Parity;
- Russian Readability Parity;
- отсутствие угадываемости correct option по длине, структуре и тону;
- корректные explanation sections для correct/wrong options;
- естественный русский язык без карикатурных distractor-ов.

Рабочий ledger: `.claude/mcq-manual-audit/ledger.tsv`.

## Baseline на 2026-07-01

- MCQ JSON files: 318
- Blocks scanned: 9778
- Schema validation: all 318 valid
- `.md` sidekick coverage: 318 из 318
- JSON without `.md`: 0
- `q_number` mismatch: 0
- `topic_slug` mismatch: 0
- Parity-flagged files: 315
- Parity-flagged blocks: 9301
- Critical parity files: 297
- Readability-flagged files: 265
- Readability-flagged blocks: 1473
- Readability HIGH files: 4

## Definition of Done для одного JSON файла

Файл считается вручную проверенным только если выполнены все пункты:

1. `topic_slug` совпадает с basename файла.
2. Каждый `q_number` существует в parallel `.md`.
3. Каждый `block_idx` присутствует и стабилен.
4. В каждом block ровно 4 options.
5. `order` равен `0..3`, `label` равен `A/B/C/D` и соответствует `order`.
6. Ровно один `correct: true`.
7. Correct option содержит только секции `explanation`, `example`, `when_to_apply`, `edge_cases`, `related`.
8. Wrong option содержит только секции `what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`.
9. `text` options не содержит markdown bold и не выглядит как подсказка правильного ответа.
10. Все 4 options сопоставимы по длине: целевой разброс не больше 20-30%, если смысл не требует иначе.
11. Все 4 options сопоставимы по структуре: если correct имеет причину, последовательность, перечисление или кодовый термин, distractor-ы не должны быть короткими заглушками.
12. Wrong options правдоподобны для middle/senior-разработчика и выражают реалистичную ошибочную модель.
13. Wrong options не используют маркеры карикатуры: токсичный менеджмент, абсурдные действия, абсолютные «всегда/никогда» без технической причины, dismissive-tone.
14. Все options написаны естественным русским языком.
15. Выравнивание длины не достигнуто ценой машинных цепочек, лишних стрелок, semicolon-списков или канцелярита.
16. Correct answer нельзя угадать по самому длинному, самому подробному или самому «умному» варианту.
17. Sections объясняют именно выбранный вариант, не противоречат `.md` theory и не содержат лишних section keys.
18. После правок файл проходит `bash scripts/verify-mcq-json.sh <file>`.
19. После правок файл проверен через `python3 scripts/audit-mcq-parity.py -v --top 1 <file>` и оставшиеся флаги либо исправлены, либо явно записаны в notes ledger-а как осознанные false positive / deferred.

## Ручной протокол проверки блока

Для каждого block:

1. Прочитать вопрос и соответствующий `## Q<N>.` в `.md`.
2. Определить правильную концепцию без просмотра `correct`.
3. Проверить, что отмеченный correct совпадает с `.md` и не меняет смысл темы.
4. Сначала выписать для себя 4 mental models, не редактируя варианты:
   - correct mental model в 2-4 предложениях;
   - wrong mental model 1 с одной правдоподобной ошибкой;
   - wrong mental model 2 с другой правдоподобной ошибкой;
   - wrong mental model 3 с третьей правдоподобной ошибкой.
5. Для каждой wrong mental model проверить:
   - это не глупость и не токсичная карикатура;
   - такую ошибку реально может сделать middle/senior;
   - в ней есть частично верная логика;
   - ошибка держится на одном ключевом смещении.
6. Только после mental models читать/редактировать `A/B/C/D`.
7. Прочитать все 4 option text подряд как кандидат.
8. Задать вопросы:
   - можно ли угадать correct по длине;
   - можно ли угадать correct по структуре;
   - есть ли один вариант с уникальным набором backtick-терминов;
   - есть ли один вариант с уникальным алгоритмом, стрелками, semicolon-цепочкой или причинной связкой;
   - звучат ли wrong options как реальные заблуждения;
   - не является ли wrong option карикатурой;
   - звучит ли каждый вариант естественно по-русски;
   - не был ли wrong option искусственно раздут ради длины.
9. Проверить forbidden markers в option text:
   - `всегда`;
   - `никогда`;
   - `любой ценой`;
   - `якобы`;
   - `заставить`;
   - `угрожать`;
   - `публично и жёстко`;
   - `не спрашивая`;
   - `оставить в покое`;
   - `ничего не делать`.
10. Проверить, что стрелки `→` не используются как машинная цепочка, если это не настоящий алгоритм.
11. Проверить sections:
   - correct sections объясняют правильный ответ, дают пример, область применения, edge cases и related;
   - wrong sections объясняют, что на самом деле, источник путаницы, при каком условии вариант был бы верным, и как правильно.
12. Если блок правится, сначала усиливать distractor-ы, а не сокращать correct до плохого уровня.
13. Сохранять Single-Delta principle: wrong option отличается одной ключевой ошибкой, а не полной бессмыслицей.
14. Сделать self-review по каждому варианту:
   - звучит естественно по-русски;
   - его мог бы выбрать реальный инженер;
   - он не выглядит карикатурой;
   - он похож по уровню детализации на остальные;
   - в нём есть понятная ошибочная логика.
15. Сделать self-review блока:
   - correct не самый длинный;
   - correct не самый технически насыщенный;
   - correct не единственный со структурой;
   - wrong options не палятся токсичным тоном;
   - все варианты читаются без спотыкания;
   - нет ощущения, что варианты искусственно растянуты.
16. Если слабый только один вариант, переписывать только его, а не весь блок.

## Приоритеты ручного прохода

### P0: формат и синхронизация

Сейчас P0 пуст: schema validation, `topic_slug`, `.md` sidekick и `q_number` sync прошли.

### P1: самые рискованные parity/readability файлы

Первые партии:

1. `databases/postgresql-interview.json` — начато, Q1-Q20 cleaned, продолжить с Q21.
2. `programming-languages/java/java-concurrency-interview.json`
3. `reactive/project-reactor-interview.json`
4. `design-patterns/design-patterns-interview.json`
5. `programming-languages/java/java-collections-interview.json`
6. `reactive/rxjava-interview.json`
7. `system-design/design-payment-system-interview.json` — readability HIGH.
8. `testing/chaos-engineering-interview.json` — readability HIGH, файл уже изменён в рабочем дереве до этого прохода.
9. `behavioral/failure-stories-interview.json` — readability HIGH.
10. `code-quality/code-review-interview.json` — readability HIGH.

### P2: category sweeps

После P1 пройти все категории целиком, не прыгая между доменами:

1. `databases` — 21 files
2. `programming-languages` — 51 files
3. `frameworks` — 36 files
4. `ai-ml` — 26 files
5. `architecture` — 24 files
6. `system-design` — 22 files
7. `algorithms` — 18 files
8. `testing` — 16 files
9. `devops` — 13 files
10. `security` — 10 files
11. `messaging` — 9 files
12. `monitoring` — 9 files
13. `performance` — 8 files
14. `data-engineering` — 8 files
15. `api` — 8 files
16. `leadership` — 7 files
17. `code-quality` — 7 files
18. `reactive` — 6 files
19. `cloud` — 6 files
20. `behavioral` — 6 files
21. `jvm` — 3 files
22. `cicd` — 2 files
23. `logging` — 1 file
24. `design-patterns` — 1 file

## Batch size

Ручной проход делается малыми партиями:

- 1 high-risk file per batch, если файл имеет 30+ flagged blocks;
- 2-4 medium-risk files per batch;
- 5-8 low-risk files per batch, если изменения минимальны.

Внутри большого файла:

- исправлять 4-8 blocks за итерацию;
- после каждого batch запускать schema validation именно для изменённых JSON;
- после каждого batch обновлять ledger;
- не смешивать правки разных доменов без необходимости.

## Ledger statuses

- `pending` — файл ещё не проверен вручную;
- `in_progress` — начат ручной проход, остались блоки;
- `manual-reviewed` — файл прочитан вручную, правки не требуются или уже внесены;
- `fixed` — файл правился и прошёл проверки;
- `deferred` — есть осознанный остаточный риск, причина записана в notes;
- `blocked` — нельзя безопасно править из-за конфликтующих пользовательских изменений.

## Validation commands

Для одного файла:

```bash
bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/<path>.json
python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/<path>.json
```

Для всего корпуса:

```bash
find modules/quiz-app/src/main/resources/seed/mcq -type f -name '*.json' -print0 \
  | xargs -0 bash scripts/verify-mcq-json.sh

python3 scripts/audit-mcq-parity.py \
  --json-report /tmp/mcq-parity-current.json \
  --markdown-report /tmp/mcq-parity-current.md \
  --top 20
```

Для `.md` sync:

```bash
python3 - <<'PY'
import json, re
from pathlib import Path
root = Path.cwd()
md_root = root / "cheatsheets/interview"
json_root = root / "modules/quiz-app/src/main/resources/seed/mcq"
issues = []
for jp in sorted(json_root.rglob("*.json")):
    rel = jp.relative_to(json_root)
    mp = md_root / rel.with_suffix(".md")
    if not mp.exists():
        issues.append((str(rel), "missing-md", ""))
        continue
    md_q = {int(m.group(1)) for m in re.finditer(r"^## Q(\d+)\.", mp.read_text(encoding="utf-8"), re.M)}
    data = json.loads(jp.read_text(encoding="utf-8"))
    if data.get("topic_slug") != jp.stem:
        issues.append((str(rel), "topic_slug-mismatch", data.get("topic_slug")))
    for q in data.get("questions", []):
        if q.get("q_number") not in md_q:
            issues.append((str(rel), "q-not-in-md", q.get("q_number")))
print(issues)
PY
```

## Reporting format per batch

Каждый batch в итоговом отчёте должен содержать:

- файлы и диапазон вопросов;
- найденные нарушения;
- что исправлено;
- что осталось и почему;
- `.md` sync;
- schema validation;
- parity/readability audit after;
- обновление ledger.

## Current next action

Продолжить `databases/postgresql-interview.json` с Q21-Q24, потому что файл остаётся top-risk:

- current flags: 35;
- correct longest rate: 89.1%;
- correct most technical rate: 78.2%;
- schema valid;
- Q1-Q20 уже не попадают в parity output.
