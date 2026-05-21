# Self-Learning Plan — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Provести диагностический pass по 6 сигналам, синтезировать приоритизированный backlog тем, написать единый Markdown-план самообучения, и создать 5 Singularity задач для первой темы цикла.

**Architecture:** Evidence-driven. Все сигналы сначала сохраняются в `diagnostic/*.tsv|md` как сырые данные, потом синтезируются в ранжированный backlog (`08-ranked-candidates.md`), затем кристаллизуются в финальный план (`2026-05-20-self-learning-plan.md`). Каждый шаг — отдельный коммит, чтобы можно было откатиться или оспорить.

**Tech Stack:** Bash + jq + git для диагностики. Singularity MCP (`mcp__singularity__*`) для финальных задач. Никакого Python — простые shell pipelines.

**Спека:** `docs/superpowers/specs/2026-05-20-learning-plan-design.md`

---

## Task 1: Создать diagnostic/ и собрать Signal #1 (TOC categories)

**Files:**
- Create: `docs/learning-plan/diagnostic/01-toc-categories.tsv`
- Read: `cheatsheets/interview/TOC.md`

- [ ] **Step 1: Создать каталог**

```bash
mkdir -p docs/learning-plan/diagnostic
```

Expected: молчаливое выполнение.

- [ ] **Step 2: Извлечь категории из TOC.md**

`TOC.md` использует heading-структуру. Категории — это `## <Name>` (level-2 headings) и подкатегории — `### <Name>` (level-3). Извлечь оба уровня + посчитать вложенные ссылки.

```bash
awk '
  /^## / { cat=substr($0, 4); print cat "\t" "category" }
  /^### / && cat { sub=substr($0, 5); print cat " / " sub "\t" "subcategory" }
' cheatsheets/interview/TOC.md > docs/learning-plan/diagnostic/01-toc-categories.tsv
wc -l docs/learning-plan/diagnostic/01-toc-categories.tsv
```

Expected: ≥ 25 строк (мы знаем что в interview/ 25 каталогов).

- [ ] **Step 3: Sanity check — глазами**

```bash
head -30 docs/learning-plan/diagnostic/01-toc-categories.tsv
```

Expected: видишь категории вроде `ai-ml`, `algorithms`, `api`, etc.

- [ ] **Step 4: Commit**

```bash
git add docs/learning-plan/diagnostic/01-toc-categories.tsv
git commit -m "diagnostic(signal-1): extract TOC categories from interview/TOC.md"
```

---

## Task 2: Signal #2 — размеры файлов interview/*.md

**Files:**
- Create: `docs/learning-plan/diagnostic/02-interview-sizes.tsv`

- [ ] **Step 1: Собрать размеры по строкам**

```bash
find cheatsheets/interview -name '*.md' -type f -not -name 'TOC.md' -not -name 'README.md' \
  | while read f; do
      lines=$(wc -l < "$f" | tr -d ' ')
      rel="${f#cheatsheets/interview/}"
      cat="${rel%%/*}"
      name="${rel#*/}"
      printf "%s\t%s\t%s\t%s\n" "$cat" "$name" "$lines" "$f"
    done \
  | sort -k1,1 -k3,3n > docs/learning-plan/diagnostic/02-interview-sizes.tsv
wc -l docs/learning-plan/diagnostic/02-interview-sizes.tsv
```

Expected: ~171 строка.

- [ ] **Step 2: Помечать тонкие файлы (< 200 строк)**

Это уже видно в TSV (3-я колонка). Просто проверим топ-20 самых тонких:

```bash
sort -t$'\t' -k3,3n docs/learning-plan/diagnostic/02-interview-sizes.tsv | head -20
```

Expected: список самых поверхностных файлов — это кандидаты в «пробелы».

- [ ] **Step 3: Commit**

```bash
git add docs/learning-plan/diagnostic/02-interview-sizes.tsv
git commit -m "diagnostic(signal-2): collect interview/*.md line counts (171 files)"
```

---

## Task 3: Signal #3 — даты последней правки

**Files:**
- Create: `docs/learning-plan/diagnostic/03-interview-dates.tsv`

- [ ] **Step 1: Собрать дату последнего коммита для каждого файла**

```bash
find cheatsheets/interview -name '*.md' -type f -not -name 'TOC.md' -not -name 'README.md' \
  | while read f; do
      date=$(git log -1 --format='%ai' -- "$f" 2>/dev/null | cut -d' ' -f1)
      rel="${f#cheatsheets/interview/}"
      cat="${rel%%/*}"
      name="${rel#*/}"
      printf "%s\t%s\t%s\t%s\n" "$cat" "$name" "${date:-unknown}" "$f"
    done \
  | sort -k3,3 > docs/learning-plan/diagnostic/03-interview-dates.tsv
wc -l docs/learning-plan/diagnostic/03-interview-dates.tsv
```

Expected: ~171 строка. Заметь — самые старые сверху.

- [ ] **Step 2: Sanity check — самые старые файлы**

```bash
head -20 docs/learning-plan/diagnostic/03-interview-dates.tsv
```

Expected: даты вроде 2024-... — это «давно не трогали».

- [ ] **Step 3: Commit**

```bash
git add docs/learning-plan/diagnostic/03-interview-dates.tsv
git commit -m "diagnostic(signal-3): collect interview/*.md last-modified dates"
```

---

## Task 4: Signal #4 — asymmetry interview vs general

**Files:**
- Create: `docs/learning-plan/diagnostic/04-asymmetry.tsv`

- [ ] **Step 1: Найти пересечение каталогов**

Для каждой подкатегории `cheatsheets/interview/<cat>/` проверить, есть ли соответствие в `cheatsheets/<cat>/` (вне interview/).

```bash
INT_DIR=cheatsheets/interview
GEN_DIR=cheatsheets
{
  printf "interview_category\thas_general\tinterview_files\tgeneral_files\n"
  for cat_dir in "$INT_DIR"/*/; do
    cat=$(basename "$cat_dir")
    int_count=$(find "$cat_dir" -name '*.md' | wc -l | tr -d ' ')
    if [ -d "$GEN_DIR/$cat" ]; then
      gen_count=$(find "$GEN_DIR/$cat" -name '*.md' | wc -l | tr -d ' ')
      printf "%s\tyes\t%s\t%s\n" "$cat" "$int_count" "$gen_count"
    else
      printf "%s\tno\t%s\t0\n" "$cat" "$int_count"
    fi
  done
} > docs/learning-plan/diagnostic/04-asymmetry.tsv
cat docs/learning-plan/diagnostic/04-asymmetry.tsv | column -t -s$'\t'
```

