---
title: "Вопросы на собеседовании: Domain-Driven Design"
description: "Полное покрытие DDD: Bounded Context, Ubiquitous Language, агрегаты, Entity vs Value Object, доменные события, репозитории, стратегический и тактический дизайн, CQRS, Event Sourcing, Saga, Specification."
tags:
  - interview
  - architecture
  - ddd-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Domain-Driven Design"
  - "DDD interview"
  - "DDD собеседование"
prerequisites:
  - "[[ddd]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Domain-Driven Design`

Полное покрытие `Domain-Driven Design` (`DDD`): `Bounded Context`, `Ubiquitous Language`, агрегаты, `Entity` vs `Value Object`, доменные события, репозитории, стратегический и тактический дизайн, `CQRS`, `Event Sourcing`, `Saga`, `Specification`.

**Domain-Driven Design** -- подход к проектированию сложных систем, предложенный Эриком Эвансом в 2003 году. На собеседованиях `DDD` особенно актуален для Senior-позиций: вопросы охватывают как стратегическое разделение системы на контексты, так и тактические паттерны моделирования домена. Знание `DDD` показывает способность проектировать системы, отражающие реальную бизнес-логику.

## Полезные ссылки

### Официальная документация

- [Domain-Driven Design Reference (Eric Evans)](https://www.domainlanguage.com/ddd/reference/) -- краткий справочник от автора DDD
- [Bounded Context (Martin Fowler)](https://martinfowler.com/bliki/BoundedContext.html) -- каноническое описание Bounded Context
- [DDD Bounded Contexts and Java Modules (Baeldung)](https://www.baeldung.com/java-modules-ddd-bounded-contexts) -- Bounded Context с Java-модулями
- [Hexagonal Architecture, DDD, and Spring (Baeldung)](https://www.baeldung.com/hexagonal-architecture-ddd-spring) -- гексагональная архитектура и DDD в Spring
- [DDD Aggregates and @DomainEvents (Baeldung)](https://www.baeldung.com/spring-data-ddd) -- агрегаты и доменные события в Spring Data
- [Persisting DDD Aggregates (Baeldung)](https://www.baeldung.com/spring-persisting-ddd-aggregates) -- персистенция агрегатов
- [CQRS and Event Sourcing in Java (Baeldung)](https://www.baeldung.com/cqrs-event-sourcing-java) -- CQRS и Event Sourcing
- [Implementing CQRS with Spring Modulith (Baeldung)](https://www.baeldung.com/spring-modulith-cqrs) -- CQRS через Spring Modulith
- [Strategic Domain-Driven Design with Context Mapping (InfoQ)](https://www.infoq.com/articles/ddd-contextmapping/) -- стратегический DDD и контекстное картирование

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы DDD**
- [Q1. (!) Что такое Domain-Driven Design и зачем он нужен?](#q1--что-такое-domain-driven-design-и-зачем-он-нужен)
- [Q2. (!) Что такое Ubiquitous Language?](#q2--что-такое-ubiquitous-language)
- [Q3. Какие проблемы решает DDD?](#q3-какие-проблемы-решает-ddd)
- [Q4. Когда стоит и не стоит применять DDD?](#q4-когда-стоит-и-не-стоит-применять-ddd)

**Стратегический дизайн**
- [Q5. (!) Что такое Bounded Context?](#q5--что-такое-bounded-context)
- [Q6. Что такое Context Mapping и какие паттерны интеграции существуют?](#q6-что-такое-context-mapping-и-какие-паттерны-интеграции-существуют)
- [Q7. Что такое Anti-Corruption Layer?](#q7-что-такое-anti-corruption-layer)
- [Q8. Что такое Shared Kernel?](#q8-что-такое-shared-kernel)
- [Q9. В чём разница между стратегическим и тактическим DDD?](#q9-в-чём-разница-между-стратегическим-и-тактическим-ddd)
- [Q10. Что такое Subdomain и какие типы бывают?](#q10-что-такое-subdomain-и-какие-типы-бывают)

**Entity и Value Object**
- [Q11. (!) В чём разница между Entity и Value Object?](#q11--в-чём-разница-между-entity-и-value-object)
- [Q12. Как правильно реализовать Value Object в Java?](#q12-как-правильно-реализовать-value-object-в-java)
- [Q13. Когда использовать Entity, а когда Value Object?](#q13-когда-использовать-entity-а-когда-value-object)

**Агрегаты**
- [Q14. (!) Что такое Aggregate и Aggregate Root?](#q14--что-такое-aggregate-и-aggregate-root)
- [Q15. Какие правила проектирования агрегатов?](#q15-какие-правила-проектирования-агрегатов)
- [Q16. Как определить границы агрегата?](#q16-как-определить-границы-агрегата)
- [Q17. Как реализовать Aggregate Root в Java/Spring?](#q17-как-реализовать-aggregate-root-в-javaspring)

**Доменные события**
- [Q18. Что такое Domain Event?](#q18-что-такое-domain-event)
- [Q19. Как реализовать доменные события в Spring?](#q19-как-реализовать-доменные-события-в-spring)
- [Q20. В чём разница между Domain Event и Integration Event?](#q20-в-чём-разница-между-domain-event-и-integration-event)

**Репозитории и сервисы**
- [Q21. (!) Что такое Repository в DDD?](#q21--что-такое-repository-в-ddd)
- [Q22. (!) В чём разница между Domain Service и Application Service?](#q22--в-чём-разница-между-domain-service-и-application-service)
- [Q23. Что такое Factory в DDD?](#q23-что-такое-factory-в-ddd)

**CQRS и Event Sourcing**
- [Q24. (!) Что такое CQRS?](#q24--что-такое-cqrs)
- [Q25. (!) Что такое Event Sourcing?](#q25--что-такое-event-sourcing)
- [Q26. Как связаны CQRS и Event Sourcing?](#q26-как-связаны-cqrs-и-event-sourcing)

**Saga и распределённые транзакции**
- [Q27. (!) Что такое Saga Pattern в контексте DDD?](#q27--что-такое-saga-pattern-в-контексте-ddd)
- [Q28. В чём разница между хореографией и оркестрацией в Saga?](#q28-в-чём-разница-между-хореографией-и-оркестрацией-в-saga)

**Specification Pattern**
- [Q29. Что такое Specification Pattern?](#q29-что-такое-specification-pattern)

**DDD с Spring и Java**
- [Q30. Как организовать пакетную структуру проекта по DDD?](#q30-как-организовать-пакетную-структуру-проекта-по-ddd)
- [Q31. Как DDD сочетается с гексагональной архитектурой?](#q31-как-ddd-сочетается-с-гексагональной-архитектурой)
- [Q32. Как использовать Spring Modulith для реализации DDD?](#q32-как-использовать-spring-modulith-для-реализации-ddd)
- [Q33. Какие анти-паттерны встречаются при реализации DDD?](#q33-какие-анти-паттерны-встречаются-при-реализации-ddd)
- [Q34. (!) Как связаны DDD и микросервисы?](#q34--как-связаны-ddd-и-микросервисы)
- [Q35. Как тестировать доменную модель?](#q35-как-тестировать-доменную-модель)

**Продвинутые темы**
- [Q36. (!) Как реализовать Anti-Corruption Layer (ACL) в Spring?](#q36--как-реализовать-anti-corruption-layer-acl-в-spring)
- [Q37. Какие стратегии Context Mapping применяются на практике?](#q37-какие-стратегии-context-mapping-применяются-на-практике)
- [Q38. (!) Как правильно проектировать Value Objects в Java?](#q38--как-правильно-проектировать-value-objects-в-java)

---

## Q1. (!) Что такое Domain-Driven Design и зачем он нужен?

**Domain-Driven Design** (`DDD`) -- это подход к проектированию, при котором структура кода повторяет структуру бизнес-домена, а не технические решения. Главный приоритет -- сложность самого бизнеса, а не базы данных или фреймворка. Подход описал Эрик Эванс в книге "Domain-Driven Design: Tackling Complexity in the Heart of Software" (2003).

В основе лежат четыре идеи, и каждая отвечает на конкретную боль:

1. **Домен в центре** -- архитектуру диктует бизнес-логика, а инфраструктура (БД, фреймворк, транспорт) -- лишь деталь на периферии. Это защищает ядро системы от смены технологий.
2. **Единый язык** (`Ubiquitous Language`) -- разработчики и бизнес используют одни и те же термины. Имена классов и методов = слова из разговора с экспертом, поэтому код не нужно «переводить».
3. **Модель как знание** -- код не просто работает, а фиксирует понимание домена; читая модель, можно изучить бизнес.
4. **Чёткие границы** -- система делится на контексты, у каждого своя модель. Это не даёт сложности расползтись по всему коду.

**Зачем нужен:** в сложных доменах это окупается тем, что:

- снижает когнитивную нагрузку -- большая система разбита на изолированные части;
- точно переносит бизнес-правила в код, без двойного толкования;
- упрощает диалог разработчиков и доменных экспертов (общий язык);
- даёт объективный критерий, как резать систему на модули и микросервисы (по границам контекстов).

> **Что хотят услышать на собеседовании**: `DDD` -- не набор паттернов, а философия проектирования. Главная мысль: код должен говорить на языке бизнеса, а структура системы -- повторять структуру домена. Паттерны (`Aggregate`, `Repository` и пр.) -- лишь инструменты для этой цели, а не самоцель.

---

## Q2. (!) Что такое Ubiquitous Language?

**Ubiquitous Language** (единый язык) -- это общий словарь разработчиков и доменных экспертов, на котором они описывают бизнес-процессы. Один и тот же язык пронизывает всё: код, документацию, устные обсуждения. Смысл -- убрать «перевод» между тем, что говорит бизнес, и тем, что написано в коде: чем меньше перевода, тем меньше ошибок понимания.

Принципы:

- **Один термин -- одно значение**: «Заказ» значит ровно одно и то же и в коде, и в разговоре с бизнесом -- никаких синонимов вроде `Order`/`Purchase`/`Deal` для одной сущности.
- **Модель отражает язык**: имена классов, методов и переменных дословно совпадают с терминами домена, а не с техническим жаргоном.
- **Язык эволюционирует**: углубляя понимание домена, команда уточняет термины -- и сразу же переименовывает их в коде. Язык и модель меняются синхронно.

Пример -- различие между техническим и доменным языком:

```java
// Плохо: технический язык
class DataProcessor {
    void processRecord(Map<String, Object> data) { ... }
}

// Хорошо: Ubiquitous Language
class LoanApplication {
    void submitForApproval(Applicant applicant, LoanTerms terms) { ... }
    void approve(CreditOfficer officer) { ... }
    void reject(RejectionReason reason) { ... }
}
```

Признаки нарушения `Ubiquitous Language`:

| Симптом | Проблема |
|---------|----------|
| Разработчики и бизнес говорят разными терминами | Код не отражает домен |
| Класс `Manager`, `Processor`, `Handler` | Размытая ответственность, нет доменного смысла |
| Комментарии объясняют бизнес-смысл кода | Код недостаточно выразителен |

---

## Q3. Какие проблемы решает DDD?

`DDD` целится в проблемы, которые возникают именно в **сложных** бизнес-доменах -- там, где правил много и они запутанны. На каждую проблему есть свой ответ:

1. **Big Ball of Mud** -- монолитный код без границ, где всё зависит от всего и любое изменение рискованно. `DDD` вводит `Bounded Context`, который изолирует подсистемы друг от друга.
2. **Разрыв между бизнесом и кодом** -- требования трактуются по-разному на словах и в коде. `Ubiquitous Language` убирает двойную интерпретацию.
3. **Анемичная модель домена** -- данные лежат в «мешках» (геттеры/сеттеры), а логика размазана по процедурным сервисам. `DDD` предлагает Rich Domain Model, где поведение живёт рядом с данными.
4. **Неконтролируемая сложность** -- система растёт и перестаёт умещаться в голове. Стратегический дизайн режет её на управляемые части с явными границами.
5. **Трудности масштабирования команд** -- несколько команд мешают друг другу в общем коде. Каждая команда владеет своим `Bounded Context` и развивает его независимо.

---

## Q4. Когда стоит и не стоит применять DDD?

Короткий ответ: `DDD` оправдан там, где сложность лежит в **бизнес-правилах**, а не в технике, и где этот код будет жить и меняться годами. Платой за `DDD` становится дополнительная структура (агрегаты, контексты, маппинги) -- она окупается только при реальной сложности домена.

| Стоит применять | Не стоит применять |
|-----------------|-------------------|
| Сложный бизнес-домен с нетривиальными правилами | CRUD-приложения без бизнес-логики |
| Долгоживущий проект с активным развитием | Прототипы и одноразовые скрипты |
| Доступ к доменным экспертам | Простые интеграционные адаптеры |
| Несколько команд работают над одной системой | Маленькие проекты одного разработчика |
| Ядро бизнеса (`Core Domain`) | Утилитарные и инфраструктурные модули |

**Эмпирическое правило (Эрик Эванс):** если бизнес-логику можно полностью описать через `CRUD`, то `DDD` будет лишним грузом. Его сложность оправдана только в доменах, где правила действительно нетривиальны. На практике `DDD` применяют точечно -- к `Core Domain`, а вспомогательные модули оставляют простыми.

---

## Q5. (!) Что такое Bounded Context?

**Bounded Context** (ограниченный контекст) -- центральный паттерн стратегического `DDD`. Это явная граница, внутри которой одна доменная модель и один `Ubiquitous Language` остаются согласованными и однозначными. За границей те же слова могут значить другое.

Зачем он нужен: попытка построить единую модель на всю компанию проваливается, потому что одно понятие в разных отделах означает разное. Здесь `Customer` -- это разные модели: для заказов важны id и имя, для доставки -- адрес, для биллинга -- платёжные данные. `Bounded Context` разрешает этот конфликт: вместо одного раздутого `Customer` -- три узких, каждый под свою задачу.

Три `Bounded Context` с собственной моделью каждого, где `Customer` выглядит по-разному:

- **Bounded Context: Заказы** -- `Order`, `OrderItem`, `Customer` (id + имя).
- **Bounded Context: Доставка** -- `Shipment`, `ShipmentItem`, `Customer` (id + адрес).
- **Bounded Context: Биллинг** -- `Invoice`, `Payment`, `Customer` (id + платёжные данные).

Связи между контекстами идут через `Integration Event`: `Order` (из «Заказов») публикует событие в «Доставку» (`Order` → `Shipment`) и в «Биллинг» (`Order` → `Invoice`).

Ключевые свойства:

1. **Одна модель -- один контекст**: `Customer` в «Заказах» и `Customer` в «Биллинге» -- это разные классы с разным составом полей, а не один общий.
2. **Лингвистическая граница**: внутри контекста язык однозначен -- каждый термин имеет ровно один смысл.
3. **Независимость моделей**: изменение модели в одном контексте не ломает другие -- они общаются через события/контракты, а не через общие классы.
4. **Техническая автономность**: у контекста может быть своя БД, свой стек, свой темп релизов.

**Как определить границы `Bounded Context`:**

- ищите места, где одни и те же слова означают разное -- это шов между контекстами;
- смотрите, какие команды над чем работают: граница контекста обычно проходит по границе команды (закон Конвея);
- проводите `Event Storming`, чтобы увидеть потоки событий и естественные разрывы в них;
- держитесь принципа «один контекст -- одна команда»: так контекст остаётся управляемым одной командой целиком.

---

## Q6. Что такое Context Mapping и какие паттерны интеграции существуют?

**Context Map** -- это карта всех `Bounded Context` системы и связей между ними: кто от кого зависит и через какой паттерн интегрируется. Она делает видимыми отношения власти (кто диктует контракт) и точки риска, поэтому её рисуют до начала разработки.

Связь на карте описывается в терминах upstream/downstream: **upstream** -- поставщик модели (от него зависят), **downstream** -- потребитель (зависит от upstream). Паттерн интеграции определяет, как именно downstream справляется с этой зависимостью.

Пример карты контекстов:

- **Upstream**: `Identity Context` (использует `OHS` + `PL`).
- **Downstream**: `Orders Context` (паттерн `Conformist`, `CF`) и `Shipping Context` (паттерн `Anti-Corruption Layer`, `ACL`).
- `Identity Context` поставляет данные через `Published Language` обоим downstream: `Identity` → `Orders` и `Identity` → `Shipping`.
- Отдельно `Legacy System` интегрируется с `Shipping Context` через `ACL`: `Legacy` → `Shipping`.

Основные паттерны `Context Mapping`:

| Паттерн | Описание | Пример |
|---------|----------|--------|
| **Shared Kernel** | Общая часть модели между двумя контекстами | Общая библиотека доменных примитивов |
| **Customer-Supplier** | Upstream поставляет, downstream потребляет | API-команда поставляет данные frontend-команде |
| **Conformist** | Downstream принимает модель upstream без изменений | Интеграция со сторонним API «как есть» |
| **Anti-Corruption Layer** | Downstream переводит модель upstream на свой язык | Адаптер к legacy-системе |
| **Open Host Service** (`OHS`) | Upstream публикует протокол для всех downstream | REST API с версионированием |
| **Published Language** (`PL`) | Документированный формат обмена данными | JSON Schema, Protobuf |
| **Separate Ways** | Контексты не интегрируются | Два автономных модуля |
| **Partnership** | Контексты развиваются совместно | Два контекста одной команды |

---

## Q7. Что такое Anti-Corruption Layer?

**Anti-Corruption Layer** (`ACL`) -- слой-переводчик между вашим `Bounded Context` и внешней системой (legacy, сторонний сервис, другой контекст). Он принимает чужие структуры данных и превращает их в объекты вашей доменной модели -- и обратно.

Зачем он нужен: без `ACL` чужие концепции и кривая модель внешней системы протекают внутрь и «загрязняют» ваш домен -- ваш код начинает говорить на чужом языке. `ACL` локализует эту чужеродность в одном слое: всё уродство интеграции остаётся снаружи, а ядро остаётся чистым. Бонус -- если внешний контракт изменится, чинить нужно только `ACL`, а не весь домен.

Схема расположения слоя:

- ваш `Bounded Context` обращается к `ACL` (`Ваш Bounded Context` → `ACL`);
- `ACL` ходит в `Legacy / Внешнюю систему` (`ACL` → `Legacy`);
- внутри `ACL` работает `Adapter`, который вызывает `Translator` (`ACL` → `Translator`);
- `Translator` обращается к внешней системе через `Facade` (`Translator` → `Legacy`).

Реализация на Java:

```java
// Модель внешней системы (legacy)
public class LegacyCustomerDTO {
    private String custNo;
    private String custName;
    private int custType; // 1 = physical, 2 = legal
}

// Наша доменная модель
public class Customer {
    private CustomerId id;
    private FullName name;
    private CustomerType type;
}

// Anti-Corruption Layer
@Component
public class CustomerAntiCorruptionLayer {

    private final LegacyCustomerClient legacyClient;

    public Optional<Customer> findCustomer(CustomerId id) {
        LegacyCustomerDTO dto = legacyClient.getCustomer(id.value());
        return Optional.ofNullable(dto)
            .map(this::translateToDomain);
    }

    private Customer translateToDomain(LegacyCustomerDTO dto) {
        return new Customer(
            new CustomerId(dto.getCustNo()),
            FullName.parse(dto.getCustName()),
            CustomerType.fromLegacyCode(dto.getCustType())
        );
    }
}
```

Когда применять `ACL`:

- Интеграция с legacy-системой, модель которой нельзя менять
- Работа со сторонним API с неудобной моделью данных
- Защита `Core Domain` от влияния `Generic` или `Supporting Subdomain`

---

## Q8. Что такое Shared Kernel?

**Shared Kernel** -- паттерн, при котором два `Bounded Context` совместно владеют общей частью модели (например, общей библиотекой). Ключевой момент: эта часть -- общая ответственность, поэтому любое изменение в ней требует согласия обеих команд.

Это компромисс между дублированием и связностью: вы убираете копипасту общих типов ценой того, что контексты больше не полностью независимы.

**Плюсы:**
- убирает дублирование общих концепций (один `Money` вместо двух);
- гарантирует, что общие типы одинаковы во всех контекстах.

**Минусы:**
- создаёт связность (coupling) -- изменение в одном контексте задевает другой;
- любая правка требует координации двух команд, что замедляет обе;
- легко превращается в «свалку»: туда тянут всё подряд, и ядро разрастается.

Пример: общая библиотека доменных примитивов:

```java
// shared-kernel module
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Invalid scale for currency");
        }
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
    }
}
```

> Рекомендация: `Shared Kernel` должен быть минимальным. Если он разрастается -- это сигнал о неправильно определённых границах контекстов.

---

## Q9. В чём разница между стратегическим и тактическим DDD?

Коротко: **стратегический DDD** отвечает на вопрос «из каких частей состоит система и как они связаны», а **тактический DDD** -- «как устроена модель внутри одной части». Первый работает с границами между контекстами, второй -- с классами внутри контекста.

| Аспект | Стратегический DDD | Тактический DDD |
|--------|-------------------|-----------------|
| **Уровень** | Архитектура системы в целом | Внутренняя структура одного контекста |
| **Фокус** | Границы контекстов, отношения между ними | Моделирование доменных объектов |
| **Паттерны** | `Bounded Context`, `Context Map`, `Subdomain` | `Entity`, `Value Object`, `Aggregate`, `Repository`, `Domain Event` |
| **Участники** | Архитекторы, доменные эксперты, тех-лиды | Разработчики |
| **Ошибки дорогие?** | Да -- неправильные границы ведут к переписыванию | Менее болезненные -- рефакторинг в рамках контекста |

Состав каждого уровня:

- **Стратегический DDD**: `Bounded Context`, `Context Map`, `Subdomain`, `Ubiquitous Language`.
- **Тактический DDD**: `Entity`, `Value Object`, `Aggregate`, `Repository`, `Domain Event`, `Domain Service`, `Factory`.

Уровни связаны: `Bounded Context` (стратегический) задаёт рамки, внутри которых живут `Aggregate` (тактический) -- то есть `Bounded Context` → `Aggregate`.

> **Важно**: частая ошибка -- начинать с тактических паттернов (классов и интерфейсов), не определив стратегические границы. Правильный порядок: сначала `Bounded Context`, потом модели внутри.

---

## Q10. Что такое Subdomain и какие типы бывают?

**Subdomain** (поддомен) -- логическая область бизнеса, которая существует независимо от того, какую систему вы строите. Это не технический артефакт.

Ключевое различие -- проблема против решения:

- **Subdomain** -- это **проблемное пространство** (problem space): что есть в бизнесе как данность (продажи, доставка, биллинг).
- **Bounded Context** -- это **пространство решений** (solution space): как вы решили смоделировать эту область в коде.

То есть поддомен вы *обнаруживаете* в бизнесе, а контекст *проектируете* под него. Это объясняет, почему типы поддоменов задают разный подход к разработке.

Три типа поддоменов:

| Тип | Описание | Пример (e-commerce) | Подход к разработке |
|-----|----------|---------------------|---------------------|
| **Core Domain** | Конкурентное преимущество бизнеса | Алгоритм рекомендаций, ценообразование | Максимум инвестиций, лучшие разработчики, `DDD` |
| **Supporting Subdomain** | Поддерживает core, но не уникально | Управление каталогом товаров | Средние инвестиции, можно аутсорсить |
| **Generic Subdomain** | Общее для всех бизнесов | Аутентификация, отправка e-mail | Готовое решение (Keycloak, SendGrid) |

Связь между `Subdomain` и `Bounded Context`:

- В идеале: один `Subdomain` = один `Bounded Context`
- На практике: один `Bounded Context` может покрывать несколько `Supporting Subdomain`
- Ошибка: один `Bounded Context` покрывает несколько `Core Domain` -- признак слишком широких границ

---

## Q11. (!) В чём разница между Entity и Value Object?

Главное различие -- в **идентичности**. У `Entity` есть собственное «я»: два заказа с одинаковыми полями -- это всё равно два разных заказа, потому что у каждого свой ID и своя судьба во времени. `Value Object` -- это просто значение: две купюры по 100 рублей взаимозаменяемы, важна сумма, а не «какая именно купюра». Отсюда вытекают и остальные отличия -- мутабельность, сравнение, хранение.

| Характеристика | `Entity` | `Value Object` |
|---------------|----------|----------------|
| **Идентичность** | Определяется уникальным ID | Определяется значением атрибутов |
| **Мутабельность** | Может изменяться (сохраняя ID) | Иммутабельный |
| **Жизненный цикл** | Создаётся, изменяется, удаляется | Создаётся и заменяется целиком |
| **Сравнение** | По ID (`equals` по ID) | По всем атрибутам |
| **Примеры** | `User`, `Order`, `Product` | `Money`, `Address`, `DateRange`, `Email` |
| **Хранение в БД** | Своя таблица с PK | Встраивается в таблицу Entity или отдельная таблица без собственного смысла |

```java
// Entity — идентичность по ID
public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderLine> lines;
    private Money totalPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return id.equals(other.id); // сравнение только по ID
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

// Value Object — идентичность по значению
public record Address(
    String city,
    String street,
    String zipCode,
    String country
) {
    public Address {
        Objects.requireNonNull(city, "City must not be null");
        Objects.requireNonNull(zipCode, "Zip code must not be null");
    }
    // equals/hashCode по всем полям — автоматически через record
}
```

> **Правило**: предпочитайте `Value Object` там, где это возможно. Они проще, безопаснее (иммутабельность) и лучше выражают бизнес-концепции. В типичной хорошо спроектированной модели `Value Object` больше, чем `Entity`.

---

## Q12. Как правильно реализовать Value Object в Java?

Правильный `Value Object` держится на четырёх требованиях -- и каждое имеет практический смысл:

1. **Иммутабельность** -- все поля `final`, нет сеттеров. Раз объект нельзя изменить, его можно безопасно расшаривать между сущностями и потоками, не боясь, что кто-то его испортит.
2. **Валидация в конструкторе** -- невалидный `Value Object` нельзя создать в принципе. Если объект существует, он гарантированно корректен, и проверки не нужно дублировать по всему коду.
3. **Сравнение по значению** -- `equals`/`hashCode` по всем атрибутам. Два VO с одинаковыми полями считаются равными -- это и есть «равенство по значению».
4. **Самодокументирующийся** -- имя типа выражает бизнес-концепцию (`Email`, а не `String`), поэтому сигнатуры методов читаются как предложения на языке домена.

С Java 16+ `record` закрывает первые три пункта автоматически (final-поля, сгенерированные `equals`/`hashCode`), поэтому это идеальная основа для `Value Object`:

```java
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        Objects.requireNonNull(value, "Email must not be null");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        value = value.toLowerCase(); // нормализация
    }
}

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        amount = amount.setScale(
            currency.getDefaultFractionDigits(), RoundingMode.HALF_UP
        );
    }

    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(currency, other.currency);
        }
        return new Money(amount.add(other.amount), currency);
    }

    public Money multiply(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }
}

public record DateRange(LocalDate start, LocalDate end) {
    public DateRange {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start must be before end");
        }
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public boolean overlaps(DateRange other) {
        return !this.end.isBefore(other.start) && !other.end.isBefore(this.start);
    }
}
```

`Value Object` как замена примитивов (`Primitive Obsession` -- антипаттерн):

```java
// Плохо: примитивы
void createUser(String email, String phone, int age) { ... }

// Хорошо: Value Objects
void createUser(Email email, PhoneNumber phone, Age age) { ... }
```

---

## Q13. Когда использовать Entity, а когда Value Object?

Правило простое: если объекту нужна собственная история и идентичность -- это `Entity`; если он просто описывает «какое значение» -- это `Value Object`. По умолчанию выбирайте `Value Object` (он проще и безопаснее) и повышайте до `Entity`, только если обнаружили потребность в идентичности.

Вопросы-подсказки для определения:

1. **Нужно ли отслеживать объект во времени?** Да → `Entity`
2. **Важна ли уникальная идентичность?** Да → `Entity`
3. **Можно ли заменить объект целиком на другой с такими же значениями?** Да → `Value Object`
4. **Объект разделяется между несколькими `Entity`?** Обычно → `Value Object`

Примеры:

| Концепция | `Entity` или `Value Object`? | Почему? |
|-----------|------------------------------|---------|
| Деньги (100 руб.) | `Value Object` | Одна купюра 100 руб. неотличима от другой |
| Банковский счёт | `Entity` | Имеет уникальный номер и состояние |
| Адрес доставки | `Value Object` | Определяется значением, не идентичностью |
| Пользователь | `Entity` | Имеет уникальную идентичность |
| Координата GPS | `Value Object` | Определяется широтой и долготой |
| Товар в каталоге | `Entity` | Уникальный SKU, изменяемая цена |
| Элемент заказа (строка) | `Value Object` или `Entity` | Зависит от домена: если нужно отслеживать изменения строки -- `Entity` |

---

## Q14. (!) Что такое Aggregate и Aggregate Root?

**Aggregate** -- группа доменных объектов (`Entity` и `Value Object`), которые изменяются и сохраняются как единое целое. **Aggregate Root** -- одна «главная» сущность внутри группы, единственная дверь, через которую внешний код взаимодействует с агрегатом.

Зачем это нужно: в богатой модели объекты связаны и должны быть согласованы (например, сумма заказа = сумме его строк). Если разрешить менять внутренние объекты напрямую, никто не гарантирует эту согласованность. Агрегат решает проблему так: корень становится единственным входом и стражем инвариантов, а внутренности скрыты. Заодно агрегат задаёт **границу транзакции** -- то, что меняется и сохраняется атомарно.

Состав агрегата `Order`:

- `Order` -- это `Aggregate Root` (корень).
- Корень содержит внутренние объекты: `OrderLine 1` (`Entity`), `OrderLine 2` (`Entity`), `Address` (`Value Object`), `Money` (`Value Object`) -- то есть `Order` → `OrderLine`, `Order` → `Address`, `Order` → `Money`.

Доступ к агрегату:

- внешний код обращается к содержимому **только через root**: `Внешний код` → `Order`;
- прямое обращение внешнего кода к внутренним объектам (например, к `OrderLine`) **запрещено**.

Правила агрегата (и зачем каждое):

1. **Внешний код обращается только к `Aggregate Root`** -- никогда напрямую к внутренним объектам. Иначе инварианты можно обойти в обход корня.
2. **`Aggregate Root` имеет глобальный ID** -- внутренние `Entity` имеют только локальный ID, уникальный лишь внутри агрегата. Снаружи их адресовать незачем.
3. **Транзакционная граница** -- один агрегат = одна транзакция. Меняем за раз ровно один агрегат, чтобы блокировки были короткими и предсказуемыми.
4. **Ссылки между агрегатами -- только по ID** -- не по прямой ссылке на объект. Это не даёт случайно загрузить и изменить чужой агрегат в той же транзакции.
5. **Инварианты внутри агрегата** -- корень гарантирует согласованность всех своих данных после каждой операции.

```java
public class Order {
    private final OrderId id;
    private final CustomerId customerId; // ссылка на другой агрегат по ID
    private OrderStatus status;
    private final List<OrderLine> lines = new ArrayList<>();
    private Address shippingAddress;

    public void addLine(ProductId productId, int quantity, Money price) {
        if (status != OrderStatus.DRAFT) {
            throw new OrderAlreadySubmittedException(id);
        }
        lines.add(new OrderLine(productId, quantity, price));
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderException(id);
        }
        this.status = OrderStatus.SUBMITTED;
        registerEvent(new OrderSubmittedEvent(id, customerId, calculateTotal()));
    }

    public Money calculateTotal() {
        return lines.stream()
            .map(OrderLine::subtotal)
            .reduce(Money.ZERO, Money::add);
    }
}
```

---

## Q15. Какие правила проектирования агрегатов?

Вон Вернон (Vaughn Vernon, "Implementing Domain-Driven Design") сводит проектирование агрегатов к четырём правилам. Все они -- про поиск баланса: агрегат должен быть достаточно большим, чтобы защитить инварианты, и достаточно маленьким, чтобы не мешать конкурентности.

### 1. Защищайте бизнес-инварианты внутри агрегата

Размер агрегата определяет не удобство, а инварианты: всё, что обязано быть согласованным в один момент, должно лежать в одном агрегате. Это нижняя граница размера -- меньше делать нельзя.

### 2. Делайте агрегаты маленькими

Большие агрегаты:
- Создают конкуренцию за блокировки (`OptimisticLockException`)
- Загружают лишние данные из БД
- Усложняют понимание кода

### 3. Ссылайтесь на другие агрегаты только по ID

```java
// Плохо: прямая ссылка на другой агрегат
public class Order {
    private Customer customer; // загружает весь Customer
}

// Хорошо: ссылка по ID
public class Order {
    private CustomerId customerId; // лёгкая ссылка
}
```

### 4. Используйте Eventual Consistency между агрегатами

Внутри агрегата -- строгая согласованность (одна транзакция). Между агрегатами -- отложенная: если правило охватывает несколько агрегатов, не тяните их в одну транзакцию, а свяжите доменными событиями. Один агрегат меняется, публикует событие, второй реагирует своей транзакцией. Подробнее в [паттернах согласованности](consistency-patterns-interview.md).

---

## Q16. Как определить границы агрегата?

Границы агрегата ищут не от структуры данных, а от инвариантов: «что обязано меняться вместе». Практический алгоритм:

1. **Определите инварианты**: какие данные должны быть согласованы в любой момент времени? Они задают минимальное ядро агрегата.
2. **Найдите `Aggregate Root`**: какая сущность «владеет» этими данными и отвечает за их целостность? Она и станет корнем.
3. **Проверьте конкурентность**: если две части часто меняются разными пользователями независимо -- это сигнал разнести их по разным агрегатам.
4. **Примените правило «маленького агрегата»**: всё, что не нужно для инвариантов, выносите наружу и связывайте по ID.

Пример: интернет-магазин

```
❌ Один большой агрегат:
   Order → Customer → PaymentMethod → ShippingAddress → OrderLines → Product

✅ Несколько маленьких агрегатов:
   Order (root) → OrderLines
   Customer (root) → PaymentMethods
   Product (root) → ProductVariants
   Shipment (root) → ShipmentItems
```

Сигналы, что агрегат слишком большой:
- Частые `OptimisticLockException` при параллельных изменениях
- Медленные запросы из-за загрузки лишних данных
- Изменение одной части ломает тесты другой части

---

## Q17. Как реализовать Aggregate Root в Java/Spring?

Полный пример агрегата с использованием `Spring Data` и `AbstractAggregateRoot`:

```java
// Aggregate Root
public class BankAccount extends AbstractAggregateRoot<BankAccount> {

    @Id
    private final AccountId id;
    private final CustomerId ownerId;
    private Money balance;
    private AccountStatus status;
    private final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(AccountId id, CustomerId ownerId, Money initialDeposit) {
        this.id = id;
        this.ownerId = ownerId;
        this.balance = initialDeposit;
        this.status = AccountStatus.ACTIVE;
        registerEvent(new AccountOpenedEvent(id, ownerId, initialDeposit));
    }

    public void deposit(Money amount) {
        requireActive();
        this.balance = balance.add(amount);
        transactions.add(Transaction.deposit(amount));
        registerEvent(new MoneyDepositedEvent(id, amount, balance));
    }

    public void withdraw(Money amount) {
        requireActive();
        if (balance.isLessThan(amount)) {
            throw new InsufficientFundsException(id, balance, amount);
        }
        this.balance = balance.subtract(amount);
        transactions.add(Transaction.withdrawal(amount));
        registerEvent(new MoneyWithdrawnEvent(id, amount, balance));
    }

    public void close() {
        if (!balance.isZero()) {
            throw new NonZeroBalanceException(id, balance);
        }
        this.status = AccountStatus.CLOSED;
        registerEvent(new AccountClosedEvent(id));
    }

    private void requireActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(id);
        }
    }
}
```

Репозиторий для агрегата:

```java
public interface BankAccountRepository
        extends CrudRepository<BankAccount, AccountId> {

    Optional<BankAccount> findByOwnerId(CustomerId ownerId);
}
```

> Обратите внимание: `AbstractAggregateRoot` из `Spring Data` позволяет регистрировать доменные события через `registerEvent()`. Они будут автоматически опубликованы при сохранении агрегата через репозиторий.

---

## Q18. Что такое Domain Event?

**Domain Event** -- объект, фиксирующий факт, который уже произошёл в домене и важен для бизнеса. Имя всегда в прошедшем времени, потому что событие описывает свершившееся: `OrderPlaced`, `PaymentReceived`, `AccountClosed`.

Характеристики доменного события (и почему они такие):

1. **Иммутабельное** -- прошлое нельзя переписать, поэтому факт нельзя менять задним числом.
2. **Именуется в прошедшем времени** -- это маркер «уже случилось», в отличие от команды (`PlaceOrder`), которая лишь просит что-то сделать.
3. **Содержит контекст** -- несёт достаточно данных, чтобы подписчик обработал событие, не дёргая источник обратно.
4. **Не содержит логики** -- это данные о факте, а не поведение; реагируют на событие обработчики.

```java
public record OrderPlacedEvent(
    OrderId orderId,
    CustomerId customerId,
    Money totalAmount,
    List<OrderLineSnapshot> lines,
    Instant occurredAt
) {
    public OrderPlacedEvent {
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(totalAmount);
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
```

Зачем нужны доменные события:

- **Декаплинг**: заказ не знает про доставку, но публикует `OrderPlacedEvent`
- **Аудит**: последовательность событий -- полная история изменений
- **Реактивность**: другие контексты реагируют на события асинхронно
- **Eventual Consistency**: синхронизация между агрегатами и контекстами

---

## Q19. Как реализовать доменные события в Spring?

В `Spring` есть три способа публиковать доменные события -- от самого декларативного к самому надёжному. Выбор зависит от того, нужна ли вам гарантия доставки между модулями.

### Подход 1: `AbstractAggregateRoot` + `Spring Data`

Агрегат накапливает события через `registerEvent()`, а `Spring Data` публикует их автоматически при `save()`. Удобно, потому что событие привязано к сохранению агрегата; обработчик с `@TransactionalEventListener` срабатывает после коммита.

```java
public class Order extends AbstractAggregateRoot<Order> {
    public void place() {
        this.status = OrderStatus.PLACED;
        registerEvent(new OrderPlacedEvent(this.id, this.customerId));
    }
}

// Обработчик (в том же Bounded Context)
@Component
public class OrderPlacedHandler {
    @TransactionalEventListener
    public void handle(OrderPlacedEvent event) {
        // отправить уведомление, обновить статистику и т.д.
    }
}
```

### Подход 2: `ApplicationEventPublisher` (ручной контроль)

Сервис сам решает, когда и какое событие опубликовать. Больше контроля, но и больше шаблонного кода; событие легко забыть или опубликовать до коммита.

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void placeOrder(PlaceOrderCommand cmd) {
        Order order = Order.create(cmd);
        repository.save(order);
        publisher.publishEvent(new OrderPlacedEvent(order.getId()));
    }
}
```

### Подход 3: `Spring Modulith` (рекомендуемый для модульных монолитов)

Единственный из трёх, кто даёт надёжную доставку между модулями: события переживают рестарт и не теряются, потому что под капотом -- `Transactional Outbox` с гарантией at-least-once.

```java
@ApplicationModuleListener
public void on(OrderPlacedEvent event) {
    // Spring Modulith обеспечивает at-least-once delivery
    // через Transactional Outbox
}
```

Подробнее о событийных паттернах в [Event-Driven паттернах](event-driven-patterns-interview.md).

---

## Q20. В чём разница между Domain Event и Integration Event?

Разница -- в том, кто слушает событие. **Domain Event** остаётся внутри одного `Bounded Context` и говорит на языке его модели. **Integration Event** пересекает границу контекста, поэтому должен быть стабильным, сериализуемым контрактом, не зависящим от внутренней модели. Часто Domain Event внутри становится поводом опубликовать Integration Event наружу.

| Характеристика | `Domain Event` | `Integration Event` |
|---------------|----------------|---------------------|
| **Область видимости** | Внутри `Bounded Context` | Между `Bounded Context` |
| **Транспорт** | In-memory (`ApplicationEventPublisher`) | Брокер сообщений (`Kafka`, `RabbitMQ`) |
| **Формат** | Доменные объекты | Сериализуемый DTO (JSON, Avro, Protobuf) |
| **Надёжность** | Гарантируется транзакцией | Требует `Outbox Pattern` для гарантий |
| **Связность** | Знает о доменной модели | Не зависит от внутренней модели |

Поток события от агрегата до внешних контекстов:

- внутри `Bounded Context: Orders` агрегат `Order Aggregate` публикует `Domain Event`, который ловит `OrderPlacedHandler` (`Order Aggregate` → `OrderPlacedHandler`);
- обработчик формирует `Integration Event` и кладёт его в `Outbox Table` (`OrderPlacedHandler` → `Outbox Table`);
- отдельный `Outbox Relay` вычитывает таблицу и отправляет событие в `Kafka` (`Outbox Table` → `Kafka`);
- из `Kafka` `Integration Event` расходится по контекстам-потребителям: `Kafka` → `Shipping Context` и `Kafka` → `Billing Context`.

Паттерн `Transactional Outbox`: доменное событие сохраняется в таблицу `outbox` в той же транзакции, что и изменение агрегата. Отдельный процесс вычитывает `outbox` и отправляет в брокер.

---

## Q21. (!) Что такое Repository в DDD?

**Repository** -- паттерн, который выдаёт агрегаты так, будто они лежат в обычной коллекции в памяти. Доменный код вызывает `find`/`save` и не знает, что за этим стоит SQL, JPA или вызов по сети -- вся персистенция спрятана за интерфейсом.

Смысл -- развязать домен и хранилище: модель работает с объектами, а не с таблицами, и остаётся чистой от инфраструктуры.

Правила репозиториев в `DDD` (и почему так):

1. **Один репозиторий на агрегат** -- не на каждую `Entity` или таблицу. Агрегат -- единица загрузки и сохранения, поэтому репозитории привязаны к нему, а не к схеме БД.
2. **Интерфейс в доменном слое, реализация -- в инфраструктурном** -- так домен задаёт контракт, но не зависит от технологии хранения (инверсия зависимостей).
3. **Оперирует только `Aggregate Root`** -- внутренние `Entity` достаются и меняются только через корень, иначе нарушится правило агрегата.
4. **Имитирует коллекцию** -- методы `add`/`find`/`remove`, а не `insert`/`select`/`delete`: имена на языке домена, а не SQL.

```java
// Доменный слой: интерфейс
public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    void save(Order order);
    void delete(Order order);
    OrderId nextId();
}

// Инфраструктурный слой: реализация через Spring Data
@Repository
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderRepository springRepo;

    @Override
    public Optional<Order> findById(OrderId id) {
        return springRepo.findById(id.value())
            .map(OrderMapper::toDomain);
    }

    @Override
    public void save(Order order) {
        springRepo.save(OrderMapper.toEntity(order));
    }

    @Override
    public OrderId nextId() {
        return new OrderId(UUID.randomUUID());
    }
}
```

> `Repository` ≠ `DAO`. `DAO` -- это доступ к данным (data-centric), а `Repository` -- это коллекция объектов домена (domain-centric). `Repository` работает с агрегатами, `DAO` -- с таблицами.

---

## Q22. (!) В чём разница между Domain Service и Application Service?

Различие -- в типе ответственности. **Domain Service** содержит бизнес-логику, которая не помещается ни в один агрегат (например, перевод денег задействует два счёта). **Application Service** -- это дирижёр: он не знает бизнес-правил, а только организует сценарий -- достать агрегаты из репозитория, вызвать домен, сохранить результат, открыть транзакцию. Правило: бизнес-решения -- в домене, координация -- в приложении.

| Аспект | `Domain Service` | `Application Service` |
|--------|------------------|----------------------|
| **Слой** | Доменный слой | Слой приложения |
| **Зависимости** | Только доменные объекты | Репозитории, инфра, доменные сервисы |
| **Состояние** | Stateless | Stateless |
| **Бизнес-логика** | Содержит доменную логику, не принадлежащую одному агрегату | Оркестрирует, не содержит бизнес-логику |
| **Транзакции** | Не управляет транзакциями | Управляет транзакциями (`@Transactional`) |
| **Входные/выходные типы** | Доменные объекты | DTO, команды |

```java
// Domain Service — бизнес-правило, требующее два агрегата
public class TransferService {
    public void transfer(BankAccount from, BankAccount to, Money amount) {
        if (!from.canWithdraw(amount)) {
            throw new InsufficientFundsException(from.getId());
        }
        from.withdraw(amount);
        to.deposit(amount);
    }
}

// Application Service — оркестрация
@Service
@RequiredArgsConstructor
public class TransferApplicationService {
    private final BankAccountRepository accountRepo;
    private final TransferService transferService; // Domain Service
    private final NotificationService notificationService;

    @Transactional
    public void executeTransfer(TransferCommand cmd) {
        var from = accountRepo.findById(cmd.fromAccountId())
            .orElseThrow();
        var to = accountRepo.findById(cmd.toAccountId())
            .orElseThrow();

        transferService.transfer(from, to, cmd.amount());

        accountRepo.save(from);
        accountRepo.save(to);
        notificationService.notifyTransfer(cmd);
    }
}
```

Когда нужен `Domain Service`:

- Бизнес-правило требует взаимодействия нескольких агрегатов
- Логика не принадлежит ни одному конкретному агрегату
- Операция связана с доменной концепцией (например, «перевод денег»)

---

## Q23. Что такое Factory в DDD?

**Factory** -- паттерн, который прячет сложную логику создания доменного объекта за одним вызовом. Нужен, когда обычного конструктора мало: создание требует вычислений, валидации, обращения к сервисам или выбора конкретного типа.

Зачем выделять отдельно: создание агрегата может само по себе быть бизнес-операцией со своими правилами (рассчитать скоринг, оценить риск). Если запихнуть это в конструктор, он перегрузится зависимостями; фабрика собирает уже готовый, согласованный объект и оставляет конструктор простым.

```java
// Factory Method на самом агрегате
public class Order {
    public static Order create(CustomerId customerId, Address shippingAddress) {
        var order = new Order(OrderId.generate(), customerId);
        order.shippingAddress = shippingAddress;
        order.status = OrderStatus.DRAFT;
        order.createdAt = Instant.now();
        order.registerEvent(new OrderCreatedEvent(order.id));
        return order;
    }
}

// Отдельная Factory для сложных случаев
@Component
@RequiredArgsConstructor
public class LoanApplicationFactory {
    private final CreditScoreService creditScoreService;
    private final RiskAssessmentPolicy riskPolicy;

    public LoanApplication create(Applicant applicant, LoanTerms terms) {
        CreditScore score = creditScoreService.calculate(applicant);
        RiskLevel risk = riskPolicy.assess(applicant, terms, score);

        return new LoanApplication(
            LoanApplicationId.generate(),
            applicant,
            terms,
            score,
            risk
        );
    }
}
```

Когда использовать `Factory`:
- Создание объекта требует сложной валидации или вычислений
- Нужно выбрать конкретный тип (полиморфизм) на основе входных данных
- Создание требует обращения к внешним сервисам

---

## Q24. (!) Что такое CQRS?

**CQRS** (`Command Query Responsibility Segregation`) -- паттерн, который разделяет работу с данными на две модели: **команды** меняют состояние (write), **запросы** только читают (read). Вместо одной модели «на все случаи» появляются две, заточенные каждая под свою задачу.

Зачем: у чтения и записи разные требования. Запись должна защищать инварианты и работать с агрегатами; чтение должно быстро отдавать данные в удобной для UI форме, часто из нескольких агрегатов сразу. Одна модель не угождает обеим -- она либо тормозит на чтении, либо протекает на записи. `CQRS` снимает этот конфликт, разводя их.

Схема разделения путей:

- `Клиент` отправляет `Command` в `Command Handler` (`Клиент` → `Command Handler`) и `Query` в `Query Handler` (`Клиент` → `Query Handler`);
- `Command Handler` пишет в `Write Store` (`Command Handler` → `Write Store`, путь Write);
- `Query Handler` читает из `Read Store` (`Query Handler` → `Read Store`, путь Read);
- `Write Store` синхронизирует данные в `Read Store` через `Sync / Events` (`Write Store` → `Read Store`).

`CQRS` бывает разной глубины -- от лёгкой до радикальной. Берите минимально достаточную:

1. **На уровне кода**: разные сервисы/интерфейсы для чтения и записи при одной БД. Самый дешёвый вариант, часто его достаточно.
2. **На уровне модели**: отдельные модели для чтения и записи (write-модель с агрегатами, read-модель из плоских DTO).
3. **На уровне хранения**: разные БД -- пишем в PostgreSQL, читаем из оптимизированного хранилища (например, Elasticsearch). Максимальная гибкость, но появляется eventual consistency и сложность синхронизации.

```java
// Command — изменение состояния
public record PlaceOrderCommand(
    CustomerId customerId,
    List<OrderLineDTO> lines,
    Address shippingAddress
) {}

@Service
public class OrderCommandService {
    @Transactional
    public OrderId handle(PlaceOrderCommand cmd) {
        Order order = Order.create(cmd.customerId(), cmd.shippingAddress());
        cmd.lines().forEach(l -> order.addLine(l.productId(), l.quantity(), l.price()));
        order.submit();
        orderRepository.save(order);
        return order.getId();
    }
}

// Query — чтение без побочных эффектов
public record OrderSummaryQuery(CustomerId customerId) {}

@Service
public class OrderQueryService {
    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> handle(OrderSummaryQuery query) {
        return orderReadRepository.findSummariesByCustomer(query.customerId());
    }
}
```

Подробнее о `CQRS` в контексте [микросервисов](microservices-interview.md).

---

## Q25. (!) Что такое Event Sourcing?

**Event Sourcing** -- паттерн, при котором источником истины становится не текущий снимок состояния, а полный журнал произошедших событий. В БД хранят не «баланс = 1200», а всю цепочку: открыли счёт, положили, сняли, положили. Текущее состояние получают, «проиграв» события по порядку.

Аналогия -- банковская выписка: банк не держит просто число на счёте, а хранит все операции; баланс выводится из них. Главный выигрыш -- история не теряется: видно не только «что сейчас», но и «как к этому пришли».

Пример журнала в `Event Store` -- цепочка событий по порядку:

1. `AccountOpened` (balance: 0)
2. `MoneyDeposited` (+1000)
3. `MoneyWithdrawn` (-300)
4. `MoneyDeposited` (+500)

События идут последовательно: `AccountOpened` → `MoneyDeposited` → `MoneyWithdrawn` → `MoneyDeposited`. Проигрывание (`Replay`) всей цепочки даёт `Current State` с balance: 1200.

```java
// Восстановление состояния из событий
public class BankAccount {
    private AccountId id;
    private Money balance;

    public static BankAccount reconstitute(List<DomainEvent> events) {
        var account = new BankAccount();
        events.forEach(account::apply);
        return account;
    }

    private void apply(DomainEvent event) {
        switch (event) {
            case AccountOpenedEvent e -> {
                this.id = e.accountId();
                this.balance = Money.ZERO;
            }
            case MoneyDepositedEvent e -> {
                this.balance = balance.add(e.amount());
            }
            case MoneyWithdrawnEvent e -> {
                this.balance = balance.subtract(e.amount());
            }
            default -> throw new UnknownEventException(event);
        }
    }
}
```

Преимущества и недостатки:

| Преимущества | Недостатки |
|-------------|-----------|
| Полная история изменений | Сложность реализации |
| Аудит «из коробки» | Eventual consistency для read-моделей |
| Отладка: можно воспроизвести любое состояние | Версионирование событий при эволюции |
| Легко строить проекции для `CQRS` | Снапшоты нужны для длинных потоков |

---

## Q26. Как связаны CQRS и Event Sourcing?

Главное, что хотят услышать: это **два независимых паттерна**, а не один. Их часто соединяют, потому что они хорошо дополняют друг друга, но каждый работает и сам по себе. Распространённая ошибка на собеседовании -- считать, что одно невозможно без другого.

Все три комбинации жизнеспособны:

- **`CQRS` без `Event Sourcing`**: команды пишут в обычную реляционную БД, запросы читают из отдельного оптимизированного read-store. Состояние хранится как снимок.
- **`Event Sourcing` без `CQRS`**: события дают историю, но чтение и запись идут через одну модель (восстановили агрегат -- и читаем, и меняем).
- **`CQRS` + `Event Sourcing`** (естественный союз): команды порождают события → события ложатся в Event Store (он же write-store) → проекции «раскладывают» их в read-модель под запросы. Event Store бесплатно даёт поток событий для построения проекций.

Поток в связке `CQRS` + `Event Sourcing`:

- `Command` приходит в `Aggregate` (`Command` → `Aggregate`);
- агрегат порождает `Domain Events`, которые ложатся в `Event Store` (`Aggregate` → `Event Store`);
- из `Event Store` проекция (`Projection`) строит `Read Model` (`Event Store` → `Read Model`);
- `Query` читает из `Read Model` (`Query` → `Read Model`);
- параллельно `Event Store` формирует `Snapshots` (`Event Store` → `Snapshots`) для ускорения восстановления длинных потоков.

Фреймворки для реализации в Java:

| Фреймворк | Описание |
|-----------|----------|
| `Axon Framework` | Полноценная реализация CQRS + ES |
| `Spring Modulith` | Модульный подход с событиями |
| `EventStoreDB` | Специализированная БД для событий |
| `Eventuate` | CQRS/ES для микросервисов |

---

## Q27. (!) Что такое Saga Pattern в контексте DDD?

**Saga** -- способ провести бизнес-операцию через несколько `Bounded Context` или агрегатов, когда одна общая ACID-транзакция невозможна. Saga разбивает её на цепочку локальных транзакций: каждый шаг коммитится сам по себе, а если что-то падает дальше -- уже выполненные шаги откатывают **компенсирующими** действиями.

Почему не просто транзакция: каждый сервис владеет своей БД, и распределённая транзакция (`2PC`) держала бы их все заблокированными до конца -- это медленно и хрупко. Saga меняет атомарность на доступность: вместо «всё или ничего в один момент» -- «либо все шаги, либо все компенсации, но со временем». Откат тут не технический rollback, а явное бизнес-действие: не «отменили транзакцию», а «вернули деньги», «освободили резерв».

Участники: `Order Service` (`OS`), `Payment Service` (`PS`), `Stock Service` (`SS`), `Delivery Service` (`DS`).

Прямой поток (happy path):

1. `Order Service` → `Payment Service`: «Списать оплату».
2. `Payment Service` → `Order Service`: «Оплата успешна».
3. `Order Service` → `Stock Service`: «Зарезервировать товар».
4. `Stock Service` → `Order Service`: «Товар зарезервирован».
5. `Order Service` → `Delivery Service`: «Создать доставку».
6. `Delivery Service` → `Order Service`: «Доставка создана».

Заметка по всем участникам (`Order Service` … `Delivery Service`): **если шаг 3 (Delivery) падает**, запускаются компенсирующие действия:

1. `Order Service` → `Stock Service`: «Компенсация: вернуть товар».
2. `Order Service` → `Payment Service`: «Компенсация: вернуть оплату».

Каждый шаг Saga:
1. Выполняет локальную транзакцию
2. Публикует доменное событие
3. Имеет компенсирующую транзакцию на случай отката

Когда использовать `Saga` вместо распределённой транзакции (`2PC`):

| Критерий | `2PC` | `Saga` |
|----------|-------|--------|
| Связность | Высокая (все участники блокированы) | Низкая (асинхронно) |
| Доступность | Снижается (один участник упал -- все ждут) | Сохраняется |
| Производительность | Медленно (блокировки) | Быстро (eventual consistency) |
| Сложность | Простая модель, сложная инфра | Сложная модель, простая инфра |

Подробнее о компенсирующих транзакциях в [вопросах по микросервисам](microservices-interview.md).

---

## Q28. В чём разница между хореографией и оркестрацией в Saga?

Вопрос -- кто управляет ходом saga. В **хореографии** управления нет вовсе: сервисы реагируют на события друг друга, как танцоры на музыку -- логика размазана по участникам. В **оркестрации** есть дирижёр -- отдельный оркестратор, который по шагам командует, кому что делать. Хореография проще для коротких потоков, оркестрация выигрывает, когда шагов много и нужна наблюдаемость.

| Аспект | Хореография | Оркестрация |
|--------|------------|-------------|
| **Управление** | Децентрализованное (каждый сервис реагирует на события) | Центральный оркестратор |
| **Связность** | Низкая | Средняя (оркестратор знает обо всех шагах) |
| **Наблюдаемость** | Сложно отследить поток | Легко видеть весь процесс |
| **Циклические зависимости** | Возможны | Исключены |
| **Сложность** | Растёт экспоненциально с числом участников | Линейный рост |
| **Когда использовать** | 2-3 шага, простые потоки | 4+ шагов, сложные потоки |

**Хореография** -- цепочка реакций на события без центра:

- `Order Service` публикует `OrderPlaced`, на него реагирует `Payment Service` (`Order Service` → `Payment Service`);
- `Payment Service` публикует `PaymentCompleted`, на него реагирует `Stock Service` (`Payment Service` → `Stock Service`);
- `Stock Service` публикует `StockReserved`, на него реагирует `Delivery Service` (`Stock Service` → `Delivery Service`).

**Оркестрация** -- центральный `Saga Orchestrator` командует по шагам, каждый участник возвращает результат:

- `Saga Orchestrator` → `Payment`: шаг «1. Pay»; `Payment` → `Saga Orchestrator`: `Result`;
- `Saga Orchestrator` → `Stock`: шаг «2. Reserve»; `Stock` → `Saga Orchestrator`: `Result`;
- `Saga Orchestrator` → `Delivery`: шаг «3. Ship»; `Delivery` → `Saga Orchestrator`: `Result`.

---

## Q29. Что такое Specification Pattern?

**Specification** -- паттерн, который превращает бизнес-правило («клиент имеет право на премиум») в отдельный объект, а не прячет его в `if` где-то в сервисе. Раз правило -- это объект, его можно переиспользовать, тестировать изолированно и комбинировать через булевы операции (`and`, `or`, `not`).

Главная выгода -- одно правило в одном месте: вместо дублирования условия в валидации, в фильтре и в запросе к БД вы держите его в одной спецификации и применяете во всех трёх сценариях:

1. **Валидация** -- проверить, удовлетворяет ли объект правилу (`isSatisfiedBy`).
2. **Выборка** -- отфильтровать коллекцию в памяти или превратить правило в запрос к БД.
3. **Создание** -- сконструировать объект, заведомо соответствующий правилу.

```java
// Базовый интерфейс спецификации
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);

    default Specification<T> and(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    default Specification<T> or(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    default Specification<T> not() {
        return candidate -> !this.isSatisfiedBy(candidate);
    }
}

// Конкретные спецификации
public class EligibleForPremiumSpec implements Specification<Customer> {
    public boolean isSatisfiedBy(Customer customer) {
        return customer.totalOrders() > 10
            && customer.totalSpent().isGreaterThan(Money.of(50_000));
    }
}

public class ActiveCustomerSpec implements Specification<Customer> {
    public boolean isSatisfiedBy(Customer customer) {
        return customer.lastOrderDate().isAfter(LocalDate.now().minusMonths(6));
    }
}

// Композиция
Specification<Customer> premiumAndActive =
    new EligibleForPremiumSpec().and(new ActiveCustomerSpec());

List<Customer> vipCustomers = customers.stream()
    .filter(premiumAndActive::isSatisfiedBy)
    .toList();
```

Для интеграции с JPA / Spring Data можно реализовать спецификации через `Criteria API` или `Spring Data Specification<T>`.

---

## Q30. Как организовать пакетную структуру проекта по DDD?

Есть два способа резать пакеты: **по слоям** (наверху `controller`, `service`, `repository`, внутри -- все фичи вперемешку) и **по `Bounded Context`** (наверху бизнес-области, слои -- внутри каждой). В `DDD` рекомендуют второй: пакеты должны отражать домен, а не технические слои. Так весь код одной фичи лежит рядом, а границы контекстов видны прямо в структуре каталогов.

### По Bounded Context (рекомендуемый)

```
com.example.shop
├── order/                          # Bounded Context: Заказы
│   ├── domain/
│   │   ├── model/                  # Entity, Value Object, Aggregate Root
│   │   │   ├── Order.java
│   │   │   ├── OrderLine.java
│   │   │   └── OrderStatus.java
│   │   ├── event/                  # Domain Events
│   │   │   └── OrderPlacedEvent.java
│   │   ├── service/                # Domain Services
│   │   │   └── OrderPricingService.java
│   │   ├── repository/             # Repository interfaces
│   │   │   └── OrderRepository.java
│   │   └── specification/          # Specifications
│   │       └── OverdueOrderSpec.java
│   ├── application/                # Application Services, Use Cases
│   │   ├── PlaceOrderUseCase.java
│   │   └── dto/
│   │       └── PlaceOrderCommand.java
│   └── infrastructure/             # Implementations
│       ├── persistence/
│       │   └── JpaOrderRepository.java
│       └── messaging/
│           └── KafkaOrderEventPublisher.java
├── catalog/                        # Bounded Context: Каталог
│   ├── domain/
│   ├── application/
│   └── infrastructure/
└── shipping/                       # Bounded Context: Доставка
    ├── domain/
    ├── application/
    └── infrastructure/
```

Ключевые правила:

- **`domain/` не зависит от `infrastructure/`** -- инверсия зависимостей
- Между контекстами -- только через `Integration Events` или `ACL`
- `application/` зависит от `domain/`, но не наоборот

---

## Q31. Как DDD сочетается с гексагональной архитектурой?

**Гексагональная архитектура** (Ports and Adapters) -- естественная «упаковка» для `DDD`: она физически изолирует доменную модель от инфраструктуры. `DDD` отвечает на вопрос «что моделировать» (агрегаты, сервисы, события), гексагон -- «куда это положить, чтобы инфраструктура не протекла в домен».

Идея в инверсии зависимостей через **порты**: домен объявляет интерфейсы (порты), а инфраструктура их реализует (адаптеры). Поэтому стрелки зависимостей всегда направлены внутрь, к домену -- ядро не знает ни про REST, ни про JPA, ни про Kafka. Их можно заменить, не трогая бизнес-логику.

Слои и их элементы (стрелки зависимостей направлены внутрь, к домену):

- **Adapters (Infrastructure)**: `REST Controller` (Driving Adapter), `JPA Repository` (Driven Adapter), `Kafka Publisher` (Driven Adapter).
- **Ports**: `Input Port` (Use Case Interface), `Output Port` (Repository Interface), `Event Port` (Event Publisher Interface).
- **Domain Core**: `Domain Model` -- `Aggregates`, `Entities`, `Value Objects`, `Domain Services`.

Направление связей:

- `REST Controller` → `Input Port` → `Domain Model` (входящий вызов через driving-адаптер и input-порт);
- `Domain Model` → `Output Port` → `JPA Repository` (домен пишет в БД через output-порт и driven-адаптер);
- `Domain Model` → `Event Port` → `Kafka Publisher` (домен публикует события через event-порт и driven-адаптер).

```java
// Port (Input) — интерфейс use case
public interface PlaceOrderUseCase {
    OrderId execute(PlaceOrderCommand command);
}

// Port (Output) — интерфейс репозитория (в доменном слое)
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

// Domain — чистая бизнес-логика
public class Order { /* ... */ }

// Adapter (Input) — REST контроллер
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final PlaceOrderUseCase placeOrder;

    @PostMapping("/orders")
    public ResponseEntity<OrderId> create(@RequestBody PlaceOrderRequest req) {
        return ResponseEntity.ok(placeOrder.execute(req.toCommand()));
    }
}

// Adapter (Output) — JPA реализация
@Repository
public class JpaOrderRepository implements OrderRepository { /* ... */ }
```

Подробнее в [вопросах по Clean Architecture](clean-architecture-interview.md).

---

## Q32. Как использовать Spring Modulith для реализации DDD?

`Spring Modulith` -- расширение `Spring`, позволяющее построить `DDD`-систему как модульный монолит: чёткие `Bounded Context` и слабая связанность, но без накладных расходов микросервисов (сети, отдельных деплоев, распределённых транзакций). Это золотая середина для старта -- структура микросервисов внутри одного процесса.

Что он даёт:

1. **Модульная структура** -- один пакет верхнего уровня = один `Bounded Context` = один модуль; границы заданы соглашением, а не разрозненно.
2. **Event-driven коммуникация** -- модули общаются через события `@ApplicationModuleListener`, а не прямыми вызовами, что сохраняет развязку между контекстами.
3. **Верификация границ** -- тест `ApplicationModules.verify()` падает, если модуль полез во внутренности чужого. Границы проверяются автоматически, а не на ревью.
4. **Transactional Outbox** -- встроенная гарантия доставки событий: они переживают рестарт и не теряются.

```java
// Структура проекта
// com.example.shop.order    → модуль "order"
// com.example.shop.catalog  → модуль "catalog"
// com.example.shop.shipping → модуль "shipping"

// Публикация события из модуля Order
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher events;

    @Transactional
    public void placeOrder(PlaceOrderCommand cmd) {
        Order order = Order.create(cmd);
        orderRepo.save(order);
        events.publishEvent(new OrderPlacedEvent(order.getId()));
    }
}

// Обработка в модуле Shipping (другой Bounded Context)
@ApplicationModuleListener
public void on(OrderPlacedEvent event) {
    shippingService.scheduleShipment(event.orderId());
}

// Тест верификации модульной структуры
@Test
void verifyModularStructure() {
    ApplicationModules.of(ShopApplication.class).verify();
}
```

---

## Q33. Какие анти-паттерны встречаются при реализации DDD?

Большинство провалов `DDD` -- не от незнания паттернов, а от их формального применения без понимания сути. Самые частые анти-паттерны и лекарство к каждому:

| Анти-паттерн | Описание | Как исправить |
|-------------|----------|---------------|
| **Anemic Domain Model** | Модель без поведения: сущности -- «мешки с данными», логика в сервисах | Перенести бизнес-логику в агрегаты и Entity |
| **God Aggregate** | Один огромный агрегат с десятками Entity | Разделить на несколько маленьких агрегатов |
| **CRUD-мышление** | Операции `create/read/update/delete` вместо доменных команд | Именовать методы на языке домена: `place()`, `cancel()`, `approve()` |
| **Repository per Entity** | Отдельный репозиторий для каждой таблицы | Один репозиторий на `Aggregate Root` |
| **Leaking Domain** | Доменные объекты возвращаются через API (Entity как DTO) | Использовать DTO / маппинг на границе контекста |
| **Shared Database** | Несколько контекстов работают с одной БД без границ | Каждый контекст -- своя схема или БД |
| **DDD Everywhere** | Применение DDD к простым CRUD-модулям | Применять DDD только к `Core Domain` |
| **Tactical без Strategic** | Паттерны без понимания границ контекстов | Начинать со стратегического дизайна |

Пример анемичной модели vs Rich Domain Model:

```java
// ❌ Anemic Domain Model
public class Order {
    private Long id;
    private String status;
    private List<OrderLine> lines;
    // только геттеры и сеттеры
}

@Service
public class OrderService {
    public void cancelOrder(Long orderId) {
        Order order = repo.findById(orderId);
        if (!"PLACED".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel");
        }
        order.setStatus("CANCELLED");
        for (OrderLine line : order.getLines()) {
            stockService.release(line.getProductId(), line.getQuantity());
        }
        repo.save(order);
    }
}

// ✅ Rich Domain Model
public class Order {
    private OrderId id;
    private OrderStatus status;
    private List<OrderLine> lines;

    public void cancel() {
        if (status != OrderStatus.PLACED) {
            throw new OrderCannotBeCancelledException(id, status);
        }
        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelledEvent(id, lines));
    }
}
```

---

## Q34. (!) Как связаны DDD и микросервисы?

Главная связь: `DDD` отвечает на самый трудный вопрос микросервисов -- **где провести границы**. Резать по техническим слоям или «по таблицам» приводит к распределённому монолиту, где сервисы намертво связаны. Стратегический `DDD` даёт правильный критерий: граница сервиса = граница `Bounded Context`.

Каждый стратегический паттерн находит прямое отражение в микросервисах:

- **`Bounded Context` = граница микросервиса** -- готовый критерий декомпозиции по бизнесу, а не по технике.
- **`Context Map` = карта взаимодействий** -- показывает, кто от кого зависит и через какой контракт.
- **`ACL` = адаптер между сервисами** -- защищает модель от чужих контрактов на границе.
- **`Domain Events` = асинхронная коммуникация** -- сервисы общаются событиями через брокер, оставаясь слабо связанными.

Каждый микросервис = один `Bounded Context` со своими агрегатами:

- **Микросервис Order**: `Bounded Context: Order` с агрегатом `Order Aggregate`.
- **Микросервис Inventory**: `Bounded Context: Inventory` с агрегатом `Product Aggregate`.
- **Микросервис Shipping**: `Bounded Context: Shipping` с `ACL` и агрегатом `Shipment Aggregate`.

Взаимодействие через события и брокер:

- `Bounded Context: Order` публикует `OrderPlaced Event` в `Kafka` (`Order` → `Kafka`);
- из `Kafka` событие идёт в `Bounded Context: Inventory` (`Kafka` → `Inventory`) и в `ACL` сервиса Shipping (`Kafka` → `ACL`);
- `ACL` переводит событие и передаёт его агрегату `Shipment Aggregate` (`ACL` → `Shipment Aggregate`).

Порядок проектирования:

1. **Event Storming** -- определить домен, события, команды
2. **Выделить Bounded Context** -- сгруппировать связанные концепции
3. **Определить Context Map** -- паттерны интеграции между контекстами
4. **Решить: монолит или микросервисы** -- `DDD` работает в обоих случаях
5. **Реализовать тактические паттерны** внутри каждого контекста

> Важно: `DDD` не требует микросервисов. Модульный монолит с чёткими `Bounded Context` (через `Spring Modulith`) может быть лучшим стартом, который позже легко разделить на микросервисы.

---

## Q35. Как тестировать доменную модель?

Доменную модель тестировать и важнее, и проще всего: в ней живут бизнес-правила (значит, цена ошибки высока), но она не зависит от инфраструктуры -- ни Spring-контекста, ни БД, ни моков. Это чистые объекты, которые проверяются обычными unit-тестами «создал -- вызвал метод -- проверил состояние и события».

Тестирование выстраивают пирамидой: основная масса быстрых доменных тестов внизу, тяжёлые интеграционные -- сверху.

Уровни тестирования в `DDD`:

| Уровень | Что тестируем | Инструменты |
|---------|--------------|-------------|
| **Unit** (домен) | Агрегаты, Value Objects, Domain Services | JUnit, AssertJ |
| **Integration** | Application Services, Repositories | Spring Test, Testcontainers |
| **Contract** | Формат Integration Events | Spring Cloud Contract, Pact |

```java
// Тест Value Object
@Test
void moneyAddition_sameCurrency_returnsSum() {
    Money a = new Money(BigDecimal.valueOf(100), Currency.getInstance("RUB"));
    Money b = new Money(BigDecimal.valueOf(250), Currency.getInstance("RUB"));

    Money result = a.add(b);

    assertThat(result.amount()).isEqualByComparingTo("350");
    assertThat(result.currency()).isEqualTo(Currency.getInstance("RUB"));
}

@Test
void moneyAddition_differentCurrency_throwsException() {
    Money rub = new Money(BigDecimal.valueOf(100), Currency.getInstance("RUB"));
    Money usd = new Money(BigDecimal.valueOf(50), Currency.getInstance("USD"));

    assertThatThrownBy(() -> rub.add(usd))
        .isInstanceOf(CurrencyMismatchException.class);
}

// Тест Aggregate
@Test
void orderSubmit_withLines_changesStatusAndPublishesEvent() {
    Order order = Order.create(customerId, address);
    order.addLine(productId, 2, price);

    order.submit();

    assertThat(order.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
    assertThat(order.domainEvents())
        .hasSize(1)
        .first()
        .isInstanceOf(OrderSubmittedEvent.class);
}

@Test
void orderSubmit_emptyOrder_throwsException() {
    Order order = Order.create(customerId, address);

    assertThatThrownBy(order::submit)
        .isInstanceOf(EmptyOrderException.class);
}

// Тест Domain Service
@Test
void transfer_sufficientFunds_movesMoneyBetweenAccounts() {
    BankAccount from = new BankAccount(id1, ownerId, Money.of(1000));
    BankAccount to = new BankAccount(id2, ownerId, Money.of(500));

    new TransferService().transfer(from, to, Money.of(300));

    assertThat(from.getBalance()).isEqualTo(Money.of(700));
    assertThat(to.getBalance()).isEqualTo(Money.of(800));
}
```

Преимущество `DDD` для тестирования: доменные тесты не требуют Spring-контекста, базы данных или моков инфраструктуры. Они быстрые и стабильные.

## Q36. (!) Как реализовать Anti-Corruption Layer (ACL) в Spring?

**Anti-Corruption Layer (ACL)** — слой-переводчик между двумя `Bounded Context` с разными моделями. Защищает собственный контекст от «загрязнения» чужими концепциями и языком. В Spring `ACL` -- это обычно `@Component`, который инкапсулирует клиент к внешней системе и метод-транслятор: внутрь контекста выходят только доменные объекты, чужие DTO дальше не проходят.

**Сценарий применения:** Order Context интегрируется с внешней ERP-системой (SAP), у которой своя модель данных (`artNumber`, `availStatus` и пр.). Задача `ACL` -- превратить эту чужую модель в доменный `Product` и не пустить ERP-термины в ядро.

```java
// Внешняя модель ERP (чужой язык/концепции)
public record ErpProductDto(
    String artNumber,
    BigDecimal listPrice,
    String availStatus    // "IN_STOCK", "OUT_OF_STOCK", "RESERVED"
) {}

// --- ACL: Translator ---
@Component
public class ProductCatalogACL {

    private final ErpProductClient erpClient;

    public Optional<Product> findProduct(ProductId productId) {
        try {
            ErpProductDto erp = erpClient.getProduct(productId.value());
            return Optional.of(translate(erp));
        } catch (ErpProductNotFoundException e) {
            return Optional.empty();
        } catch (ErpServiceException e) {
            throw new ProductCatalogUnavailableException("ERP unavailable", e);
        }
    }

    private Product translate(ErpProductDto erp) {
        return new Product(
            ProductId.of(erp.artNumber()),
            Money.of(erp.listPrice(), Currency.RUB),
            switch (erp.availStatus()) {
                case "IN_STOCK"  -> ProductAvailability.AVAILABLE;
                case "RESERVED"  -> ProductAvailability.LIMITED;
                default          -> ProductAvailability.UNAVAILABLE;
            }
        );
    }
}
```

Поток данных между контекстом заказа и ERP:

- внутри **Order Context**: `Order Service` обращается к `ProductCatalogACL`, передавая `ProductId` (`Order Service` → `ProductCatalogACL`), а `ACL` возвращает обратно `Product` в виде доменной модели (`ProductCatalogACL` → `Order Service`);
- в сторону **ERP System**: `ProductCatalogACL` вызывает `ERP API` по HTTP, передавая `artNumber` (`ProductCatalogACL` → `ERP API`), а `ERP API` отвечает чужим `ErpProductDto` (`ERP API` → `ProductCatalogACL`), который `ACL` и переводит в доменный `Product`.

`ACL` -- удобное место и для устойчивости: раз все вызовы внешней системы идут через него, сюда же навешивают **Circuit Breaker**. При недоступности ERP он не роняет заказы, а отдаёт устаревшие данные из локального кэша.

**ACL + Circuit Breaker (Resilience4j):**

```java
@CircuitBreaker(name = "erp", fallbackMethod = "fallback")
public Optional<Product> findProduct(ProductId id) { ... }

private Optional<Product> fallback(ProductId id, Exception e) {
    return localProductCache.find(id);  // stale-данные из локального кэша
}
```

**Когда нужен ACL:**
- интеграция с legacy (SAP, 1C, SOAP), модель которой нельзя поменять;
- чужой upstream с «плохой» моделью, навязывающей свои концепции;
- высокая изменчивость внешнего API -- правки локализуются в одном слое.

## Q37. Какие стратегии Context Mapping применяются на практике?

**Context Mapping** — описание отношений и паттернов интеграции между `Bounded Context`. Выбор паттерна определяется балансом сил: насколько вы зависите от чужого контекста, можете ли влиять на его модель и насколько важно изолироваться от его изменений.

| Паттерн | Отношение | Описание |
|---------|-----------|----------|
| **Partnership** | Равноправные | Совместная разработка API; одна команда или тесная коллаборация |
| **Shared Kernel** | Общий код | Разделяемая часть модели; риск тесной связи |
| **Customer/Supplier** | Downstream/Upstream | Upstream диктует API; Downstream формулирует требования |
| **Conformist** | Подчинение | Downstream копирует модель Upstream без власти над ней |
| **Anti-Corruption Layer** | Защита | Переводчик; применяется при интеграции с legacy |
| **Open Host Service** | Открытый протокол | Upstream публикует стабильный API/протокол |
| **Published Language** | Стандартный формат | Общий формат обмена (JSON Schema, AsyncAPI) |
| **Separate Ways** | Нет интеграции | Контексты изолированы; максимальная автономность |

Пример карты отношений между контекстами:

- `Catalog Service` -- Upstream;
- `Order Service` -- Downstream;
- `Legacy ERP` -- Conformist Upstream.

Связи:

- `Catalog Service` → `Order Service` через `Open Host Service` + `Published Language`;
- `Legacy ERP` → `Order Service` через `Anti-Corruption Layer`.

**Прагматичные рекомендации:**
- Зафиксируйте карту контекстов (`Context Map`) до начала разработки
- Всегда используйте ACL при интеграции с внешними системами
- Минимизируйте `Shared Kernel` — он создаёт скрытые зависимости между командами
- Задокументируйте паттерн в ADR (Architecture Decision Record)

## Q38. (!) Как правильно проектировать Value Objects в Java?

**Value Object (VO)** — объект, который определяется своими атрибутами, а не идентификатором. Он **неизменяем**, поэтому его можно свободно копировать и расшаривать. На практике это вопрос про реализацию: интервьюер хочет увидеть `record`, валидацию в конструкторе, доменные методы вместо примитивов и грамотную персистенцию.

**Признаки правильного Value Object:**
1. **Неизменяемость**: все поля `final`, нет сеттеров -- любая операция возвращает новый VO, а не меняет текущий.
2. **Структурное равенство**: `equals/hashCode` по значениям -- два VO с одинаковыми полями равны.
3. **Самовалидация**: проверки в конструкторе, «невалидного» VO не существует в принципе.
4. **Выразительный API**: методы говорят на языке домена (`add`, `isGreaterThan`), а не на языке примитивов.

```java
// Java record — идеальная основа для Value Object
public record Money(BigDecimal amount, Currency currency) {

    // Compact constructor = валидация
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // Доменные методы возвращают новый VO (неизменяемость)
    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new CurrencyMismatchException(this.currency, other.currency);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency))
            throw new CurrencyMismatchException(this.currency, other.currency);
        return this.amount.compareTo(other.amount) > 0;
    }
    // equals/hashCode генерируются автоматически для record
}

// Типизированный идентификатор — тоже Value Object
public record OrderId(UUID value) {
    public OrderId { Objects.requireNonNull(value); }
    public static OrderId generate() { return new OrderId(UUID.randomUUID()); }
    public static OrderId of(String value) { return new OrderId(UUID.fromString(value)); }
}
```

**Использование в агрегате:**

```java
public class Order {
    private final OrderId id;           // типизированный ID
    private final CustomerId customerId;
    private Money totalAmount;           // Value Object для денег
    private Address shippingAddress;     // Value Object для адреса

    public Money calculateTotal() {
        return lines.stream()
            .map(l -> l.unitPrice().multiply(l.quantity()))
            .reduce(Money.zero(Currency.getInstance("RUB")), Money::add);
    }
}
```

**Персистенция через JPA `@Embeddable`:**

```java
@Embeddable
public class MoneyEmbeddable {
    @Column(name = "amount")   private BigDecimal amount;
    @Column(name = "currency") private String currency;
    // Конвертация в/из Money через @AttributeConverter
}
```

**Антипаттерны:**
- `Money` с сеттерами — VO должен быть immutable
- Использование `BigDecimal` вместо `Money` в сигнатурах методов — теряется выразительность
- Проверка валидности снаружи VO: `if (amount < 0) throw ...` в сервисе — логика должна быть в конструкторе VO

---

## See also

- [Микросервисы](microservices-interview.md) — DDD как основа декомпозиции, Bounded Context ↔ микросервис
- [Design Patterns](../design-patterns/design-patterns-interview.md) — GoF-паттерны в контексте доменного моделирования (Repository, Factory, Strategy)
- [Event-Driven паттерны](event-driven-patterns-interview.md) — доменные события, Outbox Pattern и событийная архитектура
- [Clean Architecture](clean-architecture-interview.md) — слоёная архитектура, изоляция домена, порты и адаптеры
- [CQRS и Event Sourcing](cqrs-event-sourcing-interview.md) — CQRS как надстройка над доменной моделью, Event Sourcing для агрегатов
- [Распределённые системы](distributed-systems-interview.md) — согласованность и транзакции между контекстами
- [Паттерны согласованности](consistency-patterns-interview.md) — eventual consistency в DDD, Saga Pattern
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) — Circuit Breaker для Anti-Corruption Layer при межконтекстных вызовах
- [Архитектура баз данных](../databases/database-architecture-interview.md) — стратегии персистенции агрегатов: CRUD vs Event Sourcing

- [API Gateway](api-gateway-interview.md)
- [BFF Pattern](bff-pattern-interview.md)
- [Стратегии кэширования](caching-strategies-interview.md)
- [CAP-теорема](cap-theorem-interview.md)
- [Clean Architecture](clean-architecture-interview.md)
- [Паттерны согласованности](consistency-patterns-interview.md)
- [Шпаргалка: Domain-Driven Design (DDD)](../../architecture/ddd.md) — теория
