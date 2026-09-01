# LOOP: LINKAGE-FIRST FULL REBUILD MCQ

Используй все доступные и релевантные skills, subagents, MCP-серверы, web/documentation research, CLI-инструменты, Python/Node-скрипты, валидаторы, Git и встроенные возможности Claude Code.

Не ограничивайся `superpowers` или существующим `mcq-quality-fixer`.

Существующие skills, prompts, golden examples и валидаторы являются **материалом для анализа**, а не безусловным источником истины.

Если их правила конфликтуют с главной задачей MCQ — закреплением конкретного материала шпаргалки — пересматривай правила, валидаторы, golden examples и сам skill.

Это prompt для многократного запуска через loop.

## Одна итерация = один MCQ-файл темы, полностью пересобранный с нуля

Не исправляй по 2–3 вопроса за тик.

Не выполняй поверхностный corpus sweep.

В рамках одного тика:

`одна cheatsheet → весь связанный MCQ JSON → linkage audit → factual audit → distractor audit → anti-tell → sections → validators → final review`

После завершения файла переходи к следующему только в следующей итерации.

---

# 1. Главная цель

MCQ в этом проекте — **не независимый банк вопросов для интервью**.

MCQ является проверкой того материала, который пользователь **только что прочитал в конкретном `## Q<N>` шпаргалки**.

Главный контракт:

> Пользователь читает `## Q<N>` и его ответ в Markdown, после чего получает четыре A/B/C/D. Эти четыре варианта должны непосредственно проверять понимание именно этого материала.

Именно это является назначением MCQ.

Не оптимизируй MCQ прежде всего под:

* интересность самостоятельного инженерного кейса;
* широту темы;
* продвинутый production scenario;
* mental-model diversity;
* красивую симметрию;
* surface skeleton;
* длину;
* section budget;
* blind-review;
* сложность Senior+.

Все эти требования вторичны.

---

# 2. Реальная механика приложения — критический инвариант

В UI пользователь видит:

```text
заголовок ## Q<N> из cheatsheet Markdown
+
A / B / C / D из MCQ JSON
```

Пользователь **НЕ видит**:

```json
"question_text"
```

Следовательно:

> `question_text` не является фактическим stem MCQ.

Он не имеет права переопределять, уточнять, расширять или заменять видимый вопрос.

Вся генерация и вся приёмка должны моделировать настоящий UI.

---

# 3. Абсолютный SOURCE-OF-TRUTH

Для блока:

```text
q_number = N
```

единственным содержательным источником того, **что разрешено проверять**, являются:

```text
## Q<N> <видимый вопрос>

<ответ этого Q в cheatsheet>
```

Дополнительно можно читать:

* code examples внутри этого ответа;
* таблицы;
* перечисления;
* diagrams;
* edge cases;
* явно перечисленные рекомендации.

Соседние `Q<N±1>` можно читать для предотвращения дублей, но нельзя молча использовать их материал для ответа текущего `Q<N>`.

---

# 4. Главное правило answerability

Для каждого блока задай вопрос:

> Может ли пользователь выбрать правильный вариант, используя только материал `## Q<N>`, который он только что прочитал?

Если ответ:

* «нет»;
* «нужен ещё один факт»;
* «это известно из документации, но в шпаргалке этого нет»;
* «нужно знать соседний Q»;
* «нужно понимать production-сценарий, которого в тексте нет»;

блок **FAIL**.

Даже если correct фактически идеален.

---

# 5. Главное правило distractor refutability

Недостаточно, чтобы correct поддерживался материалом.

Каждый неправильный вариант должен быть **опровержим тем же материалом**.

Для A/B/C/D должно выполняться:

```text
correct:
истинность непосредственно следует из Q<N>

wrong A:
ложность можно установить из Q<N>

wrong B:
ложность можно установить из Q<N>

wrong C:
ложность можно установить из Q<N>
```

Если для отбрасывания distractor пользователь должен знать новый внешний факт:

```text
LINKAGE FAIL
```

---

# 6. Запрещённый тип блока

Категорически запрещена ситуация:

```text
Markdown Q4:
Чем retrieve() отличается от exchange()?

MCQ:
Как UriBuilder кодирует query parameters?
```

Это не:

* adjacent knowledge;
* полезное расширение;
* Senior-level scenario.

Это **другой вопрос под тем же q_number**.

Такой блок переписывать с нуля.

---

# 7. `question_text` больше не управляет генерацией

При проектировании нового блока:

**не начинай с `question_text`.**

Сначала:

1. Прочитай видимый заголовок `## Q<N>`.
2. Прочитай его материал.
3. Выдели проверяемые тезисы.
4. Выбери один тезис.
5. Спроектируй A/B/C/D.
6. Только затем заполни `question_text`.

Предпочтительный вариант:

`question_text` должен быть максимально близкой нормализованной копией реального заголовка `Q<N>`.

Например:

```markdown
## Q4. Чем `retrieve()` отличается от `exchange()`?
```

→

```json
"question_text": "Чем `retrieve()` отличается от `exchange()`?"
```

Не превращай его в новый вопрос.

---

# 8. HARD gate для `question_text`

`question_text` должен:

* совпадать по намерению с visible title;
* проверять тот же предмет;
* не добавлять независимую ось;
* не добавлять provider/version/config, которых нет в title/material;
* не менять «что такое» на «как использовать»;
* не менять сравнение на production troubleshooting;
* не менять API на внутренний механизм;
* не менять theory question на архитектурный scenario.

При возможности используй literal normalized title.

---

# 9. Никогда не проверяй linkage по `question_text`

При LINKAGE REVIEW скрывай `question_text`.

Reviewer получает:

```text
VISIBLE STEM:
## Q<N> ...

SOURCE MATERIAL:
<полный материал Q<N>>

OPTIONS:
A
B
C
D
```

Именно это видит пользователь концептуально.

`question_text` нельзя использовать для оправдания варианта.

---

# 10. SOURCE CLAIM MAP

