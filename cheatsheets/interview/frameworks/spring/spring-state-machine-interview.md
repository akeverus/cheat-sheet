---
title: "Spring State Machine — Interview"
description: "Вопросы на собеседовании по Spring State Machine: конфигурация, состояния, переходы, guards, actions, persistence, UML-диаграммы."
tags:
  - interview
  - spring
  - state-machine
  - fsm
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring State Machine — Interview

## Q1. Что такое State Machine и когда её применять?

**State Machine (конечный автомат)** — модель поведения, описывающая объект через набор состояний и переходов между ними, инициируемых событиями.

**Spring State Machine** — фреймворк для реализации FSM в Spring-приложениях.

Применяется когда:
- Объект имеет чётко определённые состояния (Order: PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED).
- Переходы требуют валидации (guard) и побочных эффектов (action).
- Нужно сохранять состояние между запросами (persistence).
- Бизнес-логика зависит от текущего состояния.

**Не стоит использовать** для простых enum-полей без сложных переходов — это избыточно.

## Q2. Как настроить базовую State Machine?

```xml
<dependency>
    <groupId>org.springframework.statemachine</groupId>
    <artifactId>spring-statemachine-core</artifactId>
</dependency>
```

```java
enum OrderState { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
enum OrderEvent { CONFIRM, SHIP, DELIVER, CANCEL }
```

```java
@Configuration
@EnableStateMachine
public class OrderStateMachineConfig
        extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states)
            throws Exception {
        states.withStates()
            .initial(OrderState.PENDING)
            .states(EnumSet.allOf(OrderState.class))
            .end(OrderState.DELIVERED)
            .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions)
            throws Exception {
        transitions
            .withExternal()
                .source(PENDING).target(CONFIRMED).event(CONFIRM)
                .and()
            .withExternal()
                .source(CONFIRMED).target(SHIPPED).event(SHIP)
                .and()
            .withExternal()
                .source(SHIPPED).target(DELIVERED).event(DELIVER)
                .and()
            .withExternal()
                .source(PENDING).target(CANCELLED).event(CANCEL)
                .and()
            .withExternal()
                .source(CONFIRMED).target(CANCELLED).event(CANCEL);
    }
}
```

## Q3. Как отправить событие и получить текущее состояние?

```java
@Service
@RequiredArgsConstructor
public class OrderFsmService {
    private final StateMachine<OrderState, OrderEvent> stateMachine;

    @PostConstruct
    public void init() {
        stateMachine.startReactively().subscribe();
    }

    public boolean sendEvent(OrderEvent event, Map<String, Object> variables) {
        Message<OrderEvent> message = MessageBuilder.withPayload(event)
            .setHeader("orderId", variables.get("orderId"))
            .build();

        Mono<StateMachineEventResult<OrderState, OrderEvent>> result =
            stateMachine.sendEvent(Mono.just(message));

        return result.blockFirst().getResultType() == ResultType.ACCEPTED;
    }

    public OrderState getCurrentState() {
        return stateMachine.getState().getId();
    }
}
```

## Q4. Что такое Guard и для чего он нужен?

**Guard** — условие, которое должно быть истинным для выполнения перехода. Если guard возвращает `false` — переход не происходит.

```java
@Bean
public Guard<OrderState, OrderEvent> paymentVerifiedGuard() {
    return context -> {
        String orderId = (String) context.getMessageHeader("orderId");
        return paymentService.isVerified(orderId);
    };
}

// Подключение в конфигурации
transitions
    .withExternal()
        .source(PENDING).target(CONFIRMED).event(CONFIRM)
        .guard(paymentVerifiedGuard())
        .and()
    ...
```

```java
// Условный переход с Choice State
states.withStates()
    .choice(OrderState.PAYMENT_CHECK);

transitions
    .withChoice()
        .source(PAYMENT_CHECK)
        .first(CONFIRMED, paymentVerifiedGuard())  // если оплата прошла
        .last(CANCELLED);                           // иначе — отмена
```

## Q5. Что такое Action в State Machine?

**Action** — побочный эффект, выполняемый при переходе или входе/выходе из состояния.

```java
@Bean
public Action<OrderState, OrderEvent> sendConfirmationEmail() {
    return context -> {
        String orderId = (String) context.getMessageHeader("orderId");
        emailService.sendConfirmation(orderId);
        // Сохранение данных в extended state
        context.getExtendedState().getVariables().put("confirmedAt", LocalDateTime.now());
    };
}

@Bean
public Action<OrderState, OrderEvent> updateInventory() {
    return context -> {
        String orderId = (String) context.getMessageHeader("orderId");
        inventoryService.reserveItems(orderId);
    };
}
```

