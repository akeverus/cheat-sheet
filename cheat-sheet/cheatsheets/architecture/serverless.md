# Serverless Architecture

Serverless Architecture — это архитектурный подход, при котором разработчики создают и запускают приложения без управления серверами. Провайдер облачных услуг управляет инфраструктурой, автоматически выделяя ресурсы по мере необходимости.

**Дата последнего обновления:** 2026-01-23

## Полезные ссылки

### Официальная документация
- [AWS Lambda Documentation](https://docs.aws.amazon.com/lambda/)
- [Azure Functions Documentation](https://docs.microsoft.com/azure/azure-functions/)
- [Google Cloud Functions Documentation](https://cloud.google.com/functions/docs)
- [Spring Cloud Function Documentation](https://spring.io/projects/spring-cloud-function)

### См. также
- `event-driven.md` - Event-Driven Architecture
- `microservices.md` - Микросервисная архитектура
- `architecture-patterns.md` - Архихитектурные паттерны

## Содержание

- [Введение в Serverless Architecture](#введение-в-serverless-architecture)
- [Function as a Service (FaaS)](#function-as-a-service-faas)
- [Backend as a Service (BaaS)](#backend-as-a-service-baas)
- [AWS Lambda](#aws-lambda)
- [Azure Functions](#azure-functions)
- [Google Cloud Functions](#google-cloud-functions)
- [Spring Cloud Function](#spring-cloud-function)
- [Event-driven Serverless Patterns](#event-driven-serverless-patterns)
- [Cold Start проблема](#cold-start-проблема)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Введение в Serverless Architecture

Serverless Architecture позволяет разработчикам создавать приложения без необходимости управления серверами. Провайдер облачных услуг автоматически управляет инфраструктурой, масштабированием и доступностью.

### Основные характеристики Serverless

**Автоматическое масштабирование**
Функции автоматически масштабируются в зависимости от нагрузки.

**Оплата за использование**
Оплата производится только за фактическое время выполнения функций.

**Управление инфраструктурой провайдером**
Провайдер управляет серверами, операционной системой и средами выполнения.

**Event-driven**
Функции запускаются в ответ на события.

### Преимущества Serverless

- **Снижение операционных затрат** — нет необходимости управлять серверами
- **Автоматическое масштабирование** — функции масштабируются автоматически
- **Быстрое развертывание** — быстрое развертывание и обновление функций
- **Снижение затрат** — оплата только за фактическое использование
- **Фокус на бизнес-логике** — разработчики фокусируются на коде, а не на инфраструктуре

### Недостатки Serverless

- **Cold Start** — задержка при первом запуске функции
- **Ограничения времени выполнения** — ограничения на время выполнения функций
- **Отладка** — сложность отладки распределенных функций
- **Vendor Lock-in** — зависимость от конкретного провайдера
- **Ограничения ресурсов** — ограничения на память и CPU

## Function as a Service (FaaS)

FaaS — это модель облачных вычислений, которая позволяет разработчикам выполнять код в ответ на события без управления серверами.

### Основные концепции FaaS

**Function (Функция)**
Единица выполнения кода, которая обрабатывает событие.

**Trigger (Триггер)**
Событие, которое запускает выполнение функции.

**Runtime (Среда выполнения)**
Среда выполнения кода (Java, Python, Node.js и т.д.).

**Event Source (Источник событий)**
Источник событий, который запускает функцию (HTTP запрос, сообщение из очереди, изменение в базе данных и т.д.).

### Пример простой функции

```java
// AWS Lambda функция
public class HelloFunction implements RequestHandler<String, String> {
    
    @Override
    public String handleRequest(String input, Context context) {
        context.getLogger().log("Input: " + input);
        return "Hello, " + input + "!";
    }
}

// Spring Cloud Function
@SpringBootApplication
public class ServerlessApplication {
    
    @Bean
    public Function<String, String> hello() {
        return input -> "Hello, " + input + "!";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ServerlessApplication.class, args);
    }
}
```

## Backend as a Service (BaaS)

BaaS — это модель облачных вычислений, которая предоставляет готовые backend сервисы (база данных, аутентификация, хранилище файлов и т.д.).

### Примеры BaaS сервисов

**Firebase**
- Realtime Database
- Authentication
- Cloud Storage
- Cloud Functions

**AWS Amplify**
- Authentication
- API (GraphQL/REST)
- Storage
- Analytics

**Azure Mobile Apps**
- Authentication
- Data Sync
- Push Notifications
- Offline Support

## AWS Lambda

AWS Lambda — это сервис для выполнения кода без управления серверами.

### Создание Lambda функции на Java

```java
// Lambda Handler
public class OrderProcessor implements RequestHandler<OrderEvent, OrderResult> {
    
    private final OrderService orderService;
    
    public OrderProcessor() {
        // Инициализация зависимостей
        this.orderService = new OrderService();
    }
    
    @Override
    public OrderResult handleRequest(OrderEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Processing order: " + event.getOrderId());
        
        try {
            Order order = orderService.processOrder(event.getOrderId());
            return new OrderResult(order.getId(), "SUCCESS");
        } catch (Exception e) {
            logger.log("Error processing order: " + e.getMessage());
            throw new RuntimeException("Failed to process order", e);
        }
    }
}

// Event класс
public class OrderEvent {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    
    // Getters and setters
}

// Result класс
public class OrderResult {
    private String orderId;
    private String status;
    
    // Constructors, getters, setters
}
```

### Конфигурация Lambda

```yaml
# serverless.yml для Serverless Framework
service: order-processor

provider:
  name: aws
  runtime: java11
  region: us-east-1
  memorySize: 512
  timeout: 30

functions:
  processOrder:
    handler: com.example.OrderProcessor
    events:
      - http:
          path: orders
          method: post
      - sqs:
          arn: arn:aws:sqs:us-east-1:123456789:order-queue
```

### Использование Spring Boot с Lambda

```java
@SpringBootApplication
public class LambdaApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static SpringBootLambdaContainerHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handler;
    
    static {
        handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(LambdaApplication.class);
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input,
            Context context) {
        return handler.proxy(input, context);
    }
    
    @RestController
    @RequestMapping("/api")
    public static class OrderController {
        
        @PostMapping("/orders")
        public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(order);
        }
    }
}
```

## Azure Functions

Azure Functions — это сервис для выполнения кода без управления серверами в Azure.

### Создание Azure Function на Java

```java
@FunctionName("ProcessOrder")
public class OrderProcessor {
    
    @FunctionName("ProcessOrder")
    public HttpResponseMessage processOrder(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<OrderEvent>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing order");
        
        OrderEvent event = request.getBody().orElseThrow(() -> 
            new IllegalArgumentException("Order event is required"));
        
        OrderService orderService = new OrderService();
        Order order = orderService.processOrder(event.getOrderId());
        
        return request.createResponseBuilder(HttpStatus.OK)
            .body(new OrderResult(order.getId(), "SUCCESS"))
            .build();
    }
}
```

### Конфигурация Azure Function

```json
{
  "scriptFile": "order-processor.jar",
  "entryPoint": "com.example.OrderProcessor.processOrder",
  "bindings": [
    {
      "type": "httpTrigger",
      "name": "req",
      "direction": "in",
      "authLevel": "function",
      "methods": ["post"]
    },
    {
      "type": "http",
      "name": "$return",
      "direction": "out"
    }
  ]
}
```

## Google Cloud Functions

Google Cloud Functions — это сервис для выполнения кода без управления серверами в Google Cloud.

### Создание Cloud Function на Java

```java
public class OrderProcessor implements HttpFunction {
    
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Gson gson = new Gson();
        
        OrderEvent event = gson.fromJson(
            request.getReader(),
            OrderEvent.class
        );
        
        OrderService orderService = new OrderService();
        Order order = orderService.processOrder(event.getOrderId());
        
        OrderResult result = new OrderResult(order.getId(), "SUCCESS");
        response.getWriter().write(gson.toJson(result));
    }
}
```

## Spring Cloud Function

Spring Cloud Function предоставляет абстракцию для создания serverless функций, которые могут работать на различных платформах.

### Создание функции

```java
@SpringBootApplication
public class ServerlessApplication {
    
    @Bean
    public Function<String, String> uppercase() {
        return input -> input.toUpperCase();
    }
    
    @Bean
    public Function<OrderEvent, OrderResult> processOrder() {
        return event -> {
            OrderService orderService = new OrderService();
            Order order = orderService.processOrder(event.getOrderId());
            return new OrderResult(order.getId(), "SUCCESS");
        };
    }
    
    @Bean
    public Consumer<String> logMessage() {
        return message -> System.out.println("Received: " + message);
    }
    
    @Bean
    public Supplier<String> generateMessage() {
        return () -> "Hello from serverless function!";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ServerlessApplication.class, args);
    }
}
```

### Конфигурация для AWS Lambda

```yaml
spring:
  cloud:
    function:
      definition: processOrder
    aws:
      lambda:
        handler: processOrder
```

### Конфигурация для Azure Functions

```yaml
spring:
  cloud:
    function:
      definition: processOrder
    azure:
      function:
        definition: processOrder
```

## Event-driven Serverless Patterns

### HTTP Trigger

```java
@SpringBootApplication
public class HttpFunctionApplication {
    
    @Bean
    public Function<HttpRequest, HttpResponse> httpFunction() {
        return request -> {
            String body = request.getBody();
            // Обработка запроса
            return HttpResponse.ok()
                .body("Processed: " + body)
                .build();
        };
    }
}
```

### Message Queue Trigger

```java
@SpringBootApplication
public class QueueFunctionApplication {
    
    @Bean
    public Function<Message<String>, Void> queueFunction() {
        return message -> {
            String payload = message.getPayload();
            // Обработка сообщения
            processMessage(payload);
            return null;
        };
    }
    
    private void processMessage(String payload) {
        // Логика обработки
    }
}
```

### Database Trigger

```java
@SpringBootApplication
public class DatabaseFunctionApplication {
    
    @Bean
    public Function<ChangeEvent, Void> databaseFunction() {
        return changeEvent -> {
            // Обработка изменения в базе данных
            if (changeEvent.getEventType() == EventType.INSERT) {
                processInsert(changeEvent);
            }
            return null;
        };
    }
}
```

### Scheduled Trigger

```java
@SpringBootApplication
public class ScheduledFunctionApplication {
    
    @Bean
    public Supplier<String> scheduledFunction() {
        return () -> {
            // Выполнение по расписанию
            return "Scheduled task executed at " + Instant.now();
        };
    }
}
```

## Cold Start проблема

Cold Start — это задержка при первом запуске функции после периода бездействия.

### Стратегии уменьшения Cold Start

**1. Provisioned Concurrency**

Поддержание определенного количества готовых экземпляров функции.

```yaml
# serverless.yml
functions:
  processOrder:
    handler: com.example.OrderProcessor
    provisionedConcurrency: 2
```

**2. Оптимизация размера функции**

Уменьшение размера JAR файла и зависимостей.

```xml
<!-- Использование Spring Boot Thin JAR -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot.experimental</groupId>
            <artifactId>spring-boot-thin-layout</artifactId>
            <version>1.0.28.RELEASE</version>
        </dependency>
    </dependencies>
</plugin>
```

**3. Инициализация вне handler**

```java
public class OrderProcessor implements RequestHandler<OrderEvent, OrderResult> {
    
    // Инициализация при загрузке класса
    private static final OrderService orderService = initializeService();
    
    private static OrderService initializeService() {
        // Инициализация сервиса
        return new OrderService();
    }
    
    @Override
    public OrderResult handleRequest(OrderEvent event, Context context) {
        // Использование предварительно инициализированного сервиса
        return orderService.processOrder(event);
    }
}
```

**4. Использование GraalVM Native Image**

Компиляция в нативный образ для уменьшения времени запуска.

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.example.ServerlessApplication</mainClass>
    </configuration>
</plugin>
```

## Best Practices

### 1. Идемпотентность функций

Функции должны быть идемпотентными для безопасной повторной обработки.

```java
@Bean
public Function<OrderEvent, OrderResult> processOrder() {
    return event -> {
        // Проверка, не обработан ли уже заказ
        if (orderRepository.existsById(event.getOrderId())) {
            return orderRepository.findById(event.getOrderId())
                .map(order -> new OrderResult(order.getId(), "ALREADY_PROCESSED"))
                .orElseThrow();
        }
        
        // Обработка заказа
        Order order = orderService.processOrder(event.getOrderId());
        return new OrderResult(order.getId(), "SUCCESS");
    };
}
```

### 2. Обработка ошибок

Реализуйте правильную обработку ошибок и retry механизмы.

```java
@Bean
public Function<OrderEvent, OrderResult> processOrder() {
    return event -> {
        try {
            Order order = orderService.processOrder(event.getOrderId());
            return new OrderResult(order.getId(), "SUCCESS");
        } catch (TransientException e) {
            // Временная ошибка - можно повторить
            throw new RetryableException("Transient error", e);
        } catch (Exception e) {
            // Постоянная ошибка - не повторять
            log.error("Failed to process order", e);
            return new OrderResult(event.getOrderId(), "FAILED");
        }
    };
}
```

### 3. Логирование и мониторинг

Используйте структурированное логирование и мониторинг.

```java
public class OrderProcessor implements RequestHandler<OrderEvent, OrderResult> {
    
    private static final Logger log = LoggerFactory.getLogger(OrderProcessor.class);
    
    @Override
    public OrderResult handleRequest(OrderEvent event, Context context) {
        MDC.put("orderId", event.getOrderId());
        MDC.put("requestId", context.getAwsRequestId());
        
        try {
            log.info("Processing order");
            Order order = orderService.processOrder(event.getOrderId());
            log.info("Order processed successfully");
            return new OrderResult(order.getId(), "SUCCESS");
        } catch (Exception e) {
            log.error("Failed to process order", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

### 4. Ограничение размера функций

Держите функции небольшими и сфокусированными на одной задаче.

```java
// Хорошо: небольшая, сфокусированная функция
@Bean
public Function<OrderEvent, OrderResult> processOrder() {
    return event -> orderService.processOrder(event.getOrderId());
}

// Плохо: функция делает слишком много
@Bean
public Function<OrderEvent, OrderResult> processOrderAndSendEmailAndUpdateInventory() {
    return event -> {
        // Слишком много ответственности
    };
}
```

### 5. Использование внешних конфигураций

Храните конфигурацию вне кода функции.

```java
@Configuration
public class FunctionConfig {
    
    @Value("${database.url}")
    private String databaseUrl;
    
    @Value("${api.key}")
    private String apiKey;
    
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(databaseUrl)
            .build();
    }
}
```

## Troubleshooting

### Проблема: Cold Start задержки

**Решение:**
- Используйте Provisioned Concurrency
- Оптимизируйте размер функции
- Используйте GraalVM Native Image
- Инициализируйте ресурсы вне handler

### Проблема: Таймауты функций

**Решение:**
- Увеличьте timeout для функции
- Оптимизируйте производительность кода
- Используйте асинхронную обработку

```yaml
functions:
  processOrder:
    handler: com.example.OrderProcessor
    timeout: 60  # Увеличение timeout
```

### Проблема: Ограничения памяти

**Решение:**
- Увеличьте выделенную память
- Оптимизируйте использование памяти в коде

```yaml
functions:
  processOrder:
    handler: com.example.OrderProcessor
    memorySize: 1024  # Увеличение памяти
```

### Проблема: Ошибки выполнения

**Решение:**
- Реализуйте правильную обработку ошибок
- Используйте Dead Letter Queue для проблемных сообщений
- Логируйте ошибки для отладки

---

**Дата последнего обновления:** 2026-01-23

