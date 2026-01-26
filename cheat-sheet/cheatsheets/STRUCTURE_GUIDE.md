# Руководство по структурированию файлов (Backend Focus)

Это руководство описывает стандарты и соглашения для организации файлов в backend-focused проекте cheatsheets.

**Дата создания:** 2025-01-11
**Дата последнего обновления:** 2026-01-23
**Фокус:** Backend разработка (JVM + Go), исключены frontend/mobile

---

## Соглашения об именовании

### Файлы и папки

- **Имена файлов и папок**: `kebab-case` (латинские символы, дефисы для разделения слов)
  - ✅ Правильно: `java-basics.md`, `spring-data-jpa.md`, `docker-spring-boot.md`
  - ❌ Неправильно: `Java Basics.md`, `spring_data_jpa.md`, `dockerSpringBoot.md`

- **Префиксы категорий**: Использовать префикс категории, если файл относится к конкретной технологии
  - Java: `java-{topic}.md`
  - Spring: `spring-{topic}.md`
  - PostgreSQL: `postgres-{topic}.md`

### Структура имен файлов

**Базовый формат:**
```
{category}-{topic}.md
```

**Для подтем:**
```
{category}-{topic}-{subtopic}.md
```

**Примеры:**
- `java-basics.md` - основы Java
- `java-concurrency-basics.md` - основы многопоточности Java
- `java-concurrency-advanced.md` - продвинутая многопоточность Java
- `spring-data-jpa.md` - Spring Data JPA
- `postgres-indexes.md` - индексы PostgreSQL

---

## Структура содержимого файла

### Обязательные элементы

Каждый файл должен содержать:

1. **Заголовок H1** с названием темы
2. **Краткое описание** (1-2 предложения)
3. **Дата последнего обновления** в формате `YYYY-MM-DD`
4. **Секция "Полезные ссылки"**
5. **Оглавление** с якорными ссылками
6. **Основное содержимое**

### Шаблон файла

```markdown
# Название темы

Кратко: краткое описание темы в 1-2 предложениях.

**Дата последнего обновления:** YYYY-MM-DD

## Полезные ссылки

### Официальная документация
- [Название](URL)

### Baeldung (если применимо)
- [Tutorial](URL)

### См. также
- `../related-file.md` - описание связи
- `./other-topic.md` - описание связи

## Содержание

- [Раздел 1](#раздел-1)
- [Раздел 2](#раздел-2)
- [Подраздел 2.1](#подраздел-21)

## Раздел 1

Контент раздела...

### Подраздел 1.1

Детали подраздела...

## Раздел 2

Контент...

## Подраздел 2.1

Детали...
```

---

## Организация по категориям

### Когда создавать отдельный файл?

Создавайте отдельный файл, если:
- Тема содержит **более 300 строк** реального контента
- Тема логически отдельна и может использоваться независимо
- Тема требует детального покрытия (например, `java-concurrency-basics.md` и `java-concurrency-advanced.md`)

### Когда объединять в один файл?

Объединяйте в один файл, если:
- Темы тесно связаны и используются вместе
- Каждая тема меньше 200 строк
- Темы образуют единую концепцию (например, все типы коллекций List в одном файле)

### Когда создавать подпапки?

Создавайте подпапки, если:
- В категории более 10 файлов на одну тему
- Файлы логически группируются (например, `linux/file-operations/`, `linux/administration/`)
- Нужна дополнительная организация

---

## Стандарты именования разделов

### Основные разделы (H2)

Используйте описательные названия:
- ✅ Правильно: `## Работа с коллекциями`, `## Основы многопоточности`
- ❌ Неправильно: `## Раздел 1`, `## Коллекции`

### Подразделы (H3, H4)

Используйте конкретные названия:
- ✅ Правильно: `### Создание списка`, `### Методы списка`
- ❌ Неправильно: `### Основное`, `### Детали`

---

## Минимальные требования к контенту

### Размер файла

- **Минимум**: 300 строк реального контента (не считая заголовки и пустые строки)
- **Рекомендуется**: 500-1000 строк для основных тем
- **Максимум**: 2000-3000 строк (если больше, разбить на подфайлы)

### Примеры кода

- Все основные концепции должны иметь примеры кода
- Примеры должны быть рабочими и понятными
- Используйте синтаксические блоки с указанием языка
- **СТРОГО ЗАПРЕЩЕНО использовать Python для примеров кода**
- **Использовать только Java + Spring для примеров кода**

### Требования к контенту

- **MongoDB и ClickHouse файлы должны содержать расширенную текстовую информацию**
- **Избегать "воды" - весь контент должен быть полезным и информативным**
- **Каждый концепт должен быть объяснен с практическими примерами**
- **Включать best practices, оптимизации и реальные сценарии использования**

### Оглавление

- Оглавление должно содержать все разделы уровня H2 и важные H3
- Используйте якорные ссылки для навигации
- Порядок в оглавлении должен соответствовать порядку разделов в файле

