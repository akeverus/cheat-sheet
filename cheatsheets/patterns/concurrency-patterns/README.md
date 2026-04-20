---
title: "Паттерны многопоточности"
description: "Точка входа в паттерны конкурентного программирования: пулы потоков, фьючи, Producer-Consumer, блокировки, синхронизация."
tags:
  - meta
  - index
  - patterns
  - concurrency
type: "index"
updated: "2026-04-17"
---
# Паттерны многопоточности

Раздел собирает классические паттерны конкурентного программирования: как управлять потоками, разделять ресурсы, передавать данные между производителями и потребителями, организовывать асинхронные вычисления и безопасно инициализировать разделяемое состояние. Примеры ориентированы на JVM (`java.util.concurrent`, `CompletableFuture`, `Executor`).

Для кого: разработчики серверных Java/Kotlin-приложений, которые проектируют пулы, очереди, кэши и асинхронные пайплайны и хотят избегать классических ошибок (race condition, deadlock, ложная видимость, перегрузка пула).

## Полезные ссылки

### Основные документы
- [[active-object|Active Object]] — отделение вызова метода от его выполнения
- [[double-checked-locking|Double-Checked Locking]] — ленивая инициализация с `volatile`
- [[future-task|Future/Task]] — асинхронные результаты, `Future`/`CompletableFuture`
- [[producer-consumer]] — разделение производителей и потребителей через очередь
- [[read-write-lock|Read-Write Lock]] — параллельное чтение, эксклюзивная запись
- [[thread-pool|Thread Pool]] — переиспользование потоков через пул

### Соседние разделы
- [[README|Patterns]] — корень паттернов
- [[README|Behavioral]]
- [[java-concurrency-basics|Java Concurrency (basics)]]
- [[java-concurrency-advanced|Java Concurrency (advanced)]]
- [Architecture: scalability](../../architecture/scalability/README.md)

### Внешние ресурсы
- [Java Concurrency in Practice (Goetz)](https://jcip.net/)
- [Doug Lea: Concurrent Programming in Java](https://gee.cs.oswego.edu/dl/cpj/)
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [Refactoring.Guru — многопоточность](https://refactoring.guru/ru/design-patterns)

## Содержание

- [Карта паттернов](#карта-паттернов)
- [Когда применять: паттерн и задача](#когда-применять-паттерн-и-задача)
- [Типичные ошибки](#типичные-ошибки)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта паттернов

```text
┌────────────────────────────────────────────────────────────┐
│                Concurrency Patterns                         │
├──────────────────────┬─────────────────────────────────────┤
│ Ресурсы и пулы       │ Thread Pool, Active Object           │
├──────────────────────┼─────────────────────────────────────┤
│ Обмен данными        │ Producer-Consumer                    │
├──────────────────────┼─────────────────────────────────────┤
│ Синхронизация        │ Read-Write Lock, Double-Checked Lock │
├──────────────────────┼─────────────────────────────────────┤
│ Асинхронные вычисления│ Future/Task, CompletableFuture      │
└──────────────────────┴─────────────────────────────────────┘
```

## Когда применять: паттерн и задача

| Паттерн | Применяйте, когда |
|---------|-------------------|
| Thread Pool | Много коротких задач, дорого создавать поток на каждую; нужна backpressure через очередь |
| Producer-Consumer | Нужно развязать темп производства и потребления через буфер (`BlockingQueue`, Kafka, Disruptor) |
| Future/Task | Требуется неблокирующий результат вычисления, композиция (`thenApply`, `thenCompose`, `allOf`) |
| Active Object | Нужно скрыть многопоточность за простым API, сериализовать доступ к объекту (actor-подобный подход) |
| Read-Write Lock | Много параллельных чтений, редкие записи (кэш конфигурации, справочники) |
| Double-Checked Locking | Ленивая инициализация дорогого singleton-а с минимальным оверхедом после первой инициализации |

## Типичные ошибки

- Использование `new Thread(...)` вместо пула в hot-path (GC, latency).
- Неограниченная `LinkedBlockingQueue` в `ThreadPoolExecutor` — OOM под нагрузкой.
- DCL без `volatile` — Java Memory Model допускает видимость полуинициализированного объекта.
- Один общий пул на блокирующие IO и CPU-задачи — голодание CPU-задач.
- Вложенные submit в один пул — deadlock по fixed-size пулам.
- `Future.get()` без таймаута — зависание на сбойном апстриме.

## Маршруты чтения

- **База для backend-разработчика:** `Thread Pool` -> `Producer-Consumer` -> `Future/Task`.
- **Подготовка к собеседованию по concurrency:** вся секция, плюс `java-concurrency-basics.md` и `java-concurrency-advanced.md`.
- **Оптимизация hot-path:** `Double-Checked Locking` -> `Read-Write Lock` -> профилирование в [[README]].

## Куда идти дальше

- Реактивные стеки — [[java-concurrency-advanced]]
- Архитектура и масштабирование — [../../architecture/scalability/README.md](../../architecture/scalability/README.md)
- Очереди и стриминг — [[README]]
- Вопросы на собеседовании — [../../interview/frameworks/java/java-concurrency-interview.md](../../interview/frameworks/java/java-concurrency-interview.md)
