---
title: "Вопросы на собеседовании: Планировщик Spring"
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
# Вопросы на собеседовании: Планировщик Spring

`Spring Scheduling` – механизм для выполнения задач по расписанию. `@Scheduled` – декларативный способ через аннотации. `TaskScheduler` – программный API для динамического планирования. Каждая реплика запускает своё расписание. Если запуски требуется координировать, применяют общую блокировку или кластерный планировщик. Примеры ниже используют Spring Framework 6.2 и Spring Boot 3.5, если не указано иное.

## Полезные ссылки

### Официальная документация

- [Spring Task Execution and Scheduling](https://docs.spring.io/spring-framework/reference/6.2/integration/scheduling.html)
- [Spring Boot 3.5 Task Execution and Scheduling](https://docs.spring.io/spring-boot/3.5/reference/features/task-execution-and-scheduling.html)
- [Quartz JDBC JobStore Clustering](https://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ConfigJDBCJobStoreClustering.html)
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

```text
Boot 3.5 обнаруживает компонент ReportJob. Метод generateDailyReport() уже помечен @Scheduled.
В приложении пока нет конфигурации, которая включает обработку этой аннотации.
Как её включить?
```

Нужны два шага: включить механизм аннотацией `@EnableScheduling` и пометить методы аннотацией `@Scheduled`. Наличие `spring-context` или автоконфигурация планировщика сами по себе не включают обработку `@Scheduled`. Для приведённых примеров достаточно зарегистрировать конфигурацию с `@EnableScheduling` и бин задачи.

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

**В plain Spring** (без Boot) можно использовать `<task:annotation-driven/>` в XML или `@EnableScheduling` в Java-конфигурации. Framework ищет однозначный подходящий `TaskScheduler` или подходящий бин с именем `taskScheduler`, затем аналогично ищет `ScheduledExecutorService`. Если подходящего бина нет, используется локальный однопоточный планировщик. В Boot при выполнении условий автоконфигурации создаётся бин планировщика.

## Q2. Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?

```text
Spring Framework 6.2, свободный ThreadPoolTaskScheduler, синхронный метод.
В двух независимых опытах первый запуск начался в 08:00:00 и завершился в 08:00:02.
Опыт A – fixedDelay = 5000. Опыт B – fixedRate = 5000.
Когда следующий запуск в каждом опыте и как отличается cron = "0 * * * * *"?
```

Это три способа задать расписание, и различаются они точкой отсчёта: `fixedDelay` считает паузу от **конца** предыдущего запуска, `fixedRate` задаёт шаг между **плановыми** моментами старта, а `cron` задаёт моменты по календарю в выбранном часовом поясе.

| Параметр | Что означает | Пример |
|---|---|---|
| `fixedDelay` | Задержка между концом предыдущего и началом следующего | `fixedDelay = 5000` |
| `fixedRate` | Шаг между плановыми стартами | `fixedRate = 5000` |
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

В опытах A и B следующие запуски доступны к исполнению соответственно в 08:00:07 и 08:00:05. Приведённый cron соответствует началу каждой минуты. Для синхронной периодической регистрации в `ThreadPoolTaskScheduler` отдельные выполнения не перекрываются даже при пуле из нескольких потоков. Если метод выполняется 8 секунд при периоде 5 секунд, следующий плановый момент уже прошёл. Такой запуск может начаться без дополнительной паузы, когда освободится поток. Период относится к плановым моментам, поэтому опоздавшие выполнения могут идти подряд. У обычного `CronTrigger` в этой модели следующий момент рассчитывается от завершения предыдущего выполнения, и совпадения во время долгой работы могут быть пропущены.

Когда что выбирать:

- `fixedDelay` – когда важна гарантированная пауза между запусками (например, опрос внешнего API без перегруза). Пауза фиксирована, а промежуток от старта до старта включает длительность работы.
- `fixedRate` – когда нужен регулярный плановый темп, например для сбора метрик. Фактический старт может опаздывать.
- **`cron`** — когда задача привязана ко времени суток или дням недели (отчёт каждый будний день в 8:00).

## Q3. Как работает синтаксис cron-выражений в Spring?

```text
Spring Framework 6.2, CronExpression, часовой пояс UTC.
Выражение – 0 0 8 5 * MON
Точка отсчёта – 1 апреля 2025 года, 00:00.
Как интерпретируются поля и какой ближайший момент соответствует выражению?
```

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
| `L` | последний день месяца или последнее в месяце вхождение заданного дня недели |
| `W` | ближайший рабочий день |

**Про `?` и поля «день месяца» / «день недели»:** в нативном Spring `CronExpression` (с 5.3 – парсер по умолчанию для `@Scheduled`) `?` означает «не важно» и полностью эквивалентен `*` – он допустим, но не обязателен. Можно задать оба поля конкретными значениями: выражение `0 0 8 5 * MON` валидно и требует совпадения **обоих** полей дня. В условии ближайшее совпадение приходится на понедельник 5 мая 2025 года в 08:00 UTC. Для правила по одному из полей второе можно оставить свободным с помощью `*` или `?`. В Quartz для правила по одному полю дня в другом используют `?`.

**Спецзначение Spring:** `@Scheduled(cron = "-")` — задача не выполняется никогда. Это удобно для отключения через конфигурацию: подставив `-` в значение из `application.yml`, задачу глушат без правки кода (см. Q5, Q15).

## Q4. Что такое `initialDelay` и зачем он нужен?

```text
Spring Framework 6.2. Свободный планировщик регистрирует задачу в 10:00:00.
@Scheduled(fixedRate = 60_000, initialDelay = 30_000)
Когда запланирован первый запуск? Можно ли указать ту же задержку вместе с cron?
```

`initialDelay` задаёт задержку перед **первым** запуском относительно регистрации задачи в планировщике. Единица по умолчанию для числового атрибута соответствует миллисекундам, её можно переопределить через `timeUnit`. В условии первый запуск запланирован на 10:00:30. Для периодических задач задержку можно сочетать с `fixedRate` или `fixedDelay`. С `cron` комбинация недопустима. В Framework 6.2 один `initialDelay` задаёт однократный запуск. Без задержки периодическая задача становится доступна к запуску сразу после регистрации.

```java
@Scheduled(fixedRate = 60_000, initialDelay = 30_000)
public void startAfterDelay() {
    // Первый запуск запланирован через 30 секунд после регистрации.
    // Плановый период составляет одну минуту.
}
```

**Зачем нужен:**
- Дать приложению время инициализироваться — прогреть кэш, поднять пул соединений к БД, чтобы задача не упала на холодном старте.
- Развести по времени старт нескольких задач, чтобы они не ударили по системе все разом при подъёме приложения.
- Дать внешней системе дополнительное время на запуск. Если нужна гарантия готовности, проверяйте её отдельно.

```java
@Component
public class CacheWarmupJob {

    @Scheduled(fixedDelay = 3_600_000, initialDelay = 60_000)
    public void warmupCache() {
        // Первый прогрев через минуту после регистрации.
        // Следующий старт через час после завершения предыдущего прогрева.
    }
}
```

Аналог через конфиг: `@Scheduled(fixedDelayString = "${cache.warmup.delay:60000}")`.

## Q5. Как вынести cron-выражение в конфигурацию?

```text
ReportJob использует одно расписание в dev и другое в prod.
Ключ настройки – reporting.cron. При отсутствии свойства нужно расписание ежедневно в 08:00.
Как связать настройку с @Scheduled?
```

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

**Зачем так делать:** одно приложение можно запускать с разным расписанием через профильные `application-*.yml` или переменные окружения. Обычная обработка `@Scheduled` разрешает placeholder при регистрации задачи. Для изменения расписания на работающем экземпляре нужна явная перерегистрация или программное планирование.

**Полезный трюк:** `@Scheduled(cron = "${job.cron:-}")` — значение по умолчанию `-`, то самое спецзначение «никогда» из Q3. Если свойство не задано ни в одном окружении, задача просто не запустится, и это безопасный дефолт.

## Q6. В каком потоке выполняется `@Scheduled`?

```text
Boot 3.5, @EnableScheduling включён, виртуальные потоки выключены.
Приложение не объявляет свои scheduler-бины или SchedulingConfigurer. Свойства пула не заданы.
Синхронная задача A занимает поток с 08:00:00 до 08:00:10. Задача B запланирована на 08:00:03.
В каком потоке выполняются задачи и сможет ли B начаться в 08:00:03?
```

В Boot 3.5 при включённом scheduling, выключенных виртуальных потоках и отсутствии своего scheduler-а или `SchedulingConfigurer` автоконфигурация выбирает `ThreadPoolTaskScheduler` с одним потоком по умолчанию. Синхронные задачи выполняются непосредственно в его рабочих потоках.

```
Поток 1: [task-A------][task-B--][task-C-----------]
```

Если задача A заняла единственный поток до 08:00:10, задача B, запланированная на 08:00:03, сможет начаться только после его освобождения. С пулом больше одного независимые задачи могут выполняться одновременно (Q7).

На Java 21+ при включённых виртуальных потоках та же автоконфигурация выбирает `SimpleAsyncTaskScheduler`. Он отправляет работу `fixedRate` и `cron` в отдельные потоки. Синхронные задачи `fixedDelay` выполняются на общем потоке планировщика. Свой scheduler или `@Async` меняет картину (Q8).
## Q7. Как настроить пул потоков для `@Scheduled`?

```text
Boot 3.5 использует автоконфигурацию планировщика, виртуальные потоки выключены.
Независимые синхронные задачи должны получить пул на десять рабочих потоков.
Как задать размер пула в конфигурации? Как задать его своим Spring-бином?
```

Размер можно задать в `application.yml` для автоконфигурируемого пула или настроить собственный `ThreadPoolTaskScheduler` как Spring-бин. Увеличение pool size позволяет независимым задачам выполняться параллельно вместо очереди на одном потоке (см. Q6).

В программном варианте объявляем единственный бин `TaskScheduler`. Если планировщиков несколько, нужный можно выбрать через `SchedulingConfigurer` или атрибут `scheduler` у `@Scheduled`.

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("sched-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}
```

Жизненным циклом бина управляет контейнер. При штатном закрытии контекста `setWaitForTasksToCompleteOnShutdown(true)` позволяет текущим задачам продолжиться, а `setAwaitTerminationSeconds(60)` ограничивает ожидание завершения executor-а в ходе его остановки. Эти настройки не гарантируют завершения любой задачи за 60 секунд.

Для автоконфигурируемого `ThreadPoolTaskScheduler` в Boot 3.5 размер задаётся в `application.yml`. При выборе `SimpleAsyncTaskScheduler` из-за виртуальных потоков настройки пула игнорируются.

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

```text
Spring Framework 6.2. Методы A и B имеют по одной регистрации fixedRate = 1000.
Работа в каждом занимает около трёх секунд. ThreadPoolTaskScheduler имеет два рабочих потока.
Сравните синхронный запуск с вариантом, где вызов A перехватывает @Async и у его исполнителя есть свободные потоки.
Какие выполнения могут перекрыться в каждом варианте?
```

С одной регистрацией на метод, одним рабочим потоком `ThreadPoolTaskScheduler` и синхронной работой методы выполняются по очереди. В условии сравниваются два способа изменить модель исполнения.

- **Пул планировщика** из двух потоков позволяет A и B выполняться одновременно. Выполнения одной синхронной регистрации `fixedRate` на `ThreadPoolTaskScheduler` друг друга не перекрывают. Несколько независимых регистраций, например при повторяемом `@Scheduled`, могут совпасть.
- `@Async` передаёт работу исполнителю, если асинхронный перехват включён и вызов проходит через прокси. При наличии свободных рабочих потоков A может начать следующий запуск до окончания предыдущего. При перегрузке исход зависит от настроек исполнителя.

Вариант с `@Async`:

```java
@Configuration
@EnableScheduling
@EnableAsync
class SchedulingConfig {

    @Bean("schedulingExecutor")
    ThreadPoolTaskExecutor schedulingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        return executor;
    }
}

@Component
public class ParallelJob {

    @Async("schedulingExecutor")   // кастомный executor
    @Scheduled(fixedRate = 1_000)
    public void parallelTask() {
        // Работа задачи
    }
}
```

Если выполнения могут пересекаться, учитывайте конкурентный доступ к данным даже для идемпотентных операций. Если перекрытие запрещено, защищайте всё фактическое выполнение, например локальной блокировкой в одном процессе или общей блокировкой между репликами. Блокировка только вокруг отправки в executor закончится раньше работы.

## Q9. Что такое `TaskScheduler` и когда его использовать?

```text
После запуска приложения пользователь сохраняет расписание отчёта, а затем удаляет его по ID.
Новые расписания должны начинать действовать в том же процессе без перезапуска.
Какой API выбрать и что сохранить для отмены?
```

`TaskScheduler` – Spring-интерфейс для **программного** планирования задач, в отличие от декларативного `@Scheduled`. Приложение передаёт расписание при вызове API во время работы, в том числе после запуска контекста. Именно поэтому им планируют то, что нельзя зашить в код заранее: задачи, добавляемые и отменяемые на лету.

Пример сервиса, который заводит задачи по cron и умеет их отменять по id:

```java
@Service
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void scheduleTask(String taskId, Runnable task, String cronExpression) {
        CronTrigger trigger = new CronTrigger(cronExpression);
        scheduledTasks.computeIfAbsent(taskId, id -> taskScheduler.schedule(task, trigger));
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
```

Пример оставляет действующую регистрацию при повторной передаче того же ID. Если `Trigger` не возвращает ни одного момента запуска, `schedule` может вернуть `null` и запись в карте не появится. `cancel(false)` отменяет будущие выполнения, позволяя уже начатому завершиться. Для замены расписания нужно отменить предыдущую регистрацию и создать новую. При конкурентном изменении одного ID эти операции координируют вместе.

**Когда нужен `TaskScheduler`:**
- Расписание задаёт пользователь во время работы приложения (cron из UI/БД).
- Задачи нужно добавлять и удалять динамически, по событиям.
- Нужно выполнить задачу один раз в конкретный момент будущего.

**Когда достаточно `@Scheduled`:**
- Расписание фиксировано и известно на этапе разработки.
- Простые cron / fixedRate / fixedDelay без управления в рантайме.

Эмпирическое правило: если расписание можно записать в аннотацию и оно не меняется — берите `@Scheduled`; если оно живёт в данных и меняется на ходу — `TaskScheduler`.

## Q10. Как запланировать задачу динамически из кода?

```text
При создании заказа в 14:00 нужно один раз вызвать sendReminder(orderId) через 15 минут.
В момент планирования orderId и срок уже известны. Нужно получить дескриптор для отмены.
Какую сигнатуру TaskScheduler использовать?
```

Внедрить `TaskScheduler` и вызвать `schedule(...)` в нужный момент. Метод возвращает `ScheduledFuture` – его сохраняют, если задачу потом нужно будет отменить. В условии целевой момент составляет 14:15. Если напоминание зависит от состояния заказа, проверку оплаты выполняют при запуске задачи. Типичный сценарий – отложенное действие, привязанное к бизнес-событию: например, напоминание о неоплаченном заказе через N минут после его создания.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderReminderService {

    private final TaskScheduler scheduler;

    // Отправить напоминание через X минут
    public ScheduledFuture<?> scheduleReminder(Long orderId, int delayMinutes) {
        Instant fireAt = scheduler.getClock().instant().plusSeconds(delayMinutes * 60L);

        ScheduledFuture<?> future = scheduler.schedule(
            () -> sendReminder(orderId),
            fireAt
        );

        log.info("Scheduled reminder for order {} at {}", orderId, fireAt);
        return future;
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

```text
Три реплики приложения запущены к 08:00 в одной зоне UTC. У каждой есть один экземпляр ReportJob.
В каждом контексте зарегистрировано расписание на 08:00, общего механизма координации нет.
Сколько реплик попытается отправить один и тот же отчёт?
```

Потому что `@Scheduled` ничего не знает о других экземплярах приложения. Расписание живёт локально в каждой JVM, и если приложение поднято в нескольких репликах (Kubernetes, Docker Swarm), каждая реплика планирует своё выполнение. В условии отчёт попытаются отправить три реплики. Фактические старты могут прийтись на разные моменты.

```
Pod 1: [8:00 job runs] — генерирует отчёт
Pod 2: [8:00 job runs] — генерирует отчёт (дублирует!)
Pod 3: [8:00 job runs] — генерирует отчёт (дублирует!)
```

К чему это приводит:
- **Дублирование работы** — отчёт сгенерируется трижды, письмо уйдёт каждому клиенту по числу реплик.
- **Конкуренция за ресурсы** — реплики лезут в одни и те же строки БД, отсюда deadlocks и race conditions.
- **Повторное выполнение неидемпотентных операций** — списание денег или инкремент счётчика отработают несколько раз.

Подход зависит от требований к повторам и пропущенным запускам. Для координации между репликами применяют несколько вариантов.

1. **ShedLock** — distributed lock через общую БД или Redis; самый лёгкий вариант поверх существующего `@Scheduled` (Q12).
2. **Quartz Clustered** — enterprise-планировщик со своими таблицами и встроенной кластеризацией (Q13).
3. **Spring Batch** подходит для пакетной работы. Общий `JobRepository` и одинаковые идентифицирующие параметры помогают координировать запуски одного экземпляра job. Расписание и параметры запуска задаются отдельно.
4. **Один scheduler-pod** сокращает число планирующих экземпляров, но его недоступность может привести к пропуску тика. Для операций с побочным эффектом отдельно предусматривают защиту от повторов.

## Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?

```text
Две реплики используют ShedLock, одну таблицу и одно имя блокировки. Локальное расписание задано через @Scheduled.
lockAtLeastFor = PT5M, lockAtMostFor = PT30M. Реплика A получила блокировку в 08:00:00.
Вариант 1 – A завершилась за секунду, B пытается начать в 08:00:02.
Вариант 2 – A ещё работает в 08:31, B в это время пытается получить блокировку.
Что произойдёт в каждом варианте и гарантирует ли схема выполнение каждого тика?
```

Реплики, которые используют один провайдер хранилища и одно имя блокировки, пытаются захватить общую аренду. Пока она действует, конкурентная попытка пропускается без ожидания. В первом варианте B пропустит запуск. Во втором аренда уже могла истечь, поэтому B может начать работу одновременно с A. Пропущенные запуски ShedLock не ставит в очередь.

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
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
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
        // Синхронная генерация отчёта под блокировкой
    }
}
```

Два параметра, которые спрашивают чаще всего:

- `lockAtMostFor` – верхняя граница удержания лока. Если pod, взявший лок, упадёт, не отпустив его явно, по истечении этого времени другая реплика сможет претендовать на блокировку. Истечение аренды не останавливает исходную задачу. Значение выбирают с запасом относительно максимально ожидаемого времени выполнения.
- **`lockAtLeastFor`** — нижняя граница: лок держится не меньше указанного, даже если задача отработала за секунду. Это защита от повторного запуска из-за рассинхрона часов между нодами: без неё быстрая задача успела бы отпустить лок и второй pod схватил бы его в тот же тик.

**SQL для создания таблицы в PostgreSQL:**

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

Вместо JDBC можно подключить модуль `shedlock-provider-redis-spring` и зарегистрировать Redis-провайдер. Для примера используется общее окружение приложения `reports`.

```java
@Bean
public LockProvider lockProvider(RedisConnectionFactory cf) {
    return new RedisLockProvider(cf, "reports");
}
```

## Q13. Чем Quartz отличается от Spring Scheduling?

```text
Пользователи создают расписания во время работы приложения. Они должны пережить перезапуск всех реплик.
Две реплики должны координировать запуск по общим триггерам. Для отдельных задач нужны календарные исключения.
Какой механизм и какое хранилище подходят?
```

Для условия подходит Quartz с JDBC JobStore, общей БД и включённой кластеризацией. В Boot 3.5 Quartz по умолчанию использует хранилище в памяти. При закрытии процесса программные регистрации `TaskScheduler` теряются, а декларации `@Scheduled` регистрируются заново при следующем старте контекста.

| Критерий | Spring Scheduling | Quartz Scheduler |
|---|---|---|
| Персистентность регистраций | Память процесса | Память по умолчанию, БД с JDBC JobStore |
| Кластеризация | Внешняя координация, например ShedLock | Общая БД и включённая кластеризация JDBC JobStore |
| Гибкость | Аннотации и `TaskScheduler` | Триггеры, хранилища, календари |
| Диагностика в Boot 3.5 | Actuator `scheduledtasks` | Actuator `quartz` |
| Сложность setup | Низкая | Высокая |
| Динамическое изменение | Через `TaskScheduler` | Через `Scheduler` API |
| Spring Boot интеграция | Нативная | `spring-boot-starter-quartz` |

**Quartz** подходит когда:
- Задачи не должны теряться при перезапуске (persist to DB)
- Нативная кластеризация без внешнего lock-provider
- Сложные расписания с календарями (выходные дни и т.д.)

ShedLock подходит, если расписание остаётся локальным, нужна защита от перекрытия и допустим пропуск конкурентного запуска. Для Quartz с JDBC в Boot задают `spring.quartz.job-store-type=jdbc`, готовят схему и включают `org.quartz.jobStore.isClustered=true` через `spring.quartz.properties`. Поведение при пропущенных срабатываниях и сбоях определяют настройки триггера и восстановления job.

## Q14. Как тестировать `@Scheduled`-задачи?

```text
Boot 3.5. Spring-бин ReportJob вызывает ReportService.generate().
Нужно отдельно проверить бизнес-вызов без запуска контекста и регистрацию задачи в приложении с включённым scheduling.
У job задано ежедневное расписание через свойство reporting.cron. В тесте можно изменить свойство и подменить ReportService.
Как построить эти проверки?
```

Прямой вызов проверяет работу метода без участия инфраструктуры. Тест с запущенным контекстом и коротким тестовым расписанием проверяет, что инфраструктура вызвала задачу. Точные календарные правила можно проверять через `CronExpression.next(...)` с фиксированной датой и зоной, без ожидания тика. Для примеров ниже используется следующий бин.

```java
@Component
class ReportJob {

    private final ReportService reportService;

    ReportJob(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "${reporting.cron:0 0 8 * * *}")
    public void generateReport() {
        reportService.generate();
    }
}
```

**Модульный тест прямым вызовом**

```java
class ReportJobTest {

    ReportService reportService = mock(ReportService.class);
    ReportJob job = new ReportJob(reportService);

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
@SpringBootTest(properties = "reporting.cron=* * * * * *")
class ReportJobIntegrationTest {

    @MockitoBean
    ReportService reportService;

    @Test
    void scheduledTask_shouldInvokeService() {
        verify(reportService, timeout(5000).atLeastOnce()).generate();
    }
}
```

Контекст приложения включает `@EnableScheduling`. Расписание в тесте даёт возможность запускаться каждую секунду, а `timeout(5000)` ограничивает ожидание наблюдаемого вызова пятью секундами. `@MockitoBean` из Framework 6.2 подменяет сервис отчётов и позволяет запускать настоящий job без отправки отчёта.

**Вариант 3 — Awaitility для асинхронного ожидания:**

```java
@Test
void scheduledTask_shouldBeCalledWithin3Seconds() {
    await().atMost(3, SECONDS)
           .untilAsserted(() -> verify(reportService, atLeastOnce()).generate());
}
```

Awaitility опрашивает условие с интервалом до заданного таймаута и проходит, как только оно выполнилось, — тест не висит лишнего и читается яснее, чем `Thread.sleep`.

**Вариант 4 — Отключить реальную задачу в остальных тестах:**

```java
@SpringBootTest(properties = "reporting.cron=-")
class ApplicationTest {
    @Test
    void contextLoads() { }
}
```

Свойство отключает cron-регистрацию `generateReport()` в этом контексте. Остальные тесты могут пользоваться бином без фоновой отправки отчётов (Q15).

## Q15. Как условно включать/отключать `@Scheduled`?

```text
В dev метод generateReport() не должен запускаться по расписанию, но ReportJob нужен другим бинам.
Для второго компонента HeavyScheduledJob в профиле test требуется вообще не создавать бин.
Какие настройки дадут оба результата?
```

Есть три подхода, и они отличаются гранулярностью: значение cron гасит **один метод**, `@ConditionalOnProperty` и `@Profile` убирают **весь бин** с задачами из контекста. Выбор зависит от того, что отключаете — отдельный job или целую группу.

**Через конфигурацию — переопределить расписание (рекомендуется для одного метода):**

```yaml
# application-dev.yml
reporting.cron: "-"
```

```yaml
# application-prod.yml
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

```text
Spring Framework 6.2. @EnableScheduling включён. Рассмотрите случаи независимо.
A. Объект с корректным @Scheduled создаётся через new вне контейнера.
B. Для зарегистрированного бина задан cron = "0 8 * * MON-FRI".
C. Две реплики имеют одинаковую локально зарегистрированную задачу без общей координации.
Как диагностировать эти три случая?
```

В случае A Spring не обрабатывает аннотацию у объекта вне контейнера. В B обычное выражение из пяти полей не разбирается как Spring cron, и ошибка обнаруживается при регистрации. В C обе реплики могут выполнять задачу независимо. Похожие симптомы возникают и по другим причинам.

| Симптом | Причина | Решение |
|---|---|---|
| Задача не запускается | Нет `@EnableScheduling` | Добавить на `@Configuration` или `@SpringBootApplication` |
| Задача не регистрируется | Объект не является Spring-бином | Зарегистрировать через component scan или `@Bean` |
| Метод нельзя вызвать на проксированном бине | Метод отсутствует в интерфейсе JDK-прокси или недоступен через используемый прокси | Проверить сигнатуру и доступность метода с учётом типа прокси |
| Дублирование в кластере | Нет distributed lock | Использовать ShedLock или Quartz |
| Задача «голодает» при долгом выполнении | Один поток по умолчанию | Настроить pool size |
| Запуски `fixedRate` опаздывают | Синхронное выполнение дольше периода | Сравнить длительность с периодом, учесть возможность последовательных запусков без пауз |
| Cron не работает | Неправильный синтаксис | Spring cron — 6 полей (первое — секунды!) |
| Прямой вызов метода из того же бина | Обычный Java-вызов | Для `@Scheduled` расписание регистрируется постпроцессором. Другие аннотации на методе, например `@Async`, могут зависеть от прокси |

Для диагностики сначала проверьте регистрацию и выбранный планировщик. В Boot 3.5 зарегистрированные задачи можно посмотреть через `/actuator/scheduledtasks` при доступности и HTTP-экспозиции эндпоинта. Для проверки дублирования сопоставьте фактические запуски на нескольких репликах.

---

## See also

- [Spring Boot](spring-boot-interview.md) — автоконфигурация `TaskSchedulingAutoConfiguration`, `spring.task.scheduling.*`
- [Spring Framework](spring-framework-interview.md) — `@EnableScheduling`, `SchedulingConfigurer`, `ThreadPoolTaskScheduler`
- [Spring AOP](spring-aop-interview.md) · прокси влияют на доступность метода и работу `@Async`. Регистрацию `@Scheduled` выполняет постпроцессор
- [Apache Kafka](../../messaging/kafka-interview.md) · транспорт для передачи задач воркерам при отдельном механизме отложенной доставки
- [Redis](../../databases/redis-interview.md) — ShedLock с Redis-провайдером как distributed lock
- [Spring Data JPA](spring-data-jpa-interview.md) — ShedLock с JDBC-провайдером (lock table в реляционной БД)
- [Kubernetes](../../devops/kubernetes-interview.md) — CronJob как альтернатива `@Scheduled` в k8s; один pod vs replica
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — проблема distributed scheduling, distributed lock
- [Unit Testing](../../testing/unit-testing-interview.md) · Mockito, Awaitility и тестирование задач с расписанием
- [Docker](../../devops/docker-interview.md) — несколько реплик одного Docker-контейнера и проблема дублирования задач
- [Шпаргалка: Spring Scheduling: Полное руководство по](../../../frameworks/java-frameworks/spring/spring-scheduling.md) — теория
