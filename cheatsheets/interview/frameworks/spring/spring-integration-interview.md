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
>
> **Вопрос:** Что отличает Service Activator от обычного Spring-сервиса (`@Service`), вызванного из `@Component`-обработчика? Зачем нужна отдельная EIP-абстракция?
>
> ---
>
> #### A) Service Activator — это просто синоним `@Service`, аннотации `@ServiceActivator` и `@Service` взаимозаменяемы — ❌ Неверно
>
> **Что на самом деле:** это **разные** аннотации с разной семантикой. `@Service` маркирует класс как Spring-bean бизнес-уровня (это `@Component` stereotype). `@ServiceActivator` — это **endpoint-маркер**: связывает метод с `inputChannel`, создаёт `MessageHandler` и `PollingConsumer`/`EventDrivenConsumer`, который слушает канал и вызывает метод при поступлении сообщения.
>
> **Откуда путаница:** оба слова имеют «Service» в названии. Но `@Service` — про DI/stereotype, а `@ServiceActivator` — про подписку на канал.
>
> **Если бы это было правдой:** не нужен был бы `inputChannel`-атрибут — обычный `@Service` ничего про каналы не знает. На практике без `@ServiceActivator` метод не подпишется на канал.
>
> ---
>
> #### B) Service Activator — это endpoint, который инкапсулирует бизнес-метод и подписывает его на `MessageChannel` через `MessageHandler`-обёртку — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Service Activator (EIP-паттерн) — это «активатор сервиса»: мост между миром Message-каналов и миром обычных Java-методов. Spring Integration оборачивает ваш бизнес-метод в `ServiceActivatingHandler` (реализация `MessageHandler`), создаёт `Consumer` (`PollingConsumer` для `PollableChannel` или `EventDrivenConsumer` для `SubscribableChannel`), и подписывает его на `inputChannel`.
>
> Когда сообщение приходит в канал:
> 1. Consumer вытаскивает `Message<T>`.
> 2. `ServiceActivatingHandler` извлекает payload (или передаёт `Message` целиком, если метод принимает `Message`).
> 3. Spring через `MethodInvokingMessageProcessor` маппит аргументы метода: `@Payload`, `@Header("name")`, `@Headers Map`, и др.
> 4. Метод вызывается; return value (если не `void`) оборачивается в новое `Message` и отправляется в `outputChannel` (если задан) или в `replyChannel` из заголовков.
>
> Это даёт **invocation-агностику**: бизнес-метод не знает про Spring Integration. Его можно тестировать как обычный сервис, плюс интегрировать в flow через аннотацию или DSL.
>
> **Пример:**
> ```java
> @MessageEndpoint
> public class OrderProcessor {
>     @ServiceActivator(inputChannel = "ordersInput", outputChannel = "ordersResult")
>     public OrderResult process(@Payload Order order, @Header("traceId") String traceId) {
>         MDC.put("traceId", traceId);
>         return orderService.handle(order);
>     }
> }
>
> // Эквивалент через Java DSL
> @Bean
> public IntegrationFlow orderFlow(OrderService service) {
>     return IntegrationFlow.from("ordersInput")
>         .handle(Order.class, (payload, headers) -> service.handle(payload))
>         .channel("ordersResult")
>         .get();
> }
> ```
>
> **Когда применять:** любая бизнес-логика, которая должна вызываться по сообщению из канала — обработка заказов из Kafka, преобразование файлов, ответ на REST-вход через `HttpRequestHandlingMessagingGateway`. Это самый частый endpoint после `Transformer` и `Filter`.
>
> **Подводные камни:**
> - **`void`-метод не отправляет reply**: если нужно подтвердить обработку, метод должен возвращать значение или явно отправлять в `outputChannel` через `@SendTo`.
> - **Exception без error-handling** уходит в global `errorChannel` (если задан) или поднимается вверх. На `DirectChannel` exception летит обратно в `send()`-caller.
> - **`outputChannel` имеет приоритет над `replyChannel` из заголовков** — если оба заданы, reply идёт в `outputChannel`. Это ломает request-reply через Gateway.
> - **Метод должен быть `public`** — иначе Spring AOP не сможет проксировать.
>
> **Связанные вопросы:** [[Q4]] — Gateway как обратная сторона Service Activator; [[Q5]] — Java DSL `.handle()` как альтернатива; [[Q7]] — обработка ошибок в handler.
>
> ---
>
> #### C) Service Activator работает только с `QueueChannel`, на `DirectChannel` его использовать нельзя — ❌ Неверно
>
> **Что на самом деле:** Service Activator работает с обоими типами каналов. Разница в том, какой `Consumer` создаётся: `EventDrivenConsumer` для `SubscribableChannel` (включая `DirectChannel`), `PollingConsumer` для `PollableChannel` (включая `QueueChannel`). Spring Integration выбирает автоматически на основе типа канала.
>
> **Откуда путаница:** в документации часто примеры с `QueueChannel` и `Poller`, что создаёт впечатление обязательности.
>
> **Если бы это было правдой:** простой синхронный pipeline на `DirectChannel` был бы невозможен — нужен был бы Poller на каждом шаге. На практике 90% Service Activator-ов работают на `DirectChannel`.
>
> ---
>
> #### D) Service Activator автоматически делает retry при exception до 3 раз — ❌ Неверно
>
> **Что на самом деле:** retry **не включён по умолчанию**. Exception в handler-методе летит вверх (или в `errorChannel`). Для retry нужен `RequestHandlerRetryAdvice` (на базе Spring Retry) — он явно подключается через `@ServiceActivator(adviceChain = "retryAdvice")` или DSL `.handle(svc, e -> e.advice(retryAdvice()))`.
>
> **Откуда путаница:** многие messaging-фреймворки делают retry по умолчанию (Spring Kafka с DefaultErrorHandler делает 10 попыток). Но Spring Integration этого не делает — политика на разработчике.
>
> **Если бы это было правдой:** не нужны были бы Q7-вопросы про `RequestHandlerRetryAdvice`. Reality: без явной настройки retry нет, и первая же transient-ошибка (network blip) пробивает в `errorChannel`.

