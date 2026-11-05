# JDBC theory peer review (Q1–Q6)

Дата: 2026-11-05. Ограниченный review теории и связи с paired MCQ, не MCQ FINAL. Прочитаны полностью теория Q1–Q6, paired production seed Q1–Q6, авторский отчёт MCQ, а также diff HEAD по теории и seed. Все исходные файлы, seed, индекс и HEAD оставлены без изменений.

Текущие SHA-256:

- Theory: 603a15225432d6e6bff08aa90e9ec923593d696b6ff1c2404a60fbf0a13e7d23
- Paired seed: be7349c8d0014caffcd5a19572b3e8da0e0930cf8cff8a8e46ae832905fcd2b8

Runtime-проверки с Java/JDBC, Spring, pgJDBC, HikariCP или живой БД не выполнялись.

## Итог

Теория в целом точная, с хорошими оговорками про контракт JDBC, детали драйвера, пул и неопределённый исход ошибки commit. Выявлены две узкие, практически значимые границы:

1. **Q3 — cleanup при Error.** Пример пытается rollback только для SQLException и RuntimeException. Если тело транзакции выбрасывает Error, соединение закрывается try-with-resources при активной транзакции; JDBC определяет результат такого close как implementation-defined. Требуется либо явно ограничить обещание указанными типами исключений, либо показать completion strategy с попыткой явного завершения транзакции и учётом сбоев очистки.
2. **Q5 — pgJDBC cursor restriction.** Перечислены forward-only, autocommit off и positive fetch size, но руководство pgJDBC дополнительно требует single SQL statement (не несколько statements, разделённых точкой с запятой), иначе cursor mode не применяется. Поскольку текст говорит, что эти настройки «нужны», это не буквально неверное условие достаточности, но примерный список требований неполон для точной эксплуатационной инструкции. Уточнить это ограничение.

Других подтверждённых дефектов по Q1, Q2, Q4, Q6 не нашёл. Статус после повторного просмотра: оба findings закрыты; ограниченный theory review **PASS**. Проверка выполнена по JDBC 25/pgJDBC контрактам. Авторская dynamic-proxy проверка транзакционного lifecycle выполнена под Java 22, но это не runtime-проверка драйвера, БД, HikariCP или Spring.

## Матрица вопросов

| Q | Теория и границы | MCQ sync |
|---|---|---|
| Q1 | PASS. DataSource — рекомендуемая абстракция получения соединения, но интерфейс сам не обещает pooling. Пул отдаёт логический Connection и close возвращает его пулу; граница транзакции отделена от DataSource. | Correct A совпадает с теорией; B–D — соответствующие заблуждения. |
| Q2 | PASS. try-with-resources корректно закрывает ResultSet, Statement, Connection в обратном порядке. JDBC Statement закрывает текущий ResultSet при close и при повторном execute. Пара Spring DataSourceUtils get/release учитывает thread-bound transaction; release не должен вручную закрывать привязанное Spring соединение. | Correct B ограничен собственным JDBC; distractors проверяют GC, неправильный lifecycle и ожидание от пула. Совпадает с теорией. |
| Q3 | PASS после правки. Catch охватывает SQLException, RuntimeException и Error, пытается сделать rollback, повторно выбрасывает исходный сбой и добавляет ошибку rollback как suppressed. Ошибка самого commit по-прежнему корректно обозначена как неизвестный outcome. | Correct D и distractors A–C синхронны с основным содержанием. MCQ остаётся согласован с основным транзакционным контрактом; seed не менялся. |
| Q4 | PASS. Batch не гарантирует атомарность. BatchUpdateException может содержать counts только выполненных до ошибки или полный массив при продолжении драйвера; EXECUTE_FAILED и SUCCESS_NO_INFO объяснены верно. getLargeUpdateCounts уместен после executeLargeBatch. Повтор зависит от установленного результата и транзакции. | Correct B и distractors A/C/D совпадают с теорией. |
| Q5 | PASS после правки. JDBC fetchSize — hint, не обещание streaming. pgJDBC cursor требует выключенный autocommit, положительный fetch size, forward-only ResultSet и один SQL statement без нескольких команд через `;`. Connector/J имеет отдельные настройки. Предупреждения о долгом удержании connection и пагинации уместны. | Correct C указывает на проверку драйвера и измерение, поэтому остаётся точным; wrong A/B/D соответствуют теории. |
| Q6 | PASS. Hikari connectionTimeout — ожидание получения Connection из пула, не SQL statement timeout. Диагностика отличает pool saturation от неудачного создания соединения. maxLifetime, keepaliveTime и leakDetectionThreshold описаны в основных границах, leak warning не выдаётся за доказательство/автоматическое закрытие. | Correct A соответствует теории; distractors B–D меняют местами timeout, насыщение и утечку. |

