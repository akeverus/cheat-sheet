---
title: "Spring Data R2DBC: Полное руководство по реактивным базам данных"
description: "Комплексное руководство по Spring Data R2DBC: реактивные репозитории, транзакции, connection pooling, testing и best practices"
tags: ["spring", "r2dbc", "reactive", "database", "non-blocking", "java"]
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "spring/spring-webflux.md"]
next: ["spring/spring-webflux.md", "spring/spring-data-jdbc.md"]
updated: "2025-01-16"
related: ["spring/spring-boot.md", "spring/spring-webflux.md"]
---

# Spring Data R2DBC: Полное руководство по реактивным базам данных

## Введение в Spring Data R2DBC

Spring Data R2DBC предоставляет реактивный API для работы с реляционными базами данных. В отличие от традиционного JDBC, R2DBC использует неблокирующий I/O, что делает его идеальным для реактивных приложений.

### Основные возможности

- **Reactive Repositories**: Реактивные репозитории
- **Non-blocking I/O**: Неблокирующий ввод-вывод
- **Reactive Transactions**: Реактивные транзакции
- **Connection Pooling**: Пул соединений
- **Multiple Database Support**: Поддержка различных БД

### Архитектура R2DBC

```
┌─────────────────────────────────────────────────────────┐
│              Application Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Reactive   │  │   Reactive   │  │   Reactive   │  │
│  │   Repository │  │   Service    │  │   Controller │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Data R2DBC                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   R2dbc      │  │   Reactive   │  │   Reactive   │  │
│  │   Entity     │  │   Query      │  │   Transaction│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              R2DBC Driver                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   PostgreSQL │  │   MySQL      │  │   H2         │  │
│  │   Driver     │  │   Driver     │  │   Driver     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Настройка R2DBC

### Зависимости

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
</dependency>
```

### Конфигурация

```properties
# R2DBC Configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/mydb
spring.r2dbc.username=user
spring.r2dbc.password=password
spring.r2dbc.pool.initial-size=5
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m
```

### Java Configuration

```java
@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, 5432)
                .option(ConnectionFactoryOptions.DATABASE, "mydb")
                .option(ConnectionFactoryOptions.USER, "user")
                .option(ConnectionFactoryOptions.PASSWORD, "password")
                .build()
        );
    }
    
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
```

## Reactive Repositories

### Entity Definition

```java
@Table("users")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    private Integer age;
    
    // Getters and setters
}
```

### Repository Interface

```java
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Flux<User> findByAgeGreaterThan(Integer age);
    Mono<Long> countByAge(Integer age);
    Mono<Void> deleteByEmail(String email);
}
```

### Использование Repository

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }
    
    public Mono<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public Mono<User> updateUser(Long id, User user) {
        return userRepository.findById(id)
            .flatMap(existing -> {
                existing.setName(user.getName());
                existing.setEmail(user.getEmail());
                return userRepository.save(existing);
            });
    }
    
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
```

## R2dbcEntityTemplate

### Basic Operations

```java
@Service
public class UserTemplateService {
    
    @Autowired
    private R2dbcEntityTemplate template;
    
    public Mono<User> saveUser(User user) {
        return template.insert(User.class)
            .using(user)
            .then()
            .thenReturn(user);
    }
    
    public Mono<User> findUserById(Long id) {
        return template.select(User.class)
            .matching(Query.query(Criteria.where("id").is(id)))
            .one();
    }
    
    public Flux<User> findAllUsers() {
        return template.select(User.class)
            .all();
    }
    
    public Mono<Long> updateUser(Long id, User user) {
        return template.update(User.class)
            .matching(Query.query(Criteria.where("id").is(id)))
            .apply(Update.update("name", user.getName())
                .set("email", user.getEmail()));
    }
    
    public Mono<Long> deleteUser(Long id) {
        return template.delete(User.class)
            .matching(Query.query(Criteria.where("id").is(id)))
            .all();
    }
}
```

### Custom Queries

```java
@Service
public class CustomQueryService {
    