## Q4. Как работает MessagingGateway?

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
>
> **Вопрос:** Как `@MessagingGateway`-интерфейс превращается в работающий bean без вашей реализации? Что именно генерирует Spring и как там работает request-reply?
>
> ---
>
> #### A) Spring генерирует bytecode реализации через CGLIB — это enchancer, как у `@Configuration`-классов — ❌ Неверно
>
> **Что на самом деле:** Spring Integration использует **JDK Dynamic Proxy** (`java.lang.reflect.Proxy`) — стандартный механизм Java для интерфейсов. `GatewayProxyFactoryBean` создаёт proxy через `Proxy.newProxyInstance()` с `InvocationHandler`, который для каждого вызова метода: формирует `Message<>`, отправляет в `defaultRequestChannel` (или `requestChannel` из `@Gateway`), при синхронных методах блокирует поток ожидая reply, оборачивает результат в return-тип метода.
>
> **Откуда путаница:** в Spring Framework для классов проксирование идёт через CGLIB, для интерфейсов — через JDK proxy. `@MessagingGateway` работает **только на интерфейсах**, поэтому именно JDK proxy.
>
> **Если бы это было правдой:** работало бы и для абстрактных классов. На практике `@MessagingGateway` на классе вызывает ошибку конфигурации.
>
> ---
>
> #### B) Spring создаёт JDK Dynamic Proxy для интерфейса, который сериализует вызов в `Message`, шлёт в request-канал и блокирует ожидая reply через temporary reply-channel — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `GatewayProxyFactoryBean` (FactoryBean) — за `@MessagingGateway` стоит именно он. Шаги при вызове `gateway.placeOrder(order)`:
>
> 1. Proxy перехватывает вызов через `InvocationHandler`.
> 2. Spring через `MethodArgsHolder` маппит аргументы: `@Payload` → payload, `@Header("name")` → header, `@Headers Map<String,Object>` → headers map.
> 3. Создаётся `Message<Order>` с payload + headers + сгенерированным `replyChannel` (`TemporaryReplyChannel` — anonymous `DirectChannel`).
> 4. Сообщение отправляется в `defaultRequestChannel` или channel из `@Gateway(requestChannel = "...")`.
> 5. Если метод **возвращает значение** — Gateway блокируется на `replyChannel.receive(timeout)`, ждёт ответ.
> 6. Если метод `void` — fire-and-forget, возврат сразу.
> 7. Если возвращает `Future<X>` / `CompletableFuture<X>` / `Mono<X>` / `Flux<X>` — Spring оборачивает асинхронно без блокировки.
>
> Это даёт **декларативный фасад**: бизнес-код не знает про каналы, он работает с обычным Java-интерфейсом. Под капотом — асинхронная инфраструктура.
>
> **Пример:**
> ```java
> @MessagingGateway(defaultRequestChannel = "ordersInput",
>                   defaultReplyTimeout = "5000",
>                   defaultRequestTimeout = "2000")
> public interface OrderGateway {
>     OrderResult placeOrder(@Payload Order order);           // sync request-reply
>
>     @Gateway(requestChannel = "ordersAsync")
>     void placeOrderAsync(Order order);                       // fire-and-forget
>
>     @Gateway(requestChannel = "ordersInput", replyTimeout = 10000)
>     CompletableFuture<OrderResult> placeOrderFuture(Order order);  // async result
>
>     @Gateway(requestChannel = "ordersInput")
>     Mono<OrderResult> placeOrderReactive(Order order);       // reactive
> }
>
> @Service
> @RequiredArgsConstructor
> public class CheckoutService {
>     private final OrderGateway gateway;   // обычный @Autowired — Spring inject-ит proxy
>     public OrderResult checkout(Cart cart) { return gateway.placeOrder(new Order(cart)); }
> }
> ```
>
> **Когда применять:** Gateway — стандартный способ войти в integration flow из контроллера/сервиса/тестов. Альтернатива — `MessagingTemplate.convertSendAndReceive()`, но это императивно и менее читаемо.
>
> **Подводные камни:**
> - **`defaultReplyTimeout` = 30 секунд по умолчанию**. Если handler тормозит, Gateway возвращает `null` (не exception!) — критично проверять на `null` или ставить адекватный timeout.
> - **`void` метод без `@Async`**: если канал — `DirectChannel`, метод всё равно блокируется на handler (synchronous chain). `void` влияет только на ожидание reply, не на dispatch.
> - **`CompletableFuture` требует `AsyncTaskExecutor`** в `@MessagingGateway(asyncExecutor = "...")` — иначе future будет уже завершённым (синхронным).
> - **Reply correlation**: Gateway генерирует уникальный `TemporaryReplyChannel` на каждый вызов — это thread-safe для concurrent calls.
>
> **Связанные вопросы:** [[Q3]] — Service Activator как ответ на сообщение Gateway; [[Q1]] — Gateway-паттерн в EIP-каталоге; [[Q10]] — тестирование через Gateway.
>
> ---
>
> #### C) Gateway работает только синхронно — для async нужно вручную реализовать `MessageHandler` — ❌ Неверно
>
> **Что на самом деле:** Gateway поддерживает все три модели: sync (return T), fire-and-forget (return `void`), async (return `Future<T>`/`CompletableFuture<T>`/`Mono<T>`/`Flux<T>`). Достаточно изменить сигнатуру метода — Spring сам подберёт InvocationHandler-логику.
>
> **Откуда путаница:** первые версии Spring Integration (до 4.0) поддерживали только sync. Сейчас async/reactive поддерживается из коробки.
>
> **Если бы это было правдой:** не было бы интеграции Spring Integration с WebFlux. На практике контроллер на WebFlux вызывает Gateway-метод, возвращающий `Mono<T>`, и весь стек реактивен.
>
> ---
>
> #### D) Gateway требует, чтобы интерфейс наследовал `org.springframework.integration.gateway.Gateway` — ❌ Неверно
>
> **Что на самом деле:** Gateway — это **обычный Java-интерфейс** с аннотацией `@MessagingGateway` (или `<int:gateway>` в XML). Никаких наследований не нужно. Это намеренная design-decision: интерфейс должен быть «чистым», без привязки к Spring Integration.
>
> **Откуда путаница:** многие Spring-абстракции требуют наследование marker-интерфейсов (`CrudRepository`, `JpaRepository`).
>
> **Если бы это было правдой:** бизнес-интерфейс был бы загрязнён dependency на Spring Integration. Это противоречит цели Gateway — «скрыть messaging от потребителя».

