---
title: "Java Reactive: RxJava"
description: "Материал по теме Java Reactive: RxJava в разделе cheatsheets."
tags:
  - languages
  - java
  - java-reactive-rxjava
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Java Reactive: RxJava

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)

### Обучающие материалы

- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)


## Содержание

- [Руководство по RxJava](#руководство-по-rxjava)
  - [Основные концепции](#основные-концепции)
  - [Создание Observable](#создание-observable)
  - [Подписка на Observable](#подписка-на-observable)
  - [Основные операторы](#основные-операторы)
    - [map](#map)
    - [flatMap](#flatmap)
    - [scan](#scan)
    - [groupBy](#groupby)
    - [filter](#filter)
    - [defaultIfEmpty](#defaultifempty)
    - [takeWhile](#takewhile)
  - [ConnectableObservable](#connectableobservable)
  - [Single](#single)
  - [Subject](#subject)
- [Single.just() против Single.fromCallable()](#singlejust-против-singlefromcallable)
  - [Single.just()](#singlejust)
  - [Single.fromCallable()](#singlefromcallable)
  - [Сравнение](#сравнение)
- [Руководство по работе с противодавлением](#руководство-по-работе-с-противодавлением)
  - [Hot и Cold Observables](#hot-и-cold-observables)
    - [Cold Observable](#cold-observable)
    - [Hot Observable](#hot-observable)
  - [Стратегии обработки противодавления](#стратегии-обработки-противодавления)
    - [Буферизация](#буферизация)
    - [Группировка в окна](#группировка-в-окна)
    - [Выборка и дросселирование](#выборка-и-дросселирование)
    - [onBackpressureBuffer](#onbackpressurebuffer)
    - [onBackpressureDrop](#onbackpressuredrop)
- [Руководство по обработке исключений](#руководство-по-обработке-исключений)
  - [onErrorReturn](#onerrorreturn)
  - [onErrorResumeNext](#onerrorresumenext)
  - [onExceptionResumeNext](#onexceptionresumenext)
  - [retry](#retry)
  - [retryWhen](#retrywhen)
- [Разница между flatMap и switchMap](#разница-между-flatmap-и-switchmap)
  - [flatMap](#flatmap-1)
  - [switchMap](#switchmap)
- [Руководство по filter](#руководство-по-filter)
  - [Дополнительные фильтрующие операторы](#дополнительные-фильтрующие-операторы)
- [Руководство по Maybe](#руководство-по-maybe)
  - [Создание Maybe](#создание-maybe)
- [Руководство по Flowable](#руководство-по-flowable)
  - [Преобразование Observable в Flowable](#преобразование-observable-в-flowable)
  - [Стратегии обратного давления](#стратегии-обратного-давления)
- [Руководство по Schedulers](#руководство-по-schedulers)
  - [Типы Schedulers](#типы-schedulers)
  - [Использование subscribeOn и observeOn](#использование-subscribeon-и-observeon)
- [Руководство по настройке нескольких Subscribers на один Observable](#руководство-по-настройке-нескольких-subscribers-на-один-observable)
  - [using](#using)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Руководство по RxJava

**RxJava** — это библиотека для реактивного программирования на **Java**. Она позволяет работать с асинхронными потоками данных и событиями.

### Основные концепции

**RxJava** использует концепцию реактивных потоков, вводя **Observables**, на которые может подписаться один или несколько наблюдателей.

**Основные компоненты:**
- **Observable** — источник данных, который может испускать ноль или более элементов
- **Observer** — потребитель данных, который подписывается на **Observable**
- **Subscriber** — расширение **Observer** с дополнительными методами управления подпиской
- **Subject** — одновременно **Observable** и **Observer**

### Создание Observable

```java
// Создание из массива
String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
Observable<String> observable = Observable.from(letters);

// Создание из коллекции
List<String> list = Arrays.asList("a", "b", "c");
Observable<String> observable = Observable.from(list);

// Создание из одного элемента
Observable<String> observable = Observable.just("Hello");

// Создание диапазона чисел
Observable<Integer> observable = Observable.range(1, 10);
```

### Подписка на Observable

**Возвращаемое значение для метода подписки **Observables** — это интерфейс подписки:**

```java
String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
Observable<String> observable = Observable.from(letters);

observable.subscribe(
    i -> result += i,
    Throwable::printStackTrace,
    () -> result += "_Completed"
);

assertTrue(result.equals("abcdefg_Completed"));
```

### Основные операторы

#### map

**map** преобразует элементы, испускаемые **Observable**, применяя функцию к каждому элементу.

**Предположим, что есть объявленный массив строк, который содержит некоторые буквы алфавита, и мы хотим напечатать их в режиме заглавных букв:**

```java
Observable.from(letters)
    .map(String::toUpperCase)
    .subscribe(letter -> result += letter);

assertTrue(result.equals("ABCDEFG"));
```

#### flatMap

**flatMap** можно использовать для выравнивания **Observables** всякий раз, когда мы получаем вложенные **Observables**.

**Предположим, у нас есть метод, который возвращает **Observable&lt;`String`&gt;** из списка строк. Теперь мы будем печатать для каждой строки из нового **Observable** список заголовков на основе того, что видит подписчик:**

```java
Observable<String> getTitle() {
    return Observable.from(titleList);
}

Observable.just("book1", "book2")
    .flatMap(s -> getTitle())
    .subscribe(l -> result += l);

assertTrue(result.equals("titletitle"));
```

#### scan

**scan** последовательно применяет функцию к каждому элементу, испускаемому **Observable**, и выдаёт каждое последующее значение.

**Это позволяет нам переносить состояние от события к событию:**

```java
String[] letters = {"a", "b", "c"};
Observable.from(letters)
    .scan(new StringBuilder(), StringBuilder::append)
    .subscribe(total -> result += total.toString());

assertTrue(result.equals("aababc"));
```

#### groupBy

Группировка по оператору позволяет нам классифицировать события во входном **Observable** по выходным категориям.

**Предположим, что мы создали массив целых чисел от **0** до **10,** затем применим группировку, которая разделит их на категории четные и нечетные:**

```java
Observable.from(numbers)
    .groupBy(i -> 0 == (i % 2) ? "EVEN" : "ODD")
    .subscribe(group ->
        group.subscribe((number) -> {
            if (group.getKey().toString().equals("EVEN")) {
                EVEN[0] += number;
            } else {
                ODD[0] += number;
            }
        })
    );

assertTrue(EVEN[0].equals("0246810"));
assertTrue(ODD[0].equals("13579"));
```

#### filter

**filter** испускает только те элементы наблюдаемого объекта, которые проходят предикатную проверку.

**Итак, давайте отфильтруем массив целых чисел для нечетных чисел:**

```java
Observable.from(numbers)
    .filter(i -> (i % 2 == 1))
    .subscribe(i -> result += i);

assertTrue(result.equals("13579"));
```

#### defaultIfEmpty

**defaultIfEmpty** создает элемент из исходного **Observable** или элемент по умолчанию, если исходный **Observable** пуст:**

```java
Observable.empty()
    .defaultIfEmpty("Observable is empty")
    .subscribe(s -> result += s);

assertTrue(result.equals("Observable is empty"));
```

**Следующий код выдает первую букву алфавита '**a**', потому что массив букв не пуст, и это то, что он содержит в первой позиции:**

```java
Observable.from(letters)
    .defaultIfEmpty("Observable is empty")
    .first()
    .subscribe(s -> result += s);

assertTrue(result.equals("a"));
```

#### takeWhile

**takeWhile** отбрасывает элементы, испускаемые **Observable** после того, как указанное условие становится ложным:**

```java
Observable.from(numbers)
    .takeWhile(i -> i < 5)
    .subscribe(s -> sum[0] += s);

assertTrue(sum[0] == 10);
```

Конечно, есть и другие **Operators**, которые могли бы удовлетворить наши потребности, такие как **Contain**, **SkipWhile**, **SkipUntil**, **TakeUntil** и т. д.

### ConnectableObservable

**ConnectableObservable** похож на обычный **Observable**, за исключением того, что он не начинает испускать элементы, когда на него подписаны, а только когда к нему применяется **Operator** соединения.

**Таким образом, мы можем дождаться, пока все предполагаемые **Observers** подпишутся на **Observable,** прежде чем **Observable** начнет генерировать элементы:**

```java
String[] result = {""};
ConnectableObservable<Long> connectable =
    Observable.interval(200, TimeUnit.MILLISECONDS).publish();

connectable.subscribe(i -> result[0] += i);

assertFalse(result[0].equals("01"));

connectable.connect();

Thread.sleep(500);

assertTrue(result[0].equals("01"));
```

### Single

**Single** похож на **Observable**, который вместо серии значений выдает одно значение или уведомление об ошибке.

**С этим источником данных мы можем использовать только два метода для подписки:**

1.  **OnSuccess** возвращает **Single**, который также вызывает указанный нами метод.
2.  **OnError** также возвращает **Single**, который немедленно уведомляет подписчиков об ошибке.

```java
String[] result = {""};
Single<String> single = Observable.just("Hello")
    .toSingle()
    .doOnSuccess(i -> result[0] += i)
    .doOnError(error -> {
        throw new RuntimeException(error.getMessage());
    });

single.subscribe();

assertTrue(result[0].equals("Hello"));
```

### Subject

**Subject** — это одновременно два элемента: **Subscriber** и **Observable**. В качестве **Subscriber Subject** может использоваться для публикации событий, происходящих из более чем одного **Observable**.

И поскольку **Subject** является также **Observable**, события от нескольких **Subscriber** могут быть повторно отправлены как его события любому, кто его наблюдает.

**В следующем примере мы рассмотрим, как **Observers** смогут видеть события, происходящие после подписки:**

```java
PublishSubject<Integer> subject = PublishSubject.create();

subject.subscribe(getFirstObserver());

subject.onNext(1);
subject.onNext(2);
subject.onNext(3);

subject.subscribe(getSecondObserver());

subject.onNext(4);

subject.onCompleted();

assertTrue(subscriber1 + subscriber2 == 14);
```

## Single.just() против Single.fromCallable()

В этом коротком руководстве мы сравним два популярных способа создания объекта **Single** в **RxJava** и протестируем реализации с помощью **TestSubscriber**. Во-первых, мы рассмотрим фабричный метод **Single.just()** и воспользуемся им для создания экземпляра объекта. После этого мы узнаем о **Single.`fromCallable()`** и посмотрим, как использовать его для повышения производительности.

### Single.just()

**Single.just() -** это простой способ создания экземпляра **Observable.** Он принимает объект в качестве аргумента и оборачивает его внутри **Single `RxJava`:**

```java
Single<String> employee = Single.just("John Doe");
```

Проблема с этим подходом заключается в том, что объект создаётся до подписки. Это означает, что если мы передадим тяжёлую операцию (например, запрос к базе данных), она выполнится немедленно, даже если никто не подписался на **Single**.

### Single.fromCallable()

**Single.`fromCallable()`** позволяет нам создавать **Single** ленивым способом. Вычисление значения происходит только при подписке:**

```java
Single<String> employee = Single.fromCallable(() -> repository.findById(123L));
```

Это более эффективно, так как операция выполняется только тогда, когда кто-то подписывается на **Single.**

### Сравнение

```java
@Test
public void whenUsingJust_thenEagerExecution() {
    Mockito.when(repository.findById(123L)).thenReturn("John Doe");

    Single<String> employee = Single.just(repository.findById(123L));

    Mockito.verify(repository, times(1)).findById(123L); // Выполнится сразу
    // даже если никто не подписался
}

@Test
public void whenUsingFromCallable_thenLazyExecution() {
    Mockito.when(repository.findById(123L)).thenReturn("John Doe");

    Single<String> employee = Single.fromCallable(() -> repository.findById(123L));

    Mockito.verify(repository, never()).findById(123L); // Не выполнится

    employee.subscribe(testSubscriber);

    Mockito.verify(repository, times(1)).findById(123L); // Выполнится при подписке
    testSubscriber.assertCompleted();
    testSubscriber.assertValue("John Doe");
}
```

В этой статье мы сравнили фабричные методы **just()** и **fromCallable()** класса **Single.** Мы узнали, что если мы хотим воспользоваться ленивой выборкой данных, мы должны использовать опцию **fromCallable().**

## Руководство по работе с противодавлением

В этой статье мы рассмотрим, как библиотека **RxJava** помогает нам справляться с противодавлением.

Проще говоря, **RxJava** использует концепцию реактивных потоков, вводя **Observables**, на которые может подписаться один или несколько наблюдателей. Работа с потенциально бесконечными потоками очень сложна, так как нам нужно столкнуться с проблемой противодавления.

Несложно попасть в ситуацию, когда **Observable** выдаёт элементы быстрее, чем подписчик может их потреблять. Мы рассмотрим различные решения проблемы растущего буфера неиспользованных предметов.

### Hot и Cold Observables

У нас есть два типа **Observables** — **Hot** и **Cold** — которые совершенно разные, когда дело доходит до обработки противодавления.

#### Cold Observable

Холодный **Observable** испускает определённую последовательность элементов, но может начать испускать эту последовательность, когда его **Observer** сочтёт это удобным, и с любой скоростью, которую желает **Observer**, не нарушая целостность последовательности. **Cold Observable** предоставляет предметы ленивым способом.

**Observer** берёт элементы только тогда, когда он готов обработать этот элемент, и элементы не нужно буферизовать в **Observable**, поскольку они запрашиваются методом извлечения.

**Например, если вы создаете **Observable** на основе статического диапазона элементов от одного до миллиона, этот **Observable** будет выдавать одну и ту же последовательность элементов независимо от того, как часто эти элементы наблюдаются:**

```java
Observable.range(1, 1_000_000)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute);
```

Когда мы запускаем нашу программу, элементы будут вычисляться **Observer** лениво и запрашиваться в режиме **pull.** Метод **Schedulers.computation()** означает, что мы хотим запустить наш **Observer** в пуле потоков вычислений в **RxJava.**

Холодные наблюдаемые объекты не нуждаются в какой-либо форме противодавления, потому что они работают по принципу вытягивания. Примеры элементов, испускаемых холодным **Observable**, могут включать результаты запроса к базе данных, поиска файлов или веб-запроса.

#### Hot Observable

Горячий **Observable** начинает генерировать элементы и испускает их сразу же после их создания. Это противоречит вытягивающей модели обработки **Cold Observables**. **Hot Observable** испускает предметы в своём собственном темпе, и его **Observers** должны не отставать.

Когда **Observer** не может потреблять элементы так же быстро, как они создаются **Observable,** их необходимо буферизовать или обрабатывать каким-либо другим способом, поскольку они будут заполнять память, что в конечном итоге приведет к **OutOfMemoryException.**

**Давайте рассмотрим пример горячего **Observable**, который производит **1** миллион элементов для конечного потребителя, который обрабатывает эти элементы. Когда метод **calculate()** в **Observer** требует некоторого времени для обработки каждого элемента, **Observable** начинает заполнять память элементами, что приводит к сбою программы:**

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);

IntStream.range(1, 1_000_000).forEach(source::onNext);
```

Запуск этой программы завершится с ошибкой **MissingBackpressureException,** потому что мы не определили способ обработки перепроизводства **Observable.**

Примеры элементов, испускаемых горячим **Observable**, могут включать события мыши и клавиатуры, системные события или курсы акций.

### Стратегии обработки противодавления

#### Буферизация

Первый способ справиться с перепроизводством **Observable -** определить своего рода буфер для элементов, которые не могут быть обработаны **Observer.**

Мы можем сделать это, вызвав метод **buffer():**

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.buffer(1024)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

Определение буфера размером **1024** даст **Observer** некоторое время, чтобы догнать источник с перепроизводством. Буфер будет хранить элементы, которые ещё не были обработаны.

Мы можем увеличить размер буфера, чтобы было достаточно места для производимых значений.

Обратите внимание, однако, что обычно это может быть только временным исправлением, поскольку переполнение всё ещё может произойти, если источник превышает прогнозируемый размер буфера.

#### Группировка в окна

Мы можем группировать перепроизводимые элементы в окна из **N** элементов.

**Когда **Observable** создает элементы быстрее, чем **Observer** может их обработать, мы можем облегчить эту ситуацию, сгруппировав созданные элементы вместе и отправив пакет элементов в **Observer,** который может обрабатывать набор элементов, а не элемент один за другим:**

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.window(500)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

Использование метода **window()** с аргументом **500** укажет **Observable** сгруппировать элементы в пакеты размером **500**. Этот метод может уменьшить проблему перепроизводства **Observable**, когда **Observer** может обрабатывать пакет элементов быстрее, чем обработка элементов один за другим.

#### Выборка и дросселирование

Если некоторые из значений, выдаваемых **Observable,** можно безопасно игнорировать, мы можем использовать выборку в течение определенного времени и дросселирующие **Operators.**

**Методы **sample()** и **throttleFirst()** принимают продолжительность в качестве параметра:**

1.  Метод **sample()** периодически просматривает последовательность элементов и выдаёт последний элемент, созданный в течение времени, указанного в качестве параметра.
2.  Метод **throttleFirst()** выдаёт первый элемент, который был создан после продолжительности, указанной в качестве параметра.

**Продолжительность -** это время, по истечении которого из последовательности созданных элементов выбирается один конкретный элемент. Мы можем указать стратегию обработки противодавления, пропустив элементы:**

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.sample(100, TimeUnit.MILLISECONDS)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

Мы указали, что стратегией пропуска элементов будет метод **sample().** Нам нужен образец последовательности продолжительностью **100** миллисекунд. Этот элемент будет передан **Observer.**

Помните, однако, что эти **Operators** только снижают скорость получения значения нижестоящим наблюдателем и, таким образом, они все еще могут приводить к **MissingBackpressureException.**

#### onBackpressureBuffer

В случае, если наши стратегии выборки или пакетирования элементов не помогают с заполнением буфера, нам необходимо реализовать стратегию обработки случаев, когда буфер заполняется.

Нам нужно использовать метод **onBackpressureBuffer(),** чтобы предотвратить исключение **BufferOverflowException.**

Метод **onBackpressureBuffer()** принимает три аргумента: емкость буфера **Observable,** метод, который вызывается при заполнении буфера, и стратегию обработки элементов, которые необходимо удалить из буфера. Стратегии переполнения находятся в классе **BackpressureOverflow.**

**Существует **4** типа действий, которые могут быть выполнены при заполнении буфера:**

1.  **ON_OVERFLOW_ERROR** — это поведение по умолчанию, сигнализирующее об исключении **BufferOverflowException**, когда буфер заполнен.
2.  **ON_OVERFLOW_DEFAULT** — в настоящее время это то же самое, что и **ON_OVERFLOW_ERROR**.
3.  **ON_OVERFLOW_DROP_LATEST** — если произойдет переполнение, текущее значение будет просто проигнорировано, и только старые значения будут доставлены после запроса нижестоящего **Observer**.
4.  **ON_OVERFLOW_DROP_OLDEST** — удаляет самый старый элемент в буфере и добавляет к нему текущее значение

**Давайте посмотрим, как указать эту стратегию:**

```java
Observable.range(1, 1_000_000)
    .onBackpressureBuffer(16, () -> {}, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
    .observeOn(Schedulers.computation())
    .subscribe(e -> {}, Throwable::printStackTrace);
```

Здесь наша стратегия обработки переполнения буфера заключается в удалении самого старого элемента в буфере и добавлении нового элемента, созданного **Observable.**

Обратите внимание, что последние две стратегии вызывают разрыв в потоке, поскольку они пропускают элементы. Кроме того, они не будут сигнализировать об исключении **BufferOverflowException.**

#### onBackpressureDrop

Всякий раз, когда нижестоящий **Observer** не готов принять элемент, мы можем использовать метод **onBackpressureDrop()**, чтобы удалить этот элемент из последовательности.

Мы можем думать об этом методе как о методе **onBackpressureBuffer()** с емкостью буфера, установленной на ноль, со стратегией **ON_OVERFLOW_DROP_LATEST.**

**Этот **Operator** полезен, когда мы можем безопасно игнорировать значения из исходного **Observable (например, движения мыши или текущие сигналы местоположения GPS),** поскольку позже будут более актуальные значения:**

```java
Observable.range(1, 1_000_000)
    .onBackpressureDrop()
    .observeOn(Schedulers.computation())
    .doOnNext(ComputeFunction::compute)
    .subscribe(v -> {}, Throwable::printStackTrace);
```

Метод **onBackpressureDrop()** устраняет проблему перепроизводства **Observable**, но его следует использовать с осторожностью.

В этой статье мы рассмотрели проблему перепроизводства **Observable** и способы борьбы с противодавлением. Мы рассмотрели стратегии буферизации, группирования и пропуска элементов, когда **Observer** не может потреблять элементы так же быстро, как они создаются **Observable.**

## Руководство по обработке исключений

В этой статье мы рассмотрим, как обрабатывать исключения и ошибки с помощью **RxJava.**

Во-первых, имейте в виду, что **Observable** обычно не генерирует исключений. Вместо этого по умолчанию **Observable** вызывает метод **onError()** своего **Observer,** уведомляя **Observer** о том, что только что произошла неисправимая ошибка, а затем завершает работу, не вызывая больше никаких методов своего **Observer.**

### onErrorReturn

**Метод **onErrorReturn** позволяет вернуть значение по умолчанию при возникновении ошибки:**

```java
Observable<String> observable = Observable.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .onErrorReturn(throwable -> "default");

observable.subscribe(
    System.out::println,
    throwable -> System.out.println("Error: " + throwable.getMessage())
);
```

### onErrorResumeNext

**Метод **onErrorResumeNext** позволяет предоставить альтернативный **Observable** при возникновении ошибки:**

```java
Observable<String> observable = Observable.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .onErrorResumeNext(Observable.just("fallback1", "fallback2"));

observable.subscribe(
    System.out::println,
    throwable -> System.out.println("Error: " + throwable.getMessage())
);
```

### onExceptionResumeNext

**Метод **onExceptionResumeNext** работает аналогично **onErrorResumeNext**, но перехватывает только **Exception**, а не другие типы ошибок (например, Error):**

```java
Observable<String> observable = Observable.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .onExceptionResumeNext(Observable.just("fallback"));
```

### retry

**Метод **retry** позволяет повторить операцию при возникновении ошибки:**

```java
Observable<String> observable = Observable.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .retry(3); // Повторить до 3 раз
```

### retryWhen

**Метод **retryWhen** позволяет определить более сложную логику повторных попыток:**

```java
Observable<String> observable = Observable.error(new RuntimeException("Error"))
    .retryWhen(throwableObservable ->
        throwableObservable
            .zipWith(Observable.range(1, 3), (throwable, integer) -> integer)
            .flatMap(integer -> Observable.timer(integer, TimeUnit.SECONDS))
    );
```

Этот пример повторяет операцию 3 раза с экспоненциальной задержкой (1, 2, 3 секунды).

## Разница между flatMap и switchMap

### flatMap

**flatMap** преобразует каждый элемент **Observable** в новый **Observable**, а затем объединяет все эти **Observable** в один поток. Все **Observable** выполняются параллельно:**

```java
Observable.just("a", "b", "c")
    .flatMap(s -> Observable.just(s.toUpperCase())
        .delay(100, TimeUnit.MILLISECONDS))
    .subscribe(System.out::println);
```

### switchMap

**switchMap** похож на **flatMap**, но если новый элемент приходит до завершения предыдущего **Observable**, предыдущий **Observable** отменяется, и начинается обработка нового:**

```java
Observable.just("a", "b", "c")
    .switchMap(s -> Observable.just(s.toUpperCase())
        .delay(100, TimeUnit.MILLISECONDS))
    .subscribe(System.out::println);
```

**switchMap** полезен, когда нас интересует только последнее значение, например, при поиске, где пользователь может быстро вводить текст.

## Руководство по filter

**filter** испускает только те элементы наблюдаемого объекта, которые проходят предикатную проверку.

**Итак, давайте отфильтруем массив целых чисел для нечетных чисел:**

```java
Observable.from(numbers)
    .filter(i -> (i % 2 == 1))
    .subscribe(i -> result += i);

assertTrue(result.equals("13579"));
```

### Дополнительные фильтрующие операторы

- **filter()** — фильтрует элементы по предикату
- **distinct()** — удаляет дубликаты
- **distinctUntilChanged()** — удаляет последовательные дубликаты
- **take(n)** — берет первые n элементов
- **takeLast(n)** — берет последние n элементов
- **skip(n)** — пропускает первые n элементов
- **skipLast(n)** — пропускает последние n элементов
- **first()** — берет первый элемент
- **last()** — берет последний элемент
- **elementAt(n)** — берет элемент по индексу

## Руководство по Maybe

**Maybe** — это тип **Observable**, который может испускать ноль или один элемент или завершиться с ошибкой. Это комбинация **Single** и **Completable**.

```java
Maybe<String> maybe = Maybe.just("Hello");
maybe.subscribe(
    System.out::println,  // onSuccess
    throwable -> System.err.println("Error: " + throwable),  // onError
    () -> System.out.println("Completed")  // onComplete (если нет элемента)
);
```

### Создание Maybe

```java
// Из одного значения
Maybe<String> maybe1 = Maybe.just("Hello");

// Пустое Maybe
Maybe<String> maybe2 = Maybe.empty();

// Из Observable
Maybe<String> maybe3 = Observable.just("Hello").firstElement();

// Из Single
Maybe<String> maybe4 = Single.just("Hello").toMaybe();
```

## Руководство по Flowable

**Flowable** — это **Observable** с поддержкой обратного давления (backpressure). Он реализует спецификацию **Reactive Streams**.

**Flowable** следует использовать вместо **Observable**, когда:**
- Производитель может генерировать больше данных, чем потребитель может обработать
- Работаете с большими объемами данных
- Нужна явная поддержка обратного давления

```java
Flowable<Integer> flowable = Flowable.range(1, 1000000)
    .onBackpressureBuffer()
    .observeOn(Schedulers.computation());

flowable.subscribe(System.out::println);
```

### Преобразование Observable в Flowable

```java
Observable<Integer> observable = Observable.range(1, 1000);
Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.BUFFER);
```

### Стратегии обратного давления

- **BUFFER** — буферизует все элементы (может привести к OutOfMemoryError)
- **DROP** — отбрасывает элементы, если потребитель не успевает
- **LATEST** — сохраняет только последний элемент
- **ERROR** — выбрасывает **MissingBackpressureException**
- **MISSING** — не обрабатывает обратное давление

## Руководство по Schedulers

**Schedulers** определяют, в каком потоке будут выполняться операции **Observable**.

### Типы Schedulers

1. **Schedulers.io()** — для I/O операций (сеть, файлы)
2. **Schedulers.computation()** — для вычислений (CPU-интенсивные задачи)
3. **Schedulers.`newThread()`** — создает новый поток для каждой задачи
4. **Schedulers.single()** — один поток для всех задач
5. **Schedulers.trampoline()** — выполнение в текущем потоке
6. **Schedulers.from(Executor)** — пользовательский **Executor**

### Использование subscribeOn и observeOn

**subscribeOn** определяет, в каком потоке будет выполняться **Observable**:**

```java
Observable.just("Hello")
    .subscribeOn(Schedulers.io())
    .subscribe(System.out::println);
```

**observeOn** определяет, в каком потоке будут обрабатываться результаты:**

```java
Observable.just("Hello")
    .observeOn(Schedulers.computation())
    .map(String::toUpperCase)
    .observeOn(AndroidSchedulers.mainThread())  // Для Android
    .subscribe(System.out::println);
```

## Руководство по настройке нескольких Subscribers на один Observable

По умолчанию, каждый **Subscriber** получает собственную копию данных от **Observable**. Это называется **Cold Observable**.

**Если нужно, чтобы несколько **Subscribers** разделяли один поток данных, используйте **ConnectableObservable**:**

```java
ConnectableObservable<String> connectable = Observable.just("a", "b", "c")
    .publish();

connectable.subscribe(s -> System.out.println("Subscriber 1: " + s));
connectable.subscribe(s -> System.out.println("Subscriber 2: " + s));

connectable.connect(); // Начинаем испускать элементы только после подключения
```

**Или используйте **share()** для автоматического подключения:**

```java
Observable<String> shared = Observable.just("a", "b", "c")
    .share();

shared.subscribe(s -> System.out.println("Subscriber 1: " + s));
shared.subscribe(s -> System.out.println("Subscriber 2: " + s));
```

### using

**using** позволяет нам связать ресурсы, такие как соединение с базой данных **JDBC,** сетевое соединение или открытые файлы, с нашими **Observables.**

**Здесь мы представляем в комментариях шаги, которые нам нужно сделать для достижения этой цели, а также пример реализации:**

```java
String[] result = {""};
Observable<Character> values = Observable.using(
    () -> "MyResource",  // Фабрика ресурсов
    r -> {  // Фабрика Observable
        return Observable.create(o -> {
            for (Character c : r.toCharArray()) {
                o.onNext(c);
            }
            o.onCompleted();
        });
    },
    r -> System.out.println("Disposed: " + r)  // Утилизация ресурса
);

values.subscribe(v -> result[0] += v, e -> result[0] += e);

assertTrue(result[0].equals("MyResource"));
```

## Лучшие практики

- **Schedulers:** не выполнять блокирующие вызовы на `Schedulers.io()` без ограничения; для **CPU-bound** — `Schedulers.computation()`; для тяжёлых или блокирующих задач — отдельный пул; не подписываться на `Schedulers.trampoline()` для длинных цепочек.
- **Обработка ошибок:** использовать `onErrorResumeNext`, `onErrorReturn`, `doOnError`; не оставлять подписки без `onError` — иначе исключения теряются; для критичных ошибок — логировать и перебрасывать.
- **Backpressure:** для больших потоков использовать `Flowable` и операторы с поддержкой **backpressure**; избегать `Observable` с быстрым **producer** и медленным **consumer** без буферов/стратегий.
- **Ресурсы:** связывать подписки с жизненным циклом через `CompositeDisposable`; отменять при уничтожении компонента; использовать `using()` для ресурсов с временем жизни.
- **Тестирование:** использовать `TestScheduler` для детерминированных тестов; **TestObserver** / **TestSubscriber** для проверки сигналов; избегать реальных задержек в тестах.
- **Избегать:** не создавать **Observable** внутри цикла без отмены подписок; не смешивать синхронные и асинхронные вызовы без чёткого контракта; не блокировать в цепочке операторов.

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
