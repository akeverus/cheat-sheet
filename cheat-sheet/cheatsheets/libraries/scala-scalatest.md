# ScalaTest

ScalaTest - это гибкий и мощный фреймворк для тестирования Scala приложений. Предоставляет множество стилей тестирования, богатый набор матчеров и отличную интеграцию с популярными инструментами сборки и CI/CD системами.

## Содержание

- [Основы ScalaTest](#основы-scalatest)
  - [Подключение и базовая структура](#подключение-и-базовая-структура)
  - [Стили тестирования](#стили-тестирования)
- [Матчеры (Matchers)](#матчеры-matchers)
  - [Should матчеры](#should-матчеры)
  - [Must матчеры](#must-матчеры)
  - [Кастомные матчеры](#кастомные-матчеры)
- [Хуки Before/After](#хуки-beforeafter)
  - [FunSuite с lifecycle](#funsuite-с-lifecycle)
  - [FlatSpec с fixture](#flatspec-с-fixture)
- [Мокирование](#мокирование)
  - [Mockito integration](#mockito-integration)
  - [ScalaMock integration](#scalamock-integration)
- [Property-based тестирование](#property-based-тестирование)
  - [ScalaCheck integration](#scalacheck-integration)
- [Async тестирование](#async-тестирование)
  - [ScalaFutures для Future](#scalafutures-для-future)
  - [Cats Effect IO тестирование](#cats-effect-io-тестирование)
- [Тестирование веб-приложений](#тестирование-веб-приложений)
  - [Play Framework integration](#play-framework-integration)
- [Интеграция с другими инструментами](#интеграция-с-другими-инструментами)
  - [JUnit integration](#junit-integration)
  - [TestNG integration](#testng-integration)
- [Лучшие практики](#лучшие-практики)
  - [Организация тестов](#организация-тестов)
  - [Тестовые утилиты и хелперы](#тестовые-утилиты-и-хелперы)
  - [Продвинутые возможности](#продвинутые-возможности)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Performance Tips](#performance-tips)
- [Руководство по миграции](#руководство-по-миграции)
  - [From JUnit to ScalaTest](#from-junit-to-scalatest)
  - [From Specs2 to ScalaTest](#from-specs2-to-scalatest)

## Основы ScalaTest

### Подключение и базовая структура
```scala
// build.sbt
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.15",
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0", // Mockito integration
  "org.scalatestplus" %% "selenium-4-1" % "3.2.15.0", // Selenium integration
  "org.scalatestplus" %% "junit-4-13" % "3.2.15.0"    // JUnit integration
)

// scalatest.properties (опционально, для конфигурации)
// reporter = org.scalatest.tools.HTMLReporter
// slowpoke = 1000
```

### Стили тестирования

#### FunSuite - функциональный стиль
```scala
import org.scalatest.funsuite.AnyFunSuite

/**
 * FunSuite - функциональный стиль тестирования в ScalaTest
 * Напоминает JUnit - простой и понятный стиль
 * AnyFunSuite - базовый класс для FunSuite стиля
 */
class CalculatorFunSuite extends AnyFunSuite {
  // Наследование от AnyFunSuite предоставляет метод test()

  // Создание экземпляра тестируемого класса
  val calculator = new Calculator()

  // test("описание") { тело теста } - определение теста
  // Описание должно быть понятным и описывать что тестируется
  test("addition should work correctly") {
    // assert(условие) - проверка условия
    // Если условие false, тест падает с AssertionError
    assert(calculator.add(2, 3) == 5)
    // Проверяем что сложение 2 и 3 дает 5
  }

  test("subtraction should work correctly") {
    assert(calculator.subtract(5, 3) == 2)
    // Проверяем что вычитание 3 из 5 дает 2
  }

  test("division by zero should throw exception") {
    // assertThrows[T] { код } - проверка что код выбрасывает исключение типа T
    // Если исключение не выброшено или другого типа, тест падает
    assertThrows[ArithmeticException] {
      calculator.divide(10, 0)  // Деление на ноль должно выбросить ArithmeticException
    }
    // Тест проходит если ArithmeticException был выброшен
  }

  test("multiplication should be commutative") {
    // Тест проверяет математическое свойство (коммутативность умножения)
    val a = 3
    val b = 4
    // Умножение должно быть коммутативным: a * b == b * a
    assert(calculator.multiply(a, b) == calculator.multiply(b, a))
    // Проверяем что результат не зависит от порядка операндов
  }
}
```

#### FlatSpec - спецификационный стиль
```scala
import org.scalatest.flatspec.AnyFlatSpec

class CalculatorFlatSpec extends AnyFlatSpec {

  val calculator = new Calculator()

  "A Calculator" should "add two numbers correctly" in {
    assert(calculator.add(2, 3) == 5)
  }

  it should "subtract two numbers correctly" in {
    assert(calculator.subtract(5, 3) == 2)
  }

  it should "multiply two numbers correctly" in {
    assert(calculator.multiply(3, 4) == 12)
  }

  "Division" should "work for non-zero divisors" in {
    assert(calculator.divide(10, 2) == 5)
  }

  it should "throw ArithmeticException when dividing by zero" in {
    assertThrows[ArithmeticException] {
      calculator.divide(10, 0)
    }
  }
}
```

#### WordSpec - BDD стиль
```scala
import org.scalatest.wordspec.AnyWordSpec

class CalculatorWordSpec extends AnyWordSpec {

  val calculator = new Calculator()

  "A Calculator" when {
    "adding numbers" should {
      "return the sum of two positive numbers" in {
        assert(calculator.add(2, 3) == 5)
      }

      "return the correct result when one number is zero" in {
        assert(calculator.add(5, 0) == 5)
      }

      "handle negative numbers correctly" in {
        assert(calculator.add(-2, 3) == 1)
      }
    }

    "subtracting numbers" should {
      "return the difference between two numbers" in {
        assert(calculator.subtract(5, 3) == 2)
      }
    }

    "multiplying numbers" should {
      "return the product of two numbers" in {
        assert(calculator.multiply(3, 4) == 12)
      }

      "be commutative" in {
        val a = 3
        val b = 4
        assert(calculator.multiply(a, b) == calculator.multiply(b, a))
      }
    }
  }
}
```

#### FunSpec - еще один BDD стиль
```scala
import org.scalatest.funspec.AnyFunSpec

class CalculatorFunSpec extends AnyFunSpec {

  val calculator = new Calculator()

  describe("A Calculator") {

    describe("when adding numbers") {
      it("should return the sum of two positive numbers") {
        assert(calculator.add(2, 3) == 5)
      }

      it("should handle zero correctly") {
        assert(calculator.add(5, 0) == 5)
      }
    }

    describe("when dividing numbers") {
      it("should divide positive numbers correctly") {
        assert(calculator.divide(10, 2) == 5)
      }

      it("should throw an exception when dividing by zero") {
        assertThrows[ArithmeticException] {
          calculator.divide(10, 0)
        }
      }
    }
  }
}
```

## Матчеры (Matchers)

### Should матчеры
```scala
import org.scalatest.matchers.should.Matchers

class MatchersExample extends AnyFunSpec with Matchers {

  val calculator = new Calculator()
  val list = List(1, 2, 3, 4, 5)

  describe("Matchers") {

    it("should demonstrate equality matchers") {
      calculator.add(2, 3) shouldBe 5
      calculator.add(2, 3) shouldEqual 5
      calculator.add(2, 3) should === (5)
    }

    it("should demonstrate comparison matchers") {
      calculator.multiply(3, 4) should be > 10
      calculator.multiply(3, 4) should be >= 12
      calculator.multiply(3, 4) should be < 15
      calculator.multiply(3, 4) should be <= 12
    }

    it("should demonstrate boolean matchers") {
      (calculator.add(2, 3) == 5) shouldBe true
      List(1, 2, 3).isEmpty shouldBe false
    }

    it("should demonstrate collection matchers") {
      list should have size 5
      list should contain (3)
      list should not contain (6)
      list should be (sorted)

      List(1, 2, 3) should contain allOf (1, 2)
      List(1, 2, 3) should contain oneOf (1, 4)
      List(1, 2, 3) should contain noneOf (4, 5)
    }

    it("should demonstrate string matchers") {
      "Hello World" should startWith ("Hello")
      "Hello World" should endWith ("World")
      "Hello World" should include ("lo Wo")

      "Hello" should have length 5
      "  hello  ".trim should fullyMatch regex ("[a-z]+")
    }

    it("should demonstrate option matchers") {
      Some(42) shouldBe defined
      None should not be defined

      Some(42) should contain (42)
      Some("hello") should be ('defined)
    }

    it("should demonstrate exception matchers") {
      an [ArithmeticException] should be thrownBy {
        calculator.divide(10, 0)
      }

      the [ArithmeticException] thrownBy {
        calculator.divide(10, 0)
      } should have message "/ by zero"
    }
  }
}
```

### Must матчеры
```scala
import org.scalatest.matchers.must.Matchers

class MustMatchersExample extends AnyFunSpec with MustMatchers {

  val calculator = new Calculator()

  describe("Must matchers") {

    it("must demonstrate equality") {
      calculator.add(2, 3) mustBe 5
      calculator.add(2, 3) mustEqual 5
    }

    it("must demonstrate collections") {
      List(1, 2, 3) must contain (2)
      List(1, 2, 3) must have size 3
      List(1, 2, 3) must not be empty
    }

    it("must demonstrate exceptions") {
      a [RuntimeException] must be thrownBy {
        throw new RuntimeException("test")
      }
    }
  }
}
```

### Кастомные матчеры
```scala
import org.scalatest.matchers.{Matcher, MatchResult}

class CustomMatchersExample extends AnyFunSpec with Matchers {

  // Кастомный матчер для проверки положительных чисел
  def bePositive = Matcher { (left: Int) =>
    MatchResult(
      left > 0,
      s"$left was not positive",
      s"$left was positive"
    )
  }

  // Матчер для проверки валидных email
  def beValidEmail = Matcher { (left: String) =>
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r
    MatchResult(
      emailRegex.findFirstIn(left).isDefined,
      s"$left was not a valid email",
      s"$left was a valid email"
    )
  }

  // Матчер для проверки отсортированности
  def beSorted[T](implicit ord: Ordering[T]) = Matcher { (left: Seq[T]) =>
    val isSorted = left.zip(left.tail).forall(ord.lteq)
    MatchResult(
      isSorted,
      s"$left was not sorted",
      s"$left was sorted"
    )
  }

  describe("Custom matchers") {

    it("should validate positive numbers") {
      5 must bePositive
      -1 must not be bePositive
    }

    it("should validate email addresses") {
      "user@example.com" must beValidEmail
      "invalid-email" must not be beValidEmail
    }

    it("should check if sequence is sorted") {
      List(1, 2, 3, 4) must beSorted
      List(1, 3, 2, 4) must not be beSorted
    }
  }
}
```

## Хуки Before/After

### FunSuite с lifecycle
```scala
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.funsuite.AnyFunSuite

class LifecycleFunSuite extends AnyFunSuite with BeforeAndAfter with BeforeAndAfterAll {

  var calculator: Calculator = _

  override def beforeAll(): Unit = {
    println("Setting up test suite")
    // Инициализация ресурсов для всего suite
  }

  override def afterAll(): Unit = {
    println("Tearing down test suite")
    // Очистка ресурсов для всего suite
  }

  before {
    println("Setting up test")
    calculator = new Calculator()
  }

  after {
    println("Tearing down test")
    calculator = null
  }

  test("addition works") {
    assert(calculator.add(2, 3) == 5)
  }

  test("subtraction works") {
    assert(calculator.subtract(5, 3) == 2)
  }
}
```

### FlatSpec с fixture
```scala
import org.scalatest.flatspec.AnyFlatSpec

class FixtureFlatSpec extends AnyFlatSpec {

  // Fixture method
  def withCalculator(test: Calculator => Any): Unit = {
    val calculator = new Calculator()
    try {
      test(calculator)
    } finally {
      // cleanup if needed
    }
  }

  "Calculator with fixture" should "add correctly" in withCalculator { calc =>
    assert(calc.add(2, 3) == 5)
  }

  it should "subtract correctly" in withCalculator { calc =>
    assert(calc.subtract(5, 3) == 2)
  }
}

// Loan pattern fixture
class LoanFixtureSpec extends AnyFlatSpec {

  case class Fixture(calculator: Calculator, database: TestDatabase)

  def withFixture(test: Fixture => Any): Unit = {
    val fixture = Fixture(new Calculator(), new TestDatabase())
    try {
      test(fixture)
    } finally {
      fixture.database.close()
    }
  }

  "Calculator with database" should "persist calculations" in withFixture { f =>
    val result = f.calculator.add(2, 3)
    f.database.save("2+3", result)
    assert(f.database.get("2+3").contains(5))
  }
}
```

## Мокирование

### Mockito integration
```scala
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.flatspec.AnyFlatSpec

class MockitoExampleSpec extends AnyFlatSpec with MockitoSugar {

  trait UserRepository {
    def findById(id: Long): Option[User]
    def save(user: User): User
    def delete(id: Long): Boolean
  }

  class UserService(repository: UserRepository) {
    def getUser(id: Long): Option[User] = repository.findById(id)
    def createUser(name: String, email: String): User = {
      val user = User(0, name, email, Instant.now())
      repository.save(user)
    }
  }

  "UserService with Mockito" should "return user when found" in {
    val mockRepo = mock[UserRepository]
    val service = new UserService(mockRepo)

    val expectedUser = User(1, "John", "john@example.com", Instant.now())

    when(mockRepo.findById(1L)).thenReturn(Some(expectedUser))

    val result = service.getUser(1L)

    result shouldBe Some(expectedUser)
    verify(mockRepo).findById(1L)
  }

  it should "create user successfully" in {
    val mockRepo = mock[UserRepository]
    val service = new UserService(mockRepo)

    val savedUser = User(1, "Jane", "jane@example.com", Instant.now())

    when(mockRepo.save(any[User])).thenReturn(savedUser)

    val result = service.createUser("Jane", "jane@example.com")

    result shouldBe savedUser
    verify(mockRepo).save(any[User])
  }

  it should "handle repository exceptions" in {
    val mockRepo = mock[UserRepository]
    val service = new UserService(mockRepo)

    when(mockRepo.findById(1L)).thenThrow(new RuntimeException("DB error"))

    assertThrows[RuntimeException] {
      service.getUser(1L)
    }
  }
}
```

### ScalaMock integration
```scala
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

class ScalaMockExampleSpec extends AnyFlatSpec with MockFactory {

  "UserService with ScalaMock" should "return user when found" in {
    val mockRepo = mock[UserRepository]
    val service = new UserService(mockRepo)

    val expectedUser = User(1, "John", "john@example.com", Instant.now())

    (mockRepo.findById _).expects(1L).returning(Some(expectedUser))

    val result = service.getUser(1L)

    result shouldBe Some(expectedUser)
  }

  it should "create user with proper calls" in {
    val mockRepo = mock[UserRepository]
    val service = new UserService(mockRepo)

    val savedUser = User(1, "Jane", "jane@example.com", Instant.now())

    (mockRepo.save _).expects(*).returning(savedUser)

    val result = service.createUser("Jane", "jane@example.com")

    result shouldBe savedUser
  }
}
```

## Property-based тестирование

### ScalaCheck integration
```scala
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.flatspec.AnyFlatSpec
import org.scalacheck.Gen

class PropertyBasedSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {

  val calculator = new Calculator()

  "Calculator" should "be associative for addition" in {
    forAll { (a: Int, b: Int, c: Int) =>
      assert(calculator.add(calculator.add(a, b), c) == calculator.add(a, calculator.add(b, c)))
    }
  }

  it should "have identity element for addition" in {
    forAll { (a: Int) =>
      assert(calculator.add(a, 0) == a)
      assert(calculator.add(0, a) == a)
    }
  }

  it should "be commutative for addition" in {
    forAll { (a: Int, b: Int) =>
      assert(calculator.add(a, b) == calculator.add(b, a))
    }
  }

  it should "handle string concatenation properties" in {
    forAll { (a: String, b: String) =>
      val concatenated = a + b
      assert(concatenated.length == a.length + b.length)
      assert(concatenated.startsWith(a))
      assert(concatenated.endsWith(b))
    }
  }

  // Кастомные генераторы
  val positiveIntGen = Gen.choose(1, 1000)
  val nonEmptyStringGen = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

  it should "handle positive numbers correctly" in {
    forAll(positiveIntGen, positiveIntGen) { (a: Int, b: Int) =>
      val result = calculator.add(a, b)
      assert(result > 0)
      assert(result >= a)
      assert(result >= b)
    }
  }

  it should "handle non-empty strings" in {
    forAll(nonEmptyStringGen, nonEmptyStringGen) { (a: String, b: String) =>
      whenever(a.nonEmpty && b.nonEmpty) {
        val result = a + b
        assert(result.contains(a))
        assert(result.contains(b))
        assert(result.length > a.length)
        assert(result.length > b.length)
      }
    }
  }

  // Условные свойства
  it should "handle division correctly" in {
    forAll { (a: Int, b: Int) =>
      whenever(b != 0) {
        val result = calculator.divide(a, b)
        assert(result * b == a || result * b == a - (a % b))
      }
    }
  }
}
```

## Async тестирование

### ScalaFutures для Future
```scala
import org.scalatest.flatspec.AsyncFlatSpec
import scala.concurrent.Future
import org.scalatest.time.{Span, Seconds}

class AsyncSpec extends AsyncFlatSpec {

  "UserService" should "create user asynchronously" in {
    val service = new UserService(new InMemoryUserRepository())

    val future = service.createUser("Async User", "async@example.com")

    future.map { user =>
      assert(user.name == "Async User")
      assert(user.email == "async@example.com")
    }
  }

  it should "find user by id asynchronously" in {
    val service = new UserService(new InMemoryUserRepository())

    for {
      created <- service.createUser("Test User", "test@example.com")
      found <- service.getUser(created.id)
    } yield {
      found shouldBe defined
      found.get.name shouldBe "Test User"
    }
  }
}

// С таймаутами
class AsyncWithTimeoutSpec extends AsyncFlatSpec {

  implicit override def executionContext = scala.concurrent.ExecutionContext.Implicits.global

  "Slow operation" should "complete within timeout" in {
    val slowFuture = Future {
      Thread.sleep(100)
      "done"
    }

    slowFuture.map { result =>
      assert(result == "done")
    }
  }
}
```

### Cats Effect IO тестирование
```scala
import org.scalatest.flatspec.AnyFlatSpec
import cats.effect.IO
import cats.effect.unsafe.implicits.global

class IOSpec extends AnyFlatSpec {

  "IO operations" should "work correctly" in {
    val ioOperation = IO.pure(42).map(_ * 2)

    val result = ioOperation.unsafeRunSync()

    assert(result == 84)
  }

  it should "handle errors properly" in {
    val failingIO = IO.raiseError(new RuntimeException("Test error"))

    assertThrows[RuntimeException] {
      failingIO.unsafeRunSync()
    }
  }

  it should "compose operations" in {
    val composed = for {
      a <- IO.pure(10)
      b <- IO.pure(20)
      c <- IO.pure(a + b)
    } yield c * 2

    val result = composed.unsafeRunSync()

    assert(result == 60)
  }
}
```

## Тестирование веб-приложений

### Play Framework integration
```scala
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import scala.concurrent.Future

class PlayControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HomeController" should {
    "render the index page" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome")
    }

    "return JSON for API calls" in {
      val request = FakeRequest(GET, "/api/users")
        .withHeaders("Accept" -> "application/json")

      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "users").as[Seq[User]] must not be empty
    }

    "handle POST requests correctly" in {
      val userJson = Json.obj(
        "name" -> "Test User",
        "email" -> "test@example.com"
      )

      val request = FakeRequest(POST, "/api/users")
        .withJsonBody(userJson)
        .withHeaders("Content-Type" -> "application/json")

      val result = route(app, request).get

      status(result) mustBe CREATED
      val createdUser = contentAsJson(result).as[User]
      createdUser.name mustBe "Test User"
    }
  }
}
```

## Интеграция с другими инструментами

### JUnit integration
```scala
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.flatspec.AnyFlatSpec

@RunWith(classOf[JUnitRunner])
class JUnitIntegrationSpec extends AnyFlatSpec {

  "Calculator" should "add numbers correctly" in {
    val calc = new Calculator()
    assert(calc.add(2, 3) == 5)
  }
}
```

### TestNG integration
```scala
import org.scalatestplus.testng.TestNGSuite
import org.testng.annotations.Test

class TestNGIntegrationSuite extends TestNGSuite {

  @Test
  def testAddition(): Unit = {
    val calc = new Calculator()
    assert(calc.add(2, 3) == 5)
  }

  @Test(expectedExceptions = Array(classOf[ArithmeticException]))
  def testDivisionByZero(): Unit = {
    val calc = new Calculator()
    calc.divide(10, 0)
  }
}
```

## Лучшие практики

### Организация тестов
```scala
// Структура проекта
src/
├── main/scala/
│   └── com/example/
│       ├── models/
│       ├── services/
│       └── controllers/
└── test/scala/
    └── com/example/
        ├── models/
        │   ├── UserSpec.scala
        │   └── UserRepositorySpec.scala
        ├── services/
        │   ├── UserServiceSpec.scala
        │   └── EmailServiceSpec.scala
        ├── controllers/
        │   ├── UserControllerSpec.scala
        │   └── HomeControllerSpec.scala
        └── integration/
            └── UserWorkflowSpec.scala

// Конвенции именования
class UserServiceSpec extends AnyFlatSpec with Matchers {
  // ✓ Правильно: описательные имена тестов
  "UserService" should "create user successfully when valid data provided" in { ... }
  it should "throw ValidationException when email is invalid" in { ... }

  // ✗ Неправильно: неописательные имена
  "test1" in { ... }
  "should work" in { ... }
}

// Группировка связанных тестов
class UserServiceComprehensiveSpec extends AnyFlatSpec with Matchers {

  val service = new UserService(mock[UserRepository])

  behavior of "UserService user creation"

  it should "create user with valid data" in { ... }
  it should "validate email format" in { ... }
  it should "validate name is not empty" in { ... }
  it should "send welcome email after creation" in { ... }

  behavior of "UserService user retrieval"

  it should "return user when exists" in { ... }
  it should "return None when user not found" in { ... }
  it should "handle database errors gracefully" in { ... }
}
```

### Тестовые утилиты и хелперы
```scala
import org.scalatest.{Suite, BeforeAndAfterEach}
import scala.util.Random

trait TestDataHelpers { this: Suite =>

  // Генерация тестовых данных
  def randomUser(): User = User(
    id = 0,
    name = s"User${Random.nextInt(1000)}",
    email = s"user${Random.nextInt(1000)}@example.com",
    createdAt = java.time.Instant.now()
  )

  def validUser(name: String = "John Doe", email: String = "john@example.com"): User = User(
    id = 0,
    name = name,
    email = email,
    createdAt = java.time.Instant.now()
  )

  def invalidEmails = List("", "invalid", "user@", "@domain.com", "user@.com")
  def validEmails = List("user@example.com", "test.email@domain.co.uk", "user+tag@example.com")
}

trait DatabaseTestHelpers extends BeforeAndAfterEach { this: Suite =>

  var database: TestDatabase = _

  override def beforeEach(): Unit = {
    database = new TestDatabase()
    database.setup()
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    if (database != null) {
      database.teardown()
    }
    super.afterEach()
  }
}

// Использование миксинов
class UserServiceSpec extends AnyFlatSpec
  with Matchers
  with TestDataHelpers
  with DatabaseTestHelpers
  with MockitoSugar {

  "UserService" should "create user with test data" in {
    val user = randomUser()
    val mockRepo = mock[UserRepository]

    when(mockRepo.save(user)).thenReturn(user.copy(id = 1))

    val service = new UserService(mockRepo)
    val result = service.createUser(user.name, user.email)

    result.id should be > 0
    result.name shouldBe user.name
    result.email shouldBe user.email
  }
}
```

### Продвинутые возможности
```scala
import org.scalatest.{Tag, CancelAfterFailure, Retries}
import org.scalatest.tags.Slow

// Теги для категоризации тестов
object DatabaseTest extends Tag("DatabaseTest")
object SlowTest extends Tag("SlowTest")

// Тесты с тегами
class TaggedSpec extends AnyFlatSpec with Matchers {

  "Database operations" should "work correctly" taggedAs DatabaseTest in {
    // Тесты БД
  }

  "Slow operations" should "complete eventually" taggedAs (SlowTest, DatabaseTest) in {
    // Медленные тесты
  }
}

// Повторение неудачных тестов
class RetrySpec extends AnyFlatSpec with Matchers with Retries {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetry { super.withFixture(test) }
    else
      super.withFixture(test)
  }

  def isRetryable(test: NoArgTest) = test.tags.contains("Retryable")

  "Unreliable operation" should "eventually succeed" taggedAs "Retryable" in {
    val result = unreliableOperation()
    result should be >= 0
  }
}

// Остановка после первой неудачи
class FailFastSpec extends AnyFlatSpec with Matchers with CancelAfterFailure {

  "First test" should "pass" in {
    assert(1 + 1 == 2)
  }

  "Second test" should "fail" in {
    assert(1 + 1 == 3) // Это остановит выполнение suite
  }

  "Third test" should "not run" in {
    assert(true) // Этот тест не выполнится
  }
}

// Параллельное выполнение
import org.scalatest.ParallelTestExecution

class ParallelSpec extends AnyFlatSpec with Matchers with ParallelTestExecution {

  "Test 1" should "run in parallel" in {
    Thread.sleep(1000) // Имитация долгой операции
    assert(true)
  }

  "Test 2" should "also run in parallel" in {
    Thread.sleep(1000)
    assert(true)
  }
}
```

## Устранение неполадок

### Common Issues
```scala
object ScalaTestTroubleshooting {

  // Проблема: Тесты не запускаются
  // Решение: Проверить конфигурацию test в build.sbt
  lazy val root = (project in file("."))
    .settings(
      // ✓ Правильно: включить scalatest
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      // Настроить test framework
      testFrameworks += new TestFramework("org.scalatest.tools.Framework")
    )

  // Проблема: Медленные тесты
  // Решение: Использовать теги и параллельное выполнение
  object SlowTest extends Tag("SlowTest")

  "Slow operation" should "complete" taggedAs SlowTest in {
    // Медленный тест
  }

  // В build.sbt
  Test / parallelExecution := true

  // Проблема: Flaky тесты
  // Решение: Использовать retry и стабилизировать окружение
  class FlakyTestSpec extends AnyFlatSpec with Retries {
    override def withFixture(test: NoArgTest) = withRetry { super.withFixture(test) }
  }

  // Проблема: Memory leaks в тестах
  // Решение: Правильная очистка ресурсов
  class ResourceSpec extends AnyFlatSpec with BeforeAndAfterEach {
    var resource: ExpensiveResource = _

    override def beforeEach(): Unit = {
      resource = new ExpensiveResource()
    }

    override def afterEach(): Unit = {
      if (resource != null) {
        resource.close()
        resource = null
      }
    }
  }

  // Проблема: Неправильные assertion сообщения
  // Решение: Использовать информативные сообщения
  it should "contain the right elements" in {
    val list = List(1, 2, 3)
    // ✗ Плохо
    assert(list.contains(4))
    // ✓ Хорошо
    assert(list.contains(4), s"List $list should contain 4")
    // ✓ Ещё лучше с matchers
    list should contain (4)
  }

  // Проблема: Race conditions в async тестах
  // Решение: Использовать правильные ожидания
  it should "complete async operation" in {
    val future = asyncOperation()
    // ✗ Плохо: нестабильно
    Thread.sleep(1000)
    assert(future.isCompleted)

    // ✓ Хорошо: использовать ScalaFutures
    whenReady(future) { result =>
      result shouldBe "expected"
    }
  }

  // Проблема: Too many mock expectations
  // Решение: Группировать expectations и использовать verify
  it should "call repository methods correctly" in {
    val mockRepo = mock[UserRepository]

    // ✗ Плохо: слишком много expectations
    (mockRepo.findById _).expects(1L).returning(Some(user))
    (mockRepo.save _).expects(*).returning(savedUser)

    service.processUser(1L)

    // ✓ Хорошо: использовать verify
    service.processUser(1L)

    verify(mockRepo).findById(1L)
    verify(mockRepo).save(any[User])
  }
}
```

### Performance Tips
```scala
object ScalaTestPerformance {

  // 1. Параллельное выполнение
  // build.sbt
  Test / parallelExecution := true
  Test / testForkedParallel := true

  // 2. Выборочное выполнение тестов
  // sbt shell
  testOnly *UserServiceSpec
  testOnly *UserServiceSpec -- -z "create user"

  // 3. Исключение медленных тестов
  testOptions in Test += Tests.Argument("-l", "SlowTest")

  // 4. Настройка таймаутов
  implicit val patienceConfig = PatienceConfig(
    timeout = Span(5, Seconds),
    interval = Span(100, Millis)
  )

  // 5. Легковесные fixtures
  def withFastFixture(test: FastCalculator => Any): Unit = {
    val calc = new FastCalculator() // Быстрая инициализация
    test(calc)
  }

  // 6. Shared fixtures для интеграционных тестов
  object TestEnvironment {
    lazy val database = createTestDatabase()
    lazy val services = createTestServices(database)
  }

  class IntegrationSpec extends AnyFlatSpec {
    val services = TestEnvironment.services // Используется кэшированный instance
  }
}
```

## Руководство по миграции

### From JUnit to ScalaTest
```scala
// JUnit 4
import org.junit.Test
import org.junit.Assert._
import org.junit.Before

class CalculatorTest {
  var calculator: Calculator = _

  @Before
  def setUp(): Unit = {
    calculator = new Calculator()
  }

  @Test
  def testAddition(): Unit = {
    assertEquals(5, calculator.add(2, 3))
  }

  @Test(expected = classOf[ArithmeticException])
  def testDivisionByZero(): Unit = {
    calculator.divide(10, 0)
  }
}

// ScalaTest equivalent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CalculatorSpec extends AnyFlatSpec with Matchers {

  val calculator = new Calculator()

  "Calculator" should "add two numbers correctly" in {
    calculator.add(2, 3) shouldBe 5
  }

  it should "throw ArithmeticException when dividing by zero" in {
    assertThrows[ArithmeticException] {
      calculator.divide(10, 0)
    }
  }
}
```

### From Specs2 to ScalaTest
```scala
// Specs2
import org.specs2.mutable.Specification

class CalculatorSpec extends Specification {
  val calculator = new Calculator()

  "Calculator" should {
    "add two numbers correctly" in {
      calculator.add(2, 3) must_== 5
    }

    "throw exception when dividing by zero" in {
      calculator.divide(10, 0) must throwAn[ArithmeticException]
    }
  }
}

// ScalaTest equivalent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CalculatorSpec extends AnyFlatSpec with Matchers {

  val calculator = new Calculator()

  "Calculator" should "add two numbers correctly" in {
    calculator.add(2, 3) shouldBe 5
  }

  it should "throw exception when dividing by zero" in {
    assertThrows[ArithmeticException] {
      calculator.divide(10, 0)
    }
  }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация ScalaTest](https://www.scalatest.org/)
- [ScalaTest GitHub](https://github.com/scalatest/scalatest)
- [ScalaTest User Guide](https://www.scalatest.org/user_guide)
- [ScalaCheck](https://scalacheck.org/)
- [Mockito](https://site.mockito.org/)
- [ScalaMock](https://scalamock.org/)

## См. также
- [JUnit](testing.md) - Java testing framework
- [Specs2](libraries.md) - Alternative Scala testing library
- [Cats Effect](libraries.md) - Functional programming
- [Play Framework](scala-play.md) - Web framework testing
