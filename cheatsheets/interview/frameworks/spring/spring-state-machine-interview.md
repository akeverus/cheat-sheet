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
>
> **Вопрос:** Чем Guard принципиально отличается от Action в Spring State Machine и в какой момент он вызывается?
>
> ---
>
> #### A) Guard вызывается ПОСЛЕ выполнения action и может откатить переход, если что-то пошло не так. — ❌ Неверно
>
> **Что на самом деле:** Guard вызывается СНАЧАЛА — до того, как переход будет применён, до actions. Если guard вернул `false`, никаких побочных эффектов не происходит, машина остаётся в старом состоянии, а событие фиксируется как `DENIED` (или `eventNotAccepted` через listener). После запуска action отката нет — нужно компенсирующее событие.
>
> **Откуда путаница:** в обычном Java-коде `try-catch` после побочного эффекта возвращает состояние назад. По аналогии кажется, что guard работает как post-check.
>
> **Если бы это было правдой:** action `sendConfirmationEmail()` сначала отправил бы письмо клиенту, а потом guard сказал бы «не подтверждать заказ» — клиент получил бы письмо о подтверждении несуществующего заказа.
>
> ---
>
> #### B) Guard — это `Function<StateContext, Boolean>`, вызывается ДО перехода; если возвращает `false` — переход не происходит, actions не выполняются, событие DENIED. — ✓ Верно
>
> **Развёрнутое объяснение:** Guard — условие на переход. Сигнатура: `Guard<S,E>` = `boolean evaluate(StateContext<S,E> context)`. Машина при получении события находит подходящий transition, проверяет guard и только если он `true` — выполняет actions и меняет состояние. Это первичный механизм enforce-инвариантов: «отгружать только оплаченные заказы», «отменить только pending», «approve только при наличии прав». Guard ДОЛЖЕН быть pure-функцией без побочных эффектов; вся работа — в actions.
>
> **Пример:**
> ```java
> @Bean
> public Guard<OrderState, OrderEvent> paymentVerifiedGuard() {
>     return context -> {
>         String orderId = (String) context.getMessageHeader("orderId");
>         return paymentService.isVerified(orderId);  // только чтение, без side effects
>     };
> }
>
> transitions.withExternal()
>     .source(PENDING).target(CONFIRMED).event(CONFIRM)
>     .guard(paymentVerifiedGuard())   // CONFIRM пройдёт только при оплаченном заказе
>     .action(sendConfirmationEmail()); // выполнится только если guard = true
> ```
>
> **Когда применять:** валидация инвариантов перед переходом, проверка прав, бизнес-условий, дедупликация событий, choice pseudo-state с условным роутингом.
>
> **Подводные камни:** побочные эффекты внутри guard приводят к недетерминированному поведению; throw из guard расценивается как ошибка машины (не как false) — лучше catch внутри и вернуть false; guard, обращающийся к БД, — узкое место производительности.
>
> ---
>
> #### C) Guard блокирует выполнение action, но переход состояния всё равно происходит — это просто фильтр для побочных эффектов. — ❌ Неверно
>
> **Что на самом деле:** guard блокирует ВЕСЬ переход целиком — и actions, и смену состояния. Если нужно «перейти, но не делать действие» — это разные actions на разных transitions, а не один guard.
>
> **Откуда путаница:** в Spring AOP `@Conditional` или `@ConditionalOnProperty` блокируют отдельный bean, не весь контекст.
>
> **Если бы это было правдой:** при отказе guard PENDING→CONFIRMED заказ всё равно перешёл бы в CONFIRMED, но без email — а на следующем шаге машина попыталась бы SHIP неоплаченный заказ.
>
> ---
>
> #### D) Guard — это аннотация `@PreAuthorize` на методе action, проверяющая права доступа через Spring Security. — ❌ Неверно
>
> **Что на самом деле:** Guard — это бин `Guard<S,E>`, никак не связанный со Spring Security. Можно внутри guard проверять `SecurityContextHolder`, но это руками. `@PreAuthorize` к state machine не применяется автоматически.
>
> **Откуда путаница:** концепт «pre-check перед действием» одинаков, и слово guard в IT часто ассоциируется с security.
>
> **Если бы это было правдой:** для бизнес-инвариантов (paymentVerified, inventoryAvailable) пришлось бы изобретать `@PreAuthorize("hasPaymentVerified()")` — что заведомо громоздко.
>
> ---
>
> **Связанные вопросы:** [[Q5]] — Action как side effect перехода; [[Q11]] — Listener для отслеживания `eventNotAccepted` от guard.

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


> [!mcq]
>
> **Вопрос:** Какие виды Action существуют в Spring State Machine и в какой момент каждый вызывается?
>
> ---
>
> #### A) Существует только один тип action — на transition; entry/exit actions достигаются ручной проверкой состояния внутри transition action. — ❌ Неверно
>
> **Что на самом деле:** SSM поддерживает несколько видов action из коробки: **transition action** (при переходе), **entry action** (при входе в состояние), **exit action** (при выходе из состояния), **state-do action** (выполняется пока машина в состоянии). Их разделение — фундаментальная часть UML state diagrams.
>
> **Откуда путаница:** в простых примерах часто показывают только transition action — и кажется, что больше ничего нет.
>
> **Если бы это было правдой:** «при входе в SHIPPED отправить notification» пришлось бы дублировать на каждом transition, ведущем в SHIPPED — нарушение DRY.
>
> ---
>
> #### B) Transition action нельзя получить доступ к headers сообщения — для этого нужен только Guard. — ❌ Неверно
>
> **Что на самом деле:** action получает тот же `StateContext`, что и guard. Доступ к `context.getMessageHeader(key)` и `context.getExtendedState()` есть из обоих. Разница только в семантике: guard читает и возвращает boolean, action выполняет побочный эффект.
>
> **Откуда путаница:** в туториалах guards часто читают headers, а actions просто пишут лог — формируется впечатление разделения по доступу.
>
> **Если бы это было правдой:** action не смог бы получить `orderId` из headers, и пришлось бы дублировать данные через Extended State перед каждым переходом.
>
> ---
>
> #### C) Action — это `Consumer<StateContext>`, выполняется ПОСЛЕ guard и ДО смены состояния; бывает transition, entry, exit, state-do; ошибка action может прервать переход. — ✓ Верно
>
> **Развёрнутое объяснение:** Action — побочный эффект перехода. Сигнатура: `Action<S,E>` = `void execute(StateContext<S,E> context)`. Последовательность при transition: guard → exit action источника → transition action → entry action цели → событие listener `stateChanged`. Exception в action попадает в `stateMachineError` listener; машина переходит в error state. Транзакционность action не управляется SSM — это ответственность разработчика (`@Transactional` на сервисе, в который делегирует action). Entry/exit actions объявляются в `withStates().state(STATE, entryAction, exitAction)`.
>
> **Пример:**
> ```java
> @Bean
> public Action<OrderState, OrderEvent> sendConfirmationEmail() {
>     return context -> {
>         String orderId = (String) context.getMessageHeader("orderId");
>         emailService.sendConfirmation(orderId);
>         context.getExtendedState().getVariables().put("confirmedAt", Instant.now());
>     };
> }
>
> // Entry action SHIPPED — выполнится при любом transition в SHIPPED
> states.withStates()
>     .state(SHIPPED, dispatchShipmentNotification(), null);
>     //              entry ↑                          ↑ exit
>
> // Transition action — выполнится только на конкретном переходе
> transitions.withExternal()
>     .source(PENDING).target(CONFIRMED).event(CONFIRM)
>     .action(sendConfirmationEmail())
>     .action(updateInventory());  // цепочка actions выполняется по порядку
> ```
>
> **Когда применять:** отправка нотификаций, запись audit log, обновление связанных систем, метрик. Транзакционные effects — обернуть `@Transactional` в делегируемом сервисе.
>
> **Подводные камни:** action не идемпотентен по умолчанию — при retry/recovery будет выполнен повторно; exception прерывает chain actions; нельзя полагаться на порядок отправки писем и записи в БД без transactional outbox.
>
> ---
>
> #### D) Action автоматически выполняется в транзакции `@Transactional(REQUIRES_NEW)` — SSM управляет границами транзакций. — ❌ Неверно
>
> **Что на самом деле:** SSM сама не открывает транзакций. Если action пишет в БД, нужно либо `@Transactional` на сервисе, в который делегирует action, либо явно использовать `TransactionTemplate`. По умолчанию каждое обращение к БД из action — auto-commit.
>
> **Откуда путаница:** Spring обильно использует декларативные транзакции, и пользователи ожидают, что любой Spring-фреймворк автоматически их даёт.
>
> **Если бы это было правдой:** запись audit log и отправка email атомарно либо обе откатывались, либо обе фиксировались — но без явного управления это иллюзия консистентности.
>
> ---
>
> **Связанные вопросы:** [[Q4]] — Guard как pre-condition; [[Q6]] — Extended State для передачи данных между actions.

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


