---
title: "Google Guava: Утилиты для Java"
description: "Комплексное руководство по использованию Google Guava — мощной библиотеки утилит для Java, которая расширяет стандартную библиотеку и упрощает разработку."
tags:
  - libraries
  - utility-libraries
  - java-guava
type: "overview"
difficulty: "intermediate"
aliases:
  - "Google Guava"
  - "Утилиты для Java"
  - "Google Guava: Утилиты для Java"
  - "java guava"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Google Guava: Утилиты для Java

**Комплексное руководство по использованию `Google Guava` — мощной библиотеки утилит для `Java`, которая расширяет стандартную библиотеку и упрощает разработку.**

## Полезные ссылки

### Официальная документация
- [Google Guava](https://github.com/google/guava) — репозиторий **Guava**
- [Guava Javadoc](https://guava.dev/releases/snapshot-jre-docs/api/) — **API** документация
- [Guava Wiki](https://github.com/google/guava/wiki) — руководства и примеры

### Интеграция
- [Guava Maven](https://mvnrepository.com/artifact/com.google.guava/guava) — **Maven** зависимости
- [Guava Gradle](https://docs.gradle.org/current/userguide/dependency_management.html) — **Gradle** настройка
- [Guava with Spring Boot](https://spring.io/projects/spring-boot) — **Spring Boot** интеграция


### См. также
- [Apache Commons: Обширная коллекция Java утилит](java-apache-commons.md)
- [Jackson: JSON сериализация в Java](../serialization/jackson.md)
- [Gson](../serialization/java-gson.md)
- [JUnit 5](../testing-libraries/java-junit5.md)
## Содержание

- [Введение в Guava](#введение-в-guava)
  - [Почему Guava?](#почему-guava)
  - [Основные модули Guava](#основные-модули-guava)
  - [История версий](#история-версий)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Настройка в проекте](#настройка-в-проекте)
- [Основные утилиты](#основные-утилиты)
  - [Objects](#objects)
  - [MoreObjects](#moreobjects)
- [Immutable коллекции](#immutable-коллекции)
  - [ImmutableList](#immutablelist)
  - [ImmutableSet](#immutableset)
  - [ImmutableMap](#immutablemap)
- [Новые коллекции](#новые-коллекции)
  - [Multiset](#multiset)
  - [Multimap](#multimap)
  - [BiMap](#bimap)
  - [Table](#table)
- [Функциональное программирование](#функциональное-программирование)
  - [Function](#function)
  - [Predicate](#predicate)
  - [Supplier](#supplier)
- [Утилиты для строк](#утилиты-для-строк)
  - [Joiner](#joiner)
  - [Splitter](#splitter)
  - [CharMatcher](#charmatcher)
- [Утилиты для примитивов](#утилиты-для-примитивов)
  - [Primitive wrappers](#primitive-wrappers)
  - [Ranges](#ranges)
- [Кэширование](#кэширование)
  - [Cache](#cache)
  - [LoadingCache](#loadingcache)
- [EventBus](#eventbus)
- [Утилиты для IO](#утилиты-для-io)
  - [Files](#files)
  - [Resources](#resources)
- [Параллельное программирование](#параллельное-программирование)
  - [Futures](#futures)
  - [RateLimiter](#ratelimiter)
- [Валидация и Preconditions](#валидация-и-preconditions)
  - [Preconditions](#preconditions)
- [Best practices](#best-practices)
  - [1. Использование Immutable коллекций](#1-использование-immutable-коллекций)
  - [2. Null-safe операции](#2-null-safe-операции)
  - [3. Кэширование](#3-кэширование)
  - [4. Event-driven архитектура](#4-event-driven-архитектура)
  - [5. Работа со строками](#5-работа-со-строками)
- [Заключение](#заключение)
  - [Преимущества Guava](#преимущества-guava)
  - [Когда использовать Guava](#когда-использовать-guava)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Альтернативы](#альтернативы)

## Введение в Guava

**Google Guava** — это набор утилит для **Java**, разработанный **Google**, который значительно расширяет возможности стандартной библиотеки **Java**. **Guava** предоставляет **immutable** коллекции, функциональное программирование, утилиты для строк, кэширование и многое другое.

### Почему Guava?

**Guava** предлагает множество преимуществ:**

1. **Расширение стандартной библиотеки** — Добавляет отсутствующие возможности
2. **Immutable by default** — Поощряет использование **immutable** объектов
3. **Функциональное программирование** — **Function**, **Predicate**, **Supplier** и другие
4. **Высокая производительность** — Оптимизированные реализации
5. **Null-safety** — Утилиты для работы с **null** значениями
6. **Широкая экосистема** — Используется в большинстве **Google** проектов
7. **Хорошая документация** — Подробная документация и примеры
8. **Backward compatibility** — Поддержка старых версий **Java**

### Основные модули Guava

- **guava** — Основная библиотека с коллекциями и утилитами
- **guava-testlib** — Утилиты для тестирования
- **guava-gwt** — Поддержка **GWT**
- **guava-android** — Оптимизированная версия для **Android**

### История версий

- **Guava 10+** — Современные версии с **Java** 8+ поддержкой
- **Guava 20+** — Поддержка **Java** 8 **features**
- **Guava 30+** — **Java** 11+ поддержка

## Установка и настройка

### Maven

Зависимость **Guava** в **Maven** (основная библиотека и testlib для тестов).

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.2-jre</version>
</dependency>

<!-- Для тестирования -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava-testlib</artifactId>
    <version>32.1.2-jre</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("com.google.guava:guava:32.1.2-jre")

    // Для тестирования
    testImplementation("com.google.guava:guava-testlib:32.1.2-jre")
}
```

### Настройка в проекте

```java
// В большинстве случаев Guava работает "из коробки"
// Для некоторых функций может потребоваться настройка

@Configuration
public class GuavaConfiguration {

    // Пример конфигурации CacheBuilder
    @Bean
    public Cache<String, Object> applicationCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }
}
```

## Основные утилиты

### Objects

Утилиты для работы с объектами: **equals**, **hashCode**, **toString**.

```java
import com.google.common.base.Objects;

public class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Person person = (Person) obj;
        return age == person.age &&
               Objects.equal(name, person.name);  // null-safe equals
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, age);  // null-safe hashCode
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("age", age)
            .toString();
        // Output: Person{name=John, age=30}
    }
}
```

### MoreObjects

Расширенные утилиты для **toString**.

```java
import com.google.common.base.MoreObjects;

public class DetailedPerson {
    private String name;
    private Integer age;
    private String email;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("age", age)
            .add("email", email)
            .omitNullValues()  // Исключает null значения
            .toString();
    }

    // toString с классом
    public String toStringWithClass() {
        return MoreObjects.toStringHelper(this.getClass())
            .add("name", name)
            .add("age", age)
            .toString();
        // Output: com.example.DetailedPerson{name=John, age=30}
    }
}
```

## Immutable коллекции

### ImmutableList

```java
import com.google.common.collect.ImmutableList;

/
 * Демонстрация работы с ImmutableList из Guava
 * ImmutableList - это неизменяемый список, который гарантирует что элементы не могут быть изменены после создания
 */
public class ImmutableCollectionsExample {

    /
     * Основной метод демонстрации различных способов создания и работы с ImmutableList
     */
    public static void main(String[] args) {
        // Создание immutable списка через статический метод of()
        // Это самый простой способ для небольшого количества элементов
        ImmutableList<String> fruits = ImmutableList.of("apple", "banana", "orange");
        System.out.println(fruits); // [apple, banana, orange]

        // Создание ImmutableList из существующей изменяемой коллекции
        // copyOf() создает копию коллекции, делая ее неизменяемой
        List<String> mutableList = Arrays.asList("one", "two", "three");
        ImmutableList<String> immutable = ImmutableList.copyOf(mutableList);

        // Builder паттерн для создания ImmutableList
        // Полезен когда нужно динамически добавлять элементы
        ImmutableList<String> built = ImmutableList.<String>builder()
            .add("first")  // Добавляем один элемент
            .addAll(Arrays.asList("second", "third"))  // Добавляем несколько элементов сразу
            .build();  // Строим неизменяемый список

        // Попытка изменения immutable коллекции вызовет UnsupportedOperationException
        // Это гарантирует что коллекция останется неизменной
        try {
            fruits.add("grape");  // Попытка добавить элемент
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable collection");
        }

        // Безопасные операции - создают новые коллекции вместо изменения существующих
        ImmutableList<String> reversed = fruits.reverse();  // Создает новый список в обратном порядке
        ImmutableList<String> subList = fruits.subList(0, 2); // Создает новый список с элементами [apple, banana]
    }
}
```

### ImmutableSet

```java
import com.google.common.collect.ImmutableSet;

/
 * Демонстрация работы с ImmutableSet из Guava
 * ImmutableSet - это неизменяемое множество, которое автоматически удаляет дубликаты
 */
public class ImmutableSetExample {

    /
     * Основной метод демонстрации создания и работы с ImmutableSet
     */
    public static void main(String[] args) {
        // Создание immutable set через статический метод of()
        // Set автоматически гарантирует уникальность элементов
        ImmutableSet<String> colors = ImmutableSet.of("red", "green", "blue");

        // Builder с автоматическим дедуплицированием
        // Дубликаты автоматически игнорируются при построении set
        ImmutableSet<String> uniqueColors = ImmutableSet.<String>builder()
            .add("red")    // Добавляем первый элемент
            .add("green")  // Добавляем второй элемент
            .add("red")    // Дубликат будет автоматически игнорирован (set содержит только уникальные элементы)
            .add("blue")   // Добавляем третий элемент
            .build();      // Строим неизменяемое множество

        System.out.println(uniqueColors); // [red, green, blue] - дубликат "red" удален

        // Операции над множествами - создание объединения (union)
        // Создаем два множества для демонстрации операций
        ImmutableSet<String> primary = ImmutableSet.of("red", "blue");      // Основные цвета
        ImmutableSet<String> secondary = ImmutableSet.of("green", "yellow"); // Дополнительные цвета

        // Создаем объединение двух множеств через Builder
        ImmutableSet<String> union = ImmutableSet.<String>builder()
            .addAll(primary)    // Добавляем все элементы из первого множества
            .addAll(secondary) // Добавляем все элементы из второго множества
            .build();           // Строим объединенное множество

        System.out.println(union); // [red, blue, green, yellow] - все уникальные элементы из обоих множеств
    }
}
```

### ImmutableMap

```java
import com.google.common.collect.ImmutableMap;

/
 * Демонстрация работы с ImmutableMap из Guava
 * ImmutableMap - это неизменяемая карта (ключ-значение), которая гарантирует что элементы не могут быть изменены
 */
public class ImmutableMapExample {

    /
     * Основной метод демонстрации различных способов создания и работы с ImmutableMap
     */
    public static void main(String[] args) {
        // Создание immutable map через статический метод of()
        // Пары ключ-значение передаются последовательно: key1, value1, key2, value2, ...
        ImmutableMap<String, Integer> ages = ImmutableMap.of(
            "John", 30,  // Имя и возраст
            "Jane", 25,  // Имя и возраст
            "Bob", 35    // Имя и возраст
        );

        // Builder паттерн для создания ImmutableMap
        // Полезен когда нужно динамически добавлять пары ключ-значение
        ImmutableMap<String, String> capitals = ImmutableMap.<String, String>builder()
            .put("USA", "Washington")    // Добавляем пару: страна -> столица
            .put("UK", "London")         // Добавляем еще одну пару
            .put("France", "Paris")      // Добавляем третью пару
            .build();                    // Строим неизменяемую карту

        // Создание ImmutableMap из существующей изменяемой карты
        // copyOf() создает копию карты, делая ее неизменяемой
        Map<String, Integer> mutableMap = new HashMap<>();
        mutableMap.put("a", 1);  // Добавляем пару в изменяемую карту
        mutableMap.put("b", 2);  // Добавляем еще одну пару
        ImmutableMap<String, Integer> immutable = ImmutableMap.copyOf(mutableMap);

        // Получение значения по ключу
        System.out.println(ages.get("John")); // 30 - получаем возраст John

        // Обратная операция - создание карты с обратными ключами и значениями
        // Создаем карту где ключи и значения поменяны местами
        ImmutableMap<Integer, String> reversed = ImmutableMap.of(1, "one", 2, "two");
        // Примечание: inverse() не существует в ImmutableMap, это пример концепции
        // Для создания обратной карты нужно использовать BiMap из Guava
        System.out.println(reversed); // {1=one, 2=two}
    }
}
```

## Новые коллекции

### Multiset

Коллекция, которая может содержать дубликаты и отслеживать их количество.

```java
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class MultisetExample {

    public static void main(String[] args) {
        Multiset<String> fruits = HashMultiset.create();

        // Добавление элементов (с дубликатами)
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("apple");
        fruits.add("orange");
        fruits.add("apple");

        System.out.println(fruits); // [apple x 3, banana, orange]

        // Количество элементов
        System.out.println(fruits.count("apple")); // 3
        System.out.println(fruits.count("grape")); // 0

        // Общее количество
        System.out.println(fruits.size()); // 5

        // Уникальные элементы
        System.out.println(fruits.elementSet()); // [apple, banana, orange]

        // Удаление
        fruits.remove("apple", 2); // Удалить 2 apple
        System.out.println(fruits.count("apple")); // 1
    }
}
```

### Multimap

**Map**, которая может содержать несколько значений для одного ключа.

```java
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MultimapExample {

    public static void main(String[] args) {
        Multimap<String, String> multimap = ArrayListMultimap.create();

        // Добавление значений
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("fruits", "orange");
        multimap.put("vegetables", "carrot");
        multimap.put("vegetables", "potato");

        System.out.println(multimap);
        // {fruits=[apple, banana, orange], vegetables=[carrot, potato]}

        // Получение всех значений для ключа
        Collection<String> fruits = multimap.get("fruits");
        System.out.println(fruits); // [apple, banana, orange]

        // Все ключи
        System.out.println(multimap.keySet()); // [fruits, vegetables]

        // Все значения
        System.out.println(multimap.values()); // [apple, banana, orange, carrot, potato]

        // Преобразование в обычную Map
        Map<String, Collection<String>> map = multimap.asMap();
        System.out.println(map.get("fruits")); // [apple, banana, orange]
    }
}
```

### BiMap

Двунаправленная **Map**, где можно получить ключ по значению.

```java
import com.google.common.collect.HashBiMap;
import com.google.common.collect.BiMap;

public class BiMapExample {

    public static void main(String[] args) {
        BiMap<String, Integer> biMap = HashBiMap.create();

        biMap.put("one", 1);
        biMap.put("two", 2);
        biMap.put("three", 3);

        System.out.println(biMap); // {one=1, two=2, three=3}

        // Получить ключ по значению
        String key = biMap.inverse().get(2);
        System.out.println(key); // two

        // Обратная map
        BiMap<Integer, String> inverse = biMap.inverse();
        System.out.println(inverse); // {1=one, 2=two, 3=three}

        // Попытка добавить дубликат значения вызовет ошибку
        try {
            biMap.put("four", 1); // Значение 1 уже используется
        } catch (IllegalArgumentException e) {
            System.out.println("Duplicate value not allowed");
        }
    }
}
```

### Table

Двумерная таблица с двумя ключами.

```java
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TableExample {

    public static void main(String[] args) {
        Table<String, String, Integer> grades = HashBasedTable.create();

        // rowKey, columnKey, value
        grades.put("John", "Math", 85);
        grades.put("John", "English", 92);
        grades.put("Jane", "Math", 88);
        grades.put("Jane", "English", 95);

        // Получить значение
        Integer johnMath = grades.get("John", "Math");
        System.out.println("John's Math grade: " + johnMath); // 85

        // Все оценки студента
        Map<String, Integer> johnGrades = grades.row("John");
        System.out.println("John's grades: " + johnGrades); // {Math=85, English=92}

        // Все оценки по предмету
        Map<String, Integer> mathGrades = grades.column("Math");
        System.out.println("Math grades: " + mathGrades); // {John=85, Jane=88}

        // Все значения
        Collection<Integer> allGrades = grades.values();
        System.out.println("All grades: " + allGrades); // [85, 92, 88, 95]
    }
}
```

## Функциональное программирование

### Function

Функциональный интерфейс для преобразования объектов.

```java
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class FunctionExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("alice", "bob", "charlie");

        // Преобразование в заглавные буквы
        Function<String, String> toUpperCase = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toUpperCase();
            }
        };

        List<String> upperNames = Lists.transform(names, toUpperCase);
        System.out.println(upperNames); // [ALICE, BOB, CHARLIE]

        // С Java 8 можно использовать лямбды
        List<String> upperNames2 = Lists.transform(names, String::toUpperCase);
        System.out.println(upperNames2); // [ALICE, BOB, CHARLIE]
    }
}
```

### Predicate

Функциональный интерфейс для фильтрации.

```java
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class PredicateExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        // Фильтр по длине имени
        Predicate<String> longNames = new Predicate<String>() {
            @Override
            public boolean apply(String name) {
                return name.length() > 4;
            }
        };

        Iterable<String> filtered = Iterables.filter(names, longNames);
        System.out.println(Lists.newArrayList(filtered)); // [Alice, Charlie, David]

        // С Java 8
        Iterable<String> filtered2 = Iterables.filter(names, name -> name.length() > 4);
        System.out.println(Lists.newArrayList(filtered2)); // [Alice, Charlie, David]
    }
}
```

### Supplier

Функциональный интерфейс для создания объектов.

```java
import com.google.common.base.Supplier;
import java.util.Random;

public class SupplierExample {

    public static void main(String[] args) {
        // Supplier для случайных чисел
        Supplier<Integer> randomInt = new Supplier<Integer>() {
            private final Random random = new Random();

            @Override
            public Integer get() {
                return random.nextInt(100);
            }
        };

        // Использование в Guava кэше
        Cache<String, Integer> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return randomInt.get();
                }
            });
    }
}
```

## Утилиты для строк

### Joiner

Объединение строк с разделителем.

```java
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Map;

public class JoinerExample {

    public static void main(String[] args) {
        Joiner joiner = Joiner.on(", ").skipNulls();

        // Объединение списка
        String result1 = joiner.join(Arrays.asList("apple", "banana", null, "orange"));
        System.out.println(result1); // apple, banana, orange

        // Объединение массива
        String result2 = joiner.join("one", "two", "three");
        System.out.println(result2); // one, two, three

        // Объединение Map
        Map<String, String> map = ImmutableMap.of("key1", "value1", "key2", "value2");
        String result3 = Joiner.on(" | ").withKeyValueSeparator("=").join(map);
        System.out.println(result3); // key1=value1 | key2=value2

        // С обработкой null
        Joiner nullFriendly = Joiner.on(", ").useForNull("NULL");
        String result4 = nullFriendly.join(Arrays.asList("a", null, "c"));
        System.out.println(result4); // a, NULL, c
    }
}
```

### Splitter

Разделение строк.

```java
import com.google.common.base.Splitter;

public class SplitterExample {

    public static void main(String[] args) {
        String text = "apple,,banana, orange ,grape";

        // Базовое разделение
        Iterable<String> parts = Splitter.on(',')
            .trimResults()  // Убрать пробелы
            .omitEmptyStrings()  // Игнорировать пустые строки
            .split(text);

        System.out.println(Lists.newArrayList(parts)); // [apple, banana, orange, grape]

        // Разделение с лимитом
        Iterable<String> limited = Splitter.on(',')
            .limit(2)
            .split("a,b,c,d");

        System.out.println(Lists.newArrayList(limited)); // [a, b,c,d]

        // Разделение по регулярному выражению
        Iterable<String> regexSplit = Splitter.onPattern("\\s+")
            .trimResults()
            .split("one   two\t\tthree");

        System.out.println(Lists.newArrayList(regexSplit)); // [one, two, three]
    }
}
```

### CharMatcher

Работа с символами в строках.

```java
import com.google.common.base.CharMatcher;

public class CharMatcherExample {

    public static void main(String[] args) {
        String text = "Hello 123 World! @#$";

        // Удалить все цифры
        String noDigits = CharMatcher.digit().removeFrom(text);
        System.out.println(noDigits); // Hello  World! @#$

        // Оставить только буквы
        String onlyLetters = CharMatcher.javaLetter().retainFrom(text);
        System.out.println(onlyLetters); // HelloWorld

        // Заменить пробелы на подчеркивания
        String underscored = CharMatcher.whitespace().replaceFrom(text, "_");
        System.out.println(underscored); // Hello_123_World!_@#$

        // Проверить, содержит ли строка только цифры
        boolean isNumeric = CharMatcher.digit().matchesAllOf("12345");
        System.out.println(isNumeric); // true

        // Подсчитать количество цифр
        int digitCount = CharMatcher.digit().countIn(text);
        System.out.println(digitCount); // 3
    }
}
```

## Утилиты для примитивов

### Primitive wrappers

```java
import com.google.common.primitives.Ints;
import com.google.common.primitives.Doubles;

public class PrimitivesExample {

    public static void main(String[] args) {
        // Преобразование массива int в List<Integer>
        int[] numbers = {1, 2, 3, 4, 5};
        List<Integer> numberList = Ints.asList(numbers);
        System.out.println(numberList); // [1, 2, 3, 4, 5]

        // Поиск максимума/минимума
        int max = Ints.max(numbers);
        int min = Ints.min(numbers);
        System.out.println("Max: " + max + ", Min: " + min); // Max: 5, Min: 1

        // Проверка на вхождение
        boolean contains = Ints.contains(numbers, 3);
        System.out.println(contains); // true

        // Преобразование строки в примитив с default значением
        int value1 = Ints.tryParse("123");
        int value2 = Ints.tryParse("abc"); // вернет null
        System.out.println("Parsed: " + value1 + ", Invalid: " + value2);

        // Работа с double
        double[] doubles = {1.1, 2.2, 3.3};
        double average = Doubles.mean(doubles);
        System.out.println("Average: " + average); // 2.2
    }
}
```

### Ranges

Работа с диапазонами примитивов.

```java
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class RangeExample {

    public static void main(String[] args) {
        // Создание диапазонов
        Range<Integer> range1 = Range.closed(1, 10);    // [1, 10]
        Range<Integer> range2 = Range.open(5, 15);      // (5, 15)
        Range<Integer> range3 = Range.atLeast(20);      // [20, +∞)

        // Проверка вхождения
        System.out.println(range1.contains(5));     // true
        System.out.println(range1.contains(15));    // false
        System.out.println(range3.contains(25));    // true

        // Работа с RangeSet
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10));
        rangeSet.add(Range.closed(15, 20));

        System.out.println(rangeSet.contains(5));   // true
        System.out.println(rangeSet.contains(12));  // false

        // Объединение диапазонов
        rangeSet.add(Range.closed(8, 18));
        System.out.println(rangeSet); // [[1..20]]
    }
}
```

## Кэширование

### Cache

Мощная система кэширования с **eviction policies**.

```java
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class CacheExample {

    public static void main(String[] args) throws Exception {
        // Создание кэша
        Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)  // Максимум 100 элементов
            .expireAfterWrite(10, TimeUnit.MINUTES)  // Истекает через 10 минут после записи
            .expireAfterAccess(5, TimeUnit.MINUTES)  // Истекает через 5 минут после доступа
            .recordStats()  // Включить статистику
            .build();

        // Добавление значений
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        // Получение значений
        String value1 = cache.getIfPresent("key1");
        System.out.println(value1); // value1

        // Получение с загрузчиком
        String value3 = cache.get("key3", () -> {
            System.out.println("Loading key3...");
            return "computed_value3";
        });

        // Статистика
        System.out.println(cache.stats()); // CacheStats{hitCount=1, missCount=1, ...}

        // Инвалидация
        cache.invalidate("key1");
        System.out.println(cache.getIfPresent("key1")); // null

        // Очистка всего кэша
        cache.invalidateAll();
    }
}
```

### LoadingCache

Кэш с автоматической загрузкой.

```java
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader;

public class LoadingCacheExample {

    public static void main(String[] args) throws Exception {
        // Создание LoadingCache
        LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, User>() {
                @Override
                public User load(String key) throws Exception {
                    // Имитация загрузки из базы данных
                    return loadUserFromDatabase(key);
                }
            });

        // Автоматическая загрузка при первом обращении
        User user1 = userCache.get("user123");
        User user2 = userCache.get("user456");

        // Повторное обращение вернет кэшированное значение
        User user1Cached = userCache.get("user123");

        // Загрузка нескольких значений
        ImmutableMap<String, User> users = userCache.getAll(
            Arrays.asList("user123", "user456", "user789"));
    }

    private static User loadUserFromDatabase(String id) {
        // Имитация загрузки пользователя
        return new User(id, "User " + id);
    }

    static class User {
        final String id;
        final String name;

        User(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
```

## EventBus

Простая система событий.

```java
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusExample {

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();

        // Регистрация слушателей
        eventBus.register(new UserEventListener());
        eventBus.register(new OrderEventListener());

        // Отправка событий
        eventBus.post(new UserCreatedEvent("user123", "john@example.com"));
        eventBus.post(new OrderPlacedEvent("order456", 99.99));
        eventBus.post(new GenericEvent("Some message"));
    }

    // События
    static class UserCreatedEvent {
        final String userId;
        final String email;

        UserCreatedEvent(String userId, String email) {
            this.userId = userId;
            this.email = email;
        }
    }

    static class OrderPlacedEvent {
        final String orderId;
        final double amount;

        OrderPlacedEvent(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }
    }

    static class GenericEvent {
        final String message;

        GenericEvent(String message) {
            this.message = message;
        }
    }

    // Слушатели
    static class UserEventListener {
        @Subscribe
        public void handleUserCreated(UserCreatedEvent event) {
            System.out.println("User created: " + event.userId + " (" + event.email + ")");
        }
    }

    static class OrderEventListener {
        @Subscribe
        public void handleOrderPlaced(OrderPlacedEvent event) {
            System.out.println("Order placed: " + event.orderId + " for $" + event.amount);
        }

        @Subscribe
        public void handleGeneric(GenericEvent event) {
            System.out.println("Generic event: " + event.message);
        }
    }
}
```

## Утилиты для `IO`

### Files

Утилиты для работы с файлами.

```java
import com.google.common.io.Files;
import com.google.common.io.CharSource;
import com.google.common.io.CharSink;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class FilesExample {

    public static void main(String[] args) throws Exception {
        File sourceFile = new File("source.txt");
        File destFile = new File("dest.txt");

        // Копирование файлов
        Files.copy(sourceFile, destFile);

        // Чтение всего файла как строка
        String content = Files.asCharSource(sourceFile, StandardCharsets.UTF_8).read();
        System.out.println("File content: " + content);

        // Чтение файла построчно
        ImmutableList<String> lines = Files.asCharSource(sourceFile, StandardCharsets.UTF_8)
            .readLines();
        System.out.println("Lines: " + lines);

        // Запись в файл
        Files.asCharSink(destFile, StandardCharsets.UTF_8)
            .write("Hello, Guava!");

        // Получение расширения файла
        String extension = Files.getFileExtension("document.pdf");
        System.out.println(extension); // pdf

        // Получение имени без расширения
        String nameWithoutExt = Files.getNameWithoutExtension("document.pdf");
        System.out.println(nameWithoutExt); // document
    }
}
```

### Resources

Работа с **classpath** ресурсами.

```java
import com.google.common.io.Resources;
import java.net.URL;

public class ResourcesExample {

    public static void main(String[] args) throws Exception {
        // Чтение ресурса как строки
        String config = Resources.asCharSource(
            Resources.getResource("config.properties"),
            StandardCharsets.UTF_8
        ).read();

        // Получение URL ресурса
        URL resourceUrl = Resources.getResource("data.json");

        // Чтение ресурса как байты
        byte[] data = Resources.toByteArray(resourceUrl);

        System.out.println("Config loaded: " + config.length() + " characters");
    }
}
```

## Параллельное программирование

### Futures

Утилиты для работы с **Future**.

```java
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.*;

public class FuturesExample {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final ListeningExecutorService listeningExecutor =
        MoreExecutors.listeningDecorator(executor);

    public static void main(String[] args) throws Exception {
        // Создание асинхронных задач
        ListenableFuture<String> future1 = listeningExecutor.submit(() -> {
            Thread.sleep(1000);
            return "Task 1 completed";
        });

        ListenableFuture<String> future2 = listeningExecutor.submit(() -> {
            Thread.sleep(1500);
            return "Task 2 completed";
        });

        // Ожидание всех задач
        ListenableFuture<List<String>> allFutures = Futures.allAsList(future1, future2);

        // Преобразование результата
        ListenableFuture<String> transformed = Futures.transform(allFutures,
            results -> String.join(", ", results),
            listeningExecutor);

        // Ожидание результата с таймаутом
        try {
            String result = transformed.get(3, TimeUnit.SECONDS);
            System.out.println("Combined result: " + result);
        } catch (TimeoutException e) {
            System.out.println("Timeout occurred");
        }

        // Очистка
        listeningExecutor.shutdown();
        listeningExecutor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
```

### RateLimiter

Ограничение скорости выполнения операций.

```java
import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterExample {

    public static void main(String[] args) {
        // Создание rate limiter (10 операций в секунду)
        RateLimiter rateLimiter = RateLimiter.create(10.0);

        for (int i = 0; i < 20; i++) {
            // Ожидание разрешения
            double waitTime = rateLimiter.acquire();
            System.out.println("Operation " + i + " - waited " + waitTime + " seconds");

            // Имитация работы
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Проверка доступности без ожидания
        if (rateLimiter.tryAcquire()) {
            System.out.println("Permit acquired immediately");
        } else {
            System.out.println("No permit available");
        }

        // Rate limiter с burst режимом
        RateLimiter burstLimiter = RateLimiter.create(5.0, 1.0, TimeUnit.SECONDS);
        // Позволяет burst до 5 операций, затем 5 в секунду
    }
}
```

## Валидация и Preconditions

### Preconditions

Утилиты для валидации параметров.

```java
import com.google.common.base.Preconditions;

public class PreconditionsExample {

    public static void main(String[] args) {
        processUser("john@example.com", 25);
        processUser("", 150); // Вызовет исключение
    }

    public static void processUser(String email, int age) {
        // Проверка на null
        Preconditions.checkNotNull(email, "Email cannot be null");

        // Проверка с сообщением
        Preconditions.checkArgument(!email.trim().isEmpty(), "Email cannot be empty");

        // Проверка с форматированным сообщением
        Preconditions.checkArgument(age >= 0 && age <= 120,
            "Age must be between 0 and 120, but was: %s", age);

        // Проверка состояния
        Preconditions.checkState(age >= 18, "User must be 18 or older");

        System.out.println("User processed: " + email + ", age: " + age);
    }
}
```

## Best practices

### 1. Использование Immutable коллекций

```java
// ✅ Хорошо
public class Configuration {
    private final ImmutableList<String> servers;
    private final ImmutableMap<String, String> properties;

    public Configuration(List<String> servers, Map<String, String> properties) {
        this.servers = ImmutableList.copyOf(servers);
        this.properties = ImmutableMap.copyOf(properties);
    }

    public ImmutableList<String> getServers() {
        return servers; // Безопасно возвращать напрямую
    }
}

// ❌ Плохо - mutable коллекции
public class BadConfiguration {
    private final List<String> servers; // Может быть изменена извне

    public BadConfiguration(List<String> servers) {
        this.servers = servers; // Опасно!
    }

    public List<String> getServers() {
        return servers; // Клиент может изменить внутреннее состояние
    }
}
```

### 2. Null-safe операции

```java
// ✅ Хорошо - использование Guava утилит
public class SafeOperations {

    public static String safeToString(Object obj) {
        return Objects.toString(obj, "null");
    }

    public static List<String> safeFilter(List<String> list) {
        return list != null ? ImmutableList.copyOf(Iterables.filter(list,
            Predicates.notNull())) : ImmutableList.of();
    }

    public static String joinNonNull(List<String> parts) {
        return Joiner.on(", ").skipNulls().join(parts);
    }
}

// Использование Optional с Guava
public class OptionalExample {
    public static Optional<String> findUserName(String userId) {
        // Имитация поиска пользователя
        return userId != null && userId.startsWith("user")
            ? Optional.of("User " + userId)
            : Optional.absent();
    }
}
```

### 3. Кэширование

```java
// ✅ Хорошо - правильная настройка кэша
@Service
public class UserService {

    private final LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .refreshAfterWrite(10, TimeUnit.MINUTES) // Soft refresh
        .recordStats()
        .build(new CacheLoader<String, User>() {
            @Override
            public User load(String userId) {
                return loadUserFromDatabase(userId);
            }
        });

    public User getUser(String userId) throws ExecutionException {
        return userCache.get(userId);
    }

    public void invalidateUser(String userId) {
        userCache.invalidate(userId);
    }

    // Мониторинг кэша
    public CacheStats getCacheStats() {
        return userCache.stats();
    }
}
```

### 4. Event-driven архитектура

```java
// ✅ Хорошо - использование EventBus
@Service
public class OrderService {

    private final EventBus eventBus;

    public OrderService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request);
        orderRepository.save(order);

        // Отправка события
        eventBus.post(new OrderCreatedEvent(order.getId(), order.getTotal()));

        return order;
    }
}

// Асинхронная обработка событий
@Async
@Component
public class OrderEventHandler {

    @Subscribe
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Отправка email, обновление статистики и т.д.
        sendConfirmationEmail(event.getOrderId());
        updateInventory(event.getOrderId());
    }
}
```

### 5. Работа со строками

```java
// ✅ Хорошо - использование Guava утилит
public class StringProcessing {

    private static final Splitter LINE_SPLITTER = Splitter.on('\n')
        .trimResults()
        .omitEmptyStrings();

    private static final Joiner CSV_JOINER = Joiner.on(',')
        .useForNull("NULL");

    public static List<String> parseLines(String text) {
        return ImmutableList.copyOf(LINE_SPLITTER.split(text));
    }

    public static String toCsv(List<String> values) {
        return CSV_JOINER.join(values);
    }

    public static String cleanInput(String input) {
        return CharMatcher.whitespace()
            .trimAndCollapseFrom(input, ' ')
            .toLowerCase();
    }

    public static boolean isValidEmail(String email) {
        return email != null &&
               email.length() > 3 &&
               email.contains("@") &&
               !CharMatcher.whitespace().matchesAnyOf(email);
    }
}
```


## Заключение

**Guava** — это фундаментальная библиотека для **Java** проектов, которая значительно расширяет возможности стандартной библиотеки. Она предоставляет:**

### Преимущества Guava

1. **Immutable коллекции** — Безопасность и производительность
2. **Функциональное программирование** — **Function**, **Predicate**, **Supplier**
3. **Утилиты для строк** — **Joiner**, **Splitter**, **CharMatcher**
4. **Кэширование** — Мощная система с **eviction policies**
5. **EventBus** — Простая система событий
6. **Примитивы** — Эффективная работа с примитивными типами
7. **Null-safety** — Защита от **null pointer exceptions**
8. **Высокая производительность** — Оптимизированные реализации

### Когда использовать Guava

**Рекомендуется:**
- **Enterprise** приложения
- Проекты с **complex** бизнес-логикой
- Системы с высокими требованиями к производительности
- Проекты с **event-driven** архитектурой
- Приложения с интенсивной работой со строками

**Особенно полезно:**
- Для создания **immutable DTO**
- В многоуровневых приложениях
- При работе с коллекциями
- В высоконагруженных системах
- При реализации кэширования

### Основные паттерны использования

1. **Immutable `Collections` паттерн** — Всегда использовать **ImmutableList**/**Map**/**Set**
2. **Builder паттерн** — Для сложных объектов конфигурации
3. **Cache паттерн** — **LoadingCache** для **expensive** операций
4. **EventBus паттерн** — Для **decoupling** компонентов
5. **Preconditions паттерн** — Для валидации параметров
6. **Function/`Predicate` паттерн** — Для функционального программирования

### Альтернативы

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **Guava** | Полная экосистема, **Google quality** | Большой размер |
| **Apache Commons** | Легковесная, модульная | Меньше возможностей |
| **Eclipse Collections** | Специализирована на коллекциях | Не так популярна |
| **Vavr** | Функциональное программирование | Только **JVM** |

**Guava** является стандартом де-факто для большинства **Java** проектов и рекомендуется как обязательная зависимость для **enterprise** приложений.


[⬆ Наверх](../)

