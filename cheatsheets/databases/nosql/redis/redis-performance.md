---
title: "Redis: Производительность"
description: "Полное руководство по оптимизации производительности Redis: бенчмаркинг, профилирование, мониторинг, тюнинг, best practices"
tags:
  - redis
  - performance
  - optimization
  - benchmarking
  - profiling
  - monitoring
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-data-structures.md"]
next: ["databases/redis-security.md", "databases/redis-monitoring.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-clustering.md"]
---

# **Redis**: Производительность

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Optimization](https://redis.io/docs/management/optimization/) — оптимизация

### См. также
- [redis-basics.md](redis-basics.md) — основы Redis
- [redis-monitoring.md](redis-monitoring.md) — мониторинг

## Содержание

- [Введение в оптимизацию производительности **Redis**](#введение-в-оптимизацию-производительности-redis)
  - [Факторы производительности](#факторы-производительности)
- [Бенчмаркинг](#бенчмаркинг)
  - [**Redis Benchmark Tool**](#redis-benchmark-tool)
  - [Специфичные тесты](#специфичные-тесты)
  - [Кастомные бенчмарки](#кастомные-бенчмарки)
- [Профилирование](#профилирование)
  - [**Slow Log**](#slow-log)
  - [Мониторинг команд](#мониторинг-команд)
  - [**Memory Profiling**](#memory-profiling)
- [Оптимизация памяти](#оптимизация-памяти)
  - [Настройки памяти](#настройки-памяти)
  - [Оптимизация структур данных](#оптимизация-структур-данных)
  - [**Lazy Free**](#lazy-free)
- [Оптимизация сети](#оптимизация-сети)
  - [**TCP** настройки](#tcp-настройки)
  - [**Client Output Buffers**](#client-output-buffers)
  - [**Pipeline**](#pipeline)
- [Оптимизация персистентности](#оптимизация-персистентности)
  - [**RDB** оптимизация](#rdb-оптимизация)
  - [**AOF** оптимизация](#aof-оптимизация)
- [Мониторинг производительности](#мониторинг-производительности)
  - [Метрики для мониторинга](#метрики-для-мониторинга)
  - [**Prometheus Metrics**](#prometheus-metrics)
- [Лучшие практики](#лучшие-практики)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Оптимизация памяти](#оптимизация-памяти-1)
  - [Оптимизация сети](#оптимизация-сети-1)
- [**Advanced Performance Tuning**](#advanced-performance-tuning)
  - [**Memory Optimization Strategies**](#memory-optimization-strategies)
  - [**CPU Optimization**](#cpu-optimization)
  - [**Network Optimization**](#network-optimization)
- [**Performance Monitoring Tools**](#performance-monitoring-tools)
  - [**Redis Insight**](#redis-insight)
  - [**Custom Monitoring Script**](#custom-monitoring-script)
- [**Performance Testing Scenarios**](#performance-testing-scenarios)
  - [**Load Testing**](#load-testing)
- [**Optimization Patterns**](#optimization-patterns)
  - [**Connection Pooling**](#connection-pooling)
  - [**Batch Operations**](#batch-operations)
- [Решение проблем производительности](#решение-проблем-производительности)
  - [**High Memory Usage**](#high-memory-usage)
  - [**Slow Operations**](#slow-operations)
  - [**High CPU Usage**](#high-cpu-usage)
- [**Performance Tuning Guide**](#performance-tuning-guide)
  - [**Memory Optimization**](#memory-optimization)
  - [**CPU Optimization**](#cpu-optimization-1)
  - [**Network Optimization**](#network-optimization-1)
- [**Performance Testing**](#performance-testing)
  - [**Load Testing Script**](#load-testing-script)
  - [**Stress Testing**](#stress-testing)

## Введение в оптимизацию производительности **Redis**

Производительность **Redis** зависит от множества факторов: конфигурации, использования памяти, сетевых настроек, структуры данных и паттернов доступа. Понимание этих факторов критически важно для достижения максимальной производительности.

### Факторы производительности

1. **Память**: Использование памяти и политики **eviction**
2. **Сеть**: Пропускная способность и задержка
3. **CPU**: Использование процессора
4. **Диск**: I/O операции для персистентности
5. **Конфигурация**: Настройки сервера и клиентов

---

## Бенчмаркинг

### **Redis Benchmark Tool**

```bash
# Базовый бенчмарк
redis-benchmark -h localhost -p 6379 -c 50 -n 10000

# Параметры:
# -h: хост
# -p: порт
# -c: количество параллельных клиентов
# -n: количество запросов
# -d: размер данных (в байтах)
# -t: тестируемые команды
# -P: pipeline (количество команд в pipeline)
# -k: keepalive (1=да, 0=нет)
```

### Специфичные тесты

```bash
# Тест SET/GET операций
redis-benchmark -t set,get -c 10 -n 1000

# Тест с аутентификацией
redis-benchmark -a mypassword -c 100 -n 10000 -d 256

# Тест Pipeline
redis-benchmark -P 16 -c 100 -n 10000

# Тест разных типов данных
redis-benchmark -t hset,hget -c 50 -n 5000
redis-benchmark -t lpush,lpop -c 50 -n 5000
redis-benchmark -t sadd,spop -c 50 -n 5000
redis-benchmark -t zadd,zrange -c 50 -n 5000

# Тест с большими данными
redis-benchmark -d 1024 -c 10 -n 1000

# Тест с разными размерами ключей
for size in 64 256 1024 4096; do
    redis-benchmark -d $size -c 10 -n 1000 -t set,get
done
```

### Кастомные бенчмарки

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedisBenchmark {
    private JedisPool jedisPool;
    
    public RedisBenchmark(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }
    
    public void benchmarkOperations(int operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Тест SET
            List<Long> setTimes = new ArrayList<>();
            for (int i = 0; i < operations; i++) {
                long start = System.nanoTime();
                jedis.set("key" + i, "value" + i);
                long duration = (System.nanoTime() - start) / 1_000_000; // в миллисекундах
                setTimes.add(duration);
            }
            
            // Тест GET
            List<Long> getTimes = new ArrayList<>();
            for (int i = 0; i < operations; i++) {
                long start = System.nanoTime();
                jedis.get("key" + i);
                long duration = (System.nanoTime() - start) / 1_000_000;
                getTimes.add(duration);
            }
            
            printStatistics("SET operations", setTimes);
            printStatistics("GET operations", getTimes);
        }
    }
    
    private void printStatistics(String operation, List<Long> times) {
        Collections.sort(times);
        double mean = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double median = times.get(times.size() / 2);
        double p95 = times.get((int)(times.size() * 0.95));
        
        System.out.println(operation + ":");
        System.out.println(String.format("  Mean: %.2f ms", mean));
        System.out.println(String.format("  Median: %.2f ms", median));
        System.out.println(String.format("  P95: %.2f ms", p95));
    }
    
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    
    public static void main(String[] args) {
        RedisBenchmark benchmark = new RedisBenchmark("localhost", 6379);
        benchmark.benchmarkOperations(10000);
        benchmark.close();
    }
}
```

---

## Профилирование

### **Slow Log**

```redis
# Настройка slow log
CONFIG SET slowlog-log-slower-than 10000  # микросекунды
CONFIG SET slowlog-max-len 128

# Просмотр slow log
SLOWLOG GET 10
SLOWLOG LEN
SLOWLOG RESET

# Формат записи:
# 1) Уникальный ID
# 2) Unix timestamp
# 3) Время выполнения (микросекунды)
# 4) Массив аргументов команды
```

### Мониторинг команд

```redis
# Статистика команд
INFO commandstats

# Формат:
# cmdstat_set:calls=1000,usec=5000,usec_per_call=5.00
# cmdstat_get:calls=2000,usec=2000,usec_per_call=1.00

# Сброс статистики
CONFIG RESETSTAT
```

### **Memory Profiling**

```redis
# Анализ использования памяти
MEMORY USAGE key
MEMORY STATS
MEMORY DOCTOR
MEMORY MALLOC-STATS

# Получить образец ключей для анализа
MEMORY SAMPLES 5
```

---

## Оптимизация памяти

### Настройки памяти

```conf
# Ограничение памяти
maxmemory 2gb

# Политики eviction
maxmemory-policy allkeys-lru
# noeviction - не удалять ключи
# allkeys-lru - удалять наименее используемые
# volatile-lru - удалять наименее используемые с TTL
# allkeys-lfu - удалять наименее часто используемые
# volatile-lfu - удалять наименее часто используемые с TTL
# allkeys-random - удалять случайные
# volatile-random - удалять случайные с TTL
# volatile-ttl - удалять с наименьшим TTL

# Количество образцов для eviction
maxmemory-samples 5
```

### Оптимизация структур данных

```conf
# Оптимизация хэшей
hash-max-ziplist-entries 512
hash-max-ziplist-value 64

# Оптимизация списков
list-max-ziplist-size -2
list-compress-depth 0

# Оптимизация множеств
set-max-intset-entries 512

# Оптимизация sorted sets
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
```

### **Lazy Free**

```conf
# Ленивое освобождение памяти
lazyfree-lazy-eviction yes
lazyfree-lazy-expire yes
lazyfree-lazy-server-del yes
replica-lazy-flush yes
```

---

## Оптимизация сети

### **TCP** настройки

```conf
# TCP backlog
tcp-backlog 511

# TCP keepalive
tcp-keepalive 300

# Таймауты
timeout 0
```

### **Client Output Buffers**

```conf
# Ограничения буферов для клиентов
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
```

### **Pipeline**

```java
// Использование Pipeline для множественных операций
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class PipelineExample {
    private JedisPool jedisPool;
    
    public PipelineExample(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void withoutPipeline() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Без Pipeline (медленно)
            for (int i = 0; i < 1000; i++) {
                jedis.set("key" + i, "value" + i);
            }
        }
    }
    
    public void withPipeline() {
        try (Jedis jedis = jedisPool.getResource()) {
            // С Pipeline (быстро)
            Pipeline pipe = jedis.pipelined();
            for (int i = 0; i < 1000; i++) {
                pipe.set("key" + i, "value" + i);
            }
            pipe.sync();
        }
    }
}
```

---

## Оптимизация персистентности

### **RDB** оптимизация

```conf
# Отключить RDB если используется только AOF
save ""

# Оптимизация процесса сохранения
stop-writes-on-bgsave-error no
rdbcompression yes
rdbchecksum yes
rdb-save-incremental-fsync yes
```

### **AOF** оптимизация

```conf
# Оптимизация синхронизации
appendfsync everysec
no-appendfsync-on-rewrite yes
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-rewrite-incremental-fsync yes
aof-use-rdb-preamble yes
```

---

## Мониторинг производительности

### Метрики для мониторинга

```redis
# CPU использование
INFO cpu

# Память
INFO memory

# Статистика команд
INFO commandstats

# Статистика клиентов
INFO clients

# Статистика ключей
INFO keyspace

# Статистика репликации
INFO replication
```

### **Prometheus Metrics**

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'redis'
    static_configs:
      - targets: ['localhost:9121']
```

```bash
# Redis Exporter
docker run -d \
  --name redis-exporter \
  -p 9121:9121 \
  -e REDIS_ADDR=redis://localhost:6379 \
  oliver006/redis_exporter
```

---

## Лучшие практики

### Оптимизация запросов

1. **Используйте Pipeline** для множественных операций
2. **Используйте SCAN** вместо **KEYS**
3. **Используйте хэши** вместо множества ключей
4. **Используйте правильные структуры данных**
5. **Избегайте блокирующих операций**

### Оптимизация памяти

1. **Настройте maxmemory** правильно
2. **Выберите правильную политику eviction**
3. **Используйте TTL** для временных данных
4. **Оптимизируйте структуры данных**
5. **Мониторьте использование памяти**

### Оптимизация сети

1. **Используйте Pipeline** для снижения **round-trips**
2. **Настройте правильные буферы**
3. **Используйте connection pooling**
4. **Оптимизируйте размер данных**
5. **Мониторьте сетевую задержку**

## **Advanced Performance Tuning**

### **Memory Optimization Strategies**

```conf
# Оптимизация использования памяти
# Использовать более эффективные структуры данных
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64

# Lazy free для больших удалений
lazyfree-lazy-eviction yes
lazyfree-lazy-expire yes
lazyfree-lazy-server-del yes
replica-lazy-flush yes
```

### **CPU Optimization**

```conf
# Оптимизация использования CPU
# Использовать правильное количество потоков для I/O
io-threads 4
io-threads-do-reads yes

# Оптимизация для многопроцессорных систем
taskset -c 0,1,2,3 redis-server
```

### **Network Optimization**

```conf
# Оптимизация сети
tcp-backlog 511
tcp-keepalive 300
timeout 0

# Увеличение лимитов соединений
maxclients 10000

# Оптимизация буферов
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
```

## **Performance Monitoring Tools**

### **Redis Insight**

```bash
# Redis Insight - GUI инструмент для мониторинга
docker run -d \
  --name redis-insight \
  -p 8001:8001 \
  redislabs/redisinsight:latest
```

### **Custom Monitoring Script**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RedisPerformanceMonitor {
    private JedisPool jedisPool;
    private Gson gson;
    
    public RedisPerformanceMonitor(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        this.gson = new Gson();
    }
    
    public Map<String, Object> collectMetrics() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            // Общая информация
            Map<String, String> info = jedis.info();
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("redis_version", info.get("redis_version"));
            infoMap.put("uptime_in_seconds", info.get("uptime_in_seconds"));
            infoMap.put("connected_clients", info.get("connected_clients"));
            infoMap.put("used_memory_human", info.get("used_memory_human"));
            infoMap.put("used_memory_peak_human", info.get("used_memory_peak_human"));
            metrics.put("info", infoMap);
            
            // Статистика команд
            Map<String, String> commandstats = jedis.info("commandstats");
            Map<String, Map<String, Object>> cmdStatsMap = new HashMap<>();
            for (Map.Entry<String, String> entry : commandstats.entrySet()) {
                Map<String, Object> cmdStats = new HashMap<>();
                // Парсинг статистики команд
                cmdStatsMap.put(entry.getKey(), cmdStats);
            }
            metrics.put("commandstats", cmdStatsMap);
            
            // Память
            Map<String, String> memory = jedis.info("memory");
            Map<String, Object> memoryMap = new HashMap<>();
            memoryMap.put("used_memory", memory.get("used_memory"));
            memoryMap.put("used_memory_human", memory.get("used_memory_human"));
            memoryMap.put("used_memory_peak", memory.get("used_memory_peak"));
            memoryMap.put("mem_fragmentation_ratio", memory.get("mem_fragmentation_ratio"));
            metrics.put("memory", memoryMap);
            
            // Клиенты
            Map<String, String> clients = jedis.info("clients");
            Map<String, Object> clientsMap = new HashMap<>();
            clientsMap.put("connected_clients", clients.get("connected_clients"));
            clientsMap.put("blocked_clients", clients.get("blocked_clients"));
            clientsMap.put("client_recent_max_input_buffer", clients.get("client_recent_max_input_buffer"));
            clientsMap.put("client_recent_max_output_buffer", clients.get("client_recent_max_output_buffer"));
            metrics.put("clients", clientsMap);
            
            // Статистика
            Map<String, String> stats = jedis.info("stats");
            Map<String, Object> statsMap = new HashMap<>();
            statsMap.put("total_connections_received", stats.get("total_connections_received"));
            statsMap.put("total_commands_processed", stats.get("total_commands_processed"));
            statsMap.put("instantaneous_ops_per_sec", stats.get("instantaneous_ops_per_sec"));
            statsMap.put("keyspace_hits", stats.get("keyspace_hits"));
            statsMap.put("keyspace_misses", stats.get("keyspace_misses"));
            metrics.put("stats", statsMap);
            
            return metrics;
        }
    }
    
    public void monitorContinuously(long intervalMs) throws InterruptedException {
        while (true) {
            Map<String, Object> metrics = collectMetrics();
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
        RedisPerformanceMonitor monitor = new RedisPerformanceMonitor("localhost", 6379, null);
        monitor.monitorContinuously(60000);
    }
}
```

## **Performance Testing Scenarios**

### **Load Testing**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class LoadTester {
    private JedisPool jedisPool;
    private List<Long> results;
    
    public LoadTester(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        this.results = Collections.synchronizedList(new ArrayList<>());
    }
    
    private void worker(int workerId, int operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Long> times = new ArrayList<>();
            
            for (int i = 0; i < operations; i++) {
                long start = System.nanoTime();
                jedis.set("key_" + workerId + "_" + i, "value_" + i);
                jedis.get("key_" + workerId + "_" + i);
                long duration = (System.nanoTime() - start) / 1_000_000; // в миллисекундах
                times.add(duration);
            }
            
            results.addAll(times);
        }
    }
    
    public void runTest(int threads, int operationsPerThread) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threads; i++) {
            final int workerId = i;
            executor.submit(() -> worker(workerId, operationsPerThread));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        int totalOperations = threads * operationsPerThread * 2;  // SET + GET
        
        Collections.sort(results);
        double mean = results.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double median = results.get(results.size() / 2);
        double p95 = results.get((int)(results.size() * 0.95));
        double p99 = results.get((int)(results.size() * 0.99));
        
        System.out.println(String.format("Total time: %.2f seconds", totalTime / 1000.0));
        System.out.println("Total operations: " + totalOperations);
        System.out.println(String.format("Operations per second: %.2f", totalOperations / (totalTime / 1000.0)));
        System.out.println(String.format("Mean latency: %.2f ms", mean));
        System.out.println(String.format("Median latency: %.2f ms", median));
        System.out.println(String.format("P95 latency: %.2f ms", p95));
        System.out.println(String.format("P99 latency: %.2f ms", p99));
    }
    
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        LoadTester tester = new LoadTester("localhost", 6379, null);
        tester.runTest(50, 1000);
        tester.close();
    }
}
```

## **Optimization Patterns**

### **Connection Pooling**

```java
// Java пример connection pooling
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class OptimizedRedisPool {
    private JedisPool jedisPool;
    
    public OptimizedRedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        
        // Оптимизация пула
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(60000);
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setNumTestsPerEvictionRun(3);
        config.setMaxWaitMillis(5000);
        
        this.jedisPool = new JedisPool(config, "localhost", 6379);
    }
}
```

### **Batch Operations**

```java
// Java пример batch операций
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchOperations {
    private JedisPool jedisPool;
    
    public BatchOperations(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void batchWithPipeline() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Использование Pipeline
            Pipeline pipe = jedis.pipelined();
            for (int i = 0; i < 1000; i++) {
                pipe.set("key" + i, "value" + i);
            }
            pipe.sync();
        }
    }
    
    public void batchWithMSET() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Использование MSET/MGET
            Map<String, String> keyValues = new HashMap<>();
            for (int i = 0; i < 1000; i++) {
                keyValues.put("key" + i, "value" + i);
            }
            jedis.mset(keyValues);
            
            String[] keys = new String[1000];
            for (int i = 0; i < 1000; i++) {
                keys[i] = "key" + i;
            }
            List<String> values = jedis.mget(keys);
        }
    }
}
```

## Решение проблем производительности

### **High Memory Usage**

```redis
# Проверить использование памяти
INFO memory

# Найти большие ключи
MEMORY USAGE key
MEMORY STATS

# Анализ памяти
MEMORY DOCTOR
```

### **Slow Operations**

```redis
# Проверить slow log
SLOWLOG GET 10

# Настроить порог
CONFIG SET slowlog-log-slower-than 10000
CONFIG SET slowlog-max-len 128
```

### **High CPU Usage**

```redis
# Проверить использование CPU
INFO cpu

# Проверить статистику команд
INFO commandstats

# Найти медленные команды
SLOWLOG GET 10
```

## **Performance Tuning Guide**

### **Memory Optimization**

```conf
# Оптимизация использования памяти
# Использовать более эффективные структуры данных
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64

# Lazy free для больших удалений
lazyfree-lazy-eviction yes
lazyfree-lazy-expire yes
lazyfree-lazy-server-del yes
replica-lazy-flush yes
```

### **CPU Optimization**

```conf
# Оптимизация использования CPU
# Использовать правильное количество потоков для I/O
io-threads 4
io-threads-do-reads yes

# Оптимизация для многопроцессорных систем
taskset -c 0,1,2,3 redis-server
```

### **Network Optimization**

```conf
# Оптимизация сети
tcp-backlog 511
tcp-keepalive 300
timeout 0

# Увеличение лимитов соединений
maxclients 10000

# Оптимизация буферов
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
```

## **Performance Testing**

### **Load Testing Script**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class PerformanceTester {
    private JedisPool jedisPool;
    private List<Long> results;
    
    public PerformanceTester(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        this.results = Collections.synchronizedList(new ArrayList<>());
    }
    
    private void worker(int workerId, int operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Long> times = new ArrayList<>();
            
            for (int i = 0; i < operations; i++) {
                long start = System.nanoTime();
                jedis.set("key_" + workerId + "_" + i, "value_" + i);
                jedis.get("key_" + workerId + "_" + i);
                long duration = (System.nanoTime() - start) / 1_000_000;
                times.add(duration);
            }
            
            results.addAll(times);
        }
    }
    
    public void runTest(int threads, int operationsPerThread) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threads; i++) {
            final int workerId = i;
            executor.submit(() -> worker(workerId, operationsPerThread));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        int totalOperations = threads * operationsPerThread * 2;
        
        Collections.sort(results);
        double mean = results.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double median = results.get(results.size() / 2);
        double p95 = results.get((int)(results.size() * 0.95));
        double p99 = results.get((int)(results.size() * 0.99));
        
        System.out.println(String.format("Total time: %.2f seconds", totalTime / 1000.0));
        System.out.println("Total operations: " + totalOperations);
        System.out.println(String.format("Operations per second: %.2f", totalOperations / (totalTime / 1000.0)));
        System.out.println(String.format("Mean latency: %.2f ms", mean));
        System.out.println(String.format("Median latency: %.2f ms", median));
        System.out.println(String.format("P95 latency: %.2f ms", p95));
        System.out.println(String.format("P99 latency: %.2f ms", p99));
    }
    
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        PerformanceTester tester = new PerformanceTester("localhost", 6379, null);
        tester.runTest(50, 1000);
        tester.close();
    }
}
```

### **Stress Testing**

```bash
# Stress тест с redis-benchmark
redis-benchmark \
  -h localhost \
  -p 6379 \
  -c 100 \
  -n 100000 \
  -d 256 \
  -t set,get \
  -P 16

# Тест разных типов данных
redis-benchmark -t hset,hget -c 50 -n 5000
redis-benchmark -t lpush,lpop -c 50 -n 5000
redis-benchmark -t sadd,spop -c 50 -n 5000
redis-benchmark -t zadd,zrange -c 50 -n 5000
```

---

- [Redis Performance](https://redis.io/docs/management/optimization/)
- [Redis Benchmarking](https://redis.io/docs/management/optimization/benchmarks/)

---


