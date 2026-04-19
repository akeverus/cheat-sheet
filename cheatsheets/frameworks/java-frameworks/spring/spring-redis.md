---
title: "Spring Data Redis: Полное руководство"
description: "Комплексное руководство по Spring Data Redis: RedisTemplate, операции, cache abstraction, pub/sub, transactions и best practices"
tags:
  - spring
  - redis
  - cache
  - nosql
  - pub-sub
  - java
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "spring/spring-cache.md"]
next: ["spring/spring-mongodb.md", "databases/redis.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "spring/spring-cache.md", "databases/redis.md"]
---

# Spring Data Redis: Полное руководство



## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Введение в Spring Data Redis](#введение-в-spring-data-redis)
  - [Основные возможности](#основные-возможности)
  - [Архитектура Spring Data Redis](#архитектура-spring-data-redis)
- [Настройка Spring Data Redis](#настройка-spring-data-redis)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Redis Configuration](#redis-configuration)
  - [Java Configuration](#java-configuration)
- [RedisTemplate](#redistemplate)
  - [Базовые операции](#базовые-операции)
  - [Работа с объектами](#работа-с-объектами)
  - [List Operations](#list-operations)
  - [Set Operations](#set-operations)
  - [Hash Operations](#hash-operations)
- [Redis Repositories](#redis-repositories)
  - [Настройка Repository](#настройка-repository)
  - [Entity](#entity)
  - [Repository](#repository)
- [Cache Abstraction](#cache-abstraction)
  - [Настройка Redis Cache](#настройка-redis-cache)
  - [Использование Cache](#использование-cache)
- [Pub/Sub](#pubsub)
  - [Publisher](#publisher)
  - [Subscriber](#subscriber)
- [Transactions](#transactions)
  - [Транзакционные операции](#транзакционные-операции)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные сериализаторы](#1-используйте-правильные-сериализаторы)
  - [2. Настраивайте TTL для кеша](#2-настраивайте-ttl-для-кеша)
  - [3. Используйте connection pooling](#3-используйте-connection-pooling)
- [✅ Хорошо](#хорошо)
  - [4. Обрабатывайте ошибки подключения](#4-обрабатывайте-ошибки-подключения)
  - [5. Используйте транзакции для критических операций](#5-используйте-транзакции-для-критических-операций)
- [Redis Streams](#redis-streams)
  - [Отправка сообщений в Stream](#отправка-сообщений-в-stream)
  - [Чтение из Stream](#чтение-из-stream)
  - [Consumer Groups](#consumer-groups)
- [Redis Cluster](#redis-cluster)
  - [Настройка Cluster](#настройка-cluster)
- [Redis Cluster Configuration](#redis-cluster-configuration)
  - [Работа с Cluster](#работа-с-cluster)
- [Redis Sentinel](#redis-sentinel)
  - [Настройка Sentinel](#настройка-sentinel)
- [Redis Sentinel Configuration](#redis-sentinel-configuration)
- [Продвинутые операции](#продвинутые-операции)
  - [Pipeline Operations](#pipeline-operations)
  - [Lua Scripts](#lua-scripts)
  - [Bitmap Operations](#bitmap-operations)
  - [HyperLogLog](#hyperloglog)
  - [Geospatial Operations](#geospatial-operations)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Redis Metrics](#redis-metrics)
  - [Health Check](#health-check)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Connection Pooling](#connection-pooling)
  - [Serialization Optimization](#serialization-optimization)
- [Безопасность](#безопасность)
  - [Redis Authentication](#redis-authentication)
  - [SSL/TLS Configuration](#ssltls-configuration)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Spring Data Redis**

**Spring Data Redis** предоставляет простую интеграцию с **Redis** для работы с ключ-значение хранилищем, кешированием, **pub**/**sub messaging** и другими возможностями **Redis**.

### Основные возможности

- **RedisTemplate**: Удобный **API** для работы с **Redis**
- **Repository Support**: **Spring Data** репозитории для **Redis**
- **Cache Abstraction**: Интеграция с **Spring Cache**
- **Pub/Sub**: Поддержка **publish**/**subscribe**
- **Transactions**: Транзакционная поддержка

### Архитектура **Spring Data Redis**

```text
┌─────────────────────────────────────────────────────────┐
│              Application Code                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Redis      │  │   Redis     │  │   Cache      │  │
│  │   Template   │  │   Repository│  │   Abstraction│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Redis Client                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Lettuce    │  │   Jedis      │  │   Reactive    │  │
│  │   Client     │  │   Client     │  │   Client      │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Redis Server                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Key-Value  │  │   Pub/Sub     │  │   Cache      │  │
│  │   Store      │  │   Messaging   │  │   Layer      │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Настройка **Spring Data Redis**

### Зависимости

**Зависимость **spring-`boot-starter-data`-redis** (**pom.xml**):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Конфигурация

**application.properties:**

```properties
# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.timeout=2000ms
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

### **Java Configuration**

```java
// Конфигурация RedisTemplate и сериализаторов
@Configuration
public class RedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        return new LettuceConnectionFactory(config);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

## RedisTemplate

### Базовые операции

```java
// Сервис для операций с Redis (get/set/delete)
@Service
public class RedisService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
    
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public void setValueWithExpiry(String key, String value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
}
```

### Работа с объектами

```java
@Service
public class UserRedisService {
    
    @Autowired
    private RedisTemplate<String, User> redisTemplate;
    
    public void saveUser(String key, User user) {
        redisTemplate.opsForValue().set(key, user);
    }
    
    public User getUser(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void saveUserWithExpiry(String key, User user, Duration timeout) {
        redisTemplate.opsForValue().set(key, user, timeout);
    }
}
```

### **List Operations**

```java
@Service
public class ListRedisService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void addToList(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }
    
    public List<String> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    
    public String popFromList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    public long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
}
```

### **Set Operations**

```java
@Service
public class SetRedisService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void addToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }
    
    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    public boolean isMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    public void removeFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }
}
```

### **Hash Operations**

```java
@Service
public class HashRedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setHashValue(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
    
    public Object getHashValue(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
    
    public Map<Object, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    public void deleteHashKey(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }
}
```

## Redis Repositories

### Настройка **Repository**

```java
@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
}
```

### **Entity**

```java
@RedisHash("user")
public class User {
    
    @Id
    private String id;
    
    @Indexed
    private String email;
    
    @Indexed
    private String name;
    
    private Integer age;
    
    // Getters and setters...
}
```

### **Repository**

```java
public interface UserRepository extends CrudRepository<User, String> {
    
    List<User> findByEmail(String email);
    
    List<User> findByName(String name);
    
    List<User> findByAgeGreaterThan(Integer age);
}
```

## Cache Abstraction

### Настройка **Redis Cache**

```java
@Configuration
@EnableCaching
public class RedisCacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("users", 
                config.entryTtl(Duration.ofMinutes(30)))
            .withCacheConfiguration("products",
                config.entryTtl(Duration.ofHours(2)))
            .transactionAware()
            .build();
    }
}
```

### Использование **Cache**

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User findById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public User update(User user) {
        return userRepository.save(user);
    }
}
```

## Pub/Sub

### **Publisher**

```java
@Service
public class RedisPublisher {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
```

### **Subscriber**

```java
@Component
public class RedisSubscriber implements MessageListener {
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        System.out.println("Received message from channel: " + channel + ", body: " + body);
    }
}

@Configuration
public class RedisPubSubConfig {
    
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new PatternTopic("user.*"));
        return container;
    }
}
```

## Transactions

### Транзакционные операции

```java
@Service
public class TransactionalRedisService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Transactional
    public void transfer(String fromKey, String toKey, String amount) {
        String fromValue = redisTemplate.opsForValue().get(fromKey);
        String toValue = redisTemplate.opsForValue().get(toKey);
        
        // Операции в транзакции
        redisTemplate.opsForValue().set(fromKey, 
            String.valueOf(Integer.parseInt(fromValue) - Integer.parseInt(amount)));
        redisTemplate.opsForValue().set(toKey, 
            String.valueOf(Integer.parseInt(toValue) + Integer.parseInt(amount)));
    }
}
```

## Лучшие практики

### 1. Используйте правильные сериализаторы

```java
// ✅ Хорошо
template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

// ❌ Плохо - для объектов
template.setValueSerializer(new StringRedisSerializer());
```

### 2. Настраивайте **TTL** для кеша

```java
// ✅ Хорошо
config.entryTtl(Duration.ofHours(1));
```

### 3. Используйте **connection pooling**

```properties
# ✅ Хорошо
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
```

### 4. Обрабатывайте ошибки подключения

```java
// ✅ Хорошо
try {
    redisTemplate.opsForValue().set(key, value);
} catch (Exception e) {
    log.error("Redis error", e);
    // Fallback логика
}
```

### 5. Используйте транзакции для критических операций

```java
// ✅ Хорошо
@Transactional
public void transfer(String from, String to, String amount) {
    // Транзакционные операции
}
```

## Redis Streams

### Отправка сообщений в **Stream**

```java
@Service
public class RedisStreamProducer {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void sendToStream(String stream, String key, String value) {
        Map<String, String> body = Map.of(key, value);
        redisTemplate.opsForStream().add(stream, body);
    }
    
    public void sendToStreamWithId(String stream, String id, Map<String, String> body) {
        redisTemplate.opsForStream().add(Record.of(body).withStreamKey(stream).withId(RecordId.of(id)));
    }
}
```

### Чтение из **Stream**

```java
@Service
public class RedisStreamConsumer {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public List<MapRecord<String, Object, Object>> readFromStream(String stream, String lastId) {
        StreamReadOptions options = StreamReadOptions.empty()
            .count(10)
            .block(Duration.ofSeconds(1));
        
        return redisTemplate.opsForStream().read(
            StreamOffset.create(stream, ReadOffset.from(lastId)),
            options
        );
    }
    
    public void readFromMultipleStreams(Map<String, String> streams) {
        StreamReadOptions options = StreamReadOptions.empty()
            .count(10)
            .block(Duration.ofSeconds(1));
        
        List<StreamOffset<String>> offsets = streams.entrySet().stream()
            .map(e -> StreamOffset.create(e.getKey(), ReadOffset.from(e.getValue())))
            .collect(Collectors.toList());
        
        List<MapRecord<String, Object, Object>> records = 
            redisTemplate.opsForStream().read(options, offsets);
    }
}
```

### **Consumer Groups**

```java
@Configuration
public class RedisStreamConfig {
    
    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> 
            streamMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = 
            StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();
        
        return StreamMessageListenerContainer.create(connectionFactory, options);
    }
}

@Component
public class StreamConsumerGroup {
    
    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    
    @PostConstruct
    public void start() {
        StreamMessageListener<String, MapRecord<String, String, String>> listener = 
            new StreamMessageListener<String, MapRecord<String, String, String>>() {
                @Override
                public void onMessage(MapRecord<String, String, String> message) {
                    // Обработка сообщения
                    processMessage(message);
                }
            };
        
        StreamOffset<String> offset = StreamOffset.create("mystream", ReadOffset.lastConsumed());
        container.receive(Consumer.from("mygroup", "consumer1"), offset, listener);
    }
    
    private void processMessage(MapRecord<String, String, String> message) {
        // Логика обработки
    }
}
```

## Redis Cluster

### Настройка **Cluster**

```properties
# Redis Cluster Configuration
spring.redis.cluster.nodes=localhost:7000,localhost:7001,localhost:7002
spring.redis.cluster.max-redirects=3
spring.redis.timeout=2000ms
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

```java
@Configuration
public class RedisClusterConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        List<String> clusterNodes = Arrays.asList(
            "localhost:7000",
            "localhost:7001",
            "localhost:7002"
        );
        
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        clusterConfiguration.setMaxRedirects(3);
        
        return new LettuceConnectionFactory(clusterConfiguration);
    }
}
```

### Работа с **Cluster**

```java
@Service
public class RedisClusterService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void setInCluster(String key, String value) {
        // Redis автоматически определяет узел по ключу
        redisTemplate.opsForValue().set(key, value);
    }
    
    public String getFromCluster(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void setWithSlot(String key, String value) {
        // Использование hash tags для размещения на одном узле
        String hashTag = "{user:" + key + "}";
        redisTemplate.opsForValue().set(hashTag, value);
    }
}
```

## Redis Sentinel

### Настройка **Sentinel**

```properties
# Redis Sentinel Configuration
spring.redis.sentinel.master=mymaster
spring.redis.sentinel.nodes=localhost:26379,localhost:26380,localhost:26381
```

```java
@Configuration
public class RedisSentinelConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
            .master("mymaster")
            .sentinel("localhost", 26379)
            .sentinel("localhost", 26380)
            .sentinel("localhost", 26381);
        
        return new LettuceConnectionFactory(sentinelConfiguration);
    }
}
```

## Продвинутые операции

### **Pipeline Operations**

```java
@Service
public class RedisPipelineService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public List<Object> executePipeline(List<String> keys) {
        return redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = 
                    (StringRedisConnection) connection;
                
                for (String key : keys) {
                    stringRedisConnection.get(key);
                }
                return null;
            }
        });
    }
}
```

### **Lua Scripts**

```java
@Service
public class RedisScriptService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String INCREMENT_SCRIPT = 
        "local current = redis.call('get', KEYS[1]) " +
        "if current == false then " +
        "  current = 0 " +
        "end " +
        "local new = current + ARGV[1] " +
        "redis.call('set', KEYS[1], new) " +
        "return new";
    
    public Long incrementWithScript(String key, Long delta) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(INCREMENT_SCRIPT);
        script.setResultType(Long.class);
        
        return redisTemplate.execute(script, Collections.singletonList(key), delta.toString());
    }
}
```

### **Bitmap Operations**

```java
@Service
public class RedisBitmapService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void setBit(String key, long offset, boolean value) {
        redisTemplate.opsForValue().setBit(key, offset, value);
    }
    
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }
    
    public Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> 
            connection.bitCount(key.getBytes())
        );
    }
    
    public Long bitOpAnd(String destination, String... keys) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> 
            connection.bitOp(RedisStringCommands.BitOperation.AND, 
                destination.getBytes(), 
                Arrays.stream(keys).map(String::getBytes).toArray(byte[][]::new))
        );
    }
}
```

### **HyperLogLog**

```java
@Service
public class RedisHyperLogLogService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void addToHyperLogLog(String key, String... values) {
        redisTemplate.opsForHyperLogLog().add(key, values);
    }
    
    public Long countHyperLogLog(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }
    
    public Long unionHyperLogLog(String destination, String... keys) {
        return redisTemplate.opsForHyperLogLog().union(destination, keys);
    }
}
```

### **Geospatial Operations**

```java
@Service
public class RedisGeospatialService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void addLocation(String key, String member, double longitude, double latitude) {
        Point point = new Point(longitude, latitude);
        redisTemplate.opsForGeo().add(key, point, member);
    }
    
    public Distance distance(String key, String member1, String member2) {
        return redisTemplate.opsForGeo().distance(key, member1, member2);
    }
    
    public List<GeoResult<RedisGeoCommands.GeoLocation<String>>> findNearby(
            String key, String member, double radius) {
        Circle circle = new Circle(member, new Distance(radius, Metrics.KILOMETERS));
        return redisTemplate.opsForGeo().radius(key, circle).getContent();
    }
}
```

## Мониторинг и метрики

### **Redis Metrics**

```java
@Component
public class RedisMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter redisOperations;
    private final Timer redisOperationTimer;
    
    public RedisMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.redisOperations = Counter.builder("redis.operations")
            .description("Number of Redis operations")
            .tag("type", "total")
            .register(meterRegistry);
        this.redisOperationTimer = Timer.builder("redis.operation.duration")
            .description("Redis operation duration")
            .register(meterRegistry);
    }
    
    public <T> T measureOperation(String operation, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = supplier.get();
            redisOperations.increment(Tags.of("operation", operation, "status", "success"));
            return result;
        } catch (Exception e) {
            redisOperations.increment(Tags.of("operation", operation, "status", "error"));
            throw e;
        } finally {
            sample.stop(redisOperationTimer);
        }
    }
}
```

### **Health Check**

```java
@Component
public class RedisHealthIndicator implements HealthIndicator {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Health health() {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                return connection.ping();
            });
            
            if ("PONG".equals(result)) {
                return Health.up()
                    .withDetail("redis", "Available")
                    .build();
            } else {
                return Health.down()
                    .withDetail("redis", "Unavailable")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## Оптимизация производительности

### **Connection Pooling**

```java
@Configuration
public class OptimizedRedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(2))
            .poolConfig(GenericObjectPoolConfig.builder()
                .maxTotal(20)
                .maxIdle(10)
                .minIdle(5)
                .build())
            .build();
        
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName("localhost");
        serverConfig.setPort(6379);
        
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
}
```

### **Serialization Optimization**

```java
@Configuration
public class OptimizedSerializationConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Использование более эффективной сериализации
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
```

## Безопасность

### **Redis Authentication**

```properties
# Redis Authentication
spring.redis.password=your-password
```

```java
@Configuration
public class SecureRedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setPassword(RedisPassword.of("your-password"));
        
        return new LettuceConnectionFactory(config);
    }
}
```

### **SSL**/**TLS Configuration**

```java
@Configuration
public class SecureRedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        SslOptions sslOptions = SslOptions.builder()
            .truststore(new File("truststore.jks"), "password".toCharArray())
            .build();
        
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .useSsl()
            .sslOptions(sslOptions)
            .build();
        
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName("localhost");
        serverConfig.setPort(6380);
        
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
}
```


## Заключение

**Spring Data Redis** предоставляет мощные инструменты для работы с **Redis**. Правильное использование **RedisTemplate**, репозиториев, **cache abstraction**, **pub**/**sub**, **streams**, **cluster**, **sentinel** и других продвинутых возможностей позволяет создавать эффективные, масштабируемые системы кеширования, **messaging** и хранения данных.

## Дополнительные ресурсы

- [**Spring Data Redis** Documentation](https://docs.spring.io/spring-data/redis/reference/)
- [**Redis** Documentation](https://redis.io/docs/)
- [**Spring Boot** Redis](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.nosql.redis)
- [**Redis** Streams](https://redis.io/docs/data-types/streams/)
- [**Redis** Cluster](https://redis.io/docs/management/scaling/)
