---
title: "Redis: Репликация"
description: "Полное руководство по репликации в Redis: master-slave, настройка, мониторинг, failover, Sentinel, оптимизация"
tags: ["redis", "replication", "master-slave", "sentinel", "failover", "high-availability"]
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-persistence.md"]
next: ["databases/redis-clustering.md", "databases/redis-high-availability.md"]
updated: "2026-01-16"
related: ["databases/redis-basics.md", "databases/redis-persistence.md"]
---

# Redis: Репликация

## Введение в репликацию Redis

Репликация в Redis позволяет создавать копии данных на других серверах для обеспечения высокой доступности, распределения нагрузки чтения и резервного копирования.

### Типы репликации

1. **Master-Slave (Replica)**: Один master, один или несколько slaves
2. **Cascading Replication**: Slave может быть master для других slaves
3. **Sentinel**: Автоматический мониторинг и failover

### Преимущества репликации

- **Высокая доступность**: Автоматический failover при сбое master
- **Масштабирование чтения**: Распределение запросов на чтение
- **Резервное копирование**: Использование slaves для бэкапов
- **Географическое распределение**: Реплики в разных регионах

---

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

---

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

---

## Redis Sentinel

Sentinel обеспечивает автоматический мониторинг и failover для Redis master-slave репликации.

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

---

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

---

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

---

## Cascading Replication

Cascading Replication позволяет создавать цепочки реплик, где slave может быть master для других slaves.

### Настройка Cascading Replication

```conf
# Slave 1 (подключен к Master)
replicaof master-host 6379

# Slave 2 (подключен к Slave 1)
replicaof slave1-host 6380
```

### Преимущества и недостатки

**Преимущества:**
- Снижение нагрузки на master
- Географическое распределение

**Недостатки:**
- Увеличенная задержка репликации
- Больше точек отказа

---

## Troubleshooting

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

---

## Best Practices

### Конфигурация

1. **Используйте Sentinel** для автоматического failover
2. **Настройте правильный quorum** для Sentinel
3. **Используйте пароли** для безопасности
4. **Настройте repl-backlog** для восстановления
5. **Мониторьте lag** репликации

### Мониторинг

1. **Отслеживайте статус** репликации регулярно
2. **Мониторьте lag** между master и slaves
3. **Проверяйте здоровье** Sentinel
4. **Настройте алерты** на проблемы репликации
5. **Тестируйте failover** регулярно

### Безопасность

1. **Используйте пароли** для master и slaves
2. **Ограничьте доступ** через firewall
3. **Используйте SSL/TLS** для удаленных подключений
4. **Регулярно обновляйте** Redis
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

## Replication Lag Management

### Monitoring Lag

```java
// Redis Python example replaced with Java Spring
