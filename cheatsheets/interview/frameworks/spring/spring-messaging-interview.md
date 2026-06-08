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
updated: 2026-05-31
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

**Spring Messaging** — слой абстракции Spring над разными системами обмена сообщениями. Он вводит единую модель: тип `Message<T>` (тело + заголовки), `MessageChannel` для передачи и аннотации вроде `@MessageMapping`. Смысл — писать код продюсера/консьюмера одинаково, независимо от того, что под капотом: ActiveMQ, RabbitMQ или WebSocket. Конкретный брокер подключается отдельным стартером.

**Поддерживаемые транспорты** (каждый — свой модуль):

- **JMS** (Java Message Service) — ActiveMQ, IBM MQ, Oracle AQ; модуль `spring-jms`. Это стандарт Java EE, единый API для всех JMS-брокеров.
- **AMQP** (RabbitMQ) — модули `spring-amqp` / `spring-rabbit`. Даёт exchanges и routing keys, которых в JMS нет.
- **STOMP поверх WebSocket** — модуль `spring-websocket`; для push-сообщений в браузер.
- **Apache Kafka** — модуль `spring-kafka` (отдельный проект). Event streaming, а не классические очереди.
- **Redis Pub/Sub** — модуль `spring-data-redis`; лёгкий broadcast без гарантий доставки.

**Важно для собеседования:** под зонтиком «Spring Messaging» чаще всего имеют в виду именно JMS и AMQP — две классические очереди. Kafka и Redis формально тоже сюда относятся, но у них своя семантика (лог событий и fire-and-forget), поэтому их обычно обсуждают отдельно.

## Q2. Как настроить JMS и отправить сообщение?

Достаточно трёх шагов: подключить стартер, прописать брокер в `application.yml` и впрыснуть `JmsTemplate`. Spring Boot сам создаст `ConnectionFactory` и `JmsTemplate` через auto-configuration — настраивать их руками не нужно.

Отправка идёт через `convertAndSend`: объект автоматически сериализуется `MessageConverter`-ом (по умолчанию — Java-сериализация, в проде обычно меняют на JSON). Если нужно задать заголовки или TTL — передают `MessagePostProcessor` (лямбда, дорабатывающая `Message` перед отправкой).

**1. Зависимость:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

**2. Конфигурация** (адрес брокера, режим подтверждения, пул консьюмеров):

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

**3. Продюсер:**

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

## Q3. Как получать JMS-сообщения?

Метод помечается `@JmsListener(destination = "...")` — Spring сам подписывается на очередь, десериализует тело в тип параметра и вызывает метод на каждое сообщение. `concurrency = "3-10"` поднимает от 3 до 10 параллельных консьюмеров под нагрузку.

Из аргументов метода можно вытащить всё нужное:

- **`@Payload`** — тело сообщения (можно опустить, если параметр один).
- **`@Header("priority")`** — конкретный заголовок; **`@Headers Map`** — все заголовки.
- **`Message` / `Session`** — нативные JMS-объекты, когда нужен полный контроль.

**Подтверждение (ACK).** По умолчанию режим `auto`: Spring подтверждает сообщение сразу после успешного выхода из метода, при исключении — сообщение возвращается в очередь. Для точного контроля берут `session` и вручную вызывают `commit()` / `rollback()` (режим `client` или транзакционная сессия) — так сообщение не теряется при сбое посреди обработки.

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

## Q4. Как настроить RabbitMQ и отправить сообщение?

Ключевое отличие от JMS: в RabbitMQ продюсер шлёт **не в очередь напрямую, а в exchange** с routing key. Exchange по правилам binding-ов решает, в какие очереди положить сообщение. Поэтому при настройке описывают триаду: `Queue` + `Exchange` + `Binding`, который их связывает. Эти бины Spring AMQP сам объявит в брокере при старте (auto-declare).

`spring-boot-starter-amqp` поднимает `ConnectionFactory` и `RabbitTemplate`. Отправка — снова `convertAndSend`, но первый аргумент теперь имя exchange, второй — routing key.

**1. Зависимость:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**2. Конфигурация** (брокер + публикационные гарантии + пул консьюмеров):

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

**3. Топология** (очередь с привязанной DLQ через TTL, exchange и binding по шаблону):

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

**4. Публикация** (в exchange с routing key, заголовки задаёт `MessagePostProcessor`):

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

## Q5. Какие типы Exchange в RabbitMQ?

Exchange — это «маршрутизатор» между продюсером и очередями. Тип exchange определяет, **по какому правилу** сообщение раскладывается по очередям на основе binding-ов. Всего четыре типа:

