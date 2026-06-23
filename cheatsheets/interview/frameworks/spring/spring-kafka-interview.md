---
title: "Вопросы на собеседовании: Spring Kafka"
description: "Spring Kafka для работы с Apache Kafka: KafkaTemplate, @KafkaListener, Consumer Groups, DLT, транзакции, тестирование с EmbeddedKafka"
tags:
  - interview
  - spring
  - spring-kafka-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Kafka"
  - "Spring Kafka interview"
  - "Spring Kafka собеседование"
prerequisites:
  - "[[spring-kafka]]"
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Spring Kafka`

`Spring Kafka` — интеграция Apache Kafka в экосистему Spring. Предоставляет `KafkaTemplate` для отправки, `@KafkaListener` для потребления, поддержку транзакций и Dead Letter Topics. Часто задаётся в интервью рядом с основами Kafka.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Kafka Docs](https://docs.spring.io/spring-kafka/docs/current/reference/html/) — официальная документация
- [Baeldung: Spring Kafka](https://www.baeldung.com/spring-kafka) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. Что такое Spring Kafka и какие ключевые абстракции он предоставляет?](#q1-что-такое-spring-kafka-и-какие-ключевые-абстракции-он-предоставляет)
- [Q2. Как настроить KafkaTemplate и отправить сообщение?](#q2-как-настроить-kafkatemplate-и-отправить-сообщение)
- [Q3. Как написать Kafka Consumer с @KafkaListener?](#q3-как-написать-kafka-consumer-с-kafkalistener)
- [Q4. Что такое Consumer Group и почему это важно?](#q4-что-такое-consumer-group-и-почему-это-важно)

**Обработка ошибок**
- [Q5. Как обрабатывать ошибки в @KafkaListener?](#q5-как-обрабатывать-ошибки-в-kafkalistener)
- [Q6. Что такое Dead Letter Topic (DLT) и как его использовать?](#q6-что-такое-dead-letter-topic-dlt-и-как-его-использовать)

**Конфигурация и особенности**
- [Q7. Как работают транзакции в Spring Kafka?](#q7-как-работают-транзакции-в-spring-kafka)
- [Q8. Как настроить Kafka Listener для конкурентного чтения?](#q8-как-настроить-kafka-listener-для-конкурентного-чтения)
- [Q9. Как управлять offset коммитами?](#q9-как-управлять-offset-коммитами)
- [Q10. Как работает сериализация/десериализация в Spring Kafka?](#q10-как-работает-сериализациядесериализация-в-spring-kafka)

**Тестирование и специальные случаи**
- [Q11. Как тестировать Spring Kafka без реального брокера?](#q11-как-тестировать-spring-kafka-без-реального-брокера)
- [Q12. Что такое Kafka Streams в контексте Spring?](#q12-что-такое-kafka-streams-в-контексте-spring)
- [Q13. Какие стратегии обеспечения порядка сообщений есть в Kafka?](#q13-какие-стратегии-обеспечения-порядка-сообщений-есть-в-kafka)
- [Q14. Как настроить idempotent producer?](#q14-как-настроить-idempotent-producer)
- [Q15. Как работает Pause/Resume для @KafkaListener?](#q15-как-работает-pauseresume-для-kafkalistener)

## Q1. Что такое Spring Kafka и какие ключевые абстракции он предоставляет?

Spring Kafka — это обёртка над клиентом `kafka-clients`, которая встраивает Apache Kafka в экосистему Spring: вместо ручного создания продюсеров, консьюмеров и циклов опроса вы работаете через бины и аннотации, а инфраструктуру (пулы, ребаланс, коммиты offset) берёт на себя фреймворк. Брокер при этом остаётся внешним — Spring его не заменяет.

Ключевые абстракции закрывают весь цикл «отправил — получил — обработал ошибку»:

- **`KafkaTemplate`** — отправка сообщений (надстройка над Producer API). Управляет пулом продюсеров, поддерживает синхронную и асинхронную отправку.
- **`@KafkaListener`** — декларативная подписка на топик: помечаете метод, а контейнер сам опрашивает брокер и вызывает метод на каждое сообщение (надстройка над Consumer API).
- **`KafkaListenerContainerFactory`** — фабрика, которая собирает контейнеры слушателей: число потоков, режим коммита, обработчик ошибок.
- **`ConsumerRecord` / `ProducerRecord`** — обёртки над сырыми записями Kafka (ключ, значение, заголовки, метаданные партиции и offset).
- **`KafkaTransactionManager`** — интеграция Kafka-транзакций в механизм `@Transactional`.

**Важно:** Spring Kafka упрощает код, но не прячет модель Kafka. Партиции, offset и consumer group нужно понимать всё равно — иначе декларативный `@KafkaListener` приведёт к неожиданным ребалансам и потере порядка.

## Q2. Как настроить KafkaTemplate и отправить сообщение?

`KafkaTemplate` создаётся автоматически: достаточно задать `bootstrap-servers` и сериализаторы в `application.yml`, после чего бин можно внедрять и вызывать `send(...)`. Spring Boot сам поднимет `ProducerFactory` и шаблон.

Метод `send` не блокирует — он возвращает future. Отсюда два режима работы:

- **Асинхронно (по умолчанию)** — повесить callback на future и не ждать ответа брокера. Максимальная пропускная способность, но об ошибке отправки вы узнаете только в callback.
- **Синхронно** — вызвать `.get()` на future и дождаться подтверждения (acks). Медленнее, зато ошибка отправки сразу прилетит исключением в текущий поток.

Первым аргументом после топика идёт **ключ сообщения** — он определяет партицию (одинаковый ключ → одна партиция → сохранённый порядок).

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

```java
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderSync(OrderEvent event) throws Exception {
        // Синхронная отправка: блокируемся на .get() до подтверждения брокера
        SendResult<String, OrderEvent> result =
            kafkaTemplate.send("orders", event.orderId(), event).get();
        log.info("Sent offset: {}", result.getRecordMetadata().offset());
    }

    // Асинхронная (Spring Kafka 3.x: send() возвращает CompletableFuture;
    // старые ListenableFuture/addCallback удалены)
    public CompletableFuture<SendResult<String, OrderEvent>> sendAsync(OrderEvent event) {
        return kafkaTemplate.send("orders", event.orderId(), event)
            .whenComplete((result, ex) -> {
                if (ex == null) log.info("Sent offset: {}", result.getRecordMetadata().offset());
                else log.error("Failed to send: {}", event.orderId(), ex);
            });
    }
}
```

## Q3. Как написать Kafka Consumer с @KafkaListener?

Помечаете метод `@KafkaListener(topics = ..., groupId = ...)` — и Spring запускает контейнер, который в фоне опрашивает брокер и вызывает ваш метод на каждое сообщение. Десериализация значения в нужный тип (здесь `OrderEvent`) происходит автоматически, по настроенному десериализатору.

Сигнатуру метода Spring подстраивает под ваши нужды:

- **Только payload** — самый частый вариант: метод принимает уже распарсенный объект.
- **Payload + метаданные** — через `@Header` достаются топик, партиция, offset (полезно для логирования и идемпотентности).
- **Batch listener** — метод принимает `List<...>` и обрабатывает целую пачку записей за один вызов (требует фабрики с `batchListener = true`); снижает накладные расходы на коммиты при высоком потоке.

```java
@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "order-service")
    public void handle(OrderEvent event) {
        log.info("Received: {}", event.orderId());
        processOrder(event);
    }

    // С доступом к метаданным записи
    @KafkaListener(topics = "orders", groupId = "order-service")
    public void handleWithMeta(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("topic={}, partition={}, offset={}: {}", topic, partition, offset, event);
    }

    // Batch listener
    @KafkaListener(topics = "orders", containerFactory = "batchFactory")
    public void handleBatch(List<OrderEvent> events) {
        log.info("Received batch of {}", events.size());
    }
}
```

## Q4. Что такое Consumer Group и почему это важно?

Consumer Group — это группа потребителей с общим `groupId`, которые совместно читают топик и делят его партиции между собой. Ключевое правило: **каждая партиция в любой момент назначена ровно одному потребителю группы**. Именно это обеспечивает и масштабирование (нагрузка распределяется), и гарантию, что одно сообщение внутри группы обработается один раз.

```
Topic "orders" (3 partitions)
  Partition 0 → Consumer A (group "order-service")
  Partition 1 → Consumer B (group "order-service")
  Partition 2 → Consumer C (group "order-service")
