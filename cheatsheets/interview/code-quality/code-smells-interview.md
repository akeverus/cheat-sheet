---
title: "Вопросы на собеседовании: Code Smells"
description: "Краткие ответы про запахи кода: каталог Фаулера, long methods, god classes, feature envy, primitive obsession, shotgun surgery, обнаружение и устранение."
tags:
  - interview
  - code-quality
  - code-smells
  - refactoring
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: Code Smells

Краткие ответы про запахи кода: классификация, каталог Мартина Фаулера, практическое обнаружение и способы устранения.

## Содержание

- [See also](#see-also)

**Основы**
- [Q1. Что такое code smell и чем он отличается от бага?](#q1-что-такое-code-smell-и-чем-он-отличается-от-бага)
- [Q2. Откуда взялся термин «запах кода»? Кто ввёл классификацию?](#q2-откуда-взялся-термин-запах-кода-кто-ввёл-классификацию)
- [Q3. Как code smells соотносятся с техническим долгом?](#q3-как-code-smells-соотносятся-с-техническим-долгом)
- [Q4. Какие основные категории code smells выделяет Мартин Фаулер?](#q4-какие-основные-категории-code-smells-выделяет-мартин-фаулер)

**Запахи уровня метода**
- [Q5. Что такое Long Method и почему он вреден?](#q5-что-такое-long-method-и-почему-он-вреден)
- [Q6. Что такое Long Parameter List и как его устранить?](#q6-что-такое-long-parameter-list-и-как-его-устранить)
- [Q7. Что такое Duplicate Code и какие техники рефакторинга применяются?](#q7-что-такое-duplicate-code-и-какие-техники-рефакторинга-применяются)
- [Q8. Что такое Dead Code и как его обнаружить?](#q8-что-такое-dead-code-и-как-его-обнаружить)

**Запахи уровня класса**
- [Q9. Что такое God Class / Large Class?](#q9-что-такое-god-class--large-class)
- [Q10. Что такое Feature Envy?](#q10-что-такое-feature-envy)
- [Q11. Что такое Data Clumps?](#q11-что-такое-data-clumps)
- [Q12. Что такое Primitive Obsession?](#q12-что-такое-primitive-obsession)
- [Q13. Что такое Lazy Class и когда класс считается лишним?](#q13-что-такое-lazy-class-и-когда-класс-считается-лишним)
- [Q14. Что такое Data Class и почему она проблематична?](#q14-что-такое-data-class-и-почему-она-проблематична)

**Запахи уровня архитектуры и связей**
- [Q15. Что такое Shotgun Surgery?](#q15-что-такое-shotgun-surgery)
- [Q16. Что такое Divergent Change?](#q16-что-такое-divergent-change)
- [Q17. Что такое Inappropriate Intimacy?](#q17-что-такое-inappropriate-intimacy)
- [Q18. Что такое Message Chains?](#q18-что-такое-message-chains)
- [Q19. Что такое Middle Man?](#q19-что-такое-middle-man)
- [Q20. Что такое Parallel Inheritance Hierarchies?](#q20-что-такое-parallel-inheritance-hierarchies)

**Запахи, связанные с условиями**
- [Q21. Что такое Switch Statements как code smell?](#q21-что-такое-switch-statements-как-code-smell)
- [Q22. Что такое Speculative Generality?](#q22-что-такое-speculative-generality)
- [Q23. Что такое Refused Bequest?](#q23-что-такое-refused-bequest)
- [Q24. Что такое Comments как code smell (по Фаулеру)?](#q24-что-такое-comments-как-code-smell-по-фаулеру)

**Обнаружение и инструменты**
- [Q25. Как автоматически обнаруживать code smells в Java-проекте?](#q25-как-автоматически-обнаруживать-code-smells-в-java-проекте)
- [Q26. Какие метрики кода связаны с code smells?](#q26-какие-метрики-кода-связаны-с-code-smells)
- [Q27. Как приоритизировать устранение code smells?](#q27-как-приоритизировать-устранение-code-smells)

---

## Q1. Что такое code smell и чем он отличается от бага?

**Code smell** (запах кода) — признак в коде, который указывает на возможную проблему в дизайне, но при этом код может работать корректно.

Отличие от бага:
- **Баг** — код работает неправильно, нарушает ожидаемое поведение.
- **Code smell** — код работает, но его структура затрудняет понимание, изменение и тестирование.

Смелл — это сигнал, что стоит остановиться и задуматься. Не каждый смелл обязательно требует рефакторинга — контекст решает.

---

## Q2. Откуда взялся термин «запах кода»? Кто ввёл классификацию?

Термин popularized **Кент Бек** (Kent Beck), классификацию систематизировал **Мартин Фаулер** в книге «Refactoring: Improving the Design of Existing Code» (1999, 2-е изд. 2018).

Каталог содержит ~22 базовых смелла (1-е издание) и расширен в 2-м. Каждый смелл сопровождён рекомендованным рефакторингом.

---

## Q3. Как code smells соотносятся с техническим долгом?

- **Code smell** — конкретный симптом плохого дизайна (длинный метод, дублирование).
- **Технический долг** — накопленная «стоимость» этих симптомов: время, которое придётся потратить на исправление в будущем.

Смелл → выявляет → технический долг → нужно погасить через рефакторинг.

---

## Q4. Какие основные категории code smells выделяет Мартин Фаулер?

| Категория | Примеры |
|-----------|---------|
| Bloaters (раздутый код) | Long Method, Large Class, Long Parameter List, Data Clumps, Primitive Obsession |
| Object-Orientation Abusers | Switch Statements, Refused Bequest, Temporary Field |
| Change Preventers | Divergent Change, Shotgun Surgery, Parallel Inheritance Hierarchies |
| Dispensables (лишнее) | Comments, Duplicate Code, Lazy Class, Dead Code, Speculative Generality |
| Couplers (связность) | Feature Envy, Inappropriate Intimacy, Message Chains, Middle Man |

---

## Q5. Что такое Long Method и почему он вреден?

Метод считается «длинным», если его сложно охватить взглядом и понять с первого прочтения. На практике — обычно более 20–30 строк (зависит от контекста).

Проблемы:
- Трудно тестировать изолированно.
- Сложно назвать — раз метод делает много вещей.
- Изменение одной части влечёт риск сломать другую.

**Рефакторинг: Extract Method**

```java
// До: один метод на всё
public void processOrder(Order order) {
    // валидация — 20 строк
    // расчёт скидки — 15 строк
    // формирование счёта — 25 строк
    // отправка уведомления — 10 строк
}

// После: каждый шаг — отдельный метод
public void processOrder(Order order) {
    validateOrder(order);
    applyDiscount(order);
    generateInvoice(order);
    sendNotification(order);
}
```

---

## Q6. Что такое Long Parameter List и как его устранить?

Метод принимает слишком много параметров (обычно 4+ считается тревожным сигналом), что затрудняет вызов и понимание.

**Рефакторинги:**
- **Introduce Parameter Object** — сгруппировать параметры в объект.
- **Preserve Whole Object** — передавать весь объект, а не его поля по отдельности.

```java
// До
void createUser(String firstName, String lastName, String email,
                String phone, String city, String zipCode) { ... }

// После: Parameter Object
record UserRegistrationRequest(
    String firstName, String lastName,
    String email, String phone,
    Address address
) {}

void createUser(UserRegistrationRequest request) { ... }
```

---

## Q7. Что такое Duplicate Code и какие техники рефакторинга применяются?

Один и тот же (или почти одинаковый) фрагмент кода встречается в нескольких местах. При изменении логики нужно обновлять все копии, что легко пропустить.

**Рефакторинги:**
- **Extract Method** — вынести в общий метод.
- **Pull Up Method** — переместить в родительский класс.
- **Form Template Method** — если структура одна, но шаги разные.

```java
// До: дублирование в двух классах
class OrderReport {
    String formatHeader() {
        return "=== Report ===\nDate: " + LocalDate.now();
    }
}
class InvoiceReport {
    String formatHeader() {
        return "=== Report ===\nDate: " + LocalDate.now();
    }
}

// После: общий базовый класс или утилита
abstract class BaseReport {
    protected String formatHeader() {
        return "=== Report ===\nDate: " + LocalDate.now();
    }
}
```

---

## Q8. Что такое Dead Code и как его обнаружить?

Dead code — код, который никогда не выполняется: неиспользуемые методы, переменные, ветки условий, константы.

**Обнаружение:**
- IDE-предупреждения (IntelliJ IDEA помечает серым).
- SonarQube — правила `squid:S1172`, `squid:S1481`.
- JaCoCo — непокрытые строки.

```java
// Примеры dead code
private void unusedMethod() { ... }  // никто не вызывает

if (false) {  // никогда не выполнится
    doSomething();
}

int result = compute();  // результат не используется
```

Удаление dead code снижает когнитивную нагрузку и уменьшает поверхность для ошибок.

---

## Q9. Что такое God Class / Large Class?

**God Class** (God Object) — класс, который знает слишком много и делает слишком много. Концентрирует логику, которая должна быть распределена по нескольким классам.

Признаки:
- Класс содержит сотни методов или тысячи строк.
- У него слишком много полей, относящихся к разным концепциям.
- Нарушает принцип единственной ответственности (SRP).

**Рефакторинг:** Extract Class, Move Method, Move Field.

```java
// До: God Class
class UserService {
    // Аутентификация
    boolean authenticate(String login, String password) { ... }
    // Уведомления
    void sendWelcomeEmail(User user) { ... }
    // Отчёты
    List<UserStats> generateReport(DateRange range) { ... }
    // Платёжная логика
    void processPayment(User user, Order order) { ... }
}

// После: разделение по ответственностям
class AuthService { boolean authenticate(...) { ... } }
class NotificationService { void sendWelcomeEmail(...) { ... } }
class UserReportService { List<UserStats> generateReport(...) { ... } }
class PaymentService { void processPayment(...) { ... } }
```

---

## Q10. Что такое Feature Envy?

Метод в классе A больше работает с данными класса B, чем со своими. Это сигнал, что метод находится не в том классе.

```java
// Feature Envy: метод OrderPrinter работает только с Order
class OrderPrinter {
    void print(Order order) {
        System.out.println(order.getCustomer().getName());
        System.out.println(order.getItems().size());
        System.out.println(order.getTotalPrice());
        // почти нет обращений к self
    }
}

// После рефакторинга: метод переезжает в Order
class Order {
    void print() {
        System.out.println(customer.getName());
        System.out.println(items.size());
        System.out.println(getTotalPrice());
    }
}
```

**Рефакторинг:** Move Method.

---

## Q11. Что такое Data Clumps?

Несколько полей или параметров, которые всегда ходят вместе — и в разных классах, и в списках параметров. Это сигнал, что они образуют самостоятельную концепцию.

```java
// До: city, street, zip везде ходят вместе
void deliver(String city, String street, String zip) { ... }
class Customer { String city; String street; String zip; }

// После: Address как отдельный класс
record Address(String city, String street, String zip) {}
void deliver(Address address) { ... }
class Customer { Address address; }
```

**Рефакторинг:** Extract Class, Introduce Parameter Object.

---

## Q12. Что такое Primitive Obsession?

Использование примитивных типов (`String`, `int`, `boolean`) вместо объектов предметной области. Логика валидации и форматирования при этом разбросана по всему коду.

```java
// До: Email как String
class User {
    String email;  // валидация где-то снаружи, или нигде
}

// После: Email как Value Object
record Email(String value) {
    Email {
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
    }
}
class User {
    Email email;
}
```

---

## Q13. Что такое Lazy Class и когда класс считается лишним?

**Lazy Class** — класс, который не несёт достаточной ответственности, чтобы оправдать своё существование. Обычно появляется после агрессивного рефакторинга или speculative generality.

Если класс содержит 1–2 тривиальных метода или просто делегирует вызовы — стоит рассмотреть его слияние с другим классом.

**Рефакторинг:** Inline Class, Collapse Hierarchy.

---

## Q14. Что такое Data Class и почему она проблематична?

**Data Class** — класс, который содержит только поля и геттеры/сеттеры, но не имеет поведения. Логика по работе с этими данными распылена по другим классам — нарушается инкапсуляция.

```java
// Data Class — только данные, нет поведения
class OrderItem {
    private int quantity;
    private BigDecimal price;
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}

// Лучше: добавить поведение туда, где оно должно жить
class OrderItem {
    private int quantity;
    private BigDecimal price;

    public BigDecimal subtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
```

---

## Q15. Что такое Shotgun Surgery?

Изменение одной функциональности требует правок в множестве несвязанных классов. Противоположность Divergent Change.

- **Divergent Change** — один класс меняется по многим причинам.
- **Shotgun Surgery** — одна причина изменения затрагивает много классов.

Признак: «хочу изменить логику логирования — надо поправить 15 файлов».

**Рефакторинг:** Move Method, Move Field, Inline Class — собрать разбросанную логику вместе.

---

## Q16. Что такое Divergent Change?

Класс меняется по разным причинам, не связанным между собой. Нарушение SRP.

Пример: `UserService` меняется при изменении логики аутентификации, и при изменении правил уведомлений, и при изменении схемы отчётов.

**Рефакторинг:** Extract Class — разделить на классы с единственной ответственностью.

---

## Q17. Что такое Inappropriate Intimacy?

Класс A слишком хорошо знает внутренности класса B: обращается к приватным полям через reflection, полагается на детали реализации. Высокое coupling.

**Рефакторинг:**
- Move Method / Move Field — перенести логику ближе к данным.
- Extract Class — выделить общую часть.
- Change Bidirectional Association to Unidirectional.

---

## Q18. Что такое Message Chains?

Цепочка последовательных вызовов: `a.getB().getC().getD().doSomething()`. Нарушает **Закон Деметры** (Law of Demeter): объект должен общаться только с ближайшими «соседями».

```java
// Message chain — хрупко к изменениям структуры
String city = order.getCustomer().getAddress().getCity().getName();

// Лучше: делегирующий метод
String city = order.getCustomerCity();  // скрывает структуру
```

**Рефакторинг:** Hide Delegate.

---

## Q19. Что такое Middle Man?

Класс существует только чтобы делегировать вызовы другому классу, не добавляя ценности. Обычно результат чрезмерного применения Hide Delegate.

```java
// Middle Man — PersonFacade ничего не делает сам
class PersonFacade {
    private Person person;
    String getName() { return person.getName(); }
    int getAge() { return person.getAge(); }
    String getAddress() { return person.getAddress(); }
}
```

**Рефакторинг:** Remove Middle Man — обращаться к Person напрямую.

---

## Q20. Что такое Parallel Inheritance Hierarchies?

При добавлении нового подкласса в одну иерархию приходится добавлять соответствующий подкласс в другую. Две иерархии растут синхронно.

Признак: имена классов из двух иерархий имеют общий префикс (`AnimalRenderer`, `AnimalExporter` + `DogRenderer`, `DogExporter`, `CatRenderer`, `CatExporter`).

**Рефакторинг:** сдвинуть ответственность так, чтобы один из иерархических деревьев стал ненужным.

---

## Q21. Что такое Switch Statements как code smell?

Большие блоки `switch`/`if-else` по типу объекта, особенно если они повторяются в нескольких местах — сигнал, что нужен полиморфизм.

```java
// До: switch по типу фигуры в нескольких методах
double area(Shape shape) {
    return switch (shape.type()) {
        case CIRCLE -> Math.PI * shape.radius() * shape.radius();
        case RECTANGLE -> shape.width() * shape.height();
        case TRIANGLE -> 0.5 * shape.base() * shape.height();
    };
}

// После: полиморфизм
interface Shape { double area(); }
class Circle implements Shape { public double area() { return Math.PI * r * r; } }
class Rectangle implements Shape { public double area() { return w * h; } }
```

**Рефакторинг:** Replace Conditional with Polymorphism, Replace Type Code with Subclasses.

---

## Q22. Что такое Speculative Generality?

Код написан «на будущее» — обобщения, абстракции, параметры, которые сейчас никто не использует. YAGNI (You Aren't Gonna Need It) нарушается.

Признаки: абстрактные классы без реальных наследников, параметры методов, которые всегда передаются одинаково, необъяснимые интерфейсы с единственной реализацией.

**Рефакторинг:** Collapse Hierarchy, Inline Class, Remove Parameter.

---

## Q23. Что такое Refused Bequest?

Подкласс наследует от родителя, но игнорирует или переопределяет большую часть унаследованных методов — фактически отказывается от наследства.

Сигнал: связь наследования выбрана неверно, лучше использовать композицию.

```java
// Refused Bequest: Stack наследует Vector, но не хочет большинство его методов
class Stack<T> extends Vector<T> {  // проблема в стандартной Java
    // push/pop используются, но add(index, element) нарушает инварианты стека
}
```

**Рефакторинг:** Replace Inheritance with Delegation.

---

## Q24. Что такое Comments как code smell (по Фаулеру)?

Комментарий — не всегда плохо, но если он объясняет, **что** делает код — это сигнал, что код недостаточно выразителен.

Правило: хороший комментарий объясняет **почему** (бизнес-причина, нестандартное решение), а не «что».

```java
// Плохо: объясняет «что» — код должен говорить сам
// Умножаем цену на 1.2, потому что добавляем 20% наценку
double finalPrice = price * 1.2;

// Хорошо: выразительный код без комментария
double finalPrice = price.withMarkup(STANDARD_MARKUP);

// Хорошо: комментарий объясняет «почему» (бизнес-ограничение)
// Округляем вниз, т.к. банк не принимает дробные копейки
BigDecimal rounded = amount.setScale(2, RoundingMode.FLOOR);
```

---

## Q25. Как автоматически обнаруживать code smells в Java-проекте?

| Инструмент | Что находит |
|------------|-------------|
| **SonarQube** | God classes, long methods, cyclomatic complexity, duplicate code |
| **PMD** | Long methods, data clumps, switch complexity, unused variables |
| **SpotBugs** | Потенциальные баги, связанные с плохим дизайном |
| **Checkstyle** | Нарушения стиля, которые часто коррелируют со смеллами |
| **IntelliJ IDEA** | Code inspections: dead code, feature envy, duplicate fragments |
| **JaCoCo** | Dead code через незакрытые ветки |

Конфигурация PMD в Maven:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <configuration>
        <rulesets>
            <ruleset>/rulesets/java/design.xml</ruleset>
            <ruleset>/rulesets/java/codesize.xml</ruleset>
        </rulesets>
    </configuration>
</plugin>
```

---

## Q26. Какие метрики кода связаны с code smells?

| Метрика | Связанный смелл |
|---------|----------------|
| **Cyclomatic Complexity** > 10 | Long Method, Switch Statements |
| **Lines of Code** (LOC) метода > 30 | Long Method |
| **Number of Parameters** > 4 | Long Parameter List |
| **Coupling Between Objects** (CBO) | Inappropriate Intimacy, Shotgun Surgery |
| **Lack of Cohesion of Methods** (LCOM) | God Class, Divergent Change |
| **Depth of Inheritance Tree** (DIT) > 4 | Refused Bequest, Parallel Hierarchies |
| **Code Duplication** % | Duplicate Code |

---

## Q27. Как приоритизировать устранение code smells?

Не все смеллы одинаково вредны. Приоритет устранения:

1. **Смеллы в часто меняемом коде** — там, где команда часто работает (hotspot анализ через `git log --stat`).
2. **Смеллы, блокирующие тестируемость** — длинные методы с множеством ветвлений.
3. **Дублирование в критической бизнес-логике** — один баг в копиях размножается.
4. **God Classes** — мешают параллельной разработке (merge-конфликты).
5. **Смеллы в стабильном, нетрогаемом коде** — низкий приоритет.

Подход: исправлять смелл при следующем касании файла (Boy Scout Rule — «оставь лагерь чище, чем нашёл»).

---

## See also

- [[code-review-interview|Code Review]]
- [[refactoring-patterns-interview|Refactoring Patterns]]
- [[technical-debt-interview|Technical Debt]]
- [[clean-code-practices-interview|Clean Code Practices]]
- [[static-analysis-interview|Static Analysis]]
- [[design-patterns-interview|Design Patterns]]
- [[java-core-interview|Java Core]]
