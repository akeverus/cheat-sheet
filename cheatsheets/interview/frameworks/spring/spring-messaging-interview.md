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

В теме рассматриваются общая модель Spring Messaging и две интеграции с брокерами: Spring JMS для Jakarta Messaging и Spring AMQP для RabbitMQ. Примеры рассчитаны на Spring Boot 3.5, Spring Framework 6.2, Spring AMQP 3.2 и RabbitMQ по AMQP 0-9-1. Отличия других версий указаны в соответствующих ответах.

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

**Spring Messaging** – модуль Spring Framework с общими контрактами для обмена сообщениями. `Message<T>` хранит тело и заголовки, `MessageChannel` задаёт контракт отправки, а `@MessageMapping` связывает входящее сообщение с методом обработчика в поддерживающей эту аннотацию инфраструктуре.

Подключение к конкретной системе требует своего модуля и настройки:

- **Jakarta Messaging (JMS)** – `spring-jms`, `JmsTemplate` и `@JmsListener` для совместимых брокеров, например ActiveMQ. Spring Framework 6 использует пространство имён `jakarta.jms`.
- **RabbitMQ по AMQP 0-9-1** – Spring AMQP, `RabbitTemplate` и `@RabbitListener`. Здесь приложение настраивает exchange, routing key и очередь.
- **STOMP поверх WebSocket** – `spring-messaging` вместе с `spring-websocket`. Spring может обслуживать подписки встроенным простым брокером или передавать сообщения внешнему STOMP-брокеру.
- **Kafka** и **Redis Pub/Sub** подключают отдельными интеграциями Spring for Apache Kafka и Spring Data Redis.

Например, при переносе обработки заказов с JMS на RabbitMQ бизнес-метод может остаться прежним. Точки отправки и приёма, маршрутизацию и поведение при ошибке нужно настроить для нового брокера. Общая модель сообщения помогает передать данные в бизнес-метод, а гарантии доставки определяет выбранная интеграция.

