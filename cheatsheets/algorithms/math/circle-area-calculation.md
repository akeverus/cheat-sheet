---
title: "Вычисление площади круга (Circle Area Calculation)"
description: "Вычисление площади круга по формуле πr² в Java: простая функция, класс Circle, валидация входа и округление. Рассмотрены варианты: кольцо, сектор, сегмент, радиус по площади."
tags:
  - algorithms
  - math
  - circle-area-calculation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Вычисление площади круга (Circle Area Calculation)

Вычисление площади круга по формуле πr² в `Java`: простая функция, класс `Circle`, валидация входа и округление. Рассмотрены варианты: кольцо, сектор, сегмент, радиус по площади.

## Полезные ссылки

### Официальная документация
- [Math.PI (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#PI)

### См. также
- [Вычисление расстояния между точками](distance-between-points.md) — расстояние между точками
- [Пересечение прямоугольников](rectangle-overlap.md) — перекрытие прямоугольников

- [Пересечение прямых (Line Intersection)](line-intersection.md)
- [Взаимно простые числа (Coprime Numbers)](coprime-numbers.md)
- [Преобразование широты и долготы в 2D-точку (Lat/Lon to 2D Point)](lat-lon-to-2d-point.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическая формула](#математическая-формула)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Простая функция (Java)](#подход-1-простая-функция-java)
  - [Версия с использованием Math.pow()](#версия-с-использованием-mathpow)
- [Подход 2: Класс Circle](#подход-2-класс-circle)
  - [Дополнительные методы класса Circle](#дополнительные-методы-класса-circle)
- [Обработка входных данных](#обработка-входных-данных)
  - [Валидация радиуса](#валидация-радиуса)
  - [Округление результата](#округление-результата)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Площадь кольца](#вариант-1-площадь-кольца)
  - [Вариант 2: Площадь сектора круга](#вариант-2-площадь-сектора-круга)
  - [Вариант 3: Площадь сегмента круга](#вариант-3-площадь-сегмента-круга)
  - [Вариант 4: Радиус по площади](#вариант-4-радиус-по-площади)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 1: Простая функция (Kotlin)](#подход-1-простая-функция-kotlin)
  - [Подход 2: Класс Circle](#подход-2-класс-circle-1)
  - [Варианты задачи](#варианты-задачи-1)
  - [Пример использования](#пример-использования)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Operational context в production](#operational-context-в-production)
- [Trade-offs точности и производительности](#trade-offs-точности-и-производительности)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Площадь круга вычисляется по формуле A = πr², где A — площадь, π ≈ 3.14159, r — радиус. В `Java` используют `Math.PI` и либо умножение радиуса на себя, либо `Math.pow(radius, 2)`.

### Математическая формула

A = πr². A — площадь круга, π — константа (в коде `Math.PI`), r — радиус.

## Реализация на Java

### Подход 1: Простая функция (Java)

Базовый вариант: умножить радиус на себя и на `Math.PI`. Ниже — метод с выводом в консоль и вариант с возвратом значения и проверкой радиуса.

```java
// Вычисление площади круга по формуле πr² с выводом в консоль
private void calculateArea(double radius) {
    double area = radius * radius * Math.PI;
    System.out.println("The area of the circle [radius = " + radius + "]: " + area);
}
```

Чтение радиуса из аргумента командной строки и вызов: `double radius = Double.parseDouble(args[0]); calculateArea(radius);`. Запуск: `javac CircleArea.java && java CircleArea 7`. Вывод будет вида: площадь для радиуса 7.0 ≈ 153.94.

Улучшенная версия с проверкой радиуса и возвратом значения:

```java
// Валидация радиуса и возврат площади
public double calculateArea(double radius) {
    if (radius < 0) {
        throw new IllegalArgumentException("Radius cannot be negative");
    }
    return radius * radius * Math.PI;
}
```

### Версия с использованием Math.pow()

```java
// Вариант через Math.pow(radius, 2)
public double calculateAreaWithPow(double radius) {
    return Math.PI * Math.pow(radius, 2);
}
```

## Подход 2: Класс Circle

Радиус можно получать из ввода пользователя (`Scanner`) или передавать в конструктор. Класс `Circle` инкапсулирует радиус и вычисление площади; площадь не хранится отдельно, а считается по радиусу. Метод вычисления площади делают приватным, чтобы `toString()` не зависел от переопределяемых публичных методов.

```java
// Класс Circle: радиус, валидация в конструкторе и setter, площадь вычисляется по радиусу
public class Circle {
    private double radius;

    public Circle(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        this.radius = radius;
    }

    private double calculateArea() {
        return radius * radius * Math.PI;
    }

    public double getArea() {
        return calculateArea();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "The area of the circle [radius = " + radius + "]: " + calculateArea();
    }
}
```

Создание экземпляра и вывод:

```java
Circle circle = new Circle(7);
System.out.println(circle);
```

### Дополнительные методы класса Circle

```java
// Длина окружности, диаметр и сравнение по радиусу
public double getCircumference() {
    return 2 * Math.PI * radius;
}

public double getDiameter() {
    return 2 * radius;
}

public boolean equals(Circle other) {
    return Double.compare(this.radius, other.radius) == 0;
}
```

## Обработка входных данных

### Валидация радиуса

Проверка на отрицательный, NaN и бесконечный радиус:

```java
// Проверка радиуса перед вычислением
public double calculateAreaSafe(double radius) {
    if (radius < 0) {
        throw new IllegalArgumentException("Radius must be non-negative");
    }
    if (Double.isNaN(radius) || Double.isInfinite(radius)) {
        throw new IllegalArgumentException("Radius must be a finite number");
    }
    return Math.PI * radius * radius;
}
```

### Округление результата

Округление до заданного числа знаков после запятой:

```java
// Округление площади до decimalPlaces знаков
public double calculateAreaRounded(double radius, int decimalPlaces) {
    double area = Math.PI * radius * radius;
    double scale = Math.pow(10, decimalPlaces);
    return Math.round(area * scale) / scale;
}
```

## Варианты задачи

### Вариант 1: Площадь кольца

Площадь кольца = π(R² − r²), где R — внешний, r — внутренний радиус.

```java
// Площадь кольца: π(outer² − inner²)
public double calculateRingArea(double outerRadius, double innerRadius) {
    if (outerRadius <= innerRadius) {
        throw new IllegalArgumentException("Outer radius must be greater than inner radius");
    }
    return Math.PI * (outerRadius * outerRadius - innerRadius * innerRadius);
}
```

### Вариант 2: Площадь сектора круга

Сектор: (α/2)r² в радианах, угол переводится из градусов через `Math.toRadians`.

```java
// Площадь сектора: 0.5 * r² * угол в радианах
public double calculateSectorArea(double radius, double angleInDegrees) {
    double angleInRadians = Math.toRadians(angleInDegrees);
    return 0.5 * radius * radius * angleInRadians;
}
```

### Вариант 3: Площадь сегмента круга

Сегмент: площадь сектора минус площадь треугольника (вершина в центре); угол переводим в радианы.

```java
// Площадь сегмента = площадь сектора − площадь треугольника
public double calculateSegmentArea(double radius, double angleInDegrees) {
    double angleInRadians = Math.toRadians(angleInDegrees);
    double sectorArea = 0.5 * radius * radius * angleInRadians;
    double triangleArea = 0.5 * radius * radius * Math.sin(angleInRadians);
    return sectorArea - triangleArea;
}
```

### Вариант 4: Радиус по площади

Обратная задача: r = √(A/π).

```java
// Радиус по площади: sqrt(area / π)
public double calculateRadiusFromArea(double area) {
    if (area < 0) {
        throw new IllegalArgumentException("Area cannot be negative");
    }
    return Math.sqrt(area / Math.PI);
}
```

## Реализация на Kotlin

### Подход 1: Простая функция (Kotlin)

```kotlin
// Площадь с проверкой радиуса и вариант через Math.pow
fun calculateAreaK(radius: Double): Double {
    if (radius < 0) {
        throw IllegalArgumentException("Radius cannot be negative")
    }
    return radius * radius * Math.PI
}

fun calculateAreaWithPowK(radius: Double): Double {
    return Math.PI * Math.pow(radius, 2.0)
}
```

### Подход 2: Класс Circle

```kotlin
// Класс Circle с валидацией в init и setRadius
class CircleK(private var radius: Double) {
    init {
        if (radius < 0) {
            throw IllegalArgumentException("Radius cannot be negative")
        }
    }

    fun getArea(): Double {
        return radius * radius * Math.PI
    }

    fun getRadius(): Double = radius

    fun setRadius(newRadius: Double) {
        if (newRadius < 0) {
            throw IllegalArgumentException("Radius cannot be negative")
        }
        radius = newRadius
    }

    override fun toString(): String {
        return "The area of the circle [radius = $radius]: ${getArea()}"
    }
}
```

### Варианты задачи

```kotlin
// Кольцо, сектор, сегмент, радиус по площади
fun calculateRingAreaK(outerRadius: Double, innerRadius: Double): Double {
    if (outerRadius <= innerRadius) {
        throw IllegalArgumentException("Outer radius must be greater than inner radius")
    }
    return Math.PI * (outerRadius * outerRadius - innerRadius * innerRadius)
}

fun calculateSectorAreaK(radius: Double, angleInDegrees: Double): Double {
    val angleInRadians = Math.toRadians(angleInDegrees)
    return 0.5 * radius * radius * angleInRadians
}

fun calculateSegmentAreaK(radius: Double, angleInDegrees: Double): Double {
    val angleInRadians = Math.toRadians(angleInDegrees)
    val sectorArea = 0.5 * radius * radius * angleInRadians
    val triangleArea = 0.5 * radius * radius * Math.sin(angleInRadians)
    return sectorArea - triangleArea
}

fun calculateRadiusFromAreaK(area: Double): Double {
    if (area < 0) {
        throw IllegalArgumentException("Area cannot be negative")
    }
    return Math.sqrt(area / Math.PI)
}
```

### Пример использования

```kotlin
fun main() {
    val radius = 7.0
    val area = calculateAreaK(radius)
    println("Area: $area")

    val circle = CircleK(radius)
    println(circle)

    val ringArea = calculateRingAreaK(10.0, 5.0)
    println("Ring area: $ringArea")
}
```

## Сложность

Время и память — константа `O(1)`: одно умножение и использование `Math.PI`.

## Особенности

Формула проста; `Math.PI` даёт достаточную точность для большинства задач. Для денег или строгих норм можно использовать `BigDecimal` и задать π с нужной точностью.

## Применение

Площадь круга нужна в геометрии, компьютерной графике, инженерии, физике и играх — везде, где фигурируют окружности, кольца или секторы.

## Когда использовать

Простую функцию достаточно при разовом расчёте без инкапсуляции. Класс `Circle` удобен при нескольких операциях с одним кругом (площадь, длина окружности, диаметр) и при ООП-модели.

## Лучшие практики

Проверяйте, что радиус не отрицательный (и при необходимости не NaN/бесконечность), при ошибке бросайте `IllegalArgumentException`. Используйте `Math.PI`, а не собственное значение π. Для финансов или строгих стандартов рассмотрите `BigDecimal`. В тестах покрывайте радиус 0, большие значения и отрицательный радиус (ожидание исключения).

Минимальная матрица тестов:
- `radius = 0` (ожидаем площадь 0),
- `radius > 0` типовые значения,
- `radius < 0` (ожидаем исключение),
- `NaN`/`Infinity` (ожидаем исключение),
- very large radius (проверка overflow-поведения).

## Operational context в production

Даже простой расчет площади может быть частью критичного потока:
- биллинг или тарификация,
- гео-аналитика и картографические сервисы,
- CAD/графические и инженерные расчеты.

Что важно в эксплуатации:
- фиксировать требуемую точность результата,
- явно выбирать стратегию округления,
- логировать входные значения при ошибках валидации,
- мониторить долю ошибок валидации как отдельную метрику качества входных данных.

## Trade-offs точности и производительности

- `double` быстрее и подходит для большинства прикладных задач.
- `BigDecimal` медленнее, но дает контролируемую точность и округление.
- `Math.pow(radius, 2)` обычно медленнее, чем `radius * radius`, но может быть удобнее при параметризуемых формулах.

Практическое правило:
- инженерные/финансовые требования к точности -> `BigDecimal`,
- высоконагруженный поток с умеренными требованиями -> `double`.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Отрицательная площадь | Отрицательный радиус не проверяется | Валидировать радиус, при radius < 0 бросать исключение |
| NaN или Infinity в результате | NaN/Infinite радиус или переполнение | Проверять вход и при необходимости использовать BigDecimal |
| Неточный результат для денег | Использование double | Считать в BigDecimal с нужным масштабом |
| Исключение при radius = 0 | Логика считает 0 недопустимым | Радиус 0 допустим (площадь 0), при необходимости явно обработать |

Короткий диагностический чеклист:
1. проверить вход (`radius`) на валидность и диапазон,
2. проверить требуемую точность домена,
3. сравнить результат с эталонным расчетом,
4. проверить единицы измерения (мм, см, м),
5. убедиться, что округление применяется один раз в правильной точке.

## Частые вопросы

**Почему не хранить площадь в поле класса Circle?** Площадь однозначно определяется радиусом. Хранение только радиуса избегает рассинхрона и дублирования; при изменении радиуса площадь пересчитывается автоматически.

**Когда использовать Math.pow(radius, 2) вместо radius * radius?** Оба варианта дают O(1). Умножение `radius * radius` обычно быстрее; `Math.pow` удобен, если степень выносится в параметр (например, общая формула для разных степеней).

**Как округлить площадь до N знаков?** Умножить на 10^N, вызвать `Math.round`, разделить на 10^N. Либо использовать `BigDecimal.setScale(decimalPlaces, RoundingMode.HALF_UP)`.

## Заключение

В документе разобраны вычисление площади круга в `Java` по формуле πr²: простая функция с валидацией, класс `Circle`, варианты (кольцо, сектор, сегмент, радиус по площади), обработка ввода и округление. Добавлены эксплуатационный контекст, trade-offs точности и диагностический чеклист для production-практики. Аналоги приведены на `Kotlin`.
