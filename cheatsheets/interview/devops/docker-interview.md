---
title: "Вопросы на собеседовании: Docker"
description: "Вопросы и ответы по Docker: контейнеры, образы, Dockerfile, multi-stage сборка, Compose, тома, сети, безопасность, оптимизация для Java/Spring Boot."
tags:
  - interview
  - devops
  - docker-interview
aliases:
  - "Docker interview"
  - "Docker собеседование"
  - "Вопросы по Docker"
  - "Контейнеризация interview"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Docker`

Вопросы и ответы по `Docker`: контейнеры, образы, `Dockerfile`, `multi-stage` сборка, `Compose`, тома, сети, безопасность, оптимизация для `Java` / `Spring Boot`.

**`Docker`** — платформа для разработки, доставки и запуска приложений в контейнерах. Контейнеры изолируют приложение и его зависимости от хоста и друг от друга, используют ядро ОС хоста и не требуют полноценной виртуальной машины. `Docker` стал стандартом де-факто для упаковки и доставки приложений в [микросервисной архитектуре](../architecture/microservices-interview.md) и является основой для оркестраторов вроде [Kubernetes](kubernetes-interview.md).

## Полезные ссылки

### Официальная документация

- [Docker Documentation](https://docs.docker.com/) — официальная документация
- [Dockerfile reference](https://docs.docker.com/engine/reference/builder/) — спецификация инструкций `Dockerfile`
- [Docker Compose reference](https://docs.docker.com/compose/compose-file/) — спецификация `docker-compose.yml`
- [Dockerizing a Spring Boot Application](https://www.baeldung.com/dockerizing-spring-boot-application) — контейнеризация `Spring Boot`
- [Creating Docker Images with Spring Boot](https://www.baeldung.com/spring-boot-docker-images) — `Buildpacks` и layered jars
- [Reusing Docker Layers with Spring Boot](https://www.baeldung.com/docker-layers-spring-boot) — оптимизация слоёв

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Docker**
- [Q1. (!) Что такое контейнер Docker и чем он отличается от виртуальной машины?](#q1--что-такое-контейнер-docker-и-чем-он-отличается-от-виртуальной-машины)
- [Q2. (!) Из каких компонентов состоит архитектура Docker?](#q2--из-каких-компонентов-состоит-архитектура-docker)
- [Q3. (!) Что такое Docker Image и из чего он состоит?](#q3--что-такое-docker-image-и-из-чего-он-состоит)
- [Q4. В чём разница между Docker Image и Docker Layer?](#q4-в-чём-разница-между-docker-image-и-docker-layer)
- [Q5. (!) Что такое Docker Namespace и Cgroups?](#q5--что-такое-docker-namespace-и-cgroups)
- [Q6. (!) Опишите жизненный цикл контейнера Docker](#q6--опишите-жизненный-цикл-контейнера-docker)

**Dockerfile**
- [Q7. (!) Какие основные инструкции Dockerfile вы знаете?](#q7--какие-основные-инструкции-dockerfile-вы-знаете)
- [Q8. (!) В чём разница между CMD и ENTRYPOINT?](#q8--в-чём-разница-между-cmd-и-entrypoint)
- [Q9. В чём разница между COPY и ADD?](#q9-в-чём-разница-между-copy-и-add)
- [Q10. (!) Что такое multi-stage сборка и зачем она нужна?](#q10--что-такое-multi-stage-сборка-и-зачем-она-нужна)
- [Q11. Как оптимизировать порядок слоёв в Dockerfile для кэширования?](#q11-как-оптимизировать-порядок-слоёв-в-dockerfile-для-кэширования)
- [Q12. Что такое .dockerignore и зачем он нужен?](#q12-что-такое-dockerignore-и-зачем-он-нужен)

**Docker и Java / Spring Boot**
- [Q13. (!) Как контейнеризировать Spring Boot приложение?](#q13--как-контейнеризировать-spring-boot-приложение)
- [Q14. Что такое Spring Boot Layered Jars и как они оптимизируют Docker-образы?](#q14-что-такое-spring-boot-layered-jars-и-как-они-оптимизируют-docker-образы)
- [Q15. Что такое Cloud Native Buildpacks и Jib?](#q15-что-такое-cloud-native-buildpacks-и-jib)
- [Q16. Как передать Spring Profile при запуске контейнера?](#q16-как-передать-spring-profile-при-запуске-контейнера)

**Команды и Registry**
- [Q17. Какие основные команды Docker CLI вы используете?](#q17-какие-основные-команды-docker-cli-вы-используете)
- [Q18. Что такое Docker Registry и Docker Hub?](#q18-что-такое-docker-registry-и-docker-hub)
- [Q19. Как экспортировать и импортировать Docker-образы?](#q19-как-экспортировать-и-импортировать-docker-образы)
- [Q20. Можно ли удалить контейнер в состоянии PAUSED?](#q20-можно-ли-удалить-контейнер-в-состоянии-paused)

**Volumes и хранение данных**
- [Q21. (!) Какие типы хранения данных поддерживает Docker?](#q21--какие-типы-хранения-данных-поддерживает-docker)
- [Q22. Где физически хранятся Docker Volumes?](#q22-где-физически-хранятся-docker-volumes)
- [Q23. При каких обстоятельствах теряются данные контейнера?](#q23-при-каких-обстоятельствах-теряются-данные-контейнера)

**Сети Docker**
- [Q24. (!) Какие сетевые драйверы поддерживает Docker?](#q24--какие-сетевые-драйверы-поддерживает-docker)
- [Q25. Как контейнеры общаются между собой и с хостом?](#q25-как-контейнеры-общаются-между-собой-и-с-хостом)
- [Q26. В чём разница между expose и ports в Docker Compose?](#q26-в-чём-разница-между-expose-и-ports-в-docker-compose)

**Docker Compose**
- [Q27. (!) Что такое Docker Compose и как он работает?](#q27--что-такое-docker-compose-и-как-он-работает)
- [Q28. В чём разница между docker compose up, run и start?](#q28-в-чём-разница-между-docker-compose-up-run-и-start)
- [Q29. Как управлять порядком запуска сервисов в Compose?](#q29-как-управлять-порядком-запуска-сервисов-в-compose)
- [Q30. Что такое Docker Compose Support в Spring Boot 3?](#q30-что-такое-docker-compose-support-в-spring-boot-3)

**Безопасность**
- [Q31. (!) Какие best practices безопасности Docker вы знаете?](#q31--какие-best-practices-безопасности-docker-вы-знаете)
- [Q32. Как запускать контейнеры от непривилегированного пользователя?](#q32-как-запускать-контейнеры-от-непривилегированного-пользователя)
- [Q33. Как управлять секретами в Docker?](#q33-как-управлять-секретами-в-docker)

**Политики перезапуска и ресурсы**
- [Q34. (!) Какие политики перезапуска контейнеров существуют?](#q34--какие-политики-перезапуска-контейнеров-существуют)
- [Q35. Как ограничить ресурсы контейнера (CPU, память)?](#q35-как-ограничить-ресурсы-контейнера-cpu-память)
- [Q36. Сколько контейнеров можно запустить на одном хосте?](#q36-сколько-контейнеров-можно-запустить-на-одном-хосте)

**Логирование и отладка**
- [Q37. Как работает логирование в Docker?](#q37-как-работает-логирование-в-docker)
- [Q38. (!) Как отлаживать проблемы с контейнером?](#q38--как-отлаживать-проблемы-с-контейнером)

**BuildKit и продвинутая сборка**
- [Q39. (!) Что такое Docker BuildKit и чем он лучше классического builder?](#q39--что-такое-docker-buildkit-и-чем-он-лучше-классического-builder)
- [Q40. Как использовать кэш сборки уровня registry (cache mounts)?](#q40-как-использовать-кэш-сборки-уровня-registry-cache-mounts)
- [Q41. Как сканировать Docker-образ на уязвимости?](#q41-как-сканировать-docker-образ-на-уязвимости)

---

## Q1. (!) Что такое контейнер `Docker` и чем он отличается от виртуальной машины?

**Контейнер** — изолированный процесс, работающий на ядре хост-ОС. Он содержит приложение и все его зависимости, но не включает собственное ядро операционной системы — в отличие от виртуальной машины (`VM`).

| Критерий | Контейнер | Виртуальная машина |
|---|---|---|
| Изоляция | На уровне процесса (`namespace`, `cgroups`) | На уровне оборудования (гипервизор) |
| Ядро ОС | Разделяет ядро хоста | Собственное ядро |
| Размер образа | Мегабайты (10–500 MB) | Гигабайты (1–20 GB) |
| Время запуска | Секунды | Минуты |
| Потребление ресурсов | Минимальный overhead | Значительный overhead |
| Плотность | Сотни на хосте | Десятки на хосте |

```mermaid
graph TB
    subgraph "Виртуальная машина"
        HW1[Hardware] --> HV[Hypervisor]
        HV --> VM1[Guest OS + App 1]
        HV --> VM2[Guest OS + App 2]
    end
    subgraph "Контейнеры"
        HW2[Hardware] --> OS[Host OS + Docker Engine]
        OS --> C1[App 1 + Libs]
        OS --> C2[App 2 + Libs]
        OS --> C3[App 3 + Libs]
    end
