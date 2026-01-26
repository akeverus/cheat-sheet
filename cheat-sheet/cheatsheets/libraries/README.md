# Библиотеки (Libraries)

**Enterprise библиотеки для Java, Kotlin, Scala и Go: JSON, тестирование, мониторинг, утилиты**

## 📋 Описание

Этот раздел содержит детальные руководства по популярным библиотекам для backend разработки. Каждая библиотека покрыта с практическими примерами на Java + Spring с подробными комментариями на русском языке.

## 📚 Структура раздела

### ☕ [Java Libraries](.)
**24+ файла** - Библиотеки для Java экосистемы

#### JSON & Serialization
- **[Jackson](jackson.md)** - JSON сериализация/десериализация
- **[Gson](java-gson.md)** - Google Gson библиотека
- **[Protobuf](java-protobuf.md)** - Protocol Buffers

#### Testing
- **[JUnit 5](java-junit5.md)** - Unit тестирование
- **[Mockito](java-mockito.md)** - Mocking фреймворк
- **[REST Assured](java-rest-assured.md)** - REST API тестирование
- **[WireMock](java-wiremock.md)** - HTTP mock сервер
- **[Testcontainers](java-testcontainers.md)** - Интеграционное тестирование

#### Utilities
- **[Lombok](java-lombok.md)** - Уменьшение boilerplate кода
- **[Guava](java-guava.md)** - Google Guava утилиты
- **[Apache Commons](java-apache-commons.md)** - Apache Commons утилиты
- **[Vavr](java-vavr.md)** - Функциональное программирование

#### Mapping & Code Generation
- **[MapStruct](java-mapstruct.md)** - Type-safe маппинг
- **[JOOQ](java-jooq.md)** - Type-safe SQL

#### HTTP & Networking
- **[OkHttp](java-okhttp.md)** - HTTP клиент
- **[Apache HttpClient](java-apache-httpclient.md)** - HTTP клиент
- **[Retrofit](java-retrofit.md)** - REST API клиент

#### Database
- **[HikariCP](java-hikaricp.md)** - Connection pool

#### Office Documents
- **[Apache POI](java-apache-poi.md)** - Excel/Word обработка

#### Resilience & Observability
- **[Resilience4j](java-resilience4j.md)** - Circuit Breaker, Retry, Rate Limiter
- **[Micrometer](java-micrometer.md)** - Метрики
- **[OpenTelemetry](java-opentelemetry.md)** - Distributed tracing
- **[Spring Cloud Sleuth](java-spring-cloud-sleuth.md)** - Tracing для Spring

### 🟢 [Kotlin Libraries](.)
**10 файлов** - Библиотеки для Kotlin

- **[Arrow](kotlin-arrow.md)** - Функциональное программирование
- **[MockK](kotlin-mockk.md)** - Mocking для Kotlin
- **[Kotlinx Coroutines](kotlin-kotlinx-coroutines.md)** - Асинхронность
- **[Kotlinx Serialization](kotlin-kotlinx-serialization.md)** - Сериализация
- **[Kotlinx DateTime](kotlin-kotlinx-datetime.md)** - Работа с датами
- **[Exposed](kotlin-exposed.md)** - SQL фреймворк
- **[Ktor](kotlin-ktor.md)** - HTTP клиент/сервер
- **[Konfig](kotlin-konfig.md)** - Конфигурация
- **[Klaxon](kotlin-klaxon.md)** - JSON парсинг
- **[Kodein](kotlin-kodein.md)** - Dependency Injection

### 🔷 [Scala Libraries](.)
**8 файлов** - Библиотеки для Scala

- **[Akka](scala-akka.md)** - Actor модель
- **[Cats](scala-cats.md)** - Функциональное программирование
- **[ZIO](scala-zio.md)** - Эффекты и асинхронность
- **[ScalaTest](scala-scalatest.md)** - Тестирование
- **[Play](scala-play.md)** - Веб-фреймворк
- **[Slick](scala-slick.md)** - FRM (Functional Relational Mapping)
- **[Doobie](scala-doobie.md)** - Функциональный JDBC
- **[Circe](scala-circe.md)** - JSON библиотека

## 🎯 Для кого этот раздел

### Новички
- **Выбор библиотек** - какие библиотеки использовать
- **Изучение API** - как работать с библиотеками
- **Best practices** - правильное использование

### Опытные разработчики
- **Оптимизация** - производительность библиотек
- **Альтернативы** - сравнение библиотек
- **Продвинутые темы** - расширенные возможности

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Essential Libraries
   ├── Jackson (JSON)
   ├── Lombok (уменьшение кода)
   └── JUnit 5 + Mockito (тестирование)

2. Spring Integration
   ├── Spring Cloud Sleuth (tracing)
   └── Micrometer (метрики)

3. Utilities
   ├── Guava
   └── Apache Commons
```

### Для опытных
```
1. Выбор по задаче
   ├── JSON → Jackson vs Gson
   ├── Testing → JUnit 5 vs TestNG
   ├── HTTP → OkHttp vs Retrofit
   └── Resilience → Resilience4j vs Hystrix

2. Продвинутые темы
   ├── Performance tuning
   ├── Custom configurations
   └── Integration patterns
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Languages](../languages/)** - Языки программирования
- **[Frameworks](../frameworks/)** - Фреймворки
- **[Testing](../testing/)** - Тестирование
- **[Monitoring](../monitoring/)** - Мониторинг и observability

### Специфичные связи
- **Jackson** → [Spring MVC](../frameworks/java-frameworks/spring/spring-mvc.md)
- **JUnit 5 + Mockito** → [Testing](../testing/)
- **Resilience4j** → [Microservices](../architecture/microservices.md)
- **OpenTelemetry** → [Monitoring](../monitoring/tracing/opentelemetry/)

## 📚 Полезные ресурсы

### Официальная документация
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Resilience4j Documentation](https://resilience4j.readme.io/)

### Учебные материалы
- [Baeldung Libraries](https://www.baeldung.com/)
- [Vogella Tutorials](https://www.vogella.com/tutorials/)

## 🎯 Следующие шаги

После изучения библиотек:
1. **Изучите тестирование** → [Testing](../testing/)
2. **Освойте мониторинг** → [Monitoring](../monitoring/)
3. **Изучите паттерны** → [Patterns](../patterns/)
4. **Подготовьтесь к интервью** → [Interview](../interview/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../algorithms/)

*Обновлено: 2026-01-25*