| Тип | Маршрутизация | Применение |
|-----|---------------|------------|
| `direct` | По точному совпадению routing key | Роутинг по типу события |
| `topic` | По шаблону routing key (`*` / `#`) | Иерархические события (`orders.created.eu`) |
| `fanout` | Broadcast во все связанные очереди | Событийная модель pub-sub |
| `headers` | По заголовкам сообщения | Сложная маршрутизация без routing key |

Как читать шаблоны у `topic`: routing key — это слова через точку (`orders.created.eu`). В binding `*` заменяет **ровно одно** слово, `#` — **ноль или больше** слов. Поэтому `orders.*.eu` ловит `orders.created.eu`, но не `orders.created.de.eu`, а `orders.#` — вообще всё, что начинается на `orders`.

Практический выбор: `direct` — когда маршрут известен точно (статусы заказа); `topic` — когда нужна гибкая фильтрация по иерархии; `fanout` — широковещание (нотификации всем подписчикам); `headers` — редкий случай, когда маршрут зависит от нескольких атрибутов сразу, а не от одной строки.

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

## Q6. Как получать RabbitMQ сообщения?

Метод помечается `@RabbitListener(queues = "...")` — отличие от JMS в том, что подписка идёт на **очередь**, а не на destination. Дальше всё привычно: тело десериализуется в тип параметра, `concurrency` масштабирует консьюмеров.

Три типичных режима:

- **Авто-ACK** (по умолчанию) — Spring подтверждает после успешного выхода из метода; исключение возвращает сообщение в очередь.
- **`@SendTo`** — метод возвращает значение, и оно автоматически уходит в reply-очередь (request-reply, см. Q10).
- **Ручной ACK** (`ackMode = "MANUAL"`) — даёт точный контроль над судьбой сообщения. Принципиально различаются три исхода через `Channel`: `basicAck` (успех), `basicNack(..., requeue=true)` (вернуть в очередь — для временных сбоев) и `basicNack(..., requeue=false)` (отправить в DLQ — для неисправимых ошибок). Именно это разделение временных и фатальных ошибок и есть суть надёжной обработки.

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

## Q7. Как работает Dead Letter Queue (DLQ)?

**DLQ** — отдельная очередь, куда брокер автоматически складывает сообщения, которые не удалось обработать. Без неё «отравленное» сообщение либо теряется, либо бесконечно крутится в очереди, блокируя нормальные. DLQ изолирует проблемные сообщения, чтобы их можно было разобрать руками или отдельным процессом.

**Что отправляет сообщение в DLQ:**

- Превышен TTL (`x-message-ttl`) — сообщение протухло, не дождавшись обработки.
- Consumer отклонил с `requeue=false` (`basicNack`/`basicReject`) — то самое решение «это неисправимо» из Q6.
- Очередь переполнена (`x-max-length`) — лишние сообщения вытесняются.

**Как это устроено механически:** у основной очереди задают аргументы `x-dead-letter-exchange` и `x-dead-letter-routing-key`. Когда срабатывает одно из условий выше, RabbitMQ перепубликует сообщение в указанный DLX, а тот по routing key кладёт его в DLQ. То есть DLQ — это обычная очередь, просто привязанная к dead-letter exchange.

**Полезный нюанс:** при перемещении в DLQ RabbitMQ добавляет заголовок `x-death` с историей — сколько раз и почему сообщение «умирало». По нему в консьюмере DLQ видно первопричину и можно принять решение (алерт, ручной разбор, повторная отправка).

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

## Q8. Как работают транзакции в Spring Messaging?

Цель транзакции в messaging — связать обработку сообщения с записью в БД так, чтобы они подтверждались или откатывались **вместе**. Иначе возможна рассинхронизация: сообщение подтверждено (consumed), но запись в БД не сохранилась, или наоборот.

**JMS.** Если listener работает на транзакционном `ConnectionFactory` и помечен `@Transactional`, ACK сообщения попадает в ту же транзакцию, что и JPA. При исключении откатывается всё: JMS возвращает сообщение в очередь, JPA отменяет запись. Это и есть «обработать сообщение ровно при условии успешной записи».

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

**RabbitMQ.** Аналогично: `setChannelTransacted(true)` включает транзакционный канал, и публикация попадает в транзакцию Spring. Отправка в брокер «зафиксируется» только при коммите внешней транзакции.

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

