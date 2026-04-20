---
title: "Java Streams и функциональное программирование"
description: "Java Streams API и функциональное программирование предоставляют мощные инструменты для работы с коллекциями данных. Этот документ охватывает Stream API, лямбда-выражения, функциональные интерфейсы, Optional и современные паттерны функционального программирования в Java."
tags:
  - languages
  - java
  - java-streams-fp
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Java Streams и функциональное программирование

**Java Streams API** и функциональное программирование предоставляют мощные инструменты для работы с коллекциями данных. Этот документ охватывает **Stream API**, лямбда-выражения, функциональные интерфейсы, **Optional** и современные паттерны функционального программирования в **Java**.

## Полезные ссылки
- [Java Stream API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html)
- [Functional Programming in Java](https://www.baeldung.com/java-functional-programming)
- [Optional Best Practices](https://www.baeldung.com/java-optional)
- [Java Functional Interfaces](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/package-summary.html)


### См. также
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-reactive-project-reactor|Java Reactive: Project Reactor]]
## Содержание

- [Основы функционального программирования](#основы-функционального-программирования)
  - [Функциональные интерфейсы](#функциональные-интерфейсы)
  - [Лямбда-выражения и ссылки на методы](#лямбда-выражения-и-ссылки-на-методы)
- [Stream API](#stream-api)
  - [Создание потоков](#создание-потоков)
  - [Промежуточные операции](#промежуточные-операции)
  - [Терминальные операции](#терминальные-операции)
- [Optional](#optional)
  - [Работа с Optional](#работа-с-optional)
- [Продвинутые паттерны функционального программирования](#продвинутые-паттерны-функционального-программирования)
  - [Монады и функторы](#монады-и-функторы)
- [Производительность и ограничения](#производительность-и-ограничения)
  - [Оптимизация работы с потоками](#оптимизация-работы-с-потоками)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы функционального программирования

### Функциональные интерфейсы
```java
// Основные функциональные интерфейсы в java.util.function
public class FunctionalInterfacesDemo {

    public void demonstrateFunctionalInterfaces() {
        // Predicate<T> - возвращает boolean
        Predicate<String> isNotEmpty = str -> str != null && !str.trim().isEmpty();
        Predicate<Integer> isEven = n -> n % 2 == 0;

        // Function<T, R> - преобразует T в R
        Function<String, Integer> stringLength = String::length;
        Function<Integer, String> intToString = Object::toString;
        Function<User, String> getUserName = User::getName;

        // Consumer<T> - принимает T, ничего не возвращает
        Consumer<String> printer = System.out::println;
        Consumer<User> userProcessor = user -> processUser(user);

        // Supplier<T> - ничего не принимает, возвращает T
        Supplier<String> uuidGenerator = () -> UUID.randomUUID().toString();
        Supplier<LocalDateTime> currentTime = LocalDateTime::now;

        // UnaryOperator<T> - принимает и возвращает T
        UnaryOperator<String> toUpperCase = String::toUpperCase;
        UnaryOperator<Integer> square = n -> n * n;

        // BinaryOperator<T> - принимает два T, возвращает T
        BinaryOperator<Integer> sum = Integer::sum;
        BinaryOperator<String> concat = String::concat;

        // BiFunction<T, U, R> - принимает T и U, возвращает R
        BiFunction<String, String, String> joinWithSpace = (s1, s2) -> s1 + " " + s2;
        BiFunction<Integer, Integer, Double> divide = (a, b) -> (double) a / b;

        // BiConsumer<T, U> - принимает T и U, ничего не возвращает
        BiConsumer<String, Integer> printWithIndex = (str, index) ->
            System.out.println(index + ": " + str);

        // Примеры использования
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        names.stream()
             .filter(isNotEmpty)
             .map(stringLength)
             .forEach(printer);

        Optional<String> firstName = names.stream()
                                         .filter(isNotEmpty)
                                         .findFirst();
    }

    private void processUser(User user) {
        System.out.println("Processing user: " + user.getName());
    }

    // Пользовательские функциональные интерфейсы
    @FunctionalInterface
    interface Validator<T> {
        ValidationResult validate(T value);

        default Validator<T> and(Validator<T> other) {
            return value -> {
                ValidationResult result1 = this.validate(value);
                ValidationResult result2 = other.validate(value);

                if (!result1.isValid()) return result1;
                if (!result2.isValid()) return result2;

                return ValidationResult.valid();
            };
        }

        default Validator<T> or(Validator<T> other) {
            return value -> {
                ValidationResult result1 = this.validate(value);
                return result1.isValid() ? result1 : other.validate(value);
            };
        }
    }

    static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }
}
```

### Лямбда-выражения и ссылки на методы
```java
public class LambdaExpressionsDemo {

    public void demonstrateLambdas() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");

        // Лямбда-выражения
        names.forEach(name -> System.out.println("Hello " + name));

        // Ссылка на метод
        names.forEach(System.out::println);

        // Ссылка на метод экземпляра
        names.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

        // Ссылка на конструктор
        Supplier<List<String>> listSupplier = ArrayList::new;
        Function<Integer, String[]> arrayCreator = String[]::new;

        // Ссылка на метод суперкласса или текущего класса
        names.stream()
             .filter(this::isValidName)
             .forEach(this::processName);
    }

    // Эффективный final (можно использовать в лямбдах)
    public void demonstrateEffectiveFinal() {
        String prefix = "Mr. ";
        List<String> names = Arrays.asList("Smith", "Johnson", "Brown");

        // prefix - effectively final (не изменяется после инициализации)
        names.stream()
             .map(name -> prefix + name)
             .forEach(System.out::println);

        // Локальные переменные в лямбдах
        for (int i = 0; i < 3; i++) {
            final int index = i; // Делаем effectively final
            names.stream()
                 .filter(name -> name.length() > index)
                 .forEach(System.out::println);
        }
    }

    // Замыкания
    public Supplier<String> createGreeter(String greeting) {
        return () -> greeting + " World!"; // greeting захватывается
    }

    public Function<String, String> createFormatter(String prefix, String suffix) {
        return text -> prefix + text + suffix; // prefix и suffix захватываются
    }

    // Сравнение с анонимными классами
    public void compareWithAnonymousClasses() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        // Анонимный класс
        Comparator<String> anonymousComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        };

        // Лямбда-выражение
        Comparator<String> lambdaComparator = (s1, s2) -> Integer.compare(s1.length(), s2.length());

        // Ссылка на метод
        Comparator<String> methodRefComparator = Comparator.comparingInt(String::length);

        names.stream()
             .sorted(anonymousComparator)
             .forEach(System.out::println);

        names.stream()
             .sorted(lambdaComparator)
             .forEach(System.out::println);

        names.stream()
             .sorted(methodRefComparator)
             .forEach(System.out::println);
    }

    // Обработка исключений в лямбдах
    public void handleExceptionsInLambdas() {
        List<String> filePaths = Arrays.asList("file1.txt", "file2.txt", "file3.txt");

        // Неправильно - исключение не может быть брошено из лямбды
        // filePaths.stream()
        //          .map(path -> Files.readString(Paths.get(path))) // IOException!
        //          .forEach(System.out::println);

        // Правильно - оборачиваем в try-catch
        filePaths.stream()
                 .map(path -> {
                     try {
                         return Files.readString(Paths.get(path));
                     } catch (IOException e) {
                         throw new RuntimeException("Failed to read file: " + path, e);
                     }
                 })
                 .forEach(System.out::println);

        // Или используем вспомогательный метод
        filePaths.stream()
                 .map(this::readFileSafely)
                 .filter(Optional::isPresent)
                 .map(Optional::get)
                 .forEach(System.out::println);
    }

    private boolean isValidName(String name) {
        return name != null && name.length() > 2;
    }

    private void processName(String name) {
        System.out.println("Processing: " + name);
    }

    private Optional<String> readFileSafely(String path) {
        try {
            return Optional.of(Files.readString(Paths.get(path)));
        } catch (IOException e) {
            System.err.println("Failed to read file: " + path);
            return Optional.empty();
        }
    }
}
```

## Stream API

### Создание потоков
```java
public class StreamCreationDemo {

    public void demonstrateStreamCreation() {
        // Из коллекций
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        Stream<String> parallelStream = list.parallelStream();

        // Из массивов
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        Stream<String> streamFromArrayRange = Arrays.stream(array, 1, 3); // "y", "z"

        // Из значений
        Stream<String> streamOfValues = Stream.of("one", "two", "three");
        Stream<String> emptyStream = Stream.empty();

        // Бесконечные потоки
        Stream<Integer> infiniteStream = Stream.iterate(1, n -> n + 1);
        Stream<String> infiniteRandom = Stream.generate(() -> UUID.randomUUID().toString());

        // Ограничение бесконечных потоков
        List<Integer> firstTen = infiniteStream.limit(10).collect(Collectors.toList());

        // Из файлов
        try {
            Stream<String> lines = Files.lines(Paths.get("data.txt"));
            // Обработка строк файла
            lines.close();
        } catch (IOException e) {
            // Обработка ошибки
        }

        // Из примитивных типов
        IntStream intStream = IntStream.range(1, 10); // 1, 2, 3, ..., 9
        LongStream longStream = LongStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5
        DoubleStream doubleStream = DoubleStream.of(1.1, 2.2, 3.3);

        // Из строк
        IntStream chars = "Hello World".chars(); // int values of characters

        // Builder pattern
        Stream<String> streamFromBuilder = Stream.<String>builder()
            .add("first")
            .add("second")
            .add("third")
            .build();
    }

    // Пользовательские источники потоков
    public Stream<String> readLinesFromSocket(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));

        return reader.lines().onClose(() -> {
            try {
                reader.close();
                socket.close();
            } catch (IOException e) {
                // Обработка ошибки закрытия
            }
        });
    }

    // Stream из Iterator
    public <T> Stream<T> streamFromIterator(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    // Stream из Supplier
    public Stream<Integer> fibonacciStream() {
        return Stream.iterate(new int[]{0, 1},
            fib -> new int[]{fib[1], fib[0] + fib[1]})
            .map(fib -> fib[0]);
    }
}
```

### Промежуточные операции
```java
public class IntermediateOperationsDemo {

    public void demonstrateIntermediateOperations() {
        List<User> users = createSampleUsers();

        // filter - фильтрация элементов
        List<User> adults = users.stream()
                                .filter(user -> user.getAge() >= 18)
                                .collect(Collectors.toList());

        // map - преобразование элементов
        List<String> names = users.stream()
                                 .map(User::getName)
                                 .collect(Collectors.toList());

        // flatMap - плоское преобразование
        List<String> allSkills = users.stream()
                                     .flatMap(user -> user.getSkills().stream())
                                     .distinct()
                                     .collect(Collectors.toList());

        // distinct - удаление дубликатов
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
        List<Integer> distinctNumbers = numbers.stream()
                                              .distinct()
                                              .collect(Collectors.toList());

        // sorted - сортировка
        List<User> sortedByAge = users.stream()
                                     .sorted(Comparator.comparingInt(User::getAge))
                                     .collect(Collectors.toList());

        List<User> sortedByNameDesc = users.stream()
                                          .sorted(Comparator.comparing(User::getName).reversed())
                                          .collect(Collectors.toList());

        // limit и skip - ограничение и пропуск
        List<User> firstThree = users.stream()
                                    .limit(3)
                                    .collect(Collectors.toList());

        List<User> skipFirstTwo = users.stream()
                                      .skip(2)
                                      .collect(Collectors.toList());

        // peek - отладка потока
        List<User> debuggedStream = users.stream()
                                        .filter(user -> user.getAge() >= 18)
                                        .peek(user -> System.out.println("Filtered: " + user.getName()))
                                        .map(user -> {
                                            user.setName(user.getName().toUpperCase());
                                            return user;
                                        })
                                        .peek(user -> System.out.println("Mapped: " + user.getName()))
                                        .collect(Collectors.toList());

        // takeWhile и dropWhile (Java 9+)
        List<Integer> numbers2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> takenWhile = numbers2.stream()
                                          .takeWhile(n -> n < 5)
                                          .collect(Collectors.toList()); // [1, 2, 3, 4]

        List<Integer> droppedWhile = numbers2.stream()
                                            .dropWhile(n -> n < 5)
                                            .collect(Collectors.toList()); // [5, 6, 7, 8, 9, 10]

        // mapMulti (Java 16+) - множественное преобразование
        List<String> expandedList = users.stream()
                                        .<String>mapMulti((user, consumer) -> {
                                            consumer.accept(user.getName());
                                            consumer.accept(user.getEmail());
                                            user.getSkills().forEach(consumer);
                                        })
                                        .collect(Collectors.toList());
    }

    // Пользовательские промежуточные операции
    public Stream<User> customFilter(Stream<User> stream, Predicate<User> predicate) {
        return stream.filter(predicate);
    }

    public <T, R> Stream<R> customMap(Stream<T> stream, Function<T, R> mapper) {
        return stream.map(mapper);
    }

    // Композиция операций
    public Stream<User> complexFiltering(Stream<User> users) {
        return users
            .filter(user -> user.getAge() >= 18)
            .filter(user -> !user.getSkills().isEmpty())
            .filter(user -> user.getName().startsWith("A"));
    }

    // Ленивые операции
    public void demonstrateLaziness() {
        List<User> users = createSampleUsers();

        Stream<User> lazyStream = users.stream()
                                      .filter(user -> {
                                          System.out.println("Filtering: " + user.getName());
                                          return user.getAge() >= 18;
                                      })
                                      .map(user -> {
                                          System.out.println("Mapping: " + user.getName());
                                          return user.getName().toUpperCase();
                                      });

        System.out.println("Stream created, no operations performed yet");

        // Терминальная операция запускает выполнение
        List<String> result = lazyStream.limit(2).collect(Collectors.toList());

        System.out.println("Result: " + result);
    }

    private List<User> createSampleUsers() {
        return Arrays.asList(
            new User("Alice", 25, Arrays.asList("Java", "Spring")),
            new User("Bob", 17, Arrays.asList("Python", "Django")),
            new User("Charlie", 30, Arrays.asList("JavaScript", "React")),
            new User("Diana", 22, Collections.emptyList())
        );
    }

    static class User {
        private String name;
        private int age;
        private List<String> skills;
        private String email;

        public User(String name, int age, List<String> skills) {
            this.name = name;
            this.age = age;
            this.skills = skills;
            this.email = name.toLowerCase() + "@example.com";
        }

        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public List<String> getSkills() { return skills; }
        public String getEmail() { return email; }
    }
}
```

### Терминальные операции
```java
public class TerminalOperationsDemo {

    public void demonstrateTerminalOperations() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // forEach - выполнение действия для каждого элемента
        numbers.stream().forEach(System.out::println);

        // forEachOrdered - выполнение в порядке инкапсуляции
        numbers.parallelStream()
               .forEachOrdered(System.out::println);

        // toArray - преобразование в массив
        Integer[] array = numbers.stream().toArray(Integer[]::new);
        int[] intArray = numbers.stream().mapToInt(Integer::intValue).toArray();

        // collect - сборка в коллекцию
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());

        Set<Integer> numberSet = numbers.stream()
                                       .collect(Collectors.toSet());

        String joinedString = numbers.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "));

        // Группировка
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                                                        .collect(Collectors.partitioningBy(n -> n % 2 == 0));

        Map<Integer, List<Integer>> groupedByModulo = numbers.stream()
                                                            .collect(Collectors.groupingBy(n -> n % 3));

        // Агрегация
        long count = numbers.stream().count();

        Optional<Integer> max = numbers.stream().max(Integer::compare);
        Optional<Integer> min = numbers.stream().min(Integer::compare);

        // Суммирование и статистика
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        double average = numbers.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        IntSummaryStatistics stats = numbers.stream().mapToInt(Integer::intValue).summaryStatistics();

        // Поиск элементов
        Optional<Integer> firstEven = numbers.stream()
                                            .filter(n -> n % 2 == 0)
                                            .findFirst();

        Optional<Integer> anyEven = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .findAny();

        boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
        boolean anyEven2 = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

        // reduce - свертка
        Optional<Integer> sum2 = numbers.stream().reduce(Integer::sum);
        Integer sumWithIdentity = numbers.stream().reduce(0, Integer::sum);

        String concatenated = numbers.stream()
                                    .map(String::valueOf)
                                    .reduce("", (a, b) -> a + ", " + b);

        // collect с кастомным коллектором
        List<Integer> customCollected = numbers.stream()
                                              .collect(ArrayList::new, List::add, List::addAll);
    }

    // Продвинные примеры collect
    public void advancedCollectExamples() {
        List<User> users = createSampleUsers();

        // Группировка с подсчетом
        Map<String, Long> usersByCity = users.stream()
                                            .collect(Collectors.groupingBy(
                                                User::getCity,
                                                Collectors.counting()));

        // Группировка с суммированием
        Map<String, Integer> totalAgeByCity = users.stream()
                                                  .collect(Collectors.groupingBy(
                                                      User::getCity,
                                                      Collectors.summingInt(User::getAge)));

        // Группировка с маппингом
        Map<String, List<String>> skillsByCity = users.stream()
                                                     .collect(Collectors.groupingBy(
                                                         User::getCity,
                                                         Collectors.mapping(User::getName, Collectors.toList())));

        // Многоуровневая группировка
        Map<String, Map<String, List<User>>> usersByCityAndDepartment = users.stream()
                                                                            .collect(Collectors.groupingBy(
                                                                                User::getCity,
                                                                                Collectors.groupingBy(User::getDepartment)));

        // Partitioning
        Map<Boolean, List<User>> partitionedByAge = users.stream()
                                                        .collect(Collectors.partitioningBy(
                                                            user -> user.getAge() >= 25));

        // toMap с обработкой конфликтов
        Map<String, User> usersByName = users.stream()
                                            .collect(Collectors.toMap(
                                                User::getName,
                                                Function.identity(),
                                                (existing, replacement) -> {
                                                    throw new IllegalStateException(
                                                        "Duplicate user name: " + existing.getName());
                                                }));

        // toMap с мержингом
        Map<String, List<User>> usersByDepartment = users.stream()
                                                        .collect(Collectors.groupingBy(User::getDepartment));
    }

    // Параллельные потоки
    public void demonstrateParallelStreams() {
        List<Integer> numbers = IntStream.range(1, 1000000).boxed().collect(Collectors.toList());

        // Последовательная обработка
        long startTime = System.nanoTime();
        long sequentialSum = numbers.stream()
                                   .mapToLong(Integer::longValue)
                                   .sum();
        long sequentialTime = System.nanoTime() - startTime;

        // Параллельная обработка
        startTime = System.nanoTime();
        long parallelSum = numbers.parallelStream()
                                 .mapToLong(Integer::longValue)
                                 .sum();
        long parallelTime = System.nanoTime() - startTime;

        System.out.printf("Sequential: %d ns, Parallel: %d ns%n", sequentialTime, parallelTime);
        System.out.printf("Speedup: %.2fx%n", (double) sequentialTime / parallelTime);

        // Когда использовать параллельные потоки
        List<String> largeList = createLargeStringList();

        // Параллельная обработка для CPU-bound задач
        List<String> processed = largeList.parallelStream()
                                         .map(this::cpuIntensiveOperation)
                                         .collect(Collectors.toList());

        // Избегать параллельности для IO-bound задач
        // largeList.parallelStream()
        //          .map(this::ioOperation) // Плохо!
        //          .collect(Collectors.toList());
    }

    private List<User> createSampleUsers() {
        return Arrays.asList(
            new User("Alice", 25, "New York", "Engineering"),
            new User("Bob", 30, "San Francisco", "Sales"),
            new User("Charlie", 35, "New York", "Engineering"),
            new User("Diana", 28, "Chicago", "HR")
        );
    }

    private List<String> createLargeStringList() {
        return IntStream.range(0, 10000)
                       .mapToObj(String::valueOf)
                       .collect(Collectors.toList());
    }

    private String cpuIntensiveOperation(String input) {
        // Имитация CPU-bound операции
        return input.chars()
                   .mapToObj(c -> String.valueOf((char) c).toUpperCase())
                   .collect(Collectors.joining());
    }

    private String ioOperation(String input) {
        // Имитация IO-bound операции
        try {
            Thread.sleep(10); // Имитация задержки
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return input;
    }

    static class User {
        private String name;
        private int age;
        private String city;
        private String department;

        public User(String name, int age, String city, String department) {
            this.name = name;
            this.age = age;
            this.city = city;
            this.department = department;
        }

        // Геттеры
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getCity() { return city; }
        public String getDepartment() { return department; }
    }
}
```

## Optional

### Работа с Optional
```java
public class OptionalDemo {

    public void demonstrateOptional() {
        // Создание Optional
        Optional<String> empty = Optional.empty();
        Optional<String> withValue = Optional.of("Hello");
        Optional<String> nullable = Optional.ofNullable(null); // Будет empty

        // Проверка наличия значения
        boolean isPresent = withValue.isPresent();
        boolean isEmpty = empty.isEmpty(); // Java 11+

        // Получение значения
        String value = withValue.get(); // Может бросить NoSuchElementException
        String defaultValue = empty.orElse("default");
        String computedDefault = empty.orElseGet(() -> "computed default");

        // orElseThrow
        String mustHaveValue = withValue.orElseThrow();
        String customException = withValue.orElseThrow(
            () -> new IllegalStateException("Value must be present"));

        // Условное выполнение
        withValue.ifPresent(System.out::println);
        withValue.ifPresentOrElse(
            System.out::println,
            () -> System.out.println("No value present"));

        // Преобразование
        Optional<Integer> length = withValue.map(String::length);
        Optional<String> upper = withValue.map(String::toUpperCase);

        // Фильтрация
        Optional<String> filtered = withValue.filter(s -> s.length() > 3);

        // Плоское преобразование
        Optional<String> flatMapped = withValue.flatMap(this::parseJson);
    }

    // Практические примеры использования Optional
    public String getUserDisplayName(User user) {
        return Optional.ofNullable(user)
                      .map(User::getProfile)
                      .map(Profile::getDisplayName)
                      .orElse("Anonymous");
    }

    public Optional<User> findUserById(String userId) {
        // Имитация поиска пользователя
        return userId != null && userId.startsWith("user")
            ? Optional.of(new User(userId))
            : Optional.empty();
    }

    public String getUserEmail(String userId) {
        return findUserById(userId)
            .flatMap(user -> Optional.ofNullable(user.getEmail()))
            .orElse("email@example.com");
    }

    // Композиция Optional операций
    public Optional<Order> processOrder(String orderId) {
        return findOrderById(orderId)
            .filter(this::isOrderValid)
            .filter(order -> isUserActive(order.getUserId()))
            .map(this::enrichOrder);
    }

    // Stream API с Optional
    public List<String> getActiveUserEmails(List<User> users) {
        return users.stream()
                   .map(user -> Optional.ofNullable(user)
                                       .filter(User::isActive)
                                       .map(User::getEmail))
                   .flatMap(Optional::stream) // Java 9+
                   .collect(Collectors.toList());
    }

    // Преобразование null в Optional
    public Optional<String> safeToUpperCase(String input) {
        return Optional.ofNullable(input)
                      .map(String::toUpperCase);
    }

    // Каскадные проверки
    public boolean isUserAuthorized(String userId, String resource) {
        return Optional.ofNullable(userId)
                      .flatMap(this::findUserById)
                      .filter(User::isActive)
                      .map(user -> hasPermission(user, resource))
                      .orElse(false);
    }

    // Optional в качестве возвращаемого типа
    public Optional<BigDecimal> calculateDiscount(String promoCode) {
        return Optional.ofNullable(promoCode)
                      .filter(code -> code.startsWith("DISCOUNT"))
                      .map(code -> new BigDecimal(code.substring(8)));
    }

    private Optional<String> parseJson(String json) {
        try {
            // Имитация парсинга JSON
            return Optional.of(json.replace("{", "").replace("}", ""));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Order> findOrderById(String orderId) {
        // Имитация поиска заказа
        return Optional.of(new Order(orderId, "user123"));
    }

    private boolean isOrderValid(Order order) {
        return order != null && order.getUserId() != null;
    }

    private boolean isUserActive(String userId) {
        return userId != null && userId.startsWith("user");
    }

    private Order enrichOrder(Order order) {
        // Обогащение заказа дополнительной информацией
        order.setStatus("ENRICHED");
        return order;
    }

    private boolean hasPermission(User user, String resource) {
        // Проверка разрешений
        return true;
    }

    // Вспомогательные классы
    static class User {
        private String id;
        private Profile profile;
        private String email;
        private boolean active;

        public User(String id) {
            this.id = id;
            this.email = id + "@example.com";
            this.active = true;
        }

        public Profile getProfile() { return profile; }
        public String getEmail() { return email; }
        public boolean isActive() { return active; }
    }

    static class Profile {
        private String displayName = "Default User";
        public String getDisplayName() { return displayName; }
    }

    static class Order {
        private String id;
        private String userId;
        private String status;

        public Order(String id, String userId) {
            this.id = id;
            this.userId = userId;
        }

        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
```

## Продвинутые паттерны функционального программирования

### Монады и функторы
```java
public class FunctionalPatternsDemo {

    // Functor pattern - map для контейнеров
    public interface Functor<T> {
        <R> Functor<R> map(Function<T, R> mapper);
    }

    // Optional как функтор
    public void optionalAsFunctor() {
        Optional<String> name = Optional.of("alice");

        Optional<String> upperName = name.map(String::toUpperCase);
        Optional<Integer> nameLength = name.map(String::length);

        // Композиция функций
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;

        Optional<String> result = name.map(trim.andThen(upper));
    }

    // Stream как функтор
    public void streamAsFunctor() {
        Stream<String> names = Stream.of("alice", "bob", "charlie");

        Stream<String> upperNames = names.map(String::toUpperCase);
        Stream<Integer> nameLengths = names.map(String::length);

        // Композиция
        Function<String, String> addPrefix = s -> "User: " + s;
        Function<String, String> addSuffix = s -> s + "!";

        names.map(addPrefix.andThen(addSuffix))
             .forEach(System.out::println);
    }

    // Try как монада (обработка ошибок)
    public static class Try<T> {
        private final T value;
        private final Exception exception;

        private Try(T value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public static <T> Try<T> success(T value) {
            return new Try<>(value, null);
        }

        public static <T> Try<T> failure(Exception exception) {
            return new Try<>(null, exception);
        }

        public boolean isSuccess() {
            return exception == null;
        }

        public T getValue() {
            if (!isSuccess()) {
                throw new RuntimeException("Cannot get value from failed Try", exception);
            }
            return value;
        }

        public Exception getException() {
            return exception;
        }

        public <R> Try<R> map(Function<T, R> mapper) {
            if (isSuccess()) {
                try {
                    return success(mapper.apply(value));
                } catch (Exception e) {
                    return failure(e);
                }
            }
            return failure(exception);
        }

        public <R> Try<R> flatMap(Function<T, Try<R>> mapper) {
            if (isSuccess()) {
                try {
                    return mapper.apply(value);
                } catch (Exception e) {
                    return failure(e);
                }
            }
            return failure(exception);
        }

        public T orElse(T defaultValue) {
            return isSuccess() ? value : defaultValue;
        }

        public Try<T> orElseTry(Supplier<T> supplier) {
            if (isSuccess()) {
                return this;
            }
            try {
                return success(supplier.get());
            } catch (Exception e) {
                return failure(e);
            }
        }
    }

    // Использование Try монады
    public void demonstrateTryMonad() {
        Try<Integer> result = divideSafely(10, 2)
            .flatMap(this::multiplyByTwo)
            .flatMap(this::convertToString)
            .map(Integer::valueOf);

        if (result.isSuccess()) {
            System.out.println("Result: " + result.getValue());
        } else {
            System.err.println("Error: " + result.getException().getMessage());
        }
    }

    private Try<Integer> divideSafely(int a, int b) {
        try {
            return Try.success(a / b);
        } catch (ArithmeticException e) {
            return Try.failure(e);
        }
    }

    private Try<Integer> multiplyByTwo(int value) {
        return Try.success(value * 2);
    }

    private Try<String> convertToString(int value) {
        return Try.success(String.valueOf(value));
    }

    // Either для обработки альтернативных результатов
    public static class Either<L, R> {
        private final L left;
        private final R right;
        private final boolean isRight;

        private Either(L left, R right, boolean isRight) {
            this.left = left;
            this.right = right;
            this.isRight = isRight;
        }

        public static <L, R> Either<L, R> left(L value) {
            return new Either<>(value, null, false);
        }

        public static <L, R> Either<L, R> right(R value) {
            return new Either<>(null, value, true);
        }

        public boolean isLeft() { return !isRight; }
        public boolean isRight() { return isRight; }

        public L getLeft() {
            if (isRight) throw new IllegalStateException("Cannot get left value from Right");
            return left;
        }

        public R getRight() {
            if (!isRight) throw new IllegalStateException("Cannot get right value from Left");
            return right;
        }

        public <R2> Either<L, R2> map(Function<R, R2> mapper) {
            return isRight ? right(mapper.apply(right)) : Either.<L, R2>left(left);
        }

        public <R2> Either<L, R2> flatMap(Function<R, Either<L, R2>> mapper) {
            return isRight ? mapper.apply(right) : Either.<L, R2>left(left);
        }

        public void fold(Consumer<L> leftConsumer, Consumer<R> rightConsumer) {
            if (isRight) {
                rightConsumer.accept(right);
            } else {
                leftConsumer.accept(left);
            }
        }
    }

    // Reader монада для dependency injection
    public static class Reader<D, A> {
        private final Function<D, A> run;

        private Reader(Function<D, A> run) {
            this.run = run;
        }

        public static <D, A> Reader<D, A> of(Function<D, A> run) {
            return new Reader<>(run);
        }

        public A run(D dependency) {
            return run.apply(dependency);
        }

        public <B> Reader<D, B> map(Function<A, B> mapper) {
            return new Reader<>(d -> mapper.apply(run.apply(d)));
        }

        public <B> Reader<D, B> flatMap(Function<A, Reader<D, B>> mapper) {
            return new Reader<>(d -> mapper.apply(run.apply(d)).run(d));
        }

        public static <D, A, B> Reader<D, B> map2(Reader<D, A> ra, Reader<D, B> rb,
                                                 BiFunction<A, B, B> f) {
            return ra.flatMap(a -> rb.map(b -> f.apply(a, b)));
        }
    }

    // Пример использования Reader для DI
    public void demonstrateReader() {
        // Компоненты зависят от конфигурации
        Reader<Config, Database> databaseReader = Reader.of(config -> new Database(config.getDbUrl()));
        Reader<Config, Cache> cacheReader = Reader.of(config -> new Cache(config.getCacheUrl()));
        Reader<Config, UserService> userServiceReader = Reader.of(config ->
            new UserService(databaseReader.run(config), cacheReader.run(config)));

        // Выполнение с конкретной конфигурацией
        Config config = new Config("jdbc:h2:mem:test", "redis://localhost:6379");
        UserService userService = userServiceReader.run(config);
    }

    // Вспомогательные классы
    static class Config {
        private final String dbUrl;
        private final String cacheUrl;

        public Config(String dbUrl, String cacheUrl) {
            this.dbUrl = dbUrl;
            this.cacheUrl = cacheUrl;
        }

        public String getDbUrl() { return dbUrl; }
        public String getCacheUrl() { return cacheUrl; }
    }

    static class Database {
        public Database(String url) { /* ... */ }
    }

    static class Cache {
        public Cache(String url) { /* ... */ }
    }

    static class UserService {
        public UserService(Database db, Cache cache) { /* ... */ }
    }
}
```

## Производительность и ограничения

### Оптимизация работы с потоками
```java
public class StreamPerformanceDemo {

    public void demonstratePerformanceConsiderations() {
        List<User> users = createLargeUserList();

        // ❌ Плохо: множественные проходы по потоку
        long count1 = users.stream().filter(User::isActive).count();
        long count2 = users.stream().filter(User::isActive).mapToLong(User::getId).sum();

        // ✅ Хорошо: один проход с collect
        UserStats stats = users.stream()
                              .filter(User::isActive)
                              .collect(Collectors.summarizingLong(User::getId));

        // ❌ Плохо: боксинг примитивов
        int sum1 = users.stream()
                       .mapToInt(User::getAge)
                       .boxed()  // Не нужно!
                       .mapToInt(Integer::intValue)
                       .sum();

        // ✅ Хорошо: использование примитивных потоков
        int sum2 = users.stream()
                       .mapToInt(User::getAge)
                       .sum();

        // ❌ Плохо: создание новых объектов в map
        List<String> names1 = users.stream()
                                  .map(user -> "User: " + user.getName())
                                  .collect(Collectors.toList());

        // ✅ Хорошо: использование ссылки на метод
        List<String> names2 = users.stream()
                                  .map(User::getName)
                                  .map(name -> "User: " + name)
                                  .collect(Collectors.toList());

        // Параллельные потоки - когда использовать
        List<String> largeList = createLargeStringList();

        // Для CPU-bound операций
        long cpuBoundTime = measureTime(() ->
            largeList.parallelStream()
                    .map(this::cpuIntensiveOperation)
                    .count()
        );

        // Для IO-bound операций использовать обычные потоки или async
        long ioBoundTime = measureTime(() ->
            largeList.stream()
                    .map(this::ioOperation)
                    .count()
        );

        // Избегать параллельности для небольших коллекций
        List<String> smallList = Arrays.asList("a", "b", "c");

        // Лучше использовать обычный stream
        List<String> processed = smallList.stream()
                                         .map(String::toUpperCase)
                                         .collect(Collectors.toList());

        // Кэширование результатов expensive операций
        Map<String, Optional<User>> userCache = new ConcurrentHashMap<>();

        users.stream()
             .map(user -> userCache.computeIfAbsent(user.getEmail(),
                 email -> findUserByEmail(email)))
             .filter(Optional::isPresent)
             .map(Optional::get)
             .forEach(this::processUser);
    }

    // Эффективная фильтрация и преобразование
    public List<String> processUsersEfficiently(List<User> users) {
        return users.stream()
                   // Фильтрация в начале для уменьшения объема данных
                   .filter(User::isActive)
                   .filter(user -> user.getAge() >= 18)

                   // Преобразование
                   .map(user -> new UserDTO(user.getId(), user.getName()))

                   // Сортировка только если необходимо
                   .sorted(Comparator.comparing(UserDTO::getName))

                   // Ограничение результатов
                   .limit(100)

                   // Финальное преобразование
                   .map(UserDTO::getName)
                   .collect(Collectors.toList());
    }

    // Использование takeWhile для раннего прекращения
    public Optional<User> findFirstAdult(List<User> users) {
        return users.stream()
                   .filter(User::isActive)
                   .takeWhile(user -> user.getAge() < 18)  // Прекращаем на первом совершеннолетнем
                   .findFirst();
    }

    // Правильное использование parallel streams
    public void parallelStreamBestPractices() {
        List<Integer> numbers = IntStream.range(1, 1000000).boxed().collect(Collectors.toList());

        // 1. Проверяем, что операция CPU-bound
        int sum = numbers.parallelStream()
                        .filter(n -> n % 2 == 0)
                        .mapToInt(Integer::intValue)
                        .sum();

        // 2. Избегаем side effects
        List<Integer> evenNumbers = numbers.parallelStream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());

        // 3. Используем правильные коллекторы
        ConcurrentMap<Integer, List<Integer>> grouped = numbers.parallelStream()
                                                              .collect(Collectors.groupingByConcurrent(
                                                                  n -> n % 10));

        // 4. Для больших коллекций
        if (numbers.size() > 1000) {
            numbers.parallelStream()
                   .forEach(this::processInParallel);
        } else {
            numbers.forEach(this::processSequentially);
        }
    }

    private List<User> createLargeUserList() {
        return IntStream.range(0, 10000)
                       .mapToObj(i -> new User(i, "User" + i, i % 2 == 0))
                       .collect(Collectors.toList());
    }

    private List<String> createLargeStringList() {
        return IntStream.range(0, 10000)
                       .mapToObj(String::valueOf)
                       .collect(Collectors.toList());
    }

    private long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

    private String cpuIntensiveOperation(String input) {
        // Имитация CPU-bound операции
        return input.chars()
                   .mapToObj(c -> (char) c)
                   .map(Character::toUpperCase)
                   .map(String::valueOf)
                   .collect(Collectors.joining());
    }

    private String ioOperation(String input) {
        // Имитация IO-bound операции
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return input;
    }

    private Optional<User> findUserByEmail(String email) {
        // Имитация поиска в БД
        return Optional.of(new User(1, "Test User", true));
    }

    private void processUser(User user) {
        // Обработка пользователя
    }

    private void processInParallel(Integer number) {
        // Параллельная обработка
    }

    private void processSequentially(Integer number) {
        // Последовательная обработка
    }

    // Вспомогательные классы
    static class User {
        private final int id;
        private final String name;
        private final boolean active;
        private final String email;

        public User(int id, String name, boolean active) {
            this.id = id;
            this.name = name;
            this.active = active;
            this.email = name.toLowerCase() + "@example.com";
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public boolean isActive() { return active; }
        public int getAge() { return 20 + (id % 50); }
        public String getEmail() { return email; }
    }

    static class UserDTO {
        private final int id;
        private final String name;

        public UserDTO(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    static class UserStats {
        // Статистика пользователей
    }
}
```


## Решение проблем

| Проблема | Почему возникает | Что делать |
|---|---|---|
| `IllegalStateException: stream has already been operated upon or closed` | попытка повторно использовать уже терминально обработанный stream | создавать новый stream для каждой цепочки операций |
| `NullPointerException` внутри stream pipeline | в коллекции есть `null`, а операции не учитывают это | использовать `filter(Objects::nonNull)` перед `map`/`flatMap` |
| Медленнее, чем цикл `for` | лишние boxing/unboxing и сложные лямбды для маленьких наборов | для hot path и простых операций выбирать обычный цикл |
| Непредсказуемый результат в `parallelStream()` | side effects и небезопасные общие структуры | убирать side effects, использовать thread-safe collectors и чистые функции |
| Трудно отладить длинный pipeline | цепочка слишком длинная и скрывает шаги трансформации | разбивать pipeline на промежуточные шаги с именованными методами |

## Частые вопросы

**Когда использовать Stream, а когда обычный цикл?**
`Stream` удобен для декларативных преобразований и композиции; цикл обычно лучше в критичном по производительности участке.

**`map()` и `flatMap()` — в чём ключевая разница?**
`map()` возвращает элемент того же уровня, `flatMap()` разворачивает вложенные структуры (например, `List<List<T>>` -> `List<T>`).

**Стоит ли использовать `parallelStream()` по умолчанию?**
Нет. Его включают только после измерений на реальном workload и при отсутствии side effects в pipeline.

**`reduce()` или `collect()`?**
`reduce()` — для свёртки в одно значение, `collect()` — для построения структур (списки, мапы, группировки).
## См. также
- [[java-exceptions|Java Exceptions]] — обработка исключений
- [[java-annotations-reflection|Java Annotations]] — аннотации и рефлексия
- [[java-collections-list|Java Collections]] — коллекции **Java**
