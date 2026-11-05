# Java Concurrency: процессы, гонки и virtual threads

Дата запроса: 2026-11-05. Режим: `SEMANTIC_UPDATE`. Улучшены Q1/Q25/Q46 после коммита `b753c2278682`. Это авторский пакет теории с реальными слепой фазой A и раскрытой фазой B для трёх MCQ; **не FINAL** и не приёмка всех 56 вопросов. Production JSON не редактировался.

## Снимок и границы

- Исходный Markdown: `5dce7d0980795677c0658a8f7c6c6c210917aaf517eb82e77cdc0379b03231fe`.
- Итоговый Markdown: `19bdce86d951b1d63bfcd5194e8f57a7e56e64383ec3876bcb5d966c34a2716a`.
- Зафиксированный seed: `2d941990f381579ea5d2f3ee5e9eabfe85235c0f2135ef64ad34e3e7f8d598ee`; три выбранные записи: `a06470761fc927813fbb3b2f93ab68cf4b0c24b4d2f46d3d56f22b0cba7bc9d2`.
- Изменены только тела Q1/Q25/Q46. Остальные 53 секции, все 56 заголовков, frontmatter, TOC и 25 относительных ссылок сохранены. Java Concurrency имеет 13 авторски улучшенных ответов в этой серии, без объявления всего файла принятым.

## Исправления теории и источники

