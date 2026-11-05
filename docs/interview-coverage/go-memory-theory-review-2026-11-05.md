# Независимая проверка теории Go Memory и GC

Проверен весь файл Q1–Q27, его diff относительно `HEAD` и связанный MCQ JSON в режиме чтения. После первоначальной проверки отдельно перепроверены изменённые Q4/Q8 и добавлен официальный SSA compiler source. Это ограниченная проверка теории и источников; не формальная MCQ-приёмка. Дата проверки источников: 2026-11-05.

## Итог

По прочитанному тексту не обнаружено подтверждённых фактических дефектов, требующих исправления. Основные акценты ревизии — отсутствие гарантии фиксированной STW, корректное условие гибридного write barrier, мягкая семантика `GOMEMLIMIT`, профильные отличия `-diff_base`/`-base`, не гарантируемый запуск finalizer/Cleanup и временная природа `sync.Pool` — подтверждены первичными источниками. При последующей правке Q8 уточнение о границе между псевдокодом модели и compiler implementation также подтверждено: runtime-псевдокод задаёт условную модель корректности, тогда как SSA pass может консервативно поместить в write buffer старое и новое значения, опуская барьер лишь в доказуемо безопасных случаях. Удаление из Q4 замечания об отсутствии Go в окружении корректно: это ограничение аудита остаётся в этом отчёте, а не в учебном тексте.

Есть ограничения проверки: Go toolchain в `PATH` не найден, поэтому команды и поведение Go-кода здесь не запускались; заключения основаны на документации и исходниках. Также версии `/src` и `pkg.go.dev` являются актуальными страницами на дату проверки и не заменяют проверку поведения конкретного старого toolchain. Итог: **теория PASS по источникам; runtime NOT_RUN**.

## Матрица вопросов

| Q | Предмет проверки | Результат | MCQ sync |
|---:|---|---|---|
| 1 | Stack/heap, GC сканирует стеки, указатель не гарантирует heap | PASS | Содержательно согласован |
| 2 | Escape analysis как решение компилятора, пример адреса | PASS | Согласован |
| 3 | Условия escape и контекстные оговорки | PASS | Согласован |
| 4 | `-gcflags=-m`, область пакетов, отсутствие локального runtime | PASS, запуск NOT_RUN | Согласован; команду не исполнял |
| 5 | Рост/сжатие стеков, `SetMaxStack`, stack metric | PASS по источникам | Согласован; метрика реализационно-зависима |
| 6 | Concurrent mark-and-sweep, не поколенческий/не compaction, Green Tea | PASS | Согласован |
| 7 | Трёхцветная модель и её границы применимости | PASS | Согласован |
| 8 | Hybrid write barrier: затенение старой ссылки и условное новой | PASS | Согласован |
| 9 | Mark setup/termination STW и конкурентная маркировка/sweep | PASS | Согласован |
| 10 | Паузы без верхней гарантии, метрики и GC CPU | PASS | Согласован |
| 11 | `GOGC`, цель роста кучи с roots, `off` | PASS | Согласован |
| 12 | Мягкий лимит `GOMEMLIMIT`, учитываемая память, CPU limiter | PASS | Согласован |
| 13 | Отличие GC-percent от memory limit | PASS | Согласован |
| 14 | Явный GC и его ограниченная роль | PASS | Согласован |
| 15 | heap pprof, выборочность, `inuse_space`/`alloc_space`, HTTP pprof | PASS | Согласован |
| 16 | Сравнение heap-профилей и семантика `-diff_base`/`-base` | PASS | Согласован; проценты описаны корректно |
| 17 | `allocs/op`, `B/op` и границы вывода из бенчмарка | PASS | Согласован |
| 18 | Подslice/подстрока удерживает backing storage | PASS | Согласован |
| 19 | `delete` и сохранение внутреннего map storage, Swiss Tables | PASS | Согласован |
| 20 | Утечка goroutine как удержание стека и ссылок | PASS | Согласован |
| 21 | Finalizer, `AddCleanup`, `Stop`, `KeepAlive`, отсутствие гарантии запуска | PASS | Согласован |
| 22 | Сравнение Go 1.26+ с G1/ZGC JDK 25 | PASS по указанным версиям | Согласован |
| 23 | Методика сравнения памяти Go/Java и compact object headers | PASS | Согласован |
| 24 | Временная, очищаемая runtime природа `sync.Pool` | PASS | Согласован |
| 25 | Предварительная capacity slice/map и её пределы | PASS | Согласован |
| 26 | Inlining, диагностика, PGO | PASS | Согласован |
| 27 | Критерии GC bottleneck и план измерений | PASS | Согласован |

Для paired MCQ прочитаны все 27 блоков и варианты; ключи в JSON использованы только для проверки связи «вопрос–разбор», а не как свидетельство фактической корректности. Явного расхождения теории и MCQ не найдено. Это не проверка качества дистракторов или угадываемости ключа.

## Проверяемые утверждения и источники

