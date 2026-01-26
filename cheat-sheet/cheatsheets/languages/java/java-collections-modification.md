# Java Collections - Модификация



**Дата последнего обновления:** 2026-01-11

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/javase/)
- [Java API Documentation](https://docs.oracle.com/javase/8/docs/api/)

### Baeldung

- [Java Tutorials](https://www.baeldung.com/java-tutorial)


## Содержание

- Сбор элементов Stream в List
- Разница между Collection.stream().forEach() и Collection.forEach()
- Null-Safe Stream из коллекций
- Сортировка Array, List, Set и Map
- Перетасовка коллекций
- Сведение вложенных коллекций
- Объединение нескольких коллекций
- Поиск максимального/минимального значения коллекции
- Руководство по Iterator
- Удаление элементов из коллекций

## Сбор элементов Stream в List

Получение списка из потока - наиболее часто используемая терминальная операция конвейера потока. До **Java 16** мы использовали метод **Stream.collect()** и передавали его коллектору в качестве аргумента для сбора элементов. Сам коллектор был создан путем вызова метода **Collectors.toList().**

Однако были запросы на изменение метода для получения списка непосредственно из экземпляра **Stream.** После выпуска **Java 16** теперь мы можем вызывать **toList(),** новый метод непосредственно в **Stream,** чтобы получить **List.** Такие библиотеки, как **StreamEx,** также предоставляют удобный способ получить список непосредственно из потока**.

Мы можем накапливать элементы **Stream** в список, используя:

### Java 16+

```java
List<Integer> list = stream.toList();
```

Этот метод возвращает неизменяемый список, поэтому мы не можем добавлять, удалять или заменять элементы после его создания**.

### До Java 16

```java
List<Integer> list = stream.collect(Collectors.toList());
```

Мы также можем использовать другие коллекторы для получения конкретных реализаций списков:

```java
List<Integer> list = stream.collect(Collectors.toCollection(ArrayList::new));
```

Для получения неизменяемого списка можем использовать:

```java
List<Integer> list = stream.collect(Collectors.collectingAndThen(
    Collectors.toList(),
    Collections::unmodifiableList
));
```

## Разница между Collection.stream().forEach() и Collection.forEach()

В этой статье мы рассмотрим различия между **Collection.forEach()** и **Collection.stream().forEach().**

### Collection.forEach()

**Collection.forEach()** использует итератор коллекции**(если он указан)**. Это означает, что порядок обработки элементов определен. В большинстве случаев порядок, в котором мы выбираем элементы, не имеет значения**.

Потокобезопасность: Если мы модифицируем коллекцию во время выполнения **forEach(), мы получим исключение **ConcurrentModificationException.**

```java
List<String> list = Arrays.asList("A", "B", "C", "D");
list.forEach(e -> {
    System.out.println(e);
});
```

### Collection.stream().forEach()

**Collection.stream().forEach()** также обрабатывает элементы по порядку, но мы можем обрабатывать поток параллельно, если мы явно создадим параллельный поток**.

Важно отметить, что порядок выполнения не гарантируется, даже если мы используем поток последовательно**.

```java
List<String> list = Arrays.asList("A", "B", "C", "D");
list.stream().forEach(e -> {
    System.out.println(e);
});
```

### Модификация во время итерации

Мы можем изменить элемент во время итерации по списку:

```java
list.forEach(e -> {
    list.set(3, "E");
});
```

Но хотя нет проблем с выполнением этого с помощью **Collection.forEach()** или **stream().forEach(), Java** требует, чтобы операция над потоком не мешала. Это означает, что элементы не должны изменяться во время выполнения потокового конвейера**.

Причина этого в том, что поток должен способствовать параллельному выполнению. Здесь изменение элементов потока может привести к неожиданному поведению**.

## Null-Safe Stream из коллекций

В этом руководстве мы узнаем, как создавать **Null-Safe** **Stream** из коллекций **Java.**

Прежде чем мы начнем, есть одна зависимость **Maven,** которая нам понадобится для определенных сценариев:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.2</version>
</dependency>
```

Основной подход к созданию потока из коллекции любого типа заключается в вызове методов **stream()** или **parallelStream()** для коллекции, в зависимости от требуемого типа потока:

```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();
```

Однако, если коллекция равна **null, вызов метода **stream()** приведет к исключению **NullPointerException**.

Чтобы избежать этого, мы можем добавить проверку на **null** перед вызовом метода **stream():**

```java
Stream<String> stream = collection != null ? collection.stream() : Stream.empty();
```

Или использовать вспомогательный метод:

```java
public <T> Stream<T> collectionAsStream(Collection<T> collection) {
    return collection == null ? Stream.empty() : collection.stream();
}
```

### Использование Apache Commons Collections

Мы также можем использовать **CollectionUtils** из **Apache Commons Collections** для безопасной работы с **null** коллекциями:

```java
Stream<String> stream = CollectionUtils.emptyIfNull(collection).stream();
```

## Сортировка Array, List, Set и Map

В этом разделе мы рассмотрим различные способы сортировки коллекций в **Java.**

### Сортировка Array

Для массивов примитивов и объектов мы можем использовать **Arrays.sort()**:

```java
int[] array = {5, 4, 3, 2, 1};
Arrays.sort(array);
```

Для объектов можем использовать компаратор:

```java
String[] array = {"c", "b", "a"};
Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
```

### Сортировка List

Для списков можем использовать **Collections.sort()**:

```java
List<String> list = Arrays.asList("c", "b", "a");
Collections.sort(list);
```

Или с **Java 8+** можем использовать **Stream API**:

```java
List<String> sorted = list.stream()
    .sorted()
    .collect(Collectors.toList());
```

### Сортировка Set

Поскольку **Set** не гарантирует порядок, для сортировки мы обычно преобразуем его в **List** или используем **TreeSet**:

```java
Set<String> set = new HashSet<>(Arrays.asList("c", "b", "a"));
List<String> sorted = set.stream()
    .sorted()
    .collect(Collectors.toList());
```

Или создаем **TreeSet**:

```java
Set<String> sortedSet = new TreeSet<>(set);
```

### Сортировка Map

Для сортировки **Map** по ключам или значениям, мы можем использовать **Stream API**:

```java
Map<String, Integer> map = new HashMap<>();
// Сортировка по ключам
Map<String, Integer> sortedByKey = map.entrySet().stream()
    .sorted(Map.Entry.comparingByKey())
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (oldValue, newValue) -> oldValue,
        LinkedHashMap::new
    ));

