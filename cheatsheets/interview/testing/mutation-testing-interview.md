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

`Mutation testing` — техника оценки **качества тестов** (а не качества кода). Идея: модифицировать ("**mutate**") код, прогнать тесты и проверить, **сколько мутаций поймано** (killed). Выжившие мутанты (surviving mutants) = слабые тесты. Главные инструменты: **PIT** (Java), **Stryker** (JS/TS/.NET/Scala), **mutmut** (Python). Дополняет (но не заменяет) coverage.

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

**Mutation testing** — это тестирование **самих тестов**.

**Процесс:**
1. **Мутируем** код (небольшое изменение): `if (x > 5)` → `if (x >= 5)`
2. **Прогоняем тесты** на мутированном коде
3. Если тесты **падают** → мутант **killed** (хорошо, тесты его поймали)
4. Если тесты **проходят** → мутант **survived** (плохо, тесты его пропустили)

**Mutation score (kill rate)** = killed / общее число мутантов.

**Чем выше score** — тем лучше тесты.

**Идея:** **если ваши тесты не замечают небольших изменений в коде, значит, они на самом деле ничего не проверяют.**

## Q2. (!) Зачем mutation testing если есть coverage?

**Coverage:** показывает, какие строки кода выполняются тестами. **Качество не измеряет.**

**Пример:**
```java
// Coverage: 100%
@Test
void test() {
    int result = calculate(5);
    assertNotNull(result);  // weak assertion
}
```

Coverage отличный, но если `calculate` вернёт неверное значение — тест всё равно пройдёт!

**Mutation testing это ловит:**
```java
// Mutant: change return value
int calculate(int x) {
    return 999; // mutation
}
```

Тест по-прежнему проходит (`assertNotNull(999)` → true) → **survived mutant** → слабый тест.

**Mutation = качество ассертов**, а не просто покрытие выполнением.

## Q3. Mutant, killed, survived, equivalent?

**Mutant** — изменённая версия кода (согласно mutation operator).

**Killed** — хотя бы один тест падает на мутанте. Хорошо.
**Survived** — все тесты проходят. Плохо.
**No coverage** — мутант в непокрытом коде (не выполняется).
**Timeout** — мутант вызывает бесконечный цикл.
**Equivalent** — семантически идентичен оригиналу (убить невозможно). Доставляет хлопоты, см. Q10.

## Q4. (!) Какие mutations типичные?

**Типичные mutation operators:**

| Оператор | Оригинал | Мутация |
|----------|----------|----------|
| **Conditional Boundary** | `<` | `<=` |
| **Conditional Boundary** | `>` | `>=` |
| **Negate Conditional** | `==` | `!=` |
| **Math** | `+` | `-` |
| **Math** | `*` | `/` |
| **Increment** | `++` | `--` |
| **Boolean Return** | `return true` | `return false` |
| **Void Method Call** | `methodCall();` | (удалён) |
| **Constant** | `42` | `0` или `1` |

**Каждая строка с оператором** → порождает несколько мутантов.

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

**Тест:**
```java
@Test
void testAdult() {
    assertTrue(check(18));  // boundary
    assertTrue(check(19));
    assertFalse(check(17));
}
```

**Граничные тесты** (18, 17, 19) **убивают** boundary-мутантов. Без них — они выживают.

## Q6. Math Mutator?

```java
// Original
int total = price * quantity;

// Mutations
int total = price + quantity;  // * → +
int total = price - quantity;  // * → -
int total = price / quantity;  // * → /
```

**Тест:**
```java
@Test
void testTotal() {
    assertEquals(20, calculate(5, 4));  // 5*4=20
}
```

Если тест проверяет хотя бы `5*4=20` → мутация `5+4=9` будет **killed** (ассерт упадёт).

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

**Void Method Call (удаление вызова):**
```java
log.info("Processing");  →  // removed
```

**Каждый оператор** порождает множество мутантов на строку. **Общее число мутантов** в проекте может исчисляться сотнями и тысячами.

## Q8. (!) Mutation score (kill rate)?

```
Mutation Score = killed_mutants / (total_mutants - equivalent_mutants)
```

**Чем выше — тем лучше тесты.**

**Ориентиры:**
- **80%+ — отлично**
- **60-80% — хорошо**
- **40-60% — приемлемо**
- **< 40% — слабые тесты**

**100% нереалистично** (эквивалентные мутанты, таймауты).

**На практике многие команды целятся в 70-85%.**

## Q9. Surviving mutants — как анализировать?

**Каждый выживший мутант** = потенциальный пробел в тестах.

**Разбираемся:**
1. **Реальный баг?** Добавить тест → мутант killed
2. **Эквивалентный мутант?** Пропустить (пометить как такой)
3. **Допустимо?** Возможно, эта граница некритична

**Инструменты генерируют отчёт:**
```
SurvivedMutant: line 42
  ConditionalBoundary
  if (count >= MIN) → if (count > MIN)
  In: validate()
  No test catches this
```

**Действие:** добавить граничный тест для `MIN` (случай `count == MIN` должен проходить).

## Q10. Equivalent mutants?

**Эквивалентный мутант** — семантически идентичен оригиналу. **Убить невозможно.**

**Пример:**
```java
// Original
int i = 0;
while (i < 10) { i++; }

// Mutant: < → <=
int i = 0;
while (i <= 9) { i++; }  // SAME behavior!
```

