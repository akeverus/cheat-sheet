---
title: "Структурные паттерны"
description: "Точка входа в структурные GoF-паттерны: как собирать объекты в крупные структуры, адаптировать интерфейсы и управлять доступом."
tags:
  - meta
  - index
  - patterns
  - structural
type: "index"
updated: "2026-04-17"
---
# Структурные паттерны

Структурные паттерны из каталога GoF описывают, как из мелких объектов собирать крупные структуры, совмещать несовместимые интерфейсы, добавлять обязанности динамически и контролировать доступ к ресурсам. Раздел покрывает все семь классических структурных паттернов с Java-реализациями и примерами из JDK, Spring и JPA.

Для кого: разработчики, интегрирующие чужие библиотеки, проектирующие фасады для легаси, применяющие proxy для AOP/кеширования или строящие иерархические модели (дерево компонентов, файловая система, меню).

## Полезные ссылки

### Основные документы
- [Обзор структурных паттернов](structural-patterns.md)
- [Adapter](adapter.md) — адаптация несовместимых интерфейсов
- [Bridge](structural-patterns.md) — отделение абстракции от реализации (в обзоре)
- [Composite](composite.md) — дерево объектов, единый интерфейс для листьев и узлов
- [Decorator](decorator.md) — динамическое расширение поведения
- [Facade](facade.md) — упрощённый фасад над сложной подсистемой
- [Flyweight](flyweight.md) — разделение состояния для экономии памяти
- [Proxy](proxy.md) — контроль доступа, ленивая инициализация, AOP

### Соседние разделы
- [Patterns](../README.md) — корень паттернов
- [Creational](../creational/README.md)
- [Behavioral](../behavioral/README.md)
- [Concurrency](../concurrency-patterns/README.md)
- [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md)

### Внешние ресурсы
- [Refactoring.Guru: Structural Patterns](https://refactoring.guru/design-patterns/structural-patterns)
- [Java Design Patterns — Structural](https://java-design-patterns.com/patterns/#structural)
- [Spring AOP Reference](https://docs.spring.io/spring-framework/reference/core/aop.html) — Proxy в действии

## Содержание

- [Карта паттернов](#карта-паттернов)
- [Когда применять: паттерн и задача](#когда-применять-паттерн-и-задача)
- [Близкие паттерны: как выбрать](#близкие-паттерны-как-выбрать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта паттернов

```text
┌────────────────────────────────────────────────────────────┐
│                  Structural GoF                            │
├──────────────────────┬─────────────────────────────────────┤
│ Совмещение интерфейсов│ Adapter, Bridge                    │
├──────────────────────┼─────────────────────────────────────┤
│ Композиция           │ Composite, Decorator                 │
├──────────────────────┼─────────────────────────────────────┤
│ Упрощение API        │ Facade                               │
├──────────────────────┼─────────────────────────────────────┤
│ Управление ресурсом  │ Proxy, Flyweight                     │
└──────────────────────┴─────────────────────────────────────┘
```

## Когда применять: паттерн и задача

| Паттерн | Применяйте, когда |
|---------|-------------------|
| Adapter | Нужно подключить класс с несовместимым интерфейсом к существующему коду (`Arrays.asList`, legacy SDK) |
| Bridge | Иерархия расходится по двум осям (абстракция × платформа); нужно разделить их, чтобы избежать комбинаторного взрыва классов |
| Composite | Нужно одинаково обрабатывать отдельный объект и группу объектов (UI, меню, файловая система, AST) |
| Decorator | Нужно добавлять обязанности динамически без наследования (`BufferedInputStream`, Spring HTTP-interceptor) |
| Facade | Сложная подсистема должна иметь простую точку входа (`JdbcTemplate`, Spring `Environment`) |
| Flyweight | Много одинаковых объектов, внутреннее состояние можно разделить (`Integer.valueOf`, `String.intern`, кэши) |
| Proxy | Нужен контроль доступа, ленивая инициализация, логирование, кеш, удалённый вызов (Hibernate lazy, Spring AOP, RMI) |

## Близкие паттерны: как выбрать

- **Adapter vs Decorator:** Adapter меняет интерфейс; Decorator сохраняет его и добавляет поведение.
- **Decorator vs Proxy:** Decorator расширяет функциональность для клиента; Proxy контролирует доступ, но для клиента работает «прозрачно».
- **Facade vs Adapter:** Facade упрощает работу с целой подсистемой; Adapter адаптирует один несовместимый интерфейс.
- **Composite vs Decorator:** Composite строит дерево; Decorator — линейная цепочка обёрток.
- **Flyweight vs Singleton:** Singleton — один объект в системе; Flyweight — множество одинаковых объектов с разделяемым immutable состоянием.

## Маршруты чтения

- **Быстрый старт:** `Facade` -> `Decorator` -> `Adapter` -> `Proxy`.
- **Подготовка к собеседованию:** обзор + все семь паттернов + [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md).
- **Оптимизация памяти:** `Flyweight` -> анализ hot allocations в [../../monitoring/apm/README.md](../../monitoring/apm/README.md).

## Куда идти дальше

- Поведенческие паттерны — [../behavioral/README.md](../behavioral/README.md)
- Порождающие паттерны — [../creational/README.md](../creational/README.md)
- Spring AOP и proxy-бины — [../../frameworks/java-frameworks/spring/spring-aop.md](../../frameworks/java-frameworks/spring/spring-aop.md)
- Архитектурные паттерны уровня системы — [../../architecture/README.md](../../architecture/README.md)
