---
title: "Spring Data JPA/Hibernate"
description: "Полное руководство по Spring Data JPA и Hibernate: репозитории, запросы, транзакции, кэширование, оптимизация производительности и best practices"
tags:
  - spring-data-jpa
  - hibernate
  - orm
  - database
  - jpa
  - sql
  - transactions
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "databases/postgres-basics.md"]
next: ["spring/spring-boot.md", "interview/spring-data-jpa.md"]
updated: "2026-02-06"
---

# Spring Data JPA / Hibernate

## Полезные ссылки

### Официальная документация

- [**Spring** Documentation](https://spring.io/projects/spring-framework)
- [**Spring Framework** Reference](https://docs.spring.io/spring-framework/reference/)

### **Baeldung**

- [**Spring** Tutorial](https://www.baeldung.com/spring-tutorial)

### См. также

- [[spring-boot|**Spring Boot**]] — основы **Spring Boot**
- [[spring-hibernate|**Spring Hibernate**]] — интеграция **Hibernate**
- [[postgres-basics|**PostgreSQL**]] — работа с **PostgreSQL**
- [[maven-advanced|**Maven**]] — система сборки

## Содержание

- [Spring Data JPA/Hibernate](#spring-data-jpahibernate)
- [Программная настройка источника данных](#программная-настройка-источника-данных)
  - [Тестирование](#тестирование)
- [Руководство по JPA](#руководство-по-jpa)
  - [Настройка источника данных](#настройка-источника-данных)
  - [Интерфейсы репозиториев](#интерфейсы-репозиториев)
  - [Преобразование исключений](#преобразование-исключений)
  - [Включение JPA репозиториев](#включение-jpa-репозиториев)
  - [Запросы JPQL и Native SQL](#запросы-jpql-и-native-sql)
  - [Сортировка](#сортировка)
  - [Пагинация](#пагинация)
  - [Параметры запросов](#параметры-запросов)
  - [Динамические запросы](#динамические-запросы)
- [Транзакции](#транзакции)
  - [Управление транзакциями в JPA](#управление-транзакциями-в-jpa)
  - [Управление транзакциями в Spring](#управление-транзакциями-в-spring)
- [Propagation (распространение) и Isolation (изоляция) транзакций](#propagation-распространение-и-isolation-изоляция-транзакций)
  - [Propagation](#propagation)
  - [Isolation](#isolation)
- [Persistence Context (контекст персистентности)](#persistence-context-контекст-персистентности)
  - [Типы Persistence Context](#типы-persistence-context)
  - [Состояния сущностей в Hibernate](#состояния-сущностей-в-hibernate)
- [Data Access Object (DAO)](#data-access-object-dao)
- [Упрощение DAO](#упрощение-dao)
- [Руководство по @Entity](#руководство-по-entity)
  - [Первичный ключ](#первичный-ключ)
  - [Таблицы и схемы](#таблицы-и-схемы)
  - [Столбцы](#столбцы)
  - [@Transient](#transient)
  - [@Temporal](#temporal)
  - [@Enumerated](#enumerated)
- [Значения столбца по умолчанию](#значения-столбца-по-умолчанию)
- [Руководство по @Basic](#руководство-по-basic)
  - [Атрибуты @Basic](#атрибуты-basic)
  - [Различия между @Basic и @Column](#различия-между-basic-и-column)
- [Разница между @Size, @Length и @Column (length = значение)](#разница-между-size-length-и-column-length-значение)
  - [Различия](#различия)
- [Руководство по @Embedded и @Embeddable](#руководство-по-embedded-и-embeddable)
  - [Переопределение атрибутов столбцов](#переопределение-атрибутов-столбцов)
- [@NotNull против @Column (nullable = false)](#notnull-против-column-nullable-false)
- [Определение уникальных ограничений](#определение-уникальных-ограничений)
  - [Использование @Column(unique = true)](#использование-columnunique-true)
  - [Использование @UniqueConstraint](#использование-uniqueconstraint)
- [Интерфейс Serializable и сущности](#интерфейс-serializable-и-сущности)
- [Отношения "один-к-одному"](#отношения-один-к-одному)
  - [Одностороннее отношение](#одностороннее-отношение)
  - [Двустороннее отношение](#двустороннее-отношение)
- [Отношения "многие-ко-многим"](#отношения-многие-ко-многим)
- [Разница между @JoinColumn и mappedBy](#разница-между-joincolumn-и-mappedby)
  - [@JoinColumn](#joincolumn)
  - [mappedBy](#mappedby)
- [Сопоставление одного объекта с несколькими таблицами](#сопоставление-одного-объекта-с-несколькими-таблицами)
- [Обзор каскадных типов JPA/Hibernate](#обзор-каскадных-типов-jpahibernate)
- [Наследование](#наследование)
  - [SINGLE_TABLE (одна таблица на всю иерархию)](#single_table-одна-таблица-на-всю-иерархию)
  - [JOINED (таблица для каждого класса)](#joined-таблица-для-каждого-класса)
  - [TABLE_PER_CLASS (таблица для каждого класса)](#table_per_class-таблица-для-каждого-класса)
- [Руководство по @FetchMode и @FetchType](#руководство-по-fetchmode-и-fetchtype)
  - [FetchMode.SELECT](#fetchmodeselect)
  - [FetchMode.JOIN](#fetchmodejoin)
  - [FetchMode.SUBSELECT](#fetchmodesubselect)
  - [Важные замечания](#важные-замечания)
- [Пакетная вставка/обновление](#пакетная-вставкаобновление)
  - [Настройка пакетной обработки](#настройка-пакетной-обработки)
- [Разница между save() и saveAndFlush()](#разница-между-save-и-saveandflush)
- [Аудирование](#аудирование)
- [Частичное обновление данных](#частичное-обновление-данных)
  - [Подход 1: Загрузка и обновление](#подход-1-загрузка-и-обновление)
  - [Подход 2: Использование MapStruct с DTO](#подход-2-использование-mapstruct-с-dto)
  - [Подход 3: Пользовательский запрос](#подход-3-пользовательский-запрос)
- [Руководство по JPA Buddy](#руководство-по-jpa-buddy)
  - [Основные функции](#основные-функции)
- [Руководство по Specifications](#руководство-по-specifications)
  - [Настройка](#настройка)
  - [Использование спецификаций](#использование-спецификаций)
- [Сравнение между JPA и JDBC](#сравнение-между-jpa-и-jdbc)
  - [JDBC](#jdbc)
  - [JPA](#jpa)
- [Руководство по @Query](#руководство-по-query)
  - [JPQL запросы](#jpql-запросы)
  - [Нативные SQL запросы](#нативные-sql-запросы)
  - [Обновление данных](#обновление-данных)
  - [Использование параметров](#использование-параметров)
    - [Индексированные параметры](#индексированные-параметры)
    - [Именованные параметры](#именованные-параметры)
  - [Пагинация с @Query](#пагинация-с-query)
  - [Использование Collection в запросах](#использование-collection-в-запросах)
- [Использование нескольких файлов SQL для импорта](#использование-нескольких-файлов-sql-для-импорта)
- [Кэш второго уровня](#кэш-второго-уровня)
  - [Настройка кэша второго уровня в Hibernate](#настройка-кэша-второго-уровня-в-hibernate)
  - [Использование @Cacheable](#использование-cacheable)
  - [Стратегии кэширования](#стратегии-кэширования)
- [Продвинутые возможности JPA](#продвинутые-возможности-jpa)
  - [Criteria API для динамических запросов](#criteria-api-для-динамических-запросов)
  - [Entity Graphs для оптимизации загрузки](#entity-graphs-для-оптимизации-загрузки)
  - [Projections для оптимизации запросов](#projections-для-оптимизации-запросов)
  - [Specifications для сложных запросов](#specifications-для-сложных-запросов)
- [Оптимизация производительности](#оптимизация-производительности)
  - [N+1 проблема и ее решения](#n1-проблема-и-ее-решения)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Кэширование второго уровня](#кэширование-второго-уровня)
- [Распространенные проблемы и решения](#распространенные-проблемы-и-решения)
  - [Проблема: LazyInitializationException](#проблема-lazyinitializationexception)
  - [Проблема: Optimistic Lock Exception](#проблема-optimistic-lock-exception)
  - [Проблема: Memory leaks с EntityManager](#проблема-memory-leaks-с-entitymanager)
- [Миграция с Hibernate/JPA](#миграция-с-hibernatejpa)
  - [Переход с чистого Hibernate на Spring Data JPA](#переход-с-чистого-hibernate-на-spring-data-jpa)
  - [Миграция с JDBC на JPA](#миграция-с-jdbc-на-jpa)
- [Лучшие практики для Spring Data JPA](#лучшие-практики-для-spring-data-jpa)
  - [1. Используйте правильные типы отношений](#1-используйте-правильные-типы-отношений)
  - [2. Правильное использование @Transactional](#2-правильное-использование-transactional)
  - [3. Используйте DTO для сложных запросов](#3-используйте-dto-для-сложных-запросов)
  - [4. Правильное управление связями](#4-правильное-управление-связями)
- [Заключение](#заключение)
  - [Ключевые принципы:](#ключевые-принципы)
  - [Рекомендуемые ресурсы:](#рекомендуемые-ресурсы)

## Программная настройка источника данных

Мы реализуем простой уровень репозитория, который будет выполнять операции **CRUD** с некоторыми объектами **JPA**.

**Зависимость **Spring Boot Starter Data JPA** (**pom.xml**):**

```xml
<!-- Spring Boot Starter Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Иногда требуется более высокий уровень реализации - определить фабричный метод **DataSource** и поместить его в класс, помеченный аннотацией **@Configuration**:**

```java
// Конфигурация DataSource программным способом
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource getDataSource() {
        // Создание DataSource через builder
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}
```

**Конечно, это также можно частично экстернализовать нашу **DataSource** конфигурацию. Например, мы могли бы определить некоторые базовые свойства **DataSource** в нашем фабричном методе:**

```java
// Частичная конфигурация - остальное из application.properties
@Bean
public DataSource getDataSource() {
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.username("SA");
    dataSourceBuilder.password("");
    return dataSourceBuilder.build();
}
```

**Затем мы можем указать несколько дополнительных в файле **application.properties**:**

```properties
# DataSource: H2 in-memory для тестов
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.driver-class-name=org.h2.Driver
```

Свойства, определенные во внешнем источнике, таком как указанный выше файл **application.properties**, или через класс, аннотированный с помощью **@ConfigurationProperties**, переопределят те, которые определены в **Java API**.

### Тестирование

Тестирование нашего пользовательского **DataSource** конфигурации очень просто. Весь процесс сводится к созданию объекта **JPA**, определению базового интерфейса репозитория и тестированию уровня репозитория.

```java
// JPA-сущность User для таблицы users
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;   // Автогенерируемый ID
    private String name;
    private String email;
}
```

Затем нам нужно реализовать базовый уровень репозитория, который позволяет нам выполнять операции **CRUD** с экземплярами класса сущности **User**, определенного выше.

**Поскольку мы используем Spring Data JPA**, нам не нужно создавать собственную реализацию **DAO** с нуля. Нам просто нужно расширить интерфейс **CrudRepository**, чтобы получить работающую реализацию репозитория:**

```java
// Репозиторий с базовыми CRUD-операциями через CrudRepository
@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
```

**Интеграционный тест:**

```java
// Интеграционный тест репозитория с @DataJpaTest
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    
    // Тест сохранения пользователя
    @Test
    public void whenCalledSave_thenCorrectNumberOfUsers() {
        userRepository.save(new User("Bob", "bob@domain.com"));
        List<User> users = (List<User>) userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
    }
}
```

## Руководство по **JPA**

**Java `Persistence API` (**JPA**)** - это спецификация **Java**, которую можно использовать для устранения разрыва между объектно-ориентированными моделями предметной области и системами реляционных баз данных. Итак, существует несколько реализаций **JPA** от третьих сторон, таких как **Hibernate**, **EclipseLink** и **iBatis**.

### Настройка источника данных

**Чтобы настроить источник данных с помощью файла свойств, мы должны установить свойства с префиксом **spring.datasource**:**

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=mysqluser
spring.datasource.password=mysqlpass
spring.datasource.url=jdbc:mysql://localhost:3306/myDb?createDatabaseIfNotExist=true
```

### Интерфейсы репозиториев

Чтобы начать использовать модель программирования **Spring Data** с **JPA**, интерфейс **DAO** должен расширить специфический для **JPA** интерфейс репозитория **JpaRepository**. Это позволит **Spring Data** найти этот интерфейс и автоматически создать для него реализацию.

Как уже говорилось, при реализации одного из интерфейсов репозитория в **DAO** уже будут определены и реализованы некоторые базовые методы (**и запросы**) CRUD**.

**Для определения более конкретных методов доступа **Spring JPA** поддерживает несколько параметров:**

- просто определите новый метод в интерфейсе
- предоставить фактический запрос **JPQL** с помощью аннотации @**Query**
- использовать более продвинутые спецификации и поддержку **Querydsl** в **Spring Data**
- определять пользовательские запросы через именованные запросы **JPA**

**Пример с **@Query**:**

```java
// JPQL-запрос с параметром и регистронезависимым поиском
@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
Foo retrieveByName(@Param("name") String name);
```

### Преобразование исключений

Преобразование исключений по-прежнему разрешено с помощью аннотации **@Repository** в **DAO**. Эта аннотация позволяет постпроцессору **bean-компонента Spring** сообщать всем **bean-компонентам @Repository** обо всех экземплярах **PersistenceExceptionTranslator**, обнаруженных в контейнере, и обеспечивать преобразование исключений, как и раньше.

**Давайте проверим трансляцию исключения с помощью интеграционного теста:**

```java
// Тест трансляции исключения - ожидается DataIntegrityViolationException
@Test(expected = DataIntegrityViolationException.class)
public void givenFooHasNoName_whenInvalidEntityIsCreated_thenDataException() {
    service.create(new Foo());  // Создание невалидной сущности
}
```

### Включение **JPA** репозиториев

**Чтобы активировать поддержку репозитория **Spring JPA**, мы можем использовать аннотацию **@EnableJpaRepositories** и указать пакет, содержащий интерфейсы **DAO**:**

```java
// Включение JPA-репозиториев с указанием базового пакета
@EnableJpaRepositories(basePackages = "com.baeldung.spring.data.persistence.repository")
public class PersistenceConfig {
}
```

**Мы также можем использовать **JPA**-зависимость **Spring `Boot Starter` Data**, которая автоматически настроит для нас **DataSource**:**

```xml
<!-- Spring Boot Starter Data JPA: автоконфигурация DataSource и JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.5.11</version>
</dependency>
```

В результате, просто выполняя эти зависимости, наше приложение запускается и работает, и мы можем использовать его для других операций с базой данных.

Явная конфигурация для стандартного приложения **Spring** теперь включена как часть автоконфигурации **Spring Boot**.

Конечно, мы можем изменить автоконфигурацию, добавив нашу индивидуальную явную конфигурацию.

**Spring Boot** предоставляет простой способ сделать это с помощью свойств в файле **application.properties**:**

```properties
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

В этом примере мы изменили **URL-**адрес подключения и учетные данные.

### Запросы **JPQL** и **Native SQL**

По умолчанию определение запроса использует **JPQL.**

**Давайте посмотрим на простой метод репозитория, который возвращает активные объекты **User** из базы данных:**

```java
// JPQL-запрос для получения активных пользователей
@Query("SELECT u FROM User u WHERE u.status = 1")
Collection<User> findAllActiveUsers();
```

**Мы также можем использовать собственный **SQL** для определения нашего запроса. Все, что нам нужно сделать, это установить для атрибута **nativeQuery** значение **true** и определить собственный **SQL-**запрос в атрибуте **value** аннотации:**

```java
// Native SQL запрос (nativeQuery = true)
@Query(value = "SELECT * FROM USERS u WHERE u.status = 1", nativeQuery = true)
Collection<User> findAllActiveUsersNative();
```

### Сортировка

**Для методов, которые мы получаем из коробки, таких как **findAll (**Sort**)** или тех, которые генерируются путем синтаксического анализа сигнатур методов, мы можем использовать только свойства объекта для определения нашей сортировки:**

```java
// Примеры сортировки через Sort API
userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));  // По возрастанию
userRepository.findAllUsers(Sort.by("name"));
```

**И поскольку мы использовали аннотацию **`@Query`,** мы можем использовать тот же метод для получения отсортированного списка пользователей по длине их имен:**

```java
// JpaSort.unsafe() для сортировки по SQL-выражению
userRepository.findAllUsers(JpaSort.unsafe("LENGTH(name)"));
```

Очень важно использовать **JpaSort.unsafe()** для создания экземпляра объекта **Sort.**

### Пагинация

Разбиение на страницы позволяет нам возвращать только часть результата на странице. Это полезно, например, при навигации по нескольким страницам данных на веб-странице.

Еще одно преимущество разбиения на страницы состоит в том, что объем данных, отправляемых с сервера клиенту, сведен к минимуму. Отправляя меньшие фрагменты данных, мы обычно видим улучшение производительности.

**Использование разбивки на страницы в определении запроса **JPQL** очень просто:**

```java
// Пагинация через Pageable параметр
@Query(value = "SELECT u FROM User u ORDER BY id")
Page<User> findAllUsersWithPagination(Pageable pageable);
```

Мы можем включить разбиение на страницы для собственных запросов, объявив дополнительный атрибут **countQuery.**

**Это определяет **SQL,** который нужно выполнить для подсчета количества строк во всем результате:**

```java
// Native query с пагинацией и отдельным count-запросом
@Query(
    value = "SELECT * FROM Users ORDER BY id",
    countQuery = "SELECT count(*) FROM Users",
    nativeQuery = true)
Page<User> findAllUsersWithPagination(Pageable pageable);
```

### Параметры запросов

**Для индексированных параметров в **JPQL `Spring` Data** будет передавать параметры метода в запрос в том же порядке, в котором они указаны в объявлении метода:**

```java
// Индексированные параметры (?1, ?2) в порядке объявления
@Query("SELECT u FROM User u WHERE u.status =?1")
User findUserByStatus(Integer status);

@Query("SELECT u FROM User u WHERE u.status =?1 and u.name =?2")
User findUserByStatusAndName(Integer status, String name);
```

Индексированные параметры для собственных запросов работают точно так же, как для **JPQL:**

```java
// Индексированный параметр в native SQL
@Query(
    value = "SELECT * FROM Users u WHERE u.status =?1",
    nativeQuery = true)
User findUserByStatusNative(Integer status);
```

Мы также можем передавать параметры метода в запрос, используя именованные параметры. Мы определяем их с помощью аннотации **@Param** внутри объявления метода репозитория.

Каждый параметр, помеченный **@Param**, должен иметь строку значения, соответствующую имени соответствующего параметра запроса **JPQL** или **SQL.** Запрос с именованными параметрами легче читать и меньше подвержен ошибкам в случае необходимости рефакторинга запроса.

```java
// Именованные параметры JPQL через :status и :name
@Query("SELECT u FROM User u WHERE u.status =:status and u.name =:name")
User findUserByStatusAndNameNamedParams(
    @Param("status") Integer status,
    @Param("name") String name);
```

**Обратите внимание, что в приведенном выше примере мы определили наши параметры **SQL-**запроса и метода с одинаковыми именами, но это не требуется, если строки значений одинаковы:**

```java
// Именованные параметры - имена могут отличаться от переменных метода
@Query("SELECT u FROM User u WHERE u.status =:status and u.name =:name")
User findUserByUserStatusAndUserName(
    @Param("status") Integer userStatus,
    @Param("name") String userName);
```

Для определения собственного запроса нет разницы в том, как мы передаем параметр через имя в запрос по сравнению с **JPQL -** мы используем аннотацию **`@Param`:**

```java
// Именованные параметры в native SQL
@Query(value = "SELECT * FROM Users u WHERE u.status =:status and u.name =:name", nativeQuery = true)
User findUserByStatusAndNameNamedParamsNative(
    @Param("status") Integer status,
    @Param("name") String name);
```

### Динамические запросы

Часто мы сталкиваемся с необходимостью создания операторов **SQL** на основе условий или наборов данных, значения которых известны только во время выполнения. И в этих случаях мы не можем просто использовать статический запрос, а должны использовать динамический запрос.

Например, давайте представим ситуацию, когда нам нужно выбрать всех пользователей, чей адрес электронной почты похож на один, из набора, определенного во время выполнения **\- email1, email2,** …**, emailn:**

```sql
SELECT u FROM User u WHERE u.email LIKE '%email1%'
or u.email LIKE '%email2%'
or u.email LIKE '%emailn%'
```

Реализуя настраиваемый составной репозиторий, мы можем расширить базовую функциональность **JpaRepository** и предоставить нашу собственную логику для создания динамического запроса. Давайте посмотрим, как это сделать.

К счастью для нас, **Spring** предоставляет способ расширения базового репозитория за счет использования пользовательских интерфейсов фрагментов. Затем мы можем связать их вместе, чтобы создать составной репозиторий.

**Начнем с создания пользовательского интерфейса фрагмента:**

```java
// Кастомный интерфейс для расширения репозитория
public interface UserRepositoryCustom {
    List<User> findUserByEmails(Set<String> emails);
}
```

**А потом мы это реализуем:**

```java
// Реализация кастомного репозитория с Criteria API
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<User> findUserByEmails(Set<String> emails) {
        // Создание динамического запроса через Criteria API
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Path<String> emailPath = user.get("email");
        
        // Формирование списка условий LIKE для каждого email
        List<Predicate> predicates = new ArrayList<>();
        for (String email : emails) {
            predicates.add(cb.like(emailPath, email));
        }
        query.select(user).where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
        
        return entityManager.createQuery(query).getResultList();
    }
}
```

**Затем нам нужно расширить наш обычный интерфейс репозитория этим пользовательским интерфейсом:**

```java
// Составной репозиторий - JpaRepository + кастомный интерфейс
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
}
```

И теперь мы можем использовать этот метод, как любой другой метод в интерфейсе репозитория.

## Транзакции

Транзакции в **JPA** позволяют нам группировать несколько операций в единую единицу работы, которая либо полностью завершается успешно, либо полностью откатывается в случае ошибки.

### Управление транзакциями в **JPA**

**В **JPA** мы можем определить обычные классы как **Entity**, обеспечивающие им постоянную идентичность. Класс **EntityManager** предоставляет необходимый интерфейс для работы с несколькими сущностями в контексте персистентности. Контекст персистентности можно рассматривать как кеш первого уровня, в котором осуществляется управление сущностями:**

Контекст персистентности здесь может быть двух типов: в области транзакции или в расширенной области. Контекст персистентности в области транзакции привязан к одной транзакции. В то время как контекст персистентности с расширенной областью действия может охватывать несколько транзакций. Областью контекста персистентности по умолчанию является область транзакции.

**Давайте посмотрим, как мы можем создать **EntityManager** и определить границу транзакции вручную:**

```java
// Ручное управление транзакциями через EntityManager
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-example");
EntityManager entityManager = entityManagerFactory.createEntityManager();
try {
    entityManager.getTransaction().begin();  // Начало транзакции
    entityManager.persist(firstEntity);
    entityManager.persist(secondEntity);
    entityManager.getTransaction().commit();  // Фиксация
} catch (Exception e) {
    entityManager.getTransaction().rollback();  // Откат при ошибке
}
```

Здесь мы создаем **EntityManager** из **EntityManagerFactory** в контексте контекста персистентности на уровне транзакции. Затем мы определяем границу транзакции с помощью методов **begin**, фиксации и отката.

### Управление транзакциями в **Spring**

**В **Spring** мы можем использовать декларативное управление транзакциями с помощью аннотации **@Transactional**:**

```java
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

Аннотация **@Transactional** может применяться как на уровне класса, так и на уровне метода. При применении на уровне класса, все публичные методы класса будут выполняться в транзакции.

## Propagation (распространение) и Isolation (изоляция) транзакций

### **Propagation**

**Распространение транзакций определяет, как ведет себя транзакция, когда она вызывается из другой транзакции. В **Spring** доступны следующие варианты распространения:**

- **REQUIRED** (**по умолчанию**): Если существует транзакция, используется она, иначе создается новая
- **REQUIRES_NEW**: Всегда создается новая транзакция, существующая приостанавливается
- **SUPPORTS**: Если существует транзакция, используется она, иначе выполнение без транзакции
- **NOT_SUPPORTED**: Всегда выполнение без транзакции, существующая приостанавливается
- **MANDATORY**: Требуется существующая транзакция, иначе исключение
- **NEVER**: Выполнение без транзакции, если существует транзакция, выбрасывается исключение
- **NESTED**: Создается вложенная транзакция, если она поддерживается

**Пример использования:**

```java
@Service
public class UserService {
    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUserActivity(User user) {
        // логирование активности пользователя
    }
}
```

### **Isolation**

**Изоляция транзакций определяет, насколько транзакция изолирована от других параллельных транзакций. В **Spring** доступны следующие уровни изоляции:**

- **DEFAULT**: Использует уровень изоляции базы данных по умолчанию
- **READ_UNCOMMITTED**: Позволяет читать незафиксированные данные (**грязное чтение**)
- **READ_COMMITTED**: Разрешает чтение только зафиксированных данных (**предотвращает грязное чтение**)
- **REPEATABLE_READ**: Гарантирует, что повторное чтение данных даст тот же результат (**предотвращает неповторяющееся чтение**)
- **SERIALIZABLE**: Самый строгий уровень, транзакции выполняются последовательно

**Пример использования:**

```java
@Service
public class UserService {
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User findUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        // перевод денег
    }
}
```

## Persistence Context (контекст персистентности)

**Persistence context** находится между клиентским кодом и хранилищем данных.

Это промежуточная область, где постоянные данные преобразуются в сущности, готовые к чтению и изменению клиентским кодом.

**Теоретически Persistence Context** — это реализация паттерна **Unit of Work**. Он отслеживает все загруженные данные, отслеживает изменения этих данных и отвечает за синхронизацию любых изменений с базой данных в конце бизнес-транзакции.

**JPA EntityManager** и **Hibernate Session** — это реализация концепции **Persistence context**. В этой статье мы будем использовать **Hibernate Session** для представления **Persistence context**.

Состояние жизненного цикла сущности **Hibernate** объясняет, как сущность связана с **Persistence context**. Сессии либо загружают сущности из базы данных, либо повторно присоединяют отдельностоящие сущности.

### Типы **Persistence Context**

1. **Transaction-scoped**: Контекст привязан к одной транзакции (**по умолчанию**)
2. **Extended**: Контекст может охватывать несколько транзакций

### Состояния сущностей в **Hibernate**

1. **Transient** (**временная**): Объект создан, но не связан с сессией
2. **Persistent** (**постоянная**): Объект связан с сессией и управляется ею
3. **Detached** (**отсоединенная**): Объект был связан с сессией, но сессия закрыта
4. **Removed** (**удаленная**): Объект помечен для удаления

## Data Access Object (DAO)

**DAO (**Data Access Object**)** — это паттерн проектирования, который абстрагирует доступ к данным и инкапсулирует логику взаимодействия с базой данных.

**Создадим класс базового **DAO** - абстрактного параметризованного **DAO,** который поддерживает общие универсальные операции и который мы можем расширить для каждой сущности:**

```java
// Базовый DAO на Hibernate SessionFactory с CRUD по типу сущности
public abstract class AbstractHibernateDao<T extends Serializable> {
    private Class<T> clazz;
    
    @Autowired
    protected SessionFactory sessionFactory;
    
    public final void setClazz(final Class<T> clazzToSet) {
        clazz = Preconditions.checkNotNull(clazzToSet);
    }
    
    public T findOne(final long id) {
        return (T) getCurrentSession().get(clazz, id);
    }
    
    public List<T> findAll() {
        return getCurrentSession().createQuery("from " + clazz.getName()).list();
    }
    
    public T create(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }
    
    public T update(final T entity) {
        Preconditions.checkNotNull(entity);
        return (T) getCurrentSession().merge(entity);
    }
    
    public void delete(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().delete(entity);
    }
    
    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        Preconditions.checkState(entity != null);
        delete(entity);
    }
    
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
```

**Здесь интересны несколько аспектов - как уже говорилось, абстрактный **DAO** не расширяет какой-либо шаблон **Spring** (**например, HibernateTemplate**). Вместо этого **Hibernate SessionFactory** вводится непосредственно в **DAO** и будет играть роль основного **Hibernate API** через контекстный сеанс, который он предоставляет:**

**this.`sessionFactory`.`getCurrentSession()`;**

Также обратите внимание, что конструктор получает класс сущности в качестве параметра, который будет использоваться в общих операциях.

Теперь давайте посмотрим на пример реализации этого **DAO** для объекта **Foo:**

```java
// Реализация DAO для сущности Foo
@Repository
public class FooDAO extends AbstractHibernateDAO<Foo> implements IFooDAO {
    public FooDAO() {
        setClazz(Foo.class);
    }
}
```

## Упрощение **DAO**

Мы можем еще больше упростить реализацию **DAO**, используя универсальный подход.

Универсальный **DAO:**

```java
// Универсальный DAO с prototype scope для разных сущностей
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericHibernateDao<T extends Serializable> 
    extends AbstractHibernateDao<T> implements IGenericDao<T> {
}

public interface IGenericDao<T extends Serializable> {
    void setClazz(Class<T> clazzToSet);
    T findOne(final long id);
    List<T> findAll();
    T create(final T entity);
    T update(final T entity);
    void delete(final T entity);
    void deleteById(final long entityId);
}
```

**AbstractJpaDao** очень похож на **AbstractHibernateDao:**

```java
// Базовый DAO на JPA EntityManager с CRUD по типу сущности
public abstract class AbstractJpaDAO<T extends Serializable> {
    private Class<T> clazz;
    
    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;
    
    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }
    
    public T findOne(final long id) {
        return entityManager.find(clazz, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return entityManager.createQuery("from " + clazz.getName()).getResultList();
    }
    
    public T create(final T entity) {
        entityManager.persist(entity);
        return entity;
    }
    
    public T update(final T entity) {
        return entityManager.merge(entity);
    }
    
    public void delete(final T entity) {
        entityManager.remove(entity);
    }
    
    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }
}
```

**Подобно реализации **Hibernate,** объект доступа к данным **JPA** также прост:**

```java
// Универсальный JPA DAO с prototype scope
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericJpaDao<T extends Serializable>
    extends AbstractJpaDao<T> implements IGenericDao<T> {
}
```

**Теперь у нас есть единый интерфейс **DAO**, который мы можем внедрить. Нам также нужно указать класс:**

```java
@Service
class FooService implements IFooService {
    IGenericDao<Foo> dao;
    
    @Autowired
    public void setDao(IGenericDao<Foo> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Foo.class);
    }
}
```

**Spring** автоматически подключает новый экземпляр **DAO** с помощью внедрения установщика, чтобы реализацию можно было настроить с помощью объекта **Class**.

После этого **DAO** полностью параметризован и готов к использованию службой.

## Руководство по @**Entity**

**Итак, давайте определим его, используя аннотацию **@Entity**. Мы должны указать эту аннотацию на уровне класса. Мы также должны убедиться, что _у объекта есть конструктор без аргументов и первичный ключ_:**

```java
// Минимальная JPA-сущность (имя по умолчанию = имя класса)
@Entity
public class Student {
}
```

По умолчанию имя объекта соответствует имени класса. Мы можем изменить его имя, используя элемент **name**.

```java
@Entity(name="student")
public class Student {
}
```

Поскольку различные реализации **JPA** будут пытаться создать подкласс нашей сущности для обеспечения своей функциональности, классы сущностей не должны объявляться окончательными.

### Первичный ключ

Каждый объект **JPA** должен иметь первичный ключ, который однозначно его идентифицирует. Аннотация **@Id** определяет первичный ключ. Мы можем генерировать идентификаторы разными способами, которые указаны в аннотации **@GeneratedValue**.

Мы можем выбрать одну из четырёх стратегий генерации идентификаторов с помощью элемента стратегии. Значение может быть **AUTO**, **TABLE**, **SEQUENCE** или **IDENTITY**.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
}
```

Если мы укажем **GenerationType.`AUTO`,** провайдер **JPA** будет использовать любую стратегию для генерации идентификаторов.

### Таблицы и схемы

Для указания имени таблицы с помощью аннотации **`@Table`:**

```java
@Entity
@Table(name="STUDENT")
public class Student {
}
```

**Мы также можем упомянуть схему, используя элемент схемы:**

```java
@Entity
@Table(name="STUDENT", schema="SCHOOL")
public class Student {
}
```

### Столбцы

Как и аннотация **@Table**, мы можем использовать аннотацию **@Column** для упоминания деталей столбца в таблице.

**@Column** аннотации имеют много параметров, такие как **name, length, nullable и unique.**

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
}
```

Элемент **name** указывает имя столбца в таблице. Элемент **length** указывает его длину. Элемент **nullable** указывает, допускает ли столбец значение **NULL**, а элемент **unique** указывает, является ли столбец уникальным.

### @**Transient**

Иногда мы можем захотеть сделать поле необязательным. Для этого мы можем использовать аннотацию **@Transient**. Он указывает, что поле не будет сохраняться.

Например, мы можем рассчитать возраст студента по дате рождения.

**Итак, давайте аннотирование поля возраста с **@Transient** аннотации:**

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false)
    private String name;
    
    @Transient
    private Integer age;
}
```

В результате возраст поля не будет сохранен в таблице.

### @**Temporal**

В некоторых случаях нам может потребоваться сохранить **временные** значения в нашей таблице.

**Для этого, мы имеем в **@Temporal** аннотацию:**

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
    
    @Transient
    private Integer age;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
}
```

Однако с **JPA 2.2** у нас также есть поддержка **java.time.`LocalDate`, `java.time`.`LocalTime`, `java.time`.`LocalDateTime`, `java.time`.OffsetTime** и **java.time.`OffsetDateTime`.**

### @**Enumerated**

Иногда нам может потребоваться сохранить тип перечисления **Java.**

Мы можем использовать аннотацию **`@Enumerated`,** чтобы указать, должно ли перечисление сохраняться по имени или по порядковому номеру (**по умолчанию**).**

```java
public enum Gender {
    MALE,
    FEMALE
}

@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
    
    @Transient
    private Integer age;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
```

## Значения столбца по умолчанию

Чтобы создать значение по умолчанию непосредственно в определении таблицы **SQL,** мы можем использовать аннотацию **@Column** и установить для нее параметр **columnDefinition:**

```java
@Entity
public class User {
    @Id
    Long id;
    
    @Column(columnDefinition = "varchar(255) default 'John Snow'")
    private String name;
    
    @Column(columnDefinition = "integer default 25")
    private Integer age;
    
    @Column(columnDefinition = "boolean default false")
    private Boolean locked;
}
```

При использовании этого метода значение по умолчанию будет присутствовать в определении таблицы **SQL:**

```sql
create table user
(
    id bigint not null constraint user_pkey primary key,
    name varchar(255) default 'John Snow',
    age integer default 35,
    locked Boolean default false
);
```

И объект будет правильно сохранен со значениями по умолчанию.

Помните, что с помощью этого решения мы не сможем установить для данного столбца значение **NULL** при первом сохранении объекта. Если мы не предоставим никакого значения, автоматически будет установлено значение по умолчанию.

## Руководство по @**Basic**

**Мы можем использовать аннотацию **@Basic**, чтобы отметить свойство базового типа:**

```java
@Entity
public class Course {
    @Basic
    @Id
    private int id;
    
    @Basic
    private String name;
}
```

Другими словами, аннотация **@Basic** на поле или свойстве означает, что это базовый тип и **Hibernate** должен использовать стандартное отображение.

**Обратите внимание, что это **необязательная аннотация**. Итак, мы можем переписать нашу сущность **Course** как:**

```java
@Entity
public class Course {
    @Id
    private int id;
    private String name;
}
```

### Атрибуты @**Basic**

У аннотации **@Basic** есть два атрибута: **optional** и **fetch**. Давайте подробнее рассмотрим каждую из них.

Атрибут **optional** представляет собой логический параметр, который определяет, разрешает ли отложенную загрузку и наличие нулевого значения. По умолчанию это правда. Итак, если поле не является примитивным типом, предполагается, что базовый столбец по умолчанию допускает значение **NULL**.

**Fetch** определяет, должно ли поле или свойство загружаться лениво или жадно. По умолчанию он имеет значение **FetchType.EAGER**, но мы можем разрешить отложенную загрузку, установив для него значение **FetchType.LAZY**.

Ленивая загрузка будет иметь смысл только тогда, когда у нас есть большой объект **Serializable**, отображаемый как базовый тип, поскольку в этом случае стоимость доступа к полю может быть значительной.

```java
@Entity
public class Course {
    @Id
    private int id;
    
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String name;
}
```

### Различия между @**Basic** и @**Column**

Атрибуты аннотации **@Basic** применяются к сущностям **JPA,** тогда как атрибуты **@Column** применяются к столбцам базы данных.

**@Basic** аннотации это факультативный определяет атрибут может ли поле объекта быть нулевым или нет; с другой стороны, атрибут аннотации **`@Column`,** допускающий значение **NULL,** указывает, может ли соответствующий столбец базы данных иметь значение **NULL.**

Мы можем использовать **`@Basic`,** чтобы указать, что поле должно загружаться лениво.

**@Column** аннотации позволяют указать имя отображенного столбца базы данных.

## Разница между @**Size**, @**Length** и @**Column** (**length = значение**)

Проще говоря, все эти аннотации предназначены для обозначения размера поля.

**@Size** и **@Length** похожи. Мы можем использовать любой из них для проверки размера поля. Первая представляет собой аннотацию стандарта **Java**, а вторая — специфичную для **Hibernate**.

Однако **@Column** — это аннотация **JPA**, которую мы используем для управления операторами **DDL.**

### Различия

**@Size** делает **bean-**компонент независимым от **JPA** и его поставщиков, таких как **Hibernate**

**@Length** — это версия **@Size** для **Hibernate**

**@Column** — совсем другое дело.

**Мы будем использовать **`@Column`,** чтобы указать конкретные характеристики физического столбца базы данных. Давайте использовать длину атрибут **@Column** аннотации указать строковую длину столбца:**

```java
@Entity
public class User {
    @Column(length = 3)
    private String firstName;
}
```

Следовательно, результирующий столбец будет сгенерирован как **VARCHAR (3),** и попытка вставить более длинную строку приведет к ошибке **SQL.**

Обратите внимание, что мы будем использовать **@Column** только для указания свойств столбца таблицы, поскольку он не обеспечивает проверки.

Конечно, мы можем использовать **@Column** вместе с **@Size**, чтобы указать свойство столбца базы данных с проверкой **bean-**компонента.

```java
@Entity
public class User {
    @Column(length = 5)
    @Size(min = 3, max = 5)
    private String city;
}
```

## Руководство по @**Embedded** и @**Embeddable**

В этом руководстве мы увидим, как можно сопоставить один объект, содержащий встроенные свойства, с одной таблицей базы данных.

Итак, для этой цели мы будем использовать аннотации **@Embeddable** и **`@Embedded`,** предоставляемые **Java `Persistence API` (**JPA**).**

**Прежде всего, давайте определим таблицу под названием **company.**В таблице компании будет храниться основная информация, такая как название компании, адрес и телефон, а также информация о контактном лице:**

```java
public class Company {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String contactFirstName;
    private String contactLastName;
    private String contactPhone;
}
```

Контактное лицо, однако, похоже, должно быть выделено в отдельный класс. Проблема в том, что мы не хотим создавать отдельную таблицу для этих деталей. Итак, посмотрим, что мы можем сделать.

**JPA** предоставляет аннотацию **@Embeddable**, чтобы объявить, что класс будет встроен другими объектами.

**Давайте определим класс для абстрагирования деталей контактного лица:**

```java
@Embeddable
public class ContactPerson {
    private String firstName;
    private String lastName;
    private String phone;
}
```

Аннотация **JPA `@Embedde`d** используется для встраивания типа в другую сущность.

**Теперь давайте изменим наш класс **Company**. Мы добавим аннотации **JPA**, а также изменим использование **ContactPerson** вместо отдельных полей:**

```java
@Entity
public class Company {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String address;
    private String phone;
    
    @Embedded
    private ContactPerson contactPerson;
}
```

В результате у нас есть наша юридическая компания, встраивающая данные контактного лица и отображающая их в единую таблицу базы данных.

### Переопределение атрибутов столбцов

Однако у нас все еще есть еще одна проблема, и именно так **JPA** будет сопоставлять эти поля со столбцами базы данных.

Дело в том, что наши поля назывались как **contactFirstName** в нашем исходном классе **Company**, а теперь **firstName** в нашем классе **ContactPerson**. Итак, **JPA** захочет сопоставить их с **contact_first_name** и **first_name** соответственно.

Помимо того, что он далеко не идеален, он фактически сломает нас из-за нашей теперь дублированной телефонной колонки.

Итак, мы можем использовать **@AttributeOverrides** и **@AttributeOverride**, чтобы переопределить свойства столбца нашего встроенного типа.

**Давайте добавим это в поле **ContactPerson** в нашей сущности **Company**:**

```java
@Embedded
@AttributeOverrides({
    @AttributeOverride(name = "firstName", column = @Column(name = "contact_first_name")),
    @AttributeOverride(name = "lastName", column = @Column(name = "contact_last_name")),
    @AttributeOverride(name = "phone", column = @Column(name = "contact_phone"))
})
private ContactPerson contactPerson;
```

Обратите внимание: поскольку эти аннотации размещаются в поле, у нас могут быть разные переопределения для каждой включающей сущности.

## @**NotNull** против @**Column** (**nullable = false**)

**@NotNull** аннотации определяются в **Bean Validation** спецификации. Это означает, что его использование не ограничивается только сущностями. Напротив, мы можем использовать **@NotNull** и для любого другого **bean-**компонента.

**@Column** аннотации определяются как часть **Java `Persistence` API** спецификации.

Он используется в основном при создании метаданных схемы **DDL.** Это означает, что если мы позволим **Hibernate** автоматически генерировать схему базы данных, он применяет ограничение **not null** к определённому столбцу базы данных.

### Различия

Несмотря на то, что оба они не позволяют нам сохранять нулевые значения в базе данных, они используют разные подходы.

Как правило, мы должны предпочесть **@NotNull** аннотацию над **`@Column` (**nullable** = **false**)** аннотацией. Таким образом, мы гарантируем, что проверка выполняется до того, как **Hibernate** отправит в базу данных какие-либо запросы вставки или обновления **SQL**.

Кроме того, обычно лучше полагаться на стандартные правила, определённые в **Bean Validation**, чем позволять базе данных обрабатывать логику проверки.

Но даже если мы позволим **Hibernate** сгенерировать схему базы данных, он переведёт аннотацию **@NotNull** в ограничения базы данных. Затем мы должны только убедиться, что для свойства **hibernate.validator.apply_to_ddl** установлено значение **true**.

## Определение уникальных ограничений

Ограничения как уникального, так и первичного ключа обеспечивают гарантию уникальности столбца или набора столбцов.

**JPA** позволяет нам определять уникальные ограничения в нашем коде, используя **`@Column` (**unique** = **true**)** и **@UniqueConstraint**

### Использование @**Column(**unique = true**)

Мы можем определить уникальное ограничение для одного столбца, используя атрибут **unique** аннотации **`@Column`:**

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
}
```

Это создаст уникальное ограничение для столбца **username**.

### Использование @**UniqueConstraint**

**Для составных уникальных ограничений мы должны использовать аннотацию **@UniqueConstraint** на уровне таблицы:**

```java
@Entity
@Table(name = "users", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "email"})
    })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
}
```

Это создаст уникальное ограничение для комбинации **username** и **email**.

## Интерфейс **Serializable** и сущности

**Сущности **JPA** не обязаны реализовывать интерфейс **Serializable.** Однако, есть случаи, когда это необходимо:**

1. Когда сущность передается как параметр в удаленный интерфейс (**EJB** удаленный интерфейс)
2. Когда сущность передается через сеть (например, через **RMI**)
3. Когда сущность сохраняется в **HttpSession**

```java
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
}
```

## Отношения "один-к-одному"

В **JPA** мы можем определить отношения один-к-одному между двумя сущностями.

### Одностороннее отношение

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
}

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
}
```

### Двустороннее отношение

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Address address;
}

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String street;
    private String city;
}
```

## Отношения "многие-ко-многим"

Отношения многие-ко-многим требуют промежуточной таблицы связи.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

## Разница между @**JoinColumn** и **mappedBy**

**@JoinColumn** указывает, какая сторона отношения владеет связью. Это сторона, где находится столбец внешнего ключа.

**mappedBy** указывает, что сторона не владеет связью и просто отображает связь, которая уже была определена на другой стороне.

### @**JoinColumn**

```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "address_id")  // Владеет связью
    private Address address;
}
```

### **mappedBy**

```java
@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne(mappedBy = "address")  // Не владеет связью
    private User user;
}
```

## Сопоставление одного объекта с несколькими таблицами

**Иногда нам нужно сопоставить одну сущность с несколькими таблицами. Для этого мы можем использовать аннотацию **@SecondaryTable** или **@SecondaryTables**:**

```java
@Entity
@Table(name = "users")
@SecondaryTable(name = "user_details", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(table = "users")
    private String username;
    
    @Column(table = "user_details")
    private String phone;
    
    @Column(table = "user_details")
    private String address;
}
```

## Обзор каскадных типов **JPA**/**Hibernate**

Каскадные операции определяют, какие операции должны распространяться на связанные сущности.

Доступные типы каскадов в **JPA:**

- **PERSIST**: Сохранение родительской сущности также сохраняет связанные сущности
- **MERGE**: Объединение родительской сущности также объединяет связанные сущности
- **REMOVE**: Удаление родительской сущности также удаляет связанные сущности
- **REFRESH**: Обновление родительской сущности также обновляет связанные сущности
- **DETACH**: Отсоединение родительской сущности также отсоединяет связанные сущности
- **ALL**: Все вышеперечисленные операции

```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
```

## Наследование

**JPA** поддерживает три стратегии наследования:**

### **SINGLE_TABLE** (**одна таблица на всю иерархию**)

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}

@Entity
@DiscriminatorValue("BOOK")
public class Book extends Product {
    private String author;
}

@Entity
@DiscriminatorValue("PEN")
public class Pen extends Product {
    private String color;
}
```

Эта стратегия имеет преимущество в производительности полиморфных запросов, поскольку при запросе родительских сущностей требуется доступ только к одной таблице.

С другой стороны, это также означает, что мы больше не можем использовать ограничения **NOT NULL** для свойств сущности подкласса.

### **JOINED** (**таблица для каждого класса**)

Используя **Joined Table**, каждый класс в иерархии сопоставляется со своей таблицей. Единственный столбец, который постоянно появляется во всех таблицах**, —** это идентификатор, который будет использоваться для их объединения при необходимости.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Animal {
    @Id
    private long animalId;
    private String species;
}

@Entity
public class Pet extends Animal {
    private String name;
}
```

Недостатком этого метода сопоставления наследования является то, что для извлечения сущностей требуются соединения между таблицами, что может привести к снижению производительности для большого количества записей.

### **TABLE_PER_CLASS** (**таблица для каждого класса**)

Стратегия **Table per Class** сопоставляет каждую сущность с ее таблицей, которая содержит все свойства сущности, включая унаследованные.

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Vehicle {
    @Id
    private long vehicleId;
    private String manufacturer;
}

@Entity
public class Car extends Vehicle {
    private int numberOfDoors;
}
```

Использование **UNION** также может привести к снижению производительности при выборе этой стратегии. Другая проблема заключается в том, что мы больше не можем использовать генерацию идентификационного ключа.

## Руководство по @**FetchMode** и @**FetchType**

**FetchMode** определяет, как **Hibernate** будет извлекать данные (**путём выбора, присоединения или частичного выбора**). **FetchType**, с другой стороны, определяет, будет ли **Hibernate** загружать данные быстро или лениво.

**В качестве примера мы будем использовать следующую сущность **Customer** всего с двумя свойствами - идентификатором и набором заказов:**

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "customer")
    @Fetch(value = FetchMode.SELECT)
    private Set<Order> orders = new HashSet<>();
}

@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
```

### **FetchMode.SELECT**

**FetchMode.SELECT** генерирует отдельный запрос для каждого заказа, который должен быть загружен.

В нашем примере это дает один запрос для загрузки клиентов и пять дополнительных запросов для загрузки коллекции заказов.

Эта проблема известна как **проблема выбора n + 1**. Выполнение одного запроса вызовет n дополнительных запросов.

**FetchMode.SELECT** имеет необязательную аннотацию конфигурации с использованием аннотации **@BatchSize**:**

```java
@OneToMany
@Fetch(FetchMode.SELECT)
@BatchSize(size=10)
private Set<Order> orders;
```

**Hibernate** попытается загрузить коллекцию заказов партиями, определёнными параметром размера.

### **FetchMode.JOIN**

**В то время как **FetchMode.SELECT** лениво загружает отношения, **FetchMode.JOIN** загружает их с готовностью, скажем, через соединение:**

```java
@OneToMany
@Fetch(FetchMode.JOIN)
private Set<Order> orders;
```

Это приводит только к одному запросу для клиента и заказов через **LEFT OUTER JOIN**.

### **FetchMode.SUBSELECT**

Мы можем использовать **SUBSELECT** только с коллекциями.

```java
@OneToMany
@Fetch(FetchMode.SUBSELECT)
private Set<Order> orders;
```

При такой настройке мы возвращаемся к одному запросу для Заказчика и одному запросу для заказов, на этот раз с подвыборкой.

### Важные замечания

**В общем, `FetchMode` определяет, как `Hibernate` будет извлекать данные (**путем выбора, присоединения или частичного выбора**). `FetchType`, с другой стороны, определяет, будет ли `Hibernate` загружать данные быстро или лениво.**

- Если код не устанавливает **FetchMode**, по умолчанию используется **JOIN**, а **FetchType** работает, как определено
- С установленным **FetchMode.SELECT** или **FetchMode.SUBSELECT**, **FetchType** также работает, как определено
- С **FetchMode.JOIN** набором **FetchType** игнорируется, и запрос всегда готов

## Пакетная вставка/обновление

Пакетная обработка позволяет нам выполнять несколько операций вставки или обновления в одном пакете, что значительно улучшает производительность.

Давайте посмотрим на нашу модель данных, которую мы будем использовать в примерах.

Во-первых, мы создадим объект **School:**

```java
@Entity
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    
    @OneToMany(mappedBy = "school")
    private List<Student> students;
}

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}
```

### Настройка пакетной обработки

**Чтобы включить пакетную обработку, нам нужно установить следующие свойства:**

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=10
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

**Теперь, когда мы сохраняем несколько сущностей**, Hibernate** будет группировать операции вставки или обновления:**

```java
@Service
public class BatchService {
    @Autowired
    private StudentRepository studentRepository;
    
    @Transactional
    public void batchInsert(List<Student> students) {
        for (Student student : students) {
            studentRepository.save(student);
        }
    }
}
```

## Разница между **save**() и **saveAndFlush**()

**save()** сохраняет сущность в контексте персистентности, но не обязательно немедленно записывает изменения в базу данных. Изменения могут быть отложены до коммита транзакции.

**saveAndFlush()** сохраняет сущность и немедленно синхронизирует изменения с базой данных, выполняя операцию **flush**.

```java
@Transactional
public void saveExample() {
    User user = new User("John");
    userRepository.save(user);  // Изменения могут быть отложены
    
    user.setName("Jane");
    userRepository.saveAndFlush(user);  // Немедленная синхронизация с БД
}
```

## Аудирование

Аудирование позволяет нам отслеживать, когда и кто создал или изменил сущность.

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bar {
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;
    
    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;
}
```

**Если наше приложение использует **Spring Security**, мы можем отслеживать, когда и кем были внесены изменения:**

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bar {
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    
    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;
}
```

**Столбцы, аннотированные **@CreatedBy** и **@LastModifiedBy**, заполняются именем принципала, который создал или последним изменил сущность. Информация поступает из экземпляра аутентификации **SecurityContext**. Если мы хотим настроить значения, заданные для аннотированных полей, мы можем реализовать интерфейс **AuditorAware&lt;T&gt;**:**

```java
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public String getCurrentAuditor() {
        // возвращаем текущего пользователя
        return "system";
    }
}
```

Чтобы настроить приложение для использования **AuditorAwareImpl** для поиска текущего принципала, мы объявляем **bean-**компонент типа **AuditorAware,** инициализированный экземпляром **AuditorAwareImpl,** и указываем имя **bean-**компонента в качестве значения параметра **auditAwareRef** в **`@EnableJpaAuditing`:**

```java
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
```

## Частичное обновление данных

**CrudRespository.save** от **Spring Data**, несомненно, прост, но одна особенность может быть недостатком: он обновляет каждый столбец в таблице. Такова семантика **CRUD**, но что, если вместо этого мы хотим сделать **PATCH**?

### Подход 1: Загрузка и обновление

Давайте добавим в наш сервис метод для обновления контактных данных наших клиентов.

```java
public void updateCustomerContacts(long id, String phone) {
    Customer myCustomer = repo.findById(id);
    myCustomer.phone = phone;
    repo.save(myCustomer);
}
```

Мы вызовем метод **findById** и получим соответствующий объект. Затем мы продолжаем и обновляем необходимые поля и сохраняем данные.

Этот базовый метод эффективен, когда количество полей для обновления относительно невелико, а наши объекты довольно просты.

### Подход 2: Использование **MapStruct** с **DTO**

Когда наши объекты имеют большое количество полей с разными уровнями доступа, довольно часто реализуется шаблон **DTO**.

```java
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDto(CustomerDto dto, @MappingTarget Customer entity);
}

public void updateCustomer(CustomerDto dto) {
    Customer myCustomer = repo.findById(dto.id);
    mapper.updateCustomerFromDto(dto, myCustomer);
    repo.save(myCustomer);
}
```

### Подход 3: Пользовательский запрос

Другой подход, который мы можем реализовать, заключается в определении пользовательского запроса для частичных обновлений.

На самом деле **JPA** определяет две аннотации **@Modifying** и **@Query**, которые позволяют нам явно написать наш оператор обновления.

Теперь мы можем указать нашему приложению, как вести себя во время обновления, не перекладывая нагрузку на **ORM.**

**Давайте добавим наш собственный метод обновления в репозиторий:**

```java
@Modifying
@Query("update Customer u set u.phone =:phone where u.id =:id")
void updatePhone(@Param(value = "id") long id, @Param(value = "phone") String phone);
```

**Теперь мы можем переписать наш метод обновления:**

```java
public void updateCustomerContacts(long id, String phone) {
    repo.updatePhone(id, phone);
}
```

Теперь мы можем выполнить частичное обновление. Всего несколькими строками кода и без изменения наших сущностей мы достигли своей цели.

Недостатком этого метода является то, что нам придётся определять метод для каждого возможного частичного обновления нашего объекта.

## Руководство по **JPA Buddy**

**JPA Buddy** — это широко используемый плагин **IntelliJ IDEA**, предназначенный как для новых, так и для опытных разработчиков, которые работают с моделью данных **JPA** и связанными технологиями, такими как **Spring Data JPA**, инструменты управления версиями БД (**Flyway**, **Liquibase**), **MapStruct** и некоторые другие. Плагин предоставляет визуальные дизайнеры, генерацию кода и дополнительные проверки, которые должны упростить разработку и улучшить код в соответствии с лучшими практиками для **JPA.**

Плагин работает как с версиями **IntelliJ IDEA Community**, так и с **Ultimate** и использует модель «**freemium**». Большинство функций доступны бесплатно, и нам нужно будет купить подписку, чтобы получить доступ к платным.

### Основные функции

1. **Визуальный дизайнер сущностей**: Создание и редактирование сущностей с помощью визуального интерфейса
2. **Генерация миграций БД**: Автоматическая генерация скриптов миграции для **Liquibase** и **Flyway**
3. **Создание репозиториев**: генерация репозиториев **Spring Data JPA** на основе сущностей
4. **Извлечение запросов**: Рефакторинг длинных имен методов в аннотации **@Query**
5. **Генерация DTO**: Создание **DTO** и преобразователей с помощью **MapStruct**
6. **Проверки кода**: Обнаружение потенциальных проблем с производительностью и ошибок

## Руководство по **Specifications**

Если вы ищете лучший способ управления вашими запросами или хотите генерировать динамические и типизированные запросы, вы можете найти свое решение в спецификациях **Spring Data JPA**.

Спецификации **Spring Data JPA** — это ещё один инструмент в нашем распоряжении для выполнения запросов к базе данных с помощью **Spring** или **Spring Boot**.

Спецификации строятся на основе **Criteria API**.

При построении запроса **Criteria** мы должны сами создавать и управлять объектами **Root**, **CriteriaQuery** и **CriteriaBuilder**.

**Спецификации основаны на **Criteria API**, чтобы упростить работу разработчиков. Нам просто нужно реализовать интерфейс **Specification**:**

```java
interface Specification<T> {
    Predicate toPredicate(Root<T> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder);
}
```

Используя Спецификации, мы можем создавать атомарные предикаты и комбинировать эти предикаты для создания сложных динамических запросов.

Спецификации основаны на шаблоне «Спецификация» доменно-ориентированного проектирования.

### Настройка

**Во-первых, нам нужно иметь зависимость Spring Data JPA в нашем файле build.gradle:**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
annotationProcessor 'org.hibernate:hibernate-jpamodelgen'
```

Мы также добавили зависимость процессора аннотаций **hibernate-jpamodelgen**, которая будет генерировать классы статической метамодели наших сущностей.

### Использование спецификаций

**Наш репозиторий должен расширять **JpaSpecificationExecutor**:**

```java
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
}
```

**Теперь мы можем создавать спецификации:**

```java
public static Specification<Product> hasName(String name) {
    return (root, query, criteriaBuilder) -> 
        criteriaBuilder.equal(root.get("name"), name);
}

public static Specification<Product> priceGreaterThan(double price) {
    return (root, query, criteriaBuilder) -> 
        criteriaBuilder.greaterThan(root.get("price"), price);
}
```

**И комбинировать их:**

```java
Specification<Product> spec = Specification
    .where(hasName("Product"))
    .and(priceGreaterThan(100.0));

List<Product> products = productRepository.findAll(spec);
```

## Сравнение между **JPA** и **JDBC**

**JPA** и **JDBC** - это два разных подхода к работе с базами данных в **Java**:**

### **JDBC**

- **Низкоуровневый API**: Работает напрямую с базой данных
- **SQL-запросы**: Требует написания **SQL** вручную
- **Управление ресурсами**: Необходимо вручную управлять соединениями, транзакциями
- **Производительность**: Прямой доступ может быть быстрее
- **Сложность**: Больше кода для базовых операций

### **JPA**

- **Высокоуровневый API**: Абстракция над **JDBC**
- **Object-Relational Mapping**: автоматическое преобразование объектов в **SQL**
- **Управление жизненным циклом**: Автоматическое управление транзакциями, соединениями
- **Производительность**: Может быть медленнее из-за дополнительных слоев абстракции
- **Простота**: Меньше кода, более читаемый

## Руководство по @**Query**

Аннотация **@Query** позволяет нам определять пользовательские запросы **JPQL** или нативный **SQL**.

### **JPQL** запросы

```java
@Query("SELECT u FROM User u WHERE u.status = 1")
Collection<User> findAllActiveUsers();
```

### Нативные **SQL** запросы

```java
@Query(value = "SELECT * FROM USERS u WHERE u.status = 1", nativeQuery = true)
Collection<User> findAllActiveUsersNative();
```

### Обновление данных

Мы можем использовать аннотацию **@Query** для изменения состояния базы данных, также добавив аннотацию **@Modifying** к методу репозитория.

```java
@Modifying
@Query("update User u set u.status =:status where u.name =:name")
int updateUserSetStatusForName(@Param("status") Integer status, @Param("name") String name);
```

### Использование параметров

#### Индексированные параметры

```java
@Query("SELECT u FROM User u WHERE u.status =?1 and u.name =?2")
User findUserByStatusAndName(Integer status, String name);
```

#### Именованные параметры

```java
@Query("SELECT u FROM User u WHERE u.status =:status and u.name =:name")
User findUserByStatusAndNameNamedParams(
    @Param("status") Integer status,
    @Param("name") String name);
```

### Пагинация с @**Query**

```java
@Query(value = "SELECT u FROM User u ORDER BY id")
Page<User> findAllUsersWithPagination(Pageable pageable);
```

### Использование **Collection** в запросах

```java
@Query(value = "SELECT u FROM User u WHERE u.name IN:names")
List<User> findUserByNameList(@Param("names") Collection<String> names);
```

## Использование нескольких файлов **SQL** для импорта

**В **Spring Boot** мы можем использовать несколько файлов **SQL** для инициализации базы данных:**

```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:schema.sql,classpath:data.sql
```

Или в **application.yml:**

```yaml
spring:
  sql:
    init:
      mode: always
      data-locations:
        - classpath:schema.sql
        - classpath:data.sql
```

## Кэш второго уровня

Кэш второго уровня — это кэш, который распространяется на все сессии. Он может значительно улучшить производительность приложения, уменьшая количество обращений к базе данных.

### Настройка кэша второго уровня в **Hibernate**

```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.use_query_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
```

### Использование @**Cacheable**

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
```

### Стратегии кэширования

- **READ_ONLY**: Только для чтения, не изменяется
- **READ_WRITE**: Чтение и запись, обеспечивает согласованность
- **NONSTRICT_READ_WRITE**: Нестрогая согласованность
- **TRANSACTIONAL**: Полная поддержка транзакций

## Продвинутые возможности **JPA**

### **Criteria API** для динамических запросов

```java
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private LocalDate createdDate;
}

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> findProducts(String name, BigDecimal minPrice,
                                    BigDecimal maxPrice, String category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Динамические условия
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(product.get("name")),
                                 "%" + name.toLowerCase() + "%"));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
        }

        if (category != null && !category.trim().isEmpty()) {
            predicates.add(cb.equal(product.get("category"), category));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(product.get("createdDate")));

        return entityManager.createQuery(query).getResultList();
    }

    public List<ProductStats> getProductStats() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductStats> query = cb.createQuery(ProductStats.class);
        Root<Product> product = query.from(Product.class);

        query.multiselect(
            product.get("category"),
            cb.count(product),
            cb.avg(product.get("price")),
            cb.min(product.get("price")),
            cb.max(product.get("price"))
        );

        query.groupBy(product.get("category"));
        query.having(cb.gt(cb.count(product), 5));

        return entityManager.createQuery(query).getResultList();
    }
}
```

### **Entity Graphs** для оптимизации загрузки

```java
@Entity
@NamedEntityGraph(
    name = "User.withOrders",
    attributeNodes = {
        @NamedAttributeNode("orders")
    }
)
public class User {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.withOrders", type = EntityGraphType.LOAD)
    List<User> findByName(String name);

    @EntityGraph(attributePaths = {"orders", "orders.items"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findUserWithOrdersAndItems(@Param("id") Long id);
}

// Динамическое создание Entity Graph
public User findUserWithDynamicGraph(Long userId, String... attributePaths) {
    EntityGraph<User> graph = entityManager.createEntityGraph(User.class);
    graph.addAttributeNodes(attributePaths);

    Map<String, Object> hints = new HashMap<>();
    hints.put("javax.persistence.loadgraph", graph);

    return entityManager.find(User.class, userId, hints);
}
```

### **Projections** для оптимизации запросов

```java
// Interface-based projection
public interface UserSummary {
    String getName();
    String getEmail();
    Long getOrderCount();

    @Value("#{target.name + ' (' + target.email + ')'}")
    String getDisplayName();
}

// Class-based projection
public class UserDTO {
    private final String name;
    private final String email;
    private final Long orderCount;

    public UserDTO(String name, String email, Long orderCount) {
        this.name = name;
        this.email = email;
        this.orderCount = orderCount;
    }

    // getters
}

// Dynamic projection
public interface UserProjection {
    String getName();
    String getEmail();
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Interface projection
    <T> List<T> findByActiveTrue(Class<T> projectionType);

    // Class projection
    @Query("SELECT new com.example.UserDTO(u.name, u.email, COUNT(o)) " +
           "FROM User u LEFT JOIN u.orders o " +
           "WHERE u.active = true " +
           "GROUP BY u.id, u.name, u.email")
    List<UserDTO> findUserSummaries();

    // Dynamic projection
    <T> List<T> findByNameStartingWith(String prefix, Class<T> projectionType);
}

// Использование
List<UserSummary> summaries = userRepository.findByActiveTrue(UserSummary.class);
List<UserDTO> dtos = userRepository.findUserSummaries();
List<UserProjection> projections = userRepository.findByNameStartingWith("A", UserProjection.class);
```

### **Specifications** для сложных запросов

```java
// Specification interface
public interface Specification<T> {
    Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}

// User specifications
public class UserSpecifications {

    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.like(root.get("email"), "%" + email + "%");
    }

    public static Specification<User> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<User> createdAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThan(root.get("createdDate"), date);
    }

    public static Specification<User> hasOrders() {
        return (root, query, cb) -> cb.isNotEmpty(root.get("orders"));
    }
}

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // JpaSpecificationExecutor provides findAll(Specification), count(), etc.
}

// Service с использованием specifications
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findUsers(String name, String email, Boolean active,
                               LocalDate createdAfter, Boolean hasOrders) {

        Specification<User> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(UserSpecifications.hasName(name));
        }

        if (email != null) {
            spec = spec.and(UserSpecifications.hasEmail(email));
        }

        if (active != null) {
            if (active) {
                spec = spec.and(UserSpecifications.isActive());
            } else {
                spec = spec.and(UserSpecifications.isActive().not());
            }
        }

        if (createdAfter != null) {
            spec = spec.and(UserSpecifications.createdAfter(createdAfter));
        }

        if (hasOrders != null && hasOrders) {
            spec = spec.and(UserSpecifications.hasOrders());
        }

        return userRepository.findAll(spec);
    }
}
```

## Оптимизация производительности

### N+1 проблема и ее решения

```java
// Проблема N+1 запросов
@Entity
public class Department {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;
}

@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}

// ПЛОХОЙ код - N+1 проблема
public List<String> getAllEmployeeNames() {
    List<Department> departments = departmentRepository.findAll();
    List<String> employeeNames = new ArrayList<>();

    for (Department dept : departments) {
        for (Employee emp : dept.getEmployees()) { // N дополнительных запросов
            employeeNames.add(emp.getName());
        }
    }

    return employeeNames;
}

// РЕШЕНИЕ 1: JOIN FETCH
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();
}

// РЕШЕНИЕ 2: Entity Graph
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @EntityGraph(attributePaths = "employees")
    List<Department> findAll();
}

// РЕШЕНИЕ 3: Batch fetching
@Configuration
public class JpaConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        emf.setPackagesToScan("com.example");

        Properties props = new Properties();
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.order_inserts", "true");
        props.setProperty("hibernate.order_updates", "true");
        props.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        emf.setJpaProperties(props);
        return emf;
    }
}

// РЕШЕНИЕ 4: @BatchSize
@Entity
public class Department {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department")
    @BatchSize(size = 10) // Загружать по 10 сотрудников за раз
    private List<Employee> employees;
}
```

### Оптимизация запросов

```java
// Проблема: Избыточная загрузка данных
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ПЛОХО: Загружает все поля пользователя
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    // ЛУЧШЕ: Использовать projection
    @Query("SELECT u.id, u.name, u.email FROM User u WHERE u.email = :email")
    UserSummary findUserSummaryByEmail(@Param("email") String email);

    // ИЛИ: Использовать DTO
    @Query("SELECT new com.example.UserDTO(u.id, u.name, u.email) FROM User u WHERE u.email = :email")
    UserDTO findUserDTOByEmail(@Param("email") String email);
}

// Проблема: Неэффективная пагинация
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ПЛОХО: Сначала выбирает все, потом сортирует и ограничивает
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findTop10ByOrderByPriceDesc(Pageable pageable);

    // ЛУЧШЕ: База данных делает всю работу
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    Page<Product> findTop10ByOrderByPriceDesc(PageRequest.of(0, 10));
}

// Проблема: Cartesian product при JOIN
@Entity
public class Author {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "author")
    private List<Book> books;
}

@Entity
public class Book {
    @Id
    private Long id;
    private String title;

    @ManyToOne
    private Author author;
}

// ПЛОХО: Создает cartesian product
@Query("SELECT a FROM Author a JOIN a.books b WHERE b.title LIKE :title")
List<Author> findAuthorsByBookTitle(@Param("title") String title);

// ЛУЧШЕ: Использовать DISTINCT или EXISTS
@Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b.title LIKE :title")
List<Author> findAuthorsByBookTitle(@Param("title") String title);

// ИЛИ: Использовать EXISTS
@Query("SELECT a FROM Author a WHERE EXISTS (SELECT b FROM Book b WHERE b.author = a AND b.title LIKE :title)")
List<Author> findAuthorsByBookTitle(@Param("title") String title);
```

### Кэширование второго уровня

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
        return factory;
    }
}

// ehcache.xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="ehcache.xsd"
         updateCheck="true"
         monitoring="autodetect"
         dynamicConfig="true">

    <cache name="com.example.User"
           maxEntriesLocalHeap="1000"
           eternal="false"
           timeToIdleSeconds="300"
           timeToLiveSeconds="600"
           memoryStoreEvictionPolicy="LRU">
    </cache>

    <cache name="com.example.Product"
           maxEntriesLocalHeap="5000"
           eternal="false"
           timeToIdleSeconds="600"
           timeToLiveSeconds="3600">
    </cache>
</ehcache>

// Использование кэширования
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
    @Id
    private Long id;
    private String name;
    private String email;

    // getters and setters
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable("users")
    Optional<User> findByEmail(String email);

    @CacheEvict(value = "users", key = "#user.email")
    <S extends User> S save(S user);

    @CacheEvict(value = "users", allEntries = true)
    void deleteAll();
}
```

## Распространенные проблемы и решения

### Проблема: **LazyInitializationException**

```java
// Проблема
@Service
public class UserService {

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(user -> new UserDTO(user.getName(), user.getDepartment().getName())) // LazyInitializationException!
            .collect(Collectors.toList());
    }
}

// Решение 1: JOIN FETCH в запросе
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.department")
    List<User> findAllWithDepartments();
}

// Решение 2: Entity Graph
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "department")
    List<User> findAll();
}

// Решение 3: DTO projection
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.example.UserDTO(u.name, d.name) FROM User u JOIN u.department d")
    List<UserDTO> findAllUserDTOs();
}
```

### Проблема: **Optimistic Lock Exception**

```java
@Entity
@Version
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;

    @Version
    private Long version;
}

// Проблема: Конфликты версий
@Service
public class ProductService {

    @Transactional
    public void updateProduct(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setPrice(newPrice);
        productRepository.save(product); // Может бросить OptimisticLockException
    }
}

// Решение: Обработка исключений
@Service
public class ProductService {

    @Transactional
    public void updateProduct(Long productId, BigDecimal newPrice) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            product.setPrice(newPrice);
            productRepository.save(product);
        } catch (OptimisticLockException e) {
            // Повторная попытка или бизнес-логика
            throw new ConcurrentModificationException("Product was modified by another transaction", e);
        }
    }

    // Или: Использовать retry механизм
    @Retryable(value = OptimisticLockException.class, maxAttempts = 3)
    @Transactional
    public void updateProductWithRetry(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setPrice(newPrice);
        productRepository.save(product);
    }
}
```

### Проблема: **Memory leaks** с **EntityManager**

```java
// Проблема: Утечка памяти
@Service
public class ProblematicService {

    @PersistenceContext
    private EntityManager entityManager;

    public void processLargeDataset() {
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class)
            .getResultList(); // Все объекты остаются в памяти

        for (User user : users) {
            processUser(user);
            // Объекты не очищаются из persistence context
        }
    }
}

// Решение: Использовать pagination и clear()
@Service
public class OptimizedService {

    @PersistenceContext
    private EntityManager entityManager;

    public void processLargeDataset() {
        int pageSize = 100;
        int page = 0;
        List<User> users;

        do {
            users = entityManager.createQuery("SELECT u FROM User u", User.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

            for (User user : users) {
                processUser(user);
            }

            entityManager.clear(); // Очищает persistence context
            entityManager.getEntityManagerFactory().getCache().evictAll(); // Очищает кэш

            page++;
        } while (!users.isEmpty());
    }
}
```

## Миграция с **Hibernate**/**JPA**

### Переход с чистого **Hibernate** на **Spring Data JPA**

```java
// Старый код с Hibernate
public class HibernateUserDao {

    private SessionFactory sessionFactory;

    public User findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }

    public List<User> findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE name = :name", User.class)
                         .setParameter("name", name)
                         .list();
        }
    }
}

// Новый код с Spring Data JPA
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByName(String name);
}

// Service слой упрощается
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}
```

### Миграция с **JDBC** на **JPA**

```java
// Старый JDBC код
public class JdbcUserDao {

    private JdbcTemplate jdbcTemplate;

    public User findById(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT id, name, email FROM users WHERE id = ?",
            new Object[]{id},
            (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email")
            )
        );
    }
}

// Новый JPA код
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Автоматически генерируется
    Optional<User> findById(Long id);
}

// Или с custom запросом
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findUserById(@Param("id") Long id);
}
```

## Лучшие практики для Spring Data JPA

### 1. Используйте правильные типы отношений

```java
// ПЛОХО: Использование @OneToMany без mappedBy
@Entity
public class Author {
    @OneToMany
    @JoinColumn(name = "author_id")
    private List<Book> books; // Создает лишнюю таблицу
}

// ЛУЧШЕ: Bidirectional relationship
@Entity
public class Author {
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;
}

@Entity
public class Book {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
}
```

### 2. Правильное использование @**Transactional**

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Transactional
    public Order createOrder(OrderRequest request) {
        // Все операции в одной транзакции
        Order order = new Order(request.getItems());
        orderRepository.save(order);

        try {
            paymentService.processPayment(order);
            order.setStatus(OrderStatus.PAID);
        } catch (PaymentException e) {
            order.setStatus(OrderStatus.FAILED);
            throw e; // Транзакция откатится
        }

        return orderRepository.save(order);
    }
}
```

### 3. Используйте **DTO** для сложных запросов

```java
// ПЛОХО: Загрузка лишних данных
@Service
public class ReportService {

    public List<User> getActiveUsersWithDetails() {
        return userRepository.findAll().stream()
            .filter(User::isActive)
            .collect(Collectors.toList()); // Загружает все поля всех пользователей
    }
}

// ЛУЧШЕ: Использовать DTO
public interface UserSummary {
    String getName();
    String getEmail();
    Long getOrderCount();
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.name as name, u.email as email, COUNT(o) as orderCount " +
           "FROM User u LEFT JOIN u.orders o " +
           "WHERE u.active = true " +
           "GROUP BY u.id, u.name, u.email")
    List<UserSummary> findActiveUserSummaries();
}
```

### 4. Правильное управление связями

```java
@Service
public class DepartmentService {

    @Transactional
    public void addEmployeeToDepartment(Long departmentId, Long employeeId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Правильная установка bidirectional связи
        employee.setDepartment(department);
        department.getEmployees().add(employee);

        // Сохраняем только владельца связи (Employee)
        employeeRepository.save(employee);
    }

    @Transactional
    public void removeEmployeeFromDepartment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Department department = employee.getDepartment();
        if (department != null) {
            department.getEmployees().remove(employee);
            employee.setDepartment(null);

            departmentRepository.save(department);
        }
    }
}
```


## Заключение

**Spring Data JPA** предоставляет мощный и гибкий способ работы с данными в **Spring**-приложениях. Понимание основных концепций **JPA**, правильное проектирование сущностей, оптимизация запросов и избежание распространенных проблем являются ключом к созданию эффективных и поддерживаемых приложений.

### Ключевые принципы:

1. **Правильное моделирование данных**: Используйте подходящие типы отношений и каскадирование
2. **Оптимизация запросов**: Используйте **projections**, **pagination** и индексы
3. **Управление транзакциями**: Правильное использование @**Transactional**
4. **Избегание N+1 проблемы**: Используйте **JOIN FETCH** и **Entity Graphs**
5. **Кэширование**: Используйте **second-level cache** для часто запрашиваемых данных
6. **Мониторинг**: Отслеживайте производительность запросов и транзакций

### Рекомендуемые ресурсы:

- [**Spring Data JPA** Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [**Hibernate** Documentation](https://hibernate.org/orm/documentation/)
- [Vlad Mihalcea's High-Performance Java Persistence](https://vladmihalcea.com/books/high-performance-java-persistence/)

Этот всесторонний гид по **Spring Data JPA** охватывает все основные аспекты: от базовых **CRUD** операций до продвинутых техник оптимизации, паттернов проектирования и решения распространенных проблем.
