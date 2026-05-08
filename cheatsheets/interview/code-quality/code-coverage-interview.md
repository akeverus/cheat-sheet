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

> [!mcq]
> - [x] Code coverage — метрика % кода, выполненного тестами; высокий coverage не гарантирует корректность, только факт выполнения | ✓ ПРИМЕНЯТЬ: для выявления непокрытых веток и quality gate в CI 📋 ПРАВИЛО: coverage = факт выполнения строк, а не правильность результата 🔗 См. Q16
> - [ ] Code coverage гарантирует отсутствие багов во всём покрытом коде | ❌ ПОСЛЕДСТВИЕ: тест без assertions увеличивает coverage до 100%, при этом regression-баг уходит в production незамеченным
> - [ ] 80% line coverage = 80% бизнес-логики проверено тестами | ❌ ПОСЛЕДСТВИЕ: line coverage пропускает branch ветки — код с 100% line и 50% branch coverage пропустит null→NPE в непроверенной ветке
> - [ ] Цель coverage — обеспечить запуск всех тестов без ошибок | ❌ ПОСЛЕДСТВИЕ: пройденные тесты ≠ высокий coverage; если тесты не вызывают нужный код, coverage 20% даже при всех зелёных тестах

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

> [!mcq]
> - [ ] Statement coverage и branch coverage — это одно и то же, просто разные названия | ❌ ПОСЛЕДСТВИЕ: statement считает выполненные инструкции, branch считает true/false каждого условия — 100% statement при 50% branch пропускает целые ветки условий
> - [ ] Path coverage — наиболее практичный вид, используется в CI по умолчанию | ❌ ПОСЛЕДСТВИЕ: path coverage экспоненциально растёт с числом условий — 10 if-блоков = 1024 пути; JaCoCo использует branch/instruction, не path
> - [x] Основные виды: line, branch, condition, path, method, class, mutation — каждый строже предыдущего; JaCoCo по умолчанию: instruction + branch | ✓ ПРИМЕНЯТЬ: выбирать branch как минимум для бизнес-логики 📋 ПРАВИЛО: строгость: line < branch < condition < path < mutation 🔗 См. Q4
> - [ ] JaCoCo по умолчанию измеряет только line coverage | ❌ ПОСЛЕДСТВИЕ: JaCoCo считает instruction, branch, line, method, class — без branch JaCoCo пропустит непроверенные ветки условий в отчёте

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

> [!mcq]
> - [ ] Line coverage = branch coverage; если строка выполнена, все её ветки покрыты | ❌ ПОСЛЕДСТВИЕ: `String r = x != null ? x.upper() : "def"` — строка покрыта при x!=null, но ветка x==null не выполнена; 100% line, 50% branch
> - [ ] 100% line coverage гарантирует что все if/else ветки проверены | ❌ ПОСЛЕДСТВИЕ: тест только с paid=true даёт 100% line для метода с if/else, но ветка else (notifyUnpaid) никогда не вызывается
> - [ ] Line coverage — строжайший вид покрытия, заменяет все остальные | ❌ ПОСЛЕДСТВИЕ: line coverage наименее строгий — не учитывает ветки, условия и пути; использование как единственной метрики даёт false confidence
> - [x] Line coverage = % строк, выполненных хотя бы раз; наименее строгий вид — одна строка может содержать несколько веток | ✓ ПРИМЕНЯТЬ: как первичная быстрая метрика; для критической логики дополнять branch coverage 📋 ПРАВИЛО: строка выполнена ≠ все ветки в строке выполнены 🔗 См. Q4

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

> [!mcq]
> - [x] Branch coverage считает true+false ветки каждого условия; строже line coverage, т.к. требует покрытия граничных случаев; SonarQube и JaCoCo показывают branch отдельно | ✓ ПРИМЕНЯТЬ: обязательно для бизнес-логики с условиями; paid=true тест → 50% branch coverage 📋 ПРАВИЛО: каждый if = 2 ветки; 100% line + 50% branch = ветка else не тестирована 🔗 См. Q3
> - [ ] Branch coverage и line coverage — взаимозаменяемы, всегда дают одинаковый результат | ❌ ПОСЛЕДСТВИЕ: тест только с paid=true → 100% line coverage, но 50% branch; ветка else (notifyUnpaid) не протестирована → баг в unpaid-сценарии уйдёт в production
> - [ ] 70% branch coverage означает, что 70% тестов прошло без ошибок | ❌ ПОСЛЕДСТВИЕ: branch coverage измеряет % покрытых веток, а не % успешных тестов; 0% branch при 100% passing tests если код вообще не вызывается
> - [ ] Condition coverage — то же, что branch coverage | ❌ ПОСЛЕДСТВИЕ: branch считает ветки per if-statement, condition считает каждое булево подвыражение; `if (a && b)` — 2 ветки для branch, но 4 комбинации для condition

