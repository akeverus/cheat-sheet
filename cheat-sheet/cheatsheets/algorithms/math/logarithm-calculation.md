# Logarithm Calculation

Кратко: вычисление логарифмов в Java. Рассматриваются десятичные логарифмы, натуральные логарифмы и логарифмы с пользовательским основанием.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.log() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#log-double-)
- [Java Math.log10() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#log10-double-)

### См. также
- `./factorial-calculation.md` - вычисление факториала
- `./fibonacci-sequence.md` - ряд Фибоначчи

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы научимся вычислять логарифмы в Java. Мы рассмотрим как десятичные, так и натуральные логарифмы, а также логарифмы с пользовательским основанием.

Логарифм - это математическая формула, представляющая степень, в которую мы должны возвести фиксированное число (по основанию), чтобы получить данное число.

В своей простейшей форме он отвечает на вопрос: сколько раз нужно умножить одно число, чтобы получить другое число?

### Математическое определение

Мы можем определить логарифм следующим уравнением:

```
log_b(x) = y, где b^y = x
```

Где:
- b - основание логарифма
- x - число, для которого вычисляется логарифм
- y - результат (логарифм)

## Java Implementation

### Десятичный логарифм

Логарифмы по основанию 10 называются десятичными логарифмами.

Чтобы вычислить десятичный логарифм в Java, мы можем просто использовать метод `Math.log10()`:

```java
@Test
public void givenLog10_shouldReturnValidResults() {
    assertEquals(Math.log10(100), 2, 0.001);
    assertEquals(Math.log10(1000), 3, 0.001);
    assertEquals(Math.log10(1), 0, 0.001);
}
```

### Примеры использования

```java
double log10_100 = Math.log10(100);    // 2.0
double log10_1000 = Math.log10(1000);  // 3.0
double log10_0_1 = Math.log10(0.1);    // -1.0
```

### Обработка особых случаев

```java
public static double log10Safe(double x) {
    if (x <= 0) {
        throw new IllegalArgumentException("Logarithm is not defined for non-positive numbers");
    }
    return Math.log10(x);
}
```

## Натуральный логарифм

Логарифмы по основанию e (число Эйлера ≈ 2.71828) называются натуральными логарифмами.

Для вычисления натурального логарифма в Java мы используем метод `Math.log()`:

```java
@Test
public void givenNaturalLog_shouldReturnValidResults() {
    assertEquals(Math.log(Math.E), 1, 0.001);
    assertEquals(Math.log(10), 2.30258, 0.001);
}
```

### Примеры использования

```java
double ln_e = Math.log(Math.E);        // 1.0
double ln_10 = Math.log(10);           // ≈ 2.302585
double ln_1 = Math.log(1);             // 0.0
```

### Связь с экспонентой

```java
double x = 5.0;
double ln_x = Math.log(x);
double exp_ln_x = Math.exp(ln_x);      // ≈ x
```

## Логарифм с пользовательским основанием

Чтобы вычислить логарифм с пользовательской базой в Java, мы используем следующее тождество:

```
log_b(x) = log(x) / log(b)
```

Где log может быть натуральным логарифмом или логарифмом по любому другому основанию.

### Реализация

```java
@Test
public void givenCustomLog_shouldReturnValidResults() {
    assertEquals(customLog(2, 256), 8, 0.001);
    assertEquals(customLog(10, 100), 2, 0.001);
    assertEquals(customLog(3, 81), 4, 0.001);
}

private static double customLog(double base, double logNumber) {
    return Math.log(logNumber) / Math.log(base);
}
```

### Альтернативная реализация с использованием log10

```java
private static double customLogWithLog10(double base, double logNumber) {
    return Math.log10(logNumber) / Math.log10(base);
}
```

### Улучшенная версия с проверками

```java
public static double customLogSafe(double base, double logNumber) {
    if (base <= 0 || base == 1) {
        throw new IllegalArgumentException("Base must be positive and not equal to 1");
    }
    if (logNumber <= 0) {
        throw new IllegalArgumentException("Logarithm is not defined for non-positive numbers");
    }
    return Math.log(logNumber) / Math.log(base);
}
```

## Сложность

### Временная сложность

- **Math.log() / Math.log10():** O(1) - константное время (аппроксимация)
- **customLog():** O(1) - два вызова log, константное время

### Пространственная сложность

- **Все методы:** O(1) - только константная память

## Особенности

- **Точность:** Результаты могут иметь небольшие ошибки округления
- **Особые случаи:** log(0) = -∞, log(1) = 0, log(∞) = ∞
- **Производительность:** Math.log() и Math.log10() оптимизированы

## Применение

Логарифмы используются в:

- Научных вычислениях
- Алгоритмах (бинарный поиск, сложность)
- Финансовых расчетах
- Обработке сигналов
- Машинном обучении

## Варианты задачи

### Вариант 1: Логарифм по основанию 2

```java
public static double log2(double x) {
    return Math.log(x) / Math.log(2);
}
```

### Вариант 2: Дискретный логарифм (целочисленный)

```java
public static int discreteLog2(int x) {
    if (x <= 0) {
        throw new IllegalArgumentException("x must be positive");
    }
    return 31 - Integer.numberOfLeadingZeros(x);
}
```

### Вариант 3: Логарифмическая шкала

```java
public static double toLogScale(double value, double base) {
    return Math.log(value) / Math.log(base);
}

public static double fromLogScale(double logValue, double base) {
    return Math.pow(base, logValue);
}
```

## Kotlin Implementation

### Десятичный логарифм

```kotlin
fun log10K(x: Double): Double {
    return Math.log10(x)
}

fun log10SafeK(x: Double): Double {
    if (x <= 0) {
        throw IllegalArgumentException("Logarithm is not defined for non-positive numbers")
    }
    return Math.log10(x)
}
```

### Натуральный логарифм

```kotlin
fun naturalLogK(x: Double): Double {
    return Math.log(x)
}

fun naturalLogE(): Double {
    return Math.log(Math.E) // 1.0
}
```

### Логарифм с пользовательским основанием

```kotlin
fun customLogK(base: Double, logNumber: Double): Double {
    return Math.log(logNumber) / Math.log(base)
}

fun customLogWithLog10K(base: Double, logNumber: Double): Double {
    return Math.log10(logNumber) / Math.log10(base)
}

fun customLogSafeK(base: Double, logNumber: Double): Double {
    if (base <= 0 || base == 1.0) {
        throw IllegalArgumentException("Base must be positive and not equal to 1")
    }
    if (logNumber <= 0) {
        throw IllegalArgumentException("LogNumber must be positive")
    }
    return Math.log(logNumber) / Math.log(base)
}
```

### Логарифм по основанию 2

```kotlin
fun log2K(x: Double): Double {
    return Math.log(x) / Math.log(2.0)
}

fun log2IntegerK(x: Int): Int {
    if (x <= 0) {
        throw IllegalArgumentException("x must be positive")
    }
    return 31 - Integer.numberOfLeadingZeros(x)
}
```

### Пример использования

```kotlin
fun main() {
    println(Math.log10(100.0)) // 2.0
    println(Math.log10(1000.0)) // 3.0
    
    println(Math.log(Math.E)) // 1.0
    println(Math.log(10.0)) // ≈ 2.302585
    
    println(customLogK(2.0, 256.0)) // 8.0
    println(customLogK(10.0, 100.0)) // 2.0
    
    println(log2K(8.0)) // 3.0
    println(log2IntegerK(8)) // 3
}
```

## Когда использовать

### Используйте Math.log10(), когда:

- Работаете с десятичными логарифмами
- Нужна производительность
- Основание 10

### Используйте Math.log(), когда:

- Работаете с натуральными логарифмами
- Нужна производительность
- Основание e

### Используйте customLog(), когда:

- Нужно произвольное основание
- Гибкость важна
- Производительность не критична

## Заключение

В этом уроке мы узнали, как вычислять логарифмы в Java. Мы рассмотрели десятичные логарифмы с помощью Math.log10(), натуральные логарифмы с помощью Math.log(), и логарифмы с пользовательским основанием, используя формулу изменения основания.
