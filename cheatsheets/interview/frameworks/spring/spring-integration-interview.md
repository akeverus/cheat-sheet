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
