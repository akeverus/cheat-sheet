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

`Spring Retry` – библиотека автоматического повтора неудавшихся операций. Предоставляет аннотационный (`@Retryable`/`@Recover`) и программный (`RetryTemplate`) API. Основные примеры относятся к Spring Retry 2.0.x. Для подключения и тестов используется Spring Boot 3.5 / Spring Framework 6.2. Встроенный retry Spring Framework 7.0 рассматривается отдельно.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Retry GitHub](https://github.com/spring-projects/spring-retry) — официальный репозиторий с документацией
- [Spring Retry 2.0.12 API](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/)
- [Resilience Features в Spring Framework 7.0](https://docs.spring.io/spring-framework/reference/7.0/core/resilience.html)

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
- [Q9. (!) Как ведёт себя @Retryable при self-invocation?](#q9--как-ведёт-себя-retryable-при-self-invocation)
- [Q10. (!) Как правильно комбинировать @Retryable и @Transactional?](#q10--как-правильно-комбинировать-retryable-и-transactional)
- [Q11. Как @Retryable обрабатывает ошибки WebClient в Spring Retry 2.0.x и Framework 7.0?](#q11-как-retryable-обрабатывает-ошибки-webclient-в-spring-retry-20x-и-framework-70)
- [Q12. Что такое Stateful Retry и когда он нужен?](#q12-что-такое-stateful-retry-и-когда-он-нужен)

**Мониторинг и тестирование**
- [Q13. Как добавить метрики и логирование через RetryListener?](#q13-как-добавить-метрики-и-логирование-через-retrylistener)
- [Q14. Как настроить разные retry-политики для разных методов?](#q14-как-настроить-разные-retry-политики-для-разных-методов)
- [Q15. Что произойдёт, если @Recover выбросит исключение?](#q15-что-произойдёт-если-recover-выбросит-исключение)
- [Q16. Как протестировать логику retry в тестах?](#q16-как-протестировать-логику-retry-в-тестах)
- [Q17. Чем Spring Retry отличается от Resilience4j?](#q17-чем-spring-retry-отличается-от-resilience4j)

## Q1. (!) Что такое Spring Retry и для каких задач он предназначен?

```text
Операция A один раз получает HTTP 503, затем ресурс восстанавливается.
Операция B каждый раз получает ошибку валидации для тех же данных.
Операция C отправляет платёж, затем клиент получает таймаут, а статус списания неизвестен.
```

`Spring Retry` — библиотека, которая автоматически повторяет операцию, если та упала с заданным исключением. Идея простая: при сбое метод вызывается заново — заданное число раз и с настраиваемой паузой между попытками. Это закрывает класс **временных (transient) сбоев**, которые проходят сами собой и не требуют вмешательства человека.

**Где это уместно** — когда ошибка с высокой вероятностью пропадёт при повторе:
- HTTP-обращения к нестабильным внешним API
- сетевые вызовы с временными ошибками (таймауты, 5xx)
- операции с БД при дедлоках или `OptimisticLockException`
- чтение из очередей (Kafka, JMS)

Ключевое слово – **временный**: повторять имеет смысл только то, что может починиться само (сеть моргнула, соседняя транзакция отпустила блокировку). Повтор запроса с неизменными невалидными данными обычно бессмысленен. Допустимость повтора для HTTP-статуса зависит от контракта ресурса и запроса.

Перед повтором операции с побочным эффектом проверьте её идемпотентность. Например, при таймауте платёж мог уже пройти. Без ключа идемпотентности повторная отправка может создать второе списание.

В Spring Retry есть также базовый circuit breaker (Q8). Для ограничения скорости и изоляции нагрузки см. [Resilience4j](resilience4j-interview.md).

## Q2. Как подключить Spring Retry?

```text
Spring Boot 3.5, Maven с BOM Boot. Нужно применить
org.springframework.retry.annotation.Retryable к методу Spring-бина.
Зависимости и обработка этой аннотации ещё не настроены.
```

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

Аннотационный подход требует `@EnableRetry` — именно эта аннотация включает AOP-инфраструктуру, которая обрабатывает `@Retryable`:
```java
@Configuration
@EnableRetry
public class AppConfig {}
```

В примере BOM Boot 3.5 управляет версиями зависимостей. Если библиотека уже пришла транзитивно, это обеспечивает наличие классов, а обработку аннотаций включает `@EnableRetry`. Программному `RetryTemplate` из Spring Retry эта AOP-конфигурация не нужна. [Координаты зависимостей Boot 3.5](https://docs.spring.io/spring-boot/3.5/appendix/dependency-versions/coordinates.html)

В Spring Framework 7.0 встроенная аннотация находится в пакете `org.springframework.resilience.annotation` и включается через `@EnableResilientMethods`. Аннотация библиотеки Spring Retry находится в `org.springframework.retry.annotation`. [Spring Framework 7.0](https://docs.spring.io/spring-framework/reference/7.0/core/resilience.html)

## Q3. (!) Как работает @Retryable?

```java
@Retryable(retryFor = IOException.class, maxAttempts = 3)
public Data readData() throws IOException {
    return gateway.read();
}

// Spring Retry 2.0.x, вызов через прокси, gateway.read() всегда выбрасывает IOException.
// Обработчика @Recover и внешнего повтора нет.
```

```java
@Retryable(
    retryFor = {IOException.class, TimeoutException.class},
    noRetryFor = {ValidationException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
)
public PaymentResult processPayment(PaymentRequest req) throws IOException, TimeoutException {
    return gateway.charge(req);
}
```

`@Retryable` — это AOP-обёртка: при вызове метода прокси ловит исключение, и если оно подходит под фильтр — вызывает метод заново, выдерживая паузу из `backoff`.

**Параметры:**
- `retryFor` – какие исключения повторять (бывший `value`); при непустом списке он ограничивает набор типов для повтора
- `noRetryFor` – типы, для которых повторы исключены (бывший `exclude`). При пересечении иерархий классификация учитывает конкретность типа
- `maxAttempts` — максимум попыток **включая первую** (по умолчанию 3). То есть `maxAttempts = 3` — это один обычный вызов плюс два повтора, а не три повтора
- `backoff` — пауза между попытками (см. Q5)

В обычном stateless-вызове без recovery вызывающий получает последнее исключение. При доступном `@Recover` результат может прийти из него. `noRetryFor` отключает повторы для типа, но разрешает recovery. `notRecoverable` исключает recovery для типа, а в сочетании с `noRetryFor` исключение уходит вызывающему после первой неудачной попытки. [Spring Retry @Retryable](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/org/springframework/retry/annotation/Retryable.html)

У встроенного `@Retryable` Spring Framework 7.0 `maxRetries = 3` означает до четырёх вызовов вместе с первым. Для отбора типов служат `includes` и `excludes`.

## Q4. (!) Что такое @Recover и каковы требования к сигнатуре?

```text
Spring Retry 2.0.x. Метод PaymentResult processPayment(PaymentRequest req)
исчерпал попытки на IOException. В том же классе доступны два @Recover:
PaymentResult recoverIo(IOException e, PaymentRequest req)
PaymentResult recoverGeneral(Exception e, PaymentRequest req)
```

`@Recover` даёт запасной результат, если политика закончила попытки и допускает восстановление для этой ошибки. Следующий пример показывает отдельный обработчик для `IOException`.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    log.error("Payment failed after retries for {}", req.getId(), e);
    return PaymentResult.failed("Service unavailable");
}
```

**Сигнатура обработчика в Spring Retry 2.0.x:**
- метод — в **том же классе**, что и `@Retryable`
- **первый аргумент**, если он есть, принимает исключение подходящего типа. Обработчик без параметра `Throwable` может служить запасным вариантом
- **возвращаемый тип совпадает** с типом `@Retryable`-метода — иначе результат некуда вернуть
- после исключения можно принять аргументы исходного вызова в том же порядке, включая только начальную часть списка

**Как выбирается нужный @Recover.** Recover-методов может быть несколько, и Spring выбирает самый специфичный по типу исключения при обычном выборе без явно заданного имени. Для `IOException` сработает recover с параметром `IOException`, а не общий с `Exception`.

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

Если recovery-обработчик настроен, но подходящую сигнатуру выбрать не удалось, возможен `ExhaustedRetryException` с исходным исключением в качестве причины. Встроенный `@Retryable` Framework 7.0 не использует `@Recover` библиотеки Spring Retry. [Spring Retry @Recover](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/org/springframework/retry/annotation/Recover.html)

## Q5. Какие backoff-стратегии поддерживает Spring Retry?

```text
Spring Retry 2.0.x. Вызов каждый раз завершается ошибкой, допустимы шесть
повторов. Случайность отключена. @Backoff задан с delay = 1000,
multiplier = 2.0 и maxDelay = 10000. Какие паузы будут между попытками?
```

Backoff – это пауза между попытками. Главная задача – не повторять мгновенно: дать упавшему ресурсу время восстановиться и не усугубить его перегрузку шквалом ретраев. Все стратегии настраиваются через `@Backoff` в аннотационном API. В программном API задержку определяет `BackOffPolicy`.

```java
// Фиксированная задержка (1 сек)
@Backoff(delay = 1000)

// Экспоненциальная: 1s → 2s → 4s
@Backoff(delay = 1000, multiplier = 2.0)

// Экспоненциальная с потолком: 1s → 2s → 4s → 8s → 10s → 10s
@Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)

// С джиттером (предотвращает thundering herd)
@Backoff(delay = 1000, multiplier = 2.0, random = true)
```

У `@Backoff` без параметров фиксированная пауза 1000 мс. `delay` вместе с большим `maxDelay` без множителя задаёт равномерно случайную паузу в этом диапазоне. При `multiplier > 1` задержка растёт экспоненциально до потолка. Это настройки аннотации. Самостоятельный `RetryTemplate` по умолчанию работает без пауз. [Spring Retry @Backoff](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/org/springframework/retry/annotation/Backoff.html)

**Подводный камень – thundering herd.** Если внешний сервис упал и сотни клиентов одновременно начали ретраить, их повторы выстроятся в синхронные волны и в момент восстановления добьют сервис согласованным всплеском нагрузки. **Джиттер** (`random = true`) добавляет к паузе случайный разброс – попытки размазываются по времени, и волна сглаживается. Для экспоненциальной политики `random = true` задаёт случайный множитель для расчёта паузы.

**Программный BackOffPolicy:**
```java
ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
backOff.setInitialInterval(1000);
backOff.setMultiplier(2.0);
backOff.setMaxInterval(10000);
```

## Q6. Что такое RetryTemplate и когда его использовать?

```text
Дан вызов externalApi.call(data) в объекте, созданном через new.
Нужны до трёх попыток при IOException, возрастающие паузы
и fallbackResult(data) после исчерпания. Политика задаётся в вызывающем коде.
```

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

// Использование внедрённого экземпляра шаблона
retryTemplate.execute(
    ctx -> externalApi.call(data),      // операция
    ctx -> fallbackResult(data)         // recover callback
);
```

У одноимённого `org.springframework.core.retry.RetryTemplate` из Framework 7.0 другой контракт. Он запускает операцию через `invoke(...)` или `execute(...)` без `RecoveryCallback` Spring Retry. Для программной обработки исчерпания попыток у `execute` можно перехватить `RetryException`. [Программный retry Framework 7.0](https://docs.spring.io/spring-framework/reference/7.0/core/resilience.html)

## Q7. Какие RetryPolicy реализации есть в Spring Retry?

```text
Spring Retry 2.0.x. Для IOException допустимы три вызова вместе с первым,
для TimeoutException пять, для IllegalArgumentException только первый.
Тип исключения виден непосредственно, без обёртки.
```

`RetryPolicy` отвечает на один вопрос: **стоит ли делать ещё одну попытку?** Это точка расширения, по которой Spring Retry решает, продолжать или остановиться. По умолчанию используется `SimpleRetryPolicy`, но есть готовые альтернативы под разные стратегии:

| Политика | Когда продолжать повторы |
|---|---|
| `SimpleRetryPolicy` | пока не исчерпано фиксированное число попыток и исключение подходит по типу (дефолт) |
| `AlwaysRetryPolicy` | всегда, бесконечно — осторожно, легко словить вечный цикл |
| `NeverRetryPolicy` | после первой неудачной попытки повторов нет |
| `TimeoutRetryPolicy` | пока при проверке политики не истёк таймаут. Выполняющаяся попытка не прерывается |
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

```text
Spring Retry 2.0.x. Последовательные вызовы используют состояние с одним
ключом. SimpleRetryPolicy(3), openTimeout = 5 с, resetTimeout = 20 с.
Три вызова завершились подходящей ошибкой за первые 3 с.
Что произойдёт с запросами через 1 с и 21 с после третьей ошибки?
```

`CircuitBreakerRetryPolicy` – это политика, добавляющая поверх обычного retry логику «предохранителя» (Circuit Breaker). Идея в том, что бесконечно ретраить заведомо упавший сервис вредно. `openTimeout` – окно детекции: если вложенная политика исчерпала попытки **быстрее**, чем за `openTimeout`, значит сервис падает стабильно – цепь **размыкается** (open). Пока цепь открыта, запросы не уходят вовсе: они быстро падают, не нагружая лежащий ресурс. После `resetTimeout` очередная проверка состояния может сбросить контекст и снова допустить вызов.

```java
CircuitBreakerRetryPolicy circuitBreakerPolicy = new CircuitBreakerRetryPolicy(
    new SimpleRetryPolicy(3)
);
circuitBreakerPolicy.setOpenTimeout(5000);    // исчерпались быстрее 5 сек → разомкнуть
circuitBreakerPolicy.setResetTimeout(20000);  // закрыть цепь снова через 20 сек

RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(circuitBreakerPolicy);

PaymentResult result = template.execute(
    ctx -> gateway.readPaymentStatus(paymentId),
    new DefaultRetryState("payment-status")
);
```

**Чем это хуже полноценного CB.** Это упрощённый вариант для несложных случаев: нет состояния HALF_OPEN (пробного полуоткрытия для проверки, ожил ли сервис), нет метрик и нет Spring Boot auto-configuration. Если CB нужен всерьёз — наблюдаемость, HALF_OPEN, настройка через стартер — берите [Resilience4j](resilience4j-interview.md).

В условии вызов через 1 с не дойдёт до операции, а через 21 с операция снова допустима. В этом варианте отдельные вызовы используют один ключ `DefaultRetryState`, поэтому проверяют одно состояние цепи. Обычный `execute(callback)` создаёт новый контекст на вызов. Повторное открытие доступа проверяется при следующем обращении после интервала, фоновый таймер для этого не нужен. [Исходник CircuitBreakerRetryPolicy 2.0.12](https://github.com/spring-attic/spring-retry/blob/v2.0.12/src/main/java/org/springframework/retry/policy/CircuitBreakerRetryPolicy.java)

## Q9. (!) Как ведёт себя @Retryable при self-invocation?

Потому что `@Retryable` реализован через **Spring AOP proxy**, а внутренний вызов прокси не проходит. Spring оборачивает bean в прокси-объект, и всю retry-логику добавляет именно прокси — снаружи. Когда метод вызывает другой свой же метод через `this.method()`, вызов идёт напрямую по ссылке на реальный объект, минуя прокси. Прокси не видит этот вызов — значит, не видит и исключение, и повторять нечего.

Это ровно та же ловушка, что и с `@Transactional`, и причина у неё одна — proxy-based AOP. Подробнее в [Spring AOP](spring-aop-interview.md).

```java
@Service
public class PaymentService {

    // Клиент вызывает process через Spring-прокси. gateway.charge выбрасывает IOException.
    public void process(Payment p) throws IOException {
        this.retryableMethod(p);
    }

    @Retryable(retryFor = IOException.class)
    public void retryableMethod(Payment p) throws IOException {
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
    public void charge(Payment p) throws IOException { gateway.charge(p); }
}
```

2. **Self-inject через `@Lazy`** — внедрить ссылку на собственный прокси и звать метод через неё, а не через `this`. `@Lazy` нужен, чтобы разорвать циклическую зависимость бина на самого себя:
```java
@Service
public class PaymentService {
    @Autowired @Lazy
    private PaymentService self;

    public void process(Payment p) throws IOException {
        self.retryableMethod(p); // через proxy
    }
}
```

3. **`RetryTemplate`** — вообще не зависит от AOP, так что проблемы self-invocation у него нет в принципе.

## Q10. (!) Как правильно комбинировать @Retryable и @Transactional?

```text
Spring Retry 2.0.x. В одном Spring-бине метод помечен @Retryable и
@Transactional. Вызов идёт через прокси, порядок advice стандартный,
propagation = REQUIRED, внешней транзакции нет. На первом flush
Spring Data выбрасывает OptimisticLockingFailureException, включённый
в retryFor. Какая транзакция используется на второй попытке?
```

Чтобы при каждой попытке заново пройти границу транзакции, retry-interceptor размещают снаружи transaction-interceptor. В Spring Retry 2.0.x порядок `@EnableRetry` по умолчанию равен `Ordered.LOWEST_PRECEDENCE - 1`, а стандартный transaction advice имеет `LOWEST_PRECEDENCE`. При таком порядке, отсутствии внешней транзакции и `REQUIRED` каждая попытка получает собственную транзакцию. Перестановка аннотаций над методом порядок advice не меняет. [Spring Retry @EnableRetry](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/org/springframework/retry/annotation/EnableRetry.html)

При уже активной транзакции `REQUIRED` присоединяется к ней. Ошибка может пометить общую транзакцию rollback-only. Вложенный порядок advice сам по себе проблему не решает. Если по требованиям попытки должны фиксироваться независимо от внешней транзакции, можно использовать `REQUIRES_NEW`. Успешный commit такой попытки переживёт откат внешней транзакции.

Разнесение между двумя бинами задаёт границы явно. Пример предполагает JPA, доступ через прокси для обоих вызовов, отсутствие транзакции у вызывающего и стандартные rollback-правила. Репозиторий в примере возвращает управляемую сущность.

```java
@Service
class RetryingUpdates {
    private final TransactionalUpdates updates;

    RetryingUpdates(TransactionalUpdates updates) {
        this.updates = updates;
    }

    @Retryable(
        retryFor = OptimisticLockingFailureException.class,
        maxAttempts = 3
    )
    public void rename(long id, String name) {
        updates.rename(id, name);
    }
}

@Service
class TransactionalUpdates {
    private final ItemRepository repository;

    TransactionalUpdates(ItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void rename(long id, String name) {
        Item item = repository.findById(id).orElseThrow();
        item.rename(name);
        repository.flush();
    }
}
```

Здесь `ItemRepository` расширяет `JpaRepository<Item, Long>`. Правило повтора соответствует исключению, которое приложение получает после трансляции Spring Data. Загрузка по ID внутри попытки позволяет прочитать текущую версию вместо повторного сохранения того же устаревшего экземпляра. Если приложение получает исключение другого типа, фильтр повтора нужно согласовать с ним.

В штатной конфигурации Spring Framework 7.0 встроенный retry advice также оборачивает transaction advice. Это другой механизм. На него нельзя переносить настройку `@EnableRetry(order = ...)` из библиотеки Spring Retry. [Spring Framework 7.0: порядок advice](https://docs.spring.io/spring-framework/reference/7.0/core/resilience.html#_combining_retryable_with_other_proxy_based_features)

## Q11. Как @Retryable обрабатывает ошибки WebClient в Spring Retry 2.0.x и Framework 7.0?

```text
WebClient возвращает холодный Mono из метода, вызванного через Spring-прокси.
Сам метод возвращается успешно. Первая подписка получает ошибку HTTP 503.
Сравните @Retryable из Spring Retry 2.0.x и из Spring Framework 7.0
при включённом соответствующем механизме и допустимом типе исключения.
```

Аннотация `org.springframework.retry.annotation.Retryable` видит исключение, выброшенное при вызове метода. Если метод вернул `Mono` успешно, а ошибка пришла сигналом при подписке, библиотека Spring Retry этот сигнал не перехватывает. Наличие отдельного потока для этого различия неважно. В Reactor повтор задают через `retryWhen`:

```java
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
        .jitter(0.5)
        .onRetryExhaustedThrow((spec, signal) ->
            new IllegalStateException("All retries exhausted", signal.failure())));
```

Что здесь происходит:
- `Retry.backoff(3, ...)` задаёт до трёх повторных подписок сверх первой. Базовая пауза равна 500 мс и растёт экспоненциально
- **`.filter(...)`** — повторяем только нужные ошибки (здесь `503 Service Unavailable`), остальное пробрасываем сразу
- **`.jitter(0.5)`** — тот же джиттер против thundering herd, что и в Q5, но средствами Reactor
- **`.onRetryExhaustedThrow(...)`** — что пробросить, когда попытки кончились

Встроенный `org.springframework.resilience.annotation.Retryable` Spring Framework 7.0 умеет оборачивать реактивный результат средствами Reactor. Повтор подписки может повторить исходную операцию, поэтому выбор retry для вызовов с побочными эффектами зависит от идемпотентности. Для других политик WebClient можно применять декораторы из `resilience4j-reactor`. [Spring Framework 7.0](https://docs.spring.io/spring-framework/reference/7.0/core/resilience.html) · [Reactor RetryBackoffSpec](https://projectreactor.io/docs/core/release/api/reactor/util/retry/RetryBackoffSpec.html)

## Q12. Что такое Stateful Retry и когда он нужен?

```text
В обработчике ошибка выходит за границу вызова, и транзакция откатывается.
Одна и та же запись затем доставляется повторно в том же процессе.
Известен стабильный ID доставки. Как учитывать попытки между вызовами?
```

Разница между обычным (stateless) и stateful retry — в том, **где живёт счётчик попыток**. У обычного retry весь цикл попыток крутится внутри одного вызова: исключение ловится в том же стеке, и состояние держится в памяти. Stateful retry, наоборот, **сохраняет состояние между отдельными вызовами** — по ключу (`RetryState`), который идентифицирует конкретную единицу работы.

**Зачем это нужно – на примере транзакционного Kafka/JMS listener.** Здесь повтор устроен принципиально иначе: ошибка выходит из обработчика и транзакция откатывается. Повторная доставка зависит от брокера и конфигурации контейнера. Для Kafka контейнер может перечитать записи, например через `DefaultAfterRollbackProcessor`. Но это уже **новый, отдельный вызов** listener-а, а не продолжение предыдущего цикла – обычный stateless retry о прошлых попытках ничего не знает и каждый раз начинает счёт заново. Stateful retry решает проблему: по ключу сообщения он узнаёт, что эту единицу работы уже пытались обработать, и ведёт сквозной счётчик попыток поверх повторных доставок.

```java
// RetryTemplate с stateful retry для Kafka
RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(new SimpleRetryPolicy(3));

// DefaultRetryState с ключом сообщения идентифицирует единицу работы;
// состояние передаётся вторым аргументом execute
template.execute(ctx -> {
    processMessage(message);
    return null;
}, new DefaultRetryState(deliveryId));
```

Используйте стабильный идентификатор единицы работы, например topic, partition и offset для Kafka. Kafka key обычно не уникален для записи. Локальный `RetryContextCache` сам по себе не переносит счётчик между процессами или через перезапуск. Для non-transactional контейнера Spring Kafka есть `DefaultErrorHandler` с `BackOff`. Для транзакционного сценария отдельно настраивают обработку после отката. [Spring Kafka 3.3: обработка ошибок](https://docs.spring.io/spring-kafka/reference/3.3/kafka/annotation-error-handling.html)

## Q13. Как добавить метрики и логирование через RetryListener?

```text
На первом вызове externalApi выбрасывает IOException, на втором возвращает
данные. Требуется записать ошибку попытки и итог всей операции,
используя один постоянный тег и сохраняя существующую политику повтора.
```

`RetryListener` — это хук в жизненный цикл retry: он даёт перехватывать ключевые события, не засоряя бизнес-логику. Это штатное место для сквозных задач — метрик и логирования. Основные коллбэки:
- `open` — перед началом цикла попыток
- `onError` – на каждой неудачной попытке (здесь можно считать ошибки попыток, включая последнюю)
- `onSuccess` перед завершением успешного вызова, с доступом к результату
- `close` – после завершения цикла, успешного или нет (можно измерить длительность и зафиксировать итог)

Все коллбэки у `RetryListener` – default-методы (с spring-retry 2.0), поэтому анонимный класс реализует только нужные; старый базовый класс `RetryListenerSupport` помечен deprecated. Пример считает именно ошибки попыток. Имя контекста инициализируется в `open`, чтобы тег оставался стабильным и был доступен при программном вызове. Для измерения длительности Spring Retry также поставляет `MetricsRetryListener`. [Spring Retry RetryListener](https://docs.spring.io/spring-retry/docs/2.0.12/apidocs/org/springframework/retry/RetryListener.html)

```java
@Bean
public RetryTemplate retryTemplate(MeterRegistry meterRegistry) {
    RetryTemplate template = RetryTemplate.builder()
        .maxAttempts(3)
        .build();

    template.registerListener(new RetryListener() {
        @Override
        public <T, E extends Throwable> boolean open(
                RetryContext ctx, RetryCallback<T, E> cb) {
            ctx.setAttribute(RetryContext.NAME, "external-api");
            return true;
        }

        @Override
        public <T, E extends Throwable> void onError(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            String method = ctx.getAttribute(RetryContext.NAME).toString();
            meterRegistry.counter("retry.attempt.failures", "method", method).increment();
            log.warn("Retry attempt {} for {}: {}", ctx.getRetryCount(), method, t.getMessage());
        }

        @Override
        public <T, E extends Throwable> void close(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            if (ctx.getRetryCount() > 0) {
                log.info("Retry finished with {} failed attempts for {}",
                    ctx.getRetryCount(), ctx.getAttribute(RetryContext.NAME));
            }
        }
    });
    return template;
}
```

## Q14. Как настроить разные retry-политики для разных методов?

```text
Два метода одного Spring-бина в Spring Retry 2.0.x. syncData должен
повторяться при IOException до пяти попыток с растущей паузой.
getQuote должен повторяться при ServiceUnavailableException до двух
попыток с фиксированной паузой. Методы вызываются через прокси.
```

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

Если у большинства методов класса политика одинаковая, её можно вынести на **уровень класса**. Аннотация метода задаёт собственную политику целиком. Например, `@Retryable(maxAttempts = 5)` на методе не наследует классовый `retryFor`, поэтому нужный фильтр следует повторить:
```java
@Service
@Retryable(retryFor = IOException.class, maxAttempts = 3)
public class ExternalApiService {

    public Data fetchData() { ... }      // унаследует class-level retry

    @Retryable(retryFor = IOException.class, maxAttempts = 5)
    public Data fetchCriticalData() { ... }
}
```

## Q15. Что произойдёт, если @Recover выбросит исключение?

```text
Spring Retry 2.0.x. processPayment вызван через прокси, исчерпал попытки
и передал управление подходящему @Recover. Вызванный из него
fallbackService.getFallbackResult выбрасывает IllegalStateException.
Внешней политики повтора нет.
```

В условии клиент получит `IllegalStateException`. Восстановление выполняется после завершения цикла попыток, поэтому исходная retry-политика повторять его не будет.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    return fallbackService.getFallbackResult(req);
}
```

Checked-исключение из recovery может быть обёрнуто при рефлексивном вызове или на границе прокси. Внешняя политика retry, если она есть, оценивает этот вызов отдельно.

## Q16. Как протестировать логику retry в тестах?

```text
Spring Boot 3.5. У PaymentService настроено maxAttempts = 3 и есть
@Recover для IOException. Тестовая зависимость PaymentGateway при каждом
вызове выбрасывает IOException. Нужно проверить число обращений
и результат внедрённого сервиса.
```

Главное, что проверяют на собеседовании: **retry живёт в прокси, поэтому бин нужно поднимать через Spring-контекст**. Создашь `new MyService()` — получишь голый объект без прокси, и `@Retryable` не отработает: повтора не будет, тест проверит совсем не то. Нужен `@SpringBootTest` (или `@SpringJUnitConfig` с включённым AOP), чтобы инжектировался именно прокси.

Типовой подход — замокать зависимость так, чтобы она стабильно падала, и проверить **число вызовов** (что попыток было ровно `maxAttempts`) и срабатывание `@Recover`:

```java
@SpringBootTest
@Import(PaymentServiceTest.NoSleepConfig.class)
class PaymentServiceTest {
    private final PaymentRequest request = new PaymentRequest("test-payment");

    @TestConfiguration
    static class NoSleepConfig {
        @Bean
        Sleeper testSleeper() {
            return millis -> {};
        }
    }


    @Autowired
    PaymentService paymentService;

    @MockitoBean  // бывший @MockBean: устарел с Boot 3.4, удалён в Boot 4
    PaymentGateway gateway;

    @Test
    void shouldMakeThreeAttemptsOnIOException() throws IOException, TimeoutException {
        when(gateway.charge(any())).thenThrow(new IOException("timeout"));

        paymentService.processPayment(request); // после 3 неудач управление уйдёт в @Recover

        verify(gateway, times(3)).charge(any()); // проверяем 3 попытки
    }

    @Test
    void shouldCallRecoverAfterExhaustion() throws IOException, TimeoutException {
        when(gateway.charge(any())).thenThrow(new IOException());

        PaymentResult result = paymentService.processPayment(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo("Service unavailable");
    }
}
```

Тест предполагает `@EnableRetry` в конфигурации приложения, `PaymentService` с политикой Q3, обработчик для `IOException` из Q4 и конструктор тестовой заявки по ID. Единственный тестовый бин `Sleeper` заменяет ожидание, сохраняя работу аннотационного advice. Для отдельного теста программной политики можно вызвать `RetryTemplate` без backoff. [Spring Retry RetryConfiguration 2.0.12](https://github.com/spring-attic/spring-retry/blob/v2.0.12/src/main/java/org/springframework/retry/annotation/RetryConfiguration.java)

## Q17. Чем Spring Retry отличается от Resilience4j?

```text
Нужно повторять временно неудачные вызовы, ограничивать частоту запросов
и изолировать работу внешнего сервиса. Дополнительно команда хочет
проверять восстановление сервиса пробными вызовами после размыкания цепи.
```

Коротко: **Spring Retry даёт повтор и базовый circuit breaker, а Resilience4j добавляет отдельные политики изоляции и ограничения нагрузки**. Если нужен лишь retry в простом приложении – хватит Spring Retry; если строишь устойчивые микросервисы с Circuit Breaker, rate limiting и метриками – берёшь Resilience4j.

| Критерий | Spring Retry | Resilience4j |
|---|---|---|
| Retry | Полноценный | Полноценный |
| Circuit Breaker | Базовый (без HALF_OPEN) | Полноценный (CLOSED/OPEN/HALF_OPEN) |
| Rate Limiter | Нет | Есть |
| Bulkhead | Нет | Есть (ThreadPool + Semaphore) |
| TimeLimiter | Нет | Есть |
| Реактивный стек | Нет | Reactor, RxJava |
| Метрики Micrometer | Через зарегистрированный listener, в том числе `MetricsRetryListener` | Интеграция с Micrometer при соответствующей настройке |
| Spring Boot auto-config | Нет (`@EnableRetry` вручную) | Полная (через starter) |

**Берём Spring Retry, когда:** нужен простой retry, приложение уже использует эту библиотеку, например через Spring Batch 5.x или Spring Integration 6.x, полноценный Circuit Breaker не требуется.

**Берём Resilience4j, когда:** это production-микросервисы, и нужен Circuit Breaker с HALF_OPEN, встроенные метрики и observability, а часто ещё и Rate Limiter с Bulkhead. Подробнее в [Resilience4j](resilience4j-interview.md).

Spring Batch 6.0 и Spring Integration 7.0 перевели встроенные механизмы повторов на API Spring Framework 7.0. При миграции сверяйте импорты, счётчик попыток и способ recovery. [Spring Batch 6.0 Retry](https://docs.spring.io/spring-batch/reference/6.0/retry.html) · [Миграция Spring Integration 7.0](https://docs.spring.io/spring-integration/reference/changes-6.5-7.0.html)

---

## See also

- [Resilience4j](resilience4j-interview.md) — полноценная fault tolerance: CB, RateLimiter, Bulkhead
- [Spring @Transactional](spring-transaction-interview.md) — совместное использование с @Retryable
- [Spring AOP](spring-aop-interview.md) — механизм proxy, self-invocation проблема
- [Spring WebFlux](spring-webflux-interview.md) — реактивный retry через Reactor
- [Spring Testing](spring-testing-interview.md) — тестирование @Retryable в @SpringBootTest
- [Spring Batch](spring-batch-interview.md) – повторы в step и их API в Batch 5 и 6
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики retry через RetryListener + MeterRegistry
- [Distributed Systems](../../architecture/distributed-systems-interview.md) — теория: retry паттерны, idempotency
