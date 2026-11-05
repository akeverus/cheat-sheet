# Java Concurrency: теория Q24/Q31/Q32/Q35

Дата запроса: 2026-11-05. Режим: `SEMANTIC_UPDATE`. Подготовлена теория; JSON вопросов не изменялся. Это частичный авторский пакет с раскрытым LLM-ревью, **не FINAL** и не приёмка всех 56 вопросов.

## Зафиксированный вход и границы

- Markdown до изменений: SHA-256 `6e76fed25b1dd1aa57c27771371ac3063d876f7a9bb82ab26f240f1266fca9ea`; прежнего diff этого файла не было.
- Текущий JSON при проверке: `06e9c2eac10d1c4b66172ae0831b588ddfdaddc26c669ddf8b42868ece228e71`.
- Подготовленный владельцем MCQ candidate: `4ba5b01a386dde6993d595f1052c1b65fb5c2d1e7e8553d4a30a43a4b26b1f4c`. Он ещё не был применён на момент этой проверки.
- Итоговый Markdown: `e4a5506056ba189a7515570dbbdc2807a2f264414103112975cf6357835ab564`.
- Изменены только тела Q24/Q31/Q32/Q35. Остальные 52 секции, все 56 заголовков, frontmatter, TOC и заключительные ссылки сохранены побайтно. `updated` во frontmatter не использован как признак полной актуализации темы.

Проверены четыре текущих блока MCQ и соответствующие candidate-блоки: все 16 `text`, все sections, различия версий и соответствие видимым заголовкам. Ожидаемые ключи D/B/D/D не менялись. Независимый рецензент прочитал исходный Markdown целиком, затем перечитал четыре изменённых ответа и связанные разделы. Это не означает содержательной приёмки остальных 52 MCQ.

## Исправления теории и первичные источники

