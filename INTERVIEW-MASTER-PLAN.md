---
title: "INTERVIEW MASTER PLAN — единый план аудита, доводки и расширения interview-шпаргалок"
description: "Консолидация 5 планов (ULTRATHINK v5, ULTRATHINK-audit, READABILITY-AUDIT, INTERVIEW-AUDIT-PLAN, INTERVIEW_EXPANSION_PLAN). Источник истины по качеству, инвариантам, тулчейну, истории работ (LANE 1–9) и оставшемуся расширению."
status: "active"
updated: "2026-06-16"
supersedes:
  - ULTRATHINK.md
  - ULTRATHINK-audit.md
  - READABILITY-AUDIT.md
  - INTERVIEW-AUDIT-PLAN.md
  - INTERVIEW_EXPANSION_PLAN.md
---

# INTERVIEW MASTER PLAN

> **Единственный источник истины** по interview-шпаргалкам (`cheatsheets/interview/`) и парным MCQ-сидерам (`modules/quiz-app/src/main/resources/seed/mcq/`). Объединяет 5 ранее раздельных планов без потери содержания. Старые планы удалены — их детальные пофайловые снимки остаются в git-истории.
>
> **Прогон правок:** проза `.md` — `/interview-writer`; JSON-сидеры — `/mcq-quality-fixer` либо детерминированные Python-правки/воркфлоу. Коммит по логической единице (1–3 файла или один батч), explicit pathspec. Граф `cheatsheets/` вне scope graphify — rebuild не требуется.

---

## 0. Текущее проверенное состояние (ре-аудит 2026-06-16)

Полный корпусный прогон всех гейтов:

