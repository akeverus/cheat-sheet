---
title: "Вопросы на собеседовании: Чистая архитектура"
description: "Полное покрытие Clean Architecture и Hexagonal Architecture: правило зависимостей, слои, порты и адаптеры, Use Case Interactors, Onion Architecture, реализация в Spring Boot, тестирование, антипаттерны."
tags:
  - interview
  - architecture
  - clean-architecture-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Clean Architecture"
  - "Clean Architecture interview"
  - "Гексагональная архитектура"
prerequisites:
  - "[[clean-architecture]]"
next: []
updated: "2026-11-09"
---
# Вопросы на собеседовании: Чистая архитектура

Полное покрытие `Clean Architecture` и `Hexagonal Architecture`: правило зависимостей, слои (`Entities`, `Use Cases`, `Interface Adapters`, `Frameworks`), порты и адаптеры, `Onion Architecture`, `Dependency Inversion`, реализация в `Spring Boot`, тестирование и антипаттерны.

**Clean Architecture** (Роберт Мартин, 2012) и **Hexagonal Architecture** (Алистер Кокбёрн, 2005) -- два наиболее влиятельных архитектурных подхода, объединённых общей идеей: изоляция бизнес-логики от инфраструктурных деталей. На собеседованиях уровня Senior/Lead вопросы по этим темам проверяют понимание `Dependency Inversion`, умение проектировать границы модулей и выбирать подходящую архитектуру для конкретного контекста.

## Полезные ссылки

### Первичные источники

