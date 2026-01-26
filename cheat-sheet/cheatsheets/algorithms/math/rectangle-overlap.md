# Rectangle Overlap

Кратко: проверка перекрытия двух прямоугольников. Рассматривается алгоритм определения наличия общей точки между двумя прямоугольниками на основе их координат.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Point Documentation](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)

### См. также
- `./line-intersection.md` - поиск пересечения двух линий
- `./distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Представление прямоугольника](#представление-прямоугольника)
- [Алгоритм проверки перекрытия](#алгоритм-проверки-перекрытия)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Варианты задачи](#варианты-задачи)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы научимся решать алгоритмическую задачу проверки того, перекрываются ли два заданных прямоугольника.

Мы начнем с определения проблемы, а затем постепенно создадим решение.

Наконец, мы реализуем это на Java.

### Определение проблемы

Допустим, у нас есть два заданных прямоугольника - r1 и r2. Нам нужно проверить, есть ли хотя бы одна общая точка между r1 и r2. Если да, то это просто означает, что эти два прямоугольника перекрываются.

### Примеры перекрытия

1. **Полное перекрытие:** Один прямоугольник полностью внутри другого
2. **Частичное перекрытие:** Прямоугольники пересекаются по части
3. **Совпадение границ:** Прямоугольники касаются друг друга
4. **Нет перекрытия:** Прямоугольники не имеют общих точек

## Представление прямоугольника

Прямоугольник можно легко представить его координатами слева внизу и справа вверху:

```java
public class Rectangle {
    private Point bottomLeft;
    private Point topRight;
    
    public Rectangle(Point bottomLeft, Point topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }
    
    public Point getBottomLeft() {
        return bottomLeft;
    }
    
    public Point getTopRight() {
        return topRight;
    }
}
```

где Point - это класс, представляющий точку (x, y) в пространстве:

```java
public class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}
```

## Алгоритм проверки перекрытия

Два заданных прямоугольника **не будут перекрываться**, если выполняется одно из следующих условий:

1. Один из двух прямоугольников находится **над** верхним краем другого прямоугольника.
2. Один из двух прямоугольников находится **слева** от левого края другого прямоугольника.

Во всех остальных случаях два прямоугольника будут накладываться друг на друга.

### Математическая формулировка

Для двух прямоугольников:
- r1: bottomLeft(x1, y1), topRight(x2, y2)
- r2: bottomLeft(x3, y3), topRight(x4, y4)

Прямоугольники **не перекрываются**, если:
- r1 находится выше r2: `y2 < y3`
- r1 находится ниже r2: `y1 > y4`
- r1 находится слева от r2: `x2 < x3`
- r1 находится справа от r2: `x1 > x4`

## Java Implementation

### Реализация

Теперь, когда мы поняли решение, давайте реализуем наш метод `isOverlapping()`:

```java
public boolean isOverlapping(Rectangle other) {
    // Проверка по оси Y: один прямоугольник выше другого
    if (this.topRight.getY() < other.bottomLeft.getY() 
        || this.bottomLeft.getY() > other.topRight.getY()) {
        return false;
    }
    
    // Проверка по оси X: один прямоугольник левее другого
    if (this.topRight.getX() < other.bottomLeft.getX() 
        || this.bottomLeft.getX() > other.topRight.getX()) {
        return false;
    }
    
    return true;
}
```

Наш метод `isOverlapping()` в классе Rectangle возвращает `false`, если один из прямоугольников находится выше или слева от другого, в противном случае - `true`.

Чтобы узнать, находится ли один прямоугольник над другим, мы сравниваем их y-координаты. Точно так же мы сравниваем координаты x, чтобы проверить, находится ли один прямоугольник левее другого.

### Полный пример класса

```java
public class Rectangle {
    private Point bottomLeft;
    private Point topRight;
    
    public Rectangle(Point bottomLeft, Point topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }
    
    public boolean isOverlapping(Rectangle other) {
        if (this.topRight.getY() < other.bottomLeft.getY() 
            || this.bottomLeft.getY() > other.topRight.getY()) {
            return false;
        }
        
        if (this.topRight.getX() < other.bottomLeft.getX() 
            || this.bottomLeft.getX() > other.topRight.getX()) {
            return false;
        }
        
        return true;
    }
    
    // Геттеры
    public Point getBottomLeft() {
        return bottomLeft;
    }
    
    public Point getTopRight() {
        return topRight;
    }
}
```

### Пример использования

```java
// Прямоугольник 1: от (0, 0) до (5, 5)
Rectangle r1 = new Rectangle(new Point(0, 0), new Point(5, 5));

// Прямоугольник 2: от (3, 3) до (8, 8) - перекрывается
Rectangle r2 = new Rectangle(new Point(3, 3), new Point(8, 8));

// Прямоугольник 3: от (10, 10) до (15, 15) - не перекрывается
Rectangle r3 = new Rectangle(new Point(10, 10), new Point(15, 15));

