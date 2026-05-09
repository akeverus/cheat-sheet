---
title: "Вопросы на собеседовании: Saga Pattern"
description: "Полное покрытие паттерна Saga: оркестрация vs хореография, компенсирующие транзакции, семантические блокировки, реализация на Spring Boot с Axon/Eventuate Tram, Outbox, Kafka, сравнение с 2PC, тестирование, антипаттерны."
tags:
  - interview
  - architecture
  - saga-pattern-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Saga Pattern"
  - "Saga pattern interview"
  - "Saga собеседование"
prerequisites:
  - "[[saga-pattern]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Saga Pattern`

**Saga Pattern** -- один из ключевых паттернов управления распределёнными транзакциями в микросервисной архитектуре. Вместо `2PC` (двухфазной фиксации), невозможной или дорогой в распределённых системах, `Saga` разбивает бизнес-операцию на последовательность локальных транзакций, каждая из которых имеет компенсирующее действие. Тема обязательна на Senior-собеседованиях: проверяет понимание `eventual consistency`, компенсаций, семантических блокировок и практической реализации на `Spring Boot` с `Axon Framework`, `Eventuate Tram` или через `Kafka` + `Outbox`.

## Полезные ссылки

### Официальная документация

- [Pattern: Saga (microservices.io)](https://microservices.io/patterns/data/saga.html) -- каноническое описание паттерна от Chris Richardson
- [Saga Pattern in Microservices (Baeldung)](https://www.baeldung.com/cs/saga-pattern-microservices) -- обзор паттерна и видов координации
- [Two-Phase Commit vs Saga Pattern (Baeldung)](https://www.baeldung.com/cs/two-phase-commit-vs-saga-pattern) -- сравнение с 2PC
- [Saga Pattern in a Microservices Architecture with Spring Boot (Baeldung)](https://www.baeldung.com/orkes-conductor-saga-pattern-spring-boot) -- реализация на Spring Boot с Orkes Conductor
- [Saga Design Pattern (Microsoft Learn)](https://learn.microsoft.com/en-us/azure/architecture/patterns/saga) -- Azure Architecture Center
- [Axon Framework Sagas (docs.axoniq.io)](https://docs.axoniq.io/axon-framework-reference/5.0/sagas/implementation/) -- реализация Saga в Axon
- [Eventuate Tram Sagas (eventuate.io)](https://eventuate.io/docs/manual/eventuate-tram/latest/getting-started-eventuate-tram-sagas.html) -- фреймворк для orchestration-based Saga
- [Transactional Outbox (microservices.io)](https://microservices.io/patterns/data/transactional-outbox.html) -- Outbox для atomic publish
- [Developing Sagas Part 1--4 (Chris Richardson)](https://microservices.io/post/microservices/2019/07/09/developing-sagas-part-1.html) -- цикл статей с примерами

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Saga**
- [Q1. (!) Что такое Saga Pattern и зачем он нужен?](#q1--что-такое-saga-pattern-и-зачем-он-нужен)
- [Q2. (!) Какую проблему решает Saga в микросервисной архитектуре?](#q2--какую-проблему-решает-saga-в-микросервисной-архитектуре)
- [Q3. Что такое локальная транзакция в Saga?](#q3-что-такое-локальная-транзакция-в-saga)
- [Q4. Какие ACID-свойства сохраняет Saga, а какие теряет?](#q4-какие-acid-свойства-сохраняет-saga-а-какие-теряет)
- [Q5. Что такое semantic consistency и чем она отличается от strong consistency?](#q5-что-такое-semantic-consistency-и-чем-она-отличается-от-strong-consistency)

**Оркестрация**
- [Q6. (!) Что такое orchestration-based Saga?](#q6--что-такое-orchestration-based-saga)
- [Q7. Как устроен saga orchestrator?](#q7-как-устроен-saga-orchestrator)
- [Q8. Какие преимущества и недостатки у оркестрации?](#q8-какие-преимущества-и-недостатки-у-оркестрации)
- [Q9. (!) Как реализовать оркестратор как state machine?](#q9--как-реализовать-оркестратор-как-state-machine)

**Хореография**
- [Q10. (!) Что такое choreography-based Saga?](#q10--что-такое-choreography-based-saga)
- [Q11. Как события связывают шаги хореографии?](#q11-как-события-связывают-шаги-хореографии)
- [Q12. (!) Оркестрация vs хореография -- когда что выбрать?](#q12--оркестрация-vs-хореография----когда-что-выбрать)
- [Q13. Какой риск несёт хореография при росте числа сервисов?](#q13-какой-риск-несёт-хореография-при-росте-числа-сервисов)

**Компенсирующие транзакции**
- [Q14. (!) Что такое compensating transaction?](#q14--что-такое-compensating-transaction)
- [Q15. (!) Типы шагов: compensable, pivot, retryable](#q15--типы-шагов-compensable-pivot-retryable)
- [Q16. Почему компенсация не равна откату (rollback)?](#q16-почему-компенсация-не-равна-откату-rollback)
- [Q17. Что делать, если compensating transaction тоже упала?](#q17-что-делать-если-compensating-transaction-тоже-упала)

**Isolation и countermeasures**
- [Q18. (!) Почему Saga не обеспечивает isolation и какие возникают аномалии?](#q18--почему-saga-не-обеспечивает-isolation-и-какие-возникают-аномалии)
- [Q19. (!) Что такое semantic lock?](#q19--что-такое-semantic-lock)
- [Q20. Что такое commutative updates, pessimistic view, reread, version file?](#q20-что-такое-commutative-updates-pessimistic-view-reread-version-file)
- [Q21. Что такое by value strategy?](#q21-что-такое-by-value-strategy)

**Сравнение с 2PC**
- [Q22. (!) Saga vs 2PC -- ключевые отличия](#q22--saga-vs-2pc----ключевые-отличия)
- [Q23. Почему 2PC плохо работает в микросервисах?](#q23-почему-2pc-плохо-работает-в-микросервисах)
- [Q24. Что такое TCC (Try-Confirm-Cancel) и чем он отличается от Saga?](#q24-что-такое-tcc-try-confirm-cancel-и-чем-он-отличается-от-saga)

**Реализация на Spring Boot**
- [Q25. (!) Как реализовать оркестратор Saga на чистом Spring Boot + Kafka?](#q25--как-реализовать-оркестратор-saga-на-чистом-spring-boot--kafka)
- [Q26. (!) Как связать Saga с Outbox pattern?](#q26--как-связать-saga-с-outbox-pattern)
- [Q27. (!) Как реализовать Saga на Axon Framework?](#q27--как-реализовать-saga-на-axon-framework)
- [Q28. Как использовать Eventuate Tram для orchestration Saga?](#q28-как-использовать-eventuate-tram-для-orchestration-saga)
- [Q29. Как использовать Camunda/BPMN для Saga?](#q29-как-использовать-camundabpmn-для-saga)
- [Q30. Как реализовать persistent state saga orchestrator?](#q30-как-реализовать-persistent-state-saga-orchestrator)

**Pitfalls и антипаттерны**
- [Q31. (!) Какие антипаттерны Saga следует избегать?](#q31--какие-антипаттерны-saga-следует-избегать)
- [Q32. Что такое distributed monolith и как Saga может его спровоцировать?](#q32-что-такое-distributed-monolith-и-как-saga-может-его-спровоцировать)
- [Q33. Как бороться с зависшими (stuck) Saga?](#q33-как-бороться-с-зависшими-stuck-saga)
- [Q34. (!) Как обеспечить идемпотентность шагов Saga?](#q34--как-обеспечить-идемпотентность-шагов-saga)
- [Q35. Как бороться с out-of-order сообщениями в хореографии?](#q35-как-бороться-с-out-of-order-сообщениями-в-хореографии)

**Тестирование и наблюдаемость**
- [Q36. Как тестировать Saga?](#q36-как-тестировать-saga)
- [Q37. Как мониторить Saga в production?](#q37-как-мониторить-saga-в-production)
- [Q38. Как трассировать Saga через distributed tracing?](#q38-как-трассировать-saga-через-distributed-tracing)

**Продвинутые темы**
- [Q39. (!) Как реализовать timeout и circuit breaker в Saga?](#q39--как-реализовать-timeout-и-circuit-breaker-в-saga)
- [Q40. Как Saga взаимодействует с CQRS и Event Sourcing?](#q40-как-saga-взаимодействует-с-cqrs-и-event-sourcing)
- [Q41. Как версионировать Saga при изменении бизнес-процесса?](#q41-как-версионировать-saga-при-изменении-бизнес-процесса)
- [Q42. Long-running Saga -- особенности (часы, дни, недели)](#q42-long-running-saga----особенности-часы-дни-недели)
- [Q43. Как объяснить eventual consistency клиенту/пользователю?](#q43-как-объяснить-eventual-consistency-клиентупользователю)

---

## Q1. (!) Что такое Saga Pattern и зачем он нужен?

**Saga Pattern** -- паттерн управления распределёнными бизнес-транзакциями через последовательность локальных ACID-транзакций, каждая из которых имеет компенсирующее действие для отмены. Если один из шагов падает, оркестратор или реагирующие сервисы запускают компенсации для уже выполненных шагов.

Исторически паттерн описан в статье Hector Garcia-Molina и Kenneth Salem (1987) для long-running transactions в базах данных; в микросервисной эпохе его адаптировал Chris Richardson.

```mermaid
graph LR
    Start([Начало]) --> T1[T1: Создать заказ]
    T1 --> T2[T2: Зарезервировать товар]
    T2 --> T3[T3: Списать оплату]
    T3 --> T4[T4: Отправить]
    T4 --> Done([Успех])

    T3 -.fail.-> C2[C2: Вернуть товар]
    C2 -.-> C1[C1: Отменить заказ]
    C1 -.-> Failed([Откат])

    style T1 fill:#cde
    style T2 fill:#cde
    style T3 fill:#cde
    style T4 fill:#cde
    style C1 fill:#fcc
    style C2 fill:#fcc
```

**Зачем нужен:**
- В микросервисах каждый сервис имеет свою БД (`Database per Service`) -- единая ACID-транзакция невозможна
- `2PC` (двухфазная фиксация) блокирует ресурсы и плохо масштабируется
- Нужна согласованность данных между сервисами без глобальных блокировок

**Гарантии:** `Saga` обеспечивает `ACD` (Atomicity через компенсации, Consistency, Durability), но теряет `Isolation` -- требует `countermeasures` (см. [Q18](#q18--почему-saga-не-обеспечивает-isolation-и-какие-возникают-аномалии)).


> [!mcq]
> - [ ] Saga — это просто цепочка синхронных REST-вызовов с try/catch для отката | ❌ ПОСЛЕДСТВИЕ: синхронная цепочка не атомарна — если сервис упал в середине, REST-ответа не будет, состояние несогласованно; Saga использует async events/commands
> - [ ] Saga гарантирует ACID, включая Isolation, для распределённых транзакций | ❌ ПОСЛЕДСТВИЕ: Saga обеспечивает ACD но НЕ Isolation; промежуточные состояния видны другим транзакциям → требуются countermeasures (semantic lock, commutative updates)
> - [x] Saga = цепочка локальных ACID-транзакций с компенсирующими действиями; при сбое запускаются компенсации уже выполненных шагов | ✓ ПРИМЕНЯТЬ: Place Order, Book Travel, Process Payment в микросервисах 📋 ПРАВИЛО: Saga = local ACID + compensating txns; eventual consistency 🔗 См. Q2
> - [ ] Saga использует единую распределённую БД для всех сервисов | ❌ ПОСЛЕДСТВИЕ: единая БД нарушает Database per Service принцип; Saga именно потому и нужна, что каждый сервис имеет свою БД

## Q2. (!) Какую проблему решает Saga в микросервисной архитектуре?

В монолите бизнес-операция «создать заказ» (создание записи Order, резерв Inventory, списание Payment) выполняется в одной `@Transactional` -- `ACID` из коробки. В микросервисах эти данные живут в разных БД, и классическая транзакция не охватывает их.

**Варианты решения и их проблемы:**

| Решение | Проблема |
|---|---|
| `XA / 2PC` через `JTA` | Coordinator becomes SPOF, блокировки на уровне БД, медленно, плохо работает между разными типами СУБД и очередей |
| Единая БД на все сервисы | Нарушает принцип `Database per Service`, теряем независимость сервисов |
| Синхронные REST-цепочки с try/catch | Нет атомарности: если упал сервис в середине -- данные в несогласованном состоянии; зависимость от времени жизни HTTP-соединения |
| **Saga** | Атомарность через компенсации, `eventual consistency`, decoupling через события/команды |

`Saga` -- стандарт де-факто для бизнес-процессов типа `Place Order`, `Book Travel`, `Process Payment`, где нужно скоординировать несколько автономных сервисов без блокирующих глобальных транзакций.


> [!mcq]
> - [ ] 2PC (XA) через JTA лучше Saga — он гарантирует полный ACID | ❌ ПОСЛЕДСТВИЕ: 2PC делает coordinator SPOF; блокирует ресурсы всех участников до commit; плохо работает между разными типами БД и MQ
> - [ ] REST-цепочки с try/catch дают ту же атомарность что и Saga | ❌ ПОСЛЕДСТВИЕ: при сбое в середине цепочки нет механизма отката выполненных шагов; Saga гарантирует compensating transactions
> - [x] Saga решает отсутствие кросс-сервисных ACID-транзакций: каждый сервис имеет свою БД, Saga координирует через события/команды с компенсациями | ✓ ПРИМЕНЯТЬ: когда нужна бизнес-атомарность без глобальных блокировок 📋 ПРАВИЛО: нет кросс-сервисного ACID → Saga с compensating txns 🔗 См. Q1
> - [ ] Единая БД для всех сервисов решает проблему лучше Saga | ❌ ПОСЛЕДСТВИЕ: нарушает Database per Service; создаёт shared database coupling; убирает независимость деплоя сервисов

## Q3. Что такое локальная транзакция в Saga?

Локальная транзакция -- это атомарная операция в пределах одного сервиса и одной БД, которая выполняется под обычной `@Transactional` и удовлетворяет `ACID`. `Saga` = цепочка таких локальных транзакций: $T_1, T_2, \dots, T_n$, с компенсациями $C_1, C_2, \dots, C_{n-1}$.

**Ключевое условие:** каждая $T_i$ должна коммитить свои изменения немедленно, не держа блокировки до конца Saga. Это делает шаги видимыми для других транзакций (нарушение `isolation`) -- зато даёт высокую пропускную способность.

```java
@Service
@Transactional
public class OrderService {

    public Order createOrder(CreateOrderCommand cmd) {
        // Локальная транзакция: пишем в БД + публикуем событие через Outbox
        Order order = new Order(cmd.customerId(), cmd.items(), OrderStatus.PENDING);
        orderRepository.save(order);
        outboxRepository.save(new OutboxEvent("OrderCreated", order.toPayload()));
        return order;
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Локальная транзакция в Saga держит блокировки до завершения всей Saga | ❌ ПОСЛЕДСТВИЕ: Saga именно потому и нужна, что каждый шаг коммитится немедленно (нет глобальных блокировок); промежуточное состояние видно, но isolation — это жертва которую осознанно принимают

## Q4. Какие ACID-свойства сохраняет Saga, а какие теряет?

| Свойство | В локальной транзакции | В Saga в целом |
|---|---|---|
| **A**tomicity | Да | Обеспечивается компенсациями (`semantic atomicity`) |
| **C**onsistency | Да | Да -- в конечном счёте (`eventual consistency`) |
| **I**solation | Да | **Нет** -- промежуточные состояния видны |
| **D**urability | Да | Да -- каждый шаг коммитится в свою БД |

Chris Richardson называет это `ACD`: Saga даёт Atomicity (через compensations), Consistency, Durability, но не Isolation. Это главная «головная боль» при разработке: два одновременных Saga могут увидеть промежуточные состояния друг друга, из-за чего возникают аномалии -- `lost update`, `dirty read`, `fuzzy read` (non-repeatable). Решение -- `countermeasures` ([Q19](#q19--что-такое-semantic-lock)--[Q21](#q21-что-такое-by-value-strategy)).


> [!mcq]
> - [ ] Saga сохраняет Isolation — промежуточные состояния не видны другим транзакциям | ❌ ПОСЛЕДСТВИЕ: каждый шаг коммитится сразу; другие Saga могут видеть Order=PENDING, Inventory=reserved → dirty read аномалия → нужны countermeasures (semantic lock)
> - [ ] Saga нарушает Durability — данные теряются при сбое | ❌ ПОСЛЕДСТВИЕ: каждый шаг персистируется в свою БД (ACID локально); Durability сохранена; теряется только глобальная Isolation
> - [x] Saga сохраняет ACD: Atomicity через compensating txns, Consistency (eventual), Durability; теряет Isolation | ✓ ПРИМЕНЯТЬ: когда допустима eventual consistency и нет требований к изоляции 📋 ПРАВИЛО: Saga = ACD без I; compensating txns = semantic atomicity 🔗 См. Q18
> - [ ] Saga сохраняет полный ACID, но только на уровне оркестратора | ❌ ПОСЛЕДСТВИЕ: оркестратор не дает ACID участникам; он лишь координирует; каждый участник имеет только свои локальные ACID гарантии

## Q5. Что такое semantic consistency и чем она отличается от strong consistency?

**Strong consistency** (сильная согласованность) -- после коммита транзакции любой следующий запрос видит обновлённые данные, нет промежуточных состояний. Даёт ACID.

**Semantic consistency** (семантическая согласованность) -- система становится согласованной в конечном счёте по бизнес-смыслу. Промежуточные состояния возможны (заказ `PENDING`, товар «временно зарезервирован»), но бизнес-правила всегда выполняются: либо полный успех, либо полный откат через компенсации.

```mermaid
sequenceDiagram
    participant C as Client
    participant O as Order Service
    participant I as Inventory Service
    participant P as Payment Service
    C->>O: POST /orders
    O->>O: Order(PENDING)
    O-->>C: 202 Accepted
    Note over O,I: время t1: Order=PENDING, Inventory=resv
    O->>I: ReserveItems
    I-->>O: Reserved
    O->>P: Charge
    P-->>O: Charged
    O->>O: Order(CONFIRMED)
    Note over C: клиенту нужно polling/webhook для финального статуса
```

На собеседовании важно подчеркнуть: `Saga` требует переосмысления UX -- клиент получает 202 Accepted + идентификатор саги, статус узнаёт через polling/SSE/webhook.


> [!mcq]
> - [ ] Semantic consistency = strong consistency, просто другое название | ❌ ПОСЛЕДСТВИЕ: strong consistency — любой запрос после commit видит новые данные; semantic — данные согласованы eventual (промежуточные состояния допустимы)
> - [ ] При semantic consistency клиент получает финальный результат в том же HTTP-ответе | ❌ ПОСЛЕДСТВИЕ: Saga async — клиент получает 202 Accepted + saga ID; финальный статус через polling/webhook; синхронный ответ нарушит async nature
> - [x] Semantic consistency: система достигает согласованности eventual; промежуточные состояния (PENDING, reserved) допустимы; бизнес-правила всегда выполняются | ✓ ПРИМЕНЯТЬ: Saga с 202 Accepted + polling/SSE для статуса 📋 ПРАВИЛО: semantic = eventual consistent по бизнес-смыслу; не strong consistency 🔗 См. Q4
> - [ ] Semantic consistency гарантирует что два параллельных Saga не видят состояния друг друга | ❌ ПОСЛЕДСТВИЕ: это противоположное — semantic consistency допускает видимость промежуточных состояний; для изоляции нужны countermeasures

## Q6. (!) Что такое orchestration-based Saga?

В orchestration-based Saga есть **центральный координатор** (`Saga Orchestrator`), который хранит состояние процесса и по очереди отправляет командные сообщения участникам, дожидаясь ответа (`reply`). На основании ответов оркестратор решает, какой шаг выполнить дальше и какие компенсации запустить при ошибке.

```mermaid
sequenceDiagram
    participant O as Orchestrator
    participant Ord as Order Service
    participant Inv as Inventory
    participant Pay as Payment

    O->>Ord: CreateOrderCmd
    Ord-->>O: OrderCreated
    O->>Inv: ReserveItemsCmd
    Inv-->>O: ItemsReserved
    O->>Pay: ChargeCmd
    Pay-->>O: PaymentFailed
    O->>Inv: ReleaseItemsCmd (компенсация)
    Inv-->>O: ItemsReleased
    O->>Ord: CancelOrderCmd (компенсация)
    Ord-->>O: OrderCancelled
```

**Характеристики:**
- Oркестратор -- persistent сущность (запись в БД), чтобы пережить падение
- Общается с участниками через request/async-response (обычно Kafka/RabbitMQ)
- Участники не знают друг о друге, знают только своего оркестратора
- Логика «что делать при ошибке» централизована

**Фреймворки:** `Axon Framework`, `Eventuate Tram`, `Camunda BPMN`, `Orkes Conductor`, `Netflix Conductor`, `Temporal`.


> [!mcq]
> - [ ] Orchestration Saga — оркестратор вызывает участников синхронно (REST) и ждёт ответа | ❌ ПОСЛЕДСТВИЕ: синхронный оркестратор блокирует thread на всё время Saga (минуты); при сбое участника — зависание; нужен async request/reply через MQ
> - [x] Центральный оркестратор хранит SagaState в БД, отправляет команды участникам через MQ, переходит по состояниям на reply | ✓ ПРИМЕНЯТЬ: сложные многошаговые бизнес-процессы с branching и retry 📋 ПРАВИЛО: orchestrator = persistent state machine + async command/reply 🔗 См. Q6
> - [ ] Оркестратор содержит всю бизнес-логику сервисов — решает за них что делать | ❌ ПОСЛЕДСТВИЕ: god-object антипаттерн; оркестратор должен содержать только flow-логику; бизнес-правила остаются в агрегатах участников
> - [ ] При ошибке оркестратор откатывает изменения через одну общую БД | ❌ ПОСЛЕДСТВИЕ: у каждого участника своя БД; откат через compensating commands в MQ, а не через общую БД

## Q7. Как устроен saga orchestrator?

Оркестратор -- это обычно persistent state machine:

1. Получает начальное событие/команду (например, `OrderPlaced`)
2. Создаёт запись Saga в БД со состоянием (`currentStep`, `completedSteps`, `sagaData`)
3. Для текущего шага формирует команду и отправляет через брокер
4. При получении reply обновляет состояние и переходит к следующему шагу
5. При ошибке -- идёт назад по completed steps и запускает компенсации

```java
@Entity
public class CreateOrderSagaState {
    @Id private UUID sagaId;
    private UUID orderId;
    private BigDecimal amount;
    private SagaStep currentStep;     // CREATE_ORDER, RESERVE_INVENTORY, CHARGE_PAYMENT, CONFIRM
    private SagaStatus status;         // IN_PROGRESS, COMPENSATING, COMPLETED, FAILED
    private List<SagaStep> completedSteps;
    private Instant lastUpdate;
    @Version private Long version;     // оптимистичная блокировка
}
```

Важно: оркестратор **не содержит бизнес-логики**, только flow; бизнес-решения принимают агрегаты участников.


> [!mcq]
> - [ ] Оркестратор не нужен persistent state — он stateless, восстанавливается из событий | ❌ ПОСЛЕДСТВИЕ: без persistent SagaState при рестарте оркестратора текущий шаг неизвестен → Saga зависает или запускается повторно → дублирующие транзакции
> - [ ] SagaState хранится только в памяти — падение оркестратора = потеря Saga | ❌ ПОСЛЕДСТВИЕ: Saga зависшие in-flight не восстановятся; нужен persistent state (БД + @Version для оптимистичной блокировки)
> - [x] SagaState персистируется в БД (sagaId, currentStep, status, @Version); оркестратор отправляет command → участник → reply → обновить state → следующий шаг | ✓ ПРИМЕНЯТЬ: Axon Framework, Eventuate Tram, Temporal для production Saga 📋 ПРАВИЛО: SagaState = persistent + versioned; flow без бизнес-логики 🔗 См. Q6
> - [ ] Оркестратор содержит бизнес-логику решений — это правильная ответственность | ❌ ПОСЛЕДСТВИЕ: god-object; при изменении бизнес-правил нужно менять оркестратор вместо участников; нарушение SRP

## Q8. Какие преимущества и недостатки у оркестрации?

**Преимущества:**
- Централизованная логика -- легко читать, отлаживать, менять flow
- Явные состояния -- проще мониторить, подойдёт BPMN-диаграмма
- Легче добавить timeout, retry, circuit breaker на уровне оркестратора
- Проще реализовать сложные ветвления и условия
- Участники не знают друг о друге -- `low coupling` между ними

**Недостатки:**
- Оркестратор -- single point of failure (нужна HA и persistent state)
- Риск превратить оркестратор в `god-object` с бизнес-логикой (антипаттерн)
- Доп. сервис/модуль, который нужно разрабатывать и поддерживать
- Coupling участников к модели команд оркестратора


> [!mcq]
> - [ ] Главный плюс оркестрации — участники не coupling к оркестратору | ❌ ПОСЛЕДСТВИЕ: участники знают модели команд оркестратора — это coupling; главный плюс — централизованная flow-логика и видимость процесса
> - [x] Плюсы: централизованная flow-логика, явные состояния, мониторинг; минусы: SPOF (нужна HA), риск god-object, доп. сервис | ✓ ПРИМЕНЯТЬ: сложные multi-step процессы с branching; choreography для простых 📋 ПРАВИЛО: orchestration = visible flow + centralized retry; choreography = decoupled events 🔗 См. Q10
> - [ ] Недостаток оркестрации — нельзя добавить retry и timeout | ❌ ПОСЛЕДСТВИЕ: оркестратор как раз удобен для retry/timeout/circuit breaker на уровне шагов; это одно из главных преимуществ
> - [ ] Оркестратор должен быть stateless для простоты | ❌ ПОСЛЕДСТВИЕ: stateless оркестратор теряет позицию в Saga при рестарте; persistent state обязателен для корректного восстановления

## Q9. (!) Как реализовать оркестратор как state machine?

Классическая реализация -- явный enum состояний и таблица переходов:

```java
public enum CreateOrderState {
    STARTED,
    ORDER_CREATED,
    INVENTORY_RESERVED,
    PAYMENT_CHARGED,
    COMPLETED,
    COMPENSATING_PAYMENT,
    COMPENSATING_INVENTORY,
    COMPENSATING_ORDER,
    FAILED
}

@Component
public class CreateOrderSagaManager {

    public void handle(SagaEvent event, CreateOrderSagaState state) {
        switch (state.getCurrentState()) {
            case STARTED -> {
                commandGateway.send(new CreateOrderCommand(...));
                state.setCurrentState(ORDER_CREATED);
            }
            case ORDER_CREATED -> {
                if (event instanceof OrderCreated ok) {
                    commandGateway.send(new ReserveInventoryCommand(...));
                    state.setCurrentState(INVENTORY_RESERVED);
                } else if (event instanceof OrderCreationFailed) {
                    state.setCurrentState(FAILED);
                }
            }
            case INVENTORY_RESERVED -> {
                if (event instanceof InventoryReserved) {
                    commandGateway.send(new ChargePaymentCommand(...));
                    state.setCurrentState(PAYMENT_CHARGED);
                } else {
                    commandGateway.send(new CancelOrderCommand(...));
                    state.setCurrentState(COMPENSATING_ORDER);
                }
            }
            // ... и так далее
        }
        sagaRepository.save(state);
    }
}
```

В продакшене лучше использовать готовые решения: `Spring Statemachine`, `Axon Saga`, `Temporal workflows`, `Camunda BPMN` -- они дают persistence, таймеры, версионирование, визуализацию.


> [!mcq]
> - [ ] Оркестратор вызывает сервисы синхронно через REST, не нужен message broker | ❌ ПОСЛЕДСТВИЕ: синхронный оркестратор = temporal coupling; при падении любого шага — всё зависает; нужен async + retry через MQ
> - [x] Оркестратор хранит state machine, посылает команды сервисам через MQ, ждёт ответных событий; Temporal/Axon/Camunda дают persistence + timeout + retry из коробки | ✓ ПРИМЕНЯТЬ: ≥4 шагов, сложное branching, нужна audit trail 📋 ПРАВИЛО: orchestration = explicit state machine = debuggable flow 🔗 См. Q8
> - [ ] Оркестратор должен выполнять локальные транзакции всех сервисов сам | ❌ ПОСЛЕДСТВИЕ: оркестратор только координирует; локальные транзакции остаются в каждом сервисе — иначе нарушается Single Responsibility и data isolation
> - [ ] При сбое оркестратора Saga откатывается автоматически без дополнительной конфигурации | ❌ ПОСЛЕДСТВИЕ: нужен HA оркестратор (Temporal cluster, Camunda cluster) + compensating transactions явно прописаны; без этого partial execution зависает

## Q10. (!) Что такое choreography-based Saga?

В choreography-based Saga **нет центрального координатора**: каждый сервис публикует доменные события после своей локальной транзакции, а другие сервисы подписываются на эти события и реагируют. Координация распределена между участниками.

```mermaid
graph LR
    A[Order Service] -->|OrderCreated| B[(Kafka)]
    B --> C[Inventory Service]
    C -->|ItemsReserved| B
    B --> D[Payment Service]
    D -->|PaymentCharged| B
    B --> A
    D -.PaymentFailed.-> B
    B -.-> C
    C -->|ItemsReleased| B
    B --> A
```

**Пример flow `PlaceOrder`:**
1. `Order Service` сохраняет `Order(PENDING)` и публикует `OrderCreated`
2. `Inventory Service` слушает `OrderCreated`, резервирует товар, публикует `ItemsReserved`
3. `Payment Service` слушает `ItemsReserved`, списывает деньги, публикует `PaymentCharged` или `PaymentFailed`
4. При `PaymentFailed`: `Inventory Service` слушает и отменяет резерв (`ItemsReleased`); `Order Service` слушает и отменяет заказ

Подробнее про брокеры сообщений -- в [event-driven паттернах](event-driven-patterns-interview.md).


> [!mcq]
> - [ ] Choreography лучше Orchestration всегда — нет SPOF | ❌ ПОСЛЕДСТВИЕ: без центрального координатора сложно отследить Saga in-flight, реализовать conditional branching, добавить timeout и retry на уровне шагов
> - [ ] В Choreography Saga сервисы вызывают друг друга напрямую через REST | ❌ ПОСЛЕДСТВИЕ: прямые REST-вызовы создают temporal coupling; при сбое получателя — потеря события; нужен MQ как буфер
> - [x] Нет центрального координатора; каждый сервис публикует доменное событие и реагирует на события других; coordination через Kafka topic с correlation-id | ✓ ПРИМЕНЯТЬ: простые 2-3 шаговые Saga без сложного branching 📋 ПРАВИЛО: choreography = event-driven + decoupled; orchestration = centralized flow 🔗 См. Q6
> - [ ] Choreography требует общей БД для передачи состояния между сервисами | ❌ ПОСЛЕДСТВИЕ: состояние передаётся через события (payload с sagaId/orderId); каждый сервис имеет свою БД

## Q11. Как события связывают шаги хореографии?

Каждое событие -- триггер для следующего сервиса. Связь через `publish-subscribe` в брокере ([Kafka](../messaging/kafka-interview.md), [RabbitMQ](../messaging/rabbitmq-interview.md)), с `correlation-id` = `sagaId` или `orderId` для связывания событий одной Saga.

```java
@Component
public class InventoryEventHandler {

    @KafkaListener(topics = "order-events")
    @Transactional
    public void on(OrderCreatedEvent event) {
        // локальная транзакция: резерв + публикация результата через Outbox
        InventoryReservation reservation = inventoryService.reserve(
            event.orderId(), event.items()
        );
        outboxService.publish(new ItemsReservedEvent(
            event.orderId(),        // correlation id
            reservation.getId(),
            event.items()
        ));
    }

    @KafkaListener(topics = "payment-events")
    @Transactional
    public void on(PaymentFailedEvent event) {
        // компенсация
        inventoryService.release(event.orderId());
        outboxService.publish(new ItemsReleasedEvent(event.orderId()));
    }
}
```


> [!mcq]
> - [ ] Сервисы вызывают друг друга напрямую через REST — событий достаточно для согласованности | ❌ ПОСЛЕДСТВИЕ: REST-вызовы без MQ = при сболе потребителя событие теряется; нужен Kafka/RabbitMQ для at-least-once delivery с retention
> - [ ] Для correlation-id достаточно использовать timestamp — он уникален | ❌ ПОСЛЕДСТВИЕ: timestamp неуникален при конкурентных запросах; correlation-id = UUID генерируется в точке входа и прокидывается через все события
> - [x] Каждое событие несёт sagaId/orderId как correlation-id; сервис публикует событие → следующий сервис реагирует; Outbox Pattern гарантирует at-least-once | ✓ ПРИМЕНЯТЬ: decoupled choreography flow; Outbox = событие в той же транзакции что и state change 📋 ПРАВИЛО: correlation-id + Outbox = надёжная choreography без потерь 🔗 См. Q10
> - [ ] Kafka автоматически удаляет дублирующиеся события — idempotency не нужна | ❌ ПОСЛЕДСТВИЕ: Kafka доставляет at-least-once; идемпотентность на стороне потребителя обязательна (processedEventIds в БД)

## Q12. (!) Оркестрация vs хореография -- когда что выбрать?

| Критерий | Оркестрация | Хореография |
|---|---|---|
| Центр управления | Оркестратор | Нет -- распределён |
| Количество шагов | Много (>4) -- удобнее | 2--4 -- не создаёт сложности |
| Ветвления и условия | Легко -- в одном месте | Сложно -- размазано |
| Сложность отладки | Проще -- логи оркестратора | Сложнее -- трассировка по брокеру |
| Coupling | Participants ↔ Orchestrator | Все ↔ все через события |
| Риск SPOF | Да -- нужен HA | Нет |
| Антипаттерн | `god-orchestrator` с логикой | `distributed monolith` через события |
| Наблюдаемость | Хорошая -- state в БД | Требует distributed tracing |

**Rule of thumb:**
- **Оркестрация** -- сложные многошаговые процессы с ветвлениями, BPMN-like workflows, где важен явный контроль и audit trail (обработка заказа с 7+ шагами, кредитные процессы, supply chain)
- **Хореография** -- короткие реактивные цепочки из 2--4 шагов без сложного flow (notification pipeline, cache invalidation cascade, simple order flow)
- Можно комбинировать: крупный оркестратор, внутри отдельные хореографические цепочки


> [!mcq]
> - [ ] Оркестрацию выбирать всегда — хореография принципиально ненадёжна | ❌ ПОСЛЕДСТВИЕ: хореография отлично работает для 2-4 шагов; принудительная оркестрация добавляет доп. сервис и сложность где это не нужно
> - [ ] Хореографию выбирать когда нужен audit trail всего процесса | ❌ ПОСЛЕДСТВИЕ: audit trail сложен при хореографии (нужен distributed tracing); оркестрация даёт state в БД = готовый audit
> - [x] Оркестрация: сложные 5+ шаговые процессы с branching, audit trail; Choreography: 2-4 шага без ветвлений | ✓ ПРИМЕНЯТЬ: кредитный процесс → orchestration; cache invalidation cascade → choreography 📋 ПРАВИЛО: >4 шагов или нужен явный контроль → orchestration 🔗 См. Q6
> - [ ] Можно смешивать оркестрацию и хореографию только теоретически | ❌ ПОСЛЕДСТВИЕ: комбинирование — стандартная практика; внешний оркестратор с внутренними event-driven цепочками между несколькими сервисами

## Q13. Какой риск несёт хореография при росте числа сервисов?

Главный риск -- превращение в **implicit coupling monster**:

- Каждый сервис подписан на десятки событий и публикует свои -- flow становится невидимым
- Нет одного места, где бы описан полный процесс -- только через чтение кода всех участников
- Добавить новый шаг = обновить код нескольких сервисов и все их топики
- Cyclic dependencies: сервис A подписан на B, B на A -- легко получить event loop
- Отладка через брокер и распределённые логи, без общей view
- Rollback при частичном сбое распределён и ненадёжен

В книге Sam Newman'а это называют `distributed big ball of mud`. Когда цепочка выросла до 5+ шагов -- рассматривайте миграцию на оркестрацию.


> [!mcq]
> - [ ] При росте до 7+ сервисов в хореографии нужно добавить центральный event registry | ❌ ПОСЛЕДСТВИЕ: event registry не решает проблему невидимости flow; нужна миграция на оркестрацию с явным SagaState
> - [x] Implicit coupling monster: flow невидим, 5+ сервисов подписаны друг на друга, cyclic dependencies, отладка только через distributed tracing | ✓ ПРИМЕНЯТЬ: при 5+ шагах мигрировать на оркестрацию 📋 ПРАВИЛО: хореография 5+ шагов = distributed big ball of mud → orchestration 🔗 См. Q12
> - [ ] Хореография масштабируется лучше оркестрации при любом количестве сервисов | ❌ ПОСЛЕДСТВИЕ: при росте coupling через события становится хуже чем orchestration coupling; масштабирование ≠ maintainability
> - [ ] Главный риск хореографии — SPOF брокера сообщений | ❌ ПОСЛЕДСТВИЕ: брокер (Kafka) HA по дизайну; главный риск — invisible flow и accidental coupling между сервисами

## Q14. (!) Что такое compensating transaction?

**Compensating transaction** (компенсирующая транзакция) -- локальная транзакция, семантически отменяющая эффект ранее выполненной транзакции той же Saga. Это не `rollback` в SQL-смысле (который невозможен после коммита), а новая транзакция, которая приводит данные к состоянию «как будто операции не было».

**Примеры:**

| Шаг $T_i$ | Компенсация $C_i$ |
|---|---|
| Создать заказ | Отметить заказ как `CANCELLED` (не удалять -- нужен audit) |
| Зарезервировать 5 штук товара | Вернуть 5 штук на склад |
| Списать 1000 ₽ с карты | Выпустить refund на 1000 ₽ |
| Отправить email «заказ принят» | Отправить email «заказ отменён» |
| Начислить бонусы | Списать бонусы |

Требования к компенсациям:
- **Идемпотентность** -- компенсация может выполниться повторно ([Q34](#q34--как-обеспечить-идемпотентность-шагов-saga))
- **Коммутативность** -- по возможности порядок компенсаций не должен менять итог
- **Семантическая корректность** -- не «удалить запись», а «пометить отменённой» с причиной


> [!mcq]
> - [ ] Compensating transaction = SQL ROLLBACK на удалённой БД | ❌ ПОСЛЕДСТВИЕ: после коммита локальной транзакции SQL ROLLBACK невозможен; compensating = новая транзакция, семантически отменяющая эффект
> - [ ] Компенсация должна удалять запись, а не помечать её как отменённую | ❌ ПОСЛЕДСТВИЕ: удаление ломает audit trail; нужно помечать CANCELLED с причиной — «не удалять, а пометить»
> - [x] Новая локальная транзакция, семантически отменяющая T_i; должна быть идемпотентной и коммутативной; не SQL ROLLBACK | ✓ ПРИМЕНЯТЬ: Order.CANCELLED вместо DELETE; refund вместо reverse-charge 📋 ПРАВИЛО: compensate = новая txn + idempotent + semantic correctness 🔗 См. Q15
> - [ ] Компенсацию не нужно делать идемпотентной — она выполняется только один раз | ❌ ПОСЛЕДСТВИЕ: при retry из-за network timeout компенсация может выполниться дважды → двойной refund; идемпотентность обязательна

## Q15. (!) Типы шагов: compensable, pivot, retryable

Chris Richardson выделяет три типа локальных транзакций в Saga:

**1. Compensable transactions** -- шаги, которые МОЖНО компенсировать. Если после них падает более поздний шаг, запускаем компенсацию.
- Пример: «зарезервировать товар», «создать заказ в статусе PENDING»

**2. Pivot transaction** -- «точка невозврата». После её успеха Saga считается успешной: следующие шаги только `retryable`. До неё -- все шаги compensable.
- Пример: «списать деньги с карты» (refund возможен, но это уже отдельный бизнес-процесс)

**3. Retryable transactions** -- шаги после pivot, которые ТОЛЬКО retry до успеха (никогда не падают бизнесово).
- Пример: «отправить email», «начислить бонусы», «обновить аналитику»

```mermaid
graph LR
    T1[T1 compensable<br/>Create Order] --> T2[T2 compensable<br/>Reserve Inventory]
    T2 --> T3[T3 PIVOT<br/>Charge Payment]
    T3 --> T4[T4 retryable<br/>Send Email]
    T4 --> T5[T5 retryable<br/>Update Analytics]
    style T3 fill:#fc9
```

Эта классификация помогает при проектировании: всё, что не вернуть -- pushni вправо за pivot.


> [!mcq]
> - [ ] Retryable транзакции можно ставить в начало Saga — они не требуют компенсации | ❌ ПОСЛЕДСТВИЕ: retryable шаги идут ПОСЛЕ pivot; до pivot — compensable шаги которые можно отменить; porядок нарушает design invariant
> - [x] Compensable (до pivot, с компенсацией), Pivot (точка невозврата, без компенсации), Retryable (после pivot, гарантированно успешны при retry) | ✓ ПРИМЕНЯТЬ: email/notification после pivot = retryable; payment = pivot; order create = compensable 📋 ПРАВИЛО: pushni irreversible steps за pivot 🔗 См. Q16
> - [ ] Pivot transaction обязательно является самой долгой операцией в Saga | ❌ ПОСЛЕДСТВИЕ: pivot = точка невозврата по бизнес-семантике, не по длительности; обычно это payment/stock write-off
> - [ ] Если один шаг retryable, все последующие тоже retryable автоматически | ❌ ПОСЛЕДСТВИЕ: retryable свойство не транзитивно; каждый шаг описывается явно; шаг после retryable может сам требовать компенсации

## Q16. Почему компенсация не равна откату (rollback)?

| Характеристика | DB rollback | Saga compensation |
|---|---|---|
| Уровень | СУБД | Приложение |
| Когда возможен | До коммита | После коммита, всегда |
| Видимость промежуточных состояний | Нет | Да -- другие могут их увидеть |
| Обратимость | Полная | Семантическая |
| Реализация | Автомат через WAL/undo log | Ручная бизнес-логика |

После того как локальная транзакция $T_i$ закоммичена, её эффект увидели другие сервисы и, возможно, пользователи. «Отменить» это физически нельзя -- нужно новое бизнес-действие.

**Яркий пример:** если $T_1$ -- «отправить email клиенту с подтверждением заказа», компенсация $C_1$ -- «отправить email с извинением об отмене», а не волшебно «развидеть» письмо.

**Аудит и бухгалтерия:** в compliant-системах компенсация НЕ удаляет запись, а создаёт reversing entry (как в бухучёте: списание + зачисление, а не удаление проводки).


> [!mcq]
> - [ ] Компенсирующая транзакция отменяет эффект как DB rollback — данные возвращаются в исходное состояние | ❌ ПОСЛЕДСТВИЕ: локальная транзакция уже закоммичена и видима другим; физически «удалить» нельзя; нужно новое бизнес-действие (reversing entry)
> - [ ] Компенсация автоматически создаётся фреймворком — программировать не нужно | ❌ ПОСЛЕДСТВИЕ: Temporal/Axon помогают с orchestration, но бизнес-логику компенсации (отправить email об отмене, вернуть деньги) пишет разработчик
> - [x] Компенсация = семантическая отмена через новое бизнес-действие (reversing entry); промежуточные состояния уже были видимы; compliant системы не удаляют, а добавляют запись | ✓ ПРИМЕНЯТЬ: audit-trail систему, финансовые reversals, бухучёт 📋 ПРАВИЛО: compensation ≠ rollback; compensation = new forward action 🔗 См. Q15
> - [ ] Если Saga не достигла pivot, компенсация не нужна — шаги не закоммичены | ❌ ПОСЛЕДСТВИЕ: compensable шаги коммитятся сразу; если Saga падает на шаге 3, шаги 1-2 уже закоммичены и нужны компенсации C2, C1

## Q17. Что делать, если compensating transaction тоже упала?

Это реальный кошмар: Saga не может ни завершиться, ни откатиться. Стратегии:

1. **Retry с экспоненциальной задержкой** -- большинство падений transient (сеть, таймаут); retry 5--10 раз в течение часов решает 90% случаев
2. **DLQ (Dead Letter Queue)** -- после исчерпания retry отправляем команду компенсации в DLQ для ручного разбора
3. **Saga stuck alert** -- мониторинг по `lastUpdate` Saga: если старше threshold -- алерт оператору
4. **Manual compensation** -- UI/runbook, в котором оператор может вручную запустить компенсацию или откорректировать данные
5. **Backward recovery pipeline** -- отдельный компонент, который периодически находит зависшие Saga и пытается завершить их
6. **Alternative compensation** -- если $C_i$ не работает, можно попробовать $C_i'$ (другую стратегию: вместо возврата товара -- списать как убыток)

**Критичный принцип:** компенсации должны быть как можно более устойчивыми -- идемпотентными, минимально зависимыми, с простейшей логикой. Pivot transaction ставится как можно раньше, чтобы после неё шли только retryable steps и не нужны были компенсации.


> [!mcq]
> - [ ] При падении compensation фреймворк автоматически откатывает предыдущие compensations | ❌ ПОСЛЕДСТВИЕ: нет такой автоматики; каждая compensation — независимое действие; нужен explicit retry + DLQ + мониторинг stuck Sagas
> - [ ] Достаточно одного retry немедленно — если не сработало, Saga завершена как failed | ❌ ПОСЛЕДСТВИЕ: немедленный retry часто падает по той же причине; нужен exponential backoff + jitter; отдельная обработка through DLQ с manual intervention
> - [x] Retry с exponential backoff → DLQ после exhausted retries → Saga stuck alert + manual compensation UI → optional alternative compensation | ✓ ПРИМЕНЯТЬ: финансовые Saga с обязательным eventual completion 📋 ПРАВИЛО: compensations must be idempotent + retryable; DLQ = safety net 🔗 См. Q18
> - [ ] Compensating transaction не нужно делать идемпотентной — она вызывается максимум один раз | ❌ ПОСЛЕДСТВИЕ: при retry compensation вызовется повторно (at-least-once); двойной refund, двойной release — катастрофа для финансов

## Q18. (!) Почему Saga не обеспечивает isolation и какие возникают аномалии?

Локальные транзакции коммитятся немедленно, поэтому их результат виден другим транзакциям ДО того, как Saga завершится. Возникают аномалии:

**1. Lost update** -- одна Saga перезаписывает изменения другой
- Пример: Saga A резервирует 5 штук → Saga B резервирует ещё 5 штук → A компенсирует → на складе минус 5

**2. Dirty read** -- транзакция читает промежуточное состояние Saga, которая потом компенсирует
- Пример: UI показывает заказ как CONFIRMED, пользователь выставляет отзыв, но Saga откатывается → отзыв висит на несуществующем заказе

**3. Fuzzy/non-repeatable read** -- один и тот же запрос в рамках транзакции возвращает разные данные, т.к. параллельная Saga обновила их
- Пример: расчёт комиссии дважды дал разные суммы, т.к. в середине прошёл refund другой Saga

**4. Double-write** -- две параллельные Saga делают одинаковую операцию, у каждой свой ID
- Пример: два параллельных `PlaceOrder` на один `cartId` из-за таймаута -- создались два заказа

Для исправления применяют `countermeasures` (следующие вопросы).


> [!mcq]
> - [ ] Saga обеспечивает изоляцию через distributed lock на всё время выполнения | ❌ ПОСЛЕДСТВИЕ: distributed lock на минуты → DeadLock риск, scalability ноль; именно поэтому Saga применяется — isolation жертвуется ради доступности
> - [ ] Dirty read в Saga — редкий edge case, не требует countermeasures | ❌ ПОСЛЕДСТВИЕ: dirty read типичен при любом параллелизме; UI может показать CONFIRMED заказ который потом откатится; countermeasures (semantic lock) обязательны в финансах
> - [x] Saga не имеет глобальной изоляции: каждая локальная транзакция видима после коммита; аномалии: dirty read (откатившаяся Saga), lost update (параллельные Saga), fuzzy read, double-write | ✓ ПРИМЕНЯТЬ: проектировать countermeasures для каждой аномалии в domain 📋 ПРАВИЛО: Saga = eventual consistency + visible intermediate states 🔗 См. Q19
> - [ ] Аномалии Saga идентичны аномалиям при уровне READ UNCOMMITTED в SQL | ❌ ПОСЛЕДСТВИЕ: SQL READ UNCOMMITTED — dirty reads в одной БД; Saga аномалии — cross-service, cross-DB, persist после rollback; fundamentally different scope

## Q19. (!) Что такое semantic lock?

**Semantic lock** -- application-level блокировка через флаг в записи, который сигнализирует «запись участвует в текущей Saga, обрабатывать с осторожностью». Другие Saga и читатели проверяют флаг и действуют соответственно.

```java
public class Order {
    private UUID id;
    private OrderStatus status;          // PENDING, CONFIRMED, CANCELLED
    private String sagaLock;              // sagaId или null
    // ...

    public void approve(UUID sagaId) {
        if (this.sagaLock != null && !this.sagaLock.equals(sagaId.toString())) {
            throw new SagaLockViolation("Order locked by another saga");
        }
        this.sagaLock = sagaId.toString();
        this.status = OrderStatus.APPROVAL_PENDING;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.sagaLock = null;  // освобождаем
    }
}
```

**Типичные статусы-локи:** `APPROVAL_PENDING`, `REVISION_PENDING`, `PAYMENT_IN_PROGRESS`, `CANCELLATION_PENDING`. UI показывает эти состояния как «обрабатывается» и не даёт совершить конкурирующие действия. Лок снимается при completion или по timeout watchdog'ом.


> [!mcq]
> - [ ] Semantic lock полностью устраняет все аномалии Saga — других countermeasures не нужно | ❌ ПОСЛЕДСТВИЕ: semantic lock только предотвращает concurrent modification; не защищает от lost update при отсутствии проверки, от fuzzy read вне locked ресурсов
> - [ ] Semantic lock реализуется через distributed database lock (Redis SETNX) | ❌ ПОСЛЕДСТВИЕ: semantic lock = application-level флаг в записи БД (sagaLock поле); Redis lock = infrastructure lock; разные уровни, разные гарантии
> - [x] Semantic lock = флаг в записи (sagaLock = sagaId); другие читают флаг и либо ждут, либо fail-fast; снимается после завершения/компенсации | ✓ ПРИМЕНЯТЬ: предотвращение dirty read + concurrent Saga на одной записи 📋 ПРАВИЛО: lock = флаг в своей БД = no cross-service lock 🔗 См. Q20
> - [ ] Semantic lock блокирует запись глобально для всех читателей | ❌ ПОСЛЕДСТВИЕ: semantic lock — advisory lock; читатели сами решают как реагировать; некоторые читают locked запись с пометкой "in progress", другие fail-fast

## Q20. Что такое commutative updates, pessimistic view, reread, version file?

Остальные countermeasures из классификации Chris Richardson:

**Commutative updates** -- проектируем операции так, чтобы порядок применения не влиял на результат. Пример: вместо `balance = 100` (set) использовать `balance += delta` (increment) -- тогда компенсация `balance -= delta` всегда корректна, даже если порядок смешался.

**Pessimistic view** -- переупорядочиваем шаги Saga так, чтобы «опасные» данные обновлялись в последнюю retryable-фазу, когда уже нет риска компенсации.
- Пример: не увеличиваем лимит кредитной линии клиента до одобрения платежа -- чтобы он не успел им воспользоваться и не пришлось отбирать обратно

**Reread value** -- перед компенсацией заново читаем запись и проверяем, что значение не изменилось с момента $T_i$. Если изменилось -- не компенсируем, сигнализируем оператору или применяем альтернативную стратегию.

**Version file** -- храним все операции над записью с их ID; компенсация применяется как `inverse operation` с привязкой к ID, а не к значению. Если между $T_i$ и $C_i$ прошли другие операции -- они не затираются.

**By value** -- переключаемся на более строгую стратегию (например, sync 2PC) для high-risk бизнес-операций. Разные risk tiers для разных клиентов/сумм.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Что такое by value strategy? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`By value` -- это не отдельный countermeasure, а runtime-стратегия выбора механизма транзакции в зависимости от бизнес-параметров:
- Маленькая сумма, retail-клиент → `Saga` с компенсациями, eventual consistency
- Крупная сумма, VIP-клиент, high risk → синхронный `2PC` или отклонение с запросом ручного одобрения

Применяется редко -- усложняет код, но иногда оправданно для финтеха и банкинга, где риск аномалии стоит миллионы.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. (!) Saga vs 2PC -- ключевые отличия ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Характеристика | `2PC` (XA) | `Saga` |
|---|---|---|
| Координация | `Transaction Coordinator` с двумя фазами (prepare, commit) | Оркестратор или события |
| Блокировки | Блокирует ресурсы до конца фазы commit | Нет глобальных блокировок |
| Согласованность | Strong -- атомарная (все или ничего) | Eventual -- через компенсации |
| Isolation | ACID | ACD (без I) -- нужны countermeasures |
| Производительность | Низкая -- синхронный блокирующий | Высокая -- асинхронный |
| Fault tolerance | Coordinator = SPOF, зависшие `in-doubt` транзакции | Падение участника = retry/compensation |
| Гетерогенные ресурсы | Только если все поддерживают XA (PostgreSQL, Oracle, JMS -- не все брокеры поддерживают) | Работает с любыми ресурсами |
| Long-running | Невозможно -- блокировки | Подходит идеально (часы, дни) |
| Сложность кода | Простая (одна `@Transactional`) | Высокая (компенсации, countermeasures) |
| Применимость | Монолит с несколькими БД, legacy | Микросервисы |

**Вывод:** в микросервисах `2PC` практически не используется. Saga -- стандарт, несмотря на сложность.

Подробнее про проблемы 2PC -- в [consistency patterns](consistency-patterns-interview.md) и [distributed systems](distributed-systems-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. Почему 2PC плохо работает в микросервисах? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Блокировки ресурсов** -- во время фазы prepare ресурсы залочены, и если один из участников тормозит, все остальные ждут; при массовой нагрузке -- deadlock
2. **Single Point of Failure** -- coordinator падает после phase 1, участники остаются в `in-doubt` -- не знают, committ-ить или rollback, держат локи
3. **Network partition** -- coordinator не может достучаться до участника; неизвестно, committ-ить или нет
4. **Гетерогенность** -- многие современные брокеры (`Kafka`, modern NoSQL) не поддерживают XA/JTA
5. **Производительность** -- ~3x overhead на каждую транзакцию из-за round-trips
6. **Масштабируемость** -- чем больше участников, тем больше вероятность таймаута и медленнее Commit
7. **Long-running** -- невозможно -- никто не станет блокировать ресурсы на часы

Инженерный консенсус: в микросервисах `2PC` -- антипаттерн. Даже если ваши БД его поддерживают, лучше перепроектировать процесс под `Saga` или `Outbox`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Что такое TCC (Try-Confirm-Cancel) и чем он отличается от Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**TCC** (Try-Confirm-Cancel) -- вариация Saga, где каждый шаг разбит на три операции:
- **Try** -- зарезервировать ресурс, не коммитя окончательно (например, «зарезервировать 100 ₽ на карте»)
- **Confirm** -- финализировать (спишем 100 ₽)
- **Cancel** -- освободить резерв (вернём 100 ₽)

```mermaid
sequenceDiagram
    participant O as Orchestrator
    participant Inv as Inventory
    participant Pay as Payment

    Note over O: Try phase
    O->>Inv: Try(reserve items)
    Inv-->>O: OK
    O->>Pay: Try(authorize payment)
    Pay-->>O: OK

    Note over O: Confirm phase
    O->>Inv: Confirm(commit reservation)
    O->>Pay: Confirm(capture payment)
```

**Saga vs TCC:**

| Критерий | Saga | TCC |
|---|---|---|
| Модель | T_i + C_i | Try + Confirm/Cancel |
| Isolation | Нарушен -- нужны countermeasures | Лучше -- ресурсы «зарезервированы», не видны другим |
| Сложность API | T_i -- одно действие | Три метода на шаг |
| Применимость | Любые операции | Требует от сервисов поддерживать резервирование |
| Видимость промежуточных состояний | Полная (ORDER=PENDING виден) | Меньше (резерв скрыт) |

TCC популярен в Alibaba Seata и в финтехе, где важна минимизация временных окон с видимыми промежуточными состояниями. Реализация сложнее -- каждый сервис должен поддерживать три операции.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. (!) Как реализовать оркестратор Saga на чистом Spring Boot + Kafka? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Минимальная реализация без фреймворков:

```java
@Entity
@Table(name = "saga_state")
public class CreateOrderSagaState {
    @Id private UUID sagaId;
    private UUID orderId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private SagaStep currentStep;
    @Enumerated(EnumType.STRING)
    private SagaStatus status;
    @Version private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}

@Component
@RequiredArgsConstructor
public class CreateOrderSagaOrchestrator {
    private final SagaStateRepository repo;
    private final KafkaTemplate<String, Object> kafka;

    @Transactional
    public UUID start(PlaceOrderRequest req) {
        UUID sagaId = UUID.randomUUID();
        CreateOrderSagaState state = new CreateOrderSagaState(
            sagaId, req.orderId(), req.amount(),
            SagaStep.CREATE_ORDER, SagaStatus.IN_PROGRESS
        );
        repo.save(state);
        kafka.send("order.commands",
            sagaId.toString(),
            new CreateOrderCommand(sagaId, req.orderId(), req.items())
        );
        return sagaId;
    }

    @KafkaListener(topics = "order.replies")
    @Transactional
    public void onOrderReply(OrderCreatedReply reply) {
        CreateOrderSagaState state = repo.findById(reply.sagaId()).orElseThrow();
        if (reply.success()) {
            state.setCurrentStep(SagaStep.RESERVE_INVENTORY);
            kafka.send("inventory.commands",
                new ReserveInventoryCommand(reply.sagaId(), ...)
            );
        } else {
            state.setStatus(SagaStatus.FAILED);
        }
        repo.save(state);
    }

    @KafkaListener(topics = "payment.replies")
    @Transactional
    public void onPaymentReply(PaymentReply reply) {
        CreateOrderSagaState state = repo.findById(reply.sagaId()).orElseThrow();
        if (reply.success()) {
            state.setStatus(SagaStatus.COMPLETED);
        } else {
            // компенсации
            state.setStatus(SagaStatus.COMPENSATING);
            kafka.send("inventory.commands",
                new ReleaseInventoryCommand(reply.sagaId())
            );
            kafka.send("order.commands",
                new CancelOrderCommand(reply.sagaId())
            );
        }
        repo.save(state);
    }
}
```

**Критически важно:**
- Публикация команды и обновление state -- в одной транзакции через Outbox ([Q26](#q26--как-связать-saga-с-outbox-pattern))
- `@Version` для optimistic locking -- два reply одновременно не должны конфликтовать
- Идемпотентность обработки reply (проверка `currentStep` и completed steps)
- Таймауты на каждый шаг через scheduler


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Как связать Saga с Outbox pattern? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Проблема «dual write»: в локальной транзакции нужно атомарно (а) сохранить state Saga и (б) отправить команду/событие в Kafka. Если (а) успешно, (б) -- нет -- Saga зависнет. Если (б) -- успешно, (а) -- упало -- команда потерялась.

**Решение -- Outbox pattern:**

```mermaid
graph LR
    S[Saga Orchestrator] -->|INSERT saga_state<br/>INSERT outbox| DB[(Service DB)]
    DB -->|CDC / poll| R[Relay / Debezium]
    R --> K[(Kafka)]
    K --> P1[Inventory Service]
    K --> P2[Payment Service]
```

```java
@Transactional
public void sendCommand(UUID sagaId, Object command, String topic) {
    // 1. Обновляем state
    sagaRepo.save(currentState);

    // 2. В той же транзакции пишем в outbox
    OutboxMessage msg = new OutboxMessage(
        UUID.randomUUID(),
        topic,
        sagaId.toString(),
        jsonMapper.writeValueAsString(command),
        Instant.now(),
        OutboxStatus.PENDING
    );
    outboxRepo.save(msg);
}

// Отдельный publisher читает outbox и отправляет в Kafka
@Scheduled(fixedDelay = 500)
public void publishOutbox() {
    List<OutboxMessage> pending = outboxRepo.findTop100ByStatus(OutboxStatus.PENDING);
    for (OutboxMessage m : pending) {
        kafka.send(m.getTopic(), m.getKey(), m.getPayload())
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    m.setStatus(OutboxStatus.PUBLISHED);
                    outboxRepo.save(m);
                }
            });
    }
}
```

Альтернатива -- `Debezium` + Kafka Connect: CDC читает транзакционный лог и автоматически публикует изменения таблицы outbox в Kafka. Такой подход используют `Eventuate Tram CDC` и многие production-решения. Подробнее -- в [event-driven паттернах](event-driven-patterns-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) Как реализовать Saga на Axon Framework? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Axon` даёт декларативную модель Saga:

```java
@Saga
public class CreateOrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    private UUID orderId;
    private UUID paymentId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        // Шаг 2: зарезервировать товар
        commandGateway.send(new ReserveItemsCommand(event.orderId(), event.items()))
            .exceptionally(ex -> {
                commandGateway.send(new CancelOrderCommand(orderId));
                return null;
            });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ItemsReservedEvent event) {
        // Шаг 3: списать деньги
        paymentId = UUID.randomUUID();
        SagaLifecycle.associateWith("paymentId", paymentId.toString());
        commandGateway.send(new ChargePaymentCommand(paymentId, orderId, event.amount()));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentChargedEvent event) {
        commandGateway.send(new ConfirmOrderCommand(orderId));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentFailedEvent event) {
        // компенсация: освободить резерв и отменить заказ
        commandGateway.send(new ReleaseItemsCommand(orderId));
        commandGateway.send(new CancelOrderCommand(orderId));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderConfirmedEvent event) {
        // Saga завершена
    }
}
```

**Ключевые особенности Axon:**
- `@StartSaga` + `@SagaEventHandler` -- Axon создаёт instance Saga по первому event'у с нужным `associationProperty`
- State Saga автоматически персистится (JPA, JDBC, MongoDB)
- `SagaLifecycle.associateWith` добавляет дополнительные ассоциации (paymentId, shipmentId), чтобы связывать будущие events с этим instance
- `@EndSaga` помечает завершение -- state удаляется
- Таймауты через `EventScheduler.schedule` -- подписка на `SchedulerEvent`

Подробнее про Axon и CQRS/ES -- в [вопросах по CQRS и Event Sourcing](cqrs-event-sourcing-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Как использовать Eventuate Tram для orchestration Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Eventuate Tram Sagas` -- специализированный фреймворк для orchestration-based Saga от Chris Richardson.

```java
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

    private SagaDefinition<CreateOrderSagaData> sagaDefinition =
        step()
          .invokeLocal(this::createOrder)
          .withCompensation(this::rejectOrder)
        .step()
          .invokeParticipant(this::reserveCredit)
          .onReply(CreditReservedReply.class, this::handleCreditReserved)
          .onReply(CreditLimitExceededReply.class, this::handleCreditLimitExceeded)
        .step()
          .invokeParticipant(this::chargePayment)
          .withCompensation(this::refund)
        .build();

    @Override
    public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
        return sagaDefinition;
    }

    private CommandWithDestination reserveCredit(CreateOrderSagaData data) {
        return send(new ReserveCreditCommand(data.getCustomerId(), data.getAmount()))
            .to("customerService")
            .build();
    }
    // ...
}
```

**Особенности:**
- DSL для декларативного описания steps и compensations
- Встроенный Outbox (через Eventuate Tram CDC с Debezium)
- Поддержка Spring Boot, Micronaut, Quarkus
- Работает с MySQL/PostgreSQL (полагается на CDC) + Apache Kafka
- Автоматическое управление persistence state, retry, idempotency

Плюс -- явный и компактный DSL; минус -- инфраструктурная зависимость от CDC и Kafka, сложнее отлаживать внутреннюю механику.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. Как использовать Camunda/BPMN для Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Camunda 8` (`Zeebe`) и `Camunda 7` позволяют моделировать Saga как BPMN-процесс с явной визуальной схемой, compensation activities и boundary events.

```xml
<bpmn:process id="createOrderSaga">
  <bpmn:serviceTask id="createOrder" camunda:type="external" camunda:topic="create-order"/>
  <bpmn:serviceTask id="reserveInventory" camunda:type="external" camunda:topic="reserve-inventory"/>
  <bpmn:serviceTask id="chargePayment" camunda:type="external" camunda:topic="charge-payment"/>

  <!-- Compensation activities -->
  <bpmn:serviceTask id="cancelOrder" isForCompensation="true"
                    camunda:type="external" camunda:topic="cancel-order"/>
  <bpmn:serviceTask id="releaseInventory" isForCompensation="true"
                    camunda:type="external" camunda:topic="release-inventory"/>

  <bpmn:boundaryEvent id="compensationTrigger" attachedToRef="chargePayment">
    <bpmn:compensateEventDefinition/>
  </bpmn:boundaryEvent>
</bpmn:process>
```

На Java participant'ы реализуются как worker'ы:

```java
@ExternalTaskSubscription("reserve-inventory")
public class ReserveInventoryHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask task, ExternalTaskService svc) {
        try {
            inventoryService.reserve(task.getVariable("orderId"), ...);
            svc.complete(task);
        } catch (Exception e) {
            svc.handleBpmnError(task, "INSUFFICIENT_STOCK");
        }
    }
}
```

**Плюсы BPMN-подхода:** визуализация процесса, business-analyst может читать схему; встроенные compensation semantics; мощная персистентность и версионирование процессов.
**Минусы:** runtime-overhead (BPMN-движок), сложность деплоя, lock-in на Camunda.

Альтернативы с похожей моделью: `Temporal`, `Netflix Conductor`, `Orkes Conductor`, `AWS Step Functions`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. Как реализовать persistent state saga orchestrator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Оркестратор должен пережить падение процесса и restart JVM -- значит, state живёт в БД, не в памяти.

**Ключевые принципы:**

1. **State в одной таблице с версионированием** -- `@Version` для optimistic locking
2. **Процесс изменения state + отправки команды -- в одной транзакции с Outbox** ([Q26](#q26--как-связать-saga-с-outbox-pattern))
3. **Load-on-event** -- при получении reply сначала грузим state из БД, проверяем совместимость, потом обновляем
4. **Scheduled recovery** -- отдельный job читает зависшие (stuck) саги и пытается продолжить

```java
@Component
public class SagaRecoveryJob {
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void recoverStuckSagas() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        List<CreateOrderSagaState> stuck = repo
            .findByStatusAndUpdatedAtBefore(SagaStatus.IN_PROGRESS, cutoff);

        for (CreateOrderSagaState s : stuck) {
            log.warn("Resuming stuck saga {} at step {}", s.getSagaId(), s.getCurrentStep());
            resume(s);  // повторяем команду текущего шага
        }
    }
}
```

5. **Idempotent command handling** -- участник проверяет, не обрабатывал ли он уже эту команду (по `sagaId` + `step`)
6. **Monitoring** -- метрика `saga_stuck_duration_seconds` и alert на долгие саги


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. (!) Какие антипаттерны Saga следует избегать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Бизнес-логика в оркестраторе** -- оркестратор должен быть тупым координатором; бизнес-решения принимают агрегаты участников. Оркестратор = `workflow engine`, не `domain service`.

2. **Синхронные REST-вызовы из оркестратора** -- оркестратор должен общаться async через брокер; sync-вызовы делают его зависимым от доступности и производительности участников.

3. **Компенсация как `rollback` через `DELETE`** -- удаление записи теряет audit trail и не работает, если на неё уже сослались другие записи. Компенсация = `UPDATE status = CANCELLED`.

4. **Забыть про isolation** -- отсутствие countermeasures ведёт к lost update, dirty read в production (особенно под нагрузкой).

5. **Нет pivot transaction** -- делать все шаги compensable до самого конца создаёт ненужные сценарии компенсации и увеличивает риск зависания.

6. **Хореография с 10+ событиями** -- превращается в `distributed monolith`; мигрируйте на оркестрацию, когда цепочка вырастет.

7. **Игнорировать DLQ и stuck sagas** -- без recovery-процесса в production накопятся зависшие саги, данные разойдутся.

8. **Отсутствие correlation-id** -- без `sagaId` / `traceId` в каждом событии невозможно отладить цепочку.

9. **Компенсации не идемпотентны** -- retry после transient fail создаёт двойную компенсацию (двойной refund).

10. **Нет таймаутов** -- Saga может висеть вечно, если один участник не ответил.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. Что такое distributed monolith и как Saga может его спровоцировать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Distributed monolith** -- архитектура, где сервисы технически разделены, но настолько связаны (через sync-вызовы, общую БД, цепочки событий), что:
- Нельзя задеплоить один сервис без других
- Изменение в одном сервисе ломает другие
- Падение одного сервиса роняет весь процесс

**Как Saga провоцирует:**
- **Хореография с неявной связностью** -- сервис A зависит от event формата сервиса B, изменение схемы требует coordinated release
- **Orchestrator, знающий внутреннее устройство участников** -- оркестратор, который вызывает DB-методы или внутренние endpoint'ы участников
- **Жёсткие связи через команды** -- каждый сервис-участник должен поддерживать конкретный набор команд, изменение требует synchronized deploy
- **Циклические зависимости** -- событие из A триггерит B, из B -- C, из C -- снова A

**Как избежать:**
- Стабильные контракты событий/команд + `Schema Registry` с backward compatibility ([schema registry](../messaging/kafka-interview.md))
- Оркестратор общается только через async-commands, не знает internals
- Версионирование команд и событий
- Правило: изменение одного сервиса не должно требовать deploy других


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q33. Как бороться с зависшими (stuck) Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Stuck saga -- ни in-progress, ни completed, ни compensating. Причины: потерянное сообщение, падение участника, bug в reply-обработке.

**Обнаружение:**
- Колонка `updatedAt` в state + scheduled job, который ищет saga старше threshold
- Метрика `saga_age_seconds{status="IN_PROGRESS"}` -- алерт на перцентиль 99
- Dashboard с количеством активных саг по статусам

**Ресолюшн:**
1. **Автоматический retry** -- отдельный job находит stuck saga и повторно отправляет команду текущего шага (участник должен быть идемпотентен)
2. **Timeout + force compensation** -- если saga висит > 1 час, принудительно переходим в `COMPENSATING`
3. **Ручное разрешение через admin UI** -- оператор может перевести saga в любой state, запустить/пропустить шаг
4. **Audit trail** -- логируем все переходы state для forensic-анализа
5. **Alert + runbook** -- каждый stuck saga -- инцидент, runbook описывает шаги разбора

**Профилактика:**
- Таймауты на каждый шаг (запрос и обработка)
- Идемпотентность participants
- Health check участников с circuit breaker


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q34. (!) Как обеспечить идемпотентность шагов Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Идемпотентность критична: любое сообщение может прийти дважды (retry продюсера, rebalance consumer'а, recovery после падения).

**Подходы:**

**1. Уникальный ключ запроса + табл. processed:**
```java
@Entity
public class ProcessedCommand {
    @Id private UUID messageId;
    private String handler;
    private Instant processedAt;
}

@Transactional
public void handle(ReserveInventoryCommand cmd) {
    if (processedRepo.existsById(cmd.messageId())) {
        return;  // уже обработали
    }
    inventoryService.reserve(cmd.orderId(), cmd.items());
    processedRepo.save(new ProcessedCommand(cmd.messageId(), "reserve", Instant.now()));
}
```

**2. Идемпотентность на уровне бизнес-операции:**
- «Create order with id=X» -- upsert by id, повтор не создаёт дубль
- «Reserve N items for order X» -- check if reservation exists, иначе создать
- «Charge payment P» -- Payment Service сам проверяет, был ли уже чардж на этот `paymentIntentId`

**3. Optimistic locking через version:**
```java
UPDATE order SET status='RESERVED', version=version+1
WHERE id=? AND version=?
```

**4. Conditional compensation:**
- Перед компенсацией проверяем, что шаг действительно был выполнен (иначе nop)

Подробнее про идемпотентность consumer'ов -- в [event-driven паттернах](event-driven-patterns-interview.md) и [Kafka](../messaging/kafka-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q35. Как бороться с out-of-order сообщениями в хореографии? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В хореографии нет центрального контроля, и события могут приходить не в том порядке -- `ItemsReserved` раньше `OrderCreated`, если они публиковались в разные топики/партиции.

**Стратегии:**

1. **Partition key = aggregate id** -- в Kafka все события по одному `orderId` идут в одну партицию → строгий порядок внутри
2. **Causal ordering через version/sequence** -- каждый event несёт `sequenceNo`; consumer буферизует out-of-order и обрабатывает в нужной последовательности
3. **State check перед обработкой** -- при получении `ItemsReserved` проверяем, что в локальной read model Order уже существует; если нет -- откладываем (retry from DLQ)
4. **Process Manager** -- маленький локальный state machine в каждом сервисе, который знает, какой event ожидается следующим

**Анти-решение:** «просто ретраим» -- работает, но при массовом out-of-order создаёт хаос в DLQ. Лучше сразу спроектировать partitioning правильно.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q36. Как тестировать Saga? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Тесты Saga многоуровневые:

**1. Unit-тесты шагов и компенсаций** -- проверяем бизнес-логику каждой локальной транзакции изолированно (моки репозиториев и брокера).

**2. Unit-тесты оркестратора** -- state machine тестируется как набор переходов:
```java
@Test
void shouldTransitionToCompensatingWhenPaymentFails() {
    CreateOrderSagaState state = SagaFixture.atStep(PAYMENT_CHARGED);
    orchestrator.onPaymentReply(new PaymentReply(state.sagaId(), false));
    assertEquals(COMPENSATING, state.getStatus());
    verify(kafka).send(eq("inventory.commands"), any(ReleaseInventoryCommand.class));
}
```

**3. Тест happy path -- integration** -- поднимаем все сервисы в Testcontainers (PostgreSQL + Kafka), прогоняем end-to-end создание заказа, проверяем финальные состояния во всех БД.

**4. Chaos-тесты для компенсаций** -- искусственно падает один из участников, Saga должна корректно откатиться (например, через `@MockBean` поднять `PaymentService` в режим `alwaysFail`).

**5. Idempotency-тесты** -- отправляем команду дважды, проверяем, что результат как после одного раза.

**6. Concurrency-тесты** -- две параллельные Saga над одним агрегатом, проверяем countermeasures (semantic lock сработал, нет lost update).

**7. Contract-тесты** -- Pact/Spring Cloud Contract между оркестратором и participants, чтобы формат команд/replies не сломался.

В Axon есть `SagaTestFixture`:
```java
fixture.givenAggregate(orderId).published(new OrderCreatedEvent(...))
    .whenPublishingA(new ItemsReservedEvent(...))
    .expectDispatchedCommands(new ChargePaymentCommand(...));
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q37. Как мониторить Saga в production? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Ключевые метрики и логи:

**Метрики (Prometheus/Micrometer):**
- `saga_started_total{name="CreateOrder"}` -- счётчик стартов
- `saga_completed_total{name, status}` -- completed / failed / compensated
- `saga_duration_seconds{name}` -- гистограмма времени жизни
- `saga_stuck_total{name}` -- количество зависших
- `saga_step_duration_seconds{name, step}` -- время каждого шага
- `saga_compensation_total{name, step}` -- сколько компенсаций запущено

**Логи:**
- Каждый переход состояния с `sagaId`, `step`, `reason`
- Все компенсации логируются с WARN
- Structured logs для поиска по `sagaId`

**Алерты:**
- Rate компенсаций выше нормы (индикатор проблемы в payment-service)
- Stuck saga > 5 мин
- Saga duration p99 > threshold
- Ratio `compensated / started` выше X%

**Dashboards:**
- Воронка completion -- какой процент достигает каждого шага
- Heatmap длительности шагов
- Real-time список in-progress saga


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q38. Как трассировать Saga через distributed tracing? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Каждая Saga должна иметь `sagaId` = `correlationId` или `traceId`, который пропагируется через все команды и события.

```java
// Создание Saga
String traceId = UUID.randomUUID().toString();
MDC.put("sagaId", traceId);
MDC.put("traceId", traceId);
kafka.send("order.commands",
    new CreateOrderCommand(sagaId, ...) {
        Headers headers = new RecordHeaders()
            .add("sagaId", sagaId.toString().getBytes())
            .add("traceparent", currentSpan.context().toString().getBytes());
    }
);
```

**Инструменты:**
- **OpenTelemetry** + Jaeger/Tempo/Zipkin -- автоматическая трассировка HTTP и Kafka spans
- **Spring Cloud Sleuth** / **Micrometer Tracing** -- интеграция с Kafka headers
- **Correlation ID** в логах + Kibana/Loki для поиска по всем сервисам

В UI распределённой трассировки видно полный flow: `orchestrator → order-svc → inventory-svc → payment-svc → orchestrator`, время каждого шага, ошибки. Критично для отладки production-инцидентов.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q39. (!) Как реализовать timeout и circuit breaker в Saga? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

**Timeout на шаг:** оркестратор при отправке команды планирует `timer event` через N секунд. Если reply не пришёл -- считаем шаг failed и запускаем компенсацию или retry.

```java
// Axon
@StartSaga
@SagaEventHandler(associationProperty = "orderId")
public void on(OrderCreatedEvent event) {
    commandGateway.send(new ReserveItemsCommand(event.orderId()));
    // запланировать timeout через 30 секунд
    String timerId = eventScheduler.schedule(
        Duration.ofSeconds(30),
        new ReserveItemsTimeoutEvent(event.orderId())
    );
    SagaLifecycle.associateWith("orderId", event.orderId().toString());
}

@SagaEventHandler(associationProperty = "orderId")
public void on(ReserveItemsTimeoutEvent event) {
    // считаем, что резерв не получился
    commandGateway.send(new CancelOrderCommand(event.orderId()));
}
```

**Circuit Breaker перед участниками:**
- Если Payment Service стабильно отвечает ошибками -- `Resilience4j` размыкает цепь, саги сразу идут в compensation без попыток списать
- После cooldown пробует снова
- Спасает от каскадных сбоев при деградации одного сервиса

**Bulkhead:** отдельные thread pool'ы для каждого participant, чтобы тормозящий участник не выжрал все threads оркестратора.

Подробнее про circuit breaker -- в [resilience patterns](resilience-patterns-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q40. Как Saga взаимодействует с CQRS и Event Sourcing? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В системах с `CQRS` + `Event Sourcing` Saga -- естественный способ координировать агрегаты:

- Каждый **агрегат** обрабатывает команды и генерирует события (`OrderCreated`, `InventoryReserved`)
- **Saga** подписывается на события из event store, реагирует и отправляет новые команды в другие агрегаты
- State Saga хранится отдельно (как process manager), может сам быть event-sourced
- Проекции для read model обновляются из тех же событий независимо от Saga

```mermaid
graph LR
    C[Command] --> A[Aggregate]
    A -->|events| ES[(Event Store)]
    ES --> S[Saga]
    S -->|commands| A2[Other Aggregate]
    A2 -->|events| ES
    ES --> P[Projection / Read Model]
```

В `Axon Framework` всё это работает нативно: Saga -- first-class citizen наряду с Aggregate и Projection. Детали -- в [CQRS + Event Sourcing](cqrs-event-sourcing-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q41. Как версионировать Saga при изменении бизнес-процесса? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Проблема: вы деплоите новую версию Saga, но в БД уже есть in-progress instance'ы старой версии. Их нельзя ломать.

**Стратегии:**

1. **Версия в state** -- `@Column private Integer sagaVersion;` -- оркестратор читает версию и выбирает соответствующий handler
```java
public void handle(SagaEvent event, CreateOrderSagaState state) {
    CreateOrderSagaLogic logic = logicRegistry.get(state.getSagaVersion());
    logic.handle(event, state);
}
```

2. **Zero-break changes** -- новые шаги добавляются в конец, не ломая существующие перехода; старые compensation действия сохраняются
3. **Snapshot + migration** -- для long-running Saga делается migration script, переводящий старые instances в новую схему
4. **Workflow-системы (Temporal, Camunda)** -- имеют встроенный versioning: версия процесса фиксируется в момент старта, новые instance'ы стартуют по новой версии
5. **Deprecation period** -- держим две версии параллельно, пока все старые instances не завершатся

**Правило:** никогда не меняем семантику существующих команд/событий задним числом -- всегда новая версия команды (`ReserveItemsCommandV2`).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q42. Long-running Saga -- особенности (часы, дни, недели) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Saga может длиться намного больше, чем HTTP-request. Примеры: `Process Loan Approval` (неделя), `Book Travel` (до даты поездки), `Subscription Renewal` (месяц).

**Ключевые особенности:**

1. **Persistent state -- обязательно** -- никаких in-memory state
2. **Timeouts на шаги + watchdog** -- шаг может ждать несколько дней (подтверждение от пользователя)
3. **Таймеры и отложенные события** -- `EventScheduler`, `@Scheduled`, Quartz; в workflow-движках (Temporal, Camunda) -- нативно
4. **Idempotency -- критична** -- за неделю может много чего случиться: restart JVM, redeploy, миграция БД
5. **Versioning обязателен** -- за время жизни одной Saga выходит 2--3 релиза приложения
6. **Humans in the loop** -- пользователь/оператор может влиять на процесс; нужны UI для просмотра state и manual интервенции
7. **Externalize state -- DB, Redis, Temporal** -- не в памяти приложения
8. **Observability** -- нужен persistent log всех переходов с timestamps для аудита и анализа
9. **Compensation для старых шагов** -- компенсация, запускаемая через неделю, должна учитывать, что данные изменились; применяем `reread value`, `commutative updates`

Для таких задач обычно выбирают специализированные решения: **Temporal**, **AWS Step Functions**, **Camunda** -- они закрывают большую часть плиток автоматически (persistence, timers, versioning, UI).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q43. Как объяснить eventual consistency клиенту/пользователю? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

Это не только технический, но и UX-вопрос: на собеседовании вас могут спросить «как показать статус заказа пользователю, если Saga ещё идёт?».

**Паттерны UX:**

1. **Optimistic UI** -- показываем «заказ создан» сразу после создания Order-записи, даже если ещё не списан платёж. Если платёж упадёт -- показываем уведомление «к сожалению, оплата не прошла, заказ отменён»
2. **Processing states** -- явный статус `Ожидает подтверждения платежа`, `Готовим к отправке` -- пользователь понимает, что процесс ещё не завершён
3. **202 Accepted + polling** -- REST API возвращает `202` с `Location: /sagas/{sagaId}`, клиент опрашивает статус
4. **Server-Sent Events / WebSocket** -- push-уведомления о смене статуса без polling
5. **Webhook callbacks** -- для B2B интеграций, когда клиент -- другой сервис
6. **Email / push-notifications** -- для долгих процессов (`ваш заказ подтверждён`)

**На собеседовании важно подчеркнуть:**
- Eventual consistency -- не bug, а design choice с trade-offs
- Обычно пользователю не важна мгновенная согласованность -- секундная задержка незаметна
- Критично корректно обработать failed saga -- пользователь должен узнать об отмене и, если списали деньги, увидеть refund
- Саги -- инструмент бизнеса, а не только кода: UX команда должна согласовать processing states

---

## See also

- [CQRS и Event Sourcing](cqrs-event-sourcing-interview.md) -- Saga как неотъемлемая часть CQRS/ES архитектур, orchestration с Axon Framework
- [Event-driven паттерны](event-driven-patterns-interview.md) -- брокеры, идемпотентность, Outbox, choreography -- основа для Saga
- [Микросервисная архитектура](microservices-interview.md) -- Saga в контексте микросервисов, сравнение с 2PC, Bounded Context
- [Паттерны согласованности](consistency-patterns-interview.md) -- eventual consistency, ACID vs BASE, CAP
- [Распределённые системы](distributed-systems-interview.md) -- 2PC, 3PC, FLP, консенсус, проблемы распределённых транзакций
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) -- Circuit Breaker, Retry, Timeout, Bulkhead для шагов Saga
- [CAP-теорема](cap-theorem-interview.md) -- теоретическая основа выбора между consistency и availability
- [Kafka](../messaging/kafka-interview.md) -- партицирование для порядка событий, Schema Registry, Kafka как транспорт для Saga
- [DDD](ddd-interview.md) -- агрегаты, bounded context, domain events -- фундамент для проектирования Saga
- [Паттерны масштабирования](scalability-patterns-interview.md) -- independent scaling, eventual consistency как способ масштабирования


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [API Gateway](api-gateway-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [BFF Pattern](bff-pattern-interview.md)
- [Стратегии кэширования](caching-strategies-interview.md)
- [CAP-теорема](cap-theorem-interview.md)
- [Clean Architecture](clean-architecture-interview.md)
- [Паттерны согласованности](consistency-patterns-interview.md)
