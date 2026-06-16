---
title: "Вопросы на собеседовании: Spring State Machine"
description: "Spring State Machine (FSM): конфигурация состояний и переходов, Guards, Actions, Extended State, persistence, UML-диаграммы"
tags:
  - interview
  - spring
  - spring-state-machine-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring State Machine"
  - "Spring State Machine interview"
  - "Spring FSM interview"
prerequisites:
  - "[[spring-state-machine]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring State Machine`

`Spring State Machine` — фреймворк для реализации конечных автоматов (FSM) на Spring. Подходит там, где у объекта есть строгий жизненный цикл: бизнес-процессы (workflow), жизненный цикл заказа, согласования (approval), переходы статуса платежа. На собеседовании всплывает в контексте stateful-систем — там, где важно явно описать «какие состояния и переходы допустимы».

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring State Machine Docs](https://docs.spring.io/spring-statemachine/docs/current/reference/) — официальная документация
- [Baeldung: Spring State Machine](https://www.baeldung.com/spring-state-machine) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое State Machine и когда её применять?

**State Machine (конечный автомат, FSM)** — модель поведения, в которой объект в каждый момент находится ровно в одном из конечного набора состояний, а переходы между ними разрешены только по описанным правилам и запускаются событиями. Главная идея: вместо разбросанных по коду `if (status == ...)` поведение объекта задаётся декларативно — графом «состояние → событие → новое состояние», и невозможные переходы (например, отгрузить ещё не оплаченный заказ) просто не существуют.

**Spring State Machine** — фреймворк, который реализует эту модель в Spring-приложениях: вы описываете состояния, события, переходы, условия и побочные эффекты в `@Configuration`, а фреймворк следит за корректностью переходов.

**Когда применять.** FSM окупается, когда у объекта есть жизненный цикл со строгими правилами:
- Объект проходит через чётко определённые состояния — например, заказ: `PENDING → CONFIRMED → SHIPPED → DELIVERED`, плюс ветка `→ CANCELLED`.
- Переход допустим не всегда: его надо проверять условием (**guard**, например «оплата подтверждена») и сопровождать побочным эффектом (**action**, например «зарезервировать товар»).
- Состояние нужно хранить между запросами и перезапусками приложения (**persistence**).
- Поведение системы зависит от текущего состояния — одно и то же событие должно обрабатываться по-разному.

**Когда не стоит.** Для простого enum-поля без условных переходов и побочных эффектов FSM — избыточная инфраструктура: пара строк с `switch`/`if` понятнее и дешевле в поддержке. State Machine оправдан там, где правил переходов много и они меняются.

## Q2. Как настроить базовую State Machine?

Конфигурация состоит из трёх шагов: подключить зависимость, описать состояния и события (обычно enum'ами) и задать граф переходов. Класс-конфигуратор расширяет `StateMachineConfigurerAdapter` и переопределяет два метода `configure(...)` — один для **состояний**, второй для **переходов**.

**1. Зависимость.**

```xml
<dependency>
    <groupId>org.springframework.statemachine</groupId>
    <artifactId>spring-statemachine-core</artifactId>
</dependency>
```

**2. Состояния и события** — два enum'а: что может происходить с заказом и какие команды он принимает.

```java
enum OrderState { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
enum OrderEvent { CONFIRM, SHIP, DELIVER, CANCEL }
```

**3. Конфигурация.** `@EnableStateMachine` создаёт один bean `StateMachine`. В первом `configure` объявляются начальное состояние (`initial`), полный набор состояний и финальные (`end`) — попав в них, машина считается завершённой. Во втором `configure` каждый `withExternal()` описывает одно ребро графа: из какого состояния (`source`), в какое (`target`), по какому событию (`event`). Цепочки разделяются `.and()`.

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

Событие передаётся машине как `Message` — это позволяет приложить к нему заголовки (например, `orderId`), которые потом будут доступны в guard'ах и action'ах. Поток работы такой:

- **Запуск.** Перед первым событием машину нужно стартовать — `startReactively()` (в современном API; реактивный вызов, поэтому `.subscribe()` / `.block()`).
- **Отправка события.** `sendEvent(...)` возвращает `StateMachineEventResult`. Ключевое поле — `getResultType()`: `ACCEPTED` означает, что переход разрешён и выполнен, `DENIED` — что из текущего состояния такого перехода нет (или его заблокировал guard). Возвращаемый `boolean` — это и есть «приняла машина событие или нет».
- **Чтение состояния.** `stateMachine.getState().getId()` возвращает текущее состояние (enum-константу).

Важно: машина **не бросает исключение** на недопустимое событие — она его молча отклоняет, и узнать об этом можно только по `resultType` (см. Q11 про `eventNotAccepted`).

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

        Flux<StateMachineEventResult<OrderState, OrderEvent>> result =
            stateMachine.sendEvent(Mono.just(message));

        return result.blockFirst().getResultType() == ResultType.ACCEPTED;
    }

    public OrderState getCurrentState() {
        return stateMachine.getState().getId();
    }
}
```

## Q4. Что такое Guard и для чего он нужен?

**Guard** — булево условие, привязанное к переходу: переход выполняется, только если guard вернул `true`. Если `false` — переход блокируется, событие отклоняется (`DENIED`), машина остаётся в прежнем состоянии.

Назначение guard'а — не дать совершить недопустимый переход по бизнес-правилу. Граф переходов задаёт, *какие* переходы вообще возможны, а guard добавляет *условие*, при котором конкретный возможный переход разрешён прямо сейчас. Классический пример: переход `PENDING → CONFIRMED` существует в графе, но допустим только когда оплата подтверждена.

**Рекомендация:** guard должен только *проверять* и возвращать `true`/`false`, не меняя состояние системы. Побочные эффекты — задача action (Q5). Тяжёлую логику выносите в сервис, а в guard оставляйте делегирование (как `paymentService.isVerified(...)` ниже).

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

**Choice State** — это псевдосостояние «развилка»: машина попадает в него и тут же уходит дальше по тому переходу, чей guard сработал первым (`first` — если условие выполнено, иначе fallback-ветка `last`). Аналог `if/else` внутри графа: вместо двух отдельных переходов с инвертированными guard'ами вы получаете явную точку ветвления.

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

**Action** — это побочный эффект, который State Machine выполняет в привязке к переходу или к состоянию: отправить письмо, зарезервировать товар, записать метку времени. Если guard отвечает на вопрос «*можно* ли перейти?», то action отвечает на «*что сделать*, когда переход происходит».

Action бывает трёх видов по моменту срабатывания:
- **Transition action** — при выполнении конкретного перехода (привязывается через `.action(...)` к ребру).
- **Entry action** — при входе в состояние (первый аргумент в `.state(...)`).
- **Exit action** — при выходе из состояния (второй аргумент в `.state(...)`).

Внутри action доступен `context`: оттуда берут заголовки сообщения (`getMessageHeader`) и пишут данные в **Extended State** (`getExtendedState()`, см. Q6) — например, `confirmedAt`, чтобы передать значение последующим шагам.

**Рекомендация:** action не должен содержать бизнес-логику напрямую — пусть делегирует в сервисы (`emailService`, `inventoryService`). Так логика тестируется отдельно, а конфигурация остаётся тонкой.

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

**Extended State** — это key-value хранилище (`Map<Object, Object>`), которое живёт внутри машины и переживает переходы. Оно решает проблему: состояние (enum) описывает *где* находится объект, но не хранит *данные*, накопленные по дороге — `retryCount`, `paymentId`, `confirmedAt`. Держать всё это в самих состояниях невозможно (иначе их станет бесконечно много), поэтому переменные кладут в Extended State.

**Зачем именно оно, а не заголовки сообщения:** заголовки живут только в рамках одного события и теряются после обработки. Extended State сохраняется на всё время жизни машины и доступен любому action и guard — это естественный «общий контекст» автомата. Запись делают в action (`put`), чтение — в guard или следующем action (`get`).

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

При сохранении машины (Q7) Extended State персистируется вместе с состоянием — он входит в `StateMachineContext`, поэтому после восстановления накопленные переменные на месте.

## Q7. Как сохранять состояние State Machine (persistence)?

Машина в памяти живёт только до перезапуска приложения, а заказ обрабатывается днями и в нескольких HTTP-запросах. Поэтому для долгоживущих процессов состояние выгружают в БД и восстанавливают на каждый запрос. Абстракция для этого — `StateMachinePersist<S, E, ContextKey>`: `write(...)` сохраняет снимок, `read(...)` возвращает его.

Цикл обработки одного события выглядит так:
1. Взять (или создать) машину для конкретного `orderId` через `StateMachineFactory` (Q8).
2. Прочитать сохранённый `StateMachineContext` из хранилища и «вкатить» его в машину через `resetStateMachine(ctx)` — машина встаёт в то состояние, на котором остановилась, с тем же Extended State.
3. Стартовать машину, отправить событие.
4. Записать новый снимок (`DefaultStateMachineContext` из текущего состояния и переменных) обратно в хранилище.

Снимок хранится как JPA-сущность: `state` (enum) плюс сериализованный Extended State (`@Convert`). Ключ — бизнес-идентификатор (`orderId`).

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

**StateMachineFactory** — фабрика, которая по запросу создаёт **новый независимый экземпляр** машины (`factory.getStateMachine(id)`). Включается аннотацией `@EnableStateMachineFactory` вместо `@EnableStateMachine`.

Зачем это нужно. `@EnableStateMachine` даёт **один singleton** `StateMachine` на всё приложение. Это нормально, если автомат один (например, конфигуратор UI-визарда). Но когда у каждого заказа свой жизненный цикл, singleton не подходит: машина хранит *текущее* состояние внутри себя, и два заказа, шарящие один экземпляр, будут затирать состояние друг друга, да и параллельные запросы (один экземпляр не thread-safe) дадут гонки.

**Правило:** один бизнес-объект — одна машина. Для каждого `orderId` берёте у фабрики отдельный экземпляр, восстанавливаете его состояние из БД (Q7), обрабатываете событие, сохраняете обратно. Часто фабрику настраивают с `autoStartup(false)`, чтобы стартовать машину вручную уже после загрузки состояния.

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

Итог: без фабрики — один singleton `StateMachine` на всё приложение, не thread-safe и не годится для нескольких бизнес-объектов; с фабрикой — по экземпляру на сущность.

## Q9. Как реализовать иерархические состояния?

**Иерархические (вложенные) состояния** — это состояние-контейнер с собственными подсостояниями внутри. Их используют, чтобы вынести общее поведение группы подсостояний на уровень родителя и не дублировать переходы.

Как устроено: объявляете родительское состояние (`PROCESSING`), а затем во втором `withStates()` через `.parent(PROCESSING)` описываете его внутренний автомат — со своим `initial` и `end`. Внешняя машина «видит» `PROCESSING` как одно состояние, но внутри него идёт собственная под-жизнь (`PAYMENT_PENDING → PAYMENT_CONFIRMED`).

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

**Выгода:** переход `PROCESSING → CANCELLED` по событию `CANCEL`, объявленный на родителе, срабатывает из *любого* его подсостояния. Без иерархии пришлось бы прописывать отмену для `PAYMENT_PENDING`, `PAYMENT_CONFIRMED` и каждого нового подсостояния отдельно — общее правило поднимается на уровень контейнера.

## Q10. Что такое regions (параллельные регионы)?

**Regions** — несколько независимых подавтоматов, работающих *одновременно* внутри одного составного состояния. Если иерархия (Q9) — это «в один момент времени мы в одном из подсостояний», то регионы — «мы одновременно в нескольких ветках сразу». Это способ смоделировать параллельные подпроцессы одного объекта.

Пример: внутри `PROCESSING` заказ одновременно проходит оплату (регион `PAYMENT`) и резервирование товара (регион `INVENTORY`) — это два самостоятельных автомата.

Управляют параллелизмом два псевдосостояния:
- **fork** — расщепляет один поток на несколько регионов (запускает их параллельно).
- **join** — синхронизирует: переход за `join` срабатывает только когда **все** регионы дошли до своих финальных состояний (`PAY_DONE` и `INV_DONE`).

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

`StateMachineListener` — единая точка наблюдения за жизнью машины: переходы, ошибки, отклонённые события. Удобно расширять `StateMachineListenerAdapter` и переопределять только нужные методы. Типичные применения — логирование и метрики переходов, алертинг на ошибки и, что особенно важно, отлов **отклонённых событий**.

Ключевые колбэки:
- `stateChanged(from, to)` — каждый успешный переход (хорошая точка для аудита/метрик).
- `eventNotAccepted(event)` — событие, которое машина отвергла (нет такого перехода или его заблокировал guard). Без этого колбэка такие события теряются молча — это одна из главных ловушек (см. Q15).
- `stateMachineError(sm, e)` — исключение внутри обработки; здесь уместны алерт и метрика.

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

Машину тестируют как чёрный ящик: отправляют событие и проверяют два факта — приняла ли его машина (`resultType == ACCEPTED`) и в какое состояние перешла (`getState().getId()`). Стоит покрывать обе ветки: **happy path** (валидный переход прошёл) и **negative path** (недопустимое событие отклонено, состояние не изменилось).

Практика:
- Перед каждым тестом стартуйте машину (`startReactively().block()`), иначе события не обработаются.
- Если в проекте `StateMachineFactory`, берите свежий экземпляр на тест — singleton накапливает состояние между тестами и делает их зависимыми друг от друга.

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

Главное различие — **масштаб границы**. State Machine управляет жизненным циклом **одного объекта внутри одного сервиса**: переходы локальны, состояние хранится тут же, целостность обеспечивает обычная ACID-транзакция. Saga координирует **многошаговый процесс между несколькими сервисами**: единой транзакции нет, согласованность — eventual, а откат делается не rollback'ом, а компенсирующими действиями (например, «отменить резерв», «вернуть платёж»).

Они не конкурируют, а часто работают вместе: saga оркестрирует распределённый процесс, а State Machine внутри каждого сервиса отслеживает локальное состояние шага.

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

`@WithStateMachine` — аннотация на bean'е, которая позволяет вешать обработчики прямо на методы, без ручной регистрации Action-бинов в графе переходов. Метод помечается аннотацией момента жизненного цикла, и Spring сам вызывает его в нужный момент:
- `@OnTransition(target = "...")` — при переходе в указанное состояние (можно задавать и `source`).
- `@OnStateEntry(target = "...")` / `@OnStateExit(source = "...")` — при входе в состояние / выходе из него.

В параметры метода можно инжектировать контекст: `@EventHeaders Map<String,Object>` — заголовки события, `ExtendedState` — общее хранилище машины (Q6).

**Когда удобно:** обработка событий читается рядом с доменной логикой, а не растворена в конфигурации. **Компромисс:** связь «состояние → метод» задаётся строковыми именами в аннотациях, поэтому такой код хрупче к опечаткам и переименованиям, чем явная привязка action к переходу через `.action(...)`.

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

Итог: `@WithStateMachine` связывает методы с моментами жизненного цикла машины через аннотации — без отдельных Action-бинов и ручной привязки их к переходам.

## Q15. Какие типичные ошибки при работе со Spring State Machine?

Большинство граблей сводятся к тому, что машина — это объект с *изменяемым внутренним состоянием*, и про это забывают.

1. **Один singleton-экземпляр на несколько объектов.** Заказы начинают делить одну машину и затирать состояние друг друга. Решение — `StateMachineFactory` вместо `@EnableStateMachine`: по экземпляру на сущность (Q8).

2. **Игнорирование thread safety.** Один экземпляр `StateMachine` не thread-safe — параллельные запросы дадут гонки. Подход: на каждый запрос восстанавливать состояние из БД, обрабатывать событие, сохранять обратно (Q7).

3. **Бизнес-логика прямо в конфигурации.** Когда Actions и Guards содержат логику внутри, её невозможно протестировать отдельно, а конфигурация распухает. Они должны лишь делегировать в сервисы.

4. **Нет обработки `eventNotAccepted`.** Машина не бросает исключение на недопустимое событие — она молча его отклоняет, и баг остаётся незамеченным. Всегда вешайте Listener и логируйте/метрите отклонённые события (Q11).

5. **Отсутствие end-состояний.** Без `end()` машина формально никогда не завершается и продолжает принимать события даже после логического конца процесса. Явно помечайте терминальные состояния.

## See also

- [Spring Events](spring-events-interview.md) — ApplicationEvents, альтернатива для простых случаев
- [Spring Integration](spring-integration-interview.md) — EIP, роутинг как альтернативный подход
- [Saga Pattern](../../architecture/saga-pattern-interview.md) — распределённые транзакции, использование state machines
- [CQRS & Event Sourcing](../../architecture/cqrs-event-sourcing-interview.md) — event-driven архитектура для сложных workflow
- [Domain-Driven Design](../../architecture/ddd-interview.md) — aggregate root как контекст state machine
- [Spring Framework](spring-framework-interview.md) — основа Spring Boot
- [Spring @Transactional](spring-transaction-interview.md) — транзакции при переходах
- [Spring Modulith](spring-modulith-interview.md) — модульный подход к workflow
- [Spring Batch](spring-batch-interview.md) — batch jobs с state-driven пошаговой обработкой
- [Microservices](../../architecture/microservices-interview.md) — state machines в межсервисной координации