## Q5. Как устроен Java DSL?

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
>
> **Вопрос:** Когда `IntegrationFlow` builder вызывает `.get()`, что возвращается и как это попадает в Spring контекст? Что определяет «когда flow стартует»?
>
> ---
>
> #### A) `.get()` возвращает уже подписанный объект, поток начинает работать сразу при вызове `.get()` — ❌ Неверно
>
> **Что на самом деле:** `.get()` возвращает `IntegrationFlow` — это **спецификация** (immutable description), а не подписанный poller. Регистрация endpoint-ов, создание каналов, подписка на адаптеры происходит **позже** — когда Spring `IntegrationFlowContext` или `IntegrationFlowBeanPostProcessor` обрабатывает этот `@Bean` во время старта контекста.
>
> **Откуда путаница:** в reactive Stream `subscribe()` запускает подписку. В Spring Integration DSL подписка автоматическая, но не на вызове `.get()`, а на регистрации bean-а в контексте.
>
> **Если бы это было правдой:** flow начинал бы работать до того, как остальные bean-ы инициализированы — `OrderService`, `KafkaTemplate` и т. п. могли бы быть `null`. На практике flow стартует после `ApplicationContext.refresh()`, когда все bean-ы готовы.
>
> ---
>
> #### B) `.get()` возвращает `IntegrationFlow` (immutable спецификация); Spring через `IntegrationFlowBeanPostProcessor` регистрирует endpoint-ы и каналы при refresh контекста, flow стартует с `SmartLifecycle.start()` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Java DSL — это **builder pattern** поверх `IntegrationFlowDefinition`. Каждый метод (`from`, `transform`, `filter`, `handle`, `route`, `split`, `aggregate`, `channel`, `enrich`, `bridge`) добавляет шаг в внутренний список `componentsToRegister`. `.get()` возвращает immutable `IntegrationFlow` с этим списком.
>
> Когда `IntegrationFlow` объявлен как `@Bean`, при старте контекста:
> 1. `IntegrationFlowBeanPostProcessor` находит bean типа `IntegrationFlow`.
> 2. Итерирует по `componentsToRegister` и регистрирует каждый компонент: каналы (`DirectChannel` между шагами), endpoint-ы (`ConsumerEndpointFactoryBean` → `EventDrivenConsumer` или `PollingConsumer`), handler-ы (`ServiceActivatingHandler`, `MessageFilter`, `MessageTransformer`).
> 3. Каждый endpoint реализует `SmartLifecycle`, что означает: при `ApplicationContext.refresh()` Spring вызывает `start()` в порядке `phase` (по умолчанию `Integer.MAX_VALUE` для endpoints, что значит «стартуют последними»).
> 4. После старта endpoint-ы подписываются на свои `inputChannel` — flow начинает обрабатывать входящие сообщения.
>
> Альтернатива — динамическая регистрация через `IntegrationFlowContext.registration(flow).register()` (runtime), которая полезна для multi-tenant сценариев или горячих изменений.
>
> **Пример:**
> ```java
> @Bean
> public IntegrationFlow ordersPipeline(OrderService service, KafkaTemplate<String,String> kafka) {
>     return IntegrationFlow
>         .from(Kafka.messageDrivenChannelAdapter(consumerFactory, "orders-in"))
>         .transform(Transformers.fromJson(Order.class))
>         .filter((Order o) -> o.amount().signum() > 0,
>             f -> f.discardChannel("rejectedOrders"))
>         .<Order, String>route(o -> o.type().name(),
>             m -> m.subFlowMapping("EXPRESS", sf -> sf.handle(service::express))
>                   .subFlowMapping("NORMAL", sf -> sf.handle(service::normal)))
>         .transform(Transformers.toJson())
>         .handle(Kafka.outboundChannelAdapter(kafka).topic("orders-out"))
>         .get();
> }
> ```
>
> **Когда применять:** Java DSL — рекомендуемый способ описания flow в современных Spring Integration приложениях. XML-конфигурация — legacy, аннотации (`@ServiceActivator`, `@Filter`, и т. д.) — для отдельных endpoint-ов в обычных bean-классах.
>
> **Подводные камни:**
> - **Анонимные каналы**: между шагами DSL создаёт анонимные `DirectChannel` с генерированными именами. Если нужен named channel для метрик/monitoring — явный `.channel("name")` между шагами.
> - **Type-inference при `.<Source,Target>route()`**: Java не всегда корректно выводит generics, требуются explicit type witnesses.
> - **`flow.get()` vs `flowReturning(...).get()`**: первый не возвращает результат, второй возвращает Mono/Future — важно для Gateway request-reply.
> - **Lifecycle ordering**: если flow использует bean, который ещё не готов, нужны `@DependsOn` или `phase` настройка endpoint-ов.
>
> **Связанные вопросы:** [[Q4]] — Gateway как точка входа в flow; [[Q1]] — DSL-методы соответствуют EIP-паттернам; [[Q8]] — Kafka adapter в DSL.
>
> ---
>
> #### C) Java DSL — это просто синтаксический сахар над XML, под капотом всё компилируется в `<int:chain>` XML и парсится — ❌ Неверно
>
> **Что на самом деле:** Java DSL и XML — параллельные API. DSL **напрямую** создаёт Java-объекты `IntegrationFlow`, без промежуточной XML-генерации. Это даёт type-safety, IDE-autocomplete, refactoring-support и compile-time проверки.
>
> **Откуда путаница:** XML был первым API Spring Integration (2007), DSL появился в 4.0 (2014). Многие думают, что новый API строится поверх старого.
>
> **Если бы это было правдой:** при ошибке в DSL стек-трейс упоминал бы XML-парсер. На практике стек идёт через `IntegrationFlowBuilder` напрямую.
>
> ---
>
> #### D) Каждый метод DSL (например, `.filter`) создаёт новый `IntegrationFlow` — это immutable builder в стиле `Stream` API — ❌ Неверно
>
> **Что на самом деле:** `IntegrationFlowBuilder` — **mutable** builder (как `StringBuilder`). Каждый метод модифицирует внутренний список `componentsToRegister` и возвращает `this`. Это отличается от immutable Stream API. Reuse одного builder между двумя flow невозможен.
>
> **Откуда путаница:** chaining-синтаксис похож на Stream/Optional, но Spring Integration предпочёл mutable builder для производительности и простоты.
>
> **Если бы это было правдой:** можно было бы делать `var base = flow.from(...).transform(...);` и переиспользовать `base` в двух потоках. На практике это приведёт к двойной регистрации компонентов.

