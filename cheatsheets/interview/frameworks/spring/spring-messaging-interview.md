---
title: "Вопросы на собеседовании: Spring Messaging"
description: "Spring Messaging для JMS и RabbitMQ: JmsTemplate, RabbitTemplate, @JmsListener, @RabbitListener, exchanges, routing keys, DLQ, транзакции"
tags:
  - interview
  - spring
  - spring-messaging-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Messaging"
  - "Spring Messaging interview"
  - "Spring Messaging собеседование"
prerequisites:
  - "[[spring-messaging]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Messaging`

`Spring Messaging` — общая абстракция Spring для работы с JMS и RabbitMQ (AMQP). Предоставляет унифицированные `JmsTemplate`/`RabbitTemplate` для отправки и `@JmsListener`/`@RabbitListener` для потребления. Часто требуется в enterprise Java вакансиях.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring JMS](https://docs.spring.io/spring-framework/reference/integration/jms.html) — Spring JMS документация
- [Spring AMQP](https://docs.spring.io/spring-amqp/reference/html/) — Spring AMQP (RabbitMQ)
- [Baeldung: Spring JMS](https://www.baeldung.com/spring-jms) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring Messaging и какие брокеры он поддерживает?

**Spring Messaging** — общая абстракция Spring для работы с очередями сообщений. Предоставляет унифицированные интерфейсы (`Message<T>`, `MessageChannel`) и аннотации (`@MessageMapping`).

Поддерживаемые брокеры:
- **JMS** (Java Message Service): ActiveMQ, IBM MQ, Oracle AQ — через `spring-jms`.
- **AMQP** (RabbitMQ) — через `spring-amqp` / `spring-rabbit`.
- **STOMP** (WebSocket Messaging) — через `spring-websocket`.
- **Apache Kafka** — через `spring-kafka` (отдельный проект).
- **Redis Pub/Sub** — через `spring-data-redis`.


> [!mcq]
> - [ ] Spring Messaging = отдельный брокер сообщений, замена RabbitMQ | ❌ ПОСЛЕДСТВИЕ: Spring Messaging — абстракция над брокерами, не сам брокер; без ActiveMQ/Rabbit/Kafka инфраструктура не работает
> - [x] Абстракция над JMS/AMQP/STOMP/Kafka с унифицированными Message<T> и MessageChannel интерфейсами | ✓ ПРИМЕНЯТЬ: когда нужна единая модель для разных брокеров 📋 ПРАВИЛО: spring-messaging = интерфейсы; spring-jms/spring-rabbit = реализации 🔗 См. Q2
> - [ ] Spring Messaging поддерживает только JMS (ActiveMQ/IBM MQ) | ❌ ПОСЛЕДСТВИЕ: spring-amqp (RabbitMQ), spring-kafka, spring-websocket (STOMP) — все отдельные проекты поверх spring-messaging абстракции
> - [ ] Один стартер spring-boot-starter-messaging включает все брокеры | ❌ ПОСЛЕДСТВИЕ: каждый брокер требует отдельный стартер: spring-boot-starter-activemq, spring-boot-starter-amqp, spring-kafka

## Q2. Как настроить JMS и отправить сообщение?

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
  jms:
    template:
      default-destination: orders-queue
    listener:
      acknowledge-mode: client  # или auto, dups-ok
      concurrency: 3-10          # мин-макс consumers
```

```java
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final JmsTemplate jmsTemplate;

    public void sendOrder(Order order) {
        jmsTemplate.convertAndSend("orders-queue", order);
    }

    public void sendWithHeaders(Order order) {
        jmsTemplate.convertAndSend("orders-queue", order, message -> {
            message.setStringProperty("priority", "HIGH");
            message.setJMSExpiration(60_000);  // 1 минута
            return message;
        });
    }
}
```


> [!mcq]
> - [ ] JmsTemplate.send() принимает POJO напрямую без конвертера | ❌ ПОСЛЕДСТВИЕ: send() принимает MessageCreator, не POJO; для POJO нужен convertAndSend() с настроенным MessageConverter
> - [ ] acknowledge-mode: auto гарантирует exactly-once доставку | ❌ ПОСЛЕДСТВИЕ: auto-ack коммитится до обработки; при краше сообщение теряется; нужен client-ack с ручным session.commit()/rollback()
> - [x] jmsTemplate.convertAndSend(destination, object) + MessageConverter (Jackson) автоматически конвертирует POJO в JSON | ✓ ПРИМЕНЯТЬ: отправка объектов в JMS очереди без ручной сериализации 📋 ПРАВИЛО: convertAndSend = POJO → MessageConverter → Message 🔗 См. Q3
> - [ ] JmsTemplate является stateful — нельзя использовать как @Bean | ❌ ПОСЛЕДСТВИЕ: JmsTemplate thread-safe и stateless — можно и нужно инжектировать как singleton @Bean

## Q3. Как получать JMS-сообщения?

```java
@Component
public class OrderConsumer {

    @JmsListener(destination = "orders-queue", concurrency = "3-10")
    public void handleOrder(Order order) {
        log.info("Received: {}", order);
        processOrder(order);
    }

    // С доступом к заголовкам
    @JmsListener(destination = "orders-queue")
    public void handleWithHeaders(
            @Payload Order order,
            @Header("priority") String priority,
            @Headers Map<String, Object> allHeaders,
            Message jmsMessage) {
        log.info("Priority={}, Order={}", priority, order);
    }

    // Ручной ACK
    @JmsListener(destination = "orders-queue")
    public void handleManualAck(Order order, Session session) throws JMSException {
        try {
            processOrder(order);
            session.commit();
        } catch (Exception e) {
            session.rollback();  // сообщение вернётся в очередь
        }
    }
}
```


> [!mcq]
> - [ ] @JmsListener работает только с acknowledge-mode: auto | ❌ ПОСЛЕДСТВИЕ: MANUAL режим требует Session параметра в методе и явного session.commit()/rollback() — полный контроль над ACK
> - [x] @JmsListener(destination, concurrency="3-10") + @Payload/@Header для binding; MANUAL ACK через Session параметр | ✓ ПРИМЕНЯТЬ: async потребление JMS сообщений с параллельными consumers 📋 ПРАВИЛО: concurrency="min-max" = пул JMS consumers; MANUAL ACK = session.commit/rollback 🔗 См. Q2
> - [ ] concurrency на @JmsListener создаёт thread pool для всего приложения | ❌ ПОСЛЕДСТВИЕ: concurrency создаёт pool только для данного listener; разные @JmsListener имеют независимые pools
> - [ ] @Header инжектирует только JMS system headers (JMSMessageID, JMSTimestamp) | ❌ ПОСЛЕДСТВИЕ: @Header инжектирует любой header — и system (JMSPriority), и пользовательские (priority, source)

## Q4. Как настроить RabbitMQ и отправить сообщение?

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    publisher-confirm-type: correlated  # подтверждение отправки
    publisher-returns: true              # уведомления о возврате
    listener:
      simple:
        acknowledge-mode: auto
        concurrency: 3
        max-concurrency: 10
```

```java
@Configuration
public class RabbitConfig {

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable("orders.queue")
            .withArgument("x-dead-letter-exchange", "dlx")
            .withArgument("x-dead-letter-routing-key", "orders.dlq")
            .withArgument("x-message-ttl", 60000)  // TTL 1 минута
            .build();
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange("orders.exchange");
    }

    @Bean
    public Binding binding(Queue ordersQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(ordersQueue)
            .to(ordersExchange)
            .with("orders.created.#");
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class OrderPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrder(Order order) {
        rabbitTemplate.convertAndSend("orders.exchange",
            "orders.created.retail",
            order,
            message -> {
                message.getMessageProperties().setContentType("application/json");
                message.getMessageProperties().setHeader("source", "api");
                return message;
            });
    }
}
```


> [!mcq]
> - [ ] QueueBuilder.durable(name) без x-dead-letter-exchange гарантирует доставку | ❌ ПОСЛЕДСТВИЕ: без DLX сообщения с истёкшим TTL или превышенным maxLength просто выбрасываются; DLX обязателен для poison pill handling
> - [ ] publisher-confirm-type: correlated — асинхронные подтверждения не нужны, достаточно transactional | ❌ ПОСЛЕДСТВИЕ: transactional режим синхронный и медленный (10x overhead); correlated confirms — async и производительный способ гарантировать доставку
> - [ ] RabbitTemplate.convertAndSend(queue, object) без exchange работает только для default exchange | ❌ ПОСЛЕДСТВИЕ: это верно — default exchange маршрутизирует по имени очереди; но для custom routing нужен convertAndSend(exchange, routingKey, object)
> - [x] RabbitTemplate.convertAndSend(exchange, routingKey, object) + @Bean Queue/Exchange/Binding + publisher-confirms для guaranteed delivery | ✓ ПРИМЕНЯТЬ: надёжная публикация с custom routing через TopicExchange 📋 ПРАВИЛО: RabbitMQ = exchange + routing-key → queue; DLX для failed messages 🔗 См. Q5

## Q5. Какие типы Exchange в RabbitMQ?

| Тип | Маршрутизация | Применение |
|-----|---------------|------------|
| `direct` | По точному совпадению routing key | Роутинг по типу события |
| `topic` | По шаблону routing key (`*` / `#`) | Иерархические события (`orders.created.eu`) |
| `fanout` | Broadcast во все связанные очереди | Событийная модель pub-sub |
| `headers` | По заголовкам сообщения | Сложная маршрутизация без routing key |

```java
// Direct exchange
@Bean DirectExchange directEx() { return new DirectExchange("direct.ex"); }

// Topic с шаблонами
@Bean TopicExchange topicEx() { return new TopicExchange("topic.ex"); }
@Bean Binding b1(Queue q1, TopicExchange ex) {
    return BindingBuilder.bind(q1).to(ex).with("orders.*.eu");    // orders.*.eu
}
@Bean Binding b2(Queue q2, TopicExchange ex) {
    return BindingBuilder.bind(q2).to(ex).with("orders.#");       // любой уровень
}

// Fanout — все привязанные очереди получат копию
@Bean FanoutExchange fanoutEx() { return new FanoutExchange("events.fanout"); }
```


> [!mcq]
> - [ ] fanout exchange требует routing key для маршрутизации | ❌ ПОСЛЕДСТВИЕ: fanout игнорирует routing key — broadcast во все привязанные очереди; routing key важен только для direct и topic exchanges
> - [ ] topic exchange использует * для любого числа слов в routing key | ❌ ПОСЛЕДСТВИЕ: * = ровно одно слово; # = ноль или более слов; orders.*.eu — одно слово между points; orders.# — любой suffix
> - [x] direct=точное совпадение ключа; topic=шаблоны (* одно слово, # много); fanout=broadcast; headers=по заголовкам | ✓ ПРИМЕНЯТЬ: topic для иерархических событий (orders.created.eu); fanout для pub-sub 📋 ПРАВИЛО: * = одно слово, # = любое количество; fanout игнорирует routing key 🔗 См. Q4
> - [ ] headers exchange медленнее других — не рекомендован в production | ❌ ПОСЛЕДСТВИЕ: headers exchange — стандартная RabbitMQ фича; медленнее topic только при сложных условиях, но часто оправдан когда routing key неудобен

## Q6. Как получать RabbitMQ сообщения?

```java
@Component
public class OrderProcessor {

    @RabbitListener(queues = "orders.queue", concurrency = "3-10")
    public void handleOrder(Order order) {
        processOrder(order);
    }

    // С @SendTo для reply
    @RabbitListener(queues = "orders.requests")
    @SendTo("orders.responses")
    public OrderResult processRequest(OrderRequest request) {
        return orderService.process(request);
    }

    // Ручной ACK для контроля над failed-обработкой
    @RabbitListener(queues = "orders.queue", ackMode = "MANUAL")
    public void handleManualAck(Order order, Channel channel,
                                 @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            processOrder(order);
            channel.basicAck(tag, false);
        } catch (RetryableException e) {
            channel.basicNack(tag, false, true);  // вернуть в очередь
        } catch (NonRetryableException e) {
            channel.basicNack(tag, false, false); // отправить в DLQ
        }
    }
}
```


> [!mcq]
> - [ ] ackMode="MANUAL" автоматически ACK при выходе из метода без исключения | ❌ ПОСЛЕДСТВИЕ: MANUAL = никакого auto-ACK; без явного channel.basicAck(tag, false) сообщение остаётся unacked и блокирует consumer
> - [x] @RabbitListener(ackMode="MANUAL") + channel.basicAck(tag, false) при успехе; basicNack(tag, false, requeue) при ошибке — контроль DLQ маршрутизации | ✓ ПРИМЕНЯТЬ: когда нужно разделить RetryableException (requeue=true) и NonRetryable (→DLQ) 📋 ПРАВИЛО: basicNack(false) = в DLQ; basicNack(true) = requeue 🔗 См. Q7
> - [ ] @SendTo работает только с @RabbitListener, не с @JmsListener | ❌ ПОСЛЕДСТВИЕ: @SendTo поддерживается обеими: JMS (reply destination) и AMQP (routing key); универсальная аннотация Spring Messaging
> - [ ] concurrency на @RabbitListener создаёт отдельный thread pool для всего Spring context | ❌ ПОСЛЕДСТВИЕ: concurrency создаёт SimpleMessageListenerContainer с N consumers только для данного listener, изолированно

## Q7. Как работает Dead Letter Queue (DLQ)?

**DLQ** — очередь для "мертвых" сообщений, которые не удалось обработать.

Сообщение попадает в DLQ когда:
- Превышен TTL (time-to-live).
- Consumer отклонил с `requeue=false`.
- Очередь переполнена (x-max-length).

```java
// Конфигурация Dead Letter Exchange
@Bean
public DirectExchange dlx() {
    return new DirectExchange("dlx");
}

@Bean
public Queue ordersDlq() {
    return QueueBuilder.durable("orders.dlq").build();
}

@Bean
public Queue ordersQueue() {
    return QueueBuilder.durable("orders.queue")
        .withArgument("x-dead-letter-exchange", "dlx")
        .withArgument("x-dead-letter-routing-key", "orders.dlq")
        .build();
}

@Bean
public Binding dlqBinding(Queue ordersDlq, DirectExchange dlx) {
    return BindingBuilder.bind(ordersDlq).to(dlx).with("orders.dlq");
}

// Consumer DLQ для разбора проблемных сообщений
@RabbitListener(queues = "orders.dlq")
public void processDlq(Order order,
                       @Header("x-death") List<Map<String, ?>> deathInfo) {
    log.error("Failed message: {}, death chain: {}", order, deathInfo);
    alertingService.notifyDlqMessage(order);
}
```


> [!mcq]
> - [ ] DLQ настраивается только в RabbitMQ Management UI, не программно | ❌ ПОСЛЕДСТВИЕ: x-dead-letter-exchange и x-dead-letter-routing-key задаются в @Bean Queue через withArgument(); полностью программная конфигурация
> - [ ] сообщение попадает в DLQ только при consumer exception | ❌ ПОСЛЕДСТВИЕ: DLQ принимает также: превышение TTL, переполнение очереди (x-max-length), basicNack с requeue=false — три независимых триггера
> - [ ] @Header("x-death") доступен только для первого отклонённого сообщения | ❌ ПОСЛЕДСТВИЕ: x-death — список (List<Map>) накапливающий всю цепочку rejections; каждое отклонение добавляет новый entry с причиной
> - [x] x-dead-letter-exchange + x-dead-letter-routing-key на source queue → failed messages → DLQ consumer с @Header("x-death") для аудита | ✓ ПРИМЕНЯТЬ: poison pill handling + alerting на необрабатываемые сообщения 📋 ПРАВИЛО: DLQ = safety net для TTL/nack/overflow; x-death = death audit trail 🔗 См. Q6

## Q8. Как работают транзакции в Spring Messaging?

```java
// JMS транзакции
@JmsListener(destination = "orders", containerFactory = "transactionalJmsFactory")
@Transactional  // общий контекст с JPA
public void process(Order order) {
    orderRepository.save(order);           // JPA
    emailService.sendConfirmation(order);  // отправка в другую очередь
    // При ошибке — rollback обоих: JMS вернёт сообщение, JPA откатит
}
```

```java
// RabbitMQ транзакции
@Bean
public RabbitTemplate transactionalRabbitTemplate(ConnectionFactory cf) {
    RabbitTemplate template = new RabbitTemplate(cf);
    template.setChannelTransacted(true);
    return template;
}

@Transactional
public void processAndPublish(OrderCommand cmd) {
    Order order = orderRepository.save(new Order(cmd));
    rabbitTemplate.convertAndSend("events", "order.created", order);
    // всё в одной транзакции
}
```

**Важно**: AMQP не поддерживает 2PC (two-phase commit). JMS через XA-транзакции может, но это сложно и медленно. Альтернатива — Transactional Outbox Pattern.


> [!mcq]
> - [ ] @Transactional на @JmsListener гарантирует exactly-once с JPA без дополнительной настройки | ❌ ПОСЛЕДСТВИЕ: нужен transactional JMS container factory (sessionTransacted=true); без него JPA rollback не откатит JMS acknowledge
> - [x] JMS: @Transactional + transactional ContainerFactory = atomic JMS+JPA; AMQP: setChannelTransacted=true; для cross-system — Transactional Outbox | ✓ ПРИМЕНЯТЬ: когда DB save и message publish должны быть атомарными 📋 ПРАВИЛО: AMQP не 2PC → Outbox Pattern; JMS XA работает но медленно 🔗 См. Q9
> - [ ] RabbitMQ поддерживает 2PC (XA транзакции) как JMS | ❌ ПОСЛЕДСТВИЕ: AMQP не поддерживает 2PC; RabbitMQ транзакции — channel-level, не XA; для distributed tx нужен Outbox или Saga
> - [ ] setChannelTransacted=true на RabbitTemplate не влияет на @RabbitListener | ❌ ПОСЛЕДСТВИЕ: setChannelTransacted влияет на RabbitTemplate (publisher side); listener transactionality настраивается отдельно через ContainerFactory

## Q9. Что такое Publisher Confirms в RabbitMQ?

**Publisher Confirms** — механизм подтверждения от брокера о получении сообщения.

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated  # или simple
```

```java
@Service
@RequiredArgsConstructor
public class ReliableOrderPublisher {
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void configureCallbacks() {
        rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
            if (ack) {
                log.info("Message confirmed: {}", correlation);
            } else {
                log.error("Message NACK: {}, cause: {}", correlation, cause);
                // retry or alert
            }
        });

        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Message returned: {}, reason: {}",
                returned.getMessage(), returned.getReplyText());
        });
    }

    public void sendReliably(Order order) {
        CorrelationData correlation = new CorrelationData(order.getId());
        rabbitTemplate.convertAndSend("orders.exchange", "orders.created",
            order, correlation);
    }
}
```

Без confirms отправитель не знает, достигло ли сообщение брокера — при проблемах сети сообщение может потеряться.


> [!mcq]
> - [ ] publisher-confirm-type: simple достаточно для production надёжности | ❌ ПОСЛЕДСТВИЕ: simple = fire-and-forget confirms без correlation; correlated = async confirm с CorrelationData для track каждого сообщения индивидуально
> - [ ] setReturnsCallback срабатывает когда consumer не обработал сообщение | ❌ ПОСЛЕДСТВИЕ: ReturnsCallback = сообщение не маршрутизировалось ни в одну очередь (нет binding); это publisher → broker уровень, не consumer
> - [ ] Publisher Confirms гарантируют exactly-once delivery | ❌ ПОСЛЕДСТВИЕ: Confirms гарантируют at-least-once (сообщение дошло до брокера); для exactly-once нужен идемпотентный consumer + уникальный messageId
> - [x] setConfirmCallback(ack, correlationData, cause): ack=true → доставлено в broker; ack=false → retry; setReturnsCallback → сообщение не маршрутизировано ни в одну очередь | ✓ ПРИМЕНЯТЬ: guaranteed delivery с async подтверждением 📋 ПРАВИЛО: Confirms = broker ACK; Returns = unroutable message alert 🔗 См. Q4

## Q10. Что такое Request-Reply Pattern в Spring Messaging?

**Request-Reply** — двустороннее взаимодействие через messaging.

```java
// Синхронный вызов через RabbitTemplate
@Service
@RequiredArgsConstructor
public class OrderClient {
    private final RabbitTemplate rabbitTemplate;

