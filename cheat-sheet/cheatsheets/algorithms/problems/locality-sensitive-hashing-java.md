# Хеширование с учетом местоположения с использованием Java-LSH

Руководство по использованию алгоритма хеширования с учетом местоположения (LSH) для поиска похожих элементов в больших наборах данных.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java-LSH Library](https://github.com/tdebatty/java-LSH)
- [Locality Sensitive Hashing - Wikipedia](https://en.wikipedia.org/wiki/Locality-sensitive_hashing)

### См. также
- [Хеширование](../problems/algorithms.md)
- [Поиск похожих элементов](../searching/)

## Содержание

- [Обзор](#обзор)
- [Что такое LSH?](#что-такое-lsh)
- [Настройка проекта](#настройка-проекта)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Настройка параметров](#настройка-параметров)
- [Полный пример](#полный-пример)

## Обзор

Алгоритм хеширования с учетом местоположения (LSH) хэширует входные элементы, чтобы аналогичные элементы с высокой вероятностью были сопоставлены с одними и теми же сегментами.

В этой быстрой статье мы будем использовать библиотеку java-lsh, чтобы продемонстрировать простой вариант использования этого алгоритма.

## Что такое LSH?

LSH (Locality Sensitive Hashing) - это алгоритм, который хэширует входные элементы таким образом, что похожие элементы с высокой вероятностью попадают в одни и те же "корзины" (buckets). Это делает LSH особенно полезным для:

- Поиска похожих документов
- Обнаружения дубликатов
- Кластеризации данных
- Рекомендательных систем

## Настройка проекта

Для начала нам нужно добавить зависимость Maven в библиотеку java-lsh:

```xml
<dependency>
    <groupId>info.debatty</groupId>
    <artifactId>java-lsh</artifactId>
    <version>0.10</version>
</dependency>
```

## Java Implementation

### Базовое использование

LSH имеет множество возможных применений, но мы рассмотрим один конкретный пример.

Предположим, у нас есть база данных документов и мы хотим реализовать поисковую систему, которая сможет идентифицировать похожие документы.

Мы можем использовать LSH как часть этого решения:

1. Каждый документ можно преобразовать в вектор чисел или логических значений - например, мы могли бы использовать алгоритм word2vec для преобразования слов и документов в вектора чисел.
2. Когда у нас есть вектор, представляющий каждый документ, мы можем использовать алгоритм LSH для вычисления хэша для каждого вектора, и благодаря характеристикам LSH документы, представленные как похожие векторы, будут иметь похожий или одинаковый хеш.
3. В результате, по заданному вектору документа мы можем найти N номеров векторов с похожим хешем и вернуть соответствующие документы конечному пользователю.

### Пример: Поиск похожих документов

Мы будем использовать библиотеку java-lsh для вычисления хэшей для наших входных векторов. Мы не будем освещать само преобразование, так как это огромная тема, выходящая за рамки данной статьи.

Однако предположим, что у нас есть три входных вектора, которые преобразованы из набора из трех документов, представленных в форме, которую можно использовать в качестве входных данных для алгоритма LSH:

```java
import info.debatty.java.lsh.LSHMinHash;
import java.util.Arrays;

public class LSHExample {
    
    public static void main(String[] args) {
        // Предположим, что векторы представляют документы
        // true означает наличие слова/признака, false - отсутствие
        boolean[] vector1 = new boolean[] {true, true, true, true, true};
        boolean[] vector2 = new boolean[] {false, false, false, true, false};
        boolean[] vector3 = new boolean[] {false, false, true, true, false};
        
        // Обратите внимание: vector1 сильно отличается от vector2 и vector3
        // vector2 и vector3 очень похожи друг на друга
        
        // Создаем экземпляр LSHMinHash
        int sizeOfVectors = 5;
        int numberOfBuckets = 10;
        int stages = 4;
        
        LSHMinHash lsh = new LSHMinHash(stages, numberOfBuckets, sizeOfVectors);
        
        // Вычисляем хеши
        int[] firstHash = lsh.hash(vector1);
        int[] secondHash = lsh.hash(vector2);
        int[] thirdHash = lsh.hash(vector3);
        
        System.out.println("Hash для vector1: " + Arrays.toString(firstHash));
        System.out.println("Hash для vector2: " + Arrays.toString(secondHash));
        System.out.println("Hash для vector3: " + Arrays.toString(thirdHash));
    }
}
```

Обратите внимание, что в рабочем приложении количество входных векторов должно быть намного больше, чтобы использовать алгоритм LSH, но для этой демонстрации мы будем придерживаться только трех векторов.

Важно отметить, что первый вектор сильно отличается от второго и третьего, тогда как второй и третий векторы очень похожи друг на друга.

### Настройка параметров

Давайте создадим экземпляр класса LSHMinHash. Нам нужно передать ему размер входных векторов - все входные векторы должны иметь одинаковый размер. Нам также нужно указать, сколько хеш-багет мы хотим и сколько этапов вычислений (итераций) должен выполнять LSH:

```java
int sizeOfVectors = 5;        // Размер каждого вектора
int numberOfBuckets = 10;     // Количество корзин для хеширования
int stages = 4;               // Количество итераций LSH

LSHMinHash lsh = new LSHMinHash(stages, numberOfBuckets, sizeOfVectors);
```

Мы указываем, что все векторы, которые будут хэшироваться алгоритмами, должны хешироваться среди десяти сегментов. Мы также хотим иметь четыре итерации LSH для вычисления хэшей.

Чтобы вычислить хэш для каждого вектора, мы передаем вектор методу `hash()`:

```java
int[] firstHash = lsh.hash(vector1);
int[] secondHash = lsh.hash(vector2);
int[] thirdHash = lsh.hash(vector3);

System.out.println(Arrays.toString(firstHash));
System.out.println(Arrays.toString(secondHash));
System.out.println(Arrays.toString(thirdHash));
```

Выполнение этого кода приведет к выводу, подобному:

```
[0, 0, 1, 0]
[9, 3, 9, 8]
[1, 7, 8, 8]
```

Глядя на каждый выходной массив, мы видим хеш-значения, рассчитанные на каждой из четырех итераций для соответствующего входного вектора. Первая строка показывает результаты хеширования для первого вектора, вторая строка для второго вектора и третья строка для третьего вектора.

После четырех итераций LSH дал результаты, как мы и ожидали - LSH вычислил одно и то же значение хеш-функции (8) для второго и третьего векторов, которые были похожи друг на друга, и другое значение хеш-функции (0) для первого вектора, который был отличается от второго и третьего векторов.

## Полный пример

```java
import info.debatty.java.lsh.LSHMinHash;
import java.util.*;

public class DocumentSimilaritySearch {
    
    private LSHMinHash lsh;
    private Map<String, boolean[]> documents;
    private Map<String, int[]> documentHashes;
    
    public DocumentSimilaritySearch(int vectorSize, int buckets, int stages) {
        this.lsh = new LSHMinHash(stages, buckets, vectorSize);
        this.documents = new HashMap<>();
        this.documentHashes = new HashMap<>();
    }
    
    public void addDocument(String docId, boolean[] vector) {
        documents.put(docId, vector);
        documentHashes.put(docId, lsh.hash(vector));
    }
    
    public List<String> findSimilarDocuments(String queryDocId, int threshold) {
        if (!documentHashes.containsKey(queryDocId)) {
            return Collections.emptyList();
        }
        
        int[] queryHash = documentHashes.get(queryDocId);
        List<String> similarDocs = new ArrayList<>();
        
        for (Map.Entry<String, int[]> entry : documentHashes.entrySet()) {
            if (entry.getKey().equals(queryDocId)) {
                continue;
            }
            
            int[] docHash = entry.getValue();
            int matchingBuckets = countMatchingBuckets(queryHash, docHash);
            
            if (matchingBuckets >= threshold) {
                similarDocs.add(entry.getKey());
            }
        }
        
        return similarDocs;
    }
    
    private int countMatchingBuckets(int[] hash1, int[] hash2) {
        int matches = 0;
        for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] == hash2[i]) {
                matches++;
            }
        }
        return matches;
    }
    
    public static void main(String[] args) {
        // Создаем систему поиска похожих документов
        DocumentSimilaritySearch search = new DocumentSimilaritySearch(5, 10, 4);
        
        // Добавляем документы
        search.addDocument("doc1", new boolean[] {true, true, true, true, true});
        search.addDocument("doc2", new boolean[] {false, false, false, true, false});
        search.addDocument("doc3", new boolean[] {false, false, true, true, false});
        search.addDocument("doc4", new boolean[] {true, true, false, false, false});
        
        // Ищем похожие документы
        List<String> similar = search.findSimilarDocuments("doc2", 2);
        System.out.println("Документы, похожие на doc2: " + similar);
        // Ожидаемый результат: [doc3], так как doc2 и doc3 имеют похожие векторы
    }
}
```

## Важные замечания

LSH - это алгоритм, основанный на вероятности, поэтому мы не можем быть уверены, что два одинаковых вектора окажутся в одном и том же хеш-корзине. Тем не менее, когда у нас есть достаточно большое количество входных векторов, алгоритм выдает результаты, которые будут иметь высокую вероятность присвоения похожих векторов одним и тем же корзинам.

### Выбор параметров

- **numberOfBuckets**: Больше корзин = более точное разделение, но больше памяти
- **stages**: Больше этапов = более точные результаты, но медленнее вычисления
- **vectorSize**: Должен соответствовать размеру ваших входных векторов

### Ограничения

1. Все векторы должны иметь одинаковый размер
2. Результаты вероятностные, не гарантированные
3. Требуется достаточно большое количество данных для эффективной работы

## Применение LSH

LSH особенно полезен для:

1. **Поиск похожих документов** - быстро найти документы, похожие на запрос
2. **Обнаружение дубликатов** - найти почти идентичные записи в базе данных
3. **Рекомендательные системы** - найти похожих пользователей или товары
4. **Кластеризация** - группировка похожих элементов
5. **Обработка изображений** - поиск похожих изображений

## Резюме

Когда мы имеем дело с массивными наборами данных, LSH может быть удобным алгоритмом для быстрого поиска похожих элементов.

Ключевые моменты:
- LSH хэширует похожие элементы в одни и те же корзины
- Алгоритм вероятностный, но эффективный для больших наборов данных
- Требует настройки параметров для оптимальной производительности
- Особенно полезен для поиска похожих документов и обнаружения дубликатов

LSH позволяет нам избежать полного попарного сравнения всех элементов, что было бы слишком медленно для больших наборов данных.

## Kotlin Implementation

### Базовое использование

```kotlin
import info.debatty.java.lsh.LSHMinHash

fun main() {
    // Предположим, что векторы представляют документы
    val vector1 = booleanArrayOf(true, true, true, true, true)
    val vector2 = booleanArrayOf(false, false, false, true, false)
    val vector3 = booleanArrayOf(false, false, true, true, false)
    
    // Создаем экземпляр LSHMinHash
    val sizeOfVectors = 5
    val numberOfBuckets = 10
    val stages = 4
    
    val lsh = LSHMinHash(stages, numberOfBuckets, sizeOfVectors)
    
    // Вычисляем хеши
    val firstHash = lsh.hash(vector1)
    val secondHash = lsh.hash(vector2)
    val thirdHash = lsh.hash(vector3)
    
    println("Hash для vector1: ${firstHash.contentToString()}")
    println("Hash для vector2: ${secondHash.contentToString()}")
    println("Hash для vector3: ${thirdHash.contentToString()}")
}
```

### Поиск похожих документов

```kotlin
import info.debatty.java.lsh.LSHMinHash
import java.util.*

class DocumentSimilaritySearchK(
    vectorSize: Int,
    buckets: Int,
    stages: Int
) {
    private val lsh: LSHMinHash = LSHMinHash(stages, buckets, vectorSize)
    private val documents = mutableMapOf<String, BooleanArray>()
    private val documentHashes = mutableMapOf<String, IntArray>()
    
    fun addDocument(docId: String, vector: BooleanArray) {
        documents[docId] = vector
        documentHashes[docId] = lsh.hash(vector)
    }
    
    fun findSimilarDocuments(queryDocId: String, threshold: Int): List<String> {
        val queryHash = documentHashes[queryDocId] ?: return emptyList()
        val similarDocs = mutableListOf<String>()
        
        for ((docId, docHash) in documentHashes) {
            if (docId == queryDocId) continue
            
            val matchingBuckets = countMatchingBuckets(queryHash, docHash)
            if (matchingBuckets >= threshold) {
                similarDocs.add(docId)
            }
        }
        
        return similarDocs
    }
    
    private fun countMatchingBuckets(hash1: IntArray, hash2: IntArray): Int {
        var matches = 0
        for (i in hash1.indices) {
            if (hash1[i] == hash2[i]) {
                matches++
            }
        }
        return matches
    }
}

fun main() {
    val search = DocumentSimilaritySearchK(5, 10, 4)
    
    search.addDocument("doc1", booleanArrayOf(true, true, true, true, true))
    search.addDocument("doc2", booleanArrayOf(false, false, false, true, false))
    search.addDocument("doc3", booleanArrayOf(false, false, true, true, false))
    search.addDocument("doc4", booleanArrayOf(true, true, false, false, false))
    
    val similar = search.findSimilarDocuments("doc2", 2)
    println("Документы, похожие на doc2: $similar")
}
```