> [!mcq]
>
> **Вопрос:** Чем Extended State отличается от обычных переменных бина и зачем он нужен, если можно просто хранить данные в `@Entity` сущности заказа?
>
> ---
>
> #### A) Extended State — это поле `Map` внутри `StateMachine`, которое не сохраняется при persistence и существует только в RAM до рестарта. — ❌ Неверно
>
> **Что на самом деле:** Extended State (точнее, его `getVariables()`) сериализуется в `StateMachineContext` вместе с текущим состоянием при сохранении через `StateMachinePersister`/`StateMachinePersist`. При восстановлении машины через `resetStateMachine(ctx)` переменные восстанавливаются обратно.
>
> **Откуда путаница:** в первых туториалах часто показывают Extended State без persistence, и кажется, что это in-memory кэш.
>
> **Если бы это было правдой:** после рестарта приложения retry-счётчик из Extended State обнулился бы, и заказ мог снова получить уже отвергнутый платёж.
>
> ---
>
> #### B) Extended State — key-value хранилище в `StateContext`, видимое всем guard'ам и action'ам одной машины, сериализуется через `StateMachineContext` и используется для данных, релевантных только текущему workflow (retry count, временные флаги). — ✓ Верно
>
> **Развёрнутое объяснение:** Extended State — это `Map<Object, Object>` в составе `StateMachineContext<S,E>`. Доступ единый из guard, action, listener: `context.getExtendedState().getVariables()`. Используется для данных, которые нужны только в рамках жизненного цикла FSM (счётчик retry, флаг ручного override, последняя ошибка платежа). При персистенции `DefaultStateMachineContext` принимает Map переменных и сохраняет вместе с состоянием. Это позволяет хранить специфичные для workflow данные отдельно от доменной сущности `Order`, не загрязняя её колонками типа `retryCount`, `lastPaymentError`. По UML-нотации это и есть "extended state" — расширение конечного state набором переменных.
>
> **Пример:**
> ```java
> // Запись в action
> @Bean
> public Action<OrderState, OrderEvent> incrementRetry() {
>     return context -> {
>         Map<Object, Object> vars = context.getExtendedState().getVariables();
>         vars.merge("retryCount", 1, (a, b) -> ((Integer) a) + 1);
>     };
> }
>
> // Чтение в guard
> @Bean
> public Guard<OrderState, OrderEvent> maxRetriesGuard() {
>     return context -> {
>         Integer retries = context.getExtendedState().get("retryCount", Integer.class);
>         return retries == null || retries < 3;
>     };
> }
> ```
>
> **Когда применять:** retry counters в payment processing; временные флаги (`manualOverride=true`); кэш промежуточных результатов между guard и action; данные, которые нет смысла выносить в доменную модель.
>
> **Подводные камни:** значения должны быть сериализуемы (для JDBC/Redis persister); коллизии ключей при больших workflow — заводить отдельный enum для key namespace; нельзя класть туда тяжёлые объекты (Session, Connection); очистка переменных при достижении end-state не происходит автоматически.
>
> ---
>
> #### C) Extended State дублирует функциональность `@Entity` сущности — это просто in-memory кэш над колонками БД, и его нужно держать синхронным с доменной моделью. — ❌ Неверно
>
> **Что на самом деле:** Extended State и доменная сущность решают разные задачи. Доменная сущность хранит бизнес-данные (сумма, товары, адрес). Extended State хранит метаданные workflow (retry count, флаги). Их синхронизация — не требование, а часто антипаттерн: смешивание ответственностей и двойная запись.
>
> **Откуда путаница:** при первом знакомстве кажется логичным «зачем дублировать — давайте всё в Order».
>
> **Если бы это было правдой:** retry counter протёк бы в доменную модель, и `Order.retryCount` появился бы в API ответах клиентам, хотя это деталь реализации FSM.
>
> ---
>
> #### D) Extended State доступен только action'ам, но не guard'ам — guard работает только с `event` и `source`/`target`. — ❌ Неверно
>
> **Что на самом деле:** guard получает тот же `StateContext`, что и action, и читает Extended State одинаково: `context.getExtendedState().get(key, type)`. Это основной паттерн «условие на основании накопленного состояния workflow».
>
> **Откуда путаница:** в простых примерах guard действительно проверяет только headers, и Extended State не показан.
>
> **Если бы это было правдой:** реализовать `maxRetriesGuard` через Extended State было бы невозможно, и пришлось бы каждый раз пробрасывать счётчик через headers сообщения.
>
> ---
>
> **Связанные вопросы:** [[Q4]] — Guard читает Extended State; [[Q5]] — Action пишет в Extended State; [[Q7]] — persistence сериализует переменные.

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


