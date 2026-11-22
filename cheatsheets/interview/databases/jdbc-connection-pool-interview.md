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

**Сценарий со Spring.** Сервис открыл локальную JDBC-транзакцию через `DataSourceTransactionManager`. Запись через `JdbcTemplate` использует привязанное к транзакции соединение. Если второй DAO вызовет `getConnection()` напрямую на обычном HikariCP `DataSource`, он получит отдельное соединение. При включённом на нём auto-commit успешная запись второго DAO сохранится даже после отката транзакции сервиса. Используйте `JdbcTemplate` для того же источника либо получайте и освобождайте соединение через `DataSourceUtils` для источника, которым управляет менеджер. Для существующего DAO с обычным JDBC можно передать `TransactionAwareDataSourceProxy`. Через него вызовы `getConnection()` и `close()` учитывают границы Spring-транзакции. [DataSourceTransactionManager](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jdbc/datasource/DataSourceTransactionManager.html), [TransactionAwareDataSourceProxy](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/jdbc/datasource/TransactionAwareDataSourceProxy.html).

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

## Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?

`PreparedStatement` связывает данные отдельно от SQL и может накопить набор параметров через `addBatch()`, затем отправить их `executeBatch()`. Это может сократить число round trips, но не означает одну атомарную SQL-операцию и не гарантирует одинаковый способ исполнения у драйверов. Делите огромный batch на разумные порции с учётом памяти, транзакции и лимитов сервера.

При `BatchUpdateException` проверьте `getUpdateCounts()`: драйвер может остановиться на первом сбое или продолжить после него. Массив содержит результат для выполненных до сбоя команд или для всего набора, если драйвер продолжил работу; `Statement.SUCCESS_NO_INFO` означает успех без числа изменённых строк, `Statement.EXECUTE_FAILED` — сбой команды. Для `executeLargeBatch()` используйте `getLargeUpdateCounts()`, чтобы избежать переполнения `int`. Исключение само по себе не доказывает откат всего batch: исход зависит от auto-commit и транзакции. При необходимости атомарности задайте явную транзакцию, обработайте rollback и решайте вопрос повтора по установленному исходу. [PreparedStatement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html), [BatchUpdateException API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html).

**Пример.** Импорт добавил в batch три `INSERT` в порядке A, B, C. Драйвер остановился на ошибке уникальности при B и вернул в исключении массив `[1]` через `getUpdateCounts()`. По позиции видно, что A выполнилась и затронула одну строку. Если все три вставки выполнялись на одном соединении в явной транзакции и приложение успешно вызвало `rollback()`, строка A тоже отменена. При включённом auto-commit A могла уже зафиксироваться. Перед повтором проверьте результат по стабильным идентификаторам импорта или используйте идемпотентную запись с уникальным ключом операции.

**Перезапись batch в Connector/J.** При `rewriteBatchedStatements=true` драйвер может объединить совместимые вставки в один `INSERT` с несколькими наборами `VALUES`. Если так исполнен batch из `INSERT ... ON DUPLICATE KEY UPDATE`, сервер возвращает один суммарный счётчик на объединённую команду. При положительном суммарном счётчике драйвер возвращает `SUCCESS_NO_INFO` для каждой исходной позиции этой команды, при нуле — `0`. Например, три `SUCCESS_NO_INFO` не позволяют сказать, сколько записей вставлено и сколько обновлено. Для сверки результата используйте ключи импорта. Границу фиксации по-прежнему задаёт транзакция. [MySQL Connector/J — rewriteBatchedStatements](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).


## Q5. Как читать большой ResultSet без предположения о настоящем streaming?

Цикл `while (rows.next())` обрабатывает строки по одной в приложении, но **не доказывает**, что драйвер не загрузил весь результат в память. `Statement.setFetchSize(n)` — подсказка JDBC, её реальное действие зависит от драйвера, режима курсора, транзакции и настроек соединения. Например, pgJDBC по умолчанию собирает весь результат; для курсорного чтения нужны положительный fetch size, выключенный auto-commit, forward-only `ResultSet` и один SQL statement в запросе, без нескольких команд через точку с запятой. Connector/J предлагает другие режимы, включая `useCursorFetch` с положительным fetch size. Проверяйте документацию своего драйвера и измеряйте память, сеть и время удержания соединения.