    public OrderResult requestOrder(OrderRequest request) {
        return (OrderResult) rabbitTemplate.convertSendAndReceive(
            "orders.exchange",
            "orders.requests",
            request
        );
    }
}

// Серверная сторона
@RabbitListener(queues = "orders.requests")
@SendTo("orders.responses")  // @SendTo не нужен если replyTo установлен в заголовке
public OrderResult handleRequest(OrderRequest request) {
    return orderService.process(request);
}
```

Под капотом: клиент создаёт временную очередь для reply, передаёт её имя в `replyTo` заголовок, ждёт ответ с коррелирующим ID.


> [!mcq]
> - [ ] convertSendAndReceive() неблокирующий — возвращает CompletableFuture | ❌ ПОСЛЕДСТВИЕ: convertSendAndReceive() блокирующий — ждёт ответ по умолчанию 5с; для async нужен AsyncRabbitTemplate.sendAndReceive()
> - [x] convertSendAndReceive(exchange, routingKey, request) блокирует до ответа; сервер использует @SendTo или replyTo header; под капотом — временная reply queue с correlationId | ✓ ПРИМЕНЯТЬ: RPC-like sync запрос/ответ через messaging 📋 ПРАВИЛО: Request-Reply = temp reply-queue + correlationId + blocking wait 🔗 См. Q6
> - [ ] @SendTo на listener обязателен для Request-Reply — без него ответ теряется | ❌ ПОСЛЕДСТВИЕ: @SendTo не нужен если клиент передаёт replyTo заголовок; RabbitMQ автоматически маршрутизирует ответ в replyTo очередь
> - [ ] Request-Reply работает только с direct exchange — не поддерживает topic | ❌ ПОСЛЕДСТВИЕ: работает с любым exchange типом; routing key указывается клиентом в convertSendAndReceive(exchange, routingKey, ...)

## Q11. Как настроить кастомный MessageConverter?

```java
// JSON конвертер по умолчанию
@Bean
public MessageConverter jsonMessageConverter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    converter.setClassMapper(idClassMapper());  // для type-safe десериализации
    return converter;
}

