---
title: "Java Collections: Set"
description: "Материал по теме Java Collections: Set в разделе cheatsheets."
tags:
  - languages
  - java
  - java-collections-set
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Java Collections: Set

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)

### Обучающие материалы

- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)


## Содержание

- [Руководство по TreeSet](#руководство-по-treeset)
  - [Метод add()](#метод-add)
  - [Метод contains()](#метод-contains)
  - [Метод remove()](#метод-remove)
  - [Метод size()](#метод-size)
  - [Метод isEmpty()](#метод-isempty)
  - [Метод iterator()](#метод-iterator)
  - [Метод first()](#метод-first)
  - [Метод last()](#метод-last)
  - [Метод subSet()](#метод-subset)
  - [Метод headSet()](#метод-headset)
  - [Метод tailSet()](#метод-tailset)
  - [Работа с null](#работа-с-null)
  - [Сравнение элементов](#сравнение-элементов)
  - [Производительность](#производительность)
- [Руководство по HashSet](#руководство-по-hashset)
  - [Метод clear()](#метод-clear)
  - [Работа с hashCode() и equals()](#работа-с-hashcode-и-equals)
  - [Основные правила работы с HashSet](#основные-правила-работы-с-hashset)
- [Лучшие практики](#лучшие-практики)

## Руководство по TreeSet

В этой статье мы рассмотрим неотъемлемую часть **Java Collections Framework** и одну из самых популярных реализаций **Set** — **TreeSet**.

**Проще говоря, TreeSet** — это отсортированная коллекция, которая расширяет класс **AbstractSet** и реализует интерфейс **NavigableSet**.

**Вот краткий обзор наиболее важных аспектов этой реализации:**

1.  Он хранит уникальные элементы
2.  Он не сохраняет порядок вставки элементов
3.  Сортирует элементы в порядке возрастания
4.  Это не потокобезопасно

В этой реализации объекты сортируются и сохраняются в порядке возрастания в соответствии с их естественным порядком. **TreeSet** использует самобалансирующееся бинарное дерево поиска, точнее красно-чёрное дерево.

Проще говоря, будучи самобалансирующимся бинарным деревом поиска, каждый узел бинарного дерева содержит дополнительный бит, который используется для определения цвета узла: красного или черного. Во время последующих вставок и удалений эти **«цветные»** биты помогают обеспечить более или менее сбалансированное дерево.

Итак, давайте создадим экземпляр **TreeSet:**

```java
// Создание TreeSet с естественным порядком элементов (Comparable)
Set<String> treeSet = new TreeSet<>();
```

При желании мы можем создать **TreeSet** с помощью конструктора, который позволяет нам определить порядок сортировки элементов с помощью **Comparable** или **Comparator:**

```java
// Сортировка по длине строки через Comparator
Set<String> treeSet = new TreeSet<>(Comparator.comparing(String::length));
```

Хотя **TreeSet** не является потокобезопасным, его можно синхронизировать извне с помощью оболочки **Collections.`synchronizedSet()`:**

```java
// Потокобезопасная обёртка над TreeSet
Set<String> syncTreeSet = Collections.synchronizedSet(treeSet);
```

Хорошо, теперь, когда у нас есть четкое представление о том, как создать экземпляр **TreeSet,** давайте посмотрим на доступные нам общие операции.

### Метод add()

Метод **add()** можно использовать для добавления элементов в **TreeSet**. Если элемент был добавлен, метод возвращает **true**, иначе **false**.

В контракте метода указано, что элемент будет добавлен только в том случае, если его ещё нет в **Set**.

Давайте добавим элемент в **TreeSet:**

```java
// add() возвращает true, если элемент был добавлен (ранее отсутствовал)
@Test
public void whenAddingElement_shouldAddElement() {
    Set<String> treeSet = new TreeSet<>();
    assertTrue(treeSet.add("String Added"));
}
```

**Метод **add** чрезвычайно важен, так как детали реализации этого метода иллюстрируют, как **TreeSet** работает внутри, как он использует метод **put** из **TreeMap** для хранения элементов:**

```java
// TreeSet внутри использует TreeMap: ключ — элемент, значение — константа PRESENT
public boolean add(E e) {
    return m.put(e, PRESENT) == null;
}
```

Переменная **m** ссылается на внутреннюю поддержку **TreeMap (**обратите внимание, что **TreeMap** реализует **NavigateableMap**):**

```java
// Внутреннее хранилище — NavigableMap (обычно TreeMap)
private transient NavigableMap<E, Object> m;
```

Таким образом, **TreeSet** внутренне зависит от поддержки **NavigableMap**; которая инициализируется экземпляром **TreeMap** при создании экземпляра **TreeSet:**

```java
// Конструктор по умолчанию создаёт внутренний TreeMap
public TreeSet() {
    this(new TreeMap<E,Object>());
}
```

### Метод contains()

Метод **contains()** используется для проверки наличия данного элемента в данном **TreeSet**. Если элемент найден, он возвращает **true**, в противном случае **false**.

**Давайте посмотрим, как **contains()** в действии:**

```java
// contains() — проверка наличия элемента (O(log n) для TreeSet)
@Test
public void whenCheckingForElement_shouldSearchForElement() {
    Set<String> treeSetContains = new TreeSet<>();
    treeSetContains.add("String Added");
    assertTrue(treeSetContains.contains("String Added"));
}
```

### Метод remove()

Метод **remove()** используется для удаления указанного элемента из набора, если он присутствует.

Если набор содержит указанный элемент, этот метод возвращает значение **true**.

**Давайте посмотрим на это в действии:**

```java
// remove() возвращает true, если элемент был удалён
@Test
public void whenRemovingElement_shouldRemoveElement() {
    Set<String> removeFromTreeSet = new TreeSet<>();
    removeFromTreeSet.add("String Added");
    assertTrue(removeFromTreeSet.remove("String Added"));
}
```

Если мы хотим удалить все элементы из набора, мы можем использовать метод **clear():**

```java
@Test
public void whenClearingTreeSet_shouldClearTreeSet() {
    Set<String> clearTreeSet = new TreeSet<>();
    clearTreeSet.add("String Added");
    clearTreeSet.clear();
    assertTrue(clearTreeSet.isEmpty());
}
```

### Метод size()

**Метод **size()** используется для определения количества элементов, присутствующих в **TreeSet**. Это один из основных методов в **API**:**

```java
@Test
public void whenCheckingTheSizeOfTreeSet_shouldReturnThesize() {
    Set<String> treeSetSize = new TreeSet<>();
    treeSetSize.add("String Added");
    assertEquals(1, treeSetSize.size());
}
```

### Метод isEmpty()

**Метод **isEmpty()** можно использовать, чтобы выяснить, является ли данный экземпляр **TreeSet** пустым или нет:**

```java
@Test
public void whenCheckingForEmptyTreeSet_shouldCheckForEmpty() {
    Set<String> emptyTreeSet = new TreeSet<>();
    assertTrue(emptyTreeSet.isEmpty());
}
```

### Метод iterator()

Метод **iterator()** возвращает итератор, выполняющий итерацию в порядке возрастания элементов в наборе. Эти итераторы отказоустойчивы.

**Мы можем наблюдать восходящий порядок итераций здесь:**

```java
@Test
public void whenIteratingTreeSet_shouldIterateTreeSetInAscendingOrder() {
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add("Second");
    treeSet.add("Third");
    Iterator<String> itr = treeSet.iterator();
    while (itr.hasNext()) {
        System.out.println(itr.next());
    }
}
```

Кроме того, **TreeSet** позволяет нам перебирать набор в порядке убывания.

**Давайте посмотрим, что в действии:**

```java
@Test
public void whenIteratingTreeSet_shouldIterateTreeSetInDescendingOrder() {
    TreeSet<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add("Second");
    treeSet.add("Third");
    Iterator<String> itr = treeSet.descendingIterator();
    while (itr.hasNext()) {
        System.out.println(itr.next());
    }
}
```

Итератор генерирует исключение **ConcurrentModificationException,** если набор изменяется в любое время после создания итератора любым способом, кроме как с помощью метода итератора **remove().**

**Создадим для этого тест:**

```java
@Test(expected = ConcurrentModificationException.class)
public void whenModifyingTreeSetWhileIterating_shouldThrowException() {
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add("Second");
    treeSet.add("Third");
    Iterator<String> itr = treeSet.iterator();
    while (itr.hasNext()) {
        itr.next();
        treeSet.remove("Second");
    }
}
```

**В качестве альтернативы, если бы мы использовали метод удаления итератора, мы бы не столкнулись с исключением:**

```java
@Test
public void whenRemovingElementUsingIterator_shouldRemoveElement() {
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add("Second");
    treeSet.add("Third");
    Iterator<String> itr = treeSet.iterator();
    while (itr.hasNext()) {
        String element = itr.next();
        if (element.equals("Second"))
            itr.remove();
    }
    assertEquals(2, treeSet.size());
}
```

Нет никакой гарантии отказоустойчивого поведения итератора, поскольку невозможно дать какие-либо жесткие гарантии при наличии несинхронизированной параллельной модификации.

Подробнее об этом можно узнать здесь.

### Метод first()

Этот метод возвращает первый элемент из **TreeSet**, если он не пуст. В противном случае выдаётся исключение **NoSuchElementException**.

**Давайте посмотрим пример:**

```java
@Test
public void whenCheckingFirstElement_shouldReturnFirstElement() {
    TreeSet<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    assertEquals("First", treeSet.first());
}
```

### Метод last()

**Аналогично приведенному выше примеру, этот метод вернет последний элемент, если набор не пуст:**

```java
@Test
public void whenCheckingLastElement_shouldReturnLastElement() {
    TreeSet<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add("Last");
    assertEquals("Last", treeSet.last());
}
```

### Метод subSet()

**Этот метод вернет элементы в диапазоне от **fromElement** до **toElement**. Обратите внимание, что **fromElement** является инклюзивным, а **toElement** — исключающим:**

```java
@Test
public void whenUsingSubSet_shouldReturnSubSetElements() {
    SortedSet<Integer> treeSet = new TreeSet<>();
    treeSet.add(1);
    treeSet.add(2);
    treeSet.add(3);
    treeSet.add(4);
    treeSet.add(5);
    treeSet.add(6);
    Set<Integer> expectedSet = new TreeSet<>();
    expectedSet.add(2);
    expectedSet.add(3);
    expectedSet.add(4);
    expectedSet.add(5);
    Set<Integer> subSet = treeSet.subSet(2, 6);
    assertEquals(expectedSet, subSet);
}
```

### Метод headSet()

**Этот метод вернет элементы **TreeSet,** которые меньше указанного элемента:**

```java
@Test
public void whenUsingHeadSet_shouldReturnHeadSetElements() {
    SortedSet<Integer> treeSet = new TreeSet<>();
    treeSet.add(1);
    treeSet.add(2);
    treeSet.add(3);
    treeSet.add(4);
    treeSet.add(5);
    treeSet.add(6);
    Set<Integer> subSet = treeSet.headSet(6);
    assertEquals(subSet, treeSet.subSet(1, 6));
}
```

### Метод tailSet()

**Этот метод вернет элементы **TreeSet,** которые больше или равны указанному элементу:**

```java
@Test
public void whenUsingTailSet_shouldReturnTailSetElements() {
    NavigableSet<Integer> treeSet = new TreeSet<>();
    treeSet.add(1);
    treeSet.add(2);
    treeSet.add(3);
    treeSet.add(4);
    treeSet.add(5);
    treeSet.add(6);
    Set<Integer> subSet = treeSet.tailSet(3);
    assertEquals(subSet, treeSet.subSet(3, true, 6, true));
}
```

### Работа с null

До **Java** 7 можно было добавлять пустые элементы в пустой **TreeSet**.

Однако это посчитали ошибкой. Поэтому **TreeSet** больше не поддерживает добавление **null**.

**Когда мы добавляем элементы в **TreeSet,** элементы сортируются в соответствии с их естественным порядком или в порядке, указанном компаратором. Следовательно, добавление нулевого значения по сравнению с существующими элементами приводит к исключению **NullPointerException,** поскольку нуль нельзя сравнивать ни с каким значением:**

```java
@Test(expected = NullPointerException.class)
public void whenAddingNullToNonEmptyTreeSet_shouldThrowException() {
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("First");
    treeSet.add(null);
}
```

### Сравнение элементов

Элементы, вставленные в **TreeSet,** должны либо реализовывать интерфейс **Comparable,** либо, по крайней мере, быть приняты указанным компаратором. Все такие элементы должны быть взаимно сравнимы, т. е. **e1.`compareTo`(e2)** или **comparator.compare(**e1**, **e2**)** не должны вызывать исключение **ClassCastException**.

**Давайте посмотрим пример:**

```java
class Element {
    private Integer id;

    // getters and setters
}

Comparator<Element> comparator = (ele1, ele2) -> {
    return ele1.getId().compareTo(ele2.getId());
};

@Test
public void whenUsingComparator_shouldSortAndInsertElements() {
    Set<Element> treeSet = new TreeSet<>(comparator);
    Element ele1 = new Element();
    ele1.setId(100);
    Element ele2 = new Element();
    ele2.setId(200);
    treeSet.add(ele1);
    treeSet.add(ele2);
    System.out.println(treeSet);
}
```

### Производительность

По сравнению с **HashSet** производительность **TreeSet** ниже. Такие операции, как добавление, удаление и поиск, занимают время `O(log n)`, в то время как такие операции, как печать n элементов в отсортированном порядке, требуют времени `O(n)`.

**TreeSet** должен быть нашим основным выбором, если мы хотим, чтобы наши записи были отсортированы, поскольку доступ к **TreeSet** и его обход можно осуществлять как в восходящем, так и в нисходящем порядке, а производительность восходящих операций и представлений, вероятно, будет выше, чем у нисходящих.

**Принцип локальности** — это термин, обозначающий явление, при котором часто осуществляется доступ к одним и тем же значениям или связанным местам хранения в зависимости от шаблона доступа к памяти.

Когда мы говорим о **TreeSet:**

1.  Приложение часто обращается к аналогичным данным с одинаковой частотой.
2.  Если две записи находятся рядом с заданным порядком, **TreeSet** помещает их рядом друг с другом в структуре данных и, следовательно, в памяти.
3.  **TreeSet** является структурой данных с большей локальностью, поэтому мы можем сделать вывод в соответствии с принципом локальности, что мы должны отдать предпочтение **TreeSet**, если у нас мало памяти и если мы хотим получить доступ к относительно близким элементам. друг к другу в соответствии с их естественным порядком.

В случае, если данные должны быть прочитаны с жесткого диска (который имеет большую задержку, чем данные, прочитанные из кеша или памяти),** тогда предпочтите **TreeSet,** поскольку он имеет большую локальность.

## Руководство по HashSet

**HashSet** — одна из основных структур данных в **Java Collections API**.

**Напомним наиболее важные аспекты этой реализации:**

1.  Он хранит уникальные элементы и допускает нули
2.  Он поддерживается **HashMap**
3.  Он не поддерживает порядок вставки
4.  Это не потокобезопасно

Обратите внимание, что этот внутренний **HashMap** инициализируется при создании экземпляра **HashSet:**

```java
public HashSet() {
    map = new HashMap<>();
}
```

### Метод add()

Метод **add()** можно использовать для добавления элементов в набор. В контракте метода указано, что элемент будет добавлен только в том случае, если его еще нет в наборе. Если элемент был добавлен, метод возвращает **true**, иначе **false**.

**Мы можем добавить элемент в **HashSet**, например:**

```java
@Test
public void whenAddingElement_shouldAddElement() {
    Set<String> hashset = new HashSet<>();
    assertTrue(hashset.add("String Added"));
}
```

**С точки зрения реализации метод **add** чрезвычайно важен. Детали реализации показывают, как **HashSet** работает внутри и использует метод **put** из **HashMap**:**

```java
public boolean add(E e) {
    return map.put(e, PRESENT) == null;
}
```

Переменная карты является ссылкой на внутреннюю поддержку **HashMap:**

```java
private transient HashMap<E, Object> map;
```

Было бы неплохо сначала ознакомиться с хэш-кодом, чтобы получить подробное представление о том, как элементы организованы в структурах данных на основе хэшей.

**Резюмируя:**

**HashMap** — это массив сегментов с емкостью по умолчанию 16 элементов — каждому сегменту соответствует свое значение хэш-кода.

Если разные объекты имеют одинаковое значение хэш-кода, они сохраняются в одном сегменте.

Если коэффициент загрузки достигнут, создается новый массив, вдвое превышающий размер предыдущего, и все элементы повторно хешируются и перераспределяются между новыми соответствующими корзинами.

Чтобы получить значение, мы хешируем ключ, модифицируем его, а затем переходим к соответствующей корзине и ищем в потенциальном связанном списке, если есть более одного объекта.

### Метод contains()

**Цель метода contains** — проверить, присутствует ли элемент в заданном **HashSet**. Возвращает **true**, если элемент найден, иначе **false**.

Мы можем проверить наличие элемента в **HashSet:**

```java
@Test
public void whenCheckingForElement_shouldSearchForElement() {
    Set<String> hashsetContains = new HashSet<>();
    hashsetContains.add("String Added");
    assertTrue(hashsetContains.contains("String Added"));
}
```

Всякий раз, когда объект передается этому методу, вычисляется хеш-значение. Затем соответствующее местоположение корзины разрешается и проходится.

### Метод remove()

Метод удаляет указанный элемент из набора, если он присутствует. Этот метод возвращает **true,** если набор содержит указанный элемент.

**Давайте посмотрим на рабочий пример:**

```java
@Test
public void whenRemovingElement_shouldRemoveElement() {
    Set<String> removeFromHashSet = new HashSet<>();
    removeFromHashSet.add("String Added");
    assertTrue(removeFromHashSet.remove("String Added"));
}
```

### Метод clear()

Мы используем этот метод, когда собираемся удалить все элементы из набора. Базовая реализация просто удаляет все элементы из базового **HashMap**.

**Давайте посмотрим, что в действии:**

```java
@Test
public void whenClearingHashSet_shouldClearHashSet() {
    Set<String> clearHashSet = new HashSet<>();
    clearHashSet.add("String Added");
    clearHashSet.clear();
    assertTrue(clearHashSet.isEmpty());
}
```

### Метод size()

Это один из основных методов в **API**. Он широко используется, поскольку помогает определить количество элементов, присутствующих в **HashSet**. Базовая реализация просто делегирует вычисление методу **size()** из **HashMap**.

**Давайте посмотрим, что в действии:**

```java
@Test
public void whenCheckingTheSizeOfHashSet_shouldReturnThesize() {
    Set<String> hashSetSize = new HashSet<>();
    hashSetSize.add("String Added");
    assertEquals(1, hashSetSize.size());
}
```

### Метод isEmpty()

**Мы можем использовать этот метод, чтобы выяснить, является ли данный экземпляр **HashSet** пустым или нет. Этот метод возвращает **true,** если набор не содержит элементов:**

```java
@Test
public void whenCheckingForEmptyHashSet_shouldCheckForEmpty() {
    Set<String> emptyHashSet = new HashSet<>();
    assertTrue(emptyHashSet.isEmpty());
}
```

### Метод iterator()

Метод возвращает итератор по элементам в **Set**. Элементы посещаются в произвольном порядке, а итераторы отказоустойчивы.

**Здесь мы можем наблюдать случайный порядок итераций:**

```java
@Test
public void whenIteratingHashSet_shouldIterateHashSet() {
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while(itr.hasNext()) {
        System.out.println(itr.next());
    }
}
```

Если набор изменяется в любой момент после создания итератора каким-либо образом, кроме как с помощью собственного метода удаления итератора, итератор выдаёт исключение **ConcurrentModificationException**.

**Давайте посмотрим, что в действии:**

```java
@Test(expected = ConcurrentModificationException.class)
public void whenModifyingHashSetWhileIterating_shouldThrowException() {
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while (itr.hasNext()) {
        itr.next();
        hashset.remove("Second");
    }
}
```

**В качестве альтернативы, если бы мы использовали метод удаления итератора, мы бы не столкнулись с исключением:**

```java
@Test
public void whenRemovingElementUsingIterator_shouldRemoveElement() {
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while (itr.hasNext()) {
        String element = itr.next();
        if (element.equals("Second"))
            itr.remove();
    }
    assertEquals(2, hashset.size());
}
```

Безотказное поведение итератора не может быть гарантировано, поскольку невозможно дать какие-либо жесткие гарантии при наличии несинхронизированной параллельной модификации.

Отказоустойчивые итераторы генерируют исключение **ConcurrentModificationException** в максимально возможной степени. Следовательно, было бы неправильно писать программу, корректность которой зависела бы от этого исключения.

### Работа с hashCode() и equals()

Когда мы помещаем объект в **HashSet,** он использует значение хэш-кода объекта, чтобы определить, отсутствует ли уже элемент в наборе.

Каждое значение хеш-кода соответствует определенному местоположению корзины, которая может содержать различные элементы, для которых вычисленное значение хеш-функции одинаково. Но два объекта с одинаковым **hashCode** могут не совпадать.

Таким образом, объекты в пределах одного сегмента будут сравниваться с использованием метода **equals().**

### Производительность

На производительность **HashSet** влияют в основном два параметра — его начальная емкость и коэффициент загрузки.

Ожидаемая временная сложность добавления элемента в набор составляет **O(1)**, которая может упасть до **O(n)** в худшем случае (присутствует только одна корзина)** — поэтому важно поддерживать правильную ёмкость **HashSet**.

Важное примечание: начиная с **JDK 8** временная сложность в наихудшем случае составляет **O(**log**\*n**).**

Коэффициент загрузки описывает максимальный уровень заполнения, выше которого необходимо изменить размер набора.

**Мы также можем создать **HashSet** с пользовательскими значениями начальной емкости и коэффициента загрузки:**

```java
Set<String> hashset = new HashSet<>();
Set<String> hashset = new HashSet<>(20);
Set<String> hashset = new HashSet<>(20, 0.5f);
```

В первом случае используются значения по умолчанию — начальная мощность **16** и коэффициент загрузки **0,75**. Во втором мы переопределяем ёмкость по умолчанию, а в третьем мы переопределяем обе.

Низкая начальная емкость уменьшает сложность пространства, но увеличивает частоту повторного хеширования, что является дорогостоящим процессом.

С другой стороны, высокая начальная емкость увеличивает стоимость итерации и начальное потребление памяти.

### Основные правила работы с HashSet

1.  Высокая начальная емкость хороша для большого количества записей в сочетании с небольшим количеством итераций или без них.
2.  Низкая начальная емкость хороша для нескольких записей с большим количеством итераций.
3.  Поэтому очень важно найти правильный баланс между ними. Обычно реализация по умолчанию оптимизирована и работает просто отлично, если мы почувствуем необходимость настроить эти параметры в соответствии с требованиями, нам нужно действовать разумно.

## Лучшие практики

- **Выбор реализации:** для уникальных элементов без порядка — `HashSet`; для сортировки по натуральному порядку или **Comparator** — `TreeSet`; для сохранения порядка вставки — `LinkedHashSet`; для многопоточности — `**ConcurrentHashMap.newKeySet**()` или `CopyOnWriteArraySet` (редкие записи).
- **Неизменяемые множества:** использовать `**Set.of**()` (Java 9+) для константных наборов; не допускает **null**.
- **Инициализация `HashSet`:** задавать начальную ёмкость при известном размере: `**new HashSet**<>(n)` — уменьшает реаллокации; **load factor** по умолчанию `0.75`.
- **Элементы Set:** использовать объекты с корректными `**equals**()` и `**hashCode**()`; для **TreeSet** — реализовывать **Comparable** или передавать **Comparator**; избегать мутабельных элементов, меняющих **hashCode** после добавления.
- **null:** **HashSet** / **LinkedHashSet** допускают один **null**; `TreeSet` — нет; явно документировать политику **null** в **API**.
- **Потоки:** для фильтрации/преобразования использовать **Stream API**; собирать в **Set** через `**Collectors.toSet**()` или `**toUnmodifiableSet**()`; для дедупликации — `**stream**().**distinct**().**collect(**Collectors.`toSet`(**))`.

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
