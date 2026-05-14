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
>
> **Вопрос:** Почему Spring Integration реализует именно EIP-паттерны Хопе и Вульфа, а не предлагает свою модель интеграции? Что это даёт архитектуре приложения?
>
> ---
>
> #### A) Spring Integration — это обёртка над JMS API, она просто упрощает работу с очередями сообщений — ❌ Неверно
>
> **Что на самом деле:** Spring Integration транспортно-агностичен. JMS — лишь один из десятков адаптеров (Kafka, AMQP, FTP, HTTP, File, JDBC, Email и др.). Ядро — это `Message<Payload, Headers>` и `MessageChannel`, которые не знают про конкретный транспорт. JMS adapter — отдельный модуль `spring-integration-jms`.
>
> **Откуда путаница:** многие первое знакомство с интеграцией получают через JMS (J2EE-эра), и любую «messaging-абстракцию» воспринимают как очередь.
>
> **Если бы это было правдой:** Spring Integration не смог бы пайплайнить File → Transform → Kafka без брокера. На практике один и тот же `IntegrationFlow` цепляет inbound/outbound адаптеры разных протоколов без переписывания бизнес-логики.
>
> ---
>
> #### B) Это реализация EIP-каталога Хопе-Вульфа: единый словарь Message/Channel/Endpoint/Gateway даёт переносимый язык дизайна интеграций — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Книга «Enterprise Integration Patterns» (Hohpe & Woolf, 2003) каталогизировала 65 паттернов асинхронной интеграции: Message, Channel, Router, Filter, Splitter, Aggregator, Service Activator, Gateway, Claim Check и др. Spring Integration берёт эти паттерны как **first-class building blocks** — каждому соответствует Java-абстракция или DSL-метод.
>
> Польза — общий словарь. Архитектор на доске рисует «Splitter → Filter → Aggregator», и разработчик мгновенно отображает это в `.split().filter().aggregate()`. Не нужно изобретать названия. Те же паттерны работают в Apache Camel, Mule, IBM Integration Bus — это переносимое знание.
>
> Ключевые абстракции: `Message<T>` (payload + headers), `MessageChannel` (транспорт между endpoint-ами), `MessageEndpoint` (узел обработки: filter/transformer/router/service-activator), `Gateway` (вход/выход из мира интеграции для обычного Java-кода).
>
> **Пример:**
> ```java
> @Configuration
> @EnableIntegration
> public class OrdersIntegration {
>     @Bean
>     public IntegrationFlow ordersFlow(OrderService service) {
>         return IntegrationFlow
>             .from(Kafka.messageDrivenChannelAdapter(consumerFactory, "orders"))
>             .transform(Transformers.fromJson(Order.class))   // Message Translator
>             .filter((Order o) -> o.amount().signum() > 0)    // Message Filter
>             .<Order, String>route(o -> o.type().name(),      // Content-Based Router
>                 m -> m.subFlowMapping("EXPRESS", sf -> sf.handle(service::express))
>                       .subFlowMapping("NORMAL", sf -> sf.handle(service::normal)))
>             .get();
>     }
> }
> ```
>
> **Когда применять:** интеграции между гетерогенными системами (Kafka + REST + DB + S3 в одном flow), ETL-конвейеры с трансформациями, batch-orchestration через event-driven подход, разрыв связности между bounded contexts в монолите через каналы.
>
> **Подводные камни:** EIP-паттерны решают **асинхронную** интеграцию. Для синхронного RPC (REST между микросервисами) Spring Integration избыточен — хватит `WebClient`/`RestTemplate`. Не путать каналы Spring Integration (in-process) с реальными брокерами (Kafka, RabbitMQ) — каналы это абстракция, под ними может быть как `LinkedBlockingQueue`, так и Kafka topic через адаптер.
>
> **Связанные вопросы:** [[Q2]] — типы MessageChannel; [[Q4]] — Gateway как фасад; [[Q11]] — Spring Integration vs Apache Camel.
>
> ---
>
> #### C) Spring Integration — это альтернатива Spring Boot для интеграционных приложений, его нельзя использовать в обычном Spring Boot — ❌ Неверно
>
> **Что на самом деле:** Spring Integration — это **дополнение** к Spring Boot, не альтернатива. Подключается через `spring-boot-starter-integration` и `@EnableIntegration`. Живёт в том же `ApplicationContext`, использует те же `@Bean`-ы и autoconfiguration.
>
> **Откуда путаница:** название «Integration» звучит как отдельный стек. На практике это Spring-модуль среди прочих, как `spring-data-jpa` или `spring-security`.
>
> **Если бы это было правдой:** нельзя было бы в одном Spring Boot приложении иметь и REST-контроллер, и интеграционный flow. На деле миллионы приложений делают именно так.
>
> ---
>
> #### D) Spring Integration — это реализация JBI (Java Business Integration) спецификации — ❌ Неверно
>
> **Что на самом деле:** Spring Integration **не реализует** JSR-208 (JBI). JBI — устаревший стандарт ESB-контейнеров, реализованный в OpenESB/ServiceMix; Spring команда осознанно отказалась от него в пользу EIP. Сам JBI давно мёртв (последний релиз спецификации — 2005).
>
> **Откуда путаница:** в 2000-х любая интеграционная платформа должна была заявить совместимость с JBI. Spring Integration вышел в 2007 году и стал успешен **именно потому**, что не пошёл этим путём.
>
> **Если бы это было правдой:** Spring Integration зависел бы от мёртвой спецификации и устаревших контейнеров. На практике он легковесен и работает в любом Java-процессе.