```

Почему это важно на практике:

- **Горизонтальное масштабирование «из коробки».** Добавили ещё один экземпляр приложения с тем же `groupId` — он вошёл в группу, и Kafka сама передала ему часть партиций (ребаланс). Никакой ручной координации.
- **Потолок параллелизма = число партиций.** Потребителей больше, чем партиций → лишние простаивают без работы. Чтобы масштабироваться дальше, нужно увеличивать число партиций в топике, а не консьюмеров.
- **Разные группы изолированы.** Топик можно читать несколькими группами независимо — каждая получает полную копию потока (fan-out). Так одни и те же события забирают, например, и сервис аналитики, и сервис нотификаций, не мешая друг другу.

## Q5. Как обрабатывать ошибки в @KafkaListener?

Главный механизм — `DefaultErrorHandler` на уровне контейнера: при исключении в листенере он по политике backoff повторяет обработку записи, а когда попытки исчерпаны — передаёт запись recoverer'у (как правило, отправляет в DLT). Без него непрошедшее сообщение бесконечно перечитывалось бы и блокировало партицию.

Из чего складывается настройка:

- **Политика повторов (backoff)** — сколько раз и с какими паузами повторять. `ExponentialBackOffWithMaxRetries` даёт растущие интервалы, чтобы не долбить падающую зависимость.
- **Recoverer** — что делать после исчерпания попыток. `DeadLetterPublishingRecoverer` публикует запись в DLT (здесь — в топик с суффиксом `.DLT`).
- **Классификация исключений** — `addNotRetryableExceptions(...)` помечает ошибки, которые повторять бессмысленно (например, `ValidationException`): такая запись уходит в DLT сразу, без повторов.

**Эмпирическое правило:** повторять стоит только транзиентные ошибки (таймаут БД, недоступность сервиса). Ошибки данных (невалидный payload) — сразу в DLT, иначе впустую жжём попытки.

```java
// 1. DefaultErrorHandler — retry + DLT
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> template) {
    // 3 попытки с экспоненциальным backoff
    ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2.0);

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
    handler.addNotRetryableExceptions(ValidationException.class);
    return handler;
}