```java
// Подключение action в конфигурации
transitions
    .withExternal()
        .source(PENDING).target(CONFIRMED).event(CONFIRM)
        .guard(paymentVerifiedGuard())
        .action(sendConfirmationEmail())  // при переходе
        .action(updateInventory())
        .and()

// Entry/Exit actions — выполняются при входе/выходе из состояния
states.withStates()
    .state(SHIPPED, dispatchShipmentNotification(), null)
    //           entry action ↑                  ↑ exit action
```

## Q6. Что такое Extended State?

**Extended State** — key-value хранилище, доступное в контексте State Machine. Используется для передачи данных между actions и guards без загрязнения заголовков сообщений.

```java
// Запись в Extended State
context.getExtendedState().getVariables().put("retryCount", 3);
context.getExtendedState().getVariables().put("paymentId", "PAY-123");

// Чтение в guard
public Guard<OrderState, OrderEvent> maxRetriesGuard() {
    return context -> {
        Integer retries = context.getExtendedState().get("retryCount", Integer.class);
        return retries != null && retries < 3;
    };
}
```

Extended State персистируется вместе с машиной состояний при использовании `StateMachineContext`.

## Q7. Как сохранять состояние State Machine (persistence)?

```java
// JPA Entity для сохранения состояния
@Entity
public class OrderStateMachineContext {
    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Convert(converter = ExtendedStateConverter.class)
    private Map<String, Object> extendedState;
}
```

```java
@Service
@RequiredArgsConstructor
public class PersistableOrderFsmService {
    private final StateMachinePersist<OrderState, OrderEvent, String> persist;
    private final StateMachineFactory<OrderState, OrderEvent> factory;

    public boolean processEvent(String orderId, OrderEvent event) {
        StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(orderId);

        // Восстановление состояния из хранилища
        StateMachineContext<OrderState, OrderEvent> ctx = persist.read(orderId);
        if (ctx != null) {
            sm.getStateMachineAccessor()
                .doWithAllRegions(a -> a.resetStateMachine(ctx));
        }

        sm.startReactively().block();
        boolean accepted = sendEvent(sm, orderId, event);

        // Сохранение нового состояния
        persist.write(new DefaultStateMachineContext<>(
            sm.getState().getId(),
            null,
            sm.getExtendedState().getVariables(),
            null
        ), orderId);

        return accepted;
    }
}
```

## Q8. Что такое StateMachineFactory и когда его использовать?

**StateMachineFactory** — создаёт отдельный экземпляр State Machine для каждой сущности. Используется когда каждый объект (заказ, пользователь) должен иметь свою независимую машину состояний.

```java
@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig
        extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {
    // конфигурация аналогична @EnableStateMachine

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config)
            throws Exception {
        config.withConfiguration()
            .autoStartup(false)  // не запускать автоматически
            .beanFactory(beanFactory);
    }
}
```

```java
// Создание машины для конкретного заказа
StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(orderId.toString());
sm.startReactively().block();
```

Без фабрики — один singleton `StateMachine` на всё приложение (не thread-safe для разных бизнес-объектов).

## Q9. Как реализовать иерархические состояния?

Иерархические состояния позволяют группировать состояния с общим поведением:

```java
states.withStates()
    .initial(PENDING)
    .state(PROCESSING)           // состояние-контейнер
    .and()
    .withStates()
        .parent(PROCESSING)
        .initial(PAYMENT_PENDING)
        .state(PAYMENT_CONFIRMED)
        .end(PAYMENT_FAILED);

// Переход из любого подсостояния PROCESSING
transitions
    .withExternal()
        .source(PROCESSING).target(CANCELLED).event(CANCEL);  // работает для всех подсостояний
```

**Применение**: одно событие CANCEL работает из любого подсостояния PROCESSING — не нужно дублировать переходы.

## Q10. Что такое regions (параллельные регионы)?

**Regions** — параллельные независимые подавтоматы внутри одного состояния. Используются для моделирования параллельных процессов.

```java
states.withStates()
    .initial(PROCESSING)
    .fork(FORK_STATE)
    .join(JOIN_STATE)
    .and()
    .withStates()
        .parent(PROCESSING).region("PAYMENT")
        .initial(PAY_PENDING).end(PAY_DONE)
    .and()
    .withStates()
        .parent(PROCESSING).region("INVENTORY")
        .initial(INV_PENDING).end(INV_DONE);

// Переход в JOIN_STATE только когда ОБА региона завершены
transitions
    .withFork().source(FORK_STATE).target(PROCESSING).and()
    .withJoin().source(PROCESSING).target(JOIN_STATE).and()
    .withExternal().source(JOIN_STATE).target(CONFIRMED).event(CONFIRM);
```