| Измерение | Значение |
|---|---|
| Всего interview `.md` | **308** (305 вопросных + `README.md` + `TOC.md` + `preparation/interview-preparation.md`) |
| MCQ-сидеров (`.json`) | **305** |
| Выравнивание `q_number` ⊆ `## Q<N>` (alignment) | **305/305 OK**, 0 нарушений |
| Покрытие: каждый `## Q<N>` имеет JSON-блок | **305/305 полное**, 0 coverage-short |
| STRUCT (ровно 1 `correct`/блок, уникальные label A–D, order 0–3) | **305/305 OK** |
| mixed-script (intra-word рус+лат) | **0** (детектор — по строковым ЗНАЧЕНИЯМ, не по `json.dumps`: `\n`-эскейпы дают ложные срабатывания) |
| `> [!mcq]` callouts в `.md` | **0** |
| Tier-1 маркеры (`❌ ПОСЛЕДСТВИЕ`/`✓ ПРИМЕНЯТЬ`/`📋 ПРАВИЛО`/`🔗 См.`) | **0** |
| ` ```mermaid ` фенсы в `.md` | **0** |
| Словоупоминания «mermaid» в `.md` | **0** |
| Проза `.md` cyr-ratio | медиана ~0.72, минимум ~0.45, 0 файлов <0.45 |
| LANE 9 пилот полноты прозы (6 тем) | 6/6 score 4–5, 0 флагов |

**Вывод:** структурно/фактологически/по читаемости корпус чист. Оставшаяся активная работа — (а) подтверждающий сплошной ре-аудит полноты прозы `.md` (LANE 9), (б) net-new расширение тем (§9).

---

## 1. Полосы работ (LANE 1–9) — история и статус

| LANE | Что | Статус |
|---|---|---|
| **LANE 1** | callout-strip: 151 `.md` очищен от 5282 legacy `> [!mcq]` | ✅ done (1f9d11bc) |
| **LANE 2 A/B** | 55 single-blob `.md` восстановлены + сгенерирован JSON | ✅ done (c0059df8…1cafd2df) |
| **LANE 3** | restore-buried: 7 одиночных зарытых заголовков + observability 25 | ✅ done (3f4a09df, f6b21e3a) |
| **LANE 4** | round-2 single-blob (12 файлов), хвостовые зарытые вопросы | ✅ done (5f08f642, ff1a6335) |
| **LANE 5** | русификация англоязычной прозы 75 `.md` (cyr<0.45 → русский) | ✅ done (c3226303→финал) |
| **LANE 6** | глубокая редактура читаемости/качества всех 305 `.md` | ✅ done 2026-06-09 (verify.py+regen_toc.py, gate_clean 305/305) |
| **LANE 7** | перепроверка всех 305 JSON-сидеров новой моделью (факты+читаемость+дидактика+выравнивание), R1–R9 | ✅ done 2026-06-16 |
| **LANE 8A** | отказ от mermaid в контенте `.md` (конверт в прозу+списки / удаление декоративных) | ✅ done (verify_nomermaid.py, phaseA-strip) |
| **LANE 8B** | инфраструктура mermaid в app-коде (`MermaidSanitizer`/`Validator`/`DiagramService`, CSP) | ⚠️ внешний auto-improve — **НЕ трогать** |
| **LANE 9** | сплошной ре-аудит полноты раскрытия темы в прозе `.md` новой моделью | 🔄 active (пилот 6/6 чист; масштабируется по категориям) |

### Ключевые находки/решения (НЕ переоткрывать)
1. **Билингвальный регистр — НЕ дефект.** Русская связка + англо-термины (`high-risk`, `use case`, `human-in-the-loop`, имена API/продуктов) в `backticks` — принятый стиль. Форсить кириллицу на термины = churn + потеря узнаваемости. Флагать только целые англ. ПРЕДЛОЖЕНИЯ в прозе.
2. **Класс дефекта «зарытые хвостовые вопросы»** (LANE 3/4): TOC обещает Q1..Qmax, заголовки — чистый префикс Q1..QK, Q(K+1)..Qmax зарыты в блобе последнего заголовка. Лечится split блоба (0 потери прозы) + регенерация полного JSON. Урок → **completeness-gate**: файл «полон» ⟺ `TOC == ## Q == json` И gate-PASS И sequential (старый gate `## Q == json` это не ловил).
3. **Отказ ТОЛЬКО от mermaid** (LANE 8A): 0 ```mermaid фенсов и словоупоминаний. Критерий C6 старого ULTRATHINK («Mermaid diagrams 1–3 на файл») инвертирован В ЧАСТИ mermaid. **Уточнение пользователя (2026-06-16): все ОСТАЛЬНЫЕ диаграммы, которые рендерит markdown (ASCII/box-drawing в код-блоках, таблицы, списки-как-схемы), — приветствуются.** Бан касается исключительно mermaid; архитектуру/flow можно давать прозой, списками, таблицами ИЛИ ASCII-диаграммой — но не mermaid. См. [[feedback_diagrams_non_mermaid_ok]].
4. **Seed-first рантайм:** MCQ грузятся из JSON-сидеров; AI в рантайме не зовётся (только при `AI_FALLBACK_ENABLED=true` + ключ). См. `CLAUDE.md`.
5. **MCQ живут ТОЛЬКО в JSON** (`seed/mcq/<category>/<topic>.json`), c 2026-05-20. В `.md` — НИКОГДА `> [!mcq]` / Tier-1 маркеров (pre-commit `scripts/verify-md-no-mcq.sh` банит).
6. **Выравнивание по номеру:** приложение привязывает опции по `q_number` к `## Q<N>` (`MarkdownQuestionParser.QUESTION_PATTERN = ^##\s+Q(\d+)`). JSON `question_text` НЕ отображается. Поэтому JSON `q_number` ОБЯЗАН = `## Q<N>`, тему которого отвечают опции.
7. **Concurrency воркфлоу:** ≤6 агентов/волна (>6 → rate-limit, агенты не дописывают; урок LANE 5). Одна тяжёлая волна за раз.

---

## 2. Per-file checklist `.md` (A–E) — канонические критерии

> Источник: ULTRATHINK v5 (A–H) + INTERVIEW-AUDIT (A–D) + READABILITY-AUDIT (12 пунктов), объединено и дедуплицировано.

### A. Frontmatter (обязательные поля)
- **A1 `title`** — `"Вопросы на собеседовании: \`<Topic>\`"`.
- **A2 `description`** — одна строка ~80–200 символов, summary темы.
- **A3 `tags`** — YAML-список, ≥3 элемента (`interview`, `<category>`, `<topic>`). НЕ inline `[a,b]`.
- **A4 `aliases`** — ≥3 термина, mix EN+RU.
- **A5 `difficulty`** — `intermediate` | `advanced`.
- **A6 `updated`** — ISO-8601 `YYYY-MM-DD`, не в будущем.
- (исторически встречалось поле `type` — допустимо, не обязательно.)
- **Verify:** `text.startswith('---')` и все ключи в первых ~2000 символах.

