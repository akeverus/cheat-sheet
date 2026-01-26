# Вопросы на собеседовании: Test Automation

**Комплексное руководство по вопросам собеседования на тему Test Automation для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


Test automation является ключевым навыком для повышения эффективности тестирования. Senior Java Developer должен понимать различные инструменты автоматизации, паттерны проектирования тестов и стратегии поддержки автоматизированных тестов.Дата последнего обновления: 2026-01-24

## Полезные ссылки

### Официальная документация
- [Selenium WebDriver](https://www.selenium.dev/documentation/webdriver/)
- [Rest Assured](https://rest-assured.io/)
- [Cucumber](https://cucumber.io/docs/cucumber/)

### Ресурсы
- [Test Automation University](https://testautomationu.applitools.com/)
- [Automation Panda](https://automationpanda.com/)

### См. также
- `unit-testing-interview.md` - Unit тестирование
- `integration-testing-interview.md` - Интеграционное тестирование
- `test-strategies-interview.md` - Стратегии тестирования

## Содержание

- [Q1. Что такое test automation и зачем она нужна?](#q1-что-такое-test-automation-и-зачем-она-нужна)
- [Q2. Какие инструменты используются для test automation?](#q2-какие-инструменты-используются-для-test-automation)
- [Q3. Как автоматизировать UI тестирование с Selenium?](#q3-как-автоматизировать-ui-тестирование-с-selenium)
- [Q4. Как автоматизировать API тестирование?](#q4-как-автоматизировать-api-тестирование)
- [Q5. Что такое Page Object Model?](#q5-что-такое-page-object-model)
- [Q6. Как организовать тестовые данные в автоматизации?](#q6-как-организовать-тестовые-данные-в-автоматизации)
- [Q7. Как интегрировать автоматизацию в CI/CD?](#q7-как-интегрировать-автоматизацию-в-cicd)
- [Q8. Как поддерживать автоматизированные тесты?](#q8-как-поддерживать-автоматизированные-тесты)
- [Q9. Какие паттерны используются в test automation?](#q9-какие-паттерны-используются-в-test-automation)
- [Q10. Как измерить ROI от test automation?](#q10-как-измерить-roi-от-test-automation)

## Q1. Что такое test automation и зачем она нужна?

Test automation — это использование программного обеспечения для автоматического выполнения тестовых сценариев с целью проверки корректности работы программного обеспечения.

### Преимущества test automation

#### 1. Скорость и эффективность

```java
// Ручной тест: занимает 5 минут на выполнение
// Автоматизированный тест: занимает 30 секунд

public class PerformanceComparison {
 
 public void manualVsAutomated() {
 // Manual testing scenario
 ManualTest manual = new ManualTest();
 long manualTime = manual.executeLoginTest(); // 5 minutes
 
 // Automated testing scenario
 AutomatedTest automated = new AutomatedTest();
 long automatedTime = automated.executeLoginTest(); // 30 seconds
 
 double speedImprovement = (double) manualTime / automatedTime; // 10x faster
 
 // Automated tests can run 24/7
 int nightlyRuns = 8; // Every 3 hours during 24 hours
 int totalAutomatedTests = nightlyRuns * 30; // Month of testing
 int equivalentManualDays = totalAutomatedTests * 5 / (60 * 8); // Manual days needed
 
 System.out.println("Speed improvement: " + speedImprovement + "x");
 System.out.println("Automated test capacity: " + equivalentManualDays + " manual testing days per month");
 }
}
```

#### 2. Надежность и повторяемость

```java
public class ReliabilityComparison {
 
 public void humanVsMachine() {
 // Human testing error rate:2-5%
 double humanErrorRate = 0.03;
 
 // Machine testing error rate:0.1%
 double machineErrorRate = 0.001;
 
 // Test suite with 1000 tests
 int testSuiteSize = 1000;
 
 // Human errors per test run
 int humanErrors = (int) (testSuiteSize * humanErrorRate);
 
 // Machine errors per test run
 int machineErrors = (int) (testSuiteSize * machineErrorRate);
 
 // Over 100 test runs (e.g., regression testing)
 int totalRuns = 100;
 int totalHumanErrors = humanErrors * totalRuns;
 int totalMachineErrors = machineErrors * totalRuns;
 
 System.out.println("Human errors over 100 runs: " + totalHumanErrors);
 System.out.println("Machine errors over 100 runs: " + totalMachineErrors);
 System.out.println("Reliability improvement: " + 
 (double) totalHumanErrors / totalMachineErrors + "x");
 }
}
```

#### 3. Раннее обнаружение регрессий

```java
// Regression test that runs on every commit
@Test
public void shouldMaintainLoginFunctionality() {
 // This test runs automatically on every code change
 // If login breaks due to refactoring, test fails immediately
 
 LoginPage loginPage = new LoginPage(driver);
 DashboardPage dashboard = loginPage.login("user@example.com", "password");
 
 assertTrue(dashboard.isUserLoggedIn());
 assertEquals("Welcome, User!", dashboard.getWelcomeMessage());
}

// CI/CD integration ensures immediate feedback
/*.github/workflows/regression.yml
on: [push]
jobs:
 regression:
 runs-on: ubuntu-latest
 steps:
 - uses: actions/checkout@v3
 - name: Run regression tests
 run: mvn test -Dtest=RegressionTestSuite
 - name: Notify on failure
 if: failure()
 run: curl -X POST -H 'Content-type: application/json' 
 --data '{"text":"Regression tests failed!"}' 
 $SLACK_WEBHOOK
*/
```

#### 4. Масштабируемость

```java
public class ScalabilityDemo {
 
 public void demonstrateScalability() {
 // Manual testing scalability
 int manualTesters = 5;
 int testsPerDayPerTester = 20;
 int manualCapacity = manualTesters * testsPerDayPerTester; // 100 tests/day
 
 // Automated testing scalability
 int automatedTestRuns = 24; // Every hour
 int testsPerRun = 500;
 int automatedCapacity = automatedTestRuns * testsPerRun; // 12,000 tests/day
 
 double scalabilityFactor = (double) automatedCapacity / manualCapacity;
 
 System.out.println("Manual capacity: " + manualCapacity + " tests/day");
 System.out.println("Automated capacity: " + automatedCapacity + " tests/day");
 System.out.println("Scalability improvement: " + scalabilityFactor + "x");
 
 // Automated tests can run across multiple environments simultaneously
 int environments = 4; // dev, staging, pre-prod, prod
 int totalAutomatedCapacity = automatedCapacity * environments;
 
 System.out.println("Multi-environment capacity: " + totalAutomatedCapacity + " tests/day");
 }
}
```

### Когда автоматизировать тесты?

```java
public class AutomationCandidateEvaluator {
 
 public AutomationDecision evaluateTestForAutomation(TestCase testCase) {
 AutomationDecision decision = new AutomationDecision();
 
 // High-value automation candidates
 boolean isHighValue = 
 testCase.isFrequentlyExecuted() && // Runs often
 testCase.isBusinessCritical() && // Important functionality
 testCase.isStable() && // Doesn't change frequently
 testCase.isTimeConsuming() && // Takes long time manually
 testCase.isTechnicallyFeasible(); // Can be automated
 
 if (isHighValue) {
 decision.setShouldAutomate(true);
 decision.setPriority(Priority.HIGH);
 decision.setReason("High business value, stable, frequently executed");
 }
 
 // Medium-value candidates
 boolean isMediumValue = 
 testCase.isRegressionTest() || // Regression protection needed
 testCase.isDataDriven() || // Multiple data sets
 testCase.isComplexWorkflow(); // Complex business logic
 
 if (isMediumValue &&!isHighValue) {
 decision.setShouldAutomate(true);
 decision.setPriority(Priority.MEDIUM);
 decision.setReason("Suitable for automation with medium priority");
 }
 
 // Low-value or not suitable
 if (!isHighValue &&!isMediumValue) {
 decision.setShouldAutomate(false);
 decision.setReason("Better left manual or not worth automating");
 }
 
 return decision;
 }
 
 public static class AutomationDecision {
 private boolean shouldAutomate;
 private Priority priority;
 private String reason;
 
 // getters and setters...
 }
 
 public enum Priority {
 HIGH, MEDIUM, LOW
 }
}
```

## Q2. Какие инструменты используются для test automation?

### 1. Unit Testing Frameworks

```java
// JUnit 5 - Modern testing framework
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
 
 @Mock
 private UserRepository userRepository;
 
 @Autowired
 private UserService userService;
 
 @Test
 @DisplayName("Should create user successfully")
 void shouldCreateUserSuccessfully() {
 // Given
 User user = new User("john@example.com", "password");
 when(userRepository.save(any(User.class))).thenReturn(user);
 
 // When
 User created = userService.createUser(user);
 
 // Then
 assertNotNull(created);
 verify(userRepository).save(user);
 }
 
 @ParameterizedTest
 @ValueSource(strings = {"", "invalid-email", "@example.com"})
 void shouldRejectInvalidEmails(String email) {
 assertThrows(IllegalArgumentException.class, () -> {
 userService.createUser(new User(email, "password"));
 });
 }
}
```

### 2. API Testing Tools

```java
// Rest Assured - Fluent API testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @Test
 void shouldCreateUserViaApi() {
 // Using Rest Assured with Spring Boot
 given().contentType(ContentType.JSON).body("""
 {
 "email": "john@example.com",
 "password": "password123"
 }
 """).when().post("/api/users").then().statusCode(201).body("email", equalTo("john@example.com")).body("id", notNullValue());
 }
 
 @Test
 void shouldHandleValidationErrors() {
 given().contentType(ContentType.JSON).body("""
 {
 "email": "invalid-email",
 "password": "short"
 }
 """).when().post("/api/users").then().statusCode(400).body("errors.email", hasItem("Invalid email format")).body("errors.password", hasItem("Password too short"));
 }
}
```

### 3. UI Testing Frameworks

```java
// Selenium WebDriver with JUnit 5
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginUITest {
 
 private WebDriver driver;
 
 @BeforeEach
 void setUp() {
 // Setup WebDriver
 ChromeOptions options = new ChromeOptions();
 options.addArguments("--headless"); // Run in headless mode for CI
 options.addArguments("--no-sandbox");
 options.addArguments("--disable-dev-shm-usage");
 
 driver = new ChromeDriver(options);
 
 // Navigate to application
 driver.get("http://localhost:" + port);
 }
 
 @AfterEach
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 void shouldLoginSuccessfully() {
 // Find elements
 WebElement emailField = driver.findElement(By.id("email"));
 WebElement passwordField = driver.findElement(By.id("password"));
 WebElement loginButton = driver.findElement(By.id("login-button"));
 
 // Perform login
 emailField.sendKeys("user@example.com");
 passwordField.sendKeys("password");
 loginButton.click();
 
 // Wait for redirect and verify
 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 wait.until(ExpectedConditions.urlContains("/dashboard"));
 
 WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
 assertEquals("Welcome, User!", welcomeMessage.getText());
 }
 
 @Test
 void shouldShowErrorForInvalidCredentials() {
 WebElement emailField = driver.findElement(By.id("email"));
 WebElement passwordField = driver.findElement(By.id("password"));
 WebElement loginButton = driver.findElement(By.id("login-button"));
 
 emailField.sendKeys("invalid@example.com");
 passwordField.sendKeys("wrongpassword");
 loginButton.click();
 
 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
 WebElement errorMessage = wait.until(
 ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
 
 assertEquals("Invalid credentials", errorMessage.getText());
 }
}
```

### 4. BDD Frameworks

```java
// Cucumber with JUnit 5
@SpringBootTest
public class UserRegistrationBDDTest {
 
 @Autowired
 private UserService userService;
 
 private UserRegistrationResult result;
 
 @Given("a user wants to register with email {string}")
 public void aUserWantsToRegister(String email) {
 // Setup test context
 this.email = email;
 }
 
 @When("they submit valid registration details")
 public void theySubmitValidRegistrationDetails() {
 UserRegistrationRequest request = new UserRegistrationRequest(email, "password123");
 result = userService.registerUser(request);
 }
 
 @Then("the registration should be successful")
 public void theRegistrationShouldBeSuccessful() {
 assertNotNull(result.getUserId());
 assertEquals(RegistrationStatus.SUCCESS, result.getStatus());
 }
 
 @Then("a welcome email should be sent")
 public void aWelcomeEmailShouldBeSent() {
 // Verify email was sent
 verify(emailService).sendWelcomeEmail(email);
 }
 
 @When("they submit invalid email format")
 public void theySubmitInvalidEmailFormat() {
 UserRegistrationRequest request = new UserRegistrationRequest("invalid-email", "password123");
 try {
 userService.registerUser(request);
 } catch (ValidationException e) {
 this.exception = e;
 }
 }
 
 @Then("the registration should fail with validation error")
 public void theRegistrationShouldFailWithValidationError() {
 assertNotNull(exception);
 assertTrue(exception.getErrors().contains("Invalid email format"));
 }
}
```

### 5. Mobile Testing Frameworks

```java
// Appium for mobile testing
public class MobileLoginTest {
 
 private AndroidDriver<AndroidElement> driver;
 
 @BeforeEach
 void setUp() throws MalformedURLException {
 DesiredCapabilities caps = new DesiredCapabilities();
 caps.setCapability("platformName", "Android");
 caps.setCapability("platformVersion", "11.0");
 caps.setCapability("deviceName", "emulator-5554");
 caps.setCapability("app", "/path/to/app.apk");
 caps.setCapability("automationName", "UiAutomator2");
 
 driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
 }
 
 @AfterEach
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 void shouldLoginOnMobileApp() {
 // Wait for app to load
 WebDriverWait wait = new WebDriverWait(driver, 30);
 
 // Enter credentials
 AndroidElement emailField = (AndroidElement) wait.until(
 ExpectedConditions.elementToBeClickable(By.id("email_input")));
 emailField.sendKeys("user@example.com");
 
 AndroidElement passwordField = driver.findElement(By.id("password_input"));
 passwordField.sendKeys("password");
 
 // Tap login button
 AndroidElement loginButton = driver.findElement(By.id("login_button"));
 loginButton.click();
 
 // Verify successful login
 AndroidElement welcomeMessage = wait.until(
 ExpectedConditions.presenceOfElementLocated(By.id("welcome_message")));
 
 assertEquals("Welcome to the app!", welcomeMessage.getText());
 }
}
```

### 6. Performance Testing Tools

```java
// JMeter test script simulation
public class PerformanceTestSimulation {
 
 @Test
 void simulateJMeterLoadTest() {
 // Simulate JMeter thread group
 int numberOfThreads = 100;
 int rampUpPeriod = 30; // seconds
 int loopCount = 5;
 
 ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
 CountDownLatch latch = new CountDownLatch(numberOfThreads * loopCount);
 
 List<Future<PerformanceResult>> results = new ArrayList<>();
 
 // Start threads gradually (ramp-up)
 for (int i = 0; i < numberOfThreads; i++) {
 executor.submit(() -> {
 try {
 Thread.sleep((long) (Math.random() * rampUpPeriod * 1000));
 
 for (int loop = 0; loop < loopCount; loop++) {
 long startTime = System.currentTimeMillis();
 
 // Simulate API call
 PerformanceResult result = simulateApiCall();
 
 long endTime = System.currentTimeMillis();
 result.setResponseTime(endTime - startTime);
 
 results.add(CompletableFuture.completedFuture(result));
 latch.countDown();
 
 // Think time between requests
 Thread.sleep(1000);
 }
 } catch (Exception e) {
 // Handle exceptions
 }
 });
 }
 
 // Wait for all requests to complete
 try {
 assertTrue(latch.await(300, TimeUnit.SECONDS));
 } catch (InterruptedException e) {
 fail("Load test timed out");
 }
 
 // Analyze results
 analyzePerformanceResults(results);
 
 executor.shutdown();
 }
 
 private PerformanceResult simulateApiCall() {
 // Simulate HTTP call with RestTemplate or WebClient
 try {
 ResponseEntity<String> response = restTemplate.getForEntity("/api/users", String.class);
 return new PerformanceResult(response.getStatusCode().is2xxSuccessful(), null);
 } catch (Exception e) {
 return new PerformanceResult(false, e.getMessage());
 }
 }
 
 private void analyzePerformanceResults(List<Future<PerformanceResult>> futures) {
 List<Long> responseTimes = new ArrayList<>();
 int successCount = 0;
 int totalCount = 0;
 
 for (Future<PerformanceResult> future: futures) {
 try {
 PerformanceResult result = future.get();
 totalCount++;
 
 if (result.isSuccess()) {
 successCount++;
 responseTimes.add(result.getResponseTime());
 }
 } catch (Exception e) {
 // Handle future exceptions
 }
 }
 
 // Calculate metrics
 double successRate = (double) successCount / totalCount * 100;
 double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
 double percentile95 = calculatePercentile(responseTimes, 95);
 
 // Assert performance criteria
 assertTrue(successRate >= 99.0, "Success rate should be >= 99%");
 assertTrue(avgResponseTime <= 2000, "Average response time should be <= 2s");
 assertTrue(percentile95 <= 5000, "95th percentile should be <= 5s");
 }
 
 private double calculatePercentile(List<Long> values, double percentile) {
 Collections.sort(values);
 int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
 return values.get(Math.max(0, index));
 }
}
```

## Q3. Как автоматизировать UI тестирование с Selenium?

### 1. Основы Selenium WebDriver

```java
public class BasicSeleniumTest {
 
 private WebDriver driver;
 
 @BeforeEach
 void setUp() {
 // Setup Chrome driver
 WebDriverManager.chromedriver().setup();
 
 ChromeOptions options = new ChromeOptions();
 options.addArguments("--headless"); // For CI/CD
 options.addArguments("--no-sandbox");
 options.addArguments("--disable-dev-shm-usage");
 options.addArguments("--window-size=1920,1080");
 
 driver = new ChromeDriver(options);
 driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
 }
 
 @AfterEach
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 void shouldLoadHomePage() {
 driver.get("https://example.com");
 
 String title = driver.getTitle();
 assertEquals("Example Domain", title);
 
 WebElement heading = driver.findElement(By.tagName("h1"));
 assertEquals("Example Domain", heading.getText());
 }
}
```

### 2. Locators и поиск элементов

```java
public class ElementLocationTest {
 
 private WebDriver driver;
 
 @Test
 void demonstrateLocators() {
 driver.get("https://example.com/login");
 
 // By ID (most reliable)
 WebElement emailField = driver.findElement(By.id("email"));
 emailField.sendKeys("user@example.com");
 
 // By Name
 WebElement passwordField = driver.findElement(By.name("password"));
 passwordField.sendKeys("password123");
 
 // By CSS Selector (powerful)
 WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
 loginButton.click();
 
 // By XPath (when CSS is not enough)
 WebElement successMessage = driver.findElement(
 By.xpath("//div[@class='alert alert-success']"));
 assertTrue(successMessage.isDisplayed());
 
 // By Link Text
 WebElement logoutLink = driver.findElement(By.linkText("Logout"));
 assertTrue(logoutLink.isDisplayed());
 
 // By Partial Link Text
 WebElement profileLink = driver.findElement(By.partialLinkText("Profile"));
 profileLink.click();
 }
 
 @Test
 void handleDynamicElements() {
 // Wait for element to be present
 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 
 WebElement dynamicElement = wait.until(
 ExpectedConditions.presenceOfElementLocated(By.id("dynamic-content")));
 
 // Wait for element to be clickable
 WebElement clickableButton = wait.until(
 ExpectedConditions.elementToBeClickable(By.id("submit-button")));
 
 // Wait for element to be visible
 WebElement visibleElement = wait.until(
 ExpectedConditions.visibilityOfElementLocated(By.className("result")));
 
 // Wait for specific text
 wait.until(ExpectedConditions.textToBePresentInElement(
 By.id("status"), "Operation completed"));
 }
}
```

### 3. Actions и взаимодействие с элементами

```java
public class UserInteractionsTest {
 
 private WebDriver driver;
 private Actions actions;
 
 @BeforeEach
 void setUp() {
 driver = new ChromeDriver();
 actions = new Actions(driver);
 }
 
 @Test
 void demonstrateUserInteractions() {
 driver.get("https://example.com/form");
 
 // Text input
 WebElement textField = driver.findElement(By.id("name"));
 textField.clear();
 textField.sendKeys("John Doe");
 
 // Dropdown selection
 Select countrySelect = new Select(driver.findElement(By.id("country")));
 countrySelect.selectByVisibleText("United States");
 // or countrySelect.selectByValue("US");
 // or countrySelect.selectByIndex(1);
 
 // Checkbox
 WebElement newsletterCheckbox = driver.findElement(By.id("newsletter"));
 if (!newsletterCheckbox.isSelected()) {
 newsletterCheckbox.click();
 }
 
 // Radio buttons
 WebElement maleRadio = driver.findElement(By.cssSelector("input[name='gender'][value='male']"));
 maleRadio.click();
 
 // File upload
 WebElement fileInput = driver.findElement(By.id("file-upload"));
 fileInput.sendKeys("/path/to/file.pdf");
 
 // Keyboard actions
 WebElement searchField = driver.findElement(By.id("search"));
 searchField.sendKeys("selenium");
 searchField.sendKeys(Keys.ENTER); // Press Enter
 
 // Mouse actions
 WebElement menuItem = driver.findElement(By.id("menu-item"));
 actions.moveToElement(menuItem).perform(); // Hover
 
 WebElement submenuItem = driver.findElement(By.id("submenu-item"));
 actions.moveToElement(submenuItem).click().perform();
 
 // Drag and drop
 WebElement source = driver.findElement(By.id("draggable"));
 WebElement target = driver.findElement(By.id("droppable"));
 actions.dragAndDrop(source, target).perform();
 }
 
 @Test
 void handleJavaScriptAlerts() {
 driver.get("https://example.com/alerts");
 
 // Trigger alert
 driver.findElement(By.id("alert-button")).click();
 
 // Handle alert
 Alert alert = driver.switchTo().alert();
 String alertText = alert.getText();
 assertEquals("This is an alert!", alertText);
 alert.accept(); // Click OK
 
 // Handle confirmation dialog
 driver.findElement(By.id("confirm-button")).click();
 Alert confirmation = driver.switchTo().alert();
 confirmation.dismiss(); // Click Cancel
 
 // Handle prompt
 driver.findElement(By.id("prompt-button")).click();
 Alert prompt = driver.switchTo().alert();
 prompt.sendKeys("Test input");
 prompt.accept();
 }
}
```

### 4. Page Object Model (POM)

```java
// Base page class
public abstract class BasePage {
 
 protected WebDriver driver;
 protected WebDriverWait wait;
 
 public BasePage(WebDriver driver) {
 this.driver = driver;
 this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 }
 
 protected WebElement findElement(By locator) {
 return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
 }
 
 protected void clickElement(By locator) {
 WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
 element.click();
 }
 
 protected void typeText(By locator, String text) {
 WebElement element = findElement(locator);
 element.clear();
 element.sendKeys(text);
 }
 
 protected String getText(By locator) {
 return findElement(locator).getText();
 }
 
 public boolean isDisplayed(By locator) {
 try {
 return driver.findElement(locator).isDisplayed();
 } catch (NoSuchElementException e) {
 return false;
 }
 }
}

// Login page
public class LoginPage extends BasePage {
 
 private static final By EMAIL_FIELD = By.id("email");
 private static final By PASSWORD_FIELD = By.id("password");
 private static final By LOGIN_BUTTON = By.id("login-button");
 private static final By ERROR_MESSAGE = By.className("error-message");
 
 public LoginPage(WebDriver driver) {
 super(driver);
 }
 
 public void enterEmail(String email) {
 typeText(EMAIL_FIELD, email);
 }
 
 public void enterPassword(String password) {
 typeText(PASSWORD_FIELD, password);
 }
 
 public DashboardPage clickLoginButton() {
 clickElement(LOGIN_BUTTON);
 return new DashboardPage(driver);
 }
 
 public LoginPage clickLoginButtonExpectingFailure() {
 clickElement(LOGIN_BUTTON);
 return this;
 }
 
 public DashboardPage login(String email, String password) {
 enterEmail(email);
 enterPassword(password);
 return clickLoginButton();
 }
 
 public String getErrorMessage() {
 return getText(ERROR_MESSAGE);
 }
 
 public boolean isErrorMessageDisplayed() {
 return isDisplayed(ERROR_MESSAGE);
 }
}

// Dashboard page
public class DashboardPage extends BasePage {
 
 private static final By WELCOME_MESSAGE = By.className("welcome-message");
 private static final By LOGOUT_BUTTON = By.id("logout-button");
 
 public DashboardPage(WebDriver driver) {
 super(driver);
 // Verify we're on the correct page
 wait.until(ExpectedConditions.urlContains("/dashboard"));
 }
 
 public String getWelcomeMessage() {
 return getText(WELCOME_MESSAGE);
 }
 
 public LoginPage clickLogout() {
 clickElement(LOGOUT_BUTTON);
 return new LoginPage(driver);
 }
 
 public boolean isUserLoggedIn() {
 return isDisplayed(WELCOME_MESSAGE);
 }
}

// Test using page objects
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginUITest {
 
 @Autowired
 private int port;
 
 private WebDriver driver;
 
 @BeforeEach
 void setUp() {
 driver = new ChromeDriver();
 driver.get("http://localhost:" + port + "/login");
 }
 
 @AfterEach
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 void shouldLoginSuccessfully() {
 LoginPage loginPage = new LoginPage(driver);
 
 DashboardPage dashboard = loginPage.login("user@example.com", "password");
 
 assertTrue(dashboard.isUserLoggedIn());
 assertEquals("Welcome, User!", dashboard.getWelcomeMessage());
 }
 
 @Test
 void shouldShowErrorForInvalidCredentials() {
 LoginPage loginPage = new LoginPage(driver);
 
 loginPage.login("invalid@example.com", "wrongpassword");
 
 assertTrue(loginPage.isErrorMessageDisplayed());
 assertEquals("Invalid credentials", loginPage.getErrorMessage());
 }
}
```

### 5. Selenium Grid и параллельное выполнение

```java
@Configuration
public class SeleniumGridConfig {
 
 @Bean
 @Scope("prototype")
 public WebDriver remoteWebDriver() throws MalformedURLException {
 ChromeOptions options = new ChromeOptions();
 options.addArguments("--headless");
 
 return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
 }
}

// Parallel test execution
@SpringBootTest
public class ParallelUITest {
 
 @Autowired
 private WebDriver driver;
 
 @Test
 void testOnChrome() {
 // Test runs on Chrome
 driver.get("https://example.com");
 assertEquals("Example Domain", driver.getTitle());
 }
 
 @Test
 void testOnFirefox() {
 // Test runs on Firefox (different thread)
 driver.get("https://example.com");
 assertEquals("Example Domain", driver.getTitle());
 }
}

// JUnit 5 parallel execution
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParallelTestSuite {
 
 private WebDriver driver;
 
 @BeforeAll
 void setUp() {
 driver = new ChromeDriver();
 }
 
 @AfterAll
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 @Execution(ExecutionMode.CONCURRENT)
 void test1() {
 driver.get("https://example.com/page1");
 // Test page 1
 }
 
 @Test
 @Execution(ExecutionMode.CONCURRENT)
 void test2() {
 driver.get("https://example.com/page2");
 // Test page 2
 }
 
 @Test
 @Execution(ExecutionMode.CONCURRENT)
 void test3() {
 driver.get("https://example.com/page3");
 // Test page 3
 }
}
```

## Q4. Как автоматизировать API тестирование?

### 1. Rest Assured Framework

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAssuredApiTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 private int port;
 
 @BeforeEach
 void setUp() {
 // Configure Rest Assured
 RestAssured.port = port;
 RestAssured.baseURI = "http://localhost";
 }
 
 @Test
 void shouldCreateUserViaApi() {
 String requestBody = """
 {
 "email": "john@example.com",
 "password": "password123",
 "firstName": "John",
 "lastName": "Doe"
 }
 """;
 
 given().contentType(ContentType.JSON).body(requestBody).when().post("/api/users").then().statusCode(201).body("id", notNullValue()).body("email", equalTo("john@example.com")).body("firstName", equalTo("John")).body("lastName", equalTo("Doe")).body("createdAt", notNullValue());
 }
 
 @Test
 void shouldHandleValidationErrors() {
 String invalidRequest = """
 {
 "email": "invalid-email",
 "password": "short"
 }
 """;
 
 given().contentType(ContentType.JSON).body(invalidRequest).when().post("/api/users").then().statusCode(400).body("errors.email", hasItem("Invalid email format")).body("errors.password", hasItem("Password must be at least 8 characters")).body("errors.firstName", hasItem("First name is required"));
 }
 
 @Test
 void shouldAuthenticateAndAccessProtectedResource() {
 // First, authenticate
 String authResponse = given().contentType(ContentType.JSON).body("""
 {
 "username": "admin",
 "password": "admin123"
 }
 """).when().post("/api/auth/login").then().statusCode(200).extract().path("token");
 
 // Use token to access protected resource
 given().header("Authorization", "Bearer " + authResponse).when().get("/api/admin/users").then().statusCode(200).body("size()", greaterThan(0));
 }
 
 @Test
 void shouldTestFileUpload() {
 byte[] fileContent = "Test file content".getBytes();
 
 given().multiPart("file", "test.txt", fileContent, "text/plain").multiPart("description", "Test file upload").when().post("/api/files/upload").then().statusCode(200).body("fileId", notNullValue()).body("filename", equalTo("test.txt"));
 }
}
```

### 2. API Test Automation Framework

```java
// Base API test class
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {
 
 @Autowired
 protected TestRestTemplate restTemplate;
 
 protected int port;
 
 @BeforeAll
 void setUpRestAssured() {
 RestAssured.port = port;
 RestAssured.baseURI = "http://localhost";
 RestAssured.config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 5000).setParam("http.socket.timeout", 5000));
 }
 
 protected ValidatableResponse authenticate(String username, String password) {
 return given().contentType(ContentType.JSON).body(String.format("""
 {
 "username": "%s",
 "password": "%s"
 }
 """, username, password)).when().post("/api/auth/login").then();
 }
 
 protected String getAuthToken(String username, String password) {
 return authenticate(username, password).statusCode(200).extract().path("token");
 }
 
 protected RequestSpecification authenticatedRequest(String token) {
 return given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON);
 }
}

// User API tests
public class UserApiTest extends BaseApiTest {
 
 @Test
 void shouldGetUserProfile() {
 String token = getAuthToken("user@example.com", "password");
 
 authenticatedRequest(token).when().get("/api/users/profile").then().statusCode(200).body("email", equalTo("user@example.com")).body("firstName", notNullValue()).body("lastName", notNullValue());
 }
 
 @Test
 void shouldUpdateUserProfile() {
 String token = getAuthToken("user@example.com", "password");
 
 String updateRequest = """
 {
 "firstName": "Updated",
 "lastName": "Name"
 }
 """;
 
 authenticatedRequest(token).body(updateRequest).when().put("/api/users/profile").then().statusCode(200).body("firstName", equalTo("Updated")).body("lastName", equalTo("Name"));
 }
 
 @Test
 void shouldNotAccessOtherUserProfile() {
 String token = getAuthToken("user@example.com", "password");
 
 authenticatedRequest(token).when().get("/api/users/999/profile").then().statusCode(403);
 }
}

// Order API tests
public class OrderApiTest extends BaseApiTest {
 
 private Long createdOrderId;
 
 @Test
 void shouldCreateOrder() {
 String token = getAuthToken("user@example.com", "password");
 
 String orderRequest = """
 {
 "items": [
 {
 "productId": 1,
 "quantity": 2
 }
 ],
 "shippingAddress": {
 "street": "123 Main St",
 "city": "Anytown",
 "zipCode": "12345"
 }
 }
 """;
 
 ValidatableResponse response = authenticatedRequest(token).body(orderRequest).when().post("/api/orders").then().statusCode(201).body("id", notNullValue()).body("status", equalTo("PENDING")).body("total", notNullValue());
 
 createdOrderId = response.extract().path("id");
 }
 
 @Test
 void shouldGetOrderById() {
 assumeTrue(createdOrderId!= null, "Order must be created first");
 
 String token = getAuthToken("user@example.com", "password");
 
 authenticatedRequest(token).when().get("/api/orders/{id}", createdOrderId).then().statusCode(200).body("id", equalTo(createdOrderId.intValue())).body("items.size()", equalTo(1)).body("status", equalTo("PENDING"));
 }
 
 @Test
 void shouldCancelOrder() {
 assumeTrue(createdOrderId!= null, "Order must be created first");
 
 String token = getAuthToken("user@example.com", "password");
 
 authenticatedRequest(token).when().post("/api/orders/{id}/cancel", createdOrderId).then().statusCode(200);
 
 // Verify order is cancelled
 authenticatedRequest(token).when().get("/api/orders/{id}", createdOrderId).then().statusCode(200).body("status", equalTo("CANCELLED"));
 }
}
```

### 3. Contract Testing с Pact

```java
// Consumer test (Order Service)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8081")
public class OrderServiceContractTest {
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact getUserDetails(PactDslWithProvider builder) {
 return builder.given("User with id 123 exists").uponReceiving("A request for user details").path("/api/users/123").method("GET").headers("Authorization", "Bearer token123").willRespondWith().status(200).headers("Content-Type", "application/json").body(new PactDslJsonBody().numberType("id", 123).stringType("name", "John Doe").stringType("email", "john@example.com").booleanType("active", true)).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "getUserDetails")
 void shouldGetUserDetailsForOrder(MockServer mockServer) {
 UserServiceClient client = new UserServiceClient(mockServer.getUrl());
 OrderService orderService = new OrderService(client, null);
 
 // Test that order service can get user details
 User user = orderService.getUserForOrder(123L);
 
 assertEquals(123L, user.getId());
 assertEquals("John Doe", user.getName());
 assertEquals("john@example.com", user.getEmail());
 assertTrue(user.isActive());
 }
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact updateUserLoyaltyPoints(PactDslWithProvider builder) {
 return builder.given("User has sufficient loyalty points").uponReceiving("A request to deduct loyalty points").path("/api/users/123/loyalty").method("POST").headers("Authorization", "Bearer token123").body(new PactDslJsonBody().numberType("points", 50)).willRespondWith().status(200).body(new PactDslJsonBody().numberType("remainingPoints", 150)).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "updateUserLoyaltyPoints")
 void shouldDeductLoyaltyPointsForOrder(MockServer mockServer) {
 UserServiceClient client = new UserServiceClient(mockServer.getUrl());
 OrderService orderService = new OrderService(client, null);
 
 // Test loyalty points deduction
 int remainingPoints = orderService.deductLoyaltyPoints(123L, 50);
 
 assertEquals(150, remainingPoints);
 }
}

// Provider test (User Service)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("UserService")
@PactFolder("pacts")
public class UserServiceProviderTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @State("User with id 123 exists")
 public void userExists() {
 // Setup test data
 User user = new User(123L, "John Doe", "john@example.com", true);
 user.setLoyaltyPoints(200);
 userRepository.save(user);
 }
 
 @State("User has sufficient loyalty points")
 public void userHasLoyaltyPoints() {
 // Same as above - user already has 200 points
 }
 
 @Test
 void shouldHonorUserServiceContract() {
 // Pact framework automatically verifies contracts
 // Tests are generated from consumer pacts
 }
}
```

### 4. Performance Testing APIs

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPerformanceTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @Test
 void shouldHandleConcurrentApiRequests() {
 int numberOfThreads = 50;
 int requestsPerThread = 20;
 ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
 CountDownLatch latch = new CountDownLatch(numberOfThreads * requestsPerThread);
 
 List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
 AtomicInteger successCount = new AtomicInteger(0);
 AtomicInteger errorCount = new AtomicInteger(0);
 
 for (int i = 0; i < numberOfThreads; i++) {
 executor.submit(() -> {
 for (int j = 0; j < requestsPerThread; j++) {
 long startTime = System.nanoTime();
 
 try {
 ResponseEntity<String> response = restTemplate.getForEntity(
 "/api/users", String.class);
 
 if (response.getStatusCode().is2xxSuccessful()) {
 successCount.incrementAndGet();
 } else {
 errorCount.incrementAndGet();
 }
 } catch (Exception e) {
 errorCount.incrementAndGet();
 }
 
 long endTime = System.nanoTime();
 responseTimes.add((endTime - startTime) / 1_000_000); // to milliseconds
 
 latch.countDown();
 
 // Small delay between requests
 try {
 Thread.sleep(50);
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 }
 }
 });
 }
 
 // Wait for all requests to complete
 try {
 assertTrue(latch.await(300, TimeUnit.SECONDS), "Performance test timed out");
 } catch (InterruptedException e) {
 fail("Test interrupted");
 }
 
 executor.shutdown();
 
 // Analyze results
 double successRate = (double) successCount.get() / (successCount.get() + errorCount.get()) * 100;
 double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
 long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
 long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
 
 // Calculate percentiles
 Collections.sort(responseTimes);
 double percentile95 = getPercentile(responseTimes, 95);
 double percentile99 = getPercentile(responseTimes, 99);
 
 // Performance assertions
 assertTrue(successRate >= 99.5, String.format("Success rate too low: %.2f%%", successRate));
 assertTrue(avgResponseTime <= 500, String.format("Average response time too high: %.2f ms", avgResponseTime));
 assertTrue(percentile95 <= 1000, String.format("95th percentile too high: %.2f ms", percentile95));
 assertTrue(percentile99 <= 2000, String.format("99th percentile too high: %.2f ms", percentile99));
 
 System.out.println("=== Performance Test Results ===");
 System.out.println("Total requests: " + responseTimes.size());
 System.out.println("Success rate: " + String.format("%.2f%%", successRate));
 System.out.println("Average response time: " + String.format("%.2f ms", avgResponseTime));
 System.out.println("Min response time: " + minResponseTime + " ms");
 System.out.println("Max response time: " + maxResponseTime + " ms");
 System.out.println("95th percentile: " + String.format("%.2f ms", percentile95));
 System.out.println("99th percentile: " + String.format("%.2f ms", percentile99));
 }
 
 private double getPercentile(List<Long> sortedValues, double percentile) {
 int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
 return sortedValues.get(Math.max(0, Math.min(index, sortedValues.size() - 1)));
 }
 
 @Test
 void shouldTestApiUnderIncreasingLoad() {
 int[] loadLevels = {10, 25, 50, 100, 200};
 
 for (int concurrentUsers: loadLevels) {
 System.out.println("Testing with " + concurrentUsers + " concurrent users...");
 
 long startTime = System.nanoTime();
 
 // Run load test
 LoadTestResult result = runLoadTest(concurrentUsers, 10); // 10 requests per user
 
 long testDuration = (System.nanoTime() - startTime) / 1_000_000_000; // to seconds
 
 // Log results
 System.out.println("Load level: " + concurrentUsers + " users");
 System.out.println("Duration: " + testDuration + " seconds");
 System.out.println("Requests/sec: " + (double) (concurrentUsers * 10) / testDuration);
 System.out.println("Avg response time: " + result.getAverageResponseTime() + " ms");
 System.out.println("Error rate: " + result.getErrorRate() + "%");
 System.out.println("---");
 
 // Performance degrades gracefully
 assertTrue(result.getErrorRate() < 5.0, 
 "Error rate too high at load level " + concurrentUsers);
 assertTrue(result.getAverageResponseTime() < 2000, 
 "Response time too high at load level " + concurrentUsers);
 }
 }
 
 private LoadTestResult runLoadTest(int concurrentUsers, int requestsPerUser) {
 // Implementation similar to the concurrent test above
 // Return aggregated results
 return new LoadTestResult(450.0, 1.2); // example values
 }
 
 static class LoadTestResult {
 private final double averageResponseTime;
 private final double errorRate;
 
 public LoadTestResult(double averageResponseTime, double errorRate) {
 this.averageResponseTime = averageResponseTime;
 this.errorRate = errorRate;
 }
 
 public double getAverageResponseTime() { return averageResponseTime; }
 public double getErrorRate() { return errorRate; }
 }
}
```

## Q5. Что такое Page Object Model?

Page Object Model (POM) — это паттерн проектирования для автоматизации UI тестирования, который создает объектно-ориентированную модель веб-страниц.

### 1. Базовая структура POM

```java
// Base page class with common functionality
public abstract class BasePage {
 
 protected WebDriver driver;
 protected WebDriverWait wait;
 protected Actions actions;
 
 public BasePage(WebDriver driver) {
 this.driver = driver;
 this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 this.actions = new Actions(driver);
 }
 
 // Common element interactions
 protected WebElement findElement(By locator) {
 return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
 }
 
 protected void clickElement(By locator) {
 WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
 element.click();
 }
 
 protected void typeText(By locator, String text) {
 WebElement element = findElement(locator);
 element.clear();
 element.sendKeys(text);
 }
 
 protected String getText(By locator) {
 return findElement(locator).getText();
 }
 
 protected boolean isDisplayed(By locator) {
 try {
 return driver.findElement(locator).isDisplayed();
 } catch (NoSuchElementException e) {
 return false;
 }
 }
 
 protected void waitForPageLoad() {
 wait.until(webDriver -> 
 ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
 }
 
 // Navigation methods
 public String getCurrentUrl() {
 return driver.getCurrentUrl();
 }
 
 public String getTitle() {
 return driver.getTitle();
 }
}
```

### 2. Page Objects для конкретных страниц

```java
// Login page object
public class LoginPage extends BasePage {
 
 // Page elements
 private static final By EMAIL_FIELD = By.id("email");
 private static final By PASSWORD_FIELD = By.id("password");
 private static final By LOGIN_BUTTON = By.id("login-button");
 private static final By ERROR_MESSAGE = By.className("error-message");
 private static final By FORGOT_PASSWORD_LINK = By.linkText("Forgot Password?");
 private static final By REGISTER_LINK = By.linkText("Create Account");
 
 public LoginPage(WebDriver driver) {
 super(driver);
 // Verify we're on the login page
 wait.until(ExpectedConditions.urlContains("/login"));
 waitForPageLoad();
 }
 
 // Page actions
 public void enterEmail(String email) {
 typeText(EMAIL_FIELD, email);
 }
 
 public void enterPassword(String password) {
 typeText(PASSWORD_FIELD, password);
 }
 
 public DashboardPage clickLoginButton() {
 clickElement(LOGIN_BUTTON);
 return new DashboardPage(driver);
 }
 
 public LoginPage clickLoginButtonExpectingFailure() {
 clickElement(LOGIN_BUTTON);
 return this;
 }
 
 // Fluent interface for chaining
 public LoginPage typeEmail(String email) {
 enterEmail(email);
 return this;
 }
 
 public LoginPage typePassword(String password) {
 enterPassword(password);
 return this;
 }
 
 // Combined actions
 public DashboardPage login(String email, String password) {
 return typeEmail(email).typePassword(password).clickLoginButton();
 }
 
 public LoginPage loginExpectingFailure(String email, String password) {
 return typeEmail(email).typePassword(password).clickLoginButtonExpectingFailure();
 }
 
 // Page verifications
 public boolean isErrorMessageDisplayed() {
 return isDisplayed(ERROR_MESSAGE);
 }
 
 public String getErrorMessage() {
 return getText(ERROR_MESSAGE);
 }
 
 public boolean isEmailFieldDisplayed() {
 return isDisplayed(EMAIL_FIELD);
 }
 
 public boolean isPasswordFieldDisplayed() {
 return isDisplayed(PASSWORD_FIELD);
 }
 
 // Navigation to other pages
 public ForgotPasswordPage clickForgotPassword() {
 clickElement(FORGOT_PASSWORD_LINK);
 return new ForgotPasswordPage(driver);
 }
 
 public RegisterPage clickRegisterLink() {
 clickElement(REGISTER_LINK);
 return new RegisterPage(driver);
 }
}
```

```java
// Dashboard page object
public class DashboardPage extends BasePage {
 
 private static final By WELCOME_MESSAGE = By.className("welcome-message");
 private static final By USER_MENU = By.id("user-menu");
 private static final By LOGOUT_BUTTON = By.id("logout-button");
 private static final By DASHBOARD_CARDS = By.className("dashboard-card");
 private static final By NOTIFICATIONS_BELL = By.id("notifications-bell");
 private static final By NOTIFICATION_COUNT = By.className("notification-count");
 
 public DashboardPage(WebDriver driver) {
 super(driver);
 // Verify we're on the dashboard
 wait.until(ExpectedConditions.urlContains("/dashboard"));
 waitForPageLoad();
 }
 
 // Page information
 public String getWelcomeMessage() {
 return getText(WELCOME_MESSAGE);
 }
 
 public int getNotificationCount() {
 if (isDisplayed(NOTIFICATION_COUNT)) {
 String countText = getText(NOTIFICATION_COUNT);
 return Integer.parseInt(countText);
 }
 return 0;
 }
 
 public List<String> getDashboardCardTitles() {
 return driver.findElements(DASHBOARD_CARDS).stream().map(card -> card.findElement(By.className("card-title")).getText()).collect(Collectors.toList());
 }
 
 // Page actions
 public LoginPage clickLogout() {
 // Hover over user menu first
 actions.moveToElement(findElement(USER_MENU)).perform();
 
 clickElement(LOGOUT_BUTTON);
 return new LoginPage(driver);
 }
 
 public DashboardPage clickNotificationBell() {
 clickElement(NOTIFICATIONS_BELL);
 return this;
 }
 
 public boolean isNotificationPanelDisplayed() {
 return isDisplayed(By.id("notification-panel"));
 }
 
 // Page verifications
 public boolean isUserLoggedIn() {
 return isDisplayed(WELCOME_MESSAGE) && 
 getCurrentUrl().contains("/dashboard");
 }
 
 public boolean isDashboardLoaded() {
 return isDisplayed(WELCOME_MESSAGE) && 
 driver.findElements(DASHBOARD_CARDS).size() > 0;
 }
 
 public boolean hasNotifications() {
 return getNotificationCount() > 0;
 }
}
```

### 3. Page Factory паттерн

```java
// Using Selenium PageFactory
public class LoginPageWithFactory {
 
 private WebDriver driver;
 
 // Page elements with @FindBy annotation
 @FindBy(id = "email")
 private WebElement emailField;
 
 @FindBy(id = "password")
 private WebElement passwordField;
 
 @FindBy(id = "login-button")
 private WebElement loginButton;
 
 @FindBy(className = "error-message")
 private WebElement errorMessage;
 
 @FindBy(linkText = "Forgot Password?")
 private WebElement forgotPasswordLink;
 
 // List of elements
 @FindBy(className = "form-field")
 private List<WebElement> formFields;
 
 public LoginPageWithFactory(WebDriver driver) {
 this.driver = driver;
 PageFactory.initElements(driver, this);
 
 // Verify we're on the correct page
 new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/login"));
 }
 
 public void enterEmail(String email) {
 emailField.clear();
 emailField.sendKeys(email);
 }
 
 public void enterPassword(String password) {
 passwordField.clear();
 passwordField.sendKeys(password);
 }
 
 public DashboardPage clickLoginButton() {
 loginButton.click();
 return new DashboardPage(driver);
 }
 
 public String getErrorMessage() {
 return errorMessage.getText();
 }
 
 public boolean isErrorMessageDisplayed() {
 try {
 return errorMessage.isDisplayed();
 } catch (NoSuchElementException e) {
 return false;
 }
 }
 
 public ForgotPasswordPage clickForgotPassword() {
 forgotPasswordLink.click();
 return new ForgotPasswordPage(driver);
 }
 
 public int getFormFieldCount() {
 return formFields.size();
 }
}
```

### 4. Component Objects для переиспользуемых элементов

```java
// Reusable header component
public class HeaderComponent {
 
 private WebDriver driver;
 private WebDriverWait wait;
 
 private static final By USER_MENU = By.id("user-menu");
 private static final By LOGOUT_BUTTON = By.id("logout-button");
 private static final By NOTIFICATIONS_BELL = By.id("notifications-bell");
 private static final By SEARCH_FIELD = By.id("search-field");
 private static final By SEARCH_BUTTON = By.id("search-button");
 
 public HeaderComponent(WebDriver driver) {
 this.driver = driver;
 this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 }
 
 public LoginPage logout() {
 // Click user menu dropdown
 clickElement(USER_MENU);
 
 // Click logout
 clickElement(LOGOUT_BUTTON);
 
 return new LoginPage(driver);
 }
 
 public HeaderComponent search(String query) {
 typeText(SEARCH_FIELD, query);
 clickElement(SEARCH_BUTTON);
 return this;
 }
 
 public boolean hasNotifications() {
 return isDisplayed(NOTIFICATIONS_BELL) && 
 isDisplayed(By.className("notification-indicator"));
 }
 
 public int getNotificationCount() {
 if (hasNotifications()) {
 WebElement countElement = driver.findElement(By.className("notification-count"));
 return Integer.parseInt(countElement.getText());
 }
 return 0;
 }
 
 private WebElement findElement(By locator) {
 return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
 }
 
 private void clickElement(By locator) {
 WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
 element.click();
 }
 
 private void typeText(By locator, String text) {
 WebElement element = findElement(locator);
 element.clear();
 element.sendKeys(text);
 }
 
 private boolean isDisplayed(By locator) {
 try {
 return driver.findElement(locator).isDisplayed();
 } catch (NoSuchElementException e) {
 return false;
 }
 }
}

// Using component in page objects
public class DashboardPage extends BasePage {
 
 private HeaderComponent header;
 
 public DashboardPage(WebDriver driver) {
 super(driver);
 this.header = new HeaderComponent(driver);
 }
 
 public LoginPage logoutViaHeader() {
 return header.logout();
 }
 
 public DashboardPage search(String query) {
 header.search(query);
 return this;
 }
 
 public boolean hasNotificationsInHeader() {
 return header.hasNotifications();
 }
 
 public int getHeaderNotificationCount() {
 return header.getNotificationCount();
 }
}
```

### 5. Loadable Component паттерн

```java
// Loadable component pattern
public abstract class LoadableComponent<T extends LoadableComponent<T>> {
 
 protected WebDriver driver;
 protected WebDriverWait wait;
 
 public LoadableComponent(WebDriver driver) {
 this.driver = driver;
 this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 }
 
 // Template method pattern
 @SuppressWarnings("unchecked")
 public T load() {
 performLoad();
 return (T) this;
 }
 
 @SuppressWarnings("unchecked")
 public T get() {
 performLoad();
 isLoaded();
 return (T) this;
 }
 
 protected abstract void performLoad();
 protected abstract void isLoaded() throws Error;
}

// Login page as loadable component
public class LoginPageLoadable extends LoadableComponent<LoginPageLoadable> {
 
 private static final By EMAIL_FIELD = By.id("email");
 private static final By PASSWORD_FIELD = By.id("password");
 private static final By LOGIN_BUTTON = By.id("login-button");
 
 public LoginPageLoadable(WebDriver driver) {
 super(driver);
 }
 
 @Override
 protected void performLoad() {
 driver.get("http://localhost:8080/login");
 }
 
 @Override
 protected void isLoaded() throws Error {
 try {
 if (!driver.getCurrentUrl().contains("/login")) {
 throw new Error("Not on login page. Current URL: " + driver.getCurrentUrl());
 }
 
 if (!driver.findElement(EMAIL_FIELD).isDisplayed()) {
 throw new Error("Email field not found");
 }
 
 if (!driver.findElement(PASSWORD_FIELD).isDisplayed()) {
 throw new Error("Password field not found");
 }
 
 if (!driver.findElement(LOGIN_BUTTON).isDisplayed()) {
 throw new Error("Login button not found");
 }
 
 } catch (NoSuchElementException e) {
 throw new Error("Login page not loaded properly: " + e.getMessage());
 }
 }
 
 public void enterEmail(String email) {
 driver.findElement(EMAIL_FIELD).sendKeys(email);
 }
 
 public void enterPassword(String password) {
 driver.findElement(PASSWORD_FIELD).sendKeys(password);
 }
 
 public DashboardPageLoadable clickLoginButton() {
 driver.findElement(LOGIN_BUTTON).click();
 return new DashboardPageLoadable(driver).get();
 }
 
 public LoginPageLoadable login(String email, String password) {
 enterEmail(email);
 enterPassword(password);
 return clickLoginButton();
 }
}

// Dashboard page as loadable component
public class DashboardPageLoadable extends LoadableComponent<DashboardPageLoadable> {
 
 private static final By WELCOME_MESSAGE = By.className("welcome-message");
 
 public DashboardPageLoadable(WebDriver driver) {
 super(driver);
 }
 
 @Override
 protected void performLoad() {
 // Dashboard should be loaded by navigation, not direct URL
 throw new UnsupportedOperationException("Dashboard must be accessed via login");
 }
 
 @Override
 protected void isLoaded() throws Error {
 try {
 if (!driver.getCurrentUrl().contains("/dashboard")) {
 throw new Error("Not on dashboard page. Current URL: " + driver.getCurrentUrl());
 }
 
 WebElement welcomeElement = driver.findElement(WELCOME_MESSAGE);
 if (!welcomeElement.isDisplayed()) {
 throw new Error("Welcome message not displayed");
 }
 
 } catch (NoSuchElementException e) {
 throw new Error("Dashboard page not loaded properly");
 }
 }
 
 public String getWelcomeMessage() {
 return driver.findElement(WELCOME_MESSAGE).getText();
 }
 
 public boolean isUserLoggedIn() {
 return driver.findElement(WELCOME_MESSAGE).isDisplayed();
 }
}

// Test using loadable components
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoadableComponentTest {
 
 @Autowired
 private int port;
 
 private WebDriver driver;
 
 @BeforeEach
 void setUp() {
 driver = new ChromeDriver();
 driver.get("http://localhost:" + port);
 }
 
 @AfterEach
 void tearDown() {
 if (driver!= null) {
 driver.quit();
 }
 }
 
 @Test
 void shouldLoginUsingLoadableComponents() {
 // Load login page
 LoginPageLoadable loginPage = new LoginPageLoadable(driver).get();
 
 // Perform login
 DashboardPageLoadable dashboard = loginPage.login("user@example.com", "password");
 
 // Verify dashboard
 assertTrue(dashboard.isUserLoggedIn());
 assertEquals("Welcome, User!", dashboard.getWelcomeMessage());
 }
}
```

## Q6. Как организовать тестовые данные в автоматизации?

### 1. Test Data Management стратегии

```java
public enum TestDataStrategy {
 STATIC, // Fixed test data
 DYNAMIC, // Generated at runtime
 SHARED, // Shared between tests
 ISOLATED // Separate data per test
}
```

### 2. Static Test Data

```java
// Static test data class
public class TestData {
 
 public static final User JOHN_DOE = User.builder().email("john.doe@example.com").password("password123").firstName("John").lastName("Doe").build();
 
 public static final User JANE_SMITH = User.builder().email("jane.smith@example.com").password("password456").firstName("Jane").lastName("Smith").build();
 
 public static final Product LAPTOP = Product.builder().name("Gaming Laptop").price(BigDecimal.valueOf(1299.99)).category("Electronics").build();
 
 public static final Product BOOK = Product.builder().name("Java Programming Book").price(BigDecimal.valueOf(49.99)).category("Books").build();
 
 // Test scenarios
 public static final TestScenario VALID_LOGIN = new TestScenario(
 "Valid Login",
 JOHN_DOE,
 true,
 null
 );
 
 public static final TestScenario INVALID_LOGIN = new TestScenario(
 "Invalid Login",
 User.builder().email("invalid@example.com").password("wrong").build(),
 false,
 "Invalid credentials"
 );
}

public class TestScenario {
 private final String name;
 private final User user;
 private final boolean shouldSucceed;
 private final String expectedError;
 
 // constructor, getters...
}
```

### 3. Dynamic Test Data Generation

```java
// Dynamic test data generation
@Component
public class TestDataGenerator {
 
 private final Faker faker = new Faker();
 private final AtomicLong userIdCounter = new AtomicLong(1000);
 private final AtomicLong productIdCounter = new AtomicLong(2000);
 
 public User generateRandomUser() {
 String firstName = faker.name().firstName();
 String lastName = faker.name().lastName();
 String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + 
 "@example.com";
 
 return User.builder().id(userIdCounter.incrementAndGet()).email(email).password(faker.internet().password(8, 12)).firstName(firstName).lastName(lastName).phone(faker.phoneNumber().phoneNumber()).address(generateRandomAddress()).build();
 }
 
 public Product generateRandomProduct() {
 return Product.builder().id(productIdCounter.incrementAndGet()).name(faker.commerce().productName()).price(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000))).category(faker.commerce().department()).description(faker.lorem().sentence()).build();
 }
 
 public Order generateRandomOrder(User user, List<Product> products) {
 List<OrderItem> items = products.stream().map(product -> OrderItem.builder().product(product).quantity(faker.number().numberBetween(1, 5)).price(product.getPrice()).build()).collect(Collectors.toList());
 
 BigDecimal total = items.stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
 
 return Order.builder().user(user).items(items).total(total).shippingAddress(generateRandomAddress()).build();
 }
 
 public Address generateRandomAddress() {
 return Address.builder().street(faker.address().streetAddress()).city(faker.address().city()).state(faker.address().stateAbbr()).zipCode(faker.address().zipCode()).country("US").build();
 }
 
 // Generate test data based on specific criteria
 public List<User> generateUsers(int count, UserCriteria criteria) {
 return Stream.generate(this::generateRandomUser).filter(user -> matchesCriteria(user, criteria)).limit(count).collect(Collectors.toList());
 }
 
 public List<Product> generateProducts(int count, ProductCriteria criteria) {
 return Stream.generate(this::generateRandomProduct).filter(product -> matchesCriteria(product, criteria)).limit(count).collect(Collectors.toList());
 }
 
 private boolean matchesCriteria(User user, UserCriteria criteria) {
 if (criteria.getMinAge()!= null && user.getAge() < criteria.getMinAge()) {
 return false;
 }
 if (criteria.getState()!= null &&!user.getAddress().getState().equals(criteria.getState())) {
 return false;
 }
 return true;
 }
 
 private boolean matchesCriteria(Product product, ProductCriteria criteria) {
 if (criteria.getCategory()!= null &&!product.getCategory().equals(criteria.getCategory())) {
 return false;
 }
 if (criteria.getMinPrice()!= null && product.getPrice().compareTo(criteria.getMinPrice()) < 0) {
 return false;
 }
 return true;
 }
}

// Criteria classes
public class UserCriteria {
 private Integer minAge;
 private String state;
 // getters, setters, builder...
}

public class ProductCriteria {
 private String category;
 private BigDecimal minPrice;
 // getters, setters, builder...
}
```

### 4. Test Data Builders

```java
// Builder pattern for test data
public class UserBuilder {
 
 private Long id;
 private String email = "user@example.com";
 private String password = "password";
 private String firstName = "John";
 private String lastName = "Doe";
 private Integer age = 30;
 private Address address;
 private List<String> roles = Arrays.asList("USER");
 private boolean active = true;
 
 public static UserBuilder aUser() {
 return new UserBuilder();
 }
 
 public UserBuilder withId(Long id) {
 this.id = id;
 return this;
 }
 
 public UserBuilder withEmail(String email) {
 this.email = email;
 return this;
 }
 
 public UserBuilder withPassword(String password) {
 this.password = password;
 return this;
 }
 
 public UserBuilder withName(String firstName, String lastName) {
 this.firstName = firstName;
 this.lastName = lastName;
 return this;
 }
 
 public UserBuilder withAge(int age) {
 this.age = age;
 return this;
 }
 
 public UserBuilder withAddress(Address address) {
 this.address = address;
 return this;
 }
 
 public UserBuilder withRoles(String... roles) {
 this.roles = Arrays.asList(roles);
 return this;
 }
 
 public UserBuilder inactive() {
 this.active = false;
 return this;
 }
 
 public UserBuilder admin() {
 this.roles = Arrays.asList("ADMIN", "USER");
 return this;
 }
 
 public User build() {
 User user = new User();
 user.setId(id);
 user.setEmail(email);
 user.setPassword(password);
 user.setFirstName(firstName);
 user.setLastName(lastName);
 user.setAge(age);
 user.setAddress(address!= null? address: new AddressBuilder().build());
 user.setRoles(roles);
 user.setActive(active);
 return user;
 }
}

public class AddressBuilder {
 
 private String street = "123 Main St";
 private String city = "Anytown";
 private String state = "CA";
 private String zipCode = "12345";
 
 public AddressBuilder withStreet(String street) {
 this.street = street;
 return this;
 }
 
 public AddressBuilder withCity(String city) {
 this.city = city;
 return this;
 }
 
 public AddressBuilder withState(String state) {
 this.state = state;
 return this;
 }
 
 public AddressBuilder withZipCode(String zipCode) {
 this.zipCode = zipCode;
 return this;
 }
 
 public Address build() {
 Address address = new Address();
 address.setStreet(street);
 address.setCity(city);
 address.setState(state);
 address.setZipCode(zipCode);
 return address;
 }
}

// Using builders in tests
public class UserServiceTest {
 
 @Autowired
 private UserService userService;
 
 @Test
 void shouldCreateActiveUser() {
 User user = UserBuilder.aUser().withEmail("john@example.com").withName("John", "Doe").withAge(25).build();
 
 User created = userService.createUser(user);
 
 assertNotNull(created.getId());
 assertTrue(created.isActive());
 assertEquals("john@example.com", created.getEmail());
 }
 
 @Test
 void shouldCreateAdminUser() {
 User admin = UserBuilder.aUser().withEmail("admin@example.com").withName("Admin", "User").admin().build();
 
 User created = userService.createUser(admin);
 
 assertTrue(created.getRoles().contains("ADMIN"));
 assertTrue(created.getRoles().contains("USER"));
 }
 
 @Test
 void shouldNotCreateInactiveUser() {
 User inactiveUser = UserBuilder.aUser().withEmail("inactive@example.com").inactive().build();
 
 assertThrows(ValidationException.class, () -> {
 userService.createUser(inactiveUser);
 });
 }
}
```

### 5. Test Data Management в базах данных

```java
// Database test data setup
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseTestDataSetup {
 
 @Autowired
 private JdbcTemplate jdbcTemplate;
 
 @Autowired
 private UserRepository userRepository;
 
 @BeforeEach
 void setUpTestData() {
 // Clean up
 jdbcTemplate.execute("DELETE FROM orders");
 jdbcTemplate.execute("DELETE FROM users");
 
 // Insert test users
 jdbcTemplate.update("""
 INSERT INTO users (id, email, password, first_name, last_name, active) 
 VALUES (?,?,?,?,?,?)
 """, 
 1L, "john@example.com", "password", "John", "Doe", true);
 
 jdbcTemplate.update("""
 INSERT INTO users (id, email, password, first_name, last_name, active) 
 VALUES (?,?,?,?,?,?)
 """, 
 2L, "jane@example.com", "password", "Jane", "Smith", true);
 }
 
 @Test
 void shouldFindUserByEmail() {
 Optional<User> user = userRepository.findByEmail("john@example.com");
 
 assertTrue(user.isPresent());
 assertEquals("John", user.get().getFirstName());
 assertEquals("Doe", user.get().getLastName());
 }
}

// Using @Sql for data setup
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/test-data/users.sql")
@Sql(scripts = "/test-data/products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SqlBasedTest {
 
 @Autowired
 private UserRepository userRepository;
 
 @Autowired
 private ProductRepository productRepository;
 
 @Test
 void shouldLoadDataFromSqlFiles() {
 List<User> users = userRepository.findAll();
 List<Product> products = productRepository.findAll();
 
 assertEquals(3, users.size());
 assertEquals(5, products.size());
 }
}

// users.sql
/*
INSERT INTO users (id, email, password, first_name, last_name, active) VALUES
(1, 'john@example.com', 'password', 'John', 'Doe', true),
(2, 'jane@example.com', 'password', 'Jane', 'Smith', true),
(3, 'bob@example.com', 'password', 'Bob', 'Wilson', false);
*/

// products.sql
/*
INSERT INTO products (id, name, price, category) VALUES
(1, 'Laptop', 1299.99, 'Electronics'),
(2, 'Book', 29.99, 'Books'),
(3, 'Phone', 799.99, 'Electronics'),
(4, 'Tablet', 499.99, 'Electronics'),
(5, 'Headphones', 99.99, 'Electronics');
*/
```

### 6. Data-Driven Testing

```java
// CSV-based test data
@ParameterizedTest
@CsvFileSource(resources = "/test-data/login-scenarios.csv", numLinesToSkip = 1)
void shouldHandleVariousLoginScenarios(String email, String password, 
 boolean shouldSucceed, String expectedMessage) {
 
 LoginPage loginPage = new LoginPage(driver);
 
 loginPage.enterEmail(email);
 loginPage.enterPassword(password);
 
 if (shouldSucceed) {
 DashboardPage dashboard = loginPage.clickLoginButton();
 assertTrue(dashboard.isUserLoggedIn());
 } else {
 loginPage.clickLoginButtonExpectingFailure();
 assertTrue(loginPage.isErrorMessageDisplayed());
 assertEquals(expectedMessage, loginPage.getErrorMessage());
 }
}

// login-scenarios.csv
/*
email,password,shouldSucceed,expectedMessage
john@example.com,password,true,
invalid@example.com,password,false,Invalid credentials
john@example.com,wrongpassword,false,Invalid credentials,,false,Email and password are required
john@example.com,,false,Password is required
@example.com,password,false,Invalid email format
*/

// JSON-based test data
public class JsonTestDataLoader {
 
 private final ObjectMapper objectMapper = new ObjectMapper();
 
 public List<LoginTestCase> loadLoginTestCases() throws IOException {
 InputStream inputStream = getClass().getResourceAsStream("/test-data/login-cases.json");
 return objectMapper.readValue(inputStream, 
 objectMapper.getTypeFactory().constructCollectionType(List.class, LoginTestCase.class));
 }
}

public class LoginTestCase {
 private String email;
 private String password;
 private boolean shouldSucceed;
 private String expectedMessage;
 
 // getters and setters...
}

// login-cases.json
/*
[
 {
 "email": "john@example.com",
 "password": "password",
 "shouldSucceed": true
 },
 {
 "email": "invalid@example.com",
 "password": "password",
 "shouldSucceed": false,
 "expectedMessage": "Invalid credentials"
 }
]
*/

@ParameterizedTest
@MethodSource("loginTestCases")
void shouldHandleLoginFromJson(LoginTestCase testCase) {
 LoginPage loginPage = new LoginPage(driver);
 
 loginPage.enterEmail(testCase.getEmail());
 loginPage.enterPassword(testCase.getPassword());
 
 if (testCase.isShouldSucceed()) {
 DashboardPage dashboard = loginPage.clickLoginButton();
 assertTrue(dashboard.isUserLoggedIn());
 } else {
 loginPage.clickLoginButtonExpectingFailure();
 assertEquals(testCase.getExpectedMessage(), loginPage.getErrorMessage());
 }
}

static Stream<LoginTestCase> loginTestCases() throws IOException {
 JsonTestDataLoader loader = new JsonTestDataLoader();
 return loader.loadLoginTestCases().stream();
}
```

## Q7. Как интегрировать автоматизацию в CI/CD?

### 1. GitHub Actions Pipeline

```yaml
#.github/workflows/automated-tests.yml
name: Automated Testing Pipeline

on:
 push:
 branches: [ main, develop ]
 pull_request:
 branches: [ main ]

jobs:
 test:
 runs-on: ubuntu-latest
 strategy:
 matrix:
 test-type: [unit, integration, api]
 
 services:
 postgres:
 image: postgres:13
 env:
 POSTGRES_DB: testdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
 ports:
 - 5432:5432
 options: >-
 --health-cmd pg_isready
 --health-interval 10s
 --health-timeout 5s
 --health-retries 5
 
 rabbitmq:
 image: rabbitmq:3-management
 ports:
 - 5672:5672
 - 15672:15672
 
 steps:
 - name: Checkout code
 uses: actions/checkout@v3
 
 - name: Set up JDK 17
 uses: actions/setup-java@v3
 with:
 java-version: '17'
 distribution: 'temurin'
 cache: maven
 
 - name: Cache Maven dependencies
 uses: actions/cache@v3
 with:
 path:/.m2/repository
 key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
 restore-keys: |
 ${{ runner.os }}-maven-
 
 - name: Run ${{ matrix.test-type }} tests
 run: mvn test -Dtest="*${{ matrix.test-type }}*Test" -Dspring.profiles.active=test
 env:
 DATABASE_URL: jdbc:postgresql://localhost:5432/testdb
 RABBITMQ_HOST: localhost
 
 - name: Publish test results
 uses: actions/upload-artifact@v3
 if: always()
 with:
 name: ${{ matrix.test-type }}-test-results
 path: |
 target/surefire-reports/
 target/failsafe-reports/
 
 - name: Generate test report
 uses: dorny/test-reporter@v1
 if: always()
 with:
 name: ${{ matrix.test-type }} Tests
 path: 'target/surefire-reports/*.xml'
 reporter: java-junit
```

### 2. Jenkins Pipeline

```groovy
// Jenkinsfile
pipeline {
 agent any
 
 stages {
 stage('Checkout') {
 steps {
 git branch: 'main', url: 'https://github.com/company/project.git'
 }
 }
 
 stage('Unit Tests') {
 steps {
 sh 'mvn test -Dtest="*UnitTest"'
 }
 post {
 always {
 junit 'target/surefire-reports/*.xml'
 publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')]
 }
 }
 }
 
 stage('Integration Tests') {
 steps {
 script {
 docker.image('postgres:13').withRun('-e POSTGRES_DB=testdb -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test') { c ->
 docker.image('rabbitmq:3-management').withRun { r ->
 sh 'mvn verify -Dtest="*IT" -Dspring.profiles.active=test'
 }
 }
 }
 }
 post {
 always {
 junit 'target/failsafe-reports/*.xml'
 }
 }
 }
 
 stage('API Tests') {
 steps {
 sh 'mvn test -Dtest="*ApiTest"'
 }
 }
 
 stage('UI Tests') {
 steps {
 sh '''
 # Start application
 mvn spring-boot:run &
 APP_PID=$!
 
 # Wait for app to start
 timeout 60 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
 
 # Run UI tests
 mvn test -Dtest="*UITest"
 
 # Stop application
 kill $APP_PID
 '''
 }
 }
 
 stage('Performance Tests') {
 steps {
 sh 'mvn test -Dtest="*PerformanceTest"'
 }
 post {
 always {
 publishPerformanceReport()
 }
 }
 }
 
 stage('Security Tests') {
 steps {
 sh 'mvn test -Dtest="*SecurityTest"'
 sh 'mvn org.owasp:dependency-check-maven:check'
 }
 }
 }
 
 post {
 always {
 // Archive test artifacts
 archiveArtifacts artifacts: 'target/**/*.log', allowEmptyArchive: true
 
 // Send notifications
 script {
 def results = currentBuild.result?: 'SUCCESS'
 if (results == 'FAILURE') {
 slackSend channel: '#testing', 
 color: 'danger', 
 message: "Tests failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
 }
 }
 }
 }
}
```

### 3. Parallel Test Execution

```yaml
# Parallel test execution in GitHub Actions
jobs:
 test-parallel:
 runs-on: ubuntu-latest
 strategy:
 matrix:
 test-group: [1, 2, 3, 4, 5]
 
 steps:
 - uses: actions/checkout@v3
 
 - name: Set up JDK
 uses: actions/setup-java@v3
 with:
 java-version: '17'
 distribution: 'temurin'
 
 - name: Run parallel tests
 run: |
 # Calculate test groups
 TOTAL_GROUPS=5
 TESTS_PER_GROUP=$(( $(find. -name "*Test.java" | wc -l) / TOTAL_GROUPS + 1 ))
 
 # Find tests for this group
 TEST_FILES=$(find. -name "*Test.java" | \
 awk "NR % $TOTAL_GROUPS == ${{ matrix.test-group }} - 1 {print}")
 
 # Run tests
 if [! -z "$TEST_FILES" ]; then
 mvn test -Dtest=$(echo $TEST_FILES | tr '\n' ',')
 fi
```

### 4. Test Environments Management

```yaml
# Multi-environment testing
jobs:
 test-environments:
 runs-on: ubuntu-latest
 strategy:
 matrix:
 environment: [dev, staging, prod]
 
 environment:
 name: ${{ matrix.environment }}
 
 steps:
 - uses: actions/checkout@v3
 
 - name: Set up JDK
 uses: actions/setup-java@v3
 with:
 java-version: '17'
 
 - name: Run tests against ${{ matrix.environment }}
 run: |
 case ${{ matrix.environment }} in
 dev)
 BASE_URL="https://dev-api.company.com";;
 staging)
 BASE_URL="https://staging-api.company.com";;
 prod)
 BASE_URL="https://api.company.com";;
 esac
 
 mvn test -Dtest="*ApiTest" -Dbase.url=$BASE_URL
```

### 5. Test Results Analysis и Reporting

```java
@Configuration
public class TestReportingConfig {
 
 @Bean
 public TestExecutionListener testExecutionListener() {
 return new TestExecutionListener() {
 
 @Override
 public void executionStarted(TestIdentifier testIdentifier) {
 // Log test start
 logger.info("Starting test: {}", testIdentifier.getDisplayName());
 metrics.counter("test.started").increment();
 }
 
 @Override
 public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
 // Log test result
 if (result.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
 logger.info("Test passed: {}", testIdentifier.getDisplayName());
 metrics.counter("test.passed").increment();
 } else {
 logger.error("Test failed: {} - {}", testIdentifier.getDisplayName(), 
 result.getThrowable().get().getMessage());
 metrics.counter("test.failed").increment();
 }
 
 // Record execution time
 long duration = getTestDuration(testIdentifier);
 metrics.timer("test.duration").record(duration, TimeUnit.MILLISECONDS);
 }
 };
 }
 
 @Bean
 public TestReporter testReporter() {
 return testResult -> {
 // Send results to external systems
 if (testResult.getStatus() == Status.FAILED) {
 slackService.sendMessage("Test failed: " + testResult.getDisplayName());
 jiraService.createBug(testResult);
 }
 };
 }
}

// Custom test watcher
public class TestWatcherExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
 
 private final Map<String, Long> testStartTimes = new ConcurrentHashMap<>();
 
 @Override
 public void beforeTestExecution(ExtensionContext context) {
 testStartTimes.put(context.getDisplayName(), System.currentTimeMillis());
 
 // Log test start with thread info
 logger.info("Starting test: {} on thread: {}", 
 context.getDisplayName(), 
 Thread.currentThread().getName());
 }
 
 @Override
 public void afterTestExecution(ExtensionContext context) {
 long startTime = testStartTimes.remove(context.getDisplayName());
 long duration = System.currentTimeMillis() - startTime;
 
 TestExecutionResult result = context.getExecutionException().map(e -> TestExecutionResult.failed(e)).orElse(TestExecutionResult.successful());
 
 // Store test result
 testResultRepository.save(new TestResult(
 context.getDisplayName(),
 result.getStatus().name(),
 duration,
 result.getThrowable().orElse(null)
 ));
 
 // Send to monitoring
 metricsService.recordTestResult(context.getDisplayName(), result, duration);
 }
}
```

## Q8. Как поддерживать автоматизированные тесты?

### 1. Test Maintenance Strategies

```java
public class TestMaintenanceStrategy {
 
 public enum MaintenanceAction {
 FIX_IMMEDIATELY, // Critical failures
 SCHEDULE_FIX, // Important but not blocking
 DEPRECATE, // Test no longer relevant
 QUARANTINE, // Temporarily disable
 REFACTOR // Test needs improvement
 }
 
 public MaintenanceAction determineAction(TestFailure failure) {
 if (failure.isCritical() && failure.blocksDeployment()) {
 return MaintenanceAction.FIX_IMMEDIATELY;
 }
 
 if (failure.isDueToCodeChange() && failure.hasSimpleFix()) {
 return MaintenanceAction.SCHEDULE_FIX;
 }
 
 if (failure.isFlaky() && failure.canBeQuarantined()) {
 return MaintenanceAction.QUARANTINE;
 }
 
 if (failure.isOutdated() && failure.noLongerProvidesValue()) {
 return MaintenanceAction.DEPRECATE;
 }
 
 return MaintenanceAction.REFACTOR;
 }
 
 public void applyMaintenanceAction(TestFailure failure, MaintenanceAction action) {
 switch (action) {
 case FIX_IMMEDIATELY:
 fixTestImmediately(failure);
 break;
 case SCHEDULE_FIX:
 scheduleTestFix(failure);
 break;
 case DEPRECATE:
 deprecateTest(failure);
 break;
 case QUARANTINE:
 quarantineTest(failure);
 break;
 case REFACTOR:
 refactorTest(failure);
 break;
 }
 }
}
```

### 2. Flaky Test Management

```java
// Flaky test detection and quarantine
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlakyTest {
 int maxRetries() default 3;
 String reason() default "";
}

public class FlakyTestExtension implements TestExecutionExceptionHandler {
 
 @Override
 public void handleTestExecutionException(ExtensionContext context, Throwable throwable) 
 throws Throwable {
 
 FlakyTest flakyAnnotation = context.getRequiredTestMethod().getAnnotation(FlakyTest.class);
 if (flakyAnnotation!= null) {
 handleFlakyTest(context, throwable, flakyAnnotation);
 } else {
 throw throwable;
 }
 }
 
 private void handleFlakyTest(ExtensionContext context, Throwable originalException, 
 FlakyTest flakyAnnotation) throws Throwable {
 
 int maxRetries = flakyAnnotation.maxRetries();
 String reason = flakyAnnotation.reason();
 
 logger.warn("Detected flaky test: {} - Reason: {}", 
 context.getDisplayName(), reason);
 
 for (int attempt = 1; attempt <= maxRetries; attempt++) {
 try {
 logger.info("Retrying flaky test {} (attempt {}/{})", 
 context.getDisplayName(), attempt, maxRetries);
 
 // Retry the test
 context.getRequiredTestMethod().invoke(context.getRequiredTestInstance());
 
 logger.info("Flaky test passed on retry {}: {}", 
 attempt, context.getDisplayName());
 return;
 
 } catch (Throwable retryException) {
 logger.warn("Flaky test retry {} failed: {}", 
 attempt, context.getDisplayName());
 
 if (attempt == maxRetries) {
 // All retries failed
 logger.error("Flaky test failed all {} retries: {}", 
 maxRetries, context.getDisplayName());
 
 // Report to monitoring system
 flakyTestReporter.reportFlakyTest(context, originalException, reason);
 
 throw originalException;
 }
 
 // Wait before retry
 Thread.sleep(1000 * attempt);
 }
 }
 }
}

// Usage
public class FlakyUITest {
 
 @Test
 @FlakyTest(maxRetries = 2, reason = "Intermittent network issues")
 void shouldLoadDashboard() {
 // Test that sometimes fails due to network issues
 driver.get("https://example.com/dashboard");
 assertTrue(driver.findElement(By.className("dashboard")).isDisplayed());
 }
}
```

### 3. Test Code Quality

```java
// Test code quality rules
public class TestCodeQualityChecker {
 
 public List<QualityViolation> checkTestQuality(Class<?> testClass) {
 List<QualityViolation> violations = new ArrayList<>();
 
 violations.addAll(checkTestNaming(testClass));
 violations.addAll(checkTestStructure(testClass));
 violations.addAll(checkTestIsolation(testClass));
 violations.addAll(checkTestDataManagement(testClass));
 
 return violations;
 }
 
 private List<QualityViolation> checkTestNaming(Class<?> testClass) {
 List<QualityViolation> violations = new ArrayList<>();
 
 for (Method method: testClass.getMethods()) {
 if (method.isAnnotationPresent(Test.class)) {
 String methodName = method.getName();
 
 // Check naming convention
 if (!methodName.startsWith("should") &&!methodName.startsWith("test")) {
 violations.add(new QualityViolation(
 Severity.WARNING,
 "Test method '" + methodName + "' should start with 'should' or follow naming convention"
 ));
 }
 
 // Check for underscores
 if (methodName.contains("_")) {
 violations.add(new QualityViolation(
 Severity.INFO,
 "Consider using camelCase instead of underscores in test name: " + methodName
 ));
 }
 }
 }
 
 return violations;
 }
 
 private List<QualityViolation> checkTestStructure(Class<?> testClass) {
 List<QualityViolation> violations = new ArrayList<>();
 
 boolean hasSetup = Arrays.stream(testClass.getMethods()).anyMatch(m -> m.isAnnotationPresent(BeforeEach.class) || 
 m.isAnnotationPresent(BeforeAll.class));
 
 boolean hasTeardown = Arrays.stream(testClass.getMethods()).anyMatch(m -> m.isAnnotationPresent(AfterEach.class) || 
 m.isAnnotationPresent(AfterAll.class));
 
 if (!hasSetup) {
 violations.add(new QualityViolation(
 Severity.WARNING,
 "Test class should have setup method (@BeforeEach or @BeforeAll)"
 ));
 }
 
 if (!hasTeardown) {
 violations.add(new QualityViolation(
 Severity.WARNING,
 "Test class should have teardown method (@AfterEach or @AfterAll)"
 ));
 }
 
 return violations;
 }
 
 private List<QualityViolation> checkTestIsolation(Class<?> testClass) {
 List<QualityViolation> violations = new ArrayList<>();
 
 // Check for shared state
 if (hasSharedState(testClass)) {
 violations.add(new QualityViolation(
 Severity.ERROR,
 "Test class has shared state that may cause test interference"
 ));
 }
 
 // Check for proper cleanup
 if (!hasProperCleanup(testClass)) {
 violations.add(new QualityViolation(
 Severity.WARNING,
 "Test class may not properly clean up after tests"
 ));
 }
 
 return violations;
 }
 
 private List<QualityViolation> checkTestDataManagement(Class<?> testClass) {
 List<QualityViolation> violations = new ArrayList<>();
 
 // Check for hard-coded test data
 if (hasHardCodedData(testClass)) {
 violations.add(new QualityViolation(
 Severity.INFO,
 "Consider using test data builders or factories instead of hard-coded data"
 ));
 }
 
 // Check for data isolation
 if (!hasDataIsolation(testClass)) {
 violations.add(new QualityViolation(
 Severity.WARNING,
 "Test may not properly isolate test data"
 ));
 }
 
 return violations;
 }
}

// Quality violation model
public class QualityViolation {
 public enum Severity { INFO, WARNING, ERROR }
 
 private final Severity severity;
 private final String message;
 
 public QualityViolation(Severity severity, String message) {
 this.severity = severity;
 this.message = message;
 }
 
 // getters...
}
```

### 4. Test Refactoring

```java
// Before refactoring
public class UserServiceTest {
 
 @Test
 void testCreateUser() {
 User user = new User();
 user.setEmail("test@example.com");
 user.setPassword("password");
 user.setFirstName("Test");
 user.setLastName("User");
 
 User created = userService.createUser(user);
 
 assertNotNull(created.getId());
 assertEquals("test@example.com", created.getEmail());
 assertEquals("Test", created.getFirstName());
 assertEquals("User", created.getLastName());
 assertNotNull(created.getCreatedAt());
 }
 
 @Test
 void testCreateUserWithInvalidEmail() {
 User user = new User();
 user.setEmail("invalid-email");
 user.setPassword("password");
 
 try {
 userService.createUser(user);
 fail("Should have thrown exception");
 } catch (ValidationException e) {
 assertEquals("Invalid email format", e.getMessage());
 }
 }
}

// After refactoring
public class UserServiceTest {
 
 @Autowired
 private UserService userService;
 
 @Test
 void shouldCreateUserWithValidData() {
 // Given
 User user = UserBuilder.aUser().withEmail("john@example.com").withName("John", "Doe").build();
 
 // When
 User created = userService.createUser(user);
 
 // Then
 assertThat(created).isNotNull().extracting(User::getId, User::getEmail, User::getFirstName, User::getLastName).containsExactly(created.getId(), "john@example.com", "John", "Doe");
 
 assertThat(created.getCreatedAt()).isNotNull();
 }
 
 @Test
 void shouldRejectUserWithInvalidEmail() {
 // Given
 User user = UserBuilder.aUser().withEmail("invalid-email").build();
 
 // When & Then
 assertThatThrownBy(() -> userService.createUser(user)).isInstanceOf(ValidationException.class).hasMessage("Invalid email format");
 }
 
 @Test
 void shouldRejectUserWithDuplicateEmail() {
 // Given
 User existingUser = UserBuilder.aUser().withEmail("existing@example.com").build();
 userService.createUser(existingUser);
 
 User duplicateUser = UserBuilder.aUser().withEmail("existing@example.com").build();
 
 // When & Then
 assertThatThrownBy(() -> userService.createUser(duplicateUser)).isInstanceOf(ValidationException.class).hasMessage("Email already exists");
 }
}
```

### 5. Test Documentation

```java
/**
 * Test suite for UserService functionality.
 * 
 * This test class covers the main user management operations:
 * - User creation and validation
 * - User authentication
 * - Password management
 * - User profile updates
 * 
 * Test Data Strategy:
 * - Uses UserBuilder for test data creation
 * - Each test is isolated with its own data
 * - Database is cleaned between tests
 * 
 * Dependencies:
 * - UserService (SUT)
 * - UserRepository (mocked for unit tests)
 * - EmailService (mocked for unit tests)
 * - TestContainers PostgreSQL (for integration tests)
 * 
 * @author Test Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootTest
@DisplayName("UserService Test Suite")
public class UserServiceTest {
 
 // Test constants
 private static final String VALID_EMAIL = "john@example.com";
 private static final String VALID_PASSWORD = "password123";
 private static final String INVALID_EMAIL = "invalid-email";
 
 @Autowired
 private UserService userService;
 
 @MockBean
 private UserRepository userRepository;
 
 @MockBean
 private EmailService emailService;
 
 private User testUser;
 
 @BeforeEach
 void setUp() {
 // Initialize test data
 testUser = UserBuilder.aUser().withEmail(VALID_EMAIL).withPassword(VALID_PASSWORD).build();
 
 // Setup common mocks
 when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
 User user = invocation.getArgument(0);
 user.setId(1L);
 user.setCreatedAt(LocalDateTime.now());
 return user;
 });
 }
 
 @Nested
 @DisplayName("User Creation")
 class UserCreationTests {
 
 @Test
 @DisplayName("Should create user with valid data")
 void shouldCreateUserWithValidData() {
 // Given: Valid user data
 User user = UserBuilder.aUser().withEmail("newuser@example.com").withName("New", "User").build();
 
 // When: Creating the user
 User created = userService.createUser(user);
 
 // Then: User should be created successfully
 assertAll("User creation validation",
 () -> assertThat(created.getId()).isNotNull(),
 () -> assertThat(created.getEmail()).isEqualTo("newuser@example.com"),
 () -> assertThat(created.getCreatedAt()).isNotNull(),
 () -> assertThat(created.isActive()).isTrue()
 );
 
 // And: Welcome email should be sent
 verify(emailService).sendWelcomeEmail("newuser@example.com");
 }
 
 @Test
 @DisplayName("Should reject user with invalid email")
 void shouldRejectUserWithInvalidEmail() {
 // Given: User with invalid email
 User user = UserBuilder.aUser().withEmail(INVALID_EMAIL).build();
 
 // When & Then: Creating the user should fail
 assertThatThrownBy(() -> userService.createUser(user)).isInstanceOf(ValidationException.class).hasMessage("Invalid email format");
 
 // And: User should not be saved
 verify(userRepository, never()).save(any(User.class));
 
 // And: No email should be sent
 verify(emailService, never()).sendWelcomeEmail(anyString());
 }
 }
 
 @Nested
 @DisplayName("User Authentication")
 class UserAuthenticationTests {
 
 @BeforeEach
 void setUpAuthentication() {
 // Setup authentication mocks
 when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
 when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
 }
 
 @Test
 @DisplayName("Should authenticate user with valid credentials")
 void shouldAuthenticateUserWithValidCredentials() {
 // When: Authenticating with valid credentials
 AuthenticationResult result = userService.authenticate(VALID_EMAIL, VALID_PASSWORD);
 
 // Then: Authentication should succeed
 assertThat(result.isSuccessful()).isTrue();
 assertThat(result.getUser()).isEqualTo(testUser);
 }
 
 @Test
 @DisplayName("Should reject authentication with invalid password")
 void shouldRejectAuthenticationWithInvalidPassword() {
 // When: Authenticating with invalid password
 AuthenticationResult result = userService.authenticate(VALID_EMAIL, "wrongpassword");
 
 // Then: Authentication should fail
 assertThat(result.isSuccessful()).isFalse();
 assertThat(result.getErrorMessage()).isEqualTo("Invalid credentials");
 }
 }
}
```

## Q9. Какие паттерны используются в test automation?

### 1. Factory Pattern для Test Objects

```java
public interface TestObjectFactory<T> {
 T create();
 T create(Map<String, Object> properties);
}

public class UserFactory implements TestObjectFactory<User> {
 
 private final Faker faker = new Faker();
 
 @Override
 public User create() {
 return create(Collections.emptyMap());
 }
 
 @Override
 public User create(Map<String, Object> properties) {
 User user = new User();
 user.setEmail(getProperty(properties, "email", faker.internet().emailAddress()));
 user.setPassword(getProperty(properties, "password", faker.internet().password()));
 user.setFirstName(getProperty(properties, "firstName", faker.name().firstName()));
 user.setLastName(getProperty(properties, "lastName", faker.name().lastName()));
 user.setActive(getProperty(properties, "active", true));
 return user;
 }
 
 @SuppressWarnings("unchecked")
 private <T> T getProperty(Map<String, Object> properties, String key, T defaultValue) {
 return (T) properties.getOrDefault(key, defaultValue);
 }
}

public class TestDataFactory {
 
 private static final UserFactory userFactory = new UserFactory();
 
 public static User createUser() {
 return userFactory.create();
 }
 
 public static User createUser(String email) {
 return userFactory.create(Map.of("email", email));
 }
 
 public static User createInactiveUser() {
 return userFactory.create(Map.of("active", false));
 }
 
 public static List<User> createUsers(int count) {
 return Stream.generate(userFactory::create).limit(count).collect(Collectors.toList());
 }
}
```

### 2. Strategy Pattern для Test Execution

```java
public interface TestExecutionStrategy {
 TestResult execute(TestCase testCase);
 boolean canExecute(TestCase testCase);
}

public class UnitTestExecutionStrategy implements TestExecutionStrategy {
 
 @Override
 public TestResult execute(TestCase testCase) {
 // Execute unit test
 long startTime = System.currentTimeMillis();
 try {
 // Run JUnit test
 JUnitCore junit = new JUnitCore();
 Result result = junit.run(testCase.getTestClass());
 
 return TestResult.builder().testCase(testCase).passed(result.wasSuccessful()).executionTime(System.currentTimeMillis() - startTime).failureCount(result.getFailureCount()).errorMessages(result.getFailures().stream().map(failure -> failure.getMessage()).collect(Collectors.toList())).build();
 
 } catch (Exception e) {
 return TestResult.builder().testCase(testCase).passed(false).executionTime(System.currentTimeMillis() - startTime).errorMessages(List.of(e.getMessage())).build();
 }
 }
 
 @Override
 public boolean canExecute(TestCase testCase) {
 return testCase.getType() == TestType.UNIT;
 }
}

public class ApiTestExecutionStrategy implements TestExecutionStrategy {
 
 private final RestTemplate restTemplate;
 
 public ApiTestExecutionStrategy(RestTemplate restTemplate) {
 this.restTemplate = restTemplate;
 }
 
 @Override
 public TestResult execute(TestCase testCase) {
 long startTime = System.currentTimeMillis();
 
 try {
 // Execute API test scenario
 ApiTestScenario scenario = (ApiTestScenario) testCase;
 
 for (ApiCall call: scenario.getCalls()) {
 HttpMethod method = HttpMethod.valueOf(call.getMethod());
 ResponseEntity<String> response = restTemplate.exchange(
 call.getUrl(), method, 
 new HttpEntity<>(call.getBody(), call.getHeaders()), 
 String.class);
 
 // Validate response
 if (call.getExpectedStatus()!= response.getStatusCode().value()) {
 return TestResult.builder().testCase(testCase).passed(false).executionTime(System.currentTimeMillis() - startTime).errorMessages(List.of("Expected status " + call.getExpectedStatus() + 
 " but got " + response.getStatusCode().value())).build();
 }
 }
 
 return TestResult.builder().testCase(testCase).passed(true).executionTime(System.currentTimeMillis() - startTime).build();
 
 } catch (Exception e) {
 return TestResult.builder().testCase(testCase).passed(false).executionTime(System.currentTimeMillis() - startTime).errorMessages(List.of(e.getMessage())).build();
 }
 }
 
 @Override
 public boolean canExecute(TestCase testCase) {
 return testCase.getType() == TestType.API;
 }
}

public class TestExecutionStrategyFactory {
 
 private final Map<TestType, TestExecutionStrategy> strategies = new HashMap<>();
 
 public TestExecutionStrategyFactory(List<TestExecutionStrategy> strategyList) {
 for (TestExecutionStrategy strategy: strategyList) {
 for (TestType type: TestType.values()) {
 if (strategy.canExecute(new TestCase("", type))) {
 strategies.put(type, strategy);
 }
 }
 }
 }
 
 public TestResult executeTest(TestCase testCase) {
 TestExecutionStrategy strategy = strategies.get(testCase.getType());
 if (strategy == null) {
 throw new IllegalArgumentException("No strategy found for test type: " + testCase.getType());
 }
 return strategy.execute(testCase);
 }
}
```

### 3. Template Method Pattern для Test Structure

```java
public abstract class AbstractTestTemplate {
 
 protected final Logger logger = LoggerFactory.getLogger(getClass());
 
 // Template method
 public final TestResult execute() {
 TestResult result = new TestResult();
 long startTime = System.currentTimeMillis();
 
 try {
 // Setup phase
 setUp();
 
 // Pre-conditions verification
 verifyPreConditions();
 
 // Execute test
 executeTest();
 
 // Post-conditions verification
 verifyPostConditions();
 
 // Cleanup
 tearDown();
 
 result.setPassed(true);
 
 } catch (AssertionError e) {
 logger.error("Test assertion failed", e);
 result.setPassed(false);
 result.setErrorMessage(e.getMessage());
 
 } catch (Exception e) {
 logger.error("Test execution failed", e);
 result.setPassed(false);
 result.setErrorMessage("Unexpected error: " + e.getMessage());
 
 } finally {
 result.setExecutionTime(System.currentTimeMillis() - startTime);
 
 // Always cleanup
 try {
 emergencyCleanup();
 } catch (Exception e) {
 logger.warn("Emergency cleanup failed", e);
 }
 }
 
 return result;
 }
 
 // Abstract methods to be implemented by subclasses
 protected abstract void setUp() throws Exception;
 protected abstract void verifyPreConditions() throws Exception;
 protected abstract void executeTest() throws Exception;
 protected abstract void verifyPostConditions() throws Exception;
 protected abstract void tearDown() throws Exception;
 
 // Hook method for emergency cleanup
 protected void emergencyCleanup() throws Exception {
 // Default implementation does nothing
 }
}

public class UserCreationTest extends AbstractTestTemplate {
 
 private User testUser;
 private User createdUser;
 
 @Override
 protected void setUp() throws Exception {
 // Initialize test data
 testUser = UserBuilder.aUser().withEmail("test@example.com").withName("Test", "User").build();
 
 // Setup database state if needed
 databaseSetup.ensureCleanState();
 }
 
 @Override
 protected void verifyPreConditions() throws Exception {
 // Verify that user doesn't exist
 assertFalse(userRepository.existsByEmail(testUser.getEmail()));
 
 // Verify system is ready
 assertTrue(healthCheck.isDatabaseAvailable());
 }
 
 @Override
 protected void executeTest() throws Exception {
 // Execute the main test action
 createdUser = userService.createUser(testUser);
 }
 
 @Override
 protected void verifyPostConditions() throws Exception {
 // Verify user was created correctly
 assertNotNull(createdUser.getId());
 assertEquals(testUser.getEmail(), createdUser.getEmail());
 assertNotNull(createdUser.getCreatedAt());
 
 // Verify user exists in database
 Optional<User> savedUser = userRepository.findById(createdUser.getId());
 assertTrue(savedUser.isPresent());
 assertEquals(createdUser, savedUser.get());
 
 // Verify welcome email was sent
 verify(emailService).sendWelcomeEmail(testUser.getEmail());
 }
 
 @Override
 protected void tearDown() throws Exception {
 // Clean up test data
 if (createdUser!= null && createdUser.getId()!= null) {
 userRepository.deleteById(createdUser.getId());
 }
 }
 
 @Override
 protected void emergencyCleanup() throws Exception {
 // Emergency cleanup in case of test failure
 try {
 userRepository.deleteByEmail(testUser.getEmail());
 } catch (Exception e) {
 logger.warn("Emergency cleanup failed for user: " + testUser.getEmail());
 }
 }
}
```

### 4. Observer Pattern для Test Monitoring

```java
public interface TestExecutionListener {
 void testStarted(TestCase testCase);
 void testFinished(TestCase testCase, TestResult result);
 void testFailed(TestCase testCase, Throwable error);
}

public class TestExecutionMonitor {
 
 private final List<TestExecutionListener> listeners = new ArrayList<>();
 
 public void addListener(TestExecutionListener listener) {
 listeners.add(listener);
 }
 
 public void removeListener(TestExecutionListener listener) {
 listeners.remove(listener);
 }
 
 public TestResult executeTest(TestCase testCase) {
 // Notify listeners that test started
 listeners.forEach(listener -> listener.testStarted(testCase));
 
 TestResult result = null;
 try {
 // Execute the test
 result = testExecutionStrategy.execute(testCase);
 
 // Notify listeners of completion
 listeners.forEach(listener -> listener.testFinished(testCase, result));
 
 } catch (Throwable error) {
 result = TestResult.builder().testCase(testCase).passed(false).errorMessage(error.getMessage()).build();
 
 // Notify listeners of failure
 listeners.forEach(listener -> listener.testFailed(testCase, error));
 }
 
 return result;
 }
}

@Component
public class TestMetricsListener implements TestExecutionListener {
 
 private final MeterRegistry meterRegistry;
 
 @Override
 public void testStarted(TestCase testCase) {
 meterRegistry.counter("test.started", 
 "type", testCase.getType().name(),
 "category", testCase.getCategory()).increment();
 }
 
 @Override
 public void testFinished(TestCase testCase, TestResult result) {
 meterRegistry.timer("test.execution.time",
 "type", testCase.getType().name(),
 "result", result.isPassed()? "success": "failure").record(result.getExecutionTime(), TimeUnit.MILLISECONDS);
 
 if (result.isPassed()) {
 meterRegistry.counter("test.passed",
 "type", testCase.getType().name()).increment();
 } else {
 meterRegistry.counter("test.failed",
 "type", testCase.getType().name()).increment();
 }
 }
 
 @Override
 public void testFailed(TestCase testCase, Throwable error) {
 meterRegistry.counter("test.error",
 "type", testCase.getType().name(),
 "error_type", error.getClass().getSimpleName()).increment();
 }
}

@Component
public class TestNotificationListener implements TestExecutionListener {
 
 private final SlackService slackService;
 
 @Override
 public void testStarted(TestCase testCase) {
 // Log test start for important tests
 if (testCase.isCritical()) {
 slackService.sendMessage("🚀 Started critical test: " + testCase.getName());
 }
 }
 
 @Override
 public void testFinished(TestCase testCase, TestResult result) {
 if (!result.isPassed() && testCase.isCritical()) {
 slackService.sendMessage("❌ Critical test failed: " + testCase.getName() + 
 "\nError: " + result.getErrorMessage());
 }
 }
 
 @Override
 public void testFailed(TestCase testCase, Throwable error) {
 if (testCase.isCritical()) {
 slackService.sendMessage("💥 Critical test crashed: " + testCase.getName() + 
 "\nException: " + error.getMessage());
 }
 }
}
```

## Q10. Как измерить ROI от test automation?

### 1. ROI Calculation Framework

```java
public class TestAutomationROI {
 
 public ROICalculation calculateROI(TestAutomationMetrics metrics, 
 TimePeriod period) {
 
 // Calculate benefits
 double timeSavings = calculateTimeSavings(metrics, period);
 double qualityImprovements = calculateQualityImprovements(metrics, period);
 double riskReduction = calculateRiskReduction(metrics, period);
 
 double totalBenefits = timeSavings + qualityImprovements + riskReduction;
 
 // Calculate costs
 double initialInvestment = calculateInitialInvestment(metrics);
 double maintenanceCosts = calculateMaintenanceCosts(metrics, period);
 double toolCosts = calculateToolCosts(metrics, period);
 
 double totalCosts = initialInvestment + maintenanceCosts + toolCosts;
 
 // Calculate ROI
 double netBenefits = totalBenefits - totalCosts;
 double roi = totalCosts > 0? (netBenefits / totalCosts) * 100: 0;
 
 return ROICalculation.builder().timeSavings(timeSavings).qualityImprovements(qualityImprovements).riskReduction(riskReduction).totalBenefits(totalBenefits).initialInvestment(initialInvestment).maintenanceCosts(maintenanceCosts).toolCosts(toolCosts).totalCosts(totalCosts).netBenefits(netBenefits).roi(roi).breakEvenPeriod(calculateBreakEvenPeriod(totalCosts, netBenefits)).build();
 }
 
 private double calculateTimeSavings(TestAutomationMetrics metrics, TimePeriod period) {
 // Time saved per test execution
 double manualExecutionTime = metrics.getAverageManualTestTimeMinutes();
 double automatedExecutionTime = metrics.getAverageAutomatedTestTimeMinutes();
 double timeSavedPerExecution = manualExecutionTime - automatedExecutionTime;
 
 // Number of executions in period
 int executionsInPeriod = calculateExecutionsInPeriod(metrics, period);
 
 // Apply learning curve (automation gets faster over time)
 double learningFactor = calculateLearningFactor(period);
 
 return timeSavedPerExecution * executionsInPeriod * learningFactor;
 }
 
 private double calculateQualityImprovements(TestAutomationMetrics metrics, TimePeriod period) {
 // Reduced defect leakage
 double defectLeakageReduction = metrics.getDefectLeakageReductionPercent() / 100.0;
 double averageDefectCost = metrics.getAverageDefectCost();
 int defectsPrevented = (int) (metrics.getTotalDefectsFound() * defectLeakageReduction);
 
 // Improved test coverage
 double coverageIncrease = metrics.getTestCoverageIncreasePercent() / 100.0;
 double coverageBenefit = metrics.getCoverageBenefitValue();
 
 return (defectsPrevented * averageDefectCost) + 
 (coverageIncrease * coverageBenefit * period.getMonths());
 }
 
 private double calculateRiskReduction(TestAutomationMetrics metrics, TimePeriod period) {
 // Reduced production incidents
 double incidentReduction = metrics.getProductionIncidentReductionPercent() / 100.0;
 double averageIncidentCost = metrics.getAverageIncidentCost();
 int incidentsPrevented = (int) (metrics.getAverageMonthlyIncidents() * incidentReduction * period.getMonths());
 
 // Improved confidence in releases
 double confidenceImprovement = metrics.getReleaseConfidenceImprovementPercent() / 100.0;
 
 return (incidentsPrevented * averageIncidentCost) + 
 (confidenceImprovement * metrics.getConfidenceBenefitValue());
 }
 
 private double calculateInitialInvestment(TestAutomationMetrics metrics) {
 return metrics.getInitialSetupCost() + 
 metrics.getTrainingCost() + 
 metrics.getFrameworkDevelopmentCost();
 }
 
 private double calculateMaintenanceCosts(TestAutomationMetrics metrics, TimePeriod period) {
 double monthlyMaintenance = metrics.getMonthlyMaintenanceCost();
 double maintenanceGrowthRate = metrics.getMaintenanceGrowthRatePercent() / 100.0;
 
 double totalMaintenance = 0;
 for (int month = 1; month <= period.getMonths(); month++) {
 totalMaintenance += monthlyMaintenance * Math.pow(1 + maintenanceGrowthRate, month - 1);
 }
 
 return totalMaintenance;
 }
 
 private double calculateToolCosts(TestAutomationMetrics metrics, TimePeriod period) {
 return metrics.getMonthlyToolCost() * period.getMonths();
 }
 
 private int calculateExecutionsInPeriod(TestAutomationMetrics metrics, TimePeriod period) {
 return metrics.getTestsExecutedPerMonth() * period.getMonths();
 }
 
 private double calculateLearningFactor(TimePeriod period) {
 // Learning curve: efficiency increases over time
 double initialEfficiency = 0.8; // 80% efficiency initially
 double finalEfficiency = 0.95; // 95% efficiency after learning
 double monthsToLearn = 6; // 6 months to reach full efficiency
 
 if (period.getMonths() <= monthsToLearn) {
 return initialEfficiency + 
 (finalEfficiency - initialEfficiency) * (period.getMonths() / monthsToLearn);
 } else {
 return finalEfficiency;
 }
 }
 
 private double calculateBreakEvenPeriod(double totalCosts, double monthlyNetBenefits) {
 if (monthlyNetBenefits <= 0) {
 return Double.POSITIVE_INFINITY; // Never breaks even
 }
 return totalCosts / (monthlyNetBenefits * 12); // Convert to years
 }
}
```

### 2. Metrics Collection

```java
@Component
public class TestAutomationMetricsCollector {
 
 private final TestExecutionRepository testExecutionRepository;
 private final DefectRepository defectRepository;
 private final IncidentRepository incidentRepository;
 private final MeterRegistry meterRegistry;
 
 @Scheduled(fixedRate = 86400000) // Daily
 public void collectDailyMetrics() {
 LocalDate today = LocalDate.now();
 LocalDate yesterday = today.minusDays(1);
 
 // Collect test execution metrics
 int testsExecuted = testExecutionRepository.countByDate(yesterday);
 int testsPassed = testExecutionRepository.countPassedByDate(yesterday);
 long averageExecutionTime = testExecutionRepository.getAverageExecutionTimeByDate(yesterday);
 
 // Collect defect metrics
 int defectsFound = defectRepository.countByDate(yesterday);
 int defectsFromAutomation = defectRepository.countFoundByAutomationByDate(yesterday);
 
 // Collect incident metrics
 int productionIncidents = incidentRepository.countByDate(yesterday);
 
 // Record metrics
 meterRegistry.gauge("automation.tests.executed", testsExecuted);
 meterRegistry.gauge("automation.tests.pass.rate", (double) testsPassed / testsExecuted);
 meterRegistry.timer("automation.test.execution.time").record(averageExecutionTime, TimeUnit.MILLISECONDS);
 meterRegistry.counter("automation.defects.found").increment(defectsFound);
 meterRegistry.gauge("automation.defect.leakage.rate", 
 (double) defectsFromAutomation / defectsFound);
 meterRegistry.counter("automation.incidents.prevented").increment(productionIncidents);
 }
 
 public TestAutomationMetrics getMetricsForPeriod(LocalDate startDate, LocalDate endDate) {
 return TestAutomationMetrics.builder().totalTestsExecuted(testExecutionRepository.countByDateBetween(startDate, endDate)).averageTestExecutionTime(testExecutionRepository.getAverageExecutionTimeBetween(startDate, endDate)).defectsFound(defectRepository.countByDateBetween(startDate, endDate)).productionIncidents(incidentRepository.countByDateBetween(startDate, endDate)).testCoverage(getCurrentTestCoverage()).automationPercentage(getAutomationPercentage()).build();
 }
 
 private double getCurrentTestCoverage() {
 // Implementation to get current test coverage from JaCoCo or similar
 return jacocoService.getCurrentCoverage();
 }
 
 private double getAutomationPercentage() {
 int totalTests = testRepository.countAll();
 int automatedTests = testRepository.countAutomated();
 
 return totalTests > 0? (double) automatedTests / totalTests * 100: 0;
 }
}
```

### 3. ROI Dashboard

```java
@RestController
@RequestMapping("/api/roi")
public class ROIDashboardController {
 
 private final TestAutomationROI roiCalculator;
 private final TestAutomationMetricsCollector metricsCollector;
 
 @GetMapping("/current")
 public ROIDashboard getCurrentROI() {
 TestAutomationMetrics metrics = metricsCollector.getMetricsForPeriod(
 LocalDate.now().minusMonths(12), LocalDate.now());
 
 ROICalculation roi = roiCalculator.calculateROI(metrics, TimePeriod.ofMonths(12));
 
 return ROIDashboard.builder().currentROI(roi).metrics(metrics).trends(calculateTrends(metrics)).recommendations(generateRecommendations(roi)).build();
 }
 
 @GetMapping("/projection/{months}")
 public ROIProjection getROIProjection(@PathVariable int months) {
 TestAutomationMetrics currentMetrics = metricsCollector.getMetricsForPeriod(
 LocalDate.now().minusMonths(6), LocalDate.now());
 
 // Project future metrics based on trends
 TestAutomationMetrics projectedMetrics = projectMetrics(currentMetrics, months);
 
 ROICalculation projectedROI = roiCalculator.calculateROI(
 projectedMetrics, TimePeriod.ofMonths(months));
 
 return ROIProjection.builder().projectedROI(projectedROI).projectedMetrics(projectedMetrics).confidenceLevel(calculateConfidenceLevel(months)).build();
 }
 
 private List<ROITrend> calculateTrends(TestAutomationMetrics metrics) {
 // Calculate month-over-month trends
 List<ROITrend> trends = new ArrayList<>();
 
 for (int i = 1; i <= 12; i++) {
 LocalDate startDate = LocalDate.now().minusMonths(i + 1);
 LocalDate endDate = LocalDate.now().minusMonths(i);
 
 TestAutomationMetrics monthlyMetrics = metricsCollector.getMetricsForPeriod(startDate, endDate);
 ROICalculation monthlyROI = roiCalculator.calculateROI(monthlyMetrics, TimePeriod.ofMonths(1));
 
 trends.add(ROITrend.builder().month(startDate.getMonth()).roi(monthlyROI.getRoi()).metrics(monthlyMetrics).build());
 }
 
 return trends;
 }
 
 private List<String> generateRecommendations(ROICalculation roi) {
 List<String> recommendations = new ArrayList<>();
 
 if (roi.getRoi() < 50) {
 recommendations.add("ROI is below target. Consider improving test execution speed or reducing maintenance costs.");
 }
 
 if (roi.getMaintenanceCosts() > roi.getTotalBenefits() * 0.3) {
 recommendations.add("Maintenance costs are too high. Review test stability and reduce flaky tests.");
 }
 
 if (roi.getBreakEvenPeriod() > 2) {
 recommendations.add("Break-even period is too long. Focus on high-value test automation.");
 }
 
 return recommendations;
 }
 
 private TestAutomationMetrics projectMetrics(TestAutomationMetrics current, int months) {
 // Simple projection based on current trends
 return TestAutomationMetrics.builder().totalTestsExecuted((int) (current.getTotalTestsExecuted() * Math.pow(1.1, months))).averageTestExecutionTime(current.getAverageTestExecutionTime() * 0.95) // 5% improvement.defectsFound((int) (current.getDefectsFound() * Math.pow(0.9, months))) // 10% reduction.testCoverage(Math.min(85, current.getTestCoverage() + months)) // Up to 85%.build();
 }
 
 private double calculateConfidenceLevel(int months) {
 // Confidence decreases with projection length
 return Math.max(0.5, 1.0 - (months * 0.05));
 }
}
```

### 4. ROI Reporting

```java
@Service
public class ROIReportGenerator {
 
 private final TestAutomationROI roiCalculator;
 private final TestAutomationMetricsCollector metricsCollector;
 private final ReportService reportService;
 
 @Scheduled(cron = "0 0 1 * *") // First day of each month
 public void generateMonthlyROIReport() {
 LocalDate reportDate = LocalDate.now().minusMonths(1);
 TimePeriod reportPeriod = TimePeriod.ofMonths(1);
 
 TestAutomationMetrics metrics = metricsCollector.getMetricsForPeriod(
 reportDate.withDayOfMonth(1), 
 reportDate.withDayOfMonth(reportDate.lengthOfMonth()));
 
 ROICalculation roi = roiCalculator.calculateROI(metrics, reportPeriod);
 
 // Generate report
 ROIReport report = ROIReport.builder().reportDate(reportDate).period(reportPeriod).metrics(metrics).roi(roi).summary(generateSummary(roi)).charts(generateCharts(metrics, roi)).recommendations(generateRecommendations(roi)).build();
 
 // Save and distribute report
 reportService.saveReport(report);
 reportService.emailReport(report, getStakeholders());
 reportService.publishToDashboard(report);
 }
 
 private String generateSummary(ROICalculation roi) {
 StringBuilder summary = new StringBuilder();
 summary.append("Test Automation ROI Report\n");
 summary.append("========================\n\n");
 summary.append(String.format("ROI: %.1f%%\n", roi.getRoi()));
 summary.append(String.format("Total Benefits: $%.2f\n", roi.getTotalBenefits()));
 summary.append(String.format("Total Costs: $%.2f\n", roi.getTotalCosts()));
 summary.append(String.format("Net Benefits: $%.2f\n", roi.getNetBenefits()));
 summary.append(String.format("Break-even Period: %.1f years\n\n", roi.getBreakEvenPeriod()));
 
 if (roi.getRoi() > 100) {
 summary.append("🎉 Excellent ROI! Test automation is highly profitable.\n");
 } else if (roi.getRoi() > 50) {
 summary.append("✅ Good ROI. Test automation provides solid returns.\n");
 } else if (roi.getRoi() > 0) {
 summary.append("⚠️ Positive ROI, but consider optimization opportunities.\n");
 } else {
 summary.append("❌ Negative ROI. Review automation strategy.\n");
 }
 
 return summary.toString();
 }
 
 private List<ChartData> generateCharts(TestAutomationMetrics metrics, ROICalculation roi) {
 List<ChartData> charts = new ArrayList<>();
 
 // ROI over time chart
 charts.add(ChartData.builder().title("ROI Trend").type(ChartType.LINE).data(generateROITrendData()).build());
 
 // Cost vs Benefits chart
 charts.add(ChartData.builder().title("Costs vs Benefits").type(ChartType.BAR).data(Map.of(
 "Costs", roi.getTotalCosts(),
 "Benefits", roi.getTotalBenefits())).build());
 
 // Test execution metrics
 charts.add(ChartData.builder().title("Test Execution Metrics").type(ChartType.PIE).data(Map.of(
 "Passed", metrics.getTestsPassed(),
 "Failed", metrics.getTestsFailed(),
 "Skipped", metrics.getTestsSkipped())).build());
 
 return charts;
 }
 
 private List<String> generateRecommendations(ROICalculation roi) {
 List<String> recommendations = new ArrayList<>();
 
 if (roi.getMaintenanceCosts() > roi.getTotalBenefits() * 0.4) {
 recommendations.add("High maintenance costs detected. Focus on improving test stability and reducing flaky tests.");
 }
 
 if (roi.getBreakEvenPeriod() > 1.5) {
 recommendations.add("Consider prioritizing high-impact, low-effort automation opportunities to reduce break-even time.");
 }
 
 if (roi.getRoi() < 75) {
 recommendations.add("ROI could be improved by increasing test execution frequency or expanding automation coverage.");
 }
 
 recommendations.add("Continue monitoring automation metrics and adjust strategy based on ROI trends.");
 
 return recommendations;
 }
 
 private List<String> getStakeholders() {
 return Arrays.asList(
 "test-automation-team@company.com",
 "engineering-managers@company.com",
 "product-owners@company.com",
 "qa-directors@company.com"
 );
 }
 
 private Map<String, Double> generateROITrendData() {
 // Implementation to get historical ROI data
 return roiHistoryService.getLast12MonthsROI();
 }
}
```

## Заключение

Test automation является мощным инструментом для повышения качества и эффективности тестирования. Senior Java Developer должен понимать различные фреймворки автоматизации, паттерны проектирования тестов, стратегии CI/CD интеграции и подходы к поддержке автоматизированных тестов. Измерение ROI помогает обосновать инвестиции в автоматизацию и оптимизировать стратегию тестирования.

