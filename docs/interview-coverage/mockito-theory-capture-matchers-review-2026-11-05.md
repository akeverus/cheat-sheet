# Mockito Q17/Q18 — capture lifecycle и argument matchers

Дата: 2026-11-05. `interview-writer / SEMANTIC_UPDATE`, продолжение [Q19](mockito-theory-argthat-review-2026-11-05.md) и [Q39](mockito-theory-captor-review-2026-11-05.md). Прочитаны два полных ответа и все восемь MCQ-вариантов с sections. Исправлены только Q17/Q18; суммарно автором исправлены **19 из 45** ответов, остальные 26 этим набором не исправлялись. Это авторская проверка с открытыми ключами, не blind Phase A/B, independent acceptance или FINAL. JSON и индекс не редактировались.

Входная теория SHA `bcd78017c353f4ceb69c3366558138da5d8fddf0ae5535173fefa4d076115461`, выходная SHA **`2d7da2e1cfcf5b7b24507782a7a241b1a6867439bfe75e17577c99f0dc067af6`**. Seed SHA **`2cc16d5379c06898bc20e70d96fd289465ea6bb46591dd9e1e68a751b173c6ef`**; Q17/Q18 не менялись между начальным и итоговым снимком. Ключи D/D и текущие объяснения согласуются с исправлениями.

Q17 показывает полный пример с MockitoExtension, Mock и Captor, реальным Act, verification и отдельными assertions. Разделены регистрация matcher, состоявшийся захват, последнее значение и история. Уточнены инициализация поля, отсутствие deep snapshot и рекомендация verification вместо заявления о невозможности stubbing.

Q18 дополняет правило всех аргументов одного вызова и runtime-валидацию, placeholder, null-границы и различие элемента/массива varargs в исследованной версии. Eq отделён от same; для массивов отражено сравнение элементов. Три полных теста показывают stubbing и verification, null и 0/1/2 varargs.

Первичные источники **Mockito 5.11.0**: [Captor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/Captor.java), [ArgumentCaptor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentCaptor.java), [ArgumentMatchers](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentMatchers.java), [Equality](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/matchers/Equality.java). Основные границы дополнительно подтверждены исполнением, а не перенесены на все версии по названию метода.

**10 tests PASS / 0 failed**, javac/JVM exit 0 с первого запуска. Два полных Java-блока Markdown скопированы без изменений и дали **пять** успешных тестов. Ещё пять support probes проверили getValue до/после захвата, отсутствие snapshot у повторно переданного mutable-объекта, ожидаемый InvalidUseOfMatchersException при mixed raw/matcher, eq/same и arrays, исключение null-wrapper из anyInt. Неожиданных внутренних падений нет; ожидаемые исключения перехвачены assertions соответствующих probes.

Конфигурация: JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Agent указан только при старте дочерней JVM. Загрузки зависимостей, настройки приложений, full project/Gradle/CI, Spring, Kotlin, матрица других makers/JVM/версий, performance и concurrency — **NOT_RUN**.

Schema **PASS 45/45**, semantic **NOT_RUN**. Whole-topic linkage **FAIL: 8 errors / 21 warnings / 25 manual review**, exit 1; Q17/Q18 — **0 errors / 0 warnings**. Прежний Q17→Q39 исчез. Остальные общие ошибки не объявляются устранёнными, правила и пороги не менялись. Совокупные четыре подготовленных ответа Q17/Q18/Q19/Q39 требуют отдельного манифеста с текущей теорией; прежние манифесты сохранены как исторические снимки.

Структурная проверка: только Q17/Q18 изменены относительно входа этой группы; другие 43 ответа побайтно сохранены. Все 45 заголовков, ID, metadata, TOC и See also сохранены. По отношению к theory-коммиту da0c8e618151 подготовлены ровно Q17/Q18/Q19/Q39; ранее проверенные Java-блоки Q19/Q39 сохранены и повторно не запускались без изменения кода.

Артефакты `/tmp/mockito-theory-capture-matchers-20261105/`: before/seed-before/seed-postcheck, два тела, structural-check, primary-source-extracts, runtime с точными классами/runner и execution с SHA JAR/блоков, schema/linkage/diagnostic-summary, frozen-validation с точной парой и неизменённым linkage-скриптом. Веб-выдержки не являются полными byte snapshots.

Тема целиком **NEEDS_REPAIR**, FINAL **NOT_RUN**. Staging и коммиты не выполнялись; индекс находится у MCQ.

## Сверка при передаче индекса

После MCQ-коммита HEAD **`1f0f82c78dbb460ed88fe0078c4c36e9edb468df`** индекс передан теории. Пустой индекс и SHA четырёх подготовленных файлов проверены. Текущий seed целиком совпадает с прочитанным **2cc16d53**, все четыре per-Q SHA Q17/Q18/Q19/Q39 совпадают; повторное семантическое чтение неизменённых полей не требовалось. SHA всех четырёх полных Java-блоков совпадают с успешно исполненными. Результаты schema/linkage выше относятся к этой же точной паре.

Пакет коммита ограничен Mockito Markdown и тремя отчётами теории. Selenium и его прежний diff в пакет не включены. Состояние Git и точный снимок seed сохранены в `/tmp/mockito-theory-matchers-transfer-20261105/`, адресные файлы/SHA — в `/tmp/prepared-mockito-theory-matchers-20261105-v3.json`.