### B. Структура файла
- **B1** intro (h1 + 1–3 параграфа) → **B2** `## Содержание` (TOC) сразу после.
- **B3** TOC-якоря = GitHub-slug заголовков (lowercase, пробел→`-`, спецсимволы strip; кириллица/`_` сохраняются). **Регенерируется детерминированно `regen_toc.py`, не руками.**
- **B4** заголовки вопросов строго `## Q<N>. <текст>?` (точка после номера; опц. `(!)` для важных). Парсер: `^## Q\d+`.
- **B5** Q-нумерация без пропусков (Q1..QN sequential).
- **B6** `## See also` — standalone top-level в конце (после `---`), НЕ nested.
- **B7** ≥5 markdown-ссылок в See also, относительные пути с `.md`.
- **Completeness-gate (см. §1.2):** `TOC-count == ## Q-count == json-count`, sequential. Файлы без question-TOC → `## Q == json == seq`.

### C. Человекочитаемость прозы (ГЛАВНОЕ)
- **C1** Только русский в повествовании; EN — только в `backticks`/коде/путях/именах. (Билингвальный регистр §1.1 — ок.)
- **C2** Короткие абзацы (≤4 предложения), активный залог.
- **C3** Bullet-списки для 3+ пунктов.
- **C4** Таблицы для `vs`-сравнений.
- **C5** Код в блоках с language-tag.
- **C6** ~~Mermaid diagrams~~ → **под запретом ТОЛЬКО mermaid** (§1.3): mermaid нет; но ASCII/box-drawing-диаграммы в код-блоках, таблицы и списки — приветствуются для архитектуры/flow.
- **C7** **Жирное** для ключевых выводов, ≤2 раза/абзац.
- **C8** Примеры обязательны: конкретные числа (5 000 QPS), компании (Netflix, Stripe), код (`@Transactional(...)`).
- **C9** Без воды: «важно отметить что», «стоит упомянуть», «необходимо понимать».
- **C10** Без маркетинга: «мощный инструмент», «гибкое решение», «современный подход».
- **C11** Tone — инженер инженеру (без академизма «рассмотрим», «таким образом»).
- **C12** Каждый ответ: суть → механика → пример → **Итог:**. Термин раскрыт при первом употреблении.
- **C13** Связный нарратив: логичный порядок вопросов, переходы между блоками.
- **C14 (полнота раскрытия темы — LANE 9):** ответ полно и корректно покрывает тему на senior-уровне; нет дыр (ключевой аспект пропущен), заглушек, ответа не на тот вопрос.

