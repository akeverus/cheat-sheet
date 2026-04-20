---
title: "SOA (Service-Oriented Architecture)"
description: "Service-Oriented Architecture (SOA) — это архитектурный подход к проектированию распределенных систем, основанный на использовании сервисов как основных строительных блоков. Сервисы в SOA являются автономными, слабосвязанными и могут быть использованы повторно."
tags:
  - architecture
  - soa
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# SOA (Service-Oriented Architecture)

**Service-Oriented Architecture** (SOA) — это архитектурный подход к проектированию распределенных систем, основанный на использовании сервисов как основных строительных блоков. Сервисы в **SOA** являются автономными, слабосвязанными и могут быть использованы повторно.

## Полезные ссылки

### Официальная документация
- [Service-Oriented Architecture (Wikipedia)](https://en.wikipedia.org/wiki/Service-oriented_architecture)
- [Spring Web Services](https://docs.spring.io/spring-ws/reference/) — **Spring WS**
- [Spring Cloud](https://spring.io/projects/spring-cloud) — **Spring Cloud**

### См. также
- [Микросервисы](software-architecture/microservices.md) — микросервисная архитектура
- [Архитектурные паттерны](architecture-patterns.md) — архитектурные паттерны
- [Event-Driven Architecture](event-driven.md) — **Event-Driven Architecture**

- [Domain-Driven Design (DDD)](ddd.md)
- [Event Sourcing](event-sourcing.md)
## Содержание

- [Введение в SOA](#введение-в-soa)
  - [Основные характеристики SOA](#основные-характеристики-soa)
  - [Преимущества SOA](#преимущества-soa)
  - [Недостатки SOA](#недостатки-soa)
- [Принципы SOA](#принципы-soa)
  - [1. Service Autonomy (Автономность сервисов)](#1-service-autonomy-автономность-сервисов)
  - [2. Service Loose Coupling (Слабая связанность)](#2-service-loose-coupling-слабая-связанность)
  - [3. Service Reusability (Переиспользование)](#3-service-reusability-переиспользование)
  - [4. Service Discoverability (Обнаруживаемость)](#4-service-discoverability-обнаруживаемость)
- [Service Contracts](#service-contracts)
  - [REST Service Contract](#rest-service-contract)
  - [SOAP Service Contract (WSDL)](#soap-service-contract-wsdl)
  - [OpenAPI Specification](#openapi-specification)
- [Service Registry и Discovery](#service-registry-и-discovery)
  - [Eureka Service Registry](#eureka-service-registry)
  - [Service Discovery с RestTemplate](#service-discovery-с-resttemplate)
  - [Service Discovery с Feign Client](#service-discovery-с-feign-client)
- [ESB (Enterprise Service Bus)](#esb-enterprise-service-bus)
  - [Apache Camel как ESB](#apache-camel-как-esb)
- [Service Composition](#service-composition)
  - [Orchestration Pattern](#orchestration-pattern)
  - [Choreography Pattern](#choreography-pattern)
- [SOAP vs REST](#soap-vs-rest)
  - [SOAP (Simple Object Access Protocol)](#soap-simple-object-access-protocol)
  - [REST (Representational State Transfer)](#rest-representational-state-transfer)
- [Реализация на Spring WS](#реализация-на-spring-ws)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
  - [Endpoint](#endpoint)
- [Реализация на Spring Cloud](#реализация-на-spring-cloud)
  - [Service Registration и Discovery](#service-registration-и-discovery)
  - [API Gateway](#api-gateway)
- [Лучшие практики](#лучшие-практики)
  - [1. Стандартизация интерфейсов](#1-стандартизация-интерфейсов)
  - [2. Версионирование сервисов](#2-версионирование-сервисов)
  - [3. Обработка ошибок](#3-обработка-ошибок)
  - [4. Мониторинг и логирование](#4-мониторинг-и-логирование)
- [Решение проблем](#решение-проблем)
  - [Проблема: Сервисы не могут найти друг друга](#проблема-сервисы-не-могут-найти-друг-друга)
  - [Проблема: Таймауты при вызове сервисов](#проблема-таймауты-при-вызове-сервисов)
- [Частые вопросы](#частые-вопросы)

## Введение в SOA

**Service-Oriented Architecture** — это стиль архитектуры, в котором приложения состоят из сервисов, которые взаимодействуют через стандартизированные интерфейсы. Сервисы являются независимыми, могут быть разработаны разными командами и развернуты на разных платформах.

### Основные характеристики SOA

**Автономность сервисов**
Сервисы независимы и могут функционировать самостоятельно.

**Слабая связанность**
Сервисы взаимодействуют через стандартизированные интерфейсы, не зная деталей реализации друг друга.

**Переиспользование**
Сервисы могут быть использованы в различных контекстах и приложениях.

**Интероперабельность**
Сервисы могут взаимодействовать независимо от платформы и языка программирования.

**Композиция**
Сервисы могут быть объединены для создания более сложных сервисов.

### Преимущества SOA

Ключевые преимущества подхода **SOA**:

- **Переиспользование** — сервисы могут использоваться в разных приложениях
- **Гибкость** — легко добавлять новые сервисы или изменять существующие
- **Масштабируемость** — сервисы могут масштабироваться независимо
- **Интеграция** — упрощает интеграцию различных систем
- **Независимость от платформы** — сервисы могут быть реализованы на разных технологиях

### Недостатки SOA

Среди ограничений **SOA** можно выделить:

- **Сложность** — управление множеством сервисов может быть сложным
- **Производительность** — сетевые вызовы могут влиять на производительность
- **Управление версиями** — сложность управления версиями сервисов
- **Безопасность** — необходимость обеспечения безопасности на уровне сервисов

## Принципы SOA

### 1. Service Autonomy (Автономность сервисов)

Сервисы должны быть автономными и контролировать свои ресурсы.

Ниже — пример контракта сервиса в **SOA** (Java).
```java
@Service
public class UserService {

    private final UserRepository userRepository;

    // Сервис контролирует свою логику и данные
    public User createUser(CreateUserRequest request) {
        // Валидация
        validateUserRequest(request);

        // Бизнес-логика
        User user = new User(request.getEmail(), request.getName());

        // Сохранение
        return userRepository.save(user);
    }

    private void validateUserRequest(CreateUserRequest request) {
        // Валидация внутри сервиса
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

### 2. Service Loose Coupling (Слабая связанность)

Сервисы взаимодействуют через стандартизированные интерфейсы.

```java
// Интерфейс сервиса
public interface PaymentService {
    PaymentResult processPayment(PaymentRequest request);
}

// Реализация сервиса
@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Реализация может быть изменена без влияния на клиентов
        return processPaymentInternal(request);
    }
}

// Клиент использует интерфейс, а не реализацию
@Service
public class OrderService {

    private final PaymentService paymentService; // Зависимость от интерфейса

    public void processOrder(Order order) {
        PaymentRequest request = new PaymentRequest(order.getAmount());
        PaymentResult result = paymentService.processPayment(request);
        // Обработка результата
    }
}
```

### 3. Service Reusability (Переиспользование)

Сервисы должны быть спроектированы для переиспользования.

```java
// Переиспользуемый сервис
@Service
public class NotificationService {

    public void sendEmail(String to, String subject, String body) {
        // Общая логика отправки email
    }

    public void sendSms(String phoneNumber, String message) {
        // Общая логика отправки SMS
    }
}

// Использование в разных контекстах
@Service
public class UserService {

    private final NotificationService notificationService;

    public void createUser(CreateUserRequest request) {
        User user = // создание пользователя
        notificationService.sendEmail(user.getEmail(), "Welcome", "Welcome message");
    }
}

@Service
public class OrderService {

    private final NotificationService notificationService;

    public void confirmOrder(Order order) {
        // подтверждение заказа
        notificationService.sendEmail(order.getCustomerEmail(), "Order Confirmed", "Order details");
    }
}
```

### 4. Service Discoverability (Обнаруживаемость)

Сервисы должны быть легко обнаруживаемы через **Service Registry**.

```java
// Регистрация сервиса
@Service
public class UserService {

    @PostConstruct
    public void registerService() {
        serviceRegistry.register("user-service", "http://user-service:8080");
    }
}

// Обнаружение сервиса
@Service
public class OrderService {

    private final ServiceRegistry serviceRegistry;

    public void processOrder(Order order) {
        ServiceInfo userService = serviceRegistry.discover("user-service");
        // Использование обнаруженного сервиса
        User user = restTemplate.getForObject(
            userService.getUrl() + "/users/" + order.getCustomerId(),
            User.class
        );
    }
}
```

## Service Contracts

**Service Contract** определяет интерфейс сервиса, включая операции, параметры, возвращаемые значения и исключения.

### REST Service Contract

```java
// REST API Contract
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

// DTO для контракта
public class CreateUserRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    // Getters and setters
}

public class User {
    private String id;
    private String email;
    private String name;
    private Instant createdAt;

    // Getters and setters
}
```

### SOAP Service Contract (WSDL)

```java
// SOAP Service Contract через WSDL
@Endpoint
public class UserServiceEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/users";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateUserRequest")
    @ResponsePayload
    public CreateUserResponse createUser(@RequestPayload CreateUserRequest request) {
        User user = userService.createUser(request);

        CreateUserResponse response = new CreateUserResponse();
        response.setUser(toUserDto(user));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        User user = userService.getUser(request.getId());

        GetUserResponse response = new GetUserResponse();
        response.setUser(toUserDto(user));
        return response;
    }

    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }
}

// WSDL Schema
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateUserRequest", namespace = "http://example.com/users")
public class CreateUserRequest {
    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String name;

    // Getters and setters
}
```

### OpenAPI Specification

```java
@RestController
@RequestMapping("/api/users")
@Api(tags = "User Management")
public class UserController {

    @PostMapping
    @ApiOperation(value = "Create a new user", response = User.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "User created successfully"),
        @ApiResponse(code = 400, message = "Invalid request")
    })
    public ResponseEntity<User> createUser(
            @ApiParam(value = "User creation request", required = true)
            @RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
```

## Service Registry и Discovery

**Service Registry** — это централизованное хранилище информации о доступных сервисах. **Service Discovery** позволяет сервисам находить друг друга динамически.

### Eureka Service Registry

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

// Eureka Client (Service Registration)
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

// application.yml для Eureka Client
spring:
  application:
    name: user-service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    hostname: user-service
    prefer-ip-address: true
```

### Service Discovery с RestTemplate

```java
@Configuration
public class RestTemplateConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    // Использование имени сервиса вместо URL
    public User getUser(String userId) {
        return restTemplate.getForObject(
            "http://user-service/api/users/{id}",
            User.class,
            userId
        );
    }
}
```

### Service Discovery с Feign Client

```java
@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    User getUser(@PathVariable String id);

    @PostMapping("/api/users")
    User createUser(@RequestBody CreateUserRequest request);
}

@Service
public class OrderService {

    private final UserServiceClient userServiceClient;

    public void processOrder(Order order) {
        User user = userServiceClient.getUser(order.getCustomerId());
        // Обработка заказа
    }
}
```

## ESB (Enterprise `Service` Bus)

**ESB** — это архитектурный паттерн, который обеспечивает централизованную интеграцию сервисов через единую шину сообщений.

### Apache Camel как ESB

```java
@Configuration
public class CamelConfig {

    @Bean
    public RouteBuilder routeBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Маршрутизация сообщений
                from("direct:userCreated")
                    .to("log:userCreated")
                    .to("kafka:user-events")
                    .to("direct:sendWelcomeEmail");

                // Трансформация сообщений
                from("direct:orderCreated")
                    .transform().simple("${body.orderId}")
                    .to("jms:orderQueue");

                // Агрегация сообщений
                from("direct:orderItems")
                    .aggregate(header("orderId"), new OrderAggregationStrategy())
                    .completionSize(10)
                    .to("direct:processOrder");
            }
        };
    }
}

@Component
public class OrderService {

    @Autowired
    private ProducerTemplate producerTemplate;

    public void createOrder(Order order) {
        // Отправка сообщения в ESB
        producerTemplate.sendBody("direct:orderCreated", order);
    }
}
```

## Service Composition

**Service Composition** — это объединение нескольких сервисов для создания более сложного сервиса.

### Orchestration Pattern

```java
@Service
public class OrderOrchestrationService {

    private final UserServiceClient userServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final ShippingServiceClient shippingServiceClient;

    @Transactional
    public OrderResult processOrder(OrderRequest request) {
        // Шаг 1: Валидация пользователя
        User user = userServiceClient.getUser(request.getCustomerId());
        if (user == null) {
            throw new UserNotFoundException(request.getCustomerId());
        }

        // Шаг 2: Проверка наличия товаров
        InventoryCheckResult inventoryCheck = inventoryServiceClient
            .checkAvailability(request.getItems());
        if (!inventoryCheck.isAvailable()) {
            throw new InsufficientInventoryException();
        }

        // Шаг 3: Обработка платежа
        PaymentRequest paymentRequest = new PaymentRequest(
            request.getCustomerId(),
            request.getTotalAmount()
        );
        PaymentResult paymentResult = paymentServiceClient.processPayment(paymentRequest);
        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException();
        }

        // Шаг 4: Резервирование товаров
        inventoryServiceClient.reserveItems(request.getItems());

        // Шаг 5: Создание заказа
        Order order = createOrder(request);

        // Шаг 6: Отправка заказа
        ShippingRequest shippingRequest = new ShippingRequest(
            order.getId(),
            user.getAddress()
        );
        shippingServiceClient.shipOrder(shippingRequest);

        return new OrderResult(order.getId(), OrderStatus.CONFIRMED);
    }

    private Order createOrder(OrderRequest request) {
        // Создание заказа
        return new Order(/* ... */);
    }
}
```

### Choreography Pattern

```java
// Каждый сервис реагирует на события
@Service
public class OrderService {

    @KafkaListener(topics = "order-commands", groupId = "order-service")
    public void handleCreateOrder(CreateOrderCommand command) {
        Order order = createOrder(command);
        // Публикация события
        kafkaTemplate.send("order-events", new OrderCreatedEvent(order));
    }
}

@Service
public class PaymentService {

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        PaymentResult result = processPayment(event.getOrder());
        if (result.isSuccess()) {
            kafkaTemplate.send("payment-events", new PaymentSucceededEvent(event.getOrderId()));
        } else {
            kafkaTemplate.send("payment-events", new PaymentFailedEvent(event.getOrderId()));
        }
    }
}

@Service
public class InventoryService {

    @KafkaListener(topics = "payment-events", groupId = "inventory-service")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        reserveItems(event.getOrderId());
        kafkaTemplate.send("inventory-events", new InventoryReservedEvent(event.getOrderId()));
    }
}
```

## SOAP vs REST

### SOAP (Simple `Object Access` Protocol)

**SOAP** — это протокол для обмена структурированными сообщениями в веб-сервисах.

**Преимущества:**
- Стандартизированный протокол
- Поддержка транзакций
- Встроенная безопасность (WS-Security)
- Поддержка сложных типов данных

**Недостатки:**
- Сложность
- Больший размер сообщений
- Медленнее **REST**

```java
// SOAP Service
@Endpoint
public class UserServiceEndpoint {

    @PayloadRoot(namespace = "http://example.com/users", localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        User user = userService.getUser(request.getId());
        GetUserResponse response = new GetUserResponse();
        response.setUser(toUserDto(user));
        return response;
    }
}
```

### REST (Representational `State` Transfer)

**REST** — это архитектурный стиль для веб-сервисов, использующий **HTTP** методы.

**Преимущества:**
- Простота
- Легковесность
- Кэширование
- Широкое распространение

**Недостатки:**
- Нет стандартизированного контракта
- Ограниченная поддержка транзакций
- Меньше возможностей для сложных операций

```java
// REST Service
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }
}
```

## Реализация на Spring `WS`

### Зависимости

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web-services</artifactId>
    </dependency>
    <dependency>
        <groupId>wsdl4j</groupId>
        <artifactId>wsdl4j</artifactId>
    </dependency>
</dependencies>
```

### Конфигурация

```java
@Configuration
@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "users")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema usersSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UsersPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://example.com/users");
        wsdl11Definition.setSchema(usersSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema usersSchema() {
        return new SimpleXsdSchema(new ClassPathResource("users.xsd"));
    }
}
```

### Endpoint

```java
@Endpoint
public class UserServiceEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/users";

    private final UserService userService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        User user = userService.getUser(request.getId());

        GetUserResponse response = new GetUserResponse();
        response.setUser(toUserDto(user));
        return response;
    }

    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }
}
```

## Реализация на Spring Cloud

### Service Registration и Discovery

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

// Service Provider
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

// Service Consumer
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

### API Gateway

```java
@SpringBootApplication
@EnableZuulProxy
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

// Конфигурация маршрутов
zuul:
  routes:
    user-service:
      path: /users/
      service-id: user-service
    order-service:
      path: /orders/
      service-id: order-service
```

## Лучшие практики

### 1. Стандартизация интерфейсов

Используйте стандартные протоколы и форматы данных.

### 2. Версионирование сервисов

Поддерживайте версионирование для обратной совместимости.

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    // Версия 1 API
}

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {
    // Версия 2 API
}
```

### 3. Обработка ошибок

Стандартизируйте обработку ошибок во всех сервисах.

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### 4. Мониторинг и логирование

Реализуйте централизованное логирование и мониторинг.

```java
@Component
public class ServiceMonitoring {

    private final MeterRegistry meterRegistry;

    public void recordServiceCall(String serviceName, Duration duration) {
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("service.call")
            .tag("service", serviceName)
            .register(meterRegistry));
    }
}
```

## Решение проблем

### Проблема: Сервисы не могут найти друг друга

**Решение:** Проверьте конфигурацию **Service Registry** и убедитесь, что сервисы зарегистрированы.

### Проблема: Таймауты при вызове сервисов

**Решение:** Настройте таймауты и **retry** механизмы.

```java
@Configuration
public class FeignConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 10000); // Connect timeout, Read timeout
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 2000, 3);
    }
}
```

## Частые вопросы

**Чем SOA отличается от микросервисов?** SOA — более общий подход к организации распределённых систем через сервисы с контрактами и часто с централизованной оркестрацией или шиной (ESB). Микросервисы — эволюция SOA: меньшие сервисы, упор на децентрализацию, самостоятельное развёртывание и владение данными. Многие практики (сервисный контракт, обнаружение сервисов, устойчивость вызовов) общие.

**Когда выбирать SOAP, а когда REST?** SOAP уместен в корпоративных интеграциях с жёсткими контрактами (WSDL), WS-* стандартами и транзакциями. REST проще для веб- и мобильных клиентов, кэширования и горизонтального масштабирования. Для новых проектов чаще выбирают REST или gRPC; SOAP остаётся там, где уже есть экосистема и требования к стандартам.

**Нужен ли ESB в современном стеке?** Не обязателен. ESB полезен при множестве разнородных систем и протоколов и централизованной маршрутизации/трансформации. В микросервисной архитектуре часто обходятся API Gateway, сервисной шиной событий (Kafka, RabbitMQ) и прямыми вызовами между сервисами. Решение зависит от масштаба и разнородности интеграций.