// Сортировка по значениям
Map<String, Integer> sortedByValue = map.entrySet().stream()
    .sorted(Map.Entry.comparingByValue())
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (oldValue, newValue) -> oldValue,
        LinkedHashMap::new
    ));
```

## Перетасовка коллекций

Перетасовка коллекции означает случайное изменение порядка элементов**. Collections.shuffle()** используется для этой цели**.

```java
List<String> list = Arrays.asList("a", "b", "c", "d", "e");
Collections.shuffle(list);
```

Мы также можем передать объект **Random** для управления случайностью:

```java
Random random = new Random();
Collections.shuffle(list, random);
```

## Сведение вложенных коллекций

Сведение вложенных коллекций означает преобразование коллекции коллекций в одну плоскую коллекцию**. Stream API** предоставляет метод **flatMap()** для этого**:

```java
List<List<String>> nestedList = Arrays.asList(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d")
);

List<String> flatList = nestedList.stream()
    .flatMap(Collection::stream)
    .collect(Collectors.toList());
// Результат: [a, b, c, d]
```

## Объединение нескольких коллекций

Для объединения нескольких коллекций мы можем использовать несколько подходов**:

### Использование addAll()

```java
List<String> list1 = Arrays.asList("a", "b");
List<String> list2 = Arrays.asList("c", "d");
List<String> combined = new ArrayList<>();
combined.addAll(list1);
combined.addAll(list2);
```

### Использование Stream API

```java
List<String> combined = Stream.concat(list1.stream(), list2.stream())
    .collect(Collectors.toList());
```

### Использование flatMap()

```java
List<String> combined = Stream.of(list1, list2)
    .flatMap(Collection::stream)
    .collect(Collectors.toList());
```

## Поиск максимального/минимального значения коллекции

Для поиска максимального и минимального значения в коллекции мы можем использовать несколько подходов**:

### Использование Collections.max() и Collections.min()

```java
List<Integer> list = Arrays.asList(1, 5, 3, 7, 2);
Integer max = Collections.max(list);
Integer min = Collections.min(list);
```

### Использование Stream API

```java
Optional<Integer> max = list.stream().max(Integer::compareTo);
Optional<Integer> min = list.stream().min(Integer::compareTo);
```

С кастомным компаратором:

```java
Optional<String> maxLength = list.stream()
    .max(Comparator.comparing(String::length));
```

## Руководство по Iterator

Итератор — это интерфейс, который позволяет перебирать элементы коллекции**. Iterator** предоставляет следующие методы**:

- **hasNext()** — возвращает **true,** если есть следующий элемент
- **next()** — возвращает следующий элемент
- **remove()** — удаляет текущий элемент (опционально)

```java
List<String> list = Arrays.asList("a", "b", "c");
Iterator<String> iterator = list.iterator();

while (iterator.hasNext()) {
    String element = iterator.next();
    System.out.println(element);
}
```

### ListIterator

**ListIterator** расширяет **Iterator** и позволяет перемещаться в обоих направлениях**:

```java
List<String> list = Arrays.asList("a", "b", "c");
ListIterator<String> listIterator = list.listIterator();

while (listIterator.hasNext()) {
    String next = listIterator.next();
    System.out.println(next);
}

while (listIterator.hasPrevious()) {
    String previous = listIterator.previous();
    System.out.println(previous);
}
```

В этом примере мы начинаем с получения **ListIterator** из списка, затем мы можем получить следующий элемент либо по индексу, который не увеличивает внутренний текущий элемент итератора, либо с помощью вызова **next.**

Затем мы можем заменить конкретный элемент на **set** и вставить новый с помощью **add.**

Достигнув конца итерации, мы можем вернуться назад, чтобы изменить дополнительные элементы или просто распечатать их снизу вверх**.

## Удаление элементов из коллекций

В этом кратком руководстве мы поговорим о четырех различных способах удаления элементов из коллекций **Java,** соответствующих определенным предикатам**.

Естественно, мы также рассмотрим некоторые предостережения**.

### Использование Iterator.remove()

Самый безопасный способ удаления элементов во время итерации**:

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
Iterator<String> iterator = list.iterator();

while (iterator.hasNext()) {
    String element = iterator.next();
    if (element.equals("b")) {
        iterator.remove();
    }
}
```

### Использование Collection.removeIf() (Java 8+)

Более элегантный способ с **Java 8+**:

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
list.removeIf(element -> element.equals("b"));
```

### Использование Stream API

Если нам нужно создать новую коллекцию без определенных элементов**:

```java
List<String> list = Arrays.asList("a", "b", "c", "d");
List<String> filtered = list.stream()
    .filter(element -> !element.equals("b"))
    .collect(Collectors.toList());
```

### Обратное удаление

Для удаления элементов из списка во время итерации можно итерировать в обратном порядке**:

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("b")) {
        list.remove(i);
    }
}
```

Этот подход эффективен, так как не вызывает проблем с индексацией при удалении элементов**.