    @Autowired
    private R2dbcEntityTemplate template;
    
    public Flux<User> findUsersByAge(Integer minAge, Integer maxAge) {
        return template.select(User.class)
            .matching(Query.query(
                Criteria.where("age").between(minAge, maxAge)
            ))
            .all();
    }
    
    public Mono<User> findUserByEmail(String email) {
        return template.select(User.class)
            .matching(Query.query(Criteria.where("email").is(email)))
            .one();
    }
    
    public Flux<User> findUsersWithPagination(int page, int size) {
        return template.select(User.class)
            .matching(Query.empty()
                .page(PageRequest.of(page, size))
                .sort(Sort.by("name").ascending()))
            .all();
    }
}
```

## Reactive Transactions

### Transactional Operations

```java
@Service
public class TransactionalUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionalOperator transactionalOperator;
    
    public Mono<User> createUserWithTransaction(User user) {
        return transactionalOperator.execute(status -> 
            userRepository.save(user)
        );
    }
    
    public Mono<Void> transferBalance(Long fromId, Long toId, BigDecimal amount) {
        return transactionalOperator.execute(status -> 
            userRepository.findById(fromId)
                .flatMap(from -> {
                    from.setBalance(from.getBalance().subtract(amount));
                    return userRepository.save(from);
                })
                .then(userRepository.findById(toId))
                .flatMap(to -> {
                    to.setBalance(to.getBalance().add(amount));
                    return userRepository.save(to);
                })
                .then()
        );
    }
}
```

### Transaction Configuration

```java
@Configuration
public class TransactionConfig {
    
    @Bean
    public TransactionalOperator transactionalOperator(ConnectionFactory connectionFactory) {
        R2dbcTransactionManager transactionManager = new R2dbcTransactionManager(connectionFactory);
        return TransactionalOperator.create(transactionManager);
    }
}
```

## Connection Pooling

### Pool Configuration

```java
@Configuration
public class PoolConfig {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory())
            .initialSize(5)
            .maxSize(20)
            .maxIdleTime(Duration.ofMinutes(30))
            .maxAcquireTime(Duration.ofSeconds(30))
            .maxCreateConnectionTime(Duration.ofSeconds(30))
            .validationQuery("SELECT 1")
            .build();
        
        return new ConnectionPool(poolConfiguration);
    }
    
    private ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, 5432)
                .option(ConnectionFactoryOptions.DATABASE, "mydb")
                .option(ConnectionFactoryOptions.USER, "user")
                .option(ConnectionFactoryOptions.PASSWORD, "password")
                .build()
        );
    }
}
```

## Custom Queries

### @Query Annotation

```java
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    @Query("SELECT * FROM users WHERE age > :age")
    Flux<User> findUsersOlderThan(@Param("age") Integer age);
    
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<User> findByEmail(@Param("email") String email);
    
    @Query("UPDATE users SET name = :name WHERE id = :id")
    Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);
    
    @Query("DELETE FROM users WHERE age < :age")
    Mono<Integer> deleteUsersYoungerThan(@Param("age") Integer age);
}
```

### Native Queries

```java
@Service
public class NativeQueryService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<User> executeNativeQuery(String sql) {
        return databaseClient.sql(sql)
            .map((row, metadata) -> {
                User user = new User();
                user.setId(row.get("id", Long.class));
                user.setName(row.get("name", String.class));
                user.setEmail(row.get("email", String.class));
                return user;
            })
            .all();
    }
}
```

## Мониторинг и метрики

### R2DBC Metrics

```java
@Component
public class R2dbcMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter queriesExecuted;
    private final Timer queryExecutionTime;
    
    public R2dbcMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.queriesExecuted = Counter.builder("r2dbc.queries.executed")
            .description("Number of R2DBC queries executed")
            .register(meterRegistry);
        this.queryExecutionTime = Timer.builder("r2dbc.query.execution.time")
            .description("R2DBC query execution time")
            .register(meterRegistry);
    }
    
    public <T> Mono<T> measureQuery(String operation, Mono<T> query) {
        Timer.Sample sample = Timer.start(meterRegistry);
        return query
            .doOnSuccess(result -> {
                queriesExecuted.increment(Tags.of("operation", operation, "status", "success"));
                sample.stop(queryExecutionTime);
            })
            .doOnError(error -> {
                queriesExecuted.increment(Tags.of("operation", operation, "status", "error"));
                sample.stop(queryExecutionTime);
            });
    }
}
```

## Best Practices

### 1. Используйте реактивные типы

```java
// ✅ Хорошо
public Mono<User> findUser(Long id) {
    return userRepository.findById(id);
}

