---
title: "Event-Driven Architecture"
description: "Event-Driven Architecture (EDA) — это архитектурный паттерн, в котором компоненты системы взаимодействуют через асинхронную передачу событий. Это позволяет создавать слабосвязанные, масштабируемые и отзывчивые системы, где компоненты реагируют на события, происходящие в системе."
tags:
  - architecture
  - event-driven
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Event-Driven Architecture

**Event-Driven Architecture** (**EDA**) — это архитектурный паттерн, в котором компоненты системы взаимодействуют через асинхронную передачу событий. Это позволяет создавать слабосвязанные, масштабируемые и отзывчивые системы, где компоненты реагируют на события, происходящие в системе.

## Полезные ссылки

### Официальная документация
- [Spring Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/)
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [Event-Driven Architecture (Martin Fowler)](https://martinfowler.com/articles/201701-event-driven.html)

### См. также
- [[architecture-patterns|Архитектурные паттерны]] — общие архитектурные паттерны
- [[cqrs|CQRS]] — **CQRS** паттерн
- [[event-sourcing|Event Sourcing]] — **Event Sourcing**
- [[microservices|Микросервисы]] — микросервисная архитектура
- [[kafka|Apache Kafka]] — **Apache Kafka**
- [[rabbitmq|RabbitMQ]] — **RabbitMQ**

## Содержание

- [Введение в **Event-Driven Architecture**](#введение-в-event-driven-architecture)
  - [Преимущества **Event-Driven Architecture**](#преимущества-event-driven-architecture)
  - [Недостатки **Event-Driven Architecture**](#недостатки-event-driven-architecture)
- [Основные концепции](#основные-концепции)
  - [Событие (**Event**)](#событие-event)
  - [**Event Producer** (**Производитель событий**)](#event-producer-производитель-событий)
  - [**Event Consumer** (**Потребитель событий**)](#event-consumer-потребитель-событий)
  - [**Event Bus**](#event-bus)
- [**Event Producers** и **Consumers**](#event-producers-и-consumers)
  - [Паттерны **Producer**](#паттерны-producer)
  - [Паттерны **Consumer**](#паттерны-consumer)
- [**Event Bus** и **Message Brokers**](#event-bus-и-message-brokers)
  - [Выбор **Message Broker**](#выбор-message-broker)
  - [**Spring Cloud Stream**](#spring-cloud-stream)
- [application.yml](#applicationyml)
- [**Event Sourcing** vs **Event Streaming**](#event-sourcing-vs-event-streaming)
  - [**Event Sourcing**](#event-sourcing)
  - [**Event Streaming**](#event-streaming)
  - [Когда использовать **Event Sourcing**](#когда-использовать-event-sourcing)
  - [Когда использовать **Event Streaming**](#когда-использовать-event-streaming)
- [**Saga Pattern** для распределенных транзакций](#saga-pattern-для-распределенных-транзакций)
  - [**Choreography-based Saga**](#choreography-based-saga)
  - [**Orchestration-based Saga**](#orchestration-based-saga)
- [**Event Choreography** vs **Orchestration**](#event-choreography-vs-orchestration)
  - [**Event Choreography**](#event-choreography)
  - [**Orchestration**](#orchestration)
  - [Выбор подхода](#выбор-подхода)
- [Реализация на **Spring Cloud Stream**](#реализация-на-spring-cloud-stream)
  - [Настройка проекта](#настройка-проекта)
  - [Определение **Channels**](#определение-channels)
  - [**Producer**](#producer)
  - [**Consumer**](#consumer)
  - [Конфигурация](#конфигурация)
- [Реализация на **Apache Kafka**](#реализация-на-apache-kafka)
  - [**Producer Configuration**](#producer-configuration)
  - [**Consumer Configuration**](#consumer-configuration)
  - [Использование](#использование)
- [Обработка ошибок и **Retry** механизмы](#обработка-ошибок-и-retry-механизмы)
  - [**Retry Policy**](#retry-policy)
  - [**Dead Letter Queue**](#dead-letter-queue)
  - [**Circuit Breaker**](#circuit-breaker)
- [Мониторинг и **Observability**](#мониторинг-и-observability)
  - [Метрики для мониторинга](#метрики-для-мониторинга)
  - [**Distributed Tracing**](#distributed-tracing)
- [Лучшие практики](#лучшие-практики)
  - [1. Идемпотентность](#1-идемпотентность)
  - [2. Версионирование событий](#2-версионирование-событий)
  - [3. Разделение событий по типам](#3-разделение-событий-по-типам)
  - [4. Обработка порядка событий](#4-обработка-порядка-событий)
- [**Anti-patterns**](#anti-patterns)
  - [1. Синхронное ожидание событий](#1-синхронное-ожидание-событий)
  - [2. Использование событий для запросов данных](#2-использование-событий-для-запросов-данных)
  - [3. Игнорирование дубликатов](#3-игнорирование-дубликатов)
- [Решение проблем](#решение-проблем)
  - [Проблема: События не доставляются](#проблема-события-не-доставляются)
  - [Проблема: Дублирование событий](#проблема-дублирование-событий)
  - [Проблема: Потеря событий](#проблема-потеря-событий)
  - [Проблема: Медленная обработка событий](#проблема-медленная-обработка-событий)
- [Частые вопросы](#частые-вопросы)

## Введение в **Event-Driven Architecture**

**Event-Driven Architecture** представляет собой парадигму проектирования, где компоненты системы взаимодействуют через события. Событие — это значимое изменение состояния системы, которое может быть обработано одним или несколькими компонентами.

### Преимущества **Event-Driven Architecture**

**Слабая связанность (**Loose Coupling**)**
Компоненты не знают друг о друге напрямую, они взаимодействуют только через события. Это позволяет изменять один компонент без влияния на другие.

**Масштабируемость**
Компоненты могут масштабироваться независимо в зависимости от нагрузки на обработку событий.

**Отзывчивость (**Responsiveness**)**
Система может реагировать на события в реальном времени, обеспечивая быструю обработку и низкую задержку.

**Гибкость**
Новые компоненты могут легко подключаться к системе, подписываясь на интересующие их события.

**Отказоустойчивость**
При сбое одного компонента другие продолжают работать, так как события могут быть обработаны позже.

### Недостатки **Event-Driven Architecture**

**Сложность отладки**
Асинхронная природа событий усложняет отслеживание потока выполнения и отладку.

**Eventual Consistency**
Система может находиться в несогласованном состоянии в течение некоторого времени, что требует дополнительных механизмов для обеспечения согласованности.

**Сложность тестирования**
Тестирование асинхронных событийных потоков сложнее, чем синхронных вызовов.

**Дублирование событий**
Необходимо обрабатывать случаи повторной доставки событий и идемпотентность операций.

## Основные концепции

### Событие (**Event**)

**Событие представляет собой неизменяемый факт о том, что что-то произошло в системе. События обычно содержат:**
- Уникальный идентификатор события
- Тип события
- Временную метку
- Данные события
- Метаданные (**источник, версия и т.д.**)

Ниже — пример определения события домена (**Java**).
```java
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String eventType;

    protected DomainEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public String getEventType() {
        return eventType;
    }
}

public class UserCreatedEvent extends DomainEvent {
    private final String userId;
    private final String email;
    private final String name;

    public UserCreatedEvent(String userId, String email, String name) {
        super("UserCreated");
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
```

### **Event Producer** (**Производитель событий**)

**Event Producer** — это компонент, который создает и публикует события в систему. **Producer** не знает, кто будет обрабатывать события.

```java
@Component
public class UserEventProducer {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreated(User user) {
        UserCreatedEvent event = new UserCreatedEvent(
            user.getId(),
            user.getEmail(),
            user.getName()
        );

        kafkaTemplate.send("user-events", user.getId(), event);
    }
}
```

### **Event Consumer** (**Потребитель событий**)

**Event Consumer** — это компонент, который подписывается на события и обрабатывает их. **Consumer** может быть одним из многих обработчиков одного и того же события.

```java
@Component
public class UserEventConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    public UserEventConsumer(EmailService emailService,
                            NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "user-events", groupId = "user-handlers")
    public void handleUserCreated(UserCreatedEvent event) {
        // Отправка приветственного email
        emailService.sendWelcomeEmail(event.getEmail(), event.getName());

        // Отправка уведомления администратору
        notificationService.notifyAdmin("New user created: " + event.getEmail());
    }
}
```

### **Event Bus**

**Event Bus** — это инфраструктурный компонент, который обеспечивает доставку событий от **producers** к **consumers**. **Event Bus** может быть реализован различными способами:**
- **Message Broker** (**Kafka, `RabbitMQ`, ActiveMQ**)
- **Event Store** (**EventStore, `Axon` Server**)
- **In-memory Event Bus** (**для простых случаев**)

## **Event Producers** и **Consumers**

### Паттерны **Producer**

**Fire-and-Forget**
**Producer** отправляет событие и не ждет подтверждения. Подходит для случаев, когда потеря события не критична.

```java
@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );

        // Fire-and-forget отправка
        kafkaTemplate.send("order-events", order.getId(), event);
    }
}
```

**Request-Reply Pattern**
**Producer** отправляет событие и ждет ответного события. Используется для синхронного взаимодействия через события.

```java
@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, CompletableFuture<OrderResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<OrderResponse> createOrderAndWait(OrderRequest request) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<OrderResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        OrderCreatedEvent event = new OrderCreatedEvent(
            correlationId,
            request.getCustomerId(),
            request.getItems()
        );

        kafkaTemplate.send("order-commands", correlationId, event);

        // Таймаут для ответа
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
                if (!future.isDone()) {
                    pendingRequests.remove(correlationId);
                    future.completeExceptionally(new TimeoutException());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return future;
    }

    @KafkaListener(topics = "order-responses")
    public void handleOrderResponse(OrderResponseEvent event) {
        CompletableFuture<OrderResponse> future = pendingRequests.remove(event.getCorrelationId());
        if (future != null) {
            future.complete(event.getResponse());
        }
    }
}
```

### Паттерны **Consumer**

**Single Consumer**
Один **consumer** обрабатывает все события. Подходит для критичных операций, где важно обработать каждое событие.

```java
@Component
public class PaymentProcessor {

    @KafkaListener(topics = "order-events", groupId = "payment-processor")
    public void processPayment(OrderCreatedEvent event) {
        // Обработка платежа
        Payment payment = new Payment(event.getOrderId(), event.getAmount());
        paymentService.processPayment(payment);
    }
}
```

**Multiple Consumers** (Fan-out)
Несколько **consumers** обрабатывают одно и то же событие независимо. Каждый **consumer** имеет свою **consumer group**.

```java
// Consumer 1: Отправка email
@Component
public class EmailNotificationConsumer {

    @KafkaListener(topics = "order-events", groupId = "email-notifications")
    public void sendOrderConfirmationEmail(OrderCreatedEvent event) {
        emailService.sendOrderConfirmation(event.getOrderId(), event.getCustomerId());
    }
}

// Consumer 2: Обновление инвентаря
@Component
public class InventoryUpdateConsumer {

    @KafkaListener(topics = "order-events", groupId = "inventory-updates")
    public void updateInventory(OrderCreatedEvent event) {
        inventoryService.reserveItems(event.getOrderId(), event.getItems());
    }
}

// Consumer 3: Аналитика
@Component
public class AnalyticsConsumer {

    @KafkaListener(topics = "order-events", groupId = "analytics")
    public void trackOrder(OrderCreatedEvent event) {
        analyticsService.trackOrderCreated(event.getOrderId(), event.getAmount());
    }
}
```java

**Competing Consumers**
Несколько **consumers** в одной группе конкурируют за обработку событий. Используется для распределения нагрузки.

```java
@Component
public class OrderProcessor {

    // Несколько экземпляров этого сервиса будут конкурировать за события
    @KafkaListener(topics = "order-events", groupId = "order-processors")
    public void processOrder(OrderCreatedEvent event) {
        // Обработка заказа
        orderService.processOrder(event.getOrderId());
    }
}
```

## **Event Bus** и **Message Brokers**

### Выбор **Message Broker**

**Apache Kafka**
- Высокая пропускная способность
- Долговременное хранение событий
- Поддержка **replay** событий
- Идеален для **event streaming** и **event sourcing**

```java
@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, DomainEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, DomainEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, DomainEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "event-consumers");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        return factory;
    }
}
```

**RabbitMQ**
- Гибкая маршрутизация через **exchanges**
- Поддержка различных паттернов обмена сообщениями
- Хорошая поддержка **request-reply**
- Идеален для **task queues** и **workflow**

```java
@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange("events", true, false);
    }

    @Bean
    public Queue userEventsQueue() {
        return QueueBuilder.durable("user-events").build();
    }

    @Bean
    public Binding userEventsBinding() {
        return BindingBuilder
            .bind(userEventsQueue())
            .to(eventExchange())
            .with("user.*");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("user-events");
        container.setMessageListener(new MessageListenerAdapter(new UserEventHandler()));
        return container;
    }
}
```

**Redis Pub/Sub**
- Простота использования
- Низкая задержка
- Подходит для **real-time** уведомлений
- Не гарантирует доставку

```java
@Component
public class RedisEventPublisher {

    private final StringRedisTemplate redisTemplate;

    public RedisEventPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishEvent(String channel, DomainEvent event) {
        String message = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(channel, message);
    }
}

@Component
public class RedisEventSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        // Обработка события
        handleEvent(channel, body);
    }
}
```

### **Spring Cloud Stream**

**Spring Cloud Stream** предоставляет абстракцию над различными **message brokers**, позволяя легко переключаться между ними.

```java
@SpringBootApplication
@EnableBinding(UserProcessor.class)
public class EventDrivenApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventDrivenApplication.class, args);
    }
}

interface UserProcessor {
    String INPUT = "userEvents";
    String OUTPUT = "userCommands";

    @Input(INPUT)
    SubscribableChannel userEvents();

    @Output(OUTPUT)
    MessageChannel userCommands();
}

@Component
public class UserEventHandler {

    @StreamListener(UserProcessor.INPUT)
    public void handleUserCreated(UserCreatedEvent event) {
        // Обработка события
        System.out.println("User created: " + event.getEmail());
    }

    @Autowired
    private UserProcessor processor;

    public void publishUserCommand(UserCommand command) {
        processor.userCommands().send(MessageBuilder.withPayload(command).build());
    }
}
```

```yaml
# application.yml
spring:
  cloud:
    stream:
      bindings:
        userEvents:
          destination: user-events
          group: user-handlers
          content-type: application/json
        userCommands:
          destination: user-commands
          content-type: application/json
      kafka:
        binder:
          brokers: localhost:9092
          defaultBrokerPort: 9092
```

## **Event Sourcing** vs **Event Streaming**

### **Event Sourcing**

**Event Sourcing** — это паттерн, где состояние приложения определяется последовательностью событий. Вместо хранения текущего состояния, хранятся все события, которые привели к этому состоянию.

**Характеристики:**
- События являются источником истины
- Состояние восстанавливается путем **replay** событий
- Полная история изменений
- Поддержка временных запросов (**time-travel queries**)

```java
public class UserAggregate {
    private String userId;
    private String email;
    private String name;
    private boolean active;

    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    public static UserAggregate create(String userId, String email, String name) {
        UserAggregate user = new UserAggregate();
        user.apply(new UserCreatedEvent(userId, email, name));
        return user;
    }

    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("User already deactivated");
        }
        apply(new UserDeactivatedEvent(userId));
    }

    private void apply(DomainEvent event) {
        // Обновление состояния на основе события
        if (event instanceof UserCreatedEvent) {
            UserCreatedEvent e = (UserCreatedEvent) event;
            this.userId = e.getUserId();
            this.email = e.getEmail();
            this.name = e.getName();
            this.active = true;
        } else if (event instanceof UserDeactivatedEvent) {
            this.active = false;
        }

        uncommittedEvents.add(event);
    }

    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    // Восстановление состояния из событий
    public static UserAggregate fromHistory(List<DomainEvent> events) {
        UserAggregate user = new UserAggregate();
        events.forEach(user::apply);
        user.uncommittedEvents.clear();
        return user;
    }
}
```

### **Event Streaming**

**Event Streaming** — это паттерн, где события используются для передачи данных между компонентами в реальном времени. События могут быть временными и не обязательно хранятся долго.

**Характеристики:**
- События используются для коммуникации
- Текущее состояние хранится отдельно
- Фокус на **real-time** обработке
- События могут быть удалены после обработки

```java
@Component
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public Order createOrder(OrderRequest request) {
        // Создание заказа в базе данных
        Order order = new Order(
            UUID.randomUUID().toString(),
            request.getCustomerId(),
            request.getItems()
        );
        orderRepository.save(order);

        // Публикация события для других сервисов
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
        kafkaTemplate.send("order-events", order.getId(), event);

        return order;
    }
}
```

### Когда использовать **Event Sourcing**

**Event Sourcing** уместен, когда:

- Требуется полная история изменений
- Нужна возможность восстановления состояния на любой момент времени
- Аудит и **compliance** требования
- Сложная бизнес-логика с множеством состояний

### Когда использовать **Event Streaming**

**Event Streaming** подходит для сценариев, где:

- **Real-time** обработка данных
- Интеграция между микросервисами
- Обработка потоков данных
- Не требуется полная история событий

## **Saga Pattern** для распределенных транзакций

**Saga Pattern** используется для управления распределенными транзакциями в микросервисной архитектуре. Вместо использования двухфазного коммита (**2PC**), **Saga** разбивает транзакцию на последовательность локальных транзакций с компенсирующими действиями.

### **Choreography-based Saga**

В **Choreography-based Saga** каждый сервис знает, какие события слушать и какие события публиковать. Нет центрального координатора.

```java
// Order Service
@Component
public class OrderSaga {

    @KafkaListener(topics = "order-commands", groupId = "order-service")
    public void handleCreateOrder(CreateOrderCommand command) {
        Order order = orderService.createOrder(command);

        // Публикация события для следующего шага
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getAmount());
        kafkaTemplate.send("order-events", order.getId(), event);
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-service")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // Компенсирующее действие: отмена заказа
        orderService.cancelOrder(event.getOrderId());
    }
}

// Payment Service
@Component
public class PaymentSaga {

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            paymentService.processPayment(event.getOrderId(), event.getAmount());

            // Публикация успешного события
            PaymentSucceededEvent successEvent = new PaymentSucceededEvent(event.getOrderId());
            kafkaTemplate.send("payment-events", event.getOrderId(), successEvent);
        } catch (PaymentException e) {
            // Публикация события об ошибке
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.getOrderId(), e.getMessage());
            kafkaTemplate.send("payment-events", event.getOrderId(), failedEvent);
        }
    }
}

// Inventory Service
@Component
public class InventorySaga {

    @KafkaListener(topics = "payment-events", groupId = "inventory-service")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        try {
            inventoryService.reserveItems(event.getOrderId());

            InventoryReservedEvent reservedEvent = new InventoryReservedEvent(event.getOrderId());
            kafkaTemplate.send("inventory-events", event.getOrderId(), reservedEvent);
        } catch (InsufficientInventoryException e) {
            // Компенсация: возврат платежа
            RefundPaymentCommand refundCommand = new RefundPaymentCommand(event.getOrderId());
            kafkaTemplate.send("payment-commands", event.getOrderId(), refundCommand);
        }
    }

    @KafkaListener(topics = "payment-failed", groupId = "inventory-service")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // Ничего не делаем, так как резервирование еще не произошло
    }
}
```

### **Orchestration-based Saga**

В **Orchestration-based Saga** есть центральный координатор (**orchestrator**), который управляет выполнением шагов **Saga**.

```java
@Component
public class OrderOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, SagaState> sagaStates = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-commands", groupId = "order-orchestrator")
    public void handleCreateOrder(CreateOrderCommand command) {
        String sagaId = UUID.randomUUID().toString();
        SagaState state = new SagaState(sagaId, command);
        sagaStates.put(sagaId, state);

        // Шаг 1: Создание заказа
        CreateOrderCommand orderCommand = new CreateOrderCommand(
            sagaId,
            command.getCustomerId(),
            command.getItems()
        );
        kafkaTemplate.send("order-commands", sagaId, orderCommand);
    }

    @KafkaListener(topics = "order-events", groupId = "order-orchestrator")
    public void handleOrderCreated(OrderCreatedEvent event) {
        SagaState state = sagaStates.get(event.getSagaId());
        if (state == null) return;

        state.setOrderId(event.getOrderId());

        // Шаг 2: Обработка платежа
        ProcessPaymentCommand paymentCommand = new ProcessPaymentCommand(
            event.getSagaId(),
            event.getOrderId(),
            event.getAmount()
        );
        kafkaTemplate.send("payment-commands", event.getSagaId(), paymentCommand);
    }

    @KafkaListener(topics = "payment-events", groupId = "order-orchestrator")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        SagaState state = sagaStates.get(event.getSagaId());
        if (state == null) return;

        // Шаг 3: Резервирование инвентаря
        ReserveInventoryCommand inventoryCommand = new ReserveInventoryCommand(
            event.getSagaId(),
            event.getOrderId(),
            state.getItems()
        );
        kafkaTemplate.send("inventory-commands", event.getSagaId(), inventoryCommand);
    }

    @KafkaListener(topics = "inventory-events", groupId = "order-orchestrator")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        SagaState state = sagaStates.get(event.getSagaId());
        if (state == null) return;

        // Saga завершена успешно
        state.setStatus(SagaStatus.COMPLETED);
        sagaStates.remove(event.getSagaId());

        OrderCompletedEvent completedEvent = new OrderCompletedEvent(event.getOrderId());
        kafkaTemplate.send("order-events", event.getSagaId(), completedEvent);
    }

    @KafkaListener(topics = {"payment-failed", "inventory-failed"}, groupId = "order-orchestrator")
    public void handleFailure(SagaFailureEvent event) {
        SagaState state = sagaStates.get(event.getSagaId());
        if (state == null) return;

        // Выполнение компенсирующих действий
        compensateSaga(state, event);
    }

    private void compensateSaga(SagaState state, SagaFailureEvent event) {
        // Откат выполненных шагов в обратном порядке
        if (state.getInventoryReserved()) {
            ReleaseInventoryCommand releaseCommand = new ReleaseInventoryCommand(state.getOrderId());
            kafkaTemplate.send("inventory-commands", state.getSagaId(), releaseCommand);
        }

        if (state.getPaymentProcessed()) {
            RefundPaymentCommand refundCommand = new RefundPaymentCommand(state.getOrderId());
            kafkaTemplate.send("payment-commands", state.getSagaId(), refundCommand);
        }

        if (state.getOrderId() != null) {
            CancelOrderCommand cancelCommand = new CancelOrderCommand(state.getOrderId());
            kafkaTemplate.send("order-commands", state.getSagaId(), cancelCommand);
        }

        state.setStatus(SagaStatus.COMPENSATED);
        sagaStates.remove(state.getSagaId());
    }
}

class SagaState {
    private final String sagaId;
    private final CreateOrderCommand originalCommand;
    private String orderId;
    private boolean paymentProcessed;
    private boolean inventoryReserved;
    private SagaStatus status = SagaStatus.IN_PROGRESS;

    // Getters and setters
}
```

## **Event Choreography** vs **Orchestration**

### **Event Choreography**

В **Event Choreography** каждый сервис знает, какие события слушать и какие публиковать. Нет центрального координатора.

**Преимущества:**
- Слабая связанность
- Простота добавления новых участников
- Отказоустойчивость (**нет единой точки отказа**)

**Недостатки:**
- Сложность понимания потока выполнения
- Сложность отладки
- Риск циклических зависимостей

### **Orchestration**

В **Orchestration** есть центральный координатор, который управляет выполнением шагов.

**Преимущества:**
- Ясный поток выполнения
- Легче отлаживать
- Централизованное управление компенсацией

**Недостатки:**
- Центральная точка отказа
- Более тесная связанность
- Координатор может стать узким местом

### Выбор подхода

**Используйте `Choreography` когда:**
- Простые **workflows**
- Низкая связанность критична
- Много независимых участников

**Используйте `Orchestration` когда:**
- Сложные **workflows** с множеством шагов
- Нужен четкий контроль над выполнением
- Важна простота отладки

## Реализация на **Spring Cloud Stream**

**Spring Cloud Stream** предоставляет удобную абстракцию для работы с **event-driven** архитектурой.

### Настройка проекта

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-stream-binder-kafka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
</dependencies>
```

### Определение **Channels**

```java
public interface EventChannels {
    String USER_EVENTS = "userEvents";
    String ORDER_EVENTS = "orderEvents";
    String PAYMENT_EVENTS = "paymentEvents";

    @Input(USER_EVENTS)
    SubscribableChannel userEvents();

    @Output(ORDER_EVENTS)
    MessageChannel orderEvents();

    @Output(PAYMENT_EVENTS)
    MessageChannel paymentEvents();
}
```

### **Producer**

```java
@Component
@EnableBinding(EventChannels.class)
public class OrderEventProducer {

    private final EventChannels channels;

    public OrderEventProducer(EventChannels channels) {
        this.channels = channels;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );

        channels.orderEvents().send(
            MessageBuilder
                .withPayload(event)
                .setHeader("eventType", "OrderCreated")
                .setHeader("correlationId", UUID.randomUUID().toString())
                .build()
        );
    }
}
```

### **Consumer**

```java
@Component
@EnableBinding(EventChannels.class)
public class OrderEventConsumer {

    @StreamListener(target = EventChannels.USER_EVENTS, condition = "headers['eventType']=='UserCreated'")
    public void handleUserCreated(UserCreatedEvent event) {
        // Обработка события
        System.out.println("User created: " + event.getEmail());
    }

    @StreamListener(EventChannels.ORDER_EVENTS)
    public void handleOrderEvent(OrderEvent event, @Header("eventType") String eventType) {
        switch (eventType) {
            case "OrderCreated":
                handleOrderCreated((OrderCreatedEvent) event);
                break;
            case "OrderCancelled":
                handleOrderCancelled((OrderCancelledEvent) event);
                break;
        }
    }

    private void handleOrderCreated(OrderCreatedEvent event) {
        // Логика обработки
    }

    private void handleOrderCancelled(OrderCancelledEvent event) {
        // Логика обработки
    }
}
```

### Конфигурация

```yaml
spring:
  cloud:
    stream:
      bindings:
        userEvents:
          destination: user-events
          group: user-handlers
          content-type: application/json
          consumer:
            max-attempts: 3
            back-off-initial-interval: 1000
            back-off-max-interval: 10000
            back-off-multiplier: 2.0
        orderEvents:
          destination: order-events
          content-type: application/json
          producer:
            error-channel-enabled: true
      kafka:
        binder:
          brokers: localhost:9092
          defaultBrokerPort: 9092
        bindings:
          userEvents:
            consumer:
              enable-auto-commit: false
              auto-offset-reset: earliest
```

## Реализация на **Apache Kafka**

### **Producer Configuration**

```java
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, DomainEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Надежность доставки
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        // Идемпотентность
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Сжатие
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        // Батчинг
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, DomainEvent> kafkaTemplate() {
        KafkaTemplate<String, DomainEvent> template = new KafkaTemplate<>(producerFactory());
        template.setProducerListener(new LoggingProducerListener<>());
        return template;
    }
}
```

### **Consumer Configuration**

```java
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, DomainEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "event-consumers");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Настройки offset
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Настройки производительности
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DomainEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
```

### Использование

```java
@Component
public class OrderEventService {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public OrderEventService(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );

        ListenableFuture<SendResult<String, DomainEvent>> future =
            kafkaTemplate.send("order-events", order.getId(), event);

        future.addCallback(
            result -> log.info("Event sent: {}", result.getProducerRecord().value()),
            failure -> log.error("Failed to send event", failure)
        );
    }
}

@Component
public class OrderEventHandler {

    @KafkaListener(topics = "order-events", groupId = "order-handlers")
    public void handleOrderEvent(
            OrderEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            // Обработка события
            processOrderEvent(event);

            // Подтверждение обработки
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event", e);
            // В случае ошибки событие не подтверждается и будет обработано повторно
        }
    }

    private void processOrderEvent(OrderEvent event) {
        // Логика обработки
    }
}
```

## Обработка ошибок и **Retry** механизмы

### **Retry Policy**

```java
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Exponential backoff
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
```

### **Dead Letter Queue**

```java
@Component
public class OrderEventConsumer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    @KafkaListener(topics = "order-events", groupId = "order-handlers")
    public void handleOrderEvent(OrderEvent event, Acknowledgment acknowledgment) {
        try {
            retryTemplate.execute(context -> {
                processOrderEvent(event);
                return null;
            });
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process event after retries", e);
            // Отправка в Dead Letter Queue
            sendToDeadLetterQueue(event, e);
            acknowledgment.acknowledge(); // Подтверждаем, чтобы не обрабатывать повторно
        }
    }

    private void sendToDeadLetterQueue(OrderEvent event, Exception error) {
        FailedEvent failedEvent = new FailedEvent(event, error.getMessage());
        kafkaTemplate.send("order-events-dlq", event.getId(), failedEvent);
    }
}
```

### **Circuit Breaker**

```java
@Component
public class OrderEventProcessor {

    private final CircuitBreaker circuitBreaker;

    @PostConstruct
    public void init() {
        circuitBreaker = CircuitBreaker.of("order-processor", CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .build());
    }

    @KafkaListener(topics = "order-events", groupId = "order-handlers")
    public void handleOrderEvent(OrderEvent event, Acknowledgment acknowledgment) {
        Try.ofSupplier(CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
            processOrderEvent(event);
            return null;
        }))
        .onSuccess(result -> acknowledgment.acknowledge())
        .onFailure(error -> {
            log.error("Circuit breaker opened or processing failed", error);
            sendToDeadLetterQueue(event, error);
            acknowledgment.acknowledge();
        });
    }
}
```

## Мониторинг и **Observability**

### Метрики для мониторинга

```java
@Component
public class EventMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter eventsPublished;
    private final Counter eventsConsumed;
    private final Timer eventProcessingTime;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.eventsPublished = Counter.builder("events.published")
            .tag("type", "domain")
            .register(meterRegistry);
        this.eventsConsumed = Counter.builder("events.consumed")
            .tag("type", "domain")
            .register(meterRegistry);
        this.eventProcessingTime = Timer.builder("events.processing.time")
            .register(meterRegistry);
    }

    public void recordEventPublished(String eventType) {
        eventsPublished.increment(Tags.of("event.type", eventType));
    }

    public void recordEventConsumed(String eventType) {
        eventsConsumed.increment(Tags.of("event.type", eventType));
    }

    public Timer.Sample startProcessingTimer() {
        return Timer.start(meterRegistry);
    }
}
```

### **Distributed Tracing**

```java
@Component
public class TracedEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final Tracer tracer;

    public void publishEvent(String topic, String key, DomainEvent event) {
        Span span = tracer.nextSpan()
            .name("publish-event")
            .tag("event.type", event.getEventType())
            .tag("topic", topic)
            .start();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // Добавление trace context в headers
            Message<String, DomainEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader("traceId", span.context().traceId())
                .setHeader("spanId", span.context().spanId())
                .build();

            kafkaTemplate.send(topic, key, message.getPayload());
            span.tag("event.id", event.getEventId());
        } catch (Exception e) {
            span.tag("error", true);
            span.tag("error.message", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## Лучшие практики

### 1. Идемпотентность

Все обработчики событий должны быть идемпотентными, чтобы безопасно обрабатывать дубликаты событий.

```java
@Component
public class IdempotentOrderHandler {

    private final Set<String> processedEventIds = new ConcurrentHashMap<>().newKeySet();

    @KafkaListener(topics = "order-events", groupId = "order-handlers")
    public void handleOrderEvent(OrderEvent event, Acknowledgment acknowledgment) {
        // Проверка идемпотентности
        if (processedEventIds.contains(event.getEventId())) {
            log.warn("Event already processed: {}", event.getEventId());
            acknowledgment.acknowledge();
            return;
        }

        try {
            processOrderEvent(event);
            processedEventIds.add(event.getEventId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event", e);
            // Не подтверждаем, чтобы обработать повторно
        }
    }
}
```

### 2. Версионирование событий

События должны иметь версии для поддержки эволюции схемы.

```java
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String eventType;
    private final int version;

    protected DomainEvent(String eventType, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}

public class UserCreatedEventV1 extends DomainEvent {
    private final String userId;
    private final String email;

    public UserCreatedEventV1(String userId, String email) {
        super("UserCreated", 1);
        this.userId = userId;
        this.email = email;
    }
}

public class UserCreatedEventV2 extends DomainEvent {
    private final String userId;
    private final String email;
    private final String name; // Новое поле

    public UserCreatedEventV2(String userId, String email, String name) {
        super("UserCreated", 2);
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
}
```

### 3. Разделение событий по типам

Используйте разные топики для разных типов событий для лучшей изоляции и масштабирования.

```java
// Разделение по доменам
order-events
payment-events
user-events
inventory-events

// Разделение по приоритету
order-events-high-priority
order-events-normal-priority
order-events-low-priority
```

### 4. Обработка порядка событий

Если порядок событий важен, используйте партиционирование по ключу.

```java
// Отправка событий с ключом для обеспечения порядка
kafkaTemplate.send("order-events", orderId, event);

// Consumer group обрабатывает события из одной партиции последовательно
@KafkaListener(topics = "order-events", groupId = "order-handlers")
public void handleOrderEvent(OrderEvent event) {
    // События для одного orderId будут обработаны последовательно
}
```

## **Anti-patterns**

### 1. Синхронное ожидание событий

Не используйте события для синхронного взаимодействия. События должны быть асинхронными.

```java
// Плохо: синхронное ожидание события
public Order createOrder(OrderRequest request) {
    Order order = orderService.createOrder(request);
    publishOrderCreated(order);

    // Плохо: ожидание ответного события
    waitForOrderProcessed(order.getId()); // Блокирующий вызов

    return order;
}

// Хорошо: асинхронная обработка
public Order createOrder(OrderRequest request) {
    Order order = orderService.createOrder(request);
    publishOrderCreated(order);
    return order; // Возвращаем сразу
}
```

### 2. Использование событий для запросов данных

События не должны использоваться для запросов данных. Используйте синхронные **API** или **CQRS**.

```java
// Плохо: запрос данных через события
public User getUser(String userId) {
    publishGetUserRequest(userId);
    return waitForUserResponse(userId); // Неправильно
}

// Хорошо: синхронный запрос или CQRS
public User getUser(String userId) {
    return userRepository.findById(userId); // Правильно
}
```

### 3. Игнорирование дубликатов

Всегда обрабатывайте возможность дублирования событий.

```java
// Плохо: нет проверки дубликатов
@KafkaListener(topics = "order-events")
public void handleOrderEvent(OrderEvent event) {
    orderService.processOrder(event.getOrderId()); // Может обработать дважды
}

// Хорошо: идемпотентная обработка
@KafkaListener(topics = "order-events")
public void handleOrderEvent(OrderEvent event) {
    if (alreadyProcessed(event.getEventId())) {
        return; // Пропускаем дубликат
    }
    orderService.processOrder(event.getOrderId());
    markAsProcessed(event.getEventId());
}
```

## Решение проблем

### Проблема: События не доставляются

**Причины:**
- Неправильная конфигурация **consumer group**
- Проблемы с сетью или **broker**
- Неправильная сериализация/десериализация

**Решение:**
```java
// Проверка конфигурации
@KafkaListener(topics = "order-events", groupId = "order-handlers")
public void handleOrderEvent(
        @Payload OrderEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.OFFSET) long offset) {

    log.info("Received event from topic: {}, partition: {}, offset: {}",
        topic, partition, offset);
    // Обработка события
}
```

### Проблема: Дублирование событий

**Причины:**
- **Retry** механизмы
- Проблемы с подтверждением **offset**
- Сетевые проблемы

**Решение:**
Реализуйте идемпотентную обработку и используйте уникальные идентификаторы событий.

### Проблема: Потеря событий

**Причины:**
- Подтверждение **offset** до обработки
- Сбои в обработке без **retry**
- Неправильная конфигурация **producer** (**acks=0**)

**Решение:**
```java
// Правильная конфигурация producer
configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждем подтверждения от всех реплик
configProps.put(ProducerConfig.RETRIES_CONFIG, 3);

// Правильная конфигурация consumer
configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Ручное подтверждение

// Подтверждение только после успешной обработки
@KafkaListener(topics = "order-events")
public void handleOrderEvent(OrderEvent event, Acknowledgment acknowledgment) {
    try {
        processOrderEvent(event);
        acknowledgment.acknowledge(); // Подтверждаем только после успешной обработки
    } catch (Exception e) {
        // Не подтверждаем при ошибке
    }
}
```

### Проблема: Медленная обработка событий

**Причины:**
- Недостаточная параллельность
- Блокирующие операции в обработчике
- Неоптимальная конфигурация **consumer**

**Решение:**
```java
// Увеличение параллельности
@Bean
public ConcurrentKafkaListenerContainerFactory<String, DomainEvent> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(10); // Увеличиваем количество потоков
    return factory;
}

// Асинхронная обработка
@KafkaListener(topics = "order-events")
public void handleOrderEvent(OrderEvent event) {
    CompletableFuture.runAsync(() -> {
        processOrderEvent(event);
    });
}
```

## Частые вопросы

**Когда выбирать Event-Driven вместо синхронного API?** Когда нужна слабая связанность, масштабирование по потребителям, буферизация пиковых нагрузок или асинхронная обработка. Не заменяйте простой запрос-ответ очередью «для гибкости» без явной причины.

**Kafka или RabbitMQ для событий?** Kafka — высокая пропускная способность, персистентность, переигрывание; подходит для логов событий и стриминга. RabbitMQ — гибкая маршрутизация, очереди задач, меньше требований к инфраструктуре. Выбор зависит от нагрузки и модели доставки.

**Как гарантировать порядок и отсутствие потерь?** Порядок: партиционирование по ключу в Kafka; один партиция — один consumer в группе. От потерь: acks=all у producer, коммит offset после обработки у consumer, идемпотентность обработки и DLQ для сбойных сообщений.
