---
title: "Основы программирования"
description: "Точка входа в раздел основ: переменные, типы, управляющие конструкции, функции, ООП, обработка ошибок и парадигмы с примерами на Java."
tags:
  - meta
  - index
  - programming-basics
type: "index"
updated: "2026-04-17"
---
# Основы программирования

Раздел собирает базу, без которой остальные материалы репозитория не ложатся на практику: переменные и типы, управляющие конструкции, функции, ООП-концепции, обработка ошибок, парадигмы (императивная, функциональная, декларативная). Примеры на Java, но подходы применимы к любому мэйнстрим-языку.

Для кого: начинающие разработчики и инженеры, переходящие с другого языка на Java. Остальные шпаргалки репозитория предполагают, что эти концепции уже знакомы.

## Полезные ссылки

### Основной документ
- [Основы программирования (на примере Java)](programming-basics.md)

### Соседние разделы
- [Computer Science](../computer-science/README.md)
- [Алгоритмы и структуры данных](../../algorithms/README.md)
- [Паттерны проектирования](../../patterns/README.md)
- [Java](../../languages/java/README.md)
- [Принципы проектирования](../../architecture/design-principles/README.md)

### Внешние ресурсы
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se21/html/)
- [Baeldung — Java tutorials](https://www.baeldung.com/java-tutorial)
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Clean Code (Robert Martin)](https://www.oreilly.com/library/view/clean-code-a/9780136083238/) — читаемость и поддерживаемость

## Содержание

- [Карта тем](#карта-тем)
- [Что внутри документа](#что-внутри-документа)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Где изучать глубже |
|------|-------------------|
| Переменные, типы, литералы | [programming-basics.md](programming-basics.md#типы-данных) |
| Управляющие конструкции, циклы | [programming-basics.md](programming-basics.md#управляющие-конструкции) |
| Функции/методы, параметры, возврат | [programming-basics.md](programming-basics.md#функции-и-модули) |
| ООП: классы, объекты, наследование, полиморфизм | [programming-basics.md](programming-basics.md#объектно-ориентированное-программирование) |
| Обработка ошибок, исключения | [programming-basics.md](programming-basics.md#обработка-ошибок) |
| Парадигмы: императивная, функциональная, декларативная | [programming-basics.md](programming-basics.md#парадигмы-программирования) |
| SOLID и принципы | [architecture/design-principles/](../../architecture/design-principles/README.md) |
| Design Patterns | [patterns/](../../patterns/README.md) |

## Что внутри документа

[programming-basics.md](programming-basics.md) содержит:

- Что такое программа, компиляция и выполнение (bytecode, JIT)
- Переменные и константы, области видимости, scope
- Примитивные и ссылочные типы, boxing/unboxing
- Операторы, приоритеты, выражения
- Условные операторы (`if/switch`), циклы (`for/while/do`)
- Методы, перегрузка, параметры по значению/ссылке
- Классы и объекты, конструкторы, инкапсуляция
- Наследование, полиморфизм, абстрактные классы и интерфейсы
- Исключения checked/unchecked, try-with-resources
- Функциональные интерфейсы, лямбды, Stream API

## Маршруты чтения

- **С нуля (3 ч):** весь документ по порядку, с практическим написанием кода.
- **Переход с Python/JS на Java:** `Типы → ООП → Исключения → Парадигмы`, затем [languages/java/](../../languages/java/README.md).
- **Подготовка к junior-интервью:** весь документ + [algorithms/data-structures/](../../algorithms/data-structures/README.md).

## Куда идти дальше

- Глубже в Java: JVM, GC, Collections — [languages/java/](../../languages/java/README.md)
- Паттерны проектирования (GoF) — [patterns/](../../patterns/README.md)
- SOLID, DRY, KISS, YAGNI — [architecture/design-principles/](../../architecture/design-principles/README.md)
- Алгоритмы и структуры данных — [algorithms/](../../algorithms/README.md)
- Тестирование и TDD — [testing/unit-testing/](../../testing/unit-testing/README.md)
