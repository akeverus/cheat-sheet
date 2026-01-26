# Тестирование (Testing)

**Комплексные руководства по тестированию: unit, integration, e2e, performance тестирование**

## 📋 Описание

Этот раздел содержит детальные руководства по различным типам тестирования: unit тестирование (JUnit 5, Mockito), integration тестирование (Testcontainers, REST Assured), UI тестирование (Selenium), performance тестирование (JMeter, Gatling).

## 📚 Структура раздела

### 🧪 [Unit Testing](unit-testing/)
**Модульное тестирование**

#### [JUnit](unit-testing/junit/)
- **[JUnit Advanced](unit-testing/junit/junit-advanced.md)** - JUnit 5 продвинутые темы
- **[Mockito Advanced](unit-testing/junit/mockito-advanced.md)** - Mockito продвинутые темы
- **[AssertJ](unit-testing/junit/assertj.md)** - Fluent assertions
- **[Hamcrest](unit-testing/junit/hamcrest.md)** - Matchers

### 🔗 [Integration Testing](integration-testing/)
**Интеграционное тестирование**

- **[REST Assured](integration-testing/rest-assured.md)** - REST API тестирование
- **[WireMock](integration-testing/wiremock.md)** - HTTP mock сервер
- **[Testcontainers](integration-testing/)** - Интеграционное тестирование с контейнерами

### 🖥️ [UI Testing](ui-testing/)
**Тестирование пользовательского интерфейса**

#### [Selenium](ui-testing/selenium/)
- **[Selenium](ui-testing/selenium/selenium.md)** - Selenium WebDriver

### ⚡ [Performance Testing](performance-testing/)
**Тестирование производительности**

#### [JMeter](performance-testing/jmeter/)
- **JMeter** - Apache JMeter

#### [K6](performance-testing/k6/)
- **K6** - K6 нагрузочное тестирование

### 📋 [Other Testing](.)
**Другие типы тестирования**

- **[Cucumber](cucumber.md)** - BDD тестирование

## 🎯 Для кого этот раздел

### QA Engineers
- **Изучение инструментов** - JUnit, Mockito, Selenium
- **Типы тестирования** - unit, integration, e2e
- **Best practices** - лучшие практики тестирования

### Developers
- **Написание тестов** - как писать качественные тесты
- **Test coverage** - покрытие кода тестами
- **TDD/BDD** - Test-Driven Development

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Unit Testing
   ├── JUnit 5 Basics
   ├── Mockito Basics
   └── AssertJ

2. Integration Testing
   ├── REST Assured
   └── Testcontainers

3. Test Strategies
   └── TDD/BDD
```

### Для опытных
```
1. Advanced Testing
   ├── Performance Testing (JMeter, K6)
   ├── UI Testing (Selenium)
   └── BDD (Cucumber)

2. Test Architecture
   ├── Test Pyramid
   ├── Test Coverage
   └── Continuous Testing
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Libraries](../libraries/)** - JUnit 5, Mockito, REST Assured
- **[Frameworks](../frameworks/)** - Spring Testing
- **[Interview](../interview/testing/)** - Вопросы на собеседовании
- **[CI/CD](../ci-cd/)** - Continuous Testing

### Специфичные связи
- **JUnit 5** → [Libraries](../libraries/java-junit5.md)
- **Mockito** → [Libraries](../libraries/java-mockito.md)
- **REST Assured** → [Libraries](../libraries/java-rest-assured.md)
- **Spring Testing** → [Spring](../frameworks/java-frameworks/spring/spring-testing.md)

## 📚 Полезные ресурсы

### Официальная документация
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/)
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Testcontainers Documentation](https://www.testcontainers.org/)

### Учебные материалы
- [Baeldung Testing](https://www.baeldung.com/tag/testing)
- [Martin Fowler - Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)

## 🎯 Следующие шаги

После изучения тестирования:
1. **Изучите библиотеки** → [Libraries](../libraries/)
2. **Освойте CI/CD** → [CI/CD](../ci-cd/)
3. **Подготовьтесь к интервью** → [Interview](../interview/testing/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../monitoring/)

*Обновлено: 2026-01-25*
