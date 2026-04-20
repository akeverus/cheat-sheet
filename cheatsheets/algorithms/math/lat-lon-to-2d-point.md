---
title: "Преобразование широты и долготы в 2D-точку (Lat/Lon to 2D Point)"
description: "Преобразование координат (широта, долгота) в 2D-точку по проекции Меркатора. В документе описаны сферическая (Web Mercator) и эллиптическая проекции в Java и Kotlin, обратное преобразование и варианты с масштабом и центрированием."
tags:
  - algorithms
  - math
  - lat-lon-to-2d-point
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Преобразование широты и долготы в 2D-точку (`Lat/Lon to 2D Point`)

Преобразование координат (широта, долгота) в 2D-точку по проекции Меркатора. В документе описаны сферическая (Web Mercator) и эллиптическая проекции в `Java` и Kotlin, обратное преобразование и варианты с масштабом и центрированием.

## Полезные ссылки

### Официальная документация
- [Math (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html)

### См. также
- [[distance-between-points|Вычисление расстояния]] — расстояние между точками
- [[rectangle-overlap|Проверка перекрытия двух прямоугольников]] — перекрытие прямоугольников

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическое определение](#математическое-определение)
- [Проекция Меркатора](#проекция-меркатора)
- [Реализация на Java](#реализация-на-java)
- [Эллиптическая проекция Меркатора](#эллиптическая-проекция-меркатора)
- [Сравнение проекций](#сравнение-проекций)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

При реализации приложений, использующих карты, мы обычно сталкиваемся с проблемой преобразования координат. В большинстве случаев нам нужно преобразовать широту и долготу в 2D-точку, чтобы отобразить. К счастью, для решения этой проблемы мы можем использовать формулы проекции Меркатора.

В этом уроке мы рассмотрим проекцию Меркатора и узнаем, как реализовать два ее варианта.

### Математическое определение

Проекция Меркатора переводит (широта, долгота) в точку на плоскости. Сферическая (псевдо-Меркатор) считает Землю сферой; эллиптическая — эллипсоидом (WGS84). Ниже — обе реализации.

## Проекция Меркатора

Базовый класс с радиусами Земли (WGS84) и абстрактными методами проекции по осям X и Y.

```java
// Радиусы в метрах; абстрактные xAxisProjection, yAxisProjection
abstract class Mercator {
    final static double RADIUS_MAJOR = 6378137.0;  // Большой радиус (экватор)
    final static double RADIUS_MINOR = 6356752.3142;  // Малый радиус (полюса)

    abstract double yAxisProjection(double input);
    abstract double xAxisProjection(double input);
}
```

## Реализация на Java

### Сферическая проекция Меркатора (Java)

Земля как сфера; одна константа радиуса. Используется в Web Mercator (Google Maps, OpenStreetMap). Расстояния и пропорции приближённые.

```java
// X: долгота в радианах × радиус; Y: log(tan(π/4 + φ/2)) × радиус
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

Типичный диапазон координат Web Mercator (в метрах): примерно ±20 037 508 по X, ±23 810 769 по Y.

## Эллиптическая проекция Меркатора

Земля как эллипсоид (RADIUS_MAJOR, RADIUS_MINOR). Точнее сферической, особенно на высоких широтах; вычисления сложнее. Широту ограничивают [-89.5, 89.5] во избежание сингулярности на полюсах.

```java
// Формула с учётом сжатия Земли (эллипсоид WGS84)
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

## Сравнение проекций

| Характеристика | Сферическая | Эллиптическая |
|----------------|-------------|---------------|
| Точность | Приближённая | Выше |
| Производительность | Быстрее | Сложнее |
| Использование | Веб-карты | Точная картография |

## Сложность

Обе проекции: O(1) по времени и памяти на одну точку. Эллиптическая требует больше арифметических операций.

## Особенности

Эллиптическая точнее на высоких широтах. Сферическая проще и быстрее. У обеих на полюсах проекция уходит в бесконечность — широту ограничивают.

## Применение

Используется в веб-картах (Google Maps, OpenStreetMap, Яндекс.Карты), GPS-навигации, GIS, играх с картами и мобильных приложениях.

## Варианты задачи

### Вариант 1: Обратное преобразование (2D → Lat/Lon)

По x, y в метрах (сферическая проекция) восстанавливаем долготу и широту в градусах.

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

Умножение результата проекции на коэффициент масштаба.

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

Проекция относительно выбранного центра (centerLat, centerLon); результат в локальных координатах.

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

## Реализация на Kotlin

```kotlin
// MercatorK с радиусами WGS84; SphericalMercatorK, EllipticalMercatorK, InverseMercatorK
abstract class MercatorK {
    companion object {
        const val RADIUS_MAJOR = 6378137.0  // Большой радиус (экватор)
        const val RADIUS_MINOR = 6356752.3142  // Малый радиус (полюса)
    }

    abstract fun yAxisProjection(input: Double): Double
    abstract fun xAxisProjection(input: Double): Double
}
```

### Сферическая проекция Меркатора (Kotlin)

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

Сферическую проекцию используйте для веб-карт и когда важна скорость; эллиптическую — когда нужна максимальная точность (научные расчёты, точная картография).

## Лучшие практики

При эллиптической проекции ограничивайте широту [-89.5, 89.5]. Явно документируйте единицы: вход в градусах, выход в метрах (Web Mercator). Используйте константы WGS84 для радиусов. Для веб-тайлов (OpenStreetMap, Google) — сферическая проекция и согласованный zoom level. Обратное преобразование для сферической: atan/exp; для эллиптической — соответствующие обратные формулы или библиотека.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| NaN или Inf на высоких широтах | Сингулярность у полюсов | Ограничить широту [-89.5, 89.5] в эллиптической проекции |
| Точки «съехали» относительно тайлов карты | Несовпадение проекции или масштаба с провайдером | Использовать сферическую Web Mercator и тот же zoom/масштаб, что у карты |
| Обратное преобразование даёт неверные градусы | Перепутаны x/y или единицы | Проверить: x → долгота, y → широта; координаты в метрах |

## Частые вопросы

**Почему сферическая проекция достаточна для веб-карт?** Провайдеры (Google, OSM) используют Web Mercator (сферическую); для совпадения с тайлами нужна та же проекция. Эллиптическая даёт другую сетку.

**В каких единицах результат?** В метрах (расстояние от условного центра). Для экрана нужно дополнительное масштабирование и сдвиг по zoom level и границам карты.

**Когда нужна эллиптическая проекция?** Когда важна точность измерений расстояний или площадей на карте, либо при научных расчётах; для отображения на веб-карте обычно достаточно сферической.

## Заключение

В документе описано преобразование широты и долготы в 2D-точку по проекции Меркатора в `Java` и Kotlin: сферическая (Web Mercator) и эллиптическая проекции, обратное преобразование, масштаб и центрирование. Для веб-карт обычно используют сферическую; для точной картографии — эллиптическую.
