---
title: "Redis: Решение проблем"
description: "Полное руководство по решению проблем в Redis: диагностика, common issues, debugging, performance problems, recovery"
tags:
  - redis
  - troubleshooting
  - debugging
  - diagnostics
  - performance
  - recovery
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-performance.md"]
next: []
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-performance.md", "databases/redis-monitoring.md"]
---

# **Redis**: Решение проблем

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Troubleshooting](https://redis.io/docs/management/troubleshooting/) — диагностика

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-monitoring|redis-monitoring.md]] — мониторинг

## Содержание

- [Введение в **troubleshooting Redis**](#введение-в-troubleshooting-redis)
  - [Области проблем](#области-проблем)
- [Диагностика проблем](#диагностика-проблем)
  - [Проверка статуса сервера](#проверка-статуса-сервера)
  - [Проверка памяти](#проверка-памяти)
  - [Проверка производительности](#проверка-производительности)
  - [**Issue** 1: **Out** of **Memory**](#issue-1-out-of-memory)
  - [**Issue** 2: **Slow Performance**](#issue-2-slow-performance)
  - [**Issue** 3: **Connection Issues**](#issue-3-connection-issues)
- [**Advanced Troubleshooting**](#advanced-troubleshooting)
  - [**Memory Issues**](#memory-issues)
  - [**Performance Issues**](#performance-issues)
  - [**Network Issues**](#network-issues)
  - [**Replication Issues**](#replication-issues)
- [**Diagnostic Scripts**](#diagnostic-scripts)
  - [**Comprehensive Health Check**](#comprehensive-health-check)
- [Лучшие практики](#лучшие-практики)

## Введение в **troubleshooting Redis**

Решение проблем в **Redis** требует систематического подхода к диагностике. Понимание общих проблем и их решений критически важно для поддержания стабильной работы **Redis**.

### Области проблем

1. **Производительность**: Медленные операции, высокий **latency**
2. **Память**: Нехватка памяти, **fragmentation**
3. **Сеть**: Проблемы подключения, таймауты
4. **Репликация**: **Lag**, разрывы соединения
5. **Кластер**: Проблемы с слотами, **failover**

---

## Диагностика проблем

### Проверка статуса сервера

```bash
# Проверка доступности Redis (PING, INFO server, логи)
redis-cli PING

# Информация о сервере
redis-cli INFO server

# Проверка логов
tail -f /var/log/redis/redis-server.log
```

### Проверка памяти

```redis
# Информация о памяти
INFO memory

# Ключевые метрики:
# used_memory: Используемая память
# used_memory_peak: Пиковое использование
# mem_fragmentation_ratio: Фрагментация памяти
# maxmemory: Максимальная память
# maxmemory_policy: Политика eviction
```

### Проверка производительности

```redis
# Статистика команд
INFO commandstats

# Slow log
SLOWLOG GET 10

# Операций в секунду
INFO stats | grep instantaneous_ops_per_sec
```

### **Issue** 1: **Out** of **Memory**

**Симптомы:**
- Ошибки **OOM** (**Out of Memory**)
- Команды возвращают ошибки
- Высокое использование памяти

**Решение:**
```redis
# Проверить использование памяти
INFO memory

# Увеличить maxmemory
CONFIG SET maxmemory 4gb

# Изменить политику eviction
CONFIG SET maxmemory-policy allkeys-lru

# Очистить память
FLUSHDB  # Осторожно!
```

### **Issue** 2: **Slow Performance**

**Симптомы:**
- Высокий **latency**
- Медленные операции
- Таймауты

**Решение:**
```redis
# Проверить slow log
SLOWLOG GET 10

# Проверить статистику команд
INFO commandstats

# Оптимизировать запросы
# Использовать Pipeline
# Использовать правильные структуры данных
```

### **Issue** 3: **Connection Issues**

**Симптомы:**
- Ошибки подключения
- Таймауты
- Отключения

**Решение:**
```bash
# Проверить количество подключений
redis-cli INFO clients

# Проверить лимиты
redis-cli CONFIG GET maxclients

# Увеличить лимиты
redis-cli CONFIG SET maxclients 10000
```

## **Advanced Troubleshooting**

### **Memory Issues**

```bash
# Детальный анализ памяти
redis-cli INFO memory

# Поиск больших ключей
redis-cli --bigkeys

# Анализ использования памяти ключами
redis-cli MEMORY USAGE key
redis-cli MEMORY STATS
redis-cli MEMORY DOCTOR
```

### **Performance Issues**

```bash
# Анализ медленных команд
redis-cli SLOWLOG GET 10

# Статистика команд
redis-cli INFO commandstats

# Мониторинг в реальном времени
redis-cli MONITOR  # Только для отладки!
```

### **Network Issues**

```bash
# Проверка подключений
redis-cli INFO clients

# Проверка сетевых настроек
redis-cli CONFIG GET tcp-keepalive
redis-cli CONFIG GET timeout

# Проверка лимитов
redis-cli CONFIG GET maxclients
```

### **Replication Issues**

```bash
# Проверка статуса репликации
redis-cli INFO replication

# Проверка lag
redis-cli INFO replication | grep lag

# Проверка подключения
redis-cli INFO replication | grep master_link_status
```

## **Diagnostic Scripts**

### **Comprehensive Health Check**

```java
// Redis Python example replaced with Java Spring
```

## Лучшие практики

- **Диагностика:** используйте **INFO** (**server, memory, replication, stats**), **MONITOR** выборочно; логи и метрики (**latency, hit rate**) для выявления узких мест.
- **Память:** следите за **used_memory**, **mem_fragmentation_ratio** и **maxmemory_policy**; при нехватке памяти настройте **eviction** и ограничьте размер ключей/значений.
- **Репликация:** проверяйте **master_link_status** и **lag**; при разрывах — переподключение реплики, проверка сети и диска; не допускайте длительного отставания реплики.
- **Кластер:** проверяйте слоты (**CLUSTER SLOTS**), миграции и **failover**; при проблемах — ручная миграция слотов или восстановление из бэкапа.
- **Восстановление:** регулярные **RDB**/**AOF** бэкапы; тесты восстановления; при повреждении **AOF** — **redis-check-aof**; при необходимости — перезапуск с пустой БД и загрузка из бэкапа.

