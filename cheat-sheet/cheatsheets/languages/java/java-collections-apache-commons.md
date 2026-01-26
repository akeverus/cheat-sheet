---
title: "Java Collections - Apache Commons"
description: "Комплексное руководство по Apache Commons Collections: Bag, SetUtils, OrderedMap, BidiMap, CollectionUtils, MapUtils, CircularFifoQueue и другие коллекции с примерами и best practices"
tags: ["java", "collections", "apache-commons", "data-structures", "bag", "bidimap", "orderedmap", "collectionutils", "maputils", "circularfifoqueue"]
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "java/java-collections-list.md"]
next: ["java/java-collections-set.md", "databases/postgres-basics.md"]
updated: "2025-01-11"
related: ["java/java-basics.md", "java/java-collections-list.md", "java/java-collections-set.md"]
---

# Java Collections - Apache Commons

Это подробное руководство по библиотеке Apache Commons Collections — мощному расширению стандартного Java Collections Framework. Библиотека предоставляет дополнительные структуры данных, утилиты и декораторы, которые значительно расширяют возможности работы с коллекциями в Java.

## Содержание

### Основы Apache Commons Collections
- Что такое Apache Commons Collections
- Установка и зависимости
- Основные концепции и паттерны

### Bag и мультимножества
- HashBag и TreeBag
- Операции с мультимножествами
- Сравнение с другими коллекциями

### SetUtils - утилиты для множеств
- Объединение и пересечение множеств
- Разность множеств
- Проверка подмножеств

### OrderedMap - упорядоченные карты
- LinkedMap и ListOrderedMap
- LRU кэширование
- Двунаправленная навигация

### BidiMap - двунаправленные карты
- DualHashBidiMap и TreeBidiMap
- Обратные операции
- Обработка конфликтов

### CollectionUtils - утилиты для коллекций
- Предикаты и трансформации
- Фильтрация и селекция
- Объединение и разделение коллекций

### MapUtils - утилиты для карт
- Безопасные операции с null
- Трансформации и фильтры
- Итерация и отладка

### CircularFifoQueue - кольцевая очередь
- Реализация кольцевого буфера
- Производительность и ограничения
- Использование в многопоточной среде

### Продвинутые коллекции
- MultiMap и MultiValueMap
- LazyMap и FactoryMap
- Composite collections
- Unmodifiable и Predicated collections

### Декораторы коллекций
- Synchronized collections
- Unmodifiable wrappers
- Predicated collections
- Transformed collections

### Производительность и оптимизации
- Сравнение производительности
- Memory footprint анализ
- Best practices по использованию

### Интеграция с Java 8+
- Stream API интеграция
- Lambda expressions
- Method references
- Optional integration

### Тестирование и отладка
- Unit testing коллекций
- Debugging утилиты
- Performance benchmarking

### Миграция и обновление
- Миграция с commons-collections 3.x
- Обновление до последней версии
- Compatibility matrix

## Основы Apache Commons Collections

### Что такое Apache Commons Collections

**Apache Commons Collections** — это библиотека, разработанная Apache Software Foundation, которая расширяет возможности стандартного Java Collections Framework. Она предоставляет дополнительные структуры данных, утилиты и декораторы, которые часто требуются в enterprise приложениях.

**Основные преимущества:**

1. **Дополнительные структуры данных** - Bag, BidiMap, OrderedMap и другие
2. **Удобные утилиты** - CollectionUtils, MapUtils, SetUtils
3. **Декораторы** - synchronized, unmodifiable, predicated коллекции
4. **Высокая производительность** - оптимизированные реализации
5. **Backward compatibility** - поддержка старых версий Java

**Когда использовать Apache Commons Collections:**

- Когда стандартных коллекций недостаточно
- Для сложных операций с коллекциями
- В enterprise приложениях с высокими требованиями к производительности
- При работе с legacy кодом
- Для создания DSL (Domain Specific Language) для работы с данными

### Установка и зависимости

**Maven зависимость:**
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

**Gradle зависимость:**
```gradle
implementation 'org.apache.commons:commons-collections4:4.4'
```

**Проверка версии:**
```java
// Проверка версии в runtime
String version = org.apache.commons.collections4.CollectionUtils.class.getPackage().getImplementationVersion();
System.out.println("Apache Commons Collections version: " + version);
```

**Требования к Java версии:**
- Commons Collections 4.x требует Java 8+
- Commons Collections 3.x поддерживает Java 1.2+
- Рекомендуется использовать последнюю версию для новых проектов

### Основные концепции и паттерны

**Decorator Pattern:**
```java
// Стандартный подход
List<String> synchronizedList = Collections.synchronizedList(new ArrayList<>());

// Apache Commons подход
List<String> predicatedList = ListUtils.predicatedList(new ArrayList<>(), notNullPredicate);
```

**Factory Pattern:**
```java
// Создание коллекций с фабриками
Factory<String> factory = () -> "default";
Map<String, String> lazyMap = LazyMap.lazyMap(new HashMap<>(), factory);
```

**Predicate Pattern:**
```java
// Функциональные предикаты
Predicate<String> notEmpty = StringUtils::isNotEmpty;
Predicate<Integer> positive = i -> i > 0;

// Использование в коллекциях
Collection<String> filtered = CollectionUtils.select(strings, notEmpty);
```

## Bag и мультимножества

### HashBag - базовая реализация Bag

**Bag** — это коллекция, которая позволяет хранить несколько экземпляров одного элемента. В отличие от Set, Bag может содержать дубликаты и отслеживает количество каждого элемента.

**Основные характеристики:**
- Позволяет дубликаты элементов
- Поддерживает счетчики для каждого элемента
- Быстрые операции добавления/удаления
- Не упорядоченная коллекция

**Примеры использования:**
```java
@Test
public void whenUsingHashBag_thenCorrect() {
    // Создание Bag
    Bag<String> bag = new HashBag<>();

    // Добавление элементов
    bag.add("apple", 3);  // Добавляем 3 яблока
    bag.add("orange", 2); // Добавляем 2 апельсина
    bag.add("apple");     // Добавляем еще одно яблоко

    // Проверка счетчиков
    assertEquals(4, bag.getCount("apple"));
    assertEquals(2, bag.getCount("orange"));
    assertEquals(1, bag.getCount("banana")); // Не существующий элемент

    // Удаление элементов
    bag.remove("apple", 2); // Удаляем 2 яблока
    assertEquals(2, bag.getCount("apple"));

    // Общее количество элементов
    assertEquals(4, bag.size()); // 2 яблока + 2 апельсина

    // Уникальные элементы
    assertEquals(2, bag.uniqueSet().size()); // apple и orange
}
```

**Сравнение с другими коллекциями:**
```java
// List - позволяет дубликаты, но медленный подсчет
List<String> list = Arrays.asList("apple", "apple", "orange", "orange", "apple");
long appleCount = list.stream().filter("apple"::equals).count(); // O(n)

// Map - требует дополнительной логики
Map<String, Integer> map = new HashMap<>();
map.put("apple", 3);
map.put("orange", 2);
// Требуется ручной подсчет и обновление

// Bag - оптимальное решение
Bag<String> bag = new HashBag<>(Arrays.asList("apple", "apple", "orange", "orange", "apple"));
int appleCount = bag.getCount("apple"); // O(1)
```

