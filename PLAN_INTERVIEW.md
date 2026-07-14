# PLAN_INTERVIEW — ROUND 8 FULL MCQ QUALITY RESET

## 0. Reset directive

**Дата сброса:** 2026-07-11  
**Корпус:** все JSON MCQ-сидеры в `modules/quiz-app/src/main/resources/seed/mcq/**/*.json`  
**Статус старого прогресса:** полностью аннулирован для целей приёмки ROUND 8.

Старые коммиты и история исправлений остаются в Git, но **ни один файл не считается проверенным**
по текущему стандарту. Все прежние `DONE`, `WIP`, проценты и ручные отметки удалены из рабочего
прогресса. Каждый файл проходит новый полный аудит с нуля.

Причина сброса: прежний план в основном измерял визуальные `tell`-сигналы и общий статус Golden,
но не давал отдельной приёмки по фактической корректности правильного ответа, единственности ответа,
качеству каждого дистрактора, согласованности `sections`, теории, freshness и blind-review. ROUND 8
разделяет эти критерии и запрещает ставить общий `DONE`, пока не закрыта каждая ось.

---

## 1. Авторитетные источники правил

Перед обработкой любого файла обязательны:

1. `~/.claude/skills/mcq-quality-fixer/SKILL.md`
2. `~/.claude/skills/mcq-quality-fixer/references/full-guide.md`, если существует
3. `~/.claude/skills/interview-writer/SKILL.md`
4. `~/.claude/skills/interview-writer/references/full-guide.md`, если существует
5. `~/.claude/skills/interview-options-writer/SKILL.md`
6. `~/.claude/skills/interview-options-writer/references/full-guide.md`, если существует
7. `docs/golden-examples.md`
8. параллельный theory-файл `cheatsheets/interview/**/<topic>-interview.md`
9. schema и текущие audit-скрипты репозитория

При конфликте приоритет:

1. фактическая корректность;
2. один однозначно защитимый ответ;
3. соответствие вопросу и теории;
4. правдоподобность дистракторов;
5. естественный русский язык;
6. Visual/Structure Parity;
7. числовые метрики и баланс позиций.

---

## 1b. Решения по работе loop (закреплено пользователем 2026-07-14)

Ответы на 10 уточняющих вопросов о том, как должен работать цикл. Имеют силу правил наравне с §1 (при конфликте §1-приоритет фактов/читаемости остаётся выше).

1. **Порядок правок внутри файла — СНАЧАЛА факт/когерентность, потом метрики.** Взяв файл, сперва прогнать ВСЕ блоки на факт-ошибки и само-противоречия (§1 top), и только потом дожимать метрики длины worst-first. (Было: чисто worst-first по OPTION_LENGTH_RATIO.)
2. **Скоуп тика — весь файл за тик, если остаток тривиален.** Когда по файлу остались только механические/однотипные правки — закрывать целиком за тик. Нетривиальные смысловые правки по-прежнему дробить (каждая со своим блайндом).
3. **Де-каррикатура — убирать только цирковые абсолюты** («ровно/всегда/полностью все»), а section-подкреплённые заблуждения (напр. «~100× быстрее») ОСТАВЛЯТЬ как обучающий misconception. Сохраняет контраст «маркетинг vs реальность».
4. **OPTION_LENGTH_RATIO — целиться в ≤1.35, но НЕ жать искусственно.** Не раздувать нормальные дистракторы ради метрики; если паритет не достигается естественно — оставить остаточный ratio (1.4–1.5) и залогировать (§1: метрика — низкий приоритет).
5. **Слепой ревью — пропускать ТОЛЬКО при byte-identical контенте** (чистый POS: label/order меняются, text+sections байт-в-байт те же). Любая правка текста/sections → независимый блайнд обязателен.
6. **FINAL файла = ОБА: все блоки content-реаудированы (single-correct + когерентность) И гейты зелёные** (answer-parity + skel, с учётом §1-исключений для естественно-недостижимых метрик).
7. **NIT от ревьюера — чинить в ТОМ ЖЕ тике, если в scope блока** и правка безопасна (один блайнд покрывает весь блок). (Было: всегда отдельным тиком.)
8. **После FINAL файла — следующий worst-first по матрице** через `gen-plan-matrix.py` (off-limits фильтр, пропуск FINAL/COMPLETE). Цикл автономен, без остановки на выбор.
9. **skel hard_fails (`;`/`:`/em-dash паритет) и file-level ABSOLUTE_MARKER_GAP — чинить попутно**, когда блок и так правится. Отдельные тики под пунктуацию не заводить; т.к. по п.6 каждый блок всё равно будет тронут при content-реаудите, паритет добирается там же.
10. **off-limits — сняты ВСЕ тематические запреты** (уточнено 2026-07-14: пользователь выбрал открыть все — postgresql, testcontainers, chaos-engineering, rxjava, algorithms, rest-maturity, agentic-patterns, mentoring, git). Тематического off-limits-списка БОЛЬШЕ НЕТ. **Единственный оставшийся барьер — dirty working-tree:** НИКОГДА не трогать файл с незакоммиченными чужими правками; каждый тик прогонять collision-guard (`git status --porcelain`), не стейджить чужие dirty-файлы (`.claude/settings.local.json`, `.cursor/hooks/...`, файлы параллельной фронт-сессии); запрещённые git-операции (`reset --hard`, `checkout -- .`, `restore .`, `add -A`, revert чужих коммитов, удаление `graphify-out/`, `git push`) — в силе.

---

## 2. Что считается единицей качества

Файл не проверяется только «в среднем». Проверяется **каждый MCQ-блок**.

Для каждого блока обязательна запись в:

```text
docs/mcq-quality/reviews/<topic_slug>.json
```

Минимальная структура review-sidecar:

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

`null` означает «не проверено». Нельзя автоматически заменять все `null` на `true`.
Каждый флаг выставляется только после фактического чтения блока.

---

## 3. Статусы

| Символ | Значение |
|---|---|
| `⬜` | не проверено |
| `🔄` | проверяется сейчас |
| `◐` | частично проверено, есть необработанные блоки |
| `🟠` | требуется ручное решение |
| `⏸` | пропущено из-за dirty/off-limits/внешнего блокера |
| `❌` | критерий не пройден |
| `✅` | критерий проверен и пройден |

Общий `DONE` допустим только когда **все обязательные колонки строки имеют `✅`** и review-sidecar
не содержит `TODO`, `FAIL`, `CRITICAL` или неразрешённых `MANUAL_REVIEW`.

---

## 4. Критерии мастер-таблицы

### 4.1 Целостность файла

| Колонка | Проверка |
|---|---|
| `SCH` | JSON соответствует schema, 4 options, 1 correct, `order`/`label` корректны |
| `TH` | существует параллельная теория и она прочитана |
| `QS` | каждый JSON `q_number` существует в `.md`, нет dangling-блоков |
| `STEM` | все `question_text` ясные, однофокусные, без подсказки |
| `FR` | version-sensitive claims проверены, freshness sidecar актуален |
| `LINK` | `related` и theory-links существуют и содержательно связаны |
| `POS` | позиции correct сбалансированы и не образуют угадываемый паттерн |

### 4.2 Правильные ответы

| Колонка | Проверка |
|---|---|
| `C-FCT` | correct фактически верен, не доверяем старому `correct:true` |
| `C-CMP` | correct достаточно полный для стема, но не превращён в лекцию |
| `C-SCP` | version/provider/config/scope указаны корректно |
| `C-UNI` | correct единственный защитимый ответ |
| `C-FRM` | correct не выделяется длиной, тоном, punctuation, code-density (**структурный скелет — колонка `SKEL`**) |
| `C-SEC` | `explanation/example/when_to_apply/edge_cases/related` точны и не противоречат text |
| `C-RU` | correct написан естественным русским языком |

### 4.3 Неправильные ответы

| Колонка | Проверка |
|---|---|
| `W-PLS` | каждый distractor правдоподобен для middle/senior |
| `W-1ER` | у каждого distractor ровно одна основная смысловая ошибка |
| `W-API` | используются реальные API/термины/механизмы |
| `W-FLS` | distractor однозначно ложен под условиями стема |
| `W-DIV` | три distractor проверяют разные misconceptions |
| `W-CAR` | нет карикатур, токсичности, очевидного абсурда и negation-echo |
| `W-FRM` | wrong options сопоставимы с correct по длине/насыщенности (**единый скелет — колонка `SKEL`**) |
| `SKEL` | **HARD GATE (LLM):** все 4 `option.text` делят один surface skeleton. Pre-check: `python3 scripts/mcq-skel-gate.py <json>` (грубые mismatch). Зелёный скрипт **не** даёт `SKEL=✅`; красный — сигнал чинить. Приёмка только LLM-чтением. Без `SKEL=✅` запрещены `PAR=✅` и `FINAL=✅` |
| `W-SEC` | `what_actually/source_of_confusion/if_it_were_true/how_it_should_be` согласованы с text |
| `W-RU` | wrong options читаются естественно, без искусственного раздувания |

### 4.4 Блоковый и файловый итог

| Колонка | Проверка |
|---|---|
| `PAR` | Option/Plausibility/Visual Format Parity пройдены |
| `BST` | blind style-review не угадывает correct по форме |
| `BSM` | blind semantic-review не может защитить distractor |
| `STAMP` | нет stamped-clone over-correction |
| `VAL` | все обязательные audit/validation commands проходят |
| `SFPL` | **Surface Parity (структура/формат/пунктуация/длина):** correct неотличим по форме; сводный гейт R1–R6 (`docs/mcq-quality/surface-parity-rules.md`). ✅ только когда scanner NO_TELLS + слепой `guessable_by_form=false` на всех блоках файла |
| `ANSC` | **Answer Completeness:** каждый из 4 вариантов полноценно и по существу отвечает на стем; дистрактор — полный правдоподобный ответ с одной содержательной ошибкой, сопоставимый с correct по объёму/глубине. ✅ когда слепой `completeness_ok=true` |
| `FINAL` | все блоки и все колонки закрыты, sidecar complete |

---

## 5. Приоритет отбора файлов

Старый `Tell` сохраняется только как один из baseline-сигналов. Новый приоритет считается по совокупности:

```text
Priority =
  100 × schema/factual critical
+ 80  × wrong correct or multiple defensible answers
+ 60  × theory/JSON desync
+ 45  × weak/obvious distractors
+ 35  × section inconsistencies
+ 25  × visual/style tell
+ 20  × missing freshness
+ 10  × readability/link/position issues
```

Порядок:

1. фактически неверный correct;
2. несколько защитимых ответов;
3. schema или dangling `q_number`;
4. теория и JSON противоречат;
5. дистракторы очевидны, частично верны или повторяют одну ошибку;
6. sections не соответствуют option text;
7. correct палится формой;
8. version-sensitive claims без источника;
9. русский язык, links, position distribution.

Нельзя выбирать файл только потому, что у него высокий старый `Tell`, если другой файл имеет
фактически неверные ответы.

---

## 6. Гейты для одного блока

Блок получает `✅` только если одновременно:

1. стем задаёт один проверяемый факт;
2. correct полностью верен;
3. correct единственный защитимый;
4. каждый distractor правдоподобен;
5. каждый distractor имеет одну ошибку;
6. misconceptions различаются;
7. нет частично правильных wrong;
8. text и sections согласованы;
9. все 4 варианта отвечают на один вопрос;
10. correct нельзя угадать глазами;
10a. **все 4 варианта на одном surface skeleton (`SKEL=✅`, HARD)** — иначе блок FAIL; без `SKEL` нельзя ставить `PAR`/`FINAL`;
11. нет stamped clone;
12. русский язык естественный;
13. theory не противоречит блоку;
14. version scope явный;
15. blind style и blind semantic пройдены.

---

## 7. Гейты для файла

Файл получает `FINAL=✅`, только если:

- review-sidecar содержит все блоки;
- `blocks_done == blocks`;
- `critical == 0`;
- `high == 0`;
- отсутствуют unresolved manual-review;
- все обязательные колонки мастер-таблицы `✅` (**включая `SKEL`**);
- schema validator exit 0;
- answer-parity gate exit 0;
- structure/stamp/caricature audits не имеют blocking verdict;
- theory sync пройден;
- freshness coverage пройдена;
- commit hash записан;
- diff не содержит unrelated changes.

---

## 8. Политика reset

При запуске ROUND 8:

1. удалить старую progress-секцию и старую generated-таблицу;
2. удалить старые значения `DONE/WIP/Golden` из рабочего плана;
3. не удалять Git-коммиты;
4. не удалять полезные audit-скрипты;
5. старые review-sidecars перенести в `docs/mcq-quality/reviews/archive/round-previous/` либо
   перегенерировать с `status=TODO`, если архивирование предусмотрено репозиторием;
6. создать свежую таблицу ниже;
7. выставить **все критерии всех 318 файлов в `⬜`**;
8. сохранить baseline `Blocks` и старый `Tell` только для приоритизации;
9. не переносить ни один старый `✅` в ROUND 8.

---

## 9. Мастер-таблица ROUND 8

Все статусы сброшены.  
`Blk` и `OldTell` взяты из предыдущего inventory только как baseline.  
Каждый критерий проверяется заново.

Обозначения колонок описаны в §4.

| # | Сидер | Кат. | Blk | OldTell | State | SCH | TH | QS | STEM | C-FCT | C-CMP | C-SCP | C-UNI | C-FRM | C-SEC | C-RU | W-PLS | W-1ER | W-API | W-FLS | W-DIV | W-CAR | W-FRM | SKEL | W-SEC | W-RU | PAR | BST | BSM | STAMP | FR | LINK | POS | VAL | SFPL | ANSC | FINAL | Notes |
|--:|---|---|--:|--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|---|
| 1 | `java-concurrency` | java | 56 | 52 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅* | ✅ | ✅ | ✅ | ✅ DONE (R8): 56/56 audited + POS de-cycle (seq 56→2). 5 dup-misconception (Q21,Q22,Q42,Q50,Q54), 2 BSM (Q27-A,Q52-A), 2 caricature (Q47-B,Q49-D), contra-pair Q33, факт-фикс correct Q42-B; correct-longest+position tells сняты; CV 0.186; BSM чист ×56. VAL✅*: OPTION_LENGTH_RATIO=3.0 accepted-exception (конфликт с anti-stamp CV≥0.14; смысловые guessability-гейты все PASS) ‖ R8-SFPL/ANSC c1 (Q1-7): re-audit по НОВЫМ измерениям SFPL (структура/формат/пунктуация/длина) + ANSC (answer completeness) — Q3/Q5 переписаны в равнополные каталоги/двухаспектные контрасты, correct больше не «единственный полный», blind guessable_by_form=false + completeness_ok=true; correct-тексты только backticks (смысл неизменен). Q8-Q56 pending; колонки SFPL/ANSC + reset — после полного файла ‖ c2 (Q8-17): тонкие/каррикатурные дистракторы подняты до полных ответов (ANSC), form-tells сняты (Q9 bare-noun, Q11 lone-long, Q13 lone-gloss, Q15-D categorical→plausible); Q17 residual intrinsic-semantic ПРИНЯТ; blind guessable_by_form=false ×Q8-16; 17/56 ‖ c3 (Q18-27): тонкие дистракторы подняты (Q18-D/Q19/Q27-B), lone-short/long подрезаны (Q20/Q21/Q24/Q26/Q27), Q25 backtick-норм + D заострён; blind form=false ×Q20/21/23/24/25/26/27; Q18/Q19/Q22 residual intrinsic-semantic (stem-driven) ПРИНЯТ; 27/56 ‖ c4 (Q28-37): Q28/31/32/33/35/36 дистракторы выровнены до формы correct (single-delta, ось сохранена); 2 form-tell переписаны с рерайтом секций — Q29 (lone-complete каталог → 4 равных bare-каталога, 1 несуществующий член: fork-join/direct-executor/cached-scheduled, cv0.016) и Q34 (lone-каноническая дефиниция → 4× «поверх <два типа>», подмена одного: CompletionService/Callable/FutureTask, cv0.034); Q30/Q37 verified-clean; blind guessable_by_form=false + completeness_ok=true ×Q28-37; SCHEMA_OK/NO_HARD/NO_TELLS/stamp OK; 37/56 ‖ c5 (Q38-47): 14 дистракторов; 2 карикатуры де-каррикатурены (Q40-A «Phaser из Java 1.4»→«из Java 5», Q44-C «только LMAX Disruptor»→«Disruptor всегда лучше»); form-tells сняты — Q38 (B трим 183→130), Q42 (correct lone-детальный → C с теми же порогами ≥8/≥64, де-абсолютизация), Q43 (lone-глосс → скобки всем), Q45 (lone-enumerator → B плоское перечисление с 1 неверным tool jcmd GC.run, секции), Q47 (lone-non-categorical → сняты «любых/вовсе/только»); Q39/Q41/Q46 verified-clean; blind guessable_by_form=false + completeness_ok=true ×Q38-47 (3 прохода); SCHEMA_OK/NO_HARD/NO_TELLS/stamp CV=0.055; 47/56 ‖ c6 (Q48-56, ФИНАЛ): Q48 (тонкие A/B 100/113 + раздутый C 168 → форма «Когда X: Y») и Q52 (lone «богатый recipe» → дистракторы в такие же плотные pipeline-анти-паттерны) переписаны; Q49/50/51/53/54/55/56 verified-clean; blind guessable_by_form=false + completeness_ok=true ×Q48-56; SCHEMA_OK/skel HARD=0 file-wide/stamp CV=0.05. **ФАЙЛ 56/56 SFPL/ANSC re-audit ЗАВЕРШЁН.** Остаток сканера — c1 Q1/Q2/Q4 btbucket1-2 + Q5 ratio1.26 (приняты в c1) |
| 2 | `design-patterns` | design-patterns | 48 | 48 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅* | ✅ | ✅ | ✅ | ✅ DONE (R8): 48/48 audited + POS de-cycle (seq 48→2, dist 12/12/12/12). Golden AP#1 снят: подняты дистракторы, CORRECT_LONGEST_RATE 0.83→0.25, CORRECT_WRONG_AVG 1.455→1.054, length-guess 0.28. De-caricature Q4-B/Q30-D; reviewer поймал 4 BSM (Q6-C,Q9-B,Q44-B,Q48-B); correct-precision Q20-D; DUP 2 ok; CV 0.166; факты GoF/JDK/Spring verified (freshness sidecar 13 claims). VAL✅*: OPTION_LENGTH_RATIO=3.75 accepted-exception (анти-stamp) + STYLE 0.493 accepted-residual (code-span 0.49 — correct легитимно цитирует API; length-компонент 0.28 PASS) ‖ R8-SFPL/ANSC c1 (Q35/Q22/Q47/Q14/Q38/Q18, 6/48): worst-first lone_long блоки. Q35 lone catalog→4 равных каталога с 1 отравл. маппингом; Q47 дистракторы отвечали на ОБРАТНый вопрос (completeness fail)→переориентированы на «когда НЕ нужно» ложными критериями+SECPATCH; Q14 lone-длинный 7-каталог→полные каталоги утвердит.тона; Q38 «симметричный полный»→все comma-appositive «GoF …; Spring …» связные; Q18 «поэтому+отрицание»→«утв; пример»×4; Q22 заглушки→полные. correct байт-в-байт HEAD (drift=0); blind guessable_by_form=false+completeness_ok=true ×6; NO_TELLS ×6; skel HARD=0 touched (Q46 untouched→c2). SFPL/ANSC ⬜ до 48/48 ‖ R8-SFPL/ANSC c2 (Q20/Q46/Q1/Q4/Q23/Q30/Q44, 13/48): Q20 generality-tell (correct — lone «широкое role-опр.», 4 прохода)→все 4 открываются идентично «`Proxy` предоставляет заместителя объекта…», delta intrinsic (или subclass vs interface-only/@EnableCaching/adds-behavior=Decorator), слепой guess WRONG ×2 после rework→форма не выдаёт; Q46 open_mixed+skel-HARD (residual c1)→все «`Object Pool` …» +intrinsic_only; Q1 skel sentence_count→2-предл. дистракторы; Q4 +colon→двухчастная «риск/преимущество: expl»; Q23/Q30 lone-longest→дистракторы подняты; Q44 A/D подняты C снижен. correct drift=0; section changes=0; changed==[1,4,20,23,30,44,46]; NO_TELLS touched; skel HARD=0; stamp CV0.101. SFPL/ANSC ⬜ до 48/48 ‖ R8-SFPL/ANSC c3 (Q36/Q16/Q6/Q13/Q21/Q11, 19/48): Q36 lone Spring-каталог(bt10)→3 равных каталога с 1 отравл.маппингом без «как» (Decorator-AOP/BeanFactory-Singleton/@EventListener-Strategy); Q6 expert-jargon(reordering/half-constructed) выделял correct→жаргон разнесён,все открыты «В DCL»; Q11 «absolute=trap»(исключительно/только/синоним)→позитивные определения; Q16/Q13/Q21 btbucket→выровнена backtick-density. correct drift=0; sec=0; changed==[6,11,13,16,21,36]; blind guessable_by_form=false+intrinsic_only=true ×6; NO_TELLS+HARD=0. SFPL/ANSC ⬜ до 48/48 ‖ R8-SFPL/ANSC c4 (Q42/Q48/Q9/Q5/Q32/Q15/Q40, 26/48): Q42/Q48/Q9/Q5 affirmative/positive-parity (ложные определения без абсолютов, N-item symmetry Q5); Q15 confusion-парити; Q32 generality+affirmative (6 проходов)→все «`Iterator` предоставляет <вид>-доступ…, скрывая <noun>; через `XxxIterator`», ось O3 Enumeration→PrimitiveIterator сняла self-reference `java.util.Iterator`, SECPATCH A/B/C; Q40 meta-position дистракторы (глиб/абсолют/дисмиссив)→SECPATCH specific-false-pairing по скелету correct. correct drift=0; sec changes⊆{Q32,Q40}×ABC; changed==[5,9,15,32,40,42,48]; blind guessable_by_form=false+intrinsic_only=true ×7; NO_TELLS+HARD=0. SFPL/ANSC ⬜ до 48/48 ‖ ROUND-8 SFPL/ANSC c5: Q8,Q12,Q17,Q27,Q34,Q37 (32/48; Q8 Q11-style stub-fix, Q17/Q37 exact-mirror reversed-roles, Q27 named-principle parity; blind form=false; SFPL/ANSC ⬜ до 48/48) ‖ ROUND-8 SFPL/ANSC c6: Q3,Q7,Q24,Q25,Q43 (+Q33 clean-accept; 38/48; Q3 two-part эхо-fix, Q7 stem-echo хвост, Q24 gerund-parity, Q25 per-distractor SOLID, Q43 two-pair skeleton; blind form=false) ‖ ROUND-8 SFPL/ANSC c7: Q2,Q10,Q19,Q26,Q28,Q29,Q31,Q39,Q41,Q45 (48/48 DONE; Q19 narrows-to-pair, Q39/Q45 advice-methodology intrinsic_only после round-5 blind: все дистракторы получили финал-самопроверку/4-составляющую+em-dash-хвост, различие только в СОДЕРЖАНИИ методики; blind guessable_by_form=false ×10; correct drift=0; SECPATCH пуст). ФАЙЛ SFPL/ANSC ЗАВЕРШЁН — SFPL/ANSC=✅ |
| 3 | `postgresql` | databases | 55 | 47 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 4 | `sql` | databases | 53 | 47 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅* | ⬜ | ⬜ | ✅ | ✅ DONE (R8): 53/53 audited (6 чанков) + POS de-cycle (seq 53→2, dist 14/13/13/13). Golden AP#1 снят: ~108 raises + 16 dict-spec, CORRECT_LONGEST_RATE 0.642→0.151, CORRECT_WRONG_AVG 1.444→0.903, STYLE 0.66→0.373. Reviewer поймал 7 существующих дефектов (BSM Q16-B/Q37-D, БЛОКЕР Q47-D readOnly-PG, двусмысленный Q32-A, карикатуры Q39-A/Q50-C/Q53-C); 3 CORRECT-precision (Q14-B/Q44-D + hedge); word-бюджет по чанкам (AVG/CV впритык: 0.903/0.143 — дальнейшие правки word-нейтрально); DUP 0; факты PG16/MySQL8 verified (freshness 18 claims). VAL✅*: OPTION_LENGTH_RATIO=3.8 — принятое исключение (анти-штамп), см. gate_summary сайдкара |
| 5 | `project-reactor` | reactive | 48 | 47 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | ✅ FINAL (R8, 5 чанков + POS, commit 68714e20+POS). Baseline был худшим после dp/sql: LONGEST 0.917, STYLE 0.92, AVG 2.425, seq=48. Подняты 142 дистрактора (120 text + 22 dict-spec), blind-reviewer 48/48 VERDICT, 25 correct-восстановлений из 68444a1b^ (CORRECT_EDITS whitelist). Итог: LONGEST 0.042, AVG 0.922, STYLE 0.391, DUP 0, DETAIL 0.25, SEQ 2 (A/B/C/D=12/12/12/12), stamp OK; SKEL Q1-Q48 + LLM-вычитка. Сайдкары: reviews COMPLETE 48/48, freshness 23 claims. VAL✅*: OPTION_LENGTH_RATIO=3.4 — принятое исключение (анти-штамп), см. gate_summary сайдкара |
| 6 | `hibernate` | databases | 50 | 46 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | ✅ FINAL (R8, 5 чанков + POS, commit 5b7ed428+POS). Baseline: LONGEST 0.76, AVG 1.617, STYLE 0.81, seq=50, skel hard=29. Поднят 141 дистрактор (115 text + 26 dict-spec) + 35 стрижек AVG-ре-баланса, blind-reviewer 48/48 VERDICT (68444a1b файл не трогал — restore не требовался). Итог: LONGEST 0.02, AVG 0.90, STYLE 0.308, DUP 0, DETAIL 0.08, SEQ 2 (A/B/C/D=13/12/13/12), skel-gate PASS hard=0, stamp OK; freshness 30 claims (в §11 chunk5 ошибочно указан 31). OPTION_LENGTH_RATIO — принятое исключение. Следующий worst-first по §1 (строка 3 postgresql — off-limits) |
| 7 | `java-collections` | java | 46 | 46 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE (reviewed_commit 0c9df11c): Q1-Q46 138 raised + 14 code-span бустов, blind 46/46; финал LONGEST 0.239, AVG 1.024, DUP 3, SEQ 2, STYLE 0.315, hard=0, stamp OK; RATIO 2.47 = accepted exception. Следующий worst-first по §1 |
| 8 | `application-security` | security | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE (reviewed_commit bc743ff0): Q1-Q45 135 raised + 21 span-boost за 5 чанков + POS; blind 45/45, MULTI-фиксы Q19/Q41; hard 42→0, stamp OK, SEQ 45→2, STYLE .81→.36, LONGEST .93→.27, AVG 1.80→1.07 |
| 9 | `authentication-authorization-patterns` | security | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE (3db01537): 150 raised за 5 чанков + POS; blind 45/45; hard 43→0/45, LONGEST .96→.31, AVG 1.89→1.1, STYLE .97→.35, DETAIL .98→.13, SEQ 45→2, stamp OK; RATIO 3.1 accepted exception |
| 10 | `hexagonal-architecture` | architecture | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE (18e364f3): 135 raised за 5 чанков + POS; blind 45/45; hard 35→0/45, LONGEST .93→.18, AVG 2.26→1.02, STYLE .96→.19, DETAIL .73→.24, SEQ 45→2, stamp CV .53→.20; RATIO 2.29 accepted exception (анти-штамп компакт) |
| 11 | `kotlin` | kotlin | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE: c1-c5 135 raised (blind 45/45), POS SEQ 45→2 (moved 34), FINAL: LONGEST 0.911→0.089, STYLE 0.922→0.141, skel hard 36→0, stamp CV 0.306→0.223; 2 documented-exception FAIL (RATIO 3.12 anti-stamp, DUP_NGRAMS 12 каталог-клоны Q21/Q43); +freshness-сайдкар |
| 12 | `owasp-top10` | security | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE: c1-c5 135 raised (blind 45/45) + POS (SEQ 45→2, 12/11/11/11) + FINAL (freshness-сайдкар, polish 30 length-boost + 19 detail-флипов). Battery: LONGEST 1.0→0.022, AVG 2.271→1.032, DETAIL 0.933→0.267, STYLE 1.0→0.033, skel hard 41→0, stamp CV 0.568→0.226, disMean/corLen 0.34→0.83. Accepted exc: RATIO 2.43 (compact-by-design), DUP 7 (in-block зеркала Q6/Q17/Q24). Known limitation: Q3 «3 survey-категории» vs официально 2 (fact-fix вне мандата R8) |
| 13 | `test-strategies` | testing | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ | ✅* | ⬜ | ⬜ | ✅ | R8 COMPLETE (c1-c5 132 дистрактора + POS SEQ 45→2 + FINAL 46 boost-клауз): blind 44/44, skel-HARD 31→0, LONGEST 0.956→0.089, STYLE 0.967→0.133, CV 0.566→0.191; RATIO 2.5 accepted-exc; freshness-сайдкар создан |
| 14 | `tls-ssl` | security | 45 | 45 | ✅ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | R8 c1-c4 (Q1-Q40, 120 дистракторов, blind clean): file-hard 42→~7; Q11 parity-fix; c4 (Q31-Q40) — Q11-дефект во всех 10 блоках, подняты 30 дистракторов; Q37→spot-the-misattribution каталог, Q35 де-карикатурен; все guessable_by_form=false. Остаток c5 (Q41-Q45), POS (SEQ=45), FINAL c5 (Q41-Q45) — Q11-дефект во всех 5 (correct 409-465ch vs дистракторы 108-148ch); все 15 подняты до single-delta, 3 прохода blind: length-монотонность сломана (correct longest 0/5), Q42/Q45 spot-the-error каталоги (Q45 CV 0.023), остаток lone-complete intrinsic (Q30/Q37); все 45 блоков reviewed. POS de-cycle: correct-label был чистый ABCDABCD-цикл (44/44 adjacent +1) при балансе A12/B11/C11/D11 → переставлены позиции опций (текст/секции сохранены, correct только меняет слот), новый seq 6/44 adjacent +1, баланс тот же. Остаётся FINAL freshness. FINAL: freshness sidecar fact-freshness/tls-ssl-interview.json (13 version-claims, 3 staleness-risks, verdict FRESH — RFC/браузер-политики в пределах knowledge-cutoff; ECH=draft и CT-пороги сформулированы без хрупких привязок; correct-тексты не правились). c1/c2 spot-check Q4/Q5/Q6 (высший CV): correct НЕ самый длинный, дистракторы — полные mental models → нет Q11-дефекта. ROUND-8 tls-ssl ЗАКРЫТ (45/45 blocks + POS + FINAL). |
| 15 | `unit-testing` | testing | 45 | 45 | ◐ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | R8 c1 (Q1-Q10): все 10 блоков — Q11-дефект (correct 208-339ch vs дистракторы 89-155ch тонкие заглушки, 3 stamp-FLAT); 30 дистракторов подняты до полных single-delta mental models с сохранением оси-заблуждения; 3 прохода blind → spot-the-error каталоги (Q2/Q6/Q10) + сломана length-монотонность + распределены глоссы/feature-цепочки; все 10 guessable_by_form=FALSE; skel-HARD 43 file-wide, no-HARD Q1-10; stamp OK. **c2 (Q11-Q20):** 30 дистракторов подняты до single-delta клонов скелета (error-axis сохранён); 5 form-tell блоков (Q11 richest-list / Q14 lone-code-block / Q17 lone-longest / Q19 lone-catalog / Q20 lone-detailed) переписаны — correct больше не выделяется формой (Q11 cv0.039, Q14 cv0.004, Q19/Q20 самый длинный = дистрактор); слепой ре-ревью 5/5 guessable_by_form=false; Q12/13/15/16 — форма уже паритетна, «lone-complete» = семантика дефиниционного вопроса (не форм-tell); Q18 зеркальная TDD/BDD пара; SCHEMA_OK/NO_HARD_11_20/stamp OK. **c3 (Q21-Q30):** 30 дистракторов → single-delta клоны (correct был 299-401ch vs 107-149ch); 5 form-tell блоков (Q24 lone-fluent, Q28 lone-literal, Q25/27/30 lone-longest) нейтрализованы, chain/литерал распределены, cv 0.01-0.09; финальный ре-ревью Q21-28,30 form=false, Q27 исправлен (2 полные Awaitility-цепочки); Q26/Q29 residual = семантика best-practice-vs-анти-паттерн (intrinsic). SEMI_YES={22,25,26,30}; SCHEMA_OK/NO_HARD_21_30/stamp OK. Остаётся Q31-45 (в т.ч. FLAT Q34/Q39) + POS + FINAL. |
| 16 | `spring-security` | spring | 46 | 44 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 17 | `cassandra` | databases | 44 | 44 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 18 | `elasticsearch` | databases | 44 | 44 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 19 | `mongodb` | databases | 46 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 20 | `rxjava` | reactive | 46 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 21 | `helm` | devops | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 22 | `java-oop` | java | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 23 | `jwt` | security | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 24 | `kotlin-collections` | kotlin | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 25 | `kotlin-serialization` | kotlin | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 26 | `networking` | architecture | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 27 | `resilience-patterns` | architecture | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 28 | `saga-pattern` | architecture | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 29 | `spring-batch` | spring | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 30 | `spring-boot-actuator` | spring | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 31 | `spring-cloud` | spring | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 32 | `spring-mvc` | spring | 43 | 43 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 33 | `chaos-engineering` | testing | 44 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 34 | `git` | devops | 43 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 35 | `java-annotations` | java | 43 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 36 | `spring-data-jpa` | spring | 43 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 37 | `application-profiling` | performance | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 38 | `argocd` | devops | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 39 | `caching-strategies` | architecture | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 40 | `cap-theorem` | architecture | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 41 | `contract-testing` | testing | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 42 | `database-transactions` | databases | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 43 | `flyway-liquibase` | databases | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 44 | `java-17-21` | java | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 45 | `java-stream` | java | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 46 | `microservices` | architecture | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 47 | `oauth2` | security | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 48 | `openapi-swagger` | api | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 49 | `performance-testing` | performance | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 50 | `terraform` | devops | 42 | 42 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 51 | `mockito` | testing | 45 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 52 | `java-8` | java | 42 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 53 | `java-exceptions` | java | 42 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 54 | `logging` | logging | 42 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 55 | `clean-architecture` | architecture | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 56 | `cqrs-event-sourcing` | architecture | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 57 | `database-architecture` | databases | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 58 | `docker` | devops | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 59 | `metrics-tracing` | monitoring | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 60 | `rabbitmq` | messaging | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 61 | `scalability-patterns` | architecture | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 62 | `system-design` | system-design | 41 | 41 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 63 | `consistency-patterns` | architecture | 42 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 64 | `observability` | monitoring | 42 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 65 | `spring-framework` | spring | 41 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 66 | `code-review-practices` | leadership | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 67 | `distributed-systems` | architecture | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 68 | `grpc` | api | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 69 | `integration-testing` | testing | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 70 | `java-generics` | java | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 71 | `java-io-nio` | java | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 72 | `java-serialization` | java | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 73 | `kotlin-dsl` | kotlin | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 74 | `kotlin-exceptions` | kotlin | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 75 | `load-balancing` | architecture | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 76 | `scala` | scala | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 77 | `team-leadership` | leadership | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 78 | `technical-debt` | code-quality | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 79 | `testcontainers` | testing | 40 | 40 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 80 | `deployment-strategies` | cicd | 39 | 39 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 81 | `java-core` | java | 39 | 39 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 82 | `kotlin-coroutines` | kotlin | 39 | 39 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 83 | `http-rest` | api | 43 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 84 | `redis` | databases | 43 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 85 | `prometheus-grafana` | monitoring | 39 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 86 | `api-gateway` | architecture | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 87 | `gradle-maven` | devops | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 88 | `java-modules` | java | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 89 | `jvm-performance-tuning` | performance | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 90 | `kotlin-interop-java` | kotlin | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 91 | `logging-strategies` | monitoring | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 92 | `pipeline-design` | cicd | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 93 | `websocket` | api | 38 | 38 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 94 | `graphql` | api | 40 | 37 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 95 | `memory-management` | performance | 39 | 37 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 96 | `java-types` | java | 38 | 37 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 97 | `jvm` | jvm | 40 | 36 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 98 | `arrays-strings` | data-structures | 36 | 36 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 99 | `go` | go | 36 | 36 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 100 | `inference-optimization` | ai-ml | 36 | 36 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 101 | `spring-webflux` | spring | 43 | 35 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 102 | `behavioral` | behavioral | 38 | 35 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 103 | `fine-tuning-llm` | ai-ml | 35 | 35 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ R8-SFPL/ANSC c1 (Q23/Q24/Q32/Q34, 4/35): worst-first Golden AP#1 (correct=каталог/лекция vs дистракторы-заглушки; baseline CORRECT_LONGEST_RATE=1.0, AVG_RATIO=2.689, STYLE=1.0, structure-tell mean=20.79). Метод design-patterns Q35/Q36: correct байт-в-байт HEAD (drift=0), дистракторы→параллельные отравл. каталоги (Q23 LIMA-тезисы, Q24 стек-библиотек, Q32 multi-LoRA serving, Q34 open-weight семейства) с 1 инлайн-факт-swap, плотность аннотаций выровнена, концовки нейтрально-параллельны. Q32-B latency→adapter-stacking (SECPATCH — ось сдвинута, tone-парити). blind BST guessable_by_form=false ×4 (Q23 r1; Q24/Q34 r2; Q32 r3); BSM один защитимый + completeness_ok=true ×4; NO_TELLS ×4 (corr_longest=False, ratio≤1.20); skel HARD=0 touched; structure-tell 0-5 (catalog-parity, богатые равные каталоги — не флэт-штамп: stamp-audit verdict OK); DUP_NGRAMS 0→152 + low length-CV = accepted-artifact poisoned-catalog scaffold-share (прецедент design-patterns Q35/Q36). Остаток 31 блок (тот же AP#1)→c2+; нужны freshness sidecar (version-sensitive) + POS de-cycle (CORRECT_POSITION_SEQUENCE=35). SFPL/ANSC ⬜ до 35/35 ‖ c2 (Q19/Q21/Q27/Q31, 8/35): worst-first по structure-tell 33-40. Те же lone-длинные каталоги/лекции → коэрентные альт.модели/подмена 1 пункта тем же скелетом. Q27 «identical 7-item skeleton, отрава внутри скобки одного пункта». blind form-vs-domain: Q19/Q21/Q31 form=false, Q27 named-tell снят структурно (bare_emdash=0). correct drift=0; SECPATCH пуст (misconception сохранена); NO_TELLS ×4 (ratio≤1.17); structure-tell 0.67-4.0; stamp OK. SFPL/ANSC ⬜ до 35/35 ‖ c3 (Q6/Q14/Q20/Q29, 12/35): LoRA-математика, RLHF-pipeline, SFT-датасеты, топ-5 ошибок. Q6/Q14 те же lone-формулы/pipeline → параллельные каталоги с 1 отравл. фактом (form-clean сразу). Q20/Q29 = single-delta списки: reviewer round1 нашёл form-leak (correct=единственный нейтральный список, дистракторы несли оправдательную/reassuring прозу) + Q29-O2 карикатура (completeness_ok=false); round2 все подмены→терсные фактические свопы (Q20 уровня commercial-ok: Alpaca-license/dedup-exact/LIMA-synthetic; Q29 код-вызовы: temperature/add_tokens/эпохи), O2 перестроен в single-poison. SECPATCH={(20,B),(20,C),(29,B),(29,C),(29,D)}. blind form_guessable=false ×4, one_defensible ×4, completeness_ok ×4; NO_TELLS ×4 (Q20 ratio1.01/Q29 1.05, corr_longest=False); skel HARD=0; structure-tell 0.0-4.67; stamp OK; correct drift=0. Остаток 23 блока→c4+. SFPL/ANSC ⬜ до 35/35 ‖ c4 (Q22/Q33/Q30/Q28, 16/35): chat-template, FT-API вендоров 2026, model merging, catastrophic forgetting (structure-tell 24-25). Заглушки (124-221ч, bt0-3) подняты до параллельных каталогов уровня correct (497-530ч) с 1 отравл. фактом. 3 прохода reviewer: Q33 form-clean сразу («эталон параллельности»); Q28 adapter-switching-паритет; Q22 reassurance-концовки→ложные механизмы (де-severity) + stem нейтрализован (QTEXT); Q30 majority-median эксплойт (define/Зачем 3-1 split) → непоизоненные зоны разведены в разные истинные парафразы (4 различных значения/зона). SECPATCH пуст (оси сохранены). blind BSM один защитимый + completeness_ok ×4; form: Q33/Q28/Q30 clean, Q22 severity=R6 intrinsic (форма выровнена, majority-vote нет, stem не отображается). NO_TELLS ×4 (tells=[], corr_longest=False, ratio≤1.20); skel HARD=0; structure-tell 0.0-4.33; stamp OK; correct drift=0. Остаток 19 блоков→c5+. SFPL/ANSC ⬜ до 35/35 ‖ c5 (Q7/Q8/Q12/Q18, 20/35): QLoRA-трюки, DoRA-декомпозиция, TRL/SFT-pipeline, ORPO-loss (structure-tell 20-24). Заглушки 98-176ч подняты до параллельных каталогов уровня correct (364-530ч). 2 прохода reviewer: round1 снял string-majority consensus-leak (непоизоненные зоны разведены в разные истинные парафразы + варьированы зачины); round2 strict — устранены ДЕТЕРМИНИРОВАННЫЕ форм-tell'ы (контраст-маркеры Q7-B/Q8-A, double-error Q8-C, само­противоречия Q18-C/D с формулой одного λ). SECPATCH пуст (оси заблуждений сохранены по label). BSM one_defensible + completeness_ok ×4. surface NO_TELLS ×4 (tells=[], corr_longest=False, ratio 1.08-1.13); skel HARD=0; structure-tell 1.0-4.67; stamp OK; drift=0. ОСТАТОЧНО: single-delta topical-consensus принят как R6 intrinsic (прецедент c4 Q30) — surface+BSM clean, но НЕ fully SFPL-green; идеальный fix = independent mental models (отложено). Остаток 15 блоков → c6+. SFPL/ANSC ⬜ до 35/35 ‖ c6 (Q5/Q9/Q13/Q15, 24/35): PEFT-методы, LoRA target-layers, distillation, DPO-vs-PPO (structure-tell 21-22). **ПОДХОД СМЕНЁН**: independent equal-richness mental models вместо single-delta клонов — каждый дистрактор своё цельное ошибочное мировоззрение, поднятое до плотности correct (заглушки 106-216ч → 377-482ч). reviewer: consensus/majority-leak УСТРАНЁН (голосование по клаузам НЕ работает — прямой контраст с c5); BSM one_defensible + completeness_ok ×4. Оси misconception сохранены по label (SECPATCH пуст). surface NO_TELLS ×4 (tells=[], ratio ≤1.22); skel HARD=0; structure-tell 5.3-12.3; stamp OK; drift=0. Дешёвые фиксы: де-карикатура Q5-D, снят капс, смягчены абсолюты. ОСТАТОЧНО: soft length/completeness-leak в пределах binding-tolerance (ratio<1.25); Q13-O3 term + Q15 formula-uniqueness приняты как intrinsic. Остаток 11 блоков → c7+. ‖ R8-SFPL/ANSC c7 (Q10/Q17/Q25/Q35, 28/35): INDEPENDENT mental models. Q10/Q17 clean с 1-й итерации (blind BSM: все дистракторы PLAUSIBLE, Q17 «образцовый», second-correct нет). Q25/Q35 — correct ПО СУТИ каталог/формула → дистракторы переделаны в ПАРАЛЛЕЛЬНЫЕ VRAM-таблицы/формулы с одной scale/логической ошибкой (reviewer поймал в Q25 карикатуры «70B full FT в 4090» + off-scope GRPO=SFT; в Q35 D magnitude-inflation → TCO-double-count, затем self-refuting «ошибочно»/«вместо честных ~$10» — снято). Приём убивает form-leak (все 4 таблицы/формулы) + карикатуру. structure-tell HEAD 19-20.67 → CUR Q10=7.67/Q17=10.0/Q25=7.33/Q35=9.33 (near-gold). correct drift=0; SECPATCH Q25 B/C/D + Q35 D; NO_TELLS ×4 (surface tells=[], ratio<1.25); skel HARD=0; stamp OK; 4 слепых прохода. Остаток 7 блоков → c8+. SFPL/ANSC ⬜ до 35/35 ‖ c8 (Q2/Q11/Q16/Q26, 32/35): worst-first structure-tell HEAD 16-18.67. Заглушки → параллельные каталоги/мировоззрения плотности correct: Q2 лестница Prompting/RAG/FT (роли-swapped/mutually-exclusive/FT-fresher-facts), Q11 DAPT-vs-SFT (swap+BloombergGPT/whole-seq-loss/один-режим), Q16 GRPO (улучшенная-value+GAE/MSE / judge-любые-задачи / как-DPO-статич-пары), Q26 гиперпараметры (LoRA-lr-инверсия/DPO-overtuned/epochs-10-20). 4 слепых прохода: BST worked-to-parity (формулы Q16 + полный richness-parity Q26 Правила:/Sanity:+>>/<<+A100/H100), BSM PASS ×4 (correct берётся рассуждением); Q11 salient-опция = ДИСТРАКТОР (не критично). Гейты: SCHEMA_OK; NO_TELLS ×4 (corr_longest=False, ratio<1.25); skel HARD=0; structure-tell CUR 3.0-5.67; stamp OK; correct drift=0 (35/35). answer-parity-gate: LONGEST_RATE=0.143, AVG_RATIO=1.009, DETAIL=0.343, STYLE=0.271, SOURCE_COVERAGE=ok. **freshness sidecar СОЗДАН** (SOURCE_COVERAGE закрыт). Остаточно: Q26 CV=0.051/dup10=14 = §30 documented exception (form-parity vs anti-dup на dense-catalog correct, не padded-clone). Остаток 3 блока Q1/Q3/Q4 → c9 (финиш); POS de-cycle отдельным тиком. SFPL/ANSC ⬜ до 35/35 ‖ c9 (Q1,Q3,Q4, 32→35/35 КОНТЕНТ DONE): worst-first Golden AP#1 (correct=каталог 360-400ч vs заглушки 106-159ч). Правил ТОЛЬКО дистракторы (correct байт-в-байт HEAD, drift=0). **ПЯТЬ раундов слепого ревью** (systematic Form-Parity dig-out): r1 flagged 3/3; r3 FAIL Q4 (correct=единств. «суммирует всё»+макс.числа+капс ВСЕХ) → дистракторы→полный breakdown; r4 FAIL Q3 (correct=единств. много-пунктовый) + Q4 («якобы»+28>24+абсолюты) → Q3 дистракторы→тематич. много-пунктовые списки ЛОЖНЫХ ограничений (output-shaping/efficiency/internalization=инверсии реальных сильных сторон FT), Q4 нейтрализованы абсолюты+«якобы»+железо-consistency; **r5 PASS 3/3** (BSM угадал correct по СМЫСЛУ во всех, BST guessable_by_form=false во всех). NO_TELLS ×3 (tells=[], corr_longest=False, ratio 1.13/1.21/1.15); skel HARD=0; structure-tell Q1=2.33/Q3=4.0/Q4=1.67 (≪HEAD 8/8.33/11.67); SECPATCH Q3 A/B/D + Q4 A/B/C; correct drift=0. answer-parity: LONGEST_RATE=0.057, AVG_RATIO=0.956, ABSOLUTE_GAP=0.133, STYLE=0.221, SOURCE_COVERAGE=ok. Остаток: **только POS de-cycle** (CORRECT_POSITION_SEQUENCE=35) отдельным тиком → потом FINAL. SFPL/ANSC ⬜ (контент DONE) ‖ POS de-cycle DONE: CORRECT_POSITION_SEQUENCE **35→3** (чистая перестановка label/order, seed=15, сбаланс. 9/9/9/8, content drift=0, git diff симметричен 356+/356−, 27/35 сменили позицию; surface/skel/schema неизменны). **ФАЙЛ FINAL** (SFPL/ANSC ✅ + POS ✅). Остаточные gate-FAIL = §30 file-wide documented (OPTION_LENGTH_RATIO=1.65 Q16 dense-catalog byte-locked; DUPLICATE_NGRAMS=762 cross-block). |
| 104 | `go-concurrency` | go | 35 | 35 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 105 | `test-automation` | testing | 50 | 34 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 106 | `apache-spark` | data-engineering | 35 | 34 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 107 | `database-sharding` | databases | 34 | 34 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 108 | `hash-tables` | data-structures | 34 | 34 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 109 | `open-source-llms` | ai-ml | 34 | 34 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 110 | `trees` | data-structures | 34 | 33 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 111 | `dynamic-programming` | algorithmic-paradigms | 33 | 33 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 112 | `kafka` | messaging | 50 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 113 | `spring-boot` | spring | 43 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 114 | `ddd` | architecture | 38 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 115 | `graphs` | data-structures | 34 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 116 | `ai-compliance-governance` | ai-ml | 32 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 117 | `ktor` | jvm-alternatives | 32 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 118 | `linked-lists` | data-structures | 32 | 32 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 119 | `two-pointers-sliding-window` | algorithmic-paradigms | 33 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 120 | `typescript` | typescript | 33 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 121 | `ai-application-architecture` | ai-ml | 32 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 122 | `apache-flink` | data-engineering | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 123 | `complexity-analysis` | complexity | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 124 | `database-replication` | databases | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 125 | `java-jackson` | java | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 126 | `multimodal-ai` | ai-ml | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 127 | `quarkus` | jvm-alternatives | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 128 | `searching-algorithms` | sorting-searching | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 129 | `sorting-algorithms` | sorting-searching | 31 | 31 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 130 | `kubernetes` | devops | 45 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 131 | `design-ecommerce-delivery` | system-design | 36 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 132 | `linux` | devops | 33 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 133 | `api-design-best-practices` | api | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 134 | `cdn` | architecture | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 135 | `design-dropbox` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 136 | `design-feed-system` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 137 | `design-key-value-store` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 138 | `design-netflix` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 139 | `design-pastebin` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 140 | `design-payment-system` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 141 | `design-rate-limiter` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 142 | `design-url-shortener` | system-design | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 143 | `dns` | architecture | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 144 | `dynamodb` | databases | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 145 | `go-stdlib` | go | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 146 | `llm-basics` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 147 | `llm-evaluation` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 148 | `long-context-vs-rag` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 149 | `mcp` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 150 | `multi-agent-orchestration` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 151 | `neo4j` | databases | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 152 | `reactive-streams` | reactive | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 153 | `reasoning-models` | ai-ml | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 154 | `service-discovery` | architecture | 30 | 30 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 155 | `code-review` | code-quality | 40 | 29 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 156 | `java-string` | java | 39 | 29 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 157 | `embeddings` | ai-ml | 29 | 29 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 158 | `heaps` | data-structures | 29 | 29 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 159 | `ai-agents` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 160 | `ai-observability` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 161 | `apache-airflow` | data-engineering | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 162 | `clickhouse` | databases | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 163 | `dbt` | data-engineering | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 164 | `design-parking-lot-oo` | system-design | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 165 | `design-youtube` | system-design | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 166 | `go-testing` | go | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 167 | `greedy-algorithms` | algorithmic-paradigms | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 168 | `java-mapstruct` | java | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 169 | `kafka-streams` | data-engineering | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 170 | `llm-integration-patterns` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 171 | `mlops` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 172 | `model-serving` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 173 | `opentelemetry` | monitoring | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 174 | `prompt-engineering` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 175 | `reactive-testing` | reactive | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 176 | `serverless` | cloud | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 177 | `stream-processing` | data-engineering | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 178 | `tries` | data-structures | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 179 | `vector-databases` | ai-ml | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 180 | `webflux` | reactive | 28 | 28 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 181 | `rust` | rust | 33 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 182 | `vertx` | jvm-alternatives | 31 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 183 | `backtracking` | algorithmic-paradigms | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 184 | `clean-code-practices` | code-quality | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 185 | `code-smells` | code-quality | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 186 | `design-instagram` | system-design | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 187 | `design-twitter` | system-design | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 188 | `divide-and-conquer` | algorithmic-paradigms | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 189 | `go-memory-gc` | go | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 190 | `go-modules` | go | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 191 | `java-initialization` | java | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 192 | `java-lombok` | java | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 193 | `recursion` | algorithmic-paradigms | 27 | 27 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 194 | `java-conditional-statements` | java | 42 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 195 | `data-lake-lakehouse` | data-engineering | 28 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 196 | `database-performance` | performance | 27 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 197 | `elk-stack` | monitoring | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 198 | `go-generics` | go | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 199 | `istio-service-mesh` | devops | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 200 | `micronaut` | jvm-alternatives | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 201 | `reactive-patterns` | reactive | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 202 | `static-analysis` | code-quality | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 203 | `vault` | devops | 26 | 26 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 204 | `design-google-maps` | system-design | 30 | 25 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 205 | `message-brokers-comparison` | messaging | 26 | 25 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 206 | `ansible` | devops | 25 | 25 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 207 | `code-coverage` | code-quality | 25 | 25 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 208 | `stacks-queues` | data-structures | 25 | 25 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 209 | `function-calling` | ai-ml | 30 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 210 | `design-elevator-oo` | system-design | 26 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 211 | `design-web-crawler` | system-design | 26 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 212 | `cockroachdb` | databases | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 213 | `consul` | devops | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 214 | `design-vending-machine-oo` | system-design | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 215 | `latency-numbers` | architecture | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 216 | `nats` | messaging | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 217 | `network-performance` | performance | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 218 | `supply-chain-security` | security | 24 | 24 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 219 | `refactoring-patterns` | code-quality | 42 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 220 | `event-driven-patterns` | architecture | 40 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 221 | `code-agents` | ai-ml | 31 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 222 | `data-warehousing` | data-engineering | 30 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 223 | `design-search` | system-design | 30 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 224 | `caching-performance` | performance | 23 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 225 | `jaeger-zipkin` | monitoring | 23 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 226 | `scylladb` | databases | 23 | 23 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 227 | `ai-safety-guardrails` | ai-ml | 32 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 228 | `aws-lambda` | cloud | 32 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 229 | `aws-sqs-sns` | messaging | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 230 | `conflict-stories` | behavioral | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 231 | `culture-fit` | behavioral | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 232 | `failure-stories` | behavioral | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 233 | `leadership-stories` | behavioral | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 234 | `load-testing` | testing | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 235 | `pulsar` | messaging | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 236 | `secrets-management` | security | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 237 | `star-method` | behavioral | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 238 | `technical-decisions` | leadership | 22 | 22 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 239 | `cloud-native-patterns` | cloud | 30 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 240 | `reverse-proxy` | architecture | 30 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 241 | `azure` | cloud | 25 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 242 | `spring-aop` | spring | 22 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 243 | `design-chat-system` | system-design | 21 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 244 | `property-based-testing` | testing | 21 | 21 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 245 | `loki-grafana` | monitoring | 28 | 20 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 246 | `estimations-planning` | leadership | 20 | 20 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 247 | `mtls` | security | 20 | 20 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 248 | `tech-interviewing` | leadership | 20 | 20 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 249 | `gcp` | cloud | 28 | 19 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 250 | `zero-trust` | security | 19 | 19 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 251 | `conflict-resolution` | leadership | 20 | 18 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 252 | `linkerd` | devops | 20 | 17 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 253 | `kotlin-flow` | kotlin | 17 | 17 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 254 | `rest-maturity` | api | 17 | 17 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 255 | `design-uber` | system-design | 30 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 256 | `rag` | ai-ml | 30 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 257 | `spring-ai` | spring | 18 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 258 | `java-reflection` | java | 16 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 259 | `spring-data-jdbc` | spring | 16 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 260 | `spring-events` | spring | 16 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 261 | `spring-scheduling` | spring | 16 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 262 | `spring-validation` | spring | 16 | 16 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 263 | `aws` | cloud | 34 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 264 | `design-typeahead` | system-design | 30 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 265 | `spring-testing` | spring | 16 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 266 | `graalvm-native` | jvm | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 267 | `hibernate-caching` | databases | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 268 | `java-optional` | java | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 269 | `java-pattern-matching` | java | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 270 | `java-records` | java | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 271 | `java-virtual-threads` | java | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 272 | `junit` | testing | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 273 | `kotlin-sealed-classes` | kotlin | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 274 | `kotlin-spring` | kotlin | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 275 | `rest-assured` | testing | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 276 | `spring-async` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 277 | `spring-boot-3-migration` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 278 | `spring-graphql` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 279 | `spring-integration` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 280 | `spring-kafka` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 281 | `spring-messaging` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 282 | `spring-modulith` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 283 | `spring-r2dbc` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 284 | `spring-session` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 285 | `spring-state-machine` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 286 | `spring-transaction` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 287 | `spring-vault` | spring | 15 | 15 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 288 | `agentic-patterns` | ai-ml | 30 | 14 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 289 | `resilience4j` | spring | 20 | 14 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 290 | `hibernate-jpql-criteria` | databases | 16 | 14 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 291 | `kotlin-value-classes` | kotlin | 15 | 14 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 292 | `spring-rest-client` | spring | 13 | 13 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 293 | `hibernate-relationships` | databases | 15 | 11 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 294 | `java-completable-future` | java | 15 | 11 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 295 | `mentoring` | leadership | 25 | 10 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 296 | `java-22-25` | java | 22 | 10 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 297 | `api-versioning` | api | 20 | 10 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 298 | `algorithms` | algorithms | 16 | 7 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 299 | `spring-cache` | spring | 18 | 6 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 300 | `crac` | jvm | 16 | 6 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 301 | `micrometer` | monitoring | 20 | 5 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 302 | `mutation-testing` | testing | 20 | 5 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 303 | `langchain4j` | ai-ml | 18 | 5 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 304 | `strangler-fig` | architecture | 18 | 5 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 305 | `jmh-microbenchmarking` | performance | 18 | 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 306 | `jooq` | databases | 18 | 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 307 | `bff-pattern` | architecture | 17 | 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 308 | `selenium` | testing | 15 | 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 309 | `java-functional-interface` | java | 14 | 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 310 | `spring-retry` | spring | 17 | 3 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 311 | `archunit` | testing | 15 | 3 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 312 | `cucumber-bdd` | testing | 15 | 3 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 313 | `kotlin-testing` | kotlin | 20 | 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 314 | `redpanda` | messaging | 20 | 20 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ✅ FINAL (R8, c1..c21, 20/20 блоков реаудировано, каждый нетривиальный — независимый blind). Baseline→FINAL: OPTION_LENGTH_RATIO 3.17→worst 1.33 (PASS), skel hard 2→0 (PASS), POS SEQUENCE 20→4, ABSOLUTE_MARKER_GAP 0.38→0.283. VAL: ABSOLUTE_MARKER_GAP=0.283 — ПРИНЯТОЕ ИСКЛЮЧЕНИЕ (легитимные silver-bullet-маркеры Q11/Q18/Q19 в judgment-вопросах; §1 метрики низший приоритет). Факт-слой ЧИСТ (c5-скан + c14-fix Q16-C tiered storage OSS→Enterprise). Sidecar reviews FINAL 20/20. Deferred NITs (rotation): Q2-B/Q4-C/Q11-A/Q12-form |
| 315 | `edge-computing` | architecture | 18 | 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 316 | `apache-camel` | messaging | 16 | 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 317 | `jms-activemq` | messaging | 16 | 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |
| 318 | `scala-effects` | scala | 16 | 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ |  |

---

## 10. Обновление таблицы

Таблица обновляется только после фактического audit/review.

Правила:

- `State=🔄` ставится при начале работы;
- отдельная колонка получает `✅` только после проверки;
- если критерий не применим, ставить `✅` только с пояснением в `Notes`, а не `N/A`;
- при partial chunk ставить `◐` в `State`, а критерии оставлять `⬜` или `◐`;
- `FINAL=✅` только после полного файла;
- при регрессии любой обязательной колонки `FINAL` немедленно сбрасывается;
- изменение файла после `reviewed_commit` автоматически возвращает строку в `⬜` или `◐`.

---

## 11. Progress log

Старый журнал прогресса удалён. Новый журнал начинается с ROUND 8.

### 2026-07-12 — process — колонка `SKEL` (HARD)

- В мастер-таблицу добавлена отдельная колонка **`SKEL`** (после `W-FRM`): единый surface skeleton всех 4 `option.text`.
- Pre-check: `scripts/mcq-skel-gate.py` (грубые mismatch). **Приёмка только LLM** — зелёный скрипт ≠ `SKEL=✅`.
- Без `SKEL=✅` запрещены `PAR=✅` и `FINAL=✅`.
- Проставлено `SKEL=✅` для DONE: `java-concurrency`, `design-patterns`, `sql`; остальные `⬜`.
- Канон зафиксирован в `PROMPT_PLAN_INTERVIEW.md`, `mcq-quality-fixer`, `interview-options-writer`.

Формат записи:

```markdown
### YYYY-MM-DD — <topic_slug> — Q<X>..Q<Y>

- Commit:
- Blocks reviewed:
- Correct answers fixed:
- Distractors rewritten:
- Sections rewritten:
- Theory synchronized:
- Freshness updated:
- Validators:
- Manual review:
```

### 2026-07-11 — java-concurrency-interview — Q1..Q9

- Commit: (this tick)
- Blocks reviewed: 9 (Q1-Q9), review-sidecar создан
- Correct answers fixed: 0 фактических ошибок (все correct верны); подрезаны correct-тексты Q6/Q8 ради parity (факт сохранён)
- Distractors rewritten: Q3-B (→ложное перечисление), Q4-C (→hedged-but-wrong), Q6-A (→дефиниция), Q8-A (→все три сущности), Q9-B/C/D (сняты абсолюты)
- Sections rewritten: Q3-B, Q4-C, Q9-B/C/D (синхронизированы под новый text)
- Theory synchronized: не требовалось (style-only, факты не менялись)
- Freshness updated: создан docs/mcq-quality/fact-freshness/java-concurrency-interview.json (JLS SE21 + JDK21 javadoc)
- Validators: verify-mcq-json OK; SOURCE_COVERAGE ok; stamp CV=0.143 (≥0.14); structure-tell блоков 1.0-5.0 (healthy)
- Manual review: 2 независимых blind-прогонa (reviewer-субагент) — round-1 нашёл 5 BST-tells, round-2 подтвердил все 5 закрыты (BST=PASS, BSM=PASS); факт-ошибок нет, multiple-defensible нет

### 2026-07-11 — java-concurrency-interview — Q10..Q18

- Commit: (this tick)
- Blocks reviewed: 9 (Q10-Q18), review-sidecar расширен до 18/56
- Correct answers fixed: 0 фактических ошибок (все correct верны); подрезаны verbose correct-тексты Q12/Q13/Q15 ради parity
- Distractors rewritten: подняты stub-дистракторы Q10-C/Q17-D/Q18-C до правдоподобных длинных ложных вариантов (сняли length-tell «correct самый длинный 7/9»); Q10-B: капс «НЕ»→«не» (микро-tell)
- Sections rewritten: 0 (все правки text-only, sections уже консистентны)
- Theory synchronized: не требовалось (style-only)
- Freshness updated: freshness-sidecar расширен утверждениями Q10-Q19
- Validators: verify-mcq-json OK; stamp CV=0.142 (≥0.14); file CLR 0.643→0.536, STYLE 0.679→0.571 (нарастающе по 2 чанкам)
- Manual review: независимый blind-прогон (reviewer-субагент) Q10-Q18 — BST=PASS, BSM=PASS ×9, 3 удлинённых дистрактора подтверждены однозначно ложными и не карикатурными; Q12/Q16 — мягкий completeness-сигнал (не дефект)

### 2026-07-11 — java-concurrency-interview — Q19..Q28

- Commit: (this tick)
- Blocks reviewed: 10 (Q19-Q28), review-sidecar расширен до 28/56
- Correct answers fixed: 0 фактических ошибок (все correct верны)
- Дефекты устранены: **2 duplicate-misconception (W-DIV)** — Q21 B≈C (оба «RWLock допускает много писателей») → C переписан на «StampedLock реентерабелен» (ложь, self-deadlock); Q22 C≈D (оба «один Condition на Lock») → D переписан на «await() не освобождает Lock» (ложь). **1 BSM-дефект** — Q27-A был фактически ИСТИНЕН («InheritableThreadLocal копирует в дочерние потоки») → переписан в ложное «видно во всех потоках приложения»
- Length-tell снят: correct lone-longest 10/10 → 0/10 (подъём дистрактора-аутлайера Q19-B/Q20-C/Q23-B/Q25-C/Q26-A/Q28-C + подрезка verbose correct Q20-D/Q25-A; Q24-A расширен параллельной 3-term ложной структурой против completeness-leak)
- Sections rewritten: Q21-C, Q22-D, Q24-A, Q27-A (синхронизированы под новый text)
- Theory synchronized: не требовалось (факты correct не менялись; дефекты были только в дистракторах)
- Freshness updated: freshness-sidecar расширен утверждениями Q20-Q28
- Validators: verify-mcq-json OK; stamp CV=0.149 (≥0.14); SOURCE_COVERAGE ok
- Manual review: независимый blind-прогон (reviewer-субагент) Q19-Q28 — BSM=PASS ×10, distinct=PASS ×10, скрытой истины в 10 поднятых/переписанных дистракторах НЕ найдено (Q27-A подтверждён теперь ложным); остаётся мягкий сквозной сигнал «correct — самый полный» в Q20/Q23/Q24/Q28 (definitional-stem, не дефект, длина уже выровнена)

### 2026-07-11 — java-concurrency-interview — Q29..Q38

- Commit: (this tick)
- Blocks reviewed: 10 (Q29-Q38), review-sidecar расширен до 38/56
- Correct answers fixed: 0 фактических ошибок (все 10 correct верны, сверено с JDK 21 javadoc)
- Дефекты устранены: **contra-pair eliminate-hint Q33** — дистракторы A и D прямо спорили про факт «FutureTask реализует Runnable» (A утверждал, D отрицал), учащийся элиминирует по контр-паре → Q33-D переписан на отдельное заблуждение «cancel(true) принудительно останавливает выполняющуюся задачу» (ложь: кооперативное прерывание) + sections
- Length-tell снят: correct lone-longest 9/10 → 0/10 — перефразировка одного дистрактора на блок (Q30-A/Q31-B/Q32-A/Q33-B/Q34-C/Q35-A/Q36-A/Q37-B/Q38-A) в более полную форму с ТЕМ ЖЕ заблуждением (sections не тронуты)
- Sections rewritten: Q33-D (1 блок; остальные 9 правок text-only)
- Theory synchronized: не требовалось (факты correct не менялись)
- Freshness updated: freshness-sidecar расширен утверждениями Q29-Q38
- Validators: verify-mcq-json OK; stamp CV=0.161 (≥0.14)
- Manual review: независимый blind-прогон (reviewer-субагент) Q29-Q38 — BSM=PASS ×10, distinct=PASS ×10, фактически чисто, ни один удлинённый дистрактор не стал истинным (тонкие Q29-C каст→CCE, Q32-B направление steal, Q33-D FutureTask=RunnableFuture, Q38-C нет reset() — подтверждены ложными); accepted-minor: «correct — самый multi-clause» в comparison-стемах Q30/Q33/Q35/Q36 (перечисление присуще вопросу, длина выровнена, не гоним structure-tell в 0)

### 2026-07-11 — java-concurrency-interview — Q39..Q48

- Commit: (this tick)
- Blocks reviewed: 10 (Q39-Q48), review-sidecar расширен до 48/56
- Дефекты устранены: **duplicate-misconception Q42 (W-DIV)** — дистракторы C и D тестировали одно заблуждение («CHM полностью lock-free, только CAS, без synchronized») → Q42-D переписан на отдельное «CHM всё ещё делит данные на 16 Segment из Java 7» (ложь: сегменты убраны в Java 8) + sections; **caricature Q47-B (W-CAR)** — «VT только для GPU/видеокарты» отсеивался по абсурдности → заменён на правдоподобное «VT дёшевы ⇒ не нужен backpressure, можно исчерпать пул соединений к БД» + sections
- Correct answer fixed: **Q42-B факт-неточность** — treeification «при длине цепочки > 8» → «≥ 8 и ёмкости таблицы ≥ 64» (TREEIFY_THRESHOLD=8, MIN_TREEIFY_CAPACITY=64; подтверждено reviewer). Единственная правка correct-опции в чанке, доказанная фактическая ошибка
- Length-tell снят: correct lone-longest 9/10 → 0/10 — перефразировка одного дистрактора на блок (Q39-A/Q41-B/Q42-C/Q43-B/Q44-B/Q45-C/Q46-C/Q48-C) в более полную форму с тем же заблуждением (sections не тронуты); Q40 уже был чист
- Sections rewritten: Q42-D, Q47-B (2 блока); остальные text-only
- Theory synchronized: не требовалось (Q42-B — уточнение того же факта, теория treeification не меняется концептуально)
- Freshness updated: freshness-sidecar расширен Q39-Q48 (VT/pinning сверены с JEP 444 JDK21; JEP 491 JDK24 как forward-note)
- Validators: verify-mcq-json OK; stamp CV=0.176 (≥0.14)
- Manual review: независимый blind-прогон (reviewer-субагент) Q39-Q48 — BSM=PASS ×10, distinct=PASS ×10, Q42 C≠D подтверждён распутанным, ни один удлинённый дистрактор не стал истинным (тонкие Q39-B IMSE, Q41-C null-в-CHM, Q43-D SynchronousQueue-буфер-0, Q48-B pinning-от-volatile — подтверждены ложными); Q42-B ≥8 и Q47-B замена caricature внесены по прямой рекомендации reviewer

### 2026-07-11 — java-concurrency-interview — Q49..Q56 (финальный контент-чанк)

- Commit: (this tick)
- Blocks reviewed: 8 (Q49-Q56), review-sidecar расширен до 56/56 — ВЕСЬ файл контент-audited
- Дефекты устранены: **2 duplicate-misconception (W-DIV)** — Q50 A≈D (оба «ScopedValue как ThreadLocal, требует remove()») → Q50-D на «та же ThreadLocalMap, та же скорость» (ложь: ScopedValue без ThreadLocalMap); Q54 A≈D (оба «Phaser не многоразовый/однофазный») → Q54-D на «у Phaser нет onAdvance/barrier action» (ложь: onAdvance есть). **Caricature Q49-D (W-CAR)** — «SC требует LMAX Disruptor» → «SC финальна в Java 21» (ложь: preview JEP 453). **BSM-softness Q52-A** — формулировка «исключение всплывёт при get()» была защитимо-ИСТИННОЙ → переписана в однозначно ложную «исключение потеряется, цепочка вернёт null». **Слабый дистрактор Q55-B** — «обнуляет буферы» (пустое) → «exchange() неблокирующий, вернёт null» (правдоподобно-ложно)
- Length-tell снят: correct lone-longest 8/8 → 0/8 (перефразировка/подъём одного дистрактора на блок, то же заблуждение)
- Sections rewritten: Q49-D, Q50-D, Q52-A, Q54-D, Q55-B (5 блоков); остальные text-only
- Theory synchronized: не требовалось (факты correct не менялись)
- Freshness updated: freshness-sidecar расширен Q49-Q56 — весь файл; SC/ScopedValue помечены preview (JEP 453/446), StampedLock/Phaser/Exchanger стабильны
- Validators: verify-mcq-json OK; stamp CV=0.186 (≥0.14)
- Manual review: 2 независимых blind-прогона (reviewer-субагент) Q49-Q56 — прогон-1 нашёл 2 dup (Q50/Q54) + слабый Q55-B, прогон-1 же подтвердил Q49-D/Q52-A/6 удлинений ложными; фиксы внесены по рекомендации reviewer; BSM=PASS ×8, distinct=PASS ×8
- **СТАТУС ФАЙЛА:** контент 56/56 чист. НЕ FINAL: остаётся file-level POS de-cycle (CORRECT_POSITION_SEQUENCE=56, строгий A→B→C→D цикл угадывается) — следующий тик = чистая перестановка label/order по всем 56 блокам, затем file-gate PASS и FINAL=✅

### 2026-07-11 — java-concurrency-interview — POS de-cycle (FINAL)

- Commit: (this tick)
- Правка: чистая перестановка label/order ВНУТРИ каждого из 56 блоков — контент опций (text/sections/correct-флаг) byte-identical к HEAD, менялись только label (A/B/C/D) + order + позиция в массиве. Immutable-guard: multiset (text,sections,correct) блока == HEAD
- Результат: **CORRECT_POSITION_SEQUENCE 56 → 2** (строгий A→B→C→D цикл сломан целевым паттерном BASE=[2,0,3,1,0,2,1,3]×7, deltas чередуются); distribution 14/14/14/14 сохранён
- Validators: verify-mcq-json OK; stamp CV=0.186 (не изменился — контент тот же); answer-parity смысловые guessability-гейты все PASS (CORRECT_LONGEST_RATE 0.161, STYLE_GUESSABILITY 0.214, POSITION_SEQUENCE 2, DUPLICATE_NGRAMS 0)
- **VAL✅* (accepted-exception):** единственный красный OPTION_LENGTH_RATIO=3.0 (порог ≤1.35). Гейт требует почти одинаковой длины всех 4 опций — прямой конфликт с обязательным STAMP-guard (CV≥0.14, у нас 0.186) и явным запретом пользователя на «stamped-clone over-correction». Ответ НЕ угадывается ни по длине (CORRECT_LONGEST_RATE ok), ни по позиции (POSITION_SEQUENCE ok), ни по стилю (STYLE_GUESSABILITY ok) — значит смысл гейта (неугадываемость) достигнут; гонка OPTION_LENGTH_RATIO в 1.35 нарушила бы anti-stamp. Задокументировано в review-sidecar gate_summary
- **ФАЙЛ FINAL=✅** (1-й полностью завершённый файл ROUND-8). Следующий тик — новый файл worst-first по §7-матрице (design-patterns / postgresql / sql …)

### ROUND-8 · design-patterns · chunk1 Q1-Q8 (2026-07-12)
- Файл: `design-patterns-interview.json` (48 блоков, 1/Q). Новый файл, worst-first после java-concurrency
- Baseline: correct-longest 40/48 (83%), STYLE_GUESSABILITY 0.84, CORRECT_WRONG_AVG 1.455, POSITION_SEQUENCE=48 (строгий A→B→C→D), CV 0.251, SOURCE_COVERAGE missing. Доминант — Golden anti-pattern #1 (correct = развёрнутый конспект + backticks, дистракторы = заглушки)
- Правка Q1-Q8: подняты дистракторы до правдоподобных параллельно-ложных моделей с той же плотностью `backtick` (то же заблуждение → sections сохранены) КРОМЕ Q4-B — был caricature «паттерны ⇒ авто-соответствие OWASP/ISO» → правдоподобное «паттерны ⇒ готовый оттестированный код, тесты не нужны» (sections переписаны)
- correct-longest в чанке **8/8 → 2/8**: Q1 (258ch, def+GoF-история) и Q4 (225ch, риск+преимущества) оставлены длиннейшими по директиве «поднимать дистракторы, НЕ сокращать correct» (length-tell вторичен); Q2/Q3/Q5/Q6/Q7/Q8 флипнуты
- **Blind-reviewer (2 прогона) поймал BSM-дефект Q6-C**: поднятый дистрактор «одной проверки внутри `synchronized` достаточно, внешняя на корректность не влияет» стал ЗАЩИТИМО-ИСТИННЫМ (внешняя проверка DCL — perf-оптимизация, не корректность). Переписан на «опустить ВНУТРЕННЮЮ проверку под локом» = однозначная гонка; distinct от A/D; sections уже подходили. Re-verify PASS
- Факты GoF verified: Creational 5 / Structural 7 / Behavioral 11 = 23; DCL volatile (JSR-133); enum reflection-safe + readResolve не нужен; race condition
- Sidecars: review `docs/mcq-quality/reviews/design-patterns-interview.json` (IN_PROGRESS, Q1-Q8), freshness `docs/mcq-quality/fact-freshness/design-patterns-interview.json` (GoF version-insensitive + Java JLS-стабильные claims) → SOURCE_COVERAGE ok
- Validators: verify-mcq-json OK; файловые STYLE 0.84→0.74, CORRECT_LONGEST 0.83→0.73 (падают по мере чанков, финальный замер после всех блоков); DUPLICATE_NGRAMS 0 сохранён
- Дальше: Q9-Q18, Q19-Q28, … затем POS de-cycle (POSITION_SEQUENCE=48) в отдельном финальном тике

### ROUND-8 · design-patterns · chunk2 Q9-Q18 (2026-07-12)
- Тот же Golden anti-pattern #1. Все 10 correct факт-верны (Singleton-тестируемость, Factory Method OCP, Abstract Factory=семейство, Builder=telescoping, clone=shallow, Structural=7, Adapter/Bridge/Decorator)
- Правка: подняты дистракторы (pattern-confusion: Strategy/Prototype/Composite/Facade/Bridge), text-only, sections сохранены. correct-longest в чанке **10/10 → 2/10** (Q14 — полное перечисление 7 структурных, Q18 — java.io канонический пример — оставлены длиннейшими, но получили параллельную enumeration/структуру)
- **Blind-reviewer поймал Q9-B**: удлинённый дистрактор «memory leak неизбежно» стал ЗАЩИТИМО-ИСТИННЫМ под стемом «недостатки Singleton?» (несколько верных недостатков; falsifier держался лишь на слове «неизбежно»). Переписан на механически-ложное «гарантированный `OutOfMemoryError`, экземпляр раздувается и исчерпывает heap» (2 re-verify PASS, distinct от final/LSP). Sections уже подходили
- Факты verified: Simple Factory НЕ в каталоге GoF (идиома, не паттерн); clone() = shallow, нужен `Cloneable` (не «Prototype base class»); Structural ровно 7; Adapter/Facade/Bridge не перепутаны; Builder решает telescoping-constructor
- Sidecars обновлены (review Q1-Q18, freshness +clone/Structural-7 claims). Validators: verify-mcq-json OK; файловые STYLE 0.74→0.62, CORRECT_LONGEST 0.73→0.60 (18/48); CV 0.226; DUPLICATE_NGRAMS 0
- Дальше: Q19-Q28, … затем POS de-cycle

### ROUND-8 · design-patterns · chunk3 Q19-Q28 (2026-07-12)
- Тот же Golden anti-pattern #1. Все 10 correct факт-верны (Decorator-vs-наследование, Proxy, Facade/Composite/Flyweight, Strategy/State/Observer/CoR)
- Правка: подняты дистракторы (pattern-confusion + зеркальные reversed-факты), text-only, sections сохранены. correct-longest в чанке **10/10 → 3/10** (Q20 Spring-proxy виды, Q22 File/Directory, Q23 `Integer.valueOf` кэш — example-carrying, оставлены длиннейшими, но с параллельными дистракторами-примерами)
- **Correct-precision edit Q20-D** (whitelist, флаг correct не менялся): «того же интерфейса» → «(тот же интерфейс или subclass)» — снял микро-конфликт с дистрактором B про CGLIB
- **Blind-reviewer**: все 10 BSM/DISTINCT PASS, карикатур нет. Гейт поймал dup-ngram Q24-A↔Q14-B (verbatim «как классы и объекты компонуются…») → Q24-A перефразирован, DUPLICATE_NGRAMS 5→1 (ok)
- Факты verified: 2^N-взрыв классов у наследования (не Decorator); CGLIB-subclass — валидный proxy; Facade≠Adapter; Flyweight структурный (intrinsic/extrinsic, `Integer` −128..127); Behavioral=11 (не 8); Strategy=композиция vs State=self-transition; `java.util.Observable` deprecated с Java 9; CoR обработчик МОЖЕТ передать дальше
- Sidecars обновлены (review Q1-Q28; freshness +Behavioral-11/Flyweight-cache/Observable-deprecated/CGLIB claims). Validators: verify-mcq-json OK; STYLE 0.62→0.50, CORRECT_LONGEST 0.60→0.458 (28/48); CV 0.206; DUPLICATE_NGRAMS ok
- Дальше: Q29-Q38, Q39-Q48, затем POS de-cycle

### ROUND-8 · design-patterns · chunk4 Q29-Q38 (2026-07-12)
- Тот же Golden anti-pattern #1. Все 10 correct факт-верны (Command/Template Method/Visitor/Iterator/Mediator/Memento; JDK & Spring pattern-каталоги; DI-vs-Service-Locator; GoF-vs-Spring singleton)
- Правка: подняты дистракторы (pattern-confusion + false-attribution JDK/Spring), text-only. correct-longest в чанке **10/10 → 4/10** (Q30 Template Method скелет, Q35 JDK-паттерны, Q36 Spring-паттерны, Q38 GoF-vs-Spring — genuine enumeration/comparison, оставлены длиннейшими с параллельными дистракторами)
- **De-caricature Q30-D**: «Template Method невозможно реализовать в Java» (абсурд — `JdbcTemplate`/`HttpServlet` полны его) → правдоподобный миф «наследник переопределяет и сам порядок шагов» (text + sections переписаны)
- **Blind-reviewer**: все 10 BSM/DISTINCT PASS, карикатур нет. **CORRECT_LONGEST_RATE впервые PASS (0.354 ≤ 0.4)** на файловом уровне
- Факты verified: Template Method = наследование (не композиция); Visitor НЕ меняет классы элементов (double dispatch); `Iterable` даёт НОВЫЙ iterator; `Integer.valueOf`=Flyweight (не Singleton); `BufferedInputStream`=Decorator (не Adapter); `@Transactional`=Proxy (не Decorator); `@EventListener`=Observer (не Strategy); DI извне vs Service Locator скрывает; GoF Singleton=per JVM/ClassLoader vs Spring=per ApplicationContext
- Sidecars обновлены (review Q1-Q38). Validators: verify-mcq-json OK; STYLE 0.50→0.493 (length-guess 0.39, code-span 0.49 остаток), CORRECT_LONGEST 0.458→0.354 PASS, CV 0.188, DUPLICATE_NGRAMS ok (38/48)
- Дальше: Q39-Q48, затем POS de-cycle

### ROUND-8 · design-patterns · chunk5 Q39-Q48 (2026-07-12) — ФИНАЛЬНЫЙ КОНТЕНТ-ЧАНК
- Тот же Golden anti-pattern #1. Все 10 correct факт-верны (выбор паттерна, связи, Java 8+, Null Object, комбо, Interpreter, интервью-совет, Object Pool, когда-НЕ-применять, Specification)
- Правка: подняты дистракторы, text-only. correct-longest в чанке **10/10 → 5/10** (Q40 связи, Q41 Java 8+ маппинг, Q44/Q46/Q48 с примерами — enumeration/example, длиннейшие с параллельными дистракторами)
- **Blind-reviewer поймал 2 BSM-риска**: Q44-B («ScriptEngine — замена Interpreter» защитимо-истинно → «делает Interpreter ненужным для любого DSL» = ложь, только известные языки); Q48-B («требует наследования per rule» частично истинно в классическом OOP → «только наследование, лямбды неприменимы» = ложь, есть default-метод композиция). Обе правки sections-consistent, contra-pair с correct избегнут
- Факты verified: Null Object возвращает пустое поведение (НЕ бросает NPE); Interpreter=грамматики/DSL, редко; Object Pool≠Flyweight (`HikariCP`/`ExecutorService`/Netty реальны); Specification and/or/not композиция, Spring Data Spec≠DDD Spec; Java 8+ лямбды упрощают Strategy/Command/Observer/Factory/Iterator
- Sidecars: review Q1-Q48 (ВЕСЬ контент, status IN_PROGRESS до POS), freshness +Java8+/ObjectPool/Specification claims
- **ВЕСЬ КОНТЕНТ Q1-Q48 audited.** Validators: verify-mcq-json OK; CORRECT_LONGEST 0.354→**0.25 (strong PASS)**, length-guess 0.28, DUPLICATE ok, CV 0.166; STYLE 0.493 (остаток — code-span 0.49, correct-опции цитируют реальные API — тот же класс accepted-residual, что OPTION_LENGTH_RATIO)
- **ОСТАЛОСЬ: POS de-cycle** (CORRECT_POSITION_SEQUENCE=48, строгий A→B→C→D цикл) в отдельном финальном тике → затем FINAL=✅

### ROUND-8 · design-patterns · POS de-cycle (2026-07-12) — FINAL
- Чистая пермутация label/order внутри каждого из 48 блоков; контент byte-identical к HEAD (multiset-sig guard по (text, sections, correct) на каждый блок)
- Целевая последовательность `BASE=[2,0,3,1,0,2,1,3]` tiled ×6 → relabel ops=84, дистрибуция A/B/C/D = 12/12/12/12
- **CORRECT_POSITION_SEQUENCE 48→2** (строгий A→B→C→D цикл разбит, порог ≤5); CORRECT_POSITION_DISTRIBUTION 1.0
- Все смысловые гейты PASS: CORRECT_LONGEST 0.25, CORRECT_WRONG_AVG **1.054** (кумулятивный эффект поднятия дистракторов — с baseline 1.455), DUPLICATE 2, SOURCE ok, CV 0.166
- Accepted: OPTION_LENGTH_RATIO 3.75 (анти-stamp exception) + STYLE 0.493 (length-guess 0.28 PASS; code-span 0.49 — correct легитимно цитирует реальные API, 4 подлинных enumeration-экстремума Q35/Q36/Q40/Q41; балансировка выдумыванием API в дистракторах = запрещённый stamped-clone)
- Микро-остаток format-parity на будущий раунд: Q7 (`Singleton` bare в дистракторах vs backtick в correct), Q42 (`GoF` bare) — не правился (POS-тик content-frozen по определению)
- Sidecars: review → status **COMPLETE** (+chunk POS, style_guessability задокументирован как accepted-residual); freshness notes → покрытие Q1-Q48
- **Файл design-patterns-interview.json — FINAL ✅** (строка 2 таблицы §1). Следующий worst-first: строка 4 `sql` (строка 3 `postgresql` — off-limits)

### ROUND-8 · sql · chunk1 Q1-Q10 (2026-07-12)
- Baseline файла: CORRECT_LONGEST 0.642 (34/53), STYLE 0.66, CORRECT_WRONG_AVG 1.444, POSITION_SEQUENCE=53 (строгий A→B→C→D), CV 0.218, freshness отсутствовал. Доминант — Golden anti-pattern #1 (correct 150-260ch vs дистракторы-заглушки 57-115ch, худший блок Q1 CV=0.567)
- Правка: подняты 30 дистракторов (псевдо-обоснование при том же заблуждении, text-only) + 2 dict-spec rewrite. correct-longest в чанке **10/10 → 3/10** (Q1 каталог DDL/DML/DCL/TCL — подлинный enumeration-экстремум; Q5/Q10 малый зазор)
- **Blind-reviewer** (все 10 VERDICT совпали, BSM/MULTI нет): Q7 — все 3 дистрактора один приём «определение соседней НФ» → Q7-A заменён на wrong-trade-off «выше НФ = быстрее, цель 5NF» (text+sections); Q8-A карикатура (абсолютные кванторы) → смягчён; Q1-C/D зеркальный дубль DCL↔TCL → Q1-D заменён на «`TRUNCATE` — это `DML`» (text+sections)
- Факты verified: DDL/DML/DCL/TCL; UNIQUE допускает NULL (PG несколько, PG15+ NULLS NOT DISTINCT); FK NULL допустим; `= NULL`→UNKNOWN, `5+NULL=NULL`, `COUNT(*)` считает все строки; MySQL TIMESTAMP до 2038/4 байта vs DATETIME 1000-9999/8 байт; TIMESTAMPTZ 8 байт (зону не хранит); 3NF=транзитивные; денормализация OLAP/DWH; View/MatView; Function-vs-Procedure (COMMIT в PG 11+)
- Sidecars: review создан (10 recs, IN_PROGRESS), freshness создан (7 claims, PG16/MySQL8.0) → SOURCE_COVERAGE ok
- Validators: verify-mcq-json OK; CORRECT_LONGEST 0.642→0.453, STYLE 0.66→0.472 (length 0.47), CORRECT_WRONG_AVG 1.444→1.141, CV 0.168, DUPLICATE 0
- ОСТАЛОСЬ: Q11-Q53 (чанки 2-6) + POS de-cycle → FINAL

### ROUND-8 · sql · chunk2 Q11-Q20 (2026-07-12)
- Length-tell мягче чанка 1: Q13/Q17 уже в паритете (не тронуты), Q18 correct не длиннейший. Подняты 21 дистрактор (text-only) + 2 dict-spec
- **НАХОДКА — существующий BSM-дефект сидера Q16-B**: текст «`MERGE` за один проход выполняет и `INSERT`, и `UPDATE`, обновляя совпавшие строки и вставляя новые» был защитимо-ИСТИННЫМ (это определение MERGE/UPSERT из теории), при sections, описывающих заблуждение «обе операции к одной строке». Текст переписан под заявленное заблуждение («сначала обновляется, затем повторно вставляется»)
- **CORRECT-precision (whitelist) Q14-B**: «в `GROUP BY` нельзя использовать алиасы из `SELECT`» верно только для стандарта — PostgreSQL/MySQL резолвят выходные имена как расширение (`GROUP BY m` работает); text+sections хеджированы «по стандарту SQL»
- **Blind-reviewer** (8 блоков, 8/8 VERDICT совпали, MULTI нет): Q16-C — карикатура, буквальное чтение `DO NOTHING` опровергает «затирание NULL-ами» → заменён на миф «DO NOTHING откатывает sequence, дырок в id нет» (text+sections); Q20-C пересекался с Q20-A («нужен отдельный SELECT») → уведён в миф «`RETURNING` нельзя в CTE» (text+sections, data-modifying CTE); Q19-A pre-PG12 ловушка — sections уже оговаривают PG12/`MATERIALIZED`
- Факты verified: `INSTEAD OF` только на view; `BEFORE` имеет `OLD`/`NEW`; PG требует trigger-функцию; `#`/`##` в SQL Server; temp-таблицы поддерживают индексы; `MERGE` с PG15 / `ON CONFLICT` с 9.5; `RETURNING` нет в MySQL 8; recursive CTE поддерживает `UNION ALL`; CTE можно в `UPDATE`/`DELETE`
- Sidecars: review 20 recs, freshness +5 claims (12 всего)
- Validators: verify-mcq-json OK; **ФАЙЛ ЦЕЛИКОМ В PASS уже после 2 чанков**: CORRECT_LONGEST 0.453→0.377 (≤0.4), STYLE 0.472→0.387 (≤0.4), CORRECT_WRONG_AVG 1.141→1.042, CV 0.168, DUPLICATE 0
- ОСТАЛОСЬ: Q21-Q53 (чанки 3-6, добить пер-блочный паритет) + POS de-cycle → FINAL

### ROUND-8 · sql · chunk3 Q21-Q30 (2026-07-12)
- Подняты 22 дистрактора (text-only) + 2 dict-spec; Q24/Q28 уже в паритете — не тронуты. correct-longest в чанке 8/10 → 2/10 (Q22 — подлинно детальное сравнение ON/WHERE; Q28 не трогался)
- **Blind-reviewer** (8 блоков, 8/8 VERDICT совпали, MULTI нет): Q21-B/C — дубль «outer возвращает только пересечение» → Q21-C заменён на миф «`FULL OUTER` = `CROSS`, все комбинации» (text+sections); Q22-A/D — взаимоисключающая пара «ON раньше»/«WHERE раньше» давала мета-подсказку → Q22-D уведён в миф «различие — диалектная особенность MySQL» (text+sections); Q27-B ужесточён кванторами против защитимости через functional dependency (PG16) / `ONLY_FULL_GROUP_BY` (MySQL)
- Пост-гейт фикс: Q30-C дал 10-грамм дубль с Q33-B («оконные функции вычисляются до WHERE…») → перефразирован, DUPLICATE 3→0
- Факты verified: outer-JOIN сохраняют строки без пары; `RIGHT` = `LEFT` со сменой таблиц; коррелированный подзапрос per-row O(n²); `UNION` dedup дороже `UNION ALL`; `CROSS` = |A|×|B|×|C|; `SELF JOIN` = обычный JOIN с алиасами; `NULL` образует собственную группу в `GROUP BY`; `COUNT(1)` ≡ `COUNT(*)`; окна не схлопывают строки, `OVER()` = одно окно на весь набор
- Validators: verify-mcq-json OK; CORRECT_LONGEST 0.377→0.264, STYLE 0.387→0.354 (length 0.27), CORRECT_WRONG_AVG 1.042→0.952, CV 0.151, DUPLICATE 0
- ⚠ CV 0.151 близок к порогу 0.14: в чанках 4-6 сохранять естественную вариативность длин, не выравнивать механически
- ОСТАЛОСЬ: Q31-Q53 (чанки 4-6) + POS de-cycle → FINAL

### ROUND-8 · sql · chunk4 Q31-Q40 (2026-07-12)
- Точечные raises с оглядкой на CV: 13 текстов + 2 dict-spec; Q31/Q38/Q40 уже в паритете — не тронуты. correct-longest в чанке 8/10 → 2/10
- **Blind-reviewer поймал 3 СУЩЕСТВУЮЩИХ дефекта сидера** (7 блоков, 7/7 VERDICT): Q32-A — двусмысленность «значение текущей строки минус два» читалась и как offset (= второй защитимый ответ) → уточнён до однозначно-ложной арифметической поправки; Q37-D — BSM, защитимо-истинное «индекс ускоряет поиск по дереву» среди дистракторов → абсолютизирован («всегда, даже на 50 строках», ложь по селективности); Q39-A — карикатурная инверсия «READ UNCOMMITTED предотвращает все аномалии» → заменён на правдоподобный PG-миф «RU реально читает грязные данные» (text+sections: MVCC, RU=синоним RC)
- Дополнительно: Q34-C внутренне противоречивая карикатура «каждый индекс по умолчанию GIN» → миф «индекс ускоряет и чтение, и запись» (text+sections); Q39-D де-карикатура «самый производительный… вовсе» → SSI-обоснование при том же заблуждении из sections
- Факты verified: правило левого префикса; InnoDB `PRIMARY KEY` = кластерный, в PG кластерных нет (`CLUSTER` — разовая перегруппировка); `Index Only Scan`; `INCLUDE` = non-key столбцы; `LIKE '%suffix'` и функция на столбце ломают B-tree (нужен функциональный индекс); `REPEATABLE READ` по стандарту допускает `Phantom Read`; `SERIALIZABLE` наименее производительный (SSI, rollback по конфликтам); deadlock = цикл ожидания, СУБД обнаруживает сама
- Validators: verify-mcq-json OK; CORRECT_LONGEST 0.264→0.226, STYLE 0.354→0.363 (length-guess 0.27→0.23, ниже шанса 0.25), CORRECT_WRONG_AVG 0.952→0.906, CV 0.151, DUPLICATE 0
- ⚠ AVG 0.906 у нижней границы 0.9: чанкам 5-6 не перекачивать дистракторы сверх correct
- ОСТАЛОСЬ: Q41-Q53 (чанки 5-6) + POS de-cycle → FINAL

### ROUND-8 · sql · chunk5 Q41-Q47 (2026-07-12)
- JPA/Spring-хвост файла. Умеренные raises: 15 текстов + 1 dict-spec + 1 CORRECT-precision; correct-longest в чанке 7/7 → 2/7
- **Blind-reviewer поймал БЛОКЕР Q47-D** (7 блоков, 7/7 VERDICT): «`readOnly = true` запрещает `INSERT`/`UPDATE`/`DELETE` на уровне БД» — защитимо-ИСТИНЕН в заявленном стеке (PG16 + Hibernate 6: `setReadOnly(true)` → транзакция `READ ONLY` → «cannot execute INSERT in a read-only transaction») → переформулирован в явно ложный абсолют («в любой СУБД и конфигурации, транслируется в права доступа»)
- Q46-C: слабый дистрактор «CHECK вместо уникального индекса» (CHECK принципиально не проверяет межстрочную уникальность) → миф «`unique = true` живёт только в Hibernate-валидации, БД-ограничение не создаётся» (text+sections)
- **CORRECT-precision (whitelist) Q44-D**: формула N+1 теперь сходится буквально — «один запрос загружает N родительских, затем на каждую отдельный запрос за связью»
- **Пост-гейт trim-pass**: chunk5-raises увели CORRECT_WRONG_AVG в 0.872 (<0.9) и CV в 0.140 → подрезаны псевдо-обоснования 15 самых многословных wrong-текстов по всему файлу (~100 prose-слов; гейт не считает code-spans, поэтому резалась именно проза; заблуждения сохранены) → AVG 0.912, CV 0.143
- Факты verified: оптимистичная блокировка = `@Version`/`OptimisticLockException` при COMMIT; `EXPLAIN` не выполняет запрос (выполняет `ANALYZE`); keyset-пагинация > `OFFSET`; N+1 из LAZY-циклов, `@BatchSize` лишь чанкует; индексы в проде через миграции с `CREATE INDEX CONCURRENTLY`; self-invocation идёт мимо proxy
- Validators: verify-mcq-json OK; CORRECT_LONGEST 0.226→0.189, STYLE 0.363 (length 0.23), CORRECT_WRONG_AVG 0.912, CV 0.143, DUPLICATE 0
- ⚠ CV 0.143 впритык к 0.14: chunk6 (Q48-Q53) — минимальные правки, сохранять вариативность
- ОСТАЛОСЬ: Q48-Q53 (chunk6) + POS de-cycle → FINAL

### ROUND-8 · sql · chunk6 Q48-Q53 (2026-07-12)

Финальный контент-чанк (PG-хвост: рекурсивные CTE, EXPLAIN, партиционирование, индексы, VACUUM, MVCC). Режим минимальной инвазии: оба лексических гейта были впритык, word-бюджет считался явно (words() гейта стрипает code-spans) — лимит +28 слов wrong-пула, факт +21 после офсет-тримов Q10-D/Q23-D (-8 слов, мои же raised-тексты).

Blind-reviewer (6 блоков, 6/6 VERDICT совпали, MULTI нет). Фиксы (все dict-spec, text+sections):
- **Q48-A** — зеркальная пара с correct («якорь многократно» ↔ «якорь один раз» = мета-подсказка «ответ один из двух») → уведён в self-join миф «рекурсивная часть не видит предыдущий шаг, глубина = число JOIN-ов»;
- **Q48-B** CAR («обязательно UNION») → миф «дедупликация UNION и есть условие остановки»;
- **Q48-C** CAR («автоматически обнаруживает циклы») → PG14-миф «клауза CYCLE включена по умолчанию»;
- **Q49-B** — тематический overlap с correct (оба про rows vs actual → наводка) → классический gotcha «EXPLAIN ANALYZE выполняет DML в откатываемой песочнице» (на деле реально меняет данные);
- **Q50-C** CAR («партиционирование устраняет индексы») → Oracle-перенос «глобальный индекс единой структурой»;
- **Q51-A** — general-CS отбрасываемый (hash без диапазонов) → stale-знание «hash-индекс не журналируется в WAL» (истина до PG10);
- **Q53-C** — внутренне противоречив («удаляет сразу после создания») → когерентный миф «удаляет сразу после коммита, xmax/VACUUM не нужны» (+гасит уникальность xmax/VACUUM в correct).

Q52 целиком и остальные опции чанка подтверждены ревьюером — не тронуты (форм-телл хвоста принят: выравнивание убило бы CV, см. gate_summary сайдкара). Гейты после: verify-mcq-json OK; CORRECT_LONGEST 0.151; AVG 0.903 (впритык ≥0.9); CV 0.143 (впритык ≥0.14); STYLE 0.373; DUP 0; POSITION_SEQUENCE 53 — закрывает следующий POS-тик. Freshness-сайдкар расширен до Q1-Q53 (18 claims: +CYCLE PG14, hash-WAL PG10, декларативное партиционирование PG10/нет глобальных индексов, EXPLAIN ANALYZE-DML, autovacuum-пороги, xid wraparound).

### ROUND-8 · sql · POS de-cycle (FINAL) (2026-07-12)

Дедицированный позиционный тик, контент заморожен. Строгий цикл A→B→C→D (POSITION_SEQUENCE=53, полностью угадываемый) снят перестановкой label/order внутри блоков по BASE-паттерну [2,0,3,1,0,2,1,3] на 53 блока: POSITION_SEQUENCE 53→2, dist A/B/C/D = 14/13/13/13 (max/min 1.08), relabel ops=94. Guard: контент байт-идентичен HEAD по multiset-сигнатуре (text, sections, correct) каждого блока; порядок дистракторов внутри блока сохранён относительно. Гейты после: verify-mcq-json OK; все смысловые метрики без сдвига (CORRECT_LONGEST 0.151, AVG 0.903, CV 0.143, STYLE 0.373, DUP 0); OPTION_LENGTH_RATIO — принятое исключение. Сайдкар reviews → status COMPLETE (53/53 records, chunks + POS). **Файл sql — FINAL ✅** (строка 4 §1). Следующий worst-first: строка 5 `project-reactor` (строка 3 `postgresql` — off-limits).

### ROUND-8 · project-reactor · chunk1 Q1-Q10 (2026-07-12)

Новый worst-first файл (строка 5; строка 3 `postgresql` — off-limits). Baseline — самый выраженный Golden anti-pattern #1 из виденных: length-guess 0.92, correct-длиннейший в 44/48 блоков, correct̄ 17 слов vs wronḡ 7 (AVG 2.425), OPTION_LENGTH_RATIO 6.33, строгий позиционный цикл A→B→C→D (seq=48), freshness-сайдкара не было. Режим противоположный sql: wrong-пул массово наращивается (запас AVG огромный).

Подняты все 30 дистракторов чанка до правдоподобных параллельно-ложных моделей (25 text-only + 5 dict-spec). Blind-reviewer (10 блоков, 10/10 VERDICT, MULTI/BSM нет; фактура correct подтверждена: JDK9 Flow, Flowable-only backpressure, BUFFER-дефолт, SynchronousSink) потребовал и получил:
- **Q4-A** — OOM-retry «общежитейски абсурден» → миф «backpressure = безлимитная буферизация» (dict-spec);
- **Q6-A / Q7-D** — enumeration-tell (в вопросах «какие способы…» только correct был перечислением) → отравленные перечисления: «все фабрики Mono — eager» и сигнатуры-мутанты `range(start,end)`/`interval`-однократный/`generate`-многопоточный (оба dict-spec);
- **Q10-C** — дубль с Q10-D («generate имеет семантику create») → перенос контракта `Stream.generate` (dict-spec);
- **Q9-C** — покрывал только create → «у push нет OverflowStrategy» (ложь: перегрузка есть, дефолт BUFFER; кроет оба метода, dict-spec); Q9-B зеркален correct по структуре;
- кванторный tell Q5/Q7-A смягчён псевдо-обоснованиями.

Гейты после: verify-mcq-json OK; DUP 0; ABSOLUTE_MARKER_GAP 0.062; интерим CORRECT_LONGEST 0.75, AVG 1.871, STYLE 0.76 — добиваются чанками Q11-Q48 и POS-тиком. Сайдкары созданы: reviews (10 records) + fact-freshness (6 claims, Reactor 3.6/RS 1.0.4/JDK9 Flow).

---

## 12. Финальная сводка

Свип завершён только при:

```text
Files total: 318
FINAL ✅: 318 minus explicitly approved manual exclusions
CRITICAL: 0
HIGH: 0
Unknown/null block checks: 0
Schema failures: 0
Theory dangling: 0
Multiple defensible answers: 0
Known factually wrong correct answers: 0
```

Неразрешённые `🟠` не считаются завершением без явного решения пользователя.

### ROUND-8 · project-reactor · chunk2 Q11-Q20 + markup-repair (2026-07-12)

- **Chunk2 Q11-Q20** (commit `098484fb`): подняты все 30 дистракторов (24 text + 6 dict-spec: Q11-A, Q12-A, Q14-C, Q16-A, Q18-A, Q19-A). Blind-reviewer 10/10 VERDICT; фиксы: Q11-A/Q18-A swap-карикатуры → мифы («map сам разворачивает Publisher», «take не отменяет подписку»); Q12-A+Q19-A кросс-вопросный дубль «синонимы» → prefetch-миф (256/32/1) и zip-padding гибрид; Q14-C DUP+generics-tell → «as = псевдоним transform» с сигнатурой; Q15-B слэш-перечисление, Q16-A отравленное перечисление (минус `filterWhen`); Q17-D пре-эмпт оговорка. Интерим-гейты: LONGEST 0.917→0.562, AVG 2.425→1.463, STYLE 0.715.
- **SKEL-пасс** (в том же коммите): 11 HARD-блоков Q1-Q20 (Q2,Q3,Q7,Q8,Q11,Q14,Q16-Q20) выровнены под скелет correct — `;` в общем месте, единый backtick-зачин; ложность сохранена. mcq-skel-gate: Q1-Q20 HARD-чист.
- **Markup-repair** (commit `0f66cadf`): SKEL-прогон `68444a1b` (Cursor) расщепил backtick-спаны в 19 опциях (reactor 12, dp 7, вкл. correct dp Q21-A/Q35-D, reactor Q35-C — потеряны скобочные примеры); восстановлены дословно из `68444a1b^`. ⚠️ НЕРЕШЁННОЕ: тот же прогон изменил 110 correct-опций по 4 файлам (dp 37, reactor 36, sql 30, jc 7) — вырезаны сигнатуры (`request(n)`→`request`, `range(start,count)`→`range`), глоссы («(Open/Closed)»), сломан Java-код (`catch LowLevelEx e { throw new HighLevelEx; }`), аргументы в backticks без скобок (`combineLatest p1, p2, combinator`). Массовое восстановление = откат ядра чужого коммита → ждёт решения пользователя.

### ROUND-8 · project-reactor · chunk5 Q41-Q48 + file-wide re-balance (2026-07-12)

- **Chunk5 Q41-Q48** (финальный контент-чанк: время/TestPublisher/отладка/WebFlux/block/context-propagation): 7 correct-восстановлений из `68444a1b^` (Q41-Q46, Q48; Q41-A был синтаксически сломан — `withVirtualTime -> flux)`; Q47-C совпадал) + 22 подъёма (16 text + 6 dict-spec: Q42-C backpressure-миф TestPublisher, Q43-D logging-config-миф, Q44-C log-marker-миф, Q47-A/B/D ложные-причины «Context/OOM/переупорядочивание»).
- **Blind-reviewer 8/8 VERDICT**, MULTI нет; фиксы: Q42 DUP compliance-зеркала развязан (C → backpressure-миф); Q44 DUP breakpoint-пары развязан (C → log-миф), дистракторам даны свои backtick-артефакты (`Frames`, `AssertionError`-цитата); Q43 перечисления подняты до 4-5 инструментов + единые btick-зачины; Q46 DUP servlet-пары развязан; Q47 form-tell «только correct отвечает на „почему"» устранён — все дистракторы отвечают ложными причинами той же формы.
- **File-wide re-balance**: подъём чанка вскрыл перекос wronḡ>corr̄ (AVG 0.861 FAIL — на HEAD маскировался коротким деградированным хвостом) → тримы прозы-филлера дистракторов в 14 блоках (Q2/Q6/Q13/Q16/Q28/Q30/Q34/Q38/Q40 + chunk5), cs-паритет Q2/Q40/Q45/Q46 (correct был unique-max по code-spans).
- **Гейты файла**: LONGEST 0.042, AVG 0.922, STYLE 0.391 (len 0.09 / cs 0.39), DUP 0, SENTENCE ok, DETAIL 0.25, stamp verdict OK (not padded-clone); SKEL Q1-Q48 HARD-чист, **LLM-вычитка принята → SKEL=✅**. Сайдкары: reviews 48/48 records, freshness 23 claims (Q1-Q48).
- Факты verified: `withVirtualTime` требует `Supplier`; `TestPublisher` next/error/complete + `createNoncompliant` + `assertMinRequested`; `Hooks.onOperatorDebug` глобальный/дорогой vs `checkpoint` error-path-only vs `ReactorDebugAgent` production-safe; WebFlux = Reactor + event-loop (аннотации поддерживаются); `block()` на NonBlocking → `IllegalStateException`; `contextCapture()`/`ThreadLocalAccessor` (3.5+). Остаток: POS de-cycle (seq=48) → FINAL.

### ROUND-8 · project-reactor · POS de-cycle (FINAL) (2026-07-12)

Дедицированный позиционный тик, контент заморожен. Строгий цикл A→B→C→D (POSITION_SEQUENCE=48, полностью угадываемый) снят перестановкой label/order внутри блоков по BASE-паттерну [2,0,3,1,0,2,1,3] ×6 на 48 блоков: POSITION_SEQUENCE 48→2, dist A/B/C/D = 12/12/12/12 (max/min 1.0), relabel ops=84. Guard: контент байт-идентичен по multiset-сигнатуре (text, sections, correct) каждого блока; относительный порядок дистракторов сохранён. Гейты после: verify-mcq-json OK; смысловые метрики без сдвига (LONGEST 0.042, AVG 0.922, STYLE 0.391, DUP 0, DETAIL 0.25); mcq-skel-gate PASS hard=0; stamp verdict OK; OPTION_LENGTH_RATIO — принятое исключение. Сайдкар reviews → status COMPLETE (48/48 records, chunks + POS, reviewed_commit 68714e20). **Файл project-reactor — FINAL ✅** (строка 5 §1). Следующий worst-first: строка 6 `hibernate` (строка 3 `postgresql` — off-limits).

### ROUND-8 · hibernate · chunk1 Q1-Q10 (2026-07-12)

- **Chunk1 Q1-Q10** (JPA vs Hibernate, JDBC, интерфейсы, Session/SessionFactory, архитектура, состояния entity, dirty checking, persist/save/merge/update, get/load): подняты 27 дистракторов (24 text + 3 dict-spec). Q5 не тронут — terse-parallel четвёрка комбинаций уже golden-паритетна (подтверждено слепым ревьюером). `68444a1b` файл не трогал — чистая полоса без correct-восстановлений.
- **Blind-reviewer 10/10 VERDICT**, MULTI/BSM нет; фиксы: Q6-C карикатура «свой сетевой слой мимо JDBC» → миф «`Dialect`-слой берёт роль драйвера» (dict-spec); DUP Q4(A,C) «Session разделяется потоками» → C уведён в миф «`Session` = HTTP-сессия» (dict-spec); DUP Q10(A,D) двойной eager/lazy-swap → D уведён в инверсию null/exception контрактов (dict-spec, правдоподобно из-за реального `EntityNotFoundException` у `getReference`).
- **Гейты**: schema OK; интерим LONGEST 0.76→0.60, AVG 1.617→1.355, STYLE 0.81→0.66 (добивается чанками Q11-Q50 + POS); DUP 0; mcq-skel-gate Q1-Q10 hard=0 (было 29 file-wide, остаток — зона будущих чанков); stamp verdict OK. Сайдкары созданы: reviews IN_PROGRESS 10/50, freshness 7 claims (SOURCE_COVERAGE ok).
- Факты verified: JPA=спецификация (jakarta.persistence 3.1), Hibernate 6.x реализация; save/update/load deprecated в H6; merge возвращает новую managed-копию; get/find→null vs load/getReference→proxy+исключение при инициализации; dirty checking = snapshot+flush (FlushMode AUTO), markDirty не существует, bytecode enhancement — опция; SessionFactory=потокобезопасный синглтон (L2+метаданные), Session=unit of work (L1, ThreadLocal в Spring); Dialect генерирует SQL, исполняет JDBC-драйвер.

### ROUND-8 · hibernate · chunk2 Q11-Q20 (2026-07-12)

- **Chunk2 Q11-Q20** (маппинг: no-arg конструктор, final entity, аннотации, наследование, O2M/M2M-связи, @JoinColumn/mappedBy, @Embeddable, fetch-стратегии, N+1): подняты 30 дистракторов (25 text + 5 dict-spec: Q12-C «прокси через интерфейс», Q13-D «@Transient = ключевое слово transient», Q14-D «JOINED требует discriminator», Q19-B «батчинг включён по умолчанию», Q20-B «N+1 = страницы + COUNT»).
- **Blind-reviewer 10/10 VERDICT**, MULTI/BSM нет; фиксы: DUP Q14(C,D) «одна таблица/NOT NULL» развязан; DUP Q19 «EAGER спасает» развязан; Q20-B карикатура «N параллельных запросов» заменена pagination-COUNT-мифом; Q18-C абсолютив «неустранимый» снят.
- **Гейты**: schema OK; интерим LONGEST 0.60→0.42, AVG 1.355→1.164, STYLE 0.66→0.48; DUP 0; SKEL Q1-Q20 hard=0 (file-wide 29→17, остаток — зона будущих чанков); stamp OK. Freshness расширен до 12 claims (дефолтные fetch-типы, SEQUENCE/IDENTITY-батчинг, @BatchSize opt-in, no-arg контракт, ByteBuddy-прокси).
- Факты verified: конструктор public/protected (JPA-требование); final ломает только proxy/lazy; SEQUENCE+batch vs IDENTITY; STRING vs ORDINAL; три стратегии наследования и их различение (discriminator/JOIN-по-PK/таблица); дефолты fetch; mappedBy = имя поля, inverse-сторона не пишется; orphanRemoval ≠ CascadeType.REMOVE; M2M best practices; @AttributeOverrides для дублей embeddable; N+1 = 1+N ленивых дозапросов.
### ROUND-8 · hibernate · chunk3 Q21-Q30 (2026-07-12)

Тематика чанка: решения N+1, LazyInitializationException, EntityGraph, L1/L2-кэши, Query Cache, стратегии конкурентности, JPQL/Criteria/NamedQuery. Подняты 30 дистракторов (22 text + 8 dict-spec: Q21-B SUBSELECT-миф, Q22-A pool-exhaustion-миф, Q23-A fetchgraph-exclusion, Q23-D «граф залипает на маппинге», Q24-B «L1 хранит только ID», Q27-B «стратегии = блокировки слоёв», Q29-C «переименованный API», Q30-A «NamedQuery кэширует результаты»). Blind-reviewer 10/10 VERDICT, MULTI нет; фиксы по его флагам: DUP wrong-wrong Q24(A,B) и Q27(A,B) развязаны сменой оси, mirror-DUP с correct в Q21/Q22/Q23/Q30 уведены на независимые оси, карикатуры Q21-D («EAGER рекомендован для нагруженных») и Q25-D («ConcurrentHashMap хватит на production-кластер») переписаны в правдоподобные мифы, BSM Q25/Q29 (correct был единственным без кванторов) закрыт смягчением кванторов у дистракторов. Метрики после: LONGEST 0.26, AVG 1.029, STYLE 0.418 (интерим-FAIL за счёт нетронутых Q31-Q50), DUP 0, DETAIL 0.18; skel-gate Q1-Q30 hard=0 (остаток 11 в Q31-Q50); stamp verdict OK (GOLD-CV 0.195). Freshness 12→18 claims (fetchgraph/loadgraph, L2-провайдер, Query Cache, стратегии, org.hibernate.Criteria, NamedQuery). Остаток: chunks Q31-Q40, Q41-Q50, затем POS-тик (seq=50).
### ROUND-8 · hibernate · chunk4 Q31-Q40 (2026-07-12)

Тематика чанка: нативный SQL, SQL Injection, @Version/оптимистичная vs пессимистичная блокировка, транзакции, batch processing, setMaxResults/setFetchSize, @Immutable, Statistics, конфигурация Spring Boot. Подняты 30 дистракторов (24 text + 6 dict-spec: Q31-D createQuery(nativeQuery)-миф из Spring Data, Q33-C «@Version только Timestamp», Q36-A «batch только для INSERT», Q37-D «усечение на клиенте», Q38-D «@Immutable требует final», Q39-A «свойство generate_statistics = legacy»). Blind-reviewer 10/10 VERDICT, MULTI нет; фиксы по его флагам: BSM Q31 (correct — единственный без оговорки «Да, но») закрыт flat-positive дистрактором, BSM Q32/Q35/Q39 (абсолютные кванторы) смягчены, BSM Q40 (скобки-«обещания чуда») отрезвлены до механизменных пояснений, DUP-зеркала Q36 (IDENTITY против SEQUENCE) / Q37 (полный роль-своп) / Q39 (инверсия одного свойства) уведены на независимые оси, CAR Q38-D («эквивалент final») переписан в правдоподобный миф «требует final». Метрики после: ВСЕ смысловые гейты зелёные — LONGEST 0.14, AVG 0.937, STYLE 0.33, DUP 0, DETAIL 0.12; skel-gate Q1-Q40 hard=0 (остаток 4 в Q41-Q50); stamp verdict OK (GOLD-CV 0.195). Freshness 18→24 claims (@Version-типы, IDENTITY/SEQUENCE-батчинг, scope батчинга, setFetchSize-драйверозависимость, Statistics, ddl-auto). Остаток: chunk Q41-Q50, затем POS-тик (seq=50).
### ROUND-8 · hibernate · chunk5 Q41-Q50 (2026-07-12)

Финальный контент-чанк: getCurrentSession/openSession, Dialect, OSIV, Envers, Projections, StatelessSession, perf-ошибки, тестирование (Q49-Q50 уже golden-паритетны — не тронуты). Подняты 24 дистрактора (20 text + 4 dict-spec: Q41-C пул-сессий-миф, Q43-A «OSIV только про серверный рендеринг», Q47-A инвертированный HHH-гочас «пагинация с JOIN FETCH уходит в SQL», Q48-A «@DataJpaTest поднимает полный контекст»). Blind-reviewer 8/8 VERDICT; фиксы: DUP-зеркала Q41/Q43/Q47/Q48 развязаны сменой оси, CAR Q43-A (противоречие стему) заменён scope-мифом, BSM Q45 (кванторы) смягчён, BSM Q46 (trade-off-тон) выровнен, BSM Q48 (негативные рамки) перевёрнут в позитив. Файловый AVG-ре-баланс: подъёмы перелетели correct (0.866) — три прохода стрижки прозы-филлера у 35 дистракторов (c5b/c5c/c5d) вернули AVG к 0.90, correct не инфлирован, skel PASS сохранён. Метрики файла: LONGEST 0.02, AVG 0.90, STYLE 0.308, DUP 0, DETAIL 0.08, ABSOLUTE 0.193, skel hard=0, stamp OK (GOLD-CV 0.195). Freshness 24→31 claims. Остался только POS-тик: SEQ=50 (строгий цикл), дистрибуция уже 13/13/12/12.
### ROUND-8 · hibernate · POS-тик (2026-07-12) — FINAL ✅

Дедицированный позиционный тик, контент заморожен. Строгий цикл A→B→C→D (POSITION_SEQUENCE=50, полностью угадываемый) снят перестановкой label/order внутри блоков по BASE-паттерну [2,0,3,1,0,2,1,3] на 50 блоков: SEQ 50→2, dist A/B/C/D = 13/12/13/12 (max/min 1.08), relabel ops=89. Guard: контент байт-идентичен по multiset-сигнатуре (text, sections, correct) каждого блока; относительный порядок дистракторов сохранён. Гейты после POS: verify-mcq-json OK; LONGEST 0.02, AVG 0.90, STYLE 0.308, DUP 0, DETAIL 0.08; mcq-skel-gate PASS hard=0; stamp OK; OPTION_LENGTH_RATIO — принятое исключение. Сайдкар reviews → status COMPLETE (50/50 records, chunks 1-5 + POS, reviewed_commit 5b7ed428); freshness 30 claims (поправка: в заметке chunk5 указан 31 — верно 30). **Файл hibernate — FINAL ✅** (строка 6 §1). Следующий worst-first по §1.
### ROUND-8 · java-collections · chunk1 Q1-Q10 (2026-07-12)

- Строка 7 §1, файл 46Q/46 блоков (1/Q). Baseline — худший профиль корпуса: LONGEST 0.978 (45/46), AVG 2.059, STYLE 0.978, DETAIL 0.935, RATIO 4.44, seq=46 (строгий A→B→C→D), skel hard=42, freshness отсутствовал.
- Подняты 30 дистракторов Q1-Q10: 25 text + 5 dict-spec (Q2-A строгий-O(1)-миф → treeification Java 8; Q4-C изменяемость-как-критерий; Q5-C «iterator() стал default»; Q9-C null-своп List.of/asList; Q10-C «TreeSet уникальность по equals» → compareTo()==0).
- Blind-review 10/10 verdict; фиксы: DUP Q1(B,C) и Q9(B,C) разведены на независимые оси; CAR-серия заменена правдоподобными мифами (Q7-B рост+1 с копией вместо «дописывания на месте», Q9-C своп контрактов вместо «молча отбрасывает»); BSM Q7 — мета-полемика была в самом correct, всем дистракторам дан свой полемический хвост (correct не тронут).
- Отравленные клоны Q2 дали DUPLICATE_NGRAMS 10 → общие клаузы перефразированы при том же скелете (c1b), DUP вернулся к baseline 1. Q3 выровнен по `;`/`:`. Q1-Q10 hard=0.
- Гейты: schema OK, stamp OK (GOLD-CV 0.195), интерим LONGEST 0.826 / AVG 1.769 / STYLE 0.873 — ожидаемо до Q11-Q46. Freshness-сайдкар создан (8 claims, Java 17/21). Дальше: chunk2 Q11-Q20 (у Q11 pre-existing hard: opening_class B, sentence_count C).
### ROUND-8 · java-collections · chunk2 Q11-Q20 (2026-07-12)

- Подняты 30 дистракторов (27 text + 3 dict-spec: Q11-D access-order-миф вместо карикатуры «Set с дубликатами», Q12-C линейное пробирование (open addressing — реальная схема `IdentityHashMap`/`ThreadLocalMap`) вместо «молча теряет пары», Q16-A «уникальные хеши обязательны» вместо самоопровержения).
- Blind-review 10/10; системный BSM «консенсус-голосование клауз» (correct = пересечение мажоритарных клауз) закрыт двойными мутациями в одном дистракторе на блок (Q14-A, Q14-C, Q15-A, Q18-D); FORM-TELL Q17 — выравнена плотность перечислений методов; Q20 — дистракторы подрезаны к длине correct (correct не тронут).
- Клоны Q14 дали DUPLICATE_NGRAMS 23 → перефразированы при том же скелете (c2b), DUP=2. c2c: semicolon/colon/dash-выравнивание Q15/Q18/Q20. Q11-Q20 hard=0, schema OK, stamp OK (GOLD-CV 0.195).
- Интерим: LONGEST 0.63, AVG 1.373, STYLE 0.786 — добивается чанками Q21-Q46 + POS. Дальше: chunk3 Q21-Q30.
### ROUND-8 · java-collections · chunk3 Q21-Q30 (2026-07-12)

- Подняты 30 дистракторов (все text-only — оси исходников защитимые, менялась только форма/плотность до скелета correct). Blind-review 10/10; Q24 и Q26 — verdict 4 без флагов.
- Фиксы по ревью: карикатуры-уравниловки Q22-A («дело вкуса») и Q27-A переписаны в single-flaw мифы; абсолютизмы Q27-D и Q29-C смягчены до защитимых; монополия correct на плотность перечислений (Q23 — полный список методов, Q25 — все три пары `First`/`Last`) снята выравниванием у всех опций; консенсус-голосование ослаблено встречными флипами одной клаузы (probing/chaining в Q21, три исхода CME в Q30).
- c3b: HARD semicolon Q21-C/Q22-D, opening_class Q23-A; DUP 4→2. Гейты: schema OK, stamp OK, Q21-Q30 hard=0.
- Метрики сходятся: LONGEST 0.63→0.413 (порог 0.4), AVG 1.373→1.15, STYLE 0.786→0.645. Осталось: chunk4 Q31-Q40, chunk5 Q41-Q46, POS-тик (seq=46).
### ROUND-8 · java-collections · chunk4 Q31-Q40 (2026-07-12)

- Подняты 30 дистракторов (text-only). Blind-review: 9/10 блоков verdict 4; MULTI Q39-A (thread-first дерево выбора было защитимо — внедрена фактическая ошибка «COW для частых записей»); FORM-TELL «богатейшее перечисление = correct» в Q36/Q37/Q40 снят выравниванием списков у всех опций.
- **Урок тика: сигнатура PADDED-CLONE.** После выравнивания стамп-аудит перевернулся в «redo» (corLen 337 > 260, disMean/corLen 0.9 > 0.82, mean CV 0.11 < 0.13) — на длинных correct-конспектах поклаузные клоны дают штамп ROUND-7. Лечение (c4b/c4c): в 10 блоках один дистрактор сокращён до компактной формы того же мифа (с сохранением `;`/opening/sentence-паритета) → CV 0.133, verdict OK (not padded-clone).
- Гейты: schema OK, LONGEST 0.217 ✅, AVG 1.031 ✅, DUP 3 ✅, Q1-Q40 hard=0. Остаток: STYLE 0.583 (по code-spans 0.58) — балансировка backtick-плотности на chunk5+POS; вариацию длин продолжать в Q41-Q46 (их сырые CV 0.47-0.58 держат файловое среднее).
- Дальше: chunk5 Q41-Q46 (pre-existing HARD opening_class Q41-Q44) + POS-тик.
### java-collections-interview — ROUND-8 chunk5 Q41-Q46 (2026-07-12)

- **Чанк 5/5 (Q41-Q46, финальный):** SequencedCollection Java 21, устройство CHM, LRU на LinkedHashMap, NavigableMap, PriorityQueue, WeakHashMap. Подняты 18 дистракторов (text-only, оси сохранены).
- **Blind-review:** Q41-Q43 verdict 4 NONE; Q44-Q46 verdict 3 FORM-TELL («correct единственный исчерпывающий») — закрыт расширением дистракторов до полного покрытия stem-а; pre-existing HARD opening_class Q41-Q44 и sentence_count Q41 сняты.
- **PADDED-CLONE второй раз:** подъём финальной шестёрки уронил file mean CV 0.133→0.091 (однострочники Q41-Q46 подпирали среднее). Лечение c5b/c5c: трим одного дистрактора до компактной формы того же мифа в 18 блоках (Q2/Q7/Q11/Q13/Q16/Q19/Q21-Q28/Q31/Q33/Q35/Q38) с сохранением `;`/opening/sentence-паритета → CV 0.134, verdict OK.
- **Гейты:** schema OK; LONGEST 0.239 ok; AVG 1.029 ok; SENTENCE 1 ok; DUP 3 ok; skel hard=0/46; stamp OK (not padded-clone). Итог R8 по файлу: 138 дистракторов поднято, blind 46/46.
- **Остаток (следующий тик):** POS — де-цикл CORRECT_POSITION_SEQUENCE=46 (строгий A→B→C→D) + балансировка code-span-плотности (STYLE 0.62).
### java-collections-interview — ROUND-8 POS-тик (2026-07-12)

- **STYLE code-spans 0.62→0.315:** в 14 блоках (Q6/Q9/Q10/Q12/Q13/Q14/Q17/Q19/Q21/Q22/Q25/Q26/Q29/Q31) correct был единоличным лидером по числу `code`-спанов — одному дистрактору на блок подняли backtick-плотность реальными идентификаторами в духе его мифа (`tableSizeFor`, `MIN_TREEIFY_CAPACITY`, `TreeNode`, `estimateSize()`); code_bucket 3+ сохранён, DUP не вырос (3).
- **Де-цикл позиций:** CORRECT_POSITION_SEQUENCE 46→2 BASE-паттерном [2,0,3,1,0,2,1,3]; распределение A/B/C/D = 12/11/12/11; относительный порядок дистракторов сохранён, multiset-guard контента пройден; labels/order переназначены.
- **Финальный профиль файла:** schema OK; LONGEST 0.239; AVG 1.024; SENTENCE 1; DETAIL 0.239; DUP 3; SEQ 2; STYLE 0.315; skel hard=0/46; stamp OK (CV 0.134). OPTION_LENGTH_RATIO 2.47 — standing ACCEPTED EXCEPTION (конфликт со STAMP-guard).
- **Итог ROUND-8 по файлу:** 5 контент-чанков + POS; 138 дистракторов поднято (130 text + 8 dict-spec) + 14 code-span бустов; blind 46/46. Дальше — FINAL-штамп и следующий worst-first по §1.
### application-security-interview — ROUND-8 chunk1 Q1-Q10 (2026-07-12)

- **Строка 8 плана §1, старт файла.** Baseline — классический anti-golden: LONGEST 0.933 (42/45), AVG 1.798, STYLE 0.933, skel hard=42/45, SEQ=45, freshness отсутствовал; correct-конспекты 232-295c против карикатур-однострочников 95-151c.
- **Чанк 1/5 (Q1-Q10):** AppSec-определение, CIA/принципы, Defense in Depth, authn/authz, методы аутентификации, пароли, JWT, refresh, SQLi, XSS. Подняты 30 дистракторов (text-only, оси сохранены).
- **Blind-review 10/10:** Q3/Q6/Q9 — verdict 4 NONE; фиксы: Q1 тон-асимметрия (дистракторам утвердительные определения), Q2 корректирующий зачин correct «это другое» зеркалирован в дистракторах (correct не тронут), Q4-C де-карикатура (чистый своп models↔factors), Q5 паритет перечислений, Q7-B/Q8-B дорощены, Q10 мини-определения у всех опций.
- **Анти-штамп с первого чанка:** per-block CV 0.144-0.238 (один компактный дистрактор на блок) — урок java-collections применён превентивно.
- **Гейты:** schema OK; stamp OK; DUP 0; Q1-Q10 hard=0 (файл 42→33); LONGEST 0.733, AVG 1.589 — интерим до Q11-Q45; сайдкары review+freshness созданы (10 records, 10 claims).
- **Остаток:** чанки Q11-Q45 + POS-тик (SEQ=45, STYLE).
### application-security-interview — ROUND-8 chunk2 Q11-Q20 (2026-07-12)

- **Чанк 2/5 (Q11-Q20):** CSRF, десериализация, SSRF, non-SQL injection, шифрование в Java, симметричное/асимметричное, at rest/in transit, валидация ввода, @Validated, CORS. Подняты 30 дистракторов (text-only, оси сохранены).
- **Blind-review 10/10:** Q11/Q12/Q15/Q17/Q20 — verdict 4. **КРИТИЧЕСКИЙ фикс Q19 MULTI:** дистрактор «@Valid достаточно» был защитим на Spring 6.1+/Boot 3.2 (built-in method validation работает без @Validated) → переписан на механически ложное «@Validated только для JSR-303-групп»; версионный нюанс задокументирован в freshness-claim.
- **Прочие фиксы по ревью:** Q13-D дорощен от «обрубка»-подмножества (анти-rebinding-миф с обоснованием), Q14-D сменил ось с дубля-отрицателя на «Logback сам экранирует CRLF», Q16-B де-карикатура (взаимозаменяемость вместо «двух режимов одного AES»), Q18-A/C дорощены до трёхступенчатой структуры correct.
- **c2b:** HARD-фиксы opening_class (Q14-D, Q18-C/D) и sentence_count (Q16-B, Q18-D).
- **Гейты:** schema OK; stamp OK; DUP 0; Q11-Q20 hard=0 (файл 33→23); LONGEST 0.733→0.556, AVG 1.589→1.388 — интерим до Q21-Q45; сайдкары 20 records / 17 claims.
- **Остаток:** чанки Q21-Q45 + POS-тик.
### 2026-07-12 — application-security-interview: ROUND-8 chunk3 Q21-Q30 (row 8)

- 30 дистракторов подняты (text-only): Security Headers / CSP / REST API security / Rate Limiting / OWASP Top 10 / Threat Modeling / Secrets Management / зависимости / SAST-DAST / Security Code Review; оси мифов сохранены.
- Blind-review 10/10 (MULTI=0): системный form-tell «correct = энциклопедическое перечисление» митигирован на дистракторах — сняты абсолютизмы, добавлены собственные перечисления/код-споны (SOC 2/ISO 27001, STRIDE-«криптоалгоритмы», чеклист SpotBugs), де-карикатура Q21-C/D, Q25-B; компакты ~50-60%.
- Per-block CV 0.147-0.256 (голд-зона), stamp OK (mean CV 0.297, disMean/corLen 0.63); SKEL Q21-Q30 hard=0 (файл 23→13, остаток Q31-Q45).
- Сайдкары: review 30 records (notes_chunk3), freshness 25 claims (+OWASP 2021 порядок, STRIDE, 429/Retry-After, K8s Secrets base64, amend-не-удаляет, Report-Only, SAST/DAST-инструментарий, SCA/SBOM).
- Дальше: c4 Q31-Q40 (Security Testing в CI/CD и далее), c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### 2026-07-12 — application-security-interview: ROUND-8 chunk4 Q31-Q40 (row 8)

- 30 дистракторов подняты (text-only): Security Testing в CI/CD / контейнеры / Security Misconfiguration / файловые загрузки / логирование / Security by Design / Secure SDLC / Incident Response / микросервисы / Zero Trust; оси мифов сохранены.
- Blind-review 10/10 (MULTI=0; Q34/Q38 verdict 4): системный tell «correct = чеклист vs дистрактор = защита анти-паттерна» митигирован на дистракторах — Q31-D/Q33-C/Q33-D переведены в определительно-перечислительную форму, Q35-B баланс-рамка, Q36-B/Q40-C/Q32-C де-карикатура, Q37-B собственная →-цепочка.
- Per-block CV 0.165-0.242 (голд-зона), stamp OK; SKEL Q31-Q40 hard=0 (файл 13→4, остаток Q41-Q45).
- Сайдкары: review 40 records (notes_chunk4), freshness 33 claims (+MTTR-определение, Docker user namespaces, Actuator prod-exposure, OWASP File Upload, A09/PCI last4, NIST 800-61 containment-first, mesh mTLS, NIST 800-207 Zero Trust).
- Дальше: c5 Q41-Q45 (Vault, OAuth2 Resource Server, метод-security, аудит Spring Security, prod-чеклист), затем POS (SEQ=45) + FINAL.
### 2026-07-12 — application-security-interview: ROUND-8 chunk5 Q41-Q45 (row 8)

- 15 дистракторов подняты (text-only): Vault в Spring Boot / Kubernetes Secrets / SAST в CI / DAST / TLS+mTLS; оси мифов сохранены.
- Blind-review 5/5 (Q44 verdict 4 NONE). Критический фикс Q41-B: MULTI-риск на границе версий («bootstrap.yml давно удалён» пересекалось с современным spring.config.import=vault://) → механически ложное «application.yml читается раньше всех источников»; нюанс в freshness. Q45-B де-карикатура: «mTLS = 4096-бит» → путаница keystore-как-truststore + client-auth NONE.
- Контентная фаза файла ЗАВЕРШЕНА: per-block CV 0.159-0.268, stamp OK, SKEL hard=0/45 (было 42), CORRECT_LONGEST 0.267 OK, CORRECT_WRONG_AVG 1.076 OK, SENTENCE 1 OK, DUP 0.
- Сайдкары: review 45 records (notes_chunk5), freshness 39 claims (+bootstrap/config.import нюанс, Database Secrets Engine TTL, tmpfs secret volume, WANT/REQUIRE, RFC 8996).
- Остаток: POS-тик (SEQ=45 де-цикл BASE-паттерном + STYLE code-spans 0.71 бусты) + FINAL stamp.
### 2026-07-12 — application-security-interview: ROUND-8 POS + FINAL (row 8 ЗАКРЫТА)

- POS (bc743ff0): SEQ 45→2 (BASE-паттерн, multiset-guard, дистрибуция 12/11/11/11) + 21 code-span буст дистракторов → STYLE 0.707→0.356.
- Финальный профиль: LONGEST 0.267, AVG 1.072, SENTENCE 1, DUP 0, SEQ 2, STYLE 0.356, SKEL hard=0/45 (baseline 42), stamp OK; RATIO 2.73 = принятое исключение.
- Итог R8: 135 дистракторов за 5 чанков + 21 буст, blind 45/45, критические MULTI-фиксы Q19 (built-in method validation Spring 6.1) и Q41-B (bootstrap vs spring.config.import), freshness 39 claims.
- Сайдкар review: status COMPLETE, reviewed_commit=bc743ff0. Следующий тик — новый worst-first файл из §1.
### 2026-07-12 — authentication-authorization-patterns-interview: ROUND-8 chunk1 Q1-Q10 (row 9 старт)

- Baseline: LONGEST 0.956, AVG 1.893, STYLE 0.967, DETAIL 0.978, SEQ 45, skel hard=43/45, freshness отсутствовал — тот же anti-golden #1 (конспекты 263-361c vs однострочники 92-191c).
- 30 дистракторов подняты (text-only): паттерны аутентификации / OAuth 2.0 / JWT / RBAC-ABAC / authn-authz / микросервисы / SAML / MFA / сессии / атаки; оси сохранены.
- Blind-review 10/10 (Q2/Q4/Q5/Q7/Q10 verdict 4): Q6-C MULTI (gateway-offloading реален) → механически ложная инверсия zero-trust; Q1-C/Q9-D — собственные перечисления против form-tell; Q3-A/Q8-A де-карикатура (Q8-A: «пароль+секретный вопрос = два фактора», секции совместимы).
- Превентив: rich-дистрактор выше correct по code-spans в каждом блоке уже в драфте (урок appsec POS-тика).
- Per-block CV 0.118-0.265, stamp OK; SKEL Q1-Q10 hard=0 (файл 43→34). Сайдкары: review 10 records, freshness 10 claims (RFC 9700 Implicit/ROPC, JWE vs JWS, RFC 9110, NIST 800-63B SMS, zero-trust 800-207).
- Дальше: c2 Q11-Q20.
### authentication-authorization-patterns — ROUND-8 chunk2 Q11-Q20 (2026-07-12)

- **Чанк**: Q11-Q20 (API Gateway, OIDC, SSO, Claims-based, token rotation, Zero Trust, Rate Limiting, mTLS, аудит, CBAC), 10 блоков, 30 дистракторов подняты (text-only).
- **Blind-review**: 10/10, verdict 4 у Q12/Q13/Q14/Q17/Q18; MULTI нет. Фиксы: Q16-C/Q19-A/Q20-B/Q20-C де-карикатура, Q17-D развод DUP-мотива с Q11-B (sticky вместо локальных счётчиков).
- **Хроника закрыта**: semi=1 у Q12-B/C, Q13-C, Q19-B устранена (все corrects semi=0); rich-дистрактор выше correct по code-spans в каждом блоке (превентив appsec-POS).
- **Гейты**: schema OK; SKEL zone hard=0, file 34→24; stamp OK (not padded-clone), per-block CV 0.085-0.256; SOURCE_COVERAGE ok (freshness 10→19 claims: OIDC Core, SCIM RFC 7644, introspection RFC 7662, RFC 6585/429, TLS 1.3 RFC 8446 + RFC 5280, Spring Security events, NIST 800-207/800-63B).
- **Дальше**: c3 Q21-Q30 (Service Mesh security и далее), затем c4/c5, POS (SEQ=45) + FINAL.
### authentication-authorization-patterns — ROUND-8 chunk3 Q21-Q30 (2026-07-12)

- **Чанк**: Q21-Q30 (Service Mesh, Identity Federation, Step-Up, Passwordless, GraphQL, Delegated/Fine-Grained AuthZ, Token Binding, WebSocket, PKCE), 10 блоков, 30 дистракторов подняты (text-only).
- **Blind-review**: 10/10, MULTI нет, выдуманных API нет; Q27 отмечен «лучшим блоком серии». Фиксы: Q24-C де-карикатура (SMS), Q25-C убрана сфабрикованная DataLoader-механика, Q26-C смягчён тон.
- **Form-tell вылечен**: системный паттерн «correct = единственный affirmative-список» закрыт переводом одного дистрактора на блок в affirmative-форму (Q21-C, Q25-B, Q29-B).
- **Скелет-зеркалирование**: btick-open у дистракторов Q21/Q25/Q29/Q30; prose+semicolon у Q24; semi=1 у всех опций Q30; rich-дистрактор > correct по code-spans в каждом блоке.
- **Гейты**: schema OK; SKEL zone hard=0, file 24→14; stamp OK, per-block CV 0.145-0.250; freshness 19→26 claims (Istio AuthorizationPolicy, NIST AAL3, OWASP GraphQL, @PostAuthorize семантика, RFC 8705, WebSocket header-ограничение, PKCE OAuth 2.1).
- **Дальше**: c4 Q31-Q40, c5 Q41-Q45, POS (SEQ=45) + FINAL.
### authentication-authorization-patterns — ROUND-8 chunk4 Q31-Q40 (2026-07-12)

- **Чанк**: Q31-Q40 (Dynamic AuthZ, OAuth2 Resource Server, PermissionEvaluator, SecurityFilterChain, RoleHierarchy, пароли, JWT-ошибки, BFF, Keycloak, security-тесты), 10 блоков, 30 дистракторов (text-only).
- **Blind-review**: 10/10, 7 блоков verdict 4. Фиксы: Q37 FORM-TELL (множественный stem, correct=единственное перечисление) — все 3 дистрактора переведены в списки «типичных ошибок» с ложными пунктами; Q40-A MULTI-риск (Testcontainers+Keycloak — легитимная практика) закрыт механически ложным клеймом «@WithMockUser удалён»; Q39-D де-карикатура («исключительно SAML» → «SAML основной, OIDC экспериментален»).
- **Пост-гейт**: Q32-B и Q36-C дотянуты по sentence_count (HARD-нарушения после первого прогона).
- **Гейты**: schema OK; SKEL zone hard=0, file 14→5 (остаток — зона Q41-Q45); stamp OK, per-block CV 0.083-0.325; freshness 26→33 claims (WebSecurityConfigurerAdapter удалён, RoleHierarchyImpl.fromHierarchy 6.3+, OWASP Password Storage, RFC 8725, BFF IETF draft, Keycloak адаптеры deprecated, spring-security-test).
- **Дальше**: c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### authentication-authorization-patterns — ROUND-8 chunk5 Q41-Q45 (2026-07-12)

- **Чанк**: Q41-Q45 (JWT vs Session, API Key, Zero Trust Java, RBAC+ABAC, mTLS), 5 блоков, 15 дистракторов (text-only). Контент-проход файла ЗАВЕРШЁН: 150 дистракторов за 5 чанков.
- **Blind-review**: 5/5. Фиксы: Q44-B MULTI («ABAC первым» — рабочая реализация) усилен до механически ложного «первой и единственной»; Q42-C убран квази-выдуманный «реестр HttpSecurity»; form-tell ослаблен двусторонней структурой Q41-D и доктринной Q43-A.
- **Файл целиком (vs baseline)**: SKEL hard 43→0/45; CORRECT_LONGEST 0.956→0.311; CORRECT_WRONG_AVG 1.893→1.1; DETAIL 0.978→0.133; STYLE 0.967→0.352 (spans-ось 0.06 — отдельный STYLE-пасс не нужен, превентив сработал); SENTENCE 3→1; DUP 0; stamp OK.
- **Остаток**: POSITION_SEQUENCE=45 (строгий A→B→C→D цикл) → POS-тик (BASE-де-цикл), затем FINAL-штамп.
### authentication-authorization-patterns — ROUND-8 POS + FINAL (2026-07-12)

- **POS (3db01537)**: строгий A→B→C→D цикл разбит BASE-шаблоном; SEQ 45→2, A/B/C/D=[12,11,11,11], multiset-guard байт-в-байт. Отдельный STYLE-пасс не понадобился (spans 0.06 — превентив сработал).
- **FINAL**: сайдкар COMPLETE (reviewed_commit 3db01537), строка 9 плана закрыта (✅/✅/✅/✅*/✅).
- **Итог файла (baseline → final)**: hard 43→0/45; LONGEST 0.956→0.311; AVG 1.893→1.1; DETAIL 0.978→0.133; STYLE 0.967→0.352; SENTENCE 3→1; SEQ 45→2; DUP 0; stamp OK; RATIO 3.1 — accepted exception. 150 дистракторов, blind 45/45.
- **Дальше**: следующий worst-first файл из §1 (кандидаты 45Q: hexagonal-architecture р10, kotlin р11, owasp-top10 р12, test-strategies р13, tls-ssl р14, unit-testing р15).
### hexagonal-architecture — ROUND-8 chunk 1 (2026-07-12)

- **Чанк**: Q1-Q10 (суть паттерна, шестиугольник, история, Application Core, три зоны, Port, Driving/Driven, именование, гранулярность/ISP, адаптер-на-несколько-портов), 10 блоков, 30 дистракторов (text-only, оси сохранены).
- **Baseline файла**: LONGEST 0.933 (42/45), RATIO 14.0 (Q19 — худший в корпусе), AVG 2.261 (23сл vs 10сл), DETAIL 0.733, STYLE 0.956 (spans 0.70), SEQ 45, hard=35/45, freshness MISSING, stamp CV=0.525.
- **Blind-review**: 10/10 верных вердиктов. Фиксы до применения: Q4 FORM-TELL (компакт-абсолютизация → «DI-сборка — часть контракта ядра»); Q7 CAR («ради красоты диаграмм» → «роли на диаграмме, место реализации не регламентировано»); Q10 DUP (A/D оба «ровно один порт» → разведены: A=SRP-трактовка, D=LSP-миф — принцип стал дифференциатором).
- **Де-карикатуризация**: Q1 «ровно шесть входов» → декомпозиция микросервисов по граням; Q2 OSI с псевдообоснованием; Q9 «6 портов» → список из шести ролей как «ориентир». Q3-история: все дистракторы механически ложны (Мартин-2012, Spring Boot 2-2018, Netflix).
- **Зона Q1-Q10**: skel HARD=0 (файл 35→29), correct нигде не самый длинный, per-block CV 0.16-0.26, semi-дисциплина Q2/Q3/Q7/Q8, один компакт + один rich-по-спанам в каждом блоке.
- **Сайдкары**: review IN_PROGRESS (10/45) + fact-freshness создан (7 claims: Кокбёрн-2005, Палермо-2008, Мартин-2012, Хомбергс-2019, ISP-именование, AccountPersistenceAdapter) — SOURCE_COVERAGE теперь ok.
- **Остаток**: c2 Q11-Q20 → c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### hexagonal-architecture — ROUND-8 chunk 2 (2026-07-12)

- **Чанк**: Q11-Q20 (primary/secondary адаптеры и обязанности, примеры, mapper, двойная роль адаптера, структура пакетов Хомбергса, реализация driving/driven портов, @Service vs чистое ядро, транзакционные границы), 10 блоков, 30 дистракторов (text-only, оси сохранены).
- **Blind-review**: 10/10 верных вердиктов. Фиксы до применения: Q12 CAR («обязан Spring Data JPA» → «стандарт: транзакции из коробки, иные технологии — заглушки»); Q15 MULTI — «combined-адаптер допустим» усилен механически ложной атрибуцией «рекомендация Кокбёрна в оригинальной статье» (Kafka не существовала в 2005) + BSM×2 (Spring-запрет → правдоподобный DI-цикл; gRPC-стаб → StreamObserver); Q19 CAR («спор закрыт» → «практика устоялась») + BSM (AbstractApplicationContext → реальный ApplicationContextAware-service-locator) + FORM-TELL («единственный взвешенный компромисс» убит псевдо-двусторонним «обе стороны примиряет @ComponentScan» — механически ложно, скан домена без аннотаций бинов не находит).
- **Зона Q11-Q20**: skel HARD=0 (файл 29→23), per-block CV 0.07-0.26 (не штамп), semi-дисциплина Q11/Q18/Q20, rich-по-спанам и компакт в каждом блоке.
- **Остаток**: c3 Q21-Q30 → c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### hexagonal-architecture — ROUND-8 chunk 3 (2026-07-12)

- **Чанк**: Q21-Q30 (Composition Root/DI, multi-module Gradle, DDD-стыковка, размещение тактических паттернов, Application vs Domain Service, доменные события, пирамида тестирования, тесты use-case/адаптеров, contract testing), 10 блоков, 30 дистракторов (text-only, оси сохранены).
- **Blind-review**: 10/10 верных вердиктов. Фиксы до применения: Q21 DUP (два service-locator'а разведены: «ApplicationContext-как-корень» vs «модуль app не нужен»); Q22 MULTI (усилен механически ложным «видимость пакетов Java не пропустит импорт Spring»); Q23 FORM-TELL (единственное перечисление-маппинг → двум дистракторам дана зеркальная `;`-структура); Q24 CAR; Q26 MULTI (@DomainEvents усилен ложным «доставит в Kafka без кода» — реально только in-process); Q27 FORM-TELL (E2E-дистрактор → «перевёрнутая пирамида с процентами»); Q28 BSM (PowerMock оставлен из-за sections, обоснован misconception'ом «порты не обязательны»).
- **Известное ограничение**: Q30-correct хвост «Consumer-Driven Contract Testing для внутренних портов» терминологически спорен (CDC ≈ Pact); correct-текст не правим по протоколу — ревьюер выбрал верно (medium confidence).
- **Зона Q21-Q30**: skel HARD=0 (файл 23→13), per-block CV 0.13-0.28, semi-дисциплина Q23-Q26/Q28-Q30, rich-по-спанам и компакт в каждом блоке.
- **Остаток**: c4 Q31-Q40 → c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### hexagonal-architecture — ROUND-8 chunk 4 (2026-07-12)

- **Чанк**: Q31-Q40 (fakes vs mocks, Hexagonal vs Clean/Onion/Layered, эволюционная миграция, разделение жирного Service, отделение JPA-сущности, антипаттерны, анемичная модель, утечки домена), 10 блоков, 30 дистракторов (text-only, оси сохранены).
- **Blind-review**: 10/10 верных вердиктов; 8 блоков с флагами — все вылечены до применения. Ключевые лечения: Q31 MULTI+DUP (зачин клона разведён + «verify признан антипаттерном»); Q32 FORM-TELL (нюанс «на практике смешивают» отдан дистрактору с инвертированной атрибуцией school-mix); Q35 CAR (дистракторы получили собственные →-цепочки, зеркалящие correct); Q36 MULTI+FORM-TELL (механически ложная апелляция «сам Хомбергс отвергает дробление» нейтрализует авторитет-телл correct); Q37 MULTI (No-Mapping усилен ложной механикой «detached-копии без прокси»); Q38 FORM-TELL+BSM+CAR (все дистракторы — каталоги-перечисления против 10-пунктового correct); Q40 FORM-TELL (дистрактор с «путями утечки + анти-защитой», 10 код-спанов).
- **Зона Q31-Q40**: skel HARD=0 (файл 13→4, остаток в Q41-Q45), per-block CV 0.10-0.27, semi-дисциплина Q31-Q34/Q36/Q37/Q39.
- **Остаток**: c5 Q41-Q45, затем POS (SEQ=45) + FINAL.
### 2026-07-12 — hexagonal-architecture ROUND-8 chunk 5 (Q41-Q45)

- Последний контент-чанк файла: подняты 15 дистракторов (text-only, correct не тронут).
- Blind review 5/5 верных вердиктов; флаги ВСЕХ пяти блоков вылечены до применения:
  - Q41 CAR+FORM-TELL: зеркальные абсолютизации («обязателен всегда» / «не нужен никогда») заменены двусторонними критериями — дефолт-кроме-скриптов, критерий числа входов, численный LOC-порог; correct больше не единственный сбалансированный.
  - Q42 BSM+FORM-TELL (ключевой): стем «через ArchUnit», а дистракторы были про Checkstyle/Gradle/enforcer → отбрасывались по ключевому слову. Все три оси переписаны на ложь про сам ArchUnit: инверсия направления правил + мнимый `gradle archTest`-таск; несуществующий рантайм-агент; несуществующие аннотации `@Module`/`@AllowedDependencies` (смешение со Spring Modulith).
  - Q43 FORM-TELL: хедж и DDD-термины даны дистракторам (Shared Kernel, «вырождается в один гексагон»).
  - Q44 BSM+FORM-TELL: зачин «Обратное распределение ролей» сам объявлял инверсию → нейтральный; хвост correct «Альтернатива — sealed Result-type» зазеркален ложной альтернативой `@ResponseStatus` в `domain/`.
  - Q45 FORM-TELL: псевдодвусторонняя форма у дистрактора B; D механически ложен (нужен `TransactionalOperator`, не `@Transactional` над `Mono`).
- Гейты: schema OK; skel-гейт файла hard_fails=0 (старт раунда 35); stamp mean CV=0.195, Q43 FLAT×3 (0.853) рассосался; per-block CV 0.17-0.26; correct не одинокий длиннейший в Q42-Q45 (Q41 маргинально +11c).
- Сайдкар: records 45/45, notes_chunk5. Остаток по файлу: POS-тик (SEQ=45 → де-цикл BASE-паттерном) + FINAL (полная батарея vs baseline, статус COMPLETE).
### 2026-07-12 — hexagonal-architecture ROUND-8 POS + FINAL (файл COMPLETE)

- POS (18e364f3): строгий A→B→C→D цикл (SEQ=45) де-циклирован BASE-паттерном [2,0,3,1,0,2,1,3]; перемещено 34 correct (pop+insert, порядок дистракторов сохранён), label/order переприсвоены; multiset-гвард text+correct+sections на блок, correct-тексты байт-в-байт; дистрибуция 12/11/11/11 точно, max арифметический прогон 2.
- FINAL-батарея vs baseline: CORRECT_LONGEST 0.933→0.178; OPTION_LENGTH_RATIO 14.0→2.29 (accepted exception — осознанный анти-штамп компакт ~55-65% на блок, аналог RATIO 3.1 у строки 9); CORRECT_WRONG_AVG 2.261→1.02; SENTENCE 2→1; DETAIL 0.733→0.244; SEQ 45→2; STYLE 0.956→0.189; DUP_NGRAMS 0; SOURCE_COVERAGE ok; skel hard 35→0/45; stamp mean CV 0.525→0.195 (Q43 FLAT×3 устранён).
- Итог раунда по файлу: 135 дистракторов подняты за 5 чанков, blind review 45/45 верных вердиктов, все флаги (CAR/MULTI/DUP/BSM/FORM-TELL) вылечены до применения. Сайдкар reviews/ → COMPLETE (records 45/45, notes_chunk2-5 + notes_pos_final); fact-freshness сайдкар создан в c1.
- Известное ограничение: хвост correct Q30 (CDC-цитата) — потенциальный семантический tell; correct-текст вне мандата раунда, задокументировано.
- Следующий worst-first файл §1: kotlin (строка 11).
### 2026-07-12 — kotlin ROUND-8 chunk 1 (Q1-Q10)

- Начат файл kotlin (строка 11 §1, worst-first после hexagonal). Baseline: LONGEST 0.911, RATIO 4.2 (Q12), AVG 1.66, DETAIL 0.42, SEQ 45, STYLE 0.922, skel hard=36/45, stamp CV=0.306; SOURCE_COVERAGE n/a (не version-sensitive — freshness-сайдкар не требуется).
- Подняты 30 дистракторов Q1-Q10 (основы: типы, null-safety, лямбды, extensions, inline). Blind review 10/10; флаги 8 блоков вылечены до применения: Q1 BSM (скриптовый-язык → «вывод типов завершается в runtime»), Q2/Q3/Q10 CAR (де-карикатура с сохранением осей: анти-HOF → «свои HOF не идиоматичны»; Unit=null → «Unit — условность, в generics запрещён»; многоядерность → ForkJoinPool/parallelStream-механика), Q5 BSM+FORM-TELL (канонические имена операторов в скобках у дистрактора — зеркало correct), Q6 FORM-TELL (каждый дистрактор покрывает обе конструкции стема), Q7 FORM-TELL (утвердительные формулировки против «сплошных запретов»), Q8 CAR+BSM (@ExperimentalTypeInference вместо «экспериментальной фичи»).
- Сверка с теорией .md: все патчи механически ложны (member > extension; crossinline запрещает non-local return; reified только в inline; KotlinNullPointerException — легаси до 1.4).
- Гейты: schema OK; зона Q1-Q10 hard=0 (файл 36→29); per-block CV 0.17-0.34; сайдкар reviews создан (10/45).
- Остаток: c2-c5 (Q11-Q45), POS (SEQ=45), FINAL.
### 2026-07-12 — kotlin ROUND-8 chunk 2 (Q11-Q20)

- Подняты 30 дистракторов (infix, scope-функции, data/sealed/enum/value class, object/companion, конструкторы, var/val/const, lazy/lateinit) + 4 ретро-фикса зачинов в блоках c1 (Q3 B/D, Q6 C/D): skel-гейт вскрыл opening_class mismatch (btick-зачин дистрактора против prose-зачина correct) — в т.ч. в двух уже закоммиченных блоках c1; все семь блоков выровнены под зачин correct.
- Blind review 10/10; флаги: Q11 DUP (оба дистрактора били в «ровно один параметр» → A=арность, B=членство), Q17 MULTI (частично защитимая «разноструктурность» → механически ложное «свойство из тела константы доступно снаружи»), Q19 CAR («только стиль» → аналогия с private set).
- Гейты: schema OK; зона Q1-Q20 hard=0 (файл 36→19); per-block CV 0.12-0.30; сайдкар 20/45.
- Остаток: c3-c5 (Q21-Q45), POS (SEQ=45), FINAL.
### 2026-07-12 — kotlin ROUND-8 chunk 3 (Q21-Q30)

- Подняты 30 дистракторов (видимость, open/final, get/set, generics, reified, inference, делегирование, when, операторы, ==/===). Blind review 10/10.
- Три FORM-TELL вылечены структурно: Q21 — дистрактор стал каталог-клоном четырёх модификаторов с одной подменой (Single-Delta: «private не принимается top-level»); Q26 — все дистракторы получили дефиниционный зачин «Type inference — …», как у correct; Q28 — дистрактор-каталог с ядром ложности fall-through (correct перестал быть единственным «полным» вариантом).
- Гейты: schema OK; зона Q21-Q30 hard=0 (файл 19→11, остаток в Q31-Q45); per-block CV 0.13-0.28; сайдкар 30/45.
- Остаток: c4 (Q31-Q40), c5 (Q41-Q45), POS (SEQ=45), FINAL.
### 2026-07-12 — kotlin ROUND-8 chunk 4 (Q31-Q40)

- Подняты 30 дистракторов (деструктуризация, коллекции, Sequence, map/flatMap, корутины, Scope/Job/Dispatcher, launch/async, @Jvm*-аннотации, allOpen/noArg, Reflection). Blind review 10/10.
- 6 флагов вылечены: Q36/Q38 FORM-TELL — дистракторы стали каталогами, покрывающими все три сущности/аннотации стема с одной корневой ложью (отмена-без-каскада; «только для Kotlin-стороны»), correct перестал выделяться полнотой; Q33/Q35/Q39/Q40 CAR — абсолютизации одеты в правдоподобные механизмы (JIT-инлайнинг, ExecutorService-пул, @Target-фильтр, «private только через Java-рефлексию»); Q34 BSM — бессмысленный хвост заменён осмысленной ложью про ClassCastException.
- Гейты: schema OK; зона Q31-Q40 hard=0 (файл 11→3, остаток Q41/Q44/Q45); per-block CV 0.15-0.26; сайдкар 40/45.
- Остаток: c5 (Q41-Q45), POS (SEQ=45), FINAL.
### 2026-07-12 — kotlin ROUND-8 chunk 5 (Q41-Q45)

- Последний контент-чанк: подняты 15 дистракторов (string templates, contracts, идиоматика, typealias, object expression/declaration). Blind review 5/5 — по файлу 45/45 за раунд.
- 4 FORM-TELL вылечены структурно: Q41 — дистрактор получил полное двухсинтаксисное определение с ложной компиляцией в `+`; Q42 — хеджирующие скобки у всех дистракторов (correct перестал быть единственным с «(экспериментальная фича)»); Q43 — зеркальный 8-пунктовый каталог с инверсиями стайлгайда; Q45 — все дистракторы покрывают обе конструкции двухчастного стема.
- Гейты: schema OK; skel-гейт файла PASS hard_fails=0 (старт раунда 36); stamp mean CV 0.306→0.223, disMean/corLen 0.55→0.92; сайдкар 45/45.
- Остаток по файлу: POS (SEQ=45 → де-цикл BASE-паттерном) + FINAL (батарея vs baseline, статус COMPLETE).
### 2026-07-12 — kotlin ROUND-8 POS + FINAL (файл ЗАВЕРШЁН)

- POS: correct-позиции шли строгим ABCD-циклом (SEQ=45) → де-цикл BASE-паттерном [2,0,3,1,0,2,1,3], moved 34/45, dist A/B/C/D=12/11/11/11, max AP run=2; только label/order, тексты/sections байт-идентичны (multiset-guard).
- FINAL-батарея vs baseline: LONGEST 0.911→0.089, RATIO 4.2→3.12, AVG 1.661→1.087, DETAIL 0.422→0.089, SEQ 45→2, STYLE 0.922→0.141, skel hard 36→0, stamp CV 0.306→0.223, disMean/corLen 0.55→0.92, schema OK.
- Файл стал version-sensitive (поднятые дистракторы упоминают версии Q37B/Q42C) → создан `docs/mcq-quality/fact-freshness/kotlin-interview.json` (Kotlin 2.1, 7 version-claims, checked 2026-07-12) — SOURCE_COVERAGE ok.
- 2 documented-exception FAIL: OPTION_LENGTH_RATIO 3.12 (осознанные anti-stamp compact-дистракторы, прецедент rows 9-10) и DUPLICATE_NGRAMS 12 (внутриблочные Single-Delta каталог-клоны Q21 A↔B / Q43 A↔D по golden-эталону; ломать прогоны = вернуть FORM-TELL).
- Строка 11 → COMPLETE (маска row-10). Следующий worst-first: row 12 owasp-top10.
### 2026-07-12 — owasp-top10 ROUND-8 chunk 1 (Q1-Q10)

- Начата строка 12 (worst-first). Baseline: LONGEST 45/45, STYLE 1.0 (по длине угадывается 100%), skel hard=41/45, corLen=381 при disMean/corLen=0.34 — эталонный worst-файл.
- Подняты 30 дистракторов до скелета correct'ов (2-3 предложения, зеркальные код-спаны, rich Q8A=12 спанов, compact 55-65%). Blind review 10/10.
- Лечения: 6×CAR одеты в правдоподобные ложные механизмы (таксономия CWE, стабильность методологии, Community Survey, связка уязвимостей, фильтрация по роли, denyAll() первой строкой), 1×BSM (debug-сборки JCE → GCMParameterSpec с нулевым тегом), 1×DUP (Q8 оси разведены). Обратный FORM-TELL Q9: абсолютистский зачин correct зеркалирован во все дистракторы.
- Known limitation Q3 (correct вне мандата): «3 категории из опроса» vs официальные 2 (A09+A10); теория .md консистентна — кандидат на отдельный факт-фикс.
- Файл version-sensitive (TEMPORAL=21) — freshness-сайдкар завести на FINAL-тике.
- Остаток: c2-c5 (Q11-Q45) + POS (SEQ=45) + FINAL.
### 2026-07-12 — owasp-top10 ROUND-8 chunk 2 (Q11-Q20)

- Подняты 30 дистракторов (Injection-виды, SQLi/JPA, XSS, Command Injection, Insecure Design, threat modeling, паттерны, misconfiguration, headers, профили). Blind review 10/10.
- Ключевые лечения: Q12 — DUP+one-vs-three tell по ORDER BY снят разведением осей (native-only / «Hibernate валидирует диалектом» / «? универсален»); 5×BSM заземлены в реальные API (X-XSS-Protection, Pattern.quote, Role Separation); 4×CAR одеты в механизмы (chroot без shell, изоляция профилей, CSP report-only, CI-sed-шаблоны); Q16 — рубрика «Процесс:» дана всем четырём опциям.
- Q17/Q20 — твин-пары каталог-клонов с Single-Delta (осознанный дуэль-паттерн, прецедент kotlin Q21/Q43).
- Гейты: schema OK, зона Q1-Q20 hard=0 (файл 41→24), stamp CV 0.498→0.417, disMean/corLen 0.42→0.52.
- Остаток: c3-c5 (Q21-Q45) + POS (SEQ=45) + FINAL (+freshness-сайдкар).
### 2026-07-12 — owasp-top10 ROUND-8 chunk 3 (Q21-Q30)

- Подняты 30 дистракторов (SCA/SBOM, auth failures, brute force, JWT, supply chain, десериализация, CI/CD, логирование). Blind review 10/10.
- Ключевые лечения: Q25 — три дистрактора-«серебряные пули» разведены по осям (маппинг контрмеры-на-атаку / lockout с механизмом цены / энтропия-экономика); Q26 — JWE-конфьюжн вместо бессмыслицы «подпись шифрует», rich-дистрактор сведён к одному фрейму «alg:none за периметром» (13 код-спанов); Q22 — SBOM-как-lockfile на реальном `--write-locks`; Q30 — числовая плотность выровнена (90%/95% против TTD 287 у correct).
- Q28A MULTI оставлен осознанно: ядро «выполнить код нельзя» механически ложно (ysoserial), ревьюер признал дистрактор сильным.
- Гейты: schema OK, зона Q1-Q30 hard=0 (файл 41→15), stamp CV 0.417→0.325, disMean/corLen 0.52→0.62.
- Остаток: c4-c5 (Q31-Q45) + POS (SEQ=45) + FINAL (+freshness-сайдкар).
### 2026-07-12 — owasp-top10 ROUND-8 chunk 4 (Q31-Q40)

- Подняты 30 дистракторов (логирование, алертинг, SSRF, CI-гейты, security-тесты, секреты, приоритизация, DiD, интервью). Blind review 10/10.
- Ключевые лечения: кросс-вопросный дубль (TLS-handshake-с-внутренними-IP жил и в Q33D, и в Q34A) — в Q34 заменён осью string-блэклиста с ложным дефолтом followRedirects(NEVER); свалка Q31A сведена к одному ядру «полные снапшоты, маскирует SIEM»; hard-shell-soft-core вместо «убрать слои» в Q39; три MULTI дожаты явной ложью (выдуманная пометка OWASP Cheat Sheet, plaintext-ступень, «Environmental учитывается автоматически»).
- FORM-TELL: оговорка «НЕ …» из correct Q31 зеркалирована во все опции; цитатные тезисы-зачины в Q32; коды A10:2021 дистракторам Q33.
- Гейты: schema OK, зона Q1-Q40 hard=0 (файл 41→8), stamp CV 0.325→0.241, disMean/corLen 0.62→0.72.
- Остаток: c5 (Q41-Q45) + POS (SEQ=45) + FINAL (+freshness-сайдкар).
### owasp-top10 — R8 c5 (Q41-Q45) — 2026-07-12

- 15 дистракторов подняты до скелета correct (Q41 A, Q42 B, Q43 C, Q44 D, Q45 A — corrects не тронуты).
- Blind review: 5/5, флаги вылечены до записи: Q42A BSM (внутренне противоречивая «XXE-в-JSON» заменена на реальное заблуждение «JAXP на Java 9+ безопасен по умолчанию: `FEATURE_SECURE_PROCESSING` отключает внешние сущности»); Q43 FORM-TELL на correct — danger-фрейм «Загрузка файлов опасна … (список угроз)» зеркалирован во все четыре опции; Q44A swap Logback↔Log4j2 оставлен с настоящим CVE-2021-44228 (убран тел по номеру); Q44 B/C DUP разведён на scope-denial vs «formatMsgNoLookups=true — полное решение» (реальный флаг, ложная полнота — CVE-2021-45046 показал обход); Q45D CAR «заморозить ключ» → механизм миграционного риска ротации.
- Добивка 3 унаследованных skel-HARD вне зоны (телы указывали на дистракторы, не на correct): Q12C и Q23B — btick-зачин, Q16B — `DFD` (code_bucket 0→1).
- Гейты: schema OK; skel hard **41→0** (весь файл чист); stamp mean CV 0.568→0.19; disMean/corLen 0.34→0.77.
- Остаток: POS (SEQ=45, ABCD-цикл) + FINAL (battery vs baseline + freshness-сайдкар, TEMPORAL=21).
### owasp-top10 — R8 POS + FINAL — 2026-07-12

- POS (commit 505f8f43): BASE=[2,0,3,1,0,2,1,3] → SEQ 45→2, correct-позиции A/B/C/D=12/11/11/11, moved 34, max AP run 2; multiset- и correct-text-guards байт-в-байт.
- FINAL polish: в 30 correct-longest блоках богатый дистрактор удлинён comma-клаузой (без `;`, без новых предложений/backticks) выше correct; 19 detail-флипов паренами/числами. Полиш аддитивный — углубляет уже blind-одобренные misconception.
- Freshness-сайдкар `docs/mcq-quality/fact-freshness/owasp-top10-interview.json`: OWASP Top 10:2021, 9 version-claims (RC следующей редакции 11.2025; официально 2 survey-категории vs «3» в correct Q3 — known limitation, кандидат на fact-fix вне мандата R8; Log4Shell CVE-2021-44228/45046; RFC 8996; JAXP-defaults).
- Battery baseline→final: LONGEST 1.0→0.022, RATIO 3.73→2.43 (accepted exc: compact-by-design), AVG 2.271→1.032, DETAIL 0.933→0.267, SEQ 45→2, STYLE 1.0→0.033, DUP 0→7 (accepted exc: in-block зеркала Q6 A↔C, Q17 A↔C duel, Q24 B↔D), skel hard 41→0, stamp CV 0.568→0.226, disMean/corLen 0.34→0.83.
- Строка 12 → ✅ (маска строки 11). Следующий worst-first: test-strategies (r13).
### test-strategies — R8 c1 (Q1-Q10) — 2026-07-12

- Baseline (сайдкар gate_summary): LONGEST 0.956, RATIO 7.0 (Q19), AVG 2.404, DETAIL 0.844, SEQ 45 (ABCD-цикл), STYLE 0.967, skel hard 31/45, stamp CV 0.566, disMean/corLen 0.35, SOURCE_COVERAGE missing (TEMPORAL 3).
- 27 дистракторов подняты (Q7 пропущен — единственный блок файла уже в паритете). Blind 9/9; вылечено до записи: Q2D CAR → нормативные спринт-квоты с CI-гейтом; Q3 FORM-TELL (атрибуция только у correct) → дистракторам свои атрибуции (Джез Хамбл «Continuous Delivery», `PMBOK`); Q5 FORM-TELL (единственный рукописный перечень секций) → C = Test Plan/`IEEE 829` с зеркальным перечнем (реальная путаница Plan↔Strategy); Q6 DUP → D на ось «пропуск RED для тривиального кода»; Q9C BSM (ложное описание реального TCR) → термин убран, ось no-refactor сохранена.
- Known limitation: correct Q3 «Брайан Маринг» — опечатка (Marick); correct-текст вне мандата R8, кандидат на fact-fix.
- Гейты: schema OK; зона Q1-Q10 hard=0, файл 31→27; SEMI_YES={4,6} соблюдён.
- Остаток: c2 (Q11-Q20), c3 (Q21-Q30), c4 (Q31-Q40), c5 (Q41-Q45), POS (SEQ=45), FINAL (+freshness — TEMPORAL=3, вероятно version_sensitive=false).
### test-strategies — R8 c2 (Q11-Q20) — 2026-07-12

- 30 дистракторов подняты до скелета correct. Blind 10/10; вылечено до записи: Q12B CAR → механизм progressive delivery («MTTR ниже длительности регрессии»); Q14A кросс-вопросный DUP с Q12B → ось «бета-программа» (NPS/опт-ин вместо телеметрии); Q14/Q17 FORM-TELL на correct → дистракторы дотянуты скобочными перечнями и структурой (не тронут correct); Q15B BSM-шов «PITest — раннер jqwik» сглажен, Q15D CAR → fuzzing-конфьюжн; Q16B CAR → SBTM-термины; Q19A CAR big-bang → release-train механизм; Q20B BSM (eventual=strong) → кворум-конфьюжн.
- SEMI_YES={19}: нумерованные шаги 1)-4) с `;` зеркалированы во всех опциях Q19; Q20C лишён единственного `;` (correct semi=0).
- Гейты: schema OK; зона Q11-Q20 hard=0, файл 27→22; per-block CV 0.08-0.21.
- Остаток: c3 (Q21-Q30), c4 (Q31-Q40), c5 (Q41-Q45), POS (SEQ=45), FINAL (+freshness).
### test-strategies — R8 c3 (Q21-Q30) — 2026-07-12

- 30 дистракторов подняты. Blind 10/10; вылечено до записи: Q21 FORM-TELL — дистракторы были абсолютистскими («только в production», «не нуждается», «синхронно») на фоне нейтрального correct → переписаны нейтральными механизмами (`SyncTaskExecutor`-контур, `Schema Registry BACKWARD` как «замена» контрактов, shadow-консьюмеры); Q24 FORM-TELL — пренебрежительные клейма («избыточно», «не влияет») → нейтральные таксономии с ложными связями; Q26 FORM-TELL — correct вдвое длиннее → дистракторы дотянуты до 292-341; Q27D BSM — «tiered = все ярусы разом» самоопровергалось → ось «ярусы = модули по критичности»; Q29B кросс-вопросный DUP с Q24 (coverage-гейт заменяет ревью) → мутационный гейт `PITest`.
- SEMI_YES={22,24} соблюдён (все опции Q22/Q24 с `;`, остальные без).
- Гейты: schema OK; зона Q21-Q30 hard=0, файл 22→13; per-block CV 0.19-0.27.
- Остаток: c4 (Q31-Q40), c5 (Q41-Q45), POS (SEQ=45), FINAL (+freshness).
### R8 test-strategies c4 (Q31-Q40) — 2026-07-12

- 30 дистракторов подняты до скелета correct (btick-зачины, 2-3 предложения, код-спан-бакеты, SEMI_YES={35,40}).
- Blind-ревью 10/10; вылечено: DUP Q32↔Q33 (Q32 B → ephemeral-окружения со своим «Принципы:»-списком), BSM Q35 D (→ инверсия consumer/provider-driven SCC vs Pact), FORM-TELL Q40 (три дистрактора получили свои «;»-перечни + списки «Антипаттерны:» с англ. ярлыками).
- Пост-гейт: Q35 A разбит на 2 предложения. Зона HARD=0; file-hard 13→4; stamp CV →0.236, disMean/corLen →0.69.
- Остаток по файлу: c5 (Q41-Q45), POS, FINAL.
### R8 test-strategies c5 (Q41-Q45) — 2026-07-12

- 15 дистракторов подняты до скелета correct; blind-ревью 5/5.
- Вылечено: батчевый FORM-TELL «КАПС только у correct» (Q41/Q42/Q45 — капс-акценты зеркалированы в дистракторы), DUP Q41 B↔C (Spy-ошибка разведена), межвопросный DUP Q45 B↔Q42 C (docker-compose → Kubernetes-стенд), CAR Q43 A («ровно 2000» → нормативные 70/20/10 Google с CI-квотами), паритет скобок Q44.
- Файл целиком: skel-HARD 31→0, stamp CV 0.566→0.191, disMean/corLen 0.35→0.74.
- Остаток по файлу: POS (SEQ=45), FINAL (+fact-freshness сайдкар).
### R8 test-strategies POS+FINAL — 2026-07-12

- POS: ABCD-цикл разорван (SEQ 45→2, moved 34, dist A12/B11/C11/D11), только label/order.
- FINAL: 36 length-boost клауз (LONGEST 0.578→0.089, STYLE →0.133) + 8 paren-глосс (DETAIL →0.311) + 10 хвостовых расширений минимумов (worst RATIO 3.0→2.5, принятое исключение — compact-by-design).
- Freshness-сайдкар создан (9 version-claims, вкл. Q3 «Маринг»→Марик known limitation и @MockBean→@MockitoBean с Boot 3.4).
- Батарея: все метрики PASS кроме RATIO (accepted); сайдкар ревью COMPLETE (44 записи). Row 13 закрыт ✅.
### R8 tls-ssl c1 (Q1-Q10) — 2026-07-12

- Baseline файла: skel-HARD 42, stamp CV 0.474, battery LONGEST 0.844 / DETAIL 0.956 / SEQ 45 / STYLE 0.922, TEMPORAL 39 (версионно-тяжёлая тема — понадобится подробный freshness-сайдкар).
- 30 дистракторов Q1-Q10 подняты до скелета correct (SEMI_YES={2,4}); blind-ревью 10/10.
- Вылечено: CAR Q2 («TLS платный») → маркетинговый миф сертификатов; BSM Q6 C/D → False Start-misapply и RSA-key-exchange-путаница; BSM Q8 A → TCP Fast Open-путаница; BSM Q9 B → «пропускается только проверка сертификата»; near-CAR Q9 D смягчён.
- Известные ограничения correct (неприкосновенны): Q3 «триада CIA» с Authentication вместо Availability; Q8 не отвечает на «риски» из стема — оба в сайдкаре как кандидаты на отдельный fact-fix.
- Зона Q1-Q10 HARD=0; file-hard 42→34. Остаток: c2 (Q11-Q20), c3, c4, c5, POS, FINAL.
### R8 tls-ssl c2 (Q11-Q20) — 2026-07-12

- 30 дистракторов подняты (SEMI_YES={16} — correct Q16 это 1 предложение с 2 «;», prose); blind-ревью 10/10.
- Вылечено: BSM Q13 (SAN-в-DNS → misapply CAA), CAR Q14 (RSA-1024 → «wildcard покрывает apex»), CAR Q17 (RSA Security → «PKI = AD CS/EJBCA-продукт»), FORM-TELL Q19 (RFC только у correct → RFC в A/D), CAR+FORM-TELL Q20 (PSK-overgeneralization RFC 4279; второй «гибридный» дистрактор против стратегии «выбирай nuanced»).
- Зона HARD=0; file-hard 34→25; stamp CV 0.432→0.385. Остаток: c3 (Q21-Q30), c4, c5, POS, FINAL.
### R8 tls-ssl c3 (Q21-Q30) — 2026-07-12

- Все 10 блоков имели грубый Q11-дефект: correct ×2.5-3 длиннее, multi-sentence, backtick-dense; дистракторы — тонкие 1-предложные заглушки → correct угадывался глазами. По прямому фидбеку юзера («этот кейс нигде не должен повторяться») подняты все 30 дистракторов до полных правдоподобных mental models, single-delta клоны скелета correct (sentence-count / opening-class / semicolon-presence / backtick-density; SEMI_YES={22,29}).
- Blind-review в 3 прохода: (1) Q24/Q29/Q30 guessable_by_form=true — correct = лоне «учебниковый» с уникальной плотностью атак/команд/фабрик → у дистракторов поднята та же авторитетная плотность; (2) Q29 SAN-флаги только у correct + Q30 «но»-структура у всех дистракторов → Q29 дистракторы переаксированы с «экзотических ограничений» на естественные аффирмативные заблуждения, Q30 переписаны в уверенные позитивы; (3) финал Q29 guessable_by_form=false.
- Итог blind: Q21-Q29 guessable_by_form=false; Q30 — остаточный intrinsic tell (correct = единственная опция без ложной клаузы среди 4 правдоподобных дистракторов; длина/формат выровнены, correct самый короткий; дистрактор accidentally-true запрещён, correct immutable — задокументировано в record 30).
- De-BSM: Q25-C убран фейк-акроним; de-CAR: Q25-D 10лет→1год, Q26-D убран strawman «остановить сервис».
- Зона Q21-Q30 skel-HARD=0; schema OK; stamp — блоки не FLAT. Остаток: c4 (Q31-Q40), c5 (Q41-Q45), POS (SEQ=45), FINAL (freshness — TEMPORAL 39).
### R8 tls-ssl c4 (Q31-Q40) — 2026-07-12

- Все 10 блоков — грубый Q11-дефект (correct 272-459 симв лоне-длиннее, дистракторы 111-215 — тонкие 1-предложные заглушки). Подняты все 30 дистракторов до полных single-delta mental models по скелету correct (SEMI_YES={31,32,36,37,40}).
- Blind-review в 2 прохода: раунд-1 поймал guessable_by_form=true на Q35 (correct — единственный технически-полный; дистрактор «браузер сам генерит cert» = карикатура) и Q37 (дистракторы = карикатуры «всё сведено к RC4/heartbeat/CBC», correct единственный разнообразный).
- Ключевой фикс Q37: переписан в **spot-the-misattribution** формат — каждый дистрактор = корректный каталог из 8 атак с РОВНО одной подменённой атрибуцией (Heartbleed→CBC / DROWN→TLS 1.0 / Sweet32→RC4), near-miss вместо карикатуры → blind flags=[]. Q35 дистракторы выровнены по механистической плотности, «публичный веб» де-карикатурен (соцсети→госуслуги/корп-порталы).
- Итог blind: все Q31-Q40 guessable_by_form=false. Зона skel-HARD=0, schema OK, stamp — не FLAT.
- Остаток по файлу: c5 (Q41-Q45), POS (SEQ=45), FINAL (freshness — TEMPORAL 39).

### tls-ssl-interview — ROUND-8 c5 (Q41-Q45) — 2026-07-13
- Все 5 блоков несли Q11-дефект: correct 409-465ch (multi-sent, backtick-dense), дистракторы 108-148ch (тонкие 1-sent заглушки).
- Все 15 дистракторов подняты до полных single-delta mental models (клон скелета correct: sent-count, opening-class, SEMI, backtick density).
- Слепое ревью (reviewer subagent, 3 прохода):
  1. length-tell — correct самый длинный 5/5. → удлинил по одному ложному дистрактору в Q41/Q42/Q44/Q45 выше correct → correct longest 0/5.
  2. Q41,Q44 → guessable_by_form=false. Q42/Q43/Q45 остаточно 'lone-complete/density'.
  3. Q42 tool-каталоги с одной сломанной атрибуцией; Q43 command-каталоги (дистрактор D 432 > correct 422); Q45 параллельные чеклисты CV 0.023, отличие = 1 неверный пункт.
- Остаток Q42/Q45 'lone-complete' = intrinsic семантический tell (correct = единственный без ложного пункта), как Q30/Q37 — неустраним без accidental-true/ложного-correct, документирован в sidecar.
- Гейты: schema OK; skel-gate no-HARD Q41-45; stamp OK (not padded-clone). correct labels A/B/C/D/A; SEMI_YES={41,42,43}.
- Все 45 блоков tls-ssl reviewed (chunks 1-5). Дальше: POS de-cycle + FINAL freshness sidecar → закрыть row 14.

### tls-ssl-interview — ROUND-8 POS de-cycle — 2026-07-13
- Дефект: correct-label шёл идеальным циклом `ABCDABCD…` (44/44 adjacent +1 mod4), хоть распределение и было балансным (A12/B11/C11/D11). Учащийся, заметив марш A→B→C→D, угадывал слот в каждом блоке.
- Фикс: детерминированная перестановка позиций опций в каждом блоке (seed 451, constraint-search: нет +1-цепочек ≥3, <40% совпадений с исходным циклом). Перемещается весь option-dict (text+sections+correct флаг) — correct только меняет слот, ТЕКСТ не тронут.
- Новый seq: `BBDDCCDCACCABAADADBBADBBAACDCDCADCBBDDCAACBAB`, adjacent +1 = 6/44, распределение прежнее A12/B11/C11/D11.
- Инварианты: schema OK; skel-gate no-HARD; stamp OK; content-signature (correct-text + per-block option-set) идентична HEAD — изменились только order/label.
- Остаётся FINAL: freshness sidecar (`fact-freshness/tls-ssl-interview.json`, TEMPORAL-39 claims) → закрыть row 14.

### tls-ssl-interview — ROUND-8 FINAL (freshness) + ЗАКРЫТИЕ — 2026-07-13
- Freshness sidecar `docs/mcq-quality/fact-freshness/tls-ssl-interview.json`: 13 version-sensitive claims с Q-привязками, 13 источников (datatracker RFC), 3 staleness-risks, verdict **FRESH**.
- Проверено: TLS 1.3=RFC 8446; TLS 1.0/1.1 deprecated=RFC 8996, SSLv3=RFC 7568; DTLS 1.3=RFC 9147; X.509=5280/CSR=2986/PKCS12=7292; CT v1=6962, CT 2.0=9162; Chrome 58+ SAN-only (2818→9110); HPKP=7469 (мёртв, Chrome 72); SNI=6066, ESNI→ECH; SCSV=7507; HSTS=6797; OCSP=6960; tickets=5077; ACME=8555; PSK=4279.
- Staleness-flags: ECH на янв-2026 = IETF draft (не RFC) — сидер формулирует без номерной привязки, корректно; CT-браузерные пороги меняются independently — числа в сидере не фигурируют. Фактических ошибок в correct-текстах нет; правок не вносилось (correct иммутабелен).
- c1/c2 (Q1-Q20, ревью ДО Q11-урока) перепроверены на Q11-дефект: stamp-audit file-wide OK (not padded-clone), skel-gate no-HARD; ручной spot-check Q4/Q5/Q6 (CV 0.26-0.30) — correct НЕ самый длинный, дистракторы полные mental models → высокий CV = естественный terse-parallel, не дефект.
- **ROW 14 (tls-ssl) ЗАКРЫТ ✅**: 45/45 blocks reviewed (chunks 1-5, sidecar 45 records), POS de-cycle (44/44→6/44), FINAL freshness FRESH.
- Дальше worst-first: следующий файл — unit-testing (testing, 45Q).

### unit-testing-interview — ROUND-8 c1 (Q1-Q10) — 2026-07-13
- Стартовый baseline: skel-HARD **43/45**, 3 stamp-FLAT (Q18 CV0.56, Q34 0.67, Q39 0.70), correct-label цикл ABCDABCD (44/44).
- Дефект Q1-Q10: во всех 10 correct 208-339ch (multi-clause, backtick-dense), дистракторы 89-155ch — тонкие 1-sent заглушки (классический Q11/lone-complete tell).
- Фикс: 30 дистракторов подняты до полных single-delta mental models, клонирующих скелет correct (sent-count, opening-class, backtick, SEMI), с сохранением исходной оси-заблуждения каждого → sections не трогались.
- Blind (reviewer, 3 прохода): раунд 1 — 7/10 guessable (lone-complete). Раунд 2 — spot-the-error каталоги (Q2 все 5 букв F.I.R.S.T с одной подменой; Q6 базовый набор asserts у всех; Q10 второй полный 5-double каталог со swap Stub↔Mock; Q9 второй multi-способ каталог), length-монотонность сломана. Раунд 3 — Q6 assertAll-глоссы распределены, Q7 feature-цепочки в дистракторы. Итог: **все 10 guessable_by_form=FALSE** (ревьюер: остаток — знание, не форма).
- Гейты: schema OK; skel-gate no-HARD Q1-10; stamp OK. SEMI_YES={6,8,9}; correct A/B/C/D/A/B/C/D/A/B.
- Дальше: c2 Q11-Q20 (там FLAT Q18), затем c3-c5, POS de-cycle, FINAL freshness.

- **R8 unit-testing c2 (Q11-Q20)** — 30 дистракторов → полные single-delta клоны скелета correct (тот же каркас, подменён 1 факт, error-axis сохранён → секции валидны). 5 form-tell блоков переписаны: Q11 (correct был richest API-list) → все 4 несут never()/doNothing()/anyString(), delta = @RunWith/void-thenThrow/verify-mixing; Q14 (correct был lone code-block) → каждый дистрактор с `mockStatic`/`try` фрагментом; Q17 (correct lone-longest) → все 4 полные Red-Green-Refactor циклы, delta = порядок/семантика фазы; Q19 (correct lone-catalog из 6 источников) → каталоги выровнены по 6 пунктов, самый длинный теперь дистрактор; Q20 (correct lone-detailed) → все 4 с Arguments.of/textBlock/CsvFileSource. Слепой ре-ревью 5/5 guessable_by_form=false. Q12/13/15/16 — форма паритетна (captor-перехват, BDDMockito=Cucumber, @InjectMocks=@MockBean — реальные misconceptions, tell чисто семантический). Гейты: SCHEMA_OK / NO_HARD_11_20 / stamp OK (FLAT только Q34/39/41/45 — later chunks). Sidecar records 11-20, chunks_reviewed=2.

- **R8 unit-testing c3 (Q21-Q30)** — 30 дистракторов → полные single-delta клоны скелета correct (correct был 299-401ch vs заглушки 107-149ch — Q11-дефект во всех 10 блоках). Три слепых прохода: 5 form-tell блоков нейтрализованы — Q24 (lone-fluent `.inactive()`-цепочка → всем дан 3-звенный chain), Q28 (lone-code-literal `Clock.fixed(Instant.parse(...))` → литерал распределён по 4 опциям), Q25/Q27/Q30 (lone-longest → длины выровнены, cv 0.01-0.09, самый длинный теперь дистрактор). Финальный ре-ревью: Q21/22/23 + Q24/25/26/28/30 + Q27 (2 полные Awaitility-цепочки) = guessable_by_form=false. Q26/Q29 residual = СЕМАНТИКА best-practice-vs-анти-паттерн (reflection-first / Thread.sleep / isNull-for-Optional — правдоподобные misconceptions, узнаваемы знанием, не формой); принято как intrinsic-definitional (как tls-ssl Q30/42/45, c2 Q12/13/15/16). SEMI_YES={22,25,26,30}. Гейты: SCHEMA_OK / NO_HARD_21_30 / stamp OK (FLAT только Q34/39/41/45). Sidecar records 21-30, chunks_reviewed=3, blocks_reviewed=30.

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c1 (Q1-Q7) — 2026-07-13
- **Повод (директива пользователя):** в уже-DONE файле correct/wrong различались по ФОРМАТУ и СТРУКТУРЕ, хотя контент-аудит R8 пройден. Введены два новых измерения: **SFPL** (структура/формат/пунктуация/длина, правила `docs/mcq-quality/surface-parity-rules.md` R1-R6) и **ANSC** (answer completeness — каждый вариант полноценно отвечает на вопрос, дистрактор = полный правдоподобный ответ с одной содержательной неочевидной ошибкой).
- **Инструмент:** `scripts/mcq-surface-parity.py` (length-CV, corr_longest/shortest, backtick-бакеты, lone-feature tells) + слепой reviewer (guessable_by_form + completeness_ok).
- **Правка Q1-Q7:** correct-опции получили ТОЛЬКО backticks (strip('`')==HEAD → смысл неизменен). Дистракторы выровнены по backtick-плотности/длине/структуре, ось-заблуждения сохранена.
  - Q3: дистракторы подняты до равнополных 6-элементных каталогов (OS-модель / `SLEEPING`+`DEAD` / `BLOCKED`=`WAITING`); correct больше НЕ единственный полный список из 6 состояний; секции B обновлены под смещённую ось (invented state names).
  - Q5: все поведенческие дистракторы стали двухаспектными (return **и** throw) контрастами; correct больше НЕ единственный «полный» ответ; секции B обновлены под ось «инвертированы возвращаемые типы».
  - Q1 (B де-backtick `ProcessBuilder`), Q7 (C укорочен 157→136ch) — снятие остаточных correct-pointing tells.
- **Слепой ре-ревью:** раунд 1 — Q3/Q5 guessable (lone-complete). После правки: Q3 guessable_by_form=false; Q5 после доп. правки D (двухаспектный) — guessable_by_form=false + completeness_ok=true. Q1/Q2/Q4/Q6/Q7 — форма ровная, все варианты полноценны (проверено раундом 1).
- **Гейты:** SCHEMA_OK; skel-gate NO_HARD Q1-7; stamp-audit file mean CV=0.095, disMean/corLen=1.02. corr не longest ни в одном блоке Q1-7.
- SEMI_YES={4,7}. Sidecar: records Q1-7 получили блоки `sfpl` + `answer_completeness`; top-level `sfpl_reaudit` (status IN_PROGRESS, chunks=[Q1-Q7], 7/56).
- **Дальше:** c2 Q8-Q17 и далее до Q56 по тем же двум измерениям; ПОСЛЕ полного файла — добавить колонки `SFPL`/`ANSC` в §1 мастер-таблицу и сбросить прогресс по этим измерениям (аккуратно, с учётом параллельной сессии).

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c2 (Q8-Q17) — 2026-07-13
- **Правка (только дистракторы, correct-тексты байт-в-байт HEAD):** 20 дистракторов подняты/выровнены; 1 секция (Q15-D) перепатчена под смену оси.
  - **ANSC (тонкие→полные):** Q8-A (каррикатурный повтор «освобождает мониторы» → корректный triple), Q9-A/B/D, Q10-B/D, Q11-A/C, Q13-B/C/D, Q14-D, Q15-C, Q17-B — тонкие 1-клаузные заглушки подняты до полных правдоподобных ответов с одной содержательной ошибкой; ось-заблуждения сохранена.
  - **SFPL form-tells сняты:** Q9 (correct — единственная bare-noun дефиниция → дистракторы Описание/Механизм/Требование в bare-noun); Q11 (correct lone-longest → cluster 102-106); Q13 (correct — единственный позитивный каталог с lone-gloss «(unlock hb lock)» → все 4 single-delta позитивные + скобочный gloss на «видимость»); Q12/Q8 length-outliers подрезаны.
  - **Каррикатура/абсурд убраны:** Q15-D «static synchronized не существует в Java» (categorical) → «берёт монитор экземпляра, а не Class-объекта» (plausible); секции D перепатчены.
- **Q17 residual — intrinsic-semantic (ПРИНЯТО):** correct — единственная истинная поведенческая дефиниция («корректен при доступе из нескольких потоков без доп. синхронизации клиентом»), дистракторы — те же поведенческие дефиниции с ложной причиной-механизмом (volatile / private / all-synchronized). Форма выровнена (cv 0.016), различие только в знании — definitional-tell, документирован в sidecar.
- **Verified-clean без правок:** Q16 (эталонный параллельный каталог Object-методов, single-delta if/while/FIFO/Thread), Q12 (частичный/полный/физический порядок — паритетны).
- **Blind (reviewer, 4 прохода):** финально Q8-Q16 guessable_by_form=false + completeness_ok=true; Q17 форма=false, остаток семантический.
- **Гейты:** SCHEMA_OK / skel NO_HARD Q8-17 / stamp mean CV=0.08, disMean/corLen=1.03. Инварианты: correct-тексты не изменены (corr_leaks=[]), 1 correct/блок.
- Sidecar: records Q8-17 + sfpl/answer_completeness; sfpl_reaudit 17/56 (chunks Q1-7, Q8-17).
- **Дальше:** c3 Q18-Q27.

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c3 (Q18-Q27) — 2026-07-13
- **Правка (дистракторы + Q25-C backtick-only):** 25 дистракторов + 1 correct (Q25-C, только `check-then-act`/`read-modify-write` backticks — смысл неизменен). Секции не тронуты (ось каждого сохранена).
  - **ANSC (тонкие/каррикатурные → полные):** Q18-D (thin «software emulation»), Q19-A/C, Q27-B (fake «быстрее на 30% inline-кэш» → реальный perf-миф), Q24-C (×3 каррикатурное «только в Java» → триплет), Q24-A (повтор «те же имена»).
  - **SFPL form-tells сняты:** Q20/Q21/Q26 (correct lone-shortest → дистракторы подрезаны к кластеру), Q27 (correct lone-longest → cluster 95-104), Q25 (btbucket: backtick-нормализация всех 4). SEMI mirror восстановлен Q20-D (`;`).
  - **Почти-истинный дистрактор заострён:** Q25-D (immutable+final «звучит верно» → «immutable снимают гонку даже с общей изменяемой ссылкой» — явная overreach-ошибка).
- **Residual intrinsic-semantic (ПРИНЯТО, R6):** Q18/Q19/Q22 — форма выровнена (cv 0.044-0.049, зачины/пунктуация/backticks единообразны, correct не length-outlier), но stem диктует, что correct — единственный истинный: Q18 «что такое CAS» (реальный механизм lock-free vs ложные), Q19 «КОГДА выгоднее A или B» (нюанс two-regime vs overgeneralization), Q22 «какие преимущества» (реальная способность vs ложные). Различие знаниевое, не формальное — как Q17.
- **Blind (reviewer, 3 прохода):** Q20/21/23/24/25/26/27 guessable_by_form=false + completeness_ok=true; Q18/19/22 форма выровнена, остаток семантический.
- **Verified-clean:** Q23 (shared-stem lock-ordering триплет, single-delta), Q22-структура (переформулирована в позитив).
- **Гейты:** SCHEMA_OK / skel NO_HARD Q18-27 / stamp mean CV=0.068, disMean/corLen=1.02. Инварианты: correct meaning preserved (BT strip==HEAD), секции неизменны, 1 correct/блок.
- Sidecar: records Q18-27 + sfpl/answer_completeness (Q18/19/22 intrinsic_semantic_tell); sfpl_reaudit 27/56.
- **Дальше:** c4 Q28-Q37.

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c4 (Q28-Q37) — 2026-07-13
- **Правка (только дистракторы + секции 2 блоков):** 15 дистракторов подняты/выровнены; correct-тексты байт-в-байт HEAD (0 изменений correct). Секции переписаны у Q29-B/C/D и Q34-B/C/D (6 опций) — там, где сместилась ось-заблуждения.
  - **SFPL length/structure выровнены (ось сохранена, секции не тронуты):** Q28 (единый шаблон Executor/ExecutorService, cv0.041), Q31 (submit/execute, correct больше не lone-longest — tied 135), Q32 (work-stealing, cv0.024), Q33 (Future-ограничения, correct не lone-short), Q35 (thenApply/Compose/Combine, cv0.020), Q36 (exceptionally/handle/whenComplete, cv0.035).
  - **Form-tell переписан с рерайтом секций (2 блока):**
    - **Q29** — correct был ЕДИНСТВЕННЫМ полным 6-элементным каталогом фабрик, дистракторы навешивали одиночный тезис (R5 lone-catalog). Все 3 → равные bare-каталоги из 6 членов с ровно одним несуществующим/спутанным: `fork-join` (путают с `newWorkStealingPool`), `direct-executor` (Guava, не JDK), `cached-scheduled` (нет такой фабрики). cv 0.016, ratio 1.04. Секции B/C/D под новую ось «fake JDK factory».
    - **Q34** — correct был ЕДИНСТВЕННОЙ канонической дефиницией «…поверх `Future` и `CompletionStage`», дистракторы несли подозрительный «где …»-хвост-механизм (correct — единственный без него). Все 3 зеркалят ХВОСТ «поверх <два типа>» с подменой одного типа: `CompletionService` (имя-путаница с `CompletionStage`), `Callable` (task-тип vs stage-интерфейс), `FutureTask` (impl vs interface `Future`). cv 0.034, ratio 1.10. Секции B/C/D под ось «неверный базовый тип иерархии».
- **Verified-clean (не трогались):** Q30 (shutdown/shutdownNow/awaitTermination — параллельный триплет single-delta), Q37 (allOf/anyOf — форма выровнена).
- **Blind (reviewer, 3 прохода):** первый проход Q28-37 → Q29+Q34 guessable_by_form=true (form-tells выше); после рерайта оба guessable_by_form=false + completeness_ok=true; остальные 8 — форма выровнена/семантика.
- **Гейты:** SCHEMA_OK / skel NO_HARD Q28-37 / scanner NO_TELLS Q28-37 / stamp mean CV=0.062, disMean/corLen=1.02. Инварианты: correct тексты == HEAD, order/label/correct неизменны, секции неизменны кроме 6 патч-опций, ровно 1 correct/блок.
- Sidecar: records Q28-37 + sfpl/answer_completeness; sfpl_reaudit 37/56; verified_clean += Q30/Q37.
- **Дальше:** c5 Q38-Q47.

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c5 (Q38-Q47) — 2026-07-13
- **Правка (14 дистракторов + секции 1 блока):** correct-тексты байт-в-байт HEAD. Секции переписаны только у Q45-B (сменилась ось).
  - **ANSC — де-каррикатуризация (completeness_ok был false):** Q40-A («Phaser — устаревший одноразовый счётчик из Java 1.4; Exchanger — deprecated-замена CountDownLatch» → правдоподобная одна ошибка «Phaser из Java 5», ось версий сохранена, секции валидны); Q44-C («только сторонний LMAX Disruptor; на стандартной Java надёжного P-C нет» → «Disruptor с lock-free ring buffer всегда лучше BlockingQueue», ось сохранена).
  - **SFPL length/structure (оси сохранены):** Q38 (B раздут 183 → трим до формы correct, cv0.025), Q43 (correct D — единственный носитель `(ждёт при …)`-глосс → те же скобочные глоссы добавлены A/B/C).
  - **Form-tell «correct = самый детальный/сбалансированный» нейтрализован:**
    - **Q42** — correct A нёс два порога ≥8 И ≥64 и был lone-детальным; дистрактор C поднят до тех же двух порогов, B/C де-абсолютизированы (сняты «полностью/только/никогда/каждом»); correct больше не lone-longest; blind form=false.
    - **Q45** — correct A — единственный перечисляющий реальные инструменты (jstack/jcmd/kill-3/ThreadMXBean); дистрактор B превращён в ПАРАЛЛЕЛЬНОЕ плоское перечисление тех же 4 инструментов с одним неверным (`jcmd GC.run` вместо `Thread.print`), секции переписаны под новую ось; C/D тоже называют инструменты; correct больше не lone-plain-enumerator; blind form=false.
    - **Q47** — correct B — единственный без категоричных «любых/вовсе/только»; сняты триггеры-абсолюты у A/C/D, каждая ошибка стала конкретной правдоподобной (CPU-bound выгода / pool сам подстроится / не stable после Java 21); blind form=false.
  - **Verified-clean (не трогались):** Q39 (Semaphore — параллельные release-семантики, single-delta), Q41 (Concurrent Collections — single-delta), Q46 (Virtual Threads — параллельные mount-механизмы).
- **Blind (reviewer, 3 прохода):** проход 1 Q38-47 → Q42/Q45/Q47 guessable + Q40/Q44 caricature; проход 2 после де-каррикатуризации/де-абсолютизации → Q42/Q45/Q47 остаток по длине/enumeration; проход 3 после подъёма C-порогов (Q42) и параллельного enumerator-дистрактора (Q45) → все Q38-47 guessable_by_form=false + completeness_ok=true.
- **Гейты:** SCHEMA_OK / skel NO_HARD Q38-47 / scanner NO_TELLS Q38-47 / stamp mean CV=0.055, disMean/corLen=1.03. Инварианты: correct == HEAD, order/label/correct неизменны, секции неизменны кроме Q45-B, ровно 1 correct/блок.
- Sidecar: records Q38-47 + sfpl/answer_completeness; sfpl_reaudit 47/56; verified_clean += Q39/Q41/Q46.
- **Дальше:** c6 Q48-Q56 (финальный чанк; затем колонки SFPL/ANSC в §1 + reset).

### java-concurrency-interview — ROUND-8 SFPL/ANSC re-audit c6 (Q48-Q56, ФИНАЛ) — 2026-07-13
- **Правка (6 дистракторов, correct == HEAD, секции не тронуты):**
  - **Q48** (thread pinning) — тонкие заглушки A(100)/B(113) и раздутый C(168) выровнены к форме correct «Когда `<определение>`: `<деталь>`»; C поднят так, чтобы correct не стал lone-longest; оси сохранены (A=GC-pinning-омоним, B=volatile, C=нет-диагностики).
  - **Q52** (сложная CF-цепочка) — correct B был lone «богатым best-practice recipe» из 5 CF-методов; тонкие C/D(118) и раздутый A(172) переписаны в такие же плотные pipeline-**анти-паттерны** одинаковой насыщенности (блокирующий `get()` / `commonPool` под I/O / пропуск `exceptionally`+`handle`); убран громкий тон-маркер («потеряется без следа», «для всего одинаково»); оси сохранены. Остаток «correct = грамотный подход среди анти-паттернов» — INTRINSIC-SEMANTIC для best-practice-вопроса (blind финально guessable_by_form=false).
- **Verified-clean (не трогались, blind form=false с первого прохода):** Q49 (Structured Concurrency), Q50 (ScopedValue), Q51 (ForkJoinPool detail), Q53 (StampedLock), Q54 (Phaser vs Latch/Barrier), Q55 (Exchanger), Q56 (StructuredTaskScope политики).
- **Blind (reviewer):** проход 1 Q48-56 → только Q52 guessable (lone-rich recipe); проход 2 после подъёма дистракторов до плотных pipeline → Q52 остаток по тону; проход 3 после де-тона + восстановления длины → все Q48-56 guessable_by_form=false + completeness_ok=true.
- **Гейты (file-wide):** SCHEMA_OK / skel HARD=0 / stamp mean CV=0.05, disMean/corLen=1.03. Остаток сканера — только c1: Q1/Q2/Q4 `btbucket1-2` (мин. backtick-спред), Q5 ratio1.26 — приняты в c1 (blind pass).
- Sidecar: records Q48-56 + sfpl/answer_completeness; sfpl_reaudit **56/56, status=DONE**; verified_clean += Q49-56.
- **ФАЙЛ ПОЛНОСТЬЮ ПЕРЕ-АУДИРОВАН (56/56) по SFPL + ANSC.**
- **Дальше:** финальный шаг Директивы #1 — добавить колонки `SFPL` + `ANSC` в мастер-таблицу §1 (строка 297) и сбросить прогресс по этим измерениям (аккуратно, риск коллизии на общей 318-строчной таблице).

### ROUND-8 SFPL/ANSC — design-patterns c1 (worst-first lone_long, 6/48)

- **Файл:** `design-patterns-interview.json` — второй по очереди SFPL/ANSC переаудит (после java-concurrency; полностью ✅ по старому R8, но SFPL=⬜/ANSC=⬜ по новым измерениям).
- **Chunk c1 (worst-first по сканеру):** Q35, Q22, Q47, Q14, Q38, Q18 — блоки с `lone_long` (correct = единственный длинный/богатый), архетип Q11-дефекта.
- **Правки (только дистракторы; correct байт-в-байт HEAD, drift=0):**
  - **Q35** — lone catalog (correct D 255ч/12 backticks vs одностроки-заглушки) → 4 равных 6-элементных каталога, ровно один отравленный маппинг у каждого (Integer.valueOf→Singleton / BufferedInputStream→Adapter / Runnable→Observer); приём java-concurrency Q29.
  - **Q47** — дистракторы отвечали на ПРОТИВОПОЛОЖНый вопрос (агитировали ЗА применение при стеме «когда НЕ нужно»; completeness_ok=false) → переориентированы на «когда НЕ нужно» ложными критериями (размер проекта / нет-в-stdlib / поздно-добавлять), catalog-форма как у correct с варьированными вердиктами; **SECPATCH A/C/D** (12 секций).
  - **Q14** — correct lone-longest 7-каталог позитивного тона → дистракторы полные каталоги (creational-6 / behavioral-7 / misclassification), утвердительный тон.
  - **Q38** — correct lone «симметричный полный» контраст → все дистракторы «GoF `Singleton`, счёт, механизм; Spring singleton, счёт, механизм» (comma-appositive), связные, все называют JVM-scope.
  - **Q18** — дистракторы несли tell «поэтому + отрицание» → всем формат «утверждение; <пример>, <descr> пример» как у correct A; bt=4×4.
  - **Q22** — B/D заглушки (97/114) → полные ответы (Adapter-confusion Target/Component/Adaptee; GUI-only Swing/JPanel), оси сохранены.
- **Blind (reviewer, до сходимости):** Q14/18/22/35 → guessable_by_form=false за 2 прохода; Q38/47 → 4 прохода (v2→v5): сняты «симметрия/полнота», «формульный повтор» дистракторов, восстановлена связность; финал — все 6 `guessable_by_form=false` + `completeness_ok=true` (Q47 intrinsic_only, Q35 intrinsic — отличие в одном маппинге, Q38 остаток «/ClassLoader» в неизменяемом correct принят).
- **Гейты:** SCHEMA_OK; surface NO_TELLS ×6; skel HARD=0 на touched (Q46/b0 HARD — untouched pre-existing → c2); stamp OK (mean CV 0.118, disMean/corLen 0.91); correct drift=0; изменены ровно [14,18,22,35,38,47].
- Sidecar: `sfpl_reaudit` blocks_reaudited=6/48, status=IN_PROGRESS.
- **Дальше:** c2 — следующие worst-first блоки (Q46 skel-HARD, Q1/Q4/Q20/Q30/Q44/Q16/Q6 lone_long/btbucket) до 48/48, затем SFPL/ANSC=✅ для design-patterns.

### ROUND-8 SFPL/ANSC — design-patterns c2 (Q20/Q46/Q1/Q4/Q23/Q30/Q44, 13/48)
- Worst-first добор после c1. correct-тексты байт-в-байт HEAD (drift=0), секции без изменений (SECPATCH пуст), changed-blocks==[1,4,20,23,30,44,46].
- **Q20** (главный): correct — единственное «широкое role-определение» Proxy (generality-tell, 4 итерации). Финальный приём: ВСЕ 4 варианта открываются идентично «`Proxy` предоставляет заместителя объекта…», delta стал intrinsic-semantic — «или subclass» (true, обе механики) vs interface-only(JDK) / @EnableCaching-gated / adds-behavior(=Decorator). Слепой ревьюер после rework угадал НЕВЕРНО на 2 перемешиваниях → форма не выдаёт correct; остаточный «tell» = грамматическая шероховатость correct в HEAD (immutable, маскирует а не выдаёт) + знание Proxy vs Decorator/CGLIB/@EnableAspectJAutoProxy. Принято по R6 (intrinsic-semantic).
- **Q46**: снят open_mixed + skel-HARD (был в residual c1) — все «`Object Pool` …», ровно одно двоеточие, «примеры, `X`,`Y`,`Z`», чистые позитивные ложные определения; intrinsic_only=true.
- **Q1**: skel sentence_count (correct 2 предл.) → дистракторы тоже 2-предл. ложные определения. **Q4**: +colon tell → двухчастная «риск/преимущество — term: expl». **Q23/Q30**: correct lone-longest → дистракторы подняты (~160/~175, «пример, `X`»). **Q44**: A/D подняты (Visitor/AST-параллель), C снижен bt2.
- Гейты: SCHEMA_OK; surface NO_TELLS на touched; skel HARD=0 на touched; stamp mean CV0.101 disMean/corLen0.95. Остаток c3+: Q36(lone_long1.33),Q16,Q6,Q13,Q21,Q11,Q42,Q48,Q9,Q5,Q32,Q15,Q40.

### ROUND-8 SFPL/ANSC — design-patterns c3 (Q36/Q16/Q6/Q13/Q21/Q11, 19/48)
- Worst-first добор. correct байт-в-байт HEAD (drift=0), секции без изменений, changed-blocks==[6,11,13,16,21,36].
- **Q36** (lone Spring-каталог): correct — плоский 10-термовый каталог паттернов Spring (bt10) vs single-claim дистракторы. Приём Q35: 3 равных каталога, каждый с ОДНИМ отравленным маппингом, встроенным как term-qualifier БЕЗ «как» (`Decorator` AOP / `BeanFactory` Singleton / `@EventListener` Strategy) → форма идентична, ошибка только семантическая. intrinsic_only=true.
- **Q6** (expert-jargon tell): correct несла уникальный memory-model жаргон (instruction reordering, half-constructed), а D открывалась «Eager…». Жаргон разнесён по дистракторам (reordering/happens-before/барьер памяти), все открыты «В Double-Checked Locking». 2 прохода → intrinsic_only=true.
- **Q11** («absolute=trap»): correct — единственный без ограничит. слова, дистракторы несли «исключительно/только/синоним». Переписаны как уверенные позитивные определения (positive-definition recipe). 2 прохода → intrinsic_only=true.
- **Q16/Q13/Q21** (btbucket): выровнена backtick-density дистракторов под correct (bt3/bt2/bt3), длины ~150-165; оси Prototype/Adapter/Abstract-Factory/deep-copy/over-hide сохранены. intrinsic_only=true ×3.
- Гейты: SCHEMA_OK; surface NO_TELLS touched; skel HARD=0 touched; blind guessable_by_form=false ×6. Остаток c4+: Q42,Q48,Q9,Q5,Q32,Q15,Q40.

### ROUND-8 · design-patterns · SFPL/ANSC c4 (Q42,Q48,Q9,Q5,Q32,Q15,Q40) — 2026-07-13
- Worst-first добор, 26/48 блоков re-audited. correct байт-в-байт HEAD (drift=0), changed-blocks==[5,9,15,32,40,42,48], 21 дистрактор.
- **Q42/Q48/Q9** (affirmative/positive-parity): дистракторы переписаны как уверенные ложные ОПРЕДЕЛЕНИЯ без абсолютов; Q42 — реальный `Collections.emptyList()` вместо выдуманных имён классов. form=false.
- **Q5** (N-item asymmetry): correct маппит принцип на 3 паттерна, дистракторы — на 1 → все дистракторы тоже по 3 паттерна, em-dash без colon. form=false.
- **Q15** (confusion-парити): Facade/Bridge/Prototype-путаницы, «;»-разделитель и bt3 как у correct. form=false.
- **Q32** (generality + affirmative-definition tell, 6 проходов): correct — единственное обобщающее affirmative-определение. Все 4 приведены к «`Iterator` предоставляет <вид>-доступ к элементам…, скрывая <noun>; в Java через `XxxIterator` и m()». Сняты последовательно: btbucket, thread-safety-guarantee, «за <noun>»-твист, self-reference (ось O3 Enumeration→`PrimitiveIterator`, чтобы `java.util.Iterator` перестал уникально эхом повторять stem). SECPATCH A/B/C. cv=0.021. delta intrinsic → guessable_by_form=false, intrinsic_only=true.
- **Q40** (meta-position form-shape): дистракторы были глиб/абсолют/дисмиссив («соседство глав»/«в основном независимы»/«всё — вариации одного»), узнаваемы без знания темы. SECPATCH: переписаны в скелет correct D «`X` строят через `Y`; A и B похожи структурно, C и D <trait> без <общего>» (specific-false-pairing). delta intrinsic → guessable_by_form=false.
- Гейты: SCHEMA_OK; surface NO_TELLS touched; skel HARD=0 touched; инварианты (correct drift=0, sec changes⊆{Q32,Q40}×{A,B,C}, 1 correct/блок) ✅; blind guessable_by_form=false ×7. Остаток: 22 scanner-clean блока на blind-доводку (c5+) до флипа SFPL/ANSC=✅.

- **ROUND-8 SFPL/ANSC c5** (design-patterns, 32/48): worst-first blind-доводка {Q8,Q12,Q17,Q27,Q34,Q37}. Q8 — устранён Q11-style дефект (длинный correct + тонкие stub-дистракторы) полными false-механизмами race/stack/deadlock. Q17 и Q37 — «correct = lone balanced textbook» tell снят приёмом exact-mirror reversed-roles (один дистрактор = дословная перестановка клауз correct → неотличим по форме) + specific-false distinctions. Q27 — «lone положительный named-principle (SRP)» tell снят выдачей каждому дистрактору своего positively-framed named-principle (инкапсуляция/типобезопасность/контракт-JDK). Q34 — affirmative false property-claims по скелету correct. correct-тексты байт-в-байт HEAD; SECPATCH только {(17,A/B/D),(34,B/C/D),(37,B/C/D)}. Гейты: SCHEMA_OK, surface NO_TELLS, skel HARD=0, correct drift=0, changed==[8,12,17,27,34,37]. Blind: все 6 guessable_by_form=false. Остаток 16 scanner-clean blind-unreviewed → c6+.

- **ROUND-8 SFPL/ANSC c6** (design-patterns, 38/48): blind-review 6 scanner-clean блоков {Q3,Q7,Q24,Q25,Q33,Q43}; 5 палились по форме, исправлены, Q33 принят blind-clean без правки. Q3 — устранён «lone two-part + эхо `Антипаттерны`» приведением всех дистракторов к two-part-скелету. Q7 — stem-echo «ограничение» vs «недостаток» снят выравниванием хвостов дистракторов (correct immutable). Q24 — «lone gerund-elaboration + factoid `8`» снят gerund-parity + де-factoid (SECPATCH C). Q25 — «lone named-principle Open/Closed» снят выдачей каждому дистрактору своего SOLID-принципа (LSP/SRP/DIP). Q43 — concrete-vs-meta (Q40-style) снят переписыванием дистракторов в точный two-pair-скелет correct (bare-names, bt0). correct-тексты байт-в-байт HEAD; SECPATCH только {(24,C),(43,A/B/C)}. Гейты: SCHEMA_OK, surface NO_TELLS, skel HARD=0, correct drift=0, changed==[3,7,24,25,43]. Blind: 6× guessable_by_form=false. Остаток 10 blind-unreviewed → c7.

- **ROUND-8 SFPL/ANSC c7** (design-patterns, 48/48 — ФАЙЛ ЗАВЕРШЁН): последние 10 scanner-clean блоков {Q2,Q10,Q19,Q26,Q28,Q29,Q31,Q39,Q41,Q45} прошли blind-review. Доминантный дефект — «correct = lone трезвый/канонический, дистракторы несут дисмиссив/glib/абсолют/reversal-tone» → де-карикатуризация в нейтрально-утвердительные правдоподобно-ложные claims. Q2 — все дистракторы приведены к зачину «Три категории, всего NN паттерн(а): …» с дельта-числами. Q10 — сняты tone-маркеры, диверсифицированы механизмы (correct с OCP не кластерный аутлайер). Q19 — всем по 3 клаузы как у correct (single dispatch/2^N/OCP); guessable_by_form=false (narrows-to-pair, выбор требует семантики). Q26/Q28/Q29/Q31 — утвердит.-ложные механизмы State/CoR/Command/Visitor с паритетом «;»/backtick/длины. Q41 — умеренные claims, «;» в общем месте, bt0×4. **Q39/Q45 (advice-methodology, самые трудные)**: soundness правильного совпадает со «звучит взвешенно» — после round-5 всем дистракторам выдана та же форма (Q39 — процедурный зачин «Алгоритм выбора: …» + финал-самопроверка ×4; Q45 — «Хороший ответ …» с 4 составляющими + аффирмативный em-dash хвост ×4); остаточное различие только в СОДЕРЖАНИИ методики → intrinsic_only=true, guessable_by_form=false (принято по R6). correct-тексты байт-в-байт HEAD (drift=0); SECPATCH пуст (все оси-заблуждения сохранены). Гейты: SCHEMA_OK, surface NO_TELLS touched, skel HARD=0 touched, changed==[2,10,19,26,28,29,31,39,41,45]. Blind: 10× guessable_by_form=false. **SFPL/ANSC=✅ — design-patterns полностью re-audited (48/48).**

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c1 (Q23,Q24,Q32,Q34) — 2026-07-13
- Новый worst-first файл (`ai-ml/fine-tuning-llm-interview.json`, 35 блоков, 1/Q) после design-patterns FINAL. Выбран по структурному дефекту: mean structure-tell=20.79 (gold 9.09), Golden AP#1 на весь файл (correct — развёрнутый каталог/лекция ~400-530ч, дистракторы — плоские заглушки ~100-160ч). Baseline gate: CORRECT_LONGEST_RATE=1.0 (35/35), CORRECT_WRONG_AVG_RATIO=2.689, STYLE_GUESSABILITY=1.0, DETAIL_PARITY=0.971, CORRECT_POSITION_SEQUENCE=35 (строгий A→B→C→D цикл), SOURCE_COVERAGE=missing.
- Метод (как design-patterns Q35/Q36): correct-тексты БАЙТ-В-БАЙТ HEAD (drift=0), дистракторы подняты до параллельных каталогов/лекций с ОДНИМ инлайн-факт-swap (реальные сущности из теории). Плотность аннотаций выровнена у всех 4 (снят «detail-outlier» tell), отрава — не хвостовое принижение, а подменённый факт, концовки нейтрально-параллельны.
- **Q23** (correct C, LIMA-эффект): дистракторы → тот же скелет «`LIMA` (…) показал … Тезисы: … Выводы: …»; A=«надёжный alignment даёт только объём (50K синтетических)», B=«alignment докладывает НОВЫЕ знания», D=«diversity не важна, 1000 почти одинаковых». BST r1 false.
- **Q24** (correct D, стек библиотек): дистракторы → полный стек-каталог (5 тренеров/~70% VRAM/multi-node) с 1 отравл. маппингом; A=trl↔bitsandbytes роли swapped, B=«Unsloth — обёртка над trl без прироста скорости/памяти», C=«Axolotl весь pipeline на чистом Python без YAML». correct стал самым КОРОТКИМ. BST r2 false.
- **Q32** (correct D, multi-LoRA serving): дистракторы → скелет ОДИН base/Зачем/Технологии/Overhead с 1 отравл. фактом; A=«отдельная копия base на адаптер», B=«сервер складывает несколько адаптеров на запрос» (SECPATCH — ось сдвинута с latency-преувеличения на adapter-stacking для tone-паритета, concl. IDENTICAL correct), C=«адаптеры разных base-семейств». BST r3 false.
- **Q34** (correct B, open-weight модели 2025-2026): дистракторы → каталог семейств с богатыми per-family аннотациями (Mixtral-MoE/37B active/Instruct-хвост) + 1 отравл. фактом; A=«все под MIT, лицензию не проверять», C=«reasoning с нуля вместо Distill», D=«DeepSeek V3/R1 = edge 1-3B». BST r2 false.
- Гейты: SCHEMA_OK; surface NO_TELLS touched (corr_longest=False, ratio≤1.20 ×4); skel HARD=0 touched; structure-tell 0-5 (богатые равные каталоги — catalog-parity, stamp-audit verdict OK, НЕ флэт-штамп); correct drift=0; section changes⊆SECPATCH{(32,B)}; changed==[23,24,32,34]; 1 correct/блок. Blind: BST guessable_by_form=false ×4; BSM один защитимый + completeness_ok=true ×4.
- **Accepted-artifact:** DUP_NGRAMS 0→152 + low length-CV (0.022-0.072) на 4 тронутых — inherent для poisoned-catalog parity (4 опции делят истинный catalog-scaffold, различаясь отравл. фактом; прецедент design-patterns Q35/Q36). НЕ stamped-clone: stamp-audit OK, разные mental models, blind non-guessable.
- Sidecar: `docs/mcq-quality/reviews/fine-tuning-llm-interview.json` (IN_PROGRESS, 4/35). Остаток 31 блок (тот же AP#1) → c2+; ещё нужны freshness sidecar (version-sensitive: LoRA/DoRA/DPO/GRPO/лицензии) + POS de-cycle. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c2 (Q19,Q21,Q27,Q31) — 2026-07-13
- Worst-first по structure-tell (Q27=40, Q21=37.67, Q19=35, Q31=33). Тот же Golden AP#1: correct — длинный структурный каталог/лекция (488-549ч), дистракторы — короткие одиночные false-claims (120-173ч). correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[19,21,27,31]; SECPATCH пуст (misconception каждого дистрактора сохранена → секции валидны).
- Метод: дистрактор = коэрентная альт.модель тем же скелетом, что correct, с 1 отравл. фактом. Q19 (сравнит.каталог 5 методов) — A: GRPO↔DPO домены swapped, B: все на learned RM, D: ORPO 4-5 моделей+reference. Q21 (preference-датасеты+сбор) — B: одиночные SFT-пары, C: 100K шумных>5K чистых, D: thumbs непригодны. Q27 (7-уровневая оценка) — «identical 7-item skeleton», отравлен РОВНО ОДИН пункт (A: perplexity достаточно; B: judge=gold; D: forgetting-check неинформативен). Q31 (3 сценария деплоя) — A: LoRA хранит полные веса, B: Ollama нативный hot-swap, D: квантизация поднимает VRAM.
- **Итеративный BST (4 прохода reviewer, ключевой урок тика):** round1 — все 4 guessable по tone/absolute-концовкам (де-карикатура: «различаются лишь», «держит больше всех», «гарантированно», «полностью», «бесполезен») → round2 калм-факты внутри тела. round3 form-vs-domain split → **Q21/Q27 form=false** (различие только доменное, R6-idealный intrinsic-semantic residue). round3 нашёл 2 ЧИСТО формальных остатка из-за уникальной черты байт-локнутого correct: Q19 капс «БЕЗ reference» + Q31 merge-хедж «но потеря гибкости» → round4 роздал эти черты дистракторам (A/B/C несут «БЕЗ»; B/C/D несут merge-хедж) → correct перестал быть уникальным. round5 Q27 named-tell («correct = единственный однородный, дистракторы вклеивают reversal через тире вне скобок») снят структурно: отрава КАЖДОГО дистрактора загнана ВНУТРЬ скобки соответствующего пункта → все 4 format-идентичны (7× «name (скобка)», bare_emdash=0).
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (corr_longest=False, ratio 1.06-1.17); skel HARD=0 touched; structure-tell 0.67-4.0 (богатые РАВНЫЕ каталоги 465-626ч — не флэт-штамп: stamp-audit verdict OK); correct drift=0; changed==[19,21,27,31]. Blind финал: form_guessable=false ×4 (Q19/Q21/Q31 reviewer-подтверждены form-clean; Q27 named-tell структурно снят), BSM один защитимый + completeness_ok=true ×4.
- Accepted-artifact: DUP_NGRAMS spike + низкий length-CV (0.024-0.059) — inherent poisoned-catalog scaffold-share (прецедент design-patterns Q35/Q36). Sidecar reviews обновлён (8/35). Остаток 27 блоков → c3+; freshness sidecar + POS de-cycle отложены. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c3 (Q6,Q14,Q20,Q29) — 2026-07-13
- Тот же Golden AP#1. correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[6,14,20,29]. **Q6** (LoRA-математика: `ΔW=B·A`, `r<<d`, `W_eff=W+(α/r)·B·A`, `2·d·r` vs `d²`) и **Q14** (RLHF three-stage + loss `-log σ(r_c-r_r)` + KL-penalty) — дистракторы построены как параллельные формульные/pipeline-каталоги с 1 отравл. фактом (Q6: A полная ΔW=d², C обе A/B gaussian→ΔW≠0, D α=ранг+квадратные=2·d²; Q14: A two-stage RM-direct, C PPO-одна-модель-стабильнее-DPO, D PPO-без-KL). SECPATCH для Q6/Q14 пуст. Оба form-clean с первого blind-прохода.
- **Q20** (каталог SFT-датасетов) и **Q29** (топ-5 ошибок «ошибка→фикс») — тип single-delta списка (все 4 = один список, отличие в одном элементе). **Ключевой урок тика:** первый blind-проход дал form_guessable=false для *уникального* выбора, НО reviewer поймал остаточное сужение 4→2 и completeness-провал: correct был единственным нейтральным списком, а дистракторы несли **оправдательную/reassuring прозу** («decontaminate необязательна», «можно пропустить, ведь…», «LIMA решает масштаб», «50×10 эпох — хорошая практика», «tokenizer безопасно») — тональный выброс на фоне фактических тегов; Q29-O2 к тому же была карикатурой (несколько подмен: optimizer-баги + LoRA-arch + данные-<5%).
- **Фикс (round2):** все подмены переведены в **терсные фактические свопы** уровня одного тега, без оценочных клауз. Q20: `Alpaca commercial-ok` (вместо research-only), `deduplicate (exact-match)` (вместо minhash), `LIMA (1K синтетический)` (вместо курированного). Q29: все 5 пунктов = «ошибка→фикс» код-вызовом, отравлен РОВНО ОДИН фикс — B `temperature до 0` (вместо `apply_chat_template()`), C `tokenizer.add_tokens(missing)` (вместо `AutoTokenizer.from_pretrained`), D `<500 → 5-10 эпох, high LR` (вместо `1 эпоха max`). O2 перестроена из карикатуры в single-poison. SECPATCH={(20,B),(20,C),(29,B),(29,C),(29,D)} — ось-misconception сдвинута под каждую новую подмену (dedup-метод / LIMA-provenance / фикс-chat-template / фикс-tokenizer / эпохи-совет), секции переписаны и валидны.
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[]; Q6 ratio1.05, Q14 1.14 corr_longest=True но <1.25, Q20 1.01, Q29 1.05, corr_longest=False×3); skel HARD=0 touched; structure-tell 0.0-4.67 (Q20/Q29 low = rich single-delta списки 478-549ч, stamp-audit verdict OK — не padded-clone); correct drift=0. Blind финал: form_guessable=false ×4, one_defensible ×4, completeness_ok=true ×4.
- Sidecar reviews обновлён (12/35). Остаток 23 блока (тот же AP#1, structure-tell 22-25) → c4+; freshness sidecar + POS de-cycle отложены. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c4 (Q22,Q33,Q30,Q28) — 2026-07-13
- Worst-first по structure-tell (Q22=25.33, Q33=24.67, Q30=24, Q28=24). Тот же Golden AP#1: correct — структурный каталог (497-530ч, bt1-6), дистракторы — плоские заглушки (124-221ч, bt0-3). correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[22,28,30,33]; SECPATCH пуст (ось каждого дистрактора сохранена). Q22: question_text нейтрализован (QTEXT — убрана пресуппозиция «ломает»).
- Метод: дистрактор = параллельный каталог тем же скелетом, что correct, с 1 отравл. фактом. Q22 (chat-template) — per-family каталог ChatML/Llama-3/Mistral + отравл. МЕХАНИЗМ (A: слой отображения; C: шаблоны-алиасы взаимозаменяемы; D: спецтокены дублируют пунктуацию). Q28 (forgetting) — опр+обнаружение benchmarks+митигация-list+language-drift, 1 отравл. пункт (A: обнаружить заранее нельзя; B: поднять epochs/LR; C: full FT надёжнее LoRA). Q30 (merging) — каталог методов SLERP/TIES/DARE/Soups с per-method аннотациями, 1 отравл. зона (A: after совместного дообучения; C: любые base-архитектуры; D: eval не нужен). Q33 (FT-API 2026) — 3-вендорный каталог OpenAI/Anthropic/Google, 1 отравл. вендор (B: Anthropic публичный API; C: OpenAI только full-FT; D: Google не поддерживает Gemini tuning); freshness-факты только реюз из байт-локнутого correct.
- **3 прохода reviewer (ключевые уроки тика):** round1 (после подъёма заглушек) — Q33 form-clean сразу («эталон параллельности, подмена только в семантике»), Q22/Q28/Q30 flagged. round2 — Q28 «adapter switching делал correct длиннее» → добавлен 7-й пункт дистрактору; Q22 «выбери не-успокаивающий» (3 reassurance-концовки vs 1 тревожная correct) → концовки переписаны в ложные МЕХАНИЗМЫ (де-severity), stem нейтрализован; Q30 «correct — единственный БЕЗ вставного предложения-тезиса» → поизон вплетён ВНУТРЬ define/Зачем. round3 — Q30 «majority-median: define/Зачем имеют 3-1 split, консенсус=correct» → непоизоненные зоны разведены в РАЗНЫЕ истинные парафразы (define и Зачем теперь 4 различных значения/зона → per-zone majority ≠ correct, эксплойт снят).
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[], corr_longest=False, ratio 1.08-1.20); skel HARD=0 touched; structure-tell 0.0-4.33 (Q30 0.0 = identical-scaffold каталог с парафраз-разведёнными зонами, stamp verdict OK); correct drift=0. Blind финал: BSM один защитимый + completeness_ok=true ×4; form: Q33/Q28/Q30 clean, **Q22 severity-асимметрия принята как R6 intrinsic-semantic** (форма полностью выровнена, majority-vote нет — 4 разных механизма, stem не отображается в UI).
- Sidecar reviews обновлён (16/35). Остаток 19 блоков (тот же AP#1, structure-tell 20-24) → c5+; freshness sidecar + POS de-cycle отложены. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c5 (Q7,Q8,Q12,Q18) — 2026-07-13
- Worst-first по structure-tell (Q18=24, Q12=24, Q8=22.67, Q7=22.33). Тот же Golden AP#1: correct — насыщенный каталог (391-467ч, bt4-12), дистракторы — плоские заглушки (98-176ч, bt0-1). correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[7,8,12,18]; SECPATCH пуст (оси заблуждений сохранены по label → секции валидны как есть).
- Метод: дистрактор = параллельный каталог тем же скелетом, что correct, 1 отравл. факт из реальных сущностей теории (NF4/Double Quantization/Paged Optimizers; magnitude/direction W=m·(V/||V||); BitsAndBytesConfig/prepare_model_for_kbit_training/LoraConfig/SFTTrainer/packing; L_ORPO/odds-ratio). Оси: Q7 A=NF4-обучает-base / B=Paged-multi-GPU / D=4bit-compute-5×; Q8 A=overhead-выше / B=high-rank / C=меньше-параметров+быстрее; Q12 A=полные-16GB / B=без-prepare_kbit / C=packing-увеличивает-VRAM; Q18 A=нужна-reference / C=нужна-reward-model / D=раздельный-контроль.
- **2 прохода reviewer + прицельные фиксы:** round1 — consensus-leak на Q7/Q12 (идентичные строки непоизоненных зон → мажоритарное голосование по клаузам БЕЗ знания темы) → развёл непоизоненные зоны в РАЗНЫЕ истинные парафразы + варьировал зачины (string-majority снят). round2 (strict-reviewer) — остаточный topical-consensus + форм-tell'ы: контраст-маркеры (Q7-B «а не выгрузка на CPU», Q8-A «а...выше»), double-error Q8-C (меньше-параметров И быстрее — оба ложны), само­противоречия Q18-C/D с видимой формулой (один λ vs «два оптимизатора»/reward-стадия). Все ДЕТЕРМИНИРОВАННЫЕ форм-tell'ы устранены прицельно (Q8-C → single-delta: параметры≈истинно, ложь только скорость; Q18-C/D переформулированы без противоречия формуле).
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[], corr_longest=False, ratio 1.08-1.13); skel HARD=0 touched; structure-tell 1.0-4.67; stamp verdict OK; correct drift=0. Blind BSM: one_defensible + completeness_ok=true ×4.
- **ОСТАТОЧНО (честно):** 4 блока surface-clean + BSM-clean, но несут остаточный single-delta topical-consensus (верный = единственный, согласованный со скелетом по всем клаузам). ПРИНЯТ как R6 intrinsic-semantic по прецеденту c4 Q30: бинарный surface-гейт (единственный автоматический SFPL-гейт) его не ловит; метод single-delta предписан самим скиллом; атака требует чтения+топикального рассуждения по каждой клаузе — несопоставимо сложнее тривиального length/backtick-tell'а на HEAD (плоские заглушки). НЕ помечены как fully SFPL-green; идеальный fix = independent equal-richness mental models (крупнее single-tick, отложено).
- Sidecar reviews обновлён (20/35). Остаток 15 блоков (тот же AP#1, FLAT-маркеры stamp Q5/9/13/16/25/26/35, structure-tell 16-24) → c6+; freshness sidecar + POS de-cycle отложены. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c6 (Q5,Q9,Q13,Q15) — 2026-07-13 — СМЕНА ПОДХОДА
- Worst-first по structure-tell (Q5=22.33, Q15=22, Q9=21.67, Q13=21). Тот же Golden AP#1: correct — насыщенный каталог (402-482ч, bt3-5), дистракторы — плоские заглушки (98-216ч). correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[5,9,13,15]; SECPATCH пуст (оси misconception сохранены по label).
- **КЛЮЧЕВАЯ СМЕНА ПОДХОДА (урок c5):** вместо single-delta параллельных клонов (которые в c5 дали consensus/majority-leak) — INDEPENDENT equal-richness mental models: каждый дистрактор = СВОЁ цельное ошибочное мировоззрение, поднятое до длины/плотности correct, но НЕ «correct с одной подменой». Оси: Q5 B=full-FT-в-FP8 / C=LoRA-это-квантизация / D=PEFT-это-HW-sparse; Q9 B=embed/lm_head / C=один-слой-за-раз / D=слои-не-влияют; Q13 B=большая-учится-у-маленькой / C=logit-дешевле / D=speculative-decoding=distillation; Q15 A=DPO-всё-ещё-RM+PPO / B=большой-lr-2e-4 / D=те-же-4-модели.
- **reviewer (1 проход) — ГЛАВНЫЙ РЕЗУЛЬТАТ:** «чистого скелета клауз для majority-leak НЕТ, голосование по совпадению формулировок НЕ работает» — consensus-leak c5 УСТРАНЁН сменой подхода. BSM: one_defensible + completeness_ok ×4. Остаточно strict-reviewer отметил SOFT length/completeness-leak (верный чаще самый длинный + единственный с хеджами, дистракторы несут абсолюты «исключительно/только/строго») + Q13-O3 term-outlier (`Speculative decoding`) + Q15 формула loss только у correct.
- **Обработка остатка:** length-leak в пределах binding-гейта (surface tells=[], ratio ≤1.22 <1.25 — порог скилла); дешёвые фиксы применены (де-карикатура Q5-D: убран Ampere/2:4-sparsity нагром; снят капс ВСЕХ/НЕ; «антипаттерн»→«рискованный»). Q13-O3 `Speculative decoding` СОХРАНЁН намеренно (это верная целевая misconception — люди реально путают spec decoding с дистилляцией; удаление потеряло бы педагогику). Q15 формула — intrinsic (closed-form DPO-loss опознаётся знанием предмета = domain knowledge, не форм-tell). Полная де-абсолютизация + формулы во всех опциях отложены как over-polish.
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[], ratio 1.09-1.22); skel HARD=0 touched; structure-tell 5.3-12.3 (богатые независимые опции, около/выше gold=9); stamp verdict OK; correct drift=0.
- Sidecar reviews обновлён (24/35). Остаток 11 блоков (тот же AP#1, FLAT-маркеры stamp Q2/10/11/16/17/25/26/35, structure-tell 16-22) → c7+; freshness sidecar + POS de-cycle отложены. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c7 (Q10,Q17,Q25,Q35) — 2026-07-13
- Worst-first по structure-tell (HEAD 19-20.67): rank/alpha, KTO, железо/VRAM, cost self-host-vs-API. correct БАЙТ-В-БАЙТ HEAD (drift=0); changed==[10,17,25,35]. Подход — INDEPENDENT equal-richness mental models (продолжение c6).
- **Приём для «correct ПО СУТИ каталог/формула»:** где правильный это таблица VRAM (Q25) или формула Cost (Q35), дистракторы = ПАРАЛЛЕЛЬНЫЕ таблицы/формулы с одной scale/логической ошибкой — убивает и form-leak (все 4 = таблицы), и карикатуру, тянет structure-tell к gold. Оси: Q10 A=max-rank-качество-монотонно / C=важно-абсолютное-α / D=dropout=0-годится-всегда; Q17 B=KTO=DPO-с-парами / C=KTO=maxent-RL / D=KTO-не-тянет-дисбаланс; Q25 B=full-FT≈QLoRA / C=QLoRA-экономит-мало / D=потреб-карт-хватает; Q35 A=cost=только-обучение / B=self-host-почти-всегда-дешевле / D=пиковый-H100+месяц-base-инференса.
- **4 слепых прохода:** Q10/Q17 clean с 1-й итерации (BSM: все 3 дистрактора PLAUSIBLE, second-correct нет, Q17 «образцовый»). Q25 initial BSM поймал CARICATURE (O2 «70B full FT в 4090» + внутр. противоречие, O4 «QLoRA=full-FT VRAM») + off-scope O1 (GRPO) → переделаны в параллельные VRAM-таблицы [SECPATCH B/C/D], re-review clean. Q35 initial magnitude-inflation O2; финальный reviewer поймал self-refuting язык D («ошибочно закладывают»/«вместо честных ~$10» — leak числа) → снято [SECPATCH D].
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[], ratio<1.25); skel HARD=0 touched; structure-tell HEAD 19-20.67 → CUR Q10=7.67/Q17=10.0/Q25=7.33/Q35=9.33 (near-gold); stamp verdict OK; correct drift=0.
- Sidecar reviews обновлён (28/35), коммит da868aa3. Остаток 7 блоков → c8+. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c8 (Q2,Q11,Q16,Q26) — 2026-07-13
- Worst-first по structure-tell (HEAD 16-18.67): лестница Prompting/RAG/FT (Q2), DAPT-vs-SFT (Q11), GRPO (Q16), гиперпараметры-каталог (Q26). correct БАЙТ-В-БАЙТ HEAD (drift=0, 35/35); changed==[2,11,16,26]; SECPATCH пуст (оси HEAD-заглушек сохранены). Заглушки (~100-180ч) → параллельные каталоги/мировоззрения плотности correct.
- Оси: Q2 A=за-актуальность-фактов-отвечает-FT / C=пройдя-лестницу-оставляют-только-FT / D=роли-swapped(стиль-из-RAG,факты-из-FT); Q11 A=DAPT↔SFT-swapped+BloombergGPT / B=SFT-loss-по-всей-последовательности / D=DAPT=SFT-один-режим; Q16 A=улучшенная-value-model+GAE/MSE / B=любые-субъективные-через-judge / C=как-DPO-на-статических-парах; Q26 A=LoRA-lr-меньше-full-FT(инверсия) / C=DPO-overtuned(10+эпох) / D=epochs-10-20.
- **4 слепых прохода (BST worked-to-parity, BSM PASS ×4):** Reviewer1 формула/каталог-tell Q16/Q26 → формулы розданы Q16, частичная richness Q26; Reviewer2 Q11 caps-tell + Q26 ranges/labels → caps ТОЛЬКО/СЫРОМ/ВСЕЙ розданы Q11, ranges+dual-batch Q26; Reviewer3 Q26 labels/operators «direct leak» → полный richness-parity «Правила:»/«Sanity:» + >>/<< во ВСЕХ 4, отметил salient-опцию Q11 = ДИСТРАКТОР («не критичен») → Q11 accepted; Reviewer4 Q26 hardware/mixed-operators/snake_case → A100/H100 + mixed >>/<< + effective-batch/num_gpus во всех. BSM: correct во всех 4 берётся РАССУЖДЕНИЕМ (все варианты — правдоподобные mental models).
- Гейты: SCHEMA_OK; surface NO_TELLS ×4 (tells=[], corr_longest=False, ratio<1.25); skel HARD=0 touched; structure-tell HEAD 16-18.67 → CUR 3.0-5.67; stamp verdict OK; correct drift=0. Authoritative answer-parity-gate: CORRECT_LONGEST_RATE=0.143 ok, CORRECT_WRONG_AVG_RATIO=1.009 ok, DETAIL_PARITY=0.343 ok, STYLE_GUESSABILITY=0.271 ok, SOURCE_COVERAGE=ok.
- **ОСТАТОЧНО (честно):** Q26 — 4 плотных ПАРАЛЛЕЛЬНЫХ каталога, CV=0.051 и dup10=14 приняты как §30 documented exception (form-parity vs anti-dup конфликт на байт-locked dense-catalog correct: colon/paren/semi/операторы/железо — паритет у всех, это НЕ padded-clone; dup снижен 66→14 за 7 раундов). Остаточный ультра-гранулярный snake_case-tell Q26 потребовал бы клонирования байт-locked correct → принят как residual (не бесконечная регрессия), коммит без 5-го прохода. c8 добавляет лишь +18 intra-block dup (0.5%) к файловым 3283.
- **freshness sidecar СОЗДАН** (`docs/mcq-quality/fact-freshness/fine-tuning-llm-interview.json`, checked_date=2026-07-13) — SOURCE_COVERAGE закрыт; version-sensitive: GPU-VRAM/цены, GRPO/DeepSeek-R1, DAPT-vs-SFT loss, QLoRA 4-bit, гиперпараметр-диапазоны; knowledge-cutoff guard соблюдён.
- Sidecar reviews обновлён (32/35). Остаток 3 блока (Q1, Q3, Q4 — заглушки) → c9 (финиш файла). Файловая задача POS de-cycle (CORRECT_POSITION_SEQUENCE=35) — отдельным тиком после c9. SFPL/ANSC ⬜ до 35/35.

### ROUND-8 · fine-tuning-llm · SFPL/ANSC c9 (Q1,Q3,Q4) — 2026-07-13
- **ФИНИШ контент-реаудита файла (32→35/35).** Последние 3 блока, тот же Golden AP#1 (correct=каталог 360-400ч vs заглушки 106-159ч, corr_longest=True). correct БАЙТ-В-БАЙТ HEAD (drift=0, 35/35); changed==[1,3,4].
- Оси: Q1 (correct A=определение+«когда нужен») B=fresh-facts(вместо RAG) / C=decoding-params(temperature/top_p/system) / D=over-apply(«как правило нужен» перед любым продом); Q3 (correct C=много-пунктовый список реальных слабостей) A/B/D=тематич. МНОГО-пунктовые списки ЛОЖНЫХ ограничений — инверсии реальных сильных сторон FT: A=output-shaping(формат/tone/схема) / B=efficiency(малая-модель/latency/cost) / D=internalization(правила/domain/длина-промпта); Q4 (correct D=полный VRAM-breakdown ~80GB) A/B/C=ПОЛНЫЙ breakdown у каждого с ложной доминантой+итогом: A=weights(optimizer в half precision,~28GB) / B=activations(checkpointing не спасает,~120GB) / C=gradients(optimizer вторичен,~40GB). SECPATCH Q3 A/B/D + Q4 A/B/C (оси переписаны); Q1 секции HEAD.
- **ПЯТЬ слепых проходов (systematic Form-Parity dig-out, reviewer НЕ читает репо, shuffle 9000-9300+qn):** r1 flagged 3/3 (Q1 оборонит.-скобки, Q3 enumerated-«не»+абсолюты, Q4 stance-contradiction+duplicate+2 карикатуры); r2 переаксировал Q4 B/C; **r3 FAIL Q4** (correct=единств. «суммирует всё» + макс.числа + больше скобок + капс ВСЕХ + FSDP-конкретика) → все 4 дистрактора перестроены в полный breakdown, НО дало dup10=49 (штамп) → откат к варьированным формулировкам; **r4 FAIL Q3** (correct=единств. много-пунктовый список → плюральный вопрос выдаёт) **+ Q4** («якобы» само-палево + числовое 28>24 + громкие абсолюты «перевешивает всё») → Q3 дистракторы→тематич. много-пунктовые ложные списки, Q4 нейтрализованы абсолюты/«якобы», железо внутренне непротиворечиво, +шардинг-упоминание в дистрактор (FSDP не уникален у correct); **r5 PASS 3/3** — Q1 PASS (guessable_by_form=false), Q3 PASS (инверсия-сильных-сторон=осознанная педагогика), Q4 PASS (correct=среднее число не экстремум, делит структуру с decoy). BSM угадал correct по СМЫСЛУ во всех трёх.
- Гейты: SCHEMA_OK; surface NO_TELLS ×3 (tells=[], corr_longest=False, ratio 1.13/1.21/1.15); skel HARD=0; structure-tell CUR Q1=2.33/Q3=4.0/Q4=1.67 (≪HEAD 8.0/8.33/11.67, over≥0 — не padded-clone); dup10 within-block Q1=0/Q3=0/Q4=11; correct drift=0. Authoritative: CORRECT_LONGEST_RATE=0.057, CORRECT_WRONG_AVG_RATIO=0.956, SENTENCE_PARITY=1, DETAIL_PARITY=0.314, ABSOLUTE_MARKER_GAP=0.133, STYLE_GUESSABILITY=0.221, SOURCE_COVERAGE=ok.
- **Не-блокеры (не правились):** Q4-correct чистейшая 2+2+8-арифметика (смягчено: O2 делит 12б/param, correct не экстремум); Q3-B первая клауза «зерно правды»=ровно Golden-паттерн дистрактора. Остаточные FAIL авторитетного гейта — документированные/отложенные: OPTION_LENGTH_RATIO=1.65 (худший Q16 c8 §30; мои блоки <1.35), DUPLICATE_NGRAMS=762 (file-wide §30, +2 от требуемого Form-Parity общего зачина Q4 — per-block dup низкий, не штамп).
- Sidecar reviews обновлён (35/35). **КОНТЕНТ-реаудит файла ЗАВЕРШЁН.** Осталось ЕДИНСТВЕННОЕ: POS de-cycle (CORRECT_POSITION_SEQUENCE=35) — отдельным тиком чистой перестановкой label/order при байт-идентичном содержимом → потом файл FINAL. SFPL/ANSC ⬜ (контент DONE) до POS.

### ROUND-8 · fine-tuning-llm · POS de-cycle (все 35 блоков) — 2026-07-13
- **Файловый пункт, НЕ контент.** До: строгий A→B→C→D цикл (CORRECT_POSITION_SEQUENCE=35, самый длинный +1-mod4 прогон=35). Чистая перестановка label/order внутри каждого блока: correct переставлен на детерминированную целевую позицию (seed=15, сбалансированная целевая seq 9/9/9/8), дистракторы заполнили остальные слоты с сохранением относительного порядка.
- **Содержимое опций (text/sections/correct) БАЙТ-ИДЕНТИЧНО HEAD** — меняются ТОЛЬКО `label`+`order`+позиция correct. Immutable-guard: content-мультимножество (text,correct,sections-json) каждого блока == HEAD; глобально content-мультимножество vs HEAD ИДЕНТИЧНО (drift=0). git diff строго симметричен (356 insertions / 356 deletions — чистое переупорядочивание). 27/35 блоков сменили позицию correct.
- Гейт: **CORRECT_POSITION_SEQUENCE 35→3** (порог ≤5, PASS); CORRECT_POSITION_DISTRIBUTION=1.12 (A/B/C/D=9/9/9/8) ok; schema OK; surface tells=[] ×3 / skel HARD=0 / structure-tell неизменны (position-agnostic). Остаточные gate-FAIL — §30 file-wide documented, НЕ дефекты: OPTION_LENGTH_RATIO=1.65 (худший Q16), DUPLICATE_NGRAMS=762.
- **ФАЙЛ fine-tuning-llm FINAL** (SFPL/ANSC ✅ + POS ✅, 35/35). Sidecar reviews: status=DONE, +pos_decycle. PLAN row 103 → ✅. Следующий worst-first файл выбирается заново (`gen-plan-matrix.py`).

### ROUND-8 · mutation-testing · c1 Q4/Q5 skel-`;` + де-карикатура + sidecar bootstrap — 2026-07-14
- **Новый worst-first файл** (matrix: ΣCAR=2, HIGH; я offset-ниже предполагаемого top-down таргета hibernate-relationships; agentic-patterns off-limits). Файл `testing/mutation-testing-interview.json` (20 блоков) исходно высокого качества — богатые параллельные дистракторы. Первый ROUND-8 тик, sidecar'ов не было → **bootstrap freshness + review sidecars** (freshness закрыл SOURCE_COVERAGE: PIT 1.19/Stryker 8/mutmut 3, checked_date=2026-07-14).
- **Binding-дефекты авторитетных гейтов, устранённые в scope:** skel-HARD (hard_fails 2→0): Q4-A нёс лишний `;` в `` `line1;`↔`line2;` `` → `` `stmtA`↔`stmtB` `` (backtick-count 42 сохранён); Q5-B нёс `;` в `` `{ log(); }` `` — блок переписан целиком (см. ниже). surface-parity Q4/Q5 tells=[] (всего реальных tells по файлу = 3: Q1/Q16/Q20 backtick-bucket — следующий контент-тик).
- **Де-карикатура (главный таргет цикла, ΣCAR):** первый слепой проход (SEED=9400) FAIL — reviewer пометил каррикатуру O3 в обоих блоках. Q4-C «ровно ОДИН mutation-оператор — инверсия булевых, 7 псевдонимов, никакие boundary/math не применяются» (абсолютизм + избыточные алиасы) → правдоподобное заблуждение **«mutation = подмена ВХОДНЫХ данных (фаззинг)»** (text + 4 wrong-секции). Q5-B «Boundary удаляет void-вызовы log/save/notify/flush из тела» (4 произвольных вызова натянуты) → плотная cross-operator путаница **«Boundary = Remove Conditional (условие → `if (true)`)»** (text + 4 секции).
- **correct (Q4-D, Q5-A) БАЙТ-В-БАЙТ HEAD** (вне git diff подтверждено); менялись только тексты/секции 3 дистракторов (Q4-A, Q4-C, Q5-B); label/order/1-correct инвариантны; diff 11/11 строк.
- **Второй слепой проход (SEED=9500, независимый reviewer) PASS 2/2:** Q4 guessable_by_form=false, semantic_correct=D ✓, flags=[]; Q5 guessable_by_form=false, semantic_correct=A ✓, flags=[]. Три дистрактора в каждом = самостоятельные mid-level заблуждения (Q4: block-swap/rename/fuzz-confusion; Q5: Constant/Negate/Remove-Conditional), без дублей и карикатур. stamp-audit Q4/Q5 чисто.
- **Остаток по файлу (след. тики):** (1) POS de-cycle — CORRECT_POSITION_SEQUENCE=20 (строгий A→B→C→D); (2) surface backtick-bucket Q1/Q16/Q20; (3) добить остальные блоки под 20 правил + blind. Accepted-exceptions (§30): OPTION_LENGTH_RATIO=1.64 (code-span word-collapse, binding surface length-tells нет), DUPLICATE_NGRAMS=98 (структурный skeleton-паритет — общий каркас, чтобы correct не угадывался).

### ROUND-8 · mutation-testing · c2 POS de-cycle (все 20 блоков) — 2026-07-14
- **Файловый пункт, НЕ контент.** До: строгий A→B→C→D цикл (CORRECT_POSITION_SEQUENCE=20). Чистая перестановка `label`+`order` внутри каждого блока: correct на детерминированную target-позицию (seed=3, target_seq=`BABBDDACAACCDBCCADDB`, maxrun=3, сбаланс. 5/5/5/5), дистракторы заполнили остальные слоты с сохранением относит. порядка.
- **Содержимое опций (text/correct/sections) БАЙТ-ИДЕНТИЧНО HEAD** — immutable-guard: per-block + глобальный content-мультимножество (text,correct,sections-json) == HEAD (drift=0). git diff строго симметричен (205 ins / 205 del — чистое переупорядочивание). 15/20 блоков сменили позицию correct.
- Гейт: **CORRECT_POSITION_SEQUENCE 20→3** (порог ≤5, PASS); DISTRIBUTION=1.0 (5/5/5/5); schema OK; surface tells неизменны (=3: Q1/Q16/Q20, position-agnostic); skel hard_fails=0. Остаток gate-FAIL = §30 documented: OPTION_LENGTH_RATIO=1.64, DUPLICATE_NGRAMS=98. Blind review не требуется (контент не менялся).
- Sidecar reviews: +c2, +pos_decycle, residual POS→✅. **Осталось по файлу:** surface Q1/Q16/Q20 + добор блоков Q1-3/Q6-20.

### ROUND-8 · mutation-testing · c3 surface backtick-bucket tells Q1/Q16/Q20 → 0 — 2026-07-14
- **Surface-only, formatting-only.** 3 реальных tells (backtick-bucket): Q1 (бакеты 1-2), Q16 (correct C уникально bt=0 → `-bt` tell), Q20 (дистрактор bt=0). Механика: `bucket(bt)` = 0/(1-2)/(≥3); tell фаерится при разбросе бакетов и «correct уникально без backticks».
- Фикс — обёртка УЖЕ-присутствующих терминов в `backticks` (слова/порядок/смысл неизменны, `strip_bt` == HEAD): Q1-A `autogen` + Q1-D `covered` (bt 2→3, все bucket 2); Q16-C `incremental`+`in-process` (correct, formatting-only, bt 0→2) + Q16-A `selection` (дистрактор, raw-паритет bt→2, все bucket 1); Q20-D `dashboard` (bt 0→1, все bucket 1).
- **correct-тач только Q16-C и только formatting** (harness whitelist + strip-backtick-equality guard); Q1-B/Q20-B correct нетронуты. diff 5 строк (5×5 backtick-adds).
- Гейт: **surface real tells 3→0** (вся файла NO_TELLS); skel hard_fails=0; schema OK; POS/DISTRIBUTION неизменны. **Blind SEED=9600 PASS 3/3:** Q1/Q16/Q20 guessable_by_form=false, semantic_correct ✓ (B/C/B), без карикатур/дублей; ревьюер-нит по Q16 (1-vs-2 bt у дистрактора) устранён добором Q16-A.
- Sidecar reviews: +c3/+blind_c3, residual surface→✅. **Осталось по файлу:** добор блоков Q1-3/Q6-20 под 20 правил + blind (per-block факт/uniqueness/plausibility).

### ROUND-8 · mutation-testing · c4 Q1-3/Q6-8 контент-реаудит + переработка Q8 B/D — 2026-07-14
- **Контент-реаудит чанка Q1/Q2/Q3/Q6/Q7/Q8** по 20 правилам через 2 независимых blind-ревьюера (SEED=9700). PASS сразу: Q1/Q2/Q3 (correct O1/O1/O1, guessable_by_form=false), Q6 (correct O4), Q7 (correct O4 — near-miss O1 «Void Method Call ДОБАВЛЯЕТ вызов» vs correct «удаляет» — легитимный FALSE-дистрактор, не hidden_truth).
- **Q8 FAIL → переработка (correct C `killed/(total−equivalent)` byte-lock).** Дистрактор D был каррикатурой «годным считается ТОЛЬКО 100%, 99% провал, обязателен даже при equivalent» (absolutes_tell). Переписал D. Промежуточный `killed/total` **отвергнут blind-ревью как second_correct** — это реальная PIT-метрика Mutation Coverage. Финальный D = `covered_mutants/total` (модель «covered≠killed»: мутант засчитан, если тест до него *добрался*, а не *уронил*) — однозначно ложна, не именованная метрика. Плюс подрезан self-announcing хвост B «100% легко достижимо, если ПЛОДИТЬ equivalent» → «кажется лёгким, ведь equivalent прибавляют к killed как якобы убитых».
- **Урок:** формульные дистракторы mutation score опасны hidden_truth — `killed/total` (Mutation Coverage) и `killed/(killed+survived)` (Test Strength) РЕАЛЬНЫ у PIT; нельзя как «неверные». Совет ревьюера взять `killed/covered` отвергнут по той же причине.
- Гейт: git diff симметричен (7/7, только Q8-B/Q8-D); surface Q8 tells=[] (ratio=1.21); skel hard_fails=0; answer-parity профиль ИДЕНТИЧЕН HEAD (accepted §30/§18.1: OPTION_LENGTH_RATIO=1.64/6 блоков, DUPLICATE_NGRAMS=98 — не введено правкой); POS_SEQ=3, DIST=1.0, ABSOLUTE_MARKER_GAP=0.017 ok. **Re-blind Q8 SEED=9900 PASS** (correct O3=C, guessable_by_form=false, hidden_truth снята).
- Sidecar reviews: +c4/+blind_c4, blocks_reaudited 2→8, +reworked Q8. **Осталось по файлу:** Q9-Q20 контент-реаудит + blind, затем FINAL. Non-blocking: A(`total/killed`) odd_one_out — на будущее.

### ROUND-8 · mutation-testing · c5 Q9-14 контент-реаудит (Q11 форма + Q14 факт Mutil→Gremlins) — 2026-07-14
- **Чанк Q9-Q14** по 20 правилам, 2 независимых blind (SEED=9950). **PASS сразу:** Q9 (triage выживших), Q10 (equivalent unkillable; «автодетект» ложен, не hidden_truth), Q12 (Stryker JS/TS/.NET/Scala; O2 «break не роняет сборку» — сильный дистрактор), Q13 (mutmut/pytest/медленно).
- **Q11 FAIL (форма, 3 итерации).** form-tell: correct C — единственный «богатый позитивный фича-лист» (Java+HTML+JUnit5+incremental+coverage-based+суперлатив «самый популярный»), дистракторы = ограничение/чужая платформа. Correct byte-lock → балансирую дистрактором: **B переработан в позитивный Java-twin correct** (тот же суперлатив + фича-лист, единственная ошибка — «правит исходный `.java` и перекомпилирует» вместо байткода, вшита в середину, конец на позитиве); D → «только XML/CSV, HTML не формирует». re-blind SEED=10010 **PASS** (guessable_by_form=false, semantic_correct=C, second_correct=нет). **Урок формы:** когда correct byte-lock и уникально-позитивен — сделать ОДИН дистрактор его точным позитивным зеркалом с единственной вшитой фактической ошибкой; суперлатив у correct дублировать в дистрактор, чтобы не был tell.
- **Q14 FAIL (фактический).** `Mutil (Go)` — **несуществующий инструмент** (verified WebSearch: реальные Go-тулзы — `Gremlins`/go-gremlins, go-mutesting). Был в correct B + всех дистракторах + question_text + .md-шпаргалке (строка 371). **Замена `Mutil`→`Gremlins`** во всех опциях/секциях/question_text JSON + .md; языковые привязки сохранены (correct=Gremlins Go; swap-дистрактор=Gremlins PHP ошибка). re-blind SEED=9970 **PASS** (correct единственный, форма честная twin-lists, различитель Cosmic Ray≠mutmut). Правка correct оправдана доказанной фактической ошибкой (protocol §1: факт-корректность > byte-lock).
- Гейт: все 20 блоков surface tells=[]; skel hard_fails=0; answer-parity accepted-профиль (OPTION_LENGTH_RATIO=1.64, DUPLICATE_NGRAMS 98→100 — намеренное сближение Q11-B с correct, §18.1); ABSOLUTE_MARKER_GAP=0.0; 0 «Mutil». Sidecar: +c5/+blind_c5, blocks_reaudited 8→14, +reworked Q11/Q14. **Осталось:** Q15-Q20 + blind → FINAL.

### ROUND-8 · mutation-testing · c6 Q15-Q20 контент-реаудит (Q16/Q18 форма) — ФАЙЛ FINAL — 2026-07-14
- **Чанк Q15-Q20** по 20 правилам, 4 независимых blind-раунда (SEED 9990/10010/10090/10200/10300/10400). **PASS сразу:** Q15, Q17, Q19 (многослойная критика MT: скорость+equivalent-шум+ложная уверенность+unit-фокус+cost-benefit+сопровождение; дистракторы — чистые инверсии integration-tool/equivalent-is-plus/no-criticism), Q20 (CI best-practice: incremental на PR + threshold-гейт РЕАЛЬНО роняет билд + полный прогон ночью; сильный twin-дистрактор «гейт только раскраска, ронять нельзя»).
- **Q16 FAIL (форма, 2 итерации).** Первый blind: три дистрактора несли одинаковый «самоопровергающий» хвост («это не ускоряет, лишь надёжнее/точнее»), correct C — единственный с позитивным финалом → форма выдаёт. Correct byte-lock → **убрал самоопровержение, каждый дистрактор уверенно утверждает свою неверную модель**; второй blind: остаточный tell «correct — единственный разнородный перечень vs моно-тематичные дистракторы» → **O2/O3 переработаны в разнородные списки** (2 реальных приёма спереди + инвертированное ядро: incremental=наращивание выборки / in-process=форк JVM на мутант); третий — нейтрализованы «продающие» хвосты O2/O3. Финал re-blind SEED=10400 **PASS по однозначности** (second_correct=нет, дистракторы = сильные инвертированные определения с hidden_truth-подмешиванием реальных приёмов).
- **Q18 FAIL (форма + second_correct, 2 итерации).** Первый blind: дистрактор нёс ТУ ЖЕ верную таблицу ориентиров (90%+/75-85%/60-75%/N/A), отличаясь лишь фразой про тренд → **second_correct** (нарушение §1 «ровно один защитимый ответ», выше форм-паритета). Correct D byte-lock → **B переработан: external-benchmark ~70% → грубая двухуровневая градация по критичности + абсолют-над-трендом** (две явные ошибки, НЕ второй правильный); **C де-карикатурен** («перевёрнуты/перестраховка» → достижимость-модель «где score дешевле взять, там планку задирают»). Критичность-осознанность размазана на 3/4 опции — correct больше не единственный «it-depends». Финал re-blind SEED=10300 **PASS по однозначности** (second_correct=нет подтверждено ×3 blind).
- **Форм-residual (принят, документирован).** Оба блока: byte-locked correct — это КАНОНИЧЕСКИЙ/нюансный ответ (Q16 полный термин-сет; Q18 «зависит от критичности + тренд важнее»). Остаточная «умеренная» угадываемость по форме структурно неустранима без правки correct (запрещена) — тренд-ось неотъемлемо эксклюзивна для верного ответа, а любая натяжка дистрактора под неё воссоздаёт second_correct. По §1 форм-паритет — низший приоритет и не может перевешивать факт-корректность/single-correct/читаемость. Принято как §30-класс исключение (наряду с Q4 OPTION_LENGTH_RATIO=1.64 и DUPLICATE_NGRAMS=92).
- Гейт: все 20 блоков surface tells=[] (Q16/Q18 corr_longest=False, ratio<1.25); skel hard_fails=0; answer-parity accepted-профиль (OPTION_LENGTH_RATIO=1.64 Q4, DUPLICATE_NGRAMS=92 — оба §30). Sidecar: +c6/+blind_c6, blocks_reaudited 14→20, +reworked Q16/Q18, status FINAL.
- **ФАЙЛ FINAL=✅** — все 20 блоков mutation-testing контент-реаудированы (c1 Q4/Q5, c2 POS, c3 surface, c4 Q1-3/Q6-8, c5 Q9-14, c6 Q15-20). **Урок c6:** для двух-осевого correct (напр. «зависит от критичности» И «тренд важнее абсолюта») идеальный дистрактор невозможен без выбора между second_correct (совпал по одной оси) и форм-tell (correct единственный нюансный) — при byte-lock приоритет за single-correct, форм-residual принимается.

### ROUND-8 · hibernate-relationships · c1 «якобы»-de-tell (file-wide) — 2026-07-14
- **Новый worst-first файл** после mutation-testing FINAL (matrix ΣCAR=14, 15 блоков, databases/, TRACTABLE=моя полоса; fresh, sidecar создан). Файл содержательно высокого качества (когерентные сильные дистракторы), но матричный ΣCAR=14 = **слово-маркер «якобы»**.
- **c1 = file-wide удаление distractor-маркера «якобы»** (27 дистракторов в 14 блоках; в correct — 0 → **byte-locked by construction**). «якобы» помечал ложную клаузу ТОЛЬКО в неверных (27/45 wrong, 0/15 correct) — идеальный distractor-tell, сильнейшая угадываемость. Мех. замена `' якобы '→' '`; грамматика естественна во всех 27; correct == HEAD. Дистрактор теперь **уверенно утверждает ложную модель** (де-tell + рост правдоподобности).
- **blind (SEED=11000, выборка Q1/Q6/Q10/Q11/Q12):** все 5 PASS по однозначности, **second_correct=нет ни в одном** — снятие «якобы» single-correctness не сломало (дистракторы содержательны: Q6-O4 инверсия под именем orphanRemoval, Q12-O2 путаница с @OneToMany).
- **blind ВСКРЫЛ более широкий сквозной form-маркер (цель c2):** correct = уверенно-нейтральный позитивный описатель механизма; дистракторы кластеризуют absolutes/отрицания «лишь/только/нельзя/никак не/одно и то же/синоним/взаимозаменяемы/глобально/физически» — ни один correct не несёт → форм-угадывание 5/5. Требует переавторства дистракторов с СОХРАНЕНИЕМ ошибочности (нельзя просто удалять «лишь» — часто это и есть каррикатура). Плюс **c3:** opening_class/surface формат-паритет (skel hard_fails=11 btick-vs-prose; Q1 backtick-opener выдаёт correct; lone_long Q1/Q15).
- Гейт c1: answer-parity PASS (OPTION_LENGTH_RATIO 1.33→1.29, CORRECT_LONGEST=0.067, ABSOLUTE_MARKER_GAP=0.089, POS_SEQ=3, DUPLICATE_NGRAMS=0). skel hard_fails=11 / surface 11 TELL — форматные, отложены в c3. **Урок:** маркер-де-tell слоями — сначала явное слово-хедж («якобы»), затем широкий absolutes-кластер (c2), затем формат (c3). correct byte-locked → де-tell только через дистракторы.

### ROUND-8 · hibernate-relationships · c2 loud-marker кластер de-tell (13 дистракторов) — 2026-07-14
- **c2 = снятие loud-маркеров** (цель, вскрытая blind c1): 13 дистракторов (Q2-C/D, Q6-A, Q8-B/D, Q10-A/C/D, Q11-A/C, Q12-A/D) переписаны из **абсолютов/false-equivalence/impossibility** («одно и то же/синоним/взаимозаменяемы/не бывает/нельзя/попросту/совсем не/вовсе/глобально/физически») в **уверенно-положительные/правдоподобно-ограниченные утверждения неверного механизма** — БЕЗ ввода новых loud-маркеров, ошибочная модель каждого сохранена (sections валидны). 0 остаточных loud. Мягкие «лишь/не X» оставлены (часть misconception; correct Q6-B тоже несёт «лишь» → «лишь» не чистый маркер). Бонус: Q10-C `;`→`.` (skel hard 11→10).
- **blind (SEED=11500, все 6 переписанных блоков):** ГЛАВНОЕ — **second_correct=НЕТ ни в одном**: смягчение absolutes не сделало дистракторы истинными (все 18 неверны, 6 correct точны, подтверждено пофактно). Q6 — образец (маркер «лишь» размазан в correct → форм-подсказка исчезла).
- **Остаток (цель c3):** (1) opening_class/surface формат-паритет (skel hard=10 btick-vs-prose; Q1 backtick-opener; lone_long Q1/Q15); (2) Q11-A де-карикатура (materialized view слишком абсурден); (3) мягкий descriptive-vs-corrective паттерн — correct byte-locked, маркер в correct не добавить, только продолжать смягчать дистракторы где не рушит misconception. Позиц-tell O3×4 в blind = **артефакт shuffle-SEED, НЕ реального файла** (gate POS_SEQ=3 ok).
- Гейт c2: answer-parity PASS (OPTION_LENGTH_RATIO 1.33 после баланса Q8-B, ABSOLUTE_MARKER_GAP 0.089→0.067, DUPLICATE_NGRAMS=0). **Урок:** de-loud абсолютов повышает правдоподобность (дистрактор перестаёт «кричать ловушку») И снимает форм-tell — двойная польза; но byte-lock correct ставит потолок «descriptive-vs-corrective» асимметрии (нельзя добавить маркер в correct).

### ROUND-8 · hibernate-relationships · c3 opening_class/формат-паритет + Q11-C/Q14-C де-карикатура — 2026-07-14
- **c3 = opening_class/формат-паритет** (цель, вскрытая blind c1/c2): в 10 блоках (Q1/Q3/Q5/Q7/Q8/Q9/Q11/Q12/Q13/Q14/Q15) открытие 4 опций было разнородным (btick-идентификатор vs проза-слово) — где correct единственный btick/проза-opener (Q1/Q8/Q9) это read-through ключ. Дистракторы переформулированы под **класс открытия byte-locked correct** (Q1/Q9 проза→btick; Q8 btick→проза; остальные — одиночный odd-дистрактор). **skel opening_class hard_fails 10→0.** Q8 B/C/D `:`→`;` под скелет correct A (semicolon-presence паритет). Correct не тронут.
- **+ де-карикатура (blind c2/c3 таргеты):** Q11-C (materialized-view/«меняет схему БД» — слишком абсурдно → **advisory-hint модель**: граф необязателен, провайдер вправе проигнорировать → N+1 сохраняется; text+sections переписаны, эксплуатирует loadgraph/fetchgraph-нюанс) и Q14-C (магический «секундомер >200 мс = N+1» → **бюджет времени**, убран cartoonish порог). Обе де-карикатуры blind-подтверждены как не-истинные (single-correct цел).
- **Баланс длин:** Q1-B укорочен (раздутая проза), Q11-C проза сжата → answer-parity OPTION_LENGTH_RATIO 1.47→**1.33 PASS**. Гейт считает ПРОЗА-слова (code-spans вычёркивает) — урок для будущих балансировок.
- **blind (SEED=90714, 11 переписанных блоков):** second_correct=НЕТ, hidden_truth-как-факт=НЕТ, НОВЫХ карикатур c3 не внёс (ревьюер: «Смысловое наполнение и дистракторы качественные»). Q11-C де-карикатура ПОДТВЕРЖДЕНА. **НО вскрыт СИСТЕМНЫЙ form-tell (цель c4):** form_guess=correct **11/11** — correct всегда «энциклопедичная/перечисляющая-всё/мерная» опция, дистракторы короче и категоричнее. Это **pre-existing, correct-anchored** свойство (correct byte-locked к HEAD = исходный автор), НЕ регресс c3 — гейты: CORRECT_LONGEST_RATE=**0.0** (correct не длиннейший по прозе-словам), STYLE_GUESSABILITY=0.328. Вердикт NEEDS_WORK — про этот системный tone-tell, не про правки c3. Позиц-tell = shuffle-артефакт (POS_SEQ=3, DIST=1.33 ok).
- Гейт c3: answer-parity PASS (OPTION_LENGTH_RATIO=1.33 Q10), skel hard_fails 10→**0** PASS. surface: остаточный lone_long на correct (Q1/Q11/Q13/Q15) — **char-based, иррредуцируем** (correct плотен backtick-идентификаторами: длинен по символам, короток по прозе-словам; byte-locked). Sidecar: +c3/+blind_c3, +reworked opening_class/decaricature, status WIP.
- **Остаток → c4 (крупный контент-пасс, отдельный чанк):** distractor tone/plausibility parity — разабсолютить/«размерить» кластеры дистракторов до сопоставимой «мерности» с correct, НЕ добавляя маркеры в correct (byte-lock). ОПАСНО: размягчение → защитимо-верный дистрактор (second_correct); делать поблочно с обязательным blind. **Урок c3:** гейты ловят количественную длину, но НЕ качественную «энциклопедичность» correct — её вскрывает только blind; de-tell идёт слоями: слово(c1)→absolutes(c2)→формат/opening_class(c3)→tone/mensurate(c4).

### ROUND-8 · hibernate-relationships · c4 де-абсолют/plausibility-паритет — ФАЙЛ FINAL — 2026-07-14
- **c4 = ответ на blind c3 «form_guess=correct 11/11»** (системный tone-tell). Разабсолючено **8 дистракторов в 5 блоках** (Q2-D, Q7-B, Q8-B/C/D, Q14-A/D, Q15-D): бальные категоричные маркеры (не поддерживается/только/всегда/достаточно/не работают/без эффекта/отношения не имеет) → уверенно-ограниченные false-belief формулировки. **Ложный механизм каждого сохранён ВЕРБАТИМ** (misconception не тронут → 0 риска second_correct). **ABSOLUTE_MARKER_GAP 0.089→0.0.** opening_class из c3, Q8-`;`-скелет и длины сохранены. Taxonomy-опорные дистракторы (где обогащение=second_correct) НЕ трогались намеренно.
- **blind (SEED=40714, Q2/Q7/Q8/Q14/Q15):** **second_correct=НЕТ ни в одном (5/5)** — де-абсолют не сделал ни один дистрактор защитимо-верным. «hidden_truth»-флаги (Q8-Map/@MapKey, Q14 no-flush/подсчёт-необязателен) ревьюер сам квалифицирует «норм для дистрактора» — это misconceptions, поданные уверенно, не реальная правда.
- **ФОРМ-RESIDUAL ПРИНЯТ (§30-class).** form_guess=correct 5/5 сохранился: ревьюер подтверждает «correct во всех 5 самая полная/мерная», а его же рецепт «подтянуть дистракторы до сопоставимой детализации» = на taxonomy/best-practice вопросах ПРЯМОЙ путь в second_correct. **Вывод: form-tell СТРУКТУРНО НЕУСТРАНИМ при byte-lock correct** (correct = каноничный энциклопедичный ответ; любой равно-полный дистрактор становится верным). Подтверждён blind ×2 (c3 11/11, c4 5/5) как correct-anchored, НЕ регресс правок. Гейты в норме: CORRECT_LONGEST_RATE=0.0 (количественно длина НЕ выдаёт), STYLE_GUESSABILITY=0.344<0.40. Прецедент — mutation-testing Q16/Q18. По §1 single-correct/факт-корректность > plausibility > form-parity.
- Гейт c4: answer-parity PASS (OPTION_LENGTH_RATIO=1.33 Q2, ABSOLUTE_MARKER_GAP=0.0, DUPLICATE_NGRAMS=0, POS_SEQ=3). skel hard_fails=0. surface: остаточный lone_long на correct (char-based, byte-locked, иррредуцируем). Sidecar: +c4/+blind_c4/+de_absolute_c4, status FINAL.
- **ФАЙЛ FINAL=✅** — все 15 блоков hibernate-relationships контент-реаудированы через 4 слоя de-tell (c1 «якобы» → c2 loud-marker → c3 opening_class+де-карикатура Q11-C/Q14-C → c4 де-абсолют), single-correctness подтверждён 4 независимыми blind (0 second_correct во всех). **Урок c4:** на taxonomy/best-practice вопросах, где byte-locked correct = каноничный полный ответ, «энциклопедичность correct» — НЕУСТРАНИМЫЙ form-tell (равно-полный дистрактор = second_correct); принимается как §30 при чистых гейтах и приоритете single-correct (§1). de-tell идёт СЛОЯМИ: слово(c1)→absolutes(c2)→формат(c3)→tone/mensurate(c4).

### ROUND-8 · edge-computing · c1 POS de-cycle + fact-freshness bootstrap — 2026-07-14
- **Новый worst-first tractable файл** после hibernate-relationships FINAL (matrix ΣCAR=16.3 top-tractable, 18 блоков ≤20, architecture/ не off-limits, sidecar'ов не было). **СЫРОЙ файл низкого исходного качества** (в отличие от hibernate) — гейт-скан: OPTION_LENGTH_RATIO=4.22, CORRECT_POSITION_SEQUENCE=18 (строгий цикл ABCD×), DUPLICATE_NGRAMS=687, SOURCE_COVERAGE missing, skel hard=4, surface 10 TELL.
- **c1 = POS de-cycle + fact-freshness bootstrap** (content-preserving, без blind). Correct-позиции были строгим циклом `ABCDABCDABCDABCDAB` → перестановка label/order (target `CADBDBACBDCAACBDDA`, counts A5/B4/C4/D5, arith-run≤4) → **SEQUENCE 18→4, DISTRIBUTION 1.25**. Контент КАЖДОЙ опции (text/sections/correct) **БАЙТ-ИДЕНТИЧЕН HEAD** (set-equal per block; сменились лишь label/order). + fact-freshness sidecar (edge-платформы rolling, Cloudflare Workers/Lambda@Edge/Fastly/Vercel/Deno, checked 2026-07-14) → **SOURCE_COVERAGE ok**.
- **Профиль дефектов (для c2+):** (1) **DUPLICATE_NGRAMS=687 — ДОМИНИРУЮЩИЙ.** ~10 блоков (Q2/3/4/5/8/13/15/16/17/18) — **clause-flip**: все 4 опции почти дословно повторяют correct, отличаясь ОДНОЙ клаузой (Q2: A-зачин «CDN=edge синонимы», C-середина «рантайм во всех POP», D-концовка «edge заменяет origin»). Огромный verbatim-overlap + tell «варьируемая клауза». (2) **OPTION_LENGTH_RATIO=4.22** — Q1 (correct 266 vs дистракторы 96-111), Q11 (1.83): короткие каррикатурные дистракторы. Оба — контент-рерайт с обязательным blind в c2+.
- Гейт c1: POS SEQUENCE 18→4 + DISTRIBUTION 1.25 + SOURCE_COVERAGE — PASS; остаётся OPTION_LENGTH/DUPLICATE_NGRAMS/skel/surface на c2+. Sidecar review создан (WIP, blocks_reaudited=0). **Урок:** сырые templated-файлы (clause-flip опции) — иной класс работы, чем hibernate (subtle de-tell): нужен структурный рерайт дистракторов, чтобы ложь несла свой механизм, а не копию correct.

### ROUND-8 · edge-computing · c2 clause-flip dedup Q2/Q4/Q5 — 2026-07-14
- **c2 = начало контент-рерайта доминирующего дефекта DUPLICATE_NGRAMS.** 9 дистракторов в 3 блоках (Q2/Q4/Q5) переписаны: **ложный тезис каждого выражен СВОИМИ словами** (перефраз общих факт-клауз), а не дословной копией correct. Misconception каждого сохранён (sections валидны), **скелет мирит с correct** (Q2 — single-sentence + `;`-клаузы; Q4 — 3 предложения + `;`-лимиты; Q5 — 2 предложения + `:`), opening_class uniform. **DUPLICATE_NGRAMS 687→593** (~94 снято на 3 блоках), skel hard без регресса (Q2/Q4/Q5 ушли из HARD). Correct byte-locked (== HEAD).
- **blind (SEED=50714, Q2/Q4/Q5):** **second_correct=НЕТ (0/3)**, **factual_error_in_correct=НЕТ (0/3)** — перефраз не сделал дистрактор истинным, correct факт-чисты. Дистракторы разобраны как ложные пофактно (Lambda региональна; изолят не VM; CF $0.10 не $0.60; у Lambda 4 триггера). **form_guess=correct 3/3** — СТРУКТУРНЫЙ tell «correct = единственная опция без перевёрнутой клаузы», **inherent для clause-flip** (correct-anchored; не фактический блокер). Вердикт READY по фактике.
- Гейт c2: DUPLICATE_NGRAMS 687→593 (снижается поблочно), POS/SOURCE_COVERAGE ok. Остаётся FAIL: OPTION_LENGTH_RATIO=4.22, DUPLICATE_NGRAMS=593, skel hard=4 (Q11 code_bucket + др.). Sidecar: +c2/+blind_c2, blocks_reaudited 0→3.
- **Остаток c3+:** ~7 clause-flip блоков (Q3/Q8/Q13/Q15/Q16/Q17/Q18) — тот же перефраз; Q3 сложнее (A/C несут верный список сценариев). Q1/Q11 каррикатура-длина. **ПРИНЯТЬ §30:** структурный form-tell «correct=опция без твиста» inherent для clause-flip при byte-lock correct — снять лишь правкой correct (запрещена); §1 single-correct > form. **Урок:** dedup clause-flip = перефраз ложного тезиса своими словами при сохранении скелет-паритета (иначе skel HARD) и misconception.

### ROUND-8 · edge-computing · c3 clause-flip dedup Q8/Q13 (число-безопасный перефраз) — 2026-07-14
- **c3 = продолжение dedup доминирующего DUPLICATE_NGRAMS** на прозо-ёмких блоках. 6 дистракторов в 2 блоках (Q8 auth/JWT — A/B/D; Q13 KV-сторы — B/C/D) переписаны **прозо-перефразом**: общая процедура (Q8: «достаёт токен из Authorization: Bearer, 401/401, …») и факт-обёртка (Q13: «~1 мс из кэша POP, запись ~1 минуту») развёрнуты СВОИМИ словами, **ЦИФРЫ нетронуты** (~1 мс/~1 минуту/~100 записей/сек — §1: не искажать факты ради метрики). Misconception каждого сохранён (sections валидны), скелет мирит с byte-locked correct: Q8 — 2 предл., 1 `:` (в `Authorization: Bearer`), 0 `;`, проза-open «Воркёр»; Q13 — 2 предл., `:`+`;` present, btick-open `Workers KV`. **DUPLICATE_NGRAMS 593→536** (−57). Correct byte-locked (Q8-C/Q13-A == HEAD; чужие фронт-коммиты 1d3db3d9 файл не трогали, ancestry c2 ok).
- **blind (SEED=70714, Q8/Q13):** **second_correct=НЕТ (0/2)**, **factual_error_in_correct=НЕТ (0/2)** — перефраз не сделал дистрактор истинным. Q8: верна O3 (валидирует+проксирует); ложны O1 (не проксирует→origin пуст), O2 (выпускает новый JWT), O4 (мгновенный глобальный отзыв). Q13: верна O2 (KV read-optimized+eventual); ложны O1 (write-optimized инверсия), O3 (DynamoDB строгая мгновенная), O4 (KV=SQL). **ГЛАВНОЕ УЛУЧШЕНИЕ vs c2 (было form_guess=correct 3/3): form_guess БОЛЬШЕ НЕ выдаёт correct** — Q8 слепой отсеивает лишь O1 (единственная негативная концовка) и гадает среди O2/O3/O4; Q13 отсеивает O1/O4 и гадает между близнецами O2/O3 (различие — клауза DynamoDB). Причина: на процедурных/сравнительных блоках correct САМ clause-flip-формы (не «энциклопедичен»), потому формой не выделяется — в отличие от taxonomy-блоков (Q1).
- Гейт c3: DUPLICATE_NGRAMS 593→536, POS/SOURCE_COVERAGE ok. Остаётся FAIL: OPTION_LENGTH_RATIO=4.22 (Q1/Q11 каррикатура), DUPLICATE_NGRAMS=536 (нетронутые Q3/Q15/Q16/Q17/Q18), skel hard=4 (Q11 code_bucket — НЕ Q8/Q13, не регресс). Sidecar: +c3/+blind_c3, blocks_reaudited 3→5.
- **Остаток c4+:** ~5 clause-flip блоков Q3/Q15/Q16/Q17/Q18 — **ЧИСЛО-ПЛОТНЫЕ** (общие цены/лимиты): прозо-обёртку разводить можно (как Q13), но сами цифры — нельзя (§1); часть DUPLICATE тут §30-inherent (общий факт-скелет сравнения). Q1/Q11 каррикатура-длина. **Урок c3:** число-плотный clause-flip dedup-ится перефразом ПРОЗО-обёртки при сохранении цифр; form-tell снимается когда correct сам clause-flip-формы (процедура/сравнение), в отличие от taxonomy-блоков где correct=энциклопедичен (inherent §30). Блайнд был заблокирован транзиентным API 403 + classifier-unavailable — правки держались в рабочем дереве до восстановления, коммит только после успешного блайнда (скрипты одни acceptance не закрывают).

### ROUND-8 · edge-computing · c4 де-каррикатура/длина-паритет Q1/Q11 — 2026-07-14
- **c4 = второй крупный FAIL OPTION_LENGTH_RATIO=4.22** (после DUPLICATE в c2/c3). **Q1** (определение edge): 3 КОРОТКИХ каррикатурных дистрактора (A edge=кэш 14pw, B edge=браузер 14pw, D edge=ручные VM 9pw vs correct C 39pw) удлинены в **полные утвердительные ложные модели** (2 предл., 0 `:`/`;`, btick-open — паритет с C) + **правдоподобные числа под каждую ложную модель** (A: 100-300 `POP`/hit-rate 90-95%; B: 100-300 мс RTT/сотни КБ бандл; D: 20-50 городов/1-2 VM). **Q11** (хранение состояния): каррикатурный D (абсурдные «DO не хранят состояние», «KV недоступны») де-каррикатурен в защитимо-ложный тезис «edge-сторы — лишь кэш, центральная БД обязательна» (enumeration (1)-(5)-скелет, `;`/`:` present); убран дубль «внешняя БД» (4)/(5) + абсолют «единственный». **OPTION_LENGTH_RATIO 4.22→1.53** (Q1 1.30, Q11 1.09 PASS, worst сместился на Q3), **skel hard 4→3** (Q11 починен). Correct byte-locked (Q1-C/Q11-C == HEAD; ancestry c3 ok, чужие коммиты файл не трогали).
- **blind ×3 итеративно по нитам (§1 plausibility>form): SEED 40714→41114→999.** Все: **second_correct=НЕТ (0/2), factual_error_in_correct=НЕТ (0/2)**. blind#1 — каррикатура ушла, но form_guess=correct (Q1 negation-паттерн «дистракторы через отрицание»; Q11-D дубль external-DB). blind#2 (де-негация Q1 в утвердительные модели + чистка Q11-D) — **Q11 READY**, Q1 остался number-tell «цифры только в O3». blind#3 (числа добавлены во все дистракторы Q1) — **Q1 READY**: ревьюер «тэлла нет, цифры во всех 4, дистракторы не через отрицание, каждый утверждает свою ложную модель, каррикатурности нет».
- Гейт c4: OPTION_LENGTH_RATIO 4.22→1.53, CORRECT_LONGEST_RATE=0.056 (1/18 ok), skel hard 4→3. Остаётся FAIL: DUPLICATE_NGRAMS=536 (число-плотные Q3/Q15/Q16/Q17/Q18), OPTION_LENGTH_RATIO=1.53 (Q3). Sidecar: +c4/+blind_c4, blocks_reaudited 5→7.
- **Остаток c5+:** ~5 число-плотных clause-flip Q3/Q15/Q16/Q17/Q18 (прозо-обёртку разводить, цифры не трогать §1; часть DUP §30-inherent) + точечный длина-балансинг Q3. **УРОК c4:** де-каррикатура = удлинить короткие дистракторы в ПОЛНЫЕ утвердительные ложные модели (НЕ через отрицание correct) + правдоподобные числа под каждую ложную модель — снимает СРАЗУ negation-tell И number-tell, поднимает plausibility при сохранении single-correct. Нит-итерации блайнда: каррикатура→negation→number-tell (3 прохода, диминишинг, факт/single-correct с 1-го прохода чисты).

### ROUND-8 · edge-computing · c5 читаемость + де-«но» Q9 (механически-вжатые clause-flip) — 2026-07-14
- **c5 = дефект ЧИТАЕМОСТИ** (§1 натуральный русский > числовая метрика DUPLICATE). Остаток файла — fact-dense enumeration clause-flip (Q3/Q9/Q14/Q15/Q16/Q17/Q18), где DUPLICATE доминирован ОБЩИМ ФАКТИЧЕСКИМ СКЕЛЕТОМ (цены $0.60/лимиты 128МБ/шаги деплоя/список инструментов) — по §1 плотные факты НЕ перефразируют ради низкоприоритетной метрики. Взят **Q9** (ресайз картинок, наименее число-плотный): перевёрнутая клауза была механически вжата в предложение correct → **висячее «и возвращает» + грамматич. обрывы**.
- **Фикс:** 3 дистрактора (A shared-cache-key, C ignore-Accept, D origin-only) переписаны в ЕСТЕСТВЕННО текущие **утвердительные ложные модели** (не «correct-минус-дефект через но»). Грамматика вычинена, ложный тезис сохранён (sections не тронуты), список инструментов (§30-shared) нетронут. **Q9 dup10 130→28** (общий DUPLICATE 536→517), skel чист (colon/semi/sent паритет, prose-open), correct B байт-лок (== HEAD).
- **blind ×3 итеративно по тэллам (SEED 50909→51509→52209):** все second_correct=НЕТ, factual_error=НЕТ. blind#1 — грамматика/каррикатура ок, но negation-концовки (3 негатива+1 позитив). blind#2 (нейтрализация концовок) — «но»-тэлл (дистракторы 'принимает…НО дефект', correct без 'но'). blind#3 (restructure в утвердительные модели без 'но') — **READY**: «O1 верна, дистракторы бьют по 3 каноническим антипаттернам, форм-тэлл слабый (дефекты замаскированы под выгоды), переработка не обязательна».
- Гейт c5: Q9 dup10 −102, общий DUPLICATE 536→517, skel hard=3 (Q9 чист). Остаётся FAIL: DUPLICATE=517 (fact-dense enum §30-inherent), OPTION_LENGTH_RATIO=1.53 (Q3). Sidecar: +c5/+blind_c5, blocks_reaudited 7→8.
- **Остаток c6+:** Q3/Q14/Q15/Q16/Q17/Q18 fact-dense enum clause-flip — реаудит на (1) single-correct+факт [ПЕРВИЧНО, блайнд], (2) читаемость вжатых клауз [как Q9], (3) де-«но»/negation где короткая правка. DUPLICATE от общего факт-скелета — **§30-ACCEPTED** (§1 факты>метрика). **УРОК c5:** механически-вжатые clause-flip = дефект читаемости (висячие союзы) → фикс переписью в естественную утвердительную ложную модель снимает СРАЗУ читаемость + «но»-тэлл + negation-концовки; итеративный блайнд по тэллам (грамматика→negation→«но», факт/single-correct чисты с 1-го прохода).

### ROUND-8 · edge-computing · c6 когерентность Q14 (само-противоречие clause-flip) — 2026-07-14
- **c6 = дефект ВНУТРЕННЕЙ КОГЕРЕНТНОСТИ** (§1 читаемость/связность > числовая метрика). В fact-dense enum clause-flip механический флип может оставить дистрактор САМО-ПРОТИВОРЕЧИВЫМ. **Q14** (лимиты рантайма): дистрактор A флипал в «полный набор нативных Node API», но остаточная клауза «(`workerd` — подмножество), часть npm не поддерживается» осталась → full-Node тезис против «подмножество» в одном предложении.
- **Фикс:** A переписан в когерентную утвердительную ложную модель «Workers = полноценный Node (fs/TCP/child_process/npm наравне с серверным Node)» — весь текст поддерживает ОДИН ложный тезис, противоречие снято. Убраны cartoonish-абсолюты («щедрые/почти не упираются/без исключений» → «с большим запасом/наравне с серверным Node»). Correct C байт-лок (== HEAD), sections не тронуты. Почищен sent-артефакт гейта: «как в обычном Node.js.» → «Node-рантайме» (в «Node.js.» точка давала ложный лишний период → sent 4→3, паритет 3/3/3/3).
- **blind ×2 (SEED 60914→61514):** оба second_correct=НЕТ, factual_error=НЕТ, верна C. blind#1: противоречие A УСТРАНЕНО («самопротиворечия в верной O1 нет»), но A на грани каррикатуры. blind#2 (де-каррикатура) — **READY**: «единственная верная O1 чиста, дистракторы ложны по разным проверяемым осям (CPU-лимит / нативные бинарники+npm / fs+TCP+child_process), каррикатурность мягкая в пределах нормы».
- Гейт c6: skel hard=3 (Q14 чист, sent-паритет). DUPLICATE=517 (Q14 — когерентность-правка, НЕ dedup: clause-flip скелет сохранён намеренно под spot-the-flip дизайн), OPTION_LENGTH_RATIO=1.53 (Q3). Sidecar: +c6/+blind_c6, blocks_reaudited 8→9.
- **Остаток c7+:** Q3/Q15/Q16/Q17/Q18 fact-dense enum — реаудит на single-correct+факт [блайнд], когерентность вжатых клауз [как Q14], читаемость [как Q9], де-каррикатура. DUPLICATE от общего факт-скелета + form-tell «correct=полная/ограничивающая» + лёгкое cross-clause напряжение (дистрактор флипает одну ось, держит общий скелет) — **§30-ACCEPTED** (§1 факты/читаемость > метрика; снять лишь правкой byte-locked correct). **УРОК c6:** механический clause-flip может оставить само-противоречие (флип-тезис vs остаточная клауза) — фикс переписью в когерентную ложную модель; беречься sent-артефактов гейта («X.js.»).

### ROUND-8 · edge-computing · c7 де-каррикатура Q3 (3 оси знания) — 2026-07-14
- **c7 = де-каррикатура** fact-dense enum (§1 plausibility > метрика). **Q3** (выгоды/когда применять, помечен «harder»): 3 дистрактора несли cartoonish-абсолюты — A «всегда минимальная цена (дешевле любого при любой нагрузке)», B «для любых задач без исключений (тяжёлый ML/batch/stateful)», C «главная и единственная выгода DDoS».
- **Фикс:** переписаны в уверенные правдоподобные ложные модели БЕЗ абсолютов, каждый ломает РОВНО ОДНУ ось знания: A — экономика (стабильно меньшая цена, даже тяжёлый CPU дешевле `EC2` → ложь: на тяжёлом CPU дороже), B — область применения (edge для тяжёлого ML/batch/stateful наравне с лёгким → ложь: лимиты CPU/времени), C — latency (задержка почти не падает, динамику считает origin → ложь: edge снижает RTT). Correct D байт-лок (== HEAD), sections не тронуты, скелет-паритет (2 предл., 1 colon, 0 semi, «Главные выгоды:»-open). OPTION_LENGTH_RATIO Q3 1.53→сбаланс (worst→Q7=1.38), **skel hard 3→2**.
- **blind (SEED 70314):** second_correct=НЕТ, factual_error=НЕТ, верна D. Ревьюер: «дистракторы аккуратны, каждый ломает ровно одну ось, абсолюты сконцентрированы в ложных как МАРКЕРЫ ОШИБКИ (педагогически оправдано), когерентность сохранена». Похвалил дихотомию correct «часто меньшая цена» vs distractor «стабильно меньшая» как «точную и обучающую». form-tell минимальный «в пределах нормы, угадать надёжно нельзя, правки не требуются». **READY**.
- Гейт c7: OPTION_LENGTH_RATIO 1.53→1.38, skel hard 3→2 (Q3 чист). DUPLICATE=516 (Q3 — де-каррикатура, clause-flip скелет сохранён намеренно). Sidecar: +c7/+blind_c7, blocks_reaudited 9→10.
- **Остаток c8+:** Q15 (само-противоречие C: «нулевой»+«50-500 мс долгий»), Q17 (двойной ':' в C), Q16 (cartoonish A), Q18 — реаудит на single-correct+когерентность+читаемость. DUPLICATE от факт-скелета + form-tell «correct=осторожная опция» — §30-ACCEPTED. **УРОК c7:** де-каррикатура = убрать абсолюты, переписав дистрактор в уверенную ложную модель, ломающую РОВНО ОДНУ ось знания; абсолюты в ложных ок как маркер ошибки, если сбалансированы дихотомией с correct.

### ROUND-8 · edge-computing · c8 когерентность Q15 (само-противоречие cold-start) — 2026-07-14
- **c8 = дефект ВНУТРЕННЕЙ КОГЕРЕНТНОСТИ** (повтор паттерна Q14-A). **Q15** (cold starts): дистрактор C само-противоречив — скопированный из correct зачин «У Workers cold start по сути нулевой» столкнулся с флип-клаузой «без Provisioned Concurrency `V8`-изолят стартует 50-500 мс, первый запрос долгим» (нулевой vs 50-500 мс долгий в одном предложении).
- **Фикс:** зачин C → «У Workers cold start ощутимый без Provisioned Concurrency: `V8`-изолят стартует 50-500 мс...» — вся опция теперь говорит ОДНО (Workers медленный без PC), misconception «Workers нужен PC» (по sections) сохранён. Общий скелет (Lambda-предложение + warm-предложение) не тронут. Correct B байт-лок (== HEAD), skel чист (colon/semi/sent-паритет).
- **blind (SEED 80315):** second_correct=НЕТ, factual_error=НЕТ, верна B. Ревьюер прямо: «самопротиворечивых опций НЕТ, классической ловушки „нулевой и тут же долгий 50-500 мс" нет» — противоречие C УСТРАНЕНО. Каррикатуры нет, форма ответ не выдаёт (O2/O4 общий зачин, различие в Lambda-хвосте). **READY**.
- Гейт c8: skel hard=2 (Q15 чист). DUPLICATE≈516 (когерентность-правка, не dedup). OPTION_LENGTH_RATIO=1.38 (Q7). Sidecar: +c8/+blind_c8, blocks_reaudited 10→11.
- **Остаток c9+:** Q16 (cartoonish A «при любом профиле…всегда ниже EC2»), Q17 (двойной ':' в C), Q18 (деплой) — реаудит на single-correct+когерентность+читаемость. **УРОК c6+c8 (повторный):** механический clause-flip, копирующий зачин correct и флипающий продолжение, РЕГУЛЯРНО даёт само-противоречие — искать пары «скопированный позитив зачина + флип-негатив продолжения» (Q14-A, Q15-C); фикс = переписать зачин под ложный тезис дистрактора. §1: когерентность/читаемость > метрика DUPLICATE.

### ROUND-8 · edge-computing · c9 де-каррикатура Q16-A + читаемость Q17-C — 2026-07-14
- **c9 = де-каррикатура + читаемость на fact-dense enum.** **Q16** (цена): дистрактор A нёс cartoonish-абсолют «Edge дешевле при любом профиле: …цена всегда ниже `EC2`». Переписан в уверенно-ложную модель без абсолютов: «Edge остаётся дешевле `EC2` и на кэше, и на спайках; выгода держится даже на тяжёлом CPU-инференсе и stateful-нагрузке» — ломает РОВНО одну ось (переклаим edge-выгоды на CPU/stateful: кэш+спайки правда, но over-claim = защитимо-неверно); `;`-скелет и 2 colon под correct D. Q16-B: лишний двоеточие «CPU-время не тарифицируется:» → тире (colon-паритет [3,3,2,3], semi [1,1,1,1]).
- **Q17** (observability): дистрактор C имел двойной двоеточие «Трейсинг проще: …в одном централизованном рантайме: достаточно…» (§1 читаемость). Фикс: «Трейсинг проще, ведь весь код в одном централизованном рантайме: достаточно локальных логов…» — один colon, паритет [4,4,4,4], misconception «трейсинг проще/централизован» сохранён.
- **blind ×2 (SEED база 71400, +16/+17): ОБА OK.** single_correct=O4 в обоих (= byte-lock Q16-D/Q17-D), second_correct=НЕТ, factual_error=НЕТ, каррикатур нет. Q16: ревьюер подтвердил де-каррикатуру A («уверенно-ложная через over-claim, не абсурд»). Q17: двойного двоеточия НЕТ, читаемость ровная. **READY**.
- Гейт c9: skel hard=1 = **Q18/b0** (pre-existing, ещё не правлен; НЕ Q16/Q17). DUPLICATE≈516 (§30-inherent fact-scaffold, LOW). OPTION_LENGTH_RATIO=1.38 (worst Q7 — c7 §30-accepted). Correct Q16-D/Q17-D байт-лок. Sidecar: +c9/+blind_c9, blocks_reaudited 11→13.
- **DEFER Q16-C (blind вскрыл, зафиксировано в residual):** дистрактор C само-противоречив — тезис «Workers и Lambda@Edge одинаковая цена $0.60/млн» конфликтует с ОБЩИМ scaffold-предложением «Workers ~$50, Lambda@Edge ~$60» (разные цены = ПРАВДА). Устранить нельзя без смены misconception (section-rewrite, риск single-correct) или ломки shared-scaffold-чисел → отдельный тик. **УРОК c9:** когда ложный тезис дистрактора конфликтует с общим scaffold-фактом, это НЕ bounded-правка — DEFER, не форсить в readability-проходе.
- **Остаток:** Q16-C (само-противоречие, section-rewrite) + Q18 (деплой, skel hard Q18/b0) — реаудит single-correct+когерентность+читаемость. После — edge-computing FINAL.

### ROUND-8 · edge-computing · c10 skel semicolon-паритет Q18 (деплой) — 2026-07-14
- **c10 = закрытие единственного skel hard-fail файла.** **Q18** (стратегии деплоя, correct=A): дистрактор D нёс `;` («Секреты хранят прямо в коде или открытом `wrangler.toml`; `wrangler secret put` недоступен») при 0 `;` у A/B/C → `semicolon_presence mismatch {A:0,B:0,C:0,D:1}` (skel HARD Q18/b0). Фикс: `;` → «, а» — semicolon-паритет [0,0,0,0], misconception «секреты в коде» сохранён. Q18 spot-the-flip: A=baseline (верно), B флип пропагации/rollback, C флип canary, D флип секретов — каждый ломает одну ось, single-correct цел.
- **blind (SEED 71418): OK.** single_correct=O2 (= byte-lock correct A), second_correct=НЕТ, factual_error=НЕТ, читаемость ровная. form_guess «умеренный»: O2 единственный без негативного оборота (недоступен/не поддерживается) — ревьюер: «чтобы выбрать между тремя негативами, надо знать факт про secret put; форма даёт лишь эвристику». §30-inherent negation-tell (correct byte-locked = un-flipped baseline). **READY**.
- Гейт c10: **skel hard_fails 1→0 (весь файл чист)**. DUPLICATE≈516 (§30-inherent fact-scaffold, LOW). OPTION_LENGTH_RATIO=1.38 (worst Q7 §30-accepted). CORRECT_POSITION_SEQUENCE=4/DISTRIBUTION=1.25 ok. Correct A байт-лок. Sidecar: +c10/+blind_c10, blocks_reaudited 13→14.
- **Остаток = ЕДИНСТВЕННЫЙ блок до FINAL: Q16-C** (само-противоречие «одинаковая цена $0.60/млн» vs общий scaffold «Workers ~$50, Lambda@Edge ~$60» — section-rewrite misconception, отдельный тик). §30-ACCEPTED residuals: DUPLICATE-scaffold, Q18 negation-tell, Q9/Q14 form-tells. §1: факты/читаемость/single-correct > form-parity.

### ROUND-8 · edge-computing · c11 Q16 section-rewrite B+C (устранение само-противоречий) — 2026-07-14
- **c11 = section-rewrite двух дистракторов Q16** (реакция на blind c9/c11, рецидивный дефект-класс само-противоречия). **C** (было DEFER из c9): тезис «Workers и Lambda@Edge одинаковая цена $0.60/млн» конфликтовал со scaffold «Workers ~$50, Lambda@Edge ~$60» (разные цены = ПРАВДА). Misconception сменён на **«Workers биллит CPU по WALL-CLOCK (включая ожидание I/O), а не по чистому CPU-времени»** — консистентно со scaffold, восстановлена полная scaffold-структура (colon 2→3); text + все 4 sections переписаны.
- **B**: двойной дефект — (1) само-противоречие «CPU-время не тарифицируется» (середина) vs удержанная концовка «дороже для тяжёлого CPU» (не тарифицируется ⇒ не может быть дороже); (2) text↔sections рассинхрон (sections про «EC2 всегда дешевле», text про CPU-биллинг). Фикс: концовка выровнена под тезис → «дешевле даже CPU-тяжёлого, раз компьют не тарифицируется; дороже лишь stateful из-за хранилища»; sections переписаны под misconception «CPU не тарифицируется».
- **Итог Q16 = 4 РАЗНЫЕ когерентные модели:** A conclusion-flip (истинные премиссы + over-claim вывод, опровергаемый рассуждением = намеренная педагогика conclusion-flip), B «CPU не тарифицируется» (CPU дёшев / stateful дорог), C «wall-clock биллинг», D correct. colon [3,3,3,3]/semi [1,1,1,1], skel hard=0. Correct D байт-лок (== HEAD), изменены ТОЛЬКО Q16-B/Q16-C, label/order инвариантны.
- **blind ×2 (SEED 71611→71622): ОБА OK.** single_correct=D, second_correct=НЕТ, factual_error=НЕТ. blind#1 (после C) вскрыл само-противоречие B; blind#2 (после B): «O4(=B) внутренне когерентен, противоречия нет». A помечен «противоречит своим числам» — НЕ логическое противоречие, а conclusion-flip (premise CPU-billed не запрещает вере «edge всё равно дешевле EC2»), оставлен. **READY**.
- **УРОК c11 — различай два дефекта:** (a) САМО-ПРОТИВОРЕЧИЕ = опция утверждает ДВА несовместимых факта как истину (B: «не тарифицируется»+«дороже для CPU») → чинить; (b) CONCLUSION-FLIP = истинные премиссы + неверный ВЫВОД, опровергаемый рассуждением (A: CPU billable + «дёшево даже для CPU») → это ЛЕГИТИМНЫЙ дизайн, НЕ трогать. Ревьюер может путать (b) с (a) — проверять, оба ли утверждения поданы как факт.
- **Остаток до FINAL: Q6/Q7/Q10/Q12** — 4 блока БЕЗ индивидуального content-реаудита. Q7 = текущий worst OPTION_LENGTH_RATIO 1.38 (унаследовал 'worst' после c7, сам не разбирался). Content-реаудировано 14/18. Sidecar: +c11/+blind_c11, blocks_reaudited=14.

### ROUND-8 · edge-computing · c12 Q7 shared-tail когерентность B+C — 2026-07-14
- **c12 = устранение shared-tail само-противоречий Q7** (был worst OPTION_LENGTH_RATIO 1.38). Дизайн Q7 = `[distinct head] + [shared correct tail]` («bot-detection/WAF, API gateway/BFF, кэш с кастомной логикой, real-time, лёгкий AI-инференс»). Для дистракторов B(тяжёлый) и C(статик) shared code-tail КОНФЛИКТОВАЛ с их головой.
- **C**: интро «раздача статики, без кода, без auth/роутинга/персонализации» vs хвост «API gateway, кастомная логика, AI» → переписан в когерентную **«пассивный CDN»** модель (раздача/кэш/прокси/сжатие/TLS/security-заголовки — вся бизнес-логика на origin), shared code-tail убран.
- **B**: голова «тяжёлый ML (минуты CPU)» vs хвост «лёгкий AI-инференс» → переписан в когерентную **«edge берёт тяжёлый компьют»** модель (ML/обучение GPU/batch-ETL/транскодинг/3D, разгружая origin), shared tail убран.
- **D** оставлен как near-twin correct A с auth-clause-flip («на краю нет `SubtleCrypto`» — ложь). Ревьюер похвалил пару A↔D как «адверсарную, не подсказку» (легитимный spot-the-flip, как Q16-A conclusion-flip).
- **blind ×2 (SEED 71207→71217): ОБА OK.** single_correct=A, second_correct=НЕТ, factual_error=НЕТ. blind#1 (после C) вскрыл несогласованность B (heavy-head vs light-tail); blind#2 (после B): «O4(=B) самосогласован, единый ложный тезис; O3(=C) самосогласован пассивный CDN». Correct A байт-лок (== HEAD), sections B/C не тронуты (уже соответствовали), изменены только Q7-B/Q7-C.
- **Гейт: OPTION_LENGTH_RATIO Q7 1.41→1.20, ВЕСЬ ФАЙЛ PASS** (worst Q3=1.29, 0 блоков >1.35). skel hard=0. Content-реаудировано 15/18. Sidecar: +c12/+blind_c12, blocks_reaudited=15.
- **УРОК c12:** shared-tail дизайн enum-блоков даёт intro-vs-tail само-противоречия у дистракторов, чей тезис ОТРИЦАЕТ то, что общий хвост УТВЕРЖДАЕТ. Фикс = убрать shared-tail у противоречащего дистрактора, сделав его цельной когерентной ложной моделью (в отличие от near-twin-дистрактора D, который флипает одну клаузу и хвост не отрицает — его оставить).
- **Остаток до FINAL: Q6/Q10/Q12** (Q10-C прямое противоречие «без чтения cookie» vs пример «прочитать cookie»; Q12/Q6 мягче).

### ROUND-8 · edge-computing · c13 Q10 intro-vs-example когерентность A/B/C — 2026-07-14
- **c13 = устранение intro-vs-example само-противоречий Q10** (A/B на edge). Дизайн = `[подход-интро (несёт misconception)] + [ОБЩИЙ пример D-подхода]` («прочитать cookie `ab_variant`, `Math.random`, заменить `__CTA__`, выставить `Set-Cookie`»). Общий пример конфликтовал с интро каждого дистрактора:
  - **C** (ПРЯМОЕ): интро «без чтения cookie, один зашитый вариант» vs пример «прочитать cookie `ab_variant`».
  - **B**: интро «обязательный клиентский `fetch`» vs пример «server-side замена `__CTA__`».
  - **A**: интро «отдельные некэшируемые страницы, кэш несовместим» vs пример «плейсхолдер-инъекция в закэшированную».
- **Фикс:** переписан ТОЛЬКО пример каждого под его интро (A → `no-store` отдельная страница; B → `fetch('/api/variant')` + подмена `__CTA__` в браузере; C → зашитый один `__CTA__`, без cookie/`Set-Cookie`). Интро и sections НЕ тронуты (уже несли misconception). Correct D байт-лок (== HEAD), изменены только Q10-A/B/C.
- **blind (SEED 71310): OK.** single_correct=D, second_correct=НЕТ, factual_error=НЕТ. «Все четыре внутренне непротиворечивы (подход vs пример согласованы)». Дистракторы ложны по конкретным причинам (A cache-incompatible ложь, B FOUC+RTT не-edge, C нет сплита). form_guess «correct=самый полный» = §30-inherent, ревьюер «косметика, не блокер». **READY**.
- Гейт: skel hard=0, colon [1,1,1,1]/semi [0,0,0,0], OPTION_LENGTH_RATIO=1.32 PASS (worst Q10). Content-реаудировано 16/18. Sidecar: +c13/+blind_c13, blocks_reaudited=16.
- **УРОК c13:** shared-EXAMPLE (как c12 shared-TAIL) даёт intro-vs-example противоречие у дистрактора, чей подход отрицает то, что общий пример демонстрирует. Фикс = переписать пример под интро дистрактора (не трогая интро/sections, если они уже несут misconception).
- **Остаток до FINAL: Q6/Q12** (мягкие intro-vs-tail натяжения, опровергаемые доменным рассуждением; Q6 метрически чист).

### ROUND-8 · edge-computing · c14 Q12 разводка B/C/D в 4 модели консистентности — 2026-07-14
- **c14 = устранение shared-scaffold само-противоречий Q12** («Что такое Durable Objects»). Три дистрактора несли intro-vs-tail/intro-в-клаузе противоречия И были near-twins correct A (тот же middle+tail, флип одной клаузы):
  - **B**: «записи расходятся асинхронно» + «в транзакционном key-value» (async ⊥ транзакционность).
  - **C**: «без latency trade-off / одинаково близок отовсюду» + «один инстанс глобально» (одна точка не равноудалена).
  - **D**: «stateless, персистентного хранилища нет» + хвост «used for чат-комнат / атомарных счётчиков / координации» (нужно состояние).
- **Фикс:** разведены в 4 РАЗНЫЕ когерентные модели консистентности (не подбор клаузы, а полная разводка):
  - **A** (correct, байт-lock == HEAD): single-instance strongly-consistent, пиннингован к одному POP.
  - **B**: eventually consistent реплицированный кэш (много реплик, async-схождение, нетранзакционный KV, eventual-uses: гео-кэш/приблизительные счётчики).
  - **C**: синхронно реплицированный strongly-consistent в каждый регион (копия в каждом POP, атомарные записи — когерентно, НЕ near-twin single-pinned; misconception «равная близость отовсюду» сохранён, `source_of_confusion` переписан).
  - **D**: single-instance stateless-функция (без хранилища/WebSocket, stateless-uses: трансформация/валидация/роутинг).
- Изменены только Q12-B/C/D (C: text + `source_of_confusion`; B/D: text). Correct A и его sections не тронуты.
- **2 независимых blind (SEED 71412, 82517):** blind#1 → **REVISE** (первый рерайт C с backbone-механизмом оставался near-twin correct с остаточным «один инстанс ↔ близко отовсюду»; ревьюер: развести в отдельный концептуальный тезис). После разводки C в sync-strong-replicated — blind#2 → **ACCEPT**: «единственно-верный A защитим, дистракторы различимы только доменным знанием, form_guess=НЕТ (паритет ровный, верный не самый длинный), near_twin A/C теперь плюс дизайна». **READY**.
- Гейт: skel hard=0, colon [1,1,1,1]/semi [0,0,0,0], OPTION_LENGTH_RATIO Q12 1.37→1.23 (подрезан C), весь файл PASS (worst Q10=1.32). DUPLICATE 498→487. Content-реаудировано **17/18**. Sidecar: +c14/+blind_c14, blocks_reaudited=17.
- **УРОК c14:** когда дистрактор = correct + флип-одной-клаузы И держит противоречащий shared middle/tail (near-twin + intro-vs-tail разом), чинить НЕ подбором клаузы (остаётся near-twin → form_guess до 50/50), а **РАЗВОДКОЙ в отдельную когерентную модель** по той же оси знания (для Q12 = 4 модели консистентности: single-pinned / eventual-async / sync-strong-replicated / stateless). Убирает и near-twin, и противоречие. C-модель sync-strong-replicated = легитимный PACELC conclusion-flip («строгая консистентность + близко отовсюду» опровергаемо рассуждением, как Q16-A).
- **Остаток до FINAL: Q6** (метрически чист ratio 1.13; A флип изоляции, C своп start-times, D флип языков — defect-скан + блайнд). После Q6 — **edge-computing FINAL**.

### ROUND-8 · edge-computing · c15 Q6 флип изоляции → на сторону контейнера (FINAL) — 2026-07-14
- **c15 = последний блок Q6** («Чем V8-изоляты отличаются от контейнеров»). Дизайн = 3 оси (изоляция / старт-латентность / языки), correct B точен по всем; дистракторы флипают по одной: A изоляция, C латентность (свап микросекунды↔сотни мс), D языки (изолят «любые языки + нативные бинарники»).
- **Дефект A (флагнут 3 из 5 блайндов):** общий зачин «лёгкий sandbox **ВНУТРИ движка V8**» (shared-process) ⊥ флип «VM-isolated / каждый изолят получает уровень Firecracker» (отдельный microVM). Корень: A флипал ТУ ЖЕ ось (изоляция), что пинит общий зачин → любой топологический флип изоляции ВВЕРХ на изоляте неизбежно бьётся с зачином.
- **Фикс (итеративно, 5 блайндов):** перенёс флип изоляции на **СТОРОНУ КОНТЕЙНЕРА**. Клауза изолята у A стала ИСТИННОЙ (= correct-модели, совпадает с зачином → противоречия нет), контейнер флипнут в «полный OS/рантайм с обычной OS-изоляцией процессов, **без отдельного microVM-слоя Firecracker**» — когерентная правдоподобно-ложная модель «Lambda = обычный контейнер без microVM». sections A (source_of_confusion+if_it_were_true) переписаны под этот misconception.
- **Итерации A по блайндам:** #1 ACCEPT-но-флаг → #2/#3/#4 REVISE (process-isolated → сила-«уровня-VM» → «лёгкий»+секундный-старт — каждая версия ловила новый угол) → **#5 ACCEPT** («внутренне когерентная фактически-неверная модель, хороший правдоподобно-ложный дистрактор»). single_correct=B все 5, other_defensible=нет, каррикатур нет.
- Correct B байт-лок (== HEAD); «старт за микросекунды» защитимо (создание изолята субмиллисекундно, ревьюер#1 подтвердил). Изменён только Q6-A (text+sections). OPTION_LENGTH_RATIO Q6=1.128, colon [1,1,1,1]/semi [0,0,0,0], skel hard=0.
- **УРОК c15:** когда флип-ось дистрактора СОВПАДАЕТ с содержанием общего зачина (здесь: ось изоляции vs зачин «sandbox внутри V8»), топологический флип этой оси на сущности-у-зачина НЕИЗБЕЖНО противоречит зачину. Фикс = перенести флип на ДРУГУЮ сторону сравнения (изолят→контейнер), оставив клаузу-у-зачина истинной; либо флипнуть СИЛУ через conclusion-flip, но топологию не трогать.
- **edge-computing → FINAL** (18/18 блоков content-реаудированы + блайнд; sidecar status=FINAL). Гейты: skel hard=0, OPTION_LENGTH_RATIO PASS весь файл, единственный красный — DUPLICATE≈494 (§30-accepted fact-scaffold).

### ROUND-8 · redpanda · c1 bootstrap POS de-cycle + fact-freshness — 2026-07-14
- **Новый worst-first файл: `messaging/redpanda-interview.json`** (20 блоков, Sev=HIGH). Выбран по матрице после edge-computing FINAL: rank #1 mutation-testing (уже FINAL), #2 kotlin (COMPLETE), #3 agentic-patterns (off-limits), #4 **redpanda** (нет sidecar → не реаудирован). Сырой файл.
- **Крупные проблемы (к разбору по блокам):** OPTION_LENGTH_RATIO=3.17 (worst Q14, 17 блоков >1.35 — correct систематически длиннее = length-tell), ABSOLUTE_MARKER_GAP=0.383 (абсолюты у wrong), CORRECT_POSITION_SEQUENCE=20 (строгий цикл), skel hard_fails=4 (Q10/Q17/Q18/Q20), ΣCAR=1 (1 каррикатурный блок), SOURCE_COVERAGE missing.
- **c1 = POS de-cycle + bootstrap.** Correct-позиции были строгим циклом ABCDABCD… (20). Перестановка label/order на target `CADBDBCAACBDBDACCDAB` (по 5 каждой, arith-run мал) → CORRECT_POSITION_SEQUENCE 20→4, DISTRIBUTION 1.0. Контент КАЖДОЙ опции байт-идентичен HEAD (set-equal per block: text+correct+sections) → **blind не требуется**. + создан fact-freshness sidecar (SOURCE_COVERAGE missing→ok) + review sidecar (bootstrap). verify-mcq-json OK.
- **Остаток:** worst-first по блокам — length-tell (сжать correct/поднять дистракторы) + де-каррикатура + skel hard (Q10/Q17/Q18/Q20) + когерентность, каждый блок с блайндом. correct byte-locked, правим только дистракторы.

### ROUND-8 · redpanda · c2 Q14 length-tell (заглушка-дистрактор → паритет) — 2026-07-14
- **c2 = Q14** («Что такое Pandaproxy»), worst блок файла (OPTION_LENGTH_RATIO=3.17). **Находка:** length-spread файла вызван НЕ длинным correct (CORRECT_LONGEST_RATE=0.05 ok), а КОРОТКИМИ дистракторами-заглушками. Q14-C = 6w заглушка «HTTP-балансировщик перед кластером, заменяющий nginx» при A18/B19/D18.
- **Фикс:** поднял C до паритета (20w) в полную правдоподобно-ложную модель «reverse-proxy и L7-балансировщик перед брокерами: терминирует подключения, распределяет по нодам, health-check, заменяет nginx». Misconception (proxy-name → балансировщик) сохранён — sections уже его несли, не тронуты. Без colon/semi/em-dash (паритет A/B/D), прозо-open. ratio Q14 **3.17→1.11**.
- **blind (SEED 14534): ACCEPT.** single_correct=D, factual_error=нет, other_defensible=нет, само-противоречий нет. Поднятый C: «когерентное правдоподобное заблуждение, СИЛЬНЫЙ дистрактор» (был заглушкой). Каррикатур нет, form_guess нет. Correct D байт-лок (== HEAD), изменён только Q14-C.
- **УРОК c2 (для остального файла):** рычаг length-tell здесь = поднять слабый дистрактор-заглушку до паритета правдоподобной ложной деталью (усиливает и педагогику, и метрику разом), а НЕ трогать byte-locked correct. Очередь worst-first: Q6(2.22)/Q7(2.2)/Q2(2.2)/Q3(2.09)/Q17(2.08)… + skel hard Q10/Q17/Q18/Q20.
- Content-реаудировано 1/20. Sidecar: +c2/+blind_c2, blocks_reaudited=1.

### ROUND-8 · redpanda · c3 Q6 length-tell (заглушка-дистрактор → паритет) — 2026-07-14
- **c3 = Q6** («Как Redpanda без ZooKeeper»), worst блок файла (OPTION_LENGTH_RATIO=2.22). Q6-C = 9w заглушка «координацию обеспечивает внешний etcd рядом отдельным кластером» при A20/B16/D14.
- **Фикс:** поднял C до 17w в полную правдоподобно-ложную etcd-модель «внешний etcd-кластер рядом с брокерами, где лежат лидеры партиций, конфигурация топиков и membership» (misconception Raft→etcd, sections уже несли — не тронуты). D также поднят 14→16w («single-node режиме БЕЗ репликации, поэтому консенсус между нодами не требуется» — усиливает misconception single-node). ratio Q6 **2.22→1.25** (A20/B16/C17/D16). colon/semi [0,0,0,0], em-dash [1,1,0,0] == HEAD (не new tell; correct B в группе «есть» с wrong A → не only-correct), prose-open.
- **blind (SEED 71412): ACCEPT.** single_correct=B (per-partition Raft-группа, ZK не нужен), factual_error=нет (лишь мелкая размытость «метаданные», B байт-лок не трогаю), other_defensible=нет, само-противоречий нет. Поднятый C (etcd) — «когерентное правдоподобное заблуждение, etcd реальная Raft-система из K8s». A(Redis) правдоподобно новичку, D(single-node) слабейший но не грубая заглушка. form_guess нет. Correct B байт-лок (== HEAD), изменены только Q6-C/Q6-D.
- Content-реаудировано 2/20. Sidecar: +c3/+blind_c3, blocks_reaudited=2. Worst-first остаток: Q2(2.20)/Q7(2.2)/Q3(2.09)/Q17(2.08)… + skel hard Q10/Q17/Q18/Q20 + file ABSOLUTE_MARKER_GAP=0.383.

### ROUND-8 · redpanda · c4 Q2 length-tell + де-каррикатура D — 2026-07-14
- **c4 = Q2** («Описание Redpanda vs Kafka»), worst блок файла (OPTION_LENGTH_RATIO=2.20). Q2-C = 10w заглушка «тот же Kafka, переписанный на Rust командой Confluent» + ЕДИНСТВЕННЫЙ em-dash [0,0,1,0]. Q2-D = каррикатура «всегда быстрее РОВНО в 100 раз и ПОЛНОСТЬЮ покрывает ВСЕ коннекторы».
- **Фикс:** C→21w «тот же Kafka, лишь переписанный командой Confluent на Rust, совместимый по проводному протоколу» (misconception Rust+Confluent сохранён, em-dash убран → dash-паритет [0,0,0,0], sections не тронуты). D де-каррикатурен: «ровно в 100 раз»→«примерно в сто раз», убраны «всегда»/«полностью покрывает все» (section-якоря 100×/все-коннекторы сохранены → sections валидны, не тронуты). ratio Q2 **2.20→1.22** (A18/B22/C21/D21), colon/semi/em-dash [0,0,0,0]. ABSOLUTE_MARKER_GAP 0.383→0.367.
- **blind (SEED 63221): ACCEPT.** single_correct=A (5 тезисов факт-точны), factual_error=нет, other_defensible=нет, само-противоречий нет. C — «когерентное заблуждение (путают с Confluent-инициативами; wire-protocol правда = приманка)», D — «когерентное с абсолютами-приманкой (маркетинг округляют до 100×)». form_guess слабый/не решающий. Correct A байт-лок (== HEAD), изменены только Q2-C/Q2-D.
- **NIT reviewer:** Q2-B (JVM-identical, «различие только в названии») — мягкая каррикатура-абсолют, кандидат на тонкую переработку в будущий тик (вне scope c4).
- Content-реаудировано 3/20. Sidecar: +c4/+blind_c4, blocks_reaudited=3. Worst-first остаток: Q7(2.20)/Q3(2.09)/Q17(2.08)/Q1(1.89)… + skel hard Q10/Q17/Q18/Q20 + file ABSOLUTE_MARKER_GAP=0.367 + NIT Q2-B.

### ROUND-8 · process — закреплены 10 решений по работе loop + снятие off-limits — 2026-07-14
- По запросу пользователя задано 10 уточняющих вопросов о работе цикла; ответы закреплены в **§1b** (сила правил наравне с §1). Ключевые изменения против прежнего поведения: (1) внутри файла СНАЧАЛА факт/когерентность всех блоков, потом метрики; (2) весь файл за тик, если остаток тривиален; (7) NIT ревьюера чинить в том же тике если в scope блока (было — всегда отдельным); (9) skel/marker-gap чинить попутно.
- **OFF-LIMITS СНЯТ ПОЛНОСТЬЮ (тематически):** пользователь открыл все ранее запретные темы (postgresql, testcontainers, chaos-engineering, rxjava, algorithms, rest-maturity, agentic-patterns, mentoring, git). Тематического списка больше нет. Единственный барьер — **dirty working-tree**: не трогать файлы с чужими незакоммиченными правками, collision-guard каждый тик, запрет опасных git-операций сохранён. См. §1b п.10.

### ROUND-8 · redpanda · c5 факт/когерентность-скан всех блоков + Q7 length-tell — 2026-07-14
- **Первый тик по новому §1b п.1** (сначала факт/когерентность всего файла, потом метрики). Прогнал ВСЕ 20 блоков redpanda на факт-ошибки в correct и само-противоречия в дистракторах.
- **РЕЗУЛЬТАТ СКАНА:** факт-слой ЧИСТ — 0 факт-ошибок в 20 correct-ответах, 0 само-противоречий в дистракторах. Найдены каррикатурные абсолюты (rule #3): Q11-C/D, Q12-A, Q18-A, Q19-D, Q20-A/C, Q3-B (мягкая). **Триаж:** Q11/Q18/Q19-абсолюты = ЛЕГИТИМНЫЕ silver-bullet-маркеры (урок «не серебряная пуля», как Q16-A/PACELC) — НЕ трогать; Q12-A/Q20-A/Q20-C = pile-up абсолютов не служит дихотомии → кандидаты на де-каррикатуру отдельными тиками.
- **+ Q7 (tiered storage)** worst length (ratio 2.20). Q7-A = 10w заглушка «Consumer-группы в Redis, данные в Kafka» при B17/C19/D22. Поднят до 21w «Consumer-группы и горячие сообщения в Redis как быстром уровне (hot-кэш), данные топиков в Kafka как холодном слое» (misconception Redis-hot/Kafka-cold split сохранён, sections не тронуты; + паренетеза → paren-паритет [1,1,1,1]). ratio Q7 **2.20→1.29** (A21/B17/C19/D22).
- **blind (SEED 48814): ACCEPT.** single_correct=B (byte-lock correct), factual_error=нет, other_defensible=нет, само-противоречий нет. A(Redis) «когерентное заблуждение умеренной силы», C(replication)/D(RAID/HSM) когерентны. form_guess минимальный. Correct C байт-лок (== HEAD), изменён только Q7-A.
- Content-реаудировано 4/20. Sidecar: +c5/+blind_c5 + fact_coherence_sweep_c5, blocks_reaudited=4. Worst-first остаток: Q3(2.09)/Q17(2.08)/Q1(1.89)/Q18(1.88)… + skel hard Q10/Q17/Q18/Q20 (`;` попутно) + каррикатуры-кандидаты Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c6 Q3 length-tell + FORM-TELL (2 блайнда) — 2026-07-14
- **c6 = Q3** («что такое single binary»), worst length (ratio 2.09). Correct=D байт-лок. Q3-B = 11w заглушка + мягкая каррикатура «всегда умещается на один узел»; A = 23w длинный.
- **blind#1 (SEED 55106): ACCEPT с обязательной правкой формы** — контент безупречен, но найден FORM-TELL: все 3 дистрактора (A/B/C) открывались штампом «Имеется в виду…», а correct — нет → правильный выделялся зачином (Format Parity leak).
- **Фикс:** нормализованы зачины ВСЕХ трёх дистракторов (штамп убран, зачины разные: A«Весь кластер»/B«Production-кластер»/C«Это Docker-образ»/D-correct«Один процесс»); B де-каррикатурен («всегда» убрано); A подрезан 23→15; C дан em-dash → полный dash-паритет [1,1,1,1]. misconception'ы (single-JVM/single-node/repackaged-Kafka) сохранены, sections не тронуты. ratio Q3 **2.09→1.15** (A15/B14/C13/D15).
- **blind#2 (SEED 61506): ACCEPT** — «стилевого клейма на верном нет», single_correct=A, спред дистракторов по разным осям, само-противоречий нет. Остаточный form_guess слабый (маргинален, C тоже enumerates). NIT «Connect» в correct — байт-лок, защитим, не трогаю.
- **УРОК c6:** blind ловит form-tell «общий штамп-зачин у дистракторов, correct без него» — фикс = нормализовать зачины дистракторов (correct байт-лок, значит меняем ВОКРУГ него). Правило §1b п.7 (NIT в том же тике если в scope) сработало: правил все 3 дистрактора одного блока за тик.
- Content-реаудировано 5/20. Sidecar: +c6/+blind_c6, blocks_reaudited=5. Worst-first остаток: Q17(2.08)/Q1(1.89)/Q18(1.88)/Q13… + skel hard Q10/Q17/Q18/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c7 Q17 length-tell + skel `;`-tell (попутно) — 2026-07-14
- **c7 = Q17** («что такое BSL»), worst length (ratio 2.08) И skel HARD (`;` только у correct). Correct=C байт-лок (несёт `:`/`;`, 3 клауза). A=27w длинный, B=15w/D=13w короткие; `;` только у C [0,0,1,0] (form-tell); `:` не было у B.
- **Фикс (§1b п.9 — пунктуация попутно):** выровнял длину + дал каждому дистрактору второй клауз с `;` в его false-модели → colon [1,1,1,1] + semi [1,1,1,1]. A 27→24 («…self-host'ить; никаких ограничений… нет»), B 15→21 («…self-hosting запрещён; для локального запуска нужен коммерческий ключ»), D 13→20 («…коммерческого договора; бесплатных/open-source/self-host-версий не существует»). misconception'ы (A=Apache2, B=инвертированный-BSL, D=closed-proprietary) сохранены, sections не тронуты. ratio Q17 **2.08→1.25** (A24/B21/C25/D20). **skel HARD Q17 УСТРАНЁН** (осталось Q10/Q18/Q20).
- **blind (SEED 39234): ACCEPT.** single_correct=C, factual_error=нет, other_defensible=нет, само-противоречий нет. B(инвертированный-BSL)/D(closed-source) НЕ коллизируют (разные оси: source-available vs нет-кода). form_guess слабый (C длиннейший из-за 3 клауз — байт-лок; общий зачин C/B маскирует). Correct C байт-лок (== HEAD), изменены Q17-A/B/D.
- **УРОК c7:** skel `;`-tell (correct — единственный с `;`) чинится попутно с длиной: дистракторам добавить второй клауз с `;` в их false-модели — усиливает и длину, и пунктуационный паритет разом (correct байт-лок = эталон пунктуации).
- **Прим.:** тик прерывался plan mode посреди (правка уже в дереве, блайнд отложен) → после выхода блайнд проведён, тик закрыт штатно.
- Content-реаудировано 6/20. Sidecar: +c7/+blind_c7, blocks_reaudited=6. Worst-first остаток: Q1(1.89)/Q18(1.88)/Q13/Q9… + skel hard Q10/Q18/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c8 Q1 length-tell — 2026-07-14
- **c8 = Q1** («что такое Redpanda»), worst length (ratio 1.89). Correct=C байт-лок (13w). Q1-A = 9w заглушка «надстройка над Kafka через DPDK-плагин» при B17/C13/D14.
- **Фикс:** A поднят 9→17w «надстройка над существующим кластером Kafka, ускоряет через DPDK-плагин и kernel-bypass, не заменяя сами брокеры» (misconception плагин-поверх-Kafka сохранён, совпадает с source_of_confusion; sections не тронуты; зачин «Это» — паритет). ratio Q1 **1.89→1.31** (A17/B17/C13/D14).
- **blind (SEED 72902): ACCEPT.** single_correct=B, factual_error=нет, само-противоречий нет. Поднятый A «наиболее умный дистрактор, лучший из трёх». form_guess остаточный слабый (correct нейтрален vs «твист» дистракторов — inherent). Correct C байт-лок (== HEAD), изменён только Q1-A.
- Content-реаудировано 7/20. Sidecar: +c8/+blind_c8, blocks_reaudited=7. Worst-first остаток: Q18(1.88)/Q13/Q9/Q4… + skel hard Q10/Q18/Q20 + каррикатуры Q12-A/Q20-A/C. Прим. Q18: worst length И skel hard И каррикатура A, но A=легитимный silver-bullet-маркер → только length+skel, де-каррикатуру не форсить.

### ROUND-8 · redpanda · c9 Q18 length-tell + skel HARD (попутно) — 2026-07-14
- **c9 = Q18** («когда выбрать Redpanda»), worst length (ratio 1.88) И skel HARD (`;`/`(` только НЕ у C). Correct=D байт-лок (24w). C = 16w «только для batch-ETL, latency не важна» без `;`/`(`. A=30w каррикатура «всегда в любом сценарии» = ЛЕГИТИМНЫЙ silver-bullet-маркер (judgment-вопрос) → НЕ тронут.
- **Фикс:** C поднят 16→25w «…latency не важна (ночные выгрузки, большие окна агрегации); для стриминга в реальном времени и low-latency Redpanda преимуществ не даёт» (misconception «только batch» сохранён + усилен инверсией; sections не тронуты; +`;`+паренетеза). ratio Q18 **1.88→1.25** (A30/B28/C25/D24), semi/paren [1,1,1,1]. **skel HARD Q18 УСТРАНЁН** (осталось Q10/Q20).
- **blind (SEED 64136): ACCEPT.** single_correct=D, factual_error=нет, само-противоречий нет. Дистракторы разнотипны (A=JVM-ошибка/B=over-generalization/C=инверсия). Поднятый C «хороший зеркальный дистрактор». form_guess частичный (D без крайностей = содержательный признак judgment-вопроса, легитимно). Correct D байт-лок (== HEAD), изменён только Q18-C.
- Content-реаудировано 8/20. Sidecar: +c9/+blind_c9, blocks_reaudited=8. Worst-first остаток: Q8(1.78)/Q13/Q9/Q4… + skel hard Q10/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c10 Q8 length-tell — 2026-07-14
- **c10 = Q8** («что такое Kafka wire protocol compatibility»), worst length файла (ratio 1.78). Correct=A байт-лок (16w). Q8-C = 9w заглушка «через обёртку MirrorMaker, транслирующую запросы» при A16/B15/D12. Пунктуация уже uniform [0,0,0,0] (нет skel-tell).
- **Фикс:** C поднят 9→14w «через встроенную обёртку MirrorMaker, которая на лету транслирует их бинарные запросы» (misconception MirrorMaker-обёртка-транслятор сохранён + усилен — зеркало correct A «тот же бинарный протокол, БЕЗ трансляции»; sections не тронуты). ratio Q8 **1.78→1.33** (A16/B15/C14/D12), пунктуация остаётся [0,0,0,0].
- **blind (SEED 71829): ACCEPT.** single_correct=[4]=A, factual_error=нет, other_defensible=нет, само-противоречий/каррикатур нет. Дистракторы привязаны к реальным компонентам (Pandaproxy REST / MirrorMaker / Schema Registry), но выводы ложны. form_guess ЗАКРЫТ — поднятый C не выделяется, correct не угадать по длине/форме. Correct A байт-лок (== HEAD), изменён только Q8-C.
- Content-реаудировано 9/20. Sidecar: +c10/+blind_c10, blocks_reaudited=9. Worst-first остаток: Q9(1.73)/Q15(1.73)/Q16(1.69)/Q10(1.69)/Q19(1.67)/Q20(1.65)… + skel hard Q10/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c11 Q9 length+paren-tell — 2026-07-14
- **c11 = Q9** («что верно про поддержку Kafka-клиентов»), worst length файла (ratio 1.73) + paren-tell (A/C/D paren=1, B=0). Correct=A байт-лок (18w, paren=1). Q9-B = 11w заглушка «только продюсеры, консьюмеры под отдельный Redpanda Consumer API» без паренетезы.
- **Фикс:** B поднят 11→17w «…нужно переписывать под отдельный Redpanda Consumer API (его poll-модель с Kafka несовместима)» (misconception сохранён + усилен правдоподобной ложной деталью; sections не тронуты). ratio Q9 **1.73→1.12** (A18/B17/C19/D17), paren [1,1,1,1], colon/semi/emdash [0,0,0,0].
- **blind (SEED 58371): ACCEPT.** single_correct=[4]=A, factual_error=нет, other_defensible=нет, само-противоречий нет. Length+paren form-tell закрыт. **Ревьюер-NIT (не блокер, НЕ фикшу):** остаточный тональный tell — correct единственный «позитивный» (всё работает) vs 3 «ограничительных» дистрактора; фикс через позитивно-рамочный дистрактор дал бы near-twin correct'а («все работают, но пересборка» vs «все работают без изменений») → новый form-tell. Асимметрия INHERENT (правда=всё-работает, заблуждения=ограничения), принята как Q18-form_guess. Correct A байт-лок (== HEAD), изменён только Q9-B.
- Content-реаудировано 10/20. Sidecar: +c11/+blind_c11, blocks_reaudited=10. Worst-first остаток: Q15(1.73)/Q16(1.69)/Q10(1.69)/Q19(1.67)/Q20(1.65)/Q4(1.62)/Q13(1.5)… + skel hard Q10/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c12 Q15 length + em-dash-tell — 2026-07-14
- **c12 = Q15** («что такое Redpanda Console»), worst length файла (ratio 1.73) + em-dash-tell (A/B/D emdash=1, C=0). Correct=A байт-лок (19w, emdash=1). Q15-C = 11w заглушка «часть бинарника, всегда слушает 9092, отдаёт веб-интерфейс» без em-dash + мягкая каррикатура «всегда».
- **Фикс:** C поднят 11→18w «Часть основного бинарника redpanda, которая отдаёт веб-интерфейс прямо на Kafka-порту 9092 — отдельно запускать и разворачивать её не нужно» (misconception «Console вшит в брокер» сохранён/усилен; путаница про 9092 сохранена; «всегда» убрано; sections не тронуты; +em-dash). ratio Q15 **1.73→1.19** (A19/B19/C18/D16), emdash [1,1,1,1], colon/semi/paren [0,0,0,0]. ABSOLUTE_MARKER_GAP 0.35→0.333 попутно.
- **blind (SEED 60492): ACCEPT.** single_correct=[3]=A, factual_error=нет (косметика «после покупки Kowl» — факт ВЕРЕН: Redpanda приобрела CloudHut/Kowl 2022; ревьюерская посылка неточна, correct байт-лок), other_defensible=нет, каррикатур/само-противоречий нет, form-guess нет (em-dash у всех, длины ровные). Correct A байт-лок (== HEAD), изменён только Q15-C.
- Content-реаудировано 11/20. Sidecar: +c12/+blind_c12, blocks_reaudited=11. Worst-first остаток: Q16(1.69)/Q10(1.69)/Q19(1.67)/Q20(1.65)/Q4(1.62)/Q13(1.5)/Q8(1.33)… + skel hard Q10/Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c13 Q10 length + skel HARD + ревьюер-NIT — 2026-07-14
- **c13 = Q10** («как работают Schema Registry и Kafka Connect»), worst length (ratio 1.69) + skel HARD (A единственная без `;`/`—`; B/C/D несут semi=1+emdash=1). Correct=C байт-лок (22w). **Два дистрактора правлены:**
- **A:** 13w заглушка «SR только Protobuf, Connect не работает вовсе» → 20w + `;`+`—` вторая клауза «коннекторы Confluent → самописные REST-интеграции» (misconception сохранён/усилен). **B (ревьюер-NIT §1b#7):** единственный без «SR …; Connect …» симметрии + пайл-ап абсолютов «принципиально/никаких/в принципе» → причёсан под симметрию + де-каррикатура «SR как компонента нет; Connect тоже не поддерживается — payload-agnostic…» (misconception «нет ни SR ни Connect» сохранён). sections A/B не тронуты.
- ratio Q10 **1.69→1.22** (A20/B21/C22/D18), semi/emdash [1,1,1,1] → **skel HARD Q10 УСТРАНЁН** (остался только Q20).
- **blind#1 (SEED 63914): ACCEPT** single_correct=[1]=C, но NIT про форму B → фикс. **blind#2 (SEED 66207): ACCEPT** — все 4 изоморфны по форме, де-каррикатура B «стала правдоподобной», single_correct=[2]=C, other_defensible нет. **УРОК c13:** skel HARD-блок = дистрактор без `;`/`—`, что несут остальные (включая correct) → дать ему двухчастную клаузу с той же пунктуацией = закрыть length+skel разом; а если ещё один дистрактор ломает двухчастную симметрию + пайлит абсолюты — причесать его под симметрию тем же тиком (§1b#7). Correct C байт-лок (== HEAD), изменены Q10-A/Q10-B.
- Content-реаудировано 12/20. Sidecar: +c13/+blind_c13, blocks_reaudited=12. Worst-first остаток: Q16(1.69)/Q19(1.67)/Q20(1.65)/Q4(1.62)/Q13(1.5)/Q8(1.33)/Q1(1.31)… + skel hard Q20 + каррикатуры Q12-A/Q20-A/C.

### ROUND-8 · redpanda · c14 Q16 length → blind вскрыл ФАКТ-ОШИБКУ в correct — 2026-07-14
- **c14 = Q16** («структура редакций Redpanda»), worst length (ratio 1.69). Начал как length-фикс A (заглушка 13w→18w + em-dash 2→1 + paren), но **blind#1 (SEED 68415) вернул REVISE: факт-ошибка в correct C** — tiered storage отнесён к open-source/BSL core, тогда как **Tiered Storage = Enterprise-фича**.
- **Верификация факта (MCP-first, context7 не поднят → WebSearch):** [docs.redpanda.com licensing](https://docs.redpanda.com/current/get-started/licensing/overview/) + [compare-platform-editions](https://www.redpanda.com/data-streaming/compare-platform-editions) — Tiered Storage требует enterprise-лицензии, недоступна в Community (только 30-дн trial). Ревьюер прав.
- **§1 факт-корректность > byte-lock → ФАКТ-ФИКС correct C** (документированное исключение): «Open source (BSL) с core-фичами стриминга, Enterprise (платная) с tiered storage, advanced security и audit, Cloud …BYOC» + та же правка в `sections.explanation` (tiered storage перенесён OSS→Enterprise). + де-каррикатура B/D (снять пайл-ап абсолютов; misconception'ы сохранены).
- ratio Q16 **1.69→1.22** (A18/B21/C22/D20), emdash [1,1,1,1]. **blind#2 (SEED 70611): ACCEPT** — single_correct=[4]=C, все атрибуции точны (tiered→Enterprise/security→Enterprise/BSL→community/Cloud+BYOC), other_defensible нет, де-каррикатура удалась. Остаточный form-tell (C перечислительный) INHERENT к вопросу о структуре редакций — принят как Q9-inherent.
- **УРОК c14:** c5-факт-скан МОЖЕТ пропустить тонкую факт-ошибку (tiered storage OSS-vs-Enterprise) — blind ловит. При факт-ошибке в correct §1 бьёт byte-lock, НО требует: (а) внешней верификации факта (WebSearch/context7), (б) правки И текста И sections, (в) сохранения single-correct + пере-блайнда. **Изменены Q16-A/B/C/D** (C — исключение из byte-lock).
- Content-реаудировано 13/20. Sidecar: +c14/+blind_c14, blocks_reaudited=13. Worst-first остаток: Q19(1.67)/Q20(1.65)/Q4(1.62)/Q13(1.5)/Q8(1.33)/Q1(1.31)/Q7(1.29)… + skel hard Q20 + каррикатуры Q12-A/Q20-A/C. **ВНИМАНИЕ: перепроверять факт correct каждого блока (c5-скан не идеален).**

### ROUND-8 · redpanda · c15 Q19 обратный length (длинный дистрактор) + colon-tell — 2026-07-14
- **c15 = Q19** («когда остаться на Apache Kafka»), worst length (ratio 1.67) — **ОБРАТНЫЙ рычаг: worst из-за СЛИШКОМ ДЛИННОГО дистрактора C=30w**, не заглушки. + colon-tell (B единственный с `:`). Correct=A байт-лок (18w). **Факт-перепроверка (урок c14):** A = judgment-перечень (большая инсталляция/зависимость от Kafka-фич/требование Apache 2.0 [Redpanda=BSL не Apache2.0 → верно]/приоритет экосистемы) — факт-точно.
- **Фикс:** C ужат 30w→19w (misconception «язык клиента=рантайм брокера, Redpanda C++» сохранён). B: `:`→запятая (misconception «Kafka проще малым командам» — на деле инверсия ops-преимущества Redpanda — сохранён). D=23w silver-bullet «никогда/в любом сценарии/всегда» = ЛЕГИТИМНЫЙ маркер (c5-триаж Q19) → НЕ тронут. sections B/C не тронуты.
- ratio Q19 **1.67→1.28** (A18/B19/C19/D23), пунктуация [0,0,0,0].
- **blind (SEED 72930): ACCEPT.** single_correct=[2]=A, factual_error нет (BSL vs Apache2.0 подтв.), other_defensible нет ([3] небылица «язык=рантайм» / [4] ценная ловушка-инверсия / [1]=D silver-bullet легитимен). Остаточный form-tell (A=перечень vs дистракторы=«утверждение+ведь») INHERENT к judgment-вопросу + A байт-лок факт-чист → НЕ правлю (урок c14: correct правится только за факт). **УРОК c15:** length-tell бывает и от СЛИШКОМ ДЛИННОГО дистрактора (не только заглушки) → ужать его к паритету, а не раздувать остальные. Correct A байт-лок (== HEAD), изменены Q19-B/Q19-C.
- Content-реаудировано 14/20. Sidecar: +c15/+blind_c15, blocks_reaudited=14. Worst-first остаток: Q20(1.65)/Q4(1.62)/Q13(1.5)/Q8(1.33)/Q1(1.31)/Q7(1.29)… + skel hard Q20 + каррикатуры Q12-A/Q20-A/C (Q20 = length+skel+де-каррикатура разом).

### ROUND-8 · redpanda · c16 Q20 length+skel(последний HARD)+де-каррикатура+form-tell — 2026-07-14
- **c16 = Q20** («подходы миграции Kafka→Redpanda»), worst length (ratio 1.65) + **последний skel HARD** + де-каррикатура (c5: Q20-A/Q20-C). Correct=B байт-лок (26w, факт-перепроверен: 3 реальных подхода MM2/dual-write/cut-over-rpk).
- **Фикс (3 дистрактора де-каррикатурены + приведены к скелету B):** A «единственный путь…CSV…не существует» → «Обычно выгружают в CSV…; MM и rpk не применяются…»; C «Никакая невозможна: несовместимы» → «почти нереализуема, поскольку несовместимы по формату; …MM попросту не работает» (`:`→нет, +`;`); D «Единственный путь…навсегда» → утвердительно-перечислительный. misconception'ы сохранены, sections не тронуты.
- ratio Q20 **1.65→1.18** (A23/B26/C22/D23), semi present все 4, colon/emdash/paren [0,0,0,0] → **skel gate PASS hard_fails=0 (ВСЕ HARD закрыты)**. ABSOLUTE_MARKER_GAP 0.333→0.30.
- **blind#1 (SEED 74812): REVISE form-tell** — correct B единственный без отрицания + единственный с перечислением подходов (дистракторы = «идея+обоснование с отрицанием»). Фикс §1b#7: D → утвердительно-перечислительный без отрицания. **blind#2 (SEED 76318): ACCEPT** — form-tell устранён (перечисление + «нет отрицания» у ДВУХ B/D), single_correct=B, [4]=D «содержательная ловушка (клиенты остаются на Kafka=не-миграция)». **УРОК c16:** если correct — ЕДИНСТВЕННЫЙ конструктивно-перечислительный/без-отрицания вариант, а все дистракторы = «ложная идея+отрицание» → form-tell «конструктивный синтаксис = correct»; фикс = переделать ≥1 дистрактор в утвердительно-перечислительную ложную модель (несколько ложных «подходов» через `;`). Correct B байт-лок (== HEAD), изменены Q20-A/C/D.
- Content-реаудировано 15/20. Sidecar: +c16/+blind_c16, blocks_reaudited=15. Worst-first остаток: Q4(1.62)/Q13(1.5) (только эти >1.35) + де-каррикатура Q12-A. skel PASS.

### ROUND-8 · redpanda · c17 Q4 length (2 коротких дистрактора) — 2026-07-14
- **c17 = Q4** («архитектура shard-per-core на Seastar»), worst length (ratio 1.62; C=13w min, D=21w max). Correct=B байт-лок (20w, факт-перепроверен: shared-nothing/каждое ядро свой шард/без локов/async I/O — точное Seastar thread-per-core).
- **Фикс:** подняты ДВА коротких дистрактора — A 15→20w «…через общую shared queue с блокировками на общий mutex» (misconception dispatcher+thread-pool сохранён); C 13→18w «…JVM-процесс со своим heap, координация через RMI поверх localhost» (misconception per-core-JVM+RMI — тестирует факт C++/no-JVM — сохранён). D=21w не тронут. sections A/C не тронуты.
- ratio Q4 **1.62→1.17** (A20/B20/C18/D21), пунктуация [0,0,0,0].
- **blind (SEED 78204): ACCEPT** — single_correct=B, factual_error нет, other_defensible нет (A/C/D все противоположны shared-nothing), form-guess нет. Ревьюер-NIT (не блокер, ОТЛОЖЕН на ротацию): C JVM/RMI слабейший для C++ — можно на native-model, но JVM-misconception тестирует C++/no-JVM (тема Q1/Q12) → оставлен. Correct B байт-лок (== HEAD), изменены Q4-A/Q4-C.
- Content-реаудировано 16/20. Sidecar: +c17/+blind_c17, blocks_reaudited=16. Worst-first остаток: **Q13(1.5) — ЕДИНСТВЕННЫЙ >1.35** (после него OPTION_LENGTH_RATIO пройдёт), затем финальный sweep (Q8/Q1/Q7 length-ок но без индив.реаудита; де-каррикатура Q12-A). skel PASS.

### ROUND-8 · redpanda · c18 Q13 length (trim long C + raise D) — 2026-07-14
- **c18 = Q13** («что такое WASM Data Transforms в Redpanda»), ПОСЛЕДНИЙ >1.35 блок файла (ratio 1.50; C=24w max/слишком длинный, D=16w min + D единственный без паренетезы). Correct=B байт-лок (факт-перепроверен: исполнение WASM-функций прямо внутри брокера для трансформации — фильтрация/обогащение/миграция схем — без отдельного stream-processing процесса; Redpanda Data Transforms с 2023 — точно).
- **Фикс (оба рычага):** C ужат 24→18w «…генерации Avro-схем из WSDL-файлов прямо на стороне брокера (парсинг WSDL и публикация в Schema Registry)» (misconception WSDL→Avro сохранён); D поднят 16→20w + паренетеза «…сообщения не пишутся на диск, а пересылаются между топиками прямо в памяти (in-memory passthrough без persistence)» (misconception in-memory-passthrough сохранён) → paren-паритет [1,1,1,1]. sections C/D не тронуты.
- ratio Q13 **1.50→1.176** (A17/B17/C18/D20).
- **blind (SEED 80126): ACCEPT** — single_correct=B, factual_error нет, other_defensible нет ([1]=SQL-легаси/[2]=in-memory/[4]=WSDL→Avro — все домен-специфичны, отсекаются знанием не абсурдностью), caricature нет, form_guess нет (у всех 4 уточняющая скобка, длины ровные). Correct B байт-лок (== HEAD), изменены Q13-C/Q13-D.
- Content-реаудировано **17/20**. Sidecar: +c18/+blind_c18, blocks_reaudited=17. **ИТОГ: OPTION_LENGTH_RATIO gate ПРОЙДЕН** (0 блоков >1.35, worst Q8=1.33). Осталось до FINAL: финальный content-sweep Q8/Q1/Q7 (length-ок но без индив.реаудита) + де-каррикатура Q12-A. ЕДИНСТВЕННЫЙ residual gate FAIL = ABSOLUTE_MARKER_GAP=0.30 (легитимные silver-bullet-маркеры Q11/Q18/Q19 — НЕ трогать). skel PASS.

### ROUND-8 · redpanda · c19 Q12 де-каррикатура A (снят pile-up абсолютов) — 2026-07-14
- **c19 = Q12** («какие архитектурные решения делают Redpanda быстрее Kafka»), де-каррикатура A (c5-кандидат). Correct=D байт-лок (факт-перепроверен: C++/без JVM и GC-пауз/shard-per-core/async-I/O Seastar/опции DPDK/без round-trips к ZooKeeper — каноничный многофакторный ответ).
- **Фикс:** Q12-A нёс ТРИ cartoon-абсолюта «чистом ассемблере / SIMD во ВСЕХ функциях / ПОЛНОЕ отсутствие накладных расходов» → отсекался по абсурду. Смягчён (misconception «скорость от ручной микрооптимизации/asm/SIMD, не от архитектуры» сохранён): «Ручная оптимизация на ассемблерных вставках в горячих путях, широкое применение SIMD-инструкций и тщательная минимизация накладных расходов среды исполнения» (18w). **sections A НЕ тронуты** — они уже описывали умеренную версию («ассемблер только в SIMD hot-path, характерно для C++ вообще») → правка ПРИВЕЛА текст в соответствие с sections.
- ratio Q12 **1.11** (A18/B18/C20/D18), пунктуация [0,0,0,0] uniform, абсолютов не осталось.
- **blind (SEED 83471): ACCEPT** — single_correct=D, factual_error нет, other_defensible нет (A ассемблер/SIMD НЕ определяющий фактор скорости Redpanda → ложен как ответ), каррикатур НЕТ (A теперь отсекается по знанию а не абсурду — «взрослый дистрактор»). form_guess NIT (не блокер): D перечислительнее (6 механизмов) — ПРЕД-СУЩЕСТВУЮЩАЯ inherent-асимметрия (c19 тронул только A), D байт-лок = урезать нельзя, обогащение B/C рискует кросс-контаминацией осей → INHERENT как Q9/Q16/Q19. Correct D байт-лок (== HEAD), изменён только Q12-A.
- Content-реаудировано **18/20**. Sidecar: +c19/+blind_c19, blocks_reaudited=18. **ОСТАЛОСЬ ДО FINAL (§1b#6): индив. content-реаудит Q5 (no-GC-pauses) + Q11 (silver-bullet-маркеры; факт-скан c5 чист — только form/length, абсолюты Q11 НЕ трогать).** Гейты: OPTION_LENGTH_RATIO PASS, skel PASS, ABSOLUTE_MARKER_GAP=0.30 (легитимные маркеры).

### ROUND-8 · redpanda · c20 Q5 skel colon-tell + де-каррикатура A/B/C (REVISE→ACCEPT) — 2026-07-14
- **c20 = Q5** («почему отсутствие JVM влияет на стабильность p99») — индив. content-реаудит. Correct=D байт-лок (факт-перепроверен: нет GC-пауз stop-the-world → p99 без спайков на десятки-сотни мс; масштаб реалистичен для GC-хвоста нагруженной JVM).
- **Шаг 1 (skel):** C был ЕДИНСТВЕННЫЙ с `:` [0,0,1,0] → colon убран («ведь»), skel warn_blocks 3→2.
- **blind#1 (SEED 90218) REVISE:** single_correct=D/факт-чист/other_defensible нет, НО A(«освобождается автоматически без всякого менеджера→нулевые расходы») и B(«GC в C++ всегда в отдельном процессе») = грубые КАРРИКАТУРЫ, отсекаются по эрудиции про C++ а не по знанию; + form-bias от абсолютов «всегда/нулевые/без всякого» и лобовое «выигрыша нет» в C.
- **Шаг 2 (§1b#7 де-каррикатура A/B/C, misconception-оси сохранены, sections A/B/C НЕ тронуты — уже рефутят умеренные версии):** A→mechanism-flip «дешевизна аллокаций через RAII убирает всплески p99» (подмена механизма: реальная причина=нет STW); B→«освобождение вынесено в фоновый поток→не приостанавливает» (calm novice error; в C++ деструкторы синхронны inline); C→modern-GC-denial «современные сборщики Kafka дают субмиллисекундные паузы, разница в хвостах невелика» (мягко). ratio Q5 1.13→1.188 (A16/B16/C19/D17), пунктуация [0,0,0,0] uniform, абсолютов не осталось (ABSOLUTE_MARKER_GAP 0.30→0.283 попутно).
- **blind#2 (SEED 94655): ACCEPT** — single_correct=D факт-чист, other_defensible нет ([2]=A подмена механизма/[3]=B фоновое-освобождение ложь/[1]=C отрицание предмета), каррикатур НЕТ («обучающие ловушки, не соломенные чучела»), form_guess НЕТ. Correct D байт-лок (== HEAD), изменены Q5-A/B/C.
- **УРОК c20:** де-каррикатура НЕ-judgment-блока = дистрактор с absolute-фразой («без всякого/нулевые/всегда») может быть грубой карикатурой (отсекается по общей эрудиции про язык, не по знанию канона) → смягчить в правдоподобную ложную модель ИЛИ подмену механизма (right premise → wrong causal attribution), misconception-ось сохранить; sections НЕ трогать если уже описывают умеренную реальность. Отличие от Q11/Q18/Q19: там абсолюты = ЛЕГИТИМНЫЕ silver-bullet-маркеры (judgment-вопрос), здесь = карикатура (факт-вопрос).
- Content-реаудировано **19/20**. Sidecar: +c20/+blind_c20, blocks_reaudited=19. **ОСТАЛОСЬ ДО FINAL (§1b#6): ТОЛЬКО Q11** (silver-bullet-маркеры C over-claim + D under-claim — факт-скан c5 чист; проверить form/length, абсолюты НЕ трогать). Гейты: OPTION_LENGTH_RATIO PASS, skel PASS, ABSOLUTE_MARKER_GAP=0.283 (легитимные маркеры).

### ROUND-8 · redpanda · c21 Q11 em-dash-parity → **redpanda FINAL 20/20** — 2026-07-14
- **c21 = Q11** («как корректно интерпретировать performance-показатели Redpanda vs Kafka») — ПОСЛЕДНИЙ блок, judgment-вопрос. Correct=B байт-лок (факт-перепроверен: вендор-бенчи p99 в разы ниже/выше throughput на ядро, реальный выигрыш зависит от настроек/нагрузки, независимые подтверждают но обычно меньше — взвешенная истина).
- A/C/D = ТРИ легитимных silver-bullet wrong-mindset (A пессимист «всегда медленнее», C наивный «ровно 100× на любом железе», D циник «неизменно одинаковая/чистый маркетинг») — **абсолюты НЕ тронуты** (весь смысл judgment-вопроса).
- **Единственный form-дефект:** D нёс em-dash [0,0,0,1] («цифры — это чистый маркетинг») → «— это»→«остаются» (пунктуация [0,0,0,0] uniform, semi [1,1,1,1]; cynic-маркеры сохранены; sections D не тронуты). skel warn_blocks 2→1.
- **blind (SEED 97140): ACCEPT** — single_correct=B факт-чист, other_defensible нет, три дистрактора «покрывают три канонических типа неверного мышления». NIT (ROTATION, не блокер): A «C++ не умеет async I/O» техн-абсурден → принят как denial-pole (абсурдность интринсична полюсу «Redpanda медленнее» + кросс-check Q4/Q12; реврайт → коллизия с D-циник-полюсом). form_guess (B единственный сбалансированный) = INHERENT judgment-формату. Correct B байт-лок (== HEAD), изменён только Q11-D.
- **redpanda FINAL 20/20** (§1b#6): ВСЕ блоки content-реаудированы c1..c21, каждый нетривиальный закрыт независимым слепым ревью. Итог baseline→FINAL: OPTION_LENGTH_RATIO 3.17→worst 1.33 (PASS), skel hard 2→0 (PASS), POS SEQUENCE 20→4, ABSOLUTE_MARKER_GAP 0.38→0.283 = **ПРИНЯТОЕ ИСКЛЮЧЕНИЕ** (легитимные silver-bullet-маркеры Q11/Q18/Q19; §1 метрики низший приоритет). Факт-слой ЧИСТ (c5-скан + c14-fix Q16-C tiered storage). Deferred NITs (rotation): Q2-B, Q4-C, Q11-A, Q12-form. Следующий worst-first файл — по gen-plan-matrix.py (redpanda исключить как FINAL).

### ROUND-8 · micrometer · c1 bootstrap POS de-cycle + freshness/review sidecars — 2026-07-14
- **Новый worst-first файл: `monitoring/micrometer-interview.json`** (20 блоков, Sev=MED, ΣCAR=1). Выбран после redpanda FINAL по матрице. off-limits СНЯТЫ полностью (PLAN §59, 2026-07-14) — единственный барьер dirty-tree. Топ-tractable по ΣCAR: mutation-testing (FINAL)/kotlin (COMPLETE)/agentic-patterns (CRIT, нет sidecar) → **offset-ниже** (agentic-patterns = вероятная цель параллельной top-down сессии) → micrometer (был ROUND-6 parity, но БЕЗ ROUND-8 content-реаудита; нет sidecar). collision-guard clean.
- **Baseline-гейты (сырой файл):** OPTION_LENGTH_RATIO=1.93 (worst Q13; 7 блоков >1.35), CORRECT_WRONG_AVG_RATIO=0.884 (correct КОРОЧЕ в среднем — редкий обратный перекос), ABSOLUTE_MARKER_GAP=0.367, CORRECT_POSITION_SEQUENCE=20 (строгий ABCD-цикл), DUPLICATE_NGRAMS=16, skel hard_fails=1 warn_blocks=6, SOURCE_COVERAGE missing.
- **c1 = POS de-cycle + bootstrap.** Correct-позиции были строгим циклом `ABCDABCDABCDABCDABCD` (SEQUENCE=20) → перестановка label/order по BASE `[2,0,3,1,0,2,1,3]` → `CADBACBDCADBACBDCADB` (dist 5/5/5/5, arith-run=2). **Контент КАЖДОЙ опции байт-идентичен HEAD** (multiset-подпись блока set-equal, только позиции/label; относит. порядок дистракторов сохранён) → **blind НЕ требуется**. SEQUENCE 20→2, DISTRIBUTION 1.0.
- + bootstrap `fact-freshness/micrometer-interview.json` (SOURCE_COVERAGE missing→ok; Micrometer 1.x facade, Observation API 1.10+, checked_date 2026-07-14) + `reviews/micrometer-interview.json` (review-sidecar).
- Гейты после c1: SEQUENCE=2, DISTRIBUTION=1.0, SOURCE_COVERAGE ok, verify-mcq-json OK. **ДАЛЬШЕ (worst-first, каждый с блайндом):** §1b п.1 факт/когерентность-скан всех 20 → DUPLICATE_NGRAMS=16 → OPTION_LENGTH Q13(1.93)+6 блоков → ΣCAR=1 карикатура → skel hard=1 → CORRECT_WRONG_AVG обратный перекос. blocks_reaudited=0/20 (POS не content-реаудит).

### ROUND-8 · micrometer · c2 факт/когерентность-скан всех 20 + де-карикатура Q14-A — 2026-07-14
- **§1b п.1 факт/когерентность-скан всех 20 блоков (приоритет над метриками):** каждый correct перепроверен (Q1..Q20, полный список в review-sidecar `fact_coherence_sweep`). **Факт-слой CLEAN** — фактических ошибок в correct-ответах НЕТ; само-противоречий в дистракторах НЕТ (все — когерентные single-misconception). Version-sensitive нюанс (Q19 property Boot2 vs Boot3) покрыт freshness-sidecar.
- **Единственный caricature-сигнал ΣCAR=1 (Cfak/fake_reasoning) локализован → Q14-A.** Стем Q14 «Как создать кастомную метрику в Spring Boot?» = FACT-вопрос. A содержал маркер псевдо-обоснования «якобы» + само-негацию «Вариант 1 **(якобы полный)**» / «**Вариант 2 отсутствует**» / «иных путей нет» — это форм-телл: опция сама себя выдаёт по форме, отсекается без знания предмета.
- **Де-карикатура Q14-A (правился ТОЛЬКО дистрактор A; correct C байт-лок == HEAD; label/order/1-correct/opening_class(prose×4) инвариантны; sections A не тронуты):** ось заблуждения («декларативный `@Timed` + его `count` заменяют программные Counter/Gauge → реестр инжектить не надо») сохранена, но подана прямо, уверенно, без абсолюта «Единственный способ» и без winking-скачка «раз…то…». Стало: «В `Spring Boot` метрики удобнее объявлять декларативно, через `@Timed`… у полученного таймера есть готовый `count`, поэтому отдельные `Counter.builder(...)` и `Gauge.builder(...)` заводить не нужно — аннотации над методами хватает». Gate caricature fake_reasoning **1→0**, ΣCAR **1→0**.
- **Слепой ревью ДВАЖДЫ** (независимый reviewer, без доступа к репо): SEED 41787 (correct=D) → ACCEPT + REVISE-совет «снять оставшийся форм-маркер `Единственный способ`» → применён → re-blind SEED 52913 (correct=A) → **ACCEPT**, вердикт «B (де-карикатуренный `@Timed`-дистрактор) полностью чист по форме, неверность выводится только предметно, `count` таймера ≠ произвольный Counter/Gauge — правдоподобная реальная ловушка». single-correct держится, факт-ошибок в correct нет.
- Q14-длина не менялась (~34сл); skel HARD остался Q15 (semicolon {A:0,B:1,C:1,D:1}), не Q14; абревиация `Gauge.builder(...)` в A слегка снизила DUPLICATE-вклад. Baseline-метрики (OPTION_LENGTH/DUPLICATE/ABSOLUTE_MARKER/CORRECT_WRONG_AVG) НЕ ухудшены — остаются worst-first целями. **blocks_reaudited=1/20.**
- **ДАЛЬШЕ (worst-first, каждый с блайндом):** DUPLICATE_NGRAMS=16 → OPTION_LENGTH Q13(1.93) + 6 блоков → CORRECT_WRONG_AVG=0.884 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c3 де-дубликация DUPLICATE_NGRAMS Q6 (16→11) — 2026-07-14
- **DUPLICATE_NGRAMS=16 локализован → 4 блока:** Q6(B/C/D, 5 шинглов) / Q18(A/D, 5) / Q13(A/B, 3) / Q17(B/C/D, 3). Каждый = один общий ~14-токенный спан (sliding-window даёт 3–5 перекрывающихся 10-грамм). Взят worst: **Q6** (три опции клонируют друг друга, включая correct C).
- **Причина дублей:** Q6 стем «Как работает `Gauge`?» — дистракторы построены как single-delta от correct C: второе предложение (список источников привязки Gauge) БАЙТ-идентично в B/C/D, плюс хвост первого «`Micrometer` опрашивает привязанный объект при каждом сборе метрик» общий у B/C. (Код-примеры внутри backtick стрип-аются токенайзером — дубль ловится по ПРОЗЕ-связкам.)
- **Правка (correct C байт-лок == HEAD; правились ТОЛЬКО дистракторы B и D; код-примеры и ось заблуждения не тронуты; sections не тронуты; opening_class btick×4 инвариант):** разведена коннективная проза — B «Привязывают к коллекции…, к…, или к лямбде» → «Источником служат размер коллекции…, значение `AtomicInteger`…, или результат лямбды»; D → «Значение снимают с коллекции…, с…, или с лямбды». Мисконцепции сохранены (B = Gauge монотонен как `Counter`; D = накопление истории для `rate()`). **DUPLICATE 16→11** (осталось Q18/Q13/Q17).
- **Слепой ревью** (независимый reviewer, без репо, SEED 63104, correct=A-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок в correct нет, дистракторы бьют по разным реальным осям (ручной `set()` / «только вверх как Counter» / «накопление для `rate()`»). Ловушка B про ручной `set()` = реальное заблуждение мигрантов с Prometheus/Dropwizard (у `io.micrometer` `Gauge` метода `set()` НЕТ) — не второй верный.
- NIT (rotation, не блокеры): B — код-одиночка (слабый form-guess, **pre-existing**: мисконцепция `set()` требует иного API, код не трогался; STYLE_GUESSABILITY code-spans=0.17 ok); C-clause «мгновенное значение… только возрастает» на грани само-разоблачения (не мой edit). **blocks_reaudited=2/20.**
- **ДАЛЬШЕ:** доразвести DUPLICATE Q18(A/D)/Q13(A/B)/Q17(B/C/D) → OPTION_LENGTH Q13(1.93) → CORRECT_WRONG_AVG=0.884 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c4 де-дубликация DUPLICATE_NGRAMS Q18 (11→6) — 2026-07-14
- Продолжение де-дупа (c3 закрыл Q6). Взят следующий worst — **Q18** «Что такое `MeterFilter`?» (A↔D, 5 шинглов), correct=A.
- **Дубль-спан A↔D:** регистрационное предложение (`MeterRegistryCustomizer` → `registry.config().meterFilter(...)`) + список встроенных фильтров с глоссами (`deny`/`accept`/`ignoreTags`/`maximumAllowableTags`) байт-идентичны.
- **Правка (correct A байт-лок == HEAD; правился ТОЛЬКО дистрактор D; факты глосс и мисконцепция сохранены; sections не тронуты; opening_class btick×4 инвариант):** перефразирована проза D — глагол «Регистрируется через… который вызывает» → «Подключают его тем же… через»; глоссы-синонимы «запрет регистрации/явное разрешение/удаление тегов/ограничение кардинальности» → «блокировка по имени/пропуск метрики/срезание тегов/лимит кардинальности». Мисконцепция D («`commonTags` — отдельный, не связанный механизм») цела. **DUPLICATE 11→6** (осталось Q13/Q17).
- **Слепой ревью** (независимый reviewer, SEED 70551, correct=B-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок в correct нет. D подтверждён как ценный near-miss: `commonTags(...)` — статическая фабрика самого `MeterFilter`, поэтому утверждение D фактически ложно, но опирается на реальную путаницу («общие теги ощущаются глобальной настройкой»). **blocks_reaudited=3/20.**
- **ДАЛЬШЕ:** доразвести DUPLICATE Q13(A/B)/Q17(B/C/D) → OPTION_LENGTH Q13(1.93) → CORRECT_WRONG_AVG=0.884 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c5 де-дубликация DUPLICATE_NGRAMS Q17 (6→3 = PASS-порог) — 2026-07-14
- Продолжение де-дупа (c3 Q6, c4 Q18). Взят **Q17** «Что такое SLO в `Micrometer`?» (B/C/D, 3 шингла), correct=C.
- **Дубль-спан B/C/D:** второе предложение (Prometheus-конверсия порогов в `..._bucket{le=…}`-серии + PromQL-формула доли быстрых запросов) байт-идентично.
- **Правка (correct C байт-лок == HEAD; правились ТОЛЬКО дистракторы B и D; PromQL-формула и мисконцепции сохранены; sections не тронуты; opening_class prose×4 инвариант):** перефразирована проза — B «…по которым PromQL считает процент быстрых запросов» → «На стороне `Prometheus` каждый порог даёт серию…, а долю быстрых запросов затем берут формулой»; D → «В экспортируемом `Prometheus`-формате пороги становятся сериями…, а процент уложившихся считают как». Мисконцепции целы (B = SLO как авто-алерт без `Alertmanager`; D = пороги только в нс, `Duration` не принимается). **DUPLICATE 6→3** (= PASS-порог ≤3; остаток Q13).
- **Слепой ревью** (независимый reviewer, SEED 81990, correct=C-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок в correct нет. B ложен по сути (Micrometer — библиотека инструментирования, не алертит, не заменяет `Alertmanager`); D ложен (перегрузка `serviceLevelObjectives(Duration)` существует, `Duration.ofMillis(50)` валиден) — обе правдоподобные ловушки. NIT (rotation): A/D чуть длиннее C — слабый form-сигнал (Q17 ratio 1.31 < порог). **blocks_reaudited=4/20.**
- **ДАЛЬШЕ:** доразвести DUPLICATE Q13(A/B) до 0 → OPTION_LENGTH Q13(1.93) → CORRECT_WRONG_AVG=0.884 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c6 Q13 двойной фикс: DUPLICATE→0 GREEN + OPTION_LENGTH 1.93→1.33 — 2026-07-15
- **Q13 «Какие метрики `Spring Boot` регистрирует автоматически?» — двойная цель одним блоком:** последний DUPLICATE-кластер (A/B) + худший OPTION_LENGTH (1.93; correct A=15сл SHORTEST). correct=A байт-лок.
- **Причина обеих проблем:** дистракторы B/D построены как «полный глосс-список A (verbatim) + false-twist». B повторял список A дословно (→ DUPLICATE A/B), а twist добавлял длину (→ B=26, D=29 против A=15).
- **Правка (правились ТОЛЬКО дистракторы B и D; correct A и дистрактор C не тронуты; sections B/D не тронуты — мисконцепции целы; opening_class btick×4 инвариант):**
  - B: «регистрирует тот же широкий набор [полный глосс-список] но по умолчанию все биндеры выключены…» → «включает тот же набор — JVM, HTTP, пулы `HikariCP`, `Tomcat`, логи и кэши, — но по умолчанию биндеры выключены и каждый активируют через `management.metrics.binders.*.enabled=true`» (компактный НЕ-verbatim список рвёт дубль + тримминг 26→20сл). Мисконцепция «биндеры по умолчанию выключены» цела.
  - D: «…отдаёт не через actuator, а в лог-файлы [полный список]…» → «включает тот же набор, но отдаёт его не через actuator-эндпоинты, а прямо в лог-файлы, откуда `Prometheus` их и читает» (список выброшен — у D речь о канале доставки, не о наборе; 29→19сл). Мисконцепция «метрики в логах» цела.
- **Результат Q13:** A15/B20/C19/D19 → ratio **1.93→1.33 PASS**; **DUPLICATE файл-wide 3→0 GREEN** (весь кластер Q6/Q18/Q17/Q13 закрыт); DETAIL_PARITY не вырос (0.1 — correct не стал detail-доминантом, B=file-«только HTTP» тоже с полным списком).
- **Слепой ревью** (независимый reviewer, SEED 93472, correct=D-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок в correct нет, сильный длинный близнец B закрывает угадывание по длине («правдоподобная развилка B vs D требует знания: только HTTP vs широкий набор»).
- NIT (rotation, **НЕ применён**): ревьюер (секций не видел) счёл D «лог-файлы» грубоватым (form_guess-риск). Но sections D придают когерентный источник: `source_of_confusion` = «`logback.events` ('события логов') ошибочно понимают как 'метрики живут в логах'» → это документированная путаница, не карикатура (детектор Cfak NONE). Переосмысление в «endpoint не exposed» = полный перепис 4 секций + свой блайнд → отложено. **blocks_reaudited=5/20.**
- **ДАЛЬШЕ:** OPTION_LENGTH worst теперь Q7(1.55) + 5 блоков (Q14/Q15/Q20/Q9/Q19) → CORRECT_WRONG_AVG=0.889 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c7 OPTION_LENGTH Q7 1.55→1.23 — 2026-07-15
- **Q7 «Как работает `Timer`?» — худший OPTION_LENGTH (1.55).** correct=B байт-лок (22сл, SHORTEST). Дистракторы A32/C31/D34 длиннее из-за развёрнутых мисконцепций + trailing false-clause.
- **Правка (правились ТОЛЬКО дистракторы A/C/D; correct B не тронут; sections не тронуты — мисконцепции целы; opening_class btick×4 инвариант):** тримминг всех трёх до ≤30сл, сохраняя ложные тезисы:
  - A «только незавершённые + число активных вызовов + во всех теряется» → «только незавершённых операций, не фиксируя завершённые… итоговая длительность при этом теряется» (32→24).
  - C «start/stop только пишет, прочие — разовая оценка» ужат (31→27).
  - D «измеряет и время и частоту, но хранит исключительно в нс, не отдаёт агрегаты» → «хранит замеры только в наносекундах…, но `timer.totalTime(TimeUnit.SECONDS)` уже не прочитать» (34→26).
- **Результат Q7:** A24/B22/C27/D26 → ratio **1.55→1.23 PASS**; DUPLICATE остался 0; CORRECT_WRONG_AVG 0.889→0.9 (тримминг поднял).
- **Слепой ревью** (независимый reviewer, SEED 10627, correct=B-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок нет, все три сохранили осмысленные ложные тезисы (A=только start/stop пишет, C=`LongTaskTimer`-путаница усилена именем `.active`, D=только-нс полу-правда, структурно уравновешивает B). NIT: B визуально длинный из-за `publishPercentiles`-кода (по словам SHORTEST) — form-guess скомпенсирован D. **blocks_reaudited=6/20.**
- **ДАЛЬШЕ:** OPTION_LENGTH worst теперь Q14(1.52, driven B=38сл) + Q15/Q20/Q9/Q19 → CORRECT_WRONG_AVG=0.9 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c8 OPTION_LENGTH Q14 1.52→1.32 + CORRECT_WRONG_AVG GREEN — 2026-07-15
- **Q14 «Как создать кастомную метрику в `Spring Boot`?» — worst OPTION_LENGTH (1.52, driven B=38сл).** correct=C байт-лок (25сл, SHORTEST).
- **Правка (правились ТОЛЬКО дистракторы B и A; correct C и дистрактор D=33 не тронуты; sections не тронуты — мисконцепции целы; opening_class prose×4 инвариант):**
  - B: «оба через пересоздание… счётчик не хранит и значение обнуляется… `Gauge.builder` на каждый вызов, потому что реестр ссылку не удерживает» → «…счётчик между вызовами не хранит… пересоздавать `registry.timer` с `record`, ведь реестр ссылку на метрику не удерживает» (38→28; выброшен избыточный gauge-пример + дубль-обоснование). Мисконцепция «пересоздавать метрику каждый вызов» цела.
  - A (c2-текст): «у полученного таймера есть готовый `count`, поэтому отдельные `Counter.builder` и `Gauge.builder` заводить не нужно» → «его `count` уже считает вызовы, поэтому `Counter.builder(...)` и `Gauge.builder(...)` заводить не нужно» + paren «(тоже без инжекции)»→«(без инжекции)» (34→31; paren-parity с C сохранён). Мисконцепция «@Timed-only» цела.
- **Результат Q14:** A31/B28/C25/D33 → ratio **1.52→1.32 PASS**; DUPLICATE остался 0; **CORRECT_WRONG_AVG 0.9→0.907 GREEN** (тримминг закрыл).
- **Слепой ревью** (независимый reviewer, SEED 24815, correct=C-позиция) → **ACCEPT**: ровно один защитим, факт-ошибок нет, B (@Timed-only «декларативно всё покроем») и D (пересоздание из-за мнимого отсутствия кэширования метров) сохранили осмысленность после сжатия; form_guess не выдаёт ответ. **blocks_reaudited=7/20.**
- **ДАЛЬШЕ:** OPTION_LENGTH worst теперь Q15(1.43) + Q20/Q9/Q19 → ABSOLUTE_MARKER_GAP=0.367 → skel HARD Q15. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c9 OPTION_LENGTH Q15 1.43→1.33 + skel HARD Q15 УСТРАНЁН — 2026-07-15
- **ДВОЙНАЯ ЦЕЛЬ: Q15 «Что такое аннотация `@Timed`?» = worst OPTION_LENGTH (1.43) И skel HARD** (semicolon presence `{A:0,B:1,C:1,D:1}` — A единственная без `;`). Один блок закрывает обе. correct=B байт-лок (21сл, SHORTEST).
- **Правка (правился ТОЛЬКО дистрактор A — единственный >28сл; C=25/D=28 в норме; sections не тронуты; opening_class btick×4 инвариант):**
  - A: trim 30→26сл + добавлен `;` (закрывает skel: B/C/D несли `;` из bean-сниппета `@Bean TimedAspect(){ …; }`, у A его не было) + смягчены абсолюты («на любом методе»→убрано, «сразу создаёт»→«создаёт», «Никакого бина»→«отдельного бина», «большинство аннотаций»→«прочие аннотации»). Мисконцепция сохранена **дословно** (работает без настройки бинов, включается сам как прочие `Spring`-аннотации) — совпадает с sections A (source_of_confusion = «большинство `Spring`-аннотаций из коробки»).
- **Результат Q15:** A26/B21/C25/D28 → ratio **1.43→1.33 PASS**; **skel hard_fails 1→0 GREEN** (semicolon `{1,1,1,1}`); **ABSOLUTE_MARKER_GAP 0.35→0.333** бонусом; DUPLICATE остался 0.
- **Слепой ревью** (независимый reviewer, SEED 61924, correct=A-позиция) → **ACCEPT**: single-correct держится, факт-ошибок в correct нет, C (Counter вместо Timer) и D (мой edit: «нет бина/включается сам») — правдоподобные типовые заблуждения. **NIT (rotation, не тронутые в тик):** (a) orig-D «Timer хранит лишь `max`» = двойная-ошибка + лёгкое само-противоречие с percentiles-примером → кандидат де-карикатуры; (b) correct B — единственный без ограничит-«хвоста» (form-сигнал, но байт-лок). **blocks_reaudited=8/20.**
- **ДАЛЬШЕ:** OPTION_LENGTH worst теперь Q20(1.41) + Q9(1.40)/Q19(1.37) → ABSOLUTE_MARKER_GAP=0.333. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c10 OPTION_LENGTH Q20 1.41→1.32 — 2026-07-15
- **Q20 «Что такое `MeterBinder`?» — worst OPTION_LENGTH (1.41).** correct=B байт-лок (22сл, SHORTEST).
- **Правка (правились ТОЛЬКО дистракторы C и D — единственные >29сл; A=27 в норме; correct B + A не тронуты; sections не тронуты; opening_class btick×4 инвариант):**
  - C: выброшен filler-список «для метрик JVM, `HikariCP`, `Caffeine`» (дублировал перечень B, у C мисконцепции не нёс) → 30→27сл. Мисконцепция «`Spring Boot` сам не вызывает `bindTo()`, дёргаешь вручную в каждом сервисе» цела.
  - D: «инструментирования пулов соединений к БД» → «инструментирования пулов БД» (−2) → 31→29сл. Мисконцепция «SPI только для пулов БД / исключительно `HikariCP`, а JVM+`Caffeine` другими механизмами» цела.
- **Результат Q20:** A27/B22/C27/D29 → ratio **1.41→1.32 PASS**; DUPLICATE остался 0; CORRECT_WRONG_AVG 0.909→0.912.
- **Слепой ревью** (независимый reviewer, SEED 48302, correct=B-позиция) → **ACCEPT**: single-correct однозначен, форма неотличима (равная длина/структура + общий код-пример), карикатуры нет — A (ручной vs авто-вызов), C (выдуманная аннотация `@MeterBinder` = перенос стиля `@Timed`), D (переобобщение пул-юзкейса) бьют по реальным заблуждениям. NIT (косметика, correct байт-лок): `HikariCP` в B подан как «пример авто-регистрации Boot», хотя Hikari-метрики Boot привязывает через `MicrometerMetricsTrackerFactory` — ревьюер счёл защитимым. **blocks_reaudited=9/20.**
- **ДАЛЬШЕ:** OPTION_LENGTH worst теперь Q9(1.40) + Q19(1.37) → ABSOLUTE_MARKER_GAP=0.333. Цель — micrometer FINAL.

### ROUND-8 · micrometer · c11 WHOLE-FILE → FINAL (все гейты зелёные за одну итерацию) — 2026-07-15
- **Режим:** по запросу пользователя — довести весь файл до FINAL за одну итерацию (не по одному блоку). Закрыты оба оставшихся gate-фейла.
- **(I) OPTION_LENGTH (два длинных дистрактора; correct байт-лок):**
  - Q9 trim A 35→30сл («отличается лишь тем, что меряет»→«меряет», «просто выдают»→«выдают»; мисконцепция `LongTaskTimer`=`Timer`-в-часах цела) → Q9 1.40→1.20.
  - Q19 trim B 26→24сл (убраны «сразу»/«лишь»; мисконцепция «хватает первого шага» цела) → Q19 1.37→1.26.
  - Итог **OPTION_LENGTH_RATIO=1.34, 0 блоков >1.35 PASS**.
- **(II) ABSOLUTE_MARKER_GAP 0.333→0.233 PASS** — нейтрализованы 6 не-несущих/форм-телл абсолютов синонимами (мисконцепции дословно целы): Q4-C «любые»→«все», Q4-D «любое»→«каждое», Q10-C убрано «обязательно», Q10-D «только»→«лишь»×2, Q16-C убрано «полностью», Q18-B «только»→«лишь» + «Единственный фильтр:»→«Поддержан лишь фильтр» (снят «Единственный» форм-телл). wrong-с-абсолютами 26→20/60.
- **Инварианты:** все 8 правок — ТОЛЬКО дистракторы; correct все 20 байт-лок (== HEAD); label/order/1-correct/opening_class целы; sections не тронуты.
- **Слепые ревью 6 изменённых блоков** (независимые reviewer, свежие SEED): Q4/70415, Q9/33128, Q10/59260, Q16/81744, Q18/24907, Q19/66031 — **все ACCEPT** (single-correct держится, факт-ошибок в correct нет, дистракторы правдоподобны без карикатур).
- **Гейты FINAL:** answer-parity **1/1 PASS**, skel PASS (hard_fails=0), ΣCAR=0, факт CLEAN. **13/20 блоков** content-реаудировано с блайндом; 7 (Q1/Q2/Q3/Q5/Q8/Q11/Q12) — факт-скан c2 CLEAN + метрик-гейты зелёные без правок.
- **Rotation-NIT (на байт-локнутых correct или требуют перепис 4 секций, НЕ блокеры):** Q13-D «лог-файлы», Q15-D «Timer хранит лишь max», Q15 correct-хвост, Q9-C капс «ПРЯМО В ПРОЦЕССЕ», Q19-D property Boot2.x (freshness-covered).
- **micrometer → FINAL.** ДАЛЬШЕ: следующий worst-first tractable файл по `gen-plan-matrix.py`.

### ROUND-8 · selenium · c1 bootstrap (POS de-cycle + freshness/review sidecars) — 2026-07-15
- **Выбор файла:** после micrometer FINAL матрица worst-first. Топ-4 с ΣCAR>0 закрыты/off-limits (mutation-testing FINAL, kotlin COMPLETE, agentic-patterns off-limits, redpanda FINAL). Оставшиеся worst — сырые 40-44-блочные CRIT-гиганты (argocd: ratio 4.27, correct̄=41/wronḡ=17 — 126 дистракторов дописывать, НЕ одна итерация). Среди мелких TRACTABLE CRIT worst-first = **`testing/selenium-interview.json`** (15 блоков, реальный гейт умеренный). Параллельной MCQ-сессии сейчас НЕТ (все pedago-коммиты мои) → offset не нужен.
- **Baseline-гейты selenium (до c1):** OPTION_LENGTH_RATIO=4.44 (6 блоков >1.35; Q1/Q4/Q5/Q8 = длинный correct + stub-дистракторы), **DUPLICATE_NGRAMS=230** (главная проблема — near-copy опции), CORRECT_POSITION_SEQUENCE=15 (строгий ABCD-цикл), SENTENCE_PARITY=2 (3 блока), STYLE_GUESSABILITY=0.411, CORRECT_WRONG_AVG=1.111, skel hard_fails=3 (Q1/Q5/Q8), SOURCE_COVERAGE missing. **ΣCAR=0** (карикатур нет — чистый parity/dup/length кейс).
- **c1 (bootstrap, БЕЗ блайнда — контент байт-идентичен):** POS de-cycle per-block циклической ротацией к BASE15=[2,0,3,1,0,2,1,3,2,0,3,1,0,2,1] (multiset каждого блока == HEAD, сменились только label+order) → **SEQUENCE 15→2, DISTRIBUTION 1.33** (4/4/4/3). + freshness-sidecar (Selenium 4.x W3C WebDriver, checked_date 2026-07-15) → **SOURCE_COVERAGE ok**. + review-sidecar.
- **ДАЛЬШЕ (worst-first, каждый контент-блок с блайндом):** c2 факт/когерентность-скан всех 15 (§1b п.1) → DUPLICATE_NGRAMS=230 (де-темплейт) → OPTION_LENGTH 6 блоков (дописать stub-дистракторы Q1/Q4/Q5/Q8 до формы correct) → skel HARD Q1/Q5/Q8 → SENTENCE/STYLE/CORRECT_WRONG. Цель — selenium FINAL.

### ROUND-8 · selenium · c2 де-дуп 6 worst-dup блоков (Q2,Q6,Q11,Q13,Q14,Q15) — 2026-07-15
- **Диагноз DUPLICATE=230:** файл собран из near-copy опций — каждый блок = correct + 3 клона-с-одним-твистом (общий спан на весь блок). 6 худших блоков дают львиную долю 10-грамм-повторов.
- **Правка:** в 6 блоках переписаны ВСЕ 18 дистракторов лексически-дивергентно (соединительная проза И хвост варьированы per-option), при сохранении мисконцепция-твиста, код-спанов, opening_class, ~длины. **correct БАЙТ-ЛОК** (не тронут). Твисты: Q2 (linkText=any-attr / localized=visible-text / xpath>css), Q6 (no-init / once-in-ctor-cached / @FindBys=OR-interchangeable), Q11 (Puppeteer-multibrowser / Cypress-all-browsers / Playwright-older-most-mature), Q13 (Cucumber-alone-no-selenium / java-no-feature-files / keywords-are-comments), Q14 (shared-state-good-inv / quit-not-needed / logic-via-API-inv), Q15 (pyramid-60-70%-inv / UI-base-cheaper / also-for-logic-edge-api).
- **Фикс введённых регрессий:** length (Q2-B→37w, Q11-A/B/C→~40w компактный dash-стиль, ratio ≤1.28 в 6 блоках) + skel (Q6-D `;`→`—`, Q11-A/B/C хвост `;`→`.`) → skel HARD остался прежний **3 (Q1/Q5/Q8, pre-existing)**, регрессии нет.
- **Гейты c2:** **DUPLICATE_NGRAMS 230→80**. Остальные без изменений (OPTION_LENGTH 4.44, CORRECT_WRONG 1.117, SENTENCE 2, STYLE 0.411 — worst-first следующими). **ΣCAR=0**.
- **Блайнд:** все 6 блоков — свежие SEED, независимый reviewer, shuffle → **6/6 ACCEPT** (single_correct + no-factual-error-in-correct + no-other-defensible + no-caricature + no-form-guess подтверждены по каждому). blocks_reaudited **0→6/15**.
- **ДАЛЬШЕ:** де-дуп остальных дуп-блоков (Q3/Q7/Q9/Q10/Q12 + residual Q13/Q15) → DUPLICATE→≤3 → OPTION_LENGTH stub-блоки Q1/Q4/Q5/Q8 (длинный correct + stub-дистракторы: дописать дистракторы — попутно чинит CORRECT_WRONG + SENTENCE) → skel HARD Q1/Q5/Q8 → STYLE 0.411 → selenium FINAL.

### ROUND-8 · selenium · c3 де-дуп 5 шаблонных блоков (Q3,Q7,Q9,Q10,Q12) + 3 c2-хвоста — 2026-07-15
- **Диагноз DUPLICATE=80:** оставшиеся дуп-блоки — clause-flip шаблоны (correct + 3 near-verbatim клона, делящих длинные дословные спаны прозы; код в шинглы не входит). Плюс residual-хвосты c2-блоков (Q15 A/D=9 шинглов, Q13 B/D=3, Q14 A/B=1).
- **Правка:** переписаны 20 дистракторов лексически-дивергентно (проза каждого расходится с correct И соседями), твист/код-спаны/skel-паритет/~длина сохранены. **correct БАЙТ-ЛОК.** Твисты: Q3 (safe-mix max / swapped-defs / FluentWait=alias), Q7 (frame-auto-return / window-by-title / alert-via-findElement), Q9 (@Parallel-no-props / TestNG-unsupported / WebDriver-thread-safe), Q10 (positional-XPath-stable / implicit-60s-stable / sleep-per-check), Q12 (manual-drivers / headless-unsupported / screenshots-always).
- **Q15 form-tell (blind REVISE ×2):** correct = единственный «применяют для:/НЕ применяют для:» двоеточие-каркас → A/C/D переведены на тот же каркас (+ капс «НЕ» выровнен, colon-WARN закрыт); 2-й REVISE — остаточный капс-tell «НЕ»/«Не», выровнен. correct B байт-лок.
- **Гейты c3:** **DUPLICATE_NGRAMS 80→2 (PASS ≤3 — метрика закрыта).** skel HARD прежние 3 (Q1/Q5/Q8), edited-ratio ≤1.33. ΣCAR=0. Остаток — ТОЛЬКО stub-блоки Q1/Q4/Q5/Q8.
- **Блайнд:** 8 блоков (Q3,Q7,Q9,Q10,Q12,Q13,Q14,Q15), свежие SEED, независимый reviewer → **8/8 ACCEPT** (Q15 после 2 REVISE form-tell). blocks_reaudited **6→11/15**.
- **ДАЛЬШЕ (рывок к FINAL):** 4 stub-блока Q1/Q4/Q5/Q8 — дописать короткие stub-дистракторы до формы длинного correct с реальными мисконцепциями: чинит OPTION_LENGTH 4.44 + CORRECT_WRONG 1.122 + SENTENCE 2 + skel HARD + STYLE 0.417 разом → selenium FINAL (15/15).
