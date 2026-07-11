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
>
> ## 🔄 RESET #3 — 2026-07-07 (Каноничный свод из 20 правил — ROUND 4)
>
> Пользователь задал **строгий эталон** (`reactive/rxjava-interview.json`, 46 блоков) и **каноничный
> свод из 20 правил формирования неправильных вариантов**. Правила **жёстко закреплены** в
> `mcq-quality-fixer` (раздел «Каноничные 20 правил…» + формула + критерий + 12-пунктовый финальный
> чек), продублированы в workflow-RULES и в gate `interview-writer`. Формула качественного дистрактора:
> `реальное понятие + правдоподобная формулировка + ОДНА точечная смысловая ошибка + структура как у
> correct`. Критерий: «звучит похоже на правду, но перепутано конкретное правило», а не «очевидная ерунда».
>
> **Ключевое изменение методологии:** старый блокет-бан на `всегда/никогда/полностью/гарантированно`
> **отменён** — эти слова допустимы внутри правдоподобного заблуждения (напр. «`Maybe` всегда испускает
> один элемент» — законная подмена кардинальности). Запрет §7 канона — про **асимметрию** (категоричность
> только у дистракторов при осторожном correct), а не про слово. Группа `absolute` удалена из
> `audit-mcq-parity.py`; колонка `Cabs` убрана из матрицы. Токсично-абсурдные группы (toxic/absurd/
> dismissive/fake) остаются.
>
> **Прогресс сброшен → ROUND 4.** Бар поднят с «caricature-only» до полного 20-правильного стандарта:
> `CAR0` больше **НЕ** означает «готово» — файл, прошедший round-3 caricature-гейт, может нарушать
> правила 2/4/9/10/11/16. Все статусы таблицы §7 → `QUEUED` под ROUND 4. Эталон и первый готовый файл —
> `rxjava-interview` (db720dc3). Все коммиты round 2/3 остаются в git-истории. Порядок перебора —
> worst-first по остаточным сигналам, воркфлоу-бэндами ≤3 concurrent с offset против параллельной сессии.
>
> **↻ Директива ПОВТОРНО подтверждена 2026-07-07 (ultracode-режим):** пользователь заново прислал тот же
> эталон rxjava + свод 20 правил. Проверено: (1) 20 правил в `mcq-quality-fixer` — 246 строк, полны;
> (2) `rxjava-interview` совпадает с эталоном verbatim (q1/q15/q23/q31/q44 сверены). Codify+gold —
> **интактны**. **Reset подтверждён:** НИ ОДИН файл не считается «готовым» под 20-rule (length-tell — лишь
> 1 из 20 правил; 13 length-done файлов остаются в очереди на rule 2/4/9/11/16). Рестарт «переделать все»
> теперь **workflow-driven**: worst-first бэнды, агенты (general-purpose) генерят + adversarial-verify
> structured patch-sets → главный цикл применяет+гейтит+коммитит по одному. 299 файлов в очереди.
>
> ## 🔄 RESET #4 — 2026-07-07 (тот же 20-rule канон, свод + gold присланы дословно в третий раз → ROUND 5)
>
> Пользователь третий раз прислал **дословный** свод 20 правил + **полный gold** (`rxjava-interview`, 46
> вопросов, `corrected_incorrect_options`) и распорядился: (1) правила **жёстко/чётко/подробно** закрепить
> в скилле-генераторе; (2) **сбросить весь прогресс в таблице**; (3) **заново переделать ВСЕ** interview-файлы.
> Выполнено: (a) `mcq-quality-fixer` §«Каноничные 20 правил» **переподтверждён** как единственный стандарт,
> явно привязан к этому reset'у и к 12-пунктовой финальной проверке (§20); (b) gold `rxjava-interview`
> сверен на диске — распакованный source **совпадает дословно** с присланным (138 дистракторов, `changed=0`,
> schema OK); (c) **прогресс сброшен → ROUND 5**: ROUND-4 бэнды #1–#5 (30 файлов/180 блоков/538 дистракторов)
> закоммичены и остаются в git, но под свежим сводом считаются **НЕ готовыми** — все файлы снова в очереди
> worst-first, **без исключения** ранее-обработанных. Guard «сброс один раз» — **ОТМЕНЁН** прямой директивой.
> Band#6 (in-flight, verify-hang) — **остановлен и отброшен** до коммита: его 6 файлов остаются current-worst
> и будут переделаны как ROUND-5 band#1 под новым стандартом (чистая lineage, без old-rules churn).
> Очередь ROUND 5 — все ~318 файлов, worst-first, workflow-бэндами ≤3 concurrent (offset против парал. сессии).
>
> ## 🔄 RESET #5 — 2026-07-11 (GOLDEN-EXAMPLES — единый эталон вкуса)
>
> Пользователь прислал **`docs/golden-examples.md`** — 12 golden-примеров MCQ-блоков (`CascadeType`,
> `orphanRemoval`, N+1, `@ManyToMany`, `@OneToOne`, Hibernate-ошибки, функц. интерфейс,
> `@FunctionalInterface`, лямбда vs аноним, `Function/Consumer/Supplier/Predicate`,
> `Runnable/Callable/Supplier`, кастомный SAM) + anti-golden + шаблоны дистракторов + 6-проверочный
> стандарт качества (Option / Plausibility / Russian-Readability / **Visual Format** parity + Single-Delta +
> «не ответы, а mental models») и распорядился: (1) **положить файл в проект** (`docs/golden-examples.md`);
> (2) **скилл должен сверяться с ним при каждом редактировании** MCQ (закреплено в `mcq-quality-fixer`);
> (3) **внести проверку с этим файлом в таблицу**, **сбросить весь старый прогресс** и **удалить старые
> прогоны** — работаем **заново по каждому файлу**.
>
> Выполнено: (a) `docs/golden-examples.md` в репозитории; (b) `mcq-quality-fixer` — новый раздел
> «Golden Examples (АВТОРИТЕТНЫЙ few-shot эталон)» + пункт в «Checklist before finishing» требуют читать
> golden-examples.md перед КАЖДОЙ правкой и прогонять 8-вопросный «убери correct» глаз-тест; (c) §7
> заменён на **свежую reset-таблицу** `scripts/gen-interview-plan.py` — один ряд на interview-сидер,
> колонка **Golden** (⬜ QUEUED · 🔄 WIP · ✅ DONE) = статус проверки файла против golden-examples.md;
> все статусы сброшены в ⬜; (d) старые прогоны (§8 ROUND 3/4/5/6) **удалены** — начинаем чистый прогон.
>
> **Главный критерий приёмки файла (golden):** если убрать поле `correct`, правильный ответ **нельзя
> угадать глазами** — ни по длине, ни по пунктуации, ни по числу `backticks`, ни по структуре, ни по
> качеству редактуры. correct отличается **только истинностью**. Все 4 варианта — правдоподобные mental
> models одного уровня зрелости, естественный русский, без карикатур и машинных `→`-цепочек.

