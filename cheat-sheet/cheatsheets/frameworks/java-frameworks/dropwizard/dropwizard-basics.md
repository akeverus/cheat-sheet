---
title: "Dropwizard: Основы"
description: "Java фреймворк для создания RESTful веб-сервисов с встроенными метриками, логированием и операционными инструментами"
tags: ["dropwizard", "java", "rest", "microservices", "metrics", "logging"]
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "spring/spring-boot.md"]
next: ["dropwizard-core.md", "dropwizard-jersey.md"]
updated: "2026-01-16"
related: ["spring/spring-boot.md", "micronaut/micronaut-basics.md"]
---

# Dropwizard: Основы

## Введение в Dropwizard

Dropwizard - это Java фреймворк для создания высокопроизводительных RESTful веб-сервисов. Dropwizard объединяет проверенные библиотеки Java экосистемы в единый пакет для создания production-ready приложений.

### Основные особенности

- **Production-ready**: Встроенные метрики, логирование, health checks
- **Lightweight**: Минимальные зависимости
- **Jersey**: RESTful веб-сервисы через JAX-RS
- **Metrics**: Встроенная поддержка метрик (Metrics library)
- **Logging**: Настроенное логирование через Logback и SLF4J
- **Configuration**: Type-safe конфигурация через YAML

### Компоненты Dropwizard

- **Jersey**: RESTful веб-сервисы
- **Jetty**: HTTP сервер
- **Jackson**: JSON обработка
- **Metrics**: Метрики и мониторинг
- **Logback**: Логирование
- **Hibernate Validator**: Валидация

---

## Установка и настройка

### Maven зависимость

```xml
<dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Gradle зависимость

```gradle
dependencies {
    implementation 'io.dropwizard:dropwizard-core:4.0.0'
}
```

### Структура проекта

```
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           ├── HelloWorldApplication.java
│       │           ├── HelloWorldConfiguration.java
│       │           └── resources/
│       │               └── HelloWorldResource.java
│       └── resources/
│           └── config.yml
└── pom.xml
```

---

## Создание приложения

### Application класс

```java
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }
    
    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Инициализация приложения
    }
    
    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        // Регистрация ресурсов, health checks, и т.д.
        final HelloWorldResource resource = new HelloWorldResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );
        environment.jersey().register(resource);
    }
}
```

### Configuration класс

```java
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class HelloWorldConfiguration extends Configuration {
    
    @NotEmpty
    private String template;
    
    @NotEmpty
    private String defaultName = "Stranger";
    
    @JsonProperty
    public String getTemplate() {
        return template;
    }
    
    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }
    
    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }
    
    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
```

### YAML конфигурация

```yaml
template: Hello, %s!
defaultName: Dropwizard

server:
  type: simple
  connector:
    type: http
    port: 8080

logging:
  level: INFO
  loggers:
    com.example: DEBUG
```

### REST Resource

```java
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
```

### Модель данных

```java
import com.fasterxml.jackson.annotation.JsonProperty;

public class Saying {
    private long id;
    private String content;

    public Saying() {
        // Jackson deserialization
    }

    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}
```

---

## Запуск приложения

### Команда запуска

```bash
java -jar target/hello-world-1.0.0.jar server config.yml
```

### Доступные команды

- `server`: Запуск сервера
- `check`: Проверка конфигурации
- `db migrate`: Миграции базы данных (если используется)

---

## Health Checks

### Создание Health Check

```java
import com.codahale.metrics.health.HealthCheck;

public class DatabaseHealthCheck extends HealthCheck {
    private final Database database;

    public DatabaseHealthCheck(Database database) {
        this.database = database;
    }

    @Override
    protected Result check() throws Exception {
        if (database.isConnected()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot connect to database");
        }
    }
}
```

### Регистрация Health Check

```java
@Override
public void run(HelloWorldConfiguration configuration, Environment environment) {
    final DatabaseHealthCheck healthCheck = new DatabaseHealthCheck(database);
    environment.healthChecks().register("database", healthCheck);
}
```

### Проверка health

```bash
curl http://localhost:8080/healthcheck
```

---

## Метрики

### Использование Metrics

```java
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;