До написания MCQ сформируй внутреннюю карту атомарных тезисов Q.

Например для:

```text
Какие HTTP-клиенты есть в Spring и чем они отличаются?
```

может получиться:

```text
C1: RestTemplate — синхронный legacy/maintenance client.
C2: RestClient — современный синхронный client.
C3: WebClient — реактивный client.
C4: RestClient находится в spring-web.
C5: WebClient используется в reactive stack.
C6: RestClient имеет fluent API.
C7: RestClient не использует Mono/Flux.
C8: для нового blocking Spring 6.1+ кода рекомендуется RestClient.
```

Не добавляй в эту карту факты, которых в Q нет.

---

# 11. Один блок = один anchor claim

Каждый MCQ-блок должен иметь:

```text
source_claim_id
```

или эквивалентную запись в review-sidecar.

Например:

```text
Q1 / block 0
tested_claim = C2 + C3
```

MCQ не должен пытаться проверить всё содержимое большого Q одновременно.

---

# 12. Если на Q только один MCQ-блок

Выбери наиболее центральный тезис Q.

Не выбирай:

* самый редкий edge case;
* деталь примера;
* новый производственный сценарий;
* внутренний механизм, упомянутый вскользь.

Приоритет:

1. тезис, отвечающий прямо на заголовок;
2. основное различие;
3. главное правило;
4. главная рекомендация;
5. важный caveat.

---

# 13. Если одного блока недостаточно

Schema разрешает несколько `blocks`.

Если Q содержит несколько действительно независимых ключевых тезисов, можно создать:

```text
block_idx 0
block_idx 1
...
```

Но каждый блок обязан иметь собственный anchor claim.

Не увеличивай число блоков ради количества.

---

# 14. Новый design workflow блока

Используй порядок:

```text
VISIBLE QUESTION
↓
SOURCE MATERIAL
↓
ATOMIC CLAIMS
↓
TESTED CLAIM
↓
CORRECT PROPOSITION
↓
3 CONTROLLED WRONG MUTATIONS
↓
OPTIONS
↓
SECTIONS
↓
LINKAGE REVIEW
↓
FACT REVIEW
↓
ANTI-TELL REVIEW
```

Не используй старый порядок:

```text
invent question_text
→ invent production scenario
→ mental models
→ normalize length
→ force skeleton
```

---

# 15. Правильный ответ

Correct должен быть:

* прямой проверкой anchor claim;
* точным;
* достаточным;
* не шире материала;
* не уже критически важного смысла;
* понятным без внешнего знания;
* естественным продолжением visible question.

Correct не должен превращаться в мини-конспект всего Q.

---

# 16. Distractors строятся вокруг того же тезиса

Лучший distractor:

> правдоподобно искажает **одну ось того же факта**, который пользователь только что изучил.

Например, если материал говорит:

```text
RestClient — synchronous
WebClient — reactive
```

разумные оси ошибки:

* перепутать клиентов;
* перепутать blocking/reactive;
* перепутать актуальный/legacy status;
* перепутать dependency/module, если это явно есть в Q.

Плохой distractor:

* вводит новый API;
* вводит неизвестный внутренний механизм;
* вводит другой framework;
* вводит production incident, которого не было;
* требует знания вне материала.

---

# 17. SOURCE-BOUNDED distractors

Каждый distractor должен находиться внутри **лексической и понятийной вселенной Q** настолько, насколько это возможно.

Предпочитай:

* термины из Q;
* API из Q;
* числа из Q;
* версии из Q;
* противопоставления из Q;
* механизмы из Q;
* рекомендации из Q.

Можно изменять:

* принадлежность;
* порядок;
* условие;
* default;
* scope;
* направление;
* связь cause/effect;
* конкретное значение.

---

# 18. Single-Delta сохранить, но изменить назначение

Single-Delta полезен.

Но теперь:

> Delta должна быть относительно SOURCE CLAIM, а не относительно самостоятельно придуманного correct.

Пример:

Source:

```text
@OneToMany default = LAZY
@ManyToOne default = EAGER
```

Distractor может поменять:

* `LAZY ↔ EAGER`;
* одну cardinality;
* scope.

Это хорошая controlled mutation.

---

# 19. Заимствованные механизмы — больше не default strategy

Старое правило:

> сильный distractor = реальный механизм другого продукта

слишком легко уводит MCQ за пределы изученного материала.

Использовать такой distractor только если:

* другой механизм явно упомянут в Q;
* пользователь уже видел его в текущем материале;
* его неправильность можно доказать из этого Q.

Иначе не использовать.

---

# 20. Production scenario — только если source содержит scenario

Не добавляй самостоятельный production scenario ради:

* сложности;
* реалистичности;
* Senior-level;
* равной длины.

Scenario разрешён, если Q сам содержит:

* кодовый пример;
* ситуацию;
* incident pattern;
* практический выбор;
* конкретный failure mode.

Тогда MCQ может проверять его.

---

# 21. Не превращай reinforcement в новый экзамен

Цель текущего MCQ:

```text
retrieval practice
+
discrimination
+
закрепление
```

а не:

```text
новый материал
+
дополнительная лекция
+
скрытый второй уровень экзамена
```

После чтения Q пользователь должен думать:

> «Да, это ровно то, что я только что учил».

А не:

> «Тема вроде та же, но откуда я должен был это знать?»

---

# 22. LINKAGE LEVEL

Каждый блок оцени по шкале:

## L4 — DIRECT REINFORCEMENT

Correct и ложность всех distractors напрямую следуют из Q.

## L3 — ANCHORED PARAPHRASE

Формулировки новые, но проверяется ровно тот же тезис без дополнительного знания.

## L2 — ADJACENT KNOWLEDGE

Тема связана, но требуется новый факт.

## L1 — TOPIC ONLY

Совпадает только технология или раздел.

## L0 — DIFFERENT QUESTION

MCQ фактически проверяет другой вопрос.

Приёмка:

```text
L4 = PASS
L3 = PASS только после reviewer confirmation
L2 = FAIL
L1 = FAIL
L0 = FAIL
```

Целевой корпус должен преимущественно быть L4.

---

# 23. SOURCE EVIDENCE sidecar

Для каждого блока создай или обнови review-sidecar.

Минимум:

```json
{
  "q_number": 4,
  "block_idx": 0,
  "visible_question": "Чем retrieve() отличается от exchange()?",
  "tested_claim": "retrieve обрабатывает статус автоматически, exchange отдаёт полный контроль вызывающему коду",
  "source_evidence": [
    "retrieve() сам трактует статус...",
    "exchange() отдаёт вам объекты запроса и ответа..."
  ],
  "linkage_level": "L4",
  "answerable_from_source": true,
  "options": {
    "A": {
      "supported_or_refuted_by_source": true,
      "delta": "..."
    },
    "B": {
      "supported_or_refuted_by_source": true,
      "delta": "..."
    }
  },
  "novel_required_facts": []
}
```

`source_evidence` держи кратким.

Не копируй всю cheatsheet.

---

# 24. HARD LINKAGE fields

Каждый блок должен иметь внутреннюю проверку:

```text
VIS = visible title matched
SRC = tested claim exists in source
ANS = correct derivable from source
D-A = distractor A refutable from source
D-B = distractor B refutable from source
D-C = distractor C refutable from source
NOV = no required novel fact
AXIS = all four options answer same source-derived question
```

Для `FINAL` обязательно:

```text
VIS ✅
SRC ✅
ANS ✅
D-A ✅
D-B ✅
D-C ✅
NOV ✅
AXIS ✅
```

---

# 25. Linkage имеет более высокий приоритет, чем parity

Новая иерархия:

1. **Прямая связь с visible Q и его материалом.**
2. **Фактическая корректность.**
3. **Answerable from source.**
4. **Ровно один правильный ответ.**
5. **Все wrong refutable from source.**
6. **Согласованность text ↔ sections.**
7. **Правдоподобность distractors.**
8. **Естественный русский.**
9. **Anti-guessability.**
10. **A/B/C/D balance.**
11. **Length / format metrics.**
12. **Косметическая симметрия.**

Нельзя пожертвовать пунктом 1 ради пункта 9.

---

# 26. Surface skeleton больше НЕ HARD gate

Откажись от правила:

> все четыре option.text обязаны иметь буквально один surface skeleton.

Это полезная anti-tell эвристика, но не главный контракт.

Новый статус `SKEL`:

```text
DIAGNOSTIC / SOFT GATE
```

Он должен находить очевидную форму-подсказку, но не заставлять переписывать хороший прямой MCQ в четыре длинных искусственных абзаца.

Допустима естественная разница:

* числа предложений;
* пунктуация;
* длина;
* структура;

если она не позволяет стабильно определить correct.

---

# 27. Не выравнивай варианты ради выравнивания

Запрещено раздувать:

```text
RestClient — синхронный клиент.
```

до длинного production story только потому, что distractors получились длиннее.

Лучше:

* сделать distractors короче;
* сузить тезис;
* убрать лишние детали;
* сделать четыре компактных предложения.

Сначала смысл.

Потом parity.

---

# 28. Минимально достаточная длина

Нет обязательной нижней границы типа:

```text
22 слова
```

для каждого option.

Если вопрос требует короткого ответа — используй короткие варианты.

Если требует сложного сравнения — варианты могут быть длиннее.

Правило:

> option должен быть настолько коротким, насколько возможно, но настолько полным, насколько необходимо.

---

# 29. Не используй fixed prose budget как quality target

Не оптимизируй sections под:

```text
correct 102–109 words
wrong ≥86 words
```

Это статистика старого корпуса, а не продуктовый контракт.

Sections должны быть:

* достаточными;
* специфичными;
* полезными;
* без filler.

---

# 30. Anti-tell сохранить

Продолжай проверять:

* correct systematic longest;
* unique backticks;
* unique code;
* unique number;
* unique version;
* unique colon/semicolon;
* absolute-marker gap;
* unique hedging;
* editorial polish;
* A/B/C/D position distribution.

Но исправляй tell **минимальной смысловой правкой**.

Не переписывай хороший source-bound MCQ в новый сюжет.

---

# 31. Blind style review сохранить

Reviewer получает только:

```text
A
B
C
D
```

без:

* question;
* correct;
* sections.

Он отвечает:

> Можно ли определить correct только по форме?

Если уверенно да — исправить.

Но reviewer не требует буквального одинакового skeleton.

---

# 32. Новый обязательный SOURCE-BLIND reviewer

Добавь отдельного reviewer-subagent.

Ему показать:

```text
VISIBLE Q<N>
SOURCE MATERIAL
A
B
C
D
```

Не показывать:

* `question_text`;
* `correct`;
* sections.

Задачи reviewer:

1. Выбрать ответ исключительно по изученному материалу.
2. Для каждого wrong указать предложение/тезис source, который его опровергает.
3. Найти варианты, для оценки которых source недостаточно.
4. Найти новый material.
5. Оценить linkage L0–L4.

PASS:

* correct выбирается из source;
* каждый wrong опровергается source;
* нет нового обязательного знания;
* уровень L3/L4.

---

# 33. Второй linkage reviewer

Независимый reviewer получает:

```text
VISIBLE QUESTION
OPTIONS
```

и отвечает:

> Все четыре варианта действительно являются возможными ответами именно на этот видимый вопрос?

Это ловит:

* смену предмета;
* hidden stem;
* другие axes;
* scenario drift.

---

# 34. External research имеет другую роль

Официальную документацию продолжай использовать для фактчекинга.

Но external docs нужны для:

> проверить, что шпаргалка и MCQ фактически верны.

Они не дают права:

> добавить в MCQ интересный факт, которого пользователь ещё не изучал.

Если внешний research показывает важный факт, которого нет в Q:

1. Не вставляй его молча в MCQ.
2. Реши, нужен ли он в cheatsheet.
3. При необходимости обнови theory через appropriate skill.
4. Только после обновления source используй его в MCQ.

---

# 35. Если source ошибочен

Не делай MCQ, который противоречит source, даже если MCQ знает «правильнее».

Порядок:

```text
verify source
→ fix source
→ rebuild claims
→ build MCQ
```

Не маскируй theory defect на уровне JSON.

---

# 36. Sections — reinforcement first

Секции после ответа могут быть глубже option text, но их первая задача:

> объяснить изученный тезис.

Correct:

```text
explanation
example
when_to_apply
edge_cases
related
```

Wrong:

```text
what_actually
source_of_confusion
if_it_were_true
how_it_should_be
```

Структуру можно пока сохранить.

Но содержимое должно быть привязано к source.

---

# 37. Correct sections

`explanation`

Сначала объясняет anchor claim.

`example`

Предпочтительно использует пример из source или очень близкую вариацию.

`when_to_apply`

Опирается на практическую рекомендацию source.

Если source не содержит recommendation — не выдумывай длинный новый decision framework.

`edge_cases`

Опирается прежде всего на edge cases source.

Дополнительный verified nuance допустим только после основного объяснения и не должен быть нужен для ответа.

`related`

Может расширять обучение после ответа.

Это подходящее место для соседней темы.

---

# 38. Wrong sections

`what_actually`

Исправляет конкретную source-derived ошибку.

`source_of_confusion`

Объясняет plausible misconception.

`if_it_were_true`

Показывает следствие ошибки.

Не превращай его в новый production scenario на пол-экрана.

`how_it_should_be`

Возвращает пользователя к anchor claim из source.

---

# 39. Не используй sections для оправдания плохого option

Если option невозможно понять или опровергнуть из Q, хороший `what_actually` его не спасает.

Linkage оценивается прежде всего по:

```text
visible question + option.text
```

Sections идут после ответа.

---

# 40. Golden examples нужно переаудировать

Существующие `golden-examples.md` создавались прежде всего для:

* parity;
* mental models;
* Single-Delta;
* readability;
* anti-tell.

Они **не являются автоматически golden по linkage**.

Перепроверь каждый пример:

```text
golden option ↔ actual cheatsheet Q
```

Создай новый раздел или файл:

```text
docs/mcq-quality/golden-linkage-examples.md
```

Golden linkage example обязан содержать:

1. visible Q;
2. source fragment;
3. tested claim;
4. four options;
5. почему каждый option source-bound;
6. почему wrong refutable из source.

---

# 41. Anti-golden linkage examples

Зафиксируй реальные плохие примеры.

Обязательно включи класс:

```text
VISIBLE:
Чем retrieve() отличается от exchange()?

MCQ:
Как построить URI с queryParam?
```

Тип дефекта:

```text
L0 DIFFERENT QUESTION
```

Добавь также:

* topic-only;
* adjacent knowledge;
* scenario drift;
* new API drift;
* hidden question_text drift.

---

# 42. Обнови `mcq-quality-fixer`

После проверки новой методологии перепиши skill.

В skill должно быть явно:

> Visible `## Q<N>` + body — primary MCQ contract.

И явно запрещено:

> проектировать MCQ вокруг самостоятельного `question_text`.

Порядок workflow в skill должен начинаться с source claim extraction.

---

# 43. Обнови loop prompt

Старый ROUND-9 prompt не должен оставаться активным источником conflicting rules.

Либо:

* замени его;
* либо создай новый ROUND с явным supersede;
* либо пометь старый как historical.

Не оставляй одновременно:

```text
SKEL = HARD
```

и:

```text
SKEL = SOFT
```

в двух активных authoritative документах.

---

# 44. Валидаторы: linkage становится первым классом

Добавь новый validator, например:

```text
audit/mcq/linkage_scan.py
```

Он не заменяет LLM-review, но должен давать deterministic signals.

---

# 45. Что измерять автоматически

Минимум:

### Structural

```text
q_number exists in Markdown
```

### Visible stem alignment

Сравнивать:

```text
question_text ↔ visible title
```

Если `question_text` сильно ушёл — warning/error.

### Option lexical source coverage

Аналог текущего:

```text
opt_in_material
```

Но использовать как diagnostic, не как абсолютную semantic truth.

### Novel terminology

Искать значимые technical tokens/options, отсутствующие в source.

### Neighbor leakage

Проверять, не состоит ли option преимущественно из терминов соседнего Q.

---

# 46. Лексическая метрика — не hard acceptance

Нельзя сказать:

```text
coverage 0.40 = good
coverage 0.39 = bad
```

Парафраз может иметь низкое lexical overlap и быть L4.

Поэтому lexical metric:

```text
risk signal
```

а semantic source review:

```text
acceptance gate
```

---

# 47. Но экстремально низкая coverage требует review

Например текущие значения:

```text
0.026
0.043
0.065
```

автоматически отправлять на обязательный manual/LLM linkage review.

Не принимать такие блоки без доказанного source mapping.

---

# 48. Не доверяй старым summary-цифрам

Каждый новый ROUND самостоятельно пересчитывает:

* число файлов;
* число Q;
* число blocks;
* linkage medians;
* L0/L1/L2/L3/L4;
* zero-title-overlap;
* source coverage.

Не копируй цифры из старых README.

Если README и generated report расходятся:

* generated current report имеет приоритет;
* README обновить.

---

# 49. Новый review-sidecar

Для каждого topic:

```text
docs/mcq-quality/reviews/<topic_slug>.json
```

На каждый block:

```json
{
  "q_number": 1,
  "block_idx": 0,
  "linkage": {
    "visible_question_match": true,
    "source_claim_id": "C2",
    "tested_claim": "...",
    "answerable_from_source": true,
    "distractors_refutable_from_source": {
      "A": true,
      "B": true,
      "C": true,
      "D": true
    },
    "novel_required_facts": [],
    "same_semantic_axis": true,
    "level": "L4",
    "reviewer_pass": true
  },
  "factual": {
    "pass": true
  },
  "answer_quality": {
    "unique_correct": true,
    "plausible_distractors": true,
    "single_delta": true
  },
  "form": {
    "blind_pass": true,
    "parity_pass": true
  },
  "sections": {
    "consistent": true
  },
  "final": true
}
```