Expected: таблица с 25 категориями. Строки с `has_general=no` или малым `general_files` — это «знаю для собеседования, но не консолидировал для работы» — сигнал пробела.

- [ ] **Step 2: Commit**

```bash
git add docs/learning-plan/diagnostic/04-asymmetry.tsv
git commit -m "diagnostic(signal-4): map interview→general cheatsheet asymmetry"
```

---

## Task 5: Signal #5 — Chrome bookmarks (Profile 3)

**Files:**
- Create: `docs/learning-plan/diagnostic/05-bookmarks.tsv`
- Read: `~/Library/Application Support/Google/Chrome/Profile 3/Bookmarks`

- [ ] **Step 1: Скопировать и распарсить букмарки**

Файл `Bookmarks` — JSON с вложенной структурой папок. Извлекаем плоский список `<folder>\t<title>\t<url>` рекурсивно.

```bash
BMK="$HOME/Library/Application Support/Google/Chrome/Profile 3/Bookmarks"
jq -r '
  def walk(path):
    if .type == "folder" then
      .children[]? | walk(path + " > " + .name)
    elif .type == "url" then
      [path, .name // "", .url // ""] | @tsv
    else empty end;
  .roots
  | to_entries[]
  | .value
  | walk(.name // "root")
' "$BMK" > docs/learning-plan/diagnostic/05-bookmarks.tsv
wc -l docs/learning-plan/diagnostic/05-bookmarks.tsv
```

Expected: несколько сотен строк. Если ноль или ошибка — Chrome держит лок-файл, или JSON-структура изменилась. В этом случае Step 2.

- [ ] **Step 2: Fallback — папки/группы первого уровня**

Если Step 1 не вернул данные:

```bash
jq -r '
  .roots | to_entries[] |
  "=== \(.value.name // .key) ===",
  (.value.children[]? | select(.type=="folder") | "  - \(.name) (\(.children | length))")
' "$BMK"
```

Это покажет крупные папки и их размеры — уже полезный сигнал.

- [ ] **Step 3: Quick stats — топ-папок по количеству ссылок**

```bash
awk -F'\t' '{print $1}' docs/learning-plan/diagnostic/05-bookmarks.tsv | sort | uniq -c | sort -rn | head -30
```

Expected: видишь, в каких папках больше всего букмарок — это сигнал интереса.

- [ ] **Step 4: Commit**

```bash
git add docs/learning-plan/diagnostic/05-bookmarks.tsv
git commit -m "diagnostic(signal-5): extract Chrome Profile 3 bookmarks flat list"
```

---

## Task 6: Signal #6 — заметки в Singularity про учёбу

**Files:**
- Create: `docs/learning-plan/diagnostic/06-singularity-hints.md`

- [ ] **Step 1: Получить полный листинг заметок**

Используй MCP-инструмент `mcp__singularity__listNotes` с `maxCount: 200` (если есть). Ответ сохранится в файл-результат (см. skill singularity-notes).

Если ответ обрезается, есть переменная `$NOTES_FILE` с путём — работай через jq.

- [ ] **Step 2: Отфильтровать learning-related заметки**

```bash
NOTES_FILE="<path-from-step-1>"
jq -r --arg patterns 'учеб|учёб|обуч|курс|книг|читать|изучить|поучить|разобрать|выучить|java|kotlin|spring|kafka|постгрес|postgres|алгоритм|систем|design|архитект|интерв|подгот' '
.notes[]
| select(.removed != true)
| select((.content // "" | ascii_downcase) | test($patterns; "i"))
| "## containerId: \(.containerId)\n\n\(.content)\n\n---\n"
' "$NOTES_FILE" > docs/learning-plan/diagnostic/06-singularity-hints.md
wc -l docs/learning-plan/diagnostic/06-singularity-hints.md
```

Expected: подмножество заметок (10-30) — это явные сигналы намерений учиться.

- [ ] **Step 3: Декодировать Delta-формат в обычный текст**

Если в Step 2 заметки в Delta-JSON формате (начинаются с `[{`), пройтись по ним и декодировать:

```bash
python3 -c '
import json, re, sys
text = open("docs/learning-plan/diagnostic/06-singularity-hints.md").read()
def decode_delta(s):
    s = s.strip()
    if not s.startswith("["): return s
    try:
        ops = json.loads(s)
        return "".join(op.get("insert", "") for op in ops if isinstance(op, dict))
    except: return s
out = []
for block in text.split("---"):
    out.append(re.sub(r"(\[\{.*?\}\])", lambda m: decode_delta(m.group(1)), block, flags=re.S))
open("docs/learning-plan/diagnostic/06-singularity-hints.md","w").write("---".join(out))
'
head -40 docs/learning-plan/diagnostic/06-singularity-hints.md
```

Expected: читаемый текст вместо JSON.

- [ ] **Step 4: Commit**

```bash
git add docs/learning-plan/diagnostic/06-singularity-hints.md
git commit -m "diagnostic(signal-6): extract learning-related Singularity notes"
```

---

## Task 7: Bonus signal — данные quiz-app

**Files:**
- Create: `docs/learning-plan/diagnostic/07-quizapp-data.md`
- Possibly read: SQLite/Postgres БД quiz-app

- [ ] **Step 1: Найти БД и схему**

```bash
find . -name '*.db' -not -path './build/*' -not -path './node_modules/*' 2>/dev/null
find . -name 'V*__*.sql' -path '*migrations*' -not -path './build/*' 2>/dev/null | head -20
```

Expected: путь к SQLite (например `quiz.db` или `quiz-app/data/quiz.db`) и список миграций.

- [ ] **Step 2: Посмотреть схему**

Если есть `.db` файл:

```bash
sqlite3 <path-to-db> ".tables"
sqlite3 <path-to-db> ".schema interview_session"
sqlite3 <path-to-db> ".schema interview_answer"
```