### TreeBag - отсортированная версия

**TreeBag** — это отсортированная версия Bag, которая поддерживает естественный порядок элементов или кастомный компаратор.

```java
@Test
public void whenUsingTreeBag_thenSorted() {
    TreeBag<String> bag = new TreeBag<>();

    bag.add("zebra", 2);
    bag.add("apple", 3);
    bag.add("banana", 1);

    // Элементы отсортированы
    assertEquals("[apple, apple, apple, banana, zebra, zebra]",
                 bag.toString());

    // Итерация в отсортированном порядке
    List<String> sorted = new ArrayList<>();
    for (String item : bag) {
        sorted.add(item);
    }
    assertEquals(Arrays.asList("apple", "apple", "apple", "banana", "zebra", "zebra"), sorted);
}
```

**С кастомным компаратором:**
```java
@Test
public void whenUsingTreeBagWithComparator_thenCustomOrder() {
    TreeBag<String> bag = new TreeBag<>(Comparator.reverseOrder());

    bag.add("apple");
    bag.add("zebra");
    bag.add("banana");

    // Обратный порядок
    Iterator<String> iterator = bag.iterator();
    assertEquals("zebra", iterator.next());
    assertEquals("banana", iterator.next());
    assertEquals("apple", iterator.next());
}
```

### Операции с Bag

**Математические операции:**
```java
@Test
public void whenPerformingBagOperations_thenCorrect() {
    Bag<String> bag1 = new HashBag<>(Arrays.asList("apple", "orange", "apple"));
    Bag<String> bag2 = new HashBag<>(Arrays.asList("apple", "banana", "banana"));

    // Объединение
    Bag<String> union = new HashBag<>();
    union.addAll(bag1);
    union.addAll(bag2);
    assertEquals(2, union.getCount("apple"));  // max(2, 1) = 2
    assertEquals(1, union.getCount("orange")); // 1
    assertEquals(2, union.getCount("banana")); // 2

    // Пересечение
    Bag<String> intersection = new HashBag<>();
    for (String item : bag1.uniqueSet()) {
        int count1 = bag1.getCount(item);
        int count2 = bag2.getCount(item);
        intersection.add(item, Math.min(count1, count2));
    }
    assertEquals(1, intersection.getCount("apple")); // min(2, 1) = 1
    assertEquals(0, intersection.getCount("orange"));
    assertEquals(0, intersection.getCount("banana"));
}
```

**Статистические операции:**
```java
public class BagStatistics {

    public static <T> double getAverageCount(Bag<T> bag) {
        if (bag.isEmpty()) return 0.0;
        return (double) bag.size() / bag.uniqueSet().size();
    }

    public static <T> T getMostFrequent(Bag<T> bag) {
        return bag.uniqueSet().stream()
            .max(Comparator.comparing(bag::getCount))
            .orElse(null);
    }

    public static <T> Map<T, Double> getFrequencyDistribution(Bag<T> bag) {
        int total = bag.size();
        return bag.uniqueSet().stream()
            .collect(Collectors.toMap(
                item -> item,
                item -> (double) bag.getCount(item) / total
            ));
    }
}

@Test
public void whenCalculatingStatistics_thenCorrect() {
    Bag<String> bag = new HashBag<>();
    bag.add("apple", 5);
    bag.add("orange", 3);
    bag.add("banana", 2);

    assertEquals(3.33, BagStatistics.getAverageCount(bag), 0.01);
    assertEquals("apple", BagStatistics.getMostFrequent(bag));

    Map<String, Double> distribution = BagStatistics.getFrequencyDistribution(bag);
    assertEquals(0.5, distribution.get("apple"), 0.01);  // 5/10
    assertEquals(0.3, distribution.get("orange"), 0.01); // 3/10
    assertEquals(0.2, distribution.get("banana"), 0.01); // 2/10
}
```

### Сравнение Bag с другими коллекциями

| Характеристика | Bag | List | Set | Map |
|----------------|-----|------|-----|-----|
| **Дубликаты** | Да | Да | Нет | Ключи уникальны |
| **Порядок** | Нет (HashBag), Да (TreeBag) | Да | Нет (HashSet), Да (TreeSet) | Нет (HashMap), Да (TreeMap) |
| **Подсчет элементов** | O(1) | O(n) | - | O(1) |
| **Поиск элемента** | O(1) | O(n) | O(1) | O(1) |
| **Использование памяти** | Среднее | Низкое | Низкое | Среднее |
| **Основное назначение** | Подсчет частот | Последовательность | Уникальность | Ключ-значение |

## SetUtils - утилиты для множеств

### Объединение множеств

```java
@Test
public void whenUnion_thenCorrect() {
    Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
    Set<Integer> set2 = new HashSet<>(Arrays.asList(3, 4, 5));

    // Объединение
    Set<Integer> union = SetUtils.union(set1, set2);
    assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5)), union);

    // Альтернативный способ
    Set<Integer> manualUnion = new HashSet<>(set1);
    manualUnion.addAll(set2);
    assertEquals(union, manualUnion);
}
```

### Пересечение множеств

```java
@Test
public void whenIntersection_thenCorrect() {
    Set<String> set1 = new HashSet<>(Arrays.asList("apple", "orange", "banana"));
    Set<String> set2 = new HashSet<>(Arrays.asList("orange", "banana", "grape"));

    // Пересечение
    Set<String> intersection = SetUtils.intersection(set1, set2);
    assertEquals(new HashSet<>(Arrays.asList("orange", "banana")), intersection);

    // Проверка пересечения с null
    Set<String> intersectionWithNull = SetUtils.intersection(set1, null);
    assertTrue(intersectionWithNull.isEmpty());
}
```

### Разность множеств

```java
@Test
public void whenDifference_thenCorrect() {
    Set<Character> set1 = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd'));
    Set<Character> set2 = new HashSet<>(Arrays.asList('c', 'd', 'e', 'f'));

    // Разность set1 - set2
    Set<Character> difference = SetUtils.difference(set1, set2);
    assertEquals(new HashSet<>(Arrays.asList('a', 'b')), difference);

    // Симметричная разность
    Set<Character> symmetricDifference = new HashSet<>(SetUtils.difference(set1, set2));
    symmetricDifference.addAll(SetUtils.difference(set2, set1));
    assertEquals(new HashSet<>(Arrays.asList('a', 'b', 'e', 'f')), symmetricDifference);
}
```

### Проверка отношений между множествами

```java
@Test
public void whenCheckingSetRelations_thenCorrect() {
    Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
    Set<Integer> subset = new HashSet<>(Arrays.asList(1, 2));
    Set<Integer> superset = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
    Set<Integer> disjoint = new HashSet<>(Arrays.asList(4, 5, 6));

    // Проверка подмножества
    assertTrue(SetUtils.isSubset(subset, set1));
    assertFalse(SetUtils.isSubset(set1, subset));

    // Проверка равенства
    Set<Integer> copy = new HashSet<>(set1);
    assertTrue(SetUtils.isEqualSet(set1, copy));

    // Проверка непересекающихся множеств
    assertFalse(SetUtils.hasIntersection(set1, disjoint));
    assertTrue(SetUtils.hasIntersection(set1, subset));
}
```

