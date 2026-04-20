---
title: "Обзор инструментов тестирования"
description: "Краткий обзор стека для тестирования на Java: тест-раннеры (JUnit, TestNG), моки (Mockito), утверждения (AssertJ, Hamcrest), отчёты и покрытие (Allure, JaCoCo), интеграция с CI/CD."
tags:
  - testing
  - testing-tools
  - testing-tools-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Обзор инструментов тестирования

Краткий обзор стека для тестирования на Java: тест-раннеры (JUnit, TestNG), моки (Mockito), утверждения (AssertJ, Hamcrest), отчёты и покрытие (Allure, JaCoCo), интеграция с CI/CD.

**Дата обновления:** 2026-02-11

## Полезные ссылки

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ](https://assertj.github.io/doc/)
- [Allure Report](https://docs.qameta.io/allure/)
- [JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
- [TestNG](https://testng.org/doc/documentation-main.html)

## См. также

- [[README|Unit Testing]]
- [[README|Integration Testing]]
- [[README|CI/CD]]
- [[README|Testing Strategies]]
- [Productivity Tools](../../tools/)

## Содержание

- [Введение](#введение)
- [Тест-раннеры](#тест-раннеры)
- [Моки и стабы](#моки-и-стабы)
- [Библиотеки утверждений](#библиотеки-утверждений)
- [Отчёты и отчётность](#отчёты-и-отчётность)
- [Покрытие кода](#покрытие-кода)
- [Интеграция с CI/CD](#интеграция-с-cicd)
- [Рекомендации](#рекомендации)
- [Глоссарий](#глоссарий)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Шпаргалка команд](#шпаргалка-команд)
- [Заключение](#заключение)


## Введение

Инструменты тестирования охватывают:

- **Запуск тестов** — тест-раннеры (JUnit, TestNG)
- **Изоляция зависимостей** — моки и стабы (Mockito)
- **Проверки** — библиотеки утверждений (AssertJ, Hamcrest)
- **Отчёты и метрики** — Allure, JaCoCo, JUnit XML/HTML

Выбор стека влияет на скорость написания тестов, читаемость и поддержку в CI/CD.

### Категории инструментов

| Категория      | Назначение                          | Примеры        |
|----------------|-------------------------------------|----------------|
| Тест-раннеры   | Запуск тестов, жизненный цикл       | JUnit 5, TestNG |
| Моки и стабы   | Подмена зависимостей, задание поведения | Mockito, EasyMock |
| Утверждения    | Проверки ожиданий, сообщения об ошибках | AssertJ, Hamcrest |
| Отчёты         | Визуализация результатов, история   | Allure, HTML-отчёты |
| Покрытие       | Метрики покрытия кода               | JaCoCo, Cobertura |


## Тест-раннеры

### JUnit 5 (Jupiter)

Стандартный фреймворк для юнит- и интеграционных тестов в Java. Модули: Jupiter (API), Vintage (JUnit 4), Platform (запуск).

**Возможности:**

- Аннотации: `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@DisplayName`, `@ParameterizedTest`, `@RepeatedTest`, `@Tag`
- Расширения (Extension API): инжекция зависимостей, таймауты, условное выполнение
- Динамические тесты (`@TestFactory`), вложенные (`@Nested`)
- Теги (`@Tag`) для фильтрации: unit, integration, slow

**Пример:**

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервис заказов")
class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(/* зависимости */);
    }

    @Test
    @DisplayName("создаёт заказ при валидных данных")
    void createOrder_validData_returnsOrder() {
        var request = new OrderRequest("item-1", 2);
        var result = orderService.create(request);
        assertNotNull(result.getId());
        assertEquals(2, result.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("отклоняет некорректное количество")
    void createOrder_invalidQuantity_throws(int qty) {
        var request = new OrderRequest("item-1", qty);
        assertThrows(ValidationException.class, () -> orderService.create(request));
    }
}
```

### TestNG

Альтернатива JUnit: группировка тестов, параметризация, зависимости между тестами.

- Группы (`groups`), зависимости (`dependsOnGroups`, `dependsOnMethods`)
- Параметризация: `@DataProvider`
- Параллельный запуск (методы, классы, тесты)
- Аннотации: `@Test`, `@BeforeMethod`, `@AfterMethod`, `@BeforeSuite`, `@AfterSuite`

**Когда выбирать:** сложные зависимости между тестами или legacy-проект на TestNG; в новых проектах чаще JUnit 5.

| Критерий        | JUnit 5     | TestNG        |
|-----------------|------------|---------------|
| Параметризация  | @ParameterizedTest + источники | @DataProvider |
| Группы          | @Tag       | groups в @Test |
| Зависимости тестов | Нет (рекомендуется независимость) | dependsOnMethods/Groups |
| Расширяемость   | Extension API | Слушатели     |


## Моки и стабы

### Mockito

Де-факто стандарт для моков в Java: создание моков, задание поведения (when/thenReturn, thenThrow), проверка вызовов (verify).

**Возможности:**

- `mock(Class)`, `@Mock`, `@InjectMocks` — создание и внедрение в тестируемый объект
- `when(mock.method()).thenReturn(value)` — стаб возвращаемого значения
- `verify(mock).method(args)` — проверка вызова с ожидаемыми аргументами
- ArgumentMatchers: `any()`, `eq()`, `argThat()`
- `@Captor` — захват аргументов

**Пример:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway gateway;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_success_returnsResult() {
        when(gateway.charge(anyString(), any(BigDecimal.class)))
                .thenReturn(new PaymentResult(true, "tx-123"));

        var result = paymentService.process("order-1", new BigDecimal("99.99"));

        assertTrue(result.isSuccess());
        verify(gateway, times(1)).charge(eq("order-1"), any(BigDecimal.class));
    }
}
```

**Стабирование исключений и обратных вызовов:**

```java
when(repository.findById(1L)).thenThrow(new EntityNotFoundException("Not found"));
when(iterator.hasNext()).thenReturn(true, true, false);
when(iterator.next()).thenReturn("a", "b");
when(service.process(any())).thenAnswer(inv -> Response.of(inv.getArgument(0, Request.class).getId()));
```

**Матчеры аргументов (кратко):** `any()`, `any(Class)`, `eq(value)`, `argThat(Predicate)`, `anyString()`, `anyInt()`, `anyList()`. При использовании матчеров все аргументы в вызове должны быть матчерами.


## Библиотеки утверждений

### AssertJ

Флюентный API: читаемый код и информативные сообщения об ошибках.

- Входная точка: `assertThat(actual).` + цепочка методов
- Объекты: `isNotNull()`, `isEqualTo()`, `hasFieldOrPropertyWithValue()`, `extracting("field")`
- Коллекции: `hasSize()`, `contains()`, `containsExactlyInAnyOrder()`, `filteredOn()`
- Исключения: `assertThatThrownBy(() -> ...).hasMessageContaining("...")`
- Кастомные утверждения: `AbstractAssert` для доменных типов

```java
import static org.assertj.core.api.Assertions.*;

assertThat(order)
    .isNotNull()
    .extracting(Order::getStatus, Order::getTotal)
    .containsExactly(OrderStatus.CONFIRMED, new BigDecimal("100.00"));

assertThat(orders).hasSize(2).extracting(Order::getId).containsExactlyInAnyOrder("id-1", "id-2");

assertThatThrownBy(() -> service.create(null))
    .isInstanceOf(ValidationException.class)
    .hasMessageContaining("request must not be null");
```

### Hamcrest

Матрчеры для `assertThat(actual, matcher)`. Комбинирование: `allOf`, `anyOf`, `both().and()`.

```java
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

assertThat(list, hasSize(3));
assertThat(str, allOf(containsString("foo"), startsWith("bar")));
```

| Критерий   | AssertJ        | Hamcrest              |
|-----------|----------------|------------------------|
| Стиль     | Флюентный      | assertThat(x, matcher) |
| Сообщения об ошибках | Обычно информативнее | Зависит от матчера |
| Кастом    | AbstractAssert | Кастомный Matcher      |


## Отчёты и отчётность

### Allure Report

Отчёты о тестах: шаги, вложения (скриншоты, логи), группировка по сьютам и фичам, тренды.

**Maven:**

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.24.0</version>
    <scope>test</scope>
</dependency>
```

**Пример аннотаций:**

```java
@Epic("Заказы")
@Feature("Создание заказа")
    @Story("Успешное создание")
@Test
@Description("При валидных данных заказ создаётся с id")
    void createOrder_success() {
        Allure.step("Подготовка запроса", () -> { /* ... */ });
        var result = Allure.step("Вызов сервиса", () -> orderService.create(request));
    Allure.step("Проверка", () -> assertThat(result.getId()).isNotBlank());
}
```

Генерация отчёта: `mvn allure:serve` или `gradle allureServe` (результаты в `target/surefire-reports`, `allure-results`).

### Стандартные отчёты JUnit (XML, HTML)

Gradle и Maven по умолчанию пишут результаты в XML для CI. Плагины (например, maven-surefire-report-plugin) генерируют HTML. Для трендов и истории удобнее Allure или отчёты в CI (Jenkins, GitLab, GitHub Actions).


## Покрытие кода

### JaCoCo

Измерение покрытия (строки, ветви, инструкции); интеграция с Maven и Gradle; вывод XML (для CI) и HTML.

**Gradle (Kotlin DSL):**

```kotlin
plugins { jacoco }

jacoco { toolVersion = "0.8.11" }

tasks.test { finalizedBy(tasks.jacocoTestReport) }

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit { minimum = "0.70".toBigDecimal() }
        }
    }
}
```

**Исключение из отчёта:** DTO, конфиги, сгенерированный код — через `<excludes>` (Maven) или `classDirectories.setFrom(fileTree(...).exclude(...))` (Gradle).

**Метрики:** line coverage — процент строк, выполненных хотя бы раз; branch coverage — процент веток (if/else, циклы). Ориентир 70–80% для критичного кода; не гнаться за 100%.


## Интеграция с CI/CD

### Запуск тестов в пайплайне

- **Maven:** `mvn test` (юнит), `mvn verify` (с интеграционными при Failsafe)
- **Gradle:** `test` (юнит), отдельная задача `integrationTest` для интеграционных

Рекомендуется: быстрые юнит-тесты — на каждый коммит/PR; интеграционные — на merge в main или по расписанию.

### Публикация отчётов и артефактов

- Сохранять: surefire-reports, test-results, allure-results
- Jenkins: плагины Allure, JaCoCo
- GitLab CI: артефакты JUnit и JaCoCo для отображения в MR
- GitHub Actions: загрузка артефактов, опционально Allure как статические страницы

### Пример фрагмента GitLab CI

```yaml
test:
  stage: test
  script:
    - ./gradlew test jacocoTestReport
  artifacts:
    reports:
      junit: build/test-results/test//TEST-*.xml
    paths:
      - build/reports/jacoco/
      - build/allure-results
```


## Рекомендации

1. **Тест-раннер:** JUnit 5 для новых проектов; один тест-раннер в проекте; теги (@Tag) для CI.
2. **Моки:** подменять только внешние зависимости (БД, API, очереди); не мокать тип под тестом; `@InjectMocks` и конструкторная инъекция; задавать только нужное поведение.
3. **Утверждения:** предпочитать AssertJ; один логический факт на тест; проверять граничные значения и исключения.
4. **Именование:** схема «метод_сценарий_ожидание» или `@DisplayName`; связанные тесты в `@Nested`; избегать дублирования (фикстуры, параметризация).
5. **Отчёты и покрытие:** JaCoCo в сборке, пороги 70–80%; Allure для интеграционных и E2E с шагами и вложениями.
6. **CI:** юнит-тесты на каждый push/PR; интеграционные — после merge или по расписанию; хранить артефакты (JUnit XML, JaCoCo, Allure).


## Глоссарий

| Термин        | Описание |
|---------------|----------|
| Test runner   | Движок запуска тестов (JUnit Platform, TestNG) |
| Mock          | Объект с подменённым поведением для изоляции кода |
| Stub          | Настроенное поведение мока (возврат значения, исключение) |
| Spy           | Обёртка над реальным объектом с подменой части методов |
| Assertion     | Проверка ожидаемого результата в тесте |
| Покрытие      | Доля кода (строк/веток), выполненных тестами |
| Unit test     | Тест одного класса/метода в изоляции (с моками) |
| Integration test | Тест с реальными зависимостями (БД, API, контейнеры) |


## Решение проблем

*(См. раздел «Частые вопросы» для типичных сценариев.)*

## Частые вопросы

**Нужно ли использовать и JUnit, и TestNG в одном проекте?**
Нет, выберите один тест-раннер для единообразия и простоты конфигурации CI.

**Какой библиотекой утверждений пользоваться?**
AssertJ даёт читаемый код и хорошие сообщения об ошибках; Hamcrest удобен при привычке к матчерам. Для новых проектов часто выбирают AssertJ.

**Как не раздувать тесты моками?**
Мокайте только внешние зависимости (репозитории, клиенты, очереди). Внедряйте через конструктор и @InjectMocks; избегайте глубоких цепочек стабов (RETURNS_DEEP_STUBS только при необходимости).

**Какое покрытие считать достаточным?**
Ориентир 70–80% по строкам/ветвям для критичного кода. Важнее качество сценариев и граничных условий.

**Как запускать только быстрые тесты локально?**
Теги JUnit (@Tag("fast")) или группы TestNG; настройте Gradle/Maven на включение нужных тегов/групп.

**Когда использовать TestNG вместо JUnit 5?**
При существующей базе на TestNG или явной потребности в зависимостях между тестами (dependsOnMethods). В новых проектах чаще JUnit 5.

**Тесты проходят локально, но падают в CI.**
Проверить: таймзоны, локаль, пути к файлам, переменные окружения. В CI — стабильное окружение; @EnabledIf только для локальных сценариев.

**UnnecessaryStubbingException в Mockito.**
Настроен стаб для метода, который не был вызван. Удалить лишний when() или пометить стаб как lenient: `lenient().when(mock.method()).thenReturn(...)`.

**Тесты нестабильны.**
Избегать общего состояния между тестами, таймеров без моков, зависимости от порядка выполнения. Изоляция (@BeforeEach с новыми экземплярами), при необходимости @Execution(SAME_THREAD).

**Покрытие не растёт после добавления тестов.**
Проверить исключения в JaCoCo; убедиться, что тесты вызывают новый код (не только геттеры/сеттеры). Смотреть отчёт по ветвям.

**Allure-отчёт не генерируется.**
После прогона вызывать `allure serve` или плагин allure в Gradle/Maven; каталог allure-results должен содержать JSON-файлы.


## Шпаргалка команд

| Задача           | JUnit 5 / Mockito / AssertJ |
|------------------|-----------------------------|
| Запуск тестов    | `./gradlew test` или `mvn test` |
| Параметризованный тест | @ParameterizedTest + @ValueSource / @CsvSource / @MethodSource |
| Жизненный цикл   | @BeforeAll, @AfterAll, @BeforeEach, @AfterEach |
| Мок + стаб       | when(mock.method()).thenReturn(x); verify(mock).method(args) |
| AssertJ          | assertThat(x).isNotNull().isEqualTo(y); assertThat(list).hasSize(n).contains(...) |
| Покрытие         | `./gradlew test jacocoTestReport` или `mvn test jacoco:report` |

**Версии (ориентировочно):** JUnit 5 — 5.10.x; Mockito — 5.x; AssertJ — 3.24.x; JaCoCo — 0.8.11+; Allure — 2.24.x. Актуальные смотрите в Maven Central.


## Заключение

Набор инструментов (JUnit 5, Mockito, AssertJ, JaCoCo, при необходимости Allure и TestNG) покрывает потребности большинства Java-проектов для юнит- и интеграционного тестирования. Важно соблюдать единый стиль: один тест-раннер, одна основная библиотека утверждений, единые правила именования и организации тестов. Интеграция с CI/CD и хранение артефактов (JUnit XML, JaCoCo) позволяют отслеживать регрессии и качество кода. Для углублённого изучения — разделы «См. также» и официальная документация.

Примеры кода ориентированы на Java 11+. Для Kotlin — MockK и AssertJ для Kotlin; для Spring — spring-boot-starter-test (уже включает JUnit 5, Mockito, AssertJ, Hamcrest).
