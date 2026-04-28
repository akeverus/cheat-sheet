---
title: "Quarkus: Dev Services — Автоматические сервисы для разработки"
description: "Полное руководство по Dev Services в Quarkus: автоматический запуск баз данных, брокеров сообщений, Redis и других сервисов для разработки"
tags:
  - quarkus
  - dev-services
  - development
  - docker
  - containers
  - java
type: "reference"
difficulty: "beginner"
aliases:
  - "Quarkus"
  - "quarkus dev services"
prerequisites:
  - "[[quarkus-basics]]"
related:
  - "[[quarkus-basics]]"
  - "[[quarkus-testing]]"
next:
  - "[[quarkus-basics]]"
  - "[[quarkus-testing]]"
updated: "2026-04-20"
---

# Quarkus: Dev Services — Автоматические сервисы для разработки

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Database Dev Services](#database-dev-services)
  - [PostgreSQL Dev Service](#postgresql-dev-service)
  - [MySQL Dev Service](#mysql-dev-service)
  - [MongoDB Dev Service](#mongodb-dev-service)
- [Message Broker Dev Services](#message-broker-dev-services)
  - [Kafka Dev Service](#kafka-dev-service)
  - [AMQP Dev Service](#amqp-dev-service)
- [Cache Dev Services](#cache-dev-services)
  - [Redis Dev Service](#redis-dev-service)
- [Advanced Configuration](#advanced-configuration)
  - [Custom Ports](#custom-ports)
  - [Custom Images](#custom-images)
  - [Resource Limits](#resource-limits)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте Dev Services для разработки](#1-используйте-dev-services-для-разработки)
  - [2. Отключайте в production](#2-отключайте-в-production)
  - [3. Используйте отдельные контейнеры для тестов](#3-используйте-отдельные-контейнеры-для-тестов)
- [Dev Services Configuration](#dev-services-configuration)
  - [Multiple Services](#multiple-services)
  - [Service Isolation](#service-isolation)
  - [Shared Services](#shared-services)
- [Dev Services Lifecycle](#dev-services-lifecycle)
  - [Container Management](#container-management)
  - [Container Persistence](#container-persistence)
- [Advanced Dev Services](#advanced-dev-services)
  - [Custom Service Configuration](#custom-service-configuration)
  - [Volume Mounts](#volume-mounts)
  - [Health Checks](#health-checks)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus Dev Services** автоматически запускает необходимые сервисы (базы данных, брокеры сообщений, кеши) в контейнерах **Docker** во время разработки. Это значительно упрощает настройку окружения разработки.

### Основные возможности

- **Автоматический запуск**: Автоматический запуск сервисов в **Docker**
- **Автоматическая конфигурация**: Автоматическая настройка подключений
- **Изоляция**: Каждый проект получает изолированные сервисы
- **Zero Configuration**: Минимальная конфигурация

## Database Dev Services

### PostgreSQL Dev Service

**Автоматический **PostgreSQL**:**

```properties
# application.properties
quarkus.datasource.devservices.enabled=true
quarkus.datasource.db-kind=postgresql
```

### MySQL Dev Service

**Автоматический **MySQL**:**

```properties
quarkus.datasource.devservices.enabled=true
quarkus.datasource.db-kind=mysql
```

### MongoDB Dev Service

**Автоматический **MongoDB**:**

```properties
quarkus.mongodb.devservices.enabled=true
```

## Message Broker Dev Services

### Kafka Dev Service

**Автоматический **Kafka**:**

```properties
quarkus.kafka.devservices.enabled=true
```

### AMQP Dev Service

**Автоматический **AMQP** брокер:**

```properties
quarkus.amqp.devservices.enabled=true
```

## Cache Dev Services

### Redis Dev Service

**Автоматический **Redis**:**

```properties
quarkus.redis.devservices.enabled=true
```

## Advanced Configuration

### Custom Ports

**Настройка портов:**

```properties
quarkus.datasource.devservices.port=5433
```

### Custom Images

**Использование кастомных образов:**

```properties
quarkus.datasource.devservices.image-name=postgres:13
```

### Resource Limits

**Ограничение ресурсов:**

```properties
quarkus.datasource.devservices.memory=512m
```

## Лучшие практики

### 1. Используйте Dev Services для разработки

```properties
# ✅ Хорошо
quarkus.datasource.devservices.enabled=true
```

### 2. Отключайте в production

```properties
# ✅ Хорошо
%prod.quarkus.datasource.devservices.enabled=false
```

### 3. Используйте отдельные контейнеры для тестов

```properties
# ✅ Хорошо
%test.quarkus.datasource.devservices.enabled=true
```

## Dev Services Configuration

### Multiple Services

**Настройка нескольких сервисов:**

```properties
# PostgreSQL
quarkus.datasource.devservices.enabled=true
quarkus.datasource.db-kind=postgresql

# Redis
quarkus.redis.devservices.enabled=true

# Kafka
quarkus.kafka.devservices.enabled=true
```

### Service Isolation

**Изоляция сервисов:**

```properties
# Каждый проект получает свои контейнеры
quarkus.devservices.service-name=my-project
```

### Shared Services

**Разделяемые сервисы:**

```properties
# Использование одного контейнера для всех проектов
quarkus.devservices.shared=true
```

## Dev Services Lifecycle

### Container Management

**Управление контейнерами:**

```bash
# Dev Services автоматически:
# 1. Запускает контейнеры при старте приложения
# 2. Останавливает контейнеры при остановке приложения
# 3. Переиспользует контейнеры при повторном запуске
```

### Container Persistence

**Сохранение данных контейнеров:**

```properties
# Данные сохраняются между перезапусками
quarkus.datasource.devservices.reuse=true
```

## Advanced Dev Services

### Custom Service Configuration

**Кастомная конфигурация сервисов:**

```properties
quarkus.datasource.devservices.container-env.POSTGRES_DB=myapp
quarkus.datasource.devservices.container-env.POSTGRES_USER=myuser
quarkus.datasource.devservices.container-env.POSTGRES_PASSWORD=mypassword
```

### Volume Mounts

**Монтирование томов:**

```properties
quarkus.datasource.devservices.volumes=/path/to/data:/var/lib/postgresql/data
```

### Health Checks

**Проверка здоровья:**

```properties
quarkus.datasource.devservices.healthcheck.interval=10s
quarkus.datasource.devservices.healthcheck.timeout=5s
```

## Заключение

**Quarkus Dev Services** значительно упрощает разработку, автоматически запуская необходимые сервисы в **Docker** контейнерах. Поддержка баз данных, брокеров сообщений, кешей и других сервисов позволяет быстро начать разработку без ручной настройки окружения.

## Дополнительные ресурсы

- [**Quarkus** Dev **Services** Guide](https://quarkus.io/guides/dev-services)
- [Docker Documentation](https://docs.docker.com/)

## См. также

- [Quarkus: Actuator — Health Checks и Metrics](quarkus-actuator.md)
- [Quarkus: Основы](quarkus-basics.md)
- [Quarkus: Cache — Кеширование данных](quarkus-cache.md)
- [Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh](quarkus-cloud.md)
- [Quarkus: Core — CDI, Bean Scopes и Configuration](quarkus-core.md)