## Q6. Как реализовать Splitter и Aggregator?

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
>
> **Вопрос:** Почему Aggregator требует `correlationStrategy` и `releaseStrategy`, и что произойдёт, если их не настроить? Как Aggregator понимает, что группа готова?
>
> ---
>
> #### A) Aggregator группирует все сообщения подряд за указанный таймаут — никакие стратегии не нужны — ❌ Неверно
>
> **Что на самом деле:** Aggregator работает по **correlation key**: сообщения с одинаковым ключом попадают в одну `MessageGroup`. Без явной `correlationStrategy` он использует `IntegrationMessageHeaderAccessor.CORRELATION_ID` из заголовков. Если заголовка нет — каждое сообщение получает уникальный ID и группа не формируется. Time-based группировка — отдельный механизм через `groupTimeout`, не замена correlation.
>
> **Откуда путаница:** многие путают Aggregator с time-windowing (Kafka Streams). Это разные паттерны: Aggregator корреляционный, windowing — временной.
>
> **Если бы это было правдой:** не нужен был бы Splitter рядом — он генерирует именно `correlationId` для последующей сборки. Splitter+Aggregator работают в паре через одинаковый `correlationId`.
>
> ---
>
> #### B) Aggregator корреляционно группирует через `correlationStrategy` (key) и решает «когда отдавать» через `releaseStrategy` (size/expression/timeout); без них использует header `correlationId` + `sequenceSize` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Aggregator реализует EIP-паттерн «State-based / Stateful» компонент. Внутри держит `MessageStore` (default — `SimpleMessageStore` in-memory), где накапливает `MessageGroup` — группы сообщений по `correlationKey`.
>
> Три стратегии:
> 1. **CorrelationStrategy** — определяет ключ группы. Default: `HeaderAttributeCorrelationStrategy(CORRELATION_ID)`. Кастомные: SpEL (`headers['batchId']`), Lambda, метод с `@CorrelationStrategy`.
> 2. **ReleaseStrategy** — определяет, когда группа «complete». Default: `SequenceSizeReleaseStrategy` — ждёт `sequenceSize` сообщений (заголовок, который ставит Splitter). Кастомные: `groupSize() == N`, SpEL (`size() == headers['batchSize']`), `TimeoutCountSequenceSizeReleaseStrategy`.
> 3. **MessageGroupProcessor** — что вернуть из группы. Default: `DefaultAggregatingMessageGroupProcessor` — соединяет payloads в `List<T>`.
>
> Дополнительно `groupTimeout` — частичный release: если за timeout ms группа не дошла до size, отдать что есть.
>
> Splitter перед Aggregator-ом автоматически ставит заголовки `CORRELATION_ID` (из исходного messageId) + `SEQUENCE_SIZE` (количество split-ов) + `SEQUENCE_NUMBER` — это «магия» pair-операции.
>
> **Пример:**
> ```java
> @Bean
> public IntegrationFlow batchProcessing(OrderProcessor processor) {
>     return IntegrationFlow.from("batchInput")
>         // Splitter: BatchOrderRequest{orders: [o1, o2, o3]} → 3 сообщения
>         .split(BatchOrderRequest.class, BatchOrderRequest::orders)
>         // Каждый Order обрабатывается параллельно
>         .channel(c -> c.executor(taskExecutor()))
>         .handle(Order.class, (o, h) -> processor.process(o))
>         // Aggregator: собрать назад по correlationId
>         .aggregate(spec -> spec
>             .correlationStrategy(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.CORRELATION_ID))
>             .releaseStrategy(g -> g.size() == (Integer) g.getOne().getHeaders()
>                 .get(IntegrationMessageHeaderAccessor.SEQUENCE_SIZE))
>             .outputProcessor(g -> new BatchResult(g.getMessages().stream()
>                 .map(m -> (OrderResult) m.getPayload()).toList()))
>             .groupTimeout(5000L)              // частичный результат через 5 сек
>             .sendPartialResultOnExpiry(true))
>         .channel("batchOutput")
>         .get();
> }
> ```
>
> **Когда применять:** scatter-gather (распарать запросы, собрать ответы), batch-обработка с параллелизмом per-item, sequencer (восстановить порядок out-of-order сообщений), reduce-step после parallel pipeline.
>
> **Подводные камни:**
> - **Memory leak без `expireGroupsUponCompletion`**: после release MessageGroup остаётся в store как `completed`. Установить `expireGroupsUponCompletion(true)` или настроить `MessageGroupStoreReaper`.
> - **Persistent store для надёжности**: `SimpleMessageStore` теряет группы при рестарте. Для durability — `JdbcMessageStore`/`RedisMessageStore`/`MongoDbMessageStore`.
> - **`groupTimeout` создаёт `ScheduledFuture` на каждое первое сообщение группы** — на high-throughput надо настраивать `TaskScheduler` pool.
> - **Order не гарантирован после параллельной обработки**: если порядок важен, использовать `ResequencingMessageHandler`.
>
> **Связанные вопросы:** [[Q5]] — DSL для split/aggregate; [[Q9]] — persistence через MessageStore; [[Q1]] — Splitter и Aggregator в EIP-каталоге.
>
> ---
>
> #### C) Aggregator работает только in-memory и теряет данные при рестарте — это by design, для надёжности используют только Kafka — ❌ Неверно
>
> **Что на самом деле:** Aggregator работает с любым `MessageGroupStore`. In-memory `SimpleMessageStore` — лишь default. Для durability: `JdbcMessageStore` (PostgreSQL/Oracle с DDL `INT_MESSAGE_GROUP`), `RedisMessageStore`, `MongoDbMessageStore`, `HazelcastMessageStore`. После рестарта группы восстанавливаются из store, и поток продолжает накопление.
>
> **Откуда путаница:** Kafka действительно делает stateful aggregation (через RocksDB state store), но и Spring Integration Aggregator может быть persistent.
>
> **Если бы это было правдой:** не было бы classом `JdbcMessageStore` в `spring-integration-jdbc`. На практике это рабочий patterndля enterprise-приложений.
>
> ---
>
> #### D) Splitter и Aggregator не связаны — нужно вручную проставлять correlationId в заголовки сообщений между ними — ❌ Неверно
>
> **Что на самом деле:** Splitter автоматически устанавливает заголовки `CORRELATION_ID` (= ID исходного сообщения), `SEQUENCE_SIZE` (= количество split-ов), `SEQUENCE_NUMBER` (1, 2, 3, ...). Aggregator по умолчанию читает их через `HeaderAttributeCorrelationStrategy(CORRELATION_ID)` и `SequenceSizeReleaseStrategy`. Это «out of the box» pair.
>
> **Откуда путаница:** в Apache Camel требуется явная конфигурация `aggregationStrategy`. В Spring Integration работает «магически» через стандартные заголовки.
>
> **Если бы это было правдой:** простой `.split().handle().aggregate()` не работал бы без явных заголовков. На практике именно так и пишут — без явной корреляции.

