# MongoDB: Производительность и оптимизация - Полное руководство по тюнингу и мониторингу

Комплексное руководство по оптимизации производительности MongoDB: индексы, запросы, память, мониторинг и best practices.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Performance Best Practices](https://docs.mongodb.com/manual/core/performance-best-practices/)
- [Database Profiler](https://docs.mongodb.com/manual/tutorial/manage-the-database-profiler/)
- [Explain Results](https://docs.mongodb.com/manual/reference/explain-results/)

### Baeldung
- [MongoDB Performance Tuning](https://www.baeldung.com/mongodb-performance-tuning)

### См. также
- `databases/mongodb/mongodb-indexes.md` - Индексы и оптимизация запросов
- `databases/mongodb/mongodb-aggregation.md` - Aggregation Framework

## Содержание

- [Введение в оптимизацию производительности MongoDB](#введение-в-оптимизацию-производительности-mongodb)
- [Профилирование и анализ запросов](#профилирование-и-анализ-запросов)
- [Оптимизация запросов](#оптимизация-запросов)
- [Управление памятью и кэшем](#управление-памятью-и-кэшем)
- [Оптимизация дискового I/O](#оптимизация-дискового-io)
- [Репликация и шардирование для производительности](#репликация-и-шардирование-для-производительности)
- [Мониторинг производительности](#мониторинг-производительности)
- [Best Practices по оптимизации](#best-practices-по-оптимизации)
- [Troubleshooting Performance Issues](#troubleshooting-performance-issues)
- [Заключение](#заключение)

## Введение в оптимизацию производительности MongoDB

**Производительность MongoDB** зависит от множества факторов: аппаратного обеспечения, конфигурации, структуры данных, паттернов запросов и архитектуры приложения. Правильная оптимизация может ускорить операции в десятки и сотни раз.

```
Performance Pyramid:
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  • Connection Pooling    • Query Optimization              │
│  • Caching Strategy      • Schema Design                   │
├─────────────────────────────────────────────────────────────┤
│                     Database Layer                          │
│  • Indexing Strategy     • Memory Management               │
│  • Storage Engine        • Replication/Sharding            │
├─────────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                      │
│  • Hardware Specs        • Network Latency                 │
│  • Disk I/O              • CPU Resources                   │
└─────────────────────────────────────────────────────────────┘
```

### Основные метрики производительности

1. **Latency**: Время отклика запросов
2. **Throughput**: Количество операций в секунду
3. **Resource Utilization**: Использование CPU, памяти, диска
4. **Error Rates**: Процент неудачных операций
5. **Connection Counts**: Активные соединения

### Факторы влияния на производительность

#### Application Level
- **Connection Pooling**: Переиспользование соединений
- **Query Patterns**: Эффективность запросов
- **Caching**: Кэширование на уровне приложения
- **Batch Operations**: Группировка операций

#### Database Level
- **Indexing**: Правильные индексы для запросов
- **Schema Design**: Структура данных и денормализация
- **Memory Usage**: Working set в RAM
- **Storage Engine**: WiredTiger настройки

#### Infrastructure Level
- **CPU**: Количество ядер и тактовая частота
- **Memory**: Объем RAM для working set
- **Storage**: SSD vs HDD, RAID конфигурация
- **Network**: Пропускная способность и latency

## Профилирование и анализ запросов

### Database Profiler

#### Включение профилирования
```javascript
// Включить профилирование для всех операций > 100ms
db.setProfilingLevel(1, { slowms: 100 })

// Включить профилирование для всех операций
db.setProfilingLevel(2)

// Выключить профилирование
db.setProfilingLevel(0)
```

#### Просмотр профилированных запросов
```javascript
// Последние медленные запросы
db.system.profile.find().sort({ ts: -1 }).limit(10)

// Запросы по коллекции
db.system.profile.find({ ns: "mydb.users" })

// Запросы с определенным operation
db.system.profile.find({ op: "query" })
```

#### Структура профиля
```javascript
{
  "op": "query",                    // Тип операции
  "ns": "mydb.users",              // Namespace
  "query": { "name": "John" },     // Запрос
  "ntoreturn": 0,                  // Количество возвращаемых документов
  "ntoskip": 0,                    // Пропущенные документы
  "nscanned": 1000,               // Просканированные документы
  "nreturned": 1,                 // Возвращенные документы
  "keyUpdates": 0,                // Обновлений индексов
  "numYield": 0,                  // Уступок процессора
  "lockStats": { ... },           // Статистика блокировок
  "millis": 150,                  // Время выполнения (мс)
  "execStats": { ... },           // Детальная статистика выполнения
  "ts": ISODate("2023-01-01T00:00:00Z"), // Timestamp
  "client": "127.0.0.1:12345",    // Клиент
  "user": "appuser"               // Пользователь
}
```

### Explain Plan

#### Анализ плана выполнения
```javascript
// Простой explain
db.users.find({ name: "John" }).explain()

// Детальный explain
db.users.find({ name: "John" }).explain("executionStats")

// Все планы выполнения
db.users.find({ name: "John" }).explain("allPlansExecution")
```

#### Структура explain результата
```javascript
{
  "queryPlanner": {
    "plannerVersion": 1,
    "namespace": "mydb.users",
    "indexFilterSet": false,
    "parsedQuery": { "name": { "$eq": "John" } },
    "winningPlan": {
      "stage": "FETCH",
      "inputStage": {
        "stage": "IXSCAN",
        "keyPattern": { "name": 1 },
        "indexName": "name_1",
        "direction": "forward",
        "indexBounds": { "name": ["John", "John"] }
      }
    },
    "rejectedPlans": []
  },
  "executionStats": {
    "executionSuccess": true,
    "nReturned": 1,
    "executionTimeMillis": 0,
    "totalKeysExamined": 1,
    "totalDocsExamined": 1,
    "executionStages": {
      "stage": "FETCH",
      "nReturned": 1,
      "executionTimeMillisEstimate": 0,
      "works": 2,
      "advanced": 1,
      "needTime": 0,
      "needYield": 0,
      "saveState": 0,
      "restoreState": 0,
      "isEOF": 1,
      "invalidates": 0,
      "docsExamined": 1,
      "alreadyHasObj": 0,
      "inputStage": {
        "stage": "IXSCAN",
        "nReturned": 1,
        "executionTimeMillisEstimate": 0,
        "works": 2,
        "advanced": 1,
        "needTime": 0,
        "needYield": 0,
        "saveState": 0,
        "restoreState": 0,
        "isEOF": 1,
        "invalidates": 0,
        "keyPattern": { "name": 1 },
        "indexName": "name_1",
        "isMultiKey": false,
        "multiKeyPaths": { "name": [] },
        "isUnique": false,
        "isSparse": false,
        "isPartial": false,
        "indexVersion": 2,
        "direction": "forward",
        "indexBounds": { "name": ["John", "John"] },
        "keysExamined": 1,
        "seeks": 1,
        "dupsTested": 0,
        "dupsDropped": 0
      }
    }
  }
}
```

### Java анализ запросов

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;

// Explain query
FindIterable<Document> iterable = collection.find(Filters.eq("name", "John"));
Document explanation = iterable.explain();

System.out.println("Winning plan: " + explanation.get("winningPlan"));
System.out.println("Execution stats: " + explanation.get("executionStats"));

// Получить метрики
Document executionStats = (Document) explanation.get("executionStats");
int nReturned = executionStats.getInteger("nReturned");
long executionTime = executionStats.getLong("executionTimeMillis");
int docsExamined = executionStats.getInteger("totalDocsExamined");
int keysExamined = executionStats.getInteger("totalKeysExamined");

System.out.printf("Returned: %d, Time: %dms, Docs examined: %d, Keys examined: %d%n",
                 nReturned, executionTime, docsExamined, keysExamined);
```

## Оптимизация запросов

### Query Optimization Techniques

#### 1. Использование индексов

##### Single Field Index
```javascript
// Создать индекс
db.users.createIndex({ name: 1 })

// Запрос использует индекс
db.users.find({ name: "John" }).explain()

// Результат: IXSCAN вместо COLLSCAN
```

##### Compound Index
```javascript
// Compound индекс для фильтрации и сортировки
db.orders.createIndex({ customerId: 1, orderDate: -1, total: 1 })

// Оптимизированный запрос
db.orders.find({ customerId: "123" })
    .sort({ orderDate: -1 })
    .limit(10)
```

##### Covered Query
```javascript
// Индекс покрывает запрос полностью
db.users.createIndex({ name: 1, email: 1, age: 1 })

// Запрос только из индекса (без доступа к документам)
db.users.find(
    { age: { $gte: 18 } },
    { name: 1, email: 1, _id: 0 }
).explain()

// Результат: IXSCAN без FETCH
```

#### 2. Избегание неоптимальных паттернов

##### Неэффективные запросы
```javascript
// ❌ Плохо: regex без индекса
db.users.find({ email: /^john/ })

// ✅ Хороший: точное совпадение
db.users.find({ email: "john@example.com" })

// ❌ Плохо: $ne (negative match)
db.users.find({ status: { $ne: "active" } })

// ✅ Хороший: положительные условия
db.users.find({ status: "active" })

// ❌ Плохо: большое количество значений в $in
db.users.find({ userId: { $in: [1,2,3,...,1000] } })

// ✅ Хороший: перестроить запрос
// Использовать отдельную коллекцию для many-to-many
db.userTags.insertMany([
    { userId: 1, tagId: 100 },
    { userId: 2, tagId: 100 }
])

db.userTags.createIndex({ tagId: 1 })
db.userTags.find({ tagId: 100 })
```

#### 3. Pagination оптимизация

##### Skip-based pagination (плохой)
```javascript
// ❌ Плохой: skip становится медленным
db.posts.find()
    .sort({ createdAt: -1 })
    .skip(10000)  // Пропускает 10000 документов
    .limit(10)
```

##### Cursor-based pagination (хороший)
```javascript
// ✅ Хороший: range query
db.posts.find({ createdAt: { $lt: lastCreatedAt } })
    .sort({ createdAt: -1 })
    .limit(11)  // +1 для проверки следующей страницы
```

### Java оптимизация запросов

```java
@Service
public class OptimizedUserService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // Использование индексов
    public List<User> findUsersByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        query.with(Sort.by(Sort.Direction.ASC, "name"));
        
        // Добавить hint если нужно
        // query.withHint("name_1");
        
        return mongoTemplate.find(query, User.class);
    }
    
    // Оптимизированная пагинация
    public Page<User> findUsersPaginated(String lastId, int limit) {
        Query query = new Query();
        
        if (lastId != null) {
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(lastId)));
        }
        
        query.with(Sort.by("_id")).limit(limit + 1);
        
        List<User> users = mongoTemplate.find(query, User.class);
        boolean hasNext = users.size() > limit;
        
        if (hasNext) {
            users = users.subList(0, limit);
        }
        
        return new PageImpl<>(users, PageRequest.of(0, limit), hasNext ? limit + 1 : limit);
    }
    
    // Batch операции
    public void updateUsersBatch(List<String> userIds, Status status) {
        Query query = new Query(Criteria.where("_id").in(userIds));
        Update update = Update.update("status", status)
            .set("updatedAt", new Date());
        
        // Bulk update
        mongoTemplate.updateMulti(query, update, User.class);
    }
    
    // Aggregation для аналитики
    public List<Document> getUserStats() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)),
            Aggregation.group("department")
                .count().as("userCount")
                .avg("salary").as("avgSalary"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "userCount"))
        );
        
        return mongoTemplate.aggregate(aggregation, "users", Document.class)
            .getMappedResults();
    }
}
```

## Управление памятью и кэшем

### WiredTiger Storage Engine

#### Cache настройки
```javascript
// Проверить cache size
db.serverStatus().wiredTiger.cache

// Конфигурация mongod.conf
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 4  # 50-80% от RAM для dedicated MongoDB
      journalCompressor: snappy
      directoryForIndexes: true
    collectionConfig:
      blockCompressor: snappy
    indexConfig:
      prefixCompression: true
```

#### Memory monitoring
```javascript
// Текущая память
db.serverStatus().mem

// WiredTiger cache stats
db.serverStatus().wiredTiger.cache

// Resident vs Virtual memory
db.serverStatus().mem.resident  // RAM usage
db.serverStatus().mem.virtual   // Virtual memory
```

### Working Set

#### Анализ working set
```javascript
// Проверить что в памяти
db.users.stats().wiredTiger

// Размер индексов в памяти
db.users.stats().indexSizes

// Page faults (должно быть мало)
db.serverStatus().extra_info.page_faults
```

#### Оптимизация working set
```javascript
// 1. Добавить необходимые индексы
db.users.createIndex({ frequentlyQueriedField: 1 })

// 2. Удалить неиспользуемые индексы
db.users.dropIndex("unused_index")

// 3. Компакт коллекции
db.runCommand({ compact: "users" })

// 4. Настроить cache size
// mongod.conf
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 8  # Для сервера с 16GB RAM
```

### Java memory management

```java
@Configuration
public class MongoConfig {
    
    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
            .applyToConnectionPoolSettings(builder -> 
                builder.maxSize(20)      // Максимум соединений
                    .minSize(5)          // Минимум соединений
                    .maxWaitTime(5000, TimeUnit.MILLISECONDS)
                    .maxConnectionIdleTime(30, TimeUnit.SECONDS)
            )
            .applyToSocketSettings(builder ->
                builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(15000, TimeUnit.MILLISECONDS)
            )
            .build();
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoTemplate template = new MongoTemplate(mongoClient, "mydb");
        
        // Настройки для производительности
        template.setReadPreference(ReadPreference.secondaryPreferred());
        template.setWriteConcern(WriteConcern.W1.withJournal(false));
        
        return template;
    }
}
```

## Оптимизация дискового I/O

### Storage Engine настройки

#### WiredTiger конфигурация
```yaml
# mongod.conf
storage:
  dbPath: /data/mongodb
  journal:
    enabled: true
    commitIntervalMs: 100  # Частота commit journal
  
  wiredTiger:
    engineConfig:
      cacheSizeGB: 8
      journalMaxFileSizeGB: 0.5
      maxCacheOverflowSizeGB: 0.5
      
    collectionConfig:
      blockCompressor: zstd  # Лучше чем snappy для CPU bound
      
    indexConfig:
      prefixCompression: true
```

#### Directory per DB
```yaml
# Для лучшей производительности
storage:
  directoryPerDB: true
  wiredTiger:
    engineConfig:
      directoryForIndexes: true  # Отдельная директория для индексов
```

### Disk I/O monitoring

#### I/O статистика
```javascript
// Disk I/O stats
db.serverStatus().wiredTiger.threadYield

// Page faults
db.serverStatus().extra_info.page_faults

// Disk usage
db.stats().fsUsedMB
db.stats().fsTotalMB
```

#### Linux I/O monitoring
```bash
# I/O статистика
iostat -x 1

# Disk usage
df -h

# MongoDB disk I/O
iotop -p $(pgrep mongod)
```

### Оптимизация I/O

#### RAID и Filesystem
```bash
# Рекомендуемая настройка
# RAID 10 для data и journal
# XFS или EXT4 filesystem
# noatime опция

# fstab настройка
/dev/sdb1  /data  xfs  noatime,nodiratime  0 0
```

#### NUMA настройки
```bash
# Отключить NUMA для MongoDB
echo 0 > /proc/sys/vm/zone_reclaim_mode

# Или в systemd
# /etc/systemd/system/mongod.service.d/numa.conf
[Service]
Environment=MONGO_NUMACTL=disable
```

## Репликация и шардирование для производительности

### Read Preferences для масштабирования

#### Распределение нагрузки чтения
```javascript
// Primary для важных данных
db.importantData.find().readPref("primary")

// Secondary для аналитики
db.analytics.find().readPref("secondaryPreferred")

// Nearest для глобальных приложений
db.globalData.find().readPref("nearest")
```

#### Java read preferences
```java
@Configuration
public class MongoConfig {
    
    @Bean
    public MongoTemplate analyticsTemplate(MongoClient mongoClient) {
        // Secondary для аналитики
        MongoTemplate template = new MongoTemplate(mongoClient, "analytics");
        template.setReadPreference(ReadPreference.secondaryPreferred());
        return template;
    }
    
    @Bean 
    public MongoTemplate criticalTemplate(MongoClient mongoClient) {
        // Primary для критичных данных
        MongoTemplate template = new MongoTemplate(mongoClient, "critical");
        template.setReadPreference(ReadPreference.primary());
        return template;
    }
}
```

### Sharding для горизонтального масштабирования

#### Shard key выбор
```javascript
// Хороший shard key: высокая кардинальность, равномерное распределение
db.users.createIndex({ userId: 1 })
sh.enableSharding("mydb")
sh.shardCollection("mydb.users", { userId: 1 })

// Плохой shard key: монотонно возрастающий
// db.events.createIndex({ timestamp: 1 })  // Избегать!
```

#### Chunk balancing
```javascript
// Проверить распределение chunks
sh.status()

// Вручная балансировка
sh.startBalancer()
sh.stopBalancer()

// Настройки балансировки
sh.setBalancerState(true)
sh.getBalancerState()
```

### Write Concerns оптимизация

#### Выбор write concern по важности
```javascript
// Критичные данные
db.accounts.insertOne(doc, { writeConcern: { w: "majority" } })

// Логи и метрики
db.logs.insertOne(doc, { writeConcern: { w: 0 } })

// Обычные данные
db.products.insertOne(doc, { writeConcern: { w: 1 } })
```

## Мониторинг производительности

### MongoDB метрики

#### Server status
```javascript
// Основные метрики
db.serverStatus()

// Ключевые показатели
{
  "opcounters": {        // Операции по типам
    "insert": 1000,
    "query": 5000,
    "update": 800,
    "delete": 200
  },
  "opcountersRepl": {    // Репликационные операции
    ...
  },
  "mem": {              // Память
    "resident": 2048,   // RAM usage (MB)
    "virtual": 4096
  },
  "connections": {      // Соединения
    "current": 150,
    "available": 850
  },
  "network": {          // Сеть
    "bytesIn": 1000000,
    "bytesOut": 2000000
  }
}
```

#### Database metrics
```javascript
// Статистика базы данных
db.stats()

// Статистика коллекции
db.users.stats()

// Index usage
db.users.aggregate([
    { $indexStats: {} }
])
```

### Java мониторинг

```java
@Service
public class MongoPerformanceMonitor {
    
    @Autowired
    private MongoClient mongoClient;
    
    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void monitorPerformance() {
        MongoDatabase adminDb = mongoClient.getDatabase("admin");
        
        // Server status
        Document serverStatus = adminDb.runCommand(new Document("serverStatus", 1));
        
        // Memory usage
        Document mem = (Document) serverStatus.get("mem");
        int resident = mem.getInteger("resident");
        int virtual = mem.getInteger("virtual");
        
        // Connections
        Document connections = (Document) serverStatus.get("connections");
        int current = connections.getInteger("current");
        int available = connections.getInteger("available");
        
        // Operations counters
        Document opcounters = (Document) serverStatus.get("opcounters");
        int queries = opcounters.getInteger("query");
        int inserts = opcounters.getInteger("insert");
        
        // Log metrics
        logger.info("MongoDB Memory - Resident: {}MB, Virtual: {}MB", resident, virtual);
        logger.info("MongoDB Connections - Current: {}, Available: {}", current, available);
        logger.info("MongoDB Operations - Queries: {}, Inserts: {}", queries, inserts);
    }
    
    public Document getSlowQueries() {
        MongoDatabase db = mongoClient.getDatabase("mydb");
        MongoCollection<Document> profile = db.getCollection("system.profile");
        
        return profile.find()
            .sort(new Document("millis", -1))
            .limit(10)
            .first();
    }
}
```

### Prometheus + Grafana

#### MongoDB exporter
```yaml
# docker-compose.yml
services:
  mongodb-exporter:
    image: bitnami/mongodb-exporter:latest
    environment:
      MONGODB_URI: mongodb://admin:password@mongodb:27017
    ports:
      - "9216:9216"
```

#### Prometheus конфигурация
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'mongodb'
    static_configs:
      - targets: ['mongodb-exporter:9216']
```

#### Grafana dashboard
```json
{
  "dashboard": {
    "title": "MongoDB Performance",
    "panels": [
      {
        "title": "Memory Usage",
        "targets": [
          {
            "expr": "mongodb_mem_resident",
            "legendFormat": "Resident Memory"
          }
        ]
      },
      {
        "title": "Operations Rate",
        "targets": [
          {
            "expr": "rate(mongodb_opcounters_total[5m])",
            "legendFormat": "{{op}}"
          }
        ]
      }
    ]
  }
}
```

## Best Practices по оптимизации

### 1. Hardware рекомендации

#### CPU
- **4-8 ядер** минимум для production
- **Высокая тактовая частота** важнее количества ядер
- **NUMA-aware** размещение

#### Memory
- **Working set + 25%** дополнительно
- **Minimum 8GB** для development
- **32GB+** для production

#### Storage
- **SSD required** для production
- **RAID 10** для data, RAID 1 для journal
- **Separate disks** для data, journal, logs

### 2. Конфигурация оптимизация

#### WiredTiger настройки
```yaml
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 0.6 * RAM  # 60% от RAM
      maxCacheOverflowSizeGB: 0.1 * cacheSizeGB
    collectionConfig:
      blockCompressor: zstd
    indexConfig:
      prefixCompression: true
```

#### Network настройки
```yaml
net:
  maxIncomingConnections: 1000
  compression:
    compressors: zstd,snappy,zlib
```

### 3. Application level оптимизации

#### Connection pooling
```java
@Configuration
public class OptimizedMongoConfig {
    
    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
            .applyToConnectionPoolSettings(builder -> 
                builder.maxSize(100)     // Максимум соединений
                    .minSize(10)          // Минимум поддерживаемых
                    .maxWaitTime(2000, MILLISECONDS)
                    .maxConnectionIdleTime(30, SECONDS)
                    .maxConnectionLifeTime(5, MINUTES)
            )
            .build();
    }
}
```

#### Query оптимизации
```java
@Service
public class OptimizedService {
    
    // Использовать projection для уменьшения данных
    public List<UserSummary> getUserSummaries() {
        Query query = new Query();
        query.fields()
            .include("name")
            .include("email")
            .include("lastLogin")
            .exclude("_id");
        
        return mongoTemplate.find(query, UserSummary.class);
    }
    
    // Batch операции вместо множественных
    public void updateUsersBatch(List<String> userIds, Status status) {
        Query query = new Query(Criteria.where("_id").in(userIds));
        Update update = Update.update("status", status)
            .set("updatedAt", new Date());
        
        mongoTemplate.updateMulti(query, update, User.class);
    }
    
    // Использовать aggregation для аналитики
    public List<DepartmentStats> getDepartmentStats() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)),
            Aggregation.group("department")
                .count().as("count")
                .avg("salary").as("avgSalary"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "count"))
        );
        
        return mongoTemplate.aggregate(agg, User.class, DepartmentStats.class)
            .getMappedResults();
    }
}
```

### 4. Monitoring и alerting

#### Ключевые метрики для мониторинга
```yaml
# Prometheus alerting rules
groups:
  - name: mongodb_performance
    rules:
      - alert: MongoDBHighMemoryUsage
        expr: mongodb_mem_resident / mongodb_mem_virtual > 0.9
        for: 5m
        labels:
          severity: warning
        
      - alert: MongoDBHighConnectionCount
        expr: mongodb_connections_current > 800
        for: 5m
        labels:
          severity: warning
          
      - alert: MongoDBSlowQueries
        expr: rate(mongodb_opcounters_query[5m]) > 1000
        for: 2m
        labels:
          severity: warning
```

## Troubleshooting Performance Issues

### Высокая CPU загрузка

#### Диагностика
```javascript
// Проверить текущие операции
db.currentOp()

// Проверить slow queries
db.system.profile.find({ millis: { $gt: 1000 } })

// Проверить индексы
db.collection.getIndexes()
```

#### Решения
```javascript
// 1. Добавить недостающие индексы
db.collection.createIndex({ field: 1 })

// 2. Оптимизировать запросы
// Избегать full collection scans
// Использовать covered queries

// 3. Увеличить hardware resources
// Добавить CPU cores
// Увеличить RAM
```

### Высокое потребление памяти

#### Диагностика
```javascript
// Проверить memory usage
db.serverStatus().mem

// Проверить cache hit ratio
db.serverStatus().wiredTiger.cache

// Проверить large documents
db.collection.find().sort({ $natural: -1 }).limit(10)
```

#### Решения
```javascript
// 1. Увеличить cache size
// mongod.conf: cacheSizeGB: 16

// 2. Оптимизировать working set
// Добавить индексы для часто используемых полей
// Удалить неиспользуемые индексы

// 3. Проверить large documents
// Нормализовать большие документы
// Использовать GridFS для файлов
```

### Медленные запросы

#### Диагностика
```javascript
// Explain slow query
db.collection.find(query).explain("executionStats")

// Проверить index usage
db.collection.find(query).explain()

// Проверить system.profile
db.system.profile.find().sort({ millis: -1 }).limit(5)
```

#### Решения
```javascript
// 1. Добавить соответствующие индексы
db.collection.createIndex({ field: 1 })

// 2. Переписать запросы
// Избегать regex без индексов
// Использовать $in вместо множества $or

// 3. Использовать compound индексы
db.collection.createIndex({ field1: 1, field2: 1 })

// 4. Проверить data model
// Денормализация для частых joins
// Embedded documents вместо references
```

### Высокий I/O

#### Диагностика
```bash
# Disk I/O
iostat -x 1

# MongoDB I/O
db.serverStatus().wiredTiger

# Page faults
db.serverStatus().extra_info.page_faults
```

#### Решения
```javascript
// 1. Добавить RAM или SSD
// 2. Оптимизировать индексы
// 3. Настроить WiredTiger cache
// 4. Использовать compression
db.collection.createIndex({ field: 1 }, { 
  storageEngine: { 
    wiredTiger: { configString: 'block_compressor=zstd' }
  }
})
```

### Connection pool проблемы

#### Диагностика
```javascript
// Проверить connections
db.serverStatus().connections

// Проверить connection pool в приложении
// Java: MongoClientSettings connection pool config
```

#### Решения
```java
// Настроить connection pool
MongoClientSettings.builder()
    .applyToConnectionPoolSettings(builder ->
        builder.maxSize(50)     // Максимум соединений
            .minSize(5)         // Минимум соединений
            .maxWaitTime(5000, MILLISECONDS)
    )
```

### Replication lag

#### Диагностика
```javascript
// Проверить replication status
rs.status()

// Проверить oplog size
db.getReplicationInfo()

// Проверить network latency
```

#### Решения
```javascript
// 1. Увеличить oplog size
db.adminCommand({ replSetResizeOplog: 1, size: 1000 })

// 2. Оптимизировать secondary
// Добавить индексы на secondary
// Увеличить ресурсы secondary

// 3. Проверить network
// Уменьшить latency между nodes
```

## Заключение

**Оптимизация производительности MongoDB** — это комплексный процесс, затрагивающий все уровни: от hardware и конфигурации до паттернов запросов и архитектуры приложения.

### Ключевые принципы оптимизации:

1. **Правильные индексы** — основа высокой производительности
2. **Оптимальный data model** — денормализация для частых запросов
3. **Эффективные запросы** — использование индексов и projection
4. **Масштабирование** — репликация и шардирование для нагрузки
5. **Мониторинг** — постоянный контроль метрик

### Hardware и конфигурация:

- **RAM**: Working set + 25% минимум
- **SSD**: Обязательно для production
- **CPU**: 4+ ядер с высокой частотой
- **Network**: Низкая latency для кластеров

### Application оптимизации:

```java
// Connection pooling
MongoClientSettings.builder()
    .applyToConnectionPoolSettings(builder -> 
        builder.maxSize(50).minSize(5)
    )

// Query optimization
Query query = new Query(Criteria.where("field").is(value));
query.with(Sort.by("field")).limit(10);

// Batch operations
mongoTemplate.insertAll(documents);

// Aggregation для аналитики
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.match(criteria),
    Aggregation.group("field").count().as("count")
);
```

### Мониторинг и alerting:

- **Метрики**: CPU, память, I/O, соединения, операции
- **Профилирование**: Медленные запросы и их анализ
- **Alerting**: Автоматические уведомления о проблемах

### Распространенные проблемы:

1. **Отсутствие индексов** — COLLSCAN вместо IXSCAN
2. **Неправильные индексы** — не покрывают запросы
3. **Перегруженная память** — working set не помещается в RAM
4. **Неэффективные запросы** — regex, negative conditions
5. **Проблемы репликации** — lag, network issues

Правильная оптимизация MongoDB требует глубокого понимания workload, паттернов использования и ограничений системы. Регулярный мониторинг и профилирование — ключ к поддержанию высокой производительности в production. 🚀
