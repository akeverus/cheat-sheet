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
- **Gateway** — двусторонний мост request/reply: вызывающий код получает ответ синхронно.

Каждый паттерн в Spring Integration — это обычный Spring-бин (endpoint), а каналы связывают их в поток.

```java
@Configuration
@EnableIntegration
public class IntegrationConfig { ... }
```

## Q2. (!) Какие типы MessageChannel существуют?

`MessageChannel` определяет, **как и в каком потоке** сообщение доходит от отправителя до обработчика: синхронно или асинхронно, точка-в-точку или всем сразу, с буфером или без. Тип канала — главное архитектурное решение в потоке, потому что от него зависят транзакционность, развязка потоков и поведение под нагрузкой.

| Канал | Поведение | Применение |
|-------|-----------|------------|
| `DirectChannel` | Синхронный, в потоке отправителя | Default; поддерживает транзакции |
| `PublishSubscribeChannel` | Broadcast всем подписчикам | Уведомления, events |
| `QueueChannel` | In-memory FIFO (требует poller) | Буферизация, развязка потоков |
| `PriorityChannel` | Очередь с приоритетом | Приоритизация сообщений |
| `ExecutorChannel` | Асинхронный (TaskExecutor) | Параллельная обработка |
| `FluxMessageChannel` | Реактивный (Reactor) | Bridge к Spring WebFlux |

Ключевое различие — **point-to-point** (`DirectChannel`, `QueueChannel`, `ExecutorChannel`: одно сообщение получает ровно один подписчик) против **publish-subscribe** (`PublishSubscribeChannel`: копию получают все подписчики). Каналы с очередью (`QueueChannel`, `PriorityChannel`) развязывают отправителя и получателя по времени, но требуют poller, который вынимает из них сообщения.

```java
@Bean public MessageChannel ordersInput() { return new DirectChannel(); }
@Bean public MessageChannel ordersQueue() { return new QueueChannel(100); }
@Bean public MessageChannel broadcasts() { return new PublishSubscribeChannel(); }
```

**Правило**: канал по умолчанию — `DirectChannel`. Он же единственный, кто прозрачно пропускает транзакцию: обработчик выполняется в том же потоке и в той же транзакции, что и отправитель. Любой канал с очередью или executor'ом разрывает поток выполнения, а значит и транзакционный контекст.

## Q3. Что такое Service Activator?

**Service Activator** — это endpoint, который соединяет канал с обычным методом бизнес-компонента. Он берёт сообщение из `inputChannel`, разворачивает его в payload, передаёт в ваш метод, а возвращённое значение заворачивает обратно в `Message` и кладёт в `outputChannel`. По сути это «точка входа» доменной логики в интеграционный поток — благодаря ему бизнес-код остаётся обычным Java-методом и не зависит от Spring Integration.

**Три способа объявить:**
- через `@ServiceActivator` на методе — самый частый вариант;
- через `.handle(...)` в Java DSL;
- через бин `MessageHandler`, если нужен прямой доступ к `Message` и заголовкам.

Если `outputChannel` не задан, ответ отправится в канал из заголовка `replyChannel` (его проставляет, например, Gateway). Если метод возвращает `void`, поток на этом endpoint завершается.

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

## Q4. Как работает MessagingGateway?

**Gateway** — это интерфейс, который вы только описываете, а реализацию Spring Integration генерирует за вас (через прокси). Его задача — спрятать messaging за обычным Java-вызовом: код, который дёргает `gateway.placeOrder(order)`, не подозревает, что под капотом аргумент заворачивается в `Message`, уходит в `requestChannel`, проходит весь поток, а ответ из `replyChannel` распаковывается обратно в возвращаемое значение.

Так Gateway становится границей между «обычным» приложением и интеграционным слоем: на входе в поток ставят Gateway, на выходе из бизнес-логики — Service Activator (Q3).

**Поведение метода зависит от сигнатуры:**
- метод **возвращает значение** → синхронный request/reply, вызывающий поток ждёт ответ;
- метод возвращает **`void`** → fire-and-forget, ответа не ждём;
- возврат `Future`/`Mono` → асинхронный ответ без блокировки потока.

Заголовки сообщения задаются через `@Header`, payload — через `@Payload`.

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

## Q5. (!) Как устроен Java DSL?

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
                        .handle(orderService::processExpress))
                      .subFlowMapping("NORMAL", sf -> sf
                        .handle(orderService::processNormal))
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
- **timeout** — что делать, если часть сообщений так и не пришла: `groupTimeout` + `sendPartialResultOnExpiry` позволяют отдать неполную группу, иначе она зависнет в памяти навсегда.

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

