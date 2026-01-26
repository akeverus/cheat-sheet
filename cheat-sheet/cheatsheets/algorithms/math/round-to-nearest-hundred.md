# Round to Nearest Hundred

Кратко: округление заданного числа до ближайшей сотни. Рассматривается реализация с использованием Math.ceil() и целочисленного деления.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.ceil() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#ceil-double-)

### См. также
- `./circle-area-calculation.md` - вычисление площади круга
- `./distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Варианты задачи](#варианты-задачи)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы покажем, как округлить заданное число до ближайшей сотни.

### Примеры

- 99 → 100
- 200.2 → 300
- 400 → 400
- 150 → 200
- 149 → 100

## Java Implementation

### Реализация

Во-первых, мы собираемся вызвать `Math.ceil()` для входного параметра. `Math.ceil()` возвращает наименьшее целое число, которое больше или равно аргументу. Например, если введено значение 200.2, `Math.ceil()` вернет 201.

Затем мы добавляем 99 к результату и делим на 100. Мы используем целочисленное деление, чтобы усечь десятичную часть частного. Наконец, мы умножаем частное на 100, чтобы получить желаемый результат.

Вот наша реализация:

```java
static long round(double input) {
    long i = (long) Math.ceil(input);
    return ((i + 99) / 100) * 100;
}
```

Проверим реализацию:

```java
@Test
public void givenInput_whenRound_thenRoundUpToTheNearestHundred() {
    assertEquals("Rounded up to hundred", 100, RoundUpToHundred.round(99));
    assertEquals("Rounded up to three hundred", 300, RoundUpToHundred.round(200.2));
    assertEquals("Returns same rounded value", 400, RoundUpToHundred.round(400));
}
```

### Альтернативная реализация с Math.round()

```java
static long roundAlternative(double input) {
    return Math.round(input / 100.0) * 100;
}
```

### Реализация с округлением вниз

```java
static long roundDown(double input) {
    long i = (long) Math.floor(input);
    return (i / 100) * 100;
}
```

### Реализация с округлением до ближайшего

```java
static long roundNearest(double input) {
    return Math.round(input / 100.0) * 100;
}
```

## Варианты задачи

### Вариант 1: Округление до произвольного разряда

```java
static long roundToNearest(long input, int place) {
    long multiplier = (long) Math.pow(10, place);
    return Math.round((double) input / multiplier) * multiplier;
}
```

### Вариант 2: Округление до ближайшей тысячи

```java
static long roundToNearestThousand(double input) {
    long i = (long) Math.ceil(input);
    return ((i + 999) / 1000) * 1000;
}
```

### Вариант 3: Округление с указанием направления

```java
public enum RoundDirection {
    UP, DOWN, NEAREST
}

static long roundWithDirection(double input, RoundDirection direction) {
    switch (direction) {
        case UP:
            long i = (long) Math.ceil(input);
            return ((i + 99) / 100) * 100;
        case DOWN:
            long j = (long) Math.floor(input);
            return (j / 100) * 100;
        case NEAREST:
            return Math.round(input / 100.0) * 100;
        default:
            return Math.round(input / 100.0) * 100;
    }
}
```

## Kotlin Implementation

### Основная реализация

```kotlin
fun roundK(input: Double): Long {
    val i = Math.ceil(input).toLong()
    return ((i + 99) / 100) * 100
}

fun roundAlternativeK(input: Double): Long {
    return Math.round(input / 100.0) * 100
}

fun roundDownK(input: Double): Long {
    val i = Math.floor(input).toLong()
    return (i / 100) * 100
}

fun roundNearestK(input: Double): Long {
    return Math.round(input / 100.0) * 100
}
```

### Варианты задачи

```kotlin
fun roundToNearestK(input: Long, place: Int): Long {
    val multiplier = Math.pow(10.0, place.toDouble()).toLong()
    return Math.round(input.toDouble() / multiplier) * multiplier
}

fun roundToNearestThousandK(input: Double): Long {
    val i = Math.ceil(input).toLong()
    return ((i + 999) / 1000) * 1000
}
```

### Округление с указанием направления

```kotlin
enum class RoundDirectionK {
    UP, DOWN, NEAREST
}

fun roundWithDirectionK(input: Double, direction: RoundDirectionK): Long {
    return when (direction) {
        RoundDirectionK.UP -> {
            val i = Math.ceil(input).toLong()
            ((i + 99) / 100) * 100
        }
        RoundDirectionK.DOWN -> {
            val i = Math.floor(input).toLong()
            (i / 100) * 100
        }
        RoundDirectionK.NEAREST -> {
            Math.round(input / 100.0) * 100
        }
    }
}
```

### Пример использования

```kotlin
fun main() {
    println(roundK(99.0)) // 100
    println(roundK(200.2)) // 300
    println(roundK(400.0)) // 400
    println(roundNearestK(150.0)) // 200
    println(roundNearestK(149.0)) // 100
}
```

## Сложность

### Временная сложность

- **Все подходы:** O(1) - константное время

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Простота:** Алгоритм прост и понятен
- **Эффективность:** Выполняется за константное время
- **Гибкость:** Легко адаптировать для других разрядов

## Применение

Округление до ближайшей сотни используется в:

- Финансовых расчетах
- Статистике
- Отчетности
- Визуализации данных
- Обработке данных

## Когда использовать

### Используйте Math.ceil() подход, когда:

- Нужно округление вверх
- Простота важна
- Работаете с положительными числами

### Используйте Math.round() подход, когда:

- Нужно округление до ближайшего
- Важна точность
- Работаете с любыми числами

## Заключение

В этой быстрой статье мы показали, как округлить число до ближайшей сотни. Алгоритм использует Math.ceil() и целочисленное деление для достижения результата.