## Q7. Как обрабатывать ошибки?

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
>
> **Вопрос:** Куда летит exception из handler-а, если в потоке не настроен ни `errorChannel`, ни `RequestHandlerRetryAdvice`? Какие три уровня обработки ошибок есть?
>
> ---
>
> #### A) Spring Integration ловит exception и кладёт в DLQ автоматически — никакой настройки не нужно — ❌ Неверно
>
> **Что на самом деле:** **никакой автоматической DLQ нет**. Spring Integration — это процессинговый фреймворк, не брокер. DLQ — это концепция Kafka/RabbitMQ, и реализуется она через явный send в DLT-topic при ошибке (Spring Kafka делает это через `DeadLetterPublishingRecoverer`). В Spring Integration без явной настройки exception летит наверх по стеку (sync) или в global `errorChannel` (если канал async).
>
> **Откуда путаница:** Spring Kafka действительно имеет default `DefaultErrorHandler` с retry + DLT. Spring Integration более низкоуровневый.
>
> **Если бы это было правдой:** не было бы Q7-вопросов про error-handling вообще. На практике любой production-flow требует explicit error handling.
>
> ---
>
> #### B) Три уровня: (1) глобальный `errorChannel` для async exception-ов, (2) локальный `errorChannel` в `.handle(svc, e -> e.errorChannel(...))`, (3) `RequestHandlerRetryAdvice` для retry перед уходом в errorChannel — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Integration предоставляет иерархическую обработку ошибок:
>
> **Уровень 1 — Глобальный errorChannel.** Spring Integration автоматически регистрирует bean `errorChannel` (`PublishSubscribeChannel`). Для **синхронных** flow на `DirectChannel` exception летит обратно в `send()`-caller (или Gateway-вызов). Для **асинхронных** flow (`QueueChannel`/`ExecutorChannel`/`PublishSubscribeChannel` с TaskExecutor) exception оборачивается в `ErrorMessage` и публикуется в `errorChannel`. Если на этом канале нет подписчика — `MessagingExceptionHandler` логирует WARN. Регистрируем `@ServiceActivator(inputChannel = "errorChannel")` для централизованного хендлинга — отправка в DLQ, алертинг, метрики.
>
> **Уровень 2 — Локальный errorChannel per-endpoint.** В DSL `.handle(svc, e -> e.errorChannel("orderErrors"))` или аннотацией `@ServiceActivator(errorChannel = "orderErrors")`. Это override глобального — exception из этого конкретного handler-а идёт в named channel, не в global. Полезно для feature-специфичной обработки.
>
> **Уровень 3 — RequestHandlerRetryAdvice (Spring Retry).** Оборачивает handler в proxy с retry-логикой. До того как exception улетит в errorChannel, делается N попыток (exponential backoff, retryOn classes, recoveryCallback). Если все попытки исчерпаны — exception идёт по обычному пути (errorChannel или вверх).
>
> Также есть `ExpressionEvaluatingRequestHandlerAdvice` для conditional advice и `RequestHandlerCircuitBreakerAdvice` (circuit breaker).
>
> **Пример:**
> ```java
> @Configuration
> public class ErrorHandlingConfig {
>     // Уровень 1: глобальный errorChannel
>     @ServiceActivator(inputChannel = "errorChannel")
>     public void onError(ErrorMessage error, @Header(MessageHeaders.ID) UUID id) {
>         Throwable cause = error.getPayload().getCause();
>         Message<?> failed = error.getPayload().getFailedMessage();
>         meterRegistry.counter("integration.errors", "type", cause.getClass().getSimpleName()).increment();
>         deadLetterPublisher.send(failed.getPayload(), cause.getMessage());
>     }
>
>     // Уровень 3: retry advice
>     @Bean
>     public RequestHandlerRetryAdvice retryAdvice() {
>         var advice = new RequestHandlerRetryAdvice();
>         advice.setRetryTemplate(RetryTemplate.builder()
>             .maxAttempts(3)
>             .exponentialBackoff(500, 2.0, 5000)
>             .retryOn(TransientException.class)
>             .build());
>         advice.setRecoveryCallback(ctx -> {  // вызывается после исчерпания retry
>             log.error("All retries failed", ctx.getLastThrowable());
>             return null;
>         });
>         return advice;
>     }
>
>     // Уровень 2 + 3: локальный errorChannel + retry
>     @Bean
>     public IntegrationFlow orderFlow(OrderService svc, RequestHandlerRetryAdvice retry) {
>         return IntegrationFlow.from("ordersInput")
>             .handle(svc, "process",
>                 e -> e.advice(retry).errorChannel("orderErrors"))
>             .get();
>     }
> }
> ```
>
> **Когда применять:** уровень 1 — обязательно, центральный handler для observability. Уровень 2 — когда нужна feature-специфичная обработка (например, разные DLQ для разных типов сообщений). Уровень 3 — transient errors (network, временно недоступный сервис).
>
> **Подводные камни:**
> - **`DirectChannel` пробрасывает exception синхронно** — `errorChannel` не сработает для sync flow, exception летит в Gateway-вызов. Чтобы попадало в errorChannel — async канал.
> - **`@Transactional` rollback при exception** случается до того, как сообщение попадает в errorChannel — DLQ-получатель не увидит rollback-нутые изменения.
> - **`recoveryCallback` возвращает значение, которое становится reply** — `null` ломает downstream, который ждёт payload.
> - **Retry на `DirectChannel`-flow** блокирует caller на N\*backoff времени — может выбить HTTP timeout наверху.
>
> **Связанные вопросы:** [[Q9]] — транзакции и rollback при ошибке; [[Q10]] — тестирование error-flow; [[Q3]] — Service Activator как точка отказа.
>
> ---
>
> #### C) `errorChannel` обрабатывает только `IntegrationException`-ы — обычные RuntimeException туда не попадают — ❌ Неверно
>
> **Что на самом деле:** Spring Integration **оборачивает любой Throwable** из handler-а в `MessagingException` и публикует в `errorChannel` как `ErrorMessage(MessagingException)`. Внутри payload-а — `getCause()` возвращает оригинальный `Throwable` (любой тип), `getFailedMessage()` — оригинальное сообщение. Не нужно наследовать `IntegrationException`.
>
> **Откуда путаница:** название `errorChannel` ассоциируется с framework-specific exceptions.
>
> **Если бы это было правдой:** обработка обычных бизнес-исключений (`InvalidOrderException`) не работала бы. На практике все exceptions попадают в errorChannel.
>
> ---
>
> #### D) `RequestHandlerRetryAdvice` — это AOP-аспект, который применяется к `@Service`-классам, помеченным `@Retryable` — ❌ Неверно
>
> **Что на самом деле:** `RequestHandlerRetryAdvice` — это **`HandleMessageAdvice`**, реализация интерфейса `MethodInterceptor`, которая применяется к `MessageHandler`-у (не к `@Service`), причём только в контексте `IntegrationFlow`. Это специфика Spring Integration, не общий Spring AOP. `@Retryable` (Spring Retry annotation) — отдельный механизм, применимый к любому Spring-bean.
>
> **Откуда путаница:** оба используют `RetryTemplate` под капотом, но проксируют разные точки.
>
> **Если бы это было правдой:** retry в integration-flow и retry в обычном сервисе работали бы одинаково. На практике в flow используют именно `RequestHandlerRetryAdvice`.

