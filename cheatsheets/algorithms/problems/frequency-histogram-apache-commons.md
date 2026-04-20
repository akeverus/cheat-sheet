---
title: "Гистограмма частот с Apache Commons (Frequency Histogram Apache Commons)"
description: "Подсчёт частот значений и построение гистограммы с классом Frequency (Apache Commons Math): добавление значений, группировка в интервалы, визуализация через XChart. Java и Kotlin."
tags:
  - algorithms
  - problems
  - frequency-histogram-apache-commons
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Гистограмма частот с Apache Commons (Frequency Histogram Apache Commons)

Подсчёт частот значений и построение гистограммы с классом `Frequency` (Apache Commons Math): добавление значений, группировка в интервалы, визуализация через XChart. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Apache Commons Math — Frequency](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/stat/Frequency.html)
- [XChart Library](https://github.com/knowm/XChart) — графики

### См. также
- [[README|Математические алгоритмы]] — раздел math
- [Задачи и алгоритмы](./) — обзор разделов

## Содержание

- [Обзор](#обзор)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
- [Дополнительные возможности Frequency](#дополнительные-возможности-класса-frequency)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)


## Обзор

Класс `Frequency` (Apache Commons Math) считает, сколько раз каждое значение встречается в наборе данных; по нему удобно строить гистограммы частот. Для отображения используется, например, XChart. Гистограмма частот — столбчатая диаграмма по интервалам (бинам) с высотой, равной частоте попадания значений в интервал.

## Настройка проекта

Зависимости Maven: `commons-math3` (класс `Frequency`) и при необходимости `xchart` для отображения.

```xml
<!-- commons-math3 — Frequency; xchart — отображение гистограммы -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>

<dependency>
    <groupId>org.knowm.xchart</groupId>
    <artifactId>xchart</artifactId>
    <version>3.8.2</version>
</dependency>
```

## Реализация на Java

Данные добавляются в `Frequency` через `addValue(double)`; подсчёт по значению — `getCount(observation)`. Для гистограммы по интервалам: задаём ширину класса (CLASS_WIDTH), для каждого уникального значения определяем бин (нижняя/верхняя граница), суммируем частоты в `getCount` по значениям, попавшим в тот же бин, и складываем в карту «бин суммарная частота». Визуализация: `CategoryChart` в XChart, по оси X — подписи интервалов, по Y — частоты.

```java
// Подсчёт частоты значений в наборе данных с помощью Apache Commons Math Frequency.
import org.apache.commons.math3.stat.Frequency;
import java.util.*;

public class FrequencyHistogramExample {

    public static void main(String[] args) {
        List<Integer> datasetList = Arrays.asList(
            36, 25, 38, 46, 55, 68, 72, 55, 36, 38,
            67, 45, 22, 48, 91, 46, 52, 61, 58, 55
        );

        Frequency frequency = new Frequency();
        datasetList.forEach(d -> frequency.addValue(Double.parseDouble(d.toString())));

        // Получаем частоту каждого значения
        datasetList.stream()
            .map(d -> Double.parseDouble(d.toString()))
            .distinct()
            .forEach(observation -> {
                long observationFrequency = frequency.getCount(observation);
                System.out.println("Возраст " + observation +
                    ": частота = " + observationFrequency);
            });
    }
}
```

Группировка в бины по CLASS_WIDTH и суммирование частот:

```java
import java.util.*;

public class AgeGroupHistogram {
    private static final int CLASS_WIDTH = 10;
    private Map<String, Long> distributionMap = new LinkedHashMap<>();
    private Set<Double> processedObservations = new HashSet<>();

    public void createHistogram(List<Integer> datasetList, Frequency frequency) {
        datasetList.stream()
            .map(d -> Double.parseDouble(d.toString()))
            .distinct()
            .forEach(observation -> {
                if (processedObservations.contains(observation)) {
                    return;
                }

                long observationFrequency = frequency.getCount(observation);

                int upperBoundary = (observation > CLASS_WIDTH)
                    ? Math.multiplyExact(
                        (int) Math.ceil(observation / CLASS_WIDTH),
                        CLASS_WIDTH)
                    : CLASS_WIDTH;

                int lowerBoundary = (upperBoundary > CLASS_WIDTH)
                    ? Math.subtractExact(upperBoundary, CLASS_WIDTH)
                    : 0;

                String bin = lowerBoundary + "-" + upperBoundary;
                updateDistributionMap(lowerBoundary, bin, observationFrequency);
                processedObservations.add(observation);
            });
    }

    private void updateDistributionMap(int lowerBoundary, String bin, long observationFrequency) {
        if (distributionMap.containsKey(bin)) {
            distributionMap.put(bin,
                distributionMap.get(bin) + observationFrequency);
        } else {
            distributionMap.put(bin, observationFrequency);
        }
    }

    public Map<String, Long> getDistributionMap() {
        return distributionMap;
    }
}
```

Верхняя граница интервала: при observation > CLASS_WIDTH — округление вверх до кратного CLASS_WIDTH; нижняя — верхняя минус CLASS_WIDTH. Ключ карты — строка вида "lower-upper", значение — сумма частот. Обработанные наблюдения храним в Set, чтобы не учитывать одно значение дважды. Отображение: CategoryChartBuilder, оси из ключей и значений карты, SwingWrapper.displayChart().

```java
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import javax.swing.*;
import java.util.*;

public class HistogramVisualization {

    public void displayHistogram(Map<String, Long> distributionMap) {
        CategoryChart chart = new CategoryChartBuilder()
            .width(800)
            .height(600)
            .title("Распределение возраста")
            .xAxisTitle("Возрастная группа")
            .yAxisTitle("Частота")
            .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setAvailableSpaceFill(0.99);
        chart.getStyler().setOverlapped(true);

        List<Long> yData = new ArrayList<>(distributionMap.values());
        List<String> xData = new ArrayList<>(distributionMap.keySet());

        chart.addSeries("возрастная группа", xData, yData);

        new SwingWrapper<>(chart).displayChart();
    }
}
```

## Полный пример

```java
import org.apache.commons.math3.stat.Frequency;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import javax.swing.*;
import java.util.*;

public class CompleteHistogramExample {
    private static final int CLASS_WIDTH = 10;

    public static void main(String[] args) {
        // Исходные данные
        List<Integer> datasetList = Arrays.asList(
            36, 25, 38, 46, 55, 68, 72, 55, 36, 38,
            67, 45, 22, 48, 91, 46, 52, 61, 58, 55
        );

        // Создаем объект Frequency
        Frequency frequency = new Frequency();
        datasetList.forEach(d -> frequency.addValue(d.doubleValue()));

        // Группируем данные
        Map<String, Long> distributionMap = groupData(datasetList, frequency);

        // Отображаем гистограмму
        displayHistogram(distributionMap);

        // Выводим статистику
        printStatistics(frequency, datasetList);
    }

    private static Map<String, Long> groupData(List<Integer> datasetList, Frequency frequency) {
        Map<String, Long> distributionMap = new LinkedHashMap<>();
        Set<Double> processed = new HashSet<>();

        for (Integer value : datasetList) {
            double observation = value.doubleValue();
            if (processed.contains(observation)) {
                continue;
            }

            long observationFrequency = frequency.getCount(observation);
            int upperBoundary = (observation > CLASS_WIDTH)
                ? (int) (Math.ceil(observation / CLASS_WIDTH) * CLASS_WIDTH)
                : CLASS_WIDTH;
            int lowerBoundary = (upperBoundary > CLASS_WIDTH)
                ? upperBoundary - CLASS_WIDTH
                : 0;

            String bin = lowerBoundary + "-" + upperBoundary;
            distributionMap.merge(bin, observationFrequency, Long::sum);
            processed.add(observation);
        }

        return distributionMap;
    }

    private static void displayHistogram(Map<String, Long> distributionMap) {
        CategoryChart chart = new CategoryChartBuilder()
            .width(800)
            .height(600)
            .title("Распределение возраста учащихся")
            .xAxisTitle("Возрастная группа")
            .yAxisTitle("Частота")
            .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setAvailableSpaceFill(0.99);
        chart.getStyler().setOverlapped(true);

        List<Long> yData = new ArrayList<>(distributionMap.values());
        List<String> xData = new ArrayList<>(distributionMap.keySet());

        chart.addSeries("возрастная группа", xData, yData);

        new SwingWrapper<>(chart).displayChart();
    }

    private static void printStatistics(Frequency frequency, List<Integer> datasetList) {
        System.out.println("\n=== Статистика ===");
        System.out.println("Общее количество наблюдений: " + datasetList.size());
        System.out.println("Уникальных значений: " +
            datasetList.stream().distinct().count());

        datasetList.stream()
            .mapToInt(Integer::intValue)
            .min()
            .ifPresent(min -> System.out.println("Минимальный возраст: " + min));

        datasetList.stream()
            .mapToInt(Integer::intValue)
            .max()
            .ifPresent(max -> System.out.println("Максимальный возраст: " + max));

        double mean = datasetList.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        System.out.println("Средний возраст: " + String.format("%.2f", mean));
    }
}
```

## Дополнительные возможности класса Frequency

`getCount(value)` — частота значения; `getPct(value)` — доля в процентах; `getCumPct(value)` — накопленный процент; `values()` — все уникальные значения; `getSumFreq()` — сумма частот.

## Реализация на Kotlin

```kotlin
import org.apache.commons.math3.stat.Frequency

fun main() {
    val datasetList = listOf(
        36, 25, 38, 46, 55, 68, 72, 55, 36, 38,
        67, 45, 22, 48, 91, 46, 52, 61, 58, 55
    )

    val frequency = Frequency()
    datasetList.forEach { d -> frequency.addValue(d.toDouble()) }

    // Получаем частоту каждого значения
    datasetList.distinct().forEach { observation ->
        val observationFrequency = frequency.getCount(observation.toDouble())
        println("Возраст $observation: частота = $observationFrequency")
    }
}
```

### Группировка данных

```kotlin
import org.apache.commons.math3.stat.Frequency
import java.util.*

class AgeGroupHistogramK {
    private val CLASS_WIDTH = 10
    private val distributionMap = LinkedHashMap<String, Long>()
    private val processedObservations = mutableSetOf<Double>()

    fun createHistogram(datasetList: List<Int>, frequency: Frequency) {
        datasetList.distinct().forEach { observation ->
            val obs = observation.toDouble()
            if (processedObservations.contains(obs)) {
                return@forEach
            }

            val observationFrequency = frequency.getCount(obs)

            val upperBoundary = if (obs > CLASS_WIDTH) {
                (Math.ceil(obs / CLASS_WIDTH) * CLASS_WIDTH).toInt()
            } else {
                CLASS_WIDTH
            }

            val lowerBoundary = if (upperBoundary > CLASS_WIDTH) {
                upperBoundary - CLASS_WIDTH
            } else {
                0
            }

            val bin = "$lowerBoundary-$upperBoundary"
            updateDistributionMap(lowerBoundary, bin, observationFrequency)
            processedObservations.add(obs)
        }
    }

    private fun updateDistributionMap(lowerBoundary: Int, bin: String, observationFrequency: Long) {
        distributionMap[bin] = distributionMap.getOrDefault(bin, 0L) + observationFrequency
    }

    fun getDistributionMap(): Map<String, Long> = distributionMap
}
```

### Создание гистограммы с XChart

```kotlin
import org.knowm.xchart.*
import org.knowm.xchart.style.Styler
import javax.swing.*
import java.util.*

class HistogramVisualizationK {
    fun displayHistogram(distributionMap: Map<String, Long>) {
        val chart = CategoryChartBuilder()
            .width(800)
            .height(600)
            .title("Распределение возраста")
            .xAxisTitle("Возрастная группа")
            .yAxisTitle("Частота")
            .build()

        chart.styler.legendPosition = Styler.LegendPosition.InsideNW
        chart.styler.isAvailableSpaceFill = 0.99
        chart.styler.isOverlapped = true

        val yData = distributionMap.values.toList()
        val xData = distributionMap.keys.toList()

        chart.addSeries("возрастная группа", xData, yData)

        SwingUtilities.invokeLater {
            SwingWrapper(chart).displayChart()
        }
    }
}
```

## Лучшие практики

Ширину интервалов (CLASS_WIDTH) подбирайте так, чтобы получилось 5–15 столбцов без пустых бинов. При большом разбросе — логарифмическая шкала или отсечение выбросов. Для больших выборок — один проход по данным и при необходимости потоковая обработка. Подписывайте оси и заголовки для читаемости.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| NullPointerException при addValue | В Frequency передаётся null или NaN | Frequency не принимает null; проверять значения до addValue; для пропусков использовать отдельную метку |
| Неверное количество в бинe | Дублирование при группировке или неучтённые значения | Учитывать каждое уникальное значение один раз; суммировать getCount по всем наблюдениям в бинe |
| Гистограмма пустая или одна полоса | Все значения попали в один бин или CLASS_WIDTH слишком большой | Уменьшить CLASS_WIDTH; проверить диапазон данных (min/max) |

## Частые вопросы

**Чем Frequency отличается от ручного Map&lt;Double, Long&gt;?** Frequency даёт готовые методы getPct, getCumPct, values(), getSumFreq и согласованную работу с типами Comparable; для простого подсчёта достаточно Map.

**Нужен ли XChart для подсчёта частот?** Нет; Frequency только считает. XChart нужен только для отображения; вывод в консоль или экспорт в CSV возможны без него.

**Как считать частоты по категориям (строкам)?** Frequency работает с числами; для строк используйте Map&lt;String, Long&gt; или предварительно закодируйте категории в числа и постройте гистограмму по кодам с подписями осей из маппинга.

## Резюме

`Frequency` упрощает подсчёт частот; группировка в бины даёт распределение по интервалам; XChart или аналог — для визуализации. Дополнительно доступны проценты и накопленный процент.
