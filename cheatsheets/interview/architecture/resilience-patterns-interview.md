---
title: "Вопросы на собеседовании: Паттерны отказоустойчивости"
description: "Ответы по паттернам отказоустойчивости: Circuit Breaker, Retry, Bulkhead, Rate Limiter, Time Limiter, Fallback, Resilience4j, Spring Cloud Circuit Breaker, Hystrix, комбинирование паттернов, метрики, тестирование, chaos engineering."
tags:
  - interview
  - architecture
  - resilience-patterns-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Паттерны отказоустойчивости"
  - "Resilience patterns interview"
  - "Circuit Breaker interview"
prerequisites:
  - "[[resilience-patterns]]"
next: []
updated: "2026-05-05"
---
# Вопросы на собеседовании: `Паттерны отказоустойчивости`

Паттерны отказоустойчивости (`Circuit Breaker`, `Retry`, `Bulkhead`, `Rate Limiter`, `Time Limiter`, `Fallback`) -- ключевые инструменты для построения надёжных распределённых систем. На собеседованиях спрашивают про состояния и переходы `Circuit Breaker`, стратегии повторных попыток, изоляцию ресурсов, ограничение нагрузки, библиотеку `Resilience4j`, комбинирование паттернов, метрики и chaos engineering.

## Полезные ссылки

### Официальная документация

