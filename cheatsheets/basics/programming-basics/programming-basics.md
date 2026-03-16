---
title: "Основы программирования"
description: "Кратко: Базовые концепции программирования - от алгоритмов и структур данных до парадигм программирования с практическими примерами на Java."
tags: ["basics", "programming-basics"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Основы программирования

Кратко: Базовые концепции программирования - от алгоритмов и структур данных до парадигм программирования с практическими примерами на **Java**.

**Дата последнего обновления:** 2026-02-11

## Полезные ссылки

### Официальная документация
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se21/html/) — спецификация языка **Java**

### Ресурсы
- [Java Programming Basics](https://www.baeldung.com/java-tutorial) — основы **Java** программирования

### См. также
- [Computer Science](../computer-science/computer-science-basics.md)
- [Алгоритмы и структуры данных](../../algorithms/)

## Содержание

- [Введение в программирование](#введение-в-программирование)
  - [Что такое программа?](#что-такое-программа)
  - [Компиляция и выполнение](#компиляция-и-выполнение)
- [Основные концепции](#основные-концепции)
  - [Переменные и константы](#переменные-и-константы)
- [Типы данных](#типы-данных)
  - [Примитивные и ссылочные типы](#примитивные-и-ссылочные-типы)
- [Управляющие конструкции](#управляющие-конструкции)
  - [Условные операторы](#условные-операторы)
- [Функции и модули](#функции-и-модули)
  - [Функции (методы в Java)](#функции-методы-в-java)
- [Объектно-ориентированное программирование](#объектно-ориентированное-программирование)
  - [Классы и объекты](#классы-и-объекты)
- [Обработка ошибок](#обработка-ошибок)
  - [Исключения в Java](#исключения-в-java)
- [Парадигмы программирования](#парадигмы-программирования)
  - [Императивное программирование](#императивное-программирование)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые концепции](#ключевые-концепции)
  - [Важность изучения основ](#важность-изучения-основ)
  - [Рекомендации по практике](#рекомендации-по-практике)

## Введение в программирование

Программирование - это процесс создания инструкций для компьютера. Программа - это набор инструкций, которые компьютер может выполнить для решения конкретной задачи.

### Что такое программа?

```java
/**
 * Простейшая программа на Java
 * Эта программа выводит приветствие на экран
 */
public class HelloWorld {

    /**
     * Точка входа в программу
     * JVM начинает выполнение с этого метода
     */
    public static void main(String[] args) {
        // Вывод текста на консоль
        System.out.println("Привет, мир!");

        // Вывод аргументов командной строки
        if (args.length > 0) {
            System.out.println("Переданные аргументы:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("Аргумент " + i + ": " + args[i]);
            }
        }
    }
}
```

### Компиляция и выполнение

```java
/**
 * Процесс разработки программы
 */
public class DevelopmentProcess {

    /**
     * Этапы разработки программы:
     * 1. Анализ требований
     * 2. Проектирование решения
     * 3. Написание кода
     * 4. Компиляция
     * 5. Тестирование
     * 6. Отладка
     * 7. Документирование
     */
    public void developmentStages() {
        System.out.println("Этапы разработки:");
        System.out.println("1. Анализ - понять задачу");
        System.out.println("2. Проектирование - спланировать решение");
        System.out.println("3. Кодирование - написать программу");
        System.out.println("4. Компиляция - преобразовать в машинный код");
        System.out.println("5. Тестирование - проверить работу");
        System.out.println("6. Отладка - исправить ошибки");
        System.out.println("7. Релиз - подготовить к использованию");
    }

    /**
     * Компиляция и запуск Java программы
     */
    public void javaCompilationProcess() {
        // Исходный код (файл HelloWorld.java)
        String sourceCode = "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello!\"); } }";

        System.out.println("1. Исходный код:");
        System.out.println(sourceCode);

        // Компиляция в байт-код
        System.out.println("\n2. Компиляция: javac HelloWorld.java");
        System.out.println("   Результат: HelloWorld.class (байт-код JVM)");

        // Выполнение
        System.out.println("\n3. Выполнение: java HelloWorld");
        System.out.println("   JVM загружает класс, находит main метод и выполняет его");
    }

    /**
     * Типы ошибок в программировании
     */
    public void programmingErrors() {
        System.out.println("Типы ошибок:");

        // Синтаксическая ошибка
        System.out.println("1. Синтаксические ошибки:");
        System.out.println("   - Отсутствующая точка с запятой");
        System.out.println("   - Неправильные скобки");
        System.out.println("   - Ошибки в именах");

        // Логическая ошибка
        System.out.println("\n2. Логические ошибки:");
        System.out.println("   - Неправильный алгоритм");
        System.out.println("   - Ошибки в условиях");
        System.out.println("   - Неправильные вычисления");

        // Ошибки времени выполнения
        System.out.println("\n3. Ошибки выполнения:");
        System.out.println("   - Деление на ноль");
        System.out.println("   - Доступ к null ссылке");
        System.out.println("   - Выход за границы массива");
    }
}
```

## Основные концепции

### Переменные и константы

```java
/**
 * Работа с переменными и константами
 */
public class VariablesAndConstants {

    /**
     * Переменные - контейнеры для хранения данных
     */
    public void demonstrateVariables() {
        // Объявление переменных
        int age;        // Целое число
        double salary;  // Вещественное число
        String name;    // Строка
        boolean isStudent; // Логическое значение

        // Инициализация
        age = 25;
        salary = 50000.50;
        name = "Иван";
        isStudent = true;

        // Использование в выражениях
        int nextYearAge = age + 1;
        double bonus = salary * 0.1;
        String greeting = "Привет, " + name + "!";

        System.out.println("Имя: " + name);
        System.out.println("Возраст: " + age);
        System.out.println("Зарплата: " + salary);
        System.out.println("Студент: " + isStudent);
        System.out.println("Приветствие: " + greeting);
    }

    /**
     * Константы - неизменяемые значения
     */
    public static final double PI = 3.14159;        // Число π
    public static final int MAX_USERS = 100;        // Максимальное число пользователей
    public static final String COMPANY_NAME = "ABC Corp"; // Название компании

    public void demonstrateConstants() {
        System.out.println("Число π: " + PI);
        System.out.println("Максимум пользователей: " + MAX_USERS);
        System.out.println("Компания: " + COMPANY_NAME);

        // Попытка изменить константу вызовет ошибку компиляции
        // PI = 3.14; // Ошибка: cannot assign a value to final variable

        // Константы часто используются для:
        // - Магических чисел
        // - Конфигурационных параметров
        // - Ограничений системы
    }

    /**
     * Области видимости переменных
     */
    public void variableScope() {
        // Глобальная переменная класса
        int classVariable = 10;

        if (true) {
            // Локальная переменная блока
            int blockVariable = 20;
            System.out.println("Класс: " + classVariable + ", Блок: " + blockVariable);
        }

        // blockVariable здесь недоступна
        // System.out.println(blockVariable); // Ошибка компиляции

        for (int i = 0; i < 3; i++) {
            // i доступна только внутри цикла
            System.out.println("Итерация: " + i);
        }

        // i здесь недоступна
        // System.out.println(i); // Ошибка компиляции
    }

    /**
     * Примитивные типы данных в Java
     */
    public void primitiveTypes() {
        // Целые числа
        byte byteValue = 127;           // 8 бит, -128 до 127
        short shortValue = 32767;       // 16 бит, -32,768 до 32,767
        int intValue = 2147483647;      // 32 бит, -2^31 до 2^31-1
        long longValue = 9223372036854775807L; // 64 бит

        // Вещественные числа
        float floatValue = 3.14f;       // 32 бит
        double doubleValue = 3.141592653589793; // 64 бит

        // Символы и логические значения
        char charValue = 'A';           // 16 бит Unicode
        boolean booleanValue = true;    // true или false

        System.out.println("Целые числа:");
        System.out.println("byte: " + byteValue);
        System.out.println("short: " + shortValue);
        System.out.println("int: " + intValue);
        System.out.println("long: " + longValue);

        System.out.println("\nВещественные числа:");
        System.out.println("float: " + floatValue);
        System.out.println("double: " + doubleValue);

        System.out.println("\nДругие типы:");
        System.out.println("char: " + charValue);
        System.out.println("boolean: " + booleanValue);
    }
}
```

## Типы данных

### Примитивные и ссылочные типы

```java
/**
 * Типы данных в Java
 */
public class DataTypes {

    /**
     * Примитивные типы данных
     */
    public void primitiveTypes() {
        // Целочисленные типы
        byte b = 100;     // 8 бит
        short s = 1000;   // 16 бит
        int i = 100000;   // 32 бит
        long l = 100000L; // 64 бит

        // Вещественные типы
        float f = 3.14f;     // 32 бит
        double d = 3.14159;  // 64 бит

        // Символьный тип
        char c = 'A';        // 16 бит Unicode

        // Логический тип
        boolean bool = true; // true/false

        // Литералы
        int decimal = 42;      // Десятичный
        int hex = 0x2A;        // Шестнадцатеричный
        int binary = 0b101010; // Двоичный
        long bigNumber = 1_000_000_000L; // С разделителями

        System.out.println("Примитивные типы:");
        System.out.println("byte: " + b + " (" + Byte.SIZE + " бит)");
        System.out.println("short: " + s + " (" + Short.SIZE + " бит)");
        System.out.println("int: " + i + " (" + Integer.SIZE + " бит)");
        System.out.println("long: " + l + " (" + Long.SIZE + " бит)");
        System.out.println("float: " + f + " (" + Float.SIZE + " бит)");
        System.out.println("double: " + d + " (" + Double.SIZE + " бит)");
        System.out.println("char: " + c + " (" + Character.SIZE + " бит)");
        System.out.println("boolean: " + bool);
    }

    /**
     * Ссылочные типы данных
     */
    public void referenceTypes() {
        // Строки
        String text = "Hello, World!";
        String empty = "";
        String nullString = null;

        // Массивы
        int[] numbers = {1, 2, 3, 4, 5};
        String[] names = new String[3];
        names[0] = "Alice";
        names[1] = "Bob";
        names[2] = "Charlie";

        // Объекты
        Object obj = new Object();
        Integer wrapper = Integer.valueOf(42);

        System.out.println("Ссылочные типы:");
        System.out.println("String: " + text);
        System.out.println("Массив: " + Arrays.toString(numbers));
        System.out.println("Обертка: " + wrapper);
    }

    /**
     * Автоматическая упаковка/распаковка
     */
    public void autoboxing() {
        // Автоматическая упаковка (boxing)
        Integer boxed = 42;        // int -> Integer
        Double pi = 3.14;          // double -> Double

        // Автоматическая распаковка (unboxing)
        int unboxed = boxed;       // Integer -> int
        double unboxedPi = pi;     // Double -> double

        // В коллекциях используются только ссылочные типы
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);            // Автоматическая упаковка
        numbers.add(2);
        numbers.add(3);

        int sum = 0;
        for (Integer num : numbers) {
            sum += num;            // Автоматическая распаковка
        }

        System.out.println("Автоматическая упаковка/распаковка:");
        System.out.println("boxed: " + boxed + " (тип: " + boxed.getClass().getSimpleName() + ")");
        System.out.println("unboxed: " + unboxed + " (тип: int)");
        System.out.println("Сумма: " + sum);
    }

    /**
     * Массивы
     */
    public void arrays() {
        // Объявление и инициализация
        int[] numbers1 = {1, 2, 3, 4, 5};           // Литерал массива
        int[] numbers2 = new int[5];                 // Пустой массив
        int[] numbers3 = new int[]{1, 2, 3, 4, 5};  // Альтернативный синтаксис

        // Доступ к элементам
        numbers2[0] = 10;
        numbers2[1] = 20;

        // Длина массива
        int length = numbers1.length;

        // Многомерные массивы
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println("Массивы:");
        System.out.println("Одномерный: " + Arrays.toString(numbers1));
        System.out.println("Длина: " + length);
        System.out.println("Элемент [0]: " + numbers1[0]);
        System.out.println("Двумерный:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Перечисления (Enums)
     */
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    public enum HttpStatus {
        OK(200, "OK"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_ERROR(500, "Internal Server Error");

        private final int code;
        private final String message;

        HttpStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public String getMessage() { return message; }
    }

    public void enumerations() {
        DayOfWeek today = DayOfWeek.MONDAY;
        HttpStatus status = HttpStatus.OK;

        System.out.println("Перечисления:");
        System.out.println("Сегодня: " + today);
        System.out.println("Все дни недели: " + Arrays.toString(DayOfWeek.values()));
        System.out.println("HTTP статус: " + status.getCode() + " " + status.getMessage());
    }
}
```

## Управляющие конструкции

### Условные операторы

```java
/**
 * Условные операторы и циклы
 */
public class ControlStructures {

    /**
     * Условный оператор if-else
     */
    public void ifElseStatements() {
        int score = 85;
        String grade;

        if (score >= 90) {
            grade = "Отлично";
        } else if (score >= 80) {
            grade = "Хорошо";
        } else if (score >= 70) {
            grade = "Удовлетворительно";
        } else {
            grade = "Неудовлетворительно";
        }

        System.out.println("Оценка: " + grade);

        // Тернарный оператор
        String result = score >= 70 ? "Прошел" : "Не прошел";
        System.out.println("Результат: " + result);
    }

    /**
     * Оператор switch
     */
    public void switchStatement() {
        int day = 3;
        String dayName;

        switch (day) {
            case 1:
                dayName = "Понедельник";
                break;
            case 2:
                dayName = "Вторник";
                break;
            case 3:
                dayName = "Среда";
                break;
            case 4:
                dayName = "Четверг";
                break;
            case 5:
                dayName = "Пятница";
                break;
            case 6:
                dayName = "Суббота";
                break;
            case 7:
                dayName = "Воскресенье";
                break;
            default:
                dayName = "Неверный день";
                break;
        }

        System.out.println("День недели: " + dayName);

        // Switch с enum
        DayOfWeek today = DayOfWeek.MONDAY;
        switch (today) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                System.out.println("Рабочий день");
                break;
            case SATURDAY:
            case SUNDAY:
                System.out.println("Выходной день");
                break;
        }
    }

    /**
     * Циклы
     */
    public void loops() {
        // Цикл for
        System.out.println("Цикл for:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Итерация " + i);
        }

        // Цикл while
        System.out.println("\nЦикл while:");
        int counter = 1;
        while (counter <= 3) {
            System.out.println("Счетчик: " + counter);
            counter++;
        }

        // Цикл do-while
        System.out.println("\nЦикл do-while:");
        int number = 1;
        do {
            System.out.println("Число: " + number);
            number *= 2;
        } while (number <= 10);

        // Цикл for-each
        System.out.println("\nЦикл for-each:");
        String[] fruits = {"Яблоко", "Банан", "Апельсин"};
        for (String fruit : fruits) {
            System.out.println("Фрукт: " + fruit);
        }

        // Прерывание и продолжение циклов
        System.out.println("\nПрерывание цикла:");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) {
                System.out.println("Достигнуто 5, прерываем цикл");
                break;  // Выход из цикла
            }
            if (i % 2 == 0) {
                continue;  // Пропуск четных чисел
            }
            System.out.println("Нечетное число: " + i);
        }
    }

    /**
     * Логические операторы
     */
    public void logicalOperators() {
        boolean a = true;
        boolean b = false;

        System.out.println("Логические операторы:");
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a && b (И): " + (a && b));
        System.out.println("a || b (ИЛИ): " + (a || b));
        System.out.println("!a (НЕ): " + (!a));
        System.out.println("!b (НЕ): " + (!b));

        // Примеры использования
        int age = 25;
        boolean hasLicense = true;
        boolean isStudent = false;

        boolean canDrive = age >= 18 && hasLicense;
        boolean getsDiscount = isStudent || age < 18;
        boolean isAdult = !(age < 18);

        System.out.println("\nПримеры:");
        System.out.println("Может водить: " + canDrive);
        System.out.println("Получает скидку: " + getsDiscount);
        System.out.println("Взрослый: " + isAdult);
    }

    /**
     * Вложенные условия и циклы
     */
    public void nestedStructures() {
        System.out.println("Таблица умножения:");

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                int product = i * j;

                if (product % 2 == 0) {
                    System.out.println(i + " × " + j + " = " + product + " (четное)");
                } else {
                    System.out.println(i + " × " + j + " = " + product + " (нечетное)");
                }
            }
            System.out.println();  // Пустая строка между строками таблицы
        }
    }
}
```

## Функции и модули

### Функции (**методы в Java**)

```java
/**
 * Работа с функциями (методами)
 */
public class FunctionsAndMethods {

    /**
     * Простые функции
     */
    public static int add(int a, int b) {
        return a + b;
    }

    public static void greet(String name) {
        System.out.println("Привет, " + name + "!");
    }

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Функции с переменным числом аргументов
     */
    public static int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }

    /**
     * Функции с несколькими возвращаемыми значениями
     * (используем массивы или объекты)
     */
    public static int[] divideAndRemainder(int dividend, int divisor) {
        int quotient = dividend / divisor;
        int remainder = dividend % divisor;
        return new int[]{quotient, remainder};
    }

    /**
     * Перегрузка методов
     */
    public static double calculateArea(double radius) {
        return Math.PI * radius * radius;  // Круг
    }

    public static double calculateArea(double length, double width) {
        return length * width;  // Прямоугольник
    }

    public static double calculateArea(double base, double height, boolean isTriangle) {
        if (isTriangle) {
            return 0.5 * base * height;  // Треугольник
        } else {
            return base * height;  // Прямоугольник
        }
    }

    /**
     * Рекурсивные функции
     */
    public static int factorial(int n) {
        if (n <= 1) {
            return 1;  // Базовый случай
        }
        return n * factorial(n - 1);  // Рекурсивный случай
    }

    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * Демонстрация функций
     */
    public static void demonstrateFunctions() {
        System.out.println("Функции:");

        // Простые функции
        int sum = add(5, 3);
        System.out.println("5 + 3 = " + sum);

        greet("Мир");

        boolean even = isEven(4);
        System.out.println("4 четное: " + even);

        // Переменное число аргументов
        int total = sum(1, 2, 3, 4, 5);
        System.out.println("Сумма 1+2+3+4+5 = " + total);

        // Несколько возвращаемых значений
        int[] result = divideAndRemainder(17, 5);
        System.out.println("17 ÷ 5 = " + result[0] + " остаток " + result[1]);

        // Перегрузка методов
        double circleArea = calculateArea(5.0);
        double rectangleArea = calculateArea(4.0, 6.0);
        double triangleArea = calculateArea(4.0, 6.0, true);

        System.out.println("Площадь круга (r=5): " + String.format("%.2f", circleArea));
        System.out.println("Площадь прямоугольника (4×6): " + rectangleArea);
        System.out.println("Площадь треугольника (4×6): " + triangleArea);

        // Рекурсия
        System.out.println("Факториал 5: " + factorial(5));
        System.out.println("Фибоначчи 8: " + fibonacci(8));
    }

    /**
     * Побочные эффекты и чистые функции
     */
    private static int globalCounter = 0;

    // Функция с побочным эффектом
    public static int incrementCounter() {
        globalCounter++;
        return globalCounter;
    }

    // Чистая функция (без побочных эффектов)
    public static int addPure(int a, int b) {
        return a + b;
    }

    // Функция высшего порядка
    public static int[] processArray(int[] array, java.util.function.IntUnaryOperator operation) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = operation.applyAsInt(array[i]);
        }
        return result;
    }

    public static void demonstrateAdvancedFunctions() {
        System.out.println("Продвинутые концепции:");

        // Побочные эффекты
        System.out.println("Счетчик: " + incrementCounter());
        System.out.println("Счетчик: " + incrementCounter());

        // Чистые функции
        System.out.println("Чистая функция: 3 + 4 = " + addPure(3, 4));

        // Функции высшего порядка
        int[] numbers = {1, 2, 3, 4, 5};
        int[] doubled = processArray(numbers, x -> x * 2);
        int[] squared = processArray(numbers, x -> x * x);

        System.out.println("Оригинал: " + Arrays.toString(numbers));
        System.out.println("Удвоенные: " + Arrays.toString(doubled));
        System.out.println("Квадраты: " + Arrays.toString(squared));
    }
}
```

## Объектно-ориентированное программирование

### Классы и объекты

```java
/**
 * Основы объектно-ориентированного программирования
 */
public class ObjectOrientedProgramming {

    /**
     * Простой класс
     */
    public static class Person {
        // Поля (состояние объекта)
        private String name;
        private int age;
        private String email;

        // Конструктор (создание объекта)
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Конструктор с email
        public Person(String name, int age, String email) {
            this(name, age);  // Вызов другого конструктора
            this.email = email;
        }

        // Методы (поведение объекта)
        public void introduce() {
            System.out.println("Привет! Меня зовут " + name + ", мне " + age + " лет.");
            if (email != null) {
                System.out.println("Мой email: " + email);
            }
        }

        public void celebrateBirthday() {
            age++;
            System.out.println("С днем рождения! Теперь мне " + age + " лет.");
        }

        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        // Переопределение toString()
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
        }

        // Переопределение equals() и hashCode()
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Person person = (Person) obj;
            return age == person.age &&
                   Objects.equals(name, person.name) &&
                   Objects.equals(email, person.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, email);
        }
    }

    /**
     * Наследование
     */
    public static class Student extends Person {
        private String university;
        private double gpa;  // Средний балл

        public Student(String name, int age, String university) {
            super(name, age);  // Вызов конструктора родителя
            this.university = university;
            this.gpa = 0.0;
        }

        @Override
        public void introduce() {
            super.introduce();  // Вызов метода родителя
            System.out.println("Я учусь в " + university + " со средним баллом " + gpa);
        }

        public void study() {
            gpa += 0.1;
            if (gpa > 4.0) gpa = 4.0;
            System.out.println("Учусь... Средний балл теперь: " + String.format("%.1f", gpa));
        }

        public String getUniversity() { return university; }
        public double getGpa() { return gpa; }
    }

    /**
     * Инкапсуляция и модификаторы доступа
     */
    public static class BankAccount {
        private String accountNumber;
        private double balance;
        private String owner;

        public BankAccount(String accountNumber, String owner) {
            this.accountNumber = accountNumber;
            this.owner = owner;
            this.balance = 0.0;
        }

        // Публичные методы для доступа к приватным полям
        public double getBalance() {
            return balance;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getOwner() {
            return owner;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
                System.out.println("Пополнение на " + amount + ". Баланс: " + balance);
            }
        }

        public boolean withdraw(double amount) {
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                System.out.println("Снятие " + amount + ". Баланс: " + balance);
                return true;
            }
            System.out.println("Недостаточно средств или неверная сумма");
            return false;
        }

        // Приватный метод (вспомогательный)
        private void logTransaction(String type, double amount) {
            System.out.println("Транзакция: " + type + " " + amount + " для счета " + accountNumber);
        }
    }

    /**
     * Полиморфизм
     */
    public static abstract class Animal {
        protected String name;

        public Animal(String name) {
            this.name = name;
        }

        // Абстрактный метод (должен быть реализован в подклассах)
        public abstract void makeSound();

        // Обычный метод
        public void eat() {
            System.out.println(name + " ест.");
        }

        public String getName() { return name; }
    }

    public static class Dog extends Animal {
        public Dog(String name) {
            super(name);
        }

        @Override
        public void makeSound() {
            System.out.println(name + " лает: Гав-гав!");
        }
    }

    public static class Cat extends Animal {
        public Cat(String name) {
            super(name);
        }

        @Override
        public void makeSound() {
            System.out.println(name + " мяукает: Мяу!");
        }
    }

    /**
     * Демонстрация ООП
     */
    public static void demonstrateOOP() {
        System.out.println("=== Объектно-ориентированное программирование ===");

        // Создание объектов
        Person person = new Person("Иван", 25, "ivan@example.com");
        Student student = new Student("Мария", 20, "МГУ");

        System.out.println("Человек: " + person);
        System.out.println("Студент: " + student);

        // Использование методов
        person.introduce();
        System.out.println();
        student.introduce();
        student.study();
        student.study();

        // Инкапсуляция
        BankAccount account = new BankAccount("123456789", "Иван Петров");
        account.deposit(1000.0);
        account.withdraw(200.0);
        System.out.println("Баланс: " + account.getBalance());

        // Полиморфизм
        List<Animal> animals = Arrays.asList(
            new Dog("Бобик"),
            new Cat("Мурка"),
            new Dog("Шарик")
        );

        System.out.println("\nЗвуки животных:");
        for (Animal animal : animals) {
            animal.makeSound();
            animal.eat();
            System.out.println();
        }
    }
}
```

## Обработка ошибок

### Исключения в **Java**

```java
/**
 * Обработка исключений
 */
public class ExceptionHandling {

    /**
     * Типы исключений
     */
    public static void exceptionTypes() {
        System.out.println("Типы исключений в Java:");
        System.out.println("1. Checked exceptions - проверяемые (IOException, SQLException)");
        System.out.println("2. Unchecked exceptions - непроверяемые (RuntimeException)");
        System.out.println("3. Errors - ошибки (OutOfMemoryError, StackOverflowError)");
    }

    /**
     * Базовый синтаксис try-catch-finally
     */
    public static void basicTryCatch() {
        try {
            // Код, который может вызвать исключение
            int result = 10 / 0;  // ArithmeticException
            System.out.println("Результат: " + result);

        } catch (ArithmeticException e) {
            // Обработка конкретного типа исключения
            System.out.println("Ошибка деления: " + e.getMessage());

        } catch (Exception e) {
            // Обработка остальных исключений
            System.out.println("Общая ошибка: " + e.getMessage());

        } finally {
            // Код, который выполнится всегда
            System.out.println("Блок finally выполнен");
        }
    }

    /**
     * Создание собственных исключений
     */
    public static class InsufficientFundsException extends Exception {
        private double required;
        private double available;

        public InsufficientFundsException(double required, double available) {
            super("Недостаточно средств: требуется " + required + ", доступно " + available);
            this.required = required;
            this.available = available;
        }

        public double getRequired() { return required; }
        public double getAvailable() { return available; }
    }

    public static class BankAccount {
        private double balance;

        public BankAccount(double initialBalance) {
            this.balance = initialBalance;
        }

        public void withdraw(double amount) throws InsufficientFundsException {
            if (amount > balance) {
                throw new InsufficientFundsException(amount, balance);
            }
            balance -= amount;
            System.out.println("Снято: " + amount + ", остаток: " + balance);
        }

        public double getBalance() { return balance; }
    }

    /**
     * Цепочка исключений
     */
    public static void exceptionChaining() throws Exception {
        try {
            // Имитация ошибки в низкоуровневой операции
            throw new SQLException("Ошибка базы данных");

        } catch (SQLException e) {
            // Обертывание в высокоуровневое исключение
            throw new RuntimeException("Ошибка доступа к данным", e);
        }
    }

    /**
     * Try-with-resources
     */
    public static void tryWithResources() {
        // Автоматическое закрытие ресурсов
        try (BufferedReader reader = new BufferedReader(new FileReader("example.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
        // reader.close() вызывается автоматически
    }

    /**
     * Демонстрация обработки исключений
     */
    public static void demonstrateExceptions() {
        System.out.println("=== Обработка исключений ===");

        // Базовый try-catch
        basicTryCatch();

        // Собственные исключения
        BankAccount account = new BankAccount(100.0);

        try {
            account.withdraw(50.0);    // Успешно
            account.withdraw(80.0);    // Исключение
        } catch (InsufficientFundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
            System.out.println("Требуется: " + e.getRequired());
            System.out.println("Доступно: " + e.getAvailable());
        }

        // Цепочка исключений
        try {
            exceptionChaining();
        } catch (Exception e) {
            System.out.println("Цепочка исключений: " + e.getMessage());
            System.out.println("Причина: " + e.getCause().getMessage());
        }
    }

    /**
     * Лучшие практики обработки исключений
     */
    public static void bestPractices() {
        System.out.println("Лучшие практики обработки исключений:");

        System.out.println("1. Ловите конкретные исключения, не Exception");
        System.out.println("2. Не глотите исключения молча");
        System.out.println("3. Используйте finally для очистки ресурсов");
        System.out.println("4. Создавайте информативные сообщения об ошибках");
        System.out.println("5. Не используйте исключения для управления потоком");
        System.out.println("6. Документируйте проверяемые исключения");
        System.out.println("7. Избегайте пустых блоков catch");

        // Пример хорошей практики
        try {
            riskyOperation();
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при выполнении операции", e);
            // Попытка восстановления или уведомление пользователя
            notifyUser("Произошла ошибка. Попробуйте позже.");
        } catch (IllegalArgumentException e) {
            logger.warn("Неверные аргументы: " + e.getMessage());
            // Валидация данных
        }
    }

    // Заглушки для демонстрации
    private static void riskyOperation() throws IOException {
        if (Math.random() > 0.7) {
            throw new IOException("Сетевая ошибка");
        }
    }

    private static void logger(String level, String message, Exception e) {
        System.out.println("[" + level.toUpperCase() + "] " + message);
    }

    private static void notifyUser(String message) {
        System.out.println("Уведомление пользователю: " + message);
    }
}
```

## Парадигмы программирования

### Императивное программирование

```java
/**
 * Парадигмы программирования
 */
public class ProgrammingParadigms {

    /**
     * Императивное программирование - команды изменяют состояние
     */
    public static class ImperativeProgramming {

        public static void demonstrateImperative() {
            System.out.println("Императивное программирование:");

            // Изменение состояния через команды
            int sum = 0;
            for (int i = 1; i <= 5; i++) {
                sum = sum + i;  // Изменение переменной
            }
            System.out.println("Сумма чисел от 1 до 5: " + sum);

            // Работа с массивами
            int[] numbers = {1, 2, 3, 4, 5};
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = numbers[i] * 2;  // Изменение элементов массива
            }
            System.out.println("Удвоенные числа: " + Arrays.toString(numbers));
        }
    }

    /**
     * Функциональное программирование - функции как значения
     */
    public static class FunctionalProgramming {

        public static void demonstrateFunctional() {
            System.out.println("Функциональное программирование:");

            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

            // Функции высшего порядка
            List<Integer> doubled = numbers.stream()
                    .map(x -> x * 2)  // Преобразование
                    .collect(Collectors.toList());

            List<Integer> evenNumbers = numbers.stream()
                    .filter(x -> x % 2 == 0)  // Фильтрация
                    .collect(Collectors.toList());

            int sum = numbers.stream()
                    .reduce(0, (a, b) -> a + b);  // Свертка

            System.out.println("Оригинал: " + numbers);
            System.out.println("Удвоенные: " + doubled);
            System.out.println("Четные: " + evenNumbers);
            System.out.println("Сумма: " + sum);

            // Функции как параметры
            Function<Integer, Integer> square = x -> x * x;
            Function<Integer, Integer> addTen = x -> x + 10;

            // Композиция функций
            Function<Integer, Integer> squareThenAddTen = square.andThen(addTen);
            Function<Integer, Integer> addTenThenSquare = addTen.andThen(square);

            System.out.println("square(5) = " + square.apply(5));
            System.out.println("addTen(5) = " + addTen.apply(5));
            System.out.println("square(addTen(5)) = " + squareThenAddTen.apply(5));
            System.out.println("addTen(square(5)) = " + addTenThenSquare.apply(5));
        }

        /**
         * Чистые функции
         */
        public static int pureAdd(int a, int b) {
            return a + b;  // Только зависит от аргументов, без побочных эффектов
        }

        public static List<Integer> pureFilter(List<Integer> list, Predicate<Integer> predicate) {
            return list.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());  // Возвращает новый список, не изменяет оригинал
        }

        /**
         * Рекурсия вместо циклов
         */
        public static int recursiveSum(List<Integer> numbers) {
            if (numbers.isEmpty()) {
                return 0;
            }
            return numbers.get(0) + recursiveSum(numbers.subList(1, numbers.size()));
        }

        /**
         * Ленивые вычисления
         */
        public static void demonstrateLazy() {
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            // Ленивые операции - выполняются только при необходимости
            Stream<Integer> evenSquares = numbers.stream()
                    .filter(x -> {
                        System.out.println("Фильтрация: " + x);
                        return x % 2 == 0;
                    })
                    .map(x -> {
                        System.out.println("Преобразование: " + x);
                        return x * x;
                    });

            System.out.println("Создан поток, но вычисления еще не выполнялись");

            // Терминальная операция запускает вычисления
            List<Integer> result = evenSquares.limit(2).collect(Collectors.toList());
            System.out.println("Результат: " + result);
        }
    }

    /**
     * Объектно-ориентированное программирование
     */
    public static class OOPExample {

        interface Shape {
            double area();
            double perimeter();
        }

        static class Circle implements Shape {
            private double radius;

            Circle(double radius) { this.radius = radius; }

            @Override
            public double area() { return Math.PI * radius * radius; }

            @Override
            public double perimeter() { return 2 * Math.PI * radius; }
        }

        static class Rectangle implements Shape {
            private double width, height;

            Rectangle(double width, double height) {
                this.width = width;
                this.height = height;
            }

            @Override
            public double area() { return width * height; }

            @Override
            public double perimeter() { return 2 * (width + height); }
        }

        public static void demonstrateOOP() {
            System.out.println("Объектно-ориентированное программирование:");

            List<Shape> shapes = Arrays.asList(
                new Circle(5.0),
                new Rectangle(4.0, 6.0),
                new Circle(3.0)
            );

            for (Shape shape : shapes) {
                System.out.printf("Площадь: %.2f, Периметр: %.2f%n",
                                shape.area(), shape.perimeter());
            }
        }
    }

    /**
     * Демонстрация парадигм
     */
    public static void demonstrateParadigms() {
        System.out.println("=== Парадигмы программирования ===");

        ImperativeProgramming.demonstrateImperative();
        System.out.println();

        FunctionalProgramming.demonstrateFunctional();
        System.out.println();

        OOPExample.demonstrateOOP();
        System.out.println();

        // Сравнение подходов
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        System.out.println("Сравнение парадигм для суммирования:");

        // Императивно
        int imperativeSum = 0;
        for (int num : numbers) {
            imperativeSum += num;
        }
        System.out.println("Императивно: " + imperativeSum);

        // Функционально
        int functionalSum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Функционально: " + functionalSum);

        // ООП стиль
        Calculator calc = new Calculator();
        int oopSum = calc.sum(numbers);
        System.out.println("ООП: " + oopSum);
    }

    static class Calculator {
        public int sum(List<Integer> numbers) {
            int total = 0;
            for (int num : numbers) {
                total += num;
            }
            return total;
        }
    }
}
```


## Решение проблем

- **Ошибки компиляции / типов** — проверить объявления типов, импорты и соответствие сигнатур; в Java — дженерики и автоупаковку.
- **Исключения в рантайме** — NullPointerException: проверить инициализацию и null-checks; ArrayIndexOutOfBounds: границы массивов и циклов.
- **Неочевидное поведение** — сверить с разделом «Типы данных» и «Управляющие конструкции»; использовать отладчик и логирование.

## Частые вопросы

- **Когда использовать класс, а когда интерфейс?** Класс — конкретная реализация и состояние; интерфейс — контракт для полиморфизма и слабой связности.
- **Как правильно обрабатывать ошибки?** Не глотать исключения; использовать проверяемые исключения для восстановимых ошибок; см. раздел «Обработка ошибок».
- **См. также:** разделы «ООП», «Типы данных» и «Полезные ссылки» в документе.


## Заключение

Основы программирования включают фундаментальные концепции, которые являются основой для всех языков программирования. Понимание этих концепций критически важно для становления хорошим программистом.

### Ключевые концепции

1. **Переменные и типы данных**: Хранение и обработка информации
2. **Управляющие конструкции**: Принятие решений и повторения
3. **Функции**: Модуляризация и повторное использование кода
4. **Объектно-ориентированное программирование**: Организация кода через объекты
5. **Обработка ошибок**: Управление исключительными ситуациями
6. **Парадигмы программирования**: Различные подходы к решению задач

### Важность изучения основ

- **Фундаментальные знания**: Основа для изучения любого языка программирования
- **Алгоритмическое мышление**: Способность разбивать сложные задачи на простые
- **Хорошие привычки**: Читаемый, поддерживаемый и эффективный код
- **Адаптивность**: Легкость изучения новых технологий и языков
- **Решение проблем**: Систематический подход к программированию

### Рекомендации по практике

1. **Начинайте с малого**: Пишите простые программы
2. **Регулярно практикуйтесь**: Решайте задачи ежедневно
3. **Читаете чужой код**: Изучайте как пишут другие
4. **Экспериментируйте**: Пробуйте разные подходы к одной задаче
5. **Документируйте**: Комментируйте и объясняйте свой код
6. **Участвуйте в проектах**: Работайте над реальными задачами
7. **Следите за качеством**: Читаемость важнее скорости написания

Помните: программирование - это навык, который развивается с практикой!

---
