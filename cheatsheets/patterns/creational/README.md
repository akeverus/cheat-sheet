---
title: "Порождающие паттерны"
description: "Точка входа в порождающие GoF-паттерны: как создавать объекты гибко, управляемо и без жёстких зависимостей от конкретных классов."
tags:
  - meta
  - index
  - patterns
  - creational
type: "index"
updated: "2026-04-17"
---
# Порождающие паттерны

Порождающие паттерны из каталога GoF отвечают за создание объектов: инкапсулируют выбор конкретной реализации, отделяют процесс конструирования от представления, управляют единственностью экземпляра и копированием. Раздел покрывает все пять классических порождающих паттернов с Java-реализациями и ссылками на Spring, JDK и библиотеки.

Для кого: разработчики, проектирующие конструкторы сложных объектов (DTO с десятками полей, конфигурация клиентов, фабрики сервисов), а также инженеры, готовящиеся к собеседованиям по design patterns.

## Полезные ссылки

### Основные документы
- [Обзор порождающих паттернов](creational-patterns.md) — общая карта группы
- [Abstract Factory](abstract-factory.md) — семейство связанных объектов
- [Builder](builder.md) — пошаговое построение сложного объекта
- [Factory Method](factory-method.md) — делегирование создания подклассам
- [Prototype](prototype.md) — клонирование вместо конструирования
- [Singleton](singleton.md) — один экземпляр на приложение

### Соседние разделы
- [Patterns](../README.md) — корень паттернов
- [Structural](../structural/README.md)
- [Behavioral](../behavioral/README.md)
- [Concurrency](../concurrency-patterns/README.md)
- [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md)

### Внешние ресурсы
- [Refactoring.Guru: Creational Patterns](https://refactoring.guru/design-patterns/creational-patterns)
- [Java Design Patterns — Creational](https://java-design-patterns.com/patterns/#creational)
- [Effective Java (Item 2: Builder; Item 3: Singleton)](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)

## Содержание

- [Карта паттернов](#карта-паттернов)
- [Когда применять: паттерн и задача](#когда-применять-паттерн-и-задача)
- [Близкие паттерны: как выбрать](#близкие-паттерны-как-выбрать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта паттернов

```text
┌────────────────────────────────────────────────────────────┐
│                  Creational GoF                            │
├──────────────────────┬─────────────────────────────────────┤
│ Один объект          │ Singleton                            │
├──────────────────────┼─────────────────────────────────────┤
│ Один тип, вариации   │ Factory Method                       │
├──────────────────────┼─────────────────────────────────────┤
│ Семейство связанных  │ Abstract Factory                     │
├──────────────────────┼─────────────────────────────────────┤
│ Сложная сборка       │ Builder                              │
├──────────────────────┼─────────────────────────────────────┤
│ Копирование          │ Prototype                            │
└──────────────────────┴─────────────────────────────────────┘
```

## Когда применять: паттерн и задача

| Паттерн | Применяйте, когда |
|---------|-------------------|
| Abstract Factory | Нужно создавать семейство согласованных объектов (разные темы UI, SQL-диалекты, платформозависимые драйверы) |
| Builder | Объект имеет много опциональных полей или требует пошаговой сборки (запрос HTTP-клиента, `StringBuilder`, `Stream.Builder`) |
| Factory Method | Клиент не должен знать конкретный класс; создание делегируется методу (`Calendar.getInstance()`, Spring `FactoryBean`) |
| Prototype | Создать объект дорого (сеть, БД, сложная инициализация), но можно клонировать уже существующий (`Object.clone()`, сериализация) |
| Singleton | Требуется ровно один экземпляр с глобальным доступом (логгер, пул, конфигурация); осторожно в распределённых системах |

## Близкие паттерны: как выбрать

- **Factory Method vs Abstract Factory:** Factory Method создаёт один тип объекта; Abstract Factory — согласованную группу связанных объектов.
- **Builder vs Factory Method:** Builder для сложной пошаговой сборки с множеством опций; Factory Method — когда достаточно одного вызова.
- **Prototype vs Factory:** Prototype дешевле, если конструктор дорог; Factory чище, если объект нужно конфигурировать с нуля.
- **Singleton vs DI-контейнер:** в Spring используйте `@Component` с областью `singleton` вместо ручного паттерна; так проще тестировать.

## Маршруты чтения

- **Быстрый старт (1.5 ч):** `Singleton` -> `Factory Method` -> `Builder`.
- **Подготовка к собеседованию:** обзор + все пять паттернов + раздел [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md).
- **Проектирование API/SDK:** `Builder` -> `Abstract Factory` -> `Prototype` для immutable DTO и клиентских конфигураций.

## Куда идти дальше

- Поведенческие паттерны — [../behavioral/README.md](../behavioral/README.md)
- Структурные паттерны — [../structural/README.md](../structural/README.md)
- Spring IoC и бины — [../../frameworks/java-frameworks/spring/spring-core.md](../../frameworks/java-frameworks/spring/spring-core.md)
- Вопросы на собеседовании — [../../interview/design-patterns/design-patterns-interview.md](../../interview/design-patterns/design-patterns-interview.md)
