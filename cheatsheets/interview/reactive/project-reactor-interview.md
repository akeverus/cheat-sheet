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
> - [ ] Реактивный подход — это многопоточность через `ExecutorService` и `CompletableFuture` | Многопоточность не равна реактивности: `CompletableFuture` блокирует поток в `.get()` и не поддерживает backpressure. ❌ ПОСЛЕДСТВИЕ: команда мигрирует на `CompletableFuture` и удивляется, что Tomcat-пул всё ещё забит при 5K rps.
> - [x] Парадигма работы с асинхронными потоками данных через push-модель и backpressure | Поток данных течёт от `Publisher` к `Subscriber` сигналами `onNext`/`onComplete`/`onError`, потребитель управляет скоростью через `request(n)`. ✓ ПРИМЕНЯТЬ: Netflix использует `RxJava`/`Reactor` для streaming-API; Spring WebFlux — основа event-driven backend в banking/telecom. 📋 ПРАВИЛО: «Push с обратной связью, а не pull в цикле». 🔗 См. Q2, Q4, Q27.
> - [ ] Это синоним функционального программирования с `Stream.map().filter()` | `Stream API` — pull-based и синхронный, не имеет сигналов завершения и backpressure. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает неблокирующий I/O от `stream().map(http::call)`, под нагрузкой получает thread starvation.
> - [ ] Подход, при котором каждый запрос обрабатывается в отдельном потоке через `@Async` | `@Async` создаёт thread-per-request, что не масштабируется до 10K connections и не передаёт backpressure. ❌ ПОСЛЕДСТВИЕ: в e-commerce под Black Friday `@Async` кидает `RejectedExecutionException`, заказы теряются.
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
> - [ ] `Publisher`, `Subscriber`, `Observer`, `Subject` — четыре интерфейса спецификации | `Observer`/`Subject` — термины `RxJava`, в спецификации их нет. ❌ ПОСЛЕДСТВИЕ: на собеседовании путают RxJava 2 API с Reactive Streams, проваливают вопрос про `java.util.concurrent.Flow`.
> - [ ] `Publisher.subscribe()` сразу шлёт все элементы через `onNext` без участия подписчика | Без `request(n)` `Publisher` обязан ждать запрос: иначе нарушение Rule 1 спецификации. ❌ ПОСЛЕДСТВИЕ: самописный `Publisher` шлёт `onNext` до `request()`, TCK-тесты падают, интеграция с `Reactor` ломается.
> - [ ] Спецификация определяет 4 интерфейса (`Publisher`, `Subscriber`, `Subscription`, `Processor`) и протокол с `request(n)` для backpressure | `JDK 9` включил эту спецификацию как `java.util.concurrent.Flow`; `Reactor`, `RxJava`, `Akka Streams` совместимы между собой. ✓ ПРИМЕНЯТЬ: `MongoDB Reactive Driver` и `R2DBC` реализуют `Publisher`, что позволяет использовать их с любой Reactive Streams-библиотекой. 📋 ПРАВИЛО: «4 интерфейса + request(n) = совместимость библиотек». 🔗 См. Q1, Q4, Q27.
> - [ ] Реализация спецификации возможна только через `Project Reactor`, других нет | Спецификация многоплатформенная: `RxJava 3`, `Akka Streams`, `Mutiny` (Quarkus) — независимые реализации. ❌ ПОСЛЕДСТВИЕ: команда отказывается от Quarkus с `Mutiny` из-за ложного убеждения, что только `Reactor` совместим со Spring.
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
> - [ ] `Reactor` и `RxJava 3` идентичны: `Mono ≡ Single`, `Flux ≡ Observable` | `RxJava` различает `Single`/`Maybe`/`Observable`/`Flowable` (4 типа), `Reactor` — только `Mono`/`Flux`; `Observable` не имеет backpressure, `Flux` имеет всегда. ❌ ПОСЛЕДСТВИЕ: миграция RxJava→Reactor «один-в-один» теряет различие `Maybe` (0/1 без ошибки), nullable-семантику сломали в production.
> - [ ] `Project Reactor` имеет встроенный backpressure во всех типах и не допускает `null` в потоке; `RxJava` поддерживает backpressure только в `Flowable` | `Reactor` спроектирован для Spring-экосистемы, имеет `Context` для propagation; `null` в `Mono.just(null)` бросает `NullPointerException` сразу. ✓ ПРИМЕНЯТЬ: Spring WebFlux/`R2DBC`/`WebClient` строятся на `Reactor`, а не RxJava — выбор сделан осознанно для backpressure-by-default. 📋 ПРАВИЛО: «Reactor для Spring, RxJava для Android». 🔗 См. Q1, Q5, Q33.
> - [ ] `Project Reactor` — это просто Spring-обёртка над `RxJava 3` | `Reactor` — независимая библиотека от Pivotal/VMware с нуля, использует `reactive-streams` API, но без зависимости от RxJava. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `io.reactivex:rxjava` в WebFlux-проект «для совместимости» — конфликт классов, baggage в classpath.
> - [ ] `RxJava` поддерживает реактивность, а `Reactor` — только функциональные стримы | Оба полностью реализуют Reactive Streams TCK; `Reactor.Flux` совместим с `RxJava.Flowable` через `Flux.from(publisher)`. ❌ ПОСЛЕДСТВИЕ: команда выбирает RxJava для «настоящей реактивности» в Spring WebFlux-проекте, теряет нативную интеграцию и `Context`.
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
> - [ ] Backpressure — это retry-механизм после `OutOfMemoryError` | Backpressure — превентивный контроль, а не post-mortem reaction; OOM наступает уже после нарушения. ❌ ПОСЛЕДСТВИЕ: команда «решает» OOM перезапуском пода каждый час, latency p99 деградирует до 10s.
> - [ ] Это автоматическое сжатие данных в потоке для уменьшения нагрузки | Сжатие — отдельная техника (gzip, Snappy); backpressure управляет скоростью, а не размером. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет gzip к Kafka-сообщениям и не понимает, почему consumer всё ещё в lag — сжатие не решает проблему скорости.
> - [ ] Механизм запроса данных подписчиком через `Subscription.request(n)`: producer не шлёт больше, чем consumer запросил | Это Flow-control между Publisher и Subscriber; запрашивается ровно `n` элементов, что предотвращает буфер-overflow и OOM. ✓ ПРИМЕНЯТЬ: Kafka Streams reactor-bridge и `R2DBC` используют backpressure для plug-and-play protection от медленных consumer'ов. 📋 ПРАВИЛО: «Pull в push-обёртке через request(n)». 🔗 См. Q27, Q28, Q29.
> - [ ] Это паттерн Circuit Breaker для отключения upstream при ошибках | Circuit Breaker (`Resilience4j`) реагирует на failure-rate; backpressure — на скорость потребления. ❌ ПОСЛЕДСТВИЕ: вместо `request(n)` команда обвешивает chain Circuit Breaker'ами, OOM не уходит — буферы продолжают расти между вызовами.
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
> - [ ] `Mono` — синхронный, `Flux` — асинхронный | Оба асинхронные; разница только в количестве элементов: `Mono` ≤ 1, `Flux` 0..N. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `block()` на `Mono` «потому что синхронный» в WebFlux-controller, блокирует event-loop поток.
> - [ ] `Mono<T>` — 0 или 1 элемент (как `Optional`/HTTP-ответ), `Flux<T>` — 0..N элементов (поток событий) | `Mono` подходит для `findById`, save, REST-response; `Flux` — для `findAll`, SSE, файлов построчно. ✓ ПРИМЕНЯТЬ: Spring Data R2DBC возвращает `Mono` для `findById` и `Flux` для `findAll`; WebFlux SSE-endpoint всегда `Flux<ServerSentEvent>`. 📋 ПРАВИЛО: «Mono = 0..1, Flux = 0..N». 🔗 См. Q1, Q6, Q7.
> - [ ] `Mono` всегда содержит ровно 1 элемент, иначе бросает исключение | `Mono.empty()` валиден; `Mono` может завершиться `onComplete` без `onNext`. ❌ ПОСЛЕДСТВИЕ: вместо `switchIfEmpty` пишут `.map(x -> x != null ? x : default)`, маппинг не вызывается на empty Mono — баг в логике.
> - [ ] `Flux` обязательно бесконечен, `Mono` всегда конечен | `Flux` может быть конечным (`Flux.just(1,2,3)`); бесконечность — частный случай (`Flux.interval`). ❌ ПОСЛЕДСТВИЕ: тест с `StepVerifier.expectComplete()` на конечном Flux считают «неправильным», переписывают на `expectNoEvent` и ловят false positive.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Оба идентичны: `just(supplier.get())` ≡ `defer(supplier)` | `just` вычисляет значение в момент вызова метода; `defer` — на каждом `subscribe`, что критично для `LocalDateTime.now()`/счётчиков. ❌ ПОСЛЕДСТВИЕ: `Mono.just(LocalDateTime.now())` отдаёт всем подписчикам один и тот же timestamp, кэш-busting не работает.
> - [ ] `Mono.just(value)` — eager evaluation в момент создания; `Mono.defer(() -> Mono.just(...))` — lazy, supplier вызывается на каждой подписке | `defer` нужен когда значение зависит от состояния (now, counter, DB-call), `just` — для готовых констант. ✓ ПРИМЕНЯТЬ: `defer` обязателен в `retryWhen` для повторного вызова repository; кеш-валидация через `defer(() -> redis.get(key))`. 📋 ПРАВИЛО: «just = const, defer = supplier». 🔗 См. Q6, Q7, Q25.
> - [ ] `defer` блокирует поток до получения значения, а `just` — нет | Оба не блокируют; разница только в моменте вычисления supplier. ❌ ПОСЛЕДСТВИЕ: команда боится `defer` из-за «блокировки», заворачивает всё в `just(blockingCall())` — каждый subscribe выполняется один раз, кэшируется stale value.
> - [ ] `defer` создаёт горячий поток, `just` — холодный | Оба cold; hot-поведение даёт `share()`/`publish()`/`Sinks`. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает broadcast от `defer`, два подписчика не видят одинаковые события — путаница в SSE-сценарии.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] `map` для async, `flatMap` для sync | Наоборот: `map` синхронный (T→R), `flatMap` для async (T→Publisher<R>) и flattens результат. ❌ ПОСЛЕДСТВИЕ: `flux.map(id -> webClient.get(id).bodyToMono(User.class))` создаёт `Flux<Mono<User>>`, никто не подписывается на внутренние Mono — запросы не выполняются.
> - [ ] `map(T → R)` — синхронная трансформация 1-к-1; `flatMap(T → Publisher<R>)` — асинхронная, разворачивает inner publisher (1-к-N) | `flatMap` подписывается на inner и flattens; нужен для I/O-операций (HTTP, DB). ✓ ПРИМЕНЯТЬ: WebFlux-controller `userIds.flatMap(userService::findById)` для параллельных DB-запросов; Booking.com использует `flatMap` для composite price-aggregation. 📋 ПРАВИЛО: «map для CPU, flatMap для I/O». 🔗 См. Q12, Q13, Q15.
> - [ ] `flatMap` сохраняет порядок, `map` — нет | Наоборот: `map` сохраняет порядок (sync), `flatMap` — НЕ сохраняет (interleaved); порядок гарантирует `concatMap` или `flatMapSequential`. ❌ ПОСЛЕДСТВИЕ: банковский transfer-pipeline на `flatMap` обрабатывает дебет после кредита из-за разной latency, баланс уходит в минус.
> - [ ] `map` доступен только для `Mono`, `flatMap` — для `Flux` | Оба доступны и в `Mono`, и в `Flux`. ❌ ПОСЛЕДСТВИЕ: разработчик дублирует `Mono.map().flux().flatMap()` вместо одного `flatMap`, читаемость падает, цепочка раздувается.
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
> - [ ] Все три идентичны, отличие только в названии | Поведение различается принципиально: `flatMap` — параллельно/interleaved, `concatMap` — последовательно с сохранением порядка, `switchMap` — отменяет предыдущий при новом элементе. ❌ ПОСЛЕДСТВИЕ: команда меняет `concatMap` на `flatMap` для «оптимизации», ломает порядок транзакций в банковском pipeline.
> - [ ] `flatMap` — параллельно (порядок не гарантирован), `concatMap` — последовательно (FIFO), `switchMap` — каждый новый upstream-элемент отменяет inner-обработку предыдущего | `flatMap` ≈ ConcurrencyN; `switchMap` идеален для search-as-you-type, где старые запросы уже не нужны. ✓ ПРИМЕНЯТЬ: Google Search/Algolia на frontend используют `switchMap` для cancel предыдущего запроса при новом keystroke; банковские системы — `concatMap` для строгого ордеринга. 📋 ПРАВИЛО: «flat — parallel, concat — order, switch — cancel». 🔗 См. Q11, Q13, Q15.
> - [ ] `flatMap` сохраняет порядок строго, `concatMap` параллелит, `switchMap` буферизует | Перепутаны: `concatMap` — sequential, `flatMap` — parallel; `switchMap` отменяет, не буферизует. ❌ ПОСЛЕДСТВИЕ: при выборе оператора по неверной модели результат `flatMap` приходит в произвольном порядке, тесты с `expectNext("A","B")` нестабильны.
> - [ ] `switchMap` блокирует поток до завершения inner; `concatMap` — non-blocking | Все три non-blocking; `switchMap` cancel'ит inner subscription, не блокирует. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `subscribeOn(boundedElastic)` под `switchMap` «чтобы разблокировать», впустую тратит worker'ы из-за ложной модели.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] `doOnNext` мутирует элемент и возвращает его дальше | `doOnNext` — это side-effect callback (`Consumer<T>`), он НЕ меняет downstream-значение; для трансформации нужен `map`. ❌ ПОСЛЕДСТВИЕ: разработчик мутирует поле в `doOnNext(user -> user.setEmail(...))` и удивляется race-condition с другим subscriber на shared объекте.
> - [ ] `doOnNext` — side-effect callback (логи, метрики, audit), `map(T → R)` — трансформация значения с возвратом нового | `doOnNext` нельзя использовать для изменения потока; результат `doOnNext` игнорируется reactor'ом. ✓ ПРИМЕНЯТЬ: `doOnNext(req -> log.info("got {}", req))` в WebFlux-фильтре; Micrometer-метрики через `doOnNext(x -> meter.increment())`. 📋 ПРАВИЛО: «do = логи/метрики, map = трансформация». 🔗 См. Q11, Q14, Q43.
> - [ ] `doOnNext` синхронен, `doOnComplete` асинхронен | Оба синхронны (вызываются в текущем executor), асинхронность даёт `publishOn`/`subscribeOn`. ❌ ПОСЛЕДСТВИЕ: команда оборачивает HTTP-вызов в `doOnNext` ожидая «асинхронности», блокирует event-loop поток в WebFlux.
> - [ ] `doOnError` отлавливает и подавляет ошибку, как `onErrorResume` | `doOnError` — observation-only, ошибка идёт дальше; для подавления нужны `onErrorReturn`/`onErrorResume`. ❌ ПОСЛЕДСТВИЕ: разработчик ловит ошибку в `doOnError`, но клиент всё равно получает 500, никто не понимает «почему я же handle сделал».
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] `merge`, `concat`, `zip` — синонимы, разница только в API | Поведение фундаментально различается: `merge` interleaves параллельно, `concat` склеивает последовательно, `zip` ждёт по одному элементу с каждого источника. ❌ ПОСЛЕДСТВИЕ: команда заменяет `zip` на `merge` для «параллельности», теряет связку user+order и собирает мусорные пары.
> - [ ] `merge(a,b)` — параллельная подписка, элементы interleaved; `concat(a,b)` — `b` подписывается после complete от `a`; `zip(a,b)` — попарная комбинация (ждёт по 1 элементу от каждого) | `merge` — full parallel, `concat` — strict order, `zip` — pairwise sync. ✓ ПРИМЕНЯТЬ: agreggator-сервисы с parallel external API через `merge`; `zip(userMono, settingsMono)` — типичный pattern для composition в WebFlux. 📋 ПРАВИЛО: «merge=parallel, concat=order, zip=pair». 🔗 См. Q11, Q20, Q21.
> - [ ] `concat` подписывается на оба источника параллельно | Нет, `concat` — строго последовательный: `b` стартует только после `onComplete` от `a`. ❌ ПОСЛЕДСТВИЕ: команда ставит `concat` ожидая параллельности при aggregate-API, latency растёт в N раз вместо параллельных запросов.
> - [ ] `zip` дожидается завершения обоих стримов и эмитит финальный список | `zip` эмитит попарно `Tuple2` на каждой паре; финальный список даёт `Mono.zip(a,b).map(...)` или `collectList`. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт List от `zip` двух Flux, цепочка не реагирует, тест падает по таймауту.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Можно использовать только `try/catch` внутри `map` | В реактивной цепочке `try/catch` ловит исключение, но нужно вернуть `Publisher` или поглотить — лучше использовать declarative-операторы. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `try/catch` в `map`, throw'ит RuntimeException, получает `onError` без fallback'а — клиент видит 500.
> - [ ] Достаточно одного `onErrorMap` для всех сценариев | `onErrorMap` только трансформирует тип ошибки; для fallback нужны `onErrorReturn`/`onErrorResume`, для retry — `retry`/`retryWhen`. ❌ ПОСЛЕДСТВИЕ: команда логирует ошибку через `onErrorMap` и считает «обработали», стрим прерывается, downstream не получает данных.
> - [ ] `onErrorReturn(default)`, `onErrorResume(fallbackPublisher)`, `onErrorMap(transformer)`, `onErrorContinue`, `retry`/`retryWhen` — каждый под свой кейс | `Return` — статический default, `Resume` — fallback Publisher, `Map` — wrap exception, `Continue` — skip элемент (опасный), `retry` — повтор upstream. ✓ ПРИМЕНЯТЬ: WebFlux + Resilience4j: `onErrorResume` для fallback к cached response; `retryWhen(Retry.backoff(3, ofMillis(100)))` для transient HTTP-ошибок. 📋 ПРАВИЛО: «Return-default, Resume-fallback, Map-wrap, retry-replay». 🔗 См. Q23, Q24, Q25.
> - [ ] Достаточно `subscribe(onNext, onError)` без операторов | Это терминальный handler, не позволяет продолжить chain — пригоден только в самом конце. ❌ ПОСЛЕДСТВИЕ: ошибка ловится в `subscribe`, но downstream-потребители уже подписаны — `onComplete` не приходит, ресурсы не освобождаются.
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
> - [ ] `onErrorReturn(value)` всегда параметризуется Publisher'ом | `onErrorReturn` принимает значение `T`, не Publisher; для Publisher используется `onErrorResume`. ❌ ПОСЛЕДСТВИЕ: код `onErrorReturn(Mono.just(default))` возвращает `Mono<Mono<T>>`, downstream получает не `T`, а сам Publisher как value.
> - [ ] Оба возвращают статическое значение | `onErrorResume` принимает функцию `Throwable → Publisher<T>`, что позволяет динамически выбирать fallback (cache, retry-API) на основе типа ошибки. ❌ ПОСЛЕДСТВИЕ: вместо `onErrorResume(NotFoundException.class, e -> cache.get())` команда пишет `onErrorReturn(emptyList)`, теряет cached данные.
> - [ ] `onErrorReturn(value)` подменяет ошибку на статическое `T`; `onErrorResume(fn)` подписывается на новый Publisher из лямбды — позволяет динамический fallback | `Resume` мощнее: можно условно выбрать fallback или re-throw через `Mono.error(...)`. ✓ ПРИМЕНЯТЬ: Netflix Hystrix-style fallback через `onErrorResume(e -> cache.get(key))`; `onErrorReturn` для дефолтного списка settings при отсутствии записи. 📋 ПРАВИЛО: «Return — value, Resume — Publisher». 🔗 См. Q22, Q24, Q25.
> - [ ] `onErrorResume` ловит только `RuntimeException`, `onErrorReturn` — все `Throwable` | Оба ловят `Throwable` (с overload по типу); поведение симметрично, отличие только в форме fallback. ❌ ПОСЛЕДСТВИЕ: разработчик ставит double-handler «для надёжности», ошибка ловится дважды, метрики error-rate удваиваются.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] `retry(N)` повторяет N раз с фиксированной задержкой 1 секунда | `retry(N)` повторяет немедленно без backoff; задержку даёт `retryWhen(Retry.fixedDelay(N, dur))`. ❌ ПОСЛЕДСТВИЕ: при сбое downstream-API `retry(5)` шлёт 5 запросов за миллисекунды, добивает API под нагрузкой — retry storm.
> - [ ] `retry(N)` — простой повтор upstream до N раз без задержки; `retryWhen(Retry.backoff(N, base))` — exponential backoff с jitter и фильтрацией по типу исключения | `retryWhen` принимает `Retry`-spec из Reactor: backoff, max, jitter, filter; обязателен для production. ✓ ПРИМЕНЯТЬ: AWS SDK/DynamoDB используют exponential backoff для transient errors; Spring Cloud `retryWhen(Retry.backoff(3, ofMillis(200)).jitter(0.5))` стандарт для WebClient. 📋 ПРАВИЛО: «retry(N) — для тестов, retryWhen(backoff) — для прода». 🔗 См. Q22, Q23, Q24.
> - [ ] `retryWhen` повторяет цепочку и trigger'ит только при success | `retryWhen` срабатывает на error-сигнал; success не вызывает повтор. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает повторного обращения после `onComplete` для polling — никогда не сработает, polling реализуется через `Flux.interval`.
> - [ ] `retry()` без аргументов повторяет ровно 3 раза | `retry()` без аргументов — `Long.MAX_VALUE` повторов (бесконечно); таймаут не остановит. ❌ ПОСЛЕДСТВИЕ: при downstream-сбое `retry()` зацикливает retry навсегда, под не падает по `livenessProbe`, hot loop съедает CPU.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Через автоматическое сэмплирование событий | Sampling/throttle есть как операторы (`sample`, `throttleFirst`), но это не основной механизм backpressure. ❌ ПОСЛЕДСТВИЕ: разработчик использует `sample(1s)` вместо `request(n)` для real-time котировок, теряет половину событий — баг в trading-системе.
> - [ ] Subscriber через `Subscription.request(n)` указывает producer'у, сколько может принять; стратегии overflow (`onBackpressureBuffer`/`Drop`/`Latest`/`Error`) | `Reactor` буферизует между операторами, прозрачно проксирует request от downstream к upstream; стратегии нужны для unbounded источников. ✓ ПРИМЕНЯТЬ: `R2DBC` пропагирует request от WebFlux к БД, БД отдаёт ровно нужное количество rows; Kafka reactor-bridge использует backpressure для committing offsets. 📋 ПРАВИЛО: «request(n) пропагирует от Subscriber к Publisher». 🔗 См. Q4, Q28, Q29.
> - [ ] `Reactor` использует только pull-модель: subscriber тянет элементы из publisher | Это push-модель с feedback (`request`), не чистый pull; pull был бы блокирующим. ❌ ПОСЛЕДСТВИЕ: ложная модель приводит к ожиданию `next()`-метода как в Iterator, разработчик ищет несуществующий API.
> - [ ] Backpressure активируется только если subscriber явно реализует `BaseSubscriber` | Стандартные методы (`subscribe(consumer)`) запрашивают `Long.MAX_VALUE`, но операторы (`flatMap`, `buffer`, `limitRate`) применяют backpressure прозрачно. ❌ ПОСЛЕДСТВИЕ: команда переписывает все subscribe на `BaseSubscriber` «ради backpressure», получает boilerplate без необходимости.
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
> - [ ] Существует только одна стратегия: блокировать producer до request | Блокировка producer'а — антипаттерн в async; стратегии работают через буфер/drop/error. ❌ ПОСЛЕДСТВИЕ: производитель блокируется на `synchronized`-стеке, hot path встаёт колом, latency p99 уходит в 30s.
> - [ ] `Buffer` (default), `Drop` (отбрасывать новые), `Latest` (хранить только последний), `Error` (бросать `OverflowException`) | Стратегии задаются через `onBackpressureBuffer/Drop/Latest/Error`; выбор зависит от семантики потерь. ✓ ПРИМЕНЯТЬ: real-time котировки на бирже используют `onBackpressureLatest` — старая цена не нужна; audit-лог использует `Buffer` или `Error`, потери недопустимы. 📋 ПРАВИЛО: «Buffer/Drop/Latest/Error — выбор по семантике потерь». 🔗 См. Q4, Q27, Q29.
> - [ ] Все стратегии равнозначны, выбирать произвольно | Семантика разная: `Drop` теряет события, `Buffer` без лимита → OOM, `Error` ломает stream. ❌ ПОСЛЕДСТВИЕ: разработчик ставит default `Buffer` для unbounded Kafka-consumer, OOM через 2 часа высокой нагрузки.
> - [ ] `Buffer` имеет встроенный лимит 256 элементов | Default `onBackpressureBuffer()` без аргументов — unbounded; лимит надо указать явно через `onBackpressureBuffer(maxSize)`. ❌ ПОСЛЕДСТВИЕ: команда верит, что buffer ограничен «дефолтом», под нагрузкой в production heap растёт линейно — OOM через 4 часа.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Все Schedulers одинаковые, разница только в имени | Семантика принципиально различна: `parallel` фиксированный (CPU-bound), `boundedElastic` для I/O с лимитом, `single` — один поток, `immediate` — без переключения. ❌ ПОСЛЕДСТВИЕ: команда ставит `parallel()` для blocking JDBC, parallel-thread'ы блокированы, latency p99 → 30s.
> - [ ] `immediate` (без переключения), `single` (один thread), `parallel` (CPU-bound, размер = кол-во CPU), `boundedElastic` (I/O, до 10×CPU thread'ов с очередью) | Каждый под свой класс задач: parallel — для CPU, boundedElastic — для blocking I/O. ✓ ПРИМЕНЯТЬ: WebFlux event-loop для non-blocking; `subscribeOn(Schedulers.boundedElastic())` для блокирующего JDBC через `R2DBC`-bridge. 📋 ПРАВИЛО: «parallel — CPU, boundedElastic — I/O, single — order, immediate — caller». 🔗 См. Q31, Q32, Q47.
> - [ ] `boundedElastic` создаёт unbounded thread'ов под нагрузкой | `boundedElastic` ограничен (default 10×CPU + queue 100K); replacement для устаревшего unbounded `elastic()`. ❌ ПОСЛЕДСТВИЕ: команда верит «elastic — без лимита», запускает 100K параллельных задач, очередь переполняется, бросает `RejectedExecutionException`.
> - [ ] `parallel` подходит для blocking I/O | `parallel` — для non-blocking CPU-задач; для blocking использовать `boundedElastic`. ❌ ПОСЛЕДСТВИЕ: блокирующий `JdbcTemplate` на `parallel(8)` блокирует все 8 потоков, весь WebFlux встаёт под нагрузкой.
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
> - [ ] `subscribeOn` влияет на downstream-операторы, `publishOn` — на upstream | Наоборот: `subscribeOn` влияет на ВСЮ цепочку (момент подписки идёт upstream, источник работает в указанном scheduler), `publishOn` переключает только downstream после себя. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `subscribeOn` после `flatMap` ожидая локальный эффект, источник всё равно работает в parallel — neблокирующий код блокируется.
> - [ ] `subscribeOn` задаёт scheduler для всего источника подписки (work upstream); `publishOn` переключает scheduler для downstream-операторов после себя | Множественные `subscribeOn` — побеждает первый (ближайший к источнику); `publishOn` можно ставить несколько раз, каждый меняет следующие операторы. ✓ ПРИМЕНЯТЬ: типичный pattern WebFlux — `Mono.fromCallable(blockingCall).subscribeOn(boundedElastic).publishOn(parallel)` для blocking-bridge. 📋 ПРАВИЛО: «subscribeOn — где source работает, publishOn — где downstream». 🔗 См. Q30, Q32, Q47.
> - [ ] `subscribeOn` и `publishOn` идентичны, можно использовать любой | Поведение разное: `subscribeOn` идёт «вверх» к источнику, `publishOn` действует «вниз». ❌ ПОСЛЕДСТВИЕ: команда меняет `subscribeOn` на `publishOn` для blocking JDBC source, JDBC выполняется на event-loop вместо boundedElastic — pinning, OOM пулов.
> - [ ] Множественные `publishOn` суммируются: каждый прибавляет thread | Каждый `publishOn` ПЕРЕопределяет scheduler для downstream, не суммируется; thread меняется в точке оператора. ❌ ПОСЛЕДСТВИЕ: разработчик ставит 5 `publishOn` подряд «для multi-threading», получает 5 переключений context'а — overhead и worse latency.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Это `ThreadLocal`-замена внутри Reactor | `ThreadLocal` не работает в реактивных цепочках с переключением scheduler'ов; `Context` — иммутабельная карта, привязанная к Subscription, propagates через chain снизу вверх. ❌ ПОСЛЕДСТВИЕ: команда использует `ThreadLocal` для traceId в WebFlux, после `publishOn` traceId теряется — логи без correlation ID, debug невозможен.
> - [ ] Иммутабельная key-value карта, привязанная к Subscription, доступная всем операторам в chain через `deferContextual` или `contextWrite` | Распространяется снизу вверх (от subscribe к источнику); используется для `traceId`, `securityContext`, `tenantId` в multi-tenant. ✓ ПРИМЕНЯТЬ: Spring Security WebFlux хранит `SecurityContext` в Reactor Context; Sleuth/Micrometer Tracing пробрасывают `traceId` через `Context` для distributed tracing. 📋 ПРАВИЛО: «Reactor Context = ThreadLocal реактивного мира». 🔗 См. Q31, Q34, Q46.
> - [ ] Mutable хеш-таблица, изменяемая через `context.put()` | Context immutable; каждое `contextWrite` создаёт новую копию, существующая остаётся неизменной. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает `contextWrite` мутацию, теряет данные после `flatMap` — concurrent ChangeContext race.
> - [ ] Глобальная переменная процесса JVM | Context привязан к каждой Subscription отдельно; нет «global Context». ❌ ПОСЛЕДСТВИЕ: разработчик пишет данные одного запроса в «global Context», другой запрос видит чужой `tenantId` — критический security-leak в multi-tenant.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Hot publisher быстрее cold | Скорость не определяется hot/cold; разница в semantics: cold — каждый subscriber запускает свой источник заново, hot — общий источник для всех. ❌ ПОСЛЕДСТВИЕ: команда переводит API на hot «для скорости», теряет идемпотентность — два subscriber видят разные суб-наборы событий.
> - [ ] Cold publisher запускает источник заново для каждого subscriber (`Flux.fromIterable`, HTTP-вызов); Hot вещает общий поток событий всем подписчикам (`Sinks.Many`, `share()`) | Cold идемпотентен — повторная подписка получает full sequence; Hot пропускает события, произошедшие до подписки. ✓ ПРИМЕНЯТЬ: WebFlux REST endpoint = cold (каждый запрос — свой Flux); Server-Sent Events broadcast = hot через `Sinks.many().multicast()`; Twitter timeline = hot. 📋 ПРАВИЛО: «Cold — replay, Hot — broadcast». 🔗 См. Q36, Q37, Q40.
> - [ ] Cold = синхронный, Hot = асинхронный | Оба могут быть и тем, и другим; различие — в момент эмиссии относительно подписки. ❌ ПОСЛЕДСТВИЕ: команда заворачивает cold Flux в Mono.fromCallable «чтобы сделать асинхронным», получает double-subscribe error в `flatMap`.
> - [ ] Cold нельзя превратить в Hot | Можно через `share()`, `publish().refCount()`, `Sinks.many()`, `replay()`. ❌ ПОСЛЕДСТВИЕ: команда дублирует API-запрос для двух consumer'ов вместо `cache()`/`share()`, нагрузка на upstream удваивается, бьёт rate limit.
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
> - [ ] Достаточно вызвать `.subscribe()` дважды | Двойной subscribe на cold Flux запустит источник дважды (два HTTP-запроса), не сделает hot. ❌ ПОСЛЕДСТВИЕ: разработчик «делает hot» через двойной subscribe, выполняет дорогой DB-запрос дважды, удваивает latency.
> - [ ] Через `share()` (publish + refCount), `publish()` + `connect()`, `cache()` (replay) или `Sinks.many()` | `share()` — hot пока ≥1 subscriber; `publish().connect()` — manual control; `cache()` — hot с full replay; `Sinks` — programmatic emission. ✓ ПРИМЕНЯТЬ: SSE feed на `Sinks.many().multicast().onBackpressureBuffer()`; WebFlux endpoint с дорогим upstream через `cache(Duration.ofMinutes(5))`. 📋 ПРАВИЛО: «share — лайв, cache — replay, Sinks — programmatic». 🔗 См. Q35, Q37, Q42.
> - [ ] Только через `Schedulers.parallel()` | Scheduler меняет thread, не меняет hot/cold-семантику. ❌ ПОСЛЕДСТВИЕ: команда ставит `subscribeOn(parallel)` для «hot-режима», семантика остаётся cold, broadcasts не работают.
> - [ ] Hot — это всегда `Mono`, cold — `Flux` | Hot/cold не определяется типом; есть hot Flux и cold Mono (cold по умолчанию). ❌ ПОСЛЕДСТВИЕ: разработчик возвращает `Flux` ожидая «всегда hot», `WebClient` возвращает cold Flux — два subscriber'а делают два HTTP-запроса.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] `block()` в `assertEquals` достаточно для тестирования | `block()` теряет всю реактивную семантику (порядок, ошибки, signal'ы), не тестирует backpressure и timing. ❌ ПОСЛЕДСТВИЕ: команда покрывает WebFlux-код `assertEquals(flux.collectList().block(), expected)`, не ловит регрессии в `onError`/timing — баги уходят в production.
> - [ ] Инструмент тестирования из `reactor-test`: `StepVerifier.create(publisher).expectNext(...).expectComplete().verify()` — пошагово описывает ожидаемые сигналы (`onNext`, `onComplete`, `onError`) и timing | Поддерживает `expectError`, `thenAwait`, `verifyComplete`, `verifyTimeout`; работает с virtual time через `withVirtualTime`. ✓ ПРИМЕНЯТЬ: стандарт для WebFlux unit-тестов; Spring Boot включает в `spring-boot-starter-webflux test`. 📋 ПРАВИЛО: «StepVerifier пошагово проверяет signals». 🔗 См. Q41, Q42, Q43.
> - [ ] Это альтернатива `Mockito` для мокирования publishers | Для моков есть `TestPublisher`; `StepVerifier` — assertion-фреймворк, а не mock-framework. ❌ ПОСЛЕДСТВИЕ: команда использует `StepVerifier` для моков, дублирует функциональность `TestPublisher`, тесты становятся хрупкими.
> - [ ] Заменяет `JUnit @Test` для реактивного кода | `StepVerifier` работает ВНУТРИ `@Test` метода, не заменяет JUnit. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `StepVerifier`-цепочку без `@Test` annotation, тест не запускается в CI, ложно-зелёный билд.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] Достаточно поставить breakpoint в IDE на `flatMap` | Breakpoint в реактивной цепочке не показывает upstream-stack из-за async-разрыва; видно только текущий operator-frame, не путь до источника ошибки. ❌ ПОСЛЕДСТВИЕ: разработчик 4 часа дебажит NullPointerException в WebFlux-controller, не может найти source — stack-trace обрывается на `OnNextRunnable.run`.
> - [ ] `Hooks.onOperatorDebug()` (global, expensive), `checkpoint("name")` (точечный), `log()` (per-step), `doOnEach`, `Reactor Tools agent` (production-safe) | Глобальный `Hooks.onOperatorDebug()` — для dev (тяжёлый); `checkpoint` для prod точечно; ReactorDebugAgent — bytecode-инструментирование. ✓ ПРИМЕНЯТЬ: dev-профиль Spring Boot включает `Hooks.onOperatorDebug()`; production использует `ReactorDebugAgent.init()` (light overhead) либо точечные `checkpoint("transferMoney-step")`. 📋 ПРАВИЛО: «Hooks для dev, checkpoint для prod, log для chain-trace». 🔗 См. Q44, Q45, Q40.
> - [ ] Использовать `e.printStackTrace()` в `onErrorResume` | Stack-trace в реактивке усечённый из-за async-разрыва без assembly-trace; `printStackTrace` без `Hooks.onOperatorDebug` бесполезен. ❌ ПОСЛЕДСТВИЕ: log содержит «UnsupportedOperationException» без места возникновения, root cause неуловим, fix откладывается на дни.
> - [ ] Включить SQL-debug через `logging.level.sql=DEBUG` | SQL-debug к Reactor не имеет отношения; работает для `R2DBC`/JPA, но не для chain-debugging. ❌ ПОСЛЕДСТВИЕ: команда ловит SQL-логами «не тот» баг, реактивная цепочка остаётся непрозрачной.
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
> - [ ] Это breakpoint в IDE для async-кода | `checkpoint` — оператор Reactor (не IDE-фича); добавляет assembly-information в stack trace при ошибке без перезапуска. ❌ ПОСЛЕДСТВИЕ: команда ставит breakpoint вместо checkpoint, теряет async-контекст; production-инцидент не воспроизвести в IDE.
> - [ ] Оператор, добавляющий «traceback»-маркер в chain: при ошибке в stack trace появляется `Assembly trace from producer [Flux.checkpoint("name")]` | Light-weight (только для error-path), production-safe; альтернатива `Hooks.onOperatorDebug` без global overhead. ✓ ПРИМЕНЯТЬ: точечно на критичных шагах transfer-pipeline в банке: `.checkpoint("debit-step")` идентифицирует шаг падения; Spring Cloud Sleuth рекомендует для production. 📋 ПРАВИЛО: «checkpoint = ассерт-маркер в stack trace». 🔗 См. Q43, Q45, Q40.
> - [ ] Аналог `assertEquals` для проверки значений в потоке | Для assertion'ов используется `StepVerifier`/`assertNext`; `checkpoint` маркирует, не проверяет значения. ❌ ПОСЛЕДСТВИЕ: команда пишет `.checkpoint(value -> value > 0)`, метод не существует — compile error либо ложная уверенность в проверке.
> - [ ] Останавливает поток до его ручного продолжения | `checkpoint` non-blocking, не приостанавливает; работает только при ошибке. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `checkpoint` в надежде «дождаться» события для дебага, поток продолжает идти, debug сессия теряется.
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
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
> - [ ] WebFlux работает поверх Servlet API, как Spring MVC | WebFlux работает на Netty/Reactor Netty (default), не на Servlet API; есть бэкенд под Tomcat 8.5+, но non-blocking native — Reactor Netty. ❌ ПОСЛЕДСТВИЕ: команда добавляет `HttpServletRequest` в WebFlux-controller, получает compile error и непонимание API — недели на изучение «почему не работает».
> - [ ] WebFlux построен на Reactor: controller возвращает `Mono`/`Flux`, request-цепочка работает на event-loop без thread-per-request, через `WebClient`/`R2DBC` для I/O | Reactor Netty event-loop (#CPU потоков) обрабатывает все запросы; blocking I/O нужно делать на `boundedElastic`. ✓ ПРИМЕНЯТЬ: high-load streaming (10K+ соединений на ноду) — WebFlux в Booking.com, Wolt; SSE-API через `Flux<ServerSentEvent>`. 📋 ПРАВИЛО: «WebFlux = Netty + Mono/Flux + non-blocking I/O». 🔗 См. Q31, Q33, Q47.
> - [ ] WebFlux — это REST-template на Spring MVC с реактивной обёрткой | Это полностью отдельный стек: `RouterFunction`, `WebClient`, `WebFilter` вместо MVC-аналогов; вместе они не работают в одном приложении (выбор один). ❌ ПОСЛЕДСТВИЕ: команда инжектит `RestTemplate` в WebFlux, блокирует event-loop, latency p99 уходит в 30s под нагрузкой.
> - [ ] WebFlux не поддерживает аннотации `@RestController`/`@GetMapping` | Поддерживает (annotation-based + functional через `RouterFunction`); работают идентично, отличается только thread model. ❌ ПОСЛЕДСТВИЕ: команда переписывает аннотированный controller на functional API «потому что WebFlux», тратит спринт на одно и то же поведение.
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
> - [ ] Это normal pattern — block() безопасен внутри controller'а WebFlux | `block()` на event-loop потоке Netty бросает `IllegalStateException` (Reactor 3.2+ блокирует это явно). ❌ ПОСЛЕДСТВИЕ: разработчик ставит `.block()` в WebFlux controller, в логах `IllegalStateException: blocking is not supported in thread reactor-http-nio-1`, 100% запросов падают.
> - [ ] `block()` блокирует Netty event-loop поток (всего #CPU потоков), что вызывает дедлок, deg latency и thread starvation; Reactor бросает `IllegalStateException` для защиты | `WebFlux` идеологически non-blocking; для bridge с blocking-кодом нужен `subscribeOn(Schedulers.boundedElastic())`, а не `block()`. ✓ ПРИМЕНЯТЬ: для legacy JDBC из WebFlux: `Mono.fromCallable(blockingCall).subscribeOn(boundedElastic())`; никогда `block()` в reactive controller. 📋 ПРАВИЛО: «block() в WebFlux — деградация и дедлок». 🔗 См. Q30, Q31, Q46.
> - [ ] `block()` безопасен, если вызвать его на отдельном потоке через `new Thread()` | Создание потоков вручную — антипаттерн (нет управления, нет integration с reactor scheduler), всё равно блокирует. ❌ ПОСЛЕДСТВИЕ: команда плодит `new Thread()` для обхода защиты Reactor, JVM забивается thread'ами без bound, OOM thread stacks через 30 минут.
> - [ ] Достаточно поставить `@Async` над методом с `block()` | `@Async` использует свой `TaskExecutor`, не интегрируется с Reactor backpressure/Context, под нагрузкой пул `@Async` не масштабируется. ❌ ПОСЛЕДСТВИЕ: `@Async` + `block()` создаёт thread-per-request, преимущества WebFlux теряются, latency и memory как у обычного MVC.
- [Reactive Patterns](reactive-patterns-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](webflux-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
