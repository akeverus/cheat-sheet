---
title: "Вопросы на собеседовании: Project Reactor"
description: "Комплексное руководство по Project Reactor: Mono, Flux, операторы, backpressure, Schedulers, Context, тестирование, отладка, Hot/Cold publishers"
tags:
  - interview
  - reactive
  - project-reactor-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Project Reactor"
  - "Project Reactor interview"
  - "Project Reactor собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Project Reactor`

Комплексное руководство по вопросам собеседования на тему `Project Reactor` для `Senior Java Developer`. Охватывает реактивное программирование, `Mono`/`Flux`, операторы трансформации, backpressure, `Schedulers`, `Context`, тестирование с `StepVerifier`, отладку и интеграцию со `Spring WebFlux`.

**`Project Reactor`** — реактивная библиотека для `JVM`, реализующая спецификацию `Reactive Streams`. Является фундаментом `Spring WebFlux` и широко применяется для построения неблокирующих, высоконагруженных приложений. Библиотека предоставляет два основных типа: `Mono` (0 или 1 элемент) и `Flux` (0 или N элементов).

## Полезные ссылки

### Официальная документация

- [Project Reactor Reference Guide](https://projectreactor.io/docs/core/release/reference/) — полная документация по `Reactor Core`
- [Reactor API Javadoc](https://projectreactor.io/docs/core/release/api/) — API Javadoc
- [Reactive Streams Specification](https://www.reactive-streams.org/) — спецификация `Reactive Streams`
- [Marble Diagrams](https://rxmarbles.com/) — интерактивные marble-диаграммы операторов

### Статьи Baeldung

- [Intro To Reactor Core](https://www.baeldung.com/reactor-core) — введение в `Reactor Core`
- [Handling Exceptions in Project Reactor](https://www.baeldung.com/reactor-exceptions) — обработка исключений
- [Backpressure Mechanism in Spring WebFlux](https://www.baeldung.com/spring-webflux-backpressure) — backpressure
- [Testing Reactive Streams Using StepVerifier and TestPublisher](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher) — тестирование
- [Project Reactor: map() vs flatMap()](https://www.baeldung.com/java-reactor-map-flatmap) — операторы трансформации
- [Combining Publishers in Project Reactor](https://www.baeldung.com/reactor-combine-streams) — комбинирование потоков
- [Debugging Reactive Streams in Spring 5](https://www.baeldung.com/spring-debugging-reactive-streams) — отладка
- [Programmatically Creating Sequences with Project Reactor](https://www.baeldung.com/flux-sequences-reactor) — создание последовательностей
- [Parallel Flux vs Flux in Project Reactor](https://www.baeldung.com/reactor-flux-vs-parallel-flux) — параллельная обработка
- [Mono just() vs defer() vs create()](https://www.baeldung.com/reactive-mono-just-defer-create) — создание `Mono`
- [Concurrency in Spring WebFlux](https://www.baeldung.com/spring-webflux-concurrency) — конкурентность

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы реактивного программирования и Reactive Streams**
- [Q1. (!) Что такое реактивное программирование?](#q1--что-такое-реактивное-программирование)
- [Q2. (!) Что такое спецификация Reactive Streams?](#q2--что-такое-спецификация-reactive-streams)
- [Q3. Что такое Project Reactor и чем он отличается от RxJava?](#q3-что-такое-project-reactor-и-чем-он-отличается-от-rxjava)
- [Q4. (!) Что такое backpressure и почему это важно?](#q4--что-такое-backpressure-и-почему-это-важно)

**Mono и Flux: создание и базовые операции**
- [Q5. (!) В чём разница между Mono и Flux?](#q5--в-чём-разница-между-mono-и-flux)
- [Q6. Какие способы создания Mono существуют?](#q6-какие-способы-создания-mono-существуют)
- [Q7. Какие способы создания Flux существуют?](#q7-какие-способы-создания-flux-существуют)
- [Q8. (!) В чём разница между Mono.just() и Mono.defer()?](#q8--в-чём-разница-между-monojust-и-monodefer)
- [Q9. Что такое Flux.create() и Flux.push() и когда их использовать?](#q9-что-такое-fluxcreate-и-fluxpush-и-когда-их-использовать)
- [Q10. Что такое Flux.generate() и чем он отличается от Flux.create()?](#q10-что-такое-fluxgenerate-и-чем-он-отличается-от-fluxcreate)

**Операторы трансформации**
- [Q11. (!) В чём разница между map() и flatMap()?](#q11--в-чём-разница-между-map-и-flatmap)
- [Q12. (!) В чём разница между flatMap(), concatMap() и switchMap()?](#q12--в-чём-разница-между-flatmap-concatmap-и-switchmap)
- [Q13. Что такое flatMapSequential()?](#q13-что-такое-flatmapsequential)
- [Q14. Для чего используются операторы transform() и as()?](#q14-для-чего-используются-операторы-transform-и-as)
- [Q15. Что делают операторы doOnNext(), doOnComplete(), doOnError()?](#q15-что-делают-операторы-doonnext-dooncomplete-doonerror)

**Операторы фильтрации**
- [Q16. Какие операторы фильтрации предоставляет Project Reactor?](#q16-какие-операторы-фильтрации-предоставляет-project-reactor)
- [Q17. Что делает оператор switchIfEmpty()?](#q17-что-делает-оператор-switchifempty)
- [Q18. Что делают операторы take() и skip()?](#q18-что-делают-операторы-take-и-skip)

**Комбинирование потоков**
- [Q19. (!) В чём разница между merge(), concat() и zip()?](#q19--в-чём-разница-между-merge-concat-и-zip)
- [Q20. Что такое combineLatest() и когда его использовать?](#q20-что-такое-combinelatest-и-когда-его-использовать)
- [Q21. В чём разница между zipWith() и withLatestFrom()?](#q21-в-чём-разница-между-zipwith-и-withlatestfrom)

**Обработка ошибок**
- [Q22. (!) Какие операторы используются для обработки ошибок в Project Reactor?](#q22--какие-операторы-используются-для-обработки-ошибок-в-project-reactor)
- [Q23. В чём разница между onErrorReturn() и onErrorResume()?](#q23-в-чём-разница-между-onerrorreturn-и-onerrorresume)
- [Q24. Что делает оператор onErrorMap()?](#q24-что-делает-оператор-onerrormap)
- [Q25. (!) Как работают операторы retry() и retryWhen()?](#q25--как-работают-операторы-retry-и-retrywhen)
- [Q26. Что такое Exceptions.propagate() и когда его применять?](#q26-что-такое-exceptionspropagate-и-когда-его-применять)

**Backpressure и управление потоком**
- [Q27. (!) Как Project Reactor реализует backpressure?](#q27--как-project-reactor-реализует-backpressure)
- [Q28. Какие стратегии backpressure существуют?](#q28-какие-стратегии-backpressure-существуют)
- [Q29. Что делает оператор limitRate()?](#q29-что-делает-оператор-limitrate)

**Schedulers: управление потоками**
- [Q30. (!) Какие Schedulers существуют в Project Reactor?](#q30--какие-schedulers-существуют-в-project-reactor)
- [Q31. (!) В чём разница между subscribeOn() и publishOn()?](#q31--в-чём-разница-между-subscribeon-и-publishon)
- [Q32. Что такое ParallelFlux и когда его использовать?](#q32-что-такое-parallelflux-и-когда-его-использовать)

**Context: передача данных в реактивной цепочке**
- [Q33. (!) Что такое Context в Project Reactor?](#q33--что-такое-context-в-project-reactor)
- [Q34. Как записывать и читать данные из Context?](#q34-как-записывать-и-читать-данные-из-context)

**Hot vs Cold publishers**
- [Q35. (!) В чём разница между Cold и Hot publisher?](#q35--в-чём-разница-между-cold-и-hot-publisher)
- [Q36. Как преобразовать Cold publisher в Hot?](#q36-как-преобразовать-cold-publisher-в-hot)
- [Q37. Что такое ConnectableFlux?](#q37-что-такое-connectableflux)

**Операторы времени**
- [Q38. Какие операторы для работы со временем предоставляет Project Reactor?](#q38-какие-операторы-для-работы-со-временем-предоставляет-project-reactor)
- [Q39. Что делает оператор timeout()?](#q39-что-делает-оператор-timeout)

**Тестирование**
- [Q40. (!) Что такое StepVerifier и как его использовать?](#q40--что-такое-stepverifier-и-как-его-использовать)
- [Q41. Как тестировать потоки, зависящие от времени?](#q41-как-тестировать-потоки-зависящие-от-времени)
- [Q42. Что такое TestPublisher?](#q42-что-такое-testpublisher)

**Отладка**
- [Q43. (!) Как отлаживать реактивные цепочки в Project Reactor?](#q43--как-отлаживать-реактивные-цепочки-в-project-reactor)
- [Q44. Что такое checkpoint() и как он помогает при отладке?](#q44-что-такое-checkpoint-и-как-он-помогает-при-отладке)
- [Q45. Что такое Hooks и как использовать Hooks.onOperatorDebug()?](#q45-что-такое-hooks-и-как-использовать-hooksonoperatordebug)

**Интеграция со Spring WebFlux**
- [Q46. (!) Как Project Reactor интегрируется со Spring WebFlux?](#q46--как-project-reactor-интегрируется-со-spring-webflux)
- [Q47. Почему нельзя вызывать block() в WebFlux-приложении?](#q47-почему-нельзя-вызывать-block-в-webflux-приложении)
- [Q48. (!) Как пробросить `ThreadLocal`-контекст в реактивную цепочку (context-propagation)?](#q48--как-пробросить-threadlocal-контекст-в-реактивную-цепочку-context-propagation)

---

## Q1. (!) Что такое реактивное программирование?

**Реактивное программирование** — это парадигма работы с асинхронными потоками данных, где компоненты **реагируют** на события (новый элемент, ошибка, завершение), а не блокируются в ожидании результата. В основе лежит паттерн `Observer`: потребитель подписывается на источник и получает данные по мере их появления.

**Зачем это нужно.** Классический блокирующий код держит поток занятым, пока ждёт ответ от БД или сети. Под нагрузкой это упирается в число потоков. Реактивный подход освобождает поток на время ожидания, поэтому небольшой пул потоков обслуживает тысячи параллельных запросов — отсюда выигрыш в throughput на I/O-нагрузке.

Идеологию задаёт [Reactive Manifesto](https://www.reactivemanifesto.org/) — четыре свойства реактивной системы:
- **Responsive** — отвечает быстро и предсказуемо
- **Resilient** — сохраняет отзывчивость при сбоях (изоляция, репликация)
- **Elastic** — масштабируется под нагрузку вверх и вниз
- **Message Driven** — компоненты общаются асинхронными сообщениями, что и даёт первые три свойства

Ключевое отличие от императивного стиля: реактивный код **декларативен** — он описывает, *что* произойдёт с данными по мере поступления, а не пошагово *как* их извлечь:

```java
// Императивный подход (блокирующий)
User user = userRepository.findById(id); // блокирует поток
Order order = orderRepository.findByUser(user); // ещё блокировка
return order;

// Реактивный подход (неблокирующий)
return userRepository.findById(id)          // Mono<User>
    .flatMap(user -> orderRepository.findByUser(user)); // Mono<Order>
```

---

## Q2. (!) Что такое спецификация Reactive Streams?

**Reactive Streams** — это стандарт (спецификация + набор интерфейсов) для асинхронной обработки потоков данных с обязательной поддержкой backpressure. Главная цель — дать разным реактивным библиотекам (`Reactor`, `RxJava`, `Akka Streams`) общий контракт, чтобы они совместимо работали друг с другом. Включена в `JDK 9` как `java.util.concurrent.Flow`.

Спецификация сводится к 4 интерфейсам:

| Интерфейс | Описание |
|-----------|----------|
| `Publisher<T>` | Источник данных. Метод `subscribe(Subscriber)` |
| `Subscriber<T>` | Потребитель. Методы `onSubscribe`, `onNext`, `onError`, `onComplete` |
| `Subscription` | Связь между `Publisher` и `Subscriber`. Методы `request(n)`, `cancel()` |
| `Processor<T,R>` | Комбинирует `Publisher` и `Subscriber` |

Протокол взаимодействия:
```
Publisher                Subscriber
   |                         |
   |<-- subscribe() ---------|
   |                         |
   |--- onSubscribe(sub) --->|
   |                         |
   |<-- request(n) ----------|  backpressure!
   |                         |
   |--- onNext(item) ------->|  (повторяется n раз)
   |--- onComplete() ------->|  или onError()
```

Суть протокола: подписчик не пассивно «получает» данные, а явно **запрашивает** их через `request(n)`. Это и есть точка, где встроен backpressure, — производитель не имеет права отдать больше, чем запрошено.

`Project Reactor` полностью реализует эту спецификацию: `Flux` и `Mono` реализуют интерфейс `Publisher<T>`, поэтому совместимы с любым `Subscriber`, написанным под `Reactive Streams`.

---

## Q3. Что такое Project Reactor и чем он отличается от RxJava?

**`Project Reactor`** — реактивная библиотека для `JVM` от команды `Spring/Pivotal`, реализующая спецификацию `Reactive Streams`. Это фундамент `Spring WebFlux`: весь реактивный стек Spring построен на `Mono`/`Flux`.

И `Reactor`, и `RxJava` решают одну задачу, но различаются API и философией. Ключевые различия:

| Характеристика | Project Reactor | RxJava 3 |
|----------------|-----------------|----------|
| Основные типы | `Mono<T>`, `Flux<T>` | `Single`, `Maybe`, `Observable`, `Flowable` |
| Backpressure | Встроен во все типы | Только `Flowable` |
| Интеграция со Spring | Нативная | Через адаптер |
| Nullable | Не допускает `null` | Зависит от типа |
| Java версия | Java 8+ | Java 8+ |
| `Context` | Есть (`Context`) | Отсутствует аналог |

**Когда что выбирать.** Если проект на Spring — берите `Reactor`: он нативно интегрирован с `Spring WebFlux`, `Spring Data Reactive`, `Spring Security` и не требует адаптеров. `RxJava` исторически популярен на Android и в проектах вне Spring; его сильная сторона — богатый набор типов под каждый кейс (`Single`, `Maybe`, `Completable`). API двух библиотек во многом похож, поэтому переход между ними не составляет большого труда.

---

## Q4. (!) Что такое backpressure и почему это важно?

**Backpressure** — это механизм, позволяющий потребителю (`Subscriber`) контролировать скорость, с которой производитель (`Publisher`) отдаёт данные. Грубо говоря, потребитель говорит «дай мне ровно N элементов, не больше», и производитель обязан это соблюдать.

**Какую проблему решает.** Если производитель генерирует данные быстрее, чем потребитель успевает их обрабатывать, элементы где-то копятся. Без обратной связи они оседают в буферах, и под нагрузкой это приводит к `OutOfMemoryError` или к потере данных. Backpressure убирает эту проблему в корне: производитель просто не отправляет больше, чем у него запросили.

```
Без backpressure:
Producer ----[1,2,3,4,5,6,7,8...]----> Consumer (обрабатывает 1 в секунду)
                                        ↑ переполнение буфера!

С backpressure:
Producer ----[1]---[2]---[3]-------> Consumer
             ↑     ↑     ↑
             request(1) → обработал → request(1) → ...
```

В `Reactive Streams` это реализовано через метод `Subscription.request(n)`: подписчик запрашивает ровно столько элементов, сколько готов принять. Чаще всего backpressure работает прозрачно — операторы сами пробрасывают запросы вверх по цепочке. Но при необходимости его можно контролировать вручную через `BaseSubscriber`:

```java
// Явный контроль backpressure через BaseSubscriber
flux.subscribe(new BaseSubscriber<String>() {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(1); // запрашиваем по 1 элементу
    }

    @Override
    protected void hookOnNext(String value) {
        process(value);
        request(1); // запрашиваем следующий
    }
});
```

---

## Q5. (!) В чём разница между Mono и Flux?

Оба типа — это `Publisher`, разница только в **количестве элементов**, которое они обещают.

**`Mono<T>`** представляет **0 или 1 элемент** — асинхронный аналог `Optional`/`CompletableFuture`. Поток завершается сигналом `onComplete` (с одним элементом или без него) либо `onError`.

**`Flux<T>`** представляет **0 или N элементов** (поток может быть и бесконечным). После произвольного числа сигналов `onNext` поток завершается через `onComplete` или `onError`.

Эта разница не косметическая: тип в сигнатуре метода сразу сообщает контракт. `Mono<User>` — «вернётся максимум один пользователь», `Flux<User>` — «вернётся список, возможно пустой».

```java
// Mono — один результат
Mono<User> userMono = userRepository.findById(42L);

// Flux — коллекция результатов
Flux<User> usersFlux = userRepository.findAll();

// Mono из Flux — сворачивание потока
Mono<List<User>> listMono = usersFlux.collectList();

// Flux из Mono — разворачивание
Flux<String> flux = Mono.just("hello").flux();
```

**Когда что использовать:**
- `Mono` — поиск по ID, сохранение одной сущности, единичный HTTP-запрос, подсчёт (`count()`)
- `Flux` — список результатов, стриминг событий, чтение файла построчно, Server-Sent Events

---

## Q6. Какие способы создания Mono существуют?

Фабричные методы `Mono` делятся на три группы: **из готового значения** (`just`, `empty`, `error`), **ленивые/обёртки над вычислением** (`defer`, `fromCallable`, `fromFuture`) и **ручное управление эмиссией** (`create`). Главное различие — момент вычисления: `just` вычисляет значение сразу при сборке, остальные откладывают его до подписки.

```java
// Уже готовое значение (eager — вычисляется немедленно)
Mono<String> just = Mono.just("value");

// Пустой Mono (только onComplete)
Mono<String> empty = Mono.empty();

// Ошибка
Mono<String> error = Mono.error(new RuntimeException("fail"));

// Lazy-вычисление (вычисляется при подписке)
Mono<String> deferred = Mono.defer(() -> Mono.just(computeValue()));

// Из Callable/Supplier
Mono<String> fromCallable = Mono.fromCallable(() -> blockingOperation());

// Из CompletableFuture
Mono<String> fromFuture = Mono.fromFuture(asyncOperation());

// Из Publisher (адаптер)
Mono<String> fromPublisher = Mono.from(somePublisher);

// Из Optional
Mono<String> fromOptional = Mono.justOrEmpty(Optional.of("value"));

// Ручное управление через MonoSink
Mono<String> create = Mono.create(sink -> {
    sink.success("value"); // или sink.error(ex)
});
```

---

## Q7. Какие способы создания Flux существуют?

Способы создания `Flux` группируются так: **из конечных данных** (`just`, `fromIterable`, `fromArray`, `range`), **специальные сигналы** (`empty`, `error`), **временны́е/бесконечные** (`interval`) и **программная генерация** (`generate` для синхронного источника, `create` — для асинхронного с произвольным числом элементов).

```java
// Из элементов vararg
Flux<Integer> fromValues = Flux.just(1, 2, 3, 4, 5);

// Из коллекции
Flux<String> fromIterable = Flux.fromIterable(List.of("a", "b", "c"));

// Из массива
Flux<Integer> fromArray = Flux.fromArray(new Integer[]{1, 2, 3});

// Диапазон
Flux<Integer> range = Flux.range(1, 10); // 1..10

// Пустой
Flux<String> empty = Flux.empty();

// Ошибка
Flux<String> error = Flux.error(new RuntimeException());

// Интервал (бесконечный поток с задержкой)
Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

// Программное создание с поддержкой backpressure
Flux<String> generate = Flux.generate(
    () -> 0, // начальное состояние
    (state, sink) -> {
        sink.next("value " + state);
        if (state == 10) sink.complete();
        return state + 1;
    }
);

// Создание с произвольным числом элементов
Flux<String> create = Flux.create(sink -> {
    for (String item : dataSource) {
        sink.next(item);
    }
    sink.complete();
});
```

---

## Q8. (!) В чём разница между Mono.just() и Mono.defer()?

Разница — в **моменте вычисления значения**.

**`Mono.just(value)`** вычисляет значение **сразу при создании** `Mono`, ещё до какой-либо подписки. Аргумент — это уже готовое значение: выражение в скобках выполняется в точке вызова `just()`. Если в нём есть побочный эффект (запрос, `LocalDateTime.now()`), он произойдёт немедленно и зафиксируется навсегда.

**`Mono.defer(supplier)`** откладывает вычисление: переданный `Supplier<Mono<T>>` вызывается **при каждой новой подписке**. Поэтому каждый подписчик получает свежий, заново посчитанный `Mono`.

```java
// just — значение фиксируется при создании
Mono<LocalDateTime> eagerTime = Mono.just(LocalDateTime.now());
// При первой подписке через 5 секунд — вернёт время создания, не подписки!

// defer — значение вычисляется при каждой подписке
Mono<LocalDateTime> lazyTime = Mono.defer(() -> Mono.just(LocalDateTime.now()));
// Каждый подписчик получает актуальное время подписки

// Практический пример — кэш с инвалидацией
Mono<User> userMono = Mono.defer(() ->
    cache.getUser(id)
        .switchIfEmpty(repository.findById(id))
);
```

**Эмпирическое правило:** если значение должно вычисляться заново при каждой подписке — потому что у него есть побочный эффект или оно зависит от текущего состояния (времени, кэша) — используйте `defer()`. Для константы или уже полученного значения достаточно `just()`.

**Подводный камень:** `Mono.just(blockingCall())` выполнит `blockingCall()` немедленно и заблокирует поток сборки цепочки, даже если на `Mono` никто не подпишется. Для отложенного блокирующего вызова используйте `Mono.fromCallable()` или `defer()`.

---

## Q9. Что такое Flux.create() и Flux.push() и когда их использовать?

Оба оператора — мост между «миром callback-ов» (события приходят извне: event bus, WebSocket, callback-based API) и реактивным потоком. Вы получаете `FluxSink`, в который вручную пушите элементы через `sink.next()`. Разница — в том, из скольких потоков можно безопасно вызывать `sink`.

**`Flux.create(consumer)`** допускает **многопоточную** генерацию: `FluxSink` потокобезопасен, его можно вызывать из разных потоков. Поддерживает `OverflowStrategy` — что делать, если данные приходят быстрее, чем подписчик их запрашивает.

**`Flux.push(consumer)`** — облегчённый вариант для **однопоточной** генерации (классическая push-модель: один источник callback-ов). Дешевле `create()`, но не потокобезопасен.

```java
// create() — многопоточная генерация
Flux<String> multiThreadFlux = Flux.create(sink -> {
    eventBus.subscribe(event -> sink.next(event)); // из разных потоков
    eventBus.onClose(() -> sink.complete());
}, FluxSink.OverflowStrategy.BUFFER);

// push() — однопоточная генерация (обёртка над callback-based API)
Flux<String> callbackFlux = Flux.push(sink -> {
    asyncApi.onData(sink::next);
    asyncApi.onError(sink::error);
    asyncApi.onComplete(sink::complete);
});
```

**Стратегии `OverflowStrategy`** (что делать, если подписчик не успевает):
- `BUFFER` — копить лишние элементы в буфере (по умолчанию; риск роста памяти)
- `DROP` — отбрасывать **новые** элементы при переполнении
- `LATEST` — хранить только **последний** элемент
- `ERROR` — сигнализировать `IllegalStateException`
- `IGNORE` — полностью игнорировать backpressure (нарушает спецификацию, на свой риск)

---

## Q10. Что такое Flux.generate() и чем он отличается от Flux.create()?

**`Flux.generate()`** строит поток через **синхронный** генератор с состоянием. На каждый запрос подписчика генератор вызывается ровно один раз и обязан вызвать **ровно один** `sink.next()` (плюс при необходимости `sink.complete()`/`sink.error()`).

**Почему ровно один `next()` за вызов.** Именно это ограничение даёт `generate()` встроенный backpressure: подписчик запросил один элемент — генератор произвёл один. Поэтому `generate()` идеален для синхронных, потенциально бесконечных последовательностей (числа Фибоначчи, генератор ID), где элементы дёшево считать «по требованию».

```java
// Генерация последовательности Фибоначчи
Flux<Long> fibonacci = Flux.generate(
    () -> Tuples.of(0L, 1L), // начальное состояние (a, b)
    (state, sink) -> {
        sink.next(state.getT1());         // эмитируем текущее значение
        return Tuples.of(state.getT2(),   // переходим к следующей паре
            state.getT1() + state.getT2());
    }
);

fibonacci.take(10).subscribe(System.out::println);
// 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```

Различия:

| | `generate()` | `create()` |
|--|--------------|-----------|
| Потоков | Однопоточный | Многопоточный |
| Вызовов sink.next() за итерацию | Ровно 1 | Любое количество |
| Backpressure | Встроен (pull-based) | Через `OverflowStrategy` |
| Применение | Синхронные генераторы | Async/callback-based API |

---

## Q11. (!) В чём разница между map() и flatMap()?

Главное различие — в типе результата функции: `map` ждёт **готовое значение**, `flatMap` — **другой реактивный поток**.

**`map(Function<T, R>)`** выполняет **синхронную** трансформацию «значение → значение»: каждый `T` превращается в `R` прямо здесь и сейчас. Подходит для дешёвых преобразований без обращений к БД/сети.

**`flatMap(Function<T, Publisher<R>>)`** выполняет **асинхронную** трансформацию «значение → поток»: каждый элемент превращается в `Publisher<R>` (например, в результат запроса), а `flatMap` **сливает** все эти внутренние потоки в один. Подписки на внутренние `Publisher` происходят **одновременно (конкурентно)** — поэтому несколько запросов уходят параллельно.

Простое правило выбора: если функция возвращает `Mono`/`Flux` — нужен `flatMap`; если обычное значение — `map`. Использовать `map` там, где нужен `flatMap`, — частая ошибка (см. пример ниже).

```java
// map — синхронная трансформация
Flux<String> upperCase = Flux.just("hello", "world")
    .map(String::toUpperCase); // String → String

// flatMap — асинхронная трансформация (конкурентная)
Flux<Order> orders = Flux.just(user1, user2, user3)
    .flatMap(user -> orderService.findByUser(user)); // User → Flux<Order>
    // Запросы к orderService выполняются параллельно!

// Ловушка: map с Mono внутри — НЕПРАВИЛЬНО
Flux<Mono<Order>> wrong = userFlux.map(user -> orderService.findByUser(user));
// Это Flux<Mono<Order>>, а не Flux<Order>!

// Правильно:
Flux<Order> correct = userFlux.flatMap(user -> orderService.findByUser(user));
```

**Подводный камень:** `flatMap()` **не сохраняет порядок** элементов в результирующем потоке — внутренние потоки выполняются конкурентно, и кто первым отдал результат, тот первым и попал в выход. Если порядок важен, нужен `concatMap()` или `flatMapSequential()` (см. далее).

---

## Q12. (!) В чём разница между flatMap(), concatMap() и switchMap()?

Все три принимают одну и ту же функцию `Function<T, Publisher<R>>` (значение → поток), но по-разному управляют **конкурентностью и порядком** внутренних подписок. Выбор между ними — это выбор компромисса «скорость против порядка против актуальности»:

| Оператор | Подписки | Порядок | Применение |
|----------|----------|---------|------------|
| `flatMap()` | Одновременно (concurrently) | Не гарантирован | Независимые параллельные запросы |
| `concatMap()` | Последовательно (одна за раз) | Сохраняется | Последовательные зависимые операции |
| `switchMap()` | Отменяет предыдущую при новом элементе | Только последний | Поиск по вводу (typeahead) |

```java
Flux<String> source = Flux.just("A", "B", "C");

// flatMap — конкурентно, порядок может нарушиться
source.flatMap(s -> slowOperation(s))
// Результат: B, A, C (зависит от скорости операций)

// concatMap — последовательно, порядок сохранён
source.concatMap(s -> slowOperation(s))
// Результат: A, B, C (всегда)

// switchMap — при появлении "B" отменяет обработку "A"
source.switchMap(s -> slowOperation(s))
// Результат: C (только последний успел завершиться)
```

Коротко о сути каждого: `flatMap` — «запусти всё сразу, порядок не важен»; `concatMap` — «по очереди, строго по порядку»; `switchMap` — «важен только самый свежий запрос, старые отменяй». Последнее идеально для typeahead-поиска, где промежуточные запросы уже неактуальны.

Практический пример `switchMap` — поиск с дебаунсом:
```java
inputFlux
    .debounce(Duration.ofMillis(300))
    .switchMap(query -> searchService.search(query));
// Если пользователь быстро набирает, предыдущие запросы отменяются
```

---

## Q13. Что такое flatMapSequential()?

**`flatMapSequential()`** берёт лучшее от обоих операторов: внутренние `Publisher` подписываются **одновременно** (параллелизм, как у `flatMap`), но их результаты буферизуются и отдаются на выход **в исходном порядке** (как у `concatMap`).

**Чем отличается от `concatMap`.** `concatMap` запускает следующий внутренний поток только после завершения предыдущего — медленно, но порядок естественный. `flatMapSequential` запускает все сразу и лишь придерживает готовые результаты, ожидая, пока подойдёт очередь более ранних. Цена этого — буфер: если первый элемент обрабатывается долго, результаты остальных копятся в памяти, ожидая его.

```java
// flatMapSequential — параллельные запросы + сохранение порядка
Flux.range(1, 5)
    .flatMapSequential(i -> fetchFromDB(i)) // запросы параллельны
    .subscribe(System.out::println);        // но результат: 1, 2, 3, 4, 5
```

| Оператор | Скорость | Порядок |
|----------|----------|---------|
| `flatMap()` | Быстрый | Не гарантирован |
| `concatMap()` | Медленный | Гарантирован |
| `flatMapSequential()` | Быстрый | Гарантирован |

---

## Q14. Для чего используются операторы transform() и as()?

Оба оператора нужны, чтобы **переиспользовать готовые куски цепочки** и не дублировать одни и те же операторы по всему коду.

**`transform(Function<Flux<T>, Publisher<V>>)`** применяет функцию-трансформер ко всей цепочке **один раз при сборке** (а не при каждой подписке). Это удобно, чтобы вынести типовой набор операторов (`filter` + `map` + `timeout`) в переменную и применять его к разным потокам. Если нужно, чтобы трансформер пересобирался для каждого подписчика, есть вариант `transformDeferred`.

**`as(Function<Flux<T>, P>)`** конвертирует весь `Flux` в **другой тип** — например, свернуть его в `Mono<List<T>>` через `Flux::collectList` или передать в сторонний адаптер. По сути это просто синтаксис «применить функцию к потоку как к целому».

```java
// Переиспользуемый трансформер
Function<Flux<String>, Flux<String>> applyCommonOps = flux -> flux
    .filter(s -> !s.isEmpty())
    .map(String::trim)
    .timeout(Duration.ofSeconds(5));

Flux<String> stream1 = Flux.just("a", " b ", "c")
    .transform(applyCommonOps);

// as() — конвертация типа
Mono<List<String>> list = Flux.just("a", "b", "c")
    .as(flux -> flux.collectList());

// Или через метод-ссылку
Mono<List<String>> list2 = Flux.just("a", "b", "c")
    .as(Flux::collectList);
```

---

## Q15. Что делают операторы doOnNext(), doOnComplete(), doOnError()?

Операторы `doOn*` — это операторы **побочных эффектов**: они выполняют действие в ответ на сигнал, но **никак не меняют** сами данные и сигналы потока, а возвращают тот же `Flux`/`Mono`. Их основное назначение — логирование, метрики, трассировка, отладка — то есть всё, что нужно «подсмотреть» в потоке, не вмешиваясь в него.

**Важный нюанс:** `doOn*` не обрабатывают событие, а только реагируют на него. Например, `doOnError()` залогирует ошибку, но **не погасит** её — поток всё равно завершится с этой ошибкой. Чтобы её обработать, нужен `onErrorResume()`/`onErrorReturn()`.

```java
Flux.just(1, 2, 3)
    .doOnSubscribe(sub -> log.info("Subscribed"))
    .doOnNext(item -> log.info("Processing: {}", item))
    .doOnError(ex -> log.error("Error: {}", ex.getMessage()))
    .doOnComplete(() -> log.info("Completed"))
    .doFinally(signalType -> log.info("Finally: {}", signalType))
    .subscribe();
```

Полный список side-effect операторов:

| Оператор | Событие |
|----------|---------|
| `doOnSubscribe()` | При подписке |
| `doOnRequest()` | При `request(n)` |
| `doOnNext()` | При каждом элементе |
| `doOnError()` | При ошибке |
| `doOnComplete()` | При завершении |
| `doOnCancel()` | При отмене подписки |
| `doOnTerminate()` | При завершении или ошибке |
| `doFinally()` | В любом случае (завершение, ошибка, отмена) |
| `doOnEach()` | При любом сигнале |

---

## Q16. Какие операторы фильтрации предоставляет Project Reactor?

Фильтрующие операторы решают одну задачу — **пропустить часть элементов**, — но по разным критериям: по предикату (`filter`, `filterWhen`), по уникальности (`distinct`, `distinctUntilChanged`), по количеству/позиции (`take`, `skip`, `elementAt`) и по условию-границе (`takeWhile`, `skipWhile`). Отдельно стоит `filterWhen` — единственный **асинхронный** фильтр: его предикат возвращает `Mono<Boolean>`, что позволяет проверять элемент запросом к БД или сервису.

```java
Flux<Integer> numbers = Flux.range(1, 10);

// filter — базовый фильтр по предикату
numbers.filter(n -> n % 2 == 0) // 2, 4, 6, 8, 10

// filterWhen — асинхронный фильтр (предикат возвращает Mono<Boolean>)
numbers.filterWhen(n -> isAllowed(n)) // асинхронная проверка

// distinct — уникальные элементы
Flux.just(1, 2, 1, 3, 2).distinct() // 1, 2, 3

// distinctUntilChanged — убирает последовательные дубликаты
Flux.just(1, 1, 2, 2, 1).distinctUntilChanged() // 1, 2, 1

// take — первые N элементов
numbers.take(3) // 1, 2, 3

// takeLast — последние N элементов
numbers.takeLast(3) // 8, 9, 10

// takeWhile — пока условие истинно
numbers.takeWhile(n -> n < 5) // 1, 2, 3, 4

// skip — пропустить первые N
numbers.skip(7) // 8, 9, 10

// skipWhile — пропускать пока условие истинно
numbers.skipWhile(n -> n < 5) // 5, 6, 7, 8, 9, 10

// ignoreElements — только завершение/ошибка
numbers.ignoreElements() // Mono<Integer> (empty)

// elementAt — элемент по индексу
numbers.elementAt(4) // Mono<Integer>(5)
```

---

## Q17. Что делает оператор switchIfEmpty()?

**`switchIfEmpty(Publisher<T>)`** — это реактивный аналог `Optional.orElseGet()`: если исходный `Mono`/`Flux` завершился **пустым** (ни одного `onNext`, только `onComplete`), оператор вместо него подписывается на альтернативный `Publisher`. Типичный сценарий — каскад источников: «нет в кэше → возьми из БД → нет и там → ошибка».

```java
// Поиск в кэше, при промахе — в БД
Mono<User> user = cacheRepository.findById(id)
    .switchIfEmpty(dbRepository.findById(id))  // fallback при пустом кэше
    .switchIfEmpty(Mono.error(new UserNotFoundException(id))); // или ошибка

// Flux-вариант
Flux<Product> products = cache.getProducts()
    .switchIfEmpty(database.loadProducts());
```

**Подводный камень:** `switchIfEmpty` реагирует только на **пустоту**, но не на **ошибку**. Если источник упал с `onError`, fallback не сработает — для этого нужен `onErrorResume()`. Пустой результат и ошибка — это два разных терминальных сигнала, и операторы их не путают.

---

## Q18. Что делают операторы take() и skip()?

`take()` и `skip()` — зеркальная пара: первый **берёт** элементы (и отбрасывает остальное), второй **пропускает** элементы (и пропускает остальное дальше). У обоих есть удобные перегрузки: по количеству (`take(n)`), по времени (`take(Duration)`), по условию (`takeWhile`/`takeUntil`) и по внешнему сигналу (`takeUntilOther`). Особенно полезен `take(n)` на бесконечных потоках: набрав `n` элементов, он сам отменяет подписку на источник, поэтому `Flux.range(1, 100).take(5)` не вычисляет лишнего.

```java
// take(n) — взять первые n элементов, затем отменить подписку
Flux.range(1, 100).take(5) // 1, 2, 3, 4, 5

// take(duration) — брать элементы в течение указанного времени
Flux.interval(Duration.ofMillis(100)).take(Duration.ofSeconds(1))

// takeUntil(predicate) — брать до выполнения условия (включительно)
Flux.range(1, 10).takeUntil(n -> n == 5) // 1, 2, 3, 4, 5

// takeWhile(predicate) — брать пока условие истинно (исключая нарушившее)
Flux.range(1, 10).takeWhile(n -> n < 5) // 1, 2, 3, 4

// takeUntilOther(publisher) — брать до первого элемента другого publisher
Flux<String> data = Flux.interval(Duration.ofMillis(100)).map(Object::toString);
Mono<Void> stopSignal = Mono.delay(Duration.ofSeconds(1)).then();
data.takeUntilOther(stopSignal);

// skip(n) — пропустить первые n элементов
Flux.range(1, 10).skip(7) // 8, 9, 10

// skip(duration) — пропускать элементы в течение указанного времени
Flux.interval(Duration.ofMillis(100)).skip(Duration.ofSeconds(1))
```

---

## Q19. (!) В чём разница между merge(), concat() и zip()?

Это три способа «соединить» несколько потоков в один, и различаются они тем, **как именно** элементы источников попадают в результат:

- `merge()` — **чередует** элементы по мере поступления (как слияние двух очередей);
- `concat()` — **склеивает** потоки встык: сначала весь первый, затем весь второй;
- `zip()` — **спаривает** элементы по индексу: i-й из первого с i-м из второго.

| Оператор | Подписка | Порядок | Завершение |
|----------|----------|---------|------------|
| `merge()` | Одновременно | Перемешивается | Когда все завершились |
| `concat()` | Последовательно | Сохранён | Когда все завершились |
| `zip()` | Одновременно | По парам | Как только самый короткий |

```java
Flux<String> fast = Flux.just("F1", "F2").delayElements(Duration.ofMillis(100));
Flux<String> slow = Flux.just("S1", "S2").delayElements(Duration.ofMillis(300));

// merge — смешивает элементы по мере поступления
Flux.merge(fast, slow)
// Возможный результат: F1, F2, S1, S2

// concat — сначала все элементы первого, потом второго
Flux.concat(fast, slow)
// Всегда: F1, F2, S1, S2

// zip — объединяет попарно
Flux.zip(fast, slow)
// Результат: [F1,S1], [F2,S2] — Tuple2

// zip с функцией комбинирования
Flux.zip(fast, slow, (f, s) -> f + "+" + s)
// Результат: "F1+S1", "F2+S2"
```

**Когда что:** `concat()` — когда важна строгая последовательность (сначала загрузить A, потом B); `merge()` — когда нужно как можно быстрее собрать всё из нескольких независимых источников; `zip()` — когда результат собирается из соответствующих частей разных потоков (например, объединить ответы двух сервисов в один DTO). Помните про `zip`: он завершается, как только закончился **самый короткий** источник, — лишние элементы длинных потоков отбрасываются.

---

## Q20. Что такое combineLatest() и когда его использовать?

**`combineLatest(publisher1, publisher2, combinator)`** — при появлении нового элемента из **любого** источника берёт его и **последние известные** значения остальных источников, прогоняет через `combinator` и эмитирует результат. То есть он всегда оперирует «текущим снимком» всех входов.

Типичный сценарий — UI или конфигурация, зависящая от нескольких независимо меняющихся параметров: поменялся любой из них — пересчитываем результат с актуальными значениями всех.

```java
// Реальный пример: фильтрация по нескольким критериям
Flux<String> searchQuery = searchInput.asFlux();
Flux<Category> selectedCategory = categorySelector.asFlux();

Flux<SearchResult> results = Flux.combineLatest(
    searchQuery,
    selectedCategory,
    (query, category) -> searchService.search(query, category)
).flatMap(mono -> mono);
// При изменении query ИЛИ category — немедленно новый поиск с текущими значениями обоих
```

Отличие от `zip()`:
- `zip()` — ждёт **нового элемента** от **каждого** источника (попарное соответствие)
- `combineLatest()` — реагирует на любое обновление, используя последние значения

---

## Q21. В чём разница между zipWith() и withLatestFrom()?

Оба оператора комбинируют основной `Flux` с дополнительным, но по-разному относятся к их **темпу**.

**`zipWith(other)`** работает **симметрично и попарно**: i-й элемент основного потока ждёт i-й элемент `other`. Если один источник быстрее, он притормаживает и ждёт пару. Подходит, когда оба потока поставляют равноправные части одного результата.

**`withLatestFrom(other, combinator)`** **асимметричен**: ведущим является основной `Flux`, а `other` лишь поставляет «последнее известное значение». Каждый элемент основного потока комбинируется с тем, что было в `other` на этот момент. Если `other` ещё ничего не эмитировал — элемент основного потока **просто пропускается**. Применяется, когда один поток задаёт ритм (тики, события), а другой — фоновое состояние.

```java
Flux<Integer> ticks = Flux.interval(Duration.ofSeconds(1)).map(Long::intValue);
Flux<String> updates = externalService.getUpdates(); // нерегулярный поток

// withLatestFrom — каждый тик берёт последний апдейт
ticks.withLatestFrom(updates, (tick, update) -> tick + ": " + update)
// Если updates пуст — элементы ticks пропускаются
```

---

## Q22. (!) Какие операторы используются для обработки ошибок в Project Reactor?

В реактивном коде нельзя обернуть цепочку в `try/catch` — ошибка приходит как сигнал `onError` и завершает поток. Поэтому `Reactor` даёт набор операторов, каждый из которых перехватывает `onError` и решает его по-своему. Их удобно разделить по стратегии:

- **подменить результат:** `onErrorReturn` (статическое значение), `onErrorResume` (целый запасной поток), `onErrorComplete` (тихо завершить без ошибки);
- **переписать ошибку:** `onErrorMap` (заменить тип/обернуть);
- **повторить:** `retry`, `retryWhen`;
- **только наблюдать:** `doOnError` (логирование, ошибку не гасит);
- **по таймауту:** `timeout` (превратить «зависание» в ошибку).

| Оператор | Описание |
|----------|----------|
| `onErrorReturn(value)` | Возвращает фиксированное значение при ошибке |
| `onErrorResume(fallback)` | Переключается на другой `Publisher` |
| `onErrorMap(mapper)` | Преобразует тип ошибки |
| `onErrorComplete()` | Завершает поток при ошибке (без ошибки) |
| `doOnError(consumer)` | Side-effect при ошибке (не обрабатывает) |
| `retry(n)` | Повторяет подписку при ошибке n раз |
| `retryWhen(spec)` | Повторяет по сложной стратегии |
| `timeout(duration)` | Бросает `TimeoutException` при превышении времени |

```java
Mono<User> result = userService.findUser(id)
    .onErrorMap(DatabaseException.class, ex ->
        new ServiceException("DB unavailable", ex))    // wrap ошибки
    .onErrorResume(ServiceException.class, ex ->
        cacheService.getCachedUser(id))                // fallback
    .onErrorReturn(new User("default"))                // последний resort
    .timeout(Duration.ofSeconds(5));
```

---

## Q23. В чём разница между onErrorReturn() и onErrorResume()?

Оба заменяют упавший поток на запасной результат, но различаются гибкостью fallback-а.

**`onErrorReturn(T)`** подставляет заранее известное **статическое значение** и завершает поток успешно. Это «значение по умолчанию» — никакой логики, никаких новых асинхронных вызовов.

**`onErrorResume(Function<Throwable, Publisher<T>>)`** переключается на **целый запасной `Publisher`**, который можно построить динамически, исходя из самой ошибки. Так реализуется настоящий fallback: сходить в резервный сервис, в кэш, вернуть результат другого запроса.

**Эмпирическое правило:** нужна простая константа — `onErrorReturn`; нужно выполнить ещё одну асинхронную операцию или выбрать fallback по типу ошибки — `onErrorResume`.

```java
// onErrorReturn — простая замена на значение по умолчанию
Mono<String> result1 = apiCall()
    .onErrorReturn("default");

// onErrorReturn с типом — только для конкретного типа ошибки
Mono<String> result2 = apiCall()
    .onErrorReturn(TimeoutException.class, "timeout-default");

// onErrorResume — динамический fallback
Mono<String> result3 = primaryService.call()
    .onErrorResume(ex -> {
        log.warn("Primary failed: {}", ex.getMessage());
        return fallbackService.call(); // вызов резервного сервиса
    });

// onErrorResume с типом
Mono<User> result4 = dbRepository.findById(id)
    .onErrorResume(DataAccessException.class,
        ex -> cacheRepository.findById(id));
```

---

## Q24. Что делает оператор onErrorMap()?

**`onErrorMap(mapper)`** заменяет одно исключение другим, **не подавляя** ошибку: поток всё равно завершится с `onError`, но уже с новым типом. Это прямой реактивный аналог `catch (LowLevelEx e) { throw new HighLevelEx(e); }`.

**Зачем это нужно.** Чтобы не «протекали» детали реализации: инфраструктурные исключения (`SQLException`, `TimeoutException`) оборачивают в доменные (`UserRepositoryException`, `ServiceUnavailableException`). Верхние слои тогда ловят понятные доменные ошибки, а исходную причину сохраняют как `cause`.

```java
// Оборачивание инфраструктурных исключений в доменные
userRepository.findById(id)
    .onErrorMap(SQLException.class,
        ex -> new UserRepositoryException("Failed to find user: " + id, ex))
    .onErrorMap(TimeoutException.class,
        ex -> new ServiceUnavailableException("DB timeout", ex));

// Без типа — любая ошибка
Mono<User> result = operation()
    .onErrorMap(ex -> new CustomException("Operation failed", ex));
```

---

## Q25. (!) Как работают операторы retry() и retryWhen()?

Оба оператора при ошибке **заново подписываются** на источник. Ключевое слово — «заново»: повторяется весь `Publisher` с самого начала, а не «с места падения».

**`retry(n)`** — простейший вариант: до `n` повторов **немедленно, без паузы**. Годится разве что для быстрых идемпотентных операций; против перегруженного сервиса вреден — будет долбить его без передышки.

**`retryWhen(RetrySpec)`** — продакшен-вариант: позволяет задать задержку между попытками, экспоненциальный backoff, случайный разброс (jitter), лимит попыток и фильтр по типу ошибки (повторять только временные сбои, а не, скажем, `IllegalArgumentException`).

```java
// retry(n) — простой retry без задержки
Mono<String> result = apiCall()
    .retry(3); // до 3 повторных попыток

// retryWhen — продвинутая стратегия (экспоненциальный backoff)
Mono<String> result2 = apiCall()
    .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
        .maxBackoff(Duration.ofSeconds(5))
        .jitter(0.5)   // случайный разброс ±50%
        .filter(ex -> ex instanceof TransientException) // только для временных ошибок
        .onRetryExhaustedThrow((spec, signal) ->
            new ServiceException("Retry exhausted", signal.failure())));

// retryWhen с фиксированной задержкой
Mono<String> result3 = apiCall()
    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)));
```

**Подводный камень:** ретрай повторяет источник **целиком**. Для `Mono` это нормально, а для `Flux`, который уже успел отдать часть элементов, повтор означает повторную выдачу **всей** последовательности с нуля — это надо учитывать, особенно если у элементов есть побочные эффекты. И всегда ретраить только **идемпотентные** операции.

---

## Q26. Что такое Exceptions.propagate() и когда его применять?

**Проблема.** Лямбды операторов (`map`, `filter`, …) не объявляют `throws`, поэтому из них нельзя пробросить checked-исключение вроде `IOException` напрямую. Приходится оборачивать его в unchecked.

**Решение.** `Exceptions.propagate(Throwable)` оборачивает checked-исключение в `RuntimeException` (а `Error`/`RuntimeException` пробрасывает как есть). Внутри цепочки `Reactor` сам распознаёт такую обёртку и доставляет исходное исключение как сигнал `onError`. На приёмной стороне его разворачивают обратно через `Exceptions.unwrap()`.

```java
// Проблема — нельзя бросить IOException из лямбды
Flux.just("file.txt")
    .map(filename -> {
        try {
            return Files.readString(Path.of(filename)); // throws IOException
        } catch (IOException e) {
            throw Exceptions.propagate(e); // оборачиваем в RuntimeException
        }
    });

// Распаковка в catch-блоке
try {
    result.block();
} catch (RuntimeException e) {
    if (Exceptions.unwrap(e) instanceof IOException ioEx) {
        // обработка IOException
    }
}
```

---

## Q27. (!) Как Project Reactor реализует backpressure?

`Project Reactor` реализует backpressure через протокол `Reactive Streams`: подписчик задаёт скорость, вызывая `Subscription.request(n)`, а операторы пробрасывают этот запрос вверх по цепочке к источнику.

**Как это работает на практике.** В пределах одного потока механизм действует автоматически — каждый оператор запрашивает у предыдущего ровно столько, сколько нужно ниже по цепочке. Когда цепочка пересекает границу потоков (оператор `publishOn`), между потоками появляется внутренний буфер (`SpscArrayQueue`): он развязывает производителя и потребителя, но при этом сам ограничен по размеру и тоже соблюдает backpressure, не давая буферу разрастаться бесконтрольно.

```java
// Управление скоростью через BaseSubscriber
Flux.range(1, 1000)
    .subscribe(new BaseSubscriber<Integer>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            request(10); // запрашиваем первые 10
        }

        @Override
        protected void hookOnNext(Integer value) {
            process(value);
            if (value % 10 == 0) {
                request(10); // запрашиваем следующие 10
            }
        }
    });

// limitRate — автоматическое управление backpressure
Flux.range(1, 1000)
    .limitRate(100) // запрашивать не более 100 элементов за раз
    .publishOn(Schedulers.boundedElastic())
    .subscribe(this::process);
```

---

## Q28. Какие стратегии backpressure существуют?

Не каждый источник умеет притормаживать по запросу `request(n)`. Hot publisher (события UI, тики таймера, Kafka) и callback-based API эмитируют данные в своём темпе, игнорируя готовность потребителя. Для таких случаев `Reactor` даёт семейство операторов `onBackpressure*`, которые описывают, **что делать с лишними элементами**, когда подписчик не успевает:

- `onBackpressureBuffer` — **копить** в буфере (надёжно, но грозит ростом памяти);
- `onBackpressureDrop` — **выбрасывать новые** элементы;
- `onBackpressureLatest` — хранить только **самый свежий**;
- `onBackpressureError` — **падать** с ошибкой переполнения.

Выбор — это компромисс между полнотой данных и безопасностью по памяти:

```java
// onBackpressureBuffer — буферизует все элементы (риск OOM)
Flux.interval(Duration.ofMillis(1))
    .onBackpressureBuffer(1000) // максимальный размер буфера
    .publishOn(Schedulers.boundedElastic())
    .subscribe(this::slowProcess);

// onBackpressureBuffer с callback при переполнении
hotFlux.onBackpressureBuffer(
    1000,
    dropped -> log.warn("Dropped: {}", dropped),
    BufferOverflowStrategy.DROP_OLDEST
);

// onBackpressureDrop — отбрасывает новые элементы при переполнении
hotFlux.onBackpressureDrop(dropped -> log.warn("Dropped: {}", dropped));

// onBackpressureLatest — хранит только последний непрочитанный элемент
hotFlux.onBackpressureLatest();

// onBackpressureError — бросает OverflowException
hotFlux.onBackpressureError();
```

---

## Q29. Что делает оператор limitRate()?

**`limitRate(n)`** ограничивает backpressure сверху: даже если подписчик ниже по цепочке запрашивает неограниченно (`request(Long.MAX_VALUE)`), `limitRate` дробит это на запросы по `n` элементов. Чтобы не возникало пауз, он не ждёт полного исчерпания партии, а делает упреждающий запрос (prefetch) уже при потреблении 75% — следующая порция подгружается заранее.

**Зачем нужен.** Многие подписчики (особенно `subscribe()` без явного управления) запрашивают «всё сразу». Если источник большой, это сводит backpressure на нет. `limitRate` навязывает порционность и защищает память и нижестоящие операторы.

```java
// Запрашивать данные порциями по 50
Flux.range(1, 10000)
    .limitRate(50)  // request(50), после 75% → request(38), и т.д.
    .publishOn(Schedulers.boundedElastic())
    .subscribe(this::process);

// limitRate(highTide, lowTide) — явное управление replenish-порогом
Flux.range(1, 10000)
    .limitRate(100, 50) // запрашивать по 100, пополнять при потреблении 50
    .subscribe(this::process);
```

---

## Q30. (!) Какие Schedulers существуют в Project Reactor?

`Scheduler` — это абстракция над пулом потоков: он определяет, **в каком потоке** выполняются операторы реактивной цепочки. `Reactor` поставляет несколько готовых планировщиков, каждый заточен под свой тип нагрузки. Главное при выборе — **не смешивать блокирующие и неблокирующие задачи**: блокирующий вызов на `parallel`-планировщике способен застопорить все CPU-потоки.

| Scheduler | Описание | Применение |
|-----------|----------|------------|
| `Schedulers.immediate()` | Текущий поток | Синхронный код, тесты |
| `Schedulers.single()` | Один переиспользуемый поток | Лёгкие асинхронные задачи |
| `Schedulers.parallel()` | `N` потоков (по числу CPU) | CPU-intensive операции |
| `Schedulers.boundedElastic()` | Динамический пул, max 10×CPU потоков + очередь | Блокирующие I/O операции |
| `Schedulers.fromExecutor(e)` | Адаптер над `ExecutorService` | Интеграция с существующим пулом |
| `Schedulers.newSingle(name)` | Новый одиночный поток | Изолированные задачи |
| `Schedulers.newParallel(name)` | Новый параллельный пул | Изолированные параллельные задачи |

**Ключевое разграничение двух самых частых:** `parallel()` — для **CPU-bound** работы (вычисления), число потоков ≈ числу ядер, и блокировать их нельзя. `boundedElastic()` — для **блокирующих I/O** (JDBC, файлы, legacy-API): он держит большой пул и очередь, чтобы заблокированный поток не мешал остальной системе. Это «безопасная гавань» для блокирующего кода в реактивном приложении.

```java
// boundedElastic для блокирующих операций
Mono.fromCallable(() -> jdbcRepository.findById(id)) // блокирующий вызов
    .subscribeOn(Schedulers.boundedElastic());

// parallel для CPU-intensive
Flux.range(1, 1000)
    .parallel()
    .runOn(Schedulers.parallel())
    .map(i -> heavyComputation(i))
    .sequential()
    .subscribe();
```

---

## Q31. (!) В чём разница между subscribeOn() и publishOn()?

Оба переключают поток выполнения, но действуют на **разные части цепочки** и в разном направлении. Это одна из самых частых тем на собеседовании, поэтому важно понять механику.

**`subscribeOn(Scheduler)`** задаёт поток, в котором **запускается подписка и работает источник** — то есть влияет на всё, что **выше** по цепочке (upstream), вплоть до места создания данных. Позиция в цепочке не важна: достаточно одного `subscribeOn`, и он определяет поток с самого начала. Повторные `subscribeOn` ниже игнорируются — побеждает ближайший к источнику.

**`publishOn(Scheduler)`** переключает поток **для операторов, идущих после него** (downstream). В отличие от `subscribeOn`, позиция здесь критична, и таких переключателей может быть несколько: каждый `publishOn` начинает новый «участок» цепочки в указанном планировщике.

Короткая мнемоника: `subscribeOn` отвечает на вопрос «**где стартует** поток данных», `publishOn` — «**куда переключиться** дальше по ходу».

```
subscribeOn влияет на: [SOURCE] → [op1] → [op2] → subscribeOn → [op3]
                        ↑ меняет поток здесь

publishOn влияет на:  [SOURCE] → [op1] → publishOn → [op2] → [op3]
                                           ↑ меняет поток отсюда и дальше
```

```java
Flux.just(1, 2, 3)                          // Поток: main
    .map(i -> i * 2)                          // Поток: boundedElastic (из subscribeOn)
    .subscribeOn(Schedulers.boundedElastic()) // Устанавливает поток для источника
    .publishOn(Schedulers.parallel())         // Меняет поток для следующих операторов
    .map(i -> i + 1)                          // Поток: parallel
    .subscribe(System.out::println);          // Поток: parallel

// Практический паттерн: блокирующий источник + неблокирующая обработка
Mono.fromCallable(() -> blockingDbCall())
    .subscribeOn(Schedulers.boundedElastic()) // чтение в отдельном потоке
    .publishOn(Schedulers.parallel())         // обработка в другом потоке
    .map(this::processResult);
```

---

## Q32. Что такое ParallelFlux и когда его использовать?

**`ParallelFlux<T>`** — особая разновидность `Flux`, которая распределяет элементы по нескольким «рельсам» (rails), и каждая рельса обрабатывается своим потоком параллельно. Создаётся вызовом `flux.parallel()`, а после параллельного участка поток собирают обратно через `sequential()`.

**Важно:** сам по себе `parallel()` лишь делит элементы по рельсам, но не задаёт потоки. Реальный параллелизм включается только после `runOn(Scheduler)` — без него рельсы выполнятся всё равно последовательно.

```java
// Параллельная обработка элементов
Flux.range(1, 100)
    .parallel(4)                    // 4 рельсы (по умолчанию = CPU count)
    .runOn(Schedulers.parallel())   // указываем Scheduler
    .map(i -> heavyComputation(i))  // выполняется параллельно
    .sequential()                   // обратно в обычный Flux
    .subscribe(System.out::println);

// Параллельная обработка с агрегацией
Flux<Integer> result = Flux.range(1, 1000)
    .parallel()
    .runOn(Schedulers.parallel())
    .filter(i -> i % 2 == 0)
    .map(i -> i * i)
    .sequential()
    .reduce(0, Integer::sum)
    .flux();
```

**`ParallelFlux` vs `flatMap` + `Scheduler`:**
- `ParallelFlux` — явное распределение данных по фиксированному числу рельс; параллелизм предсказуем и ограничен числом рельс. Хорош для CPU-bound обработки большого набора элементов.
- `flatMap` + `Scheduler` — гибче (степень конкурентности задаётся параметром `concurrency`), но параллелизм менее жёстко предсказуем. Привычнее для I/O-bound задач, где каждый элемент порождает асинхронный запрос.

**Эмпирическое правило:** тяжёлые вычисления над коллекцией — `parallel().runOn(...)`; множество независимых асинхронных запросов — `flatMap`.

---

## Q33. (!) Что такое Context в Project Reactor?

**`Context`** — неизменяемая (immutable) карта пар ключ-значение, привязанная к **подписке** (а не к потоку). Это реактивная замена `ThreadLocal`.

**Почему не `ThreadLocal`.** В реактивном коде один логический поток данных свободно «перескакивает» между потоками ОС (через `publishOn`/`subscribeOn`), поэтому `ThreadLocal` теряется на границе потоков. `Context` же привязан к подписке и путешествует вместе с ней независимо от того, какой поток сейчас выполняет операторы.

**Ключевые особенности:**
- Распространяется **снизу вверх** — от `subscriber` к `source` (upstream). Это контринтуитивно, но логично: контекст пишется при подписке (внизу) и должен быть виден операторам выше, которые исполнятся уже после подписки.
- **Неизменяем:** `put()` не меняет карту, а возвращает новый `Context`.
- Читается через `Mono.deferContextual()` / `Flux.deferContextual()` или оператор `transformDeferredContextual()`.

```
Source ←--- Context --- Operator ←--- Context --- Subscriber
       (Context читается здесь)              (Context пишется здесь)
```

**Типичное применение:** сквозные данные запроса — `userId`, `traceId`, `locale`, токен авторизации, — которые нужны глубоко в цепочке, но которые не хочется тащить параметром через каждый метод.

---

## Q34. Как записывать и читать данные из Context?

**Запись** — оператором `contextWrite()`, **чтение** — через `deferContextual()` или `transformDeferredContextual()`. Из-за того что `Context` идёт снизу вверх, есть важное правило размещения: `contextWrite()` должен стоять в цепочке **ниже** (то есть позже) тех операторов, которые этот контекст читают. Иначе на момент чтения значение ещё «не дописано».

```java
// Запись — при подписке через contextWrite()
Mono<String> result = service.process()
    .contextWrite(ctx -> ctx.put("userId", "user-123"))
    .contextWrite(ctx -> ctx.put("traceId", UUID.randomUUID().toString()));

// Чтение — через deferContextual()
Mono<String> withContext = Mono.deferContextual(ctx -> {
    String userId = ctx.get("userId");
    return userService.findUser(userId);
});

// Чтение — через transformDeferredContextual()
Flux<String> data = sourceFlux
    .transformDeferredContextual((flux, ctx) ->
        flux.map(item -> ctx.getOrDefault("prefix", "") + item));

// Полный пример — MDC-логирование
public Mono<Response> handle(Request request) {
    return processRequest(request)
        .contextWrite(ctx -> ctx
            .put("traceId", request.getTraceId())
            .put("userId", request.getUserId()));
}

// В глубине цепочки читаем без явной передачи:
Mono<String> loggedOperation() {
    return Mono.deferContextual(ctx ->
        Mono.fromCallable(() -> {
            MDC.put("traceId", ctx.getOrDefault("traceId", "unknown"));
            return doWork();
        })
    );
}
```

---

## Q35. (!) В чём разница между Cold и Hot publisher?

Разница в том, **привязана ли генерация данных к подписке**.

**Cold publisher** — **ленивый**: ничего не происходит, пока на него не подписались. Каждая новая подписка запускает поток заново и получает **полную независимую** копию данных с самого начала. Классический пример — HTTP-запрос: каждый подписчик инициирует свой собственный запрос.

**Hot publisher** — **активный**: данные генерируются **сами по себе**, вне зависимости от наличия подписчиков. Подписчик подключается к уже идущему потоку и видит только то, что было эмитировано **после** его подписки; всё, что было раньше, он пропускает. Примеры — котировки акций, события UI, сообщения из Kafka-топика.

```
Cold publisher:
  Subscriber A ----подписался----> [1, 2, 3, 4, 5] (с начала)
  Subscriber B ----подписался----> [1, 2, 3, 4, 5] (с начала)

Hot publisher (поток уже идёт):
  Время: --------[1]---[2]---[3]---[4]---[5]------
  Sub A подписался в начале:  [1, 2, 3, 4, 5]
  Sub B подписался после [2]: [3, 4, 5] (1 и 2 пропущены)
```

```java
// Cold — HTTP-запрос (каждая подписка = новый запрос)
Mono<Response> coldMono = webClient.get().retrieve().bodyToMono(Response.class);

// Hot — WebSocket, Kafka-топик, UI-события
Flux<StockPrice> hotFlux = stockService.priceStream(); // уже работает
```

**По умолчанию все `Flux`/`Mono` в `Project Reactor` — Cold.** Hot-поведение нужно включать явно — операторами `share()`, `publish()`, `cache()` и т.п. (см. следующий вопрос).

---

## Q36. Как преобразовать Cold publisher в Hot?

Превратить Cold в Hot — значит сделать поток **мультикастовым**: один запуск источника, общий для всех подписчиков. Способы различаются тем, **когда источник стартует** и **что видят опоздавшие** подписчики:

- `share()` — самый простой мультикаст: стартует при первой подписке, останавливается при последней (под капотом — `publish().refCount(1)`);
- `publish().connect()` — ручной контроль момента старта;
- `publish().autoConnect(n)` — старт автоматически при `n` подписчиках;
- `publish().refCount(n)` — `autoConnect` + автоостановка, когда подписчиков снова стало 0;
- `cache()` — мультикаст с «памятью»: переигрывает (replay) ранее эмитированные элементы новым подписчикам.

```java
Flux<String> coldFlux = Flux.interval(Duration.ofMillis(500))
    .map(i -> "item-" + i);

// Способ 1: share() — multicast + автоподключение
Flux<String> hotFlux = coldFlux.share();
// Первая подписка активирует источник, последняя отменяет

// Способ 2: publish().connect() — ручное управление
ConnectableFlux<String> connectable = coldFlux.publish();
connectable.subscribe(sub1); // не запускает ещё
connectable.subscribe(sub2); // не запускает ещё
connectable.connect();       // запускает источник для всех

// Способ 3: publish().autoConnect(n) — автозапуск при n подписчиках
Flux<String> autoHot = coldFlux.publish().autoConnect(2);
// Запустится, когда подпишутся 2 подписчика

// Способ 4: publish().refCount(n) — autoConnect + автоотключение
Flux<String> refCounted = coldFlux.publish().refCount(1);
// Запускается при 1 подписчике, останавливается когда их 0

// Способ 5: cache() — replay + hot (кэширует все элементы)
Flux<String> cached = coldFlux.cache();
Flux<String> cached3 = coldFlux.cache(3); // последние 3 элемента
```

---

## Q37. Что такое ConnectableFlux?

**`ConnectableFlux<T>`** — разновидность `Flux`, которая **откладывает запуск источника до явного вызова `connect()`**. Возвращается методом `publish()`. Его главное назначение — **мультикаст**: дать нескольким подписчикам разделить одну-единственную подписку на источник вместо того, чтобы каждый запускал его заново.

**Зачем нужна задержка через `connect()`.** Она позволяет сначала подписать всех потребителей, а уже потом одним вызовом запустить поток — так гарантируется, что никто из них не пропустит начальные элементы. Это тонкий контроль над моментом старта, которого нет у `share()`.

```java
Flux<Long> source = Flux.interval(Duration.ofSeconds(1)).take(5);
ConnectableFlux<Long> connectable = source.publish();

// Подписываемся ДО запуска
connectable.subscribe(i -> System.out.println("A: " + i));
connectable.subscribe(i -> System.out.println("B: " + i));

// Теперь запускаем — оба подписчика получат одинаковые данные
Disposable connection = connectable.connect();

// Отключаем
connection.dispose();
```

Методы `ConnectableFlux`:
- `connect()` — запуск, возвращает `Disposable`
- `autoConnect(n)` — автозапуск при `n` подписчиках
- `refCount(n)` — автозапуск/автоостановка

---

## Q38. Какие операторы для работы со временем предоставляет Project Reactor?

Временны́е операторы делятся по назначению: **порождение по времени** (`interval` — периодические тики), **задержки** (`delayElements`, `delaySubscription`, `delayElement`), **ограничение по времени** (`timeout`) и **измерение времени** (`elapsed`, `timestamp`). Все они по умолчанию работают на `Schedulers.parallel()`, поэтому не блокируют поток, в котором собрана цепочка.

```java
// interval — периодический поток
Flux<Long> ticks = Flux.interval(Duration.ofSeconds(1)); // 0, 1, 2, 3...

// delayElements — задержка между каждым элементом
Flux.just("a", "b", "c")
    .delayElements(Duration.ofMillis(500));

// delaySubscription — задержка перед подпиской
Flux.range(1, 5)
    .delaySubscription(Duration.ofSeconds(2));

// delay — Mono: задержка перед эмиссией
Mono.just("result")
    .delayElement(Duration.ofSeconds(1));

// timeout — прерывает если нет элементов за указанное время
Mono<String> withTimeout = apiCall()
    .timeout(Duration.ofSeconds(5));

// timeout с fallback
Mono<String> withFallback = apiCall()
    .timeout(Duration.ofSeconds(5), Mono.just("fallback"));

// elapsed — добавляет время с последнего элемента
Flux.interval(Duration.ofMillis(100))
    .elapsed() // Flux<Tuple2<Long, Long>> — (прошло мс, значение)
    .take(5)
    .subscribe(t -> System.out.println(t.getT1() + "ms: " + t.getT2()));

// timestamp — добавляет timestamp
Flux.just("a", "b").timestamp()
// Flux<Tuple2<Long, String>> — (Unix ms, значение)
```

---

## Q39. Что делает оператор timeout()?

**`timeout(Duration)`** — страховка от «зависания»: если очередной элемент не пришёл за отведённое время, оператор прерывает ожидание, отменяет подписку на источник и сигнализирует `TimeoutException`.

**Важный нюанс для `Flux`:** таймаут отсчитывается **между соседними элементами**, а не на весь поток. Каждый новый `onNext` сбрасывает таймер. То есть `timeout(5s)` на `Flux` означает «не больше 5 секунд между двумя элементами», а не «весь поток за 5 секунд».

```java
// Базовый timeout
Mono<User> user = userService.findById(id)
    .timeout(Duration.ofSeconds(3));

// Timeout с fallback вместо ошибки
Mono<User> userWithFallback = userService.findById(id)
    .timeout(Duration.ofSeconds(3), Mono.just(User.anonymous()));

// timeout для Flux — таймаут ожидания КАЖДОГО следующего элемента
Flux<Data> stream = dataSource.stream()
    .timeout(Duration.ofSeconds(5)); // 5 сек на каждый элемент

// Обработка TimeoutException
Mono<Response> result = apiCall()
    .timeout(Duration.ofSeconds(2))
    .onErrorResume(TimeoutException.class,
        ex -> Mono.just(Response.timeout()));
```

---

## Q40. (!) Что такое StepVerifier и как его использовать?

**`StepVerifier`** — основной инструмент тестирования реактивных потоков из модуля `reactor-test`. Идея проста: вы описываете **ожидаемый сценарий** сигналов (`onNext` со значениями, затем `onComplete` или `onError`), а `StepVerifier` подписывается на поток и пошагово сверяет реальность с ожиданием.

**Почему нельзя просто `assertEquals`.** Реактивный поток асинхронен и «ленив» — пока на него не подписались, ничего не происходит. Терминальный вызов `.verify()` как раз и **запускает** подписку и **дожидается** завершения потока, после чего проваливает тест при первом расхождении.

```java
// Зависимость
// testImplementation 'io.projectreactor:reactor-test'

// Базовое использование
@Test
void testFlux() {
    Flux<Integer> flux = Flux.just(1, 2, 3);

    StepVerifier.create(flux)
        .expectNext(1)
        .expectNext(2, 3)        // можно несколько сразу
        .expectComplete()
        .verify();               // запускает и ждёт завершения
}

// Проверка ошибки
@Test
void testError() {
    Mono<String> failingMono = Mono.error(new RuntimeException("oops"));

    StepVerifier.create(failingMono)
        .expectErrorMessage("oops")
        .verify();
}

// Более детальная проверка ошибки
StepVerifier.create(failingMono)
    .expectErrorMatches(ex ->
        ex instanceof RuntimeException && ex.getMessage().equals("oops"))
    .verify();

// Проверка с условием на элемент
StepVerifier.create(userFlux)
    .expectNextMatches(user -> user.isActive())
    .expectNextCount(4)          // следующие 4 без проверки содержимого
    .expectComplete()
    .verify(Duration.ofSeconds(5)); // с таймаутом

// Потребление N элементов и завершение вручную
StepVerifier.create(Flux.range(1, 100))
    .expectNextCount(10)
    .thenCancel()               // отменяем после 10 элементов
    .verify();
```

---

## Q41. Как тестировать потоки, зависящие от времени?

**Проблема.** Поток с `interval(1h)` или `timeout(5s)` в обычном тесте заставил бы тест реально ждать — час или пять секунд. Это недопустимо.

**Решение — `StepVerifier.withVirtualTime()`.** Он подменяет планировщик на виртуальный, где время **перематывается мгновенно** командами `thenAwait(Duration)` и `expectNoEvent(Duration)`. Тест временны́х операторов выполняется за миллисекунды, оставаясь детерминированным.

```java
// Без виртуального времени тест занял бы 1 час:
// Flux.interval(Duration.ofHours(1)).take(3)

// С виртуальным временем — мгновенно
@Test
void testWithVirtualTime() {
    StepVerifier.withVirtualTime(() ->
            Flux.interval(Duration.ofHours(1)).take(3))
        .expectSubscription()
        .expectNoEvent(Duration.ofHours(1)) // перематываем 1 час
        .expectNext(0L)
        .thenAwait(Duration.ofHours(1))     // перематываем ещё 1 час
        .expectNext(1L)
        .thenAwait(Duration.ofHours(1))
        .expectNext(2L)
        .expectComplete()
        .verify();
}

// Тестирование timeout
@Test
void testTimeout() {
    StepVerifier.withVirtualTime(() ->
            Mono.never()
                .timeout(Duration.ofSeconds(5)))
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(5))
        .expectError(TimeoutException.class)
        .verify();
}
```

**Подводный камень:** в `withVirtualTime()` поток передаётся именно `Supplier`-ом (лямбдой `() -> ...`), а не готовым значением. Это не прихоть API: `StepVerifier` должен сначала установить виртуальный планировщик, и только затем собрать поток внутри лямбды — иначе операторы захватят реальный планировщик и виртуальное время не сработает.

---

## Q42. Что такое TestPublisher?

**`TestPublisher<T>`** — управляемый источник из `reactor-test`: в тесте вы **вручную** решаете, когда и какой сигнал отправить (`next`, `error`, `complete`). Это противоположность готовому `Flux.just(...)`, где последовательность фиксирована заранее.

**Когда нужен.** Когда тестируется реакция на **точную хореографию** событий: «пришло два элемента, затем ошибка», «долгая пауза, потом завершение». Также `TestPublisher.createNoncompliant(...)` умеет намеренно **нарушать** спецификацию `Reactive Streams` (например, слать `null`) — чтобы проверить, как ваш код держит удар от некорректного источника.

```java
@Test
void testWithTestPublisher() {
    TestPublisher<String> publisher = TestPublisher.create();

    StepVerifier.create(publisher.flux()
            .map(String::toUpperCase))
        .then(() -> publisher.next("hello"))    // эмитируем элемент
        .expectNext("HELLO")
        .then(() -> publisher.next("world"))
        .expectNext("WORLD")
        .then(publisher::complete)              // завершаем
        .expectComplete()
        .verify();
}

// TestPublisher для тестирования обработки ошибок
@Test
void testErrorHandling() {
    TestPublisher<Integer> publisher = TestPublisher.create();

    StepVerifier.create(publisher.flux()
            .onErrorReturn(-1))
        .then(() -> publisher.next(1, 2))
        .expectNext(1, 2)
        .then(() -> publisher.error(new RuntimeException("test error")))
        .expectNext(-1)
        .expectComplete()
        .verify();
}

// Nonconforming publisher — нарушает Reactive Streams spec (для тестирования устойчивости)
TestPublisher<String> nonConforming = TestPublisher.createNoncompliant(
    TestPublisher.Violation.ALLOW_NULL);
```

---

## Q43. (!) Как отлаживать реактивные цепочки в Project Reactor?

**Главная сложность отладки.** Сборка цепочки и её выполнение разнесены во времени: операторы создаются в одном месте, а ошибка возникает позже, при подписке, в другом потоке. Поэтому стандартный стек-трейс показывает внутренности `Reactor`, а не ту строку вашего кода, где «настоящая» проблема. `Reactor` даёт несколько инструментов, чтобы вернуть в трейс смысл:

**1. `log()` — логирование всех сигналов:**
```java
Flux.just(1, 2, 3)
    .log("MyFlux") // логирует onSubscribe, request, onNext, onComplete
    .map(i -> i * 2)
    .log("AfterMap")
    .subscribe();
```

**2. `checkpoint()` — именованная точка для стек-трейсов:**
```java
Flux.just(1, 2, 3)
    .map(i -> i / 0) // ArithmeticException
    .checkpoint("after-map") // добавляет к стек-трейсу
    .subscribe();
// Стек-трейс будет содержать "Assembly site of checkpoint after-map"
```

**3. `Hooks.onOperatorDebug()` — полные стек-трейсы для всех операторов:**
```java
// В тестах или при запуске (влияет на производительность!)
Hooks.onOperatorDebug();

// Альтернатива: ReactorDebugAgent (агент без overhead в продакшене)
// ReactorDebugAgent.init();
```

**4. `doOnEach()` — полный трейсинг каждого сигнала:**
```java
Flux.just(1, 2, 3)
    .doOnEach(signal -> {
        if (signal.isOnNext()) log.debug("Next: {}", signal.get());
        if (signal.isOnError()) log.error("Error", signal.getThrowable());
    });
```

---

## Q44. Что такое checkpoint() и как он помогает при отладке?

**`checkpoint(description)`** — помечает место в цепочке именованной «точкой сборки». Если ниже по цепочке возникнет ошибка, `Reactor` допишет в её трассировку запись «Assembly site of checkpoint ...», и по этой метке сразу видно, **какой участок** цепочки оказался проблемным.

**Чем удобнее `Hooks.onOperatorDebug()`.** `checkpoint` — точечный и **дешёвый**: он расставляется вручную в подозрительных местах и не замедляет всё приложение. Глобальный дебаг-хук собирает трейсы для каждого оператора и потому тяжёл. Поэтому в продакшене предпочитают `checkpoint` (или `ReactorDebugAgent`).

```java
// Без checkpoint — стек-трейс нечитаем
Flux.just(1, 2, 0, 3)
    .map(i -> 10 / i)
    .subscribe(System.out::println, System.err::println);
// Error: ArithmeticException, но непонятно где в цепочке

// С checkpoint — понятно где произошла ошибка
Flux.just(1, 2, 0, 3)
    .checkpoint("before-division")
    .map(i -> 10 / i)
    .checkpoint("after-division")
    .subscribe(System.out::println, ex -> {
        System.err.println(ex.getMessage());
        // Стек-трейс содержит "Assembly site of checkpoint after-division"
    });

// checkpoint(description, forceStackTrace) — принудительный полный стек-трейс
Flux.just(1, 0).map(i -> 10 / i)
    .checkpoint("division", true);
```

---

## Q45. Что такое Hooks и как использовать Hooks.onOperatorDebug()?

**`Hooks`** — класс для **глобальной** настройки поведения `Reactor`. В отличие от точечных `log()`/`checkpoint()`, хуки влияют сразу на все операторы во всём приложении и позволяют централизованно перехватывать диагностические события.

**`Hooks.onOperatorDebug()`** включает сбор полного стек-трейса сборки для **каждого** оператора. Это мощно, но дорого: на каждом операторе захватывается стек, что заметно бьёт по производительности. Поэтому его держат включённым только при отладке.

Помимо отладки, `Hooks` ловят «потерянные» сигналы: `onErrorDropped` — ошибки, которые некому было доставить (например, пришли после отмены), `onNextDropped` — отброшенные элементы. Эти хуки полезны, чтобы такие ситуации не уходили в тишину, а попадали в лог.

```java
// Включить полные стек-трейсы для ВСЕХ операторов
// Вызывать один раз при старте (влияет на производительность!)
Hooks.onOperatorDebug();

// Сбросить дебаг-хук
Hooks.resetOnOperatorDebug();

// Перехват ошибок для глобального логирования
Hooks.onErrorDropped(ex -> log.error("Unhandled error in Reactor", ex));

// Перехват отброшенных элементов
Hooks.onNextDropped(item -> log.warn("Dropped item: {}", item));

// Альтернатива для продакшена — ReactorDebugAgent (Java Agent)
// В pom.xml: io.projectreactor:reactor-tools
// В коде:
ReactorDebugAgent.init();
// ИЛИ через командную строку:
// -javaagent:reactor-tools.jar
```

**Рекомендация для продакшена:** вместо `Hooks.onOperatorDebug()` используйте `ReactorDebugAgent` (модуль `reactor-tools`). Он даёт ту же информацию о месте сборки, но инструментирует байткод один раз при загрузке классов, поэтому накладные расходы во время работы минимальны.

---

## Q46. (!) Как Project Reactor интегрируется со Spring WebFlux?

`Spring WebFlux` построен **поверх** `Project Reactor` — `Mono`/`Flux` являются «родным языком» всего реактивного стека Spring. Это значит, что реактивные типы проходят сквозь все слои без преобразований: от веб-слоя до доступа к данным. Интеграция охватывает четыре ключевые точки:

**Контроллеры** возвращают `Mono`/`Flux`:
```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/users")
    public Flux<User> getUsers() {
        return userService.findAll();
    }

    // Server-Sent Events
    @GetMapping(value = "/users/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamUsers() {
        return userService.findAll()
            .delayElements(Duration.ofMillis(100));
    }
}
```

**`WebClient`** — реактивный HTTP-клиент:
```java
WebClient client = WebClient.create("http://api.example.com");

Mono<User> user = client.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class);
```

**`R2DBC`** — реактивный доступ к БД:
```java
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Flux<User> findByActiveTrue();
    Mono<User> findByEmail(String email);
}
```

**Обработка ошибок** через `@ExceptionHandler`:
```java
@ExceptionHandler(UserNotFoundException.class)
public Mono<ResponseEntity<ErrorResponse>> handleNotFound(UserNotFoundException ex) {
    return Mono.just(ResponseEntity.notFound().build());
}
```

---

## Q47. Почему нельзя вызывать block() в WebFlux-приложении?

**`block()`** синхронно **останавливает** вызывающий поток до завершения реактивного потока. В `WebFlux` это разрушает саму основу реактивной модели и опасно по двум причинам:

1. **Деградация производительности (главная причина).** `WebFlux` обслуживает все запросы небольшим пулом event-loop-потоков `Netty` (по числу ядер). Реактивная модель эффективна именно потому, что поток не простаивает в ожидании I/O. `block()` же занимает один из этих немногочисленных потоков «вхолостую» — и пропускная способность сервера резко падает.
2. **Возможный дедлок.** Если заблокировать event-loop-поток, а результат, которого он ждёт, должен прийти на том же самом потоке, — система встаёт намертво.

```java
// НЕПРАВИЛЬНО — вызов block() в reactive chain
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id).block(); // ДЕДЛОК ВОЗМОЖЕН!
    return Mono.just(transform(user));
}

// ПРАВИЛЬНО — полная реактивная цепочка
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(this::transform);
}

// Если нужна блокирующая операция — изолируем в boundedElastic
Mono<Result> result = Mono.fromCallable(() -> blockingOperation())
    .subscribeOn(Schedulers.boundedElastic());
```

Именно поэтому `Spring WebFlux` выбрасывает `IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread xxx` — это встроенная защита, которая ловит случайный `block()` на event-loop-потоке. Если же блокирующий вызов действительно неизбежен (legacy-API, JDBC), его нужно изолировать на `Schedulers.boundedElastic()`, как показано выше, чтобы он не трогал потоки `Netty`.

---

## Q48. (!) Как пробросить `ThreadLocal`-контекст в реактивную цепочку (context-propagation)?

В реактивном коде операторы выполняются на разных потоках, поэтому обычный `ThreadLocal` (MDC для логов, `SecurityContext`, контекст трейсинга) «теряется» между шагами. Решение — библиотека `io.micrometer:context-propagation`, мост между `ThreadLocal` и `Reactor Context`.

Механизм:

- `ThreadLocalAccessor` — описывает, как читать/писать конкретный `ThreadLocal`; регистрируется в `ContextRegistry`.
- `ContextSnapshot` — захватывает значения зарегистрированных `ThreadLocal` и восстанавливает их вокруг выполнения.

В `Reactor` это подключается оператором `contextCapture()` или глобально:

```java
Hooks.enableAutomaticContextPropagation(); // один раз на старте приложения

Mono.deferContextual(ctx -> service.call())
    .contextCapture(); // захватит зарегистрированные ThreadLocal в Reactor Context
```

Типовые применения: `traceId` в MDC через всю цепочку логов, `SecurityContext` Spring Security в WebFlux, спаны Micrometer/OpenTelemetry через async-границы.

**Итог:** `context-propagation` связывает императивный `ThreadLocal`-мир с `Reactor Context`. Без неё MDC и `SecurityContext` пропадают при первом же переключении потока в реактивной цепочке.

---

## See also

- [RxJava](rxjava-interview.md) — альтернативная реактивная библиотека, похожая концепция `Observable`/`Flowable`
- [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md) — реактивный веб-стек на основе `Project Reactor`
- [Java Concurrency](../programming-languages/java/java-concurrency-interview.md) — основы многопоточности и асинхронного программирования в `Java`
- [Java Stream API](../programming-languages/java/java-stream-interview.md) — синхронный аналог для работы с коллекциями, схожий API
- [Apache Kafka](../messaging/kafka-interview.md) — типичный источник данных для реактивных приложений
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — экосистема, в которой используется `Spring WebFlux`
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) — сравнение с реактивными репозиториями `R2DBC`
- [Java 8](../programming-languages/java/java-8-interview.md) — `CompletableFuture` как альтернативный подход к асинхронности
- [Kotlin Coroutines](../programming-languages/kotlin/kotlin-coroutines-interview.md) — сравнение с корутинами как альтернативой реактивному программированию
- [Распределённые системы](../architecture/distributed-systems-interview.md) — реактивное программирование в контексте микросервисов

- [Reactive Patterns](reactive-patterns-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](webflux-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
- [Cats Effect и ZIO](../programming-languages/scala/scala-effects-interview.md) — эффект-системы Scala с fibers и structured concurrency