---

## Новая иерархическая структура (17 разделов)

### 01-fundamentals/ (Фундаментальные концепции)
- `computer-science/` - Computer Science основы
- `programming-basics/` - Базовые концепции программирования
- `networks/` - Компьютерные сети
- `operating-systems/` - Операционные системы

### 02-languages/ (Языки программирования - Backend Focus)
- `java/` - Java (14 файлов, 42k+ строк)
- `kotlin/` - Kotlin (27 файлов, 47k+ строк)
- `scala/` - Scala (49 файлов, 42k+ строк)
- `go/` - Go (32 файла, 42k+ строк)
- `python/` - Python basics (базовый файл)
- `javascript/` - JavaScript basics (базовый файл)
- `rust/` - Структура подготовлена
- `cpp/` - Структура подготовлена

### 03-frameworks/ (Фреймворки - JVM + Kotlin)
- `java-frameworks/` - JVM фреймворки (Spring, Micronaut, Quarkus, Vert.x, etc.)
- `kotlin-frameworks/` - Kotlin фреймворки (Ktor, Exposed)

### 04-databases/ (Базы данных)
- `relational/` - PostgreSQL (21 файл), MySQL (7 файлов)
- `nosql/` - MongoDB (11 файлов), Redis (16 файлов), Cassandra (6), Elasticsearch (6)
- `time-series/` - ClickHouse (9 файлов)
- `graph/` - Структура для графовых БД

### 05-web-development/ (Веб-разработка - Backend Only)
- `backend/` - REST API, GraphQL, gRPC, Serverless
- `tools/` - API инструменты, тестирование

### 07-devops/ (DevOps & Infrastructure)
- `containers/` - Docker, Kubernetes
- `infrastructure-as-code/` - Terraform, Ansible
- `ci-cd/` - CI/CD pipelines
- `cloud-providers/` - AWS, Azure, GCP

### 08-testing/ (Тестирование - 12 инструментов)
- `unit-testing/` - JUnit, Mockito, AssertJ
- `integration-testing/` - Testcontainers, REST Assured
- `ui-testing/` - Selenium
- `performance-testing/` - JMeter, Gatling

### 09-monitoring/ (Мониторинг - 10 инструментов)
- `metrics-collection/` - Prometheus, Micrometer
- `visualization/` - Grafana
- `logging/` - ELK Stack
- `tracing/` - Jaeger, Zipkin, OpenTelemetry
- `alerting/` - Alertmanager

### 10-security/ (Безопасность)
- `application-security/` - Authentication, Authorization
- `infrastructure-security/` - Network, Container, Cloud security
- `security-tools/` - Scanning, Compliance tools

### 12-algorithms/ (Алгоритмы - 100+ файлов)
- `data-structures/` - Структуры данных
- `algorithms/` - Алгоритмы (sorting, searching, dynamic programming)
- `algorithmic-paradigms/` - Парадигмы
- `problem-solving/` - Решение задач

### 13-patterns/ (Паттерны - 25+ файлов)
- `creational/` - Порождающие паттерны
- `structural/` - Структурные паттерны
- `behavioral/` - Поведенческие паттерны
- `concurrency-patterns/` - Параллельные паттерны

### 14-architecture/ (Архитектура)
- `software-architecture/` - Слоистая, гексагональная архитектуры
- `system-design/` - Масштабируемость, надежность
- `design-principles/` - SOLID, DRY, KISS
- `enterprise-patterns/` - DDD, CQRS, Event Sourcing

### 15-interview/ (Подготовка к собеседованиям)
- `programming-languages/` - Вопросы по языкам
- `algorithms-interview/` - Алгоритмические задачи
- `system-design-interview/` - System design вопросы
- `behavioral-interview/` - Поведенческие вопросы

### 16-tools-utilities/ (Инструменты)
- `development-tools/` - IDE, Build tools
- `infrastructure-tools/` - Monitoring, Deployment
- `database-tools/` - DB clients, Migration tools
- `api-tools/` - API clients, Testing tools

### Libraries (38+ файлов - расширены)
- `java-libraries/` - 20+ файлов (Jackson, Lombok, Guava, OpenTelemetry, REST Assured, Vavr)
- `kotlin-libraries/` - 10+ файлов (Arrow, MockK, Konfig)
- `scala-libraries/` - 8 файлов
- `go-libraries/` - 12 файлов

### Linux

**Структура:**
- `process-{topic}.md` - процессы (basics, search, control, monitoring)
- `filesystem-{topic}.md` - файловая система (basics, catalogs, files, links, scripts, advanced)
- `bash-{level}.md` - Bash (basics, advanced)
- `administration/` - подпапка для администрирования
- `file-operations/` - подпапка для операций с файлами
- `network/` - подпапка для сетевых операций

---

## Перекрестные ссылки

### Когда добавлять ссылки "См. также"?

