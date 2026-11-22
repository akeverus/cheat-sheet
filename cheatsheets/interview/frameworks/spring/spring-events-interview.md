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
- Нужен результат от обработчика — `publishEvent()` возвращает `void`. По умолчанию публикатор ждёт синхронных слушателей, а результат их работы через этот API не получает.
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
        // обновить счётчик попыток создания заказа
    }
}
```

Связывание идёт по типу: метод слушателя принимает `OrderCreatedEvent`, и Spring вызовет его именно на этом событии. Аннотацию `@Component` на слушателе не забывайте — обработчики ищутся только среди бинов.

При стандартной синхронной доставке слушатель из примера вызывается до завершения транзакции создания заказа. Если действие нужно выполнять только после успешного commit, используйте фазу `AFTER_COMMIT` из Q6. [События Spring Framework](https://docs.spring.io/spring-framework/reference/6.2/core/beans/context-introduction.html#context-functionality-events)

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

**Что выбрать:** для доменного события обычно достаточно POJO или `record`. При вызове `publishEvent(Object)` Spring оборачивает такой объект в `PayloadApplicationEvent`. Метод `@EventListener` может принимать сам объект, например `OrderCreatedEvent`. Подкласс `ApplicationEvent` позволяет явно передать `source` при создании события. Его смысл задаёт публикатор, а базовый класс сохраняет время создания события. [ApplicationEventPublisher](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/context/ApplicationEventPublisher.html#publishEvent(java.lang.Object)).

Для события удобно использовать неизменяемые данные. Слушатели могут получить ссылку на один объект, поэтому его изменение влияет на других получателей. `record` фиксирует ссылки компонентов. Если компонент содержит изменяемую коллекцию, при создании события можно сохранить её неизменяемую копию.

```java
public record OrderCreatedEvent(Long orderId, String customerId, Instant occurredAt) { }
```

## Q4. Какие встроенные Spring-события (context events) существуют?

Приложение на Spring Boot 3.5 должно закончить обязательный прогрев кеша до перехода readiness в `ACCEPTING_TRAFFIC`. В какой точке запуска выполнить прогрев и как она расположена относительно событий `ApplicationStartedEvent` и `ApplicationReadyEvent`?

```text
Spring Boot 3.5, веб-приложение
cacheService.warmUp() выполняет обязательный синхронный прогрев.
Маршрутизация трафика учитывает readiness приложения.
```


Spring сам публикует события на ключевых этапах жизненного цикла контекста и приложения — можно подписаться на них, чтобы выполнить код в нужный момент старта или остановки.

| Событие | Когда публикуется |
|---|---|
| `ContextRefreshedEvent` | После инициализации/обновления контекста |
| `ContextStartedEvent` | При вызове `context.start()` |
| `ContextStoppedEvent` | При вызове `context.stop()` |
| `ContextClosedEvent` | При закрытии контекста |
| `ApplicationReadyEvent` | После успешного завершения `ApplicationRunner` и `CommandLineRunner` (Spring Boot) |
| `ApplicationStartedEvent` | После refresh контекста, до вызова обоих типов runners (Spring Boot) |
| `ApplicationFailedEvent` | При ошибке старта (Spring Boot) |
| `RequestHandledEvent` | После обработки HTTP-запроса (Spring MVC) |

```java
@Bean
ApplicationRunner warmUpCache(CacheService cacheService) {
    return args -> cacheService.warmUp();
}
```

В Boot 3.5 контекст проходит refresh, затем Boot публикует `ApplicationStartedEvent` и вызывает runners. Если синхронный прогрев в `ApplicationRunner` завершился успешно и остальные runners отработали, Boot публикует `ApplicationReadyEvent`, затем событие доступности с `ReadinessState.ACCEPTING_TRAFFIC`. Если прогрев выбрасывает исключение из runner, запуск завершается ошибкой. Уже открытый порт сам по себе не доказывает завершение прогрева. [Жизненный цикл и события Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/features/spring-application.html#features.spring-application.application-events-and-listeners)

`ContextRefreshedEvent` относится к конкретному refresh контекста. В иерархии слушатель родительского контекста может получить события и своего, и дочернего контекста. Если действие должно выполняться для одного контекста, сравните контекст из события с нужным экземпляром.

## Q5. Как сделать обработку события асинхронной?

В приложении используется стандартный синхронный multicaster. Как направить только этот слушатель в заданный пул и чего будет ждать публикатор?

```java
// Как направить только этот слушатель в eventsExecutor?
// Чего будет ждать вызов publishEvent() после изменения?
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
        return executor;
    }
}