// ❌ Плохо
public User findUser(Long id) {
    return userRepository.findById(id).block();
}
```

### 2. Обрабатывайте ошибки

```java
// ✅ Хорошо
public Mono<User> findUser(Long id) {
    return userRepository.findById(id)
        .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
        .onErrorResume(error -> {
            log.error("Error finding user", error);
            return Mono.empty();
        });
}
```

### 3. Используйте транзакции для критических операций

```java
// ✅ Хорошо
@Transactional
public Mono<Void> transferBalance(Long from, Long to, BigDecimal amount) {
    // Транзакционные операции
}
```

### 4. Настраивайте connection pooling

```java
// ✅ Хорошо
@Bean
public ConnectionFactory connectionFactory() {
    ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(...)
        .maxSize(20)
        .build();
    return new ConnectionPool(poolConfiguration);
}
```

### 5. Используйте правильные типы данных

```java
// ✅ Хорошо
public Mono<User> findUser(Long id) {
    return userRepository.findById(id);
}

// ❌ Плохо
public Flux<User> findUser(Long id) {
    return userRepository.findById(id);
}
```

## Продвинутые возможности

### Batch Operations

```java
@Service
public class BatchOperationService {
    
    @Autowired
    private R2dbcEntityTemplate template;
    
    public Mono<Integer> batchInsert(List<User> users) {
        return template.insert(User.class)
            .all(users)
            .collectList()
            .map(List::size);
    }
    
    public Mono<Integer> batchUpdate(List<User> users) {
        return Flux.fromIterable(users)
            .flatMap(user -> template.update(User.class)
                .matching(Query.query(Criteria.where("id").is(user.getId())))
                .apply(Update.update("name", user.getName())
                    .set("email", user.getEmail())))
            .reduce(0, (count, updated) -> count + updated);
    }
}
```

### Custom Row Mapping

```java
@Service
public class CustomMappingService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<UserDTO> findUsersWithCustomMapping() {
        return databaseClient.sql("SELECT id, name, email FROM users")
            .map((row, metadata) -> {
                UserDTO dto = new UserDTO();
                dto.setId(row.get("id", Long.class));
                dto.setName(row.get("name", String.class));
                dto.setEmail(row.get("email", String.class));
                return dto;
            })
            .all();
    }
}
```

### Stored Procedures

```java
@Service
public class StoredProcedureService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Mono<String> callStoredProcedure(Long userId) {
        return databaseClient.sql("CALL get_user_name(:userId)")
            .bind("userId", userId)
            .map((row, metadata) -> row.get("name", String.class))
            .one();
    }
}
```

## Интеграция с WebFlux

### Reactive Controller

```java
@RestController
@RequestMapping("/api/users")
public class ReactiveUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.findAllUsers();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        return userService.createUser(user)
            .map(ResponseEntity::ok);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable Long id, 
            @RequestBody User user) {
        return userService.updateUser(id, user)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
            .then(Mono.just(ResponseEntity.ok().build()));
    }
}
```

## Обработка ошибок

### Error Handling

```java
@Service
public class ErrorHandlingService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Mono<User> findUserWithErrorHandling(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .onErrorMap(DataAccessException.class, ex -> 
                new ServiceException("Database error", ex))
            .retry(3)
            .doOnError(error -> log.error("Error finding user", error));
    }
    
    public Mono<User> createUserWithValidation(User user) {
        return validateUser(user)
            .flatMap(validated -> userRepository.save(validated))
            .onErrorResume(DuplicateKeyException.class, ex -> {
                log.warn("User already exists: {}", user.getEmail());
                return Mono.error(new UserAlreadyExistsException(user.getEmail()));
            });
    }
    
    private Mono<User> validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            return Mono.error(new ValidationException("Invalid email"));
        }
        return Mono.just(user);
    }
}
```

## Тестирование

### Test Configuration

```java
@SpringBootTest
@AutoConfigureR2dbc
class R2dbcTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        StepVerifier.create(userRepository.save(user))
            .expectNextMatches(saved -> saved.getId() != null)
            .verifyComplete();
    }
    
    @Test
    void testFindUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        StepVerifier.create(
            userRepository.save(user)
                .flatMap(saved -> userRepository.findById(saved.getId()))
        )
            .expectNextMatches(found -> found.getName().equals("Test User"))
            .verifyComplete();
    }
}
```

### Embedded Database

```java
@SpringBootTest
@AutoConfigureR2dbc
class EmbeddedR2dbcTest {
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        public ConnectionFactory connectionFactory() {
            return new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                    .url("mem:testdb")
                    .build()
            );
        }
    }
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testWithEmbeddedDatabase() {
        // Тесты с встроенной БД
    }
}
```

## Производительность

### Оптимизация запросов

```java
@Service
public class OptimizedQueryService {
    