- [Robert C. Martin: The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) — слои и Dependency Rule.
- [Alistair Cockburn: Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/) — порты и адаптеры.
- [Jeffrey Palermo: Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/) — внутренние политики и внешние детали.
- [Spring Framework: `@Transactional`](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html) — proxy, внешние вызовы и self-invocation.
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html) — правила зависимостей.
- [Robert C. Martin: Screaming Architecture](https://blog.cleancoder.com/uncle-bob/2011/09/30/Screaming-Architecture.html) — структура по предметной области.
- [Jimmy Bogard: Vertical Slice Architecture](https://www.jimmybogard.com/vertical-slice-architecture/) — группировка по сценариям.

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Clean Architecture**
- [Q1. (!) Что такое Clean Architecture и какую проблему она решает?](#q1--что-такое-clean-architecture-и-какую-проблему-она-решает)
- [Q2. (!) Что такое правило зависимостей (Dependency Rule)?](#q2--что-такое-правило-зависимостей-dependency-rule)
- [Q3. Какие слои определяет Clean Architecture?](#q3-какие-слои-определяет-clean-architecture)
- [Q4. Что такое Entities в контексте Clean Architecture?](#q4-что-такое-entities-в-контексте-clean-architecture)
- [Q5. (!) Что такое Use Cases (Interactors) и какова их роль?](#q5--что-такое-use-cases-interactors-и-какова-их-роль)
- [Q6. Что такое Interface Adapters?](#q6-что-такое-interface-adapters)
- [Q7. Какова роль слоя Frameworks & Drivers?](#q7-какова-роль-слоя-frameworks--drivers)

**Hexagonal Architecture (Ports & Adapters)**
- [Q8. (!) Что такое Hexagonal Architecture и чем она отличается от Clean Architecture?](#q8--что-такое-hexagonal-architecture-и-чем-она-отличается-от-clean-architecture)
- [Q9. Что такое порты (Ports) в гексагональной архитектуре?](#q9-что-такое-порты-ports-в-гексагональной-архитектуре)
- [Q10. Что такое адаптеры (Adapters) и какие их типы существуют?](#q10-что-такое-адаптеры-adapters-и-какие-их-типы-существуют)
- [Q11. Чем отличаются Input (Driving) и Output (Driven) порты?](#q11-чем-отличаются-input-driving-и-output-driven-порты)
- [Q12. Как связаны Hexagonal Architecture и Dependency Inversion Principle?](#q12-как-связаны-hexagonal-architecture-и-dependency-inversion-principle)

**Onion Architecture и сравнение подходов**
- [Q13. Что такое Onion Architecture?](#q13-что-такое-onion-architecture)
- [Q14. Сравните Clean, Hexagonal и Onion Architecture](#q14-сравните-clean-hexagonal-и-onion-architecture)
- [Q15. (!) Чем Clean Architecture отличается от традиционной слоистой архитектуры?](#q15--чем-clean-architecture-отличается-от-традиционной-слоистой-архитектуры)
- [Q16. Что такое Vertical Slice Architecture и когда она предпочтительнее?](#q16-что-такое-vertical-slice-architecture-и-когда-она-предпочтительнее)

**Реализация в Java и Spring Boot**
- [Q17. Как организовать пакетную структуру Spring Boot проекта по Clean Architecture?](#q17-как-организовать-пакетную-структуру-spring-boot-проекта-по-clean-architecture)
- [Q18. Как реализовать Input Port и Use Case Interactor в Java?](#q18-как-реализовать-input-port-и-use-case-interactor-в-java)
- [Q19. Как реализовать Output Port и его адаптер?](#q19-как-реализовать-output-port-и-его-адаптер)
- [Q20. (!) Как Spring Boot вписывается в Clean Architecture?](#q20--как-spring-boot-вписывается-в-clean-architecture)
- [Q21. Как правильно передавать данные между слоями (DTO, Domain Model, Entity)?](#q21-как-правильно-передавать-данные-между-слоями-dto-domain-model-entity)
- [Q22. Как обрабатывать транзакции в Clean Architecture?](#q22-как-обрабатывать-транзакции-в-clean-architecture)

**Тестирование и качество**
- [Q23. Какие преимущества для тестирования даёт Clean Architecture?](#q23-какие-преимущества-для-тестирования-даёт-clean-architecture)
- [Q24. Как тестировать Use Case без инфраструктуры?](#q24-как-тестировать-use-case-без-инфраструктуры)
- [Q25. Как использовать ArchUnit для проверки архитектурных правил?](#q25-как-использовать-archunit-для-проверки-архитектурных-правил)

**Антипаттерны и практические проблемы**
- [Q26. (!) Какие типичные антипаттерны нарушают Clean Architecture?](#q26--какие-типичные-антипаттерны-нарушают-clean-architecture)
- [Q27. Когда Clean Architecture -- это overkill?](#q27-когда-clean-architecture----это-overkill)
- [Q28. (!) Как избежать анемичной доменной модели в Clean Architecture?](#q28--как-избежать-анемичной-доменной-модели-в-clean-architecture)
- [Q29. Как применять Clean Architecture в микросервисах?](#q29-как-применять-clean-architecture-в-микросервисах)
- [Q30. Как эволюционно мигрировать монолит к Clean Architecture?](#q30-как-эволюционно-мигрировать-монолит-к-clean-architecture)

**Продвинутые темы**
- [Q31. (!) Как правильно реализовать Use Case Interactor с несколькими портами?](#q31--как-правильно-реализовать-use-case-interactor-с-несколькими-портами)
- [Q32. Как организовать обработку ошибок в Use Case без исключений инфраструктуры?](#q32-как-организовать-обработку-ошибок-в-use-case-без-исключений-инфраструктуры)
- [Q33. (!) Как проверить соблюдение Dependency Rule через ArchUnit?](#q33--как-проверить-соблюдение-dependency-rule-через-archunit)

**Clean Architecture: расширенные темы**
- [Q34. (!) Чем Clean Architecture отличается от Hexagonal (Ports & Adapters) — ключевые отличия?](#q34--чем-clean-architecture-отличается-от-hexagonal-ports--adapters--ключевые-отличия)
- [Q35. Как реализовать Presenter паттерн в Clean Architecture?](#q35-как-реализовать-presenter-паттерн-в-clean-architecture)
- [Q36. Как организовать тестирование на каждом уровне Clean Architecture?](#q36-как-организовать-тестирование-на-каждом-уровне-clean-architecture)
- [Q37. Как реализовать Dependency Injection без Spring IoC контейнера?](#q37-как-реализовать-dependency-injection-без-spring-ioc-контейнера)
- [Q38. Как интегрировать агрегаты и Domain Events в Clean Architecture?](#q38-как-интегрировать-агрегаты-и-domain-events-в-clean-architecture)
- [Q39. Что такое Screaming Architecture и как она связана с Clean Architecture?](#q39-что-такое-screaming-architecture-и-как-она-связана-с-clean-architecture)
- [Q40. (!) Как эволюционировать от Clean Architecture к Vertical Slice Architecture?](#q40--как-эволюционировать-от-clean-architecture-к-vertical-slice-architecture)
- [Q41. Как правильно реализовать пакетную структуру Spring Boot проекта в стиле Clean Architecture на практике?](#q41-как-правильно-реализовать-пакетную-структуру-spring-boot-проекта-в-стиле-clean-architecture-на-практике)

---

## Q1. (!) Что такое Clean Architecture и какую проблему она решает?

**Clean Architecture -- это способ организовать код так, чтобы бизнес-логика не зависела от инфраструктуры** (UI, базы данных, фреймворков, внешних сервисов). Подход предложил Роберт Мартин (Uncle Bob) в 2012 году.

Проблема, которую она решает, -- **сильная связанность** (tight coupling) бизнес-логики с технологическими деталями. В типичном приложении бизнес-правила "размазаны" по контроллерам, сервисам и репозиториям, и это бьёт по сопровождаемости:

- **Технологию не заменить** без переписывания бизнес-логики
- **Тестировать тяжело** -- для unit-теста нужна база данных или HTTP-сервер
- **Правила не переиспользовать** в другом контексте (CLI, другой сервис)
- **Изменения каскадят** -- правка UI ломает доменную логику

Лекарство -- **правило зависимостей** (Dependency Rule): зависимости в коде направлены только **внутрь**, к бизнес-логике. Внешний слой знает о внутреннем, но не наоборот. За счёт этого ядро ничего не знает о том, кто его вызывает и где хранятся данные, поэтому остаётся стабильным при смене любых внешних деталей.

```mermaid
graph TB
    subgraph "Frameworks & Drivers (внешний)"
        FW[Web Framework<br/>DB Driver<br/>UI]
    end
    subgraph "Interface Adapters"
        IA[Controllers<br/>Presenters<br/>Gateways]
    end
    subgraph "Use Cases"
        UC[Application<br/>Business Rules]
    end
    subgraph "Entities (ядро)"
        E[Enterprise<br/>Business Rules]
    end
    FW --> IA --> UC --> E
    style E fill:#2d5016,color:#fff
    style UC fill:#4a7c2e,color:#fff
    style IA fill:#6ba34a,color:#fff
    style FW fill:#8cc665,color:#000
```

> На собеседовании важно подчеркнуть: Clean Architecture -- это не конкретная структура папок, а **набор принципов** организации зависимостей. Конкретная реализация может варьироваться.

## Q2. (!) Что такое правило зависимостей (Dependency Rule)?

**Dependency Rule -- правило, что зависимости в исходном коде направлены только внутрь, к более высокоуровневым политикам.** Это центральный принцип Clean Architecture, ради которого и затеяны все слои.

На практике из него следуют три конкретных запрета:

1. **Внутренние слои ничего не знают о внешних**: `Entity` не знает о `Use Case`, `Use Case` не знает о `Controller`. Зависимость всегда указывает от периферии к ядру.
2. **Данные пересекают границы** через простые структуры (DTO), а не через объекты внешних слоёв -- иначе внутренний слой косвенно узнал бы о внешнем.
3. **Имена из внешних слоёв** не упоминаются во внутренних: домен не должен видеть имя класса `OrderController` или `OrderJpaEntity`.

Но `Use Case` всё же надо как-то достучаться до базы. Как направить зависимость внутрь, если данные физически снаружи? Через **Dependency Inversion Principle** (DIP): внутренний слой объявляет интерфейс (порт), а внешний слой его реализует (адаптер). Стрелка зависимости разворачивается -- инфраструктура зависит от домена, а не наоборот.

```java
// Внутренний слой (Use Case) определяет интерфейс
public interface OrderRepository {
    Order findById(OrderId id);
    void save(Order order);
}

// Внешний слой (Infrastructure) реализует интерфейс
public class JpaOrderRepository implements OrderRepository {
    private final JpaOrderEntityRepository jpaRepo;
    private final OrderMapper mapper;

    @Override
    public Order findById(OrderId id) {
        return jpaRepo.findById(id.value())
            .map(mapper::toDomain)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
```

Типичное нарушение правила: доменная сущность содержит JPA-аннотации (`@Entity`, `@Column`). Формально код компилируется, но домен теперь зависит от фреймворка -- его нельзя ни переиспользовать без JPA на classpath, ни протестировать без неё.

## Q3. Какие слои определяет Clean Architecture?

Clean Architecture определяет **четыре концентрических слоя**, от стабильного ядра к легко заменяемой периферии. Чем ближе к центру -- тем стабильнее код и тем реже он меняется.

| Слой | Содержимое | Зависимости |
|------|-----------|-------------|
| **Entities** | Доменные объекты, бизнес-правила уровня предприятия | Ни от чего |
| **Use Cases** | Сценарии использования, бизнес-правила приложения | Только от Entities |
| **Interface Adapters** | Контроллеры, презентеры, маперы, шлюзы | От Use Cases и Entities |
| **Frameworks & Drivers** | Web-фреймворк, БД, UI, внешние API | От всех внутренних слоёв |

```mermaid
graph LR
    subgraph "Entities"
        E1[Order]
        E2[Customer]
        E3[Product]
    end
    subgraph "Use Cases"
        U1[CreateOrderUseCase]
        U2[CancelOrderUseCase]
    end
    subgraph "Interface Adapters"
        C1[OrderController]
        P1[OrderPresenter]
        G1[OrderGateway]
    end
    subgraph "Frameworks"
        F1[Spring MVC]
        F2[PostgreSQL]
        F3[RabbitMQ]
    end
    F1 --> C1 --> U1 --> E1
    F2 --> G1 --> U1
    U2 --> E1
```

Ответственность каждого слоя:

- **Entities** -- самые стабильные правила, меняются реже всего; они переживают смену приложений
- **Use Cases** -- оркестрируют бизнес-логику: вызывают Entities и обращаются к внешнему миру через порты
- **Interface Adapters** -- переводят данные между форматом ядра и форматом внешних систем (JSON, строки таблиц)
- **Frameworks** -- самый нестабильный слой; именно его проще всего выкинуть и заменить

**Зачем именно концентрические кольца:** код, который меняется по разным причинам и с разной частотой, разнесён по разным слоям. Частые правки (новый endpoint, смена БД) задевают только периферию и не доходят до ядра.

## Q4. Что такое Entities в контексте Clean Architecture?

**Entities -- объекты, инкапсулирующие бизнес-правила уровня предприятия** (Enterprise Business Rules): те правила, что верны независимо от конкретного приложения. Это самое стабильное и ценное в системе.

Их свойства:

- Содержат **критичную бизнес-логику**, не привязанную к конкретному приложению
- Переиспользуются в нескольких приложениях (один и тот же `Order` -- и в веб-сервисе, и в batch-обработке)
- **Не содержат** аннотаций фреймворков (`@Entity`, `@Component`)
- Ничего не знают о базах данных, HTTP, очередях

Главное -- термин `Entity` здесь в смысле Uncle Bob (доменный объект), а не `@Entity` из JPA. Это разные вещи, и путать их -- частая ошибка.

```java
public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderLine> lines;
    private OrderStatus status;
    private Money totalPrice;

    public void addLine(Product product, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new OrderAlreadySubmittedException(id);
        }
        lines.add(new OrderLine(product, quantity));
        recalculateTotal();
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException(id);
        }
        this.status = OrderStatus.SUBMITTED;
    }

    public void cancel(CancellationPolicy policy) {
        if (!policy.canCancel(this)) {
            throw new CancellationNotAllowedException(id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void recalculateTotal() {
        this.totalPrice = lines.stream()
            .map(OrderLine::lineTotal)
            .reduce(Money.ZERO, Money::add);
    }
}
```

Обратите внимание: `Order` -- это **богатая доменная модель** (Rich Domain Model). Бизнес-логика (`addLine`, `submit`, `cancel`) живёт внутри объекта, а не в сервисах, поэтому объект сам охраняет свои инварианты: нельзя добавить строку в уже отправленный заказ или отправить пустой. Это противоположность анемичной модели, где `Order` -- просто набор геттеров/сеттеров, а правила раскиданы по сервисам (см. Q28).

## Q5. (!) Что такое Use Cases (Interactors) и какова их роль?

**Use Case (он же Interactor) -- единица бизнес-логики уровня приложения, описывающая один конкретный сценарий** («создать заказ», «перевести деньги»). Если Entity отвечает на вопрос «что такое заказ», то Use Case -- на вопрос «что значит создать заказ в этом приложении».

Его роль:
- **Оркестрирует** работу: загружает доменные объекты, вызывает их методы, сохраняет результат через порты
- **Реализует** правила, специфичные для приложения (а не для предприятия в целом)
- **Определяет** свои вход и выход через порты-интерфейсы, не завязываясь на конкретную технологию
- **Не зависит** от способа доставки запроса -- один и тот же Use Case вызывается из HTTP-контроллера, CLI или слушателя очереди

```java
// Input Port -- интерфейс, который определяет Use Case
public interface CreateOrderUseCase {
    CreateOrderResponse execute(CreateOrderRequest request);
}

// Входные данные
public record CreateOrderRequest(
    String customerId,
    List<OrderLineRequest> lines
) {}

// Выходные данные
public record CreateOrderResponse(
    String orderId,
    BigDecimal totalPrice,
    String status
) {}

// Interactor -- реализация Use Case
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderNotificationPort notificationPort;

    public CreateOrderInteractor(
            CustomerRepository customerRepo,
            ProductRepository productRepo,
            OrderRepository orderRepo,
            OrderNotificationPort notificationPort) {
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.notificationPort = notificationPort;
    }

    @Override
    public CreateOrderResponse execute(CreateOrderRequest request) {
        Customer customer = customerRepo.findById(
            new CustomerId(request.customerId()));

        Order order = Order.create(customer);

        for (var lineReq : request.lines()) {
            Product product = productRepo.findById(
                new ProductId(lineReq.productId()));
            order.addLine(product, lineReq.quantity());
        }

        order.submit();
        orderRepo.save(order);
        notificationPort.notifyOrderCreated(order);

        return new CreateOrderResponse(
            order.getId().value(),
            order.getTotalPrice().amount(),
            order.getStatus().name()
        );
    }
}
```

Обратите внимание: бизнес-правила (`addLine`, `submit`) живут в доменном объекте `Order`, а Interactor лишь дирижирует -- загружает участников, вызывает их методы, сохраняет. Это удерживает Use Case тонким.

> Эмпирическое правило: один Use Case -- один класс. Так соблюдается `Single Responsibility Principle`, а тест на сценарий не тянет за собой логику соседних сценариев.

## Q6. Что такое Interface Adapters?

**Interface Adapters -- слой-переводчик между форматом ядра и форматом внешнего мира** (web, БД, внешние сервисы). Ядро говорит на языке доменных объектов, внешний мир -- на языке JSON, строк таблиц и байтов очереди; этот слой переводит в обе стороны.

Что здесь живёт:

- **Controllers** -- принимают HTTP-запрос и переводят его в `Request` для Use Case
- **Presenters** -- форматируют `Response` из Use Case под нужды клиента
- **Gateways / реализации репозиториев** -- переводят доменные объекты в формат хранения и обратно
- **Mappers** -- конвертируют между DTO, доменными и persistence-моделями

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final OrderDtoMapper mapper;

    public OrderController(CreateOrderUseCase createOrder,
                           OrderDtoMapper mapper) {
        this.createOrder = createOrder;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(
            @RequestBody CreateOrderDto dto) {
        // Adapter: HTTP DTO → Use Case Request
        var request = mapper.toRequest(dto);

        // Вызов Use Case
        var response = createOrder.execute(request);

        // Adapter: Use Case Response → HTTP DTO
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toDto(response));
    }
}
```

Ключевой признак правильного контроллера: в нём **нет ни одной строки бизнес-логики** -- только перевод формата и делегирование Use Case. Если в контроллере появляется `if` про бизнес-правила, логика утекла не туда (антипаттерн из Q26).

## Q7. Какова роль слоя Frameworks & Drivers?

**Frameworks & Drivers -- самый внешний слой, где собраны все технические детали и сторонние библиотеки.** По Clean Architecture фреймворк -- это деталь, а не фундамент: он на периферии, а не в центре.

Что сюда входит:

- **Web-фреймворк**: `Spring MVC`, `Spring WebFlux`
- **ORM / Persistence**: `Hibernate`, `JDBC`, `jOOQ`
- **Драйверы БД**: `PostgreSQL`, `MySQL`, `MongoDB`
- **Очереди сообщений**: `RabbitMQ`, `Kafka`
- **Внешние API**: REST-клиенты, gRPC-стабы
- **UI**: шаблоны, фронтенд

Характеристики слоя:

1. **Тонкий "клей"** -- здесь минимум кода: конфигурация и wiring, а не логика
2. **Легко заменяем** -- смена БД с `PostgreSQL` на `MongoDB` в идеале задевает только этот слой
3. **Дом для конфигурации** Spring: `@Configuration`, `@Bean`, `application.yml`

Frameworks & Drivers размещает web/ORM-драйверы и composition root. Entities и Use Cases не импортируют этот слой, поэтому Spring Boot собирает компоненты, но не владеет бизнес-правилами.

```java
@Configuration
public class OrderConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            CustomerRepository customerRepo,
            ProductRepository productRepo,
            OrderRepository orderRepo,
            OrderNotificationPort notificationPort) {
        return new CreateOrderInteractor(
            customerRepo, productRepo, orderRepo, notificationPort);
    }
}
```

Обратите внимание: `CreateOrderInteractor` **не аннотирован** `@Service` -- он не знает о Spring. Wiring происходит в конфигурационном классе внешнего слоя.

## Q8. (!) Что такое Hexagonal Architecture и чем она отличается от Clean Architecture?

**Hexagonal Architecture (Ports & Adapters) -- паттерн, где приложение общается с внешним миром только через порты** (абстрактные интерфейсы), а конкретные технологии подключаются адаптерами (реализациями этих интерфейсов). Предложен Алистером Кокбёрном в 2005 году. Метафора шестиугольника подчёркивает, что граней (точек входа и выхода) много и все они равноправны: HTTP, CLI, тест -- лишь разные адаптеры к одному ядру.

```mermaid
graph LR
    subgraph "Внешний мир"
        HTTP[HTTP Client]
        CLI[CLI]
        MQ[Message Queue]
        DB[(Database)]
        EXT[External API]
    end
    subgraph "Primary Adapters"
        WA[Web Adapter]
        CA[CLI Adapter]
        MA[MQ Listener]
    end
    subgraph "Application Core"
        IP[Input Ports]
        APP[Application<br/>Services]
        DOM[Domain<br/>Model]
        OP[Output Ports]
    end
    subgraph "Secondary Adapters"
        PA[Persistence<br/>Adapter]
        EA[External API<br/>Adapter]
    end
    HTTP --> WA --> IP
    CLI --> CA --> IP
    MQ --> MA --> IP
    IP --> APP --> DOM
    APP --> OP
    OP --> PA --> DB
    OP --> EA --> EXT
```

Сравнение с Clean Architecture:

| Аспект | Hexagonal | Clean Architecture |
|--------|-----------|-------------------|
| **Автор** | Alistair Cockburn (2005) | Robert C. Martin (2012) |
| **Терминология** | Порты, адаптеры | Entities, Use Cases, Interface Adapters |
| **Слои** | 2 зоны: core и внешний мир | 4 концентрических кольца |
| **Фокус** | Взаимозаменяемость внешних систем | Направление зависимостей |
| **Общая идея** | Изоляция домена от инфраструктуры | Изоляция домена от инфраструктуры |

Главное, что стоит сказать на собеседовании: по сути это **один и тот же принцип** (изоляция домена через инверсию зависимостей) с разной терминологией и визуализацией. Clean Architecture детальнее проговаривает слои внутри ядра, Hexagonal -- абстрактнее и сильнее акцентирует взаимозаменяемость адаптеров. На практике их свободно комбинируют.

## Q9. Что такое порты (Ports) в гексагональной архитектуре?

**Port -- интерфейс, объявленный в ядре приложения, который описывает контракт с внешним миром.** Ключевое: порт принадлежит ядру (домену), а не инфраструктуре -- именно поэтому зависимость указывает внутрь. Порты бывают двух типов, по направлению вызова:

**Input Port (Driving Port)** -- описывает, что приложение умеет делать. Через него внешний мир инициирует действия (вызов идёт снаружи внутрь):

```java
// Input Port
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}

public record SendMoneyCommand(
    AccountId sourceAccountId,
    AccountId targetAccountId,
    Money amount
) {
    public SendMoneyCommand {
        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException(
                "Amount must be positive");
        }
    }
}
```

**Output Port (Driven Port)** -- описывает, что приложению нужно от внешнего мира. Через него ядро обращается к инфраструктуре (вызов идёт изнутри наружу):

```java
// Output Port
public interface LoadAccountPort {
    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}

// Output Port
public interface UpdateAccountStatePort {
    void updateActivities(Account account);
}
```

Принципиальное отличие в том, кто реализует порт. **Input Port реализует само ядро** (Use Case -- это его реализация), а **Output Port реализует адаптер** снаружи. Оба интерфейса объявлены внутри, поэтому ядро не ссылается ни на один внешний класс.

## Q10. Что такое адаптеры (Adapters) и какие их типы существуют?

**Adapter -- конкретная реализация, которая связывает порт с конкретной технологией.** Адаптер -- сменная деталь: чтобы заменить технологию (REST на gRPC, JPA на MongoDB), достаточно написать новый адаптер, не трогая ядро. Адаптеры делятся на два типа по тому, с какой стороны они подключены к ядру.

**Primary Adapters (Driving)** -- стоят на входе и сами вызывают Input Ports, инициируя сценарий:

```java
// Primary Adapter: REST Controller
@RestController
@RequestMapping("/accounts")
public class SendMoneyController {

    private final SendMoneyUseCase sendMoney;

    @PostMapping("/send/{sourceId}/{targetId}/{amount}")
    public ResponseEntity<Void> sendMoney(
            @PathVariable long sourceId,
            @PathVariable long targetId,
            @PathVariable long amount) {

        var command = new SendMoneyCommand(
            new AccountId(sourceId),
            new AccountId(targetId),
            Money.of(amount));

        sendMoney.sendMoney(command);

        return ResponseEntity.ok().build();
    }
}
```

**Secondary Adapters (Driven)** -- стоят на выходе и реализуют Output Ports, поставляя ядру инфраструктуру (БД, внешний сервис):

```java
// Secondary Adapter: JPA Repository
@Repository
public class AccountPersistenceAdapter
        implements LoadAccountPort, UpdateAccountStatePort {

    private final AccountJpaRepository accountRepo;
    private final ActivityJpaRepository activityRepo;
    private final AccountMapper mapper;

    @Override
    public Account loadAccount(AccountId accountId,
                               LocalDateTime baselineDate) {
        AccountJpaEntity entity = accountRepo
            .findById(accountId.value())
            .orElseThrow(() -> new EntityNotFoundException(
                "Account not found: " + accountId));

        List<ActivityJpaEntity> activities = activityRepo
            .findByOwnerSince(accountId.value(), baselineDate);

        return mapper.toDomain(entity, activities, baselineDate);
    }

    @Override
    public void updateActivities(Account account) {
        account.getActivityWindow().getActivities()
            .forEach(activity -> activityRepo.save(
                mapper.toJpaEntity(activity)));
    }
}
```

Запомнить разницу легко: Primary адаптер **вызывает** порт (зависит от Input Port), Secondary адаптер **реализует** порт (подставляется в Output Port через DI). В обоих случаях стрелка зависимости направлена к ядру -- правило зависимостей соблюдено.

## Q11. Чем отличаются Input (Driving) и Output (Driven) порты?

Коротко: **Input Port -- это «что приложение умеет», Output Port -- «что приложению нужно».** Отсюда вытекает и направление вызова, и кто реализует интерфейс.

| Характеристика | Input Port (Driving) | Output Port (Driven) |
|---------------|---------------------|---------------------|
| **Направление** | Внешний мир → Приложение | Приложение → Внешний мир |
| **Кто вызывает** | Primary Adapter (Controller) | Use Case (Interactor) |
| **Кто реализует** | Use Case / Application Service | Secondary Adapter |
| **Примеры** | `CreateOrderUseCase`, `SendMoneyUseCase` | `OrderRepository`, `PaymentGateway` |
| **Аналогия** | "Что я умею делать" | "Что мне нужно от внешнего мира" |

```mermaid
graph LR
    PA[Primary Adapter<br/>Controller] -->|вызывает| IP[Input Port<br/>UseCase interface]
    IS[Interactor<br/>реализует Input Port] -->|вызывает| OP[Output Port<br/>Repository interface]
    SA[Secondary Adapter<br/>JPA Repository] -->|реализует| OP
    IP -.->|implements| IS
    style IP fill:#4a7c2e,color:#fff
    style OP fill:#4a7c2e,color:#fff
    style IS fill:#2d5016,color:#fff
```

Важный нюанс, который часто путают на собеседовании: Input Port Use Case **реализует**, а Output Port Use Case **использует**. Но объявлены оба интерфейса в ядре приложения -- именно поэтому ядро не зависит ни от контроллера, ни от репозитория.

## Q12. Как связаны Hexagonal Architecture и Dependency Inversion Principle?

**DIP -- это механизм, который физически позволяет соблюсти правило зависимостей.** Без него «зависимости только внутрь» оставалось бы лозунгом: ведь Use Case реально нужна база. `Dependency Inversion Principle` (пятый принцип `SOLID`) формулируется так:

> Модули высокого уровня не должны зависеть от модулей низкого уровня. Оба должны зависеть от абстракций.

Применительно к Hexagonal это работает в три шага:

1. **Ядро** (высокий уровень) объявляет Output Port -- абстракцию того, что ему нужно
2. **Инфраструктура** (низкий уровень) реализует эту абстракцию
3. **Зависимость инвертируется**: компилируемая зависимость идёт от инфраструктуры к домену, хотя в runtime поток управления идёт от домена к инфраструктуре

В этом и фокус: направление вызова в рантайме (домен → БД) и направление зависимости в коде (БД → домен) противоположны. Порт-интерфейс -- та точка, где они расходятся.

```mermaid
graph TB
    subgraph "Без DIP (плохо)"
        UC1[Use Case] -->|зависит от| DB1[JpaRepository]
    end
    subgraph "С DIP (хорошо)"
        UC2[Use Case] -->|зависит от| PORT[Port: OrderRepository]
        ADAPTER[JpaOrderRepository] -->|реализует| PORT
    end
    style UC1 fill:#8b0000,color:#fff
    style DB1 fill:#8b0000,color:#fff
    style UC2 fill:#2d5016,color:#fff
    style PORT fill:#4a7c2e,color:#fff
    style ADAPTER fill:#6ba34a,color:#fff
```

Без DIP: `Use Case → JPA Repository` (домен зависит от инфраструктуры).
С DIP: `Use Case → Port ← Adapter` (инфраструктура зависит от домена).

В Spring это реализуется автоматически через `@Autowired` / constructor injection: Spring подставляет `JpaOrderRepository` вместо `OrderRepository` в runtime.

## Q13. Что такое Onion Architecture?

**Onion Architecture** Джеффри Палермо помещает доменную модель в центр, а инфраструктуру и UI — на периферию. Внутренний код может зависеть только от более центральных слоёв; число промежуточных колец зависит от приложения. Контракты хранения задают в ядре, а конкретную БД подключают снаружи.

С Hexagonal подход разделяет идею изоляции приложения от внешних деталей. Onion делает акцент на концентрических слоях и доменной модели, Hexagonal — на целевых разговорах приложения с внешними агентами через порты. Это не фиксированные альтернативы друг другу. [Palermo](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/), [Cockburn](https://alistair.cockburn.us/hexagonal-architecture/).

## Q14. Сравните Clean, Hexagonal и Onion Architecture

Все три архитектуры -- **вариации одной идеи**: бизнес-логика в центре, инфраструктура на периферии, зависимости направлены внутрь через DIP. Различаются они терминологией, метафорой и степенью детализации слоёв, но не сутью.

| Критерий | Clean Architecture | Hexagonal Architecture | Onion Architecture |
|----------|-------------------|----------------------|-------------------|
| **Автор** | Robert C. Martin (2012) | Alistair Cockburn (2005) | Jeffrey Palermo (2008) |
| **Визуализация** | Концентрические кольца | Шестиугольник | "Луковица" |
| **Слои** | 4: Entities, Use Cases, Adapters, Frameworks | 2 зоны: Core и Internal/External | 4: Domain Model, Domain Services, App Services, Infra |
| **Терминология** | Entities, Interactors | Ports, Adapters | Onion layers |
| **Фокус** | Dependency Rule | Взаимозаменяемость через порты | Доменная модель в центре |
| **Use Cases** | Явно выделены | Часть Application Core | Application Services |
| **DIP** | Ключевой принцип | Ключевой принцип | Ключевой принцип |

На практике термины свободно смешивают, и это нормально. Собеседователь ждёт, что вы:
1. Понимаете, что все три подхода -- **вариации одной идеи** (а не три конкурирующие теории)
2. Знаете терминологию каждого и не путаетесь в ней
3. Умеете применить любой из них на практике, выбирая по контексту, а не по моде

## Q15. (!) Чем Clean Architecture отличается от традиционной слоистой архитектуры?

Clean Architecture и обычная слоистая структура различаются не именами пакетов, а **направлением зависимостей исходного кода**. В типичной N-tier реализации бизнес-сервис напрямую знает `JpaRepository` и ORM-модель. Тогда изменение механизма хранения может затронуть правила сценария.

Слоистое приложение тоже может использовать DIP и сохранить чистое ядро; ярлык `Layered` сам по себе не означает нарушение. Проверяют конкретные импорты и контракты: высокоуровневый сценарий зависит от своей абстракции, а JPA-адаптер реализует её. UI и БД остаются работающими деталями, а не запрещёнными технологиями.

## Q16. Что такое Vertical Slice Architecture и когда она предпочтительнее?

**Vertical Slice Architecture -- организация кода по бизнес-функциям (features / slices), а не по техническим слоям.** Вместо горизонтальных пакетов `controllers/`, `services/`, `repositories/` код режется вертикально: каждый slice содержит всё нужное для одного сценария -- от контроллера до запроса в БД. Это противоположность разрезу по слоям.

```
// Традиционный подход (по слоям)
controllers/
  OrderController.java
  ProductController.java
services/
  OrderService.java
  ProductService.java
repositories/
  OrderRepository.java
  ProductRepository.java

// Vertical Slices (по фичам)
features/
  create-order/
    CreateOrderCommand.java
    CreateOrderHandler.java
    CreateOrderValidator.java
  cancel-order/
    CancelOrderCommand.java
    CancelOrderHandler.java
```

Сравнение:

| Аспект | Clean Architecture | Vertical Slices |
|--------|-------------------|----------------|
| **Организация кода** | По архитектурным слоям | По бизнес-фичам |
| **Связанность** | Слои связаны через абстракции | Slices независимы друг от друга |
| **Переиспользование** | Высокое (общие Use Cases) | Минимальное (каждый slice самодостаточен) |
| **Сложность для простых CRUD** | Высокая (много слоёв ради простой операции) | Низкая (один файл на операцию) |
| **Когда выбирать** | Сложный домен, долгоживущий проект | CRUD-heavy, микросервисы, быстрая разработка |

**Когда Vertical Slices предпочтительнее:** CRUD-heavy приложения, микросервисы, быстрая разработка -- там, где фичи независимы и слои Clean Architecture только добавляют церемоний без выгоды. **Когда выигрывает Clean Architecture:** сложный домен и долгоживущий проект, где переиспользование Use Cases и изоляция домена окупают накладные расходы.

На практике подходы комбинируют: Clean Architecture для сложного ядра + Vertical Slices для feature-модулей. Именно так устроен наш проект (см. `feature/` в [Spring Framework](../frameworks/spring/spring-framework-interview.md)).

## Q17. Как организовать пакетную структуру Spring Boot проекта по Clean Architecture?

Структуру пакетов выбирают так, чтобы было видно бизнес-области и можно было проверить границы зависимостей. Например, внутри `orders/` могут находиться `domain`, `application`, `adapter/in/web`, `adapter/out/persistence` и `config`. Для небольшого модуля можно обойтись меньшим числом папок.

Важнее дерево импортов: доменные классы не знают Spring MVC и JPA; use case зависит от контракта нужного ему порта; адаптер импортирует этот контракт. `@SpringBootApplication` и конфигурацию component scan размещают так, чтобы нужные адаптеры и composition root действительно собирались. Один универсальный набор четырёх верхнеуровневых пакетов архитектура не предписывает.

## Q18. Как реализовать Input Port и Use Case Interactor в Java?

Входной порт описывает действие приложения, например `TransferMoney.execute(command)`. Interactor реализует сценарий: загружает данные через выходной порт, вызывает доменные операции и сохраняет результат. Его зависимости передают через конструктор; HTTP DTO и JPA-классы не входят в контракт сценария.

Интерфейс входного порта полезен, когда есть несколько вызывающих адаптеров, декоратор или отдельная модульная граница. Для простого случая класс use case тоже может быть достаточным. Аннотация `@Service` на классе application — осознанная зависимость от Spring, а не логическое доказательство неправильной архитектуры; степень независимости выбирают явно.

## Q19. Как реализовать Output Port и его адаптер?

```java
// === Output Port (определён в application layer) ===
public interface LoadAccountPort {
    Account load(AccountId accountId);
}

public interface UpdateAccountPort {
    void update(Account account);
}

// === Persistence Model (не доменная!) ===
@Entity
@Table(name = "accounts")
class AccountJpaEntity {
    @Id
    private Long id;
    private String holderName;
    private BigDecimal balance;

    // JPA-specific: конструктор без аргументов, геттеры, сеттеры
}

// === Secondary Adapter (реализация Output Port) ===
@Repository
class AccountPersistenceAdapter
        implements LoadAccountPort, UpdateAccountPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    AccountPersistenceAdapter(
            AccountJpaRepository jpaRepository,
            AccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account load(AccountId accountId) {
        AccountJpaEntity entity = jpaRepository
            .findById(accountId.value())
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        return mapper.toDomain(entity);
    }

    @Override
    public void update(Account account) {
        AccountJpaEntity entity = mapper.toJpaEntity(account);
        jpaRepository.save(entity);
    }
}

// === Mapper между domain и persistence моделями ===
@Component
class AccountPersistenceMapper {

    Account toDomain(AccountJpaEntity entity) {
        return Account.reconstitute(
            new AccountId(entity.getId()),
            entity.getHolderName(),
            Money.of(entity.getBalance())
        );
    }

    AccountJpaEntity toJpaEntity(Account domain) {
        var entity = new AccountJpaEntity();
        entity.setId(domain.getId().value());
        entity.setHolderName(domain.getHolderName());
        entity.setBalance(domain.getBalance().amount());
        return entity;
    }
}
```

Ключевой момент всей конструкции: доменная модель `Account` и JPA-модель `AccountJpaEntity` -- **два разных класса**, между которыми стоит маппер. Да, это лишний класс и лишний маппинг. Но взамен схема БД может меняться (денормализация, переименование колонок), не задевая домен, а домен может иметь поля, которых в БД нет вовсе. Адаптер (`@Repository`) и маппер -- единственное место, которое знает обе модели сразу.

Application объявляет output port по потребности сценария, а внешний JPA- или HTTP-адаптер реализует контракт. Use case не импортирует JpaRepository; бизнес-инварианты не переносят в persistence adapter.

## Q20. (!) Как Spring Boot вписывается в Clean Architecture?

Spring Boot создаёт web и persistence адаптеры, связывает их с use cases и управляет инфраструктурными механизмами. Домен может оставаться обычными Java-классами без Spring-аннотаций. Use case также может быть чистым POJO, который собирает `@Configuration`.

Другой допустимый компромисс — `@Service` на application-классе ради простой сборки. Тогда нужно честно понимать зависимость application от Spring и проверять, что бизнес-правила не переехали в controller или JPA-адаптер. Граница транзакции должна реально охватить нужные локальные изменения; Spring proxy применяется к внешним вызовам через proxy, а self-invocation его обходит. [Spring](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

## Q21. Как правильно передавать данные между слоями (DTO, Domain Model, Entity)?

На границе переводят данные, если внешнее представление имеет другой контракт: HTTP-запрос, прикладная команда, доменный объект и JPA-запись могут быть разными типами. Это защищает бизнес-правила от изменения JSON, схемы БД и ошибок сериализации.

Отдельный класс на каждый переход не обязателен. Если простая внутренняя структура не несёт внешних аннотаций и не связывает независимые контракты, её можно передать без дополнительного маппера. Решение принимают по реальной границе изменений, а не по числу нарисованных колец.

## Q22. Как обрабатывать транзакции в Clean Architecture?

Транзакционная граница обычно охватывает **локальные** операции одного сценария, которые должны коммититься вместе. Само бизнес-правило не обязано знать `PlatformTransactionManager` или JDBC. Границу можно задать на вызываемом Spring-фасаде, декораторе, конфигурации или через явный исполнитель транзакции снаружи ядра.

`@Transactional` на application-сервисе — возможный прагматичный выбор, если зависимость от Spring принята. Проверяют реальный путь вызова: в proxy-режиме self-invocation не открывает новую транзакцию. Вызовы другой БД или внешнего HTTP-сервиса не становятся частью локального commit; для межсервисного процесса нужна отдельная схема согласования. [Spring transactions](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

## Q23. Какие преимущества для тестирования даёт Clean Architecture?

**Главная выгода для тестов -- бизнес-логика отвязана от инфраструктуры, поэтому большинство тестов не требуют ни БД, ни Spring Context и выполняются за миллисекунды.** Это даёт здоровую пирамиду тестов, а не «ледяной конус» из медленных интеграционных проверок.

**1. Unit-тесты доменных сущностей -- без моков и фреймворков:**

```java
@Test
void order_cannot_be_submitted_when_empty() {
    Order order = Order.create(customerId);

    assertThrows(EmptyOrderException.class, order::submit);
}

@Test
void adding_line_recalculates_total() {
    Order order = Order.create(customerId);

    order.addLine(product, 2);

    assertEquals(Money.of(200), order.getTotalPrice());
}
```

**2. Unit-тесты Use Cases -- только моки портов:**

```java
@Test
void transfers_money_between_accounts() {
    // Given
    var loadPort = mock(LoadAccountPort.class);
    var updatePort = mock(UpdateAccountPort.class);
    var useCase = new TransferMoneyService(loadPort, updatePort,
        new FixedIdGenerator("tx-123"));

    when(loadPort.load(sourceId)).thenReturn(sourceAccount);
    when(loadPort.load(targetId)).thenReturn(targetAccount);

    // When
    var result = useCase.execute(
        new TransferMoneyCommand(sourceId, targetId, Money.of(100)));

    // Then
    verify(updatePort).update(sourceAccount);
    verify(updatePort).update(targetAccount);
    assertEquals(Money.of(900), result.newBalanceFrom());
}
```

**3. Интеграционные тесты адаптеров -- изолированно от бизнес-логики:**

```java
@DataJpaTest
class AccountPersistenceAdapterTest {

    @Autowired private AccountJpaRepository jpaRepo;
    private AccountPersistenceAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new AccountPersistenceAdapter(
            jpaRepo, new AccountPersistenceMapper());
    }

    @Test
    void loads_account_from_database() {
        jpaRepo.save(new AccountJpaEntity(1L, "Alice", new BigDecimal("1000")));

        Account account = adapter.load(new AccountId(1L));

        assertEquals("Alice", account.getHolderName());
    }
}
```

Итоговая пирамида тестирования в Clean Architecture:
- **Много** быстрых unit-тестов домена и Use Cases (без Spring Context, миллисекунды)
- **Несколько** интеграционных тестов адаптеров (`@DataJpaTest`, `@WebMvcTest`)
- **Мало** медленных end-to-end тестов (`@SpringBootTest`, полный контекст + БД)

Логика проста: дешёвые тесты проверяют то, что меняется чаще всего (бизнес-правила), а дорогие медленные тесты остаются точечной проверкой проводки между слоями.

## Q24. Как тестировать Use Case без инфраструктуры?

Поскольку Use Case обращается к миру только через Output Ports (интерфейсы), в тесте достаточно подсунуть вместо реальных адаптеров либо моки, либо in-memory реализации. БД и сеть не нужны.

```java
// In-memory реализация Output Port для тестов
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> store = new HashMap<>();

    @Override
    public Order findById(OrderId id) {
        return Optional.ofNullable(store.get(id))
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }

    // Тестовый хелпер
    public int count() {
        return store.size();
    }
}

// Тест Use Case с in-memory адаптером
@Test
void creates_order_and_saves_it() {
    var orderRepo = new InMemoryOrderRepository();
    var customerRepo = new InMemoryCustomerRepository();
    customerRepo.save(testCustomer);
    var productRepo = new InMemoryProductRepository();
    productRepo.save(testProduct);
    var notifier = mock(OrderNotificationPort.class);

    var useCase = new CreateOrderInteractor(
        customerRepo, productRepo, orderRepo, notifier);

    var response = useCase.execute(new CreateOrderRequest(
        testCustomer.getId().value(),
        List.of(new OrderLineRequest(testProduct.getId().value(), 3))
    ));

    assertNotNull(response.orderId());
    assertEquals(1, orderRepo.count());
    verify(notifier).notifyOrderCreated(any());
}
```

**In-memory адаптер vs мок -- когда что:** мок проверяет факт вызова (`verify(notifier).notifyOrderCreated(...)`), а in-memory реализация проверяет реальное поведение -- сохранённый объект потом можно прочитать тем же портом. In-memory тесты устойчивее к рефакторингу: они не ломаются от смены внутренней последовательности вызовов, пока итоговое состояние верно. Для запросов чтения/записи (репозитории) обычно берут in-memory, для побочных эффектов (уведомления) -- мок.

## Q25. Как использовать ArchUnit для проверки архитектурных правил?

`ArchUnit` -- библиотека, которая проверяет архитектурные правила обычным JUnit-тестом. Зачем она нужна: правило зависимостей легко проговорить, но без автоматической проверки оно нарушается уже на втором месяце -- кто-нибудь импортирует `@Service` в домен «на минутку». ArchUnit ловит это на CI и превращает архитектуру из договорённости в исполняемый контракт.

```java
@AnalyzeClasses(packages = "com.example.myapp")
class CleanArchitectureRulesTest {

    // Правило: domain не зависит от application, adapter, config
    @ArchTest
    static final ArchRule domain_should_not_depend_on_outer_layers =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..application..", "..adapter..", "..config..");

    // Правило: application не зависит от adapter и config
    @ArchTest
    static final ArchRule application_should_not_depend_on_adapters =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..adapter..", "..config..");

    // Правило: domain не использует Spring-аннотации
    @ArchTest
    static final ArchRule domain_should_not_use_spring =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("org.springframework..");

    // Правило: domain не использует JPA
    @ArchTest
    static final ArchRule domain_should_not_use_jpa =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("javax.persistence..", "jakarta.persistence..");

    // Hexagonal architecture rule (встроенная поддержка ArchUnit)
    @ArchTest
    static final ArchRule hexagonal =
        Architectures.onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("web", "..adapter.in.web..")
            .adapter("persistence", "..adapter.out.persistence..")
            .adapter("external", "..adapter.out.external..");
}
```

Так это и работает: тест запускается вместе с остальными, и если кто-то добавит `import org.springframework.stereotype.Service` в доменный класс, сборка упадёт на CI с понятным сообщением о нарушенном правиле -- задолго до code review.

## Q26. (!) Какие типичные антипаттерны нарушают Clean Architecture?

Все эти антипаттерны объединяет одно: **зависимость потекла не в ту сторону** -- внутренний слой узнал о внешнем. Разберём пять самых частых.

**1. JPA-аннотации на доменной сущности:**

```java
// АНТИПАТТЕРН: домен зависит от JPA
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
}
```

Чем плохо: домен теперь не существует без JPA на classpath, а схема БД диктует структуру доменной модели. Лечится **отдельной JPA-моделью** и маппером (Q19).

**2. Use Case знает о HTTP:**

```java
// АНТИПАТТЕРН: Use Case возвращает ResponseEntity
public class CreateOrderService {
    public ResponseEntity<OrderDto> execute(...) { // HTTP в Use Case!
        ...
    }
}
```

Чем плохо: Use Case завязался на web-фреймворк и больше не вызывается из CLI или очереди. Должен возвращать доменный объект или простой DTO, а упаковку в `ResponseEntity` делает контроллер.

**3. «Прозрачный» сервис (Pass-through Service):**

```java
// АНТИПАТТЕРН: сервис только делегирует в репозиторий
public class OrderService {
    public Order findById(Long id) {
        return orderRepository.findById(id);  // нет бизнес-логики
    }
}
```

Чем плохо: лишний слой без ценности -- сервис только пробрасывает вызов. Если так выглядят все сервисы, архитектура избыточна для этого случая, проще убрать слой (см. Q27).

**4. Shared domain model (один класс на все слои):**

Один и тот же класс `Order` служит доменной сущностью, JPA-моделью и HTTP DTO. Поначалу это экономит код, но связывает все слои в один узел: добавили поле для JSON -- поменялась схема БД; поменяли тип в БД -- сломали API. Слои перестают эволюционировать независимо.

**5. Бизнес-логика в контроллере:**

```java
// АНТИПАТТЕРН
@PostMapping("/orders")
public ResponseEntity<OrderDto> create(@RequestBody CreateOrderDto dto) {
    if (dto.items().isEmpty()) throw new BadRequestException("...");
    var order = new Order();
    order.setStatus("CREATED");
    orderRepo.save(order);  // контроллер работает с репозиторием напрямую
    return ResponseEntity.ok(mapper.toDto(order));
}
```

Чем плохо: бизнес-правило (`items.isEmpty()`) живёт в web-слое и не переиспользуется, контроллер лезет в репозиторий мимо Use Case. Контроллер должен только адаптировать формат и делегировать Use Case.

## Q27. Когда Clean Architecture -- это overkill?

Полный набор интерфейсов, DTO и мапперов может стоить больше, чем защищает, если приложение почти не содержит бизнес-правил. Для простого CRUD или короткоживущего прототипа часто достаточно ясного разделения web, сценария и хранения без отдельного класса на каждое кольцо.

Граница определяется не числом endpoints и не размером команды, а ожидаемыми изменениями, инвариантами и стоимостью тестирования. Можно начать с одной явной границы к внешней системе и усилить модульность по мере роста домена. `Clean Architecture` — набор правил зависимостей, а не обязательный шаблон каталога.

## Q28. (!) Как избежать анемичной доменной модели в Clean Architecture?

**Анемичная модель -- это объект из одних геттеров/сеттеров, у которого всю логику забрали сервисы.** Формально слои на месте, но Entity перестала быть носителем бизнес-правил и превратилась в structure-холдер. Лекарство одно: вернуть поведение внутрь объекта, сделав его богатым (Rich Domain Model).

```java
// АНЕМИЧНАЯ модель (антипаттерн)
public class Account {
    private BigDecimal balance;
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}

public class AccountService {
    public void withdraw(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        account.setBalance(account.getBalance().subtract(amount));
    }
}
```

```java
// БОГАТАЯ модель (правильно)
public class Account {
    private Money balance;

    public void withdraw(Money amount) {
        if (balance.isLessThan(amount)) {
            throw new InsufficientFundsException(
                "Cannot withdraw " + amount + ", balance is " + balance);
        }
        this.balance = balance.subtract(amount);
    }

    public void deposit(Money amount) {
        this.balance = balance.add(amount);
    }

    // Нет setBalance()! Баланс меняется только через бизнес-операции.
}
```

Разница принципиальная: в анемичном варианте `AccountService.withdraw` может забыть проверку баланса, и снаружи никто не помешает вызвать `setBalance(-1000)`. В богатой модели единственный путь изменить баланс -- через `withdraw`/`deposit`, и проверка инварианта неминуема.

Принципы богатой модели:
1. **Нет публичных сеттеров** -- состояние меняется только через именованные бизнес-методы
2. **Инварианты проверяются внутри** -- объект невозможно привести в невалидное состояние
3. **Value Objects вместо примитивов** (`Money` вместо `BigDecimal`, `OrderId` вместо `Long`) -- тип сам исключает ошибки вроде передачи суммы туда, где ждут id
4. **Решения принимаются в Entity**, а не в сервисе -- сервис лишь оркеструет

Подробнее о паттернах проектирования домена -- в [Domain-Driven Design](ddd-interview.md).

## Q29. Как применять Clean Architecture в микросервисах?

**В микросервисах Clean Architecture работает внутри каждого сервиса, а не на уровне всей системы.** Один микросервис -- одно мини-приложение со своими слоями. На уровне системы границы задают уже не слои, а Bounded Context-ы и сетевые контракты.

```mermaid
graph TB
    subgraph "Order Service"
        OC[REST Adapter] --> OUC[Use Cases] --> OD[Domain]
        OUC --> ODB[DB Adapter]
        OUC --> OKA[Kafka Adapter]
    end
    subgraph "Payment Service"
        PC[REST Adapter] --> PUC[Use Cases] --> PD[Domain]
        PUC --> PDB[DB Adapter]
        PUC --> PEA[External API Adapter]
    end
    OKA -->|event| PC
```

Рекомендации:

**1. Каждый сервис -- отдельный Bounded Context с собственным доменом:**
- Не расшаривайте доменные модели между сервисами
- Используйте `Anti-Corruption Layer` (адаптер) для преобразования данных из другого сервиса

**2. Межсервисная коммуникация -- через Output Port:**

```java
// Output Port в Order Service
public interface PaymentPort {
    PaymentResult requestPayment(OrderId orderId, Money amount);
}

// Adapter: HTTP-клиент к Payment Service
@Component
public class PaymentServiceHttpAdapter implements PaymentPort {
    private final WebClient webClient;

    @Override
    public PaymentResult requestPayment(OrderId orderId, Money amount) {
        return webClient.post()
            .uri("/api/payments")
            .bodyValue(new PaymentRequest(orderId.value(), amount.amount()))
            .retrieve()
            .bodyToMono(PaymentResponse.class)
            .map(this::toDomain)
            .block();
    }
}
```

Удобство в том, что другой сервис -- такой же внешний мир, как БД: обращение к нему прячется за обычным Output Port (`PaymentPort`), а HTTP/gRPC становится сменным адаптером.

**3. Не перестарайтесь**: простой CRUD-микросервис не нуждается в полной Clean Architecture. Сложность архитектуры должна быть пропорциональна сложности домена сервиса, а не одинаковой для всех.

Подробнее о межсервисном взаимодействии -- в [Микросервисы](microservices-interview.md).

## Q30. Как эволюционно мигрировать монолит к Clean Architecture?

**Мигрировать нужно постепенно, на одном Bounded Context за раз, а не переписывать всё разом (Big Bang).** Big Bang рискован: долгая ветка, конфликты, нулевая ценность до самого конца. Пошаговый путь даёт работающую систему на каждом шаге. Порядок шагов выбран так, чтобы каждый следующий опирался на предыдущий.

**Шаг 1. Отделить доменную модель от JPA:**

```java
// Было: JPA-сущность = доменная модель
@Entity
public class Order { @Id private Long id; ... }

// Стало: отдельная доменная модель
public class Order { private OrderId id; ... }  // domain

@Entity
class OrderJpaEntity { @Id private Long id; ... }  // persistence
```

**Шаг 2. Ввести Output Ports для репозиториев:**

```java
// Было: сервис зависит от JPA-репозитория
@Service
public class OrderService {
    @Autowired private JpaOrderRepository repo;
}

// Стало: сервис зависит от абстракции
public class OrderService {
    private final OrderRepository repo;  // интерфейс в domain/application
}
```

**Шаг 3. Выделить Use Cases из "толстых" сервисов:**

```java
// Было: один OrderService с 20 методами

// Стало: отдельные Use Cases
public class CreateOrderService implements CreateOrderUseCase { ... }
public class CancelOrderService implements CancelOrderUseCase { ... }
public class ShipOrderService implements ShipOrderUseCase { ... }
```

**Шаг 4. Разделить модули (Gradle/Maven):**

```
modules/
  order-domain/      # domain model, ports
  order-application/ # use cases
  order-adapter/     # controllers, persistence, external
  order-boot/        # Spring Boot main, config
```

**Шаг 5. Добавить ArchUnit-тесты** для предотвращения регрессии архитектуры.

Главное -- начать с одного Bounded Context, на нём доказать команде и себе ценность подхода (тесты стали быстрее, БД стала сменной), и только потом раскатывать на остальную систему. Так миграция остаётся обратимой и не блокирует поставку фич.

## Q31. (!) Как правильно реализовать Use Case Interactor с несколькими портами?

Interactor получает только нужные ему порты через конструктор. Он решает, в каком порядке проверять правила и вызывать доменные операции; конкретные JPA, HTTP и брокерные классы остаются в адаптерах. Если список портов разрастается, проверяют, не объединены ли несколько сценариев в один класс.

Граница локальной транзакции охватывает только ресурсы, которые действительно участвуют в ней. Вызов внешнего Inventory API и последующая публикация сообщения не становятся атомарными от `@Transactional` на interactor. Для надёжного события используют outbox или другой согласованный механизм; для межсервисной компенсации проектируют отдельный процесс. [Spring transactions](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

## Q32. Как организовать обработку ошибок в Use Case без исключений инфраструктуры?

Ожидаемый бизнес-исход, например «недостаточно средств», выражают прикладным результатом или доменным исключением. Web-адаптер переводит его в HTTP-ответ; use case не возвращает `ResponseEntity`.

Технический сбой БД или сети не нужно маскировать под нарушение бизнес-правила. Адаптер может преобразовать низкоуровневое исключение в прикладной тип сбоя с сохранением причины. Слой обработки ошибок выбирает retry, отказ или недоступность, а журнал сохраняет диагностику. Один `null` для всех ошибок теряет смысл и не даёт надёжного контракта.

## Q33. (!) Как проверить соблюдение Dependency Rule через ArchUnit?

ArchUnit проверяет зависимости скомпилированных Java-классов обычным тестом. Правила можно выразить как запрет домену зависеть от web/JPA-пакетов и application зависеть от адаптеров, либо описать допустимые слои. Проверка должна охватывать **реальные** пакеты проекта, включая вложенные feature-пакеты.

Перед CI проверяют, что правило выбрало ожидаемые классы и падает на намеренном нарушении; пустая область проверки создаёт ложное чувство защиты. Архитектурный тест не оценивает смысл бизнес-решения и не заменяет ревью. [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html).

## Q34. (!) Чем Clean Architecture отличается от Hexagonal (Ports & Adapters) — ключевые отличия?

Оба подхода отделяют приложение от внешних деталей. **Hexagonal Architecture** называет границу портом, а технологическую реализацию адаптером; внешние агенты могут инициировать сценарий или обслуживать потребность приложения. **Clean Architecture** дополнительно показывает отдельные политики Entities и Use Cases, а также слои Interface Adapters и Frameworks.

Это не взаимоисключающие схемы. Порты и адаптеры можно использовать внутри Clean Architecture. В обоих случаях исходные зависимости направляют к ядру: `Controller → Use Case → Entity`, а для внешней потребности — `Use Case → Port ← Persistence Adapter`. Стрелка вызова во время работы может идти в обратную сторону. Количество пакетов и способ деплоя ни один подход не фиксирует. [Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html), [Cockburn](https://alistair.cockburn.us/hexagonal-architecture/).

## Q35. Как реализовать Presenter паттерн в Clean Architecture?

Presenter преобразует результат use case в форму, удобную конкретному внешнему представлению. В классическом варианте Clean Architecture use case вызывает выходную границу, а presenter реализует её и строит view model.

Для обычного REST endpoint отдельный presenter не всегда нужен: use case может вернуть прикладной результат, а controller или mapper превратит его в HTTP DTO. Важно, чтобы сам use case не импортировал `ResponseEntity`, JSON-аннотации и компоненты UI. Выбор формы зависит от сложности презентации, а не от обязательного шаблона.

Presenter не является доменной Entity и не форматирует JSON внутри ядра. Для простого ответа controller может построить view model из прикладного DTO без отдельного output boundary.

## Q36. Как организовать тестирование на каждом уровне Clean Architecture?

Разделённые границы позволяют проверить доменные правила обычными unit-тестами без Spring и БД. Use case тестируют с fake или mock реализациями нужных портов. Адаптер хранения проверяют с настоящим механизмом доступа к данным, web-адаптер — с HTTP-маршрутизацией и сериализацией.

Сквозные тесты остаются нужны для конфигурации, транзакций и ключевых пользовательских потоков. Никакая схема пакетов не создаёт тесты «бесплатно»: нужно проверить, что fake действительно отражает контракт порта, а wiring связывает правильные реализации. Для Spring можно использовать подходящие test slices, например `@DataJpaTest` для JPA и `@WebMvcTest` для MVC, если они соответствуют фактическому стеку.

## Q37. Как реализовать Dependency Injection без Spring IoC контейнера?

**DI -- это принцип (зависимости приходят извне), а IoC-контейнер вроде Spring -- лишь один из способов его реализовать.** Без контейнера сборку делают вручную в **Composition Root** -- единственной точке, где приложение создаёт и связывает все объекты. Это доказывает важную мысль: Clean Architecture не требует Spring, домен остаётся переносимым.

**Ручная сборка через Composition Root:**

```java
// Composition Root — точка сборки всего приложения
// В Clean Architecture это часть слоя Frameworks & Drivers
public class AppCompositionRoot {

    public static OrderController buildOrderController() {
        // Создаём инфраструктурные зависимости
        DataSource dataSource = buildDataSource();
        OrderJpaRepository jpaRepo = new OrderJpaRepository(dataSource);
        InventoryHttpAdapter inventoryAdapter = new InventoryHttpAdapter("http://inventory-svc");
        KafkaOrderNotifier notifier = new KafkaOrderNotifier(buildKafkaProducer());

        // Создаём Use Case, внедряя порты
        PlaceOrderOutputPort presenter = new PlaceOrderJsonPresenter();
        PlaceOrderInteractor useCase = new PlaceOrderInteractor(
            jpaRepo,          // implements OrderRepository (Output Port)
            inventoryAdapter, // implements InventoryPort (Output Port)
            notifier,         // implements OrderNotificationPort (Output Port)
            presenter         // Output Port для ответа
        );

        // Возвращаем Controller (Input Adapter)
        return new OrderController(useCase);
    }
}
```

**Pure DI в тестах — без фреймворков:**

```java
class PlaceOrderIntegrationTest {
    @Test
    void full_scenario_without_spring() {
        // Stub-адаптеры вместо реальной инфраструктуры
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        StubInventoryPort inventory = StubInventoryPort.withAllAvailable();
        CapturingOutputPort output = new CapturingOutputPort();

        PlaceOrderInteractor useCase = new PlaceOrderInteractor(repo, inventory, output);
        useCase.placeOrder(new PlaceOrderCommand(List.of(item)));

        assertThat(repo.count()).isEqualTo(1);
        assertThat(output.getLastResult().isSuccess()).isTrue();
    }
}
```

**Когда Spring IoC подходит:** в production Spring (`@Component`, `@Bean`) -- правильный выбор, ручная сборка масштабируется плохо. В этом случае Composition Root -- это `@Configuration`-классы. Важно понять: Spring просто автоматизирует ту же ручную сборку и не нарушает Clean Architecture, пока Domain и Use Cases не импортируют его аннотации.

**Правило:** `@Component`, `@Service` допустимы в адаптерах (и, по компромиссу из Q22, в Interactor-ах), но Domain Entities о Spring знать не должны. Ручной Composition Root полезен и тогда, когда вы остаётесь на Spring -- в тестах он позволяет собирать сценарий без поднятия контекста.

## Q38. Как интегрировать агрегаты и Domain Events в Clean Architecture?

Агрегат может регистрировать доменные факты во время изменения своего состояния, не зная Kafka или HTTP. Use case получает эти факты после проверки инвариантов и передаёт наружу через подходящий порт или сохраняет их вместе с изменением агрегата.

`repository.save(order)` и затем `publisher.publish(events)` — два независимых эффекта, даже если метод помечен `@Transactional`: при отказе между ними сообщение может потеряться, а публикация до commit может сообщить о незафиксированном изменении. Если событие должно надёжно уйти после commit, его записывают в outbox той же локальной транзакцией и публикуют отдельно. Domain Event не обязательно означает Event Sourcing.

## Q39. Что такое Screaming Architecture и как она связана с Clean Architecture?

**Screaming Architecture** означает, что верхний уровень проекта рассказывает о предметной области и сценариях, а не только о фреймворке. Папки `orders`, `payments`, `inventory` помогают увидеть назначение системы раньше, чем `controllers`, `services`, `repositories`.

Этот принцип совместим с Clean Architecture: внутри бизнес-области по-прежнему можно проверять направление зависимостей между доменом, сценарием и адаптерами. Сами названия папок ничего не гарантируют — важны реальные импорты и границы изменений. [Martin](https://blog.cleancoder.com/uncle-bob/2011/09/30/Screaming-Architecture.html).

## Q40. (!) Как эволюционировать от Clean Architecture к Vertical Slice Architecture?

Vertical Slice группирует код по бизнес-сценарию, чтобы изменение одной фичи требовало меньше переходов между общими техническими папками. Переход можно делать постепенно: новый сценарий разместить рядом со своим входом, обработчиком и адаптерами, а старый код переносить по мере работы над ним.

Общие доменные инварианты не дублируют механически в каждом срезе. Внутри сложной фичи по-прежнему могут действовать Dependency Rule и чистая модель; простая CRUD-фича может быть плоской. Цель — локальность изменения при ясных границах, а не отказ от независимости ядра по факту переименования папок. [Bogard](https://www.jimmybogard.com/vertical-slice-architecture/).

## Q41. Как правильно реализовать пакетную структуру Spring Boot проекта в стиле Clean Architecture на практике?

Практичная структура Spring Boot показывает **предметные области и зависимости**. Например, в `orders/` могут быть `domain/Order`, `application/PlaceOrder`, `application/OrderStore`, `adapter/in/OrderController`, `adapter/out/JpaOrderStore` и внешняя конфигурация. Это пример, а не стандарт из фиксированных четырёх пакетов.

Сборка адаптеров живёт у внешней границы; component scan или явные `@Bean` должны находить нужные реализации. Домен не импортирует Spring MVC и JPA, application зависит от своих контрактов, а адаптер реализует их. Модульные зависимости и ArchUnit могут закрепить правило; одно название пакета не доказывает его соблюдение.

## See also

- [Domain-Driven Design](ddd-interview.md) — тактические и стратегические паттерны DDD, Bounded Context и агрегаты
- [Design Patterns](../design-patterns/design-patterns-interview.md) — SOLID, GoF и архитектурные паттерны, лежащие в основе Clean Architecture
- [Микросервисы](microservices-interview.md) — применение Clean Architecture в микросервисной среде
- [CQRS и Event Sourcing](cqrs-event-sourcing-interview.md) — разделение моделей чтения и записи в рамках чистой архитектуры
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) — Circuit Breaker и Retry в слое Infrastructure/Adapter
- [Паттерны масштабируемости](scalability-patterns-interview.md) — горизонтальное масштабирование модулей чистой архитектуры
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — практическая реализация Clean Architecture со Spring IoC и Spring Data
- [Шпаргалка: Clean Architecture](../../architecture/clean-architecture.md) — теория
- [ArchUnit](../testing/archunit-interview.md) — автоматическая проверка зависимостей между слоями в тестах