public class MetricsExample {
    private final Counter requests;
    private final Timer responses;

    public MetricsExample(MetricRegistry metrics) {
        this.requests = metrics.counter("requests");
        this.responses = metrics.timer("responses");
    }

    public void handleRequest() {
        requests.inc();
        Timer.Context context = responses.time();
        try {
            // Обработка запроса
        } finally {
            context.stop();
        }
    }
}
```

### Доступ к метрикам

```bash
curl http://localhost:8080/metrics
```

---

## Логирование

### Использование Logger

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);

    public void example() {
        logger.info("Info message");
        logger.debug("Debug message");
        logger.warn("Warning message");
        logger.error("Error message", new Exception("Error"));
    }
}
```

### Конфигурация логирования

```yaml
logging:
  level: INFO
  loggers:
    com.example: DEBUG
    org.hibernate: WARN
  appenders:
    - type: console
    - type: file
      currentLogFilename: ./logs/application.log
      archivedLogFilenamePattern: ./logs/application-%d.log.gz
      archivedFileCount: 5
```

---

## База данных

### JDBI Integration

```java
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jdbi3.JdbiFactory;

public class DatabaseExample extends Application<HelloWorldConfiguration> {
    
    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        JdbiFactory factory = new JdbiFactory();
        Jdbi jdbi = factory.build(env, config.getDataSourceFactory(), "postgresql");
        
        UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        env.jersey().register(new UserResource(userDAO));
    }
}

public interface UserDAO {
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    User findById(@Bind("id") long id);
    
    @SqlUpdate("INSERT INTO users (name, email) VALUES (:name, :email)")
    void insert(@BindBean User user);
}
```

### Hibernate Integration

```java
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;

public class HibernateExample extends Application<HelloWorldConfiguration> {
    
    private final HibernateBundle<HelloWorldConfiguration> hibernate = 
        new HibernateBundle<HelloWorldConfiguration>(User.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration config) {
                return config.getDataSourceFactory();
            }
        };
    
    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }
    
    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        SessionFactory sessionFactory = hibernate.getSessionFactory();
        UserDAO userDAO = new UserDAO(sessionFactory);
        env.jersey().register(new UserResource(userDAO));
    }
}
```

### Миграции базы данных

```java
import io.dropwizard.db.DataSourceFactory;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class MigrationExample extends Application<HelloWorldConfiguration> {
    
    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();
        DataSource dataSource = dataSourceFactory.build(env.metrics(), "migrations");
        
        try (Connection connection = dataSource.getConnection()) {
            Liquibase liquibase = new Liquibase("migrations.xml",
                new ClassLoaderResourceAccessor(),
                new JdbcConnection(connection));
            liquibase.update("");
        }
    }
}
```

---

## REST API Advanced

### Content Negotiation

```java
@Path("/api")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ContentNegotiationResource {
    
    @GET
    @Path("/users")
    public List<User> getUsers(@Context HttpHeaders headers) {
        List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
        // Возврат данных в зависимости от Accept заголовка
        return getAllUsers();
    }
}
```

### Параметры запроса

```java
@Path("/api/users")
public class QueryParamsResource {
    
    @GET
    public List<User> getUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("id") String sort,
            @QueryParam("order") @DefaultValue("asc") String order) {
        // Пагинация и сортировка
        return getUsersPaginated(page, size, sort, order);
    }
}
```

### Path параметры

```java
@Path("/api/users")
public class PathParamsResource {
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") long id) {
        return findUserById(id);
    }
    
    @GET
    @Path("/{id}/posts/{postId}")
    public Post getPost(@PathParam("id") long userId, 
                       @PathParam("postId") long postId) {
        return findPost(userId, postId);
    }
}
```

### Matrix параметры

```java
@GET
@Path("/search")
public List<Result> search(@MatrixParam("q") String query,
                           @MatrixParam("type") String type) {
    return performSearch(query, type);
}
```

---

## Валидация

### Bean Validation

```java
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class User {
    @NotNull
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    
    @NotNull
    @Email
    private String email;
    
    // Getters and setters
}

@Path("/api/users")
public class ValidationResource {
    
    @POST
    public Response createUser(@Valid User user) {
        // User автоматически валидируется
        return Response.status(201).entity(user).build();
    }
}
```

