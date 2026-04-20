---
title: "Design Principles"
description: "Точка входа в раздел принципов проектирования: SOLID, DRY, KISS, YAGNI и связанные эвристики."
tags:
  - meta
  - index
  - architecture
  - design-principles
type: "index"
updated: "2026-04-20"
---
# Design Principles

Принципы проектирования — это эвристики, которые помогают удерживать код и архитектуру в состоянии, пригодном для изменений. Они не заменяют анализ конкретного контекста, но задают общий язык: «здесь нарушается SRP», «это противоречит Liskov», «мы делаем в угоду YAGNI».

Раздел для инженеров, которые хотят системно разложить принципы по полкам — не как список лозунгов, а как инструменты с примерами нарушений и правильного применения. Читать перед проектированием модулей, код-ревью и техническим собеседованием.

## Полезные ссылки

### Основные документы
- [[design-principles]] — обзор всех принципов (SOLID, DRY, KISS, YAGNI, LoD, POLA)
- [[solid-principles]] — подробный разбор каждой буквы SOLID с Java-примерами

### Соседние разделы
- [[README|Родительский раздел: Architecture]]
- [[README|Design Patterns]] — конкретные паттерны, реализующие принципы
- [[README|Software Architecture]] — стили, где принципы проявляются на уровне системы
- [[refactoring-patterns-interview|Code Quality: Refactoring]] — как применять принципы при рефакторинге
- [[README|Clean Code practices]]

### Внешние ресурсы
- [Refactoring Guru — SOLID](https://refactoring.guru/design-patterns/solid-principles)
- [Baeldung — SOLID Principles in Java](https://www.baeldung.com/solid-principles)
- [Clean Code (Robert C. Martin)](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
- [The Pragmatic Programmer](https://pragprog.com/titles/tpp20/the-pragmatic-programmer-20th-anniversary-edition/)

## Содержание

- [Карта принципов](#карта-принципов)
- [Когда применять какой принцип](#когда-применять-какой-принцип)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта принципов

| Принцип | Что говорит | Где читать |
|---------|-------------|------------|
| SRP — Single Responsibility | У класса одна причина для изменения | [[solid-principles#s-single-responsibility]] |
| OCP — Open/Closed | Открыт для расширения, закрыт для модификации | [[solid-principles#o-openclosed]] |
| LSP — Liskov Substitution | Подкласс взаимозаменяем с базой без сюрпризов | [[solid-principles#l-liskov-substitution]] |
| ISP — Interface Segregation | Много маленьких интерфейсов вместо одного большого | [[solid-principles#i-interface-segregation]] |
| DIP — Dependency Inversion | Зависим от абстракций, не от реализаций | [[solid-principles#d-dependency-inversion]] |
| DRY | Одно знание — одно место | [[design-principles#dry-dont-repeat-yourself]] |
| KISS | Проще — лучше; сложность добавлять по доказательству | [[design-principles#kiss-keep-it-simple-stupid]] |
| YAGNI | Не строить обобщения «на всякий случай» | [[design-principles#yagni-you-arent-gonna-need-it]] |
| Principle of Least Astonishment | Код не должен удивлять читателя | [[design-principles#принцип-наименьшего-удивления]] |

## Когда применять какой принцип

- **При проектировании нового модуля** — SRP (границы ответственности) + DIP (инверсия зависимостей для тестируемости).
- **При расширении функциональности** — OCP: сначала попробовать расширить, потом уже менять существующее.
- **При наследовании** — LSP: проверить, что сабклассы не ломают контракт базы (не бросают новые checked-исключения, не сужают допустимый вход).
- **При появлении монструозных интерфейсов** — ISP: разбить на роли для разных клиентов.
- **Когда тянет «обобщить впрок»** — YAGNI: писать под реальный use case, не под воображаемый.
- **Перед добавлением абстракции** — KISS: простое решение + тест обычно бьёт «универсальное» с конфигами.

## Маршруты чтения

- **Быстрый обзор (45 мин):** `design-principles.md` целиком главы SRP/OCP в `solid-principles.md`.
- **Подготовка к собеседованию (2-3 ч):** весь `solid-principles.md` с примерами + вопросы из [[design-patterns-interview|interview/design-patterns]].
- **Применение на практике:** принципы паттерны практика код-ревью с упором на нарушения SRP/OCP.

## Куда идти дальше

- Паттерны как реализации принципов — [patterns/creational-patterns/](../../patterns/creational-patterns/README.md), [patterns/structural-patterns/](../../patterns/structural-patterns/README.md)
- Архитектурные стили — [[README|architecture/software-architecture/]]
- ADR-шаблон для фиксации принципиальных решений — [[README|architecture/architectural-decision-records/]]
- Рефакторинг и код-ревью — [interview/code-quality/](../../interview/code-quality/)