### Transform и filter операции

```java
@Test
public void whenTransformingSet_thenCorrect() {
    Set<String> original = new HashSet<>(Arrays.asList("hello", "world", "java"));

    // Трансформация в верхний регистр
    Set<String> transformed = SetUtils.transformedSet(original,
        TransformerUtils.stringValueTransformer());

    // Фильтрация по длине
    Set<String> filtered = SetUtils.predicatedSet(transformed,
        PredicateUtils.lengthPredicate(5)); // слова длиной > 5

    assertEquals(new HashSet<>(Arrays.asList("HELLO", "WORLD")), filtered);
}
```

## OrderedMap - упорядоченные карты

### LinkedMap - поддержка порядка вставки

**LinkedMap** — это карта, которая поддерживает порядок вставки элементов, подобно LinkedHashMap из стандартной библиотеки.

```java
@Test
public void whenUsingLinkedMap_thenMaintainsInsertionOrder() {
    OrderedMap<String, Integer> map = new LinkedMap<>();

    map.put("first", 1);
    map.put("second", 2);
    map.put("third", 3);

    // Порядок вставки сохраняется
    assertEquals("first", map.firstKey());
    assertEquals("third", map.lastKey());

    // Итерация в порядке вставки
    List<String> keys = new ArrayList<>();
    for (String key : map.keySet()) {
        keys.add(key);
    }
    assertEquals(Arrays.asList("first", "second", "third"), keys);
}
```

### ListOrderedMap - список-карта

**ListOrderedMap** — это OrderedMap, которая поддерживает доступ к элементам по индексу.

```java
@Test
public void whenUsingListOrderedMap_thenIndexAccess() {
    ListOrderedMap<String, String> map = new ListOrderedMap<>();

    map.put("key1", "value1");
    map.put("key2", "value2");
    map.put("key3", "value3");

    // Доступ по индексу
    assertEquals("key1", map.get(0));
    assertEquals("value2", map.getValue(1));
    assertEquals(2, map.indexOf("key3"));

    // Установка значения по индексу
    map.setValue(1, "newValue2");
    assertEquals("newValue2", map.get("key2"));

    // Удаление по индексу
    map.remove(0);
    assertEquals("key2", map.get(0));
}
```

### LRU Map - least recently used кэш

```java
@Test
public void whenUsingLRUMap_thenEvictsLeastRecentlyUsed() {
    // LRU карта с максимум 3 элементами
    LRUMap<String, Integer> lruMap = new LRUMap<>(3);

    lruMap.put("a", 1);
    lruMap.put("b", 2);
    lruMap.put("c", 3);
    assertEquals(3, lruMap.size());

    // Доступ к элементу 'a' делает его наиболее недавно использованным
    lruMap.get("a");

    // Добавление 4-го элемента вытесняет 'b' (наименее недавно использованный)
    lruMap.put("d", 4);
    assertFalse(lruMap.containsKey("b"));
    assertTrue(lruMap.containsKey("a"));
    assertTrue(lruMap.containsKey("c"));
    assertTrue(lruMap.containsKey("d"));
}
```

### Двунаправленная навигация

```java
@Test
public void whenNavigatingOrderedMap_thenCorrect() {
    OrderedMap<String, Integer> map = new LinkedMap<>();
    map.put("first", 1);
    map.put("second", 2);
    map.put("third", 3);
    map.put("fourth", 4);

    // Навигация
    assertEquals("first", map.firstKey());
    assertEquals("fourth", map.lastKey());

    assertEquals("second", map.nextKey("first"));
    assertEquals("third", map.nextKey("second"));
    assertNull(map.nextKey("fourth"));

    assertEquals("third", map.previousKey("fourth"));
    assertEquals("second", map.previousKey("third"));
    assertNull(map.previousKey("first"));
}
```

## BidiMap - двунаправленные карты

### DualHashBidiMap - базовая двунаправленная карта

**BidiMap** — это карта, которая позволяет искать значения по ключам и ключи по значениям. Все значения в BidiMap должны быть уникальными.

```java
@Test
public void whenUsingDualHashBidiMap_thenBidirectionalLookup() {
    BidiMap<String, String> bidiMap = new DualHashBidiMap<>();

    bidiMap.put("key1", "value1");
    bidiMap.put("key2", "value2");
    bidiMap.put("key3", "value3");

    // Прямой поиск
    assertEquals("value2", bidiMap.get("key2"));

    // Обратный поиск
    assertEquals("key2", bidiMap.getKey("value2"));

    // Проверка уникальности значений
    assertEquals(3, bidiMap.size());
    assertEquals(3, bidiMap.keySet().size());
    assertEquals(3, bidiMap.values().size());
}
```

**Обработка конфликтов при вставке:**
```java
@Test
public void whenHandlingValueConflicts_thenReplaces() {
    BidiMap<String, String> bidiMap = new DualHashBidiMap<>();

    bidiMap.put("key1", "value1");
    bidiMap.put("key2", "value1"); // Конфликт значения!

    // Старый mapping key1 -> value1 удален
    assertFalse(bidiMap.containsKey("key1"));
    assertEquals("key2", bidiMap.getKey("value1"));
    assertEquals("value1", bidiMap.get("key2"));
}
```

### TreeBidiMap - отсортированная двунаправленная карта

**TreeBidiMap** поддерживает естественный порядок ключей.

```java
@Test
public void whenUsingTreeBidiMap_thenSortedKeys() {
    TreeBidiMap<String, Integer> bidiMap = new TreeBidiMap<>();

    bidiMap.put("zebra", 3);
    bidiMap.put("apple", 1);
    bidiMap.put("banana", 2);

    // Ключи отсортированы
    assertEquals("apple", bidiMap.firstKey());
    assertEquals("zebra", bidiMap.lastKey());

    // Итерация в отсортированном порядке
    List<String> keys = new ArrayList<>(bidiMap.keySet());
    assertEquals(Arrays.asList("apple", "banana", "zebra"), keys);
}
```

### Обратные операции

```java
@Test
public void whenUsingInverseBidiMap_thenReversedOperations() {
    BidiMap<String, String> bidiMap = new DualHashBidiMap<>();
    bidiMap.put("user1", "john@example.com");
    bidiMap.put("user2", "jane@example.com");

    // Получение inverse map
    BidiMap<String, String> inverseMap = bidiMap.inverseBidiMap();

    // В inverse map ключи и значения поменялись местами
    assertEquals("user1", inverseMap.get("john@example.com"));
    assertEquals("user2", inverseMap.get("jane@example.com"));

    // Оригинальная карта не изменилась
    assertEquals("john@example.com", bidiMap.get("user1"));
}
```

### Практические примеры использования

