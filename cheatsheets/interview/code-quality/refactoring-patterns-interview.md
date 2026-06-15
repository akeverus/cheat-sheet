---
title: "Вопросы на собеседовании: Паттерны рефакторинга"
description: "Краткие ответы по рефакторингу: приёмы (Extract Method, Replace Conditional и др.), связь с тестами, «запахи кода», legacy."
tags:
  - interview
  - code-quality
  - refactoring-patterns-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Паттерны рефакторинга"
  - "Refactoring patterns interview"
  - "Рефакторинг собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: Паттерны рефакторинга

Краткие ответы по рефакторингу: приёмы (`Extract Method`, `Replace Conditional` и др.), связь с тестами, «запахи кода», legacy.

## Полезные ссылки

### Официальная документация

- [Refactoring Guru](https://refactoring.guru/refactoring)
- [Martin Fowler - Refactoring](https://refactoring.com/)
- [Baeldung — Refactoring in Java](https://www.baeldung.com/java-refactoring)
- [Code Smells](https://www.baeldung.com/cs/code-smells) — виды "запахов кода" с примерами
- [An Introduction to Refactoring with IntelliJ IDEA](https://www.baeldung.com/intellij-refactoring) — рефакторинг в IntelliJ IDEA
- [Clean Coding in Java](https://www.baeldung.com/java-clean-code) — принципы чистого кода

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)
- [Введение](#введение)

**Основы рефакторинга**
- [Q1. (!) Что такое рефакторинг и когда его проводить?](#q1--что-такое-рефакторинг-и-когда-его-проводить)
- [Q2. (!) Рефакторинг без изменения поведения — что это значит?](#q2--рефакторинг-без-изменения-поведения--что-это-значит)
- [Q3. (!) Что такое Extract Method и когда применять?](#q3--что-такое-extract-method-и-когда-применять)
- [Q4. Что такое Replace Conditional with Polymorphism?](#q4-что-такое-replace-conditional-with-polymorphism)
- [Q5. Что такое Move Method / Move Field?](#q5-что-такое-move-method--move-field)

**Паттерны рефакторинга (Fowler)**
- [Q6. Что такое Introduce Parameter Object?](#q6-что-такое-introduce-parameter-object)
- [Q7. Что такое Replace Magic Numbers with Constants?](#q7-что-такое-replace-magic-numbers-with-constants)
- [Q8. Что такое Replace Type Code with State/Strategy?](#q8-что-такое-replace-type-code-with-statestrategy)
- [Q9. Что такое Extract Class / Extract Interface?](#q9-что-такое-extract-class--extract-interface)
- [Q10. Что такое Inline Method / Inline Variable?](#q10-что-такое-inline-method--inline-variable)
- [Q11. Что такое Replace Inheritance with Delegation (и наоборот)?](#q11-что-такое-replace-inheritance-with-delegation-и-наоборот)
- [Q12. Что такое Replace Exception with Test?](#q12-что-такое-replace-exception-with-test)
- [Q13. Что такое Introduce Null Object?](#q13-что-такое-introduce-null-object)

**Рефакторинг и тестирование**
- [Q14. Рефакторинг и тесты — как связаны?](#q14-рефакторинг-и-тесты--как-связаны)
- [Q15. Что такое «запахи кода» (code smells)?](#q15-что-такое-запахи-кода-code-smells)
- [Q16. Рефакторинг больших классов (God Class)?](#q16-рефакторинг-больших-классов-god-class)
- [Q17. Рефакторинг длинных методов?](#q17-рефакторинг-длинных-методов)
- [Q18. Рефакторинг дублирования кода?](#q18-рефакторинг-дублирования-кода)

**Рефакторинг в legacy и больших системах**
- [Q19. Рефакторинг в legacy-коде без тестов?](#q19-рефакторинг-в-legacy-коде-без-тестов)
- [Q20. Что такое Strangler Fig при рефакторинге системы?](#q20-что-такое-strangler-fig-при-рефакторинге-системы)
- [Q21. Рефакторинг и производительность?](#q21-рефакторинг-и-производительность)
- [Q22. Рефакторинг API и обратная совместимость?](#q22-рефакторинг-api-и-обратная-совместимость)
- [Q23. Какие инструменты помогают при рефакторинге в Java?](#q23-какие-инструменты-помогают-при-рефакторинге-в-java)
- [Q24. Рефакторинг многопоточного кода?](#q24-рефакторинг-многопоточного-кода)
- [Q25. Когда рефакторинг не стоит делать?](#q25-когда-рефакторинг-не-стоит-делать)

**Практики и метрики**
- [Q26. Что такое Replace Method with Method Object?](#q26-что-такое-replace-method-with-method-object)
- [Q27. Рефакторинг и покрытие тестами: какой минимум?](#q27-рефакторинг-и-покрытие-тестами-какой-минимум)
- [Q28. Как рефакторить без «большого взрыва» в монолите?](#q28-как-рефакторить-без-большого-взрыва-в-монолите)
- [Q29. Рефакторинг и код-ревью: на что смотреть?](#q29-рефакторинг-и-код-ревью-на-что-смотреть)
- [Q30. Какие метрики использовать до и после рефакторинга?](#q30-какие-метрики-использовать-до-и-после-рефакторинга)

**Дополнительные приёмы Фаулера и принципы**
- [Q31. (!) Что такое Decompose Conditional и когда применять?](#q31--что-такое-decompose-conditional-и-когда-применять)
- [Q32. Что такое Separate Query from Modifier?](#q32-что-такое-separate-query-from-modifier)
- [Q33. Как рефакторить нарушения SOLID-принципов на практике?](#q33-как-рефакторить-нарушения-solid-принципов-на-практике)
- [Q34. Что такое рефакторинг «по DRY»: Consolidate Duplicate Conditional Fragments?](#q34-что-такое-рефакторинг-по-dry-consolidate-duplicate-conditional-fragments)

**Продвинутые паттерны рефакторинга**
- [Q35. Как реализовать паттерн Strangler Fig в Spring Boot?](#q35-как-реализовать-паттерн-strangler-fig-в-spring-boot)
- [Q36. Какими приёмами заменить сложные if/else-цепочки?](#q36-какими-приёмами-заменить-сложные-ifelse-цепочки)
- [Q37. Что такое Replace Primitive with Object (паттерн Value Object)?](#q37-что-такое-replace-primitive-with-object-паттерн-value-object)
- [Q38. Как бороться с длинными списками параметров (Introduce Parameter Object)?](#q38-как-бороться-с-длинными-списками-параметров-introduce-parameter-object)
- [Q39. Как безопасно рефакторить схему БД (Expand-Contract паттерн)?](#q39-как-безопасно-рефакторить-схему-бд-expand-contract-паттерн)
- [Q40. Что такое Feature Envy и как его лечить?](#q40-что-такое-feature-envy-и-как-его-лечить)
- [Q41. Что такое Shotgun Surgery и как с ним бороться?](#q41-что-такое-shotgun-surgery-и-как-с-ним-бороться)
- [Q42. Когда Extract Method недостаточно и нужен Method Object?](#q42-когда-extract-method-недостаточно-и-нужен-method-object)

## Введение

**Рефакторинг** — изменение внутренней структуры кода без изменения внешнего поведения. На собеседовании часто спрашивают о приёмах (по Фаулеру), связи с [тестами](../testing/unit-testing-interview.md), «запахах кода» и стратегиях в legacy. Рефакторинг тесно связан с [паттернами проектирования](../design-patterns/design-patterns-interview.md) — многие приёмы рефакторинга приводят код к известным паттернам.

## Q1. (!) Что такое рефакторинг и когда его проводить?

**Рефакторинг** — это изменение внутренней структуры кода (именование, разбиение, перенос) без изменения наблюдаемого поведения. Цель — сделать код понятнее и легче в изменении, не трогая то, что видит пользователь и вызывающий код.

**Когда проводить:**

- **Перед добавлением фичи** — сначала привести код в форму, в которую новую логику легко встроить, затем добавлять логику.
- **При исправлении бага** — упростить место бага, чтобы причина стала видна, и снизить шанс повторной ошибки.
- **При «запахах кода»** — дублирование, длинные методы, большие классы; рефакторинг убирает накопленный беспорядок.

**Почему не смешивать рефакторинг и фичу в одном коммите.** Когда в диффе и перенос кода, и новая логика, ревьюер не может отличить «просто переехало» от «изменилось поведение», а откат тянет за собой обе вещи. Поэтому: один PR — только рефакторинг (тесты остаются зелёными), второй — новая логика. Так проще [ревью](code-review-interview.md) и откат.

**Правило «три удара»** (rule of three) — про дублирование: при первом случае оставляем как есть, при втором замечаем и терпим, при третьем выносим в общий метод/класс. Раннее обобщение по одному-двум случаям часто промахивается мимо реальной абстракции.

Рефакторинг без [тестов](../testing/unit-testing-interview.md) рискован: нечем поймать регрессию. Сначала покрыть критичные места тестами, затем менять структуру.

**На практике:** перед фичей в модуле X — отдельный тикет «Рефакторинг X: вынести дублирование, разбить метод `processOrder`»; после merge рефакторинга — тикет с самой фичей. В сообщении коммита — `refactor: extract calculateDiscount` без `feat:` в том же коммите. Каждый шаг делать через IDE (`IntelliJ Refactor` -> `Extract Method`, `Rename`) с прогоном тестов после каждого изменения.

## Q2. (!) Рефакторинг без изменения поведения — что это значит?

«Без изменения поведения» означает, что наблюдаемое снаружи поведение остаётся прежним: те же входы дают те же выходы, те же исключения, те же побочные эффекты. Меняется только внутренняя структура — имена, разбиение на методы/классы, расположение кода.

**Как это гарантировать.** Доказательством служат тесты (unit/integration): перед рефакторингом они зелёные, после каждого шага — тоже. Если зелёный тест вдруг падает — поведение изменилось, и шаг нужно откатить. Именно поэтому рефакторинг безопасен: тесты ловят случайную регрессию до того, как она дойдёт до прода.

Цикл безопасного рефакторинга по шагам:

1. Тесты зелёные — стартовое состояние.
2. Делаем шаг рефакторинга (`Extract`, `Rename`, `Move`).
3. Снова прогоняем тесты:
   - если тесты зелёные и **ещё есть шаги** → возвращаемся к шагу 2;
   - если тесты зелёные и **шагов больше нет** → готово;
   - если **тест упал** → откатываем шаг и возвращаемся к состоянию «тесты зелёные».

**На практике:** перед рефакторингом запустить полный набор тестов и зафиксировать результат; после каждого шага (`Extract Method`, `Rename`) — снова запустить. Если тест упал — откатить шаг или исправить логику. Публичный API (сигнатуры методов, контракты) в рамках «чистого» рефакторинга не трогать: смена сигнатуры — это уже изменение поведения для вызывающего кода, и её выносят в отдельный коммит с миграцией вызовов или пометкой `@Deprecated`.

## Q3. (!) Что такое Extract Method и когда применять?

`Extract Method` — вынос фрагмента кода в отдельный метод с говорящим именем. Имя метода становится комментарием к фрагменту: вместо того чтобы вчитываться в строки, читаешь название действия.

**Когда применять:**

- фрагмент можно назвать одним действием (`validateOrder`, `calculateDiscount`);
- метод слишком длинный, и в нём смешаны несколько уровней абстракции;
- один и тот же фрагмент повторяется в нескольких местах.

Приём улучшает читаемость и открывает возможность переиспользовать логику.

**До рефакторинга:**

```java
public void processOrder(Order order) {
    // валидация
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Пустой заказ");
    }
    if (order.getCustomer() == null) {
        throw new IllegalArgumentException("Нет покупателя");
    }

    // расчёт скидки
    BigDecimal discount = BigDecimal.ZERO;
    if (order.getCustomer().isVip()) {
        discount = order.getTotal()
            .multiply(new BigDecimal("0.10"));
    }
    if (order.getItems().size() > 10) {
        discount = discount.add(
            order.getTotal().multiply(new BigDecimal("0.05"))
        );
    }

    // применение и сохранение
    order.setDiscount(discount);
    order.setFinalPrice(order.getTotal().subtract(discount));
    orderRepository.save(order);
    notificationService.sendConfirmation(order);
}
```

**После рефакторинга:**

```java
public void processOrder(Order order) {
    validateOrder(order);
    BigDecimal discount = calculateDiscount(order);
    applyAndSave(order, discount);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Пустой заказ");
    }
    if (order.getCustomer() == null) {
        throw new IllegalArgumentException("Нет покупателя");
    }
}

private BigDecimal calculateDiscount(Order order) {
    BigDecimal discount = BigDecimal.ZERO;
    if (order.getCustomer().isVip()) {
        discount = order.getTotal()
            .multiply(VIP_DISCOUNT_RATE);
    }
    if (order.getItems().size() > BULK_THRESHOLD) {
        discount = discount.add(
            order.getTotal().multiply(BULK_DISCOUNT_RATE)
        );
    }
    return discount;
}

private void applyAndSave(Order order, BigDecimal discount) {
    order.setDiscount(discount);
    order.setFinalPrice(order.getTotal().subtract(discount));
    orderRepository.save(order);
    notificationService.sendConfirmation(order);
}
```

Метод-оркестратор `processOrder` теперь читается как три шага высокого уровня: проверить, посчитать, применить. Детали каждого шага спрятаны в отдельных методах.

**Эмпирическое правило:** имя метода должно описывать действие, а не детали реализации. Если в имя просятся «и», «или» (`validateAndSave`) — фрагмент делает несколько вещей, и его стоит разбить на отдельные методы. Выполнять приём лучше через IDE (`IntelliJ`, `Eclipse`, `Refactor` -> `Extract Method`) — она сама вычислит параметры и тип возврата.

## Q4. Что такое Replace Conditional with Polymorphism?

Это замена цепочки `if/else` или `switch` по типу/коду на полиморфизм: каждый вариант становится отдельным классом с общим интерфейсом, а вызывающий код просто вызывает метод интерфейса, не зная, какой класс под ним. Ветвление по типу исчезает — за выбор реализации отвечает диспетчеризация языка (или `Map`/фабрика). Приём часто приводит код к паттерну [Strategy](../design-patterns/design-patterns-interview.md).

Главная выгода — соблюдение OCP: добавление нового варианта не требует править старый `switch`, где легко что-то сломать.

**До рефакторинга:**

```java
public BigDecimal calculateShipping(Order order) {
    switch (order.getDeliveryType()) {
        case STANDARD:
            return order.getWeight()
                .multiply(new BigDecimal("5.0"));
        case EXPRESS:
            return order.getWeight()
                .multiply(new BigDecimal("10.0"))
                .add(new BigDecimal("200"));
        case PICKUP:
            return BigDecimal.ZERO;
        default:
            throw new IllegalArgumentException(
                "Unknown delivery: " + order.getDeliveryType()
            );
    }
}
```

**После рефакторинга:**

```java
public interface ShippingCalculator {
    BigDecimal calculate(Order order);
}

public class StandardShipping implements ShippingCalculator {
    public BigDecimal calculate(Order order) {
        return order.getWeight().multiply(new BigDecimal("5.0"));
    }
}

public class ExpressShipping implements ShippingCalculator {
    public BigDecimal calculate(Order order) {
        return order.getWeight()
            .multiply(new BigDecimal("10.0"))
            .add(new BigDecimal("200"));
    }
}

public class PickupShipping implements ShippingCalculator {
    public BigDecimal calculate(Order order) {
        return BigDecimal.ZERO;
    }
}

// Использование через Map или фабрику
private final Map<DeliveryType, ShippingCalculator> calculators = Map.of(
    STANDARD, new StandardShipping(),
    EXPRESS,  new ExpressShipping(),
    PICKUP,   new PickupShipping()
);

public BigDecimal calculateShipping(Order order) {
    return calculators.get(order.getDeliveryType())
        .calculate(order);
}
```

Структура классов после рефакторинга:

- `ShippingCalculator` — интерфейс с методом `calculate(Order) BigDecimal`.
- Реализации интерфейса `ShippingCalculator` (`implements`), у каждой свой метод `calculate(Order) BigDecimal`:
  - `StandardShipping`;
  - `ExpressShipping`;
  - `PickupShipping`.
- `OrderService` — поле `calculators: Map` и метод `calculateShipping(Order) BigDecimal`; зависит от `ShippingCalculator` (вызывает реализацию через `Map`).

Добавление нового типа доставки = новый класс + строка в `Map`, без правок в условной логике.

## Q5. Что такое Move Method / Move Field?

**Move Method** — перенос метода в тот класс, чьи данные он использует чаще всего (или куда он логически относится). **Move Field** — перенос поля в класс, который владеет им по смыслу.

**Зачем.** Когда метод живёт не там, где его данные, он вынужден дёргать чужие геттеры — растёт сцепление между классами, логика «размазана». Переезд метода к данным повышает связность (cohesion) внутри класса-владельца и снижает сцепление (coupling) между классами: меньше зависимостей наружу, проще менять. Это типичное лечение запаха Feature Envy (см. Q40).

**До рефакторинга:**

```java
// Метод в OrderService использует только поля Order
public class OrderService {
    public String formatAddress(Order order) {
        return order.getCity() + ", "
            + order.getStreet() + " "
            + order.getBuilding();
    }
}
```

**После рефакторинга:**

```java
public class Order {
    private String city;
    private String street;
    private String building;

    public String formatAddress() {
        return city + ", " + street + " " + building;
    }
}

// В сервисе — делегирование с @Deprecated до миграции
public class OrderService {
    @Deprecated(since = "2.0", forRemoval = true)
    public String formatAddress(Order order) {
        return order.formatAddress();
    }
}
```

**Подводный камень:** перенос меняет место, откуда вызывается логика, — это может сломать вызывающий код. Поэтому старый метод оставляют как тонкую обёртку с делегированием и пометкой `@Deprecated`, пока все вызовы не мигрируют на новый.

## Q6. Что такое Introduce Parameter Object?

Группа параметров, которые постоянно ходят вместе, заменяется одним объектом (record или классом). Длинный список аргументов — это сигнал, что параметры образуют единое понятие (например, диапазон дат), которому просится имя.

**Что это даёт:**

- меньше параметров в сигнатуре — метод проще читать и вызывать;
- добавление нового поля не ломает сигнатуру метода (правка только в объекте);
- объекту-обёртке можно дать поведение, например валидацию (`from` не позже `to`).

**До рефакторинга:**

```java
public List<Transaction> findTransactions(
        long userId,
        LocalDate from,
        LocalDate to,
        String currency,
        BigDecimal minAmount) {
    // ... фильтрация по всем параметрам
}
```

**После рефакторинга:**

```java
public record DateRange(LocalDate from, LocalDate to) {
    public DateRange {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                "from не может быть позже to"
            );
        }
    }
}

public record TransactionFilter(
        long userId,
        DateRange dateRange,
        String currency,
        BigDecimal minAmount) {}

public List<Transaction> findTransactions(
        TransactionFilter filter) {
    // ... фильтрация через filter.dateRange().from() и т.д.
}
```

В Java 16+ для неизменяемого Parameter Object удобен `record`: компактный синтаксис, готовые `equals`/`hashCode`, а в compact-конструкторе (как у `DateRange`) — место для валидации инвариантов. Добавив новое поле в `TransactionFilter`, сигнатуру `findTransactions` менять не нужно.

## Q7. Что такое Replace Magic Numbers with Constants?

Числовые и строковые литералы с неочевидным смыслом заменяются именованными константами. «Магическое» число `3` ничего не говорит читателю, а `MAX_RETRY_COUNT` — говорит. Плюс одно и то же значение перестаёт дублироваться: меняешь его в одном месте, а не ищешь все вхождения по коду (где легко пропустить одно и получить рассинхрон).

**До рефакторинга:**

```java
public void processWithRetry(Runnable task) {
    for (int i = 0; i < 3; i++) {
        try {
            task.run();
            return;
        } catch (Exception e) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
    if (role.equals("admin")) {
        notifyAdmin();
    }
}
```

**После рефакторинга:**

```java
private static final int MAX_RETRY_COUNT = 3;
private static final long RETRY_DELAY_MS = 3_000L;
private static final String ROLE_ADMIN = "admin";

public void processWithRetry(Runnable task) {
    for (int i = 0; i < MAX_RETRY_COUNT; i++) {
        try {
            task.run();
            return;
        } catch (Exception e) {
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
    if (ROLE_ADMIN.equals(role)) {
        notifyAdmin();
    }
}
```

Константы размещают в классе-владельце или в отдельном классе констант. Для замкнутого набора строковых значений (роли, статусы) вместо строковых констант лучше `enum` — компилятор проверит полноту `switch` и не даст передать опечатку.

## Q8. Что такое Replace Type Code with State/Strategy?

Вместо поля «тип» (число или enum) и разбросанных по коду ветвлений по нему вводятся **отдельные классы состояний или стратегий**; объект просто делегирует поведение текущему состоянию/стратегии. Type code опасен тем, что ветвления по нему расползаются: каждый новый тип требует найти и поправить все `switch`. Приём приводит код к [паттернам State и Strategy](../design-patterns/design-patterns-interview.md).

**Чем State отличается от Strategy:** State применяют, когда объект меняет поведение в зависимости от своего внутреннего состояния (и сам может переключать состояния); Strategy — когда алгоритм выбирается извне и не зависит от состояния объекта.

**До рефакторинга:**

```java
public class Employee {
    private int type; // 0 = ENGINEER, 1 = MANAGER, 2 = SALESMAN

    public BigDecimal calculatePay(int hoursWorked) {
        switch (type) {
            case 0: return hourlyRate.multiply(
                        BigDecimal.valueOf(hoursWorked));
            case 1: return salary;
            case 2: return salary.add(commission);
            default: throw new IllegalStateException();
        }
    }
}
```

**После рефакторинга:**

```java
public interface EmployeeType {
    BigDecimal calculatePay(PayContext ctx);
}

public class Engineer implements EmployeeType {
    public BigDecimal calculatePay(PayContext ctx) {
        return ctx.hourlyRate()
            .multiply(BigDecimal.valueOf(ctx.hoursWorked()));
    }
}

public class Manager implements EmployeeType {
    public BigDecimal calculatePay(PayContext ctx) {
        return ctx.salary();
    }
}

public class Salesman implements EmployeeType {
    public BigDecimal calculatePay(PayContext ctx) {
        return ctx.salary().add(ctx.commission());
    }
}

public class Employee {
    private final EmployeeType type;

    public BigDecimal calculatePay(int hoursWorked) {
        return type.calculatePay(
            new PayContext(hourlyRate, salary, commission, hoursWorked)
        );
    }
}
```

Добавление нового типа сотрудника = новый класс, без правок в `switch`.

## Q9. Что такое Extract Class / Extract Interface?

**Extract Class** — выделение части ответственности большого класса в новый класс; исходный класс держит ссылку на него и делегирует. Это лечение запаха God Class: когда класс отвечает за расчёты, аудит, уведомления сразу, его сложно понять и тестировать, а изменение в одной зоне рискует задеть другую.

**Extract Interface** — выделение контракта в интерфейс, чтобы клиенты зависели от абстракции, а не от конкретной реализации. Это открывает подмену реализаций (в том числе моков в тестах) без правки клиентского кода.

**Пример Extract Class:**

```java
// До: God Class с расчётами и аудитом
public class OrderService {
    public BigDecimal calculateDiscount(Order order) { /* ... */ }
    public BigDecimal calculateTax(Order order) { /* ... */ }
    public void logAudit(Order order, String action) { /* ... */ }
    public void sendAuditReport(LocalDate date) { /* ... */ }
}

// После: ответственности разделены
public class DiscountCalculator {
    public BigDecimal calculate(Order order) { /* ... */ }
}

public class TaxCalculator {
    public BigDecimal calculate(Order order) { /* ... */ }
}

public class OrderAuditLogger {
    public void log(Order order, String action) { /* ... */ }
    public void sendReport(LocalDate date) { /* ... */ }
}

public class OrderService {
    private final DiscountCalculator discountCalc;
    private final TaxCalculator taxCalc;
    private final OrderAuditLogger auditLogger;

    public void processOrder(Order order) {
        BigDecimal discount = discountCalc.calculate(order);
        BigDecimal tax = taxCalc.calculate(order);
        auditLogger.log(order, "processed");
    }
}
```

**Пример Extract Interface:**

```java
public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    List<User> findByRole(String role);
}

// В тестах подставляем in-memory реализацию
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> store = new HashMap<>();

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
    // ...
}
```

**Итог:** Extract Class уменьшает размер класса и разводит ответственности; Extract Interface улучшает тестируемость (мок по интерфейсу вместо тяжёлой реализации) и позволяет подставлять разные реализации. В Java интерфейсы традиционно используют для границ слоёв (сервис, репозиторий) — это видно в [вопросах по unit-тестированию](../testing/unit-testing-interview.md).

## Q10. Что такое Inline Method / Inline Variable?

Это обратные приёмы к Extract: вместо вынесения — встраивание.

- **Inline Method** — тело метода вставляется в место вызова, сам метод удаляется. Применяют, когда метод не яснее своего содержимого: его имя ничего не добавляет, а лишний уровень косвенности только мешает.
- **Inline Variable** — переменная заменяется выражением, которое в неё записывали. Уместно, когда имя переменной не яснее самого выражения.

Парадокс, но удаление абстракции — тоже улучшение: лишний слой косвенности так же вредит читаемости, как и его отсутствие.

**До рефакторинга:**

```java
private int getRating() {
    return moreThanFiveDeliveries() ? 2 : 1;
}

private boolean moreThanFiveDeliveries() {
    return numberOfDeliveries > 5;
}

// Вызов:
int rating = getRating();
if (rating > 1) {
    applyBonus();
}
```

**После Inline Method + Inline Variable:**

```java
// moreThanFiveDeliveries() не добавляет ясности — инлайним
if (numberOfDeliveries > 5) {
    applyBonus();
}
```

**Подводный камень:** перед инлайном убедиться, что метод вызывается только там, где вы ожидаете (иначе подстановка размножит код), что тесты покрывают поведение и что читаемость не пострадает. IDE (`IntelliJ Refactor` -> `Inline`) сама находит все вызовы, подставляет тело и удаляет метод.

## Q11. Что такое Replace Inheritance with Delegation (и наоборот)?

**Replace Inheritance with Delegation** — вместо наследования класс хранит экземпляр бывшего «родителя» в поле и делегирует ему нужные вызовы. **Replace Delegation with Inheritance** — обратный приём: если делегат используется почти как единственная реализация и приходится прокидывать почти все его методы, проще унаследоваться.

**Зачем уходить от наследования.** Наследование тащит в потомок весь публичный API предка, даже ненужный и опасный. Классический пример — `Stack extends Vector`: стек наследует `add(index, e)`, и в LIFO-структуру внезапно можно вставить элемент в середину. Делегирование отдаёт наружу только те методы, которые вы выбрали, — инкапсуляция сохраняется.

**До рефакторинга (проблема с наследованием):**

```java
// Stack наследует Vector — тянет все методы: get(i), add(i, e)
// Нарушает инкапсуляцию стека — можно вставить в середину!
public class Stack<E> extends Vector<E> {
    public E push(E item) {
        addElement(item);
        return item;
    }
    public E pop() {
        return remove(size() - 1);
    }
}

// Проблема:
Stack<String> stack = new Stack<>();
stack.push("a");
stack.add(0, "oops"); // доступен метод Vector — сломан LIFO
```

**После рефакторинга (делегирование):**

```java
public class Stack<E> {
    private final List<E> delegate = new ArrayList<>();

    public E push(E item) {
        delegate.add(item);
        return item;
    }

    public E pop() {
        if (delegate.isEmpty()) {
            throw new EmptyStackException();
        }
        return delegate.remove(delegate.size() - 1);
    }

    public E peek() {
        if (delegate.isEmpty()) {
            throw new EmptyStackException();
        }
        return delegate.get(delegate.size() - 1);
    }

    public int size() {
        return delegate.size();
    }
}
```

**Эмпирическое правило:** наследование оправдано только при отношении «является» (is-a), когда потомок действительно подставим вместо предка (Liskov Substitution Principle). Если связь скорее «использует» или «состоит из» — выбирайте делегирование. «Композиция вместо наследования» — про этот же выбор.

## Q12. Что такое Replace Exception with Test?

Управление потоком через `try/catch` заменяется явной проверкой условия до операции. Исключения дороги (формирование stack trace) и оставляются для действительно исключительных ситуаций, а не для ожидаемых сценариев вроде «не найдено».

Проблема исходного кода: `EntityNotFoundException` для отсутствующего пользователя — это норма, а не сбой, поэтому ловить её через `catch` дорого и нечитаемо. Дешевле и яснее проверить наличие заранее.

**До рефакторинга:**

```java
public User findUser(Long id) {
    try {
        return repository.findById(id);
    } catch (EntityNotFoundException e) {
        return null;
    }
}
```

**После рефакторинга:**

```java
// Вариант 1: Optional
public Optional<User> findUser(Long id) {
    return repository.findOptionalById(id);
}

// Вариант 2: явная проверка
public User findUser(Long id) {
    if (!repository.existsById(id)) {
        return null;
    }
    return repository.findById(id);
}
```

Из двух вариантов `Optional` обычно предпочтительнее явной проверки `existsById` + `findById`: он не делает двух обращений к хранилищу и в самой сигнатуре честно говорит вызывающему «значение может отсутствовать», заставляя обработать оба случая. Явная проверка `if (!existsById)` проще, но содержит гонку — между проверкой и чтением запись может исчезнуть.

## Q13. Что такое Introduce Null Object?

Вместо `null` и проверок на `null` подставляется объект с «пустым», но валидным поведением (например, логгер, который ничего не пишет). Вызывающий код больше не отличает «есть зависимость» от «нет зависимости» — он всегда вызывает метод, и в «пустом» случае просто ничего не происходит.

**Что это даёт:** исчезают разбросанные по коду `if (x != null)` и связанный с ними риск `NullPointerException`. Приём уместен там, где «ничего не делать» — корректный исход: логирование, уведомления, метрики. Для бизнес-логики, где отсутствие значения важно, лучше `Optional` (см. Q12), а не молчаливый Null Object.

**До рефакторинга:**

```java
public class OrderService {
    private final Logger logger; // может быть null

    public void process(Order order) {
        if (logger != null) {
            logger.info("Processing order: " + order.getId());
        }
        // ... бизнес-логика
        if (logger != null) {
            logger.info("Order processed: " + order.getId());
        }
    }
}
```

**После рефакторинга:**

```java
public interface Logger {
    void info(String message);
    void error(String message, Throwable cause);
}

public class ConsoleLogger implements Logger {
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }
    public void error(String message, Throwable cause) {
        System.err.println("[ERROR] " + message);
        cause.printStackTrace();
    }
}

// Null Object — пустая реализация
public class NoOpLogger implements Logger {
    public void info(String message) { /* ничего */ }
    public void error(String message, Throwable cause) { /* ничего */ }
}

public class OrderService {
    private final Logger logger; // всегда не null

    public OrderService(Logger logger) {
        this.logger = Objects.requireNonNull(logger);
    }

    public void process(Order order) {
        logger.info("Processing order: " + order.getId());
        // ... бизнес-логика — никаких if (logger != null)
        logger.info("Order processed: " + order.getId());
    }
}
```

Тот же принцип для коллекций: возвращать `Collections.emptyList()` вместо `null`, чтобы вызывающий код мог сразу итерироваться без проверки `if (list != null && !list.isEmpty())`.

## Q14. Рефакторинг и тесты — как связаны?

Тесты — это страховочная сетка рефакторинга: именно они доказывают, что поведение не изменилось. Без них любой шаг — это правка вслепую. Поэтому порядок такой: сначала пишут или дополняют [тесты](../testing/unit-testing-interview.md) на критичное поведение (особенно в legacy, где их обычно нет), убеждаются, что они зелёные, затем рефакторят небольшими шагами и после каждого шага прогоняют тесты. Малый шаг важен: если тест упал, понятно, какое именно изменение виновато, и откатить нужно немного.

Порядок работы по шагам:

1. Написать или дополнить тесты.
2. Убедиться, что тесты зелёные.
3. Сделать малый шаг рефакторинга.
4. Проверить, зелёные ли тесты:
   - тесты зелёные и **ещё есть шаги** → возвращаемся к шагу 3;
   - тесты зелёные и **шагов больше нет** → коммит;
   - тесты **упали** → откатываем шаг и возвращаемся к шагу 2 (убедиться, что тесты снова зелёные).

**Пример characterization test для legacy-кода:**

```java
@Test
void characterize_discountCalculation() {
    // Фиксируем ТЕКУЩЕЕ поведение, даже если оно кажется странным
    Order order = new Order();
    order.setTotal(new BigDecimal("1000"));
    order.setCustomerType("VIP");
    order.setItemCount(15);

    BigDecimal result = legacyService.calculateDiscount(order);

    // Этот тест защищает от регрессии при рефакторинге
    assertThat(result).isEqualByComparingTo("150.00");
}
```

**Что такое characterization test.** Это тест, который фиксирует *текущее* поведение кода, даже если оно кажется странным или ошибочным. Его задача — не проверить «как должно быть», а зафиксировать «как есть сейчас», чтобы рефакторинг ничего случайно не сломал. Если найдётся настоящий баг — его правят отдельно, осознанно меняя такой тест.

Крупный рефакторинг разбивают на малые коммиты/PR, каждый — с зелёными тестами. В legacy без тестов characterization tests добавляют именно в тех местах, которые собираетесь менять (покрывать весь legacy сразу не нужно).

## Q15. Что такое «запахи кода» (code smells)?

«Запах кода» (code smell) — это поверхностный признак, который намекает на более глубокую проблему в структуре кода. Важно: запах не баг и не ошибка — код работает; это сигнал «здесь стоит присмотреться», а не «здесь обязательно надо чинить». Каждому типичному запаху соответствует свой приём рефакторинга — классификация по Фаулеру как раз связывает «симптом» и «лечение». Запахи тесно связаны с [техническим долгом](technical-debt-interview.md): накапливаясь, они делают код всё дороже в изменении.

| Запах кода | Приём рефакторинга |
|---|---|
| Длинный метод (> 20 строк) | `Extract Method` |
| Большой класс (God Class) | `Extract Class`, `Extract Interface` |
| Длинный список параметров | `Introduce Parameter Object` |
| Дублирование кода | `Extract Method`, вынос в общий модуль |
| Цепочки `if/else` по типу | `Replace Conditional with Polymorphism` |
| Feature Envy (метод завидует чужим данным) | `Move Method` |
| Magic Numbers | `Replace Magic Numbers with Constants` |
| Отказ от наследства | `Replace Inheritance with Delegation` |

**Пример Feature Envy:**

```java
// До: метод в ReportService завидует данным Order
public class ReportService {
    public String formatOrderSummary(Order order) {
        return order.getCustomer().getName() + ": "
            + order.getItems().size() + " items, "
            + order.getTotal().subtract(order.getDiscount());
    }
}

// После: логика перенесена в Order (Move Method)
public class Order {
    public String formatSummary() {
        return customer.getName() + ": "
            + items.size() + " items, "
            + total.subtract(discount);
    }
}
```

Часть запахов ловится автоматически: `SonarQube` и `SpotBugs` находят дублирование и высокую сложность. Но «логические» запахи (Feature Envy, неудачные имена) инструменты не видят — их ловят на [ревью](code-review-interview.md), сверяясь со списком из таблицы выше.

## Q16. Рефакторинг больших классов (God Class)?

God Class — класс, который взял на себя слишком много ответственностей (десятки полей и методов про разные вещи). Его понимают с трудом, тестируют ещё хуже, а правка в одной зоне рискует задеть другую. Лечат разбиением: `Extract Class` по зонам ответственности (валидация, расчёт, уведомления — каждая в свой класс); `Extract Interface` для контракта; вынос части логики в отдельные сервисы/хелперы. Исходный класс остаётся как тонкий фасад, делегирующий выделенным классам.

Схема разбиения по порядку:

- Исходный God Class `OrderManager` (15 полей, 40 методов) разбивается приёмом `Extract Class` на классы по зонам ответственности:
  - `OrderValidator` — валидация;
  - `PricingEngine` — расчёт цен;
  - `OrderNotifier` — уведомления;
  - `OrderPersistence` — сохранение.
- Сам `OrderManager` остаётся как фасад и делегирует в `OrderValidator`, `PricingEngine`, `OrderNotifier`, `OrderPersistence`.

**Пример поэтапного разбиения:**

```java
// Шаг 1: выделяем самый независимый кусок
public class DiscountCalculator {
    public BigDecimal calculate(Order order) {
        // логика из OrderManager
    }
}

// Шаг 2: исходный класс делегирует
public class OrderManager {
    private final DiscountCalculator discountCalc =
        new DiscountCalculator();

    @Deprecated(since = "2.0", forRemoval = true)
    public BigDecimal calculateDiscount(Order order) {
        return discountCalc.calculate(order);
    }
}

// Шаг 3: повторить для следующей зоны ответственности
```

**Подводные камни:** не сломать существующие вызовы — оставлять фасад или `@Deprecated`-методы с делегированием, пока клиенты не мигрируют. Не делать «большой взрыв»: один `Extract Class` за PR. Начинать с самой независимой зоны ответственности — её проще выделить, не разорвав связи внутри класса.

## Q17. Рефакторинг длинных методов?

Основной приём — `Extract Method`: каждый логический блок (валидация, расчёт, форматирование), а также циклы и сложные условия выносят в отдельный метод с говорящим именем. Цель — превратить длинный метод в короткий метод-оркестратор, который читается как последовательность шагов высокого уровня, а детали каждого шага спрятаны на уровень ниже. Так читатель сначала видит «что делается», и лишь при необходимости спускается к «как».

**До рефакторинга:**

```java
public Invoice generateInvoice(Order order) {
    // 80 строк: валидация, расчёт, форматирование, отправка
    if (order == null || order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Invalid order");
    }
    BigDecimal subtotal = BigDecimal.ZERO;
    for (OrderItem item : order.getItems()) {
        subtotal = subtotal.add(
            item.getPrice().multiply(
                BigDecimal.valueOf(item.getQuantity())
            )
        );
    }
    BigDecimal tax = subtotal.multiply(TAX_RATE);
    BigDecimal total = subtotal.add(tax);
    // ... ещё 50 строк форматирования и отправки
}
```

**После рефакторинга (метод-оркестратор):**

```java
public Invoice generateInvoice(Order order) {
    validateOrder(order);
    BigDecimal subtotal = calculateSubtotal(order);
    BigDecimal tax = calculateTax(subtotal);
    Invoice invoice = formatInvoice(order, subtotal, tax);
    sendToCustomer(invoice);
    return invoice;
}
```

Каждый новый метод — один шаг рефакторинга, после него прогон тестов. **Подводный камень:** не выносить в отдельный метод один тривиальный оператор без смысла — это плодит косвенность, не добавляя ясности. Извлекать стоит блок, которому можно дать осмысленное имя-действие.

## Q18. Рефакторинг дублирования кода?

Дублирование лечится выносом общей части в одно место: полностью идентичный код — в общий метод; почти идентичный (отличается значением) — параметризацией; отличается шагом алгоритма — шаблонным методом (Template Method) или передачей поведения (`Predicate`, `Function`). Опасность дублирования в том, что при изменении логики легко поправить одну копию и забыть про остальные — отсюда рассинхрон и баги.

**До рефакторинга (дублирование):**

```java
public List<User> findActiveAdmins() {
    List<User> result = new ArrayList<>();
    for (User user : allUsers) {
        if (user.isActive() && user.getRole().equals("ADMIN")) {
            result.add(user);
        }
    }
    return result;
}

public List<User> findActiveManagers() {
    List<User> result = new ArrayList<>();
    for (User user : allUsers) {
        if (user.isActive() && user.getRole().equals("MANAGER")) {
            result.add(user);
        }
    }
    return result;
}
```

**После рефакторинга (параметризация):**

```java
public List<User> findActiveByRole(String role) {
    return allUsers.stream()
        .filter(User::isActive)
        .filter(u -> u.getRole().equals(role))
        .toList();
}

// Или более гибко — через Predicate
public List<User> findUsers(Predicate<User> criteria) {
    return allUsers.stream()
        .filter(criteria)
        .toList();
}
```

**Правило трёх:** при первом дублировании можно оставить, при втором — задуматься, при третьем — вынести. Преждевременное обобщение по одному случаю часто создаёт неудачную абстракцию, которую потом дороже разбирать, чем дублирование.

**Подводный камень:** при выносе в утилитный класс проверять зависимости — общая утилита не должна тянуть за собой доменную логику, иначе вместо устранения связности вы создадите новый узел сцепления.

## Q19. Рефакторинг в legacy-коде без тестов?

Главная сложность legacy без тестов — замкнутый круг: чтобы безопасно менять код, нужны тесты, но чтобы написать тесты, код часто приходится менять (он не тестируем из-за жёстких зависимостей). Разрывают круг так: сначала минимальным, самым безопасным вмешательством делают код тестируемым (выделяют интерфейс для жёсткой зависимости — seam, «шов»), затем покрывают текущее поведение characterization-тестами, и только потом рефакторят небольшими шагами с частым прогоном тестов.

Последовательность работы с legacy без тестов:

1. Legacy-код без тестов — исходное состояние.
2. Characterization tests — фиксируем текущее поведение.
3. `Extract Interface` для зависимостей.
4. Подставить mock в тесте.
5. Рефакторинг малыми шагами.
6. Проверка тестами.

**Пример: обёртка зависимости для тестирования:**

```java
// Legacy: жёсткая зависимость, невозможно тестировать
public class LegacyReportService {
    public Report generate(String query) {
        Connection conn = DriverManager.getConnection(DB_URL);
        // 200 строк SQL и логики...
    }
}

// Шаг 1: Extract Interface для зависимости
public interface DataSource {
    List<Map<String, Object>> executeQuery(String sql);
}

// Шаг 2: обернуть legacy-зависимость
public class JdbcDataSource implements DataSource {
    public List<Map<String, Object>> executeQuery(String sql) {
        // ... JDBC-код
    }
}

// Шаг 3: теперь можно тестировать
public class ReportService {
    private final DataSource dataSource;

    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    // ... логика без прямого JDBC
}

// В тесте:
DataSource mock = Mockito.mock(DataSource.class);
var service = new ReportService(mock);
```

Ключевая идея примера: до выделения `DataSource` метод `generate` нельзя было протестировать без живой БД — `DriverManager.getConnection` зашит внутри. После Extract Interface зависимость подменяется моком, и логику можно проверять изолированно. Это «шов», в который вставляется тест. Для крупных систем тот же подход масштабируется в Strangler Fig — об этом в следующем вопросе.

## Q20. Что такое Strangler Fig при рефакторинге системы?

Strangler Fig (паттерн Фаулера) — стратегия постепенной замены старой системы новой без её остановки и без рискованного «большого взрыва». Название от дерева-душителя: оно оплетает старое дерево и со временем полностью замещает его. Так же и здесь: новый функционал пишется в новом коде, перед старой системой ставится фасад/прокси, через который вызовы по мере готовности переключаются со старого кода на новый, и когда всё перенесено — старый код удаляют. В любой момент система работоспособна, а риск размазан на множество мелких переключений вместо одного большого.

Три этапа замены по схеме Strangler Fig:

- **Этап 1 — фасад:** Клиент → Фасад / Proxy → Старый модуль (100% трафика).
- **Этап 2 — постепенный перевод:** Клиент → Фасад / Proxy, который делит трафик: 70% → Старый модуль, 30% → Новый модуль.
- **Этап 3 — завершение:** Клиент → Фасад / Proxy → Новый модуль (100% трафика); Старый модуль удалён.

**Пример переключения по feature flag:**

```java
@Service
public class OrderFacade {
    private final LegacyOrderService legacyService;
    private final NewOrderService newService;
    private final FeatureToggle featureToggle;

    public OrderResult processOrder(Order order) {
        if (featureToggle.isEnabled("new-order-processing")) {
            return newService.process(order);
        }
        return legacyService.process(order);
    }
}
```

**Сценарии применения:** распил монолита на микросервисы, смена технологии/фреймворка, крупный рефакторинг подсистемы — везде, где переписать всё разом слишком рискованно, а остановить продукт нельзя. Feature flag здесь не только переключатель, но и аварийный тормоз: при проблеме в новом коде трафик мгновенно возвращается на старый.

## Q21. Рефакторинг и производительность?

Рефакторинг — это про структуру и читаемость, а не про скорость; его явная цель не «ускорить», а «не замедлить». Производительность и рефакторинг лучше не смешивать: сначала привести код в чистую форму, затем — если замеры покажут проблему — оптимизировать. Чистый, понятный код потом и оптимизировать проще, потому что узкие места в нём видны. Поэтому проверка такая: после рефакторинга при необходимости снимают замеры (профилирование, бенчмарки) и сравнивают с состоянием «до».

**Пример: бенчмарк до/после рефакторинга (JMH):**

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class DiscountBenchmark {

    private Order testOrder;

    @Setup
    public void setup() {
        testOrder = createLargeOrder(1000);
    }

    @Benchmark
    public BigDecimal legacyCalculation() {
        return legacyService.calculateDiscount(testOrder);
    }

    @Benchmark
    public BigDecimal refactoredCalculation() {
        return new DiscountCalculator().calculate(testOrder);
    }
}
```

**Компромисс.** Эффект двоякий: иногда рефакторинг улучшает производительность (устранение дублирования, более ясные границы кэширования), иногда добавляет накладные расходы (лишние вызовы, слои абстракции). Поэтому критичные по производительности участки рефакторят с замерами до/после, а не на глаз. И главное эмпирическое правило: не делать микрооптимизаций «на будущее» — сначала измерить, потом оптимизировать только подтверждённое узкое место.

## Q22. Рефакторинг API и обратная совместимость?

Внутренний код рефакторить безопасно, потому что виден весь круг вызовов. Публичный API — другое дело: его вызывают внешние клиенты, которых вы не контролируете и не можете поправить одним коммитом, поэтому ломать его сразу нельзя. Решение — эволюция, а не замена: старый метод помечают `@Deprecated`, оставляют как тонкую обёртку, делегирующую в новую реализацию, и в Javadoc указывают, чем заменить и в какой версии метод исчезнет. Клиенты мигрируют в своём темпе, а старый и новый API какое-то время сосуществуют.

**Пример эволюции API:**

```java
public class UserService {

    /**
     * @deprecated Use {@link #findUser(UserQuery)} instead.
     * Will be removed in 3.0.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public User findByNameAndAge(String name, int age) {
        return findUser(new UserQuery(name, age, null));
    }

    // Новый метод с Parameter Object
    public User findUser(UserQuery query) {
        // ... реализация
    }
}

// Контракт-тест (Spring Cloud Contract / Pact)
@Test
void legacyMethod_delegatesToNew() {
    User result = service.findByNameAndAge("John", 30);
    User expected = service.findUser(
        new UserQuery("John", 30, null)
    );
    assertThat(result).isEqualTo(expected);
}
```

Контракт-тесты (`Pact`, `Spring Cloud Contract`) — страховка от поломки клиентов: они проверяют, что обёртка-делегат даёт тот же результат, что и новый метод (см. тест в примере). Подробнее — в [вопросах по интеграционному тестированию](../testing/integration-testing-interview.md). Внутри одного приложения, где видны все вызовы, breaking changes допустимы — но тоже аккуратно: с версионированием и миграцией вызывающего кода в том же изменении.

## Q23. Какие инструменты помогают при рефакторинге в Java?

| Категория | Инструменты | Назначение |
|---|---|---|
| IDE | `IntelliJ IDEA`, `Eclipse` | `Rename`, `Extract Method/Class/Interface`, `Move`, `Inline` |
| Статический анализ | `SonarQube`, `SpotBugs`, `Checkstyle` | Дублирование, сложность, «запахи» |
| Тестирование | `JUnit 5`, `Mockito`, `AssertJ` | Страховка при рефакторинге |
| Покрытие | `JaCoCo` | Визуализация покрытия тестами |
| Бенчмарки | `JMH` | Замеры производительности до/после |

**Пример: Extract Method в IntelliJ IDEA:**

```java
// 1. Выделить фрагмент кода
// 2. Ctrl+Alt+M (или Refactor → Extract Method)
// 3. Задать имя метода
// 4. IDE автоматически определит параметры и возвращаемый тип
// 5. Запустить тесты — убедиться, что всё зелёное
```

**Почему IDE, а не руками.** Автоматический рефакторинг в IDE безопаснее ручного: IDE учитывает область видимости, находит все вызовы, корректно обновляет ссылки и не делает опечаток — то, на чём легко ошибиться при ручном переносе. Перед крупным рефакторингом полезно прогнать `SonarQube` по проекту и закрыть критичные замечания. При ручных правках — один приём за коммит и [тесты](../testing/unit-testing-interview.md) после каждого шага.

## Q24. Рефакторинг многопоточного кода?

Многопоточный код опасен тем, что у него есть скрытый «контракт», который тесты обычно не видят: порядок операций, границы критических секций, отношения `happens-before` (видимость записей одного потока другому). Обычный рефакторинг легко нарушает этот контракт молча — баг проявится только под нагрузкой как гонка или deadlock. Поэтому правило: сохранять потоковую модель — не вводить новые блокировки без необходимости, не менять порядок операций и, главное, не выносить код за пределы критической секции.

Безопасный приём — `Extract Method` целиком *внутри* `synchronized`: критическая секция остаётся той же, меняются только имена. Опасный — извлечение, при котором часть кода выпадает из-под блокировки.

**Безопасный рефакторинг:**

```java
// Extract Method внутри synchronized — безопасно
public synchronized void transfer(Account from, Account to,
                                   BigDecimal amount) {
    validateBalance(from, amount);  // извлечённый метод
    executeTransfer(from, to, amount);  // извлечённый метод
}

private void validateBalance(Account from, BigDecimal amount) {
    if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException();
    }
}

private void executeTransfer(Account from, Account to,
                              BigDecimal amount) {
    from.debit(amount);
    to.credit(amount);
}
```

**Опасный рефакторинг (нарушение happens-before):**

```java
// ОПАСНО: вынос части кода за пределы synchronized
public void transfer(Account from, Account to, BigDecimal amount) {
    validateBalance(from, amount); // БЕЗ блокировки — гонка!
    synchronized (this) {
        executeTransfer(from, to, amount);
    }
}
```

**Рекомендации:** документировать инварианты и потоковую модель в комментариях (иначе следующий разработчик их не увидит и сломает); прогонять нагрузочные/конкурентные тесты до и после рефакторинга, потому что unit-тесты гонки обычно не ловят; при появлении гонок — немедленный откат, а не «доработка на месте».

## Q25. Когда рефакторинг не стоит делать?

Рефакторинг — инструмент с ценой (время, риск регрессии), и применять его стоит только когда выгода эту цену оправдывает. Не стоит рефакторить:

- **в середине hotfix под давлением** — главное сейчас погасить инцидент, а лишние правки увеличивают риск новой регрессии;
- **без [тестов](../testing/unit-testing-interview.md) в критичной области** — нечем поймать поломку, а цена ошибки высока;
- **«ради рефакторинга», без цели** — изменение ради изменения вносит риск и шум в историю, не давая отдачи.

**Дерево принятия решения:**

1. **Есть цель?** (читаемость, долг, подготовка к фиче)
   - **Нет** → не рефакторить.
   - **Да** → переходим к вопросу о тестах.
2. **Есть тесты?**
   - **Нет** → **можно быстро добавить?**
     - **Да** → добавить characterization tests → рефакторить малыми шагами.
     - **Нет** → отложить в техдолг.
   - **Да** → **Hotfix / deadline?**
     - **Да** → отложить в отдельный тикет.
     - **Нет** → рефакторить малыми шагами.

**Откладывают в техдолг, если:** нет времени на тесты и проверку; команда не согласовала объём и границы (иначе один разойдётся с другими в видении); правка тянет каскад изменений без явной выгоды. При hotfix — только минимальные правки по делу, а напрашивающийся рядом рефакторинг выносят в отдельный тикет, чтобы вернуться к нему осознанно. Решение «делать сейчас или отложить» — это управление [техническим долгом](technical-debt-interview.md): сознательно взятый долг с тикетом лучше стихийного.

## Q26. Что такое Replace Method with Method Object?

Это приём для случая, когда обычный `Extract Method` не работает: длинный метод напичкан локальными переменными, которые используются в разных его частях, и любая попытка вынести кусок упирается в передачу 5-7 параметров туда-обратно. Решение — превратить сам метод в отдельный класс (Method Object): его параметры и локальные переменные становятся полями этого класса, а тело — методом. Теперь переменные доступны всем приватным методам как поля, и `Extract Method` внутри проходит свободно, без длинных списков аргументов.

**До рефакторинга:**

```java
public BigDecimal calculatePrice(Order order, Customer customer,
                                  List<Promotion> promotions) {
    BigDecimal base = order.getSubtotal();
    BigDecimal loyaltyDiscount = BigDecimal.ZERO;
    BigDecimal promoDiscount = BigDecimal.ZERO;
    BigDecimal taxRate = resolveTaxRate(customer.getRegion());
    BigDecimal shippingCost = BigDecimal.ZERO;

    // 60 строк переплетённой логики с этими переменными
    if (customer.isVip()) {
        loyaltyDiscount = base.multiply(VIP_RATE);
    }
    for (Promotion p : promotions) {
        promoDiscount = promoDiscount.add(p.apply(base));
    }
    // ... и так далее
}
```

**После рефакторинга (Method Object):**

```java
public class PriceCalculator {
    // Бывшие параметры
    private final Order order;
    private final Customer customer;
    private final List<Promotion> promotions;

    // Бывшие локальные переменные — теперь поля
    private BigDecimal base;
    private BigDecimal loyaltyDiscount;
    private BigDecimal promoDiscount;
    private BigDecimal taxRate;
    private BigDecimal shippingCost;

    public PriceCalculator(Order order, Customer customer,
                           List<Promotion> promotions) {
        this.order = order;
        this.customer = customer;
        this.promotions = promotions;
    }

    public BigDecimal compute() {
        initializeBase();
        applyLoyaltyDiscount();
        applyPromotions();
        calculateShipping();
        return calculateFinalPrice();
    }

    private void initializeBase() {
        this.base = order.getSubtotal();
        this.taxRate = resolveTaxRate(customer.getRegion());
    }

    private void applyLoyaltyDiscount() {
        loyaltyDiscount = customer.isVip()
            ? base.multiply(VIP_RATE)
            : BigDecimal.ZERO;
    }

    private void applyPromotions() {
        promoDiscount = promotions.stream()
            .map(p -> p.apply(base))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ...
}

// Вызов:
BigDecimal price = new PriceCalculator(order, customer, promos)
    .compute();
```

**Сценарий применения:** парсеры, сложные расчёты, валидаторы — всё, где много промежуточных переменных и переплетённых шагов. Бонус: отдельные шаги (`applyPromotions`, `applyLoyaltyDiscount`) становятся приватными методами, которые можно тестировать или отлаживать по отдельности. Один Method Object держит один расчёт — это в духе SRP.

## Q27. Рефакторинг и покрытие тестами: какой минимум?

Достаточного минимума «в процентах» нет — важна не цифра, а то, *что* покрыто. Перед рефакторингом критичной области нужны тесты на её текущее поведение (приёмочные или интеграционные, на вход/выход), и после рефакторинга те же тесты должны остаться зелёными. Гнаться за 100% покрытия не нужно: покрывать стоит ветки и граничные случаи, которые затрагивает именно этот рефакторинг. Высокий процент покрытия сам по себе ничего не гарантирует — строки могут «исполняться» без единого осмысленного assert.

**Пример: защитные тесты перед рефакторингом:**

```java
@Nested
class BeforeRefactoring {

    @Test
    void normalOrder_calculatesCorrectly() {
        Order order = createOrder(3, "100.00");
        assertThat(service.calculate(order))
            .isEqualByComparingTo("300.00");
    }

    @Test
    void emptyOrder_throwsException() {
        Order order = createOrder(0, "0");
        assertThatThrownBy(() -> service.calculate(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void vipDiscount_appliedCorrectly() {
        Order order = createVipOrder(5, "200.00");
        BigDecimal result = service.calculate(order);
        // VIP скидка 10%
        assertThat(result).isEqualByComparingTo("900.00");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    void variousQuantities_noExceptions(int qty) {
        Order order = createOrder(qty, "50.00");
        assertThatCode(() -> service.calculate(order))
            .doesNotThrowAnyException();
    }
}
```

Пример выше покрывает не одну «счастливую» ветку, а набор: норму, пустой заказ (исключение), VIP-скидку и параметризованный диапазон количеств — то есть именно те случаи, поведение которых рефакторинг не должен изменить. Подробнее о стратегиях покрытия — в [вопросах по стратегиям тестирования](../testing/test-strategies-interview.md). В legacy роль таких тестов играют characterization tests на вход/выход затронутого метода; написав их, рефакторят малыми шагами.

## Q28. Как рефакторить без «большого взрыва» в монолите?

«Большой взрыв» (переписать модуль разом и влить одним PR) опасен: долгая ветка расходится с основной, ревью неподъёмное, а откатить в случае проблем нечего — всё или ничего. Альтернатива — двигаться так, чтобы система оставалась рабочей на каждом шаге:

1. **Strangler Fig** — новый код живёт рядом со старым, трафик переключается постепенно (см. Q20/Q35).
2. **Изоляция модуля фасадом или внутренним API** — за стабильной границей внутренности можно менять, не трогая клиентов.
3. **Рефакторинг внутри модуля малыми шагами**, каждый — с зелёными тестами.

**Пример поэтапного плана:**

```java
// Этап 1: фасад перед модулем
public class PaymentFacade {
    private final LegacyPaymentService legacy;

    public PaymentResult process(PaymentRequest request) {
        return legacy.processPayment(
            request.getAmount(),
            request.getCurrency(),
            request.getCardToken()
        );
    }
}

// Этап 2: новая реализация за фасадом
public class PaymentFacade {
    private final LegacyPaymentService legacy;
    private final NewPaymentService newService;
    private final FeatureToggle toggle;

    public PaymentResult process(PaymentRequest request) {
        if (toggle.isEnabled("new-payment")) {
            return newService.process(request);
        }
        return legacy.processPayment(
            request.getAmount(),
            request.getCurrency(),
            request.getCardToken()
        );
    }
}

// Этап 3: удалить legacy после полного переключения
public class PaymentFacade {
    private final NewPaymentService service;

    public PaymentResult process(PaymentRequest request) {
        return service.process(request);
    }
}
```

**Рекомендации:** каждый этап — отдельный PR и деплой (так его реально отревьюить и в случае чего откатить). Не планировать «переписать модуль за месяц» одним куском — разбить на короткие итерации, каждая с измеримой ценностью на выходе. План и границы фиксировать письменно (ADR, бэклог), чтобы команда не разошлась в понимании объёма.

## Q29. Рефакторинг и код-ревью: на что смотреть?

Главный вопрос ревью рефакторинг-PR — «уверен ли я, что поведение не изменилось?». От обычного ревью он отличается тем, что новой функциональности здесь быть не должно: любое изменение поведения в таком PR — повод для вопроса. Ревьюер проверяет три вещи:

- **Поведение прежнее** — тесты зелёные, контракты публичного API сохранены.
- **Рефакторинг сфокусирован** — нет посторонних правок «заодно», которые смешивают намерения и затрудняют ревью/откат.
- **Нет новых запахов** — устраняя одну проблему, автор не завёл другую.

Подробнее о процессе — в [вопросах по code review](code-review-interview.md).

**Чек-лист ревьюера для рефакторинг-PR:**

```markdown
- [ ] Тесты есть и зелёные
- [ ] Поведение не изменилось (те же входы/выходы)
- [ ] Публичный API сохранён (или @Deprecated + делегирование)
- [ ] Разбиение логичное и понятное
- [ ] Нет смешивания рефакторинга и фичи
- [ ] Нет введения новых запахов
- [ ] В описании PR явно указан объём рефакторинга
```

**Эмпирическое правило:** крупный рефакторинг — отдельный PR от фич; малый «по пути» допустим в том же PR, но с явным пояснением в описании. Ключевая ответственность автора — не заставлять ревьюера угадывать, что изменилось: объём и характер рефакторинга описываются в PR прямо.

## Q30. Какие метрики использовать до и после рефакторинга?

| Метрика | Инструмент | Что показывает |
|---|---|---|
| Цикломатическая сложность | `SonarQube`, `Checkstyle` | Количество путей в методе |
| Когнитивная сложность | `SonarQube` | Насколько трудно понять код |
| Дублирование | `SonarQube`, `CPD` | Процент дублированных строк |
| Размер класса/метода | `SonarQube` | Строки кода, количество методов |
| Покрытие тестами | `JaCoCo` | Процент покрытого кода |
| Связность (LCOM4) | `JDepend` | Степень связности внутри класса |
| Сцепление (afferent/efferent) | `JDepend` | Зависимости между пакетами |

**Пример: снятие метрик до/после в CI:**

```bash
# До рефакторинга — сохранить отчёт
./gradlew sonarqube -Dsonar.branch=before-refactoring

# После рефакторинга
./gradlew sonarqube -Dsonar.branch=after-refactoring

# Сравнить в SonarQube UI:
# - Cognitive Complexity: 45 → 12
# - Duplicated Lines: 18% → 3%
# - Test Coverage: 62% → 85%
```

**Зачем нужны метрики.** Они превращают «код стал лучше» (субъективно, не убеждает менеджмент) в «когнитивная сложность упала с 45 до 12, дублирование с 18% до 3%» — это уже аргумент, чтобы выбить время на рефакторинг и зафиксировать результат «до/после». **Подводный камень:** метрики — не самоцель. Низкая цикломатическая сложность при нечитаемом коде — обман; цель остаётся «понятный и изменяемый код», а числа лишь помогают её отслеживать. Метрики связаны с управлением [техническим долгом](technical-debt-interview.md) и мониторингом качества.

## Q31. (!) Что такое Decompose Conditional и когда применять?

**Decompose Conditional** — приём из каталога Фаулера: само условие и каждую из ветвей `if/else` выносят в отдельный метод с говорящим именем. Идея в том, что сложное условие — такой же кандидат на `Extract Method`, как и любой блок кода: вместо того чтобы разбирать `weight > 10 && city.equals("Moscow") && !express`, читаешь `isEligibleForFreeDelivery(order)`. Логика начинает читаться на уровне намерений, а детали проверок прячутся на уровень ниже.

**До рефакторинга:**

```java
// Сложное условие — нужно понять детали, чтобы понять смысл
public BigDecimal calculateDelivery(Order order) {
    if (order.getItems().stream()
             .mapToInt(Item::getWeight).sum() > 10
        && order.getDeliveryAddress().getCity().equals("Moscow")
        && !order.isExpressDelivery()) {
        return order.getTotal()
                    .multiply(new BigDecimal("0.05"));
    } else if (order.isExpressDelivery()
               && ChronoUnit.DAYS.between(
                   LocalDate.now(), order.getDeliveryDate()) < 2) {
        return new BigDecimal("500");
    } else {
        return new BigDecimal("200");
    }
}
```

**После рефакторинга:**

```java
public BigDecimal calculateDelivery(Order order) {
    if (isEligibleForFreeDelivery(order)) {
        return freeDeliveryDiscount(order);
    } else if (isUrgentExpress(order)) {
        return URGENT_EXPRESS_FEE;
    } else {
        return STANDARD_DELIVERY_FEE;
    }
}

private boolean isEligibleForFreeDelivery(Order order) {
    int totalWeight = order.getItems().stream()
        .mapToInt(Item::getWeight).sum();
    return totalWeight > HEAVY_THRESHOLD
        && isMoscowDelivery(order)
        && !order.isExpressDelivery();
}

private boolean isUrgentExpress(Order order) {
    long daysUntilDelivery = ChronoUnit.DAYS.between(
        LocalDate.now(), order.getDeliveryDate());
    return order.isExpressDelivery() && daysUntilDelivery < URGENT_DAYS;
}

private boolean isMoscowDelivery(Order order) {
    return "Moscow".equals(order.getDeliveryAddress().getCity());
}
```

**Когда применять:** условие занимает больше одной строки; объединяет несколько несвязанных проверок; смысл не очевиден по коду. Дополнительный выигрыш — тестируемость и переиспользование: метод-предикат `isEligibleForFreeDelivery()` можно покрыть отдельным unit-тестом и применить в другом месте, тогда как inline-условие не вытащить ни в тест, ни в повторный вызов.

## Q32. Что такое Separate Query from Modifier?

**Separate Query from Modifier** (в основе — принцип Command-Query Separation, CQS) — приём Фаулера: метод либо отвечает на вопрос (query — возвращает значение без побочных эффектов), либо выполняет действие (command — меняет состояние, ничего не возвращая), но не то и другое сразу. Смешение делает код непредсказуемым: невинно выглядящий вызов `getUser(id)` втихую меняет БД, и читатель не может доверять «геттерам». Разделив их, вы возвращаете коду свойство, на которое все полагаются: query можно вызывать сколько угодно раз без последствий.

**До рефакторинга:**

```java
// BAD: метод одновременно возвращает значение И изменяет состояние
public User getUserAndMarkVisited(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User " + id));
    user.setLastVisited(Instant.now());   // побочный эффект!
    userRepository.save(user);            // ещё побочный эффект!
    return user;
}

// Вызов выглядит невинно, но меняет БД:
User u = service.getUserAndMarkVisited(42L);
```

**После рефакторинга — разделение:**

```java
// Query: только читаем, без побочных эффектов
public User getUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User " + id));
}

// Command: только изменяем, без возврата значения
public void markUserVisited(Long id) {
    User user = getUser(id);
    user.setLastVisited(Instant.now());
    userRepository.save(user);
}

// Вызов — явно видно: сначала действие, потом чтение
service.markUserVisited(42L);
User u = service.getUser(42L);
```

**Что это даёт на практике:** query-методы безопасно вызывать повторно и кэшировать; command-методы самим фактом «ничего не возвращаю» сигнализируют, что у них есть побочный эффект. Тестировать query проще — достаточно проверить возвращаемое значение, не заглядывая в состояние хранилища. На уровне архитектуры тот же принцип масштабируется до CQRS — в [архитектурных паттернах](../design-patterns/design-patterns-interview.md) read- и write-модели разделяют физически.

**Граничные случаи:** некоторые методы нарушают CQS осознанно — `Stack.pop()`/`Queue.poll()` и итераторы одновременно возвращают значение и меняют состояние, потому что иначе они неудобны; это принятый компромисс. В Java Streams промежуточные операции — это query (без побочных эффектов внутри pipeline), что и делает их предсказуемыми.

## Q33. Как рефакторить нарушения SOLID-принципов на практике?

SOLID-рефакторинг — это систематическое устранение нарушений принципов SOLID, накопившихся как [технический долг](technical-debt-interview.md). На практике каждый принцип нарушается узнаваемым образом, и под каждый есть свой приём рефакторинга — связка «симптом → лечение» ниже.

**SRP (класс делает слишком много): разбиение God Class через Extract Class.** Симптом — один класс отвечает за валидацию, расчёт, отправку, платёж. Лечение — выделить по классу на ответственность.

```java
// BAD: OrderService делает всё
@Service
public class OrderService {
    public Order create(OrderRequest req) { /* валидация, расчёт, сохранение */ }
    public void sendConfirmation(Order order) { /* email + sms */ }
    public byte[] generateInvoice(Order order) { /* PDF генерация */ }
    public void processPayment(Order order) { /* вызов платёжного шлюза */ }
}

// GOOD после рефакторинга (Extract Class)
@Service public class OrderCreationService { ... }    // создание + валидация
@Service public class OrderNotificationService { ... } // email + sms
@Service public class InvoiceService { ... }           // PDF
@Service public class PaymentService { ... }           // платёж
// OrderFacade координирует (опционально)
```

**OCP (новое требование заставляет править существующий код): Replace Conditional with Strategy.** Симптом — на каждый новый формат/тип дописывается ветка в один и тот же `if/else`, и старый рабочий код приходится трогать. Лечение — вынести варианты в классы-стратегии, тогда новый вариант = новый класс, а существующий код остаётся нетронутым.

```java
// BAD: каждое новое условие — правка существующего класса
public void export(Report report, String format) {
    if ("pdf".equals(format)) { /* ... */ }
    else if ("excel".equals(format)) { /* ... */ }
    else if ("csv".equals(format)) { /* ... */ }
}

// GOOD: новый формат = новый класс, существующий код не трогаем
public interface ReportExporter {
    String format();
    byte[] export(Report report);
}

@Component public class PdfExporter implements ReportExporter { ... }
@Component public class ExcelExporter implements ReportExporter { ... }

@Service
@RequiredArgsConstructor
public class ReportExportService {
    private final Map<String, ReportExporter> exporters;  // инжекция всех

    public byte[] export(Report report, String format) {
        return Optional.ofNullable(exporters.get(format))
            .orElseThrow(() -> new IllegalArgumentException("Unknown format: " + format))
            .export(report);
    }
}
```

**DIP (зависимость от конкретики): Introduce Dependency Injection.** Симптом — класс сам создаёт свои зависимости через `new MySqlOrderRepository()`, из-за чего его нельзя протестировать без реальной БД и SMTP. Лечение — зависеть от интерфейсов и принимать их через конструктор: в тестах подставляются моки, в проде — реальные реализации (их связывает Spring).

```java
// BAD: зависимость от конкретного класса — не тестируется
public class OrderService {
    private final MySqlOrderRepository repo = new MySqlOrderRepository();
    private final SmtpEmailSender emailSender = new SmtpEmailSender("smtp.mail.ru");
}

// GOOD: инверсия зависимостей — зависим от абстракций
public class OrderService {
    private final OrderRepository repo;        // интерфейс
    private final NotificationSender sender;   // интерфейс

    public OrderService(OrderRepository repo, NotificationSender sender) {
        this.repo = repo;
        this.sender = sender;
    }
}
// В тестах: new OrderService(mockRepo, mockSender)
// В prod: Spring подставляет MySqlOrderRepository и SmtpEmailSender
```

**Рекомендуемый порядок** (не случаен — каждый шаг готовит почву следующему):
1. **SRP** первым — разбиение God Class даёт самый заметный результат и обнажает остальные проблемы.
2. **DIP** сразу после — выделенные классы стоит сразу подключать через конструктор, а не `new`.
3. **OCP** — по мере появления `if/switch` по типу в растущем коде.
4. **LSP / ISP** — при рефакторинге иерархий наследования и «толстых» интерфейсов.

## Q34. Что такое рефакторинг «по DRY»: Consolidate Duplicate Conditional Fragments?

**Consolidate Duplicate Conditional Fragments** — приём Фаулера про DRY внутри условия: если один и тот же фрагмент кода присутствует во *всех* ветвях `if/else`, он не зависит от условия и его дублирование бессмысленно. Такой фрагмент выносят за пределы условия — до него (если он не зависит от ветви) или после (как в примере: отправка уведомления и сохранение нужны при любом исходе). В условии остаётся только то, что действительно различается по ветвям.

**До рефакторинга:**

```java
// Дублирование в обеих ветвях — отправка уведомления
public void processRefund(Order order, RefundReason reason) {
    if (reason == RefundReason.DEFECT) {
        order.setStatus(OrderStatus.REFUNDED);
        order.setRefundAmount(order.getTotal());      // полный возврат
        notificationService.sendRefundNotification(order);  // дубль!
        orderRepository.save(order);                        // дубль!
    } else if (reason == RefundReason.CANCEL) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setRefundAmount(calculatePartialRefund(order));
        notificationService.sendRefundNotification(order);  // дубль!
        orderRepository.save(order);                        // дубль!
    }
}
```

**После рефакторинга:**

```java
public void processRefund(Order order, RefundReason reason) {
    // Уникальная логика по ветвям
    if (reason == RefundReason.DEFECT) {
        order.setStatus(OrderStatus.REFUNDED);
        order.setRefundAmount(order.getTotal());
    } else if (reason == RefundReason.CANCEL) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setRefundAmount(calculatePartialRefund(order));
    }

    // Общий для всех ветвей — вынесен после условия
    notificationService.sendRefundNotification(order);
    orderRepository.save(order);
}
```

**Связанный приём — Guard Clause (Replace Nested Conditional with Guard Clauses).** Глубокая вложенность `if/else` плоха тем, что основная логика тонет на самом дне отступов, а читателю приходится держать в голове весь стек условий. Guard clause переворачивает подход: особые случаи (null, неактивен, невалиден) отсеиваются ранним `return` в начале, после чего основной путь идёт без вложенности — на «нулевом» уровне отступа.

```java
// BAD: глубокая вложенность
public BigDecimal getDiscount(User user) {
    if (user != null) {
        if (user.isActive()) {
            if (user.getLoyaltyLevel() > 0) {
                return calculateLoyaltyDiscount(user);
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    } else {
        return BigDecimal.ZERO;
    }
}

// GOOD: guard clauses — early return
public BigDecimal getDiscount(User user) {
    if (user == null) return BigDecimal.ZERO;
    if (!user.isActive()) return BigDecimal.ZERO;
    if (user.getLoyaltyLevel() <= 0) return BigDecimal.ZERO;
    return calculateLoyaltyDiscount(user);
}
```

**Эмпирическое правило:** особые случаи (null, inactive, invalid) — guard clauses в начале метода; основная логика — без вложенности после них. Читаемость растёт, а тестирование упрощается: каждый guard — это одна явная ветка под один тест.

## Q35. Как реализовать паттерн Strangler Fig в Spring Boot?

**Strangler Fig** (Фаулер) — постепенная замена legacy-системы без её остановки: новая функциональность пишется в новом сервисе/модуле, старая вытесняется порциями через маршрутизацию запросов. Название — от дерева-душителя, которое оплетает старое и со временем полностью его замещает. В Spring Boot роль «оплётки» играет роутер или фасад-контроллер: он стоит перед обеими реализациями и решает, куда направить каждый запрос, поэтому переключение происходит без изменений на стороне клиента.

**Стратегия в Spring Boot:**

1. **Proxy/Router перед legacy** — Spring Cloud Gateway или обычный `@RestController` принимает все запросы и маршрутизирует: новые эндпоинты → новый сервис, остальные → legacy.
2. **Feature toggle** — включить новую реализацию для части трафика.
3. **Data synchronisation** — пока оба сервиса живут, данные синхронизируются через event или dual write.
4. **Cut-over** — когда все эндпоинты перенесены, proxy убирается.

```java
// Gateway-роутер через Spring Cloud Gateway (application.yml)
// spring:
//   cloud:
//     gateway:
//       routes:
//         - id: new-orders
//           uri: http://orders-new-service
//           predicates:
//             - Path=/api/v2/orders/**
//         - id: legacy-orders
//           uri: http://legacy-monolith
//           predicates:
//             - Path=/api/v1/orders/**

// Или программный вариант — @RestController-facade
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderStranglerController {

    private final NewOrderService newOrderService;
    private final LegacyOrderClient legacyOrderClient;
    private final FeatureToggleService featureToggle;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        if (featureToggle.isEnabled("new-order-service")) {
            return ResponseEntity.ok(newOrderService.findById(id));
        }
        return legacyOrderClient.getOrder(id);  // проксируем в legacy
    }
}
```

**Подводные камни:**
- **Согласованность данных** — пока обе реализации работают с одними данными, нужна синхронизация: dual write или Change Data Capture (Debezium), иначе старый и новый код разойдутся в состоянии.
- **Прокси-слой временный** — после полной миграции его убирают; забытый «навсегда» фасад сам становится новым legacy.
- **Паритет поведения** — обязательно тестировать, что новая и старая реализации дают одинаковый результат на одних входах, иначе постепенное переключение незаметно изменит поведение для части пользователей.

## Q36. Какими приёмами заменить сложные if/else-цепочки?

Сложные условные конструкции (`if/else if/else`, `switch`) трудно читать, тяжело расширять (каждый новый случай — правка существующего кода, нарушение OCP) и легко ломаются. Есть несколько приёмов их устранения — выбор зависит от того, *что* стоит за ветвями.

**Приёмы устранения:**

**1. Replace Conditional with Polymorphism** — когда ветви различаются *поведением* по типу: каждая ветвь становится отдельным классом-стратегией, выбор делает реестр (`Map`), который заполняет Spring:
```java
// ДО: длинный switch
public double calculateShipping(Order order) {
    return switch (order.getDeliveryType()) {
        case EXPRESS  -> order.getTotal() * 0.1 + 5.0;
        case STANDARD -> order.getTotal() * 0.05;
        case FREE     -> 0.0;
        default       -> throw new IllegalArgumentException();
    };
}

// ПОСЛЕ: Strategy/полиморфизм
public interface ShippingStrategy {
    double calculate(Order order);
}

@Component("EXPRESS")
public class ExpressShipping implements ShippingStrategy {
    public double calculate(Order order) { return order.getTotal() * 0.1 + 5.0; }
}

@Component("STANDARD")
public class StandardShipping implements ShippingStrategy {
    public double calculate(Order order) { return order.getTotal() * 0.05; }
}

// Реестр стратегий из Spring context
@Service
@RequiredArgsConstructor
public class ShippingService {
    private final Map<String, ShippingStrategy> strategies; // Spring инжектирует

    public double calculate(Order order) {
        return strategies.get(order.getDeliveryType().name()).calculate(order);
    }
}
```

**2. Introduce Null Object** — когда ветви проверяют `x == null`: пустой объект убирает сами проверки (см. Q13).

**3. Replace Conditional with Command** — когда ветви это *действия*: их складывают в `Map<Key, Runnable/Function>` и вызывают по ключу, без `if`.

**4. Decompose Conditional** (Фаулер) — когда условие просто *сложное для чтения*: само условие и ветви выносятся в методы с говорящими именами (см. Q31), структура `if/else` остаётся:
```java
// ДО:
if (date.before(SUMMER_START) || date.after(SUMMER_END)) {
    charge = quantity * winterRate + winterServiceCharge;
} else {
    charge = quantity * summerRate;
}

// ПОСЛЕ:
if (isWinter(date)) {
    charge = winterCharge(quantity);
} else {
    charge = summerCharge(quantity);
}
```

## Q37. Что такое Replace Primitive with Object (паттерн Value Object)?

Это приём против запаха Primitive Obsession (примитивная одержимость) — когда самостоятельная бизнес-концепция (email, деньги, телефон) представлена голым примитивом `String`/`int`/`BigDecimal`. Беда в том, что примитив ничего не знает о своих правилах: валидация и форматирование email разбросаны по всем местам, где он используется, и легко создать заведомо невалидное значение. Решение — завернуть примитив в небольшой неизменяемый тип (Value Object), который инкапсулирует и значение, и правила обращения с ним.

**Решение — Value Object:**

```java
// ДО: Email — просто String, валидация везде
public class User {
    private String email;  // primitive obsession
    public void setEmail(String email) {
        if (!email.contains("@")) throw new IllegalArgumentException();
        this.email = email;
    }
}

// ПОСЛЕ: Email — Value Object
public final class Email {
    private final String value;

    public Email(String value) {
        if (value == null || !value.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new InvalidEmailException(value);
        }
        this.value = value.toLowerCase().trim();
    }

    public String getValue() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        return value.equals(((Email) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}

// User использует Email
public class User {
    private Email email;  // богатый тип
    public User(Email email) { this.email = email; }
}

// JPA/Hibernate — Embeddable
@Embeddable
public class Money {
    private BigDecimal amount;
    private Currency currency;
    // equals/hashCode/toString + бизнес-методы
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) throw new CurrencyMismatchException();
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

**Плюсы Value Object:**

- **Валидация в одном месте** — в конструкторе; раз `Email` создан, он гарантированно валиден, и проверять его дальше не нужно.
- **Невозможно создать невалидный объект** — нет «полусырого» состояния, в которое можно записать мусор сеттером.
- **Бизнес-методы рядом с данными** — `Money.add` сам проверяет совпадение валют, а не оставляет это вызывающему коду.
- **Равенство по значению** — два `Email` с одинаковой строкой равны (`equals`/`hashCode` по value), как и положено значению, а не ссылке.

## Q38. Как бороться с длинными списками параметров (Introduce Parameter Object)?

Запах Long Parameter List — метод с 4+ параметрами: его трудно вызывать (легко перепутать местами однотипные аргументы), тестировать (много комбинаций) и менять (каждое новое «поле» ломает сигнатуру и все вызовы). Introduce Parameter Object группирует связанные параметры в один объект, давая этой группе имя и стабильную сигнатуру.

**Признаки, что список параметров пора сворачивать:**
- несколько параметров всегда передаются вместе — это скрытое понятие, которому нужно имя;
- сигнатура меняется при добавлении каждого нового «поля»;
- `null` в аргументах — параметр на самом деле опциональный, и список перегружен.

**Рефакторинг:**

```java
// ДО: 7 параметров — nightmare
public Order createOrder(
    Long customerId,
    String deliveryCity,
    String deliveryStreet,
    BigDecimal discount,
    String promoCode,
    boolean isPriority,
    LocalDate requestedDate
) { ... }

// ШАГИ:
// 1. Выделяем Parameter Object (или несколько)
public record DeliveryInfo(
    String city,
    String street,
    LocalDate requestedDate,
    boolean isPriority
) {}

public record DiscountInfo(
    BigDecimal discount,
    String promoCode
) {}

// 2. Метод упрощается
public Order createOrder(Long customerId, DeliveryInfo delivery, DiscountInfo discountInfo) { ... }

// 3. Можно добавить методы в Parameter Object (бизнес-логику)
public record DiscountInfo(BigDecimal discount, String promoCode) {
    public boolean hasPromoCode() { return promoCode != null && !promoCode.isBlank(); }
    public BigDecimal effectiveDiscount() { return hasPromoCode() ? discount.add(PROMO_BONUS) : discount; }
}
```

**Builder** — альтернатива для объектов с большим числом опциональных полей:
```java
Order order = Order.builder()
    .customerId(customerId)
    .deliveryCity("Moscow")
    .isPriority(true)
    .build();
```

**Чем Parameter Object отличается от Value Object** (частый вопрос). Parameter Object — транзитная группировка аргументов для удобства передачи (не обязан быть immutable или иметь equals по значению); живёт ради одного вызова. Value Object (Q37) — постоянная доменная концепция со своими инвариантами (immutable, equals по значению), которая существует в домене сама по себе. Грубо: Parameter Object — про сигнатуру метода, Value Object — про модель домена.

## Q39. Как безопасно рефакторить схему БД (Expand-Contract паттерн)?

Прямое изменение схемы при работающем приложении опасно: переименование или удаление колонки в том же деплое, что и код, ломает совместимость — старые экземпляры приложения ещё ждут старую колонку, новые уже пишут в новую. Expand-Contract (он же Parallel Change) разводит изменение схемы и изменение кода во времени, чтобы в каждый момент работала и старая, и новая версия приложения. Делается это в три фазы.

**Фаза 1 — Expand (расширение):**
```sql
-- Добавляем новую колонку (nullable, backward-compatible)
ALTER TABLE users ADD COLUMN full_name VARCHAR(255);

-- Flyway-миграция V2__add_full_name.sql
-- Приложение обновлено: пишет в обе колонки (old + new)
-- UPDATE users SET full_name = first_name || ' ' || last_name;
```

**Фаза 2 — Migrate (миграция данных):**
```sql
-- Заполняем новую колонку для существующих строк
UPDATE users SET full_name = CONCAT(first_name, ' ', last_name)
WHERE full_name IS NULL;

-- Делаем NOT NULL после заполнения
ALTER TABLE users ALTER COLUMN full_name SET NOT NULL;
```

**Фаза 3 — Contract (сужение):**
```sql
-- После того как все версии приложения перешли на full_name
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;
```

Ключевая идея: между Expand и Contract есть переходный период, когда приложение пишет в обе колонки — это и есть «параллельное изменение», которое держит обе версии работоспособными.

**Правила (нарушение любого ломает zero-downtime):**
- Никогда не переименовывать и не удалять колонку в одном деплое с кодом — старые экземпляры приложения её ещё используют.
- Новая колонка должна быть nullable ИЛИ иметь DEFAULT, иначе старый код (не знающий о ней) не сможет вставлять строки.
- В Hibernate читать по новому полю: `@Column(name = "full_name", insertable = true, updatable = true)`.
- Порядок шагов строго: деплой нового кода → backfill данных → удаление старой колонки уже следующим деплоем.

## Q40. Что такое Feature Envy и как его лечить?

**Feature Envy** (зависть к функциям) — код-запах: метод класса A интересуется данными класса B больше, чем своими собственными — то есть дёргает чужие геттеры чаще, чем работает с полями родного класса. Это сигнал, что метод живёт не в том классе: логику тянет к данным, которые она использует. Лечение почти всегда — Move Method: перенести метод (или его «завидующую» часть) в класс-владелец данных, чтобы он работал со своими полями напрямую.

**Симптомы:**
- метод начинается с серии геттеров чужого объекта;
- метод принимает объект и разбирает его на части;
- логика «знает» о внутреннем устройстве другого класса слишком много.

```java
// ДО: OrderService слишком много знает о Customer
public class OrderService {
    public BigDecimal calculateDiscount(Order order) {
        Customer customer = order.getCustomer();
        // Feature Envy — всё про Customer:
        int loyaltyYears = customer.getLoyaltyYears();
        String tier = customer.getMembershipTier();
        boolean hasPremium = customer.hasPremiumSubscription();

        if (hasPremium && loyaltyYears > 3) return BigDecimal.valueOf(0.20);
        if ("GOLD".equals(tier)) return BigDecimal.valueOf(0.15);
        if (loyaltyYears > 1) return BigDecimal.valueOf(0.05);
        return BigDecimal.ZERO;
    }
}

// ПОСЛЕ: Move Method — логика переезжает в Customer
public class Customer {
    // ...
    public BigDecimal getDiscount() {
        if (hasPremiumSubscription() && getLoyaltyYears() > 3) return BigDecimal.valueOf(0.20);
        if ("GOLD".equals(getMembershipTier())) return BigDecimal.valueOf(0.15);
        if (getLoyaltyYears() > 1) return BigDecimal.valueOf(0.05);
        return BigDecimal.ZERO;
    }
}

public class OrderService {
    public BigDecimal calculateDiscount(Order order) {
        return order.getCustomer().getDiscount();  // делегируем владельцу данных
    }
}
```

**Граничные случаи (когда «зависть» — не запах):** DTO намеренно не содержат поведения, поэтому работа с их полями снаружи — норма; в паттерне Strategy алгоритм осознанно отделён от данных, на которых работает. То есть прежде чем переносить метод, стоит убедиться, что данные действительно «принадлежат» другому классу, а не специально вынесены наружу.

## Q41. Что такое Shotgun Surgery и как с ним бороться?

**Shotgun Surgery** (выстрел дробью) — код-запах: одно логическое изменение «разлетается» правками по множеству разных классов и файлов. Причина — одна ответственность размазана по системе, единой точки изменения нет. Это в некотором смысле противоположность Feature Envy: там одна функция тянет к чужим данным (слишком сконцентрирована не там), здесь одна тема расползлась по всему коду (слишком рассеяна). Лечение — собрать рассеянную логику в одно место (Move Field, Inline Class, общий базовый тип).

**Симптомы:**
- добавление нового поля → правка 10 классов;
- изменение формата даты → поиск всех мест форматирования;
- новый статус заказа → правки в контроллере, сервисе, репозитории, маппере, DTO и тестах.

**Пример:**
```java
// Изменение: добавить поле `updatedAt` в Order
// Нужно поправить:
// OrderEntity.java (+поле)
// OrderDto.java (+поле)
// OrderMapper.java (+маппинг)
// OrderRepository.java (+колонка в SQL)
// OrderService.java (+установка значения)
// V5__add_updated_at.sql (+миграция)
// 3 теста...

// ← это Shotgun Surgery. Причина: логика разбросана, нет единой точки.
```

**Рефакторинг — Move Field / Inline Class:**
```java
// Объединить разрозненную логику в одном месте
// Аудит-поля → вынести в @MappedSuperclass или @Embeddable
@MappedSuperclass
public abstract class AuditableEntity {
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
// Теперь @EnableJpaAuditing + наследование — поля автоматически.

// Маппинг → MapStruct с автоматическим маппингом по имени
// DTO → record с Lombok/Java record
// Единая точка изменения
```

**Связь с принципами:** Shotgun Surgery — это нарушение SRP (ответственность не сосредоточена в одном месте) и DRY (одна и та же логика повторяется в разных файлах). Общее правило лечения: переместить и сконцентрировать разрозненную логику в одной точке, чтобы будущее изменение затрагивало один файл, а не десять.

## Q42. Когда Extract Method недостаточно и нужен Method Object?

Обычный `Extract Method` буксует, когда в длинном методе много локальных переменных, переплетённых между его частями: чтобы вынести кусок, пришлось бы передать 5-7 параметров и вернуть несколько значений — выигрыша в читаемости нет. Признак именно этого случая, а не «просто длинного метода».

**Решение — Extract Method Object** (у Фаулера Replace Method with Method Object): сам метод превращается в отдельный класс, его параметры и локальные переменные становятся полями этого класса. После этого переменные доступны всем приватным методам как поля — и `Extract Method` внутри проходит свободно, без передачи аргументов туда-сюда.

```java
// ДО: метод processLoanApplication с 8 локальными переменными
public class LoanService {
    public LoanResult processLoanApplication(LoanApplication app) {
        BigDecimal income = app.getAnnualIncome();
        BigDecimal existingDebt = creditBureauService.getDebt(app.getApplicantId());
        double dti = existingDebt.doubleValue() / income.doubleValue();  // debt-to-income
        BigDecimal requestedAmount = app.getRequestedAmount();
        int creditScore = creditBureauService.getCreditScore(app.getApplicantId());
        BigDecimal maxAllowed = income.multiply(BigDecimal.valueOf(5));
        boolean dtiOk = dti < 0.43;
        boolean scoreOk = creditScore >= 640;
        // ... 50 строк бизнес-логики с этими переменными ...
    }
}

// ПОСЛЕ: Method Object
public class LoanApplicationProcessor {
    // Локальные переменные → поля класса
    private final LoanApplication app;
    private final CreditBureauService creditBureauService;
    private BigDecimal income;
    private BigDecimal existingDebt;
    private double dti;
    private int creditScore;

    public LoanApplicationProcessor(LoanApplication app, CreditBureauService creditBureauService) {
        this.app = app;
        this.creditBureauService = creditBureauService;
    }

    public LoanResult process() {
        computeFinancials();
        validateDti();
        return buildResult();
    }

    private void computeFinancials() {
        income = app.getAnnualIncome();
        existingDebt = creditBureauService.getDebt(app.getApplicantId());
        dti = existingDebt.doubleValue() / income.doubleValue();
        creditScore = creditBureauService.getCreditScore(app.getApplicantId());
    }

    private void validateDti() {
        if (dti >= 0.43) throw new DtiExceededException(dti);
    }

    private LoanResult buildResult() { ... }
}

// LoanService стал простым:
public class LoanService {
    public LoanResult processLoanApplication(LoanApplication app) {
        return new LoanApplicationProcessor(app, creditBureauService).process();
    }
}
```

**Когда применять:** метод > 30-50 строк с 5+ локальными переменными; разбить обычным Extract Method без большого числа параметров не получается; нужно тестировать отдельные шаги расчёта. **Отличие от обычного Extract Method:** тот выносит самостоятельный фрагмент в метод; Method Object нужен именно тогда, когда фрагменты не самостоятельны — они связаны общим набором переменных, и эти переменные превращаются в общее состояние нового класса.

## See also

- [Code review](code-review-interview.md) — вопросы по code review
- [Технический долг](technical-debt-interview.md) — вопросы по техническому долгу
- [Паттерны проектирования](../design-patterns/design-patterns-interview.md) — GoF-паттерны, которые часто применяются при рефакторинге
- [Unit-тестирование](../testing/unit-testing-interview.md) — тесты как страховка при рефакторинге
- [Стратегии тестирования](../testing/test-strategies-interview.md) — планирование тестового покрытия перед рефакторингом
- [ООП в Java](../programming-languages/java/java-oop-interview.md) — принципы SOLID и проектирование классов

- [Code Coverage](code-coverage-interview.md)
- [Code review](code-review-interview.md)
- [Code Smells](code-smells-interview.md)
- [Static Analysis](static-analysis-interview.md)
- [Технический долг](technical-debt-interview.md)
