---
title: "Вопросы на собеседовании: JVM Performance Tuning"
description: "Практичные вопросы и ответы по JVM performance tuning: GC-алгоритмы (G1, ZGC, Shenandoah), JIT-компиляция, escape analysis, memory tuning, контейнеры, native memory, class loading и продвинутые JVM-флаги."
tags:
  - interview
  - performance
  - jvm-performance-tuning-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "JVM Performance Tuning"
  - "JVM tuning"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `JVM Performance Tuning`

Практичные вопросы и ответы по тюнингу `JVM`: как измерять, диагностировать и улучшать производительность без регрессий в production. Покрыты `GC`-алгоритмы, `JIT`-компиляция, native memory, контейнерные среды и продвинутые техники оптимизации.

## Полезные ссылки

### Официальная документация

- [Java GC Tuning Guide (JDK 21)](https://docs.oracle.com/en/java/javase/21/gctuning/) — актуальное руководство по настройке GC
- [JVM Diagnostic Tools](https://docs.oracle.com/en/java/javase/21/troubleshoot/diagnostic-tools.html) — инструменты диагностики
- [JFR / JMC](https://docs.oracle.com/en/java/javase/21/jfapi/) — Java Flight Recorder API
- [JEP 376: ZGC Concurrent Thread-Stack Processing](https://openjdk.org/jeps/376) — финализация ZGC
- [JEP 379: Shenandoah Production-Ready](https://openjdk.org/jeps/379) — Shenandoah в production
- [Baeldung — JVM Parameters](https://www.baeldung.com/jvm-parameters) — обзор ключевых JVM-флагов
- [Baeldung — JVM Garbage Collectors](https://www.baeldung.com/jvm-garbage-collectors) — сравнение сборщиков мусора
- [An Introduction to ZGC](https://www.baeldung.com/jvm-zgc-garbage-collector) — ZGC: low-latency сборщик мусора
- [Choosing a GC Algorithm in Java](https://www.baeldung.com/java-choosing-gc-algorithm) — как выбрать GC для продакшена
- [Generational ZGC in Java](https://www.baeldung.com/java-21-generational-z-garbage-collector) — Generational ZGC в Java 21

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Подход к тюнингу**
- [Q1. (!) С чего начинать JVM tuning, чтобы не делать "магии флагов"?](#q1--с-чего-начинать-jvm-tuning-чтобы-не-делать-магии-флагов)
- [Q2. Какие метрики обязательно мониторить перед изменениями?](#q2-какие-метрики-обязательно-мониторить-перед-изменениями)
- [Q3. Как корректно проверять эффект тюнинга?](#q3-как-корректно-проверять-эффект-тюнинга)

**GC-алгоритмы: обзор и выбор**
- [Q4. (!) Какие сборщики мусора доступны в современной JVM и чем они отличаются?](#q4--какие-сборщики-мусора-доступны-в-современной-jvm-и-чем-они-отличаются)
- [Q5. (!) Как устроен G1 GC и какие у него фазы?](#q5--как-устроен-g1-gc-и-какие-у-него-фазы)
- [Q6. (!) Как работает ZGC и почему у него паузы менее 1 мс?](#q6--как-работает-zgc-и-почему-у-него-паузы-менее-1-мс)
- [Q7. Как работает Shenandoah GC и чем он отличается от ZGC?](#q7-как-работает-shenandoah-gc-и-чем-он-отличается-от-zgc)
- [Q8. Как выбирать GC под разные SLA?](#q8-как-выбирать-gc-под-разные-sla)
- [Q9. Когда имеет смысл переходить с G1 на ZGC/Shenandoah?](#q9-когда-имеет-смысл-переходить-с-g1-на-zgcshenandoah)

**GC-диагностика и тюнинг**
- [Q10. (!) Какие ключевые JVM-флаги управляют G1 GC?](#q10--какие-ключевые-jvm-флаги-управляют-g1-gc)
- [Q11. Какие сигналы в GC-логах говорят о реальной проблеме?](#q11-какие-сигналы-в-gc-логах-говорят-о-реальной-проблеме)
- [Q12. Как включить и анализировать GC-логи?](#q12-как-включить-и-анализировать-gc-логи)
- [Q13. Как объяснить влияние allocation/promotion на latency?](#q13-как-объяснить-влияние-allocationpromotion-на-latency)
- [Q14. Что такое Humongous Allocations в G1 и как с ними бороться?](#q14-что-такое-humongous-allocations-в-g1-и-как-с-ними-бороться)

**Память: heap и beyond**
- [Q15. (!) Из каких областей состоит память JVM-процесса?](#q15--из-каких-областей-состоит-память-jvm-процесса)
- [Q16. Что такое Native Memory и как его отслеживать?](#q16-что-такое-native-memory-и-как-его-отслеживать)
- [Q17. (!) Как правильно рассчитать размер heap и off-heap?](#q17--как-правильно-рассчитать-размер-heap-и-off-heap)
- [Q18. Что такое String Deduplication и когда она полезна?](#q18-что-такое-string-deduplication-и-когда-она-полезна)
- [Q19. Как настроить Metaspace и зачем?](#q19-как-настроить-metaspace-и-зачем)

**JIT-компиляция**
- [Q20. (!) Как работает JIT-компилятор и Tiered Compilation?](#q20--как-работает-jit-компилятор-и-tiered-compilation)
- [Q21. Как JIT и warm-up влияют на производительность в проде?](#q21-как-jit-и-warm-up-влияют-на-производительность-в-проде)
- [Q22. (!) Что такое Escape Analysis и какие оптимизации она включает?](#q22--что-такое-escape-analysis-и-какие-оптимизации-она-включает)
- [Q23. Как посмотреть, какие методы JIT скомпилировал, и диагностировать деоптимизации?](#q23-как-посмотреть-какие-методы-jit-скомпилировал-и-диагностировать-деоптимизации)
- [Q24. Что такое On-Stack Replacement (OSR) и Inlining?](#q24-что-такое-on-stack-replacement-osr-и-inlining)

**CPU и профилирование**
- [Q25. Как искать CPU bottleneck в JVM-приложении?](#q25-как-искать-cpu-bottleneck-в-jvm-приложении)
- [Q26. Что такое safepoint bias и почему профили могут врать?](#q26-что-такое-safepoint-bias-и-почему-профили-могут-врать)

**Class Loading**
- [Q27. Как устроен Class Loading в JVM и какие проблемы он вызывает?](#q27-как-устроен-class-loading-в-jvm-и-какие-проблемы-он-вызывает)
- [Q28. Что такое Metaspace leak и как его диагностировать?](#q28-что-такое-metaspace-leak-и-как-его-диагностировать)

**Контейнеры и облако**
- [Q29. (!) Как правильно настроить JVM в Kubernetes/Docker?](#q29--как-правильно-настроить-jvm-в-kubernetesdocker)
- [Q30. Какие ошибки в контейнерном tuning встречаются чаще всего?](#q30-какие-ошибки-в-контейнерном-tuning-встречаются-чаще-всего)
- [Q31. Как JVM определяет ресурсы в контейнере (Container-Aware JVM)?](#q31-как-jvm-определяет-ресурсы-в-контейнере-container-aware-jvm)
- [Q32. Как оптимизировать startup time JVM в контейнерах?](#q32-как-оптимизировать-startup-time-jvm-в-контейнерах)

**Продвинутые техники**
- [Q33. Какие JVM-флаги обязательны для production?](#q33-какие-jvm-флаги-обязательны-для-production)
- [Q34. Что такое CDS/AppCDS и как это ускоряет запуск?](#q34-что-такое-cdsappcds-и-как-это-ускоряет-запуск)
- [Q35. Как работают Compressed Oops и Compressed Class Pointers?](#q35-как-работают-compressed-oops-и-compressed-class-pointers)
- [Q36. Что такое NUMA-aware GC и когда это важно?](#q36-что-такое-numa-aware-gc-и-когда-это-важно)

**Senior-подача**
- [Q37. Какие anti-patterns JVM tuning звучат слабо на интервью?](#q37-какие-anti-patterns-jvm-tuning-звучат-слабо-на-интервью)
- [Q38. Как дать сильный ответ по JVM tuning за 60 секунд?](#q38-как-дать-сильный-ответ-по-jvm-tuning-за-60-секунд)

---

## Q1. (!) С чего начинать JVM tuning, чтобы не делать "магии флагов"?

Начинать нужно со сбора **baseline** — измеримой точки отсчёта. Без неё любой тюнинг превращается в гадание: непонятно, от чего отталкиваться и стало ли реально лучше. Что зафиксировать:

1. **Текущие SLI/SLO** — `latency` (p50/p95/p99), `error rate`, `throughput`. Это критерий, по которому потом судим об успехе.
2. **Профиль нагрузки** — пиковый RPS, среднее число concurrent users. Тюнить надо под реальный трафик, а не под синтетику.
3. **Окружение** — версия `JDK`, контейнерные лимиты, текущие JVM-флаги. Поведение GC и JIT сильно зависит от версии.
4. **GC-логи и JFR-записи** — хотя бы за сутки под реальной нагрузкой, чтобы увидеть полную картину, а не случайный срез.

```bash
# Минимальный набор флагов для сбора baseline
java -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=100m \
     -XX:StartFlightRecording=filename=baseline.jfr,duration=60m,settings=profile \
     -jar app.jar
```

> На интервью подчёркивайте: тюнинг — это **инженерный процесс** с гипотезой, измерением и верификацией, а не "поигрались с флагами и стало лучше".

## Q2. Какие метрики обязательно мониторить перед изменениями?

Мониторить нужно метрики на трёх уровнях: что видит пользователь (latency, throughput), что происходит внутри JVM (heap, GC, threads) и на чём всё крутится (CPU). Без всех трёх не получится связать симптом с причиной — например, отличить алгоритмическую проблему от GC-пауз. Минимальный набор:

| Категория | Метрики | Источник |
|-----------|---------|----------|
| Latency | p50, p95, p99 response time | Prometheus / Micrometer |
| Throughput | RPS, transactions/sec | Application metrics |
| CPU | utilization, load average, system vs user | `cAdvisor` / node exporter |
| Heap | used, committed, max | `jvm_memory_bytes_used` |
| GC | pause duration p95/p99, frequency, overhead % | `jvm_gc_pause_seconds` |
| Allocation | allocation rate (MB/s), promotion rate | GC logs / JFR |
| Threads | thread count, blocked threads | `jvm_threads_current` |

Полезно держать бизнес-метрику рядом (конверсия, ошибки заказов), чтобы видеть реальный пользовательский impact.

## Q3. Как корректно проверять эффект тюнинга?

Главное правило: меняем **один** параметр за итерацию и прогоняем одинаковый нагрузочный профиль. Если поменять сразу несколько флагов и результат улучшится, вы не узнаете, какой именно из них помог (а какой, возможно, навредил). Каждое изменение должно проверяться как отдельная гипотеза.

Цикл проверки выглядит так:

1. **Baseline** — сбор метрик (точка отсчёта).
2. **Гипотеза** — что менять и зачем.
3. **Изменение** — один параметр за итерацию.
4. **Нагрузочный тест** — тот же профиль нагрузки.
5. **Решение** — улучшился ли `p99` и не упал ли throughput?
   - Если **да** → фиксируем результат как новый baseline и возвращаемся к шагу «Гипотеза» для следующего изменения.
   - Если **нет** → откатываем изменение и формулируем новую гипотезу, снова возвращаясь к шагу «Гипотеза».

Сравнивать нужно не только средние, но и **хвосты распределения** (`p99`, `p999`) и стабильность во времени — именно хвосты бьют по пользователю, а среднее их маскирует. Лучший формат проверки: **A/B по двум одинаковым инстансам** под одинаковой нагрузкой — так вы убираете влияние внешних факторов (соседей по кластеру, фоновых задач) и сравниваете именно эффект изменения.

## Q4. (!) Какие сборщики мусора доступны в современной JVM и чем они отличаются?

В `JDK 21+` доступны следующие production-ready сборщики:

| Сборщик | Тип | Max пауза | Heap | Подходит для |
|---------|-----|-----------|------|--------------|
| `Serial` | STW | секунды | малый | embedded, single-core |
| `Parallel` (Throughput) | STW | сотни ms | средний | batch, offline задачи |
| `G1` (default) | concurrent + STW | десятки ms | средний-большой | универсальный backend |
| `ZGC` | concurrent | < 1 ms | большой | low-latency, большие heap |
| `Shenandoah` | concurrent | < 10 ms | средний-большой | low-latency (OpenJDK) |

Как выбрать GC, по характеру нагрузки:

- **Throughput важнее, latency не критична** → `Parallel GC`.
- **Универсальный backend, heap < 32 GB** → `G1 GC`.
- **Жёсткий SLA по паузам, heap > 16 GB** → дальше смотрим на сборку JDK:
  - на любом JDK (Oracle JDK или OpenJDK) → `ZGC`;
  - на OpenJDK → также подходит `Shenandoah`.
- **Embedded / single-core** → `Serial GC`.

Ключевое отличие — насколько сборщик останавливает приложение (Stop-The-World):

- `Serial` и `Parallel` останавливают все потоки на время всех фаз сборки — паузы растут вместе с heap.
- `G1` маркирует мусор concurrent (параллельно с приложением), но саму эвакуацию/компактизацию делает в STW — паузы умеренные и предсказуемые.
- `ZGC` и `Shenandoah` выполняют почти всю работу, включая перемещение объектов, concurrent — поэтому их паузы почти не зависят от размера heap.

Чем меньше STW, тем дороже это обходится по CPU (барьеры и concurrent-потоки) — отсюда и разница в применимости.

## Q5. (!) Как устроен G1 GC и какие у него фазы?

`G1` (`Garbage-First`) делит heap на **регионы** одинакового размера (обычно 1-32 МБ) и собирает в первую очередь те регионы, где больше всего мусора (отсюда и название — «мусор сначала»). Регион в любой момент играет одну из ролей (`Eden`, `Survivor`, `Old`, `Humongous`), и эта роль может меняться — это даёт гибкость по сравнению с фиксированными непрерывными поколениями старых сборщиков. Цель G1 — уложиться в заданный target по паузе (`-XX:MaxGCPauseMillis`), собирая ровно столько регионов, сколько успевает за это время.

Heap при этом выглядит как набор перемешанных регионов разных ролей: часть занята под `Eden`, часть под `Survivor`, часть под `Old`, отдельные регионы — под `Humongous`, а ещё часть стоит свободной (`Free`) и готова принять новые объекты. Расположение ролей не фиксировано: один и тот же регион в разные моменты может быть и `Eden`, и `Old`.

**Фазы работы G1:**

1. **Young GC (STW)** — эвакуация живых объектов из `Eden` и `Survivor` регионов. Самая частая и короткая операция: молодые объекты в основном уже мертвы, копировать приходится мало.
2. **Concurrent Marking** — определение живых объектов во всём heap параллельно с приложением, чтобы потом знать, какие Old-регионы выгоднее собирать:
   - Initial Mark (STW, piggyback на Young GC — отдельной паузы не добавляет)
   - Concurrent Mark (основная работа, идёт параллельно с приложением)
   - Remark (STW, дочищает изменения, сделанные приложением во время Concurrent Mark)
   - Cleanup (частично STW, освобождает полностью пустые регионы)
3. **Mixed GC (STW)** — эвакуация Young + части Old-регионов с наибольшим количеством мусора. Именно так G1 постепенно чистит старое поколение, не сваливаясь в Full GC.
4. **Full GC (STW)** — fallback, когда G1 не успевает за нагрузкой. Это однопоточная (в старых JDK) полная сборка с длинной паузой. **Её нужно избегать** — каждый Full GC сигнализирует, что концепция G1 в данном случае не справляется (слишком высокий allocation rate, мало heap, фрагментация).

```java
// Ключевые флаги G1
// -XX:+UseG1GC                        (default в JDK 9+)
// -XX:MaxGCPauseMillis=200            (target pause, default 200ms)
// -XX:G1HeapRegionSize=16m            (размер региона, степень 2)
// -XX:InitiatingHeapOccupancyPercent=45  (порог запуска marking)
```

## Q6. (!) Как работает ZGC и почему у него паузы менее 1 мс?

`ZGC` — сборщик мусора с паузами **меньше 1 мс**, которые не растут с размером heap. Секрет в том, что почти вся работа (маркировка и перемещение объектов) идёт concurrent, а STW-паузы фиксированной длины и не зависят от объёма данных. Это достигается тремя механизмами:

1. **Colored pointers** — ZGC прячет метаинформацию о состоянии объекта (marked, remapped, finalizable) прямо в неиспользуемых битах 64-битного указателя. Благодаря этому GC «помечает» объект, меняя биты в ссылке, а не сам объект, — и может делать это, не останавливая приложение.

2. **Load barriers** — на каждое чтение ссылки JIT вставляет короткую проверку (barrier). Если объект уже перемещён, barrier прозрачно для кода подменяет ссылку на новый адрес. Так приложение всегда работает с актуальными ссылками, даже пока GC двигает объекты.

3. **Concurrent relocation** — объекты переезжают параллельно с работой приложения; старый и новый адрес связаны через forwarding tables, по которым load barrier и находит актуальное расположение.

Фазы ZGC идут по порядку (короткие STW-паузы отмечены отдельно, всё остальное — concurrent):

1. **Pause Mark Start** — STW-пауза < 1 мс.
2. **Concurrent Mark** — маркировка параллельно с приложением.
3. **Pause Mark End** — STW-пауза < 1 мс.
4. **Concurrent Process Non-Strong References** — обработка не-strong ссылок concurrent.
5. **Concurrent Reset Relocation Set** — формирование relocation set concurrent.
6. **Concurrent Relocate** — перемещение объектов concurrent.

Только две короткие фазы (Pause Mark Start и Pause Mark End) останавливают приложение, и обе укладываются в < 1 мс.

**Характеристики ZGC (JDK 21+):**
- Паузы: < 1 мс (не растут с размером heap)
- Heap: от МБ до **16 ТБ**
- CPU overhead: 10-15% на barriers
- Generational ZGC (`-XX:+UseZGC -XX:+ZGenerational`) — с JDK 21 (default в JDK 23+)

```bash
# Включение ZGC
java -XX:+UseZGC -XX:+ZGenerational \
     -Xmx8g -Xms8g \
     -jar app.jar
```

> `Generational ZGC` значительно эффективнее классического — он разделяет heap на young/old генерации и реже сканирует long-lived объекты.

## Q7. Как работает Shenandoah GC и чем он отличается от ZGC?

`Shenandoah` — concurrent сборщик из `OpenJDK` (разработка Red Hat) с целью низких пауз, как и ZGC. Главное архитектурное отличие: для отслеживания перемещённых объектов он использует **Brooks pointers** — лишнее машинное слово в заголовке каждого объекта, указывающее на его актуальный адрес. ZGC решает ту же задачу битами в самих указателях (colored pointers) и потому не платит памятью за объект. Это и определяет компромиссы между ними:

| Аспект | `ZGC` | `Shenandoah` |
|--------|-------|--------------|
| Механизм | Colored pointers + load barriers | Brooks pointers + load/store barriers |
| Паузы | < 1 мс | < 10 мс (обычно 1-5 мс) |
| Max Heap | 16 ТБ | ограничен RAM |
| Overhead на объект | 0 | +1 machine word (forwarding ptr) |
| Доступность | Oracle JDK + OpenJDK | Только OpenJDK (Red Hat) |
| Generational | Да (JDK 21+) | Experimental (JEP 404) |

```bash
# Включение Shenandoah
java -XX:+UseShenandoahGC \
     -XX:ShenandoahGCHeuristics=adaptive \
     -Xmx8g -Xms8g \
     -jar app.jar
```

**Что выбирать.** Для новых проектов чаще берут `ZGC` — паузы меньше, нет overhead на объект, и он есть в любом современном JDK. `Shenandoah` остаётся разумным выбором, если вы уже на `OpenJDK`-сборке Red Hat и хотите low-latency GC с долгой проверенной историей в production.

## Q8. Как выбирать GC под разные SLA?

Выбор GC определяется **SLA** и характеристиками нагрузки:

| SLA | Рекомендация | Почему |
|-----|-------------|--------|
| p99 latency < 500 мс | `G1` с тюнингом | default, достаточно для большинства |
| p99 latency < 50 мс | `ZGC` или `Shenandoah` | concurrent компактизация |
| Max throughput (batch) | `Parallel GC` | минимальный overhead |
| Heap > 32 ГБ, low latency | `ZGC` | масштабируется до ТБ |
| Минимальный footprint | `Serial` или `Epsilon` | embedded, short-lived процессы |

**Компромисс:** чем ниже паузы, тем выше требования к CPU (barriers, concurrent threads) и операционной дисциплине. Низколатентный GC не «бесплатен» — вы платите процессорным временем за то, чтобы сборка шла параллельно с приложением.

```java
// Пример: сервис с жёстким SLA p99 < 20ms
// До: G1, p99 = 45ms из-за mixed GC пауз
// -XX:+UseG1GC -Xmx16g -XX:MaxGCPauseMillis=20

// После: ZGC, p99 = 8ms
// -XX:+UseZGC -XX:+ZGenerational -Xmx16g
// CPU вырос на ~12%, но SLA выполняется стабильно
```

## Q9. Когда имеет смысл переходить с G1 на ZGC/Shenandoah?

Переход оправдан только когда **одновременно** выполняются все условия — поодиночке каждое не повод менять сборщик:

1. Бизнесу реально важна низкая tail latency под большой нагрузкой (`p99`/`p999`), а не среднее.
2. `G1` после разумного тюнинга всё равно не укладывается в SLA — mixed GC паузы стабильно превышают target.
3. Heap достаточно большой (> 8-16 ГБ): именно на больших heap concurrent-сборка окупает свой overhead. На малом heap G1 обычно не хуже.
4. Есть запас по CPU (10-15% уйдёт на барьеры и concurrent-потоки нового сборщика).

**Перед переходом обязательно:**
- Провести нагрузочное тестирование с реальным профилем трафика
- Оценить рост CPU utilization
- Проверить совместимость с используемыми библиотеками (weak references, finalizers)
- Убедиться, что JDK-версия поддерживает выбранный GC в production-ready состоянии

## Q10. (!) Какие ключевые JVM-флаги управляют G1 GC?

```bash
# === Размер Heap ===
-Xms4g -Xmx4g               # Фиксированный heap (Xms = Xmx для production)

# === G1 основные ===
-XX:MaxGCPauseMillis=200     # Target пауза (default 200ms)
-XX:G1HeapRegionSize=16m     # Размер региона (1-32MB, степень 2)
-XX:G1NewSizePercent=20      # Минимум Young Gen (% от heap)
-XX:G1MaxNewSizePercent=60   # Максимум Young Gen (% от heap)

# === Concurrent Marking ===
-XX:InitiatingHeapOccupancyPercent=45  # Порог запуска marking
-XX:G1MixedGCCountTarget=8            # Кол-во mixed GC циклов
-XX:G1MixedGCLiveThresholdPercent=85   # Макс. liveness для mixed GC

# === Параллелизм ===
-XX:ParallelGCThreads=8               # STW потоки (обычно = CPU cores)
-XX:ConcGCThreads=2                   # Concurrent потоки (1/4 от parallel)
```

Важно: **не трогайте флаги без baseline метрик**. `G1` хорошо самонастраивается через `-XX:MaxGCPauseMillis`. Начинайте тюнинг только с него, и лишь потом подкручивайте остальное.

## Q11. Какие сигналы в GC-логах говорят о реальной проблеме?

Само по себе наличие GC — норма. Проблема — это устойчивый тренд к ухудшению или признаки, что сборщику не хватает ресурсов.

**Красные флаги (реальная проблема):**
- Рост pause `p99` при неизменной нагрузке — деградация без видимой причины.
- Частые **Full GC** — `G1` не успевает за concurrent marking и сваливается в дорогую полную сборку.
- Слабое освобождение памяти после сборок — либо утечка, либо слишком много live data (heap мал).
- Ускоряющийся рост occupancy `Old Generation` — мусор копится быстрее, чем собирается.
- `To-space exhausted` — не осталось свободных регионов, куда эвакуировать живые объекты.
- `Evacuation Failure` — heap фрагментирован, эвакуация не удалась.

**Не проблема сама по себе (не путать с симптомом):**
- «GC стал чаще» — нормально, если latency и SLO не деградируют. Чаще ≠ хуже.
- Рост usage `Eden` — естественен при росте нагрузки.
- Единичные длинные паузы на старте — это JIT warm-up, а не GC.

```bash
# Анализ GC-логов через GCViewer или GCEasy
# Ключевые метрики из лога:
# [gc,heap] GC(42) Eden: 2048M -> 0M  Survivors: 128M -> 128M  Old: 3200M -> 2800M
# [gc      ] GC(42) Pause Young (Normal) 5380M -> 2928M 23.456ms
```

## Q12. Как включить и анализировать GC-логи?

С `JDK 9+` используется **Unified Logging** (замена старых `-XX:+PrintGCDetails`):

```bash
# Полный GC-лог для production
java -Xlog:gc*:file=gc-%t.log:time,uptime,level,tags:filecount=10,filesize=50m \
     -jar app.jar

# Краткий вывод (только паузы)
java -Xlog:gc:stdout:time,uptime \
     -jar app.jar

# Подробный (включая age table, heap regions)
java -Xlog:gc*,gc+age=trace,gc+heap=debug:file=gc-detailed.log:time,uptime,level,tags \
     -jar app.jar
```

**Инструменты анализа:**
- [GCEasy](https://gceasy.io/) — онлайн-анализ GC-логов
- [GCViewer](https://github.com/chewiebug/GCViewer) — desktop-приложение
- `JFR` + `JMC` — встроенный в JDK, самый мощный
- `Censum` (Azul) — для Zing/Zulu

## Q13. Как объяснить влияние allocation/promotion на latency?

Связь прямая: чем больше объектов приложение создаёт и чем дольше они живут, тем чаще и тяжелее работает GC, и тем сильнее скачет tail latency.

- Высокий **allocation rate** (> 1 GB/s) быстро заполняет `Eden` → Young GC случается чаще.
- Высокий **promotion rate** (много объектов «доживают» до `Old`) перегружает старое поколение и провоцирует более дорогие Mixed/Full GC.
- Итог: паузы растут, а tail latency становится нестабильной — пользователь периодически ловит длинные ответы.

Цепочка причин и следствий выглядит так: высокий allocation rate → частый Young GC → высокий promotion rate → `Old Gen` растёт → Mixed / Full GC → долгие паузы и рост `p99 latency`.

**Как снизить allocation rate:**

```java
// ❌ Плохо: создание объектов в hot path
public String processRequest(Request req) {
    // Каждый вызов создаёт StringBuilder, DateTimeFormatter, промежуточные строки
    return new StringBuilder()
        .append(DateTimeFormatter.ISO_DATE.format(LocalDate.now()))
        .append(" - ")
        .append(req.getData().toUpperCase())
        .toString();
}

// ✅ Лучше: переиспользование, избегание лишних аллокаций
private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

public String processRequest(Request req) {
    // Переиспользуем formatter, избегаем промежуточных строк
    return FORMATTER.format(LocalDate.now()) + " - " + req.getData().toUpperCase();
}
```

> Сильный ответ: "мы снизили временные аллокации в hot-path, allocation rate упал с 1.2 GB/s до 400 MB/s, pause p99 снизился на 30%".

## Q14. Что такое Humongous Allocations в G1 и как с ними бороться?

В `G1` объект считается **humongous**, если он занимает более **50% размера региона**. Такие объекты обрабатываются особым образом, и это создаёт проблемы:
- Размещаются напрямую в Old Generation, занимая один или несколько смежных регионов целиком — даже если объект чуть больше половины региона, второй регион используется наполовину впустую.
- Не перемещаются обычной эвакуацией (до JDK 8u60; после — могут собираться уже в Young GC).
- Требование смежных свободных регионов фрагментирует heap и в пределе провоцирует Full GC, когда подряд идущих свободных регионов не находится.

```bash
# Обнаружение в GC-логах
# [gc,heap] GC(12) Humongous regions: 42->38

# Увеличение размера региона уменьшает кол-во humongous
-XX:G1HeapRegionSize=32m   # Теперь humongous > 16 МБ
```

**Частые причины:**
- Большие `byte[]` (сериализация, I/O буферы)
- Крупные `String` (JSON/XML ответы)
- Большие коллекции, создаваемые за раз (`Arrays.copyOf` при расширении `ArrayList`)

**Решения:**
1. Увеличить `-XX:G1HeapRegionSize`
2. Использовать streaming вместо буферизации (подробнее в [вопросах по Memory Management](memory-management-interview.md))
3. Пулить буферы (`ByteBuffer.allocateDirect` + pool)

## Q15. (!) Из каких областей состоит память JVM-процесса?

Память JVM **не ограничивается heap** — это самая частая ошибка при сайзинге. Кроме объектов в heap процесс держит метаданные классов, скомпилированный JIT-код, стеки потоков, off-heap буферы и внутренние структуры самой JVM. Все они входят в RSS процесса, но не учитываются `-Xmx`. Полная картина:

Память JVM-процесса (RSS) делится на три группы областей:

- **Heap (`-Xmx`):**
  - `Eden`;
  - `Survivors`;
  - `Old Gen`.
- **Non-Heap:**
  - `Metaspace` — классы, методы;
  - `Compressed Class Space`;
  - `Code Cache` — JIT-код.
- **Native:**
  - Thread Stacks (`-Xss` × N потоков);
  - Direct Buffers (NIO);
  - JNI / native-библиотеки;
  - GC Native Data;
  - Internal JVM (symbol tables и пр.).

**Формула для расчёта общего потребления:**

```
RSS ≈ Heap (Xmx)
    + Metaspace (MaxMetaspaceSize)
    + Code Cache (~240 MB default)
    + Thread stacks (Xss × thread_count)
    + Direct Buffers (MaxDirectMemorySize)
    + GC overhead (10-20% от heap для G1)
    + JNI / native libraries
    + Internal JVM structures
```

> В контейнерах это критически важно — `Xmx` = container memory limit приводит к `OOMKilled`, потому что не учитывает non-heap память.

## Q16. Что такое Native Memory и как его отслеживать?

**Native Memory** — память, которую JVM выделяет за пределами Java heap напрямую у ОС через `malloc`/`mmap`. Её не видит GC и не ограничивает `-Xmx`, поэтому утечки тут диагностируются сложнее всего. Что сюда входит:
- Thread stacks, `Metaspace`, Code Cache, GC structures, Direct Buffers, JNI.

Отследить её точно помогает встроенный механизм **Native Memory Tracking (NMT)**, разбивающий native-память по категориям.

**Native Memory Tracking (NMT) на практике:**

```bash
# Включение NMT (5-10% overhead)
java -XX:NativeMemoryTracking=summary -jar app.jar

# Снимок через jcmd
jcmd <pid> VM.native_memory summary

# Пример вывода:
# Total: reserved=5234MB, committed=3891MB
# - Java Heap (reserved=4096MB, committed=4096MB)
# - Class (reserved=1056MB, committed=42MB)      # Metaspace
# - Thread (reserved=256MB, committed=256MB)       # 256 threads × 1MB
# - Code (reserved=248MB, committed=68MB)          # JIT Code Cache
# - GC (reserved=198MB, committed=198MB)
# - Internal (reserved=12MB, committed=12MB)
# - Direct (reserved=64MB, committed=64MB)         # NIO Direct Buffers

# Сравнение двух снимков (baseline)
jcmd <pid> VM.native_memory baseline
# ... через время ...
jcmd <pid> VM.native_memory summary.diff
```

Подробнее о диагностике утечек памяти — в [Memory Management](memory-management-interview.md).

## Q17. (!) Как правильно рассчитать размер heap и off-heap?

Подход тот же, что в Q15: heap — лишь часть RSS. Эмпирическое правило для контейнеров — **оставлять 25-30% от memory limit на non-heap** (Metaspace, Code Cache, стеки, direct-буферы, накладные расходы GC). Если отдать heap всю память, контейнер словит `OOMKilled` ещё до того, как Java увидит `OutOfMemoryError`:

```bash
# Container memory limit = 4 GB
# Расчёт:
# Heap:          3 GB  (-Xmx3g)
# Metaspace:     ~256 MB (обычно 100-300 MB)
# Code Cache:    ~240 MB
# Thread stacks: ~200 MB (200 threads × 1 MB)
# Direct buffers: ~128 MB
# GC overhead:   ~150 MB
# Итого non-heap: ~974 MB → берём 1 GB запас

java -Xms3g -Xmx3g \
     -XX:MaxMetaspaceSize=256m \
     -XX:ReservedCodeCacheSize=256m \
     -XX:MaxDirectMemorySize=128m \
     -Xss512k \
     -jar app.jar
```

**Типичные ошибки:**
- `-Xmx` равен container limit (100% памяти на heap → `OOMKilled`)
- Не ограничен `MaxMetaspaceSize` (по умолчанию unlimited)
- Не учтён `MaxDirectMemorySize` (по умолчанию = `-Xmx`)
- Thread stack (`-Xss`) слишком большой (default 1 MB, часто достаточно 512 KB)

## Q18. Что такое String Deduplication и когда она полезна?

`String Deduplication` — функция `G1` (и ZGC) GC, которая находит строки с одинаковым содержимым backing-массива (`char[]/byte[]`) и заставляет их ссылаться на один общий массив. Сами объекты `String` остаются разными — дедуплицируется именно внутренний массив символов, на который приходится основной объём памяти. Делается это во время GC, на объектах, переживших несколько циклов (то есть «осевших» в памяти):

```bash
# Включение (только для G1 и ZGC)
-XX:+UseStringDeduplication
-XX:StringDeduplicationAgeThreshold=3  # после N GC циклов (default 3)
```

**Когда полезна:**
- Приложение держит много строк в памяти (кеши, справочники)
- Много дублирующихся значений (коды стран, статусы, имена полей)
- Heap pressure высокий, а аллокации нельзя изменить

**Когда НЕ полезна:**
- Строки уникальные (UUID, hash-значения)
- Приложение с маленьким heap (overhead на dedup tables)
- Строки короткоживущие (собираются Young GC раньше, чем dedup сработает)

> На практике String Deduplication может сэкономить 10-30% heap в приложениях с кешами. Проверяйте через `jcmd <pid> GC.string_dedup_stats`.

## Q19. Как настроить Metaspace и зачем?

`Metaspace` (заменил `PermGen` в JDK 8) хранит метаданные классов: `Class`-объекты, vtables, методы, constant pool. Ключевая разница с `PermGen` — Metaspace живёт в native-памяти и по умолчанию **не ограничен**, поэтому растёт до исчерпания памяти хоста/контейнера. Это удобно (нет старых `OutOfMemoryError: PermGen space`), но опасно для контейнеров — лимит надо ставить руками:

```bash
# Ограничение Metaspace (по умолчанию unlimited!)
-XX:MaxMetaspaceSize=256m

# Начальный размер (избегает ресайзов при старте)
-XX:MetaspaceSize=128m

# Compressed Class Space (часть Metaspace для class pointers)
-XX:CompressedClassSpaceSize=128m
```

**Почему важно ограничивать:**
- Без ограничения Metaspace может расти до исчерпания native memory
- В контейнерах неограниченный Metaspace → `OOMKilled` без Java `OutOfMemoryError`
- Leak классов (через ClassLoaders) может съесть всю доступную память

**Нормальный размер:**
- Микросервис на Spring Boot: 100-200 МБ
- Монолит с большим количеством зависимостей: 200-400 МБ
- Приложение с dynamic class generation (Groovy, CGLIB): 300-500 МБ

Подробнее о Metaspace leaks — в [Q28](#q28-что-такое-metaspace-leak-и-как-его-диагностировать).

## Q20. (!) Как работает JIT-компилятор и Tiered Compilation?

`JIT` (`Just-In-Time`) компилирует байт-код в нативный машинный код прямо во время выполнения — но компилирует не всё подряд, а только «горячие» методы, которые вызываются часто. Холодный код выгоднее интерпретировать, чем тратить время на его компиляцию. Чтобы найти баланс между скоростью старта и пиковой производительностью, `HotSpot JVM` использует **Tiered Compilation** — пятиуровневую систему, где код по мере «разогрева» проходит от интерпретатора к всё более агрессивно оптимизированным версиям:

Уровни связаны так: из **Level 0** (интерпретатор) код может уйти на любой из C1-уровней — **Level 1** (C1 simple), **Level 2** (C1 + counters) или **Level 3** (C1 + full profiling); из **Level 3** прогретый код переходит на **Level 4** (C2 optimized) — самый оптимизированный.

| Уровень | Компилятор | Описание |
|---------|-----------|----------|
| 0 | Интерпретатор | Байт-код интерпретируется, собирается профиль |
| 1 | C1 (Client) | Быстрая компиляция, без профилирования |
| 2 | C1 + counters | С invocation/backedge counters |
| 3 | C1 + MDO | Полное профилирование (Method Data Object) |
| 4 | C2 (Server) | Агрессивная оптимизация на основе профиля |

```bash
# Tiered Compilation включён по умолчанию (JDK 8+)
-XX:+TieredCompilation                # default: true
-XX:TieredStopAtLevel=4               # можно ограничить (1 = только C1)
-XX:CompileThreshold=10000            # порог компиляции (invocations)
```

Идея уровней проста: чем «горячее» метод, тем больше в него вкладывается оптимизаций. `C2` применяет агрессивные техники — **inlining**, **loop unrolling**, **escape analysis**, **dead code elimination**, **vectorization**. Но сама компиляция `C2` медленная, поэтому hot-методы сначала быстро компилируются `C1` (чтобы немедленно убрать интерпретацию), и лишь по-настоящему горячие позже переводятся на `C2`. Так JVM не тратит дорогую `C2`-компиляцию на код, который выполнится пару раз.

## Q21. Как JIT и warm-up влияют на производительность в проде?

Свежезапущенная JVM ещё не знает, какой код горячий, поэтому методы сначала исполняются интерпретатором и `C1`, а до агрессивной `C2`-оптимизации доходят только спустя время. Именно поэтому **cold-start** и первые минуты работы часто в 5-10 раз медленнее установившегося режима — это не баг, а нормальный цикл прогрева (warm-up):

Стадии прогрева сменяют друг друга так:

1. **Startup** — интерпретатор.
2. Через 30-60 сек → **Warming** — C1-компиляция.
3. Через 1-5 мин → **Warm** — C2-оптимизация.
4. Дальше стабильно → **Peak** — полная оптимизация.

**Практики для production:**

```bash
# 1. Прогрев перед подачей трафика (Kubernetes readiness probe)
# readinessProbe:
#   httpGet:
#     path: /actuator/health
#   initialDelaySeconds: 60      # дать время на warm-up

# 2. Предварительная компиляция часто используемых классов
-XX:CompileCommandFile=hotmethods.txt

# 3. CDS для ускорения загрузки классов (см. Q34)
-XX:SharedArchiveFile=app-cds.jsa
```

**Не делайте:** бенчмарки без warm-up, сравнение cold vs warm instances, прямое переключение 100% трафика на новый pod.

## Q22. (!) Что такое Escape Analysis и какие оптимизации она включает?

`Escape Analysis` — анализ JIT-компилятора (`C2`), который определяет, **выходит ли объект за пределы метода или потока** («убегает» ли он). Логика простая: если объект гарантированно не виден извне метода, его можно вообще не размещать в heap и не синхронизировать — никто другой к нему не обратится. На этом основаны три оптимизации:

**1. Scalar Replacement (разложение объекта в поля):**

```java
// До оптимизации: аллокация Point на heap
public double distance(double x1, double y1, double x2, double y2) {
    Point p = new Point(x2 - x1, y2 - y1);  // объект не escape
    return Math.sqrt(p.x * p.x + p.y * p.y);
}

// После Escape Analysis + Scalar Replacement:
// JIT заменяет Point на два поля dx, dy — без аллокации
public double distance(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;  // scalar
    double dy = y2 - y1;  // scalar
    return Math.sqrt(dx * dx + dy * dy);
}
```

**2. Stack Allocation** (размещение на стеке вместо heap) — в HotSpot через scalar replacement.

**3. Lock Elision** (удаление ненужных synchronized):

```java
// JIT удаляет синхронизацию, если объект не escape
public String concat(String a, String b) {
    StringBuffer sb = new StringBuffer();  // не escape → lock elision
    sb.append(a);
    sb.append(b);
    return sb.toString();
}
```

```bash
# Escape Analysis включён по умолчанию (JDK 6u23+)
-XX:+DoEscapeAnalysis          # default: true
-XX:+EliminateAllocations      # scalar replacement, default: true
-XX:+EliminateLocks            # lock elision, default: true

# Диагностика (dev only)
-XX:+PrintEscapeAnalysis
-XX:+PrintEliminateAllocations
```

> На интервью: Escape Analysis — причина, по которой создание short-lived объектов в Java может быть **бесплатным**. JIT превращает `new Point(x, y)` в два поля на стеке.

## Q23. Как посмотреть, какие методы JIT скомпилировал, и диагностировать деоптимизации?

```bash
# Лог компиляций
-XX:+PrintCompilation
# Вывод:
#  1234  456  4  com.app.Service::process (42 bytes)
#  ^      ^   ^  ^                         ^
#  time  id  tier  method                  size

# Подробные решения компилятора
-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

# JFR-событие для анализа
# jdk.Compilation, jdk.CompilerInlining, jdk.Deoptimization
```

**Деоптимизация** (`deopt`) — откат с C2 на интерпретатор, когда assumptions нарушены:

```bash
# Причины деоптимизации:
# - Uncommon trap: ветка кода, которая раньше не выполнялась
# - Class hierarchy change: загружен новый подкласс (ломает devirtualization)
# - Null check: null-ссылка в месте, где раньше всегда был non-null
# - Array bounds: выход за границы массива

# Диагностика
-XX:+TraceDeoptimization
# В JFR: jdk.Deoptimization events
```

Сама по себе одна деоптимизация — норма (JIT строит оптимизации на догадках и иногда ошибается). Но **частые** деоптимизации — сигнал о нестабильном профиле нагрузки или о коде, который постоянно «удивляет» JIT (megamorphic calls, непредсказуемые ветвления): метод то компилируется, то откатывается, и приложение работает на не-оптимизированной версии.

## Q24. Что такое On-Stack Replacement (OSR) и Inlining?

**OSR (On-Stack Replacement)** — замена интерпретируемого кода на JIT-скомпилированный **прямо во время выполнения метода** (без ожидания следующего вызова). Важно для длинных циклов:

```java
// Без OSR: метод оптимизируется только при следующем вызове
// С OSR: JIT подменяет код прямо в середине цикла
public long compute() {
    long sum = 0;
    for (int i = 0; i < 10_000_000; i++) {  // OSR может сработать здесь
        sum += transform(i);
    }
    return sum;
}
```

**Inlining** — встраивание тела вызываемого метода в вызывающий. Самая важная оптимизация JIT:

```java
// До inlining:
public int calculate(int x) {
    return doubleIt(x) + 1;
}
private int doubleIt(int x) { return x * 2; }

// После inlining:
public int calculate(int x) {
    return x * 2 + 1;  // вызов doubleIt() устранён
}
```

```bash
# Флаги inlining
-XX:MaxInlineSize=35          # макс. размер метода для inlining (байт-код)
-XX:FreqInlineSize=325        # макс. размер для hot методов
-XX:InlineSmallCode=2000      # макс. размер уже скомпилированного кода

# Диагностика
-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
# Показывает: inlined / too big / callee is too large / hot method too big
```

> Для интервью: inlining важен не сам по себе, а потому что **открывает двери** для других оптимизаций — escape analysis, constant folding, dead code elimination работают лучше на развёрнутом коде.

## Q25. Как искать CPU bottleneck в JVM-приложении?

Принцип: сначала по метрикам подтвердить, что узкое место именно в CPU (а не в I/O, блокировках или сети), и только потом профилировать. Иначе легко потратить время на оптимизацию кода, пока процесс на самом деле ждёт базу. Рабочий путь:

Алгоритм поиска по шагам:

1. Снимаем метрики CPU и latency (Prometheus/Grafana).
2. Проверяем загрузку CPU:
   - если **CPU > 80%** → профилируем через `async-profiler` / `JFR` (CPU flame graph);
   - если **нет** → проблема не в CPU: проверяем I/O, locks, thread contention.
3. По профилю выделяем hot methods и lock contention.
4. Определяем, где причина:
   - если **в коде** → оптимизируем алгоритм;
   - если **в JVM** → тюним параметры GC / JIT.

```bash
# async-profiler — лучший инструмент для CPU профилирования
# Не имеет safepoint bias (см. Q26)
./profiler.sh -d 30 -f flamegraph.html -e cpu <pid>

# JFR — встроенный в JDK
jcmd <pid> JFR.start duration=60s filename=cpu.jfr settings=profile

# Allocation profiling (поиск hot allocations)
./profiler.sh -d 30 -f alloc.html -e alloc <pid>
```

Частая ошибка: пытаться решить алгоритмическую проблему через тюнинг GC. Подробнее о профилировании — в [Application Profiling](application-profiling-interview.md).

## Q26. Что такое safepoint bias и почему профили могут врать?

**Safepoint** — точка в коде, где JVM может безопасно остановить поток (для GC, deopt и т.п.). Проблема в том, что многие профилировщики снимают стек потока **только когда тот стоит в safepoint**, а safepoint'ы расставлены не равномерно. В результате профиль перекошен: время приписывается ближайшему safepoint, а не реальному месту в коде. Это и называется **safepoint bias** — профиль систематически «врёт»:

Наглядно: исполнение чередует код между safepoints и сами safepoint'ы. Стек снимается только в safepoint'ах, а код между ними остаётся невидимым для safepoint-based профилировщика — то есть после первого участка кода идёт safepoint (стек снимается здесь), затем снова код между safepoints, затем следующий safepoint (стек снова снимается здесь).

**Последствия:**
- Короткие hot методы между safepoints **недооцениваются**
- Counted loops без safepoint polls **невидимы** для профилировщика
- Профиль может показать 100% времени в `Thread.sleep()` вместо реального bottleneck

**Решение:** использовать **async-profiler** или `JFR` (AsyncGetCallTrace), которые снимают стеки в произвольных точках:

```bash
# async-profiler — НЕ имеет safepoint bias
./profiler.sh -e cpu -d 30 <pid>

# JFR — использует AsyncGetCallTrace
jcmd <pid> JFR.start settings=profile
```

> Плохая практика: делать вывод по одному профилю без верификации и cross-check разными инструментами.

## Q27. Как устроен Class Loading в JVM и какие проблемы он вызывает?

`ClassLoader` в JVM организован по принципу **delegation hierarchy**:

Иерархия загрузчиков (сверху вниз, каждый следующий — потомок предыдущего):

- **Bootstrap ClassLoader** — `java.base`, core JDK;
- **Platform ClassLoader** — `java.sql`, `java.xml` и т.п.;
- **Application ClassLoader** — classpath приложения;
- **Custom ClassLoaders** — Spring, Tomcat, OSGi.

**Принцип parent-first delegation:**
1. ClassLoader сначала спрашивает **родителя** — может ли тот загрузить класс.
2. Загружает сам, только если родитель не нашёл.
3. Это гарантирует, что системные классы (`java.lang.String`) всегда загружаются Bootstrap-ом и существуют в одном экземпляре — иначе два разных `String` ломали бы безопасность типов.

**Проблемы class loading:**

| Проблема | Причина | Симптом |
|----------|---------|--------|
| `ClassNotFoundException` | Класс не в classpath | Startup crash |
| `NoClassDefFoundError` | Класс был, но не инициализировался | Runtime crash |
| `LinkageError` | Разные ClassLoader-ы загрузили один класс | Class cast fails |
| Metaspace leak | ClassLoader не GC'd из-за ссылки | Растущий Metaspace |
| Slow startup | Тысячи классов при старте | High startup time |

```bash
# Диагностика class loading
-verbose:class                        # JDK 8
-Xlog:class+load=info:stdout          # JDK 9+
-Xlog:class+unload=info:stdout        # отслеживание выгрузки классов
```

## Q28. Что такое Metaspace leak и как его диагностировать?

**Metaspace leak** — ситуация, когда классы загружаются, но никогда не выгружаются, и Metaspace растёт до OOM. Ключевой момент: класс выгружается только вместе со своим `ClassLoader`, а тот собирается GC, лишь когда на него не осталось ни одной ссылки. Если живая ссылка где-то удерживает ClassLoader (а с ним — все его классы), эта память не освобождается:

Механизм по шагам:

1. Код создаёт `ClassLoader`.
2. Тот загружает классы.
3. `ClassLoader` и его классы оседают в Metaspace.
4. Дальше всё зависит от того, собирается ли `ClassLoader` сборщиком мусора:
   - если **нет** (живая ссылка удерживает его) → Metaspace LEAK, память растёт;
   - если **да** → классы выгружаются, память освобождается.

**Частые причины:**
- Dynamic proxy / CGLIB generation без кеширования
- `Groovy` / `Kotlin Script` — каждый eval создаёт новый ClassLoader
- Leak через `ThreadLocal` → ссылка на ClassLoader
- Hot-reload фреймворки (Spring DevTools) при неправильной конфигурации

**Диагностика:**

```bash
# 1. Мониторинг Metaspace
jcmd <pid> VM.native_memory summary | grep Class

# 2. Количество загруженных классов
jcmd <pid> VM.classloader_stats

# 3. Heap dump — ищем множественные ClassLoader-ы
jmap -dump:live,format=b,file=heap.hprof <pid>
# В MAT/VisualVM: Histogram → ClassLoader → retained size

# 4. JFR events
# jdk.ClassLoad, jdk.ClassUnload
```

```bash
# Защита в production
-XX:MaxMetaspaceSize=256m     # ограничить рост
# При достижении лимита → OutOfMemoryError: Metaspace
# Лучше OOM, чем тихий OOMKilled контейнера
```

## Q29. (!) Как правильно настроить JVM в Kubernetes/Docker?

Главная мысль: контейнер ограничивает **всю** память процесса, а `-Xmx` ограничивает только heap. Поэтому heap нужно ужимать так, чтобы под лимит вписались и non-heap (Metaspace, Code Cache, стеки, direct-буферы), и накладные расходы GC. Ключевые правила:

```bash
# 1. Фиксированный heap (Xms = Xmx) — избегаем resize
# 2. Heap = 60-75% от container memory limit
# 3. Ограничить off-heap компоненты
# 4. Container-aware настройки

java -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=75.0 \
     -XX:InitialRAMPercentage=75.0 \
     -XX:MaxMetaspaceSize=256m \
     -XX:ReservedCodeCacheSize=256m \
     -XX:MaxDirectMemorySize=128m \
     -Xss512k \
     -Xlog:gc*:file=/var/log/gc.log:time,uptime,level,tags \
     -jar app.jar
```

**Или с явным Xmx (предпочтительнее для предсказуемости):**

```yaml
# Kubernetes deployment
resources:
  requests:
    memory: "2Gi"
    cpu: "1000m"
  limits:
    memory: "2Gi"   # limits = requests для QoS Guaranteed
    cpu: "2000m"
env:
  - name: JAVA_OPTS
    value: "-Xms1536m -Xmx1536m -XX:MaxMetaspaceSize=256m"
```

Пример раскладки при container memory limit 2 GB:

- Java Heap (`-Xmx`) — 1.5 GB;
- Metaspace — 256 MB;
- Code Cache + Threads — ~200 MB;
- запас — ~70 MB.

> В production важна **повторяемая конфигурация** на уровне Helm chart/manifest, а не "ручные" флаги на конкретном pod.

## Q30. Какие ошибки в контейнерном tuning встречаются чаще всего?

| Ошибка | Последствие | Решение |
|--------|------------|---------|
| `-Xmx` = memory limit | `OOMKilled` (non-heap не учтён) | `-Xmx` = 60-75% от limit |
| Нет `MaxMetaspaceSize` | Metaspace grows → `OOMKilled` | Ограничить 256-512 MB |
| Игнорирование Direct Buffers | NIO съедает native memory | `-XX:MaxDirectMemorySize` |
| Одинаковые флаги для всех сервисов | Over/under provisioning | Профиль под каждый сервис |
| CPU limits слишком жёсткие | CPU throttling → GC паузы растут | Мониторинг throttling |
| Нет `-XX:+UseContainerSupport` | JVM видит host resources | Включён по умолчанию с JDK 10+ |
| Нет стресс-тестов autoscaling | Падение под нагрузкой | Test scale-up/down сценарии |

```bash
# Проверка: сколько CPU/RAM видит JVM в контейнере
jcmd <pid> VM.info | grep -E "available_processors|MaxHeap"

# Или через Java API
Runtime.getRuntime().availableProcessors();
Runtime.getRuntime().maxMemory();
```

## Q31. Как JVM определяет ресурсы в контейнере (Container-Aware JVM)?

До JDK 10 JVM смотрела на ресурсы **хоста**, а не контейнера: видела все ядра и всю память сервера, выставляла под них пулы потоков и heap — и контейнер падал по OOM. С `JDK 10+` JVM по умолчанию **читает cgroup limits**, то есть ограничения самого контейнера:

```bash
# Container-Aware JVM (default: true в JDK 10+)
-XX:+UseContainerSupport

# Как JVM определяет ресурсы:
# CPU:    reads cgroup cpu.cfs_quota_us / cpu.cfs_period_us
# Memory: reads cgroup memory.limit_in_bytes (cgroup v1) 
#         или memory.max (cgroup v2)
```

**Что JVM настраивает автоматически на основе cgroup:**
- `Runtime.availableProcessors()` — по CPU limit/shares
- Default heap size (если нет `-Xmx`) — 25% от memory limit (или 50% для small heaps)
- GC threads (`ParallelGCThreads`, `ConcGCThreads`) — по доступным CPU

**Подводные камни:**

```bash
# Проблема: CPU limit = 1 core → JVM видит 1 CPU
# GC threads = 1, что может быть недостаточно
# Решение: явно задать
-XX:ParallelGCThreads=4 -XX:ConcGCThreads=1

# Проблема: JDK 8 до update 191 НЕ поддерживает cgroup
# Решение: обновить JDK или использовать экспериментальные флаги
-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap  # JDK 8u131+
```

## Q32. Как оптимизировать startup time JVM в контейнерах?

Быстрый startup критичен для autoscaling и rolling deployments: при scale-up под пиком новый pod должен принять трафик за секунды, а не за минуту. Техники бьют по двум главным статьям медленного старта — загрузке классов и JIT-компиляции. Чем агрессивнее метод, тем больше экономия, но тем выше цена (хуже пиковая производительность или сложность сборки):

| Техника | Экономия | Сложность |
|---------|----------|-----------|
| AppCDS (см. [Q34](#q34-что-такое-cdsappcds-и-как-это-ускоряет-запуск)) | 20-40% | средняя |
| Tiered Compilation `-XX:TieredStopAtLevel=1` | 30-50% start | низкая (но хуже peak perf) |
| Spring AOT (GraalVM native) | 80-95% | высокая |
| Lazy initialization | 10-30% | средняя |
| Reduce classpath | 10-20% | низкая |

```bash
# Быстрый старт за счёт только C1 (убираем C2)
# Подходит для short-lived tasks, не для long-running сервисов
java -XX:TieredStopAtLevel=1 -jar app.jar

# Spring Boot: ленивая инициализация бинов
spring.main.lazy-initialization=true

# Ускорение через CDS
java -XX:SharedArchiveFile=app-cds.jsa -jar app.jar
```

Как техники последовательно ужимают время старта:

- Cold Start — 15-30 сек;
- + AppCDS → 10-20 сек;
- + Lazy Init → 7-15 сек;
- + `TieredStopAtLevel=1` → 5-10 сек;
- + GraalVM Native → 0.5-2 сек.

## Q33. Какие JVM-флаги обязательны для production?

Базовый принцип: ещё до тюнинга производительности сервис должен быть **наблюдаемым и диагностируемым**. Минимум — это фиксированный heap, всегда включённые GC-логи и heap dump при OOM: без них post-mortem-разбор инцидента невозможен. Набор флагов ниже именно про это.

```bash
# === Обязательные для любого production-сервиса ===

# Память
-Xms4g -Xmx4g                        # Xms = Xmx (фиксированный heap)
-XX:MaxMetaspaceSize=256m             # ограничить Metaspace

# GC логи (бесценны для диагностики)
-Xlog:gc*:file=/var/log/gc.log:time,uptime,level,tags:filecount=10,filesize=50m

# Heap dump при OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/heapdump.hprof
-XX:+ExitOnOutOfMemoryError           # или CrashOnOutOfMemoryError

# Flight Recorder (always-on в production, ~1% overhead)
-XX:StartFlightRecording=disk=true,maxage=24h,maxsize=1g,dumponexit=true,filename=/var/log/flight.jfr

# === Рекомендуемые ===

# Контейнеры
-XX:+UseContainerSupport              # default в JDK 10+

# Диагностика
-XX:+UnlockDiagnosticVMOptions
-XX:NativeMemoryTracking=summary      # 5-10% overhead, но invaluable

# Encoding
-Dfile.encoding=UTF-8
```

> Главное правило: **GC-логи и heap dump on OOM** должны быть **всегда**. Без них post-mortem диагностика невозможна.

## Q34. Что такое CDS/AppCDS и как это ускоряет запуск?

**CDS** (`Class Data Sharing`) — механизм, который один раз сохраняет уже разобранные метаданные классов в shared archive (`.jsa`-файл), а при следующих запусках просто маппит этот файл в память (memory-mapped) вместо повторного парсинга классов из JAR. **AppCDS** распространяет это и на классы вашего приложения, а не только JDK. Выигрыш — за счёт того, что дорогая работа по разбору классов выполняется заранее, а не на каждом старте:

```bash
# Шаг 1: Создание списка классов
java -XX:DumpLoadedClassList=classes.lst \
     -jar app.jar
# Остановить после полной загрузки

# Шаг 2: Создание архива
java -Xshare:dump \
     -XX:SharedClassListFile=classes.lst \
     -XX:SharedArchiveFile=app-cds.jsa \
     -jar app.jar

# Шаг 3: Запуск с архивом
java -Xshare:on \
     -XX:SharedArchiveFile=app-cds.jsa \
     -jar app.jar
```

**Что даёт CDS/AppCDS:**
- Классы загружаются из memory-mapped archive → **быстрее**, чем parsing из JAR
- Общая память между JVM-процессами на одном хосте (shared pages)
- Экономия 20-40% startup time, 10-15% memory footprint

**Dynamic CDS (JDK 13+)** — автоматическое создание архива:

```bash
# Автоматический CDS при выходе
java -XX:ArchiveClassesAtExit=app-cds.jsa -jar app.jar

# Использование
java -XX:SharedArchiveFile=app-cds.jsa -jar app.jar
```

## Q35. Как работают Compressed Oops и Compressed Class Pointers?

**Compressed Oops** (`Ordinary Object Pointers`) — оптимизация, при которой 64-битные указатели на объекты хранятся в виде **32-битных** значений (через сдвиг и base). Зачем: на 64-битной JVM ссылок в heap огромное количество, и хранить каждую в 8 байт расточительно. Сжатие до 4 байт заметно уменьшает размер объектов и улучшает попадания в кеш — отсюда и выигрыш в памяти и скорости:

```bash
# Включены по умолчанию при heap < 32 GB
-XX:+UseCompressedOops        # default: true (если Xmx < 32 GB)
-XX:+UseCompressedClassPointers  # default: true
```

**Как это работает:**
- 32-бит pointer может адресовать 4 ГБ
- С 8-байтовым выравниванием объектов: 4 ГБ × 8 = **32 ГБ** адресного пространства
- JVM хранит `compressed_oop = real_address >> 3`, при использовании `real_address = compressed_oop << 3`

**Почему это важно:**
- Экономия **~30-40% heap** за счёт меньших указателей
- При `-Xmx` > 32 ГБ Compressed Oops **выключаются** → объекты становятся больше
- Парадокс: heap 31 ГБ может быть **эффективнее**, чем 33 ГБ

```bash
# Проверка
java -XX:+PrintCompressedOopsMode -version
# Вывод: heap address: 0x..., Compressed Oops mode: 32-bit
```

> На интервью: "не ставьте `-Xmx` между 32 и 38 ГБ — вы потеряете Compressed Oops и получите меньше полезного пространства, чем при 31 ГБ".

## Q36. Что такое NUMA-aware GC и когда это важно?

**NUMA** (`Non-Uniform Memory Access`) — архитектура многосокетных серверов, где у каждого CPU есть своя «локальная» память с быстрым доступом и «удалённая» (память соседнего сокета) с заметно более медленным. NUMA-aware GC старается размещать объекты в той памяти, которая локальна для потока, создающего объект, — чтобы приложение реже ходило за данными через медленный межсокетный интерконнект:

Топология выглядит так:

- **Node 0:** CPU 0 обращается к своей Local Memory быстро.
- **Node 1:** CPU 1 обращается к своей Local Memory быстро.
- При этом CPU 0 может достучаться и до памяти Node 1, а CPU 1 — до памяти Node 0, но такой межсокетный доступ медленный.

```bash
# NUMA-aware аллокация в G1 (default: true если NUMA detected)
-XX:+UseNUMA

# Проверка NUMA-топологии
numactl --hardware
# node distances:
# node   0   1
#   0:  10  21    ← доступ к remote node в 2x медленнее
#   1:  21  10
```

**Когда важно:**
- Серверы с 2+ CPU sockets (физические серверы, bare metal)
- Heap > 32 ГБ
- Latency-sensitive приложения

**Когда НЕ важно:**
- Контейнеры с CPU limit 1-4 cores (обычно на одном NUMA node)
- Виртуальные машины с малым количеством vCPU
- Облачные инстансы (обычно NUMA-прозрачны)

## Q37. Какие anti-patterns JVM tuning звучат слабо на интервью?

Общий знаменатель всех слабых ответов — отсутствие инженерного подхода: нет baseline, нет измерений, нет понимания root cause и trade-offs. Сильный кандидат всегда опирается на данные и формулирует условия, при которых решение работает.

| Anti-pattern | Почему плохо | Лучше сказать |
|-------------|-------------|---------------|
| "Ставим random флаги из интернета" | Нет инженерного подхода | "Мы анализировали GC-логи и JFR" |
| "Увеличили heap, стало лучше" (без метрик) | Нет baseline, нет proof | "p99 снизился с X до Y мс" |
| "Нам помогло один раз → универсальное решение" | Нет понимания trade-offs | "Это работает при условии X, Y, Z" |
| "Перешли на ZGC, всё починилось" | Игнорирование root cause | "Сначала снизили allocation rate, затем..." |
| Игнорирование версии `JDK` | Разные JDK — разное поведение | "На JDK 21 с G1 мы получили..." |
| "У нас нет GC-логов в production" | Диагностика невозможна | "GC-логи always-on, ~0 overhead" |

## Q38. Как дать сильный ответ по JVM tuning за 60 секунд?

Структура **CDIC** (Context → Diagnostics → Intervention → Consequence):

> 1. **Контекст:** "E-commerce сервис, SLA p99 < 300ms, 5000 RPS, `JDK 21`, `G1`, heap 8 ГБ"
> 2. **Диагностика:** "`JFR` + GC logs показали allocation rate 1.5 GB/s, частый promotion в Old Gen, mixed GC паузы до 450ms"
> 3. **Изменение:** "Оптимизировали allocation hot-path (pooling буферов, lazy init), увеличили `-XX:G1NewSizePercent` до 40%, подняли `-XX:InitiatingHeapOccupancyPercent` до 55%"
> 4. **Результат:** "p99 с 460ms до 180ms, allocation rate снизился до 500 MB/s, CPU +5%"

Такой ответ показывает:
- Понимание метрик и SLA
- Умение диагностировать через данные, а не интуицию
- Знание конкретных JVM-механизмов
- Измеримый результат

> Подробнее об инженерном подходе к диагностике — в [Application Profiling](application-profiling-interview.md) и [JVM Fundamentals](../jvm/jvm-interview.md).

## See also

- [Memory Management](memory-management-interview.md) — управление памятью, GC roots и диагностика утечек
- [Application Profiling](application-profiling-interview.md) — JFR, async-profiler, flame graphs
- [JVM Fundamentals](../jvm/jvm-interview.md) — архитектура JVM, ClassLoader, байткод
- [Java Concurrency](../programming-languages/java/java-concurrency-interview.md) — многопоточность, lock contention, memory model
- [Kubernetes](../devops/kubernetes-interview.md) — container-aware JVM, resource limits и OOM killer
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — warm-up, startup time, Actuator-метрики

- [Caching Performance](caching-performance-interview.md)
- [Database Performance](database-performance-interview.md)
- [Memory Management](memory-management-interview.md)
- [Network Performance](network-performance-interview.md)
- [Performance Testing](performance-testing-interview.md)
