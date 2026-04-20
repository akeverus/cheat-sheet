---
title: "Event Sourcing"
description: "Event Sourcing — это архитектурный паттерн, при котором состояние приложения определяется последовательностью событий, а не текущим состоянием. Вместо хранения текущего состояния объекта, система хранит все события, которые привели к этому состоянию, и состояние восстанавливается"
tags:
  - architecture
  - event-sourcing
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Event Sourcing

**Event Sourcing** — это архитектурный паттерн, при котором состояние приложения определяется последовательностью событий, а не текущим состоянием. Вместо хранения текущего состояния объекта, система хранит все события, которые привели к этому состоянию, и состояние восстанавливается путем воспроизведения (**replay**) этих событий.

## Полезные ссылки

### Официальная документация
- [Event Sourcing (Martin Fowler)](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Axon Framework](https://docs.axondb.io/)
- [EventStore](https://www.eventstore.com/docs)

### См. также
- [[event-driven|Event-Driven Architecture]] — **Event-Driven Architecture**
- [[cqrs|CQRS]] — **CQRS** паттерн
- [[ddd|Domain-Driven Design]] — **Domain-Driven Design**
- [[architecture-patterns|Архитектурные паттерны]] — архитектурные паттерны

## Содержание

- [Введение в **Event Sourcing**](#введение-в-event-sourcing)
  - [Преимущества **Event Sourcing**](#преимущества-event-sourcing)
  - [Недостатки **Event Sourcing**](#недостатки-event-sourcing)
- [Основные концепции](#основные-концепции)
  - [Событие (**Event**)](#событие-event)
  - [**Aggregate** (**Агрегат**)](#aggregate-агрегат)
- [**Event Store**](#event-store)
  - [Простая реализация **Event Store**](#простая-реализация-event-store)
  - [Использование **Kafka** как **Event Store**](#использование-kafka-как-event-store)
- [**Aggregates** и **Domain Events**](#aggregates-и-domain-events)
  - [**Repository** для **Aggregate**](#repository-для-aggregate)
- [**Event Replay** и **Snapshots**](#event-replay-и-snapshots)
  - [**Event Replay**](#event-replay)
  - [**Snapshots**](#snapshots)
- [Версионирование событий](#версионирование-событий)
- [Миграция событий](#миграция-событий)
  - [**Upcaster** для миграции событий](#upcaster-для-миграции-событий)
- [Интеграция с **CQRS**](#интеграция-с-cqrs)
- [Реализация на **Spring**](#реализация-на-spring)
  - [Конфигурация](#конфигурация)
  - [**Command Handler**](#command-handler)
- [Реализация с **Axon Framework**](#реализация-с-axon-framework)
  - [Зависимости](#зависимости)
  - [**Aggregate** с **Axon**](#aggregate-с-axon)
  - [**Command** и **Query Handlers**](#command-и-query-handlers)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Кэширование агрегатов](#кэширование-агрегатов)
  - [**Batch** обработка событий](#batch-обработка-событий)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте **Snapshots** для больших агрегатов](#1-используйте-snapshots-для-больших-агрегатов)
  - [2. Версионируйте события](#2-версионируйте-события)
  - [3. Храните метаданные событий](#3-храните-метаданные-событий)
  - [4. Используйте оптимистичную блокировку](#4-используйте-оптимистичную-блокировку)
  - [5. Разделяйте события по доменам](#5-разделяйте-события-по-доменам)
- [Решение проблем](#решение-проблем)
  - [Проблема: Медленное восстановление состояния](#проблема-медленное-восстановление-состояния)
  - [Проблема: Конфликты версий](#проблема-конфликты-версий)
  - [Проблема: Большой размер **Event Store**](#проблема-большой-размер-event-store)
- [Частые вопросы](#частые-вопросы)

## Введение в **Event Sourcing**

**Event Sourcing** хранит все изменения состояния приложения как последовательность событий. Каждое событие представляет собой неизменяемый факт о том, что произошло в системе.

### Преимущества **Event Sourcing**

**Полная история изменений**
Все изменения сохраняются, что позволяет восстановить состояние на любой момент времени.

**Аудит и compliance**
Полная история изменений обеспечивает аудит и соответствие требованиям.

**Отладка**
Можно воспроизвести последовательность событий для отладки проблем.

**Гибкость**
Легко добавлять новые проекции данных без изменения существующего кода.

**Временные запросы**
Можно запросить состояние системы на любой момент времени в прошлом.

### Недостатки **Event Sourcing**

**Сложность**
Увеличивает сложность системы за счет необходимости работы с событиями.

**Производительность**
Восстановление состояния из большого количества событий может быть медленным.

**Хранение**
Требуется больше места для хранения всех событий.

**Сложность запросов**
Запросы текущего состояния требуют обработки всех событий или использования **snapshots**.

## Основные концепции

### Событие (**Event**)

Событие представляет собой неизменяемый факт о том, что произошло в системе.

Ниже — пример определения события в **Event Sourcing** (**Java**).
```java
public abstract class DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final Instant occurredOn;
    private final String eventType;
    private final int version;
    
    protected DomainEvent(String aggregateId, String eventType, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredOn = Instant.now();
        this.eventType = eventType;
        this.version = version;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getAggregateId() {
        return aggregateId;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public int getVersion() {
        return version;
    }
}

public class UserCreatedEvent extends DomainEvent {
    private final String email;
    private final String name;
    
    public UserCreatedEvent(String aggregateId, String email, String name) {
        super(aggregateId, "UserCreated", 1);
        this.email = email;
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }
}

public class UserEmailChangedEvent extends DomainEvent {
    private final String oldEmail;
    private final String newEmail;
    
    public UserEmailChangedEvent(String aggregateId, String oldEmail, String newEmail) {
        super(aggregateId, "UserEmailChanged", 1);
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
    }
    
    // Getters
}
```

### **Aggregate** (**Агрегат**)

**Aggregate** — это кластер связанных объектов, которые рассматриваются как единое целое. Состояние **Aggregate** определяется событиями.

```java
public class UserAggregate {
    private String userId;
    private String email;
    private String name;
    private boolean active;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    private long version;
    
    // Приватный конструктор для восстановления из событий
    private UserAggregate() {
    }
    
    // Фабричный метод для создания нового агрегата
    public static UserAggregate create(String email, String name) {
        UserAggregate user = new UserAggregate();
        user.userId = UUID.randomUUID().toString();
        user.apply(new UserCreatedEvent(user.userId, email, name));
        return user;
    }
    
    // Восстановление агрегата из истории событий
    public static UserAggregate fromHistory(List<DomainEvent> events) {
        UserAggregate user = new UserAggregate();
        events.forEach(user::apply);
        user.uncommittedEvents.clear();
        return user;
    }
    
    public void changeEmail(String newEmail) {
        if (!active) {
            throw new IllegalStateException("Cannot change email for inactive user");
        }
        
        String oldEmail = this.email;
        apply(new UserEmailChangedEvent(userId, oldEmail, newEmail));
    }
    
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("User already deactivated");
        }
        apply(new UserDeactivatedEvent(userId));
    }
    
    // Применение события к агрегату
    private void apply(DomainEvent event) {
        // Обновление состояния на основе события
        if (event instanceof UserCreatedEvent) {
            UserCreatedEvent e = (UserCreatedEvent) event;
            this.email = e.getEmail();
            this.name = e.getName();
            this.active = true;
        } else if (event instanceof UserEmailChangedEvent) {
            UserEmailChangedEvent e = (UserEmailChangedEvent) event;
            this.email = e.getNewEmail();
        } else if (event instanceof UserDeactivatedEvent) {
            this.active = false;
        }
        
        this.version++;
        uncommittedEvents.add(event);
    }
    
    // Применение события при восстановлении (без добавления в uncommittedEvents)
    public void applyHistorical(DomainEvent event) {
        if (event instanceof UserCreatedEvent) {
            UserCreatedEvent e = (UserCreatedEvent) event;
            this.userId = e.getAggregateId();
            this.email = e.getEmail();
            this.name = e.getName();
            this.active = true;
        } else if (event instanceof UserEmailChangedEvent) {
            UserEmailChangedEvent e = (UserEmailChangedEvent) event;
            this.email = e.getNewEmail();
        } else if (event instanceof UserDeactivatedEvent) {
            this.active = false;
        }
        
        this.version++;
    }
    
    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }
    
    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public long getVersion() {
        return version;
    }
}
```

## **Event Store**

**Event Store** — это специализированное хранилище для событий. Оно обеспечивает:**
- Сохранение событий
- Загрузку событий по **aggregate** `ID`
- Версионирование для оптимистичной блокировки
- Поддержку **snapshots**

### Простая реализация **Event Store**

```java
public interface EventStore {
    void saveEvents(String aggregateId, List<DomainEvent> events, long expectedVersion);
    List<DomainEvent> getEvents(String aggregateId);
    List<DomainEvent> getEvents(String aggregateId, long fromVersion);
    void saveSnapshot(String aggregateId, Snapshot snapshot);
    Snapshot getSnapshot(String aggregateId);
}

@Component
public class JpaEventStore implements EventStore {
    
    private final EventRepository eventRepository;
    private final SnapshotRepository snapshotRepository;
    
    @Override
    @Transactional
    public void saveEvents(String aggregateId, List<DomainEvent> events, long expectedVersion) {
        // Проверка версии для оптимистичной блокировки
        long currentVersion = eventRepository.getMaxVersion(aggregateId);
        if (currentVersion != expectedVersion) {
            throw new ConcurrencyException(
                "Expected version " + expectedVersion + " but was " + currentVersion
            );
        }
        
        // Сохранение событий
        List<EventEntity> entities = events.stream()
            .map(event -> new EventEntity(
                event.getEventId(),
                aggregateId,
                event.getEventType(),
                serializeEvent(event),
                expectedVersion + events.indexOf(event) + 1,
                event.getOccurredOn()
            ))
            .collect(Collectors.toList());
        
        eventRepository.saveAll(entities);
    }
    
    @Override
    public List<DomainEvent> getEvents(String aggregateId) {
        List<EventEntity> entities = eventRepository.findByAggregateIdOrderByVersion(aggregateId);
        return entities.stream()
            .map(this::deserializeEvent)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DomainEvent> getEvents(String aggregateId, long fromVersion) {
        List<EventEntity> entities = eventRepository
            .findByAggregateIdAndVersionGreaterThanOrderByVersion(aggregateId, fromVersion);
        return entities.stream()
            .map(this::deserializeEvent)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void saveSnapshot(String aggregateId, Snapshot snapshot) {
        SnapshotEntity entity = new SnapshotEntity(
            aggregateId,
            snapshot.getVersion(),
            serializeSnapshot(snapshot),
            Instant.now()
        );
        snapshotRepository.save(entity);
    }
    
    @Override
    public Snapshot getSnapshot(String aggregateId) {
        SnapshotEntity entity = snapshotRepository
            .findTopByAggregateIdOrderByVersionDesc(aggregateId)
            .orElse(null);
        
        if (entity == null) {
            return null;
        }
        
        return deserializeSnapshot(entity.getData());
    }
    
    private String serializeEvent(DomainEvent event) {
        // Сериализация события в JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize event", e);
        }
    }
    
    private DomainEvent deserializeEvent(EventEntity entity) {
        // Десериализация события из JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            Class<? extends DomainEvent> eventClass = getEventClass(entity.getEventType());
            return mapper.readValue(entity.getData(), eventClass);
        } catch (JsonProcessingException e) {
            throw new EventDeserializationException("Failed to deserialize event", e);
        }
    }
    
    private Class<? extends DomainEvent> getEventClass(String eventType) {
        // Маппинг типа события на класс
        Map<String, Class<? extends DomainEvent>> eventTypes = Map.of(
            "UserCreated", UserCreatedEvent.class,
            "UserEmailChanged", UserEmailChangedEvent.class,
            "UserDeactivated", UserDeactivatedEvent.class
        );
        return eventTypes.get(eventType);
    }
    
    private String serializeSnapshot(Snapshot snapshot) {
        // Сериализация snapshot
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new SnapshotSerializationException("Failed to serialize snapshot", e);
        }
    }
    
    private Snapshot deserializeSnapshot(String data) {
        // Десериализация snapshot
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, Snapshot.class);
        } catch (JsonProcessingException e) {
            throw new SnapshotDeserializationException("Failed to deserialize snapshot", e);
        }
    }
}

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_aggregate_id", columnList = "aggregateId"),
    @Index(name = "idx_aggregate_version", columnList = "aggregateId,version")
})
public class EventEntity {
    @Id
    private String eventId;
    private String aggregateId;
    private String eventType;
    @Lob
    private String data;
    private long version;
    private Instant occurredOn;
    
    // Constructors, getters, setters
}

@Entity
@Table(name = "snapshots")
public class SnapshotEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String aggregateId;
    private long version;
    @Lob
    private String data;
    private Instant createdAt;
    
    // Constructors, getters, setters
}
```

### Использование **Kafka** как **Event Store**

```java
@Component
public class KafkaEventStore implements EventStore {
    
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final KafkaConsumer<String, DomainEvent> kafkaConsumer;
    
    @Override
    public void saveEvents(String aggregateId, List<DomainEvent> events, long expectedVersion) {
        // Сохранение событий в Kafka topic
        for (DomainEvent event : events) {
            kafkaTemplate.send("events", aggregateId, event);
        }
    }
    
    @Override
    public List<DomainEvent> getEvents(String aggregateId) {
        // Чтение событий из Kafka topic
        ConsumerRecords<String, DomainEvent> records = kafkaConsumer.poll(Duration.ofSeconds(1));
        return StreamSupport.stream(records.spliterator(), false)
            .filter(record -> record.key().equals(aggregateId))
            .map(ConsumerRecord::value)
            .sorted(Comparator.comparing(DomainEvent::getOccurredOn))
            .collect(Collectors.toList());
    }
}
```

## **Aggregates** и **Domain Events**

### **Repository** для **Aggregate**

```java
public interface AggregateRepository<T extends Aggregate> {
    T findById(String aggregateId);
    void save(T aggregate);
}

@Component
public class UserRepository implements AggregateRepository<UserAggregate> {
    
    private final EventStore eventStore;
    private final SnapshotStore snapshotStore;
    
    @Override
    public UserAggregate findById(String aggregateId) {
        // Попытка загрузить из snapshot
        Snapshot snapshot = snapshotStore.getSnapshot(aggregateId);
        
        if (snapshot != null) {
            // Восстановление из snapshot
            UserAggregate aggregate = UserAggregate.fromSnapshot(snapshot);
            
            // Применение событий после snapshot
            List<DomainEvent> events = eventStore.getEvents(aggregateId, snapshot.getVersion());
            events.forEach(aggregate::applyHistorical);
            
            return aggregate;
        }
        
        // Загрузка всех событий
        List<DomainEvent> events = eventStore.getEvents(aggregateId);
        if (events.isEmpty()) {
            throw new AggregateNotFoundException("User not found: " + aggregateId);
        }
        
        return UserAggregate.fromHistory(events);
    }
    
    @Override
    @Transactional
    public void save(UserAggregate aggregate) {
        List<DomainEvent> uncommittedEvents = aggregate.getUncommittedEvents();
        if (uncommittedEvents.isEmpty()) {
            return;
        }
        
        long expectedVersion = aggregate.getVersion() - uncommittedEvents.size();
        eventStore.saveEvents(aggregate.getUserId(), uncommittedEvents, expectedVersion);
        
        aggregate.markEventsAsCommitted();
        
        // Создание snapshot периодически
        if (aggregate.getVersion() % 100 == 0) {
            Snapshot snapshot = aggregate.createSnapshot();
            snapshotStore.saveSnapshot(aggregate.getUserId(), snapshot);
        }
    }
}
```

## **Event Replay** и **Snapshots**

### **Event Replay**

**Event Replay** — это процесс восстановления состояния агрегата путем применения всех событий в порядке их возникновения.

```java
public class UserAggregate {
    
    public static UserAggregate fromHistory(List<DomainEvent> events) {
        UserAggregate user = new UserAggregate();
        
        for (DomainEvent event : events) {
            user.applyHistorical(event);
        }
        
        return user;
    }
    
    private void applyHistorical(DomainEvent event) {
        // Применение события без добавления в uncommittedEvents
        if (event instanceof UserCreatedEvent) {
            UserCreatedEvent e = (UserCreatedEvent) event;
            this.userId = e.getAggregateId();
            this.email = e.getEmail();
            this.name = e.getName();
            this.active = true;
        } else if (event instanceof UserEmailChangedEvent) {
            UserEmailChangedEvent e = (UserEmailChangedEvent) event;
            this.email = e.getNewEmail();
        } else if (event instanceof UserDeactivatedEvent) {
            this.active = false;
        }
        
        this.version++;
    }
}
```

### **Snapshots**

**Snapshots** — это снимки состояния агрегата на определенный момент времени, используемые для оптимизации восстановления состояния.

```java
public class Snapshot {
    private final String aggregateId;
    private final long version;
    private final Instant createdAt;
    private final Map<String, Object> state;
    
    public Snapshot(String aggregateId, long version, Map<String, Object> state) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.createdAt = Instant.now();
        this.state = new HashMap<>(state);
    }
    
    // Getters
}

public class UserAggregate {
    
    public Snapshot createSnapshot() {
        Map<String, Object> state = new HashMap<>();
        state.put("userId", userId);
        state.put("email", email);
        state.put("name", name);
        state.put("active", active);
        
        return new Snapshot(userId, version, state);
    }
    
    public static UserAggregate fromSnapshot(Snapshot snapshot) {
        UserAggregate user = new UserAggregate();
        user.userId = (String) snapshot.getState().get("userId");
        user.email = (String) snapshot.getState().get("email");
        user.name = (String) snapshot.getState().get("name");
        user.active = (Boolean) snapshot.getState().get("active");
        user.version = snapshot.getVersion();
        return user;
    }
}

@Component
public class SnapshotPolicy {
    
    public boolean shouldCreateSnapshot(Aggregate aggregate) {
        // Создавать snapshot каждые N событий
        return aggregate.getVersion() % 100 == 0;
    }
    
    public boolean shouldCreateSnapshot(Aggregate aggregate, Duration timeSinceLastSnapshot) {
        // Создавать snapshot если прошло много времени
        return timeSinceLastSnapshot.toDays() > 7;
    }
}
```

## Версионирование событий

Версионирование событий необходимо для поддержки эволюции схемы событий.

### Версионирование событий

```java
public abstract class DomainEvent {
    private final int version;
    
    protected DomainEvent(String eventType, int version) {
        this.version = version;
        // ...
    }
    
    public int getVersion() {
        return version;
    }
}

// Версия 1
public class UserCreatedEventV1 extends DomainEvent {
    private final String email;
    private final String name;
    
    public UserCreatedEventV1(String aggregateId, String email, String name) {
        super(aggregateId, "UserCreated", 1);
        this.email = email;
        this.name = name;
    }
}

// Версия 2 - добавлено поле phone
public class UserCreatedEventV2 extends DomainEvent {
    private final String email;
    private final String name;
    private final String phone; // Новое поле
    
    public UserCreatedEventV2(String aggregateId, String email, String name, String phone) {
        super(aggregateId, "UserCreated", 2);
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
    
    // Миграция из версии 1
    public static UserCreatedEventV2 fromV1(UserCreatedEventV1 v1) {
        return new UserCreatedEventV2(
            v1.getAggregateId(),
            v1.getEmail(),
            v1.getName(),
            null // phone отсутствует в v1
        );
    }
}

@Component
public class EventMigrator {
    
    public DomainEvent migrate(DomainEvent event) {
        if (event instanceof UserCreatedEventV1) {
            return UserCreatedEventV2.fromV1((UserCreatedEventV1) event);
        }
        return event; // Нет миграции
    }
}
```

## Миграция событий

Миграция событий позволяет обновлять старые события при их загрузке.

### **Upcaster** для миграции событий

```java
public interface EventUpcaster {
    DomainEvent upcast(DomainEvent event);
    boolean canUpcast(DomainEvent event);
}

@Component
public class UserCreatedEventUpcaster implements EventUpcaster {
    
    @Override
    public boolean canUpcast(DomainEvent event) {
        return event instanceof UserCreatedEventV1;
    }
    
    @Override
    public DomainEvent upcast(DomainEvent event) {
        if (event instanceof UserCreatedEventV1) {
            UserCreatedEventV1 v1 = (UserCreatedEventV1) event;
            return new UserCreatedEventV2(
                v1.getAggregateId(),
                v1.getEmail(),
                v1.getName(),
                null // phone отсутствует в v1
            );
        }
        return event;
    }
}

@Component
public class EventStore {
    
    private final List<EventUpcaster> upcasters;
    
    public List<DomainEvent> getEvents(String aggregateId) {
        List<EventEntity> entities = eventRepository.findByAggregateIdOrderByVersion(aggregateId);
        return entities.stream()
            .map(this::deserializeEvent)
            .map(this::upcastEvent)
            .collect(Collectors.toList());
    }
    
    private DomainEvent upcastEvent(DomainEvent event) {
        for (EventUpcaster upcaster : upcasters) {
            if (upcaster.canUpcast(event)) {
                event = upcaster.upcast(event);
            }
        }
        return event;
    }
}
```

## Интеграция с **CQRS**

**Event Sourcing** естественно сочетается с **CQRS**. **Write** модель использует **Event Sourcing**, а **Read** модель обновляется на основе событий.

```java
@Component
public class UserCommandHandler {
    
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public String handle(CreateUserCommand command) {
        UserAggregate user = UserAggregate.create(
            command.getEmail(),
            command.getName()
        );
        
        userRepository.save(user);
        
        // Публикация событий для обновления Read модели
        user.getUncommittedEvents().forEach(eventPublisher::publish);
        
        return user.getUserId();
    }
}

@Component
public class UserViewUpdater {
    
    private final UserViewRepository userViewRepository;
    
    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserCreated(UserCreatedEvent event) {
        UserView view = new UserView();
        view.setId(event.getAggregateId());
        view.setEmail(event.getEmail());
        view.setName(event.getName());
        view.setActive(true);
        view.setCreatedAt(event.getOccurredOn());
        
        userViewRepository.save(view);
    }
    
    @KafkaListener(topics = "user-events", groupId = "user-view-updater")
    public void handleUserEmailChanged(UserEmailChangedEvent event) {
        UserView view = userViewRepository.findById(event.getAggregateId())
            .orElseThrow(() -> new UserNotFoundException(event.getAggregateId()));
        
        view.setEmail(event.getNewEmail());
        view.setUpdatedAt(event.getOccurredOn());
        
        userViewRepository.save(view);
    }
}
```

## Реализация на **Spring**

### Конфигурация

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.eventsourcing.repository")
public class EventSourcingConfig {
    
    @Bean
    public EventStore eventStore(EventRepository eventRepository) {
        return new JpaEventStore(eventRepository);
    }
    
    @Bean
    public SnapshotStore snapshotStore(SnapshotRepository snapshotRepository) {
        return new JpaSnapshotStore(snapshotRepository);
    }
    
    @Bean
    public UserRepository userRepository(EventStore eventStore, SnapshotStore snapshotStore) {
        return new UserRepository(eventStore, snapshotStore);
    }
}
```

### **Command Handler**

```java
@Component
public class UserCommandHandler {
    
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    
    public String handle(CreateUserCommand command) {
        UserAggregate user = UserAggregate.create(
            command.getEmail(),
            command.getName()
        );
        
        userRepository.save(user);
        
        // Публикация событий
        user.getUncommittedEvents().forEach(eventPublisher::publish);
        
        return user.getUserId();
    }
    
    public void handle(ChangeUserEmailCommand command) {
        UserAggregate user = userRepository.findById(command.getUserId());
        user.changeEmail(command.getNewEmail());
        userRepository.save(user);
        
        user.getUncommittedEvents().forEach(eventPublisher::publish);
    }
}
```

## Реализация с **Axon Framework**

**Axon Framework** предоставляет готовую инфраструктуру для **Event Sourcing** и **CQRS**.

### Зависимости

```xml
<dependencies>
    <dependency>
        <groupId>org.axonframework</groupId>
        <artifactId>axon-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.axonframework</groupId>
        <artifactId>axon-mongo</artifactId>
    </dependency>
</dependencies>
```

### **Aggregate** с **Axon**

```java
@Aggregate
public class UserAggregate {
    
    @AggregateIdentifier
    private String userId;
    private String email;
    private String name;
    private boolean active;
    
    protected UserAggregate() {
        // Требуется для Axon
    }
    
    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        apply(new UserCreatedEvent(command.getUserId(), command.getEmail(), command.getName()));
    }
    
    @CommandHandler
    public void handle(ChangeUserEmailCommand command) {
        if (!active) {
            throw new IllegalStateException("Cannot change email for inactive user");
        }
        apply(new UserEmailChangedEvent(userId, email, command.getNewEmail()));
    }
    
    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.userId = event.getAggregateId();
        this.email = event.getEmail();
        this.name = event.getName();
        this.active = true;
    }
    
    @EventSourcingHandler
    public void on(UserEmailChangedEvent event) {
        this.email = event.getNewEmail();
    }
    
    @EventSourcingHandler
    public void on(UserDeactivatedEvent event) {
        this.active = false;
    }
}
```

### **Command** и **Query Handlers**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    
    @PostMapping
    public CompletableFuture<String> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = new CreateUserCommand(
            UUID.randomUUID().toString(),
            request.getEmail(),
            request.getName()
        );
        return commandGateway.send(command);
    }
    
    @GetMapping("/{id}")
    public CompletableFuture<UserView> getUser(@PathVariable String id) {
        return queryGateway.query(new GetUserByIdQuery(id), UserView.class);
    }
}

@Component
public class UserQueryHandler {
    
    @QueryHandler
    public UserView handle(GetUserByIdQuery query) {
        // Загрузка из Read модели
        return userViewRepository.findById(query.getUserId());
    }
}
```

## Оптимизация производительности

### Кэширование агрегатов

```java
@Component
public class CachedUserRepository implements AggregateRepository<UserAggregate> {
    
    private final UserRepository delegate;
    private final Cache<String, UserAggregate> cache;
    
    public CachedUserRepository(UserRepository delegate) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }
    
    @Override
    public UserAggregate findById(String aggregateId) {
        return cache.get(aggregateId, delegate::findById);
    }
    
    @Override
    public void save(UserAggregate aggregate) {
        delegate.save(aggregate);
        cache.invalidate(aggregate.getUserId());
    }
}
```

### **Batch** обработка событий

```java
@Component
public class BatchEventProcessor {
    
    private final List<DomainEvent> eventBuffer = new ArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(this::processBatch, 1, 1, TimeUnit.SECONDS);
    }
    
    public void addEvent(DomainEvent event) {
        synchronized (eventBuffer) {
            eventBuffer.add(event);
        }
    }
    
    private void processBatch() {
        List<DomainEvent> events;
        synchronized (eventBuffer) {
            events = new ArrayList<>(eventBuffer);
            eventBuffer.clear();
        }
        
        if (events.isEmpty()) {
            return;
        }
        
        // Batch сохранение событий
        Map<String, List<DomainEvent>> eventsByAggregate = events.stream()
            .collect(Collectors.groupingBy(DomainEvent::getAggregateId));
        
        eventsByAggregate.forEach((aggregateId, aggregateEvents) -> {
            eventStore.saveEvents(aggregateId, aggregateEvents, getExpectedVersion(aggregateId));
        });
    }
}
```

## Лучшие практики

### 1. Используйте **Snapshots** для больших агрегатов

Создавайте **snapshots** регулярно для агрегатов с большим количеством событий.

### 2. Версионируйте события

Всегда версионируйте события для поддержки эволюции схемы.

### 3. Храните метаданные событий

Сохраняйте метаданные (**user `ID`, correlation ID**) для аудита и отладки.

### 4. Используйте оптимистичную блокировку

Используйте версионирование для предотвращения конфликтов при параллельных изменениях.

### 5. Разделяйте события по доменам

Используйте разные **Event Stores** для разных доменов для лучшей изоляции.

## Решение проблем

### Проблема: Медленное восстановление состояния

**Решение:**
- Используйте **snapshots**
- Кэшируйте агрегаты
- Оптимизируйте запросы к **Event Store**

### Проблема: Конфликты версий

**Решение:**
- Используйте оптимистичную блокировку
- Реализуйте **retry** механизмы
- Используйте команды вместо прямого изменения состояния

### Проблема: Большой размер **Event Store**

**Решение:**
- Архивируйте старые события
- Используйте сжатие
- Регулярно создавайте **snapshots**

## Частые вопросы

**Когда использовать Event Sourcing?** Когда нужна полная история изменений, аудит, возможность переигрывать состояние и перестраивать read-модели. Не стоит применять в простых CRUD-системах без этих требований.

**Event Sourcing без CQRS возможен?** Да, но на практике read-модель часто строится из потока событий (проекции), что по сути CQRS. Для сложных запросов CQRS с Event Sourcing — типичная связка.

**Как мигрировать старые события при изменении схемы?** Версионируйте события, пишите upcaster’ы (или миграции), которые по старой версии возвращают новую; при replay применяйте upcaster перед применением к агрегату.