@Bean
public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
        ConsumerFactory<?, ?> cf, DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cf);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
}
```

```java
// 2. @KafkaListener с ErrorHandler
@KafkaListener(topics = "orders", errorHandler = "myErrorHandler")
public void handle(OrderEvent event) { ... }
```

## Q6. Что такое Dead Letter Topic (DLT) и как его использовать?

DLT (Dead Letter Topic) — отдельный топик, куда складываются сообщения, которые так и не удалось обработать после всех повторов. Зачем он нужен: в Kafka внутри партиции сообщения читаются строго по порядку, поэтому одно «отравленное» сообщение, которое падает раз за разом, заблокировало бы всю партицию. DLT убирает его с дороги — основная обработка едет дальше, а проблемный кейс сохраняется для разбора, а не теряется.

`DeadLetterPublishingRecoverer` при публикации добавляет в заголовки контекст ошибки: исходный топик/партицию/offset и класс исключения. Поэтому отдельный потребитель DLT может разобрать причину сбоя, поднять алерт и при необходимости вручную перезалить сообщение.

```java
// Суффикс по умолчанию у DeadLetterPublishingRecoverer — ".DLT"
// orders → orders.DLT (в Q5 recoverer как раз и формирует record.topic() + ".DLT")

// Потребитель DLT для разбора проблем
@KafkaListener(topics = "orders.DLT", groupId = "orders-dlt-handler")
public void handleDlt(
        @Payload OrderEvent event,
        @Header(KafkaHeaders.DLT_EXCEPTION_CAUSE_FQCN) String causeClass,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic) {
    log.error("DLT event from topic={}, cause={}: {}", originalTopic, causeClass, event);
    alertingService.notify(event, causeClass);
}
```

## Q7. Как работают транзакции в Spring Kafka?

Kafka-транзакции делают набор отправок атомарным: либо все сообщения становятся видны консьюмерам (читающим с `isolation.level=read_committed`), либо ни одно. Это даёт **exactly-once semantics** при записи сразу в несколько топиков. Включаются заданием `transaction-id-prefix` — после этого продюсер становится транзакционным, и отправки внутри `@Transactional` коммитятся/откатываются вместе.

Отдельный сценарий — **«Kafka + БД» в одной транзакции**. В Spring Kafka 3.x для этого используют один `@Transactional` поверх DataSource/JPA-транзакции: когда задан `transaction-id-prefix`, `KafkaTemplate` подключает транзакцию продюсера к активной транзакции через transaction synchronization, и Kafka-транзакция коммитится/откатывается вместе с БД. (`ChainedKafkaTransactionManager`, который связывал два менеджера, был deprecated в 2.7 и удалён в 3.0.) Но это не настоящий распределённый коммит (2PC): между коммитом БД и коммитом Kafka есть окно, в котором возможен сбой. Поэтому это «best-effort» консистентность, и для строгих гарантий чаще предпочитают паттерн transactional outbox.

```yaml
spring:
  kafka:
    producer:
      transaction-id-prefix: tx-orders-
