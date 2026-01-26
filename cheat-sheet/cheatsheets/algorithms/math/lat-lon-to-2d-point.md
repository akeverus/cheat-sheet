# Latitude and Longitude to 2D Point Conversion

Кратко: преобразование координат широты и долготы в 2D-точку с использованием проекции Меркатора. Рассматриваются сферическая и эллиптическая проекции Меркатора.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html)

### См. также
- `./distance-between-points.md` - вычисление расстояния
- `./rectangle-overlap.md` - проверка перекрытия двух прямоугольников

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Проекция Меркатора](#проекция-меркатора)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение проекций](#сравнение-проекций)
- [Сложность](#сложность)

## Описание алгоритма

При реализации приложений, использующих карты, мы обычно сталкиваемся с проблемой преобразования координат. В большинстве случаев нам нужно преобразовать широту и долготу в 2D-точку, чтобы отобразить. К счастью, для решения этой проблемы мы можем использовать формулы проекции Меркатора.

В этом уроке мы рассмотрим проекцию Меркатора и узнаем, как реализовать два ее варианта.

### Математическое определение

Проекция Меркатора - это картографическая проекция, введенная фламандским картографом Герардусом Меркатором в 1569 году. Картографическая проекция преобразует координаты широты и долготы на Земле в точку на плоской поверхности. Другими словами, он переводит точку на поверхности земли в точку на плоской карте.

Есть два способа реализации проекции Меркатора. Псевдомеркаторская проекция рассматривает Землю как сферу. Истинная проекция Меркатора моделирует Землю как эллипсоид. Мы реализуем обе версии.

## Проекция Меркатора

Начнем с базового класса для обеих реализаций проекции Меркатора:

```java
abstract class Mercator {
    final static double RADIUS_MAJOR = 6378137.0;  // Большой радиус (экватор)
    final static double RADIUS_MINOR = 6356752.3142;  // Малый радиус (полюса)
    
    abstract double yAxisProjection(double input);
    abstract double xAxisProjection(double input);
}
```

Этот класс также предоставляет большой и малый радиусы Земли, измеряемые в метрах. Хорошо известно, что Земля не совсем сфера. По этой причине нам нужны два радиуса. Во-первых, большой радиус - это расстояние от центра земли до экватора. Во-вторых, малый радиус - это расстояние от центра земли до северного и южного полюсов.

## Java Implementation

### Сферическая проекция Меркатора

Модель псевдопроекции рассматривает землю как сферу. В отличие от эллиптической проекции, где Земля будет проецироваться на более точную форму. Этот подход позволяет нам быстро оценить более точную, но более сложную в вычислительном отношении эллиптическую проекцию. Вследствие этого прямые измерения расстояний в этой проекции будут приближенными.

Кроме того, пропорции фигур на карте незначительно изменятся. В результате этого широта и соотношения форм объектов на карте, таких как страны, озера, реки и т.д., точно не сохраняются.

Это также называется веб-проекцией Меркатора и обычно используется в веб-приложениях, включая Google Maps.

Давайте реализуем этот подход:

```java
public class SphericalMercator extends Mercator {
    @Override
    double xAxisProjection(double input) {
        return Math.toRadians(input) * RADIUS_MAJOR;
    }
    
    @Override
    double yAxisProjection(double input) {
        return Math.log(
            Math.tan(Math.PI / 4 + Math.toRadians(input) / 2)
        ) * RADIUS_MAJOR;
    }
}
```

Первое, что следует отметить в этом подходе, это тот факт, что этот подход представляет радиус Земли одной константой, а не двумя, как это есть на самом деле. Во-вторых, мы видим, что мы реализовали две функции для преобразования в проекцию по оси X и проекцию по оси Y. В приведенном выше классе мы использовали математическую библиотеку, предоставленную Java, чтобы упростить наш код.

### Пример использования

```java
SphericalMercator sphericalMercator = new SphericalMercator();

double x = sphericalMercator.xAxisProjection(22);  // ≈ 2449028.797
double y = sphericalMercator.yAxisProjection(44);  // ≈ 5465442.183
```

Стоит отметить, что эта проекция будет отображать точки в ограничительной рамке (слева, снизу, справа, сверху) (-20037508.34, -23810769.32, 20037508.34, 23810769.32).

## Эллиптическая проекция Меркатора

Истинная проекция моделирует Землю как эллипсоид. Эта проекция дает точные соотношения для объектов в любой точке Земли. Конечно, он учитывает объекты на карте, но не на 100% точен. Однако этот подход не является наиболее распространенным из-за сложности вычислений.

Давайте реализуем этот подход:

```java
class EllipticalMercator extends Mercator {
    @Override
    double yAxisProjection(double input) {
        input = Math.min(Math.max(input, -89.5), 89.5);
        
        double earthDimensionalRateNormalized = 1.0 - 
            Math.pow(RADIUS_MINOR / RADIUS_MAJOR, 2);
        
        double inputOnEarthProj = Math.sqrt(earthDimensionalRateNormalized) *
            Math.sin(Math.toRadians(input));
        
        inputOnEarthProj = Math.pow(
            ((1.0 - inputOnEarthProj) / (1.0 + inputOnEarthProj)),
            0.5 * Math.sqrt(earthDimensionalRateNormalized)
        );
        
        double inputOnEarthProjNormalized =
            Math.tan(0.5 * ((Math.PI * 0.5) - Math.toRadians(input))) / 
            inputOnEarthProj;
        
        return (-1) * RADIUS_MAJOR * Math.log(inputOnEarthProjNormalized);
    }
    
    @Override
    double xAxisProjection(double input) {
        return RADIUS_MAJOR * Math.toRadians(input);
    }
}
```

Выше мы видим, насколько сложен этот подход в отношении проекции на ось Y. Это потому, что он должен учитывать некруглую форму земли. Хотя истинный подход Меркатора кажется сложным, он более точен, чем сферический подход, поскольку он использует радиус для представления земли как одного малого и одного большого.

### Пример использования

```java
EllipticalMercator ellipticalMercator = new EllipticalMercator();

double x = ellipticalMercator.xAxisProjection(22);  // ≈ 2449028.797
double y = ellipticalMercator.yAxisProjection(44);  // ≈ 5435749.888
```

Эта проекция сопоставит точки с ограничивающей рамкой (-20037508.34, -34619289.37, 20037508.34, 34619289.37).

## Сравнение проекций

| Характеристика | Сферическая | Эллиптическая |
|----------------|-------------|---------------|
| Точность | Приближенная | Более точная |
| Производительность | Быстрая | Медленная |
| Сложность | Простая | Сложная |
| Использование | Веб-приложения | Точные карты |

### Когда использовать сферическую проекцию

- Веб-приложения (Google Maps, OpenStreetMap)
- Когда важна производительность
- Когда приблизительная точность достаточна

### Когда использовать эллиптическую проекцию

- Точные картографические приложения
- Научные вычисления
- Когда важна максимальная точность

## Сложность

### Временная сложность

- **Сферическая проекция:** O(1) - константное время для каждой координаты
- **Эллиптическая проекция:** O(1) - константное время, но с более сложными вычислениями

### Пространственная сложность

- **Обе проекции:** O(1) - только константная память

## Особенности

- **Точность:** Эллиптическая проекция более точна, особенно в высоких широтах
- **Производительность:** Сферическая проекция быстрее
- **Ограничения:** Обе проекции имеют проблемы с полюсами (бесконечность)

## Применение

Преобразование координат используется в:

- Веб-картах (Google Maps, Яндекс.Карты)
- GPS-навигации
- Географических информационных системах (GIS)
- Играх с картами
- Мобильных приложениях

## Варианты задачи

### Вариант 1: Обратное преобразование (2D → Lat/Lon)

```java
public class InverseMercator {
    private static final double RADIUS_MAJOR = 6378137.0;
    
    public static double longitudeFromX(double x) {
        return Math.toDegrees(x / RADIUS_MAJOR);
    }
    
    public static double latitudeFromY(double y) {
        double lat = Math.toDegrees(
            2 * Math.atan(Math.exp(y / RADIUS_MAJOR)) - Math.PI / 2
        );
        return lat;
    }
}
```

### Вариант 2: Преобразование с масштабированием

```java
public class ScaledMercator extends SphericalMercator {
    private final double scale;
    
    public ScaledMercator(double scale) {
        this.scale = scale;
    }
    
    @Override
    double xAxisProjection(double input) {
        return super.xAxisProjection(input) * scale;
    }
    
    @Override
    double yAxisProjection(double input) {
        return super.yAxisProjection(input) * scale;
    }
}
```

### Вариант 3: Преобразование с центрированием

```java
public class CenteredMercator extends SphericalMercator {
    private final double centerLat;
    private final double centerLon;
    
    public CenteredMercator(double centerLat, double centerLon) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
    }
    
    public Point2D project(double lat, double lon) {
        double x = xAxisProjection(lon - centerLon);
        double y = yAxisProjection(lat - centerLat);
        return new Point2D.Double(x, y);
    }
}
```

## Kotlin Implementation

### Абстрактный класс Mercator

```kotlin
abstract class MercatorK {
    companion object {
        const val RADIUS_MAJOR = 6378137.0  // Большой радиус (экватор)
        const val RADIUS_MINOR = 6356752.3142  // Малый радиус (полюса)
    }
    
    abstract fun yAxisProjection(input: Double): Double
    abstract fun xAxisProjection(input: Double): Double
}
```

### Сферическая проекция Меркатора

```kotlin
class SphericalMercatorK : MercatorK() {
    override fun xAxisProjection(input: Double): Double {
        return Math.toRadians(input) * RADIUS_MAJOR
    }
    
    override fun yAxisProjection(input: Double): Double {
        return Math.log(
            Math.tan(Math.PI / 4 + Math.toRadians(input) / 2)
        ) * RADIUS_MAJOR
    }
}
```

### Эллиптическая проекция Меркатора

```kotlin
class EllipticalMercatorK : MercatorK() {
    override fun yAxisProjection(input: Double): Double {
        val input = Math.min(Math.max(input, -89.5), 89.5)
        
        val earthDimensionalRateNormalized = 1.0 - 
            Math.pow(RADIUS_MINOR / RADIUS_MAJOR, 2.0)
        
        var inputOnEarthProj = Math.sqrt(earthDimensionalRateNormalized) *
            Math.sin(Math.toRadians(input))
        
        inputOnEarthProj = Math.pow(
            ((1.0 - inputOnEarthProj) / (1.0 + inputOnEarthProj)),
            0.5 * Math.sqrt(earthDimensionalRateNormalized)
        )
        
        val inputOnEarthProjNormalized =
            Math.tan(0.5 * ((Math.PI * 0.5) - Math.toRadians(input))) / 
            inputOnEarthProj
        
        return (-1) * RADIUS_MAJOR * Math.log(inputOnEarthProjNormalized)
    }
    
    override fun xAxisProjection(input: Double): Double {
        return RADIUS_MAJOR * Math.toRadians(input)
    }
}
```

### Обратное преобразование

```kotlin
class InverseMercatorK {
    companion object {
        private const val RADIUS_MAJOR = 6378137.0
    }
    
    fun longitudeFromX(x: Double): Double {
        return Math.toDegrees(x / RADIUS_MAJOR)
    }
    
    fun latitudeFromY(y: Double): Double {
        val latRad = 2 * Math.atan(Math.exp(y / RADIUS_MAJOR)) - Math.PI / 2
        return Math.toDegrees(latRad)
    }
}
```

### Пример использования

```kotlin
fun main() {
    val sphericalMercator = SphericalMercatorK()
    val x = sphericalMercator.xAxisProjection(22.0) // ≈ 2449028.797
    val y = sphericalMercator.yAxisProjection(44.0) // ≈ 5465442.183
    
    val ellipticalMercator = EllipticalMercatorK()
    val x2 = ellipticalMercator.xAxisProjection(22.0) // ≈ 2449028.797
    val y2 = ellipticalMercator.yAxisProjection(44.0) // ≈ 5435749.888
}
```

## Когда использовать

### Используйте сферическую проекцию, когда:

- Работаете с веб-приложениями
- Нужна быстрая производительность
- Приблизительная точность достаточна

### Используйте эллиптическую проекцию, когда:

- Нужна максимальная точность
- Работаете с научными данными
- Важны точные измерения расстояний

## Заключение

Если нам нужно преобразовать координаты широты и долготы в 2D-поверхность, мы можем использовать проекцию Меркатора. В зависимости от точности, необходимой для нашей реализации, мы можем использовать сферический или эллиптический подход.