| Q | Исправление | Источник |
|---|---|---|
| 24 | Разведены три причины отсутствия прогресса; устранено обещание простоя всей JVM. Указаны границы lock fairness и исключение для untimed `tryLock()`. | [Oracle: starvation/livelock](https://docs.oracle.com/javase/tutorial/essential/concurrency/starvelive.html), [ReentrantLock Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html) |
| 31 | Сравнены четыре сигнатуры. Исполнение, обёртка задачи и наблюдение ошибки рассмотрены отдельно; добавлены `FutureTask` через `execute` и Java 19+ `exceptionNow`. | [Executor](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executor.html), [ExecutorService](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html), [afterExecute](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html#afterExecute(java.lang.Runnable,java.lang.Throwable)), [Future](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html) |
| 32 | Уточнены API, deque, режимы обработки, целевой параллелизм и блокировки. Формула commonPool ограничена конкретным OpenJDK; добавлен отсутствовавший конструктор в полном запускаемом примере. | [ForkJoinPool Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html), [OpenJDK 21+35](https://github.com/openjdk/jdk/blob/jdk-21%2B35/src/java.base/share/classes/java/util/concurrent/ForkJoinPool.java) |
| 35 | Сопоставлены функции и типы результата. Связывание стадий отделено от их запуска и выбора потока; пример показывает последовательное завершение двух входов. | [CompletionStage](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html), [CompletableFuture](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) |

## Runtime и структурные проверки

Реально использован Microsoft/OpenJDK **21.0.10+7-LTS**, `javac 21.0.10 --release 21`. Это отдельный запуск от прежней проверки владельца MCQ на Corretto 22.

- Три точных Java fence из Markdown скомпилированы и исполнены: Q31 → `direct/submitted/wrapped`, Q32 → `49995000`, Q35 → `User-7/Profile-7/9`. После последней правки прозы SHA извлечённых fence повторно сверены с исполненными исходниками.
- Неизменённый `JavaConcurrencyReviewProbe.java` (`7057614a46c54838e3edcb378b313d7610dac51844b953d9947ce620a5230678`) дал **11 PASS**: прямой Executor и исключение вызывающему, сохранение ошибки submit без пользовательского get, причина ExecutionException, execute с FutureTask, inline apply/compose, отсутствие автозапуска входов combine, последовательное завершение, отсутствие автоотмены и исключительное завершение.
- Отдельная JVM с `-XX:ActiveProcessorCount=1 -Djava.util.concurrent.ForkJoinPool.common.parallelism=2` дала ещё **1 PASS**. Итого 12 семантических проверок, плюс три примера; это не исчерпывающая проверка всех interleavings и не экспериментальное доказательство fairness.
- YAML, уникальные Q1–Q56, неизменность заголовков/TOC, отсутствие inline MCQ, 25 относительных ссылок и `git diff --check` — PASS. Candidate schema: 56 вопросов / 56 блоков PASS; semantic review этим скриптом `NOT_RUN`.

Linkage запущен на замороженных мини-деревьях с неизменённым сканером и стандартными путями. До правки: **6 errors / 81 warnings / 56 manual**. Новая теория + текущий seed: **6 / 79 / 56**; в выбранных четырёх Q нет errors, остаются четыре несовпадения stem. Новая теория + candidate: **6 / 75 / 52**; в выбранных Q **0 errors / 0 warnings**. Шесть общих errors относятся к Q1/Q25/Q29/Q34/Q46/Q52 и не закрыты. Пороги и overrides не менялись. Упоминания deque/divide-and-conquer явно определены в Q32, поскольку эти термины есть в вариантах MCQ; первоначальный лексический сигнал Q32→Q51 после этого не воспроизводится.

## Передача владельцу MCQ: пояснения требуют исправления

Первый раскрытый проход отдельного `gpt-6-luna`, medium, подтвердил ключи, но пропустил дефекты sections. После адресной проверки рецензент подтвердил следующие замечания и **отозвал общий PASS пояснений**. Независимое ревью не подменяет проверку координатора.

| Место в candidate | Реальное несоответствие | Требуемый смысл исправления |
|---|---|---|
| Q24 A `how_it_should_be` | Голодание сведено к обходу планировщиком | Обобщить до систематического неполучения нужного ресурса |
| Q32 C `source_of_confusion` | Объясняется отрицание поддержки Runnable после знакомства с RecursiveTask; видимый вариант, наоборот, отрицает ForkJoinTask | Объяснять именно ошибку видимого варианта |
| Q32 C `if_it_were_true` | Из only-Runnable не следует невозможность исполнять CompletableFuture | Async-задачи CF также Runnable; контрфакт заменить на действительное следствие. [Исходник OpenJDK 21+35](https://github.com/openjdk/jdk/blob/jdk-21%2B35/src/java.base/share/classes/java/util/concurrent/CompletableFuture.java) |
| Q35 A `if_it_were_true` | Из одинаковой семантики не следует обязательная deprecation | Показать следствие неразвёрнутой вложенности |
| Q31 D `if_it_were_true` | Удаление метода не означает наличие `@Deprecated` | При предполагаемом удалении сигнатура отсутствовала бы; в Java 21 она есть |

Дополнительно Q31 A утверждает, что одинаковый Future сделал бы Executor и ExecutorService одинаковыми; это не следует из гипотезы, поскольку остаются различия управления жизненным циклом и других методов. Этот контрфакт тоже нуждается в редактуре. Логически неверные пояснения не считаются допустимыми только потому, что ключ однозначен.

Содержательная проверка полноты и ошибочной модели проведена по каждой A/B/C/D, однако отдельная формальная SFPL/ANSC-приёмка и blind A не выполнялись. Готовность пары после этих замечаний не объявляется. JSON принадлежит другому автору и нами не редактировался.

## Открытая следующая работа и доказательства

В связанном тексте подтверждены, но не исправлены этим пакетом: Q33 — тезис о get как единственном способе получения результата после Java 19; Q37 — обещание параллельного исполнения каждого supplyAsync; Q51 — безусловная политика LIFO/FIFO и сравнение ExecutorService с его собственной реализацией ForkJoinPool. Эти ответы и их MCQ требуют отдельного согласованного пакета. Не следует считать всю тему проверенной.

Снимки, полный runtime-log, три исполнения, исходный probe, структурные проверки и три linkage-отчёта: `/tmp/java-concurrency-theory-followup-20261105/`. Первый LLM-отчёт: `peer-review.json` в этой директории; его отозванный вывод сохранён для истории. Исправленное адресное заключение: `peer-followup-review.json`, SHA-256 `45418fc78d2190a12bdad9a224fbf97758f2f2feb7e81fd0ef61b9ec2ac58cf5`. Точный набор SHA и ограничений — `/tmp/prepared-java-concurrency-theory-followup-20261105.json`.

Никаких staging, commit или push в рамках этого пакета не выполнялось: индекс передан параллельной задаче MCQ. Перед следующим адресным коммитом необходимы повторная сверка HEAD, индекса и подготовленных SHA.