```

```java
@Bean
public KafkaTransactionManager<String, Object> kafkaTransactionManager(
        ProducerFactory<String, Object> pf) {
    return new KafkaTransactionManager<>(pf);
}

// Kafka + JPA в одной транзакции (Spring Kafka 3.x): один @Transactional поверх
// DataSource/JPA. При заданном transaction-id-prefix KafkaTemplate подключает
// транзакцию продюсера к активной транзакции через transaction synchronization.
@Transactional // DataSourceTransactionManager / JpaTransactionManager
public void processAndPublish(OrderCommand cmd) {
    Order order = orderRepository.save(new Order(cmd));  // JPA
    kafkaTemplate.send("orders", order.getId(), new OrderCreated(order)); // Kafka
    // При откате JPA транзакции — откатится и Kafka
}
```

## Q8. Как настроить Kafka Listener для конкурентного чтения?

Параллелизм задаётся параметром `concurrency` — это число потоков-консьюмеров внутри одного экземпляра приложения. Каждый поток берёт свой набор партиций, и они обрабатываются независимо. Установить можно на фабрике (`setConcurrency(3)`) или прямо в аннотации (`concurrency = "3"`).

Ключевой нюанс: **толку от concurrency не больше, чем партиций**. Если в топике 3 партиции, то `concurrency=3` даёт по партиции на поток; четвёртый поток просто будет простаивать. Поэтому потолок параллелизма — это число партиций (с учётом всех экземпляров в группе вместе).

В примере включён ручной коммит (`AckMode.MANUAL` + `Acknowledgment`): offset подтверждается только после успешной обработки. Если бросить исключение и не вызвать `ack.acknowledge()`, сообщение не закоммитится и будет перечитано — это защита от потери при сбое.

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory(
        ConsumerFactory<String, OrderEvent> cf) {
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cf);
    factory.setConcurrency(3); // 3 потока = 3 consumer, по одному на партицию
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    return factory;
}

@KafkaListener(topics = "orders", concurrency = "3")
public void handle(OrderEvent event, Acknowledgment ack) {
    try {
        processOrder(event);
        ack.acknowledge(); // ручной коммит offset
    } catch (Exception e) {
        log.error("Processing failed", e);
        // не вызываем ack → сообщение будет перечитано
    }
}
```