## Q7. Как обрабатывать ошибки?

В Spring Integration исключение из обработчика не просто пробрасывается вверх по стеку — оно заворачивается в `ErrorMessage` (внутри лежит `MessagingException` с причиной и исходным `failedMessage`) и отправляется в специальный канал ошибок. Это позволяет обрабатывать сбои декларативно, тем же messaging-механизмом, а не try/catch на каждом шаге.

**Три уровня обработки, от глобального к локальному:**
1. **`errorChannel`** — глобальный канал-«ловушка»: сюда по умолчанию попадают все необработанные ошибки потока. Удобно для логирования и отправки в DLQ.
2. **Локальный `errorChannel` у конкретного endpoint** — перехватывает ошибки только этого шага, не задевая остальной поток.
3. **`RequestHandlerRetryAdvice`** — не обрабатывает ошибку, а пытается выполнить операцию повторно (retry с backoff), прежде чем считать её сбоем. Часто это то, что нужно для transient-ошибок (`IOException`, недоступность внешнего сервиса).

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

## Q8. Как подключить Kafka через Spring Integration?

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

## Q9. Как транзакции работают в Spring Integration?

Транзакция в Spring — это контекст, привязанный к потоку выполнения. Поэтому в Spring Integration работает простое правило: **транзакция жива ровно до тех пор, пока сообщение идёт в одном потоке**. Через `DirectChannel` обработка выполняется в потоке отправителя, поэтому транзакция, открытая вызывающим кодом, охватывает весь поток — от отправки до последнего endpoint.

Как только в потоке появляется канал с очередью (`QueueChannel`) или executor'ом (`ExecutorChannel`), сообщение подхватывает **другой поток** — транзакционный контекст обрывается. Чтобы и асинхронная часть была транзакционной, транзакцию нужно открывать заново — на стороне poller'а, который вынимает сообщения из очереди.

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

**Ограничение**: сквозная транзакция «из коробки» работает только через `DirectChannel`. `QueueChannel` разрывает транзакционный контекст, потому что вводит асинхронность — поэтому транзакцию для асинхронной части настраивают явно на poller'е (`.transactional()`).

## Q10. Как тестировать Spring Integration потоки?

Поток — это набор бинов, связанных каналами, поэтому тестировать его удобнее целиком, как чёрный ящик: подать сообщение на вход и проверить, что пришло на выход. Для этого есть два инструмента из `spring-integration-test`:
- **`@SpringIntegrationTest`** — поднимает контекст в тест-режиме и даёт `MockIntegrationContext`.
- **`MockIntegrationContext`** — позволяет на лету подменять отдельные endpoint'ы моками (`substituteMessageHandlerFor`), не трогая остальной поток. Так удобно симулировать сбой конкретного шага и проверить, что ошибка ушла в `errorChannel`.

**Типовой сценарий теста:** отправить сообщение через Gateway → дождаться результата на выходном `PollableChannel` (`receive(timeout)`) → проверить payload. Для проверки ошибок — подменить нужный handler на бросающий исключение и убедиться, что в `errorChannel` пришёл `ErrorMessage`.

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

**Claim Check** — это EIP-паттерн «номерок в гардероб»: вместо того чтобы таскать большой payload через весь поток, его один раз кладут во внешнее хранилище (S3, БД), а по каналам гоняют только лёгкий ключ-ссылку. Когда тяжёлые данные снова нужны, по ключу их забирают обратно.

**Зачем:** очереди, брокеры и сериализация плохо переносят большие сообщения (растёт latency, память, лимиты брокера). Claim Check держит сами очереди и маршрутизацию лёгкими — большой объём «лежит на полке» и не мешает.

Реализуется парой шагов: **store** (payload → хранилище, в сообщении остаётся ключ) на входе и **retrieve** (ключ → payload из хранилища) там, где данные реально понадобились.

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

## Q14. Как работает Enricher (Content Enricher)?

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

Мониторинг строится на двух вещах: **метрики через Micrometer** (сколько сообщений прошло, как долго работали обработчики, насколько забиты очереди) и **`integrationgraph`** — actuator-endpoint, который отдаёт весь поток в виде графа узлов и каналов. Граф удобен, чтобы увидеть реальную топологию приложения: какие каналы есть, что к чему подключено, где узкое место.

На что смотреть в первую очередь: `channels.queueSize` сигналит, что `QueueChannel` не успевает разгружаться (получатель медленнее отправителя), а время в `handlers` показывает, какой endpoint тормозит поток.

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