Источники – [Spring Framework 6.2 Messaging API](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/messaging/package-summary.html), [Spring Framework 6.2 STOMP](https://docs.spring.io/spring-framework/reference/6.2/web/websocket/stomp/enable.html).

## Q2. Как настроить JMS и отправить сообщение?

```text
Spring Boot 3.5, внешний ActiveMQ Classic по tcp://localhost:61616.
Публикуем JSON заказа в orders-queue. Нужны прикладное свойство
priority = HIGH и срок жизни сообщения 60 секунд.
Что подключить и на каком уровне задать эти параметры?
```

Достаточно трёх шагов: подключить стартер, прописать брокер в `application.yml` и внедрить `JmsTemplate`. Spring Boot сам создаст `ConnectionFactory` и `JmsTemplate` через auto-configuration – настраивать их руками не нужно.

Отправка идёт через `convertAndSend`: `SimpleMessageConverter` преобразует строку в `TextMessage`, массив байтов в `BytesMessage`, а подходящий `Serializable`-объект в `ObjectMessage`. В примере ниже отправляем JSON строкой, чтобы не зависеть от Java-сериализации класса `Order`. Если нужно задать заголовки (properties) – передают `MessagePostProcessor` (лямбда, дорабатывающая `Message` перед отправкой). А вот TTL так задать **нельзя** – см. нюанс после примера.

**1. Зависимость:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

**2. Конфигурация** (адрес брокера, очередь по умолчанию и TTL для отправки):

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: ${ACTIVEMQ_USER}
    password: ${ACTIVEMQ_PASSWORD}
  jms:
    template:
      default-destination: orders-queue
      time-to-live: 60s
```

**3. Продюсер:**

```java
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final JmsTemplate jmsTemplate;

    public void sendOrder(String orderJson) {
        jmsTemplate.convertAndSend(orderJson);
    }

    public void sendWithHeaders(String orderJson) {
        jmsTemplate.convertAndSend("orders-queue", orderJson, message -> {
            message.setStringProperty("priority", "HIGH");
            return message;
        });
    }
}
```

**TTL и приоритет.** При наличии стандартной автоконфигурации Boot 3.5 свойство `spring.jms.template.time-to-live` включает явный QoS для шаблона, если его не отключили явно. При ручной настройке `JmsTemplate` задайте `setTimeToLive(60_000)` и `setExplicitQosEnabled(true)`. Провайдер вычисляет `JMSExpiration` при отправке из TTL. Запись этого заголовка в `MessagePostProcessor` TTL не меняет. Прикладное свойство `priority` можно использовать в selector, а приоритет доставки задают через JMS QoS.

Документация: [Spring Boot 3.5 JMS](https://docs.spring.io/spring-boot/3.5/reference/messaging/jms.html), [свойства Boot 3.5](https://docs.spring.io/spring-boot/3.5/appendix/application-properties/index.html), [Jakarta Messaging 3.1](https://jakarta.ee/specifications/messaging/3.1/jakarta-messaging-spec-3.1.html).

## Q3. Как получать JMS-сообщения?

```text
Spring Framework 6.2, очередь orders-queue, сообщение TextMessage с JSON.
Обработчик завершает работу исключением RuntimeException.
Случай A: DefaultMessageListenerContainer без транзакции, AUTO_ACKNOWLEDGE.
Случай B: стандартная JMS-фабрика Spring Boot 3.5 без JTA.
Как подключить обработчик и что делает каждый контейнер при сбое?
```

Метод помечается `@JmsListener(destination = "...")` – Spring сам подписывается на очередь, преобразует тело через настроенный конвертер и вызывает метод на каждое сообщение. Для очереди с `DefaultMessageListenerContainer` настройка `concurrency = "3-10"` задаёт три базовых потребителя и предел масштабирования в десять.

Из аргументов метода можно вытащить всё нужное:

- `@Payload` связывает параметр с телом сообщения. При одном параметре для тела аннотацию можно опустить.
- `@Header("priority")` извлекает прикладное свойство с этим именем. `@Headers Map` предоставляет все заголовки.
- **`Message` / `Session`** — нативные JMS-объекты, когда нужен полный контроль.

**Подтверждение JMS.** Для нетранзакционного `DefaultMessageListenerContainer` Spring Framework режим `AUTO_ACKNOWLEDGE` подтверждает сообщение до вызова обработчика. Исключение обработчика само по себе не приводит к повторной доставке. При `CLIENT_ACKNOWLEDGE` контейнер подтверждает после успешного вызова и пытается обеспечить повторную доставку при ошибке.

В стандартной JMS-автоконфигурации Spring Boot 3.5 контейнер транзакционный: если доступен `JtaTransactionManager`, Boot связывает его с фабрикой, иначе включает `sessionTransacted`. С локальной транзакцией контейнер фиксирует сессию после успешного вызова и откатывает её при исключении. Дальнейшая судьба сообщения зависит от политики брокера, включая задержку, предел попыток и перевод в очередь недоставленных сообщений. [Spring JMS Listener Container](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jms/listener/AbstractMessageListenerContainer.html) · [Spring Boot 3.5 JMS](https://docs.spring.io/spring-boot/3.5/reference/messaging/jms.html).

Продюсер из Q2 передаёт JSON-строку. Стандартный JMS `SimpleMessageConverter` восстанавливает из `TextMessage` строку. В примере ниже JSON разбирает `ObjectMapper`. Чтобы принимать `Order` напрямую, нужно настроить совместимое преобразование в фабрике слушателей. [Spring Framework 6.2 SimpleMessageConverter](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jms/support/converter/SimpleMessageConverter.html).

```java
@Component
@RequiredArgsConstructor
public class OrderConsumer {
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "orders-queue", concurrency = "3-10")
    public void handleOrder(String orderJson) throws JsonProcessingException {
        Order order = objectMapper.readValue(orderJson, Order.class);
        log.info("Received: {}", order);
        processOrder(order);
    }

    // С доступом к заголовкам
    @JmsListener(destination = "orders-queue")
    public void handleWithHeaders(
            @Payload String orderJson,
            @Header(name = "priority", required = false) String priority,
            @Headers Map<String, Object> allHeaders,
            jakarta.jms.Message jmsMessage) throws JsonProcessingException {
        Order order = objectMapper.readValue(orderJson, Order.class);
        log.info("Priority={}, Order={}", priority, order);
        processOrder(order);
    }

}
```

В примере `priority` необязателен: `sendOrder` из Q2 его не задаёт. При транзакционной фабрике исключение из разбора JSON или из `processOrder` должно выйти из слушателя. Контейнер управляет `commit` и `rollback`. Два обработчика одной очереди в примере показывают альтернативные сигнатуры. Если зарегистрировать оба, они будут конкурировать за сообщения.

## Q4. Как настроить RabbitMQ и отправить сообщение?

```text
Spring Boot 3.5, RabbitMQ доступен по localhost:5672.
Планируемые имена: orders.exchange и orders.queue. Топологии пока нет.
В очередь должны попадать события с ключами по шаблону orders.created.#.
Есть JSON заказа и прикладной заголовок source = api.
Что нужно объявить и какие аргументы передать при публикации?
```

Ключевое отличие от JMS: в RabbitMQ продюсер шлёт **не в очередь напрямую, а в exchange** с routing key. Exchange по правилам binding-ов решает, в какие очереди положить сообщение. Для собственной топологии объявляют `Queue`, `Exchange` и связывающий их `Binding`. Публикация по имени очереди использует встроенный default exchange (Q5). При включённом auto-declare `RabbitAdmin` объявляет эти бины при открытии соединения с брокером.

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
    publisher-confirm-type: correlated  # подтверждение от брокера
    publisher-returns: true              # уведомления о возврате
    template:
      mandatory: true                     # возврат при отсутствии маршрута
    listener:
      simple:
        acknowledge-mode: auto
        concurrency: 3
        max-concurrency: 10
```