## Findings

### JDBC-THEORY-Q3-ERROR-ROLLBACK — незавершённая транзакция при Error

История: OPEN в первом проходе; CLOSED после точечной правки и проверки.

Первоначально в коде Q3 rollback вызывался только в catch(SQLException | RuntimeException). При Error (например, OutOfMemoryError или LinkageError) catch пропускается, try-with-resources вызывает close на Connection с активной транзакцией. JDBC 25 прямо говорит, что результат close при активной транзакции implementation-defined, и настоятельно рекомендует явно commit/rollback до close. Следовательно, пример не обеспечивает обещанный rollback для всех аварийных выходов.

Варианты исправления: сузить текст до SQLException/RuntimeException и отдельно обозначить Error как незакрытый путь; либо изменить пример на completion tracking/finally-based rollback, сохраняя исходное исключение и suppressed rollback failure. Не перехватывать Throwable механически без обоснования, учитывая ошибки VM.

### JDBC-THEORY-Q5-PGJDBC-SINGLE-STATEMENT — неполное условие курсорного чтения pgJDBC

История: OPEN в первом проходе; CLOSED после точечной правки и проверки.

Первоначально теория правильно называла положительный fetch size, autocommit off и forward-only ResultSet. Официальное руководство pgJDBC перечисляет ещё ограничение: курсорный запрос должен быть single statement, а не набором statements с разделителями

## Повторная проверка и закрытие findings

Перечитал обновлённые абзацы Q3/Q5 и сверил их с JDBC 25 Connection и руководством pgJDBC. Q3 catch теперь включает SQLException, RuntimeException и Error; при сбое он вызывает rollback, сохраняет SQLException от rollback через addSuppressed и повторно выбрасывает исходный объект ошибки. Это закрывает исходный gap по охвату Error: код пытается выполнить rollback перед close для этих категорий и сохраняет причины сбоев. Успешный откат не гарантируется, если rollback сам завершается ошибкой; после ошибки commit outcome остаётся неизвестным. Документация JDBC сохраняет caveat: если commit сам завершился исключением, результат операции может остаться неизвестным; текст его не маскирует.

Q5 теперь явно говорит, что pgJDBC cursor query должен состоять из одной SQL-команды без нескольких команд, разделённых `;`. Это соответствует официальному ограничению драйвера; finding закрыт.

Автор сообщил и предоставил harness /tmp/jdbc-theory-review-20261105/JdbcTransactionCheck.java: точный фрагмент Q3 скомпилирован и прогнан под Java 22 на JDBC dynamic proxies в семи сценариях (успешный путь, SQLException, RuntimeException, AssertionError, commit failure, suppressed rollback failure и suppressed close failure). Я прочитал harness; в этом review сам harness не запускал. Это проверяет авторский контроль потока с proxies, но не заменяет тест реального драйвера/БД/пула/Spring.

Текущие SHA-256 после правки:

- Theory: `603a15225432d6e6bff08aa90e9ec923593d696b6ff1c2404a60fbf0a13e7d23`
- Paired seed (не изменён): `be7349c8d0014caffcd5a19572b3e8da0e0930cf8cff8a8e46ae832905fcd2b8`
