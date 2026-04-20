---
title: "Cassandra: Администрирование — Управление кластером и эксплуатация"
description: "Комплексное руководство по администрированию Apache Cassandra: управление кластером, обслуживание, мониторинг, резервное копирование и восстановление в production средах."
tags:
  - databases
  - nosql
  - cassandra-admin
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Cassandra: Администрирование — Управление кластером и эксплуатация

Комплексное руководство по администрированию **Apache Cassandra**: управление кластером, обслуживание, мониторинг, резервное копирование и восстановление в **production** средах.

## Полезные ссылки

### Официальная документация
- [Cassandra Operations](https://cassandra.apache.org/doc/latest/operating/)
- [Cassandra Administration](https://cassandra.apache.org/doc/latest/operating/)
- [nodetool Documentation](https://cassandra.apache.org/doc/latest/cassandra/tools/nodetool/nodetool.html)

### Дизайн и эксплуатация
- [Cassandra Production Checklist](https://cassandra.apache.org/doc/latest/operating/)
- [Operational Best Practices](https://cassandra.apache.org/doc/latest/operating/)
- [Cassandra Anti-Patterns](https://cassandra.apache.org/doc/latest/cassandra/operating/antipatterns.html)

### Инструменты
- [DataStax OpsCenter](https://docs.datastax.com/en/opscenter/)
- [Cassandra Reaper](https://cassandra-reaper.io/)
- [Medusa Backup Tool](https://github.com/thelastpickle/cassandra-medusa)
- [Cassandra Logging](https://cassandra.apache.org/doc/latest/operating/logging.html)

### См. также
- [Основы](cassandra-basics.md) — **Cassandra**
- [Кластеризация](cassandra-clustering.md) — кластеризация
- [Производительность](cassandra-performance.md) — производительность
- [Моделирование](cassandra-data-modeling.md) — моделирование данных

- [ClickHouse](../clickhouse/clickhouse.md)
## Содержание

- [Ежедневное администрирование](#ежедневное-администрирование)
  - [Проверка здоровья кластера](#проверка-здоровья-кластера)
    - [Статус кластера](#статус-кластера)
    - [Проверка ключевых метрик](#проверка-ключевых-метрик)
    - [Автоматизированная проверка здоровья](#автоматизированная-проверка-здоровья)
  - [Регулярные задачи обслуживания](#регулярные-задачи-обслуживания)
    - [Ежедневные задачи](#ежедневные-задачи)
    - [Еженедельные задачи](#еженедельные-задачи)
    - [Ежемесячные задачи](#ежемесячные-задачи)
- [Мониторинг здоровья кластера](#мониторинг-здоровья-кластера)
  - [Настройка мониторинга](#настройка-мониторинга)
    - [Prometheus + Grafana](#prometheus-grafana)
    - [Cassandra JMX Exporter](#cassandra-jmx-exporter)
    - [Grafana Dashboard](#grafana-dashboard)
  - [Ключевые метрики для мониторинга](#ключевые-метрики-для-мониторинга)
    - [Системные метрики](#системные-метрики)
    - [Cassandra-специфичные метрики](#cassandra-специфичные-метрики)
- [Управление конфигурацией](#управление-конфигурацией)
  - [Конфигурационные файлы](#конфигурационные-файлы)
    - [cassandra.yaml](#cassandrayaml)
    - [jvm.options](#jvmoptions)
  - [Управление конфигурацией](#управление-конфигурацией-1)
    - [Версионирование конфигурации](#версионирование-конфигурации)
    - [Валидация конфигурации](#валидация-конфигурации)
  - [Изменение конфигурации](#изменение-конфигурации)
    - [Безопасное изменение настроек](#безопасное-изменение-настроек)
    - [Rolling restart кластера](#rolling-restart-кластера)
- [Обслуживание и ремонт](#обслуживание-и-ремонт)
  - [Repair операции](#repair-операции)
    - [Автоматизированный repair](#автоматизированный-repair)
  - [Compaction управление](#compaction-управление)
    - [Мониторинг compaction](#мониторинг-compaction)
    - [Оптимизация compaction](#оптимизация-compaction)
- [Резервное копирование](#резервное-копирование)
  - [Стратегии backup](#стратегии-backup)
    - [Полное backup с Medusa](#полное-backup-с-medusa)
    - [Создание backup](#создание-backup)
    - [Автоматизированное backup](#автоматизированное-backup)
  - [Восстановление из backup](#восстановление-из-backup)
    - [Процесс восстановления](#процесс-восстановления)
    - [Тестирование восстановления](#тестирование-восстановления)
- [Восстановление после сбоев](#восстановление-после-сбоев)
  - [Node Failure Recovery](#node-failure-recovery)
    - [Автоматическое восстановление](#автоматическое-восстановление)
  - [Data Center Recovery](#data-center-recovery)
    - [Восстановление датацентра](#восстановление-датацентра)
- [Управление пользователями и безопасностью](#управление-пользователями-и-безопасностью)
  - [Аутентификация и авторизация](#аутентификация-и-авторизация)
    - [Настройка пользователей](#настройка-пользователей)
    - [Управление ролями](#управление-ролями)
  - [Шифрование данных](#шифрование-данных)
    - [Настройка SSL/TLS](#настройка-ssltls)
    - [Управление сертификатами](#управление-сертификатами)
- [Аудит и compliance](#аудит-и-compliance)
  - [Аудит доступа к данным](#аудит-доступа-к-данным)
    - [Настройка аудита](#настройка-аудита)
    - [Анализ audit логов](#анализ-audit-логов)
- [Управление версиями и обновлениями](#управление-версиями-и-обновлениями)
  - [Планирование обновлений](#планирование-обновлений)
    - [Матрица совместимости](#матрица-совместимости)
    - [Процесс обновления](#процесс-обновления)
  - [Тестирование после обновления](#тестирование-после-обновления)
    - [Post-upgrade validation](#post-upgrade-validation)
- [Лучшие практики](#лучшие-практики)
  - [Организационные практики](#организационные-практики)
    - [1. Роли и обязанности](#1-роли-и-обязанности)
    - [2. Процессы и процедуры](#2-процессы-и-процедуры)
    - [3. Документация](#3-документация)
  - [Технические best practices](#технические-best-practices)
    - [1. Capacity Planning](#1-capacity-planning)
    - [2. High Availability](#2-high-availability)
    - [3. Security First](#3-security-first)
    - [4. Performance Culture](#4-performance-culture)
  - [Мониторинг и alerting](#мониторинг-и-alerting)
    - [1. Key Metrics Dashboard](#1-key-metrics-dashboard)
    - [2. Alert Hierarchy](#2-alert-hierarchy)
    - [3. Automated Response](#3-automated-response)
  - [Операционные практики:](#операционные-практики)
  - [Управление изменениями:](#управление-изменениями)
  - [Incident management:](#incident-management)
  - [Best practices:](#best-practices)
  - [Вызовы и решения:](#вызовы-и-решения)
  - [Инструменты и технологии:](#инструменты-и-технологии)
  - [Культура и процессы:](#культура-и-процессы)
- [Решение проблем](#решение-проблем)

## Ежедневное администрирование

### Проверка здоровья кластера

#### Статус кластера

Команды **nodetool** для проверки статуса кластера **Cassandra** (bash).

```bash
# Проверка статуса всех узлов
nodetool status

# Детальный статус с нагрузкой
nodetool status --load

# Статус конкретного датацентра
nodetool status --dc DC1

# Проверка ring состояния
nodetool ring

# Информация о версиях
nodetool version
```

#### Проверка ключевых метрик
```bash
# Проверка нагрузки на узлы
nodetool netstats

# Статистика таблиц
nodetool tablestats

# Статистика compaction
nodetool compactionstats

# Статистика garbage collection
nodetool gcstats

# Проверка потоков
nodetool tpstats
```

#### Автоматизированная проверка здоровья
```java
@Service
public class ClusterHealthChecker {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private MetricsCollector metricsCollector;

    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void performHealthCheck() {
        HealthCheckResult result = new HealthCheckResult();

        // Проверка доступности узлов
        result.setNodeAvailability(checkNodeAvailability());

        // Проверка консистентности данных
        result.setDataConsistency(checkDataConsistency());

        // Проверка производительности
        result.setPerformanceMetrics(checkPerformanceMetrics());

        // Проверка дискового пространства
        result.setDiskSpace(checkDiskSpace());

        // Проверка replication health
        result.setReplicationHealth(checkReplicationHealth());

        // Отправка алертов при проблемах
        if (!result.isHealthy()) {
            sendHealthAlert(result);
        }

        // Логирование результатов
        logHealthCheckResult(result);
    }

    private NodeAvailability checkNodeAvailability() {
        List<Node> nodes = clusterManager.getAllNodes();
        int upNodes = 0;
        int downNodes = 0;

        for (Node node : nodes) {
            if (node.isUp()) {
                upNodes++;
            } else {
                downNodes++;
                log.warn("Node {} is DOWN", node.getAddress());
            }
        }

        return new NodeAvailability(upNodes, downNodes, nodes.size());
    }

    private DataConsistency checkDataConsistency() {
        // Проверка консистентности через read repair
        Map<String, Double> inconsistencyRates = new HashMap<>();

        for (String keyspace : clusterManager.getKeyspaces()) {
            double rate = calculateInconsistencyRate(keyspace);
            inconsistencyRates.put(keyspace, rate);

            if (rate > 0.01) { // > 1% inconsistency
                log.warn("High inconsistency rate in keyspace {}: {}%", keyspace, rate * 100);
            }
        }

        return new DataConsistency(inconsistencyRates);
    }

    private PerformanceMetrics checkPerformanceMetrics() {
        double avgReadLatency = metricsCollector.getAverageReadLatency();
        double avgWriteLatency = metricsCollector.getAverageWriteLatency();
        double errorRate = metricsCollector.getErrorRate();

        // Пороги для алертов
        boolean latencyOk = avgReadLatency < 50 && avgWriteLatency < 20; // ms
        boolean errorsOk = errorRate < 0.01; // 1%

        return new PerformanceMetrics(avgReadLatency, avgWriteLatency, errorRate, latencyOk && errorsOk);
    }

    private DiskSpace checkDiskSpace() {
        Map<Node, Double> diskUsage = new HashMap<>();

        for (Node node : clusterManager.getAllNodes()) {
            double usage = node.getDiskUsage();
            diskUsage.put(node, usage);

            if (usage > 0.85) { // > 85% usage
                log.warn("High disk usage on node {}: {}%", node.getAddress(), usage * 100);
            }
        }

        return new DiskSpace(diskUsage);
    }

    private ReplicationHealth checkReplicationHealth() {
        Map<String, Integer> replicationIssues = new HashMap<>();

        // Проверка репликации для каждого keyspace
        for (String keyspace : clusterManager.getKeyspaces()) {
            int issues = countReplicationIssues(keyspace);
            replicationIssues.put(keyspace, issues);

            if (issues > 0) {
                log.warn("Replication issues in keyspace {}: {} issues", keyspace, issues);
            }
        }

        return new ReplicationHealth(replicationIssues);
    }

    private void sendHealthAlert(HealthCheckResult result) {
        StringBuilder alert = new StringBuilder("Cassandra Health Check Alert:\n");

        if (!result.getNodeAvailability().isAllUp()) {
            alert.append("- Node availability issues\n");
        }

        if (!result.getDataConsistency().isConsistent()) {
            alert.append("- Data consistency issues\n");
        }

        if (!result.getPerformanceMetrics().isOk()) {
            alert.append("- Performance issues\n");
        }

        if (!result.getDiskSpace().isOk()) {
            alert.append("- Disk space issues\n");
        }

        // Отправка алерта
        alertService.sendAlert("Cassandra Health Check", alert.toString(), AlertSeverity.WARNING);
    }

    private double calculateInconsistencyRate(String keyspace) {
        // Проверка через read repair или специальные запросы
        // Упрощенная реализация
        return 0.005; // 0.5% inconsistency rate
    }

    private int countReplicationIssues(String keyspace) {
        // Проверка replication factor vs actual replicas
        return 0; // placeholder
    }
}

class HealthCheckResult {
    private NodeAvailability nodeAvailability;
    private DataConsistency dataConsistency;
    private PerformanceMetrics performanceMetrics;
    private DiskSpace diskSpace;
    private ReplicationHealth replicationHealth;

    public boolean isHealthy() {
        return nodeAvailability.isAllUp() &&
               dataConsistency.isConsistent() &&
               performanceMetrics.isOk() &&
               diskSpace.isOk() &&
               replicationHealth.isOk();
    }

    // getters and setters
}

class NodeAvailability {
    private int upNodes;
    private int downNodes;
    private int totalNodes;

    public boolean isAllUp() {
        return downNodes == 0;
    }

    // constructor, getters
}
```

### Регулярные задачи обслуживания

#### Ежедневные задачи
```bash
# 1. Проверка логов на ошибки
grep "ERROR\|WARN" /var/log/cassandra/system.log | tail -20

# 2. Проверка дискового пространства
df -h /var/lib/cassandra

# 3. Проверка нагрузки на узлы
nodetool status

# 4. Проверка pending операций
nodetool tpstats

# 5. Ротация логов (если не автоматическая)
logrotate /etc/logrotate.d/cassandra
```

#### Еженедельные задачи
```bash
# 1. Запуск repair для всех keyspaces
for keyspace in $(nodetool describecluster | grep "Keyspace:" | awk '{print $2}'); do
    echo "Running repair for keyspace: $keyspace"
    nodetool repair $keyspace
done

# 2. Очистка snapshots старше 7 дней
nodetool listsnapshots | grep -E " [0-9]{4}-[0-9]{2}-[0-9]{2} " | awk '$2 < "'$(date -d '7 days ago' +%Y-%m-%d)'" {print $1}' | xargs -I {} nodetool clearsnapshot {}

# 3. Проверка консистентности данных
nodetool scrub

# 4. Очистка commit log (если необходимо)
nodetool compact
```

#### Ежемесячные задачи
```bash
# 1. Создание полного backup
# Использование Medusa или другого инструмента

# 2. Проверка hardware health
# Проверка дисков, памяти, CPU

# 3. Обновление JVM (если необходимо)
# Тестирование в staging среде

# 4. Аудит конфигурации
# Сравнение с best practices

# 5. Performance baseline
# Запуск benchmark тестов
```

## Мониторинг здоровья кластера

### Настройка мониторинга

#### Prometheus + Grafana
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'cassandra'
    static_configs:
      - targets: ['cassandra-node1:7070', 'cassandra-node2:7070']
    scrape_interval: 15s
    metrics_path: '/metrics'

  - job_name: 'cassandra-jmx'
    static_configs:
      - targets: ['cassandra-node1:7199', 'cassandra-node2:7199']
    scrape_interval: 30s
```

#### Cassandra JMX Exporter
```yaml
# JMX Exporter configuration
startDelaySeconds: 0
hostPort: 127.0.0.1:7199
ssl: false
lowercaseOutputName: false
lowercaseOutputLabelNames: false

whitelistObjectNames:
  - org.apache.cassandra.metrics:*

rules:
  - pattern: org.apache.cassandra.metrics<type=(\w+), name=(\w+)><>(\w+)
    name: cassandra_$1_$2_$3
    type: GAUGE

  - pattern: org.apache.cassandra.metrics<type=(\w+), name=(\w+), scope=(\w+)><>(\w+)
    name: cassandra_$1_$2_$3_$4
    labels:
      scope: "$3"
    type: GAUGE
```

#### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "Cassandra Cluster Overview",
    "panels": [
      {
        "title": "Cluster Status",
        "type": "stat",
        "targets": [
          {
            "expr": "up{job=\"cassandra\"}",
            "legendFormat": "Nodes Up"
          }
        ]
      },
      {
        "title": "Read Latency",
        "type": "graph",
        "targets": [
          {
            "expr": "cassandra_clientrequest_read_latency_mean",
            "legendFormat": "Read Latency (ms)"
          }
        ]
      },
      {
        "title": "Write Latency",
        "type": "graph",
        "targets": [
          {
            "expr": "cassandra_clientrequest_write_latency_mean",
            "legendFormat": "Write Latency (ms)"
          }
        ]
      },
      {
        "title": "Disk Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "cassandra_storage_load",
            "legendFormat": "Disk Usage"
          }
        ]
      }
    ]
  }
}
```

### Ключевые метрики для мониторинга

#### Системные метрики
```java
@Service
public class SystemMetricsMonitor {

    @Autowired
    private MetricsRegistry registry;

    public void registerSystemMetrics() {
        // CPU метрики
        registry.register("cpu_usage", () -> getCpuUsage());
        registry.register("cpu_load_average", () -> getLoadAverage());

        // Memory метрики
        registry.register("memory_heap_used", () -> getHeapUsed());
        registry.register("memory_heap_max", () -> getHeapMax());
        registry.register("memory_off_heap_used", () -> getOffHeapUsed());

        // Disk метрики
        registry.register("disk_usage_data", () -> getDataDiskUsage());
        registry.register("disk_usage_commitlog", () -> getCommitLogDiskUsage());
        registry.register("disk_iops", () -> getDiskIOPS());

        // Network метрики
        registry.register("network_bytes_in", () -> getNetworkBytesIn());
        registry.register("network_bytes_out", () -> getNetworkBytesOut());
        registry.register("network_connections", () -> getActiveConnections());
    }

    private double getCpuUsage() {
        // Получение CPU usage через OperatingSystemMXBean
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemCpuLoad() * 100;
    }

    private double getLoadAverage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemLoadAverage();
    }

    private long getHeapUsed() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    private long getHeapMax() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getMax();
    }

    private long getOffHeapUsed() {
        // Расчет off-heap memory usage
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() -
               getHeapUsed();
    }

    private double getDataDiskUsage() {
        // Проверка disk usage для data directory
        return getDiskUsage("/var/lib/cassandra/data");
    }

    private double getCommitLogDiskUsage() {
        return getDiskUsage("/var/lib/cassandra/commitlog");
    }

    private long getDiskIOPS() {
        // Получение IOPS через iostat или системные метрики
        return 0; // placeholder
    }

    private long getNetworkBytesIn() {
        return 0; // placeholder - network statistics
    }

    private long getNetworkBytesOut() {
        return 0; // placeholder - network statistics
    }

    private int getActiveConnections() {
        return 0; // placeholder - active connections count
    }

    private double getDiskUsage(String path) {
        try {
            File file = new File(path);
            long totalSpace = file.getTotalSpace();
            long freeSpace = file.getFreeSpace();
            return (double) (totalSpace - freeSpace) / totalSpace;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
```

#### Cassandra-специфичные метрики
```java
@Service
public class CassandraMetricsMonitor {

    @Autowired
    private MetricsRegistry registry;

    @Autowired
    private JmxClient jmxClient;

    public void registerCassandraMetrics() {
        // Read/Write latency
        registry.register("cassandra_read_latency_p50", () -> getReadLatencyP50());
        registry.register("cassandra_read_latency_p95", () -> getReadLatencyP95());
        registry.register("cassandra_read_latency_p99", () -> getReadLatencyP99());

        registry.register("cassandra_write_latency_p50", () -> getWriteLatencyP50());
        registry.register("cassandra_write_latency_p95", () -> getWriteLatencyP95());
        registry.register("cassandra_write_latency_p99", () -> getWriteLatencyP99());

        // Throughput
        registry.register("cassandra_read_throughput", () -> getReadThroughput());
        registry.register("cassandra_write_throughput", () -> getWriteThroughput());

        // Cache metrics
        registry.register("cassandra_key_cache_hit_rate", () -> getKeyCacheHitRate());
        registry.register("cassandra_row_cache_hit_rate", () -> getRowCacheHitRate());

        // Compaction metrics
        registry.register("cassandra_pending_compactions", () -> getPendingCompactions());
        registry.register("cassandra_compaction_bytes_per_sec", () -> getCompactionThroughput());

        // Storage metrics
        registry.register("cassandra_total_disk_space", () -> getTotalDiskSpace());
        registry.register("cassandra_used_disk_space", () -> getUsedDiskSpace());

        // Connection metrics
        registry.register("cassandra_active_connections", () -> getActiveConnections());
        registry.register("cassandra_total_connections", () -> getTotalConnections());

        // Error metrics
        registry.register("cassandra_read_errors", () -> getReadErrors());
        registry.register("cassandra_write_errors", () -> getWriteErrors());
        registry.register("cassandra_timeout_errors", () -> getTimeoutErrors());
    }

    private double getReadLatencyP50() {
        return jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency",
            "50p"
        );
    }

    private double getReadLatencyP95() {
        return jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency",
            "95p"
        );
    }

    private double getReadLatencyP99() {
        return jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency",
            "99p"
        );
    }

    private double getKeyCacheHitRate() {
        long hits = jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=Cache,scope=KeyCache,name=Hits",
            "Count"
        );
        long requests = jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=Cache,scope=KeyCache,name=Requests",
            "Count"
        );

        return requests > 0 ? (double) hits / requests : 0.0;
    }

    private int getPendingCompactions() {
        return jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=Compaction,name=PendingTasks",
            "Value"
        );
    }

    private long getTotalDiskSpace() {
        return jmxClient.getAttributeValue(
            "org.apache.cassandra.metrics:type=Storage,name=Load",
            "Count"
        );
    }

    // Другие методы получения метрик...
    private long getReadThroughput() { return 0; }
    private long getWriteThroughput() { return 0; }
    private double getRowCacheHitRate() { return 0; }
    private long getCompactionThroughput() { return 0; }
    private long getUsedDiskSpace() { return 0; }
    private int getActiveConnections() { return 0; }
    private int getTotalConnections() { return 0; }
    private long getReadErrors() { return 0; }
    private long getWriteErrors() { return 0; }
    private long getTimeoutErrors() { return 0; }
    private double getWriteLatencyP50() { return 0; }
    private double getWriteLatencyP95() { return 0; }
    private double getWriteLatencyP99() { return 0; }
}
```

## Управление конфигурацией

### Конфигурационные файлы

#### cassandra.yaml
```yaml
# Основные параметры кластера
cluster_name: 'Production Cluster'
seeds: "10.0.1.10,10.0.1.11,10.0.1.12"

# Параметры узла
listen_address: 10.0.1.10
rpc_address: 10.0.1.10

# Каталоги данных
data_file_directories:
  - /var/lib/cassandra/data

commitlog_directory: /var/lib/cassandra/commitlog
saved_caches_directory: /var/lib/cassandra/saved_caches

# Параметры JVM
heap_size: 8G
young_gen_size: 2G

# Параметры сети
native_transport_port: 9042
rpc_port: 9160

# Параметры безопасности
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer

# Параметры репликации
hinted_handoff_enabled: true
batchlog_replay_throttle_in_kb: 1024

# Параметры обслуживания
concurrent_compactors: 4
compaction_throughput_mb_per_sec: 64
stream_throughput_outbound_megabits_per_sec: 200
```

#### jvm.options
```bash
# Heap settings
-Xms8g
-Xmx8g
-Xmn2g

# GC settings
-XX:+UseG1GC
-XX:G1RSetUpdatingPauseTimePercent=5
-XX:MaxGCPauseMillis=500
-XX:G1HeapRegionSize=32m

# Memory settings
-XX:+UseLargePages
-XX:LargePageSizeInBytes=2m

# Diagnostic settings
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintHeapAtGC
-XX:+PrintTenuringDistribution
-XX:+PrintGCApplicationStoppedTime
-Xloggc:/var/log/cassandra/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=10
-XX:GCLogFileSize=10m

# JMX settings
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=7199
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false

# Cassandra specific
-Dcassandra.jmx.local.port=7199
-Dcassandra.logdir=/var/log/cassandra
-Dcassandra.storagedir=/var/lib/cassandra
```

### Управление конфигурацией

#### Версионирование конфигурации
```bash
# Структура директории конфигурации
/etc/cassandra/
├── cassandra.yaml
├── jvm.options
├── logback.xml
├── cassandra-env.sh
├── cassandra-rackdc.properties
└── commitlog_archiving.properties

# Версионирование с Git
cd /etc/cassandra
git init
git add .
git commit -m "Initial Cassandra configuration"

# Создание тегов для версий
git tag -a v3.11.10 -m "Cassandra 3.11.10 configuration"
```

#### Валидация конфигурации
```java
@Service
public class ConfigurationValidator {

    public ValidationResult validateConfiguration(CassandraConfig config) {
        ValidationResult result = new ValidationResult();

        // Валидация основных параметров
        validateClusterName(config, result);
        validateSeeds(config, result);
        validateDirectories(config, result);
        validateMemorySettings(config, result);
        validateNetworkSettings(config, result);
        validateSecuritySettings(config, result);

        return result;
    }

    private void validateClusterName(CassandraConfig config, ValidationResult result) {
        String clusterName = config.getClusterName();

        if (clusterName == null || clusterName.trim().isEmpty()) {
            result.addError("cluster_name cannot be empty");
        }

        if (clusterName.length() > 63) {
            result.addWarning("cluster_name is too long (>63 characters)");
        }

        // Проверка на специальные символы
        if (!clusterName.matches("[a-zA-Z0-9_-]+")) {
            result.addError("cluster_name contains invalid characters");
        }
    }

    private void validateSeeds(CassandraConfig config, ValidationResult result) {
        List<String> seeds = config.getSeeds();

        if (seeds == null || seeds.isEmpty()) {
            result.addError("seeds list cannot be empty");
        }

        // Рекомендация: 2-3 seeds на датацентр
        if (seeds.size() < 2) {
            result.addWarning("Consider using at least 2 seeds per datacenter");
        }

        // Валидация IP адресов
        for (String seed : seeds) {
            if (!isValidIPAddress(seed)) {
                result.addError("Invalid seed IP address: " + seed);
            }
        }
    }

    private void validateDirectories(CassandraConfig config, ValidationResult result) {
        List<String> dataDirs = config.getDataFileDirectories();

        if (dataDirs == null || dataDirs.isEmpty()) {
            result.addError("data_file_directories cannot be empty");
        }

        // Проверка доступности директорий
        for (String dir : dataDirs) {
            File directory = new File(dir);
            if (!directory.exists()) {
                result.addError("Data directory does not exist: " + dir);
            }

            if (!directory.canWrite()) {
                result.addError("Data directory is not writable: " + dir);
            }

            // Проверка свободного места
            long freeSpace = directory.getFreeSpace();
            if (freeSpace < 10L * 1024 * 1024 * 1024) { // 10GB
                result.addWarning("Low free space in data directory: " + dir);
            }
        }
    }

    private void validateMemorySettings(CassandraConfig config, ValidationResult result) {
        long heapSize = config.getHeapSize();
        long systemMemory = getSystemMemory();

        // Heap не должен превышать 75% системной памяти
        if (heapSize > systemMemory * 0.75) {
            result.addWarning("Heap size is too large (>75% of system memory)");
        }

        // Heap не должен быть слишком маленьким
        if (heapSize < 1L * 1024 * 1024 * 1024) { // 1GB
            result.addWarning("Heap size is too small (<1GB)");
        }

        // Проверка Young generation
        long youngGenSize = config.getYoungGenSize();
        if (youngGenSize > heapSize * 0.5) {
            result.addWarning("Young generation is too large (>50% of heap)");
        }
    }

    private void validateNetworkSettings(CassandraConfig config, ValidationResult result) {
        int nativePort = config.getNativeTransportPort();
        int rpcPort = config.getRpcPort();

        // Проверка занятости портов
        if (isPortInUse(nativePort)) {
            result.addError("Native transport port is already in use: " + nativePort);
        }

        if (isPortInUse(rpcPort)) {
            result.addError("RPC port is already in use: " + rpcPort);
        }

        // Проверка listen_address
        String listenAddress = config.getListenAddress();
        if (!isValidIPAddress(listenAddress)) {
            result.addError("Invalid listen_address: " + listenAddress);
        }
    }

    private void validateSecuritySettings(CassandraConfig config, ValidationResult result) {
        String authenticator = config.getAuthenticator();
        String authorizer = config.getAuthorizer();

        // Предупреждение о безопасности
        if ("AllowAllAuthenticator".equals(authenticator)) {
            result.addWarning("Using AllowAllAuthenticator - consider enabling authentication");
        }

        if ("AllowAllAuthorizer".equals(authorizer)) {
            result.addWarning("Using AllowAllAuthorizer - consider enabling authorization");
        }

        // Проверка SSL настроек
        if (config.isClientEncryptionEnabled()) {
            if (config.getKeystorePath() == null) {
                result.addError("client_encryption_options enabled but keystore not configured");
            }
        }
    }

    private boolean isValidIPAddress(String ip) {
        // Простая валидация IP адреса
        return ip != null && ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }

    private boolean isPortInUse(int port) {
        // Проверка занятости порта
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private long getSystemMemory() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getTotalPhysicalMemorySize();
    }
}

class ValidationResult {
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
```

### Изменение конфигурации

#### Безопасное изменение настроек
```bash
# 1. Создание backup текущей конфигурации
cp /etc/cassandra/cassandra.yaml /etc/cassandra/cassandra.yaml.backup

# 2. Изменение настроек
vim /etc/cassandra/cassandra.yaml

# 3. Валидация конфигурации
cassandra -Dcassandra.config=file:///etc/cassandra/cassandra.yaml -f

# 4. Перезапуск узла (rolling restart)
nodetool drain  # Ожидание завершения операций
sudo systemctl restart cassandra

# 5. Проверка статуса
nodetool status
```

#### Rolling restart кластера
```java
@Service
public class RollingRestartManager {

    @Autowired
    private ClusterManager clusterManager;

    public void performRollingRestart(RestartReason reason) {
        List<Node> nodes = clusterManager.getAllNodes();

        // Сортировка по load (начинаем с наименее нагруженных)
        nodes.sort(Comparator.comparing(Node::getCurrentLoad));

        for (Node node : nodes) {
            try {
                log.info("Starting rolling restart for node: {}", node.getAddress());

                // 1. Проверка возможности перезапуска
                validateNodeForRestart(node);

                // 2. Drain операций
                drainNode(node);

                // 3. Ожидание завершения
                waitForDrainComplete(node);

                // 4. Перезапуск
                restartNode(node);

                // 5. Ожидание восстановления
                waitForNodeRecovery(node);

                // 6. Проверка здоровья
                validateNodeHealth(node);

                log.info("Successfully restarted node: {}", node.getAddress());

                // Ожидание перед следующим узлом
                Thread.sleep(30000); // 30 seconds

            } catch (Exception e) {
                log.error("Failed to restart node: {}", node.getAddress(), e);
                handleRestartFailure(node, e);
                break; // Остановка rolling restart при ошибке
            }
        }
    }

    private void validateNodeForRestart(Node node) {
        // Проверка что узел не единственный seed
        if (node.isSeed() && clusterManager.getSeedNodes().size() == 1) {
            throw new IllegalStateException("Cannot restart the only seed node");
        }

        // Проверка нагрузки
        if (node.getCurrentLoad() > 0.8) {
            log.warn("Node {} has high load ({}%) - consider draining first",
                    node.getAddress(), node.getCurrentLoad() * 100);
        }
    }

    private void drainNode(Node node) {
        executeNodetoolCommand(node, "drain");
    }

    private void waitForDrainComplete(Node node) {
        long timeoutMs = 5 * 60 * 1000; // 5 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (node.getPendingOperations() == 0) {
                return; // Drain завершен
            }

            try {
                Thread.sleep(5000); // Проверка каждые 5 секунд
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Drain did not complete within timeout");
    }

    private void restartNode(Node node) {
        // Перезапуск через systemctl или другой сервис менеджер
        executeCommand("sudo systemctl restart cassandra");
    }

    private void waitForNodeRecovery(Node node) {
        long timeoutMs = 10 * 60 * 1000; // 10 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (node.isUp() && node.isHealthy()) {
                return; // Узел восстановился
            }

            try {
                Thread.sleep(10000); // Проверка каждые 10 секунд
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Node did not recover within timeout");
    }

    private void validateNodeHealth(Node node) {
        // Проверка основных метрик
        if (node.getCpuUsage() > 0.9) {
            log.warn("High CPU usage after restart on node: {}", node.getAddress());
        }

        if (node.getMemoryUsage() > 0.9) {
            log.warn("High memory usage after restart on node: {}", node.getAddress());
        }

        // Проверка Cassandra метрик
        if (node.getReadLatency() > 100) {
            log.warn("High read latency after restart on node: {}", node.getAddress());
        }
    }

    private void handleRestartFailure(Node node, Exception e) {
        log.error("Rolling restart failed on node: {}", node.getAddress(), e);

        // Попытка восстановления
        try {
            forceNodeRestart(node);
        } catch (Exception recoveryException) {
            log.error("Failed to recover node: {}", node.getAddress(), recoveryException);
            // Escalation to human intervention
            alertService.sendCriticalAlert(
                "Rolling restart failed",
                "Node " + node.getAddress() + " failed to restart and recover"
            );
        }
    }

    private void forceNodeRestart(Node node) {
        // Force kill и перезапуск
        executeCommand("sudo systemctl stop cassandra");
        Thread.sleep(5000); // Ждем остановки
        executeCommand("sudo systemctl start cassandra");
    }
}

enum RestartReason {
    CONFIGURATION_CHANGE,
    VERSION_UPGRADE,
    MAINTENANCE_WINDOW,
    EMERGENCY_RESTART
}
```

## Обслуживание и ремонт

### Repair операции

#### Автоматизированный repair
```java
@Service
public class RepairScheduler {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private RepairHistoryRepository repairHistory;

    @Scheduled(cron = "0 2 * * 0") // Каждое воскресенье в 02:00
    public void performWeeklyRepair() {
        List<String> keyspaces = clusterManager.getUserKeyspaces();

        for (String keyspace : keyspaces) {
            try {
                log.info("Starting repair for keyspace: {}", keyspace);

                // Проверка необходимости repair
                if (needsRepair(keyspace)) {
                    performRepair(keyspace);
                    recordRepairSuccess(keyspace);
                } else {
                    log.info("Repair not needed for keyspace: {}", keyspace);
                }

            } catch (Exception e) {
                log.error("Repair failed for keyspace: {}", keyspace, e);
                recordRepairFailure(keyspace, e);
            }
        }
    }

    private boolean needsRepair(String keyspace) {
        // Проверка на основе времени последнего repair
        RepairHistory lastRepair = repairHistory.findLastRepair(keyspace);

        if (lastRepair == null) {
            return true; // Первый repair
        }

        // Repair каждые 7 дней
        long daysSinceLastRepair = ChronoUnit.DAYS.between(
            lastRepair.getCompletedAt(), LocalDateTime.now()
        );

        return daysSinceLastRepair >= 7;
    }

    private void performRepair(String keyspace) {
        // Получение всех узлов
        List<Node> nodes = clusterManager.getAllNodes();

        // Repair по одному узлу за раз
        for (Node node : nodes) {
            try {
                log.info("Repairing keyspace {} on node {}", keyspace, node.getAddress());

                // Запуск repair
                executeRepair(node, keyspace);

                // Ожидание завершения
                waitForRepairComplete(node, keyspace);

                log.info("Repair completed for keyspace {} on node {}",
                        keyspace, node.getAddress());

            } catch (Exception e) {
                log.error("Repair failed on node {} for keyspace {}",
                         node.getAddress(), keyspace, e);
                throw e;
            }
        }
    }

    private void executeRepair(Node node, String keyspace) {
        // nodetool repair -pr keyspace (partitioner range only)
        executeNodetoolCommand(node, "repair -pr " + keyspace);
    }

    private void waitForRepairComplete(Node node, String keyspace) {
        long timeoutMs = 24 * 60 * 60 * 1000; // 24 hours
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (!isRepairRunning(node, keyspace)) {
                return; // Repair завершен
            }

            try {
                Thread.sleep(60000); // Проверка каждую минуту
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Repair did not complete within timeout");
    }

    private boolean isRepairRunning(Node node, String keyspace) {
        // Проверка через JMX или nodetool compactionstats
        String output = executeNodetoolCommand(node, "compactionstats");
        return output.contains("repair");
    }

    private void recordRepairSuccess(String keyspace) {
        RepairHistory history = new RepairHistory();
        history.setKeyspace(keyspace);
        history.setStatus(RepairStatus.SUCCESS);
        history.setCompletedAt(LocalDateTime.now());
        repairHistory.save(history);
    }

    private void recordRepairFailure(String keyspace, Exception e) {
        RepairHistory history = new RepairHistory();
        history.setKeyspace(keyspace);
        history.setStatus(RepairStatus.FAILED);
        history.setErrorMessage(e.getMessage());
        history.setCompletedAt(LocalDateTime.now());
        repairHistory.save(history);
    }
}

enum RepairStatus {
    SUCCESS, FAILED, IN_PROGRESS
}

@Entity
class RepairHistory {
    @Id
    private Long id;

    private String keyspace;
    private RepairStatus status;
    private LocalDateTime completedAt;
    private String errorMessage;

    // getters and setters
}
```

### Compaction управление

#### Мониторинг compaction
```bash
# Статистика compaction
nodetool compactionstats

# Детальная информация
nodetool compactionhistory

# Остановка compaction (осторожно!)
nodetool stop COMPACTION <compaction_id>

# Принудительная major compaction (не рекомендуется)
nodetool compact
```

#### Оптимизация compaction
```java
@Service
public class CompactionOptimizer {

    @Autowired
    private MetricsCollector metrics;

    public CompactionOptimization recommendOptimization() {
        Map<String, TableMetrics> tableMetrics = metrics.getTableMetrics();

        CompactionOptimization optimization = new CompactionOptimization();

        for (Map.Entry<String, TableMetrics> entry : tableMetrics.entrySet()) {
            String table = entry.getKey();
            TableMetrics metrics = entry.getValue();

            // Анализ паттернов записи
            WritePattern pattern = analyzeWritePattern(metrics);

            // Рекомендация стратегии
            CompactionStrategy recommended = recommendStrategy(pattern, metrics);

            if (recommended != metrics.getCurrentStrategy()) {
                optimization.addRecommendation(table, recommended);
            }
        }

        return optimization;
    }

    private WritePattern analyzeWritePattern(TableMetrics metrics) {
        double writeLoad = metrics.getWriteThroughput();
        double readLoad = metrics.getReadThroughput();
        int sstableCount = metrics.getSstableCount();

        if (writeLoad > readLoad * 2 && sstableCount > 50) {
            return WritePattern.HEAVY_WRITE;
        } else if (readLoad > writeLoad * 2) {
            return WritePattern.HEAVY_READ;
        } else if (metrics.isTimeSeriesData()) {
            return WritePattern.TIME_SERIES;
        } else {
            return WritePattern.BALANCED;
        }
    }

    private CompactionStrategy recommendStrategy(WritePattern pattern, TableMetrics metrics) {
        switch (pattern) {
            case HEAVY_WRITE:
                return CompactionStrategy.SIZE_TIERED_COMPACTION;
            case HEAVY_READ:
                return CompactionStrategy.LEVELED_COMPACTION;
            case TIME_SERIES:
                return CompactionStrategy.TIME_WINDOW_COMPACTION;
            case BALANCED:
            default:
                return CompactionStrategy.LEVELED_COMPACTION;
        }
    }

    public void applyOptimization(CompactionOptimization optimization) {
        for (Map.Entry<String, CompactionStrategy> entry : optimization.getRecommendations().entrySet()) {
            String table = entry.getKey();
            CompactionStrategy strategy = entry.getValue();

            try {
                // Изменение стратегии compaction
                alterTableCompaction(table, strategy);

                // Запуск major compaction для применения изменений
                runMajorCompaction(table);

                log.info("Applied compaction optimization for table: {}", table);

            } catch (Exception e) {
                log.error("Failed to optimize compaction for table: {}", table, e);
            }
        }
    }

    private void alterTableCompaction(String table, CompactionStrategy strategy) {
        String cql = String.format(
            "ALTER TABLE %s WITH compaction = %s",
            table, getCompactionConfig(strategy)
        );

        executeCqlCommand(cql);
    }

    private String getCompactionConfig(CompactionStrategy strategy) {
        switch (strategy) {
            case SIZE_TIERED_COMPACTION:
                return "{'class': 'SizeTieredCompactionStrategy', 'min_threshold': 4, 'max_threshold': 32}";
            case LEVELED_COMPACTION:
                return "{'class': 'LeveledCompactionStrategy', 'sstable_size_in_mb': 160}";
            case TIME_WINDOW_COMPACTION:
                return "{'class': 'TimeWindowCompactionStrategy', 'compaction_window_unit': 'DAYS', 'compaction_window_size': 1}";
            default:
                return "{'class': 'LeveledCompactionStrategy'}";
        }
    }

    private void runMajorCompaction(String table) {
        // nodetool compact keyspace.table
        String[] parts = table.split("\\.");
        executeNodetoolCommand("compact " + parts[0] + " " + parts[1]);
    }
}

enum WritePattern {
    HEAVY_WRITE, HEAVY_READ, TIME_SERIES, BALANCED
}

enum CompactionStrategy {
    SIZE_TIERED_COMPACTION, LEVELED_COMPACTION, TIME_WINDOW_COMPACTION
}

class CompactionOptimization {
    private Map<String, CompactionStrategy> recommendations = new HashMap<>();

    public void addRecommendation(String table, CompactionStrategy strategy) {
        recommendations.put(table, strategy);
    }

    public Map<String, CompactionStrategy> getRecommendations() {
        return recommendations;
    }
}
```

## Резервное копирование

### Стратегии backup

#### Полное backup с Medusa
```yaml
# /etc/medusa/medusa.ini
[cassandra]
seed = 192.168.1.10
use_sudo = true
cassandra_config_directory = /etc/cassandra
ssh_username = cassandra_backup
ssh_key_file = /etc/medusa/ssh_key

[storage]
storage_provider = s3
bucket_name = cassandra-backups-prod
key_file = /etc/medusa/s3_key
prefix = backups
region = us-west-2

[monitoring]
monitoring_provider = local

[grpc]
grpc_enabled = true
grpc_port = 50051
```

#### Создание backup
```bash
# Создание полного backup
medusa backup --name full_backup_$(date +%Y%m%d)

# Создание incremental backup
medusa backup --name incremental_backup_$(date +%Y%m%d) --mode incremental

# Список backups
medusa list-backups

# Проверка статуса backup
medusa status
```

#### Автоматизированное backup
```java
@Service
public class AutomatedBackupService {

    @Autowired
    private BackupScheduler scheduler;

    @Autowired
    private BackupValidator validator;

    @PostConstruct
    public void initializeBackupSchedule() {
        // Еженедельный полный backup
        scheduler.scheduleBackup(
            "full_weekly",
            "0 2 * * 1", // Каждый понедельник в 02:00
            BackupType.FULL
        );

        // Ежедневный incremental backup
        scheduler.scheduleBackup(
            "incremental_daily",
            "0 3 * * *", // Каждый день в 03:00
            BackupType.INCREMENTAL
        );

        // Backup commit logs каждый час
        scheduler.scheduleBackup(
            "commitlog_hourly",
            "0 * * * *", // Каждый час
            BackupType.COMMITLOG
        );
    }

    @Scheduled(fixedRate = 3600000) // Каждый час
    public void validateBackupIntegrity() {
        List<BackupInfo> recentBackups = getRecentBackups();

        for (BackupInfo backup : recentBackups) {
            try {
                validator.validateBackup(backup);

                if (!backup.isValid()) {
                    log.error("Backup validation failed: {}", backup.getName());
                    alertService.sendAlert("Backup Validation Failed",
                                         "Backup " + backup.getName() + " is corrupted");
                }

            } catch (Exception e) {
                log.error("Failed to validate backup: {}", backup.getName(), e);
            }
        }
    }

    public void performManualBackup(String name, BackupType type) {
        try {
            log.info("Starting manual backup: {} ({})", name, type);

            // Создание backup
            BackupInfo backup = createBackup(name, type);

            // Валидация
            validator.validateBackup(backup);

            // Уведомление
            notificationService.sendNotification(
                "Backup Completed",
                "Manual backup '" + name + "' completed successfully"
            );

            log.info("Manual backup completed: {}", name);

        } catch (Exception e) {
            log.error("Manual backup failed: {}", name, e);
            alertService.sendCriticalAlert("Manual Backup Failed",
                                         "Backup '" + name + "' failed: " + e.getMessage());
        }
    }

    private BackupInfo createBackup(String name, BackupType type) {
        BackupInfo backup = new BackupInfo();
        backup.setName(name);
        backup.setType(type);
        backup.setStartedAt(Instant.now());

        try {
            switch (type) {
                case FULL:
                    executeMedusaCommand("backup --name " + name);
                    break;
                case INCREMENTAL:
                    executeMedusaCommand("backup --name " + name + " --mode incremental");
                    break;
                case COMMITLOG:
                    executeMedusaCommand("backup --name " + name + " --mode commitlog");
                    break;
            }

            backup.setCompletedAt(Instant.now());
            backup.setStatus(BackupStatus.SUCCESS);

        } catch (Exception e) {
            backup.setCompletedAt(Instant.now());
            backup.setStatus(BackupStatus.FAILED);
            backup.setErrorMessage(e.getMessage());
        }

        return backupRepository.save(backup);
    }

    private void executeMedusaCommand(String command) {
        // Выполнение medusa команды
        ProcessBuilder pb = new ProcessBuilder("medusa", command.split(" "));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Medusa command failed with exit code: " + exitCode);
        }
    }

    private List<BackupInfo> getRecentBackups() {
        // Получение backups за последние 24 часа
        Instant since = Instant.now().minus(24, ChronoUnit.HOURS);
        return backupRepository.findByCreatedAtAfter(since);
    }
}

enum BackupType {
    FULL, INCREMENTAL, COMMITLOG
}

enum BackupStatus {
    SUCCESS, FAILED, IN_PROGRESS
}

@Entity
class BackupInfo {
    @Id
    private Long id;

    private String name;
    private BackupType type;
    private BackupStatus status;
    private Instant startedAt;
    private Instant completedAt;
    private String errorMessage;

    // getters and setters
}
```

### Восстановление из backup

#### Процесс восстановления
```bash
# 1. Остановка Cassandra
sudo systemctl stop cassandra

# 2. Очистка данных (если полное восстановление)
sudo rm -rf /var/lib/cassandra/data/*
sudo rm -rf /var/lib/cassandra/commitlog/*

# 3. Восстановление из backup
medusa restore --backup-name full_backup_20231201

# 4. Запуск Cassandra
sudo systemctl start cassandra

# 5. Проверка целостности данных
nodetool verify

# 6. Repair для восстановления консистентности
nodetool repair
```

#### Тестирование восстановления
```java
@Service
public class BackupRestoreTester {

    @Autowired
    private BackupService backupService;

    @Autowired
    private TestClusterManager testCluster;

    @Scheduled(cron = "0 4 * * 1") // Каждую неделю в 04:00
    public void testBackupRestore() {
        try {
            log.info("Starting backup restore test");

            // 1. Создание тестового кластера
            TestCluster testCluster = createTestCluster();

            // 2. Выбор случайного backup
            BackupInfo backup = selectRandomBackup();

            // 3. Восстановление в тестовый кластер
            restoreBackupToTestCluster(backup, testCluster);

            // 4. Валидация восстановленных данных
            validateRestoredData(testCluster);

            // 5. Тестирование функциональности
            runFunctionalTests(testCluster);

            // 6. Очистка тестового кластера
            cleanupTestCluster(testCluster);

            log.info("Backup restore test completed successfully");

        } catch (Exception e) {
            log.error("Backup restore test failed", e);
            alertService.sendCriticalAlert("Backup Restore Test Failed",
                                         "Weekly backup restore test failed: " + e.getMessage());
        }
    }

    private TestCluster createTestCluster() {
        // Создание изолированного тестового кластера
        return testClusterManager.createIsolatedCluster(3); // 3 узла
    }

    private BackupInfo selectRandomBackup() {
        List<BackupInfo> recentBackups = backupService.getBackupsLastWeek();
        return recentBackups.get(new Random().nextInt(recentBackups.size()));
    }

    private void restoreBackupToTestCluster(BackupInfo backup, TestCluster testCluster) {
        // Восстановление backup в тестовый кластер
        testCluster.restoreFromBackup(backup);
    }

    private void validateRestoredData(TestCluster testCluster) {
        // Проверка количества записей
        long originalCount = getOriginalDataCount();
        long restoredCount = testCluster.getDataCount();

        if (Math.abs(originalCount - restoredCount) > originalCount * 0.01) { // 1% tolerance
            throw new ValidationException("Data count mismatch: expected " +
                                        originalCount + ", got " + restoredCount);
        }

        // Проверка integrity данных
        List<String> corruptedKeys = testCluster.findCorruptedData();
        if (!corruptedKeys.isEmpty()) {
            throw new ValidationException("Found corrupted data: " + corruptedKeys);
        }
    }

    private void runFunctionalTests(TestCluster testCluster) {
        // Запуск функциональных тестов
        TestSuite testSuite = new TestSuite();
        testSuite.addTest(new ReadWriteTest());
        testSuite.addTest(new ConsistencyTest());
        testSuite.addTest(new PerformanceTest());

        TestResult result = testSuite.run(testCluster);

        if (!result.isSuccessful()) {
            throw new FunctionalTestException("Functional tests failed: " +
                                            result.getFailedTests());
        }
    }

    private void cleanupTestCluster(TestCluster testCluster) {
        testCluster.destroy();
    }

    private long getOriginalDataCount() {
        // Получение количества данных из production кластера
        return clusterManager.getTotalDataCount();
    }
}
```

## Восстановление после сбоев

### Node Failure Recovery

#### Автоматическое восстановление
```java
@Service
public class NodeFailureRecoveryService {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private AlertService alertService;

    @EventListener
    public void handleNodeFailure(NodeFailureEvent event) {
        Node failedNode = event.getFailedNode();

        try {
            log.info("Starting recovery for failed node: {}", failedNode.getAddress());

            // 1. Диагностика причины сбоя
            FailureCause cause = diagnoseFailure(failedNode);

            // 2. Выбор стратегии восстановления
            RecoveryStrategy strategy = selectRecoveryStrategy(cause);

            // 3. Выполнение восстановления
            executeRecovery(failedNode, strategy);

            // 4. Валидация восстановления
            validateRecovery(failedNode);

            log.info("Successfully recovered node: {}", failedNode.getAddress());

        } catch (Exception e) {
            log.error("Failed to recover node: {}", failedNode.getAddress(), e);

            // Escalation
            alertService.sendCriticalAlert(
                "Node Recovery Failed",
                "Failed to recover node " + failedNode.getAddress() + ": " + e.getMessage()
            );
        }
    }

    private FailureCause diagnoseFailure(Node node) {
        // Проверка логов
        List<String> recentLogs = getRecentLogs(node);

        // Анализ паттернов
        if (recentLogs.stream().anyMatch(log -> log.contains("OutOfMemoryError"))) {
            return FailureCause.OUT_OF_MEMORY;
        }

        if (recentLogs.stream().anyMatch(log -> log.contains("Disk full"))) {
            return FailureCause.DISK_FULL;
        }

        if (!isNodeReachable(node)) {
            return FailureCause.NETWORK_FAILURE;
        }

        return FailureCause.UNKNOWN;
    }

    private RecoveryStrategy selectRecoveryStrategy(FailureCause cause) {
        switch (cause) {
            case OUT_OF_MEMORY:
                return RecoveryStrategy.RESTART_WITH_INCREASED_HEAP;
            case DISK_FULL:
                return RecoveryStrategy.CLEANUP_AND_RESTART;
            case NETWORK_FAILURE:
                return RecoveryStrategy.WAIT_AND_CHECK;
            default:
                return RecoveryStrategy.FULL_RESTORE;
        }
    }

    private void executeRecovery(Node node, RecoveryStrategy strategy) {
        switch (strategy) {
            case RESTART_WITH_INCREASED_HEAP:
                increaseHeapSize(node);
                restartNode(node);
                break;

            case CLEANUP_AND_RESTART:
                cleanupDiskSpace(node);
                restartNode(node);
                break;

            case WAIT_AND_CHECK:
                waitForNetworkRecovery(node);
                break;

            case FULL_RESTORE:
                performFullNodeRestore(node);
                break;
        }
    }

    private void validateRecovery(Node node) {
        // Ожидание восстановления
        waitForNodeUp(node);

        // Проверка здоровья
        if (!isNodeHealthy(node)) {
            throw new RecoveryValidationException("Node is not healthy after recovery");
        }

        // Проверка данных
        runDataValidation(node);
    }

    private void increaseHeapSize(Node node) {
        // Увеличение heap size в конфигурации
        long currentHeap = node.getHeapSize();
        long newHeap = Math.min(currentHeap * 2, node.getMaxHeapSize());

        node.updateConfiguration("heap_size", newHeap + "G");
    }

    private void cleanupDiskSpace(Node node) {
        // Очистка временных файлов, логов, snapshots
        executeRemoteCommand(node, "find /var/lib/cassandra -name \"*.tmp\" -delete");
        executeRemoteCommand(node, "find /var/log/cassandra -name \"*.log\" -mtime +7 -delete");
        executeRemoteCommand(node, "nodetool clearsnapshot");
    }

    private void waitForNetworkRecovery(Node node) {
        long timeoutMs = 5 * 60 * 1000; // 5 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (isNodeReachable(node)) {
                return;
            }

            try {
                Thread.sleep(10000); // 10 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new NetworkRecoveryTimeoutException("Network recovery timeout");
    }

    private void performFullNodeRestore(Node node) {
        // Полное восстановление узла из backup
        backupService.restoreNodeFromBackup(node);
    }

    private void waitForNodeUp(Node node) {
        // Ожидание пока узел поднимется
        long timeoutMs = 10 * 60 * 1000; // 10 minutes
        // implementation...
    }

    private boolean isNodeHealthy(Node node) {
        // Проверка основных метрик
        return node.isUp() &&
               node.getCpuUsage() < 0.9 &&
               node.getMemoryUsage() < 0.9 &&
               node.getReadLatency() < 100;
    }

    private void runDataValidation(Node node) {
        // Проверка консистентности данных
        executeNodetoolCommand(node, "verify");
    }
}

enum FailureCause {
    OUT_OF_MEMORY, DISK_FULL, NETWORK_FAILURE, UNKNOWN
}

enum RecoveryStrategy {
    RESTART_WITH_INCREASED_HEAP, CLEANUP_AND_RESTART,
    WAIT_AND_CHECK, FULL_RESTORE
}
```

### Data Center Recovery

#### Восстановление датацентра
```java
@Service
public class DataCenterRecoveryService {

    @Autowired
    private ClusterTopology topology;

    @Autowired
    private BackupService backupService;

    public void recoverDataCenter(String dcName) {
        try {
            log.info("Starting datacenter recovery: {}", dcName);

            DataCenter dc = topology.getDataCenter(dcName);

            // 1. Оценка состояния DC
            DataCenterStatus status = assessDataCenterStatus(dc);

            // 2. Выбор стратегии восстановления
            RecoveryStrategy strategy = selectRecoveryStrategy(status);

            // 3. Планирование восстановления
            RecoveryPlan plan = createRecoveryPlan(dc, strategy);

            // 4. Выполнение восстановления
            executeRecoveryPlan(plan);

            // 5. Валидация
            validateDataCenterRecovery(dc);

            log.info("Successfully recovered datacenter: {}", dcName);

        } catch (Exception e) {
            log.error("Failed to recover datacenter: {}", dcName, e);
            alertService.sendCriticalAlert("DataCenter Recovery Failed",
                                         "Recovery of DC " + dcName + " failed: " + e.getMessage());
        }
    }

    private DataCenterStatus assessDataCenterStatus(DataCenter dc) {
        List<Node> nodes = dc.getNodes();
        int totalNodes = nodes.size();
        int upNodes = (int) nodes.stream().filter(Node::isUp).count();

        if (upNodes == 0) {
            return DataCenterStatus.COMPLETE_OUTAGE;
        } else if (upNodes < totalNodes / 2) {
            return DataCenterStatus.MAJOR_OUTAGE;
        } else if (upNodes < totalNodes) {
            return DataCenterStatus.PARTIAL_OUTAGE;
        } else {
            return DataCenterStatus.HEALTHY;
        }
    }

    private RecoveryStrategy selectRecoveryStrategy(DataCenterStatus status) {
        switch (status) {
            case COMPLETE_OUTAGE:
                return RecoveryStrategy.FULL_DC_RESTORE;
            case MAJOR_OUTAGE:
                return RecoveryStrategy.GRADUAL_NODE_RECOVERY;
            case PARTIAL_OUTAGE:
                return RecoveryStrategy.NODE_BY_NODE_RECOVERY;
            default:
                return RecoveryStrategy.NO_RECOVERY_NEEDED;
        }
    }

    private RecoveryPlan createRecoveryPlan(DataCenter dc, RecoveryStrategy strategy) {
        RecoveryPlan plan = new RecoveryPlan();
        plan.setDataCenter(dc);
        plan.setStrategy(strategy);

        switch (strategy) {
            case FULL_DC_RESTORE:
                plan.setSteps(createFullRestoreSteps(dc));
                break;
            case GRADUAL_NODE_RECOVERY:
                plan.setSteps(createGradualRecoverySteps(dc));
                break;
            case NODE_BY_NODE_RECOVERY:
                plan.setSteps(createNodeByNodeSteps(dc));
                break;
        }

        return plan;
    }

    private List<RecoveryStep> createFullRestoreSteps(DataCenter dc) {
        List<RecoveryStep> steps = new ArrayList<>();

        // 1. Provision infrastructure
        steps.add(new RecoveryStep("Provision DC infrastructure", () -> provisionDataCenter(dc)));

        // 2. Restore from backup
        steps.add(new RecoveryStep("Restore from latest backup", () -> restoreDataCenterBackup(dc)));

        // 3. Start nodes
        steps.add(new RecoveryStep("Start all nodes", () -> startAllNodes(dc)));

        // 4. Rebuild data
        steps.add(new RecoveryStep("Rebuild data from other DCs", () -> rebuildDataFromOtherDCs(dc)));

        return steps;
    }

    private void provisionDataCenter(DataCenter dc) {
        // Provision виртуальных машин, сетей, etc.
        infrastructureService.provisionDataCenter(dc);
    }

    private void restoreDataCenterBackup(DataCenter dc) {
        BackupInfo latestBackup = backupService.findLatestSuccessfulBackup();
        backupService.restoreDataCenter(latestBackup, dc);
    }

    private void startAllNodes(DataCenter dc) {
        for (Node node : dc.getNodes()) {
            nodeService.startNode(node);
        }
    }

    private void rebuildDataFromOtherDCs(DataCenter dc) {
        // nodetool rebuild --keyspace keyspace_name
        for (String keyspace : clusterManager.getKeyspaces()) {
            for (Node node : dc.getNodes()) {
                executeNodetoolCommand(node, "rebuild --keyspace " + keyspace);
            }
        }
    }

    private void executeRecoveryPlan(RecoveryPlan plan) {
        for (RecoveryStep step : plan.getSteps()) {
            try {
                log.info("Executing recovery step: {}", step.getDescription());
                step.execute();

                // Валидация после каждого шага
                validateRecoveryStep(plan.getDataCenter(), step);

            } catch (Exception e) {
                log.error("Recovery step failed: {}", step.getDescription(), e);
                handleRecoveryStepFailure(plan, step, e);
            }
        }
    }

    private void validateDataCenterRecovery(DataCenter dc) {
        // Проверка что все узлы работают
        boolean allNodesUp = dc.getNodes().stream().allMatch(Node::isUp);
        if (!allNodesUp) {
            throw new ValidationException("Not all nodes are up after DC recovery");
        }

        // Проверка репликации
        for (String keyspace : clusterManager.getKeyspaces()) {
            validateReplication(keyspace, dc);
        }

        // Проверка производительности
        validatePerformance(dc);
    }

    private void validateReplication(String keyspace, DataCenter dc) {
        // Проверка что данные реплицированы в DC
        int expectedReplicas = clusterManager.getReplicationFactor(keyspace, dc.getName());
        int actualReplicas = countActualReplicas(keyspace, dc);

        if (actualReplicas < expectedReplicas) {
            throw new ValidationException("Insufficient replicas for keyspace " + keyspace +
                                        ": expected " + expectedReplicas + ", got " + actualReplicas);
        }
    }

    private void validatePerformance(DataCenter dc) {
        // Проверка latency и throughput
        double avgLatency = dc.getNodes().stream()
            .mapToDouble(Node::getReadLatency)
            .average()
            .orElse(0);

        if (avgLatency > 100) { // 100ms
            log.warn("High average latency in recovered DC: {}ms", avgLatency);
        }
    }
}

enum DataCenterStatus {
    HEALTHY, PARTIAL_OUTAGE, MAJOR_OUTAGE, COMPLETE_OUTAGE
}

enum RecoveryStrategy {
    NO_RECOVERY_NEEDED, NODE_BY_NODE_RECOVERY, GRADUAL_NODE_RECOVERY, FULL_DC_RESTORE
}

class RecoveryPlan {
    private DataCenter dataCenter;
    private RecoveryStrategy strategy;
    private List<RecoveryStep> steps;

    // getters and setters
}

class RecoveryStep {
    private String description;
    private Runnable action;

    public RecoveryStep(String description, Runnable action) {
        this.description = description;
        this.action = action;
    }

    public void execute() {
        action.run();
    }

    // getter
}
```

## Управление пользователями и безопасностью

### Аутентификация и авторизация

#### Настройка пользователей
```cql
-- Создание роли администратора
CREATE ROLE admin WITH SUPERUSER = true AND LOGIN = true AND PASSWORD = 'admin_password';

-- Создание роли приложения
CREATE ROLE app_user WITH LOGIN = true AND PASSWORD = 'app_password';

-- Предоставление разрешений
GRANT SELECT, INSERT, UPDATE, DELETE ON KEYSPACE myapp TO app_user;
GRANT MODIFY ON KEYSPACE myapp TO app_user;

-- Создание ролей для чтения и записи
CREATE ROLE read_only;
CREATE ROLE read_write;

GRANT SELECT ON KEYSPACE myapp TO read_only;
GRANT SELECT, INSERT, UPDATE ON KEYSPACE myapp TO read_write;
```

#### Управление ролями
```java
@Service
public class RoleManagementService {

    @Autowired
    private CqlSession session;

    public void createApplicationRole(String appName, String password) {
        String roleName = appName + "_user";

        // Создание роли
        session.execute(String.format(
            "CREATE ROLE %s WITH LOGIN = true AND PASSWORD = ?", roleName),
            password
        );

        // Предоставление разрешений
        grantBasicPermissions(roleName, appName);
    }

    private void grantBasicPermissions(String roleName, String keyspace) {
        // Разрешения на таблицы
        session.execute(String.format(
            "GRANT SELECT, INSERT, UPDATE, DELETE ON KEYSPACE %s TO %s",
            keyspace, roleName
        ));

        // Разрешения на функции (если используются UDF)
        session.execute(String.format(
            "GRANT EXECUTE ON ALL FUNCTIONS IN KEYSPACE %s TO %s",
            keyspace, roleName
        ));
    }

    public void createReadOnlyRole(String keyspace) {
        String roleName = "readonly_" + keyspace;

        session.execute(String.format(
            "CREATE ROLE %s WITH LOGIN = false", roleName
        ));

        session.execute(String.format(
            "GRANT SELECT ON KEYSPACE %s TO %s", keyspace, roleName
        ));
    }

    public void revokeAllPermissions(String roleName) {
        // Получение всех разрешений роли
        ResultSet rs = session.execute(
            "SELECT * FROM system_auth.role_permissions WHERE role = ?",
            roleName
        );

        // Отзыв всех разрешений
        for (Row row : rs) {
            String resource = row.getString("resource");
            String permission = row.getString("permission");

            session.execute(String.format(
                "REVOKE %s ON %s FROM %s",
                permission, resource, roleName
            ));
        }
    }

    public void rotatePassword(String roleName, String newPassword) {
        // Изменение пароля
        session.execute(String.format(
            "ALTER ROLE %s WITH PASSWORD = ?", roleName
        ), newPassword);

        // Логирование изменения
        auditService.logPasswordChange(roleName);
    }

    public void listRolePermissions(String roleName) {
        ResultSet rs = session.execute(
            "SELECT resource, permission FROM system_auth.role_permissions WHERE role = ?",
            roleName
        );

        System.out.println("Permissions for role: " + roleName);
        for (Row row : rs) {
            System.out.println("- " + row.getString("permission") +
                             " on " + row.getString("resource"));
        }
    }
}
```

### Шифрование данных

#### Настройка SSL/TLS
```yaml
# cassandra.yaml - Client encryption
client_encryption_options:
    enabled: true
    optional: false
    keystore: /etc/cassandra/ssl/cassandra.keystore
    keystore_password: keystore_password
    truststore: /etc/cassandra/ssl/cassandra.truststore
    truststore_password: truststore_password
    protocol: TLS
    algorithm: SunX509
    store_type: JKS
    cipher_suites: [TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA]

# Server encryption
server_encryption_options:
    internode_encryption: all
    keystore: /etc/cassandra/ssl/cassandra.keystore
    keystore_password: keystore_password
    truststore: /etc/cassandra/ssl/cassandra.truststore
    truststore_password: truststore_password
    protocol: TLS
    algorithm: SunX509
    store_type: JKS
```

#### Управление сертификатами
```bash
# Создание keystore
keytool -genkeypair -keyalg RSA -alias cassandra -keystore cassandra.keystore \
  -storepass keystore_password -keypass key_password \
  -dname "CN=Cassandra, OU=Engineering, O=Company, L=City, ST=State, C=US"

# Создание truststore
keytool -export -alias cassandra -file cassandra.crt -keystore cassandra.keystore
keytool -import -alias cassandra -file cassandra.crt -keystore cassandra.truststore \
  -storepass truststore_password -noprompt

# Проверка сертификатов
keytool -list -keystore cassandra.keystore -storepass keystore_password
```

## Аудит и compliance

### Аудит доступа к данным

#### Настройка аудита
```yaml
# cassandra.yaml
audit_logging_options:
    enabled: true
    logger: BinAuditLogger
    audit_logs_dir: /var/log/cassandra/audit

# Для Cassandra 4.0+
role_management_options:
    mode: internal
```

#### Анализ audit логов
```java
@Service
public class AuditLogAnalyzer {

    @Autowired
    private AuditLogParser parser;

    @Autowired
    private ComplianceReporter reporter;

    @Scheduled(cron = "0 6 * * *") // Ежедневно в 06:00
    public void analyzeDailyAuditLogs() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // Парсинг логов
            List<AuditEvent> events = parser.parseAuditLogs(yesterday);

            // Анализ паттернов
            AuditAnalysis analysis = analyzeAuditEvents(events);

            // Генерация отчетов
            generateComplianceReports(analysis);

            // Проверка на подозрительную активность
            checkForSuspiciousActivity(events);

        } catch (Exception e) {
            log.error("Failed to analyze audit logs", e);
        }
    }

    private AuditAnalysis analyzeAuditEvents(List<AuditEvent> events) {
        AuditAnalysis analysis = new AuditAnalysis();

        // Статистика по типам операций
        Map<String, Long> operationStats = events.stream()
            .collect(Collectors.groupingBy(AuditEvent::getOperation, Collectors.counting()));

        analysis.setOperationStatistics(operationStats);

        // Статистика по пользователям
        Map<String, Long> userStats = events.stream()
            .collect(Collectors.groupingBy(AuditEvent::getUser, Collectors.counting()));

        analysis.setUserStatistics(userStats);

        // Статистика по таблицам
        Map<String, Long> tableStats = events.stream()
            .collect(Collectors.groupingBy(AuditEvent::getTable, Collectors.counting()));

        analysis.setTableStatistics(tableStats);

        // Поиск необычной активности
        List<Anomaly> anomalies = detectAnomalies(events);
        analysis.setAnomalies(anomalies);

        return analysis;
    }

    private List<Anomaly> detectAnomalies(List<AuditEvent> events) {
        List<Anomaly> anomalies = new ArrayList<>();

        // Аномалии по количеству запросов
        Map<String, Long> userActivity = events.stream()
            .collect(Collectors.groupingBy(AuditEvent::getUser, Collectors.counting()));

        long avgActivity = userActivity.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        for (Map.Entry<String, Long> entry : userActivity.entrySet()) {
            String user = entry.getKey();
            long activity = entry.getValue();

            if (activity > avgActivity * 3) { // 3x average
                anomalies.add(new Anomaly(
                    AnomalyType.UNUSUAL_ACTIVITY,
                    "User " + user + " has unusually high activity: " + activity + " operations",
                    user
                ));
            }
        }

        // Аномалии по времени доступа
        List<AuditEvent> afterHoursEvents = events.stream()
            .filter(this::isAfterHours)
            .collect(Collectors.toList());

        if (!afterHoursEvents.isEmpty()) {
            anomalies.add(new Anomaly(
                AnomalyType.AFTER_HOURS_ACCESS,
                afterHoursEvents.size() + " operations performed after hours",
                null
            ));
        }

        return anomalies;
    }

    private boolean isAfterHours(AuditEvent event) {
        LocalTime time = event.getTimestamp().toLocalTime();
        return time.isBefore(LocalTime.of(6, 0)) || time.isAfter(LocalTime.of(18, 0));
    }

    private void generateComplianceReports(AuditAnalysis analysis) {
        // Генерация отчетов для compliance
        reporter.generateDailyReport(analysis);
        reporter.generateUserActivityReport(analysis);
        reporter.generateSecurityIncidentReport(analysis.getAnomalies());
    }

    private void checkForSuspiciousActivity(List<AuditEvent> events) {
        List<Anomaly> criticalAnomalies = events.stream()
            .filter(this::isCriticalAnomaly)
            .map(event -> new Anomaly(AnomalyType.CRITICAL_SECURITY_EVENT,
                                    "Critical security event: " + event.getOperation(),
                                    event.getUser()))
            .collect(Collectors.toList());

        if (!criticalAnomalies.isEmpty()) {
            alertService.sendCriticalAlert("Security Anomalies Detected",
                                         criticalAnomalies.size() + " critical security events found");

            // Блокировка подозрительных пользователей
            for (Anomaly anomaly : criticalAnomalies) {
                if (anomaly.getUser() != null) {
                    securityService.lockUser(anomaly.getUser());
                }
            }
        }
    }

    private boolean isCriticalAnomaly(AuditEvent event) {
        // Критические операции для блокировки
        return "DROP KEYSPACE".equals(event.getOperation()) ||
               "DROP TABLE".equals(event.getOperation()) ||
               event.getOperation().contains("ADMIN");
    }
}

class AuditAnalysis {
    private Map<String, Long> operationStatistics;
    private Map<String, Long> userStatistics;
    private Map<String, Long> tableStatistics;
    private List<Anomaly> anomalies;

    // getters and setters
}

enum AnomalyType {
    UNUSUAL_ACTIVITY, AFTER_HOURS_ACCESS, CRITICAL_SECURITY_EVENT
}

class Anomaly {
    private AnomalyType type;
    private String description;
    private String user;

    // constructor, getters
}
```

## Управление версиями и обновлениями

### Планирование обновлений

#### Матрица совместимости
```java
public class VersionCompatibilityMatrix {

    private static final Map<String, Set<String>> COMPATIBILITY_MAP = Map.of(
        "3.11.x", Set.of("3.11.x", "4.0.x", "4.1.x"),
        "4.0.x", Set.of("4.0.x", "4.1.x"),
        "4.1.x", Set.of("4.1.x")
    );

    public boolean isCompatible(String currentVersion, String targetVersion) {
        String currentMajor = getMajorVersion(currentVersion);
        String targetMajor = getMajorVersion(targetVersion);

        Set<String> compatibleVersions = COMPATIBILITY_MAP.get(currentMajor);
        return compatibleVersions != null && compatibleVersions.contains(targetMajor);
    }

    public List<String> getUpgradePath(String currentVersion, String targetVersion) {
        List<String> path = new ArrayList<>();

        if (!isCompatible(currentVersion, targetVersion)) {
            throw new IncompatibleVersionException(
                "Direct upgrade from " + currentVersion + " to " + targetVersion + " not supported"
            );
        }

        // Для major version upgrades может потребоваться промежуточный шаг
        if (isMajorVersionUpgrade(currentVersion, targetVersion)) {
            path.add(getIntermediateVersion(currentVersion, targetVersion));
        }

        path.add(targetVersion);
        return path;
    }

    private String getMajorVersion(String version) {
        return version.split("\\.")[0] + "." + version.split("\\.")[1] + ".x";
    }

    private boolean isMajorVersionUpgrade(String current, String target) {
        int currentMajor = Integer.parseInt(current.split("\\.")[0]);
        int targetMajor = Integer.parseInt(target.split("\\.")[0]);
        return targetMajor > currentMajor;
    }

    private String getIntermediateVersion(String current, String target) {
        // Для перехода 3.11 -> 4.x требуется промежуточная версия
        if (current.startsWith("3.") && target.startsWith("4.")) {
            return "4.0.x";
        }
        return null;
    }
}
```

#### Процесс обновления
```java
@Service
public class ClusterUpgradeService {

    @Autowired
    private VersionCompatibilityMatrix compatibilityMatrix;

    @Autowired
    private ClusterManager clusterManager;

    public void upgradeCluster(String targetVersion) {
        try {
            log.info("Starting cluster upgrade to version: {}", targetVersion);

            // 1. Валидация возможности обновления
            validateUpgradePrerequisites(targetVersion);

            // 2. Создание плана обновления
            UpgradePlan plan = createUpgradePlan(targetVersion);

            // 3. Выполнение обновления
            executeUpgradePlan(plan);

            // 4. Валидация после обновления
            validateUpgradeSuccess(plan);

            log.info("Successfully upgraded cluster to version: {}", targetVersion);

        } catch (Exception e) {
            log.error("Cluster upgrade failed", e);
            handleUpgradeFailure(targetVersion, e);
        }
    }

    private void validateUpgradePrerequisites(String targetVersion) {
        // Проверка текущей версии
        String currentVersion = clusterManager.getCurrentVersion();
        if (!compatibilityMatrix.isCompatible(currentVersion, targetVersion)) {
            throw new UpgradeException("Incompatible version upgrade: " +
                                     currentVersion + " -> " + targetVersion);
        }

        // Проверка здоровья кластера
        if (!clusterManager.isClusterHealthy()) {
            throw new UpgradeException("Cluster is not healthy for upgrade");
        }

        // Проверка backup
        if (!backupService.hasRecentBackup()) {
            throw new UpgradeException("No recent backup found - create backup before upgrade");
        }
    }

    private UpgradePlan createUpgradePlan(String targetVersion) {
        UpgradePlan plan = new UpgradePlan();
        plan.setTargetVersion(targetVersion);

        List<String> upgradePath = compatibilityMatrix.getUpgradePath(
            clusterManager.getCurrentVersion(), targetVersion
        );

        plan.setUpgradePath(upgradePath);
        plan.setNodesToUpgrade(clusterManager.getAllNodes());
        plan.setEstimatedDowntime(calculateEstimatedDowntime(upgradePath));

        return plan;
    }

    private void executeUpgradePlan(UpgradePlan plan) {
        for (String version : plan.getUpgradePath()) {
            log.info("Upgrading to intermediate version: {}", version);

            // Обновление каждого узла
            for (Node node : plan.getNodesToUpgrade()) {
                upgradeNode(node, version);
            }

            // Валидация после каждого шага
            validateIntermediateUpgrade(version);
        }
    }

    private void upgradeNode(Node node, String version) {
        try {
            log.info("Upgrading node {} to version {}", node.getAddress(), version);

            // 1. Drain операций
            executeNodetoolCommand(node, "drain");

            // 2. Остановка узла
            nodeService.stopNode(node);

            // 3. Обновление пакетов
            packageManager.upgradeCassandra(node, version);

            // 4. Запуск узла
            nodeService.startNode(node);

            // 5. Ожидание готовности
            waitForNodeReady(node);

            log.info("Successfully upgraded node {} to version {}", node.getAddress(), version);

        } catch (Exception e) {
            log.error("Failed to upgrade node: {}", node.getAddress(), e);
            throw new NodeUpgradeException("Node upgrade failed: " + node.getAddress(), e);
        }
    }

    private void validateIntermediateUpgrade(String version) {
        // Ожидание пока все узлы обновятся
        waitForAllNodesUp();

        // Проверка совместимости
        runCompatibilityTests(version);

        // Проверка производительности
        runPerformanceTests();
    }

    private void validateUpgradeSuccess(UpgradePlan plan) {
        // Проверка что все узлы на целевой версии
        boolean allUpgraded = clusterManager.getAllNodes().stream()
            .allMatch(node -> version.equals(node.getVersion()));

        if (!allUpgraded) {
            throw new UpgradeValidationException("Not all nodes upgraded to target version");
        }

        // Запуск полных тестов
        runFullTestSuite();

        // Проверка метрик
        validatePerformanceMetrics();
    }

    private Duration calculateEstimatedDowntime(List<String> upgradePath) {
        // 10 минут на узел для каждого шага обновления
        int totalNodes = clusterManager.getAllNodes().size();
        long totalMinutes = upgradePath.size() * totalNodes * 10L;

        return Duration.ofMinutes(totalMinutes);
    }

    private void handleUpgradeFailure(String targetVersion, Exception e) {
        log.error("Upgrade to {} failed, initiating rollback", targetVersion, e);

        // Попытка rollback
        try {
            rollbackUpgrade(targetVersion);
        } catch (Exception rollbackException) {
            log.error("Rollback also failed", rollbackException);
            alertService.sendCriticalAlert("Upgrade and Rollback Failed",
                                         "Upgrade to " + targetVersion + " failed and rollback unsuccessful");
        }
    }

    private void rollbackUpgrade(String targetVersion) {
        // Rollback к предыдущей версии
        String previousVersion = clusterManager.getPreviousVersion();

        for (Node node : clusterManager.getAllNodes()) {
            if (targetVersion.equals(node.getVersion())) {
                downgradeNode(node, previousVersion);
            }
        }
    }

    private void downgradeNode(Node node, String version) {
        // Аналогично upgrade, но с предыдущей версией
        log.info("Rolling back node {} to version {}", node.getAddress(), version);
        upgradeNode(node, version);
    }
}

class UpgradePlan {
    private String targetVersion;
    private List<String> upgradePath;
    private List<Node> nodesToUpgrade;
    private Duration estimatedDowntime;

    // getters and setters
}
```

### Тестирование после обновления

#### Post-upgrade validation
```java
@Service
public class PostUpgradeValidator {

    @Autowired
    private TestSuite testSuite;

    @Autowired
    private PerformanceBenchmark benchmark;

    public UpgradeValidationResult validateUpgrade(String targetVersion) {
        UpgradeValidationResult result = new UpgradeValidationResult();

        try {
            // 1. Базовые тесты функциональности
            result.setFunctionalTests(runFunctionalTests());

            // 2. Тесты производительности
            result.setPerformanceTests(runPerformanceTests());

            // 3. Тесты совместимости
            result.setCompatibilityTests(runCompatibilityTests());

            // 4. Тесты нагрузки
            result.setLoadTests(runLoadTests());

            // 5. Общая оценка
            result.setOverallScore(calculateOverallScore(result));

            result.setValidationPassed(result.getOverallScore() >= 0.8); // 80% success rate

        } catch (Exception e) {
            result.setValidationPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    private TestResult runFunctionalTests() {
        // Тесты CRUD операций, запросов, etc.
        return testSuite.runFunctionalTests();
    }

    private PerformanceTestResult runPerformanceTests() {
        // Сравнение производительности с baseline
        PerformanceMetrics current = benchmark.runBenchmark();
        PerformanceMetrics baseline = benchmark.getBaselineMetrics();

        return PerformanceTestResult.builder()
            .currentMetrics(current)
            .baselineMetrics(baseline)
            .regressionDetected(detectRegression(current, baseline))
            .build();
    }

    private CompatibilityTestResult runCompatibilityTests() {
        // Тесты совместимости с приложениями
        return compatibilityTestSuite.runAllTests();
    }

    private LoadTestResult runLoadTests() {
        // Тесты под нагрузкой
        return loadTester.runLoadTest();
    }

    private double calculateOverallScore(UpgradeValidationResult result) {
        double functionalScore = result.getFunctionalTests().getSuccessRate();
        double performanceScore = calculatePerformanceScore(result.getPerformanceTests());
        double compatibilityScore = result.getCompatibilityTests().getSuccessRate();
        double loadScore = result.getLoadTests().getSuccessRate();

        // Взвешенная оценка
        return (functionalScore * 0.4) +
               (performanceScore * 0.3) +
               (compatibilityScore * 0.2) +
               (loadScore * 0.1);
    }

    private double calculatePerformanceScore(PerformanceTestResult performance) {
        if (performance.isRegressionDetected()) {
            // Регрессия снижает оценку
            double regressionSeverity = calculateRegressionSeverity(performance);
            return Math.max(0, 1.0 - regressionSeverity);
        }

        // Улучшение производительности дает бонус
        double improvement = calculateImprovement(performance);
        return Math.min(1.2, 1.0 + improvement); // Max 120%
    }

    private boolean detectRegression(PerformanceMetrics current, PerformanceMetrics baseline) {
        // Проверка значительной деградации
        double latencyThreshold = 1.5; // 50% increase
        double throughputThreshold = 0.8; // 20% decrease

        return current.getAverageLatency() > baseline.getAverageLatency() * latencyThreshold ||
               current.getThroughput() < baseline.getThroughput() * throughputThreshold;
    }

    private double calculateRegressionSeverity(PerformanceTestResult performance) {
        PerformanceMetrics current = performance.getCurrentMetrics();
        PerformanceMetrics baseline = performance.getBaselineMetrics();

        double latencyRatio = current.getAverageLatency() / baseline.getAverageLatency();
        double throughputRatio = current.getThroughput() / baseline.getThroughput();

        // Серьезность регрессии
        return Math.max(0, (latencyRatio - 1) * 0.6 + (1 - throughputRatio) * 0.4);
    }

    private double calculateImprovement(PerformanceTestResult performance) {
        PerformanceMetrics current = performance.getCurrentMetrics();
        PerformanceMetrics baseline = performance.getBaselineMetrics();

        double latencyImprovement = Math.max(0, (baseline.getAverageLatency() - current.getAverageLatency()) /
                                              baseline.getAverageLatency());
        double throughputImprovement = Math.max(0, (current.getThroughput() - baseline.getThroughput()) /
                                                 baseline.getThroughput());

        return (latencyImprovement + throughputImprovement) / 2;
    }
}

class UpgradeValidationResult {
    private TestResult functionalTests;
    private PerformanceTestResult performanceTests;
    private CompatibilityTestResult compatibilityTests;
    private LoadTestResult loadTests;
    private double overallScore;
    private boolean validationPassed;
    private String errorMessage;

    // getters and setters
}
```

## Лучшие практики

### Организационные практики

#### 1. Роли и обязанности
- **Database `Administrator` (DBA)**: Управление кластером, оптимизация, **troubleshooting**
- **DevOps Engineer**: Автоматизация, CI/CD, инфраструктура
- **Application Developer**: Оптимизация запросов, моделирование данных
- **Security Officer**: Безопасность, **compliance**, аудит

#### 2. Процессы и процедуры
- **Change Management**: Все изменения через **approval process**
- **Incident Response**: План действий при сбоях
- **Backup/Restore**: Регулярное тестирование процедур
- **Performance Monitoring**: **SLA** для **latency** и **availability**

#### 3. Документация
- **Runbooks**: Пошаговые инструкции для **routine** операций
- **Architecture Diagrams**: Текущая и целевая архитектура
- **Configuration Inventory**: Все настройки с обоснованиями
- **Incident History**: Уроки из прошлых инцидентов

### Технические best practices

#### 1. Capacity Planning
- **Monitor Growth**: Запросы, данные, пользователи
- **Plan Ahead**: 6-12 месяцев для **major** изменений
- **Right-size Resources**: Не **over-provision**, но и не **under-provision**
- **Cost Optimization**: **Balance** между производительностью и стоимостью

#### 2. High Availability
- **Multiple `Data` Centers**: Для **disaster recovery**
- **Replication Factor**: Минимум 3 для **production**
- **Network Redundancy**: **Multiple network paths**
- **Automated Failover**: Быстрое восстановление после сбоев

#### 3. Security First
- **Defense in Depth**: Многоуровневая защита
- **Principle of `Least` Privilege**: Минимальные разрешения
- **Encryption Everywhere**: **Data** at **rest**, in **transit**, in **use**
- **Regular Audits**: Проверка **compliance** и **security**

#### 4. Performance Culture
- **Set Benchmarks**: Измеряй до и после изменений
- **Monitor Trends**: Не только текущие метрики
- **Proactive Optimization**: Не жди проблем
- **Share Knowledge**: Документируй и обучай команду

### Мониторинг и alerting

#### 1. Key Metrics Dashboard
Пример Cassandra Dashboard — набор панелей для мониторинга:

- **Status**: UP, 9/9 nodes, 2.1TB data, 99.9% uptime
- **Performance**: Latency 12.3ms | Throughput 45.2k/sec | Errors 0.01% | Queue 12 ops | Disk I/O 234 MB/s
- **Resources**: CPU 67% | Memory 78% | Disk 45% | Network 34%
- **Top Slow Queries**:
  1. `SELECT * FROM large_table LIMIT 1000` — 2.3s
  2. `SELECT COUNT(*) FROM events WHERE date = ?` — 890ms
  3. Complex aggregation query — 567ms

#### 2. Alert Hierarchy
- **Critical**: **Cluster down**, **data loss**, **security breach**
- **High**: **Node failure**, **high latency**, **disk full**
- **Medium**: **Performance degradation**, **configuration drift**
- **Low**: **Warning conditions**, **maintenance reminders**

#### 3. Automated Response
- **Self-healing**: Автоматическое восстановление **minor issues**
- **Escalation**: Уведомление нужных людей в зависимости от **severity**
- **Runbooks**: Автоматизированные процедуры для **common issues**
- **Feedback Loop**: Обучение от инцидентов

**Администрирование **Apache Cassandra** — это комплексная дисциплина, требующая глубокого понимания распределенных систем, баз данных и **DevOps** практик. Ключевые аспекты успешного администрирования включают:**

### Операционные практики:

1. **Мониторинг и alerting** — постоянный контроль здоровья кластера
2. **Резервное копирование** — надежные стратегии **backup** и **restore**
3. **Обслуживание** — регулярные **repair**, **compaction**, **cleanup** операции
4. **Безопасность** — многоуровневая защита данных и систем

### Управление изменениями:

1. **Configuration management** — версионирование и валидация настроек
2. **Upgrade planning** — безопасные процедуры обновления версий
3. **Capacity planning** — прогнозирование и планирование роста
4. **Change control** — процессы **approval** для изменений

### Incident management:

1. **Failure detection** — автоматическое обнаружение проблем
2. **Recovery procedures** — документированные планы восстановления
3. **Root cause analysis** — анализ причин инцидентов
4. **Prevention** — меры по предотвращению повторения проблем

### Best practices:

1. **Automation** — скрипты и инструменты для **routine** задач
2. **Documentation** — подробная документация всех процедур
3. **Testing** — регулярное тестирование **backup**, **failover**, **upgrades**
4. **Team collaboration** — четкое разделение ролей и ответственности

### Вызовы и решения:

1. **Сложность управления** — использование инструментов для упрощения
2. **Масштабируемость** — планирование роста с самого начала
3. **Производительность** — постоянный мониторинг и оптимизация
4. **Надежность** — **redundant** архитектуры и **disaster recovery**

### Инструменты и технологии:

1. **OpsCenter** — визуальное управление кластером
2. **Reaper** — автоматизация **repair** операций
3. **Medusa** — **enterprise backup** решения
4. **Prometheus/Grafana** — **monitoring** и **alerting**

### Культура и процессы:

1. **DevOps culture** — **collaboration** между **development** и **operations**
2. **Site `Reliability` Engineering** — **focus** на **reliability** и **automation**
3. **Continuous improvement** — регулярный анализ и оптимизация
4. **Knowledge sharing** — документация и обучение команды

Администрирование **Cassandra** кластера — это непрерывный процесс совершенствования. Успех зависит от правильной архитектуры, надежных процессов, качественных инструментов и компетентной команды. Регулярное обслуживание, мониторинг и оптимизация обеспечивают высокую производительность и надежность системы на протяжении всего срока эксплуатации.

Финальный результат: комплексная система администрирования **production Cassandra** кластера готова к развертыванию!

**Все файлы `Cassandra` завершены:**
- **cassandra-basics.md**
- **cassandra-`data-modeling`.md**
- **cassandra-queries.md**
- **cassandra-clustering.md**
- **cassandra-performance.md**
- **cassandra-admin.md**

## Решение проблем

**Узлы не видят друг друга (Gossip):** проверьте сеть, `listen_address`/`broadcast_address`, файрвол и seed-узлы. Убедитесь, что все узлы в одном кластере (одинаковый `cluster_name`). Просмотр статуса: `nodetool status`, `nodetool describecluster`.

**Высокий объём pending compactions:** увеличьте число compaction-потоков, проверьте размер партиций и объём данных. Временное снятие нагрузки или добавление узлов. Не отключайте компакцию надолго — это ведёт к росту чтений с диска.

**Read/Write таймауты:** увеличьте `read_request_timeout_in_ms`/`write_request_timeout_in_ms` при необходимости, но в первую очередь оптимизируйте запросы и модель данных (избегайте больших партиций, слишком широких строк). Проверьте нагрузку и консистентность (LOCAL_QUORUM и т.п.).

**Диск заполнен:** очистите снимки (`nodetool clearsnapshot`), проверьте логи и данные. Увеличьте место или добавьте узел и перебалансируйте. Настройте retention для commitlog и снимков.

**Дальнейшее развитие раздела:**
1. Актуализировать команды и параметры под целевую версию `Cassandra`.
2. Поддерживать согласованность кросс-ссылок между `cassandra-*.md`.
3. Дополнять практические runbook-сценарии по итогам эксплуатации.


