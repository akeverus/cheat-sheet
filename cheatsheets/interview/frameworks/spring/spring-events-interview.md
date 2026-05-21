---
title: "Вопросы на собеседовании: Spring Events"
description: "Spring Events: ApplicationEvent, @EventListener, ApplicationEventPublisher, @TransactionalEventListener, async events, @DomainEvents, Spring Modulith"
tags:
  - interview
  - spring
  - spring-events-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Events"
  - "Spring Events interview"
  - "Spring Events собеседование"
prerequisites:
  - "[[spring-events]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring Events`

`Spring Events` — механизм слабого связывания компонентов через события: публикатор не знает о подписчиках. По умолчанию синхронны и транзакционны. `@TransactionalEventListener` — ключевой инструмент для domain events в DDD-архитектуре.

## Полезные ссылки

### Официальная документация

- [Spring Application Events Reference](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events) — reference
- [Spring Modulith Events](https://docs.spring.io/spring-modulith/reference/events.html) — externalization

### Baeldung tutorials

- [Spring Events](https://www.baeldung.com/spring-events) — основы
- [Spring Context Events](https://www.baeldung.com/spring-context-events) — системные события
- [How to Test Spring Application Events](https://www.baeldung.com/spring-test-application-events)
- [DDD aggregates and @DomainEvents](https://www.baeldung.com/spring-data-ddd)
- [Event Externalization with Spring Modulith](https://www.baeldung.com/spring-modulith-event-externalization)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Spring Events и зачем нужны?](#q1-что-такое-spring-events-и-зачем-нужны)
- [Q2. Как опубликовать и обработать событие?](#q2-как-опубликовать-и-обработать-событие)
- [Q3. Как создать кастомное событие?](#q3-как-создать-кастомное-событие)
- [Q4. Какие встроенные Spring-события (context events) существуют?](#q4-какие-встроенные-spring-события-context-events-существуют)

**Продвинутые возможности**
- [Q5. (!) Как сделать обработку события асинхронной?](#q5-как-сделать-обработку-события-асинхронной)
- [Q6. (!) Что такое `@TransactionalEventListener` и зачем он нужен?](#q6-что-такое-transactionaleventlistener-и-зачем-он-нужен)
- [Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?](#q7-какие-фазы-транзакции-поддерживает-transactionaleventlistener)
- [Q8. Как управлять порядком обработчиков одного события?](#q8-как-управлять-порядком-обработчиков-одного-события)
- [Q9. Как условно обработать событие?](#q9-как-условно-обработать-событие)
- [Q10. Может ли `@EventListener` возвращать значение?](#q10-может-ли-eventlistener-возвращать-значение)

**DDD и интеграция**
- [Q11. (!) Что такое `@DomainEvents` в Spring Data?](#q11-что-такое-domainevents-в-spring-data)
- [Q12. Зачем нужен `@AfterDomainEventPublication`?](#q12-зачем-нужен-afterdomaineventpublication)
- [Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?](#q13-что-такое-spring-modulith-и-как-события-используются-для-межмодульного-взаимодействия)

**Тестирование**
- [Q14. Как тестировать публикацию и обработку Spring Events?](#q14-как-тестировать-публикацию-и-обработку-spring-events)

**Архитектурные вопросы**
- [Q15. Когда использовать Spring Events, а когда прямой вызов метода?](#q15-когда-использовать-spring-events-а-когда-прямой-вызов-метода)
- [Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?](#q16-в-чём-разница-между-spring-events-и-kafkarabbitmq-сообщениями)

---

## Q1. Что такое Spring Events и зачем нужны?

**Spring Events** — реализация паттерна Observer/Publisher-Subscriber в рамках Spring-контекста. Позволяет компонентам общаться, не зная друг о друге.

```
OrderService.save() → publishEvent(OrderCreatedEvent)
                              ↓
        ┌─────────────────────────────────┐
        ↓                                 ↓
EmailListener.onOrderCreated()    InventoryListener.onOrderCreated()
```

**Зачем нужны:**
- Избежать прямых зависимостей между модулями (`OrderService` не зависит от `EmailService`)
- Реализовать side effects после бизнес-операций
- Реализовать domain events в DDD (событие из агрегата)
- Уведомить несколько компонентов об одном действии без изменения кода публикатора

**Когда НЕ нужны:**
- Когда логика должна выполниться синхронно и является частью транзакции
- Когда нужен гарантированный результат от обработчика (events — fire-and-forget)
- Для взаимодействия между микросервисами (там нужен MessageBroker)


> [!mcq]
>
> **Вопрос:** Что такое Spring Events в Spring Framework и какую задачу они решают?
>
> ---
>
> #### A) Spring Events — это синхронные RPC-вызовы между микросервисами через REST/gRPC, упакованные в обёртку `ApplicationEvent` — ❌ Неверно
>
> **Что на самом деле:** Spring Events работают исключительно **внутри одного `ApplicationContext`** (in-process, in-memory). Это локальный механизм Publisher/Subscriber поверх Spring-контейнера; никакой сетевой коммуникации между процессами он не выполняет. Для межсервисной коммуникации применяют Kafka, RabbitMQ, REST или gRPC.
>
> **Откуда путаница:** слово «event» в индустрии часто ассоциируется с межсервисными очередями (event-driven microservices, Event Streaming на Kafka). Middle-разработчик, видевший Spring Cloud Stream, может ошибочно перенести эту модель на ванильные Spring Events.
>
> **Если бы это было правдой:** падение receiver-сервиса валило бы публикатора, либо `publishEvent()` блокировался бы на сетевых таймаутах. На практике же `publishEvent()` — обычный вызов внутри JVM, и при рестарте процесса все ещё не доставленные события **пропадают** (in-memory).
>
> ---
>
> #### B) Spring Events — реализация Observer/Publisher-Subscriber внутри `ApplicationContext`: компоненты публикуют события через `ApplicationEventPublisher`, а `@EventListener`/`ApplicationListener` обрабатывают их без прямой зависимости публикатор → подписчик — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Events — это легковесная реализация паттерна Observer внутри Spring-контейнера. Публикатор знает только о `ApplicationEventPublisher`, а подписчики (`@EventListener` или `ApplicationListener<E>`) регистрируются как обычные бины. По умолчанию доставка **синхронная** и в **той же транзакции** что и публикатор — это делает Spring Events идеальным инструментом для in-process domain events в DDD-архитектуре, для side-effect-ов после бизнес-операций и для уведомления нескольких подписчиков одним publish-вызовом без правки кода публикатора.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class OrderService {
>     private final ApplicationEventPublisher publisher;
>     private final OrderRepository orders;
>
>     @Transactional
>     public Order createOrder(OrderRequest req) {
>         Order order = orders.save(new Order(req));
>         publisher.publishEvent(new OrderCreatedEvent(order.getId()));
>         return order;
>     }
> }
>
> @Component
> class EmailListener {
>     @EventListener
>     void on(OrderCreatedEvent e) { /* отправка письма */ }
> }
>
> @Component
> class InventoryListener {
>     @EventListener
>     void on(OrderCreatedEvent e) { /* резерв склада */ }
> }
> ```
>
> **Когда применять:**
> - Domain events в DDD-агрегатах (`AbstractAggregateRoot.registerEvent`) — например, в Spring Modulith-монолитах в стиле Booking.com и других DDD-проектах.
> - Реакция на ApplicationContext lifecycle (`ApplicationReadyEvent` для warm-up, `ContextClosedEvent` для cleanup).
> - Side effects, которые не должны выполняться при rollback (`@TransactionalEventListener(AFTER_COMMIT)`).
> - Несколько обработчиков на одно действие (audit + email + metrics) без правки публикатора.
>
> **Подводные камни:**
> - Событие — fire-and-forget: значение из listener-а публикатор не получит (исключение — chained events через `return`).
> - Async-листенеры разрывают транзакцию и стектрейс — исключение не долетит до публикатора.
> - In-memory: при крэше процесса все непросмотренные события теряются. Для гарантий — Spring Modulith Event Publication Registry (outbox).
>
> **Связанные вопросы:** [[spring-events-interview#Q2]] — публикация и обработка через `ApplicationEventPublisher`; [[spring-events-interview#Q6]] — `@TransactionalEventListener` для транзакционных domain events; [[spring-events-interview#Q16]] — отличия от Kafka/RabbitMQ.
>
> ---
>
> #### C) Spring Events — это синоним `@Scheduled`-задач: триггеры публикуются по cron и обрабатываются `@EventListener`-методами — ❌ Неверно
>
> **Что на самом деле:** `@Scheduled` и Spring Events — разные механизмы. `@Scheduled` запускает метод по расписанию (cron, fixed-delay, fixed-rate), не публикуя никаких событий. Spring Events — это runtime-механизм Observer/Publisher-Subscriber, в котором события публикуются императивно через `publishEvent()` в момент бизнес-операции, а не по таймеру.
>
> **Откуда путаница:** оба механизма «срабатывают сами», без явного вызова получателя. Middle, не различающий триггер-источник, может смешать их в одну ментальную модель «фоновых задач».
>
> **Если бы это было правдой:** не было бы способа отреагировать на бизнес-событие в момент его возникновения — пришлось бы поллить базу по cron, что давало бы задержку (минимум 1 период cron) и лишнюю нагрузку на БД.
>
> ---
>
> #### D) Spring Events — устаревшая API, заменённая на Reactor (`Sinks.Many`) в Spring Framework 6 — ❌ Неверно
>
> **Что на самом деле:** в Spring Framework 6 (Spring Boot 3) `ApplicationEventPublisher` и `@EventListener` **остаются** идиоматичным и активно развиваемым API — добавили поддержку virtual threads, улучшили generic-resolution. Reactor `Sinks.Many` — это реактивный pub/sub примитив, не замена Spring Events (другая модель программирования — non-blocking + backpressure).
>
> **Откуда путаница:** Spring 6 действительно много инвестировал в Reactor (WebFlux, R2DBC), и middle может предполагать что устаревает и блокирующий Events API. На самом деле обе модели сосуществуют.
>
> **Если бы это было правдой:** все proven-production проекты на Spring Boot 2 → 3 ломались бы при апгрейде, так как `@EventListener` исчезал бы. Релизные ноты Spring 6 явно подтверждают обратное.

## Q2. Как опубликовать и обработать событие?

**Публикатор:**

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository repository;

    @Transactional
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(new Order(req));
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order;
    }
}
```

**Слушатель:**

```java
@Component
@Slf4j
public class OrderNotificationListener {

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Order created: {}", event.getOrderId());
        // отправить email, обновить статистику и т.д.
    }
}
```

**Альтернатива — инжектировать `ApplicationContext`** (не рекомендуется — слишком широкий контракт):

```java
applicationContext.publishEvent(new OrderCreatedEvent(order));
```

Лучше — инжектировать `ApplicationEventPublisher` (только публикация, без других возможностей контекста).


> [!mcq]
>
> **Вопрос:** Какой Spring-API рекомендуется инжектировать для публикации событий из бизнес-сервиса?
>
> ---
>
> #### A) `ApplicationContext` — поскольку он наследует `ApplicationEventPublisher` и даёт доступ ко всем бинам — ❌ Неверно
>
> **Что на самом деле:** технически `ApplicationContext` действительно расширяет `ApplicationEventPublisher` и метод `publishEvent()` у него есть. Но контракт слишком широкий — через `ApplicationContext` можно достать любой бин по имени/типу, выполнить `getEnvironment()`, инициировать `refresh()`. Это нарушает Interface Segregation Principle и мешает тестированию (приходится мокать всю фабрику).
>
> **Откуда путаница:** в старых туториалах и legacy-проектах часто инжектируют `ApplicationContext` потому что «он всё умеет». На Spring 2.x это было распространено, но с появлением `ApplicationEventPublisher` в 4.2 практика отошла.
>
> **Если бы это было правдой:** код становится сложнее юнит-тестировать (`Mockito.mock(ApplicationContext.class)` с десятками методов вместо узкого publisher), а сервис де-факто получает Service Locator antipattern. ArchUnit/Sonar обычно подсвечивают такой код как code-smell.
>
> ---
>
> #### B) Статический helper `SpringEventsUtils.publish(event)`, инициализированный из `@PostConstruct` бина-bootstrap-а — ❌ Неверно
>
> **Что на самом деле:** в Spring нет идиоматического статического API для публикации событий. Прокидывать `ApplicationEventPublisher` через статическую переменную — антипаттерн (Service Locator), который ломает тестируемость, конкурентность при старте контекста и порядок инициализации.
>
> **Откуда путаница:** разработчики из мира легаси без DI (или из Android, где часто используется EventBus как singleton) переносят паттерн статического publisher в Spring. Иногда так делают «чтобы не таскать publisher по entity».
>
> **Если бы это было правдой:** в тестах с несколькими `ApplicationContext` (parallel tests, `@DirtiesContext`) статическая переменная указывала бы на «не тот» контекст; события публиковались бы не туда, тесты флакали бы непредсказуемо.
>
> ---
>
> #### C) `ApplicationEventPublisher` через конструкторную инъекцию — узкий контракт только для публикации; обработчики регистрируются через `@EventListener` на методах бинов — ✓ Верно
>
> **Развёрнутое объяснение:** `ApplicationEventPublisher` — узкий интерфейс с единственной задачей: опубликовать событие. Spring автоматически предоставляет его как бин, и `@RequiredArgsConstructor` / `@Autowired` инжектируют его в сервис. Подписчики помечают методы аннотацией `@EventListener` — Spring сам сканирует бины через `EventListenerMethodProcessor` и регистрирует адаптеры. Этот стиль соблюдает ISP и упрощает мокирование в тестах: достаточно `Mockito.mock(ApplicationEventPublisher.class)` и `verify(publisher).publishEvent(any())`.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class OrderService {
>     private final ApplicationEventPublisher publisher;
>     private final OrderRepository orders;
>
>     @Transactional
>     public Order createOrder(OrderRequest req) {
>         Order order = orders.save(new Order(req));
>         publisher.publishEvent(new OrderCreatedEvent(order));
>         return order;
>     }
> }
>
> @Component
> @Slf4j
> public class OrderNotificationListener {
>     @EventListener
>     public void onOrderCreated(OrderCreatedEvent event) {
>         log.info("Order created: {}", event.getOrderId());
>     }
> }
> ```
>
> **Когда применять:** в любом production-сервисе, где нужно публиковать domain events; в DDD-агрегатах через `AbstractAggregateRoot.registerEvent` (Spring Data); в Spring Modulith-приложениях для межмодульной коммуникации.
>
> **Подводные камни:**
> - Если публикуете POJO (не наследник `ApplicationEvent`), Spring обернёт его в `PayloadApplicationEvent<T>` — это влияет на generic-type resolution для `ApplicationListener<PayloadApplicationEvent<MyType>>`.
> - Не публикуйте mutable объект: один и тот же экземпляр получит каждый listener, и параллельные изменения вызовут гонку.
> - `publishEvent()` синхронен: блокирует публикатора на время работы всех listener-ов. Для долгих операций используйте `@Async` (см. [[spring-events-interview#Q5]]).
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q3]] — POJO vs `ApplicationEvent` для кастомных событий; [[spring-events-interview#Q5]] — async-публикация.
>
> ---
>
> #### D) Через инжекцию `ApplicationEventMulticaster` — это рекомендуемый Spring-API; `ApplicationEventPublisher` устарел в Spring 5 — ❌ Неверно
>
> **Что на самом деле:** `ApplicationEventMulticaster` — это **внутренний** компонент Spring, который маршрутизирует событие к listener-ам после `publishEvent()`. Это не публичный contracted API для бизнес-кода. `ApplicationEventPublisher` — актуальный публичный интерфейс, не устаревший.
>
> **Откуда путаница:** название `Multicaster` (как `MulticastSocket`) интуитивно ассоциируется с «рассылкой». Middle, видевший имя в SpringFramework source, может предположить, что это публичный API.
>
> **Если бы это было правдой:** инжекция `ApplicationEventMulticaster` принуждала бы бизнес-код знать о Spring-внутренностях — `addApplicationListener()`, `setTaskExecutor()` и др. Тесты усложнялись бы, ISP нарушался бы ещё сильнее, чем при инжекции `ApplicationContext`.

## Q3. Как создать кастомное событие?

**Вариант 1 — POJO (рекомендован, начиная со Spring 4.2):**

```java
@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private final Long orderId;
    private final String customerId;
    private final BigDecimal total;
}
```

**Вариант 2 — расширение `ApplicationEvent` (старый стиль):**

```java
public class OrderCreatedEvent extends ApplicationEvent {

    private final Long orderId;

    public OrderCreatedEvent(Object source, Long orderId) {
        super(source);  // source — обычно this (публикатор)
        this.orderId = orderId;
    }

    public Long getOrderId() { return orderId; }
}
```

**Предпочтение:** POJO-события — проще, не требуют знания о Spring. `ApplicationEvent`-подход — легаси.

**Immutability:** события должны быть immutable (`@Value`, `record`, `final` поля) — один объект события может читать несколько слушателей.

```java
public record OrderCreatedEvent(Long orderId, String customerId, Instant occurredAt) { }
```


> [!mcq]
>
> **Вопрос:** Как с минимумом ceremony создать кастомное событие в Spring 5+/6 и почему именно так?
>
> ---
>
> #### A) Immutable POJO (или Java `record`) без наследования `ApplicationEvent`; Spring оборачивает его в `PayloadApplicationEvent<T>` автоматически (начиная со Spring 4.2) — ✓ Верно
>
> **Развёрнутое объяснение:** с версии Spring 4.2 событие — это **любой объект**, не обязательно наследник `ApplicationEvent`. При вызове `publisher.publishEvent(payload)` контейнер сам оборачивает POJO в `PayloadApplicationEvent<T>`, сохраняя generic-тип для маршрутизации к нужным `@EventListener`-методам. Это убирает coupling между domain-моделью и Spring API: события из агрегатов остаются «чистым Java», тестируются без Spring, и тот же event-класс можно перенести в shared-модуль или сериализовать в Kafka без зависимости от `org.springframework`. Immutability (`final` поля, `record`, `@Value`) критична — один экземпляр события читают несколько listener-ов, и mutation вызовет race condition.
>
> **Пример:**
> ```java
> // 1. Java record — самый идиоматичный вариант на Spring Boot 3 / Java 17+
> public record OrderCreatedEvent(
>     Long orderId,
>     String customerId,
>     BigDecimal total,
>     Instant occurredAt
> ) {}
>
> // 2. Публикация
> publisher.publishEvent(new OrderCreatedEvent(1L, "CUST-1", new BigDecimal("100"), Instant.now()));
>
> // 3. Подписка — generic-тип резолвится корректно
> @EventListener
> public void on(OrderCreatedEvent event) {
>     log.info("Order {}: {}", event.orderId(), event.total());
> }
>
> // 4. Если нужен access через ApplicationListener<E> — учесть, что Spring обернёт POJO:
> @Component
> class Raw implements ApplicationListener<PayloadApplicationEvent<OrderCreatedEvent>> {
>     public void onApplicationEvent(PayloadApplicationEvent<OrderCreatedEvent> e) {
>         OrderCreatedEvent payload = e.getPayload();
>     }
> }
> ```
>
> **Когда применять:** в подавляющем большинстве проектов Spring Boot 2.x+/3.x — это идиоматический стиль. Особенно ценно в Spring Modulith и DDD: events живут вместе с агрегатом и не тянут Spring-зависимость в domain-слой.
>
> **Подводные камни:**
> - Generic-stripping: если event-класс — generic (`OrderEvent<T>`), на runtime tип `T` стирается и `PayloadApplicationEvent` теряет точность маршрутизации; используйте `ResolvableType` или конкретные подтипы.
> - Для `ApplicationListener<E>` придётся писать `ApplicationListener<PayloadApplicationEvent<OrderCreatedEvent>>` — в этом случае `@EventListener` удобнее.
> - Mutability ломает observability: один listener изменил поле, второй увидел изменённое. Используйте `record` или иммутабельные структуры.
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q2]] — публикация через `ApplicationEventPublisher`; [[spring-events-interview#Q10]] — return-value из listener-а как новое событие.
>
> ---
>
> #### B) Обязательно унаследовать `ApplicationEvent`, передать `source = this` и реализовать `Serializable` — Spring требует этого для маршрутизации — ❌ Неверно
>
> **Что на самом деле:** наследование `ApplicationEvent` — **legacy-стиль**, оставшийся для обратной совместимости со Spring 1.x–4.1. С 4.2 это необязательно: Spring оборачивает POJO в `PayloadApplicationEvent<T>` сам. `Serializable` тоже не требуется — события не пересекают JVM-границы (для этого есть Kafka/RabbitMQ, externalization через Spring Modulith).
>
> **Откуда путаница:** старые туториалы (Baeldung до 2017, книги по Spring 3) показывают именно `extends ApplicationEvent`. Сертификационные тесты VMware Tanzu тоже долго упоминали этот стиль.
>
> **Если бы это было правдой:** domain-классы из агрегатов нельзя было бы вынести в shared-модуль без зависимости от Spring; пришлось бы плодить DTO-обёртки. В Spring Modulith externalization потерял бы смысл — иммутабельный `record` был бы недопустим.
>
> ---
>
> #### C) Использовать `org.springframework.context.event.GenericEvent<T>` — это стандартный wrapper для всех типов событий, поскольку POJO работают только в @EventListener, но не в ApplicationListener — ❌ Неверно
>
> **Что на самом деле:** класса `GenericEvent<T>` в Spring **не существует**. Универсальный wrapper для POJO — это `PayloadApplicationEvent<T>`, и Spring создаёт его автоматически, явно использовать не нужно. POJO одинаково работают и в `@EventListener`, и в `ApplicationListener` (для последнего сигнатура — `ApplicationListener<PayloadApplicationEvent<T>>`).
>
> **Откуда путаница:** напоминает реальные классы `GenericMessage` (Spring Integration) и `GenericApplicationListener` — middle может склеить названия и придумать несуществующий API.
>
> **Если бы это было правдой:** код бы не компилировался — IDE сразу подсветила бы `Cannot resolve symbol 'GenericEvent'`. В реальной кодовой базе таких импортов нет.
>
> ---
>
> #### D) Использовать `@Event` — аннотацию на классе события, которая регистрирует его как Spring-bean типа Event — ❌ Неверно
>
> **Что на самом деле:** аннотации `@Event` в Spring **нет**. События не являются Spring-бинами — они создаются обычным `new` и публикуются через `publishEvent()`. Бинами являются только publisher и listener-ы.
>
> **Откуда путаница:** в CDI/Java EE действительно есть `@Inject Event<MyEvent>` — middle, работавший с Quarkus/WildFly, может перенести семантику CDI на Spring.
>
> **Если бы это было правдой:** event-классы были бы singletone-бинами и не могли бы переносить данные конкретного бизнес-вызова. Каждое создание `new OrderCreatedEvent(...)` ломалось бы — пришлось бы делать prototype-scope, что усложнило бы публикацию.

## Q4. Какие встроенные Spring-события (context events) существуют?

| Событие | Когда публикуется |
|---|---|
| `ContextRefreshedEvent` | После инициализации/обновления контекста |
| `ContextStartedEvent` | При вызове `context.start()` |
| `ContextStoppedEvent` | При вызове `context.stop()` |
| `ContextClosedEvent` | При закрытии контекста |
| `ApplicationReadyEvent` | Когда приложение готово обслуживать запросы (Spring Boot) |
| `ApplicationStartedEvent` | После запуска, до `CommandLineRunner` (Spring Boot) |
| `ApplicationFailedEvent` | При ошибке старта (Spring Boot) |
| `RequestHandledEvent` | После обработки HTTP-запроса (Spring MVC) |

```java
@Component
public class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application is ready! Warming up cache...");
        cacheService.warmUp();
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        log.info("Context is closing. Cleanup...");
    }
}
```

`ApplicationReadyEvent` — предпочтительный момент для действий "при старте": к этому времени все бины инициализированы, Tomcat слушает порт, CommandLineRunners отработали.


> [!mcq]
>
> **Вопрос:** На каком этапе старта Spring Boot-приложения корректно выполнять warm-up кэша / прогрев пулов соединений?
>
> ---
>
> #### A) В `@PostConstruct` любого бина — это «момент старта приложения», и Spring гарантирует, что все остальные бины уже доступны — ❌ Неверно
>
> **Что на самом деле:** `@PostConstruct` срабатывает **во время инициализации конкретного бина**, ещё до того как Spring закончил создавать **все** остальные бины. На этом этапе нет гарантии, что нужные вам зависимости (например, `DataSource` или WebClient) полностью готовы, а embedded Tomcat вообще не стартовал и не слушает порт.
>
> **Откуда путаница:** `@PostConstruct` исторически использовался для «инициализации». Middle, не различающий lifecycle бина и lifecycle приложения, считает их синонимами.
>
> **Если бы это было правдой:** warm-up из `@PostConstruct` падал бы с `BeanCurrentlyInCreationException` или дёргал бы ещё-не-инициализированный pool, а к моменту получения первого HTTP-запроса prepared statements не были бы прогреты — p99 latency после деплоя оставался бы высоким, как при cold start.
>
> ---
>
> #### B) В `ContextStartedEvent` — это событие публикуется когда `applicationContext.start()` вызывает Spring Boot при старте — ❌ Неверно
>
> **Что на самом деле:** `ContextStartedEvent` публикуется только при **явном** вызове `context.start()`, а Spring Boot такого вызова **не делает** в стандартном lifecycle. Это событие предназначено для `Lifecycle`-бинов, которыми надо ручно стартовать/останавливать (например, мессадж-листенеры). На обычное приложение оно никогда не сработает.
>
> **Откуда путаница:** название «Started» интуитивно соответствует «приложение запустилось». Middle, не читавший javadoc, путает с `ApplicationStartedEvent`.
>
> **Если бы это было правдой:** warm-up-метод никогда бы не вызвался — приложение бы запустилось, но кэш остался пустым. Дебаг занял бы часы, потому что ошибки нет, просто «ничего не происходит».
>
> ---
>
> #### C) В `ContextRefreshedEvent` — он публикуется после полной инициализации ApplicationContext и Tomcat уже принимает запросы — ❌ Неверно
>
> **Что на самом деле:** `ContextRefreshedEvent` публикуется после того как Spring закончил создание всех singleton-бинов, но **до** того как Spring Boot запустит embedded-сервер и **до** `CommandLineRunner`/`ApplicationRunner`. Tomcat ещё не слушает порт. К тому же `ContextRefreshedEvent` может публиковаться **несколько раз** (при `context.refresh()` или в иерархии parent/child контекстов).
>
> **Откуда путаница:** в legacy Spring-приложениях на web.xml `ContextRefreshedEvent` действительно был «финальной точкой» старта. С появлением Spring Boot embedded-сервера появился отдельный `ApplicationReadyEvent`, но старая привычка остаётся.
>
> **Если бы это было правдой:** warm-up отрабатывал бы по 2-3 раза (на iterative refresh), приводя к удвоению нагрузки на БД при старте; в parent/child-иерархии warm-up в child-контексте использовал бы ещё не готовые ресурсы parent-а.
>
> ---
>
> #### D) В `ApplicationReadyEvent` — он публикуется в самом конце startup: после bean creation, `CommandLineRunner`, после того как embedded-сервер начал слушать порт. Все ресурсы готовы — это безопасный момент для warm-up — ✓ Верно
>
> **Развёрнутое объяснение:** `ApplicationReadyEvent` — финальное событие Spring Boot lifecycle. Оно публикуется в `SpringApplication.callRunners()` после: (1) полной инициализации `ApplicationContext`, (2) запуска `WebServer` и binding-а порта, (3) вызова всех `CommandLineRunner` и `ApplicationRunner`. К этому моменту приложение **готово обслуживать запросы**, и любая работа в listener-е не приведёт к гонке за неинициализированными ресурсами. Именно поэтому Spring Boot Admin, Micrometer и Kubernetes readiness-probe ориентируются на этот сигнал.
>
> **Пример:**
> ```java
> @Component
> @RequiredArgsConstructor
> @Slf4j
> public class StartupWarmer {
>     private final CacheService cache;
>     private final OrderRepository orders;
>
>     @EventListener(ApplicationReadyEvent.class)
>     public void onReady() {
>         log.info("Application ready — warming up cache");
>         cache.warmUp();
>         orders.findRecent(100); // прогреет prepared statements + connection pool
>     }
>
>     @EventListener(ContextClosedEvent.class)
>     public void onShutdown() {
>         log.info("Context closing — flushing in-flight tasks");
>     }
> }
> ```
>
> Полная таблица Spring/Spring Boot context events:
> | Событие | Когда публикуется |
> |---|---|
> | `ContextRefreshedEvent` | Все singleton-бины инициализированы (может повториться) |
> | `ContextStartedEvent` | После явного `context.start()` (редко) |
> | `ContextStoppedEvent` | После `context.stop()` |
> | `ContextClosedEvent` | При закрытии контекста (shutdown hook) |
> | `ApplicationStartingEvent` | До bean creation, после загрузки `Environment` |
> | `ApplicationEnvironmentPreparedEvent` | После настройки `Environment`, до контекста |
> | `ApplicationContextInitializedEvent` | После создания контекста, до bean definition loading |
> | `ApplicationStartedEvent` | После refresh, до runner-ов |
> | `ApplicationReadyEvent` | После runner-ов — readiness signal |
> | `ApplicationFailedEvent` | При ошибке старта |
>
> **Когда применять:**
> - Прогрев кэшей (Caffeine, Redis) — Netflix, Booking.com.
> - JIT-warm-up через выполнение горячих методов (используется в high-frequency trading).
> - Регистрация в service discovery (Eureka, Consul) — гарантия, что регистрация произойдёт только когда приложение реально готово.
> - Уведомление Slack/PagerDuty о деплое.
>
> **Подводные камни:**
> - В тестах `ApplicationReadyEvent` тоже публикуется — может замедлить `@SpringBootTest`. Делайте warm-up условным через `@Profile("!test")` или `@ConditionalOnProperty`.
> - Долгий warm-up задерживает readiness-probe в Kubernetes — пишите асинхронно (`@Async`), если не критично иметь warm-кэш в первую миллисекунду.
> - Исключение в listener-е по умолчанию **не** валит приложение — оно логируется и проглатывается; явно ловите и проверяйте.
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q5]] — `@Async` для долгого warm-up без блокировки; [[spring-events-interview#Q8]] — `@Order` для последовательности warm-up listener-ов.

## Q5. Как сделать обработку события асинхронной?

По умолчанию Spring Events синхронны: `publishEvent()` не возвращается, пока все слушатели не отработают.

`@Async` на слушателе делает обработку асинхронной:

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("eventsExecutor")
    public Executor eventsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}

@Component
public class NotificationListener {

    @Async("eventsExecutor")
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        // Выполнится в отдельном потоке, не блокируя публикатора
        emailService.send(event.getCustomerId(), "Order created");
    }
}
```

**Важные последствия async-слушателей:**
- Исключение в слушателе не propagates к публикатору
- Нельзя перехватить через `EventListener + транзакция публикатора` (они разорваны)
- Нужен отдельный `@Transactional` в слушателе, если нужна транзакция


> [!mcq]
>
> **Вопрос:** Какая комбинация аннотаций превратит `@EventListener` в асинхронный обработчик и какие побочные эффекты это создаёт?
>
> ---
>
> #### A) Достаточно повесить `@EventListener(async = true)` — Spring сам подберёт пул потоков по умолчанию — ❌ Неверно
>
> **Что на самом деле:** атрибута `async` у `@EventListener` **не существует**. Асинхронность включается отдельной аннотацией `@Async` (плюс `@EnableAsync` на конфиге), а конкретный executor указывается её аргументом — `@Async("eventsExecutor")`.
>
> **Откуда путаница:** middle мог встретить `condition` и `classes` у `@EventListener` и предположить, что и `async` есть. Также в Spring WebFlux/Reactor есть похожие переключатели режима, путающие границы абстракций.
>
> **Если бы это было правдой:** код просто не компилировался бы — IDE сразу подсветила бы «Cannot resolve attribute 'async'». В рантайме никаких странностей не было бы, так как не дошло бы до запуска.
>
> ---
>
> #### B) `@EnableAsync` на любом `@Configuration` + `@Async("executorName")` на методе-листенере, плюс явный `ThreadPoolTaskExecutor` бин — отдельный пул изолирует event-обработку от Tomcat-воркеров — ✓ Верно
>
> **Развёрнутое объяснение:** Spring async работает в три шага: (1) `@EnableAsync` включает `AsyncAnnotationBeanPostProcessor`, который заворачивает бины с `@Async` в AOP-прокси; (2) `@Async("name")` указывает имя `Executor`-бина — без него по умолчанию используется `SimpleAsyncTaskExecutor` (создаёт **новый поток на каждый вызов**, без переиспользования и без ограничений — это путь к OOM); (3) собственный `ThreadPoolTaskExecutor` с фиксированными `corePoolSize`, `maxPoolSize`, `queueCapacity` обеспечивает backpressure и предсказуемое потребление памяти. Изоляция пула от Tomcat-воркеров критична: если event-обработка вешается на сетевом I/O, без отдельного пула это съест воркеры HTTP, и приложение перестанет принимать запросы.
>
> **Пример:**
> ```java
> @Configuration
> @EnableAsync
> public class AsyncConfig {
>     @Bean("eventsExecutor")
>     public Executor eventsExecutor() {
>         ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
>         ex.setCorePoolSize(5);
>         ex.setMaxPoolSize(20);
>         ex.setQueueCapacity(100);
>         ex.setThreadNamePrefix("event-");
>         ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
>         ex.initialize();
>         return ex;
>     }
> }
>
> @Component
> @RequiredArgsConstructor
> public class NotificationListener {
>     private final EmailService email;
>
>     @Async("eventsExecutor")
>     @EventListener
>     public void onOrderCreated(OrderCreatedEvent event) {
>         // выполняется в потоке event-*, публикатор не блокируется
>         email.send(event.customerId(), "Order " + event.orderId() + " created");
>     }
> }
> ```
>
> **Когда применять:**
> - Отправка email/SMS, push-нотификаций — длинные I/O, не критичные к latency публикатора.
> - Логирование/аудит в внешние системы (Splunk, ELK, ClickHouse).
> - Webhooks к внешним партнёрам.
> - Trigger background recomputation после доменного события (recompute leaderboard, refresh materialized view).
>
> **Подводные камни:**
> - Исключение в async-листенере **не** долетит до публикатора — публикатор уже вернулся. Нужен `AsyncUncaughtExceptionHandler`, иначе ошибки пропадают.
> - `@TransactionalEventListener` + `@Async` работает, но транзакция публикатора уже закрыта (AFTER_COMMIT) — листенер видит committed state, и для своих DB-операций ему нужен **отдельный** `@Transactional(propagation = REQUIRES_NEW)`.
> - В Spring Boot 3.2+ доступны virtual threads через `spring.threads.virtual.enabled=true` — но `synchronized` + I/O в виртуальном потоке вызывает pinning carrier-потока; используйте `ReentrantLock`.
> - `@Async` работает только при вызове через прокси-бин (Spring AOP), self-invocation внутри одного класса async-механизм **не** включит.
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q6]] — `@TransactionalEventListener` (по умолчанию синхронный, но можно комбинировать с `@Async`); [[spring-events-interview#Q14]] — тестирование async listener-ов через `Awaitility`.
>
> ---
>
> #### C) Достаточно установить `app.events.async=true` в `application.yml` — Spring Boot автоматически переключит все listener-ы в асинхронный режим — ❌ Неверно
>
> **Что на самом деле:** такого свойства в Spring Boot нет. Async включается на уровне отдельного метода через `@Async`, а не глобальным флагом — иначе разработчики потеряли бы контроль над тем, что синхронно (порядок, транзакционность), а что асинхронно.
>
> **Откуда путаница:** в Spring Boot много автоконфигурации через `application.yml`. Middle, привыкший «всё через property», предполагает существование флага по аналогии с `spring.threads.virtual.enabled` или `spring.task.execution.*`.
>
> **Если бы это было правдой:** глобальное переключение неконтролируемо сломало бы транзакционность всех `@TransactionalEventListener` и порядок `@Order`-листенеров. Любой production-релиз с этим флагом превратился бы в catastrophic failure.
>
> ---
>
> #### D) Через `AsyncEventBus` из Guava EventBus — Spring Events наследуют его API, и для async достаточно использовать `AsyncEventBus` вместо `EventBus` — ❌ Неверно
>
> **Что на самом деле:** Spring Events и Guava EventBus — **разные** независимые библиотеки. Spring не наследует и не использует Guava EventBus; никакого `AsyncEventBus` в Spring API нет.
>
> **Откуда путаница:** Guava EventBus с её `AsyncEventBus` действительно популярен в Java-мире, и middle, видевший оба, может смешать их в памяти. Названия Publisher/Subscriber совпадают.
>
> **Если бы это было правдой:** код не компилировался бы — `AsyncEventBus` отсутствует в classpath Spring. Попытка явно подключить Guava не сделает её частью Spring Events; пришлось бы переписывать всё через Guava-специфичные `@Subscribe`.

## Q6. Что такое `@TransactionalEventListener` и зачем он нужен?

Проблема с `@EventListener`: если слушатель вызывается внутри транзакции и транзакция откатится — слушатель уже отработал (возможно, отправил email о "созданном" заказе, который потом откатился).

`@TransactionalEventListener` решает это, привязывая выполнение к фазе транзакции:

```java
@Service
@Transactional
public class OrderService {
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(order);
        publisher.publishEvent(new OrderCreatedEvent(order.getId()));
        // Если транзакция откатится — слушатель НЕ выполнится (при AFTER_COMMIT)
        return order;
    }
}

@Component
public class OrderNotificationListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        // Выполнится ТОЛЬКО если транзакция успешно закоммичена
        emailService.sendConfirmation(event.getOrderId());
    }
}
```

**Ключевое:** `@TransactionalEventListener` по умолчанию выполняется в **той же транзакции** что и публикатор (но уже после commit, то есть транзакция закрыта). Для новой транзакции: `@Transactional(propagation = REQUIRES_NEW)`.


> [!mcq]
>
> **Вопрос:** Какую конкретную проблему обычного `@EventListener` решает `@TransactionalEventListener` и как именно?
>
> ---
>
> #### A) Обычный `@EventListener` работает только при наличии активной транзакции; `@TransactionalEventListener` снимает это требование и работает всегда — ❌ Неверно
>
> **Что на самом деле:** ситуация **обратная**. Обычный `@EventListener` работает всегда (синхронно в треде публикатора), независимо от наличия транзакции. А `@TransactionalEventListener` **по умолчанию не выполняется**, если транзакции нет — для работы без транзакции нужен явный `fallbackExecution = true`.
>
> **Откуда путаница:** название «Transactional» создаёт иллюзию что-то «более универсальное» или «более защищённое». Middle инвертирует семантику, не сверяясь с javadoc.
>
> **Если бы это было правдой:** разработчик использовал бы `@TransactionalEventListener` для любого listener-а «на всякий случай» — и для синхронных, не-транзакционных событий он молча не сработал бы, потеряв сайд-эффекты.
>
> ---
>
> #### B) `@TransactionalEventListener` шарит транзакцию с публикатором и автоматически делает rollback также и в listener-е при ошибке — ❌ Неверно
>
> **Что на самом деле:** `@TransactionalEventListener` в фазе `AFTER_COMMIT` срабатывает **после** того, как транзакция публикатора закоммичена и закрыта — повлиять на её rollback невозможно. Если listener бросит исключение, это не откатит уже сохранённые изменения публикатора. В фазе `BEFORE_COMMIT` listener ещё в транзакции и может её сломать, но это другой режим.
>
> **Откуда путаница:** ассоциация «транзакционный = атомарный с публикатором» из мира SQL/JTA. Middle не различает «привязан к фазе транзакции» и «участвует в одной транзакции».
>
> **Если бы это было правдой:** при ошибке email-listener-а заказ откатился бы в БД, но клиент уже получил подтверждение по другому каналу. Это создавало бы distributed inconsistency, аналог classic dual-write antipattern.
>
> ---
>
> #### C) Решает проблему «email отправлен, а транзакция откатилась»: обработчик привязывается к фазе транзакции (по умолчанию `AFTER_COMMIT`) и выполняется только при успешном коммите. При rollback — не вызывается — ✓ Верно
>
> **Развёрнутое объяснение:** ванильный `@EventListener` выполняется синхронно прямо в треде публикатора **до** момента коммита транзакции. Если после publishEvent в публикаторе случится ошибка и транзакция откатится, listener уже отработал — email ушёл, очередь получила сообщение, метрика инкрементилась. Это classic dual-write проблема. `@TransactionalEventListener` решает её через `TransactionSynchronization`: Spring регистрирует listener как callback на нужную фазу (`AFTER_COMMIT`, `AFTER_ROLLBACK`, `AFTER_COMPLETION`, `BEFORE_COMMIT`) и вызывает его только когда фаза достигнута. Это **базовый кирпич** для transactional outbox в DDD/Spring Modulith.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> @Transactional
> public class OrderService {
>     private final OrderRepository orders;
>     private final ApplicationEventPublisher publisher;
>
>     public Order createOrder(OrderRequest req) {
>         Order order = orders.save(new Order(req));
>         publisher.publishEvent(new OrderCreatedEvent(order.getId()));
>         // если ниже бросится исключение — listener НЕ выполнится
>         validateInventory(order); // может бросить
>         return order;
>     }
> }
>
> @Component
> @RequiredArgsConstructor
> public class OrderEmailListener {
>     private final EmailService email;
>
>     // AFTER_COMMIT — default. Выполнится только после успешного commit
>     @TransactionalEventListener
>     public void sendConfirmation(OrderCreatedEvent event) {
>         email.send(event.orderId(), "Your order is confirmed");
>     }
> }
> ```
>
> **Когда применять:**
> - Domain events, которые становятся side-effect-ами (email, push, аудит в Splunk, метрика в Prometheus).
> - Transactional outbox pattern (Spring Modulith Event Publication Registry под капотом использует именно `@TransactionalEventListener` + DB-таблицу).
> - Booking.com, Wolt — для отвязки межмодульных сайд-эффектов от транзакции основной операции.
>
> **Подводные камни:**
> - Listener выполняется **синхронно** в треде, который завершил commit, — долгая работа задержит освобождение треда. Комбинируйте с `@Async`.
> - Если нет активной транзакции, listener **не сработает** (требуется `fallbackExecution = true` или wrapper-`@Transactional`).
> - При `AFTER_COMMIT` транзакция уже закрыта — для DB-операций в listener-е нужен явный `@Transactional(propagation = REQUIRES_NEW)`.
> - Исключение в listener-е логируется как `WARN` и не валит публикатора. Для критичных операций добавьте handle + outbox-таблицу.
>
> **Связанные вопросы:** [[spring-events-interview#Q5]] — `@Async` для не блокирующих listener-ов; [[spring-events-interview#Q7]] — список фаз `AFTER_COMMIT`/`AFTER_ROLLBACK`/`BEFORE_COMMIT`/`AFTER_COMPLETION`; [[spring-events-interview#Q13]] — Spring Modulith и transactional outbox на базе этого механизма.
>
> ---
>
> #### D) `@TransactionalEventListener` ускоряет работу обычного `@EventListener` за счёт переиспользования connection pool из текущей транзакции — ❌ Неверно
>
> **Что на самом деле:** перформанс — не цель `@TransactionalEventListener`. Он решает **корректность** (атомарность сайд-эффектов с транзакцией), а не скорость. По латенси transactional-listener обычно даже **медленнее**, потому что выполнение откладывается до фазы commit.
>
> **Откуда путаница:** «transactional» в SQL-контексте часто связывают с эффективным batch-режимом. Middle переносит это на event-уровень.
>
> **Если бы это было правдой:** разработчики оптимизировали бы латенси, переключаясь на `@TransactionalEventListener` — и получали бы обратный эффект (worse latency) и потерю sync-семантики.

## Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
```

| Фаза | Когда выполняется |
|---|---|
| `AFTER_COMMIT` (default) | После успешного commit |
| `AFTER_ROLLBACK` | После rollback |
| `AFTER_COMPLETION` | После завершения транзакции (commit или rollback) |
| `BEFORE_COMMIT` | Перед commit (ещё в рамках транзакции) |

```java
// Для BEFORE_COMMIT — можно ещё участвовать в транзакции
@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
@Transactional(propagation = Propagation.MANDATORY)
public void auditBeforeCommit(OrderCreatedEvent event) {
    auditRepository.save(new AuditEntry(event));
}

// Для AFTER_ROLLBACK — например, компенсирующие действия
@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
public void onFailure(OrderCreatedEvent event) {
    log.error("Order creation rolled back: {}", event.getOrderId());
    alertService.notify("Order rollback", event.getOrderId());
}
```

**Подводный камень:** если у публикатора нет активной транзакции, `@TransactionalEventListener` по умолчанию **не выполнится**. Параметр `fallbackExecution = true` позволяет выполняться и без транзакции:

```java
@TransactionalEventListener(fallbackExecution = true)
public void handle(MyEvent event) { }
```


> [!mcq]
>
> **Вопрос:** Какой набор фаз поддерживает `@TransactionalEventListener` и какая из них дефолтная?
>
> ---
>
> #### A) `BEFORE_COMMIT`, `AFTER_COMMIT` (default), `AFTER_ROLLBACK`, `AFTER_COMPLETION` — четыре фазы для гибкого реагирования на lifecycle транзакции — ✓ Верно
>
> **Развёрнутое объяснение:** перечисление `TransactionPhase` содержит ровно эти четыре константы: `BEFORE_COMMIT` (listener вызывается перед коммитом, ещё внутри транзакции — можно дописать что-то в БД), `AFTER_COMMIT` (default, после успешного commit — безопасно для side-effects), `AFTER_ROLLBACK` (после rollback — для компенсаций), `AFTER_COMPLETION` (после commit или rollback — для cleanup, не различает успех/неуспех). Под капотом Spring использует `TransactionSynchronizationManager.registerSynchronization()` и интерфейс `TransactionSynchronization` — те же фазы, что доступны во вручную написанных synchronization-callback-ах.
>
> **Пример:**
> ```java
> @Component
> public class TransactionalListeners {
>
>     // BEFORE_COMMIT — ещё в транзакции, можно писать в БД
>     @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
>     @Transactional(propagation = Propagation.MANDATORY)
>     public void auditBefore(OrderCreatedEvent e) {
>         auditRepo.save(new AuditEntry(e.orderId(), "BEFORE_COMMIT"));
>     }
>
>     // AFTER_COMMIT (default) — safe side-effects
>     @TransactionalEventListener
>     public void sendEmail(OrderCreatedEvent e) {
>         email.send(e.customerId(), "Order created");
>     }
>
>     // AFTER_ROLLBACK — компенсации, алёрты
>     @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
>     public void onRollback(OrderCreatedEvent e) {
>         alertService.notify("Order rollback: " + e.orderId());
>     }
>
>     // AFTER_COMPLETION — cleanup, независимо от исхода
>     @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
>     public void cleanup(OrderCreatedEvent e) {
>         metrics.recordOrderProcessingFinished();
>     }
> }
> ```
>
> Сравнение фаз:
> | Фаза | Транзакция | Кейсы |
> |---|---|---|
> | `BEFORE_COMMIT` | Открыта | Audit, late validation, materialized snapshot |
> | `AFTER_COMMIT` | Закрыта (commit) | Email, push, outbox publish to Kafka |
> | `AFTER_ROLLBACK` | Закрыта (rollback) | Compensation, alert, metrics |
> | `AFTER_COMPLETION` | Закрыта | Cleanup tmp ресурсов, span close |
>
> **Когда применять:**
> - `BEFORE_COMMIT` — для audit-trail-записей, которые должны коммититься атомарно с бизнес-данными.
> - `AFTER_COMMIT` — основной кейс (Booking.com, Wolt): уведомить внешний мир только после успешной транзакции.
> - `AFTER_ROLLBACK` — алёртинг в PagerDuty/Slack «order $orderId rolled back».
> - `AFTER_COMPLETION` — span/trace close в OpenTelemetry; cleanup независимо от исхода.
>
> **Подводные камни:**
> - `fallbackExecution = true` нужен, если listener должен срабатывать и без транзакции — иначе он молча проглатывается.
> - В `BEFORE_COMMIT` исключение **откатывает** транзакцию публикатора — будьте осторожны: что выглядит как «audit» может сломать main flow.
> - `AFTER_COMMIT` не гарантирует delivery: если процесс упадёт сразу после commit, listener не отработает (для гарантий — Spring Modulith Event Publication Registry или Kafka outbox).
>
> **Связанные вопросы:** [[spring-events-interview#Q6]] — суть `@TransactionalEventListener`; [[spring-events-interview#Q5]] — `@Async` для отложенного выполнения; [[spring-events-interview#Q13]] — Spring Modulith outbox pattern, использующий эти фазы.
>
> ---
>
> #### B) `PRE_COMMIT`, `POST_COMMIT`, `ON_ERROR`, `ALWAYS` — четыре фазы из стандарта Jakarta Transactions (JTA) — ❌ Неверно
>
> **Что на самом деле:** Spring использует собственные имена фаз — `BEFORE_COMMIT`, `AFTER_COMMIT`, `AFTER_ROLLBACK`, `AFTER_COMPLETION`. Перечислений `PRE_COMMIT`/`POST_COMMIT`/`ON_ERROR`/`ALWAYS` в `org.springframework.transaction.event.TransactionPhase` нет.
>
> **Откуда путаница:** в JTA / Jakarta Transactions есть `SessionSynchronization`, в Hibernate — `BeforeTransactionCompletionProcess`/`AfterTransactionCompletionProcess`, в JPA — `@PrePersist`/`@PostPersist`. Middle может смешать терминологию.
>
> **Если бы это было правдой:** код с `@TransactionalEventListener(phase = TransactionPhase.PRE_COMMIT)` не компилировался бы, так как этой константы нет в enum.
>
> ---
>
> #### C) Только две фазы — `BEFORE_TX` и `AFTER_TX`, потому что внутри транзакции listener выполнять опасно — ❌ Неверно
>
> **Что на самом деле:** в Spring **четыре** фазы, не две. `BEFORE_COMMIT` (как раз внутри транзакции, не запрещено) и `AFTER_ROLLBACK` существуют отдельно от `AFTER_COMMIT` — это даёт гибкость для разных типов реакций.
>
> **Откуда путаница:** упрощение модели в голове middle, не читавшего исходник. Внутренний предрассудок «нельзя писать в БД внутри listener-а» приводит к выводу что фаз внутри транзакции не должно быть.
>
> **Если бы это было правдой:** невозможно было бы реализовать audit-записи через event-driven подход — пришлось бы хардкодить вызов `auditRepository.save()` рядом с бизнес-кодом, теряя слабую связанность.
>
> ---
>
> #### D) Только одна фаза `AFTER_COMMIT` — фаза задаётся не аннотацией, а через `TransactionSynchronizationManager` программно — ❌ Неверно
>
> **Что на самом деле:** фаза задаётся атрибутом `phase = TransactionPhase.X` прямо в аннотации `@TransactionalEventListener`. Программный доступ через `TransactionSynchronizationManager.registerSynchronization()` существует как нижнеуровневый API, но для аннотации это не требуется.
>
> **Откуда путаница:** middle, видевший только default-кейс (`AFTER_COMMIT` без явного указания), может посчитать что других нет. Программный API виден в исходниках Spring и кажется единственным «настоящим» способом.
>
> **Если бы это было правдой:** реализовать `AFTER_ROLLBACK`-компенсации через аннотации было бы нельзя — пришлось бы каждый раз руками регистрировать synchronization, что разрушает суть декларативного подхода Spring.

## Q8. Как управлять порядком обработчиков одного события?

`@Order` на методе с `@EventListener`:

```java
@Component
public class OrderLifecycleListeners {

    @Order(1)
    @EventListener
    public void validateOrder(OrderCreatedEvent event) {
        // Выполнится первым
    }

    @Order(2)
    @EventListener
    public void auditOrder(OrderCreatedEvent event) {
        // Выполнится вторым
    }

    @Order(3)
    @EventListener
    public void notifyCustomer(OrderCreatedEvent event) {
        // Выполнится третьим
    }
}
```

Без `@Order` порядок не гарантирован. Меньшее число — выше приоритет.

`Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` — самый высокий приоритет.

**Важно:** порядок работает только для синхронных слушателей. Для async-слушателей порядок не определён.


> [!mcq]
>
> **Вопрос:** Как гарантировать порядок выполнения нескольких synchronous-listener-ов одного события и какие ограничения этого механизма?
>
> ---
>
> #### A) Spring выполняет listener-ы строго в порядке регистрации бинов — задавать его явно не нужно — ❌ Неверно
>
> **Что на самом деле:** порядок listener-ов **не гарантирован** Spring-ом без явных указаний. Внутри Spring listener-ы хранятся в коллекциях, и порядок зависит от стратегии classpath scanning, имён бинов и `BeanDefinitionRegistry`-логики — все эти детали могут меняться при апгрейде Spring и не являются контрактом.
>
> **Откуда путаница:** в простых проектах сейчас может работать «как ожидается», и middle считает это гарантией. Меняется одна зависимость или сортировка classpath — порядок ломается.
>
> **Если бы это было правдой:** разработчики строили бы критическую логику (validate → audit → notify) на «угадывании» порядка, и при апгрейде Spring Boot 3.1 → 3.2 пропадала бы валидация перед уведомлением — клиентам уходили бы email-ы о несуществующих заказах.
>
> ---
>
> #### B) Использовать порядковые номера в имени метода (`onOrderCreated1`, `onOrderCreated2`, `onOrderCreated3`) — Spring парсит цифру в конце имени и применяет её как `@Order` — ❌ Неверно
>
> **Что на самом деле:** Spring не парсит имена методов для определения порядка. Имя метода для `@EventListener` — произвольное; механизм упорядочивания — отдельная аннотация `@Order` (или интерфейс `Ordered`) с явным числовым приоритетом.
>
> **Откуда путаница:** некоторые JUnit-runner-ы / cucumber-плагины действительно парсят суффикс в имени теста. Перенос этой эвристики на Spring listener-ы — частая ошибка.
>
> **Если бы это было правдой:** код с двумя listener-ами `onEvent1` и `onEvent2` ломался бы при рефакторинге имён через IDE, без видимой связи с переименованием. Это создавало бы invisible coupling между именами и порядком.
>
> ---
>
> #### C) Поставить `@Async` и порядок будет определяться приоритетом потока (`Thread.setPriority`) — ❌ Неверно
>
> **Что на самом деле:** `@Async` **уничтожает** какой-либо порядок — каждый listener запускается параллельно в отдельном потоке. JVM не гарантирует исполнение по `Thread.priority` (hint, не контракт), а на практике приоритеты потоков игнорируются современными OS-планировщиками.
>
> **Откуда путаница:** аналогия с приоритезированными очередями (`PriorityBlockingQueue`) и thread priority. Middle не различает «order of dispatch» и «scheduling priority».
>
> **Если бы это было правдой:** проблема race condition между audit и notify решалась бы «через приоритеты», но фактически порядок всё равно был бы случайным — баги воспроизводились бы только под нагрузкой и были бы невоспроизводимыми.
>
> ---
>
> #### D) `@Order(N)` (или `@EventListener` + интерфейс `Ordered`) на каждом synchronous-listener-методе — Spring сортирует listener-ы по возрастанию `N`. Для async-listener-ов порядок не определён — ✓ Верно
>
> **Развёрнутое объяснение:** `@Order(int)` — стандартный механизм Spring для упорядочивания list-of-beans-ситуаций (filters, AOP-aspect-ы, listener-ы). Меньшее число — выше приоритет (раньше выполнится). Константы `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` и `Ordered.LOWEST_PRECEDENCE = Integer.MAX_VALUE` задают крайние значения. Для `@EventListener` Spring сортирует listener-ы перед каждым диспатчем и вызывает их строго по возрастанию. Это работает только для synchronous-листенеров — `@Async` запускает каждый в своём треде параллельно, и любая последовательность теряется.
>
> **Пример:**
> ```java
> @Component
> public class OrderLifecycleListeners {
>
>     @Order(1) // или Ordered.HIGHEST_PRECEDENCE
>     @EventListener
>     public void validate(OrderCreatedEvent e) {
>         if (!validator.isValid(e)) throw new IllegalStateException();
>     }
>
>     @Order(2)
>     @EventListener
>     public void audit(OrderCreatedEvent e) {
>         auditRepo.save(new AuditEntry(e.orderId()));
>     }
>
>     @Order(3)
>     @EventListener
>     public void notify(OrderCreatedEvent e) {
>         email.send(e.customerId(), "Order " + e.orderId());
>     }
> }
> // Гарантированный порядок: validate → audit → notify.
> // Если validate бросит — audit и notify НЕ выполнятся (sync chain).
> ```
>
> **Когда применять:**
> - Цепочки `validate → audit → notify`, где валидация должна предшествовать сайд-эффектам.
> - Множественные `@TransactionalEventListener(AFTER_COMMIT)` — `@Order` тоже работает, и сначала запишет outbox, потом отправит email.
> - Интеграционные тесты: явный `@Order` делает порядок воспроизводимым.
>
> **Подводные камни:**
> - `@Order` **не работает** для `@Async`-листенеров — для последовательности используйте `CompletableFuture.thenCompose` или chained events (`return` из listener-а).
> - Одинаковый `@Order(N)` у двух listener-ов даёт нестабильный порядок — Spring не гарантирует tie-breaker.
> - Не путайте с `@Priority` (jakarta.annotation) — Spring уважает свой `@Order`, JSR-250 `@Priority` тоже распознаётся, но в legacy-стиле.
> - Если listener бросает исключение, последующие listener-ы по умолчанию **не вызываются** (sync chain). Для изоляции добавьте try/catch внутри метода.
>
> **Связанные вопросы:** [[spring-events-interview#Q5]] — `@Async` (где порядок теряется); [[spring-events-interview#Q7]] — фазы `@TransactionalEventListener` (`@Order` работает внутри фазы); [[spring-events-interview#Q10]] — chained events через return-value.

## Q9. Как условно обработать событие?

`condition` в `@EventListener` — SpEL-выражение:

```java
@Component
public class PremiumOrderListener {

    @EventListener(condition = "#event.total > 10000")
    public void onLargeOrder(OrderCreatedEvent event) {
        // Только для заказов на сумму > 10000
        vipService.assignManager(event.getCustomerId());
    }

    @EventListener(condition = "#event.orderId != null && #event.customerId.startsWith('VIP')")
    public void onVipOrder(OrderCreatedEvent event) {
        priorityService.fastTrack(event.getOrderId());
    }
}
```

Параметры SpEL:
- `#event` — объект события (имя совпадает с параметром метода)
- `#root.event` — то же самое через root object
- Можно обращаться к полям и методам объекта


> [!mcq]
>
> **Вопрос:** Как ограничить вызов `@EventListener` подмножеством событий по их полям (например, обрабатывать только `OrderCreatedEvent` с `total > 10_000`)?
>
> ---
>
> #### A) Сделать if-проверку в начале метода-listener-а: `if (event.getTotal() <= 10_000) return;` — Spring не поддерживает условную фильтрацию — ❌ Неверно
>
> **Что на самом деле:** Spring **поддерживает** декларативную фильтрацию через атрибут `condition` у `@EventListener`. If-проверка работает, но это менее идиоматично: смешивает фильтрацию с бизнес-логикой, нарушает single-responsibility (метод одновременно «является listener-ом для всех событий» и «фильтрует»), хуже читается и не отображается в IDE/audit-инструментах.
>
> **Откуда путаница:** middle, не читавший javadoc, может не знать про `condition` — а if-фильтрация работает «нормально», поэтому подход кажется единственно верным.
>
> **Если бы это было правдой:** код был бы засорён pre-condition-проверками, и Spring Boot Actuator не смог бы показать «какие listener-ы применимы к этому событию» без выполнения. Тестирование фильтра требовало бы запуска listener-а.
>
> ---
>
> #### B) Атрибут `condition` в `@EventListener(condition = "#event.total > 10000")` — SpEL-выражение, в котором `#event` ссылается на объект события, `#root.event` — то же через root-object — ✓ Верно
>
> **Развёрнутое объяснение:** `@EventListener` (и `@TransactionalEventListener`) принимает атрибут `condition` — SpEL-выражение, которое Spring оценивает перед вызовом метода. Если выражение возвращает `false`, listener не вызывается. Доступны: `#root.event` (само событие), `#<paramName>` (имя параметра метода), `#root.args[0]`, `target` (бин-listener), beans через `@beanName`. SpEL компилируется и кешируется — overhead минимальный. Это декларативная фильтрация, не пересекающаяся с бизнес-логикой и хорошо видимая в IDE/audit.
>
> **Пример:**
> ```java
> @Component
> public class PremiumOrderListeners {
>
>     // Фильтр по числовому полю
>     @EventListener(condition = "#event.total > 10000")
>     public void onLargeOrder(OrderCreatedEvent event) {
>         vipService.assignManager(event.customerId());
>     }
>
>     // Несколько условий
>     @EventListener(condition = "#event.orderId != null && #event.customerId.startsWith('VIP-')")
>     public void onVipOrder(OrderCreatedEvent event) {
>         priority.fastTrack(event.orderId());
>     }
>
>     // Можно дёргать beans через @beanName
>     @EventListener(condition = "@featureFlags.isEnabled('large-order-bonus')")
>     public void onLargeOrderWithFlag(OrderCreatedEvent event) {
>         bonusService.grantPoints(event.customerId());
>     }
>
>     // Combine с phase
>     @TransactionalEventListener(
>         phase = TransactionPhase.AFTER_COMMIT,
>         condition = "#event.country == 'RU'"
>     )
>     public void onRuOrder(OrderCreatedEvent event) {
>         rocketChat.notify(event);
>     }
> }
> ```
>
> **Когда применять:**
> - Условные нотификации (VIP-клиенты, large-orders, специфичные регионы).
> - Feature flags через Unleash/LaunchDarkly — `condition = "@featureFlags.isEnabled('xxx')"`.
> - Изоляция тестовых сценариев — `condition = "!#event.synthetic"` отключает обработку тестовых событий в integration-тестах.
> - Booking.com / Wolt — условная маршрутизация side-effects в зависимости от страны/валюты/типа клиента.
>
> **Подводные камни:**
> - SpEL **runtime-компилируется** — ошибка в выражении (опечатка в имени поля) проявляется только при первом match-е события. Покройте integration-тестом.
> - Бросать исключение из SpEL небезопасно: оно превращается в `SpelEvaluationException`, listener просто не вызовется (хорошо для null-safety: используйте `safe navigation` `#event?.total ?: 0`).
> - Для `@TransactionalEventListener` `condition` оценивается **в момент публикации**, не в момент commit — если ваше событие mutable и поля меняются после publish, фильтрация работает по старому состоянию.
> - При сложной логике фильтра лучше один listener + явный if внутри, чем нечитаемое SpEL-выражение длиной в строку.
>
> **Связанные вопросы:** [[spring-events-interview#Q2]] — публикация событий; [[spring-events-interview#Q5]] — async listener-ы (`condition` работает и с ними); [[spring-events-interview#Q7]] — комбинация `condition` + `phase` для `@TransactionalEventListener`.
>
> ---
>
> #### C) Через отдельную аннотацию `@ConditionalOnEvent` из spring-boot-autoconfigure — ❌ Неверно
>
> **Что на самом деле:** аннотации `@ConditionalOnEvent` в Spring Boot **нет**. Семейство `@ConditionalOnX` работает на уровне auto-configuration (включение бинов в контекст), а не на уровне runtime-фильтрации событий.
>
> **Откуда путаница:** в Spring Boot есть `@ConditionalOnClass`, `@ConditionalOnProperty`, `@ConditionalOnBean` — обилие conditional-аннотаций. Middle ожидает что и для событий есть свой `@Conditional...`.
>
> **Если бы это было правдой:** условие проверялось бы один раз при старте контекста (как все Conditional), а не на каждом событии — фильтрация по динамическим полям (`event.total > 10000`) работала бы некорректно.
>
> ---
>
> #### D) Через `@EventFilter(predicate = MyPredicate.class)` — аннотация принимает класс-предикат `Predicate<E>` для проверки события — ❌ Неверно
>
> **Что на самом деле:** аннотации `@EventFilter` в Spring **нет**. Условная обработка реализована через `condition`/SpEL в самом `@EventListener`, без отдельной аннотации и без класса-предиката.
>
> **Откуда путаница:** в Servlet API есть `@WebFilter` и `Filter`-интерфейс, в Spring Security — фильтры безопасности; middle переносит концепцию «фильтр-класс» на событийную систему.
>
> **Если бы это было правдой:** на каждое условие пришлось бы создавать отдельный класс-предикат — ceremony значительно выросла бы. Простая `total > 10_000` проверка требовала бы отдельного `LargeOrderPredicate.class` бина.

## Q10. Может ли `@EventListener` возвращать значение?

Да — если метод возвращает объект, он становится новым событием и публикуется автоматически:

```java
@Component
public class OrderEventChain {

    @EventListener
    public OrderShippedEvent onOrderPaid(OrderPaidEvent event) {
        shippingService.arrangeShipping(event.getOrderId());
        return new OrderShippedEvent(event.getOrderId(), LocalDate.now().plusDays(3));
        // Spring автоматически опубликует OrderShippedEvent
    }

    @EventListener
    public void onOrderShipped(OrderShippedEvent event) {
        emailService.sendShippingNotification(event);
    }
}
```

Если вернуть коллекцию — каждый элемент публикуется как отдельное событие:

```java
@EventListener
public List<NotificationEvent> onOrderCreated(OrderCreatedEvent event) {
    return List.of(
        new EmailNotification(event.getCustomerId()),
        new PushNotification(event.getCustomerId())
    );
}
```


> [!mcq]
>
> **Вопрос:** Что произойдёт, если метод `@EventListener` имеет return-type, отличный от `void`?
>
> ---
>
> #### A) Spring проигнорирует return value — это просто не используется и компилятор должен бы дать warning — ❌ Неверно
>
> **Что на самом деле:** Spring **использует** return value. Не-null объект, возвращённый из listener-а, автоматически публикуется как **новое событие** через тот же `ApplicationEventPublisher`. Это специально поддерживаемая фича для chained events.
>
> **Откуда путаница:** во многих фреймворках event-handler-ы — это «void by convention». Middle, видевший Servlet-фильтры или Kafka-консьюмеры, переносит привычку.
>
> **Если бы это было правдой:** разработчики не могли бы строить event-chains декларативно — пришлось бы внутри listener-а явно дёргать `publisher.publishEvent(...)`, что менее элегантно и затрудняет тестирование.
>
> ---
>
> #### B) Метод вызовет `IllegalSignatureException` при старте контекста — `@EventListener` требует возврат `void` — ❌ Неверно
>
> **Что на самом деле:** контракт `@EventListener` допускает любой return-type. Контекст стартует нормально, и любой не-null результат публикуется. Возврат `void` или `null` — просто отсутствие нового события в цепочке.
>
> **Откуда путаница:** в JSR-330/CDI Observer-ы должны быть void; middle переносит правило из CDI на Spring.
>
> **Если бы это было правдой:** application context падал бы при первом сканировании listener-ов с не-void return-type. На практике приложения работают без проблем.
>
> ---
>
> #### C) Не-null объект автоматически публикуется как новое событие; коллекция или массив — каждый элемент как отдельное событие — ✓ Верно
>
> **Развёрнутое объяснение:** в Spring `ApplicationListenerMethodAdapter` после вызова listener-метода смотрит на result. Если он не `null` и не `void`: один объект публикуется как одно новое событие; `Collection<?>` или `Object[]` — итерируются, каждый элемент публикуется отдельно. `Optional<?>` распаковывается. С Spring 4.2 поддерживаются также `CompletableFuture<?>` и в реактивных сценариях `Publisher<?>` (Reactor). Это позволяет строить event chains декларативно: один listener превращает входное событие в одно или несколько новых, без явного дёргания publisher-а.
>
> **Пример:**
> ```java
> @Component
> @RequiredArgsConstructor
> public class OrderEventChain {
>     private final ShippingService shipping;
>     private final EmailService email;
>
>     // Один-в-один: OrderPaidEvent → OrderShippedEvent
>     @EventListener
>     public OrderShippedEvent onOrderPaid(OrderPaidEvent event) {
>         shipping.arrangeShipping(event.orderId());
>         return new OrderShippedEvent(event.orderId(), LocalDate.now().plusDays(3));
>     }
>
>     // Один-ко-многим: вернётся List → каждый элемент опубликуется отдельно
>     @EventListener
>     public List<NotificationEvent> onOrderCreated(OrderCreatedEvent event) {
>         return List.of(
>             new EmailNotification(event.customerId()),
>             new PushNotification(event.customerId()),
>             new SmsNotification(event.customerId())
>         );
>     }
>
>     @EventListener
>     public void onOrderShipped(OrderShippedEvent event) {
>         email.sendShippingNotification(event);
>     }
>
>     // Async: вернуть CompletableFuture<E> — событие опубликуется когда future завершится
>     @Async
>     @EventListener
>     public CompletableFuture<OrderProcessedEvent> processAsync(OrderCreatedEvent e) {
>         return CompletableFuture.supplyAsync(() -> new OrderProcessedEvent(e.orderId()));
>     }
> }
> ```
>
> **Когда применять:**
> - Domain event chains: `OrderPaid → OrderShipped → ShipmentConfirmed`.
> - Fan-out: одно бизнес-событие порождает несколько нотификаций разных каналов.
> - State machine transitions через события (саги, workflows).
> - В Spring Modulith — упрощает межмодульную интеграцию.
>
> **Подводные камни:**
> - Возврат `null` означает «нового события нет» — не приводит к `NullPointerException`, но легко превращается в silent no-op, если разработчик хотел вернуть событие, а получил null.
> - Бесконечный цикл: listener A возвращает событие B, listener B возвращает событие A — StackOverflowError. Spring не защищает от этого.
> - Возврат `Object` или `?` приводит к публикации события неизвестного типа, что нарушает type safety; явно типизируйте.
> - В async-режиме порядок чейна неопределён — `CompletableFuture` может завершиться вне ожидаемой последовательности.
>
> **Связанные вопросы:** [[spring-events-interview#Q5]] — `@Async` с возвратом `CompletableFuture`; [[spring-events-interview#Q9]] — `condition` для условного chain-а; [[spring-events-interview#Q11]] — domain events и `AbstractAggregateRoot.registerEvent` как альтернатива chain-у.
>
> ---
>
> #### D) Return value автоматически сохраняется в `ThreadLocal`-кэше — следующий listener для того же события получит его через `EventContext.previousResult` — ❌ Неверно
>
> **Что на самом деле:** в Spring нет `EventContext.previousResult` и нет `ThreadLocal`-кэша результатов listener-ов. Listener-ы изолированы друг от друга; результат превращается в новое событие, а не в «контекст для следующего».
>
> **Откуда путаница:** напоминает паттерн pipes-and-filters или Spring Batch (где есть `ExecutionContext` с результатами шагов). Middle смешивает разные Spring-абстракции.
>
> **Если бы это было правдой:** listener-ы становились бы coupled через скрытый ThreadLocal — это плохо для тестирования (нужно эмулировать состояние ThreadLocal) и для async (ThreadLocal не переносится между потоками без явного inheritance).

## Q11. Что такое `@DomainEvents` в Spring Data?

Spring Data позволяет агрегатам самим хранить domain events и публиковать их при сохранении:

```java
public class Order extends AbstractAggregateRoot<Order> {
    private Long id;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order pay(PaymentInfo payment) {
        this.status = OrderStatus.PAID;
        registerEvent(new OrderPaidEvent(this.id, payment.getAmount()));
        // registerEvent — из AbstractAggregateRoot
        return this;
    }
}
```

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> { }
```

При вызове `orderRepository.save(order)` Spring Data автоматически:
1. Сохраняет агрегат
2. Публикует все накопленные events через `ApplicationEventPublisher`
3. Очищает список events (через `@AfterDomainEventPublication`)

**Без `AbstractAggregateRoot`** — можно сделать вручную через `@DomainEvents`:

```java
public class Order {
    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    @DomainEvents
    public List<Object> domainEvents() { return domainEvents; }

    @AfterDomainEventPublication
    public void clearEvents() { domainEvents.clear(); }
}
```


> [!mcq]
>
> **Вопрос:** Как `@DomainEvents` в Spring Data позволяет агрегатам публиковать события через `Repository.save()` и какие преимущества это даёт?
>
> ---
>
> #### A) Аннотация `@DomainEvents` на методе агрегата возвращает коллекцию событий; Spring Data при `repository.save(aggregate)` автоматически публикует их через `ApplicationEventPublisher`, а `@AfterDomainEventPublication` очищает список — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Data поддерживает паттерн domain events на уровне репозитория. На любом методе агрегата можно поставить `@DomainEvents` — он должен возвращать `Collection<?>` накопленных событий. На другом методе ставится `@AfterDomainEventPublication` — Spring Data вызовет его после публикации, чтобы агрегат мог очистить буфер событий. На практике обычно используют базовый класс `AbstractAggregateRoot<T>` из `spring-data-commons`: он уже реализует оба метода (`domainEvents()` + `clearDomainEvents()`) и предоставляет protected-метод `registerEvent(event)` для накопления. Преимущество: события *генерирует* сам агрегат (там, где известна доменная логика), а *публикует* инфраструктура (репозиторий). Domain layer не знает о Spring Events напрямую — single responsibility, чистая DDD-архитектура.
>
> **Пример:**
> ```java
> @Entity
> public class Order extends AbstractAggregateRoot<Order> {
>     @Id @GeneratedValue
>     private Long id;
>     private String customerId;
>     @Enumerated(EnumType.STRING)
>     private OrderStatus status;
>
>     public Order pay(PaymentInfo payment) {
>         this.status = OrderStatus.PAID;
>         registerEvent(new OrderPaidEvent(this.id, payment.amount()));
>         return this;
>     }
>
>     public Order ship(LocalDate eta) {
>         this.status = OrderStatus.SHIPPED;
>         registerEvent(new OrderShippedEvent(this.id, eta));
>         return this;
>     }
> }
>
> @Repository
> public interface OrderRepository extends JpaRepository<Order, Long> { }
>
> // Использование
> @Service
> @Transactional
> public class OrderService {
>     private final OrderRepository repo;
>
>     public Order pay(Long id, PaymentInfo payment) {
>         Order order = repo.findById(id).orElseThrow();
>         order.pay(payment); // registerEvent сохраняет OrderPaidEvent в буфер
>         return repo.save(order); // <-- здесь Spring Data публикует все накопленные события
>     }
> }
>
> // Альтернатива без AbstractAggregateRoot
> public class Invoice {
>     @Transient
>     private final List<Object> events = new ArrayList<>();
>
>     @DomainEvents
>     Collection<Object> domainEvents() { return List.copyOf(events); }
>
>     @AfterDomainEventPublication
>     void clearEvents() { events.clear(); }
> }
> ```
>
> **Когда применять:**
> - DDD-проекты с богатой доменной моделью (Order, Account, Reservation) — события рождаются в доменной логике.
> - Spring Modulith монолиты (Booking.com inspired): межмодульная коммуникация без зависимостей.
> - В сочетании с `@TransactionalEventListener(AFTER_COMMIT)` — outbox-семантика «out of box».
> - Audit log генерация из доменной активности (Capital One, Wolt, retail-проекты).
>
> **Подводные камни:**
> - События публикуются **только** при вызове `save()` через Spring Data Repository. Если сохранение идёт напрямую через `EntityManager.persist()` или `JdbcTemplate`, события **не** опубликуются.
> - `findAll` / `findById` события **не** публикуют — только `save()`/`saveAll()`.
> - `AbstractAggregateRoot.domainEvents` помечен `@Transient` — поле не персистится. Если забыли `@Transient` на собственной реализации — Hibernate попытается сохранить буфер событий, словив `MappingException`.
> - Несколько `save()` в одной транзакции — события публикуются на каждый save (могут продублироваться, если забыть `@AfterDomainEventPublication`).
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q6]] — `@TransactionalEventListener` для consumer-стороны domain events; [[spring-events-interview#Q12]] — `@AfterDomainEventPublication` для очистки буфера; [[spring-events-interview#Q13]] — Spring Modulith и Event Publication Registry поверх этого механизма.
>
> ---
>
> #### B) `@DomainEvents` — это аннотация для пометки бина как «обработчика всех domain-event-ов»; Spring Data автоматически зарегистрирует его как глобальный listener — ❌ Неверно
>
> **Что на самом деле:** `@DomainEvents` ставится на **метод агрегата** (entity), а не на consumer-бин. Возвращаемая коллекция — это **исходящие** события, не обработчики. Глобальный listener реализуется через `@EventListener` / `ApplicationListener`.
>
> **Откуда путаница:** название «DomainEvents» можно интерпретировать как «обработчик событий домена». Middle, привыкший к `@KafkaListener` (consumer), переносит модель потребления на producer-аннотацию.
>
> **Если бы это было правдой:** глобальный domain-events listener получал бы каждое событие в системе — потенциально миллионы в секунду — и без фильтрации это создавало бы memory-pressure и hot path для GC.
>
> ---
>
> #### C) Метод `@DomainEvents` должен возвращать `String`-имя события для маршрутизации в Kafka — ❌ Неверно
>
> **Что на самом деле:** `@DomainEvents` возвращает **коллекцию объектов-событий**, не String. Маршрутизация в Kafka — отдельная история (Spring Modulith Externalization через `@Externalized`); сама `@DomainEvents` не связана с Kafka.
>
> **Откуда путаница:** в Spring Cloud Stream есть `DESTINATION`-routing по строкам; middle смешивает Spring Data и Spring Cloud Stream.
>
> **Если бы это было правдой:** доменная логика не могла бы переносить структурированные данные (orderId, amount, customerId) — пришлось бы упаковывать в строку и парсить consumer-ом. Это потеря type safety, классическая «stringly-typed» проблема.
>
> ---
>
> #### D) `@DomainEvents` работает только в Kotlin — для Java нужен ручной `publisher.publishEvent()` после `save()` — ❌ Неверно
>
> **Что на самом деле:** `@DomainEvents` работает в любом JVM-языке (Java, Kotlin, Scala). Это часть Spring Data Commons и не имеет языковых ограничений.
>
> **Откуда путаница:** некоторые DDD-туториалы используют Kotlin для примеров (data classes удобны как value objects). Middle ошибочно ассоциирует фичу с языком.
>
> **Если бы это было правдой:** огромная Java-кодовая база Spring-приложений не могла бы пользоваться domain events — практически любая статья про DDD в Spring опровергает этот вариант.

## Q12. Зачем нужен `@AfterDomainEventPublication`?

`@AfterDomainEventPublication` — метод очистки domain events после их публикации:

```java
public class Order extends AbstractAggregateRoot<Order> {
    // AbstractAggregateRoot уже реализует @AfterDomainEventPublication
    // Но если вручную:

    @DomainEvents
    Collection<Object> domainEvents() { return List.copyOf(events); }

    @AfterDomainEventPublication
    void clearDomainEvents() { events.clear(); }
}
```

**Зачем важен:** без очистки один и тот же event будет публиковаться при каждом следующем `save()`. Это приводит к дублированию emails, созданию дублирующих записей и т.д.

`AbstractAggregateRoot` автоматически управляет этим — в большинстве случаев используйте его.


> [!mcq]
>
> **Вопрос:** Зачем нужна аннотация `@AfterDomainEventPublication` и что произойдёт, если её не реализовать?
>
> ---
>
> #### A) Это hook для логирования факта публикации события — без неё ничего не сломается, просто пропадёт audit-trail — ❌ Неверно
>
> **Что на самом деле:** `@AfterDomainEventPublication` — это **lifecycle callback** для очистки буфера событий агрегата. Без него (или эквивалентной логики) события **остаются** в буфере агрегата и при следующем `save()` публикуются повторно — это даёт duplicate events, не «потерянный audit».
>
> **Откуда путаница:** название содержит «After», что ассоциируется с post-event hook-ами (`@PostPersist`, listener-ы); middle переносит семантику observability на семантику lifecycle cleanup.
>
> **Если бы это было правдой:** разработчики безопасно пропускали бы реализацию метода — и сразу получали бы шторм duplicate-нотификаций (двойные email-ы, двойные Kafka-сообщения).
>
> ---
>
> #### B) Это аннотация для consumer-методов, обозначающая, что метод выполнится после публикации всех домен-событий — ❌ Неверно
>
> **Что на самом деле:** `@AfterDomainEventPublication` ставится на **методе агрегата** (producer-стороне), не на consumer. Это callback, который Spring Data вызывает после успешной публикации — для **очистки** локального буфера событий внутри агрегата.
>
> **Откуда путаница:** имя похоже на `@TransactionalEventListener(phase = AFTER_COMMIT)` — consumer-side аннотацию.
>
> **Если бы это было правдой:** consumer-методы получали бы дублирующие вызовы, ломая идемпотентность сторонних систем; а агрегат продолжал бы накапливать события вечно.
>
> ---
>
> #### C) Это аннотация для `@EventListener` — Spring сам очистит буфер событий — ❌ Неверно
>
> **Что на самом деле:** `@EventListener` не имеет связи с `@AfterDomainEventPublication`. Очистка буфера событий — это ответственность **агрегата** (producer), а не listener-а. Spring Data вызывает метод с этой аннотацией на ОБЪЕКТЕ агрегата сразу после публикации.
>
> **Откуда путаница:** обе аннотации связаны со словом «event», и middle, не отличающий producer/consumer семантику, может объединить их в одну ментальную модель.
>
> **Если бы это было правдой:** реализация была бы магически-неявной и не работала бы для случаев когда событие напрямую публикуется через `ApplicationEventPublisher` без `Repository.save()`. Контракт был бы непредсказуем.
>
> ---
>
> #### D) Очищает буфер событий агрегата (`events.clear()`) после их публикации; без этого один и тот же event опубликуется на каждом `repository.save()`, давая дубликаты — ✓ Верно
>
> **Развёрнутое объяснение:** агрегат накапливает события в локальном буфере (`List<Object> domainEvents`) — обычно через `AbstractAggregateRoot.registerEvent()`. Spring Data при `save()` сначала вызывает `@DomainEvents`-метод (получает список), публикует их через `ApplicationEventPublisher`, затем вызывает `@AfterDomainEventPublication`-метод. Если эта вторая фаза не реализована (или не очищает буфер), при следующем `save()` тот же агрегат снова отдаст накопленные события — duplicate publication. `AbstractAggregateRoot` реализует это автоматически (`clearDomainEvents()` уже там есть), поэтому большинство проектов просто наследуются от него.
>
> **Пример:**
> ```java
> // Корректный ручной вариант
> public class Order {
>     @Transient
>     private final List<Object> events = new ArrayList<>();
>
>     public void pay(BigDecimal amount) {
>         this.status = OrderStatus.PAID;
>         events.add(new OrderPaidEvent(this.id, amount));
>     }
>
>     @DomainEvents
>     Collection<Object> domainEvents() {
>         return List.copyOf(events);
>     }
>
>     @AfterDomainEventPublication
>     void clearDomainEvents() {
>         events.clear(); // <-- критично!
>     }
> }
>
> // Демонстрация бага без clearDomainEvents:
> // T1: order.pay(100); save() -> публикует OrderPaidEvent
> // T2: order.someUpdate(); save() -> снова публикует тот же OrderPaidEvent
> // → клиент получает 2 письма о платеже
>
> // Идиоматичный вариант через наследование
> @Entity
> public class Order extends AbstractAggregateRoot<Order> {
>     // AbstractAggregateRoot уже:
>     //   - имеет protected registerEvent(Object event)
>     //   - реализует @DomainEvents -> List.copyOf(events)
>     //   - реализует @AfterDomainEventPublication -> events.clear()
>     // Достаточно просто:
>     public void pay(PaymentInfo payment) {
>         this.status = OrderStatus.PAID;
>         registerEvent(new OrderPaidEvent(this.id, payment.amount()));
>     }
> }
> ```
>
> **Когда применять:**
> - При ручной реализации domain events без наследования `AbstractAggregateRoot` (например, value object, не Entity).
> - В микрофреймворке-обёртке для DDD-агрегатов: shared infrastructure для всех bounded contexts проекта.
> - В legacy-кодовой базе, где наследование уже занято другим базовым классом.
>
> **Подводные камни:**
> - Очистка происходит **в памяти** — если транзакция откатится после публикации, события всё равно очистятся (но не страшно: `@TransactionalEventListener(AFTER_COMMIT)` listener-ы их и не увидели бы).
> - Если возвращаете `events` напрямую (а не `List.copyOf(events)`), Spring Data передаст ту же ссылку — публикация и clear() в одном цикле могут пересечься. Всегда возвращайте immutable copy.
> - `events.clear()` нужно делать ДО или сразу после публикации, не до — иначе race в multi-threaded saves.
> - Если забыть `@Transient` на поле — Hibernate сериализует буфер событий и сохранит в БД, словив mapping error.
>
> **Связанные вопросы:** [[spring-events-interview#Q11]] — `@DomainEvents` как pair-аннотация для накопления событий; [[spring-events-interview#Q6]] — `@TransactionalEventListener` на consumer-стороне; [[spring-events-interview#Q13]] — Spring Modulith управляет очисткой иначе через Event Publication Registry.

## Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?

**Spring Modulith** — библиотека для модульных монолитов. Модули общаются через события, не имея прямых зависимостей:

```java
// Модуль Order публикует событие
@Service
@Transactional
public class OrderService {
    public Order complete(Long orderId) {
        Order order = repository.completeOrder(orderId);
        publisher.publishEvent(new OrderCompleted(orderId));
        return order;
    }
}

// Модуль Inventory обрабатывает событие — без зависимости на OrderService
@ApplicationModuleListener  // = @TransactionalEventListener + @Async + retries
public class InventoryModule {
    @ApplicationModuleListener
    void onOrderCompleted(OrderCompleted event) {
        inventory.releaseReservation(event.orderId());
    }
}
```

**Externalization** — публикация событий во внешние брокеры (Kafka, RabbitMQ):

```java
@Externalized("orders.completed")  // → Kafka topic
public record OrderCompleted(Long orderId) { }
```

Spring Modulith хранит невыполненные события в БД и гарантирует доставку (outbox pattern из коробки).


> [!mcq]
>
> **Вопрос:** Что такое Spring Modulith и какую роль играют события в межмодульном взаимодействии?
>
> ---
>
> #### A) Spring Modulith — это форк Spring Boot для микросервисов; каждый модуль — отдельный процесс, события доставляются автоматически через REST — ❌ Неверно
>
> **Что на самом деле:** Spring Modulith — это **дополнение** к Spring Boot, а не форк. Цель — модульный **монолит** (один процесс, несколько слабо связанных модулей внутри одного `ApplicationContext`). Никакой автоматической REST-доставки нет — общение между модулями идёт через in-memory Spring Events. Для exposure наружу есть отдельный механизм Event Externalization (Kafka, RabbitMQ, JMS, AWS SNS).
>
> **Откуда путаница:** Modulith иногда называют «modular monolith pattern» как противоположность микросервисам; middle делает обратный вывод — что это microservices framework.
>
> **Если бы это было правдой:** при каждом доменном событии происходил бы сетевой round-trip, и латенси простой операции вырастала бы в 10-100 раз. Spring Modulith таковым не является — он именно про in-memory.
>
> ---
>
> #### B) Spring Modulith — фреймворк для модульного монолита: модули общаются через Spring Events (`@ApplicationModuleListener`), границы проверяются ArchTest-ом, а Event Publication Registry реализует transactional outbox для гарантий доставки и retry — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Modulith (релизнут VMware в 2022 на базе работ Oliver Drotbohm) — расширение Spring Boot для строго-модульной архитектуры внутри одного процесса. Ключевые компоненты: (1) **Модули** — Java-пакеты верхнего уровня (`com.app.order`, `com.app.inventory`), где `internal/` подпакет инкапсулирован и не может быть импортирован другими модулями; (2) `@ApplicationModuleListener` — комбо-аннотация (`@TransactionalEventListener` + `@Async` + `@Transactional(REQUIRES_NEW)`) для безопасной обработки межмодульных событий; (3) **Event Publication Registry** — таблица в БД, куда записываются все опубликованные `@TransactionalEventListener`-события до commit-а — это transactional outbox **из коробки**, гарантирует at-least-once delivery даже при крэше; (4) **Event Externalization** через `@Externalized` для публикации в Kafka/RabbitMQ; (5) `ApplicationModules.verify()` — ArchUnit-like проверка структуры в тесте, падает CI при нарушении границ модулей; (6) Auto-generated docs (PlantUML диаграммы модулей).
>
> **Пример:**
> ```java
> // Модуль Order публикует событие
> @Service
> @Transactional
> public class OrderService {
>     private final ApplicationEventPublisher publisher;
>
>     public Order complete(Long orderId) {
>         Order order = orders.completeOrder(orderId);
>         publisher.publishEvent(new OrderCompleted(orderId));
>         return order;
>     }
> }
>
> // Модуль Inventory обрабатывает — без зависимости на OrderService class
> package com.app.inventory;
>
> @Component
> class InventoryEventHandler {
>     @ApplicationModuleListener // = @Async + @TransactionalEventListener + @Transactional(REQUIRES_NEW)
>     void onOrderCompleted(OrderCompleted event) {
>         inventory.releaseReservation(event.orderId());
>     }
> }
>
> // Event Externalization в Kafka
> @Externalized("orders.completed::#{#this.orderId}")
> public record OrderCompleted(Long orderId) { }
>
> // Проверка границ модулей в тесте
> class ModularityTest {
>     @Test
>     void verifyModularity() {
>         ApplicationModules.of(MyApplication.class).verify();
>         // Падает при импорте internal-пакета из другого модуля
>     }
>
>     @Test
>     void writeDocumentation() {
>         var modules = ApplicationModules.of(MyApplication.class);
>         new Documenter(modules)
>             .writeDocumentation()  // генерирует C4-диаграммы и AsciiDoc
>             .writeIndividualModulesAsPlantUml();
>     }
> }
>
> // Event Publication Registry таблица в БД
> // event_publication(id, listener_id, event_type, serialized_event, publication_date, completion_date)
> ```
>
> **Когда применять:**
> - Команды, которые хотят преимущества DDD и слабого связывания, но не готовы платить операционную цену микросервисов (Booking.com, Wolt, многие fintech).
> - Стартапы — начать как modular monolith, при росте извлечь отдельный модуль в сервис (выход через `@Externalized` уже встроен).
> - Replacement для `spring-data-jpa @DomainEvents` + ручной outbox-таблицы — Modulith даёт это «из коробки».
>
> **Подводные камни:**
> - Event Publication Registry таблица требует миграции — Flyway/Liquibase должны её создать (`spring-modulith-events-jpa` подкидывает default DDL).
> - При большом потоке событий таблица растёт — нужен periodic cleanup completed events (`spring.modulith.events.completion-mode=delete` или archive).
> - `@ApplicationModuleListener` запускает new transaction — events не видят uncommitted changes публикатора (это и есть outbox-семантика).
> - `ApplicationModules.verify()` в CI — обязательная защита границ, без неё команды быстро ломают модульность.
>
> **Связанные вопросы:** [[spring-events-interview#Q6]] — `@TransactionalEventListener` (база Modulith listener-ов); [[spring-events-interview#Q11]] — `@DomainEvents` как способ публикации; [[spring-events-interview#Q16]] — отличия от Kafka (Modulith внутри JVM, externalization наружу).
>
> ---
>
> #### C) Spring Modulith использует RabbitMQ внутри процесса для in-memory routing между модулями — ❌ Неверно
>
> **Что на самом деле:** in-memory события работают через стандартный `ApplicationEventPublisher` Spring Framework — никакого embedded RabbitMQ нет. RabbitMQ (и Kafka, JMS, SNS) могут использоваться только на этапе **externalization** через `@Externalized`, когда событие нужно вывести за пределы JVM.
>
> **Откуда путаница:** middle, видевший Spring Cloud Stream + RabbitMQ binder, путает с Modulith.
>
> **Если бы это было правдой:** запуск Spring Modulith требовал бы поднятия RabbitMQ-брокера, что противоречило бы идее «лёгкого modular monolith». Application context стартовал бы с broker dependency.
>
> ---
>
> #### D) Spring Modulith — это библиотека для миграции с Spring Events на Kafka: все `@EventListener` автоматически становятся `@KafkaListener` — ❌ Неверно
>
> **Что на самом деле:** Spring Modulith не выполняет автоматической миграции. `@EventListener` остаётся `@EventListener`; Kafka-доставка работает только для events, помеченных `@Externalized`, и то в одну сторону (publisher-side).
>
> **Откуда путаница:** маркетинг Modulith упоминает Kafka externalization как ключевую фичу; middle делает излишний обобщающий вывод.
>
> **Если бы это было правдой:** простой добавление зависимости в build.gradle ломало бы все локальные in-memory listener-ы — каждый запуск приложения требовал бы Kafka.

## Q14. Как тестировать публикацию и обработку Spring Events?

**`ApplicationEvents` в тестах (Spring Boot 2.7+):**

```java
@SpringBootTest
@RecordApplicationEvents  // включить запись событий
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired ApplicationEvents events;

    @Test
    void createOrder_shouldPublishOrderCreatedEvent() {
        orderService.createOrder(new OrderRequest("CUST-1"));

        assertThat(events.stream(OrderCreatedEvent.class)).hasSize(1);
        OrderCreatedEvent event = events.stream(OrderCreatedEvent.class)
            .findFirst().orElseThrow();
        assertThat(event.getCustomerId()).isEqualTo("CUST-1");
    }
}
```

**Через `@SpyBean` на слушателе:**

```java
@SpringBootTest
class EventHandlingTest {

    @SpyBean NotificationListener notificationListener;
    @Autowired ApplicationEventPublisher publisher;

    @Test
    void shouldCallNotificationOnOrderCreated() {
        publisher.publishEvent(new OrderCreatedEvent(1L, "CUST-1"));
        verify(notificationListener, times(1)).onOrderCreated(any());
    }
}
```

**Через `MockEventListener`:**

```java
@TestComponent
public class TestOrderCreatedListener {
    private final List<OrderCreatedEvent> receivedEvents = new ArrayList<>();

    @EventListener
    public void capture(OrderCreatedEvent event) {
        receivedEvents.add(event);
    }

    public List<OrderCreatedEvent> getEvents() { return receivedEvents; }
}
```


> [!mcq]
>
> **Вопрос:** Какой идиоматичный способ проверить, что бизнес-метод опубликовал ожидаемое событие, в Spring Boot 2.7+ / 3.x?
>
> ---
>
> #### A) Через `Mockito.mockStatic(ApplicationEventPublisher.class)` — это статический mock, нужен JUnit-расширение `MockitoStaticExtension` — ❌ Неверно
>
> **Что на самом деле:** `ApplicationEventPublisher` — обычный интерфейс, а не утилитный класс со static-методами; `mockStatic` к нему неприменим. Идиоматичная проверка — либо `@MockBean ApplicationEventPublisher` (если тест на сервис, инжектируя mock) либо `@RecordApplicationEvents` + `ApplicationEvents` бин (для full Spring Boot test).
>
> **Откуда путаница:** Mockito 3.4+ действительно умеет `mockStatic`, и middle, видевший статические методы в Spring `SpringApplication` / `BeanUtils`, ошибочно обобщает.
>
> **Если бы это было правдой:** тесты выглядели бы громоздко и переплетались бы с JVM ByteBuddy-агентом для статики, что усложняет CI-настройку. На практике достаточно простого `@MockBean`.
>
> ---
>
> #### B) Подсчитать события через `LogCaptor` / `ListAppender` Logback — проверить, что в логе появилась строка `Publishing event: ...` — ❌ Неверно
>
> **Что на самом деле:** Spring **не логирует** публикации событий по умолчанию (только DEBUG-уровень). Полагаться на логи — fragile (текст логов меняется между версиями Spring), медленно (приходится включать DEBUG), и тестирует side-effect, а не контракт.
>
> **Откуда путаница:** в проектах без structured event-tracking разработчики действительно проверяют логи. Но это «code smell»-практика для тестов событий.
>
> **Если бы это было правдой:** обновление Spring до новой минорной версии могло бы менять формат лога и валить тесты, не имеющие к новому коду отношения.
>
> ---
>
> #### C) `@SpringBootTest + @RecordApplicationEvents` плюс injected `ApplicationEvents` — `events.stream(MyEvent.class).hasSize(N)` показывает что событие опубликовано; для проверки listener-а — `@SpyBean` на бине-listener-е и `verify(listener).onEvent(any())` — ✓ Верно
>
> **Развёрнутое объяснение:** в Spring Boot 2.7+ доступна аннотация `@RecordApplicationEvents`, которая включает `ApplicationEventsTestExecutionListener`. Он перехватывает все события, опубликованные в контексте теста, и накапливает их в `ApplicationEvents`-бине. В тесте можно инжектировать этот бин и проверять stream-API: фильтровать по типу, считать количество, доставать конкретный экземпляр для assertions на полях. Это лёгкий способ верифицировать **producer-сторону** без mock-инфраструктуры. Для **consumer-стороны** обычно используют `@SpyBean` на бине-listener-е (`verify(listener, times(1)).onOrderCreated(any())`) или тестовый bean с captureom событий. Для async-listener-ов — `Awaitility` для ожидания, что listener реально вызвался.
>
> **Пример:**
> ```java
> @SpringBootTest
> @RecordApplicationEvents
> class OrderServiceTest {
>     @Autowired OrderService orderService;
>     @Autowired ApplicationEvents events;
>
>     @Test
>     void shouldPublishOrderCreatedOnSave() {
>         orderService.createOrder(new OrderRequest("CUST-1", BigDecimal.valueOf(100)));
>
>         assertThat(events.stream(OrderCreatedEvent.class)).hasSize(1);
>         OrderCreatedEvent event = events.stream(OrderCreatedEvent.class)
>             .findFirst().orElseThrow();
>         assertThat(event.customerId()).isEqualTo("CUST-1");
>     }
> }
>
> // Тест consumer-стороны через @SpyBean
> @SpringBootTest
> class EventHandlingTest {
>     @SpyBean NotificationListener notificationListener;
>     @Autowired ApplicationEventPublisher publisher;
>
>     @Test
>     void shouldCallListenerOnOrderCreated() {
>         publisher.publishEvent(new OrderCreatedEvent(1L, "CUST-1", BigDecimal.TEN, Instant.now()));
>         verify(notificationListener, times(1)).onOrderCreated(any());
>     }
> }
>
> // Async listener — Awaitility
> @SpringBootTest
> class AsyncListenerTest {
>     @Autowired ApplicationEventPublisher publisher;
>     @SpyBean NotificationListener listener;
>
>     @Test
>     void shouldEventuallyCallAsyncListener() {
>         publisher.publishEvent(new OrderCreatedEvent(1L, "CUST", BigDecimal.TEN, Instant.now()));
>         await().atMost(Duration.ofSeconds(5))
>                .untilAsserted(() -> verify(listener).onOrderCreated(any()));
>     }
> }
>
> // Unit-тест (без Spring) с обычным Mockito
> @Test
> void unitTest_serviceShouldPublishEvent() {
>     ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
>     OrderRepository repo = mock(OrderRepository.class);
>     when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
>
>     OrderService service = new OrderService(publisher, repo);
>     service.createOrder(new OrderRequest("CUST", BigDecimal.TEN));
>
>     verify(publisher).publishEvent(any(OrderCreatedEvent.class));
> }
> ```
>
> **Когда применять:**
> - Unit-тесты сервисов — `mock(ApplicationEventPublisher.class)` + `verify(...).publishEvent(...)`. Быстро, без Spring.
> - Integration-тесты с `@SpringBootTest` — `@RecordApplicationEvents` + `ApplicationEvents`. Проверка реального flow от Service до Listener.
> - Async-тесты — `Awaitility.await().untilAsserted(...)` для надёжной проверки eventual delivery.
> - Spring Modulith тесты — `PublishedEvents` API из `spring-modulith-test`, аналог `ApplicationEvents`, но с Modulith-семантикой.
>
> **Подводные камни:**
> - `@RecordApplicationEvents` записывает события только пока контекст активен в тесте — события после `@AfterEach` не попадают.
> - `@SpyBean` сбрасывается между тестами автоматически только если используется `@DirtiesContext` или Spring Boot 3.0+ default; иначе делайте `reset(listener)` явно.
> - Для `@TransactionalEventListener(AFTER_COMMIT)` тест должен запускаться в реальной транзакции — внутри `@Transactional` теста listener не вызовется (тест откатывает транзакцию). Используйте `@Commit` или programmatic transaction.
> - Async-листенеры в синхронных тестах могут «случайно» завершиться к моменту assert-а, но это flaky — всегда используйте `Awaitility`.
>
> **Связанные вопросы:** [[spring-events-interview#Q2]] — публикация через `ApplicationEventPublisher`; [[spring-events-interview#Q5]] — async listener-ы (`Awaitility`); [[spring-events-interview#Q6]] — особенности тестирования `@TransactionalEventListener`.
>
> ---
>
> #### D) Использовать `EventLogger` из `spring-test` — он автоматически логирует все события в файл `target/spring-events.log` — ❌ Неверно
>
> **Что на самом деле:** класса `EventLogger` в `spring-test` **нет**. Записи событий в `target/spring-events.log` тоже нет.
>
> **Откуда путаница:** middle мог слышать о `spring-test`, `LogAppender`, `OutputCaptureExtension` (она реально существует, но для stdout-капчуринга) и склеить их в несуществующий API.
>
> **Если бы это было правдой:** тесты зависели бы от файловой системы (запись в файл, чтение в assert), что неуниверсально (Windows/Linux path-разделители, permissions в CI) и медленно.

## Q15. Когда использовать Spring Events, а когда прямой вызов метода?

| Критерий | Прямой вызов | Spring Events |
|---|---|---|
| Coupling | Тесная связь | Слабая связь |
| Несколько получателей | Нужно вызывать все | Один publish → все слушатели |
| Транзакционность | Контролируемая | Через `@TransactionalEventListener` |
| Тестируемость | Мокируем зависимость | Мокируем publisher / spy listener |
| Понятность | Очевидный flow | Неявный flow (нужно искать слушателей) |
| Side effects | Смешиваются с логикой | Вынесены из публикатора |

**Использовать Events когда:**
- Несколько компонентов реагируют на одно действие
- Хотим отвязать модули (DDD domain events)
- Side effect не должен выполняться при rollback (`@TransactionalEventListener`)
- Side effect можно выполнить позже (async)

**Прямой вызов когда:**
- Одна зависимость, простая логика
- Нужен результат от "получателя"
- Flow должен быть очевиден при чтении кода


> [!mcq]
>
> **Вопрос:** В каком из случаев Spring Events предпочтительнее прямого вызова метода, а в каком — наоборот?
>
> ---
>
> #### A) События — когда несколько компонентов реагируют на одно действие, нужно отвязать модули или сайд-эффект не должен выполняться при rollback. Прямой вызов — когда логика всего одна, очевидна при чтении, нужен возвращаемый результат от получателя — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Events — инструмент **снижения coupling-а**. Они окупаются когда есть несколько reaction-ов на одно действие (email + audit + metric), когда нужно отвязать модули в DDD/Spring Modulith стиле, или когда side-effect должен корректно вести себя с транзакцией (`@TransactionalEventListener(AFTER_COMMIT)` гарантирует выполнение только при commit). Цена событий — неявность flow: чтобы понять «что произойдёт после `publishEvent`» нужно искать listener-ы по проекту. Поэтому для простой однозвенной логики (`service.A() → service.B().doX()`) события избыточны — direct call яснее, типобезопаснее (возвращаемое значение, исключения долетают до caller-а) и проще для refactoring (IDE rename работает по типам).
>
> **Пример:**
> ```java
> // ✓ ХОРОШО: события — много реакций на одно действие
> @Transactional
> public Order createOrder(OrderRequest req) {
>     Order order = orders.save(new Order(req));
>     publisher.publishEvent(new OrderCreatedEvent(order.getId()));
>     return order;
> }
> // Реакции в разных модулях:
> //   - EmailListener.onOrderCreated → email клиенту
> //   - InventoryListener.onOrderCreated → резерв склада
> //   - MetricsListener.onOrderCreated → counter в Prometheus
> //   - AuditListener.onOrderCreated → запись в audit_log
> // Добавить новую реакцию = добавить новый @EventListener, без правки OrderService
>
> // ✗ ПЛОХО: события для одной реакции
> @Transactional
> public Customer registerCustomer(CustomerRequest req) {
>     Customer c = customers.save(new Customer(req));
>     publisher.publishEvent(new CustomerRegisteredEvent(c.getId()));
>     return c;
> }
> // Если есть только ONE listener (sendWelcomeEmail) — лучше прямой вызов:
> @Transactional
> public Customer registerCustomer(CustomerRequest req) {
>     Customer c = customers.save(new Customer(req));
>     emailService.sendWelcomeEmail(c.getId());
>     return c;
> }
> ```
>
> Сравнительная таблица:
> | Критерий | Прямой вызов | Spring Events |
> |---|---|---|
> | Coupling | Сильная связь | Слабая связь |
> | Несколько получателей | Вызывать все вручную | Один publish → все listener-ы |
> | Транзакционность | Контролируема прямо | Через `@TransactionalEventListener` |
> | Тестирование | Mock зависимости | Mock publisher / @RecordApplicationEvents |
> | Понятность flow | Очевидно при чтении | Неявно — искать listener-ы |
> | Refactoring | IDE rename ловит call sites | Event class rename + текстовый поиск |
> | Side effect при rollback | Выполнится если до exception | Не выполнится с `AFTER_COMMIT` |
> | Возвращаемое значение | Доступно caller-у | Через chained events (хрупко) |
>
> **Когда применять:**
> - События: domain events в DDD, межмодульная коммуникация в Spring Modulith (Booking.com, Wolt), audit/notification как cross-cutting concern.
> - Прямой вызов: тестируемый core flow (валидация → персистентность), реализация interface с одной реализацией, случаи где нужен результат от получателя.
>
> **Подводные камни:**
> - Слишком много событий = «event soup»: невозможно понять, что произойдёт после publish без grep-а. Договоритесь о правиле «событие = доменный факт», не technical wrapper.
> - События вокруг lifecycle (`OrderCreatedEvent`, `OrderShippedEvent`) — полезные доменные понятия. События «callMethodX» — анти-паттерн, маскирующий direct call.
> - Не публикуйте mutable объекты: race между listener-ами.
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — что такое Spring Events и зачем; [[spring-events-interview#Q6]] — `@TransactionalEventListener` для side-effects, привязанных к commit-у; [[spring-events-interview#Q16]] — когда даже Spring Events недостаточно и нужен Kafka.
>
> ---
>
> #### B) События — всегда (loose coupling в любой ситуации лучше); прямой вызов — никогда (нарушает SOLID) — ❌ Неверно
>
> **Что на самом деле:** loose coupling — не самоцель. Цена за него — неявность flow и потеря type-safety. Для простой логики прямой вызов **лучше**: код читается линейно, refactoring работает, исключения долетают до caller-а.
>
> **Откуда путаница:** упрощённое чтение SOLID: «depend on abstractions» → «всё через интерфейсы/события». На практике dependency-on-abstractions ≠ dependency-on-events.
>
> **Если бы это было правдой:** код деградировал бы в «event soup» — десятки событий «callValidate», «callSave», «callReturn». Понять flow стало бы невозможно, а отладка превращалась бы в марафон по `@EventListener`-методам.
>
> ---
>
> #### C) События — когда нужна гарантия доставки и retry; прямой вызов — для best-effort вызовов — ❌ Неверно
>
> **Что на самом деле:** Spring Events **НЕ дают** гарантии доставки. По умолчанию они in-memory и теряются при крэше процесса. Гарантия доставки достигается через Spring Modulith Event Publication Registry (это надстройка) или через Kafka/RabbitMQ (это уже другая категория). Прямой вызов **более** надёжен в смысле «вызов точно произойдёт»: при ошибке исключение долетает до caller-а.
>
> **Откуда путаница:** ассоциация «event-driven = reliable messaging» из Kafka/RabbitMQ-мира. Middle переносит её на in-process events.
>
> **Если бы это было правдой:** разработчики использовали бы Spring Events для критичных сценариев (оплата, нотификации) — и теряли бы события при крэше, объясняя в post-mortem-ах «но это же reliable event system, как такое возможно?».
>
> ---
>
> #### D) События — для performance-критичных мест (быстрее метод-вызова через AOP); прямой вызов — для CPU-bound операций — ❌ Неверно
>
> **Что на самом деле:** события **медленнее** прямого вызова. Каждый publish — это поиск listener-ов в `ApplicationListenerMethodAdapter`, SpEL-resolution для condition, AOP-прокси, потенциально сериализация в outbox. Прямой method call оптимизируется JIT-ом до inline-инструкции — миллисекунды vs наносекунды.
>
> **Откуда путаница:** недоразумение между «async» и «fast». Async может ускорить throughput за счёт распараллеливания, но добавляет overhead на dispatch.
>
> **Если бы это было правдой:** все high-frequency trading системы строили бы на event-bus. На практике hot path в Java всегда minimum-allocation, minimum-indirection — прямой вызов.

## Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?

| Параметр | Spring Events | Kafka/RabbitMQ |
|---|---|---|
| Scope | Внутри JVM-процесса | Между процессами/сервисами |
| Персистентность | Нет (in-memory) | Да |
| Гарантия доставки | При ошибке — потеряно | At-least-once / exactly-once |
| Replay | Нет | Да (Kafka) |
| Масштабирование | Не нужно (один JVM) | Горизонтальное |
| Latency | Микросекунды | Миллисекунды |
| Setup | Zero config | Внешняя инфраструктура |

**Spring Events + Spring Modulith Outbox** — bridge между двумя мирами: события хранятся в БД (гарантия), потом публикуются в Kafka/RabbitMQ.

**Типичная архитектура:**
```
Бизнес-логика → Spring Event → @TransactionalEventListener → publish to Kafka
                                                             (в отдельной транзакции)
```

> [!mcq]
>
> **Вопрос:** Чем Spring Events принципиально отличаются от сообщений Kafka/RabbitMQ и когда нужен переход с одного на другое?
>
> ---
>
> #### A) Это разные имена одного механизма; Spring Cloud Stream скрывает разницу: и Spring Events, и Kafka работают через `@StreamListener` — ❌ Неверно
>
> **Что на самом деле:** Spring Events и Kafka — фундаментально разные механизмы. Spring Events — in-process, in-memory; Kafka — distributed, persistent, network-based. `@StreamListener` (устар.) и `@KafkaListener` относятся к Kafka, `@EventListener` — к Spring Events. Spring Cloud Stream — abstraction поверх брокеров (Kafka, RabbitMQ), но не объединяет их с in-memory событиями.
>
> **Откуда путаница:** маркетинг «event-driven architecture» использует слово «event» одинаково для in-process и distributed-кейсов. Middle, не разбирающийся в layers, путает их.
>
> **Если бы это было правдой:** код в одной системе одинаково работал бы локально и через Kafka — но при попытке деплоя стало бы ясно, что нужны разные конфигурации, разные гарантии доставки, разные тестовые подходы.
>
> ---
>
> #### B) Spring Events персистентны и поддерживают replay из коробки — Kafka просто более масштабируемая версия — ❌ Неверно
>
> **Что на самом деле:** Spring Events **НЕ персистентны** по умолчанию (только in-memory) и **НЕ поддерживают replay**. Это базовое отличие: Kafka хранит сообщения днями/неделями и позволяет consumer-у читать с любой offset; Spring Events отрабатывают синхронно и забываются.
>
> **Откуда путаница:** в Spring Modulith есть Event Publication Registry, который добавляет персистентность поверх Spring Events. Middle обобщает это до «всех Spring Events».
>
> **Если бы это было правдой:** Kafka не возник бы как отдельная технология — компании использовали бы Spring Events для inter-service. На практике для cross-service нужна persistent log, и Kafka появился именно для этого (LinkedIn 2011).
>
> ---
>
> #### C) Spring Events работают только в рамках одного класса; Kafka — между классами — ❌ Неверно
>
> **Что на самом деле:** Spring Events работают между **любыми бинами** в `ApplicationContext` — публикатор и подписчик могут быть в разных пакетах, разных модулях. Ограничение — JVM-процесс, не класс.
>
> **Откуда путаница:** Middle путает Java AWT events (которые часто внутри одного компонента) или внутренний Observer-паттерн с Spring Events.
>
> **Если бы это было правдой:** Spring Events были бы бесполезны для cross-cutting concerns типа audit/email/metrics. На практике именно для этого они и применяются.
>
> ---
>
> #### D) Spring Events — in-JVM, in-memory, без persistence; Kafka — distributed, persistent, at-least-once delivery, replay-able. Переход нужен когда события пересекают границы процессов или нужны guarantees доставки и replay — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Events живут внутри одного JVM-процесса: publisher и listener делят `ApplicationContext`, сообщения не сериализуются, не покидают память. Это даёт микросекундную латенси и нулевой operational overhead, но проигрывает по persistence (всё теряется при crash), горизонтальному масштабированию (нельзя обработать событие на другой ноде), межсервисной интеграции и replay. Kafka/RabbitMQ решают именно эти задачи: persistent storage, broker-managed delivery, partitioning для параллелизма, consumer groups, offset-based replay. Bridge между двумя мирами — паттерн **Transactional Outbox**: Spring Modulith Event Publication Registry или собственная таблица в БД записывает события атомарно с бизнес-данными, отдельный publisher вычитывает таблицу и шлёт в Kafka. Это даёт надёжность Kafka с простотой Spring Events.
>
> **Пример:**
> ```java
> // 1. Spring Events: внутри одного процесса
> @Service
> public class OrderService {
>     private final ApplicationEventPublisher publisher;
>
>     @Transactional
>     public Order create(OrderRequest req) {
>         Order order = orders.save(new Order(req));
>         publisher.publishEvent(new OrderCreatedEvent(order.getId()));
>         return order;
>     }
> }
>
> // 2. Bridge через Spring Modulith Externalization → Kafka
> @Externalized("orders.events::#{#this.orderId}")
> public record OrderCreatedEvent(Long orderId, String customerId) { }
> // Modulith записывает в event_publication, потом публикует в Kafka topic "orders.events"
>
> // 3. Kafka consumer в другом сервисе
> @Component
> public class OrderEventsConsumer {
>     @KafkaListener(topics = "orders.events", groupId = "inventory-service")
>     public void onOrderCreated(OrderCreatedEvent event) {
>         inventory.releaseReservation(event.orderId());
>     }
> }
> ```
>
> Развёрнутая сравнительная таблица:
> | Параметр | Spring Events | Kafka/RabbitMQ |
> |---|---|---|
> | Scope | Внутри JVM | Между процессами/датацентрами |
> | Persistence | Нет (in-memory) | Да (disk-backed) |
> | Гарантия доставки | При ошибке — потеря | At-least-once / exactly-once |
> | Replay | Нет | Kafka — да; RabbitMQ — частично |
> | Масштабирование | В рамках одной JVM | Горизонтальное (partitions) |
> | Latency | Микросекунды | 1-10 ms (RTT + serialization) |
> | Setup | Zero-config | Cluster: ZooKeeper/KRaft, brokers, topics |
> | Backpressure | Нет (`@Async` queue) | Yes (lag-based) |
> | Schema evolution | Java type | Avro/Protobuf + Schema Registry |
> | Multi-tenancy | Один process | Topics/queues/vhosts |
>
> **Когда применять:**
> - **Spring Events** — domain events внутри модулярного монолита, event-driven архитектура в рамках одного приложения, ApplicationContext lifecycle events.
> - **Kafka** — межсервисная интеграция (Booking.com, Uber, Yandex), event sourcing с replay, аналитические pipelines (CDC через Debezium).
> - **RabbitMQ** — task queues, RPC-стиль, гибкая маршрутизация по exchange-ам.
> - **Outbox bridge** — Spring Modulith → Kafka: всегда когда нужны гарантии доставки наружу JVM без потери транзакционности.
>
> **Подводные камни:**
> - Не используйте Spring Events для критичных межсервисных операций — события теряются при крэше JVM.
> - Не используйте Kafka для in-process pub/sub — overhead network call неоправдан.
> - Outbox без cleanup растёт неограниченно — настройте periodic delete completed events.
> - Spring Modulith Externalization работает только publisher-side; consumer на Kafka — обычный `@KafkaListener`.
> - Kafka не гарантирует exactly-once без идемпотентности на consumer-стороне и transactional producer.
>
> **Связанные вопросы:** [[spring-events-interview#Q1]] — суть Spring Events; [[spring-events-interview#Q13]] — Spring Modulith и Event Publication Registry; [[spring-events-interview#Q15]] — когда вообще нужны events vs прямой вызов.

---

## See also

- [Spring Framework](spring-framework-interview.md) — ApplicationContext, BeanFactory, жизненный цикл бинов
- [Spring Boot](spring-boot-interview.md) — ApplicationReadyEvent, ApplicationStartedEvent для startup логики
- [Spring AOP](spring-aop-interview.md) — события реализованы без AOP, но часто используются вместе
- [Spring Data JPA](spring-data-jpa-interview.md) — @DomainEvents, AbstractAggregateRoot, @AfterDomainEventPublication
- [Spring Scheduling](spring-scheduling-interview.md) — @Scheduled vs события для периодических задач
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — общие паттерны event-driven архитектуры
- [Apache Kafka](../../messaging/kafka-interview.md) — для межсервисных событий с гарантией доставки
- [Domain-Driven Design](../../architecture/ddd-interview.md) — domain events, aggregates, bounded context
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — transactional outbox, at-least-once delivery
- [Unit Testing](../../testing/unit-testing-interview.md) — @RecordApplicationEvents, ApplicationEvents, тестирование listeners
- [Шпаргалка: Spring Events — события приложения](../../../frameworks/java-frameworks/spring/spring-events.md) — теория
