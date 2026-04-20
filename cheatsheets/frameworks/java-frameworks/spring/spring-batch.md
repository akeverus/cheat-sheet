---
title: "Spring Batch для Java"
description: "Комплексное руководство по Spring Batch: фреймворку для обработки больших объемов данных в пакетном режиме. Подробно рассматриваются job конфигурация, step lifecycle, readers/writers, chunk processing, parallel execution, error handling, scaling и production deployment."
tags:
  - frameworks
  - java-frameworks
  - spring-batch
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Spring Batch для Java

Комплексное руководство по **Spring Batch**: фреймворку для обработки больших объемов данных в пакетном режиме. Подробно рассматриваются **job** конфигурация, **step lifecycle**, **readers**/**writers**, **chunk processing**, **parallel execution**, **error handling**, **scaling** и **production deployment**.

## Полезные ссылки

### Официальная документация
- [**Spring Batch** Documentation](https://docs.spring.io/spring-batch/reference/) — полная документация **Spring Batch**
- [**Spring Batch** GitHub](https://github.com/spring-projects/spring-batch) — исходный код и примеры
- [**Spring Batch** Reference](https://docs.spring.io/spring-batch/docs/current/api/) — **API** документация

### Книги и ресурсы
- [**Spring Batch** in Action](https://www.manning.com/books/spring-batch-in-action) — классическая книга по **Spring Batch**
- [Batch Processing with **Spring Batch**](https://www.baeldung.com/spring-batch) — современное руководство
- [Pro **Spring Batch**](https://www.apress.com/gp/book/9781484237239) — профессиональное руководство

### Статьи и туториалы
- [**Spring Batch** Tutorial](https://spring.io/guides/gs/batch-processing/) — официальный туториал
- [**Spring Batch Best Practices**](https://www.baeldung.com/spring-batch-best-practices) — лучшие практики
- [**Spring Batch Performance** Tuning](https://docs.spring.io/spring-batch/reference/scalability.html) — производительность

### См. также
- [[spring-boot|**Spring Boot**]] — **Spring Boot** основы
- [[README|**PostgreSQL**]] — БД для **batch processing**
- [Мониторинг](../../../monitoring/) — мониторинг **batch jobs**
- [[spring-integration|**Spring Integration**]] — интеграция с другими системами

## Содержание

- [Введение в Spring Batch](#введение-в-spring-batch)
  - [Почему Spring Batch?](#почему-spring-batch)
  - [Когда использовать Spring Batch?](#когда-использовать-spring-batch)
    - [✅ Идеально подходит для:](#идеально-подходит-для)
    - [❌ Не подходит для:](#не-подходит-для)
  - [Основные возможности](#основные-возможности)
    - [Transaction Management](#transaction-management)
    - [Job Repository](#job-repository)
- [Архитектура и концепции](#архитектура-и-концепции)
  - [Core Components](#core-components)
    - [Job](#job)
    - [Step](#step)
    - [Execution Context](#execution-context)
- [H2 (для разработки)](#h2-для-разработки)
- [PostgreSQL (для production)](#postgresql-для-production)
- [Batch конфигурация](#batch-конфигурация)
- [Job и Step конфигурация](#job-и-step-конфигурация)
  - [Basic Job Configuration](#basic-job-configuration)
    - [Java-based Configuration](#java-based-configuration)
    - [XML-based Configuration](#xml-based-configuration)
  - [Job Parameters](#job-parameters)
    - [Parameter Types](#parameter-types)
  - [Step Configuration](#step-configuration)
    - [Tasklet Step](#tasklet-step)
    - [Chunk-oriented Step](#chunk-oriented-step)
- [Item Readers](#item-readers)
  - [Flat File Reader](#flat-file-reader)
    - [CSV File Reader](#csv-file-reader)
    - [Fixed Width File Reader](#fixed-width-file-reader)
  - [Database Readers](#database-readers)
    - [JDBC Cursor Reader](#jdbc-cursor-reader)
    - [JPA Reader](#jpa-reader)
  - [Custom Readers](#custom-readers)
    - [Custom ItemReader](#custom-itemreader)
- [Item Writers](#item-writers)
  - [Database Writers](#database-writers)
    - [JDBC Batch Writer](#jdbc-batch-writer)
    - [JPA Writer](#jpa-writer)
  - [File Writers](#file-writers)
    - [CSV File Writer](#csv-file-writer)
    - [XML File Writer](#xml-file-writer)
  - [Composite Writers](#composite-writers)
    - [MultiResource Writer](#multiresource-writer)
    - [Composite Writer](#composite-writer)
- [Chunk Processing](#chunk-processing)
  - [Chunk-oriented Processing](#chunk-oriented-processing)
    - [Chunk Size Configuration](#chunk-size-configuration)
  - [Item Processor](#item-processor)
    - [Simple Processor](#simple-processor)
    - [Composite Processor](#composite-processor)
- [Error Handling](#error-handling)
  - [Skip Logic](#skip-logic)
    - [Skip Configuration](#skip-configuration)
    - [Custom Skip Policy](#custom-skip-policy)
  - [Retry Logic](#retry-logic)
    - [Retry Configuration](#retry-configuration)
    - [Backoff Policies](#backoff-policies)
  - [Error Handling Strategies](#error-handling-strategies)
    - [Circuit Breaker Pattern](#circuit-breaker-pattern)
- [Listeners и Callbacks](#listeners-и-callbacks)
  - [Job Listeners](#job-listeners)
    - [Job Execution Listener](#job-execution-listener)
  - [Step Listeners](#step-listeners)
    - [Step Execution Listener](#step-execution-listener)
    - [Chunk Listener](#chunk-listener)
    - [Item Read/Write/Process Listeners](#item-readwriteprocess-listeners)
- [Parallel Processing](#parallel-processing)
  - [Multi-threaded Step](#multi-threaded-step)
    - [Thread Pool Configuration](#thread-pool-configuration)
  - [Partitioned Step](#partitioned-step)
    - [Partitioning Configuration](#partitioning-configuration)
    - [Custom Partitioner](#custom-partitioner)
- [Scaling и Performance](#scaling-и-performance)
  - [Performance Optimization](#performance-optimization)
    - [Reader Optimization](#reader-optimization)
    - [Writer Optimization](#writer-optimization)
    - [Processor Optimization](#processor-optimization)
  - [Memory Management](#memory-management)
    - [Chunk Size Tuning](#chunk-size-tuning)
  - [Database Optimization](#database-optimization)
    - [Connection Pooling](#connection-pooling)
    - [Batch Insert Optimization](#batch-insert-optimization)
- [Spring Boot интеграция](#spring-boot-интеграция)
  - [Auto-configuration](#auto-configuration)
    - [Spring Boot Batch Starter](#spring-boot-batch-starter)
    - [Application Properties](#application-properties)
  - [Job Scheduling](#job-scheduling)
    - [@Scheduled Integration](#scheduled-integration)
    - [Command Line Runner](#command-line-runner)
  - [REST API для управления](#rest-api-для-управления)
    - [Job Controller](#job-controller)
- [Testing](#testing)
  - [Unit Testing](#unit-testing)
    - [Testing Components](#testing-components)
    - [Testing Readers/Writers](#testing-readerswriters)
  - [Integration Testing](#integration-testing)
    - [Testing Complete Job](#testing-complete-job)
    - [Testing with Testcontainers](#testing-with-testcontainers)
- [Production Deployment](#production-deployment)
  - [Configuration Management](#configuration-management)
    - [Environment-specific Properties](#environment-specific-properties)
- [application-prod.yaml](#application-prodyaml)
    - [Docker Deployment](#docker-deployment)
- [Create directories](#create-directories)
- [Create non-root user](#create-non-root-user)
    - [Kubernetes Deployment](#kubernetes-deployment)
  - [Monitoring и Alerting](#monitoring-и-alerting)
    - [Spring Boot Actuator](#spring-boot-actuator)
    - [Health Indicators](#health-indicators)
- [Лучшие практики](#лучшие-практики)
  - [Job Design](#job-design)
    - [1. Job Naming Convention](#1-job-naming-convention)
    - [2. Parameter Management](#2-parameter-management)
    - [3. Comprehensive Error Handling](#3-comprehensive-error-handling)
  - [Performance](#performance)
    - [4. Chunk Size Optimization](#4-chunk-size-optimization)
    - [5. Test Coverage](#5-test-coverage)
  - [Operations](#operations)
    - [6. Operational Readiness](#6-operational-readiness)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [Job не запускается](#job-не-запускается)
    - [Chunk processing проблемы](#chunk-processing-проблемы)
    - [Database connection проблемы](#database-connection-проблемы)
    - [Reader/Writer performance](#readerwriter-performance)
  - [Debug techniques](#debug-techniques)
    - [Job execution monitoring](#job-execution-monitoring)
    - [Memory monitoring](#memory-monitoring)
- [Заключение](#заключение)
  - [Ключевые возможности:](#ключевые-возможности)
  - [Архитектурные преимущества:](#архитектурные-преимущества)
    - [Reliability:](#reliability)
    - [Scalability:](#scalability)
  - [Когда НЕ использовать:](#когда-не-использовать)
  - [Production considerations:](#production-considerations)
  - [Best practices summary:](#best-practices-summary)

## Введение в Spring Batch

**Spring Batch** — это легковесный, всесторонний фреймворк, предназначенный для разработки надежных пакетных приложений для предприятий. **Spring Batch** предоставляет переиспользуемые функции, необходимые для обработки большого количества данных, включая ведение журнала, отслеживание транзакций, управление заданиями, обработку заданий, управление ресурсами и административные функции.

### Почему Spring Batch?

**Spring Batch** решает сложные задачи пакетной обработки данных:**

1. **Надежность** — гарантированная обработка каждого элемента данных
2. **Масштабируемость** — обработка миллионов записей эффективно
3. **Отказоустойчивость** — продолжение обработки после сбоев
4. **Мониторинг** — подробная статистика выполнения
5. **Гибкость** — поддержка различных источников и приемников данных
6. **Интеграция** — бесшовная интеграция с **Spring** экосистемой
7. **Транзакционность** — **ACID**-подобная обработка данных
8. **Перезапуск** — возможность перезапуска с места остановки

### Когда использовать Spring Batch?

#### ✅ Идеально подходит для:
- **ETL процессы** — извлечение, трансформация, загрузка данных
- **Data Migration** — миграция данных между системами
- **Report Generation** — генерация сложных отчетов
- **File Processing** — обработка больших файлов (CSV, `XML`, JSON)
- **Database Updates** — массовые обновления базы данных
- **Integration Tasks** — интеграция с внешними системами
- **Scheduled Tasks** — регулярные пакетные операции
- **Data Validation** — валидация больших объемов данных

#### ❌ Не подходит для:
- **Real-time Processing** — используйте **Kafka Streams** или **Spring Integration**
- **Simple CRUD** — используйте обычные **Spring Data** репозитории
- **User Interactions** — используйте **Spring MVC**/**WebFlux**
- **Small Datasets** — накладные расходы не оправданы
- **Streaming Data** — используйте **Kafka** или **RabbitMQ**