Expected: видишь, есть ли таблицы session/answer и какие колонки (correct_answer, question_id, etc.).

- [ ] **Step 3: Если данные есть — собрать топ слабых тем**

```bash
DB=<path-to-db>
sqlite3 "$DB" "
SELECT q.category, COUNT(*) as wrong_count
FROM interview_answer a
JOIN interview_question q ON a.question_id = q.id
WHERE a.is_correct = 0
GROUP BY q.category
ORDER BY wrong_count DESC
LIMIT 10;
" > docs/learning-plan/diagnostic/07-quizapp-data.md
```

Expected: топ-10 категорий, где пользователь чаще ошибался. **ЭТО САМЫЙ СИЛЬНЫЙ СИГНАЛ.**

- [ ] **Step 4: Если данных нет — задокументируй**

Если БД пустая, или таблиц нет, или сам файл отсутствует:

```bash
cat > docs/learning-plan/diagnostic/07-quizapp-data.md <<'EOF'
# Quiz-app data (signal #7) — NO DATA

quiz-app session/answer data not available (no DB, empty tables, or schema mismatch).
This signal is not used for backlog ranking. To enable in future:
- Run quiz-app, complete a few interview sessions
- Re-run this diagnostic step
EOF
```

- [ ] **Step 5: Commit**

```bash
git add docs/learning-plan/diagnostic/07-quizapp-data.md
git commit -m "diagnostic(signal-7): quiz-app session data (or document absence)"
```

---

## Task 8: Синтез — ранжированные кандидаты

**Files:**
- Create: `docs/learning-plan/diagnostic/08-ranked-candidates.md`
- Read: все `diagnostic/0[1-7]-*.{tsv,md}`

- [ ] **Step 1: Сборка кандидатов**

Это аналитический шаг — не shell, а синтез. Прочитай файлы сигналов 1-7. Для каждой interview-категории (из 01) посчитай:

- **size_score**: процент тонких файлов в категории (из 02). `< 200 lines` → +1.
- **age_score**: процент старых файлов (правка > 12 мес. назад) (из 03). `> 365 days` → +1.
- **asymmetry_score**: если в 04 `has_general=no` ИЛИ `general_files < interview_files / 2` → +2.
- **bookmark_score**: количество букмарок в папках, чьё имя match'ит категорию (из 05) — нормировать до 0-3.
- **intent_score**: упоминания категории в 06 — нормировать до 0-2.
- **quiz_score**: топ-5 категорий из 07 → +3 each.

`total_rank = sum`. Берём топ-12.

Запиши результат в Markdown как таблицу:

```markdown
# Ранжированные кандидаты (signals → score)

| Rank | Тема (interview category или связка) | size | age | asym | bmk | intent | quiz | TOTAL | Evidence-summary |
|------|--------------------------------------|------|-----|------|-----|--------|------|-------|------------------|
| 1 | … | … | … | … | … | … | … | … | … |
```

После таблицы — для каждой темы в топ-12 отдельная секция:

```markdown
## 1. <Theme name>

**Evidence:**
- size: 3 файла из 6 < 200 lines (algorithms/dynamic-programming-interview.md: 180; …)
- bookmarks: 7 ссылок в папке "Algo Practice" в Profile 3
- intent: «Поучить алгоритмы, особенно DP» в Singularity заметке T-xyz

**Why this matters:** [1-2 предложения объяснения]

**Done-when:** [конкретный критерий]

**Suggested sources:**
- свои: `cheatsheets/interview/algorithms/dynamic-programming-interview.md`
- букмарки: список URL-ов
- внешнее: [только то, что встретилось в букмарках]
```

- [ ] **Step 2: Sanity check — есть ли минимум 8 тем с rank ≥ 4**

```bash
grep -c '^##' docs/learning-plan/diagnostic/08-ranked-candidates.md
```

Expected: ≥ 8. Если меньше — спека позволяет расширить пороги (см. секция 6 «Граничные случаи»). Возвращаемся к Step 1 с relaxed thresholds.

- [ ] **Step 3: Commit**

```bash
git add docs/learning-plan/diagnostic/08-ranked-candidates.md
git commit -m "diagnostic(synthesis): rank candidate themes by multi-signal evidence"
```

---

## Task 9: Финальный Markdown-план

**Files:**
- Create: `docs/learning-plan/2026-05-20-self-learning-plan.md`
- Read: `docs/learning-plan/diagnostic/08-ranked-candidates.md` + спека

- [ ] **Step 1: Создать файл с 7 секциями по спеке**

Структура (строго по секции 4 спеки):