@Component
public class NotificationListener {

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        emailService.send(event.getCustomerId(), "Order created");
    }
}
```

**Разбор.** Добавьте `@Async("eventsExecutor")` к методу `onOrderCreated`. Показанная конфигурация уже включает `@EnableAsync`. При успешной постановке задачи публикатор не ждёт завершения этого обработчика. Остальные обычные слушатели по-прежнему выполняются синхронно, и публикатор ждёт их выполнения. При отказе пула принять задачу ошибка отправки может дойти до публикатора. [Spring Framework: выполнение задач](https://docs.spring.io/spring-framework/reference/6.2/integration/scheduling.html#scheduling-annotation-support-async)


**Подводные камни async-слушателей** (всё из-за того, что обработчик идёт в другом потоке):
- **Исключение не доходит до публикатора** — оно остаётся в фоновом потоке. Обрабатывайте ошибки внутри слушателя или через `AsyncUncaughtExceptionHandler`, иначе они потеряются.
- **Транзакция публикатора не наследуется** — потоки разные, transaction context (как и `SecurityContext`, MDC) по умолчанию не передаётся. Если слушателю нужна транзакция, повесьте на него отдельный `@Transactional`.
- **Связка с транзакцией публикатора разорвана** — событие выполняется независимо, перехватить откат публикатора через async-слушатель нельзя.

## Q6. Что такое `@TransactionalEventListener` и зачем он нужен?

Метод сервиса вызван через Spring-прокси и выполняется в обычной транзакции, привязанной к потоку. Когда вызовется слушатель при commit и при rollback?

```java
// Когда вызовется слушатель при commit и при rollback?
@Service
@Transactional
public class OrderService {
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(new Order(req));
        publisher.publishEvent(new OrderCreatedEvent(order.getId()));
        return order;
    }
}

@Component
public class OrderNotificationListener {

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        emailService.sendConfirmation(event.getOrderId());
    }
}
```

`@TransactionalEventListener` — слушатель, выполнение которого привязано к фазе транзакции публикатора (по умолчанию — после успешного commit). Он решает классическую проблему обычного `@EventListener`.

**Проблема обычного `@EventListener`:** он синхронный и срабатывает прямо в момент `publishEvent()`, то есть **внутри ещё не закоммиченной транзакции**. Если дальше транзакция откатится, слушатель уже успел отработать — отправил клиенту письмо о «созданном» заказе, которого по факту в БД нет. Side effect случился, а данных нет.

`@TransactionalEventListener` устраняет это: обработчик откладывается до завершения транзакции и при rollback (для дефолтной фазы `AFTER_COMMIT`) просто не выполняется.

[Spring Framework: события, привязанные к транзакции](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/event.html).


**Реактивная транзакция.** Пример выше использует транзакцию, привязанную к потоку. Начиная со Spring Framework 6.1 слушатели также поддерживают транзакции под управлением `ReactiveTransactionManager`. В этом случае транзакция хранится в Reactor Context, а источником опубликованного `ApplicationEvent` должен быть текущий `TransactionContext`. Обычный вызов `ApplicationEventPublisher.publishEvent(orderCreated)` с POJO сам этот контекст не переносит.

Например, сервис сохраняет заказ через R2DBC и должен запланировать уведомление после успешного commit. Пусть `orderRepository.save(order)` возвращает `Mono<Order>`, а `transactionalOperator` настроен с реактивным менеджером транзакций для этого репозитория. Публикацию включаем в ту же цепочку:

```java
var events = new TransactionalEventPublisher(applicationEventPublisher);

