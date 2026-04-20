---
title: "Kotlin Another"
description: "Дополнительные возможности Kotlin: Ktor (асинхронные серверы и клиенты), Exposed (работа с БД), DSL, метапрограммирование, сериализация и продвинутые паттерны"
tags:
  - kotlin
  - ktor
  - exposed
  - database
  - web-server
  - dsl
  - serialization
  - metaprogramming
difficulty: "intermediate"
prerequisites: ["kotlin-basics.md", "kotlin-concurrency-basics.md"]
next: ["kotlin-advanced.md"]
updated: "2026-02-06"
---

# Kotlin Another

**Дополнительные возможности Kotlin** — это набор продвинутых инструментов и фреймворков для создания масштабируемых и эффективных приложений. В этом руководстве рассматриваются ключевые технологии: **Ktor** для веб-разработки, **Exposed** для работы с базами данных, **DSL**, сериализация и метапрограммирование.

## Полезные ссылки

### Официальная документация

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Kotlin API Reference](https://kotlinlang.org/api/latest/jvm/stdlib/)

### Обучающие материалы

- [Kotlin Tutorial](https://www.baeldung.com/kotlin)


## Содержание

- [**Kotlin Another**](#kotlin-another)
- [Руководство по **Ktor** (асинхронные серверы и клиенты)](#руководство-по-ktor-асинхронные-серверы-и-клиенты)
  - [Настройка проекта](#настройка-проекта)
  - [Создание сервера](#создание-сервера)
  - [Добавление **API** и маршрутизации](#добавление-api-и-маршрутизации)
  - [Запуск сервера](#запуск-сервера)
  - [Установка компонентов (Features)](#установка-компонентов-features)
  - [Работа с **JSON**](#работа-с-json)
  - [Создание **TODO** приложения](#создание-todo-приложения)
- [Руководство по **Kotlin Exposed Framework** (подключение к БД)](#руководство-по-kotlin-exposed-framework-подключение-к-бд)
  - [Добавление зависимостей](#добавление-зависимостей)
  - [Подключение к базе данных](#подключение-к-базе-данных)
  - [Транзакции](#транзакции)
  - [Логирование **SQL**](#логирование-sql)
  - [Определение таблиц](#определение-таблиц)
  - [**IntIdTable** и **LongIdTable**](#intidtable-и-longidtable)
  - [Внешние ключи](#внешние-ключи)
  - [Создание таблиц](#создание-таблиц)
  - [Запросы **SELECT**](#запросы-select)
  - [Проекции (slice)](#проекции-slice)
  - [Выражения **WHERE**](#выражения-where)
  - [Уточнение запросов](#уточнение-запросов)
  - [Сортировка](#сортировка)
  - [Группировка](#группировка)
  - [Соединения (Joins)](#соединения-joins)
  - [Вставка данных (INSERT)](#вставка-данных-insert)
  - [Обновление данных (UPDATE)](#обновление-данных-update)
  - [Удаление данных (DELETE)](#удаление-данных-delete)
  - [**DAO** (Data Access Object)](#dao-data-access-object)
- [Введение в **Ktor**](#введение-в-ktor)
  - [Ключевые особенности **Ktor**:](#ключевые-особенности-ktor)
  - [Архитектура **Ktor**:](#архитектура-ktor)
- [Настройка проекта **Ktor**](#настройка-проекта-ktor)
  - [Создание нового проекта **Ktor**:](#создание-нового-проекта-ktor)
- [Используя Ktor CLI](#используя-ktor-cli)
- [Или вручную создать структуру](#или-вручную-создать-структуру)
  - [**build.gradle.kts** для **Ktor**:](#buildgradlekts-для-ktor)
  - [Основная структура приложения:](#основная-структура-приложения)
- [Маршрутизация и обработка запросов](#маршрутизация-и-обработка-запросов)
  - [Базовая маршрутизация:](#базовая-маршрутизация)
  - [Расширенная маршрутизация с параметрами:](#расширенная-маршрутизация-с-параметрами)
  - [**HTTP** методы и статусы:](#http-методы-и-статусы)
- [Работа с **JSON Ktor**](#работа-с-json-ktor)
  - [Настройка сериализации:](#настройка-сериализации)
  - [Обработка **JSON** запросов и ответов:](#обработка-json-запросов-и-ответов)
  - [Кастомная сериализация:](#кастомная-сериализация)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
  - [**Basic Authentication**:](#basic-authentication)
  - [**JWT Authentication**:](#jwt-authentication)
  - [**Role-based Authorization**:](#role-based-authorization)
- [**WebSockets**](#websockets)
  - [Настройка **WebSocket** поддержки:](#настройка-websocket-поддержки)
  - [**WebSocket** чат сервер:](#websocket-чат-сервер)
  - [**Typed WebSocket frames**:](#typed-websocket-frames)
- [Тестирование **Ktor** приложений](#тестирование-ktor-приложений)
  - [Настройка тестов:](#настройка-тестов)
  - [Тестирование маршрутов:](#тестирование-маршрутов)
  - [Тестирование **WebSockets**:](#тестирование-websockets)
- [Развертывание и конфигурация **Ktor**](#развертывание-и-конфигурация-ktor)
  - [Конфигурационные файлы:](#конфигурационные-файлы)
  - [**Docker** для **Ktor**:](#docker-для-ktor)
  - [Производственная конфигурация:](#производственная-конфигурация)
- [Введение в **Exposed**](#введение-в-exposed)
  - [Преимущества **Exposed**:](#преимущества-exposed)
  - [Архитектура **Exposed**:](#архитектура-exposed)
- [Определение схемы данных](#определение-схемы-данных)
  - [Создание таблиц с типами данных:](#создание-таблиц-с-типами-данных)
  - [Индексы и ограничения:](#индексы-и-ограничения)
  - [Перечисления и кастомные типы:](#перечисления-и-кастомные-типы)
- [**CRUD** операции](#crud-операции)
  - [Чтение данных (Select):](#чтение-данных-select)
- [Отношения и связи](#отношения-и-связи)
  - [**One-to-Many** отношения:](#one-to-many-отношения)
  - [**Many-to-Many** отношения:](#many-to-many-отношения)
  - [**Self-referencing** отношения:](#self-referencing-отношения)
- [Транзакции и управление](#транзакции-и-управление)
  - [Уровни изоляции:](#уровни-изоляции)
  - [Вложенные транзакции:](#вложенные-транзакции)
  - [**Connection pooling** и управление соединениями:](#connection-pooling-и-управление-соединениями)
- [Миграции и эволюция схемы](#миграции-и-эволюция-схемы)
  - [**Exposed** миграции:](#exposed-миграции)
  - [**Flyway** интеграция:](#flyway-интеграция)
- [**DAO** паттерн](#dao-паттерн)
  - [**Entity** классы:](#entity-классы)
  - [**CRUD** с **DAO**:](#crud-с-dao)
  - [Преобразование в **DTO**:](#преобразование-в-dto)
  - [Кастомные запросы с **DAO**:](#кастомные-запросы-с-dao)
- [Создание **DSL**](#создание-dsl)
  - [**Type-safe builders**](#type-safe-builders)
  - [**SQL DSL** пример:](#sql-dsl-пример)
- [**Kotlinx.serialization**](#kotlinxserialization)
  - [Базовое использование:](#базовое-использование)
  - [Конфигурация **Json**:](#конфигурация-json)
  - [Полиморфная сериализация:](#полиморфная-сериализация)
- [**Metaprogramming** в **Kotlin**](#metaprogramming-в-kotlin)
  - [**Inline** функции:](#inline-функции)
  - [**Reified generics**:](#reified-generics)
  - [Операторные функции:](#операторные-функции)
  - [Свойства-делегаты:](#свойства-делегаты)
- [Продвинутые паттерны **Kotlin**](#продвинутые-паттерны-kotlin)
  - [**Sealed** классы и иерархии:](#sealed-классы-и-иерархии)
  - [Функциональное программирование:](#функциональное-программирование)
  - [**DSL** для конфигурации:](#dsl-для-конфигурации)
  - [Типобезопасные билдеры:](#типобезопасные-билдеры)
- [Продвинутые техники работы с **Ktor**](#продвинутые-техники-работы-с-ktor)
  - [**Middleware** и **Interceptors**](#middleware-и-interceptors)
  - [Валидация запросов](#валидация-запросов)
- [Продвинутые техники работы с **Exposed**](#продвинутые-техники-работы-с-exposed)
  - [**Raw SQL** запросы](#raw-sql-запросы)
  - [Пакетная обработка](#пакетная-обработка)
- [Расширенные возможности **DSL**](#расширенные-возможности-dsl)
  - [**DSL** с валидацией и обработкой ошибок](#dsl-с-валидацией-и-обработкой-ошибок)
  - [**DSL** с контекстом выполнения](#dsl-с-контекстом-выполнения)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Профилирование **Ktor** приложений](#профилирование-ktor-приложений)
  - [Оптимизация запросов к базе данных](#оптимизация-запросов-к-базе-данных)
- [Лучшие практики](#лучшие-практики)
  - [Обработка ошибок](#обработка-ошибок)
  - [Безопасность](#безопасность)
- [Продвинутые техники сериализации](#продвинутые-техники-сериализации)
  - [Сериализация с трансформациями](#сериализация-с-трансформациями)
- [Продвинутые техники работы с базами данных](#продвинутые-техники-работы-с-базами-данных)
  - [Оптимизация запросов **Exposed**](#оптимизация-запросов-exposed)
  - [Работа с транзакциями](#работа-с-транзакциями)
- [Интеграция с внешними системами](#интеграция-с-внешними-системами)
  - [Работа с **API**](#работа-с-api)
  - [Работа с микросервисами](#работа-с-микросервисами)
  - [Работа с различными форматами](#работа-с-различными-форматами)
  - [Кастомная сериализация для сложных типов](#кастомная-сериализация-для-сложных-типов)
- [Продвинутые техники метапрограммирования](#продвинутые-техники-метапрограммирования)
  - [Генерация кода с использованием **KSP**](#генерация-кода-с-использованием-ksp)
  - [Генерация кода во время компиляции](#генерация-кода-во-время-компиляции)
  - [Работа с **REST API**](#работа-с-rest-api)
  - [Работа с **GraphQL**](#работа-с-graphql)
- [Продвинутые паттерны проектирования](#продвинутые-паттерны-проектирования)
  - [**Factory Pattern**](#factory-pattern)
  - [**Builder Pattern**](#builder-pattern)
- [Дополнительные паттерны и техники](#дополнительные-паттерны-и-техники)
  - [**Singleton Pattern**](#singleton-pattern)
  - [**Adapter Pattern**](#adapter-pattern)
  - [**Facade Pattern**](#facade-pattern)
- [Дополнительные паттерны](#дополнительные-паттерны)
  - [**Observer Pattern**](#observer-pattern)
  - [**Command Pattern**](#command-pattern)
  - [**Template Method Pattern**](#template-method-pattern)
  - [**Visitor Pattern**](#visitor-pattern)
  - [**Chain of Responsibility Pattern**](#chain-of-responsibility-pattern)
  - [**State Pattern**](#state-pattern)
- [Решение проблем](#решение-проблем)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Реализация паттерна **Strategy**](#реализация-паттерна-strategy)
  - [Реализация паттерна **Decorator**](#реализация-паттерна-decorator)
  - [Реализация паттерна **Observer**](#реализация-паттерна-observer)
  - [Реализация паттерна **Command**](#реализация-паттерна-command)

## Руководство по Ktor (асинхронные серверы и клиенты)

**Ktor** — это платформа для создания асинхронных серверов и клиентов в подключённых системах с использованием мощного языка программирования **Kotlin**. Это облегчает разработку автономного приложения со встроенными серверами.

### Настройка проекта

Начнём с настройки проекта **Ktor**. Мы будем использовать **Gradle**, который является рекомендуемым и простым в использовании подходом. **Gradle** можно установить, следуя инструкциям на сайте **Gradle**.

Создайте файл **build.gradle:**

```groovy
group 'com.baeldung.kotlin'
version '1.0-SNAPSHOT'

buildscript {
    ext.kotlin_version = '1.2.40'
    ext.ktor_version = '0.9.2'
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'
apply plugin: 'application'

mainClassName = 'APIServer.kt'

sourceCompatibility = 1.8

compileKotlin { kotlinOptions.jvmTarget = "1.8" }
compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

kotlin { experimental { coroutines "enable" } }

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile "io.ktor:ktor-server-netty:$ktor_version"
    compile "ch.qos.logback:logback-classic:1.2.1"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

Мы импортировали **Ktor** и пакет **Ktor netty server. `Netty` -** это встроенный сервер, который мы будем использовать в этом примере**.

### Создание сервера

Мы создаем наше приложение, добавляя код в исходную папку **src/main/kotlin.**

**Здесь мы создаем файл **APIServer.kt** с помощью основного метода:**

```kotlin
fun main(args: Array<String>) {}
```

Далее создаем и запускаем встроенный сервер **Netty:**

```kotlin
embeddedServer(Netty, 8080) {}.start(wait = true)
```

Он создаст и запустит сервер на порту **8080**. Мы установили **wait=true** в методе **start()** для прослушивания подключений.

### Добавление API и маршрутизации

Добавим **API**. Для обработки **HTTP-**запросов **Ktor** предоставляет функцию маршрутизации.

Мы активируем функцию маршрутизации с помощью блока установки, где мы можем определить маршруты для определенных путей и методов **HTTP:**

```kotlin
val jsonResponse = """ {
    "id": 1,
    "task": "Pay waterbill",
    "description": "Pay water bill today",
}"""

embeddedServer(Netty, 8080) {
    install(Routing) {
        get("/todo") {
            call.respondText(jsonResponse, ContentType.Application.Json)
        }
    }
}.start(wait = true)
```

В этом примере сервер обработает запрос **GET** для пути **/todo** и ответит объектом **JSON todo**. Мы узнаем больше об установке компонентов в разделе Установка компонентов.

### Запуск сервера

Для запуска сервера нам нужна задача запуска в **Gradle:**

```groovy
task runServer(type: JavaExec) {
    main = 'APIServer'
    classpath = sourceSets.main.runtimeClasspath
}
```

**Чтобы запустить сервер, мы вызываем эту задачу:**

```bash
./gradlew runServer
```

Затем к нашему **API** можно получить доступ через **http://localhost:8080/todo.**

### Установка компонентов (Features)

Приложение **Ktor** обычно состоит из ряда функций. Мы могли бы думать о функциях как о функциях, внедряемых в конвейер запросов и ответов.

Используя функцию **DefaultHeaders**, мы можем добавлять заголовки к каждому исходящему ответу. Маршрутизация — это ещё одна функция, которая позволяет нам определять маршруты для обработки запросов и т. д.

Мы также можем разработать наши функции и установить их.

**Давайте посмотрим, добавив собственный заголовок к каждому запросу, установив функцию **DefaultHeaders**:**

```kotlin
install(DefaultHeaders) {
    header("X-Developer", "Baeldung")
}
```

Точно так же мы можем переопределить заголовки по умолчанию, установленные самой инфраструктурой **Ktor:**

```kotlin
install(DefaultHeaders) {
    header(HttpHeaders.Server, "My Server")
}
```

Список доступных заголовков по умолчанию можно найти в классе **io.`ktor.features`.`DefaultHeaders`.**

### Работа с JSON

Строить строковый **JSON** вручную непросто. **Ktor** предоставляет функцию для обслуживания объектов данных в формате **JSON** с использованием **Gson**.

Давайте добавим зависимость **Gson** в наш **build.gradle:**

```groovy
compile "io.ktor:ktor-gson:$ktor_version"
```

Например, мы используем объект данных с именем **Author:**

```kotlin
data class Author(val name: String, val website: String)
```

Затем мы устанавливаем функцию **gson:**

```kotlin
install(ContentNegotiation) {
    gson {
        setPrettyPrinting()
    }
}
```

Наконец, давайте добавим маршрут к серверу, который обслуживает объект автора как **JSON:**

```kotlin
get("/author") {
    val author = Author("baeldung", "baeldung.com")
    call.respond(author)
}
```

**API** автора будет обслуживать объект данных автора как **JSON**.

### Создание TODO приложения

Чтобы понять, как обрабатывать несколько **HTTP-**запросов действий, давайте создадим приложение **TODO**, которое позволяет пользователю добавлять, удалять, просматривать и перечислять элементы **TODO**.

Мы начнем с добавления класса данных **Todo:**

```kotlin
data class ToDo(var id: Int, val name: String, val description: String, val completed: Boolean)
```

Затем мы создаем **ArrayList** для хранения нескольких элементов **Todo:**

```kotlin
val toDoList = ArrayList<ToDo>()
```

Затем мы добавляем контроллеры для обработки запросов **POST, DELETE** и **GET:**

```kotlin
routing() {
    route("/todo") {
        post {
            var toDo = call.receive<ToDo>()
            toDo.id = toDoList.size
            toDoList.add(toDo)
            call.respond("Added")
        }

        delete("/{id}") {
            call.respond(toDoList.removeAt(call.parameters["id"]!!.toInt()))
        }

        get("/{id}") {
            call.respond(toDoList[call.parameters["id"]!!.toInt()])
        }

        get {
            call.respond(toDoList)
        }
    }
}
```

Мы добавили маршрут **todo**, а затем сопоставили различные **HTTP-**запросы глаголов с этой конечной точкой.

## Руководство по Kotlin Exposed Framework (подключение к БД)

В этом руководстве мы рассмотрим, как запрашивать реляционную базу данных с помощью **Exposed.**

**Exposed** — это библиотека с открытым исходным кодом **(**лицензия **Apache**)**, разработанная **JetBrains**, которая предоставляет идиоматический **API-**интерфейс **Kotlin** для некоторых реализаций реляционных баз данных, сглаживая различия между поставщиками баз данных.

**Exposed** можно использовать как в качестве высокоуровневого **DSL** поверх **SQL**, так и в качестве облегченного **ORM** (объектно-реляционного сопоставления). Таким образом, в ходе этого урока мы рассмотрим оба варианта использования.

### Добавление зависимостей

Добавим необходимые зависимости **Maven:**

```xml
<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-core</artifactId>
    <version>0.37.3</version>
</dependency>

<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-dao</artifactId>
    <version>0.37.3</version>
</dependency>

<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-jdbc</artifactId>
    <version>0.37.3</version>
</dependency>
```

**Кроме того, в следующих разделах мы покажем примеры использования базы данных **H2** в памяти:**

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.210</version>
</dependency>
```

### Подключение к базе данных

Мы определяем соединения с базой данных с помощью класса **Database:**

```kotlin
Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
```

**Мы также можем указать пользователя и пароль в качестве именованных параметров:**

```kotlin
Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "myself", password = "secret")
```

Обратите внимание, что вызов **connect** не устанавливает соединение с БД сразу**. Он просто сохраняет параметры подключения на потом**.

**Если нам нужно предоставить другие параметры подключения, мы будем использовать другую перегрузку метода подключения, которая дает нам полный контроль над получением подключения к базе данных:**

```kotlin
Database.connect({ DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL") })
```

Для этой версии подключения требуется параметр закрытия**. Exposed** вызывает закрытие всякий раз, когда ему требуется новое подключение к базе данных**.

**Если вместо этого мы подключаемся к базе данных с помощью **DataSource,** как это обычно бывает в корпоративных приложениях (например, чтобы получить выгоду от пула соединений),** мы можем использовать соответствующую перегрузку соединения:**

```kotlin
Database.connect(datasource)
```

### Транзакции

Для каждой операции с базой данных в **Exposed** требуется активная транзакция**.

**Метод транзакции принимает замыкание и вызывает его с активной транзакцией:**

```kotlin
transaction {
    // операции с базой данных
}
```

Транзакция возвращает то, что возвращает замыкание**. Затем **Exposed** автоматически закрывает транзакцию, когда выполнение блока завершается**.

Когда блок транзакции успешно возвращается**, Exposed** фиксирует транзакцию**. Когда вместо этого замыкание завершается, вызывая исключение, платформа откатывает транзакцию**.

Мы также можем вручную зафиксировать или откатить транзакцию. Закрытие, которое мы предоставляем транзакции, на самом деле является экземпляром класса **Transaction** благодаря магии **Kotlin**.

**Таким образом, у нас есть коммит и метод отката:**

```kotlin
transaction {
    commit()
}

transaction {
    rollback()
}
```

### Логирование SQL

При изучении фреймворка или отладке может оказаться полезным проверить операторы **SQL** и запросы, которые **Exposed** отправляет в базу данных**.

**Мы можем легко добавить такой логгер в активную транзакцию:**

```kotlin
transaction {
    addLogger(StdOutSqlLogger)
    // операции с базой данных
}
```

### Определение таблиц

Обычно в **Exposed** мы не работаем с необработанными строками и именами **SQL.** Вместо этого мы определяем таблицы, столбцы, ключи, отношения и т. д., используя **DSL** высокого уровня.

Мы представляем каждую таблицу экземпляром класса **Table:**

```kotlin
object StarWarsFilms: Table()
```

**Exposed** автоматически вычисляет имя таблицы из имени класса, но мы также можем указать явное имя:**

```kotlin
object StarWarsFilms: Table("STAR_WARS_FILMS")
```

**Таблица бессмысленна без столбцов**. Мы определяем столбцы как свойства нашего класса таблицы:**

```kotlin
object StarWarsFilms_Simple: Table() {
    val id = integer("id").autoIncrement()
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_StarWarsFilms_Id")
}
```

Как видно из примера в предыдущем разделе, мы можем легко определить индексы и первичные ключи с помощью гибкого **API**.

### IntIdTable и LongIdTable

**Однако для общего случая таблицы с целочисленным первичным ключом **Exposed** предоставляет классы **IntIdTable** и **LongIdTable**, которые определяют для нас ключ:**

```kotlin
object StarWarsFilms: IntIdTable() {
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
}
```

Также есть **UUIDTable**; кроме того, мы можем определить наши собственные варианты путём создания подкласса **IdTable**.

### Внешние ключи

Внешние ключи легко ввести. Мы также выигрываем от статической типизации, потому что мы всегда ссылаемся на свойства, известные во время компиляции.

**Предположим, мы хотим отслеживать имена актеров, играющих в каждом фильме:**

```kotlin
object Players: Table() {
    val sequelId = integer("sequel_id")
        .uniqueIndex()
        .references(StarWarsFilms.sequelId)
    val name = varchar("name", 50)
}
```

**Чтобы не указывать тип столбца по буквам **(**в данном случае **integer**),** когда он может быть получен из столбца, на который указывает ссылка, мы можем использовать ссылочный метод в качестве сокращения:**

```kotlin
val sequelId = reference("sequel_id", StarWarsFilms.sequelId).uniqueIndex()
```

**Если ссылка на первичный ключ, мы можем опустить имя столбца:**

```kotlin
val filmId = reference("film_id", StarWarsFilms)
```

### Создание таблиц

**Мы можем создать таблицы, как определено выше, программно:**

```kotlin
transaction {
    SchemaUtils.create(StarWarsFilms, Players)
}
```

Таблицы создаются только в том случае, если они еще не существуют**. Однако нет поддержки миграции баз данных**.

### Запросы SELECT

Как только мы определили некоторые классы таблиц, как показано в предыдущих разделах, мы можем выполнять запросы к базе данных, используя функции расширения, предоставляемые платформой**.

**Для извлечения данных из базы данных мы используем объекты **Query,** построенные из классов таблиц**. Самый простой запрос — это тот, который возвращает все строки данной таблицы:**

```kotlin
val query = StarWarsFilms.selectAll()
```

Запрос является итерируемым, поэтому он поддерживает **forEach:**

```kotlin
query.forEach {
    assertTrue { it[StarWarsFilms.sequelId] >= 7 }
}
```

Параметр замыкания, неявно названный им в приведенном выше примере, является экземпляром класса **ResultRow.** Мы можем видеть это как карту с ключом по столбцу.

### Проекции (slice)

**Мы также можем выбрать подмножество столбцов таблицы, т. е. выполнить проекцию, используя метод среза:**

```kotlin
StarWarsFilms.slice(StarWarsFilms.name, StarWarsFilms.director)
    .selectAll()
    .forEach {
        assertTrue { it[StarWarsFilms.name].startsWith("The") }
    }
```

**Мы также используем **slice** для применения функции к столбцу:**

```kotlin
StarWarsFilms.slice(StarWarsFilms.name.countDistinct())
```

Часто при использовании агрегатных функций, таких как **count** и **avg,** нам потребуется предложение **group by** в запросе**. Мы поговорим о группе позже**.

### Выражения WHERE

**Exposed** содержит выделенный **DSL** для выражений **where,** которые используются для фильтрации запросов и других типов операторов**. Это мини-язык, основанный на свойствах столбцов, с которыми мы уже познакомились, и наборе логических операторов**.

**Это выражение где:**

```kotlin
{ (StarWarsFilms.director like "J.J.%") and (StarWarsFilms.sequelId eq 7) }
```

Его тип сложный; это подкласс **SqlExpressionBuilder**, который определяет такие операторы, как **like**, **eq** и др. Как мы видим, это последовательность сравнений, объединённых операторами и и или.

**Мы можем передать такое выражение в метод **select,** который снова вернет запрос:**

```kotlin
val select = StarWarsFilms.select {
    (StarWarsFilms.director like "J.J.%") and (StarWarsFilms.sequelId eq 7)
}
assertEquals(1, select.count())
```

Благодаря выводу типа нам не нужно указывать сложный тип выражения **where,** когда оно напрямую передается методу **select,** как в приведенном выше примере**.

**Поскольку выражения являются объектами **Kotlin,** для параметров запроса нет специальных положений**. Мы просто используем переменные:**

```kotlin
val sequelNo = 7
StarWarsFilms.select { StarWarsFilms.sequelId >= sequelNo }
```

### Уточнение запросов

Объекты **Query,** возвращаемые **select** и его вариантами, имеют ряд методов, которые мы можем использовать для уточнения запроса**.

**Например, мы можем захотеть исключить повторяющиеся строки:**

```kotlin
query.withDistinct(true).forEach { ... }
```

**Или мы можем захотеть вернуть только подмножество строк, например, при разбиении на страницы результатов для пользовательского интерфейса:**

```kotlin
query.limit(20, offset = 40).forEach { ... }
```

Эти методы возвращают новый **Query,** поэтому мы можем легко связать их в цепочку**.

### Сортировка

**Метод **Query.orderBy** принимает список столбцов, сопоставленных со значением **SortOrder,** указывающим, должна ли сортироваться по возрастанию или по убыванию:**

```kotlin
query.orderBy(StarWarsFilms.name to SortOrder.ASC)
```

### Группировка

В то время как группировка по одному или нескольким столбцам, полезная, в частности, при использовании агрегатных функций, достигается с помощью метода **groupBy:**

```kotlin
StarWarsFilms
    .slice(StarWarsFilms.sequelId.count(), StarWarsFilms.director)
    .selectAll()
    .groupBy(StarWarsFilms.director)
```

### Соединения (Joins)

**Соединения, возможно, являются одним из преимуществ реляционных баз данных**. В самых простых случаях, когда у нас есть внешний ключ и нет условий соединения, мы можем использовать один из встроенных операторов соединения:**

```kotlin
(StarWarsFilms innerJoin Players).selectAll()
```

Здесь мы показали **innerJoin,** но у нас также есть левое, правое и перекрестное соединение по тому же принципу**.

**Затем мы можем добавить условия соединения с выражением **where;** например, если нет внешнего ключа, и мы должны выполнить соединение явно:**

```kotlin
(StarWarsFilms innerJoin Players).select {
    StarWarsFilms.sequelId eq Players.sequelId
}
```

**В общем случае полная форма объединения выглядит следующим образом:**

```kotlin
val complexJoin = Join(
    StarWarsFilms, Players,
    onColumn = StarWarsFilms.sequelId, otherColumn = Players.sequelId,
    joinType = JoinType.INNER,
    additionalConstraint = { StarWarsFilms.sequelId eq 8 }
)

complexJoin.selectAll()
```

**Благодаря сопоставлению имен столбцов со свойствами нам не нужны никакие псевдонимы в типичном соединении, даже если столбцы имеют одно и то же имя:**

```kotlin
(StarWarsFilms innerJoin Players)
    .selectAll()
    .forEach {
        assertEquals(it[StarWarsFilms.sequelId], it[Players.sequelId])
    }
```

### Вставка данных (INSERT)

**Для вставки данных мы используем метод **insert** класса таблицы:**

```kotlin
StarWarsFilms.insert {
    it[sequelId] = 8
    it[name] = "The Last Jedi"
    it[director] = "Rian Johnson"
}
```

### Обновление данных (UPDATE)

**Для обновления данных мы используем метод **update** класса таблицы:**

```kotlin
StarWarsFilms.update({ StarWarsFilms.sequelId eq 8 }) {
    it[name] = "The Last Jedi (Updated)"
}
```

### Удаление данных (DELETE)

**Для удаления данных мы используем метод **deleteWhere** класса таблицы:**

```kotlin
StarWarsFilms.deleteWhere { StarWarsFilms.sequelId eq 8 }
```

### DAO (Data `Access` Object)

**Exposed** также предоставляет поддержку **DAO** через модуль **exposed-dao.** Это позволяет нам работать с объектами вместо строк результатов:**

```kotlin
class StarWarsFilm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StarWarsFilm>(StarWarsFilms)

    var sequelId by StarWarsFilms.sequelId
    var name by StarWarsFilms.name
    var director by StarWarsFilms.director
}
```

**Теперь мы можем работать с объектами:**

```kotlin
val film = StarWarsFilm.new {
    sequelId = 8
    name = "The Last Jedi"
    director = "Rian Johnson"
}

val allFilms = StarWarsFilm.all()
```

**Exposed** предоставляет мощный и типобезопасный способ работы с реляционными базами данных в **Kotlin**, объединяя преимущества **DSL** и **ORM** подходов**.

## Введение в Ktor

**Ktor** — это асинхронный веб-фреймворк для **Kotlin**, созданный **JetBrains**. Он предназначен для создания высокопроизводительных веб-приложений, **API** и микросервисов. Основные преимущества **Ktor**:**

### Ключевые особенности Ktor:

1. **Асинхронность**: Построен на корутинах **Kotlin** для неблокирующего I/O
2. **Модульность**: Только необходимые компоненты, минимальный размер
3. **Типобезопасность**: Полная поддержка типов **Kotlin**
4. **Расширяемость**: Плагинная архитектура для добавления функциональности
5. **Мультиплатформенность**: Работает на **JVM**, **Native** и `JS`

### Архитектура Ktor:

**Ktor** использует **pipeline** архитектуру с перехватчиками (interceptors) для обработки **HTTP** запросов. Каждый запрос проходит через серию фаз:**

- **Setup Phase**: Инициализация приложения
- **Monitoring Phase**: Логирование и метрики
- **Content Negotiation**: Обработка контента
- **Authentication**: Аутентификация пользователей
- **Routing**: Маршрутизация запросов
- **Response**: Формирование ответа

## Настройка проекта Ktor

### Создание нового проекта Ktor:

```bash
# Используя Ktor CLI
ktor new my-project --template=website

# Или вручную создать структуру
mkdir my-ktor-project
cd my-ktor-project
gradle init
```

### build.gradle.kts для Ktor:

```kotlin
plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.0")
    implementation("io.ktor:ktor-server-sessions-jvm:2.3.0")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
}
```

### Основная структура приложения:

```kotlin
package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureSecurity()
    }.start(wait = true)
}

fun Application.configureRouting() {
    // Routing configuration
}

fun Application.configureSerialization() {
    // Serialization configuration
}

fun Application.configureSecurity() {
    // Security configuration
}
```

## Маршрутизация и обработка запросов

### Базовая маршрутизация:

```kotlin
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, Ktor!")
        }

        get("/users/{id}") {
            val id = call.parameters["id"]
            call.respondText("User ID: $id")
        }

        post("/users") {
            val user = call.receive<User>()
            // Process user
            call.respond(HttpStatusCode.Created, user)
        }

        route("/api") {
            get("/health") {
                call.respond(mapOf("status" to "OK"))
            }

            route("/v1") {
                get("/version") {
                    call.respondText("API v1.0")
                }
            }
        }
    }
}
```

### Расширенная маршрутизация с параметрами:

```kotlin
fun Application.configureRouting() {
    routing {
        // Query parameters
        get("/search") {
            val query = call.request.queryParameters["q"]
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            call.respondText("Searching for: $query, limit: $limit")
        }

        // Path parameters with validation
        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                return@get
            }

            // Find user by ID
            call.respondText("User with ID: $id")
        }

        // Optional parameters
        get("/posts/{slug?}") {
            val slug = call.parameters["slug"]

            if (slug != null) {
                call.respondText("Post: $slug")
            } else {
                call.respondText("All posts")
            }
        }

        // Wildcard routes
        get("/files/*") {
            val path = call.parameters.getAll("path")?.joinToString("/")
            call.respondText("File path: $path")
        }
    }
}
```

### HTTP методы и статусы:

```kotlin
fun Application.configureRouting() {
    routing {
        // Standard HTTP methods
        get("/resource") { /* GET request */ }
        post("/resource") { /* POST request */ }
        put("/resource/{id}") { /* PUT request */ }
        patch("/resource/{id}") { /* PATCH request */ }
        delete("/resource/{id}") { /* DELETE request */ }
        head("/resource") { /* HEAD request */ }
        options("/resource") { /* OPTIONS request */ }

        // Custom status codes
        post("/async-operation") {
            // Start async operation
            call.respond(HttpStatusCode.Accepted, mapOf("jobId" to "123"))
        }

        get("/not-found") {
            call.respond(HttpStatusCode.NotFound, "Resource not found")
        }

        post("/validation-error") {
            call.respond(HttpStatusCode.BadRequest, mapOf(
                "error" to "Validation failed",
                "details" to listOf("Name is required", "Email format invalid")
            ))
        }
    }
}
```

## Работа с JSON Ktor

### Настройка сериализации:

```kotlin
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val roles: List<String> = emptyList()
)

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}
```

### Обработка JSON запросов и ответов:

```kotlin
fun Application.configureRouting() {
    routing {
        // Receive JSON
        post("/users") {
            try {
                val user = call.receive<User>()

                // Validate user
                if (user.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Name is required"))
                    return@post
                }

                // Save user (simulated)
                val savedUser = user.copy(id = 123)

                call.respond(HttpStatusCode.Created, savedUser)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON"))
            }
        }

        // Send JSON
        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }

            // Find user (simulated)
            val user = User(
                id = id,
                name = "John Doe",
                email = "john@example.com",
                roles = listOf("USER", "ADMIN")
            )

            call.respond(user)
        }

        // List with pagination
        get("/users") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

            val users = listOf(
                User(1, "Alice", "alice@example.com"),
                User(2, "Bob", "bob@example.com"),
                User(3, "Charlie", "charlie@example.com")
            )

            val response = mapOf(
                "data" to users,
                "pagination" to mapOf(
                    "page" to page,
                    "size" to size,
                    "total" to users.size
                )
            )

            call.respond(response)
        }
    }
}
```

### Кастомная сериализация:

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(with = LocalDateSerializer::class)
data class Event(
    val id: Int,
    val title: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}
```

## Аутентификация и авторизация

### Basic Authentication:

```kotlin
import io.ktor.server.auth.*
import io.ktor.server.plugins.*

fun Application.configureSecurity() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    install(Authorization) {
        // Authorization configuration
    }
}

fun Application.configureRouting() {
    routing {
        authenticate("auth-basic") {
            get("/protected") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello, ${principal.name}!")
            }
        }

        get("/public") {
            call.respondText("This is public")
        }
    }
}
```

### JWT Authentication:

```kotlin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*

data class UserSession(val userId: String, val roles: List<String>)

fun Application.configureSecurity() {
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256("secret"))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun Application.configureRouting() {
    routing {
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()

            // Validate credentials (simplified)
            if (loginRequest.username == "admin" && loginRequest.password == "password") {
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .withClaim("username", loginRequest.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .sign(Algorithm.HMAC256("secret"))

                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                val username = principal.payload.getClaim("username").asString()

                call.respond(mapOf("username" to username))
            }
        }
    }
}
```

### Role-based Authorization:

```kotlin
data class UserPrincipal(
    val userId: String,
    val roles: List<String>
) : Principal

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            // JWT configuration
            validate { credential ->
                val roles = credential.payload.getClaim("roles")?.asList(String::class.java) ?: emptyList()
                UserPrincipal(credential.payload.subject, roles)
            }
        }
    }
}

fun Application.configureRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/admin") {
                val principal = call.principal<UserPrincipal>()!!

                if (!principal.roles.contains("ADMIN")) {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@get
                }

                call.respondText("Admin panel")
            }

            get("/user") {
                val principal = call.principal<UserPrincipal>()!!
                call.respondText("Welcome, ${principal.userId}!")
            }
        }
    }
}
```

## WebSockets

### Настройка WebSocket поддержки:

```kotlin
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}
```

### WebSocket чат сервер:

```kotlin
class ChatServer {
    private val connections = Collections.synchronizedSet<DefaultWebSocketServerSession>(LinkedHashSet())

    suspend fun handleSession(session: DefaultWebSocketServerSession) {
        connections += session

        try {
            session.send("Welcome to the chat!")

            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        broadcast("User: $text")
                    }
                    is Frame.Close -> {
                        // Handle close
                    }
                    else -> {
                        // Handle other frame types
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
        } finally {
            connections -= session
        }
    }

    private suspend fun broadcast(message: String) {
        connections.forEach { session ->
            try {
                session.send(message)
            } catch (e: Exception) {
                // Handle send failure
            }
        }
    }
}

fun Application.configureRouting() {
    val chatServer = ChatServer()

    routing {
        webSocket("/chat") {
            chatServer.handleSession(this)
        }
    }
}
```

### Typed WebSocket frames:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatMessage {
    @Serializable
    data class TextMessage(val text: String) : ChatMessage()

    @Serializable
    data class UserJoined(val username: String) : ChatMessage()

    @Serializable
    data class UserLeft(val username: String) : ChatMessage()
}

class AdvancedChatServer {
    private val connections = Collections.synchronizedMap<String, DefaultWebSocketServerSession>(mutableMapOf())

    suspend fun handleSession(session: DefaultWebSocketServerSession, username: String) {
        connections[username] = session

        broadcast(ChatMessage.UserJoined(username))

        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val json = frame.readText()
                        val message = Json.decodeFromString<ChatMessage.TextMessage>(json)
                        broadcast(ChatMessage.TextMessage("$username: ${message.text}"))
                    }
                    is Frame.Close -> {
                        broadcast(ChatMessage.UserLeft(username))
                    }
                    else -> {}
                }
            }
        } finally {
            connections.remove(username)
        }
    }

    private suspend fun broadcast(message: ChatMessage) {
        val json = Json.encodeToString(message)
        connections.values.forEach { session ->
            try {
                session.send(json)
            } catch (e: Exception) {
                // Handle send failure
            }
        }
    }
}
```

## Тестирование Ktor приложений

### Настройка тестов:

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
}
```

### Тестирование маршрутов:

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }

        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Ktor!", bodyAsText())
        }
    }

    @Test
    fun testUserCreation() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        val newUser = User(name = "Test User", email = "test@example.com")

        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newUser))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val responseUser = Json.decodeFromString<User>(bodyAsText())
            assertEquals("Test User", responseUser.name)
        }
    }

    @Test
    fun testAuthentication() = testApplication {
        application {
            configureSecurity()
            configureRouting()
        }

        // Test without authentication
        client.get("/protected").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

        // Test with authentication (simplified)
        client.get("/protected") {
            header("Authorization", "Basic ${base64Encode("admin:password")}")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}

fun base64Encode(input: String): String {
    return java.util.Base64.getEncoder().encodeToString(input.toByteArray())
}
```

### Тестирование WebSockets:

```kotlin
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*

class WebSocketTest {

    @Test
    fun testWebSocketChat() = testApplication {
        application {
            configureWebSockets()
            configureRouting()
        }

        val client = createClient {
            install(WebSockets)
        }

        client.webSocket("/chat") {
            // Send a message
            send(Frame.Text("Hello from test"))

            // Receive response
            val response = incoming.receive() as Frame.Text
            assertEquals("User: Hello from test", response.readText())
        }
    }
}
```

## Развертывание и конфигурация Ktor

### Конфигурационные файлы:

```kotlin
// application.conf (HOCON format)
ktor {
    deployment {
        port = 8080
        host = "0.0.0.0"
        watch = [ "classes", "resources" ]
    }

    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}

// или application.yaml
ktor:
  deployment:
    port: 8080
    host: "0.0.0.0"
  application:
    modules:
      - com.example.ApplicationKt.module
```

### Docker для Ktor:

```dockerfile
FROM openjdk:11-jre-slim

EXPOSE 8080

COPY build/libs/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Производственная конфигурация:

```kotlin
fun Application.module() {
    configureRouting()
    configureSerialization()
    configureSecurity()

    if (isDev) {
        configureDevFeatures()
    } else {
        configureProdFeatures()
    }
}

val isDev: Boolean by lazy {
    System.getenv("KTOR_ENV") == "dev"
}

fun Application.configureDevFeatures() {
    install(CallLogging)
    install(CORS) {
        anyHost()
    }
}

fun Application.configureProdFeatures() {
    install(HTTPSRedirect)
    install(HSTS)
    install(Compression) {
        gzip()
    }
}
```

## Введение в Exposed

**Exposed** — это легковесная **SQL** библиотека для **Kotlin**, которая предоставляет типобезопасный **DSL** для работы с реляционными базами данных. Основные преимущества:**

### Преимущества Exposed:

1. **Типобезопасность**: Компилятор **Kotlin** проверяет корректность запросов
2. **DSL синтаксис**: Читаемый и выразительный код запросов
3. **Поддержка транзакций**: Автоматическое управление транзакциями
4. **DAO поддержка**: Объектно-реляционное отображение
5. **Миграции**: Инструменты для управления схемой БД

### Архитектура Exposed:

**Exposed** состоит из нескольких модулей:**

- **exposed-core**: Основной **DSL** и **API**
- **exposed-dao**: **DAO** (Data `Access` Object) паттерн
- **exposed-jdbc**: **JDBC** интеграция
- **exposed-java-time**: Поддержка **java.time** типов

## Определение схемы данных

### Создание таблиц с типами данных:

```kotlin
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 100).uniqueIndex()
    val age = integer("age").nullable()
    val balance = decimal("balance", 10, 2).default(0.0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id, name = "PK_Users_Id")
}

object Posts : Table("posts") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val content = text("content")
    val authorId = integer("author_id").references(Users.id)
    val publishedAt = datetime("published_at").nullable()
    val tags = jsonb("tags", Json.encodeToJsonElement(emptyList<String>()))

    override val primaryKey = PrimaryKey(id)
}
```

### Индексы и ограничения:

```kotlin
object Products : Table("products") {
    val id = integer("id").autoIncrement()
    val sku = varchar("sku", 20).uniqueIndex("idx_products_sku")
    val name = varchar("name", 100)
    val price = decimal("price", 8, 2)
    val categoryId = integer("category_id")

    init {
        // Составной индекс
        index("idx_products_category_price", categoryId, price)

        // Проверка ограничений
        check { price greater 0 }
    }

    override val primaryKey = PrimaryKey(id)
}
```

### Перечисления и кастомные типы:

```kotlin
enum class UserRole {
    ADMIN, MODERATOR, USER
}

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

object Orders : Table("orders") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val status = enumeration<OrderStatus>("status").default(OrderStatus.PENDING)
    val totalAmount = decimal("total_amount", 10, 2)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}

object UserRoles : Table("user_roles") {
    val userId = integer("user_id").references(Users.id)
    val role = enumeration<UserRole>("role")

    override val primaryKey = PrimaryKey(userId, role)
}
```

## CRUD операции

### Вставка данных (Insert):

```kotlin
fun createUser(name: String, email: String, age: Int? = null): Int {
    return transaction {
        Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            if (age != null) it[Users.age] = age
        } get Users.id
    }
}

fun createPost(title: String, content: String, authorId: Int): Int {
    return transaction {
        Posts.insert {
            it[Posts.title] = title
            it[Posts.content] = content
            it[Posts.authorId] = authorId
        } get Posts.id
    }
}

fun batchInsertUsers(users: List<UserData>) {
    transaction {
        Users.batchInsert(users) { user ->
            this[Users.name] = user.name
            this[Users.email] = user.email
            this[Users.age] = user.age
        }
    }
}
```

### Чтение данных (Select):

```kotlin
fun getUserById(id: Int): UserData? {
    return transaction {
        Users.select { Users.id eq id }
            .singleOrNull()
            ?.let { row ->
                UserData(
                    id = row[Users.id],
                    name = row[Users.name],
                    email = row[Users.email],
                    age = row[Users.age]
                )
            }
    }
}

fun getUsersByAgeRange(minAge: Int, maxAge: Int): List<UserData> {
    return transaction {
        Users.select { Users.age.between(minAge, maxAge) }
            .map { row ->
                UserData(
                    id = row[Users.id],
                    name = row[Users.name],
                    email = row[Users.email],
                    age = row[Users.age]
                )
            }
    }
}

fun getUsersWithPosts(): List<UserWithPosts> {
    return transaction {
        (Users innerJoin Posts)
            .selectAll()
            .groupBy { it[Users.id] }
            .map { (userId, rows) ->
                val firstRow = rows.first()
                UserWithPosts(
                    user = UserData(
                        id = firstRow[Users.id],
                        name = firstRow[Users.name],
                        email = firstRow[Users.email],
                        age = firstRow[Users.age]
                    ),
                    posts = rows.map { row ->
                        PostData(
                            id = row[Posts.id],
                            title = row[Posts.title],
                            content = row[Posts.content]
                        )
                    }
                )
            }
    }
}
```

### Обновление данных (Update):

```kotlin
fun updateUser(id: Int, name: String? = null, email: String? = null, age: Int? = null): Boolean {
    return transaction {
        val updatedRows = Users.update({ Users.id eq id }) { user ->
            name?.let { user[Users.name] = it }
            email?.let { user[Users.email] = it }
            age?.let { user[Users.age] = it }
        }
        updatedRows > 0
    }
}

fun incrementUserBalance(userId: Int, amount: BigDecimal) {
    transaction {
        Users.update({ Users.id eq userId }) { user ->
            with(SqlExpressionBuilder) {
                user[Users.balance] = Users.balance + amount
            }
        }
    }
}

fun activateUsersWithoutPosts() {
    transaction {
        val usersWithoutPosts = Users.select { Users.id notInSubQuery Posts.slice(Posts.authorId).selectAll() }

        Users.update({ Users.id inList usersWithoutPosts.map { it[Users.id] } }) { user ->
            user[Users.isActive] = true
        }
    }
}
```

### Удаление данных (Delete):

```kotlin
fun deleteUser(id: Int): Boolean {
    return transaction {
        val deletedRows = Users.deleteWhere { Users.id eq id }
        deletedRows > 0
    }
}

fun deleteOldPosts(daysOld: Int) {
    transaction {
        val cutoffDate = LocalDateTime.now().minusDays(daysOld.toLong())

        Posts.deleteWhere {
            Posts.publishedAt less cutoffDate.toNullable()
        }
    }
}

fun cleanupInactiveUsers() {
    transaction {
        // Удалить пользователей без постов и неактивных более 30 дней
        val inactiveUsers = Users.select {
            (Users.isActive eq false) and
            (Users.createdAt less LocalDateTime.now().minusDays(30))
        }.adjustSlice { slice(this[Users.id]) }

        Users.deleteWhere { Users.id inSubQuery inactiveUsers }
    }
}
```

## Отношения и связи

### One-to-Many отношения:

```kotlin
object Categories : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val price = decimal("price", 8, 2)
    val categoryId = integer("category_id").references(Categories.id)
    override val primaryKey = PrimaryKey(id)
}

fun getCategoriesWithProducts(): List<CategoryWithProducts> {
    return transaction {
        (Categories leftJoin Products)
            .selectAll()
            .orderBy(Categories.name, SortOrder.ASC)
            .groupBy { it[Categories.id] }
            .map { (categoryId, rows) ->
                val firstRow = rows.first()
                CategoryWithProducts(
                    category = CategoryData(
                        id = firstRow[Categories.id],
                        name = firstRow[Categories.name]
                    ),
                    products = rows
                        .filter { it[Products.id] != null }
                        .map { row ->
                            ProductData(
                                id = row[Products.id],
                                name = row[Products.name],
                                price = row[Products.price]
                            )
                        }
                )
            }
    }
}
```

### Many-to-Many отношения:

```kotlin
object Tags : Table("tags") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object PostTags : Table("post_tags") {
    val postId = integer("post_id").references(Posts.id)
    val tagId = integer("tag_id").references(Tags.id)
    override val primaryKey = PrimaryKey(postId, tagId)
}

fun addTagsToPost(postId: Int, tagNames: List<String>) {
    transaction {
        // Найти или создать теги
        val tagIds = tagNames.map { tagName ->
            Tags.select { Tags.name eq tagName }
                .singleOrNull()?.get(Tags.id)
                ?: Tags.insert { it[Tags.name] = tagName } get Tags.id
        }

        // Добавить связи
        PostTags.batchInsert(tagIds) { tagId ->
            this[PostTags.postId] = postId
            this[PostTags.tagId] = tagId
        }
    }
}

fun getPostsWithTags(): List<PostWithTags> {
    return transaction {
        val postsWithTags = (Posts leftJoin PostTags leftJoin Tags)
            .selectAll()
            .groupBy { it[Posts.id] }

        postsWithTags.map { (postId, rows) ->
            val firstRow = rows.first()
            PostWithTags(
                post = PostData(
                    id = firstRow[Posts.id],
                    title = firstRow[Posts.title],
                    content = firstRow[Posts.content]
                ),
                tags = rows
                    .filter { it[Tags.id] != null }
                    .map { row ->
                        TagData(
                            id = row[Tags.id],
                            name = row[Tags.name]
                        )
                    }
                    .distinctBy { it.id }
            )
        }
    }
}
```

### Self-referencing отношения:

```kotlin
object Employees : Table("employees") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val managerId = integer("manager_id").references(Employees.id).nullable()
    val departmentId = integer("department_id").references(Departments.id)

    override val primaryKey = PrimaryKey(id)
}

object Departments : Table("departments") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

fun getEmployeeHierarchy(employeeId: Int): EmployeeHierarchy? {
    return transaction {
        // Рекурсивный CTE для иерархии
        val hierarchyCte = Employees
            .select { Employees.id eq employeeId }
            .unionAll(
                Employees.join(
                    otherTable = hierarchyCte,
                    joinType = JoinType.INNER,
                    onColumn = { Employees.managerId },
                    otherColumn = { it[Employees.id] }
                )
            )

        val employees = hierarchyCte
            .selectAll()
            .map { row ->
                EmployeeData(
                    id = row[Employees.id],
                    name = row[Employees.name],
                    managerId = row[Employees.managerId]
                )
            }

        if (employees.isNotEmpty()) {
            buildHierarchy(employees, employeeId)
        } else {
            null
        }
    }
}

private fun buildHierarchy(employees: List<EmployeeData>, rootId: Int): EmployeeHierarchy {
    val employeeMap = employees.associateBy { it.id }
    val processed = mutableSetOf<Int>()

    fun buildNode(employeeId: Int): EmployeeHierarchy {
        if (employeeId in processed) return EmployeeHierarchy(employeeMap[employeeId]!!, emptyList())

        processed.add(employeeId)
        val employee = employeeMap[employeeId]!!
        val subordinates = employees
            .filter { it.managerId == employeeId }
            .map { buildNode(it.id) }

        return EmployeeHierarchy(employee, subordinates)
    }

    return buildNode(rootId)
}
```

## Транзакции и управление

### Уровни изоляции:

```kotlin
fun transferMoney(fromUserId: Int, toUserId: Int, amount: BigDecimal) {
    transaction(TransactionIsolation.SERIALIZABLE) {
        // Проверить баланс отправителя
        val fromBalance = Users.select { Users.id eq fromUserId }
            .single()[Users.balance]

        if (fromBalance < amount) {
            throw IllegalArgumentException("Insufficient funds")
        }

        // Выполнить перевод
        Users.update({ Users.id eq fromUserId }) { user ->
            user[Users.balance] = Users.balance - amount
        }

        Users.update({ Users.id eq toUserId }) { user ->
            user[Users.balance] = Users.balance + amount
        }

        // Логировать транзакцию
        Transactions.insert {
            it[Transactions.fromUserId] = fromUserId
            it[Transactions.toUserId] = toUserId
            it[Transactions.amount] = amount
        }
    }
}
```

### Вложенные транзакции:

```kotlin
fun complexBusinessOperation(userId: Int, productId: Int) {
    transaction {
        // Внешняя транзакция
        val user = getUser(userId)

        try {
            transaction {
                // Внутренняя транзакция - создание заказа
                val orderId = createOrder(userId, productId)

                transaction {
                    // Еще одна вложенная транзакция - обновление инвентаря
                    updateInventory(productId, -1)

                    // Если что-то пойдет не так, откатим только внутреннюю транзакцию
                    if (someConditionFails()) {
                        rollback()
                    }
                }

                // Финализировать заказ
                finalizeOrder(orderId)
            }
        } catch (e: Exception) {
            // Обработать ошибку внешней транзакции
            logger.error("Business operation failed", e)
            throw e
        }
    }
}
```

### Connection pooling и управление соединениями:

```kotlin
object DatabaseConfig {
    private val pool = HikariDataSource().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/myapp"
        username = "myuser"
        password = "mypassword"
        maximumPoolSize = 10
        minimumIdle = 5
        connectionTimeout = 30000
        idleTimeout = 600000
        maxLifetime = 1800000
    }

    init {
        Database.connect(pool)
    }

    fun getConnection(): HikariDataSource = pool
}

// Использование
fun withConnection(block: (Connection) -> Unit) {
    DatabaseConfig.getConnection().connection.use { conn ->
        block(conn)
    }
}

// Мониторинг пула соединений
fun printPoolStats() {
    val pool = DatabaseConfig.getConnection()
    println("Active connections: ${pool.hikariPoolMXBean.activeConnections}")
    println("Idle connections: ${pool.hikariPoolMXBean.idleConnections}")
    println("Total connections: ${pool.hikariPoolMXBean.totalConnections}")
    println("Threads awaiting connection: ${pool.hikariPoolMXBean.threadsAwaitingConnection}")
}
```

## Миграции и эволюция схемы

### Exposed миграции:

```kotlin
class InitialSchema : Migration() {
    override fun up() {
        SchemaUtils.create(Users, Posts, Categories)
    }

    override fun down() {
        SchemaUtils.drop(Users, Posts, Categories)
    }
}

class AddTagsTable : Migration() {
    override fun up() {
        SchemaUtils.create(Tags, PostTags)
    }

    override fun down() {
        SchemaUtils.drop(PostTags, Tags)
    }
}

class AddUserRoles : Migration() {
    override fun up() {
        Users.addColumn(Users.role, enumeration<UserRole>("role").default(UserRole.USER))
    }

    override fun down() {
        Users.dropColumn(Users.role)
    }
}

fun runMigrations() {
    transaction {
        SchemaUtils.createSchema(Users, Posts, Tags, PostTags)

        val migrations = listOf(
            InitialSchema(),
            AddTagsTable(),
            AddUserRoles()
        )

        for (migration in migrations) {
            try {
                migration.up()
                println("Migration ${migration::class.simpleName} applied successfully")
            } catch (e: Exception) {
                println("Migration ${migration::class.simpleName} failed: ${e.message}")
                migration.down() // rollback
                throw e
            }
        }
    }
}
```

### Flyway интеграция:

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.flywaydb:flyway-core:9.16.0")
}

// SQL миграции в resources/db/migration
// V1__Create_users_table.sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

// Kotlin код для запуска Flyway
fun runFlywayMigrations() {
    val flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://localhost:5432/myapp", "user", "password")
        .locations("classpath:db/migration")
        .load()

    flyway.migrate()
}
```

## DAO паттерн

### Entity классы:

```kotlin
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var age by Users.age
    var balance by Users.balance
    var isActive by Users.isActive
    var createdAt by Users.createdAt

    val posts by Post referrersOn Posts.authorId
    val orders by Order referrersOn Orders.userId

    fun deactivate() {
        isActive = false
    }

    fun credit(amount: BigDecimal) {
        balance += amount
    }
}

class Post(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Post>(Posts)

    var title by Posts.title
    var content by Posts.content
    var author by User referencedOn Posts.authorId
    var publishedAt by Posts.publishedAt

    val tags by Tag via PostTags
}

class Tag(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tag>(Tags)

    var name by Tags.name

    val posts by Post via PostTags
}
```

### CRUD с DAO:

```kotlin
class UserService {
    fun createUser(name: String, email: String): User {
        return transaction {
            User.new {
                this.name = name
                this.email = email
                this.isActive = true
                this.balance = BigDecimal.ZERO
            }
        }
    }

    fun getUserById(id: Int): User? {
        return transaction {
            User.findById(id)
        }
    }

    fun getUsersByAge(minAge: Int, maxAge: Int): List<User> {
        return transaction {
            User.find { Users.age.between(minAge, maxAge) }
                .toList()
        }
    }

    fun updateUser(id: Int, name: String? = null, email: String? = null): User? {
        return transaction {
            User.findById(id)?.apply {
                name?.let { this.name = it }
                email?.let { this.email = it }
            }
        }
    }

    fun deleteUser(id: Int): Boolean {
        return transaction {
            User.findById(id)?.delete() != null
        }
    }

    fun getUsersWithPosts(): List<UserWithPostsDto> {
        return transaction {
            User.all()
                .filter { it.posts.count() > 0 }
                .map { user ->
                    UserWithPostsDto(
                        user = user.toDto(),
                        posts = user.posts.map { it.toDto() }
                    )
                }
        }
    }
}

class PostService {
    fun createPost(title: String, content: String, authorId: Int): Post {
        return transaction {
            Post.new {
                this.title = title
                this.content = content
                this.author = User[authorId]
            }
        }
    }

    fun addTagToPost(postId: Int, tagName: String) {
        transaction {
            val post = Post[postId]
            val tag = Tag.find { Tags.name eq tagName }.firstOrNull()
                ?: Tag.new { name = tagName }

            post.tags = SizedCollection(post.tags + tag)
        }
    }

    fun publishPost(postId: Int) {
        transaction {
            Post[postId].publishedAt = LocalDateTime.now()
        }
    }
}
```

### Преобразование в DTO:

```kotlin
// DTO классы
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int?,
    val balance: BigDecimal,
    val isActive: Boolean,
    val createdAt: LocalDateTime
)

data class PostDto(
    val id: Int,
    val title: String,
    val content: String,
    val authorId: Int,
    val publishedAt: LocalDateTime?
)

// Extension functions для преобразования
fun User.toDto(): UserDto = UserDto(
    id = id.value,
    name = name,
    email = email,
    age = age,
    balance = balance,
    isActive = isActive,
    createdAt = createdAt
)

fun Post.toDto(): PostDto = PostDto(
    id = id.value,
    title = title,
    content = content,
    authorId = author.id.value,
    publishedAt = publishedAt
)
```

### Кастомные запросы с DAO:

```kotlin
class AdvancedUserService {
    fun getTopUsersByPostCount(limit: Int = 10): List<UserWithStatsDto> {
        return transaction {
            User.all()
                .map { user ->
                    UserWithStatsDto(
                        user = user.toDto(),
                        postCount = user.posts.count(),
                        lastPostDate = user.posts
                            .filterNot { it.publishedAt.isNull() }
                            .maxByOrNull { it.publishedAt!! }
                            ?.publishedAt
                    )
                }
                .sortedByDescending { it.postCount }
                .take(limit)
        }
    }

    fun searchUsers(query: String, limit: Int = 20): List<UserDto> {
        return transaction {
            User.find {
                (Users.name like "%$query%") or (Users.email like "%$query%")
            }
                .limit(limit)
                .map { it.toDto() }
        }
    }

    fun getUserActivityReport(userId: Int, since: LocalDateTime): UserActivityReport {
        return transaction {
            val user = User[userId]
            val postsInPeriod = user.posts.filter { it.publishedAt?.isAfter(since) ?: false }
            val totalLikes = postsInPeriod.sumOf { it.likes }

            UserActivityReport(
                user = user.toDto(),
                postsInPeriod = postsInPeriod.count(),
                totalLikes = totalLikes,
                avgLikesPerPost = if (postsInPeriod.isNotEmpty()) totalLikes.toDouble() / postsInPeriod.count() else 0.0
            )
        }
    }
}
```

## Создание DSL

### Type-safe builders

```kotlin
// HTML DSL
class HtmlBuilder {
    private val elements = mutableListOf<Element>()

    fun div(init: DivBuilder.() -> Unit) {
        val div = DivBuilder()
        div.init()
        elements.add(div.build())
    }

    fun build(): Element = HtmlElement(elements)
}

class DivBuilder {
    private val elements = mutableListOf<Element>()
    var classes: String = ""

    fun p(text: String) {
        elements.add(ParagraphElement(text))
    }

    fun build(): Element = DivElement(classes, elements)
}

// Использование
fun createHtml() = html {
    div {
        classes = "container"
        p("Hello, World!")
        p("This is a DSL example")
    }
}

fun html(init: HtmlBuilder.() -> Unit): Element {
    val builder = HtmlBuilder()
    builder.init()
    return builder.build()
}
```

### SQL DSL пример:

```kotlin
class QueryBuilder {
    private val conditions = mutableListOf<String>()

    infix fun String.eq(value: Any) {
        conditions.add("$this = '$value'")
    }

    infix fun String.like(pattern: String) {
        conditions.add("$this LIKE '$pattern'")
    }

    fun and(init: QueryBuilder.() -> Unit) {
        val subQuery = QueryBuilder().apply(init)
        if (subQuery.conditions.isNotEmpty()) {
            conditions.add("(${subQuery.conditions.joinToString(" AND ")})")
        }
    }

    fun build(): String {
        return if (conditions.isNotEmpty()) {
            "WHERE ${conditions.joinToString(" AND ")}"
        } else {
            ""
        }
    }
}

fun query(init: QueryBuilder.() -> Unit): String {
    val builder = QueryBuilder()
    builder.init()
    return "SELECT * FROM users ${builder.build()}"
}

// Использование
val sql = query {
    "name" eq "John"
    "age" like "2%"
    and {
        "status" eq "active"
        "role" eq "admin"
    }
}
// Результат: SELECT * FROM users WHERE name = 'John' AND age LIKE '2%' AND (status = 'active' AND role = 'admin')
```

## Kotlinx.serialization

### Базовое использование:

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Person(
    val name: String,
    val age: Int,
    val email: String? = null
)

fun main() {
    val person = Person("Alice", 30, "alice@example.com")

    // Сериализация в JSON
    val json = Json.encodeToString(person)
    println(json) // {"name":"Alice","age":30,"email":"alice@example.com"}

    // Десериализация из JSON
    val personFromJson = Json.decodeFromString<Person>(json)
    println(personFromJson) // Person(name=Alice, age=30, email=alice@example.com)
}
```

### Конфигурация Json:

```kotlin
val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = false
    explicitNulls = false
}

@Serializable
data class Config(
    val name: String,
    val version: Int = 1,
    val features: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)

// С красивым форматированием
val config = Config("MyApp", features = listOf("auth", "logging"))
val jsonString = json.encodeToString(config)
println(jsonString)
/*
{
    "name": "MyApp",
    "features": [
        "auth",
        "logging"
    ]
}
*/
```

### Кастомная сериализация:

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(with = ColorSerializer::class)
data class Color(val rgb: Int)

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString("#" + value.rgb.toString(16).padStart(6, '0').uppercase())
    }

    override fun deserialize(decoder: Decoder): Color {
        val hex = decoder.decodeString()
        val rgb = hex.removePrefix("#").toInt(16)
        return Color(rgb)
    }
}

@Serializable
data class Palette(val colors: List<Color>)

fun main() {
    val palette = Palette(listOf(Color(0xFF0000), Color(0x00FF00), Color(0x0000FF)))
    val json = Json.encodeToString(palette)
    println(json) // {"colors":["#FF0000","#00FF00","#0000FF"]}
}
```

### Полиморфная сериализация:

```kotlin
@Serializable
sealed class Shape

@Serializable
@SerialName("circle")
data class Circle(val radius: Double) : Shape()

@Serializable
@SerialName("rectangle")
data class Rectangle(val width: Double, val height: Double) : Shape()

@Serializable
data class Drawing(val shapes: List<Shape>)

fun main() {
    val drawing = Drawing(listOf(
        Circle(5.0),
        Rectangle(10.0, 20.0)
    ))

    val json = Json.encodeToString(drawing)
    println(json)
    // {"shapes":[{"type":"circle","radius":5.0},{"type":"rectangle","width":10.0,"height":20.0}]}

    val decoded = Json.decodeFromString<Drawing>(json)
    println(decoded)
}
```

## Metaprogramming в Kotlin

### Inline функции:

```kotlin
inline fun measureTime(block: () -> Unit): Long {
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}

// Использование
val time = measureTime {
    // some expensive operation
    Thread.sleep(100)
}
println("Operation took $time nanoseconds")
```

### Reified generics:

```kotlin
inline fun <reified T> List<Any>.filterIsInstance(): List<T> {
    return filter { it is T }.map { it as T }
}

inline fun <reified T> Gson.fromJson(json: String): T {
    return fromJson(json, T::class.java)
}

// Использование
val mixedList = listOf("hello", 42, "world", 3.14)
val strings = mixedList.filterIsInstance<String>() // ["hello", "world"]

val user: User = gson.fromJson(jsonString)
```

### Операторные функции:

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    operator fun times(factor: Int): Point {
        return Point(x * factor, y * factor)
    }

    operator fun unaryMinus(): Point {
        return Point(-x, -y)
    }
}

val p1 = Point(1, 2)
val p2 = Point(3, 4)
val sum = p1 + p2          // Point(4, 6)
val scaled = p1 * 3        // Point(3, 6)
val negated = -p1          // Point(-1, -2)
```

### Свойства-делегаты:

```kotlin
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ObservableProperty<T>(initialValue: T, val onChange: (T) -> Unit) : ReadWriteProperty<Any?, T> {
    private var value = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value
        this.value = value
        if (oldValue != value) {
            onChange(value)
        }
    }
}

class User {
    var name: String by ObservableProperty("") {
        println("Name changed to: $it")
    }

    var age: Int by ObservableProperty(0) {
        println("Age changed to: $it")
    }
}

// Встроенные делегаты
import kotlin.properties.Delegates

class Config {
    var url: String by Delegates.notNull()
    var port: Int by Delegates.vetoable(8080) { _, old, new ->
        new > 0 // только положительные порты
    }
    var timeout: Long by Delegates.observable(5000L) { _, old, new ->
        println("Timeout changed from $old to $new")
    }
}
```

## Продвинутые паттерны Kotlin

### Sealed классы и иерархии:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun handleResult(result: Result<String>) {
    when (result) {
        is Result.Success -> println("Success: ${result.data}")
        is Result.Error -> println("Error: ${result.exception.message}")
        Result.Loading -> println("Loading...")
    }
}

// Exhaustive when - компилятор гарантирует покрытие всех случаев
fun processResult(result: Result<User>): String {
    return when (result) {
        is Result.Success -> "User: ${result.data.name}"
        is Result.Error -> "Error occurred"
        Result.Loading -> "Please wait"
    }
}
```

### Функциональное программирование:

```kotlin
// Функции высшего порядка
fun <T, R> List<T>.map(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (item in this) {
        result.add(transform(item))
    }
    return result
}

// Композиция функций
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { x -> f(g(x)) }
}

val addOne: (Int) -> Int = { it + 1 }
val multiplyByTwo: (Int) -> Int = { it * 2 }
val addOneThenMultiply: (Int) -> Int = compose(multiplyByTwo, addOne)

println(addOneThenMultiply(3)) // 8

// Currying
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C {
    return { a -> { b -> f(a, b) } }
}

val add: (Int, Int) -> Int = { a, b -> a + b }
val curriedAdd = curry(add)
val addFive = curriedAdd(5)
println(addFive(3)) // 8
```

### DSL для конфигурации:

```kotlin
class ServerConfig {
    var port: Int = 8080
    var host: String = "localhost"
    var ssl: Boolean = false
    var routes = mutableListOf<RouteConfig>()

    fun route(path: String, method: String = "GET", init: RouteConfig.() -> Unit) {
        val route = RouteConfig(path, method).apply(init)
        routes.add(route)
    }
}

class RouteConfig(val path: String, val method: String) {
    var handler: (() -> String)? = null
    var auth: Boolean = false

    fun handler(block: () -> String) {
        handler = block
    }
}

fun server(init: ServerConfig.() -> Unit): ServerConfig {
    return ServerConfig().apply(init)
}

// Использование
val config = server {
    port = 9000
    host = "0.0.0.0"
    ssl = true

    route("/api/users", "GET") {
        auth = true
        handler { "Users list" }
    }

    route("/api/login", "POST") {
        handler { "Login successful" }
    }
}
```

### Типобезопасные билдеры:

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
class HtmlBuilder {
    private val elements = mutableListOf<HtmlElement>()

    fun head(init: HeadBuilder.() -> Unit) {
        val head = HeadBuilder().apply(init)
        elements.add(head.build())
    }

    fun body(init: BodyBuilder.() -> Unit) {
        val body = BodyBuilder().apply(init)
        elements.add(body.build())
    }

    fun build(): HtmlDocument = HtmlDocument(elements)
}

@HtmlDsl
class HeadBuilder {
    var title: String = ""

    fun build(): HeadElement = HeadElement(title)
}

@HtmlDsl
class BodyBuilder {
    private val elements = mutableListOf<HtmlElement>()

    fun div(classes: String = "", init: DivBuilder.() -> Unit) {
        val div = DivBuilder(classes).apply(init)
        elements.add(div.build())
    }

    fun p(text: String) {
        elements.add(ParagraphElement(text))
    }

    fun build(): BodyElement = BodyElement(elements)
}

@HtmlDsl
class DivBuilder(val classes: String) {
    private val elements = mutableListOf<HtmlElement>()

    fun p(text: String) {
        elements.add(ParagraphElement(text))
    }

    fun a(href: String, text: String) {
        elements.add(LinkElement(href, text))
    }

    fun build(): DivElement = DivElement(classes, elements)
}

// Использование
fun html(init: HtmlBuilder.() -> Unit): HtmlDocument {
    return HtmlBuilder().apply(init).build()
}

val document = html {
    head {
        title = "My Page"
    }

    body {
        div("header") {
            p("Welcome to my page")
        }

        div("content") {
            p("This is the main content")
            a("/contact", "Contact us")
        }
    }
}
```

> **Примечание**: Это полное руководство по дополнительным возможностям **Kotlin**. Для более глубокого изучения отдельных тем рекомендуется изучать официальную документацию и специализированные ресурсы.

## Продвинутые техники работы с Ktor

### Middleware и Interceptors

**Ktor** предоставляет мощную систему **middleware** и **interceptors** для обработки запросов и ответов:**

```kotlin
fun Application.module() {
    // Глобальный interceptor
    intercept(ApplicationCallPipeline.Call) {
        val startTime = System.currentTimeMillis()
        proceed()
        val duration = System.currentTimeMillis() - startTime
        call.response.headers.append("X-Response-Time", duration.toString())
    }

    // Interceptor для конкретного маршрута
    routing {
        route("/api") {
            intercept(ApplicationCallPipeline.Call) {
                // Проверка аутентификации
                val token = call.request.headers["Authorization"]
                if (token == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@intercept
                }
                proceed()
            }

            get("/data") {
                call.respond("Data")
            }
        }
    }
}
```

**Middleware** и **interceptors** позволяют добавлять общую логику обработки запросов, такую как логирование, аутентификация, валидация и обработка ошибок.

### Валидация запросов

**Валидация входных данных критична для безопасности и надежности приложения:**

```kotlin
fun Application.module() {
    routing {
        post("/users") {
            val user = call.receive<User>()

            // Валидация данных
            val errors = validateUser(user)
            if (errors.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(errors)
                )
                return@post
            }

            // Обработка валидных данных
            val savedUser = userService.save(user)
            call.respond(HttpStatusCode.Created, savedUser)
        }
    }
}

fun validateUser(user: User): List<String> {
    val errors = mutableListOf<String>()

    if (user.name.isBlank()) {
        errors.add("Name cannot be blank")
    }

    if (user.email.isBlank() || !user.email.contains("@")) {
        errors.add("Invalid email")
    }

    if (user.age < 0 || user.age > 150) {
        errors.add("Age must be between 0 and 150")
    }

    return errors
}
```

Валидация запросов предотвращает обработку некорректных данных и обеспечивает безопасность приложения.

## Продвинутые техники работы с Exposed

### Raw SQL запросы

**Для сложных запросов может потребоваться использование **raw SQL**:**

```kotlin
transaction {
    // Выполнение raw SQL запроса
    val result = exec("SELECT * FROM users WHERE age > ?") {
        it.setInt(1, 18)
        it.executeQuery()
    }

    // Обработка результата
    while (result.next()) {
        val id = result.getInt("id")
        val name = result.getString("name")
        val age = result.getInt("age")
        println("User: $id, $name, $age")
    }
}
```

**Raw SQL** запросы позволяют выполнять сложные операции, которые сложно выразить через **DSL API Exposed**.

### Пакетная обработка

**Пакетная обработка эффективна для массовых операций:**

```kotlin
transaction {
    // Пакетная вставка
    Users.batchInsert(users) { user ->
        this[Users.name] = user.name
        this[Users.email] = user.email
        this[Users.age] = user.age
    }

    // Пакетное обновление
    val ids = listOf(1, 2, 3, 4, 5)
    Users.update({ Users.id inList ids }) {
        it[Users.lastLogin] = DateTime.now()
    }
}
```

Пакетная обработка значительно улучшает производительность при работе с большими объемами данных.

## Расширенные возможности DSL

### DSL с валидацией и обработкой ошибок

**Добавление валидации и обработки ошибок в **DSL** делает их более надежными:**

```kotlin
class ConfigDSL {
    private val errors = mutableListOf<String>()

    var port: Int = 8080
        set(value) {
            if (value !in 1..65535) {
                errors.add("Port must be between 1 and 65535")
            } else {
                field = value
            }
        }

    var host: String = "localhost"
        set(value) {
            if (value.isBlank()) {
                errors.add("Host cannot be blank")
            } else {
                field = value
            }
        }

    fun validate(): Result<Config> {
        return if (errors.isEmpty()) {
            Result.success(Config(host, port))
        } else {
            Result.failure(IllegalArgumentException(errors.joinToString(", ")))
        }
    }
}

fun config(init: ConfigDSL.() -> Unit): Result<Config> {
    return ConfigDSL().apply(init).validate()
}

// Использование
val result = config {
    host = "localhost"
    port = 8080
}

result.onSuccess { config ->
    println("Configuration loaded: $config")
}.onFailure { error ->
    println("Configuration error: ${error.message}")
}
```

Валидация в **DSL** помогает выявлять ошибки на этапе компиляции или раннего выполнения, что улучшает надежность кода.

### DSL с контекстом выполнения

**Создание **DSL**, которые используют контекст выполнения для дополнительной функциональности:**

```kotlin
class DSLContext {
    var currentScope: String = "global"
    val variables = mutableMapOf<String, Any>()
    val functions = mutableMapOf<String, (List<Any>) -> Any>()
}

fun dsl(context: DSLContext = DSLContext(), init: DSLContext.() -> Unit): DSLContext {
    return context.apply(init)
}

// Использование
val config = dsl {
    currentScope = "database"
    variables["host"] = "localhost"
    variables["port"] = 5432

    functions["connect"] = { args ->
        println("Connecting to ${args[0]}:${args[1]}")
        "Connected"
    }
}
```

Контекст выполнения позволяет **DSL** накапливать состояние и использовать его для валидации, генерации кода и выполнения операций.

## Оптимизация производительности

### Профилирование Ktor приложений

**Профилирование помогает выявить узкие места в производительности:**

```kotlin
fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> !call.request.path().startsWith("/health") }
    }

    install(Compression) {
        gzip {
            priority = 1.0
            minimumSize(1024)
        }
    }

    // Метрики производительности
    routing {
        get("/metrics") {
            val metrics = ApplicationMetrics.getMetrics()
            call.respond(metrics)
        }
    }
}
```

Профилирование и мониторинг производительности критичны для выявления и устранения узких мест в приложении.

### Оптимизация запросов к базе данных

**Оптимизация запросов улучшает производительность приложения:**

```kotlin
// Использование индексов
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
}

// Оптимизация запросов с JOIN
transaction {
    val usersWithPosts = Users
        .innerJoin(Posts)
        .select { Users.id eq Posts.userId }
        .groupBy(Users.id, Users.name)
        .map { row ->
            UserWithPosts(
                id = row[Users.id].value,
                name = row[Users.name],
                postCount = row[Posts.id.count()]
            )
        }
}

// Использование prepared statements
transaction {
    val stmt = connection.prepareStatement(
        "SELECT * FROM users WHERE age > ? AND city = ?"
    )
    stmt.setInt(1, 18)
    stmt.setString(2, "NYC")
    val resultSet = stmt.executeQuery()
    // Обработка результата
}
```

Оптимизация запросов к базе данных критична для производительности приложений, особенно при работе с большими объемами данных.

## Лучшие практики

### Обработка ошибок

**Правильная обработка ошибок критична для надежности приложения:**

```kotlin
fun Application.module() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(listOf(cause.message ?: "Validation failed"))
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(listOf(cause.message ?: "Resource not found"))
            )
        }

        exception<Exception> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(listOf("Internal server error"))
            )
            logger.error("Unexpected error", cause)
        }
    }
}
```

Централизованная обработка ошибок упрощает управление ошибками и обеспечивает консистентные ответы **API**.

### Безопасность

**Реализация мер безопасности критична для защиты приложения:**

```kotlin
fun Application.module() {
    install(Security) {
        basic {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == "user" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(ContentSecurityPolicy) {
        default {
            script {
                unsafeInline()
            }
            style {
                unsafeInline()
            }
        }
    }
}
```

Меры безопасности защищают приложение от различных типов атак и обеспечивают безопасность данных пользователей.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности и лучших практик.

## Продвинутые техники сериализации

### Кастомная сериализация

**Создание пользовательских сериализаторов для специфичных случаев:**

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

// Кастомный сериализатор для LocalDate
@Serializable(with = LocalDateSerializer::class)
data class LocalDate(val year: Int, val month: Int, val day: Int)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString("${value.year}-${value.month}-${value.day}")
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString()
        val parts = string.split("-")
        return LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
    }
}

// Использование
@Serializable
data class Event(
    val name: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

val event = Event("Conference", LocalDate(2024, 6, 15))
val json = Json.encodeToString(event)
```

Кастомная сериализация позволяет работать со специфичными типами данных, которые не поддерживаются стандартными сериализаторами.

### Сериализация с трансформациями

**Сериализация с трансформациями данных:**

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// Трансформация данных при сериализации
@Serializable
data class User(
    val name: String,
    @SerialName("email_address")
    val email: String,
    @Transient
    val password: String = ""
) {
    @Serializer(forClass = User::class)
    companion object {
        fun serializer() = object : KSerializer<User> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("User") {
                element<String>("name")
                element<String>("email_address")
            }

            override fun serialize(encoder: Encoder, value: User) {
                encoder.encodeStructure(descriptor) {
                    encodeStringElement(descriptor, 0, value.name)
                    encodeStringElement(descriptor, 1, value.email.uppercase())
                }
            }

            override fun deserialize(decoder: Decoder): User {
                return decoder.decodeStructure(descriptor) {
                    var name: String? = null
                    var email: String? = null
                    while (true) {
                        when (val index = decodeElementIndex(descriptor)) {
                            0 -> name = decodeStringElement(descriptor, 0)
                            1 -> email = decodeStringElement(descriptor, 1)
                            CompositeDecoder.DECODE_DONE -> break
                            else -> error("Unexpected index: $index")
                        }
                    }
                    requireNotNull(name)
                    requireNotNull(email)
                    User(name, email)
                }
            }
        }
    }
}
```

Трансформация данных при сериализации позволяет адаптировать структуру данных для различных форматов и **API**.

## Продвинутые техники работы с базами данных

### Оптимизация запросов Exposed

**Продвинутые техники оптимизации запросов:**

```kotlin
// Использование индексов для оптимизации
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
    val age = integer("age").index()

    // Составной индекс
    init {
        index(name, email)
    }
}

// Оптимизация JOIN запросов
transaction {
    val usersWithPosts = Users
        .innerJoin(Posts)
        .select {
            Users.id eq Posts.userId and (Users.age greater 18)
        }
        .groupBy(Users.id, Users.name)
        .map { row ->
            UserWithPosts(
                id = row[Users.id].value,
                name = row[Users.name],
                postCount = row[Posts.id.count()]
            )
        }
}

// Использование EXISTS вместо IN для больших наборов
val activeUsers = Users.select {
    exists(
        Posts.select { Posts.userId eq Users.id }
    )
}
```

Правильная оптимизация запросов критична для производительности приложений, особенно при работе с большими объемами данных.

### Работа с транзакциями

**Продвинутые техники работы с транзакциями:**

```kotlin
// Уровни изоляции транзакций
transaction(Connection.TRANSACTION_READ_UNCOMMITTED) {
    // Чтение незафиксированных данных
}

transaction(Connection.TRANSACTION_READ_COMMITTED) {
    // Чтение только зафиксированных данных
}

transaction(Connection.TRANSACTION_REPEATABLE_READ) {
    // Повторяемое чтение
}

transaction(Connection.TRANSACTION_SERIALIZABLE) {
    // Сериализуемые транзакции
}

// Вложенные транзакции
transaction {
    // Внешняя транзакция
    transaction {
        // Внутренняя транзакция
    }
}

// Сохранение точек отката
transaction {
    val savepoint = connection.setSavepoint()
    try {
        // Операции
        if (someCondition) {
            connection.rollback(savepoint)
        }
    } catch (e: Exception) {
        connection.rollback(savepoint)
        throw e
    }
}
```

Правильное управление транзакциями обеспечивает целостность данных и правильную обработку ошибок.

## Интеграция с внешними системами

### Работа с API

**Интеграция с внешними **API** через **Ktor Client**:**

```kotlin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ApiClient(private val client: HttpClient) {
    suspend fun getUsers(): List<User> {
        val response: HttpResponse = client.get("https://api.example.com/users")
        return response.body<List<User>>()
    }

    suspend fun createUser(user: User): User {
        val response: HttpResponse = client.post("https://api.example.com/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.body<User>()
    }

    suspend fun updateUser(id: Long, user: User): User {
        val response: HttpResponse = client.put("https://api.example.com/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.body<User>()
    }

    suspend fun deleteUser(id: Long) {
        client.delete("https://api.example.com/users/$id")
    }
}
```

Интеграция с внешними **API** позволяет создавать приложения, которые взаимодействуют с различными сервисами.

### Работа с микросервисами

**Интеграция с микросервисами через **Ktor**:**

```kotlin
// Service Discovery интеграция
class ServiceDiscovery {
    private val services = mutableMapOf<String, ServiceInfo>()

    fun register(serviceName: String, url: String) {
        services[serviceName] = ServiceInfo(url, System.currentTimeMillis())
    }

    fun getService(serviceName: String): ServiceInfo? {
        return services[serviceName]?.takeIf {
            System.currentTimeMillis() - it.timestamp < 60000  // 1 минута TTL
        }
    }

    data class ServiceInfo(val url: String, val timestamp: Long)
}

// Circuit Breaker для микросервисов
class CircuitBreakerClient(
    private val client: HttpClient,
    private val serviceDiscovery: ServiceDiscovery
) {
    suspend fun callService(serviceName: String, path: String): HttpResponse {
        val service = serviceDiscovery.getService(serviceName)
            ?: throw ServiceNotFoundException(serviceName)

        return try {
            client.get("${service.url}$path")
        } catch (e: Exception) {
            handleServiceError(serviceName, e)
            throw e
        }
    }
}
```

Интеграция с микросервисами требует управления **service discovery**, **circuit breakers** и обработки ошибок для создания устойчивых приложений.

## Продвинутые техники сериализации

### Работа с различными форматами

**Сериализация в различные форматы данных:**

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.properties.Properties

// JSON сериализация
@Serializable
data class User(val name: String, val age: Int)

val user = User("Alice", 25)
val json = Json.encodeToString(user)
val deserialized = Json.decodeFromString<User>(json)

// Pretty printing JSON
val prettyJson = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    ignoreUnknownKeys = true
    encodeDefaults = true
}.encodeToString(user)

// Protocol Buffers сериализация
val protoBytes = ProtoBuf.encodeToByteArray(user)
val protoDeserialized = ProtoBuf.decodeFromByteArray<User>(protoBytes)

// Properties сериализация
val properties = Properties.encodeToString(user)
val propertiesDeserialized = Properties.decodeFromString<User>(properties)
```

Поддержка различных форматов сериализации позволяет работать с разными системами и **API**.

### Кастомная сериализация для сложных типов

**Создание сериализаторов для сложных типов:**

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

// Сериализация Enum с кастомными значениями
@Serializable(with = StatusSerializer::class)
enum class Status {
    PENDING, APPROVED, REJECTED;

    companion object {
        fun fromString(value: String) = when (value.lowercase()) {
            "pending" -> PENDING
            "approved" -> APPROVED
            "rejected" -> REJECTED
            else -> throw IllegalArgumentException("Unknown status: $value")
        }
    }
}

object StatusSerializer : KSerializer<Status> {
    override val descriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Status) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): Status {
        return Status.fromString(decoder.decodeString())
    }
}

// Использование
@Serializable
data class Order(val id: Long, val status: Status)

val order = Order(1, Status.PENDING)
val json = Json.encodeToString(order)  // {"id":1,"status":"pending"}
```

Кастомная сериализация позволяет работать со специфичными типами данных и форматами.

## Продвинутые техники метапрограммирования

### Генерация кода с использованием KSP

**Использование **KSP** для создания процессоров кода:**

```kotlin
// Процессор KSP для генерации builder классов
class BuilderProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("GenerateBuilder")
        val errors = mutableListOf<KSAnnotated>()

        symbols.forEach { symbol ->
            if (symbol !is KSClassDeclaration) {
                errors.add(symbol)
                return@forEach
            }

            try {
                generateBuilder(symbol, resolver)
            } catch (e: Exception) {
                logger.error("Error generating builder for ${symbol.qualifiedName}: ${e.message}", symbol)
                errors.add(symbol)
            }
        }

        return errors
    }

    private fun generateBuilder(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        val className = classDeclaration.simpleName.asString()
        val builderName = "${className}Builder"
        val packageName = classDeclaration.packageName.asString()

        val properties = classDeclaration.getAllProperties()

        val code = buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("class $builderName {")
            properties.forEach { prop ->
                val type = prop.type.resolve()
                val propName = prop.simpleName.asString()
                appendLine("    var $propName: ${type}? = null")
            }
            appendLine()
            appendLine("    fun build(): $className {")
            appendLine("        return $className(")
            properties.forEachIndexed { index, prop ->
                val propName = prop.simpleName.asString()
                val comma = if (index < properties.size - 1) "," else ""
                appendLine("            $propName = $propName!!$comma")
            }
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        // Запись сгенерированного кода
        writeGeneratedCode(builderName, code)
    }
}
```

**KSP** позволяет создавать процессоры кода, которые генерируют код на основе аннотаций, что автоматизирует создание **boilerplate** кода.

### Генерация кода во время компиляции

**Создание инструментов для генерации кода:**

```kotlin
// Генерация тестовых классов
fun generateTestClass(sourceClass: KClass<*>): String {
    val className = sourceClass.simpleName ?: "Unknown"
    val testClassName = "${className}Test"
    val packageName = sourceClass.java.`package`.name

    return buildString {
        appendLine("package $packageName")
        appendLine()
        appendLine("import org.junit.jupiter.api.Test")
        appendLine("import org.junit.jupiter.api.Assertions.*")
        appendLine()
        appendLine("class $testClassName {")
        appendLine()
        sourceClass.memberFunctions.forEach { func ->
            if (func.parameters.size == 1) {  // Методы без параметров
                val methodName = func.name
                appendLine("    @Test")
                appendLine("    fun `test ${methodName}`() {")
                appendLine("        val instance = ${className}()")
                appendLine("        val result = instance.$methodName()")
                appendLine("        assertNotNull(result)")
                appendLine("    }")
                appendLine()
            }
        }
        appendLine("}")
    }
}
```

Генерация тестовых классов автоматизирует создание базовых тестов и улучшает покрытие тестами.

## Интеграция с внешними системами

### Работа с REST API

**Интеграция с **REST API** через различные библиотеки:**

```kotlin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// REST API клиент
class RestApiClient(private val baseUrl: String, private val client: HttpClient) {
    suspend fun getUsers(): List<User> {
        val response: HttpResponse = client.get("$baseUrl/users")
        return response.body<List<User>>()
    }

    suspend fun getUser(id: Long): User {
        val response: HttpResponse = client.get("$baseUrl/users/$id")
        return response.body<User>()
    }

    suspend fun createUser(user: User): User {
        val response: HttpResponse = client.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.body<User>()
    }

    suspend fun updateUser(id: Long, user: User): User {
        val response: HttpResponse = client.put("$baseUrl/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.body<User>()
    }

    suspend fun deleteUser(id: Long) {
        client.delete("$baseUrl/users/$id")
    }
}

// Использование
val apiClient = RestApiClient("https://api.example.com", HttpClient())
val users = apiClient.getUsers()
val user = apiClient.createUser(User(name = "Alice", email = "alice@example.com"))
```

**REST API** клиент позволяет интегрироваться с внешними сервисами и получать данные для приложения.

### Работа с GraphQL

**Интеграция с **GraphQL API**:**

```kotlin
// GraphQL запросы
class GraphQLClient(private val url: String, private val client: HttpClient) {
    suspend fun query(query: String, variables: Map<String, Any> = emptyMap()): GraphQLResponse {
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "query" to query,
                "variables" to variables
            ))
        }
        return response.body<GraphQLResponse>()
    }

    suspend fun mutation(mutation: String, variables: Map<String, Any> = emptyMap()): GraphQLResponse {
        return query(mutation, variables)
    }
}

data class GraphQLResponse(
    val data: Map<String, Any>?,
    val errors: List<Map<String, Any>>? = null
)

// Использование
val graphQLClient = GraphQLClient("https://api.example.com/graphql", HttpClient())

val query = """
    query {
        users {
            id
            name
            email
        }
    }
""".trimIndent()

val response = graphQLClient.query(query)
val users = response.data?.get("users") as? List<Map<String, Any>>
```

**GraphQL** интеграция позволяет выполнять гибкие запросы и получать только необходимые данные.

## Продвинутые паттерны проектирования

### Factory Pattern

**Реализация паттерна **Factory** в **Kotlin**:**

```kotlin
// Factory через sealed классы
sealed class Shape {
    abstract fun area(): Double

    data class Circle(val radius: Double) : Shape() {
        override fun area() = Math.PI * radius * radius
    }

    data class Rectangle(val width: Double, val height: Double) : Shape() {
        override fun area() = width * height
    }

    companion object {
        fun create(type: String, params: Map<String, Double>): Shape {
            return when (type) {
                "circle" -> Circle(params["radius"] ?: 0.0)
                "rectangle" -> Rectangle(
                    params["width"] ?: 0.0,
                    params["height"] ?: 0.0
                )
                else -> throw IllegalArgumentException("Unknown shape type: $type")
            }
        }
    }
}

// Использование
val circle = Shape.create("circle", mapOf("radius" to 5.0))
val rectangle = Shape.create("rectangle", mapOf("width" to 10.0, "height" to 20.0))
```

**Factory Pattern** позволяет создавать объекты без явного указания классов, что делает код более гибким.

### Builder Pattern

**Реализация паттерна **Builder** в **Kotlin**:**

```kotlin
// Builder Pattern с DSL
class UserBuilder {
    var name: String = ""
    var email: String = ""
    var age: Int = 0
    var address: String = ""

    fun build(): User {
        require(name.isNotBlank()) { "Name is required" }
        require(email.isNotBlank()) { "Email is required" }
        return User(name, email, age, address)
    }
}

fun user(init: UserBuilder.() -> Unit): User {
    return UserBuilder().apply(init).build()
}

// Использование
val user = user {
    name = "Alice"
    email = "alice@example.com"
    age = 25
    address = "123 Main St"
}

// Builder для сложных объектов
class DatabaseConnectionBuilder {
    var host: String = "localhost"
    var port: Int = 5432
    var database: String = ""
    var username: String = ""
    var password: String = ""
    var ssl: Boolean = false
    var connectionPoolSize: Int = 10

    fun build(): DatabaseConnection {
        require(database.isNotBlank()) { "Database name is required" }
        return DatabaseConnection(
            "jdbc:postgresql://$host:$port/$database",
            username,
            password,
            ssl,
            connectionPoolSize
        )
    }
}

fun database(init: DatabaseConnectionBuilder.() -> Unit): DatabaseConnection {
    return DatabaseConnectionBuilder().apply(init).build()
}
```

**Builder Pattern** позволяет создавать сложные объекты шаг за шагом, что делает код более читаемым и гибким.

## Дополнительные паттерны и техники

### Singleton Pattern

**Реализация паттерна **Singleton** в **Kotlin**:**

```kotlin
// Singleton через object
object DatabaseManager {
    private var connection: Connection? = null

    fun getConnection(): Connection {
        if (connection == null) {
            connection = createConnection()
        }
        return connection!!
    }

    private fun createConnection(): Connection {
        // Создание подключения
        return connection
    }
}

// Использование
val connection = DatabaseManager.getConnection()

// Thread-safe Singleton
class ThreadSafeSingleton private constructor() {
    companion object {
        @Volatile
        private var instance: ThreadSafeSingleton? = null

        fun getInstance(): ThreadSafeSingleton {
            return instance ?: synchronized(this) {
                instance ?: ThreadSafeSingleton().also { instance = it }
            }
        }
    }
}

// Lazy initialization Singleton
class LazySingleton private constructor() {
    companion object {
        val instance: LazySingleton by lazy {
            LazySingleton()
        }
    }
}
```

**Singleton Pattern** обеспечивает единственный экземпляр класса в приложении.

### Adapter Pattern

**Реализация паттерна **Adapter** в **Kotlin**:**

```kotlin
// Адаптер для интеграции с внешним API
interface PaymentProcessor {
    fun processPayment(amount: Double): Boolean
}

class LegacyPaymentSystem {
    fun pay(amount: Double): String {
        // Старая система возвращает String
        return if (amount > 0) "SUCCESS" else "FAILED"
    }
}

class PaymentAdapter(private val legacySystem: LegacyPaymentSystem) : PaymentProcessor {
    override fun processPayment(amount: Double): Boolean {
        val result = legacySystem.pay(amount)
        return result == "SUCCESS"
    }
}

// Использование
val legacySystem = LegacyPaymentSystem()
val adapter = PaymentAdapter(legacySystem)
val success = adapter.processPayment(100.0)  // true
```

**Adapter Pattern** позволяет интегрировать несовместимые интерфейсы.

### Facade Pattern

**Реализация паттерна **Facade** в **Kotlin**:**

```kotlin
// Фасад для упрощения сложной подсистемы
class OrderFacade(
    private val inventoryService: InventoryService,
    private val paymentService: PaymentService,
    private val shippingService: ShippingService
) {
    fun placeOrder(order: Order): OrderResult {
        // Проверка наличия товаров
        if (!inventoryService.checkAvailability(order.items)) {
            return OrderResult.Failure("Items not available")
        }

        // Обработка платежа
        if (!paymentService.processPayment(order.total)) {
            return OrderResult.Failure("Payment failed")
        }

        // Резервирование товаров
        inventoryService.reserveItems(order.items)

        // Отправка заказа
        val trackingNumber = shippingService.shipOrder(order)

        return OrderResult.Success(trackingNumber)
    }
}

sealed class OrderResult {
    data class Success(val trackingNumber: String) : OrderResult()
    data class Failure(val message: String) : OrderResult()
}
```

**Facade Pattern** упрощает работу со сложными подсистемами, предоставляя простой интерфейс.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, Facade), работу с микросервисами и оптимизацию производительности.

## Дополнительные паттерны

### Observer Pattern

**Реализация паттерна **Observer** в **Kotlin**:**

```kotlin
// Observer интерфейс
interface Observer<T> {
    fun update(data: T)
}

// Subject для управления наблюдателями
class Subject<T> {
    private val observers = mutableListOf<Observer<T>>()

    fun addObserver(observer: Observer<T>) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun notifyObservers(data: T) {
        observers.forEach { it.update(data) }
    }
}

// Использование
class DataSubject : Subject<String>() {
    private var data: String = ""

    fun setData(newData: String) {
        data = newData
        notifyObservers(data)
    }
}

class DataObserver : Observer<String> {
    override fun update(data: String) {
        println("Data updated: $data")
    }
}

val subject = DataSubject()
val observer = DataObserver()
subject.addObserver(observer)
subject.setData("New data")  // Data updated: New data
```

**Observer Pattern** позволяет объектам уведомлять других об изменениях состояния.

### Command Pattern

**Реализация паттерна **Command** в **Kotlin**:**

```kotlin
// Command интерфейс
interface Command {
    fun execute()
    fun undo()
}

// Конкретные команды
class AddItemCommand(
    private val list: MutableList<String>,
    private val item: String
) : Command {
    override fun execute() {
        list.add(item)
    }

    override fun undo() {
        list.remove(item)
    }
}

class RemoveItemCommand(
    private val list: MutableList<String>,
    private val item: String
) : Command {
    private var wasPresent = false

    override fun execute() {
        wasPresent = list.remove(item)
    }

    override fun undo() {
        if (wasPresent) {
            list.add(item)
        }
    }
}

// Invoker
class CommandInvoker {
    private val history = mutableListOf<Command>()

    fun execute(command: Command) {
        command.execute()
        history.add(command)
    }

    fun undo() {
        if (history.isNotEmpty()) {
            val command = history.removeLast()
            command.undo()
        }
    }
}

// Использование
val list = mutableListOf<String>()
val invoker = CommandInvoker()

invoker.execute(AddItemCommand(list, "item1"))
invoker.execute(AddItemCommand(list, "item2"))
invoker.undo()  // Удаляет "item2"
```

**Command Pattern** инкапсулирует запросы как объекты, что позволяет параметризовать клиентов различными запросами.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, Command), работу с микросервисами и оптимизацию производительности.

## Дополнительные паттерны и техники

### Template Method Pattern

**Реализация паттерна **Template Method** в **Kotlin**:**

```kotlin
// Template Method Pattern
abstract class DataProcessor {
    fun process(data: String): String {
        val validated = validate(data)
        val transformed = transform(validated)
        val saved = save(transformed)
        return saved
    }

    protected open fun validate(data: String): String {
        require(data.isNotBlank()) { "Data cannot be blank" }
        return data
    }

    protected abstract fun transform(data: String): String

    protected open fun save(data: String): String {
        println("Saving: $data")
        return data
    }
}

class XMLProcessor : DataProcessor() {
    override fun transform(data: String): String {
        return "<data>$data</data>"
    }
}

class JSONProcessor : DataProcessor() {
    override fun transform(data: String): String {
        return """{"data": "$data"}"""
    }
}
```

**Template Method Pattern** определяет скелет алгоритма, позволяя подклассам переопределять отдельные шаги.

### Visitor Pattern

**Реализация паттерна **Visitor** в **Kotlin**:**

```kotlin
// Visitor Pattern
interface Visitor {
    fun visit(element: Element): String
}

interface Element {
    fun accept(visitor: Visitor): String
}

class ConcreteElement1 : Element {
    override fun accept(visitor: Visitor): String {
        return visitor.visit(this)
    }
}

class ConcreteElement2 : Element {
    override fun accept(visitor: Visitor): String {
        return visitor.visit(this)
    }
}

class ConcreteVisitor : Visitor {
    override fun visit(element: Element): String {
        return when (element) {
            is ConcreteElement1 -> "Visited Element1"
            is ConcreteElement2 -> "Visited Element2"
            else -> "Unknown element"
        }
    }
}
```

**Visitor Pattern** позволяет добавлять новые операции к объектам без изменения их классов.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, `Command`, `Template Method`, Visitor), работу с микросервисами и оптимизацию производительности.

## Дополнительные паттерны

### Chain of Responsibility Pattern

**Реализация паттерна Chain of Responsibility:**

```kotlin
// Chain of Responsibility
abstract class Handler {
    protected var next: Handler? = null

    fun setNext(handler: Handler): Handler {
        next = handler
        return handler
    }

    abstract fun handle(request: Request): Response?

    protected fun handleNext(request: Request): Response? {
        return next?.handle(request)
    }
}

class AuthenticationHandler : Handler() {
    override fun handle(request: Request): Response? {
        return if (request.isAuthenticated) {
            handleNext(request)
        } else {
            Response(401, "Unauthorized")
        }
    }
}

class AuthorizationHandler : Handler() {
    override fun handle(request: Request): Response? {
        return if (request.hasPermission) {
            handleNext(request)
        } else {
            Response(403, "Forbidden")
        }
    }
}

// Использование
val chain = AuthenticationHandler()
    .setNext(AuthorizationHandler())
    .setNext(ProcessingHandler())

val response = chain.handle(request)
```

**Chain of Responsibility** позволяет передавать запросы по цепочке обработчиков.

### State Pattern

**Реализация паттерна **State**:**

```kotlin
// State Pattern
interface State {
    fun handle(context: Context)
}

class ConcreteStateA : State {
    override fun handle(context: Context) {
        println("State A")
        context.state = ConcreteStateB()
    }
}

class ConcreteStateB : State {
    override fun handle(context: Context) {
        println("State B")
        context.state = ConcreteStateA()
    }
}

class Context(var state: State) {
    fun request() {
        state.handle(this)
    }
}

// Использование
val context = Context(ConcreteStateA())
context.request()  // State A
context.request()  // State B
```

**State Pattern** позволяет объекту изменять свое поведение при изменении внутреннего состояния.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, `Command`, `Template Method`, `Visitor`, `Chain of Responsibility`, State), работу с микросервисами и оптимизацию производительности.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Дополнительные возможности **Kotlin**, включая сериализацию, метапрограммирование, интеграцию с внешними системами и различные паттерны проектирования, позволяют создавать гибкие, масштабируемые и поддерживаемые приложения. Понимание этих техник помогает эффективно решать сложные задачи разработки и создавать качественный код.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, `Command`, `Template Method`, `Visitor`, `Chain of Responsibility`, State), работу с микросервисами, оптимизацию производительности и заключение.

## Дополнительные ресурсы

**Для дальнейшего изучения дополнительных возможностей **Kotlin** рекомендуется:**

- **Kotlin Serialization**: **https**://**kotlinlang.org**/**docs**/**serialization.html**
- **Kotlin Multiplatform**: **https**://**kotlinlang.org**/**docs**/**multiplatform.html**
- **Kotlin Native**: **https**://**kotlinlang.org**/**docs**/**native-overview.html**
- **Design Patterns** in **Kotlin**: **https**://**github.com**/**dbacinski**/**Design-`Patterns-In`-Kotlin**
- **Kotlin Best Practices**: **https**://**kotlinlang.org**/**docs**/**coding-conventions.html**

## Итоговые рекомендации

**При использовании дополнительных возможностей **Kotlin** рекомендуется:**

1. Использовать сериализацию для работы с **JSON** и другими форматами
2. Применять метапрограммирование для автоматизации создания кода
3. Использовать паттерны проектирования для структурирования кода
4. Интегрироваться с внешними системами через **REST API** и другие протоколы
5. Оптимизировать производительность для критичных участков кода

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, `Command`, `Template Method`, `Visitor`, `Chain of Responsibility`, State), работу с микросервисами, оптимизацию производительности, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Реализация паттерна Strategy

**Пример использования паттерна **Strategy** для обработки платежей:**

```kotlin
interface PaymentStrategy {
    fun pay(amount: Double): Boolean
}

class CreditCardPayment(private val cardNumber: String) : PaymentStrategy {
    override fun pay(amount: Double): Boolean {
        // Логика оплаты кредитной картой
        return true
    }
}

class PayPalPayment(private val email: String) : PaymentStrategy {
    override fun pay(amount: Double): Boolean {
        // Логика оплаты через PayPal
        return true
    }
}

class PaymentProcessor(private val strategy: PaymentStrategy) {
    fun processPayment(amount: Double): Boolean {
        return strategy.pay(amount)
    }
}

// Использование
val processor = PaymentProcessor(CreditCardPayment("1234-5678-9012-3456"))
processor.processPayment(100.0)
```

Паттерн **Strategy** позволяет выбирать алгоритм во время выполнения.

### Реализация паттерна Decorator

**Пример использования паттерна **Decorator**:**

```kotlin
interface Coffee {
    fun cost(): Double
    fun description(): String
}

class SimpleCoffee : Coffee {
    override fun cost() = 1.0
    override fun description() = "Simple coffee"
}

abstract class CoffeeDecorator(private val coffee: Coffee) : Coffee {
    override fun cost() = coffee.cost()
    override fun description() = coffee.description()
}

class MilkDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun cost() = super.cost() + 0.5
    override fun description() = super.description() + ", milk"
}

class SugarDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun cost() = super.cost() + 0.2
    override fun description() = super.description() + ", sugar"
}

// Использование
val coffee = SugarDecorator(MilkDecorator(SimpleCoffee()))
println(coffee.description())  // "Simple coffee, milk, sugar"
println(coffee.cost())  // 1.7
```

Паттерн **Decorator** позволяет динамически добавлять функциональность к объектам.

### Реализация паттерна Observer

**Пример использования паттерна **Observer**:**

```kotlin
interface Observer<T> {
    fun update(data: T)
}

class Subject<T> {
    private val observers = mutableListOf<Observer<T>>()

    fun attach(observer: Observer<T>) {
        observers.add(observer)
    }

    fun detach(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun notify(data: T) {
        observers.forEach { it.update(data) }
    }
}

// Использование
class UserObserver : Observer<String> {
    override fun update(data: String) {
        println("User updated: $data")
    }
}

val subject = Subject<String>()
subject.attach(UserObserver())
subject.notify("User created")
```

Паттерн **Observer** позволяет объектам уведомлять других об изменениях состояния.

### Реализация паттерна Command

**Пример использования паттерна **Command**:**

```kotlin
interface Command {
    fun execute()
    fun undo()
}

class AddUserCommand(private val repository: UserRepository, private val user: User) : Command {
    private var savedUser: User? = null

    override fun execute() {
        savedUser = repository.save(user)
    }

    override fun undo() {
        savedUser?.let { repository.delete(it.id) }
    }
}

class CommandInvoker {
    private val history = mutableListOf<Command>()

    fun execute(command: Command) {
        command.execute()
        history.add(command)
    }

    fun undo() {
        if (history.isNotEmpty()) {
            history.removeLast().undo()
        }
    }
}
```

Паттерн **Command** инкапсулирует запросы как объекты, позволяя откладывать выполнение и поддерживать отмену операций.

Этот файл содержит полное руководство по дополнительным возможностям **Kotlin**, покрывающее все основные аспекты от базовых техник до продвинутых подходов, включая сериализацию, метапрограммирование, интеграцию с внешними системами, паттерны проектирования (Factory, `Builder`, `Singleton`, `Adapter`, `Facade`, `Observer`, `Command`, `Template Method`, `Visitor`, `Chain of Responsibility`, State), работу с микросервисами, оптимизацию производительности, практические примеры использования, включая паттерны **Observer** и **Command**, заключение, дополнительные ресурсы и итоговые рекомендации.

## См. также

- [[kotlin-basics|Основы Kotlin — Полное руководство]]
- [[kotlin-collections-grouping|Kotlin Collections: Grouping and Aggregation]]
- [[kotlin-collections-list|Kotlin Collections: List]]
- [[kotlin-collections-map|Kotlin Collections: Map]]
- [[kotlin-collections-operations|Kotlin Collections: Operations]]
