---
title: "Вопросы на собеседовании: Spring Scheduling"
description: "Spring Scheduling: @Scheduled, @EnableScheduling, fixedDelay/fixedRate/cron, TaskScheduler, динамическое планирование, ShedLock для кластеров, тестирование"
tags:
  - interview
  - spring
  - spring-scheduling-interview
aliases:
  - "Spring Scheduling interview"
  - "Spring @Scheduled interview"
  - "Spring Scheduling собеседование"
  - "Spring cron задачи интервью"
difficulty: "intermediate"
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
- [Q1. (!) Как настроить Spring Scheduling в Spring Boot?](#q1-как-настроить-spring-scheduling-в-spring-boot)
- [Q2. (!) Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?](#q2-чем-отличаются-fixeddelay-fixedrate-и-cron-в-scheduled)
- [Q3. Как работает синтаксис cron-выражений в Spring?](#q3-как-работает-синтаксис-cron-выражений-в-spring)
- [Q4. Что такое `initialDelay` и зачем он нужен?](#q4-что-такое-initialdelay-и-зачем-он-нужен)
- [Q5. Как вынести cron-выражение в конфигурацию?](#q5-как-вынести-cron-выражение-в-конфигурацию)

**Пул потоков и параллельность**
- [Q6. (!) В каком потоке выполняется `@Scheduled`?](#q6-в-каком-потоке-выполняется-scheduled)
- [Q7. Как настроить пул потоков для `@Scheduled`?](#q7-как-настроить-пул-потоков-для-scheduled)
- [Q8. Могут ли задачи `@Scheduled` выполняться параллельно?](#q8-могут-ли-задачи-scheduled-выполняться-параллельно)

**TaskScheduler API**
- [Q9. Что такое `TaskScheduler` и когда его использовать?](#q9-что-такое-taskscheduler-и-когда-его-использовать)
- [Q10. Как запланировать задачу динамически из кода?](#q10-как-запланировать-задачу-динамически-из-кода)

**Кластерная среда**
- [Q11. (!) Почему `@Scheduled` проблематичен в кластере?](#q11-почему-scheduled-проблематичен-в-кластере)
- [Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?](#q12-как-решить-проблему-с-scheduled-в-кластере-с-помощью-shedlock)
- [Q13. Чем Quartz отличается от Spring Scheduling?](#q13-чем-quartz-отличается-от-spring-scheduling)

**Тестирование**
- [Q14. Как тестировать `@Scheduled`-задачи?](#q14-как-тестировать-scheduled-задачи)

**Дополнительно**
- [Q15. Как условно включать/отключать `@Scheduled`?](#q15-как-условно-включатьотключать-scheduled)
- [Q16. Какие типичные ошибки при работе с `@Scheduled`?](#q16-какие-типичные-ошибки-при-работе-с-scheduled)

---

## Q1. Как настроить Spring Scheduling в Spring Boot?

В Spring Boot добавить `@EnableScheduling` на конфигурационный или главный класс:

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

**В plain Spring** (без Boot) нужен `<task:annotation-driven/>` в XML или `@EnableScheduling` + регистрация `TaskScheduler`-бина.

В Spring Boot `@EnableScheduling` включает поиск всех `@Scheduled`-методов в Spring-бинах.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?

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

Если задача выполняется дольше, чем `fixedRate`, следующий запуск начинается немедленно после завершения (не накапливается очередь запусков — один поток).

`fixedDelay` — для задач, где важна пауза между запусками.  
`fixedRate` — для задач с фиксированным темпом.  
`cron` — для задач по времени суток/недели.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как работает синтаксис cron-выражений в Spring?

Spring использует **6 полей** (Unix cron — 5), добавляя секунды:

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

**Отличие от Linux cron:** первое поле в Spring — **секунды** (в Unix cron — минуты).

**Spring Special:** `@Scheduled(cron = "-")` — никогда не выполняется (удобно для отключения через конфигурацию).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое `initialDelay` и зачем он нужен?

`initialDelay` — задержка перед первым запуском (в миллисекундах):

```java
@Scheduled(fixedRate = 60_000, initialDelay = 30_000)
public void startAfterDelay() {
    // Первый запуск через 30 сек после старта приложения,
    // затем каждую минуту
}
```

**Зачем нужен:**
- Дать приложению время инициализироваться (загрузить кэш, подключиться к БД)
- Распределить нагрузку при одновременном старте нескольких задач
- Подождать готовности внешних систем

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Как вынести cron-выражение в конфигурацию?

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

**Полезный трюк:** `@Scheduled(cron = "${job.cron:-}")` — если свойство не задано, задача никогда не запустится.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. В каком потоке выполняется `@Scheduled`?

По умолчанию Spring использует **один поток** (`ThreadPoolTaskScheduler` с pool size = 1) для всех `@Scheduled`-задач. Задачи выполняются последовательно.

```
Поток 1: [task-A------][task-B--][task-C-----------]
```

Если `task-A` занимает долго — `task-B` ждёт.

**Почему важно:** `fixedRate` задача не будет запускаться параллельно — она будет «опаздывать» если предыдущий запуск ещё не завершён.

Для параллельного выполнения нужно настроить пул (см. Q7).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как настроить пул потоков для `@Scheduled`?

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

Или через application.yml (Spring Boot 2.1+):

```yaml
spring:
  task:
    scheduling:
      pool:
        size: 10
      thread-name-prefix: sched-
```

**Совет:** выносить конфигурацию пула в `application.yml` — проще менять под нагрузку без перекомпиляции.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Могут ли задачи `@Scheduled` выполняться параллельно?

По умолчанию — нет (один поток). Для параллельности используют `@Async`:

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

**Подводный камень:** `@Async` + `@Scheduled` может привести к тому, что несколько запусков одной задачи работают одновременно. Если задача не является идемпотентной — это проблема. Для таких случаев нужна блокировка (ShedLock или `@SchedulerLock`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Что такое `TaskScheduler` и когда его использовать?

`TaskScheduler` — Spring-интерфейс для программного (не аннотационного) планирования задач:

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

**Когда использовать:**
- Расписание задаётся пользователем во время работы приложения
- Динамическое добавление/удаление задач
- Выполнение задачи один раз в будущем

**Когда достаточно `@Scheduled`:**
- Фиксированное расписание, известное на этапе разработки
- Простые cron/fixedRate/fixedDelay задачи


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как запланировать задачу динамически из кода?

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

`TaskScheduler.schedule(Runnable, Instant)` — один раз в момент времени.  
`TaskScheduler.schedule(Runnable, Trigger)` — по расписанию (CronTrigger, PeriodicTrigger).  
`TaskScheduler.scheduleAtFixedRate(Runnable, Duration)` — с фиксированным интервалом.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Почему `@Scheduled` проблематичен в кластере?

Если одно Spring-приложение запущено в нескольких экземплярах (Kubernetes replicas, Docker Swarm), каждый экземпляр независимо запустит свою `@Scheduled`-задачу.

```
Pod 1: [8:00 job runs] — генерирует отчёт
Pod 2: [8:00 job runs] — генерирует отчёт (дублирует!)
Pod 3: [8:00 job runs] — генерирует отчёт (дублирует!)
```

**Проблемы:**
- Дублирование работы (отчёты дважды, письма трижды)
- Конкурентный доступ к ресурсам (deadlocks, race conditions)
- Неидемпотентные операции выполняются несколько раз

**Решения:**
1. **ShedLock** — distributed lock через БД/Redis (Q12)
2. **Quartz Clustered** — enterprise-решение с собственными таблицами БД (Q13)
3. **Spring Batch** — если задача — batch-обработка данных
4. **One pod** — держать один «scheduler» pod с `@Scheduled` без репликации (антипаттерн для prod)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?

ShedLock использует lock-таблицу в БД/Redis чтобы гарантировать что задача выполняется максимум в одном экземпляре:

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

**Redis провайдер** (если нет реляционной БД):

```java
@Bean
public LockProvider lockProvider(RedisConnectionFactory cf) {
    return new RedisLockProvider(cf);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Чем Quartz отличается от Spring Scheduling?

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

**Spring Scheduling + ShedLock** — в большинстве microservices проектов это достаточно и значительно проще в настройке.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Как тестировать `@Scheduled`-задачи?

**Вариант 1 — Unit test метода напрямую:**

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

**Вариант 2 — Integration test с ожиданием выполнения:**

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

**Вариант 3 — Awaitility для async:**

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

**Вариант 4 — Тест без планировщика:**

```java
@SpringBootTest
@MockBean(classes = ReportJob.class)  // отключить реальную задачу в тестах
class ApplicationTest {
    @Test
    void contextLoads() { }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Как условно включать/отключать `@Scheduled`?

**Через конфигурацию (рекомендованный способ):**

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

**Через `@ConditionalOnProperty`:**

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

**Через `@Profile`:**

```java
@Component
@Profile("!test")  // не запускать в тестах
public class HeavyScheduledJob {
    @Scheduled(fixedRate = 60_000)
    public void run() { }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Какие типичные ошибки при работе с `@Scheduled`?

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

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Spring Boot](spring-boot-interview.md) — автоконфигурация `TaskSchedulingAutoConfiguration`, `spring.task.scheduling.*`
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
