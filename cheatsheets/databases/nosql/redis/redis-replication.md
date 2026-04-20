---
title: "Redis: Репликация"
description: "Полное руководство по репликации в Redis: master-slave, настройка, мониторинг, failover, Sentinel, оптимизация"
tags:
  - redis
  - replication
  - master-slave
  - sentinel
  - failover
  - high-availability
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-persistence.md"]
next: ["databases/redis-clustering.md", "databases/redis-high-availability.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-persistence.md"]
---

# Redis: Репликация

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Replication](https://redis.io/docs/management/replication/) — репликация

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-persistence|redis-persistence.md]] — персистентность

## Содержание

- [**Redis**: Репликация](#redis-репликация)
- [Введение в репликацию **Redis**](#введение-в-репликацию-redis)
  - [Типы репликации](#типы-репликации)
  - [Преимущества репликации](#преимущества-репликации)
- [Настройка **Master-Slave** репликации](#настройка-master-slave-репликации)
  - [Настройка **Master**](#настройка-master)
- [redis-master.conf](#redis-masterconf)
- [Настройки репликации](#настройки-репликации)
  - [Настройка **Slave** (Replica)](#настройка-slave-replica)
- [redis-slave.conf](#redis-slaveconf)
- [Настройка репликации](#настройка-репликации)
  - [Команды для настройки репликации](#команды-для-настройки-репликации)
- [На slave сервере через redis-cli](#на-slave-сервере-через-redis-cli)
- [С паролем](#с-паролем)
- [Проверка статуса репликации](#проверка-статуса-репликации)
  - [Проверка репликации](#проверка-репликации)
- [На master](#на-master)
- [На slave](#на-slave)
- [Проверка подключенных slaves](#проверка-подключенных-slaves)
- [Мониторинг репликации](#мониторинг-репликации)
  - [Команды мониторинга](#команды-мониторинга)
- [Информация о репликации](#информация-о-репликации)
- [Ключевые метрики:](#ключевые-метрики)
- [role: роль сервера (master/slave)](#role-роль-сервера-masterslave)
- [connected_slaves: количество подключенных slaves](#connected_slaves-количество-подключенных-slaves)
- [master_repl_offset: offset master](#master_repl_offset-offset-master)
- [slave_repl_offset: offset slave](#slave_repl_offset-offset-slave)
- [master_last_io_seconds_ago: время последнего I/O](#master_last_io_seconds_ago-время-последнего-io)
- [slave_priority: приоритет slave для failover](#slave_priority-приоритет-slave-для-failover)
  - [Скрипт мониторинга](#скрипт-мониторинга)
- [monitor_replication.sh](#monitor_replicationsh)
- [Проверить статус master](#проверить-статус-master)
- [Проверить статус slave](#проверить-статус-slave)
- [Проверить lag](#проверить-lag)
- [**Redis Sentinel**](#redis-sentinel)
  - [Настройка **Sentinel**](#настройка-sentinel)
- [sentinel.conf](#sentinelconf)
- [Мониторинг master](#мониторинг-master)
- [Пароль master](#пароль-master)
- [Время для определения недоступности](#время-для-определения-недоступности)
- [Время для failover](#время-для-failover)
- [Количество параллельных синхронизаций](#количество-параллельных-синхронизаций)
- [Защита от множественных failover](#защита-от-множественных-failover)
  - [Запуск **Sentinel**](#запуск-sentinel)
- [Или через redis-server](#или-через-redis-server)
  - [Команды **Sentinel**](#команды-sentinel)
- [Подключение к Sentinel](#подключение-к-sentinel)
- [Информация о masters](#информация-о-masters)
- [Информация о slaves](#информация-о-slaves)
- [Информация о Sentinel](#информация-о-sentinel)
- [Текущий master](#текущий-master)
- [Принудительный failover](#принудительный-failover)
  - [Конфигурация через **Sentinel**](#конфигурация-через-sentinel)
- [Sentinel автоматически обновляет конфигурацию](#sentinel-автоматически-обновляет-конфигурацию)
- [При изменении топологии, Sentinel обновляет sentinel.conf](#при-изменении-топологии-sentinel-обновляет-sentinelconf)
- [Проверка конфигурации](#проверка-конфигурации)
- [**Failover** и восстановление](#failover-и-восстановление)
  - [Автоматический **failover**](#автоматический-failover)
- [Sentinel автоматически выполняет failover при:](#sentinel-автоматически-выполняет-failover-при)
- [1. Master недоступен в течение down-after-milliseconds](#1-master-недоступен-в-течение-down-after-milliseconds)
- [2. Кворум Sentinel согласен с failover (>= quorum)](#2-кворум-sentinel-согласен-с-failover-quorum)
- [3. Выбирается лучший slave (по приоритету и offset)](#3-выбирается-лучший-slave-по-приоритету-и-offset)
- [Процесс failover:](#процесс-failover)
- [1. Обнаружение недоступности master](#1-обнаружение-недоступности-master)
- [2. Выбор нового master из slaves](#2-выбор-нового-master-из-slaves)
- [3. Промоутинг выбранного slave в master](#3-промоутинг-выбранного-slave-в-master)
- [4. Обновление конфигурации других slaves](#4-обновление-конфигурации-других-slaves)
  - [Ручной **failover**](#ручной-failover)
- [На slave сервере](#на-slave-сервере)
- [Обновить другие slaves](#обновить-другие-slaves)
  - [Восстановление после **failover**](#восстановление-после-failover)
- [После восстановления старого master](#после-восстановления-старого-master)
- [1. Настроить как slave нового master](#1-настроить-как-slave-нового-master)
- [2. Или использовать Sentinel для автоматического восстановления](#2-или-использовать-sentinel-для-автоматического-восстановления)
- [Оптимизация репликации](#оптимизация-репликации)
  - [Настройки производительности](#настройки-производительности)
- [Оптимизация сети](#оптимизация-сети)
- [Оптимизация синхронизации](#оптимизация-синхронизации)
- [Размер backlog](#размер-backlog)
  - [Оптимизация для больших баз данных](#оптимизация-для-больших-баз-данных)
- [Использование diskless sync для быстрой синхронизации](#использование-diskless-sync-для-быстрой-синхронизации)
- [Увеличение backlog для больших задержек](#увеличение-backlog-для-больших-задержек)
- [**Cascading Replication**](#cascading-replication)
  - [Настройка **Cascading Replication**](#настройка-cascading-replication)
- [Slave 1 (подключен к Master)](#slave-1-подключен-к-master)
- [Slave 2 (подключен к Slave 1)](#slave-2-подключен-к-slave-1)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [**Troubleshooting**](#решение-проблем)
  - [Проблема: **Slave** не подключается к **Master**](#проблема-slave-не-подключается-к-master)
- [Проверить сетевую связность](#проверить-сетевую-связность)
- [Проверить пароль](#проверить-пароль)
- [Проверить логи](#проверить-логи)
- [Проверить конфигурацию](#проверить-конфигурацию)
  - [Проблема: Высокий **lag** репликации](#проблема-высокий-lag-репликации)
- [Решения:](#решения)
- [1. Увеличить repl-backlog-size](#1-увеличить-repl-backlog-size)
- [2. Оптимизировать сеть](#2-оптимизировать-сеть)
- [3. Использовать diskless sync](#3-использовать-diskless-sync)
- [4. Проверить производительность slave](#4-проверить-производительность-slave)
  - [Проблема: **Sentinel** не обнаруживает **failover**](#проблема-sentinel-не-обнаруживает-failover)
- [Проверить кворум](#проверить-кворум)
  - [Конфигурация](#конфигурация)
  - [Мониторинг](#мониторинг)
  - [Безопасность](#безопасность)
- [**Advanced Replication Configuration**](#advanced-replication-configuration)
  - [**Partial Resynchronization**](#partial-resynchronization)
- [Backlog для частичной ресинхронизации](#backlog-для-частичной-ресинхронизации)
- [Backlog позволяет slave восстановить соединение](#backlog-позволяет-slave-восстановить-соединение)
- [без полной синхронизации, если master еще имеет](#без-полной-синхронизации-если-master-еще-имеет)
- [необходимые данные в backlog](#необходимые-данные-в-backlog)
  - [**Diskless Replication**](#diskless-replication)
- [Diskless replication для быстрой синхронизации](#diskless-replication-для-быстрой-синхронизации)
- [При diskless sync master передает данные напрямую](#при-diskless-sync-master-передает-данные-напрямую)
- [в сокет slave, минуя диск](#в-сокет-slave-минуя-диск)
  - [**Replication Timeouts**](#replication-timeouts)
- [Таймауты репликации](#таймауты-репликации)
- [Если slave не отвечает в течение repl-timeout,](#если-slave-не-отвечает-в-течение-repl-timeout)
- [master считает его недоступным](#master-считает-его-недоступным)
- [**Sentinel Advanced Configuration**](#sentinel-advanced-configuration)
  - [**Quorum** и **Voting**](#quorum-и-voting)
- [Quorum определяет количество Sentinel, которые должны](#quorum-определяет-количество-sentinel-которые-должны)
- [согласиться с failover](#согласиться-с-failover)
- [В этом примере quorum = 2, значит минимум 2 Sentinel](#в-этом-примере-quorum-2-значит-минимум-2-sentinel)
- [должны согласиться с failover](#должны-согласиться-с-failover)
  - [**Multiple Masters**](#multiple-masters)
- [Мониторинг нескольких masters](#мониторинг-нескольких-masters)
- [Каждый master имеет свою конфигурацию](#каждый-master-имеет-свою-конфигурацию)
  - [**Sentinel Scripts**](#sentinel-scripts)
- [Скрипты для уведомлений](#скрипты-для-уведомлений)
- [Скрипты выполняются при событиях Sentinel](#скрипты-выполняются-при-событиях-sentinel)
- [Лучшие практики](#лучшие-практики)
- [**Replication Lag Management**](#replication-lag-management)
  - [**Monitoring Lag**](#monitoring-lag)

## Введение в репликацию Redis

Репликация в **Redis** позволяет создавать копии данных на других серверах для обеспечения высокой доступности, распределения нагрузки чтения и резервного копирования.

### Типы репликации

1. **Master-`Slave` (Replica)**: Один **master**, один или несколько **slaves**
2. **Cascading Replication**: **Slave** может быть **master** для других **slaves**
3. **Sentinel**: Автоматический мониторинг и **failover**

### Преимущества репликации

- **Высокая доступность**: Автоматический **failover** при сбое **master**
- **Масштабирование чтения**: Распределение запросов на чтение
- **Резервное копирование**: Использование **slaves** для бэкапов
- **Географическое распределение**: Реплики в разных регионах


## Настройка Master-Slave репликации

### Настройка Master

```conf
# redis-master.conf
bind 0.0.0.0
port 6379
requirepass masterpassword

# Настройки репликации
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-ping-slave-period 10
repl-timeout 60
repl-disable-tcp-nodelay no
repl-backlog-size 1mb
repl-backlog-ttl 3600
```

### Настройка Slave (Replica)

```conf
# redis-slave.conf
bind 0.0.0.0
port 6380
requirepass slavepassword

# Настройка репликации
replicaof 127.0.0.1 6379
masterauth masterpassword
replica-read-only yes
replica-serve-stale-data yes
replica-priority 100
```

### Команды для настройки репликации

```redis
# На slave сервере через redis-cli
REPLICAOF master-host 6379
REPLICAOF NO ONE  # Остановить репликацию

# С паролем
CONFIG SET masterauth masterpassword

# Проверка статуса репликации
INFO replication
```

### Проверка репликации

```redis
# На master
INFO replication

# На slave
INFO replication

# Проверка подключенных slaves
ROLE
```


## Мониторинг репликации

### Команды мониторинга

```redis
# Информация о репликации
INFO replication

# Ключевые метрики:
# role: роль сервера (master/slave)
# connected_slaves: количество подключенных slaves
# master_repl_offset: offset master
# slave_repl_offset: offset slave
# master_last_io_seconds_ago: время последнего I/O
# slave_priority: приоритет slave для failover
```

### Скрипт мониторинга

```bash
#!/bin/bash
# monitor_replication.sh

MASTER_HOST="localhost"
MASTER_PORT=6379
SLAVE_HOST="localhost"
SLAVE_PORT=6380

# Проверить статус master
MASTER_INFO=$(redis-cli -h $MASTER_HOST -p $MASTER_PORT INFO replication)
MASTER_ROLE=$(echo "$MASTER_INFO" | grep "role" | cut -d: -f2 | tr -d ' ')
CONNECTED_SLAVES=$(echo "$MASTER_INFO" | grep "connected_slaves" | cut -d: -f2 | tr -d ' ')

echo "Master role: $MASTER_ROLE"
echo "Connected slaves: $CONNECTED_SLAVES"

# Проверить статус slave
SLAVE_INFO=$(redis-cli -h $SLAVE_HOST -p $SLAVE_PORT INFO replication)
SLAVE_ROLE=$(echo "$SLAVE_INFO" | grep "role" | cut -d: -f2 | tr -d ' ')
MASTER_LINK_STATUS=$(echo "$SLAVE_INFO" | grep "master_link_status" | cut -d: -f2 | tr -d ' ')

echo "Slave role: $SLAVE_ROLE"
echo "Master link status: $MASTER_LINK_STATUS"

if [ "$MASTER_LINK_STATUS" != "up" ]; then
    echo "WARNING: Slave is not connected to master"
    exit 1
fi

# Проверить lag
MASTER_OFFSET=$(echo "$MASTER_INFO" | grep "master_repl_offset" | cut -d: -f2 | tr -d ' ')
SLAVE_OFFSET=$(echo "$SLAVE_INFO" | grep "slave_repl_offset" | cut -d: -f2 | tr -d ' ')
LAG=$((MASTER_OFFSET - SLAVE_OFFSET))

echo "Replication lag: $LAG bytes"

if [ $LAG -gt 1048576 ]; then  # 1MB
    echo "WARNING: Replication lag is high"
    exit 1
fi

echo "OK: Replication is healthy"
exit 0
```


## Redis Sentinel

**Sentinel** обеспечивает автоматический мониторинг и **failover** для **Redis master-slave** репликации.

### Настройка Sentinel

```conf
# sentinel.conf
port 26379

# Мониторинг master
sentinel monitor mymaster 127.0.0.1 6379 2

# Пароль master
sentinel auth-pass mymaster masterpassword

# Время для определения недоступности
sentinel down-after-milliseconds mymaster 5000

# Время для failover
sentinel failover-timeout mymaster 60000

# Количество параллельных синхронизаций
sentinel parallel-syncs mymaster 1

# Защита от множественных failover
sentinel deny-scripts-reconfig yes
```

### Запуск Sentinel

```bash
# Запуск Sentinel
redis-sentinel sentinel.conf

# Или через redis-server
redis-server sentinel.conf --sentinel
```

### Команды Sentinel

```redis
# Подключение к Sentinel
redis-cli -p 26379

# Информация о masters
SENTINEL masters

# Информация о slaves
SENTINEL slaves mymaster

# Информация о Sentinel
SENTINEL sentinels mymaster

# Текущий master
SENTINEL get-master-addr-by-name mymaster

# Принудительный failover
SENTINEL failover mymaster
```

### Конфигурация через Sentinel

```redis
# Sentinel автоматически обновляет конфигурацию
# При изменении топологии, Sentinel обновляет sentinel.conf

# Проверка конфигурации
SENTINEL master mymaster
SENTINEL slaves mymaster
```


## Failover и восстановление

### Автоматический failover

```bash
# Sentinel автоматически выполняет failover при:
# 1. Master недоступен в течение down-after-milliseconds
# 2. Кворум Sentinel согласен с failover (>= quorum)
# 3. Выбирается лучший slave (по приоритету и offset)

# Процесс failover:
# 1. Обнаружение недоступности master
# 2. Выбор нового master из slaves
# 3. Промоутинг выбранного slave в master
# 4. Обновление конфигурации других slaves
```

### Ручной failover

```bash
# На slave сервере
redis-cli -p 6380 REPLICAOF NO ONE

# Обновить другие slaves
redis-cli -p 6381 REPLICAOF new-master-host 6380
```

### Восстановление после failover

```bash
# После восстановления старого master
# 1. Настроить как slave нового master
redis-cli -p 6379 REPLICAOF new-master-host 6380

# 2. Или использовать Sentinel для автоматического восстановления
```


## Оптимизация репликации

### Настройки производительности

```conf
# Оптимизация сети
repl-disable-tcp-nodelay no
repl-ping-slave-period 10
repl-timeout 60

# Оптимизация синхронизации
repl-diskless-sync yes
repl-diskless-sync-delay 5

# Размер backlog
repl-backlog-size 100mb
repl-backlog-ttl 3600
```

### Оптимизация для больших баз данных

```conf
# Использование diskless sync для быстрой синхронизации
repl-diskless-sync yes
repl-diskless-sync-delay 5

# Увеличение backlog для больших задержек
repl-backlog-size 1gb

# Оптимизация сети
repl-disable-tcp-nodelay no
```


## Cascading Replication

**Cascading Replication** позволяет создавать цепочки реплик, где **slave** может быть **master** для других **slaves**.

### Настройка Cascading Replication

```conf
# Slave 1 (подключен к Master)
replicaof master-host 6379

# Slave 2 (подключен к Slave 1)
replicaof slave1-host 6380
```

### Преимущества и недостатки

**Преимущества:**
- Снижение нагрузки на **master**
- Географическое распределение

**Недостатки:**
- Увеличенная задержка репликации
- Больше точек отказа


## Решение проблем

### Проблема: Slave не подключается к Master

```bash
# Проверить сетевую связность
ping master-host
telnet master-host 6379

# Проверить пароль
redis-cli -h master-host -p 6379 -a masterpassword PING

# Проверить логи
tail -f /var/log/redis/redis-server.log

# Проверить конфигурацию
redis-cli CONFIG GET replicaof
redis-cli CONFIG GET masterauth
```

### Проблема: Высокий lag репликации

```bash
# Проверить lag
redis-cli INFO replication | grep lag

# Решения:
# 1. Увеличить repl-backlog-size
# 2. Оптимизировать сеть
# 3. Использовать diskless sync
# 4. Проверить производительность slave
```

### Проблема: Sentinel не обнаруживает failover

```bash
# Проверить кворум
redis-cli -p 26379 SENTINEL masters

# Проверить конфигурацию
redis-cli -p 26379 SENTINEL get-master-addr-by-name mymaster

# Проверить логи
tail -f /var/log/redis/sentinel.log
```

### Конфигурация

1. **Используйте Sentinel** для автоматического **failover**
2. **Настройте правильный quorum** для **Sentinel**
3. **Используйте пароли** для безопасности
4. **Настройте repl-backlog** для восстановления
5. **Мониторьте lag** репликации

### Мониторинг

1. **Отслеживайте статус** репликации регулярно
2. **Мониторьте lag** между **master** и **slaves**
3. **Проверяйте здоровье Sentinel**
4. **Настройте алерты** на проблемы репликации
5. **Тестируйте failover** регулярно

### Безопасность

1. **Используйте пароли** для **master** и **slaves**
2. **Ограничьте доступ** через **firewall**
3. **Используйте `SSL`/TLS** для удаленных подключений
4. **Регулярно обновляйте Redis**
5. **Мониторьте подозрительную активность**

## Advanced Replication Configuration

### Partial Resynchronization

```conf
# Backlog для частичной ресинхронизации
repl-backlog-size 100mb
repl-backlog-ttl 3600

# Backlog позволяет slave восстановить соединение
# без полной синхронизации, если master еще имеет
# необходимые данные в backlog
```

### Diskless Replication

```conf
# Diskless replication для быстрой синхронизации
repl-diskless-sync yes
repl-diskless-sync-delay 5

# При diskless sync master передает данные напрямую
# в сокет slave, минуя диск
```

### Replication Timeouts

```conf
# Таймауты репликации
repl-ping-slave-period 10    # Ping каждые 10 секунд
repl-timeout 60              # Таймаут 60 секунд

# Если slave не отвечает в течение repl-timeout,
# master считает его недоступным
```

## Sentinel Advanced Configuration

### Quorum и Voting

```conf
# Quorum определяет количество Sentinel, которые должны
# согласиться с failover
sentinel monitor mymaster 127.0.0.1 6379 2

# В этом примере quorum = 2, значит минимум 2 Sentinel
# должны согласиться с failover
```

### Multiple Masters

```conf
# Мониторинг нескольких masters
sentinel monitor mymaster1 127.0.0.1 6379 2
sentinel monitor mymaster2 127.0.0.1 6380 2

# Каждый master имеет свою конфигурацию
sentinel down-after-milliseconds mymaster1 5000
sentinel down-after-milliseconds mymaster2 10000
```

### Sentinel Scripts

```conf
# Скрипты для уведомлений
sentinel notification-script mymaster /path/to/script.sh
sentinel client-reconfig-script mymaster /path/to/script.sh

# Скрипты выполняются при событиях Sentinel
```

## Лучшие практики

Используйте репликацию для чтения только там, где допустима задержка: реплики отстают от мастера, поэтому для строго консистентного чтения обращайтесь к мастеру. Настройте **Sentinel** или аналог для автоматического переключения при отказе мастера и проверяйте сценарии failover на стенде.

Храните на репликах столько же памяти, сколько на мастере, и мониторьте лаг репликации (`master_last_io_seconds_ago`, `repl_backlog_size`). При необходимости ограничьте трафик репликации через `client-output-buffer-limit` для replica, чтобы мастер не блокировался при медленных репликах. Регулярно проверяйте целостность данных на репликах (например, сравнение ключей или контрольные суммы).

## Replication Lag Management

### Monitoring Lag

```java
// Redis Python example replaced with Java Spring
// TODO: добавить пример
```

