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
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring State Machine`

`Spring State Machine` — фреймворк для реализации конечных автоматов (FSM) на Spring. Подходит для моделирования workflow, order lifecycle, approval processes, payment state transitions. Спрашивается в контексте stateful систем.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring State Machine Docs](https://docs.spring.io/spring-statemachine/docs/current/reference/) — официальная документация
- [Baeldung: Spring State Machine](https://www.baeldung.com/spring-state-machine) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое State Machine и когда её применять?

**State Machine (конечный автомат)** — модель поведения, описывающая объект через набор состояний и переходов между ними, инициируемых событиями.

**Spring State Machine** — фреймворк для реализации FSM в Spring-приложениях.

Применяется когда:
- Объект имеет чётко определённые состояния (Order: PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED).
- Переходы требуют валидации (guard) и побочных эффектов (action).
- Нужно сохранять состояние между запросами (persistence).
- Бизнес-логика зависит от текущего состояния.

**Не стоит использовать** для простых enum-полей без сложных переходов — это избыточно.


> [!mcq]
>
> **Вопрос:** Когда уместно применять Spring State Machine, а когда лучше обойтись без неё?
>
> ---
>
> #### A) SSM — это лёгкая замена workflow-движкам типа Camunda/Activiti, поэтому её нужно брать для любого мульти-шагового бизнес-процесса с BPMN. — ❌ Неверно
>
> **Что на самом деле:** Camunda/Activiti — это полноценные workflow-движки с BPMN, исполнителем, формами, историей, human tasks и встроенной транзакционной персистенцией. SSM — это in-memory FSM-фреймворк без BPMN, без задачек на людей, без UI; персистенция — отдельный модуль и пишется руками. Для длинных бизнес-процессов с человеческими шагами SSM не подходит.
>
> **Откуда путаница:** обе технологии «про процессы», и в туториалах SSM часто рисуют как «легковесную замену BPMN».
>
> **Если бы это было правдой:** команды брали бы SSM для onboarding/KYC, а потом упирались бы в отсутствие персистенции истории, отката шагов и UI для бизнеса — переписывали бы проект на Camunda.
>
> ---
>
> #### B) SSM нужна только когда состояний у объекта больше десяти и переходы описываются UML state diagram. — ❌ Неверно
>
> **Что на самом деле:** количество состояний не критерий. Уже 4-5 состояний с guard-ами, action-ами и end-состояниями оправдывают SSM. Критерий — сложность переходов, наличие условий и побочных эффектов, а не размер enum.
>
> **Откуда путаница:** примеры в документации часто показывают большие state diagrams, и это создаёт впечатление, что SSM нужна только для «больших» автоматов.
>
> **Если бы это было правдой:** простую модель Order(PENDING→CONFIRMED→SHIPPED→DELIVERED) с guard `paymentVerified` пришлось бы писать вручную через if/else, что даёт хаос из условий и побочных эффектов.
>
> ---
>
> #### C) SSM применяется, когда у объекта есть чёткий набор состояний, переходы требуют валидации (guard) и побочных эффектов (action), а текущее состояние нужно сохранять между запросами. — ✓ Верно
>
> **Развёрнутое объяснение:** SSM — это in-memory конечный автомат на Spring. Она формализует то, что обычно превращается в кашу из `if (status == ...)`: набор состояний, события-триггеры, условия перехода (`Guard`), побочные эффекты (`Action`) и end-состояния. Бизнес-логика, которая «зависит от текущего состояния» (Order, Document, Payment, KYC), естественно ложится на FSM. Для длительного хранения состояние сериализуется через `StateMachinePersister` (JDBC, Redis), а сам экземпляр восстанавливается по запросу.
>
> **Пример:**
> ```java
> // Объект имеет конечный набор состояний и валидируемые переходы
> enum OrderState { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
> enum OrderEvent { CONFIRM, SHIP, DELIVER, CANCEL }
>
> // CONFIRM разрешён только если оплата подтверждена (guard)
> // и должен отправить email + резервировать товар (actions)
> transitions.withExternal()
>     .source(PENDING).target(CONFIRMED).event(CONFIRM)
>     .guard(paymentVerifiedGuard())
>     .action(sendConfirmationEmail());
> ```
>
> **Когда применять:** order lifecycle, document approval, payment workflow, KYC pipeline, subscription state, любые domain-объекты с конечным набором состояний и нетривиальными переходами.
>
> **Подводные камни:** для простого `enum status` без guards/actions SSM — overkill; для распределённых процессов через несколько сервисов лучше Saga; не использовать SSM как очередь сообщений.
>
> ---
>
> #### D) SSM нужна только для приложений на WebFlux, потому что использует Reactor для `sendEvent`. — ❌ Неверно
>
> **Что на самом деле:** SSM поддерживает и блокирующий, и реактивный API. Метод `sendEvent` возвращает `Flux<StateMachineEventResult>`, но его можно `.blockFirst()` в обычном MVC-приложении. Архитектура не привязана к WebFlux.
>
> **Откуда путаница:** в SSM 3.x reactive API стал основным, и в новых примерах часто видно `Mono`/`Flux`. Но это просто API-обёртка, а не требование к стеку.
>
> **Если бы это было правдой:** команды на Spring MVC отказывались бы от SSM из-за «реактивности», возвращаясь к ручным `switch (status)` цепочкам, теряя guards/actions.
>
> ---
>
> **Связанные вопросы:** [[Q2]] — конфигурация состояний и переходов; [[Q13]] — SSM vs Saga Pattern для распределённых процессов.

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


> [!mcq]
>
> **Вопрос:** Что обязательно нужно для корректной базовой конфигурации Spring State Machine на enum-состояниях?
>
> ---
>
> #### A) Объявить `@EnableStateMachine` на конфигурационном классе, наследоваться от `StateMachineConfigurerAdapter` и описать states + transitions в `configure(...)` методах. — ✓ Верно
>
> **Развёрнутое объяснение:** `@EnableStateMachine` создаёт singleton-бин `StateMachine<S,E>` в контексте. Класс `StateMachineConfigurerAdapter<S,E>` предоставляет три точки настройки: states (initial/states/end), transitions (external/internal/local через withExternal()/...) и configuration (autoStartup, listener, taskExecutor). Без `initial(...)` машина не стартует; без `end(...)` она никогда не завершится и продолжит принимать события. Зависимость `spring-statemachine-core` подтягивается отдельно — она не входит в spring-boot starters.
>
> **Пример:**
> ```java
> @Configuration
> @EnableStateMachine
> public class OrderStateMachineConfig
>         extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {
>
>     @Override
>     public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states)
>             throws Exception {
>         states.withStates()
>             .initial(OrderState.PENDING)
>             .states(EnumSet.allOf(OrderState.class))
>             .end(OrderState.DELIVERED)
>             .end(OrderState.CANCELLED);
>     }
>
>     @Override
>     public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions)
>             throws Exception {
>         transitions
>             .withExternal().source(PENDING).target(CONFIRMED).event(CONFIRM).and()
>             .withExternal().source(CONFIRMED).target(SHIPPED).event(SHIP);
>     }
> }
> ```
>
> **Когда применять:** один глобальный автомат на приложение (например, для синглетного workflow). Для per-entity автоматов нужен `@EnableStateMachineFactory` — см. Q8.
>
> **Подводные камни:** `@EnableStateMachine` создаёт singleton — не подходит для параллельной работы с разными бизнес-объектами; забыли `end(...)` — машина «вечная»; не подключили starter — `NoSuchBeanDefinitionException` при инжекте `StateMachine`.
>
> ---
>
> #### B) Достаточно положить enum-ы `OrderState` и `OrderEvent` в classpath — SSM подберёт их по соглашению имён. — ❌ Неверно
>
> **Что на самом деле:** SSM не использует convention-over-configuration для enum-ов. Без `StateMachineConfigurerAdapter` и описания transitions фреймворк не знает, какие переходы разрешены, какое initial-состояние и какие end. Это приводит к стартовой ошибке или к машине без переходов.
>
> **Откуда путаница:** Spring Data JPA автоматически биндит сущности через `@Entity` — отсюда ложная аналогия.
>
> **Если бы это было правдой:** любой enum в classpath становился бы автоматом, и любое переименование класса ломало бы бизнес-логику.
>
> ---
>
> #### C) Нужно явно объявить `@Bean StateMachine<S,E>` с new `DefaultStateMachine` и руками регистрировать transitions через `addTransition()`. — ❌ Неверно
>
> **Что на самом деле:** прямое создание `DefaultStateMachine` обходит конфигурацию через `StateMachineConfigurerAdapter`, ломает интеграцию с `@WithStateMachine`, listener-механизмом и persister-ами. Это low-level API, который не предназначен для прикладного использования.
>
> **Откуда путаница:** в исходниках SSM встречается `DefaultStateMachine` — и пользователи копируют его в свой код.
>
> **Если бы это было правдой:** пришлось бы вручную писать boilerplate для каждого `withExternal()`, теряя type-safe builder и валидацию конфига при старте.
>
> ---
>
> #### D) Конфигурация делается через YAML-файл `statemachine.yml` в `src/main/resources`. — ❌ Неверно
>
> **Что на самом деле:** SSM не поддерживает YAML-описание автоматов «из коробки». Конфигурация — через Java DSL (`StateMachineConfigurerAdapter`) либо через UML (SCXML-файл с расширением `.uml`, отдельный builder). YAML-формата нет.
>
> **Откуда путаница:** многие Spring-модули конфигурируются через `application.yml`, и это формирует ожидание.
>
> **Если бы это было правдой:** изменения в state diagram не требовали бы пересборки — но валидация графа стала бы runtime-only, и опечатки ловились бы только в production.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — отправка событий и получение состояния; [[Q8]] — StateMachineFactory для per-entity автоматов.

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


> [!mcq]
>
> **Вопрос:** Какой способ корректно отправляет событие в Spring State Machine 3.x и проверяет, принято ли оно?
>
> ---
>
> #### A) Вызвать `stateMachine.sendEvent(OrderEvent.CONFIRM)` напрямую — метод вернёт `boolean` accepted. — ❌ Неверно
>
> **Что на самом деле:** в SSM 3.x синхронный метод `sendEvent(E)` помечен как `@Deprecated`. Основной API — реактивный: `sendEvent(Mono<Message<E>>)` возвращает `Flux<StateMachineEventResult<S,E>>`. Передавать просто enum нельзя — нужен `Message<E>`, чтобы прокинуть headers (например, `orderId`).
>
> **Откуда путаница:** в SSM 2.x был синхронный `sendEvent(E)` — старые туториалы и StackOverflow-ответы до сих пор так пишут.
>
> **Если бы это было правдой:** не получилось бы прокинуть `orderId` через headers, и actions/guards не имели бы доступа к контексту бизнес-объекта.
>
> ---
>
> #### B) Построить `Message<OrderEvent>` через `MessageBuilder.withPayload()` с нужными headers, отправить через `stateMachine.sendEvent(Mono.just(message))`, проверить `getResultType() == ACCEPTED`. — ✓ Верно
>
> **Развёрнутое объяснение:** реактивный API SSM 3.x требует `Mono<Message<E>>`. Headers сообщения доступны в guards/actions через `context.getMessageHeader(key)` — это стандартный способ прокидывать `orderId`, `userId`, корреляционные ID. Результат `StateMachineEventResult.ResultType` принимает три значения: `ACCEPTED` (переход выполнен), `DENIED` (guard вернул false / нет такого перехода), `DEFERRED` (событие отложено). Проверка `getResultType()` — единственный надёжный способ узнать, прошёл ли переход.
>
> **Пример:**
> ```java
> Message<OrderEvent> message = MessageBuilder.withPayload(OrderEvent.CONFIRM)
>     .setHeader("orderId", orderId)
>     .build();
>
> StateMachineEventResult<OrderState, OrderEvent> result =
>     stateMachine.sendEvent(Mono.just(message)).blockFirst();
>
> if (result.getResultType() == ResultType.ACCEPTED) {
>     log.info("Переход принят, новое состояние: {}", stateMachine.getState().getId());
> } else {
>     log.warn("Событие отклонено: {}", result.getResultType());
> }
> ```
>
> **Когда применять:** любой production-код на SSM 3.x; обязательно проверять `getResultType()`, иначе молчаливые отказы потеряются.
>
> **Подводные камни:** `.blockFirst()` блокирует поток — в WebFlux лучше `.next()` и асинхронная подписка; без headers actions не получат контекст; `getState().getId()` для иерархических машин вернёт лист, а не родителя.
>
> ---
>
> #### C) Создать новый `StateMachineEventTrigger` на каждое событие и пушить его в `EventQueue.offer(trigger)`. — ❌ Неверно
>
> **Что на самом деле:** `StateMachineEventTrigger` — внутренний класс SSM, не предназначенный для прямого использования. Прикладной API — только `sendEvent(Mono<Message<E>>)`. Очереди событий внутри машины управляются автоматически.
>
> **Откуда путаница:** в IDE автокомплит может показать internal-классы из пакетов `*.support.*` — пользователи думают, что это публичный API.
>
> **Если бы это было правдой:** обновление SSM ломало бы код при каждом minor-релизе, так как internal API меняется без warnings.
>
> ---
>
> #### D) Использовать `ApplicationEventPublisher.publishEvent(orderEvent)` — Spring сам найдёт state machine и применит событие. — ❌ Неверно
>
> **Что на самом деле:** Spring ApplicationEvents — независимый pub/sub-механизм, никак не связанный с SSM. Машина состояний не подписывается на `ApplicationEventPublisher` автоматически; событие просто никуда не попадёт.
>
> **Откуда путаница:** оба механизма называются «events», и Spring продвигает event-driven архитектуру через `@EventListener`.
>
> **Если бы это было правдой:** state machine реагировала бы на все события приложения подряд, включая `ContextRefreshedEvent`, что привело бы к хаотичным переходам.
>
> ---
>
> **Связанные вопросы:** [[Q4]] — Guards для условных переходов; [[Q11]] — Listener для отслеживания `eventNotAccepted`.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Что такое Action в State Machine? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое Extended State? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как сохранять состояние State Machine (persistence)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Что такое StateMachineFactory и когда его использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как реализовать иерархические состояния? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Что такое regions (параллельные регионы)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как добавить StateMachineListener? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Как тестировать Spring State Machine? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. State Machine vs Saga Pattern — когда что применять? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Критерий | State Machine | Saga Pattern |
|----------|--------------|--------------|
| Область применения | Один объект | Распределённая транзакция |
| Состояние | Локальное (в памяти/БД) | Распределённое (через события) |
| Откат | Guards/Exit actions | Компенсирующие транзакции |
| Сложность | Низкая–средняя | Средняя–высокая |
| Транзакционность | Локальная ACID | Eventual Consistency |

**State Machine** — для локальных объектов с конечным набором состояний (заказ, документ).
**Saga** — для многошаговых распределённых бизнес-процессов между несколькими сервисами.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как использовать @WithStateMachine для декларативной обработки событий? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Какие типичные ошибки при работе со Spring State Machine? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Singleton machine для нескольких объектов** — использовать `StateMachineFactory` вместо `@EnableStateMachine`.

2. **Игнорирование thread safety** — один экземпляр `StateMachine` не thread-safe; персистировать и восстанавливать при каждом запросе.

3. **Бизнес-логика в конфигурации** — Actions и Guards должны делегировать в сервисы, не содержать логику напрямую.

4. **Нет обработки `eventNotAccepted`** — незапринятые события теряются молча; всегда добавлять Listener.

5. **Отсутствие end-состояний** — без `end()` машина никогда не завершается и продолжает обрабатывать события.

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring Events](spring-events-interview.md) — ApplicationEvents, альтернатива для простых случаев ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring Integration](spring-integration-interview.md) — EIP, роутинг как альтернативный подход
- [Saga Pattern](../../architecture/saga-pattern-interview.md) — распределённые транзакции, использование state machines
- [CQRS & Event Sourcing](../../architecture/cqrs-event-sourcing-interview.md) — event-driven архитектура для сложных workflow
- [Domain-Driven Design](../../architecture/ddd-interview.md) — aggregate root как контекст state machine
- [Spring Framework](spring-framework-interview.md) — основа Spring Boot
- [Spring @Transactional](spring-transaction-interview.md) — транзакции при переходах
- [Spring Modulith](spring-modulith-interview.md) — модульный подход к workflow
- [Spring Batch](spring-batch-interview.md) — batch jobs с state-driven пошаговой обработкой
- [Microservices](../../architecture/microservices-interview.md) — state machines в межсервисной координации