## Q11. Как добавить StateMachineListener?

```java
@Component
public class OrderStateMachineListener extends StateMachineListenerAdapter<OrderState, OrderEvent> {

    @Override
    public void stateChanged(State<OrderState, OrderEvent> from,
                             State<OrderState, OrderEvent> to) {
        log.info("State changed: {} → {}", from.getId(), to.getId());
    }

    @Override
    public void eventNotAccepted(Message<OrderEvent> event) {
        log.warn("Event not accepted: {}", event.getPayload());
    }

    @Override
    public void stateMachineError(StateMachine<OrderState, OrderEvent> sm, Exception e) {
        log.error("State machine error", e);
        // алертинг, метрики
    }
}

// Регистрация listener в конфигурации
@Override
public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config)
        throws Exception {
    config.withConfiguration()
        .listener(orderStateMachineListener);
}
```

## Q12. Как тестировать Spring State Machine?

```java
@SpringBootTest
class OrderStateMachineTest {

    @Autowired
    private StateMachine<OrderState, OrderEvent> stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine.startReactively().block();
    }

    @Test
    void shouldConfirmPendingOrder() {
        assertThat(stateMachine.getState().getId()).isEqualTo(PENDING);

        boolean accepted = stateMachine.sendEvent(
            Mono.just(MessageBuilder.withPayload(CONFIRM).build())
        ).blockFirst().getResultType() == ResultType.ACCEPTED;

        assertThat(accepted).isTrue();
        assertThat(stateMachine.getState().getId()).isEqualTo(CONFIRMED);
    }

    @Test
    void shouldNotShipPendingOrder() {
        boolean accepted = stateMachine.sendEvent(
            Mono.just(MessageBuilder.withPayload(SHIP).build())
        ).blockFirst().getResultType() == ResultType.ACCEPTED;

        assertThat(accepted).isFalse();
        assertThat(stateMachine.getState().getId()).isEqualTo(PENDING);
    }
}
```

## Q13. State Machine vs Saga Pattern — когда что применять?

| Критерий | State Machine | Saga Pattern |
|----------|--------------|--------------|
| Область применения | Один объект | Распределённая транзакция |
| Состояние | Локальное (в памяти/БД) | Распределённое (через события) |
| Откат | Guards/Exit actions | Компенсирующие транзакции |
| Сложность | Низкая–средняя | Средняя–высокая |
| Транзакционность | Локальная ACID | Eventual Consistency |

**State Machine** — для локальных объектов с конечным набором состояний (заказ, документ).
**Saga** — для многошаговых распределённых бизнес-процессов между несколькими сервисами.

## Q14. Как использовать @WithStateMachine для декларативной обработки событий?

```java
@WithStateMachine
@Component
public class OrderEventHandler {

    @OnTransition(target = "CONFIRMED")
    public void onOrderConfirmed(
            @EventHeaders Map<String, Object> headers,
            ExtendedState extendedState) {
        String orderId = (String) headers.get("orderId");
        log.info("Order confirmed: {}", orderId);
    }

    @OnStateEntry(target = "SHIPPED")
    public void onOrderShipped(ExtendedState state) {
        state.getVariables().put("shippedAt", LocalDateTime.now());
    }

    @OnStateExit(source = "PENDING")
    public void onPendingExit() {
        log.info("Order leaving PENDING state");
    }
}
```

`@WithStateMachine` связывает методы с событиями через аннотации, без ручной регистрации Action бинов.

## Q15. Какие типичные ошибки при работе со Spring State Machine?

1. **Singleton machine для нескольких объектов** — использовать `StateMachineFactory` вместо `@EnableStateMachine`.

2. **Игнорирование thread safety** — один экземпляр `StateMachine` не thread-safe; персистировать и восстанавливать при каждом запросе.

3. **Бизнес-логика в конфигурации** — Actions и Guards должны делегировать в сервисы, не содержать логику напрямую.

4. **Нет обработки `eventNotAccepted`** — незапринятые события теряются молча; всегда добавлять Listener.

5. **Отсутствие end-состояний** — без `end()` машина никогда не завершается и продолжает обрабатывать события.

## See also

- [[spring-state-machine|Spring State Machine]] — полный cheatsheet
- [[spring-events-interview|Spring Events Interview]] — события в Spring
- [[spring-integration-interview|Spring Integration Interview]] — enterprise интеграция
- [[saga-pattern-interview|Saga Pattern Interview]] — распределённые транзакции
- [[cqrs-event-sourcing-interview|CQRS & Event Sourcing Interview]] — event-driven архитектура
