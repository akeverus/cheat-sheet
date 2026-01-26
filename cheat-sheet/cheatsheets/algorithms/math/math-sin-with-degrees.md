# Math.sin with Degrees

Кратко: вычисление значений синуса с помощью функции Java Math.sin() и преобразование значений углов между градусами и радианами. Рассматривается использование Math.toRadians() и Math.toDegrees().

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.sin() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#sin-double-)
- [Java Math.toRadians() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#toRadians-double-)

### См. также
- `./circle-area-calculation.md` - вычисление площади круга
- `./distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы рассмотрим, как вычислять значения синуса с помощью функции Java Math.sin() и как преобразовывать значения углов между градусами и радианами.

По умолчанию библиотека Java Math ожидает, что значения ее тригонометрических функций будут в радианах.

### Радианы и градусы

Напоминаем, что радианы - это просто еще один способ выразить меру угла, а преобразование:

```
радианы = градусы × π / 180
градусы = радианы × 180 / π
```

Java упрощает это с помощью `toRadians` и `toDegrees`:

```java
double inRadians = Math.toRadians(inDegrees);
double inDegrees = Math.toDegrees(inRadians);
```

### Примеры

- 0° = 0 радиан
- 90° = π/2 радиан ≈ 1.5708
- 180° = π радиан ≈ 3.1416
- 360° = 2π радиан ≈ 6.2832

## Java Implementation

### Преобразование градусов в радианы

Всякий раз, когда мы используем любую из тригонометрических функций Java, мы должны сначала подумать о том, что является единицей нашего ввода.

Мы можем увидеть этот принцип в действии, взглянув на метод Math.sin, один из многих, которые предоставляет Java:

```java
public static double sin(double a)
```

Это эквивалентно математической функции синуса, и он ожидает, что его ввод будет в радианах.

### Примеры преобразования

```java
// Преобразование градусов в радианы
double degrees30 = 30;
double radians30 = Math.toRadians(degrees30);  // ≈ 0.5236

double degrees45 = 45;
double radians45 = Math.toRadians(degrees45);  // ≈ 0.7854

double degrees90 = 90;
double radians90 = Math.toRadians(degrees90);  // ≈ 1.5708
```

### Преобразование радиан в градусы

```java
// Преобразование радиан в градусы
double piRadians = Math.PI;
double degrees = Math.toDegrees(piRadians);  // 180.0

double halfPi = Math.PI / 2;
double degrees90 = Math.toDegrees(halfPi);  // 90.0
```

## Вычисление синуса

Итак, предположим, что у нас есть угол, который, как мы знаем, выражается в градусах:

```java
double inDegrees = 30;
```

Сначала нам нужно преобразовать его в радианы:

```java
double inRadians = Math.toRadians(inDegrees);
```

И тогда мы можем вычислить значение синуса:

```java
double sine = Math.sin(inRadians);
```

Но если мы знаем, что это уже в радианах, то нам не нужно делать преобразование:

```java
@Test
public void givenAnAngleInDegrees_whenUsingToRadians_thenResultIsInRadians() {
    double angleInDegrees = 30;
    double sinForDegrees = Math.sin(Math.toRadians(angleInDegrees));
    
    double thirtyDegreesInRadians = 1.0/6 * Math.PI;
    double sinForRadians = Math.sin(thirtyDegreesInRadians);
    
    assertTrue(sinForDegrees == sinForRadians);
}
```

Поскольку значение `thirtyDegreesInRadians` уже было в радианах, нам не нужно было сначала преобразовывать его, чтобы получить тот же результат.

### Примеры вычисления синуса

```java
// Синус 0 градусов
double sin0 = Math.sin(Math.toRadians(0));      // 0.0

// Синус 30 градусов
double sin30 = Math.sin(Math.toRadians(30));    // 0.5

// Синус 45 градусов
double sin45 = Math.sin(Math.toRadians(45));    // ≈ 0.7071

// Синус 90 градусов
double sin90 = Math.sin(Math.toRadians(90));    // 1.0

// Синус 180 градусов
double sin180 = Math.sin(Math.toRadians(180));  // 0.0
```

### Вспомогательный метод

```java
public static double sinDegrees(double degrees) {
    return Math.sin(Math.toRadians(degrees));
}
```

## Другие тригонометрические функции

### Косинус

```java
public static double cosDegrees(double degrees) {
    return Math.cos(Math.toRadians(degrees));
}
```

### Тангенс

```java
public static double tanDegrees(double degrees) {
    return Math.tan(Math.toRadians(degrees));
}
```

### Обратные функции

```java
// Арксинус (возвращает радианы)
public static double asinDegrees(double value) {
    return Math.toDegrees(Math.asin(value));
}

// Арккосинус (возвращает радианы)
public static double acosDegrees(double value) {
    return Math.toDegrees(Math.acos(value));
}

// Арктангенс (возвращает радианы)
public static double atanDegrees(double value) {
    return Math.toDegrees(Math.atan(value));
}
```

### Примеры использования

```java
// Косинус 60 градусов
double cos60 = Math.cos(Math.toRadians(60));    // 0.5

// Тангенс 45 градусов
double tan45 = Math.tan(Math.toRadians(45));    // 1.0

// Арксинус 0.5 (в градусах)
double asin05 = Math.toDegrees(Math.asin(0.5)); // 30.0
```

## Сложность

### Временная сложность

- **Math.sin() / Math.cos() / Math.tan():** O(1) - константное время (аппроксимация)
- **Math.toRadians() / Math.toDegrees():** O(1) - простое умножение/деление

### Пространственная сложность

- **Все методы:** O(1) - только константная память

## Особенности

- **Точность:** Результаты могут иметь небольшие ошибки округления
- **Диапазон:** Math.sin() работает с любыми значениями (включая отрицательные)
- **Производительность:** Оптимизированные нативные функции

## Применение

Тригонометрические функции используются в:

- Компьютерной графике
- Физике и инженерии
- Играх
- Навигации
- Обработке сигналов

## Варианты задачи

### Вариант 1: Вычисление всех тригонометрических функций

```java
public class Trigonometry {
    public static double sinDegrees(double degrees) {
        return Math.sin(Math.toRadians(degrees));
    }
    
    public static double cosDegrees(double degrees) {
        return Math.cos(Math.toRadians(degrees));
    }
    
    public static double tanDegrees(double degrees) {
        return Math.tan(Math.toRadians(degrees));
    }
    
    public static double cotDegrees(double degrees) {
        return 1.0 / tanDegrees(degrees);
    }
    
    public static double secDegrees(double degrees) {
        return 1.0 / cosDegrees(degrees);
    }
    
    public static double cscDegrees(double degrees) {
        return 1.0 / sinDegrees(degrees);
    }
}
```

### Вариант 2: Преобразование углов

```java
public class AngleConverter {
    public static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }
    
    public static double radiansToDegrees(double radians) {
        return Math.toDegrees(radians);
    }
    
    public static double normalizeAngle(double degrees) {
        degrees = degrees % 360;
        if (degrees < 0) {
            degrees += 360;
        }
        return degrees;
    }
}
```

## Kotlin Implementation

### Преобразование градусов в радианы

```kotlin
fun degreesToRadiansK(degrees: Double): Double {
    return Math.toRadians(degrees)
}

