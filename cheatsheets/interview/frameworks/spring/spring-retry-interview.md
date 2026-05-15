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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Какие backoff-стратегии поддерживает Spring Retry? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое RetryTemplate и когда его использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Какие RetryPolicy реализации есть в Spring Retry? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Что такое CircuitBreakerRetryPolicy? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Почему @Retryable не работает при self-invocation? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) Как правильно комбинировать @Retryable и @Transactional? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как Spring Retry интегрируется с Reactor/WebClient? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое Stateful Retry и когда он нужен? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Как добавить метрики и логирование через RetryListener? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как настроить разные retry-политики для разных методов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Что произойдёт, если @Recover выбросит исключение? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Если `@Recover` метод сам выбрасывает исключение — оно **передаётся вызывающему коду** как обычное исключение. Spring Retry **не делает** повторных попыток для исключений из `@Recover`.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    // Если это выбросит, исключение дойдёт до caller без retry
    return fallbackService.getFallbackResult(req);
}
```

**Итог:** `@Recover` — терминальный handler. Обработай все случаи или явно пробрось нужное исключение.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Как протестировать логику retry в тестах? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
