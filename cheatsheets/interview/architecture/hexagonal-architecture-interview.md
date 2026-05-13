---
title: "Вопросы на собеседовании: Hexagonal Architecture (Ports & Adapters)"
description: "Полное покрытие гексагональной архитектуры: порты (driving/driven), адаптеры (primary/secondary), структура проекта в Spring Boot, интеграция с DDD, стратегия тестирования, сравнение с Clean и Onion, миграция со слоистой архитектуры, антипаттерны."
tags:
  - interview
  - architecture
  - hexagonal-architecture-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Hexagonal Architecture"
  - "Ports & Adapters"
  - "Ports and Adapters interview"
prerequisites:
  - "[[hexagonal-architecture]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Hexagonal Architecture (Ports & Adapters)`

Полное покрытие `Hexagonal Architecture` (`Ports & Adapters`, Алистер Кокбёрн, 2005): порты (`driving`/`driven`), адаптеры (`primary`/`secondary`), структура Spring Boot проекта, интеграция с `DDD`, стратегия тестирования, сравнение с `Clean` и `Onion Architecture`, миграция со слоистой архитектуры, антипаттерны.

**Hexagonal Architecture** (оригинальное название -- **Ports & Adapters**) -- один из базовых архитектурных паттернов, придуманный Алистером Кокбёрном ещё в 2005 году. Главная идея -- сделать ядро приложения (доменную логику) полностью независимым от способов его вызова (UI, REST, Kafka) и от внешних зависимостей (БД, сторонние API). На собеседованиях уровня Senior/Lead вопросы по гексагональной архитектуре проверяют понимание `Dependency Inversion`, умение проектировать границы между доменом и инфраструктурой и знание практик реализации в `Java`/`Spring Boot`.

## Полезные ссылки

