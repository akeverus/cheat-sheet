---
title: "Redis: Мониторинг"
description: "Полное руководство по мониторингу Redis: метрики, алерты, дашборды, Prometheus, Grafana, health checks"
tags:
  - redis
  - monitoring
  - metrics
  - alerting
  - prometheus
  - grafana
  - health-checks
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md", "databases/redis-performance.md"]
next: ["databases/redis-troubleshooting.md"]
updated: "2026-04-20"
related: ["databases/redis-basics.md", "databases/redis-performance.md"]
---

# Redis: Мониторинг

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Monitoring](https://redis.io/docs/management/monitoring/) — мониторинг

### См. также
- [redis-basics.md](redis-basics.md) — основы Redis
- [redis-performance.md](redis-performance.md) — производительность

- [Prometheus](../../../monitoring/metrics/prometheus.md)
- [Grafana](../../../monitoring/metrics/grafana.md)
- [Quarkus: Actuator — Health Checks и Metrics](../../../frameworks/java-frameworks/quarkus/quarkus-actuator.md)
## Содержание

- [Введение в мониторинг Redis](#введение-в-мониторинг-redis)
  - [Ключевые метрики для мониторинга](#ключевые-метрики-для-мониторинга)
- [Базовые команды мониторинга](#базовые-команды-мониторинга)
  - [INFO команда](#info-команда)
  - [Ключевые метрики](#ключевые-метрики)
- [Prometheus Monitoring](#prometheus-monitoring)
  - [Redis Exporter](#redis-exporter)
  - [Prometheus Configuration](#prometheus-configuration)
  - [Key Metrics](#key-metrics)
- [Grafana Dashboards](#grafana-dashboards)
  - [Dashboard Configuration](#dashboard-configuration)
- [Health Checks](#health-checks)
  - [Basic Health Check](#basic-health-check)
  - [Advanced Health Check](#advanced-health-check)
- [Alerting](#alerting)
  - [Alert Rules для Prometheus](#alert-rules-для-prometheus)
- [Custom Monitoring Scripts](#custom-monitoring-scripts)
  - [Comprehensive Monitoring](#comprehensive-monitoring)
- [Advanced Monitoring Techniques](#advanced-monitoring-techniques)
  - [Real-Time Monitoring](#real-time-monitoring)
  - [Custom Metrics Collection](#custom-metrics-collection)
- [Dashboard Examples](#dashboard-examples)
  - [Grafana Dashboard JSON](#grafana-dashboard-json)
- [Advanced Monitoring Setup](#advanced-monitoring-setup)
  - [Custom Metrics Collection](#custom-metrics-collection-1)
  - [Alerting System](#alerting-system)
- [Лучшие практики](#лучшие-практики)

## Введение в мониторинг Redis

Мониторинг **Redis** критически важен для обеспечения стабильной работы и быстрого обнаружения проблем. Правильный мониторинг включает отслеживание метрик производительности, использования ресурсов, состояния репликации и здоровья кластера.

### Ключевые метрики для мониторинга

1. **Производительность**: **OPS**, **latency**, **throughput**
2. **Память**: Использование, **fragmentation**, **eviction**
3. **Сеть**: **Connections**, **bandwidth**, **errors**
4. **Репликация**: **Lag**, **status**, **sync**
5. **Кластер**: **Slot coverage**, **node status**, **failover**


## Базовые команды мониторинга

### INFO команда

```redis
# Полная информация
INFO

# Информация по секциям
INFO server
INFO clients
INFO memory
INFO persistence
INFO stats
INFO replication
INFO cpu
INFO commandstats
INFO cluster
INFO keyspace
```

### Ключевые метрики

```redis
# Производительность
INFO stats | grep instantaneous_ops_per_sec
INFO stats | grep total_commands_processed

# Память
INFO memory | grep used_memory_human
INFO memory | grep mem_fragmentation_ratio

# Клиенты
INFO clients | grep connected_clients
INFO clients | grep blocked_clients

# Репликация
INFO replication | grep role
INFO replication | grep master_repl_offset
```


## Prometheus Monitoring

### Redis Exporter

```bash
# Запуск Redis Exporter
docker run -d \
  --name redis-exporter \
  -p 9121:9121 \
  -e REDIS_ADDR=redis://localhost:6379 \
  -e REDIS_PASSWORD=mypassword \
  oliver006/redis_exporter

# Или через бинарный файл
./redis_exporter \
  --redis.addr=localhost:6379 \
  --redis.password=mypassword \
  --web.listen-address=:9121
```

### Prometheus Configuration

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'redis'
    static_configs:
      - targets: ['localhost:9121']
    metrics_path: /metrics
    scrape_interval: 15s
    scrape_timeout: 10s
```

### Key Metrics

```promql
# Operations per second
rate(redis_commands_processed_total[1m])

# Memory usage
redis_memory_used_bytes
redis_memory_max_bytes

# Connections
redis_connected_clients
redis_rejected_connections_total

# Replication lag
redis_replication_master_repl_offset - redis_replication_slave_repl_offset

# Cache hit ratio
rate(redis_keyspace_hits_total[1m]) / (rate(redis_keyspace_hits_total[1m]) + rate(redis_keyspace_misses_total[1m]))
```


## Grafana Dashboards

### Dashboard Configuration

```json
{
  "dashboard": {
    "title": "Redis Monitoring",
    "panels": [
      {
        "title": "Operations per Second",
        "targets": [
          {
            "expr": "rate(redis_commands_processed_total[1m])"
          }
        ]
      },
      {
        "title": "Memory Usage",
        "targets": [
          {
            "expr": "redis_memory_used_bytes"
          }
        ]
      }
    ]
  }
}
```


## Health Checks

### Basic Health Check

```bash
#!/bin/bash
# health_check.sh

REDIS_HOST="localhost"
REDIS_PORT=6379

# Проверка доступности
if ! redis-cli -h $REDIS_HOST -p $REDIS_PORT PING > /dev/null 2>&1; then
    echo "CRITICAL: Redis is not responding"
    exit 2
fi

# Проверка использования памяти
MEMORY_USAGE=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO memory | grep used_memory_human | cut -d: -f2 | tr -d '\r')
echo "Memory usage: $MEMORY_USAGE"

# Проверка подключений
CONNECTED_CLIENTS=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO clients | grep connected_clients | cut -d: -f2 | tr -d '\r')
echo "Connected clients: $CONNECTED_CLIENTS"

echo "OK: Redis is healthy"
exit 0
```

### Advanced Health Check

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Map;

public class RedisHealthCheck {
    private JedisPool jedisPool;

    public RedisHealthCheck(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
    }

    public int healthCheck() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Проверка доступности
            if (!"PONG".equals(jedis.ping())) {
                System.out.println("CRITICAL: Redis is not responding");
                return 2;
            }

            // Проверка памяти
            Map<String, String> memoryInfo = jedis.info("memory");
            long usedMemory = Long.parseLong(memoryInfo.getOrDefault("used_memory", "0"));
            long maxMemory = Long.parseLong(memoryInfo.getOrDefault("maxmemory", "0"));

            if (maxMemory > 0 && usedMemory > maxMemory * 0.9) {
                double usagePercent = (usedMemory * 100.0) / maxMemory;
                System.out.println(String.format("WARNING: Memory usage is high: %.1f%%", usagePercent));
                return 1;
            }

            // Проверка клиентов
            Map<String, String> clientsInfo = jedis.info("clients");
            int connectedClients = Integer.parseInt(clientsInfo.getOrDefault("connected_clients", "0"));
            int maxClients = Integer.parseInt(clientsInfo.getOrDefault("maxclients", "0"));

            if (maxClients > 0 && connectedClients > maxClients * 0.9) {
                System.out.println(String.format("WARNING: Too many clients: %d/%d", connectedClients, maxClients));
                return 1;
            }

            // Проверка репликации (если slave)
            Map<String, String> replicationInfo = jedis.info("replication");
            if ("slave".equals(replicationInfo.get("role"))) {
                String masterLinkStatus = replicationInfo.get("master_link_status");
                if (!"up".equals(masterLinkStatus)) {
                    System.out.println("WARNING: Master link status: " + masterLinkStatus);
                    return 1;
                }
            }

            System.out.println("OK: Redis is healthy");
            return 0;

        } catch (Exception e) {
            System.out.println("CRITICAL: " + e.getMessage());
            return 2;
        }
    }

    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public static void main(String[] args) {
        RedisHealthCheck check = new RedisHealthCheck("localhost", 6379, null);
        System.exit(check.healthCheck());
    }
}
```


## Alerting

### Alert Rules для Prometheus

```yaml
# alerts.yml
groups:
  - name: redis_alerts
    rules:
      - alert: RedisDown
        expr: redis_up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Redis instance is down"

      - alert: RedisHighMemoryUsage
        expr: redis_memory_used_bytes / redis_memory_max_bytes > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Redis memory usage is above 90%"

      - alert: RedisHighConnections
        expr: redis_connected_clients / redis_maxclients > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Redis connections are above 90%"

      - alert: RedisReplicationLag
        expr: (redis_replication_master_repl_offset - redis_replication_slave_repl_offset) > 10485760
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Redis replication lag is high"
```


## Custom Monitoring Scripts

### Comprehensive Monitoring

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RedisMonitor {
    private JedisPool jedisPool;
    private Gson gson;

    public RedisMonitor(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        this.gson = new Gson();
    }

    public Map<String, Object> collectAllMetrics() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            metrics.put("server", jedis.info("server"));
            metrics.put("clients", jedis.info("clients"));
            metrics.put("memory", jedis.info("memory"));
            metrics.put("persistence", jedis.info("persistence"));
            metrics.put("stats", jedis.info("stats"));
            metrics.put("replication", jedis.info("replication"));
            metrics.put("cpu", jedis.info("cpu"));
            metrics.put("commandstats", jedis.info("commandstats"));
            metrics.put("keyspace", jedis.info("keyspace"));
            return metrics;
        }
    }

    public void monitorContinuously(long intervalMs) throws InterruptedException {
        while (true) {
            Map<String, Object> metrics = collectAllMetrics();
            System.out.println(gson.toJson(metrics));
            Thread.sleep(intervalMs);
        }
    }

    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RedisMonitor monitor = new RedisMonitor("localhost", 6379, null);
        monitor.monitorContinuously(60000);
    }
}
```

## Advanced Monitoring Techniques

### Real-Time Monitoring

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.*;

public class RealTimeMonitor {
    private JedisPool jedisPool;
    private Deque<Map<String, Object>> metricsHistory;

    public RealTimeMonitor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.metricsHistory = new ArrayDeque<>(100);
    }

    public Map<String, Object> collectRealtimeMetrics() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> info = jedis.info();

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("ops_per_sec", Long.parseLong(info.getOrDefault("instantaneous_ops_per_sec", "0")));
            metrics.put("connected_clients", Long.parseLong(info.getOrDefault("connected_clients", "0")));
            metrics.put("used_memory", Long.parseLong(info.getOrDefault("used_memory", "0")));
            metrics.put("keyspace_hits", Long.parseLong(info.getOrDefault("keyspace_hits", "0")));
            metrics.put("keyspace_misses", Long.parseLong(info.getOrDefault("keyspace_misses", "0")));
            metrics.put("total_commands_processed", Long.parseLong(info.getOrDefault("total_commands_processed", "0")));

            if (metricsHistory.size() >= 100) {
                metricsHistory.removeFirst();
            }
            metricsHistory.addLast(metrics);

            return metrics;
        }
    }

    public Map<String, Double> calculateTrends() {
        if (metricsHistory.size() < 2) {
            return null;
        }

        List<Map<String, Object>> recent = new ArrayList<>();
        List<Map<String, Object>> older = new ArrayList<>();

        Iterator<Map<String, Object>> iterator = metricsHistory.descendingIterator();
        int count = 0;
        while (iterator.hasNext() && count < 20) {
            Map<String, Object> metric = iterator.next();
            if (count < 10) {
                recent.add(metric);
            } else if (count < 20) {
                older.add(metric);
            }
            count++;
        }

        if (older.isEmpty()) {
            return null;
        }

        Map<String, Double> trends = new HashMap<>();
        for (String key : recent.get(0).keySet()) {
            double recentAvg = recent.stream()
                .mapToLong(m -> ((Number) m.get(key)).longValue())
                .average()
                .orElse(0.0);
            double olderAvg = older.stream()
                .mapToLong(m -> ((Number) m.get(key)).longValue())
                .average()
                .orElse(0.0);
            trends.put(key, olderAvg > 0 ? ((recentAvg - olderAvg) / olderAvg * 100) : 0.0);
        }

        return trends;
    }
}
```

### Custom Metrics Collection

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.HashMap;
import java.util.Map;

public class CustomMetricsCollector {
    private JedisPool jedisPool;

    public CustomMetricsCollector(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Map<String, Object> collectCustomMetrics() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> metrics = new HashMap<>();

            // Метрики производительности
            Map<String, String> stats = jedis.info("stats");
            metrics.put("ops_per_sec", Long.parseLong(stats.getOrDefault("instantaneous_ops_per_sec", "0")));
            metrics.put("total_commands", Long.parseLong(stats.getOrDefault("total_commands_processed", "0")));
            long hits = Long.parseLong(stats.getOrDefault("keyspace_hits", "0"));
            long misses = Long.parseLong(stats.getOrDefault("keyspace_misses", "0"));
            metrics.put("keyspace_hits", hits);
            metrics.put("keyspace_misses", misses);

            // Cache hit ratio
            long total = hits + misses;
            metrics.put("cache_hit_ratio", total > 0 ? (hits * 100.0 / total) : 0.0);

            // Метрики памяти
            Map<String, String> memory = jedis.info("memory");
            metrics.put("used_memory", Long.parseLong(memory.getOrDefault("used_memory", "0")));
            metrics.put("used_memory_peak", Long.parseLong(memory.getOrDefault("used_memory_peak", "0")));
            metrics.put("mem_fragmentation_ratio", Double.parseDouble(memory.getOrDefault("mem_fragmentation_ratio", "0")));

            // Метрики клиентов
            Map<String, String> clients = jedis.info("clients");
            metrics.put("connected_clients", Long.parseLong(clients.getOrDefault("connected_clients", "0")));
            metrics.put("blocked_clients", Long.parseLong(clients.getOrDefault("blocked_clients", "0")));

            return metrics;
        }
    }
}
```

## Dashboard Examples

### Grafana Dashboard JSON

```json
{
  "dashboard": {
    "title": "Redis Comprehensive Monitoring",
    "panels": [
      {
        "title": "Operations per Second",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(redis_commands_processed_total[1m])"
          }
        ]
      },
      {
        "title": "Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "redis_memory_used_bytes"
          },
          {
            "expr": "redis_memory_max_bytes"
          }
        ]
      },
      {
        "title": "Cache Hit Ratio",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(redis_keyspace_hits_total[1m]) / (rate(redis_keyspace_hits_total[1m]) + rate(redis_keyspace_misses_total[1m]))"
          }
        ]
      },
      {
        "title": "Connected Clients",
        "type": "graph",
        "targets": [
          {
            "expr": "redis_connected_clients"
          }
        ]
      },
      {
        "title": "Replication Lag",
        "type": "graph",
        "targets": [
          {
            "expr": "redis_replication_master_repl_offset - redis_replication_slave_repl_offset"
          }
        ]
      }
    ]
  }
}
```

## Advanced Monitoring Setup

### Custom Metrics Collection

```java
// Java пример сбора метрик
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Map;
import java.util.HashMap;

public class AdvancedMetricsCollector {
    private JedisPool jedisPool;

    public AdvancedMetricsCollector(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Map<String, Object> collectDetailedMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        try (Jedis jedis = jedisPool.getResource()) {
            // Метрики производительности
            Map<String, String> stats = jedis.info("stats");
            Map<String, Object> performanceMetrics = new HashMap<>();
            performanceMetrics.put("ops_per_sec", Integer.parseInt(stats.getOrDefault("instantaneous_ops_per_sec", "0")));
            performanceMetrics.put("total_commands", Long.parseLong(stats.getOrDefault("total_commands_processed", "0")));
            performanceMetrics.put("keyspace_hits", Long.parseLong(stats.getOrDefault("keyspace_hits", "0")));
            performanceMetrics.put("keyspace_misses", Long.parseLong(stats.getOrDefault("keyspace_misses", "0")));
            performanceMetrics.put("cache_hit_ratio", calculateHitRatio(stats));
            metrics.put("performance", performanceMetrics);

            // Метрики памяти
            Map<String, String> memory = jedis.info("memory");
            Map<String, Object> memoryMetrics = new HashMap<>();
            memoryMetrics.put("used", Long.parseLong(memory.getOrDefault("used_memory", "0")));
            memoryMetrics.put("peak", Long.parseLong(memory.getOrDefault("used_memory_peak", "0")));
            memoryMetrics.put("fragmentation", Double.parseDouble(memory.getOrDefault("mem_fragmentation_ratio", "0")));
            memoryMetrics.put("max", Long.parseLong(memory.getOrDefault("maxmemory", "0")));
            memoryMetrics.put("usage_percent", calculateMemoryUsage(memory));
            metrics.put("memory", memoryMetrics);

            // Метрики клиентов
            Map<String, String> clients = jedis.info("clients");
            Map<String, Object> clientMetrics = new HashMap<>();
            clientMetrics.put("connected", Integer.parseInt(clients.getOrDefault("connected_clients", "0")));
            clientMetrics.put("blocked", Integer.parseInt(clients.getOrDefault("blocked_clients", "0")));
            clientMetrics.put("max", Integer.parseInt(clients.getOrDefault("maxclients", "0")));
            clientMetrics.put("usage_percent", calculateClientUsage(clients));
            metrics.put("clients", clientMetrics);

        } catch (Exception e) {
            System.err.println("Error collecting metrics: " + e.getMessage());
        }

        return metrics;
    }

    private double calculateHitRatio(Map<String, String> stats) {
        long hits = Long.parseLong(stats.getOrDefault("keyspace_hits", "0"));
        long misses = Long.parseLong(stats.getOrDefault("keyspace_misses", "0"));
        long total = hits + misses;
        return total > 0 ? (hits * 100.0) / total : 0.0;
    }

    private double calculateMemoryUsage(Map<String, String> memory) {
        long used = Long.parseLong(memory.getOrDefault("used_memory", "0"));
        long max = Long.parseLong(memory.getOrDefault("maxmemory", "0"));
        return max > 0 ? (used * 100.0) / max : 0.0;
    }

    private double calculateClientUsage(Map<String, String> clients) {
        int connected = Integer.parseInt(clients.getOrDefault("connected_clients", "0"));
        int max = Integer.parseInt(clients.getOrDefault("maxclients", "0"));
        return max > 0 ? (connected * 100.0) / max : 0.0;
    }
}
```

### Alerting System

```java
// Java пример системы алертов
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class RedisAlerting {
    private JedisPool jedisPool;
    private Map<String, Double> thresholds;

    public RedisAlerting(JedisPool jedisPool, Map<String, Double> thresholds) {
        this.jedisPool = jedisPool;
        this.thresholds = thresholds;
    }

    public List<Map<String, Object>> checkAndAlert() {
        List<Map<String, Object>> alerts = new ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            // Проверка памяти
            Map<String, String> memory = jedis.info("memory");
            long used = Long.parseLong(memory.getOrDefault("used_memory", "0"));
            long maxMem = Long.parseLong(memory.getOrDefault("maxmemory", "0"));

            if (maxMem > 0) {
                double usagePercent = (used * 100.0) / maxMem;
                double memoryWarning = thresholds.getOrDefault("memory_warning", 80.0);

                if (usagePercent > memoryWarning) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("level", usagePercent < 90 ? "warning" : "critical");
                    alert.put("metric", "memory");
                    alert.put("value", usagePercent);
                    alert.put("message", String.format("Memory usage is %.1f%%", usagePercent));
                    alerts.add(alert);
                }
            }

            // Проверка клиентов
            Map<String, String> clients = jedis.info("clients");
            int connected = Integer.parseInt(clients.getOrDefault("connected_clients", "0"));
            int maxClients = Integer.parseInt(clients.getOrDefault("maxclients", "0"));

            if (maxClients > 0) {
                double usagePercent = (connected * 100.0) / maxClients;
                double clientsWarning = thresholds.getOrDefault("clients_warning", 80.0);

                if (usagePercent > clientsWarning) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("level", "warning");
                    alert.put("metric", "clients");
                    alert.put("value", usagePercent);
                    alert.put("message", String.format("Client connections are %.1f%%", usagePercent));
                    alerts.add(alert);
                }
            }

            // Проверка репликации
            Map<String, String> replication = jedis.info("replication");
            if ("slave".equals(replication.get("role"))) {
                String masterLinkStatus = replication.get("master_link_status");
                if (!"up".equals(masterLinkStatus)) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("level", "critical");
                    alert.put("metric", "replication");
                    alert.put("value", "down");
                    alert.put("message", "Master link is down");
                    alerts.add(alert);
                }
            }

        } catch (Exception e) {
            System.err.println("Error checking alerts: " + e.getMessage());
        }

        return alerts;
    }
}
```

## Лучшие практики

Собирайте минимальный, но достаточный набор метрик: `used_memory_rss`, `connected_clients`, `instantaneous_ops_per_sec`, `keyspace_hits`/`keyspace_misses` (для hit ratio), при репликации — `master_last_io_seconds_ago` и `repl_backlog_size`. Настройте алерты на пороги (например, память > 80%, лаг репликации > 10 с, отказ реплики), чтобы реагировать до сбоев.

Используйте единый стек (например, Prometheus + Grafana или Redis Insight) и храните дашборды в коде для воспроизводимости. Регулярно проверяйте актуальность алертов и снижайте шум: отключайте срабатывания, не ведущие к действиям. Документируйте процедуры реагирования на типичные срабатывания.


- [Redis Monitoring](https://redis.io/docs/management/monitoring/)
- [Redis Exporter for Prometheus](https://github.com/oliver006/redis_exporter)

