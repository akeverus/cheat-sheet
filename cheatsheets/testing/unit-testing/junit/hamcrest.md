---
title: "Hamcrest для Java"
description: "Библиотека матчеров для написания выразительных и читаемых assertions в тестах. Используется с JUnit, Mockito и другими фреймворками."
tags:
  - testing
  - unit-testing
  - hamcrest
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Hamcrest для Java

Библиотека матчеров для написания выразительных и читаемых assertions в тестах. Используется с JUnit, Mockito и другими фреймворками.

**Дата:** 2026-02-06

## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Документация | [Hamcrest Tutorial](http://hamcrest.org/JavaHamcrest/tutorial), [Javadoc](http://hamcrest.org/JavaHamcrest/javadoc/) |
| GitHub | [hamcrest/JavaHamcrest](https://github.com/hamcrest/JavaHamcrest) |
| См. также | [junit](junit.md), [assertj](assertj.md), [mockito](mockito.md) |

## Содержание

- [Подключение](#подключение)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Зачем Hamcrest](#зачем-hamcrest)
- [Базовые матчеры](#базовые-матчеры)
- [Числовые матчеры](#числовые-матчеры)
- [Строковые матчеры](#строковые-матчеры)
- [Матчеры коллекций](#матчеры-коллекций)
- [Матчеры объектов и типов](#матчеры-объектов-и-типов)
- [Логические комбинации](#логические-комбинации)
- [Создание своего матчера](#создание-своего-матчера)
- [Hamcrest + Mockito](#hamcrest-mockito)
- [Hamcrest vs AssertJ](#hamcrest-vs-assertj)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [См. также](#см-также)

## Подключение

### Maven

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
    testImplementation 'org.hamcrest:hamcrest:2.2'
```

> **Примечание:** начиная с версии 2.x единственный артефакт — `hamcrest`. Старые `hamcrest-core` и `hamcrest-library` больше не нужны.

Импорт в тестах:

```java
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
```


## Зачем Hamcrest

Hamcrest делает assertions **читаемыми как предложения** и даёт **понятные сообщения об ошибках**:

```java
// Стандартный JUnit — непонятно, что проверяем
assertEquals(3, list.size());

// Hamcrest — читается как «убедись, что list имеет размер 3»
assertThat(list, hasSize(3));
```

При ошибке Hamcrest покажет:

```text
Expected: a collection with size <3>
     but: collection size was <5>
```

Основные преимущества:
- **Читаемость** — `assertThat(value, is(greaterThan(5)))` понятнее чем `assertTrue(value > 5)`
- **Информативные ошибки** — показывает ожидание и реальность
- **Композиция** — матчеры комбинируются через `allOf`, `anyOf`, `not`
- **Расширяемость** — легко писать свои матчеры


## Базовые матчеры

| Матчер | Что проверяет | Пример |
|--------|---------------|--------|
| `is(value)` | Равенство | `assertThat(x, is(42))` |
| `equalTo(value)` | Равенство (то же что `is`) | `assertThat(x, equalTo(42))` |
| `not(matcher)` | Отрицание | `assertThat(x, not(0))` |
| `nullValue()` | Значение null | `assertThat(x, nullValue())` |
| `notNullValue()` | Значение не null | `assertThat(x, notNullValue())` |
| `sameInstance(obj)` | Тот же объект (==) | `assertThat(a, sameInstance(b))` |
| `instanceOf(Class)` | Тип объекта | `assertThat(obj, instanceOf(String.class))` |

```java
    @Test
void базовыеПроверки() {
    String name = "Hamcrest";

    assertThat(name, is(notNullValue()));
    assertThat(name, is("Hamcrest"));
    assertThat(name, instanceOf(String.class));
    assertThat(name, not(equalTo("JUnit")));
}
```


## Числовые матчеры

| Матчер | Что проверяет |
|--------|---------------|
| `greaterThan(n)` | Больше n |
| `greaterThanOrEqualTo(n)` | Больше или равно n |
| `lessThan(n)` | Меньше n |
| `lessThanOrEqualTo(n)` | Меньше или равно n |
| `closeTo(value, delta)` | Приблизительное равенство |

```java
    @Test
void числовыеПроверки() {
    double pi = 3.14159;

    assertThat(pi, greaterThan(3.0));
    assertThat(pi, lessThan(4.0));
    assertThat(pi, closeTo(3.14, 0.01));

    int count = 10;
    assertThat(count, is(both(greaterThan(5)).and(lessThan(20))));
}
```


## Строковые матчеры

| Матчер | Что проверяет |
|--------|---------------|
| `containsString(s)` | Содержит подстроку |
| `startsWith(s)` | Начинается с |
| `endsWith(s)` | Заканчивается на |
| `equalToIgnoringCase(s)` | Равно без учёта регистра |
| `isEmptyString()` | Пустая строка |
| `isEmptyOrNullString()` | Пустая или null |
| `matchesPattern(regex)` | Соответствует регулярному выражению |
| `stringContainsInOrder(s1, s2)` | Содержит подстроки по порядку |

```java
    @Test
void строковыеПроверки() {
    String msg = "Hello, World!";

    assertThat(msg, containsString("World"));
    assertThat(msg, startsWith("Hello"));
    assertThat(msg, endsWith("!"));
    assertThat(msg, equalToIgnoringCase("hello, world!"));
    assertThat(msg, matchesPattern("Hello.*!"));
    assertThat(msg, stringContainsInOrder("Hello", "World"));
}
```


## Матчеры коллекций

| Матчер | Что проверяет |
|--------|---------------|
| `hasSize(n)` | Размер коллекции |
| `empty()` | Коллекция пуста |
| `hasItem(x)` | Содержит элемент |
| `hasItems(x, y)` | Содержит все указанные |
| `contains(x, y, z)` | Содержит точно в таком порядке |
| `containsInAnyOrder(x, y)` | Содержит точно, порядок неважен |
| `everyItem(matcher)` | Каждый элемент соответствует |
| `hasEntry(k, v)` | Map содержит пару ключ-значение |
| `hasKey(k)` | Map содержит ключ |
| `hasValue(v)` | Map содержит значение |

```java
    @Test
void коллекции() {
    List<String> fruits = List.of("яблоко", "банан", "вишня");

    assertThat(fruits, hasSize(3));
    assertThat(fruits, hasItem("банан"));
    assertThat(fruits, hasItems("яблоко", "вишня"));
    assertThat(fruits, contains("яблоко", "банан", "вишня"));         // точный порядок
    assertThat(fruits, containsInAnyOrder("вишня", "яблоко", "банан")); // любой порядок
    assertThat(fruits, everyItem(not(isEmptyString())));
    }

    @Test
void карты() {
    Map<String, Integer> prices = Map.of("яблоко", 100, "банан", 80);

    assertThat(prices, hasEntry("яблоко", 100));
    assertThat(prices, hasKey("банан"));
    assertThat(prices, hasValue(80));
    assertThat(prices.entrySet(), hasSize(2));
}
```


## Матчеры объектов и типов

```java
    @Test
void объектыИТипы() {
    Object obj = "строка";

    assertThat(obj, instanceOf(String.class));
    assertThat(obj, isA(String.class)); // shortcut для instanceOf

    // Проверка свойств через hasProperty (JavaBeans)
    User user = new User("Иван", 25);
    assertThat(user, hasProperty("name", is("Иван")));
    assertThat(user, hasProperty("age", greaterThan(18)));
}
```

| Матчер | Что проверяет |
|--------|---------------|
| `instanceOf(Class)` | Объект является экземпляром класса |
| `isA(Class)` | То же, но короче |
| `hasProperty(name)` | У объекта есть свойство (геттер) |
| `hasProperty(name, matcher)` | Свойство соответствует матчеру |
| `hasToString(matcher)` | Проверка toString() |


## Логические комбинации

Матчеры можно комбинировать — это одна из сильных сторон Hamcrest:

```java
    @Test
void комбинации() {
    int age = 25;

    // И: оба условия
    assertThat(age, allOf(greaterThan(18), lessThan(65)));
    assertThat(age, is(both(greaterThan(18)).and(lessThan(65)))); // то же

    // ИЛИ: хотя бы одно
    assertThat(age, anyOf(is(25), is(30)));
    assertThat(age, is(either(equalTo(25)).or(equalTo(30))));

    // НЕ
    assertThat(age, not(equalTo(0)));
}
```

| Комбинатор | Логика | Пример |
|------------|--------|--------|
| `allOf(m1, m2)` | И (все совпали) | `allOf(notNullValue(), instanceOf(String.class))` |
| `anyOf(m1, m2)` | ИЛИ (хотя бы один) | `anyOf(is("да"), is("нет"))` |
| `both(m1).and(m2)` | И (более читаемо) | `both(greaterThan(0)).and(lessThan(100))` |
| `either(m1).or(m2)` | ИЛИ (более читаемо) | `either(is("A")).or(is("B"))` |
| `not(matcher)` | НЕ | `not(equalTo(0))` |


## Создание своего матчера

Когда встроенных матчеров недостаточно — пишем свой:

```java
public class IsEvenNumber extends TypeSafeMatcher<Integer> {

    @Override
    protected boolean matchesSafely(Integer number) {
        return number % 2 == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("чётное число");
    }

    @Override
    protected void describeMismatchSafely(Integer item, Description mismatchDescription) {
        mismatchDescription.appendText("было ").appendValue(item).appendText(" (нечётное)");
    }

    // Фабричный метод — вызывается из тестов
    public static Matcher<Integer> isEven() {
        return new IsEvenNumber();
    }
}
```

Использование:

```java
import static com.example.IsEvenNumber.isEven;

    @Test
void свойМатчер() {
    assertThat(42, isEven());
    assertThat(7, not(isEven()));
}
```

При ошибке выведет:

```text
Expected: чётное число
     but: было <7> (нечётное)
```

**Шаблон создания матчера:**
1. Наследовать `TypeSafeMatcher<T>` (безопасный null-check)
2. Реализовать `matchesSafely()` — логика
3. Реализовать `describeTo()` — описание ожидания
4. (опционально) `describeMismatchSafely()` — описание расхождения
5. Добавить статический фабричный метод


## Hamcrest + Mockito

Hamcrest матчеры работают с Mockito через `argThat`:

```java
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

// Стаб: возвращать результат, если аргумент соответствует матчеру
when(service.find(argThat(containsString("admin"))))
    .thenReturn(adminUser);

// Верификация: метод вызван с аргументом, соответствующим матчеру
verify(repository).save(argThat(allOf(
    hasProperty("name", is("Иван")),
    hasProperty("age", greaterThan(18))
)));
```

> **Важно:** используйте `org.mockito.hamcrest.MockitoHamcrest.argThat`, а не `Mockito.argThat` — они принимают разные типы.


## Hamcrest vs AssertJ

| Критерий | Hamcrest | AssertJ |
|----------|----------|---------|
| Стиль | `assertThat(x, is(42))` | `assertThat(x).isEqualTo(42)` |
| Подход | Матчеры-объекты | Fluent API с цепочкой |
| Открываемость API | Нужно знать имена матчеров | IDE подсказывает `.is...` |
| Расширяемость | Свой класс матчера | Свой класс assertions |
| Интеграция с Mockito | Нативная через `argThat` | Отдельная |
| Читаемость ошибок | Хорошая | Отличная |

**Когда выбрать Hamcrest:**
- Проект уже использует Hamcrest
- Нужна интеграция с Mockito `argThat`
- Команда привыкла к стилю `is/has/not`

**Когда выбрать AssertJ:**
- Новый проект — AssertJ удобнее за счёт IDE-автодополнения
- Важна fluent-цепочка: `assertThat(list).hasSize(3).contains("a")`


## Лучшие практики

- **Импортируйте статически** — `import static org.hamcrest.Matchers.*` для краткости
- **Не усложняйте** — `assertThat(x, is(5))` лучше чем `assertThat(x, is(equalTo(5)))`
- **Комбинируйте разумно** — `allOf` из 5+ матчеров трудно читать; вынесите в свой матчер
- **Пишите своё описание** — `describeTo` должно давать понятный текст на случай ошибки
- **Один assert — одна логическая проверка** — не смешивайте независимые проверки
- **Используйте TypeSafeMatcher** — он обрабатывает null и неверные типы за вас


## Решение проблем

| Проблема | Решение |
|----------|---------|
| `NoSuchMethodError` при использовании Hamcrest | Конфликт версий: убедитесь, что `hamcrest:2.2` единственная зависимость; удалите `hamcrest-core` |
| `assertThat` не находится | Импортируйте из `org.hamcrest.MatcherAssert`, а не из JUnit |
| Матчер не подходит по типу | Используйте дженерики: `Matcher<Integer>`, не `Matcher` |
| Сложная проверка — длинная цепочка | Вынесите в свой `TypeSafeMatcher` с понятным именем |
| `hasProperty` не работает | Убедитесь, что есть публичный геттер в стиле JavaBeans |
| Нечитаемая ошибка | Переопределите `describeMismatchSafely()` в своём матчере |


## Частые вопросы

- **Hamcrest 1.x vs 2.x?** — Используйте 2.x. В 1.x разделение на `core`/`library` вызывало конфликты
- **Можно с Kotlin?** — Да, но в Kotlin удобнее `kotest` или `assertk`
- **Нужен ли Hamcrest, если есть AssertJ?** — Для Mockito `argThat` — да; для обычных assertions AssertJ удобнее


## Заключение

Hamcrest — зрелая библиотека матчеров для Java-тестов с выразительным синтаксисом и отличной расширяемостью. Основные сценарии:

- **Читаемые assertions** в JUnit-тестах
- **Кастомные матчеры** для доменной логики
- **Интеграция с Mockito** через `argThat`

Для новых проектов чаще выбирают AssertJ за fluent API, но Hamcrest остаётся стандартом в проектах с Mockito и в legacy-кодовых базах.

## См. также

- [AssertJ для Java](assertj.md)
- [JUnit Advanced для Java](junit-advanced.md)
- [JUnit 5](junit.md)
- [Mockito Advanced для Java](mockito-advanced.md)
- [Mockito](mockito.md)