return transactionalOperator.transactional(
    orderRepository.save(order)
        .flatMap(saved -> events.publishEvent(new OrderCreatedEvent(saved.getId()))
            .thenReturn(saved)));
```

`TransactionalEventPublisher` оборачивает полезную нагрузку и помещает текущий `TransactionContext` в источник события. Возвращённый `Mono<Void>` выполняет публикацию при подписке внутри транзакции. Слушатель с фазой по умолчанию вызовется при успешной фиксации, а при откате эта фаза пропускается. При раздельной подписке цепочка публикации может оказаться без нужного контекста. [Spring Framework 6.2 и реактивные события](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/event.html) · [TransactionalEventPublisher](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/transaction/reactive/TransactionalEventPublisher.html).

**Подводный камень — операции с БД в `AFTER_COMMIT`.** Слушатель привязан к транзакции публикатора, но к фазе `AFTER_COMMIT` она уже закоммичена, хотя транзакционные ресурсы ещё могут быть доступны. Поэтому новые записи из такого слушателя могут использовать прежние ресурсы, но уже не будут закоммичены в исходной транзакции — если нужно что-то сохранить, открывайте новую транзакцию явно: `@Transactional(propagation = REQUIRES_NEW)`.

## Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?

В Spring Framework 6.2 событие опубликовано внутри активной транзакции. Менеджер дошёл до завершения, но передал синхронизациям статус `STATUS_UNKNOWN`. Какой из слушателей, привязанных к фазам завершения, вызовется?

```java
// Spring Framework 6.2, статус в afterCompletion: STATUS_UNKNOWN
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onCommit(OrderCreatedEvent event) { }

@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
public void onRollback(OrderCreatedEvent event) { }

@TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
public void onCompletion(OrderCreatedEvent event) { }
```

| Фаза | Когда выполняется | Типичное применение |
|---|---|---|
| `AFTER_COMMIT` (default) | После успешного commit | Side effects, которые валидны, только если данные реально сохранены: письма, публикация в Kafka |
| `AFTER_ROLLBACK` | После rollback | Компенсация, алерты о неудаче |
| `AFTER_COMPLETION` | При вызове `afterCompletion` с любым статусом, включая `STATUS_UNKNOWN` | Очистка ресурсов, которая не зависит от исхода |
| `BEFORE_COMMIT` | Перед commit, ещё внутри транзакции | Дозапись в ту же транзакцию (аудит), которая должна откатиться вместе с ней |

В этом сценарии вызовется только `onCompletion`. В Spring Framework 6.2 обе фазовые ветки, `AFTER_COMMIT` и `AFTER_ROLLBACK`, проверяют конкретный статус в `afterCompletion`. Например, при исключении во время commit менеджер может передать `STATUS_UNKNOWN`, если откат после такой ошибки отключён. `BEFORE_COMMIT` срабатывает на более раннем этапе и сам по себе не подтверждает успешную фиксацию. [Синхронизация слушателей, Spring Framework 6.2.19](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-tx/src/main/java/org/springframework/transaction/event/TransactionalApplicationListenerSynchronization.java) · [Обработка ошибки commit](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java#L759-L769).

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

**Подводный камень:** `@TransactionalEventListener` завязан на наличие транзакции. Если событие опубликовано **вне транзакции**, слушатель по умолчанию молча не выполнится — частый источник «почему обработчик не сработал». С `fallbackExecution = true` при публикации вне транзакции адаптер запускает обработку события без ожидания указанной фазы. Такой запуск не подтверждает ни commit, ни rollback. [Адаптер слушателя, Spring Framework 6.2.19](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-tx/src/main/java/org/springframework/transaction/event/TransactionalApplicationListenerMethodAdapter.java#L76-L94).

```java
@TransactionalEventListener(fallbackExecution = true)
public void handle(MyEvent event) { }
```

## Q8. Как управлять порядком обработчиков одного события?

Все три слушателя обрабатывают одно событие синхронно. В каком порядке Spring вызовет их?

```java
// В каком порядке Spring вызовет эти синхронные слушатели?
@Component
public class OrderLifecycleListeners {