**Подводный камень — это не настоящий 2PC.** Транзакция канала RabbitMQ и транзакция БД остаются **разными** ресурсами: между их коммитами есть зазор, где возможен сбой (БД закоммитилась, а publish — нет). AMQP вообще не поддерживает 2PC. JMS умеет через XA-транзакции с координатором, но XA сложен и медленен, и в микросервисах его избегают.

**Рекомендация:** для строгой гарантии «БД и сообщение согласованы» используют **Transactional Outbox Pattern** — сообщение пишется в таблицу-outbox в той же транзакции, что и бизнес-данные, а отдельный процесс дочитывает её и публикует в брокер. Это надёжнее и дешевле XA.

## Q9. Что такое Publisher Confirms в RabbitMQ?

**Publisher Confirms** — асинхронное подтверждение от брокера, что он принял сообщение и взял на себя ответственность за него. По умолчанию `convertAndSend` ничего не гарантирует: метод вернёт управление, даже если сообщение не дошло до брокера из-за обрыва сети, — и сообщение молча потеряется. Confirms закрывают именно эту дыру.

Здесь нужно различать **два разных колбэка**, потому что они ловят разные проблемы:

- **Confirm callback** (`setConfirmCallback`) — дошло ли сообщение **до брокера**. `ack=true` — принято; `ack=false` — брокер отказал (нет места, внутренняя ошибка), нужен retry или алерт.
- **Returns callback** (`setReturnsCallback`) — сообщение дошло до брокера, но exchange **не нашёл ни одной очереди** для него (нет подходящего binding-а). Без этого колбэка такие сообщения тихо отбрасываются.

`publisher-confirm-type: correlated` позволяет привязать к каждому сообщению `CorrelationData` (например, id заказа) — тогда в колбэке видно, какое именно сообщение подтвердилось или провалилось. Это основа надёжной публикации: продюсер знает судьбу каждого отправленного сообщения.

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

**Итог:** confirms — это разница между «надеюсь, дошло» и «знаю, что дошло». Для критичных сообщений (платежи, заказы) их включают всегда; для некритичной телеметрии можно опустить ради скорости.

## Q10. Что такое Request-Reply Pattern в Spring Messaging?

**Request-Reply** — синхронный по ощущениям вызов поверх асинхронного брокера: клиент шлёт запрос и **блокируется в ожидании ответа**, как при RPC, но транспорт — очередь. Полезно, когда нужен ответ (результат расчёта, валидация), но хочется развязать сервисы через брокер, а не звать друг друга напрямую по HTTP.

На клиенте используют `convertSendAndReceive` — он внутри делает всю магию. На сервере — обычный `@RabbitListener`, но метод **возвращает значение**, которое уходит обратно как ответ (`@SendTo` задаёт reply-очередь явно, но если в заголовке сообщения есть `replyTo`, ответ уйдёт туда автоматически).

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

**Как это работает под капотом:** клиент заводит временную (эксклюзивную) reply-очередь, кладёт её имя в заголовок `replyTo` и уникальный `correlationId`, затем блокируется. Сервер обрабатывает запрос, публикует ответ в `replyTo` с тем же `correlationId`. Клиент по `correlationId` сопоставляет ответ с конкретным запросом и разблокируется.

**Подводный камень:** это блокирующий вызов с таймаутом. Если сервер недоступен или медленный, клиент висит и тратит поток. Для долгих операций или высокого RPS лучше асинхронный fire-and-forget с отдельным колбэком, а не request-reply.

## Q11. Как настроить кастомный MessageConverter?

`MessageConverter` отвечает за то, как Java-объект превращается в байты сообщения (`toMessage`) и обратно (`fromMessage`). По умолчанию Spring AMQP использует Java-сериализацию — она хрупкая (требует одинаковых классов на обеих сторонах) и не читается другими языками. Поэтому в проде почти всегда ставят `Jackson2JsonMessageConverter`: JSON кросс-языковой, человекочитаемый и не ломается от мелких изменений класса.

**Зачем нужен `ClassMapper`:** при десериализации Jackson нужно знать, в какой класс восстанавливать JSON. По умолчанию имя класса берётся из заголовка `__TypeId__` — но если на стороне продюсера пакет другой, десериализация упадёт. `ClassMapper` явно сопоставляет логическое имя типа с классом, делая контракт стабильным между сервисами.

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

Свой конвертер пишут под бинарные форматы вроде Protobuf или Avro (компактнее JSON, со схемой): реализуют интерфейс `MessageConverter` и переопределяют оба метода.

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

## Q12. Как реализовать retry механизм?