## Q5. Что такое statement coverage vs line coverage?

- **Statement coverage** — каждая инструкция (statement) выполнена хотя бы раз.
- **Line coverage** — каждая строка выполнена хотя бы раз.

Разница в случае нескольких инструкций на одной строке:

```java
int a = 1; int b = 2; int c = a + b;  // 3 statements, 1 line
```

JaCoCo оперирует понятием **instruction coverage** (близко к statement), что точнее line coverage.

---

> [!mcq]
> - [ ] Statement coverage и line coverage всегда показывают одинаковый процент | ❌ ПОСЛЕДСТВИЕ: `int a=1; int b=2;` — 2 statement на 1 строке; если только первый выполнен, statement = 50%, line = 100%
> - [ ] JaCoCo использует line coverage как основную метрику | ❌ ПОСЛЕДСТВИЕ: JaCoCo использует instruction coverage (ближе к statement), более точную чем line; неверная трактовка приводит к неправильному сравнению с другими инструментами
> - [ ] Statement coverage строже branch coverage | ❌ ПОСЛЕДСТВИЕ: statement coverage не строже branch — он считает инструкции, не ветки; строгость добавляет именно branch coverage
> - [x] Statement — каждая инструкция выполнена; line — каждая строка; JaCoCo использует instruction coverage, более точную при нескольких инструкциях на строке | ✓ ПРИМЕНЯТЬ: instruction coverage корректнее для inline-кода; для Sonar отчёта использовать jacoco XML 📋 ПРАВИЛО: строка ≠ инструкция; JaCoCo = instruction, не line 🔗 См. Q7

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

> [!mcq]
> - [ ] 100% coverage — единственный приемлемый стандарт для production-кода | ❌ ПОСЛЕДСТВИЕ: закон убывающей отдачи — последние 10% требуют непропорциональных усилий; команда начинает писать пустые тесты ради метрики
> - [ ] Единый порог 80% подходит для всех типов кода, включая DTO и конфиги | ❌ ПОСЛЕДСТВИЕ: DTO/конфиги без логики искусственно снижают coverage % при добавлении — нужно исключать их через JaCoCo excludes
> - [x] Зависит от контекста: бизнес-логика → 90%+ branch; обычный код → 70–80%; DTO/конфиги → исключить; SonarQube Way: ≥80% на новом коде | ✓ ПРИМЕНЯТЬ: устанавливать разные пороги по зонам кода; SonarQube New Code Period как минимальный стандарт 📋 ПРАВИЛО: critical = 90%, обычный = 70-80%, генерируемый = исключить 🔗 См. Q23
> - [ ] Coverage% не важен — любое значение приемлемо если тесты проходят | ❌ ПОСЛЕДСТВИЕ: без coverage threshold в CI код с 0% coverage успешно мержится; со временем непокрытый код накапливает баги без обнаружения

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

> [!mcq]
> - [ ] JaCoCo работает на уровне исходного кода Java, анализируя .java файлы | ❌ ПОСЛЕДСТВИЕ: JaCoCo работает на уровне bytecode, вставляя probes в .class файлы; анализ .java файлов не даст данных о runtime execution
> - [x] JaCoCo — de-facto стандарт Java coverage; bytecode instrumentation: probe в каждую ветку .class, данные в .exec, генерация XML/HTML отчётов | ✓ ПРИМЕНЯТЬ: как Java Agent (on-the-fly) в Maven/Gradle; XML отчёт для Sonar интеграции 📋 ПРАВИЛО: JaCoCo = probe в каждую ветку байткода → .exec → XML/HTML report 🔗 См. Q8
> - [ ] JaCoCo требует изменения исходного кода для инструментирования | ❌ ПОСЛЕДСТВИЕ: JaCoCo работает прозрачно через Java Agent без изменений исходников; модификация кода для тестирования нарушает принцип isolation
> - [ ] JaCoCo поддерживает только Maven, для Gradle нужен другой инструмент | ❌ ПОСЛЕДСТВИЕ: JaCoCo имеет официальный Gradle плагин; неиспользование в Gradle проекте лишает команду стандартной coverage метрики

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

