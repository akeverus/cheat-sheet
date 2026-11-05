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

## Q3. Как управлять autocommit и откатом без потери соединения?

JDBC `Connection` по умолчанию в auto-commit: каждое завершённое statement фиксируется отдельно. Пул может настроить другое начальное состояние, поэтому для собственного кода проверяйте контракт полученного соединения. Для атомарной группы отключите auto-commit, выполните операции и явно вызовите `commit()`; при ошибке попробуйте `rollback()` и сохраните исходное исключение. Возврат connection в пул без явного завершения транзакции — плохой контракт: поведение `close()` при активной транзакции зависит от реализации.

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

Если транзакцией управляет Spring/JTA, не вмешивайтесь вручную в auto-commit или commit взятого им соединения. Если сбой произошёл во время самого `commit()`, попытка `rollback()` не доказывает, что запись не была зафиксирована; перед повтором операции выясните исход. Для собственной JDBC-операции проверьте поддерживаемые isolation и savepoint у драйвера. [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html).

## Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?

`PreparedStatement` связывает данные отдельно от SQL и может накопить набор параметров через `addBatch()`, затем отправить их `executeBatch()`. Это может сократить число round trips, но не означает одну атомарную SQL-операцию и не гарантирует одинаковый способ исполнения у драйверов. Делите огромный batch на разумные порции с учётом памяти, транзакции и лимитов сервера.

При `BatchUpdateException` проверьте `getUpdateCounts()`: драйвер может остановиться на первом сбое или продолжить после него. Массив содержит результат для выполненных до сбоя команд или для всего набора, если драйвер продолжил работу; `Statement.SUCCESS_NO_INFO` означает успех без числа изменённых строк, `Statement.EXECUTE_FAILED` — сбой команды. Для `executeLargeBatch()` используйте `getLargeUpdateCounts()`, если число строк может не поместиться в `int`. Исключение само по себе не доказывает откат всего batch: исход зависит от auto-commit и транзакции. При необходимости атомарности задайте явную транзакцию, обработайте rollback и решайте вопрос повтора по установленному исходу. [PreparedStatement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html), [BatchUpdateException API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html).

## Q5. Как читать большой ResultSet без предположения о настоящем streaming?

Цикл `while (rows.next())` обрабатывает строки по одной в приложении, но **не доказывает**, что драйвер не загрузил весь результат в память. `Statement.setFetchSize(n)` — подсказка JDBC, её реальное действие зависит от драйвера, режима курсора, транзакции и настроек соединения. Например, pgJDBC по умолчанию собирает весь результат; для курсорного чтения нужны положительный fetch size, выключенный auto-commit, forward-only `ResultSet` и один SQL statement в запросе, без нескольких команд через точку с запятой. Connector/J предлагает другие режимы, включая `useCursorFetch` с положительным fetch size. Проверяйте документацию своего драйвера и измеряйте память, сеть и время удержания соединения.

Держите `ResultSet` и его `Statement` открытыми только пока читаете данные. Долгий экспорт занимает connection из пула, а медленный downstream может задержать транзакцию; для больших выгрузок иногда лучше keyset pagination или отдельный поток экспорта. При выборке нескольких страниц нужен стабильный порядок и понимание изменений данных между запросами. [Statement.setFetchSize](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)), [pgJDBC cursor mode](https://jdbc.postgresql.org/documentation/query/), [Connector/J result sets](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html).

## Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?

Таймаут `getConnection()` означает, что HikariCP не выдал connection за `connectionTimeout`; это другой симптом, чем ошибка связи во время SQL. Причина не обязательно в занятом пуле: новое физическое соединение могло не создаться из-за БД или сети. Сопоставьте active, idle, total и pending, ошибки создания, время ожидания, длительность транзакций и состояние БД. Когда total достиг `maximumPoolSize`, active заняты и pending растёт, ищите длинные запросы, удержание или нехватку ёмкости. Когда total ниже лимита, ищите проблему создания соединений. Увеличение размера без анализа может перегрузить БД.

`maxLifetime` ограничивает срок жизни физического connection в пуле; HikariCP рекомендует делать его короче лимитов БД и сети. `keepaliveTime` проверяет idle connection и должен быть меньше `maxLifetime`; он не спасает уже потерянный выполняющийся запрос. `leakDetectionThreshold` сообщает о возможной утечке при долгом удержании, но не закрывает connection и не доказывает утечку. После простоя отличайте stale connection от pool exhaustion и сверяйте таймауты с драйвером и инфраструктурой. [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby), [HikariCP JMX](https://github.com/brettwooldridge/HikariCP/wiki/MBean-%28JMX%29-Monitoring-and-Management), [MySQL Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html).

---

## See also

- [SQL](sql-interview.md) Q45 — сравнение JDBC и JPA.
- [Транзакции БД](database-transactions-interview.md) Q33–Q34 — удержание соединения и Spring propagation.
- [MySQL](mysql-interview.md) Q6 — конкретный разрыв Connector/J.
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — репозитории и агрегаты поверх JDBC.
