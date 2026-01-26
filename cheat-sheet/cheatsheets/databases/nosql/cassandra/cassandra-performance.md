# Cassandra: Производительность и оптимизация - Тюнинг и мониторинг высоконагруженных кластеров

Комплексное руководство по оптимизации производительности Apache Cassandra: настройка JVM, тюнинг запросов, мониторинг метрик и устранение узких мест в высоконагруженных системах.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Performance Tuning](https://cassandra.apache.org/doc/latest/cassandra/operating/tuning.html)
- [JVM Tuning](https://cassandra.apache.org/doc/latest/cassandra/operating/jvm-tuning.html)
- [Hardware Choices](https://cassandra.apache.org/doc/latest/cassandra/operating/hardware.html)

### Дизайн и оптимизация
- [Cassandra Performance Best Practices](https://docs.datastax.com/en/dse/6.8/dse-admin/datastax_enterprise/operations/opsPerformance.html)
- [Cassandra Anti-Patterns](https://docs.datastax.com/en/dse/6.8/dse-dev/datamodeling/bestPractices/antiPatterns.html)
- [Performance Monitoring](https://docs.datastax.com/en/opscenter/6.8/opscOnline_help/opscMonitoring_r.html)

### Инструменты
- [DataStax OpsCenter](https://docs.datastax.com/en/opscenter/6.8/opscOnline_help/index.html)
- [Cassandra Reaper](https://cassandra-reaper.io/)
- [Cassandra Stress Tool](https://cassandra.apache.org/doc/latest/tools/cassandra_stress.html)
- [JMX Monitoring](https://cassandra.apache.org/doc/latest/cassandra/operating/monitoring.html)

### См. также
- `databases/cassandra-basics.md` - Основы Cassandra
- `databases/cassandra-data-modeling.md` - Моделирование данных
- `databases/cassandra-clustering.md` - Кластеризация
- `databases/cassandra-admin.md` - Администрирование

## Содержание

- [Архитектура производительности](#архитектура-производительности)
- [Оптимизация JVM](#оптимизация-jvm)
- [Тюнинг операционной системы](#тюнинг-операционной-системы)
- [Оптимизация запросов](#оптимизация-запросов)
- [Индексы и кэширование](#индексы-и-кэширование)
- [Оптимизация хранения](#оптимизация-хранения)
- [Масштабирование производительности](#масштабирование-производительности)
- [Мониторинг и метрики](#мониторинг-и-метрики)
- [Инструменты профилирования](#инструменты-профилирования)
- [Устранение проблем производительности](#устранение-проблем-производительности)
- [Лучшие практики](#лучшие-практики)
- [Заключение](#заключение)

## Архитектура производительности

### Ключевые факторы производительности

#### Read Performance Factors
```java
public class ReadPerformanceAnalyzer {

    // Факторы влияющие на скорость чтения
    public PerformanceFactors analyzeReadPerformance() {
        return PerformanceFactors.builder()
            .dataLocality(analyzeDataLocality())
            .consistencyLevel(analyzeConsistencyImpact())
            .caching(analyzeCacheEfficiency())
            .compaction(analyzeCompactionStrategy())
            .diskIO(analyzeDiskIOPatterns())
            .network(analyzeNetworkLatency())
            .build();
    }

    private double analyzeDataLocality() {
        // Анализ локальности данных
        // Влияние: partition key distribution
        double totalPartitions = clusterManager.getTotalPartitions();
        double localPartitions = clusterManager.getLocalPartitions();
        return localPartitions / totalPartitions;
    }

    private LatencyImpact analyzeConsistencyImpact() {
        // Влияние уровня консистентности на latency
        return new LatencyImpact(
            Map.of(
                ConsistencyLevel.ONE, 1.0,
                ConsistencyLevel.QUORUM, 2.5,
                ConsistencyLevel.ALL, 4.0
            )
        );
    }

    private CacheEfficiency analyzeCacheEfficiency() {
        // Эффективность кэширования
        long cacheHits = metrics.getCacheHits();
        long cacheMisses = metrics.getCacheMisses();
        double hitRate = (double) cacheHits / (cacheHits + cacheMisses);

        return new CacheEfficiency(hitRate, cacheHits, cacheMisses);
    }

    private CompactionMetrics analyzeCompactionStrategy() {
        // Влияние стратегии compaction
        return CompactionMetrics.builder()
            .strategy(currentTable.getCompactionStrategy())
            .pendingTasks(compactionManager.getPendingTasks())
            .compactionThroughput(metrics.getCompactionThroughput())
            .build();
    }

    private IOPatterns analyzeDiskIOPatterns() {
        // Паттерны дискового IO
        return IOPatterns.builder()
            .readIOPS(metrics.getReadIOPS())
            .writeIOPS(metrics.getWriteIOPS())
            .sequentialRatio(metrics.getSequentialReadRatio())
            .randomRatio(metrics.getRandomReadRatio())
            .build();
    }

    private NetworkMetrics analyzeNetworkLatency() {
        // Сетевая latency
        return NetworkMetrics.builder()
            .interNodeLatency(metrics.getAverageInterNodeLatency())
            .clientLatency(metrics.getAverageClientLatency())
            .networkUtilization(metrics.getNetworkUtilization())
            .build();
    }
}

@Builder
class PerformanceFactors {
    private double dataLocality;
    private LatencyImpact consistencyLevel;
    private CacheEfficiency caching;
    private CompactionMetrics compaction;
    private IOPatterns diskIO;
    private NetworkMetrics network;
}
```

#### Write Performance Factors
```java
public class WritePerformanceAnalyzer {

    public WritePerformanceProfile analyzeWritePerformance() {
        return WritePerformanceProfile.builder()
            .commitLog(analyzeCommitLogPerformance())
            .memtable(analyzeMemtablePerformance())
            .flushRate(analyzeFlushRate())
            .compaction(analyzeWriteAmplification())
            .replication(analyzeReplicationOverhead())
            .build();
    }

    private CommitLogMetrics analyzeCommitLogPerformance() {
        return CommitLogMetrics.builder()
            .syncLatency(metrics.getCommitLogSyncLatency())
            .segmentSize(config.getCommitLogSegmentSize())
            .syncStrategy(config.getCommitLogSyncStrategy())
            .build();
    }

    private MemtableMetrics analyzeMemtablePerformance() {
        return MemtableMetrics.builder()
            .heapUsage(metrics.getMemtableHeapUsage())
            .offHeapUsage(metrics.getMemtableOffHeapUsage())
            .flushThreshold(config.getMemtableFlushThreshold())
            .build();
    }

    private FlushMetrics analyzeFlushRate() {
        long totalFlushes = metrics.getTotalMemtableFlushes();
        long averageFlushTime = metrics.getAverageFlushTime();
        long flushQueueSize = metrics.getFlushQueueSize();

        return new FlushMetrics(totalFlushes, averageFlushTime, flushQueueSize);
    }

    private CompactionMetrics analyzeWriteAmplification() {
        double writeAmplification = calculateWriteAmplification();
        double spaceAmplification = calculateSpaceAmplification();

        return CompactionMetrics.builder()
            .writeAmplification(writeAmplification)
            .spaceAmplification(spaceAmplification)
            .compactionStrategy(currentStrategy)
            .build();
    }

    private double calculateWriteAmplification() {
        // Write amplification = total bytes written / bytes from application
        long totalBytesWritten = metrics.getTotalBytesWritten();
        long applicationBytes = metrics.getApplicationBytesWritten();
        return (double) totalBytesWritten / applicationBytes;
    }

    private ReplicationMetrics analyzeReplicationOverhead() {
        int replicationFactor = keyspace.getReplicationFactor();
        ConsistencyLevel writeCL = config.getDefaultWriteConsistency();

        return ReplicationMetrics.builder()
            .replicationFactor(replicationFactor)
            .writeConsistency(writeCL)
            .networkOverhead(calculateReplicationOverhead(replicationFactor, writeCL))
            .build();
    }

    private double calculateReplicationOverhead(int rf, ConsistencyLevel cl) {
        // Overhead depends on RF and consistency level
        double baseOverhead = rf - 1.0; // Base replication overhead

        switch (cl) {
            case ANY: return 0.1; // Minimal overhead
            case ONE: return 0.5;
            case QUORUM: return baseOverhead * 0.8;
            case ALL: return baseOverhead;
            default: return baseOverhead;
        }
    }
}

@Builder
class WritePerformanceProfile {
    private CommitLogMetrics commitLog;
    private MemtableMetrics memtable;
    private FlushMetrics flushRate;
    private CompactionMetrics compaction;
    private ReplicationMetrics replication;
}
```

### Performance Trade-offs

#### CAP Theorem в Cassandra
```java
public class CAPTradeoffAnalyzer {

    public CAPAnalysis analyzeDeployment() {
        return CAPAnalysis.builder()
            .consistency(measureConsistency())
            .availability(measureAvailability())
            .partitionTolerance(measurePartitionTolerance())
            .currentTuning(getCurrentTuning())
            .recommendations(generateRecommendations())
            .build();
    }

    private ConsistencyLevel measureConsistency() {
        // Измерение уровня консистентности
        double averageStaleness = metrics.getAverageDataStaleness();
        double consistencyViolations = metrics.getConsistencyViolations();

        if (consistencyViolations < 0.01 && averageStaleness < 100) { // ms
            return ConsistencyLevel.STRONG;
        } else if (consistencyViolations < 0.1) {
            return ConsistencyLevel.EVENTUAL;
        } else {
            return ConsistencyLevel.WEAK;
        }
    }

    private double measureAvailability() {
        // Измерение доступности
        double uptimePercentage = metrics.getUptimePercentage();
        long averageDowntime = metrics.getAverageDowntime();

        return uptimePercentage / 100.0;
    }

    private double measurePartitionTolerance() {
        // Измерение устойчивости к разделению
        int networkPartitions = metrics.getNetworkPartitions();
        long partitionRecoveryTime = metrics.getPartitionRecoveryTime();

        // Cassandra обычно имеет высокий partition tolerance
        return 0.95; // 95% tolerance
    }

    private CurrentTuning getCurrentTuning() {
        return CurrentTuning.builder()
            .readConsistency(config.getReadConsistency())
            .writeConsistency(config.getWriteConsistency())
            .replicationFactor(keyspace.getReplicationFactor())
            .tuningProfile(determineTuningProfile())
            .build();
    }

    private TuningProfile determineTuningProfile() {
        if (config.getReadConsistency() == ConsistencyLevel.ALL) {
            return TuningProfile.CONSISTENCY_PRIORITY;
        } else if (config.getWriteConsistency() == ConsistencyLevel.ANY) {
            return TuningProfile.AVAILABILITY_PRIORITY;
        } else {
            return TuningProfile.BALANCED;
        }
    }

    private List<String> generateRecommendations() {
        List<String> recommendations = new ArrayList<>();

        if (measureConsistency() == ConsistencyLevel.WEAK) {
            recommendations.add("Consider increasing read/write consistency levels");
            recommendations.add("Implement read repair for better consistency");
        }

        if (measureAvailability() < 0.999) { // Less than 99.9% uptime
            recommendations.add("Add more replica nodes for higher availability");
            recommendations.add("Implement proper failure detection and recovery");
        }

        if (metrics.getAverageLatency() > 50) { // ms
            recommendations.add("Optimize data model for better locality");
            recommendations.add("Consider adding more nodes or upgrading hardware");
        }

        return recommendations;
    }
}

enum TuningProfile {
    CONSISTENCY_PRIORITY,
    AVAILABILITY_PRIORITY,
    LOW_LATENCY_PRIORITY,
    BALANCED
}
```

## Оптимизация JVM

### JVM Memory Tuning

#### Heap Size Configuration
```bash
# Оптимальная конфигурация heap
# cassandra-env.sh

# Для серверов с 64GB+ RAM
# Heap = min(32GB, RAM/2)
# Young gen = Heap/4

# Максимальный heap
MAX_HEAP_SIZE="32G"

# Минимальный heap (равен максимальному для предотвращения resizing)
HEAP_NEWSIZE="8G"

# Young generation
JVM_OPTS="$JVM_OPTS -Xmn8G"

# Survivor ratio
JVM_OPTS="$JVM_OPTS -XX:SurvivorRatio=4"

# Максимальный размер tenured generation
JVM_OPTS="$JVM_OPTS -XX:MaxTenuringThreshold=1"
```

#### Garbage Collection Tuning
```bash
# G1GC для Cassandra 3.0+
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:G1RSetUpdatingPauseTimePercent=5"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=500"

# CMS GC (legacy, для Cassandra < 3.0)
JVM_OPTS="$JVM_OPTS -XX:+UseParNewGC"
JVM_OPTS="$JVM_OPTS -XX:+UseConcMarkSweepGC"
JVM_OPTS="$JVM_OPTS -XX:+CMSParallelRemarkEnabled"
JVM_OPTS="$JVM_OPTS -XX:SurvivorRatio=8"
JVM_OPTS="$JVM_OPTS -XX:MaxTenuringThreshold=1"
JVM_OPTS="$JVM_OPTS -XX:CMSInitiatingOccupancyFraction=75"
JVM_OPTS="$JVM_OPTS -XX:+UseCMSInitiatingOccupancyOnly"

# GC logging
JVM_OPTS="$JVM_OPTS -XX:+PrintGCDetails"
JVM_OPTS="$JVM_OPTS -XX:+PrintGCDateStamps"
JVM_OPTS="$JVM_OPTS -XX:+PrintHeapAtGC"
JVM_OPTS="$JVM_OPTS -XX:+PrintTenuringDistribution"
JVM_OPTS="$JVM_OPTS -XX:+PrintGCApplicationStoppedTime"
JVM_OPTS="$JVM_OPTS -Xloggc:/var/log/cassandra/gc.log"
JVM_OPTS="$JVM_OPTS -XX:+UseGCLogFileRotation"
JVM_OPTS="$JVM_OPTS -XX:NumberOfGCLogFiles=10"
JVM_OPTS="$JVM_OPTS -XX:GCLogFileSize=10M"
```

#### Off-heap Memory
```java
@Service
public class OffHeapMemoryManager {

    // Управление off-heap памятью
    public OffHeapConfig optimizeOffHeapMemory() {
        long totalSystemMemory = getTotalSystemMemory();
        long heapSize = config.getHeapSize();

        // Off-heap = System RAM - Heap - OS reserve
        long availableOffHeap = totalSystemMemory - heapSize - (4L * 1024 * 1024 * 1024); // 4GB for OS

        return OffHeapConfig.builder()
            .pageCache(calculatePageCache(availableOffHeap))
            .fileCache(calculateFileCache(availableOffHeap))
            .memtable(calculateMemtableOffHeap(availableOffHeap))
            .bloomFilters(calculateBloomFilters(availableOffHeap))
            .build();
    }

    private long calculatePageCache(long availableMemory) {
        // 1/3 of available memory for page cache
        return availableMemory / 3;
    }

    private long calculateFileCache(long availableMemory) {
        // 1/4 of available memory for file cache
        return availableMemory / 4;
    }

    private long calculateMemtableOffHeap(long availableMemory) {
        // 1/6 of available memory for off-heap memtables
        return availableMemory / 6;
    }

    private long calculateBloomFilters(long availableMemory) {
        // 1/12 of available memory for bloom filters
        return availableMemory / 12;
    }

    // Конфигурация Cassandra для off-heap
    public Map<String, String> generateCassandraConfig(OffHeapConfig offHeapConfig) {
        Map<String, String> config = new HashMap<>();

        // File cache
        config.put("file_cache_size_in_mb", String.valueOf(offHeapConfig.getFileCache() / 1024 / 1024));

        // Memory settings
        config.put("memtable_heap_space_in_mb", "2048"); // 2GB heap memtable
        config.put("memtable_offheap_space_in_mb", String.valueOf(offHeapConfig.getMemtable() / 1024 / 1024));

        return config;
    }
}

@Builder
class OffHeapConfig {
    private long pageCache;
    private long fileCache;
    private long memtable;
    private long bloomFilters;
}
```

### JVM Diagnostic Tools

#### Thread Dump Analysis
```bash
# Создание thread dump
kill -3 <cassandra_pid>

# Или с помощью jstack
jstack -l <cassandra_pid> > thread_dump.txt

# Анализ thread dump
# Поиск:
# - BLOCKED threads
# - WAITING threads
# - TIMED_WAITING threads
# - RUNNABLE threads в suspicious states
```

#### Heap Dump Analysis
```bash
# Создание heap dump
jmap -dump:format=b,file=heap.hprof <cassandra_pid>

# Анализ с помощью Eclipse MAT или VisualVM
# Поиск:
# - Memory leaks
# - Large objects
# - Garbage collection roots
# - Dominator tree
```

#### JFR (Java Flight Recorder)
```bash
# Включение JFR
JVM_OPTS="$JVM_OPTS -XX:+UnlockCommercialFeatures"
JVM_OPTS="$JVM_OPTS -XX:+FlightRecorder"
JVM_OPTS="$JVM_OPTS -XX:StartFlightRecording=duration=60s,filename=recording.jfr"

# Создание recording
jcmd <cassandra_pid> JFR.start duration=60s filename=recording.jfr

# Анализ recording в Java Mission Control
# Метрики производительности, memory usage, hotspots
```

## Тюнинг операционной системы

### Linux Kernel Tuning

#### Network Optimization
```bash
# /etc/sysctl.conf

# Network buffer sizes
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216
net.core.rmem_default = 4194304
net.core.wmem_default = 4194304

# TCP buffer sizes
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216

# TCP congestion control
net.ipv4.tcp_congestion_control = cubic

# TCP keepalive
net.ipv4.tcp_keepalive_time = 60
net.ipv4.tcp_keepalive_intvl = 10
net.ipv4.tcp_keepalive_probes = 6

# Connection tracking
net.nf_conntrack_max = 1048576

# Apply settings
sysctl -p
```

#### Disk I/O Optimization
```bash
# I/O scheduler (для SSD)
echo 'deadline' > /sys/block/sda/queue/scheduler

# Read-ahead
blockdev --setra 2048 /dev/sda

# I/O limits
echo 1024 > /sys/block/sda/queue/nr_requests
echo 1024 > /sys/block/sda/queue/read_ahead_kb

# Disable transparent huge pages (THP)
echo never > /sys/kernel/mm/transparent_hugepage/enabled
echo never > /sys/kernel/mm/transparent_hugepage/defrag

# Virtual memory
echo 1 > /proc/sys/vm/overcommit_memory
echo 0 > /proc/sys/vm/overcommit_ratio
```

#### File System Tuning
```bash
# Для XFS (рекомендуется для Cassandra)
mkfs.xfs -f -d su=64k,sw=8 -l size=128m /dev/sda

# Mount options
UUID=... /var/lib/cassandra xfs noatime,nodiratime,nobarrier,logbufs=8,logbsize=256k 0 0

# Для EXT4
mkfs.ext4 -T largefile -O extent,uninit_bg /dev/sda
tune2fs -o journal_data_writeback /dev/sda
tune2fs -O ^has_journal /dev/sda

# Mount options
UUID=... /var/lib/cassandra ext4 noatime,nodiratime,nobarrier,data=writeback 0 0
```

### Resource Limits

#### System Limits
```bash
# /etc/security/limits.conf

# Cassandra user limits
cassandra soft nofile 1048576
cassandra hard nofile 1048576
cassandra soft nproc 32768
cassandra hard nproc 32768
cassandra soft as unlimited
cassandra hard as unlimited
cassandra soft memlock unlimited
cassandra hard memlock unlimited

# Root limits (for maintenance)
root soft nofile 1048576
root hard nofile 1048576
```

#### Cassandra User Setup
```bash
# Создание пользователя cassandra
useradd -r -m -d /var/lib/cassandra -s /bin/bash cassandra

# Настройка директорий
mkdir -p /var/lib/cassandra/{data,commitlog,saved_caches,hints}
chown -R cassandra:cassandra /var/lib/cassandra
chmod 750 /var/lib/cassandra

# Настройка логов
mkdir -p /var/log/cassandra
chown -R cassandra:cassandra /var/log/cassandra
```

### CPU Affinity and NUMA

#### CPU Pinning
```bash
# CPU affinity для JVM
JVM_OPTS="$JVM_OPTS -XX:UseNUMA"
JVM_OPTS="$JVM_OPTS -XX:+UseCondCardMark"

# Taskset для привязки процесса
taskset -c 0-15 cassandra

# Для систем с NUMA
numactl --interleave=all cassandra
numactl --cpunodebind=0 --membind=0 cassandra
```

## Оптимизация запросов

### Query Pattern Analysis

#### Identifying Slow Queries
```java
@Service
public class QueryPerformanceAnalyzer {

    @Autowired
    private QueryLogger queryLogger;

    @Autowired
    private SchemaAnalyzer schemaAnalyzer;

    public List<QueryPerformanceIssue> analyzeSlowQueries() {
        List<QueryLogEntry> slowQueries = queryLogger.getSlowQueries(1000); // > 1s
        List<QueryPerformanceIssue> issues = new ArrayList<>();

        for (QueryLogEntry query : slowQueries) {
            QueryPerformanceIssue issue = analyzeQuery(query);
            if (issue != null) {
                issues.add(issue);
            }
        }

        return issues.stream()
            .sorted(Comparator.comparing(QueryPerformanceIssue::getImpactScore).reversed())
            .collect(Collectors.toList());
    }

    private QueryPerformanceIssue analyzeQuery(QueryLogEntry query) {
        CQLStatement statement = parseQuery(query.getQuery());

        // Анализ паттерна запроса
        QueryPattern pattern = identifyQueryPattern(statement);

        // Проверка на anti-patterns
        List<String> antiPatterns = detectAntiPatterns(statement);

        // Оценка производительности
        PerformanceMetrics metrics = measureQueryPerformance(statement);

        // Генерация рекомендаций
        List<String> recommendations = generateRecommendations(statement, pattern, antiPatterns);

        if (!recommendations.isEmpty()) {
            return QueryPerformanceIssue.builder()
                .query(query.getQuery())
                .executionTime(query.getExecutionTime())
                .pattern(pattern)
                .antiPatterns(antiPatterns)
                .metrics(metrics)
                .recommendations(recommendations)
                .impactScore(calculateImpactScore(query, antiPatterns.size()))
                .build();
        }

        return null;
    }

    private QueryPattern identifyQueryPattern(CQLStatement statement) {
        if (statement instanceof SelectStatement) {
            SelectStatement select = (SelectStatement) statement;

            if (select.getWhereClause() == null) {
                return QueryPattern.FULL_TABLE_SCAN;
            } else if (select.usesSecondaryIndex()) {
                return QueryPattern.SECONDARY_INDEX_QUERY;
            } else if (select.usesPartitionKeyOnly()) {
                return QueryPattern.PARTITION_KEY_QUERY;
            } else {
                return QueryPattern.CLUSTERING_KEY_QUERY;
            }
        }

        return QueryPattern.OTHER;
    }

    private List<String> detectAntiPatterns(CQLStatement statement) {
        List<String> antiPatterns = new ArrayList<>();

        if (statement.usesAllowFiltering()) {
            antiPatterns.add("ALLOW FILTERING used");
        }

        if (statement.hasInequalityOnClusteringKey()) {
            antiPatterns.add("Inequality on clustering key");
        }

        if (statement.hasLargeInClause()) {
            antiPatterns.add("Large IN clause (> 100 values)");
        }

        if (statement.selectsAllColumns()) {
            antiPatterns.add("SELECT * used");
        }

        return antiPatterns;
    }

    private List<String> generateRecommendations(CQLStatement statement,
                                              QueryPattern pattern,
                                              List<String> antiPatterns) {
        List<String> recommendations = new ArrayList<>();

        if (antiPatterns.contains("ALLOW FILTERING used")) {
            recommendations.add("Create secondary index or redesign data model");
        }

        if (pattern == QueryPattern.FULL_TABLE_SCAN) {
            recommendations.add("Add WHERE clause with partition key");
        }

        if (antiPatterns.contains("SELECT * used")) {
            recommendations.add("Select only required columns");
        }

        return recommendations;
    }

    private double calculateImpactScore(QueryLogEntry query, int antiPatternCount) {
        return query.getExecutionTime() * antiPatternCount * query.getFrequency();
    }
}

enum QueryPattern {
    FULL_TABLE_SCAN,
    PARTITION_KEY_QUERY,
    CLUSTERING_KEY_QUERY,
    SECONDARY_INDEX_QUERY,
    OTHER
}

@Builder
class QueryPerformanceIssue {
    private String query;
    private long executionTime;
    private QueryPattern pattern;
    private List<String> antiPatterns;
    private PerformanceMetrics metrics;
    private List<String> recommendations;
    private double impactScore;
}
```

### Query Optimization Techniques

#### Pagination Optimization
```java
@Service
public class QueryOptimizer {

    public OptimizedQuery optimizeSelectQuery(SelectStatement originalQuery) {
        OptimizedQuery optimized = new OptimizedQuery(originalQuery);

        // 1. Оптимизация LIMIT
        if (originalQuery.getLimit() > 1000) {
            optimized.addWarning("Large LIMIT may cause memory issues");
            optimized.suggestLimitReduction();
        }

        // 2. Оптимизация WHERE условий
        optimizeWhereClause(optimized);

        // 3. Оптимизация ORDER BY
        optimizeOrderBy(optimized);

        // 4. Добавление индексов если необходимо
        suggestIndexes(optimized);

        return optimized;
    }

    private void optimizeWhereClause(OptimizedQuery query) {
        SelectStatement select = query.getOriginalQuery();

        // Проверка использования partition key
        if (!select.usesPartitionKey()) {
            query.addCriticalIssue("Query does not use partition key - will scan all partitions");
            query.suggestPartitionKeyUsage();
        }

        // Проверка ALLOW FILTERING
        if (select.usesAllowFiltering()) {
            query.addWarning("ALLOW FILTERING used - consider data model redesign");
            query.suggestFilteringAlternative();
        }

        // Проверка selectivity
        double selectivity = calculateSelectivity(select);
        if (selectivity > 0.1) { // > 10% of data
            query.addWarning("Low selectivity query - consider adding indexes");
        }
    }

    private void optimizeOrderBy(OptimizedQuery query) {
        SelectStatement select = query.getOriginalQuery();

        if (select.hasOrderBy()) {
            // Проверка соответствия clustering order
            if (!matchesClusteringOrder(select)) {
                query.addWarning("ORDER BY does not match clustering order - requires sorting");
                query.suggestOrderByRemoval();
            }
        }
    }

    private void suggestIndexes(OptimizedQuery query) {
        // Анализ потребностей в индексах
        List<String> suggestedIndexes = new ArrayList<>();

        // SASI indexes for text search
        if (query.hasTextSearch()) {
            suggestedIndexes.add("SASI index for text search");
        }

        // Secondary indexes for selective queries
        if (query.hasSelectiveNonKeyFilter()) {
            suggestedIndexes.add("Secondary index for selective queries");
        }

        query.setSuggestedIndexes(suggestedIndexes);
    }

    private double calculateSelectivity(SelectStatement select) {
        // Оценка селективности запроса
        // Это упрощенная реализация
        return 0.05; // 5% selectivity as example
    }

    private boolean matchesClusteringOrder(SelectStatement select) {
        // Проверка соответствия ORDER BY clustering key order
        return true; // Simplified
    }
}

class OptimizedQuery {
    private SelectStatement originalQuery;
    private List<String> warnings;
    private List<String> criticalIssues;
    private List<String> suggestions;
    private List<String> suggestedIndexes;

    public OptimizedQuery(SelectStatement originalQuery) {
        this.originalQuery = originalQuery;
        this.warnings = new ArrayList<>();
        this.criticalIssues = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.suggestedIndexes = new ArrayList<>();
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addCriticalIssue(String issue) {
        criticalIssues.add(issue);
    }

    // other methods...
}
```

## Индексы и кэширование

### Index Strategy Optimization

#### Choosing Index Types
```java
@Service
public class IndexStrategyOptimizer {

    public IndexRecommendation analyzeAndRecommendIndexes(String keyspace, String table) {
        TableMetadata tableMetadata = schemaManager.getTableMetadata(keyspace, table);
        QueryPatterns patterns = queryAnalyzer.analyzeQueryPatterns(keyspace, table);

        return IndexRecommendation.builder()
            .tableName(table)
            .currentIndexes(getCurrentIndexes(tableMetadata))
            .recommendedIndexes(generateIndexRecommendations(patterns))
            .performanceImpact(calculatePerformanceImpact(patterns))
            .storageImpact(calculateStorageImpact())
            .build();
    }

    private List<String> generateIndexRecommendations(QueryPatterns patterns) {
        List<String> recommendations = new ArrayList<>();

        // Secondary indexes for selective queries
        for (String column : patterns.getSelectiveColumns()) {
            if (!hasExistingIndex(column)) {
                recommendations.add("Secondary index on column: " + column);
            }
        }

        // SASI indexes for text search
        for (String textColumn : patterns.getTextSearchColumns()) {
            recommendations.add("SASI index for text search on: " + textColumn);
        }

        // Composite indexes for multi-column queries
        for (List<String> columnSet : patterns.getMultiColumnQueries()) {
            if (columnSet.size() > 1) {
                recommendations.add("Composite index on columns: " + String.join(", ", columnSet));
            }
        }

        return recommendations;
    }

    private PerformanceImpact calculatePerformanceImpact(QueryPatterns patterns) {
        double readImprovement = calculateReadImprovement(patterns);
        double writeDegradation = calculateWriteDegradation(patterns);

        return PerformanceImpact.builder()
            .readLatencyImprovement(readImprovement)
            .writeLatencyDegradation(writeDegradation)
            .netBenefit(readImprovement - writeDegradation)
            .build();
    }

    private double calculateReadImprovement(QueryPatterns patterns) {
        // Оценка улучшения скорости чтения
        double improvement = 0.0;

        for (String selectiveColumn : patterns.getSelectiveColumns()) {
            double selectivity = patterns.getColumnSelectivity(selectiveColumn);
            improvement += (1.0 - selectivity) * 0.8; // 80% improvement for selective queries
        }

        return Math.min(improvement, 0.9); // Max 90% improvement
    }

    private double calculateWriteDegradation(QueryPatterns patterns) {
        // Оценка degradation скорости записи
        int indexCount = patterns.getRecommendedIndexCount();
        return indexCount * 0.05; // 5% degradation per index
    }

    private StorageImpact calculateStorageImpact() {
        // Оценка влияния на хранилище
        return StorageImpact.builder()
            .indexSizeMultiplier(1.2) // Indexes typically use 20% more space
            .memoryOverhead(calculateMemoryOverhead())
            .build();
    }

    private long calculateMemoryOverhead() {
        // Оценка overhead на память
        return 256 * 1024 * 1024; // 256MB as example
    }

    private boolean hasExistingIndex(String column) {
        // Проверка существования индекса
        return false; // Simplified
    }
}

@Builder
class IndexRecommendation {
    private String tableName;
    private List<String> currentIndexes;
    private List<String> recommendedIndexes;
    private PerformanceImpact performanceImpact;
    private StorageImpact storageImpact;
}

@Builder
class PerformanceImpact {
    private double readLatencyImprovement;
    private double writeLatencyDegradation;
    private double netBenefit;
}

@Builder
class StorageImpact {
    private double indexSizeMultiplier;
    private long memoryOverhead;
}
```

### Caching Strategies

#### Row Cache Configuration
```cql
-- Включение row cache для часто читаемых данных
ALTER TABLE frequently_read_table
WITH caching = {
    'keys': 'ALL',
    'rows_per_partition': '100'
};

-- Конфигурация через cassandra.yaml
row_cache_size_in_mb: 1024
row_cache_save_period: 0
row_cache_keys_to_save: 100
```

#### Key Cache Optimization
```yaml
# cassandra.yaml
key_cache_size_in_mb: 512
key_cache_save_period: 14400  # 4 hours
key_cache_keys_to_save: 100000

# Counter cache
counter_cache_size_in_mb: 128
counter_cache_save_period: 7200  # 2 hours
counter_cache_keys_to_save: 10000
```

#### Custom Caching Strategy
```java
@Service
public class SmartCacheManager {

    @Autowired
    private CacheMetricsCollector metrics;

    @Autowired
    private QueryPatternAnalyzer queryAnalyzer;

    public CacheConfiguration optimizeCacheConfiguration() {
        Map<String, QueryMetrics> queryMetrics = metrics.getQueryMetricsByTable();

        return CacheConfiguration.builder()
            .keyCache(optimizeKeyCache(queryMetrics))
            .rowCache(optimizeRowCache(queryMetrics))
            .counterCache(optimizeCounterCache(queryMetrics))
            .build();
    }

    private KeyCacheConfig optimizeKeyCache(Map<String, QueryMetrics> queryMetrics) {
        long totalKeyLookups = queryMetrics.values().stream()
            .mapToLong(QueryMetrics::getKeyLookups)
            .sum();

        long totalKeyHits = queryMetrics.values().stream()
            .mapToLong(QueryMetrics::getKeyHits)
            .sum();

        double hitRate = totalKeyHits / (double) totalKeyLookups;

        // Размер кэша на основе hit rate
        long recommendedSize = calculateRecommendedKeyCacheSize(hitRate, totalKeyLookups);

        return KeyCacheConfig.builder()
            .sizeInMB(recommendedSize)
            .savePeriod(4 * 3600) // 4 hours
            .keysToSave(Math.min(totalKeyLookups / 100, 100000))
            .build();
    }

    private RowCacheConfig optimizeRowCache(Map<String, QueryMetrics> queryMetrics) {
        // Анализ таблиц с высокой частотой чтения одних и тех же строк
        List<String> hotTables = queryMetrics.entrySet().stream()
            .filter(entry -> entry.getValue().getRowsPerPartition() < 10)
            .filter(entry -> entry.getValue().getReadFrequency() > 1000) // reads per minute
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return RowCacheConfig.builder()
            .enabled(!hotTables.isEmpty())
            .tables(hotTables)
            .rowsPerPartition(50)
            .build();
    }

    private CounterCacheConfig optimizeCounterCache(Map<String, QueryMetrics> queryMetrics) {
        // Counter cache для таблиц с счетчиками
        List<String> counterTables = queryMetrics.entrySet().stream()
            .filter(entry -> entry.getValue().isCounterTable())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return CounterCacheConfig.builder()
            .enabled(!counterTables.isEmpty())
            .sizeInMB(counterTables.size() * 64) // 64MB per counter table
            .savePeriod(2 * 3600) // 2 hours
            .build();
    }

    private long calculateRecommendedKeyCacheSize(double hitRate, long totalLookups) {
        // Рекомендуемый размер на основе hit rate и нагрузки
        if (hitRate > 0.9) {
            return 256; // Small cache for high hit rate
        } else if (hitRate > 0.7) {
            return 512; // Medium cache
        } else {
            return 1024; // Large cache for low hit rate
        }
    }
}
```

## Оптимизация хранения

### Compaction Strategy Selection

#### Choosing Compaction Strategy
```java
@Service
public class CompactionStrategyOptimizer {

    public CompactionRecommendation analyzeAndRecommend(String keyspace, String table) {
        TableMetadata tableInfo = schemaManager.getTableMetadata(keyspace, table);
        WorkloadPattern workload = analyzeWorkloadPattern(tableInfo);

        return CompactionRecommendation.builder()
            .tableName(table)
            .currentStrategy(tableInfo.getCompactionStrategy())
            .recommendedStrategy(recommendStrategy(workload))
            .configuration(generateConfiguration(workload))
            .performanceImpact(calculatePerformanceImpact(workload))
            .build();
    }

    private WorkloadPattern analyzeWorkloadPattern(TableMetadata tableInfo) {
        long readLoad = metrics.getReadLoad(tableInfo.getName());
        long writeLoad = metrics.getWriteLoad(tableInfo.getName());
        double readToWriteRatio = (double) readLoad / writeLoad;

        long dataSize = tableInfo.getDataSize();
        long compactionHistory = metrics.getCompactionHistory(tableInfo.getName());

        return WorkloadPattern.builder()
            .readToWriteRatio(readToWriteRatio)
            .dataSize(dataSize)
            .compactionFrequency(compactionHistory)
            .ttlUsage(hasTTLColumns(tableInfo))
            .updatePattern(detectUpdatePattern(tableInfo))
            .build();
    }

    private CompactionStrategy recommendStrategy(WorkloadPattern workload) {
        if (workload.isTimeSeriesData()) {
            return CompactionStrategy.TIME_WINDOW_COMPACTION;
        } else if (workload.getReadToWriteRatio() > 5.0) {
            return CompactionStrategy.LEVELED_COMPACTION;
        } else if (workload.hasHighWriteLoad()) {
            return CompactionStrategy.SIZE_TIERED_COMPACTION;
        } else {
            return CompactionStrategy.LEVELED_COMPACTION; // Default
        }
    }

    private Map<String, Object> generateConfiguration(WorkloadPattern workload) {
        Map<String, Object> config = new HashMap<>();

        CompactionStrategy strategy = recommendStrategy(workload);

        switch (strategy) {
            case SIZE_TIERED_COMPACTION:
                config.put("min_threshold", 4);
                config.put("max_threshold", 32);
                config.put("bucket_high", 1.5);
                config.put("bucket_low", 0.5);
                break;

            case LEVELED_COMPACTION:
                config.put("sstable_size_in_mb", 160);
                break;

            case TIME_WINDOW_COMPACTION:
                config.put("compaction_window_unit", "DAYS");
                config.put("compaction_window_size", 1);
                config.put("max_sstable_age_days", 1);
                break;
        }

        return config;
    }

    private PerformanceImpact calculatePerformanceImpact(WorkloadPattern workload) {
        CompactionStrategy current = workload.getCurrentStrategy();
        CompactionStrategy recommended = recommendStrategy(workload);

        double readImpact = calculateReadImpact(current, recommended);
        double writeImpact = calculateWriteImpact(current, recommended);
        double spaceImpact = calculateSpaceImpact(current, recommended);

        return PerformanceImpact.builder()
            .readLatencyImpact(readImpact)
            .writeLatencyImpact(writeImpact)
            .spaceUsageImpact(spaceImpact)
            .build();
    }

    private double calculateReadImpact(CompactionStrategy current, CompactionStrategy recommended) {
        // LCS обычно лучше для чтения, STCS лучше для записи
        if (current == CompactionStrategy.LEVELED_COMPACTION &&
            recommended == CompactionStrategy.SIZE_TIERED_COMPACTION) {
            return 0.15; // 15% degradation
        } else if (current == CompactionStrategy.SIZE_TIERED_COMPACTION &&
                   recommended == CompactionStrategy.LEVELED_COMPACTION) {
            return -0.10; // 10% improvement
        }
        return 0.0;
    }

    private double calculateWriteImpact(CompactionStrategy current, CompactionStrategy recommended) {
        // STCS лучше для записи, LCS имеет write amplification
        if (current == CompactionStrategy.LEVELED_COMPACTION &&
            recommended == CompactionStrategy.SIZE_TIERED_COMPACTION) {
            return -0.20; // 20% improvement
        } else if (current == CompactionStrategy.SIZE_TIERED_COMPACTION &&
                   recommended == CompactionStrategy.LEVELED_COMPACTION) {
            return 0.25; // 25% degradation
        }
        return 0.0;
    }

    private double calculateSpaceImpact(CompactionStrategy current, CompactionStrategy recommended) {
        // LCS использует меньше места, STCS имеет space amplification
        if (current == CompactionStrategy.SIZE_TIERED_COMPACTION &&
            recommended == CompactionStrategy.LEVELED_COMPACTION) {
            return -0.30; // 30% space saving
        }
        return 0.0;
    }
}

enum CompactionStrategy {
    SIZE_TIERED_COMPACTION,
    LEVELED_COMPACTION,
    TIME_WINDOW_COMPACTION
}

@Builder
class CompactionRecommendation {
    private String tableName;
    private CompactionStrategy currentStrategy;
    private CompactionStrategy recommendedStrategy;
    private Map<String, Object> configuration;
    private PerformanceImpact performanceImpact;
}
```

### Compression Tuning

#### Compression Configuration
```cql
-- Оптимизация сжатия для разных типов данных
ALTER TABLE time_series_data WITH compression = {
    'class': 'LZ4Compressor',
    'chunk_length_in_kb': 64
};

ALTER TABLE text_data WITH compression = {
    'class': 'SnappyCompressor',
    'chunk_length_in_kb': 32
};

ALTER TABLE binary_data WITH compression = {
    'class': 'DeflateCompressor',
    'chunk_length_in_kb': 128
};
```

## Масштабирование производительности

### Horizontal Scaling

#### Cluster Expansion Planning
```java
@Service
public class ClusterScalingPlanner {

    @Autowired
    private ClusterMetrics metrics;

    @Autowired
    private CapacityPlanner capacityPlanner;

    public ScalingPlan createScalingPlan() {
        CurrentCapacity current = metrics.getCurrentCapacity();
        FutureRequirements future = capacityPlanner.predictRequirements();

        List<ScalingAction> actions = new ArrayList<>();

        // Анализ CPU utilization
        if (current.getCpuUtilization() > 80.0) {
            int nodesNeeded = calculateNodesForCpu(future.getRequiredCpu());
            actions.add(new ScalingAction(ScalingActionType.ADD_NODES, nodesNeeded));
        }

        // Анализ memory usage
        if (current.getMemoryUtilization() > 85.0) {
            int nodesNeeded = calculateNodesForMemory(future.getRequiredMemory());
            actions.add(new ScalingAction(ScalingActionType.ADD_NODES, nodesNeeded));
        }

        // Анализ disk space
        if (current.getDiskUtilization() > 75.0) {
            actions.add(new ScalingAction(ScalingActionType.EXPAND_STORAGE, 0));
        }

        // Анализ network throughput
        if (current.getNetworkUtilization() > 70.0) {
            actions.add(new ScalingAction(ScalingActionType.ADD_DATACENTER, 1));
        }

        return ScalingPlan.builder()
            .currentCapacity(current)
            .futureRequirements(future)
            .actions(actions)
            .estimatedCost(calculateCost(actions))
            .timeline(createTimeline(actions))
            .build();
    }

    private int calculateNodesForCpu(double requiredCpu) {
        double currentTotalCpu = metrics.getTotalCpuCores();
        double avgCpuPerNode = metrics.getAverageCpuPerNode();

        int currentNodes = metrics.getNodeCount();
        int requiredNodes = (int) Math.ceil(requiredCpu / avgCpuPerNode);

        return Math.max(0, requiredNodes - currentNodes);
    }

    private int calculateNodesForMemory(long requiredMemory) {
        long currentTotalMemory = metrics.getTotalMemory();
        long avgMemoryPerNode = metrics.getAverageMemoryPerNode();

        int currentNodes = metrics.getNodeCount();
        int requiredNodes = (int) Math.ceil((double) requiredMemory / avgMemoryPerNode);

        return Math.max(0, requiredNodes - currentNodes);
    }

    private BigDecimal calculateCost(List<ScalingAction> actions) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (ScalingAction action : actions) {
            switch (action.getType()) {
                case ADD_NODES:
                    totalCost = totalCost.add(calculateNodeCost(action.getCount()));
                    break;
                case EXPAND_STORAGE:
                    totalCost = totalCost.add(calculateStorageCost());
                    break;
                case ADD_DATACENTER:
                    totalCost = totalCost.add(calculateDataCenterCost(action.getCount()));
                    break;
            }
        }

        return totalCost;
    }

    private List<ScalingMilestone> createTimeline(List<ScalingAction> actions) {
        List<ScalingMilestone> timeline = new ArrayList<>();
        LocalDate startDate = LocalDate.now();

        for (ScalingAction action : actions) {
            ScalingMilestone milestone = ScalingMilestone.builder()
                .action(action)
                .plannedDate(startDate)
                .estimatedDuration(estimateDuration(action))
                .dependencies(identifyDependencies(action))
                .build();

            timeline.add(milestone);
            startDate = startDate.plusDays(estimateDuration(action).toDays());
        }

        return timeline;
    }

    private Period estimateDuration(ScalingAction action) {
        switch (action.getType()) {
            case ADD_NODES:
                return Period.ofDays(action.getCount() * 2); // 2 days per node
            case EXPAND_STORAGE:
                return Period.ofDays(7); // 1 week
            case ADD_DATACENTER:
                return Period.ofDays(30); // 1 month
            default:
                return Period.ofDays(1);
        }
    }

    private List<String> identifyDependencies(ScalingAction action) {
        // Определение зависимостей для действий
        return new ArrayList<>();
    }
}

enum ScalingActionType {
    ADD_NODES,
    EXPAND_STORAGE,
    ADD_DATACENTER,
    OPTIMIZE_CONFIG
}

class ScalingAction {
    private ScalingActionType type;
    private int count;

    // constructor, getters, setters
}
```

### Vertical Scaling

#### Node Upgrade Planning
```java
@Service
public class NodeUpgradePlanner {

    public NodeUpgradePlan planNodeUpgrade(String nodeAddress, HardwareUpgrade upgrade) {
        Node currentNode = clusterManager.getNode(nodeAddress);
        HardwareUpgrade cost = estimateUpgradeCost(currentNode, upgrade);

        return NodeUpgradePlan.builder()
            .nodeAddress(nodeAddress)
            .currentHardware(currentNode.getHardware())
            .targetHardware(upgrade)
            .estimatedDowntime(estimateDowntime(upgrade))
            .performanceImprovement(estimatePerformanceGain(currentNode, upgrade))
            .cost(cost)
            .rollbackPlan(createRollbackPlan(currentNode))
            .build();
    }

    private Duration estimateDowntime(HardwareUpgrade upgrade) {
        if (upgrade.hasMemoryUpgrade()) {
            return Duration.ofHours(4); // Memory hot-plug if supported
        } else if (upgrade.hasCpuUpgrade()) {
            return Duration.ofHours(24); // Full node restart
        } else if (upgrade.hasDiskUpgrade()) {
            return Duration.ofHours(12); // Disk replacement
        }
        return Duration.ofHours(2);
    }

    private PerformanceMetrics estimatePerformanceGain(Node currentNode, HardwareUpgrade upgrade) {
        double cpuGain = upgrade.getCpuCores() > currentNode.getCpuCores() ?
            (upgrade.getCpuCores() / (double) currentNode.getCpuCores() - 1) * 0.3 : 0;

        double memoryGain = upgrade.getMemoryGB() > currentNode.getMemoryGB() ?
            (upgrade.getMemoryGB() / (double) currentNode.getMemoryGB() - 1) * 0.2 : 0;

        double diskGain = upgrade.hasSSDNVMe() ? 0.5 : 0; // Significant I/O improvement

        return PerformanceMetrics.builder()
            .cpuImprovement(cpuGain)
            .memoryImprovement(memoryGain)
            .ioImprovement(diskGain)
            .overallImprovement(cpuGain + memoryGain + diskGain)
            .build();
    }

    private RollbackPlan createRollbackPlan(Node currentNode) {
        return RollbackPlan.builder()
            .backupConfig(true)
            .backupData(true)
            .originalHardware(currentNode.getHardware())
            .rollbackProcedure("Restore from backup and restart with original hardware")
            .estimatedRollbackTime(Duration.ofHours(6))
            .build();
    }
}

@Builder
class HardwareUpgrade {
    private int cpuCores;
    private int memoryGB;
    private boolean hasSSDNVMe;
    private long diskCapacityGB;

    // getters
}
```

## Мониторинг и метрики

### Key Performance Indicators

#### System Metrics
```java
@Service
public class PerformanceMetricsCollector {

    @Autowired
    private JmxClient jmxClient;

    @Autowired
    private OsMetricsCollector osMetrics;

    public SystemPerformanceMetrics collectSystemMetrics() {
        return SystemPerformanceMetrics.builder()
            .jvmMetrics(collectJvmMetrics())
            .osMetrics(collectOsMetrics())
            .cassandraMetrics(collectCassandraMetrics())
            .applicationMetrics(collectApplicationMetrics())
            .collectedAt(Instant.now())
            .build();
    }

    private JvmMetrics collectJvmMetrics() {
        return JvmMetrics.builder()
            .heapUsage(jmxClient.getHeapUsage())
            .nonHeapUsage(jmxClient.getNonHeapUsage())
            .gcStats(jmxClient.getGCStats())
            .threadCount(jmxClient.getThreadCount())
            .classCount(jmxClient.getLoadedClassCount())
            .uptime(jmxClient.getUptime())
            .build();
    }

    private OsMetrics collectOsMetrics() {
        return OsMetrics.builder()
            .cpuUtilization(osMetrics.getCpuUtilization())
            .memoryUtilization(osMetrics.getMemoryUtilization())
            .diskUtilization(osMetrics.getDiskUtilization())
            .networkUtilization(osMetrics.getNetworkUtilization())
            .loadAverage(osMetrics.getLoadAverage())
            .contextSwitches(osMetrics.getContextSwitches())
            .build();
    }

    private CassandraMetrics collectCassandraMetrics() {
        return CassandraMetrics.builder()
            .readLatency(jmxClient.getReadLatency())
            .writeLatency(jmxClient.getWriteLatency())
            .readThroughput(jmxClient.getReadThroughput())
            .writeThroughput(jmxClient.getWriteThroughput())
            .pendingCompactions(jmxClient.getPendingCompactions())
            .activeConnections(jmxClient.getActiveConnections())
            .keyCacheHitRate(jmxClient.getKeyCacheHitRate())
            .rowCacheHitRate(jmxClient.getRowCacheHitRate())
            .build();
    }

    private ApplicationMetrics collectApplicationMetrics() {
        // Метрики приложения
        return ApplicationMetrics.builder()
            .requestCount(metricsClient.getRequestCount())
            .errorRate(metricsClient.getErrorRate())
            .responseTime(metricsClient.getAverageResponseTime())
            .activeUsers(metricsClient.getActiveUsers())
            .build();
    }

    public List<PerformanceAlert> checkThresholds(SystemPerformanceMetrics metrics) {
        List<PerformanceAlert> alerts = new ArrayList<>();

        // JVM alerts
        if (metrics.getJvmMetrics().getHeapUsage() > 0.9) {
            alerts.add(PerformanceAlert.critical("JVM heap usage > 90%"));
        }

        // OS alerts
        if (metrics.getOsMetrics().getCpuUtilization() > 0.95) {
            alerts.add(PerformanceAlert.critical("CPU utilization > 95%"));
        }

        if (metrics.getOsMetrics().getMemoryUtilization() > 0.9) {
            alerts.add(PerformanceAlert.warning("Memory utilization > 90%"));
        }

        // Cassandra alerts
        if (metrics.getCassandraMetrics().getReadLatency() > 100000) { // 100ms
            alerts.add(PerformanceAlert.warning("Read latency > 100ms"));
        }

        if (metrics.getCassandraMetrics().getPendingCompactions() > 20) {
            alerts.add(PerformanceAlert.warning("Pending compactions > 20"));
        }

        return alerts;
    }
}

@Builder
class SystemPerformanceMetrics {
    private JvmMetrics jvmMetrics;
    private OsMetrics osMetrics;
    private CassandraMetrics cassandraMetrics;
    private ApplicationMetrics applicationMetrics;
    private Instant collectedAt;
}

enum AlertSeverity {
    INFO, WARNING, CRITICAL
}

@Builder
class PerformanceAlert {
    private String message;
    private AlertSeverity severity;
    private Instant timestamp;

    public static PerformanceAlert critical(String message) {
        return PerformanceAlert.builder()
            .message(message)
            .severity(AlertSeverity.CRITICAL)
            .timestamp(Instant.now())
            .build();
    }

    public static PerformanceAlert warning(String message) {
        return PerformanceAlert.builder()
            .message(message)
            .severity(AlertSeverity.WARNING)
            .timestamp(Instant.now())
            .build();
    }
}
```

## Инструменты профилирования

### Cassandra Stress Tool

#### Load Testing
```bash
# Простой write тест
cassandra-stress write n=1000000 -rate threads=50

# Mixed workload тест
cassandra-stress mixed ratio\\(write=1,read=3\\) n=1000000 -rate threads=100

# Custom тест с YAML конфигурацией
cassandra-stress user profile=./stress-profile.yaml n=500000 -rate threads=20

# Тест с различными консистентностями
cassandra-stress write n=100000 cl=QUORUM -rate threads=50
cassandra-stress read n=100000 cl=ONE -rate threads=50
```

#### Custom Stress Profile
```yaml
# stress-profile.yaml
keyspace: stress_keyspace

keyspace_definition: |
  CREATE KEYSPACE stress_keyspace WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
  };

table: stress_table

table_definition: |
  CREATE TABLE stress_table (
    id uuid,
    data text,
    PRIMARY KEY (id)
  );

columnspec:
  - name: id
    size: fixed(36)
    population: seq(1..1000000)
  - name: data
    size: uniform(64..1024)

insert:
  partitions: fixed(1)
  batchtype: UNLOGGED

queries:
  simple_read:
    cql: SELECT * FROM stress_table WHERE id = ?
    fields: samerow
```

#### Results Analysis
```java
@Service
public class StressTestAnalyzer {

    public StressTestReport analyzeStressTest(String stressOutput) {
        Map<String, Object> metrics = parseStressOutput(stressOutput);

        return StressTestReport.builder()
            .totalOperations((Long) metrics.get("total_operations"))
            .operationsPerSecond((Double) metrics.get("op_rate"))
            .averageLatency((Double) metrics.get("avg_latency"))
            .p95Latency((Double) metrics.get("p95_latency"))
            .p99Latency((Double) metrics.get("p99_latency"))
            .errors((Integer) metrics.get("errors"))
            .performanceScore(calculatePerformanceScore(metrics))
            .bottlenecks(identifyBottlenecks(metrics))
            .recommendations(generateRecommendations(metrics))
            .build();
    }

    private Map<String, Object> parseStressOutput(String output) {
        Map<String, Object> metrics = new HashMap<>();

        // Парсинг вывода cassandra-stress
        Pattern opRatePattern = Pattern.compile("op rate\\s*:\\s*([\\d.]+)");
        Matcher matcher = opRatePattern.matcher(output);
        if (matcher.find()) {
            metrics.put("op_rate", Double.parseDouble(matcher.group(1)));
        }

        // Аналогично для других метрик
        return metrics;
    }

    private double calculatePerformanceScore(Map<String, Object> metrics) {
        double opRate = (Double) metrics.get("op_rate");
        double avgLatency = (Double) metrics.get("avg_latency");
        int errors = (Integer) metrics.get("errors");

        // Нормализация и расчет скора
        double rateScore = Math.min(opRate / 10000.0, 1.0); // Max 10k ops/s = 1.0
        double latencyScore = Math.max(0, 1.0 - (avgLatency / 100.0)); // Lower latency = higher score
        double errorScore = Math.max(0, 1.0 - (errors / 100.0)); // Lower errors = higher score

        return (rateScore * 0.5) + (latencyScore * 0.3) + (errorScore * 0.2);
    }

    private List<String> identifyBottlenecks(Map<String, Object> metrics) {
        List<String> bottlenecks = new ArrayList<>();

        double avgLatency = (Double) metrics.get("avg_latency");
        if (avgLatency > 50.0) {
            bottlenecks.add("High latency - possible I/O or network bottleneck");
        }

        double opRate = (Double) metrics.get("op_rate");
        if (opRate < 1000.0) {
            bottlenecks.add("Low throughput - possible CPU or memory bottleneck");
        }

        int errors = (Integer) metrics.get("errors");
        if (errors > 10) {
            bottlenecks.add("High error rate - check configuration and cluster health");
        }

        return bottlenecks;
    }

    private List<String> generateRecommendations(Map<String, Object> metrics) {
        List<String> recommendations = new ArrayList<>();

        if ((Double) metrics.get("avg_latency") > 20.0) {
            recommendations.add("Consider increasing consistency level trade-offs");
            recommendations.add("Optimize data model for better locality");
        }

        if ((Double) metrics.get("op_rate") < 5000.0) {
            recommendations.add("Add more nodes to the cluster");
            recommendations.add("Upgrade hardware (CPU, memory, SSD)");
        }

        return recommendations;
    }
}
```

### Performance Profiling Tools

#### Async Profiler
```bash
# Установка async-profiler
wget https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.9/async-profiler-2.9-linux-x64.tar.gz
tar xzf async-profiler-2.9-linux-x64.tar.gz

# Профилирование CPU
./profiler.sh -d 60 -f cpu_profile.html <cassandra_pid>

# Профилирование allocation
./profiler.sh -d 60 -e alloc -f alloc_profile.html <cassandra_pid>

# Профiliрование locks
./profiler.sh -d 60 -e lock -f lock_profile.html <cassandra_pid>
```

#### YourKit Java Profiler
```java
// Программная интеграция с profiler
public class ProfilingIntegration {

    private static volatile boolean profilingEnabled = false;

    public static void enableProfiling() {
        profilingEnabled = true;
        // Включение detailed logging
        System.setProperty("cassandra.logging.level", "DEBUG");
    }

    public static void disableProfiling() {
        profilingEnabled = false;
        // Возврат к нормальному уровню логирования
        System.setProperty("cassandra.logging.level", "INFO");
    }

    public static void takeSnapshot(String name) {
        if (profilingEnabled) {
            // Создание heap dump или CPU snapshot
            createProfilingSnapshot(name);
        }
    }

    private static void createProfilingSnapshot(String name) {
        try {
            // Использование HotSpot Diagnostic MBean
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanName = new ObjectName("com.sun.management:type=HotSpotDiagnostic");

            // Создание heap dump
            mbs.invoke(mxbeanName, "dumpHeap",
                new Object[]{name + ".hprof", Boolean.TRUE},
                new String[]{String.class.getName(), boolean.class.getName()});

        } catch (Exception e) {
            System.err.println("Failed to create profiling snapshot: " + e.getMessage());
        }
    }

    // AOP aspect для профилирования методов
    @Aspect
    public class PerformanceProfilingAspect {

        @Around("execution(* com.datastax.driver.core.*.*(..))")
        public Object profileCassandraDriverCalls(ProceedingJoinPoint joinPoint) throws Throwable {
            if (!profilingEnabled) {
                return joinPoint.proceed();
            }

            long startTime = System.nanoTime();
            try {
                return joinPoint.proceed();
            } finally {
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000; // to milliseconds

                if (duration > 100) { // Log slow calls > 100ms
                    System.out.println(String.format("Slow Cassandra call: %s took %d ms",
                        joinPoint.getSignature().toShortString(), duration));
                }
            }
        }
    }
}
```

## Устранение проблем производительности

### Common Performance Issues

#### Memory Issues
```java
@Service
public class MemoryIssueDiagnoser {

    @Autowired
    private JmxClient jmxClient;

    @Autowired
    private SystemMetricsCollector systemMetrics;

    public List<MemoryIssue> diagnoseMemoryIssues() {
        List<MemoryIssue> issues = new ArrayList<>();

        // Heap memory issues
        double heapUsage = jmxClient.getHeapUsage();
        if (heapUsage > 0.9) {
            issues.add(MemoryIssue.builder()
                .type(MemoryIssueType.HEAP_EXHAUSTION)
                .severity(Severity.CRITICAL)
                .description("Heap usage > 90%")
                .recommendedAction("Increase heap size or optimize memory usage")
                .build());
        }

        // GC pressure
        GCStats gcStats = jmxClient.getGCStats();
        if (gcStats.getGcTimeRatio() > 0.3) { // > 30% time spent in GC
            issues.add(MemoryIssue.builder()
                .type(MemoryIssueType.GC_OVERHEAD)
                .severity(Severity.HIGH)
                .description("GC overhead > 30%")
                .recommendedAction("Tune GC settings or reduce object allocation")
                .build());
        }

        // Off-heap memory
        long offHeapUsage = systemMetrics.getOffHeapMemoryUsage();
        long maxOffHeap = systemMetrics.getMaxOffHeapMemory();
        if (offHeapUsage > maxOffHeap * 0.8) {
            issues.add(MemoryIssue.builder()
                .type(MemoryIssueType.OFF_HEAP_EXHAUSTION)
                .severity(Severity.HIGH)
                .description("Off-heap memory usage > 80%")
                .recommendedAction("Increase system memory or reduce cache sizes")
                .build());
        }

        // Memory leaks
        if (detectMemoryLeak()) {
            issues.add(MemoryIssue.builder()
                .type(MemoryIssueType.MEMORY_LEAK)
                .severity(Severity.CRITICAL)
                .description("Memory leak detected")
                .recommendedAction("Analyze heap dumps and fix application code")
                .build());
        }

        return issues;
    }

    private boolean detectMemoryLeak() {
        // Простая эвристика для обнаружения memory leaks
        List<Double> heapHistory = jmxClient.getHeapUsageHistory();

        if (heapHistory.size() < 10) return false;

        // Проверка тренда роста heap usage
        double firstQuartile = heapHistory.subList(0, heapHistory.size() / 4)
            .stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double lastQuartile = heapHistory.subList(heapHistory.size() * 3 / 4, heapHistory.size())
            .stream().mapToDouble(Double::doubleValue).average().orElse(0);

        return lastQuartile > firstQuartile * 1.2; // 20% growth trend
    }
}

enum MemoryIssueType {
    HEAP_EXHAUSTION,
    GC_OVERHEAD,
    OFF_HEAP_EXHAUSTION,
    MEMORY_LEAK
}

enum Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Builder
class MemoryIssue {
    private MemoryIssueType type;
    private Severity severity;
    private String description;
    private String recommendedAction;
}
```

#### Disk I/O Issues
```java
@Service
public class DiskIOPerformanceDiagnoser {

    @Autowired
    private SystemMetricsCollector systemMetrics;

    @Autowired
    private CassandraMetricsCollector cassandraMetrics;

    public List<DiskIssue> diagnoseDiskIssues() {
        List<DiskIssue> issues = new ArrayList<>();

        // High I/O utilization
        double ioUtilization = systemMetrics.getDiskIOUtilization();
        if (ioUtilization > 0.9) {
            issues.add(DiskIssue.builder()
                .type(DiskIssueType.HIGH_IO_UTILIZATION)
                .severity(Severity.HIGH)
                .description("Disk I/O utilization > 90%")
                .recommendedAction("Add more disks, use SSD, or redistribute data")
                .build());
        }

        // High I/O wait
        double ioWait = systemMetrics.getIOWaitTime();
        if (ioWait > 20.0) { // > 20% iowait
            issues.add(DiskIssue.builder()
                .type(DiskIssueType.HIGH_IO_WAIT)
                .severity(Severity.MEDIUM)
                .description("I/O wait time > 20%")
                .recommendedAction("Optimize disk subsystem or reduce I/O load")
                .build());
        }

        // Compaction backlog
        int pendingCompactions = cassandraMetrics.getPendingCompactions();
        if (pendingCompactions > 50) {
            issues.add(DiskIssue.builder()
                .type(DiskIssueType.COMPACTION_BACKLOG)
                .severity(Severity.MEDIUM)
                .description("Pending compactions > 50")
                .recommendedAction("Increase compaction throughput or add nodes")
                .build());
        }

        // Slow disk operations
        double avgDiskLatency = systemMetrics.getAverageDiskLatency();
        if (avgDiskLatency > 10.0) { // > 10ms
            issues.add(DiskIssue.builder()
                .type(DiskIssueType.SLOW_DISK_OPERATIONS)
                .severity(Severity.HIGH)
                .description("Average disk latency > 10ms")
                .recommendedAction("Use faster storage (SSD/NVMe) or optimize workload")
                .build());
        }

        return issues;
    }

    public DiskOptimizationPlan createOptimizationPlan(List<DiskIssue> issues) {
        DiskOptimizationPlan plan = new DiskOptimizationPlan();

        for (DiskIssue issue : issues) {
            switch (issue.getType()) {
                case HIGH_IO_UTILIZATION:
                    plan.addRecommendation("Implement data partitioning across more disks");
                    plan.addRecommendation("Use RAID 0 or JBOD for Cassandra data");
                    break;

                case HIGH_IO_WAIT:
                    plan.addRecommendation("Switch to SSD storage");
                    plan.addRecommendation("Implement I/O scheduling optimizations");
                    break;

                case COMPACTION_BACKLOG:
                    plan.addRecommendation("Increase concurrent_compactors");
                    plan.addRecommendation("Use faster compaction strategy (STCS)");
                    break;

                case SLOW_DISK_OPERATIONS:
                    plan.addRecommendation("Upgrade to NVMe SSDs");
                    plan.addRecommendation("Implement disk controller optimizations");
                    break;
            }
        }

        return plan;
    }
}

enum DiskIssueType {
    HIGH_IO_UTILIZATION,
    HIGH_IO_WAIT,
    COMPACTION_BACKLOG,
    SLOW_DISK_OPERATIONS
}

class DiskOptimizationPlan {
    private List<String> recommendations = new ArrayList<>();

    public void addRecommendation(String recommendation) {
        recommendations.add(recommendation);
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
```

## Лучшие практики

### Performance Monitoring

#### 1. Key Metrics to Monitor
- **Latency**: Read/Write latency (target: < 10ms)
- **Throughput**: Operations per second (target: maximize)
- **Resource Utilization**: CPU, Memory, Disk, Network (< 80%)
- **Error Rates**: Application and system errors (< 1%)
- **Cache Hit Rates**: Key cache, Row cache (> 90%)
- **Compaction Statistics**: Pending tasks, throughput

#### 2. Alert Thresholds
- **Critical**: > 95% resource utilization, > 100ms latency
- **Warning**: > 80% resource utilization, > 50ms latency
- **Info**: > 60% resource utilization, > 20ms latency

### Capacity Planning

#### 1. Growth Projections
- **Data growth**: 30-50% annually
- **Traffic growth**: 20-40% annually
- **Hardware lifecycle**: 3-5 years
- **Buffer capacity**: 20-30% overhead

#### 2. Scaling Strategies
- **Horizontal scaling**: Add nodes gradually
- **Vertical scaling**: Upgrade existing hardware
- **Storage scaling**: Add disks/nodes as needed
- **Network scaling**: Upgrade network infrastructure

### Configuration Management

#### 1. Version Control
- **Configuration files** in Git
- **Change tracking** and rollback capability
- **Environment-specific** configurations
- **Automated deployment** of changes

#### 2. Testing Changes
- **Staging environment** for testing
- **Load testing** before production deployment
- **Gradual rollout** with monitoring
- **Rollback plans** for failed changes

### Troubleshooting Methodology

#### 1. Systematic Approach
1. **Define the problem** clearly
2. **Gather metrics** and logs
3. **Identify bottlenecks** using profiling tools
4. **Test hypotheses** with controlled changes
5. **Implement fixes** with monitoring
6. **Validate improvements** with benchmarks

#### 2. Common Tools
- **nodetool**: Status, statistics, maintenance
- **JMX**: Detailed metrics and management
- **OS tools**: iostat, iotop, vmstat, sar
- **Application logs**: Error patterns, slow queries
- **Third-party**: Prometheus, Grafana, DataDog

### Performance Culture

#### 1. Team Practices
- **Performance reviews** in development process
- **Load testing** as part of CI/CD
- **Performance budgets** for features
- **Shared responsibility** for performance

#### 2. Continuous Improvement
- **Regular benchmarking** and profiling
- **Performance trend analysis**
- **Technology evaluation** for improvements
- **Knowledge sharing** and training

## Заключение

Оптимизация производительности Apache Cassandra — это непрерывный процесс, требующий глубокого понимания архитектуры, тщательного мониторинга и систематического подхода к решению проблем. Ключевые аспекты успешной оптимизации:

### Архитектурные решения:

1. **Правильное моделирование данных** — основа производительности
2. **Оптимальные стратегии репликации** — баланс между консистентностью и производительностью
3. **Эффективные индексы** — SASI для текстового поиска, вторичные индексы для селективных запросов
4. **Кэширование** — key cache, row cache, off-heap memory

### Конфигурация и тюнинг:

1. **JVM оптимизация** — правильные heap размеры, GC настройки, off-heap memory
2. **OS тюнинг** — network buffers, disk I/O, CPU affinity, resource limits
3. **Cassandra настройки** — compaction strategies, compression, caching

### Мониторинг и диагностика:

1. **Комплексный мониторинг** — системные метрики, Cassandra метрики, application метрики
2. **Инструменты профилирования** — async-profiler, YourKit, Cassandra stress testing
3. **Регулярные аудиты** производительности и выявление узких мест

### Масштабирование:

1. **Горизонтальное масштабирование** — добавление узлов и датацентров
2. **Вертикальное масштабирование** — апгрейд hardware
3. **Capacity planning** — прогнозирование роста и планирование ресурсов

### Best practices:

1. **Query optimization** — анализ паттернов, устранение anti-patterns
2. **Индекс strategy** — правильный выбор типов индексов
3. **Compaction tuning** — выбор стратегии в зависимости от workload
4. **Caching optimization** — настройка различных типов кэшей

### Вызовы и решения:

1. **Сложность оптимизации** — требует экспертизы и опыта
2. **Trade-offs** — баланс между различными аспектами производительности
3. **Непрерывные изменения** — workload эволюционирует со временем
4. **Ресурсные ограничения** — hardware и бюджет ограничения

Оптимальная производительность достигается не разовыми оптимизациями, а систематическим подходом к мониторингу, анализу и улучшениям. Cassandra способна обеспечить превосходную производительность в самых требовательных сценариях при правильной настройке и обслуживании. 🎯

Регулярное профилирование, мониторинг и тюнинг — ключ к поддержанию высокой производительности Cassandra кластера на протяжении всего срока эксплуатации. 

Фундаментальный принцип заключается в глубоком понимании специфики нагрузки и архитектуры системы. Необходимо тщательно анализировать паттерны использования, выявлять узкие места и применять целенаправленные оптимизации. Только такой комплексный подход гарантирует стабильную и эффективную работу высоконагруженных Cassandra-кластеров. Важно помнить, что производительность - это не статическое состояние, а динамический процесс постоянного совершенствования. 🚀

Продолжение следует... Впереди заключительные главы по администрированию и практическим рекомендациям! 🎯