> [!mcq]
> - [ ] Для JaCoCo достаточно добавить плагин без дополнительной конфигурации — отчёт генерируется автоматически | ❌ ПОСЛЕДСТВИЕ: без execution prepare-agent JaCoCo agent не запускается при тестах; без execution report отчёт не генерируется — CI будет без coverage данных
> - [ ] В Gradle нужно явно вызывать jacocoTestReport в каждом тесте через @ExtendWith | ❌ ПОСЛЕДСТВИЕ: jacocoTestReport — это Gradle task, не Java аннотация; путаница приводит к тому что coverage не генерируется вообще
> - [x] Maven: prepare-agent + report executions; Gradle: plugins { id 'jacoco' } + test.finalizedBy(jacocoTestReport) + xml.required = true для Sonar | ✓ ПРИМЕНЯТЬ: xml.required=true обязательно для SonarQube; dependsOn test гарантирует порядок 📋 ПРАВИЛО: Maven = 2 execution; Gradle = plugin + finalizedBy + xml=true 🔗 См. Q11
> - [ ] JaCoCo автоматически отправляет данные в SonarQube без дополнительной настройки | ❌ ПОСЛЕДСТВИЕ: SonarQube читает JaCoCo XML по пути sonar.coverage.jacoco.xmlReportPaths; без xml.required=true и правильного пути Sonar показывает 0% coverage

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

> [!mcq]
> - [ ] Threshold настраивается только в SonarQube Quality Gate, в JaCoCo нет такой возможности | ❌ ПОСЛЕДСТВИЕ: JaCoCo имеет jacocoTestCoverageVerification task; без локального порога в CI coverage может упасть до релиза без предупреждения
> - [ ] Для threshold достаточно указать minimum=0.80 в корне конфигурации плагина | ❌ ПОСЛЕДСТВИЕ: minimum нужно вкладывать в structure rule/limit/counter/value; без правильной вложенности JaCoCo игнорирует threshold и build проходит при любом coverage
> - [x] Maven: check goal с violationRules → rule → limit → counter=LINE/BRANCH + minimum; Gradle: jacocoTestCoverageVerification { violationRules { rule { limit { ... } } } } | ✓ ПРИМЕНЯТЬ: устанавливать и LINE ≥0.80 и BRANCH ≥0.70 для эффективного контроля 📋 ПРАВИЛО: Maven = check goal; Gradle = jacocoTestCoverageVerification task 🔗 См. Q4
> - [ ] Threshold в 100% — оптимальный выбор для всех проектов в enterprise | ❌ ПОСЛЕДСТВИЕ: 100% порог создаёт coverage theater — команда пишет пустые тесты без assertions чтобы пройти CI; реальное качество кода не улучшается

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

