---
title: "Database Testing"
description: "Точка входа в раздел тестирования работы приложения с базой данных: схема, миграции, репозитории, транзакции."
tags:
  - meta
  - index
  - testing
  - database-testing
type: "index"
updated: "2026-04-17"
---
# Database Testing

Раздел про тестирование persistence-слоя: проверка схемы, миграций, репозиториев, транзакций и изоляции данных между тестами. Используются как лёгкие in-memory БД (H2), так и полноценные контейнеры через Testcontainers, максимально похожие на продакшен.

Применяйте, когда нужно убедиться, что SQL-запросы, маппинги и миграции работают на той СУБД, которая стоит в проде. Unit-тесты с мок-репозиториями не ловят ошибки JPQL, типов колонок, indexes и констрейнтов — для этого и нужны database tests.

## Полезные ссылки

### Основные документы
- [Database Testing](database-testing.md) — подходы, @DataJpaTest, миграции, изоляция

### Соседние разделы
- [Integration Testing](../README.md)
- [Testcontainers](../testcontainers/README.md)
- [Contract Testing](../contract-testing/README.md)
- [JUnit](../../unit-testing/junit/README.md)
- [Databases / SQL](../../../databases/sql/README.md)

### Внешние ресурсы
- [Testcontainers](https://www.testcontainers.org/)
- [Flyway](https://flywaydb.org/documentation/)
- [Liquibase](https://docs.liquibase.com/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать какой подход](#когда-использовать-какой-подход)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Подходы: embedded vs реальная БД в контейнере | [database-testing.md](database-testing.md) |
| `@DataJpaTest`, `@SpringBootTest` | [database-testing.md](database-testing.md) |
| Изоляция: транзакции, `@Transactional`, `@Sql` | [database-testing.md](database-testing.md) |
| Миграции в тестах (Flyway, Liquibase) | [database-testing.md](database-testing.md) |

## Когда использовать какой подход

- **H2 / embedded** — быстро, но расхождения с продакшен-СУБД (функции, типы, FTS) могут пропускать баги.
- **Testcontainers + реальная СУБД** — точно как в проде, медленнее на старт, решается `reuse = true` и shared singleton.
- **`@DataJpaTest`** — только JPA-слой, откатывает транзакцию, быстро; для полного флоу — `@SpringBootTest`.

## Маршруты чтения

- **Быстрый старт (30 мин):** `Подходы` → `@DataJpaTest` → изоляция данных.
- **Production-setup (2 ч):** весь документ + Testcontainers + миграции Flyway в тестах.

## Куда идти дальше

- Контейнерный запуск инфраструктуры — [../testcontainers/README.md](../testcontainers/README.md)
- SQL и индексы — [../../../databases/sql/README.md](../../../databases/sql/README.md)
- ORM и JPA — [../../../databases/orm/README.md](../../../databases/orm/README.md)
