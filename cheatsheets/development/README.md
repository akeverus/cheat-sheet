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

[[README|API]]
[[README|Build Tools]]
[[README|Messaging]]
[[README|Web Backend]]

## Быстрая навигация

- [[README|API]] — проектирование, контракты, инструменты
- [[README|Инструменты сборки]] — Gradle и Maven
- [[README|Мессенджинг]] — брокеры сообщений и паттерны интеграции
- [[README|Web Backend]] — базовые backend-практики
- [[README|Platform: CI/CD]] — пайплайны, delivery, legacy-контекст Travis
- [[README|Platform: Containers]] — Docker/Kubernetes для окружений и деплоя

## API

- [[README|Обзор API]]
- [[README|Инструменты API]]
  - [[api-documentation-basics|Документирование API]]
  - [[api-testing-basics|Тестирование API]]
  - [[insomnia-basics|Insomnia]]
  - [[postman-basics|Postman]]
  - [[openapi-swagger|Swagger]]
- [[graphql]]
- [[grpc]]
- [[rest-api-best-practices|REST]]

## Инструменты сборки

- [[README|Обзор Build Tools]]
- [[gradle]]
- [[maven]]

## Мессенджинг

- [[README|Обзор Messaging]]
- [[activemq]]
- [[kafka]]
- [[nats]]
- [[README|RabbitMQ]]

## Web Backend

- [[README|Обзор Web Backend]]
- [[backend-basics|Основы backend]]

## Рекомендуемый маршрут

1. `API` + `api-tools` для контрактов и тестирования.
2. `build-tools` для повторяемой сборки и зависимостей.
3. `messaging` для асинхронной интеграции.
4. Переход к `platform` разделам для CI/CD и контейнеризации.
