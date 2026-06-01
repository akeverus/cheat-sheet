---
title: "Вопросы на собеседовании: Code Coverage"
description: "Покрытие кода: line/branch/mutation coverage, JaCoCo, meaningful coverage vs coverage theater, quality gates, CI/CD integration"
tags:
  - interview
  - code-quality
  - code-coverage-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Code Coverage"
  - "Code Coverage interview"
  - "Code Coverage собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: Code Coverage

Краткие ответы про метрики покрытия кода: виды coverage, инструменты, подводные камни «coverage theater» и практические подходы к осмысленному покрытию.

## Содержание

- [See also](#see-also)

**Основы**
- [Q1. Что такое code coverage и зачем его измерять?](#q1-что-такое-code-coverage-и-зачем-его-измерять)
- [Q2. Какие виды coverage существуют?](#q2-какие-виды-coverage-существуют)
- [Q3. Что такое line coverage?](#q3-что-такое-line-coverage)
- [Q4. Что такое branch coverage и чем он важнее line coverage?](#q4-что-такое-branch-coverage-и-чем-он-важнее-line-coverage)
- [Q5. Что такое statement coverage vs line coverage?](#q5-что-такое-statement-coverage-vs-line-coverage)
- [Q6. Какой процент coverage считается хорошим?](#q6-какой-процент-coverage-считается-хорошим)

**JaCoCo**
- [Q7. Что такое JaCoCo и как он работает?](#q7-что-такое-jacoco-и-как-он-работает)
- [Q8. Как настроить JaCoCo в Maven/Gradle?](#q8-как-настроить-jacoco-в-mavengradle)
- [Q9. Как настроить minimum coverage threshold в JaCoCo?](#q9-как-настроить-minimum-coverage-threshold-в-jacoco)
- [Q10. Как исключить классы из подсчёта coverage в JaCoCo?](#q10-как-исключить-классы-из-подсчёта-coverage-в-jacoco)
- [Q11. Как JaCoCo интегрируется с SonarQube?](#q11-как-jacoco-интегрируется-с-sonarqube)

**Mutation Testing**
- [Q12. Что такое mutation testing?](#q12-что-такое-mutation-testing)
- [Q13. Как работает PIT (Pitest)?](#q13-как-работает-pit-pitest)
- [Q14. Что такое mutation score?](#q14-что-такое-mutation-score)
- [Q15. Какие мутации создаёт Pitest?](#q15-какие-мутации-создаёт-pitest)

**Coverage Theater**
- [Q16. Что такое «coverage theater»?](#q16-что-такое-coverage-theater)
- [Q17. Как написать тест, который увеличивает coverage, но не проверяет ничего?](#q17-как-написать-тест-который-увеличивает-coverage-но-не-проверяет-ничего)
- [Q18. Как отличить осмысленный coverage от формального?](#q18-как-отличить-осмысленный-coverage-от-формального)
- [Q19. Стоит ли гнаться за 100% coverage?](#q19-стоит-ли-гнаться-за-100-coverage)

**Практика**
- [Q20. Что не покрывает обычный code coverage?](#q20-что-не-покрывает-обычный-code-coverage)
- [Q21. Как измерять coverage в многомодульном проекте?](#q21-как-измерять-coverage-в-многомодульном-проекте)
- [Q22. Как использовать coverage в code review?](#q22-как-использовать-coverage-в-code-review)
- [Q23. Какие компоненты не имеет смысла покрывать тестами?](#q23-какие-компоненты-не-имеет-смысла-покрывать-тестами)
- [Q24. Как внедрить coverage в legacy-проект?](#q24-как-внедрить-coverage-в-legacy-проект)
- [Q25. Что такое differential coverage?](#q25-что-такое-differential-coverage)

---

## Q1. Что такое code coverage и зачем его измерять?

**Code coverage** — метрика, показывающая какой процент кода был выполнен во время тестов.

Зачем:
- Выявить непокрытые ветки — потенциальные «слепые пятна».
- Получить обратную связь о полноте тестов.
- Установить минимальный порог качества в CI.

Важно: coverage показывает, **что** код был выполнен, но не то, **что** он работает правильно. Высокий coverage ≠ хорошие тесты.

---

## Q2. Какие виды coverage существуют?

| Вид | Что считается |
|-----|--------------|
| **Statement/Line** | Выполненные строки или инструкции |
| **Branch** | Ветви условных операторов (true/false каждого `if`) |
| **Condition** | Каждое булево подвыражение в условии |
| **Path** | Все возможные пути выполнения (экспоненциален) |
| **Method** | Вызванные методы |
| **Class** | Инстанцированные классы |
| **Mutation** | Процент «убитых» мутаций (требует mutation testing) |

JaCoCo по умолчанию считает: instruction, branch, line, method, class complexity.

---

## Q3. Что такое line coverage?

**Line coverage** (покрытие строк) = строки, выполненные тестами / общее число строк × 100%.

Простейший в понимании, но наименее строгий. Одна строка может содержать несколько ветвлений:

```java
// Одна строка — но две ветки: true и false
String result = value != null ? value.toUpperCase() : "default";
// line coverage = 100%, если строка выполнена хоть раз
// branch coverage = 50%, если value всегда != null
```

---

## Q4. Что такое branch coverage и чем он важнее line coverage?

**Branch coverage** считает, были ли выполнены обе ветки каждого условного оператора (true + false).

```java
void process(Order order) {
    if (order.isPaid()) {      // две ветки: paid и not paid
        ship(order);
    } else {
        notifyUnpaid(order);
    }
}

// Тест только с paid=true: line coverage = 100%, branch coverage = 50%
```

Branch coverage строже: требует покрытия граничных случаев. SonarQube и JaCoCo показывают branch coverage отдельно.

---

## Q5. Что такое statement coverage vs line coverage?

- **Statement coverage** — каждая инструкция (statement) выполнена хотя бы раз.
- **Line coverage** — каждая строка выполнена хотя бы раз.

Разница в случае нескольких инструкций на одной строке:

```java
int a = 1; int b = 2; int c = a + b;  // 3 statements, 1 line
```

JaCoCo оперирует понятием **instruction coverage** (близко к statement), что точнее line coverage.

---

## Q6. Какой процент coverage считается хорошим?

Универсального числа нет — контекст решает:

| Контекст | Рекомендуемый coverage |
|----------|----------------------|
| Критическая бизнес-логика | 90%+ branch coverage |
| Обычный сервисный код | 70–80% |
| Утилиты, helper-классы | 80%+ |
| DTO, конфигурация | Исключить из подсчёта |
| Интеграционный слой (контроллеры) | Интеграционные тесты важнее |

SonarQube Sonar Way Quality Gate: ≥ 80% coverage на **новом** коде — разумный стандарт для многих команд.

---

## Q7. Что такое JaCoCo и как он работает?

**JaCoCo** (Java Code Coverage) — de-facto стандарт для измерения покрытия в Java.

Принцип работы — **bytecode instrumentation**:
1. JaCoCo инструментирует байткод: вставляет probe-инструкции в каждую ветку.
2. Во время выполнения тестов probes записывают, были ли выполнены.
3. После завершения JaCoCo собирает данные в `.exec` файл.
4. Из `.exec` генерируются отчёты (XML, HTML, CSV).

Два режима инструментирования:
- **On-the-fly** (Java Agent) — без модификации классов на диске.
- **Offline** — классы инструментируются заранее.

---

## Q8. Как настроить JaCoCo в Maven/Gradle?

**Maven:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

**Gradle:**
```groovy
plugins {
    id 'jacoco'
}
test {
    finalizedBy jacocoTestReport
}
jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true  // нужен для SonarQube
        html.required = true
    }
}
```

---

## Q9. Как настроить minimum coverage threshold в JaCoCo?

```xml
<!-- Maven: check goal с правилами -->
<execution>
    <id>check</id>
    <goals><goal>check</goal></goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

**Gradle:**
```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }
        }
    }
}
```

---

## Q10. Как исключить классы из подсчёта coverage в JaCoCo?

Исключают: DTO, generated код, конфигурационные классы, main-методы.

```xml
<!-- Maven exclusions -->
<configuration>
    <excludes>
        <exclude>**/dto/**</exclude>
        <exclude>**/config/**</exclude>
        <exclude>**/*Application.class</exclude>
        <exclude>**/generated/**</exclude>
    </excludes>
</configuration>
```

```groovy
// Gradle
jacocoTestReport {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/dto/**',
                '**/config/**',
                '**/*Application*'
            ])
        }))
    }
}
```

---

## Q11. Как JaCoCo интегрируется с SonarQube?

SonarQube читает отчёт JaCoCo в XML формате. Нужно:
1. Настроить JaCoCo для генерации XML отчёта.
2. Указать путь к отчёту в настройках SonarQube.

```xml
<!-- Maven: путь по умолчанию - target/site/jacoco/jacoco.xml -->
<sonar.coverage.jacoco.xmlReportPaths>
    ${project.build.directory}/site/jacoco/jacoco.xml
</sonar.coverage.jacoco.xmlReportPaths>
```

Для многомодульных проектов: нужен агрегированный отчёт или указание путей к отчётам каждого модуля.

---

## Q12. Что такое mutation testing?

**Mutation testing** — автоматическое введение небольших изменений (мутаций) в код, проверка что тесты их обнаруживают.

Принцип:
1. Инструмент создаёт «мутанты» — версии кода с небольшими изменениями.
2. Запускает тесты против каждого мутанта.
3. Если тесты падают — мутант «убит» (тесты хорошие).
4. Если тесты проходят — мутант «выжил» (тесты недостаточно строгие).

Mutation testing отвечает на вопрос: «А проверяют ли наши тесты то, что должны?»

---

## Q13. Как работает PIT (Pitest)?

**PIT (Pitest)** — наиболее популярный инструмент mutation testing для Java.

```xml
<!-- Maven -->
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.3</version>
    <configuration>
        <targetClasses>
            <param>com.example.service.*</param>
        </targetClasses>
        <targetTests>
            <param>com.example.service.*Test</param>
        </targetTests>
        <mutationThreshold>70</mutationThreshold>
    </configuration>
</plugin>
```
```bash
mvn test-compile pitest:mutationCoverage
```

Генерирует HTML-отчёт: какие мутанты выжили и где.

---

## Q14. Что такое mutation score?

**Mutation Score** = убитые мутанты / всего мутантов × 100%

Интерпретация:
- 90%+ — превосходное качество тестов.
- 70–90% — хороший уровень.
- < 50% — тесты есть, но они не проверяют поведение.

Mutation score более надёжный индикатор качества тестов, чем line coverage.

---

## Q15. Какие мутации создаёт Pitest?

| Мутатор | Пример мутации |
|---------|---------------|
| **Conditionals Boundary** | `>` → `>=`, `<` → `<=` |
| **Negate Conditionals** | `==` → `!=`, `>` → `<=` |
| **Math** | `+` → `-`, `*` → `/` |
| **Return Values** | `return true` → `return false` |
| **Void Method Calls** | Удаление вызова `void` метода |
| **Increments** | `i++` → `i--` |
| **Invert Negatives** | `-x` → `x` |

```java
// Оригинал
boolean isAdult(int age) {
    return age >= 18;
}

// Мутант: Conditionals Boundary
boolean isAdult(int age) {
    return age > 18;  // граничный случай 18 теперь false
}
// Если тест не проверяет age=18 отдельно — мутант выживет
```

---

## Q16. Что такое «coverage theater»?

**Coverage theater** — ситуация, когда команда достигает высокого % coverage, но тесты не проверяют реальное поведение. «Театр для метрик».

Проявления:
- Тесты без assertions (просто вызов метода).
- Тесты только «happy path» без edge cases.
- Тесты для увеличения цифр, а не для предотвращения регрессий.
- Мокирование всего подряд — тест ничего реального не проверяет.

Coverage theater опасен: создаёт ложное ощущение безопасности при деградации кода.

---

## Q17. Как написать тест, который увеличивает coverage, но не проверяет ничего?

```java
// Coverage theater: метод вызван, строки покрыты, но тест ничего не утверждает
@Test
void testCalculate() {
    Calculator calc = new Calculator();
    calc.calculate(10, 5, Operation.ADD);  // coverage есть, assertion нет
}

// Правильно: проверяем результат
@Test
void testCalculate_addition() {
    Calculator calc = new Calculator();
    int result = calc.calculate(10, 5, Operation.ADD);
    assertThat(result).isEqualTo(15);
}
```

---

## Q18. Как отличить осмысленный coverage от формального?

Признаки осмысленного coverage:
- Каждый тест проверяет конкретное утверждение (assertion).
- Тесты покрывают граничные случаи, а не только happy path.
- Mutation score высокий при высоком coverage.
- Тест падает при намеренном введении бага в тестируемый код.

Инструментальный способ проверки: запустить Pitest. Если mutation score значительно ниже coverage — тесты формальные.

---

## Q19. Стоит ли гнаться за 100% coverage?

Нет. Причины:

- **Закон убывающей отдачи:** последние 10% coverage требуют непропорциональных усилий.
- **Нетестируемый код:** main-методы, конфигурация, edge-cases инфраструктурных исключений.
- **Distraction от качества:** команда оптимизирует метрику, а не тесты.
- **Dead code может быть покрыт:** тестировать мёртвый код — пустая трата.

Исключение: safety-critical системы (авиация, медицина) требуют 100% MC/DC coverage — это осознанное требование стандартов.

---

## Q20. Что не покрывает обычный code coverage?

- **Correctness** — код выполнен, но результат неверный.
- **Concurrency issues** — гонки данных, deadlocks.
- **Integration** — взаимодействие компонентов в реальном окружении.
- **Performance** — покрытый код может быть медленным.
- **Security** — SQL-injection может находиться в «покрытом» коде.
- **Error handling** — исключения, брошенные внешними системами.

---

## Q21. Как измерять coverage в многомодульном проекте?

**Maven: агрегированный отчёт**
```xml
<!-- В отдельном report-модуле -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>report-aggregate</id>
            <phase>verify</phase>
            <goals><goal>report-aggregate</goal></goals>
        </execution>
    </executions>
</plugin>
```

**Gradle multi-project:**
```groovy
// В корневом build.gradle
task jacocoMergedReport(type: JacocoReport) {
    executionData fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec")
    subprojects.each {
        sourceSets it.sourceSets.main
    }
}
```

---

## Q22. Как использовать coverage в code review?

Практики:
- PR должен поддерживать или улучшать coverage (SonarQube PR Decoration показывает это).
- При добавлении новой функциональности — требовать тесты на новый код.
- При обнаружении непокрытой ветки в ревью — задавать вопрос «почему нет теста?».
- Не требовать 100%, но требовать тесты на критическую логику.

Инструментально: настроить Quality Gate «coverage on new code ≥ 80%» — блокирует merge без тестов.

---

## Q23. Какие компоненты не имеет смысла покрывать тестами?

- **DTO / Record / POJO** — если только геттеры/сеттеры без логики.
- **Конфигурационные классы** (`@Configuration`, `application.yml` binding).
- **main()-методы** Spring Boot приложений.
- **Сгенерированный код** (Mapstruct, QueryDSL, Lombok).
- **Logging-only** методы без бизнес-логики.

Все эти классы нужно явно исключить из JaCoCo через excludes, чтобы не портить метрику.

---

## Q24. Как внедрить coverage в legacy-проект?

Пошаговый подход:

1. **Измерить baseline** — запустить JaCoCo, зафиксировать текущее состояние (например, 20%).
2. **Не рачет снижения** — установить threshold = текущий уровень. CI не должен давать coverage упасть.
3. **Постепенный рост** — поднимать порог на 1–2% при каждом sprint, покрывая новый и критический код.
4. **Focus on new code** — SonarQube New Code Period: требовать 80% только для нового кода.
5. **Boy Scout Rule** — добавлять тесты при касании файла в рамках задач.

---

## Q25. Что такое differential coverage?

**Differential coverage** — измерение покрытия только изменённого кода (diff), а не всего проекта.

Применение: в PR/MR анализировать только строки, добавленные или изменённые в этом PR.

Инструменты:
- **SonarQube New Code** — встроенная поддержка.
- **Codecov / Coveralls** — сервисы с дифференциальным анализом для GitHub.

Преимущество: позволяет установить строгий порог (например, 90%) для нового кода, не требуя мгновенно покрыть унаследованный.

---

## See also

- [Code Review](code-review-interview.md)
- [Static Analysis](static-analysis-interview.md)
- [Code Smells](code-smells-interview.md)
- [Technical Debt](technical-debt-interview.md)
- [Clean Code Practices](clean-code-practices-interview.md)
- [Refactoring Patterns](refactoring-patterns-interview.md)
- [Java Core](../programming-languages/java/java-core-interview.md)
