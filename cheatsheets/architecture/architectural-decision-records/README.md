---
title: "Architectural Decision Records (ADR)"
description: "Точка входа в раздел ADR: формат, шаблон и процесс фиксации архитектурных решений."
tags:
  - meta
  - index
  - architecture
  - adr
type: "index"
updated: "2026-04-17"
---
# Architectural Decision Records (ADR)

Architectural Decision Record — короткий документ, фиксирующий контекст, варианты и обоснование одного архитектурного решения. ADR превращает неявный выбор (например, PostgreSQL вместо MongoDB, REST вместо gRPC) в отслеживаемую запись, которую можно найти через год и понять, почему команда поступила именно так.

Раздел для тех, кто начинает вести ADR в проекте или ищет готовый шаблон и минимальный процесс. Читать стоит, когда в репозитории назрели крупные архитектурные развилки, а решения теряются в чатах и комментариях к PR.

## Полезные ссылки

### Основные документы
- [adr-template.md](adr-template.md) — шаблон ADR с полным примером (PostgreSQL vs MongoDB)

### Соседние разделы
- [Родительский раздел: Architecture](../README.md)
- [Design Principles](../design-principles/README.md) — принципы, которые часто цитируются в ADR
- [Software Architecture](../software-architecture/README.md) — стили, между которыми выбирают в ADR
- [System Design](../system-design/README.md) — контекст для high-level решений
- [Design Patterns](../../patterns/README.md) — паттерны, которые всплывают в вариантах

### Внешние ресурсы
- [adr.github.io](https://adr.github.io/) — коллекция шаблонов MADR, Nygard, Y-Statements
- [Documenting Architecture Decisions — Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
- [ThoughtWorks Tech Radar: Lightweight ADR](https://www.thoughtworks.com/radar/techniques/lightweight-architecture-decision-records)

## Содержание

- [Что такое ADR и когда вести](#что-такое-adr-и-когда-вести)
- [Карта тем](#карта-тем)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что такое ADR и когда вести

ADR имеет смысл заводить, когда решение:

- касается контура сервиса или его ключевых интерфейсов (БД, протокол, границы сервисов);
- обратимо дорого (миграция БД, смена брокера сообщений, отказ от фреймворка);
- поднимается в команде повторно («а почему мы в прошлый раз выбрали X?»);
- затрагивает SLA, безопасность или compliance.

Не стоит заводить ADR на каждый рефакторинг, выбор библиотеки логирования или мелкое product-решение. ADR — про архитектурные развилки, а не про PR-описания.

Минимальная структура (по шаблону Nygard): Title, Status (proposed / accepted / superseded), Context, Decision, Consequences. MADR-шаблон расширяет это блоком «Considered Options» с таблицей сравнения.

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Полный шаблон ADR | [adr-template.md](adr-template.md#шаблон-adr) |
| Пример: PostgreSQL vs MongoDB | [adr-template.md](adr-template.md#adr-001-использование-postgresql-вместо-mongodb-для-основного-хранилища) |
| Варианты решения и их сравнение | [adr-template.md](adr-template.md#варианты-решения) |
| Последствия решения (trade-offs) | [adr-template.md](adr-template.md#последствия) |
| Связи между ADR (supersedes / relates-to) | [adr-template.md](adr-template.md#связанные-решения) |
| Лучшие практики ведения ADR | [adr-template.md](adr-template.md#лучшие-практики) |

## Маршруты чтения

- **Первый ADR в проекте (30 мин):** прочитать шаблон → скопировать в репозиторий → заполнить контекст и варианты по текущей развилке.
- **Встраивание в процесс команды:** определить каталог `docs/adr/`, привязать к PR-шаблону, выбрать формат номеров (ADR-0001), договориться про статус-поток.
- **Аудит существующих решений:** пройтись по ADR, отметить устаревшие как `superseded by ADR-NNNN`, связать родственные записи.

## Куда идти дальше

- Стили архитектуры, между которыми выбирают в ADR — [architecture/software-architecture/](../software-architecture/README.md)
- Принципы, которые цитируются в обосновании — [architecture/design-principles/](../design-principles/README.md)
- Паттерны как кандидаты в «варианты» — [patterns/](../../patterns/README.md)
- High-level проектирование — [architecture/system-design/](../system-design/README.md)