Зачем retry: многие сбои **временные** (БД на секунду недоступна, сетевой таймаут). Мгновенно сбрасывать такое сообщение в DLQ расточительно — разумнее повторить через паузу. Spring AMQP даёт встроенный механизм на основе `RetryTemplate`.

**Как это собирается:**

- `maxAttempts(3)` — не больше трёх попыток, чтобы не зациклиться.
- `exponentialBackoff(1000, 2, 10000)` — пауза растёт: 1с, 2с, 4с… (множитель 2, потолок 10с). Экспоненциальный backoff не добивает уже перегруженный ресурс частыми повторами.
- `retryOn(...)` — повторять **только** на «лечащихся» исключениях; неисправимые ошибки нет смысла повторять.
- `recoverer` — что делать после исчерпания попыток. `RejectAndDontRequeueRecoverer` отклоняет сообщение с `requeue=false`, и оно уходит в DLQ (см. Q7).

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

**Компромисс — где живёт пауза.** Описанный `RetryTemplate` повторяет **внутри консьюмера**: поток блокируется на время backoff и не берёт другие сообщения. Для коротких пауз это нормально, для долгих — съедает пропускную способность.

**Альтернатива — delayed retry на стороне брокера:** сообщение с TTL кладут в «отстойник», по истечении TTL DLX возвращает его обратно в рабочую очередь (см. Q7). Пауза реализована очередями, поток консьюмера не простаивает — это масштабируется лучше при долгих задержках между попытками.

## Q13. Как тестировать Spring Messaging?

Главная трудность тестов на messaging — **асинхронность**: после отправки сообщения обработка происходит в другом потоке, и проверять результат сразу нельзя. Поэтому используют polling-ожидание (`Awaitility`: `await().untilAsserted(...)`) вместо `Thread.sleep` — оно дожидается условия и не флакает.

Два подхода к брокеру:

- **JMS — embedded ActiveMQ.** Брокер поднимается прямо в JVM теста, без Docker. Быстро, удобно для unit-уровня интеграции.
- **RabbitMQ — Testcontainers.** У RabbitMQ нет полноценного embedded-варианта, поэтому поднимают реальный брокер в Docker-контейнере и через `@DynamicPropertySource` подставляют его host/port в конфиг. Тест работает против настоящего RabbitMQ — максимально близко к проду.

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

## Q14. Чем Spring JMS отличается от Spring AMQP?

Корень всех различий — **разный уровень абстракции**. JMS — это Java-**стандарт** (API), под который подстраиваются вендоры брокеров; продюсер шлёт прямо в очередь или топик. AMQP — это сетевой **протокол** с богатой моделью маршрутизации: между продюсером и очередью всегда стоит exchange с routing key. Отсюда и таблица отличий:

| Критерий | JMS (ActiveMQ) | AMQP (RabbitMQ) |
|----------|----------------|-----------------|
| Стандарт | Java EE | Open protocol |
| Exchanges | Нет (direct queue) | Да (direct/topic/fanout/headers) |
| Routing | Queue name + selector | Routing key + exchange |
| Message model | Queue или Topic | Exchange → Queue(s) |
| Вендоры | Один стандарт, разные реализации | Специфично для AMQP брокеров |
| Спецификация | JMS 2.0 | AMQP 0.9.1 / 1.0 |
| Полезность | Legacy enterprise | Современные микросервисы |

**Практический вывод:** JMS берут, когда уже есть Java EE-инфраструктура или корпоративный брокер (IBM MQ) и нужна совместимость со стандартом. AMQP/RabbitMQ — когда важна гибкая маршрутизация (exchanges, routing keys) и брокер выбирают с нуля под микросервисы. Ключевое: маршрутизацию в JMS делают селекторами и именами очередей, в AMQP — связкой exchange + routing key, что мощнее и нагляднее.

## Q15. Когда использовать Spring Messaging vs Kafka?

Фундаментальное различие в модели: **очередь (JMS/AMQP) — это «задача», лог (Kafka) — это «факт»**. В очереди сообщение доставляется одному консьюмеру и после ACK исчезает (queue semantics) — идеально для команд и задач, выполняемых ровно один раз. Kafka — это append-only лог: сообщение не удаляется после чтения, его независимо читают сколько угодно consumer groups и можно перечитать историю (replay). Из этой разницы и вытекает выбор:

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

**Практика — это не «или/или».** В современных системах часто живут оба: RabbitMQ для task queues (распределение долгих job-ов между воркерами), Kafka для event streaming и audit logs (события, которые нужно перечитывать и отдавать многим потребителям). Выбор делают по характеру данных, а не по моде.

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
