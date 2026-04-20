---
title: "AssertJ для Java"
description: "Краткое руководство по AssertJ: читаемые и мощные проверки (assertions) в Java-тестах — fluent API, пользовательские проверки, мягкие проверки (soft assertions), условное тестирование и интеграция с JUnit и Spring Boot."
tags:
  - testing
  - unit-testing
  - assertj
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# AssertJ для Java

Краткое руководство по **AssertJ**: читаемые и мощные проверки (assertions) в Java-тестах — fluent API, пользовательские проверки, мягкие проверки (soft assertions), условное тестирование и интеграция с JUnit и Spring Boot.

## Полезные ссылки

| Раздел | Ссылки |
|--------|--------|
| Документация | [AssertJ Documentation](https://assertj.github.io/doc/), [AssertJ Core Javadoc](https://javadoc.io/doc/org.assertj/assertj-core/latest/index.html) |
| Интеграции | [AssertJ с JUnit 5](https://assertj.github.io/doc/#assertj-core), [AssertJ с Spring](https://docs.spring.io/spring-framework/reference/testing.html), [AssertJ Guava](https://assertj.github.io/doc/#assertj-guava) |
| Практики | [Fluent Assertions (Baeldung)](https://www.baeldung.com/introduction-to-assertj) |

### См. также

- [[junit-advanced|JUnit Advanced]]
- [[mockito-advanced|Mockito Advanced]]
- [[spring-testing|Spring Testing]]

## Содержание

- [Введение](#введение-в-assertj)
- [Зависимости](#зависимости)
- [Базовое использование](#базовое-использование)
- [Проверки по типам](#проверки-по-типам) (числа, строки, коллекции, объекты, исключения, Optional)
- [Мягкие и пользовательские проверки](#мягкие-и-пользовательские-проверки)
- [Spring Boot и JSON](#spring-boot-и-json)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Введение в AssertJ

**AssertJ** — библиотека fluent-проверок для Java: выразительный API для тестов вместо громоздких `assertEquals`/`assertTrue`.

### Почему AssertJ

- **Fluent API** — читаемый цепочный синтаксис.
- **Богатый набор проверок** — для примитивов, строк, коллекций, Map, Optional, исключений, файлов.
- **Пользовательские проверки** — свои классы проверок под домен.
- **Мягкие проверки (soft assertions)** — несколько проверок за раз с отчётом по всем ошибкам.
- **Типобезопасность** — проверки типов на этапе компиляции.
- **Понятные сообщения об ошибках** и хорошая поддержка в IDE.


## Зависимости

### Maven (основной модуль)

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

### BOM (управление версиями)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-bom</artifactId>
            <version>3.24.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Spring Boot

`spring-boot-starter-test` уже включает AssertJ — отдельно подключать не нужно.

### Gradle

```gradle
dependencies {
    testImplementation 'org.assertj:assertj-core:3.24.2'
}
```

### Дополнительные модули

| Модуль | Назначение |
|--------|------------|
| assertj-json | Проверка JSON (структура, путь, схема) |
| assertj-guava | Guava: Multimap, Multiset, Table, Range |
| assertj-joda-time | Joda-Time: DateTime, Duration, Period |
| assertj-db | Проверки таблиц и строк БД |

### Решение проблем зависимостей

- **Конфликт версий** — явно подключите нужную версию `assertj-core` и при необходимости исключите транзитивную из `spring-boot-starter-test`.
- **Зависимость не найдена** — проверьте дерево: `mvn dependency:tree` или `gradle dependencies --configuration testRuntimeClasspath`.
- **IDE не видит методы** — добавьте static import: `import static org.assertj.core.api.Assertions.*;`

**Миграция с JUnit/Hamcrest:**
`assertEquals("John", user.getName())` → `assertThat(user.getName()).isEqualTo("John")`
`assertTrue(list.size() > 0)` → `assertThat(list).isNotEmpty()`
`assertThat(str, containsString("@"))` → `assertThat(str).contains("@")`


## Базовое использование

```java
import static org.assertj.core.api.Assertions.assertThat;

    @Test
    void testUserCreation() {
        User user = userService.createUser("john@example.com", "John Doe");

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void testUserList() {
        List<User> users = userService.getAllUsers();

    assertThat(users).isNotNull().isNotEmpty();
        assertThat(users).extracting("email").contains("john@example.com");
}
```


## Проверки по типам

### Числа и boolean

```java
assertThat(order.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(35.0));
assertThat(order.getItemCount()).isPositive().isLessThan(10);
        assertThat(stats.getConversionRate()).isBetween(0.0, 100.0);

assertThat(userService.isValidEmail("a@b.com")).isTrue();
        assertThat(result.hasErrors()).isFalse();
```

### Строки

```java
assertThat(emailContent)
    .contains("Welcome")
    .containsIgnoringCase("welcome")
    .doesNotContain("Error");
assertThat(userId).matches("[0-9a-f]{8}-[0-9a-f]{4}-...");
assertThat(message).startsWith("Dear").endsWith("Best regards");
```

### Коллекции (List, Set, Map)

```java
assertThat(users)
    .hasSizeGreaterThan(0)
    .extracting("email").doesNotContainNull();
assertThat(users).filteredOn(u -> u.getStatus() == UserStatus.ACTIVE).isNotEmpty();

assertThat(permissions).contains("READ", "WRITE").doesNotHaveDuplicates();

assertThat(config).containsKey("database.url").doesNotContainKey("secret.password");
        assertThat(config).containsEntry("app.name", "MyApplication");
```

### Объекты и рекурсивное сравнение

```java
assertThat(user)
    .hasFieldOrPropertyWithValue("email", "john@example.com")
    .hasNoNullFieldsOrPropertiesExcept("phoneNumber");

        assertThat(actualOrder).usingRecursiveComparison()
    .ignoringFields("id", "createdAt", "updatedAt")
            .isEqualTo(expectedOrder);
```

### Исключения

```java
        assertThatThrownBy(() -> userService.findById(999L))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("not found")
            .hasNoCause();

assertThatThrownBy(() -> userService.createUser("", "John"))
            .isInstanceOf(ValidationException.class)
    .hasMessage("Email is required");

assertThatCode(() -> userService.createUser("a@b.com", "Valid")).doesNotThrowAnyException();

// цепочка исключений
assertThatThrownBy(() -> complexService.process("invalid"))
            .isInstanceOf(BusinessException.class)
            .hasCauseInstanceOf(DataAccessException.class)
    .hasRootCauseInstanceOf(SQLException.class);
```

### Optional

```java
assertThat(userService.findByEmail("john@example.com")).isPresent();
assertThat(optionalUser).hasValueSatisfying(u -> {
    assertThat(u.getEmail()).isEqualTo("john@example.com");
});
assertThat(optionalUser).isEmpty();
// вложенный Optional
assertThat(order.flatMap(Order::getCustomer).map(User::getEmail)).isPresent().hasValue("a@b.com");
```


## Мягкие и пользовательские проверки

### Мягкие проверки (soft assertions)

Все проверки выполняются; в конце вызывается `assertAll()` — тогда тест падает с перечислением всех ошибок.

```java
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(user).isNotNull();
        softly.assertThat(user.getEmail()).isEqualTo("john@example.com");
        softly.assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        softly.assertAll();
```

Вариант с try-with-resources (автоматический вызов `assertAll()`):

```java
        try (SoftAssertions softly = new SoftAssertions()) {
    softly.assertThat(user.getEmail()).isEqualTo("john@example.com");
    softly.assertThat(user.getName()).isNotBlank();
}
```

### Пользовательские проверки

Класс наследуется от `AbstractAssert<MyAssert, MyType>`; статический фабричный метод и методы возвращают `this`.

```java
public class UserAssert extends AbstractAssert<UserAssert, User> {

    public UserAssert(User actual) {
        super(actual, UserAssert.class);
    }

    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    public UserAssert hasEmail(String email) {
        isNotNull();
        if (!Objects.equals(actual.getEmail(), email)) {
            failWithMessage("Expected email <%s> but was <%s>", email, actual.getEmail());
        }
        return this;
    }

    public UserAssert isActive() {
        isNotNull();
        if (actual.getStatus() != UserStatus.ACTIVE) {
            failWithMessage("Expected ACTIVE but was <%s>", actual.getStatus());
        }
        return this;
    }
}

// В тесте
        UserAssert.assertThat(user)
            .hasEmail("john@example.com")
    .isActive();
```

### Условные проверки

```java
assertThat(user).satisfies(u -> {
        if (featureService.isFeatureEnabled("advanced-profiling")) {
        assertThat(u.getProfile()).isNotNull();
    }
});
// проверка по предикату
assertThat(users).filteredOn(u -> u.getType() == UserType.PREMIUM).allMatch(u -> u.getSubscriptionEnd() != null);
```

### BDD-стиль (then/should)

Можно сделать свой класс в стиле «then(user).shouldBeActive().shouldHaveEmail("...")», наследуя `AbstractAssert` и называя методы `should...` — тогда тесты читаются как сценарии.


## Spring Boot и JSON

### Spring MVC + AssertJ

```java
@SpringBootTest
@AutoConfigureMockMvc
class SpringMvcAssertionsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void testUserApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andReturn();

        User user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertThat(user)
            .hasFieldOrPropertyWithValue("email", "user1@example.com")
            .hasNoNullFieldsOrPropertiesExcept("phoneNumber");
    }
}
```

### Контекст Spring

```java
        assertThat(context.containsBean("userService")).isTrue();
        assertThat(context.getBean("userService")).isInstanceOf(UserService.class);
assertThat(context.getEnvironment().getProperty("spring.application.name")).isEqualTo("my-app");
```

### JSON (модуль assertj-json)

```java
                assertThatJson(json)
                    .isObject()
                    .containsKey("id")
                    .containsKey("email")
    .doesNotContainKey("password");

                assertThatJson(json)
                    .isArray()
                    .hasSizeGreaterThan(0)
    .eachElement().isObject().containsKey("id").containsKey("email");
```

### Файлы

```java
assertThat(csvFile).exists().isFile().hasExtension("csv");
        assertThat(contentOf(csvFile))
    .startsWith("id,email,name")
            .contains("john@example.com")
    .doesNotContain("password");
// отчёт: структура и размер
assertThat(reportFile).hasName("user_report.txt").hasSizeGreaterThan(100);
assertThat(contentOf(reportFile)).matches("(?s).*Total Users: \\d+.*");
```


## Лучшие практики

1. **Static import** — для краткости: `import static org.assertj.core.api.Assertions.*;`
2. **Один главный стиль** — везде fluent: `assertThat(x).isEqualTo(y)` вместо смеси с JUnit-assert.
3. **Описания при сбоях** — `assertThat(x).describedAs("проверка поля email").isEqualTo(expected);`
4. **Один удачный пример на концепцию** — не дублировать похожие примеры.
5. **Мягкие проверки** — когда нужно проверить много полей и увидеть все ошибки разом; не забывать `assertAll()`.
6. **Пользовательские проверки** — для сложных доменных правил и повторного использования.


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|--------|
| ClassNotFoundException для AssertJ | Нет или неверная зависимость | Добавить `assertj-core` в `test` scope; в Spring Boot проверить `spring-boot-starter-test`. |
| Ошибки static import в IDE | Настройки автоимпорта | Добавить в автоимпорт `org.assertj.core.api.Assertions.*` или использовать `Assertions.assertThat(...)`. |
| Мягкие проверки не падают при ошибке | Не вызван `assertAll()` | В конце блока обязательно вызвать `softly.assertAll()` или использовать try-with-resources. |
| Пользовательские проверки не в автодополнении | Неверная иерархия | Наследовать `AbstractAssert<MyAssert, MyType>`, конструктор и методы возвращать `this`. |

### Отладка

- Использовать `describedAs("...")` для пояснения в сообщении об ошибке.
- Для сложных объектов — `assertThat(obj).satisfies(o -> { ... })` с несколькими проверками внутри.


## Частые вопросы

**Когда использовать AssertJ?**
Для unit- и интеграционных тестов, когда нужны читаемые проверки, мягкие проверки или доменные проверки. Для тривиальных `assertEquals` можно оставаться на JUnit.

**Как настроить под production?**
AssertJ — только для тестов (`scope test`). В production он не участвует; настройки относятся к тестовому окружению и CI.

**Где актуальная документация?**
В блоке «Полезные ссылки» в начале документа; основное — [AssertJ Documentation](https://assertj.github.io/doc/).


## Заключение

AssertJ даёт выразительный fluent API для проверок в Java-тестах: числа, строки, коллекции, объекты, исключения, Optional, JSON, файлы. Поддерживаются мягкие проверки и пользовательские классы проверок. Интеграция с JUnit 5 и Spring Boot удобна; сообщения об ошибках понятные. Рекомендуется для новых тестов и постепенной замены старых JUnit-assert.

### Кратко по типам проверок

| Категория | Примеры |
|-----------|--------|
| Базовые | Объекты (null, поля), примитивы, строки (содержимое, шаблоны), коллекции (размер, элементы, извлечение полей) |
| Расширенные | Optional (наличие, значение), исключения (тип, сообщение, причина), файлы (существование, содержимое) |
| Специальные | Рекурсивное сравнение, Spring MVC/контекст, JSON (assertj-json), пользовательские проверки, мягкие проверки |

**Дата:** 2026-02-06