Q1 разделяет нативный Process/API управления процессом и Thread текущей JVM. Сохранены общая куча и собственные стеки/pc, уточнена область static и class loaders. Убраны универсальные значения времени создания, обещание TLB flush и неверная причинная связь «нет пользовательского обработчика — исключение убивает JVM». Основания: [Process](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Process.html), [Thread](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html), [JVMS §2.5](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5), [JVMS §5.3](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html#jvms-5.3), [JLS static fields](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.3.1.1).

Q25 объясняет нарушение инварианта составной операции, а не только отсутствие синхронизации отдельных обращений. Исправлен пример containsKey/get: get отсутствующего ненулевого ключа возвращает null; NPE появляется при последующей распаковке/разыменовании. ComputeIfAbsent создаёт отсутствующее значение и меняет задачу чтения. Добавлен воспроизводимый потерянный инкремент, исправление incrementAndGet и различие атомарной записи ссылки, безопасной публикации и атомарности нескольких действий. Источники: [ConcurrentHashMap](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html), [AtomicInteger](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html), [JLS 17.4–17.7](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html), [jcstress](https://github.com/openjdk/jcstress). Диагностические инструменты не объявляются доказательством отсутствия любых гонок.

Q46 ограничивает описание supported unmount/pinning версией Java 21, убирает фиксированные размеры стека и гарантии количества потоков/запросов. Различены carrier-планировщик и commonPool; показаны daemon и ожидание результата. Изменение synchronized в JDK 24 приведено отдельно. Источники: [Java 21 guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html), [OpenJDK 21+35 VirtualThread](https://github.com/openjdk/jdk/blob/jdk-21%2B35/src/java.base/share/classes/java/lang/VirtualThread.java), [изменения JDK 24](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-24.html), [Java 24 guide](https://docs.oracle.com/en/java/javase/24/core/virtual-threads.html). Прямое получение JEP 491 дало HTTP 403; вывод о JDK 24 опирается на доступный первичный Oracle migration guide, не на неполученную страницу.

## Runtime: три точных примера и девять проверок

Фактически использован Microsoft/OpenJDK **21.0.10+7-LTS**, `javac --release 21`. Новые зависимости и внешние сервисы не использовались.

Все три Java fence из Markdown скомпилированы и исполнены. Q1: намеренное исключение worker напечатано в stderr, main вывел `main continues`, exit 0. Q25: потерянное обновление `1`, затем атомарный инкремент `2`. Q46: `true:true` и `true`. Это реальные исполнения, а не проверка только синтаксиса.

`MemoryRaceVirtualProbe.java` (`20287da5fb81021196117903a3636d314041ba8a267a906e33b0ad667dc14268`) проверил пять случаев:

1. Coordinated containsKey/remove/get возвращает null без исключения из get.
2. Отдельная распаковка null действительно даёт NPE.
3. ComputeIfAbsent создаёт значение, тогда как get только наблюдает отсутствие.
4. Потоки разделяют static, сохраняя отдельные ThreadLocal-значения.
5. Immutable values и AtomicReference не делают get-then-set атомарной транзакцией; updateAndGet исправляет показанный инвариант.

В отдельной JVM заданы `jdk.virtualThreadScheduler.parallelism=1` и `maxPoolSize=1`: одна проверка параметров и три наблюдения scheduler/pinning. Обычное ожидание latch дало другому VT исполниться. Ожидание latch внутри synchronized удерживало единственный carrier; после освобождения оба потока завершились. JFR с включённым `jdk.VirtualThreadPinned` записал **одно** событие для `pinned-vt`. Итого **9 PASS**, включая проверку конфигурации, плюс три примера. JSON события экспортирован штатным `jfr print`.

Это наблюдения конкретного JDK 21 и одного управляемого чередования: не тест произвольного I/O, не JDK 24 runtime, не нагрузочный тест на 100K запросов и не доказательство всех interleavings. Исходный JFR binary остался в /tmp; точный probe, stdout/stderr, SHA binary и JSON события сохранены переносимо в run.

## Реальный независимый LLM-проход

Run: [20261105T224148Z-java-memory-race-vt-theory-review-r1](../mcq-quality/runs/20261105T224148Z-java-memory-race-vt-theory-review-r1/manifest.json). Новый контекст `/root/java_memory_race_vt_blind_r1`, явно выбранный `gpt-6-luna`, medium. До phase-a receipt он читал только blind-input. Workflow последовательно выполнил prepare → phase-a → reveal; фаза A зафиксирована до раскрытия ключей.

Слепой выбор: **Q1=C, Q25=C, Q46=NO_VALID_OPTION**. Он сохранён неизменным вместе с заметками по 12 вариантам. Полный первый ответ B не был сохранён и не засчитан; последующий разбор выполнен и сохранён по одному вопросу. Фаза B покрыла все 12 text, все sections и три раскрытых ответа теории. Ошибочный выбор C в A для неполного Q25 не переписывался задним числом.

Все полные и per-block хеши трёх отчётов проверены по manifest. Исправление ошибочного input SHA в Q1 сохранено с историей. [Coordinator summary](../mcq-quality/runs/20261105T224148Z-java-memory-race-vt-theory-review-r1/coordinator-review-summary.json) отделяет фактический вывод рецензента от проверки координатором. В частности:

- Ложность wrong A в Q25 сама по себе допустима; дефекты ищутся в полноте, правдоподобии и истинности его corrections. ANSC не может быть PASS при неполном C/A. SFPL — форма, ANSC — полнота, эти оценки не взаимозаменяемы.
- Первоначальная SFPL-претензия Q46 отозвана: длины A15/B14/C16/D18, фраза об unmount есть и в A; точность технического тезиса не является формальным cue без знания предмета. Итог SFPL — UNVERIFIED, не ложный FAIL и не автоматический PASS.
- Полные по форме утверждения Q46 дают ANSC-оценку полноты, но не устанавливают валидный correct. Фактическая валидность остаётся FAIL/NO_VALID_OPTION.

Generated evidence-draft с пустыми checks сохранён как часть истории reveal и **не является принятым evidence**. Record, global sync/checkpoint и FINAL этим работником не выполнялись. Ни самопроверка, ни законченный A/B-проход плохих вопросов не увеличивают число принятых блоков.

## Передача владельцу MCQ

| Q / место | Реальный остаток |
|---|---|
| Q1 C explanation/example | Стоимость switching, page tables/TLB и времена создания требуют конкретной реализации и измерений; универсальные числа не подтверждены API |
| Q1 C edge_cases | Отсутствие custom handler не является само по себе причиной смерти JVM. Условие последнего non-daemon отделить от обычного завершения worker |
| Q1 B counterfactual | Из гипотетической эквивалентности понятий не следует невозможность двух отдельно названных API; лучше показать реальную несовместимость контрактов |
| Q25 A/C text | JSON спрашивает определение и борьбу. A даёт симптом, C — паттерны, оба пропускают способ борьбы; sections после выбора не дополняют text |
| Q25 C explanation/example | Исправить NPE «на get» и семантику computeIfAbsent. Не подменять чтение существующего значения созданием нового |
| Q25 B/D sections | Уточнить атомарность отдельных volatile/ref-access vs составных действий. Volatile не «только для флагов»; safe publication не ограничивается volatile/Atomic. Запись ссылки атомарна и без них |
| Q25 C Knight Capital | Привязка инцидента к race не подтверждена просмотренным первоисточником: [SEC order §14–17](https://www.sec.gov/litigation/admin/2013/34-70694.pdf) описывает deployment/старый код/контроли и итоговую оценку $460M. Исторические $440M и причинную помету нельзя оставлять без контекста/источника; это вывод об отсутствии подтверждения, не утверждение об отсутствии любых гонок в системе |
| Q46 C text | Безусловные «только CPU» и unmount на I/O не защищены при ограничениях Java 21; в фазах A и B выявлено отсутствие полностью верного варианта |
| Q46 A/B sections | Сравнение с интерфейсом ExecutorService ошибочно категоризирует механизм; VT-executor сам реализует этот интерфейс. CPU-bound работа на VT не запрещена, универсальное «хуже» требует условий |
| Q46 C sections | Убрать гарантии ресурсов, 100K запросов без OOM и обязательный порог 10K+. Для [Spring Boot 3.2](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/features.html#features.spring-application.virtual-threads) нужны Java21+ и включение `spring.threads.virtual.enabled=true`, а не просто версия Boot |

В Q1 основной ключ C защитим; sections требуют исправлений. В Q25 корректный полный ответ на JSON-stem не установлен. В Q46 NO_VALID_OPTION подтверждён независимыми A/B. Вопросы не приняты. Заголовки/JSON-stems ещё расходятся; будущую постановку и все четыре варианта править согласованно.

## Проверки и передача пакета

YAML, 56 уникальных заголовков, неизменность TOC и других 53 секций, 25 относительных ссылок, отсутствие inline MCQ и diff-check — PASS. Замороженный seed: schema 56/56 PASS, semantic review NOT_RUN. Неизменённый linkage scanner: **3 errors / 66 warnings / 46 manual** до правки → **0 / 66 / 46** после. Это не semantic PASS: остаются три stem mismatch и предупреждения novel wrong terms (fork, ConcurrentModificationException/immutable, ThreadLocal). Нули errors не закрывают перечисленные смысловые дефекты; пороги/overrides не менялись.

Рабочие логи — `/tmp/java-concurrency-theory-memory-races-virtual-20261105/`; переносимые A/B/runtime-доказательства — в run. Точный адресный пакет: `/tmp/prepared-java-concurrency-theory-memory-races-virtual-20261105.json`. Следующий приоритет от координатора — Test Strategies Q3/Q4/Q8/Q16/Q25/Q35/Q41 по авторскому отчёту MCQ. Прежние чужие изменения сохраняются; JSON вопросов не входит в коммит теории.
