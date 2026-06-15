---
title: "Вопросы на собеседовании: Java Collections"
description: "Комплексное руководство по вопросам собеседования на тему Java Collections Framework для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java, mermaid-диаграммы, best practices и troubleshooting."
tags:
  - interview
  - programming-languages
  - java-collections-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Collections"
  - "Java Collections Framework"
  - "Java Collections собеседование"
prerequisites: []
next: []
updated: "2026-05-20"
---
# Вопросы на собеседовании: `Java Collections`

Комплексное руководство по вопросам собеседования на тему `Java Collections Framework` для `Senior Java Developer`. Включает детальные объяснения концепций, практические примеры на `Java`, `mermaid`-диаграммы, best practices и troubleshooting.

## Полезные ссылки

### Официальная документация

- [Java Collections Framework Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/package-summary.html)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)
- [Java Collections Interview Questions — Baeldung](https://www.baeldung.com/java-collections-interview-questions) — вопросы с ответами по Collections Framework
- [The Java HashMap Under the Hood — Baeldung](https://www.baeldung.com/java-hashmap-advanced) — внутреннее устройство HashMap
- [Java HashMap Load Factor — Baeldung](https://www.baeldung.com/java-hashmap-load-factor) — load factor и rehashing
- [Guide to the Java Queue Interface — Baeldung](https://www.baeldung.com/java-queue) — очереди: PriorityQueue, ArrayDeque, BlockingQueue

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Иерархия и основные интерфейсы**
- [Q1. (!) Опишите иерархию типов `Collection`](#q1--опишите-иерархию-типов-collection)
- [Q2. (!) Реализации `Collection` и оценка их быстродействия](#q2--реализации-collection-и-оценка-их-быстродействия)
- [Q3. (!) Что такое `Map` и чем отличается от `Collection`?](#q3--что-такое-map-и-чем-отличается-от-collection)
- [Q4. Как выбрать между `List`, `Set` и `Map`?](#q4-как-выбрать-между-list-set-и-map)
- [Q5. Что такое интерфейс `Iterable` и как он связан с `Collection`?](#q5-что-такое-интерфейс-iterable-и-как-он-связан-с-collection)

**List (ArrayList, LinkedList)**
- [Q6. (!) В чем разница между `LinkedList` и `ArrayList`?](#q6--в-чем-разница-между-linkedlist-и-arraylist)
- [Q7. Как работает автоматическое расширение `ArrayList`?](#q7-как-работает-автоматическое-расширение-arraylist)
- [Q8. Что такое `CopyOnWriteArrayList`?](#q8-что-такое-copyonwritearraylist)
- [Q9. В чём разница между `List.of()` и `Arrays.asList()`?](#q9-в-чём-разница-между-listof-и-arraysaslist)

**Set (HashSet, TreeSet, LinkedHashSet)**
- [Q10. (!) В чем разница между `HashSet` и `TreeSet`?](#q10--в-чем-разница-между-hashset-и-treeset)
- [Q11. Что такое `LinkedHashSet` и когда его использовать?](#q11-что-такое-linkedhashset-и-когда-его-использовать)

**Map (HashMap, TreeMap, LinkedHashMap)**
- [Q12. (!) Что такое `HashMap` и как он устроен внутри?](#q12--что-такое-hashmap-и-как-он-устроен-внутри)
- [Q13. (!) Что происходит при коллизиях в `HashMap` и как работает treeification?](#q13--что-происходит-при-коллизиях-в-hashmap-и-как-работает-treeification)
- [Q14. (!) Какова цель параметров `initialCapacity` и `loadFactor`?](#q14--какова-цель-параметров-initialcapacity-и-loadfactor)
- [Q15. (!) Что такое `ConcurrentHashMap` и чем отличается от `HashMap`?](#q15--что-такое-concurrenthashmap-и-чем-отличается-от-hashmap)
- [Q16. (!) Контракт `equals` и `hashCode` для ключей `Map`](#q16--контракт-equals-и-hashcode-для-ключей-map)
- [Q17. Что такое `TreeMap` и какая сложность операций?](#q17-что-такое-treemap-и-какая-сложность-операций)
- [Q18. В чём разница между `HashMap` и `LinkedHashMap`?](#q18-в-чём-разница-между-hashmap-и-linkedhashmap)
- [Q19. Как реализовать `LRU`-кэш на `LinkedHashMap`?](#q19-как-реализовать-lru-кэш-на-linkedhashmap)
- [Q20. Что такое `WeakHashMap`?](#q20-что-такое-weakhashmap)
- [Q21. Что такое `IdentityHashMap`?](#q21-что-такое-identityhashmap)
- [Q22. В чём разница между `Collections.synchronizedMap` и `ConcurrentHashMap`?](#q22-в-чём-разница-между-collectionssynchronizedmap-и-concurrenthashmap)
- [Q23. Какие методы `Java 8+` добавлены в `Map`?](#q23-какие-методы-java-8-добавлены-в-map)

**Queue и Deque**
- [Q24. Что такое `Queue` и какие реализации?](#q24-что-такое-queue-и-какие-реализации)
- [Q25. Что такое `Deque` и когда использовать?](#q25-что-такое-deque-и-когда-использовать)
- [Q26. (!) Что такое `BlockingQueue` и паттерн `producer-consumer`?](#q26--что-такое-blockingqueue-и-паттерн-producer-consumer)
- [Q27. В чём разница между `ArrayDeque` и `LinkedList`?](#q27-в-чём-разница-между-arraydeque-и-linkedlist)

**Enum коллекции**
- [Q28. Что такое `EnumSet` и `EnumMap`?](#q28-что-такое-enumset-и-enummap)

**Итераторы и модификация**
- [Q29. (!) В чем разница между `fail-fast` и `fail-safe` итераторами?](#q29--в-чем-разница-между-fail-fast-и-fail-safe-итераторами)
- [Q30. Как итерировать и удалять элементы?](#q30-как-итерировать-и-удалять-элементы)
- [Q31. Что такое `Spliterator`?](#q31-что-такое-spliterator)
- [Q32. Что такое `ConcurrentModificationException`?](#q32-что-такое-concurrentmodificationexception)

**Сортировка и сравнение**
- [Q33. (!) Как использовать `Comparable` и `Comparator`?](#q33--как-использовать-comparable-и-comparator)
- [Q34. Какие алгоритмы сортировки используются в `Java`?](#q34-какие-алгоритмы-сортировки-используются-в-java)

**Immutable коллекции и утилиты**
- [Q35. Что такое `Collections.unmodifiableList`?](#q35-что-такое-collectionsunmodifiablelist)
- [Q36. (!) Что такое Immutable коллекции в `Java 9+`?](#q36--что-такое-immutable-коллекции-в-java-9)
- [Q37. Какие утилитные методы предоставляет класс `Collections`?](#q37-какие-утилитные-методы-предоставляет-класс-collections)

**Практические вопросы**
- [Q38. Почему нельзя использовать мутабельные объекты как ключи `HashMap`?](#q38-почему-нельзя-использовать-мутабельные-объекты-как-ключи-hashmap)
- [Q39. Как выбрать правильную коллекцию для конкретной задачи?](#q39-как-выбрать-правильную-коллекцию-для-конкретной-задачи)
- [Q40. Какие коллекции из сторонних библиотек стоит знать?](#q40-какие-коллекции-из-сторонних-библиотек-стоит-знать)
- [Q41. (!) Что такое `SequencedCollection`, `SequencedSet`, `SequencedMap` (Java 21)?](#q41--что-такое-sequencedcollection-sequencedset-sequencedmap-java-21)

**Продвинутые коллекции**
- [Q42. (!) Как устроен `ConcurrentHashMap` изнутри и почему он быстрее `Hashtable`?](#q42--как-устроен-concurrenthashmap-изнутри-и-почему-он-быстрее-hashtable)
- [Q43. (!) Как реализовать LRU-кэш на `LinkedHashMap`?](#q43--как-реализовать-lru-кэш-на-linkedhashmap)
- [Q44. Что такое `NavigableMap` и как использовать методы навигации `TreeMap`?](#q44-что-такое-navigablemap-и-как-использовать-методы-навигации-treemap)
- [Q45. Что такое `PriorityQueue` и как реализовать кастомный порядок?](#q45-что-такое-priorityqueue-и-как-реализовать-кастомный-порядок)
- [Q46. Что такое `WeakHashMap` и когда его использовать?](#q46-что-такое-weakhashmap-и-когда-его-использовать)

---

## Q1. (!) Опишите иерархию типов `Collection`

Иерархия типов `Collection` в `Java` центрируется вокруг интерфейса `Iterable`, от которого наследуется `Collection` — базовый интерфейс, описывающий основные операции: добавление, удаление, проверка наличия элемента и перебор.

Дерево наследования интерфейсов:

- `Iterable<T>` → `Collection<T>` (корень всей ветви коллекций)
  - `Collection<T>` → `List<T>`
  - `Collection<T>` → `Set<T>` → `SortedSet<T>` → `NavigableSet<T>`
  - `Collection<T>` → `Queue<T>` → `Deque<T>`
- `Map<K,V>` стоит отдельно (не наследуется от `Collection`) → `SortedMap<K,V>` → `NavigableMap<K,V>`

Какие реализации соответствуют каждому интерфейсу:

- `List<T>` → `ArrayList`, `LinkedList`, `CopyOnWriteArrayList`
- `Set<T>` → `HashSet`, `LinkedHashSet`
- `NavigableSet<T>` → `TreeSet`
- `Queue<T>` → `PriorityQueue`
- `Deque<T>` → `ArrayDeque`, `LinkedList` (`LinkedList` реализует и `List`, и `Deque`)
- `Map<K,V>` → `HashMap`, `LinkedHashMap`, `ConcurrentHashMap`
- `NavigableMap<K,V>` → `TreeMap`

Ключевые узлы этой иерархии — `Collection<T>` (общий корень коллекций) и `Map<K,V>` (отдельная ветвь пар ключ-значение).

Основные ветви:

- **`List`** — упорядоченная последовательность с дубликатами и индексированным доступом
- **`Set`** — хранит только уникальные элементы
- **`Queue`/`Deque`** — очереди (`FIFO`) и двусторонние очереди
- **`Map`** — пары ключ-значение; **не** наследуется от `Collection`

| Интерфейс | Порядок элементов | Дубликаты | Индекс/ключ | Основное применение |
|-----------|-------------------|-----------|-------------|---------------------|
| `List` | Порядок вставки | Да | По индексу | Упорядоченные данные |
| `Set` | Нет (кроме `LinkedHashSet`, `TreeSet`) | Нет | Нет | Уникальные элементы |
| `Queue` | `FIFO` | Да | Нет | Очереди задач |
| `Deque` | Двунаправленный | Да | Нет | Стеки и двусторонние очереди |
| `Map` | Нет (кроме `LinkedHashMap`, `TreeMap`) | Нет (ключи) | По ключу | Пары ключ-значение |

## Q2. (!) Реализации `Collection` и оценка их быстродействия

Сложность операций напрямую вытекает из внутренней структуры данных: массив даёт `O(1)` доступ по индексу, но `O(n)` на вставку в середину; хэш-таблица — `O(1)` на поиск ценой отсутствия порядка; дерево — `O(log n)`, зато ключи всегда отсортированы. Выбор коллекции — это выбор того, какие операции вы хотите сделать дешёвыми.

| Реализация | get/contains | add | remove | Итерация | Внутренняя структура |
|------------|-------------|-----|--------|----------|---------------------|
| `ArrayList` | `O(1)` / `O(n)` | `O(1)`* | `O(n)` | `O(n)` | Динамический массив |
| `LinkedList` | `O(n)` | `O(1)` | `O(1)`** | `O(n)` | Двусвязный список |
| `HashSet` | `O(1)` | `O(1)` | `O(1)` | `O(n)` | `HashMap` внутри |
| `TreeSet` | `O(log n)` | `O(log n)` | `O(log n)` | `O(n)` | Красно-чёрное дерево |
| `LinkedHashSet` | `O(1)` | `O(1)` | `O(1)` | `O(n)` | `HashMap` + двусвязный список |
| `HashMap` | `O(1)` | `O(1)` | `O(1)` | `O(n)` | Массив bucket'ов |
| `TreeMap` | `O(log n)` | `O(log n)` | `O(log n)` | `O(n)` | Красно-чёрное дерево |
| `PriorityQueue` | `O(n)` | `O(log n)` | `O(log n)` | `O(n)` | Двоичная куча |
| `ArrayDeque` | `O(n)` | `O(1)` | `O(1)` | `O(n)` | Циклический массив |
| `EnumSet` | `O(1)` | `O(1)` | `O(1)` | `O(n)` | Битовая маска |

\* амортизированно, \*\* при удалении из начала/конца

> На собеседовании важно не просто назвать сложность, а объяснить **почему** — связать с внутренней структурой данных.

## Q3. (!) Что такое `Map` и чем отличается от `Collection`?

Интерфейс `Map` представляет отображение ключ-значение. Каждый ключ уникален, значения могут повторяться. `Map` **не наследуется** от `Collection`, потому что у них несовместимые контракты: методы `Collection` (`add(e)`, `iterator()` по элементам) оперируют одиночными элементами, а `Map` — парами. Связь с миром коллекций даётся через **представления** (views): `keySet()`, `values()`, `entrySet()` возвращают `Set`/`Collection`, через которые Map можно итерировать.

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.putIfAbsent("Alice", 100); // Не перезапишет, ключ уже есть

// Итерация по Map
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}

// Получение представлений (views) — связь с Collection
Set<String> keys = scores.keySet();           // Set ключей
Collection<Integer> values = scores.values(); // Collection значений
Set<Map.Entry<String, Integer>> entries = scores.entrySet(); // Set пар
```

| Реализация | Порядок | Производительность | `null` ключи | Потокобезопасность |
|------------|---------|-------------------|-------------|-------------------|
| `HashMap` | Не гарантирован | `O(1)` | Да (один) | Нет |
| `TreeMap` | Отсортированный | `O(log n)` | Нет | Нет |
| `LinkedHashMap` | Порядок вставки | `O(1)` | Да | Нет |
| `ConcurrentHashMap` | Не гарантирован | `O(1)` | **Нет** | Да |

## Q4. Как выбрать между `List`, `Set` и `Map`?

Выбор коллекции определяется семантикой данных:

- **`List`** — важен порядок, допускаются дубликаты, нужен индексированный доступ: история событий, очередь сообщений, результаты запроса
- **`Set`** — нужна уникальность и быстрая проверка наличия: уникальные ID, фильтрация дубликатов, теги
- **`Map`** — пары ключ-значение с быстрым поиском по ключу: кэши, словари, индексы

```java
// List — последовательность с возможными дубликатами
List<String> history = new ArrayList<>();

// Set — уникальные элементы
Set<String> uniqueIds = new HashSet<>();

// Map — поиск по ключу
Map<String, User> userCache = new HashMap<>();
```

Дополнительно учитывайте: нужен ли порядок (`LinkedHashSet` vs `HashSet`), нужна ли сортировка (`TreeSet`/`TreeMap`), нужна ли потокобезопасность (`ConcurrentHashMap`, `CopyOnWriteArrayList`).

## Q5. Что такое интерфейс `Iterable` и как он связан с `Collection`?

`Iterable<T>` — корневой интерфейс иерархии коллекций и контракт «по мне можно пройти один раз». Его единственный абстрактный метод `iterator()` возвращает `Iterator<T>`, и именно на него опирается enhanced `for-loop`: цикл `for (T x : obj)` компилируется в вызов `obj.iterator()` с последующими `hasNext()`/`next()`. Поэтому в `for-each` можно подставить любой свой класс, реализовав `Iterable`:

```java
public interface Iterable<T> {
    Iterator<T> iterator();

    // default-методы (Java 8+)
    default void forEach(Consumer<? super T> action) { ... }
    default Spliterator<T> spliterator() { ... }
}
```

`Collection` расширяет `Iterable` и добавляет методы работы с группами элементов: `size()`, `add()`, `remove()`, `contains()`, `stream()` и др. Подробнее о стримах — в [вопросах по Java Stream API](java-stream-interview.md).

## Q6. (!) В чем разница между `LinkedList` и `ArrayList`?

Главное различие — структура данных под капотом, и из неё следует всё остальное. `ArrayList` — это динамический массив (быстрый доступ по индексу, дорогая вставка в середину), `LinkedList` — двусвязный список (дешёвые вставки/удаления по концам, но `O(n)` доступ по индексу и большой overhead на каждый элемент).

| Операция | `ArrayList` | `LinkedList` |
|----------|-----------|------------|
| Доступ по индексу (`get`) | `O(1)` | `O(n)` |
| Вставка в конец (`add`) | `O(1)` амортизированно | `O(1)` |
| Вставка в начало (`addFirst`) | `O(n)` | `O(1)` |
| Вставка в середину | `O(n)` | `O(n)` |
| Удаление из начала | `O(n)` | `O(1)` |
| Удаление из середины | `O(n)` | `O(n)` |
| Память на элемент | ~4 байта (ссылка) | ~24 байта (узел + 2 ссылки) |

`ArrayList` основан на **динамическом массиве** — элементы лежат последовательно в памяти, что обеспечивает отличную локальность данных и эффективное использование CPU-кэша. `LinkedList` — **двусвязный список**, узлы разбросаны по памяти, и каждый переход по ссылке — потенциальный cache-miss.

Важный нюанс: формально `O(1)`-вставка в середину `LinkedList` на практике почти всегда медленнее, чем `O(n)`-вставка в `ArrayList`. Чтобы вставить элемент в середину списка, нужно сначала **дойти** до нужного узла — а это `O(n)` обход по ссылкам с промахами кэша. У `ArrayList` сдвиг элементов — это `System.arraycopy` по непрерывной памяти, который процессор выполняет очень быстро.

```java
// ArrayList — быстрый произвольный доступ
List<String> arrayList = new ArrayList<>();
arrayList.add("First");
arrayList.add("Second");
String first = arrayList.get(0); // O(1)

// LinkedList — быстрые операции в начале/конце
LinkedList<String> linkedList = new LinkedList<>();
linkedList.addFirst("Zero"); // O(1)
linkedList.removeLast();     // O(1)
```

> **Практический совет:** `ArrayList` предпочтительнее в 95% случаев. `LinkedList` оправдан только при интенсивных вставках/удалениях в начале списка и использовании как `Deque`.

## Q7. Как работает автоматическое расширение `ArrayList`?

`ArrayList` хранит элементы в массиве `Object[] elementData`. Начальная ёмкость по умолчанию — **10** (при первом `add`). Когда массив заполнен, происходит расширение:

```java
// Упрощённый алгоритм расширения (из исходников OpenJDK)
int oldCapacity = elementData.length;
int newCapacity = oldCapacity + (oldCapacity >> 1); // Рост на 50%
elementData = Arrays.copyOf(elementData, newCapacity);
```

Ёмкость растёт в **1.5 раза** (не в 2, как часто говорят на собеседовании!). `Arrays.copyOf` создаёт новый массив и копирует элементы — это `O(n)` операция. Поэтому `add()` — `O(1)` **амортизированно**.

Если количество элементов известно заранее, используйте конструктор с начальной ёмкостью:

```java
List<User> users = new ArrayList<>(1000); // Избегаем лишних расширений
```

Метод `trimToSize()` уменьшает массив до фактического размера коллекции, освобождая лишнюю память.

## Q8. Что такое `CopyOnWriteArrayList`?

`CopyOnWriteArrayList` — потокобезопасная реализация `List` из пакета `java.util.concurrent`. При любой модификации (`add`, `set`, `remove`) создаётся **полная копия** внутреннего массива. Итератор работает со снимком на момент создания и **не выбрасывает** `ConcurrentModificationException`.

```java
List<EventListener> listeners = new CopyOnWriteArrayList<>();
listeners.add(new EventListener());

// Итерация безопасна даже при модификации из другого потока
for (EventListener listener : listeners) {
    listener.onEvent(event); // Другой поток может вызвать listeners.add(...)
}
```

**Когда использовать:** редкие записи и частое чтение — списки слушателей, конфигурации, белые списки. **Не использовать:** при частых модификациях или больших коллекциях (каждая запись — `O(n)` копирование). Подробнее о потокобезопасных коллекциях — в [вопросах по Java Concurrency](java-concurrency-interview.md).

## Q9. В чём разница между `List.of()` и `Arrays.asList()`?

`List.of()` (Java 9) возвращает **полностью неизменяемый** список: любая попытка модификации — `UnsupportedOperationException`, `null` запрещены. `Arrays.asList()` создаёт список **фиксированного размера, связанный с исходным массивом**: `set()` работает (и меняет массив), но `add()`/`remove()` запрещены. Это частая ловушка на собеседовании — `asList` лишь выглядит как обычный изменяемый список.

| Характеристика | `List.of()` (Java 9+) | `Arrays.asList()` |
|---------------|----------------------|-------------------|
| Изменяемость | Полностью immutable | `set()` работает, `add()`/`remove()` — нет |
| `null` элементы | Запрещены (`NPE`) | Разрешены |
| Связь с массивом | Нет (независимая копия) | Да (`backed` массивом) |
| Память | Оптимизирован (0–2 элемента — спец. классы) | Обёртка над массивом |

```java
// List.of — полностью неизменяемый
List<String> immutable = List.of("a", "b", "c");
immutable.set(0, "x");  // UnsupportedOperationException
immutable.add("d");     // UnsupportedOperationException

// Arrays.asList — частично изменяемый, backed массивом
String[] arr = {"a", "b", "c"};
List<String> backed = Arrays.asList(arr);
backed.set(0, "x");  // OK, arr[0] тоже станет "x"
backed.add("d");     // UnsupportedOperationException
```

## Q10. (!) В чем разница между `HashSet` и `TreeSet`?

Это классический компромисс «скорость против порядка». `HashSet` построен на хэш-таблице — операции `O(1)` в среднем, но порядок итерации непредсказуем. `TreeSet` построен на красно-чёрном дереве — операции `O(log n)`, зато элементы всегда отсортированы и доступна навигация (ближайший больший/меньший, диапазоны). Берите `HashSet` по умолчанию; `TreeSet` — когда нужен порядок или диапазонные запросы.

| Операция | `HashSet` | `TreeSet` |
|----------|---------|---------|
| `add` / `remove` / `contains` | `O(1)` в среднем | `O(log n)` |
| Порядок итерации | Не гарантирован | Отсортированный |
| `null` элементы | Один `null` допустим | Нет (`NPE` при `Comparable`) |
| Навигация | Нет | `first()`, `last()`, `lower()`, `higher()`, `subSet()` |
| Внутренняя реализация | `HashMap` | `TreeMap` (красно-чёрное дерево) |

```java
// HashSet — быстрые операции, нет порядка
Set<String> hashSet = new HashSet<>();
hashSet.add("Charlie");
hashSet.add("Alice");
// Порядок итерации непредсказуем

// TreeSet — всегда отсортированный
TreeSet<String> treeSet = new TreeSet<>();
treeSet.add("Charlie");
treeSet.add("Alice");
treeSet.add("Bob");
treeSet.first();              // "Alice"
treeSet.last();               // "Charlie"
treeSet.subSet("Alice", "C"); // ["Alice", "Bob"]
```

`HashSet` внутри — это `HashMap`, где значение — фиктивный объект `PRESENT`. Поэтому требования к `hashCode()`/`equals()` ключей применимы и к элементам `HashSet` — подробнее в [вопросах по Java Core](java-core-interview.md).

## Q11. Что такое `LinkedHashSet` и когда его использовать?

`LinkedHashSet` — гибрид `HashSet` с сохранением порядка вставки. Внутри — `LinkedHashMap`, где каждая запись содержит ссылки на предыдущий и следующий элемент.

```java
Set<String> linked = new LinkedHashSet<>();
linked.add("Charlie");
linked.add("Alice");
linked.add("Bob");
// Итерация в порядке вставки: Charlie, Alice, Bob

Set<String> hash = new HashSet<>(linked);
// Порядок итерации: непредсказуемый
```

**Когда использовать:** когда нужна уникальность **и** сохранение порядка вставки — например, результат обработки с удалением дубликатов, но сохранением исходного порядка.

## Q12. (!) Что такое `HashMap` и как он устроен внутри?

`HashMap` — основная реализация `Map`, хранящая пары через **хэширование**: позиция элемента вычисляется из `hashCode()` ключа, поэтому средняя сложность `get`/`put` — `O(1)`. Внутри это массив bucket'ов (ячеек); каждый bucket может содержать связанный список или красно-чёрное дерево узлов, попавших в одну ячейку из-за коллизий.

Пример раскладки по bucket'ам (`HashMap` с `capacity=8`, `size=5`):

- `Bucket 0`: `null` (пусто)
- `Bucket 1`: `Entry(K1,V1)`
- `Bucket 2`: `null` (пусто)
- `Bucket 3`: `Entry(K2,V2)` → `Entry(K5,V5)` — здесь коллизия: два ключа попали в одну ячейку и связаны в цепочку
- `Bucket 4`: `null` (пусто)
- `Bucket 5`: `Entry(K3,V3)`
- `Bucket 6`: `null` (пусто)
- `Bucket 7`: `Entry(K4,V4)`

**Алгоритм `put(key, value)`:**

1. Вычислить `hash = key.hashCode()` и применить дополнительное перемешивание: `hash ^ (hash >>> 16)`
2. Определить индекс bucket'а: `index = hash & (capacity - 1)` (побитовое `AND` вместо деления по модулю)
3. Если bucket пуст — создать `Node` и поместить
4. Если не пуст — пройти по цепочке, сравнивая ключи через `equals()`
5. Если ключ найден — заменить значение; если нет — добавить в конец цепочки

```java
Map<String, Integer> map = new HashMap<>(16, 0.75f);
map.put("Alice", 25);   // hash("Alice") → bucket index → Node
map.put("Bob", 30);
Integer age = map.get("Alice"); // hash → bucket → equals → value
```

> Ёмкость `HashMap` **всегда** степень двойки (16, 32, 64...), что позволяет использовать быструю побитовую операцию `& (capacity - 1)` вместо `% capacity`.

## Q13. (!) Что происходит при коллизиях в `HashMap` и как работает treeification?

При коллизии (два ключа попали в один bucket) элементы хранятся в **цепочке** (`linked list`). В `Java 8+` введён механизм **treeification** — преобразование цепочки в красно-чёрное дерево: пока в bucket'е меньше 8 элементов, они лежат связным списком; при 8 и более — список перестраивается в красно-чёрное дерево.

**Ключевые константы:**

| Константа | Значение | Описание |
|-----------|----------|----------|
| `TREEIFY_THRESHOLD` | **8** | Длина цепочки, при которой список → дерево |
| `UNTREEIFY_THRESHOLD` | **6** | Длина, при которой дерево → список |
| `MIN_TREEIFY_CAPACITY` | **64** | Минимальная ёмкость таблицы для treeification |

Если цепочка достигла 8 элементов, но ёмкость таблицы < 64, вместо treeification произойдёт **resize** (удвоение таблицы). Это важный нюанс, который часто спрашивают на собеседованиях.

```java
// Пример плохого hashCode — все ключи в одном bucket
class BadKey {
    private final int id;
    BadKey(int id) { this.id = id; }

    @Override
    public int hashCode() { return 42; } // Все в один bucket!

    @Override
    public boolean equals(Object o) {
        return o instanceof BadKey bk && bk.id == this.id;
    }
}

// Производительность деградирует: O(1) → O(n), а с Java 8 → O(log n)
```

> **Важно для собеседования:** до `Java 8` коллизии всегда обрабатывались через `linked list` (`O(n)` в худшем случае). С `Java 8` — через `red-black tree` (`O(log n)` в худшем случае), что было сделано для защиты от HashDoS-атак.

## Q14. (!) Какова цель параметров `initialCapacity` и `loadFactor`?

Эти два параметра управляют главным компромиссом `HashMap` — между скоростью и памятью. Чем больше места под bucket'ы и чем раньше карта расширяется, тем реже коллизии и быстрее поиск, но тем больше тратится памяти. `initialCapacity` и `loadFactor` позволяют настроить этот баланс и, главное, избежать дорогих перестроений таблицы (`rehashing`), если размер данных известен заранее.

**`initialCapacity`** — начальный размер массива bucket'ов (по умолчанию **16**). Всегда округляется вверх до степени двойки.

**`loadFactor`** — порог заполнения для автоматического расширения (по умолчанию **0.75**). Формула: `threshold = capacity × loadFactor`.

**Процесс `rehashing`:**
1. Когда `size > threshold`, ёмкость удваивается
2. Все элементы пересчитываются и размещаются в новых bucket'ах — `O(n)`
3. Новый `threshold = newCapacity × loadFactor`

```java
// Пример: capacity=16, loadFactor=0.75 → threshold=12
// При добавлении 13-го элемента → resize до 32, threshold=24

// Оптимизация: если знаем количество элементов заранее
int expectedSize = 1000;
// capacity = expectedSize / loadFactor + 1, округлённый до степени двойки
Map<String, User> map = new HashMap<>(expectedSize * 4 / 3 + 1);

// Или с Java 19+:
Map<String, User> map2 = HashMap.newHashMap(expectedSize);
```

| `loadFactor` | Компромисс |
|-------------|-----------|
| **0.5** | Меньше коллизий, больше памяти, реже `rehashing` |
| **0.75** (default) | Баланс производительности и памяти |
| **1.0** | Больше коллизий, меньше памяти, чаще `rehashing` |

## Q15. (!) Что такое `ConcurrentHashMap` и чем отличается от `HashMap`?

`ConcurrentHashMap` — потокобезопасная реализация `Map` из `java.util.concurrent`, оптимизированная для многопоточного доступа. В отличие от `Collections.synchronizedMap`, не блокирует всю таблицу целиком.

**Механизм в Java 8+:**
- Операции чтения (`get`) — **без блокировок** (через `volatile` переменные)
- Операции записи — **блокировка на уровне отдельного bucket'а** (через `synchronized` на первый узел bucket'а) или `CAS` (`Compare-And-Swap`) для пустых bucket'ов
- Множество потоков могут одновременно читать и писать в **разные** bucket'ы

```java
ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

// Атомарные операции — основное преимущество
counters.computeIfAbsent("hits", k -> new AtomicInteger(0)).incrementAndGet();
counters.merge("errors", new AtomicInteger(1), (old, v) -> {
    old.addAndGet(v.get());
    return old;
});

// Агрегирующие операции (parallelismThreshold)
long sum = counters.reduceValuesToLong(1, AtomicInteger::get, 0, Long::sum);
```

**Ключевые отличия от `HashMap`:**

| Характеристика | `HashMap` | `ConcurrentHashMap` |
|---------------|----------|-------------------|
| `null` ключи/значения | Допускает | **Запрещает** (`NPE`) |
| Потокобезопасность | Нет | Да |
| Итератор | `fail-fast` | `weakly consistent` |
| Атомарные операции | Нет | `compute`, `merge`, `putIfAbsent` |
| Блокировка | — | На уровне bucket'а |

Подробнее о многопоточности — в [вопросах по Java Concurrency](java-concurrency-interview.md).

## Q16. (!) Контракт `equals` и `hashCode` для ключей `Map`

Правильная реализация `equals()` и `hashCode()` критически важна для корректной работы `HashMap`, `HashSet` и других hash-based коллекций.

**Контракт:**
1. Если `a.equals(b) == true`, то `a.hashCode() == b.hashCode()` — **обязательно**
2. Если `a.hashCode() == b.hashCode()`, это **не означает** `a.equals(b)` (коллизия допустима)
3. Если `a.hashCode() != b.hashCode()`, то `a.equals(b) == false` — **гарантировано**

```java
public class Employee {
    private final Long id;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee that = (Employee) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name); // Те же поля, что в equals!
    }
}
```

**Что произойдёт при нарушении контракта:**
- Если `equals` совпадает, но `hashCode` разный — один и тот же логический ключ попадёт в разные bucket'ы, `get()` не найдёт значение
- Если `hashCode` совпадает, но `equals` не реализован — в одном bucket'е будут дубликаты логически одинаковых ключей

## Q17. Что такое `TreeMap` и какая сложность операций?

`TreeMap` — реализация `NavigableMap` на основе красно-чёрного дерева. Ключи всегда отсортированы — по `Comparable` или заданному `Comparator`.

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(3, "Three");
map.put(1, "One");
map.put(5, "Five");
map.put(2, "Two");

// Навигационные методы
map.firstKey();              // 1
map.lastKey();               // 5
map.lowerKey(3);             // 2 (строго меньше)
map.floorKey(3);             // 3 (меньше или равно)
map.subMap(2, 5);            // {2=Two, 3=Three} — [2, 5)
map.headMap(3);              // {1=One, 2=Two}
map.tailMap(3);              // {3=Three, 5=Five}
map.descendingMap();         // {5=Five, 3=Three, 2=Two, 1=One}
```

Все основные операции — `O(log n)`. Используйте `TreeMap`, когда нужна сортировка ключей, диапазонные запросы или навигация. Для максимальной производительности без требований к порядку — `HashMap`.

## Q18. В чём разница между `HashMap` и `LinkedHashMap`?

`LinkedHashMap` расширяет `HashMap` и добавляет **двусвязный список** записей поверх хэш-таблицы, сохраняя порядок вставки (или порядок доступа при `accessOrder = true`).

```java
// HashMap — порядок итерации не определён
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("C", 3);
hashMap.put("A", 1);
hashMap.put("B", 2);
// Итерация: порядок непредсказуем

// LinkedHashMap — порядок вставки сохраняется
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("C", 3);
linkedMap.put("A", 1);
linkedMap.put("B", 2);
// Итерация: C=3, A=1, B=2 — всегда в порядке вставки
```

Дополнительная память: **две ссылки на запись** (before/after) для поддержания двусвязного списка. Производительность операций такая же — `O(1)` в среднем.

## Q19. Как реализовать `LRU`-кэш на `LinkedHashMap`?

`LinkedHashMap` с `accessOrder = true` переупорядочивает элементы при каждом доступе (`get`, `put`). В сочетании с переопределением `removeEldestEntry()` получается готовый `LRU`-кэш:

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize) {
        super(maxSize * 4 / 3 + 1, 0.75f, true); // accessOrder = true
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize; // Автоматическое вытеснение старых
    }
}

// Использование
LRUCache<String, User> cache = new LRUCache<>(100);
cache.put("user:1", user1);
cache.get("user:1"); // Перемещает в конец (самый свежий)
// При добавлении 101-го элемента самый давно использованный удалится
```

> Это классический вопрос на собеседовании уровня Senior. Альтернатива — `Caffeine` или `Guava Cache` для production-кода с TTL, статистикой и weak-ссылками.

## Q20. Что такое `WeakHashMap`?

`WeakHashMap` использует **weak-ссылки** для ключей. Когда на ключ больше нет сильных ссылок, запись автоматически удаляется сборщиком мусора.

```java
WeakHashMap<Object, String> metadata = new WeakHashMap<>();
Object key = new Object();
metadata.put(key, "some metadata");
System.out.println(metadata.size()); // 1

key = null; // Убираем единственную сильную ссылку
System.gc(); // Подсказка GC (не гарантирует сборку)
// После GC: metadata может стать пустой
```

**Применение:** кэширование метаданных, привязанных к жизненному циклу объектов-ключей. Не потокобезопасна. Подробнее о типах ссылок — в [вопросах по управлению памятью](../../performance/memory-management-interview.md).

## Q21. Что такое `IdentityHashMap`?

`IdentityHashMap` сравнивает ключи по **ссылочному равенству** (`==`) вместо `equals()`, и использует `System.identityHashCode()` вместо `hashCode()`.

```java
IdentityHashMap<String, Integer> map = new IdentityHashMap<>();
String key1 = new String("key");
String key2 = new String("key");

map.put(key1, 1);
map.put(key2, 2);
map.size(); // 2! В HashMap было бы 1

map.put(key1, 3);
map.size(); // 2 — key1 найден по ==
```

Внутри использует **линейное пробирование** (`linear probing`) вместо цепочек. Используется в сериализации, глубоком копировании, отслеживании топологии объектов — когда важна идентичность экземпляра, а не логическое равенство.

## Q22. В чём разница между `Collections.synchronizedMap` и `ConcurrentHashMap`?

Обе обеспечивают потокобезопасность, но разной ценой. `synchronizedMap` — это простая обёртка, которая оборачивает **каждый** метод в `synchronized` на одном общем мониторе: корректно, но любые два потока сериализуются даже на чтении, поэтому под нагрузкой она становится бутылочным горлышком. `ConcurrentHashMap` спроектирована для конкуренции с нуля: чтение идёт без блокировок, запись блокирует только один bucket — поэтому масштабируется на много потоков.

| Характеристика | `synchronizedMap` | `ConcurrentHashMap` |
|---------------|-------------------|---------------------|
| Механизм | Единый `mutex` на всю Map | Блокировка на уровне bucket'а / `CAS` |
| Чтение | Блокирует | Без блокировок |
| Запись | Блокирует всю Map | Блокирует только один bucket |
| Производительность | Низкая при конкуренции | Высокая |
| Итерация | `fail-fast`, нужна внешняя синхронизация | `weakly consistent`, безопасна |
| `null` | Допускает | Запрещает |
| Атомарные операции | Нет (только через внешний `synchronized`) | `compute`, `merge`, `putIfAbsent` |

```java
// synchronizedMap — обёртка, все методы синхронизированы
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
synchronized (syncMap) {  // Обязательно для итерации!
    for (Map.Entry<String, Integer> e : syncMap.entrySet()) { ... }
}

// ConcurrentHashMap — итерация безопасна без внешней синхронизации
ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();
for (Map.Entry<String, Integer> e : concMap.entrySet()) { ... } // Безопасно
```

> **Рекомендация:** всегда используйте `ConcurrentHashMap` вместо `synchronizedMap` в многопоточных приложениях.

## Q23. Какие методы `Java 8+` добавлены в `Map`?

`Java 8` добавила в `Map` набор default-методов, которые заменяют громоздкий шаблон «проверь наличие ключа → достань → измени → положи обратно» одним атомарным вызовом. Особенно ценны они в `ConcurrentHashMap`, где такой составной паттерн без атомарного метода был бы небезопасен в многопоточной среде.

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);

// getOrDefault — значение по умолчанию
int val = map.getOrDefault("b", 0); // 0

// putIfAbsent — вставить, только если ключа нет
map.putIfAbsent("a", 99); // Не изменит, "a" уже есть

// computeIfAbsent — ленивое вычисление значения
map.computeIfAbsent("b", k -> k.length()); // "b" → 1

// computeIfPresent — обновить, только если ключ есть
map.computeIfPresent("a", (k, v) -> v + 10); // "a" → 11

// compute — вычислить значение (для любого ключа)
map.compute("a", (k, v) -> v == null ? 1 : v + 1);

// merge — объединить значения
map.merge("a", 5, Integer::sum); // "a" → 16

// replaceAll — изменить все значения
map.replaceAll((k, v) -> v * 2);

// forEach
map.forEach((k, v) -> System.out.println(k + "=" + v));
```

Эти методы особенно полезны для подсчёта частот, группировок и атомарных обновлений. Подробнее о `Stream API` и `Collectors` — в [вопросах по Stream API](java-stream-interview.md).

## Q24. Что такое `Queue` и какие реализации?

`Queue` — интерфейс очереди, по умолчанию `FIFO` (первым пришёл — первым ушёл). Ключевая особенность API — для каждой операции есть **две версии**: одна бросает исключение при неудаче, другая сигнализирует через возвращаемое значение (`null`/`false`). Выбор зависит от того, считаете ли вы пустую/полную очередь нормальной ситуацией (тогда `offer`/`poll`) или ошибкой (тогда `add`/`remove`).

| Операция | Бросает исключение | Возвращает `null`/`false` |
|----------|--------------------|--------------------------|
| Вставка | `add(e)` | `offer(e)` |
| Извлечение | `remove()` | `poll()` |
| Просмотр | `element()` | `peek()` |

```java
// PriorityQueue — приоритетная очередь (min-heap по умолчанию)
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(30);
pq.offer(10);
pq.offer(20);
pq.poll(); // 10 — минимальный элемент

// С Comparator — max-heap
PriorityQueue<Integer> maxPq = new PriorityQueue<>(Comparator.reverseOrder());
maxPq.offer(30);
maxPq.offer(10);
maxPq.poll(); // 30 — максимальный элемент
```

Основные реализации: `ArrayDeque` (двусторонняя очередь), `PriorityQueue` (приоритетная очередь на двоичной куче, `O(log n)` для `offer`/`poll`), `LinkedList` (также реализует `Queue` и `Deque`).

## Q25. Что такое `Deque` и когда использовать?

`Deque` (`double-ended queue`) позволяет добавлять и удалять элементы с обоих концов. `ArrayDeque` — рекомендуемая реализация для стека и очереди (быстрее устаревшего `Stack` и `LinkedList`):

```java
// Как стек (LIFO)
Deque<String> stack = new ArrayDeque<>();
stack.push("First");
stack.push("Second");
stack.pop(); // "Second"

// Как очередь (FIFO)
Deque<String> queue = new ArrayDeque<>();
queue.offerLast("First");
queue.offerLast("Second");
queue.pollFirst(); // "First"

// Двусторонний доступ
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("A");
deque.addLast("Z");
deque.peekFirst(); // "A"
deque.peekLast();  // "Z"
```

`ArrayDeque` основан на **циклическом массиве** — все операции `O(1)`, потребляет меньше памяти, чем `LinkedList`. Не потокобезопасна.

## Q26. (!) Что такое `BlockingQueue` и паттерн `producer-consumer`?

`BlockingQueue` — интерфейс очереди с **блокирующими** операциями для координации потоков. `put()` блокирует при полной очереди, `take()` — при пустой.

```java
// Producer-Consumer паттерн
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

// Producer поток
Runnable producer = () -> {
    while (true) {
        Task task = generateTask();
        queue.put(task); // Блокируется, если очередь полна
    }
};

// Consumer поток
Runnable consumer = () -> {
    while (true) {
        Task task = queue.take(); // Блокируется, если очередь пуста
        process(task);
    }
};
```

| Реализация | Ёмкость | Особенности |
|-----------|---------|-------------|
| `ArrayBlockingQueue` | Фиксированная | Циклический массив, `fair`/`unfair` блокировка |
| `LinkedBlockingQueue` | Настраиваемая (по умолчанию `Integer.MAX_VALUE`) | Два отдельных lock'а для `put`/`take` |
| `SynchronousQueue` | **0** | Каждый `put` ждёт `take` |
| `PriorityBlockingQueue` | Безграничная | Приоритетная очередь |

`ThreadPoolExecutor` использует `BlockingQueue` для очереди задач — подробнее в [вопросах по Java Concurrency](java-concurrency-interview.md).

## Q27. В чём разница между `ArrayDeque` и `LinkedList`?

Оба реализуют `Deque` и дают `O(1)` на операциях по концам, но `ArrayDeque` предпочтительнее почти всегда. Причина — память и кэш: `ArrayDeque` хранит элементы в непрерывном циклическом массиве (отличная локальность, нет аллокации на каждый элемент), тогда как `LinkedList` создаёт отдельный узел с тремя ссылками на каждый элемент — это и больше памяти, и постоянные cache-miss при обходе. `LinkedList` оправдан, только если нужны `null`-элементы или интерфейс `List`.

| Характеристика | `ArrayDeque` | `LinkedList` |
|---------------|-------------|-------------|
| Внутренняя структура | Циклический массив | Двусвязный список |
| Память на элемент | ~8 байт (ссылка + амортизация) | ~40 байт (узел + 3 ссылки) |
| CPU-кэш | Отличная локальность | Плохая (узлы разбросаны) |
| `null` элементы | Запрещены | Допускаются |
| Реализует `List` | Нет | Да |
| Производительность | Быстрее (массив) | Медленнее (аллокации узлов) |

```java
// Предпочтительно — ArrayDeque
Deque<String> stack = new ArrayDeque<>();

// Только если нужен null или List-интерфейс
Deque<String> deque = new LinkedList<>();
```

> Документация OpenJDK прямо рекомендует `ArrayDeque` как более эффективную альтернативу `LinkedList` для стеков и очередей.

## Q28. Что такое `EnumSet` и `EnumMap`?

Специализированные коллекции для `enum`, оптимизированные по памяти и производительности:

- **`EnumSet`** — множество enum-констант на основе **битовой маски**. Для enum с ≤64 константами используется один `long` (`RegularEnumSet`), для больших — массив `long[]` (`JumboEnumSet`). Все операции — `O(1)`.

- **`EnumMap`** — Map с enum-ключами на основе **массива**. Индекс = `ordinal()` значения enum. Все операции — `O(1)`, итерация в порядке объявления констант.

```java
enum Permission { READ, WRITE, EXECUTE, DELETE }

// EnumSet — компактнее и быстрее HashSet
EnumSet<Permission> readOnly = EnumSet.of(Permission.READ);
EnumSet<Permission> all = EnumSet.allOf(Permission.class);
EnumSet<Permission> rwx = EnumSet.range(Permission.READ, Permission.EXECUTE);

// EnumMap — компактнее и быстрее HashMap
EnumMap<Permission, String> descriptions = new EnumMap<>(Permission.class);
descriptions.put(Permission.READ, "Чтение файлов");
descriptions.put(Permission.WRITE, "Запись файлов");

// Сравнение по памяти (для 4-х констант):
// EnumSet: 1 long = 8 байт
// HashSet: ~128 байт (HashMap + Node + Entry)
```

## Q29. (!) В чем разница между `fail-fast` и `fail-safe` итераторами?

Разница в реакции на изменение коллекции во время итерации. **`fail-fast`** немедленно бросает `ConcurrentModificationException`, как только замечает структурную модификацию — это страховка от трудноуловимых багов, а не средство потокобезопасности. **`fail-safe`** (точнее — `weakly consistent`) работает со снимком или сегментами данных и не падает, но может не увидеть самых свежих изменений.

| Характеристика | `fail-fast` | `fail-safe` (weakly consistent) |
|---------------|-----------|-------------------------------|
| Исключение | `ConcurrentModificationException` | Нет |
| Механизм | Проверяет `modCount` | Работает со снимком / сегментами |
| Многопоточность | Небезопасно | Безопасно |
| Актуальность данных | Свежие | Может видеть устаревшие |
| Примеры | `ArrayList`, `HashMap`, `TreeMap` | `CopyOnWriteArrayList`, `ConcurrentHashMap` |

```java
// fail-fast — бросит ConcurrentModificationException
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    list.remove(s); // CME!
}

// fail-safe — не бросит исключение
List<String> cowList = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));
for (String s : cowList) {
    cowList.remove(s); // OK, итератор на снимке
}

// Правильное удаление в fail-fast коллекции
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("b")) {
        it.remove(); // Безопасно — через Iterator.remove()
    }
}
```

> **Важно:** термин `fail-safe` не является официальным в `Java` — в документации `ConcurrentHashMap` используется `weakly consistent`. Итератор может видеть часть изменений, произведённых после его создания.

## Q30. Как итерировать и удалять элементы?

Три безопасных способа удаления элементов при итерации:

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));

// 1. Iterator.remove() — классический способ
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("b")) {
        it.remove();
    }
}

// 2. removeIf (Java 8+) — лаконичный и эффективный
list.removeIf(s -> s.startsWith("c"));

// 3. Stream + filter + collect — создаёт новую коллекцию
List<String> filtered = list.stream()
    .filter(s -> !s.startsWith("a"))
    .collect(Collectors.toList());
```

Для `Map` — аналогично через `entrySet().iterator()` или `Map.entrySet().removeIf()`.

## Q31. Что такое `Spliterator`?

`Spliterator` (`splittable iterator`) — итератор для разбиения данных на части, используется внутри `Stream API` для параллельной обработки. Ключевой метод `trySplit()` делит данные на две части — одну для текущего потока, другую — для нового.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);
Spliterator<Integer> full = numbers.spliterator();
Spliterator<Integer> half = full.trySplit(); // Разделение

// Характеристики сплитератора
full.characteristics(); // SIZED | ORDERED | SUBSIZED | IMMUTABLE
full.estimateSize();    // Оценка количества оставшихся элементов
```

**Характеристики** (`characteristics`): `SIZED`, `ORDERED`, `SORTED`, `DISTINCT`, `NONNULL`, `IMMUTABLE`, `CONCURRENT`, `SUBSIZED`. Они влияют на оптимизации в `Stream API` — подробнее в [вопросах по Stream API](java-stream-interview.md).

## Q32. Что такое `ConcurrentModificationException`?

`ConcurrentModificationException` возникает при структурной модификации коллекции во время итерации (кроме через `Iterator.remove()`). Причина: итератор сохраняет `expectedModCount` при создании, а коллекция увеличивает `modCount` при каждом изменении. При расхождении — исключение.

```java
// Типичная ошибка — модификация в enhanced for-loop
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    if (s.equals("b")) {
        list.remove(s); // ConcurrentModificationException!
    }
}

// Решения:
// 1. Iterator.remove()
// 2. list.removeIf(s -> s.equals("b"))
// 3. Использовать CopyOnWriteArrayList / ConcurrentHashMap
```

> Несмотря на название, `ConcurrentModificationException` чаще возникает в **однопоточном** коде — при изменении коллекции внутри `for-each` цикла.

## Q33. (!) Как использовать `Comparable` и `Comparator`?

Оба задают порядок сортировки, но с разных позиций. `Comparable` определяет **естественный порядок** объекта изнутри самого класса (один на класс) — например, числа по возрастанию. `Comparator` — это **внешняя** стратегия сравнения: их может быть сколько угодно, они не требуют менять сам класс и легко комбинируются цепочками. Правило простое: один очевидный порядок «по умолчанию» — `Comparable`; всё остальное (по другому полю, в обратную сторону, по нескольким критериям) — `Comparator`.

| Характеристика | `Comparable<T>` | `Comparator<T>` |
|---------------|----------------|-----------------|
| Метод | `compareTo(T o)` | `compare(T o1, T o2)` |
| Реализация | В самом классе | Отдельный объект / лямбда |
| Количество критериев | Один (естественный порядок) | Множество |
| Модификация класса | Нужна | Не нужна |

```java
// Comparable — естественный порядок
public class Employee implements Comparable<Employee> {
    private String name;
    private int salary;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.salary, other.salary);
    }
}

List<Employee> employees = new ArrayList<>();
Collections.sort(employees); // По salary (естественный порядок)

// Comparator — гибкая сортировка (Java 8+)
employees.sort(Comparator.comparing(Employee::getName));

// Цепочка критериев
employees.sort(Comparator
    .comparing(Employee::getSalary).reversed()
    .thenComparing(Employee::getName));

// Обработка null
employees.sort(Comparator.nullsLast(
    Comparator.comparing(Employee::getName)));
```

> **Совет для собеседования:** контракт `Comparable.compareTo()` должен быть согласован с `equals()` — иначе `TreeSet`/`TreeMap` будут работать некорректно (считают элементы с `compareTo() == 0` равными).

## Q34. Какие алгоритмы сортировки используются в `Java`?

Java использует **разные** алгоритмы для объектов и примитивов — и это осознанный выбор. Для объектов важна стабильность (равные элементы сохраняют порядок) — берётся TimSort. Для примитивов стабильность не имеет смысла (две одинаковые `int` неразличимы), поэтому используется более быстрый по памяти Dual-Pivot Quicksort.

- **`Arrays.sort(Object[])` и `Collections.sort()`** — **TimSort** (адаптивная стабильная сортировка слиянием, `O(n log n)`, `O(n)` для частично отсортированных данных)
- **`Arrays.sort(int[])` и другие примитивы** — **Dual-Pivot Quicksort** (нестабильная, `O(n log n)` в среднем, `O(n²)` в худшем)
- **`Arrays.parallelSort()`** (Java 8+) — параллельный merge sort с `ForkJoinPool`

```java
int[] primitives = {5, 3, 1, 4, 2};
Arrays.sort(primitives); // Dual-Pivot Quicksort

String[] objects = {"c", "a", "b"};
Arrays.sort(objects); // TimSort (стабильная)

int[] large = new int[1_000_000];
Arrays.parallelSort(large); // Параллельная сортировка
```

**Стабильность** означает, что равные элементы сохраняют свой относительный порядок после сортировки. `TimSort` — стабильный, `Dual-Pivot Quicksort` — нет (но для примитивов это не имеет значения).

## Q35. Что такое `Collections.unmodifiableList`?

`Collections.unmodifiableList(list)` создаёт **обёртку** (не копию!) над существующим списком. Методы изменения бросают `UnsupportedOperationException`, но изменения через **исходный** список будут видны:

```java
List<String> original = new ArrayList<>(List.of("a", "b"));
List<String> readOnly = Collections.unmodifiableList(original);

readOnly.add("c");   // UnsupportedOperationException
original.add("c");   // OK! readOnly тоже покажет "c"

System.out.println(readOnly); // [a, b, c]
```

Для настоящей неизменяемой копии используйте `List.copyOf()` (Java 10+) или `Collections.unmodifiableList(new ArrayList<>(original))`.

## Q36. (!) Что такое Immutable коллекции в `Java 9+`?

Это **по-настоящему неизменяемые** коллекции — в отличие от `Collections.unmodifiableList()`, которая лишь оборачивает изменяемый список и не защищает от мутаций через исходную ссылку. Фабричные методы `List.of()`, `Set.of()`, `Map.of()` (Java 9) и `List.copyOf()`, `Map.copyOf()` (Java 10) создают самодостаточные неизменяемые объекты — компактнее по памяти и безопасны для шаринга между потоками без синхронизации:

```java
// Java 9 — фабричные методы
List<String> list = List.of("a", "b", "c");
Set<String> set = Set.of("x", "y", "z");
Map<String, Integer> map = Map.of("a", 1, "b", 2);
Map<String, Integer> bigMap = Map.ofEntries(
    Map.entry("a", 1),
    Map.entry("b", 2),
    Map.entry("c", 3)
);

// Java 10 — копирование в immutable
List<String> copy = List.copyOf(mutableList);
Set<String> setCopy = Set.copyOf(mutableSet);

// Все мутирующие методы → UnsupportedOperationException
list.add("d");      // UnsupportedOperationException
list.set(0, "x");   // UnsupportedOperationException
```

**Ключевые ограничения:**
- `null` элементы **запрещены** — `NullPointerException`
- `Set.of()` и `Map.of()` не допускают дубликатов — `IllegalArgumentException`
- Порядок итерации `Set.of()` **не определён** и может меняться между запусками JVM

Более детально о `Java 8+` возможностях — в [вопросах по Java 8+](java-8-interview.md).

## Q37. Какие утилитные методы предоставляет класс `Collections`?

Класс `Collections` (не путать с интерфейсом `Collection`) — это набор статических утилит, которые делятся на три группы: **алгоритмы** (сортировка, поиск, перемешивание), **обёртки** (синхронизированные, неизменяемые, типобезопасные views поверх существующей коллекции) и **фабрики** (пустые/одноэлементные коллекции).

```java
List<Integer> list = new ArrayList<>(List.of(3, 1, 4, 1, 5));

// Сортировка
Collections.sort(list);
Collections.sort(list, Comparator.reverseOrder());

// Поиск (только в отсортированном списке!)
int index = Collections.binarySearch(list, 4);

// Модификация
Collections.reverse(list);
Collections.shuffle(list);
Collections.swap(list, 0, 1);
Collections.rotate(list, 2); // Циклический сдвиг
Collections.fill(list, 0);

// Агрегация
int min = Collections.min(list);
int max = Collections.max(list);
int freq = Collections.frequency(list, 1); // Количество вхождений

// Обёртки
List<Integer> synced = Collections.synchronizedList(list);
List<Integer> unmod = Collections.unmodifiableList(list);
List<Integer> checked = Collections.checkedList(list, Integer.class);

// Фабричные
List<String> empty = Collections.emptyList();
Set<String> single = Collections.singleton("only");
List<String> nCopies = Collections.nCopies(10, "default");
```

## Q38. Почему нельзя использовать мутабельные объекты как ключи `HashMap`?

Если объект-ключ изменяется после вставки в `HashMap`, его `hashCode()` может измениться. В результате `get()` будет искать в **другом bucket'е** и не найдёт значение:

```java
List<String> key = new ArrayList<>(List.of("a", "b"));
Map<List<String>, String> map = new HashMap<>();
map.put(key, "value");

System.out.println(map.get(key)); // "value"

key.add("c"); // Мутация ключа! hashCode изменился

System.out.println(map.get(key)); // null! Значение "потеряно"
System.out.println(map.size());   // 1 — элемент есть, но недоступен
```

**Правило:** ключи `HashMap`/`HashSet` должны быть **immutable** или, как минимум, поля, участвующие в `hashCode()`/`equals()`, не должны изменяться после вставки. Идеальные ключи: `String`, `Integer`, `enum`, `record`. Подробнее — в [вопросах по Java Core](java-core-interview.md).

## Q39. Как выбрать правильную коллекцию для конкретной задачи?

Выбор коллекции можно свести к дереву решений. Стартовый вопрос — «Нужны ли пары ключ-значение?»:

- **Пары ключ-значение?**
  - **Да** → нужна сортировка ключей?
    - Нужна сортировка → `TreeMap`
    - Сортировка не нужна → многопоточность?
      - Многопоточность → `ConcurrentHashMap`
      - Однопоточный → нужен порядок вставки?
        - Порядок вставки нужен → `LinkedHashMap`
        - Порядок не важен → `HashMap`
  - **Нет** → нужна уникальность элементов?
    - **Уникальные?**
      - **Да** → нужна сортировка?
        - Нужна сортировка → `TreeSet`
        - Сортировка не нужна → нужен порядок вставки?
          - Порядок вставки нужен → `LinkedHashSet`
          - Порядок не важен → `HashSet`
      - **Нет** → нужна семантика FIFO / стека?
        - **FIFO / стек?**
          - **Да** → нужна блокирующая очередь?
            - Блокирующая → `BlockingQueue`
            - Не блокирующая → `ArrayDeque`
          - **Нет** → нужен произвольный доступ по индексу?
            - Произвольный доступ нужен → `ArrayList`
            - Произвольный доступ не нужен → `LinkedList`

## Q40. Какие коллекции из сторонних библиотек стоит знать?

Стандартная библиотека не покрывает все потребности. Популярные расширения:

**Guava (Google):**
- `ImmutableList`, `ImmutableSet`, `ImmutableMap` — настоящие immutable коллекции (до Java 9)
- `Multimap` — Map с несколькими значениями на ключ
- `BiMap` — двунаправленная Map (ключ↔значение)
- `Table<R, C, V>` — двумерная Map (строка, столбец → значение)
- `RangeSet`, `RangeMap` — работа с диапазонами

**Eclipse Collections:**
- Примитивные коллекции (`IntList`, `LongSet`) — без autoboxing
- `Bag` — коллекция с подсчётом элементов
- `MutableList`, `ImmutableList` с богатым fluent API

**Apache Commons Collections:**
- `MultiValuedMap` — аналог `Multimap`
- `BidiMap` — двунаправленная Map

```java
// Guava Multimap — несколько значений на ключ
Multimap<String, String> multimap = ArrayListMultimap.create();
multimap.put("fruits", "apple");
multimap.put("fruits", "banana");
Collection<String> fruits = multimap.get("fruits"); // [apple, banana]

// Eclipse Collections — примитивные коллекции (без boxing)
IntList ints = IntLists.mutable.of(1, 2, 3);
long sum = ints.sum(); // Без autoboxing
```

> На собеседовании достаточно **знать о существовании** этих библиотек и уметь объяснить, когда стандартных коллекций недостаточно.

## Q41. (!) Что такое `SequencedCollection`, `SequencedSet`, `SequencedMap` (Java 21)?

`Java 21` ввёл три новых интерфейса в иерархию `Collections Framework` (см. [Java 17-21](java-17-21-interview.md)):

```
java.util.SequencedCollection  → List, Deque, LinkedHashSet
java.util.SequencedSet         → SortedSet, LinkedHashSet
java.util.SequencedMap         → SortedMap, LinkedHashMap
```

**Проблема до Java 21:** не было единого API для доступа к первому/последнему элементу:
```java
// Разные способы для разных коллекций — до Java 21
list.get(0);                   // ArrayList
list.get(list.size() - 1);     // ArrayList
deque.peekFirst();             // ArrayDeque
sortedSet.first();             // TreeSet
linkedHashSet.iterator().next(); // LinkedHashSet — неудобно!
```

**Единый API после Java 21:**
```java
// SequencedCollection — единый интерфейс
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
LinkedHashSet<String> set = new LinkedHashSet<>(List.of("x", "y", "z"));
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("one", 1); map.put("two", 2);

// Первый/последний элемент (унифицировано)
list.getFirst();   // "a"
list.getLast();    // "c"
set.getFirst();    // "x"
set.getLast();     // "z"

// Добавление к началу/концу
list.addFirst("z");
list.addLast("d");

// Удаление
list.removeFirst();
list.removeLast();

// Обратный порядок — возвращает view, не копию!
SequencedCollection<String> reversed = list.reversed();

// SequencedMap — дополнительные методы
Map.Entry<String, Integer> first = map.firstEntry();
Map.Entry<String, Integer> last = map.lastEntry();
Map.Entry<String, Integer> poll = map.pollFirstEntry(); // удаляет и возвращает
LinkedHashMap<String, Integer> rev = (LinkedHashMap<String, Integer>) map.reversed();
```

**Ключевые особенности:**
- `reversed()` возвращает **view** — изменения отражаются в оригинале
- `getFirst()`/`getLast()` бросают `NoSuchElementException` для пустых коллекций
- `SequencedSet.reversed()` тоже возвращает `SequencedSet`
- `LinkedHashSet` наконец получил удобный API без хаков через итератор

## Q42. (!) Как устроен `ConcurrentHashMap` изнутри и почему он быстрее `Hashtable`?

`ConcurrentHashMap` (Java 8+) использует **сегментированную блокировку** на уровне отдельных бакетов вместо блокировки всей таблицы, как в `Hashtable`.

**Внутренняя структура (Java 8+):**
- Массив нод (`Node<K,V>[]`) — аналогично `HashMap`
- Запись/обновление блокирует только первую ноду бакета (`synchronized(bin)`)
- Чтение — **вообще без блокировки** (поля `val` и `next` объявлены `volatile`)
- При заполнении бакет переходит от `LinkedList` к `TreeBin` (красно-чёрное дерево) при 8+ элементах

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Потокобезопасные атомарные операции
map.putIfAbsent("key", 1);                     // атомарно
map.computeIfAbsent("key", k -> expensive(k)); // атомарно
map.merge("counter", 1, Integer::sum);         // инкремент счётчика
map.compute("key", (k, v) -> v == null ? 1 : v + 1); // обновление

// Параллельная обработка
map.forEach(4, (k, v) -> process(k, v));       // parallelismThreshold = 4
long sum = map.reduceValues(4, Integer::sum);
```

**Ключевые отличия от конкурентов:**

| Характеристика | `Hashtable` | `synchronizedMap` | `ConcurrentHashMap` |
|---|---|---|---|
| Блокировка | Вся таблица | Вся таблица | Один бакет |
| Чтение | Блокирует | Блокирует | **Без блокировки** |
| `null` ключи/значения | Нет | Нет | **Нет** |
| `size()` точность | Точно | Точно | Приблизительно |
| Итератор | Fail-fast | Fail-fast | **Weakly consistent** |

**Почему нет `null`:** при `get()` невозможно различить "ключ отсутствует" и "значение равно `null`" в конкурентном контексте без дополнительной синхронизации.

## Q43. (!) Как реализовать LRU-кэш на `LinkedHashMap`?

`LinkedHashMap` поддерживает режим **access-order** — при обращении к элементу он перемещается в конец. Переопределив `removeEldestEntry()`, получаем готовый LRU-кэш.

```java
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LruCache(int maxSize) {
        // true = access-order (false = insertion-order по умолчанию)
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize; // удаляем самый давно используемый
    }
}

// Использование
LruCache<String, String> cache = new LruCache<>(3);
cache.put("a", "1");
cache.put("b", "2");
cache.put("c", "3");
cache.get("a"); // "a" перемещается в конец, "b" теперь самый старый
cache.put("d", "4"); // "b" вытесняется
System.out.println(cache.keySet()); // [c, a, d]
```

**Потокобезопасный вариант:**
```java
// Для многопоточного доступа — использовать Collections.synchronizedMap
Map<K, V> syncCache = Collections.synchronizedMap(new LruCache<>(100));

// Или Caffeine/Guava Cache для production
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .build();
```

## Q44. Что такое `NavigableMap` и как использовать методы навигации `TreeMap`?

`NavigableMap<K,V>` расширяет `SortedMap` методами навигации — поиском ближайших ключей по критерию "больше/меньше/включительно".

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(1, "one"); map.put(3, "three"); map.put(5, "five");
map.put(7, "seven"); map.put(9, "nine");

// Навигация
map.floorKey(4);    // 3 — наибольший ключ <= 4
map.ceilingKey(4);  // 5 — наименьший ключ >= 4
map.lowerKey(5);    // 3 — строго меньше 5
map.higherKey(5);   // 7 — строго больше 5

// Поддиапазоны (views — изменения отражаются в оригинале!)
SortedMap<Integer, String> sub = map.subMap(3, true, 7, true); // [3..7]
SortedMap<Integer, String> head = map.headMap(5, false);        // [1..4]
SortedMap<Integer, String> tail = map.tailMap(5, true);         // [5..9]

// Обратный порядок
NavigableMap<Integer, String> desc = map.descendingMap();
map.descendingKeySet().forEach(k -> System.out.print(k + " ")); // 9 7 5 3 1

// Практический пример: задачи планировщика
TreeMap<Instant, Runnable> scheduler = new TreeMap<>();
scheduler.put(Instant.now().plusSeconds(10), () -> task1());
scheduler.put(Instant.now().plusSeconds(5), () -> task2());

// Забрать все задачи, время которых пришло
Map<Instant, Runnable> due = scheduler.headMap(Instant.now(), true);
due.values().forEach(Runnable::run);
due.clear();
```

## Q45. Что такое `PriorityQueue` и как реализовать кастомный порядок?

`PriorityQueue<E>` — очередь с приоритетом, реализованная на основе **двоичной min-кучи**. Элементы извлекаются в порядке, определённом `Comparable` или `Comparator`.

```java
// Min-heap по умолчанию
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.addAll(List.of(5, 1, 3, 2, 4));
while (!minHeap.isEmpty()) {
    System.out.print(minHeap.poll() + " "); // 1 2 3 4 5
}

// Max-heap через reverseOrder
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

// Кастомный порядок для объектов
record Task(String name, int priority) {}

PriorityQueue<Task> taskQueue = new PriorityQueue<>(
    Comparator.comparingInt(Task::priority).reversed() // высший приоритет первым
);
taskQueue.add(new Task("low", 1));
taskQueue.add(new Task("high", 10));
taskQueue.add(new Task("medium", 5));

// Алгоритм: K наименьших элементов из большого массива
static List<Integer> kSmallest(int[] nums, int k) {
    // Max-heap размером k
    PriorityQueue<Integer> heap = new PriorityQueue<>(k, Comparator.reverseOrder());
    for (int n : nums) {
        heap.offer(n);
        if (heap.size() > k) heap.poll(); // убираем максимум
    }
    return new ArrayList<>(heap); // k наименьших
}
```

**Сложность операций:**
- `offer()`/`add()`: O(log n)
- `poll()`/`remove()`: O(log n)
- `peek()`: O(1)
- Итерация: **не гарантирует порядок** (только при poll)
- `contains()`/`remove(Object)`: O(n) — линейный поиск

`PriorityQueue` **не потокобезопасна**. Для конкурентного использования — `PriorityBlockingQueue`.

## Q46. Что такое `WeakHashMap` и когда его использовать?

`WeakHashMap<K,V>` — `Map`, хранящая ключи через **слабые ссылки** (`WeakReference`). Когда ключ становится недостижимым (нет других сильных ссылок), GC может удалить запись из карты.

```java
WeakHashMap<Object, String> cache = new WeakHashMap<>();

Object key1 = new Object();
Object key2 = new Object();
cache.put(key1, "value1");
cache.put(key2, "value2");

System.out.println(cache.size()); // 2

key1 = null; // удаляем сильную ссылку на ключ
System.gc();  // подсказываем GC (не гарантируется)

// После GC запись с key1 может быть удалена
System.out.println(cache.size()); // вероятно 1
```

**Практические применения:**

```java
// 1. Кэш метаданных объектов — данные живут столько же, сколько объект
WeakHashMap<Object, Map<String, Object>> metadataCache = new WeakHashMap<>();

// 2. Listener registry — автоматическая очистка "мёртвых" слушателей
WeakHashMap<EventListener, Boolean> listeners = new WeakHashMap<>();
listeners.put(myListener, Boolean.TRUE);
// После GC мёртвых слушателей — они сами исчезают из listeners

// 3. Мемоизация без утечки памяти
WeakHashMap<ExpensiveKey, Result> memoTable = new WeakHashMap<>();
```

**Предостережения:**
- Не подходит для кэша значений (ключ `String` из пула не собирается — будет жить вечно)
- `size()` может изменяться между вызовами из-за GC
- Не потокобезопасен — для конкурентного кэша использовать `java.lang.ref.WeakReference` с `ConcurrentHashMap` или Caffeine с `weakKeys()`

Более подробно о новых возможностях Java 21 — в [вопросах по Java 17-21](java-17-21-interview.md), а о Stream API — в [вопросах по Stream API](java-stream-interview.md).

---

## See also

- [Java Stream API](java-stream-interview.md) — вопросы по `Stream API`, тесно связаны с коллекциями
- [Java Concurrency](java-concurrency-interview.md) — потокобезопасные коллекции и синхронизация
- [Java Core](java-core-interview.md) — основы языка, контракт `equals`/`hashCode`, иммутабельность
- [Java Generics](java-generics-interview.md) — параметризация коллекций, `PECS`, wildcards
- [Java 8+](java-8-interview.md) — новые методы коллекций, `Stream API`, фабричные методы `List.of()`
- [OOP & Java](java-oop-interview.md) — `Comparable`, `Comparator`, отношение is-a / has-a
- [Система типов Java](java-types-interview.md) — `autoboxing` в коллекциях, производительность
- [Java 17-21](java-17-21-interview.md) — `SequencedCollection`, улучшения в `Collections API`
- [Алгоритмы и структуры данных](../../algorithms/algorithms-interview.md) — сложность операций, выбор структуры данных
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Iterator, Composite, Decorator в контексте коллекций

- [Java 17-21](java-17-21-interview.md)
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
- [Java Core](java-core-interview.md)
