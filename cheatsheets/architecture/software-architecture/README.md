---
title: "Software Architecture"
description: "Точка входа в раздел архитектурных стилей: монолит, микросервисы, модульный монолит и сопутствующие паттерны."
tags:
  - meta
  - index
  - architecture
  - software-architecture
type: "index"
updated: "2026-04-20"
---
# Software Architecture

Раздел посвящён высокоуровневым стилям построения систем: как разбивать её на сервисы (или не разбивать), как организовать коммуникацию, где проходят границы транзакций, устойчивости и владения данными. Ответ «нужно ли здесь микросервисы» почти всегда зависит от контекста — задача раздела дать набор признаков, по которым этот контекст читается.

Для кого: инженеры, принимающие решение о стиле архитектуры нового сервиса, и те, кто упёрся в боль текущей архитектуры (бутылочное горлышко монолита, взрыв сложности микросервисов) и ищет, как её декомпозировать.

## Полезные ссылки

### Основные документы
- [[microservices]] — микросервисная архитектура: принципы, паттерны, API Gateway, Service Discovery, Circuit Breaker

### Соседние разделы
- [[README|Родительский раздел: Architecture]]
- [[README|Design Principles]] — SRP, DIP и их отражение в границах сервисов
- [[README|System Design]] — проектирование конкретных систем
- [[README|ADR]] — фиксация выбранного стиля
- [Enterprise Patterns](../enterprise-patterns/) — DDD, CQRS, Event Sourcing
- [[README|Design Patterns]]
- [[README|Messaging]] — коммуникация между сервисами
- [[README|Kubernetes]] — runtime для микросервисов
- [[README|Observability]]

### Внешние ресурсы
- [Microservices — Martin Fowler](https://martinfowler.com/articles/microservices.html)
- [Microservices.io — Chris Richardson](https://microservices.io/)
- [MonolithFirst — Martin Fowler](https://martinfowler.com/bliki/MonolithFirst.html)
- [Fundamentals of Software Architecture (O'Reilly)](https://www.oreilly.com/library/view/fundamentals-of-software/9781492043447/)

## Содержание

- [Стили архитектуры кратко](#стили-архитектуры-кратко)
- [Когда какой стиль выбирать](#когда-какой-стиль-выбирать)
- [Карта тем](#карта-тем)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Стили архитектуры кратко

| Стиль | Где подходит | Основные риски |
|-------|--------------|----------------|
| Monolith | MVP, малые команды, единый домен | Сложность масштабирования отдельных частей, длинные релизы |
| Modular Monolith | Средние команды, чёткие модули, одна БД | Размытие границ модулей без дисциплины |
| Microservices | Большие команды, разные SLA, независимые релизы | Distributed complexity, консистентность, сетевые сбои |
| SOA | Корпоративные интеграции, ESB | Сложность шины, vendor lock-in |
| Serverless / FaaS | Event-driven, редкие нагрузки, ops-минимализм | Cold start, vendor lock-in, наблюдаемость |
| Event-Driven | Асинхронные интеграции, высокая пропускная способность | Сложность отладки, порядок сообщений |

## Когда какой стиль выбирать

Быстрый чек-лист «нужны ли микросервисы»:

- [ ] Есть несколько команд, которым нужен независимый релизный цикл
- [ ] Разные части системы имеют разные требования к SLA/масштабированию
- [ ] Монолит уже достиг потолка в билде, тестировании, деплое
- [ ] Есть зрелая практика наблюдаемости, CI/CD, автоматизации
- [ ] Готовность принять цену distributed complexity (сетевые сбои, eventual consistency)

Если большинство пунктов «нет» — начните с модульного монолита (`MonolithFirst`). Микросервисы часто становятся причиной problems, а не их решением.

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Определение и признаки микросервисов | [[microservices#введение-в-микросервисы]] |
| Монолит vs микросервисы | [[microservices#монолит-vs-микросервисы]] |
| Принципы проектирования (SRP, DDD, API-first) | [[microservices#принципы-проектирования]] |
| Паттерны: API Gateway, Service Discovery, Circuit Breaker | [[microservices#паттерны-микросервисов]] |
| Event-Driven, CQRS, Event Sourcing | [architecture/enterprise-patterns/](../enterprise-patterns/) |
| ADR для архитектурного выбора | [[README|architecture/architectural-decision-records/]] |

## Маршруты чтения

- **Junior/architect-start (1 день):** `microservices.md` `design-principles/` `patterns/`.
- **Выбор стиля для нового сервиса:** чек-лист выше `microservices.md` раздел «когда использовать» ADR.
- **Декомпозиция монолита:** DDD (bounded contexts) strangler fig pattern messaging.

## Куда идти дальше

- Принципы, на которых строятся границы сервисов — [[README|architecture/design-principles/]]
- System Design и масштабирование — [[README|architecture/system-design/]]
- Паттерны коммуникации и устойчивости — [[README|patterns/]]
- Инфраструктура для микросервисов — [[README|platform/containers/kubernetes/]], [[README|monitoring/]]