### Custom Validators

```java
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("\\d{10}");
    }
}
```

---

## Exception Handling

### Exception Mapper

```java
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {
    
    @Override
    public Response toResponse(UserNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(404, exception.getMessage());
        return Response.status(404).entity(error).build();
    }
}

// Регистрация
env.jersey().register(new UserNotFoundExceptionMapper());
```

### WebApplicationException

```java
@GET
@Path("/users/{id}")
public User getUser(@PathParam("id") long id) {
    User user = findUserById(id);
    if (user == null) {
        throw new WebApplicationException(
            Response.status(404).entity("User not found").build());
    }
    return user;
}
```

---

## Filters и Interceptors

### Request Filter

```java
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String token = requestContext.getHeaderString("Authorization");
        if (token == null || !isValidToken(token)) {
            requestContext.abortWith(
                Response.status(401).entity("Unauthorized").build());
        }
    }
    
    private boolean isValidToken(String token) {
        // Проверка токена
        return true;
    }
}
```

### Response Filter

```java
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerResponseContext;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", 
            "Content-Type, Authorization");
    }
}
```

### Reader Interceptor

```java
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class LoggingReaderInterceptor implements ReaderInterceptor {
    
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException {
        System.out.println("Reading entity: " + context.getGenericType());
        return context.proceed();
    }
}
```

---

## Тестирование

### Integration Testing

```java
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {
    
    private static final ResourceExtension EXT = ResourceExtension.builder()
        .addResource(new UserResource(userDAO))
        .build();
    
    @Test
    void testGetUser() {
        User user = EXT.target("/users/1").request().get(User.class);
        assertNotNull(user);
        assertEquals(1L, user.getId());
    }
}
```

### Application Testing

```java
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {
    
    private static final DropwizardAppExtension<HelloWorldConfiguration> EXT =
        new DropwizardAppExtension<>(HelloWorldApplication.class, "config.yml");
    
    @Test
    void testApplication() {
        Client client = EXT.client();
        Response response = client.target("http://localhost:" + EXT.getLocalPort() + "/hello-world")
            .request()
            .get();
        
        assertEquals(200, response.getStatus());
    }
}
```

---

## Конфигурация

### Продвинутая конфигурация

```java
public class AdvancedConfiguration extends Configuration {
    
    @JsonProperty
    private DatabaseConfiguration database;
    
    @JsonProperty
    private RedisConfiguration redis;
    
    @JsonProperty
    private KafkaConfiguration kafka;
    
    // Getters and setters
}

public class DatabaseConfiguration {
    @JsonProperty
    private String host;
    
    @JsonProperty
    private int port;
    
    @JsonProperty
    private String database;
    
    @JsonProperty
    private PoolConfiguration pool;
    
    // Getters and setters
}
```

### Environment Variables

```yaml
database:
  host: ${DB_HOST:localhost}
  port: ${DB_PORT:5432}
  user: ${DB_USER}
  password: ${DB_PASSWORD}
```

### Configuration Validation

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    bootstrap.setConfigurationFactoryFactory(new CustomConfigurationFactoryFactory());
}
```

---

## Безопасность

### Basic Authentication

```java
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;

@Path("/api")
public class SecureResource {
    
    @GET
    @Path("/protected")
    @PermitAll
    public String getProtected(@Auth User user) {
        return "Hello " + user.getName();
    }
}

// Настройка в Application
env.jersey().register(new AuthDynamicFeature(
    new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(new BasicAuthenticator())
        .setAuthorizer(new SimpleAuthorizer())
        .buildAuthFilter()));
```

### JWT Authentication

```java
import io.dropwizard.auth.jwt.JwtAuthFilter;
import io.dropwizard.auth.jwt.JwtAuthenticator;

env.jersey().register(new AuthDynamicFeature(
    new JwtAuthFilter.Builder<User>()
        .setTokenExtractor(new BearerTokenExtractor())
        .setAuthenticator(new JwtAuthenticator())
        .buildAuthFilter()));
