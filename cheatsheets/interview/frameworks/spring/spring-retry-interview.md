---
title: "Вопросы на собеседовании: Spring Retry"
description: "Spring Retry для автоматического повтора операций: @Retryable, @Recover, RetryTemplate, backoff-стратегии, интеграция с транзакциями и Reactor"
tags:
  - interview
  - spring
  - spring-retry-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Retry"
  - "Spring Retry interview"
  - "Spring Retry собеседование"
prerequisites:
  - "[[spring-retry]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring Retry`

`Spring Retry` — библиотека автоматического повтора неудавшихся операций. Предоставляет аннотационный (`@Retryable`/`@Recover`) и программный (`RetryTemplate`) API. Тесно связана с `Spring Batch` и `Spring Integration`, где используется по умолчанию.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Retry GitHub](https://github.com/spring-projects/spring-retry) — официальный репозиторий с документацией
- [Baeldung: Spring Retry](https://www.baeldung.com/spring-retry) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и аннотации**
- [Q1. (!) Что такое Spring Retry и для каких задач он предназначен?](#q1-что-такое-spring-retry-и-для-каких-задач-он-предназначен)
- [Q2. Как подключить Spring Retry?](#q2-как-подключить-spring-retry)
- [Q3. (!) Как работает @Retryable?](#q3-как-работает-retryable)
- [Q4. (!) Что такое @Recover и каковы требования к сигнатуре?](#q4-что-такое-recover-и-каковы-требования-к-сигнатуре)
- [Q5. Какие backoff-стратегии поддерживает Spring Retry?](#q5-какие-backoff-стратегии-поддерживает-spring-retry)

**RetryTemplate и политики**
- [Q6. Что такое RetryTemplate и когда его использовать?](#q6-что-такое-retrytemplate-и-когда-его-использовать)
- [Q7. Какие RetryPolicy реализации есть в Spring Retry?](#q7-какие-retrypolicy-реализации-есть-в-spring-retry)
- [Q8. Что такое CircuitBreakerRetryPolicy?](#q8-что-такое-circuitbreakerretrypolicy)

**Ограничения и интеграции**
- [Q9. (!) Почему @Retryable не работает при self-invocation?](#q9-почему-retryable-не-работает-при-self-invocation)
- [Q10. (!) Как правильно комбинировать @Retryable и @Transactional?](#q10-как-правильно-комбинировать-retryable-и-transactional)
- [Q11. Как Spring Retry интегрируется с Reactor/WebClient?](#q11-как-spring-retry-интегрируется-с-reactorwebclient)
- [Q12. Что такое Stateful Retry и когда он нужен?](#q12-что-такое-stateful-retry-и-когда-он-нужен)

**Мониторинг и тестирование**
- [Q13. Как добавить метрики и логирование через RetryListener?](#q13-как-добавить-метрики-и-логирование-через-retrylistener)
- [Q14. Как настроить разные retry-политики для разных методов?](#q14-как-настроить-разные-retry-политики-для-разных-методов)
- [Q15. Что произойдёт, если @Recover выбросит исключение?](#q15-что-произойдёт-если-recover-выбросит-исключение)
- [Q16. Как протестировать логику retry в тестах?](#q16-как-протестировать-логику-retry-в-тестах)
- [Q17. Чем Spring Retry отличается от Resilience4j?](#q17-чем-spring-retry-отличается-от-resilience4j)

## Q1. (!) Что такое Spring Retry и для каких задач он предназначен?

`Spring Retry` — библиотека для автоматического повтора операций при сбоях. Применяется для:
- HTTP-обращений к нестабильным внешним API
- Сетевых вызовов с временными ошибками (таймауты, 5xx)
- Операций с БД при дедлоках или `OptimisticLockException`
- Чтения из очередей (Kafka, JMS)

**Основной принцип:** при получении заданного исключения метод вызывается повторно заданное число раз с настраиваемой паузой между попытками.

**Итог:** Spring Retry = простой retry. Для Circuit Breaker, Bulkhead и Rate Limiter — используй [Resilience4j](resilience4j-interview.md).


> [!mcq]
>
> **Вопрос:** Что такое Spring Retry и для каких типичных задач он применяется?
>
> ---
>
> #### A) Библиотека Spring, реализующая автоматический повтор метода при возникновении заданных исключений с настраиваемым числом попыток и backoff-стратегией; применяется для transient-ошибок (HTTP 5xx, таймауты, deadlock, OptimisticLockException, чтение из брокеров) — ✓ Верно
>
> **Развёрнутое объяснение:** `Spring Retry` решает узкую, но критичную задачу — повтор операций, у которых ошибки **временные (transient)** и могут разрешиться сами через короткую паузу. Конфигурация задаётся либо декларативно (`@Retryable` + `@Recover`), либо программно (`RetryTemplate`). Библиотека сама по себе **не реализует** Circuit Breaker, Bulkhead и Rate Limiter в полноценном смысле (хотя `CircuitBreakerRetryPolicy` есть как упрощённая обёртка). Для всех этих паттернов в проде используют `Resilience4j`. Изначально `Spring Retry` был выделен из `Spring Batch`, где повтор шагов — стандартная семантика.
>
> **Пример:**
> ```java
> @Service
> public class PaymentClient {
>     @Retryable(
>         retryFor = { IOException.class, TimeoutException.class },
>         maxAttempts = 4,
>         backoff = @Backoff(delay = 500, multiplier = 2.0, maxDelay = 5000)
>     )
>     public PaymentResponse charge(PaymentRequest req) {
>         return restClient.post()
>             .uri("/charge")
>             .body(req)
>             .retrieve()
>             .body(PaymentResponse.class);
>     }
>
>     @Recover
>     public PaymentResponse fallback(IOException e, PaymentRequest req) {
>         return PaymentResponse.deferred(req.id());
>     }
> }
> ```
>
> **Когда применять:** интеграции с внешними API (банковские шлюзы, ML-сервисы), запись в БД при гонках на оптимистических локах, потребление из Kafka/JMS с восстановимыми ошибками сериализации, межсервисные RPC.
>
> **Подводные камни:** retry на **non-idempotent** операциях без идемпотентного ключа приводит к двойным платежам; retry на любую `Exception` маскирует бизнес-ошибки валидации; долгий backoff при синхронных HTTP-вызовах съедает thread pool сервера.
>
> ---
>
> #### B) Реактивный планировщик задач с гарантией at-least-once delivery, заменяющий Spring Scheduler и Quartz — ❌ Неверно
>
> **Что на самом деле:** `Spring Retry` не планирует и не запускает задачи самостоятельно — он только **оборачивает** уже происходящий вызов и повторяет его. Планирование задач — это `@Scheduled`, `TaskScheduler`, Quartz или ShedLock для распределённого случая. At-least-once delivery — характеристика брокеров (Kafka, RabbitMQ), а не retry-библиотеки.
>
> **Откуда путаница:** оба механизма связаны с «попыткой через время», но retry повторяет уже инициированный вызов в том же потоке (или его callable-обёртку), а scheduler инициирует новые независимые запуски по расписанию.
>
> **Если бы это было правдой:** `@Retryable` имел бы поля `cron`, `fixedRate` и работал бы при старте приложения без вызова метода — но он срабатывает только когда метод физически вызвали и тот выбросил исключение.
>
> ---
>
> #### C) Полная реализация паттерна Circuit Breaker с состояниями CLOSED/OPEN/HALF_OPEN, метриками per-instance и распределённым координатором — ❌ Неверно
>
> **Что на самом деле:** в Spring Retry есть только `CircuitBreakerRetryPolicy` — упрощённая локальная (per-JVM) обёртка, открывающаяся после N подряд неудач и автоматически закрывающаяся через таймаут. Метрик, реактивных стримов состояний и распределённого координатора нет. Полноценный Circuit Breaker — это `Resilience4j` или `Hystrix` (deprecated): с micrometer-метриками, event publishers, конфигурацией через YAML и интеграцией с Spring Boot Actuator.
>
> **Откуда путаница:** название класса `CircuitBreakerRetryPolicy` создаёт впечатление, что это «тот самый» Circuit Breaker, но это retry-policy, реализующая базовое размыкание.
>
> **Если бы это было правдой:** у `Spring Retry` был бы `@CircuitBreaker` как у Resilience4j с раздельной конфигурацией thresholds, slidingWindow, slow-call-rate — но всех этих фич нет.
>
> ---
>
> #### D) Spring-абстракция поверх повторных HTTP-запросов в RestTemplate/WebClient, не поддерживающая произвольные методы и Kafka/JMS-операции — ❌ Неверно
>
> **Что на самом деле:** Spring Retry **не привязан** к HTTP. `@Retryable` может оборачивать **любой** Spring-bean метод — JDBC-запросы, обращения к Redis, парсинг JSON, бизнес-логику. Транспортный слой ему безразличен: библиотека работает через AOP-прокси и проверяет тип исключения.
>
> **Откуда путаница:** в туториалах retry чаще всего показывают на HTTP, потому что это самый частый источник transient-ошибок. Отсюда впечатление, что инструмент только для HTTP.
>
> **Если бы это было правдой:** аннотация `@Retryable` существовала бы только на `RestTemplate.exchange()` или была бы тесно связана с `WebClient.retrieve()` — но на самом деле она работает на любом методе любого бина.
>
> ---
>
> **Связанные вопросы:** [[Q2]] — подключение зависимости и `@EnableRetry`; [[Q17]] — отличия от Resilience4j

## Q2. Как подключить Spring Retry?

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<!-- AOP обязателен для аннотационного подхода -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

Аннотационный подход требует `@EnableRetry`:
```java
@Configuration
@EnableRetry
public class AppConfig {}
```

В Spring Boot `spring-retry` включается автоматически при наличии зависимости. `@EnableRetry` всё равно нужен явно.


> [!mcq]
>
> **Вопрос:** Какой минимально-достаточный набор шагов нужен, чтобы аннотация `@Retryable` начала работать в Spring Boot-приложении?
>
> ---
>
> #### A) Достаточно положить в classpath артефакт `spring-retry` — стартер сам зарегистрирует все бины и активирует AOP — ❌ Неверно
>
> **Что на самом деле:** `spring-retry` подтягивается транзитивно (например, из `spring-boot-starter-batch` или вручную), но **автоконфигурации `RetryAutoConfiguration` нет** в Spring Boot. Без аннотации `@EnableRetry` `RetryConfiguration` не подключится, AOP-аспект `AnnotationAwareRetryOperationsInterceptor` не зарегистрируется, и `@Retryable` будет просто игнорироваться — вызов пройдёт один раз без повтора.
>
> **Откуда путаница:** многие Spring-стартеры действительно «zero-config» (Actuator, DataSource, Web), и кажется, что Retry устроен так же.
>
> **Если бы это было правдой:** тест с `@SpringBootTest` без `@EnableRetry` показывал бы N попыток в логах — но на практике метод выполнится ровно один раз.
>
> ---
>
> #### B) Добавить зависимость `org.springframework.retry:spring-retry` (плюс AOP-runtime через `spring-aspects` или `spring-boot-starter-aop`) и пометить любой `@Configuration`-класс аннотацией `@EnableRetry` — ✓ Верно
>
> **Развёрнутое объяснение:** `@EnableRetry` импортирует `RetryConfiguration` (`@Configuration`), которая регистрирует `AnnotationAwareRetryOperationsInterceptor` и `RetryConfiguration`-pointcut. Без AOP-runtime прокси создать нельзя — поэтому либо нужен `spring-aspects`, либо `spring-boot-starter-aop` (он сам тянет `aspectjweaver`). По умолчанию используется **JDK-proxy** (на интерфейсах); чтобы прокси работал на классе без интерфейсов, указывают `@EnableRetry(proxyTargetClass = true)` — тогда CGLIB сгенерирует подкласс.
>
> **Пример:**
> ```xml
> <dependency>
>     <groupId>org.springframework.retry</groupId>
>     <artifactId>spring-retry</artifactId>
> </dependency>
> <dependency>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-starter-aop</artifactId>
> </dependency>
> ```
> ```java
> @SpringBootApplication
> @EnableRetry
> public class App {
>     public static void main(String[] args) { SpringApplication.run(App.class, args); }
> }
> ```
>
> **Когда применять:** всегда, когда в проекте используется хотя бы один `@Retryable` или `@Recover`. На главном `@SpringBootApplication`-классе аннотация особенно удобна — она «видна» сразу и не теряется в config-пакете.
>
> **Подводные камни:** если бин с `@Retryable` создаётся не Spring-контейнером (`new MyService()` в тесте или фабрике), прокси не сработает и retry не будет; `@EnableRetry` нужно ставить на конфигурацию **того же контекста**, что и бин с `@Retryable` (актуально для многоконтекстных приложений).
>
> ---
>
> #### C) Достаточно зависимости `spring-retry` плюс `@EnableRetry` — отдельный AOP-runtime не нужен, потому что Spring Core содержит весь необходимый proxy-механизм — ❌ Неверно
>
> **Что на самом деле:** Spring Core содержит **ProxyFactory** и базовый AOP API, но `@Retryable`-аспект требует именно AspectJ-аннотации (`@Aspect` на `AnnotationAwareRetryOperationsInterceptor`-конфигурации) и `aspectjweaver` для парсинга pointcut-expressions. Без `spring-aspects`/`spring-boot-starter-aop` в classpath не будет `org.aspectj.lang.annotation.Aspect` и инициализация `RetryConfiguration` упадёт с `ClassNotFoundException`.
>
> **Откуда путаница:** в spring-tx/security транзакции и security-аннотации часто работают «и без явного starter-aop», потому что эти модули сами тянут нужные AOP-зависимости. У `spring-retry` такой транзитивности нет.
>
> **Если бы это было правдой:** проект с одной только `spring-retry`-зависимостью успешно стартовал бы — но на практике без `aspectjweaver` падает на этапе контекста.
>
> ---
>
> #### D) Нужно создать кастомный `RetryTemplate`-бин и зарегистрировать его как `@Primary`, иначе аннотация `@Retryable` будет работать только с дефолтной политикой `NeverRetryPolicy` — ❌ Неверно
>
> **Что на самом деле:** `@Retryable` работает «из коробки» с дефолтным `RetryTemplate`, который собирается интерсептором из параметров аннотации (`maxAttempts`, `backoff`, `retryFor`). Кастомный `RetryTemplate`-бин **не требуется** для аннотационного API — он нужен только если вы вызываете retry **программно** (`retryTemplate.execute(callback)`). `NeverRetryPolicy` — это специальная политика, которая используется только если вы её явно укажете.
>
> **Откуда путаница:** в документации часто рядом показывают и аннотационный, и программный API — кажется, что `RetryTemplate`-бин обязателен в обоих случаях.
>
> **Если бы это было правдой:** все туториалы начинались бы с `@Bean RetryTemplate retryTemplate()`, но это контрфакт — стандартный путь начинается с `@EnableRetry` + `@Retryable`.
>
> ---
>
> **Связанные вопросы:** [[Q1]] — что такое Spring Retry; [[Q3]] — параметры `@Retryable`; [[Q9]] — self-invocation и proxy-механика

## Q3. (!) Как работает @Retryable?

```java
@Retryable(
    retryFor = {IOException.class, TimeoutException.class},
    noRetryFor = {ValidationException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
)
public PaymentResult processPayment(PaymentRequest req) {
    return gateway.charge(req);
}
```

**Параметры:**
- `retryFor` — исключения для retry (бывший `value`)
- `noRetryFor` — исключения без retry (бывший `exclude`)
- `maxAttempts` — максимум попыток (включая первую, default: 3)
- `backoff` — задержка между попытками

**Что происходит при исчерпании попыток:** выбрасывается последнее исключение (или вызывается `@Recover`).


> [!mcq]
>
> **Вопрос:** Как именно работает `@Retryable` и что означают его ключевые параметры (`retryFor`, `noRetryFor`, `maxAttempts`, `backoff`)?
>
> ---
>
> #### A) Прокси выполняет вызов в фоновом потоке и асинхронно повторяет его при ошибке; основной поток сразу получает `Future`, а `maxAttempts` ограничивает число параллельных попыток — ❌ Неверно
>
> **Что на самом deле:** `@Retryable` работает **синхронно** в том же потоке, в котором был сделан вызов. Прокси внутри `invoke()` запускает цикл попыток; между попытками поток **блокируется** на `Thread.sleep` (или эквиваленте у `BackOffPolicy`). Никаких `Future` и фоновых потоков нет. Если нужен асинхронный retry — это комбинируется с `@Async` (метод объявляется как `@Async @Retryable`), либо используется реактивный API (`Reactor#retryWhen`).
>
> **Откуда путаница:** другие resilience-библиотеки (Resilience4j с `RetryRegistry` + reactor-операторы) показывают reactive-сценарии, и кажется, что любой retry асинхронен.
>
> **Если бы это было правдой:** возвращаемый тип `@Retryable`-метода всегда должен был бы быть `Future` или `CompletableFuture` — но в реальности он возвращает обычный `PaymentResult`, и вызывающий поток ждёт все попытки.
>
> ---
>
> #### B) Аннотация перезагружает Spring-контекст при каждом сбое, повторно создавая бин — поэтому `maxAttempts` ограничивает число пересозданий контекста — ❌ Неверно
>
> **Что на самом деле:** Spring-контекст создаётся один раз при старте и не пересоздаётся. `@Retryable` только повторяет **вызов метода** на уже существующем экземпляре. `maxAttempts` — это количество попыток выполнить метод (включая первую: `maxAttempts = 3` означает 1 первичный + 2 повтора).
>
> **Откуда путаница:** Spring Boot DevTools перезапускает контекст при изменениях кода, и некоторые путают это с retry-механизмом.
>
> **Если бы это было правдой:** retry стоил бы сотни миллисекунд на холодный старт бинов и был бы непригоден для частых ретраев — на практике повтор стоит ~микросекунд плюс backoff-delay.
>
> ---
>
> #### C) AOP-прокси перехватывает вызов; на исключении из списка `retryFor` интерсептор повторяет метод до `maxAttempts` с паузой согласно `@Backoff`; исключения из `noRetryFor` не приводят к повтору, а после исчерпания попыток выбрасывается последнее исключение или вызывается `@Recover` — ✓ Верно
>
> **Развёрнутое объяснение:** При первом вызове `@Retryable`-метода прокси (`RetryOperationsInterceptor`) делегирует выполнение `RetryTemplate`. Тот формирует `RetryContext` (счётчик попыток, последнее исключение), вызывает callback и ловит исключение. Если тип входит в `retryFor` (бывший `value`/`include`) и не входит в `noRetryFor` (бывший `exclude`) — ждёт backoff и повторяет. Когда `maxAttempts` исчерпан, ищется подходящий `@Recover`-метод; если такого нет — последнее пойманное исключение пробрасывается наружу. `@Backoff(delay, multiplier, maxDelay, random)` контролирует паузу: `delay` — стартовое значение, `multiplier > 1` включает экспоненциальный рост, `maxDelay` — потолок, `random = true` добавляет джиттер ±50%.
>
> **Пример:**
> ```java
> @Retryable(
>     retryFor = { IOException.class, TimeoutException.class },
>     noRetryFor = { ValidationException.class, AuthenticationException.class },
>     maxAttempts = 5,
>     backoff = @Backoff(delay = 200, multiplier = 2.0, maxDelay = 5000, random = true)
> )
> public OrderResponse submitOrder(Order order) throws IOException {
>     return externalClient.submit(order);
> }
> ```
> Поведение: попытка №1 → fail (IOException) → sleep ~200ms ± джиттер → №2 → fail → ~400ms → №3 → ~800ms → №4 → ~1600ms → №5 → если всё ещё fail → `@Recover` или проброс. Если же выпала `ValidationException` — повтора нет, исключение летит наружу немедленно.
>
> **Когда применять:** `retryFor` — узко перечислять transient-ошибки; `noRetryFor` — добавлять бизнес-исключения, которые случайно являются подтипами retryable (например, `IllegalArgumentException` extends `RuntimeException`); `maxAttempts` — 3–5 для синхронных HTTP; `backoff` с `multiplier=2.0` и `random=true` — стандарт против thundering herd.
>
> **Подводные камни:** `retryFor = Exception.class` ловит вообще всё, включая `NullPointerException` — это маскирует баги; забыли `noRetryFor` для бизнес-валидации — пользователь ждёт N×backoff на каждую опечатку; `maxAttempts = 1` — это «без повторов вообще» (только одна попытка), для интуитивно «один повтор» надо ставить `2`.
>
> ---
>
> #### D) `@Retryable` запоминает состояние неудачных вызовов в распределённом кэше (по умолчанию Redis) и повторяет их даже после рестарта приложения — ❌ Неверно
>
> **Что на самом деле:** стандартный `@Retryable` — **stateless** для разных вызовов: каждый вызов независим, retry-контекст живёт только в текущем потоке и теряется при рестарте JVM. `Stateful Retry` (`@Retryable(stateful = true)`) сохраняет ключ операции, но **в локальной памяти** `RetryContextCache` — не в Redis. Если нужна персистентность через рестарт, делают outbox-pattern + scheduler, а не Spring Retry.
>
> **Откуда путаница:** в Spring экосистеме много примеров с Redis (`@Cacheable`, Spring Session), и кажется, что retry устроен так же.
>
> **Если бы это было правдой:** у `@Retryable` была бы конфигурация `cacheName`, `keyGenerator`, `cacheManager` — но этих параметров нет.
>
> ---
>
> **Связанные вопросы:** [[Q2]] — подключение и `@EnableRetry`; [[Q5]] — backoff-стратегии и `@Backoff`; [[Q9]] — почему self-invocation ломает retry

## Q4. (!) Что такое @Recover и каковы требования к сигнатуре?

`@Recover` — метод-fallback, вызывается **после исчерпания всех попыток**.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    log.error("Payment failed after retries for {}", req.getId(), e);
    return PaymentResult.failed("Service unavailable");
}
```

**Требования к сигнатуре:**
- Тот же класс, что и `@Retryable`
- **Первый аргумент** — тип пойманного исключения (или суперкласс)
- **Тот же возвращаемый тип**, что и у `@Retryable`
- Остальные аргументы — те же, что у `@Retryable` метода

**Выбор @Recover:** Spring выбирает наиболее специфичный метод по типу исключения — работает как overloaded handlers.

```java
@Recover
public PaymentResult recoverIOException(IOException e, PaymentRequest req) {
    return PaymentResult.retry();          // специфичный
}

@Recover
public PaymentResult recoverGeneral(Exception e, PaymentRequest req) {
    return PaymentResult.failed("error"); // общий fallback
}
```


> [!mcq]
>
> **Вопрос:** Что такое `@Recover` в Spring Retry и какие требования предъявляются к сигнатуре fallback-метода?
>
> ---
>
> #### A) `@Recover` — это метод, который вызывается **вместо** `@Retryable` при первом же исключении и заменяет основной retry-механизм: если он есть в классе, повторов не будет, а сразу отработает fallback — ❌ Неверно
>
> **Что на самом деле:** `@Recover` вызывается **только после исчерпания** `maxAttempts` (или когда исключение не подходит под `retryFor`/`noRetryFor`). Сначала прокси прогоняет цикл попыток через `RetryTemplate`, и лишь когда счётчик попыток исчерпан, ищется подходящий `@Recover`-метод. Если бы `@Recover` отрабатывал сразу — retry был бы полностью бессмысленным.
>
> **Откуда путаница:** в circuit-breaker-паттернах (Hystrix `fallbackMethod`, Resilience4j `@CircuitBreaker(fallbackMethod=...)`) fallback действительно может срабатывать «вместо» при открытой цепи. Spring Retry устроен иначе.
>
> **Если бы это было правдой:** `@Retryable(maxAttempts = 5)` рядом с `@Recover` всегда показывал бы в логах ровно одну попытку — но на практике видно 5 попыток, и только потом fallback.
>
> ---
>
> #### B) `@Recover`-метод может быть в **любом** Spring-бине и подбирается глобально по типу исключения через `BeanFactory`; имя класса значения не имеет — ❌ Неверно
>
> **Что на самом деле:** `@Recover` ищется **в том же классе**, где объявлен `@Retryable`-метод. `RecoverAnnotationRecoveryHandler` сканирует методы того же таргета и подбирает наиболее специфичный по типу первого аргумента (исключение) и совпадению остальных параметров. Метод `@Recover` в другом бине Spring Retry не найдёт.
>
> **Откуда путаница:** `@ControllerAdvice` + `@ExceptionHandler` действительно работают глобально по контексту — кажется, что `@Recover` устроен аналогично.
>
> **Если бы это было правдой:** можно было бы вынести один «GlobalRecoverHandler» на весь проект — но любая попытка так сделать кончается `ExhaustedRetryException`, потому что обработчик не найден в локальном классе.
>
> ---
>
> #### C) `@Recover`-метод должен иметь **другой** возвращаемый тип, чем `@Retryable` — обычно `void` или `Optional`, чтобы вызывающий код мог отличить «успех» от «fallback» — ❌ Неверно
>
> **Что на самом деле:** `@Recover` должен возвращать **тот же тип** (или совместимый — подтип), что и `@Retryable`-метод. Иначе прокси не сможет вернуть значение вызывающему коду — сигнатура AOP-перехвата требует, чтобы fallback подходил под точку вызова. Различать «успех» и «fallback» правильно через само значение (флаг, `Result.failed(...)`) или через метрики, а не через тип возврата.
>
> **Откуда путаница:** в реактивных стеках fallback иногда возвращает `Mono.empty()` вместо `Mono<T>` — кажется, что «другой тип» допустим.
>
> **Если бы это было правдой:** компилятор Java не позволил бы — но Spring Retry проверяет совместимость в рантайме и кидает понятное `IllegalStateException` при несовпадении.
>
> ---
>
> #### D) `@Recover` должен быть **в том же классе**, что и `@Retryable`-метод; **первый аргумент** — тип пойманного исключения (или его суперкласс), **возвращаемый тип совпадает** с retryable-методом, **остальные аргументы** соответствуют параметрам исходного метода; Spring выбирает наиболее специфичный по типу исключения как overloaded-handler — ✓ Верно
>
> **Развёрнутое объяснение:** Когда `RetryTemplate` исчерпал `maxAttempts`, `RecoverAnnotationRecoveryHandler` сканирует методы того же таргета, отмеченные `@Recover`. Алгоритм выбора: (1) первый параметр должен быть `Throwable` или его подтип, совместимый с пойманным исключением; (2) остальные параметры должны совпадать (по типу и порядку) с параметрами `@Retryable`-метода (можно пропустить хвост — Spring подставит); (3) возвращаемый тип совместим. Если кандидатов несколько, выбирается **самый специфичный по типу исключения** (`IOException` побеждает `Exception` для `IOException`). При отсутствии подходящего `@Recover` пробрасывается оригинальное исключение или `ExhaustedRetryException` (для stateful retry).
>
> **Пример:**
> ```java
> @Service
> public class PaymentService {
>     @Retryable(retryFor = Exception.class, maxAttempts = 3)
>     public PaymentResult charge(PaymentRequest req) throws Exception {
>         return gateway.charge(req);
>     }
>
>     @Recover
>     public PaymentResult recoverIO(IOException e, PaymentRequest req) {
>         return PaymentResult.retry("network error: " + e.getMessage());
>     }
>
>     @Recover
>     public PaymentResult recoverGeneral(Exception e, PaymentRequest req) {
>         return PaymentResult.failed("unrecoverable: " + e.getClass().getSimpleName());
>     }
> }
> ```
> При `IOException` после исчерпания попыток вызовется `recoverIO` (более специфичный), при `IllegalStateException` — `recoverGeneral` (общий).
>
> **Когда применять:** всегда, когда поток вызывающего кода должен получить **осмысленный fallback-результат** вместо пробрасывания исключения — это шаблон «graceful degradation» (логирование, метрики, отдача кэша, deferred-обработка через очередь).
>
> **Подводные камни:** (1) забыли указать `@Recover` для конкретного исключения — летит оригинальный exception, и retry выглядит «сломанным»; (2) сигнатура аргументов не совпадает (например, в `@Retryable` параметр `Order order`, а в `@Recover` — `String orderId`) — Spring не подберёт метод; (3) первый аргумент должен быть **исключением**, а не бизнес-объектом — частая ошибка; (4) `@Recover` сам в self-invocation **не работает** по той же причине, что и `@Retryable` (нужен прокси); (5) если у нескольких `@Recover` одинаковая «дистанция» по типу исключения, поведение неопределённое — лучше держать иерархию однозначной.
>
> **Связанные вопросы:** [[Q3]] — параметры `@Retryable` и поведение при исчерпании попыток; [[Q9]] — self-invocation ломает и `@Retryable`, и `@Recover`; [[Q13]] — `RetryListener` как альтернатива `@Recover` для observability

## Q5. Какие backoff-стратегии поддерживает Spring Retry?

```java
// Фиксированная задержка (1 сек)
@Backoff(delay = 1000)

// Экспоненциальная: 1s → 2s → 4s
@Backoff(delay = 1000, multiplier = 2.0)

// Экспоненциальная с потолком: 1s → 2s → 4s → 10s → 10s
@Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)

// С джиттером (предотвращает thundering herd)
@Backoff(delay = 1000, multiplier = 2.0, random = true)
```

**Thundering Herd:** если все клиенты синхронно ретраят после сбоя, они создают волну нагрузки. Джиттер разбрасывает попытки по времени.

**Программный BackOffPolicy:**
```java
ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
backOff.setInitialInterval(1000);
backOff.setMultiplier(2.0);
backOff.setMaxInterval(10000);
```


> [!mcq]
>
> **Вопрос:** Какие backoff-стратегии поддерживает Spring Retry и как они настраиваются через `@Backoff`/`BackOffPolicy`?
>
> ---
>
> #### A) Spring Retry поддерживает **`FixedBackOffPolicy`** (постоянная задержка), **`ExponentialBackOffPolicy`** (геометрический рост `delay × multiplier`, ограничивается `maxDelay`), **`ExponentialRandomBackOffPolicy`** (то же + ±50% джиттер для борьбы с thundering herd) и **`UniformRandomBackOffPolicy`** (равномерный рандом в диапазоне `[min, max]`); все они конфигурируются через аннотацию `@Backoff(delay, multiplier, maxDelay, random)` или через программный `RetryTemplateBuilder` — ✓ Верно
>
> **Развёрнутое объяснение:** `BackOffPolicy` — это контракт Spring Retry для расчёта паузы между попытками. Аннотация `@Backoff` автоматически выбирает реализацию по параметрам: если `multiplier == 0` — `FixedBackOffPolicy(delay)`; если `multiplier > 1` и `random = false` — `ExponentialBackOffPolicy` с `initialInterval=delay`, `multiplier`, `maxInterval=maxDelay`; если `random = true` — `ExponentialRandomBackOffPolicy` (тот же экспоненциальный рост, но каждый интервал умножается на случайный множитель в диапазоне ~`[0.5, 1.5]`); если задан `delayExpression` или `maxDelayExpression` — SpEL-вычисление в рантайме. Программно эти политики собираются явно: `new ExponentialBackOffPolicy()` + сеттеры, либо `RetryTemplate.builder().exponentialBackoff(initial, multiplier, max)` / `.fixedBackoff(ms)` / `.uniformRandomBackoff(min, max)`.
>
> **Пример:**
> ```java
> // 1) Фиксированная задержка 500 ms
> @Retryable(retryFor = IOException.class, backoff = @Backoff(delay = 500))
> public void fixed() { ... }
>
> // 2) Экспоненциальная: 1s → 2s → 4s → 8s (потолок 10s)
> @Retryable(backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000))
> public void exponential() { ... }
>
> // 3) Экспоненциальная + джиттер ±50% (анти-thundering-herd)
> @Retryable(backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000, random = true))
> public void exponentialJitter() { ... }
>
> // 4) Программно — равномерный рандом в диапазоне [200, 800] ms
> RetryTemplate template = RetryTemplate.builder()
>     .maxAttempts(5)
>     .uniformRandomBackoff(200, 800)
>     .build();
> ```
>
> **Когда применять:** **Fixed** — для локальных операций с предсказуемой transient-ошибкой (быстрый кэш, локальная очередь); **Exponential** — для удалённых HTTP/DB-сервисов под нагрузкой, чтобы дать им время на восстановление; **ExponentialRandom (jitter)** — стандарт де-факто для распределённых клиентов одного даунстрима (тысячи pod'ов одновременно ретраят — джиттер размазывает пик); **UniformRandom** — для scheduling-задач, где важна однородная плотность запросов.
>
> **Подводные камни:** (1) забыли `maxDelay` — экспонента уходит в минуты, retry «зависает» (`1s → 2s → 4s → ... → 1024s`); (2) `random = true` без `multiplier` — джиттер применяется к фиксированной паузе и теряет смысл; (3) `delay = 0` отключает паузу полностью — это нагрузочная атака на даунстрим; (4) backoff отрабатывает через `Thread.sleep`, блокируя поток — на больших ретраях это съедает thread pool (для реактивных стеков нужен `Retry.backoff()` из Reactor, см. [[Q11]]); (5) при stateful retry backoff между попытками **не** удерживается — пауза зависит от частоты переотправки сообщения брокером.
>
> **Связанные вопросы:** [[Q3]] — параметры `@Backoff` в составе `@Retryable`; [[Q6]] — программная конфигурация через `RetryTemplate`; [[Q11]] — backoff для реактивных потоков
>
> ---
>
> #### B) Spring Retry поддерживает только **`FixedBackOffPolicy`** — для всех остальных стратегий (экспонента, jitter) нужно подключать отдельную библиотеку Resilience4j, потому что в spring-retry экспоненциальный backoff не реализован — ❌ Неверно
>
> **Что на самом деле:** `ExponentialBackOffPolicy`, `ExponentialRandomBackOffPolicy`, `UniformRandomBackOffPolicy` — это **штатные классы** пакета `org.springframework.retry.backoff` начиная с самой первой версии. Resilience4j вообще не нужен — это альтернативный стек, а не дополнение.
>
> **Откуда путаница:** Resilience4j активно продвигается в Spring Cloud как «современный» retry — кажется, что spring-retry — это «старый и минимальный» вариант. На самом деле возможности по backoff у обеих библиотек сопоставимы.
>
> **Если бы это было правдой:** `@Backoff(multiplier = 2.0)` не компилировался бы или игнорировался — но в реальности параметр читается и применяется.
>
> ---
>
> #### C) Backoff в Spring Retry реализован **неблокирующе** через `ScheduledExecutorService`: пауза не занимает поток вызывающего, ретраи планируются и выполняются в отдельном thread pool, поэтому подходят и для high-throughput сервисов — ❌ Неверно
>
> **Что на самом деле:** стандартный `FixedBackOffPolicy`/`ExponentialBackOffPolicy` использует **`Thread.sleep`** (через `ThreadWaitSleeper` по умолчанию). Это **блокирует** вызывающий поток на всё время backoff. Для высоконагруженных сервисов это критично: при `maxAttempts = 5` и экспоненте до 10s один тред может быть занят ~20s. Чтобы получить неблокирующее поведение, нужны реактивные API (`Mono.retryWhen` / Reactor `Retry.backoff()`) или явная асинхронная архитектура.
>
> **Откуда путаница:** Spring `@Async` и `@Scheduled` создают впечатление, что Spring всегда «асинхронен» — но `@Retryable` живёт в синхронной AOP-обвязке.
>
> **Если бы это было правдой:** не было бы рекомендации «не комбинируйте `@Retryable` с длинным backoff на синхронных HTTP-эндпоинтах» — но она существует именно из-за блокировки потока.
>
> ---
>
> #### D) `@Backoff(random = true)` означает **полностью случайную** задержку без всякой связи с `delay` и `multiplier` — это режим «chaos engineering», где Spring сам выбирает интервал из равномерного распределения `[0, Long.MAX_VALUE]` — ❌ Неверно
>
> **Что на самом деле:** `random = true` в `@Backoff` включает `ExponentialRandomBackOffPolicy`, которая **сохраняет** базовый экспоненциальный рост, но домножает каждый интервал на случайный коэффициент в диапазоне примерно `[0.5, 1.5]` (классический jitter). Это нужно для anti-thundering-herd, а не для chaos engineering. Параметры `delay`, `multiplier`, `maxDelay` продолжают работать как обычно.
>
> **Откуда путаница:** Netflix Chaos Monkey, jitter в академических статьях о distributed systems — иногда смешиваются понятия «случайный backoff» и «полностью случайные пуши».
>
> **Если бы это было правдой:** retry с `random = true` мог бы дать паузу в 10 лет — это сделало бы фичу непригодной для production.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — `@Backoff` в составе `@Retryable`; [[Q6]] — `RetryTemplate.builder()` с backoff; [[Q11]] — Reactor `Retry.backoff()` как неблокирующая альтернатива

## Q6. Что такое RetryTemplate и когда его использовать?

`RetryTemplate` — программный API для retry без аннотаций.

**Используй когда:**
- Нужен retry не в Spring bean (utility-метод, лямбда)
- Retry-политика должна конфигурироваться динамически
- Нужен явный recover-callback прямо в коде
- Нужен `stateful retry` (Kafka, JMS)

```java
@Bean
public RetryTemplate retryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(IOException.class)
        .withListener(new CustomRetryListener())
        .build();
}

// Использование
retryTemplate.execute(
    ctx -> externalApi.call(data),      // операция
    ctx -> fallbackResult(data)         // recover callback
);
```


> [!mcq]
>
> **Вопрос:** Что такое `RetryTemplate`, чем он отличается от аннотации `@Retryable` и когда программный API уместнее декларативного?
>
> ---
>
> #### A) `RetryTemplate` — это специальный планировщик задач (`@Scheduled`) внутри Spring Retry: он запускает повторы по cron-выражению и не требует AOP-прокси, потому что работает через `TaskScheduler` — ❌ Неверно
>
> **Что на самом деле:** `RetryTemplate` — это **синхронный программный API** в виде шаблонного метода (`execute(RetryCallback, RecoveryCallback)`). Никакого `@Scheduled` или `TaskScheduler` он не использует. Это императивная альтернатива `@Retryable`-аннотации: вы сами вызываете `template.execute(...)` в нужной точке кода. Backoff между попытками реализован через `Thread.sleep`, а не через планировщик.
>
> **Откуда путаница:** название `*Template` напоминает `JdbcTemplate`, `RestTemplate` — но семантика у `RetryTemplate` ближе к императивному «обернуть-блок-в-retry», а не к scheduling.
>
> **Если бы это было правдой:** конфигурировался бы cron-expression, был бы `@Bean ScheduledExecutorService` — но в API `RetryTemplate` ничего такого нет.
>
> ---
>
> #### B) `RetryTemplate` — это **программный (императивный) API** Spring Retry для повторов **без аннотаций** и **без AOP-прокси**: вы создаёте экземпляр (обычно через `RetryTemplate.builder()` с `maxAttempts`, `backoff`, `retryOn`, `withListener`) и оборачиваете нужный участок кода в `template.execute(callback, recoveryCallback)`; используется когда нужен retry вне Spring-бина, динамическая конфигурация политики, явный `RecoveryCallback` или stateful-retry (Kafka/JMS) — ✓ Верно
>
> **Развёрнутое объяснение:** `RetryTemplate` — это thread-safe компонент, инкапсулирующий `RetryPolicy` (решает, продолжать ли), `BackOffPolicy` (как долго ждать), список `RetryListener` (хуки на open/close/error). Метод `execute(RetryCallback<T,E> callback)` запускает цикл: вызывает `callback.doWithRetry(context)`, ловит исключение, спрашивает у `RetryPolicy#canRetry(context)`, если да — `BackOffPolicy#backOff()` и повтор. Перегрузка `execute(callback, recoveryCallback)` принимает `RecoveryCallback<T>` — он будет вызван, когда попытки исчерпаны (программный аналог `@Recover`). Без AOP-прокси значит: работает в `static`-методе, лямбде, утилитном классе, юнит-тесте без Spring-контекста.
>
> **Пример:**
> ```java
> @Bean
> public RetryTemplate paymentRetryTemplate(MeterRegistry meters) {
>     return RetryTemplate.builder()
>         .maxAttempts(5)
>         .exponentialBackoff(200, 2.0, 5000)         // 200ms → 400 → 800 → 1.6s → 3.2s
>         .retryOn(IOException.class)
>         .traversingCauses()                          // учитывать вложенные causes
>         .withListener(new RetryListenerSupport() {
>             @Override public <T, E extends Throwable> void onError(
>                     RetryContext ctx, RetryCallback<T,E> cb, Throwable ex) {
>                 meters.counter("payment.retry.attempts").increment();
>             }
>         })
>         .build();
> }
>
> // Использование (программная композиция, динамический recovery)
> PaymentResult result = paymentRetryTemplate.execute(
>     ctx -> gateway.charge(req),                      // основная операция
>     ctx -> PaymentResult.deferred(req.getId())       // recovery после исчерпания
> );
> ```
>
> **Когда применять:** (1) код вне Spring-контейнера (утилитный класс, batch job, тест без `@SpringBootTest`); (2) **динамическая** retry-политика, выбираемая в рантайме (`policy = isCritical ? new SimpleRetryPolicy(10) : new SimpleRetryPolicy(2)`); (3) нужен **`RecoveryCallback`** на конкретный вызов, а не общий `@Recover` для всего класса; (4) **stateful retry** (`RetryState`) для Kafka/JMS listeners — аннотацией это не выразить; (5) self-invocation внутри одного бина, где `@Retryable` не сработает из-за proxy bypass; (6) комбинирование с другими паттернами (CircuitBreaker, ExceptionClassifier) — программная сборка `RetryPolicy` гибче.
>
> **Подводные камни:** (1) `RetryTemplate` — synchronous, `Thread.sleep` блокирует поток (см. [[Q5]]); (2) забыли указать `retryOn(...)` — `SimpleRetryPolicy` по умолчанию ретраит **все** `Exception`, включая `NullPointerException` — маскирует баги; (3) если шарите один `RetryTemplate` между методами с разной политикой — состояние policy общее, легко получить непредсказуемое поведение; (4) `RecoveryCallback` вызывается **только при исчерпании** попыток, не при первой ошибке (как и `@Recover`); (5) при stateful-retry обязателен `RetryState` — без него поведение деградирует до stateless.
>
> **Связанные вопросы:** [[Q2]] — `@EnableRetry` для аннотационного пути (для `RetryTemplate` не нужен); [[Q3]] — `@Retryable` как декларативная альтернатива; [[Q5]] — backoff-стратегии и их API; [[Q12]] — stateful retry через `RetryState`; [[Q13]] — `RetryListener` и метрики
>
> ---
>
> #### C) `RetryTemplate` — это **обязательный бин**, без которого `@Retryable`-аннотация не работает: интерсептор ищет в контексте `@Bean RetryTemplate retryTemplate()` и кидает `NoSuchBeanDefinitionException`, если его нет — ❌ Неверно
>
> **Что на самом деле:** `@Retryable` работает «из коробки» — `RetryConfiguration` (импортируемая через `@EnableRetry`) сама создаёт `AnnotationAwareRetryOperationsInterceptor`, который собирает `RetryTemplate` **по параметрам аннотации** на лету (`maxAttempts`, `backoff`, `retryFor`). Кастомный `RetryTemplate`-бин нужен только если вы вызываете retry **программно** (`retryTemplate.execute(...)`) — это полностью независимый путь.
>
> **Откуда путаница:** в документации часто рядом показывают и аннотационный, и программный API, и кажется, что они «связаны через общий бин».
>
> **Если бы это было правдой:** все туториалы по `@Retryable` начинались бы со снипета `@Bean RetryTemplate` — но они начинаются с `@EnableRetry` + аннотации на методе.
>
> ---
>
> #### D) `RetryTemplate` нужен только для асинхронного retry в реактивном коде (`Mono`/`Flux`); для синхронного блокирующего кода используется только `@Retryable`-аннотация — ❌ Неверно
>
> **Что на самом деле:** ровно наоборот — `RetryTemplate` **синхронный и блокирующий**. Для реактивного кода используют `Retry.backoff(...)` из Reactor (`Mono.retryWhen(...)`) или Resilience4j-reactor — `RetryTemplate` там не работает, потому что `Thread.sleep` блокирует event loop. См. [[Q11]] о реактивных стратегиях.
>
> **Откуда путаница:** «программный API» иногда ассоциируется с «реактивным» — но `RetryTemplate` появился задолго до Reactor и остался императивным.
>
> **Если бы это было правдой:** в API `RetryTemplate` были бы методы вроде `executeMono()` или `executeFlux()` — но их нет, есть только синхронный `execute(callback)`.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — декларативный `@Retryable` как альтернатива; [[Q5]] — backoff-стратегии для `RetryTemplate`; [[Q11]] — реактивный retry через Reactor; [[Q12]] — stateful-retry через `RetryTemplate.setRetryState(...)`

## Q7. Какие RetryPolicy реализации есть в Spring Retry?

| Политика | Описание |
|---|---|
| `SimpleRetryPolicy` | Фиксированное число попыток по типам исключений |
| `AlwaysRetryPolicy` | Повторять бесконечно (осторожно!) |
| `NeverRetryPolicy` | Никогда не повторять (для тестов) |
| `TimeoutRetryPolicy` | Повторять до истечения таймаута |
| `ExceptionClassifierRetryPolicy` | Разные политики для разных исключений |
| `CircuitBreakerRetryPolicy` | Встроенный Circuit Breaker |
| `CompositeRetryPolicy` | Комбинация политик (AND/OR логика) |

```java
// ExceptionClassifierRetryPolicy
ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
policy.setPolicyMap(Map.of(
    IOException.class, new SimpleRetryPolicy(3),
    TimeoutException.class, new SimpleRetryPolicy(5),
    IllegalArgumentException.class, new NeverRetryPolicy()
));
```


> [!mcq] Какая `RetryPolicy` повторяет попытки до истечения общего таймаута, независимо от количества вызовов?
>
> - [ ] **A) `SimpleRetryPolicy`** — ограничивает по числу попыток.
>
>   Эта политика повторяет до `maxAttempts` (по умолчанию 3) и игнорирует время — может занять как 10 мс, так и 10 минут, если backoff велик. Для дедлайнов она не подходит.
>
>   ❌ Использование `SimpleRetryPolicy` там, где нужен общий лимит по времени, приведёт к зависанию вызова дольше SLA.
>
> - [ ] **B) `AlwaysRetryPolicy`** — повторяет бесконечно.
>
>   У `AlwaysRetryPolicy.canRetry()` всегда возвращает `true`, никаких ограничений (ни по времени, ни по числу попыток) нет. В production без `CompositeRetryPolicy` это прямой путь к thread starvation.
>
>   ❌ Использовать её как «retry до таймаута» — миф: она не следит за временем, а просто не останавливается.
>
> - [x] **C) `TimeoutRetryPolicy`** — ограничивает retry общим временем выполнения.
>
>   `TimeoutRetryPolicy` хранит timestamp начала в `RetryContext` и в `canRetry()` сравнивает прошедшее время с `setTimeout(ms)`. Когда таймаут истёк — попытки прекращаются, даже если их было всего две.
>
>   ```java
>   TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
>   policy.setTimeout(5000); // суммарно не дольше 5 секунд
>
>   RetryTemplate template = new RetryTemplate();
>   template.setRetryPolicy(policy);
>   template.setBackOffPolicy(new FixedBackOffPolicy()); // 1 сек между попытками
>
>   String result = template.execute(ctx -> externalApi.call());
>   ```
>
>   Подходит для интеграций со строгим SLA: «не дольше N миллисекунд, сколько бы попыток ни понадобилось».
>
> - [ ] **D) `CircuitBreakerRetryPolicy`** — открывает «цепь» после серии ошибок.
>
>   Эта политика отслеживает количество ошибок за окно времени и временно блокирует вызовы (`circuit open`), но не ограничивает суммарное время одного `RetryTemplate.execute`. Она про защиту downstream-сервиса, а не про дедлайн вызова.
>
>   ❌ Путать circuit breaker с timeout — частая ошибка: первый бережёт зависимость, второй — текущий тред.

## Q8. Что такое CircuitBreakerRetryPolicy?

`CircuitBreakerRetryPolicy` — встроенный Circuit Breaker в Spring Retry (в отличие от полноценного [Resilience4j CB](resilience4j-interview.md)). Подходит для простых случаев.

```java
CircuitBreakerRetryPolicy circuitBreakerPolicy = new CircuitBreakerRetryPolicy(
    new SimpleRetryPolicy(3)
);
circuitBreakerPolicy.setOpenTimeout(5000);    // цепь открыта 5 сек
circuitBreakerPolicy.setResetTimeout(20000);  // сбросить через 20 сек

RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(circuitBreakerPolicy);
```

**Ограничения Spring Retry CB:** нет метрик, нет HALF_OPEN состояния, нет Spring Boot auto-configuration. Для production используй [Resilience4j](resilience4j-interview.md).


> [!mcq] Что делают параметры `openTimeout` и `resetTimeout` в `CircuitBreakerRetryPolicy`?
>
> - [ ] **A) `openTimeout` — таймаут одного HTTP-вызова, `resetTimeout` — общий таймаут retry.**
>
>   Spring Retry не управляет таймаутами вызовов — это работа клиента (`RestTemplate`, `WebClient`, JDBC). `CircuitBreakerRetryPolicy` оперирует своими счётчиками ошибок, а не сетевым временем.
>
>   ❌ Если поставить `openTimeout=2000` в надежде ограничить HTTP — внешний сервис продолжит висеть, пока сокет не отвалится.
>
> - [ ] **B) `openTimeout` — задержка между попытками, `resetTimeout` — backoff multiplier.**
>
>   Это путаница с `BackOffPolicy`. Задержки между попытками задают `FixedBackOffPolicy` / `ExponentialBackOffPolicy`, у `CircuitBreakerRetryPolicy` нет ни `delay`, ни `multiplier`.
>
>   ❌ Конфигурировать backoff через `openTimeout` бесполезно — это поле вообще про другое окно.
>
> - [ ] **C) Оба параметра — синонимы, задают окно мониторинга ошибок.**
>
>   В исходниках `CircuitBreakerRetryPolicy` это два разных поля с разной семантикой: одно — окно сбора ошибок, второе — длительность открытого состояния. Они не взаимозаменяемы.
>
>   ❌ Установить только один из них «потому что они одинаковые» приведёт к тому, что цепь либо не откроется, либо никогда не закроется.
>
> - [x] **D) `openTimeout` — окно подсчёта ошибок до открытия цепи, `resetTimeout` — длительность открытого состояния перед попыткой закрыть.**
>
>   Логика политики: если в течение `openTimeout` мс набралось `maxAttempts` ошибок — цепь открывается и `canRetry()` начинает возвращать `false`. Через `resetTimeout` мс цепь закрывается обратно, и счётчик сбрасывается.
>
>   ```java
>   CircuitBreakerRetryPolicy cb = new CircuitBreakerRetryPolicy(
>       new SimpleRetryPolicy(3)              // 3 ошибки = открытие цепи
>   );
>   cb.setOpenTimeout(5000);                  // считаем ошибки в окне 5 сек
>   cb.setResetTimeout(20000);                // цепь открыта 20 сек, потом закроется
>
>   RetryTemplate template = new RetryTemplate();
>   template.setRetryPolicy(cb);
>   ```
>
>   Важно: эта реализация не имеет состояния `HALF_OPEN` и не публикует метрики. Для production со сложными требованиями к CB используй Resilience4j.

## Q9. (!) Почему @Retryable не работает при self-invocation?

`@Retryable` работает через **Spring AOP proxy**: прокси обёртывает bean снаружи, а вызов `this.method()` обходит прокси и попадает напрямую в реальный объект — retry не срабатывает.

Это та же проблема, что с `@Transactional`. Подробнее в [Spring AOP](spring-aop-interview.md).

```java
@Service
public class PaymentService {

    // Self-invocation — retry НЕ работает
    public void process(Payment p) {
        retryableMethod(p);  // this.retryableMethod() — обходит proxy!
    }

    @Retryable(retryFor = IOException.class)
    public void retryableMethod(Payment p) {
        gateway.charge(p);
    }
}
```

**Решения:**

1. **Вынести в отдельный bean (рекомендуется):**
```java
@Service
public class PaymentRetryService {
    @Retryable(retryFor = IOException.class)
    public void charge(Payment p) { ... }
}
```

2. **Self-inject через `@Lazy`:**
```java
@Service
public class PaymentService {
    @Autowired @Lazy
    private PaymentService self;

    public void process(Payment p) {
        self.retryableMethod(p); // через proxy
    }
}
```

3. **RetryTemplate** — не зависит от AOP.


> [!mcq] Почему вызов `this.retryableMethod()` из другого метода того же бина не запускает retry?
>
> - [x] **A) Spring AOP оборачивает бин в proxy снаружи; `this.*` идёт мимо proxy и не активирует Retry-аспект.**
>
>   `@Retryable` реализован как AOP-аспект, который применяется к proxy-объекту (JDK dynamic proxy для интерфейсов, CGLIB для классов). Контейнер инжектит именно proxy в зависимости. Вызов через ссылку `this` обращается напрямую к полю исходного объекта, минуя проксирующий слой, поэтому интерсептор `RetryOperationsInterceptor` не отрабатывает.
>
>   ```java
>   @Service
>   public class PaymentService {
>
>       public void process(Payment p) {
>           retryableMethod(p);          // this.* — мимо proxy, retry не работает
>       }
>
>       @Retryable(retryFor = IOException.class)
>       public void retryableMethod(Payment p) {
>           gateway.charge(p);
>       }
>   }
>
>   // Решение: вынести в отдельный бин ИЛИ self-inject через @Lazy
>   @Service
>   public class PaymentService {
>       @Autowired @Lazy
>       private PaymentService self;     // proxy-ссылка
>
>       public void process(Payment p) {
>           self.retryableMethod(p);     // через proxy — retry работает
>       }
>   }
>   ```
>
>   Тот же механизм объясняет, почему `@Transactional`, `@Cacheable` и другие Spring AOP-аннотации тоже ломаются на self-invocation.
>
> - [ ] **B) `@Retryable` требует `public static` метода, а обычный instance-метод не подходит.**
>
>   `@Retryable` работает с обычными `public` instance-методами. Требование к `static` относится к совершенно другому случаю (например, `@PostConstruct` ограничения). Аннотация на static-методе вообще не сработает, потому что AOP не проксирует статику.
>
>   ❌ Менять метод на static «чтобы retry заработал» — антипаттерн, который сломает работу аспекта полностью.
>
> - [ ] **C) Контекст retry привязан к thread-local и теряется при внутреннем вызове.**
>
>   `RetryContext` действительно использует `RetrySynchronizationManager` поверх ThreadLocal, но self-invocation выполняется в том же треде — контекст бы не «потерялся». Проблема не в треде, а в том, что аспект вообще не запускается.
>
>   ❌ Попытка «починить» проблему сменой `@Async` или executor-а не поможет — proxy всё равно будет обойдён.
>
> - [ ] **D) Spring Retry не работает в `@Service`-бинах, только в `@Component`.**
>
>   `@Service` — это `@Component` с другим стереотипом, разницы для AOP нет. `@Retryable` работает в обоих, проблема self-invocation не зависит от типа стереотипа.
>
>   ❌ Переименование `@Service` в `@Component` ничего не меняет — корневая причина в обходе proxy.

## Q10. (!) Как правильно комбинировать @Retryable и @Transactional?

Если метод **одновременно** `@Transactional` и `@Retryable`, транзакция откатывается до того, как retry срабатывает — и retry бесполезен.

**Неправильно:**
```java
@Transactional
@Retryable(retryFor = OptimisticLockException.class)
public void updateWithOptimisticLock(Entity e) {
    // OptimisticLockException → rollback → retry запускается
    // → но транзакция уже откатилась и объект в dirty state
    repository.save(e);
}
```

**Правильно — retry снаружи, транзакция внутри:**
```java
@Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
public void updateWithRetry(Entity e) {
    doUpdateTransactional(e);  // каждая попытка = новая транзакция
}

@Transactional
public void doUpdateTransactional(Entity e) {
    repository.save(e);
}
```

**Правило:** `@Retryable` должен быть на **внешнем** методе (без `@Transactional`), а транзакция — во вложенном.


> [!mcq]
> **Вопрос:** Как правильно скомбинировать `@Retryable` и `@Transactional`, чтобы retry действительно повторял операцию в свежей транзакции?
>
> - [x] **A. Поместить `@Retryable` на внешний метод без `@Transactional`, а `@Transactional` — на вложенный метод, который вызывается через Spring-bean (proxy)**
>
>   Каждая попытка стартует **новую транзакцию**: при `OptimisticLockException` происходит rollback, retry-advice ловит исключение и снова вызывает внешний метод → новый `doUpdateTransactional()` → новый `TransactionInterceptor` открывает свежую транзакцию.
>
>   ```java
>   @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
>   public void updateWithRetry(Entity e) {
>       doUpdateTransactional(e);  // ← proxy-вызов в другой bean
>   }
>   @Transactional
>   public void doUpdateTransactional(Entity e) { repository.save(e); }
>   ```
>
>   **Почему правильно:** retry-interceptor находится **снаружи** транзакционного advice, поэтому ловит исключение уже после `commit/rollback` — состояние БД консистентно, JPA-контекст пересоздаётся.
>
>   **Use-case:** оптимистичные блокировки (JPA `@Version`), deadlock retry на PostgreSQL (`40P01`), serialization failures (`40001`).
>
> - [ ] **B. Поставить обе аннотации на один метод — `@Transactional` + `@Retryable` — Spring сам разрулит порядок advice через `@Order`**
>
>   Порядок advice по умолчанию: `@Retryable` стоит **внутри** `@Transactional` (retry-advice имеет более низкий приоритет). При исключении транзакция откатывается **до** того, как retry успеет среагировать — следующая попытка работает с уже rolled-back `EntityManager`, получает `TransientObjectException` или `IllegalStateException`.
>
>   ❌ ПОСЛЕДСТВИЕ: retry "работает" по логам (3 attempts), но все попытки падают на `detached entity` — данные не сохраняются, инцидент в production.
>
> - [ ] **C. Использовать `@Transactional(propagation = REQUIRES_NEW)` на том же методе, что и `@Retryable` — каждая попытка создаст новую транзакцию**
>
>   `REQUIRES_NEW` создаст новую транзакцию **внутри** retry-цикла, но порядок advice не меняется: `@Retryable` всё равно срабатывает **после** commit/rollback внешней транзакции. Плюс при self-invocation (вызов из того же класса) `@Transactional` вообще игнорируется.
>
>   ❌ ПОСЛЕДСТВИЕ: иллюзия решения — на тестах работает (если бросать через TestTemplate), в проде self-invocation ломает proxy и retry проходит без транзакции вообще.
>
> - [ ] **D. Ловить исключение вручную в `catch`-блоке и вызывать метод рекурсивно из самого себя**
>
>   Рекурсивный вызов из того же класса обходит Spring proxy → `@Transactional` не применяется → нет ни retry, ни управления транзакцией.
>
>   ❌ ПОСЛЕДСТВИЕ: stack overflow при долгих сбоях, нет backoff/jitter, нет метрик через `RetryListener`, нельзя ограничить max attempts централизованно.

## Q11. Как Spring Retry интегрируется с Reactor/WebClient?

`@Retryable` **не работает** с реактивными методами (`Mono`/`Flux`) — для них нужен встроенный Reactor-механизм:

```java
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
        .jitter(0.5)
        .onRetryExhaustedThrow((spec, signal) ->
            new ServiceUnavailableException("All retries exhausted")));
```

**`Retry.backoff()`** — Reactor-native retry с экспоненциальным backoff и jitter.

Альтернатива для WebClient — Resilience4j `ReactiveCircuitBreaker` + `ReactiveRateLimiter`.


> [!mcq]
> **Вопрос:** Почему `@Retryable` не подходит для реактивного `WebClient`, и какой механизм нужно использовать вместо него?
>
> - [x] **A. `@Retryable` работает с проверкой выброшенного исключения из синхронного вызова, а `Mono`/`Flux` возвращают результат немедленно — ошибку нужно ловить через оператор `retryWhen(Retry.backoff(...))` в reactive-цепочке**
>
>   `@Retryable` — это AOP-advice вокруг `Method.invoke()`. Реактивный метод возвращает `Mono<T>` **сразу** (без блокировки), а ошибка приходит **позже** через `onError` сигнал в подписке. AOP-перехватчик не видит этот сигнал — для него метод "успешен" в момент возврата `Mono`.
>
>   ```java
>   webClient.get().uri("/api/data")
>       .retrieve()
>       .bodyToMono(Data.class)
>       .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
>           .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
>           .jitter(0.5)
>           .onRetryExhaustedThrow((spec, signal) ->
>               new ServiceUnavailableException("All retries exhausted")));
>   ```
>
>   **Почему правильно:** `Retry.backoff()` — оператор Reactor, который встраивается в reactive stream и реагирует на `onError`-сигналы; поддерживает экспоненциальный backoff, jitter, фильтр исключений, hook на исчерпание.
>
>   **Use-case:** WebFlux HTTP-клиенты, R2DBC, реактивный Kafka — всё, что строится поверх `Project Reactor`.
>
> - [ ] **B. Достаточно навесить `@Retryable` на метод, возвращающий `Mono`, и Spring сам обернёт реактивный поток в retry-decorator**
>
>   Spring Retry **не имеет** интеграции с Reactor — `@Retryable` advice проверяет результат `Method.invoke()`, видит успешно вернувшийся `Mono` и завершает работу. Когда подписчик получит `onError` — retry-advice уже давно отработал.
>
>   ❌ ПОСЛЕДСТВИЕ: тесты с `StepVerifier` показывают, что retry не срабатывает, и в production первый же 503 от downstream-сервиса проваливается без повторов.
>
> - [ ] **C. Использовать `@Retryable(useReactive = true)` — специальный режим для Mono/Flux появился в Spring Retry 2.0**
>
>   Такого режима **не существует** ни в Spring Retry 1.x, ни в 2.x. API `@Retryable` не имеет параметра `useReactive`, и интеграция с Reactor не планируется (это противоречит блокирующей природе AOP-advice).
>
>   ❌ ПОСЛЕДСТВИЕ: придуманный флаг → код не компилируется или (если кто-то добавит wrapper) маскирует реальную проблему — junior-разработчик потратит часы на дебаг.
>
> - [ ] **D. Завернуть `Mono` в `.block()` и применить `@Retryable` к синхронному методу**
>
>   `.block()` блокирует поток до получения результата — это **разрушает** реактивную модель: вместо event-loop потока (несколько на JVM) используется обычный thread, теряется backpressure, при высокой нагрузке исчерпываются worker'ы WebFlux.
>
>   ❌ ПОСЛЕДСТВИЕ: на нагрузочном тестировании TPS падает в 10-100 раз, WebFlux-эффективность исчезает, а в `Schedulers.parallel()` ловится `BlockHound`-исключение.

## Q12. Что такое Stateful Retry и когда он нужен?

`Stateful Retry` сохраняет состояние между попытками через ключ (`RetryState`). Нужен для **транзакционных message listeners** (Kafka, JMS).

**Проблема:** в Kafka listener при ошибке транзакция откатывается, сообщение возвращается в очередь, consumer получает его снова — это уже новый вызов, не continuation предыдущего retry.

```java
// RetryTemplate с stateful retry для Kafka
RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(new SimpleRetryPolicy(3));

// RetryStateGenerator идентифицирует конкретное сообщение
template.setRetryState(ctx -> new DefaultRetryState(messageKey));

template.execute(ctx -> {
    processMessage(message);
    return null;
});
```

В `spring-kafka` stateful retry настраивается через `SeekToCurrentErrorHandler` + `BackOff`.


> [!mcq]
> **Вопрос:** В каких сценариях обычный (stateless) `RetryTemplate` не работает корректно и нужен Stateful Retry с `RetryState`?
>
> - [x] **A. Когда retry происходит внутри транзакционного message listener (Kafka, JMS): rollback откатывает транзакцию, брокер пере-доставляет сообщение, и каждый retry — это уже отдельный вызов, а не продолжение цикла**
>
>   В транзакционном Kafka listener при исключении транзакция (включая offset commit) откатывается, сообщение остаётся в партиции, consumer poll() получает его **снова** — для in-memory счётчика stateless retry это новое сообщение, и счётчик начинается с 0 → бесконечный цикл.
>
>   ```java
>   RetryTemplate template = new RetryTemplate();
>   template.setRetryPolicy(new SimpleRetryPolicy(3));
>   // RetryState идентифицирует сообщение по ключу — счётчик переживает rollback
>   RetryState state = new DefaultRetryState(message.key(), /* forceRefresh */ false);
>   template.execute(
>       ctx -> { processMessage(message); return null; },
>       ctx -> { sendToDlq(message); return null; },  // recoverer после исчерпания
>       state
>   );
>   ```
>
>   **Почему правильно:** `Stateful Retry` хранит счётчик **снаружи** (`RetryContextCache`, обычно in-memory `Map<key, RetryContext>`); при повторной доставке retry-advice находит существующий контекст по ключу и продолжает счёт. После `maxAttempts` срабатывает `recoverer` (отправка в DLQ).
>
>   **Use-case:** Kafka transactional listener (`spring-kafka` + `DefaultErrorHandler` с `BackOff`), JMS transacted session, Spring Batch retryable item processor — везде, где rollback нельзя избежать.
>
> - [ ] **B. Когда у retry-метода больше 3 аргументов и нужно сохранять их между попытками в `RetryContext`**
>
>   `RetryContext` действительно хранит атрибуты между попытками, но это работает и в stateless-режиме. Количество аргументов метода не имеет отношения к выбору stateful/stateless — состояние аргументов хранит стек вызова, а не retry-механизм.
>
>   ❌ ПОСЛЕДСТВИЕ: попытка решить выдуманную проблему добавлением `Stateful Retry` усложняет код, создаёт race conditions в `RetryContextCache` и не даёт никакого выигрыша.
>
> - [ ] **C. Когда нужно сохранять прогресс retry в БД (Redis), чтобы пережить перезапуск приложения**
>
>   `Stateful Retry` хранит контекст в **in-memory** `RetryContextCache` (`MapRetryContextCache` по умолчанию) — при рестарте JVM состояние теряется. Для durable retry нужны другие инструменты: outbox pattern, message broker с retry-policy, или явное хранение состояния в БД.
>
>   ❌ ПОСЛЕДСТВИЕ: команда рассчитывает на сохранение прогресса после деплоя, но после rollout retry начинается заново — двойные платежи, дублирование уведомлений.
>
> - [ ] **D. Когда нужен exponential backoff между попытками — stateless retry не поддерживает задержки**
>
>   Stateless `RetryTemplate` **отлично** поддерживает `ExponentialBackOffPolicy`, `FixedBackOffPolicy`, `UniformRandomBackOffPolicy` — backoff никак не связан с stateful/stateless. Stateful — это про **где** хранится счётчик, а не про **как** делается пауза.
>
>   ❌ ПОСЛЕДСТВИЕ: junior'ы путают эти концепции и тащат `Stateful Retry` туда, где достаточно `RetryTemplate.builder().exponentialBackoff(...).build()` — лишняя сложность без причины.

## Q13. Как добавить метрики и логирование через RetryListener?

`RetryListener` позволяет перехватывать события retry:

```java
@Bean
public RetryTemplate retryTemplate(MeterRegistry meterRegistry) {
    RetryTemplate template = RetryTemplate.builder()
        .maxAttempts(3)
        .build();

    template.registerListener(new RetryListenerSupport() {
        @Override
        public <T, E extends Throwable> void onError(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            String method = ctx.getAttribute(RetryContext.NAME).toString();
            meterRegistry.counter("retry.attempts", "method", method).increment();
            log.warn("Retry attempt {} for {}: {}", ctx.getRetryCount(), method, t.getMessage());
        }

        @Override
        public <T, E extends Throwable> void close(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            if (ctx.getRetryCount() > 0) {
                log.info("Retry completed after {} attempts for {}",
                    ctx.getRetryCount(), ctx.getAttribute(RetryContext.NAME));
            }
        }
    });
    return template;
}
```


> [!mcq] Какой подход правильный для сбора метрик и логирования retry через Spring Retry?
>
> - [ ] **A) Логировать `try/catch` внутри бизнес-метода и инкрементить счётчик вручную перед `throw`**
>   - Дублирует логику retry в каждом методе и ломает прозрачность `@Retryable`.
>   - ПОСЛЕДСТВИЕ: код-base заваливается копипастой, при изменении метрик нужно править все сервисы.
>
> - [ ] **B) Подписать `@EventListener` на `RetryEvent` через `ApplicationEventPublisher`**
>   - Spring Retry **не публикует** ApplicationEvent — путаница с Resilience4j, где RetryEvent действительно есть.
>   - ПОСЛЕДСТВИЕ: слушатель никогда не сработает, метрики останутся пустыми, инцидент проходит незамеченным.
>
> - [ ] **C) Включить `logging.level.org.springframework.retry=DEBUG` и парсить логи Promtail-ом**
>   - Логи дают только текст, без структурированных тегов method/exception для PromQL.
>   - ПОСЛЕДСТВИЕ: дашборд по `retry.attempts{method=...}` построить нельзя, alerting опаздывает.
>
> - [x] **D) Зарегистрировать `RetryListener` (через `registerListener` / `@Bean RetryListener`) и в `onError` инкрементить `MeterRegistry.counter` + логировать**
>   - `RetryListener` — официальный hook Spring Retry с колбэками `open/onError/close`; пишет метрики и логи централизованно для всех `@Retryable`/`RetryTemplate`.
>   - МЕХАНИЗМ: `ctx.getAttribute(RetryContext.NAME)` даёт имя метода → тег для Micrometer counter.
>   - USE-CASE: единый `RetryListener`-бин подключается ко всем `RetryTemplate`-ам и `@Retryable`-методам, метрики `retry.attempts{method=...}` сразу в Prometheus.