**3. Топология** (очередь с TTL, exchange и binding по шаблону. Объявления DLX, DLQ и их binding приведены в Q7. Определение `ordersQueue` из Q7 повторно регистрировать не нужно):

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

**4. Публикация.** Для примера на Boot 3.5 зарегистрируйте JSON-конвертер из Q11. Он сериализует `Order` и задаёт медиатип. `MessagePostProcessor` добавляет прикладной заголовок после конвертации.

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
                message.getMessageProperties().setHeader("source", "api");
                return message;
            });
    }
}
```

Источники: [Spring Boot 3.5 – AMQP](https://docs.spring.io/spring-boot/3.5/reference/messaging/amqp.html), [Spring AMQP 3.2 – Configuring the Broker](https://docs.spring.io/spring-amqp/reference/3.2/amqp/broker-configuration.html).

## Q5. Какие типы Exchange в RabbitMQ?

```text
RabbitMQ, AMQP 0-9-1. Четыре независимые задачи:
A. Сопоставлять routing key с полным ключом привязки буквально,
   даже когда в нём встречаются символы * или #.
B. Доставить события orders.created.eu и orders.cancelled.eu по общему шаблону.
C. Отправить копию во все подключённые очереди.
D. Выбрать маршрут по region = EU и format = json.
Какой базовый тип exchange выбрать для каждой задачи?
```

Exchange – это «маршрутизатор» между продюсером и очередями. Тип exchange определяет, **по какому правилу** сообщение раскладывается по очередям на основе binding-ов. Четыре базовых типа AMQP 0-9-1:

| Тип | Маршрутизация | Применение |
|-----|---------------|------------|
| `direct` | По точному совпадению routing key | Роутинг по типу события |
| `topic` | По шаблону routing key (`*` / `#`) | Иерархические события (`orders.created.eu`) |
| `fanout` | Broadcast во все связанные очереди | Событийная модель pub-sub |
| `headers` | По заголовкам сообщения | Сложная маршрутизация без routing key |

Как читать шаблоны у `topic`: routing key – это слова через точку (`orders.created.eu`). В binding `*` заменяет **ровно одно** слово, `#` – **ноль или больше** слов. Поэтому `orders.*.eu` ловит `orders.created.eu`, но не `orders.created.de.eu`, а `orders.#` – ключ `orders` и ключи, начинающиеся с сегмента `orders`, например `orders.created`. Ключ `orders2.created` не подходит.

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

**Default exchange** – встроенный `direct` exchange с именем `""` при публикации. RabbitMQ автоматически привязывает к нему каждую объявленную очередь с ключом, равным имени очереди. Например, `rabbitTemplate.convertAndSend("", "orders.queue", body)` маршрутизирует сообщение в существующую очередь `orders.queue` без собственного binding. RabbitMQ также поддерживает дополнительные типы exchange.

Источник – [RabbitMQ Exchanges](https://www.rabbitmq.com/docs/exchanges).

## Q6. Как получать RabbitMQ сообщения?

```text
Spring AMQP 3.2, RabbitMQ, очередь orders.queue с рабочим маршрутом в DLQ.
Один обработчик получает заказ. Возможны успешная обработка,
временная ошибка и ошибка, при которой повтор не поможет.
Как задать исход доставки вручную для каждого случая?
От чего зависит возврат в очередь при исключении в режиме AUTO?
```

Метод помечается `@RabbitListener(queues = "...")` – он подписывает обработчик на очередь RabbitMQ. Конвертер адаптирует тело сообщения к параметру метода. Для `SimpleMessageListenerContainer` настройка `concurrency = "3-10"` задаёт диапазон числа потребителей.

Подтверждение в контейнере и отправка ответа настраиваются отдельно:

- **Авто-ACK** (по умолчанию) – Spring подтверждает после успешного выхода из метода; при исключении решение о возврате зависит от `defaultRequeueRejected` и классификации ошибки. Например, `AmqpRejectAndDontRequeueException` исключает возврат. [Spring AMQP 3.2 Exception Handling](https://docs.spring.io/spring-amqp/reference/3.2/amqp/exception-handling.html).
- **Ручной ACK** (`ackMode = "MANUAL"`) – даёт точный контроль над судьбой сообщения. Принципиально различаются три исхода через `Channel`: `basicAck` (успех), `basicNack(..., requeue=true)` (вернуть в очередь – для временных сбоев) и `basicNack(..., requeue=false)` (отклонить без возврата. DLQ получит сообщение при настроенном DLX и подходящем маршруте, см. Q7). Именно это разделение временных и фатальных ошибок и есть суть надёжной обработки.

Режим `NONE` соответствует автоматическому подтверждению доставки самим брокером без ожидания ACK от клиента. `@SendTo` задаёт адрес для ответа, когда в запросе нет `replyTo` (см. Q10).

Обычный и ручной обработчики одной очереди показаны как альтернативы. Если зарегистрировать оба, они будут конкурировать за доставки.

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
            channel.basicNack(tag, false, false); // отклонить без возврата
        }
    }
}
```

## Q7. Как работает Dead Letter Queue (DLQ)?

```text
RabbitMQ, AMQP 0-9-1, classic queue orders.queue.
A. Слушатель отклоняет доставку с requeue=false.
B. Истекает TTL сообщения, пока оно находится в очереди.
C. Новая публикация превышает x-max-length; overflow = drop-head.
Есть очередь orders.dlq. Какие настройки нужны для доставки в неё?
Что изменится в C при overflow = reject-publish?
```

**DLQ** – обычная очередь, привязанная к dead-letter exchange (DLX). Она позволяет отдельно разбирать сообщения, которые брокер исключил из основной очереди.

RabbitMQ выполняет dead lettering при истечении TTL сообщения, отклонении с `requeue=false`, вытеснении из-за лимита длины или превышении `delivery-limit` у quorum queue. При стандартном `drop-head` переполнение вытесняет старые сообщения. Для classic queue в условии `reject-publish` отклоняет новую публикацию без отправки в DLX. В режиме `reject-publish-dlx` брокер также отправляет отклонённую публикацию в настроенный DLX. Quorum queue не поддерживает `reject-publish-dlx`.

У основной очереди задают DLX и при необходимости отдельный ключ через политику либо аргументы из примера. Для попадания сообщения в DLQ нужны существующий DLX и маршрут к очереди. При `nack` с `requeue=false` без DLX сообщение отбрасывается. При отсутствии отдельного ключа RabbitMQ использует исходные ключи маршрутизации. Наличие маршрута само по себе не гарантирует доставку при сбое приёмной очереди. Для quorum queue можно отдельно настроить режим at-least-once dead lettering. [RabbitMQ Dead Letter Exchanges](https://www.rabbitmq.com/docs/dlx) · [Queue Length Limit](https://www.rabbitmq.com/docs/maxlength).

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

```text
Два независимых запуска. В каждом метод вызывается через прокси
с активной локальной транзакцией JpaTransactionManager; XA не настроен.