### Основные возможности

#### Transaction Management
**Spring Batch** предоставляет декларативное управление транзакциями:**

**Chunk-based Processing** — обработка данных порциями в транзакциях:**

```text
Transaction 1: Process items 1-100
Transaction 2: Process items 101-200
Transaction 3: Process items 201-300
```

**Skip/Restart Logic** — пропуск проблемных элементов и перезапуск:
```text
Item 150 fails → Skip and continue
Job fails at 75% → Restart from last checkpoint
```

#### Job Repository
**Хранит метаданные выполнения заданий:**

**Job Executions** — информация о каждом запуске **job**'а:**
- **Job execution** `ID`
- **Start**/**end times**
- **Status** (STARTING, `STARTED`, `STOPPING`, `STOPPED`, `FAILED`, `COMPLETED`, ABANDONED)
- **Exit code and description**

**Step Executions** — информация о каждом шаге:**
- **Step execution** `ID`
- **Step name**
- **Read**/**write counts**
- **Commit**/**rollback counts**
- **Status information**

**Job Parameters** — параметры запуска **job**'а:**
- **Identifying parameters** (affect job identity)
- **Non-identifying parameters** (configuration only)

## Архитектура и концепции

### Core Components

#### Job
**Job** — это сущность, представляющая собой полную пакетную операцию. **Job** состоит из одного или нескольких **Step**'ов и определяет, как и когда выполнять эти шаги.

**Job Configuration** определяет:**
- **Job Name** — уникальный идентификатор **job**'а
- **Step Sequence** — порядок выполнения шагов
- **Job Parameters** — параметры конфигурации
- **Restartability** — возможность перезапуска
- **Listeners** — обработчики событий **job**'а

**Job `States`:**
- **None** — **job** не запускался
- **Started** — **job** выполняется
- **Completed** — **job** успешно завершен
- **Failed** — **job** завершился с ошибкой
- **Stopped** — **job** был остановлен
- **Abandoned** — **job** был **abandoned**

#### Step
**Step** — это независимая фаза обработки в **job**'е. Каждый **step** выполняет определенную задачу и может быть перезапущен независимо.

**Step `Types`:**
- **Tasklet Step** — выполняет произвольную логику в одном методе
- **Chunk-oriented Step** — обрабатывает данные порциями (chunks)
- **Partitioned Step** — параллельная обработка с **partitioning**
- **Job Step** — запускает другой **job** как **step**

**Step `Configuration`:**
- **Step Name** — уникальный идентификатор в **job**'е
- **Reader** — источник данных для чтения
- **Processor** — логика обработки элементов
- **Writer** — место назначения для записи результатов
- **Chunk Size** — размер порции для обработки
- **Skip/`Retry` Logic** — обработка ошибок
- **Listeners** — обработчики событий **step**'а

#### Execution Context
**Execution Context** — это механизм хранения состояния между запусками **job**'ов и **step**'ов:**

**Job Execution Context** — сохраняется между перезапусками **job**'а:
- **Current step progress**
- **Processed item count**
- **Custom state information**
- **Restart checkpoint data**

**Step Execution Context** — сохраняется между **chunk**'ами в **step**'е:
- **Current reader position**
- **Writer state**
- **Processing statistics**
- **Custom step data**

### Job Repository

**Job Repository** — это **persistence** механизм для хранения метаданных выполнения:**

**Database `Tables`:**
- **BATCH_JOB_INSTANCE** — уникальные **job instances**
- **BATCH_JOB_EXECUTION** — выполнения **job**'ов
- **BATCH_JOB_EXECUTION_PARAMS** — параметры выполнения
- **BATCH_JOB_EXECUTION_CONTEXT** — контекст выполнения
- **BATCH_STEP_EXECUTION** — выполнения **step**'ов
- **BATCH_STEP_EXECUTION_CONTEXT** — контекст **step**'ов

**Configuration `Options`:**
```properties
# H2 (для разработки)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# PostgreSQL (для production)
spring.datasource.url=jdbc:postgresql://localhost:5432/batchdb
spring.datasource.username=batchuser
spring.datasource.password=batchpass

# Batch конфигурация
spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=false
```

## Job и Step конфигурация

### Basic Job Configuration

#### Java-based Configuration

Пример **Java-based** конфигурации **Job** и **Step** в **Spring Batch**.

```java
// Конфигурация Job importUserJob и Step step1 через JobBuilderFactory
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer()) // Увеличивает ID для каждого запуска
                .listener(listener)                   // Слушатель завершения job'а
                .flow(step1)                         // Определяет flow выполнения
                .end()                               // Завершает определение job'а
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("step1")
                .tasklet(new HelloWorldTasklet()) // Tasklet для выполнения
                .build();
    }
}
```

**Разбор конфигурации:**

**`@EnableBatchProcessing` — включает **Spring Batch** и создает необходимые бины:**
- **JobBuilderFactory** для создания **job**'ов
- **StepBuilderFactory** для создания **step**'ов
- **JobRepository** для хранения метаданных
- **JobLauncher** для запуска **job**'ов

**`jobBuilderFactory.get("`importUserJob`")` — создает **job** с именем "**importUserJob**":**
- Имя используется для идентификации **job**'а
- Должно быть уникальным в приложении

**`.incrementer(new `RunIdIncrementer`())` — добавляет **incrementer** параметров:**
- Каждый запуск получает новый `ID`
- Позволяет запускать один **job** многократно
- Параметры хранятся в **BATCH_JOB_EXECUTION_PARAMS**

**`.listener(listener)` — добавляет слушатель событий **job**'а:**
- **JobCompletionNotificationListener** получает уведомления о завершении
- Может логировать, отправлять **email**, вызывать другие системы

**`.flow(step1)` — определяет последовательность выполнения:**
- **step1** будет выполнен первым
- Можно добавить условия и ветвления (**.on().to().**from**())

**`.end()` — завершает определение **job**'а:**
- Возвращает **JobBuilder** для финального **build**()

**`.build()` — создает экземпляр **Job**:**
- Валидирует конфигурацию
- Регистрирует **job** в **JobRegistry**

#### XML-based Configuration
```xml
<!-- Конфигурация Job и Step в XML: tasklet, listeners -->
<batch:job id="importUserJob" incrementer="runIdIncrementer">
    <batch:step id="step1" next="step2">
        <batch:tasklet ref="helloWorldTasklet"/>
    </batch:step>
    <batch:step id="step2">
        <batch:tasklet ref="cleanupTasklet"/>
    </batch:step>
    <batch:listeners>
        <batch:listener ref="jobCompletionListener"/>
    </batch:listeners>
</batch:job>
```

### Job Parameters

#### Parameter Types
```java
// Job с параметрами: inputFile, chunkSize, runDate (JobParametersBuilder)
@Bean
public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
    return jobBuilderFactory.get("parameterizedJob")
            .incrementer(new RunIdIncrementer())
            .parameters(
                new JobParametersBuilder()
                    .addString("inputFile", "data.csv")
                    .addLong("chunkSize", 100L)
                    .addDate("runDate", new Date())
                    .toJobParameters()
            )
            .start(step)
            .build();
}
```

**Типы параметров:**
- **String** — текстовые значения ("`inputFile`", "`outputPath`")
- **Long** — числовые значения (100L, 1000L)
- **Double** — дробные числа (`1.5`, `99.99`)
- **Date** — даты и время (**new `Date`(), **run date**)

**Идентифицирующие vs Неидентифицирующие:**
```java
// Идентифицирующие (влияют на идентичность job'а)
@Bean
public Job fileProcessingJob() {
    return jobBuilderFactory.get("fileProcessingJob")
            .start(step)
            .build();
}

// Пример использования:
JobParameters params1 = new JobParametersBuilder()
    .addString("inputFile", "file1.csv")  // Идентифицирующий
    .addLong("timestamp", System.currentTimeMillis()) // Неидентифицирующий
    .toJobParameters();

JobParameters params2 = new JobParametersBuilder()
    .addString("inputFile", "file2.csv")  // Разные файлы = разные job instances
    .addLong("timestamp", System.currentTimeMillis())
    .toJobParameters();
```

### Step Configuration

#### Tasklet Step
```java
@Bean
public Step taskletStep(StepBuilderFactory stepBuilderFactory) {
    return stepBuilderFactory.get("taskletStep")
            .tasklet((contribution, chunkContext) -> {
                // Логика выполнения
                System.out.println("Executing tasklet step");

                // Возвращаем RepeatStatus
                return RepeatStatus.FINISHED;
            })
            .build();
}
```

**Tasklet `Interface`:**
```java
// Интерфейс Tasklet: один шаг выполняется в методе execute
public interface Tasklet {
    RepeatStatus execute(StepContribution contribution,
                        ChunkContext chunkContext) throws Exception;
}
```

**RepeatStatus значения:**
- **FINISHED** — **step** завершен успешно
- **CONTINUABLE** — **step** должен продолжить выполнение

#### Chunk-oriented Step
```java
@Bean
public Step chunkStep(StepBuilderFactory stepBuilderFactory,
                     ItemReader<Person> reader,
                     ItemProcessor<Person, Person> processor,
                     ItemWriter<Person> writer) {
    return stepBuilderFactory.get("chunkStep")
            .<Person, Person>chunk(10)  // Размер chunk'а
            .reader(reader)             // Источник данных
            .processor(processor)       // Обработка элементов
            .writer(writer)             // Запись результатов
            .faultTolerant()            // Обработка ошибок
            .skipLimit(5)              // Максимум пропусков
            .skip(ValidationException.class) // Какие исключения пропускать
            .retryLimit(3)             // Максимум повторных попыток
            .retry(IOException.class)  // Какие исключения повторять
            .build();
}
```

**Chunk `Processing Flow`:**
```text
Reader reads 10 items → Processor processes each → Writer writes 10 items → Commit
Reader reads next 10 → Processor processes each → Writer writes 10 → Commit
...
```

## Item Readers

### Flat File Reader

#### CSV File Reader
```java
@Bean
@StepScope
public FlatFileItemReader<Person> personCsvReader(
        @Value("#{jobParameters['inputFile']}") String inputFile) {

    return new FlatFileItemReaderBuilder<Person>()
            .name("personCsvReader")
            .resource(new FileSystemResource(inputFile))
            .delimited()
            .names(new String[]{"firstName", "lastName", "email", "age"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }})
            .build();
}
```

**Конфигурация параметров:**

**`.name("`personCsvReader`")` — уникальное имя **reader**'а:**
- Используется для логирования и мониторинга
- Должно быть уникальным в **job**'е

**`.resource(new `FileSystemResource`(inputFile))` — источник файла:**
- **FileSystemResource** для локальных файлов
- **ClassPathResource** для файлов в **classpath**
- **UrlResource** для удаленных файлов

**`.delimited()` — указывает на разделители:**
- По умолчанию запятая
- Можно указать **custom delimiter**: .**delimiter("|")

**`.names(new `String`[]{"`firstName`", "`lastName`", "email", "age"})` — имена полей:**
- Должны соответствовать заголовкам **CSV**
- Используются для **mapping** в объект

**`.fieldSetMapper(...)` — преобразование полей в объект:**
- **BeanWrapperFieldSetMapper** использует **setters**
- Можно реализовать **custom FieldSetMapper**

#### Fixed Width File Reader
```java
@Bean
@StepScope
public FlatFileItemReader<Person> personFixedWidthReader() {
    return new FlatFileItemReaderBuilder<Person>()
            .name("personFixedWidthReader")
            .resource(new ClassPathResource("data/fixed-width.txt"))
            .fixedLength()
            .columns(new Range[]{new Range(1, 20), new Range(21, 40), new Range(41, 60)})
            .names(new String[]{"name", "email", "phone"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }})
            .build();
}
```

**Fixed `Width` параметры:**

**`.fixedLength()` — указывает на фиксированную ширину полей:**
- Каждое поле имеет фиксированную позицию и длину

**`.columns(new `Range`[]{new `Range`(1, 20), new Range(21, 40), new Range(41, 60)})` — диапазоны колонок:**
- **Range**(1, 20) — символы с 1 по 20
- **Range**(21, 40) — символы с 21 по 40
- **Range**(41, 60) — символы с 41 по 60

### Database Readers

#### JDBC Cursor Reader
```java
@Bean
@StepScope
public JdbcCursorItemReader<Person> personJdbcReader(
        @Value("#{jobParameters['lastUpdate']}") Date lastUpdate) {

    return new JdbcCursorItemReaderBuilder<Person>()
            .name("personJdbcReader")
            .dataSource(dataSource)
            .sql("SELECT id, first_name, last_name, email FROM person WHERE last_update > ?")
            .parameters(new Object[]{lastUpdate})
            .rowMapper(new BeanPropertyRowMapper<>(Person.class))
            .build();
}
```

**JDBC `Reader` параметры:**

**`.dataSource(dataSource)` — источник данных:**
- **DataSource** для подключения к базе данных
- Поддерживает **connection pooling**

**`.sql("`SELECT` ...")` — **SQL** запрос:**
- Может содержать параметры (?)
- Должен возвращать **ResultSet** подходящий для **mapping**

**`.parameters(new `Object`[]{`lastUpdate`})` — параметры запроса:**
- Передаются в **prepared statement**
- Могут быть получены из **job parameters**

**`.rowMapper(...)` — **mapping** строк в объекты:**
- **BeanPropertyRowMapper** использует **column names**
- Можно реализовать **custom RowMapper**

#### JPA Reader
```java
@Bean
@StepScope
public JpaCursorItemReader<Person> personJpaReader() {
    return new JpaCursorItemReaderBuilder<Person>()
            .name("personJpaReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT p FROM Person p WHERE p.status = :status")
            .parameterValues(Map.of("status", PersonStatus.ACTIVE))
            .build();
}
```

**JPA `Reader` особенности:**

**`.entityManagerFactory(entityManagerFactory)` — **JPA EntityManagerFactory**:**
- Использует **JPA** для чтения данных
- Поддерживает **lazy loading**

**`.queryString("`SELECT` p `FROM Person p`...")` — **JPQL** запрос:**
- Использует **JPA entity names**
- Поддерживает **joins** и **complex queries**

**`.parameterValues(...)` — параметры запроса:**
- **Map** с именами параметров
- Поддерживает все **JPA** типы параметров

### Custom Readers

#### Custom ItemReader
```java
public class CustomPersonReader implements ItemReader<Person> {

    private final PersonService personService;
    private Iterator<Person> personIterator;
    private boolean initialized = false;

    public CustomPersonReader(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person read() throws Exception {
        if (!initialized) {
            // Lazy initialization
            List<Person> persons = personService.findAllActivePersons();
            this.personIterator = persons.iterator();
            this.initialized = true;
        }

        if (personIterator.hasNext()) {
            Person person = personIterator.next();
            // Custom processing logic
            enrichPersonData(person);
            return person;
        }

        return null; // End of data
    }

    private void enrichPersonData(Person person) {
        // Custom enrichment logic
        person.setFullName(person.getFirstName() + " " + person.getLastName());
    }
}
```

**ItemReader `Contract`:**
- **read()** возвращает следующий элемент или **null** если данных больше нет
- **Thread-safe** — может использоваться в многопоточной среде
- **Stateless** — состояние хранится в **ExecutionContext** если нужно
- **Serializable** — для перезапуска **job**'ов

## Item Writers

### Database Writers

#### JDBC Batch Writer
```java
@Bean
public JdbcBatchItemWriter<Person> personJdbcWriter() {
    return new JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql("INSERT INTO person (first_name, last_name, email, age) VALUES (?, ?, ?, ?)")
            .itemPreparedStatementSetter((person, ps) -> {
                ps.setString(1, person.getFirstName());
                ps.setString(2, person.getLastName());
                ps.setString(3, person.getEmail());
                ps.setInt(4, person.getAge());
            })
            .build();
}
```

**JDBC `Writer` параметры:**

**`.dataSource(dataSource)` — **DataSource** для подключения:**
- Использует **JDBC batch updates** для производительности

**`.sql("`INSERT INTO`...")` — **SQL** для вставки:**
- **Prepared statement** с параметрами
- Поддерживает **INSERT**, **UPDATE**, **DELETE**

**`.itemPreparedStatementSetter(...)` — настройка параметров:**
- Вызывается для каждого **item**'а
- Устанавливает значения в **prepared statement**

#### JPA Writer
```java
@Bean
public JpaItemWriter<Person> personJpaWriter() {
    JpaItemWriter<Person> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(entityManagerFactory);
    return writer;
}
```

**JPA `Writer` особенности:**
- Автоматически сохраняет **entities**
- Поддерживает **batch inserts**/**updates**
- Управляет транзакциями через **Spring**