## Q14. Как настроить разные retry-политики для разных методов?

```java
@Retryable(
    retryFor = IOException.class,
    maxAttempts = 5,
    backoff = @Backoff(delay = 2000, multiplier = 1.5)
)
public void syncData() { ... }

@Retryable(
    retryFor = {ServiceUnavailableException.class},
    maxAttempts = 2,
    backoff = @Backoff(delay = 500)
)
public QuoteResponse getQuote() { ... }
```

**Класс-уровень @Retryable** как default:
```java
@Service
@Retryable(retryFor = IOException.class, maxAttempts = 3)
public class ExternalApiService {

    public Data fetchData() { ... }      // унаследует class-level retry

    @Retryable(maxAttempts = 5)          // переопределяет
    public Data fetchCriticalData() { ... }
}
```


> [!mcq] Как правильно задать разные retry-политики (maxAttempts/backoff) для разных методов одного сервиса?
>
> - [ ] **A) Один `RetryTemplate`-бин с `SimpleRetryPolicy(maxAttempts=5)`, переиспользовать его для всех методов**
>   - Все методы получают одинаковые попытки и backoff, тонкая настройка под конкретный сценарий невозможна.
>   - ПОСЛЕДСТВИЕ: критичный `getQuote()` будет долбиться 5 раз вместо 2 и съест SLA партнёра.
>
> - [x] **B) На каждом методе указать собственный `@Retryable(retryFor=..., maxAttempts=..., backoff=@Backoff(...))`; для общих дефолтов — `@Retryable` на классе с override на методах**
>   - `@Retryable` параметры независимы per-method; class-level задаёт default, method-level переопределяет.
>   - МЕХАНИЗМ: Spring AOP создаёт proxy, для каждого метода читает аннотацию и строит свой `RetryPolicy` + `BackOffPolicy`.
>   - USE-CASE: `syncData()` — 5 попыток на `IOException` с экспоненциальным backoff; `getQuote()` — 2 попытки на `ServiceUnavailableException` с фиксированной паузой; в одном бине без копипасты.
>
> - [ ] **C) Один `@Retryable` на класс с `retryFor = Exception.class`, разные сценарии разруливать `if/else` в коде метода**
>   - Перехватывает всё подряд, включая бизнес-исключения, которые ретраить нельзя; `if/else` в коде не влияет на `RetryPolicy`.
>   - ПОСЛЕДСТВИЕ: `ValidationException` ретраится 3 раза, грузя сервис и засоряя логи.
>
> - [ ] **D) Создать несколько `RetryTemplate`-бинов с разными qualifier-ами и инжектить вручную в каждый метод**
>   - Работает, но теряется декларативность; нужно императивно оборачивать вызовы и тащить шаблон через DI.
>   - ПОСЛЕДСТВИЕ: бойлерплейт растёт, читаемость падает, при появлении нового метода легко забыть обернуть.