    @Order(3)
    @EventListener
    public void notifyCustomer(OrderCreatedEvent event) {
    }

    @Order(1)
    @EventListener
    public void validateOrder(OrderCreatedEvent event) {
    }

    @Order(2)
    @EventListener
    public void auditOrder(OrderCreatedEvent event) {
    }
}
```

Порядком управляет аннотация `@Order` на методе-слушателе: меньшее число — выше приоритет, такой слушатель отработает раньше.

В примере порядок такой: `validateOrder`, `auditOrder`, `notifyCustomer`.


Без `@Order` порядок вызова слушателей **не гарантирован** — не полагайтесь на него. Граничные значения: `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` (раньше всех), `Ordered.LOWEST_PRECEDENCE = Integer.MAX_VALUE` (позже всех).

**Важно.** При `@Async` порядок вызова прокси не гарантирует порядок начала или завершения задач в пуле.

## Q9. Как условно обработать событие?

Как Spring применит условие к двум публикациям обычного объекта `OrderCreatedEvent` с суммами 10 000 и 12 500?

```java
// Какое из двух событий будет обработано?
record OrderCreatedEvent(long orderId, String customerId, int total) {}

@Component
public class PremiumOrderListener {

    @EventListener(condition = "#a0.total > 10000")
    public void onLargeOrder(OrderCreatedEvent event) {
        vipService.assignManager(event.customerId());
    }
}

// Две отдельные публикации
publisher.publishEvent(new OrderCreatedEvent(101L, "CUST-1", 10_000));
publisher.publishEvent(new OrderCreatedEvent(102L, "CUST-1", 12_500));
```

Spring вычисляет SpEL-условие перед вызовом метода. Условие истинно для суммы 12 500, поэтому обработчик сработает только для второй публикации. Сумма 10 000 равна порогу, а в условии используется строгое сравнение.

- `#a0`, `#p0` и `#root.args[0]` обращаются к первому аргументу метода. В примере это объект `OrderCreatedEvent`, переданный как полезная нагрузка.
- `#root.event` обращается к объекту события Spring. Для опубликованного POJO это обёртка `PayloadApplicationEvent`. К её содержимому можно обратиться через `#root.event.payload`.
- `#event` обращается к аргументу по имени параметра. Для этого имя должно быть доступно Spring, например через метаданные компиляции Java с `-parameters`. Индексный вариант `#a0` от имени параметра не зависит.

