# Вопросы на собеседовании: Стратегии тестирования

**Комплексное руководство по вопросам собеседования на тему Стратегии тестирования для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


Стратегии тестирования определяют эффективность и качество процесса разработки. Senior Java Developer должен понимать различные подходы к тестированию и уметь выбирать оптимальную стратегию для конкретного проекта.Дата последнего обновления: 2026-01-24

## Полезные ссылки

### Официальная документация
- [Microsoft Testing Strategy](https://docs.microsoft.com/en-us/devops/develop/strategy/testing-strategy)
- [Google Testing Blog](https://testing.googleblog.com/)

### Книги
- "Agile Testing" by Lisa Crispin and Janet Gregory
- "The Art of Software Testing" by Glenford Myers
- "Lessons Learned in Software Testing" by Cem Kaner, James Bach, Bret Pettichord

### См. также
- `unit-testing-interview.md` - Unit тестирование
- `integration-testing-interview.md` - Интеграционное тестирование
- `test-automation-interview.md` - Автоматизация тестирования

## Содержание

- [Q1. Что такое стратегия тестирования и зачем она нужна?](#q1-что-такое-стратегия-тестирования-и-зачем-она-нужна)
- [Q2. Что такое Testing Pyramid и как ее применять?](#q2-что-такое-testing-pyramid-и-как-ее-применять)
- [Q3. Какие существуют Testing Quadrants?](#q3-какие-существуют-testing-quadrants)
- [Q4. Что такое Shift-Left Testing?](#q4-что-такое-shift-left-testing)
- [Q5. Как реализовать Risk-Based Testing?](#q5-как-реализовать-risk-based-testing)
- [Q6. Какие стратегии тестирования микросервисов?](#q6-какие-стратегии-тестирования-микросервисов)
- [Q7. Как тестировать legacy код?](#q7-как-тестировать-legacy-код)
- [Q8. Что такое TDD, BDD и ATDD?](#q8-что-такое-tdd-bdd-и-atdd)
- [Q9. Как организовать тестирование в agile команде?](#q9-как-организовать-тестирование-в-agile-команде)
- [Q10. Какие метрики качества тестирования?](#q10-какие-метрики-качества-тестирования)

## Q1. Что такое стратегия тестирования и зачем она нужна?

Стратегия тестирования — это план действий, определяющий подход к тестированию программного обеспечения, включая выбор типов тестов, их приоритизацию, распределение ресурсов и критерии завершения тестирования.

### Компоненты стратегии тестирования

#### 1. Область тестирования (Test Scope)
- **Что тестировать: Функциональность, производительность, безопасность
- **Что не тестировать: Устаревшие функции, низкорисковые компоненты
- **Глубина тестирования: Полное покрытие vs sampling

#### 2. Типы тестов (Test Types)
- **Функциональное тестирование: Unit, Integration, System, Acceptance
- **Нефункциональное тестирование: Performance, Security, Usability, Compatibility
- **Регрессионное тестирование: Проверка существующих функций

#### 3. Ресурсы и расписание (Resources & Schedule)
- **Команда: Разработчики, QA инженеры, бизнес-аналитики
- **Инструменты: Фреймворки, CI/CD, monitoring tools
- **Время: Когда начинать тестирование, сроки завершения

#### 4. Критерии завершения (Exit Criteria)
- **Покрытие кода: > 80% для unit тестов
- **Качество: < 5 критических багов
- **Производительность: Все SLA соблюдены

### Пример стратегии тестирования

```yaml
# Testing Strategy Document
project: E-commerce Platform
version: 1.0
date: 2024-01-24

## Objectives
- Ensure high-quality user experience
- Prevent production issues
- Enable fast and reliable deployments

## Scope
### In Scope
- User registration and authentication
- Product catalog and search
- Shopping cart and checkout
- Payment processing
- Order management

### Out of Scope
- Third-party integrations (tested separately)
- Mobile applications (separate strategy)
- Performance under extreme load (> 10k users)

## Test Types & Priorities

### Critical Path (High Priority)
1. User authentication flow
2. Payment processing
3. Order creation and fulfillment

### Secondary Features (Medium Priority)
1. Product search and filtering
2. User profile management
3. Wishlist functionality

### Nice-to-Have (Low Priority)
1. Product recommendations
2. Social features
3. Advanced reporting

## Test Levels

### Unit Tests
- Coverage: > 85%
- Framework: JUnit 5 + Mockito
- Responsibility: Developers

### Integration Tests
- Coverage: All critical APIs
- Framework: Spring Boot Test + TestContainers
- Responsibility: Developers

### System Tests
- Coverage: End-to-end user journeys
- Framework: Selenium/Cypress
- Responsibility: QA Team

### Performance Tests
- Load: 1000 concurrent users
- Response time: < 2 seconds for 95% requests
- Framework: JMeter/Gatling
- Responsibility: Performance Team

## Entry/Exit Criteria

### Entry Criteria
- Code review completed
- Unit tests passing
- Build successful

### Exit Criteria
- All high-priority tests passing
- No critical or high-severity bugs
- Performance requirements met
- Security scan passed

## Risk Mitigation
- Automated regression tests for critical features
- Performance monitoring in production
- Feature flags for gradual rollouts
- Rollback plan for failed deployments

## Metrics
- Test coverage: > 85%
- Defect density: < 0.5 per 1000 LOC
- Mean time to detect: < 1 hour
- Mean time to resolve: < 4 hours
```

## Q2. Что такое Testing Pyramid и как ее применять?

Testing Pyramid — это концептуальная модель, описывающая оптимальное распределение различных типов тестов в проекте по количеству, скорости выполнения и стоимости.

### Структура Testing Pyramid

```
 E2E Tests (UI)
 │
 ┌────────┴────────┐
 │ │
 Integration Tests Contract Tests
 │ │
 ┌───────┴───────┐ │
 │ │ │
 Component Tests API Tests │
 │ │ │
┌───────┴───────┐ ┌─────┴─────┐ │
│ │ │ │ │
Unit Tests Unit Tests Unit Tests │
│ │ │ │ │
└──────────────┘ └───────────┘ │
 │
 ┌────────────┴────────────┐
 │ │
 Static Analysis Exploratory Testing
```

### Характеристики уровней

#### Unit Tests (Нижний уровень)
- **Количество: 70-80% всех тестов
- **Скорость: Быстрые (миллисекунды)
- **Стоимость: Низкая
- **Надежность: Высокая
- **Обнаруживаемые ошибки: Логика, алгоритмы, edge cases

#### Integration Tests (Средний уровень)
- **Количество: 15-20% всех тестов
- **Скорость: Средняя (секунды)
- **Стоимость: Средняя
- **Надежность: Средняя
- **Обнаруживаемые ошибки: Взаимодействие компонентов, контракты

#### End-to-End Tests (Верхний уровень)
- **Количество: 5-10% всех тестов
- **Скорость: Медленные (минуты/часы)
- **Стоимость: Высокая
- **Надежность: Низкая (хрупкие)
- **Обнаруживаемые ошибки: Полный пользовательский опыт, системная интеграция

### Применение Testing Pyramid

#### 1. Определение пропорций

```java
public class TestingStrategy {
 
 public void validateTestDistribution(TestSuite suite) {
 int totalTests = suite.getTotalTestCount();
 
 // Unit tests: 70-80%
 int unitTests = suite.getUnitTestCount();
 double unitTestRatio = (double) unitTests / totalTests;
 assertTrue(unitTestRatio >= 0.7 && unitTestRatio <= 0.8, 
 "Unit tests should be 70-80% of total tests");
 
 // Integration tests: 15-20%
 int integrationTests = suite.getIntegrationTestCount();
 double integrationTestRatio = (double) integrationTests / totalTests;
 assertTrue(integrationTestRatio >= 0.15 && integrationTestRatio <= 0.2,
 "Integration tests should be 15-20% of total tests");
 
 // E2E tests: 5-10%
 int e2eTests = suite.getE2eTestCount();
 double e2eTestRatio = (double) e2eTests / totalTests;
 assertTrue(e2eTestRatio >= 0.05 && e2eTestRatio <= 0.1,
 "E2E tests should be 5-10% of total tests");
 }
}
```

#### 2. Оптимизация пирамиды

```yaml
# CI/CD Pipeline с учетом Testing Pyramid
stages:
 - build
 - test
 - deploy

build:
 stage: build
 script:
 - mvn compile

test:unit:
 stage: test
 script:
 - mvn test -Dtest="*Test" -DfailIfNoTests=false
 artifacts:
 reports:
 junit: target/surefire-reports/*.xml
 allow_failure: false

test:integration:
 stage: test
 script:
 - mvn verify -Dtest="*IT" -DfailIfNoTests=false
 artifacts:
 reports:
 junit: target/failsafe-reports/*.xml
 dependencies:
 - build
 allow_failure: false

test:e2e:
 stage: test
 script:
 - mvn test -Dtest="*E2ETest" -DfailIfNoTests=false
 artifacts:
 reports:
 junit: target/e2e-reports/*.xml
 dependencies:
 - test:integration
 allow_failure: true # E2E могут быть нестабильными

deploy:staging:
 stage: deploy
 script:
 - deploy-to-staging
 dependencies:
 - test:e2e
 only:
 - develop
```

#### 3. Мониторинг распределения тестов

```java
@Component
public class TestMetricsCollector {
 
 private final MeterRegistry meterRegistry;
 
 public void collectTestMetrics(TestExecutionResult result) {
 // Unit tests
 meterRegistry.counter("tests.unit.total").increment(result.getUnitTestCount());
 meterRegistry.counter("tests.unit.failed").increment(result.getUnitTestFailures());
 
 // Integration tests
 meterRegistry.counter("tests.integration.total").increment(result.getIntegrationTestCount());
 meterRegistry.counter("tests.integration.failed").increment(result.getIntegrationTestFailures());
 
 // E2E tests
 meterRegistry.counter("tests.e2e.total").increment(result.getE2eTestCount());
 meterRegistry.counter("tests.e2e.failed").increment(result.getE2eTestFailures());
 
 // Coverage
 meterRegistry.gauge("tests.coverage.unit", result.getUnitTestCoverage());
 meterRegistry.gauge("tests.coverage.integration", result.getIntegrationTestCoverage());
 
 // Performance
 meterRegistry.timer("tests.execution.unit").record(result.getUnitTestDuration(), TimeUnit.MILLISECONDS);
 meterRegistry.timer("tests.execution.integration").record(result.getIntegrationTestDuration(), TimeUnit.MILLISECONDS);
 meterRegistry.timer("tests.execution.e2e").record(result.getE2eTestDuration(), TimeUnit.MILLISECONDS);
 }
}
```

## Q3. Какие существуют Testing Quadrants?

Testing Quadrants — это модель Brainstorming Workshop, разделяющая тесты по двум измерениям: поддержка принятия решений vs. критика продукта, и технология vs. бизнес.

### Quadrant 1: Technology facing tests that support the team (Q1)

**Характеристики:
- **Цель: Поддержка разработки
- **Аудитория: Разработчики
- **Подход: Автоматизированные тесты
- **Фокус: Качество кода

**Типы тестов:
- Unit tests
- Component tests
- Integration tests
- API tests
- Contract tests

**Пример:
```java
// Q1: Unit test supporting development
@Test
void shouldCalculateTotalPrice() {
 // Arrange
 ShoppingCart cart = new ShoppingCart();
 cart.addItem(new Item("Book", BigDecimal.valueOf(10.00)));
 cart.addItem(new Item("Pen", BigDecimal.valueOf(2.50)));
 
 // Act
 BigDecimal total = cart.calculateTotal();
 
 // Assert
 assertEquals(BigDecimal.valueOf(12.50), total);
}
```

### Quadrant 2: Business facing tests that support the team (Q2)

**Характеристики:
- **Цель: Поддержка функциональных требований
- **Аудитория: Разработчики, бизнес-аналитики
- **Подход: Автоматизированные тесты
- **Фокус: Бизнес-логика

**Типы тестов:
- Acceptance tests
- Functional tests
- Story tests
- Prototyping

**Пример:
```gherkin
# Q2: BDD scenario supporting business requirements
Feature: User Registration
 As a new user
 I want to register on the website
 So that I can access my account

 Scenario: Successful registration
 Given I am on the registration page
 When I enter valid registration details
 And I submit the form
 Then I should be redirected to my dashboard
 And I should receive a welcome email
```

### Quadrant 3: Business facing tests that critique the product (Q3)

**Характеристики:
- **Цель: Критика продукта с точки зрения бизнеса
- **Аудитория: Бизнес-пользователи, QA
- **Подход: Ручное тестирование
- **Фокус: Пользовательский опыт

**Типы тестов:
- Exploratory testing
- Usability testing
- User acceptance testing (UAT)
- Beta testing
- A/B testing

**Пример:
```java
// Q3: Exploratory testing session
public class ExploratoryTestChecklist {
 
 public static final List<String> CHECKLIST = Arrays.asList(
 "Can users register with invalid email addresses?",
 "Does the search work with special characters?",
 "How does the app behave with slow internet?",
 "Are error messages user-friendly?",
 "Does the checkout flow work on mobile devices?",
 "Can users recover forgotten passwords easily?",
 "How does the app handle concurrent users?"
 );
 
 public void performExploratoryTesting(WebDriver driver) {
 for (String scenario: CHECKLIST) {
 System.out.println("Testing: " + scenario);
 // Manual testing steps...
 }
 }
}
```

### Quadrant 4: Technology facing tests that critique the product (Q4)

**Характеристики:
- **Цель: Критика продукта с технической точки зрения
- **Аудитория: Технические эксперты
- **Подход: Автоматизированные и ручные тесты
- **Фокус: Нефункциональные требования

**Типы тестов:
- Performance testing
- Load testing
- Stress testing
- Security testing
- Compatibility testing
- Reliability testing

**Пример:
```java
// Q4: Performance test critiquing the product
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @Test
 void shouldHandleConcurrentRequests() {
 int concurrentUsers = 100;
 int requestsPerUser = 10;
 
 // Performance requirements
 long maxResponseTime = 2000; // 2 seconds
 double percentile95 = 0.95;
 
 // Load test execution
 LoadTestResult result = performLoadTest(concurrentUsers, requestsPerUser);
 
 // Assertions
 assertTrue(result.getAverageResponseTime() < maxResponseTime,
 "Average response time should be < 2000ms");
 
 assertTrue(result.get95thPercentile() < maxResponseTime,
 "95th percentile should be < 2000ms");
 
 assertEquals(0, result.getErrorCount(),
 "Should have no errors under load");
 }
 
 private LoadTestResult performLoadTest(int users, int requests) {
 // Implementation using JMeter or similar tool
 return loadTestExecutor.execute(users, requests);
 }
}
```

### Применение Testing Quadrants

#### 1. Agile проект

```
Sprint Planning:
├── Q1: Unit tests (разработчики)
├── Q2: Acceptance tests (разработчики + бизнес)
├── Q3: Exploratory testing (QA + бизнес)
└── Q4: Performance testing (техническая команда)

Daily Activities:
├── Q1: TDD, continuous integration
├── Q2: BDD, acceptance test driven development
├── Q3: User story testing, usability evaluation
└── Q4: Security scanning, performance monitoring
```

#### 2. Баланс между квадрантами

```java
public class TestingStrategyEvaluator {
 
 public TestingStrategyBalance evaluateBalance(TestMetrics metrics) {
 double q1Percentage = metrics.getQ1TestPercentage();
 double q2Percentage = metrics.getQ2TestPercentage();
 double q3Percentage = metrics.getQ3TestPercentage();
 double q4Percentage = metrics.getQ4TestPercentage();
 
 TestingStrategyBalance balance = new TestingStrategyBalance();
 
 // Идеальный баланс для большинства проектов
 if (q1Percentage >= 40 && q1Percentage <= 60) {
 balance.setQ1Status("Good");
 }
 
 if (q2Percentage >= 20 && q2Percentage <= 30) {
 balance.setQ2Status("Good");
 }
 
 if (q3Percentage >= 15 && q3Percentage <= 25) {
 balance.setQ3Status("Good");
 }
 
 if (q4Percentage >= 5 && q4Percentage <= 15) {
 balance.setQ4Status("Good");
 }
 
 return balance;
 }
 
 public List<String> getRecommendations(TestingStrategyBalance balance) {
 List<String> recommendations = new ArrayList<>();
 
 if ("Low".equals(balance.getQ1Status())) {
 recommendations.add("Increase unit test coverage and automated testing");
 }
 
 if ("Low".equals(balance.getQ2Status())) {
 recommendations.add("Add more acceptance and functional tests");
 }
 
 if ("Low".equals(balance.getQ3Status())) {
 recommendations.add("Include more exploratory and usability testing");
 }
 
 if ("Low".equals(balance.getQ4Status())) {
 recommendations.add("Add performance, security, and reliability testing");
 }
 
 return recommendations;
 }
}
```

## Q4. Что такое Shift-Left Testing?

Shift-Left Testing — это подход к тестированию, при котором тестирование начинается как можно раньше в жизненном цикле разработки, а не откладывается на поздние стадии.

### Принципы Shift-Left Testing

#### 1. Раннее вовлечение тестирования

**Традиционный подход:
```
Planning → Design → Development → Testing → Deployment
```

**Shift-Left подход:
```
Planning → Design → Development + Testing → Testing → Deployment
```

#### 2. Prevention over Detection

**Традиционный подход: Находим и исправляем ошибки
**Shift-Left подход: Предотвращаем ошибки

#### 3. Collaborative Testing

Вовлечение всех участников команды в процесс тестирования.

### Реализация Shift-Left Testing

#### 1. Test-First Development

```java
// TDD: Пишем тест перед кодом
@Test
void shouldValidateEmailFormat() {
 EmailValidator validator = new EmailValidator();
 
 assertTrue(validator.isValid("user@example.com"));
 assertFalse(validator.isValid("invalid-email"));
 assertFalse(validator.isValid(""));
 assertFalse(validator.isValid(null));
}

// Затем реализуем код
public class EmailValidator {
 private static final Pattern EMAIL_PATTERN = 
 Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
 
 public boolean isValid(String email) {
 return email!= null && EMAIL_PATTERN.matcher(email).matches();
 }
}
```

#### 2. Continuous Testing в CI/CD

```yaml
#.github/workflows/shift-left.yml
name: Shift-Left Testing Pipeline

on:
 push:
 branches: [ main, develop ]
 pull_request:
 branches: [ main ]

jobs:
 static-analysis:
 runs-on: ubuntu-latest
 steps:
 - uses: actions/checkout@v3
 - name: Run SonarQube
 uses: sonarsource/sonarqube-scan-action@v1
 env:
 SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
 
 unit-tests:
 runs-on: ubuntu-latest
 needs: static-analysis
 steps:
 - uses: actions/checkout@v3
 - name: Run unit tests
 run: mvn test
 - name: Publish test results
 uses: actions/upload-artifact@v3
 with:
 name: unit-test-results
 path: target/surefire-reports/
 
 integration-tests:
 runs-on: ubuntu-latest
 needs: unit-tests
 services:
 postgres:
 image: postgres:13
 env:
 POSTGRES_PASSWORD: postgres
 ports:
 - 5432:5432
 steps:
 - uses: actions/checkout@v3
 - name: Run integration tests
 run: mvn verify -Dspring.profiles.active=test
 env:
 DATABASE_URL: jdbc:postgresql://localhost:5432/testdb
 
 security-tests:
 runs-on: ubuntu-latest
 needs: integration-tests
 steps:
 - uses: actions/checkout@v3
 - name: Run OWASP ZAP security scan
 uses: zaproxy/action-baseline@v0.7.0
 with:
 target: 'http://localhost:8080'
 
 performance-tests:
 runs-on: ubuntu-latest
 needs: security-tests
 steps:
 - uses: actions/checkout@v3
 - name: Run JMeter performance tests
 run: |
 jmeter -n -t performance-test.jmx -l results.jtl
 # Analyze results...
```

#### 3. Code Reviews с фокусом на тестируемость

```java
// Пример code review checklist для тестируемости
public class TestabilityChecklist {
 
 public static final List<String> CHECKLIST = Arrays.asList(
 "Класс имеет dependency injection для mocking?",
 "Методы имеют четкие входы и выходы?",
 "Обработка ошибок позволяет тестирование исключений?",
 "Статические методы минимизированы?",
 "Класс следует Single Responsibility Principle?",
 "Интерфейсы определены для внешних зависимостей?",
 "Методы имеют предсказуемое поведение?",
 "Side effects изолированы?"
 );
 
 public CodeReviewResult reviewForTestability(Class<?> clazz) {
 CodeReviewResult result = new CodeReviewResult();
 
 for (String item: CHECKLIST) {
 boolean passed = evaluateChecklistItem(clazz, item);
 result.addItem(item, passed);
 }
 
 return result;
 }
 
 private boolean evaluateChecklistItem(Class<?> clazz, String item) {
 // Implementation of automated checks
 return testabilityAnalyzer.analyze(clazz, item);
 }
}
```

#### 4. Pair Programming с тестированием

```java
// Pair programming session: Developer + Tester
public class PairTestingSession {
 
 private final Developer developer;
 private final Tester tester;
 
 public void performPairTesting(UserStory story) {
 // 1. Обсудить требования
 discussRequirements(story);
 
 // 2. Написать acceptance tests вместе
 writeAcceptanceTests(story);
 
 // 3. Реализовать код
 implementCode(story);
 
 // 4. Написать unit tests
 writeUnitTests(story);
 
 // 5. Выполнить exploratory testing
 performExploratoryTesting(story);
 
 // 6. Провести code review
 performCodeReview(story);
 }
 
 private void discussRequirements(UserStory story) {
 // Developer и Tester обсуждают acceptance criteria
 // Определяют edge cases и error scenarios
 // Согласовывают понимание требований
 }
 
 private void writeAcceptanceTests(UserStory story) {
 // Пишем BDD сценарии вместе
 // Определяем Given-When-Then
 // Создаем автоматизированные acceptance tests
 }
}
```

### Преимущества Shift-Left Testing

1. Раннее обнаружение ошибок: Снижение стоимости исправления
2. Улучшение качества кода: Лучшая архитектура и дизайн
3. Сокращение time-to-market: Быстрая обратная связь
4. Снижение технического долга: Регулярное улучшение кода
5. Повышение командной эффективности: Коллаборативная разработка

## Q5. Как реализовать Risk-Based Testing?

Risk-Based Testing (RBT) — это подход к тестированию, при котором приоритизация тестов основана на оценке рисков проекта.

### Шаги Risk-Based Testing

#### 1. Идентификация рисков

```java
public class RiskIdentifier {
 
 public List<Risk> identifyRisks(Project project) {
 List<Risk> risks = new ArrayList<>();
 
 // Функциональные риски
 risks.addAll(identifyFunctionalRisks(project));
 
 // Технические риски
 risks.addAll(identifyTechnicalRisks(project));
 
 // Бизнес-риски
 risks.addAll(identifyBusinessRisks(project));
 
 // Операционные риски
 risks.addAll(identifyOperationalRisks(project));
 
 return risks;
 }
 
 private List<Risk> identifyFunctionalRisks(Project project) {
 List<Risk> risks = new ArrayList<>();
 
 // Критические бизнес-функции
 if (project.hasPaymentProcessing()) {
 risks.add(new Risk("PAYMENT_FAILURE", RiskLevel.CRITICAL, 
 "Payment processing failure could result in financial loss"));
 }
 
 // Сложные алгоритмы
 if (project.hasComplexCalculations()) {
 risks.add(new Risk("CALCULATION_ERROR", RiskLevel.HIGH,
 "Mathematical errors in calculations"));
 }
 
 return risks;
 }
 
 private List<Risk> identifyTechnicalRisks(Project project) {
 List<Risk> risks = new ArrayList<>();
 
 // Новые технологии
 if (project.usesNewTechnologies()) {
 risks.add(new Risk("TECH_UNKNOWN", RiskLevel.HIGH,
 "Unfamiliar technology stack increases risk"));
 }
 
 // Legacy integration
 if (project.integratesWithLegacy()) {
 risks.add(new Risk("LEGACY_INTEGRATION", RiskLevel.MEDIUM,
 "Integration with legacy systems may be unstable"));
 }
 
 return risks;
 }
}
```

#### 2. Оценка рисков

```java
public class RiskAssessor {
 
 public RiskAssessment assessRisk(Risk risk) {
 int likelihood = calculateLikelihood(risk);
 int impact = calculateImpact(risk);
 int riskScore = likelihood * impact;
 
 RiskPriority priority = determinePriority(riskScore);
 
 return new RiskAssessment(risk, likelihood, impact, riskScore, priority);
 }
 
 private int calculateLikelihood(Risk risk) {
 // Факторы likelihood:
 // - Complexity of feature
 // - Experience of team
 // - Historical defect rate
 // - External dependencies
 
 int score = 1; // Base score
 
 if (risk.getComplexity() == Complexity.HIGH) score += 2;
 if (risk.getTeamExperience() == Experience.LOW) score += 2;
 if (risk.getHistoricalDefects() > 10) score += 1;
 if (risk.hasExternalDependencies()) score += 1;
 
 return Math.min(score, 5); // Max 5
 }
 
 private int calculateImpact(Risk risk) {
 // Факторы impact:
 // - Financial loss
 // - User experience
 // - Reputation damage
 // - Legal compliance
 
 int score = 1; // Base score
 
 if (risk.causesFinancialLoss()) score += 2;
 if (risk.affectsUserExperience()) score += 1;
 if (risk.damagesReputation()) score += 2;
 if (risk.violatesCompliance()) score += 3;
 
 return Math.min(score, 5); // Max 5
 }
 
 private RiskPriority determinePriority(int riskScore) {
 if (riskScore >= 15) return RiskPriority.CRITICAL;
 if (riskScore >= 10) return RiskPriority.HIGH;
 if (riskScore >= 6) return RiskPriority.MEDIUM;
 return RiskPriority.LOW;
 }
}
```

#### 3. Приоритизация тестов

```java
public class TestPrioritizer {
 
 public List<TestCase> prioritizeTests(List<RiskAssessment> riskAssessments, 
 List<TestCase> availableTests) {
 
 // Группируем тесты по рискам
 Map<Risk, List<TestCase>> testsByRisk = groupTestsByRisk(availableTests);
 
 // Сортируем риски по приоритету
 List<RiskAssessment> sortedRisks = riskAssessments.stream().sorted((a, b) -> b.getPriority().compareTo(a.getPriority())).collect(Collectors.toList());
 
 // Формируем приоритизированный список тестов
 List<TestCase> prioritizedTests = new ArrayList<>();
 
 for (RiskAssessment assessment: sortedRisks) {
 List<TestCase> riskTests = testsByRisk.get(assessment.getRisk());
 if (riskTests!= null) {
 // Сортируем тесты внутри риска
 riskTests.sort((a, b) -> Double.compare(b.getCoverage(), a.getCoverage()));
 prioritizedTests.addAll(riskTests);
 }
 }
 
 return prioritizedTests;
 }
 
 private Map<Risk, List<TestCase>> groupTestsByRisk(List<TestCase> tests) {
 return tests.stream().collect(Collectors.groupingBy(TestCase::getAssociatedRisk));
 }
}
```

#### 4. Планирование тестирования на основе рисков

```java
public class RiskBasedTestPlan {
 
 private final List<RiskAssessment> riskAssessments;
 private final TestResources resources;
 
 public TestPlan generatePlan() {
 TestPlan plan = new TestPlan();
 
 // Распределяем ресурсы по приоритетам
 distributeResourcesByPriority(plan);
 
 // Определяем exit criteria
 defineExitCriteria(plan);
 
 // Создаем contingency plans
 createContingencyPlans(plan);
 
 return plan;
 }
 
 private void distributeResourcesByPriority(TestPlan plan) {
 long criticalRisks = riskAssessments.stream().filter(ra -> ra.getPriority() == RiskPriority.CRITICAL).count();
 
 long highRisks = riskAssessments.stream().filter(ra -> ra.getPriority() == RiskPriority.HIGH).count();
 
 // Распределяем время и ресурсы
 plan.setCriticalPhaseDuration(Duration.ofDays(criticalRisks * 2));
 plan.setHighPriorityPhaseDuration(Duration.ofDays(highRisks * 1));
 plan.setExploratoryPhaseDuration(Duration.ofDays(3));
 
 // Назначаем team members
 plan.setCriticalPhaseTeam(resources.getSeniorTesters());
 plan.setHighPriorityPhaseTeam(resources.getAllTesters());
 plan.setExploratoryPhaseTeam(resources.getExploratoryTesters());
 }
 
 private void defineExitCriteria(TestPlan plan) {
 Map<RiskPriority, Double> coverageRequirements = Map.of(
 RiskPriority.CRITICAL, 0.95, // 95% coverage for critical risks
 RiskPriority.HIGH, 0.85, // 85% coverage for high risks
 RiskPriority.MEDIUM, 0.70, // 70% coverage for medium risks
 RiskPriority.LOW, 0.50 // 50% coverage for low risks
 );
 
 plan.setCoverageRequirements(coverageRequirements);
 plan.setMaxDefectsByPriority(Map.of(
 RiskPriority.CRITICAL, 0, // Zero critical defects
 RiskPriority.HIGH, 2, // Max 2 high severity defects
 RiskPriority.MEDIUM, 5 // Max 5 medium severity defects
 ));
 }
 
 private void createContingencyPlans(TestPlan plan) {
 // Plan for when critical risks are found
 plan.addContingencyPlan(RiskPriority.CRITICAL, 
 "Stop release, fix all critical issues, re-test completely");
 
 // Plan for high risk issues
 plan.addContingencyPlan(RiskPriority.HIGH,
 "Schedule emergency fix, perform targeted regression testing");
 
 // Plan for resource shortages
 plan.addContingencyPlan("RESOURCE_SHORTAGE",
 "Prioritize critical and high-risk testing, reduce medium/low coverage");
 }
}
```

### Преимущества Risk-Based Testing

1. Фокус на важном: Тестирование наиболее критичных областей
2. Эффективное использование ресурсов: Оптимальное распределение времени и усилий
3. Раннее обнаружение проблем: Приоритизация тестирования высокорисковых областей
4. Обоснованные решения: Метрики для принятия решений о выпуске

## Q6. Какие стратегии тестирования микросервисов?

### 1. Consumer-Driven Contract Testing

```java
// Provider Service (User Service)
@RestController
@RequestMapping("/api/users")
public class UserController {
 
 @GetMapping("/{id}")
 public User getUser(@PathVariable Long id) {
 return userService.findById(id);
 }
}

// Consumer Service (Order Service)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8081")
public class OrderServiceContractTest {
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact validUserExists(PactDslWithProvider builder) {
 return builder.given("User with id 123 exists").uponReceiving("A request for user 123").path("/api/users/123").method("GET").willRespondWith().status(200).body(new PactDslJsonBody().numberType("id", 123).stringType("name", "John Doe").stringType("email", "john@example.com")).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "validUserExists")
 void shouldCreateOrderForValidUser(MockServer mockServer) {
 UserClient userClient = new UserClient(mockServer.getUrl());
 OrderService orderService = new OrderService(userClient);
 
 Order order = orderService.createOrder(123L, productId, 1);
 
 assertNotNull(order);
 assertEquals(123L, order.getUserId());
 }
}
```

### 2. Service Virtualization

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class ServiceVirtualizationTest {
 
 @Autowired
 private OrderService orderService;
 
 @Test
 void shouldHandlePaymentServiceFailure() {
 // Mock successful payment
 stubFor(post("/api/payments").willReturn(okJson("""
 {
 "transactionId": "txn_123",
 "status": "SUCCESS"
 }
 """)));
 
 Order order = orderService.placeOrder(orderRequest);
 
 assertEquals(OrderStatus.CONFIRMED, order.getStatus());
 verify(postRequestedFor(urlEqualTo("/api/payments")));
 }
 
 @Test
 void shouldHandlePaymentTimeout() {
 // Mock payment timeout
 stubFor(post("/api/payments").willReturn(aResponse().withFixedDelay(10000) // 10 second delay.withStatus(504))); // Gateway timeout
 
 assertThrows(PaymentTimeoutException.class, () -> {
 orderService.placeOrder(orderRequest);
 });
 }
}
```

### 3. Integration Testing с Docker Compose

```yaml
# docker-compose.test.yml
version: '3.8'
services:
 user-service:
 build:./user-service
 ports:
 - "8081:8080"
 depends_on:
 - user-db
 environment:
 - SPRING_PROFILES_ACTIVE=test
 - DATABASE_URL=jdbc:postgresql://user-db:5432/userdb
 
 order-service:
 build:./order-service
 ports:
 - "8082:8080"
 depends_on:
 - order-db
 - user-service
 environment:
 - SPRING_PROFILES_ACTIVE=test
 - DATABASE_URL=jdbc:postgresql://order-db:5432/orderdb
 - USER_SERVICE_URL=http://user-service:8080
 
 user-db:
 image: postgres:13
 environment:
 POSTGRES_DB: userdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
 
 order-db:
 image: postgres:13
 environment:
 POSTGRES_DB: orderdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
```

```java
@SpringBootTest
@Testcontainers
public class MicroservicesIntegrationTest {
 
 @Container
 private static DockerComposeContainer<?> environment = 
 new DockerComposeContainer<>(new File("docker-compose.test.yml")).withExposedService("user-service", 8081).withExposedService("order-service", 8082);
 
 @Test
 void shouldCompleteOrderFlowAcrossServices() {
 // 1. Create user via User Service
 String userServiceUrl = environment.getServiceHost("user-service", 8081) + 
 ":" + environment.getServicePort("user-service", 8081);
 
 User user = createUser(userServiceUrl, "john@example.com");
 
 // 2. Create order via Order Service
 String orderServiceUrl = environment.getServiceHost("order-service", 8082) + 
 ":" + environment.getServicePort("order-service", 8082);
 
 Order order = createOrder(orderServiceUrl, user.getId(), productId);
 
 // 3. Verify order status
 assertEquals(OrderStatus.CONFIRMED, order.getStatus());
 
 // 4. Verify user was retrieved from User Service
 assertEquals(user.getId(), order.getUserId());
 }
}
```

### 4. Chaos Engineering для микросервисов

```java
@SpringBootTest
@Testcontainers
public class ChaosEngineeringTest {
 
 @Container
 private static DockerComposeContainer<?> environment = 
 new DockerComposeContainer<>(new File("docker-compose.test.yml"));
 
 @Autowired
 private ChaosMonkey chaosMonkey;
 
 @Test
 void shouldHandleServiceDegradation() {
 // Start with all services healthy
 assertAllServicesHealthy();
 
 // Introduce chaos: kill user service
 chaosMonkey.killService("user-service");
 
 // Wait for circuit breaker to open
 await().atMost(30, SECONDS).until(() -> 
 circuitBreakerService.isCircuitBreakerOpen("user-service"));
 
 // Try to create order - should fail gracefully
 assertThrows(ServiceUnavailableException.class, () -> {
 orderService.createOrder(userId, productId, 1);
 });
 
 // Verify order was not created
 assertEquals(0, orderRepository.count());
 
 // Restore service
 chaosMonkey.restoreService("user-service");
 
 // Wait for service to recover
 await().atMost(60, SECONDS).until(() -> 
 healthCheckService.isServiceHealthy("user-service"));
 
 // Try again - should work
 Order order = orderService.createOrder(userId, productId, 1);
 assertNotNull(order);
 }
 
 @Test
 void shouldHandleNetworkLatency() {
 // Introduce network latency between services
 chaosMonkey.addNetworkLatency("order-service", "user-service", 5000); // 5 seconds
 
 // Measure response time
 long startTime = System.currentTimeMillis();
 Order order = orderService.createOrder(userId, productId, 1);
 long responseTime = System.currentTimeMillis() - startTime;
 
 // Should still work but slower
 assertNotNull(order);
 assertTrue(responseTime > 5000, "Response should be slower due to latency");
 
 // Verify timeout handling
 chaosMonkey.addNetworkLatency("order-service", "user-service", 30000); // 30 seconds
 
 assertThrows(TimeoutException.class, () -> {
 orderService.createOrder(userId, productId, 1);
 });
 }
}
```

## Q7. Как тестировать legacy код?

### 1. Характеристики Legacy Code

```java
// Legacy код: трудно тестировать
public class LegacyOrderProcessor {
 
 private static LegacyOrderProcessor instance;
 
 // Singleton - трудно mock'ать
 public static LegacyOrderProcessor getInstance() {
 if (instance == null) {
 instance = new LegacyOrderProcessor();
 }
 return instance;
 }
 
 // Статические методы - трудно тестировать
 public static boolean validateOrder(Order order) {
 // Сложная бизнес-логика без зависимостей
 return order!= null && order.getItems().size() > 0;
 }
 
 // Тесные связи с инфраструктурой
 public void processOrder(Order order) {
 // Прямой доступ к базе данных
 Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "user", "pass");
 //... бизнес-логика mixed с инфраструктурой
 }
 
 // Длинные методы с множественной ответственностью
 public void completeOrderProcessing(Order order) {
 // Валидация
 if (order == null) throw new IllegalArgumentException();
 
 // Расчет стоимости
 BigDecimal total = calculateTotal(order);
 
 // Сохранение в БД
 saveToDatabase(order, total);
 
 // Отправка email
 sendConfirmationEmail(order);
 
 // Логирование
 logOrderProcessing(order);
 }
}
```

### 2. Стратегия тестирования Legacy Code

#### Шаг 1: Создание Safety Net

```java
public class LegacyCodeSafetyNet {
 
 // Создаем characterization tests для понимания текущего поведения
 @Test
 void characterizeValidateOrder() {
 // Тестируем различные входы для понимания текущего поведения
 assertTrue(LegacyOrderProcessor.validateOrder(createValidOrder()));
 assertFalse(LegacyOrderProcessor.validateOrder(null));
 assertFalse(LegacyOrderProcessor.validateOrder(createEmptyOrder()));
 }
 
 @Test
 void characterizeProcessOrder() {
 Order order = createValidOrder();
 
 // Записываем текущее поведение (даже если оно неправильное)
 LegacyOrderProcessor processor = LegacyOrderProcessor.getInstance();
 
 // Это может бросить исключение или изменить состояние
 try {
 processor.processOrder(order);
 // Если дошли сюда - тест проходит
 assertTrue(true);
 } catch (Exception e) {
 // Записываем что произошло
 assertNotNull(e); // Пока просто фиксируем поведение
 }
 }
 
 private Order createValidOrder() {
 Order order = new Order();
 order.setItems(Arrays.asList(new OrderItem("item1", BigDecimal.ONE)));
 return order;
 }
 
 private Order createEmptyOrder() {
 return new Order();
 }
}
```

#### Шаг 2: Extract and Override

```java
// Создаем подкласс для тестирования
public class TestableLegacyOrderProcessor extends LegacyOrderProcessor {
 
 private Connection testConnection;
 
 // Override для тестирования
 @Override
 protected Connection getDatabaseConnection() {
 if (testConnection!= null) {
 return testConnection;
 }
 return super.getDatabaseConnection();
 }
 
 // Setter для инъекции test connection
 public void setTestConnection(Connection testConnection) {
 this.testConnection = testConnection;
 }
}

// Тест с использованием subclass
@Test
void shouldProcessOrderWithTestDatabase() {
 TestableLegacyOrderProcessor processor = new TestableLegacyOrderProcessor();
 
 // Setup test database
 Connection testConn = createTestDatabaseConnection();
 processor.setTestConnection(testConn);
 
 Order order = createValidOrder();
 processor.processOrder(order);
 
 // Verify data was saved to test database
 verifyOrderSaved(testConn, order);
}
```

#### Шаг 3: Sprout Method и Sprout Class

```java
// Sprout Method: Добавляем новый метод вместо изменения существующего
public class LegacyOrderProcessor {
 
 // Старый метод остается без изменений
 public void completeOrderProcessing(Order order) {
 // Legacy код...
 }
 
 // Новый метод для тестирования
 public void completeOrderProcessingRefactored(Order order) {
 validateOrder(order);
 BigDecimal total = calculateTotal(order);
 saveOrder(order, total);
 sendConfirmationEmail(order);
 logProcessing(order);
 }
 
 // Extracted methods - легко тестировать
 protected void validateOrder(Order order) {
 if (order == null) throw new IllegalArgumentException("Order cannot be null");
 if (order.getItems().isEmpty()) throw new IllegalArgumentException("Order must have items");
 }
 
 protected BigDecimal calculateTotal(Order order) {
 return order.getItems().stream().map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
 }
 
 protected void saveOrder(Order order, BigDecimal total) {
 // Refactored database logic
 }
 
 protected void sendConfirmationEmail(Order order) {
 // Refactored email logic
 }
 
 protected void logProcessing(Order order) {
 // Refactored logging logic
 }
}

// Тест для новых методов
@Test
void shouldCalculateTotalCorrectly() {
 LegacyOrderProcessor processor = new LegacyOrderProcessor();
 
 Order order = new Order();
 order.setItems(Arrays.asList(
 new OrderItem("item1", BigDecimal.valueOf(10)),
 new OrderItem("item2", BigDecimal.valueOf(20))
 ));
 
 BigDecimal total = processor.calculateTotal(order);
 assertEquals(BigDecimal.valueOf(30), total);
}

@Test
void shouldValidateOrder() {
 LegacyOrderProcessor processor = new LegacyOrderProcessor();
 
 assertThrows(IllegalArgumentException.class, () -> {
 processor.validateOrder(null);
 });
 
 assertThrows(IllegalArgumentException.class, () -> {
 processor.validateOrder(new Order()); // empty items
 });
}
```

#### Шаг 4: Wrap Method

```java
// Wrap Method: Создаем wrapper для тестирования
public class LegacyOrderProcessorWrapper {
 
 private final LegacyOrderProcessor legacyProcessor;
 
 public LegacyOrderProcessorWrapper(LegacyOrderProcessor legacyProcessor) {
 this.legacyProcessor = legacyProcessor;
 }
 
 public void processOrderSafely(Order order) {
 try {
 // Дополнительная валидация
 validateInput(order);
 
 // Вызов legacy кода
 legacyProcessor.completeOrderProcessing(order);
 
 // Дополнительная обработка
 performPostProcessing(order);
 
 } catch (Exception e) {
 handleError(order, e);
 }
 }
 
 protected void validateInput(Order order) {
 // Testable validation logic
 }
 
 protected void performPostProcessing(Order order) {
 // Testable post-processing logic
 }
 
 protected void handleError(Order order, Exception e) {
 // Testable error handling logic
 }
}

// Тест wrapper'а
@Test
void shouldHandleLegacyProcessingErrors() {
 LegacyOrderProcessor mockLegacy = mock(LegacyOrderProcessor.class);
 doThrow(new RuntimeException("Legacy error")).when(mockLegacy).completeOrderProcessing(any());
 
 LegacyOrderProcessorWrapper wrapper = new LegacyOrderProcessorWrapper(mockLegacy);
 
 Order order = createValidOrder();
 
 // Should handle error gracefully
 assertDoesNotThrow(() -> wrapper.processOrderSafely(order));
 
 // Verify error handling was called
 // (This would require spying or additional verification)
}
```

## Q8. Что такое TDD, BDD и ATDD?

### Test-Driven Development (TDD)

TDD — это практика разработки, при которой тесты пишутся перед кодом.

#### Цикл TDD: Red-Green-Refactor

```java
// RED: Пишем failing тест
@Test
void shouldReturnUserById() {
 UserRepository repository = new InMemoryUserRepository();
 UserService service = new UserService(repository);
 
 User user = service.findById(1L);
 
 assertNotNull(user);
 assertEquals(1L, user.getId());
}

// GREEN: Пишем минимальный код для прохождения теста
public class UserService {
 private final UserRepository repository;
 
 public UserService(UserRepository repository) {
 this.repository = repository;
 }
 
 public User findById(Long id) {
 return repository.findById(id).orElse(null);
 }
}

public class InMemoryUserRepository implements UserRepository {
 private final Map<Long, User> users = new HashMap<>();
 
 @Override
 public Optional<User> findById(Long id) {
 return Optional.ofNullable(users.get(id));
 }
}

// REFACTOR: Улучшаем код
public class UserService {
 private final UserRepository repository;
 
 public UserService(UserRepository repository) {
 this.repository = repository;
 }
 
 public Optional<User> findById(Long id) {
 return repository.findById(id);
 }
}
```

### Behavior-Driven Development (BDD)

BDD — это подход, при котором спецификации пишутся в виде executable examples.

#### Gherkin синтаксис

```gherkin
Feature: User Authentication
 As a registered user
 I want to log in to the system
 So that I can access my account

 Background:
 Given a user exists with email "user@example.com" and password "password"

 Scenario: Successful login
 When I attempt to login with email "user@example.com" and password "password"
 Then I should be redirected to the dashboard
 And I should see a welcome message

 Scenario: Failed login with wrong password
 When I attempt to login with email "user@example.com" and password "wrong"
 Then I should see an error message "Invalid credentials"
 And I should remain on the login page
```

#### Реализация BDD с Cucumber

```java
// Step definitions
@CucumberContextConfiguration
@SpringBootTest
public class AuthenticationSteps {
 
 @Autowired
 private UserRepository userRepository;
 
 @Autowired
 private MockMvc mockMvc;
 
 private ResultActions result;
 
 @Given("a user exists with email {string} and password {string}")
 public void aUserExists(String email, String password) {
 User user = new User(email, passwordEncoder.encode(password));
 userRepository.save(user);
 }
 
 @When("I attempt to login with email {string} and password {string}")
 public void iAttemptToLogin(String email, String password) throws Exception {
 String loginJson = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
 
 result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson));
 }
 
 @Then("I should be redirected to the dashboard")
 public void iShouldBeRedirectedToDashboard() throws Exception {
 result.andExpect(status().isOk()).andExpect(jsonPath("$.redirectUrl").value("/dashboard"));
 }
 
 @Then("I should see an error message {string}")
 public void iShouldSeeErrorMessage(String message) throws Exception {
 result.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error").value(message));
 }
}
```

### Acceptance Test-Driven Development (ATDD)

ATDD — это практика, при которой acceptance tests пишутся совместно с бизнесом перед разработкой.

#### Процесс ATDD

1. Обсуждение требований: Бизнес-аналитики, QA и разработчики обсуждают требования
2. Написание acceptance criteria: Определяют условия приемки
3. Создание acceptance tests: Пишу executable acceptance tests
4. Разработка: Developers пишут код для прохождения тестов
5. Демонстрация: Показывают работающий функционал бизнесу

#### Пример ATDD

```java
// Acceptance test для user story
@ExtendWith(MockitoExtension.class)
public class UserRegistrationAcceptanceTest {
 
 @Mock
 private UserRepository userRepository;
 
 @Mock
 private EmailService emailService;
 
 @InjectMocks
 private UserService userService;
 
 @Test
 @DisplayName("User can register with valid email and password")
 void userCanRegisterWithValidCredentials() {
 // Given: Valid registration data
 String email = "newuser@example.com";
 String password = "ValidPass123!";
 
 User savedUser = new User(email, "hashed_password");
 savedUser.setId(1L);
 
 when(userRepository.save(any(User.class))).thenReturn(savedUser);
 when(userRepository.existsByEmail(email)).thenReturn(false);
 
 // When: User attempts registration
 UserRegistrationRequest request = new UserRegistrationRequest(email, password);
 UserRegistrationResponse response = userService.registerUser(request);
 
 // Then: Registration succeeds
 assertNotNull(response.getUserId());
 assertEquals(email, response.getEmail());
 
 // And: Welcome email is sent
 verify(emailService).sendWelcomeEmail(email);
 
 // And: User is saved to repository
 verify(userRepository).save(argThat(user -> 
 user.getEmail().equals(email) && 
 passwordEncoder.matches(password, user.getPassword())));
 }
 
 @Test
 @DisplayName("Registration fails when email already exists")
 void registrationFailsWhenEmailExists() {
 // Given: Email already exists
 String email = "existing@example.com";
 when(userRepository.existsByEmail(email)).thenReturn(true);
 
 // When: User attempts registration
 UserRegistrationRequest request = new UserRegistrationRequest(email, "password");
 
 // Then: Registration fails
 assertThrows(EmailAlreadyExistsException.class, () -> {
 userService.registerUser(request);
 });
 
 // And: No user is saved
 verify(userRepository, never()).save(any(User.class));
 
 // And: No email is sent
 verify(emailService, never()).sendWelcomeEmail(anyString());
 }
}
```

### Сравнение подходов

| Аспект | TDD | BDD | ATDD |
|--------|-----|-----|------|
| **Фокус** | Developer | Business | Acceptance |
| **Уровень** | Unit | Feature | System |
| **Синтаксис** | Code | Gherkin | Natural language |
| **Участники** | Developers | Business + Dev + QA | All stakeholders |
| **Инструменты** | JUnit | Cucumber | FitNesse, Concordion |
| **Цель** | Code quality | Behavior specification | Requirements validation |

## Q9. Как организовать тестирование в agile команде?

### 1. Testing в Scrum Framework

#### Sprint Planning

```java
public class SprintPlanning {
 
 public Sprint planSprint(List<UserStory> backlog, TeamCapacity capacity) {
 Sprint sprint = new Sprint();
 
 // 1. Выбираем user stories
 List<UserStory> selectedStories = selectStoriesForSprint(backlog, capacity);
 sprint.setUserStories(selectedStories);
 
 // 2. Создаем acceptance criteria для каждой story
 for (UserStory story: selectedStories) {
 List<AcceptanceCriterion> criteria = createAcceptanceCriteria(story);
 story.setAcceptanceCriteria(criteria);
 }
 
 // 3. Оцениваем testing effort
 estimateTestingEffort(sprint);
 
 // 4. Планируем testing activities
 planTestingActivities(sprint);
 
 return sprint;
 }
 
 private List<AcceptanceCriterion> createAcceptanceCriteria(UserStory story) {
 return Arrays.asList(
 new AcceptanceCriterion("User can register with valid email"),
 new AcceptanceCriterion("Registration fails with invalid email"),
 new AcceptanceCriterion("Confirmation email is sent"),
 new AcceptanceCriterion("User can login after registration")
 );
 }
 
 private void estimateTestingEffort(Sprint sprint) {
 double totalTestingHours = 0;
 
 for (UserStory story: sprint.getUserStories()) {
 // Unit tests: 2-3 hours per story
 totalTestingHours += 2.5;
 
 // Integration tests: 1-2 hours per story
 totalTestingHours += 1.5;
 
 // Acceptance tests: 1 hour per story
 totalTestingHours += 1;
 
 // Exploratory testing: 30 min per story
 totalTestingHours += 0.5;
 }
 
 sprint.setTestingEffortHours(totalTestingHours);
 }
 
 private void planTestingActivities(Sprint sprint) {
 // Day 1-2: Development + Unit Testing
 // Day 3: Integration Testing
 // Day 4: Acceptance Testing + Bug Fixing
 // Day 5: Exploratory Testing + Documentation
 
 sprint.setTestingSchedule(createTestingSchedule());
 }
}
```

#### Daily Standup с фокусом на тестирование

```java
public class DailyStandup {
 
 public void conductStandup(List<TeamMember> team) {
 for (TeamMember member: team) {
 // 1. Что было сделано вчера?
 reportYesterdayProgress(member);
 
 // 2. Что планируется сегодня?
 reportTodayPlans(member);
 
 // 3. Есть ли препятствия?
 identifyImpediments(member);
 
 // 4. Статус testing activities
 reportTestingStatus(member);
 }
 
 // Обновляем burndown chart
 updateBurndownChart();
 }
 
 private void reportTestingStatus(TeamMember member) {
 System.out.println("Testing status for " + member.getName() + ":");
 
 // Unit test coverage
 double coverage = member.getCurrentUnitTestCoverage();
 System.out.println("- Unit test coverage: " + coverage + "%");
 
 // Open defects
 int defects = member.getOpenDefectCount();
 System.out.println("- Open defects: " + defects);
 
 // Testing progress
 double progress = member.getTestingProgress();
 System.out.println("- Testing progress: " + progress + "%");
 }
}
```

### 2. Three Amigos Meetings

```java
public class ThreeAmigosMeeting {
 
 private final BusinessAnalyst business;
 private final Developer developer;
 private final Tester tester;
 
 public void conductThreeAmigosMeeting(UserStory story) {
 // 1. BA объясняет бизнес-ценность
 business.explainBusinessValue(story);
 
 // 2. BA описывает acceptance criteria
 List<String> criteria = business.defineAcceptanceCriteria(story);
 
 // 3. Developer задает уточняющие вопросы
 List<String> devQuestions = developer.askClarifyingQuestions(criteria);
 business.answerQuestions(devQuestions);
 
 // 4. Tester предлагает test scenarios
 List<TestScenario> scenarios = tester.proposeTestScenarios(criteria);
 
 // 5. Обсуждаем edge cases и error conditions
 discussEdgeCases(scenarios);
 
 // 6. Developer оценивает complexity
 developer.estimateImplementationComplexity(story, scenarios);
 
 // 7. Согласовываем understanding
 ensureSharedUnderstanding(story, criteria, scenarios);
 
 // 8. Документируем результаты
 documentMeetingResults(story, criteria, scenarios);
 }
 
 private void discussEdgeCases(List<TestScenario> scenarios) {
 // Happy path scenarios
 // Alternative flows
 // Error conditions
 // Boundary conditions
 // Security considerations
 }
 
 private void documentMeetingResults(UserStory story, List<String> criteria, List<TestScenario> scenarios) {
 MeetingResults results = new MeetingResults();
 results.setStory(story);
 results.setAcceptanceCriteria(criteria);
 results.setTestScenarios(scenarios);
 results.setMeetingDate(LocalDate.now());
 results.setParticipants(Arrays.asList(business, developer, tester));
 
 // Save to persistent storage
 meetingRepository.save(results);
 }
}
```

### 3. Definition of Ready (DoR) и Definition of Done (DoD)

```java
public class DefinitionOfReady {
 
 public static final List<String> CRITERIA = Arrays.asList(
 "Story has clear business value",
 "Acceptance criteria are defined and agreed upon",
 "Story is small enough to complete in one sprint",
 "Dependencies are identified and manageable",
 "Story has been estimated",
 "Test scenarios have been identified",
 "Design decisions have been made",
 "No external dependencies that can't be stubbed"
 );
 
 public boolean isReady(UserStory story) {
 return CRITERIA.stream().allMatch(criterion -> 
 evaluateCriterion(story, criterion));
 }
 
 private boolean evaluateCriterion(UserStory story, String criterion) {
 switch (criterion) {
 case "Story has clear business value":
 return story.getBusinessValue()!= null &&!story.getBusinessValue().isEmpty();
 
 case "Acceptance criteria are defined and agreed upon":
 return story.getAcceptanceCriteria()!= null && 
 story.getAcceptanceCriteria().size() >= 3;
 
 case "Test scenarios have been identified":
 return story.getTestScenarios()!= null &&!story.getTestScenarios().isEmpty();
 
 //... other criteria
 default:
 return false;
 }
 }
}

public class DefinitionOfDone {
 
 public static final List<String> CRITERIA = Arrays.asList(
 "Code is written and committed",
 "Unit tests are written and passing",
 "Code review is completed",
 "Integration tests are passing",
 "Acceptance criteria are met",
 "Documentation is updated",
 "No known critical defects",
 "Performance requirements are met",
 "Security review is completed",
 "Feature is deployed to staging"
 );
 
 public boolean isDone(UserStory story) {
 return CRITERIA.stream().allMatch(criterion -> 
 evaluateCriterion(story, criterion));
 }
 
 private boolean evaluateCriterion(UserStory story, String criterion) {
 switch (criterion) {
 case "Unit tests are written and passing":
 return story.getUnitTestCoverage() >= 80.0;
 
 case "Integration tests are passing":
 return story.getIntegrationTestsPassing();
 
 case "Acceptance criteria are met":
 return story.getAcceptanceCriteria().stream().allMatch(AcceptanceCriterion::isMet);
 
 case "No known critical defects":
 return story.getDefects().stream().noneMatch(defect -> defect.getSeverity() == Severity.CRITICAL);
 
 //... other criteria
 default:
 return false;
 }
 }
}
```

### 4. Test Automation Strategy в Agile

```java
public class AgileTestAutomationStrategy {
 
 public TestAutomationPlan createPlan(Sprint sprint) {
 TestAutomationPlan plan = new TestAutomationPlan();
 
 // 1. Identify automation candidates
 List<TestCase> automationCandidates = identifyAutomationCandidates(sprint);
 plan.setAutomationCandidates(automationCandidates);
 
 // 2. Prioritize based on ROI
 List<TestCase> prioritizedTests = prioritizeByROI(automationCandidates);
 plan.setPrioritizedTests(prioritizedTests);
 
 // 3. Estimate automation effort
 double automationEffort = estimateAutomationEffort(prioritizedTests);
 plan.setAutomationEffortHours(automationEffort);
 
 // 4. Plan automation activities
 plan.setAutomationSchedule(createAutomationSchedule(automationEffort));
 
 return plan;
 }
 
 private List<TestCase> identifyAutomationCandidates(Sprint sprint) {
 return sprint.getUserStories().stream().flatMap(story -> story.getTestCases().stream()).filter(this::isAutomationCandidate).collect(Collectors.toList());
 }
 
 private boolean isAutomationCandidate(TestCase testCase) {
 // Критерии для автоматизации:
 // - Выполняется часто (regression)
 // - Критически важна для бизнеса
 // - Стабильна (не меняется часто)
 // - Трудоемка при ручном выполнении
 // - Может быть автоматизирована технически
 
 return testCase.isFrequentlyExecuted() &&
 testCase.isBusinessCritical() &&
 testCase.isStable() &&
 testCase.isTimeConsuming() &&
 testCase.isTechnicallyFeasible();
 }
 
 private List<TestCase> prioritizeByROI(List<TestCase> tests) {
 return tests.stream().sorted((a, b) -> Double.compare(calculateROI(b), calculateROI(a))).collect(Collectors.toList());
 }
 
 private double calculateROI(TestCase test) {
 // ROI = (Benefit - Cost) / Cost
 // Benefit = frequency * time_saved_per_execution
 // Cost = initial_automation_cost + maintenance_cost
 
 double benefit = test.getExecutionFrequency() * test.getManualExecutionTimeMinutes();
 double cost = test.getAutomationEffortHours() * 60; // convert to minutes
 
 return (benefit - cost) / cost;
 }
 
 private double estimateAutomationEffort(List<TestCase> tests) {
 return tests.stream().mapToDouble(TestCase::getAutomationEffortHours).sum();
 }
 
 private List<SprintActivity> createAutomationSchedule(double effortHours) {
 List<SprintActivity> activities = new ArrayList<>();
 
 // Разбиваем automation effort на ежедневные задачи
 int days = (int) Math.ceil(effortHours / 6); // 6 hours per day
 
 for (int i = 0; i < days; i++) {
 activities.add(new SprintActivity(
 "Day " + (i + 1), 
 "Continue test automation",
 Math.min(6.0, effortHours - (i * 6))
 ));
 }
 
 return activities;
 }
}
```

## Q10. Какие метрики качества тестирования?

### 1. Coverage Metrics

```java
public class CoverageMetricsCollector {
 
 public CoverageMetrics collectCoverageMetrics(TestSuite suite) {
 CoverageMetrics metrics = new CoverageMetrics();
 
 // Line coverage
 metrics.setLineCoverage(calculateLineCoverage(suite));
 
 // Branch coverage
 metrics.setBranchCoverage(calculateBranchCoverage(suite));
 
 // Method coverage
 metrics.setMethodCoverage(calculateMethodCoverage(suite));
 
 // Class coverage
 metrics.setClassCoverage(calculateClassCoverage(suite));
 
 // Instruction coverage
 metrics.setInstructionCoverage(calculateInstructionCoverage(suite));
 
 return metrics;
 }
 
 private double calculateLineCoverage(TestSuite suite) {
 int totalLines = suite.getTotalLinesOfCode();
 int coveredLines = suite.getCoveredLinesOfCode();
 
 return totalLines > 0? (double) coveredLines / totalLines * 100: 0;
 }
 
 private double calculateBranchCoverage(TestSuite suite) {
 int totalBranches = suite.getTotalBranches();
 int coveredBranches = suite.getCoveredBranches();
 
 return totalBranches > 0? (double) coveredBranches / totalBranches * 100: 0;
 }
}
```

### 2. Quality Metrics

```java
public class QualityMetricsCollector {
 
 public QualityMetrics collectQualityMetrics(Project project) {
 QualityMetrics metrics = new QualityMetrics();
 
 // Defect density
 metrics.setDefectDensity(calculateDefectDensity(project));
 
 // Defect leakage
 metrics.setDefectLeakage(calculateDefectLeakage(project));
 
 // Mean time to detect (MTTD)
 metrics.setMeanTimeToDetect(calculateMTTD(project));
 
 // Mean time to resolve (MTTR)
 metrics.setMeanTimeToResolve(calculateMTTR(project));
 
 // Test effectiveness
 metrics.setTestEffectiveness(calculateTestEffectiveness(project));
 
 return metrics;
 }
 
 private double calculateDefectDensity(Project project) {
 int totalDefects = project.getTotalDefectsFound();
 int size = project.getSizeInKLOC(); // Kilo lines of code
 
 return size > 0? (double) totalDefects / size: 0;
 }
 
 private double calculateDefectLeakage(Project project) {
 int defectsFoundByUsers = project.getDefectsFoundByUsers();
 int totalDefects = project.getTotalDefectsFound();
 
 return totalDefects > 0? (double) defectsFoundByUsers / totalDefects * 100: 0;
 }
 
 private Duration calculateMTTD(Project project) {
 List<Defect> defects = project.getDefects();
 
 if (defects.isEmpty()) {
 return Duration.ZERO;
 }
 
 long totalDetectionTime = defects.stream().mapToLong(defect -> {
 // Time from introduction to detection
 return ChronoUnit.HOURS.between(
 defect.getIntroducedAt(), 
 defect.getDetectedAt());
 }).sum();
 
 return Duration.ofHours(totalDetectionTime / defects.size());
 }
 
 private Duration calculateMTTR(Project project) {
 List<Defect> resolvedDefects = project.getDefects().stream().filter(defect -> defect.getResolvedAt()!= null).collect(Collectors.toList());
 
 if (resolvedDefects.isEmpty()) {
 return Duration.ZERO;
 }
 
 long totalResolutionTime = resolvedDefects.stream().mapToLong(defect -> {
 return ChronoUnit.HOURS.between(
 defect.getDetectedAt(), 
 defect.getResolvedAt());
 }).sum();
 
 return Duration.ofHours(totalResolutionTime / resolvedDefects.size());
 }
 
 private double calculateTestEffectiveness(Project project) {
 // Effectiveness = Defects found by tests / Total defects
 int defectsFoundByTests = project.getDefectsFoundByTests();
 int totalDefects = project.getTotalDefectsFound();
 
 return totalDefects > 0? (double) defectsFoundByTests / totalDefects * 100: 0;
 }
}
```

### 3. Process Metrics

```java
public class ProcessMetricsCollector {
 
 public ProcessMetrics collectProcessMetrics(Team team, Sprint sprint) {
 ProcessMetrics metrics = new ProcessMetrics();
 
 // Test automation progress
 metrics.setAutomationProgress(calculateAutomationProgress(team));
 
 // Test execution time
 metrics.setAverageTestExecutionTime(calculateAverageTestExecutionTime(sprint));
 
 // Test maintenance effort
 metrics.setTestMaintenanceEffort(calculateTestMaintenanceEffort(team));
 
 // Test reliability
 metrics.setTestReliability(calculateTestReliability(sprint));
 
 return metrics;
 }
 
 private double calculateAutomationProgress(Team team) {
 int totalTestCases = team.getTotalTestCases();
 int automatedTestCases = team.getAutomatedTestCases();
 
 return totalTestCases > 0? (double) automatedTestCases / totalTestCases * 100: 0;
 }
 
 private Duration calculateAverageTestExecutionTime(Sprint sprint) {
 List<TestExecution> executions = sprint.getTestExecutions();
 
 if (executions.isEmpty()) {
 return Duration.ZERO;
 }
 
 long totalTime = executions.stream().mapToLong(execution -> execution.getDuration().toMillis()).sum();
 
 return Duration.ofMillis(totalTime / executions.size());
 }
 
 private double calculateTestMaintenanceEffort(Team team) {
 // Maintenance effort as percentage of total testing effort
 double totalTestingEffort = team.getTotalTestingEffortHours();
 double maintenanceEffort = team.getTestMaintenanceEffortHours();
 
 return totalTestingEffort > 0? maintenanceEffort / totalTestingEffort * 100: 0;
 }
 
 private double calculateTestReliability(Sprint sprint) {
 List<TestExecution> executions = sprint.getTestExecutions();
 
 if (executions.isEmpty()) {
 return 0;
 }
 
 // Reliability = (Successful executions / Total executions) * 100
 long successfulExecutions = executions.stream().filter(TestExecution::isSuccessful).count();
 
 return (double) successfulExecutions / executions.size() * 100;
 }
}
```

### 4. Monitoring и Alerting

```java
@Service
public class QualityMetricsMonitor {
 
 private final MeterRegistry meterRegistry;
 private final AlertService alertService;
 
 @Scheduled(fixedRate = 300000) // Every 5 minutes
 public void monitorQualityMetrics() {
 // Monitor coverage
 double currentCoverage = getCurrentTestCoverage();
 meterRegistry.gauge("test.coverage.current", currentCoverage);
 
 if (currentCoverage < 80.0) {
 alertService.sendAlert("Test coverage dropped below 80%: " + currentCoverage + "%");
 }
 
 // Monitor defect trends
 int newDefectsThisWeek = getNewDefectsThisWeek();
 meterRegistry.counter("defects.new.weekly").increment(newDefectsThisWeek);
 
 if (newDefectsThisWeek > 10) {
 alertService.sendAlert("High defect rate this week: " + newDefectsThisWeek + " defects");
 }
 
 // Monitor test execution time
 Duration avgExecutionTime = getAverageTestExecutionTime();
 meterRegistry.timer("test.execution.average").record(avgExecutionTime);
 
 if (avgExecutionTime.compareTo(Duration.ofMinutes(30)) > 0) {
 alertService.sendAlert("Test execution time too high: " + avgExecutionTime.toMinutes() + " minutes");
 }
 
 // Monitor build stability
 double buildSuccessRate = getBuildSuccessRate();
 meterRegistry.gauge("build.success.rate", buildSuccessRate);
 
 if (buildSuccessRate < 95.0) {
 alertService.sendAlert("Build success rate dropped: " + buildSuccessRate + "%");
 }
 }
 
 private double getCurrentTestCoverage() {
 // Implementation to get current coverage from JaCoCo or similar
 return jacocoService.getCurrentCoverage();
 }
 
 private int getNewDefectsThisWeek() {
 LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
 return defectRepository.countByCreatedAtAfter(weekAgo);
 }
 
 private Duration getAverageTestExecutionTime() {
 // Implementation to calculate average test execution time
 return testExecutionService.getAverageExecutionTime();
 }
 
 private double getBuildSuccessRate() {
 // Implementation to calculate build success rate over last N builds
 return buildService.getSuccessRate(50); // Last 50 builds
 }
}
```

### 5. Test Data Management Metrics

```java
public class TestDataMetrics {
 
 public TestDataQualityMetrics assessTestDataQuality(TestEnvironment env) {
 TestDataQualityMetrics metrics = new TestDataQualityMetrics();
 
 // Data freshness
 metrics.setDataFreshness(calculateDataFreshness(env));
 
 // Data coverage
 metrics.setDataCoverage(calculateDataCoverage(env));
 
 // Data consistency
 metrics.setDataConsistency(calculateDataConsistency(env));
 
 // Data isolation
 metrics.setDataIsolation(calculateDataIsolation(env));
 
 return metrics;
 }
 
 private double calculateDataFreshness(TestEnvironment env) {
 // How fresh is test data (age in days)
 List<TestDataRecord> records = env.getTestDataRecords();
 
 if (records.isEmpty()) {
 return 0;
 }
 
 double averageAge = records.stream().mapToLong(record -> {
 long age = ChronoUnit.DAYS.between(record.getCreatedAt(), LocalDateTime.now());
 return age;
 }).average().orElse(0);
 
 // Convert to freshness score (newer = higher score)
 return Math.max(0, 100 - averageAge);
 }
 
 private double calculateDataCoverage(TestEnvironment env) {
 // How well test data covers different scenarios
 Set<String> coveredScenarios = env.getCoveredScenarios();
 Set<String> allScenarios = env.getAllPossibleScenarios();
 
 return allScenarios.isEmpty()? 0: 
 (double) coveredScenarios.size() / allScenarios.size() * 100;
 }
 
 private double calculateDataConsistency(TestEnvironment env) {
 // Check for data integrity issues
 List<DataConsistencyIssue> issues = env.validateDataConsistency();
 
 int totalRecords = env.getTotalDataRecords();
 int inconsistentRecords = issues.stream().mapToInt(DataConsistencyIssue::getAffectedRecords).sum();
 
 return totalRecords > 0? 
 (1 - (double) inconsistentRecords / totalRecords) * 100: 100;
 }
 
 private double calculateDataIsolation(TestEnvironment env) {
 // Check if test data is properly isolated between tests
 List<TestExecution> executions = env.getRecentTestExecutions();
 
 long isolatedExecutions = executions.stream().filter(TestExecution::isDataIsolated).count();
 
 return executions.isEmpty()? 0: 
 (double) isolatedExecutions / executions.size() * 100;
 }
}
```

## Заключение

Стратегии тестирования определяют эффективность и качество процесса разработки. Senior Java Developer должен понимать различные подходы: Testing Pyramid, Testing Quadrants, Shift-Left Testing, Risk-Based Testing. Выбор правильной стратегии зависит от контекста проекта, команды и требований к качеству. Ключевые метрики помогают отслеживать эффективность тестирования и принимать обоснованные решения о готовности релиза.

