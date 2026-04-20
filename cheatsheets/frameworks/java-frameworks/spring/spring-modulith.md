---
title: "Spring Modulith — модульный монолит"
description: "Spring Modulith: структура модулей, изоляция, ApplicationEvents между модулями, тестирование, документация архитектуры."
tags:
  - frameworks
  - spring
  - modulith
  - modular-monolith
  - architecture
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring Modulith — модульный монолит

Spring Modulith помогает строить модульные монолиты на Spring Boot: явные границы модулей, проверка зависимостей, изолированные тесты.

## Полезные ссылки

### Официальная документация
- [Spring Modulith (docs.spring.io)](https://docs.spring.io/spring-modulith/docs/current/reference/html/) — официальная документация Spring Modulith

### См. также
- [Spring Boot](../../spring/spring-boot.md) — базовый фреймворк
- [Spring Events](spring-events.md) — события Spring, используемые между модулями
- [Spring Core](../../spring/spring-core.md) — ядро Spring Framework
- [Вопросы на собеседовании](../../../interview/frameworks/spring/spring-modulith-interview.md) — подготовка к интервью

## Содержание

- [Зависимость](#зависимость)
- [Структура пакетов](#структура-пакетов)
- [Проверка архитектуры](#проверка-архитектуры)
- [Визуализация зависимостей](#визуализация-зависимостей)
- [Взаимодействие через ApplicationEvents](#взаимодействие-через-applicationevents)
- [Персистентность событий (Event Publication)](#персистентность-событий-event-publication)
- [Изолированное тестирование модуля](#изолированное-тестирование-модуля)
  - [Режимы bootstrap](#режимы-bootstrap)
- [Scenarios API](#scenarios-api)
- [Когда использовать](#когда-использовать)
- [Переход к микросервисам](#переход-к-микросервисам)

## Зависимость

```xml
<dependency>
  <groupId>org.springframework.modulith</groupId>
  <artifactId>spring-modulith-starter-core</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.modulith</groupId>
  <artifactId>spring-modulith-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

```groovy
implementation 'org.springframework.modulith:spring-modulith-starter-core'
testImplementation 'org.springframework.modulith:spring-modulith-starter-test'
```

## Структура пакетов

Каждый top-level пакет рядом с main-классом — отдельный модуль.

```text
com.example.shop
├── ShopApplication.java          ← main-класс
├── order/                        ← модуль Order
│   ├── Order.java                ← public API модуля
│   ├── OrderService.java
│   └── internal/                 ← закрытая часть
│       └── OrderRepository.java
├── inventory/                    ← модуль Inventory
│   ├── InventoryService.java
│   └── internal/
│       └── InventoryRepository.java
└── notification/                 ← модуль Notification
    └── NotificationService.java
```

**Правила изоляции:**
- Пакет `internal/` недоступен из других модулей.
- Модули не должны импортировать классы из `internal/` друг друга.
- Взаимодействие — только через публичный API и события.

## Проверка архитектуры

```java
@Test
void modulesAreCompliant() {
    ApplicationModules.of(ShopApplication.class).verify();
}
```

`verify()` бросает исключение при нарушении зависимостей (циклы, обращения к `internal`).

## Визуализация зависимостей

```java
ApplicationModules modules = ApplicationModules.of(ShopApplication.class);
// Вывод в консоль
modules.forEach(System.out::println);

// Генерация диаграмм (требует spring-modulith-docs)
new Documenter(modules)
    .writeModulesAsPlantUml()
    .writeIndividualModulesAsPlantUml();
```

```xml
<dependency>
  <groupId>org.springframework.modulith</groupId>
  <artifactId>spring-modulith-docs</artifactId>
  <optional>true</optional>
</dependency>
```

## Взаимодействие через ApplicationEvents

Модули общаются через события, не через прямые вызовы.

```java
// Модуль Order публикует событие
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher events;

    public void placeOrder(Order order) {
        // бизнес-логика
        events.publishEvent(new OrderPlaced(order.getId()));
    }
}

// Событие — простой record
public record OrderPlaced(UUID orderId) {}

// Модуль Inventory подписывается
@ApplicationModuleListener
public class InventoryListener {
    public void on(OrderPlaced event) {
        // уменьшить остаток
    }
}
```

`@ApplicationModuleListener` — обёртка над `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async`. Событие обрабатывается после успешного коммита транзакции.

## Персистентность событий (Event Publication)

Spring Modulith может сохранять события в БД и повторять их при сбоях.

```xml
<dependency>
  <groupId>org.springframework.modulith</groupId>
  <artifactId>spring-modulith-starter-jpa</artifactId>
</dependency>
```

```yaml
spring:
  modulith:
    republish-outstanding-events-on-restart: true
```

Таблица `event_publication` создаётся автоматически (Flyway/Liquibase скрипты в jar).

## Изолированное тестирование модуля

```java
@ApplicationModuleTest                      // только beans текущего модуля
class OrderModuleTests {

    @Test
    void placingOrderPublishesEvent(
            @Autowired OrderService orderService,
            ApplicationEvents events) {

        orderService.placeOrder(new Order(...));

        assertThat(events.ofType(OrderPlaced.class)).hasSize(1);
    }
}
```

### Режимы bootstrap

| Режим | Что загружается |
|-------|----------------|
| `STANDALONE` | только текущий модуль |
| `DIRECT_DEPENDENCIES` | модуль + прямые зависимости |
| `ALL_DEPENDENCIES` | весь граф зависимостей |

```java
@ApplicationModuleTest(mode = BootstrapMode.DIRECT_DEPENDENCIES)
class OrderModuleIntegrationTests { ... }
```

## Scenarios API

```java
@ApplicationModuleTest
class OrderScenarios {

    @Test
    void orderPlacedSendsNotification(Scenario scenario) {
        scenario.stimulate(() -> orderService.placeOrder(order))
                .andWaitForEventOfType(NotificationSent.class)
                .toArrive()
                .andVerify(event -> assertThat(event.orderId()).isEqualTo(order.getId()));
    }
}
```

## Когда использовать

**Подходит, если:**
- Команда работает над одним деплоем.
- Хочется явных границ без накладных расходов микросервисов.
- Приложение планирует вырасти до микросервисов — модули станут основой декомпозиции.

**Не подходит, если:**
- Нужно независимое масштабирование компонент.
- Разные модули разрабатывают разные команды с независимым деплоем.
- Требования к изоляции данных на уровне БД (разные схемы/БД).

## Переход к микросервисам

Модуль → микросервис: публичный API модуля становится REST/gRPC контрактом, `ApplicationEvents` — Kafka/RabbitMQ-сообщениями. Spring Modulith облегчает этот шаг, так как границы уже чёткие.

