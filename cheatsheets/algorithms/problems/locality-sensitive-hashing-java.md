---
title: "Locality-Sensitive Hashing в Java (Locality-Sensitive Hashing Java)"
description: "LSH — хеширование, при котором похожие объекты с высокой вероятностью попадают в одни корзины. Поиск похожих без полного попарного сравнения. Библиотека java-lsh (LSHMinHash), пример поиска похожих документов по булевым векторам. Java и Kotlin."
tags:
  - algorithms
  - problems
  - locality-sensitive-hashing-java
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Locality-Sensitive Hashing в Java (`Locality-Sensitive Hashing Java`)

LSH — хеширование, при котором похожие объекты с высокой вероятностью попадают в одни корзины. Поиск похожих без полного попарного сравнения. Библиотека java-lsh (LSHMinHash), пример поиска похожих документов по булевым векторам. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Java-LSH (GitHub)](https://github.com/tdebatty/java-LSH)
- [Locality-Sensitive Hashing (Wikipedia)](https://en.wikipedia.org/wiki/Locality-sensitive_hashing)

### См. также
- [[algorithms|Хеширование и хеш-функции]] — хеширование
- [Алгоритмы поиска](../searching/) — обзор разделов

## Содержание

- [Обзор](#обзор)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
- [Важные замечания](#важные-замечания)
- [Применение LSH](#применение-lsh)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)


## Обзор

LSH (Locality-Sensitive Hashing) отображает похожие объекты в одни и те же корзины с высокой вероятностью, что позволяет искать кандидатов на похожесть без полного попарного сравнения. Ниже — использование библиотеки java-lsh (LSHMinHash) для булевых векторов документов.

## Настройка проекта

Зависимость Maven: `java-lsh`.

```xml
<!-- java-lsh: LSH MinHash для поиска похожих векторов -->
<dependency>
    <groupId>info.debatty</groupId>
    <artifactId>java-lsh</artifactId>
    <version>0.10</version>
</dependency>
```

## Реализация на Java

Документы представляются векторами (например, булевы признаки или эмбеддинги); LSHMinHash вычисляет для каждого вектора хеш — массив из `stages` целых (номера корзин). Похожие векторы с высокой вероятностью совпадают по части корзин. Поиск похожих: хранить хеши документов, для запроса подсчитать, у каких документов число совпадающих корзин с запросом ≥ threshold.

```java
// Поиск похожих документов: хеширование векторов через LSHMinHash для попадания похожих в одни корзины.
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

Параметры LSHMinHash: размер вектора, число корзин (numberOfBuckets), число стадий (stages). Больше стадий — точнее, но медленнее.

Пример вывода (похожие векторы часто попадают в одни корзины на части стадий):

```text
[0, 0, 1, 0]
[9, 3, 9, 8]
[1, 7, 8, 8]
```

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

LSH вероятностный: похожие объекты с высокой вероятностью попадают в одни корзины, но не гарантированно. Все векторы должны быть одной размерности. Параметры: больше корзин — точнее разделение, больше памяти; больше стадий — точнее, но медленнее. Для эффективности нужен достаточный объём данных.

## Применение LSH

Поиск похожих документов, обнаружение дубликатов, рекомендации (похожие пользователи/товары), кластеризация, поиск похожих изображений.

## Лучшие практики

Единый размер векторов для всех документов. Подбирать numberOfBuckets и stages под объём данных. Задавать порог (threshold) по числу совпадающих корзин. Для критичных решений дополнять LSH точной проверкой (например, косинусное сходство).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Похожие документы не находятся | Порог слишком высокий или мало стадий | Увеличить stages; снизить threshold |
| Много ложных кандидатов | Порог слишком низкий | Повысить threshold; увеличить stages; верифицировать точной метрикой |
| Разная длина векторов | Документы с разной размерностью признаков | Нормализовать размер (padding или общая размерность признаков) |

## Частые вопросы

**Чем LSH лучше полного попарного сравнения?** При N объектах полное сравнение O(N²); LSH даёт приближённый поиск за время, близкое к линейному по N, с настраиваемым компромиссом точность/скорость.

**Нужно ли верифицировать кандидатов LSH?** Для критичных решений — да: LSH отбирает кандидатов, после чего можно применить точную метрику (косинус, Jaccard) к небольшому подмножеству.

**Какой размер векторов использовать?** Зависит от представления документов (например, размер словаря для bag-of-words или размерность эмбеддингов); все векторы в одном индексе должны иметь один размер.

## Резюме

LSH уменьшает стоимость поиска похожих за счёт вероятностного отнесения к одним корзинам; java-lsh (LSHMinHash) пригоден для булевых и числовых векторов. Параметры подбирают под объём и требуемую точность; при необходимости результат уточняют точной метрикой.

## Реализация на Kotlin

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