```

На собеседовании важно подчеркнуть: контейнеры легче и быстрее, но изоляция слабее (общее ядро = общая поверхность атаки). Для полной изоляции (мультитенантность, разные ОС) нужны `VM`.

## Q2. (!) Из каких компонентов состоит архитектура `Docker`?

Архитектура `Docker` построена по клиент-серверной модели и включает три основных компонента:

```mermaid
graph LR
    CLI[Docker CLI<br/>клиент] -->|REST API| D[Docker Daemon<br/>dockerd]
    D --> IMG[Images]
    D --> CONT[Containers]
    D --> NET[Networks]
    D --> VOL[Volumes]
    D -->|pull/push| REG[Registry<br/>Docker Hub / Harbor]
```

1. **`Docker Client`** (`docker` CLI) — командная строка, отправляет запросы к демону через `REST API` (unix-сокет `/var/run/docker.sock` или TCP).
2. **`Docker Daemon`** (`dockerd`) — серверный процесс, управляет образами, контейнерами, сетями, томами. Использует `containerd` для управления жизненным циклом контейнеров и `runc` для их запуска.
3. **`Docker Registry`** — хранилище образов. Публичный: `Docker Hub`. Приватные: `Harbor`, `ECR`, `GCR`, `Nexus`, `GitLab Registry`.

Дополнительно: `containerd` — высокоуровневый runtime, управляет pull-ом образов, хранением, сетью. `runc` — низкоуровневый runtime, создаёт контейнер через системные вызовы `Linux` (`clone`, `namespace`, `cgroups`).

## Q3. (!) Что такое `Docker Image` и из чего он состоит?

**Docker Image** — неизменяемый шаблон для создания контейнеров: код приложения, зависимости, runtime, метаданные. Образ идентифицируется репозиторием и тегом (например, `nginx:1.24`).

Образ строится из **слоёв (layers)**: каждая инструкция в `Dockerfile` (`FROM`, `RUN`, `COPY`, `ADD`) создаёт новый слой. Слои read-only, кэшируются и переиспользуются между образами — это экономит место и ускоряет сборку.

```mermaid
graph TB
    L1["Layer 1: FROM eclipse-temurin:17-jre-alpine"] --> L2["Layer 2: COPY dependencies"]
    L2 --> L3["Layer 3: COPY application code"]
    L3 --> L4["Layer 4: CMD / ENTRYPOINT"]
    L4 --> RW["Writable Container Layer<br/>(создаётся при docker run)"]
    style RW fill:#f9f,stroke:#333
