---
title: "Вопросы на собеседовании: События Spring"
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
# Вопросы на собеседовании: События Spring

`Spring Events` — механизм слабого связывания компонентов через события: публикатор не знает о подписчиках. По умолчанию слушатели вызываются синхронно; они участвуют в транзакционном контексте публикатора, только если событие опубликовано внутри транзакции. `@TransactionalEventListener` — ключевой инструмент для domain events в DDD-архитектуре.

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
- [Q1. Что такое Spring Events и зачем нужны?](#q1-что-такое-spring-events-и-зачем-нужны)
- [Q2. Как опубликовать и обработать событие?](#q2-как-опубликовать-и-обработать-событие)
- [Q3. Как создать кастомное событие?](#q3-как-создать-кастомное-событие)
- [Q4. Какие встроенные Spring-события (context events) существуют?](#q4-какие-встроенные-spring-события-context-events-существуют)

**Продвинутые возможности**
- [Q5. Как сделать обработку события асинхронной?](#q5-как-сделать-обработку-события-асинхронной)
- [Q6. Что такое `@TransactionalEventListener` и зачем он нужен?](#q6-что-такое-transactionaleventlistener-и-зачем-он-нужен)
- [Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?](#q7-какие-фазы-транзакции-поддерживает-transactionaleventlistener)
- [Q8. Как управлять порядком обработчиков одного события?](#q8-как-управлять-порядком-обработчиков-одного-события)
- [Q9. Как условно обработать событие?](#q9-как-условно-обработать-событие)
- [Q10. Может ли `@EventListener` возвращать значение?](#q10-может-ли-eventlistener-возвращать-значение)

**DDD и интеграция**
- [Q11. Что такое `@DomainEvents` в Spring Data?](#q11-что-такое-domainevents-в-spring-data)
- [Q12. Зачем нужен `@AfterDomainEventPublication`?](#q12-зачем-нужен-afterdomaineventpublication)
- [Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?](#q13-что-такое-spring-modulith-и-как-события-используются-для-межмодульного-взаимодействия)

**Тестирование**
- [Q14. Как тестировать публикацию и обработку Spring Events?](#q14-как-тестировать-публикацию-и-обработку-spring-events)

**Архитектурные вопросы**
- [Q15. Когда использовать Spring Events, а когда прямой вызов метода?](#q15-когда-использовать-spring-events-а-когда-прямой-вызов-метода)
- [Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?](#q16-в-чём-разница-между-spring-events-и-kafkarabbitmq-сообщениями)

---

## Q1. Что такое Spring Events и зачем нужны?

**Spring Events** — встроенный в Spring-контекст механизм событий, реализующий паттерн Observer (Publisher-Subscriber). Один компонент публикует событие, другие на него реагируют — при этом публикатор ничего не знает о подписчиках. Это и есть основная ценность: **слабое связывание** внутри одного JVM-процесса.

Идея в одной картинке: один вызов `publishEvent()` веером расходится по всем заинтересованным слушателям.

```
OrderService.save() → publishEvent(OrderCreatedEvent)
                              ↓
        ┌─────────────────────────────────┐
        ↓                                 ↓
EmailListener.onOrderCreated()    InventoryListener.onOrderCreated()
```

`OrderService` не вызывает `EmailService` напрямую и даже не подозревает о его существовании — он просто сообщает «заказ создан». Добавить нового подписчика можно, не трогая код публикатора.

**Зачем нужны:**
- **Развязать модули.** `OrderService` не зависит от `EmailService`, `InventoryService` и т.д. — между ними только тип события.
- **Вынести side effects** (письма, метрики, аудит) из основной бизнес-логики, чтобы она оставалась чистой.
- **Domain events в DDD** — агрегат сам сообщает о значимых изменениях своего состояния.
- **Веер получателей** — на одно действие реагируют несколько компонентов, и список можно расширять без правки публикатора.

**Когда НЕ нужны:**
- Логика должна выполниться синхронно и быть частью той же транзакции — проще и понятнее прямой вызов.
- Нужен результат от обработчика — события работают по принципу fire-and-forget, ответа publisher не получает.
- Взаимодействие между микросервисами — Spring Events живут внутри одной JVM; для разных процессов нужен message broker (Kafka, RabbitMQ).

## Q2. Как опубликовать и обработать событие?

Нужны две стороны: **публикатор** инжектит `ApplicationEventPublisher` и вызывает `publishEvent()`, **слушатель** помечает метод `@EventListener` — Spring сам связывает их по типу аргумента события.

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

Связывание идёт по типу: метод слушателя принимает `OrderCreatedEvent`, и Spring вызовет его именно на этом событии. Аннотацию `@Component` на слушателе не забывайте — обработчики ищутся только среди бинов.

**Альтернатива — публиковать через `ApplicationContext`** (он тоже реализует `ApplicationEventPublisher`):

```java
applicationContext.publishEvent(new OrderCreatedEvent(order));
```

**Рекомендация:** инжектировать именно `ApplicationEventPublisher`, а не весь `ApplicationContext`. Контракт уже — только публикация событий, без доступа к остальным возможностям контекста; код проще читать и тестировать.

## Q3. Как создать кастомное событие?

Событие — это просто класс с данными. Начиная со Spring 4.2 им может быть любой POJO; наследовать `ApplicationEvent` больше не обязательно.

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

**Что выбрать:** POJO. Он проще, не тащит зависимость на Spring в domain-слой и его можно публиковать как есть. Наследование `ApplicationEvent` оставьте для легаси-кода — единственный его плюс в том, что он несёт `source` (ссылку на публикатора) и timestamp.

**Делайте событие immutable** (`record`, `@Value`, `final`-поля). Один и тот же объект события получают сразу несколько слушателей; если кто-то его изменит, остальные увидят искажённые данные. `record` подходит идеально:

```java
public record OrderCreatedEvent(Long orderId, String customerId, Instant occurredAt) { }
```

## Q4. Какие встроенные Spring-события (context events) существуют?

Spring сам публикует события на ключевых этапах жизненного цикла контекста и приложения — можно подписаться на них, чтобы выполнить код в нужный момент старта или остановки.

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

**Рекомендация:** для действий «при старте» подписывайтесь на `ApplicationReadyEvent`, а не на `ContextRefreshedEvent`. К моменту `ApplicationReadyEvent` гарантированно всё готово: все бины инициализированы, веб-сервер (Tomcat) слушает порт, `CommandLineRunner` и `ApplicationRunner` уже отработали. `ContextRefreshedEvent` срабатывает раньше и может прийти несколько раз (например, при рефреше дочернего контекста).

## Q5. Как сделать обработку события асинхронной?

По умолчанию Spring Events **синхронны**: `publishEvent()` блокирует публикатора и не возвращает управление, пока все слушатели не отработают — в том же потоке.

Чтобы вынести обработку в отдельный поток, повесьте на метод слушателя `@Async` (и включите `@EnableAsync`). Тогда `publishEvent()` не будет ждать завершения этого слушателя, а его обработка уйдёт в фоновый пул потоков:

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

**Подводные камни async-слушателей** (всё из-за того, что обработчик идёт в другом потоке):
- **Исключение не доходит до публикатора** — оно остаётся в фоновом потоке. Обрабатывайте ошибки внутри слушателя или через `AsyncUncaughtExceptionHandler`, иначе они потеряются.
- **Транзакция публикатора не наследуется** — потоки разные, transaction context (как и `SecurityContext`, MDC) по умолчанию не передаётся. Если слушателю нужна транзакция, повесьте на него отдельный `@Transactional`.
- **Связка с транзакцией публикатора разорвана** — событие выполняется независимо, перехватить откат публикатора через async-слушатель нельзя.

## Q6. Что такое `@TransactionalEventListener` и зачем он нужен?

`@TransactionalEventListener` — слушатель, выполнение которого привязано к фазе транзакции публикатора (по умолчанию — после успешного commit). Он решает классическую проблему обычного `@EventListener`.

**Проблема обычного `@EventListener`:** он синхронный и срабатывает прямо в момент `publishEvent()`, то есть **внутри ещё не закоммиченной транзакции**. Если дальше транзакция откатится, слушатель уже успел отработать — отправил клиенту письмо о «созданном» заказе, которого по факту в БД нет. Side effect случился, а данных нет.

`@TransactionalEventListener` устраняет это: обработчик откладывается до завершения транзакции и при rollback (для дефолтной фазы `AFTER_COMMIT`) просто не выполняется.

```java
@Service
@Transactional
public class OrderService {
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(new Order(req));
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

**Подводный камень — операции с БД в `AFTER_COMMIT`.** Слушатель привязан к транзакции публикатора, но к фазе `AFTER_COMMIT` она уже закоммичена, хотя транзакционные ресурсы ещё могут быть доступны. Поэтому новые записи из такого слушателя могут использовать прежние ресурсы, но уже не будут закоммичены в исходной транзакции — если нужно что-то сохранить, открывайте новую транзакцию явно: `@Transactional(propagation = REQUIRES_NEW)`.

## Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?

Фаза задаётся параметром `phase` и определяет момент, в который сработает слушатель относительно жизненного цикла транзакции:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
```

| Фаза | Когда выполняется | Типичное применение |
|---|---|---|
| `AFTER_COMMIT` (default) | После успешного commit | Side effects, которые валидны, только если данные реально сохранены: письма, публикация в Kafka |
| `AFTER_ROLLBACK` | После rollback | Компенсация, алерты о неудаче |
| `AFTER_COMPLETION` | После завершения (commit или rollback) | Очистка ресурсов в любом исходе |
| `BEFORE_COMMIT` | Перед commit, ещё внутри транзакции | Дозапись в ту же транзакцию (аудит), которая должна откатиться вместе с ней |

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

**Подводный камень:** `@TransactionalEventListener` завязан на наличие транзакции. Если событие опубликовано **вне транзакции**, слушатель по умолчанию молча не выполнится — частый источник «почему обработчик не сработал». Чтобы он отрабатывал и без транзакции, выставьте `fallbackExecution = true`:

```java
@TransactionalEventListener(fallbackExecution = true)
public void handle(MyEvent event) { }
```

## Q8. Как управлять порядком обработчиков одного события?

Порядком управляет аннотация `@Order` на методе-слушателе: меньшее число — выше приоритет, такой слушатель отработает раньше.

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

Без `@Order` порядок вызова слушателей **не гарантирован** — не полагайтесь на него. Граничные значения: `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` (раньше всех), `Ordered.LOWEST_PRECEDENCE = Integer.MAX_VALUE` (позже всех).

**Важно:** упорядочивание имеет смысл только для синхронных слушателей, которые выполняются последовательно в одном потоке. У async-слушателей (`@Async`) каждый уходит в свой поток и работает параллельно — `@Order` на них не даёт никаких гарантий порядка.

## Q9. Как условно обработать событие?

У `@EventListener` есть атрибут `condition` — SpEL-выражение, которое вычисляется до вызова метода. Если оно вернуло `false`, слушатель просто пропускается. Это позволяет фильтровать события по их содержимому, не загромождая тело метода проверками `if`:

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

Что доступно внутри SpEL-выражения:
- `#event` — сам объект события; имя переменной совпадает с именем параметра метода.
- `#root.event` — то же событие через root object (полезно, если имена конфликтуют).
- Любые поля и методы объекта события — например, `#event.total > 10000` или `#event.customerId.startsWith('VIP')`.

## Q10. Может ли `@EventListener` возвращать значение?

Да. Если метод-слушатель возвращает не-`null` объект, Spring трактует его как **новое событие** и публикует автоматически. Так можно выстраивать цепочки: одно событие порождает следующее, без явного вызова `publishEvent()`.

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

Если вернуть коллекцию или массив, **каждый элемент** публикуется как отдельное событие:

```java
@EventListener
public List<NotificationEvent> onOrderCreated(OrderCreatedEvent event) {
    return List.of(
        new EmailNotification(event.getCustomerId()),
        new PushNotification(event.getCustomerId())
    );
}
```

**Когда применять:** для коротких декларативных цепочек событий в одном процессе. Учтите, что `null`-результат не публикует ничего, а связь между событиями становится неявной — на длинных цепочках flow трудно проследить, поэтому не злоупотребляйте.

## Q11. Что такое `@DomainEvents` в Spring Data?

`@DomainEvents` — механизм Spring Data, позволяющий агрегату **накапливать события внутри себя** и публиковать их автоматически в момент сохранения через репозиторий. Это «правильный» способ генерировать domain events в DDD: событие рождается там же, где меняется состояние агрегата, а не в сервисе.

Простейший путь — унаследовать агрегат от `AbstractAggregateRoot` и регистрировать события методом `registerEvent()`:

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

При вызове `orderRepository.save(order)` Spring Data сам делает три шага:
1. Сохраняет агрегат в БД.
2. Достаёт накопленные события (метод, помеченный `@DomainEvents`) и публикует каждое через `ApplicationEventPublisher`.
3. Вызывает метод с `@AfterDomainEventPublication`. В реализации `AbstractAggregateRoot` он очищает список, чтобы события не публиковались повторно при следующем `save()`.

**Без `AbstractAggregateRoot`** те же `@DomainEvents` и `@AfterDomainEventPublication` можно реализовать вручную на любом классе:

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

## Q12. Зачем нужен `@AfterDomainEventPublication`?

`@AfterDomainEventPublication` помечает метод, который Spring Data вызывает **после публикации** накопленных событий. В нём можно очистить список, чтобы те же события не публиковались при следующем `save()` того же объекта:

```java
public class Order {
    @Transient
    private final List<Object> events = new ArrayList<>();

    @DomainEvents
    Collection<Object> domainEvents() { return List.copyOf(events); }

    @AfterDomainEventPublication
    void clearDomainEvents() { events.clear(); }
}
```

**Зачем важен:** агрегат живёт дольше одного `save()`. Если события не вычистить, при каждом следующем сохранении того же объекта Spring Data опубликует **их заново** — отсюда дубли писем, повторные списания, лишние записи. Очистка предотвращает повторную публикацию этих событий при следующем `save()` того же объекта. Она не гарантирует доставку или ровно однократную обработку при сбоях.

**Рекомендация:** не реализуйте это вручную без нужды — `AbstractAggregateRoot` уже содержит корректные `@DomainEvents` и `@AfterDomainEventPublication`. Ручная реализация нужна, только если унаследоваться от него нельзя.

## Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?

**Spring Modulith** — библиотека для построения модульных монолитов: приложение делится на логические модули. Для взаимодействия между ними можно публиковать события, когда не нужен синхронный ответ от другого модуля. Так модули остаются развязанными (модуль `Inventory` не зависит от `OrderService`), но при этом всё ещё внутри одного процесса — без сложности микросервисов.

Ключевой инструмент — аннотация `@ApplicationModuleListener`. Это сокращение для `@TransactionalEventListener` с фазой `AFTER_COMMIT`, `@Async` и `@Transactional(REQUIRES_NEW)`: обработчик выполняется после успешного commit в отдельном потоке и собственной транзакции. Аннотация сама по себе не запускает повторные попытки.

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
@Component
public class InventoryModule {
    @ApplicationModuleListener
    void onOrderCompleted(OrderCompleted event) {
        inventory.releaseReservation(event.orderId());
    }
}
```

**Externalization** позволяет направить выбранные события во внешний брокер при подключённом модуле интеграции и настроенной инфраструктуре:

```java
@Externalized("orders.completed")
public record OrderCompleted(Long orderId) { }
```

При подключённом реестре публикаций Spring Modulith записывает публикации для транзакционных слушателей в исходной транзакции и отмечает успешную обработку. Необработанные публикации можно повторно отправить через API; автоматическая повторная публикация при старте включается отдельной настройкой. Для этого нужны хранилище реестра и корректная конфигурация. Само событие Spring Framework остаётся внутрипроцессным.

## Q14. Как тестировать публикацию и обработку Spring Events?

Есть два разных вопроса, и под них разные инструменты: **публикуется ли событие** (проверяем со стороны публикатора) и **вызывается ли слушатель** (проверяем со стороны обработчика).

**Способ 1 — `@RecordApplicationEvents` (Spring Framework 5.3.3+).** Самый прямой способ проверить факт публикации: Spring записывает все события контекста, а в тест инжектится `ApplicationEvents`, по которому можно их перебрать и проверить.

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

**Способ 2 — `@MockitoSpyBean` на слушателе (Spring Framework 6.2+).** Когда нужно проверить, что обработчик реально был вызван (и сколько раз): оборачиваем бин-слушатель в spy и публикуем событие.

```java
@SpringBootTest
class EventHandlingTest {

    @MockitoSpyBean NotificationListener notificationListener;
    @Autowired ApplicationEventPublisher publisher;

    @Test
    void shouldCallNotificationOnOrderCreated() {
        publisher.publishEvent(new OrderCreatedEvent(1L, "CUST-1"));
        verify(notificationListener, times(1)).onOrderCreated(any());
    }
}
```

**Способ 3 — тестовый слушатель, собирающий события.** Регистрируем в тест-контексте отдельный `@TestComponent`, который накапливает пойманные события, — удобно для проверки содержимого и для async-сценариев (там пойманное проверяют через `Awaitility`, давая async-обработчику время отработать).

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

## Q15. Когда использовать Spring Events, а когда прямой вызов метода?

Главный компромисс: события дают слабую связанность ценой неявности потока выполнения. Прямой вызов очевиден при чтении кода, но жёстко связывает компоненты.

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

## Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?

Коротко: Spring Events — это **внутрипроцессный** механизм (всё в памяти одной JVM, без встроенного долговременного хранения и восстановления), а Kafka/RabbitMQ — **межпроцессный** транспорт, у которого хранение и доставка зависят от конфигурации. Это не конкуренты, а инструменты для разных масштабов.

| Параметр | Spring Events | Kafka/RabbitMQ |
|---|---|---|
| Scope | Внутри JVM-процесса | Между процессами/сервисами |
| Персистентность | Нет (in-memory) | Возможна при настройке хранения |
| Гарантия доставки | Зависит от обработчика; без реестра нет восстановления | Зависит от брокера, настроек и подтверждений |
| Replay | Нет | Зависит от брокера и хранения |
| Масштабирование | Ограничено одним процессом | Зависит от брокера и потребителей |
| Latency | Зависит от слушателей | Зависит от брокера и сети |
| Setup | Zero config | Внешняя инфраструктура |

**Мост между мирами — Spring Modulith с реестром публикаций и модулем externalization.** При соответствующей конфигурации реестр сохраняет публикацию в исходной транзакции и позволяет повторить незавершённую отправку во внешний брокер. Семантика доставки зависит от выбранного брокера, обработчика и настроек повторной отправки.

**Типичная архитектура:** внутри сервиса работают лёгкие Spring Events, а наружу, к другим сервисам, событие уходит через брокер уже после успешного commit:
```
Бизнес-логика → Spring Event → Modulith externalization → брокер
```

---

## See also

- [Spring Framework](spring-framework-interview.md) — ApplicationContext, BeanFactory, жизненный цикл бинов
- [Spring Boot](spring-boot-interview.md) — ApplicationReadyEvent, ApplicationStartedEvent для startup логики
- [Spring AOP](spring-aop-interview.md) — события реализованы без AOP, но часто используются вместе
- [Spring Data JPA](spring-data-jpa-interview.md) — @DomainEvents, AbstractAggregateRoot, @AfterDomainEventPublication
- [Spring Scheduling](spring-scheduling-interview.md) — @Scheduled vs события для периодических задач
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — общие паттерны event-driven архитектуры
- [Apache Kafka](../../messaging/kafka-interview.md) — для межсервисных событий с настраиваемой доставкой
- [Domain-Driven Design](../../architecture/ddd-interview.md) — domain events, aggregates, bounded context
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — transactional outbox, at-least-once delivery
- [Unit Testing](../../testing/unit-testing-interview.md) — @RecordApplicationEvents, ApplicationEvents, тестирование listeners
- [Шпаргалка: Spring Events — события приложения](../../../frameworks/java-frameworks/spring/spring-events.md) — теория
- [Spring State Machine](spring-state-machine-interview.md) — конфигурация состояний и переходов, Guards, Actions, Extended State,…