A. JMS-слушатель получает заказ в контейнере с sessionTransacted=true
   и сохраняет заказ через JPA.
B. Сервис сохраняет заказ через JPA и на том же потоке вызывает
   RabbitTemplate с channelTransacted=true.

Какие ресурсы участвуют в каждой транзакции?
Что может произойти при отказе после фиксации БД?
```

**JMS.** При `sessionTransacted=true` контейнер управляет локальной JMS-транзакцией. Она охватывает получение сообщения и отправку через ту же JMS-сессию. Запись через JPA выполняется в отдельной транзакции БД. В примере при исключении, подпадающем под правила rollback JPA и вышедшем из обработчика, БД откатывается, затем контейнер откатывает JMS-сессию. При успехе JPA может зафиксироваться раньше подтверждения JMS. Сбой между коммитами допускает повторную обработку уже записанного заказа. Обработчик должен учитывать дубликаты. Общая транзакция требует XA-совместимых ресурсов и JTA-координатора. [Spring JMS – обработка транзакций](https://docs.spring.io/spring-framework/reference/6.2/integration/jms/receiving.html).

```java
// transactionalJmsFactory настроена с sessionTransacted=true.
// @Transactional выбирает менеджер транзакций JPA.
@JmsListener(destination = "orders-queue", containerFactory = "transactionalJmsFactory")
@Transactional("jpaTransactionManager")
public void process(String orderJson) throws JsonProcessingException {
    Order order = objectMapper.readValue(orderJson, Order.class);
    orderRepository.save(order);
}
```

**RabbitMQ.** В этом отдельном примере используйте бин `transactionalRabbitTemplate` и фабрику соединений без publisher confirms. Транзакции канала и confirms относятся к разным режимам работы канала. Для публикации объекта `Order` передайте JSON-конвертер из Q11 и объявите указанный exchange и binding. `setChannelTransacted(true)` включает локальную транзакцию канала. Без активной внешней транзакции шаблон завершает транзакцию канала в рамках операции. Если на вызывающем потоке активна транзакция Spring и канал синхронизирован с ней, завершение транзакции RabbitMQ откладывается до завершения внешней транзакции. [Spring AMQP 3.2 Transactions](https://docs.spring.io/spring-amqp/reference/3.2/amqp/transactions.html).

```java
// RabbitMQ транзакции
@Bean
public RabbitTemplate transactionalRabbitTemplate(
        ConnectionFactory cf, MessageConverter converter) {
    RabbitTemplate template = new RabbitTemplate(cf);
    template.setMessageConverter(converter);
    template.setChannelTransacted(true);
    return template;
}