```

**Практика для `Java`**: использовать конкретный тег базового образа (`eclipse-temurin:17-jre-alpine`), не `latest`. `Multi-stage` сборка уменьшает размер: в первой стадии — `Gradle` / `Maven`, во второй — только `JAR` и `JRE`. Типичный размер: 200-400 MB вместо 600+ MB с полным `JDK`.

## Q4. В чём разница между `Docker Image` и `Docker Layer`?

**Docker Image** состоит из серии **Docker Layer**. Каждый слой — результат одной инструкции `Dockerfile`:

```dockerfile
FROM ubuntu:22.04          # Layer 1: базовый образ
COPY . /myapp              # Layer 2: копирование файлов
RUN make /myapp            # Layer 3: сборка
CMD ["python", "/myapp/app.py"]  # Layer 4: команда запуска
```

Каждый слой — разница (diff) файловой системы по сравнению с предыдущим слоем. Слои readonly и разделяются между образами: если два образа используют одинаковый `FROM`, этот слой хранится один раз. При запуске контейнера поверх readonly-слоёв создаётся writable-слой (`Copy-on-Write`).

Команда `docker history <image>` показывает все слои образа с их размерами.

## Q5. (!) Что такое `Docker Namespace` и `Cgroups`?

Это два механизма ядра `Linux`, обеспечивающие изоляцию и ограничение ресурсов контейнеров.

**Namespaces** — изоляция видимости ресурсов:

| Namespace | Что изолирует |
|---|---|
| `PID` | Процессы (контейнер видит только свои) |
| `Network` | Сетевой стек (свои интерфейсы, IP, порты) |
| `Mount` | Файловая система |
| `UTS` | Hostname |
| `IPC` | Очереди сообщений, семафоры |
| `User` | UID/GID (маппинг пользователей) |

**Cgroups** (Control Groups) — ограничение потребления ресурсов:
- `CPU` — лимит процессорного времени (`--cpus=2`)
- Память — лимит RAM (`--memory=512m`)
- Disk I/O — ограничение скорости чтения/записи
- Сеть — приоритизация трафика

Без `namespace` контейнер видел бы все процессы и сеть хоста. Без `cgroups` — мог бы потребить все ресурсы. В [Kubernetes](kubernetes-interview.md) эти механизмы используются для `resource limits` и `requests` подов.

## Q6. (!) Опишите жизненный цикл контейнера `Docker`

```mermaid
stateDiagram-v2
    [*] --> Created: docker create
    Created --> Running: docker start
    Running --> Paused: docker pause
    Paused --> Running: docker unpause
    Running --> Stopped: docker stop
    Stopped --> Running: docker start
    Stopped --> Deleted: docker rm
    Running --> Deleted: docker rm -f
    Created --> Deleted: docker rm
