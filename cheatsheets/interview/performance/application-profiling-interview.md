---
title: "Вопросы на собеседовании: Application Profiling"
description: "Практичные вопросы и ответы по profiling Java-приложений: CPU, memory, lock contention, production-подход и связь профилей с метриками."
tags: ["interview", "performance", "application-profiling-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Application Profiling`

Практичные вопросы и ответы по profiling для `Senior Java Developer`: как находить узкие места по `CPU`, памяти, блокировкам и сетевым операциям в production.

Дата последнего обновления: 2026-02-11

## Полезные ссылки

### Официальная документация

- [Java Flight Recorder](https://docs.oracle.com/javase/9/troubleshoot/diagnostic-tools.htm#JSTGD356)
- [JVM Tool Interface](https://docs.oracle.com/javase/9/docs/specs/jvmti.html)
- [Async Profiler](https://github.com/async-profiler/async-profiler)

### См. также

- [`jvm-performance-tuning-interview.md`](jvm-performance-tuning-interview.md) — JVM tuning
- [`memory-management-interview.md`](memory-management-interview.md) — memory diagnostics
- [`../jvm/jvm-interview.md`](../jvm/jvm-interview.md) — JVM fundamentals
- [`../../monitoring/metrics/prometheus.md`](../../monitoring/metrics/prometheus.md) — метрики и алерты
- [`../../monitoring/tracing/distributed-tracing.md`](../../monitoring/tracing/distributed-tracing.md) — трассировка

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Базовый подход**
- [Q1. Что такое profiling и когда он нужен?](#q1-что-такое-profiling-и-когда-он-нужен)
- [Q2. Как выбрать между sampling и instrumentation?](#q2-как-выбрать-между-sampling-и-instrumentation)
- [Q3. Какие метрики обязательно связать с профилем?](#q3-какие-метрики-обязательно-связать-с-профилем)

**Инструменты**
- [Q4. Когда использовать JFR, а когда async-profiler?](#q4-когда-использовать-jfr-а-когда-async-profiler)
- [Q5. Как читать flame graph корректно?](#q5-как-читать-flame-graph-корректно)
- [Q6. Как интерпретировать профиль так, чтобы не ошибиться с root cause?](#q6-как-интерпретировать-профиль-так-чтобы-не-ошибиться-с-root-cause)

**CPU, память, блокировки**
- [Q7. Как профилировать CPU bottleneck?](#q7-как-профилировать-cpu-bottleneck)
- [Q8. Как профилировать allocation и memory pressure?](#q8-как-профилировать-allocation-и-memory-pressure)
- [Q9. Как находить lock contention?](#q9-как-находить-lock-contention)
- [Q10. Что такое on-CPU и off-CPU анализ, и зачем оба?](#q10-что-такое-on-cpu-и-off-cpu-анализ-и-зачем-оба)

**Production и platform**
- [Q11. Как профилировать production безопасно?](#q11-как-профилировать-production-безопасно)
- [Q12. Как профилировать сервис в Kubernetes/Docker?](#q12-как-профилировать-сервис-в-kubernetesdocker)
- [Q13. Как связать profiling с Prometheus/Grafana и алертингом?](#q13-как-связать-profiling-с-prometheusgrafana-и-алертингом)

**Senior-подача**
- [Q14. Какие anti-patterns в profiling чаще всего встречаются?](#q14-какие-anti-patterns-в-profiling-чаще-всего-встречаются)
- [Q15. Как ответить про profiling на senior-раунде за 1 минуту?](#q15-как-ответить-про-profiling-на-senior-раунде-за-1-минуту)

## Q1. Что такое profiling и когда он нужен?

`Profiling` — это измерение реального поведения приложения: где тратится CPU, память, время ожидания и блокировки.  
Он нужен не "вместо метрик", а после сигнала о деградации или при оптимизации критичных сценариев.

Ключевой принцип: profile -> hypothesis -> change -> re-profile.

## Q2. Как выбрать между sampling и instrumentation?

- **Sampling:** низкий overhead, подходит для production, но дает вероятностную картину.
- **Instrumentation:** детальнее, но дороже по overhead, чаще для dev/test.

Практика: в проде обычно sampling (`JFR`, `async-profiler`), instrumentation — точечно в безопасной среде.

## Q3. Какие метрики обязательно связать с профилем?

Минимум:
- latency p95/p99,
- throughput,
- CPU saturation,
- GC pause,
- error rate.

Без метрик профиль легко интерпретировать неверно: "горячий" метод может не быть причиной пользовательской деградации.

## Q4. Когда использовать JFR, а когда async-profiler?

- `JFR`: системный обзор JVM-событий (GC, allocation, monitor enter, threads) с малым overhead.
- `async-profiler`: быстрый и точный CPU/alloc/lock sampling с отличными flame graphs.

Обычно: сначала `JFR` для контекста, затем `async-profiler` для детализации hot-path.

## Q5. Как читать flame graph корректно?

- ширина блока = доля ресурса (времени/аллокаций),
- высота = глубина стека,
- искать нужно широкие устойчивые "плато", а не случайные пики.

Частая ошибка: оптимизировать верхний helper-метод вместо настоящей причины внизу дерева вызовов.

## Q6. Как интерпретировать профиль так, чтобы не ошибиться с root cause?

Проверять три вещи:
1. повторяется ли картина в нескольких прогонах,
2. есть ли корреляция с метриками и trace,
3. подтверждается ли гипотеза после изменения кода.

Если подтверждения нет — это была не root cause, а симптом.

## Q7. Как профилировать CPU bottleneck?

Шаги:
1. фиксируем окно деградации,
2. снимаем CPU profile на репрезентативной нагрузке,
3. выделяем hot methods и contention,
4. оцениваем алгоритмическую сложность и IO mix.

Сильный ответ включает результат: "после оптимизации hot path CPU -22%, p99 -18%".

## Q8. Как профилировать allocation и memory pressure?

- снимать allocation profile в проблемном окне,
- смотреть allocation hotspots и churn (часто создаваемые временные объекты),
- проверять влияние на GC pause/promotion.

Часто выигрывает не "магический GC-флаг", а снижение временных аллокаций в горячем коде.

## Q9. Как находить lock contention?

Сигналы:
- много потоков в `BLOCKED`/`WAITING`,
- рост времени в monitor/lock событиях,
- падение throughput при росте количества потоков.

Инструменты: `JFR` monitor events, `jstack`, lock profiling в async-profiler.

## Q10. Что такое on-CPU и off-CPU анализ, и зачем оба?

- **on-CPU:** где поток реально выполняется.
- **off-CPU:** где поток ждет (lock, I/O, sleep).

Только on-CPU анализ часто пропускает корень проблемы ожиданий и сетевых задержек.

## Q11. Как профилировать production безопасно?

- короткие сессии,
- минимально достаточный набор событий,
- sampling, а не heavy instrumentation,
- четкие окна и rollback план.

Важно заранее согласовать процесс с SRE/платформой: кто включает, кто анализирует, где хранятся артефакты.

## Q12. Как профилировать сервис в Kubernetes/Docker?

- учитывать cgroup-limits и throttling,
- собирать профили на уровне pod/instance, а не "среднюю температуру",
- синхронизировать профили с deployment/revision.

Anti-pattern: анализировать локальный профиль и переносить выводы на прод-кластер без поправки на среду.

## Q13. Как связать profiling с Prometheus/Grafana и алертингом?

Нормальная цепочка:
- алерт по SLI/latency,
- автотриггер или ручной запуск профилирования,
- привязка артефактов по timestamp/revision,
- пост-инцидентный review и фиксация action items.

Так profiling становится частью операционного цикла, а не "разовой магией".

## Q14. Какие anti-patterns в profiling чаще всего встречаются?

- профилируют без репрезентативной нагрузки,
- оптимизируют micro-hotspot без бизнес-эффекта,
- делают выводы по одному профилю,
- смешивают cold-start и steady-state,
- игнорируют off-CPU и contention.

## Q15. Как ответить про profiling на senior-раунде за 1 минуту?

Шаблон:
1. **Симптом:** "p99 вырос после релиза".
2. **Диагностика:** "по метрикам нашли окно, сняли JFR + async-profiler".
3. **Находка:** "lock contention в пуле + лишние аллокации".
4. **Решение/результат:** "переделали синхронизацию, p99 -35%, CPU -15%".

Главное — показать измеримый результат и воспроизводимый процесс.