@Transactional("jpaTransactionManager")
public void processAndPublish(OrderCommand cmd) {
    Order order = orderRepository.save(new Order(cmd));
    transactionalRabbitTemplate.convertAndSend("orders.exchange", "orders.created.eu", order);
}
```

**Окно отказа.** Коммиты БД и канала отдельные. Если БД зафиксировалась, а коммит RabbitMQ после неё завершился ошибкой, запись в БД останется, а результат публикации может быть неизвестен.

**Рекомендация:** для строгой гарантии «БД и сообщение согласованы» используют **Transactional Outbox Pattern** – сообщение пишется в таблицу-outbox в той же транзакции, что и бизнес-данные, а отдельный процесс дочитывает её и публикует в брокер. Публикация из outbox допускает повторную отправку. Потребителю нужна идемпотентная обработка.

## Q9. Что такое Publisher Confirms в RabbitMQ?

```text
Spring AMQP 3.2, RabbitMQ. Exchange orders.exchange существует,
единственная привязка принимает ключи orders.created.#.
Publisher confirms в режиме correlated, publisher returns и mandatory включены.
Публикация A: orders.created.eu. Публикация B: orders.deleted.eu.
Очередь доступна, внутреннего сбоя брокера нет.
Какие сигналы получит издатель и что они говорят об обработке потребителем?
```

**Publisher Confirms** сообщают издателю результат публикации со стороны RabbitMQ. Сам возврат из `convertAndSend` такого результата не даёт.

- **Confirm callback** связывает `ack` или `nack` с публикацией через `CorrelationData`. В условии A получает `ack`, B получает и `basic.return` с `NO_ROUTE`, и `ack`: брокер обработал публикацию B и установил отсутствие маршрута. При закрытии канала или соединения результат некоторых отправок может остаться неизвестным. Повтор в таком случае может создать дубликат.
- **Returns callback** получает публикацию, которую exchange не смог направить ни в одну очередь, при включённых publisher returns и флаге `mandatory`. Для одной публикации возможны и return, и положительный confirm. Отсутствие exchange приводит к ошибке канала, а не к return о ненайденной очереди.
- **Consumer ACK** относится к обработке доставки потребителем. Publisher confirm её не подтверждает.

[Spring AMQP 3.2: Confirms and Returns](https://docs.spring.io/spring-amqp/reference/3.2/amqp/template.html) · [RabbitMQ – Publisher Confirms](https://www.rabbitmq.com/docs/confirms).

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
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
        CorrelationData correlation = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("orders.exchange", "orders.created",
            order, correlation);
    }
}
```

Вызов `sendReliably` инициирует публикацию. Прикладной результат определяют по confirm и return, а успешную обработку потребителем отслеживают отдельно. Пример использует нетранзакционный канал. Для request-reply из Q10 используйте шаблон без установленного здесь `ReturnsCallback`, если хотите, чтобы возврат немаршрутизированного запроса преобразовался в исключение ожидающего вызова.

## Q10. Что такое Request-Reply Pattern в Spring Messaging?

```text
Spring AMQP 3.2, RabbitMQ, очередь orders.requests.
Клиент отправляет OrderRequest, сервис рассчитывает OrderResult.
Одновременно отправлены запросы A и B. Сервис может ответить
в ином порядке или не успеть за 5 секунд.
Как связать каждый ответ с запросом и что увидит клиент при таймауте?
```

**Request-Reply** связывает запрос через брокер с ответом сервиса. В синхронном варианте клиент блокируется до получения результата или таймаута. Асинхронное ожидание сохраняет ту же связь запроса и ответа.

В синхронном примере `RabbitTemplate.convertSendAndReceive` конвертирует запрос и ответ, настраивает адрес ответа и сопоставляет его с ожидающим вызовом. На сервере `@RabbitListener` возвращает результат, который Spring отправляет по адресу `replyTo` из запроса. `@SendTo` может задать резервный адрес для сообщений без `replyTo`.

Пример использует очередь `orders.requests` и default exchange из Q5. Объявите очередь через `RabbitAdmin`, как в Q4, или заранее создайте её в брокере. На клиенте и сервере настройте совместимый JSON-конвертер по Q11 для `OrderRequest` и `OrderResult`.

```java
// Синхронный вызов через RabbitTemplate
@Service
@RequiredArgsConstructor
public class OrderClient {
    private final RabbitTemplate rabbitTemplate;

    public OrderResult requestOrder(OrderRequest request) {
        OrderResult result = (OrderResult) rabbitTemplate.convertSendAndReceive(
            "",
            "orders.requests",
            request
        );
        if (result == null) {
            throw new IllegalStateException("Order response timed out");
        }
        return result;
    }
}

// Серверная сторона
@RabbitListener(queues = "orders.requests")
public OrderResult handleRequest(OrderRequest request) {
    return orderService.process(request);
}
```

**Как это работает под капотом (классическая схема):** клиент заводит временную (эксклюзивную) reply-очередь, кладёт её имя в заголовок `replyTo` и уникальный `correlationId`, затем блокируется. Сервер обрабатывает запрос, публикует ответ в `replyTo` с тем же `correlationId`. Клиент по `correlationId` сопоставляет ответ с конкретным запросом и разблокируется.

