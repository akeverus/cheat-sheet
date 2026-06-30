# PLAN_INTERVIEW — качество MCQ JSON-сидеров (Option Parity / Structural Parity)

Бесконечный `/loop 15m` цикл (cron `ae1e19ff`). Цель: правильный вариант в MCQ
**нельзя угадать по форме** — длине, структуре, насыщенности, стилю. Лечим не
сокращением correct, а **поднятием distractor-ов** до его уровня (плюс перенос
лишних деталей correct в `sections`, которые рендерятся после ответа).

> ## 🔄 RESET #1 — 2026-07-01 (Plausibility Parity)
>
> Добавлена вторая линза качества — **Plausibility Parity**: дистрактор должен быть не только
> сопоставим по длине/структуре, но и **правдоподобен** (реалистичная ошибочная модель middle/senior,
> а не раздутая карикатура с токсичными/категоричными маркерами — анти-паттерн **Inflated Caricature
> Distractor**). Бар расширился → прогресс round 2 (Option Parity) в таблице §7 **сброшен**: все
> статусы → `QUEUED`, файлы переоткрыты под объединённый критерий. Коммиты round 2 остаются в
> git-истории (не теряются).
>
> ## 🔄 RESET #2 — 2026-07-01 (Human-Readability)
>
> Добавлена **третья линза** — **человекочитаемость русской прозы** (`scripts/audit-mcq-parity.py --readability`,
> commit 3fc4745d): сигналы `LOW_CYRILLIC` / `ENGLISH_RUN` / `LONG_SENTENCE` / `FILLER_PHRASE` / `PUNCT`
> на `text` + прозаических `sections`. Калибровано адверс-выборкой (3-агентный workflow судил FP-rate) на
> высокую точность: корпус легитимно code-switch'ит, поэтому язык-сигнал требует англ. ПРОЗЫ со служебными+
> содержательными словами, а не цепочки имён. По §7 добавлена колонка **RDB** (независимая ось, не в Σ).
> Статусы таблицы снова **сброшены** в `QUEUED`. Все коммиты round 2/3 остаются в git-истории.

## 1. Правило (закреплено в skills)

| Skill | Где | Что закреплено |
|---|---|---|
| `mcq-quality-fixer` (канон) | `### Option Parity (MANDATORY — the strongest rule)` | Бар по длине/форме/claim-count/structure-mirror; «raise distractors, don't shrink correct»; **Structural-signature tell** (correct не единственный с `→`/`:`/`;`/backtick-перечислением); **Cross-block consistency** (correct не систематически длиннейший — цель ~25%, не ~90%) |
| `interview-writer` | секция MCQ-quality-rules (~line 314) | «Option Parity (обязательно)» + ссылка на канон при MCQ-sync |
| `interview-options-writer` (deprecated) | `### Length-ratio cap` (~line 631) | Синхронизировано для legacy-аудита; для нового контента НЕ использовать |

## 2. Признаки «угадываемого» correct (что ищем)

- `LEN_AVG` — len(correct) / avg(len(wrong)) > 1.3;
- `LEN_SPREAD` — max(len опций) / min(len опций) > 1.5;
- `UNIQ_MARKER` — correct **единственный** несёт `→` / «X: a; b» / `;` / ≥2 `backtick` / шаг-список;
- `COMMA_GAP` — у correct заметно больше запятых/смысловых частей;
- `SHORT_DISTR` — есть wrong короче 60% длины correct (заглушка);
- `CORRECT_LONGEST_RATE` (file-level) — доля блоков, где correct самый длинный; >50% = файл угадывается «самый длинный = правильный».

**Plausibility-линза (round 3, 2026-07-01):**
- `CARICATURE` — дистрактор несёт маркеры токсичности/абсурда/категоричности (группы: absolute / toxic_management / absurd_action / dismissive / fake_reasoning) — отбрасывается по тону, а не по знанию;
- `INFLATED_CARICATURE` — дистрактор раздут до длины/формы correct, но всё равно карикатурен (главный новый кейс);
- технич. плотность: `TECH_DENSITY_GAP` / `BACKTICK_GAP` / `NUMBER_GAP` / `STRUCTURE_DENSITY_GAP` / `WORD_COUNT_GAP` / `SENTENCE_COUNT_GAP` — correct заметно «технически насыщеннее»;
- структурная симметрия: `ONLY_ONE_SEQUENCE_OPTION` / `ONLY_ONE_ALGORITHM_OPTION` / `ONLY_ONE_ENUMERATION_OPTION` / `ONLY_ONE_CAUSAL_OPTION` — форму несёт ровно один вариант;
- severity блока: `LOW` / `MEDIUM` / `HIGH` / `CRITICAL` (`scripts/audit-mcq-parity.py`, `--json-report` / `--markdown-report` / `--fail-on`).

## 3. Как чинить (направление важно)

НЕ сокращать correct до уровня заглушек. **Поднимать distractor-ы**: каждый wrong —
правдоподобная ОШИБОЧНАЯ модель той же формы (своя неверная `→`-цепочка / шаг-список /
сравнение ролей), не заглушка. Лишние детали correct → в его `sections`
(`explanation`/`example`/`edge_cases` — рендер после ответа). Ровно 1 `correct: true`,
факты 1:1, дистрактор остаётся ложным (сверять с `.md`).

Пример для встречи-структуры (как mentoring Q2):
| Опция | Модель |
|---|---|
| A | одна структура встречи, но НЕВЕРНАЯ роль владельца повестки |
| B | другая структура, но неверная частота и цель |
| C | верная частота, но неверное распределение блоков |
| D | правильная структура (correct) |

## 4. Тулчейн

| Инструмент | Назначение |
|---|---|
| `scripts/audit-mcq-parity.py` | Аудит-скрипт: находит кандидатов (per-block + file-level). `--top N`, `-v`. Решение за человеком |
| `scratchpad/goal_loop/diagnose.py` | Строгий per-block детектор (LENGTH/SURFACE/THIN/DUP) → tells=0 |
| `scratchpad/goal_loop/gate_struct.py` | HEAD-vs-current: q_number/question_text/order/label/correct неизменны |
| `scratchpad/goal_loop/apply_patch.py` | Безопасный ассемблер (меняет ТОЛЬКО text/sections) |
| `scratchpad/goal_loop/wf_mcq_chunked.js` + `harvest_chunked.py` + `gate_commit.py` | Chunked-воркфлоу + сбор вердиктов + гейт коммита |
| `scripts/verify-mcq-json.sh` | Schema-валидатор |

## 5. Инварианты (НЕЛЬЗЯ нарушать)

1. Правим ТОЛЬКО `seed/mcq/**/*.json` (НЕ `.md` — pre-commit банит MCQ-маркеры).
2. Никогда не менять `correct`/`label`/`order`/`q_number`/`question_text` — только `text`/`sections`.
3. Поле `correct` (НЕ `is_correct`). JSON: `ensure_ascii=False, indent=2`, без trailing-newline.
4. Ровно 1 `correct: true` на блок. Факты 1:1 (перенос в sections, не потеря).
5. Explicit pathspec при коммите. ≤3 параллельных субагента (≥10 → rate-limit).
6. Не трогать файлы параллельной сессии: `agentic-patterns`, `git`, `chaos-engineering`.

## 6. Рецепт итерации

1. `audit-mcq-parity.py --top N` → worst-first кандидаты.
2. editor-агент на файл (раскрывает distractor-ы, убирает stubs, даёт им свою ложную структуру; лишнее correct → sections).
3. Гейты: `diagnose` (tells=0) → `gate_struct` (PASS) → `verify-mcq-json` (OK) → cross-section dup (0) → `audit-mcq-parity` (UNIQ/SHORT/LEN сняты, longest≤~45%).
4. Адверс-ревьюер (fidelity_ok + parity_ok).
5. Атомарный per-file коммит: `pedago(mcq): <topic> — single-delta паритет опций (N блоков)`.

## 7. Реестр всех сидеров — детальный re-аудит (round 3, audit v2, СБРОС статусов)

Всего сидеров: **318**. Источник: `scripts/audit-mcq-parity.py` (audit v2, re-аудит 2026-07-01).
Сортировка worst→best: severity файла → число CRITICAL-блоков → Σфлагов. **Все статусы сброшены в `QUEUED`** —
улучшение видно по данным (низкие Σ/severity = файл уже дорабатывался в round 2, см. git-историю), а не по статусу.

**Колонки:**

| Колонка | Смысл |
|---|---|
| **Бл.** | блоков (MCQ) в файле |
| **Sev** | severity файла = худший блок (`CRIT`/`HIGH`/`MED`/`LOW`) |
| **C/H/M/L** | сколько блоков каждого уровня severity (CRITICAL/HIGH/MEDIUM/LOW) |
| **long%** | доля блоков, где correct самый длинный (⚠ >50% = «длиннейший = правильный») |
| **tech%** | доля блоков, где correct самый технически плотный (⚠ >50%) |
| **ustr%** | доля блоков, где correct единственный со структурным маркером (→/;/backtick-enum) |
| **rank** | средний ранг длины correct (0…1; >0.5 = в среднем длиннее дистракторов) |
| **LEN** | блоков с length-tell (LEN_AVG/LEN_SPREAD/WORD/SENTENCE) |
| **STR** | блоков со structure-tell (UNIQ_MARKER/ONLY_ONE_*/STRUCTURE_DENSITY) |
| **DEN** | блоков с tech-density-tell (TECH/BACKTICK/NUMBER/COMMA gap) |
| **CAR** | блоков с маркерами карикатуры (Plausibility) |
| **INF** | блоков с `INFLATED_CARICATURE` (раздут до формы correct, но карикатурен) |
| **SHRT** | блоков с короткими-заглушками (SHORT_DISTR) |
| **RDB** | блоков с проблемой человекочитаемости русской прозы (`--readability`); маркер severity 🟥HIGH / 🟧MEDIUM / 🟨LOW. Независимая ось — НЕ входит в Σ |
| **Σ** | всего блоков-флагов parity-оси в файле |
| **Статус** | `QUEUED` (сброшено) · по мере правки → `R3✅ <commit>` |