## Q2. Какие типы MessageChannel существуют?

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
>
> **Вопрос:** В чём принципиальная разница между `DirectChannel` и `QueueChannel` с точки зрения транзакционных границ и threading-модели?
>
> ---
>
> #### A) `DirectChannel` и `QueueChannel` отличаются только наличием буфера — в остальном работают одинаково — ❌ Неверно
>
> **Что на самом деле:** различие фундаментальное. `DirectChannel` — **синхронный**: метод `send()` сам выполняет handler на том же потоке отправителя. `QueueChannel` — **асинхронный**: `send()` кладёт сообщение в очередь и возвращается; обработка происходит на отдельном потоке, который запускает `PollingConsumer` через `Poller`.
>
> **Откуда путаница:** оба реализуют `MessageChannel.send(Message)`, и снаружи API выглядит одинаково. Но семантика выполнения разная.
>
> **Если бы это было правдой:** простой апгрейд `new DirectChannel()` → `new QueueChannel(100)` не ломал бы транзакционные сценарии. На практике после такой замены теряется `@Transactional`-контекст, и rollback при ошибке consumer-а перестаёт работать.
>
> ---
>
> #### B) `DirectChannel` синхронно проносит транзакцию и ThreadLocal до получателя; `QueueChannel` разрывает поток и требует Poller с отдельной транзакцией — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `DirectChannel` — это «прямой вызов»: handler выполняется в стеке `send()`-вызывающего потока. Это значит, что `TransactionSynchronizationManager` (Spring's `@Transactional`), `SecurityContextHolder` (Spring Security), `RequestContextHolder` (MVC) — все `ThreadLocal`-контексты остаются доступны handler-у. Транзакция, начатая выше по стеку, охватывает всю цепочку.
>
> `QueueChannel` хранит сообщения в `BlockingQueue` (default capacity unbounded). Отправитель кладёт сообщение и возвращается мгновенно (или блокируется при capacity overflow). Чтобы сообщение было обработано, нужен `PollingConsumer` с настроенным `Poller` — он на отдельном потоке (`TaskScheduler`) делает `receive()` из очереди и вызывает handler. Это **разрывает** ThreadLocal-контекст: транзакция, security, MDC не пересекают границу очереди.
>
> Другие типы каналов: `ExecutorChannel` (асинхронный, но через `TaskExecutor` без буфера — async dispatch), `PublishSubscribeChannel` (broadcast всем подписчикам), `PriorityChannel` (приоритетная очередь), `FluxMessageChannel` (Reactor bridge для WebFlux).
>
> **Пример:**
> ```java
> @Configuration
> public class ChannelsConfig {
>     // Sync — транзакция проносится насквозь
>     @Bean MessageChannel ordersDirect() { return new DirectChannel(); }
>
>     // Async — нужен Poller для consume
>     @Bean MessageChannel ordersQueue() { return new QueueChannel(1000); }
>
>     // Async + Poller с транзакцией
>     @Bean
>     public IntegrationFlow asyncFlow(OrderService service, PlatformTransactionManager tm) {
>         return IntegrationFlow
>             .from("ordersQueue",
>                 c -> c.poller(Pollers.fixedDelay(100)
>                     .transactional(tm)         // транзакция стартует на каждом poll-tick
>                     .maxMessagesPerPoll(10)))
>             .handle(service::process)
>             .get();
>     }
> }
> ```
>
> **Когда применять:**
> - `DirectChannel` (default) — когда нужна сквозная транзакция и низкая latency. 95% in-process потоков.
> - `QueueChannel` — буферизация между быстрым продюсером и медленным консьюмером, развязка потоков (отправитель не ждёт обработки).
> - `ExecutorChannel` — параллельная обработка одного потока N воркерами (concurrent service-activators).
> - `PublishSubscribeChannel` — fan-out, события, уведомления нескольким подписчикам.
>
> **Подводные камни:**
> - **`QueueChannel` теряет сообщения при рестарте** — это in-memory `LinkedBlockingQueue`. Для durability нужен `MessageStore` (JDBC, Redis) или внешний брокер (Kafka/AMQP-канал).
> - **Backpressure**: `QueueChannel(capacity)` блокирует `send()` при заполнении. Без capacity — unbounded, риск OOM.
> - **Полл-задержка**: `Pollers.fixedDelay(1000)` означает 1 сек latency на каждое сообщение. Для low-latency лучше `ExecutorChannel`.
> - **`PublishSubscribeChannel` synchronous by default** — медленный подписчик блокирует всех остальных. Передать `TaskExecutor` через `setTaskExecutor()` для async fan-out.
>
> **Связанные вопросы:** [[Q1]] — EIP-словарь; [[Q9]] — транзакции в Spring Integration; [[Q3]] — Service Activator как handler канала.
>
> ---
>
> #### C) `QueueChannel` поддерживает persistence из коробки (как Kafka) — сообщения переживают рестарт — ❌ Неверно
>
> **Что на самом деле:** `QueueChannel` — это in-memory `BlockingQueue`. После рестарта JVM очередь пуста, все непрочитанные сообщения теряются. Для durability нужен либо внешний `MessageStore` (JDBC/MongoDB/Redis), либо канал-адаптер к брокеру (Kafka, RabbitMQ).
>
> **Откуда путаница:** слово «Queue» ассоциируется с Kafka/RabbitMQ, которые персистентны. Но `QueueChannel` — это просто Java-объект, а не подключение к брокеру.
>
> **Если бы это было правдой:** не нужны были бы отдельные модули `spring-integration-kafka`/`spring-integration-amqp` для надёжной доставки. На практике критичные сообщения всегда идут через брокер, а `QueueChannel` — для in-memory разгрузки.
>
> ---
>
> #### D) `PublishSubscribeChannel` гарантирует exactly-once delivery каждому подписчику — ❌ Неверно
>
> **Что на самом деле:** `PublishSubscribeChannel` делает synchronous fan-out: вызывает handler каждого подписчика по очереди в потоке отправителя. Если один из них бросает исключение, поведение зависит от `ignoreFailures`: false (default) — exception прерывает цепочку, последующие подписчики не вызываются; true — exception логируется и итерация продолжается. Гарантии «exactly-once» нет — нет персистентности и retry.
>
> **Откуда путаница:** «publish-subscribe» как паттерн часто реализуется в Kafka/JMS с гарантиями at-least-once. Но in-process `PublishSubscribeChannel` — это просто синхронный broadcast в Java.
>
> **Если бы это было правдой:** не нужен был бы Kafka для надёжной event-distribution. На деле для exactly-once нужны идемпотентные consumer-ы и transactional outbox.

