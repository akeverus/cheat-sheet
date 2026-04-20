---
title: "Java Collections: конвертирование"
description: "Материал по теме Java Collections: конвертирование в разделе cheatsheets."
tags:
  - languages
  - java
  - java-collections-converting
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Java Collections: конвертирование

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)

### Обучающие материалы

- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)


## Содержание

- [Преобразование Array в List](#преобразование-array-в-list)
  - [Преобразование List в Array](#преобразование-list-в-array)
- [Преобразование Array в Set](#преобразование-array-в-set)
  - [Преобразование Set в Array](#преобразование-set-в-array)
- [Преобразование Array, List или Set в Map](#преобразование-array-list-или-set-в-map)
  - [Преобразование Map в Array](#преобразование-map-в-array)
  - [Преобразование Map в List](#преобразование-map-в-list)
  - [Преобразование Map в Set](#преобразование-map-в-set)
- [Преобразование List в Map](#преобразование-list-в-map)
  - [Преобразование до Java 8](#преобразование-до-java-8)
  - [Преобразование с Java 8](#преобразование-с-java-8)
  - [Обработка дубликатов ключей](#обработка-дубликатов-ключей)
  - [Преобразование в конкретную реализацию Map](#преобразование-в-конкретную-реализацию-map)
- [Преобразование Map в String](#преобразование-map-в-string)
  - [Использование StringJoiner (Java 8+)](#использование-stringjoiner-java-8)
  - [Использование Stream API](#использование-stream-api)
  - [Преобразование String в Map](#преобразование-string-в-map)
- [Arrays.asList против нового ArrayList<>(Arrays.asList())](#arraysaslist-против-нового-arraylistarraysaslist)
  - [Arrays.asList()](#arraysaslist)
  - [new ArrayList<>(Arrays.asList())](#new-arraylistarraysaslist)
  - [Когда использовать каждый подход](#когда-использовать-каждый-подход)
- [Лучшие практики](#лучшие-практики)

## Преобразование Array в List

В этом кратком руководстве мы узнаем, как преобразовывать массив в список, используя основные библиотеки **Java**, коллекции **Guava** и **Apache Commons**.

### Преобразование List в Array

Начнем с преобразования списка в массив с использованием простой **Java:**

```java
@Test
public void givenUsingCoreJava_whenListConvertedToArray_thenCorrect() {
    List<Integer> sourceList = Arrays.asList(0, 1, 2, 3, 4, 5);
    Integer[] targetArray = sourceList.toArray(new Integer[0]);
}
```

Обратите внимание, что для нас предпочтительным способом использования метода является **toArray (new T\[0\])** по сравнению с **toArray (new T\[size\])**. Как доказывает Алексей Шипилев в своем блоге, это кажется быстрее, безопаснее и чище.

**Теперь давайте воспользуемся **API Guava** для того же преобразования:**

```java
@Test
public void givenUsingGuava_whenListConvertedToArray_thenCorrect() {
    List<Integer> sourceList = Lists.newArrayList(0, 1, 2, 3, 4, 5);
    int[] targetArray = Ints.toArray(sourceList);
}
```

### Преобразование Array в List

**Начнём с простого **Java-**решения для преобразования массива в список:**

```java
@Test
public void givenUsingCoreJava_whenArrayConvertedToList_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    List<Integer> targetList = Arrays.asList(sourceArray);
}
```

**Обратите внимание, что это список фиксированного размера, который по-прежнему будет поддерживаться массивом. Если нам нужен стандартный **ArrayList,** мы можем просто создать его экземпляр:**

```java
List<Integer> targetList = new ArrayList<Integer>(Arrays.asList(sourceArray));
```

**Теперь давайте воспользуемся **API Guava** для того же преобразования:**

```java
@Test
public void givenUsingGuava_whenArrayConvertedToList_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    List<Integer> targetList = Lists.newArrayList(sourceArray);
}
```

**Наконец, давайте воспользуемся **API `CollectionUtils`.addAll** из **Apache `Commons` Collections**, чтобы заполнить элементы массива в пустом списке:**

```java
@Test
public void givenUsingCommonsCollections_whenArrayConvertedToList_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    List<Integer> targetList = new ArrayList<>(6);
    CollectionUtils.addAll(targetList, sourceArray);
}
```

## Преобразование Array в Set

В этой короткой статье мы рассмотрим преобразование между массивом и набором — сначала с использованием простой **Java**, затем **Guava** и библиотеки **Commons Collections** от **Apache**.

### Преобразование Array в Set

Давайте сначала посмотрим, как превратить массив в **Set,** используя простую **Java:**

```java
@Test
public void givenUsingCoreJavaV1_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    Set<Integer> targetSet = new HashSet<Integer>(Arrays.asList(sourceArray));
}
```

**В качестве альтернативы можно сначала создать **Set,** а затем заполнить его элементами массива:**

```java
@Test
public void givenUsingCoreJavaV2_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    Set<Integer> targetSet = new HashSet<Integer>();
    Collections.addAll(targetSet, sourceArray);
}
```

Далее давайте посмотрим на преобразование **Guava** из массива в **Set:**

```java
@Test
public void givenUsingGuava_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    Set<Integer> targetSet = Sets.newHashSet(sourceArray);
}
```

Наконец, давайте сделаем преобразование, используя библиотеку **Commons Collection** от **Apache:**

```java
@Test
public void givenUsingCommonsCollections_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = {0, 1, 2, 3, 4, 5};
    Set<Integer> targetSet = new HashSet<>(6);
    CollectionUtils.addAll(targetSet, sourceArray);
}
```

### Преобразование Set в Array

**Теперь давайте посмотрим на обратное — преобразование существующего **Set** в массив:**

```java
@Test
public void givenUsingCoreJava_whenSetConvertedToArray_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    Integer[] targetArray = sourceSet.toArray(new Integer[0]);
}
```

Обратите внимание, что **toArray (new T\[0\])** является предпочтительным способом использования метода по сравнению с **toArray (new T\[size\])**. Как доказывает Алексей Шипилев в своем блоге, это кажется быстрее, безопаснее и чище.

**Далее -** решение **Guava:**

```java
@Test
public void givenUsingGuava_whenSetConvertedToArray_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    int[] targetArray = Ints.toArray(sourceSet);
}
```

Обратите внимание, что мы используем **Ints API** от **Guava**, поэтому это решение зависит от типа данных, с которым мы работаем.

## Преобразование Array, List или Set в Map

Эта короткая статья покажет, как преобразовать значения карты в массив, список или набор, используя простую **Java**, а также быстрый пример на основе **Guava**.

### Преобразование Map в Array

Во-первых, давайте посмотрим на преобразование значений карты в массив с помощью простого **java:**

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToArray_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();
    Collection<String> values = sourceMap.values();
    String[] targetArray = values.toArray(new String[0]);
}
```

Обратите внимание, что **toArray (new T\[0\])** является предпочтительным способом использования метода по сравнению с **toArray (new T\[size\])**. Как доказывает Алексей Шипилев в своем блоге, это кажется быстрее, безопаснее и чище.

### Преобразование Map в List

Затем давайте преобразуем значения карты в список, используя обычную **Java:**

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToList_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();
    List<String> targetList = new ArrayList<>(sourceMap.values());
}
```

**И используя **Guava**:**

```java
@Test
public void givenUsingGuava_whenMapValuesConvertedToList_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();
    List<String> targetList = Lists.newArrayList(sourceMap.values());
}
```

### Преобразование Map в Set

Наконец, давайте преобразуем значения карты в набор, используя простой **java:**

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToSet_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();
    Set<String> targetSet = new HashSet<>(sourceMap.values());
}
```

## Преобразование List в Map

Преобразование списка в карту — обычная задача. В этом уроке мы рассмотрим несколько способов сделать это.

Предположим, что каждый элемент списка имеет идентификатор, который будет использоваться в качестве ключа в результирующей карте.

**Сначала мы смоделируем элемент:**

```java
public class Animal {
    private int id;
    private String name;
    // constructors, getters, setters
}
```

Поле **id** уникально, поэтому мы можем сделать его ключом.

### Преобразование до Java 8

Очевидно, мы можем преобразовать список в карту, используя основные методы **Java:**

```java
public Map<Integer, Animal> convertListBeforeJava8(List<Animal> list) {
    Map<Integer, Animal> map = new HashMap<>();
    for (Animal animal : list) {
        map.put(animal.getId(), animal);
    }
    return map;
}
```

**Теперь тестируем преобразование:**

```java
@Test
public void whenConvertBeforeJava8_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService.convertListBeforeJava8(list);
    assertThat(map.values(), containsInAnyOrder(list.toArray()));
}
```

### Преобразование с Java 8

**Начиная с **Java 8,** мы можем преобразовать список в карту, используя потоки и коллекторы:**

```java
public Map<Integer, Animal> convertListAfterJava8(List<Animal> list) {
    Map<Integer, Animal> map = list.stream()
        .collect(Collectors.toMap(Animal::getId, Function.identity()));
    return map;
}
```

**Опять же, давайте удостоверимся, что преобразование выполнено правильно:**

```java
@Test
public void whenConvertAfterJava8_thenReturnMapWithTheSameElements() {
    Map<Integer, Animal> map = convertListService.convertListAfterJava8(list);
    assertThat(map.values(), containsInAnyOrder(list.toArray()));
}
```

### Обработка дубликатов ключей

Если список содержит дубликаты по полю ключа, нам нужно передать функцию разрешения конфликтов в **Collectors.`toMap()`:**

```java
Map<Integer, Animal> map = list.stream()
    .collect(Collectors.toMap(
        Animal::getId,
        Function.identity(),
        (existing, replacement) -> existing
    ));
```

### Преобразование в конкретную реализацию Map

**Если мы хотим контролировать конкретную реализацию **Map,** которую мы возвращаем, мы можем использовать перегруженную версию метода с поставщиком:**

```java
Map<Integer, Animal> map = list.stream()
    .collect(Collectors.toMap(
        Animal::getId,
        Function.identity(),
        (existing, replacement) -> existing,
        LinkedHashMap::new
    ));
```

## Преобразование Map в String

В этой статье мы рассмотрим различные способы преобразования **Map** в **String** и наоборот.

### Преобразование Map в String

**Самый простой способ преобразовать **Map** в **String** — использовать метод **toString()**:**

```java
Map<Integer, String> map = new HashMap<>();
map.put(1, "one");
map.put(2, "two");
String mapAsString = map.toString();
// Результат: {1=one, 2=two}
```

### Использование StringJoiner (Java 8+)

Для более контролируемого форматирования можно использовать **StringJoiner:**

```java
StringJoiner joiner = new StringJoiner(", ", "{", "}");
map.forEach((key, value) -> joiner.add(key + "=" + value));
String mapAsString = joiner.toString();
```

### Использование Stream API

**Мы также можем использовать **Stream API** для преобразования:**

```java
String mapAsString = map.entrySet().stream()
    .map(entry -> entry.getKey() + "=" + entry.getValue())
    .collect(Collectors.joining(", ", "{", "}"));
```

### Преобразование String в Map

**Для обратного преобразования можно использовать различные подходы. Вот простой пример с использованием регулярных выражений:**

```java
Map<String, String> map = Arrays.stream(str.split(", "))
    .map(s -> s.split("="))
    .collect(Collectors.toMap(
        s -> s[0],
        s -> s[1]
    ));
```

## Arrays.asList против нового ArrayList<>(Arrays.`asList`())

Важное различие между этими двумя подходами заключается в том, что **Arrays.`asList()`** возвращает представление массива, которое является фиксированным размером, тогда как **new `ArrayList`<>(**Arrays.asList**())** создает новый изменяемый список.

### Arrays.asList()

```java
List<String> list1 = Arrays.asList("a", "b", "c");
// list1 имеет фиксированный размер
// list1.add("d"); // вызовет UnsupportedOperationException
// list1.remove(0); // вызовет UnsupportedOperationException
```

### new ArrayList<>(Arrays.`asList`())

```java
List<String> list2 = new ArrayList<>(Arrays.asList("a", "b", "c"));
// list2 является полностью изменяемым
list2.add("d"); // работает корректно
list2.remove(0); // работает корректно
```

### Когда использовать каждый подход

- Используйте **Arrays.`asList()`**, когда вам нужен список фиксированного размера для чтения
- Используйте **new `ArrayList`<>(**Arrays.asList**())**, когда вам нужен полностью изменяемый список, независимый от исходного массива

**Изменения в исходном массиве отражаются в списке, созданном через `Arrays.asList()`, если элементы — объекты (не примитивы):**

```java
String[] array = {"a", "b", "c"};
List<String> list = Arrays.asList(array);
array[0] = "z";
// list.get(0) теперь тоже будет "z"
```

**Однако если мы используем **new `ArrayList`<>(**Arrays.asList**())**, изменения в исходном массиве не будут отражаться в списке, так как создаётся новый независимый список:**

```java
String[] array = {"a", "b", "c"};
List<String> list = new ArrayList<>(Arrays.asList(array));
array[0] = "z";
// list.get(0) все еще будет "a"
```

## Лучшие практики

- **Массив → `List`:** для неизменяемого представления — `Arrays.asList()` (изменения массива видны в списке); для независимой копии — `new ArrayList<>(Arrays.`asList`())` или `List.of(array)` (Java 9+, неизменяемый).
- **List/Set → Map:** использовать `Collectors.toMap()` с обработкой дубликатов ключей (merge function); для **null**-значений — явная проверка или `map(key, `Optional`.`ofNullable`(value))`.
- **Конвертирование примитивов:** для **int**[] → **List**<**Integer**> — **Stream API** или **Guava**; избегать ручных циклов там, где есть идиоматический способ.
- **Не мутировать через представление:** после `Arrays.`asList`(array)` не добавлять/удалять элементы; при необходимости копии — создавать новую коллекцию.

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
- [[java-collections-modification|Java Collections: модификация]]