<!-- Сводка round3 re-аудит (audit v2 + readability, 2026-07-01, СБРОС #2): файлов=318;
severity файлов CRITICAL/HIGH/MEDIUM/LOW=297/11/5/2; блоков-флагов(parity)=9311;
по группам LEN=8463 STR=7838 DEN=7587 CAR=2109 INF=92 SHRT=7874; readability RDB=19 (файлов H/M/L=4/9/3);
⚠long>50%=292, ⚠tech>50%=314.
Честно: под объединённым Option+Plausibility баром почти весь корпус = CRITICAL (correct выделяется по длине+структуре+
плотности одновременно). CAR/INF снизились vs прошлый аудит (2197→2109 / 140→92) — это round-3 caricature-коммиты.
Readability — отдельная линза (русская проза): всего 19 блоков-кандидатов, хотспот — behavioral/failure-stories (🟥4). -->

| # | Кат. | Сидер | Бл. | Sev | C/H/M/L | long% | tech% | ustr% | rank | LEN | STR | DEN | CAR | INF | SHRT | RDB | Σ | Статус |
|--:|---|---|--:|:--:|:--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|:--:|
| 1 | databases | cassandra-interview | 44 | CRIT | 44/0/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 44 | 44 | 44 | 8 | 0 | 44 | 0 | 44 | QUEUED |
| 2 | frameworks | spring-mvc-interview | 43 | CRIT | 43/0/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 43 | 43 | 43 | 8 | 0 | 43 | 0 | 43 | QUEUED |
| 3 | testing | contract-testing-interview | 42 | CRIT | 42/0/0/0 | 100%⚠ | 100%⚠ | 98% | 1.00 | 42 | 42 | 42 | 5 | 0 | 42 | 0 | 42 | QUEUED |
| 4 | databases | redis-interview | 43 | CRIT | 41/2/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 43 | 43 | 41 | 6 | 0 | 43 | 0 | 43 | QUEUED |
| 5 | architecture | saga-pattern-interview | 43 | CRIT | 40/3/0/0 | 100%⚠ | 98%⚠ | 100% | 1.00 | 43 | 43 | 40 | 7 | 0 | 43 | 0 | 43 | QUEUED |
| 6 | performance | performance-testing-interview | 42 | CRIT | 40/2/0/0 | 100%⚠ | 100%⚠ | 95% | 1.00 | 42 | 41 | 42 | 6 | 0 | 42 | 🟨1 | 42 | QUEUED |
| 7 | frameworks | spring-batch-interview | 43 | CRIT | 38/5/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 43 | 40 | 41 | 10 | 0 | 43 | 0 | 43 | QUEUED |
| 8 | security | jwt-interview | 43 | CRIT | 38/5/0/0 | 100%⚠ | 100%⚠ | 81% | 1.00 | 43 | 40 | 43 | 14 | 0 | 43 | 0 | 43 | QUEUED |
| 9 | architecture | networking-interview | 43 | CRIT | 37/6/0/0 | 100%⚠ | 100%⚠ | 84% | 1.00 | 43 | 38 | 43 | 11 | 0 | 43 | 0 | 43 | QUEUED |
| 10 | devops | argocd-interview | 42 | CRIT | 37/5/0/0 | 100%⚠ | 100%⚠ | 81% | 1.00 | 42 | 41 | 42 | 11 | 0 | 42 | 0 | 42 | QUEUED |
| 11 | system-design | system-design-interview | 41 | CRIT | 37/4/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 41 | 39 | 41 | 24 | 0 | 38 | 0 | 41 | QUEUED |
| 12 | reactive | project-reactor-interview | 48 | CRIT | 36/11/0/0 | 98%⚠ | 98%⚠ | 77% | 0.98 | 47 | 38 | 44 | 5 | 0 | 47 | 0 | 47 | QUEUED |
| 13 | security | owasp-top10-interview | 45 | CRIT | 36/9/0/0 | 100%⚠ | 100%⚠ | 76% | 1.00 | 45 | 41 | 45 | 7 | 0 | 45 | 0 | 45 | QUEUED |
| 14 | testing | test-strategies-interview | 45 | CRIT | 36/9/0/0 | 100%⚠ | 100%⚠ | 82% | 1.00 | 45 | 37 | 42 | 5 | 0 | 44 | 0 | 45 | QUEUED |
| 15 | testing | unit-testing-interview | 45 | CRIT | 36/9/0/0 | 100%⚠ | 100%⚠ | 78% | 1.00 | 45 | 41 | 42 | 5 | 0 | 45 | 0 | 45 | QUEUED |
| 16 | architecture | caching-strategies-interview | 42 | CRIT | 36/6/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 42 | 40 | 41 | 16 | 0 | 42 | 0 | 42 | QUEUED |
| 17 | frameworks | spring-data-jpa-interview | 43 | CRIT | 36/6/0/0 | 98%⚠ | 98%⚠ | 84% | 0.99 | 42 | 38 | 42 | 12 | 0 | 42 | 0 | 42 | QUEUED |
| 18 | programming-languages | scala-interview | 40 | CRIT | 36/4/0/0 | 100%⚠ | 100%⚠ | 80% | 1.00 | 40 | 37 | 40 | 8 | 0 | 40 | 0 | 40 | QUEUED |
| 19 | programming-languages | java-oop-interview | 43 | CRIT | 35/8/0/0 | 100%⚠ | 98%⚠ | 81% | 1.00 | 43 | 40 | 38 | 8 | 0 | 43 | 0 | 43 | QUEUED |
| 20 | programming-languages | kotlin-collections-interview | 43 | CRIT | 35/8/0/0 | 100%⚠ | 93%⚠ | 95% | 1.00 | 43 | 42 | 36 | 13 | 0 | 42 | 0 | 43 | QUEUED |
| 21 | programming-languages | java-17-21-interview | 42 | CRIT | 35/7/0/0 | 100%⚠ | 95%⚠ | 88% | 1.00 | 42 | 39 | 39 | 5 | 0 | 42 | 0 | 42 | QUEUED |
| 22 | security | authentication-authorization-patterns-interview | 45 | CRIT | 34/11/0/0 | 100%⚠ | 100%⚠ | 76% | 1.00 | 45 | 37 | 45 | 10 | 0 | 45 | 0 | 45 | QUEUED |
| 23 | frameworks | spring-boot-actuator-interview | 43 | CRIT | 34/9/0/0 | 100%⚠ | 100%⚠ | 84% | 1.00 | 43 | 38 | 42 | 7 | 0 | 43 | 0 | 43 | QUEUED |
| 24 | ai-ml | fine-tuning-llm-interview | 35 | CRIT | 34/1/0/0 | 100%⚠ | 100%⚠ | 97% | 1.00 | 35 | 34 | 35 | 11 | 0 | 35 | 0 | 35 | QUEUED |
| 25 | architecture | hexagonal-architecture-interview | 45 | CRIT | 33/12/0/0 | 100%⚠ | 91%⚠ | 87% | 1.00 | 45 | 40 | 36 | 4 | 0 | 45 | 0 | 45 | QUEUED |
| 26 | frameworks | spring-cloud-interview | 43 | CRIT | 33/10/0/0 | 100%⚠ | 100%⚠ | 74% | 1.00 | 43 | 36 | 43 | 4 | 0 | 43 | 0 | 43 | QUEUED |
| 27 | architecture | scalability-patterns-interview | 41 | CRIT | 33/8/0/0 | 100%⚠ | 100%⚠ | 83% | 1.00 | 41 | 37 | 39 | 17 | 0 | 41 | 0 | 41 | QUEUED |
| 28 | leadership | code-review-practices-interview | 40 | CRIT | 33/7/0/0 | 100%⚠ | 100%⚠ | 75% | 1.00 | 40 | 33 | 40 | 12 | 0 | 40 | 0 | 40 | QUEUED |
| 29 | programming-languages | kotlin-dsl-interview | 40 | CRIT | 33/7/0/0 | 100%⚠ | 100%⚠ | 82% | 1.00 | 40 | 37 | 39 | 5 | 0 | 40 | 0 | 40 | QUEUED |
| 30 | performance | memory-management-interview | 39 | CRIT | 33/6/0/0 | 100%⚠ | 97%⚠ | 87% | 1.00 | 39 | 36 | 37 | 11 | 0 | 39 | 0 | 39 | QUEUED |
| 31 | api | websocket-interview | 38 | CRIT | 33/5/0/0 | 100%⚠ | 100%⚠ | 87% | 1.00 | 38 | 33 | 38 | 4 | 0 | 37 | 0 | 38 | QUEUED |
| 32 | cicd | pipeline-design-interview | 38 | CRIT | 33/5/0/0 | 100%⚠ | 97%⚠ | 87% | 1.00 | 38 | 34 | 37 | 18 | 0 | 38 | 0 | 38 | QUEUED |
| 33 | programming-languages | kotlin-interop-java-interview | 38 | CRIT | 33/5/0/0 | 100%⚠ | 100%⚠ | 95% | 1.00 | 38 | 37 | 34 | 7 | 0 | 38 | 0 | 38 | QUEUED |
| 34 | databases | elasticsearch-interview | 44 | CRIT | 32/12/0/0 | 100%⚠ | 98%⚠ | 77% | 1.00 | 44 | 40 | 42 | 8 | 0 | 44 | 0 | 44 | QUEUED |
| 35 | databases | database-transactions-interview | 42 | CRIT | 32/10/0/0 | 100%⚠ | 100%⚠ | 76% | 1.00 | 42 | 34 | 40 | 5 | 0 | 42 | 0 | 42 | QUEUED |
| 36 | devops | terraform-interview | 42 | CRIT | 32/10/0/0 | 100%⚠ | 100%⚠ | 81% | 1.00 | 42 | 36 | 38 | 7 | 0 | 42 | 0 | 42 | QUEUED |
| 37 | architecture | cqrs-event-sourcing-interview | 41 | CRIT | 32/9/0/0 | 100%⚠ | 95%⚠ | 95% | 1.00 | 41 | 40 | 39 | 17 | 0 | 41 | 0 | 41 | QUEUED |
| 38 | logging | logging-interview | 42 | CRIT | 32/9/0/0 | 98%⚠ | 98%⚠ | 79% | 0.98 | 41 | 37 | 39 | 3 | 0 | 40 | 0 | 41 | QUEUED |
| 39 | testing | integration-testing-interview | 40 | CRIT | 32/8/0/0 | 100%⚠ | 100%⚠ | 82% | 1.00 | 40 | 37 | 40 | 11 | 0 | 40 | 0 | 40 | QUEUED |
| 40 | monitoring | logging-strategies-interview | 38 | CRIT | 32/6/0/0 | 100%⚠ | 97%⚠ | 87% | 1.00 | 38 | 36 | 37 | 1 | 0 | 38 | 0 | 38 | QUEUED |
| 41 | algorithms | trees-interview | 34 | CRIT | 32/2/0/0 | 100%⚠ | 100%⚠ | 94% | 1.00 | 34 | 32 | 33 | 13 | 0 | 34 | 0 | 34 | QUEUED |
| 42 | databases | database-sharding-interview | 34 | CRIT | 32/2/0/0 | 100%⚠ | 100%⚠ | 91% | 1.00 | 34 | 33 | 34 | 11 | 0 | 34 | 0 | 34 | QUEUED |
| 43 | programming-languages | java-collections-interview | 46 | CRIT | 31/15/0/0 | 100%⚠ | 98%⚠ | 72% | 1.00 | 46 | 42 | 43 | 14 | 0 | 46 | 0 | 46 | QUEUED |
| 44 | frameworks | spring-security-interview | 46 | CRIT | 31/12/0/0 | 96%⚠ | 94%⚠ | 67% | 0.98 | 43 | 33 | 42 | 7 | 0 | 43 | 0 | 43 | QUEUED |
| 45 | frameworks | spring-framework-interview | 41 | CRIT | 31/9/0/0 | 98%⚠ | 98%⚠ | 85% | 0.98 | 40 | 36 | 37 | 3 | 0 | 40 | 0 | 40 | QUEUED |
| 46 | cicd | deployment-strategies-interview | 39 | CRIT | 31/8/0/0 | 100%⚠ | 97%⚠ | 82% | 1.00 | 39 | 35 | 35 | 8 | 0 | 39 | 0 | 39 | QUEUED |
| 47 | performance | jvm-performance-tuning-interview | 38 | CRIT | 31/7/0/0 | 100%⚠ | 100%⚠ | 55% | 1.00 | 38 | 34 | 38 | 17 | 0 | 38 | 0 | 38 | QUEUED |
| 48 | programming-languages | java-modules-interview | 38 | CRIT | 31/7/0/0 | 100%⚠ | 97%⚠ | 92% | 1.00 | 38 | 35 | 33 | 2 | 0 | 38 | 0 | 38 | QUEUED |
| 49 | ai-ml | ai-compliance-governance-interview | 32 | CRIT | 31/1/0/0 | 100%⚠ | 100%⚠ | 97% | 1.00 | 32 | 31 | 32 | 5 | 0 | 32 | 0 | 32 | QUEUED |
| 50 | api | openapi-swagger-interview | 42 | CRIT | 30/12/0/0 | 100%⚠ | 100%⚠ | 86% | 1.00 | 42 | 37 | 35 | 4 | 0 | 42 | 0 | 42 | QUEUED |
| 51 | architecture | microservices-interview | 42 | CRIT | 30/12/0/0 | 100%⚠ | 95%⚠ | 86% | 1.00 | 42 | 38 | 35 | 12 | 0 | 42 | 🟧1 | 42 | QUEUED |
| 52 | databases | flyway-liquibase-interview | 42 | CRIT | 30/12/0/0 | 100%⚠ | 90%⚠ | 83% | 1.00 | 42 | 40 | 39 | 6 | 0 | 42 | 0 | 42 | QUEUED |
| 53 | performance | application-profiling-interview | 42 | CRIT | 30/12/0/0 | 100%⚠ | 98%⚠ | 81% | 1.00 | 42 | 37 | 37 | 10 | 0 | 42 | 0 | 42 | QUEUED |
| 54 | programming-languages | java-serialization-interview | 40 | CRIT | 30/10/0/0 | 100%⚠ | 95%⚠ | 80% | 1.00 | 40 | 38 | 34 | 4 | 0 | 39 | 0 | 40 | QUEUED |
| 55 | security | tls-ssl-interview | 45 | CRIT | 29/16/0/0 | 100%⚠ | 98%⚠ | 64% | 1.00 | 45 | 36 | 42 | 9 | 0 | 45 | 0 | 45 | QUEUED |
| 56 | ai-ml | open-source-llms-interview | 34 | CRIT | 29/5/0/0 | 100%⚠ | 100%⚠ | 85% | 1.00 | 34 | 31 | 33 | 4 | 0 | 34 | 0 | 34 | QUEUED |
| 57 | algorithms | hash-tables-interview | 34 | CRIT | 29/5/0/0 | 100%⚠ | 100%⚠ | 82% | 1.00 | 34 | 29 | 34 | 11 | 0 | 34 | 0 | 34 | QUEUED |
| 58 | algorithms | dynamic-programming-interview | 33 | CRIT | 29/4/0/0 | 100%⚠ | 97%⚠ | 91% | 1.00 | 33 | 31 | 31 | 13 | 0 | 33 | 0 | 33 | QUEUED |
| 59 | ai-ml | ai-application-architecture-interview | 32 | CRIT | 29/3/0/0 | 100%⚠ | 97%⚠ | 94% | 1.00 | 32 | 31 | 32 | 12 | 0 | 32 | 0 | 32 | QUEUED |
| 60 | system-design | design-feed-system-interview | 30 | CRIT | 29/1/0/0 | 100%⚠ | 100%⚠ | 97% | 1.00 | 30 | 30 | 30 | 3 | 0 | 30 | 0 | 30 | QUEUED |
| 61 | system-design | design-rate-limiter-interview | 30 | CRIT | 29/1/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 30 | 29 | 30 | 5 | 0 | 30 | 0 | 30 | QUEUED |
| 62 | databases | mongodb-interview | 46 | CRIT | 28/18/0/0 | 100%⚠ | 98%⚠ | 63% | 1.00 | 46 | 33 | 43 | 17 | 0 | 46 | 0 | 46 | QUEUED |
| 63 | reactive | rxjava-interview | 46 | CRIT | 28/18/0/0 | 100%⚠ | 89%⚠ | 85% | 1.00 | 46 | 39 | 34 | 9 | 0 | 46 | 0 | 46 | QUEUED |
| 64 | architecture | resilience-patterns-interview | 43 | CRIT | 28/15/0/0 | 100%⚠ | 100%⚠ | 67% | 1.00 | 43 | 34 | 41 | 5 | 0 | 43 | 0 | 43 | QUEUED |
| 65 | devops | helm-interview | 43 | CRIT | 28/15/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 43 | 32 | 43 | 8 | 0 | 43 | 0 | 43 | QUEUED |
| 66 | databases | database-architecture-interview | 41 | CRIT | 28/13/0/0 | 100%⚠ | 93%⚠ | 93% | 1.00 | 41 | 39 | 30 | 15 | 0 | 41 | 0 | 41 | QUEUED |
| 67 | ai-ml | inference-optimization-interview | 36 | CRIT | 28/8/0/0 | 100%⚠ | 100%⚠ | 78% | 1.00 | 36 | 33 | 36 | 5 | 0 | 35 | 0 | 36 | QUEUED |
| 68 | ai-ml | long-context-vs-rag-interview | 30 | CRIT | 28/2/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 30 | 29 | 30 | 11 | 0 | 30 | 0 | 30 | QUEUED |
| 69 | system-design | design-netflix-interview | 30 | CRIT | 28/2/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 30 | 28 | 29 | 4 | 0 | 30 | 0 | 30 | QUEUED |
| 70 | system-design | design-pastebin-interview | 30 | CRIT | 28/2/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 30 | 30 | 30 | 10 | 0 | 30 | 0 | 30 | QUEUED |
| 71 | system-design | design-payment-system-interview | 30 | CRIT | 28/2/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 30 | 28 | 30 | 5 | 0 | 30 | 🟥1 | 30 | QUEUED |
| 72 | programming-languages | java-8-interview | 42 | CRIT | 27/14/1/0 | 98%⚠ | 98%⚠ | 69% | 0.99 | 42 | 34 | 39 | 17 | 0 | 41 | 0 | 42 | QUEUED |
| 73 | programming-languages | java-core-interview | 39 | CRIT | 27/12/0/0 | 100%⚠ | 97%⚠ | 95% | 1.00 | 39 | 38 | 28 | 7 | 0 | 33 | 0 | 39 | QUEUED |
| 74 | architecture | api-gateway-interview | 38 | CRIT | 27/11/0/0 | 100%⚠ | 100%⚠ | 74% | 1.00 | 38 | 31 | 37 | 8 | 0 | 38 | 0 | 38 | QUEUED |
| 75 | algorithms | arrays-strings-interview | 36 | CRIT | 27/9/0/0 | 100%⚠ | 97%⚠ | 81% | 1.00 | 36 | 32 | 33 | 10 | 0 | 36 | 0 | 36 | QUEUED |
| 76 | programming-languages | go-interview | 36 | CRIT | 27/9/0/0 | 100%⚠ | 100%⚠ | 72% | 1.00 | 36 | 32 | 35 | 7 | 0 | 36 | 0 | 36 | QUEUED |
| 77 | ai-ml | llm-evaluation-interview | 30 | CRIT | 27/3/0/0 | 100%⚠ | 97%⚠ | 87% | 1.00 | 30 | 29 | 28 | 10 | 0 | 30 | 0 | 30 | QUEUED |
| 78 | system-design | design-key-value-store-interview | 30 | CRIT | 27/3/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 30 | 27 | 30 | 9 | 0 | 30 | 0 | 30 | QUEUED |
| 79 | ai-ml | mlops-interview | 28 | CRIT | 27/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 28 | 27 | 28 | 7 | 0 | 28 | 0 | 28 | QUEUED |
| 80 | system-design | design-youtube-interview | 28 | CRIT | 27/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 28 | 27 | 28 | 6 | 0 | 28 | 🟧1 | 28 | QUEUED |
| 81 | algorithms | divide-and-conquer-interview | 27 | CRIT | 27/0/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 27 | 27 | 27 | 14 | 0 | 27 | 0 | 27 | QUEUED |
| 82 | system-design | design-instagram-interview | 27 | CRIT | 27/0/0/0 | 100%⚠ | 100%⚠ | 89% | 1.00 | 27 | 27 | 27 | 11 | 0 | 27 | 0 | 27 | QUEUED |
| 83 | security | application-security-interview | 45 | CRIT | 26/19/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 45 | 39 | 41 | 6 | 0 | 45 | 0 | 45 | QUEUED |
| 84 | data-engineering | apache-spark-interview | 35 | CRIT | 26/9/0/0 | 100%⚠ | 100%⚠ | 69% | 1.00 | 35 | 31 | 35 | 14 | 0 | 35 | 0 | 35 | QUEUED |
| 85 | algorithms | searching-algorithms-interview | 31 | CRIT | 26/5/0/0 | 100%⚠ | 100%⚠ | 84% | 1.00 | 31 | 29 | 31 | 10 | 0 | 31 | 0 | 31 | QUEUED |
| 86 | ai-ml | mcp-interview | 30 | CRIT | 26/4/0/0 | 100%⚠ | 100%⚠ | 87% | 1.00 | 30 | 26 | 30 | 8 | 0 | 30 | 🟧1 | 30 | QUEUED |
| 87 | architecture | service-discovery-interview | 30 | CRIT | 26/4/0/0 | 100%⚠ | 100%⚠ | 83% | 1.00 | 30 | 26 | 30 | 8 | 0 | 30 | 0 | 30 | QUEUED |
| 88 | system-design | design-url-shortener-interview | 30 | CRIT | 26/4/0/0 | 100%⚠ | 100%⚠ | 83% | 1.00 | 30 | 27 | 30 | 2 | 0 | 30 | 0 | 30 | QUEUED |
| 89 | ai-ml | ai-observability-interview | 28 | CRIT | 26/2/0/0 | 100%⚠ | 100%⚠ | 89% | 1.00 | 28 | 26 | 28 | 6 | 0 | 28 | 0 | 28 | QUEUED |
| 90 | data-engineering | data-lake-lakehouse-interview | 28 | CRIT | 26/2/0/0 | 100%⚠ | 100%⚠ | 89% | 1.00 | 28 | 28 | 28 | 2 | 0 | 28 | 0 | 28 | QUEUED |
| 91 | programming-languages | kotlin-serialization-interview | 43 | CRIT | 25/18/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 43 | 32 | 39 | 9 | 0 | 43 | 0 | 43 | QUEUED |
| 92 | monitoring | metrics-tracing-interview | 41 | CRIT | 25/16/0/0 | 100%⚠ | 85%⚠ | 63% | 1.00 | 41 | 31 | 31 | 8 | 0 | 41 | 0 | 41 | QUEUED |
| 93 | architecture | load-balancing-interview | 40 | CRIT | 25/15/0/0 | 100%⚠ | 92%⚠ | 65% | 1.00 | 40 | 30 | 33 | 11 | 0 | 40 | 0 | 40 | QUEUED |
| 94 | ai-ml | multimodal-ai-interview | 31 | CRIT | 25/6/0/0 | 100%⚠ | 97%⚠ | 84% | 1.00 | 31 | 28 | 29 | 10 | 0 | 31 | 0 | 31 | QUEUED |
| 95 | databases | database-replication-interview | 31 | CRIT | 25/6/0/0 | 100%⚠ | 100%⚠ | 81% | 1.00 | 31 | 28 | 30 | 14 | 0 | 31 | 0 | 31 | QUEUED |
| 96 | frameworks | quarkus-interview | 31 | CRIT | 25/6/0/0 | 100%⚠ | 97%⚠ | 87% | 1.00 | 31 | 28 | 31 | 5 | 0 | 31 | 0 | 31 | QUEUED |
| 97 | ai-ml | embeddings-interview | 29 | CRIT | 25/4/0/0 | 100%⚠ | 93%⚠ | 93% | 1.00 | 29 | 27 | 26 | 14 | 0 | 28 | 0 | 29 | QUEUED |
| 98 | ai-ml | llm-integration-patterns-interview | 28 | CRIT | 25/3/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 28 | 26 | 26 | 8 | 0 | 28 | 0 | 28 | QUEUED |
| 99 | data-engineering | kafka-streams-interview | 28 | CRIT | 25/3/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 28 | 27 | 27 | 4 | 0 | 28 | 0 | 28 | QUEUED |
| 100 | monitoring | opentelemetry-interview | 28 | CRIT | 25/3/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 28 | 26 | 28 | 9 | 0 | 28 | 0 | 28 | QUEUED |
| 101 | system-design | design-parking-lot-oo-interview | 28 | CRIT | 25/3/0/0 | 100%⚠ | 100%⚠ | 89% | 1.00 | 28 | 26 | 28 | 8 | 0 | 28 | 0 | 28 | QUEUED |
| 102 | monitoring | elk-stack-interview | 26 | CRIT | 25/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 26 | 26 | 26 | 4 | 0 | 26 | 0 | 26 | QUEUED |
| 103 | devops | ansible-interview | 25 | CRIT | 25/0/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 25 | 25 | 25 | 8 | 0 | 25 | 🟧1 | 25 | QUEUED |
| 104 | programming-languages | java-types-interview | 38 | CRIT | 24/14/0/0 | 100%⚠ | 95%⚠ | 71% | 1.00 | 38 | 31 | 33 | 10 | 0 | 38 | 0 | 38 | QUEUED |
| 105 | algorithms | linked-lists-interview | 32 | CRIT | 24/7/0/0 | 100%⚠ | 91%⚠ | 88% | 1.00 | 31 | 29 | 25 | 1 | 0 | 31 | 0 | 31 | QUEUED |
| 106 | data-engineering | apache-flink-interview | 31 | CRIT | 24/7/0/0 | 100%⚠ | 100%⚠ | 77% | 1.00 | 31 | 25 | 31 | 8 | 0 | 31 | 0 | 31 | QUEUED |
| 107 | architecture | cdn-interview | 30 | CRIT | 24/4/1/0 | 100%⚠ | 100%⚠ | 70% | 1.00 | 28 | 25 | 28 | 9 | 1 | 27 | 0 | 29 | QUEUED |
| 108 | ai-ml | ai-agents-interview | 28 | CRIT | 24/4/0/0 | 100%⚠ | 100%⚠ | 86% | 1.00 | 28 | 25 | 27 | 9 | 0 | 28 | 0 | 28 | QUEUED |
| 109 | ai-ml | model-serving-interview | 28 | CRIT | 24/4/0/0 | 100%⚠ | 100%⚠ | 86% | 1.00 | 28 | 27 | 28 | 8 | 0 | 28 | 0 | 28 | QUEUED |
| 110 | cloud | serverless-interview | 28 | CRIT | 24/4/0/0 | 100%⚠ | 96%⚠ | 82% | 1.00 | 28 | 24 | 28 | 7 | 0 | 28 | 0 | 28 | QUEUED |
| 111 | code-quality | clean-code-practices-interview | 27 | CRIT | 24/3/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 27 | 27 | 26 | 7 | 0 | 27 | 0 | 27 | QUEUED |
| 112 | system-design | design-elevator-oo-interview | 26 | CRIT | 24/2/0/0 | 100%⚠ | 100%⚠ | 92% | 1.00 | 26 | 24 | 26 | 3 | 0 | 26 | 0 | 26 | QUEUED |
| 113 | architecture | clean-architecture-interview | 41 | CRIT | 23/18/0/0 | 100%⚠ | 90%⚠ | 66% | 1.00 | 41 | 29 | 34 | 5 | 0 | 41 | 0 | 41 | QUEUED |
| 114 | api | grpc-interview | 40 | CRIT | 23/13/4/0 | 100%⚠ | 90%⚠ | 65% | 1.00 | 39 | 35 | 31 | 3 | 0 | 32 | 0 | 40 | QUEUED |
| 115 | programming-languages | kotlin-coroutines-interview | 39 | CRIT | 23/16/0/0 | 100%⚠ | 92%⚠ | 80% | 1.00 | 39 | 34 | 27 | 15 | 0 | 37 | 0 | 39 | QUEUED |
| 116 | algorithms | complexity-analysis-interview | 31 | CRIT | 23/8/0/0 | 100%⚠ | 100%⚠ | 74% | 1.00 | 31 | 24 | 31 | 27 | 0 | 31 | 0 | 31 | QUEUED |
| 117 | algorithms | sorting-algorithms-interview | 31 | CRIT | 23/8/0/0 | 100%⚠ | 100%⚠ | 74% | 1.00 | 31 | 28 | 30 | 13 | 0 | 31 | 0 | 31 | QUEUED |
| 118 | databases | dynamodb-interview | 30 | CRIT | 23/7/0/0 | 100%⚠ | 100%⚠ | 77% | 1.00 | 30 | 23 | 29 | 5 | 0 | 30 | 0 | 30 | QUEUED |
| 119 | algorithms | heaps-interview | 29 | CRIT | 23/6/0/0 | 100%⚠ | 97%⚠ | 90% | 1.00 | 29 | 29 | 27 | 15 | 0 | 29 | 0 | 29 | QUEUED |
| 120 | devops | istio-service-mesh-interview | 26 | CRIT | 23/3/0/0 | 100%⚠ | 100%⚠ | 85% | 1.00 | 26 | 24 | 26 | 3 | 0 | 26 | 0 | 26 | QUEUED |
| 121 | databases | cockroachdb-interview | 24 | CRIT | 23/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 24 | 24 | 24 | 7 | 0 | 24 | 0 | 24 | QUEUED |
| 122 | performance | network-performance-interview | 24 | CRIT | 23/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 24 | 24 | 24 | 4 | 0 | 24 | 0 | 24 | QUEUED |
| 123 | system-design | design-vending-machine-oo-interview | 24 | CRIT | 23/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 24 | 24 | 24 | 5 | 0 | 24 | 0 | 24 | QUEUED |
| 124 | performance | caching-performance-interview | 23 | CRIT | 23/0/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 23 | 23 | 23 | 13 | 0 | 23 | 0 | 23 | QUEUED |
| 125 | security | oauth2-interview | 42 | CRIT | 22/20/0/0 | 100%⚠ | 100%⚠ | 57% | 1.00 | 42 | 29 | 40 | 6 | 0 | 42 | 0 | 42 | QUEUED |
| 126 | programming-languages | java-io-nio-interview | 40 | CRIT | 22/18/0/0 | 100%⚠ | 92%⚠ | 72% | 1.00 | 40 | 32 | 28 | 8 | 0 | 40 | 0 | 40 | QUEUED |
| 127 | devops | gradle-maven-interview | 38 | CRIT | 22/16/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 38 | 30 | 36 | 7 | 0 | 38 | 0 | 38 | QUEUED |
| 128 | ai-ml | multi-agent-orchestration-interview | 30 | CRIT | 22/8/0/0 | 100%⚠ | 100%⚠ | 70% | 1.00 | 30 | 29 | 30 | 11 | 0 | 30 | 0 | 30 | QUEUED |
| 129 | programming-languages | go-testing-interview | 28 | CRIT | 22/6/0/0 | 100%⚠ | 96%⚠ | 75% | 1.00 | 28 | 26 | 27 | 6 | 0 | 28 | 0 | 28 | QUEUED |
| 130 | system-design | design-twitter-interview | 27 | CRIT | 22/5/0/0 | 100%⚠ | 100%⚠ | 70% | 1.00 | 27 | 25 | 27 | 3 | 0 | 27 | 🟧1 | 27 | QUEUED |
| 131 | databases | postgresql-interview | 55 | CRIT | 21/33/1/0 | 100%⚠ | 91%⚠ | 74% | 1.00 | 55 | 42 | 29 | 5 | 0 | 52 | 0 | 55 | QUEUED |
| 132 | programming-languages | kotlin-exceptions-interview | 40 | CRIT | 21/18/1/0 | 100%⚠ | 88%⚠ | 75% | 1.00 | 40 | 36 | 28 | 11 | 0 | 39 | 0 | 40 | QUEUED |
| 133 | programming-languages | java-jackson-interview | 31 | CRIT | 21/10/0/0 | 100%⚠ | 100%⚠ | 71% | 1.00 | 31 | 28 | 29 | 7 | 0 | 31 | 0 | 31 | QUEUED |
| 134 | data-engineering | dbt-interview | 28 | CRIT | 21/7/0/0 | 100%⚠ | 100%⚠ | 79% | 1.00 | 28 | 23 | 26 | 4 | 0 | 28 | 0 | 28 | QUEUED |
| 135 | data-engineering | stream-processing-interview | 28 | CRIT | 21/7/0/0 | 100%⚠ | 96%⚠ | 86% | 1.00 | 28 | 25 | 24 | 8 | 0 | 28 | 0 | 28 | QUEUED |
| 136 | devops | vault-interview | 26 | CRIT | 21/5/0/0 | 100%⚠ | 96%⚠ | 88% | 1.00 | 26 | 23 | 24 | 2 | 0 | 26 | 0 | 26 | QUEUED |
| 137 | architecture | latency-numbers-interview | 24 | CRIT | 21/3/0/0 | 100%⚠ | 100%⚠ | 88% | 1.00 | 24 | 21 | 24 | 9 | 0 | 22 | 0 | 24 | QUEUED |
| 138 | databases | scylladb-interview | 23 | CRIT | 21/2/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 23 | 22 | 22 | 0 | 0 | 23 | 0 | 23 | QUEUED |
| 139 | security | secrets-management-interview | 22 | CRIT | 21/1/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 22 | 21 | 22 | 5 | 0 | 22 | 0 | 22 | QUEUED |
| 140 | testing | load-testing-interview | 22 | CRIT | 21/1/0/0 | 100%⚠ | 100%⚠ | 91% | 1.00 | 22 | 21 | 22 | 7 | 0 | 22 | 0 | 22 | QUEUED |
| 141 | system-design | design-chat-system-interview | 21 | CRIT | 21/0/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 21 | 21 | 21 | 0 | 0 | 21 | 0 | 21 | QUEUED |
| 142 | programming-languages | java-generics-interview | 40 | CRIT | 20/20/0/0 | 100%⚠ | 100%⚠ | 55% | 1.00 | 40 | 32 | 35 | 8 | 0 | 40 | 0 | 40 | QUEUED |
| 143 | programming-languages | go-concurrency-interview | 35 | CRIT | 20/15/0/0 | 100%⚠ | 86%⚠ | 69% | 1.00 | 35 | 28 | 25 | 8 | 0 | 34 | 0 | 35 | QUEUED |
| 144 | databases | neo4j-interview | 30 | CRIT | 20/10/0/0 | 100%⚠ | 90%⚠ | 77% | 1.00 | 30 | 27 | 24 | 7 | 0 | 30 | 0 | 30 | QUEUED |
| 145 | algorithms | greedy-algorithms-interview | 28 | CRIT | 20/8/0/0 | 100%⚠ | 89%⚠ | 89% | 1.00 | 28 | 26 | 22 | 12 | 0 | 28 | 0 | 28 | QUEUED |
| 146 | algorithms | tries-interview | 28 | CRIT | 20/8/0/0 | 100%⚠ | 100%⚠ | 75% | 1.00 | 28 | 27 | 26 | 3 | 0 | 28 | 0 | 28 | QUEUED |
| 147 | security | supply-chain-security-interview | 24 | CRIT | 20/4/0/0 | 100%⚠ | 96%⚠ | 88% | 1.00 | 24 | 23 | 24 | 2 | 0 | 24 | 0 | 24 | QUEUED |
| 148 | architecture | cap-theorem-interview | 42 | CRIT | 19/23/0/0 | 100%⚠ | 83%⚠ | 60% | 1.00 | 42 | 32 | 26 | 16 | 0 | 41 | 0 | 42 | QUEUED |
| 149 | architecture | distributed-systems-interview | 40 | CRIT | 19/21/0/0 | 100%⚠ | 92%⚠ | 75% | 1.00 | 40 | 33 | 27 | 12 | 0 | 40 | 0 | 40 | QUEUED |
| 150 | leadership | team-leadership-interview | 40 | CRIT | 19/21/0/0 | 100%⚠ | 92%⚠ | 60% | 1.00 | 40 | 30 | 34 | 4 | 0 | 40 | 0 | 40 | QUEUED |
| 151 | ai-ml | reasoning-models-interview | 30 | CRIT | 19/10/1/0 | 100%⚠ | 100%⚠ | 67% | 1.00 | 30 | 23 | 28 | 13 | 0 | 28 | 0 | 30 | QUEUED |
| 152 | algorithms | backtracking-interview | 27 | CRIT | 19/8/0/0 | 100%⚠ | 89%⚠ | 74% | 1.00 | 27 | 24 | 24 | 10 | 0 | 27 | 0 | 27 | QUEUED |
| 153 | messaging | message-brokers-comparison-interview | 26 | CRIT | 19/7/0/0 | 100%⚠ | 100%⚠ | 73% | 1.00 | 26 | 20 | 26 | 3 | 0 | 26 | 0 | 26 | QUEUED |
| 154 | devops | consul-interview | 24 | CRIT | 19/5/0/0 | 100%⚠ | 100%⚠ | 83% | 1.00 | 24 | 21 | 21 | 1 | 0 | 24 | 0 | 24 | QUEUED |
| 155 | messaging | nats-interview | 24 | CRIT | 19/5/0/0 | 100%⚠ | 100%⚠ | 79% | 1.00 | 24 | 22 | 23 | 3 | 0 | 24 | 0 | 24 | QUEUED |
| 156 | testing | testcontainers-interview | 40 | CRIT | 18/22/0/0 | 100%⚠ | 95%⚠ | 52% | 1.00 | 40 | 31 | 36 | 11 | 0 | 38 | 0 | 40 | QUEUED |
| 157 | messaging | rabbitmq-interview | 41 | CRIT | 18/16/4/0 | 100%⚠ | 95%⚠ | 63% | 1.00 | 35 | 27 | 26 | 4 | 0 | 22 | 0 | 38 | QUEUED |
| 158 | leadership | technical-decisions-interview | 22 | CRIT | 18/4/0/0 | 100%⚠ | 86%⚠ | 91% | 1.00 | 22 | 22 | 22 | 3 | 0 | 22 | 0 | 22 | QUEUED |
| 159 | security | mtls-interview | 20 | CRIT | 18/2/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 20 | 20 | 18 | 4 | 0 | 20 | 0 | 20 | QUEUED |
| 160 | architecture | consistency-patterns-interview | 42 | CRIT | 17/25/0/0 | 100%⚠ | 100%⚠ | 74% | 1.00 | 42 | 36 | 22 | 16 | 0 | 41 | 0 | 42 | QUEUED |
| 161 | api | http-rest-interview | 43 | CRIT | 17/19/4/0 | 95%⚠ | 79%⚠ | 60% | 0.96 | 35 | 34 | 23 | 9 | 1 | 27 | 0 | 40 | QUEUED |
| 162 | code-quality | technical-debt-interview | 40 | CRIT | 17/23/0/0 | 100%⚠ | 78%⚠ | 85% | 1.00 | 40 | 37 | 26 | 12 | 0 | 40 | 0 | 40 | QUEUED |
| 163 | programming-languages | go-stdlib-interview | 30 | CRIT | 17/13/0/0 | 100%⚠ | 97%⚠ | 60% | 1.00 | 30 | 23 | 25 | 7 | 0 | 30 | 0 | 30 | QUEUED |
| 164 | ai-ml | prompt-engineering-interview | 28 | CRIT | 17/10/1/0 | 100%⚠ | 93%⚠ | 75% | 1.00 | 28 | 25 | 21 | 10 | 0 | 27 | 0 | 28 | QUEUED |
| 165 | ai-ml | vector-databases-interview | 28 | CRIT | 17/10/1/0 | 100%⚠ | 100%⚠ | 61% | 1.00 | 28 | 19 | 25 | 8 | 0 | 24 | 0 | 28 | QUEUED |
| 166 | performance | database-performance-interview | 27 | CRIT | 17/10/0/0 | 100%⚠ | 100%⚠ | 63% | 1.00 | 27 | 21 | 27 | 19 | 0 | 27 | 0 | 27 | QUEUED |
| 167 | programming-languages | go-memory-gc-interview | 27 | CRIT | 17/10/0/0 | 100%⚠ | 100%⚠ | 63% | 1.00 | 27 | 20 | 25 | 8 | 0 | 27 | 0 | 27 | QUEUED |
| 168 | algorithms | stacks-queues-interview | 25 | CRIT | 17/8/0/0 | 100%⚠ | 100%⚠ | 68% | 1.00 | 25 | 20 | 25 | 5 | 0 | 25 | 0 | 25 | QUEUED |
| 169 | frameworks | micronaut-interview | 26 | CRIT | 17/8/0/0 | 100%⚠ | 96%⚠ | 65% | 1.00 | 25 | 19 | 24 | 5 | 0 | 25 | 0 | 25 | QUEUED |
| 170 | frameworks | spring-aop-interview | 22 | CRIT | 17/5/0/0 | 100%⚠ | 96%⚠ | 86% | 1.00 | 22 | 20 | 22 | 6 | 0 | 22 | 🟨1 | 22 | QUEUED |
| 171 | messaging | aws-sqs-sns-interview | 22 | CRIT | 17/5/0/0 | 100%⚠ | 100%⚠ | 77% | 1.00 | 22 | 17 | 21 | 1 | 0 | 22 | 0 | 22 | QUEUED |
| 172 | monitoring | micrometer-interview | 20 | CRIT | 17/3/0/0 | 100%⚠ | 100%⚠ | 85% | 1.00 | 20 | 17 | 20 | 1 | 0 | 20 | 0 | 20 | QUEUED |
| 173 | architecture | edge-computing-interview | 18 | CRIT | 17/1/0/0 | 100%⚠ | 100%⚠ | 94% | 1.00 | 18 | 17 | 18 | 6 | 0 | 18 | 0 | 18 | QUEUED |
| 174 | architecture | bff-pattern-interview | 17 | CRIT | 17/0/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 17 | 17 | 17 | 6 | 0 | 17 | 0 | 17 | QUEUED |
| 175 | design-patterns | design-patterns-interview | 48 | CRIT | 16/24/5/1 | 100%⚠ | 69%⚠ | 65% | 1.00 | 43 | 34 | 18 | 12 | 0 | 27 | 0 | 46 | QUEUED |
| 176 | programming-languages | kotlin-interview | 45 | CRIT | 16/27/2/0 | 100%⚠ | 84%⚠ | 80% | 1.00 | 45 | 37 | 22 | 8 | 0 | 39 | 0 | 45 | QUEUED |
| 177 | programming-languages | java-stream-interview | 42 | CRIT | 16/25/1/0 | 100%⚠ | 95%⚠ | 45% | 1.00 | 42 | 28 | 38 | 15 | 0 | 40 | 0 | 42 | QUEUED |
| 178 | api | graphql-interview | 40 | CRIT | 16/20/1/0 | 95%⚠ | 80%⚠ | 55% | 0.98 | 37 | 27 | 23 | 12 | 0 | 31 | 0 | 37 | QUEUED |
| 179 | reactive | webflux-interview | 28 | CRIT | 16/12/0/0 | 100%⚠ | 100%⚠ | 64% | 1.00 | 28 | 21 | 24 | 3 | 0 | 28 | 0 | 28 | QUEUED |
| 180 | algorithms | recursion-interview | 27 | CRIT | 16/10/1/0 | 100%⚠ | 85%⚠ | 89% | 1.00 | 26 | 26 | 20 | 11 | 0 | 22 | 0 | 27 | QUEUED |
| 181 | monitoring | jaeger-zipkin-interview | 23 | CRIT | 16/7/0/0 | 100%⚠ | 96%⚠ | 65% | 1.00 | 23 | 19 | 22 | 3 | 0 | 23 | 0 | 23 | QUEUED |
| 182 | frameworks | resilience4j-interview | 20 | CRIT | 16/4/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 20 | 20 | 16 | 1 | 0 | 20 | 0 | 20 | QUEUED |
| 183 | testing | mutation-testing-interview | 20 | CRIT | 16/4/0/0 | 100%⚠ | 100%⚠ | 75% | 1.00 | 20 | 17 | 20 | 4 | 0 | 20 | 0 | 20 | QUEUED |
| 184 | security | zero-trust-interview | 19 | CRIT | 16/3/0/0 | 100%⚠ | 100%⚠ | 90% | 1.00 | 19 | 18 | 18 | 2 | 0 | 19 | 0 | 19 | QUEUED |
| 185 | testing | mockito-interview | 45 | CRIT | 15/24/6/0 | 89%⚠ | 82%⚠ | 60% | 0.97 | 45 | 38 | 21 | 8 | 0 | 42 | 0 | 45 | QUEUED |
| 186 | reactive | reactive-streams-interview | 30 | CRIT | 15/15/0/0 | 100%⚠ | 100%⚠ | 47% | 1.00 | 30 | 17 | 24 | 6 | 0 | 30 | 0 | 30 | QUEUED |
| 187 | programming-languages | java-mapstruct-interview | 28 | CRIT | 15/13/0/0 | 100%⚠ | 96%⚠ | 61% | 1.00 | 28 | 22 | 21 | 5 | 0 | 27 | 0 | 28 | QUEUED |
| 188 | programming-languages | java-initialization-interview | 27 | CRIT | 15/12/0/0 | 100%⚠ | 96%⚠ | 59% | 1.00 | 27 | 19 | 23 | 6 | 0 | 27 | 0 | 27 | QUEUED |
| 189 | behavioral | conflict-stories-interview | 22 | CRIT | 15/7/0/0 | 100%⚠ | 100%⚠ | 96% | 1.00 | 22 | 21 | 18 | 3 | 0 | 22 | 0 | 22 | QUEUED |
| 190 | devops | linkerd-interview | 20 | CRIT | 15/5/0/0 | 100%⚠ | 100%⚠ | 80% | 1.00 | 20 | 17 | 20 | 1 | 0 | 20 | 0 | 20 | QUEUED |
| 191 | monitoring | observability-interview | 42 | CRIT | 14/22/4/0 | 95%⚠ | 95%⚠ | 45% | 0.98 | 39 | 25 | 28 | 9 | 0 | 36 | 🟧1 | 40 | QUEUED |
| 192 | programming-languages | go-modules-interview | 27 | CRIT | 14/12/1/0 | 100%⚠ | 89%⚠ | 67% | 1.00 | 27 | 19 | 16 | 1 | 0 | 27 | 0 | 27 | QUEUED |
| 193 | code-quality | static-analysis-interview | 26 | CRIT | 14/12/0/0 | 100%⚠ | 100%⚠ | 62% | 1.00 | 26 | 17 | 24 | 4 | 0 | 26 | 0 | 26 | QUEUED |
| 194 | programming-languages | go-generics-interview | 26 | CRIT | 14/12/0/0 | 100%⚠ | 96%⚠ | 58% | 1.00 | 26 | 19 | 24 | 8 | 0 | 26 | 0 | 26 | QUEUED |
| 195 | messaging | pulsar-interview | 22 | CRIT | 14/8/0/0 | 100%⚠ | 100%⚠ | 68% | 1.00 | 22 | 17 | 22 | 5 | 0 | 22 | 0 | 22 | QUEUED |
| 196 | api | api-versioning-interview | 20 | CRIT | 14/6/0/0 | 100%⚠ | 85%⚠ | 90% | 1.00 | 20 | 20 | 19 | 4 | 0 | 20 | 0 | 20 | QUEUED |
| 197 | leadership | estimations-planning-interview | 20 | CRIT | 14/6/0/0 | 100%⚠ | 95%⚠ | 75% | 1.00 | 20 | 17 | 20 | 6 | 0 | 20 | 0 | 20 | QUEUED |
| 198 | api | rest-maturity-interview | 17 | CRIT | 14/3/0/0 | 100%⚠ | 88%⚠ | 76% | 1.00 | 17 | 15 | 16 | 3 | 0 | 17 | 0 | 17 | QUEUED |
| 199 | frameworks | spring-data-jdbc-interview | 16 | CRIT | 14/2/0/0 | 100%⚠ | 100%⚠ | 94% | 1.00 | 16 | 15 | 15 | 3 | 0 | 16 | 0 | 16 | QUEUED |
| 200 | frameworks | spring-integration-interview | 15 | CRIT | 14/1/0/0 | 100%⚠ | 100%⚠ | 100% | 1.00 | 15 | 15 | 14 | 4 | 0 | 15 | 0 | 15 | QUEUED |
| 201 | programming-languages | java-virtual-threads-interview | 15 | CRIT | 14/1/0/0 | 100%⚠ | 100%⚠ | 87% | 1.00 | 15 | 15 | 14 | 1 | 0 | 15 | 0 | 15 | QUEUED |
| 202 | frameworks | ktor-interview | 32 | CRIT | 13/19/0/0 | 100%⚠ | 97%⚠ | 38% | 1.00 | 32 | 24 | 29 | 8 | 0 | 32 | 0 | 32 | QUEUED |
| 203 | behavioral | leadership-stories-interview | 22 | CRIT | 13/9/0/0 | 100%⚠ | 100%⚠ | 91% | 1.00 | 22 | 20 | 20 | 5 | 0 | 22 | 0 | 22 | QUEUED |
| 204 | behavioral | star-method-interview | 22 | CRIT | 13/9/0/0 | 100%⚠ | 96%⚠ | 86% | 1.00 | 22 | 20 | 18 | 4 | 0 | 22 | 0 | 22 | QUEUED |
| 205 | frameworks | spring-vault-interview | 15 | CRIT | 13/2/0/0 | 100%⚠ | 93%⚠ | 93% | 1.00 | 15 | 15 | 14 | 2 | 0 | 15 | 0 | 15 | QUEUED |
| 206 | testing | selenium-interview | 15 | CRIT | 13/2/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 15 | 15 | 14 | 1 | 0 | 15 | 0 | 15 | QUEUED |
| 207 | devops | docker-interview | 41 | CRIT | 12/29/0/0 | 100%⚠ | 90%⚠ | 34% | 1.00 | 41 | 17 | 33 | 5 | 0 | 41 | 0 | 41 | QUEUED |
| 208 | databases | clickhouse-interview | 28 | CRIT | 12/16/0/0 | 100%⚠ | 100%⚠ | 57% | 1.00 | 28 | 21 | 25 | 7 | 0 | 28 | 0 | 28 | QUEUED |
| 209 | testing | property-based-testing-interview | 21 | CRIT | 12/9/0/0 | 100%⚠ | 90%⚠ | 62% | 1.00 | 21 | 16 | 17 | 8 | 0 | 21 | 0 | 21 | QUEUED |
| 210 | programming-languages | kotlin-flow-interview | 17 | CRIT | 12/5/0/0 | 100%⚠ | 94%⚠ | 65% | 1.00 | 17 | 14 | 16 | 4 | 0 | 17 | 0 | 17 | QUEUED |
| 211 | frameworks | spring-graphql-interview | 15 | CRIT | 12/3/0/0 | 100%⚠ | 100%⚠ | 80% | 1.00 | 15 | 14 | 15 | 3 | 0 | 14 | 0 | 15 | QUEUED |
| 212 | frameworks | spring-kafka-interview | 15 | CRIT | 12/3/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 15 | 15 | 13 | 5 | 0 | 15 | 0 | 15 | QUEUED |
| 213 | monitoring | prometheus-grafana-interview | 39 | CRIT | 11/19/8/0 | 97%⚠ | 85%⚠ | 36% | 0.98 | 36 | 18 | 18 | 2 | 0 | 30 | 0 | 38 | QUEUED |
| 214 | ai-ml | llm-basics-interview | 30 | CRIT | 11/14/4/0 | 100%⚠ | 87%⚠ | 53% | 1.00 | 29 | 18 | 14 | 4 | 0 | 22 | 0 | 29 | QUEUED |
| 215 | architecture | dns-interview | 30 | CRIT | 11/6/9/2 | 93%⚠ | 80%⚠ | 70% | 0.97 | 21 | 23 | 19 | 5 | 1 | 10 | 0 | 28 | QUEUED |
| 216 | reactive | reactive-testing-interview | 28 | CRIT | 11/17/0/0 | 100%⚠ | 82%⚠ | 68% | 1.00 | 28 | 23 | 17 | 4 | 0 | 26 | 0 | 28 | QUEUED |
| 217 | code-quality | code-coverage-interview | 25 | CRIT | 11/14/0/0 | 100%⚠ | 92%⚠ | 68% | 1.00 | 25 | 20 | 20 | 5 | 0 | 25 | 0 | 25 | QUEUED |
| 218 | system-design | design-web-crawler-interview | 26 | CRIT | 11/10/1/1 | 92%⚠ | 100%⚠ | 46% | 0.96 | 23 | 13 | 17 | 3 | 0 | 21 | 0 | 23 | QUEUED |
| 219 | frameworks | spring-retry-interview | 17 | CRIT | 11/6/0/0 | 100%⚠ | 100%⚠ | 71% | 1.00 | 17 | 12 | 17 | 4 | 0 | 17 | 0 | 17 | QUEUED |
| 220 | frameworks | spring-messaging-interview | 15 | CRIT | 11/4/0/0 | 100%⚠ | 100%⚠ | 73% | 1.00 | 15 | 13 | 15 | 2 | 0 | 15 | 0 | 15 | QUEUED |
| 221 | frameworks | spring-state-machine-interview | 15 | CRIT | 11/4/0/0 | 100%⚠ | 100%⚠ | 73% | 1.00 | 15 | 13 | 15 | 4 | 0 | 15 | 0 | 15 | QUEUED |
| 222 | frameworks | spring-transaction-interview | 15 | CRIT | 11/4/0/0 | 100%⚠ | 100%⚠ | 93% | 1.00 | 15 | 14 | 12 | 5 | 0 | 15 | 0 | 15 | QUEUED |
| 223 | databases | hibernate-interview | 50 | CRIT | 10/27/6/1 | 92%⚠ | 76%⚠ | 36% | 0.98 | 44 | 23 | 15 | 6 | 0 | 38 | 0 | 44 | QUEUED |
| 224 | programming-languages | java-lombok-interview | 27 | CRIT | 10/16/1/0 | 100%⚠ | 89%⚠ | 52% | 1.00 | 27 | 19 | 19 | 9 | 0 | 26 | 0 | 27 | QUEUED |
| 225 | reactive | reactive-patterns-interview | 26 | CRIT | 10/16/0/0 | 100%⚠ | 92%⚠ | 81% | 1.00 | 26 | 22 | 13 | 1 | 0 | 25 | 0 | 26 | QUEUED |
| 226 | algorithms | algorithms-interview | 16 | CRIT | 10/6/0/0 | 100%⚠ | 94%⚠ | 69% | 1.00 | 16 | 15 | 16 | 9 | 0 | 16 | 0 | 16 | QUEUED |
| 227 | frameworks | spring-testing-interview | 16 | CRIT | 10/5/0/1 | 94%⚠ | 94%⚠ | 69% | 0.95 | 15 | 14 | 13 | 5 | 0 | 15 | 0 | 16 | QUEUED |
| 228 | programming-languages | java-reflection-interview | 16 | CRIT | 10/6/0/0 | 100%⚠ | 100%⚠ | 62% | 1.00 | 16 | 13 | 16 | 3 | 0 | 16 | 0 | 16 | QUEUED |
| 229 | jvm | graalvm-native-interview | 15 | CRIT | 10/5/0/0 | 100%⚠ | 100%⚠ | 73% | 1.00 | 15 | 14 | 14 | 4 | 0 | 15 | 0 | 15 | QUEUED |
| 230 | programming-languages | java-optional-interview | 15 | CRIT | 10/5/0/0 | 100%⚠ | 100%⚠ | 67% | 1.00 | 15 | 14 | 15 | 7 | 0 | 15 | 0 | 15 | QUEUED |
| 231 | programming-languages | java-functional-interface-interview | 14 | CRIT | 10/4/0/0 | 100%⚠ | 100%⚠ | 79% | 1.00 | 14 | 12 | 14 | 4 | 0 | 14 | 0 | 14 | QUEUED |
| 232 | devops | git-interview | 43 | CRIT | 9/12/19/1 | 91%⚠ | 84%⚠ | 63% | 0.97 | 25 | 31 | 18 | 11 | 0 | 8 | 0 | 41 | QUEUED |
| 233 | api | api-design-best-practices-interview | 30 | CRIT | 9/10/11/0 | 97%⚠ | 83%⚠ | 57% | 0.99 | 26 | 18 | 14 | 7 | 0 | 21 | 0 | 30 | QUEUED |
| 234 | code-quality | code-smells-interview | 27 | CRIT | 9/18/0/0 | 100%⚠ | 74%⚠ | 48% | 1.00 | 27 | 15 | 15 | 6 | 0 | 26 | 0 | 27 | QUEUED |
| 235 | frameworks | spring-validation-interview | 16 | CRIT | 9/7/0/0 | 100%⚠ | 100%⚠ | 69% | 1.00 | 16 | 13 | 16 | 3 | 0 | 15 | 0 | 16 | QUEUED |
| 236 | databases | hibernate-relationships-interview | 15 | CRIT | 9/6/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 15 | 9 | 15 | 8 | 0 | 15 | 0 | 15 | QUEUED |
| 237 | frameworks | spring-boot-3-migration-interview | 15 | CRIT | 9/6/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 15 | 12 | 15 | 1 | 0 | 15 | 0 | 15 | QUEUED |
| 238 | frameworks | spring-r2dbc-interview | 15 | CRIT | 9/6/0/0 | 100%⚠ | 100%⚠ | 53% | 1.00 | 15 | 10 | 14 | 2 | 0 | 15 | 0 | 15 | QUEUED |
| 239 | programming-languages | java-annotations-interview | 43 | CRIT | 8/28/4/2 | 98%⚠ | 79%⚠ | 40% | 0.99 | 38 | 20 | 21 | 5 | 0 | 34 | 0 | 42 | QUEUED |
| 240 | programming-languages | java-exceptions-interview | 42 | CRIT | 8/32/2/0 | 98%⚠ | 71%⚠ | 19% | 0.99 | 42 | 26 | 21 | 8 | 1 | 41 | 0 | 42 | QUEUED |
| 241 | programming-languages | rust-interview | 33 | CRIT | 8/3/18/1 | 70%⚠ | 61%⚠ | 61% | 0.86 | 7 | 28 | 9 | 11 | 5 | 2 | 0 | 30 | QUEUED |
| 242 | frameworks | spring-cache-interview | 18 | CRIT | 8/10/0/0 | 100%⚠ | 89%⚠ | 61% | 1.00 | 18 | 12 | 14 | 6 | 0 | 15 | 0 | 18 | QUEUED |
| 243 | frameworks | spring-ai-interview | 18 | CRIT | 8/7/1/0 | 89%⚠ | 94%⚠ | 50% | 0.96 | 15 | 12 | 14 | 3 | 0 | 15 | 0 | 16 | QUEUED |
| 244 | databases | hibernate-caching-interview | 15 | CRIT | 8/7/0/0 | 100%⚠ | 100%⚠ | 67% | 1.00 | 15 | 14 | 12 | 5 | 0 | 15 | 0 | 15 | QUEUED |
| 245 | frameworks | spring-async-interview | 15 | CRIT | 8/6/1/0 | 100%⚠ | 93%⚠ | 67% | 1.00 | 14 | 12 | 11 | 2 | 0 | 12 | 0 | 15 | QUEUED |
| 246 | frameworks | spring-session-interview | 15 | CRIT | 8/7/0/0 | 100%⚠ | 93%⚠ | 60% | 1.00 | 15 | 11 | 13 | 1 | 0 | 15 | 0 | 15 | QUEUED |
| 247 | databases | sql-interview | 53 | CRIT | 7/19/14/6 | 91%⚠ | 55%⚠ | 24% | 0.97 | 37 | 23 | 14 | 13 | 0 | 20 | 0 | 46 | QUEUED |
| 248 | testing | test-automation-interview | 50 | CRIT | 7/19/14/3 | 34% | 72%⚠ | 58% | 0.68 | 20 | 35 | 24 | 10 | 6 | 12 | 0 | 43 | QUEUED |
| 249 | code-quality | code-review-interview | 40 | CRIT | 7/7/15/7 | 55%⚠ | 62%⚠ | 42% | 0.74 | 12 | 29 | 18 | 8 | 5 | 3 | 🟥1 | 36 | QUEUED |
| 250 | behavioral | behavioral-interview | 38 | CRIT | 7/13/11/2 | 92%⚠ | 76%⚠ | 34% | 0.97 | 28 | 22 | 9 | 5 | 0 | 17 | 0 | 33 | QUEUED |
| 251 | programming-languages | typescript-interview | 33 | CRIT | 7/18/5/1 | 97%⚠ | 67%⚠ | 46% | 0.98 | 30 | 19 | 11 | 10 | 0 | 22 | 0 | 31 | QUEUED |
| 252 | system-design | design-dropbox-interview | 30 | CRIT | 7/16/6/0 | 100%⚠ | 93%⚠ | 80% | 1.00 | 12 | 27 | 24 | 11 | 1 | 4 | 0 | 29 | QUEUED |
| 253 | system-design | design-typeahead-interview | 30 | CRIT | 7/12/9/1 | 27% | 90%⚠ | 60% | 0.55 | 11 | 20 | 24 | 9 | 7 | 2 | 🟧1 | 29 | QUEUED |
| 254 | programming-languages | java-string-interview | 39 | CRIT | 7/7/9/5 | 69%⚠ | 64%⚠ | 36% | 0.90 | 13 | 22 | 10 | 9 | 3 | 7 | 0 | 28 | QUEUED |
| 255 | ai-ml | function-calling-interview | 30 | CRIT | 7/12/7/0 | 63%⚠ | 93%⚠ | 57% | 0.89 | 21 | 20 | 16 | 6 | 2 | 13 | 0 | 26 | QUEUED |
| 256 | behavioral | failure-stories-interview | 22 | CRIT | 7/13/2/0 | 100%⚠ | 86%⚠ | 64% | 1.00 | 22 | 15 | 8 | 4 | 0 | 18 | 🟥4 | 22 | QUEUED |
| 257 | frameworks | spring-scheduling-interview | 16 | CRIT | 7/5/4/0 | 100%⚠ | 94%⚠ | 69% | 1.00 | 13 | 14 | 9 | 3 | 0 | 4 | 0 | 16 | QUEUED |
| 258 | testing | junit-interview | 15 | CRIT | 7/8/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 15 | 10 | 12 | 2 | 0 | 14 | 0 | 15 | QUEUED |
| 259 | testing | rest-assured-interview | 15 | CRIT | 7/8/0/0 | 100%⚠ | 100%⚠ | 53% | 1.00 | 15 | 12 | 15 | 1 | 0 | 14 | 0 | 15 | QUEUED |
| 260 | frameworks | spring-rest-client-interview | 13 | CRIT | 7/5/1/0 | 100%⚠ | 92%⚠ | 46% | 1.00 | 13 | 10 | 12 | 1 | 0 | 12 | 0 | 13 | QUEUED |
| 261 | programming-languages | java-concurrency-interview | 56 | CRIT | 6/16/23/4 | 89%⚠ | 55%⚠ | 59% | 0.96 | 36 | 38 | 7 | 9 | 1 | 6 | 0 | 49 | QUEUED |
| 262 | programming-languages | java-conditional-statements-interview | 42 | CRIT | 6/14/12/7 | 57%⚠ | 57%⚠ | 24% | 0.70 | 29 | 23 | 17 | 8 | 0 | 17 | 0 | 39 | QUEUED |
| 263 | programming-languages | java-records-interview | 15 | CRIT | 6/9/0/0 | 100%⚠ | 93%⚠ | 60% | 1.00 | 15 | 9 | 11 | 2 | 0 | 15 | 0 | 15 | QUEUED |
| 264 | programming-languages | kotlin-sealed-classes-interview | 15 | CRIT | 6/9/0/0 | 100%⚠ | 67%⚠ | 67% | 1.00 | 15 | 11 | 6 | 3 | 0 | 14 | 0 | 15 | QUEUED |
| 265 | cloud | aws-interview | 34 | CRIT | 5/3/13/11 | 9% | 41% | 38% | 0.42 | 6 | 29 | 6 | 10 | 6 | 0 | 0 | 32 | QUEUED |
| 266 | monitoring | loki-grafana-interview | 28 | CRIT | 5/10/9/2 | 43% | 86%⚠ | 57% | 0.61 | 1 | 22 | 17 | 7 | 5 | 0 | 0 | 26 | QUEUED |
| 267 | leadership | conflict-resolution-interview | 20 | CRIT | 5/15/0/0 | 100%⚠ | 95%⚠ | 45% | 1.00 | 20 | 12 | 16 | 6 | 0 | 20 | 0 | 20 | QUEUED |
| 268 | frameworks | spring-events-interview | 16 | CRIT | 5/11/0/0 | 100%⚠ | 94%⚠ | 62% | 1.00 | 16 | 12 | 11 | 3 | 0 | 16 | 0 | 16 | QUEUED |
| 269 | frameworks | spring-modulith-interview | 15 | CRIT | 5/10/0/0 | 100%⚠ | 100%⚠ | 60% | 1.00 | 15 | 10 | 9 | 3 | 0 | 14 | 0 | 15 | QUEUED |
| 270 | programming-languages | kotlin-value-classes-interview | 15 | CRIT | 5/7/2/0 | 93%⚠ | 73%⚠ | 47% | 0.97 | 13 | 12 | 6 | 4 | 0 | 10 | 0 | 14 | QUEUED |
| 271 | algorithms | two-pointers-sliding-window-interview | 33 | CRIT | 4/18/11/0 | 61%⚠ | 97%⚠ | 76% | 0.86 | 19 | 29 | 26 | 10 | 1 | 3 | 0 | 33 | QUEUED |
| 272 | system-design | design-google-maps-interview | 30 | CRIT | 4/15/8/1 | 70%⚠ | 87%⚠ | 73% | 0.80 | 6 | 26 | 20 | 7 | 4 | 0 | 🟧1 | 28 | QUEUED |
| 273 | testing | chaos-engineering-interview | 44 | CRIT | 3/8/24/3 | 93%⚠ | 68%⚠ | 59% | 0.97 | 9 | 31 | 14 | 8 | 0 | 1 | 🟥1 | 38 | QUEUED |
| 274 | frameworks | spring-boot-interview | 43 | CRIT | 3/13/16/2 | 51%⚠ | 86%⚠ | 54% | 0.77 | 11 | 30 | 16 | 4 | 1 | 5 | 0 | 34 | QUEUED |
| 275 | jvm | jvm-interview | 40 | CRIT | 3/11/14/5 | 88%⚠ | 57%⚠ | 25% | 0.96 | 26 | 19 | 11 | 7 | 0 | 12 | 0 | 33 | QUEUED |
| 276 | architecture | ddd-interview | 38 | CRIT | 3/11/16/1 | 76%⚠ | 53%⚠ | 63% | 0.90 | 3 | 28 | 12 | 10 | 6 | 0 | 0 | 31 | QUEUED |
| 277 | ai-ml | ai-safety-guardrails-interview | 32 | CRIT | 3/13/12/2 | 31% | 88%⚠ | 56% | 0.69 | 5 | 24 | 20 | 9 | 5 | 1 | 0 | 30 | QUEUED |
| 278 | devops | linux-interview | 33 | CRIT | 3/18/9/0 | 73%⚠ | 79%⚠ | 64% | 0.88 | 6 | 25 | 26 | 9 | 4 | 0 | 0 | 30 | QUEUED |
| 279 | frameworks | vertx-interview | 31 | CRIT | 3/7/14/4 | 52%⚠ | 84%⚠ | 48% | 0.72 | 13 | 22 | 12 | 5 | 3 | 0 | 0 | 28 | QUEUED |
| 280 | cloud | azure-interview | 25 | CRIT | 3/14/6/0 | 40% | 96%⚠ | 72% | 0.78 | 6 | 22 | 20 | 4 | 2 | 0 | 0 | 23 | QUEUED |
| 281 | behavioral | culture-fit-interview | 22 | CRIT | 3/19/0/0 | 100%⚠ | 91%⚠ | 77% | 1.00 | 22 | 17 | 8 | 10 | 0 | 22 | 0 | 22 | QUEUED |
| 282 | architecture | strangler-fig-interview | 18 | CRIT | 3/14/1/0 | 100%⚠ | 67%⚠ | 61% | 1.00 | 18 | 15 | 3 | 4 | 0 | 13 | 0 | 18 | QUEUED |
| 283 | messaging | redpanda-interview | 20 | CRIT | 3/9/5/1 | 90%⚠ | 80%⚠ | 20% | 0.97 | 17 | 7 | 7 | 7 | 0 | 14 | 0 | 18 | QUEUED |
| 284 | programming-languages | java-pattern-matching-interview | 15 | CRIT | 3/12/0/0 | 100%⚠ | 80%⚠ | 47% | 1.00 | 15 | 8 | 8 | 1 | 0 | 13 | 0 | 15 | QUEUED |
| 285 | frameworks | spring-webflux-interview | 43 | CRIT | 2/9/24/4 | 58%⚠ | 77%⚠ | 60% | 0.77 | 5 | 31 | 18 | 6 | 2 | 0 | 0 | 39 | QUEUED |
| 286 | system-design | design-search-interview | 30 | CRIT | 2/18/9/1 | 53%⚠ | 97%⚠ | 60% | 0.81 | 20 | 27 | 25 | 0 | 0 | 1 | 0 | 30 | QUEUED |
| 287 | system-design | design-uber-interview | 30 | CRIT | 2/18/10/0 | 0% | 97%⚠ | 63% | 0.57 | 12 | 23 | 25 | 7 | 3 | 0 | 0 | 30 | QUEUED |
| 288 | ai-ml | agentic-patterns-interview | 30 | CRIT | 2/8/16/3 | 20% | 93%⚠ | 63% | 0.80 | 26 | 21 | 14 | 4 | 2 | 2 | 0 | 29 | QUEUED |
| 289 | ai-ml | code-agents-interview | 31 | CRIT | 2/8/12/6 | 58%⚠ | 77%⚠ | 45% | 0.86 | 15 | 18 | 16 | 2 | 0 | 3 | 0 | 28 | QUEUED |
| 290 | leadership | tech-interviewing-interview | 20 | CRIT | 2/17/0/0 | 100%⚠ | 85%⚠ | 15% | 1.00 | 19 | 8 | 6 | 3 | 0 | 18 | 0 | 19 | QUEUED |
| 291 | databases | hibernate-jpql-criteria-interview | 16 | CRIT | 2/8/4/1 | 88%⚠ | 75%⚠ | 44% | 0.94 | 13 | 12 | 2 | 6 | 1 | 4 | 0 | 15 | QUEUED |
| 292 | programming-languages | kotlin-spring-interview | 15 | CRIT | 2/5/6/0 | 100%⚠ | 80%⚠ | 20% | 1.00 | 12 | 5 | 6 | 2 | 0 | 9 | 0 | 13 | QUEUED |
| 293 | algorithms | graphs-interview | 34 | CRIT | 1/14/16/1 | 62%⚠ | 76%⚠ | 79% | 0.85 | 5 | 29 | 19 | 0 | 0 | 1 | 0 | 32 | QUEUED |
| 294 | architecture | event-driven-patterns-interview | 40 | CRIT | 1/5/15/8 | 62%⚠ | 62%⚠ | 38% | 0.81 | 11 | 19 | 6 | 10 | 2 | 2 | 0 | 29 | QUEUED |
| 295 | data-engineering | apache-airflow-interview | 28 | CRIT | 1/16/8/1 | 82%⚠ | 96%⚠ | 82% | 0.93 | 4 | 25 | 19 | 5 | 0 | 1 | 0 | 26 | QUEUED |
| 296 | architecture | reverse-proxy-interview | 30 | CRIT | 1/16/4/4 | 47% | 80%⚠ | 53% | 0.72 | 7 | 24 | 19 | 11 | 2 | 0 | 0 | 25 | QUEUED |
| 297 | cloud | gcp-interview | 28 | CRIT | 1/7/10/3 | 36% | 89%⚠ | 50% | 0.71 | 3 | 15 | 9 | 4 | 2 | 0 | 0 | 21 | QUEUED |
| 298 | ai-ml | rag-interview | 30 | HIGH | 0/7/13/9 | 33% | 93%⚠ | 43% | 0.72 | 4 | 24 | 14 | 0 | 0 | 0 | 🟧1 | 29 | QUEUED |
| 299 | system-design | design-ecommerce-delivery-interview | 36 | HIGH | 0/5/16/8 | 78%⚠ | 75%⚠ | 33% | 0.91 | 2 | 21 | 5 | 8 | 3 | 0 | 0 | 29 | QUEUED |
| 300 | cloud | aws-lambda-interview | 32 | HIGH | 0/6/18/4 | 38% | 78%⚠ | 38% | 0.68 | 6 | 22 | 14 | 0 | 0 | 0 | 0 | 28 | QUEUED |
| 301 | code-quality | refactoring-patterns-interview | 42 | HIGH | 0/3/18/5 | 33% | 50% | 38% | 0.66 | 2 | 23 | 6 | 0 | 0 | 0 | 0 | 26 | QUEUED |
| 302 | devops | kubernetes-interview | 45 | HIGH | 0/8/12/6 | 56%⚠ | 67%⚠ | 33% | 0.83 | 9 | 16 | 9 | 4 | 0 | 0 | 0 | 26 | QUEUED |
| 303 | data-engineering | data-warehousing-interview | 30 | HIGH | 0/6/16/1 | 33% | 63%⚠ | 60% | 0.65 | 0 | 22 | 10 | 4 | 0 | 0 | 0 | 23 | QUEUED |
| 304 | cloud | cloud-native-patterns-interview | 30 | HIGH | 0/8/9/4 | 53%⚠ | 57%⚠ | 43% | 0.72 | 1 | 17 | 9 | 6 | 2 | 0 | 0 | 21 | QUEUED |
| 305 | leadership | mentoring-interview | 25 | HIGH | 0/1/12/4 | 32% | 84%⚠ | 0% | 0.62 | 6 | 12 | 3 | 0 | 0 | 0 | 🟨1 | 17 | QUEUED |
| 306 | messaging | kafka-interview | 50 | HIGH | 0/3/6/2 | 70%⚠ | 62%⚠ | 0% | 0.85 | 9 | 0 | 2 | 2 | 0 | 1 | 0 | 11 | QUEUED |
| 307 | programming-languages | java-completable-future-interview | 15 | HIGH | 0/2/6/3 | 80%⚠ | 33% | 7% | 0.90 | 6 | 4 | 1 | 3 | 1 | 0 | 0 | 11 | QUEUED |
| 308 | testing | cucumber-bdd-interview | 15 | HIGH | 0/1/0/0 | 33% | 73%⚠ | 0% | 0.72 | 0 | 1 | 0 | 1 | 1 | 0 | 0 | 1 | QUEUED |
| 309 | programming-languages | java-22-25-interview | 22 | MEDI | 0/0/4/3 | 46% | 41% | 4% | 0.76 | 1 | 5 | 2 | 1 | 0 | 0 | 0 | 7 | QUEUED |
| 310 | jvm | crac-interview | 16 | MEDI | 0/0/1/3 | 38% | 62%⚠ | 6% | 0.64 | 0 | 4 | 0 | 0 | 0 | 0 | 0 | 4 | QUEUED |
| 311 | ai-ml | langchain4j-interview | 18 | MEDI | 0/0/2/1 | 28% | 56%⚠ | 0% | 0.61 | 1 | 1 | 1 | 2 | 0 | 0 | 0 | 3 | QUEUED |
| 312 | databases | jooq-interview | 18 | MEDI | 0/0/1/1 | 33% | 78%⚠ | 0% | 0.79 | 2 | 0 | 0 | 0 | 0 | 1 | 0 | 2 | QUEUED |
| 313 | testing | archunit-interview | 15 | MEDI | 0/0/1/0 | 20% | 87%⚠ | 0% | 0.58 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | QUEUED |
| 314 | messaging | apache-camel-interview | 16 | LOW | 0/0/0/1 | 12% | 75%⚠ | 0% | 0.62 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 | QUEUED |
| 315 | messaging | jms-activemq-interview | 16 | LOW | 0/0/0/1 | 19% | 88%⚠ | 0% | 0.59 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | QUEUED |
| 316 | performance | jmh-microbenchmarking-interview | 18 | NONE | 0/0/0/0 | 22% | 78%⚠ | 0% | 0.54 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | QUEUED |
| 317 | programming-languages | kotlin-testing-interview | 20 | NONE | 0/0/0/0 | 35% | 85%⚠ | 0% | 0.61 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | QUEUED |
| 318 | programming-languages | scala-effects-interview | 16 | NONE | 0/0/0/0 | 25% | 88%⚠ | 0% | 0.53 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | QUEUED |

## 8. Прогресс (round 3 — Plausibility + Readability, старт 2026-07-01)

**RESET #1 + #2 выполнены 2026-07-01** (см. баннер). Прогресс round 2 (Option Parity) обнулён в таблице;
коммиты round 2 остаются в git-истории (~30 сидеров: `java-concurrency` 7c013c0d … `design-uber` f0119a42,
`kubernetes` 00bf1489). Под объединённым баром Option Parity + **Plausibility Parity** + **Human-Readability**
файлы переоткрыты.

- ✅ Plausibility Parity + анти-паттерн **Inflated Caricature Distractor** закреплены в `mcq-quality-fixer` и `interview-writer`.
- ✅ `scripts/audit-mcq-parity.py` переписан в **audit v2** (commit 92fb5d46): severity LOW/MED/HIGH/CRIT, technical-density, structural-symmetry, caricature v2 (группы+сниппет), INFLATED_CARICATURE, SHORT_DISTR v2, file-level rate'ы, `--json-report`/`--markdown-report`/`--fail-on`/`--selftest`.
- ✅ **Третья линза — человекочитаемость русской прозы** (commit 3fc4745d, `--readability`): сигналы `LOW_CYRILLIC`/`ENGLISH_RUN`/`LONG_SENTENCE`/`FILLER_PHRASE`/`PUNCT` на `text`+прозаических `sections`. Калибровано адверс-выборкой (3-агентный workflow судил FP-rate; корпус легитимно code-switch'ит → язык-сигнал требует англ. ПРОЗЫ со служебными+содержательными словами). Высокая точность: 9265→19 блоков-кандидатов, FP-файл 31→1. Колонка **RDB** в §7 (независимая ось, не в Σ).
- ✅ §7 пересобран **детальным re-аудитом** (audit v2 + readability): per-signal разбивка (LEN/STR/DEN/CAR/INF/SHRT/RDB по блокам), severity-распределение C/H/M/L, file-rate'ы long%/tech%/ustr%/rank. Все статусы → QUEUED.
- ✅ Закоммичено в round 3 (workflow `wn86pui6x`, fidelity_ok+parity_ok): `ai-safety-guardrails` (ad6ca9c7, 32 бл.), `spring-webflux` (b701ac76, 43 бл.), `test-automation` (5f9d63fb, 50 бл.) — length-tell снят.
- ✅ Round-3 **de-caricature** band-1 (workflow по 3 файла, fidelity+parity+plausibility triple-ok): `leadership/mentoring` (f75faad6, CAR 27→0), `ai-ml/rag` (a545c58e, CAR 17→0), `ai-ml/design-search` (d0dfcebf, CAR 24→0) — length-tell остался 0, 0 регрессий.
- ✅ Round-3 de-caricature band-2: `refactoring-patterns` (87d04acf), `aws-lambda` (b238f382), `graphs` (5d4decdd) — каждый CAR→0, дистракторы раздуты до реалистичных ошибочных моделей без токсичных маркеров.
- ⏳ Следующая цель round 3: CAR-heavy архитектурные INF=0 файлы (`cqrs-event-sourcing`/`scalability-patterns`/`mongodb`/`caching-strategies`/`cap-theorem`/`consistency-patterns`), затем worst-first CRITICAL по §7. Readability-хотспот `behavioral/failure-stories` (🟥4) — отдельный проход при желании.

**Честно:** round 3 объединяет три линзы. Под объединённым баром 297/318 файлов = CRITICAL (correct
выделяется по длине+структуре+плотности). Length/structure-tell и caricature-tell чинятся вместе:
дистрактор поднимается до паритета по форме И остаётся правдоподобной ошибочной моделью (без токсичных
маркеров). Readability — независимая ось (русская проза), 19 блоков-кандидатов. Round-2-фиксы убрали
length-tell, но НЕ caricature (CAR/INF) — это round-3 остаток. «Все сидеры исправлены» — цель цикла,
не одного тика.

## 9. Координация

Параллельная claude-сессия работает worst-first СВЕРХУ (frontier ranks 12–14, static:
`agentic-patterns`/`git`/`chaos-engineering`). Я беру нижний слой; перед каждым слайсом —
`git status seed/mcq` → skip их dirty-файлов.

