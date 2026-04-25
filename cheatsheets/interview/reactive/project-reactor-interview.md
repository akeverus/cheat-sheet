---
title: "Вопросы на собеседовании: Project Reactor"
description: "Комплексное руководство по Project Reactor: Mono, Flux, операторы, backpressure, Schedulers, Context, тестирование, отладка, Hot/Cold publishers"
tags:
  - interview
  - reactive
  - project-reactor-interview
aliases:
  - "Project Reactor interview"
  - "Project Reactor собеседование"
  - "Project Reactor вопросы"
  - "Reactor Flux Mono"
  - "Reactor interview questions"
difficulty: "intermediate"
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
- [Q26. Что такое Exceptions.propagate() и когда его применять?](#q26-что-такое-exceptionspropegate-и-когда-его-применять)

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

---

## Q1. (!) Что такое реактивное программирование?

**Реактивное программирование** — парадигма, ориентированная на работу с асинхронными потоками данных и распространением изменений. Основана на паттерне `Observer`: компоненты системы реагируют на события (данные, ошибки, завершение), а не активно запрашивают результаты.

Ключевые принципы (из [Reactive Manifesto](https://www.reactivemanifesto.org/)):
- **Responsive** — система отвечает быстро и стабильно
- **Resilient** — устойчива к отказам
- **Elastic** — масштабируется под нагрузку
- **Message Driven** — взаимодействие через асинхронные сообщения

В отличие от императивного подхода, реактивный код описывает **что** происходит с данными, а не **как** их получить:

```java
// Императивный подход (блокирующий)
User user = userRepository.findById(id); // блокирует поток
Order order = orderRepository.findByUser(user); // ещё блокировка
return order;

// Реактивный подход (неблокирующий)
return userRepository.findById(id)          // Mono<User>
    .flatMap(user -> orderRepository.findByUser(user)); // Mono<Order>
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q2. (!) Что такое спецификация Reactive Streams?

**Reactive Streams** — спецификация для асинхронной обработки потоков данных с поддержкой backpressure. Включена в `JDK 9` как `java.util.concurrent.Flow`.

Спецификация определяет 4 интерфейса:

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

`Project Reactor` полностью реализует спецификацию `Reactive Streams`. `Flux` и `Mono` реализуют интерфейс `Publisher<T>`.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q3. Что такое Project Reactor и чем он отличается от RxJava?

**`Project Reactor`** — реактивная библиотека для `JVM` от команды `Spring/Pivotal`, реализующая спецификацию `Reactive Streams`. Является основой `Spring WebFlux`.

Основные различия между `Project Reactor` и `RxJava`:

| Характеристика | Project Reactor | RxJava 3 |
|----------------|-----------------|----------|
| Основные типы | `Mono<T>`, `Flux<T>` | `Single`, `Maybe`, `Observable`, `Flowable` |
| Backpressure | Встроен во все типы | Только `Flowable` |
| Интеграция со Spring | Нативная | Через адаптер |
| Nullable | Не допускает `null` | Зависит от типа |
| Java версия | Java 8+ | Java 8+ |
| `Context` | Есть (`Context`) | Отсутствует аналог |

`Reactor` разработан специально для экосистемы `Spring` и тесно интегрирован с `Spring WebFlux`, `Spring Data Reactive`, `Spring Security` и другими компонентами.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q4. (!) Что такое backpressure и почему это важно?

**Backpressure** — механизм управления потоком данных между производителем (`Publisher`) и потребителем (`Subscriber`), при котором потребитель контролирует скорость получения данных.

**Проблема без backpressure:** если производитель генерирует данные быстрее, чем потребитель их обрабатывает, буферы переполняются → `OutOfMemoryError` или потеря данных.

```
Без backpressure:
Producer ----[1,2,3,4,5,6,7,8...]----> Consumer (обрабатывает 1 в секунду)
                                        ↑ переполнение буфера!

С backpressure:
Producer ----[1]---[2]---[3]-------> Consumer
             ↑     ↑     ↑
             request(1) → обработал → request(1) → ...
```

В `Reactive Streams` backpressure реализован через метод `Subscription.request(n)`: подписчик запрашивает ровно столько элементов, сколько может обработать. Производитель не имеет права отправить больше, чем запрошено.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q5. (!) В чём разница между Mono и Flux?

**`Mono<T>`** — реактивный тип, представляющий **0 или 1 элемент**. Аналог `Optional` для асинхронного контекста. Завершается сигналом `onComplete` (с элементом или без) или `onError`.

**`Flux<T>`** — реактивный тип, представляющий **0 или N элементов** (потенциально бесконечный поток). Завершается сигналом `onComplete` или `onError` после произвольного числа `onNext`.

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

Когда что использовать:
- `Mono` — поиск по ID, сохранение, HTTP-запрос, подсчёт
- `Flux` — список результатов, стриминг событий, чтение файла построчно


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q6. Какие способы создания Mono существуют?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q7. Какие способы создания Flux существуют?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q8. (!) В чём разница между Mono.just() и Mono.defer()?

**`Mono.just(value)`** — **eager** (жадный): значение вычисляется **немедленно** при создании `Mono`, до подписки. Если вычисление имеет побочные эффекты, они произойдут сразу.

**`Mono.defer(supplier)`** — **lazy** (ленивый): `Supplier<Mono<T>>` вызывается **при каждой подписке**. Каждый подписчик получает свежий `Mono`.

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

Правило: если создание значения имеет побочные эффекты или должно быть свежим для каждого подписчика — используйте `defer()`.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q9. Что такое Flux.create() и Flux.push() и когда их использовать?

**`Flux.create(consumer)`** — позволяет программно создавать элементы с поддержкой многопоточной генерации. `FluxSink` можно передавать в несколько потоков. Поддерживает `OverflowStrategy` для управления backpressure.

**`Flux.push(consumer)`** — аналогично `create()`, но предназначен только для **однопоточной** генерации событий (push-модель).

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

`OverflowStrategy` для `create()`:
- `BUFFER` — буферизует лишние элементы (по умолчанию)
- `DROP` — отбрасывает новые элементы при переполнении
- `LATEST` — хранит только последний элемент
- `ERROR` — генерирует `IllegalStateException`
- `IGNORE` — полностью игнорирует backpressure


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q10. Что такое Flux.generate() и чем он отличается от Flux.create()?

**`Flux.generate()`** — создаёт поток через **синхронный** генератор с состоянием. Генератор вызывается по одному разу и должен вызвать ровно один `sink.next()` или `sink.complete()`/`sink.error()`.

```java
// Генерация последовательности Фибоначчи
Flux<Long> fibonacci = Flux.generate(
    () -> Tuple2.of(0L, 1L), // начальное состояние (a, b)
    (state, sink) -> {
        sink.next(state.getT1());         // эмитируем текущее значение
        return Tuple2.of(state.getT2(),   // переходим к следующей паре
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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q11. (!) В чём разница между map() и flatMap()?

**`map(Function<T, R>)`** — **синхронная** трансформация: преобразует каждый элемент типа `T` в `R`. Не подходит для операций, возвращающих `Mono`/`Flux`.

**`flatMap(Function<T, Publisher<R>>)`** — **асинхронная** трансформация: преобразует каждый элемент в `Publisher<R>` и **сливает** их в единый поток. Подписки на внутренние `Publisher` происходят **одновременно** (конкурентно).

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

**Важно:** `flatMap()` не гарантирует порядок элементов в результирующем потоке, т.к. подписки конкурентны.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q12. (!) В чём разница между flatMap(), concatMap() и switchMap()?

Все три оператора принимают `Function<T, Publisher<R>>`, но по-разному управляют конкурентностью:

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

Практический пример `switchMap` — поиск с дебаунсом:
```java
inputFlux
    .debounce(Duration.ofMillis(300))
    .switchMap(query -> searchService.search(query));
// Если пользователь быстро набирает, предыдущие запросы отменяются
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q13. Что такое flatMapSequential()?

**`flatMapSequential()`** — гибрид между `flatMap()` и `concatMap()`: внутренние `Publisher` подписываются **одновременно** (как `flatMap`), но результаты буферизуются и **упорядочиваются** (как `concatMap`).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q14. Для чего используются операторы transform() и as()?

**`transform(Function<Flux<T>, Publisher<V>>)`** — применяет функцию-трансформер ко всей цепочке **при сборке** (во время сборки оператора, не при подписке). Позволяет повторно использовать последовательности операторов.

**`as(Function<Flux<T>, P>)`** — преобразует `Flux` в другой тип (например, `Mono`, `List`, другой Publisher).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q15. Что делают операторы doOnNext(), doOnComplete(), doOnError()?

Операторы `doOn*` — **side-effect операторы**: они выполняют действие при наступлении события, **не изменяя** поток данных. Возвращают исходный тип `Flux`/`Mono`.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q16. Какие операторы фильтрации предоставляет Project Reactor?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q17. Что делает оператор switchIfEmpty()?

**`switchIfEmpty(Publisher<T>)`** — если исходный `Mono`/`Flux` завершается **пустым** (без элементов), подписывается на альтернативный `Publisher`. Используется как реактивный аналог `orElse`.

```java
// Поиск в кэше, при промахе — в БД
Mono<User> user = cacheRepository.findById(id)
    .switchIfEmpty(dbRepository.findById(id))  // fallback при пустом кэше
    .switchIfEmpty(Mono.error(new UserNotFoundException(id))); // или ошибка

// Flux-вариант
Flux<Product> products = cache.getProducts()
    .switchIfEmpty(database.loadProducts());
```

Важно: `switchIfEmpty` срабатывает только при **пустом** потоке, не при ошибке. Для ошибок используйте `onErrorResume()`.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q18. Что делают операторы take() и skip()?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q19. (!) В чём разница между merge(), concat() и zip()?

Три основных оператора для комбинирования нескольких `Publisher`:

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q20. Что такое combineLatest() и когда его использовать?

**`combineLatest(publisher1, publisher2, combinator)`** — при получении нового элемента из **любого** источника объединяет его с **последним известным** значением из остальных источников.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q21. В чём разница между zipWith() и withLatestFrom()?

**`zipWith(other)`** — объединяет текущий `Flux` с другим **попарно**, ожидая элементы от обоих.

**`withLatestFrom(other, combinator)`** — комбинирует каждый элемент текущего `Flux` с **последним** значением из `other`. Если `other` ещё не эмитировал — элемент пропускается.

```java
Flux<Integer> ticks = Flux.interval(Duration.ofSeconds(1)).map(Long::intValue);
Flux<String> updates = externalService.getUpdates(); // нерегулярный поток

// withLatestFrom — каждый тик берёт последний апдейт
ticks.withLatestFrom(updates, (tick, update) -> tick + ": " + update)
// Если updates пуст — элементы ticks пропускаются
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q22. (!) Какие операторы используются для обработки ошибок в Project Reactor?

`Project Reactor` предоставляет богатый набор операторов для обработки ошибок:

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q23. В чём разница между onErrorReturn() и onErrorResume()?

**`onErrorReturn(T)`** — при любой ошибке (или ошибке определённого типа) возвращает **статическое значение** и завершает поток успешно.

**`onErrorResume(Function<Throwable, Publisher<T>>)`** — при ошибке переключается на **другой `Publisher`**. Позволяет реализовать динамический fallback.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q24. Что делает оператор onErrorMap()?

**`onErrorMap(mapper)`** — преобразует тип `Throwable` в другой без изменения потока данных. Аналог `catch (LowLevelEx e) { throw new HighLevelEx(e); }` в императивном коде.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q25. (!) Как работают операторы retry() и retryWhen()?

**`retry(n)`** — при ошибке **повторно подписывается** на источник до `n` раз. Простая стратегия без задержки.

**`retryWhen(RetrySpec)`** — расширенная стратегия с возможностью настройки задержки, jitter, максимального числа попыток и фильтрации по типу ошибки.

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

Важно: `retry()` повторно подписывается на весь `Publisher` с самого начала. Для `Flux` это означает повторение всей последовательности.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q26. Что такое Exceptions.propagate() и когда его применять?

В реактивных цепочках нельзя бросать checked-исключения из лямбд. `Exceptions.propagate(Throwable)` оборачивает checked-исключение в `RuntimeException` (или перебрасывает `Error`/`RuntimeException` как есть).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q27. (!) Как Project Reactor реализует backpressure?

`Project Reactor` реализует backpressure через протокол `Reactive Streams`: подписчик контролирует скорость через `Subscription.request(n)`.

Встроенный механизм работает автоматически для операторов в одном потоке. При переходе между потоками (через `publishOn`) между операторами устанавливается внутренний буфер (`SpscArrayQueue`).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q28. Какие стратегии backpressure существуют?

Когда источник данных не поддерживает backpressure (например, Hot publisher или callback-based API), `Project Reactor` предоставляет операторы для управления переполнением:

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q29. Что делает оператор limitRate()?

**`limitRate(n)`** — управляет backpressure, запрашивая у источника элементы порциями по `n`. Дополнительно, при потреблении 75% буфера автоматически запрашивает следующую партию (prefetch).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q30. (!) Какие Schedulers существуют в Project Reactor?

`Scheduler` определяет, в каком потоке выполняются операторы реактивной цепочки.

| Scheduler | Описание | Применение |
|-----------|----------|------------|
| `Schedulers.immediate()` | Текущий поток | Синхронный код, тесты |
| `Schedulers.single()` | Один переиспользуемый поток | Лёгкие асинхронные задачи |
| `Schedulers.parallel()` | `N` потоков (по числу CPU) | CPU-intensive операции |
| `Schedulers.boundedElastic()` | Динамический пул, max 10×CPU потоков + очередь | Блокирующие I/O операции |
| `Schedulers.fromExecutor(e)` | Адаптер над `ExecutorService` | Интеграция с существующим пулом |
| `Schedulers.newSingle(name)` | Новый одиночный поток | Изолированные задачи |
| `Schedulers.newParallel(name)` | Новый параллельный пул | Изолированные параллельные задачи |

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q31. (!) В чём разница между subscribeOn() и publishOn()?

Оба оператора переключают поток выполнения, но на **разные части цепочки**:

**`subscribeOn(Scheduler)`** — меняет поток выполнения **от источника** (влияет на операторы выше в цепочке, включая создание источника). Один `subscribeOn` влияет на весь upstream.

**`publishOn(Scheduler)`** — меняет поток выполнения **для операторов после** него (downstream). Каждый `publishOn` в цепочке меняет поток для следующего блока операторов.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q32. Что такое ParallelFlux и когда его использовать?

**`ParallelFlux<T>`** — специальный тип `Flux`, который разбивает поток на несколько «рельс» (rails) для параллельной обработки. Создаётся методом `flux.parallel()`.

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

Отличие от `flatMap()` с параллельным `Scheduler`:
- `ParallelFlux` — явное партиционирование данных по рельсам, предсказуемый параллелизм
- `flatMap` + `Scheduler` — более гибкий, но менее предсказуемый параллелизм


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q33. (!) Что такое Context в Project Reactor?

**`Context`** — неизменяемая (`immutable`) карта пар ключ-значение, которая распространяется **снизу вверх** по реактивной цепочке (от подписчика к источнику). Аналог `ThreadLocal` для реактивного мира.

Особенности `Context`:
- Передаётся **в обратном направлении** — от `subscriber` к `source` (upstream)
- Неизменяем — `put()` создаёт новый `Context`
- Доступен через `Mono.deferContextual()` или оператор `transformDeferredContextual()`

```
Source ←--- Context --- Operator ←--- Context --- Subscriber
       (Context читается здесь)              (Context пишется здесь)
```

Типичное применение: передача `userId`, `traceId`, `locale` без явной передачи параметров.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q34. Как записывать и читать данные из Context?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q35. (!) В чём разница между Cold и Hot publisher?

**Cold publisher** — **ленивый**: не начинает генерировать данные до подписки. Каждый подписчик получает **независимую** копию данных с самого начала.

**Hot publisher** — **активный**: генерирует данные **независимо от подписчиков**. Подписчики получают только те элементы, которые были эмитированы **после** их подписки.

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

По умолчанию все `Flux`/`Mono` в `Project Reactor` — **Cold**.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q36. Как преобразовать Cold publisher в Hot?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q37. Что такое ConnectableFlux?

**`ConnectableFlux<T>`** — специальный тип `Flux`, который не начинает эмитировать элементы до явного вызова `connect()`. Позволяет «мультикастить» одну подписку на источник нескольким подписчикам.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q38. Какие операторы для работы со временем предоставляет Project Reactor?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q39. Что делает оператор timeout()?

**`timeout(Duration)`** — если следующий элемент не поступил в течение указанного времени, бросает `TimeoutException` и отменяет подписку.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q40. (!) Что такое StepVerifier и как его использовать?

**`StepVerifier`** — утилита из `reactor-test` для тестирования реактивных потоков. Позволяет задавать ожидаемую последовательность сигналов (`onNext`, `onError`, `onComplete`) и верифицировать их.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q41. Как тестировать потоки, зависящие от времени?

`StepVerifier.withVirtualTime()` — позволяет тестировать временные операторы без реального ожидания, используя виртуальное время.

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

Важно: `Supplier` передаётся в `withVirtualTime()` — это обязательно для правильной подстановки виртуального планировщика.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q42. Что такое TestPublisher?

**`TestPublisher<T>`** — инструмент из `reactor-test`, позволяющий вручную управлять эмиссией элементов в тестах. Полезен когда нужно симулировать произвольные последовательности событий.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q43. (!) Как отлаживать реактивные цепочки в Project Reactor?

Реактивные стек-трейсы при ошибках зачастую нечитаемы — они показывают внутренности `Reactor`, а не код приложения. Основные инструменты отладки:

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q44. Что такое checkpoint() и как он помогает при отладке?

**`checkpoint(description)`** — добавляет «точку сборки» в реактивную цепочку. При ошибке в стек-трейс включается информация о месте вызова `checkpoint`, что помогает идентифицировать проблемный участок цепочки.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q45. Что такое Hooks и как использовать Hooks.onOperatorDebug()?

**`Hooks`** — класс для глобальной настройки поведения операторов `Reactor`. Позволяет перехватывать создание операторов и добавлять диагностику.

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

`ReactorDebugAgent` — рекомендуемая альтернатива `Hooks.onOperatorDebug()` для продакшена: инструментирует байткод во время загрузки класса, минимальный overhead.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q46. (!) Как Project Reactor интегрируется со Spring WebFlux?

`Spring WebFlux` использует `Project Reactor` как основную реактивную библиотеку. Интеграция пронизывает весь стек:

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
---

## Q47. Почему нельзя вызывать block() в WebFlux-приложении?

**`block()`** — синхронно блокирует вызывающий поток до завершения реактивного потока. В `Spring WebFlux` это приводит к:

1. **Дедлоку** — `WebFlux` использует небольшой пул потоков `Netty` (`parallel` Scheduler). Если заблокировать один поток, а другой поток в этом же пуле ожидает результата от него — **дедлок**.
2. **Деградации производительности** — блокировка потока уничтожает преимущества реактивной модели.

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

`Spring WebFlux` выбрасывает `IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread xxx` — это встроенная защита от случайного использования `block()` в реактивном контексте.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
- [Reactive Patterns](reactive-patterns-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](webflux-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
