---
title: "Quarkus: Dev Services - Автоматические сервисы для разработки"
description: "Полное руководство по Dev Services в Quarkus: автоматический запуск баз данных, брокеров сообщений, Redis и других сервисов для разработки"
tags: ["quarkus", "dev-services", "development", "docker", "containers", "java"]
difficulty: "beginner"
prerequisites: ["quarkus/quarkus-basics.md"]
next: ["quarkus-basics.md", "quarkus-testing.md"]
updated: "2025-01-16"
related: ["quarkus-basics.md", "quarkus-testing.md"]
---

# Quarkus: Dev Services - Автоматические сервисы для разработки

## Введение

Quarkus Dev Services автоматически запускает необходимые сервисы (базы данных, брокеры сообщений, кеши) в контейнерах Docker во время разработки. Это значительно упрощает настройку окружения разработки.

### Основные возможности

- **Автоматический запуск**: Автоматический запуск сервисов в Docker
- **Автоматическая конфигурация**: Автоматическая настройка подключений
- **Изоляция**: Каждый проект получает изолированные сервисы
- **Zero Configuration**: Минимальная конфигурация

## Database Dev Services

### PostgreSQL Dev Service

Автоматический PostgreSQL:

```properties
# application.properties
quarkus.datasource.devservices.enabled=true
quarkus.datasource.db-kind=postgresql
```

### MySQL Dev Service

Автоматический MySQL:

```properties
quarkus.datasource.devservices.enabled=true
quarkus.datasource.db-kind=mysql
```

### MongoDB Dev Service

Автоматический MongoDB:

```properties
quarkus.mongodb.devservices.enabled=true
```

## Message Broker Dev Services

### Kafka Dev Service

Автоматический Kafka:

```properties
quarkus.kafka.devservices.enabled=true
```

### AMQP Dev Service

Автоматический AMQP брокер:

```properties
quarkus.amqp.devservices.enabled=true
```

## Cache Dev Services

### Redis Dev Service

Автоматический Redis:

```properties
quarkus.redis.devservices.enabled=true
```

## Advanced Configuration

### Custom Ports

Настройка портов:

```properties
quarkus.datasource.devservices.port=5433
```

### Custom Images

Использование кастомных образов:

```properties
quarkus.datasource.devservices.image-name=postgres:13
```

### Resource Limits

Ограничение ресурсов:

```properties
quarkus.datasource.devservices.memory=512m
```

## Best Practices

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

Настройка нескольких сервисов:

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

Изоляция сервисов:

```properties
# Каждый проект получает свои контейнеры
quarkus.devservices.service-name=my-project
```

### Shared Services

Разделяемые сервисы:

```properties
# Использование одного контейнера для всех проектов
quarkus.devservices.shared=true
```

## Dev Services Lifecycle

### Container Management

Управление контейнерами:

```bash
# Dev Services автоматически:
# 1. Запускает контейнеры при старте приложения
# 2. Останавливает контейнеры при остановке приложения
# 3. Переиспользует контейнеры при повторном запуске
```

### Container Persistence

Сохранение данных контейнеров:

```properties
# Данные сохраняются между перезапусками
quarkus.datasource.devservices.reuse=true
```

## Advanced Dev Services

### Custom Service Configuration

Кастомная конфигурация сервисов:

```properties
quarkus.datasource.devservices.container-env.POSTGRES_DB=myapp
quarkus.datasource.devservices.container-env.POSTGRES_USER=myuser
quarkus.datasource.devservices.container-env.POSTGRES_PASSWORD=mypassword
```

### Volume Mounts

Монтирование томов:

```properties
quarkus.datasource.devservices.volumes=/path/to/data:/var/lib/postgresql/data
```

### Health Checks

Проверка здоровья:

```properties
quarkus.datasource.devservices.healthcheck.interval=10s
quarkus.datasource.devservices.healthcheck.timeout=5s
```

## Best Practices

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

### 4. Настраивайте ресурсы

```properties
# ✅ Хорошо
quarkus.datasource.devservices.memory=512m
quarkus.datasource.devservices.cpus=1
```

### 5. Используйте reuse для ускорения

```properties
# ✅ Хорошо
quarkus.datasource.devservices.reuse=true
```

## Заключение

Quarkus Dev Services значительно упрощает разработку, автоматически запуская необходимые сервисы в Docker контейнерах. Поддержка баз данных, брокеров сообщений, кешей и других сервисов позволяет быстро начать разработку без ручной настройки окружения.

## Дополнительные ресурсы

- [Quarkus Dev Services Guide](https://quarkus.io/guides/dev-services)
- [Docker Documentation](https://docs.docker.com/)