---

# 50. Новый порядок gates

Для каждого блока запускать gates строго так:

## Gate 1 — LINKAGE

```text
visible Q
source
source claims
answerability
distractor refutability
same axis
novel facts
```

FAIL → переписать.

Не переходить дальше.

## Gate 2 — FACTS

Проверить source + MCQ.

## Gate 3 — UNIQUE ANSWER

Ровно один correct.

## Gate 4 — DISTRACTOR QUALITY

Wrong plausible и с одной ошибкой.

## Gate 5 — SECTIONS

Text ↔ sections.

## Gate 6 — NATURAL RUSSIAN

## Gate 7 — ANTI-TELL

Length/style/form.

## Gate 8 — POSITION BALANCE

Такой порядок принципиален.

---

# 51. После anti-tell правки повторить linkage

Очень важно.

Старый corpus мог уходить от source именно во время:

* удлинения wrong;
* выравнивания length;
* создания scenario;
* добавления caveats;
* выравнивания skeleton.

Поэтому:

> После ЛЮБОЙ substantive правки option.text ради parity снова выполнить LINKAGE gate.

Иначе linkage может быть разрушен поздней редактурой.

---

# 52. Distractor quality без ухода из source

Правдоподобность не означает энциклопедическую сложность.

Хороший distractor может быть простой перестановкой source dimensions.

Примеры:

### Classification inversion

```text
A — synchronous
B — reactive
```

### Default inversion

```text
@OneToMany default LAZY
↔
@OneToMany default EAGER
```

### Responsibility inversion

```text
retrieve handles 4xx/5xx
↔
exchange handles automatically
```

### Lifecycle inversion

```text
before
↔
after
```

### Scope error

```text
all
↔
only one category
```

Это достаточно сильные MCQ, если они проверяют именно изученный тезис.

---

# 53. Не требуй Senior production story в каждом вопросе

Уровень банка определяется качеством материала и различением понятий.

Не каждый Q должен превращаться в:

> «Представьте production-кластер из 17 nodes...»

Если source — определение API, MCQ может честно проверять определение API.

Сложность должна соответствовать source.

---

# 54. Question difficulty must not exceed source difficulty

Если шпаргалка Q объясняет базовое различие, MCQ не должен внезапно требовать knowledge уровня implementation internals.

Правило:

```text
MCQ difficulty <= material-supported difficulty
```

Иначе это новый материал.

---

# 55. Если source слишком простой для хорошего MCQ

Не выдумывай новый сложный вопрос.

Варианты:

1. Сделать простой, но качественный reinforcement MCQ.
2. Улучшить cheatsheet через `interview-writer`, если материал объективно слаб.
3. После улучшения source создать более глубокий MCQ.

---

# 56. Полный RESET текущих generated outputs

Для корпуса, созданного старой FULL REBUILD / ROUND-9 методологией, старый PASS не переносить.

В частности, 12 файлов из review kit считать:

```text
UNTRUSTED FOR LINKAGE
```

Не использовать их как content baseline.

Можно использовать:

* как anti-examples;
* для поиска потенциальных misconceptions;
* для factual comparison.

Но финальный текст каждого файла строить заново от source cheatsheet.

---

# 57. Историю Git не удалять

Reset означает сброс **приёмки**, а не уничтожение истории.

Не использовать:

```text
git reset --hard
git clean -fd
```

Не удалять пользовательскую работу.

---

# 58. Reset manifest

Создай:

```text
docs/mcq-quality/linkage-reset-manifest.json
```

Для каждого файла:

```json
{
  "topic_slug": "...",
  "status": "TODO",
  "old_linkage_acceptance_carried": false,
  "source_file": "...",
  "mcq_file": "..."
}
```

Ни один старый `FINAL ✅` не переносить без нового linkage audit.

---

# 59. Новый loop: один файл за тик

Каждая итерация обрабатывает **ровно один topic JSON полностью**.

Не один вопрос.

Не 10 случайных блоков из разных тем.

Не несколько файлов поверхностно.

---

# 60. Выбор следующего файла

Приоритет:

1. Самая низкая linkage coverage.
2. L0 mismatch.
3. L1 topic-only.
4. L2 adjacent knowledge.
5. Сильный `question_text` drift.
6. Multiple defensible answers.
7. Factual issues.
8. Weak distractors.
9. Form tells.
10. Уже хороший файл для final linkage audit.

Для review-kit первым кандидатом является:

```text
spring-rest-client-interview
```

поскольку linkage там особенно слабая.

---

# 61. Workflow одной итерации

## Step 1 — Git

```text
git status
git diff
branch
```

## Step 2 — Read source

Прочитать cheatsheet темы полностью.

## Step 3 — Inventory visible Q

Выгрузить:

```text
Q1
Q2
...
```

## Step 4 — Build claim map

Для каждого Q выделить atomic claims.

## Step 5 — Read existing MCQ only as anti-baseline

Не править построчно.

Определить:

* L0;
* L1;
* L2;
* useful misconception;
* factual defect.

## Step 6 — Rebuild JSON from scratch

Сохранять:

* `topic_slug`;
* q_number linkage;
* schema.

Содержимое blocks пересоздавать.

## Step 7 — Set question_text from visible title

Не придумывать новый stem.

## Step 8 — Build source-bound options

## Step 9 — Write sections

## Step 10 — LINKAGE review

## Step 11 — Factual review

## Step 12 — Four-option unique-answer review

## Step 13 — Anti-tell review

## Step 14 — Re-run LINKAGE

## Step 15 — Validators

## Step 16 — Loader/tests

## Step 17 — Sidecar

## Step 18 — Compare file with source