[Spring Framework: контракт EventListener](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/context/event/EventListener.html#condition()).

## Q10. Может ли `@EventListener` возвращать значение?

Публикуется `OrderPaidEvent`, оба слушателя синхронные. Что Spring сделает с объектом, который вернёт `onOrderPaid()`?

```java
// Что Spring сделает с объектом, который вернёт onOrderPaid()?
@Component
public class OrderEventChain {

    @EventListener
    public OrderShippedEvent onOrderPaid(OrderPaidEvent event) {
        shippingService.arrangeShipping(event.getOrderId());
        return new OrderShippedEvent(event.getOrderId(), LocalDate.now().plusDays(3));
    }

    @EventListener
    public void onOrderShipped(OrderShippedEvent event) {
        emailService.sendShippingNotification(event);
    }
}
```

Да. Если метод-слушатель возвращает не-`null` объект, Spring трактует его как **новое событие** и публикует автоматически. Так можно выстраивать цепочки: одно событие порождает следующее, без явного вызова `publishEvent()`.



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

Рассмотрите два независимых вызова репозитория. Перед каждым вызовом заказ существует в БД, а объект `order` содержит одно накопленное событие, доступное через метод с `@DomainEvents`. В каком случае Spring Data опубликует это событие?

```java
// Два независимых варианта. В каждом order уже содержит накопленное событие.
orderRepository.delete(order);
orderRepository.deleteById(order.getId());
```

`@DomainEvents` позволяет агрегату хранить события до вызова поддерживаемой операции репозитория. Метод без параметров возвращает одно событие или коллекцию. В первом варианте `delete(order)` получает сам агрегат и запускает публикацию. `deleteById(id)` принимает только идентификатор, поэтому контракт публикации через `@DomainEvents` его не включает. Это верно и в случае, когда реализация репозитория внутри операции загружает сущность. Аналогично передача агрегата в `save(order)` или списка агрегатов в `saveAll(orders)` позволяет опубликовать их события. [Spring Data JPA: Publishing Events from Aggregate Roots](https://docs.spring.io/spring-data/jpa/reference/repositories/core-domain-events.html)

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
1. Выполняет операцию сохранения. В открытой внешней транзакции запись может быть сброшена в БД позже, а публикация может состояться до commit. О привязке обработки к commit см. Q6–Q7.
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

Агрегат содержит два накопленных события. Первый синхронный слушатель выполнился, а при публикации второго события другой синхронный слушатель выбросил исключение. Ошибка дошла до вызова `publishEvent()`. Что произойдёт с буфером агрегата и что может произойти при повторном сохранении того же объекта?

```text
Spring Data Commons 3.5.13
orderRepository.save(order)
События агрегата: E1, E2
publishEvent(E1) завершился
publishEvent(E2) выбросил исключение
Повторный save получает тот же объект
```

Spring Data Commons 3.5.13 вызывает метод с `@AfterDomainEventPublication` после того, как все вызовы `publishEvent()` для агрегата завершились штатно и прошла проверка списка событий. В примере исключение прерывает этот цикл до вызова очистки. Если буфер сохранился и повторный `save(order)` доходит до этапа публикации, `E1` может быть опубликовано повторно. Успешный возврат из `publishEvent()` означает завершение передачи события механизму публикации. Асинхронный слушатель может ещё работать, а внешняя транзакция может ещё оставаться открытой. [Исходник Spring Data Commons 3.5.13](https://github.com/spring-projects/spring-data-commons/blob/3.5.13/src/main/java/org/springframework/data/repository/core/support/EventPublishingRepositoryProxyPostProcessor.java#L180-L208)

Для очистки буфера можно использовать такой callback.

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

После штатной очистки следующий `save()` того же объекта публикует только новые накопленные события. При повторных попытках идемпотентность обработки обеспечивают отдельно. В `AbstractAggregateRoot` callback очистки уже предусмотрен.

## Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?

**Spring Modulith** — библиотека для построения модульных монолитов: приложение делится на логические модули. Для взаимодействия между ними можно публиковать события, когда не нужен синхронный ответ от другого модуля. Так модули остаются развязанными (модуль `Inventory` не зависит от `OrderService`), но при этом всё ещё внутри одного процесса — без сложности микросервисов.

В примере со Spring Boot 3.5 и Spring Modulith 1.4 используется `org.springframework.modulith.events.ApplicationModuleListener`. Она объединяет `@TransactionalEventListener` с фазой `AFTER_COMMIT` по умолчанию, `@Async` и `@Transactional(REQUIRES_NEW)`. После успешного commit Spring передаёт вызов настроенному исполнителю, если в контексте включена обработка `@Async`, например через `@EnableAsync`. Транзакционная инфраструктура открывает для работы слушателя новую транзакцию. Повторы настраиваются отдельно. [Spring Modulith 1.4](https://docs.spring.io/spring-modulith/reference/1.4/events.html#application-module-listener) · [Spring Framework 6.2](https://docs.spring.io/spring-framework/reference/6.2/integration/scheduling.html).

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

**Externalization** позволяет опубликовать выбранные события во внешний брокер. Допустим, потребители читают тему Kafka `orders.completed` и используют идентификатор заказа как ключ сообщения. Для этого подключите `spring-modulith-events-kafka`, настройте producer и поместите аннотированный тип события в пакет, охватываемый автоконфигурацией Spring Boot.

```java
import org.springframework.modulith.events.Externalized;

@Externalized("orders.completed::#{#this.orderId().toString()}")
public record OrderCompleted(Long orderId) { }
```

При публикации `new OrderCompleted(42L)` цель `orders.completed` задаёт тему Kafka, а выражение после `::` вычисляет строковый ключ `"42"`. Вариант `@Externalized("orders.completed")` задаёт только цель. Для AMQP смысл цели и ключа определяется соответствующей интеграцией. [Spring Modulith 1.4 — Externalizing Events](https://docs.spring.io/spring-modulith/reference/1.4/events.html#externalizing-events).

При подключённом реестре публикаций Spring Modulith записывает публикации для транзакционных слушателей в исходной транзакции и отмечает успешную обработку. Необработанные публикации можно повторно отправить через API; автоматическая повторная публикация при старте включается отдельной настройкой. Для этого нужны хранилище реестра и корректная конфигурация. Само событие Spring Framework остаётся внутрипроцессным.

## Q14. Как тестировать публикацию и обработку Spring Events?

Сервис публикует `OrderCreatedEvent` в потоке теста и присоединяется к тестовой транзакции с откатом по умолчанию. Обработчик подписан на `AFTER_COMMIT`. Какие наблюдения подтверждают публикацию и какие нужны для проверки обработчика?

```text
Spring Framework 6.2
Тест использует @Transactional и @RecordApplicationEvents.
orderService.createOrder(...) публикует OrderCreatedEvent внутри транзакции теста.
Обработчик использует @TransactionalEventListener(phase = AFTER_COMMIT).
Тест проверяет события сразу после возврата createOrder(...).
```


Есть два разных вопроса, и под них разные инструменты: **публикуется ли событие** (проверяем со стороны публикатора) и **вызывается ли слушатель** (проверяем со стороны обработчика).

**Способ 1 — `@RecordApplicationEvents` (Spring Framework 5.3.3+).** TestContext предоставляет записи через `ApplicationEvents` в пределах текущего теста. В сценарии выше событие можно увидеть до commit. При откате по умолчанию обработчик `AFTER_COMMIT` не запускается. Для проверки его эффекта можно завершить тестовую транзакцию через `TestTransaction.flagForCommit()` и `TestTransaction.end()`, затем проверить результат. Если обработчик асинхронный, дождитесь наблюдаемого эффекта с ограниченным временем ожидания. [Запись событий](https://docs.spring.io/spring-framework/reference/6.2/testing/testcontext-framework/application-events.html) · [Тестовые транзакции](https://docs.spring.io/spring-framework/reference/6.2/testing/testcontext-framework/tx.html)

В примерах используется `record OrderCreatedEvent(Long orderId, String customerId, Instant occurredAt)` из Q3.

```java
@SpringBootTest
@Transactional
@RecordApplicationEvents
class OrderServiceTest {

    @Autowired OrderService orderService;

    @Test
    void createOrder_shouldPublishOrderCreatedEvent(ApplicationEvents events) {
        orderService.createOrder(new OrderRequest("CUST-1"));

        assertThat(events.stream(OrderCreatedEvent.class)
            .map(OrderCreatedEvent::customerId))
            .containsExactly("CUST-1");
    }
}
```

**Способ 2 — `@MockitoSpyBean` на слушателе (Spring Framework 6.2+).** Для проверки фазы используем бин `NotificationListener`, у которого метод `onOrderCreated` помечен `@TransactionalEventListener(phase = AFTER_COMMIT)` и выполняется синхронно. Тест публикует событие напрямую, чтобы проверить доставку отдельно от создания заказа.

```java
@SpringBootTest
@Transactional
class EventHandlingTest {

    @MockitoSpyBean NotificationListener notificationListener;
    @Autowired ApplicationEventPublisher publisher;

    @Test
    void shouldCallNotificationListenerAfterCommit() {
        var event = new OrderCreatedEvent(1L, "CUST-1", Instant.now());
        publisher.publishEvent(event);
        verify(notificationListener, never()).onOrderCreated(event);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        verify(notificationListener).onOrderCreated(event);
    }
}
```

Spy подтверждает вызов метода. Для проверки фактической отправки письма проверьте вызов тестового почтового адаптера или другой наблюдаемый эффект. Если тест фиксирует изменения в БД, используйте изолированные тестовые данные и очищайте их после проверки.

**Способ 3 — тестовый слушатель, собирающий события.** Добавьте сборщик в тестовый контекст через `@Import(TestOrderCreatedListener.class)`. Для чтения из другого потока в примере используется потокобезопасная коллекция. Сборщик подтверждает получение события самим сборщиком. Чтобы проверить работу прикладного асинхронного слушателя, дождитесь его собственного эффекта.

```java
@TestComponent
public class TestOrderCreatedListener {
    private final List<OrderCreatedEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @EventListener
    public void capture(OrderCreatedEvent event) {
        receivedEvents.add(event);
    }

    public List<OrderCreatedEvent> getEvents() { return List.copyOf(receivedEvents); }
}
```

## Q15. Когда использовать Spring Events, а когда прямой вызов метода?

Сервис оформляет заказ в транзакции. Ему нужна цена до сохранения заказа. Письмо клиенту можно отправить только после успешного commit. Какие действия стоит выполнить прямым вызовом, а какие привязать к событию?

```text
Spring Framework 6.2
calculateTotal(order) возвращает итоговую цену.
Заказ сохраняется с итоговой ценой.
Почтовый сервис принимает команду отправки вне транзакции БД.
После сохранения заказа транзакция ещё может откатиться.
```

Для расчёта цены подходит прямой вызов. Результат нужен вызывающему коду, чтобы продолжить оформление, а ошибка расчёта должна прервать операцию. Вызов сервиса делает эту зависимость явной.

Уведомление можно привязать к событию `OrderPlaced` с помощью `@TransactionalEventListener(phase = AFTER_COMMIT)`. Сервис публикует событие внутри транзакции, а слушатель отправляет письмо после успешной фиксации. При rollback этот слушатель не отправит письмо. Обычный синхронный `@EventListener` выполнился бы внутри вызова `publishEvent()`, пока транзакция ещё может откатиться. Откат записи заказа не отменяет уже отправленное письмо. [Доставка событий Spring](https://docs.spring.io/spring-framework/reference/6.2/core/beans/context-introduction.html#context-functionality-events) · [События, привязанные к транзакции](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/event.html)

События подходят, когда о факте должны узнать один или несколько независимых обработчиков, а результат их работы не нужен публикатору для принятия решения. Порядок вызовов при этом задаётся настройкой слушателей. Синхронная доставка может увеличить время вызова публикатора. Для выполнения в пуле нужна отдельная настройка, а для восстановления отправки после падения процесса — сохранённая публикация, как в Q16.

## Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?

Коротко: Spring Events без дополнительных интеграций — **внутрипроцессный** механизм (всё в памяти одной JVM, без встроенного долговременного хранения и восстановления), а Kafka/RabbitMQ — **межпроцессный** транспорт, у которого хранение и доставка зависят от конфигурации. Это не конкуренты, а инструменты для разных масштабов.

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

**Что произойдёт при падении процесса после commit заказа?** При обычном `@TransactionalEventListener` с фазой `AFTER_COMMIT` заказ уже сохранён. Если процесс упадёт до отправки, Spring Framework сам не восстановит событие из БД. С Event Publication Registry запись для слушателя сохраняется в транзакции заказа. Если заказ и запись реестра участвуют в одной транзакции, они фиксируются или откатываются вместе. После сбоя незавершённую публикацию можно повторить по настроенной политике. [Spring Modulith: Event Publication Registry](https://docs.spring.io/spring-modulith/reference/events.html#event-publication-registry).

Если брокер уже принял сообщение, а процесс упал до записи о завершении публикации, повторная отправка может создать дубликат. Подтверждение брокера и отметка реестра происходят в разных системах. Для операции вроде начисления бонусов передайте стабильный идентификатор события. Потребитель может сохранить этот идентификатор с уникальным ограничением в той же транзакции, что и начисление. При повторной доставке уже обработанный идентификатор позволит пропустить начисление.

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