Добавляйте ссылки, если:
- Файлы логически связаны (например, `java-basics.md` и `java-collections-list.md`)
- Один файл расширяет другой (например, `spring-core.md` и `spring-boot.md`)
- Файлы используются вместе (например, `spring-data-jpa.md` и `databases/postgres-design.md`)

### Формат ссылок

Используйте относительные пути:
- `./related-file.md` - файл в той же категории
- `../other-category/file.md` - файл в другой категории

---

## Примеры хорошей структуры

### Пример 1: Java Collections

```
java/
├── README.md
├── java-basics.md
├── java-collections-list.md      ✅ Логично разделено по типу
├── java-collections-set.md
├── java-collections-map.md
├── java-collections-queue.md
└── java-collections-converting.md ✅ Отдельный файл для операций
```

### Пример 2: PostgreSQL

```
databases/
├── README.md
├── postgres-basics.md             ✅ Общие основы
├── postgres-design.md             ✅ Проектирование
├── postgres-structure.md          ✅ DDL
├── postgres-queries.md            ✅ SELECT запросы
├── postgres-joins.md              ✅ JOIN отдельно (много контента)
├── postgres-data-ops.md           ✅ DML
├── postgres-indexes.md            ✅ Индексы
└── redis.md                       ✅ Другие БД отдельно
```

### Пример 3: Linux (с подпапками)

```
linux/
├── README.md
├── process-basics.md
├── filesystem-basics.md
├── bash-basics.md
├── administration/                ✅ Группировка связанных файлов
│   ├── administration-basics.md
│   ├── administration-shell.md
│   └── ...
└── file-operations/               ✅ Группировка операций
    ├── file-operations-basics.md
    └── ...
```

---

## Плохие практики (чего избегать)

### ❌ Избегайте:

1. **Слишком мелкое разбиение:**
   ```
   java-collections-arraylist.md
   java-collections-linkedlist.md
   java-collections-copyonwritearraylist.md
   ```
   ✅ Лучше: `java-collections-list.md` (все списки в одном файле)

2. **Слишком крупное разбиение:**
   ```
   java-all.md  (5000+ строк)
   ```
   ✅ Лучше: разбить на `java-basics.md`, `java-concurrency.md`, и т.д.

3. **Непоследовательное именование:**
   ```
   java-basics.md
   JavaConcurrency.md
   java_collections.md
   ```
   ✅ Лучше: единый стиль `kebab-case`

4. **Отсутствие связи:**
   - Файлы не ссылаются друг на друга
   - Нет README в категориях
   - Нет INDEX.md

---

## Чеклист при создании нового файла (Backend Focus)

- [ ] Имя файла в `kebab-case` согласно новой иерархии
- [ ] Заголовок H1 с названием (на русском языке)
- [ ] Краткое описание (1-2 предложения на русском)
- [ ] Дата последнего обновления (текущая)
- [ ] Секция "Полезные ссылки" (официальная документация)
- [ ] Секция "См. также" с перекрестными ссылками
- [ ] Оглавление с якорными ссылками
- [ ] Минимум 500 строк реального контента (backend темы)
- [ ] Примеры кода на Java + Spring (СТРОГО ЗАПРЕЩЕН Python)
- [ ] Детальные текстовые описания параметров и настроек
- [ ] Best practices для production использования

---

## Обновление существующих файлов (Backend Optimization)

При обновлении файла:
1. Обновите дату последнего обновления
2. Проверьте актуальность ссылок
3. Добавьте детальные текстовые описания (как в Kafka файле)
4. Убедитесь что контент полезный (без "воды")
5. Проверьте соответствие новой иерархии

---

## Backend Development Guidelines

### Языковой стек
- **Основной:** Java, Kotlin, Scala, Go
- **Вспомогательный:** Python, JavaScript (только basics)
- **Исключены:** Frontend frameworks, Mobile development

### Content Quality Standards
- **Текст на русском языке** для всех описаний
- **Детальные объяснения** параметров и настроек
- **Enterprise-grade examples** для production использования
- **Cross-references** между связанными темами
- **Обновления** технологий по мере выхода новых версий

### File Size Guidelines
- **Languages:** 1000-2000 строк на файл
- **Frameworks:** 800-1500 строк на файл
- **Databases:** 800-1500 строк на файл
- **Libraries:** 500-1000 строк на файл
- **DevOps/Infrastructure:** 600-1200 строк на файл

---

*Обновлено: 2026-01-23 | Фокус: Backend разработка | Структура: 17 разделов*
4. Обновите перекрестные ссылки при необходимости
5. Проверьте соответствие этому руководству

---

## Исключения

Некоторые категории могут иметь специфическую структуру:
- **Interview** - вопросы для собеседований, могут иметь другую структуру
- **Algorithms** - алгоритмы, могут группироваться по типам проблем

В таких случаях структура должна быть логичной и последовательной внутри категории.

---

**Это руководство является живым документом и может обновляться по мере развития проекта.**

