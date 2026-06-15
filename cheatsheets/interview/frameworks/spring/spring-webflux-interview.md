---
title: "Вопросы на собеседовании: Spring WebFlux"
description: "Краткие ответы по Spring WebFlux: Mono / Flux, реактивная модель, WebClient, отличия от MVC, R2DBC."
tags:
  - interview
  - frameworks
  - spring-webflux-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring WebFlux"
  - "Spring WebFlux interview"
  - "Spring WebFlux собеседование"
prerequisites:
  - "[[spring-webflux]]"
next: []
updated: "2026-05-07"
---
# Вопросы на собеседовании: `Spring WebFlux`

Краткие ответы по `Spring WebFlux`: `Mono / Flux`, реактивная модель, `WebClient`, отличия от `MVC`, `R2DBC`.

Краткое введение: `Spring WebFlux` — реактивный веб-стек на `Project Reactor`. На собеседованиях проверяют понимание реактивной модели, `Mono / Flux` и отличий от `Spring MVC`.

## Полезные ссылки

### Официальная документация

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html) — основной справочник по реактивному веб-стеку
- [Project Reactor Reference](https://projectreactor.io/docs/core/release/reference/) — документация Reactor: Mono, Flux, операторы

### Baeldung

- [Guide to Spring WebFlux](https://www.baeldung.com/spring-webflux) — полный туториал по WebFlux
- [Difference Between Flux and Mono](https://www.baeldung.com/java-reactor-flux-vs-mono) — когда использовать Mono, а когда Flux
- [Spring WebClient](https://www.baeldung.com/spring-5-webclient) — реактивный HTTP-клиент: настройка и использование
- [Concurrency in Spring WebFlux](https://www.baeldung.com/spring-webflux-concurrency) — модель потоков, event loop и Schedulers
- [Set a Timeout in Spring WebClient](https://www.baeldung.com/spring-webflux-timeout) — настройка таймаутов и retry в WebClient
- [Using Reactor Mono.cache() for Memoization](https://www.baeldung.com/spring-reactor-mono-cache) — кеширование в реактивных цепочках

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Spring WebFlux**
- [Q1. Что такое `Project Reactor` и `Spring WebFlux`?](#q1-что-такое-project-reactor-и-spring-webflux)
- [Q2. (!) В чем отличие между `Project Reactor` и `Spring WebFlux`?](#q2--в-чем-отличие-между-project-reactor-и-spring-webflux)
- [Q3. (!) Какова архитектура `Project Reactor`?](#q3--какова-архитектура-project-reactor)
- [Q4. (!) Какова архитектура `Spring WebFlux`?](#q4--какова-архитектура-spring-webflux)

**Реактивное программирование**
- [Q5. Что такое реактивное программирование?](#q5-что-такое-реактивное-программирование)
- [Q6. Какие основные принципы реактивного программирования?](#q6-какие-основные-принципы-реактивного-программирования)
- [Q7. (!) Как реализована асинхронность в `Project Reactor`?](#q7--как-реализована-асинхронность-в-project-reactor)
- [Q8. (!) Как реализована асинхронность в `Spring WebFlux`?](#q8--как-реализована-асинхронность-в-spring-webflux)

**Flux, Mono и операторы**
- [Q9. Что такое `Flux` и `Mono` в `Project Reactor`?](#q9-что-такое-flux-и-mono-в-project-reactor)
- [Q10. Какие методы доступны для работы с реактивными потоками в `Spring WebFlux`?](#q10-какие-методы-доступны-для-работы-с-реактивными-потоками-в-spring-webflux)
- [Q11. Как обрабатывать ошибки в `Project Reactor`?](#q11-как-обрабатывать-ошибки-в-project-reactor)
- [Q12. Как обрабатывать ошибки в `Spring WebFlux`?](#q12-как-обрабатывать-ошибки-в-spring-webflux)
- [Q13. Как тестировать реактивные приложения, построенные на `Project Reactor` и `Spring WebFlux`?](#q13-как-тестировать-реактивные-приложения-построенные-на-project-reactor-и-spring-webflux)
- [Q14. Что такое `backpressure` и как с ним работать?](#q14-что-такое-backpressure-и-как-с-ним-работать)

**WebClient и R2DBC**
- [Q15. Чем `WebClient` отличается от `RestTemplate`?](#q15-чем-webclient-отличается-от-resttemplate)
- [Q16. Как использовать `R2DBC` с `Spring Data`?](#q16-как-использовать-r2dbc-с-spring-data)
- [Q17. Что такое `Schedulers` и когда какой использовать?](#q17-что-такое-schedulers-и-когда-какой-использовать)
- [Q18. Как реализовать `Server-Sent Events` (`SSE`) в `WebFlux`?](#q18-как-реализовать-server-sent-events-sse-в-webflux)
- [Q19. Что такое cold и hot publishers?](#q19-что-такое-cold-и-hot-publishers)
- [Q20. Как комбинировать `Mono` и `Flux` (zip, merge, concat)?](#q20-как-комбинировать-mono-и-flux-zip-merge-concat)

**Безопасность и маршрутизация**
- [Q21. Как обеспечить безопасность в `WebFlux` (`Spring Security`)?](#q21-как-обеспечить-безопасность-в-webflux-spring-security)
- [Q22. Что такое `RouterFunction` vs `@RestController`?](#q22-что-такое-routerfunction-vs-restcontroller)
- [Q23. Как валидировать запросы в реактивном стеке?](#q23-как-валидировать-запросы-в-реактивном-стеке)
- [Q24. Когда выбирать `WebFlux` вместо `Spring MVC`?](#q24-когда-выбирать-webflux-вместо-spring-mvc)

**Продвинутые темы**
- [Q25. Как обрабатывать таймауты и retry в реактивных цепочках?](#q25-как-обрабатывать-таймауты-и-retry-в-реактивных-цепочках)
- [Q26. Что такое `Reactor Context` и для чего он нужен?](#q26-что-такое-reactor-context-и-для-чего-он-нужен)
- [Q27. Как тестировать с `StepVerifier`?](#q27-как-тестировать-с-stepverifier)
- [Q28. Как обеспечить транзакционность в `R2DBC`?](#q28-как-обеспечить-транзакционность-в-r2dbc)
- [Q29. Какие метрики и мониторинг для `WebFlux`?](#q29-какие-метрики-и-мониторинг-для-webflux)
- [Q30. Какие best practices для реактивных приложений?](#q30-какие-best-practices-для-реактивных-приложений)

**Реактивная маршрутизация и фильтры**
- [Q31. (!) Как использовать `RouterFunction` для функционального стиля маршрутизации?](#q31--как-использовать-routerfunction-для-функционального-стиля-маршрутизации)
- [Q32. (!) Что такое `WebFilter` и как он работает?](#q32--что-такое-webfilter-и-как-он-работает)
- [Q33. Как реализовать `WebSocket` в `Spring WebFlux`?](#q33-как-реализовать-websocket-в-spring-webflux)
- [Q34. (!) Как настроить реактивный `WebClient` с retry и таймаутами?](#q34--как-настроить-реактивный-webclient-с-retry-и-таймаутами)
- [Q35. Как управлять `WebSession` в реактивном стеке?](#q35-как-управлять-websession-в-реактивном-стеке)
- [Q36. (!) Как интегрировать `R2DBC` с `Spring Data` и получить полностью реактивный стек?](#q36--как-интегрировать-r2dbc-с-spring-data-и-получить-полностью-реактивный-стек)
- [Q37. Какие операторы `Project Reactor` важнее всего знать для собеседования?](#q37-какие-операторы-project-reactor-важнее-всего-знать-для-собеседования)

**WebFlux и современные альтернативы**
- [Q38. Как WebFlux работает совместно с Virtual Threads (Java 21)?](#q38-как-webflux-работает-совместно-с-virtual-threads-java-21)
- [Q39. Как интегрировать Resilience4j с WebFlux?](#q39-как-интегрировать-resilience4j-с-webflux)
- [Q40. Как Spring Security работает в реактивном стеке?](#q40-как-spring-security-работает-в-реактивном-стеке)
- [Q41. Чем R2DBC отличается от JDBC и как использовать R2dbcRepository?](#q41-чем-r2dbc-отличается-от-jdbc-и-как-использовать-r2dbcrepository)
- [Q42. WebClient vs RestTemplate vs RestClient — когда что выбирать?](#q42-webclient-vs-resttemplate-vs-restclient--когда-что-выбирать)
- [Q43. Как тестировать WebFlux-приложения с WebTestClient и StepVerifier?](#q43-как-тестировать-webflux-приложения-с-webtestclient-и-stepverifier)

## Q1. Что такое `Project Reactor` и `Spring WebFlux`?

Это две связанные технологии для реактивного программирования в `Java`: `Project Reactor` — библиотека, `Spring WebFlux` — построенный на ней веб-фреймворк.

- **Project Reactor** — реактивная библиотека от команды `Spring`. Даёт два базовых типа: `Flux` (поток из нуля и более элементов) и `Mono` (ноль или один элемент), плюс богатый набор операторов для асинхронной обработки данных. Не привязана к вебу — её можно использовать в любом коде.
- **Spring WebFlux** — реактивный веб-модуль `Spring Framework` поверх Reactor. Работает на неблокирующем I/O: горстка потоков обслуживает тысячи одновременных запросов, потому что поток не простаивает в ожидании сети или БД. Поддерживает два стиля обработчиков — аннотированные контроллеры (`@RestController` с возвратом `Mono`/`Flux`) и функциональные маршруты (`RouterFunction`), а также `WebSocket` и `SSE`.

**Суть связки:** Reactor — это «движок» (типы и операторы), WebFlux — «обёртка», которая подключает этот движок к HTTP-серверу. Вместе они дают масштабируемые приложения, держащие высокую нагрузку малым числом потоков.

## Q2. (!) В чем отличие между `Project Reactor` и `Spring WebFlux`?

Разница в уровне: Reactor — это библиотека реактивных типов, WebFlux — веб-фреймворк, который её использует.

- **Project Reactor** — самостоятельная библиотека: `Flux`, `Mono`, операторы, реализация спецификации `Reactive Streams`. Не зависит от веба и работает где угодно (сервисы, обработка данных, тесты).
- **Spring WebFlux** — модуль `Spring`, надстройка над Reactor для веб-слоя: аннотированные контроллеры (`@RestController` с возвратом `Mono`/`Flux`) или функциональные маршруты (`RouterFunction`), плюс реактивный `WebClient` для исходящих вызовов.

**Как запомнить:** Reactor можно подключить в проект без WebFlux, а WebFlux без Reactor существовать не может — он построен поверх его типов. На собеседовании частая ловушка — путать их или считать одним целым.

## Q3. (!) Какова архитектура `Project Reactor`?

В основе — спецификация **Reactive Streams** из четырёх интерфейсов:

- **Publisher** — источник данных (в Reactor это `Flux` и `Mono`).
- **Subscriber** — потребитель: получает данные через колбэки `onNext` / `onComplete` / `onError`.
- **Subscription** — связь между ними; через неё подписчик запрашивает данные (`request(n)`) и управляет потоком (`backpressure`).
- **Processor** — одновременно и Publisher, и Subscriber (промежуточное звено).

Поток запускается только при вызове `subscribe()` — до этого ничего не происходит. Данные идут не «как попало», а по запросу подписчика, что и даёт backpressure. Reactor реализует этот контракт и добавляет сверху сотни операторов (`map`, `flatMap`, `zip` и т.д.).

Контракт `Reactive Streams` — обмен сигналами между `Publisher` (`Flux` / `Mono`) и `Subscriber`, по порядку:

- `Subscriber` вызывает `subscribe()` у `Publisher`;
- `Publisher` отвечает сигналом `onSubscribe(Subscription)`;
- `Subscriber` запрашивает данные через `request(n)`;
- `Publisher` отдаёт элементы через `onNext(item)`;
- завершение — терминальный сигнал `onComplete()` или `onError()`.

Слои самого `Project Reactor` (сверху вниз):

- `Reactive Streams API` → `reactor-core` (`Flux`, `Mono`, `Schedulers`);
- `reactor-core` → операторы (`map`, `flatMap`, `filter`, `zip`...);
- операторы → `reactor-extra` (`retry`, `cache`, `math`);
- `reactor-core` → `reactor-netty` (`HTTP`, `TCP`).

**Связь с Java:** сам `Reactive Streams` API вошёл в JDK 9+ как `java.util.concurrent.Flow` (см. [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md)). Reactor совместим с ним и даёт мост через `JdkFlowAdapter` — поэтому его типы можно стыковать со стандартными `Flow.Publisher`/`Flow.Subscriber`.

## Q4. (!) Какова архитектура `Spring WebFlux`?

Архитектура повторяет `MVC` по структуре, но работает на неблокирующем стеке. Запрос проходит цепочку:

- **DispatcherHandler** — реактивный аналог `DispatcherServlet`: принимает запрос и оркеструет обработку.
- **HandlerMapping** — сопоставляет `URL` с нужным обработчиком.
- **HandlerAdapter** — вызывает обработчик и адаптирует результат.
- **Обработчик** — возвращает `Mono` или `Flux`; это и есть тело будущего ответа (выполнится позже, лениво).

Маршруты задают двумя способами: аннотациями (`@RestController`, `@GetMapping`) или функционально (**RouterFunction**). Под капотом вместо Servlet API — `Netty` с собственными `ServerHttpRequest`/`ServerHttpResponse`; для исходящих HTTP-вызовов используется реактивный **WebClient**.

**Важно: WebFlux не привязан к Netty.** `Netty` — лишь сервер по умолчанию; тот же реактивный стек работает и на Servlet-контейнерах с поддержкой неблокирующего I/O (Servlet 3.1+) — `Tomcat`, `Jetty`, а также на `Undertow`. Мост обеспечивает адаптер (`ServletHttpHandlerAdapter`), который транслирует неблокирующий Servlet API в реактивные `ServerHttpRequest`/`ServerHttpResponse`. Это частый вопрос-ловушка: «WebFlux работает только на Netty?» — нет, Netty просто дефолт.

Обработка запроса в `Spring WebFlux`, по шагам:

1. Клиент шлёт HTTP-запрос на `Netty Server` (event loop).
2. `Netty` передаёт запрос в `DispatcherHandler`.
3. `DispatcherHandler` → `HandlerMapping` (маршрутизация).
4. `HandlerMapping` → `HandlerAdapter`.
5. `HandlerAdapter` выбирает тип обработчика:
   - аннотации → `@RestController` / `@GetMapping`;
   - функциональный → `RouterFunction` + `HandlerFunction`.
6. Любой из обработчиков возвращает реактивный ответ `Mono` / `Flux`.
7. `Mono` / `Flux` → `ResultHandler` (запись ответа) → обратно в `Netty` клиенту.

Внешние вызовы из контроллера (`@RestController`): через `WebClient` — к внешнему API; через `R2DBC` — к базе данных.

**Главное отличие от [Spring MVC](spring-mvc-interview.md):** `MVC` стоит на `DispatcherServlet` и Servlet API (модель «поток на запрос»), а `WebFlux` — на `DispatcherHandler` и неблокирующем I/O (горстка потоков на event loop). Имена компонентов почти совпадают, но семантика выполнения принципиально разная.

## Q5. Что такое реактивное программирование?

Реактивное программирование — это парадигма, где код описывает обработку **потоков данных** декларативно: вы задаёте, *что* делать с элементами по мере их появления, а не пишете императивные циклы ожидания. Программа «реагирует» на события асинхронно, не блокируя поток выполнения в ожидании результата.

Ключевая разница с обычным (императивным) подходом: вместо «вызвал метод → ждём ответа → продолжили» мы строим конвейер из операторов и подписываемся на результат. Когда данные приходят, конвейер срабатывает сам.

Из чего складывается подход:

- **Потоки данных (Streams)** — последовательности событий или значений во времени; их обрабатывают и передают дальше.
- **Функциональные операторы** — `map`, `filter`, `flatMap` и др.: преобразуют, фильтруют и комбинируют потоки декларативно.
- **Подписки (Subscriptions)** — компонент подписывается на поток и реагирует на каждое новое значение.
- **Асинхронность** — операции не блокируют поток, поэтому система остаётся отзывчивой под нагрузкой.

**Где применяют:** веб-приложения, микросервисы, шлюзы — везде, где много одновременных I/O-операций и важна масштабируемость при ограниченном числе потоков.

## Q6. Какие основные принципы реактивного программирования?

Четыре из них формулирует **Reactive Manifesto** — это свойства реактивной *системы*; остальные два описывают реактивный *код*.

Свойства системы (Reactive Manifesto):

- **Отзывчивость (Responsive)** — система отвечает быстро и предсказуемо; цель остальных трёх свойств — обеспечить именно это.
- **Эластичность (Elastic)** — производительность масштабируется под нагрузку: добавили ресурсов — выросла пропускная способность, без узких мест на блокирующих потоках.
- **Устойчивость к сбоям (Resilient)** — отказ одной части не роняет всю систему; ошибки изолируются и обрабатываются (восстановление, fallback).
- **Сообщения (Message-driven)** — компоненты общаются асинхронными сообщениями, что и даёт слабую связанность, изоляцию сбоев и эластичность.

Свойства кода, которыми всё это достигается:

- **Асинхронность и неблокирующий I/O** — операции не блокируют поток в ожидании результата, поэтому горстка потоков обслуживает множество запросов.
- **Декларативная обработка потоков** — данные текут через цепочку операторов (`map`, `filter`, `flatMap`), а подписчик реагирует на каждое новое значение и сигнал backpressure.

**Связь:** message-driven код на неблокирующем I/O даёт эластичность и устойчивость, а они вместе — конечную цель, отзывчивость.

## Q7. (!) Как реализована асинхронность в `Project Reactor`?

Асинхронность в Reactor держится на двух вещах: реактивных типах (`Flux`/`Mono`) и планировщиках (`Schedulers`).

- **Реактивные типы как абстракция над потоком.** `Flux` и `Mono` не выполняют работу сразу — они описывают будущий поток данных. Операторы строят цепочку преобразований, а реальное выполнение начинается при `subscribe()`. Это и позволяет работать неблокирующе: вместо «вызвали и ждём» мы регистрируем колбэки, которые сработают, когда данные придут (паттерн «наблюдатель»).
- **Schedulers — где выполнять.** Сами по себе операторы выполняются на потоке, который вызвал `subscribe()`. Чтобы вынести работу на другой пул, используют `Schedulers`: они задают контекст выполнения и переключают потоки между этапами цепочки.

Примеры планировщиков:

- `Schedulers.parallel()` — пул из N потоков (по числу ядер) для параллельных CPU-задач.
- `Schedulers.single()` — один поток для строго последовательного выполнения.

**Итог:** реактивные типы дают неблокирующую модель «реагирования на события», а Schedulers контролируют, на каких потоках эти события обрабатываются. Вместе это позволяет держать высокую нагрузку малым числом потоков.

## Q8. (!) Как реализована асинхронность в `Spring WebFlux`?

Асинхронность в WebFlux обеспечивают четыре связанных элемента — реактивные типы, неблокирующий I/O, реактивные контроллеры и event-loop-сервер:

1. **Реактивные типы данных.** Контроллер возвращает не готовый объект, а `Mono<T>` (одно значение) или `Flux<T>` (поток). Это «обещание» результата — поток не блокируется в ожидании, пока данные не готовы.
2. **Неблокирующий I/O.** Сетевые вызовы и обращения к БД не держат поток заблокированным; он освобождается и обслуживает другие запросы, пока ждёт ответа. Отсюда — больше запросов меньшим числом потоков.
3. **Реактивные контроллеры.** `@GetMapping` и прочие маппинги выглядят как в [Spring MVC](spring-mvc-interview.md), но метод возвращает `Mono`/`Flux`, а не синхронный объект.
4. **Сервер на event loop (`Netty`).** По умолчанию WebFlux работает на `Netty`: 1–2 потока на ядро в цикле событий обслуживают тысячи соединений, переключаясь между ними по готовности I/O.

Модель Event Loop (`Netty`): `Event Loop` (1–2 потока на ядро) разбирает очередь событий и по готовности I/O выполняет фазы обработки:

- `read` — чтение запроса;
- `decode` — декодирование;
- `handler` — обработчик (`Mono` / `Flux`);
- `write` — запись ответа.

Для сравнения, модель Thread-per-Request (`MVC`): отдельный поток на каждый запрос, и поток блокирован на I/O всё время обработки — поток 1 ↔ запрос 1, поток 2 ↔ запрос 2, ..., поток N ↔ запрос N.

**Ключевое отличие от thread-per-request:** в модели [Spring MVC](spring-mvc-interview.md) каждый запрос держит отдельный поток на всё время обработки, включая простой в ожидании I/O — под нагрузкой пул потоков становится узким местом. В event-loop-модели один поток обслуживает множество запросов, переключаясь между ними по завершении I/O-событий. Именно поэтому WebFlux держит тысячи соединений малым числом потоков и эффективнее под высокой нагрузкой.

## Q9. Что такое `Flux` и `Mono` в `Project Reactor`?

`Flux` и `Mono` — два базовых реактивных типа Reactor. Разница только в кардинальности: сколько элементов может прийти.

**`Flux<T>` — поток из 0..N элементов.** Это асинхронная «коллекция»: конечная (как список) или бесконечная (как `Flux.interval`). Поддерживает все операторы преобразования, фильтрации и комбинирования, а также параллельную обработку.

Пример использования `Flux`:

```java
Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5);
numbers.map(n -> n * 2).filter(n -> n > 5).subscribe(System.out::println);
```

**`Mono<T>` — 0..1 элемент.** Аналог `Optional`, а в других библиотеках — `Single`/`Maybe`. Подходит для результата одиночной операции: запрос по id, сохранение сущности, вызов сервиса, который вернёт одно значение или ничего.

Пример использования `Mono`:

```java
Mono<String> greeting = Mono.just("Hello");
greeting.map(s -> s + " World").subscribe(System.out::println);
```

Конвейер `Mono` (0..1 элемент): `Mono.just(x)` → операторы (`map` / `flatMap` / `filter` / `zipWith`) → сигналы `onNext(x)` → `onComplete()`.

Конвейер `Flux` (0..N элементов): `Flux.just(1,2,3)` → операторы (`map` / `flatMap` / `filter` / `take` / `reduce`) → сигналы `onNext(1)` → `onNext(2)` → `onNext(3)` → `onComplete()`.

Преобразования между типами:

- `Mono` → `Flux` через `flatMapMany()`;
- `Flux` → `Mono` через `next()` / `single()`;
- `Flux` → `Mono<List>` через `collectList()`.

Оба типа предоставляют методы `map`, `filter`, `flatMap`, `reduce` и другие. Аналогия со [Java Stream API](../../programming-languages/java/java-stream-interview.md): `Flux` похож на `Stream<T>`, а `Mono` — на `Optional<T>`, но с поддержкой асинхронности и backpressure.

## Q10. Какие методы доступны для работы с реактивными потоками в `Spring WebFlux`?

Это операторы Reactor — методы `Flux`/`Mono`, которые строят цепочку обработки. Их сотни; на собеседовании достаточно уверенно владеть базовым набором по группам:

Преобразование и фильтрация:

- **`map`** — синхронно преобразует каждый элемент (`1:1`).
- **`flatMap`** — преобразует элемент в другой поток и сливает результаты в один (`1:N`, асинхронно).
- **`filter`** — пропускает только элементы, удовлетворяющие условию.

Ограничение и агрегация:

- **`take`** — берёт первые N элементов и завершает поток.
- **`reduce`** — сворачивает поток в одно значение (агрегация в `Mono`).

Комбинирование и обработка ошибок:

- **`zip`** — попарно объединяет элементы нескольких потоков.
- **`onErrorResume`** — подставляет альтернативный поток при ошибке.
- **`retry`** — повторяет цепочку при ошибке.

Помимо операторов, реактивный стек даёт компоненты для I/O: `WebClient` для HTTP-вызовов и `R2DBC` для реактивного доступа к БД — они возвращают `Mono`/`Flux` и встраиваются в те же цепочки.

## Q11. Как обрабатывать ошибки в `Project Reactor`?

Ошибка в реактивном потоке — это терминальный сигнал `onError`: он прекращает поток так же, как исключение прерывает обычный код. Reactor даёт операторы, чтобы перехватить ошибку и решить, что делать дальше — вернуть запасное значение, переключиться на другой поток или повторить попытку.

1. `doOnError`: побочное действие при ошибке — логирование, метрики, алерт. Важный нюанс: оператор **не перехватывает** ошибку и не «гасит» её — поток всё равно завершится сигналом `onError`, который пойдёт дальше по цепочке. Это «подсмотреть», а не «обработать». Отдельного оператора `onError` в Reactor нет: `onError` — это терминальный *сигнал*, а реагируют на него `doOnError` и операторы семейства `onError*` ниже.

```java
flux.doOnError(err -> {
    // побочное действие при ошибке (лог, метрика); ошибка идёт дальше
})
```

2. `onErrorReturn`: при ошибке подставляет фиксированное значение по умолчанию вместо неё — поток завершается этим значением, а не падает.

```java
flux.onErrorReturn("Default Value")
```

3. `onErrorResume`: при ошибке переключается на альтернативный поток (в отличие от `onErrorReturn`, который даёт одно значение, — здесь можно вернуть целый `Mono`/`Flux`, например запрос к резервному источнику).

```java
flux.onErrorResume(err -> {
    // вернуть альтернативный поток
})
```

4. `retry`: повторно подписывается на источник при ошибке (по сути — перезапускает операцию). Можно задать число попыток или, через `retryWhen`, условия и задержку между ними.

```java
flux.retry(3) // повторить операцию 3 раза
```

**Как выбрать:** запасное значение → `onErrorReturn`; запасной поток/источник → `onErrorResume`; повтор → `retry`/`retryWhen`; только залогировать, не глуша ошибку → `doOnError`.

## Q12. Как обрабатывать ошибки в `Spring WebFlux`?

В WebFlux ошибки ловят на разных уровнях — от точечного перехвата в цепочке до глобального обработчика на всё приложение. Выбор зависит от того, насколько широко нужно покрыть ошибки.

1. **`@ExceptionHandler` (локально/в `@ControllerAdvice`)** — метод-обработчик для конкретного типа исключения: логирует, формирует кастомный ответ. Удобно для доменных исключений с понятным маппингом на HTTP-статус.

```java
@ExceptionHandler(YourException.class)
public Mono<ServerResponse> handleYourException(YourException ex) {
    // обработка исключения
    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Custom error message");
}
```

2. **`WebExceptionHandler` (глобально)** — реактивный, самый низкоуровневый механизм: класс, реализующий интерфейс, перехватывает любые исключения на уровне `ServerWebExchange` для всего приложения. Гибче `@ExceptionHandler`, но требует ручной работы с ответом: обработчик сам выставляет статус и пишет тело в `exchange.getResponse()` через `writeWith(...)`, а возвращаемый `Mono<Void>` — сигнал завершения записи ответа.

```java
@Component
public class CustomExceptionHandler implements WebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        DataBuffer buffer = response.bufferFactory()
            .wrap("Custom error message".getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
```

3. **Оператор `onErrorResume` (точечно в цепочке)** — обработка ошибки прямо в реактивном конвейере: подставляет альтернативный поток или ответ. Подходит, когда обработка специфична для конкретной операции, а не общая для приложения.

```java
flux.onErrorResume(ex -> {
    // обработка ошибки
    return Mono.just("Error occurred");
})
```

4. **`HandlerExceptionResolver` (Servlet-механизм, для сравнения).** Это глобальный обработчик из мира `Spring MVC` (Servlet-стек, `HttpServletRequest`, `ModelAndView`). В чисто реактивном WebFlux он не используется — его реактивный аналог как раз `WebExceptionHandler` из п. 2. Полезно знать различие, чтобы не перепутать стеки на собеседовании.

```java
@Component
public class GlobalExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // обработка исключения
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", "An error occurred");
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
```

**Эмпирическое правило:** локальная ошибка одной операции → `onErrorResume`; маппинг доменных исключений на ответы → `@ExceptionHandler` в `@ControllerAdvice`; единый перехват всего → `WebExceptionHandler`.

## Q13. Как тестировать реактивные приложения, построенные на `Project Reactor` и `Spring WebFlux`?

Тестируют на двух уровнях: реактивные цепочки (сервисы) — через `StepVerifier`, HTTP-эндпоинты — через `WebTestClient`.

- **`StepVerifier` (Reactor)** — для unit-тестов `Mono`/`Flux`. Подписывается на поток и пошагово проверяет: ожидаемые элементы (`expectNext`), ошибки (`expectError`), завершение (`verifyComplete`). Главное преимущество — корректно работает с асинхронностью, а через виртуальное время (`withVirtualTime`) тестирует `delay`/`interval` без реальных задержек.
- **`WebTestClient` (Spring)** — для интеграционных тестов контроллеров: шлёт HTTP-запросы и проверяет статус, заголовки, тело. Умеет работать как против запущенного сервера, так и против замоканного (`@WebFluxTest`, `bindToRouterFunction`).
- **JUnit + `Mockito`** — для компонентов без HTTP: моки репозиториев/сервисов возвращают `Mono.just(...)`/`Flux.just(...)`, а результат проверяется через `StepVerifier`.
- **Тестовая БД** — для интеграции с хранилищем: эмбеддед-варианты вроде `H2` или `Embedded MongoDB` проверяют реальные запросы.

**Главный нюанс реактивного тестирования:** поток ничего не делает без подписки. `StepVerifier` сам подписывается при `verify()`; если просто вызвать сервис и не проверить через верификатор — тест не выполнит цепочку и ничего не проверит.

## Q14. Что такое `backpressure` и как с ним работать?

**Backpressure** — механизм протокола `Reactive Streams`, при котором подписчик через `Subscription.request(n)` сообщает издателю, сколько элементов он готов принять. Это предотвращает переполнение потребителя, когда издатель выдаёт данные быстрее, чем потребитель их обрабатывает.

Обмен сигналами между `Publisher` (`Flux`) и `Subscriber`, по порядку:

1. `Subscriber` → `Publisher`: `subscribe()`.
2. `Publisher` → `Subscriber`: `onSubscribe(Subscription)`.
3. `Subscriber` → `Publisher`: `request(3)`.
4. `Publisher` → `Subscriber`: `onNext(item1)`, `onNext(item2)`, `onNext(item3)`.
5. Дальше `Publisher` ждёт нового `request()` — больше элементов не шлёт, пока его не запросят.
6. `Subscriber` → `Publisher`: `request(2)`.
7. `Publisher` → `Subscriber`: `onNext(item4)`, `onNext(item5)`, затем `onComplete()`.

В `Project Reactor backpressure` поддерживается из коробки: при подписке передаётся запрос на объём данных. Операторы обратной связи:

- **`onBackpressureBuffer(int capacity)`** — буферизует элементы до заданного лимита; при переполнении по умолчанию — `BufferOverflowError`; можно задать стратегию переполнения (DROP_OLDEST и т.д.).
- **`onBackpressureDrop()`** — отбрасывает элементы, которые потребитель не успел запросить; подходит, когда допустима потеря части данных (например, сенсорные сэмплы).
- **`onBackpressureLatest()`** — хранит только последний необработанный элемент; старые отбрасываются; полезно для «текущего значения» (например, курс, температура).

Для медленного потребителя (запись в БД, внешний `API`) часто ограничивают параллелизм через **`flatMap(`maxConcurrency`)`**, а не полагаются только на буфер: `flux.flatMap(item -> saveToDb(item), 10)` — не более 10 одновременных сохранений.

```java
// Буфер до 100 элементов; при переполнении — ошибка
Flux.range(1, 1000)
    .onBackpressureBuffer(100)
    .subscribe(n -> slowConsumer(n));

// Только последнее значение при отставании
Flux.interval(Duration.ofMillis(10))
    .onBackpressureLatest()
    .subscribe(System.out::println);

// Ограничение параллелизма вместо неограниченного потока
Flux.range(1, 1000)
    .flatMap(id -> repository.save(entity(id)), 5)
    .subscribe();
```

## Q15. Чем `WebClient` отличается от `RestTemplate`?

| Критерий | `WebClient` | `RestTemplate` |
|----------|-----------|--------------|
| Модель | Реактивная, неблокирующая | Блокирующая (один запрос — один поток) |
| Возвращаемый тип | `Mono<T>`, `Flux<T>` | `ResponseEntity<T>`, тело напрямую |
| Движок по умолчанию | `Netty` (или `Jetty`, `Tomcat` реактивный) | `Apache HttpClient` или стандартный `HttpURLConnection` |
| Масштабирование | Мало потоков, много одновременных запросов за счёт неблокирующего I/O | Рост числа потоков при росте запросов |

**WebClient** предпочтителен в реактивном стеке (`Spring WebFlux`), при высокой нагрузке и при необходимости стриминга тела ответа (`bodyToFlux`). **RestTemplate** проще для синхронного кода и legacy-интеграций.

В `WebFlux`-приложении вызов **RestTemplate** блокирует event loop; если его нельзя убрать, выполняют в отдельном пуле: `Mono.fromCallable(() -> restTemplate.getForObject(...)).subscribeOn(Schedulers.boundedElastic())`. Для новых интеграций используют `WebClient`.

```java
// WebClient — один запрос, Mono
WebClient client = WebClient.create("https://api.example.com");
Mono<User> user = client.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class);

// Стриминг списка
Flux<Item> items = client.get()
    .uri("/items")
    .retrieve()
    .bodyToFlux(Item.class);

// В тесте или при миграции — блокирующий вызов (не в реактивной цепочке!)
User u = user.block(Duration.ofSeconds(5));
```

## Q16. Как использовать `R2DBC` с `Spring Data`?

`R2DBC` — реактивный доступ к реляционным БД; со `Spring Data` он работает почти как `JPA`, но репозитории отдают `Mono`/`Flux` вместо синхронных объектов.

Что нужно сделать:

- Подключить стартер `spring-boot-starter-data-r2dbc` и драйвер БД, настроить `ConnectionFactory`.
- Объявить репозиторий, наследующий `ReactiveCrudRepository` (или `R2dbcRepository`); его методы возвращают `Mono`/`Flux`.
- Запросы задаются как derived queries (по имени метода) или через `@Query` с SQL.
- Транзакции — через `@Transactional` в сервисе (работает реактивный `ReactiveTransactionManager`) или программно через `TransactionalOperator`.

**Чего нет по сравнению с JPA:** кэша 2-го уровня, lazy-загрузки связей и автоматических `@OneToMany`. Связанные сущности приходится грузить явными запросами. Это плата за неблокирующую модель — подробнее в Q36 и Q41.

## Q17. Что такое `Schedulers` и когда какой использовать?

`Schedulers` задают пул потоков (подробнее о пулах — в [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md)) для операторов:

| Scheduler | Потоки | Назначение |
|-----------|--------|------------|
| `parallel()` | Фиксированный пул (N = кол-во ядер) | CPU-bound задачи |
| `boundedElastic()` | Растущий пул с лимитом | Блокирующий I/O (JDBC, файлы) |
| `single()` | Один поток | Последовательные задачи |
| `immediate()` | Текущий поток | Без переключения |

**`publishOn`** меняет scheduler для downstream-операторов; **`subscribeOn`** — для всей цепочки от источника. Для блокирующих вызовов в реактивной цепочке используют `subscribeOn(Schedulers.boundedElastic())`.

```java
// CPU-bound обработка на parallel пуле
Flux.range(1, 100)
    .publishOn(Schedulers.parallel())
    .map(n -> heavyComputation(n))
    .subscribe();

// Блокирующий вызов на boundedElastic
Mono.fromCallable(() -> blockingService.call())
    .subscribeOn(Schedulers.boundedElastic())
    .flatMap(result -> reactiveProcess(result))
    .subscribe();
```

## Q18. Как реализовать `Server-Sent Events` (`SSE`) в `WebFlux`?

**SSE** — однонаправленный поток событий от сервера к клиенту по одному долгоживущему HTTP-соединению. В WebFlux он ложится на `Flux` естественно: эндпоинт просто возвращает поток, а фреймворк дописывает каждый элемент в открытый ответ по мере появления.

Как реализовать:

- Контроллер возвращает `Flux<T>` или `Flux<ServerSentEvent<T>>` с типом контента `MediaType.TEXT_EVENT_STREAM_VALUE`: `@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE) Flux<Event> stream()`.
- `Flux<ServerSentEvent<T>>` нужен, когда хотите управлять метаданными события (id, event-name, retry); для простого случая хватает `Flux<T>`.
- Событие собирают через `ServerSentEvent.builder(...).build()`.

**Клиент:** `EventSource` в браузере или `WebTestClient.bodyToFlux(ServerSentEvent.class)` в тестах. Соединение держится открытым, пока `Flux` не завершится.

**Сценарий применения:** push-уведомления, прогресс долгой задачи, котировки, лента событий. Если нужна двусторонняя связь — это уже `WebSocket` (Q33).

## Q19. Что такое cold и hot publishers?

Различие — в том, когда возникают данные и сколько раз они генерируются.

- **Cold publisher** — данные создаются заново при каждой подписке. Каждый подписчик получает полный набор «с нуля» (например, `Flux.just`, `Mono.defer`, HTTP-запрос через `WebClient`). Аналогия: фильм по запросу — каждый смотрит с начала.
- **Hot publisher** — поток данных один на всех; подписчик видит только элементы, пришедшие *после* его подписки, а то, что было раньше, пропускает (`share()`, `publish().refCount()`, `ConnectableFlux`). Аналогия: прямой эфир — подключился позже, начало не увидишь.

**Почему это важно:** cold-источник можно безопасно переиспользовать (повторный вызов = повторный запрос). Hot нужен для широковещания событий, когда один источник кормит много подписчиков и дублировать работу не нужно (биржевые тики, шина событий).

**Подводный камень:** превратить cold в hot (`share()`) без понимания приводит к тому, что поздние подписчики теряют ранние элементы. Если нужно, чтобы они получили историю, используют буферизацию (`replay()`/`cache()`).

## Q20. Как комбинировать `Mono` и `Flux` (zip, merge, concat)?

`Mono.zip(a, b)` — объединить два `Mono` в один. `Flux.merge(flux1, flux2)` — элементы по мере появления. `Flux.concat(flux1, flux2)` — сначала flux1, потом flux2. `Mono.zipWith`, `Flux.zipWith` — комбинировать с другим источником. `flatMap` — преобразовать элемент в новый поток и слить.

Как работают операторы комбинирования на конкретных входах:

- **`zip` — параллельно, попарно.** `Mono A` (User) и `Mono B` (Orders) → `Mono.zip(A, B)` → `Tuple2(User, Orders)`.
- **`merge` — чередование по готовности.** `Flux 1` (A₁ A₂ A₃) и `Flux 2` (B₁ B₂) → `Flux.merge(1, 2)` → A₁ B₁ A₂ B₂ A₃ (элементы вперемешку, по мере появления).
- **`concat` — последовательно.** `Flux 1` (A₁ A₂) и `Flux 2` (B₁ B₂) → `Flux.concat(1, 2)` → A₁ A₂ B₁ B₂ (сначала весь первый поток, потом второй).
- **`flatMap` — трансформация 1:N.** `Flux` (id₁ id₂ id₃) → `flatMap(id → getOrders(id))` → Order₁₁ Order₂₁ Order₁₂ ... (каждый id разворачивается в свой поток заказов, результаты сливаются).

**Когда что использовать:**

| Оператор | Порядок | Параллельность | Типичный сценарий |
|----------|---------|----------------|-------------------|
| `zip` | Попарно | Да | Собрать данные из нескольких источников |
| `merge` | По готовности | Да | Объединить события из нескольких потоков |
| `concat` | Последовательный | Нет | Приоритетный fallback (кэш → БД) |
| `flatMap` | По готовности | Да (concurrency) | Запрос вложенных данных |
| `concatMap` | Последовательный | Нет | Порядок важен |

```java
// zip — параллельный запрос пользователя и его заказов
Mono<User> user = userService.findById(id);
Mono<List<Order>> orders = orderService.findByUserId(id);
Mono<UserProfile> profile = Mono.zip(user, orders)
    .map(t -> new UserProfile(t.getT1(), t.getT2()));

// merge — объединение событий из нескольких источников
Flux<Event> allEvents = Flux.merge(
    kafkaEvents,
    webSocketEvents,
    scheduledEvents
);

// concat — fallback: сначала кэш, потом БД
Flux<Product> products = Flux.concat(
    cacheService.find(query),
    databaseService.find(query)
).take(10);
```

## Q21. Как обеспечить безопасность в `WebFlux` (`Spring Security`)?

Подробнее о `Spring Security` — в [Spring Security](spring-security-interview.md). В реактивном стеке используются реактивные аналоги:

- **`ReactiveUserDetailsService`** вместо `UserDetailsService`
- **`ServerSecurityContextRepository`** вместо `SecurityContextRepository`
- **`SecurityWebFilterChain`** вместо `SecurityFilterChain`
- Фильтры работают с `ServerWebExchange` вместо `HttpServletRequest`

**Ключевая идея:** механизм тот же (фильтры, аутентификация, авторизация), но всё неблокирующее и возвращает `Mono`. Самое важное отличие — `SecurityContext` хранится не в `ThreadLocal` (он бесполезен в event-loop-модели, где запрос «прыгает» между потоками), а в **Reactor Context**. Доступ к нему — через `ReactiveSecurityContextHolder.getContext()`, и он автоматически прокидывается по цепочке. Подробнее с примерами конфигурации — в Q40.

## Q22. Что такое `RouterFunction` vs `@RestController`?

Это два стиля описания маршрутов в WebFlux; функционально они равноценны, разница — в подходе.

- **`@RestController`** — аннотационная модель, как в `Spring MVC`: маршруты «прибиты» к методам через `@GetMapping` и т.п. Привычнее, меньше шаблонного кода, понятна большинству.
- **`RouterFunction`** — функциональная модель: маршруты описываются явно в коде как данные (`route().GET(...).build()`), а логика — в отдельных `HandlerFunction`. Маршрутизация отделена от обработки.

**Когда что:** `@RestController` — для типового `REST API` (по умолчанию). `RouterFunction` — когда маршруты собираются динамически/программно, нужна лёгкая композиция и тестирование без поднятия HTTP-сервера (`bindToRouterFunction`). Оба стиля можно смешивать в одном приложении. Подробный пример — в Q31.

## Q23. Как валидировать запросы в реактивном стеке?

Валидация работает через привычный Bean Validation — `@Valid` с `@RequestBody` в WebFlux **работает так же надёжно, как в MVC**. Разница не в том, «сработает ли проверка», а в том, *как приходит ошибка*.

- На параметре ставят `@Valid` вместе с телом: `@Valid @RequestBody Dto body` — обычный вариант, проверка выполняется после десериализации тела. Можно валидировать и обёрнутое тело: `@Valid @RequestBody Mono<Dto> body` — тогда проверка произойдёт в момент, когда тело реально прочитано и `Mono` материализуется.
- При нарушении ограничений возникает `WebExchangeBindException` (реактивный аналог `MethodArgumentNotValidException` из MVC). **Ключевой реактивный нюанс:** с `Mono<Dto>` ошибка не бросается синхронно до входа в метод, а приходит **error-сигналом внутри реактивной цепочки** — её можно перехватить точечно через `onErrorResume` прямо на `Mono` тела.
- Глобально ошибку перехватывают в `@ControllerAdvice` через `@ExceptionHandler(WebExchangeBindException.class)` и формируют понятный ответ (список полей с ошибками); без обработчика клиент получит `400 Bad Request`.

**Для functional endpoints** (`RouterFunction`, Q31) автоматической валидации нет — там `Validator` вызывают вручную внутри `HandlerFunction`.

## Q24. Когда выбирать `WebFlux` вместо `Spring MVC`?

| Критерий | WebFlux | [Spring MVC](spring-mvc-interview.md) |
|----------|---------|-----------|
| Модель I/O | Неблокирующая (event loop) | Блокирующая (thread-per-request) |
| Кол-во соединений | Тысячи одновременных | Ограничено пулом потоков |
| Стек данных | `R2DBC`, `MongoDB Reactive` | `JPA`, `JDBC` |
| Сложность | Высокая (реактивные цепочки) | Низкая (императивный код) |
| Стриминг | `SSE`, `WebSocket`, `Flux` | Ограничен |

**Выбирайте WebFlux**, если: большое число одновременных соединений (long polling, `SSE`, `WebSocket`), потоковая передача данных, микросервис — шлюз с множеством исходящих вызовов.

**Выбирайте MVC**, если: блокирующий стек (`JPA`, legacy-библиотеки), простой `CRUD`, команда без опыта в реактивном программировании.

**Миграция** с `MVC` на `WebFlux` — не «просто поменять зависимость», а переписать цепочки на `Mono / Flux` и убрать все блокирующие вызовы.

## Q25. Как обрабатывать таймауты и retry в реактивных цепочках?

Таймауты и повторы строятся из трёх операторов, которые ставят в цепочку друг за другом:

- **`timeout(Duration)`** — если за указанное время не пришёл следующий элемент, поток падает с `TimeoutException`. Вариант `timeout(Duration, fallback)` вместо ошибки переключается на запасной `Mono`/`Flux`.
- **`retry(n)`** — при ошибке переподписывается на источник до n раз (без задержки). Для внешних вызовов так делать опасно — мгновенные повторы добивают уже перегруженный сервис.
- **`retryWhen(Retry.backoff(...))`** — правильный способ для сети: повторы с нарастающей задержкой (экспоненциальный backoff) и `jitter`, чтобы развести во времени волну ретраев от множества клиентов.

**Как комбинировать:** сначала `timeout` (ограничить ожидание), затем `retryWhen` с backoff (повторить временные сбои), в конце `onErrorResume` (вернуть fallback, когда все попытки исчерпаны). Важно фильтровать, *что* повторять: ретраить стоит только временные ошибки (5xx, таймаут), а не 4xx — их повтор бессмыслен. Готовый пример — в Q34.

## Q26. Что такое `Reactor Context` и для чего он нужен?

**Reactor Context** — это иммутабельное хранилище «ключ-значение», привязанное к подписке. По сути, реактивная замена `ThreadLocal`: в event-loop-модели обработка «прыгает» между потоками, поэтому `ThreadLocal` не работает, а Context следует за цепочкой независимо от того, на каком потоке выполняется оператор.

Как с ним работают:

- **Запись** — `contextWrite(...)`. Важная особенность: контекст распространяется *снизу вверх* (от подписчика к источнику), поэтому `contextWrite` ставят **после** операторов, которые должны его видеть.
- **Чтение** — `deferContextual(ctx -> ...)` или специальные перегрузки операторов.
- Контекст иммутабелен: каждая запись создаёт новый экземпляр, старые значения не перезаписываются «вживую».

**Зачем нужен:** прокинуть сквозные данные без захламления сигнатур методов — `traceId` для трассировки, `SecurityContext` (так его и хранит Spring Security, Q40), локаль, tenant.

**Подводный камень:** контекст привязан к конкретной подписке. Новая подписка (например, повторный `subscribe()` или отдельная вложенная цепочка) контекст не наследует — его нужно прокидывать заново.

## Q27. Как тестировать с `StepVerifier`?

`StepVerifier.create(`fluxOrMono`)` — создать верификатор. Затем: `expectNext(...)`, `expectNextCount(n)`, `expectComplete()`, `expectError()`, `expectNextMatches(...)`. Завершить вызовом `verify()` или `verify(`Duration`)`. Для виртуального времени — `StepVerifier.withVirtualTime(() -> flux).thenAwait(...).expectNext(...).verify()`.

**Пример:** `StepVerifier.create(service.getUser(id)).expectNextMatches(u -> u.getName().equals("Alice")).expectComplete().verify(Duration.ofSeconds(2))` — проверяем, что `Mono` выдаёт один элемент и завершается. Для `Flux`: `expectNext(1).expectNext(2).expectNextCount(5).expectComplete().verify()`. Без вызова `verify()` подписка не выполняется — тест не проверит поток.

## Q28. Как обеспечить транзакционность в `R2DBC`?

Транзакции в R2DBC реактивные — управляются `ReactiveTransactionManager`. Есть три способа, от самого удобного к самому ручному:

- **`@Transactional`** — декларативный, как в JPA. Работает поверх реактивного менеджера; коммит на успешном завершении `Mono`/`Flux`, откат — при сигнале `onError`. Подходит в большинстве случаев.
- **`TransactionalOperator`** — программный: оборачивает цепочку оператором `transactional(...)`. Удобно, когда границу транзакции нужно задать в коде, а не аннотацией.
- **Ручное управление** через `Connection`: `beginTransaction()` → `flatMap` с бизнес-операциями → `commit()`/`rollback()`. Самый низкоуровневый вариант, нужен редко.

**Важный нюанс:** транзакция привязана к реактивному контексту подписки, а не к потоку (в отличие от JDBC, где она в `ThreadLocal`). Поэтому все операции должны идти одной непрерывной цепочкой `Mono`/`Flux` — если выскочить из неё (заблокировать или подписаться отдельно), они выполнятся вне транзакции.

## Q29. Какие метрики и мониторинг для `WebFlux`?

Мониторинг WebFlux строится на тех же `Actuator` и `Micrometer`, что и MVC, плюс специфика реактивного стека:

- **HTTP-метрики** — через `Actuator`: длительность запросов, коды ответов, ошибки, таймауты (по эндпоинтам).
- **Метрики Netty** — `reactor.netty.*`: количество активных соединений, состояние пула, занятость event loop. Полезны, чтобы поймать насыщение сети раньше, чем оно ударит по latency.
- **Кастомные метрики** — через `MeterRegistry`: счётчики и таймеры на критичных участках цепочки.
- **Логирование потока** — оператор `log()` в цепочке печатает все реактивные сигналы (`onNext`, `onError`, `request`), что помогает отлаживать backpressure и порядок событий.
- **Распределённая трассировка** — `Micrometer Tracing` (бывший `Sleuth`): `traceId` прокидывается через `Reactor Context`, поэтому сквозная трассировка работает несмотря на смену потоков.

**Почему трассировка тут отдельная тема:** в реактивном стеке нет `ThreadLocal`-сцепки запроса с потоком, поэтому корреляцию логов и спанов ведут именно через Reactor Context (Q26), а не через MDC по потоку.

## Q30. Какие best practices для реактивных приложений?

Главное правило одно: **никогда не блокировать поток event loop** — всё остальное вытекает из него.

- **Не блокировать в цепочке.** Любой блокирующий вызов (`JDBC`, синхронный SDK, `.block()`) на потоке Netty замораживает обслуживание сотен запросов. Если убрать нельзя — выносите на отдельный пул через `subscribeOn(Schedulers.boundedElastic())`.
- **Ловить блокировки автоматически — `BlockHound`.** Java-агент от команды Reactor: инструментирует JVM и бросает ошибку при любом блокирующем вызове (`Thread.sleep`, JDBC, синхронный I/O) на неблокирующих потоках. Включают в тестах и dev-профиле — так скрытые блокировки всплывают до прода, а не под нагрузкой.
- **Не «глотать» подписку.** Пустой `subscribe()` без обработки ошибок прячет сбои; в контроллере цепочку должен подписать сам Spring (вернуть `Mono`/`Flux`), а не вы вручную.
- **Всегда обрабатывать ошибки** — `onErrorResume`/`onErrorReturn` для восстановления, `doOnError` для логирования. Необработанная ошибка терминально гасит поток.
- **Учитывать backpressure** — для быстрых источников и медленных потребителей (Q14), иначе риск переполнения памяти.
- **Не плодить лишние подписки** — повторный `subscribe()` на cold-источник = повторное выполнение (повторный запрос к БД/сети).
- **Тестировать через `StepVerifier`** — обычные ассерты не дождутся асинхронного результата.
- **Документировать контракт потока** — cold или hot, одноразовый ли он; от этого зависит, можно ли его переиспользовать.

## Q31. (!) Как использовать `RouterFunction` для функционального стиля маршрутизации?

`RouterFunction` — альтернатива аннотационной модели. Маршруты и обработчики определяются явно в коде, что упрощает тестирование и композицию.

```java
// Handler — обработчик запросов (аналог методов контроллера)
@Component
public class UserHandler {
    private final UserService userService;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.findAll(), UserDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userService.findById(id)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CreateUserRequest.class)
            .flatMap(userService::create)
            .flatMap(user -> ServerResponse
                .created(URI.create("/api/users/" + user.getId()))
                .bodyValue(user));
    }
}

// Router — определяет маршруты
@Configuration
public class UserRouter {
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route()
            .path("/api/users", builder -> builder
                .GET("", handler::getAll)
                .GET("/{id}", handler::getById)
                .POST("", handler::create)
            )
            .build();
    }
}

// Несколько router'ов объединяются автоматически через Spring-контекст.
// Можно комбинировать вручную:
@Bean
public RouterFunction<ServerResponse> allRoutes(UserHandler u, OrderHandler o) {
    return RouterFunctions.route()
        .path("/api/users", b -> b.GET("", u::getAll).POST("", u::create))
        .path("/api/orders", b -> b.GET("", o::getAll))
        .filter((req, next) -> {
            log.info(">>> {} {}", req.method(), req.path());
            return next.handle(req);
        })
        .build();
}
```

**Тестирование `RouterFunction` без HTTP-сервера:**
```java
@Test
void shouldReturnUsers() {
    RouterFunction<ServerResponse> router = new UserRouter().userRoutes(handler);

    WebTestClient client = WebTestClient.bindToRouterFunction(router).build();

    client.get().uri("/api/users")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UserDto.class).hasSize(3);
}
```

## Q32. (!) Что такое `WebFilter` и как он работает?

`WebFilter` — реактивный аналог `javax.servlet.Filter`. Встраивается в цепочку обработки до `DispatcherHandler` и работает с `ServerWebExchange`.

```java
// Фильтр для логирования запросов
@Component
@Order(1)  // порядок применения
public class RequestLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
            .doOnSuccess(v -> {
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("{} {} → {} [{}ms]",
                    req.getMethod(), req.getPath(),
                    exchange.getResponse().getStatusCode(), elapsed);
            })
            .doOnError(ex -> log.error("{} {} → ERROR: {}",
                req.getMethod(), req.getPath(), ex.getMessage()));
    }
}

// Фильтр аутентификации по токену
@Component
@Order(2)
public class TokenAuthFilter implements WebFilter {

    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return tokenService.validate(token.substring(7))
            .flatMap(claims -> {
                // Передаём данные через Reactor Context
                return chain.filter(exchange)
                    .contextWrite(Context.of("userId", claims.getSubject()));
            })
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }
}
```

**Отличие от `HandlerFilterFunction`** (для `RouterFunction`):
```java
// HandlerFilterFunction — только для функционального стиля, более типобезопасен
RouterFunction<ServerResponse> routes = RouterFunctions.route()
    .GET("/api/users", handler::getAll)
    .filter((request, next) -> {
        // работает только внутри этого router'а
        return next.handle(request);
    })
    .build();
```

## Q33. Как реализовать `WebSocket` в `Spring WebFlux`?

В отличие от `SSE` (Q18, поток только от сервера), `WebSocket` — полнодуплексный канал. В WebFlux он реализуется через `WebSocketHandler`: внутри одного метода `handle(session)` описываются оба направления — приём (`session.receive()`) и отправка (`session.send(...)`), оба как реактивные потоки. Для рассылки сообщения нескольким подписчикам используют `Sinks.Many` (мост из императивного кода в реактивный поток).

```java
// Handler для WebSocket-сессии
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    // Разделяем входящий поток между подписчиками
    private final Sinks.Many<String> chatSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Получение сообщений от клиента и публикация в sink
        Mono<Void> input = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(msg -> chatSink.tryEmitNext(session.getId() + ": " + msg))
            .doOnError(ex -> log.error("WS error: {}", ex.getMessage()))
            .then();

        // Отправка сообщений всем подписчикам
        Flux<WebSocketMessage> output = chatSink.asFlux()
            .map(session::textMessage);

        return session.send(output).and(input);
    }
}

// Регистрация маршрута WebSocket
@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping webSocketMapping(ChatWebSocketHandler handler) {
        Map<String, WebSocketHandler> map = Map.of("/ws/chat", handler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1); // перед DispatcherHandler
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
```

**Клиент (WebTestClient не поддерживает WS, нужен реактивный клиент):**
```java
WebSocketClient client = new ReactorNettyWebSocketClient();
URI uri = URI.create("ws://localhost:8080/ws/chat");

client.execute(uri, session ->
    session.send(Mono.just(session.textMessage("Hello")))
        .thenMany(session.receive().take(5))
        .map(WebSocketMessage::getPayloadAsText)
        .doOnNext(System.out::println)
        .then()
).block(Duration.ofSeconds(10));
```

## Q34. (!) Как настроить реактивный `WebClient` с retry и таймаутами?

Надёжный `WebClient` собирают из двух уровней настроек: **транспортных таймаутов** (на уровне `Reactor Netty` — connect/read/write/response) и **логики устойчивости** в цепочке вызова (`timeout`, `retryWhen` с backoff, маппинг статусов, fallback). Ниже — `@Bean` с настроенным коннектором и сервис, который применяет ретраи только к временным ошибкам.

```java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient paymentWebClient() {
        // Настройка Reactor Netty соединения
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .responseTimeout(Duration.ofSeconds(10))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10))
                .addHandlerLast(new WriteTimeoutHandler(10)));

        return WebClient.builder()
            .baseUrl("https://payment-api.example.com")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-Client-Id", "cheatsheet-app")
            .filter(ExchangeFilterFunctions.basicAuthentication("user", "pass"))
            .filter(logRequest())
            .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (req, next) -> {
            log.info("WebClient → {} {}", req.method(), req.url());
            return next.exchange(req);
        };
    }
}

// Использование с retry и fallback
@Service
public class PaymentClient {
    private final WebClient webClient;

    public Mono<PaymentResult> charge(ChargeRequest request) {
        return webClient.post()
            .uri("/api/charges")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response ->
                response.bodyToMono(ApiError.class)
                    .flatMap(err -> Mono.error(new PaymentException(err.message()))))
            .onStatus(HttpStatusCode::is5xxServerError, response ->
                Mono.error(new ServiceUnavailableException("Payment service error")))
            .bodyToMono(PaymentResult.class)
            .timeout(Duration.ofSeconds(8))
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof ServiceUnavailableException)
                .maxBackoff(Duration.ofSeconds(5))
                .jitter(0.5)
                .doBeforeRetry(rs -> log.warn("Retry #{}: {}", rs.totalRetries(), rs.failure().getMessage())))
            .onErrorReturn(ServiceUnavailableException.class,
                PaymentResult.failed("service_unavailable"));
    }
}
```

## Q35. Как управлять `WebSession` в реактивном стеке?

`WebSession` — реактивный аналог `HttpSession`. Управляется через `WebSessionManager` (по умолчанию — in-memory с `DefaultWebSessionManager`).

```java
@RestController
@RequestMapping("/api/session")
public class SessionController {

    // Получение сессии через параметр метода
    @GetMapping("/data")
    public Mono<Map<String, Object>> getSessionData(WebSession session) {
        return Mono.just(session.getAttributes());
    }

    // Сохранение данных в сессии
    @PostMapping("/cart/add")
    public Mono<ResponseEntity<List<CartItem>>> addToCart(WebSession session,
                                          @RequestBody CartItem item) {
        List<CartItem> cart = session.getAttributeOrDefault("cart", new ArrayList<>());
        cart.add(item);
        session.getAttributes().put("cart", cart);

        return Mono.just(ResponseEntity.ok(cart));
    }

    // Сессия через ServerWebExchange
    @GetMapping("/info")
    public Mono<String> sessionInfo(ServerWebExchange exchange) {
        return exchange.getSession()
            .map(ws -> "Session ID: " + ws.getId() +
                       ", Created: " + ws.getCreationTime() +
                       ", Attrs: " + ws.getAttributes().size());
    }

    // Инвалидация сессии (logout)
    @PostMapping("/logout")
    public Mono<Void> logout(WebSession session) {
        return session.invalidate();
    }
}
```

**Не путать типы ответов двух стилей:** в аннотированном `@RestController` возвращают `Mono<T>`/`Flux<T>` или `ResponseEntity`, когда нужно управлять статусом и заголовками. `ServerResponse` — тип из **функционального стиля** (`RouterFunction` + `HandlerFunction`, Q31); внутри аннотированного контроллера он не работает — фреймворк не знает, как его сериализовать в ответ.

**Настройка Redis-хранилища** для сессий (`spring-session-data-redis`):
```yaml
spring:
  session:
    store-type: redis
    timeout: 30m
  data:
    redis:
      host: localhost
      port: 6379
```

```java
@Configuration
@EnableRedisWebSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {}
```

## Q36. (!) Как интегрировать `R2DBC` с `Spring Data` и получить полностью реактивный стек?

**R2DBC (Reactive Relational Database Connectivity)** — реактивный драйвер для реляционных БД.

**Зависимости (PostgreSQL):**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
implementation 'org.postgresql:r2dbc-postgresql'
```

**Конфигурация:**
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
    username: user
    password: secret
    pool:
      max-size: 10
      initial-size: 5
```

**Entity и репозиторий:**
```java
// Entity — без JPA-аннотаций, только Spring Data
@Table("users")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    @Column("created_at")
    private LocalDateTime createdAt;
}

// Реактивный репозиторий
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Flux<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE created_at > :since ORDER BY created_at DESC")
    Flux<User> findRecentUsers(LocalDateTime since);

    @Modifying
    @Query("UPDATE users SET email = :email WHERE id = :id")
    Mono<Integer> updateEmail(Long id, String email);
}

// Сервис с транзакциями
@Service
@Transactional  // реактивный TransactionManager (R2dbc)
public class UserService {
    private final UserRepository userRepo;
    private final AuditRepository auditRepo;

    public Mono<User> createUser(CreateUserRequest req) {
        User user = new User(null, req.name(), req.email(), LocalDateTime.now());
        return userRepo.save(user)
            .flatMap(saved ->
                auditRepo.save(new AuditLog("user_created", saved.getId()))
                    .thenReturn(saved)
            );
        // Если auditRepo.save() бросит ошибку — транзакция откатится
    }
}
```

**Ключевые отличия от JPA:**
- Нет `LazyLoading` — нет `Hibernate Session`, нет `N+1` по умолчанию
- Связи (`@OneToMany`) — только вручную через JOIN или отдельные запросы
- Нет DDL auto-create — используется `Flyway` / `Liquibase` (или `spring.r2dbc.initialization-mode`)
- Аннотация `@Transactional` работает через реактивный `ReactiveTransactionManager`

## Q37. Какие операторы `Project Reactor` важнее всего знать для собеседования?

Минимальный набор, который спрашивают чаще всего, удобно держать в голове по группам:

- **Преобразование:** `map` (синхронно, 1:1), `flatMap` (асинхронно, порядок может нарушиться), `concatMap` (асинхронно, но с сохранением порядка).
- **Фильтрация и ограничение:** `filter`, `take`, `skip`.
- **Агрегация:** `reduce`, `collectList`, `buffer`, `groupBy`.
- **Комбинирование:** `zip` (попарно), `merge` (по готовности), `concat` (последовательно).
- **Пустота и ошибки:** `switchIfEmpty`, `defaultIfEmpty`, `onErrorResume`, `doOnError`.
- **Прочее:** `cache` (мемоизация результата), `doOnNext`/`doOnComplete` (побочные эффекты без вмешательства в поток).

**Главное, что любят проверять:** разницу `map` vs `flatMap` (синхронное vs асинхронное преобразование) и `flatMap` vs `concatMap` (нарушает порядок vs сохраняет). Примеры по каждому оператору:

```java
// map — синхронное преобразование каждого элемента
Flux.just("alice", "bob")
    .map(String::toUpperCase)
    // → "ALICE", "BOB"

// flatMap — асинхронное преобразование (может изменять порядок!)
Flux.just(1L, 2L, 3L)
    .flatMap(id -> userRepo.findById(id)) // параллельно, порядок может нарушиться
    .subscribe(System.out::println);

// concatMap — как flatMap, но сохраняет порядок (последовательно)
Flux.just(1L, 2L, 3L)
    .concatMap(id -> userRepo.findById(id)) // последовательно, порядок сохраняется

// filter — фильтрация
Flux.range(1, 10)
    .filter(n -> n % 2 == 0)  // → 2, 4, 6, 8, 10

// reduce — агрегация в Mono
Flux.range(1, 5)
    .reduce(0, Integer::sum)   // → Mono.just(15)

// collectList — Flux → Mono<List>
Flux.just("a", "b", "c")
    .collectList()  // → Mono.just(["a","b","c"])

// zip — объединение по позиции
Mono<String> name = Mono.just("Alice");
Mono<Integer> age = Mono.just(30);
Mono.zip(name, age, (n, a) -> n + " is " + a)  // → "Alice is 30"

// merge — параллельно, без сохранения порядка
Flux.merge(flux1, flux2, flux3)

// concat — последовательно, с сохранением порядка
Flux.concat(flux1, flux2, flux3)

// switchIfEmpty — fallback при пустом publisher
userRepo.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException("User " + id)))

// defaultIfEmpty — значение по умолчанию
userRepo.findById(id)
    .defaultIfEmpty(User.anonymous())

// doOnNext / doOnError / doOnComplete — side effects без изменения потока
flux
    .doOnNext(item -> log.info("Processing: {}", item))
    .doOnError(ex -> metrics.incrementError())
    .doOnComplete(() -> log.info("Done"))

// onErrorResume — восстановление при ошибке
userRepo.findById(id)
    .onErrorResume(DatabaseException.class, ex -> Mono.just(User.fallback()))

// cache — подписка вычисляется один раз, результат переиспользуется
Mono<Config> config = configService.load().cache(Duration.ofMinutes(5));

// take / skip — ограничение и пропуск
Flux.range(1, 100).skip(10).take(5)  // → 11, 12, 13, 14, 15

// buffer — группировка элементов
Flux.range(1, 10).buffer(3)  // → [1,2,3], [4,5,6], [7,8,9], [10]

// groupBy — разделение на подпотоки по ключу
Flux.just("a1", "b1", "a2", "b2")
    .groupBy(s -> s.charAt(0))
    .flatMap(group -> group.collectList()
        .map(list -> group.key() + ": " + list))
    // → "a: [a1, a2]", "b: [b1, b2]"
```

---

## Q38. Как WebFlux работает совместно с Virtual Threads (Java 21)?

**Virtual Threads** (Project Loom) и **WebFlux** решают одну проблему — масштабируемость при I/O-нагрузке, но разными способами. Важно понимать, когда их комбинирование имеет смысл, а когда нет.

**Ключевое различие:**

| | WebFlux (Reactor) | Virtual Threads (Loom) |
|--|--|--|
| Модель | Неблокирующий event-loop | Блокирующий код на лёгких потоках |
| Стек | Реактивный (`Mono`/`Flux`) | Привычный императивный |
| Стек вызовов | Плохо читаемый (операторы) | Обычный, понятный stacktrace |
| Подходит для | Streaming, SSE, WebSocket | CRUD с блокирующим I/O |

**Когда комбинировать WebFlux + Virtual Threads имеет смысл:**

```java
// Перенос блокирующей операции с event loop на отдельный пул
Mono<String> result = Mono.fromCallable(() -> blockingLegacyService.call())
    .subscribeOn(Schedulers.boundedElastic()); // по умолчанию — пул платформенных потоков
```

```yaml
# Spring Boot 3.2+ / Spring 6.1+: включение virtual threads (по умолчанию выключено)
spring:
  threads:
    virtual:
      enabled: true
```

**Важный нюанс, который любят на собеседованиях:** `Schedulers.boundedElastic()` по умолчанию работает на **платформенных потоках** — никакой «автоматической дружбы» с virtual threads у него нет. VT-исполнение появилось позже (Reactor 3.6+) и включается **явно**: системным свойством `reactor.schedulers.defaultBoundedElasticOnVirtualThreads=true` (тогда `boundedElastic()` отдаёт scheduler на виртуальных потоках, по одному на задачу) или созданием собственного VT-исполнителя (`Thread.ofVirtual()`, в Spring — `VirtualThreadTaskExecutor`). Флаг `spring.threads.virtual.enabled=true` переводит на виртуальные потоки инфраструктурные исполнители Spring Boot, но сам по себе не делает Reactor-шедулеры виртуальными.

**Практические сценарии:**
- **WebFlux + Virtual Threads**: если часть операций блокирующая (legacy JDBC, синхронные SDK) — перенос на `Schedulers.boundedElastic()`; при явно включённом VT-режиме эти блокирующие задачи лягут на виртуальные потоки
- **Spring MVC + Virtual Threads** (Spring Boot 3.2+): для простых REST API более читаемая альтернатива WebFlux
- **WebFlux без Virtual Threads**: для чисто реактивного стека (R2DBC, WebClient) — виртуальные потоки не нужны

**Главный вывод:** WebFlux + Virtual Threads — не обязательная комбинация. Если весь стек реактивный, достаточно WebFlux. Если есть блокирующие зависимости — Virtual Threads помогают избежать `publishOn(Schedulers.boundedElastic())`.

---

## Q39. Как интегрировать Resilience4j с WebFlux?

`Resilience4j` поддерживает реактивный стек через модуль `resilience4j-reactor`. Операторы-трансформеры оборачивают `Mono`/`Flux` в `CircuitBreaker`, `RateLimiter`, `Retry`, `Bulkhead`.

**Подключение:**
```kotlin
implementation("io.github.resilience4j:resilience4j-reactor:2.x.x")
implementation("io.github.resilience4j:resilience4j-spring-boot3:2.x.x")
```

**Circuit Breaker с Mono:**
```java
CircuitBreaker cb = CircuitBreakerRegistry.ofDefaults().circuitBreaker("userService");

Mono<User> result = userWebClient.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class)
    .transformDeferred(CircuitBreakerOperator.of(cb))    // оборачиваем в circuit breaker
    .onErrorResume(CallNotPermittedException.class,      // CB открыт — fallback
        ex -> Mono.just(User.fallback()))
    .onErrorResume(ex -> Mono.error(new ServiceUnavailableException()));
```

**Retry с Flux:**
```java
Retry retry = RetryRegistry.ofDefaults().retry("dataStream");

Flux<Event> events = eventService.streamEvents()
    .transformDeferred(RetryOperator.of(retry));
```

**Rate Limiter:**
```java
RateLimiter rateLimiter = RateLimiterRegistry.ofDefaults().rateLimiter("api");

Mono<Response> response = apiClient.call()
    .transformDeferred(RateLimiterOperator.of(rateLimiter));
```

**Через аннотации (Spring Boot Starter):**
```java
@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
public Mono<User> getUser(Long id) {
    return webClient.get().uri("/users/" + id).retrieve().bodyToMono(User.class);
}

public Mono<User> fallback(Long id, CallNotPermittedException ex) {
    return Mono.just(User.anonymous());
}
```

**Ключевой принцип:** `transformDeferred` применяется лениво — circuit breaker проверяется при каждой подписке, что корректно для реактивного холодного publisher.

---

## Q40. Как Spring Security работает в реактивном стеке?

В WebFlux-приложениях Spring Security использует **реактивный стек безопасности** — `ReactiveSecurityContextHolder` вместо `SecurityContextHolder`, и `SecurityWebFilterChain` вместо `SecurityFilterChain`.

**Ключевые компоненты:**

| Servlet (MVC) | Reactive (WebFlux) |
|--|--|
| `SecurityContextHolder` | `ReactiveSecurityContextHolder` |
| `SecurityFilterChain` | `SecurityWebFilterChain` |
| `UserDetailsService` | `ReactiveUserDetailsService` |
| `AuthenticationManager` | `ReactiveAuthenticationManager` |

**Настройка SecurityWebFilterChain:**
```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(ex -> ex
                .pathMatchers("/public/**").permitAll()
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }
}
```

**Извлечение SecurityContext в реактивной цепочке:**
```java
public Mono<String> getCurrentUsername() {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> ctx.getAuthentication().getName());
}

// Передача контекста через Reactor Context автоматически
@GetMapping("/profile")
public Mono<UserProfile> getProfile() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(auth -> (JwtAuthenticationToken) auth)
        .flatMap(token -> userService.findById(token.getName()));
}
```

**Важный нюанс:** Reactor Context используется как аналог ThreadLocal. Spring Security автоматически помещает `SecurityContext` в Reactor Context при каждом запросе через `ReactorContextWebFilter`.

**Кастомный ReactiveUserDetailsService:**
```java
@Bean
public ReactiveUserDetailsService userDetailsService(UserRepository repo) {
    return username -> repo.findByUsername(username)
        .map(user -> User.withUsername(user.username())
            .password(user.passwordHash())
            .roles(user.roles().toArray(String[]::new))
            .build());
}
```

---

## Q41. Чем R2DBC отличается от JDBC и как использовать R2dbcRepository?

**R2DBC** (Reactive Relational Database Connectivity) — стандарт реактивного доступа к реляционным БД. В отличие от JDBC (блокирующий), R2DBC возвращает `Publisher` (`Mono`/`Flux`).

**Сравнение:**

| | JDBC | R2DBC |
|--|--|--|
| I/O модель | Блокирующий | Неблокирующий |
| Spring интеграция | Spring Data JPA | Spring Data R2DBC |
| Поддержка lazy loading | Через Hibernate | Нет (нет N+1 стратегии) |
| Поддержка join fetch | HQL/JPQL | Ручные запросы или `@Query` |
| Транзакции | `@Transactional` (синхронный) | `@Transactional` (реактивный) |

**Подключение (Spring Boot):**
```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
implementation("io.r2dbc:r2dbc-postgresql")  // или другой драйвер
```

```yaml
# application.yml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
    username: user
    password: pass
```

**Сущность и репозиторий:**
```java
@Table("users")
public record User(@Id Long id, String username, String email) {}

public interface UserRepository extends R2dbcRepository<User, Long> {

    Flux<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE username ILIKE :pattern")
    Flux<User> searchByUsername(String pattern);
}
```

**Использование в сервисе:**
```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Mono<User> createUser(CreateUserRequest req) {
        return userRepository.save(new User(null, req.username(), req.email()));
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }
}
```

**Ограничения R2DBC относительно JPA:**
- Нет lazy loading — связи нужно загружать явно
- Нет `@OneToMany`, `@ManyToOne` — joins через `@Query` или `DatabaseClient`
- Нет кеша первого/второго уровня

**Когда выбирать R2DBC:** когда весь стек реактивный (WebFlux + R2DBC + WebClient) и нужна максимальная масштабируемость без блокировок.

---

## Q42. WebClient vs RestTemplate vs RestClient — когда что выбирать?

В Spring 6.1 появился **RestClient** — синхронный fluent API, похожий на WebClient по стилю, но без реактивности.

**Сравнение трёх клиентов:**

| | RestTemplate | WebClient | RestClient |
|--|--|--|--|
| API-стиль | Template methods | Fluent, реактивный | Fluent, синхронный |
| Модель I/O | Синхронный, блокирующий | Асинхронный, неблокирующий | Синхронный, блокирующий |
| Status Spring | Maintenance mode | Активный | Активный (Spring 6.1+) |
| Возвращаемый тип | `T` | `Mono<T>` / `Flux<T>` | `T` |
| Стек | Spring MVC / любой | WebFlux / любой | Spring MVC / любой |
| Тест-поддержка | `MockRestServiceServer` | `MockWebServer` | `MockRestServiceServer` |

**RestTemplate (legacy):**
```java
RestTemplate restTemplate = new RestTemplate();
User user = restTemplate.getForObject("/users/{id}", User.class, 1L);
// Не используйте в новых проектах — deprecated в пользу RestClient
```

**WebClient (реактивный стек):**
```java
WebClient client = WebClient.builder().baseUrl("http://api.example.com").build();

Mono<User> user = client.get()
    .uri("/users/{id}", 1L)
    .retrieve()
    .bodyToMono(User.class);
```

**RestClient (Spring 6.1+, синхронный):**
```java
RestClient restClient = RestClient.builder()
    .baseUrl("http://api.example.com")
    .build();

User user = restClient.get()
    .uri("/users/{id}", 1L)
    .retrieve()
    .body(User.class);
```

**Рекомендации по выбору:**
- **WebFlux-приложение** → `WebClient`
- **Spring MVC, новый проект** → `RestClient` (Spring 6.1+)
- **Spring MVC, legacy** → мигрируйте с `RestTemplate` на `RestClient`
- **Streaming / SSE** → только `WebClient` (умеет `Flux<T>`)

---

## Q43. Как тестировать WebFlux-приложения с WebTestClient и StepVerifier?

Для интеграционного тестирования WebFlux используют **WebTestClient**, для unit-тестирования реактивных цепочек — **StepVerifier**.

**WebTestClient — интеграционные тесты:**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnUser() {
        webTestClient.get()
            .uri("/users/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(User.class)
            .consumeWith(result -> {
                User user = result.getResponseBody();
                assertThat(user).isNotNull();
                assertThat(user.id()).isEqualTo(1L);
            });
    }

    @Test
    void shouldReturnListOfUsers() {
        webTestClient.get()
            .uri("/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .hasSize(3);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRequireAdminRole() {
        webTestClient.delete()
            .uri("/users/1")
            .exchange()
            .expectStatus().isNoContent();
    }
}
```

**WebTestClient без SpringBootTest (mock server):**
```java
@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void shouldGetUser() {
        given(userService.findById(1L)).willReturn(Mono.just(new User(1L, "alice", "alice@example.com")));

        webTestClient.get().uri("/users/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.username").isEqualTo("alice");
    }
}
```

**StepVerifier — unit-тесты реактивных цепочек:**
```java
class UserServiceTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepo);

    @Test
    void shouldFindUser() {
        User expected = new User(1L, "alice", "alice@example.com");
        when(userRepo.findById(1L)).thenReturn(Mono.just(expected));

        StepVerifier.create(userService.findById(1L))
            .expectNext(expected)
            .verifyComplete();
    }

    @Test
    void shouldHandleNotFound() {
        when(userRepo.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(userService.findById(99L))
            .expectError(UserNotFoundException.class)
            .verify();
    }

    @Test
    void shouldStreamEvents() {
        Flux<Event> events = Flux.just(
            new Event("e1"), new Event("e2"), new Event("e3")
        );
        when(eventRepo.findAll()).thenReturn(events);

        StepVerifier.create(userService.streamEvents())
            .expectNext(new Event("e1"))
            .expectNext(new Event("e2"))
            .expectNext(new Event("e3"))
            .verifyComplete();
    }

    @Test
    void shouldTestWithVirtualTime() {
        // Тест с виртуальным временем для delay/interval
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(1)))
            .thenAwait(Duration.ofHours(1))
            .expectNextCount(1)
            .verifyComplete();
    }
}
```

**Ключевые методы StepVerifier:**
- `expectNext(T)` — проверить следующий элемент
- `expectNextCount(n)` — проверить количество элементов
- `expectError(Class)` — ожидать ошибку определённого типа
- `verifyComplete()` — проверить завершение и запустить
- `withVirtualTime()` — тест с виртуальным временем (для `delay`, `interval`)

---

## See also

- [Spring Framework](spring-framework-interview.md) — IoC-контейнер, на котором стоит WebFlux
- [Spring MVC](spring-mvc-interview.md) — классический синхронный стек, альтернатива WebFlux
- [Spring Boot](spring-boot-interview.md) — автоконфигурация реактивного стека
- [Spring Security](spring-security-interview.md) — реактивная безопасность (SecurityWebFilterChain)
- [Spring Data JPA](spring-data-jpa-interview.md) — сравнение с R2DBC в реактивном стеке
- [Spring Cloud](spring-cloud-interview.md) — Gateway на WebFlux под капотом
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — мониторинг реактивных приложений
- [Spring Batch](spring-batch-interview.md) — пакетная обработка vs. реактивные потоки
- [Микросервисы](../../architecture/microservices-interview.md) — реактивные паттерны в распределённых системах
- [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md) — потоки и модели конкурентности

- [Spring AOP](spring-aop-interview.md) — аспектно-ориентированное программирование
- [Шпаргалка: Spring WebFlux для Java](../../../frameworks/java-frameworks/spring/spring-webflux.md) — теория
