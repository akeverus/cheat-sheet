---
title: "Вопросы на собеседовании: Event-driven паттерны"
description: "Краткие ответы по event-driven архитектуре: события, брокеры, идемпотентность, outbox/saga, совместимость схем и наблюдаемость."
tags:
  - interview
  - architecture
  - event-driven-patterns-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Event-driven паттерны"
  - "EDA interview"
  - "Event-driven собеседование"
prerequisites:
  - "[[event-driven]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Event-driven` паттерны

Комплексное руководство по вопросам собеседования на тему event-driven паттернов для Senior Java Developer.

## Полезные ссылки

### Официальная документация

- [Event-Driven Architecture — Martin Fowler](https://martinfowler.com/articles/201701-event-driven.html) — разница между event notification, event-carried state transfer, event sourcing, CQRS
- [Event Sourcing — Martin Fowler](https://martinfowler.com/eaaDev/EventSourcing.html) — паттерн хранения состояния через события
- [Spring Application Events](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events) — механизм событий в Spring Framework
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/reference/) — интеграция Spring Boot с Apache Kafka
- [Spring Events — Baeldung](https://www.baeldung.com/spring-events) — практический гайд по Spring `ApplicationEvent`
- [Intro to Apache Kafka with Spring — Baeldung](https://www.baeldung.com/spring-kafka) — producer/consumer в Spring Boot
- [Kafka Streams With Spring Boot (Baeldung)](https://www.baeldung.com/spring-boot-kafka-streams) — обработка потоков событий через Kafka Streams
- [Introduction to Spring Cloud Stream (Baeldung)](https://www.baeldung.com/spring-cloud-stream) — event-driven микросервисы через Spring Cloud Stream
- [Event Externalization with Spring Modulith (Baeldung)](https://www.baeldung.com/spring-modulith-event-externalization) — публикация доменных событий в Kafka через Spring Modulith
- [Saga Pattern in Microservices — Microservices.io](https://microservices.io/patterns/data/saga.html) — описание паттерна Saga
- [Transactional Outbox — Microservices.io](https://microservices.io/patterns/data/transactional-outbox.html) — описание Outbox pattern

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы EDA, события и брокеры**
- [Q1. (!) Что такое event-driven архитектура и чем она отличается от синхронного REST?](#q1--что-такое-event-driven-архитектура-и-чем-она-отличается-от-синхронного-rest)
- [Q2. Что такое событие (event) в контексте EDA? Какие атрибуты у события?](#q2-что-такое-событие-event-в-контексте-eda-какие-атрибуты-у-события)
- [Q3. (!) Что такое брокер сообщений и чем отличаются Kafka и RabbitMQ для EDA?](#q3--что-такое-брокер-сообщений-и-чем-отличаются-kafka-и-rabbitmq-для-eda)
- [Q4. Как обеспечить порядок обработки событий в распределённой системе?](#q4-как-обеспечить-порядок-обработки-событий-в-распределённой-системе)
- [Q5. (!) Что такое идемпотентность потребителя и зачем она нужна?](#q5--что-такое-идемпотентность-потребителя-и-зачем-она-нужна)

**Event Sourcing, CQRS и Saga**
- [Q6. (!) Что такое Event Sourcing?](#q6--что-такое-event-sourcing)
- [Q7. Что такое CQRS и как он сочетается с EDA?](#q7-что-такое-cqrs-и-как-он-сочетается-с-eda)
- [Q8. (!) Что такое Saga и когда её используют?](#q8--что-такое-saga-и-когда-её-используют)
- [Q9. (!) Что такое Outbox pattern?](#q9--что-такое-outbox-pattern)
- [Q10. (!) Что такое dead letter queue (DLQ) и когда его использовать?](#q10--что-такое-dead-letter-queue-dlq-и-когда-его-использовать)

**Схемы, семантика доставки и мониторинг**
- [Q11. Что такое Schema Registry и зачем он в EDA?](#q11-что-такое-schema-registry-и-зачем-он-в-eda)
- [Q12. (!) Как в EDA добиться exactly-once семантики?](#q12--как-в-eda-добиться-exactly-once-семантики)
- [Q13. Что такое consumer lag и как с ним работать?](#q13-что-такое-consumer-lag-и-как-с-ним-работать)
- [Q14. Чем event-driven отличается от message-driven?](#q14-чем-event-driven-отличается-от-message-driven)
- [Q15. Как обеспечить обратную совместимость при изменении схемы события?](#q15-как-обеспечить-обратную-совместимость-при-изменении-схемы-события)
- [Q16. Что такое partition key в Kafka и как он влияет на порядок?](#q16-что-такое-partition-key-в-kafka-и-как-он-влияет-на-порядок)
- [Q17. Когда использовать топик с одной партицией, а когда с несколькими?](#q17-когда-использовать-топик-с-одной-партицией-а-когда-с-несколькими)
- [Q18. Что такое consumer group и как распределяются партиции?](#q18-что-такое-consumer-group-и-как-распределяются-партиции)
- [Q19. Как в EDA обеспечить мониторинг и наблюдаемость?](#q19-как-в-eda-обеспечить-мониторинг-и-наблюдаемость)
- [Q20. Какие антипаттерны в event-driven архитектуре стоит избегать?](#q20-какие-антипаттерны-в-event-driven-архитектуре-стоит-избегать)

**Продвинутые темы**
- [Q21. Что такое event replay и когда его использовать?](#q21-что-такое-event-replay-и-когда-его-использовать)
- [Q22. Как обеспечить транзакционность в event-driven системе?](#q22-как-обеспечить-транзакционность-в-event-driven-системе)
- [Q23. Что такое event versioning и как управлять изменениями схемы?](#q23-что-такое-event-versioning-и-как-управлять-изменениями-схемы)
- [Q24. Как обработать события в правильном порядке при параллельной обработке?](#q24-как-обработать-события-в-правильном-порядке-при-параллельной-обработке)
- [Q25. Что такое event store и чем он отличается от обычной БД?](#q25-что-такое-event-store-и-чем-он-отличается-от-обычной-бд)
- [Q26. Как обеспечить мониторинг и алертинг в event-driven системе?](#q26-как-обеспечить-мониторинг-и-алертинг-в-event-driven-системе)
- [Q27. Что такое event choreography vs orchestration?](#q27-что-такое-event-choreography-vs-orchestration)
- [Q28. Как обработать события с разной скоростью обработки?](#q28-как-обработать-события-с-разной-скоростью-обработки)
- [Q29. Что такое event sourcing snapshot и зачем он нужен?](#q29-что-такое-event-sourcing-snapshot-и-зачем-он-нужен)
- [Q30. Как обеспечить безопасность событий в event-driven системе?](#q30-как-обеспечить-безопасность-событий-в-event-driven-системе)

**Spring и Kafka на практике**
- [Q31. (!) Как работает механизм событий в Spring Framework?](#q31--как-работает-механизм-событий-в-spring-framework)
- [Q32. Как создать Kafka producer и consumer в Spring Boot?](#q32-как-создать-kafka-producer-и-consumer-в-spring-boot)
- [Q33. (!) Как реализовать Outbox pattern на Spring Boot и Kafka?](#q33--как-реализовать-outbox-pattern-на-spring-boot-и-kafka)
- [Q34. Как реализовать Event Sourcing на Java?](#q34-как-реализовать-event-sourcing-на-java)
- [Q35. (!) Как реализовать CQRS с проекциями на Spring?](#q35--как-реализовать-cqrs-с-проекциями-на-spring)

**Продвинутые паттерны EDA**
- [Q36. (!) Чем отличается Saga Orchestration от Choreography?](#q36--чем-отличается-saga-orchestration-от-choreography)
- [Q37. Что такое Process Manager и когда его использовать?](#q37-что-такое-process-manager-и-когда-его-использовать)
- [Q38. (!) Что такое Event-Carried State Transfer (ECST)?](#q38--что-такое-event-carried-state-transfer-ecst)
- [Q39. Как проектировать стратегии компенсации в EDA?](#q39-как-проектировать-стратегии-компенсации-в-eda)
- [Q40. Как организовать версионирование топиков Kafka при эволюции схемы?](#q40-как-организовать-версионирование-топиков-kafka-при-эволюции-схемы)

## Q1. (!) Что такое event-driven архитектура и чем она отличается от синхронного REST?

`Event-driven` архитектура (`EDA`) -- стиль проектирования, при котором компоненты обмениваются данными через события, публикуемые в брокер сообщений (`Kafka`, `RabbitMQ`), вместо прямых вызовов друг друга.

Механика проста: производитель (producer) публикует событие и сразу продолжает работу -- он не ждёт обработки и даже не знает, кто его прочитает. Потребители (consumers) подписываются на топики/очереди и разбирают события асинхронно, каждый в своём темпе. Источник и приёмник связаны только контрактом события, а не сетевым адресом друг друга.

Два стиля взаимодействия наглядно:

- **Синхронный REST:** `Service A` шлёт `POST /orders` в `Service B` → `Service B` отвечает `200 OK` обратно в `Service A`. Вызов адресный и двусторонний.
- **Event-driven:** `Producer` публикует `OrderCreated` в `Broker`; тот доставляет событие (poll/push) сразу нескольким независимым потребителям -- `Consumer 1` и `Consumer 2`.

Отличия от синхронного `REST`:

| Критерий | `REST` | `EDA` |
|----------|--------|-------|
| Связность | Клиент знает адрес сервиса | Producer не знает consumers |
| Масштабируемость | Нужен load balancer | Consumers масштабируются независимо |
| Отказоустойчивость | Нужны retry и circuit breaker | Сообщения сохраняются в брокере |
| Консистентность | Сильная в одной транзакции | Eventual consistency, нужны [паттерны согласованности](consistency-patterns-interview.md) (`Saga`, компенсации) |
| Латентность | Немедленный ответ | Задержка обработки |

## Q2. Что такое событие (event) в контексте EDA? Какие атрибуты у события?

Событие -- неизменяемая запись о факте, который **уже произошёл** в системе (например, «Заказ создан», «Платёж проведён»). Этим оно отличается от команды: событие констатирует свершившееся в прошедшем времени, а команда -- это запрос на будущее действие, который ещё может быть отклонён.

Неизменяемость важна по смыслу: факт нельзя «переотправить иначе» -- если состояние поменялось, публикуется новое событие. Поэтому событие удобно хранить, реплеить и использовать как источник истины.

**Рекомендуемые атрибуты** -- каждый решает свою задачу:
- **Идентификатор события** -- уникальный `eventId`; по нему потребитель дедуплицирует повторные доставки и связывает запись с трассировкой
- **Тип события** -- имя и версия; по ним брокер и потребитель маршрутизируют событие и проверяют совместимость схемы
- **Временная метка** -- когда факт произошёл (часто в `UTC`, чтобы не зависеть от таймзоны узла)
- **Агрегат** -- идентификатор сущности (`orderId`, `userId`); заодно это ключ партиционирования для порядка
- **Полезная нагрузка** (payload) -- собственно данные события
- **Метаданные** -- источник, `correlationId`, `traceId` для [распределённой трассировки](../monitoring/observability-interview.md)

Пример структуры события на Java:

```java
public record OrderCreatedEvent(
    UUID eventId,
    String type,           // "order.created"
    Instant timestamp,
    UUID aggregateId,      // orderId
    OrderPayload payload,
    EventMetadata metadata
) {
    public record OrderPayload(
        List<OrderItem> items,
        BigDecimal totalAmount,
        String currency
    ) {}

    public record EventMetadata(
        String source,          // "order-service"
        String correlationId,
        String traceId
    ) {}
}
```

## Q3. (!) Что такое брокер сообщений и чем отличаются Kafka и RabbitMQ для EDA?

Брокер сообщений -- промежуточный посредник, который принимает сообщения от производителей, надёжно их хранит и доставляет потребителям. Он развязывает producer и consumer во времени: отправитель и получатель не обязаны быть онлайн одновременно, а сообщения переживают перезапуски. В зависимости от настроек брокер даёт гарантии персистентности, доставки и порядка.

`Kafka` и `RabbitMQ` решают эту задачу принципиально по-разному: первый -- это распределённый лог, второй -- классическая система очередей.

| Характеристика | `Kafka` | `RabbitMQ` |
|----------------|---------|------------|
| Модель | Распределённый лог (append-only) | Очереди с доставкой и удалением |
| Хранение | Retention-based (дни/недели) | До потребления |
| Чтение | По offset, consumer pull | Push к consumer |
| Replay | Да, с любого offset | Нет (сообщение удаляется) |
| Порядок | В пределах партиции | В пределах очереди (один consumer) |
| Throughput | Очень высокий (миллионы msg/s) | Средний (десятки тысяч msg/s) |
| Основной сценарий | Event streaming, EDA | Task queue, RPC |

**Как выбирать.** Ключевое различие -- судьба сообщения после прочтения: `Kafka` хранит лог по retention и позволяет любому потребителю перечитать историю с нужного offset, а `RabbitMQ` удаляет сообщение, как только оно обработано. Поэтому для `EDA` с большим объёмом событий, fan-out на много потребителей и потребностью «переиграть» историю чаще выбирают `Kafka` (подробнее в [вопросах по Kafka](../messaging/kafka-interview.md)); для рабочих очередей задач и гарантированной доставки до одного потребителя -- `RabbitMQ`.

## Q4. Как обеспечить порядок обработки событий в распределённой системе?

Глобальный порядок в распределённом брокере недостижим без потери масштабируемости, поэтому на практике обеспечивают порядок **по агрегату**, а не по всему потоку. В `Kafka` порядок гарантируется только в пределах одной партиции -- значит, нужно следить, чтобы связанные события попадали в одну и ту же партицию. Отсюда правила:
- Используйте ключ партиционирования (например, `orderId`), чтобы все события по одному заказу попадали в одну партицию и читались последовательно
- Не увеличивайте число партиций без необходимости -- `partition = hash(key) % numPartitions`, поэтому смена их числа перетасует распределение ключей и разорвёт порядок
- Глобального порядка обычно не добиваются осознанно: достаточно порядка в рамках одного агрегата

Как это работает по ключу:

- `Producer` отправляет события с ключом `order-1` и `order-3` -- оба попадают в `Partition 0`.
- События с ключом `order-2` уходят в `Partition 1`.
- `Partition 0` читается последовательно одним `Consumer 1`, а `Partition 1` -- одним `Consumer 2`. Так порядок сохраняется внутри каждой партиции, а разные заказы обрабатываются параллельно.

В `RabbitMQ` порядок в одной очереди сохраняется при одном потребителе; при нескольких -- порядок не гарантирован. Решения: `single active consumer`, `message groups` для группировки по ключу.

## Q5. (!) Что такое идемпотентность потребителя и зачем она нужна?

Идемпотентность потребителя означает, что повторная обработка одного и того же сообщения даёт ровно тот же результат, что и однократная -- без двойного списания, двойного резервирования и дублей в БД.

Зачем это нужно: в распределённой системе дубли неизбежны. Брокеры дают семантику **at-least-once**, поэтому одно и то же событие может прийти повторно -- из-за retry продюсера, ребаланса или перезапуска потребителя до коммита offset. Идемпотентность -- это способ сделать дубли безвредными, а не пытаться их полностью исключить.

**Практический критерий:** если операция связана с деньгами или резервированием, идемпотентность подтверждают интеграционным тестом с повторной доставкой одного и того же `eventId`.

Способы реализации:

```java
@Service
@RequiredArgsConstructor
public class IdempotentEventHandler {

    private final ProcessedEventRepository processedEvents;
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(OrderCreatedEvent event) {
        // Проверка: уже обработано?
        if (processedEvents.existsByEventId(event.eventId())) {
            log.info("Event {} already processed, skipping", event.eventId());
            return;
        }

        // Бизнес-логика
        orderRepository.save(mapToOrder(event));

        // Пометить как обработанное (в той же транзакции)
        processedEvents.save(new ProcessedEvent(event.eventId(), Instant.now()));
    }
}
```

**Альтернативные подходы** -- когда отдельная таблица обработанных событий избыточна, идемпотентность можно встроить в саму бизнес-операцию:
- **Бизнес-ключ**: если операция по `paymentId` уже выполнена -- пропустить (дедупликация по естественному ключу домена)
- **Upsert** вместо `INSERT` -- повторное выполнение перезаписывает ту же строку, а не создаёт дубликат
- **Conditional update**: `UPDATE ... WHERE version = :expected` -- повторная попытка не пройдёт условие и ничего не изменит (оптимистичная блокировка)

## Q6. (!) Что такое Event Sourcing?

`Event Sourcing` -- подход, при котором источником истины служит не текущее состояние, а полная **последовательность событий**, его породивших. Вместо того чтобы хранить «баланс = 400», система хранит цепочку фактов («открыт счёт», «+500», «-200», «+100»), а текущее состояние **вычисляет** на лету, проигрывая эти события поверх «нулевого» состояния (или поверх снапшота + последующие события).

Цепочка событий и вычисление текущего баланса по порядку:

`AccountOpened` (balance=0) → `MoneyDeposited` (+500) → `MoneyWithdrawn` (−200) → `MoneyDeposited` (+100) → текущий баланс: 400.

Пример реализации на Java:

```java
public class BankAccount {
    private UUID id;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    // Восстановление из событий
    public static BankAccount fromEvents(List<DomainEvent> events) {
        var account = new BankAccount();
        events.forEach(account::apply);
        return account;
    }

    // Команда -> генерация события
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        raise(new MoneyDeposited(id, amount, Instant.now()));
    }

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, amount, balance);
        }
        raise(new MoneyWithdrawn(id, amount, Instant.now()));
    }

    // Применение события (мутация состояния)
    private void apply(DomainEvent event) {
        switch (event) {
            case AccountOpened e -> { this.id = e.accountId(); this.balance = BigDecimal.ZERO; }
            case MoneyDeposited e -> this.balance = balance.add(e.amount());
            case MoneyWithdrawn e -> this.balance = balance.subtract(e.amount());
            default -> throw new IllegalStateException("Unknown event: " + event);
        }
    }

    private void raise(DomainEvent event) {
        apply(event);
        uncommittedEvents.add(event);
    }
}
```

**Плюсы:** полная история изменений «из коробки» (готовый аудит), возможность пересчитать состояние на любой момент прошлого и построить новые проекции из исторических данных, не теряя ничего.
**Минусы:** запросы усложняются -- состояние не лежит готовым, поэтому нужны проекции (read-модели); схему событий нельзя просто поменять (старые события остаются в хранилище навсегда); лог только растёт, поэтому для скорости восстановления требуются снапшоты.

## Q7. Что такое CQRS и как он сочетается с EDA?

`CQRS` (`Command Query Responsibility Segregation`) -- разделение модели на две независимые части: запись (command side) и чтение (query side). Идея в том, что у этих сторон разные требования: запись должна обеспечивать консистентность и бизнес-правила, а чтение -- быстро отдавать данные в удобной форме. Поэтому command side обновляет хранилище и публикует события, а query side обслуживается из отдельного хранилища, заранее заточенного под конкретные запросы.

С `EDA` это сочетается естественно: именно события связывают две стороны -- command side их публикует, а проекция на query side подписывается и обновляет read-модель.

Поток записи и чтения по шагам:

- **Запись:** `Client` шлёт `CreateOrder` в `Command Side` → тот делает `INSERT event` в `Event Store` → событие `OrderCreated` уходит в `Projection` → она делает `UPDATE` денормализованной `Read Model`.
- **Чтение:** `Client` шлёт `GET /orders` в `Query Side` → он делает `SELECT` из той же `Read Model`.

Пример реализации проекции на `Spring`:

```java
@Component
@RequiredArgsConstructor
public class OrderProjection {

    private final OrderViewRepository orderViewRepository;

    @EventHandler  // или @KafkaListener
    public void on(OrderCreatedEvent event) {
        var view = new OrderView(
            event.orderId(),
            event.customerId(),
            event.totalAmount(),
            OrderStatus.CREATED,
            event.timestamp()
        );
        orderViewRepository.save(view);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        orderViewRepository.updateStatus(event.orderId(), OrderStatus.SHIPPED);
    }
}
```

**Преимущества:** чтение и запись масштабируются независимо (read-нагрузка обычно на порядок выше); read-модели оптимизируются под конкретные запросы (денормализация, нужные индексы); из одного потока событий можно построить сколько угодно разных представлений -- список заказов, статистику, аналитику -- не трогая command side.

**Компромисс:** между записью и появлением данных в read-модели есть лаг (eventual consistency) и заметный рост сложности -- `CQRS` оправдан там, где выигрыш от разделения перевешивает эти издержки.

## Q8. (!) Что такое Saga и когда её используют?

`Saga` -- способ выполнить бизнес-операцию, охватывающую несколько [распределённых](distributed-systems-interview.md) сервисов, без единой ACID-транзакции. Длинная операция разбивается на последовательность локальных шагов: каждый сервис атомарно фиксирует свою часть и публикует событие, запускающее следующий шаг. Если шаг падает, общий откат невозможен (каждый предыдущий шаг уже закоммичен), поэтому Saga выполняет **компенсирующие действия** -- семантически отменяет уже сделанное.

Saga нужна именно там, где двухфазный коммит (`2PC`) непрактичен: сервисы автономны, у каждого своя БД, а блокировать их все на время операции недопустимо.

Участники: `Order Service`, `Inventory Service`, `Payment Service`, `Shipping Service`.

Успешный поток по шагам:

1. `Order Service` → `Inventory Service`: `OrderCreated`.
2. `Inventory Service` → `Payment Service`: `InventoryReserved`.
3. `Payment Service` → `Shipping Service`: `PaymentProcessed`.
4. `Shipping Service` → `Order Service`: `OrderShipped`.

При сбое платежа (компенсация) между `Payment Service` и `Inventory Service`:

1. `Payment Service` → `Inventory Service`: `PaymentFailed`.
2. `Inventory Service` → `Order Service`: `InventoryReleased`.
3. `Order Service` помечает заказ как `OrderCancelled`.

Два подхода:

| | Хореография | Оркестрация |
|---|---|---|
| Координация | Децентрализованная (события) | Центральный оркестратор |
| Связность | Слабая | Средняя |
| Отладка | Сложнее (распределённый поток) | Проще (один координатор) |
| Точка отказа | Нет единой | Оркестратор |
| Когда | Простые потоки, 2-3 сервиса | Сложные потоки, много шагов |

Пример оркестратора Saga:

```java
@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;

    @Transactional
    public void execute(CreateOrderCommand cmd) {
        var order = Order.create(cmd);
        orderRepository.save(order);

        try {
            inventoryClient.reserve(order.getItems());
            paymentClient.charge(order.getPaymentDetails());
            order.markConfirmed();
        } catch (InventoryException e) {
            order.markCancelled("Insufficient inventory");
        } catch (PaymentException e) {
            // Компенсация: откатить резервирование
            inventoryClient.release(order.getItems());
            order.markCancelled("Payment failed");
        }

        orderRepository.save(order);
    }
}
```

**На что обратить внимание на собеседовании.** Три вещи определяют рабочую Saga: идемпотентность шагов (событие может прийти повторно), корректные компенсирующие действия (отмена должна быть семантически верной и тоже идемпотентной) и наблюдаемость всего потока через `traceId` и метрики -- иначе застрявшую сагу невозможно диагностировать.

## Q9. (!) Что такое Outbox pattern?

`Outbox` pattern решает проблему **двойной записи**: нельзя атомарно и сохранить данные в БД, и опубликовать событие в брокер -- это два разных хранилища, и сбой между ними оставит систему в рассогласованном состоянии (заказ создан, а событие потеряно, или наоборот).

Решение: исходящее событие записывают в таблицу `outbox` в той же БД и в той же транзакции, что и бизнес-данные. Раз это одна локальная транзакция, либо сохраняется и заказ, и событие, либо ничего. А отдельный процесс (`polling` или `CDC`) уже надёжно читает `outbox` и публикует события в брокер с гарантией at-least-once.

Поток по шагам:

- **В одной транзакции:** `Service` делает `INSERT order` в таблицу `orders` и `INSERT event` в таблицу `outbox_events` -- обе записи коммитятся вместе.
- **Асинхронно:** процесс `CDC / Polling` читает `outbox_events` и публикует события в `Kafka`.

Пример на `Spring Boot`:

```java
// Сущность outbox
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    private String aggregateType;  // "Order"
    private UUID aggregateId;
    private String eventType;      // "OrderCreated"
    @Column(columnDefinition = "TEXT")
    private String payload;        // JSON
    private Instant createdAt;
    private boolean published;
}

// Сервис: бизнес-логика + outbox в одной транзакции
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        var order = Order.from(request);
        orderRepository.save(order);

        var event = new OutboxEvent(
            UUID.randomUUID(), "Order", order.getId(),
            "OrderCreated",
            objectMapper.writeValueAsString(order),
            Instant.now(), false
        );
        outboxRepository.save(event);

        return order;
    }
}

// Поллер: периодически читает и публикует
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void pollAndPublish() {
        var events = outboxRepository.findByPublishedFalseOrderByCreatedAt();
        for (var event : events) {
            kafkaTemplate.send(event.getAggregateType(), 
                              event.getAggregateId().toString(), 
                              event.getPayload());
            event.setPublished(true);
        }
    }
}
```

Альтернатива polling -- `CDC` через `Debezium`: читает транзакционный лог БД и публикует изменения таблицы `outbox` в `Kafka` с минимальной задержкой.

## Q10. (!) Что такое dead letter queue (DLQ) и когда его использовать?

`Dead Letter Queue` (`DLQ`) -- отдельная очередь, куда сообщение перемещают после N неудачных попыток обработки. Главная её ценность -- не дать «ядовитому» сообщению (poison pill) заблокировать всю очередь: без `DLQ` сообщение, которое стабильно падает, будет бесконечно ретраиться и держать партицию, останавливая обработку всех следующих за ним событий. `DLQ` убирает такое сообщение с горячего пути и сохраняет его для последующего ручного разбора.

Используют `DLQ`, когда сбой не временный (битый payload, несовместимая схема, баг в обработчике), а ретраи бессмысленны.

В `Spring Kafka` настройка `DLQ` через `DefaultErrorHandler`:

```java
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
            (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1));

        // Retry 3 раза с backoff, потом в DLQ
        return new DefaultErrorHandler(recoverer, 
            new FixedBackOff(1000L, 3));
    }
}
```

В `RabbitMQ` -- через `x-dead-letter-exchange`:

```java
@Bean
public Queue mainQueue() {
    return QueueBuilder.durable("orders.queue")
        .withArgument("x-dead-letter-exchange", "orders.dlx")
        .withArgument("x-dead-letter-routing-key", "orders.dlq")
        .build();
}
```

**Мониторинг DLQ.** Размер `DLQ` -- прямой индикатор здоровья обработки: в норме он близок к нулю, а любой рост сигнализирует о проблеме (баг, несовместимая схема, сбой downstream). Поэтому на размер `DLQ` ставят алерт по порогу. Подробнее -- в [вопросах по наблюдаемости](../monitoring/observability-interview.md).

## Q11. Что такое Schema Registry и зачем он в EDA?

`Schema Registry` -- централизованное хранилище схем событий (`Avro`, `JSON Schema`, `Protobuf`), которое выступает контрактом между producer и consumer. Проблема, которую он решает: в `EDA` producer и consumer развязаны и деплоятся независимо, поэтому ничто не мешает выкатить producer с несовместимым изменением схемы и сломать всех потребителей в рантайме. Registry переносит эту проверку «влево» -- несовместимую схему он просто не даёт зарегистрировать.

Confluent `Schema Registry` для `Kafka` обеспечивает:
- Централизованное управление схемами -- единый источник истины вместо дублирования контракта в каждом сервисе
- Проверку совместимости (`BACKWARD`, `FORWARD`, `FULL`) при регистрации новой версии
- Версионирование схем -- каждая схема получает номер версии, история сохраняется
- Контракты между сервисами -- producer и consumer договариваются через зарегистрированную схему
- Предотвращение breaking changes -- несовместимое изменение отклоняется ещё до деплоя

Пример `Avro` схемы:

```json
{
  "type": "record",
  "name": "OrderCreated",
  "namespace": "com.example.events",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "amount", "type": "double"},
    {"name": "currency", "type": "string", "default": "RUB"},
    {"name": "discount", "type": ["null", "double"], "default": null}
  ]
}
```

Режимы совместимости:
- `BACKWARD` -- новый consumer читает старые данные (можно добавлять опциональные поля, удалять поля с default)
- `FORWARD` -- старый consumer читает новые данные (можно удалять опциональные поля, добавлять поля с default)
- `FULL` -- оба направления

## Q12. (!) Как в EDA добиться exactly-once семантики?

`Exactly-once` -- гарантия, что каждое событие учтено в результате ровно один раз: ни потерь, ни дублей. Это самая сильная и самая дорогая из трёх семантик доставки (рядом с at-most-once и at-least-once).

**Главная мысль для ответа:** настоящий end-to-end exactly-once в распределённой системе почти недостижим и дорог. Поэтому на практике его **эмулируют**: используют надёжную доставку at-least-once (дубли допускаются), а «ровно один раз» обеспечивают на стороне потребителя через идемпотентность. С точки зрения наблюдаемого результата это эквивалентно exactly-once, но проще и дешевле.

**Компромисс:** строгая exactly-once повышает сложность и latency; в большинстве production-сценариев берут `at-least-once` + идемпотентный consumer и дедупликацию.

Подходы:
- **`Kafka` транзакции**: `enable.idempotence=true`, `acks=all`, `transactional.id` для продюсера; `isolation.level=read_committed` для потребителя -- даёт exactly-once в пределах read-process-write внутри `Kafka`
- **Идемпотентность потребителя**: дедупликация по `eventId`, бизнес-проверки -- работает при любой доставке и закрывает дубли на границе с внешними системами
- **`Outbox` pattern**: атомарная запись в БД + публикация в `Kafka` -- устраняет двойную запись на стороне продюсера

```java
// Kafka producer с exactly-once настройками
@Bean
public ProducerFactory<String, String> producerFactory() {
    var props = Map.<String, Object>of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
        ProducerConfig.ACKS_CONFIG, "all",
        ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-tx-",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
    );
    return new DefaultKafkaProducerFactory<>(props);
}
```

## Q13. Что такое consumer lag и как с ним работать?

`Consumer lag` -- отставание потребителя от продюсера: разница между последним offset в партиции (сколько записано) и текущим committed offset consumer group (сколько обработано). Например, lag 10000 означает, что в очереди скопилось 10000 необработанных сообщений. По сути это главный сигнал, что потребитель не успевает за входящим потоком.

Чем опасен растущий lag: данные в read-моделях устаревают, реакция системы запаздывает, а если lag дорастёт до retention limit -- `Kafka` удалит ещё не прочитанные сообщения, и они потеряются безвозвратно.

**Причины роста:** медленная обработка, потребителей меньше, чем нужно, перегрузка downstream-сервисов.

**Как работать:**
- **Мониторинг**: метрика `kafka_consumer_lag` в `Prometheus`, инструмент `Burrow` -- сначала нужно lag видеть
- **Масштабирование**: добавить потребителей, но не больше числа партиций (лишние будут простаивать)
- **Оптимизация обработки**: увеличить `max.poll.records`, перейти на batch-обработку, вынести медленные операции в async
- **Алерты**: при lag выше порога или при опасном приближении к retention limit

## Q14. Чем event-driven отличается от message-driven?

Разница не в технологии, а в **семантике сообщения и направлении знания**. `Event-driven` строится вокруг событий-фактов («что уже произошло»): producer публикует `OrderCreated`, не зная и не заботясь, кто его прочитает, а потребители сами подписываются на нужные типы. `Message-driven` строится вокруг сообщений-команд («что нужно сделать»): отправитель адресует `CreateOrder` конкретному получателю и ожидает, что тот выполнит действие.

Отсюда и остальные различия: события дают fan-out на многих потребителей и слабую связность, команды -- адресную доставку обычно одному получателю.

| | Event-driven | Message-driven |
|---|---|---|
| Семантика | Факт: `OrderCreated` | Команда: `CreateOrder` |
| Связность | Producer не знает consumers | Sender знает receiver |
| Fan-out | Множество consumers | Обычно один receiver |
| Replay | Возможен | Обычно нет |
| Пример | `Kafka` (лог событий) | `RabbitMQ` (task queue) |

Граница размыта: события тоже передаются сообщениями. На практике часто комбинируют оба подхода.

## Q15. Как обеспечить обратную совместимость при изменении схемы события?

Корень проблемы: producer и consumers деплоятся независимо и в любой момент в системе сосуществуют сообщения старой и новой схемы, причём в `Kafka` старые события лежат в логе ещё долго. Значит, изменения схемы должны быть такими, чтобы и старый код читал новые данные, и новый код читал старые. Отсюда правила:
- Добавлять новые поля только с значениями по умолчанию -- старый потребитель их просто не заметит (backward compatible)
- Не удалять поля сразу, а помечать `@Deprecated` и убирать позже, когда все потребители перестанут их читать
- Использовать `Schema Registry` для автоматической проверки совместимости и версионирование типа события (`order.created.v2`) для несовместимых изменений
- Потребители должны быть устойчивы к неизвестным полям (игнорировать) и к отсутствующим опциональным (подставлять дефолт) -- это правило Постеля «будь либерален к тому, что принимаешь»

Два сценария эволюции схемы на примерах:

- **Backward compatible:** `v1: orderId, amount` → `v2: orderId, amount, discount?` (добавлено опциональное поле). Старые consumers просто игнорируют `discount`.
- **Breaking change:** `v1: amount (int)` → `v2: amount (string)` (сменился тип поля). Требует нового топика или нового типа события.

**Breaking changes** (удаление поля, изменение типа) -- требуют версионирования типа события или отдельного топика.

## Q16. Что такое partition key в Kafka и как он влияет на порядок?

`Partition key` -- ключ, по которому `Kafka` детерминированно выбирает партицию для сообщения: `partition = hash(key) % numPartitions`. Раз функция детерминированная, все сообщения с одним ключом гарантированно попадают в одну и ту же партицию -- а значит, обрабатываются одним потребителем в группе строго в порядке публикации.

Именно так ключ связывает порядок с бизнес-смыслом: выбрав `orderId` ключом, вы получаете гарантию, что все события одного заказа обработаются по очереди, при этом события разных заказов идут параллельно по разным партициям.

```java
// Spring Kafka: отправка с ключом
kafkaTemplate.send("orders", orderId.toString(), orderEventJson);

// Все события для order-123 попадут в одну партицию
// и будут обработаны последовательно
```

Правила выбора ключа:
- Идентификатор агрегата (`orderId`, `userId`) -- даёт порядок по агрегату и равномерное распределение нагрузки
- `null` ключ -- сообщения раскидываются round-robin по партициям, порядок не гарантируется (подходит, когда порядок не важен)
- Не менять число партиций после запуска -- `numPartitions` входит в формулу, поэтому его изменение перетасует распределение существующих ключей и разорвёт порядок

## Q17. Когда использовать топик с одной партицией, а когда с несколькими?

Выбор -- это компромисс между **строгим порядком** и **параллелизмом**: партиция одновременно и единица упорядочивания, и единица параллелизма, поэтому одно достигается за счёт другого.

**Одна партиция** -- когда нужен глобальный порядок абсолютно всех сообщений, а одного потребителя хватает по нагрузке. Платой становится отсутствие масштабирования: всё читает один consumer.

**Несколько партиций** -- когда нужны параллельная обработка и высокий throughput. Порядок при этом сохраняется только внутри партиции (по ключу), а число партиций задаёт потолок параллелизма: потребителей в группе не может быть больше, чем партиций.

**Рекомендация:** по умолчанию берут несколько партиций для масштабирования и обеспечивают порядок по агрегату через ключ партиционирования -- глобальный порядок нужен редко. Число партиций планируют с запасом заранее: увеличить его на живом топике сложно, потому что это сломает раскладку существующих ключей.

## Q18. Что такое consumer group и как распределяются партиции?

`Consumer group` -- набор потребителей с общим `group.id`, которые совместно делят между собой обработку топика. Ключевое правило: каждая партиция назначается ровно одному потребителю в группе. Именно это даёт масштабирование без нарушения порядка -- партиции делятся между инстансами, а внутри каждой партиции сообщения по-прежнему читаются последовательно одним потребителем.

Через consumer group реализуются обе модели сразу: одна группа = work queue (нагрузка делится), а несколько групп на одном топике = publish/subscribe (каждая группа читает весь поток независимо).

Пример распределения для топика `orders` с 3 партициями (`Partition 0`, `Partition 1`, `Partition 2`) и `Consumer Group A` из двух потребителей:

- `Partition 0` → `Consumer 1`
- `Partition 1` → `Consumer 1`
- `Partition 2` → `Consumer 2`

Каждая партиция назначена ровно одному потребителю; при этом один потребитель может держать несколько партиций.

При добавлении или отказе потребителя происходит **rebalance** -- партиции автоматически перераспределяются между живыми инстансами (на время ребаланса обработка приостанавливается). Если потребителей больше, чем партиций, лишние просто простаивают -- параллелизм ограничен числом партиций. Offset (прогресс чтения) хранится по consumer group, поэтому при перезапуске потребитель продолжает с последнего закоммиченного offset, а не с начала.

## Q19. Как в EDA обеспечить мониторинг и наблюдаемость?

Главная сложность EDA для наблюдаемости -- асинхронность и отсутствие сквозного вызова: запрос «растворяется» в потоке событий, поэтому стандартного стектрейса одного вызова нет, и связность приходится восстанавливать вручную. Покрывают это четырьмя слоями:
- **Метрики** -- здоровье потока в цифрах: consumer lag, throughput, latency (publish-to-consume), error rate, DLQ size
- **Распределённая трассировка** -- сшивает разрозненные шаги в один путь: `traceId` кладут в заголовки сообщения и восстанавливают в `MDC` при обработке (см. [Observability](../monitoring/observability-interview.md))
- **Логирование** -- ключевые события (публикация, обработка, ошибки), но без чувствительных данных в payload
- **Алерты** -- срабатывают на симптомы деградации: рост lag, падение throughput, рост error rate, несовместимость схем

```java
// Передача traceId через Kafka headers
@KafkaListener(topics = "orders")
public void consume(ConsumerRecord<String, String> record) {
    var traceId = new String(record.headers()
        .lastHeader("X-Trace-Id").value());
    MDC.put("traceId", traceId);
    try {
        processOrder(record.value());
    } finally {
        MDC.clear();
    }
}
```

Инструменты: `Prometheus` + `Grafana` для метрик; `Jaeger`/`Zipkin` для трассировки; `Kafka Manager`/`AKHQ` для управления [Kafka](../messaging/kafka-interview.md).

## Q20. Какие антипаттерны в event-driven архитектуре стоит избегать?

Общий корень большинства антипаттернов EDA -- попытка использовать асинхронную систему как синхронную и тем самым потерять то, ради чего EDA выбирали.

**Главный антипаттерн:** использовать события как замену синхронному RPC и ждать «мгновенный» ответ -- это возвращает жёсткую связность и убивает отказоустойчивость, оставляя лишь накладные расходы брокера.

Остальные типичные ошибки:
- **Большие сообщения** -- не гонять через брокер крупные бинарные данные; класть их в объектное хранилище (S3) и передавать в событии только ссылку (Claim Check pattern), иначе брокер деградирует
- **Синхронные вызовы внутри потребителя** -- длинные `REST`-запросы в обработчике блокируют партицию и растят lag; выносить в async или отдельные воркеры
- **Отсутствие идемпотентности** -- при at-least-once повторная доставка превращается в дублирующиеся операции (двойное списание)
- **Игнорирование схемы** -- менять формат события без версионирования = молча сломать потребителей в рантайме
- **Один гигантский топик на всё** -- мешает независимое масштабирование и права доступа; разделять по доменам и критичности
- **Event soup** -- слишком много мелких событий без чёткого контракта: поток невозможно понять и сопровождать

## Q21. Что такое event replay и когда его использовать?

`Event replay` -- повторная обработка событий из истории (из топика `Kafka` или event store) для восстановления состояния или пересчёта проекций. Это становится возможным именно потому, что лог событий сохраняется и неизменяем: события можно прочитать заново с любого offset, как будто они приходят впервые.

**Где это спасает:**
- Восстановление read-модели после её повреждения или потери -- просто проиграть события заново
- Исправление бага в обработчике: чинят логику и реплеят историю, получая корректные данные задним числом
- Создание совершенно новой проекции из уже накопленных исторических событий
- Тестирование обработчиков на реальных продакшен-данных

В `Kafka`: сброс offset consumer group на начало топика:

```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group order-projections \
  --topic orders \
  --reset-offsets --to-earliest \
  --execute
```

## Q22. Как обеспечить транзакционность в event-driven системе?

Транзакционность в `EDA` сложнее, чем в монолите, потому что одна ACID-транзакция не накрывает сразу и БД, и брокер, и другие сервисы. Поэтому атомарность собирают из специализированных приёмов, каждый закрывает свою границу:
- **`Outbox` pattern** -- атомарность на границе «БД ↔ брокер»: бизнес-данные и событие пишутся в одну транзакцию БД, исключая двойную запись
- **`Saga`** -- атомарность на границе «несколько сервисов»: вместо общей транзакции -- компенсирующие действия при сбоях
- **`CDC`** -- `Debezium` читает transaction log БД, превращая закоммиченные изменения в события без отдельной публикации
- **`Kafka` транзакции** -- атомарность на границе `read-process-write` внутри `Kafka`: чтение, обработка и запись коммитятся вместе

На практике ключевой момент -- **eventual consistency это норма** для `EDA`; сильная консистентность достигается через синхронные вызовы или [паттерны согласованности](consistency-patterns-interview.md). В интервью стоит показать, как система ведёт себя при ретраях и дублирующей доставке.

## Q23. Что такое event versioning и как управлять изменениями схемы?

`Event versioning` -- управление изменениями схемы событий так, чтобы старый и новый код продолжали работать одновременно. Это критично, потому что в `EDA` нельзя обновить producer и всех consumers атомарно: какое-то время они сосуществуют в разных версиях, и в логе лежат события разных схем.

**Стратегии** делятся по тому, в какую сторону должна работать совместимость:
- **Backward compatible** -- новый формат, который читают старые потребители: новые поля опциональны, старые не удаляются
- **Forward compatible** -- старый формат, который читают новые потребители: они переживают отсутствие новых полей за счёт значений по умолчанию
- **Версионирование типа** -- когда совместимость невозможна, заводят `order.created.v1`, `order.created.v2` и поддерживают обе версии
- **`Schema Registry`** -- автоматизирует контроль: централизованно хранит схемы (`Avro`, `JSON Schema`) и не даёт зарегистрировать несовместимое изменение

**Рекомендация:** держать совместимые (backward/forward) изменения под контролем `Schema Registry`, а неизбежные breaking changes выносить в новую версию типа или отдельный топик.

## Q24. Как обработать события в правильном порядке при параллельной обработке?

Порядок и параллелизм кажутся противоречием, но `Kafka` примиряет их через партицию -- она одновременно единица упорядочивания и единица параллелизма. Порядок гарантируется внутри партиции, поэтому связанные события держат в одной партиции, а несвязанные раскидывают по разным и обрабатывают параллельно. Конкретно:
- Ключ партиционирования (`orderId`) направляет все события одного заказа в одну партицию -- их относительный порядок сохранён
- Один поток потребителя владеет одной партицией и обрабатывает её строго последовательно

В `Spring Kafka`:

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3); // по числу партиций
    return factory;
}
```

Каждый поток обрабатывает свои партиции последовательно; между партициями -- параллельно.

## Q25. Что такое event store и чем он отличается от обычной БД?

`Event store` -- специализированное хранилище для событий в `Event Sourcing`. Принципиальное отличие от обычной БД: оно хранит не текущее состояние, а полную историю фактов, и эта история неизменяема. Отсюда его свойства:
- `Append-only` -- события только дописываются и никогда не меняются и не удаляются (это и обеспечивает аудит и replay)
- Поддерживает чтение по агрегату и временному диапазону -- типичный запрос «дай все события заказа X по порядку»
- Оптимизирован под запись и последовательное чтение цепочек событий, а не под произвольные ad-hoc запросы

| | Обычная БД (CRUD) | Event Store |
|---|---|---|
| Хранит | Текущее состояние | Историю изменений |
| Операции | `INSERT`/`UPDATE`/`DELETE` | Только `APPEND` |
| Версия | Одна (текущая) | Все версии (события) |
| Текущее состояние | Прямое чтение | Вычисляется из событий |

Примеры: `EventStoreDB`, `Axon Server`, `Kafka` как event store (с retention `infinite`).

## Q26. Как обеспечить мониторинг и алертинг в event-driven системе?

Алертинг в `EDA` строят на симптомах деградации потока, а не на ошибках отдельных запросов. Ниже -- минимальный набор метрик и пороги, на которые ставят алерты (подробнее в [мониторинге](../monitoring/observability-interview.md)):

| Метрика | Описание | Алерт |
|---------|----------|-------|
| Consumer lag | Отставание потребителя | Lag > 10000 или растёт |
| Throughput | msg/s входящие и обработанные | Падение > 50% |
| Latency | Время от publish до consume | P99 > 5s |
| Error rate | Частота ошибок обработки | > 1% |
| DLQ size | Размер dead letter queue | > 0 (или > порога) |

Инструменты: `Prometheus` + `Grafana` для метрик; `Jaeger`/`Zipkin` для trace id в событиях; `AKHQ`/`Confluent Control Center` для управления `Kafka`.

## Q27. Что такое event choreography vs orchestration?

Это два способа координировать многошаговый процесс между сервисами. Разница в том, **где живёт логика потока**: размазана по участникам или собрана в одном месте.

**Хореография** -- децентрализованная координация: логики процесса как единого целого нигде нет, каждый сервис лишь реагирует на входящие события и публикует свои. Плюс -- сервисы слабо связаны и легко добавляются; минус -- поток нигде не виден целиком, поэтому отладка и мониторинг сложнее.

**Оркестрация** -- логика потока собрана в центральном оркестраторе, который вызывает сервисы и ведёт их по шагам. Плюс -- процесс виден и управляем в одном месте, отладка проще; минус -- оркестратор становится точкой отказа и узлом более жёсткой связности.

Две схемы координации на одном наборе сервисов:

- **Хореография** (сервисы передают управление через события по цепочке): `Order` →event→ `Inventory` →event→ `Payment` →event→ `Shipping`.
- **Оркестрация** (центральный `Orchestrator` рассылает команды каждому сервису): `Orchestrator` →cmd→ `Order`, `Orchestrator` →cmd→ `Inventory`, `Orchestrator` →cmd→ `Payment`, `Orchestrator` →cmd→ `Shipping`.

В контексте `Saga`: хореографическая -- каждый сервис публикует события; оркестрируемая -- оркестратор управляет потоком и компенсацией. Для сложных потоков с 4+ сервисами предпочитают оркестрацию.

## Q28. Как обработать события с разной скоростью обработки?

Проблема -- **head-of-line blocking**: партиция обрабатывается строго по порядку, поэтому одно медленное событие задерживает все следующие за ним быстрые. Лечат это, разводя потоки разной скорости, чтобы они не делили один путь обработки:
- **Разделение по топикам** -- отдельные топики для быстрых и медленных событий, чтобы они не мешали друг другу
- **Приоритетные очереди** -- `RabbitMQ` priority queues, где срочное обходит несрочное
- **Отдельные consumer groups** -- разные группы со своим темпом для разных типов событий
- **Backpressure** -- замедление продюсера, когда потребитель не успевает, чтобы не переполнять систему
- **Rate limiting** -- сознательное ограничение скорости обработки для защиты downstream

В `Kafka`: настройка `max.poll.records` для контроля батча; использование разных consumer groups; `pause`/`resume` партиций при перегрузке.

## Q29. Что такое event sourcing snapshot и зачем он нужен?

`Snapshot` -- сохранённый «слепок» состояния агрегата на определённую версию события. Он решает главную проблему производительности `Event Sourcing`: лог событий растёт бесконечно, и восстанавливать состояние, проигрывая тысячи событий с нуля при каждой загрузке, становится слишком дорого. Снапшот превращает это из O(N) в почти O(1): загружают последний снапшот и доигрывают только события, появившиеся после него.

Восстановление со снапшотом по порядку:

`Event 1` → `Event 2` → ... → `Event 100` → `Snapshot` (version=100) → `Event 101` → `Event 102` → текущее состояние.

То есть вместо проигрывания всех 100+ событий загружают снапшот версии 100 и доигрывают только `Event 101` и `Event 102`.

Стратегии создания:
- Периодически (каждые N событий или по времени)
- По требованию (при достижении определённого размера)
- Хранение снапшотов отдельно от событий

```java
public class SnapshotStrategy {
    private static final int SNAPSHOT_THRESHOLD = 100;

    public BankAccount load(UUID accountId, EventStore eventStore, 
                            SnapshotStore snapshotStore) {
        var snapshot = snapshotStore.findLatest(accountId);
        var events = snapshot != null 
            ? eventStore.loadAfter(accountId, snapshot.version())
            : eventStore.loadAll(accountId);

        var account = snapshot != null 
            ? BankAccount.fromSnapshot(snapshot) 
            : new BankAccount();
        events.forEach(account::apply);

        if (events.size() >= SNAPSHOT_THRESHOLD) {
            snapshotStore.save(account.toSnapshot());
        }
        return account;
    }
}
```

## Q30. Как обеспечить безопасность событий в event-driven системе?

Особенность безопасности в `EDA`: события долго хранятся в брокере и доступны многим потребителям, поэтому одно чувствительное поле в payload -- это утечка с длинным временем жизни. Меры выстраивают по слоям -- транзит, доступ, сами данные:
- **Шифрование**: TLS для транзита (между клиентами и брокером), encryption at rest для хранилища
- **Аутентификация и авторизация**: `SASL`/`SSL` для подтверждения личности клиента в `Kafka`, `RBAC`/ACL для разграничения прав
- **Данные**: не класть пароли и `PII` в события; если без чувствительного поля никак -- шифровать его отдельно внутри payload
- **Схемы**: контроль формата через `Schema Registry`, чтобы исключить инъекции мусора
- **Изоляция**: отдельные топики и кластеры по окружениям, чтобы dev не видел prod-данные
- **Сеть**: брокер в приватной сети, ACL на уровне топиков для ограничения доступа

```yaml
# Kafka producer SSL/SASL настройки (application.yml)
spring:
  kafka:
    properties:
      security.protocol: SASL_SSL
      sasl.mechanism: SCRAM-SHA-256
      sasl.jaas.config: >
        org.apache.kafka.common.security.scram.ScramLoginModule required
        username="${KAFKA_USER}" password="${KAFKA_PASSWORD}";
    ssl:
      trust-store-location: classpath:kafka-truststore.jks
      trust-store-password: ${TRUSTSTORE_PASSWORD}
```

## Q31. (!) Как работает механизм событий в Spring Framework?

`Spring` предоставляет встроенный механизм событий через `ApplicationEventPublisher` -- лёгкий способ развязать бины внутри одного приложения. Принципиально важно понимать его границу: это **внутрипроцессный** pub/sub, работающий в памяти одной JVM, а не распределённый брокер. Поэтому события `Spring` не переживают рестарт, не доставляются между сервисами и не дают гарантий брокера -- путать их с `Kafka` нельзя. Подробнее о Spring -- в [вопросах по Spring Framework](../frameworks/spring/spring-framework-interview.md).

Публикация и обработка кастомного события:

```java
// 1. Определение события
public record OrderCreatedEvent(UUID orderId, BigDecimal amount) {}

// 2. Публикация события
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        var order = orderRepository.save(Order.from(request));
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getAmount()));
        return order;
    }
}

// 3. Синхронный обработчик (в той же транзакции)
@Component
public class InventoryEventListener {

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserve(event.orderId());
    }
}

// 4. Асинхронный обработчик (вне транзакции)
@Component
public class NotificationEventListener {

    @Async
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        emailService.sendConfirmation(event.orderId());
    }
}

// 5. Выполнение ПОСЛЕ коммита транзакции
@Component
public class KafkaPublishListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("orders", event.orderId().toString(),
                           toJson(event));
    }
}
```

Ключевые аннотации:
- `@EventListener` -- синхронный обработчик (по умолчанию в том же потоке)
- `@Async @EventListener` -- асинхронный обработчик (требует `@EnableAsync`)
- `@TransactionalEventListener` -- привязка к фазе транзакции (`AFTER_COMMIT`, `AFTER_ROLLBACK`, `BEFORE_COMMIT`)

**Важно:** `@TransactionalEventListener(phase = AFTER_COMMIT)` гарантирует, что обработчик выполнится только после успешного коммита. Это идеальное место для отправки в `Kafka` или отправки уведомлений.

## Q32. Как создать Kafka producer и consumer в Spring Boot?

`Spring Kafka` сводит работу с брокером к декларативной конфигурации и аннотациям: сериализаторы и группа задаются в `application.yml`, отправка идёт через `KafkaTemplate`, а приём -- через `@KafkaListener`, который сам управляет poll-циклом и коммитом offset.

Минимальная настройка `Spring Kafka`:

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: order-service
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.events"
```

Producer:

```java
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, OrderCreatedEvent>> publish(
            OrderCreatedEvent event) {
        return kafkaTemplate
            .send("orders", event.orderId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event {}", event.orderId(), ex);
                } else {
                    log.info("Sent event {} to partition {} offset {}",
                        event.orderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
```

Consumer:

```java
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderProjection orderProjection;

    @KafkaListener(topics = "orders", groupId = "order-projections")
    public void consume(@Payload OrderCreatedEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received event {} from partition {} offset {}",
                 event.orderId(), partition, offset);
        orderProjection.handle(event);
    }

    // Batch consumer для высокого throughput
    @KafkaListener(topics = "orders", groupId = "order-analytics",
                   containerFactory = "batchFactory")
    public void consumeBatch(List<ConsumerRecord<String, OrderCreatedEvent>> records) {
        log.info("Received batch of {} records", records.size());
        records.forEach(r -> analyticsService.process(r.value()));
    }
}
```

## Q33. (!) Как реализовать Outbox pattern на Spring Boot и Kafka?

Суть реализации: бизнес-сущность и `OutboxEvent` сохраняются в **одной `@Transactional`-транзакции** (это и даёт атомарность), а отдельный relay асинхронно публикует накопленные события в `Kafka` и помечает их как отправленные. Ниже полный пример с `@Scheduled` polling; для production его заменяют на `Debezium` CDC.

```java
// DDL для таблицы outbox
// CREATE TABLE outbox_events (
//     id UUID PRIMARY KEY,
//     aggregate_type VARCHAR(255) NOT NULL,
//     aggregate_id VARCHAR(255) NOT NULL,
//     event_type VARCHAR(255) NOT NULL,
//     payload TEXT NOT NULL,
//     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
//     published BOOLEAN NOT NULL DEFAULT FALSE
// );

@Entity
@Table(name = "outbox_events")
@Getter @Setter @NoArgsConstructor
public class OutboxEvent {
    @Id private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    @Column(columnDefinition = "TEXT") private String payload;
    private Instant createdAt;
    private boolean published;

    public static OutboxEvent create(String aggregateType, String aggregateId,
                                      String eventType, String payload) {
        var event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setCreatedAt(Instant.now());
        event.setPublished(false);
        return event;
    }
}

// Бизнес-сервис: данные + outbox в одной транзакции
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper mapper;

    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        var order = orderRepo.save(Order.from(req));
        outboxRepo.save(OutboxEvent.create(
            "Order", order.getId().toString(),
            "OrderCreated", mapper.writeValueAsString(order)
        ));
        return order;
    }
}

// Polling relay: читает и публикует неотправленные события
@Component
@RequiredArgsConstructor
public class OutboxRelay {
    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafka;

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void relay() {
        outboxRepo.findTop100ByPublishedFalseOrderByCreatedAt()
            .forEach(event -> {
                kafka.send(event.getAggregateType().toLowerCase(),
                           event.getAggregateId(), event.getPayload())
                     .whenComplete((res, ex) -> {
                         if (ex == null) {
                             event.setPublished(true);
                             outboxRepo.save(event);
                         }
                     });
            });
    }
}
```

Для production рекомендуется `CDC` через `Debezium` вместо polling -- меньше задержка, нет нагрузки на БД от постоянных запросов.

## Q34. Как реализовать Event Sourcing на Java?

Каркас Event Sourcing складывается из трёх частей: иерархии событий (`sealed interface DomainEvent` + record'ы фактов), агрегата, который порождает события и восстанавливает себя их применением, и event store, который только дописывает события (`append`) и читает их по агрегату в порядке версии (`load`). Полный пример с event store, агрегатом и восстановлением:

```java
// Базовый интерфейс события
public sealed interface DomainEvent permits 
        AccountOpened, MoneyDeposited, MoneyWithdrawn {
    UUID eventId();
    UUID aggregateId();
    Instant timestamp();
    int version();
}

// Конкретные события
public record AccountOpened(UUID eventId, UUID aggregateId, 
                             String owner, Instant timestamp, 
                             int version) implements DomainEvent {}

public record MoneyDeposited(UUID eventId, UUID aggregateId, 
                              BigDecimal amount, Instant timestamp, 
                              int version) implements DomainEvent {}

public record MoneyWithdrawn(UUID eventId, UUID aggregateId, 
                              BigDecimal amount, Instant timestamp, 
                              int version) implements DomainEvent {}

// Event Store (репозиторий событий)
@Repository
@RequiredArgsConstructor
public class JdbcEventStore implements EventStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    @Override
    public void append(UUID aggregateId, List<DomainEvent> events) {
        for (var event : events) {
            jdbc.update("""
                INSERT INTO domain_events 
                (event_id, aggregate_id, event_type, payload, version, created_at)
                VALUES (?, ?, ?, ?::jsonb, ?, ?)
                """,
                event.eventId(), aggregateId,
                event.getClass().getSimpleName(),
                mapper.writeValueAsString(event),
                event.version(), event.timestamp()
            );
        }
    }

    @Override
    public List<DomainEvent> load(UUID aggregateId) {
        return jdbc.query("""
            SELECT event_type, payload FROM domain_events
            WHERE aggregate_id = ? ORDER BY version
            """,
            (rs, i) -> deserialize(rs.getString("event_type"), 
                                    rs.getString("payload")),
            aggregateId
        );
    }
}
```

## Q35. (!) Как реализовать CQRS с проекциями на Spring?

Архитектура по сторонам:

- **Command Side:** `REST Controller` шлёт `CreateOrder` в `OrderCommandService`; тот делает `save events` в `Event Store` и `publish` в `Kafka`.
- **Query Side:** `OrderProjection` делает `consume` из `Kafka` и `upsert` в Read DB (`PostgreSQL`/`Redis`); отдельный `REST Controller` обрабатывает `GET` через `OrderQueryService`, который делает `select` из той же Read DB.

Command side (запись):

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService commandService;

    @PostMapping
    public ResponseEntity<UUID> createOrder(@RequestBody CreateOrderRequest req) {
        var orderId = commandService.handle(new CreateOrderCommand(
            req.customerId(), req.items(), req.shippingAddress()
        ));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderId);
    }
}

@Service
@RequiredArgsConstructor
public class OrderCommandService {
    private final EventStore eventStore;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    @Transactional
    public UUID handle(CreateOrderCommand cmd) {
        var orderId = UUID.randomUUID();
        var event = new OrderCreatedEvent(UUID.randomUUID(), orderId,
            cmd.customerId(), cmd.items(), Instant.now(), 1);

        eventStore.append(orderId, List.of(event));
        kafka.send("orders", orderId.toString(), 
                   mapper.writeValueAsString(event));
        return orderId;
    }
}
```

Query side (чтение через проекцию):

```java
// Проекция: обновляет read-модель по событиям из Kafka
@Component
@RequiredArgsConstructor
public class OrderProjection {
    private final OrderViewRepository viewRepo;

    @KafkaListener(topics = "orders", groupId = "order-view-projection")
    public void on(ConsumerRecord<String, String> record) {
        var event = parseEvent(record.value());
        switch (event) {
            case OrderCreatedEvent e -> viewRepo.save(new OrderView(
                e.orderId(), e.customerId(), 
                OrderStatus.CREATED, e.timestamp()));
            case OrderShippedEvent e -> viewRepo.updateStatus(
                e.orderId(), OrderStatus.SHIPPED);
            case OrderCancelledEvent e -> viewRepo.updateStatus(
                e.orderId(), OrderStatus.CANCELLED);
        }
    }
}

// Query service: быстрое чтение из read-модели
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderViewRepository viewRepo;

    public OrderView getOrder(UUID orderId) {
        return viewRepo.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<OrderView> getOrdersByCustomer(UUID customerId) {
        return viewRepo.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
```

Преимущество `CQRS`: read-модель `OrderView` может быть денормализованной таблицей с индексами под конкретные запросы, тогда как command side хранит только события. Можно создать несколько проекций (список заказов, аналитика, поиск) из одного потока событий.

## Q36. (!) Чем отличается Saga Orchestration от Choreography?

Оба подхода реализуют Saga -- длинную распределённую транзакцию из локальных шагов с компенсациями, -- но по-разному распределяют ответственность за управление потоком: хореография размазывает её по участникам, оркестрация концентрирует в одном компоненте.

**Choreography (хореография)** — каждый сервис самостоятельно слушает события и публикует следующие. Нет центрального координатора, логика процесса не собрана в одном месте.

Поток через брокер по шагам:

1. `Order Service` публикует `OrderCreated` в `Broker`.
2. `Broker` доставляет `OrderCreated` в `Payment Service`, тот публикует `PaymentDone` в `Broker`.
3. `Broker` доставляет `PaymentDone` в `Inventory Service`, тот публикует `Reserved` в `Broker`.
4. `Broker` доставляет `Reserved` в `Delivery Service`.

**Orchestration (оркестрация)** — центральный `Saga Orchestrator` явно управляет шагами, вызывая команды и ожидая событий.

Поток под управлением оркестратора по шагам:

1. `Saga Orchestrator` шлёт команду `ProcessPayment` в `Payment Service`; тот отвечает событием `PaymentDone` оркестратору.
2. `Saga Orchestrator` шлёт `ReserveInventory` в `Inventory Service`; тот отвечает `Reserved` оркестратору.
3. `Saga Orchestrator` шлёт `CreateDelivery` в `Delivery Service`; тот отвечает `DeliveryCreated` оркестратору.

| Критерий | Choreography | Orchestration |
|----------|-------------|---------------|
| Связность | Низкая (только через broker) | Выше (оркестратор знает всех) |
| Наблюдаемость | Сложно отследить весь флоу | Статус виден в оркестраторе |
| Сложность | Растёт с числом шагов | Явная и локализованная |
| SPOF | Нет | Оркестратор (нужна HA) |
| Тестирование | Сложнее (нужна вся цепочка) | Проще (unit тест оркестратора) |
| Подходит для | 2-3 шага, простой флоу | Сложные бизнес-процессы (5+ шагов) |

**Рекомендация:** для сложных бизнес-процессов (заказ, онбординг) выбирайте **оркестрацию** — статус процесса виден в одном месте, компенсации явно описаны. Хореографию применяйте для простых событийных цепочек (уведомления, аудит-лог).

## Q37. Что такое Process Manager и когда его использовать?

`Process Manager` (также `Saga Orchestrator`, `Workflow Engine`) — компонент, который хранит состояние долгосрочного бизнес-процесса и координирует сервисы, реагируя на их события. Ключевое в нём -- именно **персистентное состояние**: процесс может идти часами и днями, переживать перезапуски и ждать внешних событий, поэтому его статус нельзя держать в памяти, его пишут в БД.

**Отличие от простого Saga Orchestrator** -- в сложности управляемого потока:
- `Saga Orchestrator` — линейный или слегка ветвящийся флоу с явными шагами
- `Process Manager` — полноценная state machine с таймаутами, параллельными ветками, компенсациями и перезапусками

State machine процесса заказа -- состояния и переходы (в скобках -- событие/команда перехода):

- Старт → `OrderCreated`.
- `OrderCreated` → `PaymentPending` (по `ProcessPayment`).
- `PaymentPending` → `PaymentFailed` (по `PaymentFailed`).
- `PaymentPending` → `InventoryPending` (по `PaymentDone`).
- `PaymentFailed` → `OrderCancelled` (по `CompensateOrder`).
- `InventoryPending` → `InventoryFailed` (по `ReservationFailed`).
- `InventoryPending` → `DeliveryPending` (по `InventoryReserved`).
- `InventoryFailed` → `RefundPending` (по `RefundPayment`).
- `RefundPending` → `OrderCancelled` (по `RefundDone`).
- `DeliveryPending` → `OrderCompleted` (по `DeliveryCreated`).
- Финальные состояния: `OrderCompleted` и `OrderCancelled`.

**Персистентное состояние** — Process Manager хранит текущий статус в БД (устойчив к перезапускам):

```java
@Entity
public class OrderProcess {
    @Id private UUID processId;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private ProcessState state;  // PAYMENT_PENDING, INVENTORY_PENDING, ...
    private Instant createdAt;
    private Instant timeoutAt;
    // поля для компенсации
    private BigDecimal paidAmount;
    private List<UUID> reservedItems;
}
```

**Когда использовать:** бизнес-процессы с несколькими ветками, таймаутами, повторными попытками и частичными отказами. Инструменты: `Temporal.io`, `Axon Framework`, `Camunda`, Spring State Machine.

## Q38. (!) Что такое Event-Carried State Transfer (ECST)?

`Event-Carried State Transfer` (ECST) — паттерн, при котором событие несёт в себе **состояние** изменившейся сущности (нужные данные), а не только её идентификатор. Смысл -- избавить потребителя от обратного запроса к источнику: всё необходимое уже встроено в событие.

Зачем это нужно: при «тонком» событии (`OrderUpdated {orderId: 42}`) каждый потребитель вынужден дёргать источник за деталями. Это возвращает синхронную связность (источник должен быть доступен) и создаёт N запросов на N событий. ECST разрывает эту зависимость -- потребитель становится автономным.

**Сравнение с Event Notification:**

Два подхода к содержимому события:

- **Event Notification (ссылка):** `Producer` публикует `OrderUpdated {orderId: 42}` в `Broker`; `Broker` доставляет его `Consumer`, и тот вынужден сделать обратный вызов `GET /orders/42` к `Producer` за деталями.
- **Event-Carried State Transfer (данные):** `Producer` публикует `OrderUpdated {id:42, status:'SHIPPED', items:[...]}` в `Broker`; `Broker` доставляет его `Consumer` -- обратный вызов не нужен, все данные уже в событии.

**Преимущества:**
- Слабая связность: потребители автономны, не зависят от доступности источника
- Производительность: нет N запросов при N событиях
- Replay: при пересоздании проекции все данные уже в событиях

**Недостатки:**
- Размер сообщений больше (payload крупнее)
- Нельзя убрать поле из события без нарушения совместимости потребителей
- При большом числе полей события раздуваются

**Практический подход:** включать в событие **данные, необходимые потребителям**, не весь агрегат. Оставить `aggregateId` для случаев, когда нужны детали.

```java
// Event Notification — потребитель должен сделать запрос
public record OrderStatusChangedEvent(UUID orderId, String newStatus) {}

// Event-Carried State Transfer — всё нужное в событии
public record OrderShippedEvent(
    UUID orderId,
    String status,          // "SHIPPED"
    String trackingNumber,
    Address shippingAddress,
    List<OrderItem> items,
    Instant shippedAt
) {}
```

## Q39. Как проектировать стратегии компенсации в EDA?

Компенсирующие транзакции в `EDA` — механизм «отмены» уже выполненных шагов при сбое в Saga. Ключевое отличие от `2PC`: откатить нечего -- каждый шаг уже закоммичен в своей БД. Поэтому компенсация **семантически** обращает эффект новым действием (возврат денег вместо отмены списания), а не делает технический rollback. Это меняет требования к проектированию: компенсация -- такая же полноценная операция, как и прямая, и её тоже надо делать надёжной.

**Принципы проектирования:**

1. **Идемпотентность** — компенсация должна выполняться безопасно несколько раз:
```java
// Плохо: дважды вернёт деньги
void refund(UUID orderId) {
    payment.refund(amount);
}

// Хорошо: idempotency key предотвращает повтор
void refund(UUID orderId) {
    if (refundRepo.existsByOrderId(orderId)) return;
    payment.refund(amount);
    refundRepo.save(new Refund(orderId, amount));
}
```

2. **Не всегда возможна** — «нельзя разослать неразосланные письма»; для таких шагов используют **pivot transaction** (точку невозврата):
```
OrderCreated → PaymentCharged (PIVOT) → EmailSent → DeliveryCreated
                    ↑
         До этой точки — компенсации возможны
         После — только компенсации вперёд (forward recovery)
```

3. **Явные компенсирующие команды** в протоколе:

| Прямая транзакция | Компенсирующая |
|-------------------|----------------|
| `ReserveInventory` | `ReleaseInventory` |
| `ChargePayment` | `RefundPayment` |
| `CreateOrder` | `CancelOrder` |
| `LockAccount` | `UnlockAccount` |

4. **Таймауты и Dead Letter Queue** — если компенсация зависает, необходимо переместить событие в `DLQ` для ручного разбора инцидента.

## Q40. Как организовать версионирование топиков Kafka при эволюции схемы?

Эволюция схемы событий — неизбежная проблема долгоживущих `EDA`-систем: требования меняются, а несовместимое изменение схемы молча ломает потребителей в рантайме. Стратегия зависит от того, совместимо изменение или нет: совместимые держат в одном топике под контролем Schema Registry, несовместимые (мажорные) разводят по версиям топика. Ниже -- оба механизма.

**Стратегии совместимости (Confluent Schema Registry):**

| Тип совместимости | Что можно | Что нельзя |
|-------------------|-----------|-----------|
| **BACKWARD** | Добавить поле с default | Удалить обязательное поле |
| **FORWARD** | Удалить поле с default | Добавить обязательное поле |
| **FULL** | Только добавление с default | Удаление, смена типа |
| **NONE** | Всё | — (только при мажорной версии) |

**Паттерн версионирования через заголовок:**

```java
// Producer: добавляет версию в заголовок
ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
record.headers().add("schema-version", "2".getBytes());

// Consumer: маршрутизация по версии
@KafkaListener(topics = "orders")
public void consume(ConsumerRecord<String, byte[]> record) {
    String version = new String(record.headers().lastHeader("schema-version").value());
    switch (version) {
        case "1" -> handleV1(deserializeV1(record.value()));
        case "2" -> handleV2(deserializeV2(record.value()));
        default -> dlqSender.send(record, "unknown-version");
    }
}
```

**Топик-per-версия** (для мажорных несовместимых изменений):
```
orders.v1   — старый контракт (потребители мигрируют постепенно)
orders.v2   — новый контракт
```

Поток при двух версиях топика:

- `Producer v2` пишет в `orders.v2` (новый контракт) и через bridge -- в `orders.v1` (старый контракт).
- `orders.v1` читает `Consumer v1` (старый), `orders.v2` читает `Consumer v2` (новый).
- Bridge -- это конвертер `v2→v1`, обеспечивающий обратную совместимость для ещё не мигрировавших потребителей.

**Правила эволюции схемы Avro/Protobuf:**
- Всегда добавляйте новые поля с `default` значением
- Никогда не меняйте тип существующего поля
- Никогда не удаляйте поля — только помечайте как `deprecated`
- Для несовместимых изменений — новый топик + миграция потребителей

---

## See also

- [Apache Kafka](../messaging/kafka-interview.md) — брокер сообщений: партиции, consumer groups, exactly-once
- [Микросервисы](microservices-interview.md) — EDA как основа межсервисного взаимодействия
- [Распределённые системы](distributed-systems-interview.md) — идемпотентность, гарантии доставки и консенсус
- [Паттерны согласованности](consistency-patterns-interview.md) — eventual consistency, Saga, Outbox Pattern
- [Паттерны проектирования](../design-patterns/design-patterns-interview.md) — GoF-паттерны Observer и Mediator в контексте EDA
- [Spring Framework](../frameworks/spring/spring-framework-interview.md) — ApplicationEvent, @EventListener и Spring Integration
- [Observability](../monitoring/observability-interview.md) — трассировка событий, метрики и алертинг в EDA-системах

- [API Gateway](api-gateway-interview.md)
- [BFF Pattern](bff-pattern-interview.md)
- [Стратегии кэширования](caching-strategies-interview.md)
- [CAP-теорема](cap-theorem-interview.md)
- [Clean Architecture](clean-architecture-interview.md)
- [Паттерны согласованности](consistency-patterns-interview.md)
