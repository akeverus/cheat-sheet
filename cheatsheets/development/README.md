---
title: "Разработка"
description: "Шпаргалки и руководства по инструментам разработки, API, системам сборки и мессенджингу."
tags:
  - meta
  - index
type: "index"
updated: "2026-02-11"
---
# Разработка

Шпаргалки и руководства по инструментам разработки, API, системам сборки и мессенджингу.

## Полезные ссылки

[API](api/README.md)
[Build Tools](build-tools/README.md)
[Messaging](messaging/README.md)
[Web Backend](web-backend/README.md)

## Быстрая навигация

- [API](api/README.md) — проектирование, контракты, инструменты
- [Инструменты сборки](build-tools/README.md) — Gradle и Maven
- [Мессенджинг](messaging/README.md) — брокеры сообщений и паттерны интеграции
- [Web Backend](web-backend/README.md) — базовые backend-практики
- [Platform: CI/CD](../platform/ci-cd/README.md) — пайплайны, delivery, legacy-контекст Travis
- [Platform: Containers](../platform/containers/README.md) — Docker/Kubernetes для окружений и деплоя

## API

- [Обзор API](api/README.md)
- [Инструменты API](api/api-tools/README.md)
  - [Документирование API](api/api-tools/swagger/api-documentation-basics.md)
  - [Тестирование API](api/api-tools/api-testing/api-testing-basics.md)
  - [Insomnia](api/api-tools/insomnia/insomnia-basics.md)
  - [Postman](api/api-tools/postman/postman-basics.md)
  - [Swagger](api/api-tools/swagger/openapi-swagger.md)
- [GraphQL](api/graphql/graphql.md)
- [gRPC](api/grpc/grpc.md)
- [REST](api/rest/rest-api-best-practices.md)

## Инструменты сборки

- [Обзор Build Tools](build-tools/README.md)
- [Gradle](build-tools/gradle/gradle.md)
- [Maven](build-tools/maven/maven.md)

## Мессенджинг

- [Обзор Messaging](messaging/README.md)
- [ActiveMQ](messaging/activemq/activemq.md)
- [Kafka](messaging/kafka/kafka.md)
- [NATS](messaging/nats/nats.md)
- [RabbitMQ](messaging/rabbitmq/README.md)

## Web Backend

- [Обзор Web Backend](web-backend/README.md)
- [Основы backend](web-backend/backend-basics.md)

## Рекомендуемый маршрут

1. `API` + `api-tools` для контрактов и тестирования.
2. `build-tools` для повторяемой сборки и зависимостей.
3. `messaging` для асинхронной интеграции.
4. Переход к `platform` разделам для CI/CD и контейнеризации.
