---
title: "Вопросы на собеседовании: Spring Integration"
description: "Spring Integration — реализация Enterprise Integration Patterns: MessageChannel, Service Activator, Gateway, Java DSL, Splitter/Aggregator, обработка ошибок"
tags:
  - interview
  - spring
  - spring-integration-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Integration"
  - "Spring Integration interview"
  - "Spring Integration вопросы"
prerequisites:
  - "[[spring-integration]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Integration`

`Spring Integration` — реализация Enterprise Integration Patterns (EIP) поверх Spring. Предоставляет абстракции Channel, Endpoint, Gateway для построения интеграционных конвейеров между системами (JMS, Kafka, FTP, HTTP) с минимальной связностью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Integration Docs](https://docs.spring.io/spring-integration/reference/html/) — официальная документация
- [EIP Book](https://www.enterpriseintegrationpatterns.com/) — Enterprise Integration Patterns (Hohpe & Woolf)
- [Baeldung: Spring Integration](https://www.baeldung.com/spring-integration) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и каналы**
- [Q1. (!) Что такое Spring Integration и какие EIP-паттерны он реализует?](#q1-что-такое-spring-integration-и-какие-eip-паттерны-он-реализует)
- [Q2. (!) Какие типы MessageChannel существуют?](#q2-какие-типы-messagechannel-существуют)

**Endpoints**
- [Q3. Что такое Service Activator?](#q3-что-такое-service-activator)
- [Q4. Как работает MessagingGateway?](#q4-как-работает-messaginggateway)

**Java DSL и трансформации**
- [Q5. (!) Как устроен Java DSL?](#q5-как-устроен-java-dsl)
- [Q6. Как реализовать Splitter и Aggregator?](#q6-как-реализовать-splitter-и-aggregator)

**Обработка ошибок и интеграции**
- [Q7. Как обрабатывать ошибки?](#q7-как-обрабатывать-ошибки)
- [Q8. Как подключить Kafka через Spring Integration?](#q8-как-подключить-kafka-через-spring-integration)
- [Q9. Как транзакции работают в Spring Integration?](#q9-как-транзакции-работают-в-spring-integration)
- [Q10. Как тестировать Spring Integration потоки?](#q10-как-тестировать-spring-integration-потоки)

## Q1. Что такое Spring Integration и какие EIP-паттерны он реализует?

**Spring Integration** — реализация Enterprise Integration Patterns (EIP) из книги Hohpe & Woolf поверх Spring. Позволяет строить интеграционные конвейеры без жёсткой связи между компонентами.

Ключевые EIP-паттерны в Spring Integration:
- **Message Channel** — транспорт между компонентами.
- **Message Router** — маршрутизация по содержимому (Content-Based Router).
- **Message Transformer** — преобразование формата сообщения.
- **Message Filter** — отбрасывание нежелательных сообщений.
- **Splitter** — разбивка одного сообщения на N.
- **Aggregator** — сборка N сообщений в одно.
- **Service Activator** — вызов бизнес-компонента.
- **Gateway** — двусторонний мост request/reply.

```java
@Configuration
@EnableIntegration
public class IntegrationConfig { ... }
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Какие типы MessageChannel существуют? Частая ошибка в реальном коде.

| Канал | Поведение | Применение |
|-------|-----------|------------|
| `DirectChannel` | Синхронный, в потоке отправителя | Default; поддерживает транзакции |
| `PublishSubscribeChannel` | Broadcast всем подписчикам | Уведомления, events |
| `QueueChannel` | In-memory FIFO (требует poller) | Буферизация, развязка потоков |
| `PriorityChannel` | Очередь с приоритетом | Приоритизация сообщений |
| `ExecutorChannel` | Асинхронный (TaskExecutor) | Параллельная обработка |
| `FluxMessageChannel` | Реактивный (Reactor) | Bridge к Spring WebFlux |

```java
@Bean public MessageChannel ordersInput() { return new DirectChannel(); }
@Bean public MessageChannel ordersQueue() { return new QueueChannel(100); }
@Bean public MessageChannel broadcasts() { return new PublishSubscribeChannel(); }
```

**Правило**: `DirectChannel` по умолчанию — единственный канал, который прозрачно пропускает транзакции.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Что такое Service Activator? Частая ошибка в реальном коде.

**Service Activator** — endpoint, связывающий канал с методом бизнес-компонента.

```java
// Аннотация
@ServiceActivator(inputChannel = "ordersInput", outputChannel = "ordersOutput")
public OrderResult processOrder(Order order) {
    return orderService.process(order);
}

// Java DSL
IntegrationFlow flow = IntegrationFlow
    .from("ordersInput")
    .handle((Order order, MessageHeaders headers) -> orderService.process(order))
    .get();

// С явным указанием бина
@ServiceActivator(inputChannel = "ordersInput")
@Bean
public MessageHandler orderHandler(OrderService service) {
    return message -> service.process((Order) message.getPayload());
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Как работает MessagingGateway? Частая ошибка в реальном коде.

**Gateway** — интерфейс, который Spring Integration реализует автоматически. Скрывает детали каналов за обычными Java-методами.

```java
@MessagingGateway(defaultRequestChannel = "ordersInput",
                   defaultReplyChannel = "ordersOutput")
public interface OrderGateway {
    // Синхронный — ждёт ответ
    OrderResult placeOrder(Order order);

    // Асинхронный — не ждёт
    @Gateway(requestChannel = "ordersAsync")
    void placeOrderAsync(Order order);

    // С заголовком
    @Gateway(requestChannel = "priorityOrders")
    OrderResult placeUrgentOrder(@Payload Order order,
                                  @Header("priority") String priority);
}
```

```java
// Использование как обычный Spring-бин
@Autowired
private OrderGateway orderGateway;

public void submit(Order order) {
    OrderResult result = orderGateway.placeOrder(order);  // под капотом — сообщение
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Как устроен Java DSL? Частая ошибка в реальном коде.

**Java DSL** — декларативный способ описать интеграционный поток в одном `@Bean`.

```java
@Configuration
@EnableIntegration
public class OrderIntegrationFlow {

    @Bean
    public IntegrationFlow orderProcessingFlow(
            KafkaMessageDrivenChannelAdapter<String, String> kafkaAdapter,
            OrderService orderService) {

        return IntegrationFlow
            .from(kafkaAdapter)                                  // Inbound adapter
            .transform(Transformers.fromJson(Order.class))       // JSON → Order
            .filter((Order o) -> o.amount().signum() > 0,        // фильтр нулевых
                f -> f.discardChannel("rejectedOrders"))
            .<Order, String>route(                               // Content-Based Router
                o -> o.type().name(),
                m -> m.subFlowMapping("EXPRESS", sf -> sf
                        .handle(orderService::processExpress))
                      .subFlowMapping("NORMAL", sf -> sf
                        .handle(orderService::processNormal))
            )
            .get();
    }
}
```

Ключевые методы DSL: `.from()`, `.transform()`, `.filter()`, `.handle()`, `.route()`, `.split()`, `.aggregate()`, `.enrich()`, `.bridge()`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как реализовать Splitter и Aggregator? Частая ошибка в реальном коде.

**Splitter** — разбивает одно сообщение с коллекцией на несколько отдельных:

```java
// Аннотация
@Splitter(inputChannel = "batchOrders", outputChannel = "singleOrders")
public List<Order> split(BatchOrderRequest batch) {
    return batch.orders();
}

// DSL
.split(BatchOrderRequest.class, BatchOrderRequest::orders)
```

**Aggregator** — собирает несколько сообщений в одно по корреляционному ключу:

```java
@Aggregator(inputChannel = "orderResults", outputChannel = "batchResults")
public BatchResult aggregate(List<OrderResult> results) {
    return new BatchResult(results);
}

// DSL с настройкой корреляции
.aggregate(aggregator -> aggregator
    .correlationExpression("headers['batchId']")
    .releaseExpression("size() == headers['batchSize']")
    .sendPartialResultOnExpiry(true)
    .groupTimeout(5000L)  // отправить частичный результат через 5с
)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как обрабатывать ошибки? Частая ошибка в реальном коде.

```java
// 1. errorChannel — глобальный канал ошибок
@ServiceActivator(inputChannel = "errorChannel")
public void handleError(ErrorMessage errorMessage) {
    Throwable cause = errorMessage.getPayload().getCause();
    Message<?> failedMessage = errorMessage.getPayload().getFailedMessage();
    log.error("Integration error for message: {}", failedMessage, cause);
    // отправить в DLQ или алертинг
}

// 2. Локальная обработка ошибок в потоке
IntegrationFlow flow = IntegrationFlow
    .from("ordersInput")
    .handle(orderService::process,
        e -> e.advice(retryAdvice())  // retry при ошибке
              .errorChannel("orderErrorChannel"))
    .get();

// 3. RequestHandlerRetryAdvice
@Bean
public RequestHandlerRetryAdvice retryAdvice() {
    RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
    RetryTemplate template = RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(IOException.class)
        .build();
    advice.setRetryTemplate(template);
    return advice;
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Как подключить Kafka через Spring Integration? Частая ошибка в реальном коде.

```java
@Bean
public KafkaMessageDrivenChannelAdapter<String, String> kafkaInboundAdapter(
        ConcurrentKafkaListenerContainerFactory<String, String> factory) {
    KafkaMessageDrivenChannelAdapter<String, String> adapter =
        new KafkaMessageDrivenChannelAdapter<>(
            factory.createContainer("orders-topic"),
            ListenerMode.record
        );
    adapter.setOutputChannelName("ordersInput");
    return adapter;
}

@Bean
public IntegrationFlow kafkaToServiceFlow(
        KafkaMessageDrivenChannelAdapter<String, String> adapter,
        OrderService service) {
    return IntegrationFlow
        .from(adapter)
        .transform(Transformers.fromJson(Order.class))
        .handle(service::process)
        .get();
}

// Outbound adapter
@Bean
public KafkaProducerMessageHandler<String, String> kafkaOutboundAdapter(
        KafkaTemplate<String, String> template) {
    KafkaProducerMessageHandler<String, String> handler =
        new KafkaProducerMessageHandler<>(template);
    handler.setTopicExpression(new LiteralExpression("order-results"));
    return handler;
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как транзакции работают в Spring Integration? Частая ошибка в реальном коде.

```java
// DirectChannel поддерживает транзакцию от отправителя до конца потока
@Transactional
public void receiveAndProcess(Order order) {
    orderGateway.placeOrder(order);  // транзакция включает обработку в канале
}

// Для асинхронных каналов — настройка транзакций через Poller
IntegrationFlow flow = IntegrationFlow
    .from("queueChannel",
        c -> c.poller(Pollers.fixedDelay(1000)
            .transactionSynchronizationFactory(transactionSynchronizationFactory())
            .transactional()))
    .handle(service::process)
    .get();
```

**Ограничение**: транзакции работают только через `DirectChannel`. `QueueChannel` разрывает транзакционный контекст — нужна явная настройка через поллер.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как тестировать Spring Integration потоки? Частая ошибка в реальном коде.

```java
@SpringIntegrationTest
@SpringBootTest
class OrderIntegrationTest {

    @Autowired
    private MockIntegrationContext mockIntegrationContext;

    @Autowired
    private OrderGateway gateway;

    @Autowired
    @Qualifier("ordersOutput")
    private PollableChannel outputChannel;

    @Test
    void shouldProcessOrder() {
        Order order = new Order("customer-1", BigDecimal.TEN);

        gateway.placeOrder(order);

        Message<?> result = outputChannel.receive(5000);
        assertThat(result).isNotNull();
        assertThat(result.getPayload()).isInstanceOf(OrderResult.class);
    }

    @Test
    void shouldHandleServiceFailure() {
        // Замена реального handler на mock
        mockIntegrationContext.substituteMessageHandlerFor(
            "orderHandler",
            m -> { throw new RuntimeException("Service unavailable"); }
        );

        // Проверяем попадание в errorChannel
        gateway.placeOrder(new Order(...));

        Message<?> error = errorChannel.receive(5000);
        assertThat(error.getPayload()).isInstanceOf(ErrorMessage.class);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Чем Spring Integration отличается от Apache Camel? Частая ошибка в реальном коде.

| Критерий | Spring Integration | Apache Camel |
|----------|--------------------|--------------|
| Экосистема | Spring | Независимый |
| DSL | Java DSL (Spring) | Java/Scala/XML DSL |
| Коннекторы | ~30 адаптеров | 200+ компонентов |
| Learning curve | Низкая (Spring dev) | Средняя |
| EIP полнота | Основные паттерны | Почти все EIP |
| Тестирование | SpringIntegrationTest | CamelTestSupport |
| Production usage | Средний enterprise | Широко (Fuse, cloud) |

**Spring Integration** — выбор если проект уже на Spring и нужна минимальная инфраструктура. **Apache Camel** — если нужно 200+ коннекторов или специфические EIP паттерны.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Когда использовать Spring Integration vs Spring Kafka? Частая ошибка в реальном коде.

```text
Простой Kafka consumer/producer:
  → Spring Kafka (@KafkaListener, KafkaTemplate)

Несколько источников/приёмников с маршрутизацией:
  → Spring Integration с Kafka-адаптером

Сложный ETL с трансформациями, агрегацией, retry:
  → Spring Integration или Spring Batch

Real-time stream processing (windowing, joins):
  → Kafka Streams или Apache Flink
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Что такое Claim Check и как его реализовать? Частая ошибка в реальном коде.

**Claim Check** — паттерн: большие данные хранятся отдельно (S3, БД), в канале передаётся только ссылка-токен. Уменьшает размер сообщений в очереди.

```java
@Bean
public IntegrationFlow claimCheckFlow(ClaimCheckTransformer claimCheck) {
    return IntegrationFlow
        .from("largeOrdersInput")
        .transform(claimCheck::store)  // payload → S3, возвращает ключ
        .channel("lightweightChannel") // только ключ идёт дальше
        .get();
}

@Bean
public IntegrationFlow claimCheckRetrieveFlow(ClaimCheckTransformer claimCheck) {
    return IntegrationFlow
        .from("lightweightChannel")
        .transform(claimCheck::retrieve)  // ключ → реальный payload из S3
        .handle(orderService::process)
        .get();
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Как работает Enricher (Content Enricher)? Частая ошибка в реальном коде.

**Enricher** — добавляет данные из внешнего источника в сообщение (обогащение):

```java
// Обогащение заказа данными о клиенте
IntegrationFlow flow = IntegrationFlow
    .from("ordersInput")
    .enrich(enricher -> enricher
        .requestChannel("customerLookupChannel")
        .requestPayloadExpression("payload.customerId")
        .propertyFunction("customerName",
            response -> ((CustomerInfo) response.getPayload()).name())
        .propertyFunction("creditLimit",
            response -> ((CustomerInfo) response.getPayload()).creditLimit())
    )
    .handle(enrichedOrderService::process)
    .get();
```

Enricher делает запрос в `customerLookupChannel` и добавляет результат в заголовки или поля payload.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Как мониторить Spring Integration? Частая ошибка в реальном коде.

```java
// Spring Integration Metrics через Micrometer
@Configuration
public class MetricsConfig {

    @Bean
    public MetricsIntegrationExtension metricsExtension(MeterRegistry registry) {
        return new MetricsIntegrationExtension(registry);
    }
}
```

Метрики Spring Integration:
- `spring.integration.send` — количество отправленных сообщений по каналу
- `spring.integration.receive` — получение (для поллируемых каналов)
- `spring.integration.handlers` — время выполнения handlers
- `spring.integration.channels.queueSize` — размер очереди для `QueueChannel`

```yaml
management:
  endpoints:
    web:
      exposure:
        include: integrationgraph  # /actuator/integrationgraph — граф потоков
```

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Spring Kafka](spring-kafka-interview.md) — Kafka как канал/адаптер в Spring Integration Частая ошибка в реальном коде.
- [Spring Messaging](spring-messaging-interview.md) — JMS, RabbitMQ базовые абстракции
- [Spring Batch](spring-batch-interview.md) — batch-обработка, часто используется совместно
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — теория EIP паттернов
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива для монолита
- [Spring WebFlux](spring-webflux-interview.md) — FluxMessageChannel как мост к реактивному стеку
- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka для KafkaMessageDrivenChannelAdapter
- [RabbitMQ](../../messaging/rabbitmq-interview.md) — основы AMQP для RabbitMQ channel adapter
- [Spring @Transactional](spring-transaction-interview.md) — транзакции в интеграционных потоках
- [Spring Testing](spring-testing-interview.md) — тестирование Spring Integration потоков
