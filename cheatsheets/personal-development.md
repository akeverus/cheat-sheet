---
title: "Реализация плана развития Java Backend + AI"
description: "Практическая инструкция по выполнению личного плана: карта проектов, недельный ритм, чек-листы фаз, AI- и резюме-треки."
tags: ["personal", "learning-plan", "java-backend", "ai-tools"]
updated: "2026-03-16"
---

## 1. Карта опорных проектов (baseline-map)

- **cheat-sheet (Interview Prep)**
  - **Домен**: подготовка к собеседованиям, интервальное повторение, генерация вариантов через LLM.
  - **Стек**: Java 17, Spring Boot, Gradle Kotlin DSL, SQLite/PostgreSQL, Flyway, Thymeleaf, Spring AI, Caffeine.
  - **Ключевые навыки**: модульная архитектура (`quiz-domain`, `quiz-persistence`, `quiz-app`), интеграция с AI, SM-2, полнотекстовый поиск, MVC+REST.
  - **Где смотреть код**: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/**`, `modules/quiz-persistence/**`, `modules/quiz-domain/**`.

- **Kratos (ecom-backend-kratos)**
  - **Домен**: расчёт доставки и стоимости, работа с зонами доставки и мастер-данными по товарам.
  - **Стек**: Java 17, Spring Boot 3, PostgreSQL + PostGIS, Redis, Kafka, MariaDB, JOOQ, Liquibase, Docker Compose, GitLab CI, Testcontainers, Elastic APM, Unleash.
  - **Ключевые навыки**: сложная доменная модель, ETL, гео-запросы, события в Kafka, versioning через jgitver, соглашения по Liquibase и REST.
  - **Стартовые точки чтения**: `README.md`, конфигурация Liquibase и ETL, REST-контроллеры расчёта доставки.

- **Catalog (backend-products)**
  - **Домен**: каталог товаров, категорий, брендов и стоков; основное хранилище данных о продуктах.
  - **Стек**: Kotlin/Java, Spring Boot, Solr, PostgreSQL, Kafka, Liquibase, Ceph/SFTP, Jib, Testcontainers, JMH, Gatling, Unleash.
  - **Ключевые навыки**: поисковый индекс (Solr), event-driven интеграции по Kafka, мульти-БД, производительность (JMH, Gatling), feature flags.
  - **Стартовые точки чтения**: `README.MD`, описание интеграций по Kafka, Solr- и Liquibase-конфигурация.

- **Cart (backend-cart / detmir-cart)**
  - **Домен**: сервис корзины (cart), работа с Redis и PostgreSQL, интеграции с другими сервисами.
  - **Стек**: Java 21, Spring Boot 3, internal Chassis, PostgreSQL, Redis, Kafka, Liquibase, Protobuf, Caffeine, Docker Compose, GitLab CI, SonarQube, Testcontainers.
  - **Ключевые навыки**: patterns кэширования в Redis и Caffeine, сериализация (one-nio), TSID, Shedlock, интеграции с внешними сервисами, работа с dev-стендами и port-forward в Kubernetes.
  - **Стартовые точки чтения**: `backend-cart/README.md`, конфигурация Redis/DB, основные REST-эндпоинты корзины.

- **OMS**
  - **Домен**: управление заказами (Order Management System).
  - **Стек**: Kotlin 2.x, Java 21, Spring Boot 3.4, PostgreSQL, JOOQ, Liquibase, Kafka, JobRunr, Jib, GitLab CI, SonarQube, Testcontainers, Detekt, ArchUnit.
  - **Ключевые навыки**: Kotlin backend, coroutines, асинхронные джобы, строгие архитектурные проверки (ArchUnit), стиль тестов и code quality.
  - **Стартовые точки чтения**: `oms/README.md`, `build.gradle.kts`, описание модулей и интеграций с Kafka/БД.

### 1.1. Быстрый список технологий по проектам

- **Java/Spring**: все проекты.
- **Kotlin**: `backend-products`, `oms`, часть учебных примеров в `cheatsheets/languages/kotlin/**`.
- **БД**: PostgreSQL (везде), PostGIS (Kratos), Redis (Cart), Solr (Catalog), SQLite (cheat-sheet).
- **Сообщения**: Kafka (Kratos, Catalog, Cart, OMS).
- **Инфраструктура**: Docker Compose, Jib, GitLab CI, SonarQube, Testcontainers, Unleash feature flags.
- **Наблюдаемость**: Elastic APM (Catalog, Kratos), метрики/трейсинг в cheat-sheet.

### 1.2. 3‑месячные приоритеты

1. **Java/JVM**: укрепить Java Core, коллекции, исключения, многопоточность, базовую производительность JVM.
2. **SQL/PostgreSQL**: индексы, планы выполнения, транзакции, типичные паттерны запросов в e‑commerce.
3. **Spring + backend practice**: Spring Boot, MVC, Data JPA/JOOQ, тестирование, кэширование, REST‑дизайн, базовая безопасность.

Эти приоритеты будут основой для Фазы 1 и привязаны к задачам в `quiz-app`, LeetCode/SQL‑тренажеру и кода в `kratos`/`cart`.

---

## 2. Недельный ритм (weekly-rhythm)

Использовать как шаблон. Время можно двигать по дням, но суммарная нагрузка ≈ **4–6 часов в неделю**.

- **Сессия А (45–60 минут)** — теория.
  - Выбрать одну тему из `cheatsheets/interview/TOC.md` или ближайших к ней шпаргалок.
  - Прочитать и кратко проговорить вслух ключевые определения и типичные вопросы.
  - Зафиксировать 3–5 bullet-поинтов в личных заметках (можно в отдельном файле или в таск-менеджере).

- **Сессия B (45–60 минут)** — повторение + тренажер.
  - Запустить `quiz-app` и пройти 1 фокус-сессию по текущей группе тем (Java/SQL/Spring).
  - Включить **review mode** или **weak topics**, чтобы подтягивать слабые темы.
  - Решить 1–2 задачи: LeetCode (по текущей структуре данных/алгоритму) или SQL‑тренажер.

- **Сессия C (2–3 часа)** — практика.
  - Вариант 1: разобрать end‑to‑end flow в `kratos`, `catalog` или `cart` (от REST до БД/кэша/события).
  - Вариант 2: mini‑эксперимент / pet‑пример (например, локальный сервис на Kotlin, новый тест с Testcontainers).
  - Вариант 3: AI‑workflow (см. ниже) или работа с резюме/самопрезентацией.

- **Если неделя тяжёлая**:
  - Оставить только Сессию B (quiz + одна маленькая практика).
  - Не пытаться «догонять» пропуски — просто вернуться в привычный ритм на следующей неделе.

---

## 3. Чек-листы фаз

### 3.1. Фаза 1 — фундамент (phase-1-core)

Фокус: **Java → SQL → Spring → Docker/Git**.

- **Java / JVM**
  - Пройти вопросы: `java-core`, `java-types`, `java-collections`, `java-generics`, `java-exceptions`, `java-io-nio`, `java-oop`, `java-stream`, `java-string`, `java-concurrency-basics`.
  - Для каждой подтемы сделать хотя бы 1 фокус-сессию в `quiz-app`.
  - Решить минимум **20–30 задач** уровня Easy/часть Medium (arrays, strings, hash‑tables, two pointers, sliding window).

- **SQL / PostgreSQL**
  - Пройти interview-файл `databases/sql-interview.md` и шпаргалки по Postgres (`structure`, `queries`, `indexes`, `transactions`).
  - Выполнить **20+ практических запросов** (join’ы, агрегаты, оконные, индексы) в любом тренажере или локальной БД.

- **Spring / Web**
  - Пройти `spring-framework`, `spring-boot`, `spring-mvc`, `spring-data-jpa`.
  - В одном из проектов (`cart` или учебный мини-сервис) руками поднять REST‑эндпоинт + простую интеграцию с БД.

- **Инструменты**
  - Убедиться, что уверенно пользуешься Git flow в рабочих проектах.
  - Понять базу Docker (образы, контейнеры, compose) на примере `backend-products` или `cheat-sheet`.

**Критерий завершения фазы**: спокойно объясняешь 2 своих проекта, не спотыкаясь на базовых вопросах по Java/SQL/Spring, и имеешь ~30 задач/20 SQL‑примеров в активе.

### 3.2. Фаза 2 — production backend (phase-2-backend)

Фокус: **безопасность, кэширование, Kafka, тесты, наблюдаемость**.

- **Spring Security / Auth**
  - Interview-файлы `application-security`, `authentication-authorization-patterns`, `oauth2`, `owasp-top10`.
  - Найти и прочитать реальные конфигурации безопасности в одном из проектов (или учебном).

- **Хранение и кэш**
  - Redis: interview + просмотр использования в `cart`.
  - Caffeine: как устроен кэш, где и как он используется.

- **Kafka и event-driven**
  - Interview `messaging/kafka-interview.md`.
  - Разобрать хотя бы по одному consumer/producer’у в `catalog` и `kratos` (какие топики, формат сообщений, ретраи).

- **Тестирование и Testcontainers**
  - Пройти interview по `unit-testing`, `integration-testing`, `test-strategies`.
  - Взять 1–2 тест-кейса с Testcontainers из рабочих проектов и «прогуляться» по ним, потом попробовать добавить/изменить один.

- **Наблюдаемость / эксплуатация**
  - Интервью-файлы: `logging`, `logging-strategies`, `metrics-tracing`, `observability`, `application-profiling`.
  - Понять, как в рабочих сервисах подключены APM/метрики/логи.

**Критерий завершения фазы**: можешь рассказать про один кейс надежности (reties/timeouts/circuit breaker) и один кейс observability/логирования в своих сервисах, плюс уверенно ориентируешься в одной Kafka-потоковой интеграции.

### 3.3. Фаза 3 — распределённые системы (phase-3-system)

Фокус: **distributed systems, system design, CI/CD, Kubernetes, security**.

- **Архитектура**
  - Интервью: `distributed-systems`, `microservices`, `cap-theorem`, `consistency-patterns`, `load-balancing`, `scalability-patterns`, `system-design`.
  - Раз в месяц делать дизайн-упражнение по знакомому домену (cart, catalog, kratos, oms).

- **Delivery / DevOps**
  - Интервью по `cicd/pipeline-design`, `devops/docker-interview`, `devops/kubernetes-interview`.
  - Пройти по одному полному пайплайну GitLab CI в любом из проектов.

- **Security**
  - Повторить OWASP, OAuth2, общие практики hardening.

**Критерий завершения**: уверенно проговариваешь end‑to‑end архитектуру хотя бы одного сервиса и можешь набросать high-level дизайн типичной задачи за 30–40 минут.

### 3.4. Фаза 4 — рост до strong middle / senior

Фокус: **производительность, SRE‑мышление, лидерство и code review**.

- JVM performance, profiling, memory management.
- Advanced SQL, search/relevance (через Catalog + Solr).
- SRE и reliability: SLO/SLI, error budgets, инциденты и постмортемы.
- Code review, technical debt, лидерские аспекты в команде.

**Критерий**: можешь спокойно рассказать о нескольких сложных инцидентах/улучшениях, их эффекте и trade-offs.

---

## 4. Непрерывный AI-трек (ai-track)

- **Месяц 1** — закрепить базу:
  - Cursor, ChatGPT/Claude в браузере, Copilot в IDE.
  - Описать для себя 3–5 типичных сценариев, где какой инструмент удобнее.

- **Месяц 2** — продвинутые инструменты:
  - Aider (CLI‑редактор через git‑патчи), OpenClaw (автоматизация), агентные режимы в Cursor/Windsurf/Claude.

- **Месяц 3** — prompt engineering:
  - Структура промпта: контекст → задача → ограничения → формат → примеры.
  - Выделить reusable‑промпты и добавить их в каталог `prompts/`.

- **Месяц 4** — MCP и интеграции:
  - Понять, какие MCP‑серверы используются, где они помогают (fs, browser и т.п.).

- **Месяц 5+** — n8n и автоматизация:
  - Сценарии автоматизации повторяющихся задач (сбор статей, напоминания на повторение, выгрузка статистики из quiz‑app).

Общее правило: **каждый месяц внедрять минимум 1 рабочий AI‑workflow**, который реально экономит время.

---

## 5. Трек резюме и самопрезентации (resume-track)

- **Еженедельно**
  - Фиксировать как минимум **1 bullet**: задача → действие → результат (с цифрами, если возможно).

- **Раз в 2 недели**
  - Переписывать одну историю в формате `Context → Problem → Action → Result → Learning` (см. `interview-preparation.md`).

- **Раз в месяц**
  - Освежать раздел «Проекты»: `kratos`, `catalog`, `cart`, `oms`, `cheat-sheet`.

- **Раз в 6–8 недель**
  - Маленький ревью резюме: убрать воду, добавить метрики, усилить формулировки по сложности систем и личному вкладу.

Используй этот файл как «операционное руководство»: достаточно выбирать ближайшую фазу и подтему, затем планировать 1–2 недели вперёд по шаблону из раздела 2.

