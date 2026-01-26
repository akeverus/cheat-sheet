# JUnit 5

Кратко: JUnit 5 - современный фреймворк для тестирования Java приложений. Аннотации, assertions, параметризованные тесты, lifecycle, расширения.

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

### Официальная документация
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [JUnit 5 API Documentation](https://junit.org/junit5/docs/current/api/)

### Baeldung
- [JUnit Tutorial](https://www.baeldung.com/junit)

### См. также
- `../java/java-basics.md` - основы Java
- `./mockito.md` - Mockito для моков
- `./testcontainers.md` - TestContainers для интеграционных тестов

## Содержание

- [Введение в JUnit 5](#введение-в-junit-5)
- [Архитектура JUnit 5](#архитектура-junit-5)
- [Установка и настройка](#установка-и-настройка)
- [Основные аннотации](#основные-аннотации)
- [Assertions](#assertions)
- [Assumptions](#assumptions)
- [Тестовый lifecycle](#тестовый-lifecycle)
- [Параметризованные тесты](#параметризованные-тесты)
- [Тестирование исключений](#тестирование-исключений)
- [Группировка тестов](#группировка-тестов)
- [Расширения (Extensions)](#расширения-extensions)
- [Миграция с JUnit 4](#миграция-с-junit-4)

## Введение в JUnit 5

JUnit 5 - это современный фреймворк для модульного тестирования Java приложений. JUnit 5 состоит из трех основных подпроектов:

1. **JUnit Platform** - основа для запуска тестовых фреймворков
2. **JUnit Jupiter** - модель программирования и расширение для написания тестов
3. **JUnit Vintage** - для запуска тестов JUnit 3 и JUnit 4

### Основные преимущества JUnit 5

- Модульная архитектура
- Поддержка Java 8+ и лямбда-выражений
- Улучшенные assertions
- Расширяемая архитектура через Extensions
- Параметризованные тесты из коробки
- Динамические тесты
- Улучшенная интеграция с IDE и build tools

## Архитектура JUnit 5

```
JUnit 5
├── JUnit Platform
│   ├── TestEngine API
│   ├── Launcher API
│   └── Console Launcher
├── JUnit Jupiter
│   ├── Programming Model
│   ├── Extension Model
│   └── TestEngine Implementation
└── JUnit Vintage
    └── TestEngine для JUnit 3/4
```

## Установка и настройка

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
        </plugin>
    </plugins>
</build>
```

### Gradle

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
}

test {
    useJUnitPlatform()
}
```

## Основные аннотации

### @Test

Основная аннотация для обозначения тестового метода.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    
    @Test
    void testAddition() {
        Calculator calculator = new Calculator();
        assertEquals(5, calculator.add(2, 3));
    }
}
```

### @DisplayName

Задает понятное имя теста для отображения в отчетах.

```java
@Test
@DisplayName("Тест сложения двух положительных чисел")
void testAddition() {
    // тест
}
```

### @BeforeEach и @AfterEach

Методы, выполняемые до и после каждого теста.

```java
class UserServiceTest {
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService();
        // инициализация
    }
    
    @AfterEach
    void tearDown() {
        // очистка
    }
    
    @Test
    void testCreateUser() {
        // тест
    }
}
```

### @BeforeAll и @AfterAll

Статические методы, выполняемые один раз до/после всех тестов в классе.

```java
class DatabaseTest {
    private static DatabaseConnection connection;
    
    @BeforeAll
    static void setUpDatabase() {
        connection = DatabaseConnection.create();
    }
    
    @AfterAll
    static void closeDatabase() {
        connection.close();
    }
}
```

### @Disabled

Временно отключает тест.

```java
@Test
@Disabled("Тест временно отключен")
void disabledTest() {
    // тест не будет выполнен
}
```

### @Tag

Позволяет группировать и фильтровать тесты.

```java
@Test
@Tag("integration")
void integrationTest() {
    // тест
}

@Test
@Tag("unit")
@Tag("fast")
void fastUnitTest() {
    // тест
}
```

## Assertions

JUnit 5 предоставляет улучшенные assertions через класс `Assertions`.

### Базовые assertions

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void basicAssertions() {
    assertEquals(4, 2 + 2, "2+2 должно быть 4");
    assertTrue(5 > 3, "5 должно быть больше 3");
    assertFalse(2 > 5, "2 не должно быть больше 5");
    assertNull(null, "объект должен быть null");
    assertNotNull("Hello", "объект не должен быть null");
}
```

### Assertions для массивов и коллекций

```java
@Test
void arrayAssertions() {
    int[] expected = {1, 2, 3};
    int[] actual = {1, 2, 3};
    
    assertArrayEquals(expected, actual);
}

@Test
void collectionAssertions() {
    List<String> list = Arrays.asList("a", "b", "c");
    
    assertTrue(list.contains("a"));
    assertEquals(3, list.size());
    assertIterableEquals(list, list);
}
```

### Assertions для исключений

```java
@Test
void exceptionAssertions() {
    Exception exception = assertThrows(
        IllegalArgumentException.class,
        () -> {
            throw new IllegalArgumentException("error");
        }
    );
    
    assertEquals("error", exception.getMessage());
}
```

### AssertAll для множественных проверок

```java
@Test
void multipleAssertions() {
    Person person = new Person("John", "Doe", 30);
    
    assertAll("person",
        () -> assertEquals("John", person.getFirstName()),
        () -> assertEquals("Doe", person.getLastName()),
        () -> assertEquals(30, person.getAge())
    );
}
```

### Лямбда-выражения в assertions

Все assertions поддерживают лямбда-выражения для ленивой инициализации сообщений об ошибках.

```java
@Test
void lambdaAssertions() {
    assertTrue(5 > 3, () -> "5 должно быть больше 3");
    assertEquals(4, 2 + 2, () -> "Результат должен быть 4");
}
```

## Assumptions

Assumptions позволяют пропускать тесты при определенных условиях.

```java
import static org.junit.jupiter.api.Assumptions.*;

@Test
void testOnlyOnWindows() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    // тест выполнится только на Windows
}

@Test
void testOnlyOnLinux() {
    assumeFalse(System.getProperty("os.name").contains("Windows"));
    // тест выполнится только не на Windows
}

@Test
void testWithAssumption() {
    String env = System.getenv("ENV");
    assumingThat("prod".equals(env), () -> {
        // этот блок выполнится только если ENV=prod
    });
}
```

## Тестовый lifecycle

JUnit 5 предоставляет гибкий lifecycle для тестов:

1. **@BeforeAll** - выполняется один раз перед всеми тестами
2. **@BeforeEach** - выполняется перед каждым тестом
3. **@Test** - выполняется тест
4. **@AfterEach** - выполняется после каждого теста
5. **@AfterAll** - выполняется один раз после всех тестов

```java
class LifecycleTest {
    @BeforeAll
    static void setupAll() {
        System.out.println("Before all tests");
    }
    
    @BeforeEach
    void setup() {
        System.out.println("Before each test");
    }
    
    @Test
    void test1() {
        System.out.println("Test 1");
    }
    
    @Test
    void test2() {
        System.out.println("Test 2");
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("After each test");
    }
    
    @AfterAll
    static void tearDownAll() {
        System.out.println("After all tests");
    }
}
```

## Параметризованные тесты

JUnit 5 поддерживает параметризованные тесты через аннотацию `@ParameterizedTest`.

### @ValueSource

```java
@ParameterizedTest
@ValueSource(ints = {1, 3, 5, 7, 9})
void testOddNumbers(int number) {
    assertTrue(number % 2 != 0);
}

@ParameterizedTest
@ValueSource(strings = {"hello", "world", "junit"})
void testStrings(String str) {
    assertNotNull(str);
    assertFalse(str.isEmpty());
}
```

### @CsvSource

```java
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "10, 20, 30",
    "0, 0, 0"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, Calculator.add(a, b));
}
```

### @CsvFileSource

```java
@ParameterizedTest
@CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
void testFromFile(int a, int b, int expected) {
    assertEquals(expected, Calculator.add(a, b));
}
```

### @MethodSource

```java
@ParameterizedTest
@MethodSource("provideTestData")
void testWithMethodSource(int a, int b, int expected) {
    assertEquals(expected, Calculator.add(a, b));
}

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of(1, 2, 3),
        Arguments.of(5, 5, 10),
        Arguments.of(10, 20, 30)
    );
}
```

### @NullSource и @EmptySource

```java
@ParameterizedTest
@NullSource
@EmptySource
@ValueSource(strings = {"  ", "\t", "\n"})
void testBlankStrings(String input) {
    assertTrue(input == null || input.trim().isEmpty());
}
```

## Тестирование исключений

### assertThrows

```java
@Test
void testException() {
    assertThrows(IllegalArgumentException.class, () -> {
        Calculator.divide(10, 0);
    });
}

@Test
void testExceptionWithMessage() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> Calculator.divide(10, 0)
    );
    
    assertEquals("Division by zero", exception.getMessage());
}
```

### assertDoesNotThrow

```java
@Test
void testNoException() {
    assertDoesNotThrow(() -> {
        Calculator.add(2, 3);
    });
}
```

## Группировка тестов

### @Nested

Позволяет создавать вложенные тестовые классы для логической группировки.

```java
class UserServiceTest {
    
    @Nested
    class CreateUserTests {
        @Test
        void testCreateValidUser() {
            // тест
        }
        
        @Test
        void testCreateUserWithNullName() {
            // тест
        }
    }
    
    @Nested
    class UpdateUserTests {
        @Test
        void testUpdateExistingUser() {
            // тест
        }
    }
}
```

### @TestInstance(Lifecycle.PER_CLASS)

Изменяет lifecycle тестового класса для использования одного экземпляра на класс.

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SharedStateTest {
    private int counter = 0;
    
    @Test
    void test1() {
        counter++;
        assertEquals(1, counter);
    }
    
    @Test
    void test2() {
        counter++;
        assertEquals(2, counter);
    }
}
```

## Расширения (Extensions)

JUnit 5 поддерживает расширения через модель Extension API.

### Регистрация расширений

```java
// Через аннотацию @ExtendWith
@ExtendWith(CustomExtension.class)
class TestWithExtension {
    // тесты
}

// Глобально через файл junit-platform.properties
```

### Примеры встроенных расширений

#### MockitoExtension

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void test() {
        // тест с моками
    }
}
```

#### SpringExtension

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class SpringIntegrationTest {
    @Autowired
    private UserService userService;
    
    @Test
    void test() {
        // тест с Spring контекстом
    }
}
```

## Динамические тесты

JUnit 5 поддерживает динамические тесты через `@TestFactory`.

```java
@TestFactory
Stream<DynamicTest> dynamicTests() {
    return Stream.of(
        DynamicTest.dynamicTest("Тест 1", () -> {
            assertEquals(2, 1 + 1);
        }),
        DynamicTest.dynamicTest("Тест 2", () -> {
            assertEquals(4, 2 * 2);
        })
    );
}
```

## Тестовые интерфейсы

JUnit 5 позволяет создавать тестовые интерфейсы с общими методами.

```java
interface Testable<T> {
    T createValue();
    
    @BeforeEach
    default void setUp() {
        // общая настройка
    }
}

class StringTest implements Testable<String> {
    @Override
    public String createValue() {
        return "test";
    }
    
    @Test
    void test() {
        String value = createValue();
        assertNotNull(value);
    }
}
```

## Порядок выполнения тестов

### @TestMethodOrder

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedTest {
    
    @Test
    @Order(1)
    void testA() {
        // выполнится первым
    }
    
    @Test
    @Order(2)
    void testB() {
        // выполнится вторым
    }
}
```

Доступные порядки:
- `MethodOrderer.OrderAnnotation.class` - по аннотации @Order
- `MethodOrderer.MethodName.class` - по имени метода
- `MethodOrderer.Random.class` - случайный порядок

## Условное выполнение тестов

### @DisabledIf и @EnabledIf

```java
@Test
@DisabledIf("java.lang.System.getProperty('os.name').toLowerCase().contains('windows')")
void testOnlyOnNonWindows() {
    // тест отключен на Windows
}

@Test
@EnabledIf("java.lang.System.getProperty('env') == 'test'")
void testOnlyInTestEnv() {
    // тест включен только в test окружении
}
```

## Миграция с JUnit 4

### Основные изменения

1. Пакет изменен с `org.junit` на `org.junit.jupiter.api`
2. Аннотации:
   - `@Before` → `@BeforeEach`
   - `@After` → `@AfterEach`
   - `@BeforeClass` → `@BeforeAll`
   - `@AfterClass` → `@AfterAll`
   - `@Ignore` → `@Disabled`
3. Assertions:
   - `Assert.assertEquals()` → `Assertions.assertEquals()`
   - `Assert.assertThat()` → использовать AssertJ или Hamcrest
4. Правила (Rules) заменены на Extensions

### JUnit Vintage

Для запуска старых тестов JUnit 4:

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.10.1</version>
    <scope>test</scope>
</dependency>
```

## Лучшие практики

1. **Используйте описательные имена тестов** - `@DisplayName` или понятные имена методов
2. **Один assertion на тест** - когда возможно, для ясности
3. **Используйте параметризованные тесты** - для тестирования множества сценариев
4. **Группируйте связанные тесты** - через `@Nested`
5. **Используйте assumptions** - для условного выполнения
6. **Тестируйте поведение, а не реализацию** - фокусируйтесь на результатах
7. **Поддерживайте тесты независимыми** - каждый тест должен работать изолированно
8. **Используйте setup/teardown правильно** - `@BeforeEach` для общей настройки, `@AfterEach` для очистки

