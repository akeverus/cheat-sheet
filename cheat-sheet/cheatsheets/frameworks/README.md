# Фреймворки (Frameworks)

**Enterprise-grade фреймворки для backend разработки: Spring, Micronaut, Quarkus, Vert.x, Ktor**

## 📋 Описание

Этот раздел содержит комплексные руководства по основным backend фреймворкам для JVM и Go экосистем. Каждый фреймворк покрыт от основ до продвинутых тем с практическими примерами на Java + Spring.

## 📚 Структура раздела

### ☕ [Java Frameworks](java-frameworks/)
**JVM фреймворки для enterprise разработки**

#### 🌱 [Spring](java-frameworks/spring/)
**30+ файлов** - Самый популярный Java фреймворк

- **[Spring Boot](java-frameworks/spring/spring-boot.md)** - Spring Boot основы
- **[Spring Core](java-frameworks/spring/spring-core.md)** - IoC и Dependency Injection
- **[Spring MVC](java-frameworks/spring/spring-mvc.md)** - Web MVC фреймворк
- **[Spring Data JPA](java-frameworks/spring/spring-data-jpa.md)** - Работа с БД через JPA
- **[Spring Security](java-frameworks/spring/spring-security.md)** - Безопасность
- **[Spring WebFlux](java-frameworks/spring/spring-webflux.md)** - Реактивное программирование
- **[Spring Cloud](java-frameworks/spring/spring-cloud.md)** - Микросервисы
- **[Spring Kafka](java-frameworks/spring/spring-kafka.md)** - Apache Kafka интеграция
- **[Spring Redis](java-frameworks/spring/spring-redis.md)** - Redis интеграция
- **[Spring MongoDB](java-frameworks/spring/spring-mongodb.md)** - MongoDB интеграция

#### ⚡ [Micronaut](java-frameworks/micronaut/)
**29 файлов** - Современный реактивный фреймворк

- **[Micronaut Basics](java-frameworks/micronaut/micronaut-basics.md)** - Основы Micronaut
- **[Micronaut HTTP](java-frameworks/micronaut/micronaut-http.md)** - HTTP сервер
- **[Micronaut Data](java-frameworks/micronaut/micronaut-data.md)** - Работа с БД
- **[Micronaut Kafka](java-frameworks/micronaut/micronaut-kafka.md)** - Kafka интеграция

#### 🚀 [Quarkus](java-frameworks/quarkus/)
**23 файла** - Supersonic Subatomic Java

- **[Quarkus Basics](java-frameworks/quarkus/quarkus-basics.md)** - Основы Quarkus
- **[Quarkus REST](java-frameworks/quarkus/quarkus-rest.md)** - REST API
- **[Quarkus Reactive](java-frameworks/quarkus/quarkus-reactive.md)** - Реактивное программирование
- **[Quarkus Data](java-frameworks/quarkus/quarkus-data.md)** - Работа с БД

#### 📦 [Other JVM Frameworks](java-frameworks/)
- **[Vert.x](java-frameworks/vertx/vertx-basics.md)** - Реактивный toolkit
- **[Dropwizard](java-frameworks/dropwizard/dropwizard-basics.md)** - Микросервисы
- **[Javalin](java-frameworks/javalin/javalin-basics.md)** - Легковесный веб-фреймворк

### 🟢 [Kotlin Frameworks](kotlin-frameworks/)
**Kotlin-специфичные фреймворки**

- **[Ktor](kotlin-frameworks/)** - Асинхронный веб-фреймворк
- **[Exposed](kotlin-frameworks/)** - SQL фреймворк

## 🎯 Для кого этот раздел

### Новички
- **Изучение Spring Boot** - самый популярный фреймворк
- **Понимание dependency injection** - IoC контейнеры
- **Создание REST API** - веб-разработка

### Опытные разработчики
- **Выбор фреймворка** - сравнение Spring, Micronaut, Quarkus
- **Продвинутые темы** - реактивное программирование, микросервисы
- **Оптимизация производительности** - best practices

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Spring Boot Basics
   ├── Создание проекта
   ├── Dependency Injection
   └── REST API

2. Spring Data JPA
   ├── Работа с БД
   └── Репозитории

3. Spring Security
   ├── Аутентификация
   └── Авторизация
```

### Для опытных
```
1. Выбор фреймворка
   ├── Spring → Enterprise, большая экосистема
   ├── Micronaut → Cloud Native, быстрый старт
   └── Quarkus → GraalVM, низкое потребление памяти

2. Продвинутые темы
   ├── Reactive Programming (WebFlux)
   ├── Microservices (Spring Cloud)
   └── Performance Tuning
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Languages](../languages/)** - Языки программирования
- **[Databases](../databases/)** - Базы данных
- **[Libraries](../libraries/)** - Библиотеки
- **[Interview](../interview/frameworks/)** - Вопросы на собеседовании
- **[Architecture](../architecture/)** - Архитектурные паттерны

### Специфичные связи
- **Spring** → [Java](../languages/java/), [PostgreSQL](../databases/relational/postgresql/)
- **Micronaut** → [Java](../languages/java/), [MongoDB](../databases/nosql/mongodb/)
- **Quarkus** → [Java](../languages/java/), [Redis](../databases/nosql/redis/)
- **Ktor** → [Kotlin](../languages/kotlin/)

## 📚 Полезные ресурсы

### Официальная документация
- [Spring Documentation](https://spring.io/docs)
- [Micronaut Documentation](https://docs.micronaut.io/)
- [Quarkus Documentation](https://quarkus.io/guides/)
- [Vert.x Documentation](https://vertx.io/docs/)

### Учебные материалы
- [Spring Boot Guides](https://spring.io/guides)
- [Baeldung Spring](https://www.baeldung.com/spring-tutorial)
- [Micronaut Guides](https://guides.micronaut.io/)

## 🎯 Следующие шаги

После изучения фреймворков:
1. **Изучите базы данных** → [Databases](../databases/)
2. **Освойте тестирование** → [Testing](../testing/)
3. **Изучите мониторинг** → [Monitoring](../monitoring/)
4. **Подготовьтесь к интервью** → [Interview](../interview/frameworks/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../databases/)

*Обновлено: 2026-01-25*
