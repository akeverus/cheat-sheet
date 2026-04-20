---
title: "Java Collections: Map"
description: "Руководство по интерфейсу Map и реализациям: HashMap, TreeMap, LinkedHashMap."
tags:
  - languages
  - java
  - java-collections-map
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Java Collections: Map

Руководство по интерфейсу Map и реализациям: HashMap, TreeMap, LinkedHashMap.

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)

### Обучающие материалы

- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)


## Содержание

- [Руководство по HashMap](#руководство-по-hashmap)
  - [Перебор HashMap](#перебор-hashmap)
  - [Использование классов в качестве ключей](#использование-классов-в-качестве-ключей)
  - [Функциональные методы Java 8](#функциональные-методы-java-8)
  - [Внутренняя работа HashMap](#внутренняя-работа-hashmap)
- [HashMap под капотом](#hashmap-под-капотом)
  - [Операция put()](#операция-put)
  - [Почему Map не расширяет Collection?](#почему-map-не-расширяет-collection)
  - [Работа с null ключами и значениями](#работа-с-null-ключами-и-значениями)
  - [Возвращаемое значение put()](#возвращаемое-значение-put)
  - [Операция get()](#операция-get)
  - [Представления HashMap](#представления-hashmap)
- [Руководство по TreeMap](#руководство-по-treemap)
  - [Использование компаратора](#использование-компаратора)
  - [Специальные операции запросов](#специальные-операции-запросов)
  - [Внутренняя структура TreeMap](#внутренняя-структура-treemap)
  - [Синхронизация](#синхронизация)
  - [Сравнение с другими реализациями Map](#сравнение-с-другими-реализациями-map)
- [TreeMap против HashMap](#treemap-против-hashmap)
  - [Порядок элементов](#порядок-элементов)
  - [Работа с null](#работа-с-null)
  - [Производительность](#производительность)
  - [Дубликаты и синхронизация](#дубликаты-и-синхронизация)
  - [Итоговые рекомендации](#итоговые-рекомендации)
- [Сортировка HashMap](#сортировка-hashmap)
  - [Сортировка по ключу с помощью TreeMap](#сортировка-по-ключу-с-помощью-treemap)
  - [Сортировка по ключам с помощью ArrayList](#сортировка-по-ключам-с-помощью-arraylist)
  - [Сортировка по значениям](#сортировка-по-значениям)
  - [Удаление дубликатов с помощью TreeSet](#удаление-дубликатов-с-помощью-treeset)
  - [Сортировка с помощью Stream API (Java 8+)](#сортировка-с-помощью-stream-api-java-8)
  - [Сортировка с помощью Guava](#сортировка-с-помощью-guava)
- [Перебор Map](#перебор-map)
  - [Использование Entry Set](#использование-entry-set)
  - [Использование KeySet](#использование-keyset)
  - [Использование values()](#использование-values)
  - [Использование Iterator](#использование-iterator)
  - [Использование Lambda (Java 8+)](#использование-lambda-java-8)
  - [Использование Stream API](#использование-stream-api)
- [Инициализация HashMap](#инициализация-hashmap)
  - [Статический блок кода](#статический-блок-кода)
  - [Singleton Map и Empty Map](#singleton-map-и-empty-map)
  - [Инициализация с помощью Stream API (Java 8+)](#инициализация-с-помощью-stream-api-java-8)
  - [Фабричные методы Java 9](#фабричные-методы-java-9)
  - [Инициализация с помощью Guava](#инициализация-с-помощью-guava)
- [Лучшие практики](#лучшие-практики)

## Руководство по HashMap

Давайте сначала посмотрим, что означает, что **HashMap** является картой. Карта — это сопоставление ключ-значение, что означает, что каждый ключ сопоставляется ровно с одним значением и что мы можем использовать ключ для извлечения соответствующего значения из карты.

Можно спросить, почему бы просто не добавить значение в список. Зачем нам нужен **HashMap**? Причина проста: производительность. Если мы хотим найти конкретный элемент в списке, временная сложность будет **O(n)**, а если список отсортирован, то будет **O(log n)** с использованием, например, бинарного поиска.

Преимущество **HashMap** заключается в том, что временная сложность вставки и извлечения значения составляет в среднем **O(1).** Мы рассмотрим, как этого можно достичь позже. Давайте сначала посмотрим, как использовать **HashMap.**

**Давайте создадим простой класс, который будем использовать на протяжении всей статьи:**

```java
// Модель товара с методом объединения тегов (для примеров с Map)
public class Product {
    private String name;
    private String description;
    private List<String> tags;

    public Product addTagsOfOtherProduct(Product product) {
        this.tags.addAll(product.getTags());
        return this;
    }
}
```

Теперь мы можем создать **HashMap** с ключом типа **String** и элементами типа **Product:**

```java
// Карта: имя товара → объект Product
Map<String, Product> productsByName = new HashMap<>();
```

И добавляем товары в наш **HashMap:**

```java
// put(ключ, значение) — добавление пары в карту
Product eBike = new Product("E-Bike", "A bike with a battery");
Product roadBike = new Product("Road bike", "A bike for competition");
productsByName.put(eBike.getName(), eBike);
productsByName.put(roadBike.getName(), roadBike);
```

**Мы можем получить значение из карты по ее ключу:**

```java
// get(ключ) — извлечение значения по ключу
Product nextPurchase = productsByName.get("E-Bike");
assertEquals("A bike with a battery", nextPurchase.getDescription());
```

**Если мы попытаемся найти значение для ключа, которого нет на карте, мы получим нулевое значение:**

```java
// Отсутствующий ключ → get() возвращает null
Product nextPurchase = productsByName.get("Car");
assertNull(nextPurchase);
```

**И если мы вставим второе значение с тем же ключом, мы получим только последнее вставленное значение для этого ключа:**

```java
// Замена значения по тому же ключу (put перезаписывает)
Product newEBike = new Product("E-Bike", "A bike with a better battery");
productsByName.put(newEBike.getName(), newEBike);
assertEquals("A bike with a better battery", productsByName.get("E-Bike").getDescription());
```

**HashMap** также позволяет нам использовать **null** в качестве ключа:**

```java
// HashMap допускает один ключ null
Product defaultProduct = new Product("Chocolate", "At least buy chocolate");
productsByName.put(null, defaultProduct);
Product nextPurchase = productsByName.get(null);
assertEquals("At least buy chocolate", nextPurchase.getDescription());
```

**Кроме того, мы можем дважды вставить один и тот же объект с другим ключом:**

```java
// Один объект может быть доступен и по null, и по строковому ключу (если так положили)
productsByName.put(defaultProduct.getName(), defaultProduct);
assertSame(productsByName.get(null), productsByName.get("Chocolate"));
```

Мы можем удалить сопоставление ключ-значение из **HashMap:**

```java
// remove(ключ) — удаление пары по ключу
productsByName.remove("E-Bike");
assertNull(productsByName.get("E-Bike"));
```

Чтобы проверить, присутствует ли ключ на карте, мы можем использовать метод **containsKey():**

```java
// Проверка наличия ключа в карте
productsByName.containsKey("E-Bike");
```

Или, чтобы проверить, присутствует ли значение на карте, мы можем использовать метод **containsValue():**

```java
// Проверка наличия значения (линейный обход)
productsByName.containsValue(eBike);
```

В нашем примере вызовы обоих методов вернут **true**. Хотя они выглядят очень похожими, между вызовами этих двух методов есть важная разница в производительности. Сложность проверки существования ключа составляет **O(1)**, а сложность проверки элемента — **O(n)**, так как необходимо перебрать все элементы на карте.

### Перебор HashMap

Существует три основных способа перебора всех пар ключ-значение в **HashMap.**

**Мы можем перебрать набор всех ключей:**

```java
// Перебор по keySet() и получение значения через get()
for(String key: productsByName.keySet()) {
    Product product = productsByName.get(key);
}
```

**Или мы можем перебрать набор всех записей:**

```java
// Перебор пар ключ-значение через entrySet() (предпочтительно)
for(Map.Entry<String, Product> entry: productsByName.entrySet()) {
    Product product = entry.getValue();
    String key = entry.getKey();
}
```

**Наконец, мы можем перебрать все значения:**

```java
// Получение коллекции всех значений (без ключей)
List<Product> products = new ArrayList<>(productsByName.values());
```

### Использование классов в качестве ключей

**Мы можем использовать любой класс в качестве ключа в нашей **HashMap.** Однако, чтобы карта работала правильно, нам нужно предоставить реализацию для **equals**() и **hashCode**(). Допустим, мы хотим иметь карту с продуктом в качестве ключа и ценой в качестве значения:**

```java
// Ключ — объект Product; для корректной работы нужны equals/hashCode
HashMap<Product, Integer> priceByProduct = new HashMap<>();
priceByProduct.put(eBike, 900);
```

Давайте реализуем методы `equals()` и **hashCode():**

```java
// Контракт equals/hashCode обязателен для ключей Map
@Override
public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    Product product = (Product) o;
    return Objects.equals(name, product.name) &&
           Objects.equals(description, product.description);
}

@Override
public int hashCode() {
    return Objects.hash(name, description);
}
```

Обратите внимание, что **hashCode()** и `equals()` необходимо переопределять только для классов, которые мы хотим использовать в качестве ключей карты, а не для классов, которые используются только как значения в карте.

### Функциональные методы Java 8

**Java 8** добавила в **HashMap** методы функционального стиля:

Для каждого метода мы рассмотрим два примера. В первом примере показано, как использовать новый метод, а во втором примере показано, как добиться того же в более ранних версиях **Java.**

Поскольку эти методы довольно просты, мы не будем рассматривать более подробные примеры.

**Метод `forEach` -** это функциональный способ перебора всех элементов карты:**

```java
// Java 8: перебор пар ключ-значение через forEach(BiConsumer)
productsByName.forEach((key, product) -> {
    System.out.println("Key: " + key + " Product:" + product.getDescription());
});
```

До **Java 8:**

```java
for(Map.Entry<String, Product> entry: productsByName.entrySet()) {
    Product product = entry.getValue();
    String key = entry.getKey();
}
```

В нашей статье «Руководство по **Java 8 forEach**» цикл **forEach** рассматривается более подробно.

**Используя метод **getOrDefault()**, мы можем получить значение из карты или вернуть элемент по умолчанию, если для данного ключа нет сопоставления:**

```java
// getOrDefault(ключ, значениеПоУмолчанию) — без проверки containsKey
Product chocolate = new Product("chocolate", "something sweet");
Product defaultProduct = productsByName.getOrDefault("horse carriage", chocolate);
Product bike = productsByName.getOrDefault("E-Bike", chocolate);
```

До **Java 8:**

```java
Product bike2 = productsByName.containsKey("E-Bike") ? productsByName.get("E-Bike") : chocolate;
Product defaultProduct2 = productsByName.containsKey("horse carriage") ? productsByName.get("horse carriage") : chocolate;
```

**С помощью этого метода мы можем добавить новое сопоставление, но только если для данного ключа еще нет сопоставления:**

```java
productsByName.putIfAbsent("E-Bike", chocolate);
```

До **Java 8:**

```java
if (productsByName.containsKey("E-Bike")) {
    productsByName.put("E-Bike", chocolate);
}
```

**И с помощью **merge()** мы можем изменить значение для данного ключа, если сопоставление существует, или добавить новое значение в противном случае:**

```java
Product eBike2 = new Product("E-Bike", "A bike with a battery");
eBike2.getTags().add("sport");
productsByName.merge("E-Bike", eBike2, Product::addTagsOfOtherProduct);
```

До **Java 8:**

```java
if (productsByName.containsKey("E-Bike")) {
    productsByName.get("E-Bike").addTagsOfOtherProduct(eBike2);
} else {
    productsByName.put("E-Bike", eBike2);
}
```

**С помощью метода **compute()** мы можем вычислить значение для данного ключа:**

```java
productsByName.compute("E-Bike", (k,v) -> {
    if (v != null) {
        return v.addTagsOfOtherProduct(eBike2);
    } else {
        return eBike2;
    }
});
```

До **Java 8:**

```java
if (productsByName.containsKey("E-Bike")) {
    productsByName.get("E-Bike").addTagsOfOtherProduct(eBike2);
} else {
    productsByName.put("E-Bike", eBike2);
}
```

Стоит отметить, что методы **merge()** и **compute()** очень похожи. Метод **compute()** принимает два аргумента: ключ и **BiFunction** для переназначения. И **merge()** принимает три параметра: ключ, значение по умолчанию для добавления к карте, если ключ еще не существует, и **BiFunction** для переназначения.

### Внутренняя работа HashMap

Как мы видели, мы можем получить элемент из **HashMap** по его ключу. Один из подходов — использовать список, перебирать все элементы и возвращаться, когда мы находим элемент, для которого совпадает ключ. Как временная, так и пространственная сложность этого подхода будут **O(n).**

С помощью **HashMap** мы можем достичь средней временной сложности **O(1)** для операций ввода и получения и пространственной сложности **O(n).** Давайте посмотрим, как это работает.

Вместо перебора всех своих элементов **HashMap** пытается вычислить положение значения на основе его ключа.

Наивным подходом было бы иметь список, который может содержать столько элементов, сколько возможно ключей. В качестве примера предположим, что наш ключ — это символ нижнего регистра. Тогда достаточно иметь список размером **26**, и если мы хотим получить доступ к элементу с помощью ключа «**c**», мы будем знать, что это элемент в позиции **3**, и мы можем получить его напрямую.

Однако этот подход не был бы очень эффективным, если бы у нас было гораздо большее пространство ключей. Например, предположим, что наш ключ был целым числом. В этом случае размер списка должен быть **2 `147 483` 647**. В большинстве случаев у нас также было бы гораздо меньше элементов, поэтому большая часть выделенной памяти оставалась бы неиспользованной.

**HashMap** хранит элементы в так называемых сегментах, а количество сегментов называется ёмкостью.

Когда мы помещаем значение на карту, метод **hashCode()** ключа используется для определения **bucket**, в которой будет храниться значение.

Чтобы получить значение, **HashMap** вычисляет **bucket** таким же образом — с помощью **hashCode()**. Затем он перебирает объекты, найденные в этом сегменте, и использует метод `equals()` ключа, чтобы найти точное совпадение.

В большинстве случаев мы должны использовать неизменяемые ключи. Или, по крайней мере, мы должны знать о последствиях использования изменяемых ключей.

Давайте посмотрим, что произойдет, когда наш ключ изменится после того, как мы использовали его для сохранения значения на карте.

В этом примере мы создадим **MutableKey:**

```java
public class MutableKey {
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutableKey that = (MutableKey) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

**А вот и тест:**

```java
MutableKey key = new MutableKey("initial");
Map<MutableKey, String> items = new HashMap<>();
items.put(key, "success");
key.setName("changed");
assertNull(items.get(key));
```

Как мы видим, мы больше не можем получить соответствующее значение после изменения ключа, вместо этого возвращается **null**. Это связано с тем, что **HashMap** ищет не в том сегменте.

Приведенный выше тестовый пример может показаться неожиданным, если у нас нет хорошего понимания того, как **HashMap** работает внутри.

Чтобы это работало правильно, одинаковые ключи должны иметь одинаковый хеш, однако разные ключи могут иметь один и тот же хэш. Если два разных ключа имеют одинаковый хэш, два принадлежащих им значения будут храниться в одном сегменте. Внутри **bucket** значения хранятся в списке и извлекаются путем циклического перебора всех элементов. Стоимость этого `O(n)`.

Начиная с **Java** 8 (см. `JEP 180` ), структура данных, в которой хранятся значения внутри одного сегмента, изменяется со списка на сбалансированное дерево, если сегмент содержит 8 или более значений, и обратно на список, если в момент в какой-то момент в **bucket** осталось только 6 значений. Это повышает производительность до `O(log n)`.

Чтобы избежать большого количества сегментов с несколькими значениями, емкость удваивается, если 75% (коэффициент загрузки) сегментов становятся непустыми. Значение по умолчанию для коэффициента загрузки составляет 75%, а начальная мощность по умолчанию — 16. Оба параметра можно установить в конструкторе.

Подытожим, как работают операции **put** и **get**.

Когда мы добавляем элемент на карту, **HashMap** вычисляет **bucket**. Если **bucket** уже содержит значение, оно добавляется в список (или дерево), принадлежащее этому **bucket**. Если коэффициент загрузки становится больше, чем максимальный коэффициент загрузки карты, вместимость удваивается.

Когда мы хотим получить значение из карты, **HashMap** вычисляет **bucket** и получает значение с тем же ключом из списка (или дерева).

## HashMap под капотом

В этой статье мы собираемся более подробно изучить самую популярную реализацию интерфейса **Map** из **Java Collections Framework**, продолжая с того места, на котором остановилась наша вступительная статья.

Прежде чем мы приступим к реализации, важно отметить, что основные интерфейсы коллекций **List** и **Set** расширяют **Collection**, а **Map** — нет.

Проще говоря, **HashMap** хранит значения по ключу и предоставляет **API** для добавления, извлечения и обработки сохраненных данных различными способами. Реализация основана на принципах хеш-таблицы, которые на первый взгляд кажутся сложными, но на самом деле их очень легко понять.

Пары ключ-значение хранятся в так называемых **bucket**, которые вместе составляют то, что называется таблицей, которая на самом деле является внутренним массивом.

Как только мы узнаем ключ, под которым хранится или должен храниться объект, операции хранения и извлечения выполняются за постоянное время, `O(1)` в хеш-карте с хорошими размерами.

Чтобы понять, как работают хеш-карты внутри, нужно понять механизм хранения и поиска, используемый **HashMap**. Мы сосредоточимся на них.

Наконец, вопросы, связанные с **HashMap**, довольно часто встречаются на собеседованиях, так что это хороший способ либо подготовиться к интервью, либо подготовиться к нему.

### Операция put()

**Чтобы сохранить значение в хэш-карте, мы вызываем **API-**интерфейс **put**, который принимает два параметра; ключ и соответствующее значение:**

```java
V put(K key, V value);
```

Когда значение добавляется к карте под ключом, вызывается **API-**интерфейс **hashCode()** объекта ключа для получения так называемого начального хеш-значения.

**Чтобы увидеть это в действии, давайте создадим объект, который будет действовать как ключ. Мы создадим только один атрибут для использования в качестве хэш-кода для имитации первой фазы хеширования:**

```java
public class MyKey {
    private int id;

    @Override
    public int hashCode() {
        System.out.println("Calling hashCode()");
        return id;
    }
}
```

**Теперь мы можем использовать этот объект для отображения значения в хэш-карте:**

```java
@Test
public void whenHashCodeIsCalledOnPut_thenCorrect() {
    MyKey key = new MyKey(1);
    Map<MyKey, String> map = new HashMap<>();
    map.put(key, "val");
}
```

В приведенном выше коде ничего особенного не происходит, но обратите внимание на вывод консоли. Действительно вызывается метод **hashCode:**

```text
Calling hashCode()
```

Затем вызывается внутренний **API hash()** карты хэшей для вычисления конечного значения хеш-функции с использованием начального значения хэш-функции.

Это окончательное хеш-значение в конечном итоге сводится к индексу во внутреннем массиве или к тому, что мы называем местоположением **bucket.**

**Хэш- функция **HashMap** выглядит так:**

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

Здесь следует отметить только использование хеш-кода из ключевого объекта для вычисления окончательного хеш-значения.

**Находясь внутри функции **put**, конечное значение хеш-функции используется следующим образом:**

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
```

Обратите внимание, что вызывается внутренняя функция **putVal**, которой в качестве первого параметра передаётся окончательное значение хеш-функции.

Можно задаться вопросом, почему ключ снова используется внутри этой функции, ведь мы уже использовали его для вычисления хеш-значения.

Причина в том, что хеш-карты хранят как ключ, так и значение в расположении **bucket** как объект **Map.`Entry`.**

### Почему Map не расширяет Collection?

**Как обсуждалось ранее, все интерфейсы фреймворка коллекций **Java** расширяют интерфейс **Collection**, а **Map** — нет. Сравните объявление интерфейса **Map**, которое мы видели ранее, с объявлением интерфейса **Set**:**

```java
public interface Set<E> extends Collection<E>
```

Причина в том, что карты хранят не отдельные элементы, как другие коллекции, а набор пар ключ-значение.

Таким образом, общие методы интерфейса **Collection**, такие как **add**, **toArray**, не имеют смысла, когда дело доходит до **Map**.

Концепция, которую мы рассмотрели в последних трёх абзацах, является одним из самых популярных вопросов на собеседовании по **Java Collections Framework**. Итак, стоит понять.

### Работа с null ключами и значениями

**Одним из специальных атрибутов хэш-карты является то, что она принимает нулевые значения и нулевые ключи:**

```java
@Test
public void givenNullKeyAndVal_whenAccepts_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put(null, null);
}
```

Когда во время операции **put** встречается нулевой ключ, ему автоматически присваивается конечное хэш-значение **0**, что означает, что он становится первым элементом базового массива.

Это также означает, что когда ключ имеет значение **null**, операции хеширования не выполняется, и, следовательно, **API-**интерфейс **hashCode** ключа не вызывается, что в конечном итоге позволяет избежать исключения нулевого указателя.

### Возвращаемое значение put()

**Во время операции размещения, когда мы используем ключ, который уже использовался ранее для хранения значения, он возвращает предыдущее значение, связанное с ключом:**

```java
@Test
public void givenExistingKey_whenPutReturnsPrevValue_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("key1", "val1");
    String rtnVal = map.put("key1", "val2");
    assertEquals("val1", rtnVal);
}
```

**В противном случае возвращается ноль:**

```java
@Test
public void givenNewKey_whenPutReturnsNull_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    String rtnVal = map.put("key1", "val1");
    assertNull(rtnVal);
}
```

**Когда **put** возвращает **null**, это также может означать, что предыдущее значение, связанное с ключом, равно **null**, не обязательно что это новое сопоставление ключ-значение:**

```java
@Test
public void givenNullVal_whenPutReturnsNull_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    String rtnVal = map.put("key1", null);
    assertNull(rtnVal);
}
```

**API containsKey** можно использовать для различения таких сценариев, как мы увидим в следующем подразделе.

### Операция get()

**Чтобы получить объект, уже сохраненный в хэш-карте, мы должны знать ключ, под которым он был сохранен. Вызываем **get API** и передаем ему ключевой объект:**

```java
@Test
public void whenGetWorks_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("key", "val");
    String val = map.get("key");
    assertEquals("val", val);
}
```

**Внутри используется тот же принцип хеширования. Метод **hashCode()** ключевого объекта вызывается для получения начального хеш-значения:**

```java
@Test
public void whenHashCodeIsCalledOnGet_thenCorrect() {
    MyKey key = new MyKey(1);
    Map<MyKey, String> map = new HashMap<>();
    map.put(key, "val");
    map.get(key);
}
```

**На этот раз API hashCode для MyKey** вызывается дважды; один раз для **put** и один раз для **get**:**

```text
Calling hashCode()
Calling hashCode()
```

Затем это значение повторно хэшируется путём вызова внутреннего **API-**интерфейса **hash()** для получения окончательного хеш-значения.

Как мы видели в предыдущем разделе, это окончательное хеш-значение в конечном итоге сводится к местоположению **bucket** или индексу внутреннего массива.

Затем объект значения, хранящийся в этом месте, извлекается и возвращается в вызывающую функцию.

**Когда возвращаемое значение равно **null**, это может означать, что ключевой объект не связан ни с каким значением в хэш-карте:**

```java
@Test
public void givenUnmappedKey_whenGetReturnsNull_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    String rtnVal = map.get("key1");
    assertNull(rtnVal);
}
```

**Или это может просто означать, что ключ был явно сопоставлен с нулевым экземпляром:**

```java
@Test
public void givenNullVal_whenRetrieves_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("key", null);
    String val = map.get("key");
    assertNull(val);
}
```

**Чтобы различать эти два сценария, мы можем использовать **containsKey** (API), которому мы передаём ключ, и он возвращает **true** тогда и только тогда, когда для указанного ключа в хэш-карте было создано сопоставление:**

```java
@Test
public void whenContainsDistinguishesNullValues_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    String val1 = map.get("key");
    boolean valPresent = map.containsKey("key");
    assertNull(val1);
    assertFalse(valPresent);

    map.put("key", null);
    String val = map.get("key");
    valPresent = map.containsKey("key");
    assertNull(val);
    assertTrue(valPresent);
}
```

В обоих случаях в приведенном выше тесте возвращаемое значение вызова **API get** равно **null**, но мы можем различить, какое из них есть какое.

### Представления HashMap

**HashMap** предлагает три представления, которые позволяют нам рассматривать его ключи и значения как другую коллекцию. Мы можем получить набор всех ключей карты:**

```java
@Test
public void givenHashMap_whenRetrievesKeyset_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    Set<String> keys = map.keySet();
    assertEquals(2, keys.size());
    assertTrue(keys.contains("name"));
    assertTrue(keys.contains("type"));
}
```

**Набор поддерживается самой картой. Таким образом, любое изменение, внесенное в набор, отражается на карте:**

```java
@Test
public void givenKeySet_whenChangeReflectsInMap_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    assertEquals(2, map.size());
    Set<String> keys = map.keySet();
    keys.remove("name");
    assertEquals(1, map.size());
}
```

**Мы также можем получить представление коллекции значений:**

```java
@Test
public void givenHashMap_whenRetrievesValues_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    Collection<String> values = map.values();
    assertEquals(2, values.size());
    assertTrue(values.contains("baeldung"));
    assertTrue(values.contains("blog"));
}
```

Как и в случае с набором ключей, любые изменения, внесенные в эту коллекцию, будут отражены в базовой карте.

**Наконец, мы можем получить установленное представление всех записей на карте:**

```java
@Test
public void givenHashMap_whenRetrievesEntries_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    Set<Entry<String, String>> entries = map.entrySet();
    assertEquals(2, entries.size());
    for (Entry<String, String> e: entries) {
        String key = e.getKey();
        String val = e.getValue();
        assertTrue(key.equals("name") || key.equals("type"));
        assertTrue(val.equals("baeldung") || val.equals("blog"));
    }
}
```

Помните, что хеш-карта специально содержит неупорядоченные элементы, поэтому мы предполагаем любой порядок при тестировании ключей и значений записей в цикле **for each**.

Много раз вы будете использовать представления коллекций в цикле, как в последнем примере, и, более конкретно, используя их итераторы.

Просто помните, что итераторы для всех вышеперечисленных представлений отказоустойчивы.

**Если на карте будет произведена какая-либо структурная модификация, после создания итератора будет выдано исключение параллельной модификации:**

```java
@Test(expected = ConcurrentModificationException.class)
public void givenIterator_whenFailsFastOnModification_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    Set<String> keys = map.keySet();
    Iterator<String> it = keys.iterator();
    map.remove("type");
    while (it.hasNext()) {
        String key = it.next();
    }
}
```

**Единственная допустимая структурная модификация — это операция удаления, выполняемая через сам итератор:**

```java
@Test
public void givenIterator_whenRemoveWorks_thenCorrect() {
    Map<String, String> map = new HashMap<>();
    map.put("name", "baeldung");
    map.put("type", "blog");
    Set<String> keys = map.keySet();
    Iterator<String> it = keys.iterator();
    while (it.hasNext()) {
        it.next();
        it.remove();
    }
    assertEquals(0, map.size());
}
```

## Руководство по TreeMap

В этой статье мы собираемся изучить реализацию **TreeMap** интерфейса карты из **Java `Collections Framework` (JCF).**

**TreeMap** — это реализация карты, которая сортирует свои записи в соответствии с естественным порядком своих ключей или, что еще лучше, с использованием компаратора, если он предоставляется пользователем во время построения.

Ранее мы рассмотрели реализации **HashMap** и **LinkedHashMap**, и мы понимаем, что существует довольно много схожей информации о том, как работают эти классы.

Упомянутые статьи настоятельно рекомендуется прочитать, прежде чем приступить к этой.

По умолчанию **TreeMap** сортирует все свои записи в соответствии с их естественным порядком. Для целого числа это будет означать возрастающий порядок, а для строк — алфавитный порядок.

**Давайте посмотрим на естественный порядок в тесте:**

```java
@Test
public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect() {
    TreeMap<Integer, String> map = new TreeMap<>();
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");
    assertEquals("[1, 2, 3, 4, 5]", map.keySet().toString());
}
```

Обратите внимание, что мы разместили целочисленные ключи неупорядоченным образом, но при получении набора ключей мы подтверждаем, что они действительно поддерживаются в порядке возрастания. Это естественный порядок целых чисел.

**Точно так же, когда мы используем строки, они будут отсортированы в естественном порядке, то есть в алфавитном порядке:**

```java
@Test
public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect2() {
    TreeMap<String, String> map = new TreeMap<>();
    map.put("c", "val");
    map.put("b", "val");
    map.put("a", "val");
    map.put("e", "val");
    map.put("d", "val");
    assertEquals("[a, b, c, d, e]", map.keySet().toString());
}
```

**TreeMap**, в отличие от хеш-карты и связанной хэш-карты, нигде не использует принцип хеширования, поскольку не использует массив для хранения своих записей.

### Использование компаратора

Если нас не устраивает естественное упорядочение **TreeMap**, мы также можем определить собственное правило упорядочения с помощью компаратора при построении древовидной карты.

**В приведенном ниже примере мы хотим, чтобы целые ключи были упорядочены в порядке убывания:**

```java
@Test
public void givenTreeMap_whenOrdersEntriesByComparator_thenCorrect() {
    TreeMap<Integer, String> map = new TreeMap<>(Comparator.reverseOrder());
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");
    assertEquals("[5, 4, 3, 2, 1]", map.keySet().toString());
}
```

Хэш-карта не гарантирует порядок хранения ключей и, в частности, не гарантирует, что этот порядок останется неизменным с течением времени, но древовидная карта гарантирует, что ключи всегда будут сортироваться в соответствии с указанным порядком.

### Специальные операции запросов

Теперь мы знаем, что **TreeMap** хранит все свои записи в отсортированном порядке. Из-за этого атрибута древовидных карт мы можем выполнять такие запросы, как; найти **«самый большой»**, найти **«самый маленький»**, найти все ключи меньше или больше определённого значения и т. д.

**Приведенный ниже код охватывает лишь небольшой процент таких случаев:**

```java
@Test
public void givenTreeMap_whenPerformsQueries_thenCorrect() {
    TreeMap<Integer, String> map = new TreeMap<>();
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");
    Integer highestKey = map.lastKey();
    Integer lowestKey = map.firstKey();
    Set<Integer> keysLessThan3 = map.headMap(3).keySet();
    Set<Integer> keysGreaterThanEqTo3 = map.tailMap(3).keySet();
    assertEquals(new Integer(5), highestKey);
    assertEquals(new Integer(1), lowestKey);
    assertEquals("[1, 2]", keysLessThan3.toString());
    assertEquals("[3, 4, 5]", keysGreaterThanEqTo3.toString());
}
```

### Внутренняя структура TreeMap

**TreeMap** реализует интерфейс **NavigableMap** и основывает свою внутреннюю работу на принципах красно-черных деревьев:**

```java
public class TreeMap<K,V> extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
```

Принцип красно-черных деревьев выходит за рамки этой статьи, однако есть ключевые моменты, которые нужно помнить, чтобы понять, как они вписываются в **TreeMap.**

Во-первых, красно-черное дерево — это структура данных, состоящая из узлов; представьте перевернутое манговое дерево с корнями в небе и ветвями, растущими вниз. Корень будет содержать первый элемент, добавленный в дерево.

Правило состоит в том, что, начиная с корня, любой элемент в левой ветви любого узла всегда меньше, чем элемент в самом узле. Те, что справа, всегда больше. То, что определяет больше или меньше, определяется естественным порядком элементов или определенным компаратором при построении, как мы видели ранее.

Это правило гарантирует, что элементы карты дерева всегда будут отсортированы и предсказуемы.

Во-вторых, красно-черное дерево — это самобалансирующееся бинарное дерево поиска. Этот атрибут и описанное выше гарантируют, что основные операции, такие как поиск, получение, размещение и удаление, занимают логарифмическое время **O(log n).**

Ключевым моментом здесь является самобалансирование. Продолжая вставлять и удалять записи, представьте, что дерево становится длиннее с одной стороны и короче с другой.

Это означало бы, что операция займет меньше времени на более короткой ветви и больше времени на ветви, наиболее удаленной от корня, чего мы не хотели бы.

Поэтому об этом позаботились при оформлении красно-черных деревьев. Для каждой вставки и удаления максимальная высота дерева на любом ребре поддерживается на уровне **O(log n)**, т. е. дерево непрерывно уравновешивается.

### Синхронизация

Так же, как хэш-карта и связанная хэш-карта, древовидная карта не синхронизируется, и поэтому правила ее использования в многопоточной среде аналогичны правилам в двух других реализациях карты.

### Сравнение с другими реализациями Map

Посмотрев на реализации **HashMap** и **LinkedHashMap** ранее, а теперь и на **TreeMap**, важно провести краткое сравнение между ними, чтобы понять, какая из них подходит.

Хэш-карта хороша как реализация карты общего назначения, обеспечивающая быстрое хранение и поиск. Однако он терпит неудачу из-за хаотичного и беспорядочного расположения записей.

Это приводит к тому, что он плохо работает в сценариях с большим количеством итераций, поскольку вся емкость базового массива влияет на обход, а не только на количество записей.

Связанная хэш-карта обладает хорошими атрибутами хэш-карт и упорядочивает записи. Он работает лучше при большом количестве итераций, поскольку учитывается только количество записей независимо от ёмкости.

Карта дерева выводит упорядочение на новый уровень, предоставляя полный контроль над тем, как должны быть отсортированы ключи. С другой стороны, он предлагает худшую общую производительность, чем два других варианта.

Можно сказать, что связанная хеш-карта уменьшает хаос в упорядочении хэш-карты, не подвергаясь при этом снижению производительности древовидной карты.

## TreeMap против HashMap

Сначала мы поговорим о **HashMap**, которая представляет собой реализацию на основе хеш-таблиц. Он расширяет класс **AbstractMap** и реализует интерфейс **Map**. **HashMap** работает по принципу хеширования.

Эта реализация **Map** обычно действует как хеш-таблица с сегментами, но когда сегменты становятся слишком большими, они преобразуются в узлы **TreeNodes**, каждый из которых структурирован аналогично узлам в **java.util.`TreeMap`.**

Вы можете узнать больше о внутренностях **HashMap** в статье, посвящённой ему.

С другой стороны, **TreeMap** расширяет класс **AbstractMap** и реализует интерфейс **NavigableMap**. **TreeMap** хранит элементы карты в красно-чёрном дереве, которое является самобалансирующимся двоичным деревом поиска.

И вы также можете найти больше о внутренностях **TreeMap** в статье, посвящённой этому здесь.

### Порядок элементов

**HashMap** не даёт никаких гарантий относительно расположения элементов на карте.

Это означает, что мы не можем принимать какой-либо порядок при переборе ключей и значений **HashMap:**

```java
@Test
public void whenInsertObjectsHashMap_thenRandomOrder() {
    Map<Integer, String> hashmap = new HashMap<>();
    hashmap.put(3, "TreeMap");
    hashmap.put(2, "vs");
    hashmap.put(1, "HashMap");
    assertThat(hashmap.keySet(), containsInAnyOrder(1, 2, 3));
}
```

Однако элементы в **TreeMap** сортируются в соответствии с их естественным порядком.

**Если объекты **TreeMap** не могут быть отсортированы в соответствии с естественным порядком, мы можем использовать **Comparator** или **Comparable**, чтобы определить порядок, в котором элементы расположены на карте:**

```java
@Test
public void whenInsertObjectsTreeMap_thenNaturalOrder() {
    Map<Integer, String> treemap = new TreeMap<>();
    treemap.put(3, "TreeMap");
    treemap.put(2, "vs");
    treemap.put(1, "HashMap");
    assertThat(treemap.keySet(), contains(1, 2, 3));
}
```

### Работа с null

**HashMap** позволяет хранить не более одного нулевого ключа и множество нулевых значений.

**Давайте посмотрим пример:**

```java
@Test
public void whenInsertNullInHashMap_thenInsertsNull() {
    Map<Integer, String> hashmap = new HashMap<>();
    hashmap.put(null, null);
    assertNull(hashmap.get(null));
}
```

Однако **TreeMap** не допускает нулевой ключ, но может содержать множество нулевых значений.

Нулевой ключ не разрешен, потому что метод **compareTo()** или **compare()** создает исключение **NullPointerException:**

```java
@Test(expected = NullPointerException.class)
public void whenInsertNullInTreeMap_thenException() {
    Map<Integer, String> treemap = new TreeMap<>();
    treemap.put(null, "NullPointerException");
}
```

Если мы используем **TreeMap** с определяемым пользователем **Comparator**, то способ обработки нулевых значений зависит от реализации метода **compare().**

### Производительность

**Производительность** — наиболее важная метрика, которая помогает нам понять пригодность структуры данных для данного варианта использования.

В этом разделе мы предоставим всесторонний анализ производительности для **HashMap** и **TreeMap.**

**HashMap**, будучи реализацией на основе хеш-таблиц, использует структуру данных на основе массива для организации своих элементов в соответствии с хеш-функцией.

**HashMap** обеспечивает ожидаемую производительность с постоянным временем **O(1)** для большинства операций, таких как **add(), remove()** и **contains().** Следовательно, это значительно быстрее, чем **TreeMap.**

**Среднее время поиска элемента при разумном предположении в хеш-таблице составляет **O(1).** Но неправильная реализация хеш-функции может привести к плохому распределению значений в сегментах, что приводит к:**

**Накладные расходы памяти -** многие сегменты остаются неиспользованными

**Ухудшение производительности** — чем больше количество столкновений, тем ниже производительность.

До **Java 8** раздельная цепочка была единственным предпочтительным способом обработки коллизий. Обычно это реализуется с использованием связанных списков, т. е. если есть какое-либо столкновение или два разных элемента имеют одинаковое хэш-значение, то сохраняйте оба элемента в одном и том же связанном списке.

Следовательно, поиск элемента в **HashMap** в худшем случае мог занять столько же времени, сколько поиск элемента в связанном списке, т. е. **O(n)** времени.

Однако с появлением **JEP 180** произошли небольшие изменения в реализации способа расположения элементов в **HashMap**.

Согласно спецификации, когда сегменты становятся слишком большими и содержат достаточное количество узлов, они преобразуются в режимы **TreeNodes**, каждый из которых структурирован так же, как в **TreeMap.**

Следовательно, в случае коллизий с большим количеством хэшей производительность в наихудшем случае улучшилась с **O(n)** до **O(log n).**

**Код, выполняющий это преобразование, показан ниже:**

```java
if (binCount >= TREEIFY_THRESHOLD - 1) {
    treeifyBin(tab, hash);
}
```

Значение для **TREEIFY_THRESHOLD** равно восьми, что эффективно обозначает пороговое значение для использования дерева, а не связанного списка для **bucket.**

**Очевидно, что:**

1.  **HashMap** требует гораздо больше памяти, чем необходимо для хранения его данных.
2.  **HashMap** не должен быть заполнен более чем на 70-75%. Если он приближается, его размер изменяется, а записи перефразируются.
3.  Повторное хеширование требует n операций, что является дорогостоящим, при этом наша вставка с постоянным временем становится порядка `O(n)`
4.  Алгоритм хеширования определяет порядок вставки объектов в **HashMap**.
5.  Производительность **HashMap** можно настроить, установив пользовательскую начальную емкость и коэффициент загрузки во время самого создания объекта **HashMap**.

**Однако мы должны выбрать **HashMap**, если:**

1.  мы примерно знаем, сколько предметов оставить в нашей коллекции
2.  мы не хотим извлекать элементы в естественном порядке

В описанных выше обстоятельствах **HashMap** — наш лучший выбор, поскольку он предлагает вставку, поиск и удаление с постоянным временем.

**TreeMap** хранит свои данные в иерархическом дереве с возможностью сортировки элементов с помощью пользовательского компаратора.

**Краткое изложение его производительности:**

1.  **TreeMap** обеспечивает производительность **O(**log** (n))** для большинства операций, таких как **add(), remove()** и **contains().**
2.  **TreeMap** может экономить память **(по сравнению с HashMap)**, потому что она использует только объем памяти, необходимый для хранения своих элементов, в отличие от **HashMap**, который использует непрерывную область памяти.
3.  **TreeMap** должно поддерживать свой баланс, чтобы сохранить предполагаемую производительность, это требует значительных усилий и, следовательно, усложняет реализацию.

**Мы должны использовать **TreeMap** всякий раз, когда:**

1.  необходимо учитывать ограничения памяти
2.  мы не знаем, сколько элементов должно храниться в памяти
3.  мы хотим извлечь объекты в естественном порядке
4.  если элементы будут последовательно добавляться и удаляться
5.  мы готовы принять время поиска `O(log n)`

### Дубликаты и синхронизация

И **TreeMap**, и **HashMap** не поддерживают повторяющиеся ключи. Если он добавлен, он переопределяет предыдущий элемент (без ошибки или исключения):**

```java
@Test
public void givenHashMapAndTreeMap_whenputDuplicates_thenOnlyUnique() {
    Map<Integer, String> treeMap = new HashMap<>();
    treeMap.put(1, "Baeldung");
    treeMap.put(1, "Baeldung");
    assertTrue(treeMap.size() == 1);

    Map<Integer, String> treeMap2 = new TreeMap<>();
    treeMap2.put(1, "Baeldung");
    treeMap2.put(1, "Baeldung");
    assertTrue(treeMap2.size() == 1);
}
```

Обе реализации **Map** не синхронизированы, и нам нужно самостоятельно управлять одновременным доступом.

Оба должны быть синхронизированы извне всякий раз, когда несколько потоков обращаются к ним одновременно и хотя бы один из потоков изменяет их.

Мы должны явно использовать **Collections.`synchronizedMap` (mapName)** для получения синхронизированного представления предоставленной карты.

Итератор генерирует исключение **ConcurrentModificationException**, если карта изменяется каким-либо образом и в любое время после создания итератора.

Кроме того, мы можем использовать метод удаления итератора, чтобы изменить карту во время итерации.

**Давайте посмотрим пример:**

```java
@Test
public void whenModifyMapDuringIteration_thenThrowExecption() {
    Map<Integer, String> hashmap = new HashMap<>();
    hashmap.put(1, "One");
    hashmap.put(2, "Two");
    Executable executable = () -> hashmap.forEach((key,value) -> hashmap.remove(1));
    assertThrows(ConcurrentModificationException.class, executable);
}
```

### Итоговые рекомендации

В общем, обе реализации имеют свои плюсы и минусы, однако речь идет о понимании лежащих в основе ожиданий и требований, которые должны определять наш выбор в отношении одного и того же.

**Резюмируя:**

1.  Мы должны использовать **TreeMap**, если мы хотим, чтобы наши записи были отсортированы.
2.  Мы должны использовать **HashMap**, если мы отдаем приоритет производительности потреблению памяти.
3.  Поскольку **TreeMap** имеет более важное местоположение, мы могли бы рассмотреть его, если хотим получить доступ к объектам, которые относительно близки друг к другу в соответствии с их естественным порядком.
4.  **HashMap** можно настроить с помощью **initialCapacity** и **loadFactor**, что невозможно для **TreeMap**.
5.  Мы можем использовать **LinkedHashMap**, если мы хотим сохранить порядок вставки, получая при этом доступ к постоянному времени.

## Сортировка HashMap

В этом кратком руководстве мы узнаем, как сортировать **HashMap** в **Java.**

Как мы знаем, ключи в **TreeMap** сортируются в их естественном порядке. Это хорошее решение, когда мы хотим отсортировать пары ключ-значение по их ключу. Итак, идея состоит в том, чтобы передать все данные из нашего **HashMap** в **TreeMap.**

**Для начала давайте определим **HashMap** и инициализируем его некоторыми данными:**

```java
Map<String, Employee> map = new HashMap<>();
Employee employee1 = new Employee(1L, "Mher");
map.put(employee1.getName(), employee1);
Employee employee2 = new Employee(22L, "Annie");
map.put(employee2.getName(), employee2);
Employee employee3 = new Employee(8L, "John");
map.put(employee3.getName(), employee3);
Employee employee4 = new Employee(2L, "George");
map.put(employee4.getName(), employee4);
```

Обратите внимание, что для класса **Employee** мы реализовали **Comparable:**

```java
public class Employee implements Comparable<Employee> {
    private Long id;
    private String name;

    @Override
    public int compareTo(Employee employee) {
        return (int)(this.id - employee.getId());
    }
}
```

### Сортировка по ключу с помощью TreeMap

**Затем мы сохраняем записи в **TreeMap**, используя его конструктор:**

```java
TreeMap<String, Employee> sorted = new TreeMap<>(map);
```

**Мы также можем использовать метод **putAll** для копирования данных:**

```java
TreeMap<String, Employee> sorted = new TreeMap<>();
sorted.putAll(map);
```

**Вот и все! Чтобы убедиться, что наши записи карты отсортированы по ключу, давайте распечатаем их:**

```text
Annie=Employee {id=22, name='Annie'}
George=Employee {id=2, name='George'}
John=Employee {id=8, name='John'}
Mher=Employee {id=1, name='Mher'}
```

Как видим, ключи отсортированы в естественном порядке.

### Сортировка по ключам с помощью ArrayList

Конечно, мы можем сортировать записи карты с помощью **ArrayList**. Ключевое отличие от предыдущего метода заключается в том, что здесь мы не поддерживаем интерфейс карты.

Давайте загрузим набор ключей в **ArrayList:**

```java
List<String> employeeByKey = new ArrayList<>(map.keySet());
Collections.sort(employeeByKey);
```

**И вывод:**

```text
[Annie, George, John, Mher]
```

### Сортировка по значениям

А что, если мы хотим отсортировать значения карты по полю **id** объекта **Employee?** Мы также можем использовать **ArrayList** для этого.

**Во-первых, давайте скопируем значения в список:**

```java
List<Employee> employeeById = new ArrayList<>(map.values());
```

**Затем сортируем:**

```java
Collections.sort(employeeById);
```

Помните, что это работает, потому что **Employee** реализует интерфейс **Comparable.** В противном случае нам нужно было бы определить ручной компаратор для нашего вызова **Collections.sort.**

Чтобы проверить результаты, мы печатаем **employeeById:**

```text
[Employee {id=1, name='Mher'},
Employee {id=2, name='George'},
Employee {id=8, name='John'},
Employee {id=22, name='Annie'}]
```

Как мы видим, объекты отсортированы по их полю **id.**

### Удаление дубликатов с помощью TreeSet

Если мы не хотим принимать повторяющиеся значения в нашей отсортированной коллекции, есть хорошее решение с **TreeSet.**

**Во-первых, давайте добавим несколько повторяющихся записей на нашу исходную карту:**

```java
Employee employee5 = new Employee(1L, "Mher");
map.put(employee5.getName(), employee5);
Employee employee6 = new Employee(22L, "Annie");
map.put(employee6.getName(), employee6);
```

**Чтобы отсортировать карту по ее ключевым записям:**

```java
SortedSet<String> keySet = new TreeSet<>(map.keySet());
```

**Напечатаем **KeySet** и посмотрим на результат:**

```text
[Annie, George, John, Mher]
```

Теперь у нас есть ключи карт, отсортированные без дубликатов.

**Аналогично, для значений карты код преобразования выглядит так:**

```java
SortedSet<Employee> values = new TreeSet<>(map.values());
```

**И результаты таковы:**

```text
[Employee {id=1, name='Mher'},
Employee {id=2, name='George'},
Employee {id=8, name='John'},
Employee {id=22, name='Annie'}]
```

Как мы видим, в выводе нет дубликатов. Это работает с пользовательскими объектами, когда мы переопределяем **equals** и **hashCode.**

### Сортировка с помощью Stream API (Java 8+)

Начиная с **Java 8**, мы можем использовать **Stream API** и лямбда-выражения для сортировки карты. Все, что нам нужно, — вызвать метод **sorted** в потоковом конвейере карты.

Для сортировки по ключу мы используем компаратор **CompareByKey:**

```java
map.entrySet()
    .stream()
    .sorted(Map.Entry.<String, Employee>comparingByKey())
    .forEach(System.out::println);
```

**Финальный этап **forEach** выводит результаты:**

```text
Annie=Employee {id=22, name='Annie'}
George=Employee {id=2, name='George'}
John=Employee {id=8, name='John'}
Mher=Employee {id=1, name='Mher'}
```

По умолчанию используется режим сортировки по возрастанию.

Конечно, мы можем сортировать и по объектам **Employee:**

```java
map.entrySet()
    .stream()
    .sorted(Map.Entry.comparingByValue())
    .forEach(System.out::println);
```

Как мы видим, приведенный выше код выводит карту, отсортированную по полям **id** объектов **Employee:**

```text
Mher=Employee {id=1, name='Mher'}
George=Employee {id=2, name='George'}
John=Employee {id=8, name='John'}
Annie=Employee {id=22, name='Annie'}
```

**Кроме того, мы можем собрать результаты в новую карту:**

```java
Map<String, Employee> result = map.entrySet()
    .stream()
    .sorted(Map.Entry.comparingByValue())
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (oldValue, newValue) -> oldValue,
        LinkedHashMap::new));
```

Обратите внимание, что мы собрали наши результаты в **LinkedHashMap**. По умолчанию **Collectors.toMap** возвращает новый **HashMap**, но, как мы знаем, **HashMap** не гарантирует порядок итераций, в отличие от **LinkedHashMap**.

### Сортировка с помощью Guava

Наконец, библиотека, которая позволяет нам сортировать **HashMap**, — это **Guava**. Прежде чем мы начнём, будет полезно проверить нашу статью о картах в **Guava**.

**Во-первых, давайте объявим **Ordering**, так как мы хотим отсортировать нашу карту по полю идентификатора сотрудника:**

```java
Ordering naturalOrdering = Ordering.natural()
    .onResultOf(Functions.forMap(map, null));
```

**Теперь все, что нам нужно, это использовать **ImmutableSortedMap** для иллюстрации результатов:**

```java
ImmutableSortedMap.copyOf(map, naturalOrdering);
```

И снова на выходе получается карта, упорядоченная по полю **id:**

```text
Mher=Employee {id=1, name='Mher'}
George=Employee {id=2, name='George'}
John=Employee {id=8, name='John'}
Annie=Employee {id=22, name='Annie'}
```

## Перебор Map

В этом кратком руководстве мы рассмотрим различные способы перебора записей **Map** в **Java.**

Проще говоря, мы можем извлечь содержимое карты с помощью **entrySet(), `KeySet()`** или **values()**. Поскольку это все наборы, ко всем из них применяются одинаковые принципы итерации.

Давайте подробнее рассмотрим некоторые из них.

**Прежде чем мы перейдем к карте, используя три метода, давайте разберемся, что эти методы делают:**

1.  **entrySet()** — возвращает коллекцию-представление карты, элементы которой взяты из класса **Map.Entry**. Метод **entry.`getKey()`** возвращает ключ, а **entry.`getValue()`** возвращает соответствующее значение.
2.  **keySet()** — возвращает все ключи, содержащиеся в этой карте, в виде набора
3.  **values()** — возвращает все значения, содержащиеся в этой карте, в виде набора

### Использование Entry Set

Во-первых, давайте посмотрим, как перебирать **Map** с помощью **Entry Set:**

```java
public void iterateUsingEntrySet(Map<String, Integer> map) {
    for (Map.Entry<String, Integer> entry: map.entrySet()) {
        System.out.println(entry.getKey() + ":" + entry.getValue());
    }
}
```

Здесь мы извлекаем множество записей из нашей карты, а затем перебираем их, используя классический подход для каждого.

### Использование KeySet

**В качестве альтернативы мы можем сначала получить все ключи в нашей карте с помощью метода **KeySet,** а затем перебрать карту по каждому ключу:**

```java
public void iterateUsingKeySetAndForeach(Map<String, Integer> map) {
    for (String key: map.keySet()) {
        System.out.println(key + ":" + map.get(key));
    }
}
```

### Использование values()

**Иногда нас интересуют только значения на карте, независимо от того, какие ключи с ними связаны. В этом случае **values() -** наш лучший выбор:**

```java
public void iterateValues(Map<String, Integer> map) {
    for (Integer value: map.values()) {
        System.out.println(value);
    }
}
```

### Использование Iterator

Другой подход к выполнению итерации — использование **Iterator.** Давайте посмотрим, как методы работают с объектом **Iterator.**

Во-первых, давайте пройдемся по карте, используя **Iterator** и **entrySet():**

```java
public void iterateUsingIteratorAndEntry(Map<String, Integer> map) {
    Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
        Map.Entry<String, Integer> entry = iterator.next();
        System.out.println(entry.getKey() + ":" + entry.getValue());
    }
}
```

Точно так же мы можем перебирать карту с помощью **Iterator** и **KeySet():**

```java
public void iterateUsingIteratorAndKeySet(Map<String, Integer> map) {
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
        String key = iterator.next();
        System.out.println(key + ":" + map.get(key));
    }
}
```

### Использование Lambda (Java 8+)

Начиная с версии **8**, **Java** представила **Stream API** и лямбда-выражения. Давайте посмотрим, как итерировать карту, используя эти методы.

Как и большинство других вещей в **Java 8**, это оказалось намного проще, чем альтернативы. Мы просто воспользуемся методом **forEach():**

```java
public void iterateUsingLambda(Map<String, Integer> map) {
    map.forEach((k, v) -> System.out.println((k + ":" + v)));
}
```

### Использование Stream API

**Stream API** следует использовать, когда мы планируем выполнять дополнительную обработку **Stream**; в противном случае это просто **forEach()**, как описано ранее.

```java
public void iterateUsingStreamAPI(Map<String, Integer> map) {
    map.entrySet().stream()
        .forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
}
```

## Инициализация HashMap

В этом руководстве мы узнаем о различных способах инициализации **HashMap** в **Java.**

### Статический блок кода

```java
public static Map<String, String> articleMapOne;
static {
    articleMapOne = new HashMap<>();
    articleMapOne.put("ar01", "Intro to Map");
    articleMapOne.put("ar02", "Some article");
}
```

### Singleton Map и Empty Map

```java
Map<String, String> singletonMap = Collections.singletonMap("username1", "password1");
Map<String, String> emptyMap = Collections.emptyMap();
```

### Инициализация с помощью Stream API (Java 8+)

```java
Map<String, String> map = Stream.of(new String[][] {
    {"Hello", "World"},
    {"John", "Doe"},
}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
```

### Фабричные методы Java 9

```java
Map<String, String> emptyMap = Map.of();
Map<String, String> singletonMap = Map.of("key1", "value");
Map<String, String> map = Map.of("key1","value1", "key2", "value2");
```

**Используйте **Map.`ofEntries()`** для более чем 10 пар ключ-значение:**

```java
Map<String, String> map = Map.ofEntries(
    new AbstractMap.SimpleEntry<>("name", "John"),
    new AbstractMap.SimpleEntry<>("city", "budapest")
);
```

### Инициализация с помощью Guava

```java
Map<String, String> articles = ImmutableMap.of("Title", "My New Article", "Title2", "Second Article");
```

## Лучшие практики

- **Выбор реализации:** `HashMap` — по умолчанию для ключ-значение; `LinkedHashMap` — если важен порядок вставки; `TreeMap` — если нужна сортировка по ключам или диапазонные запросы; `ConcurrentHashMap` — для многопоточности.
- **Неизменяемые карты:** использовать **Map.of()** / **Map.ofEntries()** (Java 9+) для константных карт; не более 10 пар в `Map.of()` — иначе `Map.ofEntries()`.
- **Ключи:** использовать неизменяемые и с корректными `equals()` / **hashCode()**; избегать мутабельных ключей; для ключей без учёта регистра — `TreeMap` с `String.CASE_INSENSITIVE_ORDER` или кастомный **Comparator**.
- **null:** **HashMap** / **LinkedHashMap** допускают один **null**-ключ и **null**-значения; `TreeMap` и `ConcurrentHashMap` — нет; явно документировать политику **null** в **API**.
- **Инициализация:** задавать начальную ёмкость `HashMap` при известном размере: `new HashMap<>(n)` — уменьшает реаллокации; **load factor** по умолчанию `0.75`.
- **Перебор:** использовать `forEach`, `entrySet().stream()` или итератор по `entrySet()`; избегать повторного вызова `get()` в циклах.
- **Потоки:** для фильтрации/преобразования — **Stream API** по `entrySet()`; собирать в карту через `Collectors.toMap()` с обработкой дубликатов ключей.

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-modification|Java Collections: модификация]]
