---
title: "Поведенческие паттерны"
description: "Точка входа в поведенческие GoF-паттерны: как объекты взаимодействуют, распределяют ответственность и реагируют на события."
tags:
  - meta
  - index
  - patterns
  - behavioral
type: "index"
updated: "2026-04-17"
---
# Поведенческие паттерны

Поведенческие паттерны из каталога GoF описывают, как объекты взаимодействуют между собой и распределяют ответственность: кто инициирует действие, кто реагирует, как проходит сообщение по цепочке обработчиков, как отделить алгоритм от данных. В этом разделе собраны все 11 классических поведенческих паттернов плюс практический `Delegate` в стиле Kotlin.

Для кого: разработчики, проектирующие модули с многошаговой логикой, событийными пайплайнами, машинами состояний или расширяемыми обработчиками. Шпаргалки этого раздела дают краткий обзор, Java-реализации, антипаттерны и связки с реальными API (Spring, JDK Collections, Java IO).

## Полезные ссылки

### Основные документы
- [Обзор поведенческих паттернов](behavioral-patterns.md) — общая карта группы и сравнения
- [Chain of Responsibility](chain-of-responsibility.md) — цепочка обработчиков
- [Command](command.md) — команда как объект, undo/redo
- [Iterator](iterator.md) — обход коллекции без раскрытия структуры
- [Mediator](mediator.md) — централизованный посредник
- [Memento](memento.md) — сохранение и восстановление состояния
- [Observer](observer.md) — publish/subscribe
- [State](state.md) — смена поведения по состоянию
- [Strategy](strategy.md) — взаимозаменяемые алгоритмы
- [Template Method](template-method.md) — скелет алгоритма в базовом классе
- [Visitor](visitor.md) — операции над иерархией без изменения классов
- [Interpreter](interpreter.md) — грамматика и её интерпретация
- [Delegate](delegate.md) — делегирование в стиле Kotlin

### Соседние разделы
- [Patterns](../README.md) — корень раздела паттернов
- [Creational](../creational/README.md) — порождающие паттерны
- [Structural](../structural/README.md) — структурные паттерны
- [Concurrency](../concurrency-patterns/README.md) — паттерны многопоточности
- [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md)

### Внешние ресурсы
- [Refactoring.Guru: Behavioral Patterns](https://refactoring.guru/design-patterns/behavioral-patterns)
- [GoF Design Patterns (1994)](https://en.wikipedia.org/wiki/Design_Patterns) — оригинальный каталог
- [Java Design Patterns (iluwatar)](https://java-design-patterns.com/patterns/#behavioral)

## Содержание

- [Карта паттернов](#карта-паттернов)
- [Когда применять: паттерн и задача](#когда-применять-паттерн-и-задача)
- [Как выбрать между близкими паттернами](#как-выбрать-между-близкими-паттернами)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта паттернов

```text
┌────────────────────────────────────────────────────────────────┐
│                     Behavioral GoF                             │
├──────────────────────┬─────────────────────────────────────────┤
│ Interaction          │ Chain of Responsibility, Mediator,      │
│                      │ Observer, Command                       │
├──────────────────────┼─────────────────────────────────────────┤
│ Algorithm / State    │ Strategy, State, Template Method        │
├──────────────────────┼─────────────────────────────────────────┤
│ Structure traversal  │ Iterator, Visitor, Interpreter          │
├──────────────────────┼─────────────────────────────────────────┤
│ Snapshot / undo      │ Memento, Command (undo)                 │
└──────────────────────┴─────────────────────────────────────────┘
```

## Когда применять: паттерн и задача

| Паттерн | Применяйте, когда |
|---------|-------------------|
| Chain of Responsibility | Запрос должен пройти через цепочку независимых обработчиков (фильтры HTTP, middleware, валидация) |
| Command | Нужно инкапсулировать действие как объект: очередь задач, undo/redo, макросы, transactional scripts |
| Interpreter | Есть маленький DSL или грамматика (SpEL, простые правила, выражения в конфиге) |
| Iterator | Нужно обойти коллекцию, не раскрывая внутреннюю структуру (JDK `Iterator`, Stream) |
| Mediator | Много объектов связаны N×N и это хаос — нужен один брокер коммуникации (UI-диалоги, chat room) |
| Memento | Требуется сохранить и откатить состояние (undo в редакторах, snapshot в играх) |
| Observer | Событие у одного объекта должно уведомить многих (pub/sub, listeners, reactive streams) |
| State | Поведение объекта сильно зависит от состояния, и `if`/`switch` разросся (конечные автоматы) |
| Strategy | Существуют взаимозаменяемые алгоритмы, которые нужно выбирать в runtime (сортировка, валидация, pricing) |
| Template Method | Общий скелет алгоритма с шагами, которые переопределяются подклассами (frameworks, `AbstractList`) |
| Visitor | Нужна операция над стабильной иерархией классов, а добавлять метод в каждый класс неудобно (AST, компиляторы) |
| Delegate | Нужно делегировать реализацию интерфейса другому объекту (Kotlin `by`, композиция вместо наследования) |

## Как выбрать между близкими паттернами

- **Strategy vs State:** Strategy — клиент выбирает алгоритм; State — объект сам меняет поведение при смене состояния.
- **Observer vs Mediator:** Observer — однонаправленное уведомление многих; Mediator — двунаправленная координация N×N через центр.
- **Command vs Strategy:** Command упаковывает действие с контекстом и аргументами (можно сериализовать, поставить в очередь); Strategy — чистый алгоритм без состояния.
- **Template Method vs Strategy:** Template Method — наследование + хуки; Strategy — композиция + полиморфизм.
- **Chain of Responsibility vs Decorator:** CoR может прервать цепочку; Decorator всегда вызывает следующий в цепочке.

## Маршруты чтения

- **Минимум для собеседования (2 ч):** `Strategy` -> `Observer` -> `Command` -> `Template Method` -> `State`.
- **Проектирование событийных систем:** `Observer` -> `Mediator` -> `Command` -> `Chain of Responsibility`.
- **Рефакторинг легаси с ветвлениями:** `Strategy` -> `State` -> `Visitor`.

## Куда идти дальше

- Структурные паттерны — [../structural/README.md](../structural/README.md)
- Порождающие паттерны — [../creational/README.md](../creational/README.md)
- Многопоточность — [../concurrency-patterns/README.md](../concurrency-patterns/README.md)
- Вопросы на собеседовании — [../../interview/design-patterns/design-patterns-interview.md](../../interview/design-patterns/design-patterns-interview.md)
- Архитектура приложений — [../../architecture/README.md](../../architecture/README.md)
