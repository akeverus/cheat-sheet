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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. (!) Зачем mutation testing если есть coverage? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. Mutant, killed, survived, equivalent? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Mutant** — modified version code (по mutation operator).

**Killed** — at least one test fails on mutant. Good.
**Survived** — все tests pass. Bad.
**No coverage** — mutant в untested code (не executed).
**Timeout** — mutant causes infinite loop.
**Equivalent** — semantically same as original (cannot be killed). Annoying, see Q10.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. (!) Какие mutations типичные? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Conditional Boundary Mutator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Math Mutator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Other mutators? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. (!) Mutation score (kill rate)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Surviving mutants — как анализировать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Equivalent mutants? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. (!) PIT для Java? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) Stryker для JavaScript/TypeScript? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. mutmut для Python? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```bash
pip install mutmut
mutmut run
mutmut html  # report
```

**Mutates Python code**, runs pytest.

**Slow** — Python тоже dynamic, mutation testing dynamic languages medленно.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Other tools (Mutil, Cosmic Ray)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

- **Mutil** (Go) — Go mutation testing
- **Cosmic Ray** (Python) — alternative mutmut
- **Infection** (PHP)
- **Pitest** (Scala via plugin)

**Adoption** varies. **PIT (Java) и Stryker (JS)** — most mature.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) Mutation testing медленный — почему? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Process:** для каждого mutant — recompile (sometimes) + run all tests.

**N mutants × test suite time = total time.**

**Example:**
- 1000 mutants
- Tests run в 30 seconds
- Total: 1000 × 30 = **8.3 hours** (naive)

**Optimizations bring это к minutes** для most projects.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Optimization (incremental, in-process)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Optimizations:**

1. **Coverage-based selection** — only mutate covered code
2. **Per-mutant test selection** — only run tests covering mutated line
3. **Incremental** — only re-run changed code (since last run)
4. **In-process** — no JVM restart per mutant
5. **Parallel** execution
6. **Bytecode mutation** (Java) vs source (faster)

**Modern PIT:** for **incremental** runs — minutes для large codebases.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. (!) Когда mutation testing worth it? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Какой score целевой? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**No universal "right" score.** Depends на code criticality.

**Guidance:**
- **Critical paths:** 90%+
- **Business logic:** 75-85%
- **Average code:** 60-75%
- **Trivial code:** N/A (doesn't need)

**Important:** **trend matters more than absolute number.** Score trending up = improving tests.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Limitations и criticism? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. CI/CD integration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Unit Testing](unit-testing-interview.md) — context ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.
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
