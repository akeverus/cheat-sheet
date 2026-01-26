# LRU Cache Implementation

Кратко: реализация кэша LRU (Least Recently Used) с использованием комбинации HashMap и двусвязного списка. Рассматривается потокобезопасная версия с использованием ReentrantReadWriteLock.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: LRU Cache](https://www.baeldung.com/java-lru-cache)
- [Java LinkedHashMap Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)

### См. также
- `./traveling-salesman-problem.md` - задача коммивояжера
- `../trees/binary-tree.md` - бинарное дерево

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Структура данных](#структура-данных)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом руководстве мы узнаем о кеше LRU и рассмотрим его реализацию на Java.

Кэш наименее недавно использованных (LRU) - это алгоритм вытеснения кеша, который упорядочивает элементы в порядке их использования. В LRU, как следует из названия, элемент, который не использовался дольше всего, будет удален из кэша.

### Пример работы

Например, если у нас есть кэш емкостью три элемента:

1. Изначально кеш пуст, и мы кладем в кеш элемент 8
2. Элементы 9 и 6 кэшируются, как и раньше
3. Но теперь емкость кеша заполнена, и чтобы поместить следующий элемент, мы должны вытеснить последний использованный элемент из кеша

### Требования к кэшу

Прежде чем мы реализуем кеш LRU в Java, полезно знать некоторые аспекты кеша:

1. Все операции должны выполняться в порядке O(1)
2. Кэш имеет ограниченный размер
3. Обязательно, чтобы все операции кэширования поддерживали параллелизм
4. Если кэш заполнен, добавление нового элемента должно вызывать стратегию LRU

## Структура данных

Как мы можем разработать структуру данных, которая могла бы выполнять такие операции, как чтение, сортировка (временная сортировка) и удаление элементов за постоянное время?

### Анализ требований

1. **LRU-кеш - это своего рода Очередь** - при повторном доступе к элементу он переходит в конец порядка вытеснения
2. **Ограниченная емкость** - эта очередь будет иметь определенную емкость, поскольку кэш имеет ограниченный размер. Всякий раз, когда вносится новый элемент, он добавляется в начало очереди. Когда происходит выселение, оно происходит из хвоста очереди
3. **Быстрый доступ** - обращение к данным в кеше должно выполняться за постоянное время, что невозможно в Queue! Но это возможно с помощью структуры данных HashMap в Java
4. **Быстрое удаление** - удаление наименее использованного элемента должно выполняться за постоянное время, что означает, что для реализации Queue мы будем использовать DoublyLinkedList вместо SingleLinkedList или массива

Итак, кеш LRU - это не что иное, как комбинация DoublyLinkedList и HashMap.

Идея состоит в том, чтобы сохранить ключи на Карте для быстрого доступа к данным внутри Очереди.

## Java Implementation

### Базовые классы

Алгоритм LRU довольно прост! Если ключ присутствует в HashMap, это попадание в кеш; в противном случае это промах кеша.

Во-первых, мы определим наш интерфейс Cache:

```java
import java.util.Optional;

public interface Cache<K, V> {
    boolean set(K key, V value);
    Optional<V> get(K key);
    int size();
    boolean isEmpty();
    void clear();
}
```

Теперь мы определим класс LRUCache, который представляет наш кеш:

```java
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
```

Мы можем создать экземпляр LRUCache определенного размера. В этой реализации мы используем коллекцию HashMap для хранения всех ссылок на LinkedListNode.

### Основной алгоритм

Теперь давайте обсудим операции с нашим LRUCache.

#### Метод put

Первый - это метод put:

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

Сначала мы находим ключ в linkedListNodeMap, в котором хранятся все ключи/ссылки. Если ключ существует, произошло попадание в кэш, и он готов извлечь CacheElement из DoublyLinkedList и переместить его на передний план.

#### Метод get

Давайте посмотрим на нашу операцию get:

```java
public Optional<V> get(K key) {
    LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
    
    if (linkedListNode != null && !linkedListNode.isEmpty()) {
        linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
        return Optional.of(linkedListNode.getElement().getValue());
    }
    
    return Optional.empty();
}
```

Как мы видим выше, эта операция проста. Сначала мы получаем узел из linkedListNodeMap, а после этого проверяем, что он не нулевой и не пустой.

#### Вспомогательные методы

Метод `updateAndMoveToFront`:

```java
public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
    if (node.isEmpty() || (this != (node.getListReference()))) {
        return dummyNode;
    }
    detach(node);
    add(newValue);
    return head;
}
```

Метод `moveToFront`:

```java
public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
    return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
}
```

### Потокобезопасная версия

До сих пор мы предполагали, что наш кеш просто использовался в однопоточной среде.

Чтобы сделать этот контейнер потокобезопасным, нам нужно синхронизировать все общедоступные методы. Давайте добавим ReentrantReadWriteLock и ConcurrentHashMap в предыдущую реализацию:

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
```

Мы предпочитаем использовать реентерабельную блокировку чтения/записи, а не объявлять методы синхронизированными, потому что это дает нам больше гибкости при принятии решения о том, когда использовать блокировку чтения и записи.

Когда мы используем writeLock для ресурса, только поток, удерживающий блокировку, может записывать или читать из ресурса. Таким образом, все другие потоки, пытающиеся либо прочитать, либо записать ресурс, должны будут ждать, пока текущий держатель блокировки не освободит его.

Это очень важно для предотвращения взаимоблокировки. Если какая-либо из операций внутри блока try терпит неудачу, мы все равно снимаем блокировку перед выходом из функции с блоком finally в конце метода.

## Kotlin Implementation

### Простая реализация с LinkedHashMap

```kotlin
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

### Потокобезопасная версия с ReentrantReadWriteLock

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
```

### Пример использования

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

### Временная сложность

- **get():** O(1) - доступ через HashMap
- **put():** O(1) - вставка/обновление в HashMap и DoublyLinkedList
- **evictElement():** O(1) - удаление из хвоста списка

### Пространственная сложность

- **Хранение элементов:** O(n) - где n - размер кэша
- **Дополнительная память:** O(n) - для HashMap и DoublyLinkedList

## Особенности

- **Производительность:** Все операции выполняются за O(1)
- **Потокобезопасность:** Поддерживается через ReentrantReadWriteLock
- **Гибкость:** Легко адаптировать для различных типов данных

## Применение

LRU кэш используется в:

- Веб-серверах (кэширование страниц)
- Базах данных (буферный пул)
- Операционных системах (кэш страниц)
- Браузерах (кэш истории)
- Приложениях (кэширование данных)

## Варианты задачи

### Вариант 1: Использование LinkedHashMap

```java
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
```

### Вариант 2: LRU с временем жизни (TTL)

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

### Используйте LRU кэш, когда:

- Нужно ограничить размер кэша
- Важны недавно использованные элементы
- Нужна быстрая производительность (O(1))
- Работаете с ограниченной памятью

### Не используйте LRU кэш, когда:

- Нужен кэш без ограничений
- Важны все элементы одинаково
- Нужен другой алгоритм вытеснения (LFU, FIFO)

## Заключение

В этом руководстве мы узнали, что такое кэш LRU, включая некоторые из его наиболее распространенных функций. Затем мы увидели один из способов реализации кэша LRU в Java и изучили некоторые из наиболее распространенных операций.

Наконец, мы рассмотрели параллелизм в действии с помощью механизма блокировки.
