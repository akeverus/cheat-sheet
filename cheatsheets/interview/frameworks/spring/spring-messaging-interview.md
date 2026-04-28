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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Как настроить JMS и отправить сообщение? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как получать JMS-сообщения? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Как настроить RabbitMQ и отправить сообщение? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Какие типы Exchange в RabbitMQ? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как получать RabbitMQ сообщения? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как работает Dead Letter Queue (DLQ)? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Как работают транзакции в Spring Messaging? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Что такое Publisher Confirms в RabbitMQ? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Что такое Request-Reply Pattern в Spring Messaging? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как настроить кастомный MessageConverter? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как реализовать retry механизм? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как тестировать Spring Messaging? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Чем Spring JMS отличается от Spring AMQP? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Когда использовать Spring Messaging vs Kafka? Частая ошибка в реальном коде.

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

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Spring Kafka](spring-kafka-interview.md) — интеграция Kafka в Spring Частая ошибка в реальном коде.
- [Spring Integration](spring-integration-interview.md) — EIP паттерны в Spring
- [RabbitMQ](../../messaging/rabbitmq-interview.md) — основы AMQP, exchanges, queues, routing keys
- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka, partitions, consumer groups
- [Message Brokers Comparison](../../messaging/message-brokers-comparison-interview.md) — Kafka vs RabbitMQ vs NATS
- [Spring @Transactional](spring-transaction-interview.md) — транзакции JMS
- [Spring Boot](spring-boot-interview.md) — auto-configuration для JMS/AMQP
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — паттерны асинхронной коммуникации
- [Resilience4j](resilience4j-interview.md) — circuit breaker для message consumers
- [Spring Retry](spring-retry-interview.md) — retry для message listeners
