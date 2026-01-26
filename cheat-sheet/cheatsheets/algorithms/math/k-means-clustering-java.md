# K-Means Clustering in Java

Кратко: реализация алгоритма кластеризации K-Means в Java. Рассматривается неконтролируемое обучение, механизм работы алгоритма, вычисление расстояний и оптимизация количества кластеров.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: K-Means Clustering](https://www.baeldung.com/java-k-means-clustering)

### См. также
- `./gradient-descent.md` - градиентный спуск
- `./distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Неконтролируемое обучение](#неконтролируемое-обучение)
- [Механизм работы K-Means](#механизм-работы-k-means)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Оптимизация количества кластеров](#оптимизация-количества-кластеров)
- [Сложность](#сложность)

## Описание алгоритма

Кластеризация - это общий термин для класса неконтролируемых алгоритмов для обнаружения групп вещей, людей или идей, которые тесно связаны друг с другом.

В этом уроке мы собираемся, во-первых, пролить свет на эти концепции. Затем мы увидим, как они могут проявить себя в Java.

## Неконтролируемое обучение

Прежде чем использовать большинство алгоритмов обучения, мы должны каким-то образом передать им некоторые образцы данных и позволить алгоритму учиться на этих данных. В терминологии машинного обучения мы называем этот пример обучающими данными. Кроме того, весь процесс известен как процесс обучения.

Мы можем классифицировать алгоритмы обучения на основе количества контроля, необходимого им в процессе обучения. Два основных типа алгоритмов обучения в этой категории:

1. **Обучение с учителем:** в алгоритмах с учителем данные обучения должны включать фактическое решение для каждой точки. Например, если мы собираемся обучить наш алгоритм фильтрации спама, мы передаем алгоритму как примеры писем, так и их метки, т.е. спам или не спам. С математической точки зрения мы собираемся вывести f(x) из тренировочного набора, включающего как xs, так и ys.

2. **Неконтролируемое обучение:** когда в обучающих данных нет меток, алгоритм является неконтролируемым. Например, у нас есть много данных о музыкантах, и мы собираемся обнаружить в этих данных группы похожих музыкантов.

Кластеризация - это неконтролируемый алгоритм для обнаружения групп похожих вещей, идей или людей. В отличие от контролируемых алгоритмов, мы не обучаем алгоритмы кластеризации на примерах известных меток. Вместо этого кластеризация пытается найти структуры в обучающем наборе, где ни одна точка данных не является меткой.

K-Means - это алгоритм кластеризации с одним фундаментальным свойством: количество кластеров определяется заранее. В дополнение к K-Means существуют другие типы алгоритмов кластеризации, такие как иерархическая кластеризация, распространение сходства или спектральная кластеризация.

## Механизм работы K-Means

Предположим, наша цель - найти несколько похожих групп в наборе данных.

### Шаги алгоритма:

1. **Инициализация:** K-Means начинается с k случайно расположенных центроидов.
   - Центроиды, как следует из их названия, являются центральными точками кластеров.

2. **Назначение:** Затем мы назначаем каждую существующую точку данных ее ближайшему центроиду.

3. **Обновление:** После присвоения мы перемещаем центроиды в среднее расположение назначенных ему точек. Помните, что центроиды должны быть центральными точками кластеров.

4. **Повторение:** Текущая итерация завершается каждый раз, когда мы завершаем перемещение центроидов. Мы повторяем эти итерации до тех пор, пока назначение между несколькими последовательными итерациями не перестанет меняться.

Когда алгоритм завершится, кластеры будут найдены, как и ожидалось.

## Java Implementation

### Структуры данных

При моделировании различных обучающих наборов данных нам нужна структура данных для представления атрибутов модели и соответствующих им значений. Например, у музыканта может быть атрибут жанра со значением Рок. Обычно мы используем термин функция для обозначения комбинации атрибута и его значения.

Чтобы подготовить набор данных для конкретного алгоритма обучения, мы обычно используем общий набор числовых атрибутов, которые можно использовать для сравнения различных элементов.

Вектор признаков для таких исполнителей, как Linkin Park, таков: [рок -> 7890, ню-метал -> 700, альтернатива -> 520, поп -> 3].

Поскольку числовые векторы являются такими универсальными структурами данных, мы собираемся представлять объекты с их помощью. Вот как мы реализуем векторы признаков в Java:

```java
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
```

Центроиды находятся в том же пространстве, что и обычные функции, поэтому мы можем представить их аналогично функциям:

```java
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

В каждой итерации K-Means нам нужен способ найти ближайший центроид к каждому элементу в наборе данных. Один из самых простых способов вычислить расстояние между двумя векторами признаков - использовать Евклидово расстояние.

Евклидово расстояние между двумя векторами, такими как [p1, q1] и [p2, q2], равно:

```
√((p1 - p2)² + (q1 - q2)²)
```

Давайте реализуем эту функцию на Java. Во-первых, абстракция:

```java
public interface Distance {
    double calculate(Map<String, Double> f1, Map<String, Double> f2);
}
```

В дополнение к евклидову расстоянию существуют другие подходы к вычислению расстояния или сходства между различными элементами, такие как коэффициент корреляции Пирсона. Эта абстракция позволяет легко переключаться между различными показателями расстояния.

Давайте посмотрим на реализацию для евклидова расстояния:

```java
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

Сначала мы вычисляем сумму квадратов разностей между соответствующими записями. Затем, применяя функцию sqrt, мы вычисляем фактическое евклидово расстояние.

### Основной алгоритм

Теперь, когда у нас есть несколько необходимых абстракций, пришло время написать нашу реализацию K-Means. Вот краткий обзор сигнатуры нашего метода:

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
```

Давайте разберем сигнатуру этого метода:

1. Набор данных представляет собой набор векторов признаков. Поскольку каждый вектор признаков является записью, тип набора данных - `List<Record>`.
2. Параметр k определяет количество кластеров, которое мы должны предоставить заранее
3. Distance инкапсулирует способ, которым мы собираемся вычислить разницу между двумя функциями
4. K-Means завершается, когда назначение перестает меняться в течение нескольких последовательных итераций. В дополнение к этому условию завершения мы также можем установить верхнюю границу количества итераций. Аргумент maxIterations определяет верхнюю границу
5. Когда K-Means завершается, каждый центроид должен иметь несколько назначенных функций, поэтому мы используем `Map<Centroid, List<Record>>` в качестве возвращаемого типа. По сути, каждая запись карты соответствует кластеру.

Первым шагом является создание k случайно расположенных центроидов:

```java
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
```

Хотя каждый центроид может содержать совершенно случайные координаты, рекомендуется генерировать случайные координаты между минимальным и максимальным возможными значениями для каждого атрибута. Генерация случайных центроидов без учета диапазона возможных значений приведет к более медленной сходимости алгоритма.

Теперь мы можем присвоить каждую запись одному из этих случайных центроидов:

```java
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
```

Если после одной итерации центроид не содержит никаких назначений, то мы не будем его перемещать. В противном случае мы должны переместить координату центроида для каждого атрибута в среднее положение всех назначенных записей:

```java
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
```

На каждой итерации, после присвоения всех записей их ближайшему центроиду, сначала мы должны сравнить текущие присвоения с последней итерацией:

```java
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

Last.fm создает подробный профиль музыкальных вкусов каждого пользователя, записывая сведения о том, что пользователь слушает.

В этом разделе мы собираемся найти кластеры похожих исполнителей. Чтобы создать набор данных, подходящий для этой задачи, мы будем использовать три API от Last.fm:

1. API для получения коллекции лучших исполнителей на Last.fm.
2. Еще один API для поиска популярных тегов. Каждый пользователь может пометить исполнителя чем-либо, например, роком.
3. И API для получения лучших тегов для исполнителя, упорядоченных по популярности.

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

Одним из фундаментальных свойств K-Means является тот факт, что мы должны заранее определить количество кластеров. До сих пор мы использовали статическое значение k, но определение этого значения может оказаться сложной задачей. Существует два распространенных способа подсчета количества кластеров:

1. **Базовые знания:** Если нам повезло, что мы так много знаем о домене, то мы сможем просто угадать правильное число.
2. **Математическая эвристика:** Мы можем применить несколько эвристик, таких как метод локтя или метод силуэта, чтобы получить представление о количестве кластеров.

### Метод локтя

Чтобы использовать метод локтя, мы должны сначала вычислить разницу между центроидами каждого кластера и всеми его элементами. По мере того, как мы группируем в кластер больше несвязанных элементов, расстояние между центроидом и его элементами увеличивается, следовательно, качество кластера снижается.

Один из способов выполнить это вычисление расстояния состоит в том, чтобы использовать Sum of Squared Errors. Сумма квадратов ошибок или SSE равна сумме квадратов разностей между центроидом и всеми его элементами:

```java
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

Затем мы можем запустить алгоритм K-Means для разных значений k и вычислить SSE для каждого из них:

```java
List<Record> records = // the dataset;
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

Идея метода локтя состоит в том, чтобы найти подходящее значение k таким образом, чтобы SSE резко уменьшалась вокруг этого значения.

## Kotlin Implementation

### Структуры данных

```kotlin
data class RecordK(
    val description: String,
    val features: Map<String, Double>
)

data class CentroidK(
    val coordinates: Map<String, Double>
)
```

### Интерфейс Distance

```kotlin
interface DistanceK {
    fun calculate(f1: Map<String, Double>, f2: Map<String, Double>): Double
}
```

### Евклидово расстояние

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

### Основной алгоритм K-Means

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

### Временная сложность

- **Инициализация центроидов:** O(k × d × n) - где k - количество кластеров, d - размерность, n - количество точек
- **Назначение точек:** O(k × d × n) - для каждой итерации
- **Обновление центроидов:** O(k × d × n) - для каждой итерации
- **Общая сложность:** O(k × d × n × i) - где i - количество итераций

### Пространственная сложность

- **Хранение данных:** O(n × d) - для всех записей
- **Хранение центроидов:** O(k × d) - для всех центроидов
- **Хранение кластеров:** O(n) - для назначений

## Особенности

- **Сходимость:** Алгоритм гарантированно сходится, но может попасть в локальный минимум
- **Инициализация:** Качество результата сильно зависит от начальных центроидов
- **Масштабируемость:** Может быть медленным для больших наборов данных

## Применение

K-Means кластеризация используется в:

- Сегментации клиентов
- Анализе данных
- Обработке изображений
- Биоинформатике
- Рекомендательных системах

## Когда использовать

### Используйте K-Means, когда:

- Количество кластеров известно заранее
- Кластеры имеют сферическую форму
- Нужен быстрый алгоритм кластеризации
- Работаете с большими наборами данных

## Заключение

В этом уроке мы сначала рассмотрели несколько важных концепций машинного обучения. Затем мы познакомились с механикой алгоритма кластеризации K-Means. Наконец, мы написали простую реализацию для K-Means, протестировали наш алгоритм с реальным набором данных и рассмотрели методы оптимизации количества кластеров.