**Мутант выживает**, но не потому, что тесты слабые — а потому, что мутация фактически не изменила поведение.

**Сложно обнаружить** автоматически (открытая исследовательская задача). Обычно — ручной разбор.

**Инструменты** показывают всех выживших мутантов — эквивалентных приходится отфильтровывать вручную.

## Q11. (!) PIT для Java?

**PIT (Pitest)** — самый популярный инструмент mutation testing для Java.

**Настройка Maven:**
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

**Результат:** HTML-отчёт показывает:
- mutation score по каждому файлу
- выживших / убитых мутантов
- тип мутации для каждого выжившего

**Оптимизации:**
- **Инкрементальный анализ** (только изменённый код)
- **Поддержка JUnit5**
- **Coverage-based** отбор мутантов (пропуск непокрытых строк)

**Производительность:** начиная с PITest 1.6+ есть **fast mode** — на порядки быстрее.

## Q12. (!) Stryker для JavaScript/TypeScript?

**Stryker** — mutation testing для JS/TS/.NET/Scala.

```bash
npm install --save-dev @stryker-mutator/core @stryker-mutator/jest-runner
npx stryker init
npx stryker run
```

**Конфиг (`stryker.conf.json`):**
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

**Stryker Dashboard** — отслеживание score во времени.

## Q13. mutmut для Python?

```bash
pip install mutmut
mutmut run
mutmut html  # report
```

**Мутирует Python-код**, прогоняет pytest.

**Медленно** — Python динамический, а mutation testing динамических языков идёт медленно.

## Q14. Other tools (Mutil, Cosmic Ray)?

- **Mutil** (Go) — mutation testing для Go
- **Cosmic Ray** (Python) — альтернатива mutmut
- **Infection** (PHP)
- **Pitest** (Scala через плагин)

**Распространённость** разная. **PIT (Java) и Stryker (JS)** — самые зрелые.

## Q15. (!) Mutation testing медленный — почему?

**Процесс:** для каждого мутанта — перекомпиляция (иногда) + прогон всех тестов.

**N мутантов × время прогона набора тестов = общее время.**

**Пример:**
- 1000 мутантов
- тесты идут 30 секунд
- итого: 1000 × 30 = **8.3 часа** (наивный подход)

**Оптимизации сводят это к минутам** для большинства проектов.

## Q16. Optimization (incremental, in-process)?

**Оптимизации:**

1. **Coverage-based selection** — мутировать только покрытый код
2. **Per-mutant test selection** — прогонять только тесты, покрывающие мутированную строку
3. **Incremental** — перепрогонять только изменённый код (с прошлого запуска)
4. **In-process** — без перезапуска JVM на каждого мутанта
5. **Parallel** — параллельное выполнение
6. **Мутация байт-кода** (Java) против исходников (быстрее)

**Современный PIT:** при **инкрементальных** прогонах — минуты даже на крупных кодовых базах.

## Q17. (!) Когда mutation testing worth it?

**Оправдано, когда:**
- **Критичный код** (финансы, safety-critical)
- **Есть сомнения в качестве тестов** (покрытие высокое, но баги всё равно проскакивают)
- **Код библиотеки / фреймворка** (которым пользуются многие)
- **Проверка внедрения TDD**

**Менее ценно:**
- прототипы (быстро меняются)
- связующий (glue) код
- сгенерированный код
- простые data-классы

**Ресурсы:** mutation testing требует времени CI и инженерного разбора результатов.

## Q18. Какой score целевой?

**Универсального «правильного» score нет.** Зависит от критичности кода.

**Ориентиры:**
- **Критичные пути:** 90%+
- **Бизнес-логика:** 75-85%
- **Обычный код:** 60-75%
- **Тривиальный код:** не нужен

**Важно:** **тренд важнее абсолютного числа.** Растущий score = тесты улучшаются.

## Q19. (!) Limitations и criticism?

1. **Медленно** даже с оптимизациями
2. **Эквивалентные мутанты** — шум
3. **Ложное ощущение качества** — высокий score не гарантирует корректность
4. **Иногда сложно интерпретировать** результаты
5. **Не проверяет интеграцию** (только unit-уровень путей кода)
6. **Не серебряная пуля** — дополняет другие подходы
7. **Соотношение цены и выгоды** для некоторых команд неочевидно
8. **Накладные расходы на поддержку** — время на разбор

**Применять вдумчиво** — это не замена человеческому code review, exploratory-тестированию и интеграционным тестам.

## Q20. CI/CD integration?

**Запуск на pull request'ах** (инкрементально):
```bash
# PIT incremental — only changed files
mvn org.pitest:pitest-maven:mutationCoverage -DwithHistory
```

**Пороговые гейты (threshold gates):**
```
If mutation score < 60% → fail build
```

**Отчётность:**
- комментарий в PR (у Stryker есть GitHub Action)
- публикация score в dashboard
- алёрт при падениях

**Хорошие практики:**
- **Полный mutation testing** — еженедельно / по ночам
- **Инкрементальный** — на PR (быстро)
- **Отслеживать тренд** во времени

В **2025** mutation testing — нишевая практика, но набирающая обороты. Применяется командами, серьёзно относящимися к качеству (Google, финансовый сектор).

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
