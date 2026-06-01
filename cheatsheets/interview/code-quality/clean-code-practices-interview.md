---
title: "Вопросы на собеседовании: Clean Code Practices"
description: "Принципы чистого кода: именование, SOLID, DRY/KISS/YAGNI, функции, комментарии, форматирование по Роберту Мартину и практика инженеров"
tags:
  - interview
  - code-quality
  - clean-code-practices-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Clean Code Practices"
  - "Clean Code interview"
  - "Clean Code собеседование"
prerequisites:
  - "[[clean-code]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: Clean Code Practices

Краткие ответы про практики чистого кода: именование, функции, принципы SOLID, DRY/KISS/YAGNI, комментарии и форматирование — по книге Роберта Мартина и современной практике.

## Содержание

- [See also](#see-also)

**Основы**
- [Q1. Что такое «чистый код»? Как его определяют известные авторы?](#q1-что-такое-чистый-код-как-его-определяют-известные-авторы)
- [Q2. Почему чистый код важен с практической точки зрения?](#q2-почему-чистый-код-важен-с-практической-точки-зрения)

**Именование**
- [Q3. Какие правила именования переменных и методов?](#q3-какие-правила-именования-переменных-и-методов)
- [Q4. Что такое «говорящие имена» и почему важно избегать сокращений?](#q4-что-такое-говорящие-имена-и-почему-важно-избегать-сокращений)
- [Q5. Как именовать булевы переменные и методы?](#q5-как-именовать-булевы-переменные-и-методы)
- [Q6. Какие антипаттерны именования встречаются чаще всего?](#q6-какие-антипаттерны-именования-встречаются-чаще-всего)

**Функции и методы**
- [Q7. Какими должны быть функции по Роберту Мартину?](#q7-какими-должны-быть-функции-по-роберту-мартину)
- [Q8. Что такое уровень абстракции метода и почему его нужно выдерживать?](#q8-что-такое-уровень-абстракции-метода-и-почему-его-нужно-выдерживать)
- [Q9. Что такое Command Query Separation (CQS)?](#q9-что-такое-command-query-separation-cqs)
- [Q10. Почему флаги-параметры (boolean flag) — плохая практика?](#q10-почему-флаги-параметры-boolean-flag--плохая-практика)

**Принципы SOLID**
- [Q11. Что такое принцип единственной ответственности (SRP)?](#q11-что-такое-принцип-единственной-ответственности-srp)
- [Q12. Что такое принцип открытости/закрытости (OCP)?](#q12-что-такое-принцип-открытостизакрытости-ocp)
- [Q13. Что такое принцип подстановки Лисков (LSP)?](#q13-что-такое-принцип-подстановки-лисков-lsp)
- [Q14. Что такое принцип разделения интерфейсов (ISP)?](#q14-что-такое-принцип-разделения-интерфейсов-isp)
- [Q15. Что такое принцип инверсии зависимостей (DIP)?](#q15-что-такое-принцип-инверсии-зависимостей-dip)

**DRY / KISS / YAGNI**
- [Q16. Что такое DRY (Don't Repeat Yourself)?](#q16-что-такое-dry-dont-repeat-yourself)
- [Q17. Что такое KISS (Keep It Simple, Stupid)?](#q17-что-такое-kiss-keep-it-simple-stupid)
- [Q18. Что такое YAGNI (You Aren't Gonna Need It)?](#q18-что-такое-yagni-you-arent-gonna-need-it)
- [Q19. Когда DRY нарушается оправданно?](#q19-когда-dry-нарушается-оправданно)

**Комментарии**
- [Q20. Когда комментарии в коде уместны, а когда вредны?](#q20-когда-комментарии-в-коде-уместны-а-когда-вредны)
- [Q21. Что такое «самодокументируемый код»?](#q21-что-такое-самодокументируемый-код)

**Форматирование**
- [Q22. Почему форматирование кода важно для читаемости?](#q22-почему-форматирование-кода-важно-для-читаемости)
- [Q23. Что такое «вертикальная плотность» и «вертикальное расстояние» в Clean Code?](#q23-что-такое-вертикальная-плотность-и-вертикальное-расстояние-в-clean-code)

**Обработка ошибок**
- [Q24. Как правильно обрабатывать ошибки в духе Clean Code?](#q24-как-правильно-обрабатывать-ошибки-в-духе-clean-code)
- [Q25. Почему возврат null — плохая практика?](#q25-почему-возврат-null--плохая-практика)

**Практика**
- [Q26. Что такое Boy Scout Rule в контексте Clean Code?](#q26-что-такое-boy-scout-rule-в-контексте-clean-code)
- [Q27. Как балансировать чистоту кода и сроки (deadline)?](#q27-как-балансировать-чистоту-кода-и-сроки-deadline)

---

## Q1. Что такое «чистый код»? Как его определяют известные авторы?

Что такое «чистый код»? Как его определяют известные авторы?

**Чистый код** — код, который легко читать, понимать и изменять.

Определения:
- **Роберт Мартин (Uncle Bob):** «Чистый код — это код, который заботится о деталях. Он читается как хорошо написанная проза».
- **Bjarne Stroustrup:** «Чистый код делает одно дело хорошо».
- **Ward Cunningham:** «Чистый код — это код, прочитав который, вы улыбаетесь и думаете: "Именно так я бы и написал"».

Главная идея: код пишется один раз, но читается многократно. Читаемость — приоритет.

## Q2. Почему чистый код важен с практической точки зрения?

- **Скорость разработки:** грязный код замедляет команду с каждым месяцем — нужно больше времени чтобы понять что происходит.
- **Баги:** запутанный код скрывает дефекты, чистый — делает их очевидными.
- **Onboarding:** новые члены команды быстрее вникают в чистый код.
- **Тестируемость:** чистые функции с чёткими ответственностями легко тестировать изолированно.
- **Рефакторинг:** чистый код проще изменить без страха сломать что-то ещё.

## Q3. Какие правила именования переменных и методов?

**Переменные:**
- Имя должно отвечать на вопрос: зачем существует, что делает, как используется.
- Избегать однобуквенных имён (кроме счётчиков циклов `i`, `j`).
- Не использовать сокращения, кроме общепринятых (`id`, `url`, `dto`).

**Методы:**
- Методы — глаголы или глагольные фразы: `getUserById`, `calculateTax`, `isValid`.
- Предпочитать длинное описательное имя короткому непонятному.

```java
// Плохо
int d;  // elapsed time in days?
List<int[]> theList;

// Хорошо
int elapsedTimeInDays;
List<Cell> gameBoard;
```

## Q4. Что такое «говорящие имена» и почему важно избегать сокращений?

**Говорящее имя** — имя, из которого сразу понятно намерение без контекста.

```java
// Сокращения создают когнитивную нагрузку
int cnt;  // count чего? заказов? ошибок?
String addr; // address? adder? admin?

// Говорящие имена — читаются как документация
int activeOrderCount;
String userEmailAddress;

// Разница в реальном коде
// Непонятно:
if (emp.seniority() > 5 && emp.type() == 1) { ... }
// Понятно:
if (employee.hasSufficientExperience() && employee.isFullTime()) { ... }
```

## Q5. Как именовать булевы переменные и методы?

Булевы значения должны читаться как утверждение:
- Переменные: `isActive`, `hasPermission`, `canEdit`, `wasProcessed`.
- Методы: `isEmpty()`, `isValid()`, `hasErrors()`, `canAccess()`.

```java
// Плохо: неясно что значит true/false
boolean flag;
boolean status;
boolean check();

// Хорошо
boolean isEmailVerified;
boolean hasActiveSubscription;
boolean isEligibleForDiscount();
```

## Q6. Какие антипаттерны именования встречаются чаще всего?

| Антипаттерн | Пример | Проблема |
|-------------|--------|---------|
| Дезинформирующее имя | `accountList` (а это `Map`) | Несоответствие типу |
| Шум-слова | `theData`, `aVariable`, `InfoData` | Пустые слова не добавляют смысла |
| Числовые суффиксы | `getUser1()`, `getUser2()` | Сигнал о дублировании |
| Венгерская нотация | `strName`, `iCount` | IDE и так знает тип |
| Слишком общие | `Manager`, `Processor`, `Helper` | Нет конкретики |
| Аббревиатуры | `CustMgr`, `SvcFct` | Трудночитаемы |

## Q7. Какими должны быть функции по Роберту Мартину?

1. **Маленькие** — умещаться на экране (10–20 строк максимум).
2. **Делать одно** — если метод делает несколько вещей, его можно разбить на более мелкие.
3. **Один уровень абстракции** — не мешать высокоуровневую логику с низкоуровневой.
4. **Минимум аргументов** — идеально 0–2, 3 — нежелательно, 4+ — ищем Parameter Object.
5. **Без side effects** — если функция называется `checkPassword`, она не должна инициализировать сессию.
6. **Не повторять себя** — DRY.

```java
// Нарушение: один метод делает несколько вещей
void processUserRegistration(String email, String password) {
    // валидация
    if (!email.contains("@")) throw new InvalidEmailException();
    if (password.length() < 8) throw new WeakPasswordException();
    // сохранение
    User user = new User(email, hashPassword(password));
    userRepo.save(user);
    // уведомление
    emailService.sendWelcome(email);
    // аудит
    auditLog.record("User registered: " + email);
}

// Лучше: разбить на шаги
void processUserRegistration(String email, String password) {
    validateCredentials(email, password);
    User user = createUser(email, password);
    notifyUser(user);
    auditRegistration(user);
}
```

## Q8. Что такое уровень абстракции метода и почему его нужно выдерживать?

Каждая функция должна работать на **одном уровне абстракции**. Смешение высокоуровневых операций с деталями реализации — нарушение.

```java
// Нарушение: смешение уровней
void prepareReport() {
    // Высокий уровень
    List<Order> orders = getOrders();
    // Низкий уровень (детали SQL-like логики)
    orders.stream()
          .filter(o -> o.getStatus().equals("COMPLETED") && o.getTotal().compareTo(BigDecimal.ZERO) > 0)
          .forEach(o -> System.out.println(o.getId() + "," + o.getTotal()));
}

// Хорошо: единый уровень абстракции
void prepareReport() {
    List<Order> completedOrders = getCompletedOrders();
    printOrdersCsv(completedOrders);
}
```

## Q9. Что такое Command Query Separation (CQS)?

**CQS** (Бертран Мейер): метод должен быть либо **командой** (изменяет состояние, ничего не возвращает), либо **запросом** (возвращает данные, не изменяет состояние) — но не тем и другим одновременно.

```java
// Нарушение CQS: метод одновременно изменяет и возвращает
int incrementAndGet(Counter counter) {
    counter.increment();  // side effect
    return counter.getValue();  // query
}

// Соблюдение CQS
void increment(Counter counter) { counter.increment(); }  // command
int getValue(Counter counter) { return counter.getValue(); }  // query
```

Исключения: `Stack.pop()`, `Queue.poll()` — намеренные нарушения ради удобства.

## Q10. Почему флаги-параметры (boolean flag) — плохая практика?

Булев параметр сигнализирует, что функция делает два дела в зависимости от значения флага — нарушение правила «одной задачи».

```java
// Плохо: что значит true/false без контекста?
render(true);
renderHtml(false);

// Хорошо: два разных метода
renderForMobile();
renderForDesktop();

// Или через enum
render(RenderMode.MOBILE);
render(RenderMode.DESKTOP);
```

## Q11. Что такое принцип единственной ответственности (SRP)?

**SRP (Single Responsibility Principle):** класс должен иметь только **одну причину для изменения**.

Роберт Мартин уточняет: «У модуля должен быть один и только один актор (stakeholder), которому она служит».

```java
// Нарушение: класс изменяется при изменении бизнес-правил, БД-схемы, формата отчёта
class Employee {
    void calculatePay() { ... }    // финансовый отдел
    void saveToDatabase() { ... }  // DBA
    void generateReport() { ... }  // HR
}

// Соблюдение: три класса, три причины изменения
class PayrollCalculator { void calculatePay(Employee e) { ... } }
class EmployeeRepository { void save(Employee e) { ... } }
class EmployeeReportGenerator { void generate(Employee e) { ... } }
```

## Q12. Что такое принцип открытости/закрытости (OCP)?

**OCP (Open/Closed Principle):** класс должен быть **открыт для расширения**, но **закрыт для модификации**.

Добавление новой функциональности — через наследование или композицию, а не через изменение существующего кода.

```java
// Нарушение: каждый новый тип скидки требует изменения метода
BigDecimal calculateDiscount(Order order, String discountType) {
    return switch (discountType) {
        case "SEASONAL" -> order.getTotal().multiply(new BigDecimal("0.10"));
        case "LOYALTY" -> order.getTotal().multiply(new BigDecimal("0.15"));
        // при добавлении нового типа — правим этот метод
        default -> BigDecimal.ZERO;
    };
}

// Соблюдение OCP: новый тип скидки — новый класс, старый код не трогаем
interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
class SeasonalDiscount implements DiscountStrategy { ... }
class LoyaltyDiscount implements DiscountStrategy { ... }
```

## Q13. Что такое принцип подстановки Лисков (LSP)?

**LSP (Liskov Substitution Principle):** объекты подтипа должны быть заменяемы объектами базового типа без нарушения корректности программы.

Нарушение LSP: подкласс ужесточает предусловия, ослабляет постусловия или бросает новые исключения.

```java
// Нарушение LSP: Square ведёт себя не как Rectangle
class Rectangle {
    void setWidth(int w) { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    void setWidth(int w) { this.width = w; this.height = w; }  // неожиданный side effect
    @Override
    void setHeight(int h) { this.height = h; this.width = h; }
}

// Код, работающий с Rectangle, ломается при подстановке Square
void testArea(Rectangle r) {
    r.setWidth(5); r.setHeight(4);
    assert r.area() == 20;  // для Square вернёт 16!
}
```

## Q14. Что такое принцип разделения интерфейсов (ISP)?

**ISP (Interface Segregation Principle):** клиент не должен зависеть от методов, которые он не использует. Предпочитать узкие интерфейсы «толстым».

```java
// Нарушение ISP: интерфейс слишком широк
interface Worker {
    void work();
    void eat();
    void sleep();
}

// Робот реализует Worker, но не ест и не спит — вынужден имплементировать пустые методы
class Robot implements Worker {
    public void work() { ... }
    public void eat() { throw new UnsupportedOperationException(); }
    public void sleep() { throw new UnsupportedOperationException(); }
}

// Соблюдение ISP: разбить на роли
interface Workable { void work(); }
interface Eatable { void eat(); }
class Human implements Workable, Eatable { ... }
class Robot implements Workable { ... }
```

## Q15. Что такое принцип инверсии зависимостей (DIP)?

**DIP (Dependency Inversion Principle):**
- Модули высокого уровня не должны зависеть от модулей низкого уровня. Оба должны зависеть от **абстракций**.
- Абстракции не должны зависеть от деталей. Детали должны зависеть от абстракций.

```java
// Нарушение DIP: high-level зависит от конкретного класса
class OrderService {
    private MySqlOrderRepository repository = new MySqlOrderRepository();  // конкретика
}

// Соблюдение DIP: зависимость через интерфейс + DI
class OrderService {
    private final OrderRepository repository;  // абстракция

    OrderService(OrderRepository repository) {  // DI через конструктор
        this.repository = repository;
    }
}
```

DIP — основа Dependency Injection и инверсии управления (IoC).

## Q16. Что такое DRY (Don't Repeat Yourself)?

**DRY:** «Каждый фрагмент знания должен иметь единственное, однозначное, авторитетное представление в системе» (Эндрю Хант, Дэвид Томас, «Pragmatic Programmer»).

Нарушение DRY — дублирование не только кода, но и знания: если одно бизнес-правило описано в нескольких местах, изменение требует синхронизации всех копий.

```java
// Нарушение DRY: константа скидки задублирована
class OrderService {
    BigDecimal discount = order.getTotal().multiply(new BigDecimal("0.10"));
}
class CartService {
    BigDecimal discount = price.multiply(new BigDecimal("0.10")); // дублирование знания
}

// DRY: единственный источник истины
class DiscountPolicy {
    static final BigDecimal STANDARD_RATE = new BigDecimal("0.10");
}
```

## Q17. Что такое KISS (Keep It Simple, Stupid)?

**KISS:** простое решение лучше сложного. Не вводить сложность без явной необходимости.

Проявления нарушения KISS:
- Паттерны там, где хватило бы прямого кода.
- Преждевременные абстракции.
- «Умные» однострочники вместо понятного кода.

```java
// Нарушение KISS: «умно», но нечитаемо
int result = IntStream.range(0, n)
    .reduce(1, (acc, i) -> acc * (i + 1));

// KISS: просто и понятно
int factorial = 1;
for (int i = 2; i <= n; i++) {
    factorial *= i;
}
```

## Q18. Что такое YAGNI (You Aren't Gonna Need It)?

**YAGNI** (из экстремального программирования, Кент Бек): не добавляй функциональность, пока она не нужна прямо сейчас.

Проявления нарушения YAGNI:
- «Сделаю параметризованным на случай будущего».
- Абстрактный базовый класс без реальных наследников.
- Флаги конфигурации, которые никогда не будут использоваться.

Связь с XP: в agile среде с частыми итерациями проще добавить функциональность, когда она нужна, чем поддерживать ненужную.

## Q19. Когда DRY нарушается оправданно?

DRY — принцип, а не закон. Иногда небольшое дублирование лучше:

- **Случайное сходство:** два фрагмента кода похожи сейчас, но меняются по разным причинам. Слияние создаст ненужную связность.
- **Разные контексты:** валидация в domain-слое и в UI-слое похожа, но живёт отдельно намеренно.
- **Читаемость:** иногда inline-код читается лучше, чем вызов утилиты ради 1 строки.

Rule of Three (Мартин Фаулер): дублирование первый раз — окей, второй — отметить, третий — рефакторинг.

## Q20. Когда комментарии в коде уместны, а когда вредны?

**Уместные комментарии:**
- Объяснение «почему» (бизнес-ограничение, нестандартное решение).
- TODO с конкретным контекстом.
- Javadoc для публичного API.
- Предупреждение о последствиях изменения.

**Вредные комментарии:**
- Объяснение «что» делает очевидный код.
- Закомментированный код — использовать git вместо этого.
- Устаревшие комментарии, противоречащие коду.
- Шум: `// constructor`, `// getters`.

```java
// Вредный комментарий: объясняет очевидное
i++; // increment i

// Вредный: закомментированный код
// oldMethod(param1, param2);

// Полезный: объясняет «почему»
// Округляем вниз — требование банка, см. ticket JIRA-1234
BigDecimal amount = total.setScale(2, RoundingMode.FLOOR);
```

## Q21. Что такое «самодокументируемый код»?

Код, который настолько выразительно написан, что не требует пояснительных комментариев.

Инструменты самодокументирования:
- Говорящие имена переменных и методов.
- Выделение намерения в отдельный метод с говорящим именем.
- Константы вместо магических чисел.
- Value Objects вместо примитивов.

```java
// Требует комментария
// Проверяем, достаточно ли у пользователя баллов для покупки (нужно >= 100)
if (user.points >= 100) { ... }

// Самодокументируется
if (user.hasEnoughPointsForPurchase()) { ... }
// или
static final int MIN_POINTS_FOR_PURCHASE = 100;
if (user.getPoints() >= MIN_POINTS_FOR_PURCHASE) { ... }
```

## Q22. Почему форматирование кода важно для читаемости?

Мартин Фаулер: «Форматирование — это коммуникация».

- **Консистентность** снижает когнитивную нагрузку: не нужно адаптироваться к стилю каждого файла.
- **Форматирование = структура:** пустые строки разделяют концепции, отступы показывают вложенность.
- **Автоматизация:** Google Java Format, Checkstyle, IDE-форматтеры — форматирование не должно обсуждаться в code review.

```java
// Плохо: всё слеплено
class OrderProcessor{void process(Order o){validate(o);save(o);notify(o);}}

// Хорошо: пространство делает структуру очевидной
class OrderProcessor {

    void process(Order order) {
        validate(order);
        save(order);
        notify(order);
    }
}
```

## Q23. Что такое «вертикальная плотность» и «вертикальное расстояние» в Clean Code?

**Вертикальная плотность:** связанные строки кода должны быть рядом. Не разделять переменную и её использование ненужным кодом.

**Вертикальное расстояние:** несвязанные концепции разделяются пустыми строками. Разные уровни функций — в разных частях класса.

```java
// Плохо: объявление и использование разделены несвязанным кодом
int timeout = 5000;
String endpoint = buildEndpoint();
log.debug("Connecting...");
int retries = 3;
connect(endpoint, timeout, retries);  // timeout и retries далеко от connect

// Хорошо: связанное — рядом
String endpoint = buildEndpoint();
int timeout = 5000;
int retries = 3;
connect(endpoint, timeout, retries);
```

## Q24. Как правильно обрабатывать ошибки в духе Clean Code?

- **Исключения вместо кодов ошибок** — избавляет от if-else цепочек проверок.
- **Не подавлять исключения** — пустой `catch` хуже crash.
- **Не возвращать null** — использовать `Optional`, выбрасывать исключение или возвращать пустую коллекцию.
- **Информативные сообщения** — исключение должно содержать контекст операции.

```java
// Плохо: подавление исключения
try {
    processOrder(order);
} catch (Exception e) {
    // игнорируем
}

// Плохо: код ошибки
int result = processOrder(order);
if (result == -1) { ... }

// Хорошо: специфическое исключение с контекстом
try {
    processOrder(order);
} catch (PaymentFailedException e) {
    log.error("Payment failed for order {}: {}", order.getId(), e.getMessage());
    throw new OrderProcessingException("Cannot process order " + order.getId(), e);
}
```

## Q25. Почему возврат null — плохая практика?

- Требует проверки `!= null` в каждом вызывающем коде.
- Легко пропустить проверку → NullPointerException в рантайме.
- Null не несёт семантики: это «не найдено», «ошибка» или «не инициализировано»?

**Альтернативы:**
- `Optional<T>` для «может быть не найдено».
- Пустая коллекция `Collections.emptyList()` вместо null-коллекции.
- Null Object Pattern — объект с поведением по умолчанию.
- Специфическое исключение, если отсутствие — нештатная ситуация.

```java
// Плохо
User findUser(Long id) {
    return userMap.get(id);  // null если не найдено
}

// Хорошо: явная семантика
Optional<User> findUser(Long id) {
    return Optional.ofNullable(userMap.get(id));
}

// Использование
findUser(id)
    .map(User::getEmail)
    .orElse("unknown@example.com");
```

## Q26. Что такое Boy Scout Rule в контексте Clean Code?

**Boy Scout Rule** (Роберт Мартин, от правила бойскаутов): «Оставь лагерь чище, чем нашёл».

В применении к коду: при каждом касании файла оставлять его немного лучше, чем он был. Переименовать непонятную переменную, вынести длинный метод, удалить мёртвый код.

Не нужно рефакторить весь модуль — достаточно маленьких улучшений при каждом PR. Со временем кодовая база постепенно становится чище.

## Q27. Как балансировать чистоту кода и сроки (deadline)?

Честный ответ: **грязный код под дедлайном — ложная экономия**.

- Грязный код замедляет следующие итерации: команда платит «процентами» за технический долг.
- Писать чисто с опытом занимает не намного дольше, чем писать грязно.
- Если есть сильное давление — договориться о техническом долге явно: создать задачу, поставить в бэклог.

Практический баланс:
- Не делать идеально то, что нужно сделать хорошо.
- Не делать хорошо то, что не нужно делать вообще (YAGNI).
- Применять Boy Scout Rule — небольшие улучшения без риска.
- Отдельная задача на рефакторинг после дедлайна, если накопился серьёзный долг.

## See also

- [Code Smells](code-smells-interview.md)
- [Refactoring Patterns](refactoring-patterns-interview.md)
- [Technical Debt](technical-debt-interview.md)
- [Static Analysis](static-analysis-interview.md)
- [Design Patterns](../design-patterns/design-patterns-interview.md)
- [Java Core](../programming-languages/java/java-core-interview.md)
