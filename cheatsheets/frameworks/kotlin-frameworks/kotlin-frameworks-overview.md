---
title: "Kotlin Frameworks — обзор"
description: "Кратко: обзор фреймворков и библиотек для Kotlin: Ktor, Spring (Kotlin), Exposed, kotlinx.serialization, kotlinx.coroutines, Koin, веб-API и практики."
tags: ["frameworks", "kotlin-frameworks", "kotlin-frameworks-overview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Frameworks — обзор

Кратко: обзор фреймворков и библиотек для **Kotlin**: **Ktor**, **Spring** (Kotlin), **Exposed**, **kotlinx.serialization**, **kotlinx.coroutines**, **Koin**, веб-**API** и практики.

**Дата последнего обновления:** 2026-02-11

## Полезные ссылки

### Официальная документация
- [Kotlin](https://kotlinlang.org/)
- [Ktor](https://ktor.io/)
- [Exposed](https://github.com/JetBrains/Exposed)

### См. также
- [Frameworks README](../README.md) — раздел фреймворков
- [Java Frameworks](../java-frameworks/README.md) — **Spring**, **Quarkus**
- [Kotlin (languages)](../../languages/kotlin/README.md) — язык **Kotlin**

## Содержание

- [Введение](#введение)
- [Ktor](#ktor)
- [Spring с Kotlin](#spring-с-kotlin)
- [Exposed](#exposed)
- [Kotlinx библиотеки](#kotlinx-библиотеки)
- [Koin и DI](#koin-и-di)
- [Сравнение и выбор](#сравнение-и-выбор)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)

---

## Введение

**Kotlin** на **JVM** часто используют с **Spring Boot** (полная экосистема) или с **Ktor** (легковесный асинхронный фреймворк от **JetBrains**). **Exposed** — **DSL** для работы с БД; **kotlinx.coroutines**, **kotlinx.serialization** — стандартные решения для асинхронности и **JSON**. Документ даёт обзор основных фреймворков и связей с языком **Kotlin**.

**Ключевые понятия:** **Ktor**, **Exposed**, **coroutines**, **serialization**, **Koin**, **Spring Boot**.

---

## Ktor

**Ktor** — асинхронный фреймворк для **Kotlin**: плагины (**middleware**), маршрутизация, **Content Negotiation**, **Authentication**, клиент и сервер.

**Зависимости (Gradle):**
```kotlin
// Зависимости Ktor для сервера и JSON
implementation("io.ktor:ktor-server-core-jvm:2.3.0")
implementation("io.ktor:ktor-server-netty-jvm:2.3.0")
implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.0")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0")
```

**Пример сервера:**
```kotlin
// Встроенный сервер Ktor на Netty с маршрутизацией
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        routing {
            get("/") { call.respondText("Hello") }
            get("/api/items/{id}") {
                val id = call.parameters["id"]
                call.respond(mapOf("id" to id))
            }
        }
    }.start(wait = true)
}
```

**Особенности:** корутины из коробки, плагинная архитектура, **Kotlin**-first **API**.

---

## Spring с Kotlin

**Spring Boot** полностью поддерживает **Kotlin**: **Kotlin DSL** для конфигурации, корутины (**spring-boot-starter-webflux**), **null-safety**. Многие проекты выбирают **Spring** для готовой экосистемы (**Security**, **Data**, **Cloud**) и пишут код на **Kotlin**. См. [Spring](../java-frameworks/spring/README.md) и [Kotlin Spring](../../languages/kotlin/kotlin-spring.md).

---

## Exposed

**Exposed** — библиотека **JetBrains** для работы с БД: **DSL** для типовых запросов и **DAO**-подобный слой. Поддержка **PostgreSQL**, **MySQL**, **SQLite**, **H2**.

**Пример:**
```kotlin
// Таблица Exposed и примеры SELECT/INSERT
object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}
// SELECT
TransactionManager.default.exec { Users.selectAll().toList() }
// INSERT
TransactionManager.default.exec { Users.insert { it[name] = "Alice" } }
```

---

## Kotlinx библиотеки

**kotlinx.coroutines** — корутины, `async`/`await`, `Flow`. **kotlinx.serialization** — сериализация в **JSON** (и другие форматы) на основе **Kotlin**-классов без рефлексии в runtime. **kotlinx-datetime** — дата/время. Эти библиотеки естественно сочетаются с **Ktor** и нативным **Kotlin**-стеком.

---

## Koin и DI

**Koin** — лёгкий **dependency injection** для **Kotlin**: модули, `single`, `factory`, `viewModel`. Удобен с **Ktor** и **Android**. Для **Spring** используют встроенный **DI** контейнер.

---

## Сравнение и выбор

| Подход | Когда использовать |
|--------|--------------------|
| **Ktor** | Легковесный асинхронный **API**, чистый **Kotlin**, корутины |
| **Spring Boot** | Enterprise, готовые модули (**Security**, **Data**, **Cloud**), большая команда |
| **Exposed** | Простая работа с БД без **JPA**, **DSL** |

---

## Лучшие практики

- Использовать корутины для асинхронности; не блокировать диспетчер по умолчанию.
- **Ktor**: выносить плагины и маршруты в отдельные модули.
- **Exposed**: использовать транзакции и миграции (например, **Flyway**).
- **kotlinx.serialization**: задавать имена полей и стратегии по умолчанию для **API**.

---

## Решение проблем

| Проблема | Действие |
|----------|----------|
| **Ktor** не стартует | Проверить порт и плагины (ContentNegotiation для **JSON**) |
| **Exposed** исключения | Проверить драйвер БД и строку подключения |
| Корутины не отменяются | Использовать `CoroutineScope` с `Job` и отмена при завершении |

---

## Частые вопросы

**Ktor или Spring?** **Ktor** — легковес, **Kotlin**-идиоматичность. **Spring** — экосистема, много готовых решений. Выбор по масштабу и предпочтениям команды.

**Exposed vs JPA?** **Exposed** — типобезопасный **DSL**, меньше «магии». **JPA** — стандарт, **Hibernate**, сложные маппинги. Для простых **CRUD** и **Kotlin** часто выбирают **Exposed**.

---

## Глоссарий

| Термин | Описание |
|--------|----------|
| **Ktor** | Асинхронный веб-фреймворк для **Kotlin** (JetBrains) |
| **Exposed** | **DSL** и слой доступа к БД для **Kotlin** |
| **kotlinx.coroutines** | Библиотека корутин для **Kotlin** |
| **kotlinx.serialization** | Сериализация **JSON** и др. для **Kotlin** |
| **Koin** | Лёгкий **DI** контейнер для **Kotlin** |
| **Content Negotiation** | Согласование формата ответа (**JSON**, **XML**) |

---

## Заключение

**Kotlin**-экосистема предлагает **Ktor** для легковесных асинхронных приложений и **Spring** для enterprise. **Exposed**, **kotlinx** и **Koin** дополняют стек. См. [Frameworks README](../README.md) и [Kotlin (languages)](../../languages/kotlin/README.md).

---

