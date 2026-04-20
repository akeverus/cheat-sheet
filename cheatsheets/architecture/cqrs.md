---
title: "CQRS (Command Query Responsibility Segregation)"
description: "CQRS (Command Query Responsibility Segregation) — это архитектурный паттерн, который разделяет операции чтения (queries) и записи (commands) данных на отдельные модели. Это позволяет оптимизировать каждую сторону независимо и создавать более масштабируемые системы."
tags:
  - architecture
  - cqrs
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# CQRS (Command Query Responsibility Segregation)

**CQRS** (Command Query Responsibility Segregation) — это архитектурный паттерн, который разделяет операции чтения (queries) и записи (commands) данных на отдельные модели. Это позволяет оптимизировать каждую сторону независимо и создавать более масштабируемые системы.

## Полезные ссылки

### Официальная документация
- [CQRS (Martin Fowler)](https://martinfowler.com/bliki/CQRS.html)
- [Spring Data Projections](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)
- [Axon Framework](https://docs.axondb.io/)

### См. также
- [[event-driven|Event-Driven Architecture]] — **Event-Driven Architecture**
- [[event-sourcing|Event Sourcing]] — **Event Sourcing**
- [[ddd|Domain-Driven Design]] — **Domain-Driven Design**
- [[architecture-patterns|Архитектурные паттерны]] — архитектурные паттерны

## Содержание

- [Введение в CQRS](#введение-в-cqrs)
  - [Преимущества CQRS](#преимущества-cqrs)
  - [Недостатки CQRS](#недостатки-cqrs)
- [Принципы CQRS](#принципы-cqrs)
  - [Разделение ответственности](#разделение-ответственности)
  - [Разделение моделей](#разделение-моделей)
- [Command Side (Write Model)](#command-side-write-model)
  - [Command Handlers](#command-handlers)
  - [Command Bus](#command-bus)
- [Query Side (Read Model)](#query-side-read-model)
  - [Query Handlers](#query-handlers)
  - [Query Bus](#query-bus)
- [Синхронизация между моделями](#синхронизация-между-моделями)
  - [Event-Driven синхронизация](#event-driven-синхронизация)
  - [Прямая синхронизация](#прямая-синхронизация)
- [Eventual Consistency](#eventual-consistency)
  - [Стратегии работы с Eventual Consistency](#стратегии-работы-с-eventual-consistency)
- [Materialized Views](#materialized-views)
  - [Создание Materialized Views](#создание-materialized-views)
  - [Использование Materialized Views в запросах](#использование-materialized-views-в-запросах)
- [Реализация на Spring](#реализация-на-spring)
  - [Использование Spring Data Projections](#использование-spring-data-projections)
  - [Использование отдельной базы данных для Read модели](#использование-отдельной-базы-данных-для-read-модели)
- [Проекции и DTO](#проекции-и-dto)
  - [Создание DTO для Query модели](#создание-dto-для-query-модели)
  - [Маппинг с использованием MapStruct](#маппинг-с-использованием-mapstruct)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Кэширование Read модели](#кэширование-read-модели)
  - [Индексы для оптимизации запросов](#индексы-для-оптимизации-запросов)
  - [Batch обработка обновлений](#batch-обработка-обновлений)
- [Лучшие практики](#лучшие-практики)
  - [1. Четкое разделение Command и Query](#1-четкое-разделение-command-и-query)
  - [2. Использование событий для синхронизации](#2-использование-событий-для-синхронизации)
  - [3. Идемпотентность обработчиков событий](#3-идемпотентность-обработчиков-событий)
  - [4. Оптимизация Read модели для конкретных запросов](#4-оптимизация-read-модели-для-конкретных-запросов)
- [Когда использовать CQRS](#когда-использовать-cqrs)
  - [Используйте CQRS когда:](#используйте-cqrs-когда)
  - [Не используйте CQRS когда:](#не-используйте-cqrs-когда)
- [Решение проблем](#решение-проблем)
  - [Проблема: Read модель не обновляется](#проблема-read-модель-не-обновляется)
  - [Проблема: Задержка обновления Read модели](#проблема-задержка-обновления-read-модели)
  - [Проблема: Несогласованность данных](#проблема-несогласованность-данных)
- [Частые вопросы](#частые-вопросы)

## Введение в CQRS

**CQRS** разделяет модель данных на две части:
- **Command Model** (Write Model) — оптимизирована для записи данных
- **Query Model** (Read Model) — оптимизирована для чтения данных

**Это разделение позволяет:**
- Оптимизировать каждую модель независимо
- Масштабировать чтение и запись отдельно
- Упростить сложные доменные модели
- Улучшить производительность запросов

### Преимущества CQRS

**Независимая оптимизация**
**Write** модель может быть нормализована для целостности данных, а **Read** модель — денормализована для быстрых запросов.

**Масштабируемость**
**Read** и **Write** модели могут масштабироваться независимо в зависимости от нагрузки.

**Упрощение сложных доменов**
Разделение позволяет упростить сложную бизнес-логику на стороне записи.

**Гибкость**
**Read** модель может быть оптимизирована для различных типов запросов (отчеты, аналитика, поиск).

### Недостатки CQRS

**Сложность**
Увеличивает сложность системы за счет необходимости синхронизации двух моделей.

**Eventual Consistency**
**Read** модель может быть неактуальной в течение некоторого времени.

**Дублирование кода**
Необходимо поддерживать две модели данных.

**Не всегда необходимо**
Для простых **CRUD** приложений **CQRS** может быть избыточным.

## Принципы CQRS

### Разделение ответственности

**Commands (Команды)**
- Изменяют состояние системы
- Возвращают минимальную информацию (ID созданного объекта или статус)
- Могут вызывать побочные эффекты
- Примеры: **CreateUser**, **UpdateOrder**, **CancelPayment**

**Queries (Запросы)**
- Не изменяют состояние системы
- Возвращают данные для чтения
- Должны быть идемпотентными
- Примеры: **GetUserById**, **GetUserOrders**, **SearchUsers**

### Разделение моделей

Ниже — пример разделения **Command**/**Query** моделей (Java).
```java
// Command Model - Write Side
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String passwordHash;
    private String name;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    // Бизнес-логика для изменения состояния
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("User already deactivated");
        }
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void changeEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
        this.updatedAt = Instant.now();
    }
}

// Query Model - Read Side
public class UserView {
    private String id;
    private String email;
    private String name;
    private boolean active;
    private Instant createdAt;
    private List<OrderSummary> recentOrders;
    private int totalOrders;

    // Оптимизирована для чтения, может быть денормализована
}
```

## Command Side (Write Model)

### Command Handlers

**Command Handler** обрабатывает команды и изменяет состояние системы.

```java
public interface Command {
    String getCommandId();
    Instant getTimestamp();
}

public class CreateUserCommand implements Command {
    private final String commandId;
    private final Instant timestamp;
    private final String email;
    private final String password;
    private final String name;

    public CreateUserCommand(String email, String password, String name) {
        this.commandId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Getters
}

public class UpdateUserCommand implements Command {
    private final String commandId;
    private final String userId;
    private final String email;
    private final String name;

    // Constructor and getters
}

@Component
public class UserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public UserCommandHandler(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String handle(CreateUserCommand command) {
        // Валидация
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new UserAlreadyExistsException(command.getEmail());
        }

        // Создание агрегата
        User user = new User(
            UUID.randomUUID().toString(),
            command.getEmail(),
            passwordEncoder.encode(command.getPassword()),
            command.getName()
        );

        // Сохранение
        userRepository.save(user);

        // Публикация события для синхронизации Read модели
        eventPublisher.publish(new UserCreatedEvent(
            user.getId(),
            user.getEmail(),
            user.getName()
        ));

        return user.getId();
    }

    @Transactional
    public void handle(UpdateUserCommand command) {
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        if (command.getEmail() != null) {
            user.changeEmail(command.getEmail());
        }

        if (command.getName() != null) {
            user.changeName(command.getName());
        }

        userRepository.save(user);

        eventPublisher.publish(new UserUpdatedEvent(
            user.getId(),
            user.getEmail(),
            user.getName()
        ));
    }

    @Transactional
    public void handle(DeactivateUserCommand command) {
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        user.deactivate();
        userRepository.save(user);

        eventPublisher.publish(new UserDeactivatedEvent(user.getId()));
    }
}
```

### Command Bus

**Command Bus** обеспечивает централизованную обработку команд.

```java
public interface CommandBus {
    <T extends Command> void send(T command);
    <T extends Command, R> R sendAndWait(T command, Class<R> resultType);
}

@Component
public class SimpleCommandBus implements CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<?>> handlers = new HashMap<>();
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Map<String, CommandHandler> handlerBeans =
            applicationContext.getBeansOfType(CommandHandler.class);

        for (CommandHandler handler : handlerBeans.values()) {
            Class<? extends Command> commandType = handler.getCommandType();
            handlers.put(commandType, handler);
        }
    }

    @Override
    public <T extends Command> void send(T command) {
        CommandHandler<T> handler = findHandler(command);
        handler.handle(command);
    }

    @Override
    public <T extends Command, R> R sendAndWait(T command, Class<R> resultType) {
        CommandHandler<T> handler = findHandler(command);
        return (R) handler.handle(command);
    }

    @SuppressWarnings("unchecked")
    private <T extends Command> CommandHandler<T> findHandler(T command) {
        CommandHandler<?> handler = handlers.get(command.getClass());
        if (handler == null) {
            throw new NoHandlerFoundException("No handler for command: " + command.getClass());
        }
        return (CommandHandler<T>) handler;
    }
}

// Использование
@RestController
@RequestMapping("/api/users")
public class UserCommandController {

    private final CommandBus commandBus;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = new CreateUserCommand(
            request.getEmail(),
            request.getPassword(),
            request.getName()
        );

        String userId = commandBus.sendAndWait(command, String.class);
        return ResponseEntity.ok(userId);
    }
}
```

## Query Side (Read Model)

### Query Handlers

**Query Handler** обрабатывает запросы и возвращает данные из **Read** модели.

```java
public interface Query {
    String getQueryId();
    Instant getTimestamp();
}

public class GetUserByIdQuery implements Query {
    private final String queryId;
    private final Instant timestamp;
    private final String userId;

    public GetUserByIdQuery(String userId) {
        this.queryId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.userId = userId;
    }

    // Getters
}

public class SearchUsersQuery implements Query {
    private final String queryId;
    private final String emailFilter;
    private final String nameFilter;
    private final boolean activeOnly;
    private final int page;
    private final int size;

    // Constructor and getters
}

@Component
public class UserQueryHandler {

    private final UserViewRepository userViewRepository;

    public UserQueryHandler(UserViewRepository userViewRepository) {
        this.userViewRepository = userViewRepository;
    }

    public UserView handle(GetUserByIdQuery query) {
        return userViewRepository.findById(query.getUserId())
            .orElseThrow(() -> new UserNotFoundException(query.getUserId()));
    }

    public Page<UserView> handle(SearchUsersQuery query) {
        Specification<UserView> spec = Specification.where(null);

        if (query.getEmailFilter() != null) {
            spec = spec.and((root, criteriaQuery, cb) ->
                cb.like(cb.lower(root.get("email")),
                    "%" + query.getEmailFilter().toLowerCase() + "%"));
        }

        if (query.getNameFilter() != null) {
            spec = spec.and((root, criteriaQuery, cb) ->
                cb.like(cb.lower(root.get("name")),
                    "%" + query.getNameFilter().toLowerCase() + "%"));
        }

        if (query.isActiveOnly()) {
            spec = spec.and((root, criteriaQuery, cb) ->
                cb.equal(root.get("active"), true));
        }

        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return userViewRepository.findAll(spec, pageable);
    }

    public List<UserOrderView> handle(GetUserOrdersQuery query) {
        return userViewRepository.findUserOrders(query.getUserId());
    }
}
```

### Query Bus

```java
public interface QueryBus {
    <T> T send(Query query, Class<T> resultType);
}

@Component
public class SimpleQueryBus implements QueryBus {

    private final Map<Class<? extends Query>, QueryHandler<?, ?>> handlers = new HashMap<>();
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Map<String, QueryHandler> handlerBeans =
            applicationContext.getBeansOfType(QueryHandler.class);

        for (QueryHandler handler : handlerBeans.values()) {
            Class<? extends Query> queryType = handler.getQueryType();
            handlers.put(queryType, handler);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T send(Query query, Class<T> resultType) {
        QueryHandler<Query, T> handler = findHandler(query);
        return handler.handle(query);
    }

    @SuppressWarnings("unchecked")
    private <Q extends Query, R> QueryHandler<Q, R> findHandler(Query query) {
        QueryHandler<?, ?> handler = handlers.get(query.getClass());
        if (handler == null) {
            throw new NoHandlerFoundException("No handler for query: " + query.getClass());
        }
        return (QueryHandler<Q, R>) handler;
    }
}

// Использование
@RestController
@RequestMapping("/api/users")
public class UserQueryController {

    private final QueryBus queryBus;

    @GetMapping("/{id}")
    public ResponseEntity<UserView> getUser(@PathVariable String id) {
        GetUserByIdQuery query = new GetUserByIdQuery(id);
        UserView user = queryBus.send(query, UserView.class);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserView>> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchUsersQuery query = new SearchUsersQuery(email, name, activeOnly, page, size);
        Page<UserView> users = queryBus.send(query, Page.class);
        return ResponseEntity.ok(users);
    }
}
```

## Синхронизация между моделями

### Event-Driven синхронизация

**Read** модель обновляется на основе событий, публикуемых **Command** моделью.

```java
@Component
public class UserViewUpdater {

    private final UserViewRepository userViewRepository;
    private final OrderViewRepository orderViewRepository;

    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserCreated(UserCreatedEvent event) {
        UserView view = new UserView();
        view.setId(event.getUserId());
        view.setEmail(event.getEmail());
        view.setName(event.getName());
        view.setActive(true);
        view.setCreatedAt(event.getOccurredOn());
        view.setTotalOrders(0);

        userViewRepository.save(view);
    }

    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserUpdated(UserUpdatedEvent event) {
        UserView view = userViewRepository.findById(event.getUserId())
            .orElseThrow(() -> new UserNotFoundException(event.getUserId()));

        view.setEmail(event.getEmail());
        view.setName(event.getName());
        view.setUpdatedAt(event.getOccurredOn());

        userViewRepository.save(view);
    }

    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserDeactivated(UserDeactivatedEvent event) {
        UserView view = userViewRepository.findById(event.getUserId())
            .orElseThrow(() -> new UserNotFoundException(event.getUserId()));

        view.setActive(false);
        view.setUpdatedAt(event.getOccurredOn());

        userViewRepository.save(view);
    }

    @KafkaListener(topics = "order-events", groupId = "user-view-updater")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Обновление статистики заказов пользователя
        UserView view = userViewRepository.findById(event.getCustomerId())
            .orElse(null);

        if (view != null) {
            view.setTotalOrders(view.getTotalOrders() + 1);
            userViewRepository.save(view);
        }
    }
}
```

### Прямая синхронизация

Для простых случаев можно обновлять **Read** модель напрямую из **Command Handler**.

```java
@Component
public class UserCommandHandler {

    private final UserRepository userRepository;
    private final UserViewRepository userViewRepository;

    @Transactional
    public String handle(CreateUserCommand command) {
        // Сохранение в Write модель
        User user = new User(/* ... */);
        userRepository.save(user);

        // Обновление Read модели в той же транзакции
        UserView view = new UserView();
        view.setId(user.getId());
        view.setEmail(user.getEmail());
        view.setName(user.getName());
        view.setActive(true);
        userViewRepository.save(view);

        return user.getId();
    }
}
```

## Eventual Consistency

**Eventual Consistency** означает, что **Read** модель может быть неактуальной в течение некоторого времени после изменения в **Write** модели.

### Стратегии работы с Eventual Consistency

**1. `Accept` и информировать пользователя**

```java
@RestController
public class UserController {

    @PostMapping("/users")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        String userId = commandBus.sendAndWait(new CreateUserCommand(/* ... */), String.class);

        // Возвращаем ID, но предупреждаем о задержке
        return ResponseEntity.accepted()
            .header("X-Read-Model-Delay", "may-be-delayed")
            .body(new CreateUserResponse(userId, "User created. Read model will be updated shortly."));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserView> getUser(@PathVariable String id) {
        UserView user = queryBus.send(new GetUserByIdQuery(id), UserView.class);

        if (user == null) {
            // Возможно, Read модель еще не обновлена
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header("Retry-After", "1")
                .build();
        }

        return ResponseEntity.ok(user);
    }
}
```

**2. Polling для проверки обновления**

```java
@RestController
public class UserController {

    @PostMapping("/users")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        String userId = commandBus.sendAndWait(new CreateUserCommand(/* ... */), String.class);

        return ResponseEntity.accepted()
            .header("Location", "/api/users/" + userId + "/status")
            .body(new CreateUserResponse(userId));
    }

    @GetMapping("/users/{id}/status")
    public ResponseEntity<UserStatus> getUserStatus(@PathVariable String id) {
        UserView user = queryBus.send(new GetUserByIdQuery(id), UserView.class);

        if (user != null) {
            return ResponseEntity.ok(new UserStatus(id, "READY", user));
        }

        return ResponseEntity.ok(new UserStatus(id, "PENDING", null));
    }
}
```

**3. WebSocket для real-time обновлений**

```java
@Component
public class UserViewUpdater {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserCreated(UserCreatedEvent event) {
        // Обновление Read модели
        UserView view = updateUserView(event);

        // Отправка обновления через WebSocket
        messagingTemplate.convertAndSend("/topic/users/" + event.getUserId(), view);
    }
}
```

## Materialized Views

**Materialized Views** — это предварительно вычисленные представления данных, оптимизированные для чтения.

### Создание Materialized Views

```java
@Entity
@Table(name = "user_order_summary")
public class UserOrderSummaryView {
    @Id
    private String userId;
    private String email;
    private String name;
    private int totalOrders;
    private BigDecimal totalAmount;
    private Instant lastOrderDate;
    private List<OrderSummary> recentOrders; // Денормализованные данные

    // Getters and setters
}

@Component
public class UserOrderSummaryUpdater {

    private final UserOrderSummaryRepository summaryRepository;

    @KafkaListener(topics = "order-events", groupId = "order-summary-updater")
    public void handleOrderCreated(OrderCreatedEvent event) {
        UserOrderSummaryView summary = summaryRepository.findById(event.getCustomerId())
            .orElseGet(() -> {
                UserOrderSummaryView newSummary = new UserOrderSummaryView();
                newSummary.setUserId(event.getCustomerId());
                return newSummary;
            });

        summary.setTotalOrders(summary.getTotalOrders() + 1);
        summary.setTotalAmount(summary.getTotalAmount().add(event.getAmount()));
        summary.setLastOrderDate(event.getOccurredOn());

        // Добавление в список недавних заказов
        OrderSummary orderSummary = new OrderSummary(
            event.getOrderId(),
            event.getAmount(),
            event.getOccurredOn()
        );
        summary.getRecentOrders().add(0, orderSummary);

        // Ограничение размера списка
        if (summary.getRecentOrders().size() > 10) {
            summary.getRecentOrders().remove(summary.getRecentOrders().size() - 1);
        }

        summaryRepository.save(summary);
    }
}
```

### Использование Materialized Views в запросах

```java
@Component
public class UserQueryHandler {

    private final UserOrderSummaryRepository summaryRepository;

    public UserOrderSummaryView handle(GetUserOrderSummaryQuery query) {
        return summaryRepository.findById(query.getUserId())
            .orElseThrow(() -> new UserNotFoundException(query.getUserId()));
    }

    public List<UserOrderSummaryView> handle(GetTopUsersByOrdersQuery query) {
        return summaryRepository.findTopByTotalOrdersOrderByTotalOrdersDesc(query.getLimit());
    }
}
```

## Реализация на Spring

### Использование Spring Data Projections

**Spring Data Projections** позволяют создавать оптимизированные представления данных для чтения.

```java
// Write Model
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String passwordHash;
    private String name;
    private boolean active;
    // ... другие поля
}

// Projection для чтения
public interface UserViewProjection {
    String getId();
    String getEmail();
    String getName();
    boolean isActive();
}

// Repository с проекцией
public interface UserRepository extends JpaRepository<User, String> {
    UserViewProjection findProjectionById(String id);
    List<UserViewProjection> findAllProjectionsByActiveTrue();
}

// DTO для сложных запросов
public class UserDetailView {
    private String id;
    private String email;
    private String name;
    private List<OrderSummary> orders;
    private int totalOrders;

    public UserDetailView(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.orders = new ArrayList<>();
    }

    // Getters and setters
}

// Query с проекцией
@Repository
public class UserQueryRepository {

    private final EntityManager entityManager;

    public UserDetailView findUserDetailById(String userId) {
        String jpql = "SELECT new UserDetailView(u.id, u.email, u.name) " +
                     "FROM User u WHERE u.id = :userId";

        UserDetailView view = entityManager.createQuery(jpql, UserDetailView.class)
            .setParameter("userId", userId)
            .getSingleResult();

        // Загрузка связанных данных
        String ordersJpql = "SELECT new OrderSummary(o.id, o.amount, o.createdAt) " +
                            "FROM Order o WHERE o.customerId = :userId ORDER BY o.createdAt DESC";
        List<OrderSummary> orders = entityManager.createQuery(ordersJpql, OrderSummary.class)
            .setParameter("userId", userId)
            .setMaxResults(10)
            .getResultList();

        view.setOrders(orders);
        view.setTotalOrders(orders.size());

        return view;
    }
}
```

### Использование отдельной базы данных для Read модели

```java
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(writeDataSource());
        em.setPackagesToScan("com.example.write");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean readEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(readDataSource());
        em.setPackagesToScan("com.example.read");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager writeTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(writeEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager readTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(readEntityManagerFactory().getObject());
        return transactionManager;
    }
}
```

## Проекции и DTO

### Создание DTO для Query модели

```java
public class UserViewDTO {
    private String id;
    private String email;
    private String name;
    private boolean active;
    private Instant createdAt;
    private UserStatistics statistics;

    public static UserViewDTO from(UserView view) {
        UserViewDTO dto = new UserViewDTO();
        dto.setId(view.getId());
        dto.setEmail(view.getEmail());
        dto.setName(view.getName());
        dto.setActive(view.isActive());
        dto.setCreatedAt(view.getCreatedAt());
        return dto;
    }

    // Getters and setters
}

public class UserStatistics {
    private int totalOrders;
    private BigDecimal totalSpent;
    private Instant lastOrderDate;

    // Getters and setters
}
```

### Маппинг с использованием MapStruct

```java
@Mapper(componentModel = "spring")
public interface UserViewMapper {
    UserViewDTO toDTO(UserView view);
    List<UserViewDTO> toDTOList(List<UserView> views);
    Page<UserViewDTO> toDTOPage(Page<UserView> page);
}

@Component
public class UserQueryHandler {

    private final UserViewRepository userViewRepository;
    private final UserViewMapper mapper;

    public UserViewDTO handle(GetUserByIdQuery query) {
        UserView view = userViewRepository.findById(query.getUserId())
            .orElseThrow(() -> new UserNotFoundException(query.getUserId()));
        return mapper.toDTO(view);
    }
}
```

## Оптимизация производительности

### Кэширование Read модели

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration());

        return builder.build();
    }

    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}

@Component
public class UserQueryHandler {

    @Cacheable(value = "users", key = "#query.userId")
    public UserView handle(GetUserByIdQuery query) {
        return userViewRepository.findById(query.getUserId())
            .orElseThrow(() -> new UserNotFoundException(query.getUserId()));
    }

    @CacheEvict(value = "users", key = "#event.userId")
    public void handleUserUpdated(UserUpdatedEvent event) {
        // Обновление Read модели
        updateUserView(event);
    }
}
```

### Индексы для оптимизации запросов

```java
@Entity
@Table(name = "user_views", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_active", columnList = "active"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class UserView {
    // ...
}
```

### Batch обработка обновлений

```java
@Component
public class BatchUserViewUpdater {

    private final List<UserCreatedEvent> pendingEvents = new ArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(this::processBatch, 5, 5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "user-events", groupId = "batch-view-updater")
    public void handleUserCreated(UserCreatedEvent event) {
        synchronized (pendingEvents) {
            pendingEvents.add(event);
        }
    }

    private void processBatch() {
        List<UserCreatedEvent> events;
        synchronized (pendingEvents) {
            events = new ArrayList<>(pendingEvents);
            pendingEvents.clear();
        }

        if (events.isEmpty()) {
            return;
        }

        // Batch обновление Read модели
        List<UserView> views = events.stream()
            .map(this::createUserView)
            .collect(Collectors.toList());

        userViewRepository.saveAll(views);
    }
}
```

## Лучшие практики

### 1. Четкое разделение Command и Query

Не смешивайте операции чтения и записи в одном обработчике.

```java
// Плохо: смешивание Command и Query
@Component
public class UserService {
    public User createUser(CreateUserRequest request) {
        User user = new User(/* ... */);
        userRepository.save(user);
        return user; // Возврат полного объекта из Write модели
    }
}

// Хорошо: разделение
@Component
public class UserCommandHandler {
    public String handle(CreateUserCommand command) {
        // Только создание, возврат ID
        return userId;
    }
}

@Component
public class UserQueryHandler {
    public UserView handle(GetUserByIdQuery query) {
        // Только чтение из Read модели
        return userView;
    }
}
```

### 2. Использование событий для синхронизации

Всегда используйте события для синхронизации **Read** модели с **Write** моделью.

### 3. Идемпотентность обработчиков событий

Обработчики событий должны быть идемпотентными для безопасной повторной обработки.

```java
@Component
public class IdempotentUserViewUpdater {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "user-events")
    public void handleUserCreated(UserCreatedEvent event) {
        if (processedEventIds.contains(event.getEventId())) {
            return; // Уже обработано
        }

        updateUserView(event);
        processedEventIds.add(event.getEventId());
    }
}
```

### 4. Оптимизация Read модели для конкретных запросов

Создавайте специализированные представления для различных типов запросов.

```java
// Для поиска
@Entity
@Table(name = "user_search_views")
public class UserSearchView {
    @Id
    private String id;
    private String email;
    private String name;
    @Column(name = "search_text")
    private String searchText; // Денормализованное поле для поиска
}

// Для отчетов
@Entity
@Table(name = "user_report_views")
public class UserReportView {
    @Id
    private String id;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private Instant lastOrderDate;
}
```

## Когда использовать CQRS

### Используйте CQRS когда:

**CQRS** оправдан в следующих случаях:

- **Высокая нагрузка на чтение или запись** — необходимо масштабировать их независимо
- **Сложные запросы** — **Read** модель может быть оптимизирована для сложных запросов
- **Разные требования к данным** — **Write** модель нормализована, **Read** модель денормализована
- **Команда разработчиков** — разные команды могут работать над **Command** и **Query** сторонами
- **Интеграция с `Event` Sourcing** — **CQRS** естественно сочетается с **Event Sourcing**

### Не используйте CQRS когда:

От **CQRS** лучше отказаться, если:

- **Простое `CRUD` приложение** — **CQRS** добавляет сложность без выгоды
- **Нет проблем с производительностью** — преждевременная оптимизация
- **Маленькая команда** — сложность может быть избыточной
- **Строгие требования к consistency** — **Eventual Consistency** может быть неприемлема

## Решение проблем

### Проблема: Read модель не обновляется

**Причины:**
- События не публикуются
- Обработчик событий не работает
- Ошибки в обработчике событий

**Решение:**
```java
@Component
public class UserViewUpdater {

    private static final Logger log = LoggerFactory.getLogger(UserViewUpdater.class);

    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            log.info("Processing event: {}", event.getEventId());
            updateUserView(event);
            log.info("Successfully processed event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing event: {}", event.getEventId(), e);
            // Отправка в Dead Letter Queue
            sendToDLQ(event, e);
        }
    }
}
```

### Проблема: Задержка обновления Read модели

**Причины:**
- Медленная обработка событий
- Большой объем событий
- Неоптимальная конфигурация **consumer**

**Решение:**
- Увеличьте количество **consumer instances**
- Используйте **batch** обработку
- Оптимизируйте запросы к базе данных
- Используйте кэширование

### Проблема: Несогласованность данных

**Причины:**
- События обрабатываются не в порядке
- Потеря событий
- Ошибки в обработчиках

**Решение:**
- Используйте партиционирование по ключу для обеспечения порядка
- Реализуйте идемпотентность
- Добавьте мониторинг и алерты
- Используйте **Dead Letter Queue** для проблемных событий

## Частые вопросы

**Когда действительно нужен CQRS?** Когда нагрузка на чтение и запись сильно различается, нужны разные модели данных для запросов и команд или вы вводите Event Sourcing. Для простого CRUD CQRS добавляет сложность без выгоды.

**Чем CQRS отличается от Event Sourcing?** CQRS разделяет модели чтения и записи; Event Sourcing хранит историю событий. Их часто используют вместе: команды порождают события, read-модель строится из событий.

**Как жить с eventual consistency?** Принимать задержку обновления read-модели; показывать пользователю «обновлено» после записи; использовать идемпотентность и мониторинг лага репликации.