- [Resilience4j Documentation](https://resilience4j.readme.io/docs) -- официальная документация `Resilience4j`
- [Spring Cloud Circuit Breaker](https://docs.spring.io/spring-cloud-circuitbreaker/reference/html/) -- абстракция Spring Cloud над circuit breaker реализациями
- [Resilience4j GitHub](https://github.com/resilience4j/resilience4j) -- исходный код и примеры
- [Guide to Resilience4j (Baeldung)](https://www.baeldung.com/resilience4j) -- подробное руководство по всем модулям
- [Resilience4j with Spring Boot (Baeldung)](https://www.baeldung.com/spring-boot-resilience4j) -- интеграция с Spring Boot
- [Better Retries with Exponential Backoff and Jitter (Baeldung)](https://www.baeldung.com/resilience4j-backoff-jitter) -- стратегии повторных попыток
- [Circuit Breaker vs Retry in Spring Boot (Baeldung)](https://www.baeldung.com/spring-boot-circuit-breaker-vs-retry) -- сравнение Circuit Breaker и Retry
- [Quick Guide to Spring Cloud Circuit Breaker (Baeldung)](https://www.baeldung.com/spring-cloud-circuit-breaker) -- абстракция Spring Cloud над circuit breaker
- [Guide to Spring Retry (Baeldung)](https://www.baeldung.com/spring-retry) -- декларативный retry в Spring

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы отказоустойчивости**
- [Q1. (!) Что такое отказоустойчивость и зачем нужны паттерны отказоустойчивости?](#q1--что-такое-отказоустойчивость-и-зачем-нужны-паттерны-отказоустойчивости)
- [Q2. Какие основные паттерны отказоустойчивости существуют?](#q2-какие-основные-паттерны-отказоустойчивости-существуют)
- [Q3. Чем отличается fault tolerance от fault avoidance?](#q3-чем-отличается-fault-tolerance-от-fault-avoidance)

**Circuit Breaker**
- [Q4. (!) Что такое паттерн Circuit Breaker и какую проблему он решает?](#q4--что-такое-паттерн-circuit-breaker-и-какую-проблему-он-решает)
- [Q5. (!) Какие состояния у Circuit Breaker и как происходят переходы между ними?](#q5--какие-состояния-у-circuit-breaker-и-как-происходят-переходы-между-ними)
- [Q6. Чем отличается count-based sliding window от time-based?](#q6-чем-отличается-count-based-sliding-window-от-time-based)
- [Q7. Какие основные параметры конфигурации Circuit Breaker?](#q7-какие-основные-параметры-конфигурации-circuit-breaker)
- [Q8. Что такое специальные состояния Circuit Breaker: DISABLED, FORCED_OPEN, METRICS_ONLY?](#q8-что-такое-специальные-состояния-circuit-breaker-disabled-forced_open-metrics_only)
- [Q9. (!) Как реализовать Circuit Breaker с помощью Resilience4j?](#q9--как-реализовать-circuit-breaker-с-помощью-resilience4j)

**Retry**
- [Q10. (!) Что такое паттерн Retry и когда его применять?](#q10--что-такое-паттерн-retry-и-когда-его-применять)
- [Q11. (!) Какие стратегии backoff существуют для повторных попыток?](#q11--какие-стратегии-backoff-существуют-для-повторных-попыток)
- [Q12. Что такое jitter и зачем он нужен в стратегии retry?](#q12-что-такое-jitter-и-зачем-он-нужен-в-стратегии-retry)
- [Q13. Как настроить Retry в Resilience4j?](#q13-как-настроить-retry-в-resilience4j)

**Bulkhead**
- [Q14. (!) Что такое паттерн Bulkhead и какую проблему он решает?](#q14--что-такое-паттерн-bulkhead-и-какую-проблему-он-решает)
- [Q15. (!) Чем отличается Thread Pool Bulkhead от Semaphore Bulkhead?](#q15--чем-отличается-thread-pool-bulkhead-от-semaphore-bulkhead)
- [Q16. Как настроить Bulkhead в Resilience4j?](#q16-как-настроить-bulkhead-в-resilience4j)

**Rate Limiter**
- [Q17. (!) Что такое Rate Limiter и какие алгоритмы используются?](#q17--что-такое-rate-limiter-и-какие-алгоритмы-используются)
- [Q18. Как настроить Rate Limiter в Resilience4j?](#q18-как-настроить-rate-limiter-в-resilience4j)

**Time Limiter и Fallback**
- [Q19. Что такое Time Limiter и зачем он нужен?](#q19-что-такое-time-limiter-и-зачем-он-нужен)
- [Q20. (!) Что такое Fallback и как его правильно реализовать?](#q20--что-такое-fallback-и-как-его-правильно-реализовать)

**Resilience4j**
- [Q21. (!) Что такое Resilience4j и чем он лучше Netflix Hystrix?](#q21--что-такое-resilience4j-и-чем-он-лучше-netflix-hystrix)
- [Q22. Какие модули входят в Resilience4j?](#q22-какие-модули-входят-в-resilience4j)
- [Q23. Как интегрировать Resilience4j со Spring Boot?](#q23-как-интегрировать-resilience4j-со-spring-boot)

**Комбинирование паттернов**
- [Q24. (!) В каком порядке применяются паттерны отказоустойчивости?](#q24--в-каком-порядке-применяются-паттерны-отказоустойчивости)
- [Q25. Как комбинировать Circuit Breaker и Retry?](#q25-как-комбинировать-circuit-breaker-и-retry)
- [Q26. Как комбинировать несколько паттернов с помощью аннотаций Resilience4j?](#q26-как-комбинировать-несколько-паттернов-с-помощью-аннотаций-resilience4j)

**Spring Cloud Circuit Breaker и Hystrix**
- [Q27. Что такое Spring Cloud Circuit Breaker?](#q27-что-такое-spring-cloud-circuit-breaker)
- [Q28. Почему Netflix Hystrix считается устаревшим?](#q28-почему-netflix-hystrix-считается-устаревшим)

**Метрики, мониторинг и тестирование**
- [Q29. (!) Как мониторить паттерны отказоустойчивости?](#q29--как-мониторить-паттерны-отказоустойчивости)
- [Q30. Как тестировать паттерны отказоустойчивости?](#q30-как-тестировать-паттерны-отказоустойчивости)

**Отказоустойчивость в распределённых системах**
- [Q31. Что такое graceful degradation?](#q31-что-такое-graceful-degradation)
- [Q32. (!) Что такое chaos engineering и зачем он нужен?](#q32--что-такое-chaos-engineering-и-зачем-он-нужен)
- [Q33. Как проектировать идемпотентные retry-операции?](#q33-как-проектировать-идемпотентные-retry-операции)

**Продвинутые темы**
- [Q34. (!) Как организовать health checks для Kubernetes liveness/readiness проб?](#q34--как-организовать-health-checks-для-kubernetes-livenessreadiness-проб)
- [Q35. Что такое graceful shutdown и как его реализовать в Spring Boot?](#q35-что-такое-graceful-shutdown-и-как-его-реализовать-в-spring-boot)
- [Q36. (!) Что такое Hedged Request и когда его применять?](#q36--что-такое-hedged-request-и-когда-его-применять)

**TimeLimiter, Bulkhead-детали и деградация**
- [Q37. Как настроить TimeLimiter в Resilience4j и чем он отличается от таймаута WebClient?](#q37-как-настроить-timelimiter-в-resilience4j-и-чем-он-отличается-от-таймаута-webclient)
- [Q38. ThreadPoolBulkhead vs SemaphoreBulkhead — когда что выбрать на практике?](#q38-threadpoolbulkhead-vs-semaphorebulkhead--когда-что-выбрать-на-практике)
- [Q39. Какие стратегии Fallback существуют и как выбрать подходящую?](#q39-какие-стратегии-fallback-существуют-и-как-выбрать-подходящую)
- [Q40. Что такое Load Shedding и как реализовать приоритизацию запросов?](#q40-что-такое-load-shedding-и-как-реализовать-приоритизацию-запросов)
- [Q41. Как интегрировать Circuit Breaker с Spring Boot Actuator Health?](#q41-как-интегрировать-circuit-breaker-с-spring-boot-actuator-health)
- [Q42. Какие инструменты Chaos Engineering применяются для Spring Boot?](#q42-какие-инструменты-chaos-engineering-применяются-для-spring-boot)
- [Q43. Как реализовать graceful degradation с помощью Feature Flags?](#q43-как-реализовать-graceful-degradation-с-помощью-feature-flags)

---

## Q1. (!) Что такое отказоустойчивость и зачем нужны паттерны отказоустойчивости?

**Отказоустойчивость** (fault tolerance) -- способность системы продолжать корректно работать, даже когда отдельные её компоненты отказали.

Паттерны отказоустойчивости нужны потому, что в распределённой системе сбои -- не исключение, а норма: сеть ненадёжна, сервисы перегружаются, базы данных тормозят. Главная опасность -- не сам по себе единичный отказ, а **каскадный сбой** (cascading failure): отказ одного компонента "заваливает" все зависимые сервисы по цепочке, и падает вся система целиком, хотя сломалось лишь одно звено.

Как разворачивается каскадный сбой по цепочке вызовов:

- `Клиент` → `API Gateway` → `Сервис A` → `Сервис B` ❌ (отказал).
- `Сервис A` упирается в недоступный `Сервис B`: таймаут, потоки заняты → сбой `Сервиса A`.
- Каскадный эффект распространяется дальше: сбой `Сервиса A` → сбой `Gateway`.

Каждый паттерн закрывает свой класс проблем, а вместе они решают три ключевые задачи:

1. **Предотвращение каскадных сбоев** -- `Circuit Breaker` изолирует неработающий сервис, чтобы ожидание его ответа не утянуло за собой вызывающую сторону.
2. **Восстановление при временных ошибках** -- `Retry` повторяет операцию при транзиентных сбоях, которые проходят сами (моргнула сеть, сервис на секунду перегрузился).
3. **Защита ресурсов** -- `Bulkhead` и `Rate Limiter` не дают одному потребителю или одной медленной зависимости исчерпать все потоки и соединения приложения.

## Q2. Какие основные паттерны отказоустойчивости существуют?

| Паттерн | Назначение | Аналогия |
|---------|-----------|----------|
| `Circuit Breaker` | Прерывает вызовы к неработающему сервису | Автомат в электрощитке |
| `Retry` | Повторяет неудавшуюся операцию | Перезвонить, если абонент не ответил |
| `Bulkhead` | Изолирует ресурсы между потребителями | Водонепроницаемые отсеки корабля |
| `Rate Limiter` | Ограничивает частоту запросов | Турникет в метро |
| `Time Limiter` | Ограничивает время выполнения операции | Таймер на экзамене |
| `Fallback` | Предоставляет альтернативный ответ при сбое | Запасной выход |
| `Cache` | Возвращает кешированный результат при сбое | Копия документа |

Подробнее про каждый паттерн можно прочитать в [вопросах по распределённым системам](distributed-systems-interview.md).

## Q3. Чем отличается fault tolerance от fault avoidance?

Это два дополняющих друг друга подхода к надёжности. **Fault avoidance** старается не допустить ошибок вообще (качественный код, тесты, ревью). **Fault tolerance** исходит из того, что сбои всё равно случатся, и заранее проектирует систему так, чтобы пережить их с минимальными последствиями.

| Характеристика | Fault Tolerance | Fault Avoidance |
|---------------|----------------|-----------------|
| **Подход** | Допускаем сбои, но минимизируем их последствия | Стараемся предотвратить сбои |
| **Философия** | "Сбои неизбежны" (Design for Failure) | "Пишем идеальный код" |
| **Инструменты** | `Circuit Breaker`, `Retry`, `Bulkhead` | Code review, тестирование, статический анализ |
| **Применимость** | Обязательно в распределённых системах | Везде, но недостаточно для распределённых систем |

В [микросервисной архитектуре](microservices-interview.md) одного `fault avoidance` недостаточно: количество точек отказа растёт вместе с числом сервисов и связей между ними, и какая-то из них рано или поздно откажет. Поэтому `fault tolerance` здесь не опция, а обязательное требование.

## Q4. (!) Что такое паттерн Circuit Breaker и какую проблему он решает?

**Circuit Breaker** (предохранитель) -- паттерн, который предотвращает каскадные сбои: после серии ошибок он "размыкает цепь" и перестаёт обращаться к неработающему сервису, давая ему время восстановиться.

**Проблема без `Circuit Breaker`** -- упавшая зависимость тянет за собой и вызывающую сторону:
- Сервис B не отвечает (упал или перегружен).
- Сервис A продолжает слать запросы и ждать таймаут (например, 30 сек).
- Потоки сервиса A блокируются ожиданием ответа.
- Пул потоков исчерпывается -- сервис A тоже перестаёт отвечать.
- Каскадный эффект распространяется по всей цепочке вызовов.

Корень проблемы в том, что слать запросы заведомо нерабочему сервису бессмысленно: они всё равно упадут по таймауту, но успеют занять ресурсы.

**С `Circuit Breaker`** -- бессмысленные вызовы отсекаются на стороне клиента:
- После N неудачных вызовов "предохранитель срабатывает" (открывается).
- Последующие вызовы **мгновенно завершаются с ошибкой** (fail fast), не обращаясь к сервису B и не занимая поток на ожидание.
- Через заданное время пропускается несколько "пробных" запросов, чтобы проверить, восстановился ли сервис.
- Если пробные запросы успешны -- circuit закрывается, и работа возобновляется.

Паттерн назван по аналогии с электрическим автоматом: при коротком замыкании автомат размыкает цепь, предотвращая пожар. Здесь точно так же -- размыкание цепи вызовов защищает систему от "возгорания".

## Q5. (!) Какие состояния у Circuit Breaker и как происходят переходы между ними?

`Circuit Breaker` -- это конечный автомат с тремя основными состояниями. Логика проста: пока всё хорошо -- пропускаем трафик; когда ошибок стало слишком много -- блокируем его на время; затем осторожно проверяем, не пора ли вернуться к нормальной работе.

Переходы автомата (начальное состояние -- `CLOSED`):

- `CLOSED` → `OPEN`: порог ошибок превышен (`failureRateThreshold`).
- `OPEN` → `HALF_OPEN`: истёк `waitDurationInOpenState`.
- `HALF_OPEN` → `CLOSED`: пробные вызовы успешны (доля ошибок ниже порога).
- `HALF_OPEN` → `OPEN`: пробные вызовы провалились (порог ошибок превышен).

### Состояния:

**`CLOSED`** (замкнут -- нормальная работа): трафик идёт сквозь предохранитель к сервису.
- Все запросы проходят к целевому сервису.
- Результаты (успех/ошибка) записываются в скользящее окно (sliding window).
- Как только процент ошибок в окне превышает `failureRateThreshold` -- переход в `OPEN`.

**`OPEN`** (разомкнут -- защита): цепь разорвана, сервис изолирован.
- Все запросы **немедленно отклоняются** с исключением `CallNotPermittedException` -- реальный вызов не выполняется.
- Это и даёт эффект fail fast: клиент мгновенно получает ошибку вместо ожидания таймаута, а перегруженный сервис -- передышку.
- Через `waitDurationInOpenState` (по умолчанию 60 сек) -- переход в `HALF_OPEN`.

**`HALF_OPEN`** (полуоткрыт -- разведка боем): осторожная проверка, ожил ли сервис.
- Пропускается ограниченное число пробных запросов (`permittedNumberOfCallsInHalfOpenState`).
- Если процент ошибок среди них ниже порога -- сервис восстановился, переход в `CLOSED`.
- Если процент ошибок выше порога -- сервис всё ещё нездоров, возврат в `OPEN` и новое ожидание.

## Q6. Чем отличается count-based sliding window от time-based?

`Circuit Breaker` считает процент ошибок не по всей истории, а по **скользящему окну** последних вызовов. Разница между двумя типами окна -- в том, чем измеряется его размер: количеством вызовов или временем.

- **Count-based** -- "последние N вызовов". Окно сдвигается по мере поступления запросов. Подходит, когда нагрузка стабильна: N вызовов набираются за предсказуемое время.
- **Time-based** -- "вызовы за последние N секунд". Окно сдвигается по часам, независимо от трафика. Подходит при переменной нагрузке: даже редкие вызовы не "застревают" в окне надолго.

| Характеристика | Count-based | Time-based |
|---------------|-------------|------------|
| **Что считает** | Последние N вызовов | Вызовы за последние N секунд |
| **Параметр** | `slidingWindowSize` = число вызовов | `slidingWindowSize` = секунды |
| **Структура данных** | Кольцевой буфер (ring buffer) | Кольцевой буфер из N partial aggregation buckets |
| **Когда подходит** | Стабильная нагрузка | Переменная нагрузка |
| **Пример** | "Последние 100 вызовов" | "Вызовы за последние 60 секунд" |

```yaml
# Count-based (по умолчанию)
resilience4j.circuitbreaker:
  instances:
    paymentService:
      slidingWindowType: COUNT_BASED
      slidingWindowSize: 100
      failureRateThreshold: 50

# Time-based
resilience4j.circuitbreaker:
  instances:
    paymentService:
      slidingWindowType: TIME_BASED
      slidingWindowSize: 60  # секунд
      failureRateThreshold: 50
```

**Подводный камень**: `Circuit Breaker` не начнёт вычислять процент ошибок, пока не наберётся минимальное число вызовов (`minimumNumberOfCalls`, по умолчанию 100). Без этого порога одна-единственная ошибка при двух запросах дала бы 50% и сразу открыла предохранитель -- защита от таких ложных срабатываний на малом трафике и есть смысл параметра.

## Q7. Какие основные параметры конфигурации Circuit Breaker?

Параметры делятся на три смысловые группы: **когда открывать** (`failureRateThreshold`, `slowCallRateThreshold`, `slowCallDurationThreshold`), **на чём считать** (`slidingWindow*`, `minimumNumberOfCalls`) и **как восстанавливаться** (`waitDurationInOpenState`, `permittedNumberOfCallsInHalfOpenState`). Отдельно стоят `recordExceptions`/`ignoreExceptions` -- они определяют, какие исключения вообще считаются "ошибкой".

| Параметр | По умолчанию | Описание |
|----------|-------------|----------|
| `failureRateThreshold` | 50 (%) | Порог ошибок для перехода в `OPEN` |
| `slowCallRateThreshold` | 100 (%) | Порог медленных вызовов для перехода в `OPEN` |
| `slowCallDurationThreshold` | 60000 мс | Время, после которого вызов считается медленным |
| `slidingWindowType` | `COUNT_BASED` | Тип скользящего окна |
| `slidingWindowSize` | 100 | Размер окна (вызовы или секунды) |
| `minimumNumberOfCalls` | 100 | Мин. количество вызовов до расчёта процента |
| `waitDurationInOpenState` | 60000 мс | Время ожидания в `OPEN` перед переходом в `HALF_OPEN` |
| `permittedNumberOfCallsInHalfOpenState` | 10 | Число пробных вызовов в `HALF_OPEN` |
| `automaticTransitionFromOpenToHalfOpenEnabled` | false | Автоматический переход `OPEN` -> `HALF_OPEN` |
| `recordExceptions` | пусто | Исключения, считающиеся ошибками |
| `ignoreExceptions` | пусто | Исключения, которые не учитываются |

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)
    .slowCallRateThreshold(80)
    .slowCallDurationThreshold(Duration.ofSeconds(2))
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .slidingWindowType(SlidingWindowType.COUNT_BASED)
    .slidingWindowSize(10)
    .minimumNumberOfCalls(5)
    .permittedNumberOfCallsInHalfOpenState(3)
    .recordExceptions(IOException.class, TimeoutException.class)
    .ignoreExceptions(BusinessException.class)
    .build();

CircuitBreaker circuitBreaker = CircuitBreaker.of("paymentService", config);
```

## Q8. Что такое специальные состояния Circuit Breaker: DISABLED, FORCED_OPEN, METRICS_ONLY?

Помимо трёх основных состояний (`CLOSED`/`OPEN`/`HALF_OPEN`), которыми автомат управляет сам, `Resilience4j` `CircuitBreaker` поддерживает три специальных -- их включают **вручную** для отладки, эксплуатации или поэтапного внедрения. В этих состояниях автоматические переходы по порогам не работают.

| Состояние | Поведение | Применение |
|-----------|----------|------------|
| `DISABLED` | Все вызовы проходят, метрики не записываются | Отключение CB для отладки |
| `FORCED_OPEN` | Все вызовы отклоняются | Ручное отключение сервиса |
| `METRICS_ONLY` | Все вызовы проходят, метрики записываются, но CB никогда не открывается | Наблюдение перед включением |

`METRICS_ONLY` особенно полезен при внедрении `Circuit Breaker` на продакшне: можно сначала включить его в режиме наблюдения, собрать метрики, подобрать пороги, и только потом переключить в рабочий режим.

```java
// Принудительный переход в специальное состояние
circuitBreaker.transitionToDisabledState();
circuitBreaker.transitionToForcedOpenState();
circuitBreaker.transitionToMetricsOnlyState();
```

## Q9. (!) Как реализовать Circuit Breaker с помощью Resilience4j?

Есть два пути. **Программный API** -- ручное создание `CircuitBreaker` и декорирование вызова; даёт полный контроль, нужен вне Spring или для нестандартной логики. **Аннотации Spring Boot** (`@CircuitBreaker`) -- декларативный путь: конфигурация выносится в `application.yml`, а fallback задаётся методом. На практике в Spring-приложениях используют именно аннотации.

### Программный API:

```java
// 1. Создаём CircuitBreaker с конфигурацией
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .slidingWindowSize(10)
    .build();

CircuitBreaker circuitBreaker = CircuitBreaker.of("paymentService", config);

// 2. Декорируем вызов
Supplier<PaymentResponse> decoratedSupplier = CircuitBreaker
    .decorateSupplier(circuitBreaker, () -> paymentClient.processPayment(request));

// 3. Выполняем с fallback
Try<PaymentResponse> result = Try.ofSupplier(decoratedSupplier)
    .recover(CallNotPermittedException.class, e -> PaymentResponse.unavailable())
    .recover(Exception.class, e -> PaymentResponse.error(e.getMessage()));
```

### Аннотации Spring Boot:

```java
@Service
public class PaymentService {

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        return paymentClient.call(request);
    }

    private PaymentResponse paymentFallback(PaymentRequest request, Exception ex) {
        log.warn("Circuit breaker fallback for payment: {}", ex.getMessage());
        return PaymentResponse.unavailable();
    }
}
```

### Конфигурация в `application.yml`:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

## Q10. (!) Что такое паттерн Retry и когда его применять?

**Retry** -- паттерн, который автоматически повторяет неудавшуюся операцию заданное число раз. Ключевая идея: применять его только к **транзиентным ошибкам** -- кратковременным сбоям, которые проходят сами, поэтому повтор имеет шанс на успех.

**Когда применять** (ошибка временная -- повтор скорее всего пройдёт):
- Сетевые таймауты.
- HTTP 503 (Service Unavailable).
- HTTP 429 (Too Many Requests).
- Временная недоступность базы данных.
- Блокировки (deadlock) при конкурентном доступе.

**Когда НЕ применять** (повтор бесполезен или вреден -- причина не временная):
- Ошибки бизнес-логики и валидации (400 Bad Request, 422) -- те же входные данные дадут тот же отказ.
- Ошибки аутентификации/авторизации (401, 403) -- повтор не добавит прав.
- Длительные аварии -- здесь повторы только добивают лежащий сервис; нужен `Circuit Breaker`.

Эмпирическое правило: повторять стоит только то, что имеет шанс выполниться при следующей попытке. Если ответ детерминированно "нет" -- повтор лишь множит нагрузку.

```java
@Retry(name = "inventoryService", fallbackMethod = "inventoryFallback")
public InventoryResponse checkInventory(String sku) {
    return inventoryClient.getStock(sku);
}
```

**Важно**: операция, оборачиваемая в `Retry`, должна быть **идемпотентной** -- повторный вызов не должен создавать дублирующий эффект (подробнее в [вопросах по распределённым системам](distributed-systems-interview.md)).

## Q11. (!) Какие стратегии backoff существуют для повторных попыток?

**Backoff** -- это правило, по которому растёт пауза между повторами. Смысл паузы: дать перегруженному сервису время восстановиться, а не долбить его повторами вплотную. Чем агрессивнее растёт пауза, тем бережнее retry относится к сервису. На практике стандарт -- экспоненциальный backoff с jitter (пункты 3 и 4 ниже).

### 1. Fixed interval (фиксированный интервал)

Одинаковая задержка между попытками. Просто, но при массовом сбое все клиенты повторяют синхронно и держат сервис под постоянным давлением.

```
Попытка 1 → [1 сек] → Попытка 2 → [1 сек] → Попытка 3
```

### 2. Exponential backoff (экспоненциальная задержка)

Интервал растёт экспоненциально: `initialInterval * multiplier^(attempt-1)`. С каждой попыткой давление на сервис падает -- это даёт ему всё больше времени прийти в себя.

```
Попытка 1 → [1 сек] → Попытка 2 → [2 сек] → Попытка 3 → [4 сек] → Попытка 4
```

### 3. Exponential backoff with jitter (с рандомизацией)

К экспоненциальной задержке добавляется случайное отклонение, чтобы избежать "стадного эффекта" (thundering herd).

```
Попытка 1 → [1.2 сек] → Попытка 2 → [2.7 сек] → Попытка 3 → [3.9 сек]
```

### 4. Linear backoff (линейный рост)

Интервал растёт линейно.

```
Попытка 1 → [1 сек] → Попытка 2 → [2 сек] → Попытка 3 → [3 сек]
```

```java
// Resilience4j: Exponential backoff with jitter
RetryConfig config = RetryConfig.custom()
    .maxAttempts(4)
    .intervalFunction(IntervalFunction.ofExponentialBackoff(
        Duration.ofMillis(500),  // initialInterval
        2.0,                      // multiplier
        Duration.ofSeconds(30)    // maxInterval
    ))
    .retryExceptions(IOException.class, TimeoutException.class)
    .ignoreExceptions(BusinessException.class)
    .build();

// С jitter (рандомизация)
RetryConfig configWithJitter = RetryConfig.custom()
    .maxAttempts(4)
    .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
        500,   // initialIntervalMillis
        2.0,   // multiplier
        0.5    // randomizationFactor
    ))
    .build();
```

## Q12. Что такое jitter и зачем он нужен в стратегии retry?

**Jitter** -- добавление случайного отклонения к задержке между повторными попытками. Нужен, чтобы повторы разных клиентов не синхронизировались и не били по сервису волнами.

### Проблема без jitter (thundering herd, "стадный эффект"):

Сам по себе exponential backoff не спасает, если клиенты стартовали синхронно. Допустим, 1000 клиентов одновременно получили ошибку и используют одинаковый backoff:
- Через 1 сек -- все 1000 клиентов повторяют запрос одновременно.
- Через 2 сек -- все снова бьют разом.
- Каждая волна снова перегружает сервис ровно в тот момент, когда он пытается встать -- восстановление не наступает.

### С jitter:

Запросы "размазываются" по времени, что даёт сервису возможность восстановиться:

```
Клиент A: 0.8 сек → 2.1 сек → 4.5 сек
Клиент B: 1.2 сек → 1.9 сек → 3.8 сек  
Клиент C: 0.9 сек → 2.5 сек → 4.1 сек
```

Формула с jitter: `delay = baseDelay * multiplier^attempt * (1 + random(-factor, +factor))`

В `Resilience4j` jitter реализуется через `IntervalFunction.ofExponentialRandomBackoff()`:

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
        enableRandomizedWait: true
        randomizedWaitFactor: 0.5
```

## Q13. Как настроить Retry в Resilience4j?

Ключевые настройки: `maxAttempts` (сколько всего попыток, включая первую), `waitDuration` и стратегия backoff, а также фильтры исключений (`retryExceptions`/`ignoreExceptions`) и условие по результату (`retryOnResult`). Как и `Circuit Breaker`, `Retry` конфигурируется программно или через аннотацию `@Retry` + `application.yml`.

### Программный API:

```java
RetryConfig config = RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .retryOnResult(response -> response.getStatus() == 503)
    .retryExceptions(IOException.class, TimeoutException.class)
    .ignoreExceptions(BusinessValidationException.class)
    .failAfterMaxAttempts(true)
    .build();

Retry retry = Retry.of("paymentService", config);

// Декорирование вызова
Supplier<PaymentResponse> decoratedSupplier = Retry.decorateSupplier(
    retry, () -> paymentClient.processPayment(request)
);

PaymentResponse result = decoratedSupplier.get();
```

### Аннотации Spring Boot:

```java
@Service
public class PaymentService {

    @Retry(name = "paymentService", fallbackMethod = "retryFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        return paymentClient.call(request);
    }

    private PaymentResponse retryFallback(PaymentRequest request, Exception ex) {
        log.warn("All retries exhausted: {}", ex.getMessage());
        return PaymentResponse.error("Service temporarily unavailable");
    }
}
```

### Конфигурация в `application.yml`:

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        ignoreExceptions:
          - com.example.BusinessException
```

## Q14. (!) Что такое паттерн Bulkhead и какую проблему он решает?

**Bulkhead** (переборка) -- паттерн изоляции ресурсов: он ограничивает число одновременных вызовов к каждой зависимости, чтобы одна медленная зависимость не выела все потоки приложения и не утянула за собой остальных.

Название пришло из кораблестроения: корпус разделён на **водонепроницаемые отсеки** (bulkheads), и пробоина в одном отсеке не топит весь корабль. Здесь та же идея -- каждый сервис получает свой "отсек" ресурсов, и затопление одного не распространяется на другие.

Проблема, которую он решает: по умолчанию все исходящие вызовы делят один общий пул потоков. Стоит одной зависимости начать отвечать по 30 секунд -- и все потоки уходят на ожидание именно её, а вызовы ко всем остальным (исправным) сервисам встают в очередь и тоже начинают падать.

**Без `Bulkhead`** -- все запросы делят один общий пул потоков (200):

- Все запросы → общий пул потоков (200) → `Сервис A`, `Сервис B` ❌ (медленный), `Сервис C`.
- Все 200 потоков заняты ожиданием `Сервиса B` → `Сервис C` становится недоступен (потоков для него не остаётся).

**С `Bulkhead`** -- у каждого сервиса свой отдельный пул:

- Все запросы → пул A (50 потоков) → `Сервис A` ✅.
- Все запросы → пул B (50 потоков) → `Сервис B` ❌.
- Все запросы → пул C (50 потоков) → `Сервис C` ✅.

Без `Bulkhead`: медленный Сервис B может занять все потоки, и сервисы A и C тоже станут недоступны. С `Bulkhead`: каждый сервис получает свою "квоту" ресурсов, и проблемы одного не влияют на другие.

## Q15. (!) Чем отличается Thread Pool Bulkhead от Semaphore Bulkhead?

`Resilience4j` предлагает две реализации `Bulkhead`, и различие у них принципиальное -- в том, **в каком потоке выполняется вызов**.

- **Semaphore Bulkhead** -- просто счётчик (семафор) разрешений. Вызов идёт в потоке вызывающего кода; семафор лишь не пускает больше N параллельных вызовов. Дёшево, без переключения потоков -- но изоляции потоков нет.
- **Thread Pool Bulkhead** -- выделяет зависимости отдельный пул потоков. Вызов уходит в этот пул, поэтому медленная зависимость "съедает" только свои потоки и не может занять чужие. Цена -- overhead на context switch и возврат только через `CompletableFuture`.

| Характеристика | Semaphore Bulkhead | Thread Pool Bulkhead |
|---------------|-------------------|---------------------|
| **Механизм** | Ограничивает число параллельных вызовов через семафор | Выделяет отдельный пул потоков |
| **Изоляция** | Вызовы выполняются в потоке вызывающего кода | Вызовы выполняются в отдельном пуле |
| **Overhead** | Минимальный (без переключения потоков) | Есть overhead на context switch |
| **Возврат** | Синхронный (тот же тип возврата) | Только `CompletableFuture` |
| **Очередь ожидания** | `maxWaitDuration` -- сколько ждать разрешения | `queueCapacity` -- размер очереди задач |
| **Когда использовать** | Реактивные/неблокирующие вызовы | Блокирующие вызовы (JDBC, HTTP) |

### Semaphore Bulkhead:

```java
BulkheadConfig config = BulkheadConfig.custom()
    .maxConcurrentCalls(25)
    .maxWaitDuration(Duration.ofMillis(500))
    .build();

Bulkhead bulkhead = Bulkhead.of("paymentService", config);
```

### Thread Pool Bulkhead:

```java
ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
    .maxThreadPoolSize(10)
    .coreThreadPoolSize(5)
    .queueCapacity(20)
    .keepAliveDuration(Duration.ofMillis(100))
    .build();

ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("paymentService", config);
```

При превышении лимита `Semaphore Bulkhead` выбрасывает `BulkheadFullException`, а `Thread Pool Bulkhead` -- `BulkheadFullException` из очереди, если она переполнена.

## Q16. Как настроить Bulkhead в Resilience4j?

Тип задаётся параметром `type` аннотации (`SEMAPHORE` или `THREADPOOL`), а параметры -- в `application.yml`, причём для каждого типа свой раздел: `bulkhead` для семафорного и `thread-pool-bulkhead` для пулового.

### Аннотация:

```java
@Bulkhead(name = "paymentService", fallbackMethod = "bulkheadFallback",
          type = Bulkhead.Type.SEMAPHORE) // или THREADPOOL
public PaymentResponse processPayment(PaymentRequest request) {
    return paymentClient.call(request);
}

private PaymentResponse bulkheadFallback(PaymentRequest request, BulkheadFullException ex) {
    return PaymentResponse.tooManyRequests();
}
```

### Конфигурация в `application.yml`:

```yaml
resilience4j:
  bulkhead:
    instances:
      paymentService:
        maxConcurrentCalls: 25
        maxWaitDuration: 500ms
  thread-pool-bulkhead:
    instances:
      paymentService:
        maxThreadPoolSize: 10
        coreThreadPoolSize: 5
        queueCapacity: 20
        keepAliveDuration: 100ms
```

## Q17. (!) Что такое Rate Limiter и какие алгоритмы используются?

**Rate Limiter** -- паттерн, ограничивающий количество запросов за единицу времени. Важно не путать его с `Bulkhead`: `Bulkhead` ограничивает число **одновременных** вызовов (сколько "в полёте" прямо сейчас), а `Rate Limiter` -- **частоту** вызовов (сколько штук за период), независимо от того, завершились ли предыдущие.

### Основные алгоритмы:

Алгоритмы отличаются тем, как они "нарезают" время и как относятся к всплескам (burst):

**1. Fixed Window (фиксированное окно)**
- Делит время на фиксированные интервалы (например, 1 минута)
- Считает запросы в текущем окне
- Минус: скачок на границе окон (99 запросов в конце одного окна + 100 в начале следующего = 199 за 2 сек)

**2. Sliding Window (скользящее окно)**
- Использует скользящее окно для более точного подсчёта
- Устраняет проблему границ фиксированного окна
- Более ресурсоёмкий

**3. Token Bucket**
- Токены добавляются в "ведро" с фиксированной скоростью
- Каждый запрос потребляет один токен
- Если токенов нет -- запрос отклоняется или ждёт
- Позволяет "всплески" (burst) при накопленных токенах

**4. Leaky Bucket**
- Запросы добавляются в очередь фиксированного размера
- Обрабатываются с постоянной скоростью
- Сглаживает всплески нагрузки

`Resilience4j` `RateLimiter` использует вариант **fixed window** с возможностью ожидания: вызывающий поток может подождать разрешения в течение `timeoutDuration`.

## Q18. Как настроить Rate Limiter в Resilience4j?

### Программный API:

```java
RateLimiterConfig config = RateLimiterConfig.custom()
    .limitForPeriod(10)              // макс. 10 вызовов за период
    .limitRefreshPeriod(Duration.ofSeconds(1))  // период обновления
    .timeoutDuration(Duration.ofMillis(500))     // сколько ждать разрешения
    .build();

RateLimiter rateLimiter = RateLimiter.of("apiService", config);

Supplier<Response> decoratedSupplier = RateLimiter
    .decorateSupplier(rateLimiter, () -> apiClient.call());
```

### Аннотация:

```java
@RateLimiter(name = "apiService", fallbackMethod = "rateLimitFallback")
public Response callExternalApi(Request request) {
    return apiClient.call(request);
}

private Response rateLimitFallback(Request request, RequestNotPermitted ex) {
    return Response.tooManyRequests("Rate limit exceeded, try again later");
}
```

### Конфигурация в `application.yml`:

```yaml
resilience4j:
  ratelimiter:
    instances:
      apiService:
        limitForPeriod: 10
        limitRefreshPeriod: 1s
        timeoutDuration: 500ms
        registerHealthIndicator: true
        eventConsumerBufferSize: 50
```

При превышении лимита выбрасывается `RequestNotPermitted`.

## Q19. Что такое Time Limiter и зачем он нужен?

**Time Limiter** -- паттерн, ограничивающий время выполнения операции: если она не завершилась за отведённый срок, вызов прерывается с `TimeoutException`. Смысл -- не дать одной зависшей операции держать поток и ресурсы бесконечно.

**Зачем он нужен, если есть таймауты HTTP-клиента** -- `Time Limiter` работает на уровне бизнес-операции, а не транспорта:
- Единая политика таймаутов на уровне операции, а не на каждый HTTP-клиент по отдельности.
- Работает с любым типом вызова (БД, очередь, расчёт), а не только с HTTP.
- Естественно комбинируется с другими паттернами `Resilience4j` и даёт метрики/события.

```java
TimeLimiterConfig config = TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(3))
    .cancelRunningFuture(true)  // отменить Future при таймауте
    .build();

TimeLimiter timeLimiter = TimeLimiter.of("paymentService", config);
```

### Аннотация:

```java
@TimeLimiter(name = "paymentService", fallbackMethod = "timeoutFallback")
public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
    return CompletableFuture.supplyAsync(() -> paymentClient.call(request));
}

private CompletableFuture<PaymentResponse> timeoutFallback(
        PaymentRequest request, TimeoutException ex) {
    return CompletableFuture.completedFuture(PaymentResponse.timeout());
}
```

```yaml
resilience4j:
  timelimiter:
    instances:
      paymentService:
        timeoutDuration: 3s
        cancelRunningFuture: true
```

**Важно**: `@TimeLimiter` работает только с `CompletableFuture` или реактивными типами. Для синхронных вызовов используйте таймауты HTTP-клиента или `ExecutorService`.

## Q20. (!) Что такое Fallback и как его правильно реализовать?

**Fallback** -- запасной ответ, который возвращается при сбое основного вызова вместо проброса ошибки наверх. Это ключевой элемент graceful degradation: вместо "всё сломалось" пользователь получает упрощённый, но рабочий результат.

Главный вопрос при проектировании fallback -- **что отдать вместо реального ответа**. Варианты ранжируются по тому, насколько они близки к настоящим данным.

### Типы fallback:

**1. Значение по умолчанию:**
```java
private ProductResponse productFallback(String id, Exception ex) {
    return ProductResponse.builder()
        .id(id)
        .name("Товар временно недоступен")
        .price(BigDecimal.ZERO)
        .available(false)
        .build();
}
```

**2. Кэшированное значение:**
```java
private ProductResponse cachedFallback(String id, Exception ex) {
    return productCache.getIfPresent(id); // Guava Cache, Redis, etc.
}
```

**3. Вызов альтернативного сервиса:**
```java
private ProductResponse alternativeServiceFallback(String id, Exception ex) {
    return backupProductService.getProduct(id);
}
```

**4. Пустой ответ (для некритичных данных):**
```java
private List<Recommendation> recommendationsFallback(String userId, Exception ex) {
    return Collections.emptyList(); // страница отобразится без рекомендаций
}
```

### Правила реализации fallback в Resilience4j:

- Fallback-метод должен иметь **ту же сигнатуру** плюс `Exception` (или конкретный тип исключения) как последний параметр
- Должен быть в **том же классе** (или родительском)
- Может быть несколько fallback-ов для разных типов исключений

```java
@CircuitBreaker(name = "payment", fallbackMethod = "paymentFallback")
public PaymentResponse pay(PaymentRequest req) { ... }

// Специфичный fallback для таймаутов
private PaymentResponse paymentFallback(PaymentRequest req, TimeoutException ex) {
    return PaymentResponse.timeout();
}

// Общий fallback для остальных ошибок
private PaymentResponse paymentFallback(PaymentRequest req, Exception ex) {
    return PaymentResponse.unavailable();
}
```

## Q21. (!) Что такое Resilience4j и чем он лучше Netflix Hystrix?

**Resilience4j** -- легковесная библиотека отказоустойчивости для Java в функциональном стиле (паттерны навешиваются декораторами, без наследования). Пришла на замену `Netflix Hystrix`, который перешёл в maintenance mode в 2018 году.

Главные преимущества кратко: модульность (берёшь только нужные паттерны), минимум зависимостей (только `Vavr`), нативная интеграция со Spring Boot и Micrometer, поддержка реактивного стека и обоих типов `Bulkhead`. Hystrix же завязан на наследование `HystrixCommand`, тянет тяжёлые `Archaius`/`RxJava 1` и больше не развивается.

| Характеристика | Resilience4j | Netflix Hystrix |
|---------------|-------------|-----------------|
| **Статус** | Активно развивается | Maintenance mode с 2018 |
| **Зависимости** | Только `Vavr` | `Archaius`, `RxJava`, `HystrixCommand` |
| **Подход** | Функциональный (декораторы) | Наследование (`HystrixCommand`) |
| **Модульность** | Отдельные модули (бери только нужное) | Монолит |
| **Spring Boot** | Нативная поддержка (стартер) | Через Spring Cloud Netflix |
| **Конфигурация** | `application.yml` + Java Config | Archaius properties |
| **Метрики** | Micrometer (Prometheus, Grafana) | Hystrix Dashboard |
| **Bulkhead** | Semaphore + Thread Pool | Только Thread Pool |
| **Reactive** | Поддержка `Reactor`, `RxJava2/3` | Только `RxJava 1` |
| **Java version** | Java 17+ (v2.x) | Java 8 |

Spring Cloud Netflix Hystrix был удалён из Spring Cloud 2020.x, и `Resilience4j` стал рекомендуемой заменой.

## Q22. Какие модули входят в Resilience4j?

Главная идея модульности: каждый паттерн -- отдельный артефакт, и тянуть всю библиотеку не нужно. Условно модули делятся на **сами паттерны** (CircuitBreaker, Retry, Bulkhead, RateLimiter, TimeLimiter, Cache), **интеграции со Spring/метриками** (Spring Boot Starter, Micrometer) и **поддержку реактивных стеков** (Reactor, RxJava3).

| Модуль | Артефакт | Назначение |
|--------|---------|-----------|
| `CircuitBreaker` | `resilience4j-circuitbreaker` | Предохранитель |
| `Retry` | `resilience4j-retry` | Повторные попытки |
| `Bulkhead` | `resilience4j-bulkhead` | Изоляция ресурсов |
| `RateLimiter` | `resilience4j-ratelimiter` | Ограничение частоты |
| `TimeLimiter` | `resilience4j-timelimiter` | Ограничение по времени |
| `Cache` | `resilience4j-cache` | Кэширование результатов |
| `Spring Boot Starter` | `resilience4j-spring-boot3` | Автоконфигурация для Spring Boot 3 |
| `Micrometer` | `resilience4j-micrometer` | Интеграция с Micrometer для метрик |
| `Reactor` | `resilience4j-reactor` | Поддержка Project Reactor |
| `RxJava3` | `resilience4j-rxjava3` | Поддержка RxJava 3 |

Модули можно подключать независимо -- нет необходимости тянуть всю библиотеку:

```groovy
// build.gradle
dependencies {
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-micrometer:2.2.0'
    // отдельные модули, если без Spring Boot
    implementation 'io.github.resilience4j:resilience4j-circuitbreaker:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-retry:2.2.0'
}
```

## Q23. Как интегрировать Resilience4j со Spring Boot?

Интеграция в четыре шага: подключить стартер (и обязательно `spring-boot-starter-aop` -- без него аннотации не работают), описать инстансы в `application.yml`, навесить аннотации на методы и при желании смотреть состояние через Actuator-эндпоинты.

### 1. Подключить зависимость:

```groovy
// build.gradle (Spring Boot 3)
dependencies {
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
    implementation 'org.springframework.boot:spring-boot-starter-aop' // обязательно для аннотаций
    implementation 'org.springframework.boot:spring-boot-starter-actuator' // для метрик
}
```

### 2. Настроить в `application.yml`:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
  retry:
    instances:
      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
  bulkhead:
    instances:
      paymentService:
        maxConcurrentCalls: 25
```

### 3. Использовать аннотации:

```java
@Service
@Slf4j
public class PaymentService {

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
    @Retry(name = "paymentService")
    @Bulkhead(name = "paymentService")
    public PaymentResponse processPayment(PaymentRequest request) {
        return paymentClient.call(request);
    }

    private PaymentResponse fallback(PaymentRequest request, Exception ex) {
        log.warn("Fallback triggered: {}", ex.getMessage());
        return PaymentResponse.unavailable();
    }
}
```

### 4. Actuator endpoints:

```
GET /actuator/circuitbreakers      -- состояния всех CB
GET /actuator/circuitbreakerevents -- события CB
GET /actuator/retries              -- состояния retry
GET /actuator/retryevents          -- события retry
GET /actuator/bulkheads            -- состояния bulkhead
GET /actuator/ratelimiters         -- состояния rate limiter
```

## Q24. (!) В каком порядке применяются паттерны отказоустойчивости?

Когда на одном методе висит несколько аннотаций, важно, в какой последовательности они оборачивают вызов. В `Resilience4j` порядок аспектов фиксирован (от внешнего слоя к внутреннему):

```
Retry → CircuitBreaker → RateLimiter → TimeLimiter → Bulkhead → Function
```

Читать цепочку нужно "снаружи внутрь": `Retry` оборачивает всё остальное, а `Bulkhead` стоит ближе всех к самому вызову. Если смотреть от функции наружу, слои применяются так:
1. **`Bulkhead`** -- ближе всех к вызову: проверяет, есть ли свободный слот; если лимит исчерпан, дальше внутрь не идём.
2. **`TimeLimiter`** -- ограничивает время выполнения.
3. **`RateLimiter`** -- ограничивает частоту вызовов.
4. **`CircuitBreaker`** -- проверяет, не открыт ли circuit, и записывает результат в окно.
5. **`Retry`** -- самый внешний слой: оборачивает всю цепочку и при ошибке повторяет её целиком.

**Почему такой порядок важен:**
- `Retry` снаружи `CircuitBreaker` -- повторная попытка может увидеть, что circuit уже открыт, и сразу получить `CallNotPermittedException`
- `Bulkhead` внутри -- каждая retry-попытка проверяет наличие свободных ресурсов
- Если нужен другой порядок, используйте программный API с явным декорированием

```java
// Кастомный порядок через программный API
Supplier<Response> decorated = Decorators.ofSupplier(() -> service.call())
    .withBulkhead(bulkhead)
    .withCircuitBreaker(circuitBreaker)
    .withRetry(retry)
    .decorate();
```

## Q25. Как комбинировать Circuit Breaker и Retry?

Комбинация `Circuit Breaker` + `Retry` -- самая распространённая, и на собеседовании любят спрашивать про их взаимодействие. Связка работает так: `Retry` повторяет вызов, а `Circuit Breaker` следит, не пора ли вообще прекратить попытки. Каждая попытка retry проходит сквозь circuit и учитывается в его статистике.

Поток взаимодействия `Client` → `Retry` → `CircuitBreaker` → `Service` по шагам:

1. `Client` → `Retry`: запрос.
2. `Retry` → `CircuitBreaker`: попытка 1.
3. `CircuitBreaker` → `Service`: вызов (CB `CLOSED`).
4. `Service` → `CircuitBreaker`: ошибка 500.
5. `CircuitBreaker` → `Retry`: `IOException`.
6. `Retry` → `CircuitBreaker`: попытка 2 (после backoff).
7. `CircuitBreaker` → `Service`: вызов (CB `CLOSED`).
8. `Service` → `CircuitBreaker`: OK 200.
9. `CircuitBreaker` → `Retry`: успех.
10. `Retry` → `Client`: результат.

**Ключевые моменты:**

1. Каждая retry-попытка **регистрируется** в `CircuitBreaker` -- если CB открылся на попытке 2, оставшиеся retry получат `CallNotPermittedException`
2. `Retry` **не должен** повторять при `CallNotPermittedException` -- circuit открыт не просто так:

```java
RetryConfig retryConfig = RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .ignoreExceptions(CallNotPermittedException.class) // не повторять, если CB открыт
    .retryExceptions(IOException.class, TimeoutException.class)
    .build();
```

3. Суммарная нагрузка: если `maxAttempts=3` и таких клиентов много, то нагрузка на сервис утраивается при сбоях. `CircuitBreaker` решает эту проблему, отсекая лишние вызовы.

## Q26. Как комбинировать несколько паттернов с помощью аннотаций Resilience4j?

Несколько аннотаций просто навешиваются на один метод -- они применяются в фиксированном порядке из Q24. Тонкость в едином fallback: в него прилетает исключение от того слоя, который сработал первым, поэтому внутри полезно разветвлять ответ по типу исключения (`CallNotPermittedException` -- circuit открыт, `RequestNotPermitted` -- сработал rate limiter, `BulkheadFullException` -- кончились слоты).

```java
@Service
public class OrderService {

    @Retry(name = "orderService", fallbackMethod = "orderFallback")
    @CircuitBreaker(name = "orderService")
    @RateLimiter(name = "orderService")
    @Bulkhead(name = "orderService", type = Bulkhead.Type.SEMAPHORE)
    public OrderResponse createOrder(OrderRequest request) {
        return orderClient.create(request);
    }

    private OrderResponse orderFallback(OrderRequest request, Exception ex) {
        if (ex instanceof CallNotPermittedException) {
            return OrderResponse.serviceUnavailable();
        }
        if (ex instanceof RequestNotPermitted) {
            return OrderResponse.tooManyRequests();
        }
        if (ex instanceof BulkheadFullException) {
            return OrderResponse.systemOverloaded();
        }
        return OrderResponse.error(ex.getMessage());
    }
}
```

### Конфигурация для всех паттернов:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      orderService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
  retry:
    instances:
      orderService:
        maxAttempts: 3
        waitDuration: 500ms
        ignoreExceptions:
          - io.github.resilience4j.circuitbreaker.CallNotPermittedException
  ratelimiter:
    instances:
      orderService:
        limitForPeriod: 50
        limitRefreshPeriod: 1s
        timeoutDuration: 0
  bulkhead:
    instances:
      orderService:
        maxConcurrentCalls: 20
        maxWaitDuration: 0
```

## Q27. Что такое Spring Cloud Circuit Breaker?

**Spring Cloud Circuit Breaker** -- это абстракция (единый API) поверх разных реализаций circuit breaker. Аналогия: как `SLF4J` отвязывает код от конкретного логгера, так Spring Cloud CB отвязывает код от конкретной библиотеки -- сменить её можно, не трогая бизнес-логику.

Поддерживаемые реализации:
- **Resilience4j** (рекомендуемая)
- **Spring Retry**
- **Sentinel** (Alibaba)

```java
// Spring Cloud Circuit Breaker API
@Service
public class PaymentService {

    private final CircuitBreakerFactory circuitBreakerFactory;

    public PaymentResponse processPayment(PaymentRequest request) {
        return circuitBreakerFactory.create("paymentService")
            .run(
                () -> paymentClient.call(request),           // основной вызов
                throwable -> PaymentResponse.unavailable()    // fallback
            );
    }
}
```

### Настройка с Resilience4j backend:

```java
@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id ->
            new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                    .slidingWindowSize(10)
                    .failureRateThreshold(50)
                    .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(3))
                    .build())
                .build()
        );
    }
}
```

**Когда использовать Spring Cloud CB vs Resilience4j напрямую:**
- `Spring Cloud CB` -- если нужна возможность смены реализации
- `Resilience4j` напрямую -- если нужен полный набор паттернов (`Retry`, `Bulkhead`, `RateLimiter`), которые не входят в абстракцию Spring Cloud CB

## Q28. Почему Netflix Hystrix считается устаревшим?

`Netflix Hystrix` был пионером `Circuit Breaker` в Java, но в ноябре 2018 года Netflix перевёл его в **maintenance mode** -- новых фич не будет, только критические правки. Причина не в одной поломке, а в том, что и сам подход, и его реализация устарели.

**Причины перехода:**
1. **Netflix перешёл на адаптивный подход** -- вместо статических порогов используется adaptive concurrency limits
2. **Архитектурные ограничения** -- `HystrixCommand` требует наследования, что плохо ложится на функциональный стиль
3. **Зависимость от Archaius** -- тяжёлая библиотека конфигурации
4. **Только Thread Pool изоляция** -- нет Semaphore Bulkhead в полном смысле
5. **RxJava 1** -- устаревшая версия реактивных расширений

**Путь миграции:**
- `HystrixCommand` -> аннотации `@CircuitBreaker` / `@Retry` / `@Bulkhead` `Resilience4j`
- `HystrixDashboard` -> Micrometer + Prometheus + Grafana
- `HystrixRequestCache` -> `resilience4j-cache`
- `Hystrix Thread Pool` -> `ThreadPoolBulkhead` или `Semaphore Bulkhead`

Spring Cloud Netflix Hystrix был **удалён** из Spring Cloud начиная с версии `2020.0` (Ilford).

## Q29. (!) Как мониторить паттерны отказоустойчивости?

Мониторинг строится на двух источниках: **метрики** (`Resilience4j` отдаёт их через Micrometer в Prometheus -- для дашбордов и алертов) и **Actuator-эндпоинты** (для просмотра текущего состояния и событий в реальном времени). Главное, за чем следить, -- состояние circuit breaker'ов, доля ошибок и заполненность bulkhead'ов.

### Метрики Resilience4j через Micrometer:

```groovy
dependencies {
    implementation 'io.github.resilience4j:resilience4j-micrometer:2.2.0'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

### Ключевые метрики Circuit Breaker:

| Метрика | Описание |
|---------|----------|
| `resilience4j_circuitbreaker_state` | Текущее состояние (0=CLOSED, 1=OPEN, 2=HALF_OPEN) |
| `resilience4j_circuitbreaker_calls_seconds_count` | Количество вызовов |
| `resilience4j_circuitbreaker_calls_seconds_sum` | Суммарное время вызовов |
| `resilience4j_circuitbreaker_failure_rate` | Текущий процент ошибок |
| `resilience4j_circuitbreaker_not_permitted_calls_total` | Отклонённые вызовы (CB открыт) |

### Ключевые метрики Retry, Bulkhead, Rate Limiter:

| Метрика | Описание |
|---------|----------|
| `resilience4j_retry_calls_total` | Вызовы с указанием kind (successful_without_retry, successful_with_retry, failed_with_retry, failed_without_retry) |
| `resilience4j_bulkhead_available_concurrent_calls` | Свободные слоты в Bulkhead |
| `resilience4j_ratelimiter_available_permissions` | Доступные разрешения Rate Limiter |

### Пример Grafana dashboard:

Настройте алерты на:
- `resilience4j_circuitbreaker_state == 1` (OPEN) -- circuit breaker открылся
- `resilience4j_circuitbreaker_failure_rate > 30` -- растёт процент ошибок
- `resilience4j_bulkhead_available_concurrent_calls == 0` -- bulkhead заполнен
- `resilience4j_retry_calls_total{kind="failed_with_retry"}` растёт -- ретраи не помогают

### Spring Boot Actuator endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: circuitbreakers, circuitbreakerevents, retries, retryevents,
                 bulkheads, ratelimiters, health
  health:
    circuitbreakers:
      enabled: true
```

## Q30. Как тестировать паттерны отказоустойчивости?

Тестирование идёт на трёх уровнях. **Unit-тесты** проверяют логику самого паттерна на маленьких порогах (например, что circuit открывается после N ошибок). **Интеграционные тесты** с WireMock эмулируют поведение реальной зависимости -- ошибки, медленные ответы -- и проверяют, что retry и fallback срабатывают на полном Spring-стеке. Отдельно стоит проверка **порядка** срабатывания паттернов.

### Unit-тесты Circuit Breaker:

```java
@Test
void shouldOpenCircuitAfterFailures() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .slidingWindowSize(5)
        .minimumNumberOfCalls(5)
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(10))
        .build();

    CircuitBreaker circuitBreaker = CircuitBreaker.of("test", config);

    // Декорируем функцию, которая всегда бросает исключение
    Supplier<String> failingSupplier = CircuitBreaker.decorateSupplier(
        circuitBreaker, () -> { throw new RuntimeException("Fail"); }
    );

    // Генерируем 5 ошибок
    for (int i = 0; i < 5; i++) {
        Try.ofSupplier(failingSupplier);
    }

    // Circuit должен открыться
    assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

    // Следующий вызов должен быть отклонён
    assertThatThrownBy(failingSupplier::get)
        .isInstanceOf(CallNotPermittedException.class);
}
```

### Интеграционные тесты с WireMock:

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class PaymentServiceResilienceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void shouldRetryOnServerError() {
        // Первые 2 вызова -- 500, третий -- 200
        stubFor(get("/api/payment/123")
            .inScenario("retry")
            .whenScenarioStateIs(STARTED)
            .willReturn(serverError())
            .willSetStateTo("SECOND_CALL"));

        stubFor(get("/api/payment/123")
            .inScenario("retry")
            .whenScenarioStateIs("SECOND_CALL")
            .willReturn(serverError())
            .willSetStateTo("THIRD_CALL"));

        stubFor(get("/api/payment/123")
            .inScenario("retry")
            .whenScenarioStateIs("THIRD_CALL")
            .willReturn(okJson("{\"status\":\"OK\"}")));

        PaymentResponse response = paymentService.getPayment("123");

        assertThat(response.getStatus()).isEqualTo("OK");
        verify(3, getRequestedFor(urlEqualTo("/api/payment/123")));
    }

    @Test
    void shouldUseFallbackWhenCircuitOpen() {
        // Принудительно открываем circuit
        circuitBreakerRegistry.circuitBreaker("paymentService")
            .transitionToForcedOpenState();

        PaymentResponse response = paymentService.getPayment("123");

        assertThat(response.getStatus()).isEqualTo("UNAVAILABLE");
    }
}
```

### Тестирование порядка паттернов:

Используйте `@SpyBean` для верификации, что паттерны срабатывают в правильном порядке, и `RegistryEventConsumer` для отслеживания событий.

## Q31. Что такое graceful degradation?

**Graceful degradation** (плавная деградация) -- подход, при котором система продолжает работать с пониженной функциональностью, а не падает целиком из-за отказа одной её части. Идея: пользователь скорее простит отсутствие рекомендаций, чем недоступность всего сайта.

### Примеры:

| Сервис | Полная функциональность | Деградированный режим |
|--------|------------------------|-----------------------|
| Рекомендации | Персональные рекомендации ML | Популярные товары из кэша |
| Поиск | Полнотекстовый с фасетами | Поиск по названию из базы |
| Оплата | Онлайн-оплата | "Оплата при получении" |
| Рейтинги | Актуальные рейтинги | Рейтинги из последнего снапшота |
| Корзина | Персистентная корзина | Корзина в сессии/localStorage |

### Реализация с Resilience4j:

```java
@CircuitBreaker(name = "recommendations", fallbackMethod = "degradedRecommendations")
public List<Product> getRecommendations(String userId) {
    return mlRecommendationService.getPersonalized(userId);
}

private List<Product> degradedRecommendations(String userId, Exception ex) {
    log.warn("ML recommendations unavailable, falling back to popular items");
    return popularProductsCache.getTopProducts(10);
}
```

Graceful degradation -- это не только технический паттерн, но и **продуктовое решение**: нужно заранее определить, какие части системы критичны, а какие можно временно упростить. Подробнее о проектировании отказоустойчивых систем в [вопросах по распределённым системам](distributed-systems-interview.md).

## Q32. (!) Что такое chaos engineering и зачем он нужен?

**Chaos engineering** -- дисциплина, в которой сбои вносят в систему **намеренно и контролируемо**, чтобы найти скрытые слабые места раньше, чем их найдёт реальная авария. Логика парадоксальна, но верна: чтобы быть уверенным в устойчивости, надо регулярно ломать систему самому. Термин популяризирован Netflix (инструмент `Chaos Monkey`).

### Принципы chaos engineering:

1. **Определите "нормальное поведение"** -- базовые метрики (latency, error rate, throughput)
2. **Сформулируйте гипотезу** -- "если упадёт сервис X, fallback вернёт кэшированные данные"
3. **Внедрите сбой** -- убейте инстанс, добавьте latency, заполните диск
4. **Сравните с гипотезой** -- система повела себя как ожидалось?
5. **Минимизируйте blast radius** -- начинайте с минимального воздействия

### Инструменты:

| Инструмент | Уровень | Описание |
|-----------|---------|----------|
| `Chaos Monkey for Spring Boot` | Приложение | Латентность, исключения в Spring-бинах |
| `Litmus Chaos` | Kubernetes | Удаление подов, сетевые разрывы |
| `Gremlin` | Инфраструктура | Коммерческое решение, широкий спектр сбоев |
| `Toxiproxy` | Сеть | Эмуляция сетевых проблем (latency, packet loss) |

### Chaos Monkey for Spring Boot:

```groovy
dependencies {
    implementation 'de.codecentric:chaos-monkey-spring-boot:3.1.0'
}
```

```yaml
chaos:
  monkey:
    enabled: true
    assaults:
      level: 5           # каждый 5-й вызов
      latencyActive: true
      latencyRangeStart: 1000
      latencyRangeEnd: 5000
      exceptionsActive: true
    watcher:
      service: true       # наблюдать за @Service бинами
      restController: true
```

Chaos engineering верифицирует, что ваши `Circuit Breaker`, `Retry`, `Bulkhead` и `Fallback` действительно работают в реальных условиях, а не только в unit-тестах.

## Q33. Как проектировать идемпотентные retry-операции?

**Идемпотентность** -- свойство операции, при котором повторный вызов с теми же параметрами даёт тот же результат и не создаёт лишних побочных эффектов. Без неё `Retry` опасен: он может превратить один успешный заказ в два.

### Проблема:

Коварство в том, что retry не отличает "не выполнилось" от "выполнилось, но ответ потерялся". Первый вызов мог **успешно отработать на сервере**, а сбой произошёл уже при передаче ответа клиенту. Клиент видит ошибку, повторяет запрос -- и создаёт дубликат.

```
Клиент → Сервер: "Создай заказ"
Сервер: создаёт заказ #123 ✅
Сервер → Клиент: ответ "OK" ❌ (сетевой сбой)
Клиент → Сервер: Retry "Создай заказ"
Сервер: создаёт заказ #124 ← ДУБЛИКАТ!
```

### Решения:

**1. Idempotency Key:**
```java
@PostMapping("/orders")
public ResponseEntity<Order> createOrder(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody OrderRequest request) {

    // Проверяем, не обработан ли уже этот ключ
    return orderRepository.findByIdempotencyKey(idempotencyKey)
        .map(ResponseEntity::ok)
        .orElseGet(() -> {
            Order order = orderService.create(request, idempotencyKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        });
}
```

**2. Используйте идемпотентные HTTP-методы** где возможно:
- `GET`, `PUT`, `DELETE` -- идемпотентны по спецификации
- `POST` -- не идемпотентен, требует `Idempotency-Key`

**3. Проверяй-и-записывай (Check-and-Set):**
```java
// Вместо UPDATE balance = balance - 100
// Используем оптимистичную блокировку
UPDATE accounts SET balance = balance - 100, version = version + 1
WHERE id = ? AND version = ?
```

**4. Естественные идемпотентные операции:**
- `SET status = 'PAID'` -- идемпотентно (повтор ничего не меняет)
- `INCREMENT counter` -- НЕ идемпотентно (повтор увеличит дважды)

Подробнее об идемпотентности в [вопросах по распределённым системам](distributed-systems-interview.md).

## Q34. (!) Как организовать health checks для Kubernetes liveness/readiness проб?

**Health checks** -- механизм, которым приложение сообщает Kubernetes о своём состоянии, чтобы оркестратор знал, что с подом делать. Ключевое -- понимать разницу между пробами: каждая отвечает на свой вопрос и провоцирует своё действие.

- **Liveness probe** (`/actuator/health/liveness`) -- жив ли процесс? Провал → Kubernetes перезапускает pod (лечение через рестарт).
- **Readiness probe** (`/actuator/health/readiness`) -- готов ли принимать трафик? Провал → под исключается из балансировки, но **не** перезапускается (временно недоступен, скоро вернётся).
- **Startup probe** -- приложение ещё стартует? Блокирует liveness/readiness, пока запуск не завершён, чтобы медленный старт не приняли за зависание и не убили pod.

Главное не перепутать liveness и readiness: рестарт не лечит отвалившуюся БД, а исключение из балансировки бесполезно при deadlock внутри процесса.

**Spring Boot Actuator + Kubernetes:**

```yaml
# application.yml
management:
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
  endpoint:
    health:
      probes:
        enabled: true
      show-details: always
```

```yaml
# Kubernetes deployment.yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 3

startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30    # 30 * 10s = 300s для slow startup
  periodSeconds: 10
```

**Кастомный HealthIndicator:**

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            conn.isValid(1);  // 1 second timeout
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("response", "ok")
                .build();
        } catch (SQLException e) {
            return Health.down()
                .withException(e)
                .withDetail("database", "unreachable")
                .build();
        }
    }
}
```

**Разница liveness vs readiness:**

Жизненный цикл пода и переходы между состояниями:

- `Pod started` → `Starting`.
- `Starting` → `Ready`: startup probe прошёл.
- `Ready` → `NotReady`: readiness fails (потеряно соединение с БД).
- `NotReady` → `Ready`: readiness восстановился.
- `Ready` → `Restarting`: liveness fails (обнаружен deadlock).
- `Restarting` → `Starting`: контейнер перезапущен.

Пометки к состояниям:

- `NotReady`: трафик не идёт, под не убивают.
- `Restarting`: Kubernetes перезапускает контейнер.

**Правило:** liveness проверяет только состояние самого процесса (нет deadlock, нет OOM), readiness — зависимости (БД, кэш, downstream-сервисы).

## Q35. Что такое graceful shutdown и как его реализовать в Spring Boot?

**Graceful shutdown** -- завершение приложения, при котором оно сначала дорабатывает текущие запросы и аккуратно закрывает соединения, а не обрывается на полуслове. Особенно важно при деплое и масштабировании, когда поды перезапускаются постоянно.

**Проблема без graceful shutdown** -- гонка между остановкой пода и обновлением балансировщика:
```
SIGTERM →  Kubernetes отправляет сигнал завершения
           Балансировщик ещё маршрутизирует трафик на этот pod
           Pod убивается мгновенно → HTTP 502 у клиентов
```

**Spring Boot 2.3+ — встроенная поддержка:**

```yaml
# application.yml
server:
  shutdown: graceful  # ожидает завершения обработки текущих запросов

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # максимальное время ожидания
```

**Kubernetes + preStop hook для задержки:**

```yaml
# deployment.yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 5"]  # ждём 5с пока LB обновит endpoints
terminationGracePeriodSeconds: 60       # суммарное время на завершение
```

**Последовательность graceful shutdown в Spring Boot** (`Kubernetes` → `Spring Boot App`, параллельно с `Load Balancer`):

1. `Kubernetes` → `App`: `SIGTERM`.
2. `Kubernetes` → `Load Balancer`: убрать pod из endpoints.
3. `App`: preStop hook (`sleep 5s`).
4. `App`: отклоняет новые запросы (`readiness=DOWN`).
5. `App`: дорабатывает in-flight запросы (максимум 30s).
6. `App`: закрывает соединения с БД и consumers.
7. `App` → `Kubernetes`: процесс завершился (код 0).

**Кастомные shutdown hooks для Kafka-потребителей:**

```java
@Component
public class KafkaConsumerShutdown {

    private final KafkaListenerEndpointRegistry registry;

    @EventListener(ContextClosingEvent.class)
    public void onShutdown() {
        // Останавливаем Kafka-потребителей до закрытия Spring context
        registry.getListenerContainers()
            .forEach(container -> container.stop(() ->
                log.info("Kafka consumer stopped: {}", container.getListenerId())));
    }
}
```

## Q36. (!) Что такое Hedged Request и когда его применять?

**Hedged Request** (застрахованный запрос) -- паттерн борьбы с хвостовыми задержками (tail latency). Идея: не ждать медленный инстанс до конца, а подстраховаться -- если ответа нет за пороговое время, отправить такой же запрос на другой инстанс и взять тот ответ, что придёт первым. Второй запрос отменяется.

**Проблема, которую он решает** -- редкие, но болезненные "хвосты": p99-латентность в 5-10 раз превышает медиану из-за GC-пауз, перегрузки отдельных инстансов, сетевых флуктуаций. Большинству запросов быстро, но невезучим -- очень медленно, и именно они портят SLA. Hedged request "страхует" клиента от попадания на конкретный затормозивший инстанс.

Поток hedged-запроса (`Client`, `Service Instance 1` -- медленный, `Service Instance 2`):

1. `Client` → `Service Instance 1`: запрос.
2. Пометка: ждём 50ms...
3. `Client` → `Service Instance 2`: hedged-запрос (если `Service Instance 1` не ответил).
4. `Service Instance 2` → `Client`: ответ (200ms) -- победитель.
5. `Client` → `Service Instance 1`: cancel (если `Service Instance 1` ещё обрабатывает).

**Реализация с `CompletableFuture` и `ExecutorService`:**

```java
@Service
public class HedgedProductService {

    private final ProductServiceClient client;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public CompletableFuture<Product> getProduct(Long id) {
        CompletableFuture<Product> primary = CompletableFuture.supplyAsync(
            () -> client.getProduct(id));

        // Через 50мс (hedge delay) запускаем второй запрос
        CompletableFuture<Product> hedged = new CompletableFuture<>();
        ScheduledFuture<?> hedgeTask = scheduler.schedule(
            () -> CompletableFuture.supplyAsync(() -> client.getProduct(id))
                .whenComplete((r, e) -> {
                    if (e == null) hedged.complete(r);
                    else hedged.completeExceptionally(e);
                }),
            50, TimeUnit.MILLISECONDS);

        // Берём первый успешный ответ
        return CompletableFuture.anyOf(primary, hedged)
            .thenApply(r -> (Product) r)
            .whenComplete((r, e) -> hedgeTask.cancel(true));
    }
}
```

**Когда применять:**

| Условие | Хедж нужен | Хедж не нужен |
|---------|-----------|---------------|
| p99 >> p50 (хвостовые задержки) | Да | — |
| Запрос идемпотентный (GET, read-only) | Да | — |
| Запрос создаёт побочные эффекты | — | Нет (риск дублирования) |
| Нагрузка близка к пределу | — | Нет (удвоит нагрузку) |
| Нужна предсказуемая задержка | Да | — |

**Google SRE рекомендует:** hedge delay = p95 нормальной латентности. Дополнительная нагрузка обычно < 5% при hedge delay ≥ p95.

---

## Q37. Как настроить TimeLimiter в Resilience4j и чем он отличается от таймаута WebClient?

**`TimeLimiter`** в `Resilience4j` оборачивает вызов в `Future` и через `ScheduledExecutorService` отменяет его, если тот не уложился в `timeoutDuration`, бросая `TimeoutException`.

Кажется, что таймаут `WebClient` делает то же самое, но это разные уровни: `WebClient` ограничивает HTTP-транспорт, а `TimeLimiter` -- логическую операцию целиком, давая при этом метрики, события и fallback.

### Отличие от таймаута WebClient:

| Характеристика | TimeLimiter (Resilience4j) | WebClient timeout |
|---|---|---|
| **Область применения** | Любой блокирующий/асинхронный вызов | Только HTTP-запросы |
| **Метрики** | Интегрирован с Micrometer | Нет |
| **Fallback** | Поддерживает через `@TimeLimiter(fallbackMethod)` | Нужен `onErrorResume` |
| **События** | `onSuccess`, `onError`, `onTimeout` | Нет |
| **Комбинирование** | Легко с CB / Retry | Отдельный pipe |

`WebClient` таймаут срабатывает на уровне TCP/HTTP и не прерывает серверную обработку. `TimeLimiter` прерывает `Future` на клиенте, но если вызов блокирующий — поток всё равно занят до ответа сервера. Поэтому для блокирующих HTTP-клиентов (`RestTemplate`, `Feign`) правильнее использовать оба: таймаут клиента + `TimeLimiter` как дополнительный safety net.

### Конфигурация:

```java
@TimeLimiter(name = "externalService", fallbackMethod = "timeoutFallback")
@CircuitBreaker(name = "externalService")
public CompletableFuture<Response> callExternalService(Request request) {
    return CompletableFuture.supplyAsync(() -> client.call(request));
}

private CompletableFuture<Response> timeoutFallback(Request request, TimeoutException ex) {
    return CompletableFuture.completedFuture(Response.cached());
}
```

```yaml
resilience4j:
  timelimiter:
    instances:
      externalService:
        timeoutDuration: 2s          # Максимальное время ожидания
        cancelRunningFuture: true    # Прерывать Future при таймауте
```

**Важно:** `@TimeLimiter` требует, чтобы метод возвращал `CompletableFuture` или `Mono`/`Flux` (реактивный стек). Для синхронных методов нужно обернуть вручную.

---

## Q38. ThreadPoolBulkhead vs SemaphoreBulkhead — когда что выбрать на практике?

Если Q15 объяснял механику различий, то здесь -- практический критерий выбора. Главный вопрос один: **блокирующий вызов или нет**. Блокирующий код (JDBC, `RestTemplate`, `Feign`) держит поток занятым, поэтому ему нужна изоляция отдельным пулом -- `ThreadPoolBulkhead`. Неблокирующий реактивный код потоки почти не держит, и заводить для него отдельный пул лишь добавит накладных расходов -- хватит `SemaphoreBulkhead`.

### Используйте SemaphoreBulkhead когда:
- Приложение реактивное (WebFlux, Reactor) — переключение потоков дорого
- Вызовы **неблокирующие** (асинхронный HTTP, неблокирующий I/O)
- Нужен минимальный overhead
- Метод возвращает тот же тип (`String`, `Mono<T>`)

```java
@Bulkhead(name = "catalogService", type = Bulkhead.Type.SEMAPHORE)
public Mono<Product> getProduct(Long id) {
    return webClient.get().uri("/products/" + id).retrieve().bodyToMono(Product.class);
}
```

### Используйте ThreadPoolBulkhead когда:
- Вызовы **блокирующие** — JDBC, legacy REST-клиенты (RestTemplate, Feign), файловый I/O
- Нужна полная изоляция потоков (один сервис не может "украсть" потоки у другого)
- Хотите ограничить ресурсы конкретного внешнего сервиса выделенным пулом

```java
@Bulkhead(name = "paymentService", type = Bulkhead.Type.THREADPOOL)
public CompletableFuture<PaymentResult> processPayment(PaymentRequest req) {
    return CompletableFuture.supplyAsync(() -> legacyPaymentClient.process(req));
}
```

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      paymentService:
        maxThreadPoolSize: 10
        coreThreadPoolSize: 5
        queueCapacity: 20
        keepAliveDuration: 20ms
        writableStackTraceEnabled: true
```

**Правило:** в `Spring MVC` (Tomcat thread pool) + блокирующие вызовы → `ThreadPoolBulkhead`. В `Spring WebFlux` (Netty event loop) + реактивные вызовы → `SemaphoreBulkhead`.

---

## Q39. Какие стратегии Fallback существуют и как выбрать подходящую?

**Fallback** -- реакция системы на недоступность зависимости. Универсальной стратегии нет: выбор диктуется тем, насколько критичны данные и допустима ли их подмена. Для некритичного контента сойдёт пустой ответ, для редко меняющихся данных -- кэш, а для мутаций (заказ, оплата) подменять ответ нельзя -- их откладывают в очередь на повтор.

| Стратегия | Описание | Когда применять |
|---|---|---|
| **Статический ответ** | Возврат заранее заданного значения | Некритичные данные (баннеры, рекомендации) |
| **Кэшированный ответ** | Последнее известное значение из кэша | Данные меняются редко (каталог, конфиг) |
| **Деградация функциональности** | Упрощённая версия функции без зависимости | Поиск без фильтров, цена без скидок |
| **Очередь на повтор** | Запрос помещается в очередь (Kafka) | Мутации (заказ, оплата) |
| **Ошибка с подсказкой** | Понятное сообщение об ошибке | Когда деградация невозможна |

### Примеры реализации:

```java
// 1. Статический fallback
@CircuitBreaker(name = "recommendationService", fallbackMethod = "staticFallback")
public List<Product> getRecommendations(Long userId) {
    return recommendationClient.get(userId);
}
private List<Product> staticFallback(Long userId, Exception e) {
    return Collections.emptyList();  // Пустой список — корректно для UI
}

// 2. Кэшированный fallback
@CircuitBreaker(name = "catalogService", fallbackMethod = "cachedFallback")
public Product getProduct(Long id) {
    return catalogClient.get(id);
}
private Product cachedFallback(Long id, Exception e) {
    return productCache.getIfPresent(id);  // Кэш как запасной вариант
}

// 3. Деградация — поиск без внешнего индекса
@CircuitBreaker(name = "searchService", fallbackMethod = "degradedSearch")
public List<Product> search(SearchQuery query) {
    return elasticsearchClient.search(query);
}
private List<Product> degradedSearch(SearchQuery query, Exception e) {
    // Поиск только по локальной БД без полнотекстового индекса
    return productRepository.findByNameContaining(query.getText());
}
```

**Ключевой принцип:** fallback не должен сам вызывать зависимости, которые могут упасть. Иначе получается "fallback of fallback" — антипаттерн.

---

## Q40. Что такое Load Shedding и как реализовать приоритизацию запросов?

**Load Shedding** (сброс нагрузки) -- намеренный отказ части запросов при перегрузке, чтобы остальные обслужить нормально. Логика "лучше отказать 20% сразу, чем уронить 100% медленно": приняв запросов больше, чем способен переварить, сервис деградирует для всех -- честнее отсечь лишнее на входе.

**Когда применять:** когда `Rate Limiter` и `Bulkhead` уже не справляются, или нужна **приоритизация** -- сбрасывать в первую очередь дешёвое (статистику, отчёты), сохраняя критичное (платежи, заказы).

### Стратегии приоритизации:

```
Priority 1 (критичные):  /api/payments/**, /api/orders/**
Priority 2 (важные):     /api/products/**, /api/users/**
Priority 3 (фоновые):    /api/reports/**, /api/analytics/**
```

### Реализация через фильтр:

```java
@Component
@Order(1)
public class LoadSheddingFilter implements Filter {

    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private static final int MAX_REQUESTS = 100;
    
    // Критичные пути не сбрасываем
    private static final Set<String> CRITICAL_PATHS = Set.of(
        "/api/payments", "/api/orders"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();
        
        boolean isCritical = CRITICAL_PATHS.stream().anyMatch(path::startsWith);
        int current = activeRequests.get();
        
        // Некритичные сбрасываем при 80% нагрузки
        if (!isCritical && current > MAX_REQUESTS * 0.8) {
            ((HttpServletResponse) res).setStatus(503);
            ((HttpServletResponse) res).getWriter()
                .write("{\"error\":\"Service overloaded, retry later\"}");
            return;
        }
        
        // Критичные сбрасываем только при 100%
        if (current >= MAX_REQUESTS) {
            ((HttpServletResponse) res).setStatus(503);
            return;
        }
        
        activeRequests.incrementAndGet();
        try {
            chain.doFilter(req, res);
        } finally {
            activeRequests.decrementAndGet();
        }
    }
}
```

### Интеграция с Resilience4j Bulkhead:

```yaml
resilience4j:
  bulkhead:
    instances:
      critical:
        maxConcurrentCalls: 80
        maxWaitDuration: 1s
      background:
        maxConcurrentCalls: 20
        maxWaitDuration: 0ms  # Немедленный отказ
```

**Важно:** Load Shedding должен возвращать `503 Service Unavailable` с заголовком `Retry-After`, чтобы клиенты знали, когда повторить запрос.

---

## Q41. Как интегрировать Circuit Breaker с Spring Boot Actuator Health?

При наличии `resilience4j-spring-boot3` и включённом `registerHealthIndicator: true` библиотека сама регистрирует `HealthIndicator` для каждого `Circuit Breaker` -- его состояние попадает в `/actuator/health`. Это позволяет видеть здоровье предохранителей рядом со здоровьем самого приложения, но требует осторожности: открытый CB не всегда повод считать приложение нездоровым (см. практику ниже).

### Конфигурация:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        registerHealthIndicator: true  # Включить HealthIndicator
        slidingWindowSize: 10
        failureRateThreshold: 50
```

### Результат в `/actuator/health`:

```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP",
      "details": {
        "paymentService": {
          "status": "UP",
          "details": {
            "failureRate": "0.0%",
            "failureRateThreshold": "50.0%",
            "slowCallRate": "0.0%",
            "slowCallRateThreshold": "100.0%",
            "bufferedCalls": 5,
            "failedCalls": 0,
            "notPermittedCalls": 0,
            "state": "CLOSED"
          }
        }
      }
    }
  }
}
```

### Кастомный HealthIndicator с агрегацией:

```java
@Component
public class CircuitBreakerHealthAggregator implements HealthIndicator {

    private final CircuitBreakerRegistry registry;

    @Override
    public Health health() {
        List<String> openBreakers = registry.getAllCircuitBreakers()
            .stream()
            .filter(cb -> cb.getState() == CircuitBreaker.State.OPEN)
            .map(CircuitBreaker::getName)
            .toList();

        if (openBreakers.isEmpty()) {
            return Health.up()
                .withDetail("circuitBreakers", "all CLOSED")
                .build();
        }
        return Health.down()
            .withDetail("openCircuitBreakers", openBreakers)
            .withDetail("message", "Some dependencies are unavailable")
            .build();
    }
}
```

**Практика:** выносить CB-health в отдельную группу, не влияющую на liveness probe:

```yaml
management:
  endpoint:
    health:
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState,db,circuitBreakers
```

---

## Q42. Какие инструменты Chaos Engineering применяются для Spring Boot?

Инструменты выбирают по **уровню**, на котором вносится сбой: внутри JVM (бины приложения), в Kubernetes (поды, сеть) или в инфраструктуре в целом. Для Spring Boot самый близкий и удобный вариант -- `Chaos Monkey for Spring Boot`, потому что он бьёт прямо по бинам без изменения окружения.

### Chaos Monkey for Spring Boot

Библиотека, портированная на Spring Boot из подхода Netflix. Внедряет сбои (задержки, исключения) через AOP прямо в Spring-бины.

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
</dependency>
```

```yaml
chaos:
  monkey:
    enabled: true
    watcher:
      service: true        # Атаковать @Service-бины
      rest-controller: true
    assaults:
      level: 5             # 1-10: частота атак
      latency-active: true
      latency-range-start: 1000  # ms
      latency-range-end: 3000
      exceptions-active: true
      exception:
        type: java.io.IOException
      kill-application-active: false  # Не убивать приложение
```

**Управление через Actuator:**

```bash
# Включить атаки в runtime
curl -X POST http://localhost:8080/actuator/chaosmonkey/enable

# Изменить настройки
curl -X POST http://localhost:8080/actuator/chaosmonkey/assaults \
  -H "Content-Type: application/json" \
  -d '{"level": 3, "latencyActive": true}'
```

### Simian Army (Netflix) и аналоги:

| Инструмент | Уровень | Описание |
|---|---|---|
| **Chaos Monkey for Spring Boot** | JVM / Bean | Latency, exception, kill |
| **Simian Army** (устарел) | AWS / EC2 | Termination instances |
| **Chaos Mesh** | Kubernetes | Network, pod failures |
| **Gremlin** | Infrastructure | Коммерческий, широкий спектр |
| **Toxiproxy** | Network | Proxy с имитацией сетевых проблем |

### Паттерн проведения эксперимента:

1. **Определить "steady state"** — метрики нормальной работы (p99 latency, error rate)
2. **Сформулировать гипотезу** — "CB сработает раньше, чем error rate превысит 1%"
3. **Внести сбой** — включить Chaos Monkey с задержками
4. **Наблюдать** — сравнить с steady state
5. **Исправить** — если гипотеза не подтвердилась

---

## Q43. Как реализовать graceful degradation с помощью Feature Flags?

**Feature flags** дают деградацию, которой управляют **вручную и заранее**: ненадёжную или дорогую функциональность можно отключить на лету, без передеплоя, ещё до того как зависимость окончательно упала. Это дополняет `Circuit Breaker`, который реагирует уже постфактум -- только после серии ошибок.

### Интеграция с Unleash / Spring @ConditionalOnProperty:

```java
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UnleashService unleash;
    private final RecommendationClient client;
    private final ProductRepository fallbackRepo;

    public List<Product> getRecommendations(Long userId) {
        // Feature flag — если флаг выключен, используем деградированную логику
        if (!unleash.isEnabled("recommendations.enabled")) {
            return fallbackRepo.findTopSellers(10);  // Просто топ продаж
        }
        
        try {
            return client.getPersonalized(userId);
        } catch (Exception e) {
            // Дополнительный fallback если сервис упал
            return fallbackRepo.findTopSellers(10);
        }
    }
}
```

### Комбинирование с Circuit Breaker:

```java
@CircuitBreaker(name = "mlService", fallbackMethod = "degradedRecommendations")
public List<Product> getPersonalizedRecommendations(Long userId) {
    if (!featureFlags.isEnabled("ml.recommendations")) {
        throw new FeatureDisabledException("ML recommendations are disabled");
    }
    return mlClient.predict(userId);
}

private List<Product> degradedRecommendations(Long userId, Exception e) {
    // Кэшированный топ продаж как деградированная версия
    return cache.getOrLoad("top-sellers", () -> productRepo.findTopSellers(20));
}
```

### Стратегии деградации в порядке приоритета:

```
1. Полная функциональность (ML-рекомендации, персонализация)
2. Частичная (рекомендации по категории, без персонализации)
3. Кэшированный ответ (последний известный топ продаж)
4. Статический ответ (пустой список или дефолтные товары)
5. Явная ошибка с понятным сообщением пользователю
```

**Преимущество feature flags перед Circuit Breaker:** флаг можно выключить **превентивно** — до того как зависимость упала, например во время планового обслуживания или при повышенной нагрузке в пиковое время.

---

## See also

- [Микросервисы](microservices-interview.md) — паттерны отказоустойчивости как обязательная часть межсервисного взаимодействия
- [Распределённые системы](distributed-systems-interview.md) — каскадные отказы, partial failures, CAP и устойчивость
- [API Gateway](api-gateway-interview.md) — Circuit Breaker и Rate Limiter на уровне шлюза (Spring Cloud Gateway)
- [Spring Cloud](../frameworks/spring/spring-cloud-interview.md) — Spring Cloud Circuit Breaker, Resilience4j интеграция
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — `@CircuitBreaker`, `@Retry` аннотации Resilience4j в Spring Boot
- [Балансировка нагрузки](load-balancing-interview.md) — взаимодействие балансировки и Circuit Breaker при недоступности инстанса
- [Стратегии кэширования](caching-strategies-interview.md) — кэш как Fallback при недоступности upstream сервиса
- [Kubernetes](../devops/kubernetes-interview.md) — liveness/readiness пробы как механизм отказоустойчивости на уровне оркестратора

- [BFF Pattern](bff-pattern-interview.md)
- [Стратегии кэширования](caching-strategies-interview.md)
- [CAP-теорема](cap-theorem-interview.md)
- [Clean Architecture](clean-architecture-interview.md)
- [Паттерны согласованности](consistency-patterns-interview.md)