> [!mcq]
> - [ ] Для исключения DTO нужно добавить @CoverageIgnore аннотацию к каждому классу | ❌ ПОСЛЕДСТВИЕ: @CoverageIgnore — не стандартная JaCoCo аннотация; JaCoCo не поддерживает аннотации для исключений — нужна excludes конфигурация в плагине
> - [x] Maven: <excludes><exclude>**/dto/**</exclude></excludes>; Gradle: afterEvaluate classDirectories с fileTree exclude patterns | ✓ ПРИМЕНЯТЬ: исключать DTO, config, main-метод, generated код чтобы метрика отражала реальный бизнес-код 📋 ПРАВИЛО: **/dto/** + **/config/** + **/*Application* + **/generated/** 🔗 См. Q23
> - [ ] Исключение классов из coverage всегда занижает качество — всё должно быть покрыто | ❌ ПОСЛЕДСТВИЕ: DTO без логики добавляет "шум" в coverage — команда тратит время на бессмысленные тесты геттеров; метрика не отражает реальный риск
> - [ ] Исключение работает через @SuppressWarnings("coverage") аннотацию | ❌ ПОСЛЕДСТВИЕ: @SuppressWarnings работает для предупреждений компилятора, не для JaCoCo; этот подход не исключит класс из coverage отчёта

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

> [!mcq]
> - [ ] SonarQube автоматически находит JaCoCo данные без конфигурации | ❌ ПОСЛЕДСТВИЕ: SonarQube ищет XML по дефолтному пути target/site/jacoco/jacoco.xml; при нестандартном layout или multi-module нужно указать путь в sonar.coverage.jacoco.xmlReportPaths
> - [ ] JaCoCo напрямую отправляет данные в SonarQube через REST API | ❌ ПОСЛЕДСТВИЕ: JaCoCo не имеет SonarQube интеграции; он генерирует файлы (.exec, XML), которые SonarQube Scanner читает при анализе
> - [ ] Для SonarQube достаточно бинарного .exec файла JaCoCo | ❌ ПОСЛЕДСТВИЕ: SonarQube читает XML формат, не .exec; без xml.required=true в JaCoCo конфиге SonarQube покажет 0% coverage даже при наличии .exec
> - [x] SonarQube читает JaCoCo XML через sonar.coverage.jacoco.xmlReportPaths; для multi-module нужен агрегированный отчёт или перечисление путей | ✓ ПРИМЕНЯТЬ: xml.required=true + настройка пути в sonar-project.properties 📋 ПРАВИЛО: JaCoCo → XML → SonarQube Scanner → Sonar 🔗 См. Q21

## Q12. Что такое mutation testing?

**Mutation testing** — автоматическое введение небольших изменений (мутаций) в код, проверка что тесты их обнаруживают.

Принцип:
1. Инструмент создаёт «мутанты» — версии кода с небольшими изменениями.
2. Запускает тесты против каждого мутанта.
3. Если тесты падают — мутант «убит» (тесты хорошие).
4. Если тесты проходят — мутант «выжил» (тесты недостаточно строгие).

Mutation testing отвечает на вопрос: «А проверяют ли наши тесты то, что должны?»

---

> [!mcq]
> - [ ] Mutation testing = тестирование при изменённых входных данных | ❌ ПОСЛЕДСТВИЕ: mutation testing меняет КОД (операторы, константы, ветки), не входные данные; путаница с property-based testing приводит к неверному выбору инструментов
> - [ ] Если все unit тесты проходят, mutation score автоматически 100% | ❌ ПОСЛЕДСТВИЕ: тесты могут проходить при мутированном коде если нет assertions на затронутый результат; mutation score 40% при 100% passing tests — обычная ситуация
> - [x] Mutation testing вводит мелкие изменения в код; мутант убит = тесты что-то проверяют; выжил = тест слабый; mutation score надёжнее line coverage | ✓ ПРИМЕНЯТЬ: для оценки реального качества тестов; Pitest как инструмент 📋 ПРАВИЛО: убит = тест что-то проверяет; выжил = тест слабый 🔗 См. Q14
> - [ ] Mutation testing заменяет unit тесты — достаточно mutation score ≥ 80% | ❌ ПОСЛЕДСТВИЕ: mutation testing оценивает качество существующих тестов, но не создаёт тесты; без unit тестов mutation score невозможен

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

> [!mcq]
> - [ ] Pitest запускается вместо тестов и генерирует их автоматически | ❌ ПОСЛЕДСТВИЕ: Pitest не генерирует тесты; он запускает существующие тесты против мутированного кода; без unit тестов Pitest не может работать
> - [x] PIT — Maven/Gradle плагин; создаёт мутантов из байткода, запускает тесты против каждого, генерирует HTML отчёт о выживших мутантах по классам | ✓ ПРИМЕНЯТЬ: mvn pitest:mutationCoverage; targetClasses + mutationThreshold в конфиге 📋 ПРАВИЛО: Pitest = bytecode mutation + test run per mutant + HTML отчёт 🔗 См. Q15
> - [ ] Pitest работает только с TestNG, JUnit5 не поддерживается | ❌ ПОСЛЕДСТВИЕ: Pitest поддерживает JUnit4, JUnit5, TestNG через соответствующие плагины; JUnit5 без pitest-junit5-plugin зависимости не будет работать
> - [ ] Pitest запускается на каждом PR автоматически без дополнительной конфигурации | ❌ ПОСЛЕДСТВИЕ: Pitest запускает тесты N раз (N = число мутантов) — медленно; обычно добавляют в ночные сборки, не в PR пайплайн

## Q14. Что такое mutation score?

**Mutation Score** = убитые мутанты / всего мутантов × 100%

Интерпретация:
- 90%+ — превосходное качество тестов.
- 70–90% — хороший уровень.
- < 50% — тесты есть, но они не проверяют поведение.

Mutation score более надёжный индикатор качества тестов, чем line coverage.

---

> [!mcq]
> - [ ] Mutation score = % тестов, которые прошли без ошибок | ❌ ПОСЛЕДСТВИЕ: mutation score = убитые_мутанты / всего_мутантов × 100%; это про качество тестов, а не % прохождений
> - [x] Mutation score = убитые мутанты / всего мутантов × 100%; < 50% = тесты не проверяют поведение; 70–90% = хорошо; надёжнее line coverage | ✓ ПРИМЕНЯТЬ: для оценки качества тестов; Pitest показывает где мутанты выживают 📋 ПРАВИЛО: < 50% = coverage theater; 70-90% = нормально; 90%+ = отлично 🔗 См. Q12
> - [ ] Mutation score 100% означает, что в коде нет багов | ❌ ПОСЛЕДСТВИЕ: 100% mutation score означает что все мутанты убиты тестами; это не гарантирует отсутствие логических багов — тесты покрывают только реализованные сценарии
> - [ ] Mutation score всегда равен или выше line coverage | ❌ ПОСЛЕДСТВИЕ: типично mutation score значительно ниже line coverage — именно это и выявляет coverage theater; 90% line при 40% mutation score — обычная картина

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

> [!mcq]
> - [ ] Pitest мутирует только строковые литералы и числовые константы | ❌ ПОСЛЕДСТВИЕ: Pitest мутирует операторы (>, <, ==, +, -), return values, void method calls — без этих мутаций не будут тестированы граничные условия операторов
> - [x] Pitest создаёт: Conditionals Boundary (> → >=), Negate Conditionals (== → !=), Math (+→-), Return Values (true→false), Void Method Calls (удаление) | ✓ ПРИМЕНЯТЬ: Conditionals Boundary выявляет off-by-one — isAdult(age>=18) → мутант age>18 должен быть убит тестом с age=18 📋 ПРАВИЛО: граничные условия = Conditionals Boundary мутатор 🔗 См. Q13
> - [ ] Все мутации Pitest обнаруживаются статическим анализом без запуска тестов | ❌ ПОСЛЕДСТВИЕ: мутации требуют запуска тестов; статический анализ (SonarQube) не заменяет mutation testing — разные инструменты для разных проблем
> - [ ] Pitest не может обнаружить мутации в коде с 100% line coverage | ❌ ПОСЛЕДСТВИЕ: mutation score не зависит от line coverage; 100% line coverage при слабых assertions → Pitest обнаружит 60%+ выживших мутантов

## Q16. Что такое «coverage theater»?

**Coverage theater** — ситуация, когда команда достигает высокого % coverage, но тесты не проверяют реальное поведение. «Театр для метрик».

Проявления:
- Тесты без assertions (просто вызов метода).
- Тесты только «happy path» без edge cases.
- Тесты для увеличения цифр, а не для предотвращения регрессий.
- Мокирование всего подряд — тест ничего реального не проверяет.

Coverage theater опасен: создаёт ложное ощущение безопасности при деградации кода.

---

> [!mcq]
> - [ ] Coverage theater = театральный тест-план с мок-объектами для демонстрации команде | ❌ ПОСЛЕДСТВИЕ: coverage theater — антипаттерн когда команда гонится за высоким % без реальных assertions; мок-объекты сами по себе не являются theater
> - [ ] Coverage theater возможен только при < 50% coverage | ❌ ПОСЛЕДСТВИЕ: coverage theater именно при ВЫСОКОМ coverage без смысла — 95% line coverage при тестах без assertions; низкий coverage не является theater
> - [ ] Coverage theater = ситуация когда тесты занимают больше времени чем написание кода | ❌ ПОСЛЕДСТВИЕ: медленные тесты — проблема производительности CI, не coverage theater; theater = тесты без assertions или с полным мокированием без проверки результата
> - [x] Coverage theater — высокий % coverage при тестах без реальных проверок: нет assertions, только happy path, мокируется всё; создаёт ложное ощущение безопасности | ✓ ПРИМЕНЯТЬ: выявлять через mutation score — theater виден как низкий mutation score при высоком coverage 📋 ПРАВИЛО: theater = coverage без assertions = 0 пользы 🔗 См. Q17

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

> [!mcq]
> - [x] Тест без assertions: вызвать метод не проверяя результат — строки покрыты, но поведение не проверено; обнаруживается Pitest: мутант выживет всегда | ✓ ПРИМЕНЯТЬ: всегда добавлять assertThat/assertEquals; проверить что тест упадёт при намеренном баге 📋 ПРАВИЛО: тест без assert = coverage theater; мутант выживет всегда 🔗 См. Q16
> - [ ] Тест без @Test аннотации увеличивает coverage если метод вызывается из другого теста | ❌ ПОСЛЕДСТВИЕ: без @Test метод не является тестом и JUnit не запускает его; coverage будет только если другой тест явно вызывает этот метод
> - [ ] Тест с @Disabled увеличивает coverage — disabled тесты всё равно участвуют в coverage | ❌ ПОСЛЕДСТВИЕ: @Disabled тесты не запускаются и не дают никакого coverage; coverage только от реально выполненных тестов
> - [ ] Для coverage theater достаточно вызвать статические методы без создания объектов | ❌ ПОСЛЕДСТВИЕ: статические методы тоже требуют assertions; вызов без проверки результата — coverage theater независимо от статичности

## Q18. Как отличить осмысленный coverage от формального?

Признаки осмысленного coverage:
- Каждый тест проверяет конкретное утверждение (assertion).
- Тесты покрывают граничные случаи, а не только happy path.
- Mutation score высокий при высоком coverage.
- Тест падает при намеренном введении бага в тестируемый код.

Инструментальный способ проверки: запустить Pitest. Если mutation score значительно ниже coverage — тесты формальные.

---

> [!mcq]
> - [ ] Осмысленный coverage = тесты с названием testMethodName без параметров | ❌ ПОСЛЕДСТВИЕ: имя теста не влияет на качество; тест testCriticalMethod без assertions — чистый theater
> - [ ] Формальный coverage выявляется только через code review вручную | ❌ ПОСЛЕДСТВИЕ: Pitest автоматически выявляет формальный coverage — если mutation score значительно ниже line coverage, тесты формальные; ручной review дополняет, не заменяет
> - [ ] Mutation score = line coverage при осмысленных тестах | ❌ ПОСЛЕДСТВИЕ: mutation score всегда ниже или равен line coverage — они измеряют разное; одинаковые значения были бы случайностью
> - [x] Осмысленный coverage: каждый тест имеет assertions, покрывает edge cases, mutation score высокий; тест падает при намеренном баге | ✓ ПРИМЕНЯТЬ: запускать Pitest периодически; mutation score << coverage = тесты формальные 📋 ПРАВИЛО: осмысленный coverage обнаруживает мутантов; формальный — нет 🔗 См. Q14

## Q19. Стоит ли гнаться за 100% coverage?

Нет. Причины:

- **Закон убывающей отдачи:** последние 10% coverage требуют непропорциональных усилий.
- **Нетестируемый код:** main-методы, конфигурация, edge-cases инфраструктурных исключений.
- **Distraction от качества:** команда оптимизирует метрику, а не тесты.
- **Dead code может быть покрыт:** тестировать мёртвый код — пустая трата.

Исключение: safety-critical системы (авиация, медицина) требуют 100% MC/DC coverage — это осознанное требование стандартов.

---

> [!mcq]
> - [ ] 100% coverage — стандарт для enterprise Java; SonarQube требует 100% по умолчанию | ❌ ПОСЛЕДСТВИЕ: SonarQube Sonar Way Quality Gate требует 80% на новом коде, не 100%; погоня за 100% создаёт coverage theater
> - [ ] Чем выше coverage — тем лучше, ограничений нет | ❌ ПОСЛЕДСТВИЕ: закон убывающей отдачи — с 80% до 90% coverage усилия удваиваются; команда начинает писать тесты для конфигов и main-методов которые не несут ценности
> - [x] Нет: закон убывающей отдачи, нетестируемый код (main, конфиги), distraction от реального качества; исключение — safety-critical (авиация, медицина) с MC/DC | ✓ ПРИМЕНЯТЬ: оптимальный баланс 80-90% для бизнес-логики; исключать нетестируемый код 📋 ПРАВИЛО: 80%+ для логики = хорошо; 100% = theater риск 🔗 См. Q6
> - [ ] При 80% coverage можно не писать тесты для новых фич | ❌ ПОСЛЕДСТВИЕ: 80% threshold — минимальный порог, не индульгенция; новый код с 0% coverage снижает общую метрику; Quality Gate на новом коде требует ≥80% для каждого PR

## Q20. Что не покрывает обычный code coverage?

- **Correctness** — код выполнен, но результат неверный.
- **Concurrency issues** — гонки данных, deadlocks.
- **Integration** — взаимодействие компонентов в реальном окружении.
- **Performance** — покрытый код может быть медленным.
- **Security** — SQL-injection может находиться в «покрытом» коде.
- **Error handling** — исключения, брошенные внешними системами.

---

> [!mcq]
> - [ ] Code coverage покрывает всё кроме performance проблем | ❌ ПОСЛЕДСТВИЕ: coverage не покрывает: correctness, concurrency, security, integration, performance, error handling — это 6 разных категорий, не одна
> - [ ] Code coverage не выявляет NullPointerException в production | ❌ ПОСЛЕДСТВИЕ: branch coverage с тестом null-пути может предотвратить NPE; coverage не заменяет assertions, но branch coverage на null-ветку помогает
> - [x] Coverage не выявляет: correctness (выполнен но результат неверный), concurrency bugs, security issues, integration failures, performance, внешние exceptions | ✓ ПРИМЕНЯТЬ: дополнять coverage тестами интеграции, security scanning, нагрузочным тестированием 📋 ПРАВИЛО: coverage = факт выполнения; корректность = assertions + integration tests 🔗 См. Q16
> - [ ] Coverage не выявляет только баги в сторонних библиотеках | ❌ ПОСЛЕДСТВИЕ: coverage не обнаруживает баги в своём коде если assertions слабые — correctness, concurrency, security в вашем коде не гарантируются coverage

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

> [!mcq]
> - [ ] В многомодульных проектах JaCoCo автоматически агрегирует данные без конфигурации | ❌ ПОСЛЕДСТВИЕ: без report-aggregate goal каждый модуль генерирует независимый отчёт; SonarQube видит только модули по отдельности без общей картины
> - [ ] Для каждого модуля нужно создавать отдельный SonarQube проект | ❌ ПОСЛЕДСТВИЕ: отдельные Sonar проекты на модули усложняют мониторинг; единый проект с агрегированным coverage отчётом даёт целостную картину
> - [ ] В Gradle суффикс :jacoco автоматически создаёт merged отчёт | ❌ ПОСЛЕДСТВИЕ: в Gradle нет автоматического merged отчёта; нужно вручную создать jacocoMergedReport task с executionData из всех subprojects
> - [x] Maven: report-aggregate goal в отдельном reporting-модуле; Gradle: кастомный JacocoReport task с executionData из всех модулей | ✓ ПРИМЕНЯТЬ: агрегированный отчёт → единый XML для SonarQube 📋 ПРАВИЛО: агрегация нужна явно — Maven report-aggregate, Gradle merge task 🔗 См. Q11

## Q22. Как использовать coverage в code review?

Практики:
- PR должен поддерживать или улучшать coverage (SonarQube PR Decoration показывает это).
- При добавлении новой функциональности — требовать тесты на новый код.
- При обнаружении непокрытой ветки в ревью — задавать вопрос «почему нет теста?».
- Не требовать 100%, но требовать тесты на критическую логику.

Инструментально: настроить Quality Gate «coverage on new code ≥ 80%» — блокирует merge без тестов.

---

> [!mcq]
> - [ ] В code review нужно вручную считать % coverage для каждого изменения | ❌ ПОСЛЕДСТВИЕ: SonarQube PR Decoration автоматически показывает coverage delta на PR; ручной подсчёт ненадёжен и замедляет review
> - [ ] Coverage review нужен только для новых классов, для изменений в старых — нет | ❌ ПОСЛЕДСТВИЕ: изменения в существующих классах могут добавлять непокрытые ветки; Quality Gate "new code ≥ 80%" работает для любых строк в PR
> - [ ] Если coverage не упал — PR можно approve без проверки тестов | ❌ ПОСЛЕДСТВИЕ: coverage может не упасть при добавлении dead code или тестов без assertions; reviewer должен проверять смысл тестов, не только метрику
> - [x] PR должен поддерживать coverage; SonarQube PR Decoration показывает delta; Quality Gate "new code ≥ 80%" блокирует merge без тестов | ✓ ПРИМЕНЯТЬ: настроить SonarQube Quality Gate на PR; не требовать 100%, требовать тесты на критическую логику 📋 ПРАВИЛО: PR decoration + quality gate = автоматический coverage review 🔗 См. Q25

## Q23. Какие компоненты не имеет смысла покрывать тестами?

- **DTO / Record / POJO** — если только геттеры/сеттеры без логики.
- **Конфигурационные классы** (`@Configuration`, `application.yml` binding).
- **main()-методы** Spring Boot приложений.
- **Сгенерированный код** (Mapstruct, QueryDSL, Lombok).
- **Logging-only** методы без бизнес-логики.

Все эти классы нужно явно исключить из JaCoCo через excludes, чтобы не портить метрику.

---

> [!mcq]
> - [ ] Все классы нужно тестировать — только DTO с equals/hashCode можно пропустить | ❌ ПОСЛЕДСТВИЕ: генерируемый код (Mapstruct, QueryDSL), конфиги, main-методы — тестировать бессмысленно; они портят метрику и тратят время команды
> - [ ] Конфигурационные классы нужно тестировать для проверки правильных значений | ❌ ПОСЛЕДСТВИЕ: конфиги проверяются integration тестами, не unit; unit тест @Configuration класса тестирует только что Spring поднялся — нулевая ценность
> - [x] Не нужно тестировать: DTO/POJO без логики, @Configuration, main(), сгенерированный код (Mapstruct/QueryDSL/Lombok), logging-only методы — исключить из JaCoCo через excludes | ✓ ПРИМЕНЯТЬ: явно настроить excludes в JaCoCo; coverage метрика будет отражать реальный бизнес-код 📋 ПРАВИЛО: нет логики = нет смысла в unit тесте 🔗 См. Q10
> - [ ] Исключение классов из coverage скрывает баги | ❌ ПОСЛЕДСТВИЕ: DTO без логики не содержат бизнес-багов; их включение только делает метрику менее точной без добавления безопасности

## Q24. Как внедрить coverage в legacy-проект?

Пошаговый подход:

1. **Измерить baseline** — запустить JaCoCo, зафиксировать текущее состояние (например, 20%).
2. **Не рачет снижения** — установить threshold = текущий уровень. CI не должен давать coverage упасть.
3. **Постепенный рост** — поднимать порог на 1–2% при каждом sprint, покрывая новый и критический код.
4. **Focus on new code** — SonarQube New Code Period: требовать 80% только для нового кода.
5. **Boy Scout Rule** — добавлять тесты при касании файла в рамках задач.

---

> [!mcq]
> - [ ] Первый шаг — установить threshold 80% и достичь его за один спринт | ❌ ПОСЛЕДСТВИЕ: резкий скачок с 20% до 80% нереалистичен за спринт; команда будет писать пустые тесты для метрики вместо осмысленных
> - [ ] Для legacy проекта лучше начать заново с TDD вместо ретро-тестирования | ❌ ПОСЛЕДСТВИЕ: полная переписка legacy ради TDD нереалистична; инкрементальный подход (Boy Scout Rule + New Code threshold) — более практичен
> - [ ] Coverage на legacy нужно измерять только один раз для отчёта руководству | ❌ ПОСЛЕДСТВИЕ: одноразовое измерение не создаёт safety net; threshold в CI предотвращает дальнейшую деградацию при каждом коммите
> - [x] Пошагово: baseline → threshold=текущий % → постепенный рост +1-2% per sprint → SonarQube New Code ≥80% → Boy Scout Rule при касании файла | ✓ ПРИМЕНЯТЬ: не допускать снижения ниже baseline; фокус на новом коде 📋 ПРАВИЛО: legacy: baseline freeze → New Code 80% → Boy Scout Rule 🔗 См. Q22

## Q25. Что такое differential coverage?

**Differential coverage** — измерение покрытия только изменённого кода (diff), а не всего проекта.

Применение: в PR/MR анализировать только строки, добавленные или изменённые в этом PR.

Инструменты:
- **SonarQube New Code** — встроенная поддержка.
- **Codecov / Coveralls** — сервисы с дифференциальным анализом для GitHub.

Преимущество: позволяет установить строгий порог (например, 90%) для нового кода, не требуя мгновенно покрыть унаследованный.

---

> [!mcq]
> - [ ] Differential coverage = разница между coverage в двух запусках тестов | ❌ ПОСЛЕДСТВИЕ: differential coverage — coverage только изменённых строк diff, не разница между запусками; путаница приводит к неверному выбору инструмента
> - [x] Differential coverage измеряет % покрытия только изменённого кода (diff в PR); инструменты: SonarQube New Code, Codecov, Coveralls | ✓ ПРИМЕНЯТЬ: устанавливать строгий порог (90%) для нового кода без требования покрыть legacy 📋 ПРАВИЛО: differential = coverage только diff строк; строгий порог для нового кода 🔗 См. Q24
> - [ ] SonarQube New Code Period — функция для сравнения coverage с предыдущим релизом | ❌ ПОСЛЕДСТВИЕ: New Code Period определяет что считается новым кодом (дата, версия, ветка); это основа дифференциального coverage в SonarQube
> - [ ] Differential coverage требует специального тестового фреймворка | ❌ ПОСЛЕДСТВИЕ: differential coverage — функция инструмента анализа (SonarQube, Codecov), не тестового фреймворка; работает с любыми JUnit/TestNG тестами

## See also

- [Code Review](code-review-interview.md)
- [Static Analysis](static-analysis-interview.md)
- [Code Smells](code-smells-interview.md)
- [Technical Debt](technical-debt-interview.md)
- [Clean Code Practices](clean-code-practices-interview.md)
- [Refactoring Patterns](refactoring-patterns-interview.md)
- [Java Core](../programming-languages/java/java-core-interview.md)
