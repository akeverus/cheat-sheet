---
title: "Вопросы на собеседовании: Spring Scheduling"
description: "Spring Scheduling: @Scheduled, @EnableScheduling, fixedDelay/fixedRate/cron, TaskScheduler, динамическое планирование, ShedLock для кластеров, тестирование"
tags:
  - interview
  - spring
  - spring-scheduling-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Scheduling"
  - "Spring Scheduling interview"
  - "Spring @Scheduled interview"
prerequisites:
  - "[[spring-scheduling]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Scheduling`

`Spring Scheduling` — механизм для выполнения задач по расписанию. `@Scheduled` — декларативный способ через аннотации. `TaskScheduler` — программный API для динамического планирования. Для кластерных сред нужна блокировка (ShedLock / Quartz).

## Полезные ссылки

### Официальная документация

- [Spring Task Execution and Scheduling](https://docs.spring.io/spring-framework/reference/integration/scheduling.html) — reference
- [ShedLock GitHub](https://github.com/lukas-krecan/ShedLock) — distributed lock

### Baeldung tutorials

- [The @Scheduled Annotation in Spring](https://www.baeldung.com/spring-scheduled-tasks) — основы
- [A Guide to the Spring Task Scheduler](https://www.baeldung.com/spring-task-scheduler) — TaskScheduler
- [Guide to ShedLock with Spring](https://www.baeldung.com/shedlock-spring)
- [Cron Syntax in Linux vs Spring](https://www.baeldung.com/cron-syntax-linux-vs-spring)
- [Testing @Scheduled](https://www.baeldung.com/spring-testing-scheduled-annotation)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. Как настроить Spring Scheduling в Spring Boot?](#q1-как-настроить-spring-scheduling-в-spring-boot)
- [Q2. Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?](#q2-чем-отличаются-fixeddelay-fixedrate-и-cron-в-scheduled)
- [Q3. Как работает синтаксис cron-выражений в Spring?](#q3-как-работает-синтаксис-cron-выражений-в-spring)
- [Q4. Что такое `initialDelay` и зачем он нужен?](#q4-что-такое-initialdelay-и-зачем-он-нужен)
- [Q5. Как вынести cron-выражение в конфигурацию?](#q5-как-вынести-cron-выражение-в-конфигурацию)

**Пул потоков и параллельность**
- [Q6. В каком потоке выполняется `@Scheduled`?](#q6-в-каком-потоке-выполняется-scheduled)
- [Q7. Как настроить пул потоков для `@Scheduled`?](#q7-как-настроить-пул-потоков-для-scheduled)
- [Q8. Могут ли задачи `@Scheduled` выполняться параллельно?](#q8-могут-ли-задачи-scheduled-выполняться-параллельно)

**TaskScheduler API**
- [Q9. Что такое `TaskScheduler` и когда его использовать?](#q9-что-такое-taskscheduler-и-когда-его-использовать)
- [Q10. Как запланировать задачу динамически из кода?](#q10-как-запланировать-задачу-динамически-из-кода)

**Кластерная среда**
- [Q11. Почему `@Scheduled` проблематичен в кластере?](#q11-почему-scheduled-проблематичен-в-кластере)
- [Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?](#q12-как-решить-проблему-с-scheduled-в-кластере-с-помощью-shedlock)
- [Q13. Чем Quartz отличается от Spring Scheduling?](#q13-чем-quartz-отличается-от-spring-scheduling)

**Тестирование**
- [Q14. Как тестировать `@Scheduled`-задачи?](#q14-как-тестировать-scheduled-задачи)

**Дополнительно**
- [Q15. Как условно включать/отключать `@Scheduled`?](#q15-как-условно-включатьотключать-scheduled)
- [Q16. Какие типичные ошибки при работе с `@Scheduled`?](#q16-какие-типичные-ошибки-при-работе-с-scheduled)

---

## Q1. Как настроить Spring Scheduling в Spring Boot?

Нужны два шага: включить механизм аннотацией `@EnableScheduling` и пометить методы аннотацией `@Scheduled`. Сама по себе зависимость `spring-context` (она уже в любом Boot-приложении) даёт планировщик, но без `@EnableScheduling` Spring не сканирует `@Scheduled`-методы — задачи молча не запускаются.

`@EnableScheduling` вешают на конфигурационный или главный класс:

```java
@SpringBootApplication
@EnableScheduling
public class MyApp { }
```

Или отдельный конфиг:

```java
@Configuration
@EnableScheduling
public class SchedulingConfig { }
```

Затем помечать методы `@Scheduled`:

```java
@Component
@Slf4j
public class ReportJob {

    @Scheduled(cron = "0 0 8 * * MON-FRI")  // Каждый будний день в 8:00
    public void generateDailyReport() {
        log.info("Generating daily report...");
        // логика
    }
}
```

Что происходит под капотом: `@EnableScheduling` регистрирует `ScheduledAnnotationBeanPostProcessor`, который на старте контекста обходит все бины, находит `@Scheduled`-методы и регистрирует их в планировщике. Поэтому метод должен быть в Spring-бине (`@Component`, `@Service` и т.п.) — иначе постпроцессор его не увидит.

**В plain Spring** (без Boot) аналог — `<task:annotation-driven/>` в XML или `@EnableScheduling` плюс регистрация `TaskScheduler`-бина вручную; в Boot планировщик автоконфигурируется сам.

## Q2. Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?

Это три способа задать расписание, и различаются они точкой отсчёта: `fixedDelay` считает паузу от **конца** предыдущего запуска, `fixedRate` — от **начала**, а `cron` привязывается к **абсолютному времени** (часам, дням недели).

| Параметр | Что означает | Пример |
|---|---|---|
| `fixedDelay` | Задержка между концом предыдущего и началом следующего | `fixedDelay = 5000` |
| `fixedRate` | Пауза между началами запусков | `fixedRate = 5000` |
| `cron` | По cron-расписанию | `cron = "0 * * * * *"` |

```java
@Scheduled(fixedDelay = 5_000)     // Через 5 сек ПОСЛЕ завершения
public void withDelay() { doWork(); }

@Scheduled(fixedRate = 5_000)      // Каждые 5 сек ОТ НАЧАЛА
public void withRate() { doWork(); }
```

**Ключевое различие:**

```
fixedDelay:  |--work(2s)--|  5s  |--work(3s)--|  5s  |--work--|
fixedRate:   |--work(2s)--|  3s  |--work(3s)--|  2s  |--work--|
                                                        ^^ 5s от начала
```

Важный нюанс `fixedRate`: если задача выполняется дольше интервала, следующий запуск стартует сразу после завершения предыдущего — Spring не копит пропущенные тики и не запускает их параллельно (по умолчанию всё на одном потоке, см. Q6). То есть «каждые 5 секунд» при работе по 8 секунд превращается в «как только освободится поток».

Когда что выбирать:

- **`fixedDelay`** — когда важна гарантированная пауза между запусками (например, опрос внешнего API без перегруза). Длительность самой задачи на интервал не влияет.
- **`fixedRate`** — когда нужен стабильный темп независимо от длительности (сбор метрик строго раз в 30 секунд).
- **`cron`** — когда задача привязана ко времени суток или дням недели (отчёт каждый будний день в 8:00).

## Q3. Как работает синтаксис cron-выражений в Spring?

Cron-выражение в Spring — это строка из **6 полей**, разделённых пробелами, где первое поле — секунды. Это главное отличие от Unix cron, где полей **5** и первое — минуты. Из-за этой разницы скопированное из crontab выражение в Spring обычно не работает (на одно поле «съезжает»).

Поля по порядку:

```
секунды минуты часы день_месяца месяц день_недели
```

```
0  30  9  *  *  MON-FRI
^  ^   ^  ^  ^  ^
сек мин час дм  мес  дн
```

**Примеры:**

```
0 0 * * * *          — Каждый час (в 0 мин 0 сек)
0 0 8 * * *          — Каждый день в 8:00
0 0 8 * * MON-FRI    — Каждый будний день в 8:00
0 0/15 * * * *       — Каждые 15 минут
0 0 8,20 * * *       — В 8:00 и 20:00 каждый день
0 0 0 1 * *          — 1-е число каждого месяца в полночь
0 0 0 * * MON        — Каждый понедельник в полночь
0 0 0 L * *          — Последний день каждого месяца
```

**Специальные значения:**

| Символ | Значение |
|---|---|
| `*` | любое значение |
| `?` | не важно (только для дня месяца и дня недели) |
| `-` | диапазон: `1-5` |
| `,` | список: `MON,WED,FRI` |
| `/` | шаг: `0/15` = каждые 15 |
| `L` | последний день месяца/недели |
| `W` | ближайший рабочий день |

**Про `?` и поля «день месяца» / «день недели»:** в нативном Spring `CronExpression` (с 5.3 — парсер по умолчанию для `@Scheduled`) `?` означает «не важно» и полностью эквивалентен `*` — он допустим, но не обязателен. Можно задать оба поля конкретными значениями: выражение `0 0 8 5 * MON` валидно и сработает по семантике ИЛИ — «5-е число месяца **или** понедельник, в 8:00». Это отличается от Quartz, где в одном из этих двух полей обязателен именно `?`; в Spring такого требования нет.

**Спецзначение Spring:** `@Scheduled(cron = "-")` — задача не выполняется никогда. Это удобно для отключения через конфигурацию: подставив `-` в значение из `application.yml`, задачу глушат без правки кода (см. Q5, Q15).

## Q4. Что такое `initialDelay` и зачем он нужен?

`initialDelay` — задержка (в миллисекундах) перед **первым** запуском задачи после старта приложения. Работает только в паре с `fixedRate` или `fixedDelay`; для `cron` он не имеет смысла, потому что cron сам задаёт момент первого запуска. Без `initialDelay` `fixedRate`-задача стартует сразу при подъёме контекста.

```java
@Scheduled(fixedRate = 60_000, initialDelay = 30_000)
public void startAfterDelay() {
    // Первый запуск через 30 сек после старта приложения,
    // затем каждую минуту
}
```

**Зачем нужен:**
- Дать приложению время инициализироваться — прогреть кэш, поднять пул соединений к БД, чтобы задача не упала на холодном старте.
- Развести по времени старт нескольких задач, чтобы они не ударили по системе все разом при подъёме приложения.
- Дождаться готовности внешних систем, к которым задача обращается.

```java
@Component
public class CacheWarmupJob {

    @Scheduled(fixedDelay = 3_600_000, initialDelay = 60_000)
    public void warmupCache() {
        // Первый прогрев кэша — через минуту после старта
        // Затем каждый час
    }
}
```

Аналог через конфиг: `@Scheduled(fixedDelayString = "${cache.warmup.delay:60000}")`.

## Q5. Как вынести cron-выражение в конфигурацию?

Через placeholder `${...}` в значении аннотации. Главное правило: для строковых параметров используют отдельные `String`-варианты атрибутов — `cron` принимает placeholder напрямую, а вместо `fixedDelay`/`fixedRate`/`initialDelay` (они `long`) берут `fixedDelayString`/`fixedRateString`/`initialDelayString`. Через двоеточие в placeholder задают значение по умолчанию на случай, если свойство не определено.

```java
@Component
public class ReportJob {

    @Scheduled(cron = "${reporting.cron:0 0 8 * * *}")
    // Из application.yml: reporting.cron=0 0 8 * * *
    // Default если не задан: каждый день в 8:00
    public void generateReport() { }

    @Scheduled(fixedDelayString = "${sync.delay.ms:60000}")
    public void syncData() { }

    @Scheduled(fixedRateString = "${metrics.rate.ms:30000}",
               initialDelayString = "${metrics.initial-delay.ms:5000}")
    public void collectMetrics() { }
}
```

`application.yml`:

```yaml
reporting:
  cron: "0 0 6 * * MON-FRI"  # переопределить для prod

sync:
  delay:
    ms: 30000

# Отключить задачу в dev:
# reporting.cron: "-"
```

**Зачем так делать:** расписание становится частью конфигурации, а не кода — его меняют по окружениям (dev/stage/prod) без перекомпиляции и без редеплоя, через profile-specific `application-*.yml` или переменные окружения.

**Полезный трюк:** `@Scheduled(cron = "${job.cron:-}")` — значение по умолчанию `-`, то самое спецзначение «никогда» из Q3. Если свойство не задано ни в одном окружении, задача просто не запустится, и это безопасный дефолт.

## Q6. В каком потоке выполняется `@Scheduled`?

По умолчанию — в **одном-единственном** потоке. Spring создаёт `ThreadPoolTaskScheduler` с pool size = 1, и через него проходят **все** `@Scheduled`-методы приложения, выполняясь строго последовательно.

```
Поток 1: [task-A------][task-B--][task-C-----------]
```

Это означает, что задачи делят один поток между собой: пока `task-A` работает, `task-B` и `task-C` ждут в очереди, даже если их время по расписанию уже наступило.

**Почему это важно на собеседовании:** один медленный job тормозит все остальные. И `fixedRate`-задача не запускается параллельно сама с собой — если предыдущий запуск ещё идёт, следующий не стартует, а «опаздывает». Частая ошибка — считать, что десять разных `@Scheduled`-методов работают независимо; по умолчанию они конкурируют за один поток.

Чтобы задачи шли параллельно, нужно расширить пул планировщика (см. Q7) или вынести выполнение в отдельный executor через `@Async` (см. Q8).

## Q7. Как настроить пул потоков для `@Scheduled`?

Два способа: программно через `SchedulingConfigurer` (даёт полный контроль над scheduler-бином) или декларативно одной строкой в `application.yml`. Увеличение pool size позволяет независимым задачам выполняться параллельно вместо очереди на одном потоке (см. Q6).

Программный вариант — реализуем `SchedulingConfigurer` и подсовываем свой `ThreadPoolTaskScheduler`:

```java
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);               // 10 потоков
        scheduler.setThreadNamePrefix("sched-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();

        registrar.setTaskScheduler(scheduler);
    }
}
```

Здесь же настраивают graceful shutdown: `setWaitForTasksToCompleteOnShutdown(true)` плюс `setAwaitTerminationSeconds(...)` дают задачам доработать при остановке приложения, а не обрывают их посреди дела.

Декларативный вариант — через application.yml (Spring Boot 2.1+), без своего конфиг-класса:

```yaml
spring:
  task:
    scheduling:
      pool:
        size: 10
      thread-name-prefix: sched-
```

**Рекомендация:** если не нужен тонкий контроль над scheduler-бином, держите настройку пула в `application.yml` — её проще крутить под нагрузку по окружениям, без перекомпиляции.

## Q8. Могут ли задачи `@Scheduled` выполняться параллельно?

По умолчанию — нет: все задачи идут через один поток планировщика (Q6). Есть два пути сделать их параллельными, и важно различать, что именно они дают:

- **Увеличить pool size** планировщика (Q7) — тогда *разные* задачи могут идти одновременно, но один и тот же job по-прежнему не накладывается сам на себя.
- **`@Async`** — выносит выполнение в отдельный executor, освобождая поток планировщика мгновенно. Это позволяет *одной и той же* задаче запускаться несколькими экземплярами параллельно.

Вариант с `@Async`:

```java
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig { }

@Component
public class ParallelJob {

    @Async("schedulingExecutor")   // кастомный executor
    @Scheduled(fixedRate = 1_000)
    public void parallelTask() {
        // Запускается каждую секунду в отдельном потоке,
        // не блокируя другие задачи
    }
}

@Bean("schedulingExecutor")
public Executor schedulingExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.initialize();
    return executor;
}
```

**Подводный камень:** именно та свобода, которую даёт `@Async`, и есть его опасность. Если задача работает дольше своего интервала, её запуски начнут накладываться: несколько экземпляров одного job-а полезут в данные одновременно. Для идемпотентной задачи это не страшно, для остальных — race conditions и порча данных. Защита — блокировка перекрытия: ShedLock через `@SchedulerLock` либо собственный флаг «уже выполняется».

## Q9. Что такое `TaskScheduler` и когда его использовать?

`TaskScheduler` — Spring-интерфейс для **программного** планирования задач, в отличие от декларативного `@Scheduled`. Расписание здесь задаётся не в аннотации на этапе компиляции, а в рантайме вызовом метода. Именно поэтому им планируют то, что нельзя зашить в код заранее: задачи, добавляемые и отменяемые на лету.

Пример сервиса, который заводит задачи по cron и умеет их отменять по id:

```java
@Service
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void scheduleTask(String taskId, Runnable task, String cronExpression) {
        CronTrigger trigger = new CronTrigger(cronExpression);
        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
        scheduledTasks.put(taskId, future);
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
```

**Когда нужен `TaskScheduler`:**
- Расписание задаёт пользователь во время работы приложения (cron из UI/БД).
- Задачи нужно добавлять и удалять динамически, по событиям.
- Нужно выполнить задачу один раз в конкретный момент будущего.

**Когда достаточно `@Scheduled`:**
- Расписание фиксировано и известно на этапе разработки.
- Простые cron / fixedRate / fixedDelay без управления в рантайме.

Эмпирическое правило: если расписание можно записать в аннотацию и оно не меняется — берите `@Scheduled`; если оно живёт в данных и меняется на ходу — `TaskScheduler`.

## Q10. Как запланировать задачу динамически из кода?

Внедрить `TaskScheduler` и вызвать `schedule(...)` в нужный момент. Метод возвращает `ScheduledFuture` — его сохраняют, если задачу потом нужно будет отменить. Типичный сценарий — отложенное действие, привязанное к бизнес-событию: например, напоминание о неоплаченном заказе через N минут после его создания.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderReminderService {

    private final TaskScheduler scheduler;
    private final OrderRepository orderRepository;

    // Отправить напоминание через X минут
    public void scheduleReminder(Long orderId, int delayMinutes) {
        Instant fireAt = Instant.now().plusSeconds(delayMinutes * 60L);

        ScheduledFuture<?> future = scheduler.schedule(
            () -> sendReminder(orderId),
            fireAt
        );

        // Сохранить future, если нужна возможность отмены
        log.info("Scheduled reminder for order {} at {}", orderId, fireAt);
    }

    public void scheduleWithCron(String cronExpr, Runnable task) {
        scheduler.schedule(task, new CronTrigger(cronExpr));
    }

    private void sendReminder(Long orderId) {
        log.info("Sending reminder for order {}", orderId);
    }
}
```

Перегрузки `schedule` под разные сценарии:

- `schedule(Runnable, Instant)` — однократный запуск в конкретный момент времени.
- `schedule(Runnable, Trigger)` — повторяющийся запуск по расписанию (`CronTrigger` для cron, `PeriodicTrigger` для интервала).
- `scheduleAtFixedRate(Runnable, Duration)` — повтор с фиксированным интервалом.

**Важно для кластера:** такая динамическая задача живёт в памяти одного экземпляра. При нескольких репликах её увидит только тот pod, где её создали, а при рестарте она потеряется — для устойчивости расписание нужно хранить в БД и восстанавливать на старте (см. Q11–Q13).

## Q11. Почему `@Scheduled` проблематичен в кластере?

Потому что `@Scheduled` ничего не знает о других экземплярах приложения. Расписание живёт локально в каждой JVM, и если приложение поднято в нескольких репликах (Kubernetes, Docker Swarm), каждая независимо запустит свою копию задачи в одно и то же время.

```
Pod 1: [8:00 job runs] — генерирует отчёт
Pod 2: [8:00 job runs] — генерирует отчёт (дублирует!)
Pod 3: [8:00 job runs] — генерирует отчёт (дублирует!)
```

К чему это приводит:
- **Дублирование работы** — отчёт сгенерируется трижды, письмо уйдёт каждому клиенту по числу реплик.
- **Конкуренция за ресурсы** — реплики лезут в одни и те же строки БД, отсюда deadlocks и race conditions.
- **Повторное выполнение неидемпотентных операций** — списание денег или инкремент счётчика отработают несколько раз.

Суть проблемы: нужно, чтобы из всего кластера в данный тик задачу выполнил ровно один экземпляр. Способы это обеспечить:

1. **ShedLock** — distributed lock через общую БД или Redis; самый лёгкий вариант поверх существующего `@Scheduled` (Q12).
2. **Quartz Clustered** — enterprise-планировщик со своими таблицами и встроенной кластеризацией (Q13).
3. **Spring Batch** — если задача по сути batch-обработка данных.
4. **Один scheduler-pod** — вынести `@Scheduled` в отдельный неразмножаемый экземпляр. Просто, но это единая точка отказа и антипаттерн для prod.

## Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?

Идея ShedLock проста: общая lock-запись в БД или Redis выступает «дверной задвижкой». В момент тика каждый экземпляр пытается её взять; кто первым успел — выполняет задачу, остальные видят, что лок занят, и тихо пропускают запуск. Так из всего кластера срабатывает ровно один.

Важно понимать, что ShedLock **не планирует** задачи — расписанием по-прежнему управляет `@Scheduled`. Он лишь оборачивает выполнение блокировкой, поэтому интегрируется поверх имеющегося кода почти без изменений.

Зависимости (spring-обёртка плюс провайдер хранилища лока):

```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>5.10.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>5.10.0</version>
</dependency>
```

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")  // максимум держать лок 10 мин
public class SchedulingConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource,
            JdbcTemplateLockProvider.Configuration.builder()
                .withTableName("shedlock")
                .usingDbTime()
                .build());
    }
}

@Component
public class ReportJob {

    @Scheduled(cron = "0 0 8 * * *")
    @SchedulerLock(
        name = "ReportJob_generateReport",
        lockAtLeastFor = "PT5M",   // минимум держать лок 5 мин
        lockAtMostFor = "PT30M"    // максимум 30 мин (защита от смерти pod-а)
    )
    public void generateReport() {
        // Выполнится только в ОДНОМ экземпляре
    }
}
```

Два параметра, которые спрашивают чаще всего:

- **`lockAtMostFor`** — верхняя граница удержания лока. Если pod, взявший лок, упадёт, не отпустив его явно, по истечении этого времени лок освободится сам — иначе задача зависла бы навсегда. Ставят с запасом над реальным временем выполнения.
- **`lockAtLeastFor`** — нижняя граница: лок держится не меньше указанного, даже если задача отработала за секунду. Это защита от повторного запуска из-за рассинхрона часов между нодами: без неё быстрая задача успела бы отпустить лок и второй pod схватил бы его в тот же тик.

**SQL для создания таблицы:**

```sql
CREATE TABLE shedlock (
    name          VARCHAR(64)  NOT NULL,
    lock_until    TIMESTAMP    NOT NULL,
    locked_at     TIMESTAMP    NOT NULL,
    locked_by     VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);
```

Сама таблица хранит на запись одну строку: имя лока (`name` — оно же `name` из `@SchedulerLock`), время до которого лок занят (`lock_until`), момент захвата и идентификатор захватившего экземпляра. ShedLock умеет работать поверх БД, к которой вы уже подключены, — отдельной инфраструктуры заводить не нужно.

**Redis-провайдер** — если реляционной БД нет, лок-хранилищем выступает Redis (на TTL-ключах он даже легче):

```java
@Bean
public LockProvider lockProvider(RedisConnectionFactory cf) {
    return new RedisLockProvider(cf);
}
```

## Q13. Чем Quartz отличается от Spring Scheduling?

Коротко: Spring Scheduling — это легковесный планировщик «в памяти», а Quartz — полноценный enterprise-движок с персистентностью и встроенной кластеризацией. Главное различие — что происходит с задачами при перезапуске: у Spring расписание живёт в JVM и теряется, у Quartz хранится в БД (job store) и переживает рестарт.

| Критерий | Spring Scheduling | Quartz Scheduler |
|---|---|---|
| Персистентность | Нет (в памяти) | Да (JDBC job store) |
| Clustering | Нет нативно (ShedLock) | Встроенная поддержка |
| Гибкость | Простые аннотации | Triggers, JobStore, calendars |
| Мониторинг | Actuator `/health` | Встроенный UI (опционально) |
| Сложность setup | Низкая | Высокая |
| Динамическое изменение | Через `TaskScheduler` | Через `Scheduler` API |
| Spring Boot интеграция | Нативная | `spring-boot-starter-quartz` |

**Quartz** подходит когда:
- Задачи не должны теряться при перезапуске (persist to DB)
- Нативная кластеризация без внешнего lock-provider
- Нужен продвинутый UI для мониторинга задач
- Сложные расписания с календарями (выходные дни и т.д.)

**Эмпирическое правило:** для большинства микросервисов хватает связки **Spring Scheduling + ShedLock** — она проще в настройке и не тащит за собой набор таблиц Quartz. К Quartz переходят, когда задачи нельзя терять при рестарте, нужны сложные календарные расписания или продвинутый мониторинг из коробки.

## Q14. Как тестировать `@Scheduled`-задачи?

Ключевая идея: расписание тестировать почти никогда не нужно — нужно проверять **логику** метода. Поэтому в подавляющем большинстве случаев метод вызывают напрямую, без планировщика, как обычный unit-тест. Проверять, что задача сработала именно по расписанию, дорого и хрупко (нужно реальное ожидание во времени), и делают это лишь когда сама привязка ко времени критична.

**Вариант 1 — Unit-тест метода напрямую (основной):**

```java
class ReportJobTest {

    ReportJob job = new ReportJob(mock(ReportService.class));

    @Test
    void generateReport_shouldInvokeService() {
        job.generateReport();  // вызываем напрямую, без планировщика
        verify(reportService).generate();
    }
}
```

Здесь планировщик не участвует вообще: мы создаём объект руками и зовём метод. Это самый быстрый и стабильный тест, покрывающий собственно бизнес-логику.

**Вариант 2 — Integration-тест с ожиданием реального запуска:**

```java
@SpringBootTest
class ReportJobIntegrationTest {

    @SpyBean
    ReportJob reportJob;

    @Test
    void scheduledTask_shouldRunAtFixedRate() throws InterruptedException {
        // Ждём первого выполнения (если fixedRate маленький)
        verify(reportJob, timeout(5000).atLeastOnce()).generateReport();
    }
}
```

Здесь поднимается контекст, реальный планировщик действительно запускает задачу, а `timeout(5000)` ждёт срабатывания до 5 секунд. Подходит, только если интервал маленький, иначе тест будет долгим. `@SpyBean` оборачивает настоящий бин, чтобы можно было проверить вызов.

**Вариант 3 — Awaitility для асинхронного ожидания:**

```java
@Test
void scheduledTask_shouldBeCalledWithin3Seconds() {
    await().atMost(3, SECONDS)
           .until(() -> {
               verify(reportJob, atLeastOnce()).generateReport();
               return true;
           });
}
```

Awaitility опрашивает условие с интервалом до заданного таймаута и проходит, как только оно выполнилось, — тест не висит лишнего и читается яснее, чем `Thread.sleep`.

**Вариант 4 — Отключить реальную задачу в остальных тестах:**

```java
@SpringBootTest
@MockBean(classes = ReportJob.class)  // отключить реальную задачу в тестах
class ApplicationTest {
    @Test
    void contextLoads() { }
}
```

Это нужно, чтобы фоновые job-ы не дёргали внешние системы и не шумели во время прочих тестов: мок-бин подменяет настоящий, и `@Scheduled`-метод просто не выполняется.

## Q15. Как условно включать/отключать `@Scheduled`?

Есть три подхода, и они отличаются гранулярностью: значение cron гасит **один метод**, `@ConditionalOnProperty` и `@Profile` убирают **весь бин** с задачами из контекста. Выбор зависит от того, что отключаете — отдельный job или целую группу.

**Через конфигурацию — переопределить расписание (рекомендуется для одного метода):**

```yaml
# application-dev.yml — отключить в dev
reporting.cron: "-"

# application-prod.yml — включить в prod
reporting.cron: "0 0 8 * * MON-FRI"
```

```java
@Scheduled(cron = "${reporting.cron:-}")
public void generateReport() { }
```

Тут расписание просто переезжает в `application.yml` и меняется по окружениям: в dev подставляется `-` (никогда, см. Q3), в prod — реальный cron. Бин при этом остаётся в контексте, отключается только запуск метода.

**Через `@ConditionalOnProperty` — не создавать бин вовсе:**

```java
@Component
@ConditionalOnProperty(
    name = "reporting.enabled",
    havingValue = "true",
    matchIfMissing = false  // по умолчанию отключено
)
public class ReportJob {
    @Scheduled(cron = "0 0 8 * * *")
    public void generateReport() { }
}
```

Если свойство `reporting.enabled` не равно `true`, бин не попадает в контекст — вместе с ним «исчезают» и все его `@Scheduled`-методы. `matchIfMissing = false` делает дефолт безопасным: пока флаг явно не включили, задача не работает.

**Через `@Profile` — привязать бин к окружению:**

```java
@Component
@Profile("!test")  // не запускать в тестах
public class HeavyScheduledJob {
    @Scheduled(fixedRate = 60_000)
    public void run() { }
}
```

`@Profile("!test")` означает «во всех профилях, кроме `test`». Удобно, когда нужно отключить тяжёлые фоновые job-ы целиком на конкретном окружении, а не возиться с отдельными флагами.

## Q16. Какие типичные ошибки при работе с `@Scheduled`?

Коварство `@Scheduled` в том, что почти все типичные ошибки **не падают с исключением** — задача либо молча не стартует, либо тихо делает не то. Ниже — частые симптомы с причиной и фиксом.

| Симптом | Причина | Решение |
|---|---|---|
| Задача не запускается | Нет `@EnableScheduling` | Добавить на `@Configuration` или `@SpringBootApplication` |
| Задача не запускается | Нет `@Component` на классе | Добавить стереотип Spring |
| Задача не запускается | Метод не `public` | Сделать метод `public` |
| Дублирование в кластере | Нет distributed lock | Использовать ShedLock или Quartz |
| Задача «голодает» при долгом выполнении | Один поток по умолчанию | Настроить pool size |
| `fixedRate` накапливается | Долгое выполнение + один поток | Нет — Spring ждёт, но не копит. Или `@Async` |
| Cron не работает | Неправильный синтаксис | Spring cron — 6 полей (первое — секунды!) |
| Self-invocation `@Scheduled` | N/A | `@Scheduled` не вызывается из другого метода того же бина — не проблема |

Практический вывод для отладки: раз ошибки не сигналят сами, проверяйте косвенно. Первым делом убедитесь по логам старта, что метод вообще зарегистрирован планировщиком (Spring логирует найденные `@Scheduled`-методы) — это сразу отсекает половину таблицы. А распределённое дублирование вообще не видно на одной машине: его закладывают на этапе дизайна, как только приложение разворачивается в нескольких репликах.

---

## See also

- [Spring Boot](spring-boot-interview.md) — автоконфигурация `TaskSchedulingAutoConfiguration`, `spring.task.scheduling.*`
- [Spring Framework](spring-framework-interview.md) — `@EnableScheduling`, `SchedulingConfigurer`, `ThreadPoolTaskScheduler`
- [Spring AOP](spring-aop-interview.md) — `@Scheduled` реализован через AOP; проблема self-invocation неактуальна для scheduling
- [Apache Kafka](../../messaging/kafka-interview.md) — альтернатива: delayed messages для одноразовых задач по времени
- [Redis](../../databases/redis-interview.md) — ShedLock с Redis-провайдером как distributed lock
- [Spring Data JPA](spring-data-jpa-interview.md) — ShedLock с JDBC-провайдером (lock table в реляционной БД)
- [Kubernetes](../../devops/kubernetes-interview.md) — CronJob как альтернатива `@Scheduled` в k8s; один pod vs replica
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — проблема distributed scheduling, distributed lock
- [Unit Testing](../../testing/unit-testing-interview.md) — Awaitility, `@SpyBean`, тестирование временных задач
- [Docker](../../devops/docker-interview.md) — несколько реплик одного Docker-контейнера и проблема дублирования задач
- [Шпаргалка: Spring Scheduling: Полное руководство по](../../../frameworks/java-frameworks/spring/spring-scheduling.md) — теория