### File Writers

#### CSV File Writer
```java
@Bean
@StepScope
public FlatFileItemWriter<Person> personCsvWriter(
        @Value("#{jobParameters['outputFile']}") String outputFile) {

    return new FlatFileItemWriterBuilder<Person>()
            .name("personCsvWriter")
            .resource(new FileSystemResource(outputFile))
            .delimited()
            .delimiter(",")
            .names(new String[]{"id", "firstName", "lastName", "email", "age"})
            .headerCallback(writer -> writer.write("ID,First Name,Last Name,Email,Age"))
            .footerCallback(writer -> writer.write("Total records: " + getProcessedCount()))
            .build();
}
```

**CSV `Writer` параметры:**

**`.delimited()` — разделитель полей:**
- По умолчанию запятая
- Можно указать **custom delimiter**

**`.names(...)` — имена полей для записи:**
- Должны соответствовать **getter**'ам объекта

**`.headerCallback(...)` — заголовок файла:**
- Записывается в начало файла
- Полезно для **CSV** с заголовками

**`.footerCallback(...)` — **footer** файла:**
- Записывается в конец файла
- Можно использовать для статистики

#### XML File Writer
```java
@Bean
@StepScope
public StaxEventItemWriter<Person> personXmlWriter() {
    return new StaxEventItemWriterBuilder<Person>()
            .name("personXmlWriter")
            .resource(new FileSystemResource("output/persons.xml"))
            .marshaller(personMarshaller())
            .rootTagName("persons")
            .build();
}

@Bean
public XStreamMarshaller personMarshaller() {
    Map<String, Class<?>> aliases = Map.of(
        "person", Person.class,
        "persons", List.class
    );

    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.setAliases(aliases);
    return marshaller;
}
```

