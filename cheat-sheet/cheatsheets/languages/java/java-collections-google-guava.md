# Java Collections - Google Guava



**Дата последнего обновления:** 2026-01-11

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/javase/)
- [Java API Documentation](https://docs.oracle.com/javase/8/docs/api/)

### Baeldung

- [Java Tutorials](https://www.baeldung.com/java-tutorial)


## Содержание

- Руководство по Multimap
- Объединение и разделение Collection
- Руководство по Table

## Руководство по Multimap

В этой статье мы рассмотрим одну из реализаций **Map** из библиотеки **Google** **Guava -** **Multimap.** Это коллекция, которая сопоставляет ключи со значениями, подобно java.util.Map, но в которой каждый ключ может быть связан с несколькими значениями.

Сначала добавим зависимость:

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

В случае с **Guava Multimap,** если мы добавим два значения для одного и того же ключа, второе значение не переопределит первое значение. Вместо этого у нас будет два значения в результирующей карте. Давайте посмотрим на тестовый пример:

```java
String key = "a-key";
Multimap<String, String> map = ArrayListMultimap.create();

map.put(key, "firstValue");
map.put(key, "secondValue");

assertEquals(2, map.size());
```

### Реализации Multimap

**Guava** предоставляет несколько реализаций **Multimap**:

1. **ArrayListMultimap** — использует **HashMap,** поддерживаемый **ArrayList,** для каждого значения.
2. **HashMultimap** — использует **HashMap** с **HashSet** для значений (не допускает дубликатов)
3. **LinkedListMultimap** — сохраняет порядок вставки ключей и значений
4. **LinkedHashMultimap** — сохраняет порядок вставки и не допускает дубликаты значений
5. **TreeMultimap** — использует **TreeMap** и **TreeSet** (отсортированные)
6. **ImmutableListMultimap** — неизменяемая реализация с **List** для значений
7. **ImmutableSetMultimap** — неизменяемая реализация с **Set** для значений

### Преимущества Multimap

1.  Нет необходимости заполнять пустую коллекцию перед добавлением записи с помощью put().
2.  Метод get() никогда не возвращает null, а только пустую коллекцию (нам не нужно проверять значение null, как в контрольном примере `Map<String, Collection<V>>`)
3.  Ключ содержится в Multimap тогда и только тогда, когда он соответствует хотя бы одному значению. Любая операция, которая приводит к тому, что ключ имеет нулевые связанные значения, приводит к удалению этого ключа из Multimap (в `Map<String, Collection<V>>`, даже если мы удаляем все значения из коллекции, мы по-прежнему сохраняем пустую коллекцию как значение, и это ненужные накладные расходы памяти)
4.  Общее количество входных значений доступно как size()

### Примеры использования

```java
// Создание Multimap
Multimap<String, String> multimap = ArrayListMultimap.create();

// Добавление значений
multimap.put("key1", "value1");
multimap.put("key1", "value2");
multimap.put("key2", "value3");

// Получение всех значений для ключа
Collection<String> values = multimap.get("key1");
// Результат: [value1, value2]

// Проверка наличия ключа
boolean hasKey = multimap.containsKey("key1"); // true

// Проверка наличия пары ключ-значение
boolean hasEntry = multimap.containsEntry("key1", "value1"); // true

// Получение всех ключей
Set<String> keys = multimap.keySet();

// Получение всех значений
Collection<String> allValues = multimap.values();

// Удаление конкретной пары ключ-значение
multimap.remove("key1", "value1");

// Удаление всех значений для ключа
multimap.removeAll("key1");

// Замена всех значений для ключа
multimap.replaceValues("key1", Arrays.asList("newValue1", "newValue2"));
```

### Неизменяемые реализации

Как всегда, мы должны предпочесть неизменяемые реализации интерфейса **Multimap**:

```java
// ImmutableListMultimap
ImmutableListMultimap<String, String> immutableListMultimap = 
    ImmutableListMultimap.of("key1", "value1", "key1", "value2");

// ImmutableSetMultimap
ImmutableSetMultimap<String, String> immutableSetMultimap = 
    ImmutableSetMultimap.of("key1", "value1", "key1", "value2");
```

## Объединение и разделение Collection

В этом уроке мы узнаем, как использовать **Joiner** и **Splitter** в библиотеке **Guava.** Мы преобразуем коллекции в **String** с помощью **Joiner** и разделим **String** на коллекцию с помощью **Splitter.**

### Joiner

Давайте начнем с простого примера объединения списка в строку с помощью **Joiner.** В следующем примере мы объединяем список имен в одну строку, используя запятую **«, »** в качестве разделителя:

```java
@Test
public void whenConvertListToString_thenConverted() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    String result = Joiner.on(",").join(names);
    assertEquals(result, "John,Jane,Adam,Tom");
}
```

**Joiner** также поддерживает обработку null значений:

```java
// Пропустить null значения
List<String> names = Lists.newArrayList("John", null, "Jane", "Adam", null, "Tom");
String result = Joiner.on(",").skipNulls().join(names);
// Результат: "John,Jane,Adam,Tom"

// Заменить null значения
String result2 = Joiner.on(",").useForNull("nameless").join(names);
// Результат: "John,nameless,Jane,Adam,nameless,Tom"
```

**Joiner** также может работать с Map:

```java
@Test
public void whenConvertMapToString_thenConverted() {
    Map<String, Integer> salary = Maps.newHashMap();
    salary.put("John", 1000);
    salary.put("Jane", 1500);
    String result = Joiner.on(" , ").withKeyValueSeparator(" = ").join(salary);
    // Результат: "John = 1000 , Jane = 1500"
}
```

**Joiner** можно использовать для добавления в существующий StringBuilder:

```java
StringBuilder stringBuilder = new StringBuilder();
Joiner.on(",").appendTo(stringBuilder, "John", "Jane", "Adam", "Tom");
// stringBuilder теперь содержит: "John,Jane,Adam,Tom"
```

### Splitter

Теперь давайте посмотрим, как разделить строку на список с помощью **Splitter**:

```java
@Test
public void whenCreateListFromString_thenCreated() {
    String input = "circle - square - triangle";
    List<String> result = Splitter.on(" - ").trimResults().splitToList(input);
    assertEquals(Lists.newArrayList("circle", "square", "triangle"), result);
}
```

**Splitter** поддерживает множество опций:

```java
// Разделение с ограничением количества результатов
String input = "apple.banana.orange.grape";
List<String> result = Splitter.on(".")
    .limit(3)
    .splitToList(input);
// Результат: ["apple", "banana", "orange.grape"]

// Удаление пустых строк
String input2 = "apple,,banana,,orange";
List<String> result2 = Splitter.on(",")
    .omitEmptyStrings()
    .splitToList(input2);
// Результат: ["apple", "banana", "orange"]

// Обрезка результатов
String input3 = " apple , banana , orange ";
List<String> result3 = Splitter.on(",")
    .trimResults()
    .splitToList(input3);
// Результат: ["apple", "banana", "orange"]

// Обрезка с указанием символов
List<String> result4 = Splitter.on(",")
    .trimResults(CharMatcher.is('*'))
    .splitToList("*apple*,*banana*,*orange*");
// Результат: ["apple", "banana", "orange"]

// Разделение на Map
String input4 = "John=first,Adam=second";
Map<String, String> result5 = Splitter.on(",")
    .withKeyValueSeparator("=")
    .split(input4);
// Результат: {John=first, Adam=second}
```

**Splitter** также поддерживает регулярные выражения:

```java
String input = "apple.banana-orange+grape";
List<String> result = Splitter.onPattern("[.-+]")
    .splitToList(input);
// Результат: ["apple", "banana", "orange", "grape"]
```

### Использование с фиксированной длиной

**Splitter** может разделять строку на части фиксированной длины:

```java
String input = "HelloWorld";
List<String> result = Splitter.fixedLength(3).splitToList(input);
// Результат: ["Hel", "loW", "orl", "d"]
```

## Руководство по Table

В этом руководстве мы покажем, как использовать интерфейс **Table Google Guava** и его многочисленные реализации**.

**Table Guava -** это коллекция, которая представляет собой таблицу, содержащую строки, столбцы и связанные значения ячеек. Строка и столбец действуют как упорядоченная пара ключей**.

Давайте посмотрим, как использовать класс **Table.**

Начнем с добавления зависимости библиотеки **Google** **Guava** в **pom.xml:**

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Если бы мы должны были представить Table Guava с использованием коллекций, присутствующих в ядре Java, то структура была бы картой строк, где каждая строка содержит карту столбцов со связанными значениями ячеек.

Таблица представляет собой специальную карту, в которой два ключа могут быть указаны в комбинированном виде для ссылки на одно значение.

Это похоже на создание карты карт, например, `Map<UniversityName, Map<CoursesOffered, SeatAvailable>>`. Стол также был бы идеальным способом представления игрового поля Battleships.

### Создание Table

Вы можете создать экземпляр таблицы несколькими способами:

```java
// HashBasedTable - использует HashMap для строк и столбцов
Table<String, String, Integer> hashBasedTable = HashBasedTable.create();

// TreeBasedTable - использует TreeMap (отсортированные)
Table<String, String, Integer> treeBasedTable = TreeBasedTable.create();

// ImmutableTable - неизменяемая реализация
Table<String, String, Integer> immutableTable = ImmutableTable.<String, String, Integer>builder()
    .put("row1", "col1", 1)
    .put("row1", "col2", 2)
    .put("row2", "col1", 3)
    .put("row2", "col2", 4)
    .build();

// ArrayTable - использует массив (размер фиксирован при создании)
List<String> rows = Arrays.asList("row1", "row2");
List<String> cols = Arrays.asList("col1", "col2");
Table<String, String, Integer> arrayTable = ArrayTable.create(rows, cols);
```

### Основные операции

```java
Table<String, String, Integer> table = HashBasedTable.create();

// Добавление значений
table.put("row1", "col1", 1);
table.put("row1", "col2", 2);
table.put("row2", "col1", 3);
table.put("row2", "col2", 4);

// Получение значения
Integer value = table.get("row1", "col1"); // 1

// Проверка наличия
boolean contains = table.contains("row1", "col1"); // true
boolean containsRow = table.containsRow("row1"); // true
boolean containsColumn = table.containsColumn("col1"); // true
boolean containsValue = table.containsValue(1); // true

// Получение всех значений для строки
Map<String, Integer> row = table.row("row1");
// Результат: {col1=1, col2=2}

// Получение всех значений для столбца
Map<String, Integer> column = table.column("col1");
// Результат: {row1=1, row2=3}

// Получение всех строк
Set<String> rowKeys = table.rowKeySet();
// Результат: [row1, row2]

// Получение всех столбцов
Set<String> columnKeys = table.columnKeySet();
// Результат: [col1, col2]

// Получение всех значений
Collection<Integer> values = table.values();
// Результат: [1, 2, 3, 4]

// Удаление значения
Integer removed = table.remove("row1", "col1"); // возвращает 1

// Очистка таблицы
table.clear();

// Получение размера
int size = table.size(); // количество ячеек
```

### Работа с представлениями

**Table** предоставляет различные представления данных:

```java
// Получение карты всех строк
Map<String, Map<String, Integer>> rowMap = table.rowMap();
// Результат: {row1={col1=1, col2=2}, row2={col1=3, col2=4}}

// Получение карты всех столбцов
Map<String, Map<String, Integer>> columnMap = table.columnMap();
// Результат: {col1={row1=1, row2=3}, col2={row1=2, row2=4}}

// Получение всех пар (строка, столбец, значение)
Set<Table.Cell<String, String, Integer>> cells = table.cellSet();
for (Table.Cell<String, String, Integer> cell : cells) {
    String rowKey = cell.getRowKey();
    String columnKey = cell.getColumnKey();
    Integer value = cell.getValue();
}
```

### Пример использования: Университет и курсы

```java
Table<String, String, Integer> universityCourseSeats = HashBasedTable.create();

universityCourseSeats.put("MIT", "Computer Science", 50);
universityCourseSeats.put("MIT", "Mathematics", 30);
universityCourseSeats.put("Harvard", "Computer Science", 45);
universityCourseSeats.put("Harvard", "Mathematics", 35);

// Получить количество мест на курс Computer Science в MIT
Integer seats = universityCourseSeats.get("MIT", "Computer Science"); // 50

// Получить все курсы в MIT
Map<String, Integer> mitCourses = universityCourseSeats.row("MIT");
// Результат: {Computer Science=50, Mathematics=30}

// Получить все университеты, предлагающие Computer Science
Map<String, Integer> csUniversities = universityCourseSeats.column("Computer Science");
// Результат: {MIT=50, Harvard=45}
```

### Сравнение реализаций

1. **HashBasedTable** — использует HashMap для строк и столбцов, самый быстрый для общего использования
2. **TreeBasedTable** — использует TreeMap, поддерживает сортировку строк и столбцов
3. **ImmutableTable** — неизменяемая реализация, безопасна для многопоточности
4. **ArrayTable** — использует массивы, требует фиксированного размера при создании, самый быстрый по доступу, но самый медленный по созданию

### Дополнительные операции

```java
// Замена значения (если существует)
table.put("row1", "col1", 10);

// Проверка на пустоту
boolean isEmpty = table.isEmpty();

// Копирование таблицы
Table<String, String, Integer> copy = HashBasedTable.create(table);
```