- Официальный [Go GC Guide](https://go.dev/doc/gc-guide), включая формулу heap goal, влияние roots, GOGC, memory limit и latency; проверено 2026-11-05. Руководство уточняет, что лимит мягкий и может быть превышен, а GC может ограничивать CPU при memory pressure.
- Исходник Go runtime [`mbarrier.go`](https://go.dev/src/runtime/mbarrier.go) и [`mgc.go`](https://go.dev/src/runtime/mgc.go); проверено 2026-11-05. Реализация подтверждает старый-pointer deletion barrier, условный insertion barrier при grey stack, mark setup и mark termination с короткими STW.
- [Go 1.26 release notes](https://go.dev/doc/go1.26#runtime); проверено 2026-11-05. Green Tea включён по умолчанию и нацелен на локальность/маркировку небольших объектов.
- [Go runtime metrics](https://pkg.go.dev/runtime/metrics), версия страницы go1.27.3 на дату проверки. Подтверждены `/gc/stack/starting-size:bytes`, deprecated `/gc/pauses:seconds`, `/sched/pauses/total/gc:seconds`, `/cpu/classes/gc/total:cpu-seconds` и описанные смыслы метрик. Наличие конкретной метрики следует сверять через `metrics.All` на целевом runtime.
- [`runtime/debug.SetMemoryLimit`](https://pkg.go.dev/runtime/debug#SetMemoryLimit), [`runtime.SetFinalizer`](https://pkg.go.dev/runtime#SetFinalizer), [`runtime.AddCleanup`](https://pkg.go.dev/runtime#AddCleanup), [`runtime.KeepAlive`](https://pkg.go.dev/runtime#KeepAlive), [`sync.Pool`](https://pkg.go.dev/sync#Pool); проверено 2026-11-05. Подтверждены мягкий лимит и ограничения очистки/пула.
- Compiler SSA pass [`cmd/compile/internal/ssa/writebarrier.go`](https://go.dev/src/cmd/compile/internal/ssa/writebarrier.go) и runtime explanation [`runtime/mbarrier.go`](https://go.dev/src/runtime/mbarrier.go); повторно открыты и проверены 2026-11-05. SSA pass отмечает store для WB, если запись может содержать heap pointer; после проверки включённого WB компилятор буферизует и новое значение, и старое значение из slot. Статически доказанный zeroed destination позволяет опустить нужную часть барьера. Поэтому Q8 верно называет проверку цвета стека объяснительной моделью, а псевдокод — моделью корректности, не буквальным сгенерированным кодом.
- [net/http/pprof](https://pkg.go.dev/net/http/pprof) и [runtime/pprof](https://pkg.go.dev/runtime/pprof); проверено 2026-11-05. Поддерживают изложенные способы снятия профиля и оговорки по heap profile.
- [pprof: Comparing profiles](https://github.com/google/pprof/blob/main/doc/README.md#comparing-profiles); проверено 2026-11-05. `-diff_base` использует проценты относительно общего объёма base profile; `-base` для сравнения накопительных профилей задаёт проценты относительно разницы total. Текст Q16 это различие передаёт верно.
- [Go 1.24 release notes](https://go.dev/doc/go1.24#runtime), [Go 1.27 allocation update](https://go.dev/blog/size-specialized-allocations); проверено 2026-11-05. Подтверждены миграция map на Swiss Tables по умолчанию в Go 1.24 и более дешёвые некоторые малые allocations в Go 1.27.
- Oracle [G1 JDK 25](https://docs.oracle.com/en/java/javase/25/gctuning/garbage-first-g1-garbage-collector1.html), [available collectors JDK 25](https://docs.oracle.com/en/java/javase/25/gctuning/available-collectors.html), [JDK 24 changes](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-24.html), [other considerations / Compact Object Headers](https://docs.oracle.com/en/java/javase/25/gctuning/other-considerations.html); проверено 2026-11-05. Подтверждены G1 pause target, поколенческий ZGC после JDK 24, флаг compact headers и их сокращение заголовка до 64 бит.

## Ограничения

- Go не был установлен/доступен в `PATH`; `go build`, `go test`, бенчмарки, gctrace и pprof-команды не исполнялись. В частности, поведение показанных фрагментов и метрик на конкретной целевой сборке — **NOT_RUN**.
- Не проверял нагрузочный результат, фактическое RSS, JDK/Go benchmark или совместимость разных версий runtime.
- MCQ оценивался только на связь формулировок и вариантов с теорией; formal MCQ checks, независимое слепое голосование и аудит поверхностных подсказок не выполнялись.

## Хэши и объём diff

- Теория `cheatsheets/interview/programming-languages/go/go-memory-gc-interview.md` (актуальный hash после правок Q4/Q8): SHA-256 `b96b867b068da134a677f7127d7c02988f3b852fcecbf5049b675297e0897556`. Предыдущий hash при первичном обзоре: `04c94316e844f137c7eb593f216f5de8990545cf3aebbbb4a675189763fcc599`.
- Paired MCQ seed `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/go/go-memory-gc-interview.json`: SHA-256 `d9572d6b550d4c82e777277487933ac91715b1c5de58cadb38d277496e2b6b77`.
- В `git diff HEAD` для теории виден крупный переписанный diff (512 затронутых строк по краткой статистике), который был прочитан. Изменения не вносились.