**XML `Writer` особенности:**
- Использует **StAX** для эффективной записи **XML**
- **XStreamMarshaller** для сериализации объектов
- Поддерживает **root element** и **namespace**

### Composite Writers

#### MultiResource Writer
```java
@Bean
@StepScope
public MultiResourceItemWriter<Person> multiFileWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir) {

    // Base writer для одного файла
    FlatFileItemWriter<Person> delegate = new FlatFileItemWriterBuilder<Person>()
            .name("personCsvWriter")
            .delimited()
            .names(new String[]{"id", "firstName", "lastName", "email"})
            .build();

    // Multi-resource wrapper
    MultiResourceItemWriter<Person> writer = new MultiResourceItemWriter<>();
    writer.setDelegate(delegate);
    writer.setResource(new FileSystemResource(outputDir + "/persons-%d.csv"));
    writer.setItemCountLimitPerResource(1000); // 1000 записей на файл

    return writer;
}
```

**MultiResource параметры:**
- **Delegate** — базовый **writer** для каждого файла
- **Resource pattern** — шаблон имен файлов (%d для номера)
- **Item count limit** — максимум элементов на файл
- **Save state** — сохраняет состояние для перезапуска

#### Composite Writer
```java
@Bean
public CompositeItemWriter<Person> compositeWriter() {
    List<ItemWriter<Person>> writers = List.of(
        personDatabaseWriter(),  // Пишем в базу
        personCsvWriter(),       // Пишем в CSV
        personXmlWriter()        // Пишем в XML
    );

    CompositeItemWriter<Person> writer = new CompositeItemWriter<>();
    writer.setDelegates(writers);
    return writer;
}
```

**Composite `Writer` особенности:**
- Выполняет несколько **writer**'ов последовательно
- Все **writer**'ы должны успешно завершить запись
- Полезно для одновременной записи в несколько мест

## Chunk Processing

### Chunk-oriented Processing

#### Chunk Size Configuration
```java
@Bean
public Step chunkProcessingStep(StepBuilderFactory stepBuilderFactory) {
    return stepBuilderFactory.get("chunkProcessingStep")
            .<Person, Person>chunk(50)  // Process 50 items at a time
            .reader(personReader())
            .processor(personProcessor())
            .writer(personWriter())
            .faultTolerant()
            .skipLimit(10)                    // Skip up to 10 items
            .skip(ValidationException.class)  // Skip validation errors
            .retryLimit(3)                    // Retry up to 3 times
            .retry(RuntimeException.class)    // Retry runtime exceptions
            .build();
}
```

**Chunk `Processing Flow`:**
```text
1. Reader читает 50 элементов
2. Processor обрабатывает каждый элемент
3. Writer записывает 50 элементов
4. Транзакция commit
5. Повтор цикла до исчерпания данных
```

**Почему chunk'и важны:**
- **Memory efficiency** — ограниченное использование памяти
- **Transaction boundaries** — контролируемые транзакции
- **Error recovery** — перезапуск с последнего **commit**'а
- **Performance** — **batch operations** для I/O

### Item Processor

#### Simple Processor
```java
public class PersonProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        // Validation
        if (person.getEmail() == null || person.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }

        // Transformation
        person.setEmail(person.getEmail().toLowerCase());

        // Enrichment
        person.setFullName(person.getFirstName() + " " + person.getLastName());

        // Filtering (return null to filter out)
        if (person.getAge() < 18) {
            return null; // Filter out minors
        }

        return person;
    }
}
```

**Processor `Contract`:**
- **Input** — элемент для обработки
- **Output** — обработанный элемент или **null** для фильтрации
- **Exception** — для ошибок обработки
- **Idempotent** — многократный вызов безопасен

#### Composite Processor
```java
@Bean
public CompositeItemProcessor<Person, Person> personCompositeProcessor() {
    List<ItemProcessor<Person, Person>> processors = List.of(
        validationProcessor(),
        transformationProcessor(),
        enrichmentProcessor()
    );

    CompositeItemProcessor<Person, Person> processor = new CompositeItemProcessor<>();
    processor.setDelegates(processors);
    return processor;
}

@Bean
public ItemProcessor<Person, Person> validationProcessor() {
    return person -> {
        // Validation logic
        if (person.getFirstName() == null) {
            throw new ValidationException("First name required");
        }
        return person;
    };
}

@Bean
public ItemProcessor<Person, Person> transformationProcessor() {
    return person -> {
        // Transformation logic
        person.setFirstName(capitalize(person.getFirstName()));
        person.setLastName(capitalize(person.getLastName()));
        return person;
    };
}

@Bean
public ItemProcessor<Person, Person> enrichmentProcessor() {
    return person -> {
        // Enrichment logic
        person.setRegistrationDate(LocalDateTime.now());
        return person;
    };
}
```

**Composite `Processor` преимущества:**
- **Modular processing** — разделение ответственности
- **Reusable components** — переиспользование процессоров
- **Testability** — тестирование отдельных шагов
- **Maintainability** — легкость изменений

## Error Handling

### Skip Logic

#### Skip Configuration
```java
@Bean
public Step faultTolerantStep(StepBuilderFactory stepBuilderFactory) {
    return stepBuilderFactory.get("faultTolerantStep")
            .<Person, Person>chunk(10)
            .reader(personReader())
            .processor(personProcessor())
            .writer(personWriter())
            .faultTolerant()
            .skipLimit(5)                           // Максимум 5 пропусков
            .skip(ValidationException.class)        // Пропускать ValidationException
            .skip(DataIntegrityViolationException.class) // Пропускать DB ошибки
            .skipPolicy(personSkipPolicy())         // Custom skip policy
            .build();
}
```

**Skip `Logic` параметры:**

**`.skipLimit(5)` — максимальное количество пропусков:**
- После превышения лимита **step fails**
- Можно использовать **percentage**: **skipLimit(10%)

**`.skip(Exception.class)` — какие исключения пропускать:**
- **ValidationException** — бизнес-логика ошибки
- **DataIntegrityViolationException** — `DB` **constraint violations**
- **Custom exceptions**

**`.skipPolicy(...)` — **custom** политика пропусков:**
- Реализует **SkipPolicy interface**
- Позволяет **complex** логику принятия решения