## Step 19 — Mark AUDITED

Только после полного файла.

---

# 62. Full rewrite means full rewrite

Не считать файл пересобранным, если модель:

* сохранила большинство старых options;
* поправила question_text;
* уменьшила длину;
* заменила пару distractors.

Каждый блок надо заново спроектировать от source claim.

Старый текст можно оставить только если после source-first review он объективно является лучшей формулировкой.

Это должно быть исключением, а не default.

---

# 63. Проверка всего файла после rebuild

Для каждого Q:

```text
visible title ↔ block
source body ↔ tested claim
tested claim ↔ correct
source body ↔ each wrong
```

Затем проверить cross-Q:

* не забрал ли Q5 материал Q6;
* не дублируются ли blocks;
* не раскрывает ли один MCQ другой;
* равномерно ли покрываются ключевые тезисы.

---

# 64. Не допускать neighbor leakage

Особенно опасный дефект:

```text
Q5 MCQ
```

по сути проверяет материал:

```text
Q6
```

Даже если всё находится в одном cheatsheet.

Каждый block должен быть локально привязан к своему Q.

---

# 65. Multi-block rules

Если у Q несколько blocks:

* каждый тестирует отдельный source claim;
* они не дублируются;
* один не раскрывает другой;
* все остаются answerable from same Q material.

---

# 66. Correct position balance

Сохрани A/B/C/D balance.

Но position balance выполняется **после** content acceptance.

Не менять факты ради распределения.

При перестановке переносить весь option object:

```text
text
correct
sections
```

---

# 67. Schema сохранить

Текущая схема:

```text
topic_slug
questions
q_number
blocks
block_idx
question_text
4 options
one correct
role-specific sections
```

может быть сохранена.

Нет необходимости менять runtime contract только для нового linkage workflow.

---

# 68. Loader-aware validation

Поскольку `question_text` не показывается пользователю:

проверь реальный loader и тесты.

Не строить новый quality workflow на поле, которое runtime игнорирует.

При необходимости добавить contract test, фиксирующий эту механику, чтобы будущий prompt снова не начал считать `question_text` UI stem.

---

# 69. Добавь test на visible linkage contract

Автоматически проверить минимум:

```text
каждый q_number существует в Markdown
question_text соответствует title
нет orphan MCQ
нет missing Q
```

Semantic linkage остаётся LLM/review gate.

---

# 70. Linkage report после файла

После rebuild генерируй report:

```text
topic
questions
blocks
L4
L3
L2
L1
L0
novel_fact_blocks
question_text_drift
low_lexical_coverage
manual_review
```

У принятого файла:

```text
L2 = 0
L1 = 0
L0 = 0
novel_required_fact = 0
```

---

# 71. Corpus-level acceptance

После обработки всех файлов:

* повторно посчитать linkage;
* найти semantic outliers;
* проверить question_text drift;
* проверить neighbor leakage;
* проверить duplicate tested claims;
* проверить A/B/C/D;
* проверить factual freshness;
* проверить parity;
* проверить sections.

---

# 72. Новый benchmark корпуса

Главная corpus metric больше не:

```text
tell_pct = 0
```

Главная:

```text
100% blocks answerable from their own Q material
```

Anti-tell остаётся обязательным, но вторичным.

---

# 73. Нельзя оптимизировать metric вместо продукта

Не пытайся искусственно повысить `opt_in_material`:

* копированием предложений;
* повторением терминов;
* keyword stuffing.

Цель — semantic linkage.

Лексическая coverage только помогает найти подозрительные блоки.

---

# 74. Natural paraphrase разрешена

Correct не обязан дословно копировать шпаргалку.

Полезна retrieval practice через переформулировку.

Но парафраз:

* не меняет смысл;
* не расширяет scope;
* не добавляет requirement;
* не добавляет новый механизм.

---

# 75. Новая модель сложности

Вместо длинного scenario повышай сложность через:

* близкие понятия из source;
* тонкие scope distinctions;
* defaults;
* order;
* lifecycle;
* exact responsibility;
* exception;
* limitations;
* consequence.

То есть через **различение изученных фактов**, а не через добавление нового материала.

---

# 76. Проверка после изменения options ради parity

После любой редакторской правки:

1. Re-run source evidence.
2. Проверить tested claim.
3. Проверить novel facts.
4. Проверить distractor refutability.
5. Затем повторить factual/unique-answer.

Это HARD rule.

---

# 77. Проверка sections после изменения option

После любой правки `option.text` перечитать все его sections.

Старые sections автоматически становятся подозрительными.

Ни один validator не гарантирует их semantic consistency.

---

# 78. Machine validators не закрывают semantic acceptance

Ни один из:

* schema;
* validate_a;
* form_scan;
* parity;
* skel;
* leak scan;
* length metrics;

не способен сказать:

> этот MCQ проверяет материал Q<N>.

`LINKAGE PASS` требует source-aware semantic review.

---

# 79. Adversarial LINKAGE review

После завершения файла запусти отдельного reviewer.

Его задача — попытаться забраковать каждый блок вопросом:

> Какой конкретный фрагмент Q<N> позволяет пользователю понять, почему этот вариант верен или неверен?

Если reviewer не может показать источник:

```text
FAIL
```

---

# 80. Student simulation

Смоделируй реальный процесс:

```text
1. Пользователь не знает тему.
2. Он читает только Q<N>.
3. Он закрывает ответ.
4. Видит visible Q + A/B/C/D.
```

Сможет ли внимательный пользователь ответить?

Это главный user acceptance test.

---

# 81. Запрет на memory outside Q

Даже если пользователь прочитал весь cheatsheet раньше, MCQ должен быть локально корректен.

Не рассчитывай:

> «это было в Q3, поэтому Q8 можно использовать».

Связь `q_number` должна иметь смысл.

---

# 82. Full reset recommendation для текущего review-kit

Текущие `new-mcq` из 12 тем считать нуждающимися в FULL REBUILD.

Причина:

* проблема системна;
* они создавались одним workflow;
* linkage median низкая;
* есть прямые L0 mismatches;
* authoring был ориентирован на invisible question_text;
* поздние parity-гейты могли ещё сильнее уводить options от source.

Не трать итерации на локальный salvage.

---

# 83. Старые MCQ как anti-example corpus

Сохраняй старый текст через Git history.

Не нужно создавать рядом:

```text
*-old.json
*-backup.json
*-fixed.json
```

Для анализа можно временно читать Git/архив.

Рабочий путь должен содержать одну актуальную версию.

---

# 84. Первый новый golden file

После rebuild первого действительно хорошего файла:

* проведи независимый linkage review;
* factual review;
* blind review;
* UX/student simulation.

Если файл проходит, используй его как новый **linkage golden**, но не как шаблон слов/длины.

Golden означает принцип качества, а не cloning style.

---

# 85. Сравнение следующего файла с golden

Сравни:

* source linkage;
* conciseness;
* distractor refutability;
* naturalness;
* unique answer;
* sections;
* anti-tell.

Не сравнивай:

* одинаковое количество слов;
* одинаковый punctuation skeleton.

---

# 86. Обновление methodology

Во время первых итераций разрешается менять:

```text
SKILL
loop prompt
quality standard
golden examples
validators
linkage scripts
review sidecar schema
```

Если опыт первого файла показывает, что правило плохое — исправь methodology.

Не продолжай 100 файлов по ошибочному контракту только ради стабильности процесса.

---

# 87. Но не меняй methodology хаотично

Каждое изменение правила должно отвечать:

```text
Какой реальный дефект оно предотвращает?
```

Если ответа нет — не добавляй правило.

---

# 88. Удаляй obsolete rules

Не оставляй бесконечные слои:

```text
правило
уточнение
исключение
коррекция
коррекция коррекции
```

После принятия новой методологии перепиши authoritative documents цельно.

---

# 89. Не допускай metric gaming

Если модель начинает:

* повторять слова source;
* искусственно укорачивать options;
* создавать одинаковые четыре строки;
* избегать полезных терминов;

ради linkage metric — это defect.

Source linkage семантическая.

---

# 90. Не допускай обратного экстремума

Linkage-first не означает, что distractors должны быть глупыми.

Все три wrong по-прежнему должны:

* выглядеть реалистично;
* быть близкими к correct;
* отражать распространённую ошибку;
* иметь одно ядро ошибки;
* быть профессионально сформулированы.

Но ошибки должны строиться **внутри изученного материала**.

---

# 91. Финальный acceptance блока

Блок `PASS`, только если:

### LINKAGE

* visible question правильно определён;
* tested claim существует в Q;
* correct выводится из Q;
* каждый wrong опровергается Q;
* новый обязательный факт отсутствует;
* все A/B/C/D отвечают именно на visible Q;
* linkage L3/L4.

### FACT

* correct фактически верен;
* source фактически корректен;
* wrong фактически ложны в scope.

### UNIQUE

* ровно один защитимый ответ.

### DISTRACTORS

* plausible;
* разные;
* одна основная ошибка;
* не карикатуры.

### SECTIONS

* согласованы;
* source-first;
* без drift.

### LANGUAGE

* естественный русский.

### FORM

* correct не угадывается по форме.

### STRUCTURE

* schema valid;
* loader valid.

---

# 92. Финальный acceptance файла

Файл `AUDITED`, только если:

* все Q source-mapped;
* все blocks проверены;
* L0 = 0;
* L1 = 0;
* L2 = 0;
* all answerable_from_source = true;
* all distractors_refutable = true;
* novel required facts = 0;
* factual review pass;
* unique answer pass;
* sections pass;
* anti-tell pass;
* A/B/C/D acceptable;
* JSON schema pass;
* loader/tests pass;
* sidecar complete.

---

# 93. Финальный corpus review

После rebuild всего корпуса провести отдельный ROUND:

1. Recalculate current metrics.
2. Linkage semantic audit.
3. Low lexical coverage review.
4. `question_text` drift review.
5. Neighbor-Q leakage.
6. Duplicate tested claims.
7. Fact freshness.
8. Unique answer.
9. Distractor plausibility.
10. Sections.
11. Anti-tell.
12. Correct distribution.
13. Natural Russian.
14. Loader/build.

---

# 94. Условие остановки loop

Loop можно остановить, когда:

* все целевые files `AUDITED`;
* все blocks L3/L4;
* L0/L1/L2 отсутствуют;
* каждый correct source-supported;
* каждый wrong source-refutable;
* нет required novel knowledge;
* нет hidden-stem drift;
* facts verified;
* sections synchronized;
* anti-tell passes;
* schema/tests/build pass;
* current methodology соответствует реальной UI-механике.

Не продолжай добавлять косметические правила, если substantive defects закончились.

---

# 95. Запрещённое поведение

Запрещается:

* проектировать options от `question_text`;
* считать `question_text` пользовательским stem;
* задавать фактически другой вопрос;
* добавлять новый knowledge ради Senior-level;
* добавлять production scenario, которого нет в source;
* использовать соседний Q как скрытый prerequisite;
* использовать external docs как источник нового MCQ knowledge;
* раздувать options ради parity;
* насильно делать один punctuation skeleton;
* считать SKEL важнее linkage;
* считать длину важнее linkage;
* копировать старые PASS;
* чинить 177 blocks локальными косметическими патчами;
* оставлять старые sections после смысловой правки;
* принимать блок только по scripts;
* оптимизировать lexical metric keyword stuffing;
* делать correct единственным полным конспектом;
* делать wrong карикатурами;
* создавать backup-копии файлов вместо Git history.

---

# 96. Git

В начале:

```text
git status
git diff
current branch
```

Не использовать:

```text
git reset --hard
git clean -fd
```

Не уничтожать пользовательские изменения.

Не делать commit/push без отдельного поручения, если текущий loop contract проекта явно не требует атомарных commits.

---

# 97. Отчёт одной итерации