```

---

## Кеширование

### HTTP Caching

```java
@GET
@Path("/users/{id}")
public Response getUser(@PathParam("id") long id) {
    User user = findUserById(id);
    CacheControl cacheControl = new CacheControl();
    cacheControl.setMaxAge(3600);
    cacheControl.setPrivate(true);
    
    return Response.ok(user)
        .cacheControl(cacheControl)
        .build();
}
```

### Application-level Caching

```java
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CachedUserService {
    private final Cache<Long, User> cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build();
    
    public User getUser(long id) {
        try {
            return cache.get(id, () -> loadUserFromDatabase(id));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

## Логирование Advanced

### Structured Logging

```java
import net.logstash.logback.marker.Markers;

logger.info(Markers.append("userId", user.getId())
    .and(Markers.append("action", "login")),
    "User logged in");
```

### MDC (Mapped Diagnostic Context)

```java
import org.slf4j.MDC;

public class MDCExample {
    public void handleRequest(String requestId, String userId) {
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        try {
            logger.info("Processing request");
            // Обработка
        } finally {
            MDC.clear();
        }
    }
}
```

---

## Мониторинг

### Custom Metrics

```java
import com.codahale.metrics.Meter;
import com.codahale.metrics.Histogram;

public class CustomMetricsExample {
    private final Meter requests;
    private final Histogram responseSizes;
    
    public CustomMetricsExample(MetricRegistry metrics) {
        this.requests = metrics.meter("requests");
        this.responseSizes = metrics.histogram("response-sizes");
    }
    
    public void handleRequest(int responseSize) {
        requests.mark();
        responseSizes.update(responseSize);
    }
}
```

### Health Checks Advanced

```java
public class DatabaseHealthCheck extends HealthCheck {
    private final Database database;
    
    @Override
    protected Result check() {
        try {
            if (database.isConnected()) {
                long responseTime = database.ping();
                if (responseTime < 100) {
                    return Result.healthy("Database is healthy");
                } else {
                    return Result.unhealthy("Database is slow: " + responseTime + "ms");
                }
            } else {
                return Result.unhealthy("Cannot connect to database");
            }
        } catch (Exception e) {
            return Result.unhealthy(e);
        }
    }
}
```

---

## Развертывание

### Docker

```dockerfile
FROM openjdk:11-jre-slim
COPY target/myapp-*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "server", "/app/config.yml"]
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dropwizard-app
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: app
        image: myapp:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db-host
```

---

## Tasks

### Создание Task

```java
import io.dropwizard.servlets.tasks.Task;

public class CleanupTask extends Task {
    public CleanupTask() {
        super("cleanup");
    }
    
    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) {
        output.println("Starting cleanup...");
        // Выполнение задачи
        output.println("Cleanup completed");
    }
}

// Регистрация
env.admin().addTask(new CleanupTask());
```

### Доступ к Task

```bash
curl -X POST http://localhost:8081/tasks/cleanup
```

---

## Bundles

### Создание Bundle

```java
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MyBundle implements ConfiguredBundle<HelloWorldConfiguration> {
    
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Инициализация bundle
    }
    
    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        // Настройка bundle
    }
}

// Регистрация
bootstrap.addBundle(new MyBundle());
```

---

## CLI Commands

### Создание Command

```java
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

public class MyCommand extends Command {
    public MyCommand() {
        super("mycommand", "My custom command");
    }
    
    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        // Выполнение команды
        System.out.println("Command executed");
    }
}

// Регистрация
bootstrap.addCommand(new MyCommand());
```

---

## HTTP Client

### Jersey Client

```java
import javax.ws.rs.client.Client;
import javax.wsizard.client.JerseyClientBuilder;

public class HttpClientExample {
    public static void main(String[] args) {
        Client client = new JerseyClientBuilder(env)
            .build("my-client");
        
        String response = client.target("http://api.example.com/data")
            .request()
            .get(String.class);
    }
}
```

---

## Async Processing

### AsyncResponse

```java
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/api")
public class AsyncResource {
    
    @GET
    @Path("/async")
    public void getAsync(@Suspended AsyncResponse asyncResponse) {
        CompletableFuture.supplyAsync(() -> {
            // Долгая операция
            return fetchData();
        }).thenAccept(data -> {
            asyncResponse.resume(data);
        }).exceptionally(throwable -> {
            asyncResponse.resume(Response.status(500).entity(throwable.getMessage()).build());
            return null;
        });
    }
}
```

---

## Advanced Patterns

### Service Layer Pattern

```java
public class UserService {
    private final UserDAO userDAO;
    private final MetricRegistry metrics;
    
    public UserService(UserDAO userDAO, MetricRegistry metrics) {
        this.userDAO = userDAO;
        this.metrics = metrics;
    }
    
    public User getUser(long id) {
        Timer.Context context = metrics.timer("user.get").time();
        try {
            return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        } finally {
            context.stop();
        }
    }
    
    public User createUser(User user) {
        validateUser(user);
        return userDAO.save(user);
    }
    
    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Name is required");
        }
    }
}
```

### Repository Pattern

```java
public interface UserRepository {
    Optional<User> findById(long id);
    List<User> findAll();
    User save(User user);
    void delete(long id);
}

public class UserRepositoryImpl implements UserRepository {
    private final UserDAO userDAO;
    
    public UserRepositoryImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    @Override
    public Optional<User> findById(long id) {
        return userDAO.findById(id);
    }
    
    // Реализация остальных методов
}
```

### DTO Pattern

```java
public class UserDTO {
    private long id;
    private String name;
    private String email;
    
    // Getters and setters
}

public class UserMapper {
    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    public static User fromDTO(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
```

---

## Performance Optimization

### Connection Pooling

```java
public class DatabaseConfig extends Configuration {
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();
    
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }
}

// В YAML
database:
  driverClass: org.postgresql.Driver
  url: jdbc:postgresql://localhost:5432/mydb
  user: user
  password: password
  properties:
    charSet: UTF-8
  maxWaitForConnection: 1s
  validationQuery: "SELECT 1"
  validationQueryTimeout: 3s
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: false
  evictionInterval: 10s
  minIdleTime: 1 minute
```

### Query Optimization

```java
@SqlQuery("SELECT * FROM users WHERE id = :id")
@UseRowMapper(UserMapper.class)
User findById(@Bind("id") long id);

@SqlQuery("SELECT * FROM users WHERE name LIKE :name")
@UseRowMapper(UserMapper.class)
List<User> findByName(@Bind("name") String name);

@SqlUpdate("UPDATE users SET name = :name WHERE id = :id")
void updateName(@Bind("id") long id, @Bind("name") String name);
```

### Caching Strategy

```java
public class CachedUserService {
    private final Cache<Long, User> userCache;
    private final UserDAO userDAO;
    
    public CachedUserService(UserDAO userDAO, MetricRegistry metrics) {
        this.userDAO = userDAO;
        this.userCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();
        
        metrics.register("cache.stats", new Gauge<CacheStats>() {
            @Override
            public CacheStats getValue() {
                return userCache.stats();
            }
        });
    }
    
    public User getUser(long id) {
        try {
            return userCache.get(id, () -> userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

## Security Advanced

### OAuth2 Integration

```java
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;

env.jersey().register(new AuthDynamicFeature(
    new OAuthCredentialAuthFilter.Builder<User>()
        .setAuthenticator(new OAuthAuthenticator())
        .setAuthorizer(new SimpleAuthorizer())
        .setPrefix("Bearer")
        .buildAuthFilter()));
```

### Role-Based Access Control

```java
@Path("/api")
public class SecureResource {
    
    @GET
    @Path("/admin")
    @RolesAllowed("ADMIN")
    public String getAdminData(@Auth User user) {
        return "Admin data";
    }
    
    @GET
    @Path("/user")
    @RolesAllowed({"USER", "ADMIN"})
    public String getUserData(@Auth User user) {
        return "User data";
    }
}
```

### CORS Configuration

```java
import io.dropwizard.jersey.cors.CorsFilter;

CorsFilter corsFilter = new CorsFilter.Builder()
    .allowedOrigins("*")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("Content-Type", "Authorization")
    .allowCredentials(true)
    .maxAge(3600)
    .build();

env.jersey().register(corsFilter);
```

---

## Testing Advanced

### Mocking Dependencies

```java
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    
    @Mock
    private UserService userService;
    
    private UserResource resource;
    
    @BeforeEach
    void setUp() {
        resource = new UserResource(userService);
    }
    
    @Test
    void testGetUser() {
        User user = new User(1L, "John");
        when(userService.getUser(1L)).thenReturn(user);
        
        User result = resource.getUser(1L);
        assertEquals("John", result.getName());
    }
}
```

### Integration Testing with Testcontainers

```java
import org.testcontainers.containers.PostgreSQLContainer;

public class IntegrationTest {
    private static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @BeforeAll
    static void setUp() {
        postgres.start();
    }
    
    @AfterAll
    static void tearDown() {
        postgres.stop();
    }
}
```

---

## Monitoring and Observability

### Custom Metrics

```java
import com.codahale.metrics.Meter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Gauge;

public class CustomMetrics {
    private final Meter requests;
    private final Histogram responseSizes;
    private final Gauge<Integer> activeConnections;
    
    public CustomMetrics(MetricRegistry metrics) {
        this.requests = metrics.meter("requests");
        this.responseSizes = metrics.histogram("response-sizes");
        this.activeConnections = metrics.gauge("active-connections", 
            () -> getActiveConnectionCount());
    }
}
```

### Prometheus Integration

```java
import io.prometheus.client.dropwizard.DropwizardExports;

env.jersey().register(new DropwizardExports(env.metrics()));
```

---

## Production Deployment

### JVM Tuning

```bash
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar myapp.jar server config.yml
```

### Graceful Shutdown

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    System.out.println("Shutting down...");
    // Cleanup resources
}));
```

### Health Check Endpoint

```bash
curl http://localhost:8080/healthcheck
```

### Metrics Endpoint

```bash
curl http://localhost:8080/metrics
```

---

## Troubleshooting

### Common Issues

1. **Out of Memory**: Увеличить heap size
2. **Connection Pool Exhausted**: Увеличить pool size
3. **Slow Queries**: Оптимизировать запросы, добавить индексы
4. **High CPU**: Проверить метрики, профилировать код

### Debugging

```yaml
logging:
  level: DEBUG
  loggers:
    com.example: TRACE
```

---

## Best Practices

### 1. Структура проекта

```
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           ├── MyApplication.java
│       │           ├── MyConfiguration.java
│       │           ├── resources/
│       │           │   └── UserResource.java
│       │           ├── services/
│       │           │   └── UserService.java
│       │           ├── daos/
│       │           │   └── UserDAO.java
│       │           └── models/
│       │               └── User.java
│       └── resources/
│           ├── config.yml
│           └── migrations/
│               └── migrations.xml
└── pom.xml
```

### 2. Dependency Injection

```java
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceExample extends Application<HelloWorldConfiguration> {
    
    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        Injector injector = Guice.createInjector(new MyModule(config, env));
        env.jersey().register(injector.getInstance(UserResource.class));
    }
}
```

### 3. Error Handling

```java
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        ErrorMessage error = new ErrorMessage(
            500,
            exception.getMessage(),
            exception.getClass().getSimpleName()
        );
        return Response.status(500).entity(error).build();
    }
}
```

### 4. Resource Management

```java
public class ResourceManagementExample {
    private final DataSource dataSource;
    
    @PostConstruct
    public void init() {
        // Инициализация ресурсов
    }
    
    @PreDestroy
    public void cleanup() {
        // Очистка ресурсов
        if (dataSource instanceof Closeable) {
            ((Closeable) dataSource).close();
        }
    }
}
```

---

## Performance Optimization

### Connection Pool Tuning

```yaml
database:
  maxWaitForConnection: 1s
  validationQuery: "SELECT 1"
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: false
  evictionInterval: 10s
  minIdleTime: 1 minute
```

### JVM Optimization

```bash
java -server \
     -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/heapdump.hprof \
     -jar myapp.jar server config.yml
```

### Query Optimization

```java
@SqlQuery("SELECT * FROM users WHERE id = :id")
@UseRowMapper(UserMapper.class)
User findById(@Bind("id") long id);

@SqlQuery("SELECT * FROM users WHERE name = :name AND active = true")
@UseRowMapper(UserMapper.class)
List<User> findActiveByName(@Bind("name") String name);
```

---

## Advanced Topics

### Custom Message Body Writers

```java
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("application/xml")
public class XMLMessageBodyWriter implements MessageBodyWriter<User> {
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, 
                               Annotation[] annotations, MediaType mediaType) {
        return User.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(User user, Class<?> type, Type genericType,
                       Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException {
        // Запись в XML формат
    }
}
```

### Custom Message Body Readers

```java
@Provider
@Consumes("application/xml")
public class XMLMessageBodyReader implements MessageBodyReader<User> {
    
    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
        return User.class.isAssignableFrom(type);
    }
    
    @Override
    public User readFrom(Class<User> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders,
                        InputStream entityStream) throws IOException {
        // Чтение из XML формата
        return null;
    }
}
```

### Lifecycle Management

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class LifecycleExample {
    private DataSource dataSource;
    
    @PostConstruct
    public void init() {
        // Инициализация после создания бина
        dataSource = createDataSource();
    }
    
    @PreDestroy
    public void cleanup() {
        // Очистка перед уничтожением бина
        if (dataSource instanceof Closeable) {
            ((Closeable) dataSource).close();
        }
    }
}
```

---

## Production Checklist

### Performance

- [ ] Настроены connection pools
- [ ] Оптимизированы запросы к БД
- [ ] Настроено кеширование
- [ ] Настроены метрики
- [ ] Настроен мониторинг

### Security

- [ ] Настроена аутентификация
- [ ] Настроена авторизация
- [ ] Настроены CORS
- [ ] Настроены security headers
- [ ] Проведен security audit

### Monitoring

- [ ] Настроены health checks
- [ ] Настроены метрики
- [ ] Настроено логирование
- [ ] Настроены алерты
- [ ] Настроен distributed tracing

### Deployment

- [ ] Настроен CI/CD
- [ ] Настроен Docker
- [ ] Настроен Kubernetes
- [ ] Настроен мониторинг
- [ ] Настроен backup

---

## Real-World Examples

### Complete REST API

```java
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserService userService;
    private final MetricRegistry metrics;
    
    public UserResource(UserService userService, MetricRegistry metrics) {
        this.userService = userService;
        this.metrics = metrics;
    }
    
    @GET
    public List<UserDTO> getAllUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Timer.Context context = metrics.timer("users.getAll").time();
        try {
            return userService.getAllUsers(page, size);
        } finally {
            context.stop();
        }
    }
    
    @GET
    @Path("/{id}")
    public UserDTO getUser(@PathParam("id") long id) {
        return userService.getUser(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @POST
    public Response createUser(@Valid UserDTO userDTO) {
        UserDTO created = userService.createUser(userDTO);
        return Response.status(201)
            .entity(created)
            .build();
    }
    
    @PUT
    @Path("/{id}")
    public UserDTO updateUser(@PathParam("id") long id, @Valid UserDTO userDTO) {
        return userService.updateUser(id, userDTO)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") long id) {
        userService.deleteUser(id);
        return Response.status(204).build();
    }
}
```

---

## Migration Guide

### Upgrading from Version 3.x to 4.x

Основные изменения при обновлении:

1. **Java Version**: Требуется Java 11+
2. **Jersey**: Обновлен до версии 3.x
3. **Jakarta EE**: Переход на Jakarta EE вместо javax
4. **Dependencies**: Обновлены все зависимости

### Migration Steps

```java
// Старый код (3.x)
import javax.ws.rs.GET;
import javax.ws.rs.Path;

// Новый код (4.x)
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
```

---

## Common Pitfalls and Solutions

### 1. Memory Leaks

**Проблема**: Утечки памяти из-за незакрытых ресурсов

**Решение**:
```java
@PostConstruct
public void init() {
    // Инициализация
}

@PreDestroy
public void cleanup() {
    // Очистка ресурсов
    if (dataSource instanceof Closeable) {
        ((Closeable) dataSource).close();
    }
}
```

### 2. Connection Pool Exhaustion

**Проблема**: Исчерпание пула соединений

**Решение**:
```yaml
database:
  maxSize: 32
  minSize: 8
  maxWaitForConnection: 1s
```

### 3. Slow Queries

**Проблема**: Медленные запросы к БД

**Решение**:
- Добавить индексы
- Оптимизировать запросы
- Использовать prepared statements
- Настроить connection pooling

---

## Additional Resources

### Books

- "Dropwizard in Action" - Полное руководство по Dropwizard
- "Building Microservices" - Архитектура микросервисов

### Community

- Stack Overflow: тег `dropwizard`
- GitHub Discussions: обсуждения и вопросы

### Tools

- Dropwizard Maven Plugin
- Dropwizard Gradle Plugin
- Dropwizard CLI Tools

---

## Summary

Dropwizard - это мощный фреймворк для создания production-ready RESTful веб-сервисов. Он объединяет лучшие библиотеки Java экосистемы и предоставляет готовые решения для метрик, логирования, health checks и конфигурации.

### Ключевые преимущества

- **Production-ready**: Встроенные инструменты для production
- **Type-safe конфигурация**: YAML конфигурация с валидацией
- **Метрики**: Встроенная поддержка метрик
- **Health Checks**: Автоматические health checks
- **Логирование**: Настроенное логирование из коробки

### Когда использовать Dropwizard

- Создание RESTful API
- Микросервисная архитектура
- Требования к метрикам и мониторингу
- Production-ready приложения

---

## Detailed Examples

### Complete Application Structure

```java
// Application.java
public class MyApplication extends Application<MyConfiguration> {
    public static void main(String[] args) throws Exception {
        new MyApplication().run(args);
    }
    
    @Override
    public void initialize(Bootstrap<MyConfiguration> bootstrap) {
        bootstrap.addBundle(new HibernateBundle<MyConfiguration>(User.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(MyConfiguration config) {
                return config.getDataSourceFactory();
            }
        });
    }
    
    @Override
    public void run(MyConfiguration config, Environment env) {
        // Регистрация ресурсов
        env.jersey().register(new UserResource(userDAO));
        env.jersey().register(new PostResource(postDAO));
        
        // Регистрация health checks
        env.healthChecks().register("database", new DatabaseHealthCheck(database));
        
        // Регистрация exception mappers
        env.jersey().register(new GenericExceptionMapper());
    }
}
```

### Service Layer Implementation

```java
public class UserService {
    private final UserDAO userDAO;
    private final MetricRegistry metrics;
    private final Cache<Long, User> cache;
    
    public UserService(UserDAO userDAO, MetricRegistry metrics) {
        this.userDAO = userDAO;
        this.metrics = metrics;
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    }
    
    public List<UserDTO> getAllUsers(int page, int size) {
        Timer.Context context = metrics.timer("users.getAll").time();
        try {
            List<User> users = userDAO.findAll(page, size);
            return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        } finally {
            context.stop();
        }
    }
    
    public Optional<UserDTO> getUser(long id) {
        try {
            User user = cache.get(id, () -> 
                userDAO.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
            return Optional.of(toDTO(user));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public UserDTO createUser(UserDTO userDTO) {
        validateUser(userDTO);
        User user = fromDTO(userDTO);
        User saved = userDAO.save(user);
        cache.put(saved.getId(), saved);
        metrics.counter("users.created").inc();
        return toDTO(saved);
    }
    
    private void validateUser(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (userDTO.getEmail() == null || !isValidEmail(userDTO.getEmail())) {
            throw new ValidationException("Valid email is required");
        }
    }
    
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    private User fromDTO(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

---

## Полезные ссылки

- [Официальный сайт Dropwizard](https://www.dropwizard.io/)
- [Документация Dropwizard](https://www.dropwizard.io/en/latest/manual/index.html)
- [Dropwizard на GitHub](https://github.com/dropwizard/dropwizard)
- [Примеры Dropwizard](https://github.com/dropwizard/dropwizard/tree/master/dropwizard-example)

---

**Дата последнего обновления:** 2026-01-16

## Содержание

- [Введение в Dropwizard](#�-ведение-в-dropwizard)
  - [Основные особенности](#�-�-новн�-е-о�-обенно�-�-и)
  - [Компоненты Dropwizard](#�-омпонен�-�-dropwizard)
- [Установка и настройка](#�-�-�-ановка-и-на�-�-�-ойка)
  - [Maven зависимость](#maven-зави�-имо�-�-�)