## 1. Правило (закреплено в skills)

| Skill | Где | Что закреплено |
|---|---|---|
| `mcq-quality-fixer` (канон) | `### Option Parity (MANDATORY — the strongest rule)` | Бар по длине/форме/claim-count/structure-mirror; «raise distractors, don't shrink correct»; **Structural-signature tell** (correct не единственный с `→`/`:`/`;`/backtick-перечислением); **Cross-block consistency** (correct не систематически длиннейший — цель ~25%, не ~90%) |
| `mcq-quality-fixer` (канон) | `### Каноничные 20 правил формирования дистракторов (АВТОРИТЕТНЫЙ ИСТОЧНИК — 2026-07-07)` | **Полный свод из 20 правил** (RESET #3): одна ошибка на дистрактор, близкие понятия, реальный API, паритет длины/стиля, без маркеров правильности (асимметрия), без абсурда/частично-верных/спорных, типовые модели ошибок, разные ошибки по вариантам, соответствие вопросу и уровню + формула + 12-пунктовый финальный чек. Эталон: `reactive/rxjava-interview.json` |
| `interview-writer` | секция MCQ-quality-rules (~line 314) | «Option Parity (обязательно)» + ссылка на канон при MCQ-sync |
| `interview-options-writer` (deprecated) | `### Length-ratio cap` (~line 631) | Синхронизировано для legacy-аудита; для нового контента НЕ использовать |

## 1b. ОГРОМНЕЙШАЯ ТАБЛИЦА — все правила из промптов (каталог)

Полный каталог правил из всех 4 промптов цикла (Option Parity → Plausibility Parity → Russian Readability Parity). Каждая строка — атомарное правило с источником, типом (MUST/FORBID/HOW/DoD), проверкой и статусом закрепления в skills.

| # | Промпт-раунд | Категория | Правило | Тип | Как проверяю | В skills |
|---|---|---|---|---|---|---|
| R1.1 | Option Parity | Похожесть | Все 4 варианта похожи по длине | MUST | `LEN_SPREAD` max/min ≤1.5; length-tell=0 | ✅ mcq-quality-fixer |
| R1.2 | Option Parity | Похожесть | Все 4 похожи по грамматической форме | MUST | ручной/адверс-ревьюер | ✅ |
| R1.3 | Option Parity | Похожесть | Все 4 похожи по числу технических утверждений | MUST | `TECH_DENSITY_GAP`/`WORD_COUNT_GAP` | ✅ |
| R1.4 | Option Parity | Похожесть | Все 4 похожи по уровню конкретики | MUST | `NUMBER_GAP`/`BACKTICK_GAP` | ✅ |
| R1.5 | Option Parity | Похожесть | Все 4 похожи по структуре рассуждения | MUST | `ONLY_ONE_*` симметрия | ✅ |
| R1.6 | Option Parity | Похожесть | Все 4 несут условия/причины/последствия сопоставимо | MUST | адверс-ревьюер | ✅ |
| R1.7 | Option Parity | Похожесть | Все 4 — один уровень зрелости по стилю | MUST | адверс-ревьюер | ✅ |
| R1.8 | Option Parity | Запрет | correct НЕ единственный с алгоритмом | FORBID | `ONLY_ONE_ALGORITHM_OPTION` | ✅ |
| R1.9 | Option Parity | Запрет | correct НЕ единственный со списком шагов | FORBID | `ONLY_ONE_SEQUENCE_OPTION` | ✅ |
| R1.10 | Option Parity | Запрет | correct НЕ единственный с несколькими точными числами | FORBID | `NUMBER_GAP` | ✅ |
| R1.11 | Option Parity | Запрет | correct НЕ единственный с несколькими backtick-терминами | FORBID | `BACKTICK_GAP` | ✅ |
| R1.12 | Option Parity | Запрет | correct НЕ выглядит как полный конспект | FORBID | `LEN_AVG`>1.3 | ✅ |
| R1.13 | Option Parity | Запрет | correct НЕ единственный с причинно-следственной цепочкой | FORBID | `ONLY_ONE_CAUSAL_OPTION` | ✅ |
| R1.14 | Option Parity | Запрет | correct НЕ выглядит заметно экспертнее | FORBID | `CORRECT_LONGEST_RATE`/rank | ✅ |
| R1.15 | Option Parity | Баланс | Если correct несёт 3 смысл. элемента — каждый distractor 2–3 | MUST | адверс-ревьюер | ✅ |
| R1.16 | Option Parity | Направление | НЕ сокращать correct до заглушек — ПОДНИМАТЬ distractor-ы | HOW | сравнение длин до/после | ✅ §3 |
| R1.17 | Option Parity | Направление | Детализацию correct → в `sections` (не терять факты) | HOW | факты 1:1 vs .md | ✅ |
| R1.18 | Option Parity | Направление | Каждый wrong — правдоподобная ошибочная модель той же формы | HOW | адверс-ревьюер | ✅ |
| R1.19 | Option Parity | Инвариант | Ровно 1 `correct: true`, JSON schema цела | MUST | verify-mcq-json | ✅ |
| R2.1 | Plausibility | Принцип | Option Parity без Plausibility Parity недостаточен | MUST | `--caricature` audit | ✅ |
| R2.2 | Plausibility | Смысл | Wrong = реалистичная ошибочная модель middle/senior | MUST | tone-tells=0 + ревьюер | ✅ |
| R2.3 | Plausibility | Чек | Может ли разумный middle/senior так подумать? | MUST | ручной чек | ✅ |
| R2.4 | Plausibility | Чек | Есть ли частично верная логика в wrong? | MUST | ручной чек | ✅ |
| R2.5 | Plausibility | Чек | Ошибка тонкая, а не абсурдная | MUST | `absurd_action` группа | ✅ |
| R2.6 | Plausibility | Чек | Не звучит как «злодейский менеджер» | FORBID | `toxic_management` группа | ✅ |
| R2.7 | Plausibility | Чек | Нет слов-маркеров, палящих неправильность | FORBID | tone-regex | ✅ |
| R2.8 | Plausibility | Чек | Отличается от correct одним смещением, не полной чушью | MUST | ручной чек | ✅ |
| R2.9 | Plausibility | Анти-паттерн | **Inflated Caricature Distractor** (раздут, но карикатурен) | FORBID | `INFLATED_CARICATURE` | ✅ |
| R2.10 | Plausibility | Убрать | всегда/никогда/любой ценой/якобы/не спрашивая/сразу записать/заставить/угрожать | FORBID | tone-regex + `--caricature` | ✅ |
| R2.11 | Plausibility | Модели | Реальные ошибочные модели (1:1↔статус, стендап↔1:1, вера в review, mentorship/coaching, масштаб через отчётность, похвала, выгорание↔скука, blameless↔личный фидбэк) | HOW | тематически | ✅ |
| R3.1 | Readability | Приоритет | 1)корректность 2)правдоподобность 3)естеств.русский 4)структура 5)длина | HOW | `--readability` | ✅ |
| R3.2 | Readability | Звучание | Вариант = нормальная фраза инженера/тимлида/ментора | MUST | `LOW_CYRILLIC`/`ENGLISH_RUN` | ✅ |
| R3.3 | Readability | Запрет | Машинные цепочки через `→` без реального алгоритма | FORBID | `→`-подсчёт vs correct | ✅ |
| R3.4 | Readability | Запрет | Искусственно раздутые формулировки (канцелярит/ярлыки) | FORBID | `FILLER_PHRASE`/`LONG_SENTENCE` | ✅ |
| R3.5 | Readability | Запрет | Карикатурные wrong (токсичный тон) | FORBID | `--caricature` | ✅ |
| R3.6 | Readability | Запрет | Чрезмерные маркеры очевидной неправильности (просто/сразу/ничего не делать/без амбиций/оставить в покое) | FORBID | tone-regex | ✅ |
| R3.7 | Readability | Длина | Сопоставимо, но не до символа (180–240 ок; 260 vs 60 — нет) | MUST | `LEN_SPREAD` | ✅ |
| R3.8 | Readability | Чек | Все 4 читаются без спотыкания и звучат по-русски | MUST | `--readability` | ✅ |
| R3.9 | Readability | Анти-паттерн | **Artificially Balanced but Unreadable Option** | FORBID | `--readability` HIGH | ✅ |
| R3.10 | Readability | Правило | «Лучше короче но естественно, чем длинно но деревянно» | HOW | ручной чек | ✅ |
| R4.1 | Format Parity | Формат | Все 4 варианта — один пунктуационный формат (правило пользователя «тире в общем месте») | MUST | `FORMAT_DASH`/`FORMAT_PERIOD`/`FORMAT_OPEN` | ✅ mcq-quality-fixer |
| R4.2 | Format Parity | Тире | Тире `—` в одной роли у всех или ни у кого; correct не единственный с `—` | MUST | `FORMAT_DASH` | ✅ |
| R4.3 | Format Parity | Точка | Завершающая точка — у всех вариантов или ни у одного (correct не лишний) | MUST | `FORMAT_PERIOD` | ✅ |
| R4.4 | Format Parity | Backtick | Открывающий backtick-идентификатор/стиль обёртки терминов одинаков во всех | MUST | `FORMAT_OPEN`/`BACKTICK_GAP` | ✅ |
| R4.5 | Format Parity | Вопрос | `question_text` красив и корректен под тестирование: один однозначный вопрос, ровно один защитимый ответ, не подсказывает | MUST | ручной/адверс-ревьюер | ✅ |
| R4.6 | Sentence-Structure Parity | Строй | Все 4 варианта по одному синтаксическому шаблону: одинаковый зачин, число клауз, форма сказуемого (глагольная/именная), тип связки (`поэтому`/`так как`/`чтобы`); correct не выделяется грамматическим строем (правило пользователя «один формат И структура предложения») | MUST | ручной/адверс-ревьюер (+`STRUCTURE_DENSITY_GAP` частично) | ✅ mcq-quality-fixer |
| INV.1 | Все | Инвариант | Правим ТОЛЬКО seed/mcq JSON (не .md) | MUST | git pathspec | ✅ §5 |
| INV.2 | Все | Инвариант | Не менять correct/label/order/q_number/question_text | MUST | gate_struct | ✅ §5 |
| INV.3 | Все | Инвариант | Поле `correct` (не is_correct); ensure_ascii=False indent=2 no-NL | MUST | verify-mcq-json | ✅ §5 |
| DoD.1 | Все | DoD | correct не угадать по длине/структуре/насыщенности/стилю | DoD | audit все оси | ▶ per-file |
| DoD.2 | Все | DoD | wrong не заглушки и не раздутые карикатуры | DoD | SHORT+CAR+INF=0 | ▶ per-file |
| DoD.3 | Все | DoD | Все варианты — естественный русский, читаются легко | DoD | `--readability` | ▶ per-file |
| DoD.4 | Все | DoD | Каждый distractor — реальная ошибочная модель | DoD | адверс-fidelity | ▶ per-file |
| DoD.5 | Все | DoD | JSON проходит schema validation | DoD | verify-mcq-json | ▶ per-file |

