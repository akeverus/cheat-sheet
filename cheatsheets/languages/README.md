---
title: "Языки программирования"
description: "Раздел содержит шпаргалки по языкам: Go, Java, Kotlin, Scala — основы, коллекции, конкурентность и смежные темы."
tags: ["meta", "index"]
type: "index"
updated: "2026-02-11"
---
# Языки программирования

Раздел содержит шпаргалки по языкам: Go, Java, Kotlin, Scala — основы, коллекции, конкурентность и смежные темы.

Библиотеки и фреймворки (Cats, ZIO, Akka, Play, Ktor, Exposed и др.) см. в [`../libraries/`](../libraries/).

## Полезные ссылки

[Java](java/README.md)
[Kotlin](kotlin/README.md)
[Scala](scala/README.md)
[Go](go/README.md)

## Go (`go/`)

- [Обзор Go](go/README.md)
- [Основы](go/go-basics.md) — синтаксис, типы, функции, структуры, интерфейсы, конкурентность
- [Модули](go/go-modules.md) — управление зависимостями
- [Конкурентность](go/go-concurrency.md) — горутины, каналы, sync
- [Тестирование](go/go-testing.md) — тесты, бенчмарки, моки
- [Стандартная библиотека](go/go-stdlib-http.md) — net/http, encoding/json, io
- [Лучшие практики](go/go-best-practices.md) — идиоматичный Go

## Java (`java/`)

- [Обзор Java](java/README.md)
- [Основы](java/java-basics.md) — версии 11–17, ООП, коллекции, Stream API, NIO, многопоточность
- [Коллекции](java/java-collections-list.md) — List, Map, Set, Queue
- [Конкурентность](java/java-concurrency-basics.md) — потоки, ExecutorService, concurrent collections
- [Stream API и ФП](java/java-streams-fp.md) — потоки, лямбды, Optional
- [Исключения](java/java-exceptions.md) — обработка, лучшие практики
- [Reactive](java/java-reactive-project-reactor.md) — Project Reactor, RxJava

## Kotlin (`kotlin/`)

- [Обзор Kotlin](kotlin/README.md)
- [Основы](kotlin/kotlin-basics.md) — синтаксис, null-safety, классы, объекты
- [Коллекции](kotlin/kotlin-collections-list.md) — List, Set, Map, sequences, операции
- [Корутины](kotlin/kotlin-concurrency-basics.md) — корутины, Flow, каналы
- [ФП](kotlin/kotlin-fp-basics.md) — функции высшего порядка, неизменяемость
- [Тестирование](kotlin/kotlin-testing.md) — JUnit, MockK
- [Spring](kotlin/kotlin-spring.md) — интеграция с Spring

## Scala (`scala/`)

- [Обзор Scala](scala/README.md)
- [Основы](scala/scala-basics.md) — синтаксис, объекты, трейты, case classes
- [Коллекции](scala/scala-collections.md) — List, Set, Map, Vector, операции
- [ФП](scala/scala-fp-basics.md) — функции, иммутабельность, for-comprehensions
- [Конкурентность](scala/scala-concurrency.md) — Futures, Akka