## Q9. Как управлять offset коммитами?

Offset — это «закладка» в партиции: при коммите вы сообщаете брокеру, до какого места эта группа уже обработала сообщения. Если консьюмер упадёт, после перезапуска он продолжит с последнего закоммиченного offset. Поэтому **когда именно коммитить** — это компромисс между «at-least-once» (закоммитить после обработки → при сбое сообщение перечитается) и «at-most-once» (закоммитить до обработки → при сбое потеряется).

В Spring Kafka момент коммита определяется режимом `AckMode`. Первым шагом отключают авто-коммит Kafka-клиента (`enable-auto-commit: false`) и отдают управление контейнеру:

| AckMode | Поведение |
|---------|-----------|
| `BATCH` (default) | Коммит после обработки пачки записей |
| `RECORD` | Коммит после каждой записи |
| `MANUAL` | Ручной вызов `ack.acknowledge()` |
| `MANUAL_IMMEDIATE` | Немедленный коммит при вызове `ack.acknowledge()` |
| `TIME` | Периодический коммит по таймеру |
| `COUNT` | Коммит после N записей |

```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
      auto-offset-reset: earliest  # или latest
```

## Q10. Как работает сериализация/десериализация в Spring Kafka?

В Kafka по сети летят только байты, поэтому продюсеру нужен **сериализатор** (объект → байты), а консьюмеру — симметричный **десериализатор** (байты → объект). Чаще всего для значения берут `JsonSerializer`/`JsonDeserializer` из Spring Kafka, а для ключа — `StringSerializer`.

С JSON-десериализацией связаны два нюанса безопасности и типизации:

- **`spring.json.trusted.packages`** — белый список пакетов, классы из которых разрешено десериализовать. Без него (или с `*`) есть риск десериализации произвольного типа из заголовка — потенциальная RCE-поверхность.
- **Откуда берётся целевой тип.** По умолчанию `JsonDeserializer` читает имя класса из заголовка `__TypeId__`, который проставил продюсер. Если заголовка нет или продюсер чужой — тип задают явно через `spring.json.value.default.type` либо отключают заголовки (`setUseTypeHeaders(false)`) и указывают тип в коде. Это же нужно для полиморфных типов.

```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.*"
        spring.json.value.default.type: com.example.OrderEvent
```

```java
// Кастомный десериализатор для polymorphic types
@Bean
public ConsumerFactory<String, Object> consumerFactory() {
    JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
    deserializer.addTrustedPackages("com.example.*");
    deserializer.setUseTypeHeaders(false);
    deserializer.setRemoveTypeHeaders(true);
    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
}
```

## Q11. Как тестировать Spring Kafka без реального брокера?

Артефакт `spring-kafka-test` даёт аннотацию `@EmbeddedKafka` — она поднимает встроенный Kafka-брокер прямо в JVM теста. Не нужны ни Docker, ни внешний кластер: тест отправляет реальное сообщение, листенер реально его получает, и вы проверяете полный путь producer → broker → consumer.

Главная сложность таких тестов — **асинхронность**. После `send()` обработка происходит в фоновом потоке контейнера, поэтому нельзя сразу проверять результат — нужно дождаться его. В примере для этого используется Awaitility (`await().atMost(...).until(...)`), который опрашивает условие, пока оно не станет истинным или не истечёт таймаут. Это надёжнее, чем `Thread.sleep`, и не делает тест flaky.

**Альтернатива:** для интеграции, максимально близкой к проду, вместо встроенного брокера берут Testcontainers с настоящим образом Kafka — это медленнее, но точнее воспроизводит поведение реального брокера.

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka-test</artifactId>
  <scope>test</scope>