После полного rebuild одного файла вывести:

1. Topic.
2. Source cheatsheet.
3. Q count.
4. Block count before.
5. Block count after.
6. Какие старые blocks были L0/L1/L2.
7. Какие source claims выбраны.
8. Все ли `question_text` синхронизированы с visible titles.
9. L4 count.
10. L3 count.
11. L2/L1/L0 count.
12. Novel required fact count.
13. Linkage lexical diagnostics before/after.
14. Factual corrections.
15. Distractor improvements.
16. Sections changes.
17. Anti-tell results.
18. A/B/C/D distribution.
19. Validators.
20. Loader/tests.
21. Sidecar path.
22. Methodology changes.
23. Остаточные risks.
24. Итоговый status.
25. Следующий файл по linkage priority.

Не выдавай план за сделанную работу.

---

# 98. Главный принцип всего нового ROUND

Всегда возвращайся к пользовательскому сценарию:

> Я только что прочитал этот конкретный ответ шпаргалки. Теперь приложение задаёт мне вопрос, чтобы этот конкретный материал закрепился в памяти.

Если MCQ не выполняет это назначение, всё остальное качество блока не имеет значения.

Главная цепочка:

```text
VISIBLE Q<N>
→ SOURCE MATERIAL
→ SOURCE CLAIM
→ MCQ
→ RETRIEVAL
→ EXPLANATION
```

а не:

```text
TOPIC
→ NEW INTERESTING SCENARIO
→ FOUR BEAUTIFUL OPTIONS
```

Начинай новый ROUND с reset приёмки текущих generated MCQ, обновления authoritative methodology и полного rebuild самого слабого по linkage файла. Для review-kit первым кандидатом является `spring-rest-client-interview`.

---

# 99. СТРУКТУРНЫЙ ПАРИТЕТ — КРИТЕРИЙ ПРИЁМКИ (введён 2026-09-01)

Правильный ответ не должен отличаться от неправильных ПО ВИДУ. Это отдельное
требование приёмки наравне с linkage: блок с идеальной привязкой к телу всё равно
не принимается, если correct угадывается по форме, не читая смысла.

## 99.1 Коридор — абсолютное условие

Для КАЖДОГО дистрактора относительно correct своего блока:

| ось | коридор |
|-----|---------|
| длина в символах | 0.85 – 1.15 |
| число частей фразы (запятые, тире, точки с запятой) | точное равенство |
| число код-спанов в обратных кавычках | разница не больше 1 |
| число слов (после вычёркивания код-спанов) | 0.77 – 1.30 |
| финальная пунктуация | одинакова у всех четырёх |

Проверка: `python3 scripts/mcq-struct-report.py <seed>` — обязана дать
«блоков вне коридора: 0». Гейт `scripts/mcq-tools/gates.sh` зовёт её сам.

Финальную точку выравнивает сборка (`scripts/mcq-tools/build.py`), но РУЧНАЯ
правка текста опции её теряет — после любой правки перегонять гейт целиком.

## 99.2 Сравнительный признак — не выше случайного уровня

`scripts/mcq-struct-scan.py` считает долю блоков, где correct — уникальный
максимум по признаку. Случайный уровень 0.250, порог 0.28. Файл принимается
только при «чисто: ни один визуальный признак не выдаёт правильный ответ».

ВАЖНО ПРО МЕТРИКУ: нельзя считать «блок помечен, если хоть один из признаков
экстремален» — при четырёх признаках случайный уровень такой метрики около 0.94,
и любая цифра ниже неё выглядит успехом, ничего не значащим. Считать долю ПО
КАЖДОМУ признаку отдельно и сравнивать с 0.250.

## 99.3 Как чинить, чтобы не сделать хуже

- correct — уникальный МАКСИМУМ по символам и перевес больше четверти: сокращать
  correct (§27). Раздувание дистракторов под раздутый correct тиражирует дефект.
- перевес небольшой (единицы символов): дешевле и безопаснее удлинить ОДИН
  дистрактор, correct не трогать.
- correct — уникальный МИНИМУМ: укорачивать дистрактор ниже correct, а не
  удлинять correct — иначе он станет максимумом.
- добавление запятой или тире ломает ось «частей фразы»: править без новых
  разделителей.
- после ЛЮБОЙ правки correct — перегон anti-tell целиком: правки регулярно
  заносят в correct абсолютистские обороты («только», «целиком», «лишь»).

## 99.4 Телли, которых не видит ни один гейт

Ловятся только чтением нот source-blind ревьюеров:

- correct — единственный вариант с ПЕРЕЧИСЛЕНИЕМ из трёх элементов, остальные
  построены как «утверждение + следствие». Счётчик частей фразы это пропускает:
  «A, B или C» даёт одну запятую, как и любое двухчастное предложение.
- correct — единственный, кто называет ПОЛНОЕ каноничное имя
  (`spring-boot-starter-data-jpa`), дистракторы идут перифразами (`data-jpa`).
  Счётчик код-спанов равен, а глаз видит разницу.
- дистрактор — ДОСЛОВНОЕ ЗЕРКАЛО correct («уже зарегистрирован» / «ещё не
  зарегистрирован»): сужает выбор до пары ещё до чтения остальных двух.

## 99.5 Состояние корпуса на момент введения критерия

`python3 scripts/mcq-tools/struct_audit.py modules/quiz-app/src/main/resources/seed/mcq`

На 2026-09-01: 9778 блоков в 318 файлах, вне коридора 9283 блока (94.9%),
файлов без единого нарушения — 3 (edge-computing, spring-boot, spring-kafka:
ровно те, что собраны уже под этот критерий). По признакам: символы 0.547,
части фразы 0.488, слова 0.379 при случайном уровне 0.250.

То есть в половине корпуса правильный ответ выдаёт одна только длина. Файлы,
принятые в ROUND-10 ДО 2026-09-01, прошли linkage-приёмку, но структурный
паритет у них не проверялся и в общем случае не выполняется.