**mentoring-interview (negative example) — приоритетные вопросы:** Q2 (1-on-1), Q3 (IDP), Q5 (Teaching vs Telling), Q6 (сопротивление фидбэку), Q7 (impostor), Q8 (скучающий senior), Q10 (performance review), Q11 (масштаб менторства), Q12 (отказ от вертикали), Q13 (негативный фидбэк), Q14 (middle на плато). Статус: перепроверен в round 3 (см. §8).

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
| **`docs/golden-examples.md`** | **АВТОРИТЕТНЫЙ few-shot эталон вкуса** — читать ПЕРЕД каждой правкой файла (12 golden-блоков + anti-golden + шаблоны дистракторов + 8-вопросный «убери correct» глаз-тест). Единый стандарт RESET #5 |
| `scripts/gen-interview-plan.py` | Регенерирует таблицу §7 (`--write`; `--reset` сбрасывает статусы в ⬜). Статусы Golden сохраняются между прогонами |
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

## 7. Таблица «файл × golden-проверка» (живой план — новый прогон RESET #5)

**Это и есть план, по которому идёт работа.** Строка = interview-сидер, колонка **Golden** =
статус проверки файла против `docs/golden-examples.md`. Каждый файл приводится к golden-стилю:
все 4 варианта — правдоподобные mental models одного уровня, correct **нельзя угадать глазами**
(8-вопросный «убери correct» тест из golden-examples.md пройден).