## Q8. Как подключить Kafka через Spring Integration?

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
>
> **Вопрос:** Когда выбирать `KafkaMessageDrivenChannelAdapter` (event-driven) против `KafkaInboundChannelAdapter` (polling)? В чём принципиальная разница?
>
> ---
>
> #### A) Это синонимы — оба под капотом используют `KafkaConsumer.poll()` — ❌ Неверно
>
> **Что на самом деле:** это **разные** адаптеры с разной моделью консьюминга. `KafkaMessageDrivenChannelAdapter` оборачивает `MessageListenerContainer` (тот же, что у `@KafkaListener`) — он сам в фоне крутит `poll()` и push-ит сообщения в integration-channel сразу как они приходят. `KafkaInboundChannelAdapter` (Source-based) — `PollableChannel`-source, который вызывает `poll()` только когда integration-poller (Pollers.fixedDelay) триггерится. Разница: push vs pull.
>
> **Откуда путаница:** оба «inbound» Kafka adapters, разница не очевидна из имён.
>
> **Если бы это было правдой:** не было бы двух разных классов. На практике у них разные performance-характеристики.
>
> ---
>
> #### B) MessageDriven — push-модель через `MessageListenerContainer` (как `@KafkaListener`); Inbound (Source) — pull-модель через integration-Poller, лучше для batch и точечного контроля throughput — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Kafka offers two consumption styles в Spring Integration:
>
> **1. `KafkaMessageDrivenChannelAdapter` (рекомендуемый).** Под капотом — `ConcurrentMessageListenerContainer` (тот же, что используется Spring Kafka `@KafkaListener`). Container управляет жизненным циклом Consumer-а: запускает background-поток (потоки), вызывает `consumer.poll()` в цикле, и для каждого `ConsumerRecord` диспатчит в `MessageListener.onMessage()` → внутри пушит в `outputChannel`. Поддерживает `ListenerMode.record` (один record → одно сообщение) и `ListenerMode.batch` (батч → одно сообщение со списком). Auto-commit или manual ack через `Acknowledgment`. Это event-driven подход: как только Kafka вернула batch — мы сразу обрабатываем.
>
> **2. `KafkaInboundChannelAdapter` (source-based, polling).** Реализует `MessageSource<ConsumerRecord>`, который надо poll-ить через `<int:poller>` или DSL `.poller(Pollers.fixedDelay(1000))`. На каждый tick poller-а вызывается `receive()` → внутри `consumer.poll(timeout)` → возвращается одно сообщение (или null). Это даёт точный контроль над throughput (например, «не больше 100 msg/sec»), но добавляет latency (=poller interval) и не использует Kafka batch-fetch эффективно.
>
> Outbound: `KafkaProducerMessageHandler` принимает Spring `Message`, конвертирует в `ProducerRecord` и отправляет через `KafkaTemplate`. Поддерживает SpEL для topic/partition/key, `KafkaSendCallback` для async confirmation, transactional sender.
>
> **Пример (event-driven, рекомендуемый):**
> ```java
> @Bean
> public KafkaMessageDrivenChannelAdapter<String, String> ordersInbound(
>         ConsumerFactory<String, String> consumerFactory) {
>     var props = new ContainerProperties("orders-topic");
>     props.setGroupId("order-processor");
>     props.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
>     var container = new ConcurrentMessageListenerContainer<>(consumerFactory, props);
>     container.setConcurrency(3);  // 3 потока = 3 partitions
>     var adapter = new KafkaMessageDrivenChannelAdapter<>(container, ListenerMode.record);
>     adapter.setOutputChannelName("ordersChannel");
>     return adapter;
> }
>
> @Bean
> public IntegrationFlow kafkaFlow(KafkaMessageDrivenChannelAdapter<String,String> in,
>                                   KafkaTemplate<String,String> template,
>                                   OrderService service) {
>     return IntegrationFlow.from(in)
>         .transform(Transformers.fromJson(Order.class))
>         .handle(Order.class, (o, h) -> {
>             OrderResult result = service.process(o);
>             ((Acknowledgment) h.get(KafkaHeaders.ACKNOWLEDGMENT)).acknowledge();
>             return result;
>         })
>         .handle(Kafka.outboundChannelAdapter(template).topic("order-results"))
>         .get();
> }
> ```
>
> **Когда применять:**
> - `MessageDriven` — 95% случаев: realtime processing, low latency, high throughput.
> - `Inbound/Source` — точное rate-limiting, batch-обработка с явным расписанием (раз в N минут), legacy интеграции с фиксированным окном poll.
>
> **Подводные камни:**
> - **Concurrency vs partitions**: `setConcurrency(N)` создаёт N потоков, но реальный параллелизм ограничен количеством partitions в topic-е.
> - **ListenerMode.batch + integration**: batch-сообщение в Spring Integration — это `Message<List<ConsumerRecord>>` или `Message<List<Payload>>`. Downstream handler должен уметь работать со списком.
> - **Auto-commit риски**: при `AckMode.RECORD/BATCH/TIME` сообщение коммитится **после** успешной обработки в Spring Integration. При async-flow (`QueueChannel`) commit может произойти **до** реального завершения handler-а — потеря сообщения при crash. Использовать `MANUAL_IMMEDIATE` + явный `acknowledge()`.
> - **No DLQ автоматически**: `errorChannel` обработает exception, но send в DLT-topic надо делать вручную (через outbound adapter в errorChannel-flow).
>
> **Связанные вопросы:** [[Q1]] — Kafka adapter как реализация Channel Adapter pattern; [[Q9]] — transactional Kafka producer/consumer; [[Q7]] — error handling для Kafka сообщений; [[Q11]] — Spring Integration vs Spring Kafka — когда что.
>
> ---
>
> #### C) Kafka в Spring Integration работает только через XML-конфигурацию `<int-kafka:inbound-channel-adapter>` — Java DSL не поддерживается — ❌ Неверно
>
> **Что на самом деле:** Java DSL полностью поддерживает Kafka через `Kafka.messageDrivenChannelAdapter()`, `Kafka.inboundChannelAdapter()`, `Kafka.outboundChannelAdapter()` factory методы из `spring-integration-kafka`. Эти методы возвращают builders с типобезопасной конфигурацией.
>
> **Откуда путаница:** в старой документации (до 4.0) использовался XML.
>
> **Если бы это было правдой:** все современные туториалы Spring Integration были бы на XML. На практике DSL — стандарт.
>
> ---
>
> #### D) `KafkaMessageDrivenChannelAdapter` не поддерживает manual offset commit — это работает только с `@KafkaListener` — ❌ Неверно
>
> **Что на самом деле:** `KafkaMessageDrivenChannelAdapter` использует тот же `MessageListenerContainer`, что и `@KafkaListener`, и поддерживает все `AckMode`-ы, включая `MANUAL` / `MANUAL_IMMEDIATE`. `Acknowledgment` пробрасывается в integration message через заголовок `KafkaHeaders.ACKNOWLEDGMENT`. В handler-е делаем `headers.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class).acknowledge()`.
>
> **Откуда путаница:** в простых примерах manual commit редко показывается.
>
> **Если бы это было правдой:** для critical-сообщений пришлось бы городить отдельный flow на `@KafkaListener`. На практике manual commit работает прозрачно через заголовок.