### Официальная документация

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/) -- оригинальная статья автора паттерна
- [Hexagonal Architecture, DDD, and Spring (Baeldung)](https://www.baeldung.com/hexagonal-architecture-ddd-spring) -- пример реализации в Spring
- [Hexagonal Architecture with Java and Spring (Reflectoring)](https://reflectoring.io/spring-hexagonal/) -- детальный гайд по структуре проекта
- [Hexagonal Architecture pattern (AWS Prescriptive Guidance)](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/hexagonal-architecture.html) -- обзор паттерна от AWS
- [Hexagonal Architecture With Spring Boot (Arho Huttunen)](https://www.arhohuttunen.com/hexagonal-architecture-spring-boot/) -- практическая реализация
- [ArchUnit (Baeldung)](https://www.baeldung.com/java-archunit-intro) -- автоматическая проверка архитектурных правил
- ["Get Your Hands Dirty on Clean Architecture" (Tom Hombergs)](https://reflectoring.io/book/) -- каноническая книга по гексагональной архитектуре в Spring

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Hexagonal Architecture**
- [Q1. (!) Что такое Hexagonal Architecture и какую проблему она решает?](#q1--что-такое-hexagonal-architecture-и-какую-проблему-она-решает)
- [Q2. (!) Почему именно шестиугольник? В чём смысл такой визуализации?](#q2--почему-именно-шестиугольник-в-чём-смысл-такой-визуализации)
- [Q3. Какая история и авторство у паттерна Ports & Adapters?](#q3-какая-история-и-авторство-у-паттерна-ports--adapters)
- [Q4. Что такое Application Core и что в него входит?](#q4-что-такое-application-core-и-что-в-него-входит)
- [Q5. Какие три основные зоны определяет гексагональная архитектура?](#q5-какие-три-основные-зоны-определяет-гексагональная-архитектура)

**Порты (Driving / Driven)**
- [Q6. (!) Что такое Port и чем он отличается от обычного интерфейса?](#q6--что-такое-port-и-чем-он-отличается-от-обычного-интерфейса)
- [Q7. (!) В чём разница между Driving (Input) и Driven (Output) портами?](#q7--в-чём-разница-между-driving-input-и-driven-output-портами)
- [Q8. Как правильно именовать порты?](#q8-как-правильно-именовать-порты)
- [Q9. Сколько портов должно быть у приложения? Гранулярность портов](#q9-сколько-портов-должно-быть-у-приложения-гранулярность-портов)
- [Q10. Может ли один адаптер реализовывать несколько портов?](#q10-может-ли-один-адаптер-реализовывать-несколько-портов)

**Адаптеры (Primary / Secondary)**
- [Q11. (!) Что такое Primary (Driving) Adapter и какие у него обязанности?](#q11--что-такое-primary-driving-adapter-и-какие-у-него-обязанности)
- [Q12. (!) Что такое Secondary (Driven) Adapter и какие у него обязанности?](#q12--что-такое-secondary-driven-adapter-и-какие-у-него-обязанности)
- [Q13. Примеры Primary и Secondary адаптеров в реальном приложении](#q13-примеры-primary-и-secondary-адаптеров-в-реальном-приложении)
- [Q14. Как адаптер преобразует данные между внешним миром и доменом?](#q14-как-адаптер-преобразует-данные-между-внешним-миром-и-доменом)
- [Q15. Может ли адаптер быть одновременно primary и secondary?](#q15-может-ли-адаптер-быть-одновременно-primary-и-secondary)

**Структура проекта в Java / Spring Boot**
- [Q16. (!) Какая рекомендуемая структура пакетов для гексагональной архитектуры в Spring Boot?](#q16--какая-рекомендуемая-структура-пакетов-для-гексагональной-архитектуры-в-spring-boot)
- [Q17. Как реализовать Driving Port и Use Case в Java?](#q17-как-реализовать-driving-port-и-use-case-в-java)
- [Q18. Как реализовать Driven Port и его адаптер?](#q18-как-реализовать-driven-port-и-его-адаптер)
- [Q19. (!) Стоит ли использовать `@Service` на Use Case или держать ядро чистым?](#q19--стоит-ли-использовать-service-на-use-case-или-держать-ядро-чистым)
- [Q20. Где размещать транзакционные границы в гексагональной архитектуре?](#q20-где-размещать-транзакционные-границы-в-гексагональной-архитектуре)
- [Q21. Как организовать DI / Composition Root в Spring?](#q21-как-организовать-di--composition-root-в-spring)
- [Q22. Multi-module Gradle/Maven проект для гексагональной архитектуры](#q22-multi-module-gradlemaven-проект-для-гексагональной-архитектуры)

**Интеграция с DDD**
- [Q23. (!) Как гексагональная архитектура сочетается с DDD?](#q23--как-гексагональная-архитектура-сочетается-с-ddd)
- [Q24. Где в гексагоне живут агрегаты, Value Objects и доменные сервисы?](#q24-где-в-гексагоне-живут-агрегаты-value-objects-и-доменные-сервисы)
- [Q25. В чём разница между Application Service и Domain Service?](#q25-в-чём-разница-между-application-service-и-domain-service)
- [Q26. Как обрабатывать доменные события через Driven Port?](#q26-как-обрабатывать-доменные-события-через-driven-port)

**Тестирование**
- [Q27. (!) Какая стратегия тестирования рекомендуется для гексагональной архитектуры?](#q27--какая-стратегия-тестирования-рекомендуется-для-гексагональной-архитектуры)
- [Q28. (!) Как тестировать Use Case без инфраструктуры?](#q28--как-тестировать-use-case-без-инфраструктуры)
- [Q29. Как тестировать адаптеры (контроллеры и репозитории)?](#q29-как-тестировать-адаптеры-контроллеры-и-репозитории)
- [Q30. Что такое port-level / contract testing?](#q30-что-такое-port-level--contract-testing)
- [Q31. Как использовать fake-реализации портов вместо моков?](#q31-как-использовать-fake-реализации-портов-вместо-моков)

**Hexagonal vs Clean vs Onion**
- [Q32. (!) Чем Hexagonal отличается от Clean Architecture?](#q32--чем-hexagonal-отличается-от-clean-architecture)
- [Q33. Чем Hexagonal отличается от Onion Architecture?](#q33-чем-hexagonal-отличается-от-onion-architecture)
- [Q34. Сравнительная таблица: Hexagonal, Clean, Onion, Layered](#q34-сравнительная-таблица-hexagonal-clean-onion-layered)

**Миграция со слоистой архитектуры**
- [Q35. (!) Как эволюционно мигрировать из традиционной слоистой архитектуры?](#q35--как-эволюционно-мигрировать-из-традиционной-слоистой-архитектуры)
- [Q36. Как разделить "жирный" Service-класс на Use Cases?](#q36-как-разделить-жирный-service-класс-на-use-cases)
- [Q37. Как отделить JPA-сущность от доменной модели при миграции?](#q37-как-отделить-jpa-сущность-от-доменной-модели-при-миграции)

**Реальные ловушки и антипаттерны**
- [Q38. (!) Какие антипаттерны чаще всего встречаются в гексагональной архитектуре?](#q38--какие-антипаттерны-чаще-всего-встречаются-в-гексагональной-архитектуре)
- [Q39. "Анемичная" доменная модель в гексагоне -- как избежать?](#q39-анемичная-доменная-модель-в-гексагоне----как-избежать)
- [Q40. Утечка доменной модели через адаптер -- как это происходит и как предотвратить?](#q40-утечка-доменной-модели-через-адаптер----как-это-происходит-и-как-предотвратить)
- [Q41. Когда Hexagonal Architecture -- это overkill?](#q41-когда-hexagonal-architecture----это-overkill)
- [Q42. Как проверять правила гексагональной архитектуры через ArchUnit?](#q42-как-проверять-правила-гексагональной-архитектуры-через-archunit)

**Продвинутые темы**
- [Q43. (!) Как применять гексагональную архитектуру в микросервисах?](#q43--как-применять-гексагональную-архитектуру-в-микросервисах)
- [Q44. Как организовать обработку ошибок между доменом и адаптером?](#q44-как-организовать-обработку-ошибок-между-доменом-и-адаптером)
- [Q45. Реактивность (WebFlux, R2DBC) и гексагональная архитектура: что меняется?](#q45-реактивность-webflux-r2dbc-и-гексагональная-архитектура-что-меняется)

---

## Q1. (!) Что такое Hexagonal Architecture и какую проблему она решает?

**Hexagonal Architecture** (она же `Ports & Adapters`) -- архитектурный паттерн, предложенный Алистером Кокбёрном в 2005 году. Главная идея:

> Позволить приложению одинаково управляться пользователями, программами, автоматизированными тестами или batch-скриптами, и разрабатываться и тестироваться изолированно от конкретных устройств времени выполнения и баз данных.

Приложение изолируется от внешнего мира через **порты** (абстрактные интерфейсы) и **адаптеры** (конкретные реализации). Внешние технологии (`Spring MVC`, `JPA`, `Kafka`, CLI) превращаются в "подключаемые модули" вокруг ядра.

Проблемы, которые решает паттерн:

- **Сильная связанность** бизнес-логики с фреймворком (типичная ситуация -- `@Transactional` и SQL-запросы размазаны по сервисам)
- **Сложность тестирования** -- для unit-теста нужна БД, HTTP-сервер, очередь
- **Невозможность заменить инфраструктуру** без переписывания домена (сменить `PostgreSQL` на `MongoDB`, `REST` на `gRPC`)
- **Смешение ответственностей** -- контроллер содержит бизнес-правила, домен знает про HTTP

```mermaid
graph LR
    subgraph "Внешний мир"
        HTTP[HTTP Client]
        CLI[CLI / Batch]
        MQ[Kafka]
        DB[(Database)]
        EXT[External API]
    end
    subgraph "Адаптеры"
        WA[Web Adapter]
        CA[CLI Adapter]
        MA[Kafka Listener]
        PA[JPA Adapter]
        EA[HTTP Client]
    end
    subgraph "Application Core"
        IP[Driving Ports]
        APP[Use Cases]
        DOM[Domain Model]
        OP[Driven Ports]
    end
    HTTP --> WA --> IP
    CLI --> CA --> IP
    MQ --> MA --> IP
    IP --> APP --> DOM
    APP --> OP
    OP --> PA --> DB
    OP --> EA --> EXT
    style DOM fill:#2d5016,color:#fff
    style APP fill:#4a7c2e,color:#fff
    style IP fill:#6ba34a,color:#fff
    style OP fill:#6ba34a,color:#fff
```

> На собеседовании важно подчеркнуть: `Hexagonal Architecture` -- это не про количество слоёв, а про **направление зависимостей**. Все стрелки внутри -- в сторону ядра. Ядро ничего не знает о фреймворках.


> [!mcq]
> - [ ] `Hexagonal Architecture` — это `N-tier` слои `Controller → Service → Repository` с `Dependency Injection` через `@Autowired` | Слоистая архитектура: домен зависит от persistence снизу вверх, нет инверсии. ❌ ПОСЛЕДСТВИЕ: смена `PostgreSQL` на `MongoDB` требует переписывать `@Entity` и `JpaRepository` во всех use-case, миграция растягивается на месяцы.
> - [ ] `Hexagonal Architecture` инвертирует зависимости через `ports` (интерфейсы в ядре) и `adapters` (реализации снаружи), фреймворки становятся подключаемыми модулями | Ядро не знает о `Spring`/`JPA`/`Kafka`, общается с миром через интерфейсы, которые реализуют адаптеры. ✓ ПРИМЕНЯТЬ: банковский pet-clip от Хомбергса (`bookkeeper`), AWS Prescriptive Guidance рекомендует для serverless. 📋 ПРАВИЛО: «ядро в центре, фреймворки на орбите». 🔗 См. Q4, Q6, Q32.
> - [ ] `Hexagonal Architecture` — это паттерн микросервисов, где каждый сервис обязан быть шестиугольником с шестью входами | Это путаница уровней: `Hexagonal` — внутренняя структура, микросервисы — топология. ❌ ПОСЛЕДСТВИЕ: команда строит «6 микросервисов на гексагон», получает distributed monolith с 30+ HTTP-вызовами на бизнес-операцию.
> - [ ] `Hexagonal Architecture` обязывает использовать `event sourcing` и `CQRS` для разделения чтения и записи | Это разные паттерны: гексагон ничего не говорит про event sourcing, можно применять обычное `CRUD`-сохранение. ❌ ПОСЛЕДСТВИЕ: на простом сервисе тащат `Axon Framework` + Kafka — операционная сложность ради «правильности», команда тонет в eventual consistency-багах.

## Q2. (!) Почему именно шестиугольник? В чём смысл такой визуализации?

Кокбёрн выбрал шестиугольник **осознанно**, чтобы отойти от картинок "слоёв" (layer cake), которые ассоциируются с сильным направлением сверху вниз. Ключевые причины:

1. **Симметрия** -- хексагон показывает, что все стороны равнозначны. У шестиугольника **нет "верха" и "низа"**. Это подчёркивает равноправие HTTP-, CLI- и MQ-входов.
2. **Несколько граней = несколько портов** -- у шестиугольника есть "место" для нескольких портов и нескольких адаптеров на каждой грани.
3. **Рисование на доске** -- шестиугольник удобно рисовать руками, оставляя место для адаптеров снаружи каждой грани.
4. **Не ассоциируется со слоёной архитектурой** -- и поэтому не провоцирует "N-Tier" мышление.

Само число "6" **не имеет магического значения** -- можно было бы пятиугольник или восьмиугольник. Кокбёрн прямо это отмечает.

> Частая ошибка на собеседовании -- думать, что "каждая грань = один порт". На самом деле портов может быть больше или меньше шести. Шестиугольник -- это метафора, а не структурное ограничение.


> [!mcq]
> - [ ] Кокбёрн взял шестиугольник, потому что у `OSI`-модели ровно 6 уровней над физическим | Это выдуманное обоснование: `OSI` к гексагону отношения не имеет, в `OSI` 7 уровней. ❌ ПОСЛЕДСТВИЕ: ответ на собеседовании выглядит как заученная мифология, интервьюер ставит «не разбирается в первоисточнике».
> - [ ] Каждая из 6 граней соответствует обязательной паре `port + adapter`: REST, CLI, DB, MQ, Cache, Logger | Ложная структурная привязка: 6 — не магическое число, портов может быть больше или меньше. ❌ ПОСЛЕДСТВИЕ: команда искусственно дробит порты «чтобы получилось 6», порождая `LoggerPort`, `MetricsPort` ради числа, а не доменного смысла.
> - [ ] Шестиугольник — визуальная метафора, выбранная Кокбёрном против ассоциации со «слоями» (`layer cake`); число граней не имеет структурного значения | Симметричная фигура без верха и низа подчёркивает равноправие driving/driven сторон. ✓ ПРИМЕНЯТЬ: оригинальная статья `alistair.cockburn.us/hexagonal-architecture/`, Reflectoring и AWS guidance. 📋 ПРАВИЛО: «гексагон — про симметрию, не про число 6». 🔗 См. Q3, Q5.
> - [ ] Шестиугольник нужен, чтобы в `UML`-диаграммах визуально отделить `Aggregate Root` от `Value Objects` | Нет связи с `UML` или DDD-нотацией — гексагон относится к границам приложения, а не к доменным сущностям. ❌ ПОСЛЕДСТВИЕ: на code review архитектор путает диаграмму гексагона с диаграммой агрегатов, доменная модель и ports/adapters смешиваются.

## Q3. Какая история и авторство у паттерна Ports & Adapters?

- **2005 год** -- Алистер Кокбёрн (один из авторов "Agile Manifesto") публикует статью "Hexagonal Architecture" на своём блоге.
- **Оригинальное название** -- `Ports & Adapters` (порты и адаптеры). Сам Кокбёрн предпочитает его, считая "hexagonal" визуальной метафорой.
- **2008 год** -- Джеффри Палермо вводит `Onion Architecture`, концептуально близкую идею.
- **2012 год** -- Роберт Мартин публикует `Clean Architecture`, которая обобщает Hexagonal и Onion.
- **2019 год** -- Том Хомбергс выпускает книгу "Get Your Hands Dirty on Clean Architecture" с детальной реализацией гексагональной архитектуры в `Spring Boot` (фактически каноническое руководство).

Цитата из оригинальной статьи Кокбёрна:

> Создайте приложение так, чтобы оно работало одинаково и под управлением пользователей, программ, автоматизированных тестов или batch-скриптов, и чтобы его можно было разрабатывать и тестировать в изоляции от его финальных устройств времени выполнения и баз данных.

Это была прямая реакция на то, что Кокбёрн называл "ошибками слоистой архитектуры" -- когда бизнес-логика утекала в UI и в persistence.


> [!mcq]
> - [ ] `Hexagonal Architecture` придумал Robert C. Martin в 2012 году вместе с `Clean Architecture` | Перепутаны авторы и хронология: Кокбёрн опубликовал паттерн в 2005, Мартин — `Clean Architecture` в 2012, обобщив гексагон и onion. ❌ ПОСЛЕДСТВИЕ: ответ на собеседовании выдаёт незнание первоисточников, дальнейшие вопросы про различия Hexagonal/Clean провалятся.
> - [ ] Паттерн опубликовал Алистер Кокбёрн в 2005 под именем `Ports & Adapters`; «Hexagonal» — визуальная метафора | Каноничный гайд по реализации — `Get Your Hands Dirty on Clean Architecture` Тома Хомбергса (2019). ✓ ПРИМЕНЯТЬ: статья Кокбёрна `alistair.cockburn.us/hexagonal-architecture/`, книга Хомбергса как реализация в Spring Boot. 📋 ПРАВИЛО: «Cockburn 2005 → Palermo 2008 → Martin 2012». 🔗 См. Q1, Q32, Q33.
> - [ ] Это паттерн, выросший из `Spring Framework` после релиза `Spring Boot 2` в 2018 году | Гексагон существенно старше Spring Boot и не связан с конкретным фреймворком — это языко- и фреймворконезависимый принцип. ❌ ПОСЛЕДСТВИЕ: команда считает гексагон «фичей Spring», переход на Quarkus/Micronaut воспринимается как невозможный.
> - [ ] `Ports & Adapters` придумали в Netflix для миграции с monolith на микросервисы в 2010-х | Подмена истории: Netflix продвигал `Hystrix`/`Eureka`, но не авторство гексагона. ❌ ПОСЛЕДСТВИЕ: ссылки на «Netflix-стиль» уводят обсуждение в circuit breakers, а не в инверсию зависимостей и ports.

## Q4. Что такое Application Core и что в него входит?

**Application Core** (ядро приложения, иногда называется `Hexagon`) -- это изолированная часть приложения, которая содержит всю бизнес-логику и **не зависит** ни от одного фреймворка.

В него входят:

1. **Domain Model** -- доменные сущности (`Entities`), Value Objects, агрегаты, доменные события, доменные сервисы
2. **Use Cases / Application Services** -- сценарии приложения, оркестрация доменной логики
3. **Driving Ports (Input Ports)** -- интерфейсы, описывающие, что приложение умеет делать
4. **Driven Ports (Output Ports)** -- интерфейсы, описывающие, что приложение ожидает от внешнего мира

**В ядре НЕ должно быть:**
- Аннотаций `Spring` (`@Service`, `@Component`, `@Autowired`) -- в строгом варианте
- Аннотаций `JPA`/`jakarta.persistence` (`@Entity`, `@Column`)
- Зависимостей на `spring-web`, `spring-data`, `jackson`, HTTP-клиенты
- Знания о форматах передачи данных (`JSON`, `XML`, `protobuf`)

```java
// Ядро: чистая Java
public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderLine> lines;

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException(id);
        }
        this.status = OrderStatus.SUBMITTED;
    }
}

public interface PlaceOrderUseCase {           // Driving Port
    OrderId placeOrder(PlaceOrderCommand cmd);
}

public interface OrderRepository {              // Driven Port
    Optional<Order> findById(OrderId id);
    void save(Order order);
}
```

> Практический компромисс: иногда допускается `@Transactional` и минимум Spring-аннотаций на реализациях Use Case, если не хочется дублировать Composition Root вручную. Важно, чтобы это был осознанный выбор, а не "случайно протекло".


> [!mcq]
> - [ ] `Application Core` = `@RestController` + `@Service` + `@Repository`, всё в одном пакете для простоты | Это слоистая архитектура с перевёрнутым названием: `@Repository` относится к адаптеру, контроллер — к driving-адаптеру, в ядре их быть не должно. ❌ ПОСЛЕДСТВИЕ: при попытке заменить REST на gRPC ядро тянет за собой `spring-web`, миграция превращается в переписывание всего модуля.
> - [ ] `Application Core` = `Domain Model` (агрегаты, VO, доменные сервисы) + `Use Cases` + `Driving Ports` + `Driven Ports`; без `Spring`/`JPA`/`Jackson`-аннотаций | Ядро описывает «что приложение умеет» (input ports) и «что ему нужно» (output ports) на чистой Java. ✓ ПРИМЕНЯТЬ: модуль `bookkeeper-application` у Хомбергса собирается без `spring-web` и `spring-data-jpa`. 📋 ПРАВИЛО: «ядро = домен + use-cases + ports, без фреймворков». 🔗 См. Q5, Q16, Q19.
> - [ ] `Application Core` — это только доменные сущности (`Order`, `Customer`), use-case'ы живут отдельно в слое инфраструктуры | Use-cases (Application Services) — обязательная часть ядра, они оркестрируют домен и порты. ❌ ПОСЛЕДСТВИЕ: бизнес-логика расползается по адаптерам, нельзя протестировать сценарий «оформление заказа» без `MockMvc` и `@SpringBootTest`.
> - [ ] `Application Core` обязан содержать `@Configuration` и `@Bean` для DI use-case'ов | DI-конфигурация (`Composition Root`) живёт во внешнем модуле, а не в ядре, иначе ядро тянет `spring-context`. ❌ ПОСЛЕДСТВИЕ: ядро невозможно использовать в desktop-приложении без Spring, теряется главное преимущество переносимости.

## Q5. Какие три основные зоны определяет гексагональная архитектура?

Вопреки распространённому заблуждению, в гексагональной архитектуре **нет концентрических "колец"** как в `Clean`/`Onion`. Есть три функциональные зоны:

| Зона | Что содержит | Пример в Spring |
|------|--------------|-----------------|
| **Application Core** (внутри гексагона) | Domain, Use Cases, Ports | `domain/`, `application/` |
| **Driving side** (слева от гексагона) | Primary Adapters | `adapter/in/web`, `adapter/in/cli` |
| **Driven side** (справа от гексагона) | Secondary Adapters | `adapter/out/persistence`, `adapter/out/messaging` |

```mermaid
graph LR
    subgraph "Driving side"
        WEB[Web Adapter]
        CLI[CLI Adapter]
        TEST[Test Driver]
    end
    subgraph "Application Core"
        CORE["Domain + Use Cases + Ports"]
    end
    subgraph "Driven side"
        JPA[JPA Adapter]
        KAFKA[Kafka Producer]
        HTTP[HTTP Client]
    end
    WEB --> CORE
    CLI --> CORE
    TEST --> CORE
    CORE --> JPA
    CORE --> KAFKA
    CORE --> HTTP
```

Такое разделение сразу даёт **понимание кто кого вызывает**: driving-адаптеры вызывают ядро (инициируют действие), driven-адаптеры вызываются ядром (обслуживают его потребности).


> [!mcq]
> - [ ] Три концентрических кольца: `Entities` → `Use Cases` → `Frameworks & Drivers`, как в Clean Architecture | Это терминология Мартина из `Clean Architecture`, гексагон не использует кольца — у него линейные стороны. ❌ ПОСЛЕДСТВИЕ: команда смешивает паттерны, на ревью спор «у нас Hexagonal или Clean» без единого ответа, документация противоречива.
> - [ ] Driving side (primary adapters слева) → `Application Core` (центр) → Driven side (secondary adapters справа) | Симметричное разделение: driving вызывает ядро, driven вызывается ядром через output ports. ✓ ПРИМЕНЯТЬ: каноническая структура у Хомбергса — `adapter/in/web` слева, `adapter/out/persistence` справа. 📋 ПРАВИЛО: «driving вызывает ядро, ядро вызывает driven». 🔗 См. Q7, Q11, Q12.
> - [ ] `Presentation`, `Business`, `Data Access` — три классических слоя enterprise-приложения | Это `N-tier` слои с зависимостью «сверху вниз», в гексагоне другие границы — ядро vs адаптеры, направление инвертировано. ❌ ПОСЛЕДСТВИЕ: команда называет проект «гексагоном», но `BusinessLayer` зависит от `DataAccess` через JPA-сущности — первоначальная цель изоляции потеряна.
> - [ ] `Frontend`, `Backend`, `Database` — три уровня deployment | Это уровни инфраструктуры/деплоя, а не зоны архитектурного паттерна; гексагон описывает структуру внутри backend, а не разворачивание. ❌ ПОСЛЕДСТВИЕ: на дизайн-сессии обсуждение ядра и адаптеров подменяется разговорами про Kubernetes-namespace, архитектурное решение остаётся непринятым.

## Q6. (!) Что такое Port и чем он отличается от обычного интерфейса?

**Port** -- это интерфейс, определяющий **осмысленный диалог** (purposeful conversation) между ядром приложения и внешним миром.

Отличия от "обычного Java-интерфейса":

1. **Port выражает намерение домена**, а не технические детали. `LoadAccountPort` -- это порт, `AccountDao` -- это не порт, а просто технический интерфейс.
2. **Port определяется в ядре**, а не в слое инфраструктуры. Направление зависимости -- от адаптера к порту.
3. **Port принадлежит языку домена** (`Ubiquitous Language`). Названия методов -- доменные глаголы (`loadAccount`, `sendMoney`), а не технические (`select`, `execute`).
4. **Port имеет семантику**, а не только сигнатуру. Даже если два интерфейса имеют одинаковые методы, это разные порты, если они решают разные задачи.

```java
// Это Port: описывает доменную потребность
public interface LoadAccountPort {
    Account loadAccount(AccountId id, LocalDateTime baselineDate);
}

// А это не Port, а инфраструктурный интерфейс
public interface AccountDao extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByNumber(String number);
}
```

Принципиально: `LoadAccountPort` возвращает **доменную модель `Account`**, а `AccountDao` -- `AccountEntity` (JPA-сущность). Адаптер, реализующий порт, прячет это преобразование.


> [!mcq]
> - [ ] `Port` = любой Java `interface` в проекте, включая `JpaRepository<Account, Long>` | Технический интерфейс инфраструктуры (DAO) — не порт; порт описывает доменную потребность, а не механизм доступа к БД. ❌ ПОСЛЕДСТВИЕ: команда объявляет `JpaRepository` «портом», в use-case протекают `Pageable`, `Specification` — ядро узнаёт про Spring Data.
> - [ ] `Port` = интерфейс, выражающий доменное намерение (`LoadAccountPort`, `SendMoneyUseCase`); определяется в ядре, возвращает доменные типы | Семантика > сигнатуры: два одинаковых по методам интерфейса — разные порты, если решают разные задачи. ✓ ПРИМЕНЯТЬ: `LoadAccountPort` у Хомбергса возвращает `Account`, а не `AccountJpaEntity`. 📋 ПРАВИЛО: «port — это purposeful conversation, а не CRUD». 🔗 См. Q7, Q8, Q9.
> - [ ] `Port` = аннотация `@Port` в Spring, наносится на `@RestController` для маркировки HTTP-входов | Такой аннотации в Spring нет, гексагональные порты — обычные Java-интерфейсы без специальной разметки. ❌ ПОСЛЕДСТВИЕ: разработчик ищет `@Port` в `spring-boot-starter-web`, не находит, уверен что «гексагон неприменим в Spring», команда отказывается от паттерна.
> - [ ] `Port` = абстрактный класс, потому что только так можно гарантировать поведение без реализации | Порт всегда `interface`: абстрактный класс ограничивает множественную реализацию и связывает иерархию, что противоречит идее независимых адаптеров. ❌ ПОСЛЕДСТВИЕ: один адаптер не может реализовать два порта, команда плодит wrapper-классы вместо чистой композиции.

## Q7. (!) В чём разница между Driving (Input) и Driven (Output) портами?

Самое важное различие в гексагональной архитектуре:

| Характеристика | Driving Port (Input) | Driven Port (Output) |
|----------------|----------------------|----------------------|
| **Синонимы** | Primary Port, API Port | Secondary Port, SPI Port |
| **Направление** | Внешний мир → Ядро | Ядро → Внешний мир |
| **Кто вызывает** | Primary Adapter (Controller) | Use Case / Application Service |
| **Кто реализует** | Use Case / Application Service | Secondary Adapter |
| **Метафора** | "Что приложение умеет делать" | "Что приложение требует от мира" |
| **Пример** | `PlaceOrderUseCase`, `SendMoneyUseCase` | `OrderRepository`, `PaymentGateway`, `EmailSender` |

```mermaid
graph LR
    PA[Primary Adapter<br/>REST Controller] -->|вызывает| DP[Driving Port<br/>interface]
    UC[Use Case<br/>реализация] -.->|implements| DP
    UC -->|вызывает| DNP[Driven Port<br/>interface]
    SA[Secondary Adapter<br/>JPA Repo] -.->|implements| DNP
    style DP fill:#4a7c2e,color:#fff
    style DNP fill:#6ba34a,color:#fff
```

**Практическое правило**: если интерфейс реализуется в **ядре** -- это Driving Port; если в **адаптере** -- это Driven Port.

```java
// Driving Port: реализация -- в ядре (в application слое)
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}

// Driven Port: реализация -- в адаптере (adapter/out/persistence)
public interface LoadAccountPort {
    Account loadAccount(AccountId id);
}
```


> [!mcq]
> - [ ] Driving port = REST-контроллер, driven port = JPA-репозиторий — порт совпадает с адаптером | Подмена понятий: `@RestController` — это primary adapter (driving adapter), он вызывает driving port, а не является им. Контроллер знает про HTTP, port — нет. ❌ ПОСЛЕДСТВИЕ: контроллер начинает реализовывать «port» — `@RequestMapping` тащится в ядро, сменить REST на Kafka-listener невозможно без переписывания use-case.
> - [ ] Driving port реализуется в ядре (use-case), driven port — в адаптере; driving вызывается primary-адаптером, driven — самим use-case'ом | Практическое правило: «кто реализует — где живёт реализация»: ядро или адаптер. ✓ ПРИМЕНЯТЬ: `SendMoneyUseCase` (driving) реализует `SendMoneyService` в ядре; `LoadAccountPort` (driven) реализует `AccountPersistenceAdapter` в `adapter/out`. 📋 ПРАВИЛО: «driving = что приложение умеет, driven = что приложение требует». 🔗 См. Q11, Q12, Q21.
> - [ ] Driving и driven — синонимы, разделение придумали для красоты диаграмм | Это фундаментальное разделение по направлению вызова: input vs output, путать их — ломать архитектуру. ❌ ПОСЛЕДСТВИЕ: команда без понимания направления делает «двунаправленные» порты, в use-case проникают `RestTemplate` и `KafkaTemplate` напрямую.
> - [ ] Driving port — синхронный, driven port — асинхронный по определению | Синхронность определяется типом возврата (`Mono`, `CompletableFuture`), а не классификацией порта; и driving, и driven могут быть любого вида. ❌ ПОСЛЕДСТВИЕ: команда блокирует синхронные порты в реактивном WebFlux-приложении, latency p99 поднимается с 50ms до 5s под нагрузкой.

## Q8. Как правильно именовать порты?

Именование портов -- одна из деталей, на которой проваливаются на собеседовании. Рекомендации:

**Driving Ports** -- именуются по сценарию использования:
- `PlaceOrderUseCase`, `CancelOrderUseCase`, `SendMoneyUseCase`
- Суффикс `UseCase` или `Service` (реже)
- **НЕ** `OrderController` -- это адаптер, а не порт

**Driven Ports** -- именуются по семантике действия:
- `LoadAccountPort`, `UpdateAccountStatePort`, `SendEmailPort`
- Суффикс `Port` либо доменный термин-репозиторий (`OrderRepository`)
- **НЕ** `OrderDao`, `OrderMapper` -- это технические названия
- **НЕ** `AccountRepositoryImpl` -- это адаптер

```java
// Хорошо
public interface LoadAccountPort { ... }
public interface SendMoneyUseCase { ... }
public interface OrderRepository { ... }      // DDD-style допустим

// Плохо
public interface IAccountService { ... }      // Hungarian notation
public interface AccountDAO { ... }           // технический термин
public interface OrderServiceInterface { ... } // избыточный суффикс
```

Совет от Тома Хомбергса: **один порт -- один метод** (Interface Segregation Principle). Крупные интерфейсы типа `AccountRepository` с 20 методами провоцируют зависимости, которые реально не нужны Use Case-у.


> [!mcq]
> - [ ] `IAccountService`, `AccountServiceInterface` — стандарт для портов в Java | Hungarian notation и суффикс `Interface` не несут информации о роли (driving vs driven, доменная задача); это стиль .NET 2000-х. ❌ ПОСЛЕДСТВИЕ: нейминг скрывает направление зависимости, на ревью невозможно понять «это input или output», ArchUnit-правила по имени не работают.
> - [ ] Driving — `*UseCase` (`SendMoneyUseCase`); driven — `*Port` или DDD-`*Repository` (`LoadAccountPort`, `OrderRepository`); технические `*Dao`/`*Manager` запрещены | Имя несёт намерение (`PlaceOrder`) и роль (use-case/port), без префиксов и технических терминов. ✓ ПРИМЕНЯТЬ: у Хомбергса все driven-порты — `*Port`, driving — `*UseCase`. 📋 ПРАВИЛО: «UseCase для входа, Port для выхода». 🔗 См. Q6, Q9.
> - [ ] Один общий интерфейс `BusinessLogic` со всеми методами проекта — проще навигация | Это противоположность ISP: один gigantic-interface связывает всех со всеми, тестирование через моки превращается в кошмар. ❌ ПОСЛЕДСТВИЕ: каждый use-case тянет в зависимости методы, которые ему не нужны, моки в тестах содержат 30+ `when().thenReturn()` для одного теста.
> - [ ] Имя должно совпадать с именем JPA-сущности (`AccountEntityPort`, `OrderEntityRepository`) | Имена ядра не должны отражать инфраструктурные сущности — это ломает изоляцию домена от persistence. ❌ ПОСЛЕДСТВИЕ: при смене persistence на NoSQL приходится переименовывать порты в `AccountDocumentPort`, в коде остаются ссылки на старое имя в логах и метриках.

## Q9. Сколько портов должно быть у приложения? Гранулярность портов

Две крайности:

**Крупнозернистые порты** (coarse-grained):
```java
public interface AccountRepository {
    Account findById(AccountId id);
    void save(Account account);
    List<Account> findAll();
    void delete(AccountId id);
    List<Account> findByCustomer(CustomerId customerId);
}
```
Плюсы: меньше интерфейсов. Минусы: любой Use Case зависит от **всего** репозитория, сложнее тестировать, нарушается ISP.

**Мелкозернистые порты** (fine-grained, "ISP-style"):
```java
public interface LoadAccountPort {
    Account loadAccount(AccountId id);
}
public interface SaveAccountPort {
    void save(Account account);
}
public interface DeleteAccountPort {
    void delete(AccountId id);
}
```
Плюсы: Use Case зависит только от того, что реально использует; легче подменять в тестах. Минусы: больше классов.

**Практический компромисс** (подход Хомбергса):
- Для CRUD-операций -- один порт `OrderRepository` в DDD-стиле
- Для специализированных Use Cases -- отдельные порты (`SendMoneyPort`)
- Руководствоваться `Interface Segregation Principle`: **Use Case не должен видеть методы, которыми не пользуется**

```java
// Use Case зависит только от того, что нужно
public class SendMoneyService implements SendMoneyUseCase {
    private final LoadAccountPort loadAccount;       // только load
    private final UpdateAccountStatePort updateState; // только update
    // НЕ зависит от delete, findAll и т.д.
}
```


> [!mcq]
> - [ ] Идеально — один gigantic-port `AccountRepository` с 20 методами `save/find/delete/findAll/...` | Нарушение ISP: use-case зависит от методов, которые не использует, тесты вынуждены мокать всё. ❌ ПОСЛЕДСТВИЕ: `SendMoneyService` тянет зависимость на `delete()` и `findAll()` — добавление нового метода ломает 30 тестов с моками.
> - [ ] Гранулярность определяется ISP: use-case должен видеть только методы, которые реально использует; CRUD-`Repository` допустим, специализированные действия — отдельные порты | Прагматичный компромисс Хомбергса: `OrderRepository` для CRUD + `SendMoneyPort` для специфики. ✓ ПРИМЕНЯТЬ: `SendMoneyService` зависит от `LoadAccountPort` + `UpdateAccountStatePort`, а не от monolithic-`AccountRepository`. 📋 ПРАВИЛО: «один use-case видит только нужные ему методы». 🔗 См. Q8, Q10.
> - [ ] Должен быть ровно один порт на каждый Bounded Context | Привязка количества портов к BC искусственна: один контекст содержит десятки use-case'ов, каждый со своими портами. ❌ ПОСЛЕДСТВИЕ: команда втискивает 25 методов в `OrderContextPort`, новый use-case добавляет метод в общий интерфейс — ломаются все реализации.
> - [ ] Правило: ровно 6 портов на гексагон, по числу граней | Структурное прокрустово ложе: 6 — визуальная метафора, портов может быть больше или меньше. ❌ ПОСЛЕДСТВИЕ: команда искусственно объединяет несвязанные методы в порты «чтобы получилось 6», нарушая ISP и SRP.

## Q10. Может ли один адаптер реализовывать несколько портов?

**Да, это нормальная практика** -- особенно для secondary-адаптеров.

```java
@Component
class AccountPersistenceAdapter implements
        LoadAccountPort,
        UpdateAccountStatePort {

    private final AccountJpaRepository jpaRepo;
    private final ActivityJpaRepository activityRepo;
    private final AccountMapper mapper;

    @Override
    public Account loadAccount(AccountId id, LocalDateTime since) {
        var entity = jpaRepo.findById(id.value()).orElseThrow();
        var activities = activityRepo.findByOwnerSince(id.value(), since);
        return mapper.toDomain(entity, activities);
    }

    @Override
    public void updateActivities(Account account) {
        account.getActivityWindow().getActivities().stream()
            .filter(a -> a.getId() == null)  // только новые
            .forEach(a -> activityRepo.save(mapper.toActivityEntity(a)));
    }
}
```

Преимущества:
- Одна реализация = одна таблица/агрегат -- меньше дублирования маппинга
- Транзакционные границы в пределах одного адаптера
- Проще управлять зависимостями (один `@Component` на агрегат)

Когда **не стоит** объединять:
- Если порты относятся к **разным bounded contexts**
- Если один порт требует HTTP-клиент, а другой -- JPA (разные технологии)
- Если реализации имеют **разный жизненный цикл** (скажем, один кешируется, другой нет)


> [!mcq]
> - [ ] Один адаптер = один порт, всегда; 1-к-1 — жёсткое правило | Чрезмерное ограничение: `AccountPersistenceAdapter` нормально реализует `LoadAccountPort` + `UpdateAccountStatePort` для одного агрегата. ❌ ПОСЛЕДСТВИЕ: дублирование маппинга и JPA-зависимостей в двух классах ради «правила», транзакционные границы рассыпаются между адаптерами.
> - [ ] Один адаптер может реализовать несколько портов одного агрегата (driven) или одного транспорта; разделять — если контексты или технологии разные | Объединение даёт единый mapper и транзакционную границу, разделение — когда смешиваются JPA + HTTP. ✓ ПРИМЕНЯТЬ: `AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort` у Хомбергса. 📋 ПРАВИЛО: «один агрегат → один driven-адаптер». 🔗 См. Q9, Q15.
> - [ ] Primary и secondary можно объединить в один класс — `OrderKafkaAdapter` слушает + публикует | Нарушение SRP: listener и publisher имеют разные жизненные циклы и тестируются по-разному. ❌ ПОСЛЕДСТВИЕ: при отказе consumer-логики класс целиком перезапускается и теряет publisher-метрики, сложно изолировать ошибки в логах.
> - [ ] Адаптер обязан реализовывать ровно один порт, иначе нарушается Liskov Substitution | LSP про подтипы, а не про количество интерфейсов; класс может реализовать N интерфейсов без нарушения LSP. ❌ ПОСЛЕДСТВИЕ: команда дробит адаптеры на классы-обёртки `LoadAccountAdapter` + `UpdateAccountAdapter` — два экземпляра делят `EntityManager`, ломают transaction boundary.

## Q11. (!) Что такое Primary (Driving) Adapter и какие у него обязанности?

**Primary Adapter** (Driving Adapter) -- адаптер, который **инициирует** работу ядра. Он преобразует внешний вызов (HTTP-запрос, CLI-команду, сообщение из Kafka) в вызов Driving Port.

Обязанности:

1. **Принять запрос** в формате внешнего мира (HTTP, JSON, CLI args)
2. **Провалидировать вход** на синтаксическом уровне (правильный формат JSON, обязательные поля)
3. **Преобразовать в Command/Query** -- объект ядра
4. **Вызвать Driving Port**
5. **Преобразовать результат** в формат клиента (JSON, HTTP response)
6. **Обработать доменные исключения** и превратить в HTTP-статусы

```java
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping("/send/{sourceId}/{targetId}/{amount}")
    public ResponseEntity<Void> sendMoney(
            @PathVariable long sourceId,
            @PathVariable long targetId,
            @PathVariable long amount) {

        // 1. Валидация и преобразование во входной объект ядра
        var command = new SendMoneyCommand(
            new AccountId(sourceId),
            new AccountId(targetId),
            Money.of(amount));

        // 2. Вызов Driving Port
        boolean success = sendMoneyUseCase.sendMoney(command);

        // 3. Преобразование результата
        return success
            ? ResponseEntity.ok().build()
            : ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDto> handle(InsufficientFundsException e) {
        return ResponseEntity.unprocessableEntity()
            .body(new ErrorDto("INSUFFICIENT_FUNDS", e.getMessage()));
    }
}
```

**Не обязанности** Primary Adapter:
- **Не** содержит бизнес-логики
- **Не** обращается напрямую к репозиторию (это сломает архитектуру)
- **Не** решает "можно ли выполнить операцию" -- это делает ядро


> [!mcq]
> - [ ] Primary Adapter содержит бизнес-правила: проверяет лимиты, считает скидки, формирует доменные исключения | Это утечка логики из ядра в адаптер: правила должны быть в use-case или агрегате. ❌ ПОСЛЕДСТВИЕ: одно правило размазано между REST-контроллером, Kafka-listener и CLI-командой — при изменении надо чинить три места, баги расходятся между каналами входа.
> - [ ] Primary Adapter принимает внешний вызов (HTTP/CLI/MQ), валидирует синтаксис, маппит DTO → Command, вызывает driving port, маппит результат обратно; бизнес-логика отсутствует | Шесть обязанностей: приём, синтаксическая валидация, преобразование, вызов порта, преобразование ответа, обработка доменных исключений. ✓ ПРИМЕНЯТЬ: `SendMoneyController` у Хомбергса делает `@PathVariable` → `SendMoneyCommand` → `useCase.sendMoney()`. 📋 ПРАВИЛО: «primary-адаптер — переводчик с транспорта на домен». 🔗 См. Q12, Q14, Q44.
> - [ ] Primary Adapter обязан напрямую обращаться к репозиторию для оптимизации (минус один hop через use-case) | Обход use-case ломает архитектуру: транзакции, валидация, события минуют ядро. ❌ ПОСЛЕДСТВИЕ: контроллер делает `repo.findAll()` без `@Transactional`, в production читает данные без consistent snapshot, race conditions при параллельных update.
> - [ ] Primary Adapter — это `@Configuration`-класс, который собирает зависимости в Composition Root | Это другая роль (Composition Root), а primary-адаптер преобразует внешний вызов в вызов порта. ❌ ПОСЛЕДСТВИЕ: путаница ролей: `@Configuration` пытается обрабатывать HTTP-запросы через `@Bean`-методы, Spring не понимает такую конструкцию.

## Q12. (!) Что такое Secondary (Driven) Adapter и какие у него обязанности?

**Secondary Adapter** (Driven Adapter) -- адаптер, который **реализует** Driven Port, предоставляя ядру инфраструктурную функциональность: БД, внешний API, шина сообщений.

Обязанности:

1. **Реализовать контракт Driven Port**
2. **Преобразовать доменную модель в технический формат** (JPA entity, JSON payload, protobuf)
3. **Общаться с внешней системой** (выполнить SQL, отправить HTTP-запрос, опубликовать в Kafka)
4. **Преобразовать результат обратно** в доменную модель
5. **Обработать технические ошибки** и преобразовать в доменные исключения (если нужно)

```java
@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort,
                                                 UpdateAccountStatePort {

    private final AccountJpaRepository accountRepo;
    private final ActivityJpaRepository activityRepo;
    private final AccountMapper mapper;

    @Override
    public Account loadAccount(AccountId id, LocalDateTime baseline) {
        // 1. Запрос к БД через JPA
        AccountJpaEntity entity = accountRepo.findById(id.value())
            .orElseThrow(() -> new AccountNotFoundException(id));

        List<ActivityJpaEntity> activities = activityRepo
            .findByOwnerSince(id.value(), baseline);

        // 2. Преобразование в доменную модель
        return mapper.toDomain(entity, activities, baseline);
    }

    @Override
    public void updateActivities(Account account) {
        // 3. Преобразование из доменной модели
        account.getActivityWindow().getActivities().stream()
            .filter(a -> a.getId() == null)
            .forEach(a -> activityRepo.save(
                mapper.toActivityEntity(a, account.getId())));
    }
}
```

Ключевой момент: **тип возврата метода порта -- доменная модель**, не JPA-entity. Это гарантирует, что домен не узнает про `jakarta.persistence`.


> [!mcq]
> - [ ] Secondary Adapter возвращает наружу JPA-сущность (`AccountJpaEntity`) — экономия на маппере | Утечка persistence в use-case: ядро узнаёт про `@Entity`, lazy loading, `EntityManager`. ❌ ПОСЛЕДСТВИЕ: use-case ломается за пределами `@Transactional` сессии (`LazyInitializationException`), миграция на NoSQL невозможна без переписывания.
> - [ ] Secondary Adapter реализует driven port, маппит доменную модель в технический формат (JPA/HTTP/protobuf), общается с внешней системой, маппит ответ обратно в домен; технические ошибки переводит в доменные | Ключевое: тип возврата метода порта — доменная модель, mapper живёт в адаптере. ✓ ПРИМЕНЯТЬ: `AccountPersistenceAdapter` ловит `DataAccessException` и бросает `AccountNotFoundException`. 📋 ПРАВИЛО: «secondary-адаптер прячет технологию за доменным контрактом». 🔗 См. Q11, Q14, Q44.
> - [ ] Secondary Adapter сам определяет интерфейс, ядро адаптируется под него | Перевёрнутая зависимость: контракт определяет ядро (driven port), адаптер реализует — иначе теряется инверсия. ❌ ПОСЛЕДСТВИЕ: смена `Stripe` на `Adyen` требует менять use-case, потому что use-case зависит от `StripeClient` напрямую — изоляция домена сломана.
> - [ ] Secondary Adapter обязан использовать `Spring Data JPA` — иначе это не гексагон | Гексагон не диктует технологию: адаптер может быть на JDBC, R2DBC, Cassandra-driver, HTTP-клиенте. ❌ ПОСЛЕДСТВИЕ: команда отказывается от гексагона при выборе MongoDB, считая что «без `@Repository` не получится», теряется тестируемость.

## Q13. Примеры Primary и Secondary адаптеров в реальном приложении

Типовой ecommerce-сервис:

**Primary (Driving) адаптеры:**

| Адаптер | Технология | Driving Port |
|---------|-----------|--------------|
| `OrderRestController` | `Spring MVC` | `PlaceOrderUseCase` |
| `OrderGraphQLResolver` | `GraphQL` | `GetOrderDetailsQuery` |
| `PaymentKafkaListener` | `Kafka` | `ConfirmPaymentUseCase` |
| `AdminCliCommand` | `Spring Shell` | `CancelOrderUseCase` |
| `OrderScheduler` | `@Scheduled` | `ProcessExpiredOrdersUseCase` |
| `OrderWebhookController` | HTTP Webhook | `ReceivePaymentWebhookUseCase` |

**Secondary (Driven) адаптеры:**

| Адаптер | Технология | Driven Port |
|---------|-----------|-------------|
| `OrderJpaAdapter` | `Spring Data JPA` | `OrderRepository` |
| `PaymentGatewayAdapter` | `RestClient` | `PaymentGateway` |
| `OrderEventPublisher` | `Kafka Producer` | `OrderEventPublisherPort` |
| `NotificationAdapter` | `SendGrid API` | `NotificationPort` |
| `RedisOrderCache` | `Redis` | `OrderCachePort` |
| `S3InvoiceStorage` | `AWS S3 SDK` | `InvoiceStoragePort` |

Обратите внимание: **один Driving Port** может вызываться **разными Primary-адаптерами** (REST + Kafka). Это иллюстрирует главное преимущество гексагональной архитектуры -- один и тот же Use Case, разные способы вызова.


> [!mcq]
> - [ ] `KafkaListener` — secondary adapter, `KafkaTemplate.send()` — primary adapter | Перевёрнуто: listener инициирует вызов ядра (driving), publisher — реализует driven port. ❌ ПОСЛЕДСТВИЕ: команда строит «обратный» гексагон, сообщения из Kafka обрабатываются как outbound — у consumer-flow нет точки входа в use-case.
> - [ ] Primary: `RestController`, `KafkaListener`, `@Scheduled`, `CliCommand`. Secondary: `JpaAdapter`, `RestClient` к платёжному шлюзу, `KafkaProducer`, `S3Storage` | Один Driving Port (`PlaceOrderUseCase`) может вызываться тремя primary-адаптерами одновременно. ✓ ПРИМЕНЯТЬ: типовой ecommerce — REST + Kafka + Scheduler вызывают один `PlaceOrderUseCase`. 📋 ПРАВИЛО: «primary инициирует, secondary обслуживает». 🔗 См. Q11, Q12.
> - [ ] `@Scheduled` — это secondary adapter, потому что вызывает ядро по расписанию | Шедулер инициирует вызов ядра — это driving (primary), направление вызова определяет тип, а не «откуда исходит триггер». ❌ ПОСЛЕДСТВИЕ: scheduler помещают в `adapter/out`, при code review архитектура смотрится перевёрнутой, новые разработчики копируют ошибочный паттерн.
> - [ ] Webhook-обработчик — secondary adapter (он принимает callback от внешней системы) | Webhook принимает HTTP-запрос и инициирует use-case — это primary (driving), несмотря на «callback»-семантику. ❌ ПОСЛЕДСТВИЕ: команда не оборачивает webhook в use-case, обработка платежа идёт напрямую из контроллера в репозиторий, дубликаты webhook вызывают двойное списание.

## Q14. Как адаптер преобразует данные между внешним миром и доменом?

Ключевой элемент адаптера -- **Mapper**. Он превращает:
- Внешний DTO → доменная Command (в primary-адаптере)
- Доменный результат → внешний DTO
- Доменная модель → JPA entity (в secondary-адаптере)
- JPA entity → доменная модель

```java
// Mapper для Web-адаптера
@Component
public class OrderWebMapper {
    public PlaceOrderCommand toCommand(CreateOrderDto dto) {
        var lines = dto.lines().stream()
            .map(l -> new OrderLineRequest(
                new ProductId(l.productId()), l.quantity()))
            .toList();
        return new PlaceOrderCommand(
            new CustomerId(dto.customerId()), lines);
    }

    public OrderDto toDto(Order order) {
        return new OrderDto(
            order.getId().value(),
            order.getStatus().name(),
            order.getTotalPrice().amount()
        );
    }
}

// Mapper для JPA-адаптера
@Component
public class OrderJpaMapper {
    public OrderJpaEntity toJpaEntity(Order order) {
        var entity = new OrderJpaEntity();
        entity.setId(order.getId().value());
        entity.setCustomerId(order.getCustomerId().value());
        entity.setStatus(order.getStatus().name());
        entity.setLines(order.getLines().stream()
            .map(this::toLineEntity).toList());
        return entity;
    }

    public Order toDomain(OrderJpaEntity entity) {
        var lines = entity.getLines().stream()
            .map(this::toDomainLine).toList();
        return Order.reconstitute(
            new OrderId(entity.getId()),
            new CustomerId(entity.getCustomerId()),
            OrderStatus.valueOf(entity.getStatus()),
            lines);
    }
}
```

Часто задаваемый вопрос: **кто владеет маппером**? Ответ: **адаптер**, потому что доменная модель не должна знать о DTO или JPA-сущностях. Mapper -- это часть адаптера, а не ядра.

Альтернатива -- использовать `MapStruct` для автогенерации, но держать код в пакете адаптера.


> [!mcq]
> - [ ] Mapper живёт в `domain/`, потому что он работает с доменными типами | Mapper знает про DTO/JPA-сущности — это инфраструктурный код; в `domain/` его быть не должно. ❌ ПОСЛЕДСТВИЕ: ядро тянет зависимость на `Jackson`/`jakarta.persistence`, multi-module проект отказывается собираться без spring-зависимостей в `domain` модуле.
> - [ ] Mapper — часть адаптера: `OrderWebMapper` в `adapter/in/web/`, `OrderJpaMapper` в `adapter/out/persistence/`; преобразует DTO ↔ Command/Result или JPA Entity ↔ Domain | Альтернатива: `MapStruct` для автогенерации, но всё равно в пакете адаптера. ✓ ПРИМЕНЯТЬ: `OrderJpaMapper` у Хомбергса лежит рядом с `AccountPersistenceAdapter`. 📋 ПРАВИЛО: «mapper рядом с адаптером, не в ядре». 🔗 См. Q11, Q12, Q40.
> - [ ] Mapper не нужен — Spring Data JPA автоматически маппит сущности в доменные модели через `@Projection` | `@Projection` создаёт интерфейс над `@Entity`, ядро всё равно зависит от JPA — это не изоляция, а декорирование. ❌ ПОСЛЕДСТВИЕ: проекция работает только в JPA-сессии, вне `@Transactional` падает с `LazyInitializationException`, команда не может использовать домен в Kafka-listener.
> - [ ] Использовать одну и ту же доменную модель и для JSON-сериализации, и для JPA — экономия на маппере | Это «активная сущность»: `@Entity` + `@JsonProperty` на одном классе ломает изоляцию ядра. ❌ ПОСЛЕДСТВИЕ: добавление поля в JPA меняет JSON API, контракты ломаются неявно — клиенты узнают о breaking change только в production.

## Q15. Может ли адаптер быть одновременно primary и secondary?

Редкий, но возможный случай. Пример -- **Kafka-адаптер** в паттерне "listener + producer":
- Слушает входящий топик (`OrderCreatedEvent`) → primary adapter, вызывает `ProcessOrderUseCase`
- Публикует исходящий топик (`OrderShippedEvent`) → secondary adapter, реализует `OrderEventPublisherPort`

**Лучшая практика** -- разделить это на два разных класса, даже если используется одна технология:

```java
// Primary: driving adapter
@Component
class OrderKafkaListener {
    private final ProcessOrderUseCase useCase;

    @KafkaListener(topics = "orders.created")
    public void handle(OrderCreatedEvent event) {
        useCase.process(new ProcessOrderCommand(event.orderId()));
    }
}

// Secondary: driven adapter
@Component
class OrderEventKafkaPublisher implements OrderEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderShippedEvent event) {
        kafkaTemplate.send("orders.shipped", event.orderId().toString(), event);
    }
}
```

Смешение в одном классе -- **антипаттерн**, так как нарушает SRP и затрудняет тестирование.


> [!mcq]
> - [ ] Да, лучшая практика — один класс `OrderKafkaAdapter` слушает входящий топик и публикует исходящий | Нарушение SRP: listener и publisher имеют разный жизненный цикл и разные failure modes. ❌ ПОСЛЕДСТВИЕ: ошибка в десериализации входящего сообщения роняет publisher, исходящие события `OrderShipped` теряются, downstream-сервисы не узнают об отгрузке.
> - [ ] Технически возможно, но антипаттерн: разделить на два класса — `OrderKafkaListener` (primary) и `OrderEventKafkaPublisher` (secondary), даже при одной технологии | Разная роль = разный класс, у каждого свой жизненный цикл и тесты. ✓ ПРИМЕНЯТЬ: типовой Kafka-flow Хомбергса — listener в `adapter/in/messaging`, publisher в `adapter/out/messaging`. 📋 ПРАВИЛО: «один класс — одна сторона гексагона». 🔗 См. Q10, Q15.
> - [ ] Только в случае gRPC — там сервер и клиент в одном `*Stub`-классе обязательно совмещены | gRPC stubs автогенерируются, но в гексагоне их оборачивают в раздельные адаптеры (server-side primary, client-side secondary). ❌ ПОСЛЕДСТВИЕ: команда выставляет `gRPC stub` напрямую в use-case, ядро узнаёт про `Channel`, `StreamObserver`, миграция на REST невозможна.
> - [ ] Невозможно технически — Spring запретит `@Component` с двойной ролью | Spring разрешает любое количество интерфейсов на одном bean'е, ограничения нет; вопрос только архитектурного смысла. ❌ ПОСЛЕДСТВИЕ: разработчик тратит время на «обход ограничения Spring», которого нет, появляются лишние wrapper-классы.

## Q16. (!) Какая рекомендуемая структура пакетов для гексагональной архитектуры в Spring Boot?

Каноническая структура (подход Тома Хомбергса):

```
src/main/java/com/company/bookkeeper/
├── BookkeeperApplication.java             # Spring Boot entry point
│
├── account/                               # Bounded Context / Feature
│   ├── domain/                            # Ядро: доменные модели
│   │   ├── Account.java                   # Aggregate
│   │   ├── AccountId.java                 # Value Object
│   │   ├── Money.java
│   │   ├── Activity.java
│   │   └── ActivityWindow.java
│   │
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/                        # Driving Ports
│   │   │   │   ├── SendMoneyUseCase.java
│   │   │   │   ├── SendMoneyCommand.java
│   │   │   │   └── GetAccountBalanceQuery.java
│   │   │   └── out/                       # Driven Ports
│   │   │       ├── LoadAccountPort.java
│   │   │       ├── UpdateAccountStatePort.java
│   │   │       └── AccountLock.java
│   │   └── service/                       # Use Case реализации
│   │       ├── SendMoneyService.java
│   │       └── GetAccountBalanceService.java
│   │
│   └── adapter/
│       ├── in/
│       │   └── web/                       # Primary Adapter: REST
│       │       ├── SendMoneyController.java
│       │       └── AccountWebMapper.java
│       └── out/
│           └── persistence/               # Secondary Adapter: JPA
│               ├── AccountPersistenceAdapter.java
│               ├── AccountJpaEntity.java
│               ├── ActivityJpaEntity.java
│               ├── AccountJpaRepository.java
│               └── AccountMapper.java
│
└── common/                                 # Общий код (если нужен)
    └── config/
        └── BeanConfiguration.java         # Composition Root
```

**Ключевые правила:**
1. `domain/` -- без `Spring`, без `jakarta.persistence`, без `Jackson` -- чистая Java
2. `application/port/in/` -- интерфейсы Driving Ports + Command/Query объекты
3. `application/port/out/` -- интерфейсы Driven Ports
4. `application/service/` -- реализации Use Cases (**могут** быть `@Component` или подключаться вручную)
5. `adapter/in/web/` -- `@RestController`, `@RequestMapping`
6. `adapter/out/persistence/` -- `@Repository`, JPA-entity
7. **Feature-first**: сначала группировка по Bounded Context (`account/`), потом по слою

Альтернативная схема для **monorepo / multi-module** -- вынести `domain/` и `application/` в отдельные Gradle-модули (см. Q22).


> [!mcq]
> - [ ] Layer-first: `controller/`, `service/`, `repository/`, `entity/` — стандарт Spring Boot | Это слоистая архитектура: тип файла (controller/service) важнее доменного контекста, фичи размазаны по слоям. ❌ ПОСЛЕДСТВИЕ: реализация одной фичи трогает 4 пакета, code review занимает 2x времени, junior-разработчики добавляют логику в random `service`-класс.
> - [ ] Feature-first: `<context>/domain/`, `<context>/application/port/in/`, `<context>/application/port/out/`, `<context>/application/service/`, `<context>/adapter/in/web/`, `<context>/adapter/out/persistence/` | Bounded Context на верхнем уровне, внутри — гексагон; одна фича = один пакет. ✓ ПРИМЕНЯТЬ: каноническая структура Хомбергса в `bookkeeper`, AWS Prescriptive Guidance подтверждает. 📋 ПРАВИЛО: «context первый, потом ядро/адаптеры». 🔗 См. Q22, Q23.
> - [ ] `domain/`, `infrastructure/`, `web/` — три плоских пакета | Слишком грубо: нет различия port/adapter/use-case, нет инверсии зависимостей через `port/in` vs `port/out`. ❌ ПОСЛЕДСТВИЕ: в `infrastructure/` смешиваются JPA и REST, ArchUnit не может выразить правило «adapter не зависит от другого adapter», структура деградирует за 6 месяцев.
> - [ ] Все классы в одном пакете `com.company.app` без подпакетов — чтобы IDE быстрее искала | Отсутствие границ — отсутствие архитектуры; ничто не мешает доменному классу импортировать `RestController`. ❌ ПОСЛЕДСТВИЕ: через год в проекте 800 классов в одном пакете, навигация через autocomplete нерабочая, любой рефакторинг — это глобальный поиск.

## Q17. Как реализовать Driving Port и Use Case в Java?

```java
// 1. Driving Port (в пакете application/port/in)
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}

// 2. Command -- self-validating value object
public record SendMoneyCommand(
    AccountId sourceAccountId,
    AccountId targetAccountId,
    Money amount
) {
    public SendMoneyCommand {
        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        requireNonNull(amount);
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

// 3. Use Case реализация (в пакете application/service)
@UseCase  // кастомная мета-аннотация вместо @Service
@Transactional
@RequiredArgsConstructor
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccount;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountState;
    private final MoneyTransferProperties properties;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        checkThreshold(command);

        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

        Account source = loadAccount.loadAccount(
            command.sourceAccountId(), baselineDate);
        Account target = loadAccount.loadAccount(
            command.targetAccountId(), baselineDate);

        AccountId sourceId = source.getId();
        AccountId targetId = target.getId();

        accountLock.lockAccount(sourceId);
        if (!source.withdraw(command.amount(), targetId)) {
            accountLock.releaseAccount(sourceId);
            return false;
        }

        accountLock.lockAccount(targetId);
        if (!target.deposit(command.amount(), sourceId)) {
            accountLock.releaseAccount(sourceId);
            accountLock.releaseAccount(targetId);
            return false;
        }

        updateAccountState.updateActivities(source);
        updateAccountState.updateActivities(target);

        accountLock.releaseAccount(sourceId);
        accountLock.releaseAccount(targetId);

        return true;
    }

    private void checkThreshold(SendMoneyCommand cmd) {
        if (cmd.amount().isGreaterThan(properties.getTransferThreshold())) {
            throw new ThresholdExceededException(
                properties.getTransferThreshold(), cmd.amount());
        }
    }
}
```

**Кастомная аннотация `@UseCase`** -- чтобы не тащить `@Service` в ядро, а также чтобы ArchUnit-правила могли проверять, что только Use Cases помечены этой аннотацией:

```java
@Target(TYPE)
@Retention(RUNTIME)
@Service  // meta-annotation
public @interface UseCase {}
```


> [!mcq]
> - [ ] `Driving Port = @RestController` с `@PostMapping`, `Use Case = @Service` напрямую вызывается из контроллера | `@RestController` — это адаптер; driving port — Java-`interface` без аннотаций транспорта. ❌ ПОСЛЕДСТВИЕ: бизнес-логика привязана к HTTP — нельзя триггерить use-case из Kafka-listener без копирования кода контроллера.
> - [ ] `Driving Port` = `interface ...UseCase` с методами-сценариями + `Command` record (self-validating); реализация — `@UseCase` (мета-аннотация) class в `application/service/` | Command содержит компактный валидационный блок (`requireNonNull`, `if` в compact ctor). ✓ ПРИМЕНЯТЬ: `SendMoneyUseCase` + `SendMoneyCommand` (record) + `SendMoneyService` у Хомбергса. 📋 ПРАВИЛО: «port + command + service: контракт, ввод, реализация». 🔗 См. Q19, Q20, Q28.
> - [ ] Use Case = `static`-метод в утилитном классе, зависимости передаются параметрами в каждый вызов | `static`-методы делают невозможной DI и подмену в тестах через моки/fakes. ❌ ПОСЛЕДСТВИЕ: тестируемость падает, command-объект разбухает до 15 параметров, на ревью считают, что «гексагон не работает в Java».
> - [ ] Use Case = расширение `JpaRepository<Order, Long>` со специализированными query-методами | Use Case оркестрирует домен и порты; `JpaRepository` — это инфраструктурный интерфейс, putting use-case на нём ломает направление зависимости. ❌ ПОСЛЕДСТВИЕ: команда строит «use-case» как Spring Data Repository — `placeOrder` превращается в JPA-запрос, нет места для бизнес-правил.

## Q18. Как реализовать Driven Port и его адаптер?

```java
// 1. Driven Port (в пакете application/port/out)
public interface LoadAccountPort {
    Account loadAccount(AccountId id, LocalDateTime baselineDate);
}

public interface UpdateAccountStatePort {
    void updateActivities(Account account);
}

// 2. JPA-сущность (в пакете adapter/out/persistence)
@Entity
@Table(name = "account")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountJpaEntity {
    @Id
    private Long id;
    private String ownerName;
    private BigDecimal balance;
}

// 3. Spring Data репозиторий -- технический интерфейс, не порт!
public interface AccountJpaRepository
        extends JpaRepository<AccountJpaEntity, Long> { }

// 4. Адаптер, реализующий порты
@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements
        LoadAccountPort,
        UpdateAccountStatePort {

    private final AccountJpaRepository accountRepo;
    private final ActivityJpaRepository activityRepo;
    private final AccountMapper mapper;

    @Override
    public Account loadAccount(AccountId id, LocalDateTime baseline) {
        AccountJpaEntity entity = accountRepo.findById(id.value())
            .orElseThrow(() -> new AccountNotFoundException(id));

        List<ActivityJpaEntity> activities = activityRepo
            .findByOwnerSince(id.value(), baseline);

        BigDecimal withdrawalBalance = activityRepo
            .getWithdrawalBalanceUntil(id.value(), baseline)
            .orElse(BigDecimal.ZERO);
        BigDecimal depositBalance = activityRepo
            .getDepositBalanceUntil(id.value(), baseline)
            .orElse(BigDecimal.ZERO);

        return mapper.mapToDomainEntity(
            entity, activities, withdrawalBalance, depositBalance);
    }

    @Override
    public void updateActivities(Account account) {
        account.getActivityWindow().getActivities().stream()
            .filter(a -> a.getId() == null)
            .forEach(a -> activityRepo.save(mapper.mapToJpaEntity(a)));
    }
}
```

Обратите внимание: `AccountJpaRepository` **не** является Driven Port -- это технический интерфейс Spring Data. Port -- это `LoadAccountPort`, а `Spring Data` -- это технология, которую использует адаптер.


> [!mcq]
> - [ ] `JpaRepository<AccountJpaEntity, Long>` — это и есть Driven Port, ничего больше делать не надо | `JpaRepository` — технический Spring Data интерфейс; ядро не должно его видеть, иначе тянется зависимость на `org.springframework.data`. ❌ ПОСЛЕДСТВИЕ: ядро импортирует `Pageable`, `PagingAndSortingRepository`, multi-module проект ломается на стадии compile в `bookkeeper-application`.
> - [ ] Driven Port — `interface LoadAccountPort` в `application/port/out/` с доменными типами; адаптер `AccountPersistenceAdapter implements LoadAccountPort` использует внутри `AccountJpaRepository` (Spring Data) и mapper для конвертации Entity → Domain | Spring Data Repository живёт в `adapter/out`, а не в ядре. ✓ ПРИМЕНЯТЬ: `AccountPersistenceAdapter` у Хомбергса вызывает `accountRepo.findById()` и маппит в `Account`. 📋 ПРАВИЛО: «port в ядре, JpaRepository в адаптере». 🔗 См. Q12, Q14, Q40.
> - [ ] Driven Port помечается аннотацией `@Repository`, чтобы Spring сам нашёл реализацию | `@Repository` — Spring-аннотация, не должна быть в ядре; на интерфейсе порта аннотации не нужны вообще. ❌ ПОСЛЕДСТВИЕ: ядро тянет `spring-tx`, multi-module изоляция ломается, попытка использовать ядро в desktop-приложении приводит к `ClassNotFoundException`.
> - [ ] Реализация driven port должна находиться в `domain/`, чтобы ядро было самодостаточным | Реализация = адаптер, по определению живёт снаружи ядра; в `domain/` только интерфейс не должен быть, он живёт в `application/port/out`. ❌ ПОСЛЕДСТВИЕ: `AccountPersistenceAdapter` с `@Component` оказывается в `domain/` — модуль `domain` тянет `spring-data-jpa`, теряется чистота.

## Q19. (!) Стоит ли использовать `@Service` на Use Case или держать ядро чистым?

Один из самых спорных вопросов. Два лагеря:

**Лагерь "чистое ядро" (purist)**:
- Use Case -- обычный Java-класс, без `@Service`
- DI выполняется в `@Configuration` классе внешнего слоя
- Плюсы: домен полностью переносим, можно юнит-тестировать без Spring
- Минусы: нужна ручная регистрация каждого Use Case

```java
// Чистый класс
public class SendMoneyService implements SendMoneyUseCase {
    public SendMoneyService(LoadAccountPort loadAccount, ...) { ... }
}

// Регистрация в Composition Root
@Configuration
public class UseCaseConfiguration {
    @Bean
    public SendMoneyUseCase sendMoneyUseCase(
            LoadAccountPort loadAccount,
            UpdateAccountStatePort updateState,
            AccountLock accountLock,
            MoneyTransferProperties properties) {
        return new SendMoneyService(loadAccount, accountLock,
            updateState, properties);
    }
}
```

**Лагерь "прагматики" (pragmatic)**:
- Use Case помечается `@Service` или кастомной `@UseCase`
- Spring автоматически собирает граф зависимостей
- Плюсы: меньше boilerplate, Spring сам разруливает
- Минусы: ядро зависит от `spring-context` (хотя это минимальная зависимость)

```java
@UseCase  // кастомная, скрывающая @Service
@Transactional
public class SendMoneyService implements SendMoneyUseCase { ... }
```

**Компромисс Хомбергса**: использовать кастомную аннотацию `@UseCase`, чтобы:
- ArchUnit мог проверять, что **только** Use Cases помечены ей
- Можно было легко заменить реализацию DI в будущем
- Код читался самодокументированно

> На собеседовании важно не "правильно" ответить, а показать понимание trade-offs. Оба подхода валидны, выбор зависит от контекста (библиотека vs монолит, жёсткость изоляции, команда).


> [!mcq]
> - [ ] `@Service`, `@Autowired`, `@Component` обязательны на use-case — без них Spring не найдёт bean | DI можно сделать вручную через `@Configuration` + `@Bean`, ядро тогда полностью чистое; аннотации — удобство, не необходимость. ❌ ПОСЛЕДСТВИЕ: команда уверена что «без Spring никак», ядро вязнет в `org.springframework.beans`, переход на standalone — переписывание.
> - [ ] Кастомная мета-аннотация `@UseCase` (с `@Service` внутри): ядро ссылается на `@UseCase`, ArchUnit проверяет что `@UseCase` ставят только на классы из `application/service/` | Компромисс Хомбергса: одна точка контроля, в будущем легко заменить на ручной DI. ✓ ПРИМЕНЯТЬ: `bookkeeper` использует `@UseCase` для всех application services. 📋 ПРАВИЛО: «meta-annotation `@UseCase` инкапсулирует `@Service`». 🔗 См. Q21, Q42.
> - [ ] Use Case должен наследоваться от `AbstractApplicationContext` для прямого доступа к bean'ам | Это нарушает инверсию: ядро управляет контекстом, а не наоборот. ❌ ПОСЛЕДСТВИЕ: use-case вытаскивает зависимости через `getBean()` в runtime, статическая проверка зависимостей невозможна, ошибки конфигурации проявляются только в production.
> - [ ] Лучшее решение — `@SpringBootApplication` + `@ComponentScan(basePackages = "**.domain")` | `domain/` не должен сканироваться Spring'ом — это исключает доменную модель из IoC, что и нужно. ❌ ПОСЛЕДСТВИЕ: Spring пытается инстанцировать `Money` (Value Object), падает при отсутствии `@Bean`-конструктора, разработчик добавляет `@Component` в Value Object — VO становится изменяемым singleton.

## Q20. Где размещать транзакционные границы в гексагональной архитектуре?

**Транзакция -- это application concern**, а не domain concern. Поэтому `@Transactional` размещается на уровне Use Case (Application Service), а не в адаптере и не в домене.

```java
@UseCase
@Transactional  // Use Case определяет границы транзакции
public class SendMoneyService implements SendMoneyUseCase {

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        Account source = loadAccount.loadAccount(command.sourceAccountId());
        Account target = loadAccount.loadAccount(command.targetAccountId());

        source.withdraw(command.amount(), target.getId());
        target.deposit(command.amount(), source.getId());

        updateState.updateActivities(source);
        updateState.updateActivities(target);
        return true;
    }
}
```

Проблема "чистого ядра": `@Transactional` -- это Spring-аннотация. Варианты:
1. **Смириться** и поставить её на Use Case -- минимальное протекание
2. **Вынести** в декоратор (TransactionalUseCaseDecorator) в Application слое
3. **Управлять транзакцией вручную** через `TransactionTemplate`, принимая его как зависимость

Для read-only Use Cases используйте `@Transactional(readOnly = true)` -- меньше накладных расходов на JDBC.

**Не размещать `@Transactional`**:
- В адаптерах (контроллерах, JPA-адаптерах) -- транзакция должна охватывать всю бизнес-операцию
- В доменных моделях -- домен не знает о транзакциях
- Вложенно (`@Transactional` на контроллере + на Use Case) -- дублирование


> [!mcq]
> - [ ] `@Transactional` на `@RestController` — тогда вся обработка запроса в одной транзакции | Транзакция должна охватывать use-case (бизнес-операцию), а не HTTP-запрос: при ошибке после use-case (сериализация JSON) уже поздно. ❌ ПОСЛЕДСТВИЕ: исключение при сериализации возвращаемого DTO откатывает успешно выполненную бизнес-операцию; пользователю — 500, в БД — состояние неконсистентно с тем, что увидит при retry.
> - [ ] `@Transactional` на use-case (Application Service); read-only — `@Transactional(readOnly = true)`; вне адаптеров и домена | Транзакция — application concern; Use Case определяет бизнес-границу. ✓ ПРИМЕНЯТЬ: `SendMoneyService` у Хомбергса помечен `@Transactional` целиком. 📋 ПРАВИЛО: «транзакция = use-case, не контроллер и не репозиторий». 🔗 См. Q19, Q26, Q44.
> - [ ] `@Transactional` на каждом методе репозитория — granular control | Гранулярные транзакции на репозитории = N маленьких транзакций вместо одной бизнес-транзакции; consistency между save() Account и save() Activity невозможна. ❌ ПОСЛЕДСТВИЕ: при money transfer source.withdraw() и target.deposit() оказываются в разных транзакциях; крах между ними — деньги списались, не зачислены, требуется ручная reconciliation.
> - [ ] `@Transactional` на доменных сущностях (`@Entity Order`) | Домен не знает про транзакции; ставить `@Transactional` на сущность — путаница уровней, JPA-сущность ≠ доменный объект. ❌ ПОСЛЕДСТВИЕ: команда смешивает Entity и доменную модель, `@Transactional` не работает (это аннотация для proxy на bean, не для JPA-сущности), но никто этого не замечает до production.

## Q21. Как организовать DI / Composition Root в Spring?

**Composition Root** -- единственное место, где собираются все зависимости. В Spring Boot это обычно класс с `@Configuration`.

Вариант A: использование автосканирования (прагматичный):

```java
@SpringBootApplication
@ComponentScan(basePackages = "com.company.bookkeeper")
public class BookkeeperApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookkeeperApplication.class, args);
    }
}
```
Use Cases помечены `@UseCase`, адаптеры -- `@Component`/`@RestController`. Spring сам собирает граф.

Вариант B: явная конфигурация (purist):

```java
@Configuration
public class BookkeeperConfiguration {

    @Bean
    public SendMoneyUseCase sendMoneyUseCase(
            LoadAccountPort loadAccount,
            UpdateAccountStatePort updateState,
            AccountLock accountLock,
            MoneyTransferProperties props) {
        return new SendMoneyService(
            loadAccount, accountLock, updateState, props);
    }

    @Bean
    public LoadAccountPort loadAccountPort(
            AccountJpaRepository accountRepo,
            ActivityJpaRepository activityRepo,
            AccountMapper mapper) {
        return new AccountPersistenceAdapter(
            accountRepo, activityRepo, mapper);
    }
}
```

**Где размещать**:
- В модуле "main" или "app", который знает обо всех остальных модулях
- В корневом пакете, не в `domain` или `application`
- Можно разделить на несколько `@Configuration` классов по Bounded Context


> [!mcq]
> - [ ] Composition Root размещается в каждом use-case-классе через `new SendMoneyService(...)` для прозрачности | DI-сборка размазана по ядру; невозможно поменять реализацию порта без изменения use-case'а — теряется смысл инверсии. ❌ ПОСЛЕДСТВИЕ: для теста с fake-репозиторием приходится переписывать use-case или копировать его, тестовый код параллелит production-логику.
> - [ ] Composition Root — `@Configuration` класс в модуле `app` (или auto-scan через `@SpringBootApplication`); собирает граф из `domain/`, `application/`, `adapter/in`, `adapter/out`; ни один из них не знает о других | Можно разделить `@Configuration` по Bounded Context'ам. ✓ ПРИМЕНЯТЬ: `BookkeeperApplication` в модуле `app` сканирует все остальные. 📋 ПРАВИЛО: «один граф зависимостей собирается на самом верху». 🔗 См. Q19, Q22.
> - [ ] DI делается через `ApplicationContext.getBean()` внутри use-case'а — это «Service Locator pattern» | Service Locator скрывает зависимости (нет в конструкторе), статически невозможно проверить полноту графа. ❌ ПОСЛЕДСТВИЕ: запуск контекста проходит, при первом вызове — `NoSuchBeanDefinitionException`, узнаём об отсутствующей зависимости только в runtime.
> - [ ] Каждый адаптер сам резолвит свои зависимости через `@Autowired ApplicationContext` | Это та же ошибка Service Locator, плюс адаптеры тащат на себя ответственность DI, которой у них быть не должно. ❌ ПОСЛЕДСТВИЕ: тестирование адаптера требует поднимать частичный контекст Spring, юнит-тесты превращаются в интеграционные, время прогона CI растёт x10.

## Q22. Multi-module Gradle/Maven проект для гексагональной архитектуры

Для крупных проектов гексагональную архитектуру оформляют как **многомодульный проект**. Это делает правила зависимостей автоматическими (нельзя заимпортить то, что не объявлено в зависимостях).

```
bookkeeper/
├── settings.gradle
├── build.gradle
├── bookkeeper-domain/              # чистая Java, без Spring
│   └── build.gradle
├── bookkeeper-application/         # Use Cases + Ports
│   └── build.gradle                # depends on :bookkeeper-domain
├── bookkeeper-adapter-web/         # REST-адаптер
│   └── build.gradle                # depends on :bookkeeper-application
├── bookkeeper-adapter-persistence/ # JPA-адаптер
│   └── build.gradle                # depends on :bookkeeper-application
└── bookkeeper-app/                 # Composition Root
    └── build.gradle                # depends on all
```

`bookkeeper-domain/build.gradle`:
```groovy
plugins {
    id 'java-library'
}
// никаких Spring-зависимостей
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter'
}
```

`bookkeeper-application/build.gradle`:
```groovy
dependencies {
    implementation project(':bookkeeper-domain')
    // минимум Spring, только то, что нужно Use Cases
    implementation 'org.springframework:spring-tx'
}
```

`bookkeeper-app/build.gradle`:
```groovy
dependencies {
    implementation project(':bookkeeper-application')
    implementation project(':bookkeeper-adapter-web')
    implementation project(':bookkeeper-adapter-persistence')
    implementation 'org.springframework.boot:spring-boot-starter'
}
```

Преимущества:
- **Физическое разделение** -- невозможно импортировать `org.springframework.web` из `domain`
- **Параллельная сборка** Gradle
- **Быстрые unit-тесты** в `domain` (нет Spring context)

Недостаток: больше `build.gradle` файлов, сложнее рефакторинг.


> [!mcq]
> - [ ] Один модуль на весь проект, гексагон строится через пакеты — multi-module overkill | Пакеты не enforce'ят зависимости: `domain`-пакет может импортировать `org.springframework.web` без ошибки компиляции. ❌ ПОСЛЕДСТВИЕ: со временем дисциплина ослабевает, в `domain/` появляются `@RestController` и JPA-сущности, при ревью никто не заметит до сборки.
> - [ ] Multi-module: `domain` (чистая Java), `application` (зависит от `domain`), `adapter-web`/`adapter-persistence` (зависят от `application`), `app` (Composition Root, зависит от всех) | Gradle/Maven отказывается компилировать при попытке импорта Spring в `domain`. ✓ ПРИМЕНЯТЬ: проект `bookkeeper` Хомбергса разбит на 5 модулей с явными `implementation project(...)`. 📋 ПРАВИЛО: «зависимости — в `build.gradle`, а не в честном слове». 🔗 См. Q16, Q42.
> - [ ] `domain/` должен зависеть от `adapter-web`/`-persistence`, потому что они «снаружи» | Перевёрнутые зависимости: гексагон требует обратного — адаптеры зависят от `application`, который зависит от `domain`. ❌ ПОСЛЕДСТВИЕ: `domain` тащит `spring-web` и `jakarta.persistence`, теряется главная цель multi-module — изоляция ядра.
> - [ ] Один модуль на адаптер, второй на JPA-сущности отдельно от mapper'а | Произвольная декомпозиция; mapper и адаптер должны быть в одном модуле, иначе цикличные зависимости или утечка JPA-Entity в другой модуль. ❌ ПОСЛЕДСТВИЕ: модуль `mapper` тянет JPA Entity (отдельный модуль) — чтобы поменять схему БД, нужно ребилдить три модуля.

## Q23. (!) Как гексагональная архитектура сочетается с DDD?

`Hexagonal Architecture` и `DDD` -- **естественные партнёры**. Гексагон даёт структурный скелет, DDD -- тактические и стратегические паттерны.

Как они стыкуются:

| DDD понятие | Где живёт в гексагоне |
|-------------|----------------------|
| `Aggregate`, `Entity`, `Value Object` | `domain/` |
| `Domain Service` | `domain/` |
| `Domain Event` | `domain/` |
| `Application Service` | `application/service/` (реализация Use Case) |
| `Repository` interface | `application/port/out/` |
| `Repository` implementation | `adapter/out/persistence/` |
| `Bounded Context` | отдельный Java-пакет или модуль |
| `Anti-Corruption Layer` | secondary adapter между контекстами |

```mermaid
graph TB
    subgraph "Bounded Context: Order"
        subgraph "Domain"
            AGG[Order Aggregate]
            VO[OrderStatus VO]
            DS[PricingService]
            DE[OrderPlaced Event]
        end
        subgraph "Application"
            UC[PlaceOrderUseCase]
            IP[Driving Port]
            OP[OrderRepository Port]
            EP[EventPublisher Port]
        end
        subgraph "Adapters"
            CTL[OrderController]
            JPA[OrderJpaAdapter]
            KAFKA[KafkaEventPublisher]
        end
    end
    CTL --> IP
    IP --> UC
    UC --> AGG
    UC --> OP
    UC --> EP
    JPA -.->|implements| OP
    KAFKA -.->|implements| EP
```

Принципиально: **DDD обогащает домен**, гексагон **изолирует его** от инфраструктуры. Вместе они дают богатую, переносимую, тестируемую модель.


> [!mcq]
> - [ ] Hexagonal и DDD взаимоисключающи: либо гексагон, либо DDD | Это естественные партнёры: гексагон — структурный скелет (порты/адаптеры), DDD — тактика (агрегаты, VO) и стратегия (Bounded Context, ACL). ❌ ПОСЛЕДСТВИЕ: команда выбирает «или-или», теряет половину выгоды; либо чистая структура без богатого домена, либо богатая модель без явных границ адаптеров.
> - [ ] Aggregate/Entity/VO/DomainEvent — в `domain/`; Application Service — в `application/service/`; Repository interface — в `application/port/out/`; Repository implementation — в `adapter/out/persistence/`; Bounded Context = модуль/пакет; ACL = secondary adapter между контекстами | Каждое DDD-понятие имеет место в гексагоне. ✓ ПРИМЕНЯТЬ: `bookkeeper` Хомбергса использует DDD-агрегаты (`Account`) внутри гексагональной структуры. 📋 ПРАВИЛО: «гексагон — где, DDD — что». 🔗 См. Q24, Q25, Q40.
> - [ ] Все DDD-классы (Aggregate, Repository, Service) идут в `infrastructure/` | Это анти-DDD: агрегат — ядро домена, не инфраструктура; такая структура воспроизводит anemic-модель в маске «гексагона». ❌ ПОСЛЕДСТВИЕ: бизнес-правила «расползаются» по `infrastructure/`, code review не находит «куда смотреть» при изменении правила, новички пишут логику в random-классе.
> - [ ] Bounded Context реализуется как отдельный микросервис, никаких границ внутри monolith не нужно | BC — логическая граница, может быть пакетом/модулем в monolith или отдельным сервисом; гексагон применим к обоим. ❌ ПОСЛЕДСТВИЕ: команда сначала строит «хороший monolith без границ», потом вынужденная миграция в микросервисы — переписывание, потому что контексты переплетены.

## Q24. Где в гексагоне живут агрегаты, Value Objects и доменные сервисы?

Всё, что относится к **доменной логике**, живёт в пакете `domain/` внутри `application core`:

```java
// Aggregate Root
public class Account {
    private final AccountId id;
    private final Money baselineBalance;
    private final ActivityWindow activityWindow;

    public boolean withdraw(Money money, AccountId targetAccountId) {
        if (!mayWithdraw(money)) return false;

        Activity withdrawal = new Activity(
            this.id, this.id, targetAccountId,
            LocalDateTime.now(), money);
        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    private boolean mayWithdraw(Money money) {
        return Money.add(this.calculateBalance(), money.negate())
            .isPositiveOrZero();
    }
}

// Value Object
public record Money(BigDecimal amount) {
    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
    public boolean isPositive() {
        return amount.signum() > 0;
    }
}

// Domain Service (когда логика не влезает в один агрегат)
public class MoneyTransferDomainService {
    public boolean transfer(Account source, Account target,
                            Money amount) {
        if (!source.withdraw(amount, target.getId())) return false;
        target.deposit(amount, source.getId());
        return true;
    }
}
```

**Domain Event** -- тоже в `domain/`:
```java
public record AccountDebited(
    AccountId accountId,
    Money amount,
    LocalDateTime occurredAt
) {}
```

**Важно**: ни один из этих классов не должен содержать Spring-аннотаций или зависеть от инфраструктурных интерфейсов напрямую.


> [!mcq]
> - [ ] Aggregates в `application/`, Value Objects в `domain/`, Domain Services — в `infrastructure/` | Все три — части доменного ядра, должны жить в `domain/` без аннотаций фреймворков. ❌ ПОСЛЕДСТВИЕ: agreggate `Order` зависит от `application/` (нарушение направления), Value Object `Money` живёт отдельно от агрегата, который его использует — циклы импортов и сложности тестирования.
> - [ ] Aggregate Root, Entity, Value Object, Domain Service, Domain Event — все в `domain/`; никаких Spring/JPA-аннотаций, агрегаты содержат поведение (методы), не только данные | Поведение в агрегате (`Account.withdraw()`) — против анемичной модели. ✓ ПРИМЕНЯТЬ: `Account` у Хомбергса содержит `withdraw()`/`deposit()`, не setter'ы. 📋 ПРАВИЛО: «всё доменное — в `domain/`, без фреймворков». 🔗 См. Q23, Q39.
> - [ ] Value Objects в `domain/`, агрегаты — JPA-сущности в `adapter/out/persistence/` | Агрегат — ядро домена, JPA-Entity — представление в БД; объединение в одном классе ломает изоляцию. ❌ ПОСЛЕДСТВИЕ: `Order` зависит от `jakarta.persistence`, нельзя протестировать `order.submit()` без `EntityManager`, lazy loading проникает в бизнес-логику.
> - [ ] Domain Service — обязательно `@Service`, потому что иначе Spring не сможет внедрить зависимости | Domain Service — чистая Java-логика без зависимостей на инфраструктуру; если ему нужны порты — это уже Application Service. ❌ ПОСЛЕДСТВИЕ: `PricingDomainService` помечен `@Service`, ядро тянет `spring-context`, а внутри сервиса используется `@Autowired RestTemplate` — это уже не domain service.

## Q25. В чём разница между Application Service и Domain Service?

Частая тема на собеседованиях. Обе сущности содержат "логику", но разного уровня:

| Аспект | Application Service | Domain Service |
|--------|---------------------|----------------|
| **Ответственность** | Оркестрация Use Case | Чистая доменная логика |
| **Знает о** | Портах, транзакциях, безопасности | Только о домене |
| **Находится в** | `application/service/` | `domain/` |
| **Вызывает** | Domain Services, Aggregates | Только Aggregates |
| **Аннотации** | `@Transactional`, возможно `@UseCase` | Никаких |
| **Пример** | `SendMoneyService` | `OverdraftPolicyService`, `PricingService` |

```java
// Application Service: оркестрирует Use Case
@UseCase
@Transactional
public class SendMoneyService implements SendMoneyUseCase {
    private final LoadAccountPort loadAccount;
    private final UpdateAccountStatePort updateState;
    private final MoneyTransferDomainService transferService;

    public boolean sendMoney(SendMoneyCommand cmd) {
        Account source = loadAccount.loadAccount(cmd.sourceId());
        Account target = loadAccount.loadAccount(cmd.targetId());

        boolean success = transferService.transfer(
            source, target, cmd.amount());  // <-- доменная логика

        if (success) {
            updateState.updateActivities(source);
            updateState.updateActivities(target);
        }
        return success;
    }
}

// Domain Service: чистая логика без инфраструктуры
public class MoneyTransferDomainService {
    public boolean transfer(Account source, Account target, Money amount) {
        if (!source.withdraw(amount, target.getId())) return false;
        target.deposit(amount, source.getId());
        return true;
    }
}
```

Правило: если логику **можно выразить без I/O** (БД, HTTP) -- это Domain Service. Если нужна оркестрация с портами -- Application Service.


> [!mcq]
> - [ ] Application Service и Domain Service — синонимы, разница только в naming convention | ❌ ПОСЛЕДСТВИЕ: смешивание ответственностей, инфраструктура попадает в домен.
> - [x] Application Service оркеструет Use Case (порты, транзакции, безопасность); Domain Service — чистая доменная логика без I/O, выражается без БД/HTTP; если логика требует портов — Application, если pure — Domain | ✓ ПРИМЕНЯТЬ: разделение responsibilities. 📋 ПРАВИЛО: «без I/O — в домен». 🔗 См. Q26
> - [ ] Domain Service всегда @Transactional, Application — нет | ❌ ПОСЛЕДСТВИЕ: транзакции в домене = leakage инфраструктуры, ломает чистоту core.
> - [ ] Любая логика с участием Aggregate автоматически Domain Service | ❌ ПОСЛЕДСТВИЕ: anemic domain, методы агрегата уходят в "сервисы".

## Q26. Как обрабатывать доменные события через Driven Port?

Типовой подход: агрегат **порождает событие** при изменении состояния, Use Case **собирает** события и **публикует** через Driven Port.

```java
// 1. Domain Event (в domain/)
public record OrderPlaced(OrderId orderId, CustomerId customerId,
                         Money total, LocalDateTime occurredAt) {}

// 2. Aggregate накапливает события
public class Order {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public void place() {
        this.status = OrderStatus.PLACED;
        this.domainEvents.add(new OrderPlaced(
            this.id, this.customerId, this.total, LocalDateTime.now()));
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
}

// 3. Driven Port (в application/port/out/)
public interface DomainEventPublisher {
    void publish(List<DomainEvent> events);
}

// 4. Use Case публикует события после сохранения агрегата
@UseCase
@Transactional
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository orderRepo;
    private final DomainEventPublisher eventPublisher;

    @Override
    public OrderId placeOrder(PlaceOrderCommand cmd) {
        Order order = Order.create(cmd.customerId(), cmd.lines());
        order.place();
        orderRepo.save(order);

        eventPublisher.publish(order.pullDomainEvents());
        return order.getId();
    }
}

// 5. Адаптер: публикует через Kafka
@Component
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(e -> kafkaTemplate.send(
            resolveTopicFor(e), eventKey(e), e));
    }
}
```

**Типичный антипаттерн**: публиковать события из адаптера репозитория (через `@DomainEvents` в Spring Data). Это "магия", которая скрывает важную часть бизнес-логики. Лучше явно вызывать `publisher.publish()` из Use Case.

Для **гарантий доставки** используется `Transactional Outbox` -- событие сохраняется в ту же БД в отдельной таблице, отдельный процесс затем публикует в Kafka.


> [!mcq]
> - [ ] Публиковать события напрямую из агрегата через static factory или Singleton publisher | ❌ ПОСЛЕДСТВИЕ: тестируемость гибнет, агрегат знает про инфраструктуру, нарушение DIP.
> - [ ] Использовать @DomainEvents Spring Data в репозитории — это автомагия | ❌ ПОСЛЕДСТВИЕ: скрытая важная часть бизнес-логики, события публикуются за пределами транзакции Use Case, гонка с retry.
> - [x] Aggregate накапливает события (List<DomainEvent>), Use Case вызывает aggregate.pullDomainEvents() после save() и публикует через DomainEventPublisher Driven Port; адаптер — KafkaTemplate; для гарантий — Transactional Outbox | ✓ ПРИМЕНЯТЬ: domain events в гексагоне. 📋 ПРАВИЛО: «явный publish из Use Case». 🔗 См. Q27
> - [ ] Использовать ApplicationEventPublisher Spring напрямую в агрегате | ❌ ПОСЛЕДСТВИЕ: domain knows Spring, тестировать без контекста невозможно.

## Q27. (!) Какая стратегия тестирования рекомендуется для гексагональной архитектуры?

Гексагональная архитектура естественно раскладывается на пирамиду тестирования:

```mermaid
graph TB
    subgraph "E2E Tests"
        E2E[End-to-End<br/>@SpringBootTest + Testcontainers]
    end
    subgraph "Integration Tests"
        INT[Adapter Tests<br/>@WebMvcTest, @DataJpaTest]
    end
    subgraph "Unit Tests"
        UC[Use Case Tests<br/>без Spring, моки портов]
        DOM[Domain Tests<br/>чистая Java]
    end
    DOM --> UC
    UC --> INT
    INT --> E2E
    style DOM fill:#2d5016,color:#fff
    style UC fill:#4a7c2e,color:#fff
    style INT fill:#6ba34a,color:#fff
    style E2E fill:#8cc665,color:#000
```

| Уровень | Что тестируем | Инструменты | % |
|---------|---------------|-------------|---|
| **Domain** | `Account.withdraw()`, `Money.add()` | `JUnit 5`, без Spring | ~50% |
| **Use Case** | `SendMoneyService` -- моки портов | `Mockito`, без Spring | ~30% |
| **Adapter** | `AccountPersistenceAdapter`, `SendMoneyController` | `@DataJpaTest`, `@WebMvcTest`, `Testcontainers` | ~15% |
| **E2E** | Полный путь HTTP→БД | `@SpringBootTest`, `Testcontainers`, `RestAssured` | ~5% |

**Ключевое преимущество**: 80% тестов (domain + use case) не требуют Spring контекста -- они запускаются за миллисекунды.

Часто встречающийся **антипаттерн** -- писать только `@SpringBootTest`. Это медленно, ненадёжно и не проверяет архитектурную изоляцию.


> [!mcq]
> - [ ] Только @SpringBootTest с Testcontainers — единственный надёжный способ | ❌ ПОСЛЕДСТВИЕ: 100% медленных тестов, минуты на запуск, изоляция домена не проверяется.
> - [ ] Покрывать всё E2E тестами — пирамида не нужна | ❌ ПОСЛЕДСТВИЕ: ice-cream cone antipattern, fragile suite, маскирует bugs.
> - [x] Пирамида: Domain ~50% (JUnit без Spring, чистая Java) + Use Case ~30% (Mockito без Spring) + Adapter ~15% (@WebMvcTest, @DataJpaTest + Testcontainers) + E2E ~5% (@SpringBootTest); 80% тестов запускаются миллисекунды | ✓ ПРИМЕНЯТЬ: testing strategy. 📋 ПРАВИЛО: «домен без фреймворка». 🔗 См. Q28
> - [ ] Тестировать только адаптеры — Use Case = просто оркестрация | ❌ ПОСЛЕДСТВИЕ: бизнес-логика непокрыта, регрессии в orchestration уходят в прод.

## Q28. (!) Как тестировать Use Case без инфраструктуры?

Use Case -- это сердце тестирования гексагональной архитектуры. Он тестируется с **моками портов**:

```java
class SendMoneyServiceTest {

    private final LoadAccountPort loadAccount = mock(LoadAccountPort.class);
    private final AccountLock accountLock = mock(AccountLock.class);
    private final UpdateAccountStatePort updateState = mock(UpdateAccountStatePort.class);
    private final MoneyTransferProperties properties = new MoneyTransferProperties(Money.of(Long.MAX_VALUE));

    private final SendMoneyService service = new SendMoneyService(
        loadAccount, accountLock, updateState, properties);

    @Test
    void transactionSucceeds() {
        // given
        Account sourceAccount = givenSourceAccount();
        Account targetAccount = givenTargetAccount();
        givenWithdrawalWillSucceed(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        Money transferMoney = Money.of(500L);
        SendMoneyCommand command = new SendMoneyCommand(
            sourceAccount.getId(), targetAccount.getId(), transferMoney);

        // when
        boolean success = service.sendMoney(command);

        // then
        assertThat(success).isTrue();
        AccountId sourceId = sourceAccount.getId();
        AccountId targetId = targetAccount.getId();

        then(accountLock).should().lockAccount(eq(sourceId));
        then(sourceAccount).should().withdraw(eq(transferMoney), eq(targetId));
        then(accountLock).should().releaseAccount(eq(sourceId));

        then(accountLock).should().lockAccount(eq(targetId));
        then(targetAccount).should().deposit(eq(transferMoney), eq(sourceId));
        then(accountLock).should().releaseAccount(eq(targetId));

        thenAccountsHaveBeenUpdated(sourceId, targetId);
    }

    @Test
    void givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        Account sourceAccount = givenSourceAccount();
        givenWithdrawalWillFail(sourceAccount);

        SendMoneyCommand cmd = new SendMoneyCommand(
            sourceAccount.getId(), new AccountId(41L), Money.of(300L));

        boolean success = service.sendMoney(cmd);

        assertThat(success).isFalse();
        then(accountLock).should().lockAccount(eq(sourceAccount.getId()));
        then(accountLock).should().releaseAccount(eq(sourceAccount.getId()));
        then(accountLock).should(never()).lockAccount(eq(new AccountId(41L)));
    }
}
```

Ключевые моменты:
- **Никаких `@SpringBootTest`, `@MockBean`** -- обычный `new` и `Mockito`
- **Тест выполняется за миллисекунды**
- **Проверяем поведение**: что withdraw был вызван, что lock был захвачен и отпущен
- **Тестируем сценарии**: happy path, failure path, edge cases

Если Use Case принимает 8+ портов -- это знак, что его стоит разбить на несколько более мелких Use Cases (нарушение SRP).


> [!mcq]
> - [ ] @SpringBootTest + @MockBean каждого зависимого порта | ❌ ПОСЛЕДСТВИЕ: Spring context на unit-тест Use Case, секунды вместо миллисекунд, контекст пересоздаётся.
> - [x] Plain new() Use Case + Mockito моки портов; проверяем поведение (verify withdraw, lock/release); никаких @SpringBootTest или @MockBean; happy path + failure scenarios; 8+ портов = знак SRP violation | ✓ ПРИМЕНЯТЬ: unit-тесты ядра. 📋 ПРАВИЛО: «миллисекунды, не секунды». 🔗 См. Q29
> - [ ] Использовать реальные адаптеры с in-memory БД для скорости | ❌ ПОСЛЕДСТВИЕ: смешение уровней, тест проверяет адаптер вместо логики Use Case.
> - [ ] PowerMock для static mocks инфраструктуры | ❌ ПОСЛЕДСТВИЕ: тяжёлый хак, byte code manipulation, code smell — статика в гексагоне = ошибка дизайна.

## Q29. Как тестировать адаптеры (контроллеры и репозитории)?

Каждый адаптер тестируется **с его реальной инфраструктурой**, но **изолированно** от остального приложения.

**Primary Adapter (Web) -- `@WebMvcTest`**:

```java
@WebMvcTest(SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void testSendMoney() throws Exception {
        mockMvc.perform(post("/accounts/send/{src}/{tgt}/{amount}",
                41L, 42L, 500))
            .andExpect(status().isOk());

        then(sendMoneyUseCase).should()
            .sendMoney(eq(new SendMoneyCommand(
                new AccountId(41L), new AccountId(42L), Money.of(500L))));
    }
}
```

**Secondary Adapter (JPA) -- `@DataJpaTest` с Testcontainers**:

```java
@DataJpaTest
@Import({AccountPersistenceAdapter.class, AccountMapper.class})
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
class AccountPersistenceAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AccountPersistenceAdapter adapter;

    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    void loadsAccount() {
        Account account = adapter.loadAccount(
            new AccountId(1L), LocalDateTime.of(2024, 1, 1, 0, 0));

        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }
}
```

Преимущества:
- Адаптер тестируется с реальной БД (Testcontainers)
- Use Case не участвует -- проверяем чисто адаптер
- Быстро: запускается только нужный контекст


> [!mcq]
> - [x] Primary (Web) — @WebMvcTest + MockMvc + @MockBean UseCase; Secondary (JPA) — @DataJpaTest + @Import адаптер + Testcontainers PostgreSQL; адаптер с реальной БД, Use Case не участвует — тестируем чисто адаптер | ✓ ПРИМЕНЯТЬ: integration tests адаптеров. 📋 ПРАВИЛО: «реальная инфра, изолированный slice». 🔗 См. Q30
> - [ ] @SpringBootTest для всех адаптеров для consistency | ❌ ПОСЛЕДСТВИЕ: полный контекст ради одного контроллера — медленно, fragile.
> - [ ] Полностью мокать БД через @MockBean JpaRepository | ❌ ПОСЛЕДСТВИЕ: маппинг JPA не проверяется, SQL-баги уходят в прод.
> - [ ] Использовать H2 in-memory для всех integration тестов | ❌ ПОСЛЕДСТВИЕ: dialect отличия H2 vs Postgres, баги jsonb/array всплывают только в prod.

## Q30. Что такое port-level / contract testing?

**Contract testing (port-level testing)** -- тестирование, при котором **один и тот же набор тестов** применяется:
1. К реальной реализации (с инфраструктурой)
2. К fake-реализации (в памяти)

Это гарантирует, что fake **корректно имитирует** реальный адаптер -- иначе в Use Case тестах можно "проскочить" баг, который всплывёт в продакшене.

```java
// Абстрактный контракт теста порта
abstract class LoadAccountPortContractTest {
    protected abstract LoadAccountPort createPort();

    @Test
    void returnsAccountWhenExists() {
        LoadAccountPort port = createPort();
        Account account = port.loadAccount(new AccountId(1L), baseline());
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(new AccountId(1L));
    }

    @Test
    void throwsWhenAccountMissing() {
        LoadAccountPort port = createPort();
        assertThatThrownBy(() -> port.loadAccount(
            new AccountId(999L), baseline()))
            .isInstanceOf(AccountNotFoundException.class);
    }
}

// Реальная реализация
@DataJpaTest
class JpaLoadAccountPortContractTest extends LoadAccountPortContractTest {
    @Autowired AccountPersistenceAdapter adapter;

    @Override
    protected LoadAccountPort createPort() { return adapter; }
}

// Fake реализация
class InMemoryLoadAccountPortContractTest extends LoadAccountPortContractTest {
    @Override
    protected LoadAccountPort createPort() {
        var fake = new InMemoryAccountRepository();
        fake.save(/*seed*/);
        return fake;
    }
}
```

Такой подход также известен как **Consumer-Driven Contract Testing** для внутренних портов.


> [!mcq]
> - [ ] Contract test проверяет HTTP API между микросервисами через Pact | ❌ ПОСЛЕДСТВИЕ: подмена понятий, это external contract testing, port-level — внутренний.
> - [ ] Дублировать тесты в каждом адаптере вручную | ❌ ПОСЛЕДСТВИЕ: drift, баги fake не воспроизводятся в real, тесты расходятся.
> - [x] Один и тот же набор тестов в abstract class применяется к real (JPA) и fake (in-memory) реализациям порта; гарантирует что fake корректно имитирует real и Use Case тесты на fake надёжны; Consumer-Driven Contract Testing для внутренних портов | ✓ ПРИМЕНЯТЬ: когда используются fakes в unit-тестах. 📋 ПРАВИЛО: «один контракт — две реализации». 🔗 См. Q31
> - [ ] Достаточно только мокать через Mockito — fake не нужен | ❌ ПОСЛЕДСТВИЕ: interaction tests хрупки, проверка реального состояния невозможна.

## Q31. Как использовать fake-реализации портов вместо моков?

**Fake** (fake double) -- реальная, но упрощённая реализация порта. Альтернатива моку.

```java
// Fake репозиторий
public class InMemoryAccountRepository implements LoadAccountPort,
                                                   UpdateAccountStatePort {
    private final Map<AccountId, Account> accounts = new HashMap<>();

    @Override
    public Account loadAccount(AccountId id, LocalDateTime baseline) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    @Override
    public void updateActivities(Account account) {
        accounts.put(account.getId(), account);
    }

    // Test API
    public void given(Account account) {
        accounts.put(account.getId(), account);
    }
}
```

Использование в тесте:

```java
class SendMoneyServiceFakeTest {
    private final InMemoryAccountRepository repo = new InMemoryAccountRepository();
    private final InMemoryAccountLock lock = new InMemoryAccountLock();
    private final SendMoneyService service = new SendMoneyService(
        repo, lock, repo, properties);

    @Test
    void transferReducesSourceBalance() {
        repo.given(Account.withBalance(new AccountId(1L), Money.of(1000)));
        repo.given(Account.withBalance(new AccountId(2L), Money.of(500)));

        service.sendMoney(new SendMoneyCommand(
            new AccountId(1L), new AccountId(2L), Money.of(300)));

        // Проверяем реальное состояние
        Account source = repo.loadAccount(new AccountId(1L), baseline());
        assertThat(source.calculateBalance()).isEqualTo(Money.of(700));
    }
}
```

**Fake vs Mock**:

| Аспект | Mock (`Mockito`) | Fake (in-memory) |
|--------|------------------|-------------------|
| **Проверка** | Interaction (метод был вызван) | State (результат в памяти) |
| **Хрупкость** | Высокая (зависит от порядка вызовов) | Низкая |
| **Читаемость** | Средняя (`then().should()...`) | Высокая (прямые assertions) |
| **Риск** | Расхождение с реальной реализацией | Баг в fake не обнаружить |
| **Лучше всего для** | Взаимодействий | Проверки бизнес-логики |

Рекомендация: для Use Case тестов **предпочитать fakes**, они лучше отражают реальное поведение. Для проверки "событие опубликовано" -- можно моки.


> [!mcq]
> - [x] Fake = реальная упрощённая реализация порта (InMemoryAccountRepository); проверяем state (balance == 700), не interactions; less хрупко, читаемее; для бизнес-логики предпочитать fakes, для "событие опубликовано" — моки; нужен contract test чтобы fake не дрифтил от real | ✓ ПРИМЕНЯТЬ: Use Case unit-тесты. 📋 ПРАВИЛО: «state > interaction». 🔗 См. Q32
> - [ ] Fake всегда лучше mock — забудьте Mockito | ❌ ПОСЛЕДСТВИЕ: для проверки "был ли вызван publish" mocks remain удобнее.
> - [ ] Fake = просто mock с replayed responses | ❌ ПОСЛЕДСТВИЕ: подмена понятий, fake имеет логику, mock — скриптованные ответы.
> - [ ] Использовать H2 как fake для JPA | ❌ ПОСЛЕДСТВИЕ: H2 != fake адаптера, это другая БД, dialect issues.

## Q32. (!) Чем Hexagonal отличается от Clean Architecture?

По сути **это один и тот же принцип** -- инверсия зависимостей от домена к инфраструктуре. Отличия в деталях:

| Аспект | Hexagonal Architecture | Clean Architecture |
|--------|-----------------------|---------------------|
| **Автор** | Alistair Cockburn (2005) | Robert C. Martin (2012) |
| **Исходное название** | Ports & Adapters | The Clean Architecture |
| **Визуализация** | Шестиугольник | Концентрические кольца |
| **Слои/зоны** | 2 стороны: driving и driven | 4 кольца: Entities, Use Cases, Interface Adapters, Frameworks |
| **Терминология** | Port, Adapter, Driving, Driven | Entity, Use Case, Presenter, Controller, Gateway |
| **Explicit Presenters** | Нет, адаптер возвращает результат Use Case-а | Явный `Presenter` для обратного преобразования |
| **Фокус** | Взаимозаменяемость адаптеров | Правило зависимостей (Dependency Rule) |

**Clean Architecture** добавляет:
- Явный **Presenter** -- инвертирует направление возврата (Use Case → Presenter → Controller), что полезно для UI-приложений
- Более детальное разделение на 4 кольца -- `Entities` отделены от `Use Cases`

**Hexagonal Architecture** более минималистичен:
- Просто "ядро + адаптеры"
- Результат Use Case возвращается напрямую
- Нет понятия "Interface Adapters" -- всё, что снаружи ядра, это адаптер

В реальной жизни их часто **смешивают**:
- Берут пакетную структуру Hexagonal (`adapter/in/web`, `adapter/out/persistence`)
- Добавляют разделение на `Entities` vs `Use Cases` из Clean

> Ответ, который понравится интервьюеру: "Hexagonal -- это Clean Architecture без обёрток. Clean -- это Hexagonal с явным Presenter-паттерном. Оба выражают Dependency Inversion Principle."


> [!mcq]
> - [ ] Это разные парадигмы: Hexagonal про порты, Clean — про слои | ❌ ПОСЛЕДСТВИЕ: ложная дихотомия, обе про DIP, отличия в деталях.
> - [ ] Clean запрещает фреймворки в любом слое, Hexagonal — нет | ❌ ПОСЛЕДСТВИЕ: ошибка, обе изолируют core от фреймворков.
> - [x] Один принцип (DIP), отличия в деталях: Hexagonal (Cockburn 2005) — 2 стороны (driving/driven), Ports & Adapters; Clean (Martin 2012) — 4 кольца (Entities, Use Cases, Interface Adapters, Frameworks) + явный Presenter для возврата к UI; на практике часто mix — пакеты от Hexagonal, разделение Entities vs Use Cases от Clean | ✓ ПРИМЕНЯТЬ: выбор стиля под проект. 📋 ПРАВИЛО: «Clean = Hexagonal + Presenter». 🔗 См. Q33
> - [ ] Hexagonal изобретён в 2012 году, Clean — в 2005 | ❌ ПОСЛЕДСТВИЕ: фактическая ошибка, наоборот.

## Q33. Чем Hexagonal отличается от Onion Architecture?

`Onion Architecture` (Jeffrey Palermo, 2008) -- ещё один вариант той же идеи.

| Аспект | Hexagonal | Onion |
|--------|-----------|-------|
| **Автор** | Alistair Cockburn (2005) | Jeffrey Palermo (2008) |
| **Визуализация** | Hexagon с портами | Луковица с 4+ кольцами |
| **Кольца** | 1 (ядро) + снаружи адаптеры | 4: Domain Model, Domain Services, Application Services, Infrastructure |
| **Акцент** | Interface boundaries (ports) | Dependency direction (inward) |
| **DI** | Явные порты-интерфейсы | Интерфейсы на границах колец |
| **UI/Test** | Driving-адаптеры симметричны | UI и Tests -- "outer rings", симметрия |

Основная идея **Onion** -- последовательные слои, где каждый внутренний слой **не знает** о внешнем. Domain Model в центре, Infrastructure снаружи.

```mermaid
graph TB
    subgraph "Onion"
        O1[Infrastructure]
        O2[Application Services]
        O3[Domain Services]
        O4[Domain Model]
    end
    O1 --> O2 --> O3 --> O4
```

**Практическая разница** с Hexagonal:
- Onion **различает** Domain Services и Application Services -- как в DDD
- Hexagonal не делает такого различия явно, но оно есть на практике
- Onion **менее явно** говорит про порты и адаптеры -- там просто "Infrastructure слой"

На практике Onion и Hexagonal трудно отличить в реальном коде. Оба -- проявление **Dependency Inversion Principle**.


> [!mcq]
> - [x] Onion (Palermo 2008) — 4 кольца (Domain Model, Domain Services, Application Services, Infrastructure); явное разделение Domain Service vs Application Service как в DDD; less explicit про порты; в реальном коде с Hexagonal трудно отличить, оба DIP | ✓ ПРИМЕНЯТЬ: при сильном DDD focus. 📋 ПРАВИЛО: «Onion = Hexagonal с явным DDD». 🔗 См. Q34
> - [ ] Onion это синоним Layered, просто другая визуализация | ❌ ПОСЛЕДСТВИЕ: ошибка, Layered зависит сверху-вниз, Onion инвертирует.
> - [ ] Onion запрещает порты — только direct method calls | ❌ ПОСЛЕДСТВИЕ: ошибка, Onion использует интерфейсы на границах колец.
> - [ ] Onion требует event sourcing в качестве core механизма | ❌ ПОСЛЕДСТВИЕ: фальсификация, Onion не диктует persistence стратегию.

## Q34. Сравнительная таблица: Hexagonal, Clean, Onion, Layered

| Критерий | Layered (N-tier) | Hexagonal | Onion | Clean |
|----------|------------------|-----------|-------|-------|
| **Год** | 1970-е | 2005 | 2008 | 2012 |
| **Форма** | Слои | Шестиугольник | Кольца | Кольца |
| **Направление зависимостей** | Сверху вниз | Внутрь ядра | Внутрь | Внутрь |
| **Изоляция домена** | Нет (зависит от data layer) | Да | Да | Да |
| **Явные порты** | Нет | Да | Иногда | Да |
| **Presenter** | Нет | Нет | Нет | Да |
| **Количество "зон"** | 3-4 слоя | 2 (core + adapters) | 4 кольца | 4 кольца |
| **Подходит для** | CRUD, прототипы | Сложный домен | Сложный домен | Сложный домен + UI |
| **Когда избыточно** | -- | Простой CRUD | Простой CRUD | Простой CRUD |

**Ключевое различие Layered от остальных трёх**: в Layered домен **зависит от** persistence (использует JPA-сущности напрямую). В остальных -- persistence зависит от домена (через инверсию).


> [!mcq]
> - [x] Layered (1970-е): домен зависит от persistence (JPA сущности напрямую), 3-4 слоя, для CRUD; Hexagonal/Onion/Clean — domain независим, инверсия зависимостей; Hexagonal 2 зоны (core/adapters), Onion 4 кольца, Clean 4 кольца + Presenter; все три для сложного домена | ✓ ПРИМЕНЯТЬ: выбор архитектуры под complexity. 📋 ПРАВИЛО: «CRUD → Layered, домен → Hex/Onion/Clean». 🔗 См. Q35
> - [ ] Все четыре архитектуры — одно и то же, только разные имена | ❌ ПОСЛЕДСТВИЕ: непонимание ключевой разницы Layered vs остальные (направление зависимостей).
> - [ ] Layered = Hexagonal с зеркальным направлением зависимостей | ❌ ПОСЛЕДСТВИЕ: упрощение, Layered не имеет explicit ports.
> - [ ] Clean — устаревшая Hexagonal, не используется | ❌ ПОСЛЕДСТВИЕ: ошибка, Clean активно используется и развивается.

## Q35. (!) Как эволюционно мигрировать из традиционной слоистой архитектуры?

Миграция должна быть **постепенной** -- не переписывать всё сразу. Типовой план:

**Шаг 1. Стабилизировать покрытие тестами.** Без тестов любая архитектурная миграция -- лотерея.

**Шаг 2. Выделить Bounded Contexts.** Разделить кодовую базу на feature-пакеты (пока без гексагона внутри).

**Шаг 3. Внутри одного контекста создать `domain/` пакет.**
- Перенести туда доменные сущности
- Убрать из них JPA-аннотации (создать отдельные JPA-entity в persistence-пакете)
- Добавить Mapper между доменной моделью и JPA-entity

**Шаг 4. Разделить Service на Use Cases.**
- Выделить из "жирного" `OrderService` отдельные классы: `PlaceOrderService`, `CancelOrderService`
- Определить Driving Ports: `PlaceOrderUseCase`, `CancelOrderUseCase`
- Controller вызывает порт, а не конкретный сервис

**Шаг 5. Инвертировать зависимости репозитория.**
- Определить `OrderRepository` как интерфейс в `application/port/out/`
- Переименовать существующий `@Repository` в `OrderJpaAdapter` и реализовать порт

**Шаг 6. Выделить адаптеры.**
- Controller → `adapter/in/web/`
- Persistence → `adapter/out/persistence/`
- Внешние API → `adapter/out/http/`

**Шаг 7. Внедрить ArchUnit-правила.** Чтобы регрессия не сломала структуру.

```mermaid
graph LR
    A[Layered Monolith] --> B[1. Tests Added]
    B --> C[2. Bounded Contexts]
    C --> D[3. domain/ package]
    D --> E[4. Split into Use Cases]
    E --> F[5. Invert Repository]
    F --> G[6. Move Adapters]
    G --> H[7. ArchUnit rules]
    H --> I[Hexagonal]
```

**Главное правило**: не мигрировать ради миграции. Если система работает и команда счастлива -- не трогать.


> [!mcq]
> - [ ] Сразу переписать всё с нуля по гексагональному канону | ❌ ПОСЛЕДСТВИЕ: big bang rewrite, потеря фичей, 6+ месяцев без value, fail rate 50%+.
> - [x] Постепенно: tests coverage → bounded contexts → domain/ package (убрать JPA из entity) → split Service на Use Cases → invert Repository → выделить adapters → ArchUnit rules; не мигрировать ради миграции | ✓ ПРИМЕНЯТЬ: legacy → Hexagonal. 📋 ПРАВИЛО: «strangler с тестами». 🔗 См. Q36
> - [ ] Создать новый модуль hexagonal и копировать туда классы | ❌ ПОСЛЕДСТВИЕ: два кодбейза, drift, никогда не завершается.
> - [ ] Просто переименовать пакеты в adapter/in/web, adapter/out/persistence | ❌ ПОСЛЕДСТВИЕ: cosmetic change, зависимости остались layered, ничего не изменилось.

## Q36. Как разделить "жирный" Service-класс на Use Cases?

Типичный enterprise-сервис выглядит так:

```java
@Service
@Transactional
public class OrderService {
    public Order createOrder(OrderDto dto) { ... }
    public Order updateOrder(Long id, OrderDto dto) { ... }
    public void cancelOrder(Long id, String reason) { ... }
    public Order findOrderById(Long id) { ... }
    public List<Order> findOrdersByCustomer(Long customerId) { ... }
    public void confirmPayment(Long orderId, PaymentDto payment) { ... }
    public void shipOrder(Long orderId, ShipmentDto shipment) { ... }
    // ... ещё 15 методов
}
```

Проблемы:
- 20+ методов в одном классе нарушают SRP
- Для любого метода тянутся все зависимости (`@Autowired` 15 полей)
- Unit-тесты громоздкие

**Разделение на Use Cases**:

```java
public interface PlaceOrderUseCase {
    OrderId placeOrder(PlaceOrderCommand command);
}

public interface CancelOrderUseCase {
    void cancelOrder(CancelOrderCommand command);
}

public interface ConfirmPaymentUseCase {
    void confirmPayment(ConfirmPaymentCommand command);
}

// Каждый Use Case -- отдельный класс
@UseCase
@Transactional
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final DomainEventPublisher eventPublisher;

    @Override
    public OrderId placeOrder(PlaceOrderCommand cmd) { ... }
}
```

Query-операции отделяются в **Query-сервисы**:

```java
public interface GetOrderQuery {
    OrderDetailsView getOrder(OrderId id);
}

@Service
@Transactional(readOnly = true)
public class GetOrderQueryService implements GetOrderQuery {
    private final OrderReadModelRepository readRepo;

    @Override
    public OrderDetailsView getOrder(OrderId id) {
        return readRepo.findDetailsView(id);
    }
}
```

Правило Хомбергса: **один класс -- один публичный метод**. Это предельный случай, но он приводит к чистейшей архитектуре.


> [!mcq]
> - [x] Каждый метод OrderService → отдельный Use Case interface (PlaceOrderUseCase, CancelOrderUseCase, ConfirmPaymentUseCase) + impl class; query-операции в отдельные Query сервисы (GetOrderQuery, readOnly tx); правило Хомбергса — один класс, один публичный метод | ✓ ПРИМЕНЯТЬ: рефакторинг fat services. 📋 ПРАВИЛО: «один Use Case — один файл». 🔗 См. Q37
> - [ ] Оставить толстый Service — рефакторить вредно | ❌ ПОСЛЕДСТВИЕ: SRP нарушен, любой метод тянет 15 зависимостей, тесты громоздкие.
> - [ ] Разделить только по CRUD: один Create, один Read, один Update | ❌ ПОСЛЕДСТВИЕ: грубое разделение, бизнес-операции (placeOrder ≠ updateOrder) смешиваются.
> - [ ] Использовать @Qualifier для разных аспектов одного класса | ❌ ПОСЛЕДСТВИЕ: хак, класс всё ещё толстый, проблемы те же.

## Q37. Как отделить JPA-сущность от доменной модели при миграции?

Типичное состояние "до":
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue private Long id;
    @Column private String status;
    @OneToMany(cascade = ALL) private List<OrderLine> lines;
    public void addLine(Long productId, int qty) { ... }
    // JPA-сущность + доменная логика в одном классе
}
```

Проблемы: домен связан с `jakarta.persistence`; нельзя запустить без Hibernate; доменная логика захламлена JPA-деталями (lazy loading, dirty checking).

Путь разделения:

**Шаг 1. Создать чистую доменную модель** в `domain/`:
```java
public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderLine> lines = new ArrayList<>();

    public void addLine(Product product, int quantity) { ... }
    public void submit() { ... }
}
```

**Шаг 2. Переименовать старую JPA-сущность** в `OrderJpaEntity`:
```java
@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    @Id private Long id;
    @Column private String status;
    @OneToMany(cascade = ALL) private List<OrderLineJpaEntity> lines;
    // только геттеры/сеттеры, никакой логики
}
```

**Шаг 3. Mapper между двумя моделями**:
```java
@Component
public class OrderJpaMapper {
    public OrderJpaEntity toJpa(Order domain) { ... }
    public Order toDomain(OrderJpaEntity jpa) { ... }
}
```

**Шаг 4. Адаптер порта использует mapper**:
```java
@Component
class OrderPersistenceAdapter implements OrderRepository {
    private final OrderJpaRepository jpaRepo;
    private final OrderJpaMapper mapper;

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = mapper.toJpa(order);
        jpaRepo.save(entity);
    }
}
```

**Трейдоффы**:
- **Плюс**: чистая доменная модель, легко тестировать
- **Плюс**: свобода эволюции схемы БД без изменения домена
- **Минус**: дублирование (domain + JPA entity)
- **Минус**: риск рассинхрона при изменении одной стороны

**Альтернатива** (компромисс): использовать JPA-сущности **и** как доменные модели, но с `@Embeddable` для Value Objects и без бизнес-логики в отдельных сервисах. Это **прагматичный DDD**, но уже не "чистый" гексагон.


> [!mcq]
> - [x] Создать чистый domain Order (без @Entity), переименовать существующую в OrderJpaEntity (только getters/setters), OrderJpaMapper между двумя, адаптер OrderPersistenceAdapter использует mapper и реализует port; trade-off — дублирование vs чистый домен, альтернатива — pragmatic DDD с @Embeddable | ✓ ПРИМЕНЯТЬ: чистое разделение. 📋 ПРАВИЛО: «JPA-entity ≠ domain». 🔗 См. Q38
> - [ ] Оставить JPA-сущности как domain — Spring Data сам сделает магию | ❌ ПОСЛЕДСТВИЕ: домен связан с jakarta.persistence, lazy loading в логике, нельзя тестировать без Hibernate.
> - [ ] Использовать только @Embeddable Value Objects, остальное игнорировать | ❌ ПОСЛЕДСТВИЕ: половинчатое решение, основная бизнес-логика всё ещё в JPA entity.
> - [ ] Полностью отказаться от JPA, использовать raw JDBC | ❌ ПОСЛЕДСТВИЕ: теряем productivity, dirty checking, custom mapping для каждой таблицы — overkill.

## Q38. (!) Какие антипаттерны чаще всего встречаются в гексагональной архитектуре?

Топ-10 антипаттернов:

1. **Адаптер обходит Use Case** -- Controller вызывает репозиторий напрямую. Ядро теряет контроль.
2. **"Жирный" порт** -- интерфейс с 20 методами, где Use Case использует только 2 (нарушение ISP).
3. **Домен зависит от инфраструктуры** -- `@Entity`, `@Column`, `Jackson`-аннотации в доменных моделях.
4. **Анемичная модель** -- агрегаты превращаются в DTO с getter/setter, вся логика в сервисах.
5. **Утечка JPA-entity через порт** -- `LoadAccountPort.load()` возвращает `AccountJpaEntity` вместо `Account`.
6. **`Service` вместо Use Case** -- один `@Service` с 25 методами не имеет выделенных Use Case интерфейсов.
7. **Магическая публикация событий** -- `@DomainEvents` через Spring Data, логика "где-то там".
8. **Отсутствие ACL на границах контекстов** -- внешние DTO проникают в доменную модель.
9. **Транзакция в адаптере** -- `@Transactional` на Controller или JPA-репозитории.
10. **Нет ArchUnit** -- архитектурные правила держатся на честном слове, "устная договорённость".

Пример **утечки абстракции**:

```java
// АНТИПАТТЕРН: Port возвращает JPA entity!
public interface LoadAccountPort {
    AccountJpaEntity loadAccount(Long id);  // утечка инфраструктуры
}

// Use Case теперь зависит от JPA
public class SendMoneyService {
    AccountJpaEntity entity = loadAccount.loadAccount(1L);
    // дальше работаем с JPA-сущностью в бизнес-логике!
}
```

Правильно:

```java
public interface LoadAccountPort {
    Account loadAccount(AccountId id);  // доменная модель
}
```


> [!mcq]
> - [x] Adapter bypasses Use Case (Controller → Repository), fat port (ISP), domain depends on @Entity/@Column/Jackson, anemic model, JPA entity leaks through port, fat Service вместо Use Cases, magic @DomainEvents, no ACL on context boundaries, @Transactional в Controller, нет ArchUnit | ✓ ПРИМЕНЯТЬ: code review checklist. 📋 ПРАВИЛО: «10 типичных грехов». 🔗 См. Q39
> - [ ] Антипаттернов нет — Hexagonal самодостаточен | ❌ ПОСЛЕДСТВИЕ: команда не видит проблем, регрессии копятся.
> - [ ] Главный антипаттерн — слишком много адаптеров | ❌ ПОСЛЕДСТВИЕ: подмена тезиса, реальные проблемы — утечки и обход Use Case.
> - [ ] Использовать Lombok — это анти-DDD | ❌ ПОСЛЕДСТВИЕ: фокус не там, Lombok orthogonal к hexagonal architecture.

## Q39. "Анемичная" доменная модель в гексагоне -- как избежать?

**Анемичная модель** (Martin Fowler) -- объекты с только getter/setter, вся логика в сервисах.

```java
// АНЕМИЧНО
public class Order {
    private Long id;
    private OrderStatus status;
    private List<OrderLine> lines;
    // только геттеры/сеттеры
}

@Service
public class OrderService {
    public void submit(Order order) {
        if (order.getLines().isEmpty()) {
            throw new EmptyOrderException();
        }
        order.setStatus(OrderStatus.SUBMITTED);  // прямая мутация
    }
}
```

В гексагоне эта проблема **усиливается** -- Use Case может превратиться в "скрытый Service", который манипулирует анемичными доменами.

**Как избежать**:

1. **Логика -- в агрегате**:
```java
public class Order {
    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException(id);
        }
        this.status = OrderStatus.SUBMITTED;
        this.domainEvents.add(new OrderSubmitted(id));
    }
}
```

2. **Use Case только оркестрирует**:
```java
@UseCase
@Transactional
public class SubmitOrderService implements SubmitOrderUseCase {
    @Override
    public void submitOrder(OrderId orderId) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.submit();  // <-- логика в домене
        orderRepo.save(order);
        eventPublisher.publish(order.pullDomainEvents());
    }
}
```

3. **Запрещаем setter-ы в домене** -- только методы с доменными глаголами (`submit()`, `cancel()`, `addLine()`).

4. **Value Objects** для любых концепций (Money, Email, PostalCode) -- не голый `String`.

5. **Immutability** -- поля `final`, методы возвращают новый объект вместо мутации.


> [!mcq]
> - [ ] Анемичная модель — это норма, логика в сервисах удобнее тестировать | ❌ ПОСЛЕДСТВИЕ: violates DDD, агрегаты становятся DTOs, инварианты не защищены.
> - [x] Логика в агрегате (order.submit()), Use Case только оркестрирует (findById → submit → save → publish); запретить setters в домене, только доменные глаголы; Value Objects (Money, Email) вместо String; immutability с final | ✓ ПРИМЕНЯТЬ: rich domain model. 📋 ПРАВИЛО: «логика идёт к данным». 🔗 См. Q40
> - [ ] Использовать record вместо class — это автоматически решает anemic | ❌ ПОСЛЕДСТВИЕ: record без бизнес-методов = анемия в другой обёртке.
> - [ ] Анемичная модель неизбежна в JPA — смиритесь | ❌ ПОСЛЕДСТВИЕ: отказ от rich domain, нет преимуществ DDD, гексагон бессмысленен.

## Q40. Утечка доменной модели через адаптер -- как это происходит и как предотвратить?

**Утечка абстракции** -- ситуация, когда инфраструктурная деталь протекает в домен.

Типовые пути утечки:

**Утечка 1. Json-аннотации в домене**
```java
// АНТИПАТТЕРН
public class Order {
    @JsonProperty("order_id")  // Jackson знает про Order
    private OrderId id;
}
```
Правильно: DTO с `@JsonProperty` в `adapter/in/web/`, mapper между ними.

**Утечка 2. JPA-аннотации в Value Object**
```java
// АНТИПАТТЕРН
public class Money {
    @Column(name = "amount")  // JPA знает про Money
    private BigDecimal amount;
}
```
Правильно: `@Embeddable` в **JPA-сущности**, а Value Object -- чистая Java-запись.

**Утечка 3. Spring-специфичные исключения в домене**
```java
public class OrderService {
    public Order load(OrderId id) {
        try { ... }
        catch (DataAccessException e) {  // Spring-специфичное!
            throw new OrderException(e);
        }
    }
}
```
Правильно: адаптер ловит `DataAccessException` и бросает доменное `OrderNotFoundException`.

**Утечка 4. HTTP-статусы в Use Case**
```java
public interface PlaceOrderUseCase {
    ResponseEntity<OrderDto> placeOrder(PlaceOrderCommand cmd);  // HTTP в ядре!
}
```
Правильно: порт возвращает доменный `Result<OrderId>` или `OrderId` (или бросает исключение), HTTP-маппинг -- в адаптере.

**Утечка 5. JPA-сущность возвращается из репозитория**
```java
public interface OrderRepository {
    OrderJpaEntity findById(Long id);  // утечка JPA
}
```
Правильно: возвращать `Order` (доменную модель).

**Как предотвратить**:
- **ArchUnit-правила** против `jakarta.persistence.*`, `org.springframework.*` в `domain/`
- **Code review** с фокусом на границы
- **Multi-module проект** -- `domain` не видит Spring вообще


> [!mcq]
> - [x] Пути утечки: @JsonProperty в domain, @Column в Value Object, Spring DataAccessException в сервисе, ResponseEntity<...> из Use Case, OrderJpaEntity из Repository; защита — ArchUnit правила против jakarta.persistence/org.springframework в domain/, multi-module проект, code review | ✓ ПРИМЕНЯТЬ: защита границ. 📋 ПРАВИЛО: «ArchUnit или ничего». 🔗 См. Q41
> - [ ] Утечка не страшна, главное чтобы тесты проходили | ❌ ПОСЛЕДСТВИЕ: framework changes ломают домен, перевод на новую БД = переписывать ядро.
> - [ ] Достаточно review без ArchUnit — программисты знают правила | ❌ ПОСЛЕДСТВИЕ: усталость code reviewer, regression через 6 месяцев гарантирован.
> - [ ] @PackageVisibility и naming convention достаточно | ❌ ПОСЛЕДСТВИЕ: правила держатся на честном слове, любой `import` ломает.

## Q41. Когда Hexagonal Architecture -- это overkill?

Гексагональная архитектура даёт преимущества **в сложных системах**. Для простых проектов она только добавляет сложность.

**Не стоит применять**:

- **Простой CRUD без бизнес-логики** -- "таблица → API". Дополнительные слои и Mapper-ы -- чистые накладные расходы.
- **Прототип / MVP** -- быстрее написать по слоистой архитектуре, рефакторинг сделать позже.
- **Маленький сервис (<5000 строк)** -- команда из 2-3 разработчиков, короткий жизненный цикл.
- **Очень короткоживущий код** -- скрипты, миграции, одноразовые интеграции.
- **Команда новичков** -- кривая обучения высока, плохие реализации хуже слоистой архитектуры.

**Стоит применять**:

- **Сложная бизнес-логика** -- финансы, страхование, e-commerce
- **Долгоживущая система** -- 5+ лет в продакшене
- **Команды 5+ разработчиков** -- нужны строгие границы
- **Несколько каналов входа** (REST + Kafka + CLI + Scheduler) -- ключевая выгода от портов
- **Замена инфраструктуры ожидается** -- миграция с PostgreSQL на DynamoDB, с Kafka на SQS

> Правило Fowler: "Сложность архитектуры должна соответствовать сложности задачи". Гексагон -- это не "правильный" способ, а один из инструментов.


> [!mcq]
> - [ ] Hexagonal обязателен для любого Java-проекта | ❌ ПОСЛЕДСТВИЕ: для простого CRUD overhead, mapper-ы и слои без value.
> - [ ] Hexagonal не нужен никогда — слоистая архитектура достаточна | ❌ ПОСЛЕДСТВИЕ: для сложного домена пишем coupled код, через 2 года рефакторинг невозможен.
> - [x] Overkill для: простой CRUD, MVP/прототип, маленький сервис (<5000 LOC), короткоживущий код (миграции/скрипты), команда новичков; нужен для: сложная бизнес-логика, долгоживущая система 5+ лет, команды 5+ инженеров, multiple input channels (REST+Kafka+CLI), ожидаемая смена инфры | ✓ ПРИМЕНЯТЬ: решение о применении. 📋 ПРАВИЛО: «сложность ≈ сложности задачи (Fowler)». 🔗 См. Q42
> - [ ] Решение по lines of code: больше 1000 — Hexagonal, меньше — нет | ❌ ПОСЛЕДСТВИЕ: метрика не отражает сложности домена, не учитывает team size.

## Q42. Как проверять правила гексагональной архитектуры через ArchUnit?

**ArchUnit** -- библиотека для JUnit, позволяющая тестировать архитектурные правила.

```java
@AnalyzeClasses(packages = "com.company.bookkeeper",
                importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplicationOrAdapter =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnAdapter =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule domainHasNoSpringDependencies =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "com.fasterxml.jackson..");

    @ArchTest
    static final ArchRule useCasesImplementDrivingPorts =
        classes().that().resideInAPackage("..application.service..")
            .and().haveSimpleNameEndingWith("Service")
            .should().implement(
                JavaClass.Predicates.resideInAPackage(
                    "..application.port.in.."));

    @ArchTest
    static final ArchRule controllersOnlyCallUseCases =
        noClasses().that().resideInAPackage("..adapter.in.web..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule adapterOutImplementsOutPorts =
        classes().that().resideInAPackage("..adapter.out.persistence..")
            .and().areAnnotatedWith(Component.class)
            .should().implement(
                JavaClass.Predicates.resideInAPackage(
                    "..application.port.out.."));
}
```

Для более сложных правил используется DSL `layeredArchitecture()`:

```java
@ArchTest
static final ArchRule layers = layeredArchitecture()
    .consideringAllDependencies()
    .layer("Domain").definedBy("..domain..")
    .layer("Application").definedBy("..application..")
    .layer("Adapter").definedBy("..adapter..")
    .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
    .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter");
```

Запускается как обычный JUnit-тест. Нарушение правил → падение сборки в CI.


> [!mcq]
> - [x] @ArchTest правила: domain не зависит от application/adapter, application не зависит от adapter, domain без Spring/JPA/Jackson; useCases implement ports.in; controllers не вызывают adapter.out; layeredArchitecture() DSL; запуск как JUnit-тест, нарушение = fail CI | ✓ ПРИМЕНЯТЬ: enforcement правил в CI. 📋 ПРАВИЛО: «правила в коде, не в head». 🔗 См. Q43
> - [ ] Достаточно ESLint-style правил для пакетов | ❌ ПОСЛЕДСТВИЕ: Java не имеет ESLint, нужен ArchUnit или JDepend.
> - [ ] Code review достаточно — ArchUnit это lock-in | ❌ ПОСЛЕДСТВИЕ: human enforcement fails, ArchUnit это просто JUnit-тесты, никакого lock-in.
> - [ ] Использовать Maven enforcer plugin для package structure | ❌ ПОСЛЕДСТВИЕ: enforcer проверяет POM, не классовые зависимости.

## Q43. (!) Как применять гексагональную архитектуру в микросервисах?

В микросервисной среде **каждый сервис может быть отдельным гексагоном**. Это хорошо сочетается с:
- **Bounded Context per service** (подход DDD)
- **Database per service**
- **API contracts**

```mermaid
graph LR
    subgraph "Order Service Hexagon"
        O_IN[REST/Kafka In]
        O_CORE[Order Core]
        O_OUT[JPA/Kafka Out]
    end
    subgraph "Payment Service Hexagon"
        P_IN[REST/Kafka In]
        P_CORE[Payment Core]
        P_OUT[JPA/Stripe Out]
    end
    subgraph "Inventory Service Hexagon"
        I_IN[REST/Kafka In]
        I_CORE[Inventory Core]
        I_OUT[JPA Out]
    end
    O_OUT -->|Kafka event| P_IN
    O_OUT -->|HTTP| I_IN
    P_OUT -->|Stripe| EXT[External]
```

**Что даёт гексагон в микросервисах**:
1. **Независимая эволюция транспорта** -- можно перейти с REST на gRPC без изменения домена
2. **Тестируемость** -- интеграционные тесты не требуют запуска всего стека, только адаптеров
3. **ACL на границах** -- secondary-адаптеры скрывают форматы внешних сервисов (не утекает DTO в домен)
4. **Consumer-Driven Contracts** -- порты легко покрыть контрактными тестами (Pact, Spring Cloud Contract)

**Практические рекомендации**:
- **Один сервис = один гексагон** -- не плодить модули внутри микросервиса
- **Общая доменная модель только внутри одного сервиса** -- межсервисно общаются через DTO/Events
- **Shared library** с доменной моделью между микросервисами -- антипаттерн (скрытая связанность)
- **Для простых CRUD-микросервисов** гексагон может быть избыточен -- достаточно тонкого слоя


> [!mcq]
> - [x] Один сервис = один гексагон (Bounded Context per service + DB per service); даёт независимую эволюцию транспорта (REST→gRPC), тестируемость, ACL на границах, Consumer-Driven Contracts (Pact); shared library с domain между сервисами — антипаттерн (скрытая связанность); для простых CRUD micro-сервисов может быть избыточен | ✓ ПРИМЕНЯТЬ: микросервисы со сложной логикой. 📋 ПРАВИЛО: «гексагон внутри, контракт снаружи». 🔗 См. Q44
> - [ ] Один гексагон на всю систему — общая core | ❌ ПОСЛЕДСТВИЕ: distributed monolith, shared library coupling, нет независимого деплоя.
> - [ ] Гексагон несовместим с микросервисами — выберите одно | ❌ ПОСЛЕДСТВИЕ: ложная дилемма, они на разных уровнях — внутренняя структура vs топология.
> - [ ] Несколько гексагонов внутри одного сервиса — модульность | ❌ ПОСЛЕДСТВИЕ: over-engineering, гексагон уже даёт модульность через ports.

## Q44. Как организовать обработку ошибок между доменом и адаптером?

Принцип: **доменные исключения** -- в домене, **технические исключения** -- в адаптерах.

**Шаг 1. Доменные исключения -- чистая Java, в `domain/`**:

```java
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(AccountId accountId, Money requested,
                                      Money available) {
        super(String.format(
            "Insufficient funds on account %s: requested %s, available %s",
            accountId.value(), requested, available));
    }
}

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId id) {
        super("Order not found: " + id.value());
    }
}
```

**Шаг 2. Use Case бросает доменные исключения**:

```java
@Override
public boolean sendMoney(SendMoneyCommand cmd) {
    Account source = loadAccount.loadAccount(cmd.sourceId());
    if (!source.withdraw(cmd.amount(), cmd.targetId())) {
        throw new InsufficientFundsException(
            source.getId(), cmd.amount(), source.calculateBalance());
    }
    // ...
}
```

**Шаг 3. Primary адаптер преобразует в HTTP**:

```java
@ControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDto> handle(InsufficientFundsException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorDto("INSUFFICIENT_FUNDS", e.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDto> handle(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorDto("ORDER_NOT_FOUND", e.getMessage()));
    }
}
```

**Шаг 4. Secondary адаптер ловит технические и превращает в доменные**:

```java
@Override
public Account loadAccount(AccountId id) {
    try {
        return accountRepo.findById(id.value())
            .map(mapper::toDomain)
            .orElseThrow(() -> new AccountNotFoundException(id));
    } catch (DataAccessException e) {
        // Технические ошибки превращаем в доменные или инфраструктурные
        throw new PersistenceUnavailableException("Database unavailable", e);
    }
}
```

**Альтернатива: Result type** (вместо исключений):

```java
public sealed interface SendMoneyResult {
    record Success(TransferId id) implements SendMoneyResult {}
    record InsufficientFunds(Money available) implements SendMoneyResult {}
    record AccountNotFound(AccountId id) implements SendMoneyResult {}
}

public interface SendMoneyUseCase {
    SendMoneyResult sendMoney(SendMoneyCommand cmd);
}
```

Плюсы Result type: явный контракт ошибок, нет скрытого потока управления через исключения. Минус: более громоздкий код.


> [!mcq]
> - [x] Domain exceptions в domain/ (InsufficientFundsException, OrderNotFoundException, чистая Java); Use Case бросает доменные; Primary adapter (@ControllerAdvice) превращает в HTTP (422, 404 + ErrorDto); Secondary adapter ловит DataAccessException и превращает в PersistenceUnavailableException; альтернатива — sealed Result type для явного контракта ошибок | ✓ ПРИМЕНЯТЬ: error handling в гексагоне. 📋 ПРАВИЛО: «доменные — внутри, HTTP — снаружи». 🔗 См. Q45
> - [ ] Кидать DataAccessException из Use Case — Spring сам поймает | ❌ ПОСЛЕДСТВИЕ: домен зависит от Spring, leak абстракции.
> - [ ] Использовать RuntimeException везде — не различать | ❌ ПОСЛЕДСТВИЕ: HTTP-handler не знает что вернуть, всё становится 500.
> - [ ] HTTP-статусы прямо в Use Case через ResponseEntity | ❌ ПОСЛЕДСТВИЕ: HTTP в ядре, нельзя использовать Use Case из Kafka/CLI.

## Q45. Реактивность (WebFlux, R2DBC) и гексагональная архитектура: что меняется?

Гексагональная архитектура **ортогональна** реактивности -- базовые принципы не меняются, но типы портов становятся реактивными.

**Изменения в Driving Port**:

```java
// Синхронный
public interface PlaceOrderUseCase {
    OrderId placeOrder(PlaceOrderCommand command);
}

// Реактивный
public interface PlaceOrderUseCase {
    Mono<OrderId> placeOrder(PlaceOrderCommand command);
}
```

**Изменения в Driven Port**:

```java
// Синхронный
public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    void save(Order order);
}

// Реактивный
public interface OrderRepository {
    Mono<Order> findById(OrderId id);
    Mono<Void> save(Order order);
}
```

**Use Case компонует Mono/Flux**:

```java
@Override
public Mono<OrderId> placeOrder(PlaceOrderCommand cmd) {
    return customerRepo.findById(cmd.customerId())
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(cmd.customerId())))
        .flatMap(customer -> Flux.fromIterable(cmd.lines())
            .flatMap(l -> productRepo.findById(l.productId())
                .map(p -> new LineWithProduct(l, p)))
            .collectList()
            .map(lines -> Order.create(customer, lines)))
        .flatMap(order -> {
            order.place();
            return orderRepo.save(order).thenReturn(order.getId());
        });
}
```

**Что остаётся неизменным**:
- Структура пакетов
- Разделение на Domain / Application / Adapter
- ArchUnit-правила
- Principle "домен не знает об инфраструктуре"

**Что усложняется**:
- Транзакции (`@Transactional` не работает с `Mono`, нужен `TransactionalOperator`)
- Тестирование -- используется `StepVerifier` вместо простых assertions
- Доменная логика может "просочиться" -- нужно следить, чтобы `Mono.map()` и `.flatMap()` не содержали бизнес-правил

**Совет**: если доменная модель **не** реактивна (обычная), а Use Case реактивен -- это нормально. Реактивность нужна на границах (I/O), но сама бизнес-логика может оставаться синхронной внутри `.map()`/`.flatMap()`.


> [!mcq]
> - [ ] Реактивность ломает Hexagonal — нужно переписывать ядро | ❌ ПОСЛЕДСТВИЕ: ложное утверждение, гексагон ортогонален реактивности.
> - [x] Hexagonal ортогонален реактивности — типы портов меняются (Mono<OrderId>, Mono<Order>), структура пакетов и ArchUnit-правила те же; усложняется транзакционность (нужен TransactionalOperator), тесты через StepVerifier; доменная логика может оставаться синхронной внутри map/flatMap | ✓ ПРИМЕНЯТЬ: WebFlux + R2DBC проекты. 📋 ПРАВИЛО: «реактивность на границах, логика обычная». 🔗 См. See also
> - [ ] Реактивность требует event sourcing — другой паттерн | ❌ ПОСЛЕДСТВИЕ: ложная связь, реактивность и ES независимы.
> - [ ] @Transactional работает с Mono — ничего не меняется | ❌ ПОСЛЕДСТВИЕ: фактическая ошибка, для реактивных tx нужен TransactionalOperator.

---

## See also

- [Clean Architecture](clean-architecture-interview.md) -- концепция, пересекающаяся с Hexagonal; Dependency Rule, Use Case Interactors, Interface Adapters
- [Domain-Driven Design](ddd-interview.md) -- тактические и стратегические паттерны DDD, агрегаты, Bounded Context -- естественный компаньон гексагона
- [Микросервисы](microservices-interview.md) -- применение гексагональной архитектуры в микросервисной среде, сервис как гексагон
- [Распределённые системы](distributed-systems-interview.md) -- адаптеры как границы надёжности, Circuit Breaker на Driven Port
- [CQRS и Event Sourcing](cqrs-event-sourcing-interview.md) -- разделение Query и Command Use Cases в гексагоне
- [Event-driven паттерны](event-driven-patterns-interview.md) -- доменные события через Driven Port, Transactional Outbox
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) -- Retry, Circuit Breaker и Fallback в Secondary адаптерах
- [Паттерны проектирования](../design-patterns/design-patterns-interview.md) -- SOLID, Dependency Inversion, Adapter и Strategy в основе гексагона
- [Паттерны масштабируемости](scalability-patterns-interview.md) -- горизонтальное масштабирование гексагональных сервисов
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) -- практическая реализация гексагональной архитектуры со Spring IoC, Spring Data, Spring Web
- [Шпаргалка: Hexagonal Architecture (Ports & Adapters](../../architecture/hexagonal-architecture.md) — теория