Держите `ResultSet` и его `Statement` открытыми только пока читаете данные. Долгий экспорт занимает connection из пула, а медленный downstream может задержать транзакцию; для больших выгрузок иногда лучше keyset pagination или отдельный поток экспорта. При выборке нескольких страниц нужен стабильный порядок и понимание изменений данных между запросами. [Statement.setFetchSize](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)), [pgJDBC cursor mode](https://jdbc.postgresql.org/documentation/query/), [Connector/J result sets](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).

**Пример с медленным получателем.** Экспорт из PostgreSQL читает данные курсором и сразу пишет CSV в HTTP-ответ. Если клиент читает медленно, приложение дольше держит соединение и транзакцию даже при небольшом `fetchSize`. Пока старый снимок нужен курсору, он может задерживать очистку версий строк, которые остаются видимыми этому снимку. Сопоставьте ожидание в пуле с возрастом транзакции (`pg_stat_activity.xact_start`) и горизонтом `backend_xmin` экспортной сессии. Если нужен единый снимок, можно формировать файл отдельным заданием и закрыть транзакцию до скачивания. Это убирает зависимость её длительности от клиента, но чтение в задании по-прежнему занимает соединение. Если допустима видимость изменений между страницами, keyset-выборка с короткой транзакцией на страницу сокращает время удержания каждого снимка. [PostgreSQL 18: pg_stat_activity](https://www.postgresql.org/docs/18/monitoring-stats.html#MONITORING-PG-STAT-ACTIVITY-VIEW) · [PostgreSQL 18: таймауты сессии](https://www.postgresql.org/docs/18/runtime-config-client.html#GUC-IDLE-IN-TRANSACTION-SESSION-TIMEOUT).

## Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?

Таймаут `getConnection()` означает, что HikariCP не выдал connection за `connectionTimeout`; это другой симптом, чем ошибка связи во время SQL. Причина не обязательно в занятом пуле: новое физическое соединение могло не создаться из-за БД или сети. Сопоставьте active, idle, total и pending, ошибки создания, время ожидания, длительность транзакций и состояние БД. Когда total достиг `maximumPoolSize`, active заняты и pending растёт, ищите длинные запросы, удержание или нехватку ёмкости. Когда total ниже лимита, ищите проблему создания соединений. Увеличение размера без анализа может перегрузить БД.

`maxLifetime` задаёт срок плановой ротации физического соединения. По истечении срока HikariCP удаляет занятое соединение после возврата в пул. Например, если экспорт из Q5 всё ещё читает строки в момент истечения `maxLifetime`, эта настройка сама по себе не прервёт чтение и не освободит слот для ожидающего запроса. `connectionTimeout` ограничивает ожидание выдачи соединения, а ожидание на уже полученном соединении ограничивают отдельно. HikariCP рекомендует выбирать `maxLifetime` короче внешних лимитов БД и сети. `keepaliveTime` проверяет idle connection и должен быть меньше `maxLifetime`; он не спасает уже потерянный выполняющийся запрос. `leakDetectionThreshold` сообщает о возможной утечке при долгом удержании, но не закрывает connection и не доказывает утечку. После простоя отличайте stale connection от pool exhaustion и сверяйте таймауты с драйвером и инфраструктурой. [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby), [HikariCP JMX](https://github.com/brettwooldridge/HikariCP/wiki/MBean-%28JMX%29-Monitoring-and-Management), [MySQL Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html).
**Сценарий с `REQUIRES_NEW`.** Все слоты пула заняты внешними транзакциями. Каждый запрос через Spring-прокси вызывает внутреннюю транзакцию `REQUIRES_NEW`. Для неё нужно отдельное соединение, а приостановленная внешняя транзакция удерживает своё. В этом случае `pending` растёт, а стеки ожидающих потоков указывают на получение соединения при входе во внутреннюю транзакцию. В журнале БД может не быть медленного запроса: внутренний SQL ещё не начал выполняться. Сопоставьте стеки с метриками пула и учтите глубину вложенности и остальную нагрузку перед изменением размера пула. [Spring — propagation](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/tx-propagation.html), [Транзакции БД, Q34](database-transactions-interview.md#q34-какие-проблемы-возникают-при-неправильном-управлении-соединениями-в-транзакциях).

**Таймаут выдачи, запроса и сети.** Если приложение получило соединение за 20 мс, а запрос затем ждёт блокировку, HikariCP `connectionTimeout` уже выполнил свою роль. JDBC `Statement.setQueryTimeout(2)` задаёт ожидание выполнения оператора в секундах. При превышении срока драйвер пытается отменить оператор. При сетевом сбое отмена может не дойти до сервера. Например, pgJDBC отправляет сигнал отмены через отдельное соединение. Параметр `cancelSignalTimeout` задаёт таймауты подключения и чтения для этого канала в секундах. Поэтому `setQueryTimeout(2)` сам по себе не гарантирует ни возврат управления через две секунды, ни остановку SQL к этому моменту. [pgJDBC — параметры подключения](https://jdbc.postgresql.org/documentation/use/#connection-parameters). JDBC `Connection.setNetworkTimeout(executor, 5000)` при поддержке драйвером ограничивает ожидание ответа от БД в миллисекундах. При его срабатывании драйвер помечает соединение закрытым. Для вызовов с несколькими обменами точная граница зависит от драйвера, поэтому 5000 мс не задают общий бюджет обработки запроса приложения. Даже если JDBC-соединение после отмены оператора остаётся пригодным, текущее состояние транзакции определяет СУБД. Например, PostgreSQL после ошибки отмены в явной транзакции требует отката транзакции или отката к ранее созданной точке сохранения. [JDBC Statement](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setQueryTimeout(int)) · [JDBC Connection](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html#setNetworkTimeout(java.util.concurrent.Executor,int)) · [PostgreSQL — транзакции](https://www.postgresql.org/docs/19/tutorial-transactions.html).

---

## See also

- [SQL](sql-interview.md) Q45 — сравнение JDBC и JPA.
- [Транзакции БД](database-transactions-interview.md) Q33–Q34 — удержание соединения и Spring propagation.
- [MySQL](mysql-interview.md) Q6 — конкретный разрыв Connector/J.
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — репозитории и агрегаты поверх JDBC.