```

| Состояние | Описание |
|---|---|
| `Created` | Контейнер создан, но не запущен |
| `Running` | Работает со всеми процессами |
| `Paused` | Процессы заморожены (`SIGSTOP` через `cgroups` freezer) |
| `Stopped` / `Exited` | Главный процесс завершился (с кодом выхода) |
| `Deleted` | Контейнер удалён, ресурсы освобождены |

Команда `docker run` = `docker create` + `docker start`. При `docker stop` отправляется `SIGTERM`, через `--stop-timeout` (по умолчанию 10 секунд) — `SIGKILL`. Для корректного завершения `Java`-приложение должно обрабатывать `SIGTERM` — `Spring Boot` делает это из коробки с `server.shutdown=graceful`.

## Q7. (!) Какие основные инструкции `Dockerfile` вы знаете?

| Инструкция | Назначение | Пример |
|---|---|---|
| `FROM` | Базовый образ | `FROM eclipse-temurin:17-jre-alpine` |
| `WORKDIR` | Рабочий каталог | `WORKDIR /app` |
| `COPY` | Копирование файлов | `COPY target/*.jar app.jar` |
| `ADD` | Копирование + распаковка tar / загрузка URL | `ADD archive.tar.gz /app/` |
| `RUN` | Выполнение команды при сборке | `RUN apt-get update && apt-get install -y curl` |
| `ENV` | Переменная окружения | `ENV JAVA_OPTS="-Xmx512m"` |
| `ARG` | Аргумент сборки (только build-time) | `ARG JAR_FILE=app.jar` |
| `EXPOSE` | Декларация порта (документация) | `EXPOSE 8080` |
| `CMD` | Команда по умолчанию | `CMD ["java", "-jar", "app.jar"]` |
| `ENTRYPOINT` | Фиксированная команда запуска | `ENTRYPOINT ["java", "-jar"]` |
| `USER` | Пользователь для запуска | `USER 1001` |
| `HEALTHCHECK` | Проверка здоровья | `HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health` |
| `LABEL` | Метаданные образа | `LABEL maintainer="team@company.com"` |
| `VOLUME` | Декларация точки монтирования | `VOLUME /data` |

Каждая инструкция `FROM`, `RUN`, `COPY`, `ADD` создаёт новый слой. `CMD`, `ENV`, `EXPOSE`, `LABEL` — только метаданные, не увеличивают размер.

## Q8. (!) В чём разница между `CMD` и `ENTRYPOINT`?

| Аспект | `CMD` | `ENTRYPOINT` |
|---|---|---|
| Назначение | Команда по умолчанию | Фиксированная точка входа |
| Переопределение | Заменяется аргументами `docker run` | Не заменяется (только `--entrypoint`) |
| Типичное использование | Аргументы по умолчанию | Основной исполняемый файл |

Три формы записи:
```dockerfile
# Exec form (рекомендуется — PID 1 получает сигналы корректно)
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]

# Shell form (оборачивается в /bin/sh -c — PID 1 = shell, не приложение)
CMD java -jar app.jar
```

При комбинации `ENTRYPOINT` + `CMD`: `ENTRYPOINT` задаёт исполняемый файл, `CMD` — аргументы по умолчанию. Команда `docker run myapp --server.port=9090` заменит `CMD`, но не `ENTRYPOINT`.

**Важно для Java**: всегда использовать exec-форму, чтобы `JVM` была PID 1 и получала `SIGTERM` для graceful shutdown.

## Q9. В чём разница между `COPY` и `ADD`?

| Аспект | `COPY` | `ADD` |
|---|---|---|
| Копирование локальных файлов | Да | Да |
| Автоматическая распаковка tar | Нет | Да |
| Загрузка по URL | Нет | Да (не рекомендуется) |
| Прозрачность | Высокая | Низкая (неявное поведение) |

**Рекомендация**: всегда использовать `COPY`, кроме случаев, когда нужна распаковка tar. Загрузку по URL лучше делать через `RUN curl` или `RUN wget` — так можно в одном слое скачать, распаковать и удалить архив.

```dockerfile
# Плохо — ADD с URL
ADD https://example.com/file.tar.gz /app/

# Хорошо — RUN с curl (один слой, архив удалён)
RUN curl -fsSL https://example.com/file.tar.gz | tar xz -C /app/
```

## Q10. (!) Что такое `multi-stage` сборка и зачем она нужна?

`Multi-stage` сборка — использование нескольких `FROM` в одном `Dockerfile`. Позволяет собирать приложение в одной стадии (с `JDK`, `Maven` / `Gradle`, исходниками), а в финальный образ копировать только артефакт.

```dockerfile
# === Стадия 1: сборка ===
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon   # кэширование зависимостей
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# === Стадия 2: runtime ===
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Результат**: финальный образ содержит только `JRE` + `JAR` (~200-300 MB вместо ~700 MB с полным `JDK` и инструментами сборки). В стадии `builder` остаются `JDK`, `Gradle`, исходники — они не попадают в итоговый образ.

## Q11. Как оптимизировать порядок слоёв в `Dockerfile` для кэширования?

Правило: **редко меняющиеся инструкции — в начало, часто меняющиеся — в конец**. `Docker` кэширует слои: при изменении одного слоя все последующие пересобираются.

```dockerfile
# 1. Базовый образ (меняется редко)
FROM eclipse-temurin:17-jre-alpine

# 2. Системные зависимости (меняются редко)
RUN apk add --no-cache curl

# 3. Зависимости приложения (меняются при обновлении pom.xml/build.gradle)
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/
RUN ./gradlew dependencies

# 4. Исходный код (меняется часто) — только этот слой пересобирается
COPY src/ src/
RUN ./gradlew bootJar
```

При изменении только кода (`src/`) шаги 1-3 берутся из кэша. Без такого разделения любое изменение кода вызвало бы переустановку всех зависимостей.

## Q12. Что такое `.dockerignore` и зачем он нужен?

`.dockerignore` — файл, определяющий, какие файлы и каталоги исключить из контекста сборки (`build context`). Уменьшает объём данных, отправляемых демону `Docker`, и предотвращает попадание ненужных файлов в образ.

```text
# .dockerignore
.git
.gitignore
.idea
*.md
build/
.gradle/
node_modules/
*.log
.env
docker-compose*.yml
```

Без `.dockerignore` каталог `.git` (сотни MB) и `build/` попадут в контекст, замедлив сборку и раздув образ. Аналогично `.gitignore`, но для `Docker`.

## Q13. (!) Как контейнеризировать `Spring Boot` приложение?

Существует три основных подхода:

**1. Dockerfile с multi-stage сборкой** (полный контроль):
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**2. Spring Boot Buildpacks** (без `Dockerfile`):
```bash
./gradlew bootBuildImage --imageName=myapp:1.0
# или Maven:
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=myapp:1.0
```

**3. Google Jib** (без `Dockerfile` и без `Docker daemon`):
```groovy
// build.gradle
plugins {
    id 'com.google.cloud.tools.jib' version '3.4.0'
}
jib {
    from { image = 'eclipse-temurin:17-jre-alpine' }
    to { image = 'registry.example.com/myapp:1.0' }
}
```

Подробнее о сборке и деплое образов — в [вопросах по CI/CD пайплайнам](../cicd/pipeline-design-interview.md).

## Q14. Что такое `Spring Boot Layered Jars` и как они оптимизируют `Docker`-образы?

Начиная с `Spring Boot 2.3`, JAR-файл можно разбить на слои по частоте изменения:

| Слой | Содержимое | Частота изменения |
|---|---|---|
| `dependencies` | Внешние библиотеки | Редко |
| `spring-boot-loader` | Загрузчик Spring Boot | Очень редко |
| `snapshot-dependencies` | SNAPSHOT-зависимости | Иногда |
| `application` | Код приложения | Каждый билд |

```dockerfile
FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

При изменении только кода приложения пересобирается только слой `application` (~KBs), а зависимости (~100+ MB) берутся из кэша.

## Q15. Что такое `Cloud Native Buildpacks` и `Jib`?

**Cloud Native Buildpacks** (`CNB`) — стандарт `CNCF` для автоматической сборки `OCI`-образов без `Dockerfile`. `Spring Boot` интегрирует `Paketo Buildpacks`:
- Автоматически определяет `JDK`, настраивает `JVM` (`memory calculator`)
- Создаёт оптимизированный layered-образ
- Поддерживает `Gradle` и `Maven` из коробки

**Google Jib** — плагин для `Maven` / `Gradle`:
- Не требует ни `Dockerfile`, ни установленного `Docker`
- Собирает образ напрямую в registry
- Разделяет образ на слои (dependencies, resources, classes)
- Быстрее классической сборки, т.к. не создаёт промежуточный tar

Оба подхода решают проблему написания и поддержки `Dockerfile`, но дают меньше контроля над финальным образом.

## Q16. Как передать `Spring Profile` при запуске контейнера?

Три способа:

```bash
# 1. Через переменную окружения (рекомендуется)
docker run -e SPRING_PROFILES_ACTIVE=prod myapp:1.0

# 2. Через аргумент командной строки
docker run myapp:1.0 --spring.profiles.active=prod

# 3. В docker-compose.yml
```

```yaml
services:
  app:
    image: myapp:1.0
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS=-Xmx512m
```

В `Dockerfile` **не** хардкодить профиль — он должен быть параметром окружения. Переменная `SPRING_PROFILES_ACTIVE` автоматически маппится `Spring Boot` в `spring.profiles.active`.

## Q17. Какие основные команды `Docker CLI` вы используете?

```bash
# Управление образами
docker build -t myapp:1.0 .         # сборка образа
docker images                        # список образов
docker rmi myapp:1.0                 # удаление образа
docker pull nginx:1.24               # загрузка из registry
docker push registry.io/myapp:1.0    # публикация в registry

# Управление контейнерами
docker run -d -p 8080:8080 myapp:1.0 # запуск в фоне с пробросом порта
docker ps -a                         # все контейнеры
docker logs -f container_id          # логи в реальном времени
docker exec -it container_id sh      # вход в контейнер
docker stop container_id             # остановка (SIGTERM → SIGKILL)
docker rm container_id               # удаление

# Информация и очистка
docker inspect container_id          # JSON-метаданные
docker stats                         # использование ресурсов
docker system prune -a               # очистка неиспользуемых ресурсов
docker system df                     # использование диска
```

Флаги `docker ps`: `-a` — все (включая остановленные), `-q` — только ID, `--filter "status=exited"` — фильтр, `--format` — кастомный вывод.

## Q18. Что такое `Docker Registry` и `Docker Hub`?

**Docker Registry** — хранилище и система распространения `Docker`-образов. Образы организованы в репозитории с тегами.

| Тип | Примеры | Использование |
|---|---|---|
| Публичный | `Docker Hub`, `GHCR`, `Quay.io` | Open-source проекты |
| Приватный (облачный) | `ECR`, `GCR`, `ACR` | Облачные платформы |
| Приватный (self-hosted) | `Harbor`, `Nexus`, `GitLab Registry` | Корпоративная среда |

```bash
# Вход в registry
docker login registry.example.com

# Тегирование и публикация
docker tag myapp:1.0 registry.example.com/team/myapp:1.0
docker push registry.example.com/team/myapp:1.0

# Загрузка
docker pull registry.example.com/team/myapp:1.0
```

**Best practice**: не использовать тег `latest` в production — всегда конкретная версия. Образы сканировать на уязвимости (`Trivy`, `Snyk`) перед деплоем.

## Q19. Как экспортировать и импортировать `Docker`-образы?

```bash
# Экспорт образа в tar-архив (со всеми слоями и метаданными)
docker save -o myapp.tar myapp:1.0

# Импорт образа из tar-архива
docker load -i myapp.tar
```

| Команда | Что сохраняет | Использование |
|---|---|---|
| `docker save` / `docker load` | Образ со слоями и историей | Перенос между хостами без registry |
| `docker export` / `docker import` | Файловую систему контейнера (flat) | Создание нового образа из снимка контейнера |

В `CI/CD` обычно используют registry (`push` → `pull`), а не файловый перенос.

## Q20. Можно ли удалить контейнер в состоянии `PAUSED`?

Нет. Контейнер в состоянии `PAUSED` нельзя удалить напрямую. Порядок действий:

```bash
docker unpause container_id   # снять паузу
docker stop container_id      # остановить (EXITED)
docker rm container_id         # удалить
```

Альтернатива — принудительное удаление: `docker rm -f container_id` — контейнер будет убит (`SIGKILL`) и удалён. Для массовой очистки остановленных контейнеров: `docker container prune`.

## Q21. (!) Какие типы хранения данных поддерживает `Docker`?

```mermaid
graph LR
    subgraph "Docker Host"
        V[Named Volume<br/>/var/lib/docker/volumes/] 
        B[Bind Mount<br/>любой путь хоста]
        T[tmpfs Mount<br/>только в RAM]
    end
    V --> C1[Container]
    B --> C1
    T --> C1
```

| Тип | Управление | Производительность | Переносимость |
|---|---|---|---|
| **Named Volume** | Docker | Высокая | Высокая (docker volume) |
| **Bind Mount** | Пользователь | Зависит от хоста | Низкая (привязка к пути) |
| **tmpfs** | Docker | Максимальная (RAM) | Нет (данные в памяти) |

```bash
# Named volume
docker run -v mydata:/var/lib/postgresql/data postgres:16

# Bind mount
docker run -v $(pwd)/config:/app/config myapp:1.0

# tmpfs (для чувствительных данных)
docker run --tmpfs /tmp:rw,size=100m myapp:1.0
```

**Named volumes** — рекомендуемый способ для баз данных и персистентных данных. Переживают удаление контейнера. **Bind mounts** — для конфигов и разработки (изменения на хосте видны в контейнере). **tmpfs** — для временных или секретных данных, которые не должны сохраняться на диск.

## Q22. Где физически хранятся `Docker Volumes`?

Именованные и анонимные тома хранятся на хосте:

```text
/var/lib/docker/volumes/<volume_name>/_data/
```

На `macOS` / `Windows` (`Docker Desktop`) путь виртуализирован внутри `VM` (`HyperKit` / `WSL2`). Для бэкапа тома:

```bash
docker run --rm \
  -v myvolume:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/myvolume-backup.tar.gz /data
```

Команды управления: `docker volume ls` — список, `docker volume inspect myvolume` — подробности, `docker volume rm myvolume` — удаление, `docker volume prune` — удаление неиспользуемых.

## Q23. При каких обстоятельствах теряются данные контейнера?

Данные теряются при:
1. **Удалении контейнера** (`docker rm`) — writable-слой удаляется
2. **Пересоздании контейнера** без примонтированного тома
3. **`docker compose down -v`** — удаляет и контейнеры, и тома

Способы сохранения данных:
- **Named volumes** для баз данных: `docker run -v pgdata:/var/lib/postgresql/data postgres`
- **Bind mounts** для конфигов: `-v ./config:/app/config`
- **Регулярные бэкапы** томов

В production **никогда** не использовать `docker compose down -v` без бэкапа. Для [Kubernetes](kubernetes-interview.md) аналог — `PersistentVolumeClaim`.

## Q24. (!) Какие сетевые драйверы поддерживает `Docker`?

```mermaid
graph TB
    subgraph "bridge (default)"
        B[docker0 bridge] --> C1[Container 1<br/>172.17.0.2]
        B --> C2[Container 2<br/>172.17.0.3]
    end
    subgraph "host"
        H[Host Network Stack] --> C3[Container 3<br/>uses host IP]
    end
    subgraph "overlay (Swarm)"
        O[VXLAN Tunnel] --> C4[Container on Host 1]
        O --> C5[Container on Host 2]
    end
```

| Драйвер | Описание | Когда использовать |
|---|---|---|
| `bridge` | Виртуальный мост на хосте (default) | Одиночный хост, изоляция контейнеров |
| `host` | Контейнер использует сетевой стек хоста | Максимальная производительность, нет изоляции |
| `overlay` | Сеть между несколькими хостами (`VXLAN`) | `Docker Swarm`, мульти-хост |
| `macvlan` | Контейнер получает MAC-адрес, виден в LAN | Legacy-приложения, прямой доступ к LAN |
| `none` | Без сети | Полная изоляция |

```bash
# Создать пользовательскую bridge-сеть
docker network create --driver bridge my-network

# Запустить контейнер в сети
docker run --network my-network --name app myapp:1.0

# Режим host (Linux only)
docker run --network host myapp:1.0
```

В пользовательской `bridge`-сети контейнеры обращаются друг к другу по имени (встроенный `DNS`). В default bridge — только по `IP`.

## Q25. Как контейнеры общаются между собой и с хостом?

**Контейнер → контейнер** (та же сеть):
- По имени контейнера или сервиса (DNS-резолвинг в пользовательской bridge-сети)
- Пример: `Spring Boot` → `PostgreSQL` по `jdbc:postgresql://db:5432/mydb`

**Контейнер → хост**:
- `host.docker.internal` (`Docker Desktop` на `macOS` / `Windows`)
- Gateway IP bridge-сети (`172.17.0.1` по умолчанию на `Linux`)
- `--network host` — контейнер использует сеть хоста напрямую

**Хост → контейнер**:
- Через проброс порта: `-p 8080:8080` (host_port:container_port)
- `-p 127.0.0.1:8080:8080` — только с localhost

```bash
# Узнать IP контейнера
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_id

# Просмотреть сети
docker network ls
docker network inspect bridge
```

## Q26. В чём разница между `expose` и `ports` в `Docker Compose`?

```yaml
services:
  app:
    image: myapp:1.0
    expose:
      - "8080"        # доступен ТОЛЬКО другим контейнерам в той же сети
    ports:
      - "8080:8080"   # доступен с хоста И другим контейнерам
```

| Директива | Доступ с хоста | Доступ из других контейнеров |
|---|---|---|
| `expose` | Нет | Да |
| `ports` | Да | Да |

`expose` — декларативно, аналог `EXPOSE` в `Dockerfile`. `ports` — реально пробрасывает порт. Для внутренних сервисов (БД, кэш) достаточно `expose`; для внешних API — `ports`.

## Q27. (!) Что такое `Docker Compose` и как он работает?

**Docker Compose** — инструмент для описания и запуска мультиконтейнерного приложения в одном файле `docker-compose.yml`: сервисы, сети, тома, зависимости.

```yaml
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
    depends_on:
      db:
        condition: service_healthy
    networks:
      - backend

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d mydb"]
      interval: 5s
      timeout: 3s
      retries: 5
    networks:
      - backend

volumes:
  pgdata:

networks:
  backend:
```

```bash
docker compose up -d       # запуск в фоне
docker compose down        # остановка и удаление контейнеров
docker compose ps          # статус сервисов
docker compose logs -f app # логи сервиса
docker compose exec app sh # вход в контейнер
```

Compose создаёт общую сеть по умолчанию — сервисы обращаются друг к другу по имени (например, `db:5432`). Для production образы тегировать по версии; секреты — через `.env`-файл или внешнее хранилище.

## Q28. В чём разница между `docker compose up`, `run` и `start`?

| Команда | Создаёт контейнеры | Запускает зависимости | Использование |
|---|---|---|---|
| `up` | Да | Да (все сервисы) | Запуск всего стека |
| `run` | Да (одноразовый) | Да (зависимости) | Разовая задача (миграция, тесты) |
| `start` | Нет (только существующие) | Нет | Перезапуск остановленных |

```bash
docker compose up -d                    # запуск всех сервисов
docker compose run --rm app ./migrate   # разовая команда
docker compose start db                 # запуск ранее остановленного
```

`up` — основная команда; `-d` для фонового режима, `--build` для пересборки образов. `run` создаёт новый контейнер для одноразовой задачи; `--rm` удаляет его после завершения.

## Q29. Как управлять порядком запуска сервисов в `Compose`?

`depends_on` определяет порядок запуска, но **не ждёт готовности** сервиса:

```yaml
services:
  app:
    depends_on:
      db:
        condition: service_healthy   # ждёт healthcheck
      redis:
        condition: service_started   # просто ждёт запуска
```

Без `condition: service_healthy` `Compose` запускает зависимый сервис сразу после старта контейнера БД — приложение может упасть, т.к. `PostgreSQL` ещё не готов принимать соединения.

Альтернативы:
- **`healthcheck`** в `Compose` + `condition: service_healthy` (рекомендуется)
- **wait-for-it.sh** / **dockerize** — скрипты ожидания порта
- **Spring Boot retry** — повторные попытки подключения на уровне приложения

## Q30. Что такое `Docker Compose Support` в `Spring Boot 3`?

Начиная с `Spring Boot 3.1`, фреймворк умеет автоматически управлять `Docker Compose`:

```yaml
# application.yml
spring:
  docker:
    compose:
      enabled: true
      file: docker-compose.yml
      lifecycle-management: start-and-stop  # или start-only
```

При `./gradlew bootRun`:
1. `Spring Boot` запускает `docker compose up` автоматически
2. Определяет порты и настройки сервисов (БД, `Redis`, `Kafka`)
3. Автоматически конфигурирует `DataSource`, `RedisConnectionFactory` и др.
4. При остановке приложения — `docker compose stop`

Поддерживаемые сервисы: `PostgreSQL`, `MySQL`, `MongoDB`, `Redis`, `Kafka`, `RabbitMQ`, `Elasticsearch`. Это упрощает локальную разработку — не нужно вручную запускать `docker compose` и дублировать настройки подключения.

## Q31. (!) Какие best practices безопасности `Docker` вы знаете?

1. **Минимальный базовый образ**: `alpine`, `distroless`, `slim` — меньше пакетов = меньше уязвимостей
2. **Непривилегированный пользователь**: `USER 1001` в `Dockerfile` (не `root`)
3. **Сканирование образов**: `Trivy`, `Snyk`, `Docker Scout` — на этапе CI
4. **Конкретные теги**: `eclipse-temurin:17-jre-alpine`, не `latest`
5. **Секреты не в образе**: не `COPY .env`, не `ENV PASSWORD=...`; использовать `Docker Secrets`, env-файлы, `Vault`
6. **Read-only файловая система**: `--read-only` + `tmpfs` для `/tmp`
7. **Ограничение capabilities**: `--cap-drop=ALL --cap-add=NET_BIND_SERVICE`
8. **Healthcheck**: для автоматического перезапуска нездоровых контейнеров
9. **Многоуровневая сборка**: инструменты сборки не попадают в runtime-образ
10. **`.dockerignore`**: исключить `.git`, `.env`, `build/`, `node_modules/`

```dockerfile
# Пример безопасного Dockerfile для Java
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --chown=app:app target/*.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=30s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
```

Подробнее о безопасности приложений — в [вопросах по Application Security](../security/application-security-interview.md).

## Q32. Как запускать контейнеры от непривилегированного пользователя?

```dockerfile
# Вариант 1: создать пользователя в Dockerfile
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --chown=appuser:appgroup target/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Вариант 2: задать пользователя при запуске
docker run --user 1001:1001 myapp:1.0
```

Почему это важно: по умолчанию процесс в контейнере запускается от `root` (UID 0). Если злоумышленник эксплуатирует уязвимость и «выйдет» из контейнера, он получит `root`-доступ к хосту. С непривилегированным пользователем ущерб ограничен.

**Docker Rootless mode** — запуск самого демона `dockerd` от обычного пользователя. Дополнительный уровень защиты, но с ограничениями (no `cgroups v1`, ограниченные привилегированные порты).

## Q33. Как управлять секретами в `Docker`?

| Способ | Безопасность | Когда использовать |
|---|---|---|
| Переменная окружения (`-e`) | Средняя (видна в `docker inspect`) | Разработка |
| `.env`-файл | Средняя (не коммитить в Git!) | Локальная разработка |
| `Docker Secrets` (Swarm) | Высокая (шифрование, tmpfs) | Docker Swarm |
| Внешнее хранилище (`Vault`, `AWS SM`) | Максимальная | Production |

```yaml
# docker-compose.yml с .env-файлом
services:
  app:
    image: myapp:1.0
    env_file:
      - .env  # НЕ коммитить в Git!
```

```bash
# .env
SPRING_DATASOURCE_PASSWORD=secret123
DEEPSEEK_API_KEY=sk-xxx
```

**Антипаттерны**: `COPY .env /app/` в `Dockerfile`; `ENV SECRET=value` в `Dockerfile`; хардкод в `docker-compose.yml`. Все эти варианты сохраняют секрет в слоях образа.

## Q34. (!) Какие политики перезапуска контейнеров существуют?

| Политика | Поведение |
|---|---|
| `no` (default) | Не перезапускать |
| `on-failure[:max]` | Перезапуск при ненулевом exit code (макс. N раз) |
| `always` | Всегда, включая ручную остановку (после `docker restart`) |
| `unless-stopped` | Всегда, кроме ручной остановки (`docker stop`) |

```bash
# Через CLI
docker run -d --restart=unless-stopped myapp:1.0

# Через Compose
```

```yaml
services:
  app:
    image: myapp:1.0
    restart: unless-stopped
    deploy:
      restart_policy:
        condition: on-failure
        max_attempts: 3
        delay: 5s
```

Для production: `unless-stopped` или `on-failure` с лимитом попыток. В [Kubernetes](kubernetes-interview.md) аналог — `restartPolicy` в Pod spec (`Always`, `OnFailure`, `Never`).

## Q35. Как ограничить ресурсы контейнера (`CPU`, память)?

```bash
# CLI
docker run -d \
  --memory=512m \
  --memory-swap=1g \
  --cpus=1.5 \
  --pids-limit=100 \
  myapp:1.0
```

```yaml
# docker-compose.yml
services:
  app:
    image: myapp:1.0
    deploy:
      resources:
        limits:
          cpus: '1.5'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

**Важно для Java**: JVM должна знать о лимитах контейнера. Начиная с `Java 10+`, флаг `-XX:+UseContainerSupport` (включён по умолчанию) позволяет JVM видеть лимиты `cgroups`. Без него JVM видит всю память хоста и может быть убита `OOM Killer`.

```bash
# Рекомендация для Java в контейнере
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
```

`-XX:MaxRAMPercentage=75.0` — JVM использует 75% лимита памяти контейнера, оставляя 25% для не-heap памяти и ОС.

## Q36. Сколько контейнеров можно запустить на одном хосте?

Теоретически — без ограничений. Практически лимит определяется:

| Фактор | Влияние |
|---|---|
| RAM | Каждый контейнер потребляет память |
| CPU | Конкуренция за процессорное время |
| Disk I/O | Слои образов, writable layer |
| PID limit | Максимум процессов в ядре |
| Сетевые ресурсы | Порты, соединения, file descriptors |

На мощном хосте (64 GB RAM, 16 CPU) можно запустить сотни-тысячи лёгких контейнеров. Для `Java`-приложений с типичным потреблением 256-512 MB — десятки. Мониторинг: `docker stats` — `CPU`, `RAM`, сеть, disk I/O по контейнерам в реальном времени.

## Q37. Как работает логирование в `Docker`?

`Docker` поддерживает логирование на двух уровнях:

**Container level** — `stdout` / `stderr` основного процесса контейнера:
```bash
docker logs container_id           # все логи
docker logs -f container_id        # follow (как tail -f)
docker logs --since 1h container_id # за последний час
docker logs --tail 100 container_id # последние 100 строк
```

**Logging drivers** — куда направляются логи:

| Драйвер | Назначение |
|---|---|
| `json-file` (default) | JSON-файлы на хосте |
| `syslog` | Syslog-демон |
| `fluentd` | Fluentd / Fluent Bit |
| `gelf` | Graylog |
| `awslogs` | AWS CloudWatch |
| `none` | Отключить логи |

```bash
# Настройка драйвера при запуске
docker run --log-driver=json-file --log-opt max-size=10m --log-opt max-file=3 myapp:1.0
```

**Best practice**: ограничивать размер лог-файлов (`max-size`, `max-file`), иначе логи заполнят диск. Для production — централизованное логирование (`ELK`, `Loki`). Подробнее — в [вопросах по наблюдаемости](../monitoring/observability-interview.md) и [стратегиях логирования](../monitoring/logging-strategies-interview.md).

## Q38. (!) Как отлаживать проблемы с контейнером?

Пошаговый алгоритм диагностики:

```bash
# 1. Проверить статус и код выхода
docker ps -a --filter "name=myapp"

# 2. Посмотреть логи
docker logs --tail 200 container_id

# 3. Проверить использование ресурсов
docker stats container_id

# 4. Войти в работающий контейнер
docker exec -it container_id sh

# 5. Посмотреть метаданные (env, mounts, network)
docker inspect container_id

# 6. Проверить файловую систему остановленного контейнера
docker cp container_id:/app/logs/ ./debug-logs/

# 7. Проверить сеть
docker network inspect bridge
docker exec container_id ping another-container

# 8. Проверить healthcheck
docker inspect --format='{{json .State.Health}}' container_id
```

Типичные проблемы:
- **Exit code 137**: `OOM Killer` — контейнер превысил лимит памяти → увеличить `--memory` или оптимизировать приложение
- **Exit code 143**: `SIGTERM` → нормальная остановка
- **Exit code 1**: ошибка приложения → смотреть логи
- **Контейнер не стартует**: проверить `CMD` / `ENTRYPOINT`, права на файлы, доступность портов

## Q39. (!) Что такое `Docker BuildKit` и чем он лучше классического builder?

`BuildKit` — современный движок сборки `Docker`-образов, включён по умолчанию с `Docker 23.0`. Полностью заменяет legacy builder.

**Ключевые преимущества:**

| Возможность | Классический builder | BuildKit |
|---|---|---|
| Параллельная сборка стадий | Нет | Да (multi-stage параллельно) |
| Инлайн cache | Нет | Да (`--cache-from`, `--cache-to`) |
| Монтирование секретов | Нет | Да (`--mount=type=secret`) |
| Монтирование кэша пакетов | Нет | Да (`--mount=type=cache`) |
| SSH-форвардинг | Нет | Да (`--mount=type=ssh`) |
| Игнорирование неиспользуемых стадий | Нет | Да |

```bash
# Включить BuildKit (если не включён по умолчанию)
export DOCKER_BUILDKIT=1

# Или через docker buildx (расширенный BuildKit)
docker buildx build --platform linux/amd64,linux/arm64 -t myapp:1.0 --push .
```

**`RUN --mount=type=cache`** — кэширование директорий пакетных менеджеров между сборками:

```dockerfile
# Кэш Gradle не удаляется между сборками — зависимости скачиваются только при изменении
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Кэш зависимостей Gradle монтируется из хоста
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=bind,source=build.gradle,target=build.gradle \
    --mount=type=bind,source=settings.gradle,target=settings.gradle \
    --mount=type=bind,source=gradle,target=gradle \
    --mount=type=bind,source=gradlew,target=gradlew \
    ./gradlew dependencies --no-daemon

COPY src/ src/
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon -x test
```

**`RUN --mount=type=secret`** — секреты не попадают в слои образа:

```dockerfile
# Секрет доступен только во время этой инструкции RUN
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn package -s /root/.m2/settings.xml
```

```bash
# Передача секрета при сборке
docker buildx build --secret id=maven_settings,src=~/.m2/settings.xml .
```

`BuildKit` — это обязательная тема для senior-уровня. В `CI/CD` использование `--mount=type=cache` может сократить время сборки в 3-5 раз на повторных сборках.

## Q40. Как использовать кэш сборки уровня registry (`cache mounts`)?

`BuildKit` поддерживает экспорт/импорт кэша в/из `OCI registry`. Это особенно ценно в `CI/CD`, где каждый runner — чистая среда без локального кэша.

```bash
# Экспорт кэша в registry (inline — кэш встроен в image manifest)
docker buildx build \
  --cache-to type=inline \
  --push \
  -t registry.example.com/myapp:latest .

# Максимально эффективный вариант: registry cache
docker buildx build \
  --cache-from type=registry,ref=registry.example.com/myapp:buildcache \
  --cache-to   type=registry,ref=registry.example.com/myapp:buildcache,mode=max \
  --push \
  -t registry.example.com/myapp:latest .
```

**Параметр `mode`:**
- `mode=min` — кэшируются только финальные слои (по умолчанию для inline)
- `mode=max` — кэшируются все промежуточные слои multi-stage сборки

**Пример в GitLab CI:**

```yaml
build:
  stage: build
  script:
    - docker buildx build
        --cache-from type=registry,ref=${CI_REGISTRY_IMAGE}:buildcache
        --cache-to   type=registry,ref=${CI_REGISTRY_IMAGE}:buildcache,mode=max
        --push
        -t ${CI_REGISTRY_IMAGE}:${CI_COMMIT_SHA}
        .
```

```mermaid
sequenceDiagram
    participant CI as CI Runner
    participant REG as Registry

    CI->>REG: Pull buildcache (if exists)
    CI->>CI: docker buildx build (cache hit — skip layers)
    CI->>REG: Push new image + updated buildcache
```

**В Spring Boot проектах** сочетайте registry cache с `--mount=type=cache` для Gradle: это покрывает оба уровня кэширования — кэш зависимостей на ноде и кэш слоёв образа в registry.

## Q41. Как сканировать `Docker`-образ на уязвимости?

Сканирование — обязательный шаг в production CI/CD. Инструменты анализируют пакеты внутри образа на известные CVE.

**`Trivy`** (рекомендуется, open source, быстрый):

```bash
# Сканировать образ
trivy image eclipse-temurin:17-jre-alpine

# Только критичные и высокие
trivy image --severity HIGH,CRITICAL myapp:1.0

# Вывод в формате для CI (non-zero exit при уязвимостях)
trivy image --exit-code 1 --severity CRITICAL myapp:1.0

# Сканировать Dockerfile на мисконфигурации
trivy config Dockerfile
```

**`docker scout`** (встроен в Docker Desktop):

```bash
docker scout cves myapp:1.0
docker scout recommendations myapp:1.0
```

**`Snyk`** (интеграция с IDE и CI):

```bash
snyk container test myapp:1.0 --severity-threshold=high
```

**Интеграция в GitLab CI:**

```yaml
trivy-scan:
  image: aquasec/trivy:latest
  stage: test
  script:
    - trivy image
        --exit-code 1
        --severity HIGH,CRITICAL
        --no-progress
        ${CI_REGISTRY_IMAGE}:${CI_COMMIT_SHA}
  allow_failure: false
```

**Стратегия снижения уязвимостей:**
1. Использовать минимальный базовый образ: `alpine`, `distroless`, `slim`
2. Регулярно обновлять базовый образ (`FROM eclipse-temurin:17-jre-alpine` → следить за тегами)
3. Запускать сканирование в `CI` и блокировать деплой при `CRITICAL`
4. Сканировать не только при сборке, но и по расписанию (новые CVE выходят постоянно)

---

## See also

- [Kubernetes](kubernetes-interview.md) — оркестрация контейнеров на основе Docker-образов
- [Проектирование CI/CD пайплайнов](../cicd/pipeline-design-interview.md) — сборка и деплой образов
- [Стратегии деплоя](../cicd/deployment-strategies-interview.md) — blue-green, canary с контейнерами
- [Микросервисная архитектура](../architecture/microservices-interview.md) — Docker как основа микросервисов
- [Git](git-interview.md) — версионирование Dockerfile и конфигурационных файлов
- [Gradle и Maven](gradle-maven-interview.md) — Gradle Docker Plugin и Jib для сборки образов
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — контейнеризация Spring Boot приложений с Buildpacks
- [Наблюдаемость](../monitoring/observability-interview.md) — логирование и мониторинг контейнеров

- [Ansible](ansible-interview.md)
- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
- [Helm](helm-interview.md)
