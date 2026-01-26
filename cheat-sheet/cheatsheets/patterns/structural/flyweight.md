# Flyweight (Легковес)

В этой статье мы рассмотрим шаблон проектирования легковеса. Этот шаблон используется для уменьшения объема памяти. Это также может повысить производительность в приложениях, где создание объектов требует больших затрат.

**Last Updated:** 2025-01-15

## Описание

В этой статье мы рассмотрим шаблон проектирования легковеса. Этот шаблон используется для уменьшения объема памяти. Это также может повысить производительность в приложениях, где создание объектов требует больших затрат.

Проще говоря, шаблон легковеса основан на фабрике, которая перерабатывает созданные объекты, сохраняя их после создания. Каждый раз, когда запрашивается объект, фабрика ищет объект, чтобы проверить, не был ли он уже создан. Если да, то возвращается существующий объект, в противном случае создается, сохраняется и затем возвращается новый.

Состояние легковесного объекта состоит из инвариантного компонента, который используется совместно с другими подобными объектами (internal) и вариантного компонента, которым может манипулировать клиентский код (extrinsic).

Очень важно, чтобы легковесные объекты были неизменяемыми: любая операция над состоянием должна выполняться фабрикой.

## Useful Links

### Official Documentation
- [Design Patterns: Elements of Reusable Object-Oriented Software (GoF)](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Flyweight Pattern in Java](https://www.baeldung.com/java-flyweight-pattern)
- [Flyweight Pattern in Kotlin](https://www.baeldung.com/kotlin-flyweight-pattern)

### See Also
- [Facade Pattern](../structural/facade.md)
- [Composite Pattern](../structural/composite.md)

## Table of Contents

- [Components](#components)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Use Cases](#use-cases)

## Components

Основными элементами паттерна являются:

1. Интерфейс, определяющий операции, которые клиентский код может выполнять над легковесным объектом
2. Одна или несколько конкретных реализаций нашего интерфейса
3. Фабрика для обработки экземпляров и кэширования объектов

## Java Implementation

Давайте посмотрим, как реализовать каждый компонент.

Для начала мы создадим интерфейс `Vehicle`. Поскольку этот интерфейс будет типом возвращаемого значения фабричного метода, нам нужно убедиться, что все соответствующие методы раскрыты:

```java
public interface Vehicle {
    void start();
    void stop();
    Color getColor();
}
```

Далее, давайте создадим класс `Car` как конкретное транспортное средство. В нашем автомобиле будут реализованы все методы интерфейса автомобиля. Что касается его состояния, у него будет движок и цветовое поле:

```java
public class Car implements Vehicle {
    private Engine engine;
    private Color color;

    public Car(Engine engine, Color color) {
        this.engine = engine;
        this.color = color;
    }

    @Override
    public void start() {
        // Start implementation
    }

    @Override
    public void stop() {
        // Stop implementation
    }

    @Override
    public Color getColor() {
        return color;
    }
}
```

И последнее, но не менее важное: мы создадим `VehicleFactory`. Создание нового автомобиля - очень дорогая операция, поэтому фабрика будет создавать только один автомобиль каждого цвета.

Для этого мы отслеживаем созданные автомобили, используя карту как простой кеш:

```java
public class VehicleFactory {
    private static Map<Color, Vehicle> vehiclesCache = new HashMap<>();

    public static Vehicle createVehicle(Color color) {
        Vehicle newVehicle = vehiclesCache.computeIfAbsent(color, newColor -> {
            Engine newEngine = new Engine();
            return new Car(newEngine, newColor);
        });
        return newVehicle;
    }
}
```

Обратите внимание, что клиентский код может влиять только на внешнее состояние объекта (цвет нашего автомобиля), передавая его в качестве аргумента методу `createVehicle`.

## Kotlin Implementation

В Kotlin паттерн Flyweight может быть реализован следующим образом:

```kotlin
interface Vehicle {
    fun start()
    fun stop()
    fun getColor(): Color
}

class Car(private val engine: Engine, private val color: Color) : Vehicle {
    override fun start() {
        // Start implementation
    }

    override fun stop() {
        // Stop implementation
    }

    override fun getColor(): Color = color
}

object VehicleFactory {
    private val vehiclesCache = mutableMapOf<Color, Vehicle>()

    fun createVehicle(color: Color): Vehicle {
        return vehiclesCache.getOrPut(color) {
            val newEngine = Engine()
            Car(newEngine, color)
        }
    }
}
```

Использование:

```kotlin
fun main() {
    val redCar = VehicleFactory.createVehicle(Color.RED)
    val anotherRedCar = VehicleFactory.createVehicle(Color.RED)
    // redCar и anotherRedCar - это один и тот же объект
    println(redCar === anotherRedCar) // true
}
```

## Use Cases

Целью шаблона легковеса является сокращение использования памяти за счет совместного использования как можно большего количества данных, поэтому он является хорошей основой для алгоритмов сжатия без потерь. В этом случае каждый легковесный объект действует как указатель, а его внешнее состояние является контекстно-зависимой информацией.

Классический пример такого использования - текстовый процессор. Здесь каждый персонаж является легковесным объектом, который совместно использует данные, необходимые для рендеринга. В результате дополнительную память занимает только позиция символа внутри документа.

Многие современные приложения используют кеши для улучшения времени отклика. Шаблон легковеса похож на основную концепцию кэша и может хорошо подходить для этой цели.

Конечно, есть несколько ключевых различий в сложности и реализации между этим шаблоном и обычным кешем общего назначения.

