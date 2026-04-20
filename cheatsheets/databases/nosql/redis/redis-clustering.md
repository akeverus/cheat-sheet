---
title: "Redis: Кластеризация"
description: "Полное руководство по Redis Cluster: настройка, шардинг, репликация, масштабирование, мониторинг, оптимизация"
tags:
  - redis
  - cluster
  - sharding
  - scaling
  - high-availability
  - distributed
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-replication.md"]
next: ["databases/redis-performance.md", "databases/redis-security.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-replication.md"]
---

# Redis: Кластеризация

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Cluster Tutorial](https://redis.io/docs/management/scaling/) — кластеризация

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-replication|redis-replication.md]] — репликация

## Содержание

- [Введение в Redis Cluster](#введение-в-redis-cluster)
  - [Основные возможности Redis Cluster](#основные-возможности-redis-cluster)
  - [Архитектура Redis Cluster](#архитектура-redis-cluster)
- [Настройка Redis Cluster](#настройка-redis-cluster)
  - [Минимальная конфигурация](#минимальная-конфигурация)
  - [Создание кластера](#создание-кластера)
  - [Подключение к кластеру](#подключение-к-кластеру)
- [Работа с кластером](#работа-с-кластером)
  - [Базовые операции](#базовые-операции)
  - [Hash Tags](#hash-tags)
  - [Множественные ключи](#множественные-ключи)
- [Управление кластером](#управление-кластером)
  - [Проверка состояния кластера](#проверка-состояния-кластера)
  - [Добавление нового узла](#добавление-нового-узла)
  - [Удаление узла](#удаление-узла)
  - [Перебалансировка слотов](#перебалансировка-слотов)
- [Мониторинг кластера](#мониторинг-кластера)
  - [Команды мониторинга](#команды-мониторинга)
  - [Скрипт мониторинга](#скрипт-мониторинга)
- [Failover в кластере](#failover-в-кластере)
  - [Автоматический failover](#автоматический-failover)
  - [Ручной failover](#ручной-failover)
- [Оптимизация кластера](#оптимизация-кластера)
  - [Распределение слотов](#распределение-слотов)
  - [Настройки производительности](#настройки-производительности)
- [Решение проблем](#решение-проблем)
  - [Проблема: Кластер не запускается](#проблема-кластер-не-запускается)
  - [Проблема: Ключи на разных узлах](#проблема-ключи-на-разных-узлах)
  - [Проблема: CROSSSLOT ошибки](#проблема-crossslot-ошибки)
- [Лучшие практики](#лучшие-практики)
  - [Конфигурация](#конфигурация)
  - [Безопасность](#безопасность)
  - [Производительность](#производительность)
- [Advanced Cluster Operations](#advanced-cluster-operations)
  - [Slot Migration](#slot-migration)
  - [Cluster Rebalancing](#cluster-rebalancing)
  - [Import/Export данных](#import-export-данных)
- [Cluster Topology](#cluster-topology)
  - [Understanding Cluster Topology](#understanding-cluster-topology)
  - [Slot Distribution](#slot-distribution)
- [Client Configuration](#client-configuration)
  - [Java (Jedis)](#java-jedis)
  - [Java (Jedis)](#java-jedis-1)
- [Cluster Health Monitoring](#cluster-health-monitoring)
  - [Comprehensive Health Check](#comprehensive-health-check)
- [Production Deployment](#production-deployment)
  - [Docker Compose для кластера](#docker-compose-для-кластера)
  - [Kubernetes Deployment](#kubernetes-deployment)
- [Troubleshooting Advanced Issues](#troubleshooting-advanced-issues)
  - [Split-Brain Prevention](#split-brain-prevention)
  - [Network Partitions](#network-partitions)
  - [Slot Coverage Issues](#slot-coverage-issues)
- [Performance Optimization](#performance-optimization)
  - [Network Optimization](#network-optimization)
  - [Memory Optimization](#memory-optimization)
- [Best Practices Summary](#best-practices-summary)
  - [Configuration](#configuration)
  - [Operations](#operations)
  - [Security](#security)
- [Cluster Management Operations](#cluster-management-operations)
  - [Adding Nodes to Cluster](#adding-nodes-to-cluster)
  - [Removing Nodes from Cluster](#removing-nodes-from-cluster)
  - [Cluster Maintenance](#cluster-maintenance)
- [Cluster Performance Optimization](#cluster-performance-optimization)
  - [Slot Distribution](#slot-distribution-1)
  - [Network Optimization](#network-optimization-1)

## Введение в Redis Cluster

**Redis Cluster** — это встроенное решение для горизонтального масштабирования **Redis**. Оно обеспечивает автоматическое шардинг данных, репликацию и высокую доступность без использования внешних инструментов.

### Основные возможности Redis Cluster

- **Автоматический шардинг**: Данные распределяются между узлами автоматически
- **Высокая доступность**: Автоматический **failover** при сбое узла
- **Горизонтальное масштабирование**: Легкое добавление/удаление узлов
- **Нет единой точки отказа**: Распределенная архитектура
- **Прозрачная маршрутизация**: Клиенты автоматически перенаправляются

### Архитектура Redis Cluster

```text
┌─────────────────────────────────────────────────────────┐
│                    Redis Cluster                       │
├─────────────────────────────────────────────────────────┤
│  Node 1 (Master)  │  Node 2 (Master)  │  Node 3 (Master)│
│  Slots: 0-5460    │  Slots: 5461-10922│  Slots: 10923-16383│
├─────────────────────────────────────────────────────────┤
│  Node 4 (Slave)   │  Node 5 (Slave)   │  Node 6 (Slave)  │
│  Replica of 1     │  Replica of 2     │  Replica of 3    │
└─────────────────────────────────────────────────────────┘
```


## Настройка Redis Cluster

### Минимальная конфигурация

Для работы **Redis Cluster** требуется минимум 3 **master** узла. Для высокой доступности рекомендуется 3 **master** + 3 **replica** (всего 6 узлов).

### Создание кластера

#### Шаг 1: Подготовка конфигураций

```bash
# Создать директории для узлов
mkdir -p cluster/{7001,7002,7003,7004,7005,7006}

# Генерация конфигураций для каждого узла
for port in {7001..7006}; do
cat > cluster/$port/redis.conf << EOF
port $port
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
dir /var/lib/redis/cluster/$port
EOF
done
```

#### Шаг 2: Запуск узлов

```bash
# Запуск всех узлов
for port in {7001..7006}; do
    cd cluster/$port
    redis-server redis.conf &
    cd ../..
done

# Проверка запуска
ps aux | grep redis-server
```

#### Шаг 3: Создание кластера

```bash
# Создание кластера с репликацией
redis-cli --cluster create \
  127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 \
  127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006 \
  --cluster-replicas 1

# --cluster-replicas 1 означает 1 реплика на каждый master
```

### Подключение к кластеру

```bash
# Подключение к кластеру (с поддержкой редиректов)
redis-cli -c -p 7001

# Выполнение команд
127.0.0.1:7001> SET mykey "value"
-> Redirected to slot [14687] located at 127.0.0.1:7003
OK
127.0.0.1:7003> GET mykey
"value"
```


## Работа с кластером

### Базовые операции

```redis
# Установка значения
SET key "value"

# Получение значения
GET key

# Работа с хэшами
HSET user:1000 name "John"
HGET user:1000 name

# Работа со списками
LPUSH mylist "item1"
LRANGE mylist 0 -1
```

### Hash Tags

**Для обеспечения того, что несколько ключей находятся на одном узле, используйте **hash tags**:**

```redis
# Ключи с одинаковым hash tag будут на одном узле
SET user:{1000}:name "John"
SET user:{1000}:email "john@example.com"
SET user:{1000}:age 30

# Все эти ключи будут на одном узле
```

### Множественные ключи

```redis
# Операции с множественными ключами требуют hash tags
# или использования транзакций

# С hash tags (все ключи на одном узле)
MGET user:{1000}:name user:{1000}:email user:{1000}:age

# Без hash tags (может не работать если ключи на разных узлах)
# MGET key1 key2 key3  # Может вызвать ошибку CROSSSLOT
```


## Управление кластером

### Проверка состояния кластера

```bash
# Проверка состояния кластера
redis-cli --cluster check 127.0.0.1:7001

# Информация о кластере
redis-cli -p 7001 CLUSTER INFO

# Список узлов
redis-cli -p 7001 CLUSTER NODES

# Информация о слотах
redis-cli -p 7001 CLUSTER SLOTS
```

### Добавление нового узла

```bash
# Добавить master узел
redis-cli --cluster add-node 127.0.0.1:7007 127.0.0.1:7001

# После добавления нужно перераспределить слоты
redis-cli --cluster reshard 127.0.0.1:7001

# Добавить replica узел
redis-cli --cluster add-node \
  127.0.0.1:7008 127.0.0.1:7001 \
  --cluster-slave \
  --cluster-master-id <master-node-id>
```

### Удаление узла

```bash
# Переместить слоты с узла перед удалением
redis-cli --cluster reshard 127.0.0.1:7001

# Удалить узел
redis-cli --cluster del-node 127.0.0.1:7001 <node-id>
```

### Перебалансировка слотов

```bash
# Автоматическая перебалансировка
redis-cli --cluster rebalance 127.0.0.1:7001

# Перебалансировка с указанием весов
redis-cli --cluster rebalance \
  127.0.0.1:7001 \
  --cluster-weight node1=1 node2=2 node3=1
```


## Мониторинг кластера

### Команды мониторинга

```redis
# Информация о кластере
CLUSTER INFO

# Список узлов
CLUSTER NODES

# Информация о слотах
CLUSTER SLOTS

# Ключи в слоте
CLUSTER COUNTKEYSINSLOT <slot>

# Ключи в слоте (с лимитом)
CLUSTER GETKEYSINSLOT <slot> <count>
```

### Скрипт мониторинга

```bash
#!/bin/bash
# monitor_cluster.sh

CLUSTER_NODES="127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003"

for node in $CLUSTER_NODES; do
    host=$(echo $node | cut -d: -f1)
    port=$(echo $node | cut -d: -f2)

    echo "Checking node $node..."

    # Проверить статус узла
    CLUSTER_INFO=$(redis-cli -h $host -p $port CLUSTER INFO)
    CLUSTER_STATE=$(echo "$CLUSTER_INFO" | grep "cluster_state" | cut -d: -f2 | tr -d ' ')

    if [ "$CLUSTER_STATE" != "ok" ]; then
        echo "WARNING: Cluster state is $CLUSTER_STATE on $node"
    fi

    # Проверить количество подключенных узлов
    CLUSTER_SIZE=$(echo "$CLUSTER_INFO" | grep "cluster_known_nodes" | cut -d: -f2 | tr -d ' ')
    echo "Cluster size: $CLUSTER_SIZE nodes"

    # Проверить слоты
    SLOTS=$(redis-cli -h $host -p $port CLUSTER NODES | grep $node | grep -o "\[.*\]" | tr -d '[]' | cut -d'-' -f1)
    echo "Slots: $SLOTS"
done
```


## Failover в кластере

### Автоматический failover

**Redis Cluster** автоматически выполняет **failover** при недоступности **master** узла:**

1. **Обнаружение недоступности**: Другие узлы обнаруживают недоступность
2. **Выбор нового master**: **Replica** промоутится в **master**
3. **Обновление конфигурации**: Все узлы обновляют информацию о топологии

### Ручной failover

```redis
# На replica узле
CLUSTER FAILOVER

# Немедленный failover (без ожидания master)
CLUSTER FAILOVER FORCE

# Failover с ожиданием master (для обновления конфигурации)
CLUSTER FAILOVER TAKEOVER
```


## Оптимизация кластера

### Распределение слотов

```bash
# Проверить распределение слотов
redis-cli --cluster check 127.0.0.1:7001

# Перебалансировать слоты
redis-cli --cluster rebalance 127.0.0.1:7001
```

### Настройки производительности

```conf
# Оптимизация для кластера
cluster-node-timeout 5000
cluster-require-full-coverage yes
cluster-allow-reads-when-down no

# Оптимизация сети
tcp-backlog 511
tcp-keepalive 300
```


## Решение проблем

### Проблема: Кластер не запускается

```bash
# Проверить логи
tail -f /var/log/redis/redis-server.log

# Проверить конфигурацию
redis-cli -p 7001 CLUSTER INFO

# Проверить файлы конфигурации узлов
cat cluster/7001/nodes.conf
```

### Проблема: Ключи на разных узлах

```bash
# Использовать hash tags для группировки ключей
# Вместо:
SET user:1000:name "John"
SET user:1000:email "john@example.com"

# Использовать:
SET user:{1000}:name "John"
SET user:{1000}:email "john@example.com"
```

### Проблема: CROSSSLOT ошибки

```redis
# Ошибка возникает при операциях с ключами на разных узлах
# Решение: использовать hash tags или транзакции

# С hash tags
MGET user:{1000}:name user:{1000}:email

# Или использовать транзакции (требует hash tags)
MULTI
GET user:{1000}:name
GET user:{1000}:email
EXEC
```


## Лучшие практики

### Конфигурация

1. **Используйте минимум 3 master узла** для отказоустойчивости
2. **Настройте репликацию** для каждого **master**
3. **Используйте hash tags** для группировки связанных ключей
4. **Мониторьте распределение слотов** регулярно
5. **Настройте правильный `cluster-node`-timeout**

### Безопасность

1. **Используйте пароли** для всех узлов
2. **Ограничьте доступ** через **firewall**
3. **Используйте `SSL`/TLS** для удаленных подключений
4. **Регулярно обновляйте Redis**
5. **Мониторьте подозрительную активность**

### Производительность

1. **Равномерно распределяйте слоты** между узлами
2. **Используйте hash tags** для оптимизации запросов
3. **Мониторьте производительность** каждого узла
4. **Оптимизируйте сеть** между узлами
5. **Используйте connection pooling** в клиентах

## Advanced Cluster Operations

### Slot Migration

```bash
# Миграция слотов между узлами
redis-cli --cluster reshard 127.0.0.1:7001

# Интерактивный процесс:
# 1. Указать количество слотов для миграции
# 2. Выбрать источник слотов
# 3. Выбрать назначение слотов
# 4. Подтвердить миграцию
```

### Cluster Rebalancing

```bash
# Автоматическая перебалансировка
redis-cli --cluster rebalance 127.0.0.1:7001

# С указанием весов узлов
redis-cli --cluster rebalance \
  127.0.0.1:7001 \
  --cluster-weight node1=1 node2=2 node3=1

# С указанием порогов
redis-cli --cluster rebalance \
  127.0.0.1:7001 \
  --cluster-threshold 2
```

### Import/Export данных

```bash
# Импорт данных в кластер
redis-cli --cluster import \
  127.0.0.1:7001 \
  --cluster-from 127.0.0.1:6379 \
  --cluster-copy \
  --cluster-replace

# Экспорт данных из кластера
redis-cli --cluster backup 127.0.0.1:7001 /backup/cluster.rdb
```

## Cluster Topology

### Understanding Cluster Topology

```redis
# Получить полную информацию о топологии
CLUSTER NODES

# Формат вывода:
# <node-id> <ip:port> <flags> <master-id> <ping-sent> <pong-recv> <config-epoch> <link-state> <slots>

# Пример:
# 3fa3e... 127.0.0.1:7001 master - 0 1234567890 1 connected 0-5460
# 9d8f2... 127.0.0.1:7002 master - 0 1234567891 1 connected 5461-10922
# 7c3a1... 127.0.0.1:7003 master - 0 1234567892 1 connected 10923-16383
```

### Slot Distribution

```bash
# Проверить распределение слотов
redis-cli --cluster check 127.0.0.1:7001

# Redis Cluster использует 16384 слотов
# Слоты распределяются между master узлами
# Каждый ключ попадает в слот на основе CRC16(key) % 16384
```

## Client Configuration

### Java (Jedis)

```java
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.HostAndPort;
import java.util.HashSet;
import java.util.Set;

public class RedisClusterExample {
    public static void main(String[] args) {
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7001));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7002));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7003));

        JedisCluster jedisCluster = new JedisCluster(
            jedisClusterNodes,
            2000,  // connection timeout
            5,     // max redirects
            new JedisPoolConfig()
        );

        // Использование кластера
        jedisCluster.set("key", "value");
        String value = jedisCluster.get("key");

        jedisCluster.close();
    }
}
```

### Java (Jedis)

```java
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import java.util.HashSet;
import java.util.Set;

// Настройка кластера
Set<HostAndPort> jedisClusterNodes = new HashSet<>();
jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7001));
jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7002));
jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7003));

JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);

// Использование кластера
jedisCluster.set("key", "value");
String value = jedisCluster.get("key");
jedisCluster.close();
```

## Cluster Health Monitoring

### Comprehensive Health Check

```java
// Java пример проверки здоровья кластера
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPool;
import java.util.Map;
import java.util.List;

public class ClusterHealthChecker {
    private JedisPool jedisPool;

    public ClusterHealthChecker(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        this.jedisPool = new JedisPool(config, host, port);
    }

    public int checkClusterHealth() {
        List<String> issues = new java.util.ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            // Информация о кластере
            Map<String, String> clusterInfo = jedis.clusterInfo();

            // Проверить состояние кластера
            String clusterState = clusterInfo.get("cluster_state");
            if (!"ok".equals(clusterState)) {
                issues.add("Cluster state: " + clusterState);
            }

            // Проверить покрытие слотов
            int slotsAssigned = Integer.parseInt(clusterInfo.getOrDefault("cluster_slots_assigned", "0"));
            if (slotsAssigned < 16384) {
                issues.add("Not all slots assigned: " + slotsAssigned + "/16384");
            }

            // Проверить количество узлов
            int knownNodes = Integer.parseInt(clusterInfo.getOrDefault("cluster_known_nodes", "0"));
            if (knownNodes < 3) {
                issues.add("Too few nodes: " + knownNodes);
            }

            // Проверить размер кластера
            int clusterSize = Integer.parseInt(clusterInfo.getOrDefault("cluster_size", "0"));
            if (clusterSize < 3) {
                issues.add("Cluster size too small: " + clusterSize);
            }

            if (!issues.isEmpty()) {
                System.out.println("WARNING: Cluster health issues:");
                issues.forEach(issue -> System.out.println("  - " + issue));
                return 1;
            } else {
                System.out.println("OK: Cluster is healthy");
                return 0;
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return 2;
        }
    }

    public void close() {
        jedisPool.close();
    }

    public static void main(String[] args) {
        ClusterHealthChecker checker = new ClusterHealthChecker("localhost", 7001);
        int result = checker.checkClusterHealth();
        checker.close();
        System.exit(result);
    }
}
```

## Production Deployment

### Docker Compose для кластера

```yaml
version: '3.8'

services:
  redis-node1:
    image: redis:7-alpine
    ports:
      - "7001:7001"
    volumes:
      - node1_data:/data
    command: redis-server --port 7001 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

  redis-node2:
    image: redis:7-alpine
    ports:
      - "7002:7002"
    volumes:
      - node2_data:/data
    command: redis-server --port 7002 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

  redis-node3:
    image: redis:7-alpine
    ports:
      - "7003:7003"
    volumes:
      - node3_data:/data
    command: redis-server --port 7003 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

  redis-node4:
    image: redis:7-alpine
    ports:
      - "7004:7004"
    volumes:
      - node4_data:/data
    command: redis-server --port 7004 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

  redis-node5:
    image: redis:7-alpine
    ports:
      - "7005:7005"
    volumes:
      - node5_data:/data
    command: redis-server --port 7005 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

  redis-node6:
    image: redis:7-alpine
    ports:
      - "7006:7006"
    volumes:
      - node6_data:/data
    command: redis-server --port 7006 --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    networks:
      - redis-cluster

volumes:
  node1_data:
  node2_data:
  node3_data:
  node4_data:
  node5_data:
  node6_data:

networks:
  redis-cluster:
    driver: bridge
```

### Kubernetes Deployment

```yaml
# redis-cluster-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis-cluster
spec:
  serviceName: redis-cluster
  replicas: 6
  selector:
    matchLabels:
      app: redis-cluster
  template:
    metadata:
      labels:
        app: redis-cluster
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        - containerPort: 16379
        command:
        - redis-server
        - --port
        - "6379"
        - --cluster-enabled
        - "yes"
        - --cluster-config-file
        - nodes.conf
        - --cluster-node-timeout
        - "5000"
        - --appendonly
        - "yes"
        volumeMounts:
        - name: redis-data
          mountPath: /data
  volumeClaimTemplates:
  - metadata:
      name: redis-data
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

## Troubleshooting Advanced Issues

### Split-Brain Prevention

```conf
# Настройки для предотвращения split-brain
cluster-require-full-coverage yes
cluster-allow-reads-when-down no

# cluster-require-full-coverage: Кластер не принимает запросы
# если не все слоты покрыты

# cluster-allow-reads-when-down: Разрешить чтение при недоступности
# некоторых узлов (только для чтения)
```

### Network Partitions

```bash
# При сетевом разделении кластер может разделиться на части
# Redis Cluster использует majority для принятия решений

# Проверить состояние после разделения
redis-cli -p 7001 CLUSTER INFO
redis-cli -p 7001 CLUSTER NODES

# Восстановить после разделения
redis-cli --cluster fix 127.0.0.1:7001
```

### Slot Coverage Issues

```bash
# Проверить покрытие слотов
redis-cli -p 7001 CLUSTER INFO | grep slots

# Если слоты не покрыты, исправить
redis-cli --cluster fix 127.0.0.1:7001

# Или перераспределить вручную
redis-cli --cluster reshard 127.0.0.1:7001
```

## Performance Optimization

### Network Optimization

```conf
# Оптимизация сети для кластера
cluster-node-timeout 5000
tcp-backlog 511
tcp-keepalive 300

# Уменьшить таймаут для быстрого обнаружения сбоев
cluster-node-timeout 3000
```

### Memory Optimization

```conf
# Оптимизация памяти
maxmemory 2gb
maxmemory-policy allkeys-lru

# Использовать lazyfree для больших удалений
lazyfree-lazy-eviction yes
lazyfree-lazy-expire yes
lazyfree-lazy-server-del yes
```

## Best Practices Summary

### Configuration

1. **Используйте минимум 3 master узла** для отказоустойчивости
2. **Настройте репликацию** для каждого **master** (минимум 1 replica)
3. **Используйте hash tags** для группировки связанных ключей
4. **Мониторьте распределение слотов** регулярно
5. **Настройте правильный `cluster-node`-timeout**

### Operations

1. **Регулярно проверяйте состояние** кластера
2. **Мониторьте покрытие слотов** (должно быть 16384)
3. **Перебалансируйте слоты** при добавлении/удалении узлов
4. **Тестируйте failover** регулярно
5. **Документируйте топологию** кластера

### Security

1. **Используйте пароли** для всех узлов
2. **Ограничьте доступ** через **firewall**
3. **Используйте `SSL`/TLS** для удаленных подключений
4. **Регулярно обновляйте Redis**
5. **Мониторьте подозрительную активность**

## Cluster Management Operations

### Adding Nodes to Cluster

```bash
# Добавить master узел
redis-cli --cluster add-node \
  127.0.0.1:7007 \
  127.0.0.1:7001

# Добавить replica узел
redis-cli --cluster add-node \
  127.0.0.1:7008 \
  127.0.0.1:7001 \
  --cluster-slave \
  --cluster-master-id <master-node-id>
```

### Removing Nodes from Cluster

```bash
# 1. Переместить слоты с узла
redis-cli --cluster reshard 127.0.0.1:7001

# 2. Удалить узел
redis-cli --cluster del-node 127.0.0.1:7001 <node-id>
```

### Cluster Maintenance

```bash
# Проверка состояния кластера
redis-cli --cluster check 127.0.0.1:7001

# Информация о кластере
redis-cli -p 7001 CLUSTER INFO

# Список узлов
redis-cli -p 7001 CLUSTER NODES

# Информация о слотах
redis-cli -p 7001 CLUSTER SLOTS
```

## Cluster Performance Optimization

### Slot Distribution

```bash
# Проверить распределение слотов
redis-cli --cluster check 127.0.0.1:7001 | grep slots

# Перебалансировать слоты
redis-cli --cluster rebalance 127.0.0.1:7001

# С указанием весов узлов
redis-cli --cluster rebalance \
  127.0.0.1:7001 \
  --cluster-weight node1=1 node2=2 node3=1
```

### Network Optimization

```conf
# Оптимизация сети для кластера
cluster-node-timeout 5000
tcp-backlog 511
tcp-keepalive 300

# Уменьшить таймаут для быстрого обнаружения сбоев
cluster-node-timeout 3000
```


- [Redis Cluster Tutorial](https://redis.io/docs/manual/scaling/)
- [Redis Cluster Specification](https://redis.io/docs/reference/cluster-spec/)