    @Autowired
    private R2dbcEntityTemplate template;
    
    public Flux<User> findUsersOptimized() {
        return template.select(User.class)
            .matching(Query.query(Criteria.where("active").is(true))
                .limit(100)
                .offset(0)
                .sort(Sort.by("name").ascending()))
            .all()
            .take(100); // Ограничение на уровне приложения
    }
    
    public Mono<User> findUserWithProjection(Long id) {
        return template.select(User.class)
            .matching(Query.query(Criteria.where("id").is(id))
                .columns("id", "name", "email")) // Только нужные поля
            .one();
    }
}
```

### Connection Pool Tuning

```java
@Configuration
public class OptimizedPoolConfig {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory())
            .initialSize(10)
            .maxSize(50)
            .maxIdleTime(Duration.ofMinutes(20))
            .maxAcquireTime(Duration.ofSeconds(10))
            .maxCreateConnectionTime(Duration.ofSeconds(10))
            .validationQuery("SELECT 1")
            .backgroundEvictionInterval(Duration.ofMinutes(5))
            .build();
        
        return new ConnectionPool(poolConfiguration);
    }
}
```

## Продвинутые запросы

### Complex Joins

```java
@Service
public class JoinQueryService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<UserOrderDTO> findUsersWithOrders() {
        return databaseClient.sql("""
            SELECT u.id, u.name, u.email, o.id as order_id, o.total
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            """)
            .map((row, metadata) -> {
                UserOrderDTO dto = new UserOrderDTO();
                dto.setUserId(row.get("id", Long.class));
                dto.setUserName(row.get("name", String.class));
                dto.setUserEmail(row.get("email", String.class));
                dto.setOrderId(row.get("order_id", Long.class));
                dto.setOrderTotal(row.get("total", BigDecimal.class));
                return dto;
            })
            .all();
    }
}
```

### Aggregation Queries

```java
@Service
public class AggregationService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Mono<OrderStatistics> getOrderStatistics() {
        return databaseClient.sql("""
            SELECT 
                COUNT(*) as total_orders,
                SUM(total) as total_amount,
                AVG(total) as average_amount,
                MAX(total) as max_amount,
                MIN(total) as min_amount
            FROM orders
            """)
            .map((row, metadata) -> {
                OrderStatistics stats = new OrderStatistics();
                stats.setTotalOrders(row.get("total_orders", Long.class));
                stats.setTotalAmount(row.get("total_amount", BigDecimal.class));
                stats.setAverageAmount(row.get("average_amount", BigDecimal.class));
                stats.setMaxAmount(row.get("max_amount", BigDecimal.class));
                stats.setMinAmount(row.get("min_amount", BigDecimal.class));
                return stats;
            })
            .one();
    }
}
```

### Subqueries

```java
@Service
public class SubqueryService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<User> findUsersWithRecentOrders() {
        return databaseClient.sql("""
            SELECT * FROM users u
            WHERE u.id IN (
                SELECT DISTINCT user_id FROM orders
                WHERE created_at > CURRENT_DATE - INTERVAL '30 days'
            )
            """)
            .map((row, metadata) -> mapToUser(row))
            .all();
    }
}
```

### Window Functions

```java
@Service
public class WindowFunctionService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<UserRanking> getUserRankings() {
        return databaseClient.sql("""
            SELECT 
                id, name, email, balance,
                ROW_NUMBER() OVER (ORDER BY balance DESC) as rank,
                RANK() OVER (ORDER BY balance DESC) as rank_with_ties,
                DENSE_RANK() OVER (ORDER BY balance DESC) as dense_rank
            FROM users
            """)
            .map((row, metadata) -> {
                UserRanking ranking = new UserRanking();
                ranking.setUserId(row.get("id", Long.class));
                ranking.setUserName(row.get("name", String.class));
                ranking.setBalance(row.get("balance", BigDecimal.class));
                ranking.setRank(row.get("rank", Long.class));
                return ranking;
            })
            .all();
    }
}
```

### CTE (Common Table Expressions)

```java
@Service
public class CteService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Flux<User> findUsersWithCTE() {
        return databaseClient.sql("""
            WITH active_users AS (
                SELECT * FROM users WHERE active = true
            ),
            users_with_orders AS (
                SELECT DISTINCT u.* FROM active_users u
                INNER JOIN orders o ON u.id = o.user_id
            )
            SELECT * FROM users_with_orders
            """)
            .map((row, metadata) -> mapToUser(row))
            .all();
    }
}
```

## Реактивные транзакции (расширенные)

### Nested Transactions

```java
@Service
public class NestedTransactionService {
    