fun radiansToDegreesK(radians: Double): Double {
    return Math.toDegrees(radians)
}

// Примеры преобразования
fun main() {
    val degrees30 = 30.0
    val radians30 = Math.toRadians(degrees30) // ≈ 0.5236
    
    val piRadians = Math.PI
    val degrees = Math.toDegrees(piRadians) // 180.0
}
```

### Вычисление синуса

```kotlin
fun sinDegreesK(degrees: Double): Double {
    return Math.sin(Math.toRadians(degrees))
}

// Примеры вычисления синуса
fun sinExamplesK() {
    val sin0 = Math.sin(Math.toRadians(0.0))      // 0.0
    val sin30 = Math.sin(Math.toRadians(30.0))    // 0.5
    val sin45 = Math.sin(Math.toRadians(45.0))    // ≈ 0.7071
    val sin90 = Math.sin(Math.toRadians(90.0))    // 1.0
    val sin180 = Math.sin(Math.toRadians(180.0))  // 0.0
}
```

### Другие тригонометрические функции

```kotlin
object TrigonometryK {
    fun sinDegrees(degrees: Double): Double {
        return Math.sin(Math.toRadians(degrees))
    }
    
    fun cosDegrees(degrees: Double): Double {
        return Math.cos(Math.toRadians(degrees))
    }
    
    fun tanDegrees(degrees: Double): Double {
        return Math.tan(Math.toRadians(degrees))
    }
    
    fun cotDegrees(degrees: Double): Double {
        return 1.0 / tanDegrees(degrees)
    }
    
    fun secDegrees(degrees: Double): Double {
        return 1.0 / cosDegrees(degrees)
    }
    
    fun cscDegrees(degrees: Double): Double {
        return 1.0 / sinDegrees(degrees)
    }
}
```

### Преобразование и нормализация углов

```kotlin
object AngleConverterK {
    fun degreesToRadians(degrees: Double): Double {
        return Math.toRadians(degrees)
    }
    
    fun radiansToDegrees(radians: Double): Double {
        return Math.toDegrees(radians)
    }
    
    fun normalizeAngle(degrees: Double): Double {
        var angle = degrees % 360
        if (angle < 0) {
            angle += 360
        }
        return angle
    }
}
```

### Пример использования

```kotlin
fun main() {
    val angle = 30.0
    val sinValue = TrigonometryK.sinDegrees(angle)
    println("sin($angle°) = $sinValue") // sin(30.0°) = 0.5
    
    val normalized = AngleConverterK.normalizeAngle(-45.0)
    println("Normalized angle: $normalized") // 315.0
}
```

## Когда использовать

### Используйте Math.toRadians(), когда:

- Угол задан в градусах
- Нужно использовать тригонометрические функции
- Важна читаемость кода

### Используйте прямые радианы, когда:

- Угол уже в радианах
- Нужна максимальная производительность
- Работаете с математическими константами (π, π/2)

## Заключение

В этой быстрой статье мы рассмотрели радианы и градусы, а затем увидели пример того, как работать с ними с помощью Math.sin. Важно всегда помнить о единицах измерения углов при работе с тригонометрическими функциями в Java.