> [!mcq]
>
> **Вопрос:** Как правильно реализовать persistence для Spring State Machine, чтобы машина переживала рестарт приложения и работала корректно в многоинстансовом окружении?
>
> ---
>
> #### A) Достаточно сохранить текущий enum-state в колонку `Order.state` — при следующем запросе восстановить машину через `factory.getStateMachine(orderId)` и продолжить. — ❌ Неверно
>
> **Что на самом деле:** просто сохранения enum-state недостаточно. `factory.getStateMachine(orderId)` всегда создаёт машину в `initial` состоянии. Чтобы восстановить машину в нужное состояние, нужно использовать `StateMachineAccessor.resetStateMachine(StateMachineContext)` — он принимает контекст с состоянием И Extended State variables. Без `resetStateMachine` все переходы будут отрабатывать с начала.
>
> **Откуда путаница:** в маленьких примерах часто хватает только state, и кажется, что Extended State можно игнорировать.
>
> **Если бы это было правдой:** retry counter, accumulated failures и любые workflow-метаданные терялись бы между запросами — FSM фактически работала бы как stateless.
>
> ---
>
> #### B) `StateMachinePersister<S, E, K>` сериализует `StateMachineContext` (состояние + Extended State + история регионов) в storage (`StateMachinePersist`), и при следующем обращении машина восстанавливается через `resetStateMachine(ctx)`. Поддерживаются JDBC, Redis, MongoDB, кастомные реализации. — ✓ Верно
>
> **Развёрнутое объяснение:** SSM предоставляет двухуровневую абстракцию: `StateMachinePersist<S, E, K>` — низкоуровневый интерфейс хранилища (`write(ctx, key)`, `read(key)`), и `StateMachinePersister<S, E, K>` — обёртка, которая снимает `StateMachineContext` из живой машины и применяет обратно. Готовые реализации: `JpaStateMachineRepository` + `JpaPersistingStateMachineInterceptor`, `RedisStateMachinePersister`, `MongoDbPersistingStateMachineInterceptor`. Контекст содержит state, Extended State variables, child contexts (для иерархии и регионов), historyStates. При восстановлении: `factory.getStateMachine(orderId)` → `persister.restore(sm, orderId)` → `sm.startReactively().block()` → теперь машина в нужном состоянии и с переменными. Для thread safety в многоинстансовом окружении нужен distributed lock (Redis Redisson, JDBC `SELECT FOR UPDATE`) на ключе orderId.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class OrderFsmService {
>     private final StateMachineFactory<OrderState, OrderEvent> factory;
>     private final StateMachinePersister<OrderState, OrderEvent, String> persister;
>     private final RedissonClient redisson;
>
>     public boolean process(String orderId, OrderEvent event) {
>         RLock lock = redisson.getLock("fsm:" + orderId);
>         lock.lock();
>         try {
>             StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(orderId);
>             persister.restore(sm, orderId);          // <-- восстановление из БД
>             sm.startReactively().block();
>             boolean accepted = sm.sendEvent(Mono.just(
>                 MessageBuilder.withPayload(event).setHeader("orderId", orderId).build()
>             )).blockFirst().getResultType() == ResultType.ACCEPTED;
>             persister.persist(sm, orderId);          // <-- сохранение нового состояния
>             return accepted;
>         } finally {
>             lock.unlock();
>         }
>     }
> }
> ```
>
> **Когда применять:** долгоживущие workflow (order lifecycle, document approval, KYC), где между событиями могут пройти минуты/дни; многоинстансовые приложения, где запросы по одному orderId могут попасть на разные поды.
>
> **Подводные камни:** забыть про блокировку — race conditions при параллельных событиях по одному orderId; сериализация Extended State требует Serializable значений; миграция enum-имён состояний при изменении кода ломает восстановление; `persist()` после неуспешного `sendEvent` сохранит "застрявшее" состояние — нужна явная обработка ResultType.
>
> ---
>
> #### C) Spring State Machine из коробки сама пишет состояние в БД через `@Transactional` interceptor — никаких persister'ов настраивать не нужно. — ❌ Неверно
>
> **Что на самом деле:** SSM **не** имеет автоматической persistence "из коробки" в общем смысле. Есть `JpaPersistingStateMachineInterceptor`, который надо явно зарегистрировать в конфигурации (`config.withConfiguration().machineId(...)` + interceptor). Без явной настройки SSM хранит состояние только в RAM. Идея «всё магически персистится» — заблуждение.
>
> **Откуда путаница:** Spring Data, Spring Security имеют много auto-конфигурации, и пользователи ждут того же от SSM.
>
> **Если бы это было правдой:** не было бы вопросов про `StateMachinePersister` и многочисленных рецептов с Redis/JDBC — но реальность сложнее.
>
> ---
>
> #### D) Для distributed FSM нужно использовать Zookeeper-based persistence — `JdbcStateMachinePersist` не работает в multi-instance окружении. — ❌ Неверно
>
> **Что на самом деле:** JDBC persistence отлично работает в multi-instance окружении при условии добавления distributed lock (например, `SELECT FOR UPDATE` или Redis lock) для предотвращения одновременной обработки событий по одному ключу. Zookeeper нужен для другого — Distributed State Machine (когда одна логическая машина координируется между нодами кластера), а не просто для persistence.
>
> **Откуда путаница:** оба слова содержат "distributed", и легко спутать два разных сценария.
>
> **Если бы это было правдой:** каждое приложение со State Machine обязательно тянуло бы Zookeeper кластер — на практике большинство обходится JDBC + блокировкой.
>
> ---
>
> **Связанные вопросы:** [[Q6]] — Extended State сериализуется вместе с состоянием; [[Q8]] — `StateMachineFactory` создаёт восстанавливаемые экземпляры.

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


> [!mcq]
>
> **Вопрос:** В чём разница между `@EnableStateMachine` и `@EnableStateMachineFactory`, и в каких сценариях критично использовать фабрику?
>
> ---
>
> #### A) `@EnableStateMachine` создаёт `StateMachineFactory` как bean, а `@EnableStateMachineFactory` — это альтернативное имя для той же аннотации, оставленное для обратной совместимости. — ❌ Неверно
>
> **Что на самом деле:** это две **разные** аннотации с разной семантикой. `@EnableStateMachine` создаёт **один singleton** `StateMachine` bean — на всё приложение. `@EnableStateMachineFactory` создаёт `StateMachineFactory<S,E>` bean, из которого можно получать новые независимые экземпляры машин через `factory.getStateMachine(machineId)`.
>
> **Откуда путаница:** имена похожи, и в Spring часто есть синонимы (`@Component` vs `@Service` функционально).
>
> **Если бы это было правдой:** не было бы смысла иметь две аннотации в API, и факт их наличия указывает на разное предназначение.
>
> ---
>
> #### B) `@EnableStateMachine` подходит только для встраивания в Spring Boot Actuator — для бизнес-логики всегда нужна Factory. — ❌ Неверно
>
> **Что на самом деле:** `@EnableStateMachine` вполне применим для бизнес-задач, когда машина моделирует **глобальное** состояние системы (например, статус всего приложения, режим обслуживания, общий circuit breaker). Главное — не использовать его, когда нужно по экземпляру на бизнес-сущность.
>
> **Откуда путаница:** в большинстве туториалов показывают именно order workflow, где Factory обязательна — и складывается впечатление, что Singleton машина бесполезна.
>
> **Если бы это было правдой:** Spring не предоставлял бы `@EnableStateMachine` отдельно — это был бы антипаттерн API.
>
> ---
>
> #### C) Factory создаёт thread-safe singleton машин — можно безопасно использовать один экземпляр из `factory.getStateMachine("singleton")` параллельно из любого числа потоков. — ❌ Неверно
>
> **Что на самом деле:** ни singleton машина (`@EnableStateMachine`), ни экземпляры из Factory **не являются** thread-safe для одновременной обработки событий. SSM проектировалась как stateful entity, не как thread-safe service. Безопасный паттерн — distributed lock на machineId + restore из persistence + sendEvent + persist + unlock.
>
> **Откуда путаница:** Factory звучит как «фабрика безопасных объектов», а Spring beans по умолчанию singleton — отсюда ложная аналогия.
>
> **Если бы это было правдой:** не было бы необходимости в Redisson-локах и persister'ах — но реальность требует явной синхронизации.
>
> ---
>
> #### D) `@EnableStateMachine` создаёт singleton `StateMachine` (один на приложение), `@EnableStateMachineFactory` создаёт `StateMachineFactory`, из которого `getStateMachine(id)` возвращает новый независимый экземпляр; Factory обязательна для per-entity workflow (order, document, user). — ✓ Верно
>
> **Развёрнутое объяснение:** Singleton машина — `@EnableStateMachine` создаёт ровно один bean `StateMachine<S,E>`. Все потоки видят одно и то же состояние. Использовать только для глобальных состояний (режим приложения, общий feature toggle). Factory — `@EnableStateMachineFactory` создаёт bean `StateMachineFactory<S,E>` с тем же DSL-конфигом, но каждый вызов `factory.getStateMachine(machineId)` возвращает свежий экземпляр в initial state. Это нужно для per-entity сценариев: каждый Order имеет свою машину, каждый Document — свою. Без Factory попытка использовать один singleton FSM для нескольких заказов приведёт к race conditions: пока один поток обрабатывает CONFIRM, другой видит уже изменённое состояние. Factory + persister + distributed lock — каноничный паттерн.
>
> **Пример:**
> ```java
> @Configuration
> @EnableStateMachineFactory  // <-- НЕ @EnableStateMachine
> public class OrderFsmConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {
>     @Override
>     public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> c) throws Exception {
>         c.withConfiguration()
>             .autoStartup(false)          // ВАЖНО: иначе каждый getStateMachine стартует машину
>             .machineId("orderFsm");
>     }
>     // ... states, transitions
> }
>
> @Service
> public class OrderService {
>     private final StateMachineFactory<OrderState, OrderEvent> factory;
>
>     public void process(String orderId, OrderEvent event) {
>         StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(orderId);
>         // у каждого заказа — своя машина в своём состоянии
>     }
> }
> ```
>
> **Когда применять:** Factory — order lifecycle, document approval, payment processing, ticket workflow. Singleton — circuit breaker всего приложения, режим maintenance, глобальный feature flag.
>
> **Подводные камни:** забыть `autoStartup(false)` — каждое `getStateMachine` дёргает entry actions initial state; не закрывать машину после использования (`sm.stopReactively()`) — утечка ресурсов; путать `machineId` (имя конфига) с runtime ID экземпляра (передаётся в `getStateMachine(id)`).
>
> ---
>
> **Связанные вопросы:** [[Q7]] — persistence работает в связке с Factory; [[Q11]] — listener регистрируется на машину из Factory; [[Q15]] — singleton machine для нескольких объектов — типичная ошибка.

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


> [!mcq]
>
> **Вопрос:** Что именно даёт иерархическое (вложенное) состояние и какие подводные камни возникают при переходах из/в parent-состояние?
>
> ---
>
> #### A) Hierarchical state — это просто синтаксический сахар: после генерации FSM компилятор разворачивает иерархию в плоский набор состояний без вложенности. — ❌ Неверно
>
> **Что на самом деле:** иерархия — реальная UML state machine конструкция, сохраняемая в runtime. Переход на parent state означает вход в его initial substate (entry default). Переход с уровня parent применяется ко всем substate ниже без дублирования. Это не sugar, а семантика UML statechart.
>
> **Откуда путаница:** в JS/Python FSM-библиотеках hierarchical state часто реализуют через генерацию строковых ID, и кажется, что Spring делает так же.
>
> **Если бы это было правдой:** не было бы понятия "current state stack" (parent + substate одновременно), и `sm.getState().getIds()` возвращал бы один ID, а не множество.
>
> ---
>
> #### B) Hierarchical state позволяет группировать подсостояния под общим parent (`.parent(PROCESSING)`); transition с source=parent применяется ко всем substate; entry parent автоматически входит в initial substate; `getStates().getIds()` возвращает Set parent+substate. — ✓ Верно
>
> **Развёрнутое объяснение:** UML statechart разрешает composite states — состояния, содержащие свои внутренние FSM. В SSM реализуется через `.withStates().parent(PARENT).initial(...).state(...)`. Семантика: (1) машина одновременно "в" parent и в одном из substate — `getState().getIds()` возвращает `{PARENT, SUBSTATE}`; (2) transition `.source(PARENT).target(X).event(E)` срабатывает из любого substate, если из substate нет более специфичного transition (правило приоритета — внутренний переход выигрывает); (3) entry в parent через transition без указания substate приведёт к entry initial substate; (4) entry/exit actions parent выполняются при входе/выходе из всей иерархии. Это решает проблему "событие CANCEL должно работать в любой стадии PROCESSING" без дублирования 5 transitions.
>
> **Пример:**
> ```java
> @Override
> public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> s) throws Exception {
>     s.withStates()
>         .initial(NEW)
>         .state(PROCESSING)                  // composite
>         .end(DELIVERED).end(CANCELLED)
>         .and()
>         .withStates()
>             .parent(PROCESSING)
>             .initial(PAYMENT_PENDING)
>             .state(PAYMENT_CONFIRMED)
>             .state(SHIPPING)
>             .state(DELIVERY);
> }
>
> @Override
> public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> t) throws Exception {
>     t.withExternal()
>         .source(NEW).target(PROCESSING).event(CONFIRM)  // войдёт в PAYMENT_PENDING
>         .and()
>         .withExternal()
>         .source(PROCESSING).target(CANCELLED).event(CANCEL)  // работает из любого substate
>         .and()
>         .withInternal()
>         .source(SHIPPING).event(TRACK_UPDATE);  // внутренний — без exit/entry actions
> }
>
> // Runtime
> sm.getState().getIds();  // {PROCESSING, SHIPPING}
> ```
>
> **Когда применять:** order workflow с общими событиями (cancel, refund) на всех этапах processing; document approval с общими атрибутами для всех "под-ревью" состояний; payment processing с общим timeout-обработчиком на всю стадию авторизации.
>
> **Подводные камни:** забыть `initial(...)` для parent — transition в parent упадёт с runtime ошибкой; конфликт transition уровня parent vs substate — внутренний выигрывает молча, легко получить непредсказуемое поведение; persistence иерархии требует записи child contexts в `StateMachineContext` — не все persister'ы делают это корректно.
>
> ---
>
> #### C) Hierarchical state — это то же самое, что parallel regions: оба моделируют вложенность через `.parent(...)`. — ❌ Неверно
>
> **Что на самом деле:** это разные UML концепции. Hierarchical state — последовательная вложенность (машина в **одном** из substate в момент времени). Parallel regions — несколько **одновременных** независимых под-FSM внутри одного состояния. Hierarchical использует `.parent()`, regions используют `.parent().region("name")` — каждый region запускается параллельно.
>
> **Откуда путаница:** оба используют `.parent()`, и без слова `region()` синтаксис выглядит одинаково.
>
> **Если бы это было правдой:** `getStates().getIds()` всегда возвращал бы Set из всех substate сразу, а это нарушает определение FSM (one current state).
>
> ---
>
> #### D) Иерархия запрещает transitions между substates разных parent — для этого нужно сначала выйти на root уровень. — ❌ Неверно
>
> **Что на самом деле:** SSM поддерживает cross-hierarchy transitions: `.source(SUBSTATE_OF_A).target(SUBSTATE_OF_B).event(...)` корректно отрабатывает exit substate_A → exit parent_A → entry parent_B → entry substate_B (cascading actions). Этот же механизм используется UML.
>
> **Откуда путаница:** в простых FSM-библиотеках иерархия часто реализована плоско, и cross-hierarchy переходы запрещены.
>
> **Если бы это было правдой:** моделирование сложных workflow (например, переход напрямую из `PAYMENT.PENDING` в `REFUND.IN_PROGRESS`) было бы невозможно без промежуточных состояний.
>
> ---
>
> **Связанные вопросы:** [[Q10]] — parallel regions vs hierarchy; [[Q3]] — `getState().getIds()` возвращает stack для composite states.

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


> [!mcq]
>
> **Вопрос:** Когда нужны parallel regions вместо обычной hierarchical вложенности, и зачем для них существуют pseudostates fork/join?
>
> ---
>
> #### A) Regions — это просто способ задавать несколько initial states; машина выберет один из них на основе guards. — ❌ Неверно
>
> **Что на самом деле:** regions означают **одновременное** существование машины в нескольких подсостояниях параллельно. Каждый region имеет свой initial и end. Это не "выбор", а "параллельная обработка". Выбор начального состояния — это junction/choice pseudostate, не region.
>
> **Откуда путаница:** в учебниках по FSM часто обсуждают выбор между initial states через guards, и слово "region" может ошибочно ассоциироваться с этим.
>
> **Если бы это было правдой:** не было бы необходимости в fork/join pseudostates (которые синхронизируют параллельное завершение нескольких регионов).
>
> ---
>
> #### B) Regions нужны для последовательной обработки множества событий — это альтернатива hierarchical state с лучшей производительностью. — ❌ Неверно
>
> **Что на самом деле:** regions — про **параллелизм** (orthogonal regions в UML), а hierarchical — про **вложенность** (composite state). Регионы не быстрее иерархии и не заменяют её; они решают другую задачу: моделирование независимых параллельных аспектов одного объекта.
>
> **Откуда путаница:** оба механизма используют `.parent()`, и без чтения UML спецификации легко спутать.
>
> **Если бы это было правдой:** регионы не имели бы fork/join — а они есть именно для синхронизации параллельных потоков.
>
> ---
>
> #### C) Regions моделируют orthogonal (параллельные) состояния — машина одновременно находится в одном substate каждого региона; fork разветвляет переход на несколько регионов, join синхронизирует выход когда ВСЕ регионы достигли финального substate. — ✓ Верно
>
> **Развёрнутое объяснение:** UML orthogonal regions позволяют декомпозировать composite state на несколько параллельных подавтоматов. В SSM: `.parent(PROCESSING).region("PAYMENT")` создаёт регион "PAYMENT", `.parent(PROCESSING).region("INVENTORY")` — параллельный регион "INVENTORY". Машина в `PROCESSING` одновременно в `PAYMENT.PENDING` И в `INVENTORY.PENDING`. Каждое событие пытается выполнить transition в обоих регионах независимо. **Fork pseudostate** — точка разветвления: один transition `.source(START).target(FORK)` затем `.withFork().source(FORK).target(PROCESSING)` распараллеливает в initial substates всех регионов. **Join pseudostate** — точка синхронизации: `.withJoin().source(PROCESSING).target(JOIN_STATE)` сработает только когда **каждый** регион достигнет своего end-substate. Используется для "wait for all" семантики.
>
> **Пример:**
> ```java
> @Override
> public void configure(StateMachineStateConfigurer<OS, OE> s) throws Exception {
>     s.withStates()
>         .initial(NEW)
>         .state(PROCESSING)
>         .fork(FORK_STATE)
>         .join(JOIN_STATE)
>         .state(CONFIRMED)
>         .and()
>         .withStates().parent(PROCESSING).region("PAYMENT")
>             .initial(PAY_PENDING).end(PAY_DONE)
>         .and()
>         .withStates().parent(PROCESSING).region("INVENTORY")
>             .initial(INV_PENDING).end(INV_DONE);
> }
>
> @Override
> public void configure(StateMachineTransitionConfigurer<OS, OE> t) throws Exception {
>     t.withExternal().source(NEW).target(FORK_STATE).event(START).and()
>         .withFork().source(FORK_STATE).target(PROCESSING).and()    // разветвление
>         .withInternal().source(PAY_PENDING).event(PAY_OK).and()    // переход в одном регионе
>         .withInternal().source(INV_PENDING).event(INV_OK).and()    // переход в другом регионе
>         .withJoin().source(PROCESSING).target(JOIN_STATE).and()    // синхронизация
>         .withExternal().source(JOIN_STATE).target(CONFIRMED).event(FINALIZE);
> }
> ```
>
> **Когда применять:** order processing с параллельными подсистемами (payment + inventory + KYC); document approval с независимыми ветками ревью (legal review + finance review + technical review); установка телефона с параллельными checks (network + sim + account activation).
>
> **Подводные камни:** event с одинаковым trigger в обоих регионах вызовет transition в обоих сразу — нужно проектировать события "региональными"; join не сработает, если хотя бы один регион не достиг end-state — машина "зависнет" в PROCESSING; persistence параллельных регионов требует child contexts в `StateMachineContext` для каждого региона.
>
> ---
>
> #### D) Fork — это conditional branching (если/иначе) аналогично if-else, join — это loop join обратно к conditional. — ❌ Неверно
>
> **Что на самом деле:** условное ветвление в UML моделируется через **choice** или **junction** pseudostates с guards, а не fork. Fork всегда разветвляет в параллельные регионы (без условий, все ветви активируются одновременно). Путаница fork/choice — распространённая ошибка.
>
> **Откуда путаница:** в BPMN fork-join действительно может быть и conditional gateway, и parallel gateway. В UML statechart fork — это только parallel.
>
> **Если бы это было правдой:** не нужны были бы отдельные pseudostates choice и junction в SSM API — но они есть с разной семантикой.
>
> ---
>
> **Связанные вопросы:** [[Q9]] — hierarchical vs parallel; [[Q3]] — `getState().getIds()` для параллельных регионов возвращает substate каждого региона.

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


> [!mcq]
>
> **Вопрос:** В чём принципиальная разница между `StateMachineListener` и `Action` — оба ведь выполняются при переходе?
>
> ---
>
> #### A) Listener и Action — это две альтернативы одному и тому же; выбор — вопрос вкуса разработчика. — ❌ Неверно
>
> **Что на самом деле:** Action — **часть бизнес-логики перехода**, exception в action прерывает transition (машина уходит в error state). Listener — **observer**, для кросс-резных задач (логирование, метрики, audit), exception в listener **НЕ** прерывает transition. Разная семантика: Action участвует в transaction перехода, Listener — нет.
>
> **Откуда путаница:** оба вызываются при переходе и оба получают контекст — на поверхности похожи.
>
> **Если бы это было правдой:** не было бы смысла в двух API — но они разделены умышленно по UML state machine принципу "action — часть transition, listener — внешний наблюдатель".
>
> ---
>
> #### B) Listener привязывается к одному конкретному state и срабатывает только когда машина в этом состоянии. — ❌ Неверно
>
> **Что на самом деле:** `StateMachineListener` (а правильнее — `StateMachineListenerAdapter`) — это глобальный listener на всю машину. Он получает события всех переходов, всех изменений состояния, всех ошибок. Привязка к конкретному состоянию — это `@OnStateEntry(target = "X")` через `@WithStateMachine` annotation.
>
> **Откуда путаница:** есть похожая аннотация-based API через `@WithStateMachine`, где можно фильтровать по state — и легко спутать её с интерфейсом Listener.
>
> **Если бы это было правдой:** один Listener мог бы покрыть только одно состояние, и для логирования всех переходов нужны были бы N listener'ов — что нерационально.
>
> ---
>
> #### C) Listener гарантирует exactly-once семантику: каждое событие смены состояния доставится ровно один раз, даже после рестарта. — ❌ Неверно
>
> **Что на самом деле:** SSM не даёт гарантий exactly-once. Listener выполняется в той же JVM, что и transition — после рестарта приложения никакие пропущенные события не доставятся. Для exactly-once нужны Kafka transactional outbox, idempotency keys, retry с дедупликацией — это уровень выше SSM.
>
> **Откуда путаница:** Spring Integration и Kafka имеют exactly-once, и кажется, что Spring State Machine — это про то же.
>
> **Если бы это было правдой:** SSM можно было бы использовать как event bus для distributed систем — но это не его задача.
>
> ---
>
> #### D) `StateMachineListener` — observer-интерфейс с callback'ами (`stateChanged`, `eventNotAccepted`, `stateMachineError`, `transition`, `transitionStarted`/`transitionEnded`); используется для кросс-резных задач (метрики, audit, alerting); исключения в listener не прерывают transition. — ✓ Верно
>
> **Развёрнутое объяснение:** SSM реализует observer pattern: `StateMachine.addStateListener(listener)`. Интерфейс `StateMachineListener<S,E>` имеет ~12 callback'ов, обычно расширяют `StateMachineListenerAdapter` и переопределяют нужные. Ключевые: `stateChanged(from, to)` — после смены состояния; `transition(transition)` — на каждый переход (включая internal); `eventNotAccepted(event)` — событие не вызвало перехода (нет matching transition или guard вернул false); `stateMachineError(sm, exception)` — exception в action или транзишене; `extendedStateChanged` — изменение Extended State. Исключения в listener логируются, но НЕ прерывают transition. Это специально, чтобы observer не ломал core workflow. Регистрация через `.listener(bean)` в `StateMachineConfigurationConfigurer` или через `sm.addStateListener(listener)` в runtime. Для distributed аналитики — собирать события в Kafka, отправлять в Elasticsearch.
>
> **Пример:**
> ```java
> @Component
> @RequiredArgsConstructor
> public class OrderFsmMetricsListener
>         extends StateMachineListenerAdapter<OrderState, OrderEvent> {
>
>     private final MeterRegistry metrics;
>
>     @Override
>     public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
>         metrics.counter("order.state.changed",
>             "from", from == null ? "null" : from.getId().name(),
>             "to", to.getId().name()
>         ).increment();
>     }
>
>     @Override
>     public void eventNotAccepted(Message<OrderEvent> event) {
>         metrics.counter("order.event.rejected", "event", event.getPayload().name()).increment();
>         log.warn("Event {} rejected — current state doesn't allow it", event.getPayload());
>     }
>
>     @Override
>     public void stateMachineError(StateMachine<OrderState, OrderEvent> sm, Exception e) {
>         metrics.counter("order.fsm.error").increment();
>         log.error("FSM error for machine {}", sm.getId(), e);
>         // alerting через AlertManager
>     }
> }
> ```
>
> **Когда применять:** Prometheus метрики переходов; audit log в БД (с асинхронной записью); alerting на `eventNotAccepted` (бизнес-аномалия); отправка событий в Kafka для downstream систем; correlation ID propagation через MDC.
>
> **Подводные камни:** забыть, что listener не транзакционен — запись в БД из stateChanged может не откатиться при rollback transition; синхронный listener блокирует transition (тяжёлая IO в listener — антипаттерн); порядок listener'ов не гарантирован (если их несколько); listener на factory создаваемых машинах нужно регистрировать после `getStateMachine()`, не глобально.
>
> ---
>
> **Связанные вопросы:** [[Q5]] — Action vs Listener; [[Q14]] — `@WithStateMachine` как декларативная альтернатива.

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


> [!mcq]
>
> **Вопрос:** Какой подход к тестированию Spring State Machine даёт максимально полное покрытие и при этом остаётся поддерживаемым?
>
> ---
>
> #### A) `StateMachineTestPlanBuilder.<S,E>builder().stateMachine(sm).step()....step().build().test()` позволяет описать сценарий из шагов (sendEvent → expectStates → expectStateChanged), включая ожидания listener callback'ов; для unit-тестов guard/action — мокать `StateContext` через Mockito. — ✓ Верно
>
> **Развёрнутое объяснение:** SSM предоставляет `spring-statemachine-test` с DSL `StateMachineTestPlanBuilder`. Каждый шаг описывает: что отправляем (`sendEvent(MSG)`), какое состояние ожидаем (`expectStates(STATE)`), сколько transitions ожидаем (`expectStateChanged(count)`), какие events accepted/rejected. Plan управляет автоматическим ожиданием асинхронных reactor-операций (machine.startReactively, sendEvent возвращает Flux). Для guard/action логики — отдельные unit-тесты с замоканным `StateContext<S,E>`: `when(ctx.getMessageHeader("orderId")).thenReturn("ORD-1"); when(ctx.getExtendedState().get(...)).thenReturn(...)`. Для integration tests — `@SpringBootTest` с реальной машиной и моками внешних сервисов (notification, payment gateway). Для persistence — `@DataJpaTest` + `JpaPersistingStateMachineInterceptor` против H2.
>
> **Пример:**
> ```java
> @SpringBootTest
> class OrderFsmTest {
>     @Autowired
>     private StateMachineFactory<OrderState, OrderEvent> factory;
>
>     @Test
>     void shouldGoFromNewToConfirmedThroughProcessing() throws Exception {
>         StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine("test-1");
>
>         StateMachineTestPlan<OrderState, OrderEvent> plan =
>             StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
>                 .stateMachine(sm)
>                 .step()
>                     .expectStates(NEW)
>                     .and()
>                 .step()
>                     .sendEvent(MessageBuilder.withPayload(CONFIRM).setHeader("orderId", "ORD-1").build())
>                     .expectStateChanged(1)
>                     .expectStates(PROCESSING, PAYMENT_PENDING)  // hierarchical
>                     .and()
>                 .step()
>                     .sendEvent(PAY)
>                     .expectStateChanged(1)
>                     .expectStates(PROCESSING, PAYMENT_CONFIRMED)
>                     .and()
>                 .build();
>
>         plan.test();
>     }
>
>     // Negative scenario — guard блокирует
>     @Test
>     void shouldRejectShipBeforePayment() throws Exception {
>         StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine("test-2");
>         StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
>             .stateMachine(sm)
>             .step().expectStates(NEW).and()
>             .step()
>                 .sendEvent(SHIP)
>                 .expectEventNotAccepted(1)        // event отвергнут
>                 .expectStates(NEW)                // состояние не изменилось
>                 .and()
>             .build()
>             .test();
>     }
> }
> ```
>
> **Когда применять:** проверка transition table — обязательно; happy path сценариев — обязательно; negative scenarios (guard rejects) — обязательно; hierarchical/parallel regions — TestPlan видит составное состояние; persistence cycle — отдельный test, restore + sendEvent + persist + verify в БД.
>
> **Подводные камни:** забыть `expectEventNotAccepted` в негативных тестах — тест может проходить даже если guard сломался; TestPlan ждёт асинхронных reactor-операций — на медленных CI можно получить timeout, нужен `await` с таймаутом; mock-сервисов в actions требует `@MockBean` (не `@Mock`), чтобы Spring подставил их в action beans.
>
> ---
>
> #### B) Тестировать только sendEvent + getState — этого достаточно, потому что guards/actions просто мокаются на уровне unit. — ❌ Неверно
>
> **Что на самом деле:** при таком подходе вы покрываете "transition table" (откуда-куда), но не валидируете integration guards с Extended State, последовательность entry/exit actions, поведение при guard=false (event-not-accepted), interaction listeners. Полное покрытие FSM требует проверки и негативных сценариев, и Extended State, и listener interaction.
>
> **Откуда путаница:** простые туториалы показывают именно sendEvent + assertEquals — это базовое покрытие, но недостаточное для production.
>
> **Если бы это было правдой:** в SSM не было бы `StateMachineTestPlan` и `StateMachineTestPlanBuilder` — а они есть именно для сценарных тестов.
>
> ---
>
> #### C) Достаточно ArchUnit-тестов на структуру FSM-конфига — поведение тестируется на уровне E2E через REST API. — ❌ Неверно
>
> **Что на самом деле:** ArchUnit проверяет архитектуру (зависимости пакетов), но не поведение FSM. E2E тесты слишком медленные и хрупкие для проверки всех transition combinations — typical FSM имеет N states × M events комбинаций. Unit + TestPlan покрытие — золотая середина: быстро и полно.
>
> **Откуда путаница:** в проектах с богатым E2E suite иногда экономят на unit tests, надеясь на E2E coverage.
>
> **Если бы это было правдой:** одиночное падение E2E ломало бы все downstream сценарии, и debug FSM становился бы невозможным.
>
> ---
>
> #### D) Для тестирования нужно сначала сериализовать машину в `StateMachineContext`, восстановить и проверить — без persistence cycle тест не валиден. — ❌ Неверно
>
> **Что на самом деле:** persistence cycle — отдельная зона ответственности (`StateMachinePersister` тестов). Тесты transition table должны быть **быстрыми и независимыми** от persistence — это базовая пирамида тестов. Persist/restore проверяется отдельно: `persist(sm, "k") → factory.getStateMachine("k") → restore("k") → expectStates(...)`.
>
> **Откуда путаница:** для долгоживущих FSM persistence действительно критична, и хочется проверять её "везде".
>
> **Если бы это было правдой:** unit-тесты FSM длились бы минуты из-за инициализации хранилищ — и команда перестала бы их писать.
>
> ---
>
> **Связанные вопросы:** [[Q4]] — Guard тестируется отдельно через StateContext mock; [[Q5]] — Action delegation в service позволяет unit-тестировать service независимо; [[Q11]] — Listener interactions через `expectStateChanged`.

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


> [!mcq]
>
> **Вопрос:** Можно ли State Machine использовать как замену Saga для распределённых транзакций между микросервисами?
>
> ---
>
> #### A) Да, State Machine — это и есть Saga; разница только в терминологии: Saga = orchestrated FSM. — ❌ Неверно
>
> **Что на самом деле:** Saga и State Machine — связанные, но **разные** паттерны. Saga специфична для распределённых транзакций между сервисами с компенсирующими операциями и eventual consistency. State Machine — общий паттерн моделирования поведения объекта с состояниями. Orchestrated Saga **может быть реализован** через State Machine как координатор, но не каждая FSM — это Saga.
>
> **Откуда путаница:** оба паттерна работают через переходы между состояниями, и orchestrated Saga часто рисуют как state diagram.
>
> **Если бы это было правдой:** не было бы choreographed Saga (без orchestrator), который существует и широко используется.
>
> ---
>
> #### B) State Machine — это локальный паттерн моделирования жизненного цикла одной сущности (один процесс, одна БД, ACID). Saga — distributed transaction pattern с компенсирующими операциями и eventual consistency между сервисами. Их можно комбинировать: orchestrated Saga часто реализуется через State Machine как координатор. — ✓ Верно
>
> **Развёрнутое объяснение:** State Machine оперирует в рамках одного процесса и одной БД: переход состояния и побочные эффекты можно обернуть в локальную ACID транзакцию. Saga — про координацию **между** сервисами, где локальные транзакции не достаточно (CAP теорема). В Saga каждый шаг — отдельная локальная транзакция; при сбое выполняются компенсирующие действия в обратном порядке. Две реализации Saga: orchestrated (центральный координатор инициирует шаги — здесь идеально подходит SSM как state-driven координатор) и choreographed (сервисы реагируют на события друг друга без координатора — обычно через Kafka). Использовать SSM как координатор orchestrated Saga: состояния = шаги Saga (PAYMENT_PROCESSING, INVENTORY_RESERVED, SHIPPED), события = ответы сервисов (PAYMENT_OK, INVENTORY_OK), actions = вызовы сервисов и публикация компенсаций при ошибке.
>
> **Пример:**
> ```java
> // State Machine как orchestrator для Saga
> public enum OrderSagaState { CREATED, PAYMENT_PENDING, PAYMENT_OK, PAYMENT_FAILED,
>                              INVENTORY_PENDING, INVENTORY_OK, INVENTORY_FAILED,
>                              SHIPPED, COMPENSATING_PAYMENT, COMPENSATING_INVENTORY, FAILED }
>
> @Bean
> public Action<OrderSagaState, OrderSagaEvent> reservePayment() {
>     return ctx -> kafkaTemplate.send("payment.commands", new ReservePayment(...));
> }
>
> @Bean
> public Action<OrderSagaState, OrderSagaEvent> compensatePayment() {
>     return ctx -> kafkaTemplate.send("payment.commands", new RefundPayment(...));
> }
>
> // Transition с компенсацией
> transitions
>     .withExternal()
>         .source(INVENTORY_PENDING).target(INVENTORY_FAILED).event(INVENTORY_REJECT)
>     .and()
>     .withExternal()
>         .source(INVENTORY_FAILED).target(COMPENSATING_PAYMENT).event(START_COMPENSATION)
>         .action(compensatePayment());
> ```
>
> **Когда применять:** локальная FSM (один сервис, одна БД) — order processing внутри одного сервиса, document workflow в одном модуле. Saga + FSM-orchestrator — distributed: e-commerce checkout (Order → Payment → Inventory → Shipping сервисы); booking system (Reservation → Payment → Notification). Choreographed Saga (без FSM) — простые цепочки 2-3 событий с понятной семантикой.
>
> **Подводные камни:** использовать SSM Saga-orchestrator без идемпотентности на стороне worker-сервисов — двойные платежи при retry; путать локальные FSM transitions с distributed Saga steps — в Saga каждый шаг должен публиковать событие, не вызывать сервис синхронно (HTTP); компенсация не всегда инверсна — иногда refund != отмена платежа (есть комиссии).
>
> ---
>
> #### C) State Machine не подходит для долгоживущих процессов — её следует использовать только для синхронных переходов длительностью до секунды. — ❌ Неверно
>
> **Что на самом деле:** SSM наоборот часто применяется для долгоживущих workflow (часы, дни) благодаря persistence. Document approval может ждать ответа от reviewer'а сутками — машина персистится, восстанавливается при следующем событии. Ограничение на "до секунды" — выдумка.
>
> **Откуда путаница:** in-memory FSM действительно живут коротко, и persistent FSM с persister'ом — отдельный кейс, который не всегда явно обсуждается.
>
> **Если бы это было правдой:** не имело бы смысла существование `StateMachinePersister` — но он есть и применяется именно для долгоживущих машин.
>
> ---
>
> #### D) Saga всегда требует Kafka как транспорт — без Kafka реализовать Saga невозможно. — ❌ Неверно
>
> **Что на самом деле:** Saga — паттерн, а не привязка к технологии. Реализуется через любой надёжный transport: RabbitMQ, AWS SQS, gRPC streams, HTTP с retry, NATS. Kafka популярна благодаря log-based семантике, но не обязательна.
>
> **Откуда путаница:** в туториалах часто показывают Saga с Kafka, и появляется ассоциация "Saga = Kafka".
>
> **Если бы это было правдой:** проекты на AWS с SNS/SQS не могли бы использовать Saga — что не соответствует действительности.
>
> ---
>
> **Связанные вопросы:** [[Q11]] — Listener публикует события для choreographed scenarios; [[Q7]] — persistence критична для долгоживущих Saga-orchestrator машин.

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


> [!mcq]
>
> **Вопрос:** Что даёт `@WithStateMachine` и какие подводные камни связаны с его использованием вместо явной регистрации Action/Listener бинов?
>
> ---
>
> #### A) `@WithStateMachine` связывает Spring bean с конкретной машиной (по `id`/`name`) и позволяет реагировать на её события декларативно через `@OnTransition`, `@OnStateEntry`, `@OnStateExit`, `@OnEventNotAccepted` и др. — методы вызываются автоматически, без явной регистрации listener. — ✓ Верно
>
> **Развёрнутое объяснение:** `@WithStateMachine(id="orderFsm")` помечает класс как "получатель событий" конкретной FSM. SSM сканирует bean и регистрирует методы как обработчики на основе аннотаций. Доступные аннотации: `@OnTransition(source="...", target="...")` — на конкретный переход; `@OnStateEntry(target="...")` — entry в состояние; `@OnStateExit(source="...")` — exit; `@OnStateChanged` — любая смена; `@OnEventNotAccepted(event="...")` — отвергнутое событие; `@OnStateMachineStart/Stop` — lifecycle; `@OnStateMachineError` — ошибки. В метод можно инжектить параметры через типы или аннотации: `@EventHeaders Map<String, Object>`, `@EventHeader("orderId") String`, `ExtendedState`, `StateContext<S,E>`, `Message<E>`, `Exception`. Это удобная альтернатива императивной регистрации Action и Listener бинов: handler-логика остаётся декларативной, типобезопасной и читаемой.
>
> **Пример:**
> ```java
> @Component
> @WithStateMachine(id = "orderFsm")
> @RequiredArgsConstructor
> public class OrderEventHandler {
>     private final NotificationService notifications;
>     private final AuditLogRepository auditLog;
>
>     @OnTransition(source = "PAYMENT_PENDING", target = "PAYMENT_CONFIRMED")
>     public void onPaymentConfirmed(
>             @EventHeader("orderId") String orderId,
>             ExtendedState extendedState) {
>         notifications.sendPaymentReceipt(orderId);
>         extendedState.getVariables().put("paymentConfirmedAt", Instant.now());
>     }
>
>     @OnStateEntry(target = "SHIPPED")
>     public void onShipped(@EventHeaders Map<String, Object> headers) {
>         auditLog.save(new AuditEntry((String) headers.get("orderId"), "SHIPPED"));
>     }
>
>     @OnEventNotAccepted(event = "SHIP")
>     public void onShipRejected(Message<OrderEvent> msg) {
>         log.warn("Ship event rejected for order {}", msg.getHeaders().get("orderId"));
>     }
>
>     @OnStateMachineError
>     public void onError(StateMachine<OrderState, OrderEvent> sm, Exception e) {
>         alertingService.alert("FSM error", e);
>     }
> }
> ```
>
> **Когда применять:** когда handler-логика чисто декларативна (логирование, нотификации, метрики); когда нужна привязка к конкретным переходам без полной FSM-конфигурации; для отделения side effects от core конфигурации; для тестируемости — handler можно вызывать напрямую как обычный bean.
>
> **Подводные камни:** забыть `id` в `@WithStateMachine` — handler привяжется к default machine (может промахнуться в multi-machine setup); `@OnTransition` без source/target ловит **все** transitions (неожиданное поведение); параметр-инъекция через `@EventHeader` упадёт молча, если header отсутствует — нужны null-checks; handler не транзакционен — `@Transactional` нужно ставить явно; при использовании с `StateMachineFactory` все handler'ы автоматически привязываются к каждой создаваемой машине через `id` config — фильтровать по runtime ID нельзя без дополнительных проверок.
>
> ---
>
> #### B) `@WithStateMachine` — это альтернатива `@EnableStateMachine`, аннотация для класса конфигурации FSM. — ❌ Неверно
>
> **Что на самом деле:** `@WithStateMachine` и `@EnableStateMachine` решают разные задачи. `@EnableStateMachine`/`@EnableStateMachineFactory` создают саму машину/фабрику. `@WithStateMachine` ставится на **handler-классы** и связывает методы с событиями FSM через аннотации `@OnTransition`, `@OnStateEntry`, `@OnStateExit`, `@OnStateChanged`, `@OnEventNotAccepted` и т.д.
>
> **Откуда путаница:** имена похожи (`@With...`, `@Enable...`), оба содержат "StateMachine".
>
> **Если бы это было правдой:** конфигурация и handler'ы были бы переплетены в одном классе, что нарушает разделение ответственностей.
>
> ---
>
> #### C) `@WithStateMachine` отключает все программные Action и Listener — нельзя комбинировать аннотации и Java-bean подход. — ❌ Неверно
>
> **Что на самом деле:** оба подхода **сосуществуют**. Можно одновременно регистрировать Action в `transitions.action(actionBean())` и иметь `@OnTransition` handler — оба отработают. Полезно: бизнес-критичную логику оставлять в Action (часть transition transaction), наблюдение — в `@WithStateMachine`.
>
> **Откуда путаница:** часто проекты выбирают один стиль и не смешивают — кажется, что это требование.
>
> **Если бы это было правдой:** миграция legacy SSM на аннотации требовала бы переписывания всего сразу — нереалистично.
>
> ---
>
> #### D) Методы `@OnTransition` всегда выполняются в новой транзакции `REQUIRES_NEW` автоматически. — ❌ Неверно
>
> **Что на самом деле:** SSM не управляет транзакциями `@WithStateMachine` методов. Для транзакционности — добавлять `@Transactional` явно. По умолчанию метод выполняется в том же потоке, что и transition, без spring-managed транзакции (если не настроен `@Transactional` на classpath proxy).
>
> **Откуда путаница:** Spring Boot обильно использует автоматическую транзакционность (`@JpaRepository`), и хочется верить, что и здесь так же.
>
> **Если бы это было правдой:** не было бы вопросов "почему мои изменения не закоммитились в `@OnTransition`" на StackOverflow.
>
> ---
>
> **Связанные вопросы:** [[Q5]] — Action как императивная альтернатива; [[Q11]] — Listener как императивная альтернатива; [[Q12]] — handler легко тестируется как обычный bean.

## Q15. Какие типичные ошибки при работе со Spring State Machine?

1. **Singleton machine для нескольких объектов** — использовать `StateMachineFactory` вместо `@EnableStateMachine`.

2. **Игнорирование thread safety** — один экземпляр `StateMachine` не thread-safe; персистировать и восстанавливать при каждом запросе.

3. **Бизнес-логика в конфигурации** — Actions и Guards должны делегировать в сервисы, не содержать логику напрямую.

4. **Нет обработки `eventNotAccepted`** — незапринятые события теряются молча; всегда добавлять Listener.

5. **Отсутствие end-состояний** — без `end()` машина никогда не завершается и продолжает обрабатывать события.


> [!mcq]
>
> **Вопрос:** Какая из ошибок чаще всего приводит к самым серьёзным production-инцидентам в проектах со Spring State Machine?
>
> ---
>
> #### A) Слишком гранулярная иерархия состояний — нужно держать FSM плоской и без вложений. — ❌ Неверно
>
> **Что на самом деле:** иерархия состояний — это **сильная сторона** SSM, она помогает декомпозировать сложный workflow. Плоская FSM на 30 состояний и 200 transitions нечитаема и неподдерживаема; правильно структурированная hierarchical FSM на 5 composite states и 15 substates лучше для команды.
>
> **Откуда путаница:** в простых учебных примерах действительно используется плоская FSM, и кажется, что усложнять не надо.
>
> **Если бы это было правдой:** UML statechart не вводил бы concept composite state — а именно он сделал FSM применимой к большим бизнес-процессам.
>
> ---
>
> #### B) Перенос бизнес-логики в Action/Guard вместо делегирования в сервисы — handler становится god-method'ом, нет переиспользования. — ❌ Неверно
>
> **Что на самом деле:** это **реальная** ошибка, но она приводит к проблемам поддержки, не к production-инцидентам напрямую. Бизнес-логика в Action работает, просто плохо тестируется и переиспользуется. Это quality issue, не reliability issue.
>
> **Откуда путаница:** ошибка действительно частая и важная — но среди вариантов есть более критичная для production reliability.
>
> **Если бы это было правдой:** проекты с anti-pattern'ом немедленно падали бы в production — на практике они просто медленно деградируют по maintainability.
>
> ---
>
> #### C) Использование `@EnableStateMachine` (singleton) вместо `StateMachineFactory` для per-entity workflow — все заказы шарят одну машину, race conditions, потеря состояний при concurrent событиях по разным orderId. — ✓ Верно
>
> **Развёрнутое объяснение:** это **классическая** ошибка с прямыми production-последствиями. `@EnableStateMachine` создаёт **один** bean `StateMachine` для всего приложения. Если код использует его для нескольких заказов (`sm.sendEvent(confirmOrder1)` параллельно с `sm.sendEvent(shipOrder2)`), оба заказа делят одно состояние — события одного заказа меняют state для другого. Симптомы в production: заказы внезапно "перепрыгивают" этапы, события теряются ("event not accepted" в логах для валидных событий), race conditions трудно воспроизводимы локально (всё работает на одной машине в dev). Правильное решение: `@EnableStateMachineFactory` + `factory.getStateMachine(orderId)` для каждого заказа + persistence через `StateMachinePersister` + distributed lock на orderId (Redisson, JDBC `SELECT FOR UPDATE`).
>
> **Пример:**
> ```java
> // ❌ Антипаттерн — singleton для per-entity FSM
> @Configuration
> @EnableStateMachine  // <-- singleton!
> public class OrderFsmConfigWrong { ... }
>
> @Service
> @RequiredArgsConstructor
> public class OrderServiceWrong {
>     private final StateMachine<OrderState, OrderEvent> sm;  // один на всё приложение
>
>     public void process(String orderId, OrderEvent event) {
>         // RACE CONDITION: два разных заказа шарят машину
>         sm.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).blockFirst();
>     }
> }
>
> // ✓ Правильный паттерн
> @Configuration
> @EnableStateMachineFactory
> public class OrderFsmConfig { ... }
>
> @Service
> @RequiredArgsConstructor
> public class OrderService {
>     private final StateMachineFactory<OrderState, OrderEvent> factory;
>     private final StateMachinePersister<OrderState, OrderEvent, String> persister;
>     private final RedissonClient redisson;
>
>     public void process(String orderId, OrderEvent event) {
>         RLock lock = redisson.getLock("order-fsm:" + orderId);
>         lock.lock();
>         try {
>             StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(orderId);
>             persister.restore(sm, orderId);
>             sm.startReactively().block();
>             sm.sendEvent(Mono.just(MessageBuilder.withPayload(event)
>                 .setHeader("orderId", orderId).build())).blockFirst();
>             persister.persist(sm, orderId);
>         } finally {
>             lock.unlock();
>         }
>     }
> }
> ```
>
> **Когда применять:** любой per-entity workflow (order, document, ticket, user lifecycle) — обязательно Factory. Singleton машина допустима только для **глобальных** состояний приложения (maintenance mode, global circuit breaker).
>
> **Подводные камни:** code review должен ловить `@EnableStateMachine` в feature кода — добавить ArchUnit правило; миграция legacy кода с singleton на Factory требует одновременно добавления persistence и distributed lock — нельзя ограничиться заменой аннотации; тесты "локально работает" не ловят race condition — нужны concurrency tests или хотя бы load testing на staging.
>
> ---
>
> #### D) Слишком частое использование Extended State — нужно хранить всё в `@Entity`, а Extended State использовать только в исключительных случаях. — ❌ Неверно
>
> **Что на самом деле:** Extended State — спроектированный механизм для workflow-метаданных, его активное использование — норма, не антипаттерн. Перенос retry counters, временных флагов, accumulated context в доменную сущность как раз и есть антипаттерн (детали реализации FSM протекают в API).
>
> **Откуда путаница:** в DDD-литературе часто советуют "богатые domain entities" и кажется, что любая дополнительная state нужна в Entity.
>
> **Если бы это было правдой:** Extended State не существовал бы в SSM API — но он есть и используется широко.
>
> ---
>
> **Связанные вопросы:** [[Q8]] — Factory vs singleton; [[Q7]] — persistence обязательна для Factory подхода; [[Q11]] — Listener на `eventNotAccepted` помогает диагностировать race conditions.

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
