---
title: "Elasticsearch: Производительность - Оптимизация и тюнинг высоконагруженных кластеров"
description: "Комплексное руководство по оптимизации производительности Elasticsearch: настройка JVM, тюнинг запросов, оптимизация хранения, кэширование и масштабирование для высоких нагрузок."
tags:
  - databases
  - nosql
  - elasticsearch-performance
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Elasticsearch: Производительность — Оптимизация и тюнинг высоконагруженных кластеров

Комплексное руководство по оптимизации производительности **Elasticsearch**: настройка **JVM**, тюнинг запросов, оптимизация хранения, кэширование и масштабирование для высоких нагрузок.

## Полезные ссылки

### Официальная документация
- [Tune for Search Speed](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-search-speed.html)
- [Tune for Indexing Speed](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-indexing-speed.html)
- [JVM Settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/advanced-configuration.html)
- [Performance Troubleshooting](https://www.elastic.co/guide/en/elasticsearch/reference/current/troubleshooting.html)

### Дизайн и оптимизация
- [Performance Best Practices](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-search-speed.html)
- [Hardware Sizing](https://www.elastic.co/guide/en/elasticsearch/reference/current/hardware.html)
- [Index Performance](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html)

### Инструменты
- [Elasticsearch Rally](https://github.com/elastic/rally) — **Performance benchmarking**
- [Hot Threads API](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-hot-threads.html)
- [Slow Log](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-slowlog.html)
- [Profile API](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-profile.html)

### См. также
- [[elasticsearch-basics|Основы]] — **Elasticsearch**
- [[elasticsearch-clustering|Кластеризация]] — кластеризация
- [[elasticsearch-indexing|Индексация]] — индексация документов

## Содержание

- [Архитектура производительности](#архитектура-производительности)
  - [**Performance Factors**](#performance-factors)
    - [**Key Performance Components**](#key-performance-components)
  - [**Performance Trade-offs**](#performance-trade-offs)
    - [**Indexing** vs **Search Performance**](#indexing-vs-search-performance)
- [**JVM** оптимизация](#jvm-оптимизация)
  - [**Heap Size Configuration**](#heap-size-configuration)
    - [**Optimal Heap Sizing**](#optimal-heap-sizing)
- [Рекомендации по размеру heap](#рекомендации-по-размеру-heap)
- [Для dedicated data nodes: heap = system_memory / 2](#для-dedicated-data-nodes-heap-system_memory-2)
- [Максимум: 32GB (для Compressed Oops)](#максимум-32gb-для-compressed-oops)
- [elasticsearch.yml](#elasticsearchyml)
- [Для сервера с 64GB RAM](#для-сервера-с-64gb-ram)
- [Для сервера с 128GB RAM](#для-сервера-с-128gb-ram)
- [Для очень больших heap (>32GB)](#для-очень-больших-heap-32gb)
- [Отключаем Compressed Oops](#отключаем-compressed-oops)
    - [GC **Tuning for Elasticsearch**](#gc-tuning-for-elasticsearch)
- [G1GC настройки для Elasticsearch (рекомендуется)](#g1gc-настройки-для-elasticsearch-рекомендуется)
- [CMS GC (legacy, для версий < 7.0)](#cms-gc-legacy-для-версий-70)
- [GC logging](#gc-logging)
  - [**Memory Management**](#memory-management)
    - [**Off-heap Memory Optimization**](#off-heap-memory-optimization)
  - [**JVM Diagnostic Tools**](#jvm-diagnostic-tools)
    - [**Hot Threads Analysis**](#hot-threads-analysis)
- [Получение hot threads](#получение-hot-threads)
- [С детальными stack traces](#с-детальными-stack-traces)
- [Для конкретного узла](#для-конкретного-узла)
    - [**Heap Dump Analysis**](#heap-dump-analysis)
- [Создание heap dump](#создание-heap-dump)
- [Анализ с Eclipse MAT или VisualVM](#анализ-с-eclipse-mat-или-visualvm)
- [Поиск:](#поиск)
- [Автоматический heap dump при OOM](#автоматический-heap-dump-при-oom)
- [Оптимизация хранения](#оптимизация-хранения)
  - [**Index Settings Optimization**](#index-settings-optimization)
    - [**Merge Policy Tuning**](#merge-policy-tuning)
    - [**Compression Settings**](#compression-settings)
- [Для time-series данных](#для-time-series-данных)
  - [**Translog Optimization**](#translog-optimization)
    - [**Durability** vs **Performance Trade-off**](#durability-vs-performance-trade-off)
- [Для максимальной производительности индексации](#для-максимальной-производительности-индексации)
- [Для максимальной durability](#для-максимальной-durability)
  - [**Force Merge for Read Performance**](#force-merge-for-read-performance)
    - [**Optimizing for Search**](#optimizing-for-search)
- [Force merge для оптимизации поиска](#force-merge-для-оптимизации-поиска)
- [Для больших индексов (постепенно)](#для-больших-индексов-постепенно)
- [Проверка прогресса](#проверка-прогресса)
- [Тюнинг индексации](#тюнинг-индексации)
  - [**Bulk Request Optimization**](#bulk-request-optimization)
    - [**Optimal Bulk Size**](#optimal-bulk-size)
  - [**Index Refresh Tuning**](#index-refresh-tuning)
    - [**Refresh Strategy**](#refresh-strategy)
- [Полностью отключить refresh (только для bulk load)](#полностью-отключить-refresh-только-для-bulk-load)
  - [**Thread Pool Optimization**](#thread-pool-optimization)
    - [**Thread Pool Settings**](#thread-pool-settings)
    - [**Adaptive Thread Pool Sizing**](#adaptive-thread-pool-sizing)
- [Оптимизация поиска](#оптимизация-поиска)
  - [**Query Optimization**](#query-optimization)
    - [**Filter** vs **Query Context**](#filter-vs-query-context)
  - [**Search Template Optimization**](#search-template-optimization)
    - [**Pre-compiled Queries**](#pre-compiled-queries)
- [Создание search template](#создание-search-template)
- [Использование template](#использование-template)
- [Кэширование стратегии](#кэширование-стратегии)
  - [**Query Cache**](#query-cache)
    - [**Query Cache Configuration**](#query-cache-configuration)
- [Для data nodes](#для-data-nodes)
    - [**Cache Monitoring**](#cache-monitoring)
- [Статистика query cache](#статистика-query-cache)
- [Response structure](#response-structure)
  - [**Request Cache**](#request-cache)
    - [**Request Cache Usage**](#request-cache-usage)
- [Включение request cache для индекса](#включение-request-cache-для-индекса)
- [Использование request cache в запросе](#использование-request-cache-в-запросе)
  - [**Field Data Cache**](#field-data-cache)
    - [**Field Data Cache for Aggregations**](#field-data-cache-for-aggregations)
- [Мониторинг field data cache](#мониторинг-field-data-cache)
- [Очистка field data cache](#очистка-field-data-cache)
- [Ограничение размера field data cache](#ограничение-размера-field-data-cache)
- [Масштабирование производительности](#масштабирование-производительности)
  - [**Horizontal Scaling**](#horizontal-scaling)
    - [**Node Addition Strategy**](#node-addition-strategy)
  - [**Vertical Scaling**](#vertical-scaling)
    - [**Node Upgrade Planning**](#node-upgrade-planning)
- [Мониторинг и профилирование](#мониторинг-и-профилирование)
  - [**Performance Metrics Collection**](#performance-metrics-collection)
    - [**Custom Metrics Collector**](#custom-metrics-collector)
  - [**Query Profiling**](#query-profiling)
    - [**Profile API Usage**](#profile-api-usage)
- [Профилирование запроса](#профилирование-запроса)
- [Анализ profile response](#анализ-profile-response)
- [**Troubleshooting** производительности](#решение-проблем-производительности)
  - [**Common Performance Issues**](#common-performance-issues)
    - [**High Search Latency**](#high-search-latency)
    - [**Slow Query Analysis**](#slow-query-analysis)
- [Включение slow log](#включение-slow-log)
- [Анализ slow log](#анализ-slow-log)
- [**Hardware** рекомендации](#hardware-рекомендации)
  - [**CPU Optimization**](#cpu-optimization)
    - [**CPU Configuration**](#cpu-configuration)
- [CPU affinity для Elasticsearch](#cpu-affinity-для-elasticsearch)
- [NUMA оптимизация](#numa-оптимизация)
- [CPU governor settings (для dedicated servers)](#cpu-governor-settings-для-dedicated-servers)
- [BIOS settings:](#bios-settings)
  - [**Memory Optimization**](#memory-optimization)
    - [**Memory Configuration**](#memory-configuration)
- [Huge pages для JVM](#huge-pages-для-jvm)
- [Memory settings](#memory-settings)
- [Disable transparent huge pages](#disable-transparent-huge-pages)
  - [**Storage Optimization**](#storage-optimization)
    - [**Disk Configuration**](#disk-configuration)
- [I/O scheduler для SSD](#io-scheduler-для-ssd)
- [I/O optimizations](#io-optimizations)
- [RAID configuration (recommended for data nodes)](#raid-configuration-recommended-for-data-nodes)
- [RAID 0 for maximum performance (with backups)](#raid-0-for-maximum-performance-with-backups)
- [RAID 10 for balance of performance and redundancy](#raid-10-for-balance-of-performance-and-redundancy)
- [Filesystem mount options](#filesystem-mount-options)
- [/etc/fstab](#etcfstab)
  - [**Network Optimization**](#network-optimization)
    - [**Network Configuration**](#network-configuration)
- [Network buffer sizes](#network-buffer-sizes)
- [TCP optimizations](#tcp-optimizations)
- [Connection tracking](#connection-tracking)
- [**Best Practices**](#лучшие-практики)
  - [**Configuration Best Practices**](#configuration-best-practices)
    - [1. **Heap Size Guidelines**](#1-heap-size-guidelines)
    - [2. **Shard Strategy**](#2-shard-strategy)
    - [3. **Index Settings**](#3-index-settings)
  - [**Monitoring Best Practices**](#monitoring-best-practices)
    - [1. **Key Metrics** to **Monitor**](#1-key-metrics-to-monitor)
    - [2. **Alert Thresholds**](#2-alert-thresholds)
    - [3. **Performance Baselines**](#3-performance-baselines)
  - [**Optimization Workflow**](#optimization-workflow)
    - [1. **Performance Analysis**](#1-performance-analysis)
    - [2. **Optimization Steps**](#2-optimization-steps)
    - [3. **Validation**](#3-validation)
  - [**Capacity Planning**](#capacity-planning)
    - [1. **Growth Projections**](#1-growth-projections)
    - [2. **Scaling Strategy**](#2-scaling-strategy)
  - [**Disaster Recovery**](#disaster-recovery)
    - [1. **Backup Strategy**](#1-backup-strategy)
    - [2. **Recovery Planning**](#2-recovery-planning)
  - [Архитектурные решения:](#архитектурные-решения)
  - [Производительность индексации:](#производительность-индексации)
  - [Производительность поиска:](#производительность-поиска)
  - [Мониторинг и **troubleshooting**:](#мониторинг-и-troubleshooting)
  - [Вызовы и решения:](#вызовы-и-решения)

## Архитектура производительности

### Performance Factors

#### Key Performance Components
```java
public class PerformanceAnalyzer {

    // Основные факторы производительности Elasticsearch
    public PerformanceProfile analyzeClusterPerformance() {
        return PerformanceProfile.builder()
            .indexingThroughput(measureIndexingThroughput())
            .searchLatency(measureSearchLatency())
            .memoryEfficiency(measureMemoryEfficiency())
            .diskIOPerformance(measureDiskIOPerformance())
            .networkThroughput(measureNetworkThroughput())
            .cpuUtilization(measureCpuUtilization())
            .build();
    }

    private IndexingThroughput measureIndexingThroughput() {
        // Измерение скорости индексации
        long totalIndexed = metrics.getTotalDocumentsIndexed();
        long indexingTime = metrics.getTotalIndexingTime();
        double throughput = (double) totalIndexed / (indexingTime / 1000.0); // docs/sec

        return IndexingThroughput.builder()
            .documentsPerSecond(throughput)
            .bulkSize(metrics.getAverageBulkSize())
            .indexLatency(metrics.getAverageIndexLatency())
            .build();
    }

    private SearchLatency measureSearchLatency() {
        // Измерение latency поиска
        return SearchLatency.builder()
            .p50Latency(metrics.getSearchLatencyP50())
            .p95Latency(metrics.getSearchLatencyP95())
            .p99Latency(metrics.getSearchLatencyP99())
            .queryCacheHitRate(metrics.getQueryCacheHitRate())
            .requestCacheHitRate(metrics.getRequestCacheHitRate())
            .build();
    }

    private MemoryEfficiency measureMemoryEfficiency() {
        // Анализ эффективности использования памяти
        long heapUsed = jvmMetrics.getHeapUsed();
        long heapMax = jvmMetrics.getHeapMax();
        double heapUtilization = (double) heapUsed / heapMax;

        return MemoryEfficiency.builder()
            .heapUtilization(heapUtilization)
            .offHeapUsage(jvmMetrics.getOffHeapUsed())
            .cacheSizes(measureCacheSizes())
            .gcOverhead(measureGCOverhead())
            .build();
    }

    private DiskIOPerformance measureDiskIOPerformance() {
        // Производительность дискового IO
        return DiskIOPerformance.builder()
            .readIOPS(metrics.getReadIOPS())
            .writeIOPS(metrics.getWriteIOPS())
            .readThroughput(metrics.getReadThroughput())
            .writeThroughput(metrics.getWriteThroughput())
            .averageLatency(metrics.getAverageDiskLatency())
            .build();
    }

    private NetworkThroughput measureNetworkThroughput() {
        // Пропускная способность сети
        return NetworkThroughput.builder()
            .interNodeThroughput(metrics.getInterNodeThroughput())
            .clientThroughput(metrics.getClientThroughput())
            .averageLatency(metrics.getNetworkLatency())
            .build();
    }

    private CpuUtilization measureCpuUtilization() {
        // Использование CPU
        return CpuUtilization.builder()
            .overallCpu(osMetrics.getCpuUtilization())
            .gcCpuOverhead(jvmMetrics.getGcCpuOverhead())
            .indexingCpu(metrics.getIndexingCpuUsage())
            .searchCpu(metrics.getSearchCpuUsage())
            .build();
    }

    private Map<String, Long> measureCacheSizes() {
        return Map.of(
            "query_cache", metrics.getQueryCacheSize(),
            "request_cache", metrics.getRequestCacheSize(),
            "field_data_cache", metrics.getFieldDataCacheSize()
        );
    }

    private double measureGCOverhead() {
        long gcTime = jvmMetrics.getGcCollectionTime();
        long uptime = jvmMetrics.getUptime();
        return (double) gcTime / uptime; // Процент времени в GC
    }
}
```

### Performance Trade-offs

#### Indexing vs Search Performance
```java
public class PerformanceTradeoffAnalyzer {

    public TradeoffAnalysis analyzeIndexingVsSearchTradeoff(IndexingLoad load, SearchLoad search) {
        // Анализ компромиссов между индексацией и поиском

        double indexingPressure = calculateIndexingPressure(load);
        double searchPressure = calculateSearchPressure(search);

        // Определение bottleneck
        BottleneckType bottleneck = determineBottleneck(indexingPressure, searchPressure);

        // Рекомендации по оптимизации
        List<OptimizationRecommendation> recommendations = generateRecommendations(
            bottleneck, indexingPressure, searchPressure);

        return TradeoffAnalysis.builder()
            .indexingPressure(indexingPressure)
            .searchPressure(searchPressure)
            .bottleneck(bottleneck)
            .recommendations(recommendations)
            .optimalConfiguration(suggestOptimalConfiguration(load, search))
            .build();
    }

    private double calculateIndexingPressure(IndexingLoad load) {
        // Расчет давления на индексацию
        double bulkSizeFactor = Math.log(load.getAverageBulkSize() / 1000.0);
        double frequencyFactor = load.getDocumentsPerSecond() / 10000.0;
        double complexityFactor = load.getAverageDocumentSize() / 1000.0;

        return (bulkSizeFactor + frequencyFactor + complexityFactor) / 3.0;
    }

    private double calculateSearchPressure(SearchLoad search) {
        // Расчет давления на поиск
        double queryComplexity = search.getAverageQueryComplexity();
        double concurrencyFactor = search.getConcurrentQueries() / 100.0;
        double resultSetSize = search.getAverageResultSize() / 100.0;

        return (queryComplexity + concurrencyFactor + resultSetSize) / 3.0;
    }

    private BottleneckType determineBottleneck(double indexingPressure, double searchPressure) {
        if (indexingPressure > searchPressure * 1.5) {
            return BottleneckType.INDEXING_BOTTLENECK;
        } else if (searchPressure > indexingPressure * 1.5) {
            return BottleneckType.SEARCH_BOTTLENECK;
        } else {
            return BottleneckType.BALANCED_LOAD;
        }
    }

    private List<OptimizationRecommendation> generateRecommendations(
            BottleneckType bottleneck, double indexingPressure, double searchPressure) {

        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        switch (bottleneck) {
            case INDEXING_BOTTLENECK:
                recommendations.add(new OptimizationRecommendation(
                    "Increase bulk size", "Use larger bulk requests (5-15MB)"));
                recommendations.add(new OptimizationRecommendation(
                    "Optimize refresh interval", "Increase refresh_interval to 30s during bulk indexing"));
                recommendations.add(new OptimizationRecommendation(
                    "Disable replicas temporarily", "Set number_of_replicas to 0 during bulk load"));
                break;

            case SEARCH_BOTTLENECK:
                recommendations.add(new OptimizationRecommendation(
                    "Enable query cache", "Ensure query cache is enabled and sized appropriately"));
                recommendations.add(new OptimizationRecommendation(
                    "Optimize mappings", "Use keyword fields for exact matches, text for full-text search"));
                recommendations.add(new OptimizationRecommendation(
                    "Add replicas", "Increase number_of_replicas for read scaling"));
                break;

            case BALANCED_LOAD:
                recommendations.add(new OptimizationRecommendation(
                    "Optimize both", "Apply balanced optimizations for both indexing and search"));
                break;
        }

        return recommendations;
    }

    private ClusterConfiguration suggestOptimalConfiguration(IndexingLoad load, SearchLoad search) {
        // Предложение оптимальной конфигурации кластера
        return ClusterConfiguration.builder()
            .heapSize(calculateOptimalHeapSize(load, search))
            .threadPools(calculateOptimalThreadPools(load, search))
            .cacheSizes(calculateOptimalCacheSizes(load, search))
            .build();
    }
}

enum BottleneckType {
    INDEXING_BOTTLENECK, SEARCH_BOTTLENECK, BALANCED_LOAD
}
```

## JVM оптимизация

### Heap Size Configuration

#### Optimal Heap Sizing
```bash
# Рекомендации по размеру heap
# Для dedicated data nodes: heap = system_memory / 2
# Максимум: 32GB (для Compressed Oops)

# elasticsearch.yml
# Для сервера с 64GB RAM
-Xms30g
-Xmx30g

# Для сервера с 128GB RAM
-Xms31g
-Xmx31g  # Оставляем 1GB для OS и Compressed Oops

# Для очень больших heap (>32GB)
# Отключаем Compressed Oops
-XX:-UseCompressedOops
-Xms40g
-Xmx40g
```

#### `GC` Tuning for Elasticsearch
```bash
# G1GC настройки для Elasticsearch (рекомендуется)
-XX:+UseG1GC
-XX:G1ReservePercent=25
-XX:InitiatingHeapOccupancyPercent=30
-XX:G1HeapRegionSize=16m
-XX:MaxGCPauseMillis=200
-XX:G1MixedGCCountTarget=8
-XX:G1OldCSetRegionThresholdPercent=5

# CMS GC (legacy, для версий < 7.0)
-XX:+UseParNewGC
-XX:+UseConcMarkSweepGC
-XX:+CMSParallelRemarkEnabled
-XX:SurvivorRatio=8
-XX:MaxTenuringThreshold=1
-XX:CMSInitiatingOccupancyFraction=75
-XX:+UseCMSInitiatingOccupancyOnly

# GC logging
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintHeapAtGC
-XX:+PrintTenuringDistribution
-XX:+PrintGCApplicationStoppedTime
-Xloggc:/var/log/elasticsearch/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=10
-XX:GCLogFileSize=10m
```

### Memory Management

#### Off-heap Memory Optimization
```java
@Service
public class MemoryManager {

    @Autowired
    private SystemMetrics systemMetrics;

    @Autowired
    private ElasticsearchConfig config;

    public MemoryConfiguration optimizeMemoryConfiguration() {
        long systemMemory = systemMetrics.getTotalMemory();
        long heapSize = config.getHeapSize();

        // Расчет off-heap памяти
        long availableOffHeap = systemMemory - heapSize - (2L * 1024 * 1024 * 1024); // 2GB for OS

        return MemoryConfiguration.builder()
            .heapSize(heapSize)
            .offHeapMemory(calculateOffHeapMemory(availableOffHeap))
            .pageCache(calculatePageCache(availableOffHeap))
            .fileSystemCache(calculateFileSystemCache(availableOffHeap))
            .build();
    }

    private long calculateOffHeapMemory(long availableMemory) {
        // 10-20% от доступной off-heap памяти
        return (long) (availableMemory * 0.15);
    }

    private long calculatePageCache(long availableMemory) {
        // 40-60% от доступной памяти для page cache
        return (long) (availableMemory * 0.5);
    }

    private long calculateFileSystemCache(long availableMemory) {
        // Остальная память для filesystem cache
        return availableMemory - calculateOffHeapMemory(availableMemory) - calculatePageCache(availableMemory);
    }

    // Конфигурация Elasticsearch для оптимизации памяти
    public Map<String, String> generateMemorySettings() {
        MemoryConfiguration memoryConfig = optimizeMemoryConfiguration();

        Map<String, String> settings = new HashMap<>();
        settings.put("bootstrap.memory_lock", "true");
        settings.put("indices.memory.index_buffer_size", "10%");
        settings.put("indices.memory.min_index_buffer_size", "96mb");
        settings.put("indices.memory.max_index_buffer_size", "512mb");

        return settings;
    }
}
```

### JVM Diagnostic Tools

#### Hot Threads Analysis
```bash
# Получение hot threads
curl -X GET "localhost:9200/_nodes/hot_threads"

# С детальными stack traces
curl -X GET "localhost:9200/_nodes/hot_threads?threads=500&ignore_idle_threads=false"

# Для конкретного узла
curl -X GET "localhost:9200/_nodes/node1/hot_threads"
```

#### Heap Dump Analysis
```bash
# Создание heap dump
jmap -dump:format=b,file=heap.hprof <elasticsearch_pid>

# Анализ с Eclipse MAT или VisualVM
# Поиск:
# - Memory leaks
# - Large objects in heap
# - Garbage collection roots

# Автоматический heap dump при OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/elasticsearch/
```

## Оптимизация хранения

### Index Settings Optimization

#### Merge Policy Tuning
```json
PUT my_index/_settings
{
  "index": {
    "merge.policy": {
      "max_merge_at_once": 10,
      "max_merged_segment": "5gb",
      "segments_per_tier": 10.0
    },
    "merge.scheduler": {
      "max_thread_count": 1,
      "max_merge_count": 6
    }
  }
}
```

#### Compression Settings
```json
PUT my_index/_settings
{
  "index": {
    "codec": "best_compression",
    "merge.policy": {
      "max_merged_segment": "1gb"
    }
  }
}

# Для time-series данных
PUT logs-*/_settings
{
  "index": {
    "codec": "best_compression",
    "merge.policy": {
      "max_merged_segment": "2gb"
    }
  }
}
```

### Translog Optimization

#### Durability vs Performance Trade-off
```json
# Для максимальной производительности индексации
PUT my_index/_settings
{
  "index": {
    "translog": {
      "durability": "async",
      "sync_interval": "120s",
      "flush_threshold_size": "1gb"
    }
  }
}

# Для максимальной durability
PUT my_index/_settings
{
  "index": {
    "translog": {
      "durability": "request",
      "sync_interval": "5s",
      "flush_threshold_size": "512mb"
    }
  }
}
```

### Force Merge for Read Performance

#### Optimizing for Search
```bash
# Force merge для оптимизации поиска
curl -X POST "localhost:9200/my_index/_forcemerge?max_num_segments=1"

# Для больших индексов (постепенно)
curl -X POST "localhost:9200/my_index/_forcemerge?max_num_segments=5&only_expunge_deletes=true"

# Проверка прогресса
curl -X GET "localhost:9200/_cat/tasks?v"
```

## Тюнинг индексации

### Bulk Request Optimization

#### Optimal Bulk Size
```java
@Service
public class BulkOptimizer {

    @Autowired
    private MetricsCollector metrics;

    public BulkConfiguration optimizeBulkConfiguration() {
        // Анализ текущих метрик для определения оптимального размера
        double currentThroughput = metrics.getIndexingThroughput();
        long averageBulkSize = metrics.getAverageBulkSize();
        long averageLatency = metrics.getAverageIndexLatency();

        // Расчет оптимального размера bulk
        long optimalBulkSize = calculateOptimalBulkSize(currentThroughput, averageLatency);

        // Расчет оптимального количества одновременных bulk запросов
        int optimalConcurrentRequests = calculateOptimalConcurrency(currentThroughput);

        return BulkConfiguration.builder()
            .bulkSize(optimalBulkSize)
            .concurrentRequests(optimalConcurrentRequests)
            .flushInterval(calculateFlushInterval(optimalBulkSize))
            .retryPolicy(createRetryPolicy())
            .build();
    }

    private long calculateOptimalBulkSize(double throughput, long latency) {
        // Оптимальный размер: 5-15MB для большинства случаев
        // Зависит от throughput и latency

        if (throughput > 50000) { // > 50k docs/sec
            return 15 * 1024 * 1024; // 15MB
        } else if (throughput > 10000) { // > 10k docs/sec
            return 10 * 1024 * 1024; // 10MB
        } else {
            return 5 * 1024 * 1024; // 5MB
        }
    }

    private int calculateOptimalConcurrency(double throughput) {
        // Оптимальная concurrency = CPU cores / 2
        int cpuCores = Runtime.getRuntime().availableProcessors();
        return Math.max(1, cpuCores / 2);
    }

    private long calculateFlushInterval(long bulkSize) {
        // Flush interval: 5-10 секунд для больших bulk
        if (bulkSize > 10 * 1024 * 1024) { // > 10MB
            return 10000; // 10 seconds
        } else {
            return 5000; // 5 seconds
        }
    }

    private RetryPolicy createRetryPolicy() {
        return new ExponentialBackoffRetryPolicy(
            3, // max retries
            1000, // initial delay ms
            30000 // max delay ms
        );
    }
}
```

### Index Refresh Tuning

#### Refresh Strategy
```json
# Для bulk индексации - редкий refresh
PUT my_index/_settings
{
  "index": {
    "refresh_interval": "30s"
  }
}

# Для real-time search - частый refresh
PUT search_index/_settings
{
  "index": {
    "refresh_interval": "1s"
  }
}

# Полностью отключить refresh (только для bulk load)
PUT bulk_index/_settings
{
  "index": {
    "refresh_interval": "-1"
  }
}
```

### Thread Pool Optimization

#### Thread Pool Settings
```yaml
# elasticsearch.yml
thread_pool:
  write:
    size: 8
    queue_size: 200
  search:
    size: 16
    queue_size: 1000
  bulk:
    size: 8
    queue_size: 500
  index:
    size: 8
    queue_size: 200
```

#### Adaptive Thread Pool Sizing
```java
@Service
public class ThreadPoolOptimizer {

    @Autowired
    private MetricsCollector metrics;

    @Autowired
    private SystemResources system;

    public ThreadPoolConfiguration optimizeThreadPools() {
        int availableCores = system.getAvailableCpuCores();
        double currentLoad = metrics.getCurrentLoad();

        return ThreadPoolConfiguration.builder()
            .writePool(calculateWritePool(availableCores, currentLoad))
            .searchPool(calculateSearchPool(availableCores, currentLoad))
            .bulkPool(calculateBulkPool(availableCores, currentLoad))
            .indexPool(calculateIndexPool(availableCores, currentLoad))
            .build();
    }

    private ThreadPoolConfig calculateWritePool(int cores, double load) {
        // Write pool: 20-30% of cores
        int size = Math.max(1, (int) (cores * 0.25));
        int queueSize = size * 50; // 50 items per thread

        return new ThreadPoolConfig(size, queueSize);
    }

    private ThreadPoolConfig calculateSearchPool(int cores, double load) {
        // Search pool: 40-60% of cores
        int size = Math.max(1, (int) (cores * 0.5));
        int queueSize = size * 100; // 100 items per thread

        return new ThreadPoolConfig(size, queueSize);
    }

    private ThreadPoolConfig calculateBulkPool(int cores, double load) {
        // Bulk pool: 20-30% of cores
        int size = Math.max(1, (int) (cores * 0.25));
        int queueSize = size * 50;

        return new ThreadPoolConfig(size, queueSize);
    }

    private ThreadPoolConfig calculateIndexPool(int cores, double load) {
        // Index pool: 10-20% of cores
        int size = Math.max(1, (int) (cores * 0.15));
        int queueSize = size * 25;

        return new ThreadPoolConfig(size, queueSize);
    }
}
```

## Оптимизация поиска

### Query Optimization

#### Filter vs Query Context
```java
@Service
public class QueryOptimizer {

    public OptimizedQuery optimizeSearchQuery(SearchRequest originalRequest) {
        SearchSourceBuilder source = originalRequest.source();

        // Анализ и оптимизация запроса
        OptimizedQuery optimized = new OptimizedQuery(originalRequest);

        // 1. Оптимизация фильтров
        optimizeFilters(source, optimized);

        // 2. Оптимизация запросов
        optimizeQueries(source, optimized);

        // 3. Добавление hints для оптимизации
        addOptimizationHints(source, optimized);

        // 4. Проверка на anti-patterns
        checkAntiPatterns(source, optimized);

        return optimized;
    }

    private void optimizeFilters(SearchSourceBuilder source, OptimizedQuery optimized) {
        // Перемещение static фильтров в filter context
        List<QueryBuilder> filters = extractStaticFilters(source);

        if (!filters.isEmpty()) {
            BoolQueryBuilder boolQuery = getOrCreateBoolQuery(source);
            filters.forEach(boolQuery::filter);
            optimized.addOptimization("Moved " + filters.size() + " filters to filter context");
        }
    }

    private void optimizeQueries(SearchSourceBuilder source, OptimizedQuery optimized) {
        // Оптимизация query structure
        QueryBuilder query = source.query();

        if (query instanceof BoolQueryBuilder) {
            BoolQueryBuilder boolQuery = (BoolQueryBuilder) query;
            optimizeBoolQuery(boolQuery, optimized);
        }
    }

    private void optimizeBoolQuery(BoolQueryBuilder boolQuery, OptimizedQuery optimized) {
        // Проверка на слишком много should clauses
        if (boolQuery.should().size() > 10) {
            optimized.addWarning("Too many should clauses (" + boolQuery.should().size() +
                               "). Consider using best_fields or most_fields type.");
        }

        // Проверка на nested bool queries
        if (hasDeepNesting(boolQuery, 3)) {
            optimized.addWarning("Deeply nested bool queries detected. Consider flattening.");
        }
    }

    private void addOptimizationHints(SearchSourceBuilder source, OptimizedQuery optimized) {
        // Добавление _name к запросам для profiling
        if (source.query() instanceof BoolQueryBuilder) {
            nameQueryClauses((BoolQueryBuilder) source.query());
        }

        // Оптимизация pagination
        if (source.from() > 10000) {
            optimized.addWarning("Using from=" + source.from() + ". Consider search_after for deep pagination.");
        }

        // Оптимизация size
        if (source.size() > 10000) {
            optimized.addWarning("Large result size (" + source.size() + "). Consider pagination.");
        }
    }

    private void checkAntiPatterns(SearchSourceBuilder source, OptimizedQuery optimized) {
        // Проверка на распространенные anti-patterns
        QueryBuilder query = source.query();

        // Leading wildcard
        if (hasLeadingWildcard(query)) {
            optimized.addWarning("Leading wildcard detected. This can be slow on large datasets.");
        }

        // Large result sets without sorting
        if (source.size() > 1000 && source.sorts().isEmpty()) {
            optimized.addWarning("Large result set without explicit sorting. Results may be inconsistent.");
        }
    }

    private List<QueryBuilder> extractStaticFilters(SearchSourceBuilder source) {
        // Логика извлечения static фильтров (term, range без variables)
        return new ArrayList<>();
    }

    private boolean hasDeepNesting(BoolQueryBuilder query, int maxDepth) {
        // Проверка глубины вложенности
        return calculateDepth(query) > maxDepth;
    }

    private int calculateDepth(BoolQueryBuilder query) {
        int maxChildDepth = 0;
        for (QueryBuilder child : query.should()) {
            if (child instanceof BoolQueryBuilder) {
                maxChildDepth = Math.max(maxChildDepth, calculateDepth((BoolQueryBuilder) child));
            }
        }
        return maxChildDepth + 1;
    }

    private boolean hasLeadingWildcard(QueryBuilder query) {
        // Проверка на wildcard запросы начинающиеся с *
        return false; // placeholder
    }

    private void nameQueryClauses(BoolQueryBuilder boolQuery) {
        // Добавление имен к clause для profiling
        // Implementation depends on specific use case
    }
}
```

### Search Template Optimization

#### Pre-compiled Queries
```bash
# Создание search template
PUT _scripts/search_template
{
  "script": {
    "lang": "mustache",
    "source": {
      "query": {
        "bool": {
          "must": [
            {{#query}}
            {"match": {"content": "{{query}}"}},
            {{/query}}
            {{#category}}
            {"term": {"category": "{{category}}"}},
            {{/category}}
          ],
          "filter": [
            {{#date_range}}
            {"range": {"@timestamp": {"gte": "{{start_date}}", "lte": "{{end_date}}"}}},
            {{/date_range}}
          ]
        }
      },
      "sort": [
        {{#sort_by_score}}
        {"_score": "desc"},
        {{/sort_by_score}}
        {{#sort_by_date}}
        {"@timestamp": "desc"},
        {{/sort_by_date}}
      ],
      "_source": {{#fields}}["{{.}}"]{{/fields}}
    }
  }
}

# Использование template
GET my_index/_search/template
{
  "id": "search_template",
  "params": {
    "query": "elasticsearch",
    "category": "technology",
    "date_range": true,
    "start_date": "2023-01-01",
    "end_date": "2023-12-31",
    "sort_by_score": true,
    "fields": ["title", "content", "category"]
  }
}
```

## Кэширование стратегии

### Query Cache

#### Query Cache Configuration
```yaml
# elasticsearch.yml
indices.queries.cache.size: 10%
indices.requests.cache.size: 1%

# Для data nodes
node.roles: [ data ]
```

#### Cache Monitoring
```bash
# Статистика query cache
curl -X GET "localhost:9200/_nodes/stats/indices/query_cache?pretty"

# Response structure
{
  "nodes": {
    "node1": {
      "indices": {
        "query_cache": {
          "memory_size_in_bytes": 20971520,
          "total_count": 1000,
          "hit_count": 800,
          "miss_count": 200,
          "cache_size": 150,
          "cache_count": 150,
          "evictions": 10
        }
      }
    }
  }
}
```

### Request Cache

#### Request Cache Usage
```bash
# Включение request cache для индекса
curl -X PUT "localhost:9200/my_index/_settings" \
  -H "Content-Type: application/json" \
  -d '{
    "index.requests.cache.enable": true
  }'

# Использование request cache в запросе
curl -X GET "localhost:9200/my_index/_search?request_cache=true" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "category": "electronics"
      }
    },
    "size": 0
  }'
```

### Field Data Cache

#### Field Data Cache for Aggregations
```bash
# Мониторинг field data cache
curl -X GET "localhost:9200/_nodes/stats/indices/fielddata?pretty"

# Очистка field data cache
curl -X POST "localhost:9200/my_index/_cache/clear?fielddata=true"

# Ограничение размера field data cache
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "indices.fielddata.cache.size": "30%"
    }
  }'
```

## Масштабирование производительности

### Horizontal Scaling

#### Node Addition Strategy
```java
@Service
public class ScalingOptimizer {

    @Autowired
    private ClusterMetrics metrics;

    @Autowired
    private NodeProvisioningService provisioning;

    public ScalingRecommendation analyzeScalingNeeds() {
        ClusterMetrics current = metrics.getCurrentMetrics();

        // Анализ необходимости масштабирования
        ScalingRecommendation recommendation = new ScalingRecommendation();

        // CPU scaling
        if (current.getAverageCpuUtilization() > 0.8) {
            recommendation.addNodeRecommendation(
                NodeRole.DATA,
                calculateNodesNeeded(current.getAverageCpuUtilization(), "cpu")
            );
        }

        // Memory scaling
        if (current.getAverageMemoryUtilization() > 0.85) {
            recommendation.addNodeRecommendation(
                NodeRole.DATA,
                calculateNodesNeeded(current.getAverageMemoryUtilization(), "memory")
            );
        }

        // Disk scaling
        if (current.getAverageDiskUtilization() > 0.75) {
            recommendation.addNodeRecommendation(
                NodeRole.DATA,
                calculateNodesNeeded(current.getAverageDiskUtilization(), "disk")
            );
        }

        // Search scaling
        if (current.getSearchLatencyP95() > 500) { // 500ms
            recommendation.addNodeRecommendation(NodeRole.DATA, 2);
        }

        return recommendation;
    }

    private int calculateNodesNeeded(double currentUtilization, String resourceType) {
        // Расчет необходимого количества узлов
        double targetUtilization = getTargetUtilization(resourceType);
        double utilizationRatio = currentUtilization / targetUtilization;

        int currentNodes = clusterState.getDataNodes().size();
        return (int) Math.ceil(currentNodes * utilizationRatio) - currentNodes;
    }

    private double getTargetUtilization(String resourceType) {
        switch (resourceType) {
            case "cpu": return 0.7;
            case "memory": return 0.8;
            case "disk": return 0.7;
            default: return 0.75;
        }
    }

    public void executeScaling(ScalingRecommendation recommendation) {
        for (Map.Entry<NodeRole, Integer> entry : recommendation.getNodeRecommendations().entrySet()) {
            NodeRole role = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                provisioning.provisionNode(role, getOptimalNodeSize(role));
            }
        }
    }

    private NodeSize getOptimalNodeSize(NodeRole role) {
        switch (role) {
            case MASTER:
                return new NodeSize(2, 8, 100); // 2 CPU, 8GB RAM, 100GB disk
            case DATA:
                return new NodeSize(8, 32, 1000); // 8 CPU, 32GB RAM, 1TB disk
            case INGEST:
                return new NodeSize(4, 16, 500); // 4 CPU, 16GB RAM, 500GB disk
            default:
                return new NodeSize(4, 16, 500);
        }
    }
}

enum NodeRole {
    MASTER, DATA, INGEST, COORDINATING
}

class ScalingRecommendation {
    private Map<NodeRole, Integer> nodeRecommendations = new HashMap<>();

    public void addNodeRecommendation(NodeRole role, int count) {
        nodeRecommendations.put(role, nodeRecommendations.getOrDefault(role, 0) + count);
    }

    public Map<NodeRole, Integer> getNodeRecommendations() {
        return nodeRecommendations;
    }
}
```

### Vertical Scaling

#### Node Upgrade Planning
```java
@Service
public class NodeUpgradePlanner {

    public UpgradePlan planNodeUpgrade(String nodeId, HardwareUpgrade upgrade) {
        Node currentNode = clusterManager.getNode(nodeId);
        PerformanceImpact impact = calculatePerformanceImpact(currentNode, upgrade);

        return UpgradePlan.builder()
            .nodeId(nodeId)
            .currentHardware(currentNode.getHardware())
            .targetHardware(upgrade)
            .performanceImpact(impact)
            .downtimeEstimate(estimateDowntime(upgrade))
            .costAnalysis(calculateUpgradeCost(upgrade))
            .rollbackPlan(createRollbackPlan(currentNode))
            .build();
    }

    private PerformanceImpact calculatePerformanceImpact(Node currentNode, HardwareUpgrade upgrade) {
        double cpuImprovement = calculateCpuImprovement(currentNode, upgrade);
        double memoryImprovement = calculateMemoryImprovement(currentNode, upgrade);
        double diskImprovement = calculateDiskImprovement(currentNode, upgrade);

        return PerformanceImpact.builder()
            .cpuImprovement(cpuImprovement)
            .memoryImprovement(memoryImprovement)
            .diskImprovement(diskImprovement)
            .overallImprovement(calculateOverallImprovement(cpuImprovement, memoryImprovement, diskImprovement))
            .build();
    }

    private double calculateCpuImprovement(Node currentNode, HardwareUpgrade upgrade) {
        if (upgrade.getCpuCores() > currentNode.getCpuCores()) {
            return (double) upgrade.getCpuCores() / currentNode.getCpuCores() - 1.0;
        }
        return 0.0;
    }

    private double calculateMemoryImprovement(Node currentNode, HardwareUpgrade upgrade) {
        if (upgrade.getMemoryGB() > currentNode.getMemoryGB()) {
            return (double) upgrade.getMemoryGB() / currentNode.getMemoryGB() - 1.0;
        }
        return 0.0;
    }

    private double calculateDiskImprovement(Node currentNode, HardwareUpgrade upgrade) {
        if (upgrade.isSSDUpgrade()) {
            return 2.0; // SSD typically 2x faster than HDD
        }
        return 0.0;
    }

    private double calculateOverallImprovement(double cpu, double memory, double disk) {
        // Weighted average based on typical workload
        return cpu * 0.4 + memory * 0.4 + disk * 0.2;
    }

    private Duration estimateDowntime(HardwareUpgrade upgrade) {
        if (upgrade.isMemoryUpgrade() && upgrade.getMemoryGB() <= 64) {
            return Duration.ofMinutes(30); // Hot-add possible
        } else if (upgrade.isCpuUpgrade()) {
            return Duration.ofHours(4); // Requires restart
        } else {
            return Duration.ofHours(2); // Disk or other upgrades
        }
    }
}
```

## Мониторинг и профилирование

### Performance Metrics Collection

#### Custom Metrics Collector
```java
@Service
public class PerformanceMetricsCollector {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private MetricsRegistry registry;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectPerformanceMetrics() {
        try {
            // Cluster-level metrics
            collectClusterMetrics();

            // Node-level metrics
            collectNodeMetrics();

            // Index-level metrics
            collectIndexMetrics();

            // Application-level metrics
            collectApplicationMetrics();

        } catch (Exception e) {
            log.error("Failed to collect performance metrics", e);
        }
    }

    private void collectClusterMetrics() throws IOException {
        ClusterHealthResponse health = client.cluster().health();
        ClusterStatsResponse stats = client.cluster().stats();

        registry.gauge("cluster.health.status", getStatusValue(health.getStatus()));
        registry.gauge("cluster.nodes.total", health.getNumberOfNodes());
        registry.gauge("cluster.nodes.data", health.getNumberOfDataNodes());
        registry.gauge("cluster.shards.active", health.getActiveShards());
        registry.gauge("cluster.shards.unassigned", health.getUnassignedShards());
        registry.gauge("cluster.pending_tasks", health.getNumberOfPendingTasks());
    }

    private void collectNodeMetrics() throws IOException {
        NodesStatsResponse nodesStats = client.nodes().stats();

        for (Map.Entry<String, NodeStats> entry : nodesStats.getNodes().entrySet()) {
            String nodeId = entry.getKey();
            NodeStats stats = entry.getValue();

            // JVM metrics
            JvmStats jvm = stats.getJvm();
            registry.gauge("node.jvm.heap.used", nodeId, jvm.getMem().getHeapUsed().getBytes());
            registry.gauge("node.jvm.heap.max", nodeId, jvm.getMem().getHeapMax().getBytes());

            // OS metrics
            OsStats os = stats.getOs();
            registry.gauge("node.os.cpu.percent", nodeId, os.getCpu().getPercent());

            // Index metrics
            IndicesStats indices = stats.getIndices();
            registry.gauge("node.indices.docs.count", nodeId, indices.getDocs().getCount());
            registry.gauge("node.indices.search.query_total", nodeId, indices.getSearch().getTotal());
            registry.gauge("node.indices.indexing.index_total", nodeId, indices.getIndexing().getIndexTotal());
        }
    }

    private void collectIndexMetrics() throws IOException {
        CatIndicesResponse indices = client.cat().indices();
        IndicesStatsResponse stats = client.indices().stats();

        // Per-index metrics
        for (CatIndicesRecord index : indices.valueBody()) {
            String indexName = index.index();

            registry.gauge("index.docs.count", indexName, index.docsCount());
            registry.gauge("index.store.size", indexName, index.storeSizeBytes());
            registry.gauge("index.health.status", indexName, getHealthValue(index.health()));
        }
    }

    private void collectApplicationMetrics() {
        // Custom application metrics
        registry.gauge("app.search.requests", getSearchRequestCount());
        registry.gauge("app.index.requests", getIndexRequestCount());
        registry.histogram("app.search.latency", getSearchLatencies());
        registry.histogram("app.index.latency", getIndexLatencies());
    }

    private int getStatusValue(ClusterHealthStatus status) {
        switch (status) {
            case GREEN: return 0;
            case YELLOW: return 1;
            case RED: return 2;
            default: return -1;
        }
    }

    private int getHealthValue(String health) {
        switch (health.toLowerCase()) {
            case "green": return 0;
            case "yellow": return 1;
            case "red": return 2;
            default: return -1;
        }
    }

    // Методы для сбора метрик приложения (CPU, heap, GC и т.д.)
    private long getSearchRequestCount() { return 0; }
    private long getIndexRequestCount() { return 0; }
    private List<Double> getSearchLatencies() { return new ArrayList<>(); }
    private List<Double> getIndexLatencies() { return new ArrayList<>(); }
}
```

### Query Profiling

#### Profile API Usage
```bash
# Профилирование запроса
curl -X GET "localhost:9200/my_index/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "profile": true,
    "query": {
      "match": {
        "content": "elasticsearch performance"
      }
    }
  }'

# Анализ profile response
{
  "profile": {
    "shards": [
      {
        "id": "[shard_id]",
        "searches": [
          {
            "query": [
              {
                "type": "BooleanQuery",
                "description": "content:elasticsearch content:performance",
                "time": "5.2ms",
                "breakdown": {
                  "score": 0,
                  "build_scorer_count": 1,
                  "match_count": 0,
                  "create_weight": 1200,
                  "next_doc": 2300,
                  "match": 0,
                  "create_weight_count": 1,
                  "next_doc_count": 5,
                  "score_count": 5,
                  "build_scorer": 2800
                }
              }
            ],
            "rewrite_time": 4500,
            "collector": [
              {
                "name": "CancellableCollector",
                "reason": "search_cancelled",
                "time": "0.5ms"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

## Решение проблем производительности

### Common Performance Issues

#### High Search Latency
```java
@Service
public class PerformanceTroubleshooter {

    @Autowired
    private MetricsCollector metrics;

    @Autowired
    private QueryAnalyzer queryAnalyzer;

    public List<PerformanceIssue> diagnosePerformanceIssues() {
        List<PerformanceIssue> issues = new ArrayList<>();

        // Check search latency
        double p95Latency = metrics.getSearchLatencyP95();
        if (p95Latency > 500) { // 500ms threshold
            issues.add(PerformanceIssue.builder()
                .type(IssueType.HIGH_SEARCH_LATENCY)
                .severity(Severity.HIGH)
                .description("P95 search latency is " + p95Latency + "ms")
                .recommendations(generateLatencyRecommendations())
                .build());
        }

        // Check indexing throughput
        double indexingThroughput = metrics.getIndexingThroughput();
        if (indexingThroughput < 1000) { // 1000 docs/sec threshold
            issues.add(PerformanceIssue.builder()
                .type(IssueType.LOW_INDEXING_THROUGHPUT)
                .severity(Severity.MEDIUM)
                .description("Indexing throughput is " + indexingThroughput + " docs/sec")
                .recommendations(generateIndexingRecommendations())
                .build());
        }

        // Check JVM heap usage
        double heapUsage = metrics.getAverageHeapUsage();
        if (heapUsage > 0.85) {
            issues.add(PerformanceIssue.builder()
                .type(IssueType.HIGH_HEAP_USAGE)
                .severity(Severity.HIGH)
                .description("Average heap usage is " + (heapUsage * 100) + "%")
                .recommendations(generateHeapRecommendations())
                .build());
        }

        // Check slow queries
        List<String> slowQueries = queryAnalyzer.findSlowQueries();
        if (!slowQueries.isEmpty()) {
            issues.add(PerformanceIssue.builder()
                .type(IssueType.SLOW_QUERIES)
                .severity(Severity.MEDIUM)
                .description("Found " + slowQueries.size() + " slow queries")
                .recommendations(generateQueryRecommendations(slowQueries))
                .build());
        }

        return issues;
    }

    private List<String> generateLatencyRecommendations() {
        return Arrays.asList(
            "Check cluster health - ensure all shards are allocated",
            "Review slow query logs for optimization opportunities",
            "Consider adding more data nodes for horizontal scaling",
            "Check network latency between nodes",
            "Optimize query structure - use filters for static conditions",
            "Increase refresh interval if real-time search is not required"
        );
    }

    private List<String> generateIndexingRecommendations() {
        return Arrays.asList(
            "Increase bulk request size (5-15MB recommended)",
            "Reduce refresh interval during bulk indexing",
            "Consider disabling replicas during initial bulk load",
            "Check for optimal thread pool sizes",
            "Monitor disk I/O and consider SSD upgrade",
            "Review mapping for unnecessary analyzed fields"
        );
    }

    private List<String> generateHeapRecommendations() {
        return Arrays.asList(
            "Increase heap size if system memory allows",
            "Review field data cache usage",
            "Check for memory leaks in custom plugins",
            "Optimize aggregations to use less memory",
            "Consider using doc values instead of field data",
            "Monitor garbage collection patterns"
        );
    }

    private List<String> generateQueryRecommendations(List<String> slowQueries) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Review slow query logs for optimization opportunities");
        recommendations.add("Consider adding indexes for frequently queried fields");
        recommendations.add("Use query profiling to identify bottlenecks");

        // Specific recommendations based on slow queries
        for (String query : slowQueries) {
            if (query.contains("leading wildcard")) {
                recommendations.add("Avoid leading wildcards - they require full index scan");
            }
            if (query.contains("large result sets")) {
                recommendations.add("Implement pagination for large result sets");
            }
        }

        return recommendations;
    }
}

enum IssueType {
    HIGH_SEARCH_LATENCY,
    LOW_INDEXING_THROUGHPUT,
    HIGH_HEAP_USAGE,
    SLOW_QUERIES,
    HIGH_CPU_USAGE,
    DISK_IO_BOTTLENECK
}

enum Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}
```

#### Slow Query Analysis
```bash
# Включение slow log
curl -X PUT "localhost:9200/my_index/_settings" \
  -H "Content-Type: application/json" \
  -d '{
    "index.search.slowlog.threshold.query.warn": "10s",
    "index.search.slowlog.threshold.query.info": "5s",
    "index.search.slowlog.threshold.query.debug": "2s",
    "index.search.slowlog.threshold.fetch.warn": "1s",
    "index.search.slowlog.threshold.fetch.info": "800ms",
    "index.search.slowlog.threshold.fetch.debug": "500ms"
  }'

# Анализ slow log
tail -f /var/log/elasticsearch/my_index_index_search_slowlog.log
```

## Hardware рекомендации

### CPU Optimization

#### CPU Configuration
```bash
# CPU affinity для Elasticsearch
taskset -c 0-15 elasticsearch  # Привязка к первым 16 ядрам

# NUMA оптимизация
numactl --interleave=all elasticsearch

# CPU governor settings (для dedicated servers)
cpupower frequency-set -g performance

# BIOS settings:
# - Disable C-states deeper than C1
# - Disable turbo boost for consistent performance
# - Enable all cores
```

### Memory Optimization

#### Memory Configuration
```bash
# Huge pages для JVM
echo 14336 > /proc/sys/vm/nr_hugepages  # 14336 * 2MB = 28GB

# Memory settings
echo "vm.max_map_count=262144" >> /etc/sysctl.conf
echo "vm.swappiness=1" >> /etc/sysctl.conf
echo "vm.dirty_ratio=30" >> /etc/sysctl.conf
echo "vm.dirty_background_ratio=5" >> /etc/sysctl.conf

# Disable transparent huge pages
echo never > /sys/kernel/mm/transparent_hugepage/enabled
echo never > /sys/kernel/mm/transparent_hugepage/defrag

sysctl -p
```

### Storage Optimization

#### Disk Configuration
```bash
# I/O scheduler для SSD
echo "deadline" > /sys/block/sda/queue/scheduler

# I/O optimizations
echo 1024 > /sys/block/sda/queue/nr_requests
echo 256 > /sys/block/sda/queue/read_ahead_kb
echo 0 > /sys/block/sda/queue/rotational

# RAID configuration (recommended for data nodes)
# RAID 0 for maximum performance (with backups)
# RAID 10 for balance of performance and redundancy

# Filesystem mount options
# /etc/fstab
UUID=... /var/lib/elasticsearch ext4 noatime,nodiratime,nobarrier,data=writeback 0 0
```

### Network Optimization

#### Network Configuration
```bash
# Network buffer sizes
echo "net.core.rmem_max=16777216" >> /etc/sysctl.conf
echo "net.core.wmem_max=16777216" >> /etc/sysctl.conf
echo "net.core.rmem_default=4194304" >> /etc/sysctl.conf
echo "net.core.wmem_default=4194304" >> /etc/sysctl.conf

# TCP optimizations
echo "net.ipv4.tcp_rmem=4096 87380 16777216" >> /etc/sysctl.conf
echo "net.ipv4.tcp_wmem=4096 65536 16777216" >> /etc/sysctl.conf
echo "net.ipv4.tcp_congestion_control=cubic" >> /etc/sysctl.conf

# Connection tracking
echo "net.nf_conntrack_max=1048576" >> /etc/sysctl.conf

sysctl -p
```

## Лучшие практики

### Configuration Best Practices

#### 1. Heap Size Guidelines
- **Data nodes**: 50% of **system memory**, **max** 32GB
- **Master nodes**: 50% of **system memory**, **max** 16GB
- **Coordinating nodes**: 50% of **system memory**, **max** 32GB
- **Never exceed 32GB without disabling Compressed Oops**

#### 2. Shard Strategy
- **Size per shard**: 10-50GB **for optimal performance**
- **Total shards per node**: **Keep under** 20-25 **per** `GB` **heap**
- **Start small**: **Better** to **have larger shards initially**
- **Plan for growth**: **Consider future data volume**

#### 3. Index Settings
- **Refresh interval**: 1s **for real-time**, 30s **for bulk indexing**
- **Number of replicas**: 1 **for most use cases** (adjust based on SLAs)
- **Translog settings**: **Balance durability** vs **performance**
- **Merge policy**: **Optimize based** on **use case**

### Monitoring Best Practices

#### 1. Key Metrics to Monitor
- **Search performance**: **P50**, **P95**, **P99 latency**
- **Indexing performance**: **Throughput**, **latency**
- **Resource usage**: **CPU**, **memory**, **disk**, **network**
- **Cluster health**: **Status**, **unassigned shards**, **pending tasks**

#### 2. Alert Thresholds
- **Critical**: **Red cluster status**, **unassigned shards**
- **High**: **P95 latency** > 500ms, **heap usage** > 85%
- **Medium**: **CPU usage** > 80%, **disk usage** > 75%
- **Low**: **Indexing throughput drops** > 20%

#### 3. Performance Baselines
- **Establish baselines during normal operation**
- **Monitor trends over time**
- **Set realistic targets based** on **use case**
- **Regular benchmarking after changes**

### Optimization Workflow

#### 1. Performance Analysis
1. **Identify bottlenecks using monitoring and profiling**
2. **Analyze query patterns and slow logs**
3. **Review resource usage and saturation points**
4. **Test hypotheses with controlled changes**

#### 2. Optimization Steps
1. **Start with `low-risk` changes**: **Index settings**, **refresh intervals**
2. **Optimize queries**: **Filters**, **pagination**, **search templates**
3. **Tune JVM**: **Heap size**, `GC` **settings**
4. **Scale infrastructure**: **Add nodes** or **upgrade hardware**

#### 3. Validation
1. **Benchmark before and after changes**
2. **Monitor for regressions** in **other areas**
3. **Load test with production-like data**
4. **Document changes and their impact**

### Capacity Planning

#### 1. Growth Projections
- **Data volume**: 30-50% **annual growth typical**
- **Query load**: 20-40% **annual growth**
- **Hardware lifecycle**: 3-5 **years**
- **Buffer capacity**: **Plan for** 20-30% **extra capacity**

#### 2. Scaling Strategy
- **Horizontal scaling**: **Preferred for Elasticsearch**
- **Vertical scaling**: **For CPU** or **memory bound workloads**
- **Storage scaling**: **Add nodes** or **increase disk capacity**
- **Network scaling**: **Ensure sufficient bandwidth**

### Disaster Recovery

#### 1. Backup Strategy
- **Snapshot frequency**: **Based** on **RPO requirements**
- **Retention period**: **Based** on **compliance and business needs**
- **Storage location**: **Multiple regions for geo-redundancy**
- **Backup validation**: **Regular restore testing**

#### 2. Recovery Planning
- **RTO targets**: **Maximum acceptable downtime**
- **RPO targets**: **Maximum acceptable data loss**
- **Recovery procedures**: **Documented step-by-step processes**
- **Testing**: **Regular** `DR` **drills and validation**

**Оптимизация производительности **Elasticsearch** — это комплексная дисциплина, требующая глубокого понимания архитектуры, тщательного мониторинга и систематического подхода к оптимизации. Ключевые аспекты успешной оптимизации включают:**

### Архитектурные решения:

1. **Правильное sizing**: **Heap size**, **shard strategy**, **hardware selection**
2. **Оптимальная конфигурация**: **JVM settings**, **index settings**, **cluster topology**
3. **Эффективное кэширование**: **Query cache**, **request cache**, **field data cache**
4. **Масштабируемая архитектура**: **Horizontal scaling**, **data tiering**

### Производительность индексации:

1. **Bulk optimization**: Размер **bulk**, **concurrency**, **retry logic**
2. **Index tuning**: **Refresh interval**, **replicas**, **translog settings**
3. **Merge optimization**: **Merge policy**, **force merge operations**
4. **Storage optimization**: **Compression**, **translog durability**

### Производительность поиска:

1. **Query optimization**: **Filter context**, **query structure**, **search templates**
2. **Cache utilization**: **Query cache**, **request cache effectiveness**
3. **Index optimization**: **Mapping design**, **analyzer selection**
4. **Hardware optimization**: **CPU**, **memory**, **disk**, **network tuning**

### Мониторинг и troubleshooting:

1. **Комплексный мониторинг**: **System metrics**, **Elasticsearch metrics**, **application metrics**
2. **Query profiling**: **Profile API**, **slow logs**, **performance analysis**
3. **Alerting**: **Thresholds**, **escalation**, **automated response**
4. **Root cause analysis**: **Systematic troubleshooting approach**

### Best practices:

1. **Performance culture**: **Regular benchmarking**, **trend monitoring**
2. **Capacity planning**: **Growth projections**, **scaling strategies**
3. **Configuration management**: **Version control**, **change management**
4. **Continuous optimization**: **Regular review and improvement**

### Вызовы и решения:

1. **Сложность оптимизации**: Требует экспертизы и опыта
2. **Trade-offs**: Баланс между различными аспектами производительности
3. **Непрерывные изменения**: **Workload** эволюционирует со временем
4. **Ресурсные ограничения**: **Hardware** и бюджет ограничения

Оптимальная производительность достигается не разовыми оптимизациями, а систематическим подходом к мониторингу, анализу и улучшениям. **Elasticsearch** способен обеспечить превосходную производительность в самых требовательных сценариях при правильной настройке и оптимизации. 🎯

**Завершение `Elasticsearch` проекта:**
- ✅ **elasticsearch-basics.md** (завершен)
- ✅ **elasticsearch-indexing.md** (завершен)
- ✅ **elasticsearch-queries.md** (завершен)
- ✅ **elasticsearch-aggregations.md** (завершен)
- ✅ **elasticsearch-clustering.md** (завершен)
- ✅ **elasticsearch-performance.md** (завершен)

**Все файлы `Elasticsearch` завершены! 🎉**

Теперь можно переходить к следующему этапу — **MySQL**, **Cassandra** или другим базам данных в рамках общей дорожной карты проекта. 🚀


