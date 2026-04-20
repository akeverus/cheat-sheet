---
title: "Spring Cloud"
description: "Кратко: Spring Cloud - набор инструментов для создания облачных приложений. Service Discovery, Config Server, Gateway, Circuit Breaker, Load Balancing, микросервисы."
tags:
  - frameworks
  - java-frameworks
  - spring-cloud
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Spring Cloud

Кратко: **Spring Cloud** — набор инструментов для создания облачных приложений. **Service Discovery**, **Config Server**, **Gateway**, **Circuit Breaker**, **Load Balancing**, микросервисы.

## Полезные ссылки

### Официальная документация
- [**Spring Cloud** Documentation](https://spring.io/projects/spring-cloud)
- [**Spring Cloud** Netflix](https://github.com/Netflix/eureka/wiki)
- [**Spring Cloud** Config](https://spring.io/projects/spring-cloud-config)

### Обучающие материалы
- [**Spring Cloud** Series](https://www.baeldung.com/spring-cloud-series)

### См. также
- [[microservices|Микросервисы]] — микросервисная архитектура
- [[spring-boot|**Spring Boot**]] — **Spring Boot** основы
- [Мониторинг](../../../monitoring/) — мониторинг микросервисов

- [[spring-websocket|Spring WebSocket]]
- [[spring-integration|Spring Integration]]
- [[spring-batch|Spring Batch для Java]]
## Содержание

- [Введение в Spring Cloud](#введение-в-spring-cloud)
  - [Основные компоненты](#основные-компоненты)
  - [Версии и совместимость](#версии-и-совместимость)
- [Service Discovery (Eureka)](#service-discovery-eureka)
  - [Eureka Server](#eureka-server)
  - [Eureka Client](#eureka-client)
  - [Использование Service Discovery](#использование-service-discovery)
- [Config Server](#config-server)
  - [Config Server](#config-server-1)
  - [Структура репозитория конфигураций](#структура-репозитория-конфигураций)
  - [Config Client](#config-client)
  - [Refresh конфигурации](#refresh-конфигурации)
- [API Gateway (Zuul/Gateway)](#api-gateway-zuulgateway)
  - [Spring Cloud Gateway](#spring-cloud-gateway)
  - [Кастомные фильтры](#кастомные-фильтры)
  - [Authentication Filter](#authentication-filter)
- [Circuit Breaker (Hystrix/Resilience4j)](#circuit-breaker-hystrixresilience4j)
  - [Hystrix (deprecated, но все еще используется)](#hystrix-deprecated-но-все-еще-используется)
  - [Resilience4j (рекомендуется)](#resilience4j-рекомендуется)
- [Load Balancing (Ribbon)](#load-balancing-ribbon)
  - [Ribbon с RestTemplate](#ribbon-с-resttemplate)
  - [Ribbon с WebClient](#ribbon-с-webclient)
- [Spring Cloud Sleuth](#spring-cloud-sleuth)
- [Spring Cloud Stream](#spring-cloud-stream)
- [Spring Cloud Bus](#spring-cloud-bus)
- [Spring Cloud Security](#spring-cloud-security)
- [Spring Cloud Kubernetes](#spring-cloud-kubernetes)
- [Миграция на Spring Cloud 2023](#миграция-на-spring-cloud-2023)
  - [Основные изменения](#основные-изменения)
  - [Обновление зависимостей](#обновление-зависимостей)
  - [Замена Ribbon на LoadBalancer](#замена-ribbon-на-loadbalancer)
- [Лучшие практики](#лучшие-практики)
- [Примеры](#примеры)
  - [Полный микросервис с Spring Cloud](#полный-микросервис-с-spring-cloud)
  - [Eureka Server с аутентификацией](#eureka-server-с-аутентификацией)
  - [Config Server с аутентификацией](#config-server-с-аутентификацией)
  - [Circuit Breaker с метриками](#circuit-breaker-с-метриками)

## Введение в Spring Cloud

**Spring Cloud** — это набор инструментов и фреймворков, которые помогают разработчикам создавать облачные приложения, особенно в архитектуре микросервисов. **Spring Cloud** предоставляет решения для распространенных паттернов распределенных систем.

### Основные компоненты

- **Service Discovery**: Регистрация и обнаружение сервисов
- **Configuration Management**: Централизованное управление конфигурацией
- **API Gateway**: Единая точка входа для **API**
- **Circuit Breaker**: Защита от каскадных сбоев
- **Load Balancing**: Распределение нагрузки между инстансами

### Версии и совместимость

**Spring Cloud BOM** для управления версиями зависимостей (pom.xml):**

```xml
<!-- Spring Cloud BOM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Service Discovery (Eureka)

**Eureka** — это **REST-based** сервис для **service discovery**. **Eureka** сервер выступает в роли **registry**, где микросервисы регистрируют себя и находят другие сервисы.

### Eureka Server

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

```java
// EurekaServerApplication.java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# application.yml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

### Eureka Client

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

```java
// ServiceApplication.java
@SpringBootApplication
@EnableEurekaClient
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

```yaml
# application.yml
spring:
  application:
    name: user-service

server:
  port: 8081

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    preferIpAddress: true
```

### Использование Service Discovery

```java
// Сервис с RestTemplate и DiscoveryClient (Eureka)
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public String getUserData() {
        // Через RestTemplate с load balancing
        return restTemplate.getForObject("http://user-service/users/1", String.class);
    }

    public List<ServiceInstance> getServiceInstances() {
        // Получение всех инстансов сервиса
        return discoveryClient.getInstances("user-service");
    }
}
```

## Config Server

**Spring Cloud Config Server** предоставляет централизованное управление конфигурацией для распределенных систем.

### Config Server

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

```java
// ConfigServerApplication.java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
# application.yml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          search-paths: '{application}'
```

### Структура репозитория конфигураций

```text
config-repo/
├── application.yml          # Общие настройки
├── user-service.yml         # Настройки для user-service
├── user-service-dev.yml     # Настройки для dev окружения
├── user-service-prod.yml    # Настройки для prod окружения
└── order-service.yml        # Настройки для order-service
```

```yaml
# config-repo/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/defaultdb

# config-repo/user-service.yml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
  jpa:
    hibernate:
      ddl-auto: update

# config-repo/user-service-dev.yml
logging:
  level:
    com.example: DEBUG
```

### Config Client

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
</dependencies>
```

```yaml
# bootstrap.yml (важно! не application.yml)
spring:
  application:
    name: user-service
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
```

### Refresh конфигурации

```java
// Service с @RefreshScope
@Service
@RefreshScope
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    // Геттеры
}
```

```bash
# Обновление конфигурации
curl -X POST http://localhost:8081/actuator/refresh
```

## API Gateway (Zuul/Gateway)

**API Gateway** — это единая точка входа для всех клиентских запросов к микросервисам.

### Spring Cloud Gateway

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
</dependencies>
```

```java
// GatewayApplication.java
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

```yaml
# application.yml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/
          filters:
            - RewritePath=/api/users/(?<path>.*), /${path}

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/
          filters:
            - RewritePath=/api/orders/(?<path>.*), /${path}

        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/auth/
```

### Кастомные фильтры

```java
// Глобальный фильтр Gateway для логирования запросов
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("Request took: " + duration + "ms");
            }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

### Authentication Filter

```java
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || !isValidToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isValidToken(String token) {
        // Валидация JWT токена
        return true;
    }
}
```

## Circuit Breaker (Hystrix/Resilience4j)

**Circuit Breaker** защищает систему от каскадных сбоев.

### Hystrix (deprecated, но все еще используется)

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
</dependencies>
```

```java
// Application.java
@SpringBootApplication
@EnableHystrix
public class Application {
    // ...
}
```

```java
@Service
public class UserService {

    @HystrixCommand(fallbackMethod = "fallbackGetUser",
                    commandProperties = {
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
                    })
    public User getUser(String userId) {
        // Вызов внешнего сервиса
        return restTemplate.getForObject("http://user-service/users/" + userId, User.class);
    }

    public User fallbackGetUser(String userId) {
        // Возврат дефолтного значения
        return new User(userId, "Unknown", "unknown@example.com");
    }
}
```

### Resilience4j (рекомендуется)

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    </dependency>
</dependencies>
```

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        waitDurationInOpenState: 10000
        slowCallDurationThreshold: 2000
        permittedNumberOfCallsInHalfOpenState: 3
        slidingWindowSize: 10
  retry:
    instances:
      userService:
        maxRetryAttempts: 3
        waitDuration: 1000
```

```java
@Service
public class UserService {

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    @Retry(name = "userService")
    public User getUser(String userId) {
        return restTemplate.getForObject("http://user-service/users/" + userId, User.class);
    }

    public User fallbackGetUser(String userId, Throwable t) {
        System.err.println("Circuit breaker activated: " + t.getMessage());
        return new User(userId, "Unknown", "unknown@example.com");
    }
}
```

## Load Balancing (Ribbon)

**Ribbon** предоставляет клиент-**side load balancing** для микросервисов.

### Ribbon с RestTemplate

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
</dependencies>
```

```java
@Configuration
public class RibbonConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

```java
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    public User getUser(String userId) {
        // Автоматический load balancing
        return restTemplate.getForObject("http://user-service/users/" + userId, User.class);
    }
}
```

### Ribbon с WebClient

```java
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

```java
@Service
public class UserService {

    private final WebClient webClient;

    public UserService(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<User> getUser(String userId) {
        return webClient.get()
            .uri("http://user-service/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class);
    }
}
```

## Spring Cloud Sleuth

**Sleuth** добавляет **tracing capabilities** для **distributed systems**.

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-zipkin</artifactId>
    </dependency>
</dependencies>
```

```yaml
# application.yml
spring:
  sleuth:
    sampler:
      probability: 1.0  # 100% tracing для разработки
  zipkin:
    base-url: http://localhost:9411
```

```java
@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        logger.info("Getting user with id: {}", id);

        // Trace ID автоматически добавляется в логи
        return userService.getUser(id);
    }
}
```

## Spring Cloud Stream

**Spring Cloud Stream** упрощает создание **event-driven** микросервисов.

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream-binder-kafka</artifactId>
    </dependency>
</dependencies>
```

```yaml
# application.yml
spring:
  cloud:
    stream:
      bindings:
        userCreated:
          destination: user-events
          content-type: application/json
        processUser:
          destination: user-events
          group: user-processor
      kafka:
        binder:
          brokers: localhost:9092
```

```java
@EnableBinding(UserProcessor.class)
public class UserEventHandler {

    @StreamListener(UserProcessor.INPUT)
    public void handleUserCreated(User user) {
        System.out.println("Processing user: " + user.getName());
        // Обработка события
    }
}

interface UserProcessor {
    String INPUT = "processUser";
    String OUTPUT = "userCreated";

    @Input(INPUT)
    SubscribableChannel input();

    @Output(OUTPUT)
    MessageChannel output();
}
```

## Spring Cloud Bus

**Spring Cloud Bus** позволяет **broadcast** конфигурационных изменений.

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-bus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
    </dependency>
</dependencies>
```

```bash
# Обновление конфигурации всех инстансов
curl -X POST http://localhost:8081/actuator/bus-refresh
```

## Spring Cloud Security

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-security</artifactId>
    </dependency>
</dependencies>
```

```yaml
# application.yml
security:
  oauth2:
    resource:
      user-info-uri: http://auth-service/user
```

## Spring Cloud Kubernetes

Интеграция с **Kubernetes** для **service discovery** и конфигурации.

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-kubernetes-fabric8-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-kubernetes-fabric8-discovery</artifactId>
    </dependency>
</dependencies>
```

## Миграция на Spring Cloud `2023`

**Spring Cloud** `2023`.x — это новая версия с обновленными зависимостями.

### Основные изменения

1. **Java 17+** как минимальная версия
2. **Spring `Boot 3.1`+** поддержка
3. **Новые версии компонентов**:**
   - **LoadBalancer** вместо **Ribbon**
   - **Resilience4j** вместо **Hystrix**
   - **Spring Cloud Gateway** вместо **Zuul**

### Обновление зависимостей

```xml
<!-- Старый подход -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- Новый подход -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <version>4.0.3</version>  <!-- Указывать версию явно -->
</dependency>
```

### Замена Ribbon на LoadBalancer

```java
// Старый подход
@LoadBalanced
@RestTemplate restTemplate;

// Новый подход
@Bean
public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
}

@Autowired
private LoadBalancerExchangeFilterFunction lbFunction;

public Mono<String> callService() {
    return webClientBuilder.build()
        .get()
        .uri("http://user-service/users")
        .retrieve()
        .bodyToMono(String.class);
}
```

## Лучшие практики

1. **Используйте `Spring Cloud` Gateway** вместо **Zuul** для новых проектов
2. **Предпочитайте Resilience4j Hystrix** для **circuit breaker**
3. **Внедряйте централизованную конфигурацию** через **Config Server**
4. **Используйте service discovery** для коммуникации между сервисами
5. **Внедряйте distributed tracing** с **Sleuth**
6. **Регулярно обновляйте версии Spring Cloud**
7. **Тестируйте circuit breakers** и **fallback** методы
8. **Мониторьте health endpoints** всех сервисов
9. **Используйте `@LoadBalance`d** для автоматического **load balancing**
10. **Документируйте API endpoints** через **Gateway**

## Примеры

### Полный микросервис с Spring Cloud

```yaml
# docker-compose.yml для разработки
version: '3.8'
services:
  eureka:
    image: springcloud/eureka
    ports:
      - "8761:8761"

  config-server:
    build: ./config-server
    ports:
      - "8888:8888"

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    depends_on:
      - eureka
      - config-server

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    depends_on:
      - eureka
      - config-server
```

### Eureka Server с аутентификацией

```yaml
# application.yml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false

spring:
  security:
    user:
      name: admin
      password: password
```

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
```

### Config Server с аутентификацией

```yaml
# application.yml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          username: ${{GIT_USER}}
          password: ${{GIT_PASSWORD}}
  security:
    user:
      name: config
      password: config123
```

### Circuit Breaker с метриками

```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

        registry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    // Метрики для переходов состояний
                    meterRegistry.counter("circuit_breaker_state_transitions_total",
                        "name", circuitBreaker.getName(),
                        "from", event.getStateTransition().getFromState().name(),
                        "to", event.getStateTransition().getToState().name())
                        .increment();
                });
        });

        return registry;
    }
}
```

Этот файл содержит детальное описание **Spring Cloud**: от основных компонентов до полной интеграции, миграции и лучших практик. Он охватывает все ключевые аспекты создания микросервисных приложений с **Spring Cloud**.
