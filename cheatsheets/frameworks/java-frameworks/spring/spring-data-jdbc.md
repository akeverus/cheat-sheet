---
title: "Spring Data JDBC: Полное руководство по работе с базами данных"
description: "Комплексное руководство по Spring Data JDBC: репозитории, кастомные запросы, транзакции, аудит, интеграция с Spring Boot, тестирование и best practices"
tags:
  - spring
  - jdbc
  - database
  - orm
  - repositories
  - transactions
  - spring-data
  - sql
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "databases/postgres-basics.md"]
next: ["spring/spring-data-jpa.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "databases/postgres-basics.md", "java/java-basics.md"]
---

# Spring Data JDBC: Полное руководство по работе с базами данных



## Полезные ссылки

### Официальная документация
- [**Spring Data JDBC** Reference](https://docs.spring.io/spring-data/jdbc/reference/)
- [**Spring** JDBC](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html)
- [**Spring Boot Data** JDBC](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jdbc)

### **Baeldung**
- [**Spring Data JDBC** Tutorial](https://www.baeldung.com/spring-data-jdbc)
- [**JDBC Template** Examples](https://www.baeldung.com/spring-jdbc-jdbctemplate)
- [**Spring Data JDBC** vs **JPA**](https://www.baeldung.com/spring-data-jdbc-vs-jpa)

## Содержание

- [Spring Data JDBC: Полное руководство по работе с базами данных](#spring-data-jdbc-полное-руководство-по-работе-с-базами-данных)
- [Руководство по JDBC](#руководство-по-jdbc)
  - [Драйверы JDBC](#драйверы-jdbc)
  - [Подключение к базе данных](#подключение-к-базе-данных)
  - [Выполнение SQL-запросов](#выполнение-sql-запросов)
    - [Statement](#statement)
    - [PreparedStatement](#preparedstatement)
    - [CallableStatement](#callablestatement)
  - [Работа с ResultSet](#работа-с-resultset)
  - [Метаданные](#метаданные)
  - [Управление транзакциями](#управление-транзакциями)
  - [Закрытие ресурсов](#закрытие-ресурсов)
- [Руководство по Spring JDBC](#руководство-по-spring-jdbc)
  - [Настройка источника данных](#настройка-источника-данных)
  - [JdbcTemplate](#jdbctemplate)
  - [NamedParameterJdbcTemplate](#namedparameterjdbctemplate)
- [Пакетная обработка](#пакетная-обработка)
  - [Преимущества пакетной обработки](#преимущества-пакетной-обработки)
  - [Пакетная обработка с Statement](#пакетная-обработка-с-statement)
  - [Пакетная обработка с PreparedStatement](#пакетная-обработка-с-preparedstatement)
- [SQL-инъекция и как ее предотвратить?](#sql-инъекция-и-как-ее-предотвратить)
  - [Что такое SQL-инъекция?](#что-такое-sql-инъекция)
  - [Защита с помощью PreparedStatement](#защита-с-помощью-preparedstatement)
  - [Защита в JPA](#защита-в-jpa)
  - [Белые списки (Whitelisting)](#белые-списки-whitelisting)
  - [Использование Criteria API в JPA](#использование-criteria-api-в-jpa)
  - [Дополнительные меры защиты](#дополнительные-меры-защиты)
- [Введение в транзакции](#введение-в-транзакции)
  - [Локальные транзакции](#локальные-транзакции)
    - [JDBC транзакции](#jdbc-транзакции)
    - [JPA транзакции](#jpa-транзакции)
    - [JMS транзакции](#jms-транзакции)
  - [Распределенные транзакции](#распределенные-транзакции)
    - [JTA и JTS](#jta-и-jts)
  - [Транзакции в Spring](#транзакции-в-spring)
  - [Рекомендации](#рекомендации)
- [Руководство по преобразованию ResultSet в JSON](#руководство-по-преобразованию-resultset-в-json)
  - [Использование библиотеки JSON-Java](#использование-библиотеки-json-java)
  - [Использование jOOQ](#использование-jooq)
- [Spring Data JDBC](#spring-data-jdbc)
  - [Введение в Spring Data JDBC](#введение-в-spring-data-jdbc)
  - [Преимущества Spring Data JDBC](#преимущества-spring-data-jdbc)
  - [Настройка Spring Data JDBC](#настройка-spring-data-jdbc)
  - [Создание сущностей](#создание-сущностей)
  - [Репозитории](#репозитории)
  - [Кастомные запросы и RowMapper](#кастомные-запросы-и-rowmapper)
  - [Отношения между сущностями](#отношения-между-сущностями)
  - [Агрегаты и DDD](#агрегаты-и-ddd)
  - [Транзакции](#транзакции)
  - [Аудит](#аудит)
  - [Интеграция с Spring Boot](#интеграция-с-spring-boot)
- [application.yml](#applicationyml)
  - [Тестирование](#тестирование)
  - [Производительность](#производительность)
  - [Лучшие практики](#best-practices)
    - [1. Выбор между Spring Data JDBC и JPA](#1-выбор-между-spring-data-jdbc-и-jpa)
    - [2. Правильное использование транзакций](#2-правильное-использование-транзакций)
    - [3. Оптимизация запросов](#3-оптимизация-запросов)
    - [4. Безопасность](#4-безопасность)
    - [5. Мониторинг и метрики](#5-мониторинг-и-метрики)
- [Заключение](#заключение)
## Руководство по **JDBC**

В этой статье мы рассмотрим **JDBC (**Java Database Connectivity**),** который представляет собой **API** для подключения и выполнения запросов к базе данных**.

**JDBC** может работать с любой базой данных при наличии соответствующих драйверов**.

### Драйверы **JDBC**

**Драйвер JDBC** - это реализация **JDBC `API`,** используемая для подключения к определенному типу базы данных**. Существует несколько типов драйверов **JDBC**:**

1. **Тип 1** - содержит сопоставление с другим **API** доступа к данным; примером этого является драйвер **JDBC-ODBC**.
2. **Тип 2** - это реализация, использующая клиентские библиотеки целевой базы данных; также называется драйвером собственного **API**
3. **Тип 3** - использует промежуточное программное обеспечение для преобразования вызовов **JDBC** в вызовы, специфичные для базы данных; также известный как драйвер сетевого протокола
4. **Тип 4** - подключение напрямую к базе данных путем преобразования вызовов **JDBC** в вызовы, специфичные для базы данных; известные как драйверы протокола базы данных или тонкие драйверы

Наиболее часто используется тип **4,** так как он не зависит от платформы**. Прямое подключение к серверу базы данных обеспечивает более высокую производительность по сравнению с другими типами**. Недостатком этого типа драйвера является то, что он специфичен для базы данных, учитывая, что каждая база данных имеет свой собственный протокол**.

### Подключение к базе данных

Чтобы подключиться к базе данных, нам просто нужно инициализировать драйвер и открыть соединение с базой данных**.

Поскольку мы используем базу данных **MySQL,** нам нужна зависимость **mysql-`connector-java`.**

**Зависимость **MySQL connector** (**pom.xml**):**

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.32</version>
</dependency>
```

В старых версиях **JDBC** перед получением соединения нам сначала нужно было инициализировать драйвер **JDBC,** вызвав метод **Class.`forName`.** Начиная с **JDBC `4.0`,** все драйверы, найденные в пути к классам, загружаются автоматически**. Поэтому нам не понадобится эта часть **Class.forName** в современных средах**.

**Чтобы открыть соединение, мы можем использовать метод **getConnection()** класса **DriverManager.** Для этого метода требуется параметр строки **URL-**адреса подключения:**

```java
// Подключение к БД через DriverManager
try (Connection con = DriverManager
    .getConnection("jdbc:mysql://localhost:3306/myDb", "user1", "pass")) {
    // работа с подключением
}
```

Поскольку **Connection** является ресурсом **AutoCloseable,** мы должны использовать его внутри блока **try-`with-resources`.**

**Синтаксис **URL-**адреса подключения зависит от типа используемой базы данных**. Давайте рассмотрим несколько примеров:**

```java
jdbc:mysql://localhost:3306/myDb?user=user1&password=pass
jdbc:postgresql://localhost/myDb
jdbc:hsqldb:mem:myDb
```

### Выполнение **SQL**-запросов

Для отправки **SQL-**инструкций в базу данных мы можем использовать экземпляры типа **Statement, PreparedStatement** или **CallableStatement,** которые мы можем получить с помощью объекта **Connection.**

#### **Statement**

Интерфейс оператора содержит основные функции для выполнения команд **SQL.**

Во-первых, давайте создадим объект **Statement:**

```java
try (Statement stmt = con.createStatement()) {
    // использование Statement
}
```

Опять же, мы должны работать с операторами внутри блока **try-with-resources** для автоматического управления ресурсами**.

**В любом случае, выполнение инструкций **SQL** может быть выполнено тремя способами:**

1. **executeQuery()** для инструкций **SELECT**
2. **executeUpdate()** для обновления данных или структуры базы данных
3. **execute()** можно использовать в обоих случаях выше, когда результат неизвестен**.

**Давайте используем метод **execute()** для добавления таблицы студентов в нашу базу данных:**

```java
String tableSql = "CREATE TABLE IF NOT EXISTS employees"
    + "(emp_id int PRIMARY KEY AUTO_INCREMENT, name varchar(30),"
    + "position varchar(30), salary double)";
stmt.execute(tableSql);
```

При использовании метода **execute()** для обновления данных метод **stmt.`getUpdateCount()`** возвращает количество затронутых строк**.

Если результат равен **0,** то либо строки не были затронуты, либо это была команда обновления структуры базы данных**.

**Если значение равно -1,** то команда была запросом **SELECT;** затем мы можем получить результат, используя **stmt.`getResultSet()`.**

Далее добавим запись в нашу таблицу с помощью метода **executeUpdate():**

```java
String insertSql = "INSERT INTO employees(name, position, salary)"
    + " VALUES('john', 'developer', 2000)";
stmt.executeUpdate(insertSql);
```

Метод возвращает количество затронутых строк для команды, которая обновляет строки, или **0** для команды, которая обновляет структуру базы данных**.

Мы можем получить записи из таблицы, используя метод **executeQuery(),** который возвращает объект типа **ResultSet:**

```java
String selectSql = "SELECT * FROM employees";
try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
    // обработка результатов
}
```

#### **PreparedStatement**

Объекты **PreparedStatement** содержат предварительно скомпилированные последовательности **SQL.** Они могут иметь один или несколько параметров, обозначенных знаком вопроса**.

**Давайте создадим **PreparedStatement,** который обновляет записи в таблице сотрудников на основе заданных параметров:**

```java
String updatePositionSql = "UPDATE employees SET position=? WHERE emp_id=?";
try (PreparedStatement pstmt = con.prepareStatement(updatePositionSql)) {
    pstmt.setString(1, "lead developer");
    pstmt.setInt(2, 1);
    int rowsAffected = pstmt.executeUpdate();
}
```

Чтобы добавить параметры в **PreparedStatement,** мы можем использовать простые сеттеры **- `setX()` -** где **X -** тип параметра, а аргументы метода - порядок и значение параметра**.

Оператор выполняется одним из трех описанных выше методов**: `executeQuery()`, `executeUpdate()`, execute()** без параметра **SQL String**.

#### **CallableStatement**

Интерфейс **CallableStatement** позволяет вызывать хранимые процедуры**.

Чтобы создать объект **CallableStatement,** мы можем использовать метод **prepareCall() `Connection`:**

```java
String preparedSql = "{call insertEmployee(?,?,?,?)}";
try (CallableStatement cstmt = con.prepareCall(preparedSql)) {
    cstmt.setString(2, "ana");
    cstmt.setString(3, "tester");
    cstmt.setDouble(4, 2000);
    cstmt.registerOutParameter(1, Types.INTEGER);
    cstmt.execute();
    int new_id = cstmt.getInt(1);
}
```

Установка значений входных параметров для хранимой процедуры осуществляется аналогично интерфейсу **PreparedStatement** с использованием методов **setX():**

Если у хранимой процедуры есть выходные параметры, их нужно добавить с помощью метода **registerOutParameter():**

Затем давайте выполним оператор и получим возвращаемое значение с помощью соответствующего метода **getX():**

Пример хранимой процедуры для **MySQL:**

```sql
delimiter //
CREATE PROCEDURE insertEmployee(OUT emp_id int,
    IN emp_name varchar(30), IN position varchar(30), IN salary double)
BEGIN
    INSERT INTO employees(name, position,salary) VALUES (emp_name,position,salary);
    SET emp_id = LAST_INSERT_ID();
END //
delimiter;
```

### Работа с **ResultSet**

После выполнения запроса результат представляется объектом **ResultSet,** который имеет структуру, аналогичную таблице, со строками и столбцами**.

**ResultSet** использует метод **next()** для перехода к следующей строке**.

**Давайте сначала создадим класс **Employee** для хранения полученных нами записей:**

```java
public class Employee {
    private int id;
    private String name;
    private String position;
    private double salary;
    // геттеры и сеттеры
}
```

**Далее пройдемся по **ResultSet** и создадим объект **Employee** для каждой записи:**

```java
String selectSql = "SELECT * FROM employees";
try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
    List<Employee> employees = new ArrayList<>();
    while (resultSet.next()) {
        Employee emp = new Employee();
        emp.setId(resultSet.getInt("emp_id"));
        emp.setName(resultSet.getString("name"));
        emp.setPosition(resultSet.getString("position"));
        emp.setSalary(resultSet.getDouble("salary"));
        employees.add(emp);
    }
}
```

Получить значение для каждой ячейки таблицы можно с помощью методов типа **getX(),** где **X** представляет тип данных ячейки**.

Методы **getX()** можно использовать с параметром **int,** представляющим порядок ячеек, или параметром **String,** представляющим имя столбца**. Последний вариант предпочтительнее, если мы меняем порядок столбцов в запросе**.

Неявно, объект **ResultSet** может быть пройден только вперед и не может быть изменен**.

**Если мы хотим использовать **ResultSet** для обновления данных и обхода их в обоих направлениях, нам нужно создать объект **Statement** с дополнительными параметрами:**

```java
stmt = con.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_UPDATABLE
);
```

**Для навигации по этому типу **ResultSet** мы можем использовать один из методов:**

1. **first(), last(), `beforeFirst()`, `beforeLast()`** - для перехода к первой или последней строке **ResultSet** или к строке перед ними**.
2. **next(), previous()** - для перехода вперед и назад в **ResultSet**
3. **getRow()** - для получения текущего номера строки
4. **moveToInsertRow(), `moveToCurrentRow()`** - для перехода к новой пустой строке для вставки и обратно к текущей, если в новой строке
5. **absolute(**int row**)** - перейти к указанной строке
6. **relative(**int nrRows**)** - для перемещения курсора на заданное количество строк

Обновление **ResultSet** можно выполнить с помощью методов формата **updateX(),** где **X -** тип данных ячейки**. Эти методы обновляют только объект **ResultSet**,** но не таблицы базы данных**.

**Чтобы сохранить изменения **ResultSet** в базе данных, мы должны дополнительно использовать один из методов:**

1. **updateRow()** - сохранить изменения текущей строки в базе данных**.
2. **insertRow(), `deleteRow()`** - добавить новую строку или удалить текущую из базы данных
3. **refreshRow()** - для обновления **ResultSet** при любых изменениях в базе данных**.
4. **cancelRowUpdates()** - для отмены изменений, внесенных в текущую строку**.

**Пример обновления данных:**

```java
try (Statement updatableStmt = con.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
    try (ResultSet updatableResultSet = updatableStmt.executeQuery(selectSql)) {
        updatableResultSet.moveToInsertRow();
        updatableResultSet.updateString("name", "mark");
        updatableResultSet.updateString("position", "analyst");
        updatableResultSet.updateDouble("salary", 2000);
        updatableResultSet.insertRow();
    }
}
```

### Метаданные

**JDBC API** позволяет искать информацию о базе данных, называемую метаданными**.

Интерфейс **DatabaseMetadata** можно использовать для получения общей информации о базе данных, такой как таблицы, хранимые процедуры или диалект **SQL.**

**Давайте кратко рассмотрим, как мы можем получить информацию о таблицах базы данных:**

```java
DatabaseMetaData dbmd = con.getMetaData();
ResultSet tablesResultSet = dbmd.getTables(null, null, "%", null);
while (tablesResultSet.next()) {
    LOG.info(tablesResultSet.getString("TABLE_NAME"));
}
```

**Этот интерфейс можно использовать для поиска информации об определенном наборе результатов, такой как количество и имя его столбцов:**

```java
ResultSetMetaData rsmd = rs.getMetaData();
int nrColumns = rsmd.getColumnCount();
IntStream.range(1, nrColumns).forEach(i -> {
    try {
        LOG.info(rsmd.getColumnName(i));
    } catch (SQLException e) {
        e.printStackTrace();
    }
});
```

### Управление транзакциями

По умолчанию каждый оператор **SQL** фиксируется сразу после его завершения**. Однако также возможно управлять транзакциями программно**.

Это может быть необходимо в тех случаях, когда мы хотим сохранить согласованность данных, например, когда мы хотим зафиксировать транзакцию только в том случае, если предыдущая транзакция завершилась успешно**.

Во-первых, нам нужно установить для свойства **autoCommit Connection** значение **false,** а затем использовать методы **commit()** и **rollback()** для управления транзакцией**.

**Давайте добавим второй оператор обновления для столбца зарплаты после обновления столбца должности сотрудника и завернем их оба в транзакцию**. Таким образом, зарплата будет обновлена только в том случае, если позиция была успешно обновлена:**

```java
String updatePositionSql = "UPDATE employees SET position=? WHERE emp_id=?";
PreparedStatement pstmt = con.prepareStatement(updatePositionSql);
pstmt.setString(1, "lead developer");
pstmt.setInt(2, 1);

String updateSalarySql = "UPDATE employees SET salary=? WHERE emp_id=?";
PreparedStatement pstmt2 = con.prepareStatement(updateSalarySql);
pstmt2.setDouble(1, 3000);
pstmt2.setInt(2, 1);

boolean autoCommit = con.getAutoCommit();
try {
    con.setAutoCommit(false);
    pstmt.executeUpdate();
    pstmt2.executeUpdate();
    con.commit();
} catch (SQLException exc) {
    con.rollback();
} finally {
    con.setAutoCommit(autoCommit);
}
```

### Закрытие ресурсов

Когда мы больше не используем его, нам нужно закрыть соединение, чтобы освободить ресурсы базы данных**.

Мы можем сделать это с помощью **API close():**

```java
con.close();
```

Однако, если мы используем ресурс в блоке **try-`with-resources`,** нам не нужно явно вызывать метод **close(),** так как блок **try-with-resources** делает это за нас автоматически**.

То же самое относится и к операторам **Statement**, **PreparedStatement**, **CallableStatement** и **ResultSet s.**

## Руководство по **Spring JDBC**

В этом руководстве мы рассмотрим практические варианты использования модуля **Spring `JDBC`.**

**Все классы в **Spring JDBC** разделены на четыре отдельных пакета:**

1. **core** - основная функциональность **JDBC.** Некоторые из важных классов этого пакета включают **JdbcTemplate, `SimpleJdbcInsert`, SimpleJdbcCall** и **NamedParameterJdbcTemplate.**
2. **datasource** — служебные классы для доступа к источнику данных. Он также имеет различные реализации источников данных для тестирования кода **JDBC** вне контейнера **Jakarta EE**.
3. **object** - доступ к БД объектно-ориентированным способом**. Это позволяет выполнять запросы и возвращать результаты в виде бизнес-объекта**. Он также сопоставляет результаты запроса между столбцами и свойствами бизнес-объектов**.
4. **support** - классы поддержки для классов в основных и объектных пакетах, например, обеспечиваетфункциональность перевода **SQLException**

### Настройка источника данных

Начнем с простой настройки источника данных**.

Мы будем использовать базу данных **MySQL:**

```java
// Конфигурация Spring JDBC и сканирование компонентов
@Configuration
@ComponentScan("com.baeldung.jdbc")
public class SpringJdbcConfig {
    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/springjdbc");
        dataSource.setUsername("guest_user");
        dataSource.setPassword("guest_password");
        return dataSource;
    }
}
```

В качестве альтернативы мы также можем эффективно использовать встроенную базу данных для разработки или тестирования**.

Вот быстрая конфигурация, которая создает экземпляр встроенной базы данных **H2** и предварительно заполняет его простыми сценариями **SQL:**

```java
@Bean
public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:jdbc/schema.sql")
        .addScript("classpath:jdbc/test-data.sql").build();
}
```

**Наконец, то же самое можно сделать с помощью настройки **XML** для источника данных:**

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/springjdbc"/>
    <property name="username" value="guest_user"/>
    <property name="password" value="guest_password"/>
</bean>
```

### **JdbcTemplate**

**Шаблон JDBC** - это основной **API,** через который мы получим доступ к большей части интересующей нас функциональности:**

1. создание и закрытие соединений
2. запущенные операторы и вызовы хранимых процедур
3. перебор **ResultSet** и возврат результатов

Во-первых, давайте начнем с простого примера, чтобы увидеть, что может сделать **JdbcTemplate:**

```java
int result = jdbcTemplate.queryForObject(
    "SELECT COUNT(*) FROM EMPLOYEE", Integer.class);
```

А вот простой **INSERT:**

```java
public int addEmployee(int id) {
    return jdbcTemplate.update(
        "INSERT INTO EMPLOYEE VALUES (?,?,?,?)", id, "Bill", "Gates", "USA");
}
```

Обратите внимание на стандартный синтаксис предоставления параметров с использованием? характер**.

### **NamedParameterJdbcTemplate**

Чтобы получить поддержку именованных параметров, мы будем использовать другой шаблон **JDBC,** предоставляемый фреймворком **- `NamedParameterJdbcTemplate`.**

Кроме того, это обертка **JbdcTemplate** и предоставляет альтернативу традиционному синтаксису с использованием? указать параметры**.

**Под капотом он заменяет именованные параметры на **JDBC?** заполнитель и делегаты в обернутый **JDCTemplate** для выполнения запросов:**

```java
SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", 1);
return namedParameterJdbcTemplate.queryForObject(
    "SELECT FIRST_NAME FROM EMPLOYEE WHERE ID =:id", namedParameters, String.class);
```

## Пакетная обработка

**Java `Database Connectivity` (**JDBC**)** - это **Java `API`,** используемый для взаимодействия с базами данных**. Пакетная обработка группирует несколько запросов в один блок и передает его в базе данных за одно сетевое обращение**.

В этой статье мы узнаем, как можно использовать **JDBC** для пакетной обработки запросов **SQL.**

### Преимущества пакетной обработки

Производительность и согласованность данных являются основными мотивами для пакетной обработки**.

В некоторых случаях использования требуется вставить большой объем данных в таблицу базы данных**. При использовании **JDBC** одним из способов добиться этого без пакетной обработки является последовательное выполнение нескольких запросов**.

**Проблема с последовательными запросами:**

```java
statement.execute("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
    + "VALUES ('1','EmployeeName1','Designation1')");
statement.execute("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
    + "VALUES ('2','EmployeeName2','Designation2')");
```

Эти последовательные вызовы увеличат количество сетевых обращений к базе данных, что приведет к снижению производительности**.

При использовании пакетной обработки эти запросы можно отправлять в базу данных за один вызов, что повышает производительность**.

В определенных обстоятельствах данные необходимо помещать в несколько таблиц**. Это приводит к взаимосвязанной транзакции, в которой важна последовательность отправляемых запросов**.

Любые ошибки, возникающие во время выполнения, должны приводить к откату данных, переданных предыдущими запросами, если таковые имеются**.

### Пакетная обработка с **Statement**

**JDBC** предоставляет два класса**, Statement** и **PreparedStatement,** для выполнения запросов к базе данных**. Оба класса имеют собственную реализацию методов **addBatch**()** и **executeBatch**(),** которые предоставляют нам функциональность пакетной обработки**.

В **JDBC** самый простой способ выполнения запросов к базе данных - через объект **Statement.**

Во-первых, с помощью **addBatch()** мы можем добавить все **SQL-**запросы в пакет, а затем выполнить эти **SQL-**запросы с помощью **executeBatch().**

Тип возвращаемого значения **executeBatch()** представляет собой массив **int,** указывающий, сколько записей было затронуто выполнением каждого оператора **SQL.**

**Давайте посмотрим на пример создания и выполнения пакета с использованием оператора:**

```java
Statement statement = connection.createStatement();
statement.addBatch("INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
    + "VALUES ('1','EmployeeName','Designation')");
statement.addBatch("INSERT INTO EMP_ADDRESS(ID, EMP_ID, ADDRESS) "
    + "VALUES ('10','1','Address')");
statement.executeBatch();
```

В приведенном выше примере мы пытаемся вставить записи в таблицы **EMPLOYEE** и **EMP_ADDRESS** с помощью **Statement.** Мы видим, как **SQL-**запросы добавляются в пакет для выполнения**.

### Пакетная обработка с **PreparedStatement**

**PreparedStatement** - еще один класс, используемый для выполнения **SQL-**запросов**. Это позволяет повторно использовать операторы **SQL** и требует от нас установки новых параметров для каждого обновления**/**вставки**.

Давайте посмотрим на пример с использованием **PreparedStatement.** Во-первых, мы настраиваем оператор, используя **SQL-**запрос, закодированный как **String:**

```java
String[] EMPLOYEES = new String[]{"Zuck","Mike","Larry","Musk","Steve"};
String[] DESIGNATIONS = new String[]{"CFO","CSO","CTO","CEO","CMO"};
String insertEmployeeSQL = "INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
    + "VALUES (?,?,?)";
PreparedStatement employeeStmt = connection.prepareStatement(insertEmployeeSQL);
```

Затем мы перебираем массив значений **String** и добавляем в пакет только что настроенный запрос**.

**После завершения цикла мы выполняем пакет:**

```java
for(int i = 0; i < EMPLOYEES.length; i++) {
    String employeeId = UUID.randomUUID().toString();
    employeeStmt.setString(1, employeeId);
    employeeStmt.setString(2, EMPLOYEES[i]);
    employeeStmt.setString(3, DESIGNATIONS[i]);
    employeeStmt.addBatch();
}
employeeStmt.executeBatch();
```

В приведенном выше примере мы вставляем записи в таблицу **EMPLOYEE** с помощью **PreparedStatement.** Мы можем видеть, как вставляемые значения задаются в запросе, а затем добавляются в пакет для выполнения**.

## SQL-инъекция и как ее предотвратить?

Несмотря на то, что **SQL Injection** является одной из самых известных уязвимостей, он по-прежнему занимает первое место в печально известном списке **OWASP `Top 10` -** теперь это часть более общего класса **Injection.**

В этом руководстве мы рассмотрим распространенные ошибки кодирования в **Java,** которые приводят к уязвимости приложения, и способы их предотвращения с помощью **API,** доступных в стандартной библиотеке времени выполнения **JVM.** Мы также расскажем, какую защиту мы можем получить с помощью **ORM,** таких как **JPA, Hibernate** и других, и о каких слепых зонах нам все еще придется беспокоиться**.

### Что такое **SQL**-инъекция?

Атаки с внедрением работают, потому что для многих приложений единственный способ выполнить данное вычисление - это динамически сгенерировать код, который, в свою очередь, запускается другой системой или компонентом**. Если в процессе генерации этого кода мы используем ненадежные данные без надлежащей очистки, мы оставляем открытой дверь для хакеров**.

**Пример уязвимого кода:**

```java
public List<AccountDTO> unsafeFindAccountsByCustomerId(String customerId) throws SQLException {
    String sql = "select "
        + "customer_id,acc_number,branch_id,balance "
        + "from Accounts where customer_id = '"
        + customerId
        + "'";
    Connection c = dataSource.getConnection();
    ResultSet rs = c.createStatement().executeQuery(sql);
}
```

Проблема с этим кодом очевидна**: мы поместили значение **customerId** в запрос вообще без проверки**. Ничего страшного не произойдет, если мы будем уверены, что это значение будет получено только из надежных источников, но можем ли мы**?

**Пример атаки:**

```bash
curl -X GET \
'http://localhost:8080/accounts?customerId=abc%27%20or%20%271%27=%271'
```

**Если предположить, что значение параметра **customerId** не проверяется до тех пор, пока оно не достигнет нашей функции, вот что мы получим:**

```text
abc' or '1' = '1
```

**Когда мы присоединяем это значение к фиксированной части, мы получаем окончательный оператор **SQL,** который будет выполнен:**

```sql
select customer_id, acc_number,branch_id, balance
from Accounts where customerId = 'abc' or '1' = '1'
```

Наверное, не то, что мы хотели…

### Защита с помощью **PreparedStatement**

Этот метод состоит в использовании подготовленных операторов с заполнителем вопросительного знака (**«?»**)** в наших запросах всякий раз, когда нам нужно вставить введенное пользователем значение**. Это очень эффективно и, если в реализации драйвера **JDBC** нет ошибок, невосприимчиво к эксплойтам**.

**Давайте перепишем наш пример функции, чтобы использовать эту технику:**

```java
public List<AccountDTO> safeFindAccountsByCustomerId(String customerId) throws Exception {
    String sql = "select "
        + "customer_id, acc_number, branch_id, balance from Accounts"
        + "where customer_id =?";
    Connection c = dataSource.getConnection();
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, customerId);
    ResultSet rs = p.executeQuery(sql);
}
```

Здесь мы использовали метод **prepareStatement(),** доступный в экземпляре **Connection,** чтобы получить **PreparedStatement.** Этот интерфейс расширяет обычный интерфейс **Statement** несколькими методами, которые позволяют нам безопасно вставлять введенные пользователем значения в запрос перед его выполнением**.

### Защита в **JPA**

**Для **JPA** у нас есть аналогичная функция:**

```java
String jql = "from Account where customerId =:customerId";
TypedQuery<Account> q = em.createQuery(jql, Account.class)
    .setParameter("customerId", customerId);
```

**При запуске этого кода под **Spring Boot** мы можем установить для свойства **logging.level.sql** значение **DEBUG** и посмотреть, какой запрос фактически построен для выполнения этой операции:**

```text
[DEBUG][SQL] select
    account0_.id as id1_0_,
    account0_.acc_number as acc_numb2_0_,
    account0_.balance as balance3_0_,
    account0_.branch_id as branch_i4_0_,
    account0_.customer_id as customer5_0_
from accounts account0_
where account0_.customer_id=?
```

Как и ожидалось, уровень **ORM** создает подготовленный оператор, используя заполнитель для параметра **customerId.** Это то же самое, что мы сделали в простом случае **JDBC,** но с несколькими операторами меньше, что приятно**.

В качестве бонуса этот подход обычно приводит к повышению производительности запроса, поскольку большинство баз данных могут кэшировать план запроса, связанный с подготовленным оператором**.

### Белые списки (**Whitelisting**)

**Очистка данных -** это метод применения фильтра к предоставленным пользователем данным, чтобы их можно было безопасно использовать в других частях нашего приложения**. Реализация фильтров может сильно различаться, но обычно мы можем разделить их на два типа**: белые списки и черные списки**.

Черные списки, состоящие из фильтров, пытающихся идентифицировать недопустимый шаблон, обычно малоэффективны в контексте предотвращения **SQL-**инъекций, но не для их обнаружения! Подробнее об этом позже**.

С другой стороны, белые списки работают особенно хорошо, когда мы можем точно определить, что является допустимым вводом**.

**Давайте усовершенствуем наш метод **safeFindAccountsByCustomerId,** чтобы теперь вызывающая сторона также могла указать столбец, используемый для сортировки результирующего набора**. Поскольку мы знаем набор возможных столбцов, мы можем реализовать белый список, используя простой набор, и использовать его для очистки полученного параметра:**

```java
private static final Set<String> VALID_COLUMNS_FOR_ORDER_BY
    = Collections.unmodifiableSet(Stream
        .of("acc_number","branch_id","balance")
        .collect(Collectors.toCollection(HashSet::new)));

public List<AccountDTO> safeFindAccountsByCustomerId(
        String customerId,
        String orderBy) throws Exception {
    String sql = "select "
        + "customer_id,acc_number,branch_id,balance from Accounts"
        + "where customer_id =? ";
    if (VALID_COLUMNS_FOR_ORDER_BY.contains(orderBy)) {
        sql = sql + " order by " + orderBy;
    } else {
        throw new IllegalArgumentException("Nice try!");
    }
    Connection c = dataSource.getConnection();
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, customerId);
}
```

Здесь мы комбинируем подход с подготовленным оператором и белый список, используемый для очистки аргумента **orderBy.** Конечным результатом является безопасная строка с окончательным оператором **SQL.** В этом простом примере мы используем статический набор, но мы могли бы также использовать функции метаданных базы данных для его создания**.

### Использование **Criteria API** в **JPA**

**Мы можем использовать тот же подход для **JPA,** также воспользовавшись **Criteria API** и метаданными, чтобы избежать использования строковых констант в нашем коде:**

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Account> cq = cb.createQuery(Account.class);
Root<Account> root = cq.from(Account.class);
cq.select(root).where(cb.equal(root.get(Account_.customerId), customerId));
TypedQuery<Account> q = em.createQuery(cq);
```

Здесь мы использовали больше строк кода, чтобы получить тот же результат, но преимущество в том, что теперь нам не нужно беспокоиться о синтаксисе **JQL.**

### Дополнительные меры защиты

В качестве хорошей практики безопасности мы всегда должны реализовывать несколько уровней защиты - концепция, известная как многоуровневая защита**. Основная идея заключается в том, что даже если мы не сможем найти все возможные уязвимости в нашем коде - обычный сценарий при работе с устаревшими системами - мы должны хотя бы попытаться ограничить ущерб, который может нанести атака**.

**Конечно, это тема для целой статьи или даже книги, но давайте назовем несколько мер:**

1. Применяйте принцип наименьших привилегий**: максимально ограничивайте привилегии учетной записи, используемой для доступа к базе данных**.
2. Используйте доступные методы для конкретной базы данных, чтобы добавить дополнительный уровень защиты; например, база данных **H2** имеет параметр уровня сеанса, который отключает все литеральные значения в запросах **SQL.**
3. Используйте недолговечные учетные данные**: заставьте приложение часто менять учетные данные базы данных; хороший способ реализовать это - использовать **Spring Cloud Vault**.
4. Регистрируйте все**: если приложение хранит данные о клиентах, это обязательно; доступно множество решений, которые интегрируются напрямую в базу данных или работают как прокси, так что в случае атаки мы можем хотя бы оценить ущерб
5. Используйте **WAF** или аналогичные решения для обнаружения вторжений**: это типичные примеры черных списков - обычно они поставляются с большой базой данных известных сигнатур атак и запускают запрограммированное действие при обнаружении**.

## Введение в транзакции

Транзакции в **Java,** как и в целом, относятся к серии действий, которые должны завершиться успешно . Следовательно**, если одно или несколько действий завершаются неудачно, все остальные действия должны быть отменены, оставляя состояние приложения неизменным** . Это необходимо для того, чтобы целостность состояния приложения никогда не была нарушена**.

Кроме того, эти транзакции могут задействовать один или несколько ресурсов, таких как база данных, очередь сообщений, что приводит к различным способам выполнения действий в рамках транзакции**. К ним относятся выполнение локальных транзакций ресурсов с отдельными ресурсами**. Альтернативно, в глобальной транзакции могут участвовать несколько ресурсов**.

### Локальные транзакции

Сначала мы рассмотрим, как можно использовать транзакции в **Java** при работе с отдельными ресурсами**. Здесь у нас может быть несколько отдельных действий, которые мы выполняем с таким ресурсом, как база данных** . Но мы можем захотеть, чтобы они происходили как единое целое, как неделимая единица работы**. Другими словами, мы хотим, чтобы эти действия происходили в рамках одной транзакции**.

#### **JDBC** транзакции

**JDBC** предоставляет нам возможность выполнять инструкции в рамках транзакции**.

Поведение **Connection по умолчанию - auto-commit** . Чтобы уточнить, это означает, что каждый отдельный оператор рассматривается как транзакция и автоматически фиксируется сразу после выполнения**.

**Однако, если мы хотим объединить несколько операторов в одну транзакцию, этого также можно добиться:**

```java
Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
try {
    connection.setAutoCommit(false);
    PreparedStatement firstStatement = connection.prepareStatement("firstQuery");
    firstStatement.executeUpdate();
    PreparedStatement secondStatement = connection.prepareStatement("secondQuery");
    secondStatement.executeUpdate();
    connection.commit();
} catch (Exception e) {
    connection.rollback();
}
```

Здесь мы отключили режим автоматической фиксации **Connection** . Следовательно, мы можем **вручную определить границу транзакции и выполнить** фиксацию **или** откат . **JDBC** также позволяет нам установить точку сохранения, которая дает нам больше контроля над объемом отката**.

#### **JPA** транзакции

**Java `Persistence API` (**JPA**)** - это спецификация **Java,** которую можно использовать для устранения разрыва между объектно-ориентированными моделями предметной области и системами реляционных баз данных** . Итак, существует несколько реализаций **JPA** от третьих сторон, таких как **Hibernate**, **EclipseLink** и **iBatis**.**

**В **JPA** мы можем определить обычные классы как **Entity,** обеспечивающие им постоянную идентичность**. Класс **EntityManager** предоставляет необходимый интерфейс для работы с несколькими сущностями в контексте персистентности** . Контекст персистентности можно рассматривать как кеш первого уровня, в котором осуществляется управление сущностями:**

Контекст персистентности здесь может быть двух типов**: в области транзакции или в расширенной области**. Контекст персистентности в области транзакции привязан к одной транзакции**. В то время как контекст персистентности с расширенной областью действия может охватывать несколько транзакций**. Областью **контекста персистентности по умолчанию является область транзакции** .

**Давайте посмотрим, как мы можем создать **EntityManager** и определить границу транзакции вручную:**

```java
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-example");
EntityManager entityManager = entityManagerFactory.createEntityManager();
try {
    entityManager.getTransaction().begin();
    entityManager.persist(firstEntity);
    entityManager.persist(secondEntity);
    entityManager.getTransaction().commit();
} catch (Exception e) {
    entityManager.getTransaction().rollback();
}
```

Здесь мы создаем **EntityManager** из **EntityManagerFactory** в контексте контекста персистентности на уровне транзакции**. Затем мы определяем границу транзакции с помощью методов **Begin**,** фиксации и отката .

#### **JMS** транзакции

Служба сообщений **Java (**JMS**)** - это спецификация **Java,** которая позволяет приложениям взаимодействовать асинхронно с помощью сообщений**. **API** позволяет нам создавать, отправлять, получать и читать сообщения из очереди или темы**. Существует несколько служб обмена сообщениями, соответствующих спецификациям **JMS,** включая **OpenMQ** и **ActiveMQ.**

**API JMS** поддерживает объединение нескольких операций отправки или получения в одну транзакцию**. Однако по природе архитектуры интеграции на основе сообщений производство и потребление сообщения не могут быть частью одной и той же транзакции** . Объем транзакции остается между клиентом и поставщиком **JMS:**

**JMS** позволяет нам создавать сеанс из соединения, которое мы получаем из **ConnectionFactory** конкретного поставщика .

У нас есть **возможность создать** сеанс**, который будет выполняться или нет** . Для сеансов без транзакций мы также можем дополнительно определить соответствующий режим подтверждения**.

**Давайте посмотрим, как мы можем создать транзакционный сеанс для отправки нескольких сообщений в рамках транзакции:**

```java
ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_URL);
Connection connection = connectionFactory.createConnection();
connection.start();
try {
    Session session = connection.createSession(true, 0);
    Destination destination = session.createTopic("TEST.FOO");
    MessageProducer producer = session.createProducer(destination);
    producer.send(firstMessage);
    producer.send(secondMessage);
    session.commit();
} catch (Exception e) {
    session.rollback();
}
```

Здесь мы создаем **MessageProducer** для назначения типа темы**. Мы получаем пункт назначения из сеанса, который мы создали ранее**. Далее мы используем **Session** для определения границ транзакции с помощью методов **commit** и **rollback** .

### Распределенные транзакции

Как мы видели, локальные транзакции ресурсов позволяют нам выполнять несколько операций в рамках одного ресурса как единого целого**. Но довольно часто мы имеем дело с операциями, охватывающими несколько ресурсов** . Например, работа в двух разных базах данных или база данных и очередь сообщений**. Здесь нам будет недостаточно поддержки локальных транзакций внутри ресурсов**.

В этих сценариях нам нужен **глобальный механизм для разграничения транзакций, охватывающих несколько участвующих ресурсов** . Это часто называют распределенными транзакциями, и существуют спецификации, которые были предложены для эффективной работы с ними**.

#### **JTA** и **JTS**

**Спецификация XA** — одна из таких спецификаций, которая определяет менеджер транзакций для управления транзакциями между несколькими ресурсами. **Java** имеет достаточно развитую поддержку распределенных транзакций, соответствующую спецификации **XA**, посредством компонентов **JTA** и **JTS**.

**Java `Transaction API` (**JTA**)** - это **API `Java `Enterprise` Edition`,** разработанный в рамках процесса сообщества **Java.** Он **позволяет приложениям `Java` и серверам приложений выполнять распределенные транзакции между ресурсами XA** . **JTA** построен на основе архитектуры **XA** и использует двухфазную фиксацию**.

**JTA** определяет стандартные интерфейсы **Java** между менеджером транзакций и другими сторонами распределённой транзакции:

1. **TransactionManager:** интерфейс, который позволяет серверу приложений разграничивать и контролировать транзакции**.
2. **UserTransaction:** этот интерфейс позволяет прикладной программе явно разграничивать и контролировать транзакции**.
3. **XAResource:** цель этого интерфейса - позволить менеджеру транзакций работать с менеджерами ресурсов для ресурсов, совместимых с **XA.**

### Транзакции в **Spring**

Мы видели, что обработка транзакций - довольно сложная задача, включающая множество шаблонных кодов** и конфигураций**. Более того, каждый ресурс имеет свой собственный способ обработки локальных транзакций**. В **Java JTA** абстрагирует нас от этих вариаций, но дополнительно привносит детали, специфичные для поставщика, и сложность сервера приложений**.

Платформа **Spring** предоставляет нам гораздо более чистый способ обработки транзакций, как локальных, так и глобальных транзакций ресурсов в **Java**. Это вместе с другими преимуществами **Spring** создает убедительные аргументы в пользу использования **Spring** для обработки транзакций. Более того, с помощью **Spring** довольно легко настроить и переключить менеджер транзакций, который может быть как серверным, так и автономным.

**Spring** предоставляет нам эту цельную абстракцию, создавая прокси для методов** с транзакционным кодом**. Прокси управляет состоянием транзакции от имени кода с помощью **TransactionManager:**

Центральным интерфейсом здесь является **PlatformTransactionManager,** который имеет ряд различных реализаций**. Он предоставляет абстракции над **JDBC** (**DataSource**), **JMS**, **JPA**, **JTA** и многими другими ресурсами**.

### Рекомендации

Как мы видели, обработка транзакций, особенно тех, которые охватывают несколько ресурсов, сложна**. Более того**, транзакции по своей сути блокируются, что отрицательно сказывается на задержке и пропускной способности** приложения**. Кроме того, тестирование и поддержка кода с распределенными транзакциями - задача непростая, особенно если транзакция зависит от базового сервера приложений**. В общем, лучше вообще избегать транзакций, если это возможно**!

**Но это далеко от реальности**. Короче говоря, в реальных приложениях у нас часто есть законная потребность в транзакциях**. Хотя переосмыслить архитектуру приложения без транзакций можно,** это не всегда возможно**. Следовательно, мы должны принять определенные рекомендации при работе с транзакциями в **Java,** чтобы улучшить наши приложения:**

1. Одним из фундаментальных изменений, которые нам следует принять, является использование автономных менеджеров транзакций вместо тех, которые предоставляются сервером приложений** . Уже одно это может значительно упростить наше приложение**. Более того, он отлично подходит для облачной микросервисной архитектуры**.
2. Кроме того**, уровень абстракции, такой как `Spring`, может помочь нам сдержать прямое влияние таких поставщиков,** как поставщики **JPA** или **JTA.** Таким образом, это может позволить нам переключаться между поставщиками без особого влияния на нашу бизнес-логику**. Более того, это отнимает у нас низкоуровневые обязанности по управлению состоянием транзакции**.
3. Наконец, мы должны быть осторожны при выборе границы транзакции в нашем коде** . Поскольку транзакции блокируются, всегда лучше максимально ограничивать границы транзакций**. При необходимости мы должны предпочесть программный контроль транзакций декларативному**.

## Руководство по преобразованию **ResultSet** в **JSON**

В некоторых сценариях нам может потребоваться отправить результат запроса к базе данных через вызов **API** в другую систему или платформу обмена сообщениями**. В таких случаях мы часто используем **JSON** в качестве формата обмена данными**.

В этом руководстве мы увидим несколько способов преобразования объекта **JDBC ResultSet** в формат **JSON.**

### Использование библиотеки **JSON-Java**

**Для обработки **JSON** мы используем библиотеку **JSON-`Java` ( org.json ).** Сначала мы добавляем соответствующую зависимость в наш **POM-**файл:**

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20220320</version>
</dependency>
```

**JDBC API** появился раньше современных фреймворков коллекций **Java.**

Поэтому мы не можем использовать методы **foreach iteration** и **Stream.**

Вместо этого нам приходится полагаться на итераторы**. Более того, нам нужно извлечь количество и список имен столбцов из метаданных **ResultSet**.**

Это приводит к базовому циклу, состоящему из формирования объекта **JSON** для каждой строки, добавления объектов в **List** и, наконец, преобразования этого списка в массив **JSON.** Все эти функции доступны в пакете **org.json:**

```java
ResultSetMetaData md = resultSet.getMetaData();
int numCols = md.getColumnCount();
List<String> colNames = IntStream.range(0, numCols)
    .mapToObj(i -> {
        try {
            return md.getColumnName(i + 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return "?";
        }
    })
    .collect(Collectors.toList());

JSONArray result = new JSONArray();
while (resultSet.next()) {
    JSONObject row = new JSONObject();
    colNames.forEach(cn -> {
        try {
            row.put(cn, resultSet.getObject(cn));
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }
    });
    result.add(row);
}
```

Здесь мы сначала запускаем цикл для извлечения имени каждого столбца**. Позже мы используем эти имена столбцов при формировании результирующего объекта **JSON**.**

Во втором цикле мы просматриваем фактические результаты и преобразуем каждый из них в объект **JSON,** используя имена столбцов, которые мы вычислили на предыдущем шаге**. Затем мы добавляем все эти объекты в массив **JSON**.**

Мы исключили из цикла извлечение имен столбцов и количества столбцов**. Это помогает ускорить выполнение**.

**Результирующий **JSON** выглядит следующим образом:**

```json
[
    {
        "Username":"doe1",
        "First name":"John",
        "Id":"7173",
        "Last name":"Doe"
    },
    {
        "Username":"smith3",
        "First name":"Dana",
        "Id":"3722",
        "Last name":"Smith"
    },
    {
        "Username":"john22",
        "First name":"John",
        "Id":"5490",
        "Last name":"Wang"
    }
]
```

### Использование **jOOQ**

**Фреймворк **jOOQ (**Java Object Oriented Querying**)** предоставляет, помимо прочего, набор удобных служебных функций для работы с объектами **JDBC** и **ResultSet.** Во-первых, нам нужно добавить зависимость **jOOQ** в наш **POM-**файл:**

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.11.11</version>
</dependency>
```

После добавления зависимости мы можем использовать однострочное решение для преобразования **ResultSet** в объект **JSON:**

```java
JSONObject result = new JSONObject(DSL.using(dbConnection)
    .fetch(resultSet)
    .formatJSON());
```

**Результирующий элемент **JSON** представляет собой объект, состоящий из двух полей, называемых полями и записями, где поля содержат имена и типы столбцов, а записи содержат фактические данные**. Это немного отличается от предыдущего объекта **JSON** и для нашей таблицы-примера выглядит следующим образом:**

```json
{
    "records":[
        ["doe1","7173","John","Doe"],
        ["smith3","3722","Dana","Smith"],
        ["john22","5490","John","Wang"]
    ],
    "fields":[
        {
            "schema":"PUBLIC",
            "name":"Username",
            "type":"VARCHAR",
            "table":"WORDS"
        },
        {
            "schema":"PUBLIC",
            "name":"Id",
            "type":"VARCHAR",
            "table":"WORDS"
        }
    ]
}
```

## Spring Data JDBC

### Введение в **Spring Data JDBC**

**Spring Data JDBC** - это часть экосистемы **Spring Data**, которая предоставляет абстракцию над **JDBC** для работы с реляционными базами данных. В отличие от **JPA**, **Spring Data JDBC** не предоставляет **ORM** и работает ближе к **SQL**.

### Преимущества **Spring Data JDBC**

1. **Простота**: Минимальная абстракция над **SQL**
2. **Производительность**: Прямой контроль над **SQL** запросами
3. **Прозрачность**: Видны все выполняемые запросы
4. **Гибкость**: Возможность использования сложных **SQL** запросов
5. **Легковесность**: Меньше **overhead** по сравнению с **JPA**

### Настройка **Spring Data JDBC**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jdbc</artifactId>
    <version>3.1.0</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@Configuration
@EnableJdbcRepositories(basePackages = "com.example.repository")
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
    }

    @Bean
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

### Создание сущностей

```java
// Простая сущность
public class Person {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    // Getters and setters
}

// Сущность с составным ключом
public class Order {
    @Id
    private OrderId id;
    private String customerName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;

    // Getters and setters
}

public class OrderId {
    private String orderNumber;
    private String customerId;

    // Getters, setters, equals, hashCode
}
```

### Репозитории

```java
// Базовый репозиторий
public interface PersonRepository extends CrudRepository<Person, Long> {
    // Автоматически генерируемые методы:
    // save(), findById(), findAll(), delete(), etc.
}

// Кастомные методы поиска
public interface PersonRepository extends CrudRepository<Person, Long> {

    // Поиск по имени
    List<Person> findByFirstName(String firstName);

    // Поиск по фамилии и имени
    List<Person> findByLastNameAndFirstName(String lastName, String firstName);

    // Поиск с пагинацией
    Page<Person> findByLastName(String lastName, Pageable pageable);

    // Поиск с сортировкой
    List<Person> findByBirthDateAfter(LocalDate date, Sort sort);

    // Кастомные запросы
    @Query("SELECT * FROM person WHERE birth_date BETWEEN :startDate AND :endDate")
    List<Person> findBornBetween(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    // Нативные SQL запросы
    @Query(value = "SELECT * FROM person WHERE LENGTH(first_name) > :minLength",
           nativeQuery = true)
    List<Person> findByFirstNameLongerThan(@Param("minLength") int minLength);
}

// Репозиторий с кастомной логикой
public interface PersonRepository extends CrudRepository<Person, Long>, PersonRepositoryCustom {

    // Стандартные методы CRUD

    // Кастомные методы
    List<Person> findByFirstNameIgnoreCase(String firstName);
}

public interface PersonRepositoryCustom {
    List<Person> findByCustomCriteria(String criteria);
}

public class PersonRepositoryImpl implements PersonRepositoryCustom {

    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Person> findByCustomCriteria(String criteria) {
        String sql = "SELECT * FROM person WHERE first_name LIKE :criteria OR last_name LIKE :criteria";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("criteria", "%" + criteria + "%");

        return jdbcOperations.query(sql, params, new BeanPropertyRowMapper<>(Person.class));
    }
}
```

### Кастомные запросы и **RowMapper**

```java
@Repository
public class PersonCustomRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public PersonCustomRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public List<PersonSummary> findPersonSummaries() {
        String sql = """
                SELECT p.first_name, p.last_name,
                       COUNT(o.id) as order_count,
                       SUM(o.total_amount) as total_spent
                FROM person p
                LEFT JOIN orders o ON p.id = o.customer_id
                GROUP BY p.id, p.first_name, p.last_name
                ORDER BY total_spent DESC
                """;

        return jdbcOperations.query(sql, new PersonSummaryRowMapper());
    }

    public PersonDetails findPersonDetails(Long personId) {
        String personSql = "SELECT * FROM person WHERE id = :id";
        String ordersSql = "SELECT * FROM orders WHERE customer_id = :customerId ORDER BY order_date DESC";

        Person person = jdbcOperations.queryForObject(personSql,
                new MapSqlParameterSource("id", personId),
                new BeanPropertyRowMapper<>(Person.class));

        List<Order> orders = jdbcOperations.query(ordersSql,
                new MapSqlParameterSource("customerId", personId),
                new BeanPropertyRowMapper<>(Order.class));

        return new PersonDetails(person, orders);
    }

    public Page<Person> findPersonsWithPagination(String searchTerm, Pageable pageable) {
        String countSql = """
                SELECT COUNT(*) FROM person
                WHERE (:searchTerm IS NULL OR first_name ILIKE :searchTerm OR last_name ILIKE :searchTerm)
                """;

        String dataSql = """
                SELECT * FROM person
                WHERE (:searchTerm IS NULL OR first_name ILIKE :searchTerm OR last_name ILIKE :searchTerm)
                ORDER BY {sort}
                LIMIT :limit OFFSET :offset
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("searchTerm", searchTerm != null ? "%" + searchTerm + "%" : null)
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        // Выполняем count запрос
        Long total = jdbcOperations.queryForObject(countSql, params, Long.class);

        // Выполняем data запрос с динамической сортировкой
        String sortedSql = dataSql.replace("{sort}",
                pageable.getSort().isSorted() ?
                pageable.getSort().stream()
                        .map(order -> order.getProperty() + " " + order.getDirection())
                        .collect(Collectors.joining(", ")) :
                "id ASC");

        List<Person> persons = jdbcOperations.query(sortedSql, params, new BeanPropertyRowMapper<>(Person.class));

        return new PageImpl<>(persons, pageable, total);
    }
}

public class PersonSummaryRowMapper implements RowMapper<PersonSummary> {

    @Override
    public PersonSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PersonSummary.builder()
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .orderCount(rs.getInt("order_count"))
                .totalSpent(rs.getBigDecimal("total_spent"))
                .build();
    }
}
```

### Отношения между сущностями

```java
// One-to-One отношение
public class User {
    @Id
    private Long id;
    private String username;
    private String email;

    @MappedCollection(idColumn = "user_id")
    private UserProfile profile;
}

public class UserProfile {
    @Id
    private Long id;
    private String bio;
    private String avatarUrl;
    private LocalDate birthDate;
}

// One-to-Many отношение
public class Author {
    @Id
    private Long id;
    private String name;

    @MappedCollection(idColumn = "author_id")
    private Set<Book> books;
}

public class Book {
    @Id
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
}

// Many-to-Many через промежуточную таблицу
public class Student {
    @Id
    private Long id;
    private String name;

    @MappedCollection(idColumn = "student_id", keyColumn = "course_id")
    private Set<Course> courses;
}

public class Course {
    @Id
    private Long id;
    private String name;
    private String description;
}
```

### Агрегаты и **DDD**

```java
// Агрегат Order
public class Order {
    @Id
    private OrderId id;
    private String customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;

    @MappedCollection(idColumn = "order_id")
    private List<OrderItem> items;

    public void addItem(Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(String productId) {
        this.items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.totalAmount = this.items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

public class OrderItem {
    @Id
    private Long id;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
}

// Репозиторий агрегата
public interface OrderRepository extends CrudRepository<Order, OrderId> {

    @Query("SELECT * FROM orders WHERE customer_id = :customerId ORDER BY order_date DESC")
    List<Order> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT * FROM orders WHERE status = :status AND order_date >= :since")
    List<Order> findByStatusAndDate(@Param("status") OrderStatus status,
                                   @Param("since") LocalDateTime since);

    // Кастомные методы для бизнес-логики
    default Optional<Order> findOrderByIdAndCustomer(OrderId orderId, String customerId) {
        return findById(orderId)
                .filter(order -> customerId.equals(order.getCustomerId()));
    }

    default BigDecimal getTotalRevenueForCustomer(String customerId) {
        return findByCustomerId(customerId).stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Транзакции

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Валидация
        validateOrderRequest(request);

        // Создание заказа
        Order order = new Order();
        order.setId(new OrderId(request.getOrderNumber(), request.getCustomerId()));
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Добавление товаров
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.getProductId()));

            // Проверка наличия на складе
            inventoryService.checkAvailability(product.getId(), itemRequest.getQuantity());

            order.addItem(product, itemRequest.getQuantity());
        }

        // Сохранение заказа
        Order savedOrder = orderRepository.save(order);

        // Резервирование товаров
        try {
            inventoryService.reserveItems(savedOrder);
        } catch (Exception e) {
            // Откат транзакции
            throw new OrderCreationException("Failed to reserve inventory", e);
        }

        return savedOrder;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void confirmOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order is not in PENDING state");
        }

        // Подтверждение заказа
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Списание товаров со склада
        inventoryService.confirmReservation(orderId);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void cancelOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Cannot cancel shipped order");
        }

        // Отмена заказа
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Освобождение зарезервированных товаров
        inventoryService.releaseReservation(orderId);
    }
}
```

### Аудит

```java
@Configuration
@EnableJdbcAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .or(() -> Optional.of("system"));
    }
}

public class AuditableEntity {

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Version
    @Column(name = "version")
    private Long version;
}

@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sku;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    // Getters and setters
}
```

### Интеграция с **Spring Boot**

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: myapp
    password: secret
    driver-class-name: org.postgresql.Driver

  data:
    jdbc:
      repositories:
        enabled: true

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql

logging:
  level:
    org.springframework.data.jdbc: DEBUG
    org.springframework.jdbc: DEBUG
```

```java
@SpringBootApplication
@EnableJdbcRepositories
@EnableTransactionManagement
@EnableJdbcAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
```

### Тестирование

```java
@SpringBootTest
@Sql(scripts = "/test-data.sql")
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

    @Test
    public void testFindByFirstName() {
        // Given
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        personRepository.save(person);

        // When
        List<Person> found = personRepository.findByFirstName("John");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    public void testCustomRepositoryMethod() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Johnson");

        personRepository.save(person1);
        personRepository.save(person2);

        // When
        List<Person> found = ((PersonRepositoryImpl) personRepository).findByCustomCriteria("John");

        // Then
        assertThat(found).hasSize(2);
    }

    @Test
    public void testTransactionalBehavior() {
        // Given
        Person person = new Person();
        person.setFirstName("Transactional");
        person.setLastName("Test");

        // When - успешная транзакция
        personRepository.save(person);

        // Then
        Optional<Person> saved = personRepository.findById(person.getId());
        assertThat(saved).isPresent();
    }

    @Test
    public void testComplexQuery() {
        // Given - test data loaded via @Sql

        // When
        List<PersonSummary> summaries = personRepository.findPersonSummaries();

        // Then
        assertThat(summaries).isNotEmpty();
        assertThat(summaries.get(0).getOrderCount()).isGreaterThanOrEqualTo(0);
    }
}

@RunWith(SpringRunner.class)
@DataJdbcTest
public class PersonRepositorySliceTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testRepositoryWithDataJdbcSlice() {
        // Этот тест использует только JDBC слой
        Person person = new Person();
        person.setFirstName("Slice");
        person.setLastName("Test");

        Person saved = personRepository.save(person);
        assertThat(saved.getId()).isNotNull();
    }
}

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class PersonRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertySource registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testWithRealDatabase() {
        Person person = new Person();
        person.setFirstName("Integration");
        person.setLastName("Test");

        Person saved = personRepository.save(person);
        assertThat(saved.getId()).isNotNull();

        List<Person> found = personRepository.findByFirstName("Integration");
        assertThat(found).hasSize(1);
    }
}
```

### Производительность

```java
@Repository
public class OptimizedPersonRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public OptimizedPersonRepository(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    // Batch insert для производительности
    public void saveAllBatch(List<Person> persons) {
        String sql = "INSERT INTO person (first_name, last_name, birth_date) VALUES (:firstName, :lastName, :birthDate)";

        SqlParameterSource[] batch = persons.stream()
                .map(person -> new MapSqlParameterSource()
                        .addValue("firstName", person.getFirstName())
                        .addValue("lastName", person.getLastName())
                        .addValue("birthDate", person.getBirthDate()))
                .toArray(SqlParameterSource[]::new);

        jdbcOperations.batchUpdate(sql, batch);
    }

    // Streaming для больших результатов
    public Stream<Person> streamAll() {
        String sql = "SELECT * FROM person";

        return jdbcOperations.queryForStream(sql, new MapSqlParameterSource(),
                new BeanPropertyRowMapper<>(Person.class));
    }

    // Использование ResultSet для минимального mapping
    public List<PersonName> findAllNames() {
        String sql = "SELECT first_name, last_name FROM person";

        return jdbcOperations.query(sql, (rs, rowNum) -> new PersonName(
                rs.getString("first_name"),
                rs.getString("last_name")
        ));
    }

    // Connection callback для низкоуровневого контроля
    public void executeWithConnection(String sql, Consumer<ResultSet> resultHandler) {
        jdbcOperations.getJdbcOperations().execute((ConnectionCallback<Void>) connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    resultHandler.accept(rs);
                }
            }
            return null;
        });
    }
}
```

### Лучшие практики

#### 1. Выбор между **Spring Data JDBC** и **JPA**

**Используйте Spring Data JDBC когда:**
- Вам нужна максимальная производительность
- Вы хотите полный контроль над **SQL**
- Вам не нужны сложные отношения между сущностями
- Вы предпочитаете работать ближе к базе данных
- Вам важна простота и прозрачность

**Используйте `JPA` когда:**
- У вас сложная доменная модель с отношениями
- Вам нужна ленивая загрузка
- Вы хотите абстрагироваться от **SQL**
- Вам важны готовые решения для кэширования

#### 2. Правильное использование транзакций

```java
@Service
public class TransactionBestPractices {

    @Autowired
    private TransactionTemplate transactionTemplate;

    // Правильное использование декларативных транзакций
    @Transactional
    public void processOrder(Order order) {
        validateOrder(order);
        saveOrder(order);
        updateInventory(order);
        sendNotification(order);
    }

    // Для сложной логики используйте программные транзакции
    public void complexOrderProcessing(Order order) {
        transactionTemplate.execute(status -> {
            try {
                validateOrder(order);
                saveOrder(order);

                // Дополнительная бизнес-логика
                processPayment(order);
                updateInventory(order);

                return order;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    // Избегайте длинных транзакций
    @Transactional
    public void badLongTransaction() {
        List<Order> orders = orderRepository.findAll(); // Может быть много данных

        for (Order order : orders) {
            // Долгая обработка в транзакции
            processOrder(order);
            sendEmail(order); // Может быть медленно
        }
    }

    // Лучше: обрабатывайте по частям
    public void goodBatchProcessing() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            processOrderInSeparateTransaction(order);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOrderInSeparateTransaction(Order order) {
        processOrder(order);
        sendEmail(order);
    }
}
```

#### 3. Оптимизация запросов

```java
@Repository
public class QueryOptimizationRepository {

    // ПЛОХО: N+1 проблема
    public List<OrderWithItems> findOrdersWithItemsBad() {
        List<Order> orders = jdbcOperations.query(
                "SELECT * FROM orders",
                new BeanPropertyRowMapper<>(Order.class));

        for (Order order : orders) {
            List<OrderItem> items = jdbcOperations.query(
                    "SELECT * FROM order_items WHERE order_id = ?",
                    new Object[]{order.getId()},
                    new BeanPropertyRowMapper<>(OrderItem.class));
            order.setItems(items);
        }

        return orders;
    }

    // ХОРОШО: JOIN запрос
    public List<OrderWithItems> findOrdersWithItemsGood() {
        String sql = """
                SELECT o.id, o.customer_id, o.order_date, o.total_amount,
                       i.id as item_id, i.product_id, i.quantity, i.unit_price
                FROM orders o
                LEFT JOIN order_items i ON o.id = i.order_id
                ORDER BY o.id, i.id
                """;

        Map<Long, OrderWithItems> orderMap = new LinkedHashMap<>();

        jdbcOperations.query(sql, rs -> {
            Long orderId = rs.getLong("id");

            OrderWithItems order = orderMap.computeIfAbsent(orderId, id -> {
                OrderWithItems o = new OrderWithItems();
                o.setId(id);
                o.setCustomerId(rs.getString("customer_id"));
                o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                o.setTotalAmount(rs.getBigDecimal("total_amount"));
                o.setItems(new ArrayList<>());
                return o;
            });

            Long itemId = rs.getLong("item_id");
            if (itemId != null) {
                OrderItem item = new OrderItem();
                item.setId(itemId);
                item.setProductId(rs.getString("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                order.getItems().add(item);
            }
        });

        return new ArrayList<>(orderMap.values());
    }

    // Использование индексов и оптимизация запросов
    public List<Person> findPersonsOptimized(String searchTerm, Pageable pageable) {
        String sql = """
                SELECT * FROM person
                WHERE first_name ILIKE :searchTerm OR last_name ILIKE :searchTerm
                ORDER BY last_name, first_name
                LIMIT :limit OFFSET :offset
                """;

        // Убедитесь, что есть индекс на (last_name, first_name)
        // CREATE INDEX idx_person_name ON person (last_name, first_name);

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("searchTerm", "%" + searchTerm + "%")
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        return jdbcOperations.query(sql, params, new BeanPropertyRowMapper<>(Person.class));
    }
}
```

#### 4. Безопасность

```java
@Repository
public class SecureRepository {

    // Используйте PreparedStatement для предотвращения SQL injection
    public List<Person> findByNameSecure(String name) {
        // ПЛОХО: уязвимо для SQL injection
        String badSql = "SELECT * FROM person WHERE name = '" + name + "'";
        // jdbcOperations.query(badSql, ...);

        // ХОРОШО: используйте параметры
        String goodSql = "SELECT * FROM person WHERE first_name = :name OR last_name = :name";
        SqlParameterSource params = new MapSqlParameterSource("name", name);

        return jdbcOperations.query(goodSql, params, new BeanPropertyRowMapper<>(Person.class));
    }

    // Ограничение количества результатов
    public List<Person> findLimitedResults(String searchTerm, int maxResults) {
        String sql = "SELECT * FROM person WHERE first_name ILIKE :searchTerm LIMIT :limit";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("searchTerm", "%" + searchTerm + "%")
                .addValue("limit", Math.min(maxResults, 1000)); // Максимум 1000 результатов

        return jdbcOperations.query(sql, params, new BeanPropertyRowMapper<>(Person.class));
    }

    // Аудит всех изменений
    @Autowired
    private AuditService auditService;

    public Person saveWithAudit(Person person) {
        Person saved = jdbcOperations.update(
                "INSERT INTO person (first_name, last_name, birth_date) VALUES (?, ?, ?)",
                person.getFirstName(), person.getLastName(), person.getBirthDate());

        auditService.logChange("PERSON_CREATED", person.getId(), "Person created: " + person.getFirstName());

        return saved;
    }
}
```

#### 5. Мониторинг и метрики

```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> jdbcMetrics() {
        return registry -> registry.config()
                .commonTags("component", "jdbc");
    }
}

@Repository
public class MonitoredRepository {

    @Autowired
    private MeterRegistry meterRegistry;

    public List<Person> findAllMonitored() {
        Timer.Sample sample = Timer.start(meterRegistry);
        Counter.builder("repository.calls")
                .tag("method", "findAll")
                .register(meterRegistry)
                .increment();

        try {
            List<Person> result = jdbcOperations.query(
                    "SELECT * FROM person",
                    new BeanPropertyRowMapper<>(Person.class));

            sample.stop(Timer.builder("repository.execution.time")
                    .tag("method", "findAll")
                    .register(meterRegistry));

            return result;

        } catch (Exception e) {
            sample.stop(Timer.builder("repository.execution.error")
                    .tag("method", "findAll")
                    .register(meterRegistry));

            throw e;
        }
    }

    public Person saveWithMetrics(Person person) {
        return Timer.builder("repository.save.time")
                .tag("entity", "person")
                .register(meterRegistry)
                .recordCallable(() -> {
                    Person saved = jdbcOperations.update(
                            "INSERT INTO person (first_name, last_name, birth_date) VALUES (?, ?, ?)",
                            person.getFirstName(), person.getLastName(), person.getBirthDate());

                    meterRegistry.counter("repository.save.count", "entity", "person").increment();

                    return saved;
                });
    }
}
```

### Заключение

**Spring Data JDBC** предоставляет мощный и эффективный способ работы с реляционными базами данных, сохраняя контроль над **SQL** и обеспечивая высокую производительность. Он идеально подходит для приложений, где важны:**

- Прозрачность выполняемых запросов
- Максимальная производительность
- Простота и предсказуемость
- Полный контроль над **mapping** сущностей
- Интеграция с существующими **SQL** знаниями

В отличие от **JPA**, **Spring Data JDBC** не пытается абстрагировать вас от базы данных, а скорее предоставляет удобные инструменты для работы с ней, сохраняя все преимущества **Spring** экосистемы.