#### Custom Skip Policy
```java
public class PersonSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) {
        // Пропускать только определенные ошибки
        if (t instanceof ValidationException) {
            ValidationException ve = (ValidationException) t;
            // Пропускать только если ошибка не критична
            return !ve.isCritical();
        }

        // Для других исключений проверять количество пропусков
        return skipCount < 3;
    }
}
```

### Retry Logic

#### Retry Configuration
```java
@Bean
public Step retryStep(StepBuilderFactory stepBuilderFactory) {
    return stepBuilderFactory.get("retryStep")
            .<Person, Person>chunk(5)
            .reader(personReader())
            .processor(personProcessor())
            .writer(personWriter())
            .faultTolerant()
            .retryLimit(3)                          // Максимум 3 попытки
            .retry(IOException.class)               // Повторять IOException
            .retry(HttpServerErrorException.class)  // Повторять HTTP 5xx
            .retryPolicy(personRetryPolicy())       // Custom retry policy
            .backOffPolicy(exponentialBackOff())    // Backoff strategy
            .build();
}
```

**Retry `Logic` параметры:**

**`.retryLimit(3)` — максимальное количество попыток:**
- 1 **initial** + 3 **retries** = 4 **total attempts**
- После исчерпания **retry step fails**

**`.retry(Exception.class)` — какие исключения повторять:**
- **IOException** — **network** проблемы
- **HttpServerErrorException** — временные **server** ошибки
- **TimeoutException** — **timeout**'ы

**`.retryPolicy(...)` — **custom** политика повторений:**
- **Complex** логика принятия решения
- **State-based retry logic**

**`.backOffPolicy(...)` — стратегия задержки:**
- **Fixed backoff** — фиксированная задержка
- **Exponential backoff** — экспоненциальная задержка
- **Random backoff** — случайная задержка

#### Backoff Policies
```java
@Bean
public BackOffPolicy fixedBackOff() {
    FixedBackOffPolicy policy = new FixedBackOffPolicy();
    policy.setBackOffPeriod(1000); // 1 second delay
    return policy;
}

@Bean
public BackOffPolicy exponentialBackOff() {
    ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
    policy.setInitialInterval(1000);    // Initial 1 second
    policy.setMultiplier(2.0);          // Double each time
    policy.setMaxInterval(30000);       // Max 30 seconds
    return policy;
}

@Bean
public BackOffPolicy randomBackOff() {
    ExponentialRandomBackOffPolicy policy = new ExponentialRandomBackOffPolicy();
    policy.setInitialInterval(1000);
    policy.setMultiplier(2.0);
    policy.setMaxInterval(30000);
    return policy;
}
```

### Error Handling Strategies

#### Circuit Breaker Pattern
```java
public class CircuitBreakerProcessor implements ItemProcessor<Person, Person> {

    private final CircuitBreaker circuitBreaker;
    private final ExternalServiceClient externalClient;

    public CircuitBreakerProcessor(ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
        this.circuitBreaker = CircuitBreaker.ofDefaults("externalService");
    }

    @Override
    public Person process(Person person) throws Exception {
        try {
            return circuitBreaker.decorateCallable(() -> {
                // Call external service
                ExternalData data = externalClient.enrichPerson(person.getId());
                person.setExternalData(data);
                return person;
            }).call();

        } catch (CallNotPermittedException e) {
            // Circuit breaker is OPEN
            // Continue without external data or throw exception
            person.setExternalData(null);
            return person;
        }
    }
}
```

## Listeners и Callbacks

### Job Listeners

#### Job Execution Listener
```java
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private EmailService emailService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job {} starting at {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getStartTime());

        // Pre-job logic
        initializeJobMetrics(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job {} finished with status {} at {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getStatus(),
            jobExecution.getEndTime());

        // Post-job logic
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            sendCompletionEmail(jobExecution);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            sendFailureEmail(jobExecution);
        }

        cleanupJobResources(jobExecution);
    }

    private void initializeJobMetrics(JobExecution jobExecution) {
        // Initialize metrics collection
        jobExecution.getExecutionContext().put("startTime", System.currentTimeMillis());
    }

    private void sendCompletionEmail(JobExecution jobExecution) {
        String subject = "Job Completed: " + jobExecution.getJobInstance().getJobName();
        String body = buildCompletionMessage(jobExecution);
        emailService.sendEmail("admin@company.com", subject, body);
    }

    private void sendFailureEmail(JobExecution jobExecution) {
        String subject = "Job Failed: " + jobExecution.getJobInstance().getJobName();
        String body = buildFailureMessage(jobExecution);
        emailService.sendEmail("admin@company.com", subject, body);
    }

    private void cleanupJobResources(JobExecution jobExecution) {
        // Cleanup temporary files, connections, etc.
    }
}
```

**Job `Listener Events`:**
- **beforeJob** — перед началом выполнения **job**'а
- **afterJob** — после завершения **job**'а (успешно или с ошибкой)

### Step Listeners

#### Step Execution Listener
```java
@Component
public class StepExecutionListener implements org.springframework.batch.core.StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Step " + stepExecution.getStepName() + " starting");

        // Initialize step metrics
        stepExecution.getExecutionContext().put("stepStartTime", System.currentTimeMillis());
        stepExecution.getExecutionContext().put("itemsProcessed", 0L);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Step " + stepExecution.getStepName() + " finished with status: " + stepExecution.getStatus());

        // Calculate step metrics
        long duration = System.currentTimeMillis() -
            (Long) stepExecution.getExecutionContext().get("stepStartTime");

        System.out.println("Step duration: " + duration + "ms");
        System.out.println("Items read: " + stepExecution.getReadCount());
        System.out.println("Items written: " + stepExecution.getWriteCount());
        System.out.println("Items processed: " + stepExecution.getCommitCount());
        System.out.println("Items skipped: " + stepExecution.getSkipCount());

        // Custom exit status logic
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            return ExitStatus.COMPLETED;
        } else {
            return ExitStatus.FAILED;
        }
    }
}
```

#### Chunk Listener
```java
@Component
public class ChunkExecutionListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("Starting chunk processing");

        // Pre-chunk logic
        // Could initialize resources, start timers, etc.
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("Chunk processing completed");

        // Post-chunk logic
        StepExecution stepExecution = context.getStepContext().getStepExecution();

        System.out.println("Chunk committed items: " + stepExecution.getCommitCount());
        System.out.println("Chunk read items: " + stepExecution.getReadCount());
        System.out.println("Chunk write items: " + stepExecution.getWriteCount());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("Chunk processing failed");

        // Error handling logic
        // Could log error details, send notifications, etc.
        Exception exception = (Exception) context.getAttribute("exception");
        if (exception != null) {
            System.err.println("Chunk error: " + exception.getMessage());
        }
    }
}
```

#### Item Read/Write/Process Listeners
```java
@Component
public class ItemProcessingListener implements
        ItemReadListener<Person>,
        ItemProcessListener<Person, Person>,
        ItemWriteListener<Person> {

    @Override
    public void beforeRead() {
        // Before reading an item
    }

    @Override
    public void afterRead(Person person) {
        // After successfully reading an item
        System.out.println("Read person: " + person.getEmail());
    }

    @Override
    public void onReadError(Exception ex) {
        // When reading fails
        System.err.println("Read error: " + ex.getMessage());
    }

    @Override
    public void beforeProcess(Person person) {
        // Before processing an item
        System.out.println("Processing person: " + person.getEmail());
    }

    @Override
    public void afterProcess(Person person, Person result) {
        // After successfully processing an item
        System.out.println("Processed person: " + result.getEmail());
    }

    @Override
    public void onProcessError(Person person, Exception e) {
        // When processing fails
        System.err.println("Process error for " + person.getEmail() + ": " + e.getMessage());
    }

    @Override
    public void beforeWrite(List<? extends Person> items) {
        // Before writing a chunk
        System.out.println("Writing " + items.size() + " items");
    }

    @Override
    public void afterWrite(List<? extends Person> items) {
        // After successfully writing a chunk
        System.out.println("Successfully wrote " + items.size() + " items");
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Person> items) {
        // When writing fails
        System.err.println("Write error for " + items.size() + " items: " + exception.getMessage());
    }
}
```

## Parallel Processing

### Multi-threaded Step

#### Thread Pool Configuration
```java
@Bean
public Step multiThreadedStep(StepBuilderFactory stepBuilderFactory) {
    return stepBuilderFactory.get("multiThreadedStep")
            .<Person, Person>chunk(100)
            .reader(personReader())
            .processor(personProcessor())
            .writer(personWriter())
            .taskExecutor(taskExecutor())  // Thread pool для параллельной обработки
            .throttleLimit(4)             // Максимум 4 параллельных chunk'а
            .build();
}

@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("batch-thread-");
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());
    return executor;
}
```

**Multi-threading параметры:**

**`.taskExecutor(taskExecutor())` — **thread pool** для выполнения:**
- **ThreadPoolTaskExecutor** для управления потоками
- **SimpleAsyncTaskExecutor** для **unlimited threads** (не рекомендуется)

