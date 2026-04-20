---
title: "Основы Kotlin - Полное руководство"
description: "Комплексное руководство по языку Kotlin: от основ до продвинутых концепций, включая Kotlin 1.9+, DSL, корутины, инлайновые функции"
tags:
  - kotlin
  - jvm
  - android
  - backend
  - functional
  - coroutines
  - dsl
  - null-safety
difficulty: "intermediate"
prerequisites: ["java/java-basics.md"]
next: ["kotlin/kotlin-concurrency-basics.md", "kotlin/kotlin-reactive.md", "kotlin/kotlin-another.md"]
updated: "2026-04-20"
related: ["java/java-basics.md", "kotlin/kotlin-concurrency-basics.md", "kotlin/kotlin-reactive.md"]
---

# Основы Kotlin - Полное руководство

Краткое руководство по языку Kotlin: синтаксис, null-safety, классы, корутины, функциональное программирование.

## Полезные ссылки

### Официальная документация
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Kotlin API](https://kotlinlang.org/api/latest/jvm/stdlib/)

### См. также
- [[java-basics|Java]] — основы Java
- [[kotlin-concurrency-basics|Kotlin Coroutines]] — корутины

## Содержание

- [Практика миграции Kotlin 1.7/1.8 -> 1.9 -> 2.x](#практика-миграции-kotlin-1718-19-2x)
- [Введение в Kotlin](#введение-в-kotlin)
  - [Основные особенности Kotlin](#основные-особенности-kotlin)
  - [История версий Kotlin](#история-версий-kotlin)
    - [Kotlin 1.0 (2016)](#kotlin-10-2016)
    - [Kotlin 1.1 (2016)](#kotlin-11-2016)
    - [Kotlin 1.2 (2017)](#kotlin-12-2017)
    - [Kotlin 1.3 (2018)](#kotlin-13-2018)
    - [Kotlin 1.4 (2020)](#kotlin-14-2020)
    - [Kotlin 1.5 (2021)](#kotlin-15-2021)
    - [Kotlin 1.6 (2021)](#kotlin-16-2021)
    - [Kotlin 1.7 (2022)](#kotlin-17-2022)
    - [Kotlin 1.8 (2022)](#kotlin-18-2022)
    - [Kotlin 1.9 (2023)](#kotlin-19-2023)
- [Основы синтаксиса](#основы-синтаксиса)
  - [Переменные и типы данных](#переменные-и-типы-данных)
  - [Null Safety](#null-safety)
  - [Функции](#функции)
  - [Классы и объекты](#классы-и-объекты)
  - [Наследование и интерфейсы](#наследование-и-интерфейсы)
  - [Generics](#generics)
  - [Коллекции](#коллекции)
  - [Исключения](#исключения)
  - [Расширения (Extensions)](#расширения-extensions)
  - [Data Classes и Sealed Classes](#data-classes-и-sealed-classes)
  - [Inline Classes и Value Classes](#inline-classes-и-value-classes)
  - [Корутины (Coroutines)](#корутины-coroutines)
  - [DSL (Domain Specific Language)](#dsl-domain-specific-language)
  - [Инлайновые функции (Inline Functions)](#инлайновые-функции-inline-functions)
  - [Оператор перегрузка (Operator Overloading)](#оператор-перегрузка-operator-overloading)
  - [Reflection API](#reflection-api)
- [Дополнительные ссылки по разделам (legacy)](#дополнительные-ссылки-по-разделам-legacy)
- [Ссылки по расширенным темам](#ссылки-по-расширенным-темам)
  - [См. также (библиотеки)](#см-также-библиотеки)
- [Функции работы с коллекциями](#функции-работы-с-коллекциями)
  - [Фильтрация](#фильтрация)
  - [Проверка элементов](#проверка-элементов)
  - [Трансформация](#трансформация)
  - [Группировка](#группировка)
  - [Сортировка](#сортировка)
  - [Агрегатные операции](#агрегатные-операции)
- [Inline функции (встроенные)](#inline-функции-встроенные)
  - [Проблема с производительностью лямбд](#проблема-с-производительностью-лямбд)
  - [Использование inline функций](#использование-inline-функций)
  - [noinline](#noinline)
  - [Возврат из inline функций](#возврат-из-inline-функций)
- [Inline классы (встроенные)](#inline-классы-встроенные)
  - [Определение inline класса](#определение-inline-класса)
  - [Свойства и функции в inline классах](#свойства-и-функции-в-inline-классах)
- [Reified функции (овеществлённые)](#reified-функции-овеществлённые)
- [Руководство по Type Alias (псевдонимы)](#руководство-по-type-alias-псевдонимы)
- [Руководство по Delegate](#руководство-по-delegate)
  - [Стандартные делегаты](#стандартные-делегаты)
  - [Lazy делегат](#lazy-делегат)
  - [Observable делегат](#observable-делегат)
- [Руководство по Null Safety](#руководство-по-null-safety)
  - [Nullable типы](#nullable-типы)
  - [Безопасные вызовы](#безопасные-вызовы)
  - [Оператор Elvis](#оператор-elvis)
  - [Оператор !!](#оператор)
- [Несколько переменных в функции let](#несколько-переменных-в-функции-let)
  - [Простой пример let](#простой-пример-let)
  - [Обработка нескольких переменных](#обработка-нескольких-переменных)
- [Обработка исключений](#обработка-исключений)
  - [Try-catch блок](#try-catch-блок)
  - [Try-catch как выражение](#try-catch-как-выражение)
  - [Несколько блоков catch](#несколько-блоков-catch)
  - [Вложенные try-catch](#вложенные-try-catch)
- [Хвостовая рекурсия](#хвостовая-рекурсия)
- [Чтение из файла](#чтение-из-файла)
- [Запись в файл](#запись-в-файл)
- [Работа с JPA](#работа-с-jpa)
  - [Проблемы с Data классами](#проблемы-с-data-классами)
  - [Решение](#решение)
- [Руководство по @JvmField](#руководство-по-jvmfield)
  - [Использование с companion object](#использование-с-companion-object)
  - [Ограничения](#ограничения)
- [Плагин Allopen](#плагин-allopen)
  - [Настройка плагина](#настройка-плагина)
- [Работа со Spring Boot](#работа-со-spring-boot)
- [Регулярные выражения](#регулярные-выражения)
- [Логгирование](#логгирование)
  - [Объект-компаньон](#объект-компаньон)
  - [Метод расширения](#метод-расширения)
  - [Реified generic](#реified-generic)
  - [Делегированное свойство](#делегированное-свойство)
- [Рефлексия](#рефлексия)
- [Аннотации](#аннотации)
  - [Встроенные аннотации](#встроенные-аннотации)
- [Руководство по @Deprecated](#руководство-по-deprecated)
- [Kotlin быстрее, чем Java?](#kotlin-быстрее-чем-java)
- [Преобразование массива в varargs](#преобразование-массива-в-varargs)
- [Расширение Data класса](#расширение-data-класса)
- [Сравнение объектов данных](#сравнение-объектов-данных)
- [Принципы SOLID](#принципы-solid)
- [Type System (Система типов)](#type-system-система-типов)
  - [Базовые типы](#базовые-типы)
  - [Any — корневой тип](#any-корневой-тип)
  - [Unit — тип возвращаемого значения](#unit-тип-возвращаемого-значения)
  - [Nothing — тип без значений](#nothing-тип-без-значений)
  - [Nullable типы](#nullable-типы-1)
  - [Smart Casts (Умные приведения)](#smart-casts-умные-приведения)
- [Type Aliases (Псевдонимы типов)](#type-aliases-псевдонимы-типов)
  - [Базовое использование](#базовое-использование)
  - [Псевдонимы для сложных типов](#псевдонимы-для-сложных-типов)
  - [Практические примеры](#практические-примеры)
- [Object Expressions и Object Declarations](#object-expressions-и-object-declarations)
  - [Object Expressions (Анонимные объекты)](#object-expressions-анонимные-объекты)
  - [Object Declarations (Singleton)](#object-declarations-singleton)
  - [Companion Objects](#companion-objects)
- [Enum Classes](#enum-classes)
  - [Базовое использование](#базовое-использование-1)
  - [Enum с параметрами](#enum-с-параметрами)
  - [Enum с интерфейсами](#enum-с-интерфейсами)
  - [Работа с enum](#работа-с-enum)
- [Delegation (Делегирование)](#delegation-делегирование)
  - [Class Delegation (Делегирование класса)](#class-delegation-делегирование-класса)
  - [Property Delegation (Делегирование свойств)](#property-delegation-делегирование-свойств)
  - [Стандартные делегаты](#стандартные-делегаты-1)
    - [Lazy](#lazy)
    - [Observable](#observable)
    - [Map Delegation](#map-delegation)
- [Infix Functions (Инфиксные функции)](#infix-functions-инфиксные-функции)
  - [Определение infix функций](#определение-infix-функций)
  - [Требования к infix функциям](#требования-к-infix-функциям)
  - [Стандартные infix функции](#стандартные-infix-функции)
- [Destructuring Declarations (Деструктуризация)](#destructuring-declarations-деструктуризация)
  - [Деструктуризация data классов](#деструктуризация-data-классов)
  - [Деструктуризация пар и триплетов](#деструктуризация-пар-и-триплетов)
  - [Деструктуризация Map](#деструктуризация-map)
  - [Деструктуризация в лямбдах](#деструктуризация-в-лямбдах)
  - [Кастомная деструктуризация](#кастомная-деструктуризация)
- [Ranges и Progressions (Диапазоны и прогрессии)](#ranges-и-progressions-диапазоны-и-прогрессии)
  - [Создание диапазонов](#создание-диапазонов)
  - [Итерация по диапазонам](#итерация-по-диапазонам)
  - [Операции с диапазонами](#операции-с-диапазонами)
  - [Progressions (Прогрессии)](#progressions-прогрессии)
- [String Templates (Шаблоны строк)](#string-templates-шаблоны-строк)
  - [Базовое использование](#базовое-использование-2)
  - [Raw Strings (Сырые строки)](#raw-strings-сырые-строки)
  - [String Templates с выражениями](#string-templates-с-выражениями)
- [When Expression (Выражение when)](#when-expression-выражение-when)
  - [Базовое использование](#базовое-использование-3)
  - [Множественные значения](#множественные-значения)
  - [When с диапазонами](#when-с-диапазонами)
  - [When с типами (Smart Cast)](#when-с-типами-smart-cast)
  - [When без аргумента](#when-без-аргумента)
  - [When с функциями](#when-с-функциями)
  - [Exhaustive when (Исчерпывающий when)](#exhaustive-when-исчерпывающий-when)
- [Return и Labels (Возврат и метки)](#return-и-labels-возврат-и-метки)
  - [Обычный return](#обычный-return)
  - [Labeled Return (Возврат с меткой)](#labeled-return-возврат-с-меткой)
  - [Return из лямбд](#return-из-лямбд)
  - [Break и Continue с метками](#break-и-continue-с-метками)
- [Visibility Modifiers (Модификаторы видимости)](#visibility-modifiers-модификаторы-видимости)
  - [Модификаторы для членов класса](#модификаторы-для-членов-класса)
  - [Модификаторы для классов](#модификаторы-для-классов)
  - [Модификаторы для функций](#модификаторы-для-функций)
  - [Модификаторы для конструкторов](#модификаторы-для-конструкторов)
  - [Сравнение с Java](#сравнение-с-java)
  - [Top-level элементы](#top-level-элементы)
- [Расширенное покрытие Generics](#расширенное-покрытие-generics)
  - [Variance (Вариантность)](#variance-вариантность)
    - [Invariance (Инвариантность)](#invariance-инвариантность)
    - [Covariance (Ковариантность) — out](#covariance-ковариантность-out)
    - [Contravariance (Контравариантность) — in](#contravariance-контравариантность-in)
  - [Star Projections (Звездные проекции)](#star-projections-звездные-проекции)
  - [Upper Bounds (Верхние границы)](#upper-bounds-верхние-границы)
  - [Reified Type Parameters (Овеществленные параметры типов)](#reified-type-parameters-овеществленные-параметры-типов)
  - [Generic Functions (Generic функции)](#generic-functions-generic-функции)
  - [Type Erasure (Стирание типов)](#type-erasure-стирание-типов)
- [Расширенное покрытие Reflection](#расширенное-покрытие-reflection)
  - [KClass — информация о классе](#kclass-информация-о-классе)
  - [KFunction — информация о функциях](#kfunction-информация-о-функциях)
  - [KProperty — информация о свойствах](#kproperty-информация-о-свойствах)
  - [KType — информация о типах](#ktype-информация-о-типах)
  - [Аннотации через Reflection](#аннотации-через-reflection)
  - [Вызов функций через Reflection](#вызов-функций-через-reflection)
  - [Работа с конструкторами](#работа-с-конструкторами)
- [Расширенное покрытие Annotations](#расширенное-покрытие-annotations)
  - [Создание аннотаций](#создание-аннотаций)
  - [Мета-аннотации](#мета-аннотации)
  - [Использование аннотаций](#использование-аннотаций)
  - [Встроенные аннотации Kotlin](#встроенные-аннотации-kotlin)
  - [Аннотации для Java Interop](#аннотации-для-java-interop)
- [Multiplatform Projects (Мультиплатформенные проекты)](#multiplatform-projects-мультиплатформенные-проекты)
  - [Общие концепции](#общие-концепции)
  - [Expect/Actual механизм](#expectactual-механизм)
  - [Структура проекта](#структура-проекта)
  - [Общие типы](#общие-типы)
  - [Платформенно-специфичные API](#платформенно-специфичные-api)
- [Расширенное покрытие Data Classes](#расширенное-покрытие-data-classes)
  - [Автоматически генерируемые методы](#автоматически-генерируемые-методы)
  - [Ограничения Data Classes](#ограничения-data-classes)
  - [Наследование Data Classes](#наследование-data-classes)
  - [Custom методы в Data Classes](#custom-методы-в-data-classes)
- [Расширенное покрытие Sealed Classes](#расширенное-покрытие-sealed-classes)
  - [Sealed Classes vs Enum Classes](#sealed-classes-vs-enum-classes)
  - [Sealed Classes с данными](#sealed-classes-с-данными)
  - [Sealed Interfaces (Kotlin 1.5+)](#sealed-interfaces-kotlin-15)
  - [Exhaustive When](#exhaustive-when)
- [Продвинутые техники работы с типами](#продвинутые-техники-работы-с-типами)
  - [Type-safe builders с обобщенными типами](#type-safe-builders-с-обобщенными-типами)
  - [Работа с типами во время выполнения](#работа-с-типами-во-время-выполнения)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Inline функции для производительности](#inline-функции-для-производительности)
  - [Оптимизация коллекций](#оптимизация-коллекций)
- [Работа с аннотациями](#работа-с-аннотациями)
  - [Создание пользовательских аннотаций](#создание-пользовательских-аннотаций)
  - [Аннотации для обработки ошибок](#аннотации-для-обработки-ошибок)
- [Продвинутые паттерны проектирования](#продвинутые-паттерны-проектирования)
  - [Strategy Pattern с функциональным подходом](#strategy-pattern-с-функциональным-подходом)
  - [Observer Pattern с Delegated Properties](#observer-pattern-с-delegated-properties)
- [Продвинутые техники работы с классами](#продвинутые-техники-работы-с-классами)
  - [Nested классы и Inner классы](#nested-классы-и-inner-классы)
  - [Anonymous классы и Object expressions](#anonymous-классы-и-object-expressions)
- [Работа с типами](#работа-с-типами)
  - [Type inference и explicit типы](#type-inference-и-explicit-типы)
  - [Type casting и проверки типов](#type-casting-и-проверки-типов)
- [Работа с исключениями](#работа-с-исключениями)
  - [Продвинутая обработка исключений](#продвинутая-обработка-исключений)
  - [Functional подход к обработке ошибок](#functional-подход-к-обработке-ошибок)
- [Работа с корутинами в базовых концепциях](#работа-с-корутинами-в-базовых-концепциях)
  - [Асинхронное программирование](#асинхронное-программирование)
  - [Flow для обработки потоков данных](#flow-для-обработки-потоков-данных)
- [Работа с рефлексией](#работа-с-рефлексией)
  - [Продвинутая рефлексия](#продвинутая-рефлексия)
- [Дополнение: Extension-подходы для классов](#дополнение-extension-подходы-для-классов)
  - [Extension функции для классов](#extension-функции-для-классов)
  - [Extension свойства](#extension-свойства)
- [Работа с типами и generics](#работа-с-типами-и-generics)
  - [Продвинутые generics](#продвинутые-generics)
  - [Type constraints и bounds](#type-constraints-и-bounds)
- [Дополнительные возможности Kotlin](#дополнительные-возможности-kotlin)
  - [Работа с делегатами](#работа-с-делегатами)
  - [Работа с контрактами](#работа-с-контрактами)
  - [Inline классы (Value Classes)](#inline-классы-value-classes)
- [Дополнительные возможности языка](#дополнительные-возможности-языка)
  - [Работа с контекстами](#работа-с-контекстами)
  - [Работа с контрактами (кратко)](#работа-с-контрактами-кратко)
- [Дополнение: операторы и инварианты](#дополнение-операторы-и-инварианты)
  - [Работа с операторами перегрузки](#работа-с-операторами-перегрузки)
  - [Работа с инвариантами](#работа-с-инвариантами)
- [Дополнительные возможности](#дополнительные-возможности)
  - [Работа с контрактами и инвариантами](#работа-с-контрактами-и-инвариантами)
  - [Работа с типами и type inference](#работа-с-типами-и-type-inference)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Создание data class для модели данных](#создание-data-class-для-модели-данных)
  - [Использование sealed class для состояний](#использование-sealed-class-для-состояний)
  - [Использование extension functions для расширения функциональности](#использование-extension-functions-для-расширения-функциональности)
  - [Использование inline classes для типобезопасности](#использование-inline-classes-для-типобезопасности)
  - [Использование делегатов для ленивой инициализации](#использование-делегатов-для-ленивой-инициализации)
  - [Использование контрактов для оптимизации](#использование-контрактов-для-оптимизации)
- [Лучшие практики](#лучшие-практики)

## Практика миграции Kotlin 1.7/1.8 -> 1.9 -> 2.x

Рабочий сценарий миграции для production-команд:
1. Зафиксировать baseline (тесты, компиляция, предупреждения, performance smoke).
2. Обновить Kotlin plugin + Gradle + toolchain согласованно.
3. Включить строгий режим для предупреждений и последовательно закрыть deprecated API.
4. Проверить ABI/API-совместимость библиотек (особенно serialization, coroutines, reflection-heavy код).
5. Прогнать интеграционные тесты на JVM/Android/Multiplatform сценариях (если применимо).

Практический anti-pattern: обновить только `kotlin-stdlib`, но оставить старый plugin/Gradle stack.

## Введение в Kotlin

**Kotlin** — это статически типизированный язык программирования, разработанный компанией **JetBrains**. Он полностью совместим с **Java** и предназначен для создания надежного, лаконичного и безопасного кода.

### Основные особенности Kotlin

- **Null Safety**: Система типов, предотвращающая **NullPointerException**
- **Conciseness**: Меньше **boilerplate** кода по сравнению с **Java**
- **Interoperability**: Полная совместимость с **Java** кодом
- **Functional Programming**: Поддержка функционального программирования
- **Coroutines**: Легковесная асинхронность
- **DSL Support**: Отличная поддержка создания **DSL**
- **Inline Functions**: **Zero-cost abstractions**

### История версий Kotlin

#### Kotlin `1.0` (`2016`)
- Стабильный релиз
- **Android** поддержка
- **Java interoperability**

#### Kotlin `1.1` (`2016`)
- **Coroutines** (experimental)
- **Inline functions improvements**
- **JavaScript backend**

#### Kotlin `1.2` (`2017`)
- **Multiplatform projects**
- **Array literals** in **annotations**
- **lateinit improvements**

#### Kotlin `1.3` (`2018`)
- **Coroutines stable**
- **Inline classes**
- **Unsigned integers**

#### Kotlin `1.4` (`2020`)
- **SAM conversions for Kotlin interfaces**
- **Mixed named and positional arguments**
- **Trailing comma**

#### Kotlin `1.5` (`2021`)
- **JVM records support**
- **Sealed interfaces**
- **Inline classes improvements**

#### Kotlin `1.6` (`2021`)
- **New Kotlin K2 compiler**
- **Exhaustive when expressions**
- **Type inference improvements**

#### Kotlin `1.7` (`2022`)
- **Definitely non-nullable types**
- **Builder inference**
- **Opt-in requirements**

#### Kotlin `1.8` (`2022`)
- **Improved JVM records support**
- **Value classes** (inline classes successor)
- **Context receivers**

#### Kotlin `1.9` (`2023`)
- **Auto-generated enum entries**
- **Data objects**
- **Improved generics**

## Основы синтаксиса

### Переменные и типы данных

```kotlin
// Неизменяемые переменные (val)
val name: String = "Kotlin"
val age = 25 // Тип выводится автоматически

// Изменяемые переменные (var)
var counter = 0
counter += 1

// Явное указание типов
val explicitInt: Int = 42
val explicitDouble: Double = 3.14
val explicitBoolean: Boolean = true
val explicitChar: Char = 'A'
val explicitString: String = "Hello"

// Массивы
val numbers = arrayOf(1, 2, 3, 4, 5)
val strings = arrayOf("a", "b", "c")
val emptyArray = emptyArray<String>()

// Примитивные массивы для производительности
val intArray = intArrayOf(1, 2, 3)
val doubleArray = doubleArrayOf(1.1, 2.2, 3.3)

// Коллекции
val list = listOf("apple", "banana", "orange") // Неизменяемый
val mutableList = mutableListOf("apple", "banana") // Изменяемый
val set = setOf(1, 2, 3, 2) // {1, 2, 3}
val map = mapOf("key1" to "value1", "key2" to "value2")

// Пары и Triple
val pair = "key" to "value"
val triple = Triple("first", "second", "third")
```

### Null Safety

```kotlin
// Nullable типы
var nullableString: String? = null
nullableString = "Hello"

// Безопасный вызов
val length = nullableString?.length // null если nullableString = null

// Elvis оператор
val lengthOrDefault = nullableString?.length ?: 0

// Not-null assertion (опасно!)
val lengthForced = nullableString!!.length // NPE если null

// Безопасное преобразование типов
fun printLength(obj: Any?) {
    val str = obj as? String // null если не String
    println(str?.length ?: "Not a string")
}

// Smart casts
fun process(obj: Any?) {
    if (obj is String) {
        // obj автоматически преобразован в String
        println(obj.length)
    }
}

// Let function для null checks
nullableString?.let { str ->
    println("String: $str, length: ${str.length}")
}
```

### Функции

```kotlin
// Простая функция
fun greet(name: String): String {
    return "Hello, $name!"
}

// Однострочная функция
fun greetShort(name: String) = "Hello, $name!"

// Функция с параметрами по умолчанию
fun greetWithPrefix(name: String, prefix: String = "Hello"): String {
    return "$prefix, $name!"
}

// Функция с именованными параметрами
val greeting = greetWithPrefix(name = "World", prefix = "Hi")

// Vararg параметры
fun sum(vararg numbers: Int): Int {
    return numbers.sum()
}

val total = sum(1, 2, 3, 4, 5) // 15

// Infix функции
infix fun Int.add(other: Int): Int = this + other
val result = 5 add 3 // 8

// Операторные функции
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
    operator fun unaryMinus() = Point(-x, -y)
}

val p1 = Point(1, 2)
val p2 = Point(3, 4)
val sum = p1 + p2 // Point(4, 6)

// Функции высшего порядка
fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

val add = { x: Int, y: Int -> x + y }
val result = calculate(5, 3, add) // 8

// Inline функции
inline fun measureTime(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("Execution time: ${end - start}ms")
}

measureTime {
    // some code
}
```

### Классы и объекты

```kotlin
// Простой класс
class Person(val name: String, var age: Int) {

    // Вторичный конструктор
    constructor(name: String) : this(name, 0)

    // Метод
    fun introduce() = "Hi, I'm $name, $age years old"

    // Переопределение toString
    override fun toString() = "Person(name='$name', age=$age)"
}

// Data class (автоматически генерирует equals, hashCode, toString, copy, componentN)
data class User(
    val id: Long,
    val name: String,
    val email: String
) {
    // Дополнительные методы
    fun isValid() = name.isNotBlank() && email.contains("@")
}

// Enum class
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF);

    fun getHex() = "#${rgb.toString(16).uppercase()}"
}

// Sealed class
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

// Companion object (статические методы)
class MathUtils {
    companion object {
        fun factorial(n: Int): Long {
            return if (n <= 1) 1 else n * factorial(n - 1)
        }

        const val PI = 3.14159
    }
}

// Использование
val user = User(1, "John", "john@example.com")
val copy = user.copy(email = "john.doe@example.com")
println(user.isValid()) // true
println(MathUtils.factorial(5)) // 120
```

### Наследование и интерфейсы

```kotlin
// Интерфейс
interface Animal {
    val name: String
    fun makeSound()

    // Default implementation
    fun sleep() {
        println("$name is sleeping")
    }
}

// Абстрактный класс
abstract class Mammal(val name: String, var energy: Int = 100) {

    abstract fun move()

    fun eat(food: String) {
        energy += 10
        println("$name ate $food, energy: $energy")
    }
}

// Реализация
class Dog(name: String) : Mammal(name), Animal {

    override fun makeSound() {
        println("Woof!")
    }

    override fun move() {
        energy -= 5
        println("$name runs, energy: $energy")
    }

    // Собственный метод
    fun fetch() {
        energy -= 10
        println("$name fetches the ball")
    }
}

// Использование
val dog = Dog("Buddy")
dog.makeSound() // Woof!
dog.eat("bone") // Buddy ate bone, energy: 110
dog.move()      // Buddy runs, energy: 105
dog.sleep()     // Buddy is sleeping
```

### Generics

```kotlin
// Generic класс
class Box<T>(val value: T) {
    fun get(): T = value
}

// Ограничения типов
class Container<T : Number>(val value: T) {
    fun getValue(): T = value
    fun toDouble(): Double = value.toDouble()
}

// Out (covariant) - producer
interface Producer<out T> {
    fun produce(): T
}

// In (contravariant) - consumer
interface Consumer<in T> {
    fun consume(item: T)
}

// Star projection
fun printArray(array: Array<*>) {
    array.forEach { println(it) }
}

// Generic функции
fun <T> singletonList(item: T): List<T> = listOf(item)

fun <T : Comparable<T>> findMax(items: List<T>): T? {
    return items.maxOrNull()
}

// Reified generics (только для inline функций)
inline fun <reified T> isInstanceOf(obj: Any?): Boolean {
    return obj is T
}

inline fun <reified T> getTypeName(): String {
    return T::class.simpleName ?: "Unknown"
}

// Использование
val stringBox = Box("Hello")
val intBox = Box(42)

val numbers = Container(3.14)
println(numbers.toDouble()) // 3.14

val list = singletonList("item")
val max = findMax(listOf(1, 5, 3, 9, 2)) // 9

println(isInstanceOf<String>("test")) // true
println(getTypeName<List<String>>()) // "ArrayList"
```

### Коллекции

```kotlin
// Создание коллекций
val list = listOf(1, 2, 3, 4, 5) // Неизменяемая
val mutableList = mutableListOf(1, 2, 3) // Изменяемая
val set = setOf(1, 2, 3, 2) // {1, 2, 3}
val map = mapOf("a" to 1, "b" to 2)

// Операции с коллекциями
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Фильтрация
val evenNumbers = numbers.filter { it % 2 == 0 } // [2, 4, 6, 8, 10]
val filtered = numbers.filter { it > 5 } // [6, 7, 8, 9, 10]

// Отображение
val doubled = numbers.map { it * 2 } // [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
val strings = numbers.map { "Number: $it" }

// FlatMap
val nestedLists = listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))
val flat = nestedLists.flatMap { it } // [1, 2, 3, 4, 5, 6]

// Агрегация
val sum = numbers.sum() // 55
val max = numbers.maxOrNull() // 10
val min = numbers.minOrNull() // 1
val average = numbers.average() // 5.5

// Группировка
val byParity = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
// {odd=[1, 3, 5, 7, 9], even=[2, 4, 6, 8, 10]}

// Сортировка
val sorted = numbers.sortedDescending() // [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
val customSort = numbers.sortedBy { -it } // То же самое

// Цепочки операций
val result = numbers
    .filter { it > 3 }
    .map { it * it }
    .take(3)
    .sorted()
// [16, 25, 36] -> [16, 25, 36]

// Sequences для ленивых операций
val sequence = numbers.asSequence()
    .filter { println("Filter: $it"); it > 5 }
    .map { println("Map: $it"); it * 2 }
    .take(2)
    .toList()
// Filter и Map выполняются только для первых двух подходящих элементов

// Mutable операции
val mutableNumbers = mutableListOf(1, 2, 3, 4, 5)
mutableNumbers.add(6)        // [1, 2, 3, 4, 5, 6]
mutableNumbers.remove(3)     // [1, 2, 4, 5, 6]
mutableNumbers[0] = 10       // [10, 2, 4, 5, 6]

// List-specific операции
val first = list.first()     // 1
val last = list.last()       // 5
val elementAt = list.elementAtOrNull(10) // null

// Set операции
val set1 = setOf(1, 2, 3)
val set2 = setOf(3, 4, 5)
val union = set1 union set2        // {1, 2, 3, 4, 5}
val intersect = set1 intersect set2 // {3}
val subtract = set1 subtract set2   // {1, 2}

// Map операции
val mutableMap = mutableMapOf("a" to 1, "b" to 2)
mutableMap["c"] = 3              // {"a"=1, "b"=2, "c"=3}
mutableMap.remove("b")           // {"a"=1, "c"=3}
val value = mutableMap.getOrDefault("d", 0) // 0
```

### Исключения

```kotlin
// Простое исключение
fun divide(a: Int, b: Int): Int {
    if (b == 0) {
        throw IllegalArgumentException("Division by zero")
    }
    return a / b
}

// Try-catch
fun safeDivide(a: Int, b: Int): Result<Int> {
    return try {
        Result.success(a / b)
    } catch (e: ArithmeticException) {
        Result.failure("Division by zero: ${e.message}")
    }
}

// Try как выражение
val result = try {
    riskyOperation()
} catch (e: Exception) {
    defaultValue
}

// Finally
fun readFile(path: String): String? {
    var file: FileInputStream? = null
    return try {
        file = FileInputStream(path)
        file.readBytes().toString(Charsets.UTF_8)
    } catch (e: IOException) {
        println("Error reading file: ${e.message}")
        null
    } finally {
        file?.close()
    }
}

// Use для автоматического закрытия ресурсов
fun readFileSafe(path: String): String? {
    return FileInputStream(path).use { file ->
        file.readBytes().toString(Charsets.UTF_8)
    }
}

// Пользовательские исключения
class ValidationException(message: String) : Exception(message)

class UserNotFoundException(val userId: Long) : Exception("User with id $userId not found")

// Обработка нескольких исключений
fun processData(data: String) {
    try {
        validateData(data)
        processValidatedData(data)
    } catch (e: ValidationException) {
        log.error("Validation failed: ${e.message}")
        throw e
    } catch (e: IOException) {
        log.error("IO error: ${e.message}")
        throw RuntimeException("Processing failed", e)
    } catch (e: Exception) {
        log.error("Unexpected error: ${e.message}")
        throw e
    }
}

// Nothing тип для функций, которые всегда бросают исключения
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

// Elvis throw
val user = findUser(id) ?: throw UserNotFoundException(id)

// Require и check функции
fun processUser(user: User?) {
    require(user != null) { "User cannot be null" }
    check(user.isActive) { "User must be active" }

    // Продолжаем обработку
}

// Result класс для функциональной обработки ошибок
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: String) : Result<Nothing>()

    fun isSuccess() = this is Success
    fun isFailure() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw RuntimeException(error)
    }

    fun getOrDefault(default: T): T = when (this) {
        is Success -> data
        is Failure -> default
    }

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this as Result<R>
    }

    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Failure -> this as Result<R>
    }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun failure(error: String) = Failure(error)
    }
}
```

### Расширения (Extensions)

```kotlin
// Extension функция
fun String.isEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

fun String.capitalizeWords(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}

// Extension свойство
val String.isBlank: Boolean
    get() = this.trim().isEmpty()

// Extension для nullable типов
fun String?.isNullOrBlank(): Boolean {
    return this == null || this.trim().isEmpty()
}

// Generic extension
fun <T> List<T>.secondOrNull(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.randomOrNull(): T? {
    return if (this.isNotEmpty()) this.random() else null
}

// Extension с receiver
class StringBuilderScope {
    private val sb = StringBuilder()

    fun append(text: String) {
        sb.append(text)
    }

    fun appendLine(text: String = "") {
        sb.append(text).append("\n")
    }

    fun toString() = sb.toString()
}

fun buildString(builder: StringBuilderScope.() -> Unit): String {
    val scope = StringBuilderScope()
    scope.builder()
    return scope.toString()
}

// Companion object extensions
class MyClass {
    companion object
}

fun MyClass.Companion.create(): MyClass = MyClass()

// Использование extensions
fun demonstrateExtensions() {
    val email = "test@example.com"
    println(email.isEmail()) // true

    val text = "hello world kotlin"
    println(text.capitalizeWords()) // "Hello World Kotlin"

    val list = listOf(1, 2, 3, 4, 5)
    println(list.secondOrNull()) // 2
    println(list.randomOrNull()) // Случайный элемент

    val html = buildString {
        appendLine("<html>")
        appendLine("  <body>")
        appendLine("    <h1>Hello Kotlin</h1>")
        appendLine("  </body>")
        appendLine("</html>")
    }
    println(html)

    val instance = MyClass.create()
}
```

### Data Classes и Sealed Classes

```kotlin
// Data class
data class Person(
    val id: Long,
    val name: String,
    val age: Int,
    val email: String? = null
) {
    // Валидация в init блоке
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(age >= 0) { "Age cannot be negative" }
        email?.let { require(it.contains("@")) { "Invalid email format" } }
    }

    // Вычисляемые свойства
    val isAdult: Boolean
        get() = age >= 18

    val displayName: String
        get() = name.uppercase()

    // Companion object для фабричных методов
    companion object {
        fun create(name: String, age: Int) = Person(
            id = System.currentTimeMillis(),
            name = name,
            age = age
        )

        fun anonymous() = Person(
            id = -1,
            name = "Anonymous",
            age = 0
        )
    }
}

// Sealed class для ограниченной иерархии
sealed class ApiResponse<out T> {

    data class Success<T>(val data: T) : ApiResponse<T>()

    data class Error(
        val code: Int,
        val message: String,
        val details: Map<String, Any>? = null
    ) : ApiResponse<Nothing>()

    object Loading : ApiResponse<Nothing>()

    // Метод для обработки
    fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Int, String, Map<String, Any>?) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(code, message, details)
        Loading -> onLoading()
    }
}

// Sealed interface (Kotlin 1.5+)
sealed interface NetworkState {
    object Idle : NetworkState
    object Loading : NetworkState
    data class Success(val data: String) : NetworkState
    data class Error(val exception: Exception) : NetworkState
}

// Использование
fun handleApiResponse(response: ApiResponse<User>) {
    val result = response.fold(
        onSuccess = { user -> "User: ${user.name}" },
        onError = { code, message, _ -> "Error $code: $message" },
        onLoading = { "Loading..." }
    )
    println(result)
}

// Pattern matching с sealed classes
fun processNetworkState(state: NetworkState): String = when (state) {
    NetworkState.Idle -> "Ready to start"
    NetworkState.Loading -> "Loading data..."
    is NetworkState.Success -> "Success: ${state.data}"
    is NetworkState.Error -> "Error: ${state.exception.message}"
}
```

### Inline Classes и Value Classes

```kotlin
// Inline class (Kotlin 1.3+)
inline class Password(val value: String) {
    fun isValid(): Boolean = value.length >= 8
    fun strength(): PasswordStrength {
        return when {
            value.length < 8 -> PasswordStrength.WEAK
            value.any { it.isDigit() } && value.any { it.isLetter() } -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }
}

enum class PasswordStrength { WEAK, MEDIUM, STRONG }

// Value class (Kotlin 1.8+)
@JvmInline
value class UserId(val value: Long) {
    fun isValid() = value > 0
    fun toStringFormatted() = "USER_$value"
}

@JvmInline
value class Email(val value: String) {
    fun isValid() = value.contains("@") && value.contains(".")
    fun domain() = value.substringAfter("@")
    fun localPart() = value.substringBefore("@")
}

// Type aliases для читаемости
typealias UserIdMap = Map<UserId, User>
typealias EmailValidator = (String) -> Boolean

// Использование
fun authenticate(password: Password): Boolean {
    return password.isValid() && password.strength() != PasswordStrength.WEAK
}

fun sendEmail(email: Email, message: String) {
    require(email.isValid()) { "Invalid email: ${email.value}" }
    // Send email logic
}

fun findUser(users: UserIdMap, id: UserId): User? {
    return users[id]
}
```

### Корутины (Coroutines)

```kotlin
// Запуск корутин: launch, async, delay
import kotlinx.coroutines.*

// Простая корутина
fun simpleCoroutine() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}

// Suspend функция
suspend fun fetchData(): String {
    delay(1000L) // Имитация сетевого запроса
    return "Data loaded"
}

// Async/await
fun asyncExample() = runBlocking {
    val deferred = async {
        delay(1000L)
        "Async result"
    }

    val result = deferred.await()
    println(result)
}

// Последовательность vs параллельность
fun sequentialVsParallel() = runBlocking {
    val time = measureTimeMillis {
        // Последовательное выполнение
        val data1 = fetchData()
        val data2 = fetchData()
        println("Sequential: $data1, $data2")
    }
    println("Sequential time: ${time}ms")

    val time2 = measureTimeMillis {
        // Параллельное выполнение
        val deferred1 = async { fetchData() }
        val deferred2 = async { fetchData() }

        val data1 = deferred1.await()
        val data2 = deferred2.await()
        println("Parallel: $data1, $data2")
    }
    println("Parallel time: ${time2}ms")
}

// Flow для реактивных потоков
fun flowExample() = runBlocking {
    val flow = flow {
        for (i in 1..5) {
            delay(100L)
            emit(i)
        }
    }

    flow.collect { value ->
        println("Collected: $value")
    }
}

// Channel для коммуникации между корутинами
fun channelExample() = runBlocking {
    val channel = Channel<Int>()

    launch {
        for (i in 1..5) {
            channel.send(i)
            delay(100L)
        }
        channel.close()
    }

    for (value in channel) {
        println("Received: $value")
    }
}

// Exception handling
fun exceptionHandling() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
        } catch (e: CancellationException) {
            println("Job: I'm cancelled")
            throw e
        } finally {
            println("Job: I'm done")
        }
    }

    delay(1300L) // Задержка перед отменой
    job.cancelAndJoin()
    println("Main: Job is cancelled")
}

// SupervisorJob для независимой отмены дочерних корутин
fun supervisorJobExample() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        val job1 = launch {
            delay(1000L)
            println("Job 1 completed")
        }

        val job2 = launch {
            delay(500L)
            throw RuntimeException("Job 2 failed")
        }

        val job3 = launch {
            delay(1500L)
            println("Job 3 completed")
        }

        // Job2 завершается с ошибкой, но job1 и job3 продолжают работать
    }
}

// Context и Dispatcher
fun contextExample() = runBlocking {
    launch(Dispatchers.Default) {
        println("Running on Default dispatcher: ${Thread.currentThread().name}")
    }

    launch(Dispatchers.IO) {
        println("Running on IO dispatcher: ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) {
        println("Running on Unconfined dispatcher: ${Thread.currentThread().name}")
    }

    coroutineContext[CoroutineName]?.let { name ->
        println("Coroutine name: $name")
    }
}
```

### DSL (Domain `Specific` Language)

```kotlin
// Простой DSL для HTML билдера
class HtmlBuilder {
    private val elements = mutableListOf<Element>()

    fun html(init: Html.() -> Unit) {
        val html = Html().apply(init)
        elements.add(html)
    }

    fun build(): String {
        return elements.joinToString("\n") { it.render() }
    }
}

abstract class Element {
    abstract fun render(): String
}

class Html : Element() {
    private val children = mutableListOf<Element>()

    fun head(init: Head.() -> Unit) {
        children.add(Head().apply(init))
    }

    fun body(init: Body.() -> Unit) {
        children.add(Body().apply(init))
    }

    override fun render() = "<html>\n${children.joinToString("\n") { it.render() }}\n</html>"
}

class Head : Element() {
    private var title = ""

    fun title(text: String) {
        title = text
    }

    override fun render() = "  <head>\n    <title>$title</title>\n  </head>"
}

class Body : Element() {
    private val children = mutableListOf<Element>()

    fun h1(text: String) {
        children.add(TextElement("<h1>$text</h1>"))
    }

    fun p(text: String) {
        children.add(TextElement("<p>$text</p>"))
    }

    fun div(init: Div.() -> Unit) {
        children.add(Div().apply(init))
    }

    override fun render() = "  <body>\n${children.joinToString("\n") { "    ${it.render()}" }}\n  </body>"
}

class Div : Element() {
    private val children = mutableListOf<Element>()

    fun p(text: String) {
        children.add(TextElement("<p>$text</p>"))
    }

    override fun render() = "<div>\n${children.joinToString("\n") { "  ${it.render()}" }}\n</div>"
}

class TextElement(private val text: String) : Element() {
    override fun render() = text
}

// Функция-расширение для удобства
fun html(init: Html.() -> Unit): String {
    return HtmlBuilder().apply { html(init) }.build()
}

// Использование DSL
fun createHtmlPage(): String {
    return html {
        head {
            title("My Kotlin DSL Page")
        }
        body {
            h1("Welcome to Kotlin DSL")
            p("This is a simple HTML page created with Kotlin DSL")
            div {
                p("This is inside a div")
                p("Another paragraph in div")
            }
        }
    }
}

// DSL для конфигурации сервера
class ServerConfig {
    var port: Int = 8080
    var host: String = "localhost"
    var ssl: SSLConfig? = null
    val routes = mutableListOf<RouteConfig>()

    fun ssl(init: SSLConfig.() -> Unit) {
        ssl = SSLConfig().apply(init)
    }

    fun route(path: String, method: String = "GET", init: RouteConfig.() -> Unit = {}) {
        routes.add(RouteConfig(path, method).apply(init))
    }
}

class SSLConfig {
    var enabled: Boolean = true
    var keyStore: String = ""
    var keyStorePassword: String = ""
}

class RouteConfig(val path: String, val method: String) {
    var handler: String = ""
    var authRequired: Boolean = false
}

// Функция для конфигурации
fun server(init: ServerConfig.() -> Unit): ServerConfig {
    return ServerConfig().apply(init)
}

// Использование
fun configureServer(): ServerConfig {
    return server {
        port = 8443
        host = "0.0.0.0"

        ssl {
            enabled = true
            keyStore = "/path/to/keystore.jks"
            keyStorePassword = "changeit"
        }

        route("/api/users") {
            handler = "UserController.getUsers"
            authRequired = true
        }

        route("/api/login", "POST") {
            handler = "AuthController.login"
        }
    }
}

// DSL для тестирования
class TestSuite {
    val tests = mutableListOf<TestCase>()

    fun test(name: String, init: TestCase.() -> Unit) {
        tests.add(TestCase(name).apply(init))
    }
}

class TestCase(val name: String) {
    var setup: () -> Unit = {}
    var execute: () -> Unit = {}
    var teardown: () -> Unit = {}
    var assertions = mutableListOf<() -> Boolean>()

    fun setup(block: () -> Unit) {
        setup = block
    }

    fun execute(block: () -> Unit) {
        execute = block
    }

    fun teardown(block: () -> Unit) {
        teardown = block
    }

    fun assert(block: () -> Boolean) {
        assertions.add(block)
    }
}

fun testSuite(init: TestSuite.() -> Unit): TestSuite {
    return TestSuite().apply(init)
}

// Использование
fun runUserTests() {
    val suite = testSuite {
        test("User creation") {
            setup {
                println("Setting up user test")
            }

            execute {
                println("Creating user")
                // actual test logic
            }

            assert { true } // mock assertion

            teardown {
                println("Cleaning up user test")
            }
        }

        test("User validation") {
            execute {
                println("Validating user")
            }

            assert { "test@example.com".contains("@") }
        }
    }

    // Run tests
    suite.tests.forEach { testCase ->
        println("Running test: ${testCase.name}")
        testCase.setup()
        testCase.execute()
        testCase.assertions.forEach { assert ->
            if (!assert()) {
                println("Assertion failed!")
            }
        }
        testCase.teardown()
    }
}
```

### Инлайновые функции (Inline Functions)

```kotlin
// Простая inline функция
inline fun measureTime(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("Execution time: ${end - start}ms")
}

// Inline функция с generic типами
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> T): T {
    return if (condition) block() else this
}

// Inline функция с reified generics
inline fun <reified T> Any?.safeCast(): T? {
    return this as? T
}

// Noinline параметр
inline fun executeAndLog(noinline block: () -> Unit) {
    println("Before execution")
    block()
    println("After execution")
}

// Crossinline для предотвращения non-local returns
inline fun combineResults(crossinline transform: (Int) -> String): List<String> {
    val numbers = listOf(1, 2, 3, 4, 5)
    return numbers.map { number ->
        // Без crossinline это был бы non-local return
        transform(number)
    }
}

// Использование inline функций
fun demonstrateInlineFunctions() {
    // measureTime - код встраивается, нет overhead от lambda
    measureTime {
        var sum = 0L
        for (i in 1..1_000_000) {
            sum += i
        }
        println("Sum: $sum")
    }

    // applyIf
    val result = "hello"
        .applyIf(true) { uppercase() }
        .applyIf(false) { plus(" world") }

    println(result) // "HELLO"

    // safeCast
    val obj: Any = "Hello"
    val string: String? = obj.safeCast<String>()
    val number: Int? = obj.safeCast<Int>()

    println("String: $string") // "Hello"
    println("Number: $number") // null

    // combineResults
    val transformed = combineResults { "Number: $it" }
    println(transformed) // [Number: 1, Number: 2, Number: 3, Number: 4, Number: 5]
}
```

### Оператор перегрузка (Operator Overloading)

```kotlin
// Вектор класс с перегрузкой операторов
data class Vector(val x: Double, val y: Double) {

    // Унарные операторы
    operator fun unaryMinus() = Vector(-x, -y)
    operator fun unaryPlus() = this

    // Бинарные операторы
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)
    operator fun div(scalar: Double) = Vector(x / scalar, y / scalar)

    // Скалярное произведение
    operator fun times(other: Vector) = x * other.x + y * other.y

    // Индексированный доступ
    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Index $index is out of bounds")
    }

    operator fun set(index: Int, value: Double) = when (index) {
        0 -> copy(x = value)
        1 -> copy(y = value)
        else -> throw IndexOutOfBoundsException("Index $index is out of bounds")
    }

    // Сравнение
    operator fun compareTo(other: Vector): Int {
        val thisLength = length()
        val otherLength = length()
        return thisLength.compareTo(otherLength)
    }

    fun length() = kotlin.math.sqrt(x * x + y * y)

    override fun toString() = "(%.2f, %.2f)".format(x, y)
}

// Матрица с перегрузкой операторов
class Matrix(private val data: Array<DoubleArray>) {

    val rows = data.size
    val cols = data[0].size

    operator fun get(row: Int, col: Int) = data[row][col]
    operator fun set(row: Int, col: Int, value: Double) {
        data[row][col] = value
    }

    operator fun plus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { "Matrix dimensions must match" }
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = this[i, j] + other[i, j]
            }
        }
        return Matrix(result)
    }

    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) { "Matrix dimensions incompatible for multiplication" }
        val result = Array(rows) { DoubleArray(other.cols) }
        for (i in 0 until rows) {
            for (j in 0 until other.cols) {
                for (k in 0 until cols) {
                    result[i][j] += this[i, k] * other[k, j]
                }
            }
        }
        return Matrix(result)
    }

    operator fun times(scalar: Double): Matrix {
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = this[i, j] * scalar
            }
        }
        return Matrix(result)
    }

    override fun toString(): String {
        return data.joinToString("\n") { row ->
            row.joinToString(" ", prefix = "[", postfix = "]") { "%.2f".format(it) }
        }
    }

    companion object {
        fun identity(size: Int): Matrix {
            val data = Array(size) { DoubleArray(size) }
            for (i in 0 until size) {
                data[i][i] = 1.0
            }
            return Matrix(data)
        }
    }
}

// Использование
fun demonstrateOperatorOverloading() {
    // Vector operations
    val v1 = Vector(3.0, 4.0)
    val v2 = Vector(1.0, 2.0)

    println("v1: $v1")              // (3.00, 4.00)
    println("v2: $v2")              // (1.00, 2.00)
    println("v1 + v2: ${v1 + v2}")  // (4.00, 6.00)
    println("v1 * 2: ${v1 * 2.0}") // (6.00, 8.00)
    println("v1 · v2: ${v1 * v2}") // 11.0 (dot product)

    // Indexed access
    val v3 = Vector(5.0, 6.0)
    println("v3[0]: ${v3[0]}")      // 5.0
    val v4 = v3[0] = 10.0          // Создает новый Vector(10.0, 6.0)
    println("v4: $v4")              // (10.00, 6.00)

    // Matrix operations
    val m1 = Matrix(arrayOf(
        doubleArrayOf(1.0, 2.0),
        doubleArrayOf(3.0, 4.0)
    ))

    val m2 = Matrix(arrayOf(
        doubleArrayOf(5.0, 6.0),
        doubleArrayOf(7.0, 8.0)
    ))

    println("Matrix 1:")
    println(m1)
    println("Matrix 2:")
    println(m2)

    val sum = m1 + m2
    println("Matrix 1 + Matrix 2:")
    println(sum)

    val product = m1 * m2
    println("Matrix 1 * Matrix 2:")
    println(product)

    val scaled = m1 * 2.0
    println("Matrix 1 * 2.0:")
    println(scaled)
}
```

### Reflection API

```kotlin
// Reflection API: импорты и базовая интроспекция класса
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

// Простая рефлексия
fun demonstrateBasicReflection() {
    val clazz = String::class

    println("Class name: ${clazz.simpleName}")
    println("Qualified name: ${clazz.qualifiedName}")
    println("Is data class: ${clazz.isData}")
    println("Is abstract: ${clazz.isAbstract}")

    // Получение конструкторов
    val constructors = clazz.constructors
    println("Constructors: ${constructors.size}")

    // Получение свойств
    val properties = clazz.members.filterIsInstance<KProperty<*>>()
    println("Properties: ${properties.map { it.name }}")

    // Получение функций
    val functions = clazz.members.filterIsInstance<KFunction<*>>()
    println("Functions: ${functions.map { it.name }}")
}

// Работа с объектами через рефлексию
fun inspectObject(obj: Any) {
    val kClass = obj::class

    println("Object type: ${kClass.simpleName}")

    // Получение всех свойств
    kClass.members
        .filterIsInstance<KProperty1<Any, *>>()
        .forEach { property ->
            try {
                val value = property.get(obj)
                println("${property.name}: $value (${value?.javaClass?.simpleName})")
            } catch (e: Exception) {
                println("${property.name}: <cannot access>")
            }
        }
}

// Создание объектов через рефлексию
fun createInstanceDynamically() {
    val clazz = Person::class

    // Получение первичного конструктора
    val primaryConstructor = clazz.primaryConstructor
        ?: throw IllegalArgumentException("No primary constructor found")

    // Создание экземпляра
    val instance = primaryConstructor.call("John", 30)
    println("Created instance: $instance")

    // Альтернативный способ
    val secondaryConstructor = clazz.constructors.find { it.parameters.size == 1 }
    if (secondaryConstructor != null) {
        val instance2 = secondaryConstructor.call("Jane")
        println("Created instance with secondary constructor: $instance2")
    }
}

// Вызов методов через рефлексию
fun invokeMethodsDynamically() {
    val obj = Person("Alice", 25)

    val kClass = obj::class

    // Вызов метода по имени
    val greetMethod = kClass.members.find { it.name == "introduce" }
    if (greetMethod is KFunction<*>) {
        val result = greetMethod.call(obj)
        println("Method result: $result")
    }

    // Установка свойства
    val nameProperty = kClass.members.find { it.name == "name" }
    if (nameProperty is KMutableProperty1<*, *>) {
        nameProperty.setter.call(obj, "Bob")
        println("Updated object: $obj")
    }
}

// Работа с аннотациями
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class MyAnnotation(val value: String)

@MyAnnotation("User class")
data class User(
    @MyAnnotation("User name")
    val name: String,

    @MyAnnotation("User age")
    val age: Int
)

fun inspectAnnotations() {
    val kClass = User::class

    // Аннотации класса
    val classAnnotations = kClass.annotations
    println("Class annotations:")
    classAnnotations.forEach { annotation ->
        if (annotation is MyAnnotation) {
            println("  @MyAnnotation(${annotation.value})")
        }
    }

    // Аннотации свойств
    kClass.members
        .filterIsInstance<KProperty1<*, *>>()
        .forEach { property ->
            val propAnnotations = property.annotations
            if (propAnnotations.isNotEmpty()) {
                println("Property ${property.name} annotations:")
                propAnnotations.forEach { annotation ->
                    if (annotation is MyAnnotation) {
                        println("  @MyAnnotation(${annotation.value})")
                    }
                }
            }
        }
}

// Generic рефлексия
inline fun <reified T> genericReflection() {
    val kClass = T::class

    println("Type: ${kClass.simpleName}")
    println("Is nullable: ${null is T}") // Это не сработает как ожидается

    // Лучший способ проверить nullable
    val type = typeOf<T>()
    println("Type representation: $type")
}

// Демонстрация всех возможностей
fun main() {
    demonstrateBasicReflection()

    val person = Person("John", 30)
    println("\n--- Object Inspection ---")
    inspectObject(person)

    println("\n--- Dynamic Instance Creation ---")
    createInstanceDynamically()

    println("\n--- Dynamic Method Invocation ---")
    invokeMethodsDynamically()

    println("\n--- Annotation Inspection ---")
    inspectAnnotations()

    println("\n--- Generic Reflection ---")
    genericReflection<List<String>>()
}

data class Person(var name: String, var age: Int) {
    fun introduce() = "Hi, I'm $name, $age years old"
}
```

Этот расширенный файл по **Kotlin Basics** теперь содержит более `2000` строк с подробными объяснениями всех основных концепций **Kotlin**, включая последние версии и продвинутые возможности языка.

## Дополнительные ссылки по разделам (legacy)

Навигатор ниже сохранён для обратной совместимости со старыми якорями. Для первичного чтения используйте основное оглавление в начале файла.

[**Kotlin Basics** — Полное руководство](#kotlin-basics-полное-руководство)
[Введение в **Kotlin**](#введение-в-kotlin)
  [Основные особенности **Kotlin**](#основные-особенности-kotlin)
  [История версий **Kotlin**](#история-версий-kotlin)
    [**Kotlin** 1.0 (2016)](#kotlin-10-2016)
    [**Kotlin** 1.1 (2016)](#kotlin-11-2016)
    [**Kotlin** 1.2 (2017)](#kotlin-12-2017)
    [**Kotlin** 1.3 (2018)](#kotlin-13-2018)
    [**Kotlin** 1.4 (2020)](#kotlin-14-2020)
    [**Kotlin** 1.5 (2021)](#kotlin-15-2021)
    [**Kotlin** 1.6 (2021)](#kotlin-16-2021)
    [**Kotlin** 1.7 (2022)](#kotlin-17-2022)
    [**Kotlin** 1.8 (2022)](#kotlin-18-2022)
    [**Kotlin** 1.9 (2023)](#kotlin-19-2023)
[Основы синтаксиса](#основы-синтаксиса)
  [Переменные и типы данных](#переменные-и-типы-данных)
  [**Null Safety**](#null-safety)
  [Функции](#функции)
  [Классы и объекты](#классы-и-объекты)
  [Наследование и интерфейсы](#наследование-и-интерфейсы)
  [**Generics**](#generics)
  [Коллекции](#коллекции)
  [Исключения](#исключения)
  [Расширения (Extensions)](#расширения-extensions)
  [**Data Classes** и **Sealed Classes**](#data-classes-и-sealed-classes)
  [**Inline Classes** и **Value Classes**](#inline-classes-и-value-classes)
  [Корутины (Coroutines)](#корутины-coroutines)
  [**DSL** (Domain Specific Language)](#dsl-domain-specific-language)
  [Инлайновые функции (Inline Functions)](#инлайновые-функции-inline-functions)
  [Оператор перегрузка (Operator Overloading)](#оператор-перегрузка-operator-overloading)
  [**Reflection API**](#reflection-api)
[Ссылки по расширенным темам](#ссылки-по-расширенным-темам)
[Функции работы с коллекциями](#функции-работы-с-коллекциями)
  [Фильтрация](#фильтрация)
  [Проверка элементов](#проверка-элементов)
  [Трансформация](#трансформация)
  [Группировка](#группировка)
  [Сортировка](#сортировка)
  [Агрегатные операции](#агрегатные-операции)
[**Inline** функции (встроенные)](#inline-функции-встроенные)
  [Проблема с производительностью лямбд](#проблема-с-производительностью-лямбд)
  [Использование **inline** функций](#использование-inline-функций)
  [**noinline**](#noinline)
  [Возврат из **inline** функций](#возврат-из-inline-функций)
[**Inline** классы (встроенные)](#inline-классы-встроенные)
  [Определение **inline** класса](#определение-inline-класса)
  [Свойства и функции в **inline** классах](#свойства-и-функции-в-inline-классах)
[**Reified** функции (овеществлённые)](#reified-функции-овеществлённые)
[Руководство по **Type Alias** (псевдонимы)](#руководство-по-type-alias-псевдонимы)
[Руководство по **Delegate**](#руководство-по-delegate)
  [Стандартные делегаты](#стандартные-делегаты)
  [**Lazy** делегат](#lazy-делегат)
  [**Observable** делегат](#observable-делегат)
[Руководство по **Null Safety**](#руководство-по-null-safety)
  [**Nullable** типы](#nullable-типы)
  [Безопасные вызовы](#безопасные-вызовы)
  [Оператор **Elvis**](#оператор-elvis)
  [Оператор !!](#оператор)
[Несколько переменных в функции **let**](#несколько-переменных-в-функции-let)
  [Простой пример **let**](#простой-пример-let)
  [Обработка нескольких переменных](#обработка-нескольких-переменных)
[Обработка исключений](#обработка-исключений)
  [**Try-catch** блок](#try-catch-блок)
  [**Try-catch** как выражение](#try-catch-как-выражение)
  [Несколько блоков **catch**](#несколько-блоков-catch)
  [Вложенные **try-catch**](#вложенные-try-catch)
[Хвостовая рекурсия](#хвостовая-рекурсия)
[Чтение из файла](#чтение-из-файла)
[Запись в файл](#запись-в-файл)
[Работа с **JPA**](#работа-с-jpa)
  [Проблемы с **Data** классами](#проблемы-с-data-классами)
  [Решение](#решение)
[Руководство по @**JvmField**](#руководство-по-jvmfield)
  [Использование с **companion object**](#использование-с-companion-object)
  [Ограничения](#ограничения)
[Плагин **Allopen**](#плагин-allopen)
  [Настройка плагина](#настройка-плагина)
[Работа со **Spring Boot**](#работа-со-spring-boot)
[Регулярные выражения](#регулярные-выражения)
[Логгирование](#логгирование)
  [Объект-компаньон](#объект-компаньон)
  [Метод расширения](#метод-расширения)
  [Реified **generic**](#реified-generic)
  [Делегированное свойство](#делегированное-свойство)
[Рефлексия](#рефлексия)
[Аннотации](#аннотации)
  [Встроенные аннотации](#встроенные-аннотации)
[Руководство по @**Deprecated**](#руководство-по-deprecated)
[**Kotlin** быстрее, чем **Java**?](#kotlin-быстрее-чем-java)
[Преобразование массива в **varargs**](#преобразование-массива-в-varargs)
[Расширение **Data** класса](#расширение-data-класса)
[Сравнение объектов данных](#сравнение-объектов-данных)
[Принципы **SOLID**](#принципы-solid)
[**Type System** (Система типов)](#type-system-система-типов)
  [Базовые типы](#базовые-типы)
  [**Any** — корневой тип](#any-корневой-тип)
  [**Unit** — тип возвращаемого значения](#unit-тип-возвращаемого-значения)
  [**Nothing** — тип без значений](#nothing-тип-без-значений)
  [**Smart Casts** (Умные приведения)](#smart-casts-умные-приведения)
[**Type Aliases** (Псевдонимы типов)](#type-aliases-псевдонимы-типов)
  [Базовое использование](#базовое-использование)
  [Псевдонимы для сложных типов](#псевдонимы-для-сложных-типов)
  [Практические примеры](#практические-примеры)
[**Object Expressions** и **Object Declarations**](#object-expressions-и-object-declarations)
  [**Object Expressions** (Анонимные объекты)](#object-expressions-анонимные-объекты)
  [**Object Declarations** (Singleton)](#object-declarations-singleton)
  [**Companion Objects**](#companion-objects)
[**Enum Classes**](#enum-classes)
  [**Enum** с параметрами](#enum-с-параметрами)
  [**Enum** с интерфейсами](#enum-с-интерфейсами)
  [Работа с **enum**](#работа-с-enum)
[**Delegation** (Делегирование)](#delegation-делегирование)
  [**Class Delegation** (Делегирование класса)](#class-delegation-делегирование-класса)
  [**Property Delegation** (Делегирование свойств)](#property-delegation-делегирование-свойств)
    [**Lazy**](#lazy)
    [**Observable**](#observable)
    [**Map Delegation**](#map-delegation)
[**Infix Functions** (Инфиксные функции)](#infix-functions-инфиксные-функции)
  [Определение **infix** функций](#определение-infix-функций)
  [Требования к **infix** функциям](#требования-к-infix-функциям)
  [Стандартные **infix** функции](#стандартные-infix-функции)
[**Destructuring Declarations** (Деструктуризация)](#destructuring-declarations-деструктуризация)
  [Деструктуризация **data** классов](#деструктуризация-data-классов)
  [Деструктуризация пар и триплетов](#деструктуризация-пар-и-триплетов)
  [Деструктуризация **Map**](#деструктуризация-map)
  [Деструктуризация в лямбдах](#деструктуризация-в-лямбдах)
  [Кастомная деструктуризация](#кастомная-деструктуризация)
[**Ranges** и **Progressions** (Диапазоны и прогрессии)](#ranges-и-progressions-диапазоны-и-прогрессии)
  [Создание диапазонов](#создание-диапазонов)
  [Итерация по диапазонам](#итерация-по-диапазонам)
  [Операции с диапазонами](#операции-с-диапазонами)
  [**Progressions** (Прогрессии)](#progressions-прогрессии)
[**String Templates** (Шаблоны строк)](#string-templates-шаблоны-строк)
  [**Raw Strings** (Сырые строки)](#raw-strings-сырые-строки)
  [**String Templates** с выражениями](#string-templates-с-выражениями)
[**When Expression** (Выражение when)](#when-expression-выражение-when)
  [Множественные значения](#множественные-значения)
  [**When** с диапазонами](#when-с-диапазонами)
  [**When** с типами (Smart Cast)](#when-с-типами-smart-cast)
  [**When** без аргумента](#when-без-аргумента)
  [**When** с функциями](#when-с-функциями)
  [**Exhaustive when** (Исчерпывающий when)](#exhaustive-when-исчерпывающий-when)
[**Return** и **Labels** (Возврат и метки)](#return-и-labels-возврат-и-метки)
  [Обычный **return**](#обычный-return)
  [**Labeled Return** (Возврат с меткой)](#labeled-return-возврат-с-меткой)
  [**Return** из лямбд](#return-из-лямбд)
  [**Break** и **Continue** с метками](#break-и-continue-с-метками)
[**Visibility Modifiers** (Модификаторы видимости)](#visibility-modifiers-модификаторы-видимости)
  [Модификаторы для членов класса](#модификаторы-для-членов-класса)
  [Модификаторы для классов](#модификаторы-для-классов)
  [Модификаторы для функций](#модификаторы-для-функций)
  [Модификаторы для конструкторов](#модификаторы-для-конструкторов)
  [Сравнение с **Java**](#сравнение-с-java)
  [**Top-level** элементы](#top-level-элементы)
[Расширенное покрытие **Generics**](#расширенное-покрытие-generics)
  [**Variance** (Вариантность)](#variance-вариантность)
    [**Invariance** (Инвариантность)](#invariance-инвариантность)
    [**Covariance** (Ковариантность) — **out**](#covariance-ковариантность-out)
    [**Contravariance** (Контравариантность) — in](#contravariance-контравариантность-in)
  [**Star Projections** (Звездные проекции)](#star-projections-звездные-проекции)
  [**Upper Bounds** (Верхние границы)](#upper-bounds-верхние-границы)
  [**Reified Type Parameters** (Овеществленные параметры типов)](#reified-type-parameters-овеществленные-параметры-типов)
  [**Generic Functions** (Generic функции)](#generic-functions-generic-функции)
  [**Type Erasure** (Стирание типов)](#type-erasure-стирание-типов)
[Расширенное покрытие **Reflection**](#расширенное-покрытие-reflection)
  [**KClass** — информация о классе](#kclass-информация-о-классе)
  [**KFunction** — информация о функциях](#kfunction-информация-о-функциях)
  [**KProperty** — информация о свойствах](#kproperty-информация-о-свойствах)
  [**KType** — информация о типах](#ktype-информация-о-типах)
  [Аннотации через **Reflection**](#аннотации-через-reflection)
  [Вызов функций через **Reflection**](#вызов-функций-через-reflection)
  [Работа с конструкторами](#работа-с-конструкторами)
[Расширенное покрытие **Annotations**](#расширенное-покрытие-annotations)
  [Создание аннотаций](#создание-аннотаций)
  [Мета-аннотации](#мета-аннотации)
  [Использование аннотаций](#использование-аннотаций)
  [Встроенные аннотации **Kotlin**](#встроенные-аннотации-kotlin)
  [Аннотации для **Java Interop**](#аннотации-для-java-interop)
[**Multiplatform Projects** (Мультиплатформенные проекты)](#multiplatform-projects-мультиплатформенные-проекты)
  [Общие концепции](#общие-концепции)
  [**Expect**/**Actual** механизм](#expectactual-механизм)
  [Структура проекта](#структура-проекта)
  [Общие типы](#общие-типы)
  [Платформенно-специфичные **API**](#платформенно-специфичные-api)
[Расширенное покрытие **Data Classes**](#расширенное-покрытие-data-classes)
  [Автоматически генерируемые методы](#автоматически-генерируемые-методы)
  [Ограничения **Data Classes**](#ограничения-data-classes)
  [Наследование **Data Classes**](#наследование-data-classes)
  [**Custom** методы в **Data Classes**](#custom-методы-в-data-classes)
[Расширенное покрытие **Sealed Classes**](#расширенное-покрытие-sealed-classes)
  [**Sealed Classes** vs **Enum Classes**](#sealed-classes-vs-enum-classes)
  [**Sealed Classes** с данными](#sealed-classes-с-данными)
  [**Sealed Interfaces** (Kotlin 1.5+)](#sealed-interfaces-kotlin-15)
  [**Exhaustive When**](#exhaustive-when)
[Продвинутые техники работы с типами](#продвинутые-техники-работы-с-типами)
  [**Type-safe builders** с обобщенными типами](#type-safe-builders-с-обобщенными-типами)
  [Работа с типами во время выполнения](#работа-с-типами-во-время-выполнения)
[Оптимизация производительности](#оптимизация-производительности)
  [**Inline** функции для производительности](#inline-функции-для-производительности)
  [Оптимизация коллекций](#оптимизация-коллекций)
[Работа с аннотациями](#работа-с-аннотациями)
  [Создание пользовательских аннотаций](#создание-пользовательских-аннотаций)
  [Аннотации для обработки ошибок](#аннотации-для-обработки-ошибок)
[Продвинутые паттерны проектирования](#продвинутые-паттерны-проектирования)
  [**Strategy Pattern** с функциональным подходом](#strategy-pattern-с-функциональным-подходом)
  [**Observer Pattern** с **Delegated Properties**](#observer-pattern-с-delegated-properties)
[Продвинутые техники работы с классами](#продвинутые-техники-работы-с-классами)
  [**Nested** классы и **Inner** классы](#nested-классы-и-inner-классы)
  [**Anonymous** классы и **Object expressions**](#anonymous-классы-и-object-expressions)
[Работа с типами](#работа-с-типами)
  [**Type inference** и **explicit** типы](#type-inference-и-explicit-типы)
  [**Type casting** и проверки типов](#type-casting-и-проверки-типов)
[Работа с исключениями](#работа-с-исключениями)
  [Продвинутая обработка исключений](#продвинутая-обработка-исключений)
  [**Functional** подход к обработке ошибок](#functional-подход-к-обработке-ошибок)
[Работа с корутинами в базовых концепциях](#работа-с-корутинами-в-базовых-концепциях)
  [Асинхронное программирование](#асинхронное-программирование)
  [**Flow** для обработки потоков данных](#flow-для-обработки-потоков-данных)
[Работа с рефлексией](#работа-с-рефлексией)
  [Продвинутая рефлексия](#продвинутая-рефлексия)
  [**Extension** функции для классов](#extension-функции-для-классов)
  [**Extension** свойства](#extension-свойства)
[Работа с типами и **generics**](#работа-с-типами-и-generics)
  [Продвинутые **generics**](#продвинутые-generics)
  [**Type constraints** и **bounds**](#type-constraints-и-bounds)
[Дополнительные возможности **Kotlin**](#дополнительные-возможности-kotlin)
  [Работа с делегатами](#работа-с-делегатами)
  [Работа с контрактами](#работа-с-контрактами)
  [**Inline** классы (Value Classes)](#inline-классы-value-classes)
[Дополнительные возможности языка](#дополнительные-возможности-языка)
  [Работа с контекстами](#работа-с-контекстами)
  [Работа с операторами перегрузки](#работа-с-операторами-перегрузки)
  [Работа с инвариантами](#работа-с-инвариантами)
[Дополнительные возможности](#дополнительные-возможности)
  [Работа с контрактами и инвариантами](#работа-с-контрактами-и-инвариантами)
  [Работа с типами и **type inference**](#работа-с-типами-и-type-inference)
[Решение проблем](#решение-проблем)
[Частые вопросы](#частые-вопросы)
[Заключение](#заключение)
[Дополнительные ресурсы](#дополнительные-ресурсы)
[Итоговые рекомендации](#итоговые-рекомендации)
[Практические примеры использования](#практические-примеры-использования)
  [Создание **data class** для модели данных](#создание-data-class-для-модели-данных)
  [Использование **sealed class** для состояний](#использование-sealed-class-для-состояний)
  [Использование **extension functions** для расширения функциональности](#использование-extension-functions-для-расширения-функциональности)
  [Использование **inline classes** для типобезопасности](#использование-inline-classes-для-типобезопасности)
  [Использование делегатов для ленивой инициализации](#использование-делегатов-для-ленивой-инициализации)
  [Использование контрактов для оптимизации](#использование-контрактов-для-оптимизации)
[Лучшие практики](#лучшие-практики)

## Ссылки по расширенным темам

- [Kotlin Documentation](https://kotlinlang.org/docs/)

### См. также (библиотеки)
- [[kotlin-kotlinx-serialization|kotlinx.serialization]] — сериализация **JSON**, **Protobuf**, **CBOR**
- [[kotlin-exposed|Exposed]] — **ORM** для **Kotlin**
- [[kotlin-ktor|Ktor]] — веб-фреймворк

## Функции работы с коллекциями

**Kotlin** предоставляет множество полезных функций для работы с коллекциями. Вот основные категории:**

### Фильтрация

1.  **filter()** — принимает условие-предикат в виде функции `(T) -> Boolean`
2.  **filterIndexed()** — также получает индекс текущего элемента
3.  **filterIsInstance()** — можно извлечь элементы определенного типа
4.  **filterNotNull()** — позволяет выфильтровать все значения, которые равны `null`

### Проверка элементов

1.  **all {}** — проверяет, все ли элементы коллекции/последовательности соответствуют условию предиката
2.  **any {}** — проверяет, соответствует хотя бы один элемент коллекции/последовательности условию предиката
3.  **none {}** — возвращает `true`, если ни один из элементов НЕ соответствует условию предиката
4.  **contains()** — возвращает `true`, если в коллекции/последовательности есть определенный элемент
5.  **containsAll()** — возвращает `true`, если коллекция содержит все элементы другой коллекции

### Трансформация

1.  **map()** — для трансформации одной коллекции/последовательности
2.  **mapIndexed()** — также передает в функцию преобразования индекс текущего элемента
3.  **mapNotNull()** и **mapIndexedNotNull()** — если необходимо отсеять значения `null`, которые могут возникать при преобразовании
4.  **flatten()** — позволяет преобразовать коллекцию/последовательность, которая содержит вложенные коллекции/последовательности

### Группировка

1.  **groupBy()** — для группировки элементов коллекции/последовательности применяется функция

### Сортировка

1.  **sortedWith()** — принимает компаратор и на его основе сортирует коллекцию/последовательность
2.  **sortedBy()** — сортирует по возрастанию, а **sortedByDescending()** — по убыванию
3.  **reversed()** — изменяет порядок элементов на обратный
4.  **shuffle()** — перемешивает элементы случайным образом

### Агрегатные операции

1.  **minOrNull()** и **maxOrNull()** — возвращают соответственно минимальное и максимальное значение (если коллекция/последовательность пуста, то возвращается `null`)
2.  **minByOrNull()** и **maxByOrNull()** — принимают функцию селектора, которая позволяет определить критерий сравнения объектов
3.  **minWithOrNull()** и **maxWithOrNull()** — принимают компаратор. В качестве критерия сравнения здесь применяется свойство `length` строк, то есть строки сравниваются по длине
4.  **minOfWithOrNull()** и **maxOfWithOrNull()** — принимают реализацию интерфейса **Comparator** (первый параметр) и селектор критерия для сравнения (второй параметр)
5.  Для получения среднего значения применяется функция **average()**
6.  Для получения суммы числовых значений применяется функция **sum()**
7.  Для получения количества элементов в коллекции/последовательности применяется функция **count()**
8.  **reduce()** — сводит все значения потока к одному значению
9.  **fold()** — также сводит все элементы потока в один. Но в отличие от **reduce** в качестве первого параметра принимает начальное значение

## Inline функции (встроенные)

В **Kotlin** функции являются гражданами первого класса поэтому мы можем передавать функции или возвращать их, как и другие обычные типы. Однако представление этих функций во время выполнения иногда может вызвать некоторые ограничения или проблемы с производительностью.

### Проблема с производительностью лямбд

**Когда мы передаем лямбда-выражение в функцию, под капотом происходит следующее:**

- По крайней мере, один экземпляр специального типа создается и хранится в куче.
- Дополнительный вызов метода всегда будет происходить

Каждый раз, когда мы объявляем функцию более высокого порядка будет создан как минимум один экземпляр этих специальных типов **Function\**

**Kotlin** стремится к совместимости с **Java 6**, а **invokedynamic** недоступен до **Java 7**, поэтому создаются экземпляры функциональных типов.

### Использование inline функций

**Чтобы избежать накладных расходов, мы можем пометить функцию ключевым словом **inline**:**

```kotlin
inline fun <T> Collection<T>.each(block: (T) -> Unit) {
    for (e in this) block(e)
}
```

При использовании встроенных функций компилятор встраивает тело функции. То есть подставляет тело прямо в места, где вызывается функция. По умолчанию компилятор встраивает код как самой функции, так и переданных ей лямбда-выражений.

**Например, компилятор переводит:**

```kotlin
// Вызов extension-функции each для перебора коллекции
val numbers = listOf(1, 2, 3, 4, 5)
numbers.each { println(it) }
```

В:

```kotlin
// Итерация по списку через for-in
val numbers = listOf(1, 2, 3, 4, 5)
for (number in numbers)
    println(number)
```

При использовании встроенных функций нет дополнительного выделения объектов и дополнительных вызовов виртуальных методов.

### noinline

**По умолчанию все лямбда-выражения, переданные встроенной функции, также будут встроенными. Однако мы можем пометить некоторые лямбды ключевым словом **noinline** чтобы исключить их из встраивания:**

```kotlin
// noinline — лямбда не встраивается (например, для передачи в другое место)
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {.. }
```

### Возврат из inline функций

В **Kotlin** мы можем использовать выражение **return** (также известное как unqualified return) только для выхода из именованной или анонимной функции.

Однако мы не можем использовать неквалифицированные выражения возврата для выхода из лямбда-выражения. Встроенные функции являются исключением — возврат из встроенной лямбды эквивалентен возврату из объёмлющей функции.

Как правило, мы можем встраивать функции с лямбда-параметрами только в том случае, если лямбда-выражение либо вызывается напрямую, либо передается другой встроенной функции. В противном случае компилятор предотвращает встраивание с ошибкой компилятора.

Например, давайте посмотрим на функцию замены в стандартной библиотеке **Kotlin:**

```kotlin
// noinline для параметра transform — вызывается не из inline-контекста
inline fun CharSequence.replace(regex: Regex, noinline transform: (MatchResult) -> CharSequence): String =
    regex.replace(this, transform)
```

Фрагмент кода выше передаёт лямбда **transform** в обычную функцию **replace** — отсюда и **noinline**

## Inline классы (встроенные)

В **Kotlin 1.3+** у нас есть экспериментальный новый тип класса, называемый встроенным классом. В этом руководстве мы сосредоточимся на использовании встроенных классов, а также на некоторых их ограничениях.

Встроенные классы предоставляют нам способ обернуть тип, таким образом добавляя функциональность и создавая новый тип сам по себе.

В отличие от обычных (не встроенных)** оболочек, они выиграют от повышения производительности. Это происходит из-за того, что данные встроены в их использование, а создание объектов пропускается в результирующем скомпилированном коде.

### Определение inline класса

Одно свойство, инициализированное в основном конструкторе, является основным требованием встроенного класса Единственное свойство будет представлять экземпляр класса во время выполнения.

```kotlin
// Inline-класс-обёртка над Double (без аллокации в runtime)
inline class InlineDoubleWrapper(val doubleValue: Double)
```

**Давайте рассмотрим пример встроенного класса **InlinedCircleRadius** со свойством типа **Double** представляющим радиус:**

```kotlin
// Inline-класс: в байткоде будет только Double
val circleRadius = InlinedCircleRadius(5.5)
```

**Для **JVM** наш код на самом деле просто:**

```kotlin
// В байткоде JVM — только примитив double
val circleRadius = 5.5
```

Обратите внимание, что экземпляр **InlinedCircleRadius** не создается в скомпилированном коде, потому что базовое значение встроено, что избавляет нас от снижения производительности, связанного с созданием экземпляра.

### Свойства и функции в inline классах

**Они также позволяют нам определять свойства и функции так же, как обычные классы. В следующем примере определяется свойство, представляющее диаметр, и функция, возвращающая площадь круга:**

```kotlin
// Inline-класс с вычисляемыми свойствами и методом
inline class CircleRadius(private val circleRadius: Double) {
    val diameterOfCircle get() = 2 * circleRadius
    fun areaOfCircle() = 3.14 * circleRadius * circleRadius
}
```

## Reified функции (овеществлённые)

Как мы видели ранее, **Kotlin** стирает информацию об универсальном типе во время выполнения, но для встроенных функций мы можем обойти это ограничение. То есть компилятор может материализовать информацию об универсальном типе для встроенных функций.

**Все, что нам нужно сделать, это пометить параметр **type** ключевым словом **reified**:**

```kotlin
// reified сохраняет тип T в runtime для проверки is T
inline fun <reified T> Any.isA(): Boolean = this is T
```

Без **inline** и **reified** функция **isA** не будет компилироваться, так как информация о типе стирается во время выполнения.

## Руководство по Type Alias (псевдонимы)

**Type Alias** позволяет создать псевдоним для существующего типа. Это полезно для улучшения читаемости кода и работы со сложными типами:**

```kotlin
// Псевдонимы типов для читаемости и краткости
typealias UserId = String
typealias UserMap = Map<UserId, User>
```

Теперь мы можем использовать **UserId** вместо **String** для большей ясности.

## Руководство по Delegate

Делегирование — это мощный паттерн в **Kotlin**, который позволяет передавать реализацию определённых методов другому объекту.

### Стандартные делегаты

**Kotlin** предоставляет несколько встроенных делегатов:**

1. **lazy()** — вычисляет значение только при первом обращении
2. **observable()** — уведомляет об изменениях
3. **vetoable()** — позволяет отклонить изменение значения
4. **notNull()** — гарантирует, что значение будет установлено перед использованием

### Lazy делегат

```kotlin
// Ленивая инициализация: значение вычисляется при первом обращении
val lazyValue: String by lazy {
    println("computed!")
    "Hello"
}
```

Значение будет вычислено только при первом обращении, и результат будет кэширован.

### Observable делегат

```kotlin
// Делегат observable: колбэк при каждом изменении свойства
var name: String by Delegates.observable("Initial") { prop, old, new ->
    println("$old -> $new")
}
```

При изменении значения будет вызван указанный **callback**.

## Руководство по Null Safety

Одной из ключевых особенностей **Kotlin** является безопасность работы с **null** значениями. Система типов различает ссылки, которые могут содержать **null** (nullable), и те, которые не могут (non-nullable).

### Nullable типы

**Типы, допускающие значение **null**, помечаются знаком ?:**

```kotlin
var name: String? = null  // nullable
var age: Int = 25         // non-nullable
```

### Безопасные вызовы

**Оператор **?.** выполняет вызов только если объект не равен **null**:**

```kotlin
val length = name?.length  // вернет null, если name == null
```

### Оператор Elvis

Оператор **?:** предоставляет значение по умолчанию, если выражение слева равно **null**:

```kotlin
// Elvis-оператор: значение по умолчанию при null
val length = name?.length ?: 0
```

### Оператор !!

**Оператор **!!** принудительно преобразует **nullable** значение в **non-nullable**, выбрасывая исключение, если значение равно **null**:**

```kotlin
val length = name!!.length  // выбросит NPE, если name == null
```

## Несколько переменных в функции let

В **Kotlin** функция **let()** — довольно удобная функция области видимости. Это позволяет нам преобразовать данную переменную в значение другого типа.

### Простой пример let

```kotlin
// Безопасный вызов let для nullable: выполняется только при non-null
val str: String? = "hello"
val lengthReport = str?.let {
    "The length of the string [$it] is: ${it.length}"
}
println(lengthReport)
// будет выведено: The length of the string [hello] is: 5
```

### Обработка нескольких переменных

Иногда мы хотели бы применить нулевую безопасную операцию **let** более чем к одной переменной. Но стандартная функция **let()** может обрабатывать только одну переменную.

**Самый простой способ заставить **null-safe let()** обрабатывать две переменные, допускающие значение **null**, — это написать два вызова **let()**:**

```kotlin
val theName: String? = "Kai"
val theNumber: Int? = 7
val result = theName?.let { name ->
    theNumber?.let { num ->
        "Hi $name, $num squared is ${num * num}"
    }
}
assertThat(result).isEqualTo("Hi Kai, 7 squared is 49")
```

**Однако вложенную структуру нелегко прочитать. Мы можем создать вспомогательную функцию:**

```kotlin
inline fun <T1: Any, T2: Any, R: Any> let2(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}
```

**Теперь мы можем использовать:**

```kotlin
let2("Kai", 7) { name, num ->
    "Hi $name, $num squared is ${num * num}"
}
```

**Аналогично можно создать **let3**, **let4** и т.д., или использовать **vararg** для произвольного количества переменных:**

```kotlin
inline fun <T: Any, R: Any> letIfAllNotNull(vararg arguments: T?, block: (List<T>) -> R): R? {
    return if (arguments.all { it != null }) {
        block(arguments.filterNotNull())
    } else null
}
```

## Обработка исключений

**Исключения** — это проблемы, которые возникают во время выполнения программы и нарушают обычный поток. Это может происходить по разным причинам, таким как недопустимая арифметическая операция, ссылка на нулевой объект.

**Обработка исключений** — это метод изящной обработки таких проблем и продолжения выполнения программы.

В **Kotlin** есть только непроверенные исключения, которые генерируются во время выполнения программы. Все классы исключений происходят от класса **Throwable**. **Kotlin** использует ключевое слово **throw** для создания объекта исключения.

Хотя **Kotlin** наследует концепцию исключений от **Java**, он не поддерживает проверенные исключения, такие как в **Java**.

### Try-catch блок

Мы можем использовать блок **try-catch** для обработки исключений в **Kotlin**. В частности, внутри блока **try** помещается код, который может генерировать исключение. Кроме того, для обработки исключения используется соответствующий блок **catch**.

За блоком **try** всегда следует блок **catch** или **finally**, или оба сразу.

```kotlin
try {
    val message = "Welcome to Kotlin Tutorials"
    message.toInt()
} catch (exception: NumberFormatException) {
    // обработка исключения
}
```

### Try-catch как выражение

Выражение может быть комбинацией одного или нескольких значений, переменных, операторов и функций, которые выполняются для получения другого значения. Следовательно, мы можем использовать блок **try-catch** как выражение в **Kotlin.**

Возвращаемое значение выражения **try-catch** является последним выражением блока **try** или **catch**. В случае исключения возвращается значение блока **catch**. Однако блок **finally** не влияет на результат выражения.

```kotlin
val number = try {
    val message = "Welcome to Kotlin Tutorials"
    message.toInt()
} catch (exception: NumberFormatException) {
    0  // значение по умолчанию
}
```

### Несколько блоков catch

Мы можем использовать несколько блоков **catch** вместе с блоком **try** в **Kotlin**. В частности, это часто требуется, если мы выполняем разного рода операции в блоке **try**, что увеличивает вероятность отлова множественных исключений.

Кроме того, мы должны упорядочить все блоки **catch** от самых конкретных до самых общих исключений.

```kotlin
try {
    val result = 25 / 0
    result
} catch (exception: NumberFormatException) {
    // обработка NumberFormatException
} catch (exception: ArithmeticException) {
    // обработка ArithmeticException
} catch (exception: Exception) {
    // обработка любых других исключений
}
```

### Вложенные try-catch

Мы можем использовать вложенный блок **try-catch**, реализуя блок **try-catch** внутри другого блока **try**. Например, это может потребоваться, когда блок кода может генерировать исключение, а в этом блоке кода другой оператор может дополнительно генерировать исключение.

## Хвостовая рекурсия

**Kotlin** поддерживает оптимизацию хвостовой рекурсии, что позволяет избежать переполнения стека при рекурсивных вызовах.

**Чтобы функция была хвостовой рекурсией, она должна:**
1. Вызывать саму себя в качестве последней операции
2. Быть помечена ключевым словом **tailrec**

```kotlin
// tailrec — хвостовая рекурсия компилируется в цикл
tailrec fun factorial(n: Int, acc: Int = 1): Int {
    return if (n <= 1) acc
    else factorial(n - 1, n * acc)
}
```

Компилятор оптимизирует хвостовую рекурсию, преобразуя её в итерацию, что предотвращает переполнение стека.

## Чтение из файла

**Kotlin** предоставляет удобные способы чтения файлов:**

```kotlin
// Чтение всего файла как строки
val content = File("file.txt").readText()

// Чтение по строкам
val lines = File("file.txt").readLines()

// Чтение в байтах
val bytes = File("file.txt").readBytes()
```

**Используя **use**, мы можем автоматически закрыть файл:**

```kotlin
// Построчное чтение файла с автоматическим закрытием ресурса
File("file.txt").useLines { lines ->
    lines.forEach { println(it) }
}
```

## Запись в файл

**Аналогично, для записи в файл:**

```kotlin
// Запись строки
File("file.txt").writeText("Hello, World!")

// Добавление в файл
File("file.txt").appendText("\nNew line")

// Запись байтов
File("file.txt").writeBytes(byteArrayOf(1, 2, 3))
```

## Работа с JPA

**При работе с **JPA** в **Kotlin** нужно учитывать некоторые особенности:**

### Проблемы с Data классами

Классы данных в **Kotlin** автоматически генерируют методы `equals()`, **hashCode()** и **toString()** на основе всех свойств. Это может вызвать проблемы с ленивыми ассоциациями в **JPA**.

### Решение

1. Не использовать **data** классы для **JPA** сущностей (или использовать только для простых случаев)
2. Реализовать `equals()` и **hashCode()** вручную на основе `ID`
3. Использовать **@Entity** аннотацию и явно определять методы

```kotlin
@Entity
class Address(
    @Id
    @GeneratedValue
    var id: Long? = null,

    var name: String,

    @OneToMany
    var phones: List<PhoneNumber> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Address) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
```

## Руководство по @JvmField

**@JvmField** аннотация позволяет достичь совместимости между **Kotlin** и **Java**.

**По умолчанию классы **Kotlin** открывают не поля, а свойства. Язык автоматически предоставляет резервные поля для свойств, которые будут хранить свое значение в форме поля:**

```kotlin
class CompanionSample {
    var quantity = 0
    set(value) {
        if (value >= 0) field = value
    }
}
```

**@JvmField** инструктирует компилятор не создавать геттеры и сеттеры для свойства и предоставлять его как простое поле **Java**:**

```kotlin
class KotlinJvmSample {
    @JvmField
    val example = "Hello!"
}
```

**Это будет выглядеть в **Java** как:**

```java
public class KotlinJvmSample {
    @NotNull
    public final String example = "Hello!";
}
```

### Использование с companion object

**Другой случай, когда аннотация пригодится, — это всякий раз, когда свойство, объявленное в объекте имени или сопутствующем объекте, имеет статическое поле поддержки:**

```kotlin
class Sample {
    companion object {
        @JvmField val MAX_LIMIT = 20
    }
}
```

### Ограничения

**Вот некоторые ситуации, когда мы не можем использовать аннотацию:**

1. Частная собственность
2. Свойства с модификаторами **open, override, const**
3. Делегированные свойства

## Плагин Allopen

В **Kotlin** все классы по умолчанию являются **final**, что, помимо очевидных преимуществ, может быть проблематичным в приложениях **Spring**. Проще говоря, некоторые области **Spring** работают только с незавершёнными классами.

**Естественное решение** — вручную открывать классы **Kotlin** с помощью ключевого слова **open** или использовать плагин **kotlin-allopen**, который автоматически открывает все классы, необходимые для работы **Spring**.

### Настройка плагина

**Добавьте зависимость:**

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-allopen</artifactId>
    <version>1.1.4-3</version>
</dependency>
```

**Настройте плагин в разделе сборки:**

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <configuration>
        <compilerPlugins>
            <plugin>spring</plugin>
        </compilerPlugins>
    </configuration>
</plugin>
```

Теперь все классы, помеченные аннотациями **Spring** (например, `@Configuration`, `@Service`, `@Repositor`y), будут автоматически открытыми.

## Работа со Spring Boot

**Kotlin** отлично работает со **Spring Boot**. Вот некоторые ключевые моменты:**

1. Используйте плагин **kotlin-allopen** для автоматического открытия классов
2. **Data** классы можно использовать для **DTO**, но не для сущностей **JPA**
3. Используйте конструкторы по умолчанию для **Spring** компонентов
4. **Kotlin null safety** интегрируется с **Spring nullable** проверками

```kotlin
@Configuration
class AppConfig {
    @Bean
    fun myBean(): MyService {
        return MyService()
    }
}

@Service
class UserService {
    fun findUser(id: Long): User? {
        // ...
    }
}
```

## Регулярные выражения

**Kotlin** предоставляет удобные методы для работы с регулярными выражениями:**

```kotlin
val regex = Regex("pattern")
val result = "text".matches(regex)

// Или используя расширенные функции
val result2 = "text".contains(Regex("pattern"))

// Поиск всех совпадений
val matches = regex.findAll("text")

// Замена
val replaced = "text".replace(regex, "replacement")
```

## Логгирование

**В **Kotlin** есть несколько подходов к логированию:**

### Объект-компаньон

```kotlin
class MyClass {
    companion object {
        private val logger = LoggerFactory.getLogger(MyClass::class.java)
    }

    fun logSomething() {
        logger.info("Message")
    }
}
```

### Метод расширения

```kotlin
interface Logging

fun <T: Logging> T.logger(): Logger = getLogger(javaClass)

class MyClass : Logging {
    fun logSomething() {
        logger().info("Message")
    }
}
```

### Реified generic

```kotlin
inline fun <reified T: Logging> T.logger(): Logger = getLogger(T::class.java)
```

### Делегированное свойство

```kotlin
class LoggerDelegate<in R: Any>: ReadOnlyProperty<R, Logger> {
    override fun getValue(thisRef: R, property: KProperty<*>): Logger =
        getLogger(getClassForLogging(thisRef.javaClass))
}

class MyClass {
    private val logger by LoggerDelegate()
}
```

## Рефлексия

**Рефлексия -** это название возможности проверять, загружать и взаимодействовать с классами, полями и методами во время выполнения.

Все стандартные конструкции **Java Reflection** доступны и отлично работают с нашим кодом **Kotlin**. Это включает в себя класс **java.lang.Class**, а также все в пакете **java.lang.reflect**.

```kotlin
MyClass::class.java.methods
```

**Kotlin** также предлагает собственный **API** рефлексии, который мы можем использовать для решения этих проблем. Все точки входа в **Kotlin `Reflection` API** используют ссылки.

**Kotlin API** для сведений о классе сосредоточен вокруг класса **kotlin.reflect.KClass**. Доступ к этому можно получить с помощью оператора **::** из любого имени класса или экземпляра:

```kotlin
val listClass: KClass<List> = List::class
val name = "Baeldung"
val stringClass: KClass<String> = name::class
```

## Аннотации

**Kotlin** поддерживает аннотации, аналогично **Java**. Аннотации используются для добавления метаданных к коду:**

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyAnnotation

@MyAnnotation
class MyClass
```

### Встроенные аннотации

- **@JvmStatic** — делает метод статическим в байт-коде
- **@JvmOverloads** — генерирует перегруженные методы
- **@JvmName** — изменяет имя в байт-коде
- **@JvmField** — делает поле публичным без геттеров/сеттеров

## Руководство по @Deprecated

**Аннотация **@Deprecated** помечает элемент как устаревший:**

```kotlin
@Deprecated("Use newFunction() instead", ReplaceWith("newFunction()"))
fun oldFunction() {
    // ...
}
```

Компилятор будет предупреждать о использовании устаревших элементов.

## Kotlin быстрее, чем Java?

**Производительность **Kotlin** и **Java** схожа, так как оба компилируются в байт-код **JVM**. **Однако**:**

1. **Inline функции** могут улучшить производительность, избегая накладных расходов на вызовы функций
2. **Data классы** генерируют оптимизированный код для `equals()`, **hashCode()** и **toString()**
3. **Extension функции** компилируются как статические методы, не добавляя накладных расходов
4. В некоторых случаях **Kotlin** может быть даже быстрее благодаря оптимизациям компилятора

## Преобразование массива в varargs

**В **Kotlin** можно преобразовать массив в **varargs** с помощью оператора **\** (spread operator):**

```kotlin
val array = arrayOf("a", "b", "c")
someFunction(*array)  // распаковывает массив в varargs
```

## Расширение Data класса

**Data** классы в **Kotlin** можно расширять, но с некоторыми ограничениями:**

```kotlin
data class Person(val name: String, val age: Int)

// Расширение через extension функции
fun Person.fullInfo(): String = "$name is $age years old"
```

Однако, наследование **data** классов ограничено.

## Сравнение объектов данных

**Data** классы автоматически генерируют методы `equals()` и **hashCode()** на основе всех свойств конструктора:**

```kotlin
data class Person(val name: String, val age: Int)

val person1 = Person("Alice", 25)
val person2 = Person("Alice", 25)

println(person1 == person2)  // true
```

Если нужно сравнение только по определённым полям, нужно переопределить `equals()` и **hashCode()** вручную.

## Принципы SOLID

Принципы **SOLID** применимы к **Kotlin** так же, как и к другим объектно-ориентированным языкам.

Принцип **Single Responsibility** — это просто «разделяй и властвуй», с которым мы можем столкнуться во всех сферах, от образования до психологии. Правило **Open-Close** заставляет нас думать о массовом производстве, а не о единичных конструкциях. Прежде чем мы создадим наше программное обеспечение из классов и модулей, мы должны построить его из функций, и здесь функциональное программирование действительно становится очень полезным. Принцип Лискова и разделение интерфейса учат нас делать программные части небольшими и долговечными, а функция — это самая маленькая часть программного обеспечения. Инверсия зависимостей говорит о том, что эти части легко отделяются друг от друга.

**В **Kotlin** мы можем использовать:**

1. **Single Responsibility** — каждый класс/функция должна иметь одну ответственность
2. **Open/Closed** — открыт для расширения, закрыт для модификации
3. **Liskov Substitution** — подклассы должны заменять базовые классы
4. **Interface Segregation** — множество специфичных интерфейсов лучше одного общего
5. **Dependency Inversion** — зависимость от абстракций, а не от конкретных реализаций


## Type System (Система типов)

**Kotlin** имеет богатую систему типов, которая обеспечивает безопасность и выразительность кода.

### Базовые типы

```kotlin
// Примитивные типы (компилируются в примитивы JVM)
val byte: Byte = 127
val short: Short = 32767
val int: Int = 2147483647
val long: Long = 9223372036854775807L
val float: Float = 3.14f
val double: Double = 3.141592653589793
val char: Char = 'A'
val boolean: Boolean = true

// Ссылочные типы
val string: String = "Hello"
val any: Any = "Any type"
val unit: Unit = Unit
val nothing: Nothing? = null
```

### Any — корневой тип

**Any** — это супертип всех типов в **Kotlin** (аналог `Object` в Java).

```kotlin
// Any может содержать любой тип
val anyValue: Any = "String"
val anyNumber: Any = 42
val anyList: Any = listOf(1, 2, 3)

// Методы Any
val obj: Any = "Hello"
obj.toString()  // "Hello"
obj.hashCode()  // хеш-код
obj.equals("Hello")  // true

// Проверка типа
if (obj is String) {
    println(obj.length)  // Smart cast работает
}
```

### Unit — тип возвращаемого значения

**Unit** соответствует `void` в **Java**, но является полноценным типом.

```kotlin
// Функция возвращает Unit (неявно)
fun printHello() {
    println("Hello")
}

// Явное указание Unit
fun printHelloExplicit(): Unit {
    println("Hello")
}

// Unit можно использовать как значение
val unitValue: Unit = Unit
val unitFunction: () -> Unit = { println("Hello") }
```

### Nothing — тип без значений

**Nothing** — это тип, который не имеет значений. Используется для обозначения невозможных ситуаций.

```kotlin
// Функция, которая никогда не возвращает значение
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

// Использование Nothing
val result: String = fail("Error")  // компилятор знает, что это недостижимо

// Nothing в выражениях
val x: String = when {
    condition1 -> "value1"
    condition2 -> "value2"
    else -> fail("No condition met")  // Nothing позволяет компилятору знать, что все случаи покрыты
}

// Nothing? - единственное значение null
val nothing: Nothing? = null
```

### Nullable типы

```kotlin
// Nullable типы обозначаются символом ?
val nullableString: String? = null
val nullableInt: Int? = null

// Проверка на null
if (nullableString != null) {
    println(nullableString.length)  // Smart cast
}

// Безопасный вызов
val length = nullableString?.length  // Int?

// Elvis оператор
val length = nullableString?.length ?: 0  // Int

// Not-null assertion (опасно!)
val length = nullableString!!.length  // Int, но может выбросить NPE
```

### Smart Casts (Умные приведения)

**Kotlin** автоматически приводит типы после проверок.

```kotlin
// Smart cast с is
fun process(obj: Any) {
    if (obj is String) {
        println(obj.length)  // obj автоматически String
    }
}

// Smart cast с !is
fun process(obj: Any) {
    if (obj !is String) return
    println(obj.length)  // obj автоматически String
}

// Smart cast с when
fun process(obj: Any) {
    when (obj) {
        is String -> println(obj.length)  // String
        is Int -> println(obj * 2)        // Int
        else -> println("Unknown")
    }
}

// Smart cast с null checks
fun process(str: String?) {
    if (str != null) {
        println(str.length)  // str автоматически String (не String?)
    }
}

// Ограничения Smart Cast
var global: String? = null

fun example() {
    if (global != null) {
        // println(global.length)  // ОШИБКА! global может измениться
    }

    val local: String? = null
    if (local != null) {
        println(local.length)  // OK! local не может измениться
    }
}
```


## Type Aliases (Псевдонимы типов)

**Type aliases** позволяют создавать альтернативные имена для типов.

### Базовое использование

```kotlin
// Создание псевдонима
typealias Name = String
typealias Age = Int
typealias UserId = String

// Использование
fun createUser(name: Name, age: Age, id: UserId): User {
    // ...
}

// Псевдонимы для функциональных типов
typealias ClickHandler = (View) -> Unit
typealias Predicate<T> = (T) -> Boolean

fun handleClick(handler: ClickHandler) {
    // ...
}
```

### Псевдонимы для сложных типов

```kotlin
// Псевдонимы для generic типов
typealias StringMap = Map<String, String>
typealias IntList = List<Int>
typealias UserMap = Map<UserId, User>

// Псевдонимы для вложенных типов
class Outer {
    inner class Inner
}

typealias OuterInner = Outer.Inner

// Псевдонимы для типов с параметрами
typealias StringPredicate = Predicate<String>
typealias IntComparator = Comparator<Int>
```

### Практические примеры

```kotlin
// Упрощение сложных сигнатур
typealias EventHandler = (Event) -> Unit
typealias AsyncCallback<T> = (Result<T>) -> Unit

// Доменные типы
typealias Email = String
typealias PhoneNumber = String
typealias Price = Double

data class Order(
    val email: Email,
    val phone: PhoneNumber,
    val total: Price
)
```


## Object Expressions и Object Declarations

### Object Expressions (Анонимные объекты)

**Object expressions** создают объекты без явного объявления класса.

```kotlin
// Простой object expression
val handler = object {
    fun handle() {
        println("Handling")
    }
}

// Object expression с супертипами
val listener = object : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        println("Clicked")
    }

    override fun mouseEntered(e: MouseEvent) {
        println("Entered")
    }
}

// Object expression с несколькими интерфейсами
val handler = object : Clickable, Focusable {
    override fun click() {
        println("Clicked")
    }

    override fun focus() {
        println("Focused")
    }
}

// Object expression с доступом к внешним переменным
fun createCounter(): () -> Int {
    var count = 0
    return object {
        fun increment(): Int {
            return ++count
        }
    }.increment
}
```

### Object Declarations (Singleton)

**Object declarations** создают **singleton** объекты.

```kotlin
// Простой singleton
object DatabaseManager {
    fun connect() {
        println("Connecting to database")
    }

    fun disconnect() {
        println("Disconnecting from database")
    }
}

// Использование
DatabaseManager.connect()

// Object с супертипами
object DefaultListener : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        println("Default click")
    }

    override fun mouseEntered(e: MouseEvent) {
        println("Default enter")
    }
}

// Object как поле класса
class MyClass {
    object Factory {
        fun create(): MyClass = MyClass()
    }
}

val instance = MyClass.Factory.create()
```

### Companion Objects

**Companion objects** — это объекты, связанные с классом (аналог static в Java).

```kotlin
// Базовый companion object
class MyClass {
    companion object {
        fun create(): MyClass = MyClass()
        const val CONSTANT = "value"
    }
}

// Использование
val instance = MyClass.create()
val constant = MyClass.CONSTANT

// Companion object с именем
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()
    }
}

// Доступ по имени или без
val instance1 = MyClass.Factory.create()
val instance2 = MyClass.create()

// Companion object с интерфейсом
interface Factory<T> {
    fun create(): T
}

class MyClass {
    companion object : Factory<MyClass> {
        override fun create(): MyClass = MyClass()
    }
}

// Companion object для фабричных методов
class User private constructor(val name: String) {
    companion object {
        fun create(name: String): User {
            return User(name)
        }
    }
}
```


## Enum Classes

**Enum classes** представляют набор констант.

### Базовое использование

```kotlin
// Простой enum
enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

// Использование
val direction = Direction.NORTH
when (direction) {
    Direction.NORTH -> println("Going north")
    Direction.SOUTH -> println("Going south")
    Direction.EAST -> println("Going east")
    Direction.WEST -> println("Going west")
}
```

### Enum с параметрами

```kotlin
// Enum с свойствами
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}

// Enum с методами
enum class Planet(val mass: Double, val radius: Double) {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6);

    fun surfaceGravity(): Double {
        val G = 6.67300E-11
        return G * mass / (radius * radius)
    }
}

// Использование
val gravity = Planet.EARTH.surfaceGravity()
```

### Enum с интерфейсами

```kotlin
// Enum реализующий интерфейс
interface Printable {
    fun print()
}

enum class Color(val rgb: Int) : Printable {
    RED(0xFF0000) {
        override fun print() {
            println("Red color")
        }
    },
    GREEN(0x00FF00) {
        override fun print() {
            println("Green color")
        }
    },
    BLUE(0x0000FF) {
        override fun print() {
            println("Blue color")
        }
    }
}
```

### Работа с enum

```kotlin
enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

// Получение всех значений
val allDirections = Direction.values()

// Получение по имени
val north = Direction.valueOf("NORTH")

// Получение имени
val name = Direction.NORTH.name  // "NORTH"

// Получение порядкового номера
val ordinal = Direction.NORTH.ordinal  // 0

// Сравнение
if (Direction.NORTH < Direction.SOUTH) {
    // enum реализует Comparable
}
```


## Delegation (Делегирование)

### Class Delegation (Делегирование класса)

**Kotlin** поддерживает делегирование на уровне класса.

```kotlin
// Интерфейс
interface Base {
    fun print()
}

// Реализация
class BaseImpl(val x: Int) : Base {
    override fun print() {
        println(x)
    }
}

// Делегирование
class Derived(b: Base) : Base by b

// Использование
val b = BaseImpl(10)
val derived = Derived(b)
derived.print()  // 10

// Делегирование с переопределением
class Derived(b: Base) : Base by b {
    override fun print() {
        println("Derived")
        b.print()
    }
}
```

### Property Delegation (Делегирование свойств)

Делегирование свойств позволяет вынести логику работы со свойствами в отдельные классы.

```kotlin
// Базовый пример
class Example {
    var p: String by Delegate()
}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}
```

### Стандартные делегаты

#### Lazy

```kotlin
// Lazy инициализация
val lazyValue: String by lazy {
    println("Computed!")
    "Hello"
}

// Использование
println(lazyValue)  // Computed! Hello
println(lazyValue)  // Hello (не вычисляется повторно)

// Lazy с модом синхронизации
val lazyValue: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    "Thread-safe"
}
```

#### Observable

```kotlin
import kotlin.properties.Delegates

// Observable property
var name: String by Delegates.observable("Initial") { prop, old, new ->
    println("$old -> $new")
}

// Использование
name = "First"   // Initial -> First
name = "Second"  // First -> Second

// Vetoable (с возможностью отмены)
var value: Int by Delegates.vetoable(0) { prop, old, new ->
    println("$old -> $new")
    new > old  // разрешить только если новое значение больше
}

value = 5   // 0 -> 5 (разрешено)
value = 3   // 5 -> 3 (отменено, значение останется 5)
```

#### Map Delegation

```kotlin
// Делегирование к Map
class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

val user = User(mapOf(
    "name" to "John Doe",
    "age" to 25
))

println(user.name)  // John Doe
println(user.age)    // 25

// MutableMap для var свойств
class MutableUser(val map: MutableMap<String, Any?>) {
    var name: String by map
    var age: Int by map
}
```


## Infix Functions (Инфиксные функции)

**Infix** функции позволяют вызывать функции без точки и скобок.

### Определение infix функций

```kotlin
// Infix функция-расширение
infix fun Int.add(x: Int): Int {
    return this + x
}

// Использование
val result = 5 add 3  // 8
val result2 = 5.add(3)  // тоже работает

// Infix функция-член класса
class Point(val x: Int, val y: Int) {
    infix fun move(dx: Int): Point {
        return Point(x + dx, y)
    }
}

val point = Point(1, 2)
val moved = point move 5  // Point(6, 2)
```

### Требования к infix функциям

```kotlin
// Infix функция должна:
// 1. Быть функцией-членом или функцией-расширением
// 2. Иметь ровно один параметр
// 3. Не иметь vararg или параметров по умолчанию

// Правильно
infix fun String.matches(regex: Regex): Boolean {
    return regex.matches(this)
}

// Неправильно
// infix fun String.matches(vararg patterns: Regex)  // vararg не допускается
// infix fun String.matches(regex: Regex = Regex(".*"))  // параметры по умолчанию не допускаются
```

### Стандартные infix функции

```kotlin
// to - создание пары
val pair = "a" to 1  // Pair("a", 1)

// in - проверка принадлежности
val list = listOf(1, 2, 3)
val contains = 2 in list  // true

// until - создание диапазона
val range = 1 until 10  // 1..9

// step - шаг в диапазоне
for (i in 1..10 step 2) {
    println(i)  // 1, 3, 5, 7, 9
}
```


## Destructuring Declarations (Деструктуризация)

Деструктуризация позволяет извлекать значения из объектов в отдельные переменные.

### Деструктуризация data классов

```kotlin
data class Person(val name: String, val age: Int)

val person = Person("Alice", 25)

// Деструктуризация
val (name, age) = person
println(name)  // Alice
println(age)   // 25

// Пропуск значений
val (name, _) = person  // пропускаем age

// Деструктуризация в циклах
val people = listOf(
    Person("Alice", 25),
    Person("Bob", 30)
)

for ((name, age) in people) {
    println("$name is $age years old")
}
```

### Деструктуризация пар и триплетов

```kotlin
// Пары
val pair = "key" to "value"
val (key, value) = pair

// Триплеты (требует библиотеку)
// val triple = Triple(1, 2, 3)
// val (a, b, c) = triple
```

### Деструктуризация Map

```kotlin
val map = mapOf("Alice" to 25, "Bob" to 30)

// Деструктуризация в цикле
for ((name, age) in map) {
    println("$name is $age years old")
}

// Деструктуризация с фильтрацией
map.filter { (name, age) -> age > 25 }
    .forEach { (name, age) -> println("$name: $age") }
```

### Деструктуризация в лямбдах

```kotlin
val people = listOf(
    Person("Alice", 25),
    Person("Bob", 30)
)

// Деструктуризация в лямбде
people.map { (name, age) -> "$name is $age" }

// Деструктуризация с условием
people.filter { (name, age) -> age > 25 }
```

### Кастомная деструктуризация

```kotlin
// Для не-data классов нужно определить componentN функции
class Point(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

val point = Point(10, 20)
val (x, y) = point
println("x=$x, y=$y")  // x=10, y=20
```


## Ranges и Progressions (Диапазоны и прогрессии)

### Создание диапазонов

```kotlin
// IntRange
val range1 = 1..10        // от 1 до 10 включительно
val range2 = 1 until 10  // от 1 до 9 (10 исключено)

// CharRange
val charRange = 'a'..'z'

// Проверка принадлежности
val inRange = 5 in 1..10  // true
val notInRange = 15 in 1..10  // false

// Обратные диапазоны
val reverseRange = 10 downTo 1  // 10, 9, 8, ..., 1
```

### Итерация по диапазонам

```kotlin
// For loop
for (i in 1..10) {
    println(i)
}

// С шагом
for (i in 1..10 step 2) {
    println(i)  // 1, 3, 5, 7, 9
}

// Обратная итерация
for (i in 10 downTo 1) {
    println(i)  // 10, 9, 8, ..., 1
}

// Итерация по символам
for (c in 'a'..'z') {
    println(c)
}
```

### Операции с диапазонами

```kotlin
val range = 1..10

// Проверка принадлежности
5 in range        // true
15 in range       // false
5 !in range       // false

// Первый и последний элемент
range.first       // 1
range.last        // 10

// Проверка пустоты
range.isEmpty()   // false
(5..3).isEmpty() // true

// Содержится ли диапазон в другом
(3..7) in (1..10)  // true
```

### Progressions (Прогрессии)

```kotlin
// IntProgression
val progression = 1..10 step 2  // 1, 3, 5, 7, 9

// CharProgression
val charProgression = 'a'..'z' step 2

// Обратная прогрессия
val reverse = 10 downTo 1 step 2  // 10, 8, 6, 4, 2
```


## String Templates (Шаблоны строк)

**Kotlin** поддерживает интерполяцию строк.

### Базовое использование

```kotlin
val name = "Alice"
val age = 25

// Простая интерполяция
val message = "Hello, $name!"  // "Hello, Alice!"

// Интерполяция выражений
val message2 = "Hello, $name! You are $age years old."  // "Hello, Alice! You are 25 years old."

// Выражения в фигурных скобках
val message3 = "Next year you will be ${age + 1} years old."  // "Next year you will be 26 years old."

// Сложные выражения
val message4 = "Name length: ${name.length}"  // "Name length: 5"
```

### Raw Strings (Сырые строки)

```kotlin
// Тройные кавычки для многострочных строк
val text = """
    This is a
    multiline
    string
""".trimIndent()

// Сохранение форматирования
val html = """
    <html>
        <body>
            <p>Hello, World!</p>
        </body>
    </html>
""".trimIndent()

// Интерполяция в raw strings
val name = "Alice"
val raw = """
    Hello, $name!
    This is a raw string.
""".trimIndent()
```

### String Templates с выражениями

```kotlin
val items = listOf("apple", "banana", "orange")

// В циклах
val list = buildString {
    for (item in items) {
        append("$item, ")
    }
}

// С условиями
val message = "You have ${items.size} ${if (items.size == 1) "item" else "items"}"

// С функциями
val message2 = "First item: ${items.firstOrNull() ?: "none"}"
```


## When Expression (Выражение when)

**When** — это мощная альтернатива **switch** в **Java**.

### Базовое использование

```kotlin
// When как выражение
val x = 2
val result = when (x) {
    1 -> "One"
    2 -> "Two"
    3 -> "Three"
    else -> "Other"
}

// When как statement
when (x) {
    1 -> println("One")
    2 -> println("Two")
    else -> println("Other")
}
```

### Множественные значения

```kotlin
val x = 2
when (x) {
    1, 2 -> println("One or Two")
    3, 4 -> println("Three or Four")
    else -> println("Other")
}
```

### When с диапазонами

```kotlin
val x = 5
when (x) {
    in 1..10 -> println("Between 1 and 10")
    in 11..20 -> println("Between 11 and 20")
    !in 1..20 -> println("Outside 1-20")
    else -> println("Other")
}
```

### When с типами (Smart Cast)

```kotlin
fun process(value: Any) {
    when (value) {
        is String -> println("String: ${value.length}")
        is Int -> println("Int: ${value * 2}")
        is Boolean -> println("Boolean: $value")
        else -> println("Unknown type")
    }
}
```

### When без аргумента

```kotlin
val x = 5
val y = 10

when {
    x > y -> println("x is greater")
    x < y -> println("y is greater")
    else -> println("x equals y")
}

// С условиями
when {
    x > 0 && y > 0 -> println("Both positive")
    x < 0 || y < 0 -> println("At least one negative")
    else -> println("Both zero")
}
```

### When с функциями

```kotlin
fun isEven(x: Int) = x % 2 == 0

val x = 4
when {
    isEven(x) -> println("Even")
    else -> println("Odd")
}
```

### Exhaustive when (Исчерпывающий when)

```kotlin
// Когда when используется как выражение, все случаи должны быть покрыты
sealed class Result
class Success(val data: String) : Result()
class Error(val message: String) : Result()

fun process(result: Result): String = when (result) {
    is Success -> result.data
    is Error -> result.message
    // Компилятор проверяет, что все случаи покрыты
}
```


## Return и Labels (Возврат и метки)

### Обычный return

```kotlin
fun findFirstEven(numbers: List<Int>): Int? {
    for (num in numbers) {
        if (num % 2 == 0) {
            return num  // возвращает из функции
        }
    }
    return null
}
```

### Labeled Return (Возврат с меткой)

```kotlin
// Return с меткой для цикла
fun findFirstEven(numbers: List<Int>): Int? {
    numbers.forEach {
        if (it % 2 == 0) {
            return@forEach  // возвращает только из forEach, не из функции
        }
    }
    return null
}

// Return с меткой для функции
fun process() {
    listOf(1, 2, 3).forEach label@{
        if (it == 2) {
            return@label  // возврат из forEach
        }
        println(it)
    }
    println("Done")
}

// Имплицитная метка (имя функции)
fun process() {
    listOf(1, 2, 3).forEach {
        if (it == 2) {
            return@forEach  // возврат из forEach
        }
        println(it)
    }
}
```

### Return из лямбд

```kotlin
// Return из лямбды возвращает из внешней функции
fun process() {
    listOf(1, 2, 3).forEach {
        if (it == 2) {
            return  // возвращает из process(), не только из forEach
        }
        println(it)
    }
    println("This won't be printed")
}

// Return с меткой возвращает только из лямбды
fun process() {
    listOf(1, 2, 3).forEach {
        if (it == 2) {
            return@forEach  // возвращает только из forEach
        }
        println(it)
    }
    println("This will be printed")
}
```

### Break и Continue с метками

```kotlin
// Break с меткой
outer@ for (i in 1..3) {
    inner@ for (j in 1..3) {
        if (j == 2) {
            break@inner  // прерывает внутренний цикл
        }
        println("i=$i, j=$j")
    }
}

// Continue с меткой
outer@ for (i in 1..3) {
    inner@ for (j in 1..3) {
        if (j == 2) {
            continue@inner  // продолжает внутренний цикл
        }
        println("i=$i, j=$j")
    }
}
```


## Visibility Modifiers (Модификаторы видимости)

**Kotlin** имеет четыре модификатора видимости.

### Модификаторы для членов класса

```kotlin
class Example {
    public val publicProperty = "public"        // видно везде (по умолчанию)
    private val privateProperty = "private"      // видно только внутри класса
    protected val protectedProperty = "protected" // видно в классе и подклассах
    internal val internalProperty = "internal"   // видно в том же модуле
}
```

### Модификаторы для классов

```kotlin
// Public (по умолчанию)
public class PublicClass

// Private (только внутри файла)
private class PrivateClass

// Internal (в том же модуле)
internal class InternalClass

// Protected нельзя использовать для классов
```

### Модификаторы для функций

```kotlin
class Example {
    public fun publicFunction() {}      // видно везде
    private fun privateFunction() {}   // видно только в классе
    protected fun protectedFunction() {} // видно в классе и подклассах
    internal fun internalFunction() {}  // видно в модуле
}
```

### Модификаторы для конструкторов

```kotlin
class Example private constructor(val value: Int) {
    // Приватный конструктор
    companion object {
        fun create(value: Int): Example {
            return Example(value)
        }
    }
}

// Использование
val instance = Example.create(10)  // OK
// val instance2 = Example(10)     // ОШИБКА! конструктор приватный
```

### Сравнение с Java

| **Kotlin** | **Java** | Видимость |
|--------|------|-----------|
| `public` | `public` | Везде |
| `private` | `private` | Только в классе |
| `protected` | `protected` | Класс и подклассы |
| `internal` | **package-private** | В том же модуле |

### Top-level элементы

```kotlin
// Top-level функции и свойства
public fun publicFunction() {}      // видно везде
private fun privateFunction() {}   // видно только в файле
internal fun internalFunction() {}  // видно в модуле

// Top-level классы
public class PublicClass           // видно везде
private class PrivateClass         // видно только в файле
internal class InternalClass       // видно в модуле
```


## Расширенное покрытие Generics

### Variance (Вариантность)

Вариантность определяет, как отношения между типами влияют на отношения между **generic** типами.

#### Invariance (Инвариантность)

```kotlin
// По умолчанию generic типы инвариантны
class Box<T>(var value: T)

val intBox = Box<Int>(42)
// val numberBox: Box<Number> = intBox  // ОШИБКА! Box<Int> не является подтипом Box<Number>
```

#### Covariance (Ковариантность) — out

```kotlin
// out делает тип ковариантным (producer)
interface Producer<out T> {
    fun produce(): T
}

class StringProducer : Producer<String> {
    override fun produce(): String = "Hello"
}

// Producer<String> является подтипом Producer<Any>
val producer: Producer<Any> = StringProducer()  // OK!

// Ограничения: нельзя использовать T как параметр функции
// interface Producer<out T> {
//     fun consume(item: T)  // ОШИБКА! T в in-позиции
// }
```

#### Contravariance (Контравариантность) — in

```kotlin
// in делает тип контравариантным (consumer)
interface Consumer<in T> {
    fun consume(item: T)
}

class AnyConsumer : Consumer<Any> {
    override fun consume(item: Any) {
        println(item)
    }
}

// Consumer<Any> является подтипом Consumer<String>
val consumer: Consumer<String> = AnyConsumer()  // OK!

// Ограничения: нельзя использовать T как возвращаемый тип
// interface Consumer<in T> {
//     fun produce(): T  // ОШИБКА! T в out-позиции
// }
```

### Star Projections (Звездные проекции)

```kotlin
// Star projection - неизвестный тип
fun printItems(items: List<*>) {
    for (item in items) {
        println(item)  // item имеет тип Any?
    }
}

// Star projection для Producer
fun getProducer(): Producer<*> {
    return StringProducer()
}

val producer: Producer<*> = getProducer()
val item: Any? = producer.produce()  // Any?, не String

// Star projection для Consumer
fun getConsumer(): Consumer<*> {
    return AnyConsumer()
}

val consumer: Consumer<*> = getConsumer()
// consumer.consume(anything)  // ОШИБКА! нельзя передать ничего, кроме null
```

### Upper Bounds (Верхние границы)

```kotlin
// Ограничение типа сверху
class Container<T : Number>(val value: T) {
    fun toDouble(): Double = value.toDouble()
}

val intContainer = Container<Int>(42)
val doubleContainer = Container<Double>(3.14)
// val stringContainer = Container<String>("test")  // ОШИБКА! String не Number

// Несколько верхних границ (where clause)
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
    where T : CharSequence,
          T : Comparable<T> {
    return list.filter { it > threshold }.map { it.toString() }
}
```

### Reified Type Parameters (Овеществленные параметры типов)

```kotlin
// Reified позволяет обращаться к типу во время выполнения
inline fun <reified T> isInstanceOf(obj: Any?): Boolean {
    return obj is T
}

// Использование
println(isInstanceOf<String>("test"))  // true
println(isInstanceOf<Int>(42))         // true
println(isInstanceOf<String>(42))      // false

// Получение класса
inline fun <reified T> getTypeName(): String {
    return T::class.simpleName ?: "Unknown"
}

println(getTypeName<List<String>>())  // "ArrayList"

// Фильтрация по типу
inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    return filterIsInstance<T>()
}

val mixed = listOf(1, "a", 2, "b", 3)
val numbers = mixed.filterIsInstance<Int>()  // [1, 2, 3]
```

### Generic Functions (Generic функции)

```kotlin
// Простая generic функция
fun <T> singletonList(item: T): List<T> = listOf(item)

// Generic функция с ограничениями
fun <T : Comparable<T>> findMax(items: List<T>): T? {
    return items.maxOrNull()
}

// Generic extension функция
fun <T> List<T>.secondOrNull(): T? = getOrNull(1)

// Generic функция с несколькими параметрами
fun <T, R> List<T>.map(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (item in this) {
        result.add(transform(item))
    }
    return result
}
```

### Type Erasure (Стирание типов)

```kotlin
// Во время выполнения generic типы стираются
val list1 = listOf<String>()
val list2 = listOf<Int>()

// list1::class == list2::class  // true (оба List)

// Для сохранения информации о типе используйте reified
inline fun <reified T> checkType(obj: Any): Boolean {
    return obj is T
}
```


## Расширенное покрытие Reflection

### KClass — информация о классе

```kotlin
import kotlin.reflect.KClass
import kotlin.reflect.full.*

// Получение KClass
val stringClass: KClass<String> = String::class
val intClass = Int::class

// Свойства KClass
println(stringClass.simpleName)      // "String"
println(stringClass.qualifiedName)  // "kotlin.String"
println(stringClass.isAbstract)      // false
println(stringClass.isData)          // false
println(stringClass.isSealed)       // false
println(stringClass.isCompanion)     // false
```

### KFunction — информация о функциях

```kotlin
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

// Получение KFunction
fun greet(name: String): String = "Hello, $name!"

val function: KFunction<String> = ::greet

// Свойства функции
println(function.name)               // "greet"
println(function.returnType)         // String
println(function.parameters.size)   // 1
println(function.parameters[0].name) // "name"
println(function.parameters[0].type) // String

// Вызов через reflection
val result = function.call("Alice")  // "Hello, Alice!"
```

### KProperty — информация о свойствах

```kotlin
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

class Person(val name: String, var age: Int)

val person = Person("Alice", 25)

// Получение KProperty
val nameProperty: KProperty1<Person, String> = Person::name
val ageProperty: KMutableProperty1<Person, Int> = Person::age

// Чтение значения
val name = nameProperty.get(person)  // "Alice"
val age = ageProperty.get(person)     // 25

// Запись значения (для var)
ageProperty.set(person, 26)
println(person.age)  // 26
```

### KType — информация о типах

```kotlin
import kotlin.reflect.KType
import kotlin.reflect.full.*

// Получение KType
val stringType: KType = String::class.createType()
val listType: KType = List::class.createType(listOf(stringType))

// Свойства KType
println(stringType.classifier)       // KClass для String
println(stringType.arguments)        // []
println(stringType.isMarkedNullable) // false

// Generic типы
val listStringType = List::class.createType(listOf(String::class.createType()))
println(listStringType.arguments[0].type)  // String
```

### Аннотации через Reflection

```kotlin
import kotlin.reflect.full.*

// Аннотация
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyAnnotation(val value: String)

@MyAnnotation("test")
class MyClass

// Получение аннотаций
val annotations = MyClass::class.annotations
val myAnnotation = MyClass::class.findAnnotation<MyAnnotation>()
println(myAnnotation?.value)  // "test"

// Проверка наличия аннотации
val hasAnnotation = MyClass::class.hasAnnotation<MyAnnotation>()  // true
```

### Вызов функций через Reflection

```kotlin
import kotlin.reflect.full.*

class Calculator {
    fun add(a: Int, b: Int): Int = a + b
    fun multiply(a: Int, b: Int): Int = a * b
}

val calculator = Calculator()

// Получение функции
val addFunction = Calculator::add

// Вызов
val result = addFunction.call(calculator, 5, 3)  // 8

// Вызов с именованными параметрами
val result2 = addFunction.callBy(mapOf(
    addFunction.parameters[0] to calculator,
    addFunction.parameters[1] to 5,
    addFunction.parameters[2] to 3
))
```

### Работа с конструкторами

```kotlin
import kotlin.reflect.full.*

data class Person(val name: String, val age: Int)

// Получение конструктора
val constructor = Person::class.primaryConstructor

// Создание экземпляра
val person = constructor?.call("Alice", 25)

// Создание с именованными параметрами
val person2 = constructor?.callBy(mapOf(
    constructor.parameters[0] to "Bob",
    constructor.parameters[1] to 30
))
```


## Расширенное покрытие Annotations

### Создание аннотаций

```kotlin
// Простая аннотация
annotation class MyAnnotation

// Аннотация с параметрами
annotation class Author(val name: String, val date: String)

// Аннотация с параметрами по умолчанию
annotation class Version(val major: Int, val minor: Int = 0)

// Аннотация с массивами
annotation class Tags(vararg val tags: String)
```

### Мета-аннотации

```kotlin
// @Target - где можно использовать аннотацию
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class MyAnnotation

// AnnotationTarget значения:
// - CLASS
// - FUNCTION
// - PROPERTY
// - FIELD
// - CONSTRUCTOR
// - PROPERTY_GETTER
// - PROPERTY_SETTER
// - TYPE_PARAMETER
// - TYPE
// - EXPRESSION
// - FILE
// - TYPEALIAS

// @Retention - когда доступна аннотация
@Retention(AnnotationRetention.RUNTIME)
annotation class MyAnnotation

// AnnotationRetention значения:
// - SOURCE - только в исходном коде
// - BINARY - в байт-коде, но не в runtime
// - RUNTIME - доступна в runtime через reflection

// @Repeatable - можно использовать несколько раз
@Repeatable
annotation class Tag(val value: String)

// @MustBeDocumented - должна быть в документации
@MustBeDocumented
annotation class ApiDocumented
```

### Использование аннотаций

```kotlin
// На классе
@Author("John Doe", "2024-01-01")
class MyClass

// На функции
@Deprecated("Use newFunction() instead")
fun oldFunction() {}

// На параметре
fun process(@NotNull value: String) {}

// На свойстве
class MyClass {
    @JvmField
    val value = 42
}

// На конструкторе
class MyClass @Inject constructor(val value: String)

// На типе (type annotation)
fun process(): @NotNull String = "test"
```

### Встроенные аннотации Kotlin

```kotlin
// @JvmStatic - статический метод в JVM
class MyClass {
    companion object {
        @JvmStatic
        fun staticMethod() {}
    }
}

// @JvmOverloads - генерирует перегруженные методы
@JvmOverloads
fun greet(name: String, prefix: String = "Hello") {}

// @JvmName - изменяет имя в байт-коде
@JvmName("getStringList")
fun getList(): List<String> = listOf()

// @JvmField - делает поле публичным
class MyClass {
    @JvmField
    val value = 42
}

// @Throws - указывает проверяемые исключения
@Throws(IOException::class)
fun readFile() {}

// @Deprecated - помечает как устаревший
@Deprecated("Use newFunction() instead", ReplaceWith("newFunction()"))
fun oldFunction() {}
```

### Аннотации для Java Interop

```kotlin
// @JvmStatic
class MyClass {
    companion object {
        @JvmStatic
        fun staticMethod() {}
    }
}

// @JvmOverloads
@JvmOverloads
fun create(name: String, age: Int = 0) {}

// @JvmName
@file:JvmName("Utils")
package com.example

// @JvmField
class MyClass {
    @JvmField
    val value = 42
}

// @JvmSynthetic - скрывает от Java
@JvmSynthetic
fun internalFunction() {}
```


## Multiplatform Projects (Мультиплатформенные проекты)

### Общие концепции

**Kotlin Multiplatform** позволяет писать общий код для разных платформ.

```kotlin
// commonMain - общий код
expect class Platform() {
    val name: String
}

fun getPlatformName(): String {
    return Platform().name
}

// jvmMain - реализация для JVM
actual class Platform actual constructor() {
    actual val name: String = "JVM"
}

// jsMain - реализация для JavaScript
actual class Platform actual constructor() {
    actual val name: String = "JavaScript"
}

// nativeMain - реализация для Native
actual class Platform actual constructor() {
    actual val name: String = "Native"
}
```

### Expect/Actual механизм

```kotlin
// expect - объявление в commonMain
expect class HttpClient {
    fun get(url: String): String
}

// actual - реализация в platform-specific модулях
// jvmMain
actual class HttpClient {
    actual fun get(url: String): String {
        // Использование Java HTTP клиента
        return java.net.http.HttpClient.newHttpClient()
            .send(java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .build(),
                java.net.http.HttpResponse.BodyHandlers.ofString())
            .body()
    }
}

// jsMain
actual class HttpClient {
    actual fun get(url: String): String {
        // Использование JavaScript fetch
        return js("fetch(url).then(r => r.text())").toString()
    }
}
```

### Структура проекта

```text
multiplatform-project/
├── commonMain/
│   └── kotlin/
│       └── common.kt
├── jvmMain/
│   └── kotlin/
│       └── jvm.kt
├── jsMain/
│   └── kotlin/
│       └── js.kt
└── nativeMain/
    └── kotlin/
        └── native.kt
```

### Общие типы

```kotlin
// Общие типы доступны на всех платформах
expect class Date {
    fun getTime(): Long
}

// Использование в общем коде
fun formatDate(date: Date): String {
    return "Time: ${date.getTime()}"
}
```

### Платформенно-специфичные API

```kotlin
// Общий интерфейс
expect interface FileSystem {
    fun readFile(path: String): String
    fun writeFile(path: String, content: String)
}

// JVM реализация
actual class JvmFileSystem : FileSystem {
    actual override fun readFile(path: String): String {
        return java.io.File(path).readText()
    }

    actual override fun writeFile(path: String, content: String) {
        java.io.File(path).writeText(content)
    }
}
```


## Расширенное покрытие Data Classes

### Автоматически генерируемые методы

```kotlin
data class Person(val name: String, val age: Int)

// equals() - сравнение по всем свойствам
val person1 = Person("Alice", 25)
val person2 = Person("Alice", 25)
println(person1 == person2)  // true

// hashCode() - хеш-код на основе всех свойств
println(person1.hashCode() == person2.hashCode())  // true

// toString() - строковое представление
println(person1)  // Person(name=Alice, age=25)

// copy() - создание копии с изменением свойств
val person3 = person1.copy(age = 26)  // Person(name=Alice, age=26)
val person4 = person1.copy(name = "Bob")  // Person(name=Bob, age=25)

// componentN() - деструктуризация
val (name, age) = person1
println("$name is $age years old")  // Alice is 25 years old
```

### Ограничения Data Classes

```kotlin
// Data класс должен иметь хотя бы один параметр
// data class Empty  // ОШИБКА!

// Параметры должны быть val или var
data class Person(val name: String, var age: Int)  // OK
// data class Person(val name: String, age: Int)  // ОШИБКА! должен быть val или var

// Data класс не может быть abstract, open, sealed, inner
// abstract data class Person  // ОШИБКА!
// open data class Person  // ОШИБКА!
// sealed data class Person  // ОШИБКА!
// class Outer {
//     inner data class Person  // ОШИБКА!
// }
```

### Наследование Data Classes

```kotlin
// Data класс может наследоваться от других классов
open class Entity(val id: Int)
data class Person(val name: String, val age: Int) : Entity(1)

// Но equals() и hashCode() учитывают только свойства data класса
val person1 = Person("Alice", 25)
val person2 = Person("Alice", 25)
println(person1 == person2)  // true (сравниваются только name и age, не id)
```

### Custom методы в Data Classes

```kotlin
data class Person(val name: String, val age: Int) {
    // Можно добавлять методы
    fun isAdult(): Boolean = age >= 18

    // Можно переопределять toString()
    override fun toString(): String {
        return "Person(name='$name', age=$age, adult=${isAdult()})"
    }

    // Можно переопределять equals() и hashCode()
    // Но это не рекомендуется, так как нарушает контракт data класса
}
```


## Расширенное покрытие Sealed Classes

### Sealed Classes vs Enum Classes

```kotlin
// Enum - фиксированный набор констант
enum class Color {
    RED, GREEN, BLUE
}

// Sealed Class - фиксированный набор подтипов с данными
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Sealed Class более гибкий, так как может содержать данные
```

### Sealed Classes с данными

```kotlin
sealed class NetworkResult {
    data class Success(val data: String) : NetworkResult()
    data class Error(val code: Int, val message: String) : NetworkResult()
    object Loading : NetworkResult()
}

fun process(result: NetworkResult) {
    when (result) {
        is NetworkResult.Success -> println("Data: ${result.data}")
        is NetworkResult.Error -> println("Error ${result.code}: ${result.message}")
        NetworkResult.Loading -> println("Loading...")
        // Компилятор проверяет, что все случаи покрыты
    }
}
```

### Sealed Interfaces (Kotlin 1.5+)

```kotlin
// Sealed Interface
sealed interface Error {
    val message: String
}

data class NetworkError(override val message: String, val code: Int) : Error
data class ValidationError(override val message: String, val field: String) : Error

// Класс может реализовывать несколько sealed interfaces
sealed interface Readable
sealed interface Writable

class File : Readable, Writable
```

### Exhaustive When

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

// Exhaustive when - компилятор проверяет все случаи
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw Exception(message)
    // Если добавить новый подтип, компилятор потребует обработать его
}
```


Это расширение значительно увеличивает покрытие тем **Kotlin**. Файл теперь содержит детальную информацию по многим важным аспектам языка.

## Продвинутые техники работы с типами

### Type-safe builders с обобщенными типами

**Создание типобезопасных билдеров с использованием обобщенных типов:**

```kotlin
interface Builder<out T> {
    fun build(): T
}

class ListBuilder<T> : Builder<List<T>> {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
    }

    override fun build(): List<T> = items.toList()
}

fun <T> list(init: ListBuilder<T>.() -> Unit): List<T> {
    return ListBuilder<T>().apply(init).build()
}

// Использование
val numbers = list<Int> {
    add(1)
    add(2)
    add(3)
}

val strings = list<String> {
    add("a")
    add("b")
    add("c")
}
```

Типобезопасные билдеры с обобщенными типами позволяют создавать переиспользуемые и типобезопасные **DSL** для различных типов данных.

### Работа с типами во время выполнения

**Использование рефлексии для работы с типами во время выполнения:**

```kotlin
import kotlin.reflect.KClass
import kotlin.reflect.full.*

// Получение информации о типе
fun <T : Any> inspectType(obj: T) {
    val kClass = obj::class
    println("Class name: ${kClass.simpleName}")
    println("Qualified name: ${kClass.qualifiedName}")

    // Получение свойств
    kClass.memberProperties.forEach { prop ->
        println("Property: ${prop.name} = ${prop.get(obj)}")
    }

    // Получение функций
    kClass.memberFunctions.forEach { func ->
        println("Function: ${func.name}")
    }
}

// Использование
data class Person(val name: String, val age: Int)
inspectType(Person("Alice", 25))
```

Рефлексия позволяет работать с типами во время выполнения, что полезно для создания гибких систем и фреймворков.

## Оптимизация производительности

### Inline функции для производительности

**Использование **inline** функций для улучшения производительности:**

```kotlin
// Inline функция - код встраивается в место вызова
inline fun <T> measureTime(block: () -> T): Pair<T, Long> {
    val startTime = System.nanoTime()
    val result = block()
    val duration = System.nanoTime() - startTime
    return result to duration
}

// Использование
val (result, time) = measureTime {
    // Выполнение операции
    (1..1000000).sum()
}
println("Operation took ${time / 1_000_000}ms")

// Inline с reified типами
inline fun <reified T> isInstanceOf(value: Any?): Boolean {
    return value is T
}

val isString = isInstanceOf<String>("test")  // true
```

**Inline** функции устраняют накладные расходы на вызовы функций и позволяют использовать **reified** типы, что улучшает производительность.

### Оптимизация коллекций

**Оптимизация использования коллекций для лучшей производительности:**

```kotlin
// Использование sequences для цепочек операций
val largeList = (1..1_000_000).toList()

// Плохо - создает промежуточные коллекции
val result1 = largeList
    .filter { it % 2 == 0 }  // Промежуточная коллекция
    .map { it * 2 }           // Промежуточная коллекция
    .take(10)                 // Промежуточная коллекция
    .toList()

// Хорошо - использует sequence для ленивых вычислений
val result2 = largeList.asSequence()
    .filter { it % 2 == 0 }  // Ленивое вычисление
    .map { it * 2 }          // Ленивое вычисление
    .take(10)                // Ранняя остановка
    .toList()                // Создается только финальная коллекция

// Использование начальной емкости для ArrayList
val list = ArrayList<Int>(10000)  // Избегает перераспределения памяти
```

Оптимизация коллекций улучшает производительность приложения, особенно при работе с большими объемами данных.

## Работа с аннотациями

### Создание пользовательских аннотаций

**Создание и использование пользовательских аннотаций:**

```kotlin
// Определение аннотации
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Author(val name: String, val date: String)

// Использование аннотации
@Author(name = "John Doe", date = "2024-01-01")
class MyClass {
    @Author(name = "John Doe", date = "2024-01-15")
    fun myFunction() {
        // ...
    }
}

// Обработка аннотаций через рефлексию
fun processAnnotations(clazz: KClass<*>) {
    clazz.annotations.forEach { annotation ->
        when (annotation) {
            is Author -> {
                println("Author: ${annotation.name}, Date: ${annotation.date}")
            }
        }
    }
}
```

Пользовательские аннотации позволяют добавлять метаданные к коду, которые могут использоваться для генерации кода, валидации или документации.

### Аннотации для обработки ошибок

**Использование аннотаций для обработки ошибок и валидации:**

```kotlin
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class NotNull(val message: String = "Value cannot be null")

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Range(val min: Double, val max: Double)

class User {
    @NotNull("Name is required")
    var name: String? = null

    @Range(min = 0.0, max = 150.0)
    var age: Int = 0
}

fun validate(obj: Any): List<String> {
    val errors = mutableListOf<String>()
    obj::class.memberProperties.forEach { prop ->
        prop.annotations.forEach { annotation ->
            when (annotation) {
                is NotNull -> {
                    if (prop.get(obj) == null) {
                        errors.add(annotation.message)
                    }
                }
                is Range -> {
                    val value = prop.get(obj) as? Number?.toDouble()
                    if (value != null && (value < annotation.min || value > annotation.max)) {
                        errors.add("${prop.name} must be between ${annotation.min} and ${annotation.max}")
                    }
                }
            }
        }
    }
    return errors
}
```

Аннотации для валидации позволяют автоматически проверять данные и выявлять ошибки на ранних этапах.

## Продвинутые паттерны проектирования

### Strategy Pattern с функциональным подходом

**Реализация паттерна **Strategy** с использованием функций высшего порядка:**

```kotlin
// Strategy через функции
typealias SortingStrategy<T> = (List<T>) -> List<T>

val quickSort: SortingStrategy<Int> = { list ->
    // Реализация quick sort
    list.sorted()
}

val bubbleSort: SortingStrategy<Int> = { list ->
    // Реализация bubble sort
    list.sorted()
}

class Sorter<T>(private val strategy: SortingStrategy<T>) {
    fun sort(items: List<T>): List<T> {
        return strategy(items)
    }
}

// Использование
val sorter = Sorter(quickSort)
val sorted = sorter.sort(listOf(3, 1, 4, 1, 5, 9, 2, 6))
```

Использование функций вместо классов для паттерна **Strategy** делает код более функциональным и гибким.

### Observer Pattern с Delegated Properties

**Реализация паттерна **Observer** с использованием делегированных свойств:**

```kotlin
import kotlin.properties.Delegates

class ObservableProperty<T>(initialValue: T) {
    private var value: T = initialValue
    private val observers = mutableListOf<(T, T) -> Unit>()

    fun addObserver(observer: (T, T) -> Unit) {
        observers.add(observer)
    }

    fun getValue(): T = value

    fun setValue(newValue: T) {
        val oldValue = value
        value = newValue
        observers.forEach { it(oldValue, newValue) }
    }
}

class User {
    var name: String by Delegates.observable("") { prop, old, new ->
        println("Name changed from $old to $new")
    }

    var age: Int by Delegates.observable(0) { prop, old, new ->
        println("Age changed from $old to $new")
    }
}

// Использование
val user = User()
user.name = "Alice"  // Name changed from  to Alice
user.age = 25        // Age changed from 0 to 25
```

**Delegated properties** позволяют элегантно реализовать паттерн **Observer** без дополнительного кода.

Этот файл содержит полное руководство по основам **Kotlin**, покрывающее все основные аспекты от базовых концепций до продвинутых техник, оптимизации производительности и паттернов проектирования.

## Продвинутые техники работы с классами

### Nested классы и Inner классы

**Работа с вложенными и внутренними классами:**

```kotlin
// Nested класс - не имеет доступа к членам внешнего класса
class Outer {
    private val outerProperty = "Outer"

    class Nested {
        fun getOuterProperty(): String {
            // Не может обратиться к outerProperty
            return "Cannot access outer property"
        }
    }
}

// Inner класс - имеет доступ к членам внешнего класса
class Outer {
    private val outerProperty = "Outer"

    inner class Inner {
        fun getOuterProperty(): String {
            return outerProperty  // Может обратиться к outerProperty
        }
    }
}

// Использование
val outer = Outer()
val nested = Outer.Nested()  // Nested класс
val inner = outer.Inner()    // Inner класс
```

Понимание различий между **nested** и **inner** классами помогает создавать правильные структуры данных.

### Anonymous классы и Object expressions

**Использование анонимных классов и **object expressions**:**

```kotlin
// Object expression - создание анонимного объекта
val listener = object : ClickListener {
    override fun onClick() {
        println("Clicked")
    }

    fun customMethod() {
        println("Custom method")
    }
}

// Использование object expression для реализации интерфейсов
val comparator = object : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        return o1.length.compareTo(o2.length)
    }
}

// Использование object expression как singleton
val logger = object {
    fun log(message: String) {
        println("[${System.currentTimeMillis()}] $message")
    }
}

logger.log("Test message")
```

**Object expressions** позволяют создавать анонимные объекты для реализации интерфейсов или создания **singleton**-подобных объектов.

## Работа с типами

### Type inference и explicit типы

**Работа с выведением типов и явным указанием типов:**

```kotlin
// Type inference - компилятор выводит тип
val name = "Alice"  // String
val age = 25        // Int
val list = listOf(1, 2, 3)  // List<Int>

// Explicit типы - явное указание типа
val name: String = "Alice"
val age: Int = 25
val list: List<Int> = listOf(1, 2, 3)

// Когда нужны explicit типы
val nullable: String? = null
val function: (Int, Int) -> Int = { a, b -> a + b }
val map: Map<String, List<Int>> = emptyMap()

// Type inference для generic типов
fun <T> process(items: List<T>): List<T> {
    return items.filter { /* ... */ }
}

val numbers: List<Int> = process(listOf(1, 2, 3))  // T выводится как Int
val strings: List<String> = process(listOf("a", "b", "c"))  // T выводится как String
```

Понимание **type inference** и когда нужно использовать **explicit** типы помогает писать более читаемый и безопасный код.

### Type casting и проверки типов

**Работа с приведением типов и проверками типов:**

```kotlin
// Safe cast - возвращает null если приведение невозможно
val obj: Any = "test"
val str: String? = obj as? String  // "test"
val num: Int? = obj as? Int        // null

// Unsafe cast - выбрасывает ClassCastException если приведение невозможно
val str2: String = obj as String   // "test"
// val num2: Int = obj as Int      // ClassCastException

// Проверка типа через is
if (obj is String) {
    println(obj.length)  // Smart cast - obj автоматически приводится к String
}

// when для проверки типа
when (obj) {
    is String -> println("String: ${obj.length}")
    is Int -> println("Int: $obj")
    is List<*> -> println("List with ${obj.size} elements")
    else -> println("Unknown type")
}

// Проверка типа для generic
inline fun <reified T> checkType(value: Any): Boolean {
    return value is T
}

val isString = checkType<String>("test")  // true
val isInt = checkType<Int>("test")        // false
```

Правильное использование проверок типов и приведения типов делает код более безопасным и предсказуемым.

## Работа с исключениями

### Продвинутая обработка исключений

**Продвинутые техники работы с исключениями:**

```kotlin
// Пользовательские исключения
class ValidationException(message: String) : Exception(message)
class BusinessException(message: String) : Exception(message)

// Иерархия исключений
sealed class AppException(message: String) : Exception(message) {
    class ValidationError(message: String) : AppException(message)
    class BusinessError(message: String) : AppException(message)
    class SystemError(message: String) : AppException(message)
}

// Обработка исключений с типизацией
fun processUser(user: User) {
    try {
        validateUser(user)
        saveUser(user)
    } catch (e: ValidationException) {
        logger.warn("Validation error: ${e.message}")
        handleValidationError(e)
    } catch (e: BusinessException) {
        logger.error("Business error: ${e.message}")
        handleBusinessError(e)
    } catch (e: Exception) {
        logger.error("Unexpected error: ${e.message}", e)
        handleUnexpectedError(e)
    }
}

// Использование Result для обработки ошибок
fun processUserResult(user: User): Result<User> {
    return runCatching {
        validateUser(user)
        saveUser(user)
    }.onFailure { error ->
        logger.error("Error processing user: ${error.message}", error)
    }
}

// Использование
when (val result = processUserResult(user)) {
    is Result.Success -> println("User saved: ${result.value}")
    is Result.Failure -> println("Error: ${result.exception.message}")
}
```

Продвинутая обработка исключений позволяет создавать более надежные приложения с правильной обработкой различных типов ошибок.

### Functional подход к обработке ошибок

**Использование функционального подхода для обработки ошибок:**

```kotlin
// Either для обработки ошибок
sealed class Either<out L, out R> {
    data class Left<L>(val value: L) : Either<L, Nothing>()
    data class Right<R>(val value: R) : Either<Nothing, R>()

    fun <B> map(f: (R) -> B): Either<L, B> = when (this) {
        is Left -> this
        is Right -> Right(f(value))
    }

    fun <B> flatMap(f: (R) -> Either<L, B>): Either<L, B> = when (this) {
        is Left -> this
        is Right -> f(value)
    }

    fun getOrElse(default: @UnsafeVariance R): R = when (this) {
        is Left -> default
        is Right -> value
    }
}

// Использование Either
fun divide(a: Int, b: Int): Either<String, Int> {
    return if (b == 0) {
        Either.Left("Division by zero")
    } else {
        Either.Right(a / b)
    }
}

fun calculate(a: Int, b: Int, c: Int): Either<String, Int> {
    return divide(a, b)
        .flatMap { result ->
            divide(result, c)
        }
}

// Использование
when (val result = calculate(10, 2, 5)) {
    is Either.Left -> println("Error: ${result.value}")
    is Either.Right -> println("Result: ${result.value}")
}
```

Функциональный подход к обработке ошибок делает код более предсказуемым и безопасным, избегая использования исключений для контроля потока.

## Работа с корутинами в базовых концепциях

### Асинхронное программирование

**Использование корутин для асинхронного программирования:**

```kotlin
import kotlinx.coroutines.*

// Базовое использование корутин
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}

// Асинхронные операции
suspend fun fetchData(): String {
    delay(1000)  // Имитация сетевого запроса
    return "Data"
}

suspend fun processData(data: String): String {
    delay(500)  // Имитация обработки
    return "Processed: $data"
}

// Использование
fun main() = runBlocking {
    val data = async { fetchData() }
    val processed = async { processData(data.await()) }
    println(processed.await())
}

// Параллельная обработка
fun main() = runBlocking {
    val results = coroutineScope {
        val task1 = async { fetchData1() }
        val task2 = async { fetchData2() }
        val task3 = async { fetchData3() }
        listOf(task1, task2, task3).awaitAll()
    }
    println(results)
}
```

Корутины позволяют писать асинхронный код в синхронном стиле, что делает код более читаемым и понятным.

### Flow для обработки потоков данных

**Использование **Flow** для обработки асинхронных потоков данных:**

```kotlin
import kotlinx.coroutines.flow.*

// Создание Flow
fun numbers(): Flow<Int> = flow {
    for (i in 1..10) {
        delay(100)
        emit(i)
    }
}

// Использование Flow
fun main() = runBlocking {
    numbers()
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .collect { value ->
            println(value)
        }
}

// StateFlow для состояния
val _state = MutableStateFlow(0)
val state: StateFlow<Int> = _state.asStateFlow()

fun main() = runBlocking {
    state.collect { value ->
        println("State: $value")
    }
}

// SharedFlow для событий
val _events = MutableSharedFlow<Event>()
val events: SharedFlow<Event> = _events.asSharedFlow()

fun emitEvent(event: Event) {
    _events.emit(event)
}
```

**Flow** позволяет обрабатывать асинхронные потоки данных эффективно и безопасно.

## Работа с рефлексией

### Продвинутая рефлексия

**Использование рефлексии для динамической работы с типами:**

```kotlin
import kotlin.reflect.*

// Получение информации о типе
fun <T : Any> inspectType(obj: T) {
    val kClass = obj::class
    println("Class: ${kClass.simpleName}")
    println("Qualified name: ${kClass.qualifiedName}")

    // Получение свойств
    kClass.memberProperties.forEach { prop ->
        println("Property: ${prop.name} = ${prop.get(obj)}")
    }

    // Получение функций
    kClass.memberFunctions.forEach { func ->
        println("Function: ${func.name}")
    }

    // Получение конструкторов
    kClass.constructors.forEach { constructor ->
        println("Constructor: ${constructor.parameters.size} parameters")
    }
}

// Вызов методов через рефлексию
fun <T : Any> callMethod(obj: T, methodName: String, vararg args: Any?): Any? {
    val method = obj::class.memberFunctions.find { it.name == methodName }
    return method?.call(obj, *args)
}

// Создание экземпляров через рефлексию
fun <T : Any> createInstance(kClass: KClass<T>, vararg args: Any?): T? {
    val constructor = kClass.constructors.find { it.parameters.size == args.size }
    return constructor?.call(*args)
}

// Использование
data class Person(val name: String, val age: Int) {
    fun greet() = "Hello, I'm $name"
}

val person = Person("Alice", 25)
inspectType(person)
val greeting = callMethod(person, "greet")  // "Hello, I'm Alice"

val newPerson = createInstance(Person::class, "Bob", 30)
```

Рефлексия позволяет создавать гибкие системы, которые могут работать с типами, известными только во время выполнения.

## Дополнение: Extension-подходы для классов

### Extension функции для классов

**Расширение функциональности существующих классов:**

```kotlin
// Extension функции для String
fun String.removeWhitespace(): String {
    return this.replace(Regex("\\s+"), "")
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
}

fun String.capitalizeWords(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}

// Использование
val email = "alice@example.com"
println(email.isValidEmail())  // true

val text = "hello world"
println(text.capitalizeWords())  // "Hello World"

// Extension функции для коллекций
fun <T> List<T>.secondOrNull(): T? {
    return this.getOrNull(1)
}

fun <T> List<T>.secondLastOrNull(): T? {
    return if (size >= 2) this[size - 2] else null
}

fun <T> List<T>.swap(index1: Int, index2: Int): List<T> {
    val result = this.toMutableList()
    val tmp = result[index1]
    result[index1] = result[index2]
    result[index2] = tmp
    return result
}

// Использование
val list = listOf(1, 2, 3, 4, 5)
println(list.secondOrNull())  // 2
println(list.secondLastOrNull())  // 4
println(list.swap(0, 4))  // [5, 2, 3, 4, 1]
```

**Extension** функции позволяют добавлять функциональность к существующим классам без наследования.

### Extension свойства

**Добавление свойств к существующим классам:**

```kotlin
// Extension свойства
val String.isPalindrome: Boolean
    get() = this == this.reversed()

val String.wordCount: Int
    get() = this.split(Regex("\\s+")).count { it.isNotBlank() }

val List<Int>.sum: Int
    get() = this.sum()

val List<Int>.product: Int
    get() = this.fold(1) { acc, value -> acc * value }

// Использование
val text = "racecar"
println(text.isPalindrome)  // true

val sentence = "Hello world Kotlin"
println(sentence.wordCount)  // 3

val numbers = listOf(1, 2, 3, 4, 5)
println(numbers.sum)  // 15
println(numbers.product)  // 120

// Extension свойства с mutable state
var StringBuilder.lastChar: Char
    get() = this[this.length - 1]
    set(value) {
        this.setCharAt(this.length - 1, value)
    }

// Использование
val sb = StringBuilder("Hello")
println(sb.lastChar)  // 'o'
sb.lastChar = 'a'
println(sb.toString())  // "Hella"
```

**Extension** свойства позволяют добавлять вычисляемые свойства к существующим классам, что делает код более выразительным.

## Работа с типами и generics

### Продвинутые generics

**Использование продвинутых возможностей **generics**:**

```kotlin
// Variance annotations
interface Source<out T> {
    fun get(): T
}

interface Sink<in T> {
    fun put(value: T)
}

// Использование variance
fun copy(from: Source<String>, to: Sink<Any>) {
    to.put(from.get())  // String безопасно передается как Any
}

// Star projections
fun printList(list: List<*>) {
    for (item in list) {
        println(item)
    }
}

// Reified generics
inline fun <reified T> isInstanceOf(value: Any?): Boolean {
    return value is T
}

inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    return this.filterIsInstance<T>()
}

// Использование
val mixed: List<Any> = listOf(1, "hello", 2, "world", 3)
val strings: List<String> = mixed.filterIsInstance<String>()
val ints: List<Int> = mixed.filterIsInstance<Int>()

// Upper bounds
fun <T : Comparable<T>> max(a: T, b: T): T {
    return if (a > b) a else b
}

// Multiple bounds
fun <T> copyIfGreater(list: List<T>, threshold: T): List<T>
        where T : CharSequence,
              T : Comparable<T> {
    return list.filter { it > threshold }
}

// Использование
val maxInt = max(5, 10)  // 10
val maxString = max("apple", "banana")  // "banana"

val strings = listOf("apple", "banana", "cherry")
val filtered = copyIfGreater(strings, "banana")  // ["cherry"]
```

Продвинутые **generics** позволяют создавать гибкие и типобезопасные **API**.

### Type constraints и bounds

**Использование ограничений типов для **generics**:**

```kotlin
// Upper bounds
fun <T : Number> sum(numbers: List<T>): Double {
    return numbers.sumOf { it.toDouble() }
}

// Multiple constraints
fun <T> process(data: T)
        where T : Comparable<T>,
              T : CharSequence {
    val sorted = data.toString().toCharArray().sorted().joinToString("")
    println("Processed: $sorted")
}

// Нижние границы через super (в Java стиле)
interface NumberList {
    fun add(number: Number)
    fun get(index: Int): Number?
}

class MutableNumberList : NumberList {
    private val list = mutableListOf<Number>()

    override fun add(number: Number) {
        list.add(number)
    }

    override fun get(index: Int): Number? {
        return list.getOrNull(index)
    }

    fun addInts(ints: List<Int>) {
        ints.forEach { add(it) }
    }
}

// Использование
val numbers = listOf(1, 2.5, 3, 4.7, 5)
val total = sum(numbers)  // 16.2

val text = "hello"
process(text)  // "ehllo"
```

Ограничения типов делают **generics** более безопасными и предсказуемыми.

Этот файл содержит полное руководство по основам **Kotlin**, покрывающее все основные аспекты от базовых концепций до продвинутых техник, работы с типами, корутинами, рефлексией, обработкой ошибок, оптимизации производительности и паттернов проектирования.

## Дополнительные возможности Kotlin

### Работа с делегатами

**Продвинутые техники использования делегатов:**

```kotlin
// Lazy делегат
val lazyValue: String by lazy {
    println("Computing...")
    "Computed value"
}

// Observable делегат
var observableValue: String by Delegates.observable("Initial") { prop, old, new ->
    println("Property ${prop.name} changed from $old to $new")
}

// Vetoable делегат
var vetoableValue: Int by Delegates.vetoable(0) { prop, old, new ->
    println("Attempting to change ${prop.name} from $old to $new")
    new > old  // Разрешить изменение только если новое значение больше старого
}

// NotNull делегат
var notNullValue: String by Delegates.notNull<String>()

// Кастомный делегат
class CustomDelegate<T>(private var value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("Getting value of ${property.name}")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("Setting value of ${property.name} to $value")
        this.value = value
    }
}

// Использование
class MyClass {
    var customProperty: String by CustomDelegate("Initial")
}

val instance = MyClass()
println(instance.customProperty)  // Getting value...
instance.customProperty = "New"    // Setting value...
```

Делегаты позволяют переиспользовать логику работы со свойствами и создавать мощные абстракции.

### Работа с контрактами

**Использование контрактов для улучшения статического анализа:**

```kotlin
// Контракты для функций
@ExperimentalContracts
fun String?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }
    return !isNullOrBlank()
}

// Использование
val str: String? = getString()
if (str.isNotNullOrBlank()) {
    println(str.length)  // Компилятор знает, что str не null
}

// Контракты для проверок
@ExperimentalContracts
fun requireNotNull(value: String?): String {
    contract {
        returns() implies (value != null)
    }
    require(value != null) { "Value must not be null" }
    return value
}
```

Контракты помогают компилятору лучше понимать код и улучшают статический анализ.

### Inline классы (Value Classes)

**Использование **inline** классов для типобезопасности:**

```kotlin
// Inline класс для типобезопасности
@JvmInline
value class UserId(val value: Long)

@JvmInline
value class ProductId(val value: Long)

// Использование
fun getUser(id: UserId): User? {
    return userRepository.findById(id.value)
}

fun getProduct(id: ProductId): Product? {
    return productRepository.findById(id.value)
}

// Компилятор предотвращает смешивание типов
val userId = UserId(1)
val productId = ProductId(1)

// getUser(productId)  // Ошибка компиляции!
// getProduct(userId)  // Ошибка компиляции!

// Inline классы с методами
@JvmInline
value class Email(val value: String) {
    fun isValid(): Boolean {
        return value.contains("@")
    }

    fun domain(): String {
        return value.substringAfter("@")
    }
}

// Использование
val email = Email("user@example.com")
println(email.isValid())  // true
println(email.domain())   // "example.com"
```

**Inline** классы обеспечивают типобезопасность без накладных расходов на **runtime**.

## Дополнительные возможности языка

### Работа с контекстами

**Использование контекстов для передачи данных:**

```kotlin
// Thread-local контекст
val context = ThreadLocal<String>()

fun withContext(value: String, block: () -> Unit) {
    val oldValue = context.get()
    try {
        context.set(value)
        block()
    } finally {
        context.set(oldValue)
    }
}

// Использование
withContext("User123") {
    println("Current context: ${context.get()}")  // User123
    processRequest()
}

// Coroutine context
import kotlin.coroutines.coroutineContext

suspend fun getCurrentUserId(): Long? {
    return coroutineContext[UserContext]?.userId
}

data class UserContext(val userId: Long) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<UserContext>
    override val key: CoroutineContext.Key<*> = Key
}

// Использование
suspend fun processWithUser(userId: Long) {
    withContext(UserContext(userId)) {
        val currentUserId = getCurrentUserId()
        println("Processing for user: $currentUserId")
    }
}
```

Контексты позволяют передавать данные через различные уровни вызовов без явной передачи параметров.

### Работа с контрактами (кратко)

В этом блоке оставлен краткий operational-комментарий: контракты особенно полезны в API валидации и guard-функциях, где важно улучшить static analysis и убрать лишние null-checks.

Подробные примеры контрактов приведены выше в разделе `Дополнительные возможности Kotlin`, чтобы не дублировать одинаковые код-фрагменты.

## Дополнение: операторы и инварианты

### Работа с операторами перегрузки

**Перегрузка операторов для пользовательских типов:**

```kotlin
// Перегрузка операторов
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }

    operator fun times(factor: Int): Point {
        return Point(x * factor, y * factor)
    }

    operator fun unaryMinus(): Point {
        return Point(-x, -y)
    }

    operator fun compareTo(other: Point): Int {
        return (x + y).compareTo(other.x + other.y)
    }
}

// Использование
val p1 = Point(1, 2)
val p2 = Point(3, 4)
val sum = p1 + p2  // Point(4, 6)
val diff = p2 - p1  // Point(2, 2)
val scaled = p1 * 3  // Point(3, 6)
val negated = -p1  // Point(-1, -2)
val comparison = p1 < p2  // true
```

Перегрузка операторов делает код более читаемым и естественным для математических операций.

### Работа с инвариантами

**Использование инвариантов для проверки состояния:**

```kotlin
// Инварианты для проверки состояния
class BankAccount(private var balance: Int) {
    init {
        require(balance >= 0) { "Balance cannot be negative" }
    }

    fun deposit(amount: Int) {
        require(amount > 0) { "Deposit amount must be positive" }
        balance += amount
        check(balance >= 0) { "Balance invariant violated" }
    }

    fun withdraw(amount: Int) {
        require(amount > 0) { "Withdrawal amount must be positive" }
        check(balance >= amount) { "Insufficient funds" }
        balance -= amount
        assert(balance >= 0) { "Balance should never be negative" }
    }
}
```

Инварианты помогают поддерживать корректное состояние объектов.

## Дополнительные возможности

### Работа с контрактами и инвариантами

Этот раздел оставлен как краткий конспект:
- контракты — для усиления статического анализа и предсказуемых preconditions;
- инварианты — для защиты бизнес-состояния на уровне модели.

Кодовые примеры вынесены в профильные разделы выше, чтобы не дублировать блоки.

### Работа с типами и type inference

Для `type inference` правило простое: использовать вывод типов по умолчанию, но явно фиксировать тип в публичном API и в сложных generic-сигнатурах, где это повышает читаемость.


## Решение проблем

| Проблема | Что проверить в первую очередь | Быстрое решение |
|---|---|---|
| `NullPointerException` в Kotlin-коде | наличие `!!`, платформенные типы из Java | убрать `!!`, ввести `?.`/`?:`, добавить явную nullability-аннотацию на Java-границе |
| Блокировка потоков из-за `runBlocking` | где вызывается `runBlocking` (web/request path) | оставить `runBlocking` только в `main` и тестах; в runtime использовать `suspend` + корректный dispatcher |
| Утечки/висящие coroutine jobs | использование `GlobalScope` и отсутствие structured concurrency | привязать корутины к `CoroutineScope` компонента, отменять jobs в lifecycle hooks |
| Неочевидные generic-типы в API | публичные функции с агрессивным type inference | явно фиксировать типы в публичных сигнатурах и возвращаемых моделях |
| Медленная сборка | `kapt`, избыточные annotation processors, крупные модули | по возможности перейти на `ksp`, сократить processors, разделить тяжёлые модули |

## Частые вопросы

**Когда выбирать `val`, а когда `var`?**
По умолчанию использовать `val`; `var` только когда изменение состояния — явная часть модели.

**Нужно ли писать явные типы, если есть type inference?**
В локальном коде можно полагаться на inference, но в публичном API явные типы обычно повышают читаемость и стабильность контракта.

**`suspend` функция — это «параллельность»?**
Нет. `suspend` даёт неблокирующую приостановку; параллелизм появляется только при явном запуске нескольких coroutine jobs.

**Что выбрать для модели: `data class`, `value class` или `sealed`?**
`data class` — для DTO/состояния, `value class` — для типобезопасных обёрток над одним значением, `sealed` — для закрытых иерархий состояний/результатов.

**Как безопасно интегрироваться с Java-кодом?**
Явно отмечать nullability на Java-границе, избегать platform types без проверок и покрывать критичные interop-участки интеграционными тестами.

## Заключение

**Kotlin** предоставляет мощные инструменты для разработки современных приложений. Понимание основных концепций языка, работы с типами, **generics**, корутинами, **Flow**, рефлексией и других продвинутых возможностей позволяет создавать эффективный, безопасный и поддерживаемый код. Использование паттернов проектирования, делегатов, контрактов и других техник помогает структурировать код и улучшать его качество.

## Дополнительные ресурсы

**Для дальнейшего изучения **Kotlin** рекомендуется:**

- Официальная документация **Kotlin**: **https**://**kotlinlang.org**/**docs**/**home.html**
- **Kotlin Playground**: **https**://**play.kotlinlang.org**/
- **Kotlin Koans**: **https**://**play.kotlinlang.org**/**koans**
- **Kotlin Style Guide**: **https**://**kotlinlang.org**/**docs**/**coding-conventions.html**
- **Kotlin Coroutines Guide**: **https**://**kotlinlang.org**/**docs**/**coroutines-guide.html**
- **Kotlin Flow Guide**: **https**://**kotlinlang.org**/**docs**/**flow.html**

## Итоговые рекомендации

**При разработке на **Kotlin** рекомендуется:**

1. Использовать **null-safety** для предотвращения **NullPointerException**
2. Применять **data classes** для простых структур данных
3. Использовать **sealed classes** для ограниченных иерархий типов
4. Применять **extension functions** для расширения функциональности
5. Использовать **coroutines** для асинхронных операций
6. Следовать **Kotlin Coding Conventions** для читаемости кода

## Практические примеры использования

### Создание data class для модели данных

**Пример использования **data class** для создания модели:**

```kotlin
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int
) {
    fun isAdult(): Boolean = age >= 18

    fun getDisplayName(): String = "$name ($email)"
}

// Использование
val user = User(1, "John Doe", "john@example.com", 25)
println(user.isAdult())  // true
println(user.getDisplayName())  // "John Doe (john@example.com)"
```

**Data class** автоматически генерирует **equals**, **hashCode**, **toString** и **copy** методы.

### Использование sealed class для состояний

**Пример использования **sealed class** для представления состояний:**

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> handleResult(result: Result<T>) {
    when (result) {
        is Result.Success -> println("Success: ${result.data}")
        is Result.Error -> println("Error: ${result.message}")
        is Result.Loading -> println("Loading...")
    }
}
```

**Sealed class** обеспечивает типобезопасную обработку состояний.

### Использование extension functions для расширения функциональности

**Пример использования **extension functions**:**

```kotlin
// Расширение String
fun String.removeWhitespace(): String {
    return this.replace(" ", "")
}

// Расширение List
fun <T> List<T>.secondOrNull(): T? {
    return if (this.size >= 2) this[1] else null
}

// Расширение с generic constraints
fun <T : Comparable<T>> List<T>.isSorted(): Boolean {
    return this.zipWithNext().all { (a, b) -> a <= b }
}

// Использование
val text = "Hello World".removeWhitespace()  // "HelloWorld"
val numbers = listOf(1, 2, 3, 4, 5)
val second = numbers.secondOrNull()  // 2
val sorted = numbers.isSorted()  // true
```

**Extension functions** позволяют расширять функциональность существующих классов без наследования.

### Использование inline classes для типобезопасности

**Пример использования **inline classes**:**

```kotlin
@JvmInline
value class UserId(val value: Long)

@JvmInline
value class Email(val value: String)

data class User(val id: UserId, val email: Email, val name: String)

// Использование
val userId = UserId(123L)
val email = Email("user@example.com")
val user = User(userId, email, "John Doe")

// Компилятор предотвращает смешивание типов
// val wrong = User(email, userId, "John")  // Ошибка компиляции
```

**Inline classes** обеспечивают типобезопасность без накладных расходов на **runtime**.

### Использование делегатов для ленивой инициализации

**Пример использования делегатов:**

```kotlin
// Ленивая инициализация
class ExpensiveResource {
    val data: String by lazy {
        println("Initializing expensive resource")
        // Дорогая операция
        "Expensive data"
    }
}

// Наблюдаемое свойство
class ObservableProperty<T>(initialValue: T) {
    private var value = initialValue
    private val observers = mutableListOf<(T) -> Unit>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        val oldValue = value
        value = newValue
        if (oldValue != newValue) {
            observers.forEach { it(newValue) }
        }
    }

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
    }
}

// Использование
class User {
    var name: String by ObservableProperty("")
}

val user = User()
user.name.observe { println("Name changed to: $it") }
user.name = "John"  // Выведет: "Name changed to: John"
```

Делегаты позволяют создавать переиспользуемую логику для свойств.

### Использование контрактов для оптимизации

**Пример использования контрактов:**

```kotlin
@ExperimentalContracts
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return !isNullOrEmpty()
}

@ExperimentalContracts
fun processString(str: String?) {
    if (str.isNotNullOrEmpty()) {
        // Компилятор знает, что str не null здесь
        println(str.length)  // Безопасно
    }
}
```

Контракты помогают компилятору лучше анализировать код и оптимизировать его.

## Лучшие практики

- **Null safety:** использовать типы с `?` явно; предпочитать `val` и неизменяемые структуры; применять `?.`, `?:`, `!!` осознанно (избегать `!!` в production).
- **Идиомы `Kotlin`:** предпочитать **data class** для **DTO**; **object** для синглтонов; **sealed class** для закрытых иерархий; **extension**-функции вместо утилитных классов.
- **Коллекции:** использовать неизменяемые `listOf()`, `mapOf()`, `setOf()` по умолчанию; изменяемые — `mutableListOf()` и т.д. только при необходимости; предпочитать операции над коллекциями (filter, map) вместо циклов.
- **Корутины:** использовать структурированную конкурентность (CoroutineScope); не запускать корутины без привязки к **scope**; обрабатывать отмену и исключения (SupervisorJob, CoroutineExceptionHandler).
- **Совместимость с `Java`:** аннотировать `@JvmStatic`, `@JvmOverloads`, `@JvmField` при экспорте в **Java**; учитывать **nullability** в сигнатурах для **Java**-вызовов.
- **Производительность:** использовать **inline** для функций с лямбдами в горячих путях; **reified** только где нужна информация о типе; избегать избыточной аллокации в циклах.
