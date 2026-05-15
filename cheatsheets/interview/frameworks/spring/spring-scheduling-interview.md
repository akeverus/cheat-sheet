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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Как вынести cron-выражение в конфигурацию? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. В каком потоке выполняется `@Scheduled`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

По умолчанию Spring использует **один поток** (`ThreadPoolTaskScheduler` с pool size = 1) для всех `@Scheduled`-задач. Задачи выполняются последовательно.

```
Поток 1: [task-A------][task-B--][task-C-----------]
```

Если `task-A` занимает долго — `task-B` ждёт.

**Почему важно:** `fixedRate` задача не будет запускаться параллельно — она будет «опаздывать» если предыдущий запуск ещё не завершён.

Для параллельного выполнения нужно настроить пул (см. Q7).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как настроить пул потоков для `@Scheduled`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Могут ли задачи `@Scheduled` выполняться параллельно? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Что такое `TaskScheduler` и когда его использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как запланировать задачу динамически из кода? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Почему `@Scheduled` проблематичен в кластере? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Как решить проблему с `@Scheduled` в кластере с помощью ShedLock? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Чем Quartz отличается от Spring Scheduling? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как тестировать `@Scheduled`-задачи? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как условно включать/отключать `@Scheduled`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Какие типичные ошибки при работе с `@Scheduled`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring Boot](spring-boot-interview.md) — автоконфигурация `TaskSchedulingAutoConfiguration`, `spring.task.scheduling.*` ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