**`.throttleLimit(4)` — максимум одновременных **chunk**'ов:**
- Ограничивает **parallelism** для контроля ресурсов
- Предотвращает перегрузку системы

**Важные аспекты `multi-threading`:**
- **Thread Safety** — **reader**/**processor**/**writer** должны быть **thread-safe**
- **Resource Contention** — **database connections**, **external services**
- **Memory Usage** — увеличенное потребление памяти
- **Ordering** — порядок обработки может быть неопределенным

### Partitioned Step

#### Partitioning Configuration
```java
@Configuration
public class PartitionedStepConfig {

    @Bean
    public Step partitionedStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("partitionedStep")
                .partitioner("slaveStep", partitioner())  // Partitioner для разделения работы
                .step(slaveStep())                         // Шаг для выполнения на каждой партиции
                .taskExecutor(taskExecutor())             // Executor для параллельного выполнения
                .build();
    }

    @Bean
    public Step slaveStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("slaveStep")
                .<Person, Person>chunk(100)
                .reader(partitionedReader(null))  // Reader с partition context
                .processor(personProcessor())
                .writer(partitionedWriter(null))  // Writer с partition context
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Person> partitionedReader(
            @Value("#{stepExecutionContext['partition']}") String partition) {

        // Создаем reader для конкретной партиции
        return new PartitionedPersonReader(partition);
    }

    @Bean
    @StepScope
    public ItemWriter<Person> partitionedWriter(
            @Value("#{stepExecutionContext['partition']}") String partition) {

        // Создаем writer для конкретной партиции
        return new PartitionedPersonWriter(partition);
    }

    @Bean
    public Partitioner partitioner() {
        return new CustomPartitioner();
    }
}
```

#### Custom Partitioner
```java
public class CustomPartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.put("partition", "partition" + i);
            context.put("minId", i * 1000);      // 0, 1000, 2000, ...
            context.put("maxId", (i + 1) * 1000); // 1000, 2000, 3000, ...

            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}
```

