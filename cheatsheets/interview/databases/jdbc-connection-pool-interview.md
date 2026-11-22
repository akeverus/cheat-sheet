---
title: "Вопросы на собеседовании: JDBC и пулы соединений"
description: "DataSource, жизненный цикл JDBC-ресурсов, транзакции, batch, потоковое чтение и диагностика HikariCP"
tags:
  - interview
  - databases
  - jdbc-connection-pool-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "JDBC interview"
  - "JDBC и connection pool"
prerequisites:
  - "[[sql-interview]]"
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: JDBC и пулы соединений

Ответы опираются на **Java SE 25 JDBC API** и документацию HikariCP, проверенные 2026-11-05. JDBC задаёт контракты; детали курсора, batch и fetch зависят от драйвера и СУБД. Общий SQL, ORM и Spring transaction propagation разобраны отдельно.

## Содержание

- [Q1. Зачем DataSource вместо DriverManager и что добавляет пул?](#q1-зачем-datasource-вместо-drivermanager-и-что-добавляет-пул)
- [Q2. Как закрывать Connection, Statement и ResultSet?](#q2-как-закрывать-connection-statement-и-resultset)
- [Q3. Как управлять autocommit и откатом без потери соединения?](#q3-как-управлять-autocommit-и-откатом-без-потери-соединения)
- [Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?](#q4-что-даёт-preparedstatement-batch-и-что-делать-при-частичном-сбое)
- [Q5. Как читать большой ResultSet без предположения о настоящем streaming?](#q5-как-читать-большой-resultset-без-предположения-о-настоящем-streaming)
- [Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?](#q6-как-отличить-исчерпание-пула-hikaricp-от-потерянного-соединения)
- [See also](#see-also)

## Q1. Зачем DataSource вместо DriverManager и что добавляет пул?

`DataSource` — фабрика `Connection`, рекомендованная JDBC как основной способ получения соединений. Реализация может быть простой, с пулом или с поддержкой распределённых транзакций; сам интерфейс **не гарантирует**, что пул есть. `DriverManager` полезен для небольших примеров, но в сервисе `DataSource` отделяет код запросов от конфигурации подключения и выбранной реализации.

Пул переиспользует физические соединения. Вызов `close()` на выданном пулом логическом `Connection` возвращает его под управление пула; приложение обязано своевременно освобождать его после операции, а не держать в singleton. Границы транзакции задаёт вызывающий код или transaction manager, а не факт наличия `DataSource`. [DataSource API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html), [HikariCP README](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby).

**Сценарий с состоянием сессии.** В PostgreSQL приложение выполняет `SET SESSION statement_timeout = '200ms'`, фиксирует транзакцию и возвращает соединение в HikariCP. Если пул выдаст ту же физическую сессию другому запросу, её таймаут может остаться 200 мс. Для ограничения одной явной транзакции используйте `SET LOCAL statement_timeout = '200ms'` перед её запросами. После commit или rollback снова действует настройка сессии. Уровень изоляции в самостоятельном JDBC-коде задавайте через `Connection.setTransactionIsolation()` до начала транзакции, чтобы HikariCP мог отследить изменение и восстановить исходный уровень при возврате соединения. [PostgreSQL 19 — SET](https://www.postgresql.org/docs/19/sql-set.html) · [HikariCP FAQ](https://github.com/brettwooldridge/HikariCP/wiki/FAQ).

**Когда выполняется инициализация.** В HikariCP `connectionInitSql` выполняется при создании каждого физического соединения, перед его добавлением в пул. Допустим, он задаёт `statement_timeout = '2s'`, а отчёт меняет таймаут сессии на 30 секунд и фиксирует транзакцию. Следующий запрос, получивший ту же сессию, может унаследовать 30 секунд. Возврат и повторная выдача соединения не запускают `connectionInitSql` заново. Для временного лимита отчёта подойдёт `SET LOCAL statement_timeout = '30s'` в явной транзакции. После её завершения снова действует прежняя настройка сессии. [HikariCP — connectionInitSql](https://github.com/brettwooldridge/HikariCP#infrequently-used) · [PostgreSQL 19 — SET](https://www.postgresql.org/docs/19/sql-set.html).

**Доступ к API драйвера.** Сервис на pgJDBC хочет записать в лог PID серверной сессии для сопоставления с `pg_stat_activity`. `DataSource` может выдать прокси пула, поэтому используйте JDBC `unwrap(PGConnection.class)` для получения интерфейса драйвера.

```java
try (Connection connection = dataSource.getConnection()) {
    int backendPid = connection.unwrap(org.postgresql.PGConnection.class).getBackendPID();
    log.debug("PostgreSQL backend PID: {}", backendPid);
}
```

Если такой интерфейс доступен только в части конфигураций, проверьте `isWrapperFor(PGConnection.class)`. `unwrap` может вернуть сам делегат или прокси. В HikariCP 7.0.2 при совпадении типа возвращается делегат, поэтому доступ к нему действует в пределах выдачи логического соединения. Закрывайте выданный пулом `Connection` и завершайте работу с интерфейсом драйвера до этого закрытия. Стандартные JDBC-операции выполняйте через выданный `Connection`. Например, прямой вызов `setTransactionIsolation()` на делегате обходит учёт изменения в HikariCP и может оставить новый уровень изоляции следующему заёмщику. [JDBC Wrapper](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Wrapper.html) · [pgJDBC PGConnection](https://jdbc.postgresql.org/documentation/publicapi/org/postgresql/PGConnection.html#getBackendPID()) · [HikariCP 7.0.2 — ProxyConnection](https://github.com/brettwooldridge/HikariCP/blob/HikariCP-7.0.2/src/main/java/com/zaxxer/hikari/pool/ProxyConnection.java).

**Режим чтения и выбор сервера.** Пул HikariCP подключается по URL к primary PostgreSQL и настроен с `readOnly=true`. При стандартном `readOnlyMode=transaction` у pgJDBC, если приложение отключило auto-commit до первого оператора, драйвер начинает транзакцию через `BEGIN READ ONLY`. Обычный `INSERT` в постоянную таблицу завершится ошибкой уже на primary. При включённом auto-commit тот же режим pgJDBC сам по себе не включает серверное ограничение. В режиме `always` драйвер применяет режим чтения и при auto-commit, а в режиме `ignore` флаг не влияет на серверный режим. Маршрут задаётся при выборе источника соединений: например, приложение может направлять запросы чтения в отдельный пул с URL реплики, а записи — в пул primary. [HikariCP — readOnly](https://github.com/brettwooldridge/HikariCP#infrequently-used) · [pgJDBC — параметры подключения](https://jdbc.postgresql.org/documentation/use/#connection-parameters) · [PostgreSQL 19 — SET TRANSACTION](https://www.postgresql.org/docs/19/sql-set-transaction.html).

## Q2. Как закрывать Connection, Statement и ResultSet?

`Connection`, `Statement` и `ResultSet` — ресурсы с `close()`. В самостоятельном JDBC-коде используйте `try-with-resources` и ограничивайте их время жизни: результат закрывайте до statement, statement до connection. Контракт JDBC автоматически закрывает `ResultSet`, когда породивший его `Statement` закрывается или повторно исполняется. Явное закрытие результата освобождает ресурс сразу и делает границу чтения очевидной; на GC полагаться нельзя.

```java
try (Connection connection = dataSource.getConnection();
     PreparedStatement statement = connection.prepareStatement(
         "SELECT id FROM orders WHERE customer_id = ?")) {
    statement.setLong(1, customerId);
    try (ResultSet rows = statement.executeQuery()) {
        while (rows.next()) {
            consume(rows.getLong("id"));
        }
    }
}
```

Параметры `PreparedStatement` связываются через `setXxx`, а не конкатенацией данных в SQL. Пример выше — для собственного JDBC-кода. В Spring-managed транзакции предпочтите `JdbcTemplate`; если используете `DataSourceUtils.getConnection()`, освобождайте его через `DataSourceUtils.releaseConnection(connection, dataSource)`: эта пара учитывает привязку к транзакции. [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html), [ResultSet API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html), [Spring `DataSourceUtils`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceUtils.html).

**Ошибка при чтении и закрытии.** В примере выше `rows.next()` выбрасывает `SQLException`, а при выходе из блока `rows.close()` выбрасывает другое исключение. Наружу выходит ошибка чтения, а ошибку закрытия можно увидеть через `getSuppressed()`. Java всё равно попытается закрыть `statement` и `connection`. Если чтение прошло успешно, наружу выйдет первая ошибка закрытия, а последующие ошибки закрытия будут добавлены к ней как подавленные. Для диагностики передавайте в лог сам объект исключения. У JDBC есть также цепочка дополнительных ошибок через `SQLException.getNextException()`, которую драйвер может заполнить независимо от подавленных исключений. [JLS §14.20.3](https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.20.3) · [JDBC SQLException](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/SQLException.html#getNextException()).

**SQL NULL при чтении результата.** В примере выше `id` — идентификатор заказа с ограничением `NOT NULL`. Если добавить в выборку nullable-столбец `discount_cents`, то `getLong("discount_cents")` вернёт `0` и для скидки в ноль центов, и для SQL `NULL`. Проверьте `wasNull()` сразу после чтения этого столбца, до вызова следующего геттера: он относится к последнему прочитанному значению.

```java
long value = rows.getLong("discount_cents");
Long discountCents = rows.wasNull() ? null : Long.valueOf(value);
```

Так маппер сохраняет различие между отсутствующей скидкой и явно указанным нулём. [JDBC ResultSet.getLong](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html#getLong(java.lang.String)), [ResultSet.wasNull](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html#wasNull()).

**Большие объекты.** Если в явной транзакции прочитать вложение через `rows.getBlob("attachment")`, закрытие `ResultSet` освободит ресурсы набора строк, а полученный `Blob` останется действительным как минимум до конца транзакции, если ранее не вызван `free()`. То же правило действует для `Clob` и `NClob`. Обработайте данные в пределах транзакции, закройте открытый для чтения поток и вызовите `free()` для ненулевого объекта в блоке `finally`. Если данные нужны вызывающему коду после возврата соединения в пул, заранее скопируйте их в хранилище с подходящим временем жизни, например в файл. [ResultSet.close](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html#close()), [Blob.free](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Blob.html#free()).

**Сценарий со Spring.** Сервис открыл локальную JDBC-транзакцию через `DataSourceTransactionManager`. Запись через `JdbcTemplate` использует привязанное к транзакции соединение. Если второй DAO вызовет `getConnection()` напрямую на обычном HikariCP `DataSource`, он получит отдельное соединение. При включённом на нём auto-commit успешная запись второго DAO сохранится даже после отката транзакции сервиса. Используйте `JdbcTemplate` для того же источника либо получайте и освобождайте соединение через `DataSourceUtils` для источника, которым управляет менеджер. Для существующего DAO с обычным JDBC можно передать `TransactionAwareDataSourceProxy`. Через него вызовы `getConnection()` и `close()` учитывают границы Spring-транзакции. [DataSourceTransactionManager](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jdbc/datasource/DataSourceTransactionManager.html), [TransactionAwareDataSourceProxy](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jdbc/datasource/TransactionAwareDataSourceProxy.html).


**Пример с несколькими результатами.** Процедура на поддерживающей это СУБД возвращает сначала счётчик обновления, затем строки отчёта. Если `CallableStatement.execute()` вернул `false`, проверьте `getUpdateCount()`. Значение `0` означает, что команда не изменила строк, и после неё ещё могут идти результаты. Если получен счётчик, переходите к следующему результату через `getMoreResults()`. При `true` прочитайте набор через `getResultSet()` и закройте его. Обход заканчивается, когда текущий результат представлен `false` и `getUpdateCount()` вернул `-1`. Вызов `getMoreResults()` без аргументов также закрывает текущий набор строк. [JDBC Statement — getMoreResults](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#getMoreResults()).

## Q3. Как управлять autocommit и откатом без потери соединения?

JDBC `Connection` по умолчанию в auto-commit: каждое завершённое SQL-выражение фиксируется отдельно. Пул может настроить другое начальное состояние, поэтому для собственного кода проверяйте контракт полученного соединения. Для атомарной группы отключите auto-commit, выполните операции и явно вызовите `commit()`; при ошибке попробуйте `rollback()` и сохраните исходное исключение. Возврат connection в пул без явного завершения транзакции — плохой контракт: поведение `close()` при активной транзакции зависит от реализации.

По контракту JDBC момент завершения зависит от команды. Для обычного `UPDATE` без возвращаемого набора строк это конец исполнения. Для `SELECT` контракт связывает завершение с закрытием `ResultSet`. У вызова с несколькими результатами нужно закрыть все наборы строк и получить все счётчики обновления и выходные параметры, если они есть. Возврат из `executeQuery()` сам по себе не даёт переносимой гарантии о моменте фиксации. Буферизация и момент, когда сервер закончил работу, зависят от драйвера. Например, положительный `fetchSize` при включённом auto-commit не включает серверный курсор pgJDBC. [JDBC `setAutoCommit`](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html#setAutoCommit(boolean)), [pgJDBC — cursor-based results](https://jdbc.postgresql.org/documentation/query/#getting-results-based-on-a-cursor).

```java
try (Connection connection = dataSource.getConnection()) {
    connection.setAutoCommit(false);
    try {
        debit(connection, fromId, amount);
        credit(connection, toId, amount);
        connection.commit();
    } catch (SQLException | RuntimeException | Error failure) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            failure.addSuppressed(rollbackFailure);
        }
        throw failure;
    }
}
```

Пример пытается откатить собственную транзакцию и при `Error`, затем пробрасывает исходный сбой. Если `rollback()` сам выбросил `SQLException`, она сохраняется как suppressed у исходного исключения.

При переводе соединения из ручного режима в auto-commit JDBC фиксирует активную транзакцию. Например, если списание прошло, зачисление завершилось ошибкой и откат тоже не удался, безусловный вызов `setAutoCommit(true)` в `finally` может зафиксировать списание, если транзакция осталась активной и соединение принимает команды. Восстанавливайте режим только после подтверждённого завершения транзакции. При сбое отката используйте предусмотренный пулом способ вывести соединение из обращения. [Connection.setAutoCommit](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html#setAutoCommit(boolean)).

Если транзакцией управляет Spring/JTA, не вмешивайтесь вручную в auto-commit или commit взятого им соединения. Если сбой произошёл во время самого `commit()`, попытка `rollback()` не доказывает, что запись не была зафиксирована; перед повтором операции выясните исход. Для собственной JDBC-операции проверьте поддерживаемые isolation и savepoint у драйвера. [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html).

**Пример.** Сервис списывает средства с внутреннего счёта. JDBC-соединение оборвалось во время `commit()`, когда ответ сервера ещё не был получен. Списание могло уже зафиксироваться. Чтобы повторить запрос на новом соединении, клиент передаёт тот же идентификатор операции. В PostgreSQL при `READ COMMITTED` сервис сначала выполняет `INSERT … ON CONFLICT (operation_id) DO NOTHING RETURNING operation_id` в таблице операций с уникальным ключом. Если вставка вернула строку, сервис выполняет списание и сохраняет результат в той же транзакции. Если строка не вернулась, отдельный `SELECT` в этой транзакции получает сохранённый результат предыдущей попытки. Нужно сверить параметры операции, прежде чем вернуть этот результат. Отдельный запрос важен: конкурентный commit может вызвать конфликт вставки, хотя победившая строка ещё недоступна в снимке самого `INSERT`.

Храните запись операции минимум в течение окна возможных повторов. Эта схема защищает списание в той же БД. Внешнему платёжному сервису нужен собственный контракт идемпотентности. [PostgreSQL 19 — INSERT](https://www.postgresql.org/docs/19/sql-insert.html), [PostgreSQL 19 — Read Committed](https://www.postgresql.org/docs/19/transaction-iso.html#XACT-READ-COMMITTED).

**Сценарий с точкой сохранения.** В PostgreSQL 19 импорт в одной JDBC-транзакции уже записал строку A. Перед записью B приложение вызывает `Savepoint rowStart = connection.setSavepoint()`. Вставка B нарушает уникальность и выбрасывает `SQLException` с SQLSTATE `23505`. Если политика импорта разрешает пропустить именно этот конфликт, приложение вызывает `connection.rollback(rowStart)` и после успешного отката может обработать C. Простого перехвата исключения недостаточно: PostgreSQL оставляет транзакцию в состоянии ошибки до отката к точке или полного отката. При успешном общем `commit()` сохранятся A и C. До него запись A всё ещё можно откатить вместе со всей транзакцией. Освободите точку через `releaseSavepoint(rowStart)`, когда она больше не нужна. При потере соединения или ошибке самого отката обработку на текущей транзакции продолжать нельзя. [JDBC Connection](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html#setSavepoint()) · [PostgreSQL 19 — ROLLBACK TO SAVEPOINT](https://www.postgresql.org/docs/19/sql-rollback-to.html).

## Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?

`PreparedStatement` связывает данные отдельно от SQL и может накопить набор параметров через `addBatch()`, затем отправить их `executeBatch()`. Это может сократить число round trips, но не означает одну атомарную SQL-операцию и не гарантирует одинаковый способ исполнения у драйверов. Делите огромный batch на разумные порции с учётом памяти, транзакции и лимитов сервера.

При `BatchUpdateException` проверьте `getUpdateCounts()`: драйвер может остановиться на первом сбое или продолжить после него. Массив содержит результат для выполненных до сбоя команд или для всего набора, если драйвер продолжил работу; `Statement.SUCCESS_NO_INFO` означает успех без числа изменённых строк, `Statement.EXECUTE_FAILED` — сбой команды. Для `executeLargeBatch()` используйте `getLargeUpdateCounts()`, чтобы избежать переполнения `int`. Исключение само по себе не доказывает откат всего batch: исход зависит от auto-commit и транзакции. При необходимости атомарности задайте явную транзакцию, обработайте rollback и решайте вопрос повтора по установленному исходу. [PreparedStatement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html), [BatchUpdateException API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html).

**Пример.** Импорт добавил в batch три `INSERT` в порядке A, B, C. Драйвер остановился на ошибке уникальности при B и вернул в исключении массив `[1]` через `getUpdateCounts()`. По позиции видно, что A выполнилась и затронула одну строку. Если все три вставки выполнялись на одном соединении в явной транзакции и приложение успешно вызвало `rollback()`, строка A тоже отменена. При включённом auto-commit A могла уже зафиксироваться. Перед повтором проверьте результат по стабильным идентификаторам импорта или используйте идемпотентную запись с уникальным ключом операции.

**Перезапись batch в Connector/J.** При `rewriteBatchedStatements=true` драйвер может объединить совместимые вставки в один `INSERT` с несколькими наборами `VALUES`. Если так исполнен batch из `INSERT ... ON DUPLICATE KEY UPDATE`, сервер возвращает один суммарный счётчик на объединённую команду. При положительном суммарном счётчике драйвер возвращает `SUCCESS_NO_INFO` для каждой исходной позиции этой команды, при нуле — `0`. Например, три `SUCCESS_NO_INFO` не позволяют сказать, сколько записей вставлено и сколько обновлено. Для сверки результата используйте ключи импорта. Границу фиксации по-прежнему задаёт транзакция. [MySQL Connector/J — rewriteBatchedStatements](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).


**Два upsert одного ключа.** В PostgreSQL 19 в таблице цен уже есть товар с ключом `sku = 'A'`. Две отдельные команды `INSERT … ON CONFLICT (sku) DO UPDATE SET price = EXCLUDED.price` могут обновить его последовательно до 100 и затем до 120. Если объединить обе входные строки в один многорядный `INSERT` с тем же `DO UPDATE`, команда завершится ошибкой нарушения кардинальности: в пределах одной команды PostgreSQL запрещает повторно затронуть одну и ту же строку. Для импорта, где важна только последняя цена, до многорядной вставки выберите одну запись на ключ по порядку входных событий. Если важно каждое обновление, выполняйте отдельные команды и проверьте, что режим batch драйвера сохраняет эту границу. [PostgreSQL 19 — INSERT](https://www.postgresql.org/docs/19/sql-insert.html#SQL-ON-CONFLICT).

**Счётчики batch и сгенерированные ключи.** В импорте у записей A, B и C есть уникальные входные идентификаторы, а первичные ключи выдаёт БД. Массив из `executeBatch()` описывает выполнение команд. Даже результат `[1, 1, 1]` сообщает только число затронутых строк. Для получения ключей в JDBC создайте `PreparedStatement` с `Statement.RETURN_GENERATED_KEYS` и прочитайте отдельный `ResultSet` через `getGeneratedKeys()`. Поддержку для выбранного SQL и режима batch проверяйте на используемом драйвере. Например, Connector/J документирует получение ключей для перезаписанного batch только тогда, когда весь batch состоит из команд `INSERT` или `REPLACE`. [JDBC Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#getGeneratedKeys()) · [Connector/J — rewriteBatchedStatements](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).

Если импорт должен надёжно сопоставить входные записи с ключами, в PostgreSQL 19 можно явно выполнить один многорядный `INSERT ... RETURNING import_key, id` и прочитать пары из результата команды. При уникальном и неизменном `import_key` соответствие строится по нему, независимо от порядка строк результата. Полученный `id` доступен до фиксации явной транзакции. При успешном rollback вставка отменяется, поэтому публиковать результат импорта как зафиксированный следует после подтверждения commit. [PostgreSQL 19 — RETURNING](https://www.postgresql.org/docs/19/dml-returning.html).


## Q5. Как читать большой ResultSet без предположения о настоящем streaming?

Цикл `while (rows.next())` обрабатывает строки по одной в приложении, но **не доказывает**, что драйвер не загрузил весь результат в память. `Statement.setFetchSize(n)` — подсказка JDBC, её реальное действие зависит от драйвера, режима курсора, транзакции и настроек соединения. Например, pgJDBC по умолчанию собирает весь результат; для курсорного чтения нужны положительный fetch size, выключенный auto-commit, forward-only `ResultSet` и один SQL statement в запросе, без нескольких команд через точку с запятой. Connector/J предлагает другие режимы, включая `useCursorFetch` с положительным fetch size. Проверяйте документацию своего драйвера и измеряйте память, сеть и время удержания соединения.

Держите `ResultSet` и его `Statement` открытыми только пока читаете данные. Долгий экспорт занимает connection из пула, а медленный downstream может задержать транзакцию; для больших выгрузок иногда лучше keyset pagination или отдельный поток экспорта. При выборке нескольких страниц нужен стабильный порядок и понимание изменений данных между запросами. [Statement.setFetchSize](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)), [pgJDBC cursor mode](https://jdbc.postgresql.org/documentation/query/), [Connector/J result sets](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).

**Потоковое чтение в MySQL Connector/J.** По умолчанию драйвер буферизует весь результат. У него есть два режима чтения больших выборок. Для получения порций включите `useCursorFetch=true` и задайте положительный `fetchSize`, например 500. Для построчного режима создайте оператор с `TYPE_FORWARD_ONLY` и `CONCUR_READ_ONLY` и установите `setFetchSize(Integer.MIN_VALUE)`. Это специальное значение Connector/J. В построчном режиме, если во время обхода CSV-экспорта запустить запрос за метаданными на том же соединении, драйвер выбросит исключение при стандартных настройках. Прочитайте метаданные до начала экспорта либо выполните следующий запрос после полного чтения или закрытия потокового результата. Если нужен второй запрос прямо во время обхода, отдельное соединение потребует ещё одного слота пула и имеет собственный транзакционный контекст. [Connector/J — ResultSet](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-implementation-notes.html) · [Connector/J — useCursorFetch](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).

**Можно ли зафиксировать транзакцию в середине чтения?** В JDBC это зависит от режима удержания результата (holdability). При `CLOSE_CURSORS_AT_COMMIT` открытый результат закрывается на `commit()`. При поддерживаемом `HOLD_CURSORS_OVER_COMMIT` результат остаётся открытым, но JDBC не обещает способ хранения строк. Например, в pgJDBC 42.7.8 выбор `HOLD_CURSORS_OVER_COMMIT` отключает курсорный путь даже с положительным `fetchSize`, выключенным auto-commit и forward-only результатом. Поэтому фиксация после каждой порции не сохраняет свойства потокового экспорта. Для курсорного чтения завершайте транзакцию после чтения и закрытия результата. Если допустимы отдельные снимки на страницы, используйте короткие транзакции с keyset-пагинацией. [JDBC ResultSet, holdability](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html#HOLD_CURSORS_OVER_COMMIT) · [Исходник pgJDBC 42.7.8](https://github.com/pgjdbc/pgjdbc/blob/REL42.7.8/pgjdbc/src/main/java/org/postgresql/jdbc/PgStatement.java#L421-L425).

**Пример с медленным получателем.** Экспорт из PostgreSQL читает данные курсором и сразу пишет CSV в HTTP-ответ. Если клиент читает медленно, приложение дольше держит соединение и транзакцию даже при небольшом `fetchSize`. Пока старый снимок нужен курсору, он может задерживать очистку версий строк, которые остаются видимыми этому снимку. Сопоставьте ожидание в пуле с возрастом транзакции (`pg_stat_activity.xact_start`) и горизонтом `backend_xmin` экспортной сессии. Если нужен единый снимок, можно формировать файл отдельным заданием и закрыть транзакцию до скачивания. Это убирает зависимость её длительности от клиента, но чтение в задании по-прежнему занимает соединение. Если допустима видимость изменений между страницами, keyset-выборка с короткой транзакцией на страницу сокращает время удержания каждого снимка. [PostgreSQL 18: pg_stat_activity](https://www.postgresql.org/docs/18/monitoring-stats.html#MONITORING-PG-STAT-ACTIVITY-VIEW) · [PostgreSQL 18: таймауты сессии](https://www.postgresql.org/docs/18/runtime-config-client.html#GUC-IDLE-IN-TRANSACTION-SESSION-TIMEOUT).

**Таймаут между порциями.** В PostgreSQL 19 экспорт читает результат курсором в открытой транзакции. После выдачи порции сервер ждёт следующей команды, а приложение 40 секунд пишет уже полученные строки в CSV. При `idle_in_transaction_session_timeout = '30s'` сервер завершит сессию в этой паузе, если раньше не сработает другой лимит. Приложение может продолжить обработку полученных строк и обнаружить разрыв при следующем обращении к серверу. Если часть CSV уже отправлена клиенту, повтор с начала нельзя дописывать в тот же ответ. `statement_timeout` ограничивает выполнение команды на сервере, а `transaction_timeout` ограничивает длительность транзакции и при срабатывании также завершает сессию. Общий дедлайн выгрузки включает и работу приложения, поэтому его контролируют отдельно. [PostgreSQL 19 — таймауты сессии](https://www.postgresql.org/docs/19/runtime-config-client.html#GUC-IDLE-IN-TRANSACTION-SESSION-TIMEOUT).

## Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?

Таймаут `getConnection()` означает, что HikariCP не выдал connection за `connectionTimeout`; это другой симптом, чем ошибка связи во время SQL. Причина не обязательно в занятом пуле: новое физическое соединение могло не создаться из-за БД или сети. Сопоставьте active, idle, total и pending, ошибки создания, время ожидания, длительность транзакций и состояние БД. Когда total достиг `maximumPoolSize`, active заняты и pending растёт, ищите длинные запросы, удержание или нехватку ёмкости. Если во время повторяющихся таймаутов `total` устойчиво ниже лимита, проверьте длительность и ошибки создания физических соединений. На старте или при всплеске трафика рост пула может ещё идти. Увеличение размера без анализа может перегрузить БД.

**Как читать метрики.** Каждый счётчик JMX снимается отдельно. Например, при `maximumPoolSize=20` один опрос может показать `active=20`, `idle=1` и `total=20`, если между чтениями вернулось соединение. По такому опросу нельзя установить, был ли свободный слот в момент таймаута. Сопоставьте динамику по одному пулу за интервал инцидента с временем ожидания и журналом ошибок. Устойчивое сочетание `active≈total≈maximumPoolSize`, `idle≈0` и очереди ожидающих указывает на насыщение. Устойчиво низкий `total` вместе с ошибками подключения указывает на проблему пополнения пула. [HikariCP — мониторинг JMX](https://github.com/brettwooldridge/HikariCP/wiki/MBean-%28JMX%29-Monitoring-and-Management).


`maxLifetime` задаёт срок плановой ротации физического соединения. По истечении срока HikariCP удаляет занятое соединение после возврата в пул. Например, если экспорт из Q5 всё ещё читает строки в момент истечения `maxLifetime`, эта настройка сама по себе не прервёт чтение и не освободит слот для ожидающего запроса. `connectionTimeout` ограничивает ожидание выдачи соединения, а ожидание на уже полученном соединении ограничивают отдельно. HikariCP рекомендует выбирать `maxLifetime` короче внешних лимитов БД и сети. `keepaliveTime` проверяет idle connection и должен быть меньше `maxLifetime`; он не спасает уже потерянный выполняющийся запрос. `leakDetectionThreshold` сообщает о возможной утечке при долгом удержании, но не закрывает connection и не доказывает утечку. После простоя отличайте stale connection от pool exhaustion и сверяйте таймауты с драйвером и инфраструктурой. [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby), [HikariCP JMX](https://github.com/brettwooldridge/HikariCP/wiki/MBean-%28JMX%29-Monitoring-and-Management), [MySQL Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html).
**Сценарий с `REQUIRES_NEW`.** Все слоты пула заняты внешними транзакциями. Каждый запрос через Spring-прокси вызывает внутреннюю транзакцию `REQUIRES_NEW`. Для неё нужно отдельное соединение, а приостановленная внешняя транзакция удерживает своё. В этом случае `pending` растёт, а стеки ожидающих потоков указывают на получение соединения при входе во внутреннюю транзакцию. В журнале БД может не быть медленного запроса: внутренний SQL ещё не начал выполняться. Сопоставьте стеки с метриками пула и учтите глубину вложенности и остальную нагрузку перед изменением размера пула. [Spring — propagation](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/tx-propagation.html), [Транзакции БД, Q34](database-transactions-interview.md#q34-какие-проблемы-возникают-при-неправильном-управлении-соединениями-в-транзакциях).

**Таймаут выдачи, запроса и сети.** Если приложение получило соединение за 20 мс, а запрос затем ждёт блокировку, HikariCP `connectionTimeout` уже выполнил свою роль. JDBC `Statement.setQueryTimeout(2)` задаёт ожидание выполнения оператора в секундах. При превышении срока драйвер пытается отменить оператор. При сетевом сбое отмена может не дойти до сервера. Например, pgJDBC отправляет сигнал отмены через отдельное соединение. Параметр `cancelSignalTimeout` задаёт таймауты подключения и чтения для этого канала в секундах. Поэтому `setQueryTimeout(2)` сам по себе не гарантирует ни возврат управления через две секунды, ни остановку SQL к этому моменту. [pgJDBC — параметры подключения](https://jdbc.postgresql.org/documentation/use/#connection-parameters). JDBC `Connection.setNetworkTimeout(executor, 5000)` при поддержке драйвером ограничивает ожидание ответа от БД в миллисекундах. При его срабатывании драйвер помечает соединение закрытым. Для вызовов с несколькими обменами точная граница зависит от драйвера, поэтому 5000 мс не задают общий бюджет обработки запроса приложения. Даже если JDBC-соединение после отмены оператора остаётся пригодным, текущее состояние транзакции определяет СУБД. Например, PostgreSQL после ошибки отмены в явной транзакции требует отката транзакции или отката к ранее созданной точке сохранения. [JDBC Statement](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setQueryTimeout(int)) · [JDBC Connection](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html#setNetworkTimeout(java.util.concurrent.Executor,int)) · [PostgreSQL — транзакции](https://www.postgresql.org/docs/19/tutorial-transactions.html).

**Ошибка SQL после выдачи соединения.** В HikariCP 7.0.2 без переопределения политики исключение с SQLSTATE класса `08`, например `08006`, помечает соединение как сломанное. Пул выводит его из обращения и передаёт ошибку вызывающему коду. PostgreSQL `23505` означает нарушение уникальности и сам по себе такую реакцию не вызывает. В явной транзакции после `23505` всё равно нужен откат или возврат к точке сохранения из Q3. При диагностике проверьте и цепочку `SQLException.getNextException()`: HikariCP учитывает первые десять исключений, а также отдельные SQLSTATE, коды драйвера и настроенный `SQLExceptionOverride`. Замена соединения восстанавливает ёмкость пула. Решение о повторе операции принимает приложение с учётом возможного результата предыдущей попытки, особенно если ответ на `commit()` потерян. [HikariCP 7.0.2 — ProxyConnection](https://github.com/brettwooldridge/HikariCP/blob/HikariCP-7.0.2/src/main/java/com/zaxxer/hikari/pool/ProxyConnection.java) · [PostgreSQL — SQLSTATE](https://www.postgresql.org/docs/19/errcodes-appendix.html).

**Проверка соединения после простоя.** Если после сетевого сбоя `getConnection()` завершается по таймауту при числе активных соединений ниже `maximumPoolSize`, проверьте журнал пула и создание новых физических соединений. Пул мог обнаружить, что часть соединений перестала отвечать, и не успеть выдать рабочую замену. `validationTimeout` ограничивает проверку жизнеспособности соединения и по документации HikariCP должен быть меньше `connectionTimeout`. `connectionTimeout` относится к ожиданию выдачи из пула. Настройки подключения и чтения у драйвера действуют на сетевой обмен, а таймаут SQL задаётся отдельно. [HikariCP — параметры пула](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby).

---

## See also

- [SQL](sql-interview.md) Q45 — сравнение JDBC и JPA.
- [Транзакции БД](database-transactions-interview.md) Q33–Q34 — удержание соединения и Spring propagation.
- [MySQL](mysql-interview.md) Q6 — конкретный разрыв Connector/J.
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — репозитории и агрегаты поверх JDBC.