**Direct Reply-To — современный дефолт.** Актуальный `RabbitTemplate.sendAndReceive` / `convertSendAndReceive` по умолчанию использует **не** временную очередь, а механизм RabbitMQ **Direct Reply-To**: в заголовок `replyTo` подставляется псевдоочередь `amq.rabbitmq.reply-to`. Реальная очередь при этом не объявляется вовсе — брокер доставляет ответ напрямую в канал, с которого пришёл запрос. Это заметно дешевле: нет накладных расходов на создание и удаление временной очереди на каждый запрос. Spring AMQP включает Direct Reply-To автоматически, если брокер его поддерживает (RabbitMQ 3.4+); вернуться к временным очередям можно через `setUseTemporaryReplyQueues(true)`, а фиксированную reply-очередь задают через `setReplyAddress(...)` с контейнером-слушателем.

**Потеря ответа и таймаут.** Direct Reply-To доставляет ответы по модели at-most-once. Брокер их не буферизует. Если соединение оборвалось после обработки заказа, клиент может остаться без ответа. По умолчанию `RabbitTemplate` ждёт ответ до 5 секунд и при истечении срока возвращает `null`. Таймаут сам по себе оставляет результат операции неизвестным. Для отдельного обнаружения отсутствующего маршрута в Spring AMQP 3.2 включите publisher returns и `mandatory`. На шаблоне request-reply без собственного `ReturnsCallback` возврат немаршрутизированного запроса приводит к `AmqpMessageReturnedException` вместо ожидания до таймаута. Для безопасного повтора передайте тот же идентификатор операции и дедуплицируйте обработку на стороне сервиса. Если ответ должен пережить разрыв соединения, рассмотрите отдельную очередь с подходящими настройками хранения и подтверждения. `AsyncRabbitTemplate` позволяет ждать ответ без блокировки потока вызывающего кода, сохраняя протокол request-reply.

