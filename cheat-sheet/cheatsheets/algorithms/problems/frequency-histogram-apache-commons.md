# Создание гистограмм с частотой с использованием Apache Commons

Руководство по представлению данных на гистограмме с помощью класса Frequency из библиотеки Apache Commons Math.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Apache Commons Math - Frequency](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/stat/Frequency.html)
- [XChart Library](https://github.com/knowm/XChart)

### См. также
- [Статистика и анализ данных](../math/)
- [Визуализация данных](../problems/)

## Содержание

- [Обзор](#обзор)
- [Что такое гистограмма?](#что-такое-гистограмма)
- [Настройка проекта](#настройка-проекта)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Полный пример](#полный-пример)

## Обзор

В этом уроке мы рассмотрим, как мы можем представить данные на гистограмме с помощью класса Frequency Apache Commons.

Класс Frequency является частью библиотеки Apache Commons Math, рассматриваемой в этой статье.

## Что такое гистограмма?

Гистограмма - это диаграмма соединенных полос, показывающая наличие диапазона данных в наборе данных. Она отличается от гистограммы тем, что используется для отображения распределения непрерывных количественных переменных, тогда как гистограмма используется для отображения категорийных данных.

## Настройка проекта

В этой статье мы будем использовать проект Maven со следующими зависимостями:

```xml
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

Библиотека commons-math3 содержит класс Frequency, который мы будем использовать для определения появления переменных в нашем наборе данных. Библиотека xchart - это то, что мы будем использовать для отображения гистограммы в графическом интерфейсе.

## Java Implementation

### Использование класса Frequency

Для этого урока мы будем использовать набор данных, состоящий из возраста учащихся в конкретной школе. Мы хотели бы видеть частоту различных возрастных групп и наблюдать за их распределением на гистограмме.

Давайте представим набор данных с коллекцией List и используем ее для заполнения экземпляра класса Frequency:

```java
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

### Группировка данных

Теперь, когда мы заполнили наш экземпляр класса Frequency, мы собираемся получить количество каждого возраста в ячейке и просуммировать его, чтобы мы могли получить общую частоту возрастов в определенной возрастной группе:

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

Из приведенного выше фрагмента мы сначала определяем частоту наблюдения, используя `getCount()` класса Frequency. Метод возвращает общее количество вхождений наблюдения.

Используя текущее наблюдение, мы динамически определяем группу, к которой он принадлежит, определяя его верхнюю и нижнюю границы относительно ширины класса, которая равна 10.

Верхняя и нижняя границы объединяются для формирования корзины, которая хранится вместе с observationFrequency в distributionMap с помощью `updateDistributionMap()`.

Если бин уже существует, мы обновляем частоту, в противном случае мы добавляем его в качестве ключа и устанавливаем частоту текущего наблюдения в качестве его значения. Обратите внимание, что мы отслеживали обработанные наблюдения, чтобы избежать дублирования.

### Создание гистограммы с XChart

В классе Frequency также есть методы для определения процента и совокупного процента переменной в наборе данных.

Теперь, когда мы преобразовали наш необработанный набор данных в карту возрастных групп и их соответствующих частот, мы можем использовать библиотеку xchart для отображения данных в виде гистограммы:

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

Мы создали экземпляр CategoryChart с помощью построителя диаграмм, затем настроили его и заполнили данными для осей x и y.

Наконец, мы отображаем диаграмму в графическом интерфейсе с помощью SwingWrapper.

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

Класс Frequency предоставляет множество полезных методов:

```java
// Получить частоту значения
long count = frequency.getCount(55.0);

// Получить процент значения
double percent = frequency.getPct(55.0);

// Получить кумулятивный процент
double cumPct = frequency.getCumPct(55.0);

// Получить все уникальные значения
Collection<Comparable<?>> values = frequency.values();

// Получить сумму всех частот
long totalCount = frequency.getSumFreq();
```

## Kotlin Implementation

### Использование класса Frequency

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

## Резюме

В этой статье мы рассмотрели, как использовать возможности класса Frequency библиотеки Apache Commons Math для создания гистограмм.

Основные моменты:
- Класс Frequency упрощает подсчет частоты значений в наборе данных
- XChart позволяет легко визуализировать данные в виде гистограмм
- Группировка данных помогает лучше понять распределение
- Класс Frequency предоставляет дополнительные статистические методы

Из приведенной выше гистограммы видно, что учащихся в возрасте 80-90 лет нет, а преобладают студенты в возрасте 50-60 лет. Скорее всего, это будут докторанты или постдокторанты.

Мы также можем сказать, что гистограмма имеет нормальное распределение.