    @Autowired
    private TransactionalOperator transactionalOperator;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    public Mono<Void> processOrderWithNestedTransactions(Long userId, Order order) {
        return transactionalOperator.execute(status -> 
            userRepository.findById(userId)
                .flatMap(user -> {
                    // Вложенная транзакция
                    return transactionalOperator.execute(nestedStatus ->
                        orderRepository.save(order)
                            .then(userRepository.save(user))
                    );
                })
                .then()
        );
    }
}
```

### Transaction Propagation

```java
@Service
public class TransactionPropagationService {
    
    @Autowired
    private TransactionalOperator transactionalOperator;
    
    public Mono<Void> methodWithRequired() {
        return transactionalOperator.execute(status -> 
            // Транзакция будет использована, если существует, или создана новая
            performOperation()
        );
    }
    
    public Mono<Void> methodWithNewTransaction() {
        // Всегда создается новая транзакция
        TransactionalOperator newOperator = TransactionalOperator.create(
            new R2dbcTransactionManager(connectionFactory()));
        return newOperator.execute(status -> performOperation());
    }
}
```

## Реактивные миграции

### Database Migrations

```java
@Service
public class ReactiveMigrationService {
    
    @Autowired
    private DatabaseClient databaseClient;
    
    public Mono<Void> migrateDatabase() {
        return databaseClient.sql("""
            CREATE TABLE IF NOT EXISTS users (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """)
            .fetch()
            .rowsUpdated()
            .then(databaseClient.sql("""
                CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)
                """)
                .fetch()
                .rowsUpdated())
            .then();
    }
}
```

## Продвинутые паттерны

### Reactive Repository Customization

```java
public interface CustomUserRepository extends ReactiveCrudRepository<User, Long> {
    
    @Query("SELECT * FROM users WHERE age > :age")
    Flux<User> findUsersOlderThan(@Param("age") Integer age);
    
