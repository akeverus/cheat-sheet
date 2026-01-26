# Вопросы на собеседовании: Java Exceptions

**Комплексное руководство по вопросам собеседования на тему Java Exceptions для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


## Полезные ссылки

### Официальная документация
- [Java Exceptions Tutorial](https://docs.oracle.com/javase/tutorial/essential/exceptions/)
- [Exception Hierarchy](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html)
- [Best Practices for Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html)

### См. также
- `../../languages/java/java-exceptions.md` - Детальное руководство по исключениям
- `../java-core-interview.md` - Вопросы по Java Core
- `../java-8-interview.md` - Вопросы по Java 8

## Содержание

- [Q1. Что такое Exception?](#q1-что-такое-exception)
- [Q2. Какова цель Key слов throw и throws?](#q2-какова-цель-key-слов-throw-и-throws)
- [Q3. Как вы можете обработать Exception?](#q3-как-вы-можете-обработать-exception)
- [Q4. Как вы можете поймать несколько Exceptions?](#q4-как-вы-можете-поймать-несколько-exceptions)
- [Q5. (ВАЖНО) - В чем разница между Checked и Unchecked Exception?](#q5-важно---в-чем-разница-между-checked-и-unchecked-exception)
- [Q6. (ВАЖНО) - В чем разница между Exception и Error?](#q6-важно---в-чем-разница-между-exception-и-error)
- [Q7. Какой Exception будет выдан при выполнении следующего блока кода?](#q7-какой-exception-будет-выдан-при-выполнении-следующего-блока-кода)
- [Q8. Что такое Exception Chaining?](#q8-что-такое-exception-chaining)
- [Q9. Что такое Stacktrace и как она связана с Exception?](#q9-что-такое-stacktrace-и-как-она-связана-с-exception)
- [Q10. Зачем вам Subclass Exception?](#q10-зачем-вам-subclass-exception)
- [Q11. Каковы некоторые преимущества Exceptions?](#q11-каковы-некоторые-преимущества-exceptions)
- [Q12. (ВАЖНО) - Как создать какой-либо Exception внутри тела лямбда-выражения?](#q12-важно---как-создать-какой-либо-exception-внутри-тела-лямбда-выражения)
- [Q13. Как переопределить метод, выдающий Exception?](#q13-как-переопределить-метод-выдающий-exception)
- [Q14. Будет ли компилироваться следующий код?](#q14-будет-ли-компилироваться-следующий-код)
- [Q15. (ВАЖНО) - Есть ли способ генерировать Сhecked Exception из метода, который не имеет пункта throws?](#q15-важно---есть-ли-способ-генерировать-сhecked-exception-из-метода-который-не-имеет-пункта-throws)

## Q1. Что такое Exception?


Исключение это ненормальное событие, возникающее во время выполнения программы и нарушающее нормальный поток инструкций программы.



## Q2. Какова цель Key слов throw и throws?

Ключевое слово `throws` используется для указания того, что метод может вызвать исключение во время его выполнения. Он обеспечивает явную обработку исключений при вызове метода:

```java
public void simpleMethod() throws Exception {
}
```

Ключевое слово `throw` позволяет нам генерировать объект исключения, чтобы прервать нормальный ход программы. Это чаще всего используется, когда программа не удовлетворяет заданному условию:

```java
if (task.isTooComplicated()) {
    throw new TooComplicatedException("The task is too complicated");
}
```



## Q3. Как вы можете обработать Exception?


Используя оператор **try-сatch-finally:

try {

} catch (ExceptionType1 ex) {

} catch (ExceptionType2 ex) {

} finally {

}

Блок кода, в котором может возникнуть исключение, заключен в блок **try. Этот блок также называют **«**защищенным**»** или **«**защищенным**»** кодом.

Если возникает исключение, выполняется блок **сatch, соответствующий выбрасываемому исключению, в противном случае все блоки **сatch** игнорируются.

Блок **finally** всегда выполняется после выхода из блока **try, вне зависимости от того, было ли внутри него сгенерировано исключение.



## Q4. Как вы можете поймать несколько Exceptions?


Существует три способа обработки нескольких исключений в блоке кода.

Первый заключается в использовании блока **сatch, который может обрабатывать все типы выбрасываемых исключений:

try {

} catch (Exception ex) {

}

Следует иметь в виду, что рекомендуемая практика заключается в использовании максимально точных обработчиков исключений.

Слишком широкие обработчики исключений могут сделать ваш код более подверженным ошибкам, перехватывать непредвиденные исключения и вызывать непредвиденное поведение вашей программы.

Второй способ реализовать несколько блоков **сatch:

try {

} catch (FileNotFoundException ex) {

} catch (EOFException ex) {

}

Обратите внимание, что если исключения имеют отношения наследования; дочерний тип должен стоять первым, а родительский тип позже. Если мы этого не сделаем, это приведет к ошибке компиляции.

Третий использовать блок **multi-сatch:

try {

} catch (FileNotFoundException | EOFException ex) {

}

Эта функция, впервые представленная в **Java 7; уменьшает дублирование кода и упрощает его обслуживание.



## Q5. (ВАЖНО) - В чем разница между Checked и Unchecked Exception?


Различие между **checked** и **unchecked** исключениями **(Exceptions)** связано с тем, как обязательно обрабатывать эти исключения в коде.Checked** исключения **(проверяемые исключения)** это исключения, которые должны быть обрабатываны или объявлены в сигнатуре метода. Они обычно являются результатом внешних условий или ошибок, которые могут быть предсказаны и на которые можно разумно реагировать. Например, FileNotFoundException** это **checked Exception, который может возникнуть при попытке открыть файл, который не существует. Для обработки **checked** исключений обязательно требуется использование конструкций **try-сatch** или объявление исключений в сигнатуре метода.Unchecked** исключения **(непроверяемые исключения), также известные как **RuntimeExceptions, не требуют обязательного обработки или объявления. Эти исключения обычно вызываются программными ошибками, такими как деление на ноль **(ArithmeticException)** или вызов метода на нулевой ссылке **(NullPointerException). Непроверяемые исключения сигнализируют об ошибках, которые обычно не могут быть предсказаны во время выполнения кода. Все непроверяемые исключения наследуются от **IllegalStateException** или **RuntimeException. Непроверяемые исключения могут быть обработаны с помощью конструкций **try-сatch, но это не является обязательным.

Различие между **checked** и **unchecked** исключениями влияет на способ их обработки в коде. Checked** исключения обязательно должны быть обработаны или объявлены в сигнатуре метода, тогда как **unchecked** исключения не требуют этого. Это помогает разработчикам понимать важность обработки разных типов исключений и выбирать подходящую стратегию обработки ошибок в своих программных решениях.



## Q6. (ВАЖНО) - В чем разница между Exception и Error?


Исключение это событие, представляющее собой состояние, из которого можно выйти, тогда как ошибка представляет собой внешнюю ситуацию, из которой обычно невозможно выйти.

Все ошибки, выдаваемые **JVM, являются экземплярами **Error** или одного из его подклассов, наиболее распространенные из которых включают, но не ограничиваются:

1. **OutOfMemoryError**: выдается, когда **JVM** не может выделить больше объектов из-за нехватки памяти, а **Garbage Collector** не смог сделать больше доступным.
2. **StackOverflowError**: возникает, когда пространство стека для потока исчерпано, как правило, из-за слишком глубокой рекурсии приложения.
3. ExceptionInInitializerError** сигнализирует о том, что во время оценки статического инициализатора произошло непредвиденное исключение.
4. NoClassDefFoundError** выдается, когда загрузчик классов пытается загрузить определение класса и не может его найти, обычно потому, что необходимые файлы классов не были найдены в пути к классам.
5. UnsupportedClassVersionError** возникает, когда **JVM** пытается прочитать файл класса и определяет, что версия в файле не поддерживается, обычно потому, что файл был создан с более новой версией **Java.

Хотя ошибку можно обработать с помощью оператора `try`, это не рекомендуется, поскольку нет гарантии, что программа сможет что-либо надежно сделать после возникновения ошибки.



## Q7. Какой Exception будет выдан при выполнении следующего блока кода?


Integer\[\]\[\] ints = {{1, 2, 3},{null},{7, 8, 9}};

System.out.println ("value = " + ints\[1\]\[1\].intValue());

Он генерирует исключение **ArrayIndexOutOfBoundsException, поскольку мы пытаемся получить доступ к позиции, превышающей длину массива.



## Q8. Что такое Exception Chaining?


Происходит, когда исключение вызывается в ответ на другое исключение. Это позволяет нам открыть полную историю нашей поднятой проблемы:

try {

task.readConfigFile();

} catch (FileNotFoundException ex) {

throw new TaskException ("Could not perform task", ex);

}



## Q9. Что такое Stacktrace и как она связана с Exception?


Трассировка стека предоставляет имена классов и методов, которые были вызваны, от запуска приложения до момента возникновения исключения.

Это очень полезный инструмент отладки, так как он позволяет нам точно определить, где в приложении возникло исключение, и первоначальные причины, которые привели к нему.



## Q10. Зачем вам Subclass Exception?


Если тип исключения не представлен теми, которые уже существуют на платформе **Java, или если вам нужно предоставить дополнительную информацию клиентскому коду для более точной обработки, вам следует создать пользовательское исключение.

Решение о том, следует ли проверять или не проверять пользовательское исключение, полностью зависит от бизнес-кейса. Однако, как правило, если можно ожидать, что код, использующий ваше исключение, восстановится после него, создайте проверенное исключение, в противном случае сделайте его непроверенным.

Кроме того, вы должны наследовать от наиболее конкретного подкласса **Exception, который тесно связан с тем, который вы хотите сгенерировать. Если такого класса нет, то выберите **Exception** в качестве родителя.



## Q11. Каковы некоторые преимущества Exceptions?


Традиционные методы обнаружения и обработки ошибок часто приводят к тому, что спагетти-код трудно поддерживать и трудно читать. Однако исключения позволяют нам отделить основную логику нашего приложения от деталей того, что делать, когда происходит что-то неожиданное.

Кроме того, поскольку **JVM** просматривает стек вызовов в обратном направлении, чтобы найти любые методы, заинтересованные в обработке конкретного исключения; мы получаем возможность распространять ошибку вверх по стеку вызовов без написания дополнительного кода.

Кроме того, поскольку все исключения, создаваемые в программе, являются объектами, их можно группировать или классифицировать на основе иерархии классов. Это позволяет нам перехватывать группу исключений в одном обработчике исключений, указывая суперкласс исключения в блоке перехвата.



## Q12. (ВАЖНО) - Как создать какой-либо Exception внутри тела лямбда-выражения?


При использовании стандартного функционального интерфейса, уже предоставленного **Java, вы можете генерировать только непроверенные исключения, потому что стандартные функциональные интерфейсы не имеют предложения **«throws»** в сигнатурах методов:

List&lt;Integer&gt; integers = Arrays.asList(3, 9, 7, 0, 10, 20);

integers.forEach(i -> {

if (i == 0) {

throw new IllegalArgumentException("Zero not allowed");

}

System.out.println(Math.PI/i);

});

Однако, если вы используете пользовательский функциональный интерфейс, возможно создание проверенных исключений:

@FunctionalInterface

public static interface CheckedFunction&lt;T&gt; {

void apply(T t) throws Exception;

}

public void processTasks(List&lt;Task&gt; taks, CheckedFunction&lt;Task&gt; checkedFunction) {

for (Task task: taks) {

try {

checkedFunction.apply(task);

} catch (Exception e) {

}

}

}

processTasks (taskList, t -> {

throw new Exception ("Something happened");

});



## Q13. Как переопределить метод, выдающий Exception?


Несколько правил определяют, как должны объявляться исключения в контексте наследования.

Когда метод родительского класса не генерирует никаких исключений, метод дочернего класса не может генерировать никаких проверенных исключений, но может генерировать любые непроверенные исключения.

Вот пример кода, чтобы продемонстрировать это:

class Parent{

void doSomething() {

}

}

class Child extends Parent{

void doSomething() throws IllegalArgumentException {

}

}

Следующий пример не скомпилируется, так как метод переопределения выдает проверенное исключение, не объявленное в переопределенном методе:

class Parent{

void doSomething() {

}

}

class Child extends Parent{

void doSomething() throws IOException {}

}

Когда метод родительского класса выдает одно или несколько проверенных исключений, метод дочернего класса может выдать любое непроверенное исключение; все, никакие или подмножество объявленных проверенных исключений, и даже большее их количество, если они имеют ту же область действия или более узкую.

Вот пример кода, который успешно следует предыдущему правилу:

class Parent{

void doSomething() throws IOException, ParseException {

}

void doSomethingElse() throws IOException {

}

}

class Child extends Parent{

void doSomething() throws IOException {

}

void doSomethingElse() throws FileNotFoundException, EOFException {

}

}

Обратите внимание, что оба метода соблюдают правило. Первый генерирует меньше исключений, чем переопределенный метод, а второй, хотя и генерирует больше; они уже по объему.

Однако, если мы попытаемся сгенерировать проверенное исключение, которое метод родительского класса не объявляет, или мы сгенерируем исключение с более широкой областью действия; мы получим ошибку компиляции:

class Parent{

void doSomething() throws FileNotFoundException {

}

}

class Child extends Parent{

void doSomething() throws IOException {

}

}

Когда метод родительского класса имеет предложение **throws** с непроверяемым исключением, метод дочернего класса может не генерировать ни одного или любое количество непроверенных исключений, даже если они не связаны между собой.

Вот пример, который соблюдает правило:

class Parent {

void doSomething() throws IllegalArgumentException {

}

}

class Child extends Parent {

void doSomething()

throws ArithmeticException, BufferOverflowException {

}

}



## Q14. Будет ли компилироваться следующий код?

```java
void doSomething() {
    throw new RuntimeException(new Exception("Chained Exception"));
}
```

Да. При цепочке исключений компилятор заботится только о первом в цепочке, и, поскольку он обнаруживает непроверенное исключение, нам не нужно добавлять предложение **throws.



## Q15. (ВАЖНО) - Есть ли способ генерировать Сhecked Exception из метода, который не имеет пункта throws?


Да. Мы можем воспользоваться стиранием типов, выполняемым компилятором, и заставить его думать, что мы выбрасываем непроверенное исключение, когда на самом деле; мы выбрасываем проверенное исключение:

public &lt;T extends throwable&gt; T sneakyThrow(Throwable ex) throws T{

throw (T) ex;

}

public void methodWithoutThrows() {

this.&lt;RuntimeException&gt;sneakyThrow (new Exception ("Checked Exception"));

}
```

## Практические примеры на Java + Spring

### Пример 1: Обработка исключений в Spring MVC

```java
/**
 * Глобальный обработчик исключений для REST API
 * Демонстрирует обработку различных типов исключений
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
 
 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
 
 /**
 * Обработка пользовательских исключений
 */
 @ExceptionHandler(UserNotFoundException.class)
 public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
 logger.error("User not found: {}", ex.getMessage());
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).timestamp(LocalDateTime.now()).build();
 
 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
 }
 
 /**
 * Обработка ошибок валидации
 */
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ErrorResponse> handleValidationExceptions(
 MethodArgumentNotValidException ex) {
 
 Map<String, String> errors = new HashMap<>();
 ex.getBindingResult().getAllErrors().forEach(error -> {
 String fieldName = ((FieldError) error).getField();
 String errorMessage = error.getDefaultMessage();
 errors.put(fieldName, errorMessage);
 });
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("Ошибка валидации").errors(errors).timestamp(LocalDateTime.now()).build();
 
 return ResponseEntity.badRequest().body(error);
 }
 
 /**
 * Обработка всех остальных исключений
 */
 @ExceptionHandler(Exception.class)
 public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
 logger.error("Unhandled exception", ex);
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Внутренняя ошибка сервера").timestamp(LocalDateTime.now()).build();
 
 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
 }
}
```

### Пример 2: Создание кастомных исключений

```java
/**
 * Базовое исключение для бизнес-логики
 */
public class BusinessException extends RuntimeException {
 
 private final String errorCode;
 private final Map<String, Object> details;
 
 public BusinessException(String errorCode, String message) {
 super(message);
 this.errorCode = errorCode;
 this.details = new HashMap<>();
 }
 
 public BusinessException(String errorCode, String message, Throwable cause) {
 super(message, cause);
 this.errorCode = errorCode;
 this.details = new HashMap<>();
 }
 
 public String getErrorCode() {
 return errorCode;
 }
 
 public Map<String, Object> getDetails() {
 return details;
 }
 
 public BusinessException withDetail(String key, Object value) {
 this.details.put(key, value);
 return this;
 }
}

/**
 * Специализированные исключения
 */
public class UserNotFoundException extends BusinessException {
 public UserNotFoundException(Long userId) {
 super("USER_NOT_FOUND", "User not found with id: " + userId);
 withDetail("userId", userId);
 }
}

public class InsufficientFundsException extends BusinessException {
 public InsufficientFundsException(BigDecimal required, BigDecimal available) {
 super("INSUFFICIENT_FUNDS", 
 String.format("Required: %s, Available: %s", required, available));
 withDetail("required", required);
 withDetail("available", available);
 }
}
```

### Пример 3: Обработка исключений в транзакциях

```java
/**
 * Сервис с обработкой исключений в транзакциях
 */
@Service
@Transactional
public class TransactionalService {
 
 @Autowired
 private UserRepository userRepository;
 
 @Autowired
 private AccountRepository accountRepository;
 
 /**
 * Транзакция с обработкой исключений
 * При исключении транзакция откатывается
 */
 public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
 try {
 Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow(() -> new AccountNotFoundException(fromAccountId));
 
 Account toAccount = accountRepository.findById(toAccountId).orElseThrow(() -> new AccountNotFoundException(toAccountId));
 
 if (fromAccount.getBalance().compareTo(amount) < 0) {
 throw new InsufficientFundsException(amount, fromAccount.getBalance());
 }
 
 fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
 toAccount.setBalance(toAccount.getBalance().add(amount));
 
 accountRepository.save(fromAccount);
 accountRepository.save(toAccount);
 
 } catch (BusinessException e) {
 // Бизнес-исключения логируем и пробрасываем дальше
 log.error("Business error during transfer", e);
 throw e; // Транзакция откатится автоматически
 } catch (Exception e) {
 // Неожиданные исключения логируем и оборачиваем
 log.error("Unexpected error during transfer", e);
 throw new TransferException("Error during money transfer", e);
 }
 }
}
```

### Пример 4: Exception Chaining для сохранения контекста

```java
/**
 * Демонстрация Exception Chaining
 * Сохранение полного контекста ошибки
 */
@Service
public class ExceptionChainingService {
 
 /**
 * Метод с exception chaining
 */
 public void processOrder(OrderRequest request) {
 try {
 validateOrder(request);
 processPayment(request);
 updateInventory(request);
 } catch (ValidationException e) {
 // Exception chaining - сохранение исходного исключения
 throw new OrderProcessingException(
 "Failed to process order: " + request.getOrderId(), 
 e // Причина исключения
 );
 } catch (PaymentException e) {
 throw new OrderProcessingException(
 "Payment failed for order: " + request.getOrderId(), 
 e
 );
 } catch (InventoryException e) {
 throw new OrderProcessingException(
 "Inventory update failed for order: " + request.getOrderId(), 
 e
 );
 }
 }
 
 /**
 * Получение полной цепочки исключений
 */
 public void logExceptionChain(Throwable throwable) {
 Throwable current = throwable;
 int depth = 0;
 
 while (current!= null && depth < 10) {
 log.error("Exception at depth {}: {}", depth, current.getMessage(), current);
 current = current.getCause();
 depth++;
 }
 }
}
```

### Пример 5: Обработка исключений в Stream API

```java
/**
 * Обработка исключений в Stream API
 * Демонстрирует различные подходы
 */
@Service
public class StreamExceptionHandlingService {
 
 /**
 * Обработка исключений в map операциях
 */
 public List<String> processUsers(List<User> users) {
 return users.stream().map(user -> {
 try {
 return processUser(user);
 } catch (ProcessingException e) {
 log.error("Error processing user: {}", user.getId(), e);
 return null; // Или значение по умолчанию
 }
 }).filter(Objects::nonNull).collect(Collectors.toList());
 }
 
 /**
 * Использование функционального интерфейса для обработки исключений
 */
 @FunctionalInterface
 public interface ThrowingFunction<T, R> {
 R apply(T t) throws Exception;
 }
 
 public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
 return t -> {
 try {
 return f.apply(t);
 } catch (Exception e) {
 throw new RuntimeException(e);
 }
 };
 }
 
 /**
 * Использование unchecked функции
 */
 public List<String> processUsersUnchecked(List<User> users) {
 return users.stream().map(unchecked(this::processUser)).collect(Collectors.toList());
 }
 
 private String processUser(User user) throws ProcessingException {
 // Обработка пользователя
 return user.getName().toUpperCase();
 }
}
```

## Best Practices для Java Exceptions

### 1. Использование правильных типов исключений

**✅ Правильно:
```java
// Использование специфичных исключений
public User findUser(Long id) {
 return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)); // ✅ Специфичное исключение
}

// Checked exception для ошибок, которые можно обработать
public void saveFile(String content) throws IOException { // ✅ Checked exception
 Files.write(Paths.get("file.txt"), content.getBytes());
}

// Unchecked exception для ошибок программирования
public void validateInput(String input) {
 if (input == null) {
 throw new IllegalArgumentException("Input cannot be null"); // ✅ Unchecked exception
 }
}
```

**❌ Неправильно:
```java
// Использование слишком общих исключений
public User findUser(Long id) {
 return userRepository.findById(id).orElseThrow(() -> new Exception("User not found")); // ❌ Слишком общее исключение
}

// Checked exception для ошибок программирования
public void validateInput(String input) throws Exception { // ❌ Неправильный тип
 if (input == null) {
 throw new Exception("Input cannot be null");
 }
}
```

### 2. Сохранение контекста исключений

**✅ Правильно:
```java
// Exception chaining для сохранения контекста
try {
 processOrder(request);
} catch (ValidationException e) {
 throw new OrderProcessingException("Failed to process order", e); // ✅ Сохранение причины
}
```

**❌ Неправильно:
```java
// Потеря контекста исключения
try {
 processOrder(request);
} catch (ValidationException e) {
 throw new OrderProcessingException("Failed to process order"); // ❌ Потеря исходного исключения
}
```

### 3. Логирование исключений

**✅ Правильно:
```java
// Логирование с полным контекстом
try {
 processOrder(request);
} catch (Exception e) {
 log.error("Failed to process order: {}", request.getOrderId(), e); // ✅ Логирование с контекстом
 throw e;
}
```

**❌ Неправильно:
```java
// Логирование без контекста
try {
 processOrder(request);
} catch (Exception e) {
 log.error("Error occurred"); // ❌ Нет контекста
 throw e;
}
```

## Troubleshooting Java Exceptions

### Проблема 1: Suppressed Exceptions в try-with-resources

**Симптомы:
- Потеря исключений при закрытии ресурсов
- Неполная информация об ошибках

**Решение:
```java
// Правильная обработка suppressed exceptions
public void processFile(String filename) throws IOException {
 try (FileReader reader = new FileReader(filename);
 BufferedReader bufferedReader = new BufferedReader(reader)) {
 
 // Обработка файла
 String line = bufferedReader.readLine();
 
 } catch (IOException e) {
 // Проверка suppressed exceptions
 Throwable[] suppressed = e.getSuppressed();
 for (Throwable t: suppressed) {
 log.error("Suppressed exception during resource cleanup", t);
 }
 throw e;
 }
}
```

### Проблема 2: Проблемы с exception handling в лямбда-выражениях

**Симптомы:
- Ошибки компиляции при использовании checked exceptions в лямбдах
- Неудобная обработка исключений

**Решение:
```java
// Создание функционального интерфейса для checked exceptions
@FunctionalInterface
public interface ThrowingFunction<T, R> {
 R apply(T t) throws Exception;
}

// Утилита для преобразования в обычную функцию
public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
 return t -> {
 try {
 return f.apply(t);
 } catch (Exception e) {
 throw new RuntimeException(e);
 }
 };
}

// Использование
List<String> results = users.stream().map(unchecked(user -> processUser(user))) // ✅ Обработка checked exceptions.collect(Collectors.toList());
```

### Проблема 3: Проблемы с exception handling в транзакциях

**Симптомы:
- Транзакции не откатываются при исключениях
- Неожиданное поведение при обработке исключений

**Решение:
```java
// Правильная настройка rollback для исключений
@Service
@Transactional(rollbackFor = {BusinessException.class, DataAccessException.class})
public class TransactionalService {
 
 /**
 * Транзакция откатится при BusinessException
 */
 public void processOrder(OrderRequest request) {
 // При BusinessException транзакция откатится
 if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
 throw new BusinessException("INVALID_AMOUNT", "Amount must be positive");
 }
 //...
 }
 
 /**
 * Транзакция НЕ откатится при определенных исключениях
 */
 @Transactional(noRollbackFor = {ValidationException.class})
 public void validateAndProcess(OrderRequest request) {
 // При ValidationException транзакция НЕ откатится
 if (request.getUserId() == null) {
 throw new ValidationException("User ID is required");
 }
 //...
 }
}
```

## Заключение

Обработка исключений является критически важным аспектом разработки на Java. Понимание различных типов исключений и способов их обработки необходимо для создания надежных приложений.Ключевые моменты для запоминания:

1. Checked vs Unchecked** - checked исключения требуют обработки, unchecked - нет
2. Exception Hierarchy** - понимание иерархии исключений
3. Exception Chaining** - сохранение контекста через cause
4. Try-with-resources** - автоматическое управление ресурсами
5. Best Practices** - использование специфичных исключений, логирование, сохранение контекста

**Рекомендации для собеседования:

- Уметь объяснить разницу между checked и unchecked исключениями
- Знать, когда использовать каждый тип исключения
- Понимать exception chaining и suppressed exceptions
- Уметь обрабатывать исключения в Stream API и лямбда-выражениях
- Знать, как исключения работают с транзакциями в Spring

---

**Последнее обновление: 2026-01-25