System.out.println(r1.isOverlapping(r2)); // true
System.out.println(r1.isOverlapping(r3)); // false
```

## Варианты задачи

### Вариант 1: Вычисление площади перекрытия

```java
public int getOverlapArea(Rectangle other) {
    if (!isOverlapping(other)) {
        return 0;
    }
    
    int overlapX1 = Math.max(this.bottomLeft.getX(), other.bottomLeft.getX());
    int overlapY1 = Math.max(this.bottomLeft.getY(), other.bottomLeft.getY());
    int overlapX2 = Math.min(this.topRight.getX(), other.topRight.getX());
    int overlapY2 = Math.min(this.topRight.getY(), other.topRight.getY());
    
    int width = overlapX2 - overlapX1;
    int height = overlapY2 - overlapY1;
    
    return width * height;
}
```

### Вариант 2: Получение прямоугольника перекрытия

```java
public Optional<Rectangle> getOverlapRectangle(Rectangle other) {
    if (!isOverlapping(other)) {
        return Optional.empty();
    }
    
    int overlapX1 = Math.max(this.bottomLeft.getX(), other.bottomLeft.getX());
    int overlapY1 = Math.max(this.bottomLeft.getY(), other.bottomLeft.getY());
    int overlapX2 = Math.min(this.topRight.getX(), other.topRight.getX());
    int overlapY2 = Math.min(this.topRight.getY(), other.topRight.getY());
    
    Rectangle overlap = new Rectangle(
        new Point(overlapX1, overlapY1),
        new Point(overlapX2, overlapY2)
    );
    
    return Optional.of(overlap);
}
```

### Вариант 3: Проверка полного содержания

```java
public boolean contains(Rectangle other) {
    return this.bottomLeft.getX() <= other.bottomLeft.getX()
        && this.bottomLeft.getY() <= other.bottomLeft.getY()
        && this.topRight.getX() >= other.topRight.getX()
        && this.topRight.getY() >= other.topRight.getY();
}
```

### Вариант 4: Проверка касания границ

```java
public boolean isTouching(Rectangle other) {
    // Проверка касания по вертикали
    boolean touchingVertically = 
        this.topRight.getY() == other.bottomLeft.getY() 
        || this.bottomLeft.getY() == other.topRight.getY();
    
    // Проверка касания по горизонтали
    boolean touchingHorizontally = 
        this.topRight.getX() == other.bottomLeft.getX() 
        || this.bottomLeft.getX() == other.topRight.getX();
    
    return touchingVertically || touchingHorizontally;
}
```

## Kotlin Implementation

### Классы Point и Rectangle

```kotlin
data class PointK(val x: Int, val y: Int)

data class RectangleK(val bottomLeft: PointK, val topRight: PointK) {
    fun isOverlappingK(other: RectangleK): Boolean {
        // Проверка по оси Y: один прямоугольник выше другого
        if (this.topRight.y < other.bottomLeft.y 
            || this.bottomLeft.y > other.topRight.y) {
            return false
        }
        
        // Проверка по оси X: один прямоугольник левее другого
        if (this.topRight.x < other.bottomLeft.x 
            || this.bottomLeft.x > other.topRight.x) {
            return false
        }
        
        return true
    }
    
    fun getAreaK(): Int {
        val width = topRight.x - bottomLeft.x
        val height = topRight.y - bottomLeft.y
        return width * height
    }
    
    fun getOverlapAreaK(other: RectangleK): Int {
        if (!isOverlappingK(other)) {
            return 0
        }
        
        val xOverlap = maxOf(0, 
            minOf(this.topRight.x, other.topRight.x) - 
            maxOf(this.bottomLeft.x, other.bottomLeft.x)
        )
        
        val yOverlap = maxOf(0,
            minOf(this.topRight.y, other.topRight.y) - 
            maxOf(this.bottomLeft.y, other.bottomLeft.y)
        )
        
        return xOverlap * yOverlap
    }
    
    fun isTouchingK(other: RectangleK): Boolean {
        val touchingVertically = 
            this.topRight.y == other.bottomLeft.y 
            || this.bottomLeft.y == other.topRight.y
        
        val touchingHorizontally = 
            this.topRight.x == other.bottomLeft.x 
            || this.bottomLeft.x == other.topRight.x
        
        return touchingVertically || touchingHorizontally
    }
}
```

### Пример использования

```kotlin
fun main() {
    val r1 = RectangleK(PointK(0, 0), PointK(5, 5))
    val r2 = RectangleK(PointK(3, 3), PointK(8, 8))
    
    println(r1.isOverlappingK(r2)) // true
    
    val r3 = RectangleK(PointK(10, 10), PointK(15, 15))
    println(r1.isOverlappingK(r3)) // false
    
    val overlapArea = r1.getOverlapAreaK(r2)
    println("Overlap area: $overlapArea") // 4
}
```

## Сложность

### Временная сложность

- **Проверка перекрытия:** O(1) - константное время, так как мы просто сравниваем координаты

### Пространственная сложность

- **Проверка перекрытия:** O(1) - только константная память
- **Хранение прямоугольника:** O(1) - два объекта Point

## Особенности

- **Простота:** Алгоритм очень прост и понятен
- **Эффективность:** Выполняется за константное время
- **Точность:** Корректно обрабатывает все случаи перекрытия

## Применение

Проверка перекрытия прямоугольников используется в:

- Компьютерной графике
- Играх (коллизии)
- Географических информационных системах (GIS)
- UI/UX дизайне
- Обработке изображений
- Системах управления окнами

## Когда использовать

### Используйте этот алгоритм, когда:

- Нужно проверить коллизии в играх
- Работаете с GUI элементами
- Обрабатываете геометрические данные
- Нужна быстрая проверка перекрытия

## Заключение

В этом кратком руководстве мы рассмотрели алгоритм проверки перекрытия двух прямоугольников. Алгоритм основан на простом принципе: если прямоугольники не перекрываются, то один из них должен находиться выше, ниже, слева или справа от другого.
