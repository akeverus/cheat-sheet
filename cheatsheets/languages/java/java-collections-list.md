---
title: "Java Collections: List"
description: "Руководство по интерфейсу List и его реализациям: LinkedList, ArrayList, CopyOnWriteArrayList."
tags:
  - languages
  - java
  - java-collections-list
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Java Collections: List

Руководство по интерфейсу List и его реализациям: LinkedList, ArrayList, CopyOnWriteArrayList.

## Полезные ссылки

- [Visualgo](https://visualgo.net/en) — визуализация структур данных
- [Apache Commons](https://commons.apache.org/) — утилиты для коллекций
- [Google Guava](https://github.com/google/guava/wiki) — расширенные коллекции

### См. также
- [[java-apache-commons|Apache Commons Collections]] — расширенные коллекции, **Bag**, **BidiMap**, **CollectionUtils**
- [[java-guava|Google Guava]] — **Multimap**, **ImmutableList**, **FluentIterable**

## Содержание

- [Руководство по LinkedList](#руководство-по-linkedlist)
- [Руководство по ArrayList](#руководство-по-arraylist)
- [Неизменяемый ArrayList](#неизменяемый-arraylist)
- [Руководство по CopyOnWriteArrayList](#руководство-по-copyonwritearraylist)
- [Многомерный ArrayList](#многомерный-arraylist)
- [Преобразование Iterator в List](#преобразование-iterator-в-list)
- [Как получить случайный элемент из List?](#как-получить-случайный-элемент-из-list)
- [Как разделить List?](#как-разделить-list)
- [Удаление всех null из List](#удаление-всех-null-из-list)
- [Удаление всех дубликатов из List](#удаление-всех-дубликатов-из-list)
- [Как проверить одинаковость двух List?](#как-проверить-одинаковость-двух-list)
- [Как найти элемент в List?](#как-найти-элемент-в-list)
- [Исключение UnsupportedOperationException при работе с List](#исключение-unsupportedoperationexception-при-работе-с-list)
- [Копирование List](#копирование-list)
- [Удалить все вхождения определенного значения из List](#удалить-все-вхождения-определенного-значения-из-list)
- [Добавить несколько элементов в ArrayList](#добавить-несколько-элементов-в-arraylist)
- [Удалить первый элемент из List](#удалить-первый-элемент-из-list)
- [Способы перебора List](#способы-перебора-list)
- [Пересечение двух List](#пересечение-двух-list)
- [Как подсчитать повторяющиеся элементы в List](#как-подсчитать-повторяющиеся-элементы-в-list)
- [Поиск различий между двумя List](#поиск-различий-между-двумя-list)
- [Реверс LinkedList](#реверс-linkedlist)
- [Как проверить два List на равенство, игнорируя последовательность элементов?](#как-проверить-два-list-на-равенство-игнорируя-последовательность-элементов)
- [Лучшие практики](#лучшие-практики)
## Руководство по LinkedList

**LinkedList** — это двусвязный список, реализующий интерфейсы **List** и **Deque**. Он реализует все необязательные операции со списками и разрешает все элементы (включая null).

**Ниже вы можете найти наиболее важные свойства **LinkedList**:**

1. **Операции, индексирующие список, будут проходить по списку с начала или с конца**, в зависимости от того, что ближе к указанному индексу.
2. **Он не синхронизирован**.
3. **Его итераторы `Iterator` и `ListIterator` являются отказоустойчивыми** (это означает, что после создания итератора, если список будет изменен, будет выдано исключение **ConcurrentModificationException**).
4. **Каждый элемент представляет собой узел**, который хранит ссылку на следующий и предыдущий элементы.
5. **Он поддерживает порядок вставки**.

**Хотя **LinkedList** не синхронизирован, мы можем получить его синхронизированную версию, вызвав метод **Collections.synchronizedList**, например:**

```java
// Потокобезопасная обёртка над LinkedList
List list = Collections.synchronizedList(new LinkedList(...));
```

Хотя оба они реализуют интерфейс **List**, у них разная семантика, что определенно повлияет на решение, какой из них использовать.

**ArrayList** — это структура данных на основе индекса, поддерживаемая массивом. Он обеспечивает произвольный доступ к своим элементам с производительностью, равной **O(1)**.

С другой стороны, **LinkedList** хранит свои данные в виде списка элементов, и каждый элемент связан со своим предыдущим и следующим элементом. В этом случае операция поиска элемента имеет время выполнения, равное **O(n)**.

Операции вставки, добавления и удаления элемента выполняются быстрее в **LinkedList**, потому что нет необходимости изменять размер массива или обновлять индекс, когда элемент добавляется в какую-то произвольную позицию внутри коллекции, изменятся только ссылки в окружающих элементах.

**LinkedList** потребляет больше памяти, чем **ArrayList**, потому что каждый узел в **LinkedList** хранит две ссылки, одну для своего предыдущего элемента и одну для следующего элемента, тогда как **ArrayList** содержит только данные и свой индекс.

**Вот несколько примеров кода, которые показывают, как вы можете использовать **LinkedList**:**

```java
// Создание двусвязного списка (очередь, стек, итерация)
LinkedList<Object> linkedList = new LinkedList<>();
```

**LinkedList** реализует интерфейс **List** и **Deque**, кроме стандартных методов **add()** и **addAll()** есть **addFirst()** и **addLast()**, которые добавляют элемент в начало или конец соответственно.

Подобно добавлению элементов, эта реализация списка предлагает методы **removeFirst()** и **removeLast()**.

Также есть удобные методы **removeFirstOccurence()** и **removeLastOccurence()**, которые возвращают логическое значение (**true**, если коллекция содержит указанный элемент).

**Интерфейс **Deque** обеспечивает поведение, подобное очереди (фактически **Deque** расширяет интерфейс **Queue**):**

```java
// Извлечение и удаление первого элемента: poll() возвращает null для пустого списка
linkedList.poll();
// pop() бросает NoSuchElementException для пустого списка
linkedList.pop();
```

Эти методы извлекают первый элемент и удаляют его из списка.

Разница между **poll()** и **pop()** заключается в том, что **pop** выдает **NoSuchElementException()** для пустого списка, тогда как **poll** возвращает **null**. Также доступны **API `pollFirst()`** и **pollLast()**.

**Вот, например, как работает **push API**:**

```java
// Вставка элемента в начало списка (как заголовок коллекции)
linkedList.push(Object o);
```

Который вставляет элемент в качестве заголовка коллекции.

**LinkedList** имеет много других методов, большинство из которых должны быть знакомы пользователю, который уже использовал списки. Другие, предоставляемые **Deque**, могут быть удобной альтернативой «стандартным» методам.

## Руководство по ArrayList

В этой статье мы рассмотрим класс **ArrayList** из **Java Collections Framework**. Мы обсудим его свойства, распространенные варианты использования, а также преимущества и недостатки.

**ArrayList** находится в основных библиотеках **Java**, поэтому вам не нужны никакие дополнительные библиотеки. Чтобы использовать его, просто добавьте следующий оператор импорта:**

```java
// Импорт ArrayList для работы со списком
import java.util.ArrayList;
```

Список представляет собой упорядоченную последовательность значений, в которой некоторое значение может встречаться более одного раза.

**ArrayList** — это одна из реализаций списка, построенная на основе массива, который может динамически увеличиваться и уменьшаться при добавлении или удалении элементов. К элементам можно было легко получить доступ по их индексам, начиная с нуля. Эта реализация имеет следующие свойства:**

1. **Произвольный доступ занимает `O(1)` раз**
2. **Добавление элемента занимает амортизированное постоянное время O(1)**
3. **Вставка/удаление занимает `O(n)` времени**
4. **Поиск занимает `O(n)` времени для несортированного массива и `O(**log** n**)` для отсортированного**.

**ArrayList** имеет несколько конструкторов, и мы представим их все в этом разделе.

Во-первых, обратите внимание, что **ArrayList** является универсальным классом, поэтому вы можете параметризовать его любым типом, который вы хотите, и компилятор гарантирует, что, например, вы не сможете поместить значения **Integer** в коллекцию **Strings**. Кроме того, вам не нужно приводить элементы при извлечении их из коллекции.

Во-вторых, хорошей практикой является использование универсального интерфейса **List** в качестве типа переменной, поскольку это отделяет его от конкретной реализации.

```java
// Пустой ArrayList с типом String; проверка isEmpty()
List<String> list = new ArrayList<>();
assertTrue(list.isEmpty());
```

Мы просто создаем пустой экземпляр **ArrayList**.

```java
// Начальная ёмкость внутреннего массива — 20 элементов
List<String> list = new ArrayList<>(20);
```

Здесь вы указываете начальную длину базового массива. Это может помочь вам избежать ненужного изменения размера при добавлении новых элементов.

```java
// Создание списка из коллекции (набор чисел 0..9)
Collection<Integer> numbers = IntStream.range(0, 10).boxed().collect(toSet());
List<Integer> list = new ArrayList<>(numbers);
assertEquals(10, list.size());
assertTrue(numbers.containsAll(list));
```

Обратите внимание, что этот элемент экземпляра **Collection** используется для заполнения базового массива.

**Вы можете вставить элемент либо в конце, либо в определенной позиции:**

```java
// add(1L), add(2L) — в конец; add(1, 3L) — вставка по индексу 1
List<Long> list = new ArrayList<>();
list.add(1L);
list.add(2L);
list.add(1, 3L);
assertThat(Arrays.asList(1L, 3L, 2L), equalTo(list));
```

**Вы также можете вставить коллекцию или несколько элементов одновременно:**

```java
// addAll(0, ys) — вставка коллекции в начало списка
List<Long> list = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
LongStream.range(4, 10).boxed()
    .collect(collectingAndThen(toCollection(ArrayList::new), ys -> list.addAll(0, ys)));
assertThat(Arrays.asList(4L, 5L, 6L, 7L, 8L, 9L, 1L, 2L, 3L), equalTo(list));
```

Доступны два типа итераторов: **Iterator** и **ListIterator**.

В то время как первое дает вам возможность перемещаться по списку в одном направлении, второе позволяет вам перемещаться по нему в обоих направлениях.

**Здесь мы покажем вам только **ListIterator**:**

```java
// Обход списка с конца с помощью ListIterator.hasPrevious() / previous()
List<Integer> list = new ArrayList<>(
    IntStream.range(0, 10).boxed().collect(toCollection(ArrayList::new))
);

ListIterator<Integer> it = list.listIterator(list.size());
List<Integer> result = new ArrayList<>(list.size());

while (it.hasPrevious()) {
    result.add(it.previous());
}

Collections.reverse(list);
assertThat(result, equalTo(list));
```

Вы также можете искать, добавлять или удалять элементы с помощью итераторов.

**Мы продемонстрируем, как работает поиск с использованием коллекции:**

```java
// Список шестнадцатеричных строк 0..f; дублируем для поиска
List<String> list = LongStream.range(0, 16)
    .boxed()
    .map(Long::toHexString)
    .collect(toCollection(ArrayList::new));

List<String> stringsToSearch = new ArrayList<>(list);
stringsToSearch.addAll(list);
```

**Чтобы найти элемент, вы можете использовать методы **indexOf()** или **lastIndexOf()**. Они оба принимают объект и возвращают значение **int**:**

```java
// indexOf — первое вхождение "a"; lastIndexOf — последнее
assertEquals(10, stringsToSearch.indexOf("a"));
assertEquals(26, stringsToSearch.lastIndexOf("a"));
```

**Если вы хотите найти все элементы, удовлетворяющие предикату, вы можете отфильтровать коллекцию с помощью **Java 8 Stream API** (подробнее об этом читайте здесь), используя **Predicate** следующим образом:**

```java
// Фильтрация по множеству допустимых значений через Stream
Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));
List<String> result = stringsToSearch
    .stream()
    .filter(matchingStrings::contains)
    .collect(toCollection(ArrayList::new));
assertEquals(6, result.size());
```

**Также можно использовать цикл **for** или итератор:**

```java
// Поиск элементов через итератор и проверку по множеству
Iterator<String> it = stringsToSearch.iterator();
Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));
List<String> result = new ArrayList<>();

while (it.hasNext()) {
    String s = it.next();
    if (matchingStrings.contains(s)) {
        result.add(s);
    }
}
```

**Если у вас есть отсортированный массив, вы можете использовать алгоритм двоичного поиска, который работает быстрее, чем линейный поиск:**

```java
// Двоичный поиск требует отсортированного списка
List<String> copy = new ArrayList<>(stringsToSearch);
Collections.sort(copy);
int index = Collections.binarySearch(copy, "f");
assertThat(index, not(equalTo(-1)));
```

Обратите внимание, что если элемент не найден, будет возвращено **-1**.

**Для того, чтобы удалить элемент, нужно найти его индекс и только потом выполнять удаление методом **remove()**. Перегруженная версия этого метода, которая принимает объект, ищет его и выполняет удаление первого вхождения равного элемента:**

```java
// remove(0) — по индексу; remove(Integer.valueOf(0)) — по значению (важно для Integer)
List<Integer> list = new ArrayList<>(IntStream.range(0, 10).boxed().collect(toCollection(ArrayList::new)));
Collections.reverse(list);
list.remove(0);
assertThat(list.get(0), equalTo(8));
list.remove(Integer.valueOf(0));
assertFalse(list.contains(0));
```

Но будьте осторожны при работе с коробочными типами, такими как **Integer**. Чтобы удалить конкретный элемент, вы должны сначала указать значение **int**, иначе элемент будет удален по его индексу.

**Вы также можете использовать вышеупомянутый **Stream API** для удаления нескольких элементов, но мы не будем его здесь показывать. Для этого мы будем использовать итератор:**

```java
// Безопасное удаление при итерации через Iterator.remove()
Set<String> matchingStrings = HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
Iterator<String> it = stringsToSearch.iterator();

while (it.hasNext()) {
    if (matchingStrings.contains(it.next())) {
        it.remove();
    }
}
```

## Неизменяемый ArrayList

В этом кратком руководстве показано, как сделать **ArrayList** неизменяемым с помощью основного **JDK**, с помощью **Guava** и, наконец, с **Apache `Commons Collections` 4**.

**Во-первых, **JDK** предоставляет хороший способ получить неизменяемую коллекцию из существующей:**

```java
// Представление списка без возможности изменения
Collections.unmodifiableList(list);
```

**Новая коллекция больше не должна быть изменена на этом этапе:**

```java
@Test(expected = UnsupportedOperationException.class)
public void givenUsingTheJdk_whenUnmodifiableListIsCreated_thenNotModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<String> unmodifiableList = Collections.unmodifiableList(list);
    unmodifiableList.add("four");
}
```

**Начиная с **Java 9**, мы можем использовать статический фабричный метод **List<E>.of(**E… **elements**)** для создания неизменяемого списка:**

```java
@Test(expected = UnsupportedOperationException.class)
public final void givenUsingTheJava9_whenUnmodifiableListIsCreated_thenNotModifiable() {
    final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    final List<String> unmodifiableList = List.of(list.toArray(new String[] {}));
    unmodifiableList.add("four");
}
```

Обратите внимание, как мы должны преобразовать существующий список в массив. Это связано с тем, что **List.of(elements)** принимает параметры **vararg**.

**Guava** предоставляет аналогичную функциональность для создания собственной версии **ImmutableList**:**

```java
// Копия списка в неизменяемый ImmutableList (Guava)
ImmutableList.copyOf(list);
```

**Точно так же** — результирующий список не должен быть модифицируемым:**

```java
@Test(expected = UnsupportedOperationException.class)
public void givenUsingGuava_whenUnmodifiableListIsCreated_thenNotModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<String> unmodifiableList = ImmutableList.copyOf(list);
    unmodifiableList.add("four");
}
```

Обратите внимание, что эта операция фактически создаст копию исходного списка, а не только представление.

**Guava** также предоставляет конструктор — он вернет **ImmutableList** со строгим типом вместо простого **List**:**

```java
@Test(expected = UnsupportedOperationException.class)
public void givenUsingGuavaBuilder_whenUnmodifiableListIsCreated_thenNoLongerModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    ImmutableList<String> unmodifiableList = ImmutableList.<String>builder().addAll(list).build();
    unmodifiableList.add("four");
}
```

**Наконец, **Commons Collection** также предоставляет **API** для создания неизменяемого списка:**

```java
// Неизменяемая обёртка над списком (Apache Commons)
ListUtils.unmodifiableList(list);
```

**И снова изменение результирующего списка должно привести к исключению **UnsupportedOperationException**:**

```java
@Test(expected = UnsupportedOperationException.class)
public void givenUsingCommonsCollections_whenUnmodifiableListIsCreated_thenNotModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<String> unmodifiableList = ListUtils.unmodifiableList(list);
    unmodifiableList.add("four");
}
```

## Руководство по CopyOnWriteArrayList

В этой быстрой статье мы рассмотрим **CopyOnWriteArrayList** из пакета **java.util.concurrent**.

Это очень полезная конструкция в многопоточных программах — когда мы хотим выполнить итерацию по списку потокобезопасным способом без явной синхронизации.

В дизайне **CopyOnWriteArrayList** используется интересный метод, позволяющий сделать его потокобезопасным без необходимости синхронизации. Когда мы используем любой из методов модификации, таких как **add()** или **remove()**, все содержимое **CopyOnWriteArrayList** копируется в новую внутреннюю копию.

Благодаря этому простому факту мы можем безопасно перебирать список, даже когда происходит одновременная модификация.

Когда мы вызываем метод **iterator()** для **CopyOnWriteArrayList**, мы возвращаем **Iterator** с резервной копией неизменного снимка содержимого **CopyOnWriteArrayList**.

Его содержимое является точной копией данных, находящихся внутри **ArrayList** с момента создания **Iterator**. Даже если тем временем какой-то другой поток добавляет или удаляет элемент из списка, эта модификация создает новую копию данных, которая будет использоваться при любом последующем поиске данных из этого списка.

Характеристики этой структуры данных делают ее особенно полезной в тех случаях, когда мы повторяем ее чаще, чем изменяем. Если добавление элементов является обычной операцией в нашем сценарии, то **CopyOnWriteArrayList** не будет хорошим выбором, потому что дополнительные копии определенно приведут к снижению производительности.

**Допустим, мы создаем экземпляр **CopyOnWriteArrayList**, в котором хранятся целые числа:**

```java
// Потокобезопасный список с копированием при записи
CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(new Integer[] {1, 3, 5, 8});
```

**Затем мы хотим выполнить итерацию по этому массиву, поэтому мы создаем экземпляр **Iterator**:**

```java
// Итератор по снимку списка на момент вызова iterator()
Iterator<Integer> iterator = numbers.iterator();
```

**После создания **Iterator** мы добавляем новый элемент в список чисел:**

```java
// Модификация не влияет на уже созданный итератор (новая копия)
numbers.add(10);
```

Имейте в виду, что когда мы создаем итератор для **CopyOnWriteArrayList**, мы получаем неизменный снимок данных в списке на момент вызова **iterator()**.

**Из-за этого при переборе мы не увидим числа **10** в итерации:**

```java
// Итератор видит только снимок до add(10)
List<Integer> result = new LinkedList<>();
iterator.forEachRemaining(result::add);
assertThat(result).containsOnly(1, 3, 5, 8);
```

**Последующая итерация с использованием только что созданного **Iterator** также вернет добавленное число **10**:**

```java
Iterator<Integer> iterator2 = numbers.iterator();
List<Integer> result2 = new LinkedList<>();
iterator2.forEachRemaining(result2::add);
assertThat(result2).containsOnly(1, 3, 5, 8, 10);
```

**CopyOnWriteArrayList** был создан, чтобы обеспечить возможность безопасного повторения элементов даже при изменении базового списка.

**Из-за механизма копирования операция **remove()** для возвращаемого **Iterator** не разрешена, что приводит к исключению **UnsupportedOperationException**:**

```java
@Test(expected = UnsupportedOperationException.class)
public void whenIterateOverItAndTryToRemoveElement_thenShouldThrowException() {
    CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(new Integer[] {1, 3, 5, 8});
    Iterator<Integer> iterator = numbers.iterator();
    while (iterator.hasNext()) {
        iterator.remove();
    }
}
```

## Многомерный ArrayList

Создание многомерного **ArrayList** часто возникает во время программирования. Во многих случаях необходимо создать двумерный **ArrayList** или трехмерный **ArrayList**.

В этом руководстве мы обсудим, как создать многомерный **ArrayList** в **Java**.

Предположим, мы хотим представить граф с **3** вершинами, пронумерованными от **0** до **2**. Кроме того, предположим, что в графе есть **3** ребра **(0, 1), (1, 2)** и **(2, 0)**, где пара вершин представляет ребро.

Мы можем представить ребра в **2-D ArrayList**, создав и заполнив **ArrayList** из **ArrayList** s.

**Во-первых, давайте создадим новый **2-D ArrayList**:**

```java
int vertexCount = 3;
ArrayList<ArrayList<Integer>> graph = new ArrayList<>(vertexCount);
```

**Далее мы инициализируем каждый элемент **ArrayList** другим **ArrayList**:**

```java
for(int i=0; i < vertexCount; i++) {
    graph.add(new ArrayList());
}
```

**Наконец, мы можем добавить все ребра **(0, 1), (1, 2)** и **(2, 0)** в наш **2-D ArrayList**:**

```java
graph.get(0).add(1);
graph.get(1).add(2);
graph.get(2).add(0);
```

**Предположим также, что наш граф не является ориентированным графом. Итак, нам также нужно добавить ребра **(1, 0), (2, 1)** и **(0, 2)** в наш **2-D ArrayList**:**

```java
graph.get(1).add(0);
graph.get(2).add(1);
graph.get(0).add(2);
```

**Затем, чтобы перебрать весь граф, мы можем использовать двойной цикл **for**:**

```java
int vertexCount = graph.size();
for (int i = 0; i < vertexCount; i++) {
    int edgeCount = graph.get(i).size();
    for (int j = 0; j < edgeCount; j++) {
        Integer startVertex = i;
        Integer endVertex = graph.get(i).get(j);
        System.out.printf("Vertex %d is connected to vertex %d%n", startVertex, endVertex);
    }
}
```

**В предыдущем разделе мы создали двумерный список **ArrayList**. Следуя той же логике, создадим трехмерный **ArrayList**:**

Предположим, что мы хотим представить трехмерное пространство. Таким образом, каждая точка в этом трехмерном пространстве будет представлена тремя координатами, скажем, **X, Y** и **Z**.

В дополнение к этому давайте представим, что каждая из этих точек будет иметь цвет: красный, зеленый, синий или желтый. Теперь каждую точку (X, Y, Z)** и ее цвет можно представить трехмерным списком **ArrayList**.

Для простоты предположим, что мы создаем трехмерное пространство (2 `x 2` x 2)**. В нем будет восемь точек: **(0, 0, 0), (0, 0, 1), (0, 1, 0), (0, 1, 1), (1, 0, 0), (1, 0, 1), (1, 1, 0)** и **(1, 1, 1)**.

**Давайте сначала инициализируем переменные и **3-D ArrayList**:**

```java
int x_axis_length = 2;
int y_axis_length = 2;
int z_axis_length = 2;
ArrayList<ArrayList<ArrayList<String>>> space = new ArrayList<>(x_axis_length);
```

**Затем давайте инициализируем каждый элемент **ArrayList** с помощью **ArrayList<`ArrayList`<`String`>>**:**

```java
for (int i = 0; i < x_axis_length; i++) {
    space.add(new ArrayList<ArrayList<String>>(y_axis_length));
    for (int j = 0; j < y_axis_length; j++) {
        space.get(i).add(new ArrayList<String>(z_axis_length));
    }
}
```

**Теперь мы можем добавлять цвета к точкам в пространстве. Добавим красный цвет для точек **(0, 0, 0)** и **(0, 0, 1)**:**

```java
space.get(0).get(0).add(0, "Red");
space.get(0).get(0).add(1, "Red");
```

**Затем установим синий цвет для точек **(0, 1, 0)** и **(0, 1, 1)**:**

```java
space.get(0).get(1).add(0, "Blue");
space.get(0).get(1).add(1, "Blue");
```

Точно так же мы можем продолжать заполнять точки в пространстве другими цветами.

**Обратите внимание, что информация о цвете точки с координатами (i, j, k)** хранится в следующем элементе **3-D ArrayList**:**

```java
space.get(i).get(j).get(k)
```

Как мы видели в этом примере, переменная **space** — это **ArrayList**. Кроме того, каждый элемент этого **ArrayList** является двумерным **ArrayList** (аналогично тому, что мы видели в разделе 2).

Обратите внимание, что индекс элементов в нашем пространстве **ArrayList** представляет координату **X**, в то время как каждый **2-D ArrayList**, присутствующий в этом индексе, представляет координаты (Y, Z)**.

## Преобразование Iterator в List

**Начнем с подхода, который традиционно использовался до **Java 8**. Мы преобразуем итератор в список с помощью цикла **while**:**

```java
List<Integer> actualList = new ArrayList<Integer>();
while (iterator.hasNext()) {
    actualList.add(iterator.next());
}
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

**В **Java 8** и более поздних версиях мы можем использовать метод **forEachRemaining()** класса **Iterator** для построения нашего списка. Мы передадим метод **add()** интерфейса **List** в качестве ссылки на метод:**

```java
List<Integer> actualList = new ArrayList<Integer>();
iterator.forEachRemaining(actualList::add);
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

**Далее мы будем использовать **Java 8 `Streams` API** для преобразования **Iterator** в **List**. Чтобы использовать **Stream API**, нам нужно сначала преобразовать **Iterator** в **Iterable**. Мы можем сделать это, используя лямбда-выражения **Java 8**:**

```java
Iterable<Integer> iterable = () -> iterator;
```

**Теперь мы можем использовать методы **stream()** и **collect()** класса **StreamSupport** для построения списка:**

```java
List<Integer> actualList = StreamSupport
    .stream(iterable.spliterator(), false)
    .collect(Collectors.toList());
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

Библиотека **Guava** от **Google** предоставляет возможности для создания как изменяемых, так и неизменяемых списков, поэтому мы рассмотрим оба подхода.

**Давайте сначала создадим неизменяемый список, используя метод **ImmutableList.`copyOf()`**:**

```java
List<Integer> actualList = ImmutableList.copyOf(iterator);
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

**Теперь давайте создадим изменяемый список, используя метод **Lists.`newArrayList()`**:**

```java
List<Integer> actualList = Lists.newArrayList(iterator);
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

**Библиотека **Apache `Commons` Collections** предоставляет опции для работы со списком. Мы будем использовать **IteratorUtils** для преобразования:**

```java
List<Integer> actualList = IteratorUtils.toList(iterator);
assertThat(actualList, containsInAnyOrder(1, 2, 3));
```

## Как получить случайный элемент из List?

Выбор случайного элемента списка — очень простая операция, но не столь очевидная для реализации. В этой статье мы покажем наиболее эффективный способ сделать это в различных контекстах.

Чтобы получить случайный элемент из экземпляра **List**, вам нужно сгенерировать случайный номер индекса, а затем получить элемент по этому сгенерированному номеру индекса с помощью метода **List.get()**.

Ключевым моментом здесь является то, что вы не должны использовать индекс, превышающий размер вашего списка.

**Чтобы выбрать случайный индекс, вы можете использовать метод **Random.`nextInt`(int bound)**:**

```java
public void givenList_shouldReturnARandomElement() {
    List<Integer> givenList = Arrays.asList(1, 2, 3);
    Random rand = new Random();
    int randomElement = givenList.get(rand.nextInt(givenList.size()));
}
```

Вместо класса **Random** вы всегда можете использовать статический метод **Math.random()** и умножить его на размер списка (**Math.random()** генерирует двойное случайное значение между **0 (включительно)** и **1 (исключительно)**, поэтому не забудьте привести его к типу **int** после умножения).

**При написании многопоточных приложений с использованием одного экземпляра класса **Random** может быть выбрано одно и то же значение для каждого процесса, обращающегося к этому экземпляру. Мы всегда можем создать новый экземпляр для каждого потока, используя выделенный класс **ThreadLocalRandom**:**

```java
int randomElementIndex = ThreadLocalRandom.current().nextInt(listSize) % givenList.size();
```

**Иногда вам может понадобиться выбрать несколько элементов из списка. Это довольно просто:**

```java
public void givenList_whenNumberElementsChosen_shouldReturnRandomElementsRepeat() {
    Random rand = new Random();
    List<String> givenList = Arrays.asList("one", "two", "three", "four");
    int numberOfElements = 2;
    for (int i = 0; i < numberOfElements; i++) {
        int randomIndex = rand.nextInt(givenList.size());
        String randomElement = givenList.get(randomIndex);
    }
}
```

**Здесь вам нужно убедиться, что элемент удаляется из списка после выбора:**

```java
public void givenList_whenNumberElementsChosen_shouldReturnRandomElementsNoRepeat() {
    Random rand = new Random();
    List<String> givenList = Lists.newArrayList("one", "two", "three", "four");
    int numberOfElements = 2;
    for (int i = 0; i < numberOfElements; i++) {
        int randomIndex = rand.nextInt(givenList.size());
        String randomElement = givenList.get(randomIndex);
        givenList.remove(randomIndex);
    }
}
```

**Если вы хотите получить случайную серию элементов, вам может пригодиться класс **Collections utils**:**

```java
public void givenList_whenSeriesLengthChosen_shouldReturnRandomSeries() {
    List<Integer> givenList = Lists.newArrayList(1, 2, 3, 4, 5, 6);
    Collections.shuffle(givenList);
    int randomSeriesLength = 3;
    List<Integer> randomSeries = givenList.subList(0, randomSeriesLength);
}
```

## Как разделить List?

В этой статье мы покажем, как разделить список на несколько подсписков заданного размера.

Для относительно простой операции стандартные **API** коллекций **Java** на удивление не поддерживаются. К счастью, и в **Guava**, и в **Apache `Commons` Collections** операция реализована аналогичным образом.

**Guava** облегчает разбиение списка на подсписки заданного размера с помощью операции **Lists.partition**:**

```java
@Test
public void givenList_whenParitioningIntoNSublists_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = Lists.partition(intList, 3);
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

**Разделение коллекции также возможно с помощью **Guava**:**

```java
@Test
public void givenCollection_whenParitioningIntoNSublists_thenCorrect() {
    Collection<Integer> intCollection = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    Iterable<List<Integer>> subSets = Iterables.partition(intCollection, 3);
    List<Integer> firstPartition = subSets.iterator().next();
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(1, 2, 3);
    assertThat(firstPartition, equalTo(expectedLastPartition));
}
```

**Имейте в виду, что разделы представляют собой представления подсписков исходной коллекции, а это означает, что изменения в исходной коллекции будут отражены в разделах:**

```java
@Test
public void givenListPartitioned_whenOriginalListIsModified_thenPartitionsChangeAsWell() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = Lists.partition(intList, 3);
    intList.add(9);
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8, 9);
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

**В последних выпусках **Apache `Commons` Collections** недавно также была добавлена поддержка разделения списка:**

```java
@Test
public void givenList_whenParitioningIntoNSublists_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = ListUtils.partition(intList, 3);
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

Коллекции **Commons** не имеют соответствующей опции для разделения необработанной коллекции, аналогичной **Guava `Iterables`.partition**.

Наконец, здесь применимо то же предостережение: результирующие разделы являются представлениями исходного списка.

**Мы можем использовать **Collectors.`partitioningBy()`**, чтобы разделить список на **2** подсписка:**

```java
@Test
public void givenList_whenParitioningIntoSublistsUsingPartitionBy_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    Map<Boolean, List<Integer>> groups =
        intList.stream().collect(Collectors.partitioningBy(s -> s > 6));
    List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());
    List<Integer> lastPartition = subSets.get(1);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(2));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

Примечание. Результирующие разделы не являются представлением основного списка, поэтому любые изменения, происходящие с основным списком, не повлияют на разделы.

**Мы также можем использовать **Collectors.`groupingBy()`**, чтобы разделить наш список на несколько разделов:**

```java
@Test
public final void givenList_whenParitioningIntoNSublistsUsingGroupingBy_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    Map<Integer, List<Integer>> groups =
        intList.stream().collect(Collectors.groupingBy(s -> (s - 1)/3));
    List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

Примечание. Как и в случае с **Collectors.`partitioningBy()`**, результирующие разделы не будут затронуты изменениями в основном списке.

**Мы также можем использовать **Java8** для разделения нашего списка по разделителю:**

```java
@Test
public void givenList_whenSplittingBySeparator_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 0, 4, 5, 6, 0, 7, 8);
    int[] indexes =
        Stream.of(IntStream.of(-1), IntStream.range(0, intList.size())
            .filter(i -> intList.get(i) == 0), IntStream.of(intList.size()))
            .flatMapToInt(s -> s).toArray();
    List<List<Integer>> subSets =
        IntStream.range(0, indexes.length - 1)
            .mapToObj(i -> intList.subList(indexes[i] + 1, indexes[i + 1]))
            .collect(Collectors.toList());
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

Примечание. В качестве разделителя мы использовали **"0"**. Сначала мы получили индексы всех **"0"** элементов в списке, а затем разбили список по этим индексам.

## Удаление всех null из List

**Java Collections Framework** предлагает простое решение для удаления всех нулевых элементов в списке — базовый цикл **while**:**

```java
@Test
public void givenListContainsNulls_whenRemovingNullsWithPlainJava_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, null);
    while (list.remove(null));
    assertThat(list, hasSize(1));
}
```

**В качестве альтернативы мы также можем использовать следующий простой подход:**

```java
@Test
public void givenListContainsNulls_whenRemovingNullsWithPlainJavaAlternative_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, null);
    list.removeAll(Collections.singleton(null));
    assertThat(list, hasSize(1));
}
```

Обратите внимание, что оба этих решения изменят исходный список.

**Мы также можем удалить нули с помощью **Guava** и более функционального подхода с помощью предикатов:**

```java
@Test
public void givenListContainsNulls_whenRemovingNullsWithGuavaV1_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, null);
    Iterables.removeIf(list, Predicates.isNull());
    assertThat(list, hasSize(1));
}
```

**В качестве альтернативы, если мы не хотим изменять исходный список, **Guava** позволит нам создать новый список фильтров:**

```java
@Test
public void givenListContainsNulls_whenRemovingNullsWithGuavaV2_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, null, 2, 3);
    List<Integer> listWithoutNulls = Lists.newArrayList(
        Iterables.filter(list, Predicates.notNull()));
    assertThat(listWithoutNulls, hasSize(3));
}
```

**Теперь давайте рассмотрим простое решение с использованием библиотеки **Apache `Commons` Collections** с использованием аналогичного функционального стиля:**

```java
@Test
public void givenListContainsNulls_whenRemovingNullsWithCommonsCollections_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
    CollectionUtils.filter(list, PredicateUtils.notNullPredicate());
    assertThat(list, hasSize(3));
}
```

Обратите внимание, что это решение также изменит исходный список.

**Наконец** — давайте посмотрим на решение **Java 8**, использующее **Lambdas** для фильтрации списка; процесс фильтрации может выполняться параллельно или последовательно:**

```java
@Test
public void givenListContainsNulls_whenFilteringParallel_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
    List<Integer> listWithoutNulls = list.parallelStream()
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

@Test
public void givenListContainsNulls_whenFilteringSerial_thenCorrect() {
    List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
    List<Integer> listWithoutNulls = list.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

public void givenListContainsNulls_whenRemovingNullsWithRemoveIf_thenCorrect() {
    List<Integer> listWithoutNulls = Lists.newArrayList(null, 1, 2, null, 3, null);
    listWithoutNulls.removeIf(Objects::isNull);
    assertThat(listWithoutNulls, hasSize(3));
}
```

**Вот и все** — несколько быстрых и очень полезных решений для избавления от всех нулевых элементов из списка.

## Удаление всех дубликатов из List

В этом кратком руководстве мы узнаем, как удалить повторяющиеся элементы из списка. Сначала мы будем использовать обычную **Java**, затем **Guava** и, наконец, решение на основе **Java 8 Lambda**.

**Мы можем легко удалить повторяющиеся элементы из списка с помощью стандартной **Java Collections Framework** через **Set**:**

```java
public void givenListContainsDuplicates_whenRemovingDuplicatesWithPlainJava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = new ArrayList<>(
        new HashSet<>(listWithDuplicates));
    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

Как мы видим, исходный список остался без изменений.

В приведенном выше примере мы использовали реализацию **HashSet**, которая представляет собой неупорядоченную коллекцию. В результате порядок очищенного **listWithoutDuplicates** может отличаться от порядка исходного **listWithDuplicates**.

**Если нам нужно сохранить порядок, мы можем вместо этого использовать **LinkedHashSet**:**

```java
public void givenListContainsDuplicates_whenRemovingDuplicatesPreservingOrderWithPlainJava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = new ArrayList<>(
        new LinkedHashSet<>(listWithDuplicates));
    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInRelativeOrder(5, 0, 3, 1, 2));
}
```

**Мы можем сделать то же самое, используя **Guava**:**

```java
public void givenListContainsDuplicates_whenRemovingDuplicatesWithGuava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = Lists.newArrayList(Sets.newHashSet(listWithDuplicates));
    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

Здесь также первоначальный список остается без изменений.

Опять же, порядок элементов в очищаемом списке может быть случайным.

**Если мы используем реализацию **LinkedHashSet**, мы сохраним исходный порядок:**

```java
public void givenListContainsDuplicates_whenRemovingDuplicatesPreservingOrderWithGuava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = Lists.newArrayList(Sets.newLinkedHashSet(listWithDuplicates));
    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInRelativeOrder(5, 0, 3, 1, 2));
}
```

Наконец, давайте рассмотрим новое решение, использующее лямбда-выражения в **Java 8**. Мы будем использовать метод **distinct()** из **Stream API**, который возвращает поток, состоящий из отдельных элементов, на основе результата, возвращаемого методом `equals()`.

**Кроме того, для упорядоченных потоков выбор отдельных элементов является стабильным. Это означает, что для повторяющихся элементов сохраняется элемент, появляющийся первым в порядке обнаружения:**

```java
public void givenListContainsDuplicates_whenRemovingDuplicatesWithJava8_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = listWithDuplicates.stream()
        .distinct()
        .collect(Collectors.toList());
    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

Итак, у нас есть три быстрых способа удалить все повторяющиеся элементы из списка.

## Как проверить одинаковость двух List?

В этой короткой статье мы сосредоточимся на распространенной проблеме тестирования, если два экземпляра **List** содержат одни и те же элементы в точно таком же порядке.

**Список** — это упорядоченная структура данных, поэтому порядок элементов имеет значение по дизайну.

**Взгляните на отрывок из документации **List#equals** (Java):**

... два списка считаются равными, если они содержат одни и те же элементы в одном и том же порядке.

Это определение гарантирует правильную работу метода **equals** в различных реализациях интерфейса **List**.

Мы можем использовать эти знания при написании утверждений.

**В следующих фрагментах кода мы будем использовать следующие списки в качестве примеров входных данных:**

```java
List<String> list1 = Arrays.asList("1", "2", "3", "4");
List<String> list2 = Arrays.asList("1", "2", "3", "4");
List<String> list3 = Arrays.asList("1", "2", "4", "3");
```

**В чистом тесте **JUnit** будут верны следующие утверждения:**

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    Assert.assertEquals(list1, list2);
    Assert.assertNotSame(list1, list2);
    Assert.assertNotEquals(list1, list3);
}
```

**При использовании утверждений **TestNG** они будут очень похожи на утверждения **JUnit**, но важно отметить, что класс **Assert** происходит из другого пакета:**

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    Assert.assertEquals(list1, list2);
    Assert.assertNotSame(list1, list2);
    Assert.assertNotEquals(list1, list3);
}
```

**Если вам нравится использовать **AssertJ**, его утверждения будут выглядеть следующим образом:**

```java
@Test
public void whenTestingForEquality_ShouldBeEqual() throws Exception {
    assertThat(list1)
        .isEqualTo(list2)
        .isNotEqualTo(list3);
    assertThat(list1.equals(list2)).isTrue();
    assertThat(list1.equals(list3)).isFalse();
}
```

## Как найти элемент в List?

**Сначала давайте начнем с определения **Customer POJO**:**

```java
public class Customer {
    private int id;
    private String name;
}
```

**Затем **ArrayList** клиентов:**

```java
List<Customer> customers = new ArrayList<>();
customers.add(new Customer(1, "Jack"));
customers.add(new Customer(2, "James"));
customers.add(new Customer(3, "Kelly"));
```

Обратите внимание, что мы переопределили **hashCode** и **equals** в нашем классе **Customer**.

На основе нашей текущей реализации **equals** два объекта **Customer** с одинаковым идентификатором будут считаться равными.

Мы будем использовать этот список клиентов по пути.

**Когда нам нужно проверить, существует ли конкретный элемент в нашем списке, мы можем:**

```java
Customer james = new Customer(2, "James");
if (customers.contains(james)) {
    // элемент найден
}
```

**indexOf** — еще один полезный метод для поиска элементов:**

```java
int indexOf(Object element)
```

Этот метод возвращает индекс первого появления указанного элемента в данном списке или **-1**, если список не содержит элемент.

**Итак, логически, если этот метод возвращает что-либо, кроме **-1**, мы знаем, что список содержит элемент:**

```java
if (customers.indexOf(james) != -1) {
    // элемент найден
}
```

Основное преимущество использования этого метода заключается в том, что он может сообщить нам позицию указанного элемента в заданном списке.

А что, если мы хотим выполнить поиск элемента по полю? Например, скажем, мы объявляем лотерею, и нам нужно объявить Клиента с определенным именем победителем.

Для таких полевых поисков мы можем обратиться к итерации.

**Традиционным способом перебора списка является использование одной из циклических конструкций **Java**. На каждой итерации мы сравниваем текущий элемент в списке с искомым элементом, чтобы увидеть, совпадают ли они:**

```java
public Customer findUsingEnhancedForLoop(String name, List<Customer> customers) {
    for (Customer customer : customers) {
        if (customer.getName().equals(name)) {
            return customer;
        }
    }
    return null;
}
```

Здесь имя относится к имени, которое мы ищем в данном списке клиентов. Этот метод возвращает первый объект **Customer** в списке с совпадающим именем или **null**, если такой **Customer** не существует.

**Iterator** — это еще один способ, с помощью которого мы можем перемещаться по списку элементов.

**Мы можем просто взять наш предыдущий пример и немного изменить его:**

```java
public Customer findUsingIterator(String name, List<Customer> customers) {
    Iterator<Customer> iterator = customers.iterator();
    while (iterator.hasNext()) {
        Customer customer = iterator.next();
        if (customer.getName().equals(name)) {
            return customer;
        }
    }
    return null;
}
```

Начиная с **Java 8**, мы также можем использовать **Stream API** для поиска элемента в списке.

**Чтобы найти элемент, соответствующий определенным критериям в заданном списке, мы:**

```java
Customer james = customers.stream()
    .filter(customer -> "James".equals(customer.getName()))
    .findAny()
    .orElse(null);
```

Для удобства по умолчанию используется значение **null**, если необязательный параметр пуст, но это не всегда лучший выбор для каждого сценария.

Теперь, когда **Stream API** более чем достаточно, что нам делать, если мы застряли на более ранней версии **Java**?

К счастью, существует множество сторонних библиотек, таких как **Google Guava** и **Apache Commons**, которые мы можем использовать.

**Google Guava** предоставляет функциональность, похожую на то, что мы можем делать с потоками:**

```java
Customer james = Iterables.tryFind(customers,
    new Predicate<Customer>() {
        public boolean apply(Customer customer) {
            return "James".equals(customer.getName());
        }
    }).orNull();
```

**Как и в случае с **Stream API**, мы можем при желании вернуть значение по умолчанию вместо **null**:**

```java
Customer james = Iterables.tryFind(customers,
    new Predicate<Customer>() {
        public boolean apply(Customer customer) {
            return "James".equals(customer.getName());
        }
    }).or(customers.get(0));
```

Приведенный выше код выберет первый элемент в списке, если совпадений не найдено.

Кроме того, не забывайте, что **Guava** генерирует исключение **NullPointerException**, если список или предикат имеют значение **null**.

**Мы можем найти элемент почти таким же образом, используя **Apache Commons**:**

```java
Customer james = IterableUtils.find(customers,
    new Predicate<Customer>() {
        public boolean evaluate(Customer customer) {
            return "James".equals(customer.getName());
        }
    });
```

**Однако есть пара важных отличий:**

**Apache Commons** просто возвращает **null**, если мы передаем нулевой список.

Он не предоставляет функциональные возможности значений по умолчанию, такие как **tryFind** в **Guava**.

## Исключение UnsupportedOperationException при работе с List

В этом кратком руководстве мы обсудим распространенное исключение, которое может возникнуть при работе с некоторыми **API** большинства реализаций списка — **UnsupportedOperationException**.

**java.util.List** обладает большей функциональностью, чем может поддерживать обычный массив. Например, с помощью только одного вызова встроенного метода можно проверить, находится ли конкретный элемент внутри структуры. Обычно поэтому нам иногда нужно преобразовать массив в **List** или **Collection**.

**Часто эта ошибка возникает, когда мы используем метод **asList()** из **java.util.Arrays**:**

```java
public static <T> List<T> asList(T... a)
```

**Он возвращает:**

1. список фиксированного размера по размеру данного массива
2. элемент того же типа, что и в исходном массиве, и он должен быть объектом
3. элементы в том же порядке, что и в исходном массиве
4. список, который сериализуем и реализует **RandomAccess**

**Поскольку **T** является **varargs**, мы можем передать массив или элементы напрямую в качестве параметров, и метод создаст инициализированный список фиксированного размера:**

```java
List<String> flowers = Arrays.asList("Ageratum", "Allium", "Poppy", "Catmint");
```

**Мы также можем передать фактический массив:**

```java
String[] flowers = {"Ageratum", "Allium", "Poppy", "Catmint"};
List<String> flowerList = Arrays.asList(flowers);
```

Так как возвращенный **List** имеет фиксированный размер, мы не можем добавлять/удалять элементы.

**Попытка добавить больше элементов вызовет **UnsupportedOperationException**:**

```java
String[] flowers = {"Ageratum", "Allium", "Poppy", "Catmint"};
List<String> flowerList = Arrays.asList(flowers);
flowerList.add("Celosia");
```

Корень этого исключения заключается в том, что возвращаемый объект не реализует операцию **add()**, поскольку он не совпадает с **java.util.ArrayList**.

Это **ArrayList** из **java.util.Arrays**.

Другой способ получить такое же исключение — попытаться удалить элемент из полученного списка.

С другой стороны, есть способы получить изменяемый список на случай, если он нам понадобится.

**Одним из них является создание **ArrayList** или любого другого списка непосредственно из результата **asList()**:**

```java
String[] flowers = {"Ageratum", "Allium", "Poppy", "Catmint"};
List<String> flowerList = new ArrayList<>(Arrays.asList(flowers));
```

## Копирование List

**Простой способ скопировать список — использовать конструктор, который принимает коллекцию в качестве аргумента:**

```java
List<Plant> copy = new ArrayList<>(list);
```

Поскольку здесь мы копируем ссылки, а не клонируем объекты, каждое исправление, сделанное в одном элементе, повлияет на оба списка.

**Таким образом, хорошо использовать конструктор для копирования неизменяемых объектов:**

```java
List<Integer> copy = new ArrayList<>(list);
```

**Integer** — неизменный класс; его значение устанавливается при создании экземпляра и никогда не может измениться.

Таким образом, ссылка **Integer** может совместно использоваться несколькими списками и потоками, и никто не может изменить ее значение.

Распространенной проблемой при работе со списками является **ConcurrentAccessException**. Обычно это означает, что мы изменяем список, когда пытаемся его скопировать, скорее всего, в другом потоке.

**Чтобы решить эту проблему, мы должны:**

1. Используйте коллекцию, предназначенную для одновременного доступа
2. Заблокируйте коллекцию соответствующим образом, чтобы перебрать ее
3. Найдите способ избежать копирования оригинальной коллекции

Учитывая наш последний подход, он не является потокобезопасным. Если мы хотим решить нашу проблему с помощью первого варианта, мы можем захотеть использовать **CopyOnWriteArrayList**, в котором все мутативные операции реализуются путем создания новой копии базового массива.

Для получения дополнительной информации, пожалуйста, обратитесь к этой статье.

Если мы хотим заблокировать **Collection**, можно использовать примитив блокировки для сериализованного доступа для чтения/записи, например **ReentrantReadWriteLock**.

**Другой подход к копированию элементов — использование метода **addAll**:**

```java
List<Integer> copy = new ArrayList<>();
copy.addAll(list);
```

При использовании этого метода важно помнить, что, как и в случае с конструктором, содержимое обоих списков будет ссылаться на одни и те же объекты.

Класс **Collections** состоит исключительно из статических методов, которые работают с коллекциями или возвращают их.

Одним из них является **copy**, для которого требуется исходный список и целевой список, длина которого не меньше исходного.

**Он будет поддерживать индекс каждого скопированного элемента в списке назначения, например оригинала:**

```java
List<Integer> source = Arrays.asList(1, 2, 3);
List<Integer> dest = Arrays.asList(4, 5, 6);
Collections.copy(dest, source);
```

В приведенном выше примере все предыдущие элементы в списке назначения были перезаписаны, поскольку оба списка имеют одинаковый размер.

**Если размер списка назначения больше, чем размер источника:**

```java
List<Integer> source = Arrays.asList(1, 2, 3);
List<Integer> dest = Arrays.asList(5, 6, 7, 8, 9, 10);
Collections.copy(dest, source);
```

Здесь только первые три элемента были перезаписаны, а остальные элементы в списке сохранены.

**Эта версия **Java** расширяет наши возможности, добавляя новые инструменты. В следующих примерах мы рассмотрим **Stream**:**

```java
List<String> copy = list.stream().collect(Collectors.toList());
```

**Главное преимущество этого варианта — возможность использовать пропуск и фильтры. В следующем примере мы пропустим первый элемент:**

```java
List<String> copy = list.stream()
    .skip(1)
    .collect(Collectors.toList());
```

**Также можно фильтровать по длине строки или сравнивать атрибуты наших объектов:**

```java
List<String> copy = list.stream().filter(s -> s.length() > 10).collect(Collectors.toList());
List<Flower> flowers = list.stream().filter(f -> f.getPetals() > 6).collect(Collectors.toList());
```

**Вероятно, мы хотим работать нулевым безопасным способом:**

```java
List<Flower> flowers = Optional.ofNullable(list)
    .map(List::stream)
    .orElseGet(Stream::empty)
    .collect(Collectors.toList());
```

**Вероятно, мы также захотим пропустить элемент таким образом:**

```java
List<Flower> flowers = Optional.ofNullable(list)
    .map(List::stream).orElseGet(Stream::empty)
    .skip(1)
    .collect(Collectors.toList());
```

**Наконец, одна из последних версий **Java** позволяет нам создать неизменяемый список, содержащий элементы данной коллекции:**

```java
List<T> copy = List.copyOf(list);
```

Единственным условием является то, что данная коллекция не должна быть нулевой или содержать нулевые элементы.

## Удалить все вхождения определенного значения из List

В **Java** просто удалить конкретное значение из списка с помощью **List.remove()**. Однако эффективно удалить все вхождения значения намного сложнее.

В этом уроке мы увидим несколько решений этой проблемы с описанием плюсов и минусов.

Для удобочитаемости мы используем в тестах пользовательский метод **list(**int**…**)**, который возвращает **ArrayList**, содержащий переданные нами элементы.

**Поскольку мы знаем, как удалить один элемент, повторение этого в цикле выглядит достаточно просто:**

```java
void removeAll(List<Integer> list, int element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```

**Однако это не работает так, как ожидалось:**

```java
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
assertThatThrownBy(() -> removeAll(list, valueToRemove))
    .isInstanceOf(IndexOutOfBoundsException.class);
```

Проблема в **3-й** строке: мы вызываем **List.remove(int)**, который рассматривает свой аргумент как индекс, а не значение, которое мы хотим удалить.

В приведенном выше тесте мы всегда вызываем **list.remove(1)**, но индекс элемента, который мы хотим удалить, равен **0**. Вызов **List.remove()** сдвигает все элементы после удаленного на меньшие индексы.

В данном сценарии это означает, что мы удаляем все элементы, кроме первого.

Когда останется только первый, индекс **1** будет недопустимым. Следовательно, мы получаем **Exception**.

Обратите внимание, что мы сталкиваемся с этой проблемой только в том случае, если мы вызываем **List.remove()** с примитивным аргументом **byte, short, char** или **int**, поскольку первое, что делает компилятор, когда пытается найти соответствующий перегруженный метод, - это расширение.

**Мы можем исправить это, передав значение как **Integer**:**

```java
void removeAll(List<Integer> list, Integer element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```

**Теперь код работает как положено:**

```java
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

Поскольку **List.contains()** и **List.remove()** должны найти первое вхождение элемента, этот код вызывает ненужный обход элемента.

**Мы можем добиться большего успеха, если сохраним индекс первого вхождения:**

```java
void removeAll(List<Integer> list, Integer element) {
    int index;
    while ((index = list.indexOf(element)) >= 0) {
        list.remove(index);
    }
}
```

**Мы можем убедиться, что это работает:**

```java
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

Хотя эти решения создают короткий и чистый код, они по-прежнему имеют низкую производительность: поскольку мы не отслеживаем прогресс, **List.remove()** должен найти первое вхождение предоставленного значения, чтобы удалить его.

Кроме того, когда мы используем **ArrayList**, смещение элементов может привести к многократному копированию ссылок, даже к перераспределению резервного массива несколько раз.

У **List.remove(**E **element**)** есть функция, о которой мы еще не упоминали: она возвращает логическое значение, которое верно, если список был изменен из-за операции, поэтому он содержит элемент.

Обратите внимание, что **List.remove(int index)** возвращает **void**, потому что, если предоставленный индекс действителен, **List** всегда удаляет его. В противном случае выбрасывается **IndexOutOfBoundsException**.

**При этом мы можем выполнять удаления до тех пор, пока список не изменится:**

```java
void removeAll(List<Integer> list, int element) {
    while (list.remove(element));
}
```

**Он работает так, как ожидалось:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

Несмотря на то, что эта реализация коротка, она страдает теми же проблемами, которые мы описали в предыдущем разделе.

**Мы можем отслеживать наш прогресс, проходя по элементам с помощью цикла **for** и удаляя текущий, если он соответствует:**

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        }
    }
}
```

**Он работает так, как ожидалось:**

```java
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

**Однако, если мы попробуем это с другим вводом, он выдаст неверный вывод:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(1, 2, 3));
```

Таким образом, мы сталкиваемся с этой проблемой, когда у нас есть два соседних значения, которые мы хотим удалить. Чтобы решить эту проблему, мы должны сохранить переменную цикла.

**Уменьшение его при удалении элемента:**

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
            i--;
        }
    }
}
```

**Увеличиваем его только тогда, когда мы не удаляем элемент:**

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size();) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        } else {
            i++;
        }
    }
}
```

Обратите внимание, что в последнем мы удалили оператор **i++** в строке **2**.

**Оба решения работают так, как ожидалось:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

**Эта реализация кажется правильной на первый взгляд. Тем не менее, у него все еще есть серьезные проблемы с производительностью:**

- удаление элемента из **ArrayList** сдвигает все элементы после него
- доступ к элементам по индексу в **LinkedList** означает обход элементов один за другим, пока мы не найдем индекс

**Начиная с **Java 5** мы можем использовать цикл **for-each** для перебора списка. Давайте используем его для удаления элементов:**

```java
void removeAll(List<Integer> list, int element) {
    for (Integer number : list) {
        if (Objects.equals(number, element)) {
            list.remove(number);
        }
    }
}
```

Обратите внимание, что мы используем **Integer** в качестве типа переменной цикла. Поэтому мы не получим **NullPointerException**.

Кроме того, таким образом мы вызываем **List.remove(**E **element**)**, который ожидает значение, которое мы хотим удалить, а не индекс.

**Как бы чисто это ни выглядело, к сожалению, это не работает:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
assertThatThrownBy(() -> removeWithForEachLoop(list, valueToRemove))
    .isInstanceOf(ConcurrentModificationException.class);
```

Цикл **for-each** использует **Iterator** для обхода элементов. Однако, когда мы изменяем **List**, **Iterator** переходит в несогласованное состояние. Следовательно, он выдает **ConcurrentModificationException**.

Урок таков: мы не должны изменять **List**, пока мы обращаемся к его элементам в цикле **for-each**.

**Мы можем использовать итератор напрямую для обхода и изменения списка с его помощью:**

```java
void removeAll(List<Integer> list, int element) {
    for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
        Integer number = i.next();
        if (Objects.equals(number, element)) {
            i.remove();
        }
    }
}
```

**Таким образом, итератор может отслеживать состояние списка (поскольку он вносит изменения). В результате приведенный выше код работает так, как ожидалось:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

Поскольку каждый класс **List** может предоставить свою собственную реализацию **Iterator**, мы можем с уверенностью предположить, что он реализует обход и удаление элементов наиболее эффективным способом.

Однако использование **ArrayList** по-прежнему означает большое смещение элементов (и, возможно, перераспределение массива). Кроме того, приведенный выше код немного сложнее читать, поскольку он отличается от стандартного цикла **for**, с которым знакомо большинство разработчиков.

**До этого мы модифицировали исходный объект **List**, удаляя ненужные элементы. Вместо этого мы можем создать новый список и собрать элементы, которые хотим сохранить:**

```java
List<Integer> removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }
    return remainingElements;
}
```

**Поскольку мы предоставляем результат в новом объекте **List**, мы должны вернуть его из метода. Поэтому нам нужно использовать метод по-другому:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
List<Integer> result = removeAll(list, valueToRemove);
assertThat(result).isEqualTo(list(2, 3));
```

Обратите внимание, что теперь мы можем использовать цикл **for-each**, так как мы не изменяем список, который сейчас просматриваем.

Поскольку нет никаких удалений, нет необходимости перемещать элементы. Поэтому эта реализация хорошо работает, когда мы используем **ArrayList**.

**Эта реализация ведет себя иначе, чем предыдущие:**

1. он не изменяет исходный список, а возвращает новый
2. метод решает, какова реализация возвращаемого списка, она может отличаться от исходной.

**Кроме того, мы можем изменить нашу реализацию, чтобы получить старое поведение; очищаем исходный **List** и добавляем в него собранные элементы:**

```java
void removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }
    list.clear();
    list.addAll(remainingElements);
}
```

**Он работает так же, как и предыдущие:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

Поскольку мы не изменяем список постоянно, нам не нужно обращаться к элементам по положению или сдвигать их. Кроме того, есть только два возможных перераспределения массива: когда мы вызываем **List.clear()** и **List.`addAll()`**.

**В **Java 8** появились лямбда-выражения и потоковый **API**. Благодаря этим мощным функциям мы можем решить нашу проблему с помощью очень чистого кода:**

```java
List<Integer> removeAll(List<Integer> list, int element) {
    return list.stream()
        .filter(e -> !Objects.equals(e, element))
        .collect(Collectors.toList());
}
```

Это решение работает так же, как когда мы собирали оставшиеся элементы.

**В итоге он имеет такие же характеристики, и мы должны использовать его для возврата результата:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
List<Integer> result = removeAll(list, valueToRemove);
assertThat(result).isEqualTo(list(2, 3));
```

Обратите внимание, что мы можем преобразовать его для работы, как и другие решения, с тем же подходом, который мы использовали с исходной реализацией «сбора».

Вместе с лямбда-выражениями и функциональными интерфейсами в **Java 8** также появились некоторые расширения **API**. Например, метод **List.`removeIf()`**, реализующий то, что мы видели в предыдущем разделе.

**Он ожидает **Predicate**, который должен возвращать **true**, когда мы хотим удалить элемент, в отличие от предыдущего примера, где нам приходилось возвращать **true**, когда мы хотели сохранить элемент:**

```java
void removeAll(List<Integer> list, int element) {
    list.removeIf(n -> Objects.equals(n, element));
}
```

**Он работает так же, как и другие решения выше:**

```java
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

В связи с тем, что сам список реализует этот метод, можно смело предположить, что он обладает наилучшей доступной производительностью. Кроме того, это решение обеспечивает самый чистый код из всех.

## Добавить несколько элементов в ArrayList

В этом кратком руководстве мы покажем, как добавить несколько элементов в уже инициализированный **ArrayList**.

Для ознакомления с использованием **ArrayList** обратитесь к этой статье здесь.

Прежде всего, мы собираемся представить простой способ добавления нескольких элементов в список **ArrayList**.

**Во-первых, мы будем использовать **addAll()**, которая принимает коллекцию в качестве аргумента:**

```java
List<Integer> anotherList = Arrays.asList(5, 12, 9, 3, 15, 88);
list.addAll(anotherList);
```

Важно помнить, что элементы, добавленные в первый список, будут ссылаться на те же объекты, что и элементы в **otherList**.

По этой причине каждое изменение, внесенное в один из этих элементов, повлияет на оба списка.

Класс **Collections** состоит исключительно из статических методов, которые работают с коллекциями или возвращают их.

Одним из них является **addAll**, для которого требуется список назначения, а добавляемые элементы могут быть указаны по отдельности или в виде массива.

**Вот пример того, как использовать его с отдельными элементами:**

```java
List<Integer> list = new ArrayList<>();
Collections.addAll(list, 1, 2, 3, 4, 5);
```

**И еще один для примера работы с двумя массивами:**

```java
List<Integer> list = new ArrayList<>();
Integer[] otherList = new Integer[] {1, 2, 3, 4, 5};
Collections.addAll(list, otherList);
```

Аналогично тому, как описано в предыдущем разделе, содержимое обоих списков здесь будет относиться к одним и тем же объектам.

**Эта версия **Java** открывает наши возможности, добавляя новые инструменты. В следующих примерах мы рассмотрим **Stream**:**

```java
List<Integer> source = ...;
List<Integer> target = ...;
source.stream().forEachOrdered(target::add);
```

**Основные преимущества этого способа — возможность использовать пропуск и фильтры. В следующем примере мы собираемся пропустить первый элемент:**

```java
source.stream().skip(1).forEachOrdered(target::add);
```

**Можно фильтровать элементы по нашим потребностям. Например, целочисленное значение:**

```java
source.stream().filter(i -> i > 10).forEachOrdered(target::add);
```

**Наконец, есть сценарии, в которых мы хотим работать с нулевым безопасным способом. Для них мы можем использовать **Optional**:**

```java
Optional.ofNullable(source).ifPresent(target::addAll)
```

В приведенном выше примере мы добавляем элементы из исходного кода в целевой с помощью метода **addAll**.

## Удалить первый элемент из List

**Во-первых, давайте заполним наш список:**

```java
@Before
public void init() {
    list.add("cat");
    list.add("dog");
    list.add("pig");
    list.add("cow");
    list.add("goat");
    linkedList.add("cat");
    linkedList.add("dog");
    linkedList.add("pig");
    linkedList.add("cow");
    linkedList.add("goat");
}
```

**Во-вторых, давайте удалим первый элемент из **ArrayList** и убедимся, что наш список больше не содержит его:**

```java
@Test
public void givenList_whenRemoveFirst_thenRemoved() {
    list.remove(0);
    assertThat(list, hasSize(4));
    assertThat(list, not(contains("cat")));
}
```

Как показано выше, мы используем метод **remove(index)** для удаления первого элемента — это также будет работать для любой реализации интерфейса **List**.

**LinkedList** также реализует метод **remove(index) (по-своему)**, но также имеет метод **removeFirst()**.

**Давайте удостоверимся, что он работает так, как ожидалось:**

```java
@Test
public void givenLinkedList_whenRemoveFirst_thenRemoved() {
    linkedList.removeFirst();
    assertThat(linkedList, hasSize(4));
    assertThat(linkedList, not(contains("cat")));
}
```

Хотя методы выглядят одинаково, их эффективность различается. Метод **remove()** в **ArrayList** требует **O(n)** времени, тогда как метод **removeFirst()** в **LinkedList** требует **O(1)** времени.

Это связано с тем, что **ArrayList** использует массив под капотом, а операция **remove()** требует копирования остальной части массива в начало. Чем больше массив, тем больше элементов нужно сдвинуть.

В отличие от этого, **LinkedList** использует указатели, означающие, что каждый элемент указывает на следующий и предыдущий.

Следовательно, удаление первого элемента означает просто изменение указателя на первый элемент. Эта операция всегда требует одинакового времени, независимо от размера списка.

## Способы перебора List

**Перебор элементов списка** — одна из самых распространенных задач в программе.

В этом руководстве мы рассмотрим различные способы сделать это в **Java**. Мы сосредоточимся на переборе списка по порядку, хотя и в обратном порядке тоже несложно.

Во-первых, давайте рассмотрим некоторые параметры цикла **for**.

**Мы начнем с определения списка стран для наших примеров:**

```java
List<String> countries = Arrays.asList("Germany", "Panama", "Australia");
```

Наиболее распространенным оператором управления потоком для итерации является базовый цикл **for**.

Цикл **for** определяет три типа операторов, разделенных точкой с запятой. Первый оператор является оператором инициализации. Второй определяет условие завершения. Последнее утверждение — это предложение обновления.

**Здесь мы просто используем целочисленную переменную в качестве индекса:**

```java
for (int i = 0; i < countries.size(); i++) {
    System.out.println(countries.get(i));
}
```

При инициализации мы должны объявить целочисленную переменную, чтобы указать начальную точку. Эта переменная обычно действует как индекс списка.

**Условие завершения** — это выражение, которое возвращает логическое значение после оценки. Как только это выражение становится ложным, цикл завершается.

Предложение **update** используется для изменения текущего состояния индексной переменной, увеличения или уменьшения его до точки завершения.

Усовершенствованный цикл **for** представляет собой простую структуру, которая позволяет нам обращаться к каждому элементу списка. Он похож на базовый цикл **for**, но более удобочитаем и компактен. Следовательно, это одна из наиболее часто используемых форм для обхода списка.

**Обратите внимание, что расширенный цикл **for** проще, чем базовый цикл **for**:**

```java
for (String country : countries) {
    System.out.println(country);
}
```

**Итератор** — это шаблон проектирования, который предлагает нам стандартный интерфейс для обхода структуры данных, не беспокоясь о внутреннем представлении.

Этот способ обхода структур данных дает много преимуществ, среди которых мы можем подчеркнуть, что наш код не зависит от реализации.

Следовательно, структура может быть бинарным деревом или двусвязным списком, поскольку Итератор абстрагирует нас от способа выполнения обхода. Таким образом, мы можем легко заменить структуры данных в нашем коде без неприятных проблем.

В **Java** шаблон **Iterator** отражен в классе **java.util.Iterator**. Он широко используется в коллекциях **Java**. В **Iterator** есть два ключевых метода: **hasNext()** и **next()**.

**Здесь мы продемонстрируем использование обоих:**

```java
Iterator<String> countriesIterator = countries.iterator();
while(countriesIterator.hasNext()) {
    System.out.println(countriesIterator.next());
}
```

Метод **hasNext()** проверяет, остались ли какие-либо элементы в списке.

Метод **next()** возвращает следующий элемент итерации.

**ListIterator** позволяет нам перемещаться по списку элементов в прямом или обратном порядке.

Прокрутка списка с помощью **ListIterator** вперед следует механизму, аналогичному тому, который используется с помощью **Iterator**. Таким образом, мы можем переместить итератор вперед с помощью метода **next()** и найти конец списка с помощью метода **hasNext()**.

**Как мы видим, **ListIterator** очень похож на итератор, который мы использовали ранее:**

```java
ListIterator<String> listIterator = countries.listIterator();
while(listIterator.hasNext()) {
    System.out.println(listIterator.next());
}
```

Начиная с **Java 8**, мы можем использовать метод **forEach()** для перебора элементов списка. Этот метод определен в интерфейсе **Iterable** и может принимать лямбда-выражения в качестве параметра.

**Синтаксис довольно прост:**

```java
countries.forEach(System.out::println);
```

До появления функции **forEach** все итераторы в **Java** были активны, то есть они включали цикл **for** или **while**, который обходил сбор данных до тех пор, пока не выполнялось определенное условие.

С введением **forEach** в качестве функции в интерфейсе **Iterable** все классы, реализующие **Iterable**, имеют добавленную функцию **forEach**.

Мы также можем преобразовать набор значений в поток и получить доступ к таким операциям, как **forEach()**, **map()** и **filter()**.

**Здесь мы продемонстрируем типичное использование потоков:**

```java
countries.stream().forEach((c) -> System.out.println(c));
```

## Пересечение двух List

**Давайте создадим два **List s** из **String** с некоторым пересечением — оба содержат дублирующиеся элементы:**

```java
List<String> list = Arrays.asList("red", "blue", "blue", "green", "red");
List<String> otherList = Arrays.asList("red", "green", "green", "yellow");
```

**А теперь определим пересечение списков с помощью потоковых методов:**

```java
Set<String> result = list.stream()
    .distinct()
    .filter(otherList::contains)
    .collect(Collectors.toSet());
Set<String> commonElements = new HashSet(Arrays.asList("red", "green"));
Assert.assertEquals(commonElements, result);
```

Во-первых, мы удаляем повторяющиеся элементы с **distinct()**. Затем мы используем фильтр для выбора элементов, которые также содержатся в **otherList**.

Наконец, мы преобразуем наш вывод с помощью **Collector**. Пересечение должно содержать каждый общий элемент только один раз. Порядок не должен иметь значения, поэтому **toSet** — самый простой выбор, но мы также можем использовать **toList** или другой метод сбора.

Что, если наши **List** содержат не **String**, а экземпляры пользовательского класса, который мы создали? Ну, пока мы следуем соглашениям **Java**, решение с потоковыми методами будет прекрасно работать для нашего пользовательского класса.

Как метод **contains** решает, появляется ли конкретный объект в списке? На основе метода `equals()`. Таким образом, мы должны переопределить метод `equals()` и убедиться, что он сравнивает два объекта на основе значений соответствующих свойств.

Например, два прямоугольника равны, если их ширина и высота равны.

Если мы не переопределяем метод `equals()`, наш класс использует реализацию `equals()` родительского класса. В конце дня, вернее, в цепочке наследования выполняется метод `equals()` класса **Object**. Тогда два экземпляра равны, только если они ссылаются на один и тот же объект в куче.

Для получения дополнительной информации о методе `equals()` см. нашу статью о контрактах **Java equals()** и **hashCode()**.

## Как подсчитать повторяющиеся элементы в List

Нашим ожидаемым результатом будет объект **Map**, который содержит все элементы из входного списка в качестве ключей и количество каждого элемента в качестве значения.

**Самым простым решением для достижения этого было бы перебрать список ввода и для каждого элемента:**

1. если **resultMap** содержит элемент, мы увеличиваем счетчик на **1**
2. в противном случае мы помещаем новую запись карты (элемент, 1)** на карту

```java
public <T> Map<T, Long> countByClassicalLoop(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    for (T element : inputList) {
        if (resultMap.containsKey(element)) {
            resultMap.put(element, resultMap.get(element) + 1L);
        } else {
            resultMap.put(element, 1L);
        }
    }
    return resultMap;
}
```

Эта реализация имеет наилучшую совместимость, так как работает со всеми современными версиями **Java**.

**Если нам не нужна совместимость до **Java 8**, мы можем еще больше упростить наш метод:**

```java
public <T> Map<T, Long> countByForEachLoopWithGetOrDefault(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.put(e, resultMap.getOrDefault(e, 0L) + 1L));
    return resultMap;
}
```

**Далее создадим входной список для тестирования метода:**

```java
private List<String> INPUT_LIST = Lists.list(
    "expect1",
    "expect2", "expect2",
    "expect3", "expect3", "expect3",
    "expect4", "expect4", "expect4", "expect4");
```

**А теперь давайте проверим это:**

```java
private void verifyResult(Map<String, Long> resultMap) {
    assertThat(resultMap)
        .isNotEmpty().hasSize(4)
        .containsExactly(
            entry("expect1", 1L),
            entry("expect2", 2L),
            entry("expect3", 3L),
            entry("expect4", 4L));
}
```

Мы будем повторно использовать этот тестовый набор для остальных наших подходов.

**В **Java 8** в интерфейс **Map** был добавлен удобный метод **compute()**. Мы также можем использовать этот метод:**

```java
public <T> Map<T, Long> countByForEachLoopWithMapCompute(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.compute(e, (k, v) -> v == null ? 1L : v + 1L));
    return resultMap;
}
```

Обратите внимание (k, v) -> v == `null` ? 1L : v + 1L** — это функция переназначения, реализующая интерфейс **BiFunction<T, `Long`, `Long`>**. Для данного ключа он либо возвращает его текущее значение, увеличенное на единицу (если ключ уже присутствует в карте), либо возвращает значение по умолчанию, равное единице.

Чтобы сделать код более читабельным, мы могли бы извлечь функцию переназначения в ее переменную или даже взять ее в качестве входного параметра для **countByForEachLoopWithMapCompute**.

При использовании **Map.compute()** мы должны явно обрабатывать нулевые значения — например, если сопоставление для данного ключа не существует. Вот почему мы реализовали нулевую проверку в нашей функции переназначения. Однако это не выглядит красиво.

**Давайте еще больше очистим наш код с помощью метода **Map.merge()**:**

```java
public <T> Map<T, Long> countByForEachLoopWithMapMerge(List<T> inputList) {
    Map<T, Long> resultMap = new HashMap<>();
    inputList.forEach(e -> resultMap.merge(e, 1L, Long::sum));
    return resultMap;
}
```

Теперь код выглядит чистым и лаконичным.

Давайте объясним, как работает **merge()**. Если сопоставление для данного ключа не существует или его значение равно **null**, он связывает ключ с предоставленным значением. В противном случае он вычисляет новое значение с помощью функции переназначения и соответствующим образом обновляет сопоставление.

Обратите внимание, что на этот раз мы использовали **Long::sum** в качестве реализации интерфейса **BiFunction<T, `Long`, `Long`>**.

Поскольку мы уже говорили о **Java 8**, нельзя забывать о мощном **Stream API**. Благодаря **Stream API** мы можем решить задачу очень компактно.

**Сборщик **toMap()** помогает нам преобразовать входной список в карту:**

```java
public <T> Map<T, Long> countByStreamToMap(List<T> inputList) {
    return inputList.stream().collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
}
```

**toMap()** — это удобный сборщик, который может помочь нам преобразовать поток в различные реализации карты.

**За исключением **toMap()**, нашу проблему можно решить двумя другими сборщиками, **groupingBy()** и **counting()**:**

```java
public <T> Map<T, Long> countByStreamGroupBy(List<T> inputList) {
    return inputList.stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));
}
```

Правильное использование коллекторов **Java 8** делает наш код компактным и легко читаемым.

## Поиск различий между двумя List

Поиск различий между наборами объектов одного и того же типа данных — обычная задача программирования. В качестве примера представьте, что у нас есть список студентов, подавших заявку на экзамен, и еще один список студентов, которые его сдали. Разница между этими двумя списками даст нам студентов, не сдавших экзамен.

В **Java** нет явного способа нахождения различий между двумя списками в **List API**, хотя есть некоторые вспомогательные методы, которые близки.

В этом кратком руководстве мы узнаем, как найти различия между двумя списками. Мы попробуем несколько различных подходов, включая обычную **Java** (с потоками и без них) и сторонние библиотеки, такие как **Guava** и **Apache `Commons` Collections**.

**Давайте начнем с определения двух списков, которые мы будем использовать для проверки наших примеров:**

```java
public class FindDifferencesBetweenListsUnitTest {
    private static final List listOne = Arrays.asList("Jack", "Tom", "Sam", "John", "James", "Jack");
    private static final List listTwo = Arrays.asList("Jack", "Daniel", "Sam", "Alan", "James", "George");
}
```

**Мы можем создать копию одного списка, а затем удалить все элементы, общие с другим, с помощью метода **List `removeAll()`**:**

```java
List<String> differences = new ArrayList<>(listOne);
differences.removeAll(listTwo);
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

**Давайте перевернем это, чтобы найти различия наоборот:**

```java
List<String> differences = new ArrayList<>(listTwo);
differences.removeAll(listOne);
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Daniel", "Alan", "George");
```

Мы также должны отметить, что если мы хотим найти общие элементы между двумя списками, **List** также содержит метод **retainAll()**.

**Java Stream** можно использовать для выполнения последовательных операций с данными из коллекций, включая фильтрацию различий между списками:**

```java
List<String> differences = listOne.stream()
    .filter(element -> !listTwo.contains(element))
    .collect(Collectors.toList());
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

**Как и в нашем первом примере, мы можем изменить порядок списков, чтобы найти различные элементы из второго списка:**

```java
List<String> differences = listTwo.stream()
    .filter(element -> !listOne.contains(element))
    .collect(Collectors.toList());
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Daniel", "Alan", "George");
```

Следует отметить, что повторный вызов **List.contains()** может быть дорогостоящей операцией для больших списков.

**Guava** содержит удобные наборы. **difference** метод, но для его использования нам нужно сначала преобразовать наш **List** в **Set**:**

```java
List<String> differences = new ArrayList<>(Sets.difference(Sets.newHashSet(listOne), Sets.newHashSet(listTwo)));
assertEquals(2, differences.size());
assertThat(differences).containsExactlyInAnyOrder("Tom", "John");
```

Следует отметить, что преобразование списка в набор приведет к его дублированию и изменению порядка.

Класс **CollectionUtils** из **Apache `Commons` Collections** содержит метод **removeAll**.

**Этот метод делает то же самое, что и **List.removeAll**, а также создайте новую коллекцию для результата:**

```java
List<String> differences = new ArrayList<>((CollectionUtils.removeAll(listOne, listTwo)));
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

Теперь давайте посмотрим, как найти различия, когда два списка содержат повторяющиеся значения.

Для этого нам нужно удалить повторяющиеся элементы из первого списка ровно столько раз, сколько их содержится во втором списке.

**В нашем примере значение **«Джек»** появляется дважды в первом списке и только один раз во втором списке:**

```java
List<String> differences = new ArrayList<>(listOne);
listTwo.forEach(differences::remove);
assertThat(differences).containsExactly("Tom", "John", "Jack");
```

**Мы также можем добиться этого, используя метод вычитания из коллекций **Apache Commons**:**

```java
List<String> differences = new ArrayList<>(CollectionUtils.subtract(listOne, listTwo));
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Tom", "John", "Jack");
```

## Реверс LinkedList

**Связный список** — это линейная структура данных, в которой указатель в каждом элементе определяет порядок. Каждый элемент связанного списка содержит поле данных для хранения данных списка и поле указателя для указания на следующий элемент в последовательности. Кроме того, мы можем использовать головной указатель, чтобы указать на начальный элемент связанного списка.

После того, как мы перевернем связанный список, заголовок будет указывать на последний элемент исходного связанного списка, а указатель каждого элемента будет указывать на предыдущий элемент исходного связанного списка.

В **Java** у нас есть класс **LinkedList** для реализации двусвязного списка, реализующего интерфейсы **List** и **Deque**. Однако в этом руководстве мы будем использовать общую структуру данных односвязного списка.

**Давайте сначала начнем с класса **ListNode** для представления элемента связанного списка:**

```java
public class ListNode {
    private int data;
    private ListNode next;

    ListNode(int data) {
        this.data = data;
        this.next = null;
    }
}
```

**Класс **ListNode** имеет два поля:**

1. Целочисленное значение для представления данных элемента
2. Указатель/ссылка на следующий элемент

**Связанный список может содержать несколько объектов **ListNode**. Например, мы можем создать приведенный выше пример связанного списка с циклом:**

```java
ListNode constructLinkedList() {
    ListNode head = null;
    ListNode tail = null;
    for (int i = 1; i <= 5; i++) {
        ListNode node = new ListNode(i);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
    }
    return head;
}
```

**Давайте реализуем итерационный алгоритм на **Java**:**

```java
ListNode reverseList(ListNode head) {
    ListNode previous = null;
    ListNode current = head;
    while (current != null) {
        ListNode nextElement = current.getNext();
        current.setNext(previous);
        previous = current;
        current = nextElement;
    }
    return previous;
}
```

В этом итеративном алгоритме мы используем две переменные **ListNode**, предыдущую и текущую, для представления двух соседних элементов в связанном списке. Для каждой итерации мы меняем эти два элемента местами, а затем переходим к следующим двум элементам.

В конце концов, текущий указатель будет нулевым, а предыдущий указатель будет последним элементом старого связанного списка. Таким образом, **previous** также является новым указателем головы обратно связанного списка, и мы возвращаем его из метода.

**Мы можем проверить эту итеративную реализацию с помощью простого модульного теста:**

```java
@Test
public void givenLinkedList_whenIterativeReverse_thenOutputCorrectResult() {
    ListNode head = constructLinkedList();
    ListNode node = head;
    for (int i = 1; i <= 5; i++) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
    LinkedListReversal reversal = new LinkedListReversal();
    node = reversal.reverseList(head);
    for (int i = 5; i >= 1; i--) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
}
```

В этом модульном тесте мы сначала создадим пример связанного списка с пятью узлами. Кроме того, мы проверяем, что каждый узел в связанном списке содержит правильное значение данных. Затем мы вызываем итеративную функцию для обращения связанного списка. Наконец, мы проверяем перевернутый связанный список, чтобы убедиться, что данные перевернуты, как ожидалось.

**Теперь давайте реализуем рекурсивный алгоритм на **Java**:**

```java
ListNode reverseListRecursive(ListNode head) {
    if (head == null) {
        return null;
    }
    if (head.getNext() == null) {
        return head;
    }
    ListNode node = reverseListRecursive(head.getNext());
    head.getNext().setNext(head);
    head.setNext(null);
    return node;
}
```

В функции **reverseListRecursive** мы рекурсивно посещаем каждый элемент в связанном списке, пока не достигнем последнего. Этот последний элемент станет новым заголовком обратно связанного списка. Кроме того, мы добавляем посещенный элемент в конец частично перевернутого связанного списка.

**Точно так же мы можем проверить эту рекурсивную реализацию с помощью простого модульного теста:**

```java
@Test
public void givenLinkedList_whenRecursiveReverse_thenOutputCorrectResult() {
    ListNode head = constructLinkedList();
    ListNode node = head;
    for (int i = 1; i <= 5; i++) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
    LinkedListReversal reversal = new LinkedListReversal();
    node = reversal.reverseListRecursive(head);
    for (int i = 5; i >= 1; i--) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
}
```

## Как проверить два List на равенство, игнорируя последовательность элементов?

Согласно документации **Java `List`#equals**, два списка равны, если они содержат одни и те же элементы в одном и том же порядке. Следовательно, мы не можем просто использовать метод **equals**, так как мы хотим выполнять независимое от порядка сравнение.

**В этом руководстве мы будем использовать эти три списка в качестве примеров входных данных для наших тестов:**

```java
List first = Arrays.asList(1, 3, 4, 6, 8);
List second = Arrays.asList(8, 1, 6, 3, 4);
List third = Arrays.asList(1, 3, 3, 6, 6);
```

Существуют разные способы сравнения независимо от порядка. Давайте посмотрим на них один за другим.

**JUnit** — это хорошо известная платформа, используемая для модульного тестирования в экосистеме **Java**.

Мы можем использовать приведенную ниже логику, чтобы сравнить равенство двух списков, используя методы **assertTrue** и **assertFalse**.

**Здесь мы проверяем размер обоих списков и проверяем, содержит ли первый список все элементы второго списка и наоборот. Хотя это решение работает, оно не очень читабельно. Итак, теперь давайте рассмотрим некоторые альтернативы:**

```java
@Test
public void whenTestingForOrderAgnosticEquality_ShouldBeTrue() {
    assertTrue(first.size() == second.size() && first.containsAll(second) && second.containsAll(first));
}
```

В этом первом тесте размер обоих списков сравнивается, прежде чем мы проверяем, совпадают ли элементы в обоих списках. Поскольку оба этих условия возвращают значение **true**, наш тест будет пройден.

**Давайте теперь посмотрим на неудачный тест:**

```java
@Test
public void whenTestingForOrderAgnosticEquality_ShouldBeFalse() {
    assertFalse(first.size() == third.size() && first.containsAll(third) && third.containsAll(first));
}
```

Напротив, в этой версии теста, хотя размер обоих списков одинаков, все элементы не совпадают.

**AssertJ** — это управляемая сообществом библиотека с открытым исходным кодом, используемая для написания плавных и насыщенных утверждений в тестах **Java**.

**Чтобы использовать его в нашем проекте **maven**, давайте добавим зависимость **assertj-core** в файл **pom.xml**:**

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.16.1</version>
</dependency>
```

**Давайте напишем тест для сравнения равенства двух экземпляров списка одного и того же элемента и одного размера:**

```java
@Test
void whenTestingForOrderAgnosticEqualityBothList_ShouldBeEqual() {
    assertThat(first).hasSameElementsAs(second);
}
```

В этом примере мы сначала проверяем, что он содержит все элементы данной итерации и ничего больше, в любом порядке. Основным ограничением этого подхода является то, что метод **hasSameElementsAs** игнорирует дубликаты.

**Давайте посмотрим на это на практике, чтобы понять, что мы имеем в виду:**

```java
@Test
void whenTestingForOrderAgnosticEqualityBothList_ShouldNotBeEqual() {
    List a = Arrays.asList("a", "a", "b", "c");
    List b = Arrays.asList("a", "b", "c");
    assertThat(a).hasSameElementsAs(b);
}
```

**В этом тесте, хотя у нас одинаковые элементы, размер обоих списков не равен, но утверждение все равно будет верным, так как оно игнорирует дубликаты. Чтобы это работало, нам нужно добавить проверку размера для обоих списков:**

```java
assertThat(a).hasSize(b.size()).hasSameElementsAs(b);
```

Добавление проверки размера обоих наших списков с последующим методом **hasSameElementsAs** действительно завершится ошибкой, как и ожидалось.

Если мы уже используем **Hamcrest** или хотим использовать его для написания модульных тестов, вот как мы можем использовать метод **Matchers#containsInAnyOrder** для независимого от порядка сравнения.

**Чтобы использовать **Hamcrest** в нашем проекте **maven**, давайте добавим зависимость **hamcrest-all** в файл **pom.xml**:**

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>1.3</version>
</dependency>
```

**Смотрим тест:**

```java
@Test
public void whenTestingForOrderAgnosticEquality_ShouldBeEqual() {
    assertThat(first, Matchers.containsInAnyOrder(second.toArray()));
}
```

Здесь метод **containsInAnyOrder** создает независимый от порядка сопоставитель для **Iterables**, который выполняет сопоставление с проверяемыми элементами **Iterable**. Этот тест сопоставляет элементы двух списков, игнорируя порядок элементов в списке.

К счастью, это решение не страдает от той же проблемы, которая описана в предыдущем разделе, поэтому нам не нужно явно сравнивать размеры.

Другой библиотекой или фреймворком помимо **JUnit**, **Hamcrest** или **AssertJ**, который мы можем использовать, является **Apache CollectionUtils**. Он предоставляет служебные методы для общих операций, которые охватывают широкий спектр вариантов использования и помогают нам избежать написания стандартного кода.

**Чтобы использовать его в нашем проекте **maven**, добавим зависимость **commons-collections4** в файл **pom.xml**:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

**Вот тест с использованием **CollectionUtils**:**

```java
@Test
public void whenTestingForOrderAgnosticEquality_ShouldBeTrueIfEqualOtherwiseFalse() {
    assertTrue(CollectionUtils.isEqualCollection(first, second));
    assertFalse(CollectionUtils.isEqualCollection(first, third));
}
```

Метод **isEqualCollection** возвращает значение **true**, если заданные коллекции содержат точно такие же элементы с одинаковым количеством элементов. В противном случае возвращается **false**.

## Лучшие практики

- **Выбор реализации:** для частого доступа по индексу — `ArrayList`; для частой вставки/удаления — `LinkedList`; для многопоточности с редкой записью — `CopyOnWriteArrayList`.
- **Неизменяемые списки:** использовать `**List.of**()` (Java 9+) для константных списков; избегать мутаций через «обёртки», возвращаемые старыми **API**.
- **Инициализация:** задавать начальную ёмкость `ArrayList` при известном размере: `**new ArrayList**<>(n)` — уменьшает реаллокации.
- **Перебор:** предпочитать **for-each** или итератор для простого обхода; не удалять элементы в цикле по индексу без итератора/**removeIf**.
- **Сравнение и равенство:** для сравнения по содержимому использовать `**equals**()`; для порядка-независимого сравнения — сортировка + **equals** или `CollectionUtils.isEqualCollection`.
- **null:** явно документировать, допускает ли **API null** в списке; для запрета **null** использовать `**List.of**()` или валидацию при добавлении.
- **Потоки:** для фильтрации/преобразования предпочитать **Stream API**; собирать в список через `**Collectors.toList**()` или `**toUnmodifiableList**()`.
