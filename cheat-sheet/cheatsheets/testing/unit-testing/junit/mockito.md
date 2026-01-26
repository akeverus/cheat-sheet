# Mockito

Кратко: Mockito - фреймворк для создания моков в Java тестах. Моки, стабы, верификация, аргументы, частичные моки, аннотации.

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

### Официальная документация
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Mockito GitHub](https://github.com/mockito/mockito)

### Baeldung
- [Mockito Tutorial](https://www.baeldung.com/mockito)

### См. также
- `../java/java-basics.md` - основы Java
- `./junit.md` - JUnit для тестирования
- `./testcontainers.md` - TestContainers для интеграционных тестов

## Содержание

- [Введение в Mockito](#введение-в-mockito)
- [Установка и настройка](#установка-и-настройка)
- [Создание моков](#создание-моков)
- [Стабы (Stubbing)](#стабы-stubbing)
- [Верификация (Verification)](#верификация-verification)
- [Аргументы (Argument Matchers)](#аргументы-argument-matchers)
- [Частичные моки (Partial Mocks)](#частичные-моки-partial-mocks)
- [Исключения и обработка ошибок](#исключения-и-обработка-ошибок)
- [Аннотации Mockito](#аннотации-mockito)
- [Интеграция с JUnit 5](#интеграция-с-junit-5)
- [Spy vs Mock](#spy-vs-mock)
- [Лучшие практики](#лучшие-практики)

## Введение в Mockito

Mockito - это популярный фреймворк для создания моков (mock objects) в Java тестах. Моки позволяют изолировать тестируемый код от его зависимостей, заменяя реальные объекты на фейковые, поведение которых можно контролировать.

### Основные концепции

- **Mock** - объект-заглушка, поведение которого контролируется
- **Stub** - настройка поведения мока для возврата определенных значений
- **Verify** - проверка того, что методы мока были вызваны с правильными параметрами
- **Spy** - частичный мок, который использует реальную реализацию, но позволяет переопределять методы

## Установка и настройка

### Maven

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>

<!-- Для JUnit 5 интеграции -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
}
```

## Создание моков

### Статический метод mock()

```java
import static org.mockito.Mockito.*;

List<String> mockedList = mock(List.class);
```

### Аннотация @Mock

```java
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Test
    void test() {
        // userRepository - это мок
    }
}
```

### Mockito.mock() с настройками

```java
// Мок с ответами на вызовы
List<String> list = mock(List.class, withSettings()
    .defaultAnswer(RETURNS_SMART_NULLS)
    .name("myList"));
```

## Стабы (Stubbing)

Стабы определяют поведение мока при вызове методов.

### when().thenReturn()

```java
List<String> mockedList = mock(List.class);

when(mockedList.get(0)).thenReturn("first");
when(mockedList.get(1)).thenReturn("second");

assertEquals("first", mockedList.get(0));
assertEquals("second", mockedList.get(1));
```

### Последовательные возвраты

```java
when(mockedList.get(anyInt()))
    .thenReturn("first")
    .thenReturn("second")
    .thenReturn("third");

assertEquals("first", mockedList.get(0));
assertEquals("second", mockedList.get(1));
assertEquals("third", mockedList.get(2));
```

### when().thenThrow()

```java
when(mockedList.get(0)).thenThrow(new RuntimeException("Error"));

assertThrows(RuntimeException.class, () -> mockedList.get(0));
```

### when().thenAnswer()

```java
when(mockedList.get(anyInt())).thenAnswer(invocation -> {
    int index = invocation.getArgument(0);
    return "element-" + index;
});

assertEquals("element-0", mockedList.get(0));
assertEquals("element-5", mockedList.get(5));
```

### doReturn().when()

Альтернативный синтаксис для стабов.

```java
List<String> mockedList = mock(List.class);

doReturn("first").when(mockedList).get(0);
doThrow(new RuntimeException()).when(mockedList).clear();
```

### doNothing().when()

Для void методов.

```java
List<String> mockedList = mock(List.class);

doNothing().when(mockedList).clear();
mockedList.clear(); // метод будет вызван, но ничего не сделает
```

## Верификация (Verification)

Верификация проверяет, что методы мока были вызваны.

### verify()

```java
List<String> mockedList = mock(List.class);

mockedList.add("one");
mockedList.add("two");

verify(mockedList).add("one");
verify(mockedList).add("two");
```

### Количество вызовов

```java
mockedList.add("one");
mockedList.add("one");
mockedList.add("one");

verify(mockedList, times(3)).add("one");
verify(mockedList, atLeast(2)).add("one");
verify(mockedList, atMost(5)).add("one");
verify(mockedList, never()).add("two");
verify(mockedList, atLeastOnce()).add("one");
```

### Порядок вызовов

```java
List<String> mockedList = mock(List.class);
List<String> anotherMock = mock(List.class);

mockedList.add("first");
anotherMock.add("second");
mockedList.add("third");

InOrder inOrder = inOrder(mockedList, anotherMock);
inOrder.verify(mockedList).add("first");
inOrder.verify(anotherMock).add("second");
inOrder.verify(mockedList).add("third");
```

### verifyNoMoreInteractions()

```java
mockedList.add("one");
verify(mockedList).add("one");
verifyNoMoreInteractions(mockedList);
```

### verifyZeroInteractions()

```java
List<String> unusedMock = mock(List.class);
verifyZeroInteractions(unusedMock);
```

## Аргументы (Argument Matchers)

Argument matchers позволяют гибко проверять аргументы методов.

### Базовые matchers

```java
import static org.mockito.ArgumentMatchers.*;

when(mockedList.get(anyInt())).thenReturn("element");
when(mockedList.contains(anyString())).thenReturn(true);
when(mockedList.add(any(String.class))).thenReturn(true);

mockedList.get(999); // вернет "element"
mockedList.contains("anything"); // вернет true
```

### Доступные matchers

```java
// Примитивы
anyInt(), anyLong(), anyDouble(), anyFloat(), anyBoolean(), anyByte(), anyChar(), anyShort()

// Объекты
any(), any(Class.class), anyString(), anyList(), anyMap(), anySet()

// Строки
contains(String), startsWith(String), endsWith(String), matches(String)

// Null/NotNull
isNull(), isNotNull()

// Сравнения
eq(value), gt(value), lt(value), ge(value), le(value)

// Коллекции
anyCollection(), anyList(), anyMap(), anySet()
```

### Кастомные matchers

```java
import static org.mockito.ArgumentMatchers.argThat;

when(userRepository.findByEmail(argThat(email -> email.contains("@"))))
    .thenReturn(new User());

verify(userRepository).save(argThat(user -> user.getName().equals("John")));
```

### Использование matchers

**Важно:** Если используется matcher для одного аргумента, все аргументы должны быть matchers.

```java
// ✅ Правильно
when(mockedList.get(anyInt())).thenReturn("element");
when(service.process(anyString(), eq(100))).thenReturn("result");

// ❌ Неправильно - смешивание реальных значений и matchers
when(mockedList.get(anyInt())).thenReturn("element"); // OK
when(service.process(anyString(), 100)).thenReturn("result"); // Ошибка!
```

## Частичные моки (Partial Mocks)

### Spy

Spy создает частичный мок, который использует реальную реализацию, но позволяет переопределять методы.

```java
List<String> realList = new ArrayList<>();
List<String> spyList = spy(realList);

spyList.add("one");
spyList.add("two");

when(spyList.size()).thenReturn(100);

assertEquals(2, realList.size()); // реальный список
assertEquals(100, spyList.size()); // spy возвращает 100
assertEquals("one", spyList.get(0)); // реальный метод
```

### doReturn().when() для spy

```java
List<String> list = new ArrayList<>();
list.add("real");

List<String> spy = spy(list);

doReturn("mocked").when(spy).get(0);

assertEquals("mocked", spy.get(0)); // переопределено
```

### @Spy аннотация

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Spy
    private UserRepository userRepository = new UserRepositoryImpl();
    
    @Test
    void test() {
        when(userRepository.findById(anyLong())).thenReturn(new User());
        // реальные методы работают, findById переопределен
    }
}
```

## Исключения и обработка ошибок

### Стабы с исключениями

```java
when(mockedList.get(0)).thenThrow(new RuntimeException("Error"));

assertThrows(RuntimeException.class, () -> mockedList.get(0));
```

### doThrow().when()

```java
doThrow(new RuntimeException("Error")).when(mockedList).clear();

assertThrows(RuntimeException.class, () -> mockedList.clear());
```

### Последовательные исключения

```java
when(mockedList.get(0))
    .thenThrow(new RuntimeException("First error"))
    .thenReturn("success");

assertThrows(RuntimeException.class, () -> mockedList.get(0));
assertEquals("success", mockedList.get(0));
```

## Аннотации Mockito

### @Mock

Создает мок объекта.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Test
    void test() {
        // оба объекта - моки
    }
}
```

### @Spy

Создает spy объекта.

```java
@ExtendWith(MockitoExtension.class)
class CalculatorTest {
    
    @Spy
    private Calculator calculator = new Calculator();
    
    @Test
    void test() {
        doReturn(100).when(calculator).multiply(anyInt(), anyInt());
        // calculator - spy
    }
}
```

### @InjectMocks

Автоматически внедряет моки/spy в тестируемый объект.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService; // моки будут внедрены
    
    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(new User());
        
        User user = userService.createUser("John", "john@example.com");
        
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(anyString());
    }
}
```

### @Captor

Создает ArgumentCaptor для захвата аргументов.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testCreateUser() {
        userService.createUser("John", "john@example.com");
        
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        
        assertEquals("John", capturedUser.getName());
        assertEquals("john@example.com", capturedUser.getEmail());
    }
}
```

## Интеграция с JUnit 5

### MockitoExtension

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void test() {
        // тесты с моками
    }
}
```

### Инициализация моков

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        // дополнительная настройка моков
        when(userRepository.count()).thenReturn(10L);
    }
    
    @Test
    void test() {
        // тесты
    }
}
```

## Spy vs Mock

### Когда использовать Mock

- Нужно полностью контролировать поведение объекта
- Объект не имеет реальной реализации
- Нужно изолировать тестируемый код от зависимостей

```java
@Mock
private UserRepository userRepository; // полностью фейковый объект
```

### Когда использовать Spy

- Нужно использовать реальную реализацию, но переопределить некоторые методы
- Хотите убедиться, что реальные методы вызываются
- Тестируете legacy код

```java
@Spy
private UserRepository userRepository = new UserRepositoryImpl(); // реальный объект с переопределениями
```

## Ответы по умолчанию

### RETURNS_DEFAULTS

Возвращает значения по умолчанию (null, 0, false и т.д.).

```java
List<String> list = mock(List.class, RETURNS_DEFAULTS);
String value = list.get(0); // null
```

### RETURNS_SMART_NULLS

Возвращает SmartNull вместо null с информативным сообщением.

```java
List<String> list = mock(List.class, RETURNS_SMART_NULLS);
String value = list.get(0); // SmartNull с информативным сообщением
```

### RETURNS_MOCKS

Возвращает моки для сложных типов.

```java
Service service = mock(Service.class, RETURNS_MOCKS);
Object result = service.getComplexObject(); // вернет мок
```

### RETURNS_DEEP_STUBS

Позволяет создавать цепочки моков.

```java
UserService service = mock(UserService.class, RETURNS_DEEP_STUBS);

when(service.getUser().getAddress().getCity()).thenReturn("Moscow");
// создается цепочка моков автоматически
```

## Лучшие практики

1. **Не моките то, что тестируете** - моките только зависимости
2. **Используйте @InjectMocks** - для автоматического внедрения зависимостей
3. **Верифицируйте важные вызовы** - но не верифицируйте все подряд
4. **Используйте аргумент matchers осторожно** - все аргументы должны быть matchers, если используется хотя бы один
5. **Предпочитайте spy над mock** - когда возможно использовать реальную реализацию
6. **Используйте ArgumentCaptor** - для проверки сложных аргументов
7. **Не моките value objects** - моките только поведенческие объекты
8. **Используйте ответы по умолчанию с умом** - RETURNS_SMART_NULLS помогает найти проблемы
9. **Очищайте моки после тестов** - через @AfterEach, если нужно
10. **Избегайте глубоких стабов** - предпочитайте простые и понятные тесты

## Примеры использования

### Тестирование сервиса с зависимостями

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testCreateUser() {
        // Arrange
        User user = new User("John", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // Act
        User created = userService.createUser("John", "john@example.com");
        
        // Assert
        assertNotNull(created);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("john@example.com");
    }
    
    @Test
    void testCreateUserWithDuplicateEmail() {
        // Arrange
        when(userRepository.findByEmail("existing@example.com"))
            .thenReturn(new User());
        
        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser("John", "existing@example.com");
        });
        
        verify(userRepository, never()).save(any());
    }
}
```

### Использование ArgumentCaptor

```java
@Test
void testUpdateUser() {
    User user = new User("John", "john@example.com");
    
    userService.updateUser(1L, "Jane", "jane@example.com");
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    
    User savedUser = userCaptor.getValue();
    assertEquals("Jane", savedUser.getName());
    assertEquals("jane@example.com", savedUser.getEmail());
}
```

### Использование Spy

```java
@Test
void testProcessWithSpy() {
    List<String> realList = new ArrayList<>();
    List<String> spyList = spy(realList);
    
    spyList.add("one");
    spyList.add("two");
    
    doReturn(100).when(spyList).size();
    
    assertEquals(2, realList.size());
    assertEquals(100, spyList.size());
    assertEquals("one", spyList.get(0)); // реальный метод
}
```

