# Circle Area Calculation

Кратко: вычисление площади круга в Java с использованием формулы πr². Рассматриваются различные подходы: простая функция, класс Circle, и обработка входных данных.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.PI Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#PI)

### См. также
- `./distance-between-points.md` - вычисление расстояния между точками
- `./rectangle-overlap.md` - пересечение прямоугольников

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Варианты задачи](#варианты-задачи)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы покажем, как вычислить площадь круга в Java.

Мы будем использовать известную математическую формулу: r² * π.

### Математическая формула

Площадь круга вычисляется по формуле:

```
A = πr²
```

Где:
- A - площадь круга
- π (пи) ≈ 3.14159...
- r - радиус круга

## Java Implementation

### Подход 1: Простая функция

Давайте сначала создадим метод, который будет выполнять вычисления:

```java
private void calculateArea(double radius) {
    double area = radius * radius * Math.PI;
    System.out.println("The area of the circle [radius = " + radius + "]: " + area);
}
```

Теперь мы можем прочитать аргумент командной строки и вычислить площадь:

```java
double radius = Double.parseDouble(args[0]);
calculateArea(radius);
```

Когда мы компилируем и запускаем программу:

```bash
javac CircleArea.java
java CircleArea 7
```

мы получим следующий вывод:

```
The area of the circle [radius = 7.0]: 153.93804002589985
```

### Улучшенная версия с возвратом значения

```java
public double calculateArea(double radius) {
    if (radius < 0) {
        throw new IllegalArgumentException("Radius cannot be negative");
    }
    return radius * radius * Math.PI;
}
```

### Версия с использованием Math.pow()

```java
public double calculateAreaWithPow(double radius) {
    return Math.PI * Math.pow(radius, 2);
}
```

## Подход 2: Класс Circle

Другой способ получить значение радиуса - использовать входные данные от пользователя:

```java
Scanner sc = new Scanner(System.in);
System.out.println("Please enter radius value: ");
double radius = sc.nextDouble();
calculateArea(radius);
```

Вывод такой же, как и в предыдущем примере.

Помимо вызова метода для вычисления площади, как мы видели в разделе 2, мы также можем создать класс, представляющий круг:

```java
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

Мы должны отметить несколько вещей. Прежде всего, мы не сохраняем площадь как переменную, так как она напрямую зависит от радиуса, поэтому мы можем легко ее вычислить. Во-вторых, метод вычисления площади является приватным, поскольку мы используем его в методе toString(). Метод toString() не должен вызывать какие-либо общедоступные методы класса, поскольку эти методы могут быть переопределены, и их поведение будет отличаться от ожидаемого.

Теперь мы можем создать экземпляр нашего объекта Circle:

```java
Circle circle = new Circle(7);
System.out.println(circle);
```

Вывод будет, конечно, таким же, как и раньше.

### Дополнительные методы класса Circle

```java
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

```java
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

```java
public double calculateAreaRounded(double radius, int decimalPlaces) {
    double area = Math.PI * radius * radius;
    double scale = Math.pow(10, decimalPlaces);
    return Math.round(area * scale) / scale;
}
```

## Варианты задачи

### Вариант 1: Площадь кольца

```java
public double calculateRingArea(double outerRadius, double innerRadius) {
    if (outerRadius <= innerRadius) {
        throw new IllegalArgumentException("Outer radius must be greater than inner radius");
    }
    return Math.PI * (outerRadius * outerRadius - innerRadius * innerRadius);
}
```

### Вариант 2: Площадь сектора круга

```java
public double calculateSectorArea(double radius, double angleInDegrees) {
    double angleInRadians = Math.toRadians(angleInDegrees);
    return 0.5 * radius * radius * angleInRadians;
}
```

### Вариант 3: Площадь сегмента круга

```java
public double calculateSegmentArea(double radius, double angleInDegrees) {
    double angleInRadians = Math.toRadians(angleInDegrees);
    double sectorArea = 0.5 * radius * radius * angleInRadians;
    double triangleArea = 0.5 * radius * radius * Math.sin(angleInRadians);
    return sectorArea - triangleArea;
}
```

### Вариант 4: Радиус по площади

```java
public double calculateRadiusFromArea(double area) {
    if (area < 0) {
        throw new IllegalArgumentException("Area cannot be negative");
    }
    return Math.sqrt(area / Math.PI);
}
```

## Kotlin Implementation

### Подход 1: Простая функция

```kotlin
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

### Временная сложность

- **Все подходы:** O(1) - константное время

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Простота:** Формула проста и понятна
- **Точность:** Math.PI обеспечивает высокую точность
- **Производительность:** Вычисление выполняется за константное время

## Применение

Вычисление площади круга используется в:

- Геометрии
- Компьютерной графике
- Инженерии
- Физике
- Играх

## Когда использовать

### Используйте простую функцию, когда:

- Нужна простота
- Одноразовое вычисление
- Не нужна инкапсуляция

### Используйте класс Circle, когда:

- Нужна инкапсуляция
- Множественные операции с кругом
- ООП подход

## Заключение

В этой короткой и точной статье мы показали различные способы вычисления площади круга с помощью Java. Мы рассмотрели простую функцию и класс Circle для инкапсуляции логики.
