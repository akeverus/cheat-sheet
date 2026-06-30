# PLAN_INTERVIEW — качество MCQ JSON-сидеров (Option Parity / Structural Parity)

Бесконечный `/loop 15m` цикл (cron `ae1e19ff`). Цель: правильный вариант в MCQ
**нельзя угадать по форме** — длине, структуре, насыщенности, стилю. Лечим не
сокращением correct, а **поднятием distractor-ов** до его уровня (плюс перенос
лишних деталей correct в `sections`, которые рендерятся после ответа).

## Правило (закреплено в skills)

- **Канон:** `~/.claude/skills/mcq-quality-fixer/SKILL.md` → раздел
  «### Option Parity (MANDATORY — the strongest rule)»: бар по длине/форме/числу
  утверждений/структуре + «Structural-signature tell» (correct не должен быть
  единственным с `→`/`:`/`;`/backtick-перечислением) + «Cross-block consistency»
  (correct не должен быть систематически самым длинным во всём файле, цель ~25%).
- **`interview-writer`** (часть MCQ-sync) — ссылается на канон при синхронизации опций.
- **`interview-options-writer`** (deprecated) — синхронизирован для legacy-аудита,
  для нового контента не используется.

## Тулчейн

- `scripts/audit-mcq-parity.py` — **аудит-скрипт** (находит кандидатов, решение за человеком):
  per-block `LEN_AVG>1.3`, `LEN_SPREAD>1.5`, `UNIQ_MARKER` (correct уникально структурен),
  `COMMA_GAP`, `SHORT_DISTR`; file-level `CORRECT_LONGEST_RATE` (доля блоков, где
  correct — самый длинный; >0.5 = файл угадывается). `--top N`, `-v`.
- `scratchpad/goal_loop/diagnose.py` — строгий per-block детектор (LENGTH_TELL/SURFACE/THIN/DUP).
- `gate_struct.py` — HEAD-vs-current: q_number/question_text/order/label/correct неизменны.
- `apply_patch.py` — безопасный ассемблер (меняет ТОЛЬКО text/sections).
- `wf_mcq_chunked.js` + `harvest_chunked.py` + `gate_commit.py` — chunked-воркфлоу.
- `scripts/verify-mcq-json.sh` — schema-валидатор.

## Инварианты (НЕЛЬЗЯ нарушать)

1. Правим ТОЛЬКО `seed/mcq/**/*.json` (НЕ `.md` — там pre-commit банит MCQ-маркеры).
2. Никогда не менять `correct` / `label` / `order` / `q_number` / `question_text` — только `text`/`sections`.
3. Поле `correct` (НЕ `is_correct`). JSON: `ensure_ascii=False, indent=2`, без trailing-newline.
4. Ровно 1 `correct: true` на блок. Факты 1:1 (перенос в sections, не потеря).
5. Explicit pathspec при коммите (чужой WIP не коммитить). ≤3 параллельных субагента.
6. Не трогать файлы параллельной сессии: `ai-ml/agentic-patterns`, `devops/git`, `testing/chaos-engineering`.

## Рецепт итерации

1. `audit-mcq-parity.py --top N` → worst-first кандидаты (по `CORRECT_LONGEST_RATE` и числу флагов).
2. На файл: editor-субагент (раскрывает distractor-ы до паритета, убирает stubs, даёт им
   собственную правдоподобно-ложную структуру; лишнее correct → sections).
3. Гейты: `diagnose.py` (tells=0) → `gate_struct.py` (PASS) → `verify-mcq-json.sh` (OK) →
   cross-section dup-скан (0) → `audit-mcq-parity.py` (UNIQ/SHORT/LEN сняты, longest≤~60%).
4. Адверс-ревьюер (fidelity_ok + parity_ok, без блокирующих регрессий).
5. Атомарный per-file коммит: `pedago(mcq): <topic> — single-delta паритет опций (N блоков)`.

## Статус (2026-06-30)

- ✅ Правило Option Parity закреплено в 3 skills (+ structural/cross-block augmentation).
- ✅ Аудит-скрипт `scripts/audit-mcq-parity.py` создан и прогнан по корпусу.
- ✅ Закоммичены: `azure-interview` (1e42aa0c, +восстановлен Q14-факт), `code-agents-interview` (7a901ca1).
- 🔧 `leadership/mentoring-interview` (negative example, 96% correct-longest, stub-дистракторы Q2/5/6/7/8/10/11/13) — re-edit этим тиком.
- ⏳ reviewer-less, gate-clean, ждут ревьюера: `ai-ml/rag` (43%), `system-design/design-uber` (37%), `devops/kubernetes` (56%).
- ⏳ untouched, re-queue: `model-serving`, `clickhouse`.
- 📊 Аудит: дефект корпус-wide (≈100% correct-longest у большинства файлов) → стандартный
  worst-first цикл продолжается; «все сидеры исправлены» — цель цикла, не одного тика.

## Координация

Параллельная claude-сессия работает worst-first СВЕРХУ (frontier ranks 12–14, static).
Я беру нижний слой; перед каждым слайсом — `git status seed/mcq` → skip их dirty-файлов.
