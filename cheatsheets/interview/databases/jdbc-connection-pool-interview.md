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
updated: "2026-10-05"
---
# Вопросы на собеседовании: JDBC и пулы соединений

Ответы опираются на **Java SE 25 JDBC API** и опубликованную документацию HikariCP на 2026-10-05. JDBC задаёт контракты; детали курсора, batch и fetch зависят от драйвера и СУБД. Общий SQL, ORM и Spring transaction propagation разобраны отдельно.

## Содержание

- [Q1. Зачем DataSource вместо DriverManager и что добавляет пул?](#q1-зачем-datasource-вместо-drivermanager-и-что-добавляет-пул)
- [Q2. Как закрывать Connection, Statement и ResultSet?](#q2-как-закрывать-connection-statement-и-resultset)
- [Q3. Как управлять autocommit и откатом без потери соединения?](#q3-как-управлять-autocommit-и-откатом-без-потери-соединения)
- [Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?](#q4-что-даёт-preparedstatement-batch-и-что-делать-при-частичном-сбое)
- [Q5. Как читать большой ResultSet без предположения о настоящем streaming?](#q5-как-читать-большой-resultset-без-предположения-о-настоящем-streaming)
- [Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?](#q6-как-отличить-исчерпание-пула-hikaricp-от-потерянного-соединения)
- [See also](#see-also)

## Q1. Зачем DataSource вместо DriverManager и что добавляет пул?

`DataSource` — фабрика `Connection`, рекомендованная JDBC как основной способ получения соединений. Реализация может быть простой, с пулом или с поддержкой распределённых транзакций; сам интерфейс **не гарантирует**, что пул есть. `DriverManager` полезен для небольших примеров, но в сервисе `DataSource` отделяет код запросов от способа подключения и управления ресурсами.

Пул переиспользует физические соединения. Вызов `close()` на выданном пулом логическом `Connection` возвращает его под управление пула; приложение обязано закрывать его после операции, а не держать в singleton. Границы транзакции при этом определяет вызывающий код или transaction manager, а не факт наличия `DataSource`. [DataSource API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html), [HikariCP README](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby).

## Q2. Как закрывать Connection, Statement и ResultSet?

`Connection`, `Statement` и `ResultSet` — ресурсы с `close()`. Используйте `try-with-resources`; закрывайте результат до statement, statement до connection. Не полагайтесь на GC или только на каскадное закрытие родителя: оно может зависеть от реализации и затрудняет диагностику. После закрытия connection доступ к незавершённому результату уже не является безопасным контрактом.

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

Параметры `PreparedStatement` связываются через `setXxx`, а не конкатенацией данных в SQL. В Spring-managed транзакции не закрывайте вручную connection, выданный и привязанный менеджером транзакций; пример выше — для собственного JDBC-кода. [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html), [ResultSet API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html).

## Q3. Как управлять autocommit и откатом без потери соединения?

JDBC `Connection` по умолчанию в auto-commit: каждое завершённое statement фиксируется отдельно. Для атомарной группы отключите auto-commit, выполните операции и явно вызовите `commit()`; при ошибке попробуйте `rollback()` и сохраните исходное исключение. Возврат connection в пул без явного завершения транзакции — плохой контракт: поведение при `close()` зависит от реализации и состояния соединения.

```java
try (Connection connection = dataSource.getConnection()) {
    connection.setAutoCommit(false);
    try {
        debit(connection, fromId, amount);
        credit(connection, toId, amount);
        connection.commit();
    } catch (SQLException | RuntimeException failure) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            failure.addSuppressed(rollbackFailure);
        }
        throw failure;
    }
}
```

Если транзакцией управляет Spring/JTA, не вмешивайтесь вручную в auto-commit или commit взятого им соединения. Для собственной JDBC-операции проверьте поддерживаемые isolation и savepoint у драйвера. [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html).

## Q4. Что даёт PreparedStatement batch и что делать при частичном сбое?

`PreparedStatement` связывает данные отдельно от SQL и может накопить набор параметров через `addBatch()`, затем отправить их `executeBatch()`. Это сокращает число round trips, но не означает одну атомарную SQL-операцию и не гарантирует одинаковый способ исполнения у драйверов. Делите огромный batch на разумные порции с учётом памяти, транзакции и лимитов сервера.

При `BatchUpdateException` проверьте `getUpdateCounts()`: драйвер может остановиться на первом сбое или продолжить после него, а значения включают специальные маркеры успеха без точного числа и неудачи. Не делайте вывод «весь batch откатился» только из исключения: исход зависит от auto-commit и транзакции. При необходимости атомарности задайте явную транзакцию и обработайте rollback. [PreparedStatement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html), [BatchUpdateException API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html), [Statement API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html).

## Q5. Как читать большой ResultSet без предположения о настоящем streaming?

Цикл `while (rows.next())` обрабатывает строки по одной в приложении, но **не доказывает**, что драйвер не загрузил весь результат в память. `setFetchSize(n)` — подсказка JDBC, её реальное действие зависит от драйвера, режима курсора, транзакции и настроек соединения. Проверяйте документацию конкретного драйвера и измеряйте память, сеть и время удержания соединения.

Держите `ResultSet` и его `Statement` открытыми только пока читаете данные. Долгий экспорт занимает connection из пула, а медленный downstream может задержать транзакцию; для больших выгрузок иногда лучше keyset pagination или отдельный поток экспорта. При выборке нескольких страниц нужен стабильный порядок и понимание изменений данных между запросами. [Statement.setFetchSize](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)), [ResultSet API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html).

## Q6. Как отличить исчерпание пула HikariCP от потерянного соединения?

Таймаут `getConnection()` при `maximumPoolSize` занятых connection означает ожидание **свободного места в пуле** до `connectionTimeout`; это другой симптом, чем ошибка связи во время SQL. Сопоставьте active, idle, total и pending connections, время ожидания, длительность транзакций и состояние БД. Высокий pending при постоянно занятых connection может указывать на длинные запросы, утечку или несоответствие размера пула нагрузке; увеличение размера без анализа может перегрузить саму БД.

`maxLifetime` ограничивает срок жизни физического connection в пуле; HikariCP рекомендует делать его короче лимитов БД и сети. `keepaliveTime` проверяет idle connection и должен быть меньше `maxLifetime`; он не спасает уже потерянный выполняющийся запрос. `leakDetectionThreshold` сообщает о длительном удержании, но не доказывает утечку. После простоя отличайте stale connection от pool exhaustion и сверяйте таймауты с драйвером и инфраструктурой. [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby), [MySQL Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html).

---

## See also

- [SQL](sql-interview.md) Q45 — сравнение JDBC и JPA.
- [Транзакции БД](database-transactions-interview.md) Q33–Q34 — удержание соединения и Spring propagation.
- [MySQL](mysql-interview.md) Q6 — конкретный разрыв Connector/J.
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — репозитории и агрегаты поверх JDBC.
