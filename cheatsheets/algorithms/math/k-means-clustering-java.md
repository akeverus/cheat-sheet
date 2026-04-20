---
title: "K-Means кластеризация в Java (K-Means Clustering)"
description: "Неконтролируемая кластеризация с фиксированным числом кластеров k: инициализация центроидов, назначение точек ближайшему центроиду, пересчёт центроидов до сходимости. Реализация в Java и Kotlin, евклидово расстояние, метод локтя для выбора k."
tags:
  - algorithms
  - math
  - k-means-clustering-java
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# K-Means кластеризация в Java (`K-Means Clustering`)

Неконтролируемая кластеризация с фиксированным числом кластеров k: инициализация центроидов, назначение точек ближайшему центроиду, пересчёт центроидов до сходимости. Реализация в Java и Kotlin, евклидово расстояние, метод локтя для выбора k.

## Полезные ссылки

### Официальная документация
- [Baeldung: K-Means Clustering](https://www.baeldung.com/)

### См. также
- [Градиентный спуск](gradient-descent.md) — градиентный спуск
- [Вычисление расстояния](distance-between-points.md) — расстояние между точками

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Неконтролируемое обучение](#неконтролируемое-обучение)
- [Механизм работы K-Means](#механизм-работы-k-means)
- [Java Implementation](#java-implementation)
- [Пример использования](#пример-использования)
- [Оптимизация количества кластеров](#оптимизация-количества-кластеров)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

Кластеризация — неконтролируемый алгоритм для обнаружения групп похожих объектов без заранее заданных меток. K-Means — один из таких алгоритмов: число кластеров k задаётся заранее; результат зависит от инициализации центроидов.

## Неконтролируемое обучение

При обучении с учителем у каждой точки есть метка (например, спам/не спам); при неконтролируемом меток нет — алгоритм ищет структуру в данных сам. Кластеризация относится к неконтролируемым методам. K-Means выделяет k групп; альтернативы — иерархическая кластеризация, распространение сходства, спектральная кластеризация.

## Механизм работы K-Means

Инициализация: задаём k центроидов (случайно или по эвристике, например K-Means++). Назначение: каждой точке назначаем ближайший центроид по выбранной метрике (часто евклидово расстояние). Обновление: центроид сдвигается в среднее по назначенным точкам. Цикл повторяем, пока назначения не перестанут меняться или не исчерпается лимит итераций.

## Java Implementation

### Структуры данных

Объекты представляются векторами признаков (атрибут → число). Запись — описание плюс карта признаков; центроид — точка в том же пространстве признаков.

```java
// Запись: описание + карта признаков (атрибут → значение) для кластеризации
public class Record {
    private final String description;
    private final Map<String, Double> features;
    
    public Record(String description, Map<String, Double> features) {
        this.description = description;
        this.features = features;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, Double> getFeatures() {
        return features;
    }
}

// Центроид — точка в пространстве признаков (те же ключи, что у Record)
public class Centroid {
    private final Map<String, Double> coordinates;
    
    public Centroid(Map<String, Double> coordinates) {
        this.coordinates = coordinates;
    }
    
    public Map<String, Double> getCoordinates() {
        return coordinates;
    }
}
```

### Вычисление расстояний

Ближайший центроид определяем по метрике расстояния. Евклидово расстояние: √(Σ(p_i − q_i)²). Интерфейс `Distance` позволяет подставлять другие метрики (например, косинусное сходство).

```
√((p1 - p2)² + (q1 - q2)²)
```

```
```java
public interface Distance {
    double calculate(Map<String, Double> f1, Map<String, Double> f2);
}

// Сумма квадратов разностей по общим ключам, затем sqrt
public class EuclideanDistance implements Distance {
    @Override
    public double calculate(Map<String, Double> f1, Map<String, Double> f2) {
        double sum = 0;
        
        for (String key : f1.keySet()) {
            Double v1 = f1.get(key);
            Double v2 = f2.get(key);
            
            if (v1 != null && v2 != null) {
                sum += Math.pow(v1 - v2, 2);
            }
        }
        
        return Math.sqrt(sum);
    }
}
```

### Основной алгоритм

Метод `fit`: принимает список записей, k, метрику расстояния и максимум итераций; возвращает карту центроид → список записей кластера.

```
```java
public class KMeans {
    private static final Random random = new Random();
    
    public static Map<Centroid, List<Record>> fit(
        List<Record> records,
        int k,
        Distance distance,
        int maxIterations
    ) {
        // Реализация алгоритма
    }
}

// Случайные центроиды в диапазоне min–max по каждому признаку (ускоряет сходимость)
private static List<Centroid> randomCentroids(List<Record> records, int k) {
    List<Centroid> centroids = new ArrayList<>();
    Map<String, Double> maxs = new HashMap<>();
    Map<String, Double> mins = new HashMap<>();
    
    for (Record record : records) {
        record.getFeatures().forEach((key, value) -> {
            maxs.compute(key, (k1, max) -> max == null || value > max ? value : max);
            mins.compute(key, (k1, min) -> min == null || value < min ? value : min);
        });
    }
    
    Set<String> attributes = records.stream()
        .flatMap(e -> e.getFeatures().keySet().stream())
        .collect(Collectors.toSet());
    
    for (int i = 0; i < k; i++) {
        Map<String, Double> coordinates = new HashMap<>();
        
        for (String attribute : attributes) {
            double max = maxs.get(attribute);
            double min = mins.get(attribute);
            coordinates.put(attribute, random.nextDouble() * (max - min) + min);
        }
        
        centroids.add(new Centroid(coordinates));
    }
    
    return centroids;
}

// Ближайший центроид к записи по заданной метрике; назначение записи в кластер
private static Centroid nearestCentroid(
    Record record, 
    List<Centroid> centroids, 
    Distance distance
) {
    double minimumDistance = Double.MAX_VALUE;
    Centroid nearest = null;
    
    for (Centroid centroid : centroids) {
        double currentDistance = distance.calculate(
            record.getFeatures(), 
            centroid.getCoordinates()
        );
        
        if (currentDistance < minimumDistance) {
            minimumDistance = currentDistance;
            nearest = centroid;
        }
    }
    
    return nearest;
}

private static void assignToCluster(
    Map<Centroid, List<Record>> clusters,
    Record record,
    Centroid centroid
) {
    clusters.compute(centroid, (key, list) -> {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(record);
        return list;
    });
}

// Новый центроид = среднее по всем записям кластера по каждому признаку; пустой кластер — центроид не двигаем
private static Centroid average(Centroid centroid, List<Record> records) {
    if (records == null || records.isEmpty()) {
        return centroid;
    }
    
    Map<String, Double> average = new HashMap<>();
    records.stream()
        .flatMap(e -> e.getFeatures().keySet().stream())
        .forEach(k -> average.put(k, 0.0));
    
    for (Record record : records) {
        record.getFeatures().forEach(
            (k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue)
        );
    }
    
    average.forEach((k, v) -> average.put(k, v / records.size()));
    
    return new Centroid(average);
}

private static List<Centroid> relocateCentroids(
    Map<Centroid, List<Record>> clusters
) {
    return clusters.entrySet().stream()
        .map(e -> average(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
}

// Основной цикл: назначение → сравнение с предыдущим состоянием → пересчёт центроидов; выход при совпадении состояний или maxIterations
public static Map<Centroid, List<Record>> fit(
    List<Record> records, 
    int k, 
    Distance distance, 
    int maxIterations
) {
    List<Centroid> centroids = randomCentroids(records, k);
    Map<Centroid, List<Record>> clusters = new HashMap<>();
    Map<Centroid, List<Record>> lastState = new HashMap<>();
    
    for (int i = 0; i < maxIterations; i++) {
        boolean isLastIteration = i == maxIterations - 1;
        
        for (Record record : records) {
            Centroid centroid = nearestCentroid(record, centroids, distance);
            assignToCluster(clusters, record, centroid);
        }
        
        boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
        lastState = clusters;
        
        if (shouldTerminate) {
            break;
        }
        
        centroids = relocateCentroids(clusters);
        clusters = new HashMap<>();
    }
    
    return lastState;
}
```

## Пример использования

Пример на данных Last.fm: топ исполнители, топ теги, теги по исполнителям; записи — вектор признаков по тегам. Кластеризация по 7 кластерам, евклидово расстояние, до 1000 итераций.

```
```java
List<String> artists = getTop100Artists();
Set<String> topTags = getTop100Tags();
List<Record> records = datasetWithTaggedArtists(artists, topTags);

Map<Centroid, List<Record>> clusters = KMeans.fit(
    records, 
    7, 
    new EuclideanDistance(), 
    1000
);

clusters.forEach((key, value) -> {
    System.out.println("------------------------CLUSTER---------------------------");
    System.out.println(sortedCentroid(key));
    String members = String.join(", ", 
        value.stream()
            .map(Record::getDescription)
            .collect(Collectors.toSet())
    );
    System.out.print(members);
    System.out.println();
    System.out.println();
});
```

## Оптимизация количества кластеров

Число k задаётся заранее; его можно подобрать по домену или по эвристикам (метод локтя, силуэт). Метод локтя: считаем SSE (сумма квадратов расстояний от точек до своих центроидов) для k = 2, 3, …; на графике SSE от k ищем «локоть» — место, где прирост качества замедляется.

```
```java
// SSE = сумма квадратов расстояний от каждой точки до центроида своего кластера
public static double sse(
    Map<Centroid, List<Record>> clustered, 
    Distance distance
) {
    double sum = 0;
    
    for (Map.Entry<Centroid, List<Record>> entry : clustered.entrySet()) {
        Centroid centroid = entry.getKey();
        
        for (Record record : entry.getValue()) {
            double d = distance.calculate(
                centroid.getCoordinates(), 
                record.getFeatures()
            );
            sum += Math.pow(d, 2);
        }
    }
    
    return sum;
}

```
```java
// Подбор k: запуск K-Means для k=2..16, сбор SSE для графика «локтя»
List<Record> records = /* набор данных */;
Distance distance = new EuclideanDistance();
List<Double> sumOfSquaredErrors = new ArrayList<>();

for (int k = 2; k <= 16; k++) {
    Map<Centroid, List<Record>> clusters = KMeans.fit(
        records, 
        k, 
        distance, 
        1000
    );
    double sse = Errors.sse(clusters, distance);
    sumOfSquaredErrors.add(sse);
}
```

## Kotlin Implementation

```
```kotlin
// Запись и центроид в том же формате, что в Java
data class RecordK(
    val description: String,
    val features: Map<String, Double>
)

data class CentroidK(
    val coordinates: Map<String, Double>
)

interface DistanceK {
    fun calculate(f1: Map<String, Double>, f2: Map<String, Double>): Double
}
```

### Евклидово расстояние

```
```kotlin
class EuclideanDistanceK : DistanceK {
    override fun calculate(f1: Map<String, Double>, f2: Map<String, Double>): Double {
        var sum = 0.0
        
        for (key in f1.keys) {
            val v1 = f1[key]
            val v2 = f2[key]
            
            if (v1 != null && v2 != null) {
                sum += Math.pow(v1 - v2, 2.0)
            }
        }
        
        return Math.sqrt(sum)
    }
}
```

```
```kotlin
object KMeansK {
    fun fit(
        records: List<RecordK>,
        k: Int,
        distance: DistanceK,
        maxIterations: Int
    ): Map<CentroidK, List<RecordK>> {
        var centroids = initializeCentroidsK(records, k)
        var clusters = mutableMapOf<CentroidK, MutableList<RecordK>>()
        
        for (iteration in 0 until maxIterations) {
            clusters.clear()
            
            // Назначение точек ближайшим центроидам
            for (record in records) {
                val nearest = nearestCentroidK(record, centroids, distance)
                clusters.getOrPut(nearest) { mutableListOf() }.add(record)
            }
            
            // Перемещение центроидов
            val newCentroids = relocateCentroidsK(clusters)
            
            // Проверка на сходимость
            if (centroids == newCentroids) {
                break
            }
            
            centroids = newCentroids
        }
        
        return clusters
    }
    
    private fun initializeCentroidsK(records: List<RecordK>, k: Int): List<CentroidK> {
        if (records.isEmpty()) {
            return emptyList()
        }
        
        val attributes = records[0].features.keys
        val mins = attributes.associateWith { attr ->
            records.minOfOrNull { it.features[attr] ?: 0.0 } ?: 0.0
        }
        val maxs = attributes.associateWith { attr ->
            records.maxOfOrNull { it.features[attr] ?: 0.0 } ?: 0.0
        }
        
        val centroids = mutableListOf<CentroidK>()
        val random = java.util.Random()
        
        for (i in 0 until k) {
            val coordinates = attributes.associateWith { attr ->
                val max = maxs[attr] ?: 0.0
                val min = mins[attr] ?: 0.0
                random.nextDouble() * (max - min) + min
            }
            centroids.add(CentroidK(coordinates))
        }
        
        return centroids
    }
    
    private fun nearestCentroidK(
        record: RecordK,
        centroids: List<CentroidK>,
        distance: DistanceK
    ): CentroidK {
        return centroids.minByOrNull { centroid ->
            distance.calculate(record.features, centroid.coordinates)
        } ?: centroids[0]
    }
    
    private fun relocateCentroidsK(
        clusters: Map<CentroidK, List<RecordK>>
    ): List<CentroidK> {
        return clusters.map { (centroid, records) ->
            averageK(centroid, records)
        }
    }
    
    private fun averageK(centroid: CentroidK, records: List<RecordK>): CentroidK {
        if (records.isEmpty()) {
            return centroid
        }
        
        val average = mutableMapOf<String, Double>()
        val attributes = records.flatMap { it.features.keys }.distinct()
        
        for (attr in attributes) {
            average[attr] = 0.0
        }
        
        for (record in records) {
            for ((attr, value) in record.features) {
                average[attr] = (average[attr] ?: 0.0) + value
            }
        }
        
        average.forEach { (key, value) ->
            average[key] = value / records.size
        }
        
        return CentroidK(average)
    }
}
```

### Оптимизация количества кластеров

```
```kotlin
object ErrorsK {
    fun sse(clusters: Map<CentroidK, List<RecordK>>, distance: DistanceK): Double {
        var sum = 0.0
        
        for ((centroid, records) in clusters) {
            for (record in records) {
                val dist = distance.calculate(record.features, centroid.coordinates)
                sum += dist * dist
            }
        }
        
        return sum
    }
}
```

### Пример использования

```
```kotlin
fun main() {
    val records = listOf(
        RecordK("Point 1", mapOf("x" to 1.0, "y" to 2.0)),
        RecordK("Point 2", mapOf("x" to 3.0, "y" to 4.0)),
        // ... more records
    )
    
    val distance = EuclideanDistanceK()
    val clusters = KMeansK.fit(records, 3, distance, 1000)
    
    println("Clusters: ${clusters.size}")
}
```

## Сложность

Время на итерацию O(k×d×n); всего O(k×d×n×i), где i — число итераций. Память: записи O(n×d), центроиды O(k×d), назначения O(n).

## Особенности

Алгоритм сходится, но может застрять в локальном минимуме; качество сильно зависит от инициализации. На больших данных может быть медленным без оптимизаций (мини-батч K-Means и т.п.).

## Применение

Сегментация клиентов, анализ данных, сжатие изображений, биоинформатика, рекомендации.

## Когда использовать

K-Means уместен, когда число кластеров можно задать или подобрать, кластеры ожидаемо компактные (близки к сферическим) и нужна простая и быстрая кластеризация. Для вытянутых или перекрывающихся групп рассмотрите GMM или иерархическую кластеризацию.

## Лучшие практики

Нормализуйте признаки (0–1 или z-score), иначе признаки с большей дисперсией будут доминировать. Используйте K-Means++ или инициализацию из случайных точек данных. K подбирайте по методу локтя, силуэту и предметной экспертизе. Делайте несколько запусков с разной инициализацией и выбирайте результат с минимальным SSE. Выбросы смещают центроиды — удаляйте их заранее или используйте робастные метрики.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Кластеры сильно различаются по размеру | Плохая инициализация или выбросы | K-Means++, несколько запусков; проверить выбросы |
| Результат меняется при каждом запуске | Случайная инициализация, локальные минимумы | Фиксировать seed; несколько запусков, выбор по SSE |
| Один признак «забивает» остальные | Разный масштаб признаков | Нормализация (0–1 или z-score) перед кластеризацией |

## Частые вопросы

**Почему K нужно задавать заранее?** K-Means минимизирует внутрикластерную сумму квадратов; без ограничения k оптимально было бы по точке в кластере. Число кластеров — гиперпараметр, его подбирают эвристиками (локоть, силуэт) или по задаче.

**Чем K-Means++ лучше случайных центроидов?** Центроиды выбираются с вероятностью, пропорциональной квадрату расстояния до ближайшего уже выбранного центроида, что уменьшает шанс слияния кластеров и ускоряет сходимость.

**Когда не использовать K-Means?** Когда кластеры не сферические, разного размера или с сильными выбросами; тогда лучше GMM, DBSCAN или иерархическая кластеризация.

## Заключение

В документе описаны неконтролируемая кластеризация и алгоритм K-Means: структуры данных, евклидово расстояние, основной цикл в Java и Kotlin, подбор k методом локтя (SSE) и практические рекомендации.

```