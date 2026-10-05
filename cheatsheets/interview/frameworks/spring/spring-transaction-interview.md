---
title: "Вопросы на собеседовании: Транзакции в Spring"
description: "Spring @Transactional: propagation, isolation levels, rollback rules, self-invocation, readOnly, TransactionManager"
tags:
  - interview
  - frameworks
  - spring-transaction-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Transactions"
  - "Spring Transactions interview"
  - "Spring транзакции вопросы"
prerequisites: []
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: Транзакции в Spring

`@Transactional` — самая часто задаваемая тема на Spring-собеседованиях. Проверяют понимание propagation, isolation levels, правил rollback и типичных ловушек (self-invocation, checked exceptions, видимость метода).

Дата последнего обновления: 2026-04-22

## Полезные ссылки

### Официальная документация

- [Spring Transaction Management](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction) — официальная документация
- [Baeldung: @Transactional](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring) — настройка транзакций
- [Baeldung: Propagation & Isolation](https://www.baeldung.com/spring-transactional-propagation-isolation) — все типы propagation и isolation

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Как Spring реализует @Transactional в стандартном режиме proxy?](#q1--как-spring-реализует-transactional-в-стандартном-режиме-proxy)
- [Q2. Когда нужен @EnableTransactionManagement?](#q2-когда-нужен-enabletransactionmanagement)
- [Q3. Какие условия нужны для перехвата @Transactional при стандартной контейнерной настройке proxy в Spring 6.2?](#q3-какие-условия-нужны-для-перехвата-transactional-при-стандартной-контейнерной-настройке-proxy-в-spring-62)

**Propagation**
- [Q4. (!) Какие типы Propagation существуют, что они задают и какой используется по умолчанию?](#q4--какие-типы-propagation-существуют-что-они-задают-и-какой-используется-по-умолчанию)
- [Q5. (!) Чем REQUIRES_NEW отличается от NESTED при уже открытой транзакции и поддерживаемой JDBC-вложенности?](#q5--чем-requires_new-отличается-от-nested-при-уже-открытой-транзакции-и-поддерживаемой-jdbc-вложенности)
- [Q6. Когда использовать MANDATORY и NEVER?](#q6-когда-использовать-mandatory-и-never)

**Isolation**
- [Q7. (!) Какие уровни изоляции поддерживает Spring?](#q7--какие-уровни-изоляции-поддерживает-spring)
- [Q8. Что такое dirty read, non-repeatable read, phantom read?](#q8-что-такое-dirty-read-non-repeatable-read-phantom-read)

**Rollback**
- [Q9. (!) По каким исключениям Spring откатывает транзакцию по умолчанию?](#q9--по-каким-исключениям-spring-откатывает-транзакцию-по-умолчанию)
- [Q10. Как настроить rollback для checked exceptions?](#q10-как-настроить-rollback-для-checked-exceptions)

**Подводные камни**
- [Q11. (!) Почему self-invocation ломает @Transactional?](#q11--почему-self-invocation-ломает-transactional)
- [Q12. Что делает readOnly = true?](#q12-что-делает-readonly--true)
- [Q13. Можно ли ставить @Transactional на интерфейс?](#q13-можно-ли-ставить-transactional-на-интерфейс)
- [Q14. Как работает @Transactional в многопоточном коде?](#q14-как-работает-transactional-в-многопоточном-коде)
- [Q15. Как тестировать транзакционный код?](#q15-как-тестировать-транзакционный-код)

---

## Q1. (!) Как Spring реализует @Transactional в стандартном режиме proxy?

Здесь рассматривается обычное императивное управление транзакциями в режиме **proxy**. При внешнем вызове Spring AOP-прокси передаёт управление `TransactionInterceptor`. Этот advice получает атрибуты `@Transactional` и обращается к выбранному `PlatformTransactionManager` до и после выполнения целевого метода.

Propagation определяет создание транзакции или участие в существующей. Возврат из метода не всегда означает физический commit: участвующий метод не завершает общую транзакцию независимо, а отметка rollback-only может привести к откату. Исключения обрабатываются по rollback-правилам.

```text
Внешний вызов → прокси → транзакционный менеджер → метод сервиса
             ← обработка завершения через менеджер ← результат
```

Целевой метод не обязан вручную вызывать begin/commit/rollback. Прокси не переписывает его байткод; прямой вызов `this.method()` минует перехватчик. Режим **AspectJ** использует weaving и рассматривается отдельно.

Spring AOP может использовать JDK-прокси по интерфейсам либо CGLIB-прокси на основе подкласса. В автоконфигурации Spring Boot 3.5.0 CGLIB включён по умолчанию; это настраивается через `spring.aop.proxy-target-class`. Ограничения методов разобраны в Q3.

---

## Q2. Когда нужен @EnableTransactionManagement?

`@EnableTransactionManagement` включает инфраструктуру обработки `@Transactional` в Java-конфигурации. Это один из способов включения: XML-аналог — `<tx:annotation-driven/>`. Сама аннотация на сервисе является метаданными и не заменяет эту инфраструктуру или настроенный менеджер транзакций.

В Spring без Boot аннотацию добавляют в Java-конфигурацию, если обработка ещё не включена другим способом:

```java
@Configuration
@EnableTransactionManagement
public class AppConfig {
    // Бины сервисов и подходящий TransactionManager настраиваются отдельно.
}
```

В Spring Boot пользователь обычно не добавляет её вручную. В **Boot 3.5.0** `TransactionAutoConfiguration` проверяет наличие класса `PlatformTransactionManager`; вложенная конфигурация включения требует бин `TransactionManager` и отсутствие пользовательской `AbstractTransactionManagementConfiguration`. Автоконфигурацию можно исключить. Поэтому одного `spring-tx` в classpath недостаточно, а утверждение «в Boot всегда всё включено» неверно.

XML-вариант: `<tx:annotation-driven transaction-manager="txManager"/>`. Для выполнения метода также должен разрешаться подходящий менеджер транзакций; при нескольких менеджерах его можно выбрать явно.

---

## Q3. Какие условия нужны для перехвата @Transactional при стандартной контейнерной настройке proxy в Spring 6.2?

Для стандартной контейнерной настройки **proxy в Spring Framework 6.2** проверяют четыре условия:

1. Включена инфраструктура обработки `@Transactional`, настроен и доступен для выбора подходящий менеджер транзакций.
2. Объект обрабатывается контейнером Spring и вызывается через созданный для него транзакционный прокси. Обычный `new MyService()` вне контейнера такого прокси не получает.
3. Вызов приходит извне через прокси. `this.method()` не применяет атрибуты вызываемого метода. При этом уже активная транзакция внешнего метода может продолжать действовать.
4. Метод допускает перехват выбранным видом прокси. JDK-прокси требует `public`-метод проксируемого интерфейса. CGLIB по умолчанию допускает также `protected` и package-private методы, но не `private` и `final`; `final`-класс нельзя проксировать через подкласс.

Поддержка непубличных методов CGLIB по умолчанию появилась в Spring 6.0. Настройка `publicMethodsOnly` может сузить обработку до публичных методов. Режим AspectJ и программное создание прокси не входят в условия этого вопроса.

```java
@Service
public class UserService {
    @Transactional
    public void save(User user) { /* бизнес-операция */ }

    @Transactional
    private void internalSave(User user) { /* CGLIB не перехватит этот метод */ }
}
```

Аннотация на private-методе не создаёт собственную транзакционную границу. Это не означает, что любой код private-метода обязательно выполняется вне транзакции: он может участвовать в контексте вызывающего метода.

---

## Q4. (!) Какие типы Propagation существуют, что они задают и какой используется по умолчанию?

`Propagation` задаёт поведение метода при наличии или отсутствии текущей транзакции. В Spring Framework 6.2 есть семь значений; по умолчанию используется `REQUIRED`. Уровень изоляции задаётся отдельно через `Isolation`.

| Тип | Есть активная TX | Нет активной TX |
|---|---|---|
| `REQUIRED` (дефолт) | Использует существующую | Создаёт новую |
| `REQUIRES_NEW` | Приостанавливает старую, создаёт новую | Создаёт новую |
| `NESTED` | Создаёт savepoint внутри текущей | Создаёт новую |
| `SUPPORTS` | Использует существующую | Выполняется без TX |
| `NOT_SUPPORTED` | Приостанавливает TX | Выполняется без TX |
| `MANDATORY` | Использует существующую | **Исключение** |
| `NEVER` | **Исключение** | Выполняется без TX |

Поддержка приостановки транзакций и вложенности зависит от менеджера; строка `NESTED` в таблице описывает JDBC savepoints при наличии такой поддержки. `SUPPORTS` без физической транзакции всё же может участвовать в синхронизации ресурсов менеджера.

**REQUIRED (по умолчанию):**
```java
@Transactional  // REQUIRED по умолчанию
public void updateOrder(Order order) {
    orderRepository.save(order);
    auditService.log(order);  // другой бин, метод с REQUIRED: присоединится к транзакции
}
```

**REQUIRES_NEW:**
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void auditLog(String event) {
    // Всегда в отдельной TX — коммитится независимо
    auditRepository.save(new AuditEntry(event));
}
```

---

## Q5. (!) Чем REQUIRES_NEW отличается от NESTED при уже открытой транзакции и поддерживаемой JDBC-вложенности?

Условия вопроса: внешняя транзакция уже открыта, вызов проходит через транзакционный прокси, менеджер и драйвер поддерживают JDBC savepoints и необходимые операции приостановки.

| Свойство | `REQUIRES_NEW` | `NESTED` |
|---|---|---|
| Механизм | Приостановка внешней и новая независимая транзакция | Savepoint в текущей физической транзакции |
| Фиксация | Независимая | Вместе с внешней транзакцией |
| Локальный откат | Откат независимой транзакции | Откат к savepoint |
| Последующий откат внешней | Не отменяет уже зафиксированные изменения внутренней | Отменяет и изменения вложенного шага |

Если исключение внутреннего вызова выходит из внешнего метода и подпадает под его rollback-правила, внешняя транзакция тоже может откатиться. Частичный откат сам по себе не гарантирует успешного завершения внешней операции.

```java
@Transactional
public void placeOrder(Order order) {
    orderRepository.save(order);
    try {
        inventoryService.updatePart(order); // другой бин: вызов через прокси
    } catch (InventoryStepException e) {
        // Здесь приложение решает, допустимо ли продолжать внешнюю операцию.
    }
}
```

`inventoryService.updatePart` можно настроить на `NESTED` для частичного отката шага либо на `REQUIRES_NEW` для независимого завершения. Для JDBC новая независимая транзакция обычно требует дополнительного соединения; нужно учитывать вместимость пула и блокировки ресурсов внешней транзакцией.

`DataSourceTransactionManager` поддерживает вложенность через JDBC savepoints, если это умеет драйвер. Нельзя обобщать это до «только этот менеджер» или «все менеджеры поддерживают любой режим». `JpaTransactionManager` также предусматривает JDBC savepoints при соответствующей настройке и доступе к соединению, но они не восстанавливают состояние `EntityManager` и его кэш. JPA не предоставляет семантику вложенной транзакции для сущностей; нельзя рассчитывать на полноценный частичный откат JPA-контекста.

Без внешней транзакции `NESTED` начинает новую, как `REQUIRED`; это другой сценарий. Независимая фиксация в БД также не делает внешнюю отправку уведомления автоматически транзакционной.

---

## Q6. Когда использовать MANDATORY и NEVER?

Оба — это «контрактные» propagation: они не создают и не приостанавливают транзакцию, а **проверяют, в правильном ли контексте вызван метод**, и кидают исключение, если нет. Так задают допустимый контекст вызова метода.

**`MANDATORY`** — метод обязан выполняться внутри уже открытой транзакции (своей не создаёт). Удобно для «строительных блоков», которые сами по себе не имеют смысла вне транзакции:
```java
@Transactional(propagation = Propagation.MANDATORY)
public void updateBalance(Account acc, BigDecimal amount) {
    // Защита: этот метод нельзя вызывать без транзакции
    acc.setBalance(acc.getBalance().add(amount));
    repository.save(acc);
}
```
Если вызвать без активной транзакции → `IllegalTransactionStateException`.

**`NEVER`** — метод, наоборот, не должен выполняться внутри транзакции (если контракт операции требует отказа при наличии активной транзакции):
```java
@Transactional(propagation = Propagation.NEVER)
public void exportToCsv() {
    // Контракт этого метода запрещает вызов внутри активной транзакции
}
```
Если вызвать внутри активной транзакции → `IllegalTransactionStateException`.

**Не путать `NEVER` с `NOT_SUPPORTED`:** `NEVER` бросает исключение при наличии транзакции, а `NOT_SUPPORTED` приостанавливает её и выполняет метод без транзакции, если менеджер поддерживает приостановку. Длительность метода сама по себе не диктует выбор режима.

---

## Q7. (!) Какие уровни изоляции поддерживает Spring?

`Isolation` в Spring Framework 6.2 содержит `READ_UNCOMMITTED`, `READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE` и `DEFAULT`. Последнее значение использует стандартный уровень ресурса. Spring передаёт настройку менеджеру; изоляцию данных обеспечивает ресурс, например СУБД. Доступность конкретного уровня зависит от менеджера и драйвера.

Таблица ниже описывает минимальные ограничения трёх аномалий для стандартных уровней, а не обязательное поведение каждой СУБД:

| Уровень | Dirty read | Non-repeatable read | Phantom read |
|---|---|---|---|
| `READ_UNCOMMITTED` | Допускается | Допускается | Допускается |
| `READ_COMMITTED` | Запрещено | Допускается | Допускается |
| `REPEATABLE_READ` | Запрещено | Запрещено | Допускается |
| `SERIALIZABLE` | Запрещено | Запрещено | Запрещено |

В PostgreSQL 17 `READ_UNCOMMITTED` ведёт себя как `READ_COMMITTED`; `REPEATABLE_READ` предотвращает и фантомное чтение, но допускает аномалии сериализации. Стандартный уровень PostgreSQL — `READ_COMMITTED`, хотя администратор или приложение могут изменить настройки. Более строгая изоляция не означает автоматически больше блокировок: цена зависит от реализации, конфликтов и повторов транзакций.

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public List<Product> getProducts() { /* чтение данных */ }
```

Настройка применяется к новой транзакции. При присоединении к существующей её изоляция по умолчанию сохраняется; проверка `validateExistingTransactions` у менеджера позволяет отклонить несовместимое объявление.

---

## Q8. Что такое dirty read, non-repeatable read, phantom read?

Это три наблюдаемых эффекта конкурентных транзакций. Уровни изоляции ограничивают их согласно таблице Q7; конкретная реализация может давать более сильные гарантии. В PostgreSQL 17, например, фантомное чтение предотвращает уже `REPEATABLE_READ`.

**Dirty Read (грязное чтение)** — транзакция читает данные, которые другая ещё не закоммитила и может откатить. Если другая транзакция откатится, прочитанное изменение не войдёт в зафиксированное состояние:
```
TX1: UPDATE balance SET amount = 1000  (не закоммичено)
TX2: SELECT amount  → получает 1000
TX1: ROLLBACK
TX2 прочитал впоследствии отменённое изменение → dirty read
```

**Non-repeatable Read (неповторяемое чтение)** — один и тот же SELECT по одной строке внутри транзакции возвращает разные значения, потому что между чтениями другая транзакция изменила и закоммитила строку:
```
TX1: SELECT price FROM products WHERE id = 1  → 100
TX2: UPDATE products SET price = 150 WHERE id = 1; COMMIT
TX1: SELECT price FROM products WHERE id = 1  → 150 (другой результат!)
```

**Phantom Read (фантомное чтение)** — повторный SELECT по диапазону возвращает другой набор строк: появились (или исчезли) записи, которые другая транзакция вставила/удалила в этот диапазон. Набор может измениться и после UPDATE, если строка начинает или перестаёт удовлетворять условию. Для non-repeatable сравнивают ранее прочитанные данные, которые могут также оказаться удалены; для phantom — набор по условию:
```
TX1: SELECT * FROM orders WHERE amount > 500  → 5 строк
TX2: INSERT INTO orders (amount) VALUES (600); COMMIT
TX1: SELECT * FROM orders WHERE amount > 500  → 6 строк (phantom!)
```

---

## Q9. (!) По каким исключениям Spring откатывает транзакцию по умолчанию?

Стандартная политика Spring вызывает rollback, если из транзакционного метода через перехватчик выходит **`RuntimeException` или `Error`**, включая их подклассы. Checked-исключение само по себе не требует rollback.

```java
@Transactional
public void process() throws IOException {
    repository.save(data);
    throw new IOException("Ошибка файла"); // само исключение не требует rollback
}
```

При новой транзакции без rollback-only и других ошибок Spring попытается выполнить commit. Это не обещание, что фиксация обязательно удастся: ресурс мог уже отметить транзакцию для отката, а commit может завершиться ошибкой. Пойманное внутри метода исключение также не равно исключению, вышедшему через перехватчик.

В Spring Framework 6.2 глобальную политику можно изменить через `@EnableTransactionManagement(rollbackOn = ALL_EXCEPTIONS)`, тогда checked-исключения тоже требуют rollback. Такая явно выбранная политика уже отличается от стандартной. Локальные `rollbackFor` и `noRollbackFor` позволяют задать правила для типов (Q10).

---

## Q10. Как настроить rollback для checked exceptions?

Для отката по checked-типу используют `@Transactional(rollbackFor = IOException.class)`. Правило учитывает и подклассы указанного типа. `noRollbackFor` задаёт противоположное решение. При нескольких подходящих правилах побеждает наиболее конкретное; лучше избегать противоречащих правил одинаковой специфичности.

```java
@Transactional(rollbackFor = {IOException.class, SQLException.class})
public void importFile() throws IOException, SQLException {
    // Исключение, вышедшее из метода, обрабатывает транзакционный перехватчик.
}
```

`rollbackFor = Exception.class` охватывает checked-исключения, не отменяя стандартный откат по `Error`. Глобальная настройка `rollbackOn = ALL_EXCEPTIONS` в Spring 6.2 даёт ещё один способ изменить политику, а локальные правила уточняют её.

Программная отметка rollback-only полезна, когда исключение обработано внутри метода, но изменения сохранять нельзя:

```java
@Transactional
public void saveWithManualRollback() {
    try {
        performCheckedOperation(); // метод объявляет SomeCheckedException
    } catch (SomeCheckedException e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        // Исключение обработано, но транзакция должна откатиться.
    }
}
```

У новой транзакции локальная отметка `setRollbackOnly()` приводит к ожидаемому откату и не обязана вызывать `UnexpectedRollbackException`. Типичная причина этого исключения — попытка внешнего commit после того, как участник общей транзакции уже пометил её для отката. Простое подавление исключения такую отметку не снимает. Не любой пойманный сбой автоматически помечает транзакцию: это зависит от перехватчика и ресурса.

---

## Q11. (!) Почему self-invocation ломает @Transactional?

В стандартном режиме proxy транзакционный advice вызывается через прокси (см. Q1). Когда метод вызывает другой метод того же бина через `this.method()` (или просто `method()`), обращение идёт **напрямую к объекту, минуя прокси** — транзакционный advice не вызывается, поэтому новая транзакция или `REQUIRES_NEW` не создаётся, аннотация на вызванном методе фактически игнорируется. При этом уже активная транзакция внешнего метода сохраняется; обход аннотации не означает обязательного отсутствия транзакции. AspectJ weaving не имеет этого ограничения proxy-режима.

```java
@Service
public class OrderService {

    @Transactional
    public void processAll(List<Order> orders) {
        for (Order o : orders) {
            processOne(o);  // Self-invocation: REQUIRES_NEW не применяется, остаётся внешняя транзакция
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Order order) {
        // Работает только если вызван извне через прокси
    }
}
```

**Решения:**

1. **Вынести в отдельный бин** (рекомендуется):
```java
@Service
public class OrderItemService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Order order) { ... }
}

@Service
public class OrderService {
    @Autowired OrderItemService orderItemService;

    @Transactional
    public void processAll(List<Order> orders) {
        orders.forEach(orderItemService::processOne);  // через прокси ✓
    }
}
```

2. **Вызвать через внедрённую ссылку на прокси**, а не через `this`. Self-injection с `@Lazy` требует подходящей конфигурации зависимостей и усложняет устройство бина. Обычно проще выделить вызываемую операцию в отдельный сервис, как в примере выше.

---

## Q12. Что делает readOnly = true?

`@Transactional(readOnly = true)` сообщает транзакционной подсистеме о читающем характере операции. По контракту это **подсказка**, допускающая оптимизации; нельзя обещать одинаковый эффект или запрет любой записи для всех менеджеров.

```java
@Transactional(readOnly = true)
public List<Product> findAll() { /* чтение данных */ }
```

В `HibernateJpaDialect` Spring 6.2.7 для read-only транзакции выбирается `FlushMode.MANUAL`. Для локального транзакционного `EntityManager` также устанавливается `Session.setDefaultReadOnly(true)`. Это разные меры: подавление автоматического flush и режим чтения загружаемых сущностей. Нельзя выводить из них отсутствие dirty checking для любой ранее загруженной сущности в любой конфигурации.

`DataSourceTransactionManager` с `enforceReadOnly` может задавать поддерживаемый СУБД режим, запрещающий операции изменения данных. Драйвер тоже может интерпретировать подсказку. Поэтому неверны оба безусловных утверждения: «запись всегда запрещена» и «запись никогда не запрещается».

Флаг не устанавливает `SERIALIZABLE`, не выбирает read replica без настройки маршрутизации и не заменяет контроль прав доступа. Его эффект проверяют на используемых версиях менеджера, ORM и драйвера.

---

## Q13. Можно ли ставить @Transactional на интерфейс?

В Spring Framework 6.2 — **да**: аннотации интерфейса распознают и JDK-, и class-based (CGLIB) прокси. Поддержка target-class прокси существует с Spring 5.0. Утверждение «с CGLIB не работает» для этой версии устарело.

Spring рекомендует аннотировать методы конкретных классов. Существенное ограничение — **AspectJ weaving**: аннотации, объявленные только в интерфейсе, этим механизмом не распознаются. Java не наследует аннотации интерфейса на реализацию автоматически; у Spring-прокси есть собственный поиск метаданных.

```java
public interface UserService {
    void save(User user);
}

@Service
public class UserServiceImpl implements UserService {
    @Override
    @Transactional
    public void save(User user) { /* бизнес-операция */ }
}
```

Это рекомендация по размещению метаданных и совместимости режимов. Она не устанавливает универсального архитектурного запрета на транзакционные условия в контракте интерфейса.

---

## Q14. Как работает @Transactional в многопоточном коде?

Нужно различать две модели. Обычный `PlatformTransactionManager` связывает транзакционные ресурсы с текущим потоком. Новый поток внутри метода не наследует эту транзакцию автоматически. `ReactiveTransactionManager` использует **Reactor Context**: участвующие операции должны выполняться в соответствующем контексте реактивной цепочки, который не равен ThreadLocal одного потока.

```java
@Transactional
public void processAsync() {
    CompletableFuture.runAsync(() -> {
        transactionalService.save(data); // другой бин, вызов через прокси
    });
}
```

Императивная родительская транзакция в задачу автоматически не переносится. Вызванный через прокси метод `transactionalService.save` с `@Transactional` может начать собственную транзакцию в рабочем потоке. Обычного `REQUIRED` достаточно, если там нет активной транзакции; `REQUIRES_NEW` не нужен только ради перехода между потоками.

При стандартной настройке Spring Data JPA `repository.save()` тоже имеет транзакционное поведение. Поэтому «в новом потоке save обязательно выполняется без транзакции» неверно: возможна собственная транзакция репозитория. Её успешный commit не отменяется последующим rollback родительского метода.

Реактивный контекст требует отдельного анализа: корректное переключение потока в цепочке не означает обязательной потери транзакции, но произвольная новая задача не получает её автоматически. Транзакционную границу выбирают исходя из модели менеджера и требуемой атомарности операции.

---

## Q15. Как тестировать транзакционный код?

В интеграционных тестах проверяют реальные границы **rollback**, **commit** и propagation. При включённом Spring TestContext и настроенном `PlatformTransactionManager` обычный `@Transactional`-тест запускается в тестовой транзакции, которая по умолчанию откатывается после теста. `@Commit` выбирает фиксацию, `@Rollback` — откат.

```java
@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired OrderService orderService;

    @Test
    void shouldSaveOrder() {
        orderService.placeOrder(new Order("product"));
        // Проверяем состояние, участвующее в тестовой транзакции.
        // После теста именно эта транзакция по умолчанию откатится.
    }
}
```

Тестовый rollback не обеспечивает очистку любых эффектов. Уже зафиксированная **REQUIRES_NEW**-транзакция переживёт его; работа в другом потоке тоже может завершаться независимо. Такие данные очищают отдельно. Это особенно важно при тестах с принудительным выполнением в другом потоке из-за timeout.

Для проверки фиксации нужно завершить соответствующую транзакцию и читать результат с подходящей видимостью. Можно отключить тестовую обёртку через `@Transactional(propagation = Propagation.NOT_SUPPORTED)` и управлять границами сервиса либо использовать `TestTransaction` для явного завершения и начала тестовых транзакций. Это способы построить проверку, а не утверждение, что REQUIRES_NEW невозможно тестировать с обёрткой.

При ORM нужен flush там, где проверяются ограничения БД; для чтения фактического результата учитывают кэш persistence context, например очищают его или читают из нового контекста. Нельзя принять наличие сущности в кэше за доказательство успешного commit.

---

## See also

- [Spring Framework](spring-framework-interview.md) — AOP, прокси-механизм
- [Spring AOP](spring-aop-interview.md) — как именно реализованы @Transactional и self-invocation
- [Spring Data JPA](spring-data-jpa-interview.md) — транзакции в JPA-репозиториях
- [Spring Data JDBC](spring-data-jdbc-interview.md) — транзакции в JDBC
- [Hibernate](../../databases/hibernate-interview.md) — dirty checking, first-level cache в транзакции
- [Транзакции БД](../../databases/database-transactions-interview.md) — ACID, isolation levels на уровне СУБД
- [Spring Events](spring-events-interview.md) — @TransactionalEventListener (AFTER_COMMIT)
- [Spring Testing](spring-testing-interview.md) — @Transactional в тестах, @Commit