**Partitioning преимущества:**
- **Scalability** — распределение работы между **nodes**
- **Fault Isolation** — **failure** в одной партиции не влияет на другие
- **Resource Optimization** — эффективное использование **CPU**/**memory**
- **Load Balancing** — равномерное распределение нагрузки

## Scaling и Performance

### Performance Optimization

#### Reader Optimization
```java
@Bean
@StepScope
public JdbcCursorItemReader<Person> optimizedReader() {
    return new JdbcCursorItemReaderBuilder<Person>()
            .name("optimizedReader")
            .dataSource(dataSource)
            .sql("""
                SELECT id, first_name, last_name, email
                FROM person
                WHERE status = 'ACTIVE'
                ORDER BY id  -- Важно для cursor stability
                """)
            .rowMapper(new BeanPropertyRowMapper<>(Person.class))
            .maxRows(10000)        // Limit для тестирования
            .queryTimeout(300)     // 5 minute timeout
            .build();
}
```

**Reader оптимизации:**
- **ORDER BY** — важен для **cursor stability**
- **WHERE clauses** — фильтрация на **database** уровне
- **Indexes** — правильные индексы на фильтруемых полях
- **Fetch size** — размер **batch fetch** (для JDBC)

#### Writer Optimization
```java
@Bean
public JdbcBatchItemWriter<Person> optimizedWriter() {
    return new JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql("""
                INSERT INTO person (first_name, last_name, email, created_at)
                VALUES (?, ?, ?, ?)
                """)
            .itemPreparedStatementSetter((person, ps) -> {
                ps.setString(1, person.getFirstName());
                ps.setString(2, person.getLastName());
                ps.setString(3, person.getEmail());
                ps.setTimestamp(4, Timestamp.valueOf(person.getCreatedAt()));
            })
            .assertUpdates(false)  // Отключаем проверки для производительности
            .build();
}
```

**Writer оптимизации:**
- **Batch inserts** — использование **JDBC batch**
- **Connection pooling** — переиспользование **connections**
- **Prepared statements** — **compiled queries**
- **Minimal logging** — отключение ненужного **logging**

#### Processor Optimization
```java
public class OptimizedPersonProcessor implements ItemProcessor<Person, Person> {

    private final ValidationService validationService;
    private final EnrichmentService enrichmentService;

    // Cache для часто используемых данных
    private final LoadingCache<String, Country> countryCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Country>() {
                @Override
                public Country load(String countryCode) {
                    return enrichmentService.getCountryByCode(countryCode);
                }
            });

    @Override
    public Person process(Person person) throws Exception {
        // Параллельная валидация и enrichment
        CompletableFuture<Void> validationFuture = CompletableFuture.runAsync(() ->
            validationService.validatePerson(person));

        CompletableFuture<Void> enrichmentFuture = CompletableFuture.runAsync(() -> {
            try {
                Country country = countryCache.get(person.getCountryCode());
                person.setCountryName(country.getName());
            } catch (Exception e) {
                // Handle cache miss
                person.setCountryName("Unknown");
            }
        });

        // Wait for both operations
        CompletableFuture.allOf(validationFuture, enrichmentFuture).get();

        return person;
    }
}
```

### Memory Management

#### Chunk Size Tuning
```java
@Configuration
public class PerformanceConfig {

    @Value("${batch.chunk.size:100}")
    private int chunkSize;

    @Value("${batch.skip.limit:10}")
    private int skipLimit;

    @Value("${batch.retry.limit:3}")
    private int retryLimit;

    @Bean
    public Step tunedStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("tunedStep")
                .<Person, Person>chunk(chunkSize)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipLimit(skipLimit)
                .retryLimit(retryLimit)
                .build();
    }
}
```

**Chunk `Size` рекомендации:**
- **Small chunks (10-50)** — для **error-prone processing**, **low memory**
- **Medium chunks (50-200)** — **balanced approach**
- **Large chunks (200+)** — для **reliable processing**, **high throughput**

### Database Optimization

#### Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # Увеличенный pool для batch processing
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

#### Batch Insert Optimization
```sql
-- Создание индексов для batch processing
CREATE INDEX idx_person_status ON person(status);
CREATE INDEX idx_person_created_at ON person(created_at);

-- Отключение constraints для загрузки (с осторожностью!)
ALTER TABLE person DISABLE TRIGGER ALL;
-- LOAD DATA
ALTER TABLE person ENABLE TRIGGER ALL;

-- Partitioning для больших таблиц
CREATE TABLE person_y2023 PARTITION OF person
    FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');
```

## Spring Boot интеграция

### Auto-configuration

#### Spring Boot Batch Starter
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Что дает starter:**
- Автоматическая настройка **JobRepository**
- **H2 database** для **development**
- **JobLauncher bean**
- @**EnableBatchProcessing** не требуется
- **Metrics integration**

#### Application Properties
```yaml
spring:
  batch:
    job:
      enabled: false  # Отключаем автоматический запуск jobs
    jdbc:
      initialize-schema: always  # Создание таблиц в dev
    table-prefix: BATCH_  # Префикс для таблиц

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

### Job Scheduling

#### @Scheduled Integration
```java
@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class BatchApplication {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importUserJob;

    @Scheduled(cron = "0 0 2 * * ?")  // Каждый день в 2:00
    public void runImportUserJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", "/data/users.csv")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(importUserJob, params);

        if (execution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("Job completed successfully");
        } else {
            System.err.println("Job failed: " + execution.getExitStatus());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}
```

#### Command Line Runner
```java
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importUserJob;

    @Override
    public void run(String... args) throws Exception {
        // Parse command line arguments
        String inputFile = args.length > 0 ? args[0] : "/data/users.csv";
        String outputFile = args.length > 1 ? args[1] : "/data/output.csv";

        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", inputFile)
                .addString("outputFile", outputFile)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(importUserJob, params);
        System.exit(execution.getStatus() == BatchStatus.COMPLETED ? 0 : 1);
    }
}
```

### REST API для управления

#### Job Controller
```java
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @PostMapping("/{jobName}/run")
    public ResponseEntity<JobExecutionResponse> runJob(
            @PathVariable String jobName,
            @RequestBody Map<String, String> jobParameters) {

        try {
            Job job = jobRegistry.getJob(jobName);

            JobParameters params = new JobParametersBuilder()
                    .addString("user", jobParameters.getOrDefault("user", "system"))
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Add custom parameters
            JobParametersBuilder builder = new JobParametersBuilder(params);
            jobParameters.forEach((key, value) -> {
                if (!key.equals("user")) {
                    builder.addString(key, value);
                }
            });

            JobExecution execution = jobLauncher.run(job, builder.toJobParameters());

            return ResponseEntity.ok(new JobExecutionResponse(execution.getId(), execution.getStatus()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JobExecutionResponse(null, BatchStatus.FAILED));
        }
    }

    @GetMapping("/{jobName}/executions")
    public ResponseEntity<List<JobExecution>> getJobExecutions(@PathVariable String jobName) {
        List<JobExecution> executions = jobExplorer.getJobExecutions(jobRegistry.getJob(jobName).getName());
        return ResponseEntity.ok(executions);
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<JobExecution> getJobExecution(@PathVariable Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        return execution != null ? ResponseEntity.ok(execution) : ResponseEntity.notFound().build();
    }

    @PostMapping("/executions/{executionId}/stop")
    public ResponseEntity<Void> stopJobExecution(@PathVariable Long executionId) {
        try {
            jobOperator.stop(executionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

## Testing

### Unit Testing

#### Testing Components
```java
@SpringBootTest
public class PersonProcessorTest {

    @Autowired
    private PersonProcessor processor;

    @Test
    void testValidPersonProcessing() {
        Person input = new Person("John", "Doe", "john@example.com", 30);

        Person result = processor.process(input);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("john@example.com", result.getEmail().toLowerCase());
        assertTrue(result.getAge() >= 18);
    }

    @Test
    void testInvalidPersonFiltering() {
        Person input = new Person("John", "Doe", "john@example.com", 15);

        Person result = processor.process(input);

        assertNull(result); // Should be filtered out
    }

    @Test
    void testValidationException() {
        Person input = new Person("John", "Doe", null, 30);

        assertThrows(ValidationException.class, () -> processor.process(input));
    }
}
```

#### Testing Readers/Writers
```java
@SpringBootTest
public class ItemReaderWriterTest {

    @Autowired
    private PersonCsvReader reader;

    @Autowired
    private PersonDatabaseWriter writer;

    @Test
    void testReaderReadsExpectedData() throws Exception {
        Person person = reader.read();

        assertNotNull(person);
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getEmail());
    }

    @Test
    void testWriterPersistsData() {
        Person person = new Person("Test", "User", "test@example.com", 25);

        writer.write(List.of(person));

        // Verify data was written
        Person saved = personRepository.findByEmail("test@example.com");
        assertNotNull(saved);
        assertEquals("Test", saved.getFirstName());
    }
}
```

### Integration Testing

#### Testing Complete Job
```java
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ImportUserJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJobExecution() throws Exception {
        // Given
        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", "classpath:test-data.csv")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        // Then
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());

        // Verify step executions
        List<StepExecution> stepExecutions = execution.getStepExecutions();
        assertEquals(1, stepExecutions.size());

        StepExecution stepExecution = stepExecutions.get(0);
        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        assertTrue(stepExecution.getReadCount() > 0);
        assertTrue(stepExecution.getWriteCount() > 0);
        assertEquals(stepExecution.getReadCount(), stepExecution.getWriteCount());
    }

    @Test
    void testJobWithInvalidData() throws Exception {
        // Given - файл с invalid data
        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", "classpath:invalid-data.csv")
                .toJobParameters();

        // When & Then
        assertThrows(JobExecutionException.class, () ->
            jobLauncherTestUtils.launchJob(params));
    }
}
```

#### Testing with Testcontainers
```java
@SpringBootTest
@Testcontainers
public class DatabaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("batch_test")
            .withUsername("batch")
            .withPassword("batch");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testDatabaseOperations() {
        // Test with real PostgreSQL database
        Person person = new Person("Container", "Test", "container@example.com", 30);
        personRepository.save(person);

        Person found = personRepository.findByEmail("container@example.com");
        assertNotNull(found);
        assertEquals("Container", found.getFirstName());
    }
}
```

## Production Deployment

### Configuration Management

#### Environment-specific Properties
```yaml
# application-prod.yaml
spring:
  batch:
    job:
      enabled: false
  datasource:
    url: jdbc:postgresql://prod-db:5432/batchdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  rabbitmq:
    host: prod-rabbitmq
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}

batch:
  chunk-size: 500
  thread-pool-size: 8
  input-directory: /app/input
  output-directory: /app/output
```

#### Docker Deployment
```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app

COPY target/batch-application.jar app.jar
COPY config/application-prod.yaml config/

# Create directories
RUN mkdir -p /app/input /app/output /app/logs

# Create non-root user
RUN useradd -r -s /bin/false batchuser
RUN chown -R batchuser:batchuser /app
USER batchuser

ENTRYPOINT ["java", "-jar", "app.jar", \
    "--spring.config.location=config/application-prod.yaml", \
    "--batch.input-directory=/app/input", \
    "--batch.output-directory=/app/output"]
```

#### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: batch-processor
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: batch-app
        image: my-registry/batch-app:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: batch-secrets
              key: db-username
        volumeMounts:
        - name: input-volume
          mountPath: /app/input
        - name: output-volume
          mountPath: /app/output
      volumes:
      - name: input-volume
        persistentVolumeClaim:
          claimName: batch-input-pvc
      - name: output-volume
        persistentVolumeClaim:
          claimName: batch-output-pvc
```

### Monitoring и Alerting

#### Spring Boot Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,batch
  endpoint:
    batch:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

#### Health Indicators
```java
@Component
public class BatchHealthIndicator implements HealthIndicator {

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRegistry jobRegistry;

    @Override
    public Health health() {
        try {
            // Check if jobs are registered
            Collection<String> jobNames = jobRegistry.getJobNames();
            if (jobNames.isEmpty()) {
                return Health.down().withDetail("jobs", "No jobs registered").build();
            }

            // Check recent job executions
            int failedJobs = 0;
            for (String jobName : jobNames) {
                List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 5);
                for (JobInstance instance : instances) {
                    List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
                    for (JobExecution execution : executions) {
                        if (execution.getStatus() == BatchStatus.FAILED) {
                            failedJobs++;
                        }
                    }
                }
            }

            if (failedJobs > 0) {
                return Health.down()
                    .withDetail("failedJobs", failedJobs)
                    .withDetail("message", "Recent job failures detected")
                    .build();
            }

            return Health.up()
                .withDetail("jobs", jobNames.size())
                .withDetail("status", "All systems operational")
                .build();

        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

## Лучшие практики

### Job Design

#### 1. Job Naming Convention
```java
@Configuration
public class JobNamingConfig {

    @Bean
    public Job userImportJob() {
        // Good: descriptive name with context
        return jobBuilderFactory.get("userImportJob")
                // ...
                .build();
    }

    @Bean
    public Job dataMigrationJob() {
        // Good: specific purpose
        return jobBuilderFactory.get("dataMigrationJob")
                // ...
                .build();
    }

    @Bean
    public Job dailyReportJob() {
        // Good: frequency + purpose
        return jobBuilderFactory.get("dailyReportJob")
                // ...
                .build();
    }
}
```

#### 2. Parameter Management
```java
@Configuration
public class ParameterManagementConfig {

    @Bean
    public Job parameterizedJob() {
        return jobBuilderFactory.get("parameterizedJob")
                .incrementer(new RunIdIncrementer())  // Auto-increment run ID
                .start(step())
                .build();
    }

    @Bean
    @JobScope
    public Step step() {
        return stepBuilderFactory.get("step")
                .tasklet((contribution, context) -> {
                    // Access job parameters
                    String inputFile = context.getStepContext()
                        .getJobParameters().get("inputFile");

                    Long batchSize = context.getStepContext()
                        .getJobParameters().get("batchSize", 100L);

                    // Use parameters...
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
```

### Error Handling

#### 3. Comprehensive Error Handling
```java
@Configuration
public class ErrorHandlingConfig {

    @Bean
    public Step robustStep() {
        return stepBuilderFactory.get("robustStep")
                .<Person, Person>chunk(100)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipLimit(50)                              // Allow some failures
                .skip(ValidationException.class)            // Skip validation errors
                .skip(DataIntegrityViolationException.class)// Skip DB constraint errors
                .retryLimit(3)                              // Retry transient failures
                .retry(OptimisticLockingFailureException.class)
                .retry(TemporaryDataAccessException.class)
                .listener(new ErrorLoggingListener())       // Log all errors
                .build();
    }
}
```

### Performance

#### 4. Chunk Size Optimization
```java
@Configuration
public class PerformanceConfig {

    @Value("${batch.chunk.size:100}")
    private int chunkSize;

    @Value("${batch.concurrency:4}")
    private int concurrency;

    @Bean
    public Step optimizedStep() {
        return stepBuilderFactory.get("optimizedStep")
                .<Person, Person>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .throttleLimit(concurrency)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(concurrency);
        executor.setMaxPoolSize(concurrency * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-");
        return executor;
    }
}
```

### Testing

#### 5. Test Coverage
```java
@SpringBootTest
public class ComprehensiveJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncher;

    @Test
    void testSuccessfulJobExecution() {
        JobExecution execution = jobLauncher.launchJob();
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());
    }

    @Test
    void testJobWithInvalidData() {
        // Test error handling
        assertThrows(JobExecutionException.class, () ->
            jobLauncher.launchJob(getInvalidParameters()));
    }

    @Test
    void testJobRestartCapability() {
        // Test restart from failure point
        JobExecution failedExecution = jobLauncher.launchJob();
        assertEquals(BatchStatus.FAILED, failedExecution.getStatus());

        JobExecution restartedExecution = jobLauncher.launchJob();
        assertEquals(BatchStatus.COMPLETED, restartedExecution.getStatus());
    }

    @Test
    void testPerformanceRequirements() {
        long startTime = System.nanoTime();
        JobExecution execution = jobLauncher.launchJob();
        long duration = (System.nanoTime() - startTime) / 1_000_000; // ms

        assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        assertTrue(duration < 30000, "Job should complete within 30 seconds");
    }
}
```

### Operations

#### 6. Operational Readiness
```java
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar app.jar <jobName> [parameters]");
            System.out.println("Available jobs:");
            jobExplorer.getJobNames().forEach(name -> System.out.println("  - " + name));
            return;
        }

        String jobName = args[0];
        Job job = (Job) applicationContext.getBean(jobName + "Job");

        JobParametersBuilder paramBuilder = new JobParametersBuilder();
        // Parse additional args as parameters
        for (int i = 1; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                paramBuilder.addString(args[i], args[i + 1]);
            }
        }

        JobExecution execution = jobLauncher.run(job, paramBuilder.toJobParameters());

        // Exit with appropriate code
        System.exit(execution.getStatus() == BatchStatus.COMPLETED ? 0 : 1);
    }
}
```

## Решение проблем

### Распространенные проблемы

#### Job не запускается
```text
org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
```

**Решение:**
```java
// Используйте incrementer для создания новых job instances
@Bean
public Job job() {
    return jobBuilderFactory.get("jobName")
            .incrementer(new RunIdIncrementer())  // Добавьте incrementer
            .start(step())
            .build();
}

// Или используйте разные параметры
JobParameters params = new JobParametersBuilder()
        .addLong("timestamp", System.currentTimeMillis())  // Уникальный параметр
        .toJobParameters();
```

#### Chunk processing проблемы
```text
Memory usage keeps growing during processing
```

**Решение:**
```java
// Уменьшите chunk size
@Bean
public Step step() {
    return stepBuilderFactory.get("step")
            .<Person, Person>chunk(50)  // Меньше chunk size
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
}

// Или используйте paging для больших datasets
@Bean
public JdbcCursorItemReader<Person> reader() {
    return new JdbcCursorItemReaderBuilder<Person>()
            .dataSource(dataSource)
            .sql("SELECT * FROM person WHERE id > ? ORDER BY id")
            .parameters(new Object[]{lastProcessedId})
            .maxRows(1000)  // Limit rows per execution
            .build();
}
```

#### Database connection проблемы
```text
Timeout waiting for connection from pool
```

**Решение:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # Increase pool size
      connection-timeout: 30000  # Increase timeout
      idle-timeout: 600000       # Close idle connections
      max-lifetime: 1800000      # Recycle connections
```

#### Reader/Writer performance
```text
Processing 1000 records takes too long
```

**Решение:**
```java
// Оптимизируйте reader
@Bean
public JdbcCursorItemReader<Person> optimizedReader() {
    return new JdbcCursorItemReaderBuilder<Person>()
            .dataSource(dataSource)
            .sql("""
                SELECT p.id, p.first_name, p.last_name, p.email
                FROM person p
                INNER JOIN active_users au ON p.id = au.person_id  -- Join для фильтрации
                ORDER BY p.id
                """)
            .rowMapper(new ColumnMapRowMapper())  // Более быстрый mapper
            .fetchSize(100)                        // JDBC fetch size
            .build();
}

// Оптимизируйте writer
@Bean
public JdbcBatchItemWriter<Person> optimizedWriter() {
    return new JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql("""
                INSERT INTO processed_person (first_name, last_name, email, processed_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                """)
            .itemPreparedStatementSetter((person, ps) -> {
                ps.setString(1, person.getFirstName());
                ps.setString(2, person.getLastName());
                ps.setString(3, person.getEmail());
                // No need to set timestamp - CURRENT_TIMESTAMP
            })
            .build();
}
```

### Debug techniques

#### Job execution monitoring
```java
@Component
public class JobMonitoringAspect {

    @Autowired
    private JobExplorer jobExplorer;

    @Around("@annotation(org.springframework.batch.core.configuration.annotation.Job)")
    public Object monitorJob(ProceedingJoinPoint joinPoint) throws Throwable {
        String jobName = getJobName(joinPoint);

        System.out.println("Starting job: " + jobName);
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long duration = (System.nanoTime() - startTime) / 1_000_000;
            System.out.println("Job " + jobName + " completed in " + duration + "ms");

            return result;

        } catch (Exception e) {
            System.err.println("Job " + jobName + " failed: " + e.getMessage());
            throw e;
        }
    }

    @AfterReturning(pointcut = "@annotation(org.springframework.batch.core.configuration.annotation.Step)",
                   returning = "result")
    public void logStepCompletion(JoinPoint joinPoint, Object result) {
        String stepName = getStepName(joinPoint);
        System.out.println("Step " + stepName + " completed with result: " + result);
    }
}
```

#### Memory monitoring
```java
@Component
public class MemoryMonitor {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startMonitoring() {
        scheduler.scheduleAtFixedRate(this::logMemoryUsage, 0, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stopMonitoring() {
        scheduler.shutdown();
    }

    private void logMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        double usedPercentage = ((double) usedMemory / maxMemory) * 100;

        System.out.printf("Memory usage: %.2f%% (%dMB / %dMB)%n",
            usedPercentage, usedMemory / 1024 / 1024, maxMemory / 1024 / 1024);

        if (usedPercentage > 80) {
            System.err.println("WARNING: High memory usage detected!");
        }
    }
}
```


## Заключение

**Spring Batch** — это мощный и гибкий фреймворк для разработки надежных пакетных приложений. Он предоставляет все необходимые инструменты для обработки больших объемов данных с гарантией надежности и возможности масштабирования.

### Ключевые возможности:

1. **Job и `Step` управление** — декларативная конфигурация сложных **workflow**'ов
2. **Chunk-oriented processing** — эффективная обработка данных порциями
3. **Fault tolerance** — механизмы обработки ошибок и перезапуска
4. **Multiple readers/writers** — поддержка различных источников и приемников данных
5. **Parallel processing** — многопоточная и **partitioned** обработка
6. **Spring integration** — бесшовная интеграция с **Spring** экосистемой
7. **Monitoring** — подробная статистика и метрики выполнения
8. **Restart capability** — возможность перезапуска с места остановки

### Архитектурные преимущества:

#### Reliability:
- **Transaction boundaries** — контролируемые транзакции
- **Skip/retry logic** — гибкая обработка ошибок
- **Job repository** — **persistent** состояние выполнения
- **Execution context** — сохранение промежуточного состояния

#### Scalability:
- **Chunk processing** — ограниченное использование ресурсов
- **Parallel execution** — **multi-threaded** и **partitioned processing**
- **Resource management** — эффективное использование **CPU**/**memory**
- **Load balancing** — распределение нагрузки

