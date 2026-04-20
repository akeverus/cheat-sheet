---
title: "Реализация LRU-кэша (LRU Cache)"
description: "Кэш с вытеснением наименее недавно использованных элементов (Least Recently Used): HashMap + двусвязный список для get/put за O(1), потокобезопасный вариант с ReentrantReadWriteLock, вариант на LinkedHashMap и с TTL."
tags:
  - algorithms
  - problems
  - lru-cache
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Реализация LRU-кэша (`LRU Cache`)

Кэш с вытеснением наименее недавно использованных элементов (Least Recently Used): HashMap + двусвязный список для get/put за O(1), потокобезопасный вариант с ReentrantReadWriteLock, вариант на LinkedHashMap и с TTL.

## Полезные ссылки

### Официальная документация
- [Baeldung: LRU Cache](https://www.baeldung.com/)
- [LinkedHashMap (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)

### См. также
- [[traveling-salesman-problem|Задача коммивояжера]] — TSP
- [Бинарное дерево](../trees/) — бинарное дерево

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Структура данных](#структура-данных)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

LRU вытесняет элемент, к которому дольше всего не обращались. Требования: операции get/put за O(1), ограниченная ёмкость, при переполнении — вытеснение «самого старого» по обращению. Для многопоточности — синхронизация (например ReentrantReadWriteLock).

## Структура данных

Нужны быстрый доступ по ключу (HashMap) и порядок «последнего использования» с удалением с хвоста за O(1) — двусвязный список. Ключ в map указывает на узел списка; при get узел переносится в голову; при put при переполнении удаляется хвост. Итог: HashMap + DoublyLinkedList.

## Java Implementation

Интерфейс кэша (put/get/size/clear); реализация — map ссылок на узлы двусвязного списка, при get — moveToFront, при put при переполнении — evict с хвоста.

```java
// Интерфейс кэша: put/get за O(1), ограниченная ёмкость, вытеснение LRU
import java.util.Optional;

public interface Cache<K, V> {
    boolean set(K key, V value);
    Optional<V> get(K key);
    int size();
    boolean isEmpty();
    void clear();
}
```

```text
```java
// LRUCache: map ключ→узел списка, двусвязный список для порядка LRU
import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> implements Cache<K, V> {
    private int size;
    private Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;
    private DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;

    public LRUCache(int size) {
        this.size = size;
        this.linkedListNodeMap = new HashMap<>(size);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }
}
```text

#### Метод put

```
```java
public boolean put(K key, V value) {
    CacheElement<K, V> item = new CacheElement<>(key, value);
    LinkedListNode<CacheElement<K, V>> newNode;

    if (this.linkedListNodeMap.containsKey(key)) {
        // Попадание в кэш - обновляем и перемещаем в начало
        LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
        newNode = doublyLinkedList.updateAndMoveToFront(node, item);
    } else {
        // Промах кэша - добавляем новый элемент
        if (this.size() >= this.size) {
            this.evictElement();
        }
        newNode = this.doublyLinkedList.add(item);
    }

    if (newNode.isEmpty()) {
        return false;
    }

    this.linkedListNodeMap.put(key, newNode);
    return true;
}
```

По ключу в linkedListNodeMap, в котором хранятся все ключи/ссылки. Если ключ существует, произошло попадание в кэш, и он готов извлечь **CacheElement** из **DoublyLinkedList** и переместить его на передний план.

#### Метод get

```text
```java
public Optional<V> get(K key) {
    LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);

    if (linkedListNode != null && !linkedListNode.isEmpty()) {
        linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
        return Optional.of(linkedListNode.getElement().getValue());
    }

    return Optional.empty();
}

Вспомогательные методы: отцепляем узел и добавляем в голову (`updateAndMoveToFront`), обёртка `moveToFront`.

```text
```java
public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
    if (node.isEmpty() || (this != (node.getListReference()))) {
        return dummyNode;
    }
    detach(node);
    add(newValue);
    return head;
}
```text


```
```java
public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
    return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
}
```

### Потокобезопасная версия

ReentrantReadWriteLock: readLock для get, writeLock для put/evict; при необходимости ConcurrentHashMap. Блокировку снимать в finally.

