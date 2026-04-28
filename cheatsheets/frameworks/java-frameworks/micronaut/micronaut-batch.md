---
title: "Micronaut: Batch Processing — Job Processing и Scheduling"
description: "Полное руководство по batch processing в Micronaut: job processing, scheduling, chunk processing и best practices"
tags:
  - micronaut
  - batch
  - job
  - scheduling
  - chunk
  - java
  - kotlin
type: "reference"
difficulty: "intermediate"
aliases:
  - "Micronaut"
  - "micronaut batch"
prerequisites:
  - "[[micronaut-basics]]"
  - "[[micronaut-scheduling]]"
related:
  - "[[micronaut-scheduling]]"
  - "[[micronaut-data]]"
next:
  - "[[micronaut-scheduling]]"
  - "[[micronaut-data]]"
updated: "2026-04-20"
---

# Micronaut: Batch Processing — Job Processing и Scheduling

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Batch Processing](#настройка-batch-processing)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Job Definition](#job-definition)
  - [Simple Job](#simple-job)
- [Chunk Processing](#chunk-processing)
  - [Chunk-based Job](#chunk-based-job)
- [Job Scheduling](#job-scheduling)
  - [Scheduled Job](#scheduled-job)
- [Error Handling](#error-handling)
  - [Job Error Handling](#job-error-handling)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте chunk processing для больших объемов](#1-используйте-chunk-processing-для-больших-объемов)
  - [2. Обрабатывайте ошибки правильно](#2-обрабатывайте-ошибки-правильно)
  - [3. Мониторьте выполнение jobs](#3-мониторьте-выполнение-jobs)
- [Job Parameters](#job-parameters)
  - [Parameterized Jobs](#parameterized-jobs)
- [Job Listeners](#job-listeners)
  - [Step Execution Listener](#step-execution-listener)
- [Job Execution Context](#job-execution-context)
  - [Execution Context](#execution-context)
- [Job Restart](#job-restart)
  - [Restart Configuration](#restart-configuration)
- [Job Skip Policy](#job-skip-policy)
  - [Skip Configuration](#skip-configuration)
- [Job Partitioning](#job-partitioning)
  - [Partitioned Job](#partitioned-job)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет поддержку **batch processing** для обработки больших объемов данных. Это позволяет создавать эффективные **batch jobs** с поддержкой **chunk processing** и **scheduling**.

### Основные возможности

- **Job Processing**: Обработка **batch jobs**
- **Chunk Processing**: Обработка данных порциями
- **Job Scheduling**: Планирование **batch jobs**
- **Error Handling**: Обработка ошибок в **batch jobs**
- **Job Monitoring**: Мониторинг выполнения **jobs**

## Настройка Batch Processing

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.batch:micronaut-batch-core")
}
```

### Конфигурация

**application.yml:**

```yaml
batch:
  job:
    enabled: true
    execution:
      isolation:
        level: ISOLATION_SERIALIZABLE
```

## Job Definition

### Simple Job

```java
import io.micronaut.batch.core.Job;
import io.micronaut.batch.core.Step;
import jakarta.inject.Singleton;

@Singleton
public class UserProcessingJob implements Job {

    @Override
    public String getName() {
        return "userProcessingJob";
    }

    @Override
    public List<Step> getSteps() {
        return List.of(
            new Step("readUsers", this::readUsers),
            new Step("processUsers", this::processUsers),
            new Step("writeUsers", this::writeUsers)
        );
    }

    private void readUsers() {
        // Чтение пользователей
    }

    private void processUsers() {
        // Обработка пользователей
    }

    private void writeUsers() {
        // Запись пользователей
    }
}
```

## Chunk Processing

### Chunk-based Job

```java
import io.micronaut.batch.core.chunk.ChunkProcessor;
import jakarta.inject.Singleton;

@Singleton
public class UserChunkProcessor implements ChunkProcessor<User, User> {

    @Override
    public List<User> process(List<User> items) {
        return items.stream()
            .map(this::processUser)
            .collect(Collectors.toList());
    }

    private User processUser(User user) {
        // Обработка одного пользователя
        user.setProcessed(true);
        return user;
    }
}
```

## Job Scheduling

### Scheduled Job

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ScheduledBatchJob {
    private final JobLauncher jobLauncher;

    public ScheduledBatchJob(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(fixedRate = "1h")
    public void runBatchJob() {
        JobParameters parameters = new JobParameters();
        jobLauncher.run("userProcessingJob", parameters);
    }
}
```

## Error Handling

### Job Error Handling

```java
import io.micronaut.batch.core.JobExecutionListener;
import jakarta.inject.Singleton;

@Singleton
public class JobErrorHandler implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job failed: {}", jobExecution.getJobInstance().getJobName());
            // Обработка ошибки
        }
    }
}
```

## Лучшие практики

### 1. Используйте chunk processing для больших объемов

```java
// ✅ Хорошо
@Singleton
public class ChunkProcessor implements ChunkProcessor<User, User> {
    // Обработка порциями
}
```

### 2. Обрабатывайте ошибки правильно

```java
// ✅ Хорошо
try {
    jobLauncher.run("job", parameters);
} catch (JobExecutionException e) {
    log.error("Job execution failed", e);
}
```

### 3. Мониторьте выполнение jobs

```java
// ✅ Хорошо
JobExecution execution = jobLauncher.run("job", parameters);
log.info("Job status: {}", execution.getStatus());
```

## Job Parameters

### Parameterized Jobs

```java
import io.micronaut.batch.core.JobParameters;
import jakarta.inject.Singleton;

@Singleton
public class ParameterizedJobService {
    private final JobLauncher jobLauncher;

    public void runJobWithParameters() {
        JobParameters parameters = new JobParameters();
        parameters.put("inputFile", "users.csv");
        parameters.put("outputFile", "processed-users.csv");
        parameters.put("chunkSize", "100");

        jobLauncher.run("userProcessingJob", parameters);
    }
}
```

## Job Listeners

### Step Execution Listener

```java
import io.micronaut.batch.core.StepExecutionListener;
import jakarta.inject.Singleton;

@Singleton
public class CustomStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Starting step: {}", stepExecution.getStepName());
    }

    @Override
    public void afterStep(StepExecution stepExecution) {
        log.info("Completed step: {}", stepExecution.getStepName());
    }
}
```

## Job Execution Context

### Execution Context

```java
import io.micronaut.batch.core.JobExecution;
import jakarta.inject.Singleton;

@Singleton
public class ExecutionContextService {

    public void saveExecutionContext(JobExecution jobExecution) {
        Map<String, Object> context = jobExecution.getExecutionContext();
        context.put("startTime", LocalDateTime.now());
        context.put("processedCount", 0);
    }

    public void updateExecutionContext(JobExecution jobExecution, int count) {
        Map<String, Object> context = jobExecution.getExecutionContext();
        context.put("processedCount", count);
    }
}
```

## Job Restart

### Restart Configuration

```java
import io.micronaut.batch.core.JobLauncher;
import jakarta.inject.Singleton;

@Singleton
public class JobRestartService {
    private final JobLauncher jobLauncher;

    public void restartJob(Long jobExecutionId) {
        JobParameters parameters = new JobParameters();
        // Восстановление параметров из предыдущего выполнения
        jobLauncher.run("userProcessingJob", parameters);
    }
}
```

## Job Skip Policy

### Skip Configuration

```java
import io.micronaut.batch.core.SkipPolicy;
import jakarta.inject.Singleton;

@Singleton
public class CustomSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) {
        // Пропуск определенных ошибок
        return t instanceof DataValidationException && skipCount < 10;
    }
}
```

## Job Partitioning

### Partitioned Job

```java
import io.micronaut.batch.core.Partitioner;
import jakarta.inject.Singleton;

@Singleton
public class UserPartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        List<User> users = userRepository.findAll();
        int partitionSize = users.size() / gridSize;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.put("startIndex", i * partitionSize);
            context.put("endIndex", (i + 1) * partitionSize);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}
```

## Заключение

**Micronaut Batch** предоставляет мощные инструменты для **batch processing**. Поддержка **job processing**, **chunk processing**, **scheduling**, **error handling**, **monitoring**, **job parameters**, **listeners**, **execution context**, **job restart**, **skip policies**, **partitioning** и других продвинутых возможностей позволяет создавать эффективные **batch jobs** для обработки больших объемов данных.

## Дополнительные ресурсы

- [**Micronaut Batch** Documentation](https://micronaut-projects.github.io/micronaut-batch/latest/guide/)
- [**Spring Batch** Documentation](https://docs.spring.io/spring-batch/reference/)
- [**Batch Processing Patterns**](https://www.enterpriseintegrationpatterns.com/patterns/messaging/BatchProcessing.html)

## См. также

- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](micronaut-actuator.md)
- [Micronaut: Основы](micronaut-basics.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](micronaut-cache.md)
- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](micronaut-cloud.md)
- [Micronaut: Core — Dependency Injection и Bean Management](micronaut-core.md)