// RabbitTemplate использует конвертер
@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
    RabbitTemplate template = new RabbitTemplate(cf);
    template.setMessageConverter(converter);
    return template;
}
```

```java
// Кастомный конвертер (например, Protobuf)
@Component
public class ProtobufMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties properties) {
        if (object instanceof com.google.protobuf.Message proto) {
            properties.setContentType("application/x-protobuf");
            return new Message(proto.toByteArray(), properties);
        }
        throw new MessageConversionException("Not a protobuf message");
    }

    @Override
    public Object fromMessage(Message message) {
        // парсинг protobuf
    }
}
```


> [!mcq]
> - [ ] Jackson2JsonMessageConverter работает без classMapper — Spring автоматически определяет тип | ❌ ПОСЛЕДСТВИЕ: без classMapper Spring не знает в какой класс десериализовать; нужен DefaultClassMapper или @JsonTypeInfo в DTO
> - [ ] MessageConverter нужно регистрировать в Spring context через @Component | ❌ ПОСЛЕДСТВИЕ: MessageConverter должен быть внедрён в RabbitTemplate.setMessageConverter() и ContainerFactory; простой @Component недостаточен
> - [x] @Bean Jackson2JsonMessageConverter + RabbitTemplate.setMessageConverter(); для type-safe deserialization — DefaultClassMapper с trusted packages | ✓ ПРИМЕНЯТЬ: JSON сериализация POJO в RabbitMQ без ручного marshal/unmarshal 📋 ПРАВИЛО: MessageConverter = pluggable serializer; ClassMapper = type resolution 🔗 См. Q4
> - [ ] Кастомный MessageConverter заменяет Spring стандартный только если нет Jackson в classpath | ❌ ПОСЛЕДСТВИЕ: Spring выбирает MessageConverter явно через setMessageConverter(); наличие Jackson в classpath не определяет выбор автоматически

## Q12. Как реализовать retry механизм?

```java
// Автоматический retry через RetryTemplate
@Bean
public SimpleRabbitListenerContainerFactory retryContainerFactory(
        ConnectionFactory cf, MessageConverter converter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(cf);
    factory.setMessageConverter(converter);

    RetryTemplate retryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(TransientException.class)
        .build();

    factory.setAdviceChain(RetryInterceptorBuilder.stateless()
        .retryOperations(retryTemplate)
        .recoverer(new RejectAndDontRequeueRecoverer())  // после 3 попыток → DLQ
        .build());

    return factory;
}