### D. Чистота markdown
- **D1** 0 `> [!mcq]` callout-блоков. **D2** 0 Tier-1 эмодзи-маркеров. **D3** 0 `[[wikilinks]]` в `.md` (только `[text](path.md)`). **D4** парные code-fence (чётное число ```), валидные таблицы. **D5** `scripts/verify-md-no-mcq.sh <file>` exit 0.

### E. Q count и важность
- **E1** Минимум Q: базовая тема ≥20; широкая (Spring, collections) ≥25; system-design ≥30; mega-topic (postgresql, java-concurrency) 50+.
- **E2** 20–30% Q помечены `(!)` (важные).

---

## 3. MCQ-JSON checklist (F–H)

### F. Наличие и структура
- **F1** файл `seed/mcq/<category>/<topic>-interview.json` существует.
- **F2** `topic_slug` = basename `.md`.
- **F3** покрытие: каждый `## Q<N>` имеет блок с тем же `q_number` (допустимо отсутствие → флешкард-режим, но цель — полное покрытие).
- **F4** JSON Schema draft-07 валиден (0 ошибок) против `seed/mcq-schema.json`.
- **F5** структура: `q_number:int`; `blocks:[{block_idx, question_text, options}]`; ровно 4 опции `{order 0–3, label A–D, text, correct:bool, sections}`.
- **F6** rotation `correct`-позиций A/B/C/D балансирован (max/min ≤ 2.0; идеал строгий A→B→C→D).

### G. Человекочитаемость/качество JSON
- **G1 correct sections — ровно 5:** `explanation` (3–7 предл., механика), `example` (production-кейс: компания/сервис/числа/код), `when_to_apply`, `edge_cases` (1–2 граничных, НЕ повтор misconceptions), `related` (plain refs или `[[topic#Qn]]` — конвенция проекта, рендерится в кликабельную ссылку).
- **G2 wrong sections — ровно 4:** `what_actually`, `source_of_confusion`, `if_it_were_true` (technical symptom / post-mortem: Knight Capital 2012 $440M, Equifax 2017, Capital One 2019), `how_it_should_be` (мост к correct).
- **G3** все секции на русском, термины в `backticks`.
- **G4** `text` опций — info-equivalent: `max(len)/min(len) ≤ 1.4` в блоке.
- **G5** дистракторы правдоподобны (middle мог бы выбрать), не бредовые; **ОДНОЗНАЧНО неверны** (не «тоже отчасти верны»).
- **G6** `example` — реальные кейсы (Netflix/Stripe/Uber/Yandex, PostgreSQL/Kafka/Resilience4j).
- **G7** БЕЗ `**bold**` в `text` (палит correct).
- **G8** БЕЗ эмодзи (❌📋✓🔗) в sections.
- **G9 banned-фразы (auto-reject):** `Частая ошибка в реальном коде.` · `Это антипаттерн или неправильный выбор в production.` · `Ключевое отличие и best practice in production.` · `Правильный ответ описывает основную концепцию.` · `Это смежное, но отличное понятие.` · `Противоположное направление.` · `Неправильный вариант 1/2/3` · `Объяснение концепции 2-3 предложения`.

### H. Smoke
- **H1** `./gradlew :quiz-app:test --tests '*McqJsonLoader*' -q` → BUILD SUCCESSFUL.
- **H2** `scripts/verify-md-no-mcq.sh FILE.md` → OK.

### Синхронизация .md ↔ JSON
- Номера совпадают; при правке `.md`-вопроса обновлять блок JSON. `related` ведут на существующие `[[<topic>#Q<N>]]`.

---

## 4. Таксономия дефектов (что искать при ре-аудите)

### 4.1. Структурная классификация inline-MCQ (исторически, INTERVIEW-AUDIT 2026-05-31 — ВСЁ разрешено LANE 1–7)
| Форма | Что значило | Стратегия (выполнена) |
|---|---|---|
| `NO_INLINE` | inline MCQ нет | проверить JSON-сидер |
| `CLEAN 1:1` | каждый `## Q` ↔ 1 `[!mcq]` | прямая миграция MCQ→JSON, strip inline |
| `MULTI` | MCQ/вопрос ≠ 1 | поблочная миграция (`block_idx`) |
| `COLLAPSED` | `nq≤2`, MCQ 15–43 под одним `## Q1` | миграция как N блоков + реструктуризация |

### 4.2. Seed-quality gate-бакеты (ULTRATHINK-audit 2026-05-30 — разрешено)
- **SEVERE — broken alignment:** orphans (опции без `## Q`) / missing (`## Q` без опций). Метрики `orph/miss/dup/contra/empty`.
- **READABILITY — info-ratio:** `rmax` = max/min длины секций; >4 = дисбаланс (1 слово vs параграф). Цель ≤4.
- **MINOR — rotation:** `correct`-позиции не сбалансированы; детерминированный reorder к строгой ротации.

### 4.3. Классы фактологических дефектов (LANE 7, новой моделью)
- **Выдуманный API** (несуществующие методы/классы) — сверять `javap`/докой.
- **Два верных / true-distractor** — дистрактор фактически ИСТИНЕН (перефраз верного). Чинить генерацией реально-ложного. **Никогда не флипать `correct`-флаг автоматически.**
- **`explanation == text` дубль** — переписать в «ПОЧЕМУ/механизм».
- **Устаревший/опровергнутый API** (Arrow `Validated` 2.0, CNCF-статусы, Spring Boot default-exposure).
- **Перепутанные операторы между фреймворками** (RxJava `debounce`/`throttleFirst` ≠ Reactor `sampleTimeout`/`sampleFirst`/`sample`).
- **Перепутанные «противоположности»** (Shotgun Surgery ↔ Divergent Change, оба Change Preventers, НЕ ↔ Feature Envy).
- **precision/recall swap**, **machine-transliteration** (`zadacha`, `bann`), **mixed-script**.
- **`q_number ↔ .md` рассинхрон** (худший): JSON собран под старую версию `.md`; опции под чужими вопросами. Фикс — переставить по СОДЕРЖАНИЮ опций (не по `question_text`), orphan-контент удалить, осиротевшие `.md`-слоты догенерировать.

---

## 5. Инварианты (НЕЛЬЗЯ нарушать — ловит верификатор)
1. Число и нумерация `## Q` неизменны (не добавлять/удалять вопросы при редактуре).
2. Код в ```-блоках — байт-в-байт (включая комментарии). Inline-код/термины в backticks сохранять.
3. Frontmatter (между `--- ---`) — не трогать при редактуре прозы.
4. JSON-сидеры — не трогать при правке прозы (и наоборот, кроме согласованной синхронизации).
5. `## See also` и блоки ссылок — не трогать при редактуре.
6. 0 MCQ-callouts/Tier-1 маркеров в `.md`.
7. Смешанные заголовки (рус+англ-термины) — оставлять; переводить только полностью-английские.
8. Факты 1:1 — редактура, не переписывание смысла.
9. TOC согласован: текст == заголовок, anchor == slug, полнота (TOC==`## Q`==json).
10. Mermaid — 0 фенсов и 0 словоупоминаний в `.md` (LANE 8A); инфраструктуру LANE 8B не трогать.
11. Seed-fix-агенты НЕ меняют `correct`-флаги (correct-flip → делать самому, осознанно).
12. Чужой WIP в `modules/` не коммитить — explicit pathspec.

---

## 6. Тулчейн и гейты (реестр)
- **`scripts/readability/gate_clean.py <md> <json>`** — authoritative MCQ-выравнивание (печатает `<file> PASS`).
- **`scripts/readability/verify.py <md> [<json>]`** — гейт против `git show HEAD:<md>`: `## Q`-set, code-fence байт-идентичность, frontmatter, TOC-согласованность, 0 callouts/Tier-1, 0 полностью-англ. заголовков, generic англ. bold-метки, cyr не упал, gate_clean PASS.
- **`scripts/readability/slug.py [--selftest]`** — GitHub-slugger (cyrillic-aware); self-test на 293 файлах с TOC.
- **`scripts/readability/regen_toc.py <md>`** — детерминированная регенерация `## Содержание` (идемпотентна, no-op для файлов без TOC).
- **`scripts/readability/verify_nomermaid.py <md> [<json>]`** — 0 mermaid-фенсов, не-mermaid код+frontmatter == HEAD.
- **`scripts/verify-md-no-mcq.sh <file>`** — pre-commit: 0 callouts/Tier-1.
- **STRUCT-чек (inline Python):** ровно 1 `correct`/блок, уникальные label, order 0–3.
- **mixed-script скан:** `LETTER_RUN=re.compile(r'[^\W\d_]+',re.U)`, флаг maximal letter-run с И кириллицей (`Ѐ`≤c≤`ӿ`) И латиницей. **ВАЖНО: сканировать строковые ЗНАЧЕНИЯ рекурсивно, не `json.dumps`** (`\n`-эскейпы → ложные `nИли`-срабатывания).
- **Полнота прозы (LANE 9):** воркфлоу `md-completeness-review.js` — read-only вердикты `{rel, score, issues:[{q, kind, problem}]}`, kind ∈ completeness/factual/readability.
- **Безопасные правки JSON:** (1) raw-replace `assert raw.count(old)==N; replace; json.loads`; (2) структурный, когда round-trip байт-идентичен (`raw.rstrip("\n")==json.dumps(d,ensure_ascii=False,indent=2).rstrip("\n")`; файлы indent=2, ensure_ascii=False, БЕЗ trailing newline).
- **Генерация большого JSON** — чанкованный Python-генератор / воркфлоу (sub-agent), иначе падение на 32k output-лимите.
- **Воркфлоу генерации/ревью** — паттерн generate(чанки ≤6)→адверсариальный ревьюер→применить accepted детерминированно + доправить rejected по фидбеку.

---

## 7. Workflow per-file (доводка)
1. **READ** `.md` (+ JSON). 2. **CHECK** A–H + полнота (C14). 3. **FIX** проза `/interview-writer`, JSON `/mcq-quality-fixer` / Python. 4. **VERIFY** verify.py + gate_clean + STRUCT + mixed-script + (если правился JSON) smoke. 5. **COMMIT** логически, explicit pathspec, trailer `Co-Authored-By: Claude Opus 4.8 (1M context)`. 6. **regen_toc.py** после правок заголовков.

---

## 8. Go-forward: «перепроверить всё заново» (LANE 9 + сидеры)
1. **Структурный ре-аудит** (§0) — ✅ выполнен 2026-06-16, корпус чист.
2. **Сидеры (LANE 7)** — ✅ все 305 перепроверены новой моделью.
3. **Полнота прозы `.md` (LANE 9)** — 🔄 сплошной сweep по категориям, волны ≤6 read-only ревью; материальные дыры (completeness/factual/readability score ≤3) — фиксить АДДИТИВНО (факты 1:1, инварианты §5), трекинг `scripts/readability/lane9_md_reviewed.txt`. Пилот (6 тем) чист → ожидается низкий сигнал; кампания подтверждающая.
4. **Порядок:** по категориям (databases→system-design→architecture→ai-ml→…) для консистентности терминологии.

---

## 9. Расширение корпуса (net-new темы) — OUTSTANDING

> Из INTERVIEW_EXPANSION_PLAN (2026-04-18, approved). Этапы A–J ВЫПОЛНЕНЫ (+92 файла, 106→198; далее корпус дорос до 305 вопросных). Решения: размер пачки 3–5 файлов; источники — baeldung + офиц. доки; README/TOC обновлять сразу; **Frontend и Mobile — ПРОПУСКАЕМ** (решение пользователя).

### Выполнено (✅): algorithms split (17), jvm-alternatives Ktor/Quarkus/Micronaut/Vert.x, Scala, Go (7), data-engineering (8), ai-ml (9+), cloud (6), monitoring (4), databases-new (5), messaging/devops/api/testing/security/performance/system-design/architecture точечные (этап J).

### Оставшееся (net-new, по приоритету):
- **Этап K — TypeScript + Rust** (языки):
  - `typescript/`: typescript, types, generics, configs.
  - `rust/`: rust, ownership-borrowing, concurrency, async.
- **Этап L — Python** (в самом конце; доминирует в AI/ML и data):
  - `python/`: python, async (asyncio), typing, gil, memory, testing.
- **Этап 6 — Frameworks (не-Spring), низкий приоритет:**
  - `frameworks/go-frameworks/`: gin, fiber, echo.
  - `frameworks/python-frameworks/`: fastapi, django, flask.
  - `frameworks/nodejs-frameworks/`: nestjs, express.
- **Пропущено по решению пользователя:** `frontend/` (react, nextjs, state-management, browser-internals, web-performance, frontend-testing), `mobile/` (android, ios, kotlin-multiplatform, react-native).

**Стандарт нового файла:** 30–50 Q, код, ссылки, frontmatter A1–A6, TOC, See also; затем парный JSON-сидер по критериям F–H; обновить `TOC.md`/`README.md`. После каждой пачки — verify.py + gate_clean.

---

## 10. Приложение — историческое
- Детальные пофайловые снимки (305-строчные таблицы оформления/JSON/cyr-ratio из ULTRATHINK.md и INTERVIEW-AUDIT-PLAN.md; per-category статусы READABILITY-AUDIT.md; SEVERE/READABILITY/MINOR списки ULTRATHINK-audit.md) **сохранены в git-истории удалённых файлов** — восстановимы `git show <commit>:<file>`. Все перечисленные там дефекты разрешены к 2026-06-16 (см. §0).
- `/compact` недоступна через Tool API — функциональный эквивалент: коммиты между итерациями (история переживает компакцию).
- Память-заметки: `[[project_lane7_lane8_mermaid_mixedscript]]`, `[[project_md_lane6_readability]]`, `[[project_md_prose_russification]]`, `[[project_mcq_quality_audit]]`, `[[feedback_mcq_in_json_only]]`, `[[feedback_mcq_alignment]]`, `[[project_workflow_rate_limit]]`.