### Когда использовать Spring Batch:

✅ **ETL operations** — извлечение, трансформация, загрузка данных
✅ **Data migration** — миграция между системами
✅ **Report generation** — генерация сложных отчетов
✅ **File processing** — обработка больших **CSV**/**XML**/**JSON** файлов
✅ **Database updates** — массовые обновления
✅ **Integration tasks** — интеграция с внешними системами
✅ **Scheduled processing** — регулярные **batch** операции
✅ **Guaranteed delivery** — надежная обработка каждого элемента

### Когда НЕ использовать:

❌ **Real-time processing** — используйте **Kafka Streams** или **WebFlux**
❌ **Simple CRUD** — используйте обычные репозитории
❌ **User interactions** — используйте **Spring MVC**
❌ **Small datasets** — **overhead** не оправдан
❌ **Streaming data** — используйте **Kafka** или **WebSocket**
❌ **Simple workflows** — используйте @**Scheduled** методы

### Production considerations:

1. **Configuration management** — **environment-specific** настройки
2. **Monitoring setup** — **metrics**, **alerts**, **dashboards**
3. **Resource optimization** — **memory**, **CPU**, **database connections**
4. **Error handling** — **comprehensive error recovery**
5. **Performance tuning** — **chunk sizes**, **parallel processing**
6. **Operational procedures** — **deployment**, **maintenance**, **troubleshooting**

### Best practices summary:

1. **Job design** — **logical step separation**, **clear naming**
2. **Error handling** — **skip**/**retry policies**, **dead letter queues**
3. **Performance** — **optimal chunk sizes**, **parallel processing**
4. **Testing** — **comprehensive unit and integration tests**
5. **Monitoring** — **execution metrics**, **health checks**
6. **Operations** — **restart procedures**, **maintenance tasks**
7. **Documentation** — **job descriptions**, **parameter documentation**

**Spring Batch** является **industrial standard** для **enterprise batch processing**. Его зрелость, надежность и интеграция с **Spring** делают его идеальным выбором для критически важных пакетных операций в **enterprise** системах. 🚀

**Далее: `Spring Integration` (enterprise integration patterns)**