@RabbitListener(queues = "orders", containerFactory = "retryContainerFactory")
public void handleOrder(Order order) {
    orderService.process(order);  // при ошибке — retry с backoff
}
```

**Альтернатива**: ручной retry с TTL + DLX (см. Q7) — "delayed retry" через циклирование между очередями.


> [!mcq]
> - [ ] RetryTemplate с maxAttempts=3 применяется автоматически к любому @RabbitListener | ❌ ПОСЛЕДСТВИЕ: нужно явно создать containerFactory с RetryInterceptor и указать его в @RabbitListener(containerFactory="retryContainerFactory")
> - [ ] RejectAndDontRequeueRecoverer реквеует сообщение после всех попыток | ❌ ПОСЛЕДСТВИЕ: RejectAndDontRequeueRecoverer отвергает с requeue=false → сообщение идёт в DLQ; для requeue нужен ImmediateRequeueRecoverer
> - [x] RetryTemplate.builder().maxAttempts(3).exponentialBackoff() + RejectAndDontRequeueRecoverer → DLQ после N попыток | ✓ ПРИМЕНЯТЬ: автоматический retry с exponential backoff для transient ошибок 📋 ПРАВИЛО: RetryTemplate = stateless retry; RecoveryCallback = что делать после исчерпания попыток 🔗 См. Q7
> - [ ] exponentialBackoff(1000, 2, 10000) — первая цифра это количество попыток | ❌ ПОСЛЕДСТВИЕ: (initialInterval, multiplier, maxInterval) = начальная задержка, множитель, максимум; количество попыток — maxAttempts()

## Q13. Как тестировать Spring Messaging?

```java
// JMS с EmbeddedActiveMQ
@SpringBootTest
class JmsIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private OrderProcessor orderProcessor;

    @Test
    void shouldProcessOrderFromQueue() {
        Order order = new Order("customer-1", BigDecimal.TEN);

        jmsTemplate.convertAndSend("orders-queue", order);

        await().atMost(5, SECONDS).untilAsserted(() ->
            verify(orderRepository).save(argThat(o -> o.customerId().equals("customer-1")))
        );
    }
}
```

```java
// RabbitMQ с Testcontainers
@SpringBootTest
@Testcontainers
class RabbitIntegrationTest {

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
    }
}
```


> [!mcq]
> - [ ] @SpringBootTest с embedded JMS не требует внешнего брокера — работает из коробки без конфигурации | ❌ ПОСЛЕДСТВИЕ: нужна зависимость spring-boot-starter-activemq и embedded-broker=true в YAML; без этого тест пытается подключиться к реальному брокеру
> - [ ] RabbitMQ тесты лучше всего писать с @MockBean RabbitTemplate | ❌ ПОСЛЕДСТВИЕ: @MockBean не тестирует AMQP семантику (routing, exchange, DLQ); Testcontainers даёт реальный RabbitMQ с полной проверкой конфигурации
> - [x] JMS: @SpringBootTest + EmbeddedActiveMQ; AMQP: Testcontainers RabbitMQContainer + @DynamicPropertySource; await() для async assertion | ✓ ПРИМЕНЯТЬ: integration tests с реальным брокером вместо mocks 📋 ПРАВИЛО: @DynamicPropertySource инжектирует container ports в Spring context 🔗 См. Q6, Q3
> - [ ] @DynamicPropertySource доступен только в @Testcontainers тестах | ❌ ПОСЛЕДСТВИЕ: @DynamicPropertySource — любой @SpringBootTest; используется для внедрения динамических значений (container ports, random ports) в application properties

## Q14. Чем Spring JMS отличается от Spring AMQP?

| Критерий | JMS (ActiveMQ) | AMQP (RabbitMQ) |
|----------|----------------|-----------------|
| Стандарт | Java EE | Open protocol |
| Exchanges | Нет (direct queue) | Да (direct/topic/fanout/headers) |
| Routing | Queue name + selector | Routing key + exchange |
| Message model | Queue или Topic | Exchange → Queue(s) |
| Вендоры | Один стандарт, разные реализации | Специфично для AMQP брокеров |
| Спецификация | JMS 2.0 | AMQP 0.9.1 / 1.0 |
| Полезность | Legacy enterprise | Современные микросервисы |

JMS — выбор при legacy Java EE системах. AMQP — при выборе современного брокера с гибкой маршрутизацией.


> [!mcq]
> - [ ] JMS и AMQP взаимозаменяемы — JmsTemplate и RabbitTemplate имеют одинаковый API | ❌ ПОСЛЕДСТВИЕ: разные API (JmsTemplate.convertAndSend vs RabbitTemplate.convertAndSend с exchange/routingKey); AMQP имеет exchange routing, JMS — нет
> - [ ] JMS не поддерживает pub-sub — только point-to-point очереди | ❌ ПОСЛЕДСТВИЕ: JMS поддерживает Topics (pub-sub) через javax.jms.Topic; RabbitMQ fanout exchange аналогичен
> - [x] JMS=Java EE стандарт, direct queue, legacy enterprise; AMQP=open protocol, exchange routing (direct/topic/fanout), modern microservices | ✓ ПРИМЕНЯТЬ: новые сервисы → AMQP/RabbitMQ; интеграция с IBM MQ/Oracle AQ → JMS 📋 ПРАВИЛО: JMS = стандарт для Java EE; AMQP = протокол для любого языка 🔗 См. Q4, Q5
> - [ ] Spring JMS работает только с ActiveMQ, Spring AMQP — только с RabbitMQ | ❌ ПОСЛЕДСТВИЕ: Spring JMS работает с любым JMS-совместимым брокером (IBM MQ, Oracle AQ, Artemis); Spring AMQP — с любым AMQP 0.9.1 брокером

## Q15. Когда использовать Spring Messaging vs Kafka?

```text
Spring Messaging (JMS/AMQP) подходит когда:
✓ Нужна гарантия доставки одному consumer-у (queue semantics)
✓ Request-Reply pattern
✓ Малый объём, задержка важнее throughput
✓ Сообщения = commands/tasks, выполненные один раз

