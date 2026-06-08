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
- [Q1. (!) Что такое Spring Retry и для каких задач он предназначен?](#q1--что-такое-spring-retry-и-для-каких-задач-он-предназначен)
- [Q2. Как подключить Spring Retry?](#q2-как-подключить-spring-retry)
- [Q3. (!) Как работает @Retryable?](#q3--как-работает-retryable)
- [Q4. (!) Что такое @Recover и каковы требования к сигнатуре?](#q4--что-такое-recover-и-каковы-требования-к-сигнатуре)
- [Q5. Какие backoff-стратегии поддерживает Spring Retry?](#q5-какие-backoff-стратегии-поддерживает-spring-retry)

**RetryTemplate и политики**
- [Q6. Что такое RetryTemplate и когда его использовать?](#q6-что-такое-retrytemplate-и-когда-его-использовать)
- [Q7. Какие RetryPolicy реализации есть в Spring Retry?](#q7-какие-retrypolicy-реализации-есть-в-spring-retry)
- [Q8. Что такое CircuitBreakerRetryPolicy?](#q8-что-такое-circuitbreakerretrypolicy)

**Ограничения и интеграции**
- [Q9. (!) Почему @Retryable не работает при self-invocation?](#q9--почему-retryable-не-работает-при-self-invocation)
- [Q10. (!) Как правильно комбинировать @Retryable и @Transactional?](#q10--как-правильно-комбинировать-retryable-и-transactional)
- [Q11. Как Spring Retry интегрируется с Reactor/WebClient?](#q11-как-spring-retry-интегрируется-с-reactorwebclient)
- [Q12. Что такое Stateful Retry и когда он нужен?](#q12-что-такое-stateful-retry-и-когда-он-нужен)

**Мониторинг и тестирование**
- [Q13. Как добавить метрики и логирование через RetryListener?](#q13-как-добавить-метрики-и-логирование-через-retrylistener)
- [Q14. Как настроить разные retry-политики для разных методов?](#q14-как-настроить-разные-retry-политики-для-разных-методов)
- [Q15. Что произойдёт, если @Recover выбросит исключение?](#q15-что-произойдёт-если-recover-выбросит-исключение)
- [Q16. Как протестировать логику retry в тестах?](#q16-как-протестировать-логику-retry-в-тестах)
- [Q17. Чем Spring Retry отличается от Resilience4j?](#q17-чем-spring-retry-отличается-от-resilience4j)

## Q1. (!) Что такое Spring Retry и для каких задач он предназначен?

`Spring Retry` — библиотека, которая автоматически повторяет операцию, если та упала с заданным исключением. Идея простая: при сбое метод вызывается заново — заданное число раз и с настраиваемой паузой между попытками. Это закрывает класс **временных (transient) сбоев**, которые проходят сами собой и не требуют вмешательства человека.

**Где это уместно** — когда ошибка с высокой вероятностью пропадёт при повторе:
- HTTP-обращения к нестабильным внешним API
- сетевые вызовы с временными ошибками (таймауты, 5xx)
- операции с БД при дедлоках или `OptimisticLockException`
- чтение из очередей (Kafka, JMS)

Ключевое слово — **временный**: повторять имеет смысл только то, что может починиться само (сеть моргнула, соседняя транзакция отпустила блокировку). Повторять ошибку валидации или `404` бессмысленно — результат не изменится, а попытки лишь нагрузят систему.

**Границы применимости.** Spring Retry — это именно retry, и больше ничего. Circuit Breaker (чтобы перестать долбить упавший сервис), Bulkhead (изоляция пулов) и Rate Limiter — это уже про fault tolerance целиком, и для них берут [Resilience4j](resilience4j-interview.md).

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

Аннотационный подход требует `@EnableRetry` — именно эта аннотация включает AOP-инфраструктуру, которая обрабатывает `@Retryable`:
```java
@Configuration
@EnableRetry
public class AppConfig {}
```

**Частая ошибка на собеседовании:** считать, что в Spring Boot ничего настраивать не нужно. Boot подтянет `spring-retry` через стартер, но `@EnableRetry` всё равно надо поставить руками — без неё аннотации `@Retryable` просто игнорируются, метод выполняется один раз и никакого повтора не происходит.

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

`@Retryable` — это AOP-обёртка: при вызове метода прокси ловит исключение, и если оно подходит под фильтр — вызывает метод заново, выдерживая паузу из `backoff`.

**Параметры:**
- `retryFor` — какие исключения повторять (бывший `value`); всё, что не попало сюда, пробрасывается сразу
- `noRetryFor` — исключения-исключения: не повторять, даже если они подходят под `retryFor` (бывший `exclude`)
- `maxAttempts` — максимум попыток **включая первую** (по умолчанию 3). То есть `maxAttempts = 3` — это один обычный вызов плюс два повтора, а не три повтора
- `backoff` — пауза между попытками (см. Q5)

**Что происходит, когда попытки кончились:** последнее пойманное исключение пробрасывается вызывающему коду. Но если в том же классе есть подходящий `@Recover`-метод — управление уходит туда, и наружу летит уже его результат (см. Q4).

## Q4. (!) Что такое @Recover и каковы требования к сигнатуре?

`@Recover` — это fallback-метод: Spring вызывает его, когда все попытки `@Retryable` исчерпаны, чтобы вернуть «запасной» результат вместо проброса исключения. Без него после последней неудачи наружу полетит исходное исключение; с ним — то, что вернёт `@Recover`.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    log.error("Payment failed after retries for {}", req.getId(), e);
    return PaymentResult.failed("Service unavailable");
}
```

**Требования к сигнатуре** (если их нарушить, Spring молча не найдёт recover и пробросит исключение):
- метод — в **том же классе**, что и `@Retryable`
- **первый аргумент** — тип пойманного исключения (или его суперкласс); по нему Spring и связывает recover с retryable-методом
- **возвращаемый тип совпадает** с типом `@Retryable`-метода — иначе результат некуда вернуть
- остальные аргументы — те же, что у `@Retryable`-метода (Spring прокинет в recover исходные параметры вызова)

**Как выбирается нужный @Recover.** Recover-методов может быть несколько, и Spring выбирает самый специфичный по типу исключения — ровно как Java выбирает перегрузку метода. Для `IOException` сработает recover с параметром `IOException`, а не общий с `Exception`.

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

## Q5. Какие backoff-стратегии поддерживает Spring Retry?

Backoff — это пауза между попытками. Главная задача — не повторять мгновенно: дать упавшему ресурсу время восстановиться и не усугубить его перегрузку шквалом ретраев. Все стратегии настраиваются через `@Backoff` и различаются тем, как растёт пауза от попытки к попытке.

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

Чаще всего по умолчанию берут **экспоненциальный backoff с потолком**: паузы растут (1s → 2s → 4s…), но не разбегаются до бесконечности благодаря `maxDelay`. Это компромисс между «дать ресурсу восстановиться» и «не ждать слишком долго».

**Подводный камень — thundering herd.** Если внешний сервис упал и сотни клиентов одновременно начали ретраить, их повторы выстроятся в синхронные волны и в момент восстановления добьют сервис согласованным всплеском нагрузки. **Джиттер** (`random = true`) добавляет к паузе случайный разброс — попытки размазываются по времени, и волна сглаживается. В проде для распределённых ретраев джиттер скорее обязателен, чем опционален.

**Программный BackOffPolicy:**
```java
ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
backOff.setInitialInterval(1000);
backOff.setMultiplier(2.0);
backOff.setMaxInterval(10000);
```

## Q6. Что такое RetryTemplate и когда его использовать?

`RetryTemplate` — программный (императивный) API для retry, без аннотаций и без AOP. Ты сам оборачиваешь нужный код в `execute(...)`, а шаблон управляет попытками и backoff. Это даёт контроль там, где аннотации не работают или негибки.

**Когда брать `RetryTemplate` вместо `@Retryable`:**
- retry нужен **не в Spring bean** (utility-метод, лямбда, статический код) — туда AOP-прокси не дотянется
- политику надо **собирать динамически** в рантайме (число попыток/backoff из конфига)
- нужен **recover-callback прямо в коде**, а не отдельным `@Recover`-методом
- нужен **stateful retry** для транзакционных листенеров (Kafka, JMS) — см. Q12

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

## Q7. Какие RetryPolicy реализации есть в Spring Retry?

`RetryPolicy` отвечает на один вопрос: **стоит ли делать ещё одну попытку?** Это точка расширения, по которой Spring Retry решает, продолжать или остановиться. По умолчанию используется `SimpleRetryPolicy`, но есть готовые альтернативы под разные стратегии:

| Политика | Когда продолжать повторы |
|---|---|
| `SimpleRetryPolicy` | пока не исчерпано фиксированное число попыток и исключение подходит по типу (дефолт) |
| `AlwaysRetryPolicy` | всегда, бесконечно — осторожно, легко словить вечный цикл |
| `NeverRetryPolicy` | никогда; удобно, чтобы отключить retry (в т.ч. в тестах) |
| `TimeoutRetryPolicy` | пока не истёк заданный таймаут (ограничение по времени, а не по числу) |
| `ExceptionClassifierRetryPolicy` | по-разному в зависимости от типа исключения |
| `CircuitBreakerRetryPolicy` | как вложенная политика, но с «предохранителем» — см. Q8 |
| `CompositeRetryPolicy` | по комбинации нескольких политик (AND/OR-логика) |

`ExceptionClassifierRetryPolicy` особенно полезен, когда одни ошибки заслуживают больше попыток, чем другие, а часть — не заслуживают повтора вовсе:

```java
// ExceptionClassifierRetryPolicy
ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
policy.setPolicyMap(Map.of(
    IOException.class, new SimpleRetryPolicy(3),
    TimeoutException.class, new SimpleRetryPolicy(5),
    IllegalArgumentException.class, new NeverRetryPolicy()
));
```

## Q8. Что такое CircuitBreakerRetryPolicy?

`CircuitBreakerRetryPolicy` — это политика, добавляющая поверх обычного retry логику «предохранителя» (Circuit Breaker). Идея в том, что бесконечно ретраить заведомо упавший сервис вредно: после серии неудач цепь **размыкается** (open), и на время `openTimeout` запросы перестают уходить вовсе — они быстро падают, не нагружая лежащий ресурс. По истечении `resetTimeout` цепь сбрасывается и попытки возобновляются.

```java
CircuitBreakerRetryPolicy circuitBreakerPolicy = new CircuitBreakerRetryPolicy(
    new SimpleRetryPolicy(3)
);
circuitBreakerPolicy.setOpenTimeout(5000);    // цепь открыта 5 сек
circuitBreakerPolicy.setResetTimeout(20000);  // сбросить через 20 сек

RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(circuitBreakerPolicy);
```

**Чем это хуже полноценного CB.** Это упрощённый вариант для несложных случаев: нет состояния HALF_OPEN (пробного полуоткрытия для проверки, ожил ли сервис), нет метрик и нет Spring Boot auto-configuration. Если CB нужен всерьёз — наблюдаемость, HALF_OPEN, настройка через стартер — берите [Resilience4j](resilience4j-interview.md).

## Q9. (!) Почему @Retryable не работает при self-invocation?

Потому что `@Retryable` реализован через **Spring AOP proxy**, а внутренний вызов прокси не проходит. Spring оборачивает bean в прокси-объект, и всю retry-логику добавляет именно прокси — снаружи. Когда метод вызывает другой свой же метод через `this.method()`, вызов идёт напрямую по ссылке на реальный объект, минуя прокси. Прокси не видит этот вызов — значит, не видит и исключение, и повторять нечего.

Это ровно та же ловушка, что и с `@Transactional`, и причина у неё одна — proxy-based AOP. Подробнее в [Spring AOP](spring-aop-interview.md).

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

**Решения** (все сводятся к одному: заставить вызов пройти через прокси):

1. **Вынести `@Retryable`-метод в отдельный bean (рекомендуется).** Вызов из другого бина идёт через прокси целевого бина, и retry работает. Это самый чистый вариант — заодно не путаешь обычную логику с retry-обёрткой:
```java
@Service
public class PaymentRetryService {
    @Retryable(retryFor = IOException.class)
    public void charge(Payment p) { ... }
}
```

2. **Self-inject через `@Lazy`** — внедрить ссылку на собственный прокси и звать метод через неё, а не через `this`. `@Lazy` нужен, чтобы разорвать циклическую зависимость бина на самого себя:
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

3. **`RetryTemplate`** — вообще не зависит от AOP, так что проблемы self-invocation у него нет в принципе.

## Q10. (!) Как правильно комбинировать @Retryable и @Transactional?

Главное правило: **retry должен быть снаружи транзакции, а транзакция — внутри**, чтобы каждая попытка шла в собственной свежей транзакции.

Если повесить `@Transactional` и `@Retryable` на **один и тот же** метод, retry становится бесполезным. Порядок прокси таков, что транзакция оборачивает вызов изнутри retry: когда внутри ловится исключение и срабатывает повтор, транзакция уже помечена rollback-only. Все повторные попытки идут в той же «грязной» транзакции, которой суждено откатиться, — то есть retry крутится впустую.

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

Так каждая попытка получает **новую транзакцию**: предыдущая полностью откатилась, состояние чистое, и повтор имеет шанс пройти. Это особенно важно для `OptimisticLockException` — повторять с устаревшей версией сущности бессмысленно, нужна перезагрузка в свежей транзакции.

**Правило:** `@Retryable` — на **внешнем** методе (без `@Transactional`), `@Transactional` — на вложенном. И помни про Q9: вложенный метод должен вызываться через прокси, иначе и транзакция не стартует.

## Q11. Как Spring Retry интегрируется с Reactor/WebClient?

Короткий ответ — **никак напрямую: для реактивного кода используют не Spring Retry, а встроенный retry самого Reactor**. `@Retryable` бесполезен с методами, возвращающими `Mono`/`Flux`: метод-фабрика отрабатывает мгновенно и возвращает ещё «холодный» паблишер, не выбросив никакого исключения — ошибка возникнет позже, асинхронно, в реактивном пайплайне, куда AOP-обёртка уже не дотягивается. Поэтому повтор вешают прямо на пайплайн через `retryWhen`:

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

Что здесь происходит:
- **`Retry.backoff(3, ...)`** — Reactor-native retry с экспоненциальным backoff: 3 попытки, паузы растут от базовых 500 мс
- **`.filter(...)`** — повторяем только нужные ошибки (здесь `503 Service Unavailable`), остальное пробрасываем сразу
- **`.jitter(0.5)`** — тот же джиттер против thundering herd, что и в Q5, но средствами Reactor
- **`.onRetryExhaustedThrow(...)`** — что пробросить, когда попытки кончились

Если нужен не только retry, а полноценная отказоустойчивость для WebClient, берут Resilience4j `ReactiveCircuitBreaker` + `ReactiveRateLimiter` — они тоже умеют работать с `Mono`/`Flux`.

## Q12. Что такое Stateful Retry и когда он нужен?

Разница между обычным (stateless) и stateful retry — в том, **где живёт счётчик попыток**. У обычного retry весь цикл попыток крутится внутри одного вызова: исключение ловится в том же стеке, и состояние держится в памяти. Stateful retry, наоборот, **сохраняет состояние между отдельными вызовами** — по ключу (`RetryState`), который идентифицирует конкретную единицу работы.

**Зачем это нужно — на примере транзакционного Kafka/JMS listener.** Здесь повтор устроен принципиально иначе: при ошибке транзакция откатывается, сообщение возвращается в очередь, и брокер доставляет его снова. Но это уже **новый, отдельный вызов** listener-а, а не продолжение предыдущего цикла — обычный stateless retry о прошлых попытках ничего не знает и каждый раз начинает счёт заново. Stateful retry решает проблему: по ключу сообщения он узнаёт, что эту единицу работы уже пытались обработать, и ведёт сквозной счётчик попыток поверх повторных доставок.

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

## Q13. Как добавить метрики и логирование через RetryListener?

`RetryListener` — это хук в жизненный цикл retry: он даёт перехватывать ключевые события, не засоряя бизнес-логику. Это штатное место для сквозных задач — метрик и логирования. Основные коллбэки:
- `open` — перед началом цикла попыток
- `onError` — на каждой неудачной попытке (сюда вешают счётчик ретраев и предупреждающий лог)
- `close` — после завершения цикла, успешного или нет (удобно подвести итог: сколько попыток ушло)

Без листенера ретраи «немые» — в проде ты не увидишь, что какой-то вызов втихую повторяется по три раза. Именно `onError` + `MeterRegistry` превращают это в наблюдаемую метрику:

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

## Q14. Как настроить разные retry-политики для разных методов?

Самый прямой способ — навесить `@Retryable` со своими параметрами на каждый метод отдельно. Политика срабатывает «по месту»: для разных операций — разное число попыток, свой набор исключений и свой backoff.

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

Если у большинства методов класса политика одинаковая, её выносят на **уровень класса** как значение по умолчанию, а у отдельных методов переопределяют точечно — меньше дублирования:
```java
@Service
@Retryable(retryFor = IOException.class, maxAttempts = 3)
public class ExternalApiService {

    public Data fetchData() { ... }      // унаследует class-level retry

    @Retryable(maxAttempts = 5)          // переопределяет
    public Data fetchCriticalData() { ... }
}
```

## Q15. Что произойдёт, если @Recover выбросит исключение?

Исключение из `@Recover` **пробрасывается вызывающему коду как обычное** — никаких повторов. `@Recover` находится **вне** retry-цикла: к моменту его вызова попытки уже исчерпаны, и Spring Retry брошенное им исключение не перехватывает и не ретраит. То есть recover — это последний рубеж, а не ещё одна повторяемая попытка.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    // Если это выбросит, исключение дойдёт до caller без retry
    return fallbackService.getFallbackResult(req);
}
```

**Вывод:** `@Recover` — терминальный обработчик. Не рассчитывай, что он «дострахован» retry: либо корректно обработай все случаи внутри него, либо осознанно пробрось наружу то исключение, которое должен увидеть вызывающий код.

## Q16. Как протестировать логику retry в тестах?

Главное, что проверяют на собеседовании: **retry живёт в прокси, поэтому бин нужно поднимать через Spring-контекст**. Создашь `new MyService()` — получишь голый объект без прокси, и `@Retryable` не отработает: повтора не будет, тест проверит совсем не то. Нужен `@SpringBootTest` (или `@SpringJUnitConfig` с включённым AOP), чтобы инжектировался именно прокси.

Типовой подход — замокать зависимость так, чтобы она стабильно падала, и проверить **число вызовов** (что попыток было ровно `maxAttempts`) и срабатывание `@Recover`:

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

**Чтобы тесты не тормозили.** Реальный backoff заставит тест честно ждать паузы между попытками — на CI это секунды на ровном месте. Для проверки самой логики повторов backoff не нужен: тестируй через `RetryTemplate` напрямую без `@Backoff` (или с нулевой задержкой), чтобы попытки шли без ожидания.

## Q17. Чем Spring Retry отличается от Resilience4j?

Коротко: **Spring Retry умеет только повторять, Resilience4j — это полный набор паттернов отказоустойчивости**. Если нужен лишь retry в простом приложении — хватит Spring Retry; если строишь устойчивые микросервисы с Circuit Breaker, rate limiting и метриками — берёшь Resilience4j. Разница видна по таблице — Spring Retry закрывает по сути одну колонку:

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

**Берём Spring Retry, когда:** нужен простой retry, проект уже на Spring Batch/Integration (там Spring Retry встроен внутри), полноценный Circuit Breaker не требуется.

**Берём Resilience4j, когда:** это production-микросервисы, и нужен Circuit Breaker с HALF_OPEN, встроенные метрики и observability, а часто ещё и Rate Limiter с Bulkhead. Подробнее в [Resilience4j](resilience4j-interview.md).

---

## See also

- [Resilience4j](resilience4j-interview.md) — полноценная fault tolerance: CB, RateLimiter, Bulkhead
- [Spring @Transactional](spring-transaction-interview.md) — совместное использование с @Retryable
- [Spring AOP](spring-aop-interview.md) — механизм proxy, self-invocation проблема
- [Spring WebFlux](spring-webflux-interview.md) — реактивный retry через Reactor
- [Spring Testing](spring-testing-interview.md) — тестирование @Retryable в @SpringBootTest
- [Spring Batch](spring-batch-interview.md) — Spring Retry встроен в retry step
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики retry через RetryListener + MeterRegistry
- [Distributed Systems](../../architecture/distributed-systems-interview.md) — теория: retry паттерны, idempotency
