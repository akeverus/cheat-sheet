---
title: "OO Design: Дизайн торгового автомата"
description: "Низкоуровневый дизайн автомата продаж: State pattern, payment processing, change-making, inventory, thread safety, edge cases."
tags:
  - interview
  - system-design
  - design-vending-machine
  - oo-design
type: "interview"
difficulty: "intermediate"
aliases:
  - "Design Vending Machine interview"
  - "Vending Machine OO design"
  - "Дизайн автомата продаж собеседование"
  - "LLD Vending Machine"
updated: "2026-05-22"
---
# Вопросы на собеседовании: `OO Design — Vending Machine`

`Vending Machine` — **классическая задача на State pattern**. Главный фокус: state machine с явными переходами, no `if-else` на состояние, точная работа с деньгами (BigDecimal!), change-making algorithm, thread safety при concurrent restocking. Это **LLD-задача 30-45 мин**: показать чистый OO-дизайн, паттерны GoF и понимание edge cases (jam, out-of-change, power failure).

## Полезные ссылки

- [Refactoring Guru — State pattern](https://refactoring.guru/design-patterns/state)
- [Refactoring Guru — Strategy pattern](https://refactoring.guru/design-patterns/strategy)
- [GoF Design Patterns (книга)](https://en.wikipedia.org/wiki/Design_Patterns)
- [Java BigDecimal — почему не float для денег](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html)
- [Change-making problem (Wikipedia)](https://en.wikipedia.org/wiki/Change-making_problem)
- [Canonical coin systems (Pearson)](https://arxiv.org/abs/0809.0400)

## Содержание

**Requirements**
- [Q1. Функциональные и нефункциональные требования (!)](#q1-функциональные-и-нефункциональные-требования-)
- [Q2. Действующие лица и сценарии использования](#q2-действующие-лица-и-сценарии-использования)

**Классы и responsibilities**
- [Q3. Ключевые сущности: VendingMachine, Product, Inventory, Slot](#q3-ключевые-сущности-vendingmachine-product-inventory-slot)
- [Q4. Деньги: Coin, Note, способ оплаты (!)](#q4-деньги-coin-note-способ-оплаты-)
- [Q5. Почему деньги — это BigDecimal/центы, а не float (!)](#q5-почему-деньги--это-bigdecimalценты-а-не-float-)

**UML class diagram**
- [Q6. UML-диаграмма классов](#q6-uml-диаграмма-классов)

**State machine**
- [Q7. Состояния и переходы (Idle → ProductSelected → ...) (!)](#q7-состояния-и-переходы-idle--productselected---)
- [Q8. State pattern: интерфейс + 6 конкретных классов (!)](#q8-state-pattern-интерфейс--6-конкретных-классов-)
- [Q9. Защита от недопустимых действий: что блокировать в каждом состоянии](#q9-защита-от-недопустимых-действий-что-блокировать-в-каждом-состоянии)

**Sequence flows**
- [Q10. Основной сценарий: выбор → оплата → выдача → сдача](#q10-основной-сценарий-выбор--оплата--выдача--сдача)
- [Q11. Отмена в середине оплаты — возврат денег](#q11-отмена-в-середине-оплаты--возврат-денег)
- [Q12. Нет товара и недостаточно денег](#q12-нет-товара-и-недостаточно-денег)

**Паттерны GoF**
- [Q13. Паттерны GoF (!)](#q13-паттерны-gof-)
- [Q14. Почему State, а не switch-case (!)](#q14-почему-state-а-не-switch-case-)

**Change-making algorithm**
- [Q15. Жадный алгоритм для канонических систем монет (US/EUR) (!)](#q15-жадный-алгоритм-для-канонических-систем-монет-useur-)
- [Q16. Алгоритм DP для произвольных номиналов](#q16-алгоритм-dp-для-произвольных-номиналов)
- [Q17. Режим «только без сдачи» и граничные случаи](#q17-режим-только-без-сдачи-и-граничные-случаи)

**SOLID применение**
- [Q18. Применение принципов SOLID](#q18-применение-принципов-solid)

**Thread safety и edge cases**
- [Q19. Параллельное пополнение и покупка: synchronized / ReentrantLock (!)](#q19-параллельное-пополнение-и-покупка-synchronized--reentrantlock-)
- [Q20. Сбой питания при выдаче, замятие монет, мошенничество](#q20-сбой-питания-при-выдаче-замятие-монет-мошенничество)
- [Q21. Журнал транзакций (audit log)](#q21-журнал-транзакций-audit-log)

**Extensions**
- [Q22. Расширения: безналичная оплата, лояльность, корзина, динамическое ценообразование](#q22-расширения-безналичная-оплата-лояльность-корзина-динамическое-ценообразование)
- [Q23. Подход к тестированию: переходы состояний + property-based](#q23-подход-к-тестированию-переходы-состояний--property-based)
- [Q24. Антипаттерны: гигантский switch, деньги во float, открытое изменяемое состояние](#q24-антипаттерны-гигантский-switch-деньги-во-float-открытое-изменяемое-состояние)

---

## Q1. Функциональные и нефункциональные требования (!)

Сначала фиксируем границы задачи. Это запускает любое LLD-собеседование: показать, что вы отделяете *что* система делает (функции) от того, *насколько хорошо* она это делает (нефункциональные ограничения). У автомата продаж эти ограничения непривычно жёсткие — он работает с реальными деньгами и физическим товаром, поэтому ошибка стоит денег, а не просто 500-й ответ.

**Функциональные требования (что машина делает):**

| Сценарий | Описание |
|---|---|
| Select product | Покупатель выбирает слот (например, `A3`) |
| Insert coins/notes | Принимать монеты/банкноты, считать баланс |
| Insert card (NFC) | Безналичная оплата через payment gateway |
| Dispense product | Выдать товар после успешной оплаты |
| Return change | Точно посчитать и выдать сдачу |
| Cancel | Вернуть деньги, пока машина принимает оплату и выдача ещё не началась |
| Restock (admin) | Авторизованный персонал пополняет товар |
| Maintenance mode | Перевод в `OutOfService` (для ремонта) |

**Нефункциональные требования** — здесь и проявляется специфика именно автомата:

- **Отклик в реальном времени.** Локальные шаги (выбор слота, приём монеты, выдача, отмена) — меньше секунды: это hardware-устройство, пользователь стоит у машины и ждёт реакции мгновенно. Онлайн-авторизация карты в этот бюджет не укладывается — на неё закладывают несколько секунд, индикатор прогресса и таймаут, по которому сессия откатывается.
- **Не отдавать товар без оплаты в покупательском режиме** — главный инвариант. Его нарушение = прямой убыток, поэтому проверяется на доменном уровне, а не только в UI. Выдача без платежа существует только в явно выделенных режимах (free-vend, сервисная тестовая выдача, компенсация сбоя) — под аутентификацией и с отдельной записью в журнале.
- **Точная сдача** — арифметика с float запрещена: ошибка округления на копейку при тысячах транзакций превращается в недостачу кассы.
- **Журнал транзакций** — нужен для сверки кассы (reconciliation) и выявления мошенничества (fraud detection).
- **Честная недоступность** — состояние `OutOfService` явное и видимое, а не молчаливый отказ: пользователь сразу понимает, что машина не работает.
- **Безопасность** — admin-доступ только через аутентификацию (ключ / RFID / passcode), чтобы никто чужой не пополнял кассу и не менял цены.

---

## Q2. Действующие лица и сценарии использования

С системой работают три разных типа пользователей, и наборы операций у них почти не пересекаются. Развести их важно: наборы действий пересекаются только в сервисных сценариях (test vend, проверка монетоприёмника), а разводятся не сами операции, а канал вызова и права на них — это и задаёт границу между публичным и admin-API.

```mermaid
flowchart LR
    Customer((Customer)) --> SelectProduct[Select Product]
    Customer --> InsertMoney[Insert Money]
    Customer --> Cancel[Cancel]
    Restocker((Restocker)) --> Refill[Refill Inventory]
    Restocker --> CollectCash[Collect Cash]
    Admin((Admin)) --> Maintain[Enter Maintenance]
    Admin --> Audit[View Audit Log]
    Admin --> SetPrice[Update Prices]
```

- **Customer** — основной актёр; у лицевой панели он один за раз, поэтому покупательская сессия строго последовательна. Параллельно с ней идут операции Admin (удалённый защищённый канал) и Restocker (сервисный доступ у самого автомата) — и вот они уже конкурируют с покупкой за общее состояние (см. Q19).
- **Restocker** — пополняет товар, забирает наличность из кассы.
- **Admin** — конфигурация, отчёты, управление ценами; работает через защищённый канал, а не через лицевую панель.

---

## Q3. Ключевые сущности: VendingMachine, Product, Inventory, Slot

Скелет дизайна — четыре класса с чётко разведёнными зонами ответственности. `VendingMachine` играет роль Context в паттерне State: он не содержит логики переходов, а делегирует каждое событие текущему состоянию. Состояние одной покупки (выбранный товар, накопленный баланс, выбранная стратегия оплаты) живёт прямо в Context, потому что оно общее для всех состояний и переживает их смену.

Обратите внимание на разделение `Product` и `Slot`. Это типичная ошибка — слить их в одно. Но `Product` (SKU, имя, цена) — это *что* продаётся, а `Slot` (`"A3"`, количество) — это *где физически лежит* товар: один и тот же `Product` может занимать несколько слотов, а слот привязан к конкретной ячейке автомата.

```java
public class VendingMachine {                    // Context (State pattern)
    private State currentState;                  // current state object
    private final Inventory inventory;
    private final ChangeDispenser changeDispenser;
    private final AuditLog auditLog;
    private final List<InventoryObserver> observers = new ArrayList<>();

    // Транзакционный контекст текущей покупки
    private String selectedSlotId;               // из какого слота выдаём
    private Product selectedProduct;
    private BigDecimal currentBalance = BigDecimal.ZERO;
    private PaymentStrategy paymentStrategy;

    public VendingMachine(Inventory inventory, ChangeDispenser cd, AuditLog log) {
        this.inventory = inventory;
        this.changeDispenser = cd;
        this.auditLog = log;
        this.currentState = new IdleState(this);  // Context всегда стартует в Idle
    }

    // Делегирование событий текущему состоянию
    public void selectProduct(String slotId) { currentState.onSelectProduct(slotId); }
    public void insertMoney(Money m)         { currentState.onInsertMoney(m); }
    public void cancel()                     { currentState.onCancel(); }
    public void dispense()                   { currentState.onDispense(); }

    void setState(State next) { this.currentState = next; }
    // ... getters для состояний
}

public record Product(
    String id,                      // SKU
    String name,
    BigDecimal price                // в той же валюте, что Money
) {}

public final class Slot {           // физический слот в автомате
    private final String slotId;    // "A3"
    private final Product product;
    private int quantity;           // текущее количество в слоте

    public Slot(String slotId, Product product, int quantity) {
        this.slotId = slotId; this.product = product; this.quantity = quantity;
    }
    // ... геттеры, setQuantity
}

public class Inventory {
    private final Map<String, Slot> slots;  // slotId → Slot

    public Inventory(Map<String, Slot> slots) { this.slots = slots; }

    public Optional<Slot> getSlot(String id)   { ... }
    public boolean isAvailable(String id)      { ... }
    public synchronized void decrement(String id) { ... }
    public synchronized void refill(String id, int qty) { ... }
}
```

---

## Q4. Деньги: Coin, Note, способ оплаты (!)

Деньги моделируются типобезопасно, а не «числом и строкой». Номиналы — это `enum`: множество монет и банкнот фиксировано и известно на этапе компиляции, поэтому «монету в 7 центов» невозможно *смоделировать* — физически вставить можно что угодно, но неопознанный жетон отсекает валидатор на границе, и в домен он не попадает. Само платёжное средство — `sealed interface Money` с тремя реализациями (монета, банкнота, списание с карты): sealed-иерархия гарантирует, что компилятор знает полный список вариантов и `switch` по ним проверяется на полноту.

Способ оплаты вынесен в отдельную ось — паттерн **Strategy**. Это разводит два независимых вопроса: *чем* платят (кэш / карта / NFC) и *что* при этом происходит с балансом. Добавить новый способ оплаты можно, не трогая `VendingMachine` (см. Q13, Q18).

```java
public enum Coin {
    PENNY(new BigDecimal("0.01")),
    NICKEL(new BigDecimal("0.05")),
    DIME(new BigDecimal("0.10")),
    QUARTER(new BigDecimal("0.25"));
    private final BigDecimal value;
    Coin(BigDecimal v) { this.value = v; }
    public BigDecimal getValue() { return value; }
}

public enum Note {
    ONE(new BigDecimal("1")),
    FIVE(new BigDecimal("5")),
    TEN(new BigDecimal("10")),
    TWENTY(new BigDecimal("20"));
    // ...
}

public sealed interface Money permits CoinMoney, NoteMoney, CardCharge {
    BigDecimal amount();
}
public record CoinMoney(Coin coin) implements Money {
    public BigDecimal amount() { return coin.getValue(); }
}
public record NoteMoney(Note note) implements Money { ... }
public record CardCharge(BigDecimal amount, String tokenizedCard) implements Money { ... }
```

**Payment method (Strategy):**

```java
public interface PaymentStrategy {
    PaymentResult charge(BigDecimal amount);
    void refund(BigDecimal amount);
}

public class CashPaymentStrategy implements PaymentStrategy { ... }
public class CardPaymentStrategy implements PaymentStrategy {
    private final PaymentGateway gateway;        // Stripe/Adyen
    private final String tokenizedCard;          // токен, а не PAN (см. Q20)

    public CardPaymentStrategy(PaymentGateway gateway, String tokenizedCard) {
        this.gateway = gateway; this.tokenizedCard = tokenizedCard;
    }

    @Override public PaymentResult charge(BigDecimal amount) {
        return gateway.charge(amount, tokenizedCard);
    }

    @Override public void refund(BigDecimal amount) {
        gateway.refund(amount, tokenizedCard);
    }
}
public class NfcPaymentStrategy implements PaymentStrategy { ... }
```

---

## Q5. Почему деньги — это BigDecimal/центы, а не float (!)

Коротко: `float` и `double` точно хранят только дроби со знаменателем — степенью двойки (0.5, 0.25, 0.125), а цены состоят ровно из других (0.1, 0.2, 0.01). Причина — представление **IEEE 754 binary**: дробь `0.1` в двоичной системе бесконечна и периодична, поэтому уже её запись даёт погрешность.

```java
double a = 0.1 + 0.2;          // 0.30000000000000004 ❗
BigDecimal b = new BigDecimal("0.1").add(new BigDecimal("0.2"));  // 0.3 ✓
```

В автомате ломается не бухгалтерия по копейке, а логика. Покупатель внёс 0.7 и 0.1 при цене 0.8 — в `double` баланс равен `0.7999999999999999`, и проверка `balance >= price` даёт false: машина не отдаёт товар, хотя внесено ровно. А сдача, посчитанная вычитанием, выходит вида `0.09999999999999998` (это `0.3 - 0.2`), и уже её округление расходится с кассой.

**Чем считать деньги — варианты:**

| Подход | Плюсы | Минусы |
|---|---|---|
| `BigDecimal` со scale=2 | Точно, читабельно, есть MathContext | Чуть медленнее, нужен явный `.setScale(2, RoundingMode.HALF_UP)` |
| `long cents` (целые центы) | Быстро, ложится в БД нативно | Нет защиты от смешения валют, ручное форматирование |
| `Money` value-object (Joda Money / Moneta) | Типобезопасно, валюта внутри типа | Внешняя зависимость |
| `double` / `float` | — | **Для сумм к оплате, сдачи и остатков — никогда.** Допустимы разве что аналитические агрегаты, где цент не единица учёта |

**Эмпирическое правило:** на собеседовании сразу объявите `BigDecimal` со scale=2 и явным `RoundingMode` под правило домена — `HALF_UP` для розничной цены, `HALF_EVEN` для бухгалтерских агрегатов — он зашит в готовые `MathContext.DECIMAL32/64/128`, тогда как `new MathContext(precision)` берёт `HALF_UP`. Это маркер senior-уровня — интервьюер видит, что вы знаете о проблеме до того, как она выстрелит.

---

## Q6. UML-диаграмма классов

Диаграмма собирает воедино всё, что обсуждалось выше. Главное, на что смотреть при чтении, — тип связей:

- **Композиция** (закрашенный ромб, `*--`): `VendingMachine` владеет `Inventory`, `ChangeDispenser` и `AuditLog` — эти части не живут без машины.
- **Агрегация** (пустой ромб, `o--`): `State`, `PaymentStrategy` и набор `InventoryObserver` подключаются извне и заменяемы — объект состояния подставляется через `setState`, стратегия оплаты выбирается на транзакцию. Сюда же `Inventory o-- Product`: карточка товара принадлежит каталогу и переиспользуется несколькими слотами.
- **Реализация** (`<|..`): шесть конкретных состояний реализуют `State`, три стратегии — `PaymentStrategy`.

Именно эти связи позволяют расширять систему через новые классы, а не правки существующих (см. OCP в Q18).

```mermaid
classDiagram
    class VendingMachine {
        -State currentState
        -Inventory inventory
        -ChangeDispenser changeDispenser
        -BigDecimal currentBalance
        -Product selectedProduct
        +selectProduct(slotId)
        +insertMoney(Money)
        +cancel()
        +dispense()
        +setState(State)
    }

    class State {
        <<interface>>
        +onSelectProduct(slotId)
        +onInsertMoney(Money)
        +onCancel()
        +onDispense()
    }

    class IdleState
    class ProductSelectedState
    class AcceptingPaymentState
    class DispensingState
    class ReturningChangeState
    class OutOfServiceState

    class Product {
        -id
        -name
        -BigDecimal price
    }

    class Inventory {
        -Map slots
        +getSlot(id)
        +decrement(id)
        +refill(id, qty)
    }

    class PaymentStrategy {
        <<interface>>
        +charge(amount) PaymentResult
        +refund(amount)
    }

    class CashPaymentStrategy
    class CardPaymentStrategy
    class NfcPaymentStrategy

    class ChangeDispenser {
        -Map coinStock
        +computeChange(amount) List~Coin~
        +dispense(coins)
    }

    class InventoryObserver {
        <<interface>>
        +onLowStock(slotId)
    }

    class AuditLog {
        +record(TransactionRecord)
    }

    VendingMachine o-- State : currentState
    State <|.. IdleState
    State <|.. ProductSelectedState
    State <|.. AcceptingPaymentState
    State <|.. DispensingState
    State <|.. ReturningChangeState
    State <|.. OutOfServiceState
    VendingMachine *-- Inventory
    VendingMachine *-- ChangeDispenser
    VendingMachine *-- AuditLog
    VendingMachine o-- PaymentStrategy
    Inventory o-- Product
    PaymentStrategy <|.. CashPaymentStrategy
    PaymentStrategy <|.. CardPaymentStrategy
    PaymentStrategy <|.. NfcPaymentStrategy
    VendingMachine o-- "*" InventoryObserver
```

---

## Q7. Состояния и переходы (Idle → ProductSelected → ...) (!)

Это ядро задачи. Автомат — конечный автомат (FSM): в каждый момент он ровно в одном состоянии, и набор допустимых действий полностью определяется этим состоянием. Сначала рисуют диаграмму переходов — она становится спецификацией, по которой потом пишется код состояний (Q8) и тесты переходов (Q23).

Читайте диаграмму как контракт: стрелка — это разрешённый переход с указанием события и, при необходимости, сторожевого условия. Переходов, которых на диаграмме нет, быть не должно: часть таких вызовов — идемпотентный no-op (повторный `cancel()` в `Idle`), остальные падают с `IllegalStateException` (Q9), а неверный аргумент — это отдельный случай `IllegalArgumentException`. Обратите внимание на «петли»: `AcceptingPayment → AcceptingPayment` при `balance < price` означает, что машина копит баланс, пока его не хватит на товар.

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> ProductSelected : selectProduct (in stock)
    Idle --> Idle : selectProduct (out of stock / invalid)
    Idle --> OutOfService : admin maintenance

    ProductSelected --> AcceptingPayment : insertMoney (1st coin)
    ProductSelected --> Idle : cancel
    ProductSelected --> Idle : timeout (30s)

    AcceptingPayment --> AcceptingPayment : insertMoney (balance < price)
    AcceptingPayment --> Dispensing : balance >= price
    AcceptingPayment --> Idle : cancel (refund balance)
    AcceptingPayment --> Idle : timeout (refund)

    Dispensing --> ReturningChange : product released, change > 0
    Dispensing --> Idle : product released, exact amount
    Dispensing --> OutOfService : mechanical failure

    ReturningChange --> Idle : change dispensed
    ReturningChange --> OutOfService : insufficient coins for change

    OutOfService --> Idle : admin resolves
```

**Ключевые переходы** (событие и условие → побочный эффект):

| Переход | Событие / условие | Побочный эффект |
|---|---|---|
| `Idle → ProductSelected` | `selectProduct(slotId)` валиден | сохранить `selectedProduct` |
| `ProductSelected → AcceptingPayment` | `insertMoney(m)` | `balance += m.amount()` |
| `AcceptingPayment → Dispensing` | `insertMoney(m)` при `balance >= price` | списать оплату, уменьшить остаток слота |
| `ProductSelected → Idle` | `cancel()` | возвращать нечего, баланс ещё нулевой |
| `AcceptingPayment → Idle` | `cancel()` | вернуть `currentBalance` |
| `Dispensing → *` | товар выдан | записать транзакцию в журнал (обе ветки — со сдачей и под расчёт) |
| `Dispensing → ReturningChange` | товар выдан, `change > 0` | посчитать монеты сдачи |
| `ReturningChange → Idle` | сдача выдана | завершить транзакцию |

---

## Q8. State pattern: интерфейс + 6 конкретных классов (!)

Теперь переводим диаграмму из Q7 в код. Каждое состояние — отдельный класс, реализующий общий интерфейс `State`. Классов шесть — по одному на каждое состояние диаграммы Q7. Самый короткоживущий из них `ReturningChangeState`: машина проходит его между выдачей товара и выдачей сдачи. Ключевой приём — **default-методы в интерфейсе бросают `IllegalStateException`**: состоянию достаточно переопределить только те операции, которые в нём разрешены, а все остальные автоматически становятся запрещёнными. Это убирает горы шаблонного кода и делает «запрещено по умолчанию» поведением, а не тем, о чём надо помнить.

**Интерфейс State (default-методы бросают `IllegalStateException`):**

```java
public interface State {
    default void onSelectProduct(String slotId) { illegal("selectProduct"); }
    default void onInsertMoney(Money m)         { illegal("insertMoney"); }
    default void onCancel()                     { illegal("cancel"); }
    default void onDispense()                   { illegal("dispense"); }

    default void illegal(String op) {
        throw new IllegalStateException(op + " not allowed in " + getClass().getSimpleName());
    }
}
```

**Idle:**

```java
public class IdleState implements State {
    private final VendingMachine vm;
    public IdleState(VendingMachine vm) { this.vm = vm; }

    @Override
    public void onSelectProduct(String slotId) {
        var slot = vm.getInventory().getSlot(slotId)
            .orElseThrow(() -> new InvalidSlotException(slotId));
        if (slot.getQuantity() == 0) {
            vm.showMessage("Sold out: " + slotId);
            return;     // остаёмся в Idle
        }
        vm.setSelectedSlot(slotId);              // списывать будем именно этот слот
        vm.setSelectedProduct(slot.getProduct());
        vm.setState(new ProductSelectedState(vm));
        vm.showMessage("Selected: " + slot.getProduct().getName()
                       + ". Price: " + slot.getProduct().getPrice());
    }
    // onInsertMoney/onCancel/onDispense → IllegalStateException (default)
}
```

**ProductSelected:**

```java
public class ProductSelectedState implements State {
    private final VendingMachine vm;
    public ProductSelectedState(VendingMachine vm) { this.vm = vm; }

    @Override
    public void onInsertMoney(Money m) {
        vm.addToBalance(m.amount());
        vm.setState(new AcceptingPaymentState(vm));
        checkAndAdvance();
    }

    @Override
    public void onCancel() {
        vm.clearSelection();
        vm.setState(new IdleState(vm));
    }

    @Override
    public void onSelectProduct(String slotId) {
        // переключение выбора до оплаты — разрешено
        vm.setState(new IdleState(vm));
        vm.getState().onSelectProduct(slotId);
    }

    private void checkAndAdvance() {
        if (vm.getCurrentBalance().compareTo(vm.getSelectedProduct().getPrice()) >= 0) {
            vm.setState(new DispensingState(vm));
            vm.getState().onDispense();
        }
    }
}
```

**AcceptingPayment:**

```java
public class AcceptingPaymentState implements State {
    private final VendingMachine vm;
    public AcceptingPaymentState(VendingMachine vm) { this.vm = vm; }

    @Override
    public void onInsertMoney(Money m) {
        vm.addToBalance(m.amount());
        if (vm.getCurrentBalance().compareTo(vm.getSelectedProduct().getPrice()) >= 0) {
            vm.setState(new DispensingState(vm));
            vm.getState().onDispense();
        }
    }

    @Override
    public void onCancel() {
        vm.getPaymentStrategy().refund(vm.getCurrentBalance());
        vm.clearTransaction();
        vm.setState(new IdleState(vm));
    }
}
```

**Dispensing:**

```java
public class DispensingState implements State {
    private final VendingMachine vm;
    public DispensingState(VendingMachine vm) { this.vm = vm; }

    @Override
    public void onDispense() {
        var product  = vm.getSelectedProduct();
        var price    = product.getPrice();
        var balance  = vm.getCurrentBalance();
        var change   = balance.subtract(price);

        // 1) charge для card payment
        vm.getPaymentStrategy().charge(price);
        // 2) физически выдать
        vm.releaseProduct(product);
        // 3) списать остаток того слота, из которого выдали
        vm.getInventory().decrement(vm.getSelectedSlotId());

        vm.getAuditLog().record(new TransactionRecord(
            Instant.now(), product, price, vm.getPaymentStrategy().method(), change));

        if (change.compareTo(BigDecimal.ZERO) > 0) {
            vm.setState(new ReturningChangeState(vm, change));
            vm.getState().onDispense();   // self-trigger
        } else {
            vm.clearTransaction();
            vm.setState(new IdleState(vm));
        }
    }
}
```

**ReturningChange:**

```java
public class ReturningChangeState implements State {
    private final VendingMachine vm;
    private final BigDecimal amount;
    public ReturningChangeState(VendingMachine vm, BigDecimal amount) {
        this.vm = vm; this.amount = amount;
    }

    @Override
    public void onDispense() {
        try {
            var coins = vm.getChangeDispenser().computeChange(amount);
            vm.getChangeDispenser().dispense(coins);
            vm.clearTransaction();
            vm.setState(new IdleState(vm));
        } catch (InsufficientCoinsException e) {
            // сдачу вернуть нечем — довести транзакцию до конца, а не бросить баланс висеть
            vm.getPaymentStrategy().refund(amount);
            vm.notifyAdmin("Cannot make change: " + amount);
            vm.clearTransaction();
            vm.setState(new OutOfServiceState(vm));
        }
    }
}
```

**OutOfService:**

```java
public class OutOfServiceState implements State {
    private final VendingMachine vm;
    public OutOfServiceState(VendingMachine vm) { this.vm = vm; }

    @Override
    public void onSelectProduct(String slotId) {
        vm.showMessage("Out of service. Please use another machine.");
    }
    public void resolve() {  // вызывается admin-ом
        vm.setState(new IdleState(vm));
    }
}
```

---

## Q9. Защита от недопустимых действий: что блокировать в каждом состоянии

Эта таблица — обратная сторона диаграммы переходов: на каждом пересечении «состояние × действие» видно, разрешена операция или нет. Если ячейка помечена ❌, машина обязана отвергнуть действие, а не сделать вид, что ничего не произошло.

| Состояние | onSelect | onInsertMoney | onCancel | onDispense |
|---|---|---|---|---|
| `Idle` | OK | ❌ illegal | ❌ illegal | ❌ illegal |
| `ProductSelected` | OK (re-select) | → AcceptingPayment | refund (0) | ❌ illegal |
| `AcceptingPayment` | ❌ illegal | OK | refund balance | (internal trigger) |
| `Dispensing` | ❌ illegal | ❌ illegal | ❌ illegal | (internal trigger) |
| `OutOfService` | message | ❌ illegal | ❌ illegal | ❌ illegal |

**Принцип:** недопустимые операции → `IllegalStateException` (через default-метод интерфейса). Это подход **fail-fast**: баг в логике вскрывается сразу и громко, а не приводит к загадочному поведению где-то дальше по коду.

Важный момент про слои: UI должен гасить кнопки по состоянию — выбор товара недоступен во время оплаты и в `OutOfService`, отмена не предлагается в `Idle`, — но домен всё равно проверяет сам. Самой кнопки «выдать» в этой модели нет: выдачу запускает внутренний триггер домена, когда баланс покрыл цену. Это **эшелонированная защита** (defence in depth) — нельзя полагаться на то, что «снаружи» всё проверили правильно, потому что к домену можно обратиться и в обход UI (например, через сервисный порт).

---

## Q10. Основной сценарий: выбор → оплата → выдача → сдача

Диаграмма последовательности показывает, как объекты обмениваются сообщениями в успешном сценарии. Главное наблюдение — в `VendingMachine` (Context) нет ни одного условия вида «если состояние такое-то»: он принимает команду от покупателя и делегирует её текущему состоянию (`S`). Решение о переходе принимает состояние, а физические операции — списание в `Inventory`, выдачу, сдачу через `ChangeDispenser` и запись в `AuditLog` — по его команде выполняет сам контекст.

```mermaid
sequenceDiagram
    actor C as Customer
    participant VM as VendingMachine
    participant S as State
    participant Inv as Inventory
    participant CD as ChangeDispenser
    participant Log as AuditLog

    C->>VM: selectProduct("A3")
    VM->>S: onSelectProduct("A3")
    S->>Inv: getSlot("A3")
    Inv-->>S: Slot(Coke, $1.25, qty=5)
    S->>VM: setState(ProductSelectedState)
    S-->>C: "Selected Coke, $1.25"

    C->>VM: insertMoney(Quarter)
    VM->>S: onInsertMoney(0.25)
    S->>VM: balance = 0.25
    S->>VM: setState(AcceptingPaymentState)

    C->>VM: insertMoney(Dollar)
    VM->>S: onInsertMoney(1.00)
    S->>VM: balance = 1.25
    Note over S: balance >= price → Dispensing
    S->>VM: setState(DispensingState)
    S->>VM: onDispense()

    VM->>Inv: decrement("A3")
    VM->>VM: releaseProduct(Coke)
    VM->>Log: record(TransactionRecord)
    alt change > 0
        VM->>CD: computeChange(0.75)
        CD-->>VM: [Quarter, Quarter, Quarter]
        VM->>CD: dispense(coins)
    else exact amount
        Note over VM: change = 1.25 - 1.25 = 0
    end
    VM->>VM: setState(IdleState)
    VM-->>C: Product dispensed
```

**Вариант со сдачей** показан веткой `alt`: вставь покупатель $2, сдача составила бы $0.75, машина ушла бы в `ReturningChangeState`, и по его решению контекст попросил бы `ChangeDispenser` посчитать монеты (3×Quarter) и выдать их. В сценарии выше сумма ровная, поэтому отрабатывает ветка `else`.

---

## Q11. Отмена в середине оплаты — возврат денег

Покупатель вправе передумать, пока машина принимает оплату: после захвата платежа (capture) и начала выдачи отмена уже не принимается. До этого момента карточная оплата держится холдом, и отмена сводится к его снятию. Обработчик `onCancel` живёт в `AcceptingPaymentState`: он возвращает накопленный баланс и сбрасывает транзакцию обратно в `Idle`. Принципиально, что *перед* возвратом в `Idle` фиксируется запись в журнал — отмена тоже событие, которое должно сойтись при сверке кассы.

```java
// В AcceptingPaymentState
public void onCancel() {
    BigDecimal toRefund = vm.getCurrentBalance();
    if (toRefund.compareTo(BigDecimal.ZERO) > 0) {
        if (vm.getPaymentStrategy().isCashless()) {
            vm.getPaymentStrategy().voidAuthorization();      // снять холд, наличных тут нет
        } else {
            var returned = vm.getCoinAcceptor().returnEscrow();   // те же монеты — обратно
            var rest = toRefund.subtract(returned);
            if (rest.compareTo(BigDecimal.ZERO) > 0) {            // за ушедшую в стекер банкноту
                vm.getChangeDispenser().dispense(
                    vm.getChangeDispenser().computeChange(rest));
            }
        }
    }
    vm.getAuditLog().record(TransactionRecord.cancelled(toRefund));
    vm.clearTransaction();
    vm.setState(new IdleState(vm));
}
```

**Граничный случай — способ возврата зависит от способа оплаты:**

- **Карта / NFC:** до подтверждения продажи денег ещё не списали — автомат держит холд (pre-auth), поэтому отмена сводится к снятию холда, а не к возврату. Полноценный `refund` нужен, только если платёж уже захвачен (capture) — например, машина подтвердила продажу, а выдача сорвалась.
- **Наличные:** монеты покупателя лежат в escrow монетоприёмника и при отмене штатно возвращаются через coin return — в кассу и трубки размена они уходят только после завершения продажи. Банкнота, успевшая уйти в стекер, обратно не выдаётся: за неё возвращают эквивалент монетами из разменного фонда, и вот здесь уже важно, есть ли в фонде подходящие номиналы (см. Q17).

---

## Q12. Нет товара и недостаточно денег

Два разных «неудачных» пути, и обрабатываются они принципиально по-разному.

**Товар закончился (на этапе выбора):** машина просто остаётся в `Idle` и сообщает об этом. Это не ошибка состояния — состояние не меняется, денег ещё не было.

```java
// IdleState.onSelectProduct
if (slot.getQuantity() == 0) {
    vm.showMessage("Sold out");
    return;     // остаёмся в Idle, no state change
}
```

**Денег недостаточно** — главная опасность в том, что покупатель ушёл, оставив в машине баланс меньше цены. Деньги нельзя «зависнуть» в автомате навсегда. Решение — таймаут бездействия (30 сек), который живёт в `VendingMachine` и покрывает оба состояния незавершённой сессии: по нему машина сама выполняет cancel — с возвратом баланса, если он ненулевой, и просто откатом в `Idle`, если денег ещё не было.

```java
// Scheduler в VendingMachine
public void onUserInactive() {
    if (currentState instanceof AcceptingPaymentState
            || currentState instanceof ProductSelectedState) {
        currentState.onCancel();
    }
}
```

**Гонка за последним товаром** возникает между двумя одновременными покупками (пополнение счётчик только увеличивает). Одного синхронизированного `decrement` для неё мало: проверка остатка и списание должны быть одной атомарной операцией — `boolean tryReserve(slotId)` целиком под тем же монитором, что и `refill` (подробнее в Q19).

---

## Q13. Паттерны GoF (!)

Сильный ответ — не просто перечислить паттерны, а привязать каждый к конкретной проблеме в этом дизайне. State и Strategy здесь несущие, остальные — опциональные расширения, которые стоит назвать, чтобы показать кругозор, но не тащить в базовое решение без необходимости.

| Паттерн | Где | Зачем |
|---|---|---|
| **State** (главный) | `VendingMachine` + `State` интерфейс + 5 концретов | Убрать `switch/if` по состоянию, добавлять состояния без правки Context |
| **Strategy** | `PaymentStrategy` (Cash/Card/NFC) | Подменять способ оплаты, добавлять новые без правки VM |
| **Observer** | `InventoryObserver` слушает `Inventory.decrement` | Low-stock алерт админу, dashboard updates |
| **Command** | `AdminCommand`: `RefillCommand`, `SetPriceCommand`, `EnterMaintenanceCommand` | Очередь admin-действий и их запись в audit log; для undo интерфейсу нужен ещё `undo()` и снимок состояния |
| **Singleton** | `VendingMachine` (один на физический автомат) | Hardware bound — один контроллер на машину |
| **Factory Method** | `StateFactory.create(StateType, vm)` | Если состояний много и они с зависимостями. Статический `create` со switch — это Simple Factory, идиома, а не паттерн из каталога GoF |
| **Template Method** | `AbstractPaymentStrategy.process()` = validate → charge → log | Общий каркас для всех payment-стратегий |

**Observer — пример:**

```java
public interface InventoryObserver {
    void onLowStock(String slotId, int remaining);
    default void onOutOfStock(String slotId) { }
}

public class AdminDashboard implements InventoryObserver {
    @Override public void onLowStock(String slotId, int remaining) {
        sendAlert("Low stock " + slotId + ": " + remaining + " left");
    }
}

// в Inventory
public synchronized void decrement(String slotId) {
    var slot = slots.get(slotId);
    if (slot.getQuantity() <= 0) throw new OutOfStockException(slotId);
    slot.setQuantity(slot.getQuantity() - 1);
    if (slot.getQuantity() == 0) {
        observers.forEach(o -> o.onOutOfStock(slotId));
    } else if (slot.getQuantity() <= LOW_THRESHOLD) {
        observers.forEach(o -> o.onLowStock(slotId, slot.getQuantity()));
    }
}
```

**Command — для admin restocking:**

```java
public sealed interface AdminCommand
        permits RefillCommand, SetPriceCommand, EnterMaintenanceCommand {
    void execute(VendingMachine vm);
}
public record RefillCommand(String slotId, int qty) implements AdminCommand {
    public void execute(VendingMachine vm) {
        vm.getInventory().refill(slotId, qty);
    }
}
public record SetPriceCommand(String slotId, BigDecimal price) implements AdminCommand { ... }
public record EnterMaintenanceCommand() implements AdminCommand { ... }
```

---

## Q14. Почему State, а не switch-case (!)

Частый встречный вопрос интервьюера: «зачем городить пять классов, если можно одним `switch` по состоянию?» Ответ — наивный `switch` приводит к одной и той же конструкции в *каждом* методе, и поведение размазывается. Покажем антипаттерн и чем именно он плох.

**Антипаттерн (наивный подход):**

```java
public void insertMoney(Money m) {
    switch (state) {
        case IDLE:
            throw new IllegalStateException();
        case PRODUCT_SELECTED:
        case ACCEPTING_PAYMENT:
            balance = balance.add(m.amount());
            if (balance.compareTo(price) >= 0) {
                state = StateType.DISPENSING;
                dispense();
            } else {
                state = StateType.ACCEPTING_PAYMENT;
            }
            break;
        case DISPENSING:
            throw new IllegalStateException();
        // ...
    }
}
// и такой же switch в каждом методе onSelectProduct, onCancel, onDispense
```

**Чем плохо:**

- **Дублирование** — один и тот же `switch` по состоянию в каждом из ~5 методов.
- **Нарушение OCP** — новое состояние требует правок во *всех* методах сразу, легко что-то забыть.
- **Состояние трудно протестировать изолированно** — сначала надо привести весь объект в нужное состояние, и тест тянет за собой всю логику класса, а не одну ветку.
- **Высокая когнитивная нагрузка** — поведение размазано по 5×5 = 25 веткам `switch`, целостную картину одного состояния в голове не собрать.

**Как State это лечит:**

- Каждое состояние — отдельный класс, всё его поведение собрано в одном месте.
- Добавление состояния = новый класс; править приходится только те состояния, из которых в новое есть переход (или фабрику состояний), а не все методы сразу (OCP).
- Юнит-тест на каждое состояние пишется независимо от остальных.

---

## Q15. Жадный алгоритм для канонических систем монет (US/EUR) (!)

Сдача — это задача «набрать сумму минимальным числом монет». Жадный (greedy) алгоритм — берём самый крупный номинал, который ещё помещается, и повторяем. Он простой и быстрый, но даёт оптимум *не всегда* — только для так называемых канонических систем монет и только при неограниченном запасе каждого номинала. Реальные валюты (доллар, евро) каноничны, а вот кассеты автомата конечны, поэтому жадность со `hasInStock` — уже эвристика: она может выдать неминимальный набор или сообщить о невозможности размена там, где он существует (см. Q17).

```java
public List<Coin> computeChangeGreedy(BigDecimal amount) {
    BigDecimal remaining = amount;
    var result = new ArrayList<Coin>();
    // По убыванию номинала
    var coins = List.of(Coin.QUARTER, Coin.DIME, Coin.NICKEL, Coin.PENNY);
    for (var c : coins) {
        // считаем по копии остатков: реальный склад трогаем только при успехе
        while (remaining.compareTo(c.getValue()) >= 0 && availableInDraft(c)) {
            result.add(c);
            remaining = remaining.subtract(c.getValue());
            reserveInDraft(c);
        }
    }
    if (remaining.compareTo(BigDecimal.ZERO) > 0) {
        throw new InsufficientCoinsException(amount, remaining);   // склад не тронут
    }
    commitDraft(result);   // списываем монеты, только когда сумма набрана целиком
    return result;
}
```

**Почему greedy корректен для US/EUR:**

- **Каноническая система монет** — это система, в которой жадный алгоритм всегда выдаёт оптимум.
- US (1, 5, 10, 25 центов) — каноническая.
- EUR (1, 2, 5, 10, 20, 50 центов) — каноническая.
- **Контрпример** (неканоническая система): номиналы {1, 3, 4} и сдача 6 — жадный возьмёт `4+1+1 = 3 монеты`, а оптимум `3+3 = 2 монеты`. Именно из-за таких систем жадность нельзя применять вслепую.

**Сложность:** `O(n + k)`, где `n` — число номиналов (внешний цикл), `k` — число выданных монет: внутренние `while` суммарно дают ровно `k` итераций. На практике выполняется мгновенно.

---

## Q16. Алгоритм DP для произвольных номиналов

Если система монет неканоническая (или нужна гарантия оптимума при любом наборе номиналов), на смену жадности приходит динамическое программирование. Идея: `dp[i]` — минимальное число монет, чтобы набрать сумму `i`; считаем снизу вверх от 0 до целевой суммы, перебирая для каждой суммы все номиналы. Массив `prev` хранит, какой монетой пришли в каждую сумму, — по нему потом восстанавливается сам набор монет. Считать удобнее в целых центах, чтобы уйти от дробей.

```java
public List<Coin> computeChangeDP(BigDecimal amount) {
    // Конвертируем в cents для integer DP
    int target = amount.movePointRight(2).intValueExact();
    int[] coins = {25, 10, 5, 1};   // или любые

    // dp[i] = минимум монет для суммы i
    int[] dp = new int[target + 1];
    int[] prev = new int[target + 1];   // для восстановления решения
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;

    for (int i = 1; i <= target; i++) {
        for (int c : coins) {
            if (i - c >= 0 && dp[i - c] != Integer.MAX_VALUE && dp[i - c] + 1 < dp[i]) {
                dp[i] = dp[i - c] + 1;
                prev[i] = c;
            }
        }
    }
    // недостижимо только для наборов без монеты минимального шага (например {25,10,5} и target = 3)
    if (dp[target] == Integer.MAX_VALUE) throw new ChangeNotRepresentableException(target);

    // Восстановление
    var result = new ArrayList<Coin>();
    for (int i = target; i > 0; i -= prev[i]) result.add(centsToCoin(prev[i]));
    return result;
}
```

**Сравнение:**

| Алгоритм | Сложность | Когда |
|---|---|---|
| **Greedy** | `O(n + k)` | Canonical coin system (US, EUR) при достаточном запасе монет |
| **DP unbounded knapsack** | `O(target × n)` | Arbitrary denominations, либо unbounded coins |
| **DP bounded (limited stock)** | `O(target × n × max_stock)` | Учёт реального запаса монет каждого номинала |

**Эмпирическое правило для реального автомата:** жадный алгоритм с проверкой остатка монет, а запасной вариант — **bounded**-DP из третьей строки таблицы, который учитывает реальный запас каждого номинала. Листинг выше решает соседнюю задачу — произвольные номиналы при неограниченном запасе, — поэтому подставлять его как фолбэк при нехватке монет нельзя: он вернёт набор, которого в кассетах физически нет.

---

## Q17. Режим «только без сдачи» и граничные случаи

Тонкий момент, который отличает продуманное решение: в фонде может физически не хватить монет нужных номиналов, чтобы выдать сдачу. Это надо обнаружить *заранее*, а не после того, как товар уже выдан. Отсюда две вещи: метод `canMakeChange` для предварительной проверки — точный, по реальным остаткам кассет, а не жадный, — и режим «exact change only» (ровная сумма), который машина включает, когда разменный фонд опустел.

**Нехватка монет для сдачи:**

```java
public class ChangeDispenser {
    public List<Coin> computeChange(BigDecimal amount) {
        var result = tryGreedy(amount);
        if (result == null) result = boundedDp(amount);   // точный перебор по остаткам кассет
        if (result == null) throw new InsufficientCoinsException(amount);
        return result;
    }

    // Точная проверка выполнимости: bounded coin change по реальным остаткам кассет.
    // Жадности тут недостаточно — она даёт ложное «нельзя»: для 30 при пустых пятёрках
    // greedy возьмёт 25 и упрётся в остаток 5, хотя 10+10+10 разменивает сумму.
    public boolean canMakeChange(BigDecimal amount) {
        return boundedDp(amount) != null;
    }
}
```

**В IdleState — pre-check:**

```java
// худшая возможная сдача = самый крупный принимаемый номинал минус самый дешёвый товар
if (!changeDispenser.canMakeChange(maxNote.subtract(minPrice))) {
    vm.showMessage("EXACT CHANGE ONLY");
}
```

**Что делать, если сдачу выдать нечем — варианты:**

| Стратегия | Плюсы | Минусы |
|---|---|---|
| Отказать в транзакции | Никаких убытков | Покупатель недоволен |
| Округлить вниз (выдать меньше) | Транзакция проходит | Юридически нельзя без согласия покупателя |
| Купон / кредит на следующую покупку | Покупатель возвращается | Нужна персистентность |
| Перейти в `OutOfService` | Честно по отношению к клиенту | Прибыль = 0 до пополнения |

**Рекомендация:** при невозможности выдать сдачу **отменить транзакцию до выдачи товара** — вернуть деньги, товар не выдавать. Это не нарушает главный инвариант (не отдать товар без полной корректной оплаты). Перевод в `OutOfService` оправдан только при критической нехватке монет, когда машина в принципе не может корректно работать.

---

## Q18. Применение принципов SOLID

State и Strategy в этом дизайне — не самоцель, а способ выполнить SOLID. Полезно уметь явно показать, какой принцип закрывает каждое решение: интервьюер часто спрашивает «а где здесь SOLID?», и ответ «вот эти классы дают вот эти свойства» звучит куда сильнее общих слов.

**SRP (единственная ответственность)** — каждый класс делает одну вещь:
- `VendingMachine` — координация (Context) и тонкая точка входа для admin-команд.
- Каждый `State` — поведение в одном режиме.
- `Inventory` — управление товаром.
- `ChangeDispenser` — выдача сдачи.
- `AuditLog` — логирование.

**OCP (открытость/закрытость)** — добавить новое состояние (например, `MaintenanceCalibrationState`) = написать новый класс, не трогая обработку событий ни в `VendingMachine`, ни в остальных состояниях. Править придётся ровно одну точку — ту, откуда в новое состояние переходят. Система открыта для расширения, но закрыта для изменения.

**LSP (подстановка Лисков)** — все реализации `State` взаимозаменяемы, потому что контракт объявлен явно: операция, недопустимая в текущем режиме, завершается `IllegalStateException`. Раз это часть контракта супертипа, а не сюрприз конкретной реализации, вызывающий код одинаково работает с любым состоянием. Default-метод при этом лишь задаёт поведение по умолчанию — переопределить его реализация вправе.

**ISP (разделение интерфейсов)** — интерфейс `State` узкий (4 метода), а не «толстый» с примесью admin-методов. Admin-операции идут отдельным входом `VendingMachine.adminCommand(...)`, который лишь делегирует их в `AdminController`, — состояния лишних зависимостей не тащат.

**DIP (инверсия зависимостей)** — `VendingMachine` зависит от абстракции `PaymentStrategy`, а не от конкретного `StripeClient`. Благодаря этому тесты подставляют `FakePaymentStrategy` без сети и реального платёжного шлюза.

---

## Q19. Параллельное пополнение и покупка: synchronized / ReentrantLock (!)

Опасность в том, что чтение и запись счётчика остатка — не атомарная операция. Если покупка (`decrement`) и пополнение (`refill`) выполнятся вперемешку, одно из обновлений потеряется (lost update), и физический остаток разойдётся с учётным. Лечится сериализацией доступа к слоту: под одним монитором должны быть все мутирующие операции — и `decrement`, и `refill`, — иначе взаимного исключения между ними не возникает. Ниже три уровня решения — от простого к тонкому.

**Сценарий:** покупатель берёт Coke (`A3`, qty=1), и в тот же момент админ вызывает `refill("A3", 10)`.

```java
public class Inventory {
    private final Map<String, Slot> slots = new ConcurrentHashMap<>();

    public synchronized boolean decrement(String slotId) {
        var slot = slots.get(slotId);
        if (slot == null || slot.getQuantity() == 0) return false;
        slot.setQuantity(slot.getQuantity() - 1);
        notifyObservers(slot);
        return true;
    }

    public synchronized void refill(String slotId, int qty) {
        slots.compute(slotId, (k, s) -> {
            if (s == null) return new Slot(slotId, defaultProduct(slotId), qty);
            s.setQuantity(s.getQuantity() + qty);
            return s;
        });
    }
}
```

**Альтернатива — `ReentrantLock`** даёт более тонкий контроль (таймаут, `tryLock`): можно не блокироваться навечно, а отказать, если слот занят дольше заданного времени.

```java
private final ReentrantLock lock = new ReentrantLock();

public boolean decrement(String slotId) throws InterruptedException {
    if (!lock.tryLock(100, TimeUnit.MILLISECONDS)) return false;  // захват ДО try
    try {
        // ... critical section
        return true;
    } finally { lock.unlock(); }   // unlock только там, где лок реально взят
}
```

**Lock-free вариант** — на `AtomicInteger` и CAS-цикле (compare-and-set): обновляем счётчик без блокировки, повторяя попытку, если кто-то опередил.

```java
public class Slot {
    private final AtomicInteger quantity = new AtomicInteger(0);

    public boolean tryDecrement() {
        while (true) {
            int cur = quantity.get();
            if (cur == 0) return false;
            if (quantity.compareAndSet(cur, cur - 1)) return true;
        }
    }
}
```

**Что выбрать на практике:** реальный автомат однопользовательский — у машины физически один человек, поэтому конкурируют не два покупателя, а покупка и admin-операция (через сервисный порт / удалённо). Для такой нагрузки `synchronized` полностью достаточно; lock-free здесь — преждевременная оптимизация (overengineering), и это стоит честно сказать на собеседовании.

**Самый чистый вариант на время пополнения:** перевести машину в `OutOfService` на время `refill`. Окно гонки это резко сужает, но не закрывает само по себе: сам переход в `OutOfService` — тоже изменение общего состояния, поэтому он должен быть атомарным и дожидаться завершения уже начатой покупки.

---

## Q20. Сбой питания при выдаче, замятие монет, мошенничество

Это вопрос на устойчивость к сбоям физического устройства — то, чего нет в чисто софтверных задачах. Главный принцип всех трёх случаев один: деньги уже взяты, поэтому при любом сбое система обязана либо завершить транзакцию, либо вернуть средства, но никогда не «потерять» их молча.

**Сбой питания во время выдачи** — самый коварный случай: товар мог выйти, а мог застрять.

- **Сохранение состояния:** текущая транзакция пишется в энергонезависимую память (EEPROM/flash) *до* физической выдачи. Тогда после перезагрузки факт незавершённой покупки не теряется.
- Датчик выдачи моментальный (оптический барьер в зоне падения товара или датчик home-position мотора), состояния он не хранит — поэтому его срабатывание пишут в ту же энергонезависимую память сразу по факту. При перезагрузке `VendingMachine.recover()` читает последнюю транзакцию вместе с этой отметкой и либо помечает покупку завершённой, либо инициирует возврат денег.

```java
public void recover() {
    var pending = auditLog.findLastPendingTransaction();
    if (pending == null) return;
    if (pending.deliveryConfirmed()) {          // отметка датчика, записанная в момент выдачи
        auditLog.markComplete(pending);
    } else {
        auditLog.markFailed(pending);
        scheduleRefund(pending);
    }
}
```

**Замятие монеты (money jam):**

- Если датчик не сработал в течение таймаута → переход в `OutOfService` + оповещение админа.
- Со стороны покупателя ключевое правило: пока валидатор приёма не подтвердил, что монета принята, баланс **не** увеличивается. Иначе застрявшую монету засчитали бы как оплату.

**Распознавание подделок (counterfeit detection):**

- Проверка физическая и разная для купюр и монет: купюроприёмник смотрит IR/UV, магнитные метки краски и оптический образ, монетоприёмник — индуктивные катушки (сплав), диаметр, толщину и вес. Отклонённое в баланс не идёт.
- Атака «монета на нитке» (string pull — монету втягивают обратно после засчёта) парируется на уровне железа (anti-pullback).
- На уровне софта: ограничение частоты (rate limiting — десяток вбросов за 10 секунд для живого покупателя аномально быстро) и журнал всех событий для разбора.

**Мошенничество с картой:**

- Токенизация (PCI DSS): PAN на стороне автомата не хранится, наружу уходит токен — это резко сокращает область действия стандарта. Остаточный риск остаётся: токен без ограничения по мерчанту всё ещё чего-то стоит.
- Операция у автомата — card-present (EMV/NFC на терминале), поэтому 3DS здесь не применяется: он для card-not-present, то есть для e-commerce. На терминале работают contactless-лимиты и офлайн-риск-менеджмент EMV.
- Контроль частоты (velocity check): одна и та же карта 10 раз за минуту → блокировка.

---

## Q21. Журнал транзакций (audit log)

Журнал — это не «логирование для дебага», а финансовая необходимость. Каждая транзакция (успех, отмена, отказ, возврат) фиксируется записью с суммами и статусом, и в конце дня всё это должно сойтись с наличными в кассе. Без журнала сверка, расследование пропаж и восстановление после сбоя из Q20 держатся на косвенных источниках — необнуляемых счётчиках монетоприёмника (MDB/DEX) и выписке эквайера, — а связать их с конкретной продажей уже нечем.

```java
public record TransactionRecord(
    UUID id,
    Instant timestamp,
    String slotId,
    String productName,
    BigDecimal price,
    BigDecimal paid,
    BigDecimal change,
    PaymentMethod method,
    TransactionStatus status     // PENDING, SUCCESS, CANCELLED, FAILED, REFUNDED
) {}

public interface AuditLog {
    void record(TransactionRecord r);
    List<TransactionRecord> between(Instant from, Instant to);
    BigDecimal totalRevenue(LocalDate date);
    Optional<TransactionRecord> findLastPendingTransaction();
}
```

**Где хранить:**

- **Локально:** SQLite на контроллере автомата. WAL даёт быстрые записи и целостность базы, но переживание внезапного обесточивания обеспечивает не он, а fsync-политика: для `recover()` из Q20 нужен `PRAGMA synchronous=FULL`, иначе при `NORMAL` последние коммиты теряются.
- **Синхронизация:** периодическая выгрузка в центральную систему для бухгалтерии и сверки.
- **Сверка (reconciliation):** наличные в кассе на конец дня = начальный размен + `sum(paid по method=CASH)` − `sum(change)` − `sum(наличных возвратов)`. Безналичные оплаты в кассу не попадают и сверяются с выпиской эквайера отдельно. Если не сходится — журнал покажет, где разошлось.

---

## Q22. Расширения: безналичная оплата, лояльность, корзина, динамическое ценообразование

Финал собеседования — показать, что дизайн расширяемый. Сила базового решения в том, что почти все эти фичи добавляются через уже заложенные точки расширения (Strategy, состояния), а не переписыванием ядра.

**Безналичная оплата (Strategy + Adapter):** новый способ оплаты — это ещё одна реализация `PaymentStrategy`, обёрнутая вокруг SDK провайдера (Stripe). Ядро автомата про Stripe ничего не знает.

```java
public class StripeCardPaymentStrategy implements PaymentStrategy {
    private final StripeClient stripe;
    private final String paymentMethodId;

    public StripeCardPaymentStrategy(StripeClient stripe, String paymentMethodId) {
        this.stripe = stripe; this.paymentMethodId = paymentMethodId;
    }

    @Override
    public PaymentResult charge(BigDecimal amount) {
        try {
            var params = PaymentIntentCreateParams.builder()
                .setAmount(amount.setScale(2, RoundingMode.HALF_UP)
                                 .movePointRight(2).longValueExact())   // минорные единицы
                .setCurrency("usd")
                .setConfirm(true)
                .setPaymentMethod(paymentMethodId)
                .build();
            var intent = stripe.paymentIntents().create(params);
            return switch (intent.getStatus()) {
                case "succeeded" -> PaymentResult.success(intent.getId());
                // 3-D Secure и асинхронная обработка — это не отказ, а «ждём»
                case "requires_action", "processing" -> PaymentResult.pending(intent.getId());
                default -> PaymentResult.failure(intent.getLastPaymentError());
            };
        } catch (StripeException e) {          // checked-исключение SDK
            return PaymentResult.failure(e.getMessage());
        }
    }
}
```

**Программа лояльности:**

- `LoyaltyAccount` — отдельная сущность, привязывается через скан карты или мобильное приложение.
- Скидка применяется на этапе `IdleState.onSelectProduct` — там вычисляется `effectivePrice` с учётом баллов, и дальше вся машина работает с уже скорректированной ценой.

**Корзина из нескольких товаров:**

```java
// Расширение: вместо selectedProduct — корзина
public class VendingMachine {
    private final List<Product> cart = new ArrayList<>();

    public BigDecimal cartTotal() {
        return cart.stream().map(Product::price)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
// State machine та же, но Dispensing → multiple dispenses
```

**Динамическое ценообразование** — цена зависит от условий:

- По времени суток (happy hour −20%).
- По остатку (последние 2 шт → +10%).
- По спросу (популярный товар → +5%).
- **Реализация:** ещё одна Strategy — `PricingStrategy`, вызывается в `IdleState.onSelectProduct`. Логика «как считать цену» снова вынесена за пределы ядра, и правила можно менять, не трогая state machine.

---

## Q23. Подход к тестированию: переходы состояний + property-based

Конечный автомат удобно тестировать именно потому, что он формально описан. Диаграмма переходов из Q7 напрямую превращается в матрицу тест-кейсов «из состояния X по событию Y → ожидаем состояние Z». А самое ценное свойство (деньги не теряются) проверяется property-based тестом, который сам генерирует случайные последовательности действий.

**1. Тесты по таблице переходов:** каждая допустимая стрелка диаграммы — отдельный кейс.

```java
@ParameterizedTest
@MethodSource("stateTransitionMatrix")
void stateTransition(State from, Event event, Class<? extends State> expectedTo) {
    var vm = new VendingMachine();
    vm.setState(from);
    event.applyTo(vm);
    assertThat(vm.getState()).isInstanceOf(expectedTo);
}

static Stream<Arguments> stateTransitionMatrix() {
    return Stream.of(
        Arguments.of(idle(), selectProduct("A3"), ProductSelectedState.class),
        Arguments.of(productSelected(), insertMoney(0.25), AcceptingPaymentState.class),
        Arguments.of(acceptingPayment(), cancel(), IdleState.class),
        // полная матрица переходов
    );
}
```

**2. Недопустимые переходы:** проверяем, что запрещённое действие падает с `IllegalStateException`.

```java
@Test
void insertMoney_inIdle_throwsIllegalState() {
    var vm = new VendingMachine();  // в Idle
    assertThatThrownBy(() -> vm.insertMoney(new CoinMoney(Coin.QUARTER)))
        .isInstanceOf(IllegalStateException.class);
}
```

**3. Property-based (jqwik):** вместо ручных примеров формулируем инвариант, а фреймворк сам прогоняет сотни случайных последовательностей событий, пытаясь его сломать. Здесь проверяется главное финансовое свойство — деньги не возникают и не пропадают: внесённое равно выданному наружу плюс то, что осталось внутри машины (незавершённый эскроу и выручка кассы, куда как раз и попадает стоимость выданных товаров).

```java
// Invariant: внесено = выдано наружу + осталось внутри машины (эскроу + касса)
@Property
void noMoneyLoss(@ForAll("eventSequences") List<Event> events) {
    var vm = new VendingMachine();
    BigDecimal cashIn = BigDecimal.ZERO;
    BigDecimal cashOut = BigDecimal.ZERO;
    for (var e : events) {
        try {
            e.applyTo(vm);
            // деньги засчитываем только если событие реально прошло:
            // InsertMoney в Idle отвергается, монета в машину не попадает
            if (e instanceof InsertMoney im) cashIn = cashIn.add(im.amount());
        } catch (IllegalStateException ignored) {}
        cashOut = cashOut.add(vm.flushDispensedChange());
    }
    // isEqualByComparingTo, а не isEqualTo: у BigDecimal equals учитывает scale.
    // Стоимость выданного уже сидит в выручке машины, второй раз её прибавлять нельзя.
    assertThat(cashIn).isEqualByComparingTo(
        cashOut.add(vm.currentEscrow()).add(vm.collectedRevenue()));
}

@Provide
Arbitrary<List<Event>> eventSequences() {   // jqwik не сгенерирует свой тип сам
    return Arbitraries.of(selectProduct("A3"), insertMoney(0.25), cancel())
                      .list().ofMaxSize(20);
}
```

**4. Тесты на конкурентность:** нагружаем `Inventory` множеством потоков и проверяем, что обновления не теряются (lost update из Q19) — итоговый остаток сходится.

```java
@Test
void concurrentDecrementAndRefill_noLostUpdate() throws Exception {
    var inventory = new Inventory();
    var threads = 10;
    inventory.refill("A3", threads * 100);   // ровно столько, сколько будет декрементов
    var executor = Executors.newFixedThreadPool(threads);
    var latch = new CountDownLatch(threads * 100);
    for (int i = 0; i < threads * 100; i++) {
        executor.submit(() -> { inventory.decrement("A3"); latch.countDown(); });
    }
    latch.await();
    assertThat(inventory.getSlot("A3").get().getQuantity()).isZero();
}
```

---

## Q24. Антипаттерны: гигантский switch, деньги во float, открытое изменяемое состояние

Эта таблица — чек-лист того, чего интервьюер ждёт, что вы *не* сделаете. Каждая строка — частая ошибка и её правильная альтернатива из нашего дизайна. Пройтись по ней в конце ответа — хороший способ показать зрелость: вы не только знаете, как делать, но и понимаете, как делать не надо.

| Антипаттерн | Почему плохо | Как правильно |
|---|---|---|
| `switch (state)` в каждом методе | Дублирование, нарушение OCP | State pattern |
| `double price` | Ошибки floating-point | `BigDecimal` + scale=2 |
| `String state` («idle», «paid») | Тип-как-строка: опечатки молча проходят компиляцию | Enum или классы State |
| Публичный `vm.setState(SomeState)` | Любой код может сломать автомат | `setState()` package-private, вызывают только классы State |
| Бизнес-логика в UI | Тесты тянут за собой UI-обвязку: медленно и хрупко, headless спасает лишь частично | Домен отделён, UI лишь делегирует |
| `Map<String, Integer>` вместо inventory | Нет инкапсуляции, гонки | Класс `Inventory` с synchronized-методами |
| Хранение PAN в открытом виде (а CVV, дорожку и PIN — вообще) | Открытый PAN нарушает PCI DSS, а CVV/track/PIN хранить запрещено в любом виде | Токенизация: наружу и в журнал уходит токен |
| `Thread.sleep` для таймаута | Блокирует поток | `ScheduledExecutorService` + отмена задачи |
| Один гигантский метод `process()` | God-метод | Каждая операция = отдельный метод State |
| `vm.getInventory().getSlots()` отдаёт изменяемую `Map` | Вызывающий код может сломать инварианты | `Collections.unmodifiableMap` или DTO |

**Запах кода — «много `instanceof`»:**

```java
// ПЛОХО
if (state instanceof IdleState) { ... }
else if (state instanceof ProductSelectedState) { ... }
```

Если вы пишете такие цепочки, значит поведение, которому место **внутри** State, вытащено наружу. Лечится переносом этой логики в метод соответствующего состояния — тогда диспетчеризация по состоянию делается полиморфизмом, а не вручную.

---

## See also

- [System Design Interview — meta-методология](./system-design-interview.md)
- [OO Design: Parking Lot — родственный паттерн (Strategy/Factory)](./design-parking-lot-oo-interview.md)
- [OO Design: Elevator — State pattern + multi-elevator scheduling](./design-elevator-oo-interview.md)
- [Java Core — enum, sealed, records, BigDecimal](../programming-languages/java/java-core-interview.md)
- [Java Concurrency — synchronized, ReentrantLock, AtomicInteger](../programming-languages/java/java-concurrency-interview.md)
- [Clean Architecture — слои, DIP, тестируемость](../architecture/clean-architecture-interview.md)
- [Dynamic Programming — change-making как unbounded knapsack](../algorithms/algorithmic-paradigms/dynamic-programming-interview.md)
- [Algorithms — greedy, complexity analysis](../algorithms/algorithms-interview.md)
- [Design Payment System — реальная интеграция со Stripe/3DS](./design-payment-system-interview.md)