```markdown
# План самообучения (2026-05 — 2026-11)

## 1. Контекст
- **Уровень:** senior, пробелы не самоназваны → диагностика
- **Цель:** комбинированная — закрыть пробелы + ритм роста + готовность к интервью
- **Бюджет:** 5 ч/неделю (1 ч × 5 будних дней)
- **Горизонт:** 6 месяцев (~24 темы максимум)
- **Жёсткое правило:** 1 цикл = 1 неделя = 5 часов = 1 тема. Если не уложился — урезаем тему, а не растягиваем.

## 2. Диагностика
### Источники сигналов (snapshot: 2026-05-20)
| Источник | Что взято | Файл |
|---|---|---|
| `interview/TOC.md` | 25 категорий, N подкатегорий | `diagnostic/01-toc-categories.tsv` |
| `interview/*.md` | размеры по строкам (171 файл) | `diagnostic/02-interview-sizes.tsv` |
| `interview/*.md` | даты последней правки | `diagnostic/03-interview-dates.tsv` |
| `cheatsheets/*` | asymmetry interview vs general | `diagnostic/04-asymmetry.tsv` |
| Chrome Profile 3 | букмарки flat list | `diagnostic/05-bookmarks.tsv` |
| Singularity | заметки с learning-intent | `diagnostic/06-singularity-hints.md` |
| quiz-app DB | session-данные (или их отсутствие) | `diagnostic/07-quizapp-data.md` |

### Найденные пробелы (top-N, evidence-driven)
[Скопировать таблицу + per-theme секции из 08-ranked-candidates.md, но компактнее — без всех 6 evidence-полей, только summary]

## 3. Backlog (приоритизированный, 8-10 тем)
Cycle order = rank order. Когда закрыл цикл — берёшь следующий.

### Cycle 1: <Theme>  *(week 1)*
- **Why:** [1-2 предложения с evidence]
- **Done when:** [конкретный критерий — например, cheatsheet расширен до 400+ строк, 20 MCQ сгенерированы и пройдены ≥ 80%]
- **Источники:**
  - Свои шпаргалки: `path/to/file.md` (текущий размер: N строк)
  - Букмарки: [- title (folder) — url, …]
  - Quiz-app модуль: [категория для генерации MCQ]

### Cycle 2: <Theme> *(week 2)*
[…]

[8-10 циклов]

## 4. Формат недельного цикла
Фиксированный 5-дневный паттерн (1 ч/день, всегда):

| День | Что делаем | Артефакт |
|---|---|---|
| Пн | Прочитать свою шпаргалку темы + 2-3 букмарки → выписать holes (несоответствия, незнание, пробелы) | список holes в Singularity note задачи |
| Вт | Закрыть holes — точечное чтение источников | заметки в Singularity note |
| Ср | Hands-on: написать код / нарисовать диаграмму / разобрать кейс | артефакт в `cheatsheets/interview/<theme>/practice-<date>.md` или в quiz-app |
| Чт | Через quiz-app сгенерировать 20 MCQ, прогнать. Цель ≥ 80% correct | session log в quiz-app |
| Пт | Расширить cheatsheet (свои новые понимания), commit + push | git commit с тегом `learning(<theme>)` |

## 5. Singularity integration
- **Проект:** «Учёба» (`P-606c59f5-5404-401d-a674-8660595e84bb`)
- **Таск-группа:** базовая (или новая «Текущий цикл», если решили выделить — см. Task 11 при создании)
- **Шаблон названий:** `[Пн|Вт|Ср|Чт|Пт] <Theme>: <короткое описание дня>`
- **Свойства задач:** `start` = соответствующий день, `useTime: false`, `notifies: [60]`, `priority: 1`, `isNote: false`.
- **В note-поле:** Delta-array со ссылками (свои шпаргалки + URL букмарок + ожидаемый «done»).
- **Правило обновления:** когда цикл закрыт → создать следующие 5 задач из backlog. Не авто-rolling.

## 6. Правила пересмотра
- **Жёлтая карточка:** если цикл не закрыл за 2 недели — урезать тему до её 50% объёма или удалить из backlog. Честность важнее объёма.
- **Месячный re-run:** в конце каждого месяца перезапускать `IMPLEMENTATION_PLAN.md` Tasks 1-8 → обновлять `08-ranked-candidates.md`. Если в топ-12 появились новые темы — добавлять в конец backlog.
- **Закрытая тема ≠ выученная:** через 2-3 месяца возвращаться к закрытым темам в режиме refresh (1 пятница = 1 refresh-сессия).

## 7. Источники
### Свои шпаргалки (по темам backlog)
[Группированный список путей к interview/*.md и cheatsheets/*.md, релевантных backlog'у]

### Chrome bookmarks (по темам backlog)
[Группированный список «тема → URL → краткое описание». Только релевантные.]

### Внешнее (если есть в букмарках)
[Курсы / книги / статьи, которые встретились в букмарках. Не курируем «лучшее в интернете».]
```

- [ ] **Step 2: Sanity check — все 7 секций существуют**

```bash
grep -c '^## [1-7]\.' docs/learning-plan/2026-05-20-self-learning-plan.md
```

Expected: 7.

- [ ] **Step 3: Sanity check — backlog ≥ 8 тем**

```bash
grep -c '^### Cycle' docs/learning-plan/2026-05-20-self-learning-plan.md
```

Expected: ≥ 8.

- [ ] **Step 4: Commit**

```bash
git add docs/learning-plan/2026-05-20-self-learning-plan.md
git commit -m "feat(learning): write self-learning plan (B) from diagnostic evidence"
```

---

## Task 10: Драфт 5 задач для Cycle 1 (для review перед заливкой)

**Files:**
- Create: `docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md`

- [ ] **Step 1: Определить день старта**

Сегодня 2026-05-20 (среда). Cycle 1 стартует с **понедельника 2026-05-25**.

```bash
date -v+5d +'%Y-%m-%d %A'  # macOS — понедельник
```

Expected: `2026-05-25 Monday`.

- [ ] **Step 2: Написать draft 5 задач**

Возьми Cycle 1 из плана (= rank 1 из 08-ranked-candidates). Для каждого дня — title + note body (Delta-array).

```markdown
# Cycle 1 tasks draft — <Theme>

Start date: 2026-05-25 (Monday)
Project: «Учёба» (P-606c59f5-5404-401d-a674-8660595e84bb)
Task group: <id — будет определён в Task 11 Step 1>

## Task 1 — [Пн] <Theme>: прочитать шпаргалку + букмарки, выписать holes
**start:** 2026-05-25
**note (Delta):**
```json
[
  {"insert":"Цели дня:\n"},
  {"insert":"1. Прочитать свою шпаргалку: cheatsheets/interview/<path>.md\n"},
  {"insert":"2. Проглядеть букмарки:\n"},
  {"insert":"  - "}, {"attributes":{"link":"<url>"}, "insert":"<title>"}, {"insert":"\n"},
  {"insert":"3. Выписать holes (что непонятно / противоречит / новое) сюда же ниже.\n\nDone when: список из 5+ holes ниже.\n"}
]
```

[…same pattern for tasks 2-5…]
```

- [ ] **Step 3: Commit draft (для аудита)**

```bash
git add docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md
git commit -m "diagnostic(cycle-1): draft 5 Singularity tasks for first theme"
```

---

## Task 11: Создать 5 Singularity задач через MCP

**Files:**
- Read: `docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md`
- Side effect: Singularity «Учёба» project пополнен 5 задачами

- [ ] **Step 1: Узнать базовую таск-группу проекта «Учёба»**

```
mcp__singularity__listTaskGroups({ projectId: "P-606c59f5-5404-401d-a674-8660595e84bb" })
```

Expected: получили список групп. Берём первую/базовую. Сохраняем `groupId = G-...`.

- [ ] **Step 2: Проверить отсутствие дублей**

```
mcp__singularity__listTasks({ containerId: "P-606c59f5-5404-401d-a674-8660595e84bb", maxCount: 50 })
```

Если уже есть task с title начинающимся на `[Пн] <Theme>` — стоп. Спроси пользователя: skip / overwrite / append.

- [ ] **Step 3: Создать 5 задач**

Для каждого дня (Пн-Пт):

```
mcp__singularity__createTask({
  containerId: "P-606c59f5-5404-401d-a674-8660595e84bb",
  groupId: "<from Step 1>",
  title: "[Пн] <Theme>: прочитать шпаргалку + букмарки, выписать holes",
  start: "2026-05-25",
  useTime: false,
  notify: 1,
  notifies: [60],
  priority: 1,
  isNote: false
})
```

Сохраняем 5 полученных `task.id` для следующего шага.

- [ ] **Step 4: Прикрепить note-content к каждой задаче**

Для каждой созданной задачи:

```
mcp__singularity__createNote({
  containerId: "T-<task-id>",
  content: [
    {"insert":"Цели дня:\n"},
    {"insert":"…(из драфта)…\n"}
  ]
})
```

- [ ] **Step 5: Verify — задачи появились**

```
mcp__singularity__listTasks({ containerId: "P-606c59f5-5404-401d-a674-8660595e84bb", maxCount: 20 })
```

Expected: видишь 5 новых задач с правильными `start`-датами.

- [ ] **Step 6: Записать в план id'ы созданных задач (для трекинга)**

Дописать в конец `docs/learning-plan/2026-05-20-self-learning-plan.md`:

```markdown
## Appendix A — Cycle 1 Singularity tasks
Created 2026-05-20:
- T-<id1> [Пн] <Theme>: …
- T-<id2> [Вт] …
- T-<id3> [Ср] …
- T-<id4> [Чт] …
- T-<id5> [Пт] …
```

```bash
git add docs/learning-plan/2026-05-20-self-learning-plan.md
git commit -m "feat(learning): record cycle 1 task IDs in plan appendix"
```

---

## Task 12: Финальная проверка и summary

- [ ] **Step 1: Проверить, что все артефакты на месте**

```bash
ls -la docs/learning-plan/
ls -la docs/learning-plan/diagnostic/
```

Expected:
- `IMPLEMENTATION_PLAN.md` (этот файл)
- `2026-05-20-self-learning-plan.md` (главный артефакт)
- `diagnostic/` с 9 файлами (01-08 + 10)

- [ ] **Step 2: Проверить git состояние**

```bash
git log --oneline | head -15
```

Expected: 9-10 коммитов с префиксами `diagnostic(...)`, `feat(learning)`.

- [ ] **Step 3: Summary пользователю**

Сказать в чат:
- Сколько тем в backlog
- Какая первая (Cycle 1) и когда стартует
- Что лежит в diagnostic/ как evidence
- Что 5 задач уже созданы в Singularity, со start-датами

---

## Self-Review (заметки автора плана)

**Spec coverage check:**
- ✅ Спека §3 (методология диагностики, 6 сигналов) → Tasks 1-7
- ✅ Спека §4 (структура plan-документа, 7 секций) → Task 9
- ✅ Спека §5 (Singularity integration, 5 задач/неделя, GMT+3, notifies, priority) → Tasks 10-11
- ✅ Спека §6 (граничные случаи: пустая БД, мало кандидатов) → Tasks 7 Step 4, Task 8 Step 2
- ✅ Спека §7 (4 артефакта) → Tasks 9-11 + этот файл

**Placeholder check:**
- Cycle 1 theme name остаётся `<Theme>` до выполнения Task 8 — это намеренно (определяется evidence-rank'ом, не предугадываем). Не placeholder.
- Размеры файлов, точные подсчёты — определяются runtime'ом, разумно.

**Type consistency:**
- `containerId` всегда `P-…` или `T-…`, согласовано.
- `start` всегда string `YYYY-MM-DD`, согласовано.
- Project ID `P-606c59f5-5404-401d-a674-8660595e84bb` использован одинаково в Tasks 6, 11.

**Risks not covered in plan:**
- Если quiz-app DB действительно есть и в ней десятки тысяч записей — Task 7 Step 3 может быть медленным. Маловероятно для personal-app.
- Если Chrome букмарок > 1000 — Task 5 Step 1 даст огромный TSV. Все равно решаем через aggregations в Task 8, должно быть OK.

---

# ADDENDUM — Pivot 2026-05-21: master-checklist model

**Status of original plan:** Tasks 1-9 — выполнены. **Tasks 10-11 — SUPERSEDED.** Task 12 — будет выполнена после A-G ниже.

**Trigger:** пользователь пересмотрел Singularity-модель. Вместо 5 задач/неделя → один master-чеклист в существующей T-84507082 + Habit + переименование recurring. Полный backlog расширяется с 9 до ~67 циклов в 22 phases. Спека §9 описывает изменения.

**Файлы, затрагиваемые этим addendum'ом:**
- Modify: `docs/learning-plan/2026-05-20-self-learning-plan.md` (Section 3: с 9 циклов → 67; Section 5: переписать под master-checklist)
- Create: `docs/learning-plan/diagnostic/11-roadmap-delta-draft.md` (draft Delta для T-84507082)
- Modify: Singularity T-84507082 (note content)
- Modify: Singularity T-f2cb5669 (title rename) — опционально
- Create: Singularity Habit (новая)
- Modify: `docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md` (mark OBSOLETE)

## Task A: Расширить main plan markdown с 9 до 67 циклов

**Files:**
- Modify: `docs/learning-plan/2026-05-20-self-learning-plan.md` (sections 3 + 5)

- [ ] **Step A.1: Заменить Section 3 (Backlog) на полные 67 циклов в 22 phases**

Текущее (9 циклов) → заменить на структуру из спеки §9.4 / brainstorm-секции 2. Список phases и циклов:

```
Phase 1 — Priority (диагностика) [9]
  Cycle 1 — System Design & Highload (DDIA)
  Cycle 2 — Algorithms — Data Structures I (Trees focus)
  Cycle 3 — AI/LLM Foundations
  Cycle 4 — Kubernetes deep
  Cycle 5 — Data Engineering — Kafka Streams
  Cycle 6 — Go Concurrency
  Cycle 7 — Reactive Streams (Reactor + RxJava)
  Cycle 8 — Database Architecture deep
  Cycle 9 — Spring Cloud / Microservices
Phase 2 — Algorithms coverage [3]
  Cycle 10 — Algorithmic Paradigms (DP, Backtracking, Greedy, D&C)
  Cycle 11 — Sorting & Searching + Big-O
  Cycle 12 — Data Structures II (Graphs, Hash, Heaps, Tries, LL, Stacks)
Phase 3 — Java Core deep [4]
  Cycle 13 — Java Concurrency deep
  Cycle 14 — Java Collections + Streams API
  Cycle 15 — Java Modern (Optional, CompletableFuture, Records, Sealed)
  Cycle 16 — Java Memory Model + I/O / NIO
Phase 4 — Kotlin [2]
  Cycle 17 — Kotlin Language
  Cycle 18 — Kotlin Coroutines + Flow
Phase 5 — Go [1]
  Cycle 19 — Go fundamentals + testing
Phase 6 — Spring deep [4]
  Cycle 20 — Spring Core (IoC, AOP, Beans)
  Cycle 21 — Spring Data (JPA, R2DBC)
  Cycle 22 — Spring Security
  Cycle 23 — Spring Batch / Modulith / WebFlux
Phase 7 — Databases deep [4]
  Cycle 24 — PostgreSQL deep
  Cycle 25 — SQL Patterns
  Cycle 26 — NoSQL: MongoDB + Cassandra
  Cycle 27 — Redis + Elasticsearch + Hibernate caching
Phase 8 — Architecture [4]
  Cycle 28 — Hexagonal / Clean / DDD
  Cycle 29 — CQRS / Event Sourcing / Saga
  Cycle 30 — Resilience Patterns
  Cycle 31 — Networking + API Gateway + LB
Phase 9 — Data Engineering coverage [3]
  Cycle 32 — Apache Spark
  Cycle 33 — Apache Flink
  Cycle 34 — Data Lake / Lakehouse
Phase 10 — Messaging & Search [2]
  Cycle 35 — Kafka core
  Cycle 36 — Messaging ecosystem (Redpanda, RabbitMQ, Solr)
Phase 11 — API [3]
  Cycle 37 — REST + HTTP + REST Maturity
  Cycle 38 — GraphQL + gRPC
  Cycle 39 — API Versioning + OpenAPI
Phase 12 — Cloud [1]
  Cycle 40 — AWS Core
Phase 13 — DevOps & Containers [3]
  Cycle 41 — Docker / Containers
  Cycle 42 — CI/CD (Jenkins, GitLab CI, GHA)
  Cycle 43 — Service Mesh + GitOps
Phase 14 — Observability [3]
  Cycle 44 — Metrics (Prometheus, Grafana, VM)
  Cycle 45 — Logging (ELK, Loki)
  Cycle 46 — Tracing & APM (OpenTelemetry, Jaeger)
Phase 15 — Security [3]
  Cycle 47 — OWASP Top 10 + AppSec
  Cycle 48 — AuthN/Z (OAuth2, OIDC, JWT)
  Cycle 49 — TLS/SSL + Zero Trust
Phase 16 — Testing [3]
  Cycle 50 — Test Strategies
  Cycle 51 — Unit + Integration (JUnit, Mockito)
  Cycle 52 — E2E + Performance + Chaos
Phase 17 — Performance [2]
  Cycle 53 — JVM tuning & profiling
  Cycle 54 — App performance (DB, caching, network)
Phase 18 — JVM + Alt Frameworks [2]
  Cycle 55 — JVM internals + GraalVM Native
  Cycle 56 — Micronaut & Quarkus
Phase 19 — Code Quality + Patterns [2]
  Cycle 57 — Design Patterns (GoF)
  Cycle 58 — Refactoring + SOLID + Code Review
Phase 20 — Behavioral & Leadership [2]
  Cycle 59 — Behavioral STAR
  Cycle 60 — Leadership topics
Phase 21 — Trainers (hands-on) [5]
  Cycle 61 — SQL Trainer (LeetCode SQL)
  Cycle 62 — Git Trainer (rebase, bisect, reflog)
  Cycle 63 — Docker Trainer
  Cycle 64 — Kubernetes Trainer (kind/minikube)
  Cycle 65 — DSA Visualizer (VisuAlgo + Algorithm Visualizer)
Phase 22 — Misc [2]
  Cycle 66 — Interview preparation meta
  Cycle 67 — Cheat-sheet maintenance & meta-skills
```

Для **первых 9 циклов** (Phase 1) сохранить детальную секцию с Why/Done-when/Sources, которая уже есть в markdown'е. Для **Phase 2-22** — компактная запись по 1 циклу = 1 строка title (без Why/Done/Sources, чтобы документ оставался читаемым).

Sources: ссылки на свои шпаргалки и букмарки — добавить compact mapping в конце Section 3, не per cycle.

- [ ] **Step A.2: Заменить Section 5 (Singularity integration)**

Старый текст про «5 задач/неделя» → новый текст про master-чеклист:

```markdown
## 5. Singularity integration (новая модель, см. spec §9)

**Master Roadmap:** существующая задача-страница T-84507082 «План профессионального развития» (project «Учёба»). Полный Delta-чеклист = 67 циклов × 5 micro-task с фиксированным pattern в шапке.

**Recurring time slot:** T-f2cb5669 (Пн-Пт 17:00 1h) переименован в «Учёба: текущий цикл (см. План профразвития)». Содержимого нет.

**Habit:** «1ч учёбы сегодня», Пн-Пт, deepPurple.

**Что НЕ в Singularity:** источники (URL букмарок, пути файлов), evidence, DDIA-главы — в этом markdown-файле.

**Workflow:**
1. Открыть T-84507082 → найти активный цикл → читать какой next.
2. Сделать 1 час → отметить чек-бокс в master.
3. Отметить Habit за день.
4. По завершении цикла — следующий из backlog.
```

- [ ] **Step A.3: Verify**

```bash
grep -c '^### Cycle' docs/learning-plan/2026-05-20-self-learning-plan.md
```

Expected: ≥ 9 (детальные секции для Phase 1). Полный список циклов (1-67) присутствует, но в компактном формате внутри Phase 2-22.

```bash
grep -c '^Phase ' docs/learning-plan/2026-05-20-self-learning-plan.md
```

Expected: 22.

- [ ] **Step A.4: Commit**

```bash
git add docs/learning-plan/2026-05-20-self-learning-plan.md
git commit -m "feat(learning): expand backlog to 67 cycles in 22 phases; rewrite Singularity section under master-checklist model"
```

## Task B: Сгенерировать Roadmap-Delta draft

**Files:**
- Create: `docs/learning-plan/diagnostic/11-roadmap-delta-draft.md`

- [ ] **Step B.1: Написать Delta-JSON для master-страницы**

Полный Delta-array. Структура:

```
[
  // === Header ===
  {"insert":"План профессионального развития\n", "attributes":{"bold": true}},
  {"insert":"\n"},
  {"insert":"Бюджет: 5 ч/нед (1 ч × Пн-Пт). 67 циклов × 5 actions. ~16 мес чистого времени.\n"},
  {"insert":"Детали в repo: docs/learning-plan/2026-05-20-self-learning-plan.md\n\n"},
  
  // === Pattern ===
  {"insert":"Pattern (одинаково для каждого цикла):\n", "attributes":{"bold": true}},
  {"insert":"  1. Прочитать свою шпаргалку\n"},
  {"insert":"  2. Просмотреть закладки → выписать holes\n"},
  {"insert":"  3. Закрыть holes — точечное чтение\n"},
  {"insert":"  4. Hands-on (код / диаграмма / writeup)\n"},
  {"insert":"  5. 20 MCQ через quiz-app ≥80% + commit cheatsheet\n\n"},

  // === Phase 1 ===
  {"insert":"━━━ Phase 1 — Priority (диагностика) ━━━\n", "attributes":{"bold": true}},
  
  {"insert":"\nCycle 1 — System Design & Highload (DDIA)\n", "attributes":{"bold": true}},
  {"insert":"шпаргалка"}, {"insert":"\n","attributes":{"list":"unchecked"}},
  {"insert":"закладки + holes"}, {"insert":"\n","attributes":{"list":"unchecked"}},
  {"insert":"закрыть holes"}, {"insert":"\n","attributes":{"list":"unchecked"}},
  {"insert":"hands-on"}, {"insert":"\n","attributes":{"list":"unchecked"}},
  {"insert":"MCQ + commit"}, {"insert":"\n","attributes":{"list":"unchecked"}},

  // ...повторить для Cycle 2-67 с одинаковыми 5 чекбоксами...
  
  {"insert":"\n━━━ Phase 22 — Misc ━━━\n", "attributes":{"bold": true}},
  {"insert":"\nCycle 67 — Cheat-sheet maintenance & meta-skills\n", "attributes":{"bold": true}},
  {"insert":"шпаргалка"}, {"insert":"\n","attributes":{"list":"unchecked"}},
  // ... ещё 4 ...
]
```

Файл `11-roadmap-delta-draft.md` содержит:
1. Markdown-описание формата (для пользователя)
2. JSON-блок с полным Delta (для машинной заливки)

**Размер ожидаем:** ~67 × ~12 операций = ~800 операций. JSON ~30-50 КБ. Singularity Delta вмещает.

- [ ] **Step B.2: Sanity check — операции корректны**

```bash
jq '. | length' < (jq '.delta' docs/learning-plan/diagnostic/11-roadmap-delta-draft.md)
```

Expected: ≥ 800.

Или просто визуально: первые / последние 20 строк draft файла.

- [ ] **Step B.3: Commit**

```bash
git add docs/learning-plan/diagnostic/11-roadmap-delta-draft.md
git commit -m "diagnostic(roadmap): draft Delta for T-84507082 master-checklist (67 cycles, 22 phases)"
```

## Task C: Показать draft пользователю

- [ ] **Step C.1: Показать в чат краткое summary**

В сообщении:
- Путь к draft (`docs/learning-plan/diagnostic/11-roadmap-delta-draft.md`)
- Количество циклов / phases / чек-боксов
- Примеры из 2-3 циклов (как выглядит структура)

- [ ] **Step C.2: AskUserQuestion**

```
question: "Готов залить Roadmap в T-84507082?"
options:
  - Да, залей как есть
  - Покажи 5 циклов более подробно — хочу посмотреть формат
  - Хочу что-то поменять (опишу следующим сообщением)
  - Не заливай в Singularity сейчас (оставь только в repo)
```

Если ответ ≠ «Да» → останавливаемся, обсуждаем, возможно возвращаемся к Task B.

## Task D: Залить Delta в T-84507082 через updateNote

**Files:**
- Modify: Singularity note `N-T-84507082-aa58-4a6c-b5e7-af3df685f100`

- [ ] **Step D.1: Извлечь Delta из draft в строку**

```bash
# Извлечь JSON блок из markdown-файла и подготовить как одну JSON-строку для updateNote
# updateNote ожидает .content как string (массив сериализован в JSON-string)
DRAFT=docs/learning-plan/diagnostic/11-roadmap-delta-draft.md
jq -r '.delta | tostring' "$DRAFT" > /tmp/roadmap-delta.json
wc -c /tmp/roadmap-delta.json
```

Expected: 30-50 КБ.

- [ ] **Step D.2: Вызвать updateNote**

```
mcp__singularity__updateNote({
  note: {
    id: "N-T-84507082-aa58-4a6c-b5e7-af3df685f100",
    containerId: "T-84507082-aa58-4a6c-b5e7-af3df685f100",
    content: "<содержимое /tmp/roadmap-delta.json>"
  }
})
```

- [ ] **Step D.3: Verify**

```
mcp__singularity__getNote({ id: "N-T-84507082-aa58-4a6c-b5e7-af3df685f100" })
```

Expected: `.content` теперь содержит большой Delta-array, не `[{"insert":"\n"}]`.

## Task E: Переименовать T-f2cb5669 (опционально)

**Files:**
- Modify: Singularity task `T-f2cb5669-13f9-4d1a-a98c-68a121de9a6d`

- [ ] **Step E.1: Подтверждение через AskUserQuestion**

```
question: "Переименовать recurring задачу 'Почитать шпаргалки и разобрать план подготовки' в 'Учёба: текущий цикл'?"
options:
  - Да, переименуй
  - Не трогай — оставь как есть
  - Другое название (предложи)
```

- [ ] **Step E.2: Если "Да" — загрузить updateTask tool через ToolSearch**

```
ToolSearch({ query: "select:mcp__singularity__updateTask", max_results: 1 })
```

- [ ] **Step E.3: Вызвать updateTask**

```
mcp__singularity__updateTask({
  task: {
    id: "T-f2cb5669-13f9-4d1a-a98c-68a121de9a6d",
    title: "Учёба: текущий цикл (см. План профразвития)"
  }
})
```

Note: recurrence-поля не трогаем, только title.

## Task F: Создать Habit «1ч учёбы сегодня»

- [ ] **Step F.1: Загрузить createHabit tool**

```
ToolSearch({ query: "select:mcp__singularity__createHabit", max_results: 1 })
```

- [ ] **Step F.2: Вызвать createHabit**

```
mcp__singularity__createHabit({
  habit: {
    title: "Учусь сегодня (1ч)",
    color: "deepPurple",
    status: 0
  }
})
```

(Опц. поля типа repeat-days, если поддерживаются — daily Пн-Пт. Если в схеме нет — оставить базовую daily, пользователь подстроит в UI.)

- [ ] **Step F.3: Verify**

```
mcp__singularity__listHabits()
```

Expected: новая habit в списке.

## Task G: Пометить obsolete старый draft

**Files:**
- Modify: `docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md`

- [ ] **Step G.1: Добавить OBSOLETE-заголовок в самое начало файла**

```bash
sed -i.bak '1i\
> ⚠️ OBSOLETE 2026-05-21. Старая модель (5 задач/неделя) отменена.\
> Актуальная Roadmap-схема — в docs/learning-plan/diagnostic/11-roadmap-delta-draft.md\
> и в Singularity T-84507082.\
\
' docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md
rm docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md.bak
head -5 docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md
```

Expected: первые строки — OBSOLETE marker.

- [ ] **Step G.2: Commit**

```bash
git add docs/learning-plan/diagnostic/10-cycle-1-tasks-draft.md
git commit -m "diagnostic(cleanup): mark cycle-1 tasks draft as OBSOLETE (model pivot)"
```

## Task H: Финальная проверка (заменяет старую Task 12)

- [ ] **Step H.1: Проверить все артефакты**

```bash
ls -la docs/learning-plan/
ls -la docs/learning-plan/diagnostic/
```

Expected:
- `IMPLEMENTATION_PLAN.md` (этот файл)
- `2026-05-20-self-learning-plan.md` (67 циклов, обновлённая Section 5)
- `diagnostic/` с файлами 01-08 + 10 (OBSOLETE) + 11 (Roadmap Delta draft)

- [ ] **Step H.2: Проверить git log**

```bash
git log --oneline | head -20
```

Expected: 14-15 коммитов, включая addendum-коммиты (`expand backlog`, `roadmap draft`, `cleanup obsolete`).

- [ ] **Step H.3: Проверить Singularity состояние**

```
mcp__singularity__getNote({ id: "N-T-84507082-aa58-4a6c-b5e7-af3df685f100" })
# содержит большой Delta

mcp__singularity__getTask({ id: "T-f2cb5669-13f9-4d1a-a98c-68a121de9a6d" })
# title переименован (если Task E принят)

mcp__singularity__listHabits()
# содержит "Учусь сегодня"
```

- [ ] **Step H.4: Summary пользователю**

Сообщение в чат:
- Сколько циклов в backlog (67)
- Cycle 1 = System Design + DDIA
- T-84507082 теперь содержит весь Roadmap; Habit активирован
- Repo: `2026-05-20-self-learning-plan.md` = детальный план
- Health cleanup — следующая сессия

---

## Addendum Self-Review

**Spec §9 coverage:**
- ✅ §9.2 архитектура → Tasks A-F покрывают: master (D), recurring rename (E), Habit (F), legacy не трогаем (no task), repo md (A)
- ✅ §9.3 pattern → Task B Step B.1 жёстко закладывает pattern в Delta header
- ✅ §9.4 backlog 67 циклов → Task A Step A.1 + Task B
- ✅ §9.5 анти-паттерны → отражено в self-review: не создаём new tasks, pattern один раз, источники не дублируем
- ✅ §9.6 updates существующих → Tasks A, G + spec уже обновлена

**Placeholder check:**
- Task B Step B.1 показывает первый цикл и последний с placeholder `// ...повторить для Cycle 2-67...` — это полностью объяснимая компрессия (паттерн идентичен, я не пишу 67 одинаковых блоков в плане; engineer экстраполирует). НЕ placeholder в смысле «придумать что писать».
- Task F Step F.2 — `(Опц. поля типа repeat-days, если поддерживаются)`. Это **является** placeholder — нужно либо знать схему createHabit, либо иметь явное fallback. **Mitigation**: после Step F.1 (ToolSearch) увидим схему и заполним точно.

**Type consistency:**
- `T-84507082-aa58-4a6c-b5e7-af3df685f100` — везде одинаково.
- `N-T-84507082-aa58-4a6c-b5e7-af3df685f100` — note id согласован.
- `T-f2cb5669-13f9-4d1a-a98c-68a121de9a6d` — везде одинаково.
- Delta-format: `{"insert": "...", "attributes": {...}}` — согласовано.

**Risks:**
- updateNote с очень большим content (50 КБ) может упасть. Mitigation: если упадёт — попробовать разбить (хотя note это единое поле, разбить не получится; альтернатива — отказаться от 67 циклов и сжать до 30).
- updateTask с recurrence-полями может затереть recurrence. Mitigation: в Task E передаём только title, схема MCP должна оставить recurrence нетронутым (verify в Step H.3).
- createHabit схема неизвестна точно — Task F Step F.1 загружает её, потом заполняем.
