---
title: "Spring Events — события приложения"
description: "Spring ApplicationEvents: синхронные и асинхронные события, @TransactionalEventListener, доменные события JPA, тестирование."
tags:
  - frameworks
  - spring
  - events
  - application-events
  - transactional
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring Events — события приложения

Spring Events — встроенный механизм pub/sub внутри одного Spring-контекста. Позволяет разделить логику без введения внешнего брокера.

## Полезные ссылки

### Официальная документация
- [Spring Events (docs.spring.io)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events) — официальная документация по событиям Spring

### См. также
- [Spring Boot](../../spring/spring-boot.md) — базовый фреймворк
- [Spring Modulith](spring-modulith.md) — модульный монолит с поддержкой событий
- [Spring Core](../../spring/spring-core.md) — ядро Spring Framework
- [Spring Data JPA](spring-data-jpa.md) — JPA-интеграция, доменные события
- [Spring Testing](spring-testing.md) — тестирование Spring-приложений
- [Spring Kafka](spring-kafka.md) — асинхронный обмен сообщениями
- [Вопросы на собеседовании](../../../interview/frameworks/spring/spring-events-interview.md) — подготовка к интервью

## Содержание

- [Базовый пример](#базовый-пример)
- [@EventListener — опции](#eventlistener-опции)
- [Асинхронные события](#асинхронные-события)
- [@TransactionalEventListener](#transactionaleventlistener)
  - [fallbackExecution](#fallbackexecution)
- [Generic события](#generic-события)
- [Доменные события JPA (@DomainEvents)](#доменные-события-jpa-domainevents)
- [Тестирование событий](#тестирование-событий)
- [Типичные ошибки](#типичные-ошибки)
- [Spring Modulith интеграция](#spring-modulith-интеграция)

## Базовый пример

```java
// Событие — простой класс или record (Spring 4.2+, не нужно extends ApplicationEvent)
public record OrderPlaced(UUID orderId, BigDecimal amount) {}

// Публикация
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void placeOrder(Order order) {
        orderRepository.save(order);
        publisher.publishEvent(new OrderPlaced(order.getId(), order.getTotal()));
    }
}

// Подписчик
@Component
public class NotificationListener {
    @EventListener
    public void onOrderPlaced(OrderPlaced event) {
        notificationService.send("Order placed: " + event.orderId());
    }
}
```

По умолчанию `publishEvent()` **синхронный** — подписчик вызывается в той же нити и транзакции.

## @EventListener — опции

```java
// Условие (SpEL)
@EventListener(condition = "#event.amount > 1000")
public void onLargeOrder(OrderPlaced event) { ... }

// Несколько типов событий
@EventListener({OrderPlaced.class, OrderUpdated.class})
public void onOrderChange(Object event) { ... }

// Порядок вызова слушателей
@EventListener
@Order(1)
public void firstListener(OrderPlaced event) { ... }

// Возврат события — автоматически публикуется дальше
@EventListener
public InvoiceCreated onOrderPlaced(OrderPlaced event) {
    return new InvoiceCreated(event.orderId());
}
```

## Асинхронные события

```java
// Включить @Async
@Configuration
@EnableAsync
public class AsyncConfig {}

@Component
public class AsyncNotificationListener {
    @Async
    @EventListener
    public void onOrderPlaced(OrderPlaced event) {
        // выполняется в отдельном пуле потоков
        emailService.sendConfirmation(event.orderId());
    }
}
```

**Внимание:** `@Async` + `@EventListener` — исключения в слушателе не откатывают транзакцию вызывающего кода.

## @TransactionalEventListener

Событие обрабатывается только при определённой фазе транзакции.

```java
@Component
public class AuditListener {

    // Самый частый случай: обработать после успешного коммита
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCommitted(OrderPlaced event) {
        auditLog.record(event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(OrderPlaced event) { ... }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollback(OrderPlaced event) { ... }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void always(OrderPlaced event) { ... }
}
```

### fallbackExecution

```java
// Если транзакции нет — всё равно выполнить
@TransactionalEventListener(fallbackExecution = true)
public void onEvent(OrderPlaced event) { ... }
```

По умолчанию `fallbackExecution = false` — слушатель не вызывается без активной транзакции.

## Generic события

```java
// Событие с типом-параметром
public class EntityCreated<T> {
    private final T entity;
    public EntityCreated(T entity) { this.entity = entity; }
    public T getEntity() { return entity; }
}

@EventListener
public void onUserCreated(EntityCreated<User> event) {
    User user = event.getEntity();
}
```

## Доменные события JPA (@DomainEvents)

Spring Data JPA вызывает `publishEvent()` автоматически при `save()`.

```java
@Entity
public class Order extends AbstractAggregateRoot<Order> {

    public Order place() {
        // регистрируем событие — публикуется при save()
        registerEvent(new OrderPlaced(this.id, this.total));
        return this;
    }
}

// В сервисе:
orderRepository.save(order.place());  // OrderPlaced публикуется автоматически
```

`AbstractAggregateRoot` реализует `@DomainEvents` + `@AfterDomainEventPublication`.

## Тестирование событий

```java
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;

    @RecordApplicationEvents                // аннотация на класс
    @Test
    void placeOrderPublishesEvent(ApplicationEvents events) {
        orderService.placeOrder(new Order(...));

        assertThat(events.ofType(OrderPlaced.class))
            .hasSize(1)
            .first()
            .extracting(OrderPlaced::orderId)
            .isNotNull();
    }
}
```

Без `@RecordApplicationEvents` используй `ApplicationEventPublisher` mock или `@SpyBean`.

## Типичные ошибки

| Ошибка | Проблема | Решение |
|--------|----------|---------|
| Слушатель вызван до коммита | Использован `@EventListener` вместо `@TransactionalEventListener` | Заменить на `@TransactionalEventListener(AFTER_COMMIT)` |
| `@Async` не работает | `@EnableAsync` не добавлен | Добавить `@EnableAsync` на `@Configuration` |
| Циклические события | Слушатель публикует событие, которое снова вызывает слушатель | Проверить граф событий; использовать `@Order` и guard-условие |
| Событие не обрабатывается без транзакции | `fallbackExecution = false` (по умолчанию) | Задать `fallbackExecution = true` или обернуть в `@Transactional` |
| Исключение в слушателе откатывает основную транзакцию | `@EventListener` в одной транзакции с издателем | Использовать `@Async` или `@TransactionalEventListener(AFTER_COMMIT)` |

## Spring Modulith интеграция

```java
// @ApplicationModuleListener = @TransactionalEventListener(AFTER_COMMIT) + @Async
@ApplicationModuleListener
public void onOrderPlaced(OrderPlaced event) {
    // обработка в отдельном потоке после коммита
}
```

События хранятся в таблице `event_publication` и повторяются при сбое.

