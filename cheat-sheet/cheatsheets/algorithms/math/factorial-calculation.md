# Factorial Calculation

Кратко: факториал неотрицательного целого числа n - это произведение всех положительных целых чисел, меньших или равных n. Рассматриваются различные способы вычисления факториала: цикл, рекурсия, Stream API, Apache Commons Math и Guava.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Program for factorial of a number](https://www.geeksforgeeks.org/program-for-factorial-of-a-number/)

### См. также
- `./fibonacci-sequence.md` - ряд Фибоначчи
- `./greatest-common-divisor.md` - наибольший общий делитель

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Работа с большими числами](#работа-с-большими-числами)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Учитывая неотрицательное целое число n, факториал - это произведение всех положительных целых чисел, меньших или равных n.

В этом кратком руководстве мы рассмотрим различные способы вычисления факториала для заданного числа в Java.

### Математическое определение

Факториал числа n обозначается как n! и определяется как:

```
n! = n × (n-1) × (n-2) × ... × 2 × 1
0! = 1 (по определению)
```

### Примеры

- 5! = 5 × 4 × 3 × 2 × 1 = 120
- 3! = 3 × 2 × 1 = 6
- 0! = 1

## Java Implementation

### Подход 1: Цикл for

Давайте рассмотрим базовый алгоритм факториала с использованием цикла for:

```java
public long factorialUsingForLoop(int n) {
    long fact = 1;
    for (int i = 2; i <= n; i++) {
        fact = fact * i;
    }
    return fact;
}
```

Приведенное выше решение отлично работает для чисел до 20. Но если мы попробуем что-то большее, чем 20, это потерпит неудачу, потому что результаты будут слишком большими, чтобы поместиться в long, что приведет к переполнению.

### Обработка переполнения

```java
public long factorialUsingForLoopWithCheck(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n > 20) {
        throw new IllegalArgumentException("Result will overflow long type");
    }
    
    long fact = 1;
    for (int i = 2; i <= n; i++) {
        fact = fact * i;
    }
    return fact;
}
```

## Подход 2: Stream API

Мы также можем использовать Java 8 Stream API для довольно простого вычисления факториалов:

```java
public long factorialUsingStreams(int n) {
    return LongStream.rangeClosed(1, n)
        .reduce(1, (long x, long y) -> x * y);
}
```

В этой программе мы сначала используем LongStream для перебора чисел от 1 до n. Затем мы использовали метод `reduce()`, который использует значение идентичности и функцию-аккумулятор для шага сокращения.

### Альтернативная версия с проверкой

```java
public long factorialUsingStreamsSafe(int n) {
    if (n < 0 || n > 20) {
        throw new IllegalArgumentException("n must be between 0 and 20");
    }
    
    return LongStream.rangeClosed(1, n)
        .reduce(1, Math::multiplyExact);
}
```

## Подход 3: Рекурсия

И давайте посмотрим еще один пример факториальной программы, на этот раз с использованием рекурсии:

```java
public long factorialUsingRecursion(int n) {
    if (n <= 2) {
        return n;
    }
    return n * factorialUsingRecursion(n - 1);
}
```

### Улучшенная версия с базовым случаем

```java
public long factorialUsingRecursionImproved(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return 1;
    }
    
    return n * factorialUsingRecursionImproved(n - 1);
}
```

### Хвостовая рекурсия (для больших чисел)

```java
public BigInteger factorialTailRecursive(int n) {
    return factorialTailRecursiveHelper(BigInteger.ONE, BigInteger.valueOf(n));
}

private BigInteger factorialTailRecursiveHelper(BigInteger acc, BigInteger n) {
    if (n.compareTo(BigInteger.ONE) <= 0) {
        return acc;
    }
    return factorialTailRecursiveHelper(acc.multiply(n), n.subtract(BigInteger.ONE));
}
```

## Подход 4: Apache Commons Math

В Apache Commons Math есть класс CombinatoricsUtils со статическим методом факториала, который мы можем использовать для вычисления факториала.

Чтобы включить Apache Commons Math, мы добавим зависимость commons-math3 в наш pom:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

Давайте рассмотрим пример с использованием класса CombinatoricsUtils:

```java
public long factorialUsingApacheCommons(int n) {
    return CombinatoricsUtils.factorial(n);
}
```

Обратите внимание, что тип возвращаемого значения - long, как и в наших собственных решениях.

Здесь это означает, что если вычисленное значение превышает Long.MAX_VALUE, генерируется исключение MathArithmeticException.

## Подход 5: Guava

Библиотека Guava от Google также предоставляет служебный метод для вычисления факториалов для больших чисел.

Чтобы включить библиотеку, мы можем добавить ее зависимость от guava в наш pom:

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Теперь мы можем использовать метод статического факториала из класса BigIntegerMath для вычисления факториала заданного числа:

```java
public BigInteger factorialUsingGuava(int n) {
    return BigIntegerMath.factorial(n);
}
```

## Kotlin Implementation

### Подход 1: Цикл for

```kotlin
fun factorialUsingForLoopK(n: Int): Long {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n > 20) { // Max value for Long
        throw IllegalArgumentException("Result will overflow Long type")
    }
    var fact: Long = 1
    for (i in 2..n) {
        fact *= i
    }
    return fact
}
```

### Подход 2: Функциональный стиль

```kotlin
fun factorialUsingFoldK(n: Int): Long {
    if (n < 0 || n > 20) {
        throw IllegalArgumentException("n must be between 0 and 20")
    }
    return (1..n).fold(1L) { acc, i -> acc * i }
}
```

### Подход 3: Рекурсия

```kotlin
fun factorialUsingRecursionK(n: Int): Long {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return 1
    }
    return n * factorialUsingRecursionK(n - 1)
}
```

### Подход 4: Хвостовая рекурсия

```kotlin
tailrec fun factorialTailRecursiveK(acc: Long = 1, n: Int): Long {
    return if (n <= 1) acc else factorialTailRecursiveK(acc * n, n - 1)
}

// Использование
fun factorialK(n: Int): Long = factorialTailRecursiveK(1, n)
```

### Подход 5: Использование BigInteger

```kotlin
import java.math.BigInteger

fun factorialUsingBigIntegerK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return BigInteger.ONE
    }
    var result = BigInteger.ONE
    for (i in 2..n) {
        result = result.multiply(BigInteger.valueOf(i.toLong()))
    }
    return result
}
```

### Подход 6: Функциональный стиль с BigInteger

```kotlin
fun factorialUsingBigIntegerFoldK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    return (1..n).fold(BigInteger.ONE) { acc, i -> 
        acc.multiply(BigInteger.valueOf(i.toLong())) 
    }
}
```

### Подход 7: Мемоизация

```kotlin
val factorialCache = mutableMapOf<Int, BigInteger>()

fun factorialMemoizedK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return BigInteger.ONE
    }
    return factorialCache.getOrPut(n) {
        BigInteger.valueOf(n.toLong()).multiply(factorialMemoizedK(n - 1))
    }
}
```

### Пример использования

```kotlin
fun main() {
    // Для небольших чисел
    println(factorialUsingForLoopK(5)) // 120
    println(factorialK(5)) // 120
    
    // Для больших чисел
    println(factorialUsingBigIntegerK(25)) // 15511210043330985984000000
    println(factorialMemoizedK(25)) // 15511210043330985984000000
}
```

## Работа с большими числами

Чтобы стать больше, нам понадобится другой тип возвращаемого значения.

Как обсуждалось ранее, тип данных long можно использовать для факториалов только при n <= 20.

Для больших значений n мы можем использовать класс BigInteger из пакета java.math, который может содержать значения до 2^Integer.MAX_VALUE:

```java
public BigInteger factorialHavingLargeResult(int n) {
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
}
```

### Оптимизированная версия с BigInteger

```java
public BigInteger factorialBigIntegerOptimized(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return BigInteger.ONE;
    }
    
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
}
```

## Сравнение подходов

| Подход | Максимальное n (long) | Максимальное n (BigInteger) | Простота | Производительность |
|--------|----------------------|----------------------------|----------|-------------------|
| Цикл for | 20 | Неограничено | Высокая | Высокая |
| Stream API | 20 | Неограничено | Средняя | Средняя |
| Рекурсия | 20 | Ограничено стеком | Высокая | Низкая |
| Apache Commons | 20 | Не поддерживается | Высокая | Высокая |
| Guava | Не поддерживается | Неограничено | Высокая | Высокая |

## Сложность

### Временная сложность

- **Все подходы:** O(n) - необходимо перемножить n чисел

### Пространственная сложность

- **Цикл for / Stream API:** O(1) - только константная память
- **Рекурсия:** O(n) - для стека вызовов
- **BigInteger:** O(n log n) - для хранения результата

## Особенности

- **Ограничения:** long может хранить факториалы только до 20!
- **Переполнение:** Для n > 20 необходимо использовать BigInteger
- **Эффективность:** Итеративный подход наиболее эффективен

## Применение

Вычисление факториала используется в:

- Комбинаторике
- Теории вероятностей
- Математическом анализе
- Алгоритмах (перестановки, комбинации)
- Научных вычислениях

## Варианты задачи

### Вариант 1: Двойной факториал

```java
public long doubleFactorial(int n) {
    if (n <= 1) {
        return 1;
    }
    return n * doubleFactorial(n - 2);
}
```

### Вариант 2: Факториал с мемоизацией

```java
private static final Map<Integer, BigInteger> FACTORIAL_CACHE = new ConcurrentHashMap<>();

public BigInteger factorialMemoized(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return BigInteger.ONE;
    }
    
    return FACTORIAL_CACHE.computeIfAbsent(n, k -> 
        BigInteger.valueOf(k).multiply(factorialMemoized(k - 1))
    );
}
```

### Вариант 3: Логарифм факториала

```java
public double logFactorial(int n) {
    double sum = 0.0;
    for (int i = 2; i <= n; i++) {
        sum += Math.log(i);
    }
    return sum;
}
```

## Когда использовать

### Используйте цикл for, когда:

- Нужна максимальная производительность
- Работаете с небольшими числами (n <= 20)
- Простота важна

### Используйте BigInteger, когда:

- Работаете с большими числами (n > 20)
- Нужна точность
- Результат может быть очень большим

### Используйте библиотеки (Guava/Apache), когда:

- Нужна надежность
- Не хотите писать свой код
- Важна производительность

## Заключение

В этой статье мы увидели несколько способов вычисления факториалов с использованием ядра Java, а также нескольких внешних библиотек.

Мы впервые увидели решения, использующие тип данных long для вычисления факториалов чисел до 20. Затем мы увидели несколько способов использования BigInteger для чисел больше 20.
