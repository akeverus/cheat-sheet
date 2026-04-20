---
title: "Принципы проектирования программного обеспечения"
description: "Основные принципы проектирования: SOLID, DRY, KISS, YAGNI и другие."
tags:
  - architecture
  - design-principles
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Принципы проектирования программного обеспечения

Основные принципы проектирования: `SOLID`, `DRY`, `KISS`, `YAGNI` и другие.

## Полезные ссылки

- [SOLID Principles](https://www.geeksforgeeks.org/solid-principle-in-programming-understand-with-real-life-examples/)
- [Clean Code (Robert C. Martin)](https://www.baeldung.com/cs/solid-principles)

## Содержание

- [SOLID принципы](#solid-принципы)
  - [Single Responsibility Principle (SRP)](#single-responsibility-principle-srp)
  - [Open/Closed Principle (OCP)](#openclosed-principle-ocp)
  - [Liskov Substitution Principle (LSP)](#liskov-substitution-principle-lsp)
  - [Interface Segregation Principle (ISP)](#interface-segregation-principle-isp)
  - [Dependency Inversion Principle (DIP)](#dependency-inversion-principle-dip)
- [DRY (Don't Repeat Yourself)](#dry-dont-repeat-yourself)
- [KISS (Keep It Simple, Stupid)](#kiss-keep-it-simple-stupid)
- [YAGNI (You Aren't Gonna Need It)](#yagni-you-arent-gonna-need-it)
- [Принцип наименьшего удивления](#принцип-наименьшего-удивления)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## **SOLID** принципы

### **Single Responsibility Principle** (**SRP**)
**Принцип единственной ответственности:** класс должен иметь только одну причину для изменения.

Ниже — пример **SRP** (**Java**).
```java
// ❌ Плохо: класс делает слишком много
class User {
    void save() { /* сохранение в БД */ }
    void sendEmail() { /* отправка email */ }
    void generateReport() { /* генерация отчёта */ }
}

// ✅ Хорошо: разделение ответственности
class User {
    void save() { /* сохранение в БД */ }
}

class EmailService {
    void sendEmail(User user) { /* отправка email */ }
}

class ReportGenerator {
    void generateReport(User user) { /* генерация отчёта */ }
}
```

### **Open**/**Closed Principle** (**OCP**)
**Принцип открытости/закрытости:** классы должны быть открыты для расширения, но закрыты для модификации.

```java
// ✅ Использование интерфейсов для расширяемости
interface PaymentProcessor {
    void processPayment(double amount);
}

class CreditCardProcessor implements PaymentProcessor {
    public void processPayment(double amount) {
        // обработка кредитной карты
    }
}

class PayPalProcessor implements PaymentProcessor {
    public void processPayment(double amount) {
        // обработка PayPal
    }
}

class PaymentService {
    private PaymentProcessor processor;

    PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }

    void pay(double amount) {
        processor.processPayment(amount);
    }
}
```

### **Liskov Substitution Principle** (**LSP**)
**Принцип подстановки Лисков:** объекты подклассов должны заменять объекты базового класса без нарушения функциональности.

```java
// ✅ Правильная иерархия
abstract class Bird {
    abstract void move();
}

class Sparrow extends Bird {
    void move() {
        fly(); // воробей летает
    }

    void fly() { /* полёт */ }
}

class Penguin extends Bird {
    void move() {
        swim(); // пингвин плавает
    }

    void swim() { /* плавание */ }
}
```

### **Interface Segregation Principle** (**ISP**)
**Принцип разделения интерфейсов:** клиенты не должны зависеть от интерфейсов, которые они не используют.

```java
// ❌ Плохо: один большой интерфейс
interface Worker {
    void work();
    void eat();
    void sleep();
}

// ✅ Хорошо: разделение интерфейсов
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Eatable, Sleepable {
    public void work() { /* работа */ }
    public void eat() { /* еда */ }
    public void sleep() { /* сон */ }
}

class Robot implements Workable {
    public void work() { /* работа */ }
    // не нужны eat() и sleep()
}
```

### **Dependency Inversion Principle** (**DIP**)
**Принцип инверсии зависимостей:** модули высокого уровня не должны зависеть от модулей низкого уровня; оба должны зависеть от абстракций.

```java
// ✅ Зависимость от абстракции
interface Database {
    void save(String data);
}

class MySQLDatabase implements Database {
    public void save(String data) {
        // сохранение в MySQL
    }
}

class PostgreSQLDatabase implements Database {
    public void save(String data) {
        // сохранение в PostgreSQL
    }
}

class UserService {
    private Database database; // зависимость от интерфейса

    UserService(Database database) {
        this.database = database;
    }

    void saveUser(String userData) {
        database.save(userData);
    }
}
```

## **DRY** (**Don't `Repeat` Yourself**)

**Не повторяйся:** каждая часть знания должна иметь единственное, однозначное представление в системе.

```java
// ❌ Плохо: дублирование кода
class OrderService {
    double calculateTotal(List<Item> items) {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total * 1.2; // налог 20%
    }
}

class InvoiceService {
    double calculateTotal(List<Item> items) {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total * 1.2; // налог 20%
    }
}

// ✅ Хорошо: вынесение общей логики
class TaxCalculator {
    static double calculateWithTax(double subtotal, double taxRate) {
        return subtotal * (1 + taxRate);
    }
}

class PriceCalculator {
    static double calculateSubtotal(List<Item> items) {
        return items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
    }
}

class OrderService {
    double calculateTotal(List<Item> items) {
        double subtotal = PriceCalculator.calculateSubtotal(items);
        return TaxCalculator.calculateWithTax(subtotal, 0.2);
    }
}
```

## **KISS** (**Keep `It Simple`, Stupid**)

**Делай проще:** простота должна быть ключевой целью проектирования.

```java
// ❌ Плохо: излишняя сложность
class ComplexCalculator {
    public double calculate(double a, double b, String operation) {
        Map<String, BiFunction<Double, Double, Double>> operations = new HashMap<>();
        operations.put("add", (x, y) -> x + y);
        operations.put("subtract", (x, y) -> x - y);
        // ... много кода

        return operations.getOrDefault(operation, (x, y) -> 0.0).apply(a, b);
    }
}

// ✅ Хорошо: простое решение
class SimpleCalculator {
    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }
}
```

## **YAGNI** (**You `Aren`'t `Gonna Need` It**)

**Тебе это не понадобится:** не добавляй функциональность, пока она действительно не нужна.

```java
// ❌ Плохо: преждевременная оптимизация
class UserService {
    // Добавлена поддержка множества форматов, хотя нужен только JSON
    void saveUser(User user, Format format) {
        if (format == Format.JSON) { /* ... */ }
        else if (format == Format.XML) { /* ... */ }
        else if (format == Format.YAML) { /* ... */ }
        // ... но используется только JSON
    }
}

// ✅ Хорошо: только то, что нужно сейчас
class UserService {
    void saveUser(User user) {
        // сохранение в JSON
    }
    // Добавим другие форматы, когда они понадобятся
}
```

## Принцип наименьшего удивления

Код должен работать так, как ожидает пользователь.

```java
// ✅ Понятное именование и поведение
class BankAccount {
    void withdraw(double amount) {
        if (amount > balance) {
            throw new InsufficientFundsException();
        }
        balance -= amount;
    }
}
```

## Лучшие практики

Рекомендации ниже помогают применять принципы проектирования без избыточной сложности:

- Применяйте **SOLID** по мере роста кодовой базы; не усложняйте простой код лишними абстракциями.
- **DRY** и **KISS**: дублирование устраняйте через выделение методов/классов, но не ценой нечитаемости.
- При сомнениях между расширением и модификацией — предпочитайте расширение (**OCP**).
- Иерархии наследования держите неглубокими; композиция часто лучше наследования (**LSP, предпочтение композиции**).
- Для новых решений фиксируйте решения в **ADR** (**Architectural `Decision` Records**).


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Класс меняется по разным причинам | Нарушение SRP | Выделить классы по одной ответственности |
| Код ломается при новых требованиях | Нарушение OCP | Абстракции, расширение через новые реализации |
| Подкласс ломает поведение | Нарушение LSP | Подтипы заменяемы; не ужесточать предусловия |
| Лишнее в интерфейсе | Нарушение ISP | Узкие интерфейсы |
| Зависимость от конкретики | Нарушение DIP | Абстракции, DI |

## Частые вопросы

**SOLID всегда?** По мере роста кодовой базы; не усложнять простой код.

**DRY vs дублирование?** DRY — один источник правды. Иногда копирование допустимо.

**YAGNI?** Не вводить абстракции «на будущее» без текущей потребности.
## См. также

- [[solid-principles|SOLID — подробный справочник]] — SRP, OCP, LSP, ISP, DIP с примерами на Java
- [[adr-template|Architectural Decision Records]] — шаблон ADR
- [Enterprise Patterns](../enterprise-patterns/) — обзор enterprise-паттернов
- [Design Patterns](../) — корневой раздел архитектуры