## Q15. Что произойдёт, если @Recover выбросит исключение?

Если `@Recover` метод сам выбрасывает исключение — оно **передаётся вызывающему коду** как обычное исключение. Spring Retry **не делает** повторных попыток для исключений из `@Recover`.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    // Если это выбросит, исключение дойдёт до caller без retry
    return fallbackService.getFallbackResult(req);
}
```

**Итог:** `@Recover` — терминальный handler. Обработай все случаи или явно пробрось нужное исключение.


> [!mcq] Что произойдёт, если `@Recover`-метод сам выбросит исключение во время выполнения?
>
> - [ ] **A) Spring Retry повторно применит `RetryPolicy` к исключению из `@Recover` и снова попробует основной метод**
>   - `@Recover` — терминальная стадия, на него RetryPolicy не распространяется; повторов основного метода не будет.
>   - ПОСЛЕДСТВИЕ: разработчик ждёт «ещё одну попытку», а её нет — клиент получает ошибку сразу.
>
> - [ ] **B) Spring найдёт другой `@Recover`-метод с подходящей сигнатурой и попробует его**
>   - Цепочки `@Recover → @Recover` в Spring Retry нет; выбирается ровно один recover по match-у типа исключения исходного метода.
>   - ПОСЛЕДСТВИЕ: ожидание fallback-каскада приводит к багу — второй recover не вызывается, exception летит наверх.
>
> - [x] **C) Исключение из `@Recover` пробрасывается вызывающему коду как обычное исключение, без повторов и без поиска другого recover-а**
>   - `@Recover` — терминальный handler: что он выбросил, то caller и получает.
>   - МЕХАНИЗМ: `RetryOperationsInterceptor` вызывает `RecoveryCallback` один раз; его throwable идёт через обычный stack.
>   - USE-CASE: внутри recover нужно либо обработать все ветки и вернуть fallback-DTO, либо явно бросить доменное `BusinessException`, которое поймает `@ControllerAdvice`.
>
> - [ ] **D) Исключение проглатывается, основной метод возвращает `null`**
>   - Spring Retry не глотает исключения молча; `null` вернётся только если recover явно `return null`.
>   - ПОСЛЕДСТВИЕ: ожидание «тихого fallback» приводит к NPE у caller и скрытому повреждению данных.

## Q16. Как протестировать логику retry в тестах?

`@Retryable` работает через Spring AOP — нужен `@SpringBootTest` или `@SpringJUnitConfig` с AOP. Обычный `new MyService()` обойдёт прокси.

```java
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @MockBean
    PaymentGateway gateway;

    @Test
    void shouldRetry3TimesOnIOException() {
        when(gateway.charge(any())).thenThrow(new IOException("timeout"));

        assertThatThrownBy(() -> paymentService.processPayment(request))
            .isInstanceOf(IOException.class);

        verify(gateway, times(3)).charge(any()); // проверяем 3 попытки
    }

    @Test
    void shouldCallRecoverAfterExhaustion() {
        when(gateway.charge(any())).thenThrow(new IOException());

        PaymentResult result = paymentService.processPayment(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo("Service unavailable");
    }
}
```

**Ускорение тестов:** для тестирования retry логики без реального backoff используй `RetryTemplate` напрямую без `@Backoff`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Чем Spring Retry отличается от Resilience4j? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Критерий | Spring Retry | Resilience4j |
|---|---|---|
| Retry | Полноценный | Полноценный |
| Circuit Breaker | Базовый (без HALF_OPEN) | Полноценный (CLOSED/OPEN/HALF_OPEN) |
| Rate Limiter | Нет | Есть |
| Bulkhead | Нет | Есть (ThreadPool + Semaphore) |
| TimeLimiter | Нет | Есть |
| Реактивный стек | Нет | Reactor, RxJava |
| Метрики Micrometer | Через listener вручную | Встроенные out-of-the-box |
| Spring Boot auto-config | Частичная | Полная (через starter) |

**Когда Spring Retry:** простые сценарии retry, уже используешь Spring Batch/Integration (они используют Spring Retry внутри), не нужен CB.

**Когда Resilience4j:** production microservices, нужен Circuit Breaker + метрики + observability. Подробнее в [Resilience4j](resilience4j-interview.md).

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Resilience4j](resilience4j-interview.md) — полноценная fault tolerance: CB, RateLimiter, Bulkhead ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring @Transactional](spring-transaction-interview.md) — совместное использование с @Retryable
- [Spring AOP](spring-aop-interview.md) — механизм proxy, self-invocation проблема
- [Spring WebFlux](spring-webflux-interview.md) — реактивный retry через Reactor
- [Spring Testing](spring-testing-interview.md) — тестирование @Retryable в @SpringBootTest
- [Spring Batch](spring-batch-interview.md) — Spring Retry встроен в retry step
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики retry через RetryListener + MeterRegistry
- [Distributed Systems](../../architecture/distributed-systems-interview.md) — теория: retry паттерны, idempotency