    @Modifying
    @Query("UPDATE users SET active = :active WHERE id = :id")
    Mono<Integer> updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);
    
    Mono<User> findByEmailAndAge(String email, Integer age);
    
    Flux<User> findByNameContainingIgnoreCase(String name);
}

@Service
public class CustomRepositoryService {
    
    @Autowired
    private CustomUserRepository userRepository;
    
    public Flux<User> findActiveUsersOlderThan(Integer age) {
        return userRepository.findUsersOlderThan(age)
            .filter(user -> user.getActive());
    }
}
```

### Reactive Caching

```java
@Service
public class CachedUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final Cache<Long, Mono<User>> userCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build();
    
    public Mono<User> findUserById(Long id) {
        return userCache.get(id, key -> 
            userRepository.findById(key)
                .cache()
        );
    }
}
```

### Reactive Error Recovery

```java
@Service
public class ResilientUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Mono<User> findUserWithRetry(Long id) {
        return userRepository.findById(id)
            .retry(3)
            .retryBackoff(3, Duration.ofSeconds(1))
            .onErrorResume(DataAccessException.class, ex -> {
                log.error("Database error, returning default", ex);
                return Mono.just(createDefaultUser());
            })
            .timeout(Duration.ofSeconds(5))
            .onErrorReturn(TimeoutException.class, createDefaultUser());
    }
    
    private User createDefaultUser() {
        User user = new User();
        user.setId(-1L);
        user.setName("Unknown");
        return user;
    }
}
```

### Reactive Batch Processing

```java
@Service
public class BatchProcessingService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Mono<Integer> processUsersInBatches(int batchSize) {
        return userRepository.findAll()
            .buffer(batchSize)
            .flatMap(batch -> processBatch(batch), 5) // Параллельная обработка 5 батчей
            .reduce(0, Integer::sum);
    }
    
    private Mono<Integer> processBatch(List<User> batch) {
        return Flux.fromIterable(batch)
            .flatMap(this::processUser)
            .reduce(0, (count, processed) -> count + 1);
    }
    
    private Mono<Boolean> processUser(User user) {
        return Mono.just(true);
    }
}
```

### Reactive Transactions with Retry

```java
@Service
public class TransactionalRetryService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionalOperator transactionalOperator;
    
    public Mono<Void> transferWithRetry(Long fromId, Long toId, BigDecimal amount) {
        return Mono.defer(() -> 
            transactionalOperator.execute(status -> 
                userRepository.findById(fromId)
                    .flatMap(from -> {
                        from.setBalance(from.getBalance().subtract(amount));
                        return userRepository.save(from);
                    })
                    .then(userRepository.findById(toId))
                    .flatMap(to -> {
                        to.setBalance(to.getBalance().add(amount));
                        return userRepository.save(to);
                    })
                    .then()
            )
        )
        .retry(3)
        .retryBackoff(3, Duration.ofSeconds(1));
    }
}
```

## Заключение

Spring Data R2DBC предоставляет мощные инструменты для работы с реляционными базами данных в реактивном стиле. Правильное использование реактивных репозиториев, R2dbcEntityTemplate, транзакций, connection pooling, batch операций, сложных запросов (joins, aggregations, subqueries, window functions, CTE), интеграции с WebFlux, обработки ошибок, тестирования, оптимизации производительности, миграций, кеширования, error recovery, batch processing и других продвинутых возможностей позволяет создавать высокопроизводительные неблокирующие приложения с эффективной работой с базами данных.

## Дополнительные ресурсы

- [Spring Data R2DBC Documentation](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [R2DBC Specification](https://r2dbc.io/)
- [Spring Boot R2DBC](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.r2dbc)
- [R2DBC Drivers](https://r2dbc.io/drivers/)
- [Reactive Programming](https://projectreactor.io/docs/core/release/reference/)
- [PostgreSQL R2DBC](https://github.com/pgjdbc/r2dbc-postgresql)
- [MySQL R2DBC](https://github.com/mirromutth/r2dbc-mysql)

