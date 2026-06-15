---
title: "Вопросы на собеседовании: Micrometer"
description: "Micrometer как фасад метрик: MeterRegistry, Counter, Gauge, Timer, DistributionSummary, интеграция с Spring Boot и Prometheus, кастомные метрики"
tags:
  - interview
  - monitoring
  - micrometer-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Micrometer"
  - "Micrometer interview"
  - "Micrometer собеседование"
prerequisites:
  - "[[micrometer]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Micrometer`

`Micrometer` — библиотека инструментирования метрик для JVM, аналог `SLF4J` для метрик: предоставляет единый API и работает с 25+ мониторинговыми системами. Активно используется в `Spring Boot` начиная с версии 2.0 и спрашивается на собеседованиях в контексте observability.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Micrometer Concepts](https://docs.micrometer.io/micrometer/reference/concepts.html) — официальный гайд по концепциям
- [Micrometer Spring Boot](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html) — интеграция с Prometheus
- [Baeldung: Micrometer](https://www.baeldung.com/micrometer) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и архитектура**
- [Q1. (!) Что такое Micrometer и зачем он нужен?](#q1--что-такое-micrometer-и-зачем-он-нужен)
- [Q2. Что такое MeterRegistry?](#q2-что-такое-meterregistry)
- [Q3. Как работает CompositeMeterRegistry?](#q3-как-работает-compositemeterregistry)

**Типы метрик**
- [Q4. (!) Какие типы метрик поддерживает Micrometer?](#q4--какие-типы-метрик-поддерживает-micrometer)
- [Q5. (!) Как работает Counter?](#q5--как-работает-counter)
- [Q6. (!) Как работает Gauge?](#q6--как-работает-gauge)
- [Q7. (!) Как работает Timer?](#q7--как-работает-timer)
- [Q8. Что такое DistributionSummary?](#q8-что-такое-distributionsummary)
- [Q9. Когда использовать LongTaskTimer?](#q9-когда-использовать-longtasktimer)

**Теги и именование**
- [Q10. (!) Что такое Tags и зачем они нужны?](#q10--что-такое-tags-и-зачем-они-нужны)
- [Q11. Какие соглашения по именованию метрик?](#q11-какие-соглашения-по-именованию-метрик)

**Spring Boot интеграция**
- [Q12. (!) Как Micrometer интегрирован в Spring Boot?](#q12--как-micrometer-интегрирован-в-spring-boot)
- [Q13. Какие метрики Spring Boot регистрирует автоматически?](#q13-какие-метрики-spring-boot-регистрирует-автоматически)
- [Q14. Как создать кастомную метрику в Spring Boot?](#q14-как-создать-кастомную-метрику-в-spring-boot)
- [Q15. Что такое аннотация @Timed?](#q15-что-такое-аннотация-timed)

**Гистограммы и перцентили**
- [Q16. (!) Как настроить перцентили и гистограммы?](#q16--как-настроить-перцентили-и-гистограммы)
- [Q17. Что такое SLO (Service Level Objective) в Micrometer?](#q17-что-такое-slo-service-level-objective-в-micrometer)

**Фильтры и Prometheus**
- [Q18. Что такое MeterFilter?](#q18-что-такое-meterfilter)
- [Q19. (!) Как настроить экспорт метрик в Prometheus?](#q19--как-настроить-экспорт-метрик-в-prometheus)
- [Q20. Что такое MeterBinder?](#q20-что-такое-meterbinder)

## Q1. (!) Что такое Micrometer и зачем он нужен?

`Micrometer` — это фасад (facade) над клиентскими библиотеками метрик. Ты пишешь инструментирование один раз, через единый API, а конкретный бэкенд (Prometheus, Datadog, …) подключаешь зависимостью и конфигурацией. Та же роль, что у `SLF4J` для логирования: общий интерфейс, сменный реализатор.

**Главная проблема, которую он решает — vendor lock-in.** Если писать напрямую в клиент Prometheus, то переезд на Datadog означает переписать весь код инструментирования: другие классы, другой API, другие имена метрик. С Micrometer код остаётся прежним — меняется только зависимость `micrometer-registry-*` и пара строк конфигурации.

**Поддерживаемые системы (25+):**
- `Prometheus`, `Grafana`
- `Datadog`, `New Relic`, `Dynatrace`
- `CloudWatch`, `Stackdriver`
- `InfluxDB`, `Graphite`, `JMX`
- `OpenTelemetry Protocol (OTLP)` и ещё 20+

```java
// С Micrometer — один код, любой бэкенд
Counter counter = Counter.builder("orders.created")
    .tag("region", "eu-west")
    .register(registry);
counter.increment();
```

**Итог:** Micrometer — это SLF4J для метрик. Инструментируешь код один раз, бэкенд меняешь конфигурацией.

## Q2. Что такое MeterRegistry?

`MeterRegistry` — центральный компонент Micrometer и фабрика всех метрик: любой `Counter`, `Gauge` или `Timer` создаётся через него и им же владеет. Реестр решает две задачи:
- хранит все зарегистрированные метрики (Meter-ы) в одном месте;
- публикует их в мониторинговую систему — либо периодически push-ом (Datadog, InfluxDB), либо отдавая снимок по запросу при pull-е (Prometheus scrape).

Реализация реестра определяет, **куда** уходят метрики. Поэтому весь код инструментирования работает с абстрактным `MeterRegistry`, а конкретный класс выбирается зависимостью.

**Основные реализации:**

| Реализация | Назначение |
|---|---|
| `SimpleMeterRegistry` | In-memory, для тестов |
| `PrometheusMeterRegistry` | Экспорт в Prometheus |
| `DatadogMeterRegistry` | Экспорт в Datadog |
| `CompositeMeterRegistry` | Публикация в несколько систем |
| `Metrics.globalRegistry` | Статический глобальный реестр |

```java
// Получение из Spring context
@Autowired
MeterRegistry registry;

// Или статически (не рекомендуется в Spring)
Metrics.globalRegistry.counter("my.counter").increment();
```

## Q3. Как работает CompositeMeterRegistry?

`CompositeMeterRegistry` объединяет несколько реестров за единым фасадом: метрику регистрируешь один раз через composite, а под капотом она создаётся и обновляется во всех вложенных реестрах сразу. Это нужно, когда метрики надо отправлять одновременно в несколько систем — например, в Prometheus для прода и в локальный `SimpleMeterRegistry` для отладки.

```java
CompositeMeterRegistry composite = new CompositeMeterRegistry();
composite.add(new SimpleMeterRegistry());
composite.add(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));

Counter counter = composite.counter("requests");
counter.increment(); // записывается в оба реестра
```

**В Spring Boot:** именно `CompositeMeterRegistry` используется по умолчанию. Spring Boot всегда отдаёт через `@Autowired` composite-реестр и автоматически добавляет в него все найденные в classpath реализации (Prometheus, Datadog и т.д.). Поэтому твой код работает одинаково независимо от того, один бэкенд подключён или несколько.

## Q4. (!) Какие типы метрик поддерживает Micrometer?

Все типы — это специализации общего интерфейса `Meter`. Различаются они тем, как ведёт себя значение во времени: монотонно растёт (`Counter`), произвольно колеблется (`Gauge`) или собирается в распределение (`Timer`, `DistributionSummary`). Выбор типа определяет, какие запросы и агрегации будут доступны в мониторинге.

| Тип | Описание | Когда использовать |
|---|---|---|
| `Counter` | Монотонно возрастающий счётчик | Количество запросов, ошибок, событий |
| `Gauge` | Мгновенное значение | Размер очереди, количество соединений, использование памяти |
| `Timer` | Время выполнения + счётчик | Латентность операций, HTTP-запросы |
| `DistributionSummary` | Распределение значений | Размер payload, размер файлов |
| `LongTaskTimer` | Активные долгие задачи | Batch-задачи, фоновые процессы |
| `FunctionCounter` | Обёртка над monotonic function | Метрики из внешних объектов |

Иерархия типов — все они наследники общего интерфейса `Meter`:
- `Meter` → `Counter` — только `increment`;
- `Meter` → `Gauge` — любое значение;
- `Meter` → `Timer` — время + count;
- `Meter` → `DistributionSummary` — распределение;
- `Meter` → `LongTaskTimer` — активные задачи.

## Q5. (!) Как работает Counter?

`Counter` — монотонно возрастающее число. Его можно только **увеличивать**; decrement не поддерживается намеренно. Это правильный тип для подсчёта событий, которые произошли: запросов, ошибок, отправленных сообщений. Само абсолютное значение счётчика обычно неинтересно — важна скорость его роста.

```java
// Создание
Counter counter = Counter.builder("http.requests")
    .description("Total HTTP requests")
    .tag("method", "GET")
    .tag("status", "200")
    .register(registry);

// Использование
counter.increment();           // +1
counter.increment(5);          // +5
double total = counter.count(); // получить значение
```

**Fluent API через MeterRegistry:**
```java
registry.counter("cache.misses", "cache", "users").increment();
```

**Подводный камень.** При перезапуске приложения счётчик обнуляется. Поэтому в дашбордах смотрят не на само значение, а на производную — `rate(counter[5m])` в PromQL даёт «событий в секунду» и корректно обрабатывает рестарты (сброс считается за переполнение, а не за резкое падение).

## Q6. (!) Как работает Gauge?

`Gauge` — мгновенное значение, которое может расти и падать: размер очереди, число соединений, занятая память. Ключевая особенность: ты не «устанавливаешь» значение, а **привязываешь gauge к объекту**. Micrometer хранит слабую ссылку (weak reference) на источник и сам вызывает функцию извлечения в момент сбора метрик. Поэтому источник всегда должен жить дольше gauge — иначе значение «застрянет» или метрика исчезнет.

```java
// Привязка к коллекции (авто-обновление размера)
List<String> queue = new ArrayList<>();
registry.gaugeCollectionSize("queue.size", Tags.of("name", "orders"), queue);

// Привязка к AtomicInteger
AtomicInteger activeConnections = new AtomicInteger(0);
Gauge.builder("connections.active", activeConnections, AtomicInteger::get)
    .register(registry);

// Привязка через лямбду
Gauge.builder("cache.size", cache, c -> c.size())
    .register(registry);
```

**Отличие от Counter:**
- `Counter` только растёт; в запросах берут `rate()`, чтобы получить динамику.
- `Gauge` показывает текущее состояние; в запросах его значение используют напрямую (а тренд считают через `delta()`/`deriv()`).

**Подводный камень:** функция извлечения вызывается при scrape, поэтому она должна быть быстрой и без побочных эффектов — никаких походов в БД или блокировок.

## Q7. (!) Как работает Timer?

`Timer` измеряет длительность коротких операций и заодно считает, сколько раз они выполнялись. То есть одной метрикой получаешь сразу и латентность, и частоту (throughput) — это главный инструмент для HTTP-запросов и вызовов сервисов.

Записывать длительность можно тремя способами — выбор зависит от того, есть ли у тебя готовый `Duration` или нужно обернуть выполнение кода:

```java
Timer timer = Timer.builder("http.request.duration")
    .description("HTTP request processing time")
    .tag("endpoint", "/api/orders")
    .publishPercentiles(0.5, 0.95, 0.99)  // p50, p95, p99
    .register(registry);

// Вариант 1: запись вручную
timer.record(Duration.ofMillis(150));

// Вариант 2: обёртка вокруг кода
timer.record(() -> processRequest());

// Вариант 3: Timer.Sample для сложных случаев
Timer.Sample sample = Timer.start(registry);
// ... долгая операция ...
sample.stop(timer);
```

`Timer.Sample` нужен, когда начало и конец измерения разнесены по коду (например, старт в одном методе, стоп — в callback-е): сэмпл фиксирует момент старта и завершает замер при `stop()`.

**Что хранит Timer:**
- `count` — количество событий;
- `totalTime` — суммарное время (из него и count выводится среднее);
- `max` — максимальное время;
- перцентили (если настроены через `publishPercentiles`).

**Подводный камень:** среднее (`totalTime / count`) маскирует хвосты — медленные запросы. Для SLA смотри на p95/p99, а не на mean.

## Q8. Что такое DistributionSummary?

`DistributionSummary` устроен как `Timer` (count, total, max, перцентили, гистограммы), но измеряет **произвольную величину, а не время**. По сути `Timer` — это частный случай `DistributionSummary`, у которого единица измерения зафиксирована как секунды.

```java
DistributionSummary summary = DistributionSummary.builder("request.payload.size")
    .baseUnit("bytes")
    .publishPercentiles(0.5, 0.95)
    .register(registry);

summary.record(1024);    // размер запроса в байтах
summary.record(512);

double mean = summary.mean();
double count = summary.count();
```

**Когда применять:** размер payload, количество строк в batch, размер загружаемых файлов — везде, где интересно не просто среднее, а распределение значений (медиана, хвосты). Для простого «текущего размера» хватит `Gauge`; `DistributionSummary` нужен, когда важна история отдельных замеров.

## Q9. Когда использовать LongTaskTimer?

`LongTaskTimer` нужен для долгих задач (минуты, часы), за которыми хочется наблюдать **прямо во время выполнения**, не дожидаясь конца. Классический пример: batch-джоба зависла на 40 минут — обычный `Timer` не покажет ничего, пока она не завершится, а `LongTaskTimer` в каждый scrape сообщает, что задача всё ещё активна и сколько уже работает.

```java
LongTaskTimer taskTimer = LongTaskTimer.builder("batch.job")
    .register(registry);

LongTaskTimer.Sample sample = taskTimer.start();
try {
    // долгий процесс (минуты, часы)
    processBatch();
} finally {
    sample.stop();
}

// Пока задача выполняется, можно получить:
taskTimer.activeTasks();     // количество активных задач
taskTimer.duration(SECONDS); // суммарное время активных задач
```

**Отличие от Timer:**
- `Timer` фиксирует замер только в момент `stop()` — пока операция идёт, в метриках её нет.
- `LongTaskTimer` виден сразу после `start()`: показывает число активных задач и накопленное время. Это позволяет завести алерт «джоба выполняется дольше N минут» ещё до её завершения.

## Q10. (!) Что такое Tags и зачем они нужны?

`Tags` — пары ключ-значение, которые превращают одну метрику в **многомерную**. Вместо отдельных счётчиков `http.requests.get.200`, `http.requests.post.500` ты держишь один `http.requests` с тегами `method` и `status` и фильтруешь/группируешь данные в запросе. В терминах мониторинга каждая уникальная комбинация значений тегов — это отдельный временной ряд (time series).

```java
// Тег при создании метрики
Counter.builder("http.requests")
    .tag("method", "POST")
    .tag("status", "500")
    .tag("endpoint", "/api/orders")
    .register(registry);
```

**Общие теги (common tags)** — добавляются ко всем метрикам реестра. Так помечают принадлежность приложению, региону, версии, чтобы потом отличать метрики разных инстансов в общем Prometheus:
```java
registry.config()
    .commonTags("application", "order-service")
    .commonTags("region", "eu-west-1")
    .commonTags("version", "1.2.3");
```

В Spring Boot через `application.yml`:
```yaml
management:
  metrics:
    tags:
      application: ${spring.application.name}
      environment: production
```

**Подводный камень — кардинальность.** Не клади в теги значения с высокой кардинальностью (user ID, request ID, email). Каждое уникальное значение порождает новый временной ряд, а их произведение по всем тегам — это «cardinality explosion»: память Prometheus и память самого приложения растут линейно числу рядов, и мониторинг может лечь. Эмпирическое правило: значение тега должно иметь ограниченный, заранее известный набор вариантов (метод, статус-код, имя эндпоинта — да; идентификатор сущности — нет).

## Q11. Какие соглашения по именованию метрик?

Micrometer задаёт имена через точку (**dot-separated**): `http.requests.total`, `jvm.memory.used`. Это «нейтральный» внутренний формат — писать имена нужно именно так, независимо от целевого бэкенда.

При экспорте каждый бэкенд сам конвертирует имя в свой стиль, поэтому одно и то же имя выглядит по-разному в разных системах:
- Prometheus: `http_requests_total` (snake_case)
- Datadog: `http.requests.total` (dot-notation)
- Graphite: `http.requests.total`

```java
// Правильно — dot-notation
Counter.builder("orders.created").register(registry);
Counter.builder("payment.failed").register(registry);

// Неправильно — snake_case (для Micrometer)
Counter.builder("orders_created").register(registry);
```

**Рекомендуемые суффиксы** (Prometheus naming convention):
- `_total` — для счётчиков (Counter)
- `_seconds` — для времени (Timer)
- `_bytes` — для размеров

## Q12. (!) Как Micrometer интегрирован в Spring Boot?

Micrometer — это «движок метрик» внутри Spring Boot Actuator, поэтому отдельно его подключать не нужно: `spring-boot-starter-actuator` тянет ядро Micrometer, а зависимость `micrometer-registry-*` добавляет конкретный бэкенд. Дальше auto-configuration делает всё сама.

При наличии реализации (например, `micrometer-registry-prometheus`) Spring Boot **автоматически**:

1. Создаёт `CompositeMeterRegistry` и складывает в него все найденные реестры.
2. Регистрирует JVM-метрики (память, GC, потоки).
3. Инструментирует HTTP-запросы (`http.server.requests`).
4. Добавляет метрики пулов и контейнера (HikariCP, Tomcat).

**Зависимости:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Конфигурация:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

`MeterRegistry` доступен через `@Autowired`:
```java
@Service
public class OrderService {
    private final Counter orderCounter;

    public OrderService(MeterRegistry registry) {
        this.orderCounter = registry.counter("orders.created");
    }

    public void createOrder() {
        orderCounter.increment();
        // ...
    }
}
```

## Q13. Какие метрики Spring Boot регистрирует автоматически?

Spring Boot из коробки регистрирует целый набор готовых метрик через `MeterBinder`-ы — то есть базовая наблюдаемость приложения есть сразу, без единой строки кода инструментирования. Покрываются четыре уровня: JVM, веб-слой, инфраструктура (пулы, контейнер) и логирование.

**JVM:**
- `jvm.memory.used` / `jvm.memory.max` — heap/non-heap по регионам
- `jvm.gc.pause` — время GC пауз
- `jvm.threads.live` / `jvm.threads.daemon`
- `jvm.classes.loaded`

**HTTP (Spring MVC / WebFlux):**
- `http.server.requests` — по методу, URI, статусу, исключению

**HikariCP (connection pool):**
- `hikaricp.connections.active` / `hikaricp.connections.idle`
- `hikaricp.connections.acquire` — время получения соединения

**Tomcat:**
- `tomcat.threads.busy` / `tomcat.threads.current`
- `tomcat.sessions.active.current`

**Logback:**
- `logback.events` — по уровню (ERROR, WARN, INFO...)

**Cache (Caffeine, EhCache):**
- `cache.gets` / `cache.puts` / `cache.evictions`

## Q14. Как создать кастомную метрику в Spring Boot?

Есть два подхода. **Прямая инжекция реестра** — для метрик, тесно связанных с бизнес-логикой конкретного сервиса. **`MeterBinder`** — для инструментирования, которое хочется оформить как переиспользуемый компонент (см. Q20). Общее правило: создавай метрики один раз при инициализации бина и держи ссылки в полях, а не вызывай `registry.counter(...)` на каждый запрос.

**Вариант 1: инжекция MeterRegistry в конструктор (рекомендуется):**
```java
@Service
public class PaymentService {
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer processingTimer;

    public PaymentService(MeterRegistry registry) {
        this.successCounter = Counter.builder("payments")
            .tag("status", "success")
            .register(registry);
        this.failureCounter = Counter.builder("payments")
            .tag("status", "failure")
            .register(registry);
        this.processingTimer = Timer.builder("payment.processing.time")
            .register(registry);
    }

    public PaymentResult process(Payment payment) {
        return processingTimer.record(() -> {
            try {
                PaymentResult result = doProcess(payment);
                successCounter.increment();
                return result;
            } catch (Exception e) {
                failureCounter.increment();
                throw e;
            }
        });
    }
}
```

**Вариант 2: через `MeterBinder` (для переиспользуемой инструментации):**
```java
@Component
public class OrderQueueMetrics implements MeterBinder {
    private final BlockingQueue<Order> queue;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("orders.queue.size", queue, Queue::size)
            .description("Current order queue depth")
            .register(registry);
    }
}
```

## Q15. Что такое аннотация @Timed?

`@Timed` навешивает `Timer` на метод декларативно, через AOP: вместо ручного `timer.record(...)` ты просто помечаешь метод аннотацией, а AOP-аспект оборачивает вызов замером. Удобно для точечного тайминга отдельных методов, когда не хочется засорять код вызовами Micrometer.

```java
@RestController
public class OrderController {

    @GetMapping("/orders/{id}")
    @Timed(value = "orders.get",
           description = "Time to get order by ID",
           percentiles = {0.5, 0.95, 0.99},
           histogram = true)
    public Order getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
```

**Требование:** аннотация работает только если зарегистрирован бин `TimedAspect` — без него `@Timed` просто игнорируется:
```java
@Bean
public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
}
```

**Подводный камень:** это обычный AOP proxy, поэтому он не срабатывает при self-invocation — когда метод с `@Timed` вызывается из другого метода того же бина, минуя прокси (подробнее в [Spring AOP](../frameworks/spring/spring-aop-interview.md)).

## Q16. (!) Как настроить перцентили и гистограммы?

У `Timer` и `DistributionSummary` есть два принципиально разных способа получить перцентили, и их легко перепутать — на собеседовании это любят спрашивать. Разница в том, **где** считается перцентиль: в приложении или в Prometheus.

**Client-side percentiles** — приложение само считает p50/p95/p99 и публикует готовые числа:
```java
Timer.builder("http.request.duration")
    .publishPercentiles(0.5, 0.95, 0.99)  // p50, p95, p99
    .register(registry);
```
- **Плюс:** работает с любым бэкендом, готовые перцентили сразу видны.
- **Минус:** перцентили нельзя складывать между инстансами. Среднее от p99 двух подов — это не p99 кластера, поэтому корректной общей цифры по сервису не получить.

**Histogram (server-side percentiles)** — приложение публикует «корзины» (buckets), а перцентиль вычисляет Prometheus запросом `histogram_quantile()`:
```java
Timer.builder("http.request.duration")
    .publishPercentileHistogram(true)  // отдаёт buckets, Prometheus вычисляет histogram_quantile()
    .register(registry);
```
- **Плюс:** buckets аддитивны, поэтому перцентиль можно корректно посчитать по всему кластеру.
- **Минус:** перцентиль не приходит готовым — нужен PromQL-запрос и поддержка гистограмм на стороне Prometheus; точность зависит от ширины корзин.

**Через `application.yml`:**
```yaml
management:
  metrics:
    distribution:
      percentiles:
        http.server.requests: 0.5,0.95,0.99
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 50ms,100ms,200ms,500ms
```

## Q17. Что такое SLO (Service Level Objective) в Micrometer?

SLO здесь — это набор пороговых значений, на которых ты хочешь видеть распределение. Micrometer заводит отдельную bucket для каждого порога, и по этим корзинам можно посчитать, какая доля запросов уложилась в цель (например, «99% быстрее 200 мс»). По сути это явное управление границами гистограммы: вместо автоматических корзин ты задаёшь те, что совпадают с твоими целевыми порогами латентности.

```java
Timer.builder("http.request.duration")
    .serviceLevelObjectives(
        Duration.ofMillis(50),
        Duration.ofMillis(100),
        Duration.ofMillis(200)
    )
    .register(registry);
```

В Prometheus это превращается в:
```
http_request_duration_seconds_bucket{le="0.05"} 1234
http_request_duration_seconds_bucket{le="0.10"} 5678
http_request_duration_seconds_bucket{le="0.20"} 9012
```

**Запрос в PromQL:**
```promql
# Процент запросов быстрее 100ms
rate(http_request_duration_seconds_bucket{le="0.10"}[5m])
  / rate(http_request_duration_seconds_count[5m])
```

## Q18. Что такое MeterFilter?

`MeterFilter` — перехватчик, который срабатывает в момент **регистрации** метрики и решает её судьбу: пропустить, запретить, переименовать, добавить/убрать теги, ограничить кардинальность. Это центральная точка контроля над тем, какие метрики вообще попадут в мониторинг, — особенно полезно, чтобы отрезать шумные авто-метрики или обуздать взрывной рост тегов (см. кардинальность в Q10).

```java
@Bean
public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
    return registry -> registry.config()
        // запретить метрики по паттерну
        .meterFilter(MeterFilter.deny(id -> 
            id.getName().startsWith("jvm.buffer")))
        // ограничить кардинальность тега uri
        .meterFilter(MeterFilter.maximumAllowableTags(
            "http.server.requests", "uri", 100, MeterFilter.deny()))
        // добавить тег ко всем метрикам
        .commonTags("env", "production");
}
```

**Встроенные фильтры:**
- `MeterFilter.deny()` — запрещает регистрацию
- `MeterFilter.accept()` — разрешает явно
- `MeterFilter.ignoreTags(...)` — удаляет теги
- `MeterFilter.maximumAllowableTags(...)` — ограничивает кардинальность

## Q19. (!) Как настроить экспорт метрик в Prometheus?

Prometheus работает по pull-модели: приложение не отправляет метрики само, а отдаёт их по HTTP-эндпоинту, который Prometheus периодически опрашивает (scrape). Поэтому настройка состоит из двух частей — открыть эндпоинт на стороне приложения и прописать его в конфиге Prometheus.

**1. Зависимости:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**2. application.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
```

**3. Endpoint:** `GET /actuator/prometheus` — возвращает метрики в формате text/plain для Prometheus.

**4. prometheus.yml (настройка scraping):**
```yaml
scrape_configs:
  - job_name: 'spring-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
    scrape_interval: 15s
```

**Итог:** Spring Boot + `micrometer-registry-prometheus` → endpoint `/actuator/prometheus` → Prometheus scrapes → Grafana визуализирует.

## Q20. Что такое MeterBinder?

`MeterBinder` — это контракт (SPI) для **переиспользуемого** инструментирования: класс реализует единственный метод `bindTo(MeterRegistry)`, в котором регистрирует свой набор метрик. Идея в том, чтобы упаковать инструментирование какого-то ресурса (пула, кэша, очереди) в отдельный компонент и переиспользовать его. Именно так Spring Boot поставляет готовые метрики JVM, HikariCP, Caffeine и других — каждая группа оформлена как `MeterBinder`.

```java
@Component
public class DatabaseHealthMetrics implements MeterBinder {
    private final DataSource dataSource;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("db.pool.active", dataSource,
                ds -> ((HikariDataSource) ds).getHikariPoolMXBean().getActiveConnections())
            .description("Active DB connections")
            .register(registry);

        Gauge.builder("db.pool.idle", dataSource,
                ds -> ((HikariDataSource) ds).getHikariPoolMXBean().getIdleConnections())
            .description("Idle DB connections")
            .register(registry);
    }
}
```

**Преимущество:** любой бин типа `MeterBinder` Spring Boot находит и применяет к реестру автоматически — `bindTo()` вызывать руками не нужно. Достаточно пометить класс `@Component`.

## See also

- [Observability](observability-interview.md) — три столпа наблюдаемости: метрики, логи, трейсинг
- [Prometheus и Grafana](prometheus-grafana-interview.md) — Prometheus scraping, PromQL, Grafana дашборды
- [OpenTelemetry](opentelemetry-interview.md) — стандарт CNCF для трассировки, метрик и логов
- [Spring Boot Actuator](../frameworks/spring/spring-boot-actuator-interview.md) — health checks, info, metrics endpoint
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — auto-configuration, starter dependencies
- [Spring AOP](../frameworks/spring/spring-aop-interview.md) — механизм @Timed annotation (AOP proxy)
- [JVM Performance Tuning](../performance/jvm-performance-tuning-interview.md) — JVM метрики: GC, heap, threads
- [Database Performance](../performance/database-performance-interview.md) — метрики пулов соединений HikariCP
- [Jaeger и Zipkin](jaeger-zipkin-interview.md) — распределённый трейсинг рядом с метриками
- [Шпаргалка: Micrometer](../../monitoring/metrics/micrometer.md) — теория
