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
updated: 2026-05-31
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
- [Q1. (!) Что такое Spring Integration и какие EIP-паттерны он реализует?](#q1--что-такое-spring-integration-и-какие-eip-паттерны-он-реализует)
- [Q2. (!) Какие типы MessageChannel существуют?](#q2--какие-типы-messagechannel-существуют)

**Endpoints**
- [Q3. Что такое Service Activator?](#q3-что-такое-service-activator)
- [Q4. Как работает MessagingGateway?](#q4-как-работает-messaginggateway)

**Java DSL и трансформации**
- [Q5. (!) Как устроен Java DSL?](#q5--как-устроен-java-dsl)
- [Q6. Как реализовать Splitter и Aggregator?](#q6-как-реализовать-splitter-и-aggregator)

**Обработка ошибок и интеграции**
- [Q7. Как обрабатывать ошибки?](#q7-как-обрабатывать-ошибки)
- [Q8. Как подключить Kafka через Spring Integration?](#q8-как-подключить-kafka-через-spring-integration)
- [Q9. Как транзакции работают в Spring Integration?](#q9-как-транзакции-работают-в-spring-integration)
- [Q10. Как тестировать Spring Integration потоки?](#q10-как-тестировать-spring-integration-потоки)

**Сравнение и продвинутые паттерны**
- [Q11. Чем Spring Integration отличается от Apache Camel?](#q11-чем-spring-integration-отличается-от-apache-camel)
- [Q12. Когда использовать Spring Integration vs Spring Kafka?](#q12-когда-использовать-spring-integration-vs-spring-kafka)
- [Q13. Что такое Claim Check и как его реализовать?](#q13-что-такое-claim-check-и-как-его-реализовать)
- [Q14. Как работает Enricher (Content Enricher)?](#q14-как-работает-enricher-content-enricher)
- [Q15. Как мониторить Spring Integration?](#q15-как-мониторить-spring-integration)

## Q1. (!) Что такое Spring Integration и какие EIP-паттерны он реализует?

**Spring Integration** — это реализация Enterprise Integration Patterns (EIP) из книги Hohpe & Woolf поверх Spring. Идея проста: компоненты не вызывают друг друга напрямую, а обмениваются сообщениями (`Message`) через каналы (`Channel`). За счёт этого они не знают друг о друге — между ними стоит канал, и любой компонент можно заменить или вставить новый, не трогая остальные.

Это и есть суть EIP: типовые «кирпичики» обработки сообщений, из которых собирается интеграционный конвейер.

Ключевые паттерны, которые даёт Spring Integration:
- **Message Channel** — труба, по которой идут сообщения между компонентами.
- **Message Router** — направляет сообщение в нужный канал по его содержимому (Content-Based Router).
- **Message Transformer** — меняет формат сообщения (например JSON → доменный объект).
- **Message Filter** — пропускает дальше только подходящие сообщения, остальные отбрасывает.
- **Splitter** — разбивает одно сообщение на N (например список заказов → отдельные заказы).
- **Aggregator** — обратная операция: собирает N сообщений в одно по корреляционному ключу.
- **Service Activator** — точка, где сообщение передаётся в бизнес-метод.
- **Gateway** скрывает обмен сообщениями за вызовом метода. Поддерживает одностороннюю отправку и request/reply (Q4).

Компоненты Spring Integration регистрируются в контейнере. Каналы связывают endpoints в поток.

```java
@Configuration
@EnableIntegration
public class IntegrationConfig {}
```

## Q2. (!) Какие типы MessageChannel существуют?

`MessageChannel` определяет, **как и в каком потоке** сообщение доходит от отправителя до обработчика: синхронно или асинхронно, точка-в-точку или всем сразу, с буфером или без. Тип канала — главное архитектурное решение в потоке, потому что от него зависят транзакционность, развязка потоков и поведение под нагрузкой.

| Канал | Поведение | Применение |
|-------|-----------|------------|
| `DirectChannel` | Синхронный, в потоке отправителя | Последовательный вызов обработчика в текущем потоке |
| `PublishSubscribeChannel` | Broadcast всем подписчикам | Уведомления, events |
| `QueueChannel` | In-memory FIFO (требует poller) | Буферизация, развязка потоков |
| `PriorityChannel` | Очередь с приоритетом | Приоритизация сообщений |
| `ExecutorChannel` | Асинхронный (TaskExecutor) | Параллельная обработка |
| `FluxMessageChannel` | Реактивный (Reactor) | Bridge к Spring WebFlux |

При **point-to-point** доставке сообщение предназначено одному получателю (`DirectChannel`, `QueueChannel`, `ExecutorChannel`). При включённом failover диспетчер может попробовать другого обработчика после сбоя. При **publish-subscribe** доставке (`PublishSubscribeChannel`) канал рассылает сообщение подписчикам. Каналы с очередью (`QueueChannel`, `PriorityChannel`) развязывают отправителя и получателя по времени, но требуют poller, который вынимает из них сообщения.

```java
@Bean public MessageChannel ordersInput() { return new DirectChannel(); }
@Bean public MessageChannel ordersQueue() { return new QueueChannel(100); }
@Bean public MessageChannel broadcasts() { return new PublishSubscribeChannel(); }
```

Неявные каналы между обычными шагами Java DSL обычно являются `DirectChannel`. Для императивной транзакции важны поток выполнения, общий транзакционный ресурс и настройки propagation. Например, `PublishSubscribeChannel` без executor тоже вызывает подписчиков в потоке отправителя. При передаче работы другому потоку транзакция отправителя туда автоматически не переносится (Q9).

## Q3. Что такое Service Activator?

```text
В канале ordersInput приходит Order. Бизнес-метод OrderService.process(Order)
возвращает OrderResult. Результат должен попасть в ordersOutput.
Как связать каналы с существующим методом сервиса?
```

**Service Activator** — это endpoint, который соединяет канал с обычным методом бизнес-компонента. Он берёт сообщение из `inputChannel`, разворачивает его в payload, передаёт в ваш метод, а возвращённое значение заворачивает обратно в `Message` и кладёт в `outputChannel`. По сути это «точка входа» доменной логики в интеграционный поток — благодаря ему бизнес-код остаётся обычным Java-методом и не зависит от Spring Integration.

**Три способа объявить:**
- через `@ServiceActivator` на методе — самый частый вариант;
- через `.handle(...)` в Java DSL;
- через бин `MessageHandler`, если нужен прямой доступ к `Message` и заголовкам.

Если `outputChannel` не задан, ответ отправится в канал из заголовка `replyChannel` (его проставляет, например, Gateway). Если метод возвращает `void`, поток на этом endpoint завершается.

```java
// Через аннотацию
@ServiceActivator(inputChannel = "ordersInput", outputChannel = "ordersOutput")
public OrderResult processOrder(Order order) {
    return orderService.process(order);
}

// Альтернативная конфигурация через Java DSL
IntegrationFlow flow = IntegrationFlow
    .from("ordersInput")
    .handle(Order.class, (order, headers) -> orderService.process(order))
    .channel("ordersOutput")
    .get();

// Односторонний вариант с MessageHandler
@ServiceActivator(inputChannel = "ordersInput")
@Bean
public MessageHandler orderHandler(OrderService service) {
    return message -> service.process((Order) message.getPayload());
}
```

## Q4. Как работает MessagingGateway?

**Gateway** — это интерфейс, который вы только описываете, а реализацию Spring Integration генерирует за вас (через прокси). Его задача — спрятать messaging за обычным Java-вызовом: код, который дёргает `gateway.placeOrder(order)`, не подозревает, что под капотом аргумент заворачивается в `Message`, уходит в `requestChannel`, проходит весь поток, а ответ из `replyChannel` распаковывается обратно в возвращаемое значение.

Так Gateway становится границей между «обычным» приложением и интеграционным слоем: на входе в поток ставят Gateway, на выходе из бизнес-логики — Service Activator (Q3).

**Поведение метода зависит от сигнатуры:**
- Обычный возвращаемый тип задаёт request/reply. Вызывающий поток ждёт ответ.
- `void` задаёт одностороннюю отправку. Ответа нет, но при `DirectChannel` обработчик выполняется в потоке вызывающего до возврата из метода. При `QueueChannel` с poller отправка помещает сообщение в очередь, а обработчик вызывается отдельно.
- Для поддерживаемого `Future` или `CompletableFuture` gateway может выполнять вызов через `AsyncTaskExecutor`. При отключённом async executor downstream сам должен предоставить значение нужного типа. `Mono` задаёт реактивный контракт с отложенным выполнением на подписке. Выбор потока зависит от конфигурации цепочки.

[Messaging Gateways, Spring Integration 6.5](https://docs.spring.io/spring-integration/reference/6.5/gateway.html)

Заголовки сообщения задаются через `@Header`, payload — через `@Payload`.

```java
@MessagingGateway(defaultRequestChannel = "ordersInput",
                   defaultReplyChannel = "ordersOutput")
public interface OrderGateway {
    OrderResult placeOrder(Order order);

    // Тип канала orderSubmissions не указан. Что гарантирует сигнатура submitOrder?
    @Gateway(requestChannel = "orderSubmissions")
    void submitOrder(Order order);

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

## Q5. (!) Как устроен Java DSL?

```text
Источник: Kafka-сообщение с JSON заказа.
У заказов с суммой <= 0 получатель rejectedOrders.
Заказы с положительной суммой обрабатываются по-разному для EXPRESS и NORMAL.
Как описать этот поток в конфигурации Spring Integration?
```

**Java DSL** — это fluent-способ описать весь интеграционный поток одной цепочкой методов в одном `@Bean`, вместо россыпи отдельных бинов с аннотациями и явно проименованных каналов. Каждый вызов в цепочке (`.transform()`, `.filter()`, `.route()`, …) добавляет в поток очередной EIP-паттерн, а каналы между ними Spring создаёт сам, «по месту» — вручную их объявлять не нужно.

Главное преимущество — **поток читается сверху вниз как последовательность шагов**, а не собирается в голове из разбросанных по конфигу `@ServiceActivator` и `inputChannel`/`outputChannel`. Точка входа — `IntegrationFlow.from(...)` (канал или inbound-адаптер), завершение цепочки — `.get()`, который и возвращает готовый `IntegrationFlow`.

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
                        .handle(Order.class, (order, headers) -> orderService.processExpress(order)))
                      .subFlowMapping("NORMAL", sf -> sf
                        .handle(Order.class, (order, headers) -> orderService.processNormal(order)))
            )
            .get();
    }
}
```

**Ключевые методы DSL и их EIP-смысл:**
- `.from()` — источник: канал или inbound-адаптер;
- `.transform()` — преобразование payload (Transformer);
- `.filter()` — пропустить/отбросить сообщение (Filter);
- `.handle()` — вызвать бизнес-логику (Service Activator);
- `.route()` — развести по подпотокам по содержимому (Router);
- `.split()` / `.aggregate()` — разбить на N / собрать N в одно (Splitter / Aggregator);
- `.enrich()` — обогатить данными из другого канала (Content Enricher);
- `.bridge()` — соединить два канала без обработки.

## Q6. Как реализовать Splitter и Aggregator?

```text
Пакет A: заказы A1, A2, A3. Пакет B: заказы B1, B2.
Заказы обрабатываются по одному, результаты могут прийти вперемешку.
Для каждого пакета нужен общий результат. Ожидание неполной группы: до 5 секунд.
Как обработать отдельные заказы и сгруппировать результаты?
```

**Splitter** и **Aggregator** — это парные паттерны: первый разбивает одно сообщение на много, второй собирает много обратно в одно. Их часто используют вместе по схеме scatter-gather: разбили пакет на отдельные элементы, обработали каждый (возможно параллельно), затем собрали результаты обратно.

**Splitter** разбивает одно сообщение с коллекцией на несколько отдельных. Метод возвращает `List` (или `Iterable`), и каждый элемент уходит в `outputChannel` как самостоятельное сообщение. Spring при этом автоматически проставляет корреляционные заголовки (`correlationId`, `sequenceNumber`, `sequenceSize`) — позже по ним Aggregator поймёт, какие сообщения относятся к одной группе и сколько их ждать.

```java
// Аннотация
@Splitter(inputChannel = "batchOrders", outputChannel = "singleOrders")
public List<Order> split(BatchOrderRequest batch) {
    return batch.orders();
}

// DSL
.split(BatchOrderRequest.class, BatchOrderRequest::orders)
```

**Aggregator** — собирает несколько сообщений в одно по корреляционному ключу. Он держит группу в `MessageStore` (по умолчанию in-memory) и отдаёт результат, когда срабатывает условие релиза. Три ключевые настройки:
- **correlation** — по какому признаку сообщения объединяются в группу (например `headers['batchId']`);
- **release** — когда группа считается полной и пора собирать (например пришли все `batchSize` элементов);
- **timeout** определяет срок ожидания недостающих сообщений. `groupTimeout` вместе с `sendPartialResultOnExpiry` позволяет отдать неполную группу. При отключённой отправке частичного результата просроченные сообщения можно направить в discard-канал.

```java
@Aggregator(inputChannel = "orderResults", outputChannel = "batchResults")
public BatchResult aggregate(List<OrderResult> results) {
    return new BatchResult(results);
}

// DSL для сообщений с заголовками batchId и batchSize
.aggregate(aggregator -> aggregator
    .correlationExpression("headers['batchId']")
    .releaseExpression("size() == getOne().headers['batchSize']")
    .sendPartialResultOnExpiry(true)
    .groupTimeout(5000L)  // отправить частичный результат через 5с
)
```

## Q7. Как обрабатывать ошибки?

```java
@Bean
DirectChannel directInput() {
    return new DirectChannel();
}

@Bean
QueueChannel queuedInput() {
    return new QueueChannel(10);
}

@Bean
IntegrationFlow directFlow() {
    return IntegrationFlow.from("directInput")
        .handle(String.class, this::processOrder)
        .get();
}

@Bean
IntegrationFlow queuedFlow() {
    return IntegrationFlow.from("queuedInput")
        .handle(String.class, this::processOrder,
            e -> e.poller(Pollers.fixedDelay(1000)))
        .get();
}

private Object processOrder(String orderId, MessageHeaders headers) {
    throw new IllegalStateException("Order service unavailable");
}

// Два независимых запуска в Spring Integration 6.5, без gateway и retry advice.
// В каждом запуске вызывается один из методов с соответствующим бином канала.
void sendDirect(DirectChannel directInput) {
    directInput.send(new GenericMessage<>("order-1"));
}

void sendQueued(QueueChannel queuedInput) {
    queuedInput.send(new GenericMessage<>("order-1"));
}
// Где проявится ошибка каждого вызова обработчика?
```

В первом запуске `DirectChannel` вызывает обработчик в стеке `send()`. Исключение возвращается вызывающему, обычно как `MessagingException` с причиной. Во втором запуске `send()` помещает сообщение в очередь. При стандартной обработке ошибок задача poller публикует сбой как `ErrorMessage`. Сначала учитывается канал в заголовке `errorChannel` исходного сообщения, затем используется глобальный `errorChannel`. Отдельные входные компоненты, например gateway, могут настраивать собственную обработку ошибок.

`ErrorMessage` уже является сообщением. Его payload имеет тип `Throwable`. `getFailedMessage()` можно вызвать после проверки, что исключение относится к `MessagingException`. Метод может вернуть `null`.

```java
@ServiceActivator(inputChannel = "errorChannel")
public void handleError(ErrorMessage errorMessage) {
    Throwable error = errorMessage.getPayload();
    Message<?> failedMessage = error instanceof MessagingException messagingException
        ? messagingException.getFailedMessage()
        : null;
    log.error("Integration error for message: {}", failedMessage, error);
}
```

Для локального повтора на endpoint можно добавить `e.advice(retryAdvice())`. В Spring Integration 6.5 `RequestHandlerRetryAdvice` использует `org.springframework.retry.support.RetryTemplate`. В примере максимум три попытки с учётом первой и повтор только для `IOException`, в том числе вложенного. Без recovery callback исчерпанный сбой выходит из advice по правилам вызвавшего компонента.

```java
@Bean
RequestHandlerRetryAdvice retryAdvice() {
    var advice = new RequestHandlerRetryAdvice();
    advice.setRetryTemplate(org.springframework.retry.support.RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(IOException.class)
        .traversingCauses()
        .build());
    return advice;
}
```

Начиная с Spring Integration 7.0 этот advice использует Retry API из Spring Framework Core. Типы `RetryTemplate`, `RetryPolicy` и recovery callback меняются.

[Error Handling](https://docs.spring.io/spring-integration/reference/6.5/error-handling.html) · [Retry Advice](https://docs.spring.io/spring-integration/reference/6.5/handler-advice/classes.html#retry-advice) · [Миграция на 7.0](https://docs.spring.io/spring-integration/reference/changes-6.5-7.0.html)

## Q8. Как подключить Kafka через Spring Integration?

```text
orders-topic содержит JSON заказов. Приложение должно передавать входящие
записи в ordersInput, обрабатывать заказы и публиковать результаты
из ordersOutput в топик order-results. Как подключить оба направления?
```

Kafka подключается через **channel-адаптеры** — мосты между внешним транспортом и каналами Spring Integration. Их два, по направлению:
- **Inbound** (`KafkaMessageDrivenChannelAdapter`) — читает из топика и кладёт сообщения в канал потока. «Message-driven» означает, что адаптер сам слушает Kafka (внутри `MessageListenerContainer`) и проталкивает сообщения, а не вы их опрашиваете.
- **Outbound** (`KafkaProducerMessageHandler`) — берёт сообщения из канала и пишет их в топик через `KafkaTemplate`. Топик можно задать выражением (`setTopicExpression`), то есть динамически, по содержимому сообщения.

Смысл в том, что Kafka становится просто ещё одним источником/приёмником в потоке, а между адаптерами можно вставить любые EIP-паттерны — трансформацию JSON, фильтр, роутер.

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
        OrderService service) {
    return IntegrationFlow
        .from("ordersInput")
        .transform(Transformers.fromJson(Order.class))
        .handle(Order.class, (order, headers) -> service.process(order))
        .transform(Transformers.toJson())
        .channel("ordersOutput")
        .get();
}

@Bean
@ServiceActivator(inputChannel = "ordersOutput")
public KafkaProducerMessageHandler<String, String> kafkaOutboundAdapter(
        KafkaTemplate<String, String> template) {
    KafkaProducerMessageHandler<String, String> handler =
        new KafkaProducerMessageHandler<>(template);
    handler.setTopicExpression(new LiteralExpression("order-results"));
    return handler;
}
```

## Q9. Как транзакции работают в Spring Integration?

```java
@Bean
DirectChannel directOrders() {
    return new DirectChannel();
}

@Bean
QueueChannel queuedOrders() {
    return new QueueChannel(10);
}

@Bean
IntegrationFlow directOrderFlow(JdbcTemplate jdbc) {
    return IntegrationFlow.from("directOrders")
        .handle(String.class, (id, headers) -> updateOrder(jdbc, id))
        .get();
}

@Bean
IntegrationFlow queuedOrderFlow(JdbcTemplate jdbc) {
    return IntegrationFlow.from("queuedOrders")
        .handle(String.class, (id, headers) -> updateOrder(jdbc, id),
            e -> e.poller(Pollers.fixedDelay(1000)))
        .get();
}

private Object updateOrder(JdbcTemplate jdbc, String id) {
    jdbc.update("UPDATE orders SET status = 'PROCESSED' WHERE id = ?", id);
    return null;
}

// Отдельные запуски: отправитель в активной JDBC-транзакции вызывает
// send() на одном из каналов. JdbcTemplate использует тот же DataSource,
// что и менеджер транзакций. Poller выполняется в отдельном потоке.
// Какие обновления входят в транзакцию отправителя?
```

Императивная транзакция Spring привязана к потоку и ресурсу. При `DirectChannel` обработчик выполняется в стеке `send()`. `JdbcTemplate` получает соединение того же `DataSource`, привязанное к транзакции отправителя, поэтому обновление входит в неё. Если обработчик сам задаёт транзакционную границу, учитывается её propagation. Аналогично участвует в транзакции синхронный обработчик другого канала, например `PublishSubscribeChannel` без executor, при том же ресурсе и совместимой propagation.

При `QueueChannel` запись выполняет poller в другом потоке. Транзакция отправителя на него не распространяется. `ExecutorChannel` обычно тоже передаёт работу другому потоку. Проверять нужно фактическое выполнение, поскольку, например, политика `CallerRunsPolicy` может выполнить задачу в потоке отправителя.

Чтобы обернуть чтение из очереди и обработку в **отдельную** транзакцию, замените конфигурацию queued-потока на вариант с транзакционным poller.

```java
@Bean
IntegrationFlow queuedOrderFlow(JdbcTemplate jdbc, PlatformTransactionManager jdbcTx) {
    return IntegrationFlow.from("queuedOrders")
        .handle(String.class, (id, headers) -> updateOrder(jdbc, id),
            e -> e.poller(Pollers.fixedDelay(1000).transactional(jdbcTx)))
        .get();
}
```

Откат этой транзакции отменяет JDBC-изменения. Обычная очередь в памяти не становится транзакционной, и откат сам по себе не возвращает сообщение в неё.

[Transaction Support, Spring Integration 6.5](https://docs.spring.io/spring-integration/reference/6.5/transactions.html)

## Q10. Как тестировать Spring Integration потоки?

```java
@MessagingGateway(defaultRequestChannel = "ordersInput")
interface OrderStatusGateway {
    String process(String orderId);
}

@Bean
DirectChannel ordersInput() {
    return new DirectChannel();
}

@Bean
IntegrationFlow statusFlow() {
    return IntegrationFlow.from("ordersInput")
        .handle(String.class, (id, headers) -> "processed:" + id,
            endpoint -> endpoint.id("orderHandler"))
        .get();
}

// В тесте нужно вызвать gateway.process("order-1") и отдельно
// воспроизвести исключение только на endpoint orderHandler.
// У gateway не задан errorChannel. Как подменить шаг и проверить оба исхода?
```

`@SpringIntegrationTest` добавляет в тестовый Spring-контекст инструменты Spring Integration, в том числе `MockIntegrationContext`. Контекст с приложением загружает `@SpringBootTest`. С помощью `substituteMessageHandlerFor` можно заменить обработчик endpoint по его ID и сохранить остальную конфигурацию. После теста `resetBeans()` восстанавливает исходные обработчики.

В request/reply сценарии проверяем результат вызова gateway. Из `DirectChannel` при отсутствии настроенного канала ошибок сбой возвращается в тот же вызов. Чтобы проверить именно контракт `send()`, тест отправляет `GenericMessage` прямо в этот канал.

```java
@SpringBootTest
@SpringIntegrationTest
class OrderIntegrationTest {
    @Autowired MockIntegrationContext mockIntegrationContext;
    @Autowired OrderStatusGateway gateway;
    @Autowired @Qualifier("ordersInput") MessageChannel input;

    @AfterEach
    void restoreHandlers() {
        mockIntegrationContext.resetBeans();
    }

    @Test
    void returnsReply() {
        assertThat(gateway.process("order-1")).isEqualTo("processed:order-1");
    }

    @Test
    void propagatesHandlerFailure() {
        mockIntegrationContext.substituteMessageHandlerFor(
            "orderHandler", message -> {
                throw new IllegalStateException("Service unavailable");
            });

        assertThatThrownBy(() -> input.send(new GenericMessage<>("order-1")))
            .isInstanceOf(MessagingException.class)
            .hasRootCauseInstanceOf(IllegalStateException.class);
    }
}
```

Для теста одностороннего потока можно читать отдельный выходной `PollableChannel` через `receive(timeout)`. Для проверки доставки ошибок нужен источник, который публикует ошибку, и настроенный канал, например `QueueChannel`, заданный как канал ошибок poller. Тогда результат `receive(timeout)` проверяют на тип `ErrorMessage`, а его payload проверяют как исключение.

[Testing Support](https://docs.spring.io/spring-integration/reference/6.5/testing.html) · [Error Handling](https://docs.spring.io/spring-integration/reference/6.5/error-handling.html)

## Q11. Чем Spring Integration отличается от Apache Camel?

Оба — фреймворки EIP и решают одну задачу (интеграционные конвейеры), но по-разному расставляют акценты. **Spring Integration** глубоко встроен в Spring и силён, когда проект уже на нём: знакомая модель бинов, минимум новой инфраструктуры. **Apache Camel** — самостоятельный фреймворк со ставкой на широту: сотни готовых коннекторов и почти полный набор EIP, ценой более крутой кривой обучения.

| Критерий | Spring Integration | Apache Camel |
|----------|--------------------|--------------|
| Экосистема | Spring | Независимый |
| DSL | Java DSL (Spring) | Java/Scala/XML DSL |
| Коннекторы | ~30 адаптеров | 200+ компонентов |
| Learning curve | Низкая (Spring dev) | Средняя |
| EIP полнота | Основные паттерны | Почти все EIP |
| Тестирование | SpringIntegrationTest | CamelTestSupport |
| Production usage | Средний enterprise | Широко (Fuse, cloud) |

**Когда что:** Spring Integration — если проект уже на Spring и хватает базового набора паттернов и адаптеров (минимум лишней инфраструктуры). Apache Camel — если нужны 200+ коннекторов «из коробки» или редкие/специфические EIP-паттерны, которых в Spring Integration нет.

## Q12. Когда использовать Spring Integration vs Spring Kafka?

```text
A. Сервис читает записи из Kafka и пишет результаты в другой топик Kafka.
B. Сервис читает записи из Kafka, преобразует их и направляет результат
   по содержимому либо в топик Kafka, либо на SFTP-сервер.
Какой инструмент подходит для каждого сценария?
```

Это вопрос про уровень абстракции. **Spring Kafka** — тонкий слой именно над Kafka (`@KafkaListener`, `KafkaTemplate`): берите его, когда задача — просто читать/писать в топик. **Spring Integration** — уровень выше: он не привязан к одному транспорту и даёт EIP-паттерны (маршрутизация, трансформация, агрегация, retry) поверх Kafka и не только. Эмпирическое правило: если кроме чтения/записи нужна заметная логика обработки или несколько источников — поднимайтесь на Spring Integration; если же речь про потоковую аналитику (окна, джойны) — это вообще другой класс инструментов (Kafka Streams / Flink).

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

## Q13. Что такое Claim Check и как его реализовать?

```text
Во входящем сообщении есть вложение на 20 МБ. Лимит сообщения брокера: 1 МБ.
Маршрутизации нужны только метаданные. Исходные байты нужны на этапе проверки вложения.
Как передать данные между этапами при таком лимите?
```

**Claim Check** — это EIP-паттерн «номерок в гардероб»: вместо того чтобы таскать большой payload через весь поток, его один раз кладут во внешнее хранилище (S3, БД), а по каналам гоняют только лёгкий ключ-ссылку. Когда тяжёлые данные снова нужны, по ключу их забирают обратно.

**Зачем:** очереди, брокеры и сериализация плохо переносят большие сообщения (растёт latency, память, лимиты брокера). Claim Check держит сами очереди и маршрутизацию лёгкими — большой объём «лежит на полке» и не мешает.

Реализуется парой шагов: **store** (payload → хранилище, в сообщении остаётся ключ) на входе и **retrieve** (ключ → payload из хранилища) там, где данные реально понадобились.

Встроенные `ClaimCheckInTransformer` и `ClaimCheckOutTransformer` работают с общим `MessageStore`. Первый сохраняет исходное сообщение и выдаёт его ID, второй по ID восстанавливает сообщение. Для распределённого потока хранилище должно быть доступно обоим этапам.

При хранении вложения в S3 можно реализовать пару пользовательских преобразований. В этом фрагменте `AttachmentClaimCheck` обозначает прикладной компонент, у которого `store` сохраняет вложение и возвращает ключ, а `retrieve` читает байты по ключу. Метаданные для промежуточных шагов передаются отдельно, например в заголовках сообщения.

```java
@Bean
public IntegrationFlow claimCheckFlow(AttachmentClaimCheck claimCheck) {
    return IntegrationFlow
        .from("largeOrdersInput")
        .transform(claimCheck::store)  // payload → S3, возвращает ключ
        .channel("lightweightChannel") // только ключ идёт дальше
        .get();
}

@Bean
public IntegrationFlow claimCheckRetrieveFlow(AttachmentClaimCheck claimCheck) {
    return IntegrationFlow
        .from("lightweightChannel")
        .transform(claimCheck::retrieve)  // ключ → реальный payload из S3
        .handle(orderService::process)
        .get();
}
```

[Claim Check, Spring Integration 6.5](https://docs.spring.io/spring-integration/reference/6.5/claim-check.html)

## Q14. Как работает Enricher (Content Enricher)?

```text
На входе заказ с customerId. Сервис клиентов по этому ID возвращает
CustomerInfo с именем и кредитным лимитом. Следующему шагу нужен
тот же заказ с заполненными customerName и creditLimit.
Как встроить получение этих полей в поток?
```

**Content Enricher** решает частую проблему: в сообщении не хватает данных, которые лежат в другой системе. Например пришёл заказ только с `customerId`, а дальше по потоку нужны имя клиента и кредитный лимит. Enricher делает запрос за недостающими данными и дописывает их в сообщение — в payload или в заголовки.

Механика: Enricher отправляет «запросное» сообщение в `requestChannel` (как Gateway — туда можно повесить любой поток или Service Activator), дожидается ответа и через `propertyFunction`/`headerExpression` раскладывает поля ответа в обогащаемое сообщение. То есть это не «магия» — под капотом обычный request/reply во вспомогательный канал.

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

В примере выше Enricher шлёт `customerId` в `customerLookupChannel`, получает `CustomerInfo` и дописывает в заказ поля `customerName` и `creditLimit` — дальше по потоку обработчик уже видит обогащённое сообщение.

## Q15. Как мониторить Spring Integration?

```text
У приложения несколько каналов и обработчиков. В одном QueueChannel
растёт число сообщений. Нужно увидеть связи между endpoint и определить,
какой обработчик замедлился и как меняется размер очереди.
Какие данные о топологии и работе компонентов нужно собрать?
```

В Spring Integration 6.5 `MeterRegistry` в контексте включает регистрацию метрик поддерживаемых компонентов. В приложении с Actuator реестр может предоставить Spring Boot. Для диагностики очереди сравнивайте динамику её размера, отправки и чтения сообщений и длительность работы обработчика. Один граф топологии не показывает скорость обработки.

- `spring.integration.channel.queue.size` и `spring.integration.channel.queue.remaining.capacity` показывают текущий размер и свободную ёмкость `QueueChannel`.
- `spring.integration.send` с `type=channel` измеряет число и длительность операций отправки. С `type=handler` тот же таймер измеряет вызовы обработчика. `spring.integration.receive` считает операции получения, например из pollable-канала при `type=channel`.
- `spring.integration.handlers` показывает число компонентов-обработчиков. Для длительности используется таймер с `type=handler` либо `spring.integration.handler` при включённом Observation для обработчика и настроенном обработчике метрик Micrometer.

Actuator endpoint `integrationgraph` показывает каналы, endpoints и связи между ними. Для чтения по HTTP добавьте его в экспозицию и обеспечьте авторизацию вызывающего, если маршрут защищён.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,integrationgraph
```

При стандартном префиксе граф доступен по `GET /actuator/integrationgraph`.

[Metrics and Management](https://docs.spring.io/spring-integration/reference/6.5/metrics.html) · [Actuator Endpoints в Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/actuator/endpoints.html)

## See also

- [Spring Kafka](spring-kafka-interview.md) — Kafka как канал/адаптер в Spring Integration
- [Spring Messaging](spring-messaging-interview.md) — JMS, RabbitMQ базовые абстракции
- [Spring Batch](spring-batch-interview.md) — batch-обработка, часто используется совместно
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — теория EIP паттернов
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива для монолита
- [Spring WebFlux](spring-webflux-interview.md) — FluxMessageChannel как мост к реактивному стеку
- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka для KafkaMessageDrivenChannelAdapter
- [RabbitMQ](../../messaging/rabbitmq-interview.md) — основы AMQP для RabbitMQ channel adapter
- [Spring @Transactional](spring-transaction-interview.md) — транзакции в интеграционных потоках
- [Spring Testing](spring-testing-interview.md) — тестирование Spring Integration потоков
- [Spring State Machine](spring-state-machine-interview.md) — конфигурация состояний и переходов, Guards, Actions, Extended State,…
- [Apache Camel](../../messaging/apache-camel-interview.md) — альтернатива Spring Integration для Enterprise Integration Patterns