**Кэширование с обратным поиском:**
```java
public class UserCache {
    private final BidiMap<Long, String> idToEmail = new DualHashBidiMap<>();
    private final Map<String, User> emailToUser = new HashMap<>();

    public void put(User user) {
        idToEmail.put(user.getId(), user.getEmail());
        emailToUser.put(user.getEmail(), user);
    }

    public User getById(Long id) {
        String email = idToEmail.get(id);
        return email != null ? emailToUser.get(email) : null;
    }

    public User getByEmail(String email) {
        return emailToUser.get(email);
    }

    public Long getIdByEmail(String email) {
        return idToEmail.getKey(email);
    }

    public String getEmailById(Long id) {
        return idToEmail.get(id);
    }
}
```

**Словарь с обратным переводом:**
```java
@Test
public void whenUsingBidiMapForTranslation_thenBidirectional() {
    BidiMap<String, String> dictionary = new DualHashBidiMap<>();

    dictionary.put("hello", "привет");
    dictionary.put("world", "мир");
    dictionary.put("java", "джава");

    // Перевод с английского на русский
    assertEquals("привет", dictionary.get("hello"));

    // Перевод с русского на английский
    assertEquals("hello", dictionary.getKey("привет"));

    // Проверка наличия слов
    assertTrue(dictionary.containsKey("world"));
    assertTrue(dictionary.containsValue("мир"));
}
```

## CollectionUtils - утилиты для коллекций

### Предикаты и фильтрация

```java
@Test
public void whenUsingPredicates_thenFilterCollections() {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // Предикаты
    Predicate<Integer> even = i -> i % 2 == 0;
    Predicate<Integer> greaterThan5 = i -> i > 5;

    // Фильтрация
    Collection<Integer> evenNumbers = CollectionUtils.select(numbers, even);
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), evenNumbers);

    Collection<Integer> rejected = CollectionUtils.selectRejected(numbers, even);
    assertEquals(Arrays.asList(1, 3, 5, 7, 9), rejected);

    // Составные предикаты
    Predicate<Integer> evenAndGreaterThan5 = PredicateUtils.andPredicate(even, greaterThan5);
    Collection<Integer> result = CollectionUtils.select(numbers, evenAndGreaterThan5);
    assertEquals(Arrays.asList(6, 8, 10), result);
}
```

### Трансформации коллекций

```java
@Test
public void whenTransformingCollections_thenApplyFunctions() {
    List<String> words = Arrays.asList("hello", "world", "java");

    // Трансформация в верхний регистр
    Transformer<String, String> upperCase = String::toUpperCase;
    Collection<String> upperWords = CollectionUtils.collect(words, upperCase);
    assertEquals(Arrays.asList("HELLO", "WORLD", "JAVA"), upperWords);

    // Трансформация чисел
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
    Transformer<Integer, String> toString = Object::toString;
    Collection<String> stringNumbers = CollectionUtils.collect(numbers, toString);
    assertEquals(Arrays.asList("1", "2", "3", "4", "5"), stringNumbers);

    // Цепочка трансформаций
    Transformer<String, Integer> length = String::length;
    Collection<Integer> lengths = CollectionUtils.collect(words, length);
    assertEquals(Arrays.asList(5, 5, 4), lengths);
}
```

### Объединение и разделение коллекций

```java
@Test
public void whenMergingCollections_thenCombineElements() {
    List<String> list1 = Arrays.asList("a", "b", "c");
    List<String> list2 = Arrays.asList("1", "2", "3");

    // Объединение списков
    List<String> union = new ArrayList<>();
    union.addAll(list1);
    union.addAll(list2);
    assertEquals(Arrays.asList("a", "b", "c", "1", "2", "3"), union);

    // Пересечение
    List<String> intersection = new ArrayList<>(list1);
    intersection.retainAll(Arrays.asList("b", "c", "d"));
    assertEquals(Arrays.asList("b", "c"), intersection);

    // Разность
    List<String> difference = new ArrayList<>(list1);
    difference.removeAll(Arrays.asList("b", "d"));
    assertEquals(Arrays.asList("a", "c"), difference);
}
```

### Partitioning - разделение коллекций

```java
@Test
public void whenPartitioningCollections_thenSplitIntoGroups() {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // Разделение на четные и нечетные
    Predicate<Integer> even = i -> i % 2 == 0;
    List<List<Integer>> partitioned = ListUtils.partition(numbers, even);

    assertEquals(Arrays.asList(2, 4, 6, 8, 10), partitioned.get(0)); // even
    assertEquals(Arrays.asList(1, 3, 5, 7, 9), partitioned.get(1));  // odd

    // Разделение по размеру
    List<List<Integer>> chunked = ListUtils.partition(numbers, 3);
    assertEquals(Arrays.asList(1, 2, 3), chunked.get(0));
    assertEquals(Arrays.asList(4, 5, 6), chunked.get(1));
    assertEquals(Arrays.asList(7, 8, 9), chunked.get(2));
    assertEquals(Arrays.asList(10), chunked.get(3));
}
```

## MapUtils - утилиты для карт

### Безопасные операции с null

```java
@Test
public void whenUsingSafeOperations_thenHandleNulls() {
    Map<String, String> map = new HashMap<>();
    map.put("key1", "value1");
    map.put("key2", null);

    // Безопасное получение значения с дефолтом
    assertEquals("value1", MapUtils.getString(map, "key1"));
    assertEquals("default", MapUtils.getString(map, "key2", "default"));
    assertEquals("default", MapUtils.getString(map, "nonexistent", "default"));

    // Безопасное получение чисел
    Map<String, Object> mixedMap = new HashMap<>();
    mixedMap.put("count", "42");
    mixedMap.put("price", 29.99);

    assertEquals(42, MapUtils.getIntValue(mixedMap, "count"));
    assertEquals(29.99, MapUtils.getDoubleValue(mixedMap, "price"), 0.01);
    assertEquals(0, MapUtils.getIntValue(mixedMap, "nonexistent"));
}
```

### Трансформации карт

```java
@Test
public void whenTransformingMaps_thenApplyFunctions() {
    Map<String, Integer> original = new HashMap<>();
    original.put("a", 1);
    original.put("b", 2);
    original.put("c", 3);

    // Трансформация значений
    Map<String, String> transformed = MapUtils.transformedMap(original,
        TransformerUtils.stringValueTransformer());

    assertEquals("1", transformed.get("a"));
    assertEquals("2", transformed.get("b"));
    assertEquals("3", transformed.get("c"));

    // Трансформация ключей (редко используется)
    Map<String, Integer> keyTransformed = MapUtils.transformedMap(original,
        TransformerUtils.stringValueTransformer(), // key transformer
        null); // value transformer (no change)

    // Фильтрация карты
    Map<String, Integer> filtered = MapUtils.predicatedMap(original,
        PredicateUtils.notNullPredicate(), // key predicate
        PredicateUtils.uniquePredicate()); // value predicate
}
```

### Итерация и отладка

```java
@Test
public void whenDebuggingMaps_thenUsefulOutput() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John");
    map.put("age", 30);
    map.put("city", "New York");

    // Отладочный вывод
    String debugString = MapUtils.debugPrint(System.out, "User Map", map);
    // Выводит:
    // User Map = {
    //   age = 30
    //   city = New York
    //   name = John
    // }

    // Проверка на пустоту
    assertFalse(MapUtils.isEmpty(map));
    assertTrue(MapUtils.isEmpty(null));
    assertTrue(MapUtils.isEmpty(new HashMap<>()));

    // Проверка на не-пустоту
    assertTrue(MapUtils.isNotEmpty(map));
}
```

