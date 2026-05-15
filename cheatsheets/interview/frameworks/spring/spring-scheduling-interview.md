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
>
> **Вопрос:** Что нужно сделать в Spring Boot приложении, чтобы методы, помеченные `@Scheduled`, начали выполняться по расписанию?
>
> ---
>
> #### A) Добавить `@EnableScheduling` на конфигурационный класс (или на главный класс с `@SpringBootApplication`) и пометить методы `@Scheduled` в Spring-бинах — ✓ Верно
>
> **Развёрнутое объяснение:** Annotation-based scheduling включается через `@EnableScheduling`. Эта аннотация регистрирует `ScheduledAnnotationBeanPostProcessor`, который сканирует все Spring-бины, находит методы с `@Scheduled` и регистрирует их в `TaskScheduler`. Без `@EnableScheduling` Spring физически не знает, что нужно искать `@Scheduled`-методы — они будут просто проигнорированы. В Spring Boot из коробки этой аннотации НЕТ — её надо добавить вручную.
>
> **Пример:**
> ```java
> @SpringBootApplication
> @EnableScheduling
> public class MyApp {
>     public static void main(String[] args) {
>         SpringApplication.run(MyApp.class, args);
>     }
> }
>
> @Component
> public class ReportJob {
>     @Scheduled(cron = "0 0 8 * * MON-FRI")
>     public void generateDailyReport() { /* ... */ }
> }
> ```
>
> **Когда применять:** всегда, когда нужно декларативно запускать задачи по расписанию (cron, fixedDelay, fixedRate) в Spring/Spring Boot приложении.
>
> **Подводные камни:** метод должен быть в Spring-бине (не в обычном объекте `new`); по умолчанию метод не должен иметь параметров и возвращать `void`; `@Scheduled` не работает на `private`-методах через CGLIB-прокси; по умолчанию все задачи выполняются в одном single-threaded `TaskScheduler` — для параллельности нужен кастомный пул.
>
> **Связанные вопросы:** [[Q6]] (в каком потоке выполняется), [[Q7]] (как настроить пул потоков).
>
> ---
>
> #### B) Достаточно пометить методы `@Scheduled` — Spring Boot включает scheduling автоматически через autoconfiguration — ❌ Неверно
>
> **Что на самом деле:** Spring Boot имеет `TaskSchedulingAutoConfiguration`, которая регистрирует `TaskScheduler`-бин, но НЕ включает обработку `@Scheduled` без явной `@EnableScheduling`. Без этой аннотации `ScheduledAnnotationBeanPostProcessor` не регистрируется и методы не сканируются.
>
> **Откуда путаница:** Spring Boot действительно автоконфигурирует много вещей (DataSource, Web MVC и т.п.), и кажется, что scheduling тоже должен включаться сам. Также `TaskSchedulingAutoConfiguration` создаёт `ThreadPoolTaskScheduler`-бин, что подкрепляет иллюзию.
>
> **Если бы это было правдой:** любой бин с методом `@Scheduled` запускался бы автоматически в каждом приложении — это нарушило бы принцип явного opt-in для побочных эффектов и приводило бы к неожиданным запускам в тестах/CLI-приложениях.
>
> ---
>
> #### C) Добавить зависимость `spring-boot-starter-scheduling` и пометить методы `@Scheduled` — ❌ Неверно
>
> **Что на самом деле:** стартера `spring-boot-starter-scheduling` НЕ существует. Scheduling-инфраструктура (`@Scheduled`, `TaskScheduler`, `@EnableScheduling`) входит в `spring-context` — модуль, который тянется любым Spring Boot стартером (`spring-boot-starter`).
>
> **Откуда путаница:** у Spring Boot много starter-ов с предсказуемыми именами (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`), и логично предположить, что для scheduling тоже есть отдельный.
>
> **Если бы это было правдой:** разработчики получали бы `ClassNotFoundException` на `@Scheduled` без стартера. Но в реальности класс доступен и без него — нужна только активация через `@EnableScheduling`.
>
> ---
>
> #### D) Реализовать интерфейс `SchedulingConfigurer` и зарегистрировать задачи программно через `ScheduledTaskRegistrar` — ❌ Неверно
>
> **Что на самом деле:** `SchedulingConfigurer` — это OPTIONAL способ для продвинутой настройки (например, динамическая регистрация задач из БД или кастомный `TaskScheduler`). Для базового использования `@Scheduled` он не нужен — достаточно `@EnableScheduling`.
>
> **Откуда путаница:** `SchedulingConfigurer` часто упоминается в туториалах по динамическому планированию, и можно решить, что это основной способ.
>
> **Если бы это было правдой:** декларативный подход `@Scheduled` потерял бы смысл — пришлось бы программно описывать каждую задачу. На практике 90% задач остаются на аннотациях, а `SchedulingConfigurer` используется только для специальных случаев.

## Q2. Чем отличаются `fixedDelay`, `fixedRate` и `cron` в `@Scheduled`?

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
>
> **Вопрос:** Метод `processOrders()` помечен `@Scheduled(fixedRate = 5000)` и выполняется в среднем 8 секунд. Параллельно есть `@Scheduled(fixedDelay = 5000)` на методе `cleanup()`, который тоже выполняется ~8 секунд. Какое утверждение про интервалы запусков корректно?
>
> ---
>
> #### A) `fixedRate` гарантирует запуск ровно каждые 5 секунд независимо от длительности — следующий запуск начнётся в новом потоке через 5 секунд после старта предыдущего — ❌ Неверно
>
> **Что на самом деле:** `fixedRate` действительно задаёт интервал «от начала до начала», НО по умолчанию Spring использует single-threaded scheduler — если предыдущий запуск ещё не завершился, следующий ожидает завершения и стартует сразу после. Параллельных запусков одного и того же метода по умолчанию НЕ происходит.
>
> **Откуда путаница:** документация говорит «interval between successive invocations», и легко решить, что это жёсткая гарантия с параллельностью.
>
> **Если бы это было правдой:** при долгих задачах накапливались бы тысячи параллельных потоков и происходил бы DDoS на ресурсы (БД, внешние API). Single-thread по умолчанию — это защита.
>
> ---
>
> #### B) `fixedRate` задаёт интервал между НАЧАЛАМИ запусков; `fixedDelay` — паузу между КОНЦОМ предыдущего и НАЧАЛОМ следующего; `cron` — расписание по времени суток. При долгих задачах в single-thread `fixedRate` фактически вырождается в «запуск сразу после завершения» — ✓ Верно
>
> **Развёрнутое объяснение:** Три семантики:
> - `fixedRate = 5000`: целевой интервал «start-to-start» = 5 сек. Если задача 8 сек — следующий запуск стартует сразу после её завершения (нельзя нарушить single-thread).
> - `fixedDelay = 5000`: пауза между «end предыдущего» и «start следующего» = 5 сек. При задаче 8 сек реальный цикл = 8 + 5 = 13 сек.
> - `cron`: запуск по календарному расписанию (например, «каждый день в 8:00»).
>
> **Пример:**
> ```java
> @Scheduled(fixedRate = 5_000)
> public void processOrders() { /* 8 сек */ }
> // Цикл: start → 8с → start → 8с → ... (rate "залипает" в 8с, не 5с)
>
> @Scheduled(fixedDelay = 5_000)
> public void cleanup() { /* 8 сек */ }
> // Цикл: start → 8с (end) → пауза 5с → start → 8с → пауза 5с → ...  = 13с
>
> @Scheduled(cron = "0 0 * * * *")
> public void hourly() { /* в начале каждого часа */ }
> ```
>
> **Когда применять:**
> - `fixedDelay` — когда важно избежать одновременной работы и дать системе «отдохнуть» (cleanup, polling-задачи, ретраи).
> - `fixedRate` — когда нужен стабильный темп (метрики, heartbeat). Только если задача гарантированно быстрее интервала.
> - `cron` — когда привязка к календарю (8:00 утра, конец месяца, понедельники).
>
> **Подводные камни:** `fixedRate` при долгой задаче не накапливает «пропущенные» запуски — выполняется один раз, как только освободится поток. Чтобы получить параллельные запуски, нужен пул потоков (`@Async` + `TaskScheduler` с многопоточным executor-ом) и осознанная стратегия идемпотентности.
>
> **Связанные вопросы:** [[Q3]] (cron syntax), [[Q6]] (поток выполнения), [[Q7]] (пул потоков).
>
> ---
>
> #### C) `fixedDelay` и `fixedRate` — синонимы, оба задают период «от начала до начала»; разница только в `cron`, который работает по календарю — ❌ Неверно
>
> **Что на самом деле:** это разные параметры с разной семантикой. `fixedDelay` отсчитывается ОТ КОНЦА предыдущего выполнения, `fixedRate` — ОТ НАЧАЛА. При задаче длительности T реальный период: для `fixedDelay` = T + delay, для `fixedRate` = max(T, rate).
>
> **Откуда путаница:** имена параметров похожи; в простых примерах с быстрой задачей (например, T < period) поведение визуально совпадает.
>
> **Если бы это было правдой:** не было бы смысла иметь два параметра — Spring оставил бы только один. Существование обоих указывает на смысловое различие.
>
> ---
>
> #### D) `cron` выполняется через отдельный планировщик Quartz, а `fixedRate`/`fixedDelay` — через простой `Timer`. Поэтому смешивать их в одном приложении нельзя — ❌ Неверно
>
> **Что на самом деле:** все три параметра обрабатываются ОДНИМ `TaskScheduler` (`ThreadPoolTaskScheduler` по умолчанию). Quartz — отдельная независимая библиотека, которую можно интегрировать опционально, но Spring `@Scheduled` её не использует. Смешивать параметры в одном приложении не только можно, но и нормально.
>
> **Откуда путаница:** Quartz часто упоминается рядом со Spring Scheduling как альтернатива для распределённых задач, и можно решить, что cron работает через него.
>
> **Если бы это было правдой:** добавление одной cron-задачи тянуло бы зависимость Quartz и две конкурирующих инфраструктуры в одно приложение. Spring специально предоставляет единый API.

## Q3. Как работает синтаксис cron-выражений в Spring?

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
>
> **Вопрос:** Разработчик скопировал из Linux crontab выражение `*/15 8-20 * * MON-FRI` для запуска задачи каждые 15 минут в рабочие часы и подставил его в `@Scheduled(cron = "...")`. Приложение упало с `IllegalArgumentException: Cron expression must consist of 6 fields`. Что правильно сказать про cron в Spring?
>
> ---
>
> #### A) Spring использует обычный Unix cron из 5 полей; ошибка значит, что в выражении синтаксическая опечатка — нужно проверить кавычки и пробелы — ❌ Неверно
>
> **Что на самом деле:** Spring использует cron из 6 полей с явным полем секунд. Unix crontab — 5 полей (минуты, часы, день, месяц, день недели). Скопированное выражение из crontab всегда нужно адаптировать: добавить первое поле «секунды».
>
> **Откуда путаница:** «cron — это же cron», и большинство разработчиков годами видели только 5-полевой синтаксис в `/etc/crontab` и онлайн-генераторах.
>
> **Если бы это было правдой:** ошибка сообщала бы о неверном символе, а не о количестве полей. Само сообщение `must consist of 6 fields` явно указывает на расхождение по числу полей.
>
> ---
>
> #### B) В Spring 6 полей, но порядок другой: `минуты часы дни месяцы дни_недели секунды` — секунды в КОНЦЕ. Нужно дописать `0` в конец выражения — ❌ Неверно
>
> **Что на самом деле:** поле секунд стоит ПЕРВЫМ, не последним. Порядок: `секунды минуты часы день_месяца месяц день_недели`. Дописывать `0` нужно в начало.
>
> **Откуда путаница:** разработчик помнит, что секунды «дополнительные», и интуитивно ставит их в конец как «второстепенные».
>
> **Если бы это было правдой:** конструкция `0 0 8 * * MON-FRI` (из официальной документации Spring) значила бы «в 0 минут 0 часов 8-го дня месяца, каждый месяц, по будням, в 0 секунд», что бессмысленно. Реальная семантика — «в 8:00:00 утра по будням», что подтверждает: секунды идут первыми.
>
> ---
>
> #### C) Spring добавляет поле секунд ПЕРВЫМ — итого 6 полей: `секунды минуты часы день_месяца месяц день_недели`. Выражение из crontab нужно адаптировать, добавив `0` в начало: `0 */15 8-20 * * MON-FRI`. Также поддерживаются макросы `@hourly`, `@daily` и спецсимволы `L`, `W`, `?` — ✓ Верно
>
> **Развёрнутое объяснение:** Spring `CronExpression` (с Spring 5.3) парсит 6-полевой формат: `second minute hour dayOfMonth month dayOfWeek`. Поддерживаются: `*` (любое), `?` (не важно, для dayOfMonth/dayOfWeek), `-` (диапазон), `,` (список), `/` (шаг), `L` (последний день), `W` (ближайший рабочий день), `#` (n-й день недели в месяце). Есть макросы: `@yearly`, `@monthly`, `@weekly`, `@daily`/`@midnight`, `@hourly`. Специальное значение `-` означает «никогда не запускать» — удобно для отключения через property.
>
> **Пример:**
> ```java
> @Scheduled(cron = "0 */15 8-20 * * MON-FRI")  // каждые 15 мин с 8 до 20 по будням
> public void poll() { /* ... */ }
>
> @Scheduled(cron = "0 0 0 L * *")  // в полночь последнего дня месяца
> public void monthlyReport() { /* ... */ }
>
> @Scheduled(cron = "${app.cleanup.cron:-}")  // "-" = отключено если property не задан
> public void cleanup() { /* ... */ }
>
> @Scheduled(cron = "@hourly")  // = "0 0 * * * *"
> public void heartbeat() { /* ... */ }
> ```
>
> **Когда применять:** когда нужна привязка к календарному времени (часы суток, дни недели, конец месяца). Для cron без привязки ко времени суток (например, «каждые N минут») часто проще `fixedRate`/`fixedDelay`.
>
> **Подводные камни:** часовой пояс по умолчанию — server local time; для предсказуемости укажите `zone = "UTC"` или `zone = "Europe/Moscow"`. Поле dayOfWeek в Spring — 0-7 или MON-SUN (в Quartz — 1-7 со сдвигом). Выражение `0 0 0 31 2 *` (31 февраля) валидно, но никогда не выполнится — отлавливается только в рантайме. `?` нужен, когда нужно ИЛИ dayOfMonth ИЛИ dayOfWeek, но не оба сразу.
>
> **Связанные вопросы:** [[Q2]] (fixedRate vs cron), [[Q5]] (вынести cron в конфигурацию).
>
> ---
>
> #### D) В Spring используется 7-полевой формат как в Quartz — добавлены секунды И год. Нужно вписать ровно 7 значений с годом в конце — ❌ Неверно
>
> **Что на самом деле:** 7-полевой формат с полем года — это Quartz Scheduler, а не Spring `@Scheduled`. Spring `CronExpression` — это ровно 6 полей, поле года не поддерживается. Сообщение об ошибке прямо говорит `must consist of 6 fields`, а не 7.
>
> **Откуда путаница:** Quartz действительно использует 7 полей и часто упоминается вместе со Spring Scheduling как альтернатива для распределённых задач. Документация Quartz активно гуглится по запросу «Spring cron».
>
> **Если бы это было правдой:** разработчики получали бы ошибку «must consist of 7 fields», а не 6. Также пришлось бы каждый раз писать `*` для года, что было бы избыточным — потому Spring специально остановился на 6 полях.

## Q4. Что такое `initialDelay` и зачем он нужен?

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
>
> **Вопрос:** В приложении есть `@Scheduled(fixedRate = 60_000, initialDelay = 30_000)` на методе `warmupCache()`. Команда жалуется: «при рестарте сервиса задача стреляет сразу, ещё до того как Hibernate прогрел connection pool, и валится с `CannotCreateTransactionException`». Что правильно описывает семантику `initialDelay` и как решить проблему?
>
> ---
>
> #### A) `initialDelay` задаёт задержку между ИТЕРАЦИЯМИ задачи, а не перед первым запуском. Чтобы отложить старт, нужно использовать `@DependsOn("hibernate")` или `@Lazy` на бине — ❌ Неверно
>
> **Что на самом деле:** `initialDelay` — это пауза ИМЕННО перед первым запуском (в миллисекундах). Интервал между итерациями задаётся `fixedRate`/`fixedDelay`. `@DependsOn` управляет порядком создания бинов, но не задержкой выполнения `@Scheduled`-задач — задачи стартуют по таймеру, а не по событию готовности.
>
> **Откуда путаница:** имя параметра «initial delay» может звучать как «начальная задержка между шагами», особенно если разработчик помнит про `ScheduledExecutorService.scheduleAtFixedRate(initialDelay, period, ...)` из `java.util.concurrent`, где смысл идентичен Spring — но кажется «продвинутым».
>
> **Если бы это было правдой:** не было бы способа отложить первый запуск, и каждая задача стартовала бы немедленно после поднятия контекста — что противоречит наблюдаемому поведению `@Scheduled(initialDelay = 30_000)`.
>
> ---
>
> #### B) `initialDelay` отсчитывается от полной готовности приложения (события `ApplicationReadyEvent`), поэтому проблема не в нём — нужно проверять health check Hibernate отдельно — ❌ Неверно
>
> **Что на самом деле:** `initialDelay` отсчитывается от момента, когда `ScheduledAnnotationBeanPostProcessor` зарегистрировал задачу в `TaskScheduler` (это происходит во время инициализации бинов, до `ApplicationReadyEvent`). Если пул Hibernate ещё не прогрет к моменту срабатывания таймера — задача упадёт.
>
> **Откуда путаница:** многие assume что Spring «умный» и ждёт полной готовности контекста. На деле scheduler стартует параллельно с инициализацией.
>
> **Если бы это было правдой:** проблема в задаче не возникала бы — но команда явно наблюдает падение при старте, значит таймер тикает раньше, чем готовы зависимости.
>
> ---
>
> #### C) `initialDelay` работает только вместе с `fixedDelay`, но НЕ с `fixedRate` — комбинация `fixedRate + initialDelay` игнорируется и задача стартует немедленно — ❌ Неверно
>
> **Что на самом деле:** `initialDelay` поддерживается со ВСЕМИ режимами расписания: `fixedRate`, `fixedDelay`, и даже с `cron` (через `initialDelayString` или новый параметр в Spring 6). Никаких ограничений по комбинациям нет — это явно прописано в javadoc `@Scheduled`.
>
> **Откуда путаница:** в `ScheduledExecutorService` есть отдельные методы `scheduleAtFixedRate(initialDelay, period, ...)` и `scheduleWithFixedDelay(initialDelay, delay, ...)` — оба принимают initialDelay, и это легко перепутать.
>
> **Если бы это было правдой:** примеры из официальной документации Spring (`@Scheduled(fixedRate = 60000, initialDelay = 30000)`) не работали бы — а они работают.
>
> ---
>
> #### D) `initialDelay` — это задержка (в миллисекундах) ПЕРЕД ПЕРВЫМ запуском задачи, отсчитываемая от момента регистрации в `TaskScheduler` (фактически от старта контекста). Используется для разогрева: дать БД, кэшу, внешним сервисам подняться, прежде чем стрелять задачу. Решение проблемы: увеличить `initialDelay` (например, до 60-120 сек) ИЛИ привязать старт scheduler к `ApplicationReadyEvent` через `SmartLifecycle`/`SchedulingConfigurer` — ✓ Верно
>
> **Развёрнутое объяснение:** `initialDelay` (или `initialDelayString` для подстановки из property) задаёт ОДНОРАЗОВУЮ паузу перед первым выполнением. Дальше задача идёт по своему обычному расписанию (`fixedRate`/`fixedDelay`). Спрятан в `ScheduledAnnotationBeanPostProcessor`, который вызывает `taskScheduler.scheduleAtFixedRate(task, startTime + initialDelay, period)`. По умолчанию `initialDelay = -1` — стартовать как можно раньше.
>
> **Пример:**
> ```java
> @Scheduled(fixedRate = 60_000, initialDelay = 30_000)
> public void warmupCache() { /* ... */ }
> // T+0с      : контекст поднят, задача зарегистрирована
> // T+30с     : ПЕРВЫЙ запуск (initialDelay)
> // T+90с     : второй запуск (через 60с от первого, fixedRate)
> // T+150с    : третий и т.д.
>
> @Scheduled(fixedDelayString = "${cache.sync.delay:60000}",
>            initialDelayString = "${cache.sync.initial:30000}")
> public void syncCache() { /* всё конфигурируется через application.yml */ }
> ```
>
> **Когда применять:**
> - Прогрев кэша после старта (нельзя стрелять сразу — БД ещё не готова).
> - Распределение нагрузки: разные задачи стартуют с разными `initialDelay` (5с, 10с, 15с), чтобы не создавать пик при старте.
> - Ожидание готовности внешних систем (Kafka, Redis, downstream API).
> - Graceful start в Kubernetes: пока readiness probe не подтвердила готовность, scheduled-задачи логично попридержать.
>
> **Подводные камни:** `initialDelay` НЕ ждёт полной готовности контекста — он отсчитывается от момента регистрации задачи в scheduler, что происходит во время инициализации бинов. Если приложение поднимается медленнее, чем initialDelay — таймер всё равно сработает. Для строгой привязки к готовности используйте `SchedulingConfigurer` или слушайте `ApplicationReadyEvent` и регистрируйте задачи программно через `TaskScheduler`. Также `initialDelay` нельзя комбинировать с `cron` через `initialDelay` напрямую до Spring 6 — там был только `initialDelayString` обходной путь.
>
> **Связанные вопросы:** [[Q2]] (fixedRate vs fixedDelay), [[Q5]] (cron в конфигурации), [[Q9]] (TaskScheduler программно).

## Q5. Как вынести cron-выражение в конфигурацию?

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
>
> **Вопрос:** На dev-стенде нужно ОТКЛЮЧИТЬ задачу `generateReport()` с `@Scheduled(cron = "${reporting.cron:0 0 8 * * *}")`, оставив код без изменений, а на prod-стенде — поменять расписание на «каждый будний день в 6 утра». Какой подход корректен и идиоматичен для Spring?
>
> ---
>
> #### A) Использовать SpEL-подстановку прямо в аннотации: `@Scheduled(cron = "${reporting.cron:0 0 8 * * *}")`. В `application-prod.yml` указать `reporting.cron: "0 0 6 * * MON-FRI"`, а в `application-dev.yml` — `reporting.cron: "-"` (специальное значение Spring: «никогда не запускать»). Аналогично есть `fixedRateString`/`fixedDelayString`/`initialDelayString` для числовых параметров — ✓ Верно
>
> **Развёрнутое объяснение:** Spring `@Scheduled` поддерживает резолвинг свойств через `${...}` placeholder и SpEL во ВСЕХ string-вариантах атрибутов: `cron`, `fixedRateString`, `fixedDelayString`, `initialDelayString`. Это позволяет переопределять расписание через `application.yml`, переменные окружения, Spring Cloud Config, ConfigMap в Kubernetes — без перекомпиляции. Специальное значение `-` (дефис) для `cron` интерпретируется `CronExpression` как «отключено» — задача регистрируется, но никогда не срабатывает. Это идиоматичный способ выключить scheduled-задачу на конкретном профиле без `@Profile`/`@ConditionalOnProperty`.
>
> **Пример:**
> ```java
> @Component
> public class ReportJob {
>
>     @Scheduled(cron = "${reporting.cron:0 0 8 * * *}")
>     public void generateReport() { /* ... */ }
>
>     @Scheduled(fixedDelayString = "${sync.delay.ms:60000}",
>                initialDelayString = "${sync.initial.ms:30000}")
>     public void syncData() { /* ... */ }
> }
> ```
> ```yaml
> # application-prod.yml
> reporting:
>   cron: "0 0 6 * * MON-FRI"
> sync:
>   delay:
>     ms: 30000
>
> # application-dev.yml
> reporting:
>   cron: "-"   # выключена на dev
> ```
>
> **Когда применять:**
> - Разное расписание на разных стендах (dev/staging/prod).
> - A/B-эксперименты: динамически менять частоту через Spring Cloud Config без рестарта (с `@RefreshScope`).
> - Аварийное отключение задачи через property без выкатки нового кода.
> - Регулировка нагрузки: легко изменить `fixedRateString` с 5с на 30с в инциденте.
>
> **Подводные камни:** свойство `reporting.cron` должно быть строкой — нельзя писать его как `cron: 0 0 8 * * *` без кавычек в YAML, иначе YAML-парсер может интерпретировать `*` как ссылку или специальный токен. Всегда оборачивайте cron в двойные кавычки. Также `@RefreshScope` НЕ работает с `@Scheduled` напрямую — после рефреша свойства задачи продолжат идти по старому расписанию, потому что они зарегистрированы в `TaskScheduler` один раз при создании бина. Для динамического обновления нужен `SchedulingConfigurer` с пересозданием задач.
>
> **Связанные вопросы:** [[Q3]] (cron syntax), [[Q4]] (initialDelay), [[Q9]] (TaskScheduler программно).
>
> ---
>
> #### B) Использовать `@ConditionalOnProperty("reporting.enabled")` на классе с задачей: задача создаётся только если property true. Расписание захардкодить в коде, а для prod-варианта создать отдельный класс `ReportJobProd` с другим cron — ❌ Неверно
>
> **Что на самом деле:** дублировать класс под каждое окружение — антипаттерн (нарушение DRY, поддержка двух классов при изменении логики). Spring предоставляет идиоматичный механизм: подстановка свойства прямо в `cron`. Для отключения задачи специальное значение `-` достаточно, никаких условных бинов и копий класса не нужно.
>
> **Откуда путаница:** `@ConditionalOnProperty` действительно работает для условной регистрации бинов, и кажется логичным применить его к scheduled-задачам.
>
> **Если бы это было правдой:** каждое изменение расписания требовало бы создания нового класса или копий — Spring явно не пошёл бы по этому пути и не выставил бы `cron` как настраиваемую строку.
>
> ---
>
> #### C) Атрибут `cron` в `@Scheduled` — это compile-time константа (как и все атрибуты аннотаций в Java), поэтому подстановка свойств через `${...}` принципиально невозможна. Нужно использовать `SchedulingConfigurer` и регистрировать задачу программно — ❌ Неверно
>
> **Что на самом деле:** хотя на уровне JVM атрибут аннотации действительно константа, Spring обрабатывает строки через `StringValueResolver` (поддерживающий property placeholders и SpEL) до передачи в `TaskScheduler`. То есть `${reporting.cron}` подменяется на реальное значение в runtime во время постпроцессинга бина. `SchedulingConfigurer` — это OPTIONAL альтернатива для динамических сценариев, не базовая необходимость.
>
> **Откуда путаница:** правда в том, что Java-аннотации компилируются как константы — на это часто опираются, объясняя ограничения аннотаций. Но Spring-аннотации идут через свой post-processing, и эта особенность как раз решает проблему.
>
> **Если бы это было правдой:** в документации Spring не было бы примеров `@Scheduled(cron = "${...}")` — а они есть.
>
> ---
>
> #### D) Можно использовать `${...}`, но ТОЛЬКО для `fixedRate`/`fixedDelay`, потому что они числовые. Для `cron` свойство нужно подставлять вручную через `Environment.getProperty()` в `@PostConstruct` — ❌ Неверно
>
> **Что на самом деле:** для подстановки в `cron` используется именно атрибут `cron` (он String, в отличие от `fixedRate`, который long). Для числовых параметров есть отдельные string-варианты: `fixedRateString`, `fixedDelayString`, `initialDelayString` — именно потому, что в long нельзя подставить `${...}`. Ручная подстановка через `Environment` бессмысленна — Spring сделает это сам.
>
> **Откуда путаница:** разработчики, знающие про `fixedRateString` (string-вариант для number-параметра), могут предположить, что для `cron` нужен какой-то аналог — но `cron` УЖЕ string, и работает напрямую.
>
> **Если бы это было правдой:** в Spring не было бы атрибута `cron` как String — был бы только массив или enum. Сам факт String-типа делает подстановку естественной.

## Q6. В каком потоке выполняется `@Scheduled`?

По умолчанию Spring использует **один поток** (`ThreadPoolTaskScheduler` с pool size = 1) для всех `@Scheduled`-задач. Задачи выполняются последовательно.

```
Поток 1: [task-A------][task-B--][task-C-----------]
```

Если `task-A` занимает долго — `task-B` ждёт.

**Почему важно:** `fixedRate` задача не будет запускаться параллельно — она будет «опаздывать» если предыдущий запуск ещё не завершён.

Для параллельного выполнения нужно настроить пул (см. Q7).


> [!mcq]
>
> **Вопрос:** В Spring Boot приложении объявлены три `@Scheduled` задачи: `taskA` (cron, 2 сек), `taskB` (fixedRate = 1000, 3 сек) и `taskC` (fixedDelay = 500, 1 сек). По мере роста проекта команда замечает: задачи «опаздывают», `taskB` иногда не запускается каждую секунду как задумано. В каком потоке/потоках они работают по умолчанию и как корректно настроить параллельность?
>
> ---
>
> #### A) Каждая `@Scheduled`-задача автоматически получает СВОЙ отдельный поток (по числу методов в контексте). «Опоздания» вызваны GC-паузами, нужно тюнить heap, а не scheduler — ❌ Неверно
>
> **Что на самом деле:** по умолчанию `TaskSchedulingAutoConfiguration` в Spring Boot создаёт `ThreadPoolTaskScheduler` с pool size = 1. ВСЕ задачи делят ОДИН поток — если `taskA` ещё выполняется, `taskB` и `taskC` ждут в очереди. «Опоздания» — это блокировка scheduler-потока длинной задачей, а не GC.
>
> **Откуда путаница:** многие сравнивают со старым `java.util.Timer`, у которого тоже один поток, но интуитивно ожидают, что Spring Boot будет «умнее» и даст каждой задаче отдельный поток.
>
> **Если бы это было правдой:** проблем с «опозданиями» не возникало бы, и не существовало бы свойства `spring.task.scheduling.pool.size` — но оно есть и популярно.
>
> ---
>
> #### B) По умолчанию Spring Boot создаёт `ThreadPoolTaskScheduler` с pool size = 1 — все задачи делят ОДИН поток и выполняются последовательно. Чтобы дать каждой задаче независимый поток, нужно задать `spring.task.scheduling.pool.size: 10` в `application.yml` (или сконфигурировать кастомный `ThreadPoolTaskScheduler` через `SchedulingConfigurer`). Размер пула выбирается под число одновременно выполняющихся задач — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Boot autoconfig (`TaskSchedulingAutoConfiguration`, начиная с 2.1) создаёт бин `ThreadPoolTaskScheduler` с `poolSize = 1` по умолчанию — это безопасный дефолт, исключающий гонки между задачами при их написании. Все `@Scheduled`-методы регистрируются в этом одном scheduler-е, и если задача в один поток не успевает завершиться к моменту следующего запуска другой задачи — последняя ждёт в очереди. Свойство `spring.task.scheduling.pool.size` напрямую управляет этим размером без написания конфигурации.
>
> **Пример:**
> ```yaml
> spring:
>   task:
>     scheduling:
>       pool:
>         size: 10
>       thread-name-prefix: sched-
>       shutdown:
>         await-termination: true
>         await-termination-period: 60s
> ```
> Эквивалентно программно:
> ```java
> @Configuration
> @EnableScheduling
> public class SchedulingConfig implements SchedulingConfigurer {
>     @Override
>     public void configureTasks(ScheduledTaskRegistrar registrar) {
>         ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
>         scheduler.setPoolSize(10);
>         scheduler.setThreadNamePrefix("sched-");
>         scheduler.setWaitForTasksToCompleteOnShutdown(true);
>         scheduler.setAwaitTerminationSeconds(60);
>         scheduler.initialize();
>         registrar.setTaskScheduler(scheduler);
>     }
> }
> ```
>
> **Как выбирать размер пула:**
> - Минимум = число задач, которые могут выполняться одновременно (длинные задачи + частые тики).
> - Максимум — разумный (10-20 для большинства приложений). Слишком большой пул бесполезен: scheduler не делит ОДНУ задачу на потоки, он лишь даёт разным задачам разные потоки.
> - Если задача I/O-bound и её хочется запускать ПАРАЛЛЕЛЬНО (например, опрос N внешних API) — нужно `@Async` + отдельный `Executor`, а не просто увеличивать pool size scheduler-а.
>
> **Подводные камни:** даже с pool size = 10 ОДНА задача никогда не запускается параллельно сама с собой — пул просто разводит РАЗНЫЕ задачи. Для параллельных запусков одной задачи нужно сочетание `@Scheduled` + `@Async` (с `@EnableAsync`) и осознанная стратегия идемпотентности (иначе данные перепишутся). Также `spring.task.scheduling.pool.size` появилось в Spring Boot 2.1 — на старых версиях только `SchedulingConfigurer`. И `ThreadPoolTaskScheduler` под капотом — это `ScheduledThreadPoolExecutor`, который НЕ растёт сверх `poolSize` (нет corePoolSize/maxPoolSize, как у обычного `ThreadPoolTaskExecutor`).
>
> **Связанные вопросы:** [[Q2]] (fixedRate single-thread behaviour), [[Q7]] (детальная настройка пула), [[Q8]] (параллельность через `@Async`).
>
> ---
>
> #### C) Все задачи выполняются в главном потоке Spring контекста (Tomcat main thread). Чтобы их распараллелить, нужно поднимать второй контекст через `SpringApplicationBuilder.child(...)` — ❌ Неверно
>
> **Что на самом деле:** scheduled-задачи НИКОГДА не выполняются в Tomcat-потоках или main-потоке приложения — для них есть отдельный `TaskScheduler` (по умолчанию `ThreadPoolTaskScheduler` с pool=1). Главный поток Spring завершается после старта контекста, и держать на нём scheduling было бы невозможно.
>
> **Откуда путаница:** разработчики, не знакомые с `TaskScheduler`, могут спутать с Tomcat thread model или с тем, что @Async по умолчанию использует `SimpleAsyncTaskExecutor`.
>
> **Если бы это было правдой:** падение задачи приводило бы к падению веб-сервера — но они изолированы.
>
> ---
>
> #### D) Spring Boot создаёт ForkJoinPool.commonPool() как scheduler. Размер пула равен числу CPU. «Опоздания» решаются добавлением `@Async` на метод, который тогда выполняется в отдельном потоке ForkJoinPool — ❌ Неверно
>
> **Что на самом деле:** Spring использует `ThreadPoolTaskScheduler`, а не `ForkJoinPool`. Они принципиально разные: `ForkJoinPool` оптимизирован под short-lived recursive tasks с work-stealing, а scheduler нуждается в стабильных long-running heartbeat-потоках. Размер по умолчанию = 1, не `Runtime.availableProcessors()`. `@Async` на `@Scheduled` методе действительно дает параллельность, но использует `TaskExecutor` (по умолчанию `SimpleAsyncTaskExecutor` или настраиваемый), не commonPool ForkJoin.
>
> **Откуда путаница:** в Java 8+ `CompletableFuture.runAsync()` по умолчанию использует `commonPool()`, и это смешивается с пониманием Spring async-механики.
>
> **Если бы это было правдой:** размер пула равнялся бы числу CPU «из коробки», и проблем с «опозданиями» из задачи не возникало бы на современных машинах — но команда явно её наблюдает.

## Q7. Как настроить пул потоков для `@Scheduled`?

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


> [!mcq] Как настроить пул потоков для `@Scheduled`-задач, чтобы они не конкурировали за единственный поток?
>
> - [ ] **A:** Увеличить значение `server.tomcat.threads.max` в `application.yml` — это расширит пул, в котором выполняются и HTTP-запросы, и scheduled-задачи.
>     - Почему неверно: `server.tomcat.threads.max` управляет потоками Tomcat для обработки HTTP-запросов, а не scheduler-ом Spring. Scheduled-задачи живут в отдельном пуле (`TaskScheduler`).
>     - Последствие: настройка не даст эффекта — задачи продолжат выполняться в одном дефолтном потоке `scheduling-1`, и долгая задача будет блокировать остальные.
>
> - [ ] **B:** Пометить scheduled-метод аннотацией `@Async` — Spring автоматически создаст пул потоков и будет запускать каждую задачу в отдельном потоке.
>     - Почему неверно: `@Async` использует executor для асинхронных вызовов, но не настраивает scheduler. Без `@EnableAsync` аннотация игнорируется, а без отдельного `Executor`-бина задачи попадают в `SimpleAsyncTaskExecutor`, который создаёт новый поток на каждый вызов (антипаттерн под нагрузкой).
>     - Последствие: либо ничего не изменится, либо приложение начнёт плодить неограниченное число потоков, что приведёт к `OutOfMemoryError`.
>
> - [x] **C:** Сконфигурировать `ThreadPoolTaskScheduler` через `SchedulingConfigurer.configureTasks()` или задать `spring.task.scheduling.pool.size` в `application.yml` — Spring заменит дефолтный однопоточный scheduler.
>     - Почему верно: `SchedulingConfigurer` — официальный API Spring для кастомизации scheduler-а. `ThreadPoolTaskScheduler` поддерживает `setPoolSize()`, `setThreadNamePrefix()`, `setWaitForTasksToCompleteOnShutdown()`. Свойство `spring.task.scheduling.pool.size` (Spring Boot 2.1+) делает то же декларативно.
>     - Когда применять: всегда, когда в приложении больше одной `@Scheduled`-задачи или задачи выполняются дольше тика — иначе они выстраиваются в очередь на одном потоке.
>     - Граничный случай: `setWaitForTasksToCompleteOnShutdown(true)` + `setAwaitTerminationSeconds(60)` обеспечат graceful shutdown — недозавершённые задачи получат до 60 секунд на завершение.
>
> - [ ] **D:** Создать `ScheduledExecutorService` вручную через `Executors.newScheduledThreadPool(10)` и вызывать `submit()` в `@PostConstruct` — это полностью обходит ограничения Spring.
>     - Почему неверно: ручной `ScheduledExecutorService` теряет интеграцию со Spring — нет `@Scheduled`, нет graceful shutdown, нет `SchedulingConfigurer`, нет управления через `application.yml`. Также `Executors.newScheduledThreadPool` не имеет ограничения на очередь и при перегрузке будет копить задачи бесконечно.
>     - Последствие: дублирование инфраструктуры, утечка потоков при shutdown контекста, потеря возможности тестировать задачи через `@SpyBean`/`Awaitility`.

## Q8. Могут ли задачи `@Scheduled` выполняться параллельно?

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


> [!mcq] Как обеспечить параллельное выполнение `@Scheduled`-задач, если несколько задач должны работать одновременно, а не последовательно?
>
> - [ ] **A:** Spring автоматически запускает `@Scheduled`-методы в разных потоках, если они в разных бинах — достаточно просто пометить методы аннотацией `@Scheduled`.
>     - Почему неверно: по умолчанию Spring создаёт single-threaded `TaskScheduler`, и все `@Scheduled`-методы выполняются последовательно в одном потоке `scheduling-1`, независимо от того, в каких бинах они объявлены.
>     - Последствие: если одна задача выполняется 5 минут, остальные `@Scheduled` ждут её — даже из других классов; общая пропускная способность scheduler-а деградирует.
>
> - [ ] **B:** Использовать `@Scheduled(fixedRate = ..., concurrent = true)` — у аннотации есть атрибут, разрешающий параллельные запуски.
>     - Почему неверно: атрибута `concurrent` в `@Scheduled` не существует. У аннотации только `fixedRate`, `fixedDelay`, `fixedRateString`, `fixedDelayString`, `initialDelay`, `cron`, `zone`. Параллельность управляется через executor, а не через атрибут.
>     - Последствие: код не скомпилируется — Spring отвергнет неизвестный attribute; разработчик потеряет время на поиск несуществующей опции в документации.
>
> - [ ] **C:** Объявить `static` поле `ExecutorService` в классе с задачей и оборачивать тело метода в `executor.submit(...)` — это даст параллельность без изменения конфигурации Spring.
>     - Почему неверно: ручной `submit()` внутри `@Scheduled`-метода создаёт два уровня планирования — Spring запустит метод, а тот форкнет работу в свой executor. Это ломает семантику `fixedDelay` (Spring считает задачу завершённой сразу после `submit()`, а не после реальной работы) и ведёт к параллельным запускам без блокировки между ними.
>     - Последствие: неидемпотентная задача может работать в N потоках одновременно, race conditions, потеря трассируемости в логах.
>
> - [x] **D:** Включить `@EnableAsync`, настроить `ThreadPoolTaskScheduler` нужного размера и пометить `@Scheduled`-методы аннотацией `@Async("executorName")` — Spring будет запускать каждый тик в отдельном потоке executor-а.
>     - Почему верно: связка `@EnableScheduling + @EnableAsync + @Async + @Scheduled` — официальный паттерн Spring для параллельных scheduled-задач. `@Async` отдаёт выполнение в `Executor`, а scheduler сразу освобождается для следующего тика.
>     - Когда применять: задачи независимы, идемпотентны или защищены distributed lock (ShedLock). Размер пула подбирается под пиковую нагрузку с запасом.
>     - Граничный случай: если задача неидемпотентна, параллельные запуски опасны — нужен `@SchedulerLock` (ShedLock) или внутренний lock через `ReentrantLock`/`Semaphore`, иначе в кластере и даже в одном узле может произойти двойной запуск.

## Q9. Что такое `TaskScheduler` и когда его использовать?

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


> [!mcq] Когда стоит использовать `TaskScheduler` API программно вместо аннотации `@Scheduled`?
>
> - [x] **A:** Когда расписание задаётся пользователем в рантайме или нужно динамически добавлять/удалять задачи и контролировать их `ScheduledFuture` (отменять, перепланировать).
>     - Почему верно: `TaskScheduler` — программный API для случаев, которые `@Scheduled` принципиально не покрывает: cron-выражение неизвестно на этапе компиляции, набор задач меняется в рантайме, нужно держать ссылку на `ScheduledFuture<?>` для отмены или перепланирования.
>     - Когда применять: SaaS-функция «напоминания пользователя» (cron в БД на каждого юзера), reminder-сервисы (через `schedule(Runnable, Instant)`), runtime-конфигурируемые админ-задачи, генерация одноразовых задач «через N минут».
>     - Граничный случай: при перезапуске приложения runtime-задачи теряются — их нужно персистить (в БД) и пересоздавать через `@PostConstruct` или Quartz JobStore, если важна durability.
>
> - [ ] **B:** Всегда, как только в проекте появляется хотя бы одна `@Scheduled`-задача — это даёт лучшую производительность и более простой код.
>     - Почему неверно: `TaskScheduler` — низкоуровневый API; он требует ручного управления `ScheduledFuture`, `CronTrigger`, lifecycle. Для статических расписаний `@Scheduled` намного компактнее и читаемее.
>     - Последствие: преждевременное усложнение — десятки строк boilerplate для того, что в `@Scheduled` решается одной аннотацией; усложняется code review и тестирование.
>
> - [ ] **C:** Когда нужно гарантировать выполнение задачи только в одном экземпляре в Kubernetes-кластере — `TaskScheduler` имеет встроенную distributed coordination.
>     - Почему неверно: `TaskScheduler` — локальный (in-memory) scheduler без distributed coordination. Для cluster-wide уникальности используются ShedLock, Quartz Clustered или внешний координатор (Kafka, ZooKeeper). Сама замена аннотации на API ничего не меняет в распределённости.
>     - Последствие: задача всё равно будет дублироваться на N pod-ах; разработчик потратит время на «починку», которая не решает проблему.
>
> - [ ] **D:** Когда задача требует транзакции — `TaskScheduler` автоматически оборачивает каждый `Runnable` в `@Transactional`, а `@Scheduled` так не умеет.
>     - Почему неверно: ни `TaskScheduler`, ни `@Scheduled` не открывают транзакцию автоматически. Управление транзакциями — отдельная задача (`@Transactional` на вызываемом методе, `TransactionTemplate` внутри `Runnable`). Оба механизма одинаковы по поведению относительно транзакций.
>     - Последствие: разработчик ожидает «волшебной» транзакции, она не открывается, изменения в БД либо не коммитятся в нужный момент, либо ломаются на `LazyInitializationException`.

## Q10. Как запланировать задачу динамически из кода?

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


> [!mcq] Как запланировать одноразовую задачу на конкретный момент времени в runtime?
> - [x] Инжектить `TaskScheduler` и вызывать `scheduler.schedule(Runnable, Instant)`
>     - Возвращает `ScheduledFuture<?>`, по которому можно отменить задачу до её срабатывания.
>     - Spring Boot автоконфигурирует `ThreadPoolTaskScheduler` через `TaskSchedulingAutoConfiguration` — bean уже готов к инжекту.
>     - Use-case: отправить напоминание о заказе через 30 минут после оформления, рассылка по `Instant`-у из БД.
>     - Подводный камень: задача живёт в памяти JVM — при рестарте pod-а потеряется; для критичных задач нужен persistent store (Quartz JDBC).
> - [ ] Аннотировать метод `@Scheduled(cron = "...")` с динамически собранной строкой
>     - `@Scheduled` обрабатывается на старте контекста — выражение нельзя поменять в runtime, только через `SchedulingConfigurer`.
>     - ❌ Последствие: NPE при попытке передать в аннотацию переменную, либо задача навсегда привязана к стартовому значению.
> - [ ] Запустить `new Thread(() -> { Thread.sleep(delay); task.run(); }).start()`
>     - Голый поток вне пула Spring — не управляется shutdown, ловит `InterruptedException` молча, нет интеграции с `@Transactional`/MDC.
>     - ❌ Последствие: при graceful shutdown задача обрывается посередине, в логах нет trace-id, утечка потоков под нагрузкой.
> - [ ] Положить запись в БД и опрашивать таблицу через `@Scheduled(fixedDelay = 1000)`
>     - Это polling-паттерн, а не динамическое планирование — даёт лаг до интервала опроса и нагрузку на БД даже при отсутствии задач.
>     - ❌ Последствие: задача срабатывает с погрешностью ±1 сек, при большом числе записей — full scan каждую секунду.

## Q11. Почему `@Scheduled` проблематичен в кластере?

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


> [!mcq] Что произойдёт с `@Scheduled(cron = "0 0 8 * * *")` при деплое в Kubernetes с `replicas: 3`?
> - [ ] Spring автоматически выберет один pod как leader и запустит задачу только на нём
>     - Spring Scheduling не имеет встроенного leader election — каждый ApplicationContext независим и не знает о соседях.
>     - ❌ Последствие: разработчик полагается на «магию», в проде получает тройные счета/письма после первого же утреннего запуска.
> - [ ] Задача выполнится один раз, потому что cron-выражение синхронизирует выполнение по таймстампу
>     - Cron — это лишь расписание для локального планировщика; синхронизация между JVM требует внешнего координатора (БД/Redis/ZooKeeper).
>     - ❌ Последствие: одинаковое `0 0 8 * * *` запустит задачу на всех трёх pod-ах в одну и ту же секунду — дубль гарантирован.
> - [x] Каждый pod независимо запустит задачу в 08:00 — будет N выполнений, нужен distributed lock
>     - Каждый экземпляр JVM имеет свой `ThreadPoolTaskScheduler` и поднимает `@Scheduled` независимо от других реплик.
>     - Стандартное решение: ShedLock (`@SchedulerLock`) с JDBC/Redis-провайдером — лок берётся атомарно, остальные pod-ы пропускают тик.
>     - Use-case: ночная генерация отчётов, рассылка email-кампаний, агрегация статистики — операции, которые должны выполниться ровно один раз.
>     - Подводный камень: если задача упадёт между взятием лока и завершением, `lockAtMostFor` страхует от deadlock, но `lockAtLeastFor` нужен чтобы быстрые pod-ы не успели взять лок повторно.
> - [ ] При наличии `@EnableScheduling` Spring Boot откажется стартовать вторую реплику
>     - `@EnableScheduling` — локальная Spring-аннотация, она не общается с другими JVM и не блокирует старт.
>     - ❌ Последствие: ложное чувство безопасности, реальная защита от дублей отсутствует.

## Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock?

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


> [!mcq] Зачем в `@SchedulerLock` параметр `lockAtLeastFor`, если уже есть `lockAtMostFor`?
> - [ ] Чтобы Spring мог retry-нуть задачу через указанное время при ошибке
>     - ShedLock не делает retry — он только координирует одноразовое выполнение между нодами; retry это ответственность приложения.
>     - ❌ Последствие: разработчик ждёт автоматический повтор после исключения и не пишет свою обработку — задача молча падает.
> - [x] Гарантировать минимальное удержание лока, чтобы быстрая нода не выполнила задачу повторно при рассинхроне часов
>     - Без `lockAtLeastFor` нода, отработавшая задачу за миллисекунды, освобождает лок до того, как соседи доберутся до следующего тика — и они подхватывают лок снова.
>     - Особенно критично при cron-задачах с маленьким окном (каждую минуту) и кластерах с расхождением системных часов в пределах NTP-погрешности.
>     - Use-case: задача `housekeeping`, которая обычно завершается за 50ms — без `lockAtLeastFor = "PT55S"` риск двойного запуска в одну минуту.
>     - Подводный камень: слишком большой `lockAtLeastFor` блокирует следующий легитимный запуск; правило — чуть меньше периода cron.
> - [ ] Это таймаут, после которого ShedLock форсированно убивает поток задачи
>     - ShedLock работает только с локом в БД/Redis и не имеет доступа к потокам JVM — он не прерывает выполнение.
>     - ❌ Последствие: ожидание «kill thread» приводит к подвисшим задачам и непредсказуемому поведению в проде.
> - [ ] Минимальное время между двумя последовательными запусками одной задачи на одной ноде
>     - Интервал между запусками определяется самим cron-выражением, а не `lockAtLeastFor`; этот параметр про lock TTL, а не про расписание.
>     - ❌ Последствие: разработчик пытается заменить cron-планирование на `lockAtLeastFor`, получает непредсказуемые тайминги.

## Q13. Чем Quartz отличается от Spring Scheduling?

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


> [!mcq] Когда стоит выбрать Quartz вместо Spring `@Scheduled` + ShedLock в Spring Boot микросервисе?
> - [ ] Когда нужен синтаксис cron-выражений с секундами
> - [ ] Когда требуется конкурентный запуск нескольких задач в одном инстансе
> - [ ] Когда нужно отключать задачу через `@ConditionalOnProperty` в зависимости от профиля
> - [x] Когда задачи должны переживать рестарт JVM, иметь persistent job store и встроенную кластеризацию без внешнего lock-provider
>     - Quartz хранит job definitions и triggers в БД через `JDBCJobStore`, поэтому misfire и недозапущенные задачи восстанавливаются после рестарта; кластеризация работает «из коробки» через row-level lock в таблице `QRTZ_LOCKS`.
>     - Spring `@Scheduled` хранит расписание только в памяти JVM — пропущенный из-за рестарта запуск теряется навсегда, а для кластера нужен внешний ShedLock с отдельной таблицей/Redis.
>     - Use-case: ежедневный billing batch, который ОБЯЗАН выполниться даже если pod упал в момент срабатывания cron — Quartz пересчитает misfire и запустит задачу при следующем старте.
>     - Подводный камень: Quartz тянет за собой `spring-boot-starter-quartz`, схему из ~12 таблиц, требует миграций при апдейте версии; для простых cron-задач это overkill.
> 
> Неверные:
> - **Когда нужен синтаксис cron-выражений с секундами** — Spring cron начиная с 5.3 поддерживает 6-польные выражения с секундами через `CronExpression`, и в Quartz, и в Spring синтаксис эквивалентен; это не повод тащить Quartz.
>     - ❌ Последствие: команда подключает Quartz «ради секунд», получает 12 таблиц схемы и сложность миграций — а проблема решалась одной строкой в существующем `@Scheduled`.
> - **Когда требуется конкурентный запуск нескольких задач в одном инстансе** — это решается размером пула `ThreadPoolTaskScheduler` (`spring.task.scheduling.pool.size`), а не сменой планировщика; Quartz здесь не даёт преимущества.
>     - ❌ Последствие: разработчик меняет планировщик вместо настройки пула, ловит регрессии в существующих job'ах и тратит спринт на миграцию ради того, что чинится property.
> - **Когда нужно отключать задачу через `@ConditionalOnProperty`** — это чисто Spring-механизм, который работает с любым типом бинов; Quartz job'ы тоже регистрируются как Spring beans и поддерживают те же условия.
>     - ❌ Последствие: ложная аргументация в ADR — команда «обосновывает» Quartz возможностью, которая в обоих стеках работает одинаково, и принимает решение по сфабрикованному критерию.

## Q14. Как тестировать `@Scheduled`-задачи?

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


> [!mcq] Как лучше всего тестировать метод, помеченный `@Scheduled(fixedRate = 60_000)`, чтобы тесты были быстрыми и детерминированными?
> - [ ] Запускать `@SpringBootTest` и ждать через `Thread.sleep(61_000)` пока сработает реальный планировщик
> - [x] Вызывать метод напрямую как обычный unit-тест бизнес-логики, а сам факт планирования проверять отдельным маленьким интеграционным тестом через `@SpyBean` + `verify(..., timeout(...))` или Awaitility
>     - Метод `@Scheduled` — это обычный публичный метод бина; аннотация только регистрирует его в `ScheduledAnnotationBeanPostProcessor`, не меняя сигнатуру и поведение при прямом вызове.
>     - Unit-тест прямого вызова работает за миллисекунды, не требует контекста Spring и покрывает 100% бизнес-логики; интеграционный тест с `@SpyBean` нужен только чтобы один раз убедиться, что планировщик действительно «дёргает» метод.
>     - Use-case: класс `ReportJob.generateReport()` тестируется юнит-тестом с моками `ReportService`; отдельный `ReportJobSchedulingTest` с `@SpyBean ReportJob` и `verify(reportJob, timeout(5000).atLeastOnce()).generateReport()` валидирует регистрацию.
>     - Подводный камень: в интеграционном тесте включай отдельный профиль с маленьким `fixedRate` или `cron` каждую секунду, иначе таймаут теста будет больше реального периода задачи.
> - [ ] Включать `@EnableScheduling` в каждом юнит-тесте и проверять выполнение через `CountDownLatch`
> - [ ] Использовать `@MockBean(ReportJob.class)` в каждом тесте, чтобы избежать запуска задачи во время прогона
> 
> Неверные:
> - **`Thread.sleep(61_000)` в `@SpringBootTest`** — `Thread.sleep` делает тест медленным (минута на проверку) и flaky из-за дрейфа времени планировщика; использовать sleep для синхронизации тестов с асинхронным выполнением — антипаттерн.
>     - ❌ Последствие: CI pipeline удлиняется на минуту за каждый scheduled-тест, разработчики начинают игнорировать падения «из-за тайминга», реальные регрессии тонут в flaky-фейлах.
> - **`@EnableScheduling` + `CountDownLatch` в юнит-тестах** — это превращает юнит-тесты в интеграционные, поднимает планировщик в каждом классе и теряет преимущество быстрого unit-уровня; `CountDownLatch` ещё и требует модификации production-кода ради тестов.
>     - ❌ Последствие: каждый тест-класс с такой задачей поднимает Spring-контекст, общий test-runtime растёт в разы; код продакшна обрастает «testing seams» для счётчиков, которых там быть не должно.
> - **`@MockBean(ReportJob.class)` глобально** — это маскирует задачу везде, но не тестирует ни логику метода, ни факт регистрации в планировщике; мы не получаем покрытия — только отключение задачи в общих тестах (это валидный приём для не-scheduling-тестов, но не для тестирования самой задачи).
>     - ❌ Последствие: задача никогда не покрыта тестом, ошибка в cron-выражении или в логике метода всплывает только в проде во время первого запуска по расписанию.

## Q15. Как условно включать/отключать `@Scheduled`?

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


> [!mcq] Чем отличаются Kubernetes `CronJob` и Spring `@Scheduled` как механизмы запуска задач по расписанию, и когда какой выбирать?
> - [ ] Они взаимозаменяемы — выбор зависит только от личных предпочтений команды
> - [ ] `@Scheduled` всегда быстрее, потому что не нужно поднимать новый pod при каждом запуске
> - [x] K8s `CronJob` запускает изолированный pod на каждый тик и даёт встроенный distributed scheduling без shared state в приложении; Spring `@Scheduled` живёт внутри уже работающего pod'а и требует ShedLock/Quartz для координации, но даёт доступ ко всему контексту приложения без cold start
>     - K8s `CronJob` создаёт новый Job → Pod на каждое срабатывание, выполняет команду и завершается; планирование на уровне control plane — гарантия одного запуска решается K8s'ом, не приложением.
>     - Spring `@Scheduled` — это method invocation внутри JVM, в котором уже подняты бины, кэши, connection pools, JIT-компилированный код; cold start отсутствует, но в кластере из N реплик задача запустится N раз без внешнего lock.
>     - Use-case: heavy ETL раз в сутки → `CronJob` (изолированные ресурсы, не отнимает heap у API-сервиса, отдельный pod resource limits); периодическая инвалидация кэша каждые 30 секунд → `@Scheduled` + ShedLock (cold start pod каждые 30s неоправдан).
>     - Подводный камень `CronJob`: `concurrencyPolicy: Allow` по умолчанию допускает параллельные запуски — для long-running задач ставить `Forbid` или `Replace`; `startingDeadlineSeconds` определяет, считается ли пропущенный запуск misfire.
>     - Подводный камень `@Scheduled`: время старта задачи привязано к UTC внутри JVM, при autoscaling новые pods начинают исполнять расписание независимо — без distributed lock получается N×nominal load.
> - [ ] `CronJob` не поддерживает синтаксис cron-выражений с секундами и поэтому подходит только для задач с минутным интервалом и реже
> 
> Неверные:
> - **«Взаимозаменяемы — дело вкуса»** — у механизмов разные failure modes, ресурсные профили и операционные характеристики; рассматривать их как идентичные приводит к ошибочным архитектурным выборам.
>     - ❌ Последствие: команда оставляет heavy ETL внутри API-сервиса как `@Scheduled`, OOM kill убивает обслуживающий трафик pod в момент batch'а, SLA по latency ломается на 30 минут.
> - **«`@Scheduled` всегда быстрее»** — это правда только для коротких частых задач; для редких heavy job'ов «всегда подняты JVM ради расписания раз в сутки» — антипаттерн с точки зрения memory footprint.
>     - ❌ Последствие: разработчик прячет «тяжёлую» ежедневную миграцию внутри API-микросервиса под лозунгом «быстрее», на проде heap съедается batch'ом, остальные эндпоинты деградируют.
> - **«`CronJob` не поддерживает секунды»** — у Spring 5-польный cron исторически тоже не поддерживал секунды, у Quartz/Spring 6-польный поддерживает; гранулярность секунд у обоих стеков связана с реализацией, но не является принципиальным отличием.
>     - ❌ Последствие: ложная техническая аргументация в ADR — команда отвергает `CronJob` по выдуманному ограничению и упускает реальные плюсы изоляции ресурсов.

## Q16. Какие типичные ошибки при работе с `@Scheduled`?

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
