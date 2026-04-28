---
title: "Разработка"
description: "Шпаргалки и руководства по инструментам разработки, API, системам сборки и мессенджингу."
tags:
  - meta
  - index
type: "index"
aliases:
  - "Разработка"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Разработка

Шпаргалки и руководства по инструментам разработки, API, системам сборки и мессенджингу.

## Полезные ссылки

[API](../basics/README.md)
[Build Tools](../basics/README.md)
[Messaging](../basics/README.md)
[Web Backend](../basics/README.md)

## Быстрая навигация

- [API](../basics/README.md) — проектирование, контракты, инструменты
- [Инструменты сборки](../basics/README.md) — Gradle и Maven
- [Мессенджинг](../basics/README.md) — брокеры сообщений и паттерны интеграции
- [Web Backend](../basics/README.md) — базовые backend-практики
- [Platform: CI/CD](../basics/README.md) — пайплайны, delivery, legacy-контекст Travis
- [Platform: Containers](../basics/README.md) — Docker/Kubernetes для окружений и деплоя

## API

- [Обзор API](../basics/README.md)
- [Инструменты API](../basics/README.md)
  - [Документирование API](api/api-tools/swagger/api-documentation-basics.md)
  - [Тестирование API](api/api-tools/api-testing/api-testing-basics.md)
  - [Insomnia](api/api-tools/insomnia/insomnia-basics.md)
  - [Postman](api/api-tools/postman/postman-basics.md)
  - [Swagger](api/api-tools/swagger/openapi-swagger.md)
- [graphql](api/graphql/graphql.md)
- [grpc](api/grpc/grpc.md)
- [REST](api/rest/rest-api-best-practices.md)

## Инструменты сборки

- [Обзор Build Tools](../basics/README.md)
- [gradle](build-tools/gradle/gradle.md)
- [maven](build-tools/maven/maven.md)

## Мессенджинг

- [Обзор Messaging](../basics/README.md)
- [activemq](messaging/activemq/activemq.md)
- [kafka](messaging/kafka/kafka.md)
- [nats](messaging/nats/nats.md)
- [RabbitMQ](../basics/README.md)

## Web Backend

- [Обзор Web Backend](../basics/README.md)
- [Основы backend](web-backend/backend-basics.md)

## Рекомендуемый маршрут

1. `API` + `api-tools` для контрактов и тестирования.
2. `build-tools` для повторяемой сборки и зависимостей.
3. `messaging` для асинхронной интеграции.
4. Переход к `platform` разделам для CI/CD и контейнеризации.