### Lazy Map - отложенная инициализация

```java
@Test
public void whenUsingLazyMap_thenInitializeOnDemand() {
    Factory<String> factory = () -> "default_value";

    // Создание lazy map
    Map<String, String> lazyMap = LazyMap.lazyMap(new HashMap<>(), factory);

    // Ключ не существует
    assertFalse(lazyMap.containsKey("missing"));

    // При первом обращении создается значение
    String value = lazyMap.get("missing");
    assertEquals("default_value", value);
    assertTrue(lazyMap.containsKey("missing"));

    // Повторное обращение возвращает существующее значение
    assertEquals("default_value", lazyMap.get("missing"));
}
```

## CircularFifoQueue - кольцевая очередь

### Основы CircularFifoQueue

**CircularFifoQueue** — это ограниченная очередь, которая автоматически удаляет старые элементы при достижении максимального размера.

```java
@Test
public void whenUsingCircularFifoQueue_thenFixedSize() {
    CircularFifoQueue<String> queue = new CircularFifoQueue<>(3);

    // Добавление элементов
    queue.add("first");
    queue.add("second");
    queue.add("third");

    assertEquals(3, queue.size());
    assertEquals("first", queue.peek()); // FIFO порядок

    // Добавление 4-го элемента вытесняет первый
    queue.add("fourth");
    assertEquals(3, queue.size());
    assertEquals("second", queue.peek()); // first был удален
    assertFalse(queue.contains("first"));
    assertTrue(queue.contains("fourth"));
}
```

### Производительность и характеристики

```java
@Test
public void whenComparingPerformance_thenCircularFifoQueue() {
    CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(1000);

    // Быстрое добавление
    long start = System.nanoTime();
    for (int i = 0; i < 100000; i++) {
        queue.add(i);
    }
    long end = System.nanoTime();

    System.out.println("CircularFifoQueue add time: " + (end - start) / 1_000_000 + " ms");

    // Сравнение с LinkedList
    LinkedList<Integer> linkedList = new LinkedList<>();
    start = System.nanoTime();
    for (int i = 0; i < 100000; i++) {
        if (linkedList.size() >= 1000) {
            linkedList.removeFirst();
        }
        linkedList.addLast(i);
    }
    end = System.nanoTime();

    System.out.println("LinkedList add/remove time: " + (end - start) / 1_000_000 + " ms");
}
```

### Использование в многопоточной среде

```java
public class ThreadSafeCircularBuffer<T> {
    private final CircularFifoQueue<T> queue;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeCircularBuffer(int capacity) {
        this.queue = new CircularFifoQueue<>(capacity);
    }

    public void add(T item) {
        lock.writeLock().lock();
        try {
            queue.add(item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public T poll() {
        lock.writeLock().lock();
        try {
            return queue.poll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public T peek() {
        lock.readLock().lock();
        try {
            return queue.peek();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return queue.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

**Пример использования для логирования:**
```java
public class CircularLogBuffer {
    private final CircularFifoQueue<LogEntry> logBuffer;
    private final int maxSize = 1000;

    public CircularLogBuffer() {
        this.logBuffer = new CircularFifoQueue<>(maxSize);
    }

    public synchronized void addLog(String level, String message) {
        LogEntry entry = new LogEntry(System.currentTimeMillis(), level, message);
        logBuffer.add(entry);
    }

    public synchronized List<LogEntry> getRecentLogs(int count) {
        List<LogEntry> recent = new ArrayList<>();
        CircularFifoQueue<LogEntry> copy = new CircularFifoQueue<>(logBuffer);

        for (int i = 0; i < Math.min(count, copy.size()); i++) {
            recent.add(copy.poll());
        }

        return recent;
    }

    public static class LogEntry {
        private final long timestamp;
        private final String level;
        private final String message;

        // constructor, getters...
    }
}
```

## Продвинутые коллекции

### MultiMap - карта со множественными значениями

```java
@Test
public void whenUsingMultiMap_thenMultipleValuesPerKey() {
    MultiMap<String, String> multiMap = new MultiValueMap<>();

    multiMap.put("fruits", "apple");
    multiMap.put("fruits", "banana");
    multiMap.put("fruits", "orange");

    multiMap.put("vegetables", "carrot");
    multiMap.put("vegetables", "potato");

    // Получение всех значений для ключа
    Collection<String> fruits = (Collection<String>) multiMap.get("fruits");
    assertEquals(3, fruits.size());
    assertTrue(fruits.contains("apple"));
    assertTrue(fruits.contains("banana"));
    assertTrue(fruits.contains("orange"));

    // Общее количество элементов
    assertEquals(5, multiMap.size());

    // Количество ключей
    assertEquals(2, multiMap.keySet().size());
}
```

### Composite Collections

```java
@Test
public void whenUsingCompositeCollections_thenCombineMultiple() {
    List<String> list1 = new ArrayList<>(Arrays.asList("a", "b"));
    List<String> list2 = new ArrayList<>(Arrays.asList("c", "d"));
    List<String> list3 = new ArrayList<>(Arrays.asList("e", "f"));

    // Создание композитной коллекции
    CompositeCollection<String> composite = new CompositeCollection<>();
    composite.addComposited(list1);
    composite.addComposited(list2);
    composite.addComposited(list3);

    // Все коллекции доступны как одна
    assertEquals(6, composite.size());
    assertTrue(composite.contains("a"));
    assertTrue(composite.contains("d"));
    assertTrue(composite.contains("f"));

    // Добавление в композитную коллекцию
    composite.add("g");
    assertEquals(7, composite.size());
    assertTrue(list1.contains("g")); // Добавлено в последнюю коллекцию
}
```

### Predicated Collections

```java
@Test
public void whenUsingPredicatedCollections_thenValidateElements() {
    // Создание предиката
    Predicate<String> notNullOrEmpty = s -> s != null && !s.trim().isEmpty();

    // Создание predicated коллекции
    Collection<String> predicatedCollection = CollectionUtils.predicatedCollection(
        new ArrayList<>(), notNullOrEmpty);

    // Нормальное добавление
    predicatedCollection.add("valid string");
    assertEquals(1, predicatedCollection.size());

    // Попытка добавить невалидный элемент
    try {
        predicatedCollection.add(""); // пустая строка
        fail("Should throw exception");
    } catch (IllegalArgumentException e) {
        // Ожидаемое поведение
    }

    try {
        predicatedCollection.add(null);
        fail("Should throw exception");
    } catch (IllegalArgumentException e) {
        // Ожидаемое поведение
    }
}
```

## Декораторы коллекций

### Synchronized Collections

```java
@Test
public void whenUsingSynchronizedCollections_thenThreadSafe() {
    // Создание synchronized коллекции
    List<String> synchronizedList = ListUtils.synchronizedList(new ArrayList<>());
    Set<String> synchronizedSet = SetUtils.synchronizedSet(new HashSet<>());
    Map<String, String> synchronizedMap = MapUtils.synchronizedMap(new HashMap<>());

    // Использование в многопоточной среде безопасно
    Runnable task = () -> {
        synchronizedList.add("item" + Thread.currentThread().getId());
        synchronizedSet.add("key" + Thread.currentThread().getId());
        synchronizedMap.put("key" + Thread.currentThread().getId(), "value");
    };

    // Запуск нескольких потоков
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Thread thread = new Thread(task);
        threads.add(thread);
        thread.start();
    }

    // Ожидание завершения
    for (Thread thread : threads) {
        thread.join();
    }

    assertEquals(10, synchronizedList.size());
    assertEquals(10, synchronizedSet.size());
    assertEquals(10, synchronizedMap.size());
}
```

### Unmodifiable Collections

```java
@Test
public void whenUsingUnmodifiableCollections_thenPreventModification() {
    List<String> original = new ArrayList<>(Arrays.asList("a", "b", "c"));

    // Создание unmodifiable коллекции
    List<String> unmodifiable = ListUtils.unmodifiableList(original);

    // Чтение разрешено
    assertEquals(3, unmodifiable.size());
    assertEquals("a", unmodifiable.get(0));

    // Модификация запрещена
    try {
        unmodifiable.add("d");
        fail("Should throw exception");
    } catch (UnsupportedOperationException e) {
        // Ожидаемое поведение
    }

    try {
        unmodifiable.remove(0);
        fail("Should throw exception");
    } catch (UnsupportedOperationException e) {
        // Ожидаемое поведение
    }

    // Но оригинальная коллекция все еще изменяема
    original.add("d");
    assertEquals(4, unmodifiable.size()); // Изменения видны
}
```

### Transformed Collections

```java
@Test
public void whenUsingTransformedCollections_thenAutoTransform() {
    // Создание transforming коллекции
    Transformer<String, String> upperCase = String::toUpperCase;
    Collection<String> transformed = CollectionUtils.transformingCollection(
        new ArrayList<>(), upperCase);

    // Добавление элементов автоматически трансформируется
    transformed.add("hello");
    transformed.add("world");

    // Элементы уже в верхнем регистре
    List<String> list = new ArrayList<>(transformed);
    assertEquals(Arrays.asList("HELLO", "WORLD"), list);

    // Приведение типов
    Transformer<Object, String> toString = Object::toString;
    Collection<String> stringCollection = CollectionUtils.transformingCollection(
        new ArrayList<>(), toString);

    stringCollection.add(42);
    stringCollection.add(3.14);

    assertEquals(Arrays.asList("42", "3.14"), new ArrayList<>(stringCollection));
}
```

## Производительность и оптимизации

### Сравнение производительности

```java
public class PerformanceComparison {