## Q3. Что такое Service Activator?

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Как работает MessagingGateway? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Как устроен Java DSL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Как реализовать Splitter и Aggregator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как обрабатывать ошибки? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как подключить Kafka через Spring Integration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как транзакции работают в Spring Integration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как тестировать Spring Integration потоки? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Чем Spring Integration отличается от Apache Camel? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Когда использовать Spring Integration vs Spring Kafka? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Что такое Claim Check и как его реализовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как работает Enricher (Content Enricher)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как мониторить Spring Integration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring Kafka](spring-kafka-interview.md) — Kafka как канал/адаптер в Spring Integration ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring Messaging](spring-messaging-interview.md) — JMS, RabbitMQ базовые абстракции
- [Spring Batch](spring-batch-interview.md) — batch-обработка, часто используется совместно
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — теория EIP паттернов
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива для монолита
- [Spring WebFlux](spring-webflux-interview.md) — FluxMessageChannel как мост к реактивному стеку
- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka для KafkaMessageDrivenChannelAdapter
- [RabbitMQ](../../messaging/rabbitmq-interview.md) — основы AMQP для RabbitMQ channel adapter
- [Spring @Transactional](spring-transaction-interview.md) — транзакции в интеграционных потоках
- [Spring Testing](spring-testing-interview.md) — тестирование Spring Integration потоков
