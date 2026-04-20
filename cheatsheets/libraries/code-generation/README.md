---
title: "Code Generation Libraries"
description: "Точка входа в раздел библиотек кодогенерации JVM: Lombok (boilerplate), MapStruct (маппинг)."
tags:
  - meta
  - index
  - code-generation
  - libraries
type: "index"
updated: "2026-04-17"
---
# Code Generation Libraries

Раздел собирает библиотеки, которые генерируют Java/Kotlin-код на этапе компиляции через annotation processor. Их задача — убрать boilerplate (геттеры/сеттеры, equals/hashCode, конструкторы, маппинги DTO↔entity) и снизить вероятность рутинных ошибок. Внутри сейчас — Lombok и MapStruct, два де-факто стандарта JVM-экосистемы.

Для кого: Java-разработчики, уменьшающие объём рукописного кода; команды, стандартизирующие маппинг слоёв; инженеры, разбирающиеся в compile-time-обработке аннотаций.

## Полезные ссылки

### Основные документы
- [Lombok](java-lombok.md) — `@Data`, `@Builder`, `@Slf4j` и ловушки
- [MapStruct](java-mapstruct.md) — type-safe маппинг DTO/entity

### Соседние разделы
- [Libraries](../README.md)
- [Java-библиотеки](../java/README.md)
- [Тестирование](../testing-libraries/README.md)
- [Spring Boot](../../frameworks/spring/spring-boot.md) — где Lombok/MapStruct чаще всего применяются

### Внешние ресурсы
- [Project Lombok](https://projectlombok.org/)
- [MapStruct](https://mapstruct.org/)
- [JLS Annotation Processing](https://docs.oracle.com/javase/specs/jls/se17/html/jls-8.html)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Сравнение](#сравнение)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Lombok: аннотации, кейсы, конфликт с Records/sealed/Kotlin
- MapStruct: генерация реализации interface-mapper'а, `@Mapping`, стратегии null, вложенные маппинги
- Интеграция с Gradle/Maven annotation processor pipeline
- Совместное использование Lombok + MapStruct (порядок процессоров)

## Когда использовать

- **Lombok** — нужно быстро избавиться от boilerplate в POJO/entity. Осторожно в публичных API (декомпиляция теряется), в Records больше не нужен.
- **MapStruct** — есть слоистая архитектура (entity/DTO/domain) и надо перекладывать поля. Быстрее, чем ModelMapper (generates code, не использует reflection).
- Альтернативы: **Kotlin data classes** (в Kotlin-проектах не нужен Lombok), **Java Records** (вместо `@Value`), **ручной маппинг** или **ModelMapper** (если допустимо reflection).

## Сравнение

| Критерий | Lombok | MapStruct |
|----------|--------|-----------|
| Задача | убрать boilerplate | генерировать мапперы |
| Механизм | byte-code manipulation (AP) | чистый code-gen (AP → `.java`) |
| Reflection в runtime | нет | нет |
| Совместимость с Records | ограниченная | полная |
| Отладка | сложнее (сгенерированный код невидим в source) | проще (код видно в build/generated) |

## Маршруты чтения

- **Быстрое знакомство:** `java-lombok.md` (секции «Основные аннотации» и «Ловушки») → `java-mapstruct.md` («Основы» + «Маппинг вложенных объектов»).
- **Настройка проекта:** секция «Установка и настройка» в обоих файлах + документация по annotation processor.
- **Решение конфликтов:** секция "Lombok + MapStruct" в `java-mapstruct.md`.

## Куда идти дальше

- Java-утилиты — [libraries/java](../java/README.md)
- Сериализация — [libraries/serialization](../serialization/README.md)
- Spring-интеграция — [frameworks/java-frameworks/spring](../../frameworks/java-frameworks/spring/README.md)