## Q9. Как транзакции работают в Spring Integration?

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
>
> **Вопрос:** Почему `@Transactional` метод вызывающий `gateway.placeOrder()` НЕ покрывает обработку в QueueChannel, и какое решение в production?
>
> ---
>
> #### A) @Transactional работает только с JDBC — для Spring Integration нужен @MessageTransactional — ❌ Неверно
>
> **Что на самом деле:** аннотации `@MessageTransactional` не существует. `@Transactional` работает с любым `PlatformTransactionManager`. Проблема не в JDBC, а в **передаче транзакционного контекста через async boundary**.
>
> **Откуда путаница:** Spring имеет много specialized annotations (`@JmsListener`, `@KafkaListener` с transactional context). Можно подумать что Integration тоже имеет.
>
> **Если бы это было правдой:** в Spring Integration docs было бы упоминание `@MessageTransactional`. Реально это `@Transactional` + правильная конфигурация channels.
>
> ---
>
> #### B) `QueueChannel` имеет внутренний BlockingQueue + отдельный thread для обработки → транзакция вызывающего треда не пробрасывается. Решение: `DirectChannel` (sync, same thread) или явная конфигурация `Poller.transactional()` для async channels — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> **Корень проблемы — thread boundary:**
> - `DirectChannel.send()` — synchronous, тот же thread, тот же transaction context.
> - `QueueChannel.send()` — кладёт в `BlockingQueue`, возвращается; **poller thread** забирает и обрабатывает в **своей** транзакции (или вне транзакции вообще).
>
> `@Transactional` использует `TransactionSynchronizationManager` с `ThreadLocal` — context живёт только в потоке-обладателе. Передача между threads требует явной propagation.
>
> **Решения:**
>
> 1. **DirectChannel (sync flow)** — простейшее. Транзакция покрывает все handlers до конца.
>
> 2. **QueueChannel + Poller с транзакциями** — для async с durability:
>    ```java
>    IntegrationFlow flow = IntegrationFlow
>        .from(MessageChannels.queue("queueChannel", 1000),
>            c -> c.poller(Pollers.fixedDelay(1000)
>                .transactional(transactionManager)            // poller starts new tx
>                .maxMessagesPerPoll(10)))                      // batch обработка в одной tx
>        .handle(orderService::process)
>        .get();
>    ```
>    Здесь каждый poll создаёт новую транзакцию для batch'а messages.
>
> 3. **TransactionSynchronizationFactory** — для propagation custom logic после commit/rollback.
>
> **Пример (transactional poller + retry):**
> ```java
> @Bean
> public IntegrationFlow ordersFlow(PlatformTransactionManager txManager) {
>     return IntegrationFlow
>         .from(MessageChannels.queue("orders", 5000))
>         .handle("orderService", "process",
>             e -> e.poller(Pollers.fixedRate(100)
>                 .transactional(txManager)
>                 .advice(retryAdvice())
>                 .maxMessagesPerPoll(50)
>                 .errorChannel("dlqChannel")))
>         .get();
> }
>
> @Bean
> public RequestHandlerRetryAdvice retryAdvice() {
>     RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
>     advice.setRetryTemplate(RetryTemplate.builder()
>         .maxAttempts(3).exponentialBackoff(100, 2, 10000).build());
>     advice.setRecoveryCallback(ctx ->
>         errorChannel.send(MessageBuilder.withPayload(ctx.getLastThrowable()).build()));
>     return advice;
> }
> ```
>
> **Когда применять:**
> - **DirectChannel + @Transactional**: simple synchronous workflows, REST → business logic → DB.
> - **QueueChannel + transactional poller**: high-throughput async processing с durability requirements.
> - **ExecutorChannel + tx synchronization**: parallel processing с per-task transactions.
>
> **Подводные камни:**
> - **Без poller transaction message может потеряться**: process crashes after dequeue, before DB commit.
> - **Long transactions hurt throughput**: `maxMessagesPerPoll: 50` баланс между batch efficiency и lock time.
> - **`ChainedKafkaTransactionManager`** для Kafka + JDBC tx atomically.
> - **PublishSubscribeChannel** не propagates transaction между subscribers — каждый в своей tx.
>
> **Связанные вопросы:** [[Q3]] — MessageChannel types; [[Q7]] — error handling для tx rollback; [[Q8]] — Kafka adapter с transactions.
>
> ---
>
> #### C) Spring Integration не поддерживает транзакции — для них надо использовать Spring Batch — ❌ Неверно
>
> **Что на самом деле:** Spring Integration **полностью** поддерживает транзакции через `Poller.transactional()`, `TransactionSynchronizationFactory`, `ChainedTransactionManager`. Spring Batch — orthogonal framework для batch jobs (job/step model), не transaction wrapper.
>
> **Откуда путаница:** Spring Batch имеет explicit `transactionManager` в step config. Можно подумать что только Batch это умеет.
>
> **Если бы это было правдой:** для transactional message processing команды переходили бы с Integration на Batch, что не наблюдается.
>
> ---
>
> #### D) Транзакции автоматически работают везде в Spring Integration — никакой конфигурации не нужно — ❌ Неверно
>
> **Что на самом деле:** автоматическая propagation работает только в **synchronous channels** (DirectChannel). Для **async channels** (QueueChannel, ExecutorChannel) требуется явная конфигурация poller'а или `TaskExecutor` с `TransactionSynchronizationFactory`.
>
> **Откуда путаница:** Spring славится «just works» поведением. Но transaction propagation across threads — fundamentally manual.
>
> **Если бы это было правдой:** не было бы упоминания «transaction boundary» в Spring Integration documentation.

