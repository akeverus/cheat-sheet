---
title: "Вопросы на собеседовании: Mutation Testing"
description: "Mutation testing: проверка качества тестов через mutations кода, mutation operators, kill rate, surviving mutants, PIT, Stryker, тулинг для Java/JS/Python, vs code coverage"
tags:
  - interview
  - testing
  - mutation-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Mutation Testing"
  - "Mutation testing interview"
  - "PIT mutation testing interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Mutation Testing`

`Mutation testing` — техника оценки **качества тестов** (а не quality кода). Идея: модифицировать ("**mutate**") код, run tests, check **сколько mutations пойманы** (killed). Surviving mutants = слабые тесты. Главные tools: **PIT** (Java), **Stryker** (JS/TS/.NET/Scala), **mutmut** (Python). Дополняет (не заменяет) coverage.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [PIT Mutation Testing — Pitest.org](https://pitest.org/)
- [Stryker Mutator](https://stryker-mutator.io/)
- [Mutation Testing — Wikipedia](https://en.wikipedia.org/wiki/Mutation_testing)
- [PITclipse (PIT IntelliJ plugin)](https://github.com/hcoles/pitest)
- [Mutation Testing — Baeldung](https://www.baeldung.com/java-mutation-testing-with-pitest)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое mutation testing?](#q1--что-такое-mutation-testing)
- [Q2. (!) Зачем mutation testing если есть coverage?](#q2--зачем-mutation-testing-если-есть-coverage)
- [Q3. Mutant, killed, survived, equivalent?](#q3-mutant-killed-survived-equivalent)

**Mutation operators**
- [Q4. (!) Какие mutations типичные?](#q4--какие-mutations-типичные)
- [Q5. Conditional Boundary Mutator?](#q5-conditional-boundary-mutator)
- [Q6. Math Mutator?](#q6-math-mutator)
- [Q7. Other mutators?](#q7-other-mutators)

**Метрики**
- [Q8. (!) Mutation score (kill rate)?](#q8--mutation-score-kill-rate)
- [Q9. Surviving mutants — как анализировать?](#q9-surviving-mutants--как-анализировать)
- [Q10. Equivalent mutants?](#q10-equivalent-mutants)

**Tools**
- [Q11. (!) PIT для Java?](#q11--pit-для-java)
- [Q12. (!) Stryker для JavaScript/TypeScript?](#q12--stryker-для-javascripttypescript)
- [Q13. mutmut для Python?](#q13-mutmut-для-python)
- [Q14. Other tools (Mutil, Cosmic Ray)?](#q14-other-tools-mutil-cosmic-ray)

**Performance**
- [Q15. (!) Mutation testing медленный — почему?](#q15--mutation-testing-медленный--почему)
- [Q16. Optimization (incremental, in-process)?](#q16-optimization-incremental-in-process)

**Production**
- [Q17. (!) Когда mutation testing worth it?](#q17--когда-mutation-testing-worth-it)
- [Q18. Какой score целевой?](#q18-какой-score-целевой)
- [Q19. (!) Limitations и criticism?](#q19--limitations-и-criticism)
- [Q20. CI/CD integration?](#q20-cicd-integration)

## Q1. (!) Что такое mutation testing?

**Mutation testing** — testing **the tests**.

**Process:**
1. **Mutate** code (small change): `if (x > 5)` → `if (x >= 5)`
2. **Run tests** на mutated code
3. If tests **fail** → mutant **killed** (good, tests caught it)
4. If tests **pass** → mutant **survived** (bad, tests missed it)

**Mutation score (kill rate)** = killed / total mutants.

**Higher score** = better tests.

**Idea:** **if your tests can't detect small code changes, they don't really test anything.**


> [!mcq]
> - [ ] Mutation testing — это просто другое название coverage testing | ❌ ПОСЛЕДСТВИЕ: coverage показывает выполнение кода, mutation — детектирование изменений; путаница приводит к слабым тестам с 100% coverage и низкой quality
> - [x] Mutation score = killed/total mutants; tests с assertNotNull при coverage 100% часто dают low mutation score → слабые assertions | ✓ ПРИМЕНЯТЬ: проверка качества тестов, не только выполнения 📋 ПРАВИЛО: coverage = выполнение; mutation = детектирование 🔗 См. Q2
> - [ ] Mutation testing запускается перед каждым тестом для проверки | ❌ ПОСЛЕДСТВИЕ: mutation очень дорогой (генерирует тысячи мутантов, каждый прогоняется через все тесты); запускается отдельно (nightly, weekly), не в обычном CI
> - [ ] Mutation score должен быть 100% для качественных тестов | ❌ ПОСЛЕДСТВИЕ: 100% недостижимо из-за equivalent mutants; целевой 70-85% — реалистично

## Q2. (!) Зачем mutation testing если есть coverage?

**Coverage:** what code lines executed by tests. **Doesn't measure quality.**

**Example:**
```java
// Coverage: 100%
@Test
void test() {
    int result = calculate(5);
    assertNotNull(result);  // weak assertion
}
```

Coverage хорошее, но если `calculate` returns wrong value — test passes!

**Mutation testing finds this:**
```java
// Mutant: change return value
int calculate(int x) {
    return 999; // mutation
}
```

Test still passes (`assertNotNull(999)` true) → **survived mutant** → weak test.

**Mutation = quality of assertions**, не just execution coverage.


> [!mcq]
> - [ ] 100% coverage = 100% качество тестов | ❌ ПОСЛЕДСТВИЕ: assertNotNull() покрывает строку но не проверяет правильность результата; mutation score выявляет это
> - [x] Coverage = строки выполнены; Mutation = assertions проверяют логику; mutation score = качество тестов | ✓ ПРИМЕНЯТЬ: coverage как baseline, mutation как quality gate в production code 📋 ПРАВИЛО: coverage без mutation = false sense of safety 🔗 См. Q3
> - [ ] Mutation testing нужен только для безопасных систем (банки, медицина) | ❌ ПОСЛЕДСТВИЕ: любой production код выигрывает от качественных тестов; mutation показывает где assertions слабые
> - [ ] Если все тесты проходят без mutation testing — нет смысла его запускать | ❌ ПОСЛЕДСТВИЕ: тесты могут проходить с тривиальными asserts; mutation выявляет fake confidence от coverage метрики

## Q3. Mutant, killed, survived, equivalent?

**Mutant** — modified version code (по mutation operator).

**Killed** — at least one test fails on mutant. Good.
**Survived** — все tests pass. Bad.
**No coverage** — mutant в untested code (не executed).
**Timeout** — mutant causes infinite loop.
**Equivalent** — semantically same as original (cannot be killed). Annoying, see Q10.


> [!mcq]
> - [ ] Все mutants должны быть killed для качества — survived это всегда плохо | ❌ ПОСЛЕДСТВИЕ: equivalent mutants не могут быть killed (semantically same); их survival неизбежен и не означает плохие тесты
> - [x] Killed = test failed (good); Survived = tests passed (test gap); Equivalent = semantically identical (cannot kill); Timeout = infinite loop | ✓ ПРИМЕНЯТЬ: фокусироваться на survived но не equivalent для улучшения тестов 📋 ПРАВИЛО: mutation_score = killed / (total - equivalent) 🔗 См. Q4
> - [ ] Survived mutant — это всегда баг в коде | ❌ ПОСЛЕДСТВИЕ: survived = тесты не покрывают это поведение, не обязательно баг; нужен анализ — добавить тест или это equivalent mutant
> - [ ] Mutation score = killed / total включая equivalent | ❌ ПОСЛЕДСТВИЕ: equivalent в знаменателе занижает score искусственно; стандарт — исключать equivalent из расчёта (если можно их детектировать)

## Q4. (!) Какие mutations типичные?

**Common mutation operators:**

| Operator | Original | Mutation |
|----------|----------|----------|
| **Conditional Boundary** | `<` | `<=` |
| **Conditional Boundary** | `>` | `>=` |
| **Negate Conditional** | `==` | `!=` |
| **Math** | `+` | `-` |
| **Math** | `*` | `/` |
| **Increment** | `++` | `--` |
| **Boolean Return** | `return true` | `return false` |
| **Void Method Call** | `methodCall();` | (removed) |
| **Constant** | `42` | `0` или `1` |

**Each line с operator** → multiple mutants generated.


> [!mcq]
> - [ ] Mutation operators ограничены логическими условиями (if/else) | ❌ ПОСЛЕДСТВИЕ: типичные операторы покрывают арифметику, boolean, return statements, increments, void calls — wide range
> - [x] Conditionals (>=, ==), Math (*,/,+), Increment (++,--), Boolean (true→false), Void Calls (removed), Constant (42→0) — каждая строка может дать несколько мутантов | ✓ ПРИМЕНЯТЬ: PIT по умолчанию использует ALL_MUTATORS набор 📋 ПРАВИЛО: line с operator → N мутантов (по числу типов оператора) 🔗 См. Q5
> - [ ] PIT генерирует один mutant per строку | ❌ ПОСЛЕДСТВИЕ: каждая строка может содержать несколько операторов, каждый — отдельный мутант; total mutants ≫ lines of code
> - [ ] String mutations не поддерживаются — только numeric | ❌ ПОСЛЕДСТВИЕ: PIT поддерживает мутации строк (empty string, null), но они дают много equivalent — потому отключены по умолчанию

## Q5. Conditional Boundary Mutator?

```java
// Original
if (age >= 18) {
    allowAccess();
}

// Mutation 1: >=  →  >
if (age > 18) {  // changes boundary

// Mutation 2: >=  →  ==
if (age == 18) {

// Mutation 3: >=  →  !=
if (age != 18) {
```

**Test:**
```java
@Test
void testAdult() {
    assertTrue(check(18));  // boundary
    assertTrue(check(19));
    assertFalse(check(17));
}
```

**Boundary tests** (18, 17, 19) **kill** boundary mutants. Без них — survive.


> [!mcq]
> - [ ] Тесты только с middle values (age=20, age=15) убивают boundary mutants | ❌ ПОСЛЕДСТВИЕ: middle values далеко от границы; >=18 vs >18 проявляются на 18; нужны boundary tests (17, 18, 19)
> - [x] Boundary tests (17, 18, 19) убивают boundary mutants (>=, >, ==, !=); test on boundary = differentiates >= vs > | ✓ ПРИМЕНЯТЬ: для каждого условия с boundary — тесты на границе и +/- 1 📋 ПРАВИЛО: boundary mutants = boundary tests = на границе и соседних значениях 🔗 См. Q6
> - [ ] Boundary mutator — только для целых чисел | ❌ ПОСЛЕДСТВИЕ: применяется к любым сравнимым типам: дата (before/after), Big Decimal, time intervals; везде где есть border
> - [ ] >= → > мутант одинаков для возрастов и для процентов скидок | ❌ ПОСЛЕДСТВИЕ: семантика разная — для процентов важно >50% (порог), для возраста ≥18 (включительно); каждый случай требует своих boundary tests

## Q6. Math Mutator?

```java
// Original
int total = price * quantity;

// Mutations
int total = price + quantity;  // * → +
int total = price - quantity;  // * → -
int total = price / quantity;  // * → /
```

**Test:**
```java
@Test
void testTotal() {
    assertEquals(20, calculate(5, 4));  // 5*4=20
}
```

If test only `5*4=20` → `5+4=9` mutation **killed** (assertion fails).


> [!mcq]
> - [ ] Один тест 5*4=20 убивает все математические мутации | ❌ ПОСЛЕДСТВИЕ: 5+4=9 это правда, тест провалится; но 5/2=2 (целочисленное деление) тоже отличается от 5*2; нужны разные значения для разных операций
> - [x] Math mutator: * → +, * → -, * → /; assertEquals(20, calc(5,4)) убивает 5+4=9, 5-4=1, но requires разные test cases для разных мутаций | ✓ ПРИМЕНЯТЬ: для каждого арифметического выражения — несколько test cases с разными значениями 📋 ПРАВИЛО: math mutants = test с разными number combinations 🔗 См. Q7
> - [ ] Math mutator работает только для int — не для double | ❌ ПОСЛЕДСТВИЕ: применим к любым numeric типам (int, long, double, BigDecimal); семантика мутаций та же
> - [ ] Деление и умножение взаимозаменяемы — мутация * → / часто equivalent | ❌ ПОСЛЕДСТВИЕ: целочисленное деление совершенно другое (5*4=20 vs 5/4=1); крайне редко equivalent

## Q7. Other mutators?

**Increment/Decrement:**
```java
i++  →  i--
```

**Negate Conditional:**
```java
if (x == 5)  →  if (x != 5)
```

**Remove Conditional:**
```java
if (condition) {  →  // removed
```

**Return Values:**
```java
return true   →  return false
return 0      →  return 1
```

**Void Method Call (delete):**
```java
log.info("Processing");  →  // removed
```

**Each operator** generates множество mutants per line. **Total mutants** can be hundreds-thousands per project.


> [!mcq]
> - [ ] Negate Conditional и Remove Conditional эквивалентны — обе мутации тривиальны | ❌ ПОСЛЕДСТВИЕ: разные семантики: negate (== → !=) меняет логику; remove полностью убирает условие; разные тесты их убивают
> - [x] Increment (++ ↔ --), Negate (== ↔ !=), Remove conditional, Return values, Void Method delete; на каждый оператор — несколько мутантов | ✓ ПРИМЕНЯТЬ: понимать набор мутаторов для интерпретации mutation score 📋 ПРАВИЛО: больше операторов = больше мутантов = жёстче проверка тестов 🔗 См. Q4
> - [ ] Void Method delete не имеет смысла — методы без возврата ничего не делают | ❌ ПОСЛЕДСТВИЕ: void methods могут иметь side effects (logger, audit, repo.save); их удаление часто проходит незаметно — нужны verify-моки
> - [ ] Total mutants примерно равно total lines of code | ❌ ПОСЛЕДСТВИЕ: total mutants часто 5-10x больше LOC из-за нескольких мутаций per line; крупный проект — десятки тысяч мутантов

## Q8. (!) Mutation score (kill rate)?

```
Mutation Score = killed_mutants / (total_mutants - equivalent_mutants)
```

**Higher = better tests.**

**Targets:**
- **80%+ — excellent**
- **60-80% — good**
- **40-60% — okay**
- **< 40% — weak tests**

**100% не реалистично** (equivalent mutants, timeouts).

**Some teams aim 70-85%** в practice.


> [!mcq]
> - [ ] Mutation score 100% означает идеальные тесты | ❌ ПОСЛЕДСТВИЕ: 100% недостижимо из-за equivalent mutants; стремиться к этому = тратить время на невозможное
> - [x] Mutation Score = killed / (total - equivalent); 80%+ excellent, 60-80% good, <40% weak; targets обычно 70-85% в реальной практике | ✓ ПРИМЕНЯТЬ: установить минимальный порог 70% в CI как quality gate 📋 ПРАВИЛО: target 70-85% = реалистично + значимо 🔗 См. Q8
> - [ ] Mutation score 50% — это норма для production кода | ❌ ПОСЛЕДСТВИЕ: 50% означает половина мутантов выживает = слабые тесты; для production нужно 70%+
> - [ ] Mutation score измеряется в строках, не в мутантах | ❌ ПОСЛЕДСТВИЕ: коды покрытие в lines; mutation в мутантах (которых может быть несколько на строку); разные метрики

## Q9. Surviving mutants — как анализировать?

**Each survived mutant** = potential test gap.

**Investigate:**
1. **Real bug?** Add test → mutant killed
2. **Equivalent mutant?** Skip (mark as such)
3. **Acceptable?** Maybe boundary не critical

**Tools generate report:**
```
SurvivedMutant: line 42
  ConditionalBoundary
  if (count >= MIN) → if (count > MIN)
  In: validate()
  No test catches this
```

**Action:** add boundary test для `MIN` (count == MIN should pass).


> [!mcq]
> - [ ] Все survived мутанты нужно сразу убивать дополнительными тестами | ❌ ПОСЛЕДСТВИЕ: equivalent мутанты нельзя убить — потеря времени; нужно сначала классифицировать survived (real gap vs equivalent vs acceptable)
> - [x] Survived → investigate: real bug (add test), equivalent (skip), acceptable (boundary не critical); добавить boundary test для условий | ✓ ПРИМЕНЯТЬ: триаж survived мутантов перед действиями 📋 ПРАВИЛО: survived = test gap candidate, не всегда баг 🔗 См. Q9
> - [ ] Survived мутант = тест полностью бесполезен | ❌ ПОСЛЕДСТВИЕ: тест может покрывать другие сценарии; survived лишь означает что данный mutation не детектируется; не invalid сам тест
> - [ ] Все survived мутанты = баги в коде | ❌ ПОСЛЕДСТВИЕ: survived — это test gap, не bug в коде; код мог работать корректно, просто tests слабые

## Q10. Equivalent mutants?

**Equivalent mutant** — semantically same as original. **Cannot be killed**.

**Example:**
```java
// Original
int i = 0;
while (i < 10) { i++; }

// Mutant: < → <=
int i = 0;
while (i <= 9) { i++; }  // SAME behavior!
```

**Mutant survives**, но not because tests weak — because mutation didn't actually change behavior.

**Hard to detect** automatically (research problem). Usually manual review.

**Tools** report all surviving mutants — must filter equivalent ones manually.


> [!mcq]
> - [ ] Equivalent мутанты можно автоматически детектировать и исключать | ❌ ПОСЛЕДСТВИЕ: detection equivalent мутантов — undecidable problem; в общем случае нужен manual review
> - [x] Equivalent: семантически идентичен оригиналу (i<10 vs i<=9 в while с i++); не убивается; ручной review для классификации | ✓ ПРИМЕНЯТЬ: пометить survived equivalent → исключить из расчёта score 📋 ПРАВИЛО: equivalent = same behavior = unkillable 🔗 См. Q10
> - [ ] Equivalent мутанты значат что код плох — нужно его рефакторить | ❌ ПОСЛЕДСТВИЕ: equivalent — это особенность конкретных операторов на конкретном коде; не индикатор плохого кода
> - [ ] PIT автоматически отфильтровывает equivalent мутанты | ❌ ПОСЛЕДСТВИЕ: PIT использует эвристики (например, не мутировать i<list.size() в for), но полная фильтрация невозможна; manual review остаётся

## Q11. (!) PIT для Java?

**PIT (Pitest)** — most popular Java mutation testing tool.

**Maven setup:**
```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.0</version>
</plugin>
```

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

**Output:** HTML report shows:
- Per-file mutation score
- Surviving / killed mutants
- Mutation type per surviving

**Optimizations:**
- **Incremental analysis** (only changed code)
- **Junit5 support**
- **Coverage-based** mutant selection (skip lines not covered)

**Performance:** PIT с PITest 1.6+ has **fast mode** — orders of magnitude faster.


> [!mcq]
> - [ ] PIT очень медленный и непригоден для CI | ❌ ПОСЛЕДСТВИЕ: PIT 1.6+ имеет fast mode + incremental analysis (только changed code) + coverage-based selection; пригоден для CI
> - [x] PIT (Pitest): pitest-maven plugin + mvn mutationCoverage → HTML report; fast mode + incremental + coverage-based | ✓ ПРИМЕНЯТЬ: добавить в Maven build, запускать в nightly или PR-merge gates 📋 ПРАВИЛО: PIT = Java mutation tool; Maven/Gradle integration ready 🔗 См. Q11
> - [ ] PIT работает только с JUnit 4, JUnit 5 не поддерживается | ❌ ПОСЛЕДСТВИЕ: PIT поддерживает JUnit 5 с pitest-junit5-plugin; legacy info — JUnit 5 поддерживается несколько лет
> - [ ] PIT нужно запускать в CI на каждом коммите | ❌ ПОСЛЕДСТВИЕ: даже с fast mode PIT занимает время; запуск на каждом коммите замедляет CI; обычно nightly или quality gate перед merge

## Q12. (!) Stryker для JavaScript/TypeScript?

**Stryker** — mutation testing для JS/TS/.NET/Scala.

```bash
npm install --save-dev @stryker-mutator/core @stryker-mutator/jest-runner
npx stryker init
npx stryker run
```

**Config (`stryker.conf.json`):**
```json
{
  "testRunner": "jest",
  "mutate": ["src/**/*.ts"],
  "thresholds": {
    "high": 80,
    "low": 60,
    "break": 50
  }
}
```

**Stryker Dashboard** — track score над time.


> [!mcq]
> - [ ] Stryker — это просто Jest plugin | ❌ ПОСЛЕДСТВИЕ: Stryker — независимый mutation testing framework для JS/TS/.NET/Scala; интегрируется с Jest/Mocha/Karma как test runners
> - [x] Stryker: npm install + stryker init + npx stryker run; thresholds в config (high/low/break); Stryker Dashboard для tracking | ✓ ПРИМЕНЯТЬ: JavaScript/TypeScript projects, mutation testing с CI quality gate 📋 ПРАВИЛО: Stryker = JS mutation tool с richтthresholds 🔗 См. Q12
> - [ ] Stryker не поддерживает TypeScript — только vanilla JS | ❌ ПОСЛЕДСТВИЕ: Stryker имеет TypeScript support через @stryker-mutator/typescript-checker; полноценная работа с tsx/typescript
> - [ ] thresholds в Stryker — это GUI-настройки, не файл config | ❌ ПОСЛЕДСТВИЕ: stryker.conf.json/js определяет thresholds декларативно; CI fails if score < break threshold

## Q13. mutmut для Python?

```bash
pip install mutmut
mutmut run
mutmut html  # report
```

**Mutates Python code**, runs pytest.

**Slow** — Python тоже dynamic, mutation testing dynamic languages medленно.


> [!mcq]
> - [ ] mutmut работает быстрее PIT — Python динамика помогает | ❌ ПОСЛЕДСТВИЕ: наоборот — Python dynamic typing замедляет mutation testing; нет байткод оптимизаций как в JVM
> - [x] mutmut: pip install + mutmut run + mutmut html; запускает pytest на каждый mutant; медленнее PIT из-за dynamic Python | ✓ ПРИМЕНЯТЬ: Python projects с pytest; CI quality gate с долгим bake-time 📋 ПРАВИЛО: mutmut = Python mutation tool 🔗 См. Q13
> - [ ] mutmut работает только с unittest, не с pytest | ❌ ПОСЛЕДСТВИЕ: mutmut поддерживает pytest как primary runner; unittest тоже доступен
> - [ ] mutmut автоматически фильтрует equivalent мутанты в Python | ❌ ПОСЛЕДСТВИЕ: общая проблема mutation testing — нет инструмента полностью решающего equivalent detection; mutmut тоже требует manual filtering

## Q14. Other tools (Mutil, Cosmic Ray)?

- **Mutil** (Go) — Go mutation testing
- **Cosmic Ray** (Python) — alternative mutmut
- **Infection** (PHP)
- **Pitest** (Scala via plugin)

**Adoption** varies. **PIT (Java) и Stryker (JS)** — most mature.


> [!mcq]
> - [ ] Mutil и Cosmic Ray — это улучшенные версии PIT для разных языков | ❌ ПОСЛЕДСТВИЕ: Mutil — Go-специфичный, Cosmic Ray — Python; не имеют связи с PIT (Java); это independent инструменты
> - [x] Mutil (Go), Cosmic Ray (Python alt to mutmut), Infection (PHP); PIT (Java) и Stryker (JS/TS) — most mature | ✓ ПРИМЕНЯТЬ: выбирать по language ecosystem; mature tools для core languages 📋 ПРАВИЛО: language → tool: Java/Stryker/mutmut/Mutil/Cosmic/Infection 🔗 См. Q11
> - [ ] Все mutation testing tools имеют похожий feature set | ❌ ПОСЛЕДСТВИЕ: maturity и features сильно различаются; PIT/Stryker имеют incremental, dashboards; новые tools часто с базовым функционалом
> - [ ] Pitest для Scala — это PIT работающий с Scala source code | ❌ ПОСЛЕДСТВИЕ: PIT мутирует JVM bytecode, поэтому работает с любым JVM языком (Java, Scala, Kotlin) через тот же Maven plugin

## Q15. (!) Mutation testing медленный — почему?

**Process:** для каждого mutant — recompile (sometimes) + run all tests.

**N mutants × test suite time = total time.**

**Example:**
- 1000 mutants
- Tests run в 30 seconds
- Total: 1000 × 30 = **8.3 hours** (naive)

**Optimizations bring это к minutes** для most projects.


> [!mcq]
> - [ ] Mutation testing медленный потому что генерация мутантов сложная | ❌ ПОСЛЕДСТВИЕ: генерация мутантов быстрая (просто bytecode/source patching); медленная часть — N runs тестов для N мутантов
> - [x] N мутантов × test suite time; 1000 мутантов × 30s = 8.3 часа naive; optimizations (coverage, incremental) → minutes | ✓ ПРИМЕНЯТЬ: использовать optimization flags; incremental run после изменений 📋 ПРАВИЛО: total time ≈ N_mutants × test_time / parallel_factor 🔗 См. Q15
> - [ ] Если test suite быстрый, mutation testing тоже быстрый | ❌ ПОСЛЕДСТВИЕ: даже 1s тесты с 5000 мутантов = 1.4 часа; число мутантов важнее одного test run
> - [ ] Скорость зависит только от железа, оптимизации алгоритма не помогают | ❌ ПОСЛЕДСТВИЕ: coverage-based selection (не запускать тесты не покрывающие mutated line) даёт 10-100x ускорение независимо от железа

## Q16. Optimization (incremental, in-process)?

**Optimizations:**

1. **Coverage-based selection** — only mutate covered code
2. **Per-mutant test selection** — only run tests covering mutated line
3. **Incremental** — only re-run changed code (since last run)
4. **In-process** — no JVM restart per mutant
5. **Parallel** execution
6. **Bytecode mutation** (Java) vs source (faster)

**Modern PIT:** for **incremental** runs — minutes для large codebases.


> [!mcq]
> - [ ] In-process execution не помогает — JVM cold start dominates | ❌ ПОСЛЕДСТВИЕ: in-process убирает JVM startup для каждого мутанта = 10-100x ускорение; one of major optimizations
> - [x] Coverage-based selection + per-mutant test selection + incremental + in-process + parallel + bytecode mutation; combine для minutes на large codebase | ✓ ПРИМЕНЯТЬ: enable все optimizations в PIT/Stryker; incremental — после первого full run 📋 ПРАВИЛО: optimizations stack = orders of magnitude faster 🔗 См. Q16
> - [ ] Bytecode mutation медленнее source mutation | ❌ ПОСЛЕДСТВИЕ: bytecode mutation быстрее (no recompile); source mutation требует full recompile per mutant
> - [ ] Incremental analysis не работает с CI — нужен polный run | ❌ ПОСЛЕДСТВИЕ: incremental анализирует diff с baseline (last successful run); CI получает результаты быстро на small changes

## Q17. (!) Когда mutation testing worth it?

**Worth it когда:**
- **Critical code** (financial, safety-critical)
- **Test quality concerns** (high coverage, but bugs slip through)
- **Library / framework** code (used by many)
- **TDD adoption** verification

**Less valuable:**
- Prototypes (changing fast)
- Glue code
- Generated code
- Simple data classes

**Resources:** mutation testing requires CI time, engineering analysis.


> [!mcq]
> - [ ] Mutation testing нужно применять ко всему коду — generated, glue, prototypes | ❌ ПОСЛЕДСТВИЕ: трата ресурсов на код где value low; generated/glue/prototypes изменяются часто и mutation testing не даёт значимой ценности
> - [x] Worth it: critical (financial/safety), test quality concerns, library code, TDD verification; less valuable: prototypes, glue, generated code | ✓ ПРИМЕНЯТЬ: targeted на business logic + critical paths; не на everything 📋 ПРАВИЛО: mutation = high-value для critical code, waste для прочего 🔗 См. Q17
> - [ ] Mutation testing полезно только для библиотек | ❌ ПОСЛЕДСТВИЕ: библиотечный код важная категория, но не единственная; критичный application code (платежи, медицина) тоже выигрывает
> - [ ] Mutation testing должно быть baseline для всех проектов | ❌ ПОСЛЕДСТВИЕ: cost (CI time + анализ) высок; baseline для startup/prototype = perfect quality at cost of velocity; resource investment должен соответствовать risk

## Q18. Какой score целевой?

**No universal "right" score.** Depends на code criticality.

**Guidance:**
- **Critical paths:** 90%+
- **Business logic:** 75-85%
- **Average code:** 60-75%
- **Trivial code:** N/A (doesn't need)

**Important:** **trend matters more than absolute number.** Score trending up = improving tests.


> [!mcq]
> - [ ] Один target 80% подходит для всех типов кода | ❌ ПОСЛЕДСТВИЕ: критичный код (финансы, безопасность) требует 90%+; trivial data classes — N/A; one-size-fits-all = слишком жёстко или слишком слабо
> - [x] Critical paths 90%+, business logic 75-85%, average 60-75%, trivial — N/A; trend важнее absolute score | ✓ ПРИМЕНЯТЬ: разные targets для разных модулей; track trend, не fixate на percent 📋 ПРАВИЛО: target по criticality; trend > absolute 🔗 См. Q18
> - [ ] Score 100% — реалистичная цель для production | ❌ ПОСЛЕДСТВИЕ: equivalent мутанты предотвращают 100%; преследование = wasted time
> - [ ] Чем выше mutation score тем лучше тесты — abolutely | ❌ ПОСЛЕДСТВИЕ: высокий score можно достичь искусственно (game метрики); качество тестов измеряется множеством факторов; score — proxy

## Q19. (!) Limitations и criticism?

1. **Slow** даже с optimizations
2. **Equivalent mutants** noise
3. **False sense of quality** — high score не guarantee correctness
4. **Hard to interpret** sometimes
5. **Doesn't test integration** (just unit code paths)
6. **Not a silver bullet** — complement other approaches
7. **Cost-benefit** unclear для some teams
8. **Maintenance burden** — analysis time

**Use thoughtfully** — не replace human code review, exploratory testing, integration tests.


> [!mcq]
> - [ ] Mutation testing — silver bullet для test quality | ❌ ПОСЛЕДСТВИЕ: не заменяет integration tests, code review, exploratory testing; mutation проверяет unit-level assertions, не business correctness
> - [x] Limitations: медленно, equivalent noise, false sense of quality, doesn't test integration, complement не replace; use thoughtfully | ✓ ПРИМЕНЯТЬ: mutation как ОДНА из метрик, наряду с code review и integration tests 📋 ПРАВИЛО: mutation = unit test quality proxy, не universal correctness 🔗 См. Q19
> - [ ] High mutation score гарантирует отсутствие production bugs | ❌ ПОСЛЕДСТВИЕ: integration bugs, race conditions, business logic errors могут не отлавливаться mutation testing; нужны другие layers тестирования
> - [ ] Equivalent mutants — это исключительный случай, не реальная проблема | ❌ ПОСЛЕДСТВИЕ: 5-15% мутантов часто equivalent; они accumulate и засоряют отчёты; ручной triage — significant maintenance overhead

## Q20. CI/CD integration?

**Run на pull requests** (incremental):
```bash
# PIT incremental — only changed files
mvn org.pitest:pitest-maven:mutationCoverage -DwithHistory
```

**Threshold gates:**
```
If mutation score < 60% → fail build
```

**Reports:**
- Comment на PR (Stryker has GitHub action)
- Post score к dashboard
- Alert на drops

**Best practice:**
- **Full mutation testing** weekly / nightly
- **Incremental** на PRs (fast)
- **Track trend** over time

В **2025** mutation testing — niche, но growing. Adopted by quality-conscious teams (Google, financial sector).

---

## See also

- [Unit Testing](unit-testing-interview.md) — base for mutation testing
- [Test Strategies](test-strategies-interview.md)
- [Property-based Testing](property-based-testing-interview.md)
- [Load Testing](load-testing-interview.md)
- [[code-quality-interview|Code Quality]] — если будем добавлять
- [Code Review](../code-quality/code-review-interview.md)
- [Refactoring](../code-quality/refactoring-patterns-interview.md)
- [Mockito](mockito-interview.md) — Java mocking
- [Integration Testing](integration-testing-interview.md)
- [Test Automation](test-automation-interview.md)
- [Pipeline Design](../cicd/pipeline-design-interview.md) — CI integration