    private static final int ITERATIONS = 100000;

    @Test
    public void compareCollectionPerformance() {
        // Apache Commons Bag
        Bag<String> bag = new HashBag<>();
        long bagStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            bag.add("item" + (i % 100));
        }
        for (int i = 0; i < 100; i++) {
            bag.getCount("item" + i);
        }
        long bagTime = System.nanoTime() - bagStart;

        // Стандартный HashMap
        Map<String, Integer> map = new HashMap<>();
        long mapStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String key = "item" + (i % 100);
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        for (int i = 0; i < 100; i++) {
            map.get("item" + i);
        }
        long mapTime = System.nanoTime() - mapStart;

        System.out.println("Bag time: " + bagTime / 1_000_000 + " ms");
        System.out.println("Map time: " + mapTime / 1_000_000 + " ms");
        System.out.println("Bag is " + (double) mapTime / bagTime + "x faster for counting");
    }
}
```

### Memory Footprint анализ

```java
@Test
public void analyzeMemoryFootprint() {
    // Измерение размера пустых коллекций
    System.gc(); // Очистка памяти

    long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    // Создание различных коллекций
    List<String> arrayList = new ArrayList<>();
    List<String> linkedList = new LinkedList<>();
    Bag<String> hashBag = new HashBag<>();
    Set<String> hashSet = new HashSet<>();
    Map<String, String> hashMap = new HashMap<>();
    BidiMap<String, String> bidiMap = new DualHashBidiMap<>();

    // Добавление элементов
    for (int i = 0; i < 1000; i++) {
        String value = "value" + i;
        arrayList.add(value);
        linkedList.add(value);
        hashBag.add(value);
        hashSet.add(value);
        hashMap.put(value, value);
        if (i < 500) { // BidiMap требует уникальных значений
            bidiMap.put("key" + i, value);
        }
    }

    System.gc();
    long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    long used = after - before;
    System.out.println("Memory used by 1000 elements: " + used + " bytes");
    System.out.println("Average per element: " + (double) used / 1000 + " bytes");
}
```

### Best Practices

#### 1. Выбор правильной коллекции

```java
public class CollectionSelector {

    // Для подсчета частот - используй Bag
    public static Bag<String> createFrequencyCounter() {
        return new HashBag<>();
    }

    // Для двунаправленного поиска - используй BidiMap
    public static BidiMap<String, String> createBidirectionalMap() {
        return new DualHashBidiMap<>();
    }

    // Для LRU кэша - используй LRUMap
    public static Map<String, Object> createLRUCache(int maxSize) {
        return new LRUMap<>(maxSize);
    }

    // Для ограниченной очереди - используй CircularFifoQueue
    public static Queue<String> createBoundedQueue(int capacity) {
        return new CircularFifoQueue<>(capacity);
    }

    // Для упорядоченной карты - используй OrderedMap
    public static OrderedMap<String, Integer> createOrderedMap() {
        return new LinkedMap<>();
    }
}
```

#### 2. Безопасность и валидация

```java
public class SafeCollectionOperations {

    // Безопасное получение из Map
    public static <K, V> V safeGet(Map<K, V> map, K key, V defaultValue) {
        return MapUtils.getObject(map, key, defaultValue);
    }

    // Фильтрация null значений
    public static <T> Collection<T> filterNulls(Collection<T> collection) {
        return CollectionUtils.select(collection, PredicateUtils.notNullPredicate());
    }

    // Валидация перед добавлением
    public static <T> Collection<T> createValidatedCollection(Collection<T> collection, Predicate<T> validator) {
        return CollectionUtils.predicatedCollection(collection, validator);
    }

    // Безопасное объединение коллекций
    public static <T> Collection<T> safeUnion(Collection<T> c1, Collection<T> c2) {
        if (c1 == null && c2 == null) return new ArrayList<>();
        if (c1 == null) return new ArrayList<>(c2);
        if (c2 == null) return new ArrayList<>(c1);
        return CollectionUtils.union(c1, c2);
    }
}
```

#### 3. Оптимизация производительности

```java
public class OptimizedOperations {

    // Batch операции вместо множественных вызовов
    public static <T> void addAll(Collection<T> target, Collection<T> source) {
        target.addAll(source); // Одна операция вместо source.size() операций
    }