Источники: [Spring AMQP 3.2 – Request/Reply Messaging](https://docs.spring.io/spring-amqp/reference/3.2/amqp/request-reply.html), [RabbitMQ – Direct Reply-To](https://www.rabbitmq.com/docs/direct-reply-to).

## Q11. Как настроить кастомный MessageConverter?

```text
Spring Boot 3.5, Spring AMQP 3.2.
Издатель работает с shop.Order, получатель с warehouse.Order.
DTO имеют одинаковую структуру JSON, тип в обоих сервисах
должен обозначаться в сообщении строкой order.
Как настроить преобразование тела и указать соответствие типов?
```

`MessageConverter` преобразует объект в тело AMQP-сообщения и обратно. По умолчанию `SimpleMessageConverter` обрабатывает текст, массивы байтов и объекты `Serializable`. Смена `contentType` после конвертации не меняет байты тела.

В Spring Boot 3.5 со Spring AMQP 3.2 для JSON можно объявить один бин `Jackson2JsonMessageConverter`. Автоконфигурация подключит его к шаблону и фабрике обработчиков по умолчанию. При собственных шаблоне или фабрике передайте нужный конвертер явно. В Spring AMQP 4.x используйте `JacksonJsonMessageConverter` для Jackson 3.

```java
@Bean
public MessageConverter jsonMessageConverter() {
    DefaultClassMapper mapper = new DefaultClassMapper();
    mapper.setIdClassMapping(Map.of(
        "order", Order.class,
        "order-request", OrderRequest.class,
        "order-result", OrderResult.class
    ));
    mapper.afterPropertiesSet();

    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    converter.setClassMapper(mapper);
    return converter;
}
```

В примере `Order` из Q4 и оба DTO из Q10 имеют логические идентификаторы для заголовка `__TypeId__`. Поскольку mapper создаётся внутри фабричного метода и сам не является Spring-бином, вызов `afterPropertiesSet()` строит обратное отображение из Java-класса в идентификатор. На принимающей стороне сопоставьте те же идентификаторы с локальными DTO с совместимой структурой JSON. Для конвертера без явного `ClassMapper` Spring AMQP также может вывести целевой тип из конкретного параметра метода `@RabbitListener`. Сопоставление типов и совместимость полей JSON требуют отдельного контракта.

Для Protobuf или Avro можно реализовать `MessageConverter` самостоятельно. Например, при одной схеме Protobuf `toMessage` записывает `OrderEvent.toByteArray()` с согласованным медиатипом, а `fromMessage` проверяет медиатип и вызывает `OrderEvent.parseFrom(message.getBody())`. Ошибку разбора оберните в `MessageConversionException`. Если на маршруте несколько схем, задайте способ однозначно выбрать версию и тип сообщения.

Источники – [Spring AMQP 3.2 Message Converters](https://docs.spring.io/spring-amqp/reference/3.2/amqp/message-converters.html), [исходник DefaultClassMapper в ветке 3.2.x](https://raw.githubusercontent.com/spring-projects/spring-amqp/3.2.x/spring-amqp/src/main/java/org/springframework/amqp/support/converter/DefaultClassMapper.java).

## Q12. Как реализовать retry механизм?

```text
Spring AMQP 3.2, Spring Retry, слушатель orders.queue.
Для одной доставки разрешено максимум три вызова обработчика, включая первоначальный.
Повторять только при TransientException, в том числе в cause.
Первая пауза 1 с, множитель 2, потолок 10 с.
При окончательном отказе доставка не должна возвращаться в рабочую очередь.
Как настроить обработку и сколько пауз будет при трёх сбоях подряд?
```

Зачем retry: многие сбои **временные** (БД на секунду недоступна, сетевой таймаут). Мгновенно сбрасывать такое сообщение в DLQ расточительно – разумнее повторить через паузу. Пример рассчитан на Spring AMQP 3.2 и Spring Retry.

**Как это собирается:**

- `maxAttempts(3)` — не больше трёх попыток, чтобы не зациклиться.
- `exponentialBackoff(1000, 2, 10000)` – при трёх попытках возможны две паузы: 1 и 2 секунды. Для более длинной серии интервал удваивается до потолка в 10 секунд. Экспоненциальный backoff не добивает уже перегруженный ресурс частыми повторами.
- `retryOn(...)` — повторять **только** на «лечащихся» исключениях; неисправимые ошибки нет смысла повторять.
- `recoverer` – что делать после исчерпания попыток. `RejectAndDontRequeueRecoverer` отклоняет сообщение с `requeue=false`, после исчерпания разрешённых попыток или при неповторяемой ошибке. Для доставки в DLQ нужен настроенный DLX и маршрут (см. Q7).

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
        .traversingCauses()
        .build();

    factory.setAdviceChain(RetryInterceptorBuilder.stateless()
        .retryOperations(retryTemplate)
        .recoverer(new RejectAndDontRequeueRecoverer())  // отклонить без requeue
        .build());

    return factory;
}

@RabbitListener(queues = "orders.queue", containerFactory = "retryContainerFactory")
public void handleOrder(Order order) {
    orderService.process(order);  // при ошибке — retry с backoff
}
```

**Граница транзакции.** Stateless retry подходит, когда обработка идёт без внешней транзакции либо каждая попытка открывает свою транзакцию внутри callback. Если транзакция уже начата снаружи и после ошибки должна откатиться, используйте stateful retry со стабильным уникальным ключом сообщения, например `messageId`. Тогда очередная доставка продолжает учёт попыток после отката предыдущей. [Spring AMQP 3.2 – Retry and Recovery](https://docs.spring.io/spring-amqp/reference/3.2/amqp/resilience-recovering-from-errors-and-broker-failures.html).

**Компромисс — где живёт пауза.** Описанный `RetryTemplate` повторяет **внутри консьюмера**: поток блокируется на время backoff и не берёт другие сообщения. Для коротких пауз это нормально, для долгих — съедает пропускную способность.

**Альтернатива — delayed retry на стороне брокера:** сообщение с TTL кладут в «отстойник», по истечении TTL DLX возвращает его обратно в рабочую очередь (см. Q7). Пауза реализована очередями, поток консьюмера не простаивает — это масштабируется лучше при долгих задержках между попытками.

## Q13. Как тестировать Spring Messaging?

```text
Spring Boot 3.5, два интеграционных теста.
A. JMS: ActiveMQ Classic должен работать в JVM теста, внешнего брокера нет.
B. AMQP: тесту доступен Docker, постоянного RabbitMQ нет.
Нужно проверить обмен через реальный брокер. У отдельного теста слушателя
есть наблюдаемый эффект: после отправки заказа появляется запись в БД.
Как запустить брокеры и проверить оба вида результата?
```

Выберите наблюдаемый результат теста. Чтобы проверить подключение, маршрутизацию и конвертер, отправьте сообщение через шаблон и прочитайте его из выделенной очереди с таймаутом. Чтобы проверить listener, отправьте сообщение во входную очередь и дождитесь его бизнес-эффекта, например записи в БД. У теста должен быть свой идентификатор заказа, а у ожидания – верхняя граница. Awaitility повторяет проверку до успеха или таймаута. Тест всё ещё зависит от доступности брокера и выделенного времени.

**ActiveMQ Classic в Spring Boot 3.5.** Для встроенного брокера добавьте `org.apache.activemq:activemq-broker` в тестовые зависимости. Используйте тестовую конфигурацию без `spring.activemq.broker-url`: URL внешнего брокера отключает режим embedded. Пример проверяет обмен сообщением через брокер. Очередь `jms-roundtrip-test` зарезервирована для этого теста и не используется listener приложения.

```java
@SpringBootTest(properties = "spring.jms.template.receive-timeout=5s")
class JmsIntegrationTest {

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    void shouldReceivePublishedText() {
        String queue = "jms-roundtrip-test";
        String payload = UUID.randomUUID().toString();

        jmsTemplate.convertAndSend(queue, payload);

        assertEquals(payload, jmsTemplate.receiveAndConvert(queue));
    }
}
```

**RabbitMQ в Testcontainers.** Для Boot 3.5 добавьте `spring-boot-testcontainers` и модули Testcontainers для JUnit Jupiter и RabbitMQ. `@ServiceConnection` предоставляет настройки подключения автонастроенным клиентам Boot. Пример явно объявляет очередь, публикует через default exchange и проверяет чтение. Для воспроизводимого CI закрепите конкретную версию образа, согласованную с окружением проекта.

```java
@SpringBootTest
@Testcontainers
class RabbitIntegrationTest {

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:4-management");

    @Autowired
    AmqpAdmin admin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void shouldRouteToDeclaredQueue() {
        String queue = "rabbit-roundtrip-" + UUID.randomUUID();
        admin.declareQueue(new Queue(queue, false, false, false));
        try {
            rabbitTemplate.convertAndSend("", queue, "order-created");

            assertEquals("order-created", rabbitTemplate.receiveAndConvert(queue, 5_000));
        } finally {
            admin.deleteQueue(queue);
        }
    }
}
```

Оба теста проверяют обмен через брокер. Для проверок повторной доставки, DLQ и конкурентных обработчиков задайте топологию и условия сбоя отдельно.

Документация: [Spring Boot 3.5 JMS](https://docs.spring.io/spring-boot/3.5/reference/messaging/jms.html), [Spring Boot 3.5 Testcontainers](https://docs.spring.io/spring-boot/3.5/reference/testing/testcontainers.html), [RabbitMQ default exchange](https://www.rabbitmq.com/docs/exchanges#default-exchange).

## Q14. Чем Spring JMS отличается от Spring AMQP?

**Spring JMS** работает через Java API Jakarta Messaging. Поставщик реализует этот API и сам определяет протокол связи с брокером. Отправитель выбирает destination: `Queue` или `Topic`. Очередь обслуживает конкурирующих потребителей, а topic доставляет публикации подходящим подпискам. JMS selector фильтрует доставку получателю по заголовкам и свойствам сообщения. Например, потребитель очереди может запросить только сообщения с `region = 'EU'`. Неподходящее сообщение остаётся доступным для другого получателя.

**Spring AMQP в примерах этой шпаргалки** работает с RabbitMQ по AMQP 0-9-1. Отправитель публикует в exchange. Тип exchange и bindings определяют маршрут в одну или несколько очередей. Например, topic exchange может направить `orders.eu` в очередь европейского обработчика и одновременно в очередь аудита. При публикации через default exchange имя очереди служит routing key благодаря её автоматической привязке. AMQP 1.0 имеет другой протокол и модель взаимодействия.

Выбирайте интеграцию по API и возможностям используемого брокера, а также по тому, где нужна фильтрация: при маршрутизации публикации между очередями или при выборе получателя внутри destination.

Документация: [Jakarta Messaging 3.1](https://jakarta.ee/specifications/messaging/3.1/jakarta-messaging-spec-3.1.html), [RabbitMQ Exchanges](https://www.rabbitmq.com/docs/exchanges).

## Q15. Когда использовать очереди JMS или RabbitMQ, а когда Kafka?

```text
A. Воркеры распределяют задания на создание PDF. Каждый экземпляр
задания должен давать один бизнес-результат, в том числе после сбоя воркера.
B. Биллинг и аналитика обрабатывают один поток событий независимо.
После исправления расчёта аналитике нужно заново прочитать доступную историю.
Что подходит для каждой задачи и какие ограничения останутся?
```

Здесь сравниваем работу с обычными очередями через Spring JMS или Spring AMQP и чтение журнала через Spring for Apache Kafka.

- **Очередь для работы.** Несколько воркеров могут конкурировать за сообщения одной очереди. Подтверждение завершает обработку конкретной доставки на стороне брокера. Если воркер записал результат в БД и потерял соединение до того, как RabbitMQ принял ACK, другой воркер может получить ту же задачу. Для единственного бизнес-эффекта обработчик может записать идентификатор задачи и результат в одной транзакции БД.
- **Kafka для независимого чтения истории.** Группы потребителей ведут собственные позиции по разделам. Фиксация позиции одной группы не удаляет запись. После исправления проекции можно перечитать историю начиная с выбранного offset, пока нужные записи доступны по политике хранения. При compaction старые значения одного ключа могут исчезнуть даже в сохранённом диапазоне offset.

Например, очередь подходит для распределения заданий на формирование PDF, а журнал – для восстановления отчёта по событиям заказов. Если нужен replay в RabbitMQ, рассмотрите Streams с хранением по времени или размеру. Для потоковых преобразований Kafka также предоставляет Kafka Streams. Производительность сравнивайте на целевой нагрузке с учётом размера сообщения, подтверждений, репликации и допустимой задержки.

Документация: [RabbitMQ Consumer Acknowledgements](https://www.rabbitmq.com/docs/confirms), [RabbitMQ Streams](https://www.rabbitmq.com/docs/streams), [Apache Kafka Topic Configs](https://kafka.apache.org/43/configuration/topic-configs/).

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
- [JMS и ActiveMQ](../../messaging/jms-activemq-interview.md) — низкоуровневый JMS-API и брокер ActiveMQ, поверх которого работает Spring JMS (`@JmsListener`, `JmsTemplate`)
