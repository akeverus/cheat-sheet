---
title: "ORM"
description: "Точка входа в раздел ORM: как объектная модель мапится на реляционную БД, когда нужен ORM, а когда — чистый SQL. Мост к JPA, Hibernate, Spring Data и jOOQ."
tags:
  - meta
  - index
  - orm
type: "index"
updated: "2026-04-17"
---
# ORM

Object-Relational Mapping — прослойка, превращающая реляционные данные в объектный граф и обратно. Этот раздел сводит базовые понятия: mapping аннотации, JPA EntityManager, Hibernate-специфика, жизненный цикл сущностей, стратегии загрузки, кэши и антипаттерны (N+1, LazyInitializationException, detached-сущности).

Для кого: Java-разработчики, проектирующие persistence-слой. Для конкретных фреймворков переходите в `frameworks/java-frameworks/spring/` и `libraries/java/`. Для чистого SQL — в `databases/sql/`.

## Полезные ссылки

### Основной документ
- [[orm-basics|Основы ORM]]

### Соседние разделы
- [[README|SQL]]
- [[README|Реляционные СУБД]]
- [[README|Spring Data JPA]]
- [[README|Java-библиотеки (jOOQ, MapStruct)]]
- [[README|Тестирование БД]]

### Внешние ресурсы
- [Hibernate ORM User Guide](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [Jakarta Persistence (JPA) 3.2](https://jakarta.ee/specifications/persistence/)
- [jOOQ Manual](https://www.jooq.org/doc/latest/manual/)
- [Vlad Mihalcea — High-Performance Java Persistence](https://vladmihalcea.com/tutorials/hibernate/)

## Содержание

- [Когда нужен ORM](#когда-нужен-orm)
- [Ключевые концепции](#ключевые-концепции)
- [ORM vs чистый SQL](#orm-vs-чистый-sql)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда нужен ORM

- CRUD-heavy приложения с многосущностной моделью.
- Необходимость работать с одной доменной моделью между несколькими СУБД.
- Команда готова инвестировать в понимание жизненного цикла сущностей и кэшей.

Когда ORM **избыточен**: отчёты, batch-процессы, запросы с множеством join/агрегатов — тогда используйте JDBC, jOOQ или MyBatis.

## Ключевые концепции

| Концепция | Что понять |
|-----------|-----------|
| Mapping (`@Entity`, `@Table`, `@Column`) | Как класс становится строкой |
| Persistence Context | «Снимок» сущностей в памяти, dirty checking |
| Жизненный цикл сущности | Transient Managed Detached Removed |
| Lazy/Eager loading | Когда JOIN, когда отдельный SELECT |
| N+1 проблема | Симптом, причина, `JOIN FETCH`/`@BatchSize`/EntityGraph |
| Cascade | Как каскадируются операции по связям |
| JPQL / HQL / Criteria | Объектный язык запросов |
| Native queries | Когда спускаться до SQL |
| First/Second level cache | Кэш persistence context vs кластерный |
| Optimistic locking (`@Version`) | Контроль одновременных апдейтов |

## ORM vs чистый SQL

| Критерий | ORM (Hibernate/JPA) | SQL (JDBC/jOOQ) |
|----------|---------------------|-----------------|
| Скорость разработки CRUD | Высокая | Средняя |
| Контроль над SQL | Средний | Полный |
| Сложные аналитические запросы | Плохо | Отлично |
| Рисков перформанса | Много (N+1, cartesian product) | Меньше |
| Portability между СУБД | Высокая | Низкая |

## Маршруты чтения

- **Junior backend:** [[orm-basics]] [[README|Spring Data JPA]].
- **Миграция с JDBC:** концепции mapping persistence context lazy loading [[orm-basics#лучшие-практики]].
- **Перф-оптимизация:** N+1 кэш L2 batch fetching native queries.

## Куда идти дальше

- Оптимизация SQL и индексы — [[README|databases/sql/]], [[README|databases/relational/postgresql/]]
- Hibernate-специфика, Session API — [[README|libraries/java/]]
- Тестирование JPA-слоя с Testcontainers — [[README|testing/integration-testing/testcontainers/]]
- Паттерны DAO/Repository — [[README|patterns/]]