    // Использование подходящих структур данных
    public static Bag<String> createOptimizedCounter(Collection<String> items) {
        // Bag эффективнее Map для подсчета
        Bag<String> bag = new HashBag<>(items);
        return bag;
    }

    // Кэширование результатов трансформаций
    private static final Transformer<String, String> cachedTransformer =
        TransformerUtils.asTransformer(String::toUpperCase);

    public static Collection<String> transformCached(Collection<String> input) {
        return CollectionUtils.collect(input, cachedTransformer);
    }

    // Предварительное выделение размера
    public static <T> List<T> createSizedList(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }
}
```

## Интеграция с Java 8+

### Stream API интеграция

```java
@Test
public void whenUsingStreamsWithCommonsCollections_thenPowerful() {
    Bag<String> bag = new HashBag<>(Arrays.asList("apple", "banana", "apple", "orange", "banana", "apple"));

    // Stream API с Bag
    Map<String, Long> frequencyMap = bag.stream()
        .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

    assertEquals(3L, frequencyMap.get("apple").longValue());
    assertEquals(2L, frequencyMap.get("banana").longValue());
    assertEquals(1L, frequencyMap.get("orange").longValue());

    // Фильтрация с Commons predicates
    List<String> filtered = bag.stream()
        .filter(PredicateUtils.notNullPredicate()::evaluate)
        .filter(s -> s.length() > 5)
        .collect(Collectors.toList());

    assertEquals(Arrays.asList("banana", "orange"), filtered);
}
```

### Lambda Expressions

```java
@Test
public void whenUsingLambdasWithCommonsCollections_thenConcise() {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // Трансформация с lambda
    Collection<String> strings = CollectionUtils.collect(numbers, Object::toString);

    // Фильтрация с lambda
    Collection<Integer> evenNumbers = CollectionUtils.select(numbers, n -> n % 2 == 0);

    // Группировка с lambda (используя Java 8+ features)
    Map<Integer, List<Integer>> groupedByParity = numbers.stream()
        .collect(Collectors.groupingBy(n -> n % 2));

    assertEquals(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), strings);
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), evenNumbers);
}
```

### Method References

```java
@Test
public void whenUsingMethodReferences_thenCleanCode() {
    List<String> words = Arrays.asList("hello", "world", "java", "collections");

    // Method reference для трансформации
    Collection<String> upperCase = CollectionUtils.collect(words, String::toUpperCase);

    // Method reference для предиката
    Collection<String> longWords = CollectionUtils.select(words,
        PredicateUtils.asPredicate(String::isEmpty).negate()
            .and(s -> s.length() > 4));

    // Method reference для сравнения
    List<String> sorted = words.stream()
        .sorted(String::compareToIgnoreCase)
        .collect(Collectors.toList());

    assertEquals(Arrays.asList("HELLO", "WORLD", "JAVA", "COLLECTIONS"), upperCase);
    assertEquals(Arrays.asList("hello", "world", "java", "collections"), longWords);
}
```

### Optional Integration

```java
public class OptionalCollectionUtils {

    // Безопасное получение первого элемента
    public static <T> Optional<T> findFirst(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Optional.empty();
        }
        return Optional.ofNullable(CollectionUtils.get(collection, 0));
    }

    // Безопасное получение элемента по индексу
    public static <T> Optional<T> getAtIndex(List<T> list, int index) {
        if (ListUtils.isEmpty(list) || index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(index));
    }

    // Безопасное получение из Map
    public static <K, V> Optional<V> getFromMap(Map<K, V> map, K key) {
        return Optional.ofNullable(MapUtils.getObject(map, key));
    }

    // Фильтрация с Optional
    public static <T> List<T> filterPresent(Collection<Optional<T>> optionals) {
        return optionals.stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
}

@Test
public void whenUsingOptionalWithCollections_thenSafeOperations() {
    List<String> list = Arrays.asList("a", "b", null, "c");

    // Безопасное получение
    Optional<String> first = OptionalCollectionUtils.findFirst(list);
    assertEquals("a", first.orElse(null));

    Optional<String> third = OptionalCollectionUtils.getAtIndex(list, 2);
    assertFalse(third.isPresent()); // null element

    Optional<String> nonExistent = OptionalCollectionUtils.getAtIndex(list, 10);
    assertFalse(nonExistent.isPresent());
}
```

## Тестирование и отладка

### Unit Testing коллекций

```java
public class CommonsCollectionsTest {

    @Test
    public void testBagOperations() {
        Bag<String> bag = new HashBag<>();

        // Тестирование добавления
        bag.add("apple", 3);
        assertEquals(3, bag.getCount("apple"));

        // Тестирование удаления
        bag.remove("apple", 2);
        assertEquals(1, bag.getCount("apple"));

        // Тестирование уникальности
        assertEquals(1, bag.uniqueSet().size());
    }

    @Test
    public void testBidiMapOperations() {
        BidiMap<String, String> bidiMap = new DualHashBidiMap<>();

        bidiMap.put("user1", "john@example.com");

        // Тестирование прямого поиска
        assertEquals("john@example.com", bidiMap.get("user1"));

        // Тестирование обратного поиска
        assertEquals("user1", bidiMap.getKey("john@example.com"));

        // Тестирование конфликтов
        bidiMap.put("user2", "john@example.com"); // Конфликт
        assertFalse(bidiMap.containsKey("user1"));
        assertEquals("user2", bidiMap.getKey("john@example.com"));
    }

    @Test
    public void testOrderedMapOperations() {
        OrderedMap<String, Integer> map = new LinkedMap<>();

        map.put("third", 3);
        map.put("first", 1);
        map.put("second", 2);

        // Тестирование порядка вставки
        assertEquals("third", map.firstKey());
        assertEquals("second", map.lastKey());

        // Тестирование навигации
        assertEquals("first", map.nextKey("third"));
        assertEquals("second", map.nextKey("first"));
    }

    @Test
    public void testCircularFifoQueue() {
        CircularFifoQueue<String> queue = new CircularFifoQueue<>(3);

        // Тестирование ограниченного размера
        queue.add("first");
        queue.add("second");
        queue.add("third");
        assertEquals(3, queue.size());

        // Тестирование вытеснения
        queue.add("fourth");
        assertEquals(3, queue.size());
        assertFalse(queue.contains("first"));
        assertTrue(queue.contains("fourth"));
    }
}
```

### Debugging утилиты

```java
public class CollectionDebugger {

    // Отладочный вывод коллекции
    public static <T> void debugCollection(Collection<T> collection, String label) {
        System.out.println("=== " + label + " ===");
        System.out.println("Size: " + collection.size());
        System.out.println("Type: " + collection.getClass().getSimpleName());
        System.out.println("Elements:");
        int count = 0;
        for (T element : collection) {
            System.out.println("  [" + count++ + "] " + element);
            if (count > 10) { // Ограничение вывода
                System.out.println("  ... and " + (collection.size() - count) + " more");
                break;
            }
        }
        System.out.println("====================");
    }

    // Анализ производительности операций
    public static <T> void benchmarkOperation(Collection<T> collection,
                                              Consumer<Collection<T>> operation,
                                              String operationName,
                                              int iterations) {
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            operation.accept(collection);
        }

