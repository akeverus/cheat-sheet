---
title: "Java Reactive: Project Reactor"
description: "Материал по теме Java Reactive: Project Reactor в разделе cheatsheets."
tags:
  - languages
  - java
  - java-reactive-project-reactor
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Java Reactive: Project Reactor


## Полезные ссылки

1. **https**://**reactivex.io**/
2. **https**://**projectreactor.io**/**docs**

## Содержание

- [Руководство по Project Reactor](#руководство-по-project-reactor)
  - [Reactive Streams](#reactive-streams)
  - [Зависимости](#зависимости)
  - [Создание потоков данных](#создание-потоков-данных)
    - [Flux](#flux)
    - [Mono](#mono)
  - [Подписка на потоки](#подписка-на-потоки)
  - [Обратное давление](#обратное-давление)
  - [Операторы](#операторы)
  - [Планировщики (Schedulers)](#планировщики-schedulers)
- [Объединение Publishers](#объединение-publishers)
  - [concat и concatWith](#concat-и-concatwith)
  - [merge и mergeWith](#merge-и-mergewith)
  - [zip и zipWith](#zip-и-zipwith)
- [Обработка исключений](#обработка-исключений)
  - [Оператор onErrorReturn](#оператор-onerrorreturn)
  - [Оператор onErrorResume](#оператор-onerrorresume)
  - [Оператор handle](#оператор-handle)
  - [Оператор flatMap с обработкой ошибок](#оператор-flatmap-с-обработкой-ошибок)
  - [Обработка null значений](#обработка-null-значений)
- [Разница между Flux и Mono](#разница-между-flux-и-mono)
- [Отладка реактивных потоков](#отладка-реактивных-потоков)
  - [Активация режима отладки](#активация-режима-отладки)
  - [Использование оператора checkpoint](#использование-оператора-checkpoint)
  - [Логирование элементов](#логирование-элементов)
- [Лучшие практики](#лучшие-практики)
## Руководство по Project Reactor

**Reactor Core** — это библиотека **Java 8**, реализующая модель реактивного программирования. Он построен на основе спецификации **Reactive Streams**, стандарта для создания реактивных приложений.

На фоне нереактивной **Java**-разработки переход на реактивную может оказаться довольно сложным. Это становится более сложным при сравнении его с **Stream `API Java` 8**, поскольку они могут быть ошибочно приняты за одни и те же высокоуровневые абстракции.

В этой статье мы попытаемся демистифицировать эту парадигму. Мы будем продвигаться по **Reactor** небольшими шагами, пока не создадим представление о том, как создавать реактивный код, заложив основу для более сложных статей, которые появятся в последующих сериях.

### Reactive Streams

Прежде чем мы рассмотрим **Reactor**, мы должны взглянуть на спецификацию **Reactive Streams**. Это то, что реализует **Reactor**, и это закладывает основу для библиотеки.

По сути, **Reactive Streams** — это спецификация для асинхронной обработки потоков.

Другими словами, это система, в которой множество событий генерируется и потребляется асинхронно. Подумайте о потоке тысяч обновлений акций в секунду, поступающих в финансовое приложение, и о том, что оно должно своевременно реагировать на эти обновления.

Одной из основных целей этого является решение проблемы обратного давления. Если у нас есть производитель, который отправляет события потребителю быстрее, чем он может их обработать, то в конечном итоге потребитель будет перегружен событиями, и ему не хватит системных ресурсов.

Обратное давление означает, что наш потребитель должен иметь возможность сообщить производителю, сколько данных отправлять, чтобы предотвратить это, и это то, что изложено в спецификации.

### Зависимости

Прежде чем мы начнем, давайте добавим наши зависимости **Maven:**

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.4.16</version>
</dependency>

<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

Мы также добавляем **Logback** в качестве зависимости. Это потому, что мы будем регистрировать выходные данные **Reactor,** чтобы лучше понять поток данных.

### Создание потоков данных

Чтобы приложение было реактивным, первое, что оно должно уметь делать** — это создавать поток данных.

Это может быть что-то вроде примера обновления запасов, который мы приводили ранее. Без этих данных нам не на что было бы реагировать, поэтому это логичный первый шаг.

**Reactive Core** предоставляет нам два типа данных, которые позволяют нам это сделать.

#### Flux

**Первый способ сделать это с помощью **Flux.** Это поток, который может испускать **0..n** элементов. Давайте попробуем создать простой:**

```java
Flux<Integer> just = Flux.just(1, 2, 3, 4);
```

#### Mono

**Второй способ сделать это — использовать **Mono,** представляющий собой поток из **0..1** элементов. Давайте попробуем создать экземпляр:**

```java
Mono<Integer> just = Mono.just(1);
```

Это выглядит и ведет себя почти так же, как **Flux,** только на этот раз мы ограничены не более чем одним элементом.

Прежде чем экспериментировать дальше, стоит подчеркнуть, почему у нас есть эти два типа данных.

**Во-первых, следует отметить, что и **Flux**, и **Mono** являются реализациями интерфейса **Reactive Streams Publisher**. Оба класса соответствуют спецификации, и вместо них мы могли бы использовать этот интерфейс:**

```java
Publisher<String> just = Mono.just("foo");
```

Но на самом деле знать эту кардинальность полезно. Это связано с тем, что несколько операций имеют смысл только для одного из двух типов и потому, что они могут быть более выразительными **(**представьте себе **findOne**(**)** в репозитории**).**

### Подписка на потоки

Теперь у нас есть общий обзор того, как создать поток данных, нам нужно подписаться на него, чтобы он испускал элементы.

**Давайте воспользуемся методом **subscribe()** для сбора всех элементов в потоке:**

```java
List<Integer> elements = new ArrayList<>();

Flux.just(1, 2, 3, 4)
    .log()
    .subscribe(elements::add);

assertThat(elements).containsExactly(1, 2, 3, 4);
```

Данные не начнут поступать, пока мы не подпишемся. Обратите внимание, что мы также добавили журналирование, это будет полезно, когда мы посмотрим, что происходит за кулисами.

**Имея ведение журнала, мы можем использовать его для визуализации того, как данные проходят через наш поток:**

```text
20:25:19.550 [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | request(unbounded)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | onNext(1)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | onNext(2)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | onNext(3)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | onNext(4)
20:25:19.553 [main] INFO reactor.Flux.Array.1 - | onComplete()
```

Во-первых, все работает в основном потоке. Не будем вдаваться в подробности по этому поводу, так как мы подробнее рассмотрим параллелизм позже в этой статье. Тем не менее, это упрощает задачу, поскольку мы можем разобраться со всем по порядку.

**Теперь давайте пройдемся по последовательности, которую мы записали один за другим:**

1.  **onSubscribe()** — вызывается, когда мы подписываемся на наш поток
2.  **request(unbounded)** — Когда мы вызываем **subscribe**, за кулисами мы создаем **Subscription**. Эта подписка запрашивает элементы из потока. В этом случае по умолчанию используется значение **unbounded**, что означает, что он запрашивает каждый доступный элемент.
3.  **onNext()** — вызывается для каждого отдельного элемента.
4.  **onComplete()** \- вызывается последним, после получения последнего элемента. На самом деле также есть **onError()**, который будет вызываться, если есть исключение, но в этом случае нет

**Это поток, изложенный в интерфейсе подписчика как часть спецификации реактивных потоков, и на самом деле это то, что было создано за кулисами в нашем вызове **onSubscribe().** Это полезный метод, но чтобы лучше понять, что происходит, давайте напрямую предоставим интерфейс подписчика:**

```java
Flux.just(1, 2, 3, 4)
    .log()
    .subscribe(new Subscriber<Integer>() {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Integer integer) {
            elements.add(integer);
        }

        @Override
        public void onError(Throwable t) {}

        @Override
        public void onComplete() {}
    });
```

Мы видим, что каждый возможный этап в приведенном выше потоке соответствует методу в реализации подписчика. Так уж получилось, что **Flux** предоставил нам вспомогательный метод для уменьшения этой многословности.

### Обратное давление

Давайте посмотрим, как работает обратное давление в наших примерах.

Если мы хотим, чтобы потребитель контролировал скорость, с которой производитель отправляет данные, мы можем использовать **request().**

**Давайте модифицируем наш **Subscriber** для запроса элементов по одному за раз:**

```java
Flux.just(1, 2, 3, 4)
    .log()
    .subscribe(new Subscriber<Integer>() {
        private Subscription s;
        int onNextAmount;

        @Override
        public void onSubscribe(Subscription s) {
            this.s = s;
            s.request(2);
        }

        @Override
        public void onNext(Integer integer) {
            elements.add(integer);
            onNextAmount++;
            if (onNextAmount % 2 == 0) {
                s.request(2);
            }
        }

        @Override
        public void onError(Throwable t) {}

        @Override
        public void onComplete() {}
    });
```

**Теперь, если мы запустим этот код, мы увидим, что **request(2)** вызывается, за ним следуют два вызова **onNext(),** затем **request(2)** снова:**

```text
23:31:15.395 [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | request(2)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | onNext(1)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | onNext(2)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | request(2)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | onNext(3)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | onNext(4)
23:31:15.397 [main] INFO reactor.Flux.Array.1 - | onComplete()
```

### Операторы

**Reactor** предоставляет множество операторов, которые можно использовать для преобразования потоков данных. Например:**

```java
Flux.just(1, 2, 3, 4)
    .log()
    .map(i -> i * 2)
    .subscribe(elements::add);

assertThat(elements).containsExactly(2, 4, 6, 8);
```

Оператор **map** будет применен к каждому элементу, который испускает поток.

### Планировщики (Schedulers)

**Все приведенные выше примеры в настоящее время выполняются в основном потоке. Однако мы можем контролировать, в каком потоке выполняется наш код, если захотим. Интерфейс **Scheduler** обеспечивает абстракцию вокруг асинхронного кода, для которого нам предоставлено множество реализаций. Давайте попробуем подписаться на другой поток, отличный от основного:**

```java
Flux.just(1, 2, 3, 4)
    .log()
    .map(i -> i * 2)
    .subscribeOn(Schedulers.parallel())
    .subscribe(elements::add);
```

Планировщик **Parallel** заставит нашу подписку выполняться в другом потоке, в чем мы можем убедиться, просмотрев журналы. Мы видим, что первая запись поступает из основного потока, а **Flux** работает в другом потоке с именем **parallel-1.**

```text
20:03:27.505 [main] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
20:03:27.529 [parallel-1] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | request(unbounded)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | onNext(1)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | onNext(2)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | onNext(3)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | onNext(4)
20:03:27.531 [parallel-1] INFO reactor.Flux.Array.1 - | onComplete()
```

## Объединение Publishers

В этой статье мы рассмотрим различные способы объединения издателей в **Project `Reactor`.**

Давайте настроим наш пример с зависимостями **Project `Reactor`:**

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.1.4.RELEASE</version>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <version>3.1.4.RELEASE</version>
    <scope>test</scope>
</dependency>
```

Учитывая сценарий, когда нужно работать с **Flux&lt;T&gt;** или **Mono&lt;T&gt;** , существуют разные способы объединения потоков.

Давайте создадим несколько примеров, чтобы проиллюстрировать использование статических методов в классе **Flux&lt;T&gt;**, таких как **concat**, **concatWith**, **merge**, **zip** и **combLatest**.

В наших примерах будут использоваться два издателя типа **Flux&lt;`Integer`&gt;** , а именно **evenNumbers**, который представляет собой **Flux of Integer** и содержит последовательность чётных чисел, начинающуюся с **1 (минимальная переменная)** и ограниченную **5 (максимальная переменная)**.

**Мы создадим **oddNumbers,** а также **Flux** типа **Integer** нечетных чисел:**

```java
Flux<Integer> evenNumbers = Flux
    .range(min, max)
    .filter(x -> x % 2 == 0);

Flux<Integer> oddNumbers = Flux
    .range(min, max)
    .filter(x -> x % 2 > 0);
```

### concat и concatWith

**Метод **concat** выполняет конкатенацию входных данных, пересылая элементы, испускаемые источниками ниже по течению. Это означает, что первый источник полностью испускается перед тем, как подписаться на следующий источник:**

```java
@Test
public void givenFluxes_whenConcatIsInvoked_thenConcat() {
    Flux<Integer> fluxOfIntegers = Flux.concat(
        evenNumbers,
        oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
}
```

**Метод **concatWith** объединяет два издателя, выполняя ту же конкатенацию:**

```java
@Test
public void givenFluxes_whenConcatWithIsInvoked_thenConcatWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers.concatWith(oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
}
```

### merge и mergeWith

**Метод **merge** объединяет данные из нескольких источников в один объединённый поток. В отличие от **concat**, источники подписываются одновременно:**

```java
@Test
public void givenFluxes_whenMergeIsInvoked_thenMerge() {
    Flux<Integer> fluxOfIntegers = Flux.merge(evenNumbers, oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
}
```

**mergeWith** объединяет два издателя, выполняя ту же операцию слияния:**

```java
@Test
public void givenFluxes_whenMergeWithIsInvoked_thenMergeWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers.mergeWith(oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
}
```

**Опять же, в отличие от **concat,** на внутренние источники охотно подписываются:**

### zip и zipWith

Статический метод **zip** склеивает вместе несколько источников, т. е. ожидает, пока все источники выдадут один элемент, и объединяет эти элементы в выходное значение (созданное с помощью предоставленной функции комбинатора).

**Оператор будет продолжать делать это до тех пор, пока не завершится любой из источников:**

```java
@Test
public void givenFluxes_whenZipIsInvoked_thenZip() {
    Flux<Integer> fluxOfIntegers = Flux.zip(
        evenNumbers,
        oddNumbers,
        (a, b) -> a + b);

    StepVerifier.create(fluxOfIntegers)
        .expectComplete()
        .verify();
}
```

**zipWith** выполняет тот же метод, что и **zip**, но только с двумя издателями:**

```java
@Test
public void givenFluxes_whenZipWithIsInvoked_thenZipWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers
        .zipWith(oddNumbers, (a, b) -> a * b);

    StepVerifier.create(fluxOfIntegers)
        .expectComplete()
        .verify();
}
```

## Обработка исключений

В этом руководстве мы рассмотрим несколько способов обработки исключений в **Project `Reactor`.** Операторы, представленные в примерах кода, определены как в классах **Mono,** так и в классах **Flux.** Однако мы сосредоточимся только на методах класса **Flux.**

### Оператор onErrorReturn

**Оператор **onErrorReturn** позволяет нам предоставить значение по умолчанию при возникновении ошибки:**

```java
Flux<String> flux = Flux.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .onErrorReturn("default");

StepVerifier.create(flux)
    .expectNext("1")
    .expectNext("default")
    .verifyComplete();
```

### Оператор onErrorResume

**Оператор **onErrorResume** позволяет нам предоставить альтернативный поток при возникновении ошибки:**

```java
Flux<String> flux = Flux.just("1", "2", "3")
    .map(s -> {
        if (s.equals("2")) {
            throw new RuntimeException("Exception occurred");
        }
        return s;
    })
    .onErrorResume(e -> Flux.just("fallback1", "fallback2"));

StepVerifier.create(flux)
    .expectNext("1")
    .expectNext("fallback1")
    .expectNext("fallback2")
    .verifyComplete();
```

### Оператор handle

Подобно оператору карты, мы можем использовать оператор дескриптора для обработки элементов в потоке один за другим. Разница в том, что **Reactor** предоставляет оператору **handle** выходной приемник, что позволяет нам применять более сложные преобразования.

**Давайте обновим наш пример из предыдущего раздела, чтобы использовать оператор дескриптора:**

```java
BiConsumer<String, SynchronousSink<Integer>> handler = (input, sink) -> {
    if (input.matches("\\D")) {
        sink.error(new NumberFormatException());
    } else {
        sink.next(Integer.parseInt(input));
    }
};

Flux<String> inFlux = Flux.just("1", "1.5", "2");
Flux<Integer> outFlux = inFlux.handle(handler);
```

В отличие от оператора карты, оператор дескриптора получает функционального потребителя, вызываемого один раз для каждого элемента. Этот потребитель имеет два параметра: элемент, поступающий из восходящего потока, и **SynchronousSink**, формирующий выходные данные для отправки в нисходящий поток.

Если входной элемент является числовой строкой, мы вызываем метод **next** в приемнике, предоставляя ему целое число, преобразованное из ввода. Если это не числовая **String,** мы укажем ситуацию, вызвав метод **error** с объектом **Exception.**

Обратите внимание, что вызов метода **error** отменит подписку на восходящий поток и вызовет метод **onError** на нисходящем потоке. Такое сотрудничество **error** и **onError** является стандартным способом обработки **Exception** в реактивных потоках.

### Оператор flatMap с обработкой ошибок

Другой часто используемый оператор, поддерживающий обработку ошибок, - **flatMap**. Этот оператор преобразует элементы ввода в **Publisher**, а затем объединяет **Publisher** в новый поток. Мы можем воспользоваться этими **Publisher** для обозначения ошибочного состояния.

Давайте попробуем тот же пример, используя **flatMap:**

```java
Function<String, Publisher<Integer>> mapper = input -> {
    if (input.matches("\\D")) {
        return Mono.error(new NumberFormatException());
    } else {
        return Mono.just(Integer.parseInt(input));
    }
};

Flux<String> inFlux = Flux.just("1", "1.5", "2");
Flux<Integer> outFlux = inFlux.flatMap(mapper);

StepVerifier.create(outFlux)
    .expectNext(1)
    .expectError(NumberFormatException.class)
    .verify();
```

Обратите внимание, что единственная разница между **handle** и **flatMap** в отношении обработки ошибок заключается в том, что оператор **handle** вызывает метод **error** на подписчике, а **flatMap** вызывает его на издателе.

Если мы имеем дело с потоком, представленным объектом **Flux**, мы также можем использовать **concatMap** для обработки ошибок. Этот метод во многом похож на **flatMap**, но не поддерживает асинхронную обработку.

### Обработка null значений

**В этом разделе рассматривается обработка нулевых ссылок, которые часто вызывают **NullPointerException s,** часто встречающееся исключение в **Java.** Чтобы избежать этого исключения, мы обычно сравниваем переменную с нулевым значением и направляем выполнение другим способом, если эта переменная на самом деле равна нулю. Заманчиво сделать то же самое в реактивных потоках:**

```java
Function<String, Integer> mapper = input -> {
    if (input == null) {
        return 0;
    } else {
        return Integer.parseInt(input);
    }
};
```

**Мы можем подумать, что **NullPointerException** не произойдет, потому что мы уже обработали случай, когда входное значение равно **null.** Однако реальность говорит о другом:**

```java
Flux<String> inFlux = Flux.just("1", null, "2");
Flux<Integer> outFlux = inFlux.map(mapper);

StepVerifier.create(outFlux)
    .expectNext(1)
    .expectError(NullPointerException.class)
    .verify();
```

Судя по всему, **NullPointerException** спровоцировало ошибку ниже по течению, а это означает, что наша проверка на **null** не сработала.

Чтобы понять, почему это произошло, нам нужно вернуться к спецификации **Reactive Streams**. Правило `2.13` спецификации гласит, что «вызовы **onSubscribe**, **onNext**, **onError** или **onComplete** ДОЛЖНЫ возвращаться нормально, за исключением случаев, когда какой-либо предоставленный параметр имеет значение **null**, и в этом случае он ДОЛЖЕН вызывать исключение **java.lang.NullPointerException** для вызывающей стороны».

Как того требует спецификация, **Reactor** выдает исключение **NullPointerException**, когда значение **null** достигает функции карты.

Поэтому мы ничего не можем сделать с нулевым значением, когда оно достигает определенного потока. Мы не можем обработать его или преобразовать в ненулевое значение, прежде чем передать его вниз по течению. Таким образом, единственный способ избежать **NullPointerException** — убедиться, что нулевые значения не попадут в конвейер.

## Разница между Flux и Mono

**Mono** — это особый тип **Publisher**. Объект **Mono** представляет одиночное или пустое значение. Это означает, что он может выдать не более одного значения для запроса **onNext()**, а затем завершается сигналом **onComplete()**. В случае сбоя он выдает только один сигнал **onError()**.

**Давайте посмотрим на пример **Mono** с сигналом завершения:**

```java
@Test
public void givenMonoPublisher_whenSubscribeThenReturnSingleValue() {
    Mono<String> helloMono = Mono.just("Hello");

    StepVerifier.create(helloMono)
        .expectNext("Hello")
        .expectComplete()
        .verify();
}
```

Здесь мы видим, что когда **helloMono** подписан, он выдаёт только одно значение, а затем отправляет сигнал завершения.

**Flux** — это стандартный издатель, который представляет значения асинхронной последовательности от **0** до **N**. Это означает, что он может выдавать **0** для многих значений, возможно, бесконечных значений для запросов **onNext()**, а затем завершается либо завершением, либо сигналом ошибки.

**Давайте посмотрим на пример **Flux** с сигналом завершения:**

```java
@Test
public void givenFluxPublisher_whenSubscribedThenReturnMultipleValues() {
    Flux<String> stringFlux = Flux.just("Hello", "Baeldung");

    StepVerifier.create(stringFlux)
        .expectNext("Hello")
        .expectNext("Baeldung")
        .expectComplete()
        .verify();
}
```

**Теперь давайте посмотрим на пример **Flux** с сигналом ошибки:**

```java
@Test
public void givenFluxPublisher_whenSubscribeThenReturnMultipleValuesWithError() {
    Flux<String> stringFlux = Flux.just("Hello", "Baeldung", "Error")
        .map(str -> {
            if (str.equals("Error"))
                throw new RuntimeException("Throwing Error");
            return str;
        });

    StepVerifier.create(stringFlux)
        .expectNext("Hello")
        .expectNext("Baeldung")
        .expectError()
        .verify();
}
```

Здесь мы видим, что после получения двух значений из **Flux** мы получаем ошибку.

**Mono** и **Flux** являются реализациями интерфейса **Publisher.** Проще говоря, мы можем сказать, что когда мы делаем что-то вроде вычислений или делаем запрос к базе данных или внешней службе и ожидаем максимум один результат, мы должны использовать **Mono.**

Когда мы ожидаем множественных результатов от наших вычислений, баз данных или вызова внешней службы, мы должны использовать **Flux.**

**Mono** больше связан с классом **Optional** в **Java**, поскольку он содержит значение **0** или **1**, а **Flux** больше связан со списком, поскольку он может иметь **N** значений.

## Отладка реактивных потоков

**Теперь давайте рассмотрим фрагмент и результат, который он генерирует при обнаружении необработанной ошибки:**

```java
public void processFoo(Flux<Foo> flux) {
    flux.map(cyclicBarrier::post)
        .subscribe(this::processNextStep, this::processError);
}
```

Если при выполнении **processFoo** возникнет ошибка, стек трассировки будет минимальным и не будет указывать на место возникновения проблемы. Это потому, что, когда поток подписывается, элементы проходят через цепочку операторов, которые могут быть в разных классах. **Reactor** предоставляет несколько инструментов для отладки реактивных потоков, и мы рассмотрим некоторые из них.

### Активация режима отладки

**Reactor** предоставляет простой способ активировать режим отладки для всего приложения, добавив следующий оператор в начало цепочки или инициализировав его при запуске приложения:**

```java
Hooks.onOperatorDebug();
```

Это активирует режим отладки для всех операторов, выполняемых после этой точки. Это приведёт к сбору трассировки стека для каждого оператора, что может помочь отследить источник проблемы. Однако это может повлиять на производительность, поэтому рекомендуется использовать его только в среде разработки.

### Использование оператора checkpoint

**Оператор **checkpoint** позволяет нам добавлять теги в цепочку операторов для более простой идентификации проблем:**

```java
Flux<Integer> numbers = Flux.range(1, 10)
    .checkpoint("Range checkpoint")
    .map(n -> n * 2)
    .checkpoint("Map checkpoint")
    .filter(n -> n > 10);

numbers.subscribe(
    System.out::println,
    error -> {
        System.err.println("Error: " + error.getMessage());
        error.printStackTrace();
    }
);
```

Когда возникает ошибка, в стек трассировки будут включены сообщения из чекпоинтов, что поможет идентифицировать, где именно в цепочке произошла проблема.

### Логирование элементов

**Оператор **log** позволяет нам логировать все сигналы, проходящие через поток:**

```java
Flux<Integer> numbers = Flux.range(1, 5)
    .log()
    .map(n -> n * 2)
    .filter(n -> n > 5);

numbers.subscribe(System.out::println);
```

Это выведет подробную информацию о каждом этапе обработки, включая **onSubscribe**, **onNext**, **onComplete** и **onError** сигналы. Это может быть очень полезно для понимания потока данных и обнаружения проблем.

## Лучшие практики

- **Flux vs `Mono`:** использовать **Mono** для 0..1 элемента (результат вызова, опциональность); **Flux** для потоков и множества элементов; не блокировать в цепочке — возвращать **Mono**/**Flux** до подписки.
- **Schedulers:** блокирующие операции выполнять на `**Scheduler.boundedElastic**()` или выделенном пуле; **CPU-bound** — `**parallel**()`; не блокировать на `**Schedulers.parallel**()`.
- **Ошибки:** обрабатывать через `onErrorResume`, `onErrorReturn`, `doOnError`; подписываться с обработчиком ошибок; для отладки использовать `**checkpoint**()` и `**Hooks.onOperatorDebug**()` в **dev**.
- **Подписка:** подписываться в слое, готовом управлять жизненным циклом (контроллер, сервис); отменять подписки при завершении контекста; в **WebFlux** подписка часто неявная.
- **Тестирование:** использовать `StepVerifier` для проверки порядка и значений; `VirtualTimeScheduler` для тестов с задержками; не блокировать в тестах без `**block**()` с таймаутом.
- **Ресурсы:** использовать `**using**()` для ресурсов с временем жизни; не забывать про отмену и очистку при ошибках.

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
