---
title: "Calculator Implementation"
description: "A guide to implementing a basic calculator in Java that supports addition, subtraction, multiplication, and division operations."
tags: ["algorithms", "problems", "calculator-implementation"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Calculator Implementation

A **guide** to **implementing** a **basic calculator** in **Java that supports addition**, **subtraction**, **multiplication**, **and division operations**.

**Дата последнего обновления:** 2026-02-06



## Полезные ссылки

### Официальная документация
- [`Scanner` (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html)
- [Switch Statement (Java Tutorial)](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html)

### См. также
- [Математические алгоритмы](../math/README.md) — раздел math
- [Алгоритмы со строками](../strings/README.md) — раздел strings

## Содержание

- [Calculator Implementation](#calculator-implementation)
- [Обзор](#обзор)
- [Displaying Calculator Information](#displaying-calculator-information)
- [Reading User Input](#reading-user-input)
- [Input Validation](#input-validation)
  - [Invalid Operator Check](#invalid-operator-check)
  - [Division by Zero Check](#division-by-zero-check)
- [Java Implementation](#java-implementation)
  - [Calculation Logic](#calculation-logic)
    - [Using If-Else](#using-if-else)
    - [Using Switch Statement](#using-switch-statement)
  - [Complete Implementation](#complete-implementation)
  - [Alternative Implementation with Method](#alternative-implementation-with-method)
- [Kotlin Implementation](#kotlin-implementation)
  - [Basic Calculator](#basic-calculator)
- [Data Type Considerations](#data-type-considerations)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Обзор

In **this tutorial**, we'll **implement** a **basic calculator** in **Java that supports addition**, **subtraction**, **multiplication**, **and division operations**. We'll **also take the operator and operands** as **input and process calculations based** on **them**.

## Displaying Calculator Information

**First**, **let**'s **show some information about the calculator**:**

```java
// Вывод приветствия и списка поддерживаемых операций калькулятора
System.out.println("----------------------------------------\n" +
                   "Welcome to Basic Calculator\n" +
                   "----------------------------------------");
System.out.println("Following operations are supported:\n" +
                   "1. Addition(+)\n" +
                   "2. Subtraction(-)\n" +
                   "3. Multiplication(*)\n" +
                   "4. Division(/)\n");
```

## Reading User Input

**Now let**'s **use** `**java.util.Scanner**` to **read user input**:**

```java
Scanner scanner = new Scanner(System.in);

System.out.println("Enter an operator: (+ OR - OR * OR /) ");
char operation = scanner.next().charAt(0);

System.out.println("Enter the first number: ");
double num1 = scanner.nextDouble();

System.out.println("Enter the second number: ");
double num2 = scanner.nextDouble();
```

## Input Validation

**Since** we're **taking input into the system**, we **need** to **validate** it. **For example**, if **the operator** is **not** +, -, *, or /, **then our calculator should indicate invalid input**. **Similarly**, if we **enter the second number** `as 0` **for** a **division operation**, **the results won**'t be **good**.

So, **let**'s **implement these checks**.

### Invalid Operator Check

**First**, **let**'s **focus** on **the situation when the operator** is **invalid**:**

```java
if (!(operation == '+' || operation == '-' || operation == '*' || operation == '/')) {
    System.err.println("Invalid Operator. Please use only + or - or * or /");
    return;
}
```

### Division by Zero Check

**Then** we **can show errors for invalid operations**:**

```java
if (operation == '/' && num2 == 0.0) {
    System.err.println("The second number cannot be zero for division operation.");
    return;
}
```

**User inputs are validated first**. **After that**, **the calculation result will** be **displayed** in **the format**:**

```text
<number1> <operation> <number2> = <result>
```

## Java Implementation

### Calculation Logic

#### Using If-Else

**First**, we **can use** an **if-else construct** to **handle calculations**:**

```java
if (operation == '+') {
    System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
} else if (operation == '-') {
    System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
} else if (operation == '*') {
    System.out.println(num1 + " x " + num2 + " = " + (num1 * num2));
} else if (operation == '/') {
    System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
} else {
    System.err.println("Invalid Operator Specified.");
}
```

#### Using Switch Statement

**Similarly**, we **can use the Java switch statement**:**

```java
switch (operation) {
    case '+':
        System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
        break;
    case '-':
        System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
        break;
    case '*':
        System.out.println(num1 + " x " + num2 + " = " + (num1 * num2));
        break;
    case '/':
        System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
        break;
    default:
        System.err.println("Invalid Operator Specified.");
        break;
}
```

We **can use** a **variable** to **store the calculation results**. `As a` **result**, it **can** be **printed** at **the end**. In **this case**, `**System.out.println**` **will** be **used only once**.

### Complete Implementation

**Here**'s a **complete implementation**:**

```java
import java.util.Scanner;

public class BasicCalculator {
    
    public static void main(String[] args) {
        // Display calculator information
        System.out.println("----------------------------------------\n" +
                           "Welcome to Basic Calculator\n" +
                           "----------------------------------------");
        System.out.println("Following operations are supported:\n" +
                           "1. Addition(+)\n" +
                           "2. Subtraction(-)\n" +
                           "3. Multiplication(*)\n" +
                           "4. Division(/)\n");
        
        // Read user input
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter an operator: (+ OR - OR * OR /) ");
        char operation = scanner.next().charAt(0);
        
        System.out.println("Enter the first number: ");
        double num1 = scanner.nextDouble();
        
        System.out.println("Enter the second number: ");
        double num2 = scanner.nextDouble();
        
        // Validate input
        if (!(operation == '+' || operation == '-' || operation == '*' || operation == '/')) {
            System.err.println("Invalid Operator. Please use only + or - or * or /");
            scanner.close();
            return;
        }
        
        if (operation == '/' && num2 == 0.0) {
            System.err.println("The second number cannot be zero for division operation.");
            scanner.close();
            return;
        }
        
        // Perform calculation
        double result = 0.0;
        
        switch (operation) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                result = num1 / num2;
                break;
        }
        
        // Display result
        System.out.println(num1 + " " + operation + " " + num2 + " = " + result);
        
        scanner.close();
    }
}
```

### Alternative Implementation with Method

```java
import java.util.Scanner;

public class BasicCalculator {
    
    public static void main(String[] args) {
        displayWelcomeMessage();
        
        Scanner scanner = new Scanner(System.in);
        char operation = getOperator(scanner);
        double num1 = getNumber(scanner, "first");
        double num2 = getNumber(scanner, "second");
        
        if (!isValidOperator(operation)) {
            System.err.println("Invalid Operator. Please use only + or - or * or /");
            scanner.close();
            return;
        }
        
        if (operation == '/' && num2 == 0.0) {
            System.err.println("The second number cannot be zero for division operation.");
            scanner.close();
            return;
        }
        
        double result = calculate(operation, num1, num2);
        System.out.println(num1 + " " + operation + " " + num2 + " = " + result);
        
        scanner.close();
    }
    
    private static void displayWelcomeMessage() {
        System.out.println("----------------------------------------\n" +
                           "Welcome to Basic Calculator\n" +
                           "----------------------------------------");
        System.out.println("Following operations are supported:\n" +
                           "1. Addition(+)\n" +
                           "2. Subtraction(-)\n" +
                           "3. Multiplication(*)\n" +
                           "4. Division(/)\n");
    }
    
    private static char getOperator(Scanner scanner) {
        System.out.println("Enter an operator: (+ OR - OR * OR /) ");
        return scanner.next().charAt(0);
    }
    
    private static double getNumber(Scanner scanner, String position) {
        System.out.println("Enter the " + position + " number: ");
        return scanner.nextDouble();
    }
    
    private static boolean isValidOperator(char operation) {
        return operation == '+' || operation == '-' || 
               operation == '*' || operation == '/';
    }
    
    private static double calculate(char operation, double num1, double num2) {
        switch (operation) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operation);
        }
    }
}
```

## Kotlin Implementation

### Basic Calculator

```kotlin
import java.util.Scanner

class CalculatorK {
    private val scanner = Scanner(System.`in`)
    
    fun displayInfo() {
        println("----------------------------------------")
        println("Welcome to Basic Calculator")
        println("----------------------------------------")
        println("Following operations are supported:")
        println("1. Addition(+)")
        println("2. Subtraction(-)")
        println("3. Multiplication(*)")
        println("4. Division(/)")
        println()
    }
    
    fun readInput(): Triple<Char, Double, Double> {
        print("Enter an operator: (+ OR - OR * OR /) ")
        val operation = scanner.next()[0]
        
        print("Enter the first number: ")
        val num1 = scanner.nextDouble()
        
        print("Enter the second number: ")
        val num2 = scanner.nextDouble()
        
        return Triple(operation, num1, num2)
    }
    
    fun validateInput(operation: Char, num2: Double): Boolean {
        if (operation !in listOf('+', '-', '*', '/')) {
            System.err.println("Invalid Operator. Please use only + or - or * or /")
            return false
        }
        
        if (operation == '/' && num2 == 0.0) {
            System.err.println("The second number cannot be zero for division operation.")
            return false
        }
        
        return true
    }
    
    fun calculate(operation: Char, num1: Double, num2: Double): Double {
        return when (operation) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> num1 / num2
            else -> throw IllegalArgumentException("Invalid operator: $operation")
        }
    }
    
    fun run() {
        displayInfo()
        val (operation, num1, num2) = readInput()
        
        if (!validateInput(operation, num2)) {
            return
        }
        
        val result = calculate(operation, num1, num2)
        println("$num1 $operation $num2 = $result")
    }
}

fun main() {
    val calculator = CalculatorK()
    calculator.run()
}
```

### Using If-Else

```kotlin
fun calculateWithIfElse(operation: Char, num1: Double, num2: Double): Double {
    return if (operation == '+') {
        num1 + num2
    } else if (operation == '-') {
        num1 - num2
    } else if (operation == '*') {
        num1 * num2
    } else if (operation == '/') {
        num1 / num2
    } else {
        throw IllegalArgumentException("Invalid operator: $operation")
    }
}
```

## Data Type Considerations

**Additionally**, **the maximum range for calculations** `is 2147483647`. **Therefore**, if we **exceed** it, **overflow will occur from the int data type**. `So it` **should** be **stored** in a **variable** of a **larger data type**, **such** as **the double data type**.

**Using** `**double**` **instead** of `**int**` **provides**:**
- **Larger range** of **values**
- **Support for decimal numbers**
- **Prevention** of **integer overflow**
- **More accurate results for division operations**

## Лучшие практики

Проверяйте оператор (+, -, *, /) и деление на ноль до вычислений; при некорректном вводе выводите сообщение и запрашивайте повторный ввод. Используйте `double` для операндов и результата, чтобы поддерживать дроби и избегать целочисленного переполнения; для финансов рассмотрите `BigDecimal`. Выносите вычисление в отдельный метод; используйте switch или enum для операций — это упрощает расширение и тестирование. Не допускайте падения по исключению при неверном вводе; валидируйте через проверки и сообщайте пользователю. В тестах покройте все операции, деление на ноль, неверный оператор и граничные значения.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Неверный результат при делении | Целочисленное деление | Использовать `double` для операндов и результата |
| Исключение при вводе | Нечисловой ввод или неверный оператор | Валидировать до вычислений; try/catch с сообщением пользователю |
| Переполнение при больших числах | Тип `int` или ограничения `double` | Использовать `double`; для точной арифметики — `BigDecimal` |

## Частые вопросы

**Нужна ли валидация ввода?** Да. Проверяйте оператор (допустимые +, -, *, /), деление на ноль и при необходимости формат чисел; иначе возможны исключения или неверный результат.

**Когда использовать BigDecimal?** Для финансов и там, где важна точная десятичная арифметика; для учебного калькулятора достаточно `double`.

**Как тестировать калькулятор?** Покройте все четыре операции, деление на ноль, неверный оператор, пустой ввод и граничные значения (большие числа, отрицательные).

## Заключение

В этом документе реализован простой калькулятор на `Java` с использованием двух подходов: `if-else` и `switch`. Оба варианта включают валидацию ввода перед выполнением операций, что предотвращает ошибки при некорректных данных.