        long endTime = System.nanoTime();
        double avgTime = (endTime - startTime) / (double) iterations / 1_000_000; // в миллисекундах

        System.out.println(operationName + " - Average time: " + String.format("%.4f", avgTime) + " ms");
    }

    // Проверка целостности данных
    public static <T> boolean validateCollection(Collection<T> collection) {
        try {
            // Проверка размера
            int size = collection.size();
            if (size < 0) return false;

            // Проверка итерации
            int count = 0;
            for (T element : collection) {
                count++;
                if (count > size * 2) { // Защита от бесконечного цикла
                    return false;
                }
            }

            // Проверка соответствия размера
            return count == size;
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
            return false;
        }
    }
}

// Пример использования
@Test
public void debugCollections() {
    Bag<String> bag = new HashBag<>(Arrays.asList("apple", "banana", "apple", "orange"));
    CollectionDebugger.debugCollection(bag, "Fruit Bag");

    // Benchmark
    CollectionDebugger.benchmarkOperation(bag,
        b -> b.getCount("apple"),
        "Bag.getCount()",
        100000);

    // Validation
    assertTrue(CollectionDebugger.validateCollection(bag));
}
```

### Performance Benchmarking

```java
public class CollectionBenchmark {

    private static final int WARMUP_ITERATIONS = 10000;
    private static final int BENCHMARK_ITERATIONS = 100000;

    @Test
    public void benchmarkBagVsMap() {
        System.out.println("Benchmarking Bag vs Map for counting operations...");

        // Подготовка данных
        List<String> testData = generateTestData(1000);

        // Benchmark Bag
        Bag<String> bag = new HashBag<>();
        benchmark("Bag add/count", () -> {
            bag.clear();
            testData.forEach(bag::add);
            testData.forEach(item -> bag.getCount(item));
        });

        // Benchmark Map
        Map<String, Integer> map = new HashMap<>();
        benchmark("Map put/get", () -> {
            map.clear();
            testData.forEach(item -> map.put(item, map.getOrDefault(item, 0) + 1));
            testData.forEach(map::get);
        });
    }

    private List<String> generateTestData(int size) {
        List<String> data = new ArrayList<>();
        Random random = new Random(42); // Фиксированный seed для повторяемости
        for (int i = 0; i < size; i++) {
            data.add("item" + random.nextInt(100));
        }
        return data;
    }

    private void benchmark(String operationName, Runnable operation) {
        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            operation.run();
        }

        // Benchmark
        long startTime = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            operation.run();
        }
        long endTime = System.nanoTime();

        double avgTime = (endTime - startTime) / (double) BENCHMARK_ITERATIONS / 1_000; // в микросекундах
        System.out.println(String.format("%s: %.2f μs per operation", operationName, avgTime));
    }
}
```

## Миграция и обновление

### Миграция с commons-collections 3.x

**Основные изменения:**
```java
// В версии 3.x
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

// В версии 4.x
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

// Изменения в API
// 3.x
Bag bag = new HashBag();
bag.add("item", 2);

// 4.x - аналогично, но с улучшенной типизацией
Bag<String> bag = new HashBag<>();
bag.add("item", 2);
```

**Миграционный скрипт:**
```java
public class MigrationHelper {

    // Преобразование старых коллекций в новые
    public static <T> Bag<T> migrateToV4(org.apache.commons.collections.Bag oldBag) {
        Bag<T> newBag = new HashBag<>();
        // Копирование элементов...
        return newBag;
    }

    // Обновление ссылок на классы
    public static void updateImports(Path sourceFile) throws IOException {
        List<String> lines = Files.readAllLines(sourceFile);
        List<String> updatedLines = lines.stream()
            .map(line -> line.replace(
                "org.apache.commons.collections",
                "org.apache.commons.collections4"))
            .collect(Collectors.toMap(
                item -> item,
                item -> (double) bag.getCount(item) / total
            ));
    }
}
```

### Обновление до последней версии

**Проверка совместимости:**
```xml
<!-- Maven Enforcer Plugin для проверки версий -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>enforce-versions</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <requireJavaVersion>
                        <version>1.8.0</version>
                    </requireJavaVersion>
                    <bannedDependencies>
                        <excludes>
                            <exclude>org.apache.commons:commons-collections:3.*</exclude>
                        </excludes>
                    </bannedDependencies>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Compatibility Matrix

| Commons Collections | Java Version | Spring Framework | Status |
|---------------------|--------------|------------------|--------|
| 3.2.2 | 1.3+ | 1.x - 4.x | Legacy |
| 4.0 | 6+ | 3.x+ | Stable |
| 4.1 | 6+ | 3.x+ | Stable |
| 4.2 | 6+ | 3.x+ | Stable |
| 4.3 | 7+ | 4.x+ | Stable |
| 4.4 | 8+ | 5.x+ | Current |

## Заключение

Apache Commons Collections — это мощная библиотека, которая значительно расширяет возможности стандартного Java Collections Framework. Она предоставляет разработчикам эффективные и удобные инструменты для работы с коллекциями.

### Ключевые преимущества:

1. **Дополнительные структуры данных**: Bag, BidiMap, OrderedMap, CircularFifoQueue
2. **Удобные утилиты**: CollectionUtils, MapUtils, SetUtils для сложных операций
3. **Декораторы**: synchronized, unmodifiable, predicated коллекции
4. **Высокая производительность**: Оптимизированные реализации
5. **Безопасность**: Защита от null pointer exceptions и других ошибок

### Типичные паттерны использования:

- **Частотный анализ**: Bag для подсчета элементов
- **Кэширование**: LRUMap для ограниченного кэша
- **Двунаправленный поиск**: BidiMap для поиска по ключу и значению
- **Ограниченные буферы**: CircularFifoQueue для логов и очередей
- **Валидация**: Predicated collections для автоматической проверки

### Best practices:

- **Выбирай правильную коллекцию** для конкретной задачи
- **Используй декораторы** для добавления поведения
- **Пиши тесты** для сложных операций с коллекциями
- **Мониторь производительность** и выбирай оптимальные структуры
- **Обновляйся** до последних версий для новых возможностей

### Производительность:

| Операция | ArrayList | LinkedList | HashBag | DualHashBidiMap |
|----------|-----------|------------|---------|-----------------|
| Add | O(1) | O(1) | O(1) | O(1) |
| Get | O(1) | O(n) | O(1) | O(1) |
| Contains | O(n) | O(n) | O(1) | O(1) |
| Remove | O(n) | O(n) | O(1) | O(1) |
| Count (Bag) | - | - | O(1) | - |

### Когда использовать Apache Commons Collections:

1. **Enterprise приложения** с высокими требованиями к производительности
2. **Legacy код** требующий дополнительных структур данных
3. **DSL разработка** для специфических доменов
4. **Big Data обработки** где нужны эффективные коллекции
5. **Требуется thread-safety** без external synchronization

Библиотека Apache Commons Collections доказала свою ценность в тысячах проектов и продолжает развиваться, добавляя новые возможности и улучшая производительность. Освоение этой библиотеки значительно расширяет арсенал инструментов Java разработчика.