```text
```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> implements Cache<K, V> {
    private int size;
    private final Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;
    private final DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int size) {
        this.size = size;
        this.linkedListNodeMap = new ConcurrentHashMap<>(size);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }

    public boolean put(K key, V value) {
        this.lock.writeLock().lock();
        try {
            // Логика put
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Optional<V> get(K key) {
        this.lock.readLock().lock();
        try {
            // Логика get
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
```text

## Kotlin Implementation

LinkedHashMap с `accessOrder = true` и переопределением `removeEldestEntry` даёт LRU за одну строку; для потокобезопасности — ReentrantReadWriteLock или @Synchronized.

```
```kotlin
// LinkedHashMap(capacity, 0.75f, true) + removeEldestEntry { size > capacity }
class LRUCacheK<K, V>(private val capacity: Int) {
    private val cache = object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > capacity
        }
    }

    @Synchronized
    fun get(key: K): V? = cache[key]

    @Synchronized
    fun put(key: K, value: V) {
        cache[key] = value
    }

    @Synchronized
    fun size(): Int = cache.size

    @Synchronized
    fun isEmpty(): Boolean = cache.isEmpty()

    @Synchronized
    fun clear() = cache.clear()
}
```

```text
```kotlin
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ThreadSafeLRUCacheK<K, V>(private val capacity: Int) {
    private val cache = object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > capacity
        }
    }
    private val lock = ReentrantReadWriteLock()

    fun get(key: K): V? = lock.read {
        cache[key]
    }

    fun put(key: K, value: V): Boolean = lock.write {
        cache[key] = value
        true
    }

    fun size(): Int = lock.read { cache.size }

    fun isEmpty(): Boolean = lock.read { cache.isEmpty() }

    fun clear() = lock.write { cache.clear() }
}
```text

### Пример использования

```
```kotlin
fun main() {
    val cache = LRUCacheK<Int, String>(3)

    cache.put(1, "One")
    cache.put(2, "Two")
    cache.put(3, "Three")

    println(cache.get(1)) // "One"

    cache.put(4, "Four") // Вытесняет элемент 2

    println(cache.get(2)) // null
    println(cache.get(4)) // "Four"
}
```

## Сложность

get/put/evict — O(1). Память O(n) для n элементов кэша (map + список).

## Особенности

Все операции O(1). Потокобезопасность через ReentrantReadWriteLock или готовые библиотеки (Caffeine, Guava Cache).

## Применение

Веб-серверы, БД (буферный пул), ОС (кэш страниц), браузеры, кэширование данных в приложениях.

## Варианты задачи

### Вариант 1: LinkedHashMap

```text
```java
// accessOrder=true + removeEldestEntry — встроенный LRU
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCacheLinkedHashMap(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```text

### Вариант 2: LRU с TTL

```
```java
public class LRUCacheWithTTL<K, V> extends LRUCache<K, V> {
    private final Map<K, Long> timestamps;
    private final long ttl;

    public LRUCacheWithTTL(int size, long ttl) {
        super(size);
        this.timestamps = new HashMap<>();
        this.ttl = ttl;
    }

    @Override
    public Optional<V> get(K key) {
        if (timestamps.containsKey(key)) {
            long timestamp = timestamps.get(key);
            if (System.currentTimeMillis() - timestamp > ttl) {
                remove(key);
                return Optional.empty();
            }
        }
        return super.get(key);
    }
}
```

## Когда использовать

LRU уместен при ограниченном размере кэша, когда важны недавно использованные элементы и нужны операции O(1). Не подходит при отсутствии ограничения по размеру или при другой стратегии вытеснения (LFU, FIFO).

## Лучшие практики

Подбирайте capacity под горячие данные и память. В многопоточности — ReentrantReadWriteLock или Caffeine/Guava Cache. Считайте hit/miss ratio и при необходимости настраивайте размер или TTL. При cache-aside не держите блокировку на время загрузки из БД/сети.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Много промахов | Слишком малый capacity или холодные ключи | Увеличить размер; проверить паттерн доступа; рассмотреть LFU |
| Блокировки под нагрузкой | Один writeLock блокирует всех | Использовать read/write lock; рассмотреть Caffeine с асинхронной загрузкой |
| Устаревшие данные в кэше | Нет инвалидации по времени | Добавить TTL или явную инвалидацию при обновлении источника |

## Частые вопросы

**Почему HashMap + двусвязный список, а не только LinkedHashMap?** LinkedHashMap с accessOrder=true уже даёт LRU; кастомная связка HashMap + список нужна при особых требованиях (свои узлы, метаданные) или для понимания устройства.

**Нужна ли потокобезопасность в однопоточном приложении?** Нет; в многопоточном используйте реализацию с блокировками или готовую библиотеку.

**Когда предпочесть LFU?** Когда важна частота обращений, а не только давность (например, кэш с «горячими» ключами); LRU проще и часто достаточен.

## Заключение

В документе описана реализация LRU-кэша: HashMap + двусвязный список для O(1), потокобезопасный вариант, вариант на LinkedHashMap и с TTL. Для продакшена часто удобнее Caffeine или Guava Cache.

```text