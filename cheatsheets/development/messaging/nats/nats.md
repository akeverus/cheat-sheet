---
title: "NATS"
description: "NATS - это высокопроизводительная, облачно-нативная система обмена сообщениями с открытым исходным кодом. Этот документ охватывает архитектуру, паттерны коммуникации, интеграцию с Java/Spring и best practices для современных приложений."
tags:
  - development
  - messaging
  - nats
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# NATS

**NATS** — это высокопроизводительная, облачно-нативная система обмена сообщениями с открытым исходным кодом. Этот документ охватывает архитектуру, паттерны коммуникации, интеграцию с **Java**/**Spring** и **best practices** для современных приложений.

## Полезные ссылки
- [NATS Documentation](https://docs.nats.io/)
- [NATS JetStream](https://docs.nats.io/nats-concepts/jetstream)
- [NATS Java Client (GitHub)](https://github.com/nats-io/nats.java)
- [NATS by Example](https://docs.nats.io/developing-with-nats/by_example)

## Содержание

- [Основы NATS](#основы-nats)
  - [Архитектура](#архитектура)
- [docker-compose.yml для NATS кластера](#docker-composeyml-для-nats-кластера)
  - [Java клиент](#java-клиент)
- [Core Messaging Patterns](#core-messaging-patterns)
  - [Publish-Subscribe (Pub-Sub)](#publish-subscribe-pub-sub)
  - [Request-Reply](#request-reply)
- [JetStream (Persistent Messaging)](#jetstream-persistent-messaging)
  - [Настройка JetStream](#настройка-jetstream)
  - [Работа с JetStream](#работа-с-jetstream)
- [Key-Value Store](#key-value-store)
  - [KV операции](#kv-операции)
- [Object Store](#object-store)
  - [Работа с объектами](#работа-с-объектами)
- [Service Mesh Integration](#service-mesh-integration)
  - [Service discovery через NATS](#service-discovery-через-nats)
- [Monitoring и Observability](#monitoring-и-observability)
  - [Metrics collection](#metrics-collection)
  - [Distributed tracing](#distributed-tracing)
- [Security](#security)
  - [Authentication и Authorization](#authentication-и-authorization)
- [Performance Tuning](#performance-tuning)
  - [Оптимизация подключения](#оптимизация-подключения)
  - [Batch operations](#batch-operations)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
- [Проверка статуса кластера](#проверка-статуса-кластера)
- [Мониторинг подключений](#мониторинг-подключений)
- [Просмотр подписок](#просмотр-подписок)
- [Проверка JetStream](#проверка-jetstream)
- [Тестирование подключения](#тестирование-подключения)
- [Проверка latency](#проверка-latency)
- [Debug logging](#debug-logging)
  - [Health checks](#health-checks)
- [Лучшие практики](#лучшие-практики)
  - [Production configuration](#production-configuration)
- [См. также](#см-также)

## Основы NATS

### Архитектура
Ниже — **docker-compose** для **NATS**-кластера (YAML).
```yaml
# docker-compose.yml для NATS кластера
version: '3.8'
services:
  nats1:
    image: nats:2.9
    hostname: nats1
    command: ["--cluster", "nats://0.0.0.0:6222",
              "--routes", "nats://nats2:6222,nats://nats3:6222",
              "--http_port", "8222"]
    ports:
      - "4222:4222"
      - "8222:8222"
      - "6222:6222"

  nats2:
    image: nats:2.9
    hostname: nats2
    command: ["--cluster", "nats://0.0.0.0:6222",
              "--routes", "nats://nats1:6222,nats://nats3:6222",
              "--http_port", "8223"]
    ports:
      - "4223:4222"
      - "8223:8222"
      - "6223:6222"

  nats3:
    image: nats:2.9
    hostname: nats3
    command: ["--cluster", "nats://0.0.0.0:6222",
              "--routes", "nats://nats1:6222,nats://nats2:6222",
              "--http_port", "8224"]
    ports:
      - "4224:4222"
      - "8224:8222"
      - "6224:6222"

  nats-box:
    image: natsio/nats-box:0.13.0
    depends_on:
      - nats1
      - nats2
      - nats3
```

### Java клиент
```xml
<!-- Зависимости NATS Java-клиента -->
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats</artifactId>
    <version>2.16.0</version>
</dependency>

<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats-spring</artifactId>
    <version>2.16.0</version>
</dependency>
```

```java
// Подключение к NATS и бин Connection для приложения
@Configuration
public class NATSConfig {

    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server("nats://localhost:4222")
                .connectionName("order-service")
                .maxReconnects(-1)  // Infinite reconnects
                .reconnectWait(Duration.ofSeconds(1))
                .connectionTimeout(Duration.ofSeconds(5))
                .pingInterval(Duration.ofSeconds(20))
                .maxPingsOut(3)
                .build();

        return Nats.connect(options);
    }

    @Bean
    public NatsTemplate natsTemplate(Connection natsConnection) {
        return new NatsTemplate(natsConnection);
    }
}
```

## Core Messaging Patterns

### Publish-Subscribe (Pub-Sub)
```java
// Публикация событий заказа в subject (Publish-Subscribe)
@Service
public class OrderEventPublisher {

    @Autowired
    private Connection natsConnection;

    public void publishOrderCreated(Order order) {
        try {
            // Публикация события создания заказа
            String subject = "orders.created";
            String message = convertToJson(order);

            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));

            logger.info("Published order created event: {}", order.getId());

        } catch (Exception e) {
            logger.error("Failed to publish order event", e);
        }
    }

    public void publishOrderStatusChanged(String orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            String subject = "orders.status.changed";
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(orderId, oldStatus, newStatus);
            String message = convertToJson(event);

            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            logger.error("Failed to publish status change event", e);
        }
    }
}
```

```java
// Подписка на subject и обработка сообщений заказов
@Component
public class OrderEventSubscriber {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationService notificationService;

    @PostConstruct
    public void subscribeToEvents() {
        try {
            // Подписка на события создания заказов
            natsConnection.createDispatcher()
                .subscribe("orders.created", message -> {
                    try {
                        String json = new String(message.getData(), StandardCharsets.UTF_8);
                        Order order = parseFromJson(json, Order.class);

                        logger.info("Received order created event: {}", order.getId());

                        // Отправка уведомления
                        notificationService.sendOrderConfirmation(order);

                    } catch (Exception e) {
                        logger.error("Failed to process order created event", e);
                    }
                });

            // Подписка на события изменения статуса
            natsConnection.createDispatcher()
                .subscribe("orders.status.changed", message -> {
                    try {
                        String json = new String(message.getData(), StandardCharsets.UTF_8);
                        OrderStatusChangedEvent event = parseFromJson(json, OrderStatusChangedEvent.class);

                        logger.info("Received status change event for order: {}", event.getOrderId());

                        // Отправка уведомления о изменении статуса
                        notificationService.sendStatusUpdate(event);

                    } catch (Exception e) {
                        logger.error("Failed to process status change event", e);
                    }
                });

        } catch (Exception e) {
            logger.error("Failed to subscribe to NATS events", e);
        }
    }
}
```

### Request-Reply
```java
// Сервис-обработчик: подписка на запросы и ответ через reply subject
@Service
public class InventoryService {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostConstruct
    public void setupRequestHandlers() {
        try {
            // Обработчик запросов на проверку наличия товара
            natsConnection.createDispatcher()
                .subscribe("inventory.check", message -> {
                    try {
                        String productId = new String(message.getData(), StandardCharsets.UTF_8);
                        InventoryItem item = inventoryRepository.findByProductId(productId);

                        InventoryResponse response = new InventoryResponse(
                            productId,
                            item != null ? item.getQuantity() : 0,
                            item != null
                        );

                        String responseJson = convertToJson(response);
                        message.respond(responseJson.getBytes(StandardCharsets.UTF_8));

                    } catch (Exception e) {
                        logger.error("Failed to process inventory check request", e);

                        // Отправка ответа об ошибке
                        ErrorResponse error = new ErrorResponse("INVENTORY_CHECK_FAILED", e.getMessage());
                        String errorJson = convertToJson(error);
                        message.respond(errorJson.getBytes(StandardCharsets.UTF_8));
                    }
                });

            // Обработчик запросов на резервирование товара
            natsConnection.createDispatcher()
                .subscribe("inventory.reserve", message -> {
                    try {
                        String json = new String(message.getData(), StandardCharsets.UTF_8);
                        InventoryReservationRequest request = parseFromJson(json, InventoryReservationRequest.class);

                        boolean success = inventoryRepository.reserveItems(
                            request.getProductId(),
                            request.getQuantity(),
                            request.getOrderId()
                        );

                        ReservationResponse response = new ReservationResponse(
                            request.getOrderId(),
                            request.getProductId(),
                            success
                        );

                        String responseJson = convertToJson(response);
                        message.respond(responseJson.getBytes(StandardCharsets.UTF_8));

                    } catch (Exception e) {
                        logger.error("Failed to process inventory reservation request", e);

                        ErrorResponse error = new ErrorResponse("RESERVATION_FAILED", e.getMessage());
                        String errorJson = convertToJson(error);
                        message.respond(errorJson.getBytes(StandardCharsets.UTF_8));
                    }
                });

        } catch (Exception e) {
            logger.error("Failed to setup inventory request handlers", e);
        }
    }
}
```

```java
// Клиент request-reply: публикация запроса и ожидание ответа
@Service
public class OrderService {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(OrderRequest request) throws Exception {
        // Проверка наличия товаров через NATS
        for (OrderItem item : request.getItems()) {
            InventoryResponse inventory = checkInventory(item.getProductId());

            if (!inventory.isAvailable() || inventory.getQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                    "Insufficient inventory for product: " + item.getProductId());
            }
        }

        // Создание заказа
        Order order = new Order(request);
        orderRepository.save(order);

        // Резервирование товаров
        for (OrderItem item : request.getItems()) {
            boolean reserved = reserveInventory(order.getId(), item.getProductId(), item.getQuantity());
            if (!reserved) {
                // Компенсация - отмена заказа
                orderRepository.delete(order);
                throw new ReservationFailedException("Failed to reserve inventory");
            }
        }

        return order;
    }

    private InventoryResponse checkInventory(String productId) throws Exception {
        byte[] request = productId.getBytes(StandardCharsets.UTF_8);
        Future<Message> future = natsConnection.request("inventory.check", request);

        Message response = future.get(5, TimeUnit.SECONDS);
        String json = new String(response.getData(), StandardCharsets.UTF_8);

        return parseFromJson(json, InventoryResponse.class);
    }

    private boolean reserveInventory(String orderId, String productId, int quantity) throws Exception {
        InventoryReservationRequest request = new InventoryReservationRequest(orderId, productId, quantity);
        String json = convertToJson(request);
        byte[] requestBytes = json.getBytes(StandardCharsets.UTF_8);

        Future<Message> future = natsConnection.request("inventory.reserve", requestBytes);

        Message response = future.get(5, TimeUnit.SECONDS);
        String responseJson = new String(response.getData(), StandardCharsets.UTF_8);

        ReservationResponse reservationResponse = parseFromJson(responseJson, ReservationResponse.class);
        return reservationResponse.isSuccess();
    }
}
```

## JetStream (Persistent Messaging)

### Настройка JetStream
```java
// Конфигурация JetStream: Stream и контекст
@Configuration
public class JetStreamConfig {

    @Bean
    public JetStream jetStream(Connection natsConnection) throws IOException {
        return natsConnection.jetStream();
    }

    @Bean
    public JetStreamManagement jetStreamManagement(Connection natsConnection) throws IOException {
        return natsConnection.jetStreamManagement();
    }
}
```

```java
// Создание Stream и консьюмеров при старте приложения
@Component
public class JetStreamInitializer {

    @Autowired
    private JetStreamManagement jsm;

    @PostConstruct
    public void initializeStreams() {
        try {
            // Создание потока для заказов
            StreamConfiguration orderStreamConfig = StreamConfiguration.builder()
                .name("ORDERS")
                .subjects("orders.>")
                .storageType(StorageType.File)
                .replicas(3)
                .maxAge(Duration.ofDays(7))
                .build();

            jsm.addStream(orderStreamConfig);

            // Создание потока для инвентаря
            StreamConfiguration inventoryStreamConfig = StreamConfiguration.builder()
                .name("INVENTORY")
                .subjects("inventory.>")
                .storageType(StorageType.File)
                .replicas(3)
                .maxAge(Duration.ofDays(30))
                .build();

            jsm.addStream(inventoryStreamConfig);

            // Создание consumer для обработки заказов
            ConsumerConfiguration orderConsumerConfig = ConsumerConfiguration.builder()
                .durable("order-processor")
                .deliverSubject("order-processor")
                .deliverGroup("order-processors")
                .ackPolicy(AckPolicy.Explicit)
                .ackWait(Duration.ofSeconds(30))
                .maxDeliver(3)  // Максимум 3 попытки доставки
                .filterSubject("orders.created")
                .build();

            jsm.addOrUpdateConsumer("ORDERS", orderConsumerConfig);

        } catch (Exception e) {
            logger.error("Failed to initialize JetStream", e);
        }
    }
}
```

### Работа с JetStream
```java
// Публикация в JetStream с подтверждением
@Service
public class JetStreamOrderPublisher {

    @Autowired
    private JetStream jetStream;

    public void publishOrder(Order order) {
        try {
            String subject = "orders.created";
            String message = convertToJson(order);
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            // Публикация с подтверждением
            PublishAck ack = jetStream.publish(subject, data);

            logger.info("Published order to JetStream: {} (stream: {}, seq: {})",
                       order.getId(), ack.getStream(), ack.getSeq());

        } catch (Exception e) {
            logger.error("Failed to publish order to JetStream", e);
            throw new RuntimeException("Failed to publish order", e);
        }
    }
}
```

```java
// Push-консьюмер JetStream: обработка сообщений из Stream
@Component
public class JetStreamOrderConsumer {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private OrderService orderService;

    @PostConstruct
    public void startConsumer() {
        try {
            PushSubscribeOptions options = PushSubscribeOptions.builder()
                .durable("order-processor")
                .build();

            // Создание push-based подписки
            JetStreamSubscription subscription = natsConnection.jetStream()
                .subscribe("orders.created", "order-processors", options);

            // Обработка сообщений в отдельном потоке
            executorService.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Message message = subscription.nextMessage(Duration.ofSeconds(1));

                        if (message != null) {
                            processMessage(message);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Error processing message", e);
                    }
                }
            });

        } catch (Exception e) {
            logger.error("Failed to start JetStream consumer", e);
        }
    }

    private void processMessage(Message message) {
        try {
            String json = new String(message.getData(), StandardCharsets.UTF_8);
            Order order = parseFromJson(json, Order.class);

            logger.info("Processing order from JetStream: {}", order.getId());

            // Обработка заказа
            orderService.processOrder(order);

            // Подтверждение обработки
            message.ack();

            logger.info("Order processed successfully: {}", order.getId());

        } catch (Exception e) {
            logger.error("Failed to process order: {}", new String(message.getData()), e);

            // Отклонение сообщения (будет повторена доставка)
            message.nak();
        }
    }
}
```

## Key-Value Store

### `KV` операции
```java
// Конфигурация Key-Value бакета в JetStream
@Configuration
public class KeyValueConfig {

    @Bean
    public KeyValue keyValueStore(JetStream jetStream) throws IOException, JetStreamApiException {
        // Создание или получение KV bucket
        KeyValueConfiguration kvConfig = KeyValueConfiguration.builder()
            .name("user-preferences")
            .maxHistoryPerKey(10)
            .storageType(StorageType.File)
            .replicas(3)
            .build();

        return jetStream.keyValueManagement().create(kvConfig);
    }
}
```

```java
// Сервис настроек пользователя через Key-Value API
@Service
public class UserPreferencesService {

    @Autowired
    private KeyValue keyValueStore;

    public void saveUserPreference(String userId, String key, String value) {
        try {
            String kvKey = userId + ":" + key;
            keyValueStore.put(kvKey, value.getBytes(StandardCharsets.UTF_8));

            logger.info("Saved user preference: {} = {}", kvKey, value);

        } catch (Exception e) {
            logger.error("Failed to save user preference", e);
        }
    }

    public String getUserPreference(String userId, String key) {
        try {
            String kvKey = userId + ":" + key;
            KeyValueEntry entry = keyValueStore.get(kvKey);

            if (entry != null) {
                return new String(entry.getValue(), StandardCharsets.UTF_8);
            }

            return null;

        } catch (Exception e) {
            logger.error("Failed to get user preference", e);
            return null;
        }
    }

    public void deleteUserPreference(String userId, String key) {
        try {
            String kvKey = userId + ":" + key;
            keyValueStore.delete(kvKey);

            logger.info("Deleted user preference: {}", kvKey);

        } catch (Exception e) {
            logger.error("Failed to delete user preference", e);
        }
    }

    public List<String> getUserPreferenceHistory(String userId, String key) {
        try {
            String kvKey = userId + ":" + key;
            Iterator<KeyValueEntry> history = keyValueStore.history(kvKey);

            List<String> values = new ArrayList<>();
            while (history.hasNext()) {
                KeyValueEntry entry = history.next();
                values.add(new String(entry.getValue(), StandardCharsets.UTF_8));
            }

            return values;

        } catch (Exception e) {
            logger.error("Failed to get preference history", e);
            return Collections.emptyList();
        }
    }
}
```

## Object Store

### Работа с объектами
```java
@Configuration
public class ObjectStoreConfig {

    @Bean
    public ObjectStore objectStore(JetStream jetStream) throws IOException, JetStreamApiException {
        // Создание object store для документов
        ObjectStoreConfiguration osConfig = ObjectStoreConfiguration.builder()
            .name("documents")
            .storageType(StorageType.File)
            .replicas(3)
            .build();

        return jetStream.objectStoreManagement().create(osConfig);
    }
}
```

```java
// Загрузка и скачивание документов через Object Store
@Service
public class DocumentService {

    @Autowired
    private ObjectStore objectStore;

    public void storeDocument(String documentId, byte[] content, String contentType) {
        try {
            ObjectMetaData meta = ObjectMetaData.builder()
                .name(documentId)
                .description("User uploaded document")
                .addHeader("content-type", contentType)
                .addHeader("upload-timestamp", String.valueOf(System.currentTimeMillis()))
                .build();

            objectStore.put(meta, content);

            logger.info("Stored document: {} (size: {} bytes)", documentId, content.length);

        } catch (Exception e) {
            logger.error("Failed to store document", e);
        }
    }

    public byte[] retrieveDocument(String documentId) {
        try {
            ObjectInfo info = objectStore.getInfo(documentId);

            if (info != null) {
                return objectStore.getBytes(documentId);
            }

            return null;

        } catch (Exception e) {
            logger.error("Failed to retrieve document", e);
            return null;
        }
    }

    public void deleteDocument(String documentId) {
        try {
            objectStore.delete(documentId);
            logger.info("Deleted document: {}", documentId);

        } catch (Exception e) {
            logger.error("Failed to delete document", e);
        }
    }

    public List<String> listDocuments() {
        try {
            Iterator<ObjectInfo> objects = objectStore.list();

            List<String> documentNames = new ArrayList<>();
            while (objects.hasNext()) {
                ObjectInfo info = objects.next();
                documentNames.add(info.getName());
            }

            return documentNames;

        } catch (Exception e) {
            logger.error("Failed to list documents", e);
            return Collections.emptyList();
        }
    }
}
```

## Service Mesh Integration

### Service discovery через NATS
```java
// Регистрация сервиса и обработка входящих запросов
@Service
public class ServiceRegistry {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private KeyValue serviceStore;

    public void registerService(String serviceName, String instanceId, ServiceInfo info) {
        try {
            String key = "services:" + serviceName + ":" + instanceId;
            String value = convertToJson(info);

            serviceStore.put(key, value.getBytes(StandardCharsets.UTF_8));

            // Отправка события о регистрации
            natsConnection.publish("services.registered",
                (serviceName + ":" + instanceId).getBytes(StandardCharsets.UTF_8));

            logger.info("Registered service: {}:{}", serviceName, instanceId);

        } catch (Exception e) {
            logger.error("Failed to register service", e);
        }
    }

    public void unregisterService(String serviceName, String instanceId) {
        try {
            String key = "services:" + serviceName + ":" + instanceId;
            serviceStore.delete(key);

            // Отправка события об отключении
            natsConnection.publish("services.unregistered",
                (serviceName + ":" + instanceId).getBytes(StandardCharsets.UTF_8));

            logger.info("Unregistered service: {}:{}", serviceName, instanceId);

        } catch (Exception e) {
            logger.error("Failed to unregister service", e);
        }
    }

    public List<ServiceInfo> discoverServices(String serviceName) {
        try {
            List<ServiceInfo> services = new ArrayList<>();
            String prefix = "services:" + serviceName + ":";

            Iterator<KeyValueEntry> entries = serviceStore.keys(prefix);

            while (entries.hasNext()) {
                KeyValueEntry entry = entries.next();
                String json = new String(entry.getValue(), StandardCharsets.UTF_8);
                ServiceInfo info = parseFromJson(json, ServiceInfo.class);
                services.add(info);
            }

            return services;

        } catch (Exception e) {
            logger.error("Failed to discover services", e);
            return Collections.emptyList();
        }
    }
}
```

```java
// Клиент: обнаружение сервиса и отправка запроса
@Component
public class ServiceClient {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private Connection natsConnection;

    public <T> T callService(String serviceName, String method, Object request, Class<T> responseType) {
        try {
            // Поиск доступных экземпляров сервиса
            List<ServiceInfo> instances = serviceRegistry.discoverServices(serviceName);

            if (instances.isEmpty()) {
                throw new ServiceUnavailableException("No instances available for service: " + serviceName);
            }

            // Round-robin выбор экземпляра
            ServiceInfo targetInstance = instances.get(currentIndex.getAndIncrement() % instances.size());

            // Формирование subject для вызова
            String subject = "services." + serviceName + "." + targetInstance.getInstanceId() + "." + method;

            // Отправка запроса
            String requestJson = convertToJson(request);
            Future<Message> future = natsConnection.request(subject,
                requestJson.getBytes(StandardCharsets.UTF_8));

            // Ожидание ответа с таймаутом
            Message response = future.get(10, TimeUnit.SECONDS);
            String responseJson = new String(response.getData(), StandardCharsets.UTF_8);

            return parseFromJson(responseJson, responseType);

        } catch (Exception e) {
            logger.error("Failed to call service: {}", serviceName, e);
            throw new RuntimeException("Service call failed", e);
        }
    }
}
```

## Monitoring и Observability

### Metrics collection
```java
// Сбор метрик подключений и подписок
@Component
public class NATSMonitor {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private Connection natsConnection;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectMetrics() {
        try {
            // Server info
            ServerInfo serverInfo = natsConnection.getServerInfo();

            meterRegistry.gauge("nats_connections_total",
                serverInfo.getConnectionCount());

            meterRegistry.gauge("nats_subscriptions_total",
                serverInfo.getSubscriptionCount());

            // JetStream metrics (if available)
            if (serverInfo.isJetStreamAvailable()) {
                // Collect JetStream specific metrics
                collectJetStreamMetrics();
            }

        } catch (Exception e) {
            logger.error("Failed to collect NATS metrics", e);
        }
    }

    private void collectJetStreamMetrics() {
        try {
            // Account info
            AccountInfo accountInfo = natsConnection.jetStreamManagement().getAccountInfo();

            meterRegistry.gauge("nats_jetstream_streams_total",
                accountInfo.getStreams());

            meterRegistry.gauge("nats_jetstream_consumers_total",
                accountInfo.getConsumers());

            meterRegistry.gauge("nats_jetstream_messages_total",
                accountInfo.getMessages());

            meterRegistry.gauge("nats_jetstream_bytes_total",
                accountInfo.getBytes());

        } catch (Exception e) {
            logger.debug("Failed to collect JetStream metrics", e);
        }
    }
}
```

### Distributed tracing
```java
// Интеграция трейсинга с NATS (контекст и интерцепторы)
@Configuration
public class TracingConfig {

    @Bean
    public NatsMessageTracing natsTracing(Tracer tracer) {
        return new NatsMessageTracing(tracer);
    }
}

public class NatsMessageTracing {

    private final Tracer tracer;

    public NatsMessageTracing(Tracer tracer) {
        this.tracer = tracer;
    }

    public void traceMessage(String operationName, Message message, Runnable action) {
        Span span = tracer.buildSpan(operationName)
            .withTag("nats.subject", message.getSubject())
            .withTag("nats.replyTo", message.getReplyTo())
            .start();

        try (Scope scope = tracer.scopeManager().activate(span)) {
            // Add NATS headers to span
            span.setTag("nats.message.size", message.getData().length);

            if (message.getHeaders() != null) {
                message.getHeaders().forEach((key, values) ->
                    span.setTag("nats.header." + key, String.join(",", values)));
            }

            action.run();

        } catch (Exception e) {
            span.setTag("error", true);
            span.log(Map.of("error.message", e.getMessage()));
            throw e;
        } finally {
            span.finish();
        }
    }
}
```

## Security

### Authentication и Authorization
```java
// Настройка TLS и JWT-аутентификации для NATS
@Configuration
public class NATSSecurityConfig {

    @Bean
    public Connection secureNatsConnection() throws IOException, InterruptedException {
        // NATS with JWT authentication
        char[] jwt = loadJwtFromFile("service.jwt");
        char[] nkey = loadNkeyFromFile("service.nkey");

        Credentials credentials = new Credentials(jwt, nkey);

        Options options = new Options.Builder()
            .server("nats://localhost:4222")
            .authHandler(new AuthHandler() {
                @Override
                public char[] getID() {
                    return "service".toCharArray();
                }

                @Override
                public char[] getJWT() {
                    return jwt;
                }

                @Override
                public byte[] sign(byte[] nonce) {
                    try {
                        return NKey.sign(nonce, nkey);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to sign nonce", e);
                    }
                }

                @Override
                public AuthHandler getAuthHandlerForServer(String serverId) {
                    return this;
                }
            })
            .tlsRequired(true)
            .sslContext(createSSLContext())
            .build();

        return Nats.connect(options);
    }

    private SSLContext createSSLContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream("client.p12")) {
            keyStore.load(fis, "password".toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "password".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }
}
```

## Performance Tuning

### Оптимизация подключения
```java
@Configuration
public class OptimizedNATSConfig {

    @Bean
    public Connection optimizedConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
            .servers(new String[]{"nats://server1:4222", "nats://server2:4222", "nats://server3:4222"})
            .connectionName("high-performance-client")
            .maxReconnects(-1)
            .reconnectWait(Duration.ofMillis(100))
            .connectionTimeout(Duration.ofSeconds(2))
            .pingInterval(Duration.ofSeconds(10))
            .maxPingsOut(2)
            .sendBufferSize(2 * 1024 * 1024)  // 2MB
            .receiveBufferSize(2 * 1024 * 1024) // 2MB
            .useOldRequestStyle(false) // Use new request style for better performance
            .build();

        Connection connection = Nats.connect(options);

        // Настройка обработчика ошибок
        connection.setErrorListener(new ErrorListener() {
            @Override
            public void errorOccurred(Connection conn, String error) {
                logger.error("NATS connection error: {}", error);
            }

            @Override
            public void exceptionOccurred(Connection conn, Exception exp) {
                logger.error("NATS connection exception", exp);
            }
        });

        return connection;
    }
}
```

### Batch operations
```java
@Service
public class BatchMessageProcessor {

    @Autowired
    private Connection natsConnection;

    private final ExecutorService batchExecutor = Executors.newFixedThreadPool(10);
    private final Map<String, List<Message>> messageBatches = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> batchTimers = new ConcurrentHashMap<>();

    public void addToBatch(String batchKey, Message message) {
        synchronized (messageBatches) {
            messageBatches.computeIfAbsent(batchKey, k -> new ArrayList<>()).add(message);

            List<Message> batch = messageBatches.get(batchKey);
            if (batch.size() >= 100) { // Max batch size
                processBatch(batchKey, batch);
            } else if (batch.size() == 1) { // First message in batch
                // Schedule batch processing after 100ms
                ScheduledFuture<?> timer = batchExecutor.schedule(() -> {
                    List<Message> currentBatch = messageBatches.remove(batchKey);
                    if (currentBatch != null) {
                        processBatch(batchKey, currentBatch);
                    }
                }, 100, TimeUnit.MILLISECONDS);

                batchTimers.put(batchKey, timer);
            }
        }
    }

    private void processBatch(String batchKey, List<Message> batch) {
        try {
            logger.info("Processing batch {} with {} messages", batchKey, batch.size());

            // Process messages in batch
            for (Message message : batch) {
                processMessage(message);
            }

            logger.info("Successfully processed batch {}", batchKey);

        } catch (Exception e) {
            logger.error("Failed to process batch {}", batchKey, e);

            // Send messages to error queue
            for (Message message : batch) {
                natsConnection.publish("errors." + batchKey, message.getData());
            }
        } finally {
            batchTimers.remove(batchKey);
        }
    }

    private void processMessage(Message message) {
        // Business logic for processing individual message
        String data = new String(message.getData(), StandardCharsets.UTF_8);
        logger.debug("Processing message: {}", data);

        // Acknowledge message
        message.ack();
    }
}
```

## Решение проблем

### Распространенные проблемы
```bash
# Проверка статуса кластера
nats server check --server nats://localhost:4222

# Мониторинг подключений
nats server report connections

# Просмотр подписок
nats server report subscriptions

# Проверка JetStream
nats stream ls
nats stream info ORDERS
nats consumer ls ORDERS
nats consumer info ORDERS order-processor

# Тестирование подключения
nats pub test.subject "Hello NATS"
nats sub test.subject

# Проверка latency
nats bench --pub 1 --sub 1 --size 128 test.latency

# Debug logging
nats --trace pub test.debug "Debug message"
```

### Health checks
```java
@Component
public class NATSHealthIndicator implements HealthIndicator {

    @Autowired
    private Connection natsConnection;

    @Override
    public Health health() {
        try {
            // Test basic connectivity
            natsConnection.flush(Duration.ofSeconds(5));

            // Get server info
            ServerInfo serverInfo = natsConnection.getServerInfo();

            // Check JetStream if available
            boolean jetStreamAvailable = serverInfo.isJetStreamAvailable();
            if (jetStreamAvailable) {
                // Additional JetStream health check
                natsConnection.jetStreamManagement().getAccountInfo();
            }

            return Health.up()
                .withDetail("server", serverInfo.getServerId())
                .withDetail("version", serverInfo.getVersion())
                .withDetail("connections", serverInfo.getConnectionCount())
                .withDetail("jetStream", jetStreamAvailable)
                .build();

        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
```

## Лучшие практики

### Production configuration
```java
@Configuration
public class ProductionNATSConfig {

    @Bean
    public Connection productionConnection(
            @Value("${nats.servers}") String[] servers,
            @Value("${nats.credentials.jwt}") String jwt,
            @Value("${nats.credentials.nkey}") String nkey) throws Exception {

        Options options = new Options.Builder()
            .servers(servers)
            .connectionName("production-service")
            .authHandler(new AuthHandler() {
                @Override
                public char[] getID() {
                    return "production-service".toCharArray();
                }

                @Override
                public char[] getJWT() {
                    return jwt.toCharArray();
                }

                @Override
                public byte[] sign(byte[] nonce) {
                    try {
                        return NKey.fromSeed(nkey.toCharArray()).sign(nonce);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to sign nonce", e);
                    }
                }

                @Override
                public AuthHandler getAuthHandlerForServer(String serverId) {
                    return this;
                }
            })
            .tlsRequired(true)
            .sslContext(createProductionSSLContext())
            .maxReconnects(-1)
            .reconnectWait(Duration.ofSeconds(1))
            .connectionTimeout(Duration.ofSeconds(10))
            .pingInterval(Duration.ofSeconds(30))
            .maxPingsOut(3)
            .errorListener(new ErrorListener() {
                @Override
                public void errorOccurred(Connection conn, String error) {
                    logger.error("NATS connection error: {}", error);
                    // Send alert to monitoring system
                    monitoringService.alert("NATS Connection Error", error);
                }

                @Override
                public void exceptionOccurred(Connection conn, Exception exp) {
                    logger.error("NATS connection exception", exp);
                    // Send alert to monitoring system
                    monitoringService.alert("NATS Connection Exception", exp.getMessage());
                }
            })
            .build();

        return Nats.connect(options);
    }

    private SSLContext createProductionSSLContext() throws Exception {
        // Production-grade SSL context configuration
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream("/secrets/client.p12")) {
            keyStore.load(fis, System.getenv("KEYSTORE_PASSWORD").toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, System.getenv("KEY_PASSWORD").toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }
}
```
## См. также
- [[rabbitmq|RabbitMQ]] — надёжная система сообщений
- [[kafka|Kafka]] — потоковая обработка данных
- [[spring-cloud|Spring Cloud]] — облачные стримы