Порядок работы за тик: взять верхний ⬜ (worst-first по **Tell**) → collision-guard
(`git status --porcelain`) → прочитать `docs/golden-examples.md` для калибровки → переписать
только `text`/`sections` дистракторов в golden-стиле → гейты (`verify-mcq-json.sh`=0, immutables
без дрейфа) → коммит `pedago(mcq): <stem> — golden-style (N блоков)` → отметить ✅.

Таблица регенерируется детерминированно (статусы сохраняются между прогонами):

```bash
python3 scripts/gen-interview-plan.py --write            # регенерировать, сохранив статусы
python3 scripts/gen-interview-plan.py --write --reset      # + сбросить всё в ⬜ (новый прогон)
```

<!-- INTERVIEW-PLAN:START (auto: scripts/gen-interview-plan.py) -->

_Сводка: сидеров=318 · ✅DONE=6 · 🔄WIP=0 · ⬜QUEUED=312 · остаток визуально-палящих блоков (по не-DONE)=8950. Проверка каждого файла — против `docs/golden-examples.md`._

**Golden** = статус проверки файла против `docs/golden-examples.md` (⬜ QUEUED · 🔄 WIP · ✅ DONE). **Tell** = сколько блоков ещё визуально палят correct (самый длинный / единств. с `:` / больше backtick'ов / единств. с перечислением) — worst-first ключ, диагностика, не авто-гейт.

| # | Сидер | Категория | Бл | Tell | Golden |
|--:|---|---|--:|--:|:--:|
| 1 | `java-concurrency` | java | 56 | 52 | ⬜ |
| 2 | `design-patterns` | design-patterns | 48 | 48 | ⬜ |
| 3 | `postgresql` | databases | 55 | 47 | ⬜ |
| 4 | `sql` | databases | 53 | 47 | ⬜ |
| 5 | `project-reactor` | reactive | 48 | 47 | ⬜ |
| 6 | `hibernate` | databases | 50 | 46 | ⬜ |
| 7 | `java-collections` | java | 46 | 46 | ⬜ |
| 8 | `application-security` | security | 45 | 45 | ⬜ |
| 9 | `authentication-authorization-patterns` | security | 45 | 45 | ⬜ |
| 10 | `hexagonal-architecture` | architecture | 45 | 45 | ⬜ |
| 11 | `kotlin` | kotlin | 45 | 45 | ⬜ |
| 12 | `owasp-top10` | security | 45 | 45 | ⬜ |
| 13 | `test-strategies` | testing | 45 | 45 | ⬜ |
| 14 | `tls-ssl` | security | 45 | 45 | ⬜ |
| 15 | `unit-testing` | testing | 45 | 45 | ⬜ |
| 16 | `spring-security` | spring | 46 | 44 | ⬜ |
| 17 | `cassandra` | databases | 44 | 44 | ⬜ |
| 18 | `elasticsearch` | databases | 44 | 44 | ⬜ |
| 19 | `mongodb` | databases | 46 | 43 | ⬜ |
| 20 | `rxjava` | reactive | 46 | 43 | ⬜ |
| 21 | `helm` | devops | 43 | 43 | ⬜ |
| 22 | `java-oop` | java | 43 | 43 | ⬜ |
| 23 | `jwt` | security | 43 | 43 | ⬜ |
| 24 | `kotlin-collections` | kotlin | 43 | 43 | ⬜ |
| 25 | `kotlin-serialization` | kotlin | 43 | 43 | ⬜ |
| 26 | `networking` | architecture | 43 | 43 | ⬜ |
| 27 | `resilience-patterns` | architecture | 43 | 43 | ⬜ |
| 28 | `saga-pattern` | architecture | 43 | 43 | ⬜ |
| 29 | `spring-batch` | spring | 43 | 43 | ⬜ |
| 30 | `spring-boot-actuator` | spring | 43 | 43 | ⬜ |
| 31 | `spring-cloud` | spring | 43 | 43 | ⬜ |
| 32 | `spring-mvc` | spring | 43 | 43 | ⬜ |
| 33 | `chaos-engineering` | testing | 44 | 42 | ⬜ |
| 34 | `git` | devops | 43 | 42 | ⬜ |
| 35 | `java-annotations` | java | 43 | 42 | ⬜ |
| 36 | `spring-data-jpa` | spring | 43 | 42 | ⬜ |
| 37 | `application-profiling` | performance | 42 | 42 | ⬜ |
| 38 | `argocd` | devops | 42 | 42 | ⬜ |
| 39 | `caching-strategies` | architecture | 42 | 42 | ⬜ |
| 40 | `cap-theorem` | architecture | 42 | 42 | ⬜ |
| 41 | `contract-testing` | testing | 42 | 42 | ⬜ |
| 42 | `database-transactions` | databases | 42 | 42 | ⬜ |
| 43 | `flyway-liquibase` | databases | 42 | 42 | ⬜ |
| 44 | `java-17-21` | java | 42 | 42 | ⬜ |
| 45 | `java-stream` | java | 42 | 42 | ⬜ |
| 46 | `microservices` | architecture | 42 | 42 | ⬜ |
| 47 | `oauth2` | security | 42 | 42 | ⬜ |
| 48 | `openapi-swagger` | api | 42 | 42 | ⬜ |
| 49 | `performance-testing` | performance | 42 | 42 | ⬜ |
| 50 | `terraform` | devops | 42 | 42 | ⬜ |
| 51 | `mockito` | testing | 45 | 41 | ⬜ |
| 52 | `java-8` | java | 42 | 41 | ⬜ |
| 53 | `java-exceptions` | java | 42 | 41 | ⬜ |
| 54 | `logging` | logging | 42 | 41 | ⬜ |
| 55 | `clean-architecture` | architecture | 41 | 41 | ⬜ |
| 56 | `cqrs-event-sourcing` | architecture | 41 | 41 | ⬜ |
| 57 | `database-architecture` | databases | 41 | 41 | ⬜ |
| 58 | `docker` | devops | 41 | 41 | ⬜ |
| 59 | `metrics-tracing` | monitoring | 41 | 41 | ⬜ |
| 60 | `rabbitmq` | messaging | 41 | 41 | ⬜ |
| 61 | `scalability-patterns` | architecture | 41 | 41 | ⬜ |
| 62 | `system-design` | system-design | 41 | 41 | ⬜ |
| 63 | `consistency-patterns` | architecture | 42 | 40 | ⬜ |
| 64 | `observability` | monitoring | 42 | 40 | ⬜ |
| 65 | `spring-framework` | spring | 41 | 40 | ⬜ |
| 66 | `code-review-practices` | leadership | 40 | 40 | ⬜ |
| 67 | `distributed-systems` | architecture | 40 | 40 | ⬜ |
| 68 | `grpc` | api | 40 | 40 | ⬜ |
| 69 | `integration-testing` | testing | 40 | 40 | ⬜ |
| 70 | `java-generics` | java | 40 | 40 | ⬜ |
| 71 | `java-io-nio` | java | 40 | 40 | ⬜ |
| 72 | `java-serialization` | java | 40 | 40 | ⬜ |
| 73 | `kotlin-dsl` | kotlin | 40 | 40 | ⬜ |
| 74 | `kotlin-exceptions` | kotlin | 40 | 40 | ⬜ |
| 75 | `load-balancing` | architecture | 40 | 40 | ⬜ |
| 76 | `scala` | scala | 40 | 40 | ⬜ |
| 77 | `team-leadership` | leadership | 40 | 40 | ⬜ |
| 78 | `technical-debt` | code-quality | 40 | 40 | ⬜ |
| 79 | `testcontainers` | testing | 40 | 40 | ⬜ |
| 80 | `deployment-strategies` | cicd | 39 | 39 | ⬜ |
| 81 | `java-core` | java | 39 | 39 | ⬜ |
| 82 | `kotlin-coroutines` | kotlin | 39 | 39 | ⬜ |
| 83 | `http-rest` | api | 43 | 38 | ⬜ |
| 84 | `redis` | databases | 43 | 38 | ⬜ |
| 85 | `prometheus-grafana` | monitoring | 39 | 38 | ⬜ |
| 86 | `api-gateway` | architecture | 38 | 38 | ⬜ |
| 87 | `gradle-maven` | devops | 38 | 38 | ⬜ |
| 88 | `java-modules` | java | 38 | 38 | ⬜ |
| 89 | `jvm-performance-tuning` | performance | 38 | 38 | ⬜ |
| 90 | `kotlin-interop-java` | kotlin | 38 | 38 | ⬜ |
| 91 | `logging-strategies` | monitoring | 38 | 38 | ⬜ |
| 92 | `pipeline-design` | cicd | 38 | 38 | ⬜ |
| 93 | `websocket` | api | 38 | 38 | ⬜ |
| 94 | `graphql` | api | 40 | 37 | ⬜ |
| 95 | `memory-management` | performance | 39 | 37 | ⬜ |
| 96 | `java-types` | java | 38 | 37 | ⬜ |
| 97 | `jvm` | jvm | 40 | 36 | ⬜ |
| 98 | `arrays-strings` | data-structures | 36 | 36 | ⬜ |
| 99 | `go` | go | 36 | 36 | ⬜ |
| 100 | `inference-optimization` | ai-ml | 36 | 36 | ⬜ |
| 101 | `spring-webflux` | spring | 43 | 35 | ⬜ |
| 102 | `behavioral` | behavioral | 38 | 35 | ⬜ |
| 103 | `fine-tuning-llm` | ai-ml | 35 | 35 | ⬜ |
| 104 | `go-concurrency` | go | 35 | 35 | ⬜ |
| 105 | `test-automation` | testing | 50 | 34 | ⬜ |
| 106 | `apache-spark` | data-engineering | 35 | 34 | ⬜ |
| 107 | `database-sharding` | databases | 34 | 34 | ⬜ |
| 108 | `hash-tables` | data-structures | 34 | 34 | ⬜ |
| 109 | `open-source-llms` | ai-ml | 34 | 34 | ⬜ |
| 110 | `trees` | data-structures | 34 | 33 | ⬜ |
| 111 | `dynamic-programming` | algorithmic-paradigms | 33 | 33 | ⬜ |
| 112 | `kafka` | messaging | 50 | 32 | ⬜ |
| 113 | `spring-boot` | spring | 43 | 32 | ⬜ |
| 114 | `ddd` | architecture | 38 | 32 | ⬜ |
| 115 | `graphs` | data-structures | 34 | 32 | ⬜ |
| 116 | `ai-compliance-governance` | ai-ml | 32 | 32 | ⬜ |
| 117 | `ktor` | jvm-alternatives | 32 | 32 | ⬜ |
| 118 | `linked-lists` | data-structures | 32 | 32 | ⬜ |
| 119 | `two-pointers-sliding-window` | algorithmic-paradigms | 33 | 31 | ⬜ |
| 120 | `typescript` | typescript | 33 | 31 | ⬜ |
| 121 | `ai-application-architecture` | ai-ml | 32 | 31 | ⬜ |
| 122 | `apache-flink` | data-engineering | 31 | 31 | ⬜ |
| 123 | `complexity-analysis` | complexity | 31 | 31 | ⬜ |
| 124 | `database-replication` | databases | 31 | 31 | ⬜ |
| 125 | `java-jackson` | java | 31 | 31 | ⬜ |
| 126 | `multimodal-ai` | ai-ml | 31 | 31 | ⬜ |
| 127 | `quarkus` | jvm-alternatives | 31 | 31 | ⬜ |
| 128 | `searching-algorithms` | sorting-searching | 31 | 31 | ⬜ |
| 129 | `sorting-algorithms` | sorting-searching | 31 | 31 | ⬜ |
| 130 | `kubernetes` | devops | 45 | 30 | ⬜ |
| 131 | `design-ecommerce-delivery` | system-design | 36 | 30 | ⬜ |
| 132 | `linux` | devops | 33 | 30 | ⬜ |
| 133 | `api-design-best-practices` | api | 30 | 30 | ⬜ |
| 134 | `cdn` | architecture | 30 | 30 | ⬜ |
| 135 | `design-dropbox` | system-design | 30 | 30 | ⬜ |
| 136 | `design-feed-system` | system-design | 30 | 30 | ⬜ |
| 137 | `design-key-value-store` | system-design | 30 | 30 | ⬜ |
| 138 | `design-netflix` | system-design | 30 | 30 | ⬜ |
| 139 | `design-pastebin` | system-design | 30 | 30 | ⬜ |
| 140 | `design-payment-system` | system-design | 30 | 30 | ⬜ |
| 141 | `design-rate-limiter` | system-design | 30 | 30 | ⬜ |
| 142 | `design-url-shortener` | system-design | 30 | 30 | ⬜ |
| 143 | `dns` | architecture | 30 | 30 | ⬜ |
| 144 | `dynamodb` | databases | 30 | 30 | ⬜ |
| 145 | `go-stdlib` | go | 30 | 30 | ⬜ |
| 146 | `llm-basics` | ai-ml | 30 | 30 | ⬜ |
| 147 | `llm-evaluation` | ai-ml | 30 | 30 | ⬜ |
| 148 | `long-context-vs-rag` | ai-ml | 30 | 30 | ⬜ |
| 149 | `mcp` | ai-ml | 30 | 30 | ⬜ |
| 150 | `multi-agent-orchestration` | ai-ml | 30 | 30 | ⬜ |
| 151 | `neo4j` | databases | 30 | 30 | ⬜ |
| 152 | `reactive-streams` | reactive | 30 | 30 | ⬜ |
| 153 | `reasoning-models` | ai-ml | 30 | 30 | ⬜ |
| 154 | `service-discovery` | architecture | 30 | 30 | ⬜ |
| 155 | `code-review` | code-quality | 40 | 29 | ⬜ |
| 156 | `java-string` | java | 39 | 29 | ⬜ |
| 157 | `embeddings` | ai-ml | 29 | 29 | ⬜ |
| 158 | `heaps` | data-structures | 29 | 29 | ⬜ |
| 159 | `ai-agents` | ai-ml | 28 | 28 | ⬜ |
| 160 | `ai-observability` | ai-ml | 28 | 28 | ⬜ |
| 161 | `apache-airflow` | data-engineering | 28 | 28 | ⬜ |
| 162 | `clickhouse` | databases | 28 | 28 | ⬜ |
| 163 | `dbt` | data-engineering | 28 | 28 | ⬜ |
| 164 | `design-parking-lot-oo` | system-design | 28 | 28 | ⬜ |
| 165 | `design-youtube` | system-design | 28 | 28 | ⬜ |
| 166 | `go-testing` | go | 28 | 28 | ⬜ |
| 167 | `greedy-algorithms` | algorithmic-paradigms | 28 | 28 | ⬜ |
| 168 | `java-mapstruct` | java | 28 | 28 | ⬜ |
| 169 | `kafka-streams` | data-engineering | 28 | 28 | ⬜ |
| 170 | `llm-integration-patterns` | ai-ml | 28 | 28 | ⬜ |
| 171 | `mlops` | ai-ml | 28 | 28 | ⬜ |
| 172 | `model-serving` | ai-ml | 28 | 28 | ⬜ |
| 173 | `opentelemetry` | monitoring | 28 | 28 | ⬜ |
| 174 | `prompt-engineering` | ai-ml | 28 | 28 | ⬜ |
| 175 | `reactive-testing` | reactive | 28 | 28 | ⬜ |
| 176 | `serverless` | cloud | 28 | 28 | ⬜ |
| 177 | `stream-processing` | data-engineering | 28 | 28 | ⬜ |
| 178 | `tries` | data-structures | 28 | 28 | ⬜ |
| 179 | `vector-databases` | ai-ml | 28 | 28 | ⬜ |
| 180 | `webflux` | reactive | 28 | 28 | ⬜ |
| 181 | `rust` | rust | 33 | 27 | ⬜ |
| 182 | `vertx` | jvm-alternatives | 31 | 27 | ⬜ |
| 183 | `backtracking` | algorithmic-paradigms | 27 | 27 | ⬜ |
| 184 | `clean-code-practices` | code-quality | 27 | 27 | ⬜ |
| 185 | `code-smells` | code-quality | 27 | 27 | ⬜ |
| 186 | `design-instagram` | system-design | 27 | 27 | ⬜ |
| 187 | `design-twitter` | system-design | 27 | 27 | ⬜ |
| 188 | `divide-and-conquer` | algorithmic-paradigms | 27 | 27 | ⬜ |
| 189 | `go-memory-gc` | go | 27 | 27 | ⬜ |
| 190 | `go-modules` | go | 27 | 27 | ⬜ |
| 191 | `java-initialization` | java | 27 | 27 | ⬜ |
| 192 | `java-lombok` | java | 27 | 27 | ⬜ |
| 193 | `recursion` | algorithmic-paradigms | 27 | 27 | ⬜ |
| 194 | `java-conditional-statements` | java | 42 | 26 | ⬜ |
| 195 | `data-lake-lakehouse` | data-engineering | 28 | 26 | ⬜ |
| 196 | `database-performance` | performance | 27 | 26 | ⬜ |
| 197 | `elk-stack` | monitoring | 26 | 26 | ⬜ |
| 198 | `go-generics` | go | 26 | 26 | ⬜ |
| 199 | `istio-service-mesh` | devops | 26 | 26 | ⬜ |
| 200 | `micronaut` | jvm-alternatives | 26 | 26 | ⬜ |
| 201 | `reactive-patterns` | reactive | 26 | 26 | ⬜ |
| 202 | `static-analysis` | code-quality | 26 | 26 | ⬜ |
| 203 | `vault` | devops | 26 | 26 | ⬜ |
| 204 | `design-google-maps` | system-design | 30 | 25 | ⬜ |
| 205 | `message-brokers-comparison` | messaging | 26 | 25 | ⬜ |
| 206 | `ansible` | devops | 25 | 25 | ⬜ |
| 207 | `code-coverage` | code-quality | 25 | 25 | ⬜ |
| 208 | `stacks-queues` | data-structures | 25 | 25 | ⬜ |
| 209 | `function-calling` | ai-ml | 30 | 24 | ⬜ |
| 210 | `design-elevator-oo` | system-design | 26 | 24 | ⬜ |
| 211 | `design-web-crawler` | system-design | 26 | 24 | ⬜ |
| 212 | `cockroachdb` | databases | 24 | 24 | ⬜ |
| 213 | `consul` | devops | 24 | 24 | ⬜ |
| 214 | `design-vending-machine-oo` | system-design | 24 | 24 | ⬜ |
| 215 | `latency-numbers` | architecture | 24 | 24 | ⬜ |
| 216 | `nats` | messaging | 24 | 24 | ⬜ |
| 217 | `network-performance` | performance | 24 | 24 | ⬜ |
| 218 | `supply-chain-security` | security | 24 | 24 | ⬜ |
| 219 | `refactoring-patterns` | code-quality | 42 | 23 | ⬜ |
| 220 | `event-driven-patterns` | architecture | 40 | 23 | ⬜ |
| 221 | `code-agents` | ai-ml | 31 | 23 | ⬜ |
| 222 | `data-warehousing` | data-engineering | 30 | 23 | ⬜ |
| 223 | `design-search` | system-design | 30 | 23 | ⬜ |
| 224 | `caching-performance` | performance | 23 | 23 | ⬜ |
| 225 | `jaeger-zipkin` | monitoring | 23 | 23 | ⬜ |
| 226 | `scylladb` | databases | 23 | 23 | ⬜ |
| 227 | `ai-safety-guardrails` | ai-ml | 32 | 22 | ⬜ |
| 228 | `aws-lambda` | cloud | 32 | 22 | ⬜ |
| 229 | `aws-sqs-sns` | messaging | 22 | 22 | ⬜ |
| 230 | `conflict-stories` | behavioral | 22 | 22 | ⬜ |
| 231 | `culture-fit` | behavioral | 22 | 22 | ⬜ |
| 232 | `failure-stories` | behavioral | 22 | 22 | ⬜ |
| 233 | `leadership-stories` | behavioral | 22 | 22 | ⬜ |
| 234 | `load-testing` | testing | 22 | 22 | ⬜ |
| 235 | `pulsar` | messaging | 22 | 22 | ⬜ |
| 236 | `secrets-management` | security | 22 | 22 | ⬜ |
| 237 | `star-method` | behavioral | 22 | 22 | ⬜ |
| 238 | `technical-decisions` | leadership | 22 | 22 | ⬜ |
| 239 | `cloud-native-patterns` | cloud | 30 | 21 | ⬜ |
| 240 | `reverse-proxy` | architecture | 30 | 21 | ⬜ |
| 241 | `azure` | cloud | 25 | 21 | ⬜ |
| 242 | `spring-aop` | spring | 22 | 21 | ⬜ |
| 243 | `design-chat-system` | system-design | 21 | 21 | ⬜ |
| 244 | `property-based-testing` | testing | 21 | 21 | ⬜ |
| 245 | `loki-grafana` | monitoring | 28 | 20 | ⬜ |
| 246 | `conflict-resolution` | leadership | 20 | 20 | ✅ |
| 247 | `estimations-planning` | leadership | 20 | 20 | ✅ |
| 248 | `linkerd` | devops | 20 | 20 | ✅ |
| 249 | `mtls` | security | 20 | 20 | ✅ |
| 250 | `resilience4j` | spring | 20 | 20 | ✅ |
| 251 | `tech-interviewing` | leadership | 20 | 20 | ⬜ |
| 252 | `gcp` | cloud | 28 | 19 | ⬜ |
| 253 | `zero-trust` | security | 19 | 19 | ⬜ |
| 254 | `redpanda` | messaging | 20 | 18 | ⬜ |
| 255 | `spring-cache` | spring | 18 | 18 | ⬜ |
| 256 | `strangler-fig` | architecture | 18 | 18 | ⬜ |
| 257 | `api-versioning` | api | 20 | 17 | ✅ |
| 258 | `kotlin-flow` | kotlin | 17 | 17 | ⬜ |
| 259 | `rest-maturity` | api | 17 | 17 | ⬜ |
| 260 | `design-uber` | system-design | 30 | 16 | ⬜ |
| 261 | `rag` | ai-ml | 30 | 16 | ⬜ |
| 262 | `spring-ai` | spring | 18 | 16 | ⬜ |
| 263 | `java-reflection` | java | 16 | 16 | ⬜ |
| 264 | `spring-data-jdbc` | spring | 16 | 16 | ⬜ |
| 265 | `spring-events` | spring | 16 | 16 | ⬜ |
| 266 | `spring-scheduling` | spring | 16 | 16 | ⬜ |
| 267 | `spring-validation` | spring | 16 | 16 | ⬜ |
| 268 | `aws` | cloud | 34 | 15 | ⬜ |
| 269 | `design-typeahead` | system-design | 30 | 15 | ⬜ |
| 270 | `spring-testing` | spring | 16 | 15 | ⬜ |
| 271 | `graalvm-native` | jvm | 15 | 15 | ⬜ |
| 272 | `hibernate-caching` | databases | 15 | 15 | ⬜ |
| 273 | `hibernate-relationships` | databases | 15 | 15 | ⬜ |
| 274 | `java-optional` | java | 15 | 15 | ⬜ |
| 275 | `java-pattern-matching` | java | 15 | 15 | ⬜ |
| 276 | `java-records` | java | 15 | 15 | ⬜ |
| 277 | `java-virtual-threads` | java | 15 | 15 | ⬜ |
| 278 | `junit` | testing | 15 | 15 | ⬜ |
| 279 | `kotlin-sealed-classes` | kotlin | 15 | 15 | ⬜ |
| 280 | `kotlin-spring` | kotlin | 15 | 15 | ⬜ |
| 281 | `rest-assured` | testing | 15 | 15 | ⬜ |
| 282 | `spring-async` | spring | 15 | 15 | ⬜ |
| 283 | `spring-boot-3-migration` | spring | 15 | 15 | ⬜ |
| 284 | `spring-graphql` | spring | 15 | 15 | ⬜ |
| 285 | `spring-integration` | spring | 15 | 15 | ⬜ |
| 286 | `spring-kafka` | spring | 15 | 15 | ⬜ |
| 287 | `spring-messaging` | spring | 15 | 15 | ⬜ |
| 288 | `spring-modulith` | spring | 15 | 15 | ⬜ |
| 289 | `spring-r2dbc` | spring | 15 | 15 | ⬜ |
| 290 | `spring-session` | spring | 15 | 15 | ⬜ |
| 291 | `spring-state-machine` | spring | 15 | 15 | ⬜ |
| 292 | `spring-transaction` | spring | 15 | 15 | ⬜ |
| 293 | `spring-vault` | spring | 15 | 15 | ⬜ |
| 294 | `agentic-patterns` | ai-ml | 30 | 14 | ⬜ |
| 295 | `hibernate-jpql-criteria` | databases | 16 | 14 | ⬜ |
| 296 | `kotlin-value-classes` | kotlin | 15 | 14 | ⬜ |
| 297 | `spring-rest-client` | spring | 13 | 13 | ⬜ |
| 298 | `java-completable-future` | java | 15 | 11 | ⬜ |
| 299 | `mentoring` | leadership | 25 | 10 | ⬜ |
| 300 | `java-22-25` | java | 22 | 10 | ⬜ |
| 301 | `algorithms` | algorithms | 16 | 7 | ⬜ |
| 302 | `crac` | jvm | 16 | 6 | ⬜ |
| 303 | `micrometer` | monitoring | 20 | 5 | ⬜ |
| 304 | `mutation-testing` | testing | 20 | 5 | ⬜ |
| 305 | `langchain4j` | ai-ml | 18 | 5 | ⬜ |
| 306 | `jmh-microbenchmarking` | performance | 18 | 4 | ⬜ |
| 307 | `jooq` | databases | 18 | 4 | ⬜ |
| 308 | `bff-pattern` | architecture | 17 | 4 | ⬜ |
| 309 | `selenium` | testing | 15 | 4 | ⬜ |
| 310 | `java-functional-interface` | java | 14 | 4 | ⬜ |
| 311 | `spring-retry` | spring | 17 | 3 | ⬜ |
| 312 | `archunit` | testing | 15 | 3 | ⬜ |
| 313 | `cucumber-bdd` | testing | 15 | 3 | ⬜ |
| 314 | `kotlin-testing` | kotlin | 20 | 1 | ⬜ |
| 315 | `edge-computing` | architecture | 18 | 1 | ⬜ |
| 316 | `apache-camel` | messaging | 16 | 1 | ⬜ |
| 317 | `jms-activemq` | messaging | 16 | 1 | ⬜ |
| 318 | `scala-effects` | scala | 16 | 1 | ⬜ |

<!-- INTERVIEW-PLAN:END -->

## 8. Прогресс

**Новый прогон (RESET #5, старт 2026-07-11, golden-examples.md).** Старые прогоны (ROUND 3/4/5/6)
удалены по указанию пользователя — начинаем чисто. Коммиты прежних раундов остаются в git-истории.

- _(пока пусто — первый golden-файл будет здесь)_

## 9. Координация

Параллельная claude-сессия работает worst-first СВЕРХУ (frontier ranks 12–14, static:
`agentic-patterns`/`git`/`chaos-engineering`). Я беру нижний слой; перед каждым слайсом —
`git status seed/mcq` → skip их dirty-файлов.