</dependency>
```

```java
@EmbeddedKafka(
    partitions = 1,
    topics = {"orders", "orders.DLT"},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"}
)
@SpringBootTest
class OrderConsumerTest {

    @Autowired
    private KafkaTemplate<String, OrderEvent> template;

    @Autowired
    private OrderService orderService;

    @Test
    void shouldProcessOrderEvent() throws Exception {
        OrderEvent event = new OrderEvent(UUID.randomUUID(), "PENDING");

        template.send("orders", event.orderId().toString(), event).get();

        // Ждём обработки
        await().atMost(5, SECONDS).until(() ->
            orderService.findById(event.orderId()).isPresent());
    }
}
```

## Q12. Что такое Kafka Streams в контексте Spring?

Kafka Streams — это библиотека для потоковой обработки «топик → топик»: вы описываете цепочку преобразований (фильтр, map, агрегация, join), и она непрерывно гоняет данные из входных топиков в выходные. В отличие от `@KafkaListener`, где вы сами пишете императивный обработчик, здесь декларативно задаётся топология, а состояние агрегаций (счётчики, join'ы) Streams хранит в локальных state store и реплицирует в Kafka.

Spring подключает её через `@EnableKafkaStreams` и `StreamsBuilderFactoryBean`: фреймворк сам поднимает и останавливает `KafkaStreams` по жизненному циклу контекста, а вам остаётся объявить бины `KStream`/`KTable` с нужной топологией. В примере поток фильтруется по статусу `COMPLETED` в один топик и параллельно агрегируется (`count` по ключу) в другой.

**Когда применять:** агрегации, оконные вычисления, join потоков, enrichment. Для простой обработки «получил — записал в БД» это избыточно — там достаточно обычного `@KafkaListener`.

```java
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration streamsConfig() {
        return new KafkaStreamsConfiguration(Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, "orders-processor",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
        ));
    }

    @Bean
    public KStream<String, OrderEvent> orderStream(StreamsBuilder builder) {
        KStream<String, OrderEvent> stream = builder.stream("orders");

        stream.filter((key, value) -> "COMPLETED".equals(value.status()))
              .to("completed-orders");

        stream.groupByKey()
              .count()
              .toStream()
              .to("order-counts");

        return stream;
    }
}
```

## Q13. Какие стратегии обеспечения порядка сообщений есть в Kafka?

Kafka гарантирует порядок **только внутри одной партиции**, а не по топику целиком. Значит, чтобы связанные события (например, по одному заказу) обрабатывались строго по порядку, нужно гарантировать, что они попадут в одну и ту же партицию. Отсюда стратегии:

- **Одна партиция на топик** — абсолютный порядок для всех сообщений, но и потолок параллелизма единица: пропускная способность низкая. Подходит для небольших критичных потоков.
- **Партиционирование по ключу (основной приём)** — отправляем связанные события с одинаковым ключом (`customerId`, `orderId`). Kafka хешем ключа кладёт их в одну партицию, сохраняя порядок, и при этом разные ключи параллелятся по разным партициям. Лучший баланс порядка и throughput.
- **Sequence-номер в заголовке** — пробрасываем порядковый номер, чтобы потребитель мог сам обнаружить пропуск или переупорядочить. Применяют, когда строгий порядок на стороне продюсера обеспечить нельзя.

**Подводный камень:** при включённых повторах без идемпотентности продюсер может переставить сообщения местами (см. Q14). Порядок «внутри партиции» гарантирован, только если продюсер настроен корректно.

```java
// 1. Один partition на топик (low throughput, high ordering)
@KafkaListener(topics = "critical-orders", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))

// 2. Одинаковый ключ для связанных сообщений (partition per key)
kafkaTemplate.send("orders", order.getCustomerId(), event);  // все события одного клиента в одну партицию

