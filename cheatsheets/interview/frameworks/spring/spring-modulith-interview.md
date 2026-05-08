---
title: "Вопросы на собеседовании: Spring Modulith"
description: "Spring Modulith для модульных монолитов: определение модулей, изоляция, @ApplicationModuleListener, персистентность событий, тестирование, переход к микросервисам"
tags:
  - interview
  - spring
  - spring-modulith-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Modulith"
  - "Spring Modulith interview"
  - "Spring Modulith собеседование"
prerequisites:
  - "[[spring-modulith]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Modulith`

`Spring Modulith` — библиотека для построения модульных монолитов на Spring Boot. Обеспечивает явные границы между модулями, верифицирует зависимости и предоставляет event-driven взаимодействие через `ApplicationEvents`.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Modulith Docs](https://docs.spring.io/spring-modulith/docs/current/reference/html/) — официальная документация
- [Baeldung: Spring Modulith](https://www.baeldung.com/spring-modulith) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Spring Modulith и какую проблему он решает?](#q1-что-такое-spring-modulith-и-какую-проблему-он-решает)
- [Q2. Как Spring Modulith определяет модуль?](#q2-как-spring-modulith-определяет-модуль)
- [Q3. Как проверить соблюдение архитектурных правил?](#q3-как-проверить-соблюдение-архитектурных-правил)
- [Q4. (!) Как модули должны взаимодействовать между собой?](#q4-как-модули-должны-взаимодействовать-между-собой)
- [Q5. Что такое @ApplicationModuleListener?](#q5-что-такое-applicationmodulelistener)

**Персистентность и тестирование**
- [Q6. Как работает персистентность событий?](#q6-как-работает-персистентность-событий)
- [Q7. (!) Как тестировать отдельный модуль в изоляции?](#q7-как-тестировать-отдельный-модуль-в-изоляции)
- [Q8. Какие режимы bootstrap существуют в @ApplicationModuleTest?](#q8-какие-режимы-bootstrap-существуют-в-applicationmoduletest)
- [Q9. Что такое Scenarios API?](#q9-что-такое-scenarios-api)

**Архитектура и продвинутые темы**
- [Q10. Как визуализировать зависимости между модулями?](#q10-как-визуализировать-зависимости-между-модулями)
- [Q11. (!) Когда использовать Spring Modulith, а когда — микросервисы?](#q11-когда-использовать-spring-modulith-а-когда--микросервисы)
- [Q12. Как Spring Modulith помогает при переходе к микросервисам?](#q12-как-spring-modulith-помогает-при-переходе-к-микросервисам)
- [Q13. Что такое Named Interface?](#q13-что-такое-named-interface)
- [Q14. Как включить Spring Modulith в существующий проект?](#q14-как-включить-spring-modulith-в-существующий-проект)
- [Q15. Как Spring Modulith соотносится с Hexagonal Architecture и DDD?](#q15-как-spring-modulith-соотносится-с-hexagonal-architecture-и-ddd)

## Q1. Что такое Spring Modulith и какую проблему он решает?

Spring Modulith — библиотека для построения **модульных монолитов** на Spring Boot. Она решает проблему «большого кома грязи» (Big Ball of Mud): код растёт, границы модулей размываются, циклические зависимости появляются незаметно.

**Модульный монолит** — один деплоируемый артефакт, но с явными границами между модулями, изоляцией и верифицируемыми зависимостями. Переходный шаг между монолитом и микросервисами.


> [!mcq]
> - [ ] Фреймворк для деплоя микросервисов, аналог Docker Compose для Spring | ❌ ПОСЛЕДСТВИЕ: без изоляции пакетов модульный монолит деградирует в «Big Ball of Mud» — границы незаметно размываются
> - [ ] Инструмент генерации микросервисов из аннотаций автоматически | ❌ ПОСЛЕДСТВИЕ: автогенерация без явных bounded context создаёт распределённый монолит без преимуществ обоих подходов
> - [x] Библиотека для модульных монолитов: явные пакетные границы, verify() проверяет зависимости, ApplicationEvents для inter-module communication | ✓ ПРИМЕНЯТЬ: когда монолит растёт и нужно выделить bounded contexts до перехода к микросервисам 📋 ПРАВИЛО: Spring Modulith = package-enforced modularity + event-driven + testable isolation 🔗 См. Q4
> - [ ] Альтернатива JPMS для управления зависимостями между jar-модулями | ❌ ПОСЛЕДСТВИЕ: JPMS и Spring Modulith решают разные проблемы; JPMS не верифицирует архитектурные правила между сервисными компонентами 🔗 См. Q11

Каждый **top-level пакет** рядом с классом, аннотированным `@SpringBootApplication`, — это отдельный модуль.

```
com.example.shop
├── ShopApplication.java       ← корневой пакет
├── order/                     ← модуль Order
│   ├── OrderService.java      ← public API (доступен другим модулям)
│   └── internal/              ← закрытая реализация
│       └── OrderRepository.java
├── inventory/                 ← модуль Inventory
└── notification/              ← модуль Notification
```

Классы в `internal/` недоступны другим модулям — это нарушение, которое `verify()` обнаружит.


> [!mcq]
> - [ ] Каждый класс с аннотацией @Module в любом пакете | ❌ ПОСЛЕДСТВИЕ: без пакетной структуры модульность теряется — annotated beans не ограничивают прямой доступ к internal/ классам
> - [x] Каждый top-level пакет рядом с @SpringBootApplication; internal/ — закрытая реализация, недоступная другим модулям; verify() обнаруживает нарушения | ✓ ПРИМЕНЯТЬ: структурировать монолит в явные bounded contexts через конвенцию пакетов 📋 ПРАВИЛО: top-level пакет = модуль; internal/ = private; нарушение → тест упадёт 🔗 См. Q3
> - [ ] Каждый Maven/Gradle субмодуль соответствует одному Spring Modulith модулю | ❌ ПОСЛЕДСТВИЕ: Maven-модули создают compile-time изоляцию, но не верифицируют Spring bean dependencies — verify() не поможет
> - [ ] Пакеты указанные в @SpringBootApplication(scanModules=...) | ❌ ПОСЛЕДСТВИЕ: такого атрибута нет; отсутствие пакетной конвенции сломает автоматическое обнаружение модулей 🔗 См. Q14

```java
@Test
void modulesAreCompliant() {
    ApplicationModules.of(ShopApplication.class).verify();
}
```

`verify()` проверяет:
- Нет ссылок на `internal/` из других модулей.
- Нет циклических зависимостей между модулями.

При нарушении выбрасывает исключение с описанием проблемы. Рекомендуется запускать как часть CI.


> [!mcq]
> - [ ] Добавить @EnableModulesVerification на класс @SpringBootApplication | ❌ ПОСЛЕДСТВИЕ: такой аннотации нет — модули не проверяются, нарушения незаметно накапливаются в кодовой базе
> - [ ] Настроить spring.modulith.verify=true в application.yml | ❌ ПОСЛЕДСТВИЕ: такого свойства нет — verify() нужно вызывать явно в тесте; нарушения не обнаружатся в CI
> - [ ] Использовать ArchUnit с classes().that().resideInAPackage(...) | ❌ ПОСЛЕДСТВИЕ: ArchUnit не знает о Spring Modulith internal/ конвенции — потребует ручной поддержки всех правил
> - [x] ApplicationModules.of(ShopApplication.class).verify() в @Test — обнаруживает internal/ нарушения и циклы; упасть в CI при первом же нарушении | ✓ ПРИМЕНЯТЬ: добавить verify() тест в CI 📋 ПРАВИЛО: один тест — ноль нарушений архитектуры 🔗 См. Q7

Модули взаимодействуют **только через публичный API** (интерфейсы и классы не в `internal/`) и через **Spring Application Events** — без прямых вызовов через `internal/`.

```java
// Модуль Order публикует событие
@Service
public class OrderService {
    private final ApplicationEventPublisher events;

    public void placeOrder(Order order) {
        orderRepository.save(order);
        events.publishEvent(new OrderPlaced(order.getId()));
    }
}

// Событие — простой record
public record OrderPlaced(UUID orderId) {}

// Модуль Inventory подписывается
@ApplicationModuleListener
public class InventoryListener {
    public void on(OrderPlaced event) {
        inventoryService.decreaseStock(event.orderId());
    }
}
```


> [!mcq]
> - [ ] Через прямые вызовы service-методов из internal/ другого модуля | ❌ ПОСЛЕДСТВИЕ: verify() выбросит AssertionError с описанием forbidden dependency — код не пройдёт CI
> - [x] Через публичный API (non-internal классы) или Spring ApplicationEvents; никаких прямых зависимостей на internal/ другого модуля | ✓ ПРИМЕНЯТЬ: inter-module communication через события 📋 ПРАВИЛО: модуль публикует Event → другой модуль слушает через @ApplicationModuleListener 🔗 См. Q5
> - [ ] Через shared database tables с cross-module foreign keys | ❌ ПОСЛЕДСТВИЕ: нарушается изоляция данных; при переходе к микросервисам придётся разрывать DB-связи в сложной миграции
> - [ ] Через внедрение бинов из internal/ через @Autowired | ❌ ПОСЛЕДСТВИЕ: verify() обнаружит нарушение видимости и выбросит исключение с forbidden imports 🔗 См. Q2

`@ApplicationModuleListener` — составная аннотация:
- `@TransactionalEventListener(phase = AFTER_COMMIT)` — обработка после успешного коммита транзакции.
- `@Async` — выполнение в отдельном потоке.

Это предотвращает выполнение побочных эффектов в рамках основной транзакции и повышает изоляцию модулей. Обычный `@EventListener` выполняется синхронно в той же транзакции.


> [!mcq]
> - [ ] @ApplicationModuleListener выполняется синхронно в той же транзакции, что и publisher | ❌ ПОСЛЕДСТВИЕ: побочный эффект (email, Kafka) происходит до COMMIT — при rollback эффект уже произошёл, данные несогласованны
> - [ ] @ApplicationModuleListener аналогичен @KafkaListener и требует внешнего брокера | ❌ ПОСЛЕДСТВИЕ: приложение не запустится без Kafka-конфигурации; это in-process механизм без инфраструктурных зависимостей
> - [ ] @ApplicationModuleListener это просто alias для @EventListener без дополнительного поведения | ❌ ПОСЛЕДСТВИЕ: с обычным @EventListener потеряется transactional phase AFTER_COMMIT — побочные эффекты в неверный момент
> - [x] @TransactionalEventListener(AFTER_COMMIT) + @Async: обработка только после успешного COMMIT; в отдельном потоке; при failure — событие в event_publication для retry | ✓ ПРИМЕНЯТЬ: всегда для cross-module event handling 📋 ПРАВИЛО: @ApplicationModuleListener = @TransactionalEventListener(AFTER_COMMIT) + @Async 🔗 См. Q6

```xml
<dependency>
  <groupId>org.springframework.modulith</groupId>
  <artifactId>spring-modulith-starter-jpa</artifactId>
</dependency>
```

Spring Modulith создаёт таблицу `event_publication` и сохраняет каждое событие до его обработки. При рестарте приложения необработанные события повторяются:

```yaml
spring:
  modulith:
    republish-outstanding-events-on-restart: true
```

Это даёт **гарантию доставки at-least-once** внутри монолита без внешнего брокера.


> [!mcq]
> - [x] spring-modulith-starter-jpa создаёт event_publication table; события сохраняются до обработки; republish-on-restart обеспечивает at-least-once без внешнего брокера | ✓ ПРИМЕНЯТЬ: гарантированная доставка in-process событий 📋 ПРАВИЛО: event_publication = persisted inbox; при restart — retry необработанных 🔗 См. Q5
> - [ ] События хранятся только в памяти и теряются при падении приложения | ❌ ПОСЛЕДСТВИЕ: при рестарте после сбоя необработанные события потеряны — нет at-least-once гарантии
> - [ ] Персистентность требует отдельного Kafka-топика для хранения событий | ❌ ПОСЛЕДСТВИЕ: это накладывает инфраструктурные зависимости на монолит без реальной нужды в брокере
> - [ ] Spring Modulith хранит события в Redis с TTL, без таблицы в БД | ❌ ПОСЛЕДСТВИЕ: Redis без persistence → при рестарте необработанные события теряются; нет гарантии at-least-once 🔗 См. Q5

```java
@ApplicationModuleTest
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

`@ApplicationModuleTest` загружает только бины текущего модуля. Зависимости от других модулей автоматически мокируются.


> [!mcq]
> - [ ] @SpringBootTest с исключением других модулей через @ComponentScan.excludeFilters | ❌ ПОСЛЕДСТВИЕ: поднимается полный контекст; тест медленнее в 5-10x; другие модули не мокируются автоматически
> - [ ] @WebMvcTest для изоляции web-слоя модуля | ❌ ПОСЛЕДСТВИЕ: @WebMvcTest изолирует только web-layer; service и repository бины модуля не контролируются
> - [x] @ApplicationModuleTest: только бины текущего модуля; другие модули автомокируются; ApplicationEvents для проверки published events | ✓ ПРИМЕНЯТЬ: быстрые модульные тесты с изоляцией 📋 ПРАВИЛО: @ApplicationModuleTest = lightweight context; auto-mock других модулей 🔗 См. Q8
> - [ ] @ContextConfiguration(classes = OrderModule.class) с явным списком бинов | ❌ ПОСЛЕДСТВИЕ: при добавлении бинов тест надо обновлять вручную; нет автоматической верификации модульных границ 🔗 См. Q8

| Режим | Что загружается |
|-------|-----------------|
| `STANDALONE` | Только текущий модуль (по умолчанию) |
| `DIRECT_DEPENDENCIES` | Модуль + прямые зависимости |
| `ALL_DEPENDENCIES` | Весь граф зависимостей |

```java
@ApplicationModuleTest(mode = BootstrapMode.DIRECT_DEPENDENCIES)
class OrderIntegrationTests { ... }
```


> [!mcq]
> - [ ] ISOLATED — загружает только Spring core без @Component-сканирования | ❌ ПОСЛЕДСТВИЕ: такого режима нет; тест упадёт с NoSuchBeanDefinitionException или загрузит лишние зависимости
> - [x] STANDALONE=только текущий модуль (default); DIRECT_DEPENDENCIES=модуль + прямые зависимости; ALL_DEPENDENCIES=весь граф | ✓ ПРИМЕНЯТЬ: STANDALONE для unit-level; ALL_DEPENDENCIES для полного integration теста 📋 ПРАВИЛО: чем шире режим — тем медленнее тест; начинай с STANDALONE 🔗 См. Q7
> - [ ] MICROSERVICES — симулирует вызов модуля через REST endpoint | ❌ ПОСЛЕДСТВИЕ: такого режима нет в API; для REST нужны отдельные integration тесты с @SpringBootTest
> - [ ] FULL_CONTEXT — загружает полный Spring Boot контекст, аналог @SpringBootTest | ❌ ПОСЛЕДСТВИЕ: такого режима нет; STANDALONE/DIRECT_DEPENDENCIES/ALL_DEPENDENCIES — правильные варианты 🔗 См. Q7

Scenarios API — высокоуровневый DSL для интеграционного тестирования взаимодействия модулей через события:

```java
@ApplicationModuleTest
class OrderScenarios {

    @Test
    void orderPlacedTriggersNotification(Scenario scenario) {
        scenario
            .stimulate(() -> orderService.placeOrder(order))
            .andWaitForEventOfType(NotificationSent.class)
            .toArrive()
            .andVerify(event ->
                assertThat(event.orderId()).isEqualTo(order.getId()));
    }
}
```

Удобен для тестирования асинхронных сценариев с `@ApplicationModuleListener`.


> [!mcq]
> - [ ] Scenarios API для нагрузочного тестирования производительности модулей | ❌ ПОСЛЕДСТВИЕ: это тестовый DSL для интеграционных тестов событий, не нагрузочный инструмент; async-взаимодействия останутся непроверенными
> - [ ] Scenarios API заменяет AssertJ и предоставляет fluent assertions для всего приложения | ❌ ПОСЛЕДСТВИЕ: Scenarios API — дополнение к AssertJ для event-based сценариев, не замена; без него async-события тяжело тестировать
> - [ ] Scenarios API доступен только в @SpringBootTest, не в @ApplicationModuleTest | ❌ ПОСЛЕДСТВИЕ: наоборот — Scenarios API интегрирован именно с @ApplicationModuleTest для изолированного тестирования
> - [x] DSL для async event тестирования: scenario.stimulate(action).andWaitForEventOfType(X.class).toArrive().andVerify(event→assertion) | ✓ ПРИМЕНЯТЬ: тестирование async событий между модулями 📋 ПРАВИЛО: stimulate → andWaitForEventOfType → andVerify; без Scenarios API async-тесты требуют manual await 🔗 См. Q5

```java
// Вывод в консоль
ApplicationModules modules = ApplicationModules.of(ShopApplication.class);
modules.forEach(System.out::println);

// Генерация PlantUML-диаграмм (требует spring-modulith-docs)
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


> [!mcq]
> - [x] ApplicationModules.of(App.class).forEach(System.out::println) для текста; new Documenter(modules).writeModulesAsPlantUml() с spring-modulith-docs для PlantUML | ✓ ПРИМЕНЯТЬ: архитектурная документация из кода автоматически 📋 ПРАВИЛО: Documenter = code → diagram; обновляется при каждом запуске тестов 🔗 См. Q3
> - [ ] Выполнить ./mvnw spring-modulith:visualize как Maven плагин | ❌ ПОСЛЕДСТВИЕ: такого Maven плагина нет; диаграммы только через Java API Documenter с spring-modulith-docs зависимостью
> - [ ] Использовать @EnableModulithDiagram аннотацию на @SpringBootApplication | ❌ ПОСЛЕДСТВИЕ: такой аннотации нет; без spring-modulith-docs и Documenter диаграммы не создаются
> - [ ] ApplicationModules.of(App.class).drawDiagram() без дополнительных зависимостей | ❌ ПОСЛЕДСТВИЕ: метода drawDiagram() нет в API; нужен new Documenter(modules).writeModulesAsPlantUml() 🔗 См. Q3

**Spring Modulith подходит, если:**
- Команда небольшая (2–10 разработчиков) и работает над одним деплоем.
- Хочется явных границ без накладных расходов микросервисов (сети, независимого деплоя, распределённых транзакций).
- Планируется постепенный переход к микросервисам — модули станут основой декомпозиции.

**Микросервисы нужны, если:**
- Независимое масштабирование компонент.
- Разные команды с независимым циклом деплоя.
- Изоляция данных на уровне БД (разные схемы или БД).


> [!mcq]
> - [ ] Spring Modulith подходит только для proof-of-concept, не для production | ❌ ПОСЛЕДСТВИЕ: избегание модульного монолита ради «настоящих» микросервисов добавляет ненужную операционную сложность
> - [x] Modulith: небольшая команда, один деплой, явные границы без распределённой сложности; Microservices: независимое масштабирование, разные команды/циклы деплоя | ✓ ПРИМЕНЯТЬ: Modulith как промежуточный шаг к микросервисам 📋 ПРАВИЛО: не декомпозируй преждевременно — Modulith даёт boundary без overhead сети 🔗 См. Q12
> - [ ] Микросервисы всегда лучше монолита при команде 5+ человек | ❌ ПОСЛЕДСТВИЕ: преждевременная декомпозиция увеличивает latency, усложняет транзакции и требует дорогой инфраструктуры без реальной нужды
> - [ ] Spring Modulith требует PostgreSQL — с MongoDB не совместим | ❌ ПОСЛЕДСТВИЕ: нет такого ограничения; Spring Modulith работает с любой БД через Spring Data 🔗 См. Q12

Модуль → микросервис:
- Публичный API модуля становится REST/gRPC контрактом.
- `ApplicationEvents` → Kafka/RabbitMQ-сообщения.
- `internal/` пакет — уже скрыт, его не нужно рефакторить.
- Зависимости между модулями уже явны и верифицированы.

Spring Modulith облегчает этот переход: границы чёткие с самого начала, нет сюрпризов при декомпозиции.


> [!mcq]
> - [ ] При переходе нужно переписать Spring Modulith на Spring Cloud аннотации | ❌ ПОСЛЕДСТВИЕ: публичный API модуля уже готов стать контрактом; переписывание необходимо только для transport layer, не бизнес-логики
> - [ ] Переход невозможен без Apache Kafka как обязательного event bus | ❌ ПОСЛЕДСТВИЕ: Kafka — один из вариантов; можно RabbitMQ, REST, gRPC — выбор зависит от требований
> - [x] Публичный API → REST/gRPC контракт; ApplicationEvents → Kafka/RabbitMQ; internal/ уже скрыт; зависимости уже верифицированы и явны | ✓ ПРИМЕНЯТЬ: плавный переход без сюрпризов 📋 ПРАВИЛО: Modulith = готовая граница; смена транспорта без рефакторинга бизнес-логики 🔗 См. Q11
> - [ ] ApplicationEvents нельзя перенести в Kafka без полного рефакторинга логики | ❌ ПОСЛЕДСТВИЕ: ApplicationEvents → Kafka это смена транспорта, не логики; @ApplicationModuleListener уже async — семантика сохраняется 🔗 См. Q5

По умолчанию публичный API модуля — всё в корне пакета. Named Interface позволяет явно объявить несколько точек входа:

```java
@org.springframework.modulith.NamedInterface("api")
package com.example.shop.order.api;
```

Другие модули могут зависеть только от конкретного named interface, а не от всего модуля. Полезно для больших модулей с разными аспектами (API, events, config).


> [!mcq]
> - [x] @NamedInterface("api") на package-info.java создаёт явную точку входа; другие модули зависят только от конкретного named interface, а не от всего root модуля | ✓ ПРИМЕНЯТЬ: большие модули с разными аспектами (api, events, config) 📋 ПРАВИЛО: Named Interface = explicit sub-boundary; granular isolation within module 🔗 См. Q2
> - [ ] @NamedInterface аннотирует класс для именования Spring bean в DI контейнере | ❌ ПОСЛЕДСТВИЕ: @NamedInterface аннотирует пакет через package-info.java, не класс; без правильного применения изоляция не достигается
> - [ ] Named Interface создаёт RESTful endpoint с именем интерфейса | ❌ ПОСЛЕДСТВИЕ: никакого HTTP endpoint не создаётся; это механизм видимости пакетов внутри модуля
> - [ ] Named Interface обязателен для всех модулей, иначе verify() упадёт | ❌ ПОСЛЕДСТВИЕ: Named Interface опционален; по умолчанию публичный API — весь корень пакета; verify() работает и без него 🔗 См. Q2

1. Добавить зависимость `spring-modulith-starter-core`.
2. Запустить `ApplicationModules.of(App.class).verify()` — получить список нарушений.
3. Исправлять нарушения итеративно: переносить классы в `internal/`, удалять нежелательные зависимости.
4. Добавить `verify()` как тест в CI.

Не нужно рефакторить всё сразу — добавление проверки уже предотвращает деградацию архитектуры.


> [!mcq]
> - [x] spring-modulith-starter-core → verify() → итеративно исправлять нарушения → добавить verify() в CI; не нужно рефакторить всё сразу | ✓ ПРИМЕНЯТЬ: постепенная adoption без большого bang рефакторинга 📋 ПРАВИЛО: verify() → список нарушений → исправляй итеративно; CI защищает от регрессий 🔗 См. Q3
> - [ ] Начать с рефакторинга всего кода в internal/ прежде чем добавить зависимость | ❌ ПОСЛЕДСТВИЕ: "big bang" рефакторинг блокирует команду на недели; iterative approach быстрее и безопаснее
> - [ ] Нужно написать custom ArchUnit rules перед использованием Spring Modulith | ❌ ПОСЛЕДСТВИЕ: Spring Modulith заменяет ручные ArchUnit rules; verify() работает из коробки без конфигурации
> - [ ] Требуется миграция всех тестов на @ApplicationModuleTest перед первым verify() | ❌ ПОСЛЕДСТВИЕ: @ApplicationModuleTest и verify() независимы; начни с verify() без изменения тестов

## Q15. Как Spring Modulith соотносится с Hexagonal Architecture и DDD?

Spring Modulith реализует концепцию **Bounded Context** из DDD на уровне пакетов Java. Каждый модуль = bounded context с публичным API и изолированной реализацией.

Hexagonal Architecture: `internal/` — адаптеры и реализации; публичный API модуля — порты. Spring Modulith не навязывает конкретную внутреннюю структуру, но хорошо сочетается с Hexagonal.


> [!mcq]
> - [ ] Spring Modulith несовместим с DDD — DDD требует отдельных сервисов | ❌ ПОСЛЕДСТВИЕ: DDD Bounded Context прекрасно реализуется в одном приложении через пакетную изоляцию; Spring Modulith — инструмент DDD в монолите
> - [ ] Hexagonal Architecture требует отдельных Spring модулей (Maven субмодулей) | ❌ ПОСЛЕДСТВИЕ: Hexagonal — архитектурный паттерн, не Maven структура; internal/ в Spring Modulith уже обеспечивает изоляцию adapter от domain
> - [x] Каждый Spring Modulith модуль = DDD Bounded Context; internal/ = адаптеры в Hexagonal; публичный API = порты; структуры взаимодополняют друг друга | ✓ ПРИМЕНЯТЬ: Spring Modulith + DDD + Hexagonal как единая архитектура 📋 ПРАВИЛО: модуль = BC; internal/ = adapters; root = ports; verify() = dependency rules 🔗 См. Q4
> - [ ] Aggregate Root в DDD должен быть единственным public классом модуля | ❌ ПОСЛЕДСТВИЕ: модуль может экспортировать service, events, dto; Aggregate Root — внутренняя концепция domain, не граница видимости модуля

## See also

- [Spring Events](spring-events-interview.md) — ApplicationEventPublisher/Listener, основа взаимодействия модулей
- [Spring Boot](spring-boot-interview.md) — auto-configuration, основа для Spring Modulith
- [Microservices](../../architecture/microservices-interview.md) — переход модульного монолита к микросервисам
- [Domain-Driven Design](../../architecture/ddd-interview.md) — bounded contexts, aggregate roots — концепции модулей
- [Hexagonal Architecture](../../architecture/hexagonal-architecture-interview.md) — internal/ как адаптеры и реализации
- [Spring Testing](spring-testing-interview.md) — @ApplicationModuleTest изоляция при тестировании
- [Clean Architecture](../../architecture/clean-architecture-interview.md) — separation of concerns на уровне пакетов
- [Spring Kafka](spring-kafka-interview.md) — Kafka как замена ApplicationEvents при переходе к микросервисам