## Q10. Как тестировать Spring Integration потоки?

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
>
> **Вопрос:** Какие специальные testing utilities предоставляет `spring-integration-test`, и почему они лучше чем обычный `@SpringBootTest`?
>
> ---
>
> #### A) Обычный @SpringBootTest достаточен — не нужны special test utilities — ❌ Неверно
>
> **Что на самом деле:** обычный @SpringBootTest стартует контекст с реальными потоками, но **не даёт hooks для substitution handlers/endpoints**. Без `@SpringIntegrationTest` нельзя подменить middle-handler в flow без модификации production кода.
>
> Также обычный test не имеет access к internal `IntegrationFlowContext` для dynamic flow registration в тестах.
>
> **Если бы это было правдой:** не было бы artifact `spring-integration-test`. Реально это отдельная dependency со специфичными утилитами.
>
> ---
>
> #### B) `@SpringIntegrationTest` + `MockIntegrationContext.substituteMessageHandlerFor()` для замены handlers в running context; `IntegrationFlowContext.registration()` для dynamic flows в тестах; `MessageChannel.receive(timeout)` для assertion on async results — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Integration Test предоставляет:
>
> 1. **`@SpringIntegrationTest(noAutoStartup)`** — отключает auto-start handlers для controlled testing.
>
> 2. **`MockIntegrationContext`** — substitute handlers без модификации production:
>    ```java
>    @SpringIntegrationTest
>    @SpringBootTest
>    class OrderFlowTest {
>        @Autowired MockIntegrationContext mockContext;
>        @Autowired OrderGateway gateway;
>        @Autowired @Qualifier("ordersOutput") PollableChannel output;
>
>        @Test
>        void shouldFailoverOnServiceError() {
>            mockContext.substituteMessageHandlerFor("orderHandler",
>                m -> { throw new RuntimeException("downstream unavailable"); });
>
>            gateway.placeOrder(order);
>
>            Message<?> err = errorChannel.receive(5000);
>            assertThat(err.getPayload()).isInstanceOf(MessagingException.class);
>        }
>    }
>    ```
>
> 3. **`IntegrationFlowContext`** — dynamic registration:
>    ```java
>    IntegrationFlowRegistration reg = flowContext.registration(
>        IntegrationFlow.from(c -> c.gateway(MyGateway.class))
>            .handle(svc::process)
>            .get())
>        .register();
>    // tests with isolated flow
>    reg.destroy();
>    ```
>
> 4. **`PollableChannel.receive(timeout)`** — async assertion с timeout.
>
> **Когда применять:**
> - **Integration tests** Spring Integration flows: assertion того что message доходит до output channel.
> - **Error scenarios**: substitute handler that throws → assertion errorChannel поведения.
> - **Dynamic flow tests**: per-test flow registration без context pollution.
>
> **Подводные камни:**
> - **Async assertions без timeout** — flaky tests. Всегда `receive(5000)` с meaningful assertion.
> - **`@DirtiesContext` важен**: substituted handlers persist в context — между тестами нужна изоляция.
> - **GenericMessage vs ErrorMessage**: error channel получает `ErrorMessage` (с `MessagingException` payload), не raw exception.
> - **Не путать с `@MockBean`**: substitution на handler level, не bean level — flow остаётся wired.
>
> **Связанные вопросы:** [[Q9]] — transactional flow testing; [[Q3]] — channel types; [[Q15]] — мониторинг flows.
>
> ---
>
> #### C) Можно тестировать только через actual brokers (Testcontainers) — нет other way — ❌ Неверно
>
> **Что на самом деле:** для тестирования **business logic** в flow не нужны real brokers. `MockIntegrationContext` substitutes handlers без external dependencies. Testcontainers нужен для integration tests с real adapters (Kafka/RabbitMQ).
>
> Это две разные testing strategies:
> - **Unit-level flow tests**: substitute handlers, mock channels — fast.
> - **Integration tests**: real adapters через Testcontainers — slow but full fidelity.
>
> **Если бы это было правдой:** unit tests были бы непрактичны — каждый тест 10-30s startup.
>
> ---
>
> #### D) `@SpringIntegrationTest` deprecated — заменён JUnit 5 extensions — ❌ Неверно
>
> **Что на самом деле:** `@SpringIntegrationTest` — это **сам и есть** JUnit 5 extension (через `@ExtendWith` под капотом). Не deprecated, активно поддерживается в Spring Integration 6.x.
>
> **Если бы это было правдой:** в release notes Spring Integration 6 было бы deprecation notice.

## Q11. Чем Spring Integration отличается от Apache Camel?

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
>
> **Вопрос:** Когда выбирать Spring Integration vs Apache Camel в новом проекте?
>
> ---
>
> #### A) Spring Integration лучше при существующем Spring stack — меньше учить, native dependency injection, fewer libraries — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Trade-offs:
>
> | Аспект | Spring Integration | Apache Camel |
> |---|---|---|
> | Spring ecosystem | Native | Через camel-spring-boot |
> | Коннекторы | ~30 | 200+ (включая SAP, Salesforce, AS400) |
> | Learning curve | Низкая (для Spring devs) | Средняя (свой DSL) |
> | DSL options | Java DSL | Java/Scala/XML/YAML/Kotlin DSL |
> | Test support | `@SpringIntegrationTest` | `CamelTestSupport` |
> | Use case | Internal apps, ESB-light | Multi-protocol integration platforms |
>
> **Выбор:**
> - **Spring Integration**: проект на Spring, нужно ≤30 коннекторов, команда знает Spring.
> - **Apache Camel**: enterprise integration с многими legacy systems (SAP, AS400, IBM MQ), нужен specialized DSL (e.g., RouteBuilder), Camel-based products like Apache ServiceMix / Karaf / Fuse.
>
> **Пример migration scenario:**
> ```java
> // Spring Integration — для file → Kafka pipeline
> @Bean
> public IntegrationFlow fileToKafka() {
>     return IntegrationFlow.from(Files.inboundAdapter(new File("/inbox")))
>         .transform(File.class, this::parse)
>         .handle(Kafka.outboundChannelAdapter(producerFactory).topic("orders"))
>         .get();
> }
>
> // Camel — для SAP IDoc → JMS pipeline
> from("sap-idoc-server:server:idoc?type=ORDERS05")
>     .convertBodyTo(OrderDto.class)
>     .to("activemq:queue:orders");
> ```
>
> **Когда применять Camel вместо Integration:**
> - Нужны коннекторы которых нет в Integration (SAP, AS400, FHIR).
> - Команда уже работает с Camel в других проектах.
> - Cloud Camel K (serverless integration on Kubernetes).
>
> **Подводные камни:**
> - **«Camel = больше = лучше»** ложно. Если хватает 5 коннекторов из Integration — Camel overkill.
> - **Spring Cloud Stream** — ещё одна альтернатива для simple pub/sub поверх Kafka/RabbitMQ.
> - **Spring Integration JDBC adapters** покрывают 90% DB integration; Camel JDBC component делает то же.
> - **Migration cost**: переход Integration → Camel = переписать flows на RouteBuilder.
>
> **Связанные вопросы:** [[Q1]] — EIP patterns как foundation; [[Q12]] — Integration vs Spring Kafka; [[Q15]] — мониторинг.
>
> ---
>
> #### B) Apache Camel всегда лучше — больше коннекторов = больше возможностей — ❌ Неверно
>
> **Что на самом деле:** «больше features = лучше» — fallacy. 200 коннекторов добавляют complexity (separate libraries, more configuration surface) даже если используется 5. Для simple integration overhead не оправдан.
>
> Реально команды на pure Spring stacks (Yandex, Avito) часто выбирают Integration именно за simplicity.
>
> ---
>
> #### C) Spring Integration уже deprecated — все мигрируют на Camel — ❌ Неверно
>
> **Что на самом деле:** Spring Integration активно поддерживается (версия 6.x в 2024, синхронизация со Spring 6 / Spring Boot 3). Migration в одну сторону не наблюдается.
>
> **Если бы это было правдой:** spring-integration-* artifacts были бы в maintenance mode.
>
> ---
>
> #### D) Camel = enterprise (платный), Spring Integration = open-source — ❌ Неверно
>
> **Что на самом деле:** оба open-source (Apache 2.0). Red Hat Fuse — коммерческая поддержка Camel, но сам Camel бесплатный. Spring Integration аналогично — open-source с commercial support от VMware/Broadcom через Spring Tanzu.
>
> **Откуда путаница:** Red Hat Fuse часто упоминается с Camel.

## Q12. Когда использовать Spring Integration vs Spring Kafka?

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