// 3. Заголовки для отслеживания порядка
ProducerRecord<String, OrderEvent> record = new ProducerRecord<>("orders", event.orderId(), event);
record.headers().add(new RecordHeader("sequence", ByteBuffer.allocate(8).putLong(sequence).array()));
```

## Q14. Как настроить idempotent producer?

Idempotent producer убирает дубликаты, которые возникают при повторных отправках. Проблема такая: продюсер отправил сообщение, брокер его записал, но ack потерялся в сети → продюсер по retry отправляет повторно → в топике появляется дубль, да ещё и порядок может нарушиться. Идемпотентность это лечит.

Включается флагом `enable.idempotence: true`. Под капотом продюсер получает уникальный PID и нумерует сообщения **sequence number** в рамках партиции; брокер запоминает последний принятый номер и отбрасывает повторы, сохраняя при этом исходный порядок. При включении Kafka автоматически выставляет безопасные настройки — `acks=all`, `retries>0` и ограничивает `max.in.flight.requests.per.connection` (≤5), иначе гарантии не работали бы.

**Важно понимать границы:** идемпотентность защищает только от дублей при ретраях *одного* продюсера в рамках сессии — это не сквозной exactly-once через всё приложение. Для атомарности записи в несколько топиков нужны транзакции (Q7).

```yaml
spring:
  kafka:
    producer:
      properties:
        enable.idempotence: true   # acks=all, retries>0 автоматически
        max.in.flight.requests.per.connection: 5  # max=5 для idempotence
```

## Q15. Как работает Pause/Resume для @KafkaListener?

Pause/Resume позволяет временно остановить выборку новых сообщений, не разрывая соединение и не покидая consumer group. Это важно: при `pause()` контейнер просто перестаёт отдавать вам новые записи, но продолжает посылать heartbeat'ы брокеру — поэтому ребаланс не запускается и партиции остаются за консьюмером. После `resume()` чтение продолжается с того же offset.

Типичный сценарий — **backpressure**: внешняя зависимость (БД, downstream-сервис) перегружена или недоступна, и нет смысла вычитывать новые сообщения, которые всё равно упадут. Ставим паузу, ждём восстановления, возобновляем.

Управлять можно на двух уровнях:

- **Весь контейнер** — через `KafkaListenerEndpointRegistry` находим контейнер по id и вызываем `pause()`/`resume()`.
- **Отдельные партиции** — внедрив `Consumer` в метод листенера, паузим конкретные партиции (`consumer.pause(consumer.assignment())`), оставляя остальные работать.

```java
@Autowired
private KafkaListenerEndpointRegistry registry;

// Пауза при перегрузке
public void pauseConsumer() {
    registry.getListenerContainer("ordersContainer")
        .pause();
}

// Возобновление
public void resumeConsumer() {
    registry.getListenerContainer("ordersContainer")
        .resume();
}

// Пауза на уровне конкретной партиции
@KafkaListener(id = "ordersContainer", topics = "orders")
public void handle(ConsumerRecord<String, OrderEvent> record,
                   Consumer<String, OrderEvent> consumer) {
    if (isBackpressure()) {
        consumer.pause(consumer.assignment());
    }
    // ...
}
```

## See also

- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka: partitions, offsets, consumer groups, delivery semantics
- [Spring Boot](spring-boot-interview.md) — auto-configuration, Spring Boot starters
- [Spring @Transactional](spring-transaction-interview.md) — транзакции Kafka + JPA через ChainedKafkaTransactionManager
- [Spring Retry](spring-retry-interview.md) — retry в Kafka listeners через DefaultErrorHandler
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива Kafka в монолите
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек с ReactiveKafkaConsumerTemplate
- [Spring Testing](spring-testing-interview.md) — @EmbeddedKafka для интеграционных тестов
- [Message Brokers](../../messaging/message-brokers-comparison-interview.md) — сравнение Kafka, RabbitMQ, Pulsar
- [Spring Events](spring-events-interview.md) — ApplicationEventPublisher как лёгкая альтернатива
- [Resilience4j](resilience4j-interview.md) — circuit breaker для Kafka producers
