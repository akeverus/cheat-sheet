---
title: "System Design"
description: "Точка входа в раздел system design: процесс проектирования масштабируемых систем и связи с компонентами архитектуры."
tags:
  - meta
  - index
  - architecture
  - system-design
type: "index"
updated: "2026-04-17"
---
# System Design

System Design — это навык проектировать системы, которые выдерживают заявленную нагрузку, остаются работоспособными при отказах и адекватно развиваются со временем. Раздел даёт каркас: как разложить задачу на требования, прикинуть масштаб, выбрать компоненты (LB, БД, кэш, очередь, CDN) и честно обсудить trade-offs.

Для кого: инженеры, которые проектируют новый сервис или готовятся к system-design-интервью (URL shortener, newsfeed, chat, rate limiter). Раздел — overview; детали по каждому компоненту лежат в соседних доменах (`databases/`, `architecture/`, `patterns/`).

## Полезные ссылки

### Основные документы
- [[system-design-basics]] — введение в проектирование масштабируемых систем

### Соседние разделы
- [[README|Родительский раздел: Architecture]]
- [[README|Software Architecture]] — стили (монолит, микросервисы)
- [[README|Design Principles]] — SOLID, DRY, KISS
- [[README|ADR]] — фиксация принятых решений
- [[README|Design Patterns]] — строительные блоки
- [[README|Databases]] — выбор хранилища
- [[README|Web Backend]] — уровни backend
- [[README|Messaging]] — асинхронная интеграция
- [[README|Monitoring]] — метрики и SLO

### Внешние ресурсы
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability](http://highscalability.com/)
- [Designing Data-Intensive Applications (Kleppmann)](https://dataintensive.net/)
- [The System Design Interview (Alex Xu, Vol. 1-2)](https://www.amazon.com/System-Design-Interview-insiders-Second/dp/B08CMF2CQF)

## Содержание

- [Процесс system design](#процесс-system-design)
- [Ключевые компоненты](#ключевые-компоненты)
- [Карта тем](#карта-тем)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Процесс system design

Стандартный цикл собеседования и реального проектирования:

1. **Требования.** Функциональные (что делает) + нефункциональные (RPS, latency, доступность, объём данных).
2. **Оценка масштаба.** QPS (средний/пиковый), storage за N лет, bandwidth, connection pool.
3. **Высокоуровневая архитектура.** Клиент → LB → App → БД/Cache/Queue → CDN.
4. **Углубление.** Схема БД, шардирование, стратегия кэша, API, failure modes.
5. **Trade-offs.** CAP, консистентность vs доступность, push vs pull, sync vs async.

## Ключевые компоненты

| Компонент | Зачем | Где читать глубже |
|-----------|-------|-------------------|
| Load Balancer | Распределить трафик, скрыть отказы инстансов | [[load-balancing-interview|interview/architecture/load-balancing-interview]] |
| API Gateway | Единая точка входа, auth, rate-limit, routing | [[microservices|architecture/software-architecture/microservices.md]] |
| Database (SQL/NoSQL) | Persistence, запросы, транзакции | [[README|databases/]] |
| Cache (Redis, CDN) | Снизить latency и нагрузку на БД | [[caching-strategies-interview|interview/architecture/caching-strategies-interview]] |
| Message Queue / Log | Асинхрон, buffering, fan-out | [[README|development/messaging/]] |
| CDN | Раздача статики и edge-кэш | [[system-design-basics#cdn]] |

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Что такое System Design и зачем он нужен | [[system-design-basics#что-такое-system-design]] |
| Масштабируемость, надёжность, производительность | [[system-design-basics#основные-принципы]] |
| Компоненты: LB, API Gateway, БД, кэш, очередь, CDN | [[system-design-basics#компоненты-системы]] |
| Процесс проектирования шаг за шагом | [[system-design-basics#процесс-проектирования]] |
| CAP, консистентность | [[cap-theorem-interview|interview/architecture/cap-theorem-interview]] |
| Scalability patterns | [[scalability-patterns-interview|interview/architecture/scalability-patterns-interview]] |

## Маршруты чтения

- **Подготовка к system-design-интервью (3-5 дней):** `system-design-basics.md` → `interview/architecture/*` → проработать 5-10 классических задач (URL shortener, newsfeed, chat).
- **Проектирование нового сервиса:** требования → capacity planning → выбор компонентов → ADR (`architectural-decision-records/`).
- **Recovery после инцидента:** пересмотреть single points of failure → добавить резервирование → обновить SLO и runbook.

## Куда идти дальше

- Архитектурные стили — [[README|architecture/software-architecture/]]
- CAP, consistency patterns — [interview/architecture/](../../interview/architecture/)
- Паттерны устойчивости (Circuit Breaker, Retry, Bulkhead) — [[README|patterns/]]
- Наблюдаемость (метрики, трейсы, логи) — [[README|monitoring/]]