Kafka подходит когда:
✓ Event streaming, append-only log
✓ Множество consumer groups читают те же данные независимо
✓ Re-read исторических данных (replay)
✓ Высокий throughput (>100K msg/sec)
✓ Stream processing (Kafka Streams, Flink)
```

**Практика**: в современных системах часто оба — RabbitMQ для task queues (long-running jobs), Kafka для event streaming и audit logs.


> [!mcq]
> - [ ] Kafka лучше RabbitMQ для task queues — выше throughput | ❌ ПОСЛЕДСТВИЕ: Kafka не удаляет processed messages; task semantics (один consumer обрабатывает, удаляет) → RabbitMQ; Kafka = append-log для replay
> - [ ] Spring Messaging (JMS/AMQP) поддерживает replay исторических событий | ❌ ПОСЛЕДСТВИЕ: JMS/AMQP не хранят processed messages; replay → Kafka с retention; AMQP = fire-and-forget delivery
> - [x] JMS/AMQP: task queues, request-reply, гарантия одному consumer; Kafka: event streaming, множество consumer groups, высокий throughput, replay | ✓ ПРИМЕНЯТЬ: RabbitMQ для ordered jobs/tasks; Kafka для event log + stream processing 📋 ПРАВИЛО: queue = task выполнить один раз; Kafka topic = event log читать много раз 🔗 См. Q1
> - [ ] Kafka поддерживает request-reply паттерн нативно как RabbitMQ | ❌ ПОСЛЕДСТВИЕ: Kafka не поддерживает нативный request-reply; нужна самостоятельная реализация через correlation ID и reply topic

## See also

- [Spring Kafka](spring-kafka-interview.md) — интеграция Kafka в Spring
- [Spring Integration](spring-integration-interview.md) — EIP паттерны в Spring
- [RabbitMQ](../../messaging/rabbitmq-interview.md) — основы AMQP, exchanges, queues, routing keys
- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka, partitions, consumer groups
- [Message Brokers Comparison](../../messaging/message-brokers-comparison-interview.md) — Kafka vs RabbitMQ vs NATS
- [Spring @Transactional](spring-transaction-interview.md) — транзакции JMS
- [Spring Boot](spring-boot-interview.md) — auto-configuration для JMS/AMQP
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — паттерны асинхронной коммуникации
- [Resilience4j](resilience4j-interview.md) — circuit breaker для message consumers
- [Spring Retry](spring-retry-interview.md) — retry для message listeners
