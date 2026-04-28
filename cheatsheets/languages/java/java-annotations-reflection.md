---
title: "Java Annotations и Reflection"
description: "Java предоставляет мощные механизмы метапрограммирования через аннотации и рефлексию. Этот документ охватывает создание и использование аннотаций, работу с рефлексией, байткод манипуляцию и практические применения этих технологий."
tags:
  - languages
  - java
  - java-annotations-reflection
type: "overview"
difficulty: "intermediate"
aliases:
  - "Java Annotations и Reflection"
  - "java annotations reflection"
prerequisites: []
next:
  - "[[java-memory-model]]"
updated: "2026-04-20"
---
# Java Annotations и Reflection

**Java** предоставляет мощные механизмы метапрограммирования через аннотации и рефлексию. Этот документ охватывает создание и использование аннотаций, работу с рефлексией, байткод манипуляцию и практические применения этих технологий.

## Полезные ссылки
- [Java Annotations](https://docs.oracle.com/javase/tutorial/java/annotations/)
- [Java Reflection](https://docs.oracle.com/javase/tutorial/reflect/)
- [ASM Bytecode Manipulation](https://asm.ow2.io/)
- [Project Lombok](https://projectlombok.org/)


### См. также
- [Java Collections: конвертирование](java-collections-converting.md)
- [Java Reactive: Project Reactor](java-reactive-project-reactor.md)
- [Java Reactive: RxJava](java-reactive-rxjava.md)
## Содержание

- [Основы аннотаций](#основы-аннотаций)
  - [Создание аннотаций](#создание-аннотаций)
  - [Использование аннотаций](#использование-аннотаций)
  - [Мета-аннотации](#мета-аннотации)
- [Обработка аннотаций](#обработка-аннотаций)
  - [Runtime обработка](#runtime-обработка)
  - [Compile-time обработка (APT)](#compile-time-обработка-apt)
- [Основы рефлексии](#основы-рефлексии)
  - [Class introspection](#class-introspection)
  - [Динамическое создание объектов](#динамическое-создание-объектов)
  - [Доступ к полям и методам](#доступ-к-полям-и-методам)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Bytecode manipulation с ASM](#bytecode-manipulation-с-asm)
  - [CDI (Contexts and Dependency Injection) через reflection](#cdi-contexts-and-dependency-injection-через-reflection)
- [AOP (Aspect-Oriented Programming) через reflection](#aop-aspect-oriented-programming-через-reflection)
  - [Динамический AOP](#динамический-aop)
- [ORM через reflection](#orm-через-reflection)
  - [Простой ORM](#простой-orm)
- [Performance и best practices](#performance-и-best-practices)
  - [Кэширование reflection данных](#кэширование-reflection-данных)
  - [Безопасность reflection](#безопасность-reflection)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Основы аннотаций

### Создание аннотаций

**Примеры объявления аннотаций (`@Entity`, `@Column`, `@Transactiona`l):**

```java
// Простая аннотация-маркер
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}

// Аннотация с параметрами
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";
    boolean nullable() default true;
    int length() default 255;
}

// Аннотация для методов
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {
    Isolation isolation() default Isolation.DEFAULT;
    Propagation propagation() default Propagation.REQUIRED;
    int timeout() default -1;
}

// Перечисление для аннотаций
public enum Isolation {
    DEFAULT, READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
}

public enum Propagation {
    REQUIRED, SUPPORTS, MANDATORY, REQUIRES_NEW, NOT_SUPPORTED, NEVER, NESTED
}

// Комплексная аннотация
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Audited {
    String action() default "";
    boolean logParameters() default true;
    boolean logResult() default false;
    String[] roles() default {};
}

// Аннотация с массивами
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScheduledTasks {
    ScheduledTask[] value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScheduledTask {
    String cron() default "";
    String zone() default "";
    long fixedDelay() default -1;
    String initialDelay() default "";
}
```

### Использование аннотаций
```java
// Применение аннотаций
@Entity
@Audited(action = "USER_MANAGEMENT", roles = {"ADMIN", "USER_MANAGER"})
public class User {

    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", length = 100)
    private String email;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User save(User user) {
        // Логика сохранения
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(Long userId) {
        // Логика удаления
    }
}

// Класс с запланированными задачами
@ScheduledTasks({
    @ScheduledTask(cron = "0 0/15 * * * *"),  // Каждые 15 минут
    @ScheduledTask(fixedDelay = 300000)       // Каждые 5 минут
})
public class DataProcessor {

    public void processData() {
        // Обработка данных
    }
}
```

### Мета-аннотации
```java
// Кастомная мета-аннотация
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Audited(action = "DEFAULT_AUDIT")
@Transactional
public @interface ServiceOperation {
    String value() default "";
    boolean auditEnabled() default true;
}

// Составная аннотация
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceOperation("USER_SERVICE")
public @interface UserService {
}

// Использование составной аннотации
@UserService
public class UserManagementService {

    public void createUser(User user) {
        // Метод автоматически получает
        // @Audited и @Transactional через @ServiceOperation
    }
}
```

## Обработка аннотаций

### Runtime обработка
```java
public class AnnotationProcessor {

    public void processAnnotatedClasses(String packageName) throws Exception {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Entity.class);

        for (Class<?> clazz : annotatedClasses) {
            processEntityClass(clazz);
        }
    }

    private void processEntityClass(Class<?> clazz) {
        Entity entityAnnotation = clazz.getAnnotation(Entity.class);
        if (entityAnnotation != null) {
            System.out.println("Processing entity: " + clazz.getSimpleName());

            // Обработка полей с @Column
            for (Field field : clazz.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    processColumnField(field, column);
                }
            }

            // Обработка методов с @Transactional
            for (Method method : clazz.getDeclaredMethods()) {
                Transactional transactional = method.getAnnotation(Transactional.class);
                if (transactional != null) {
                    processTransactionalMethod(method, transactional);
                }
            }
        }
    }

    private void processColumnField(Field field, Column column) {
        String columnName = column.name().isEmpty() ?
            field.getName() : column.name();

        System.out.printf("Field: %s -> Column: %s (nullable: %s, length: %d)%n",
            field.getName(), columnName, column.nullable(), column.length());
    }

    private void processTransactionalMethod(Method method, Transactional transactional) {
        System.out.printf("Method: %s -> Transactional (isolation: %s, propagation: %s)%n",
            method.getName(), transactional.isolation(), transactional.propagation());
    }
}
```

### Compile-time обработка (APT)
```java
// Annotation Processor
@SupportedAnnotationTypes("com.example.annotations.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : annotatedElements) {
                if (element.getKind() == ElementKind.CLASS) {
                    generateBuilderClass((TypeElement) element);
                }
            }
        }
        return true;
    }

    private void generateBuilderClass(TypeElement classElement) {
        String className = classElement.getSimpleName().toString();
        String builderName = className + "Builder";
        String packageName = getPackageName(classElement);

        try (PrintWriter writer = new PrintWriter(
             processingEnv.getFiler().createSourceFile(packageName + "." + builderName).openWriter())) {

            writer.println("package " + packageName + ";");
            writer.println();
            writer.println("public class " + builderName + " {");

            // Генерация полей
            for (Element element : classElement.getEnclosedElements()) {
                if (element.getKind() == ElementKind.FIELD) {
                    String fieldName = element.getSimpleName().toString();
                    String fieldType = element.asType().toString();
                    writer.println("    private " + fieldType + " " + fieldName + ";");
                }
            }

            writer.println();

            // Генерация сеттеров
            for (Element element : classElement.getEnclosedElements()) {
                if (element.getKind() == ElementKind.FIELD) {
                    String fieldName = element.getSimpleName().toString();
                    String fieldType = element.asType().toString();
                    String methodName = "set" + capitalize(fieldName);

                    writer.println("    public " + builderName + " " + methodName + "(" + fieldType + " " + fieldName + ") {");
                    writer.println("        this." + fieldName + " = " + fieldName + ";");
                    writer.println("        return this;");
                    writer.println("    }");
                }
            }

            writer.println();

            // Генерация build метода
            writer.println("    public " + className + " build() {");
            writer.println("        " + className + " instance = new " + className + "();");

            for (Element element : classElement.getEnclosedElements()) {
                if (element.getKind() == ElementKind.FIELD) {
                    String fieldName = element.getSimpleName().toString();
                    writer.println("        instance.set" + capitalize(fieldName) + "(this." + fieldName + ");");
                }
            }

            writer.println("        return instance;");
            writer.println("    }");
            writer.println("}");

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate builder: " + e.getMessage());
        }
    }

    private String getPackageName(TypeElement classElement) {
        return processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
```

## Основы рефлексии

### Class introspection
```java
public class ReflectionBasics {

    public void inspectClass(Class<?> clazz) {
        System.out.println("Class: " + clazz.getName());
        System.out.println("Simple Name: " + clazz.getSimpleName());
        System.out.println("Package: " + clazz.getPackage().getName());
        System.out.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));
        System.out.println("Superclass: " + clazz.getSuperclass().getName());

        // Интерфейсы
        System.out.println("Interfaces:");
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            System.out.println("  " + interfaceClass.getName());
        }

        // Аннотации
        System.out.println("Annotations:");
        for (Annotation annotation : clazz.getAnnotations()) {
            System.out.println("  " + annotation);
        }
    }

    public void inspectFields(Class<?> clazz) {
        System.out.println("Fields:");

        // Все поля (включая приватные и унаследованные)
        Field[] allFields = clazz.getDeclaredFields();
        for (Field field : allFields) {
            System.out.printf("  %s %s %s%n",
                Modifier.toString(field.getModifiers()),
                field.getType().getSimpleName(),
                field.getName());
        }

        // Публичные поля
        Field[] publicFields = clazz.getFields();
        System.out.println("Public fields: " + publicFields.length);
    }

    public void inspectMethods(Class<?> clazz) {
        System.out.println("Methods:");

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.printf("  %s %s %s(%s)%n",
                Modifier.toString(method.getModifiers()),
                method.getReturnType().getSimpleName(),
                method.getName(),
                Arrays.stream(method.getParameterTypes())
                      .map(Class::getSimpleName)
                      .collect(Collectors.joining(", ")));
        }
    }

    public void inspectConstructors(Class<?> clazz) {
        System.out.println("Constructors:");

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.printf("  %s %s(%s)%n",
                Modifier.toString(constructor.getModifiers()),
                clazz.getSimpleName(),
                Arrays.stream(constructor.getParameterTypes())
                      .map(Class::getSimpleName)
                      .collect(Collectors.joining(", ")));
        }
    }
}
```

### Динамическое создание объектов
```java
public class DynamicInstantiation {

    // Создание объекта через Class.newInstance() (deprecated)
    @Deprecated
    public <T> T createInstanceDeprecated(Class<T> clazz) throws Exception {
        return clazz.newInstance();
    }

    // Создание объекта через Constructor
    public <T> T createInstance(Class<T> clazz, Object... args) throws Exception {
        Class<?>[] paramTypes = Arrays.stream(args)
                                     .map(Object::getClass)
                                     .toArray(Class<?>[]::new);

        Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true); // Для приватных конструкторов

        return constructor.newInstance(args);
    }

    // Создание объекта через reflection с dependency injection
    public <T> T createInstanceWithDI(Class<T> clazz, Map<Class<?>, Object> dependencies) throws Exception {
        // Находим конструктор с наибольшим количеством параметров
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> selectedConstructor = Arrays.stream(constructors)
            .max(Comparator.comparingInt(Constructor::getParameterCount))
            .orElseThrow(() -> new NoSuchMethodException("No suitable constructor found"));

        Class<?>[] paramTypes = selectedConstructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        // Разрешаем зависимости
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Object dependency = dependencies.get(paramType);

            if (dependency == null) {
                throw new IllegalArgumentException("No dependency found for type: " + paramType.getName());
            }

            params[i] = dependency;
        }

        selectedConstructor.setAccessible(true);
        @SuppressWarnings("unchecked")
        T instance = (T) selectedConstructor.newInstance(params);

        // Внедряем поля через setter'ы
        injectFields(instance, dependencies);

        return instance;
    }

    private void injectFields(Object instance, Map<Class<?>, Object> dependencies) throws Exception {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (dependencies.containsKey(field.getType())) {
                field.setAccessible(true);
                field.set(instance, dependencies.get(field.getType()));
            }
        }
    }

    // Фабрика объектов
    public static class ObjectFactory {
        private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        public <T> T getInstance(Class<T> clazz) {
            return (T) singletons.computeIfAbsent(clazz, this::createInstance);
        }

        private <T> T createInstance(Class<T> clazz) {
            try {
                // Попытка найти конструктор без параметров
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();

            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
            }
        }
    }
}
```

### Доступ к полям и методам
```java
public class FieldAndMethodAccess {

    public void accessFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        // Доступ к полю
        Field field = clazz.getDeclaredField("name");
        field.setAccessible(true); // Для приватных полей

        // Чтение значения
        String name = (String) field.get(instance);
        System.out.println("Field value: " + name);

        // Запись значения
        field.set(instance, "New Name");

        // Статические поля
        Field staticField = clazz.getDeclaredField("COUNTER");
        staticField.setAccessible(true);
        int counter = (int) staticField.get(null); // null для статических полей
        staticField.set(null, counter + 1);
    }

    public void invokeMethods(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        // Вызов метода без параметров
        Method toStringMethod = clazz.getMethod("toString");
        String result = (String) toStringMethod.invoke(instance);

        // Вызов метода с параметрами
        Method setNameMethod = clazz.getDeclaredMethod("setName", String.class);
        setNameMethod.invoke(instance, "Updated Name");

        // Вызов приватного метода
        Method privateMethod = clazz.getDeclaredMethod("validate");
        privateMethod.setAccessible(true);
        boolean valid = (boolean) privateMethod.invoke(instance);

        // Вызов статического метода
        Method staticMethod = clazz.getDeclaredMethod("getInstanceCount");
        int count = (int) staticMethod.invoke(null);

        // Вызов метода с переменным количеством параметров
        Method varArgsMethod = clazz.getDeclaredMethod("processItems", String[].class);
        String[] items = {"item1", "item2", "item3"};
        varArgsMethod.invoke(instance, (Object) items);
    }

    // Динамический прокси
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            handler
        );
    }

    public void demonstrateProxy() {
        UserService realService = new UserServiceImpl();

        UserService proxy = createProxy(UserService.class, (proxyObj, method, args) -> {
            System.out.println("Before method: " + method.getName());

            // Логика перехвата
            if (method.getName().equals("saveUser")) {
                User user = (User) args[0];
                validateUser(user); // Дополнительная валидация
            }

            Object result = method.invoke(realService, args);

            System.out.println("After method: " + method.getName());

            return result;
        });

        // Использование прокси
        User user = new User("john", "john@example.com");
        proxy.saveUser(user);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
    }

    // Интерфейсы и классы для демонстрации
    interface UserService {
        void saveUser(User user);
        User findUser(String id);
    }

    static class UserServiceImpl implements UserService {
        @Override
        public void saveUser(User user) {
            System.out.println("Saving user: " + user.getName());
        }

        @Override
        public User findUser(String id) {
            return new User("john", "john@example.com");
        }
    }

    static class User {
        private String name;
        private String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
```

## Продвинутые возможности

### Bytecode manipulation с ASM
```java
public class BytecodeTransformer {

    public byte[] transformClass(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                           String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                // Добавляем логирование ко всем методам
                if (!name.equals("<init>") && !name.equals("<clinit>")) {
                    return new LoggingMethodVisitor(mv, name);
                }

                return mv;
            }
        };

        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    private static class LoggingMethodVisitor extends MethodVisitor {
        private final String methodName;

        public LoggingMethodVisitor(MethodVisitor mv, String methodName) {
            super(Opcodes.ASM9, mv);
            this.methodName = methodName;
        }

        @Override
        public void visitCode() {
            // Добавляем вызов логирования в начале метода
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Entering method: " + methodName);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                             "(Ljava/lang/String;)V", false);

            super.visitCode();
        }
    }
}

// Java Agent для трансформации классов
public class TransformationAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                  Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                                  byte[] classfileBuffer) throws IllegalClassFormatException {

                // Трансформируем только определенные классы
                if (className != null && className.startsWith("com/example/service")) {
                    BytecodeTransformer transformer = new BytecodeTransformer();
                    return transformer.transformClass(classfileBuffer);
                }

                return classfileBuffer;
            }
        });
    }
}
```

### CDI (Contexts and `Dependency` Injection) через reflection
```java
public class CDIContainer {

    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
    private final Map<Class<?>, Class<?>> bindings = new ConcurrentHashMap<>();

    // Регистрация привязок
    public <T> void bind(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        bindings.put(interfaceClass, implementationClass);
    }

    // Получение экземпляра
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, this::createInstance);
    }

    private Object createInstance(Class<?> clazz) {
        try {
            // Определяем, какой класс использовать
            Class<?> implementationClass = bindings.getOrDefault(clazz, clazz);

            // Находим подходящий конструктор
            Constructor<?>[] constructors = implementationClass.getDeclaredConstructors();
            Constructor<?> constructor = findInjectableConstructor(constructors);

            // Получаем параметры конструктора
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getInstance(paramTypes[i]);
            }

            constructor.setAccessible(true);
            Object instance = constructor.newInstance(params);

            // Внедряем поля
            injectFields(instance);

            // Вызываем post-construct методы
            callPostConstruct(instance);

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    private Constructor<?> findInjectableConstructor(Constructor<?>[] constructors) {
        // Ищем конструктор с @Inject
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }

        // Используем конструктор без параметров
        try {
            return Arrays.stream(constructors)
                        .filter(c -> c.getParameterCount() == 0)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchMethodException("No suitable constructor"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No injectable constructor found", e);
        }
    }

    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object dependency = getInstance(field.getType());
                field.set(instance, dependency);
            }
        }
    }

    private void callPostConstruct(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.setAccessible(true);
                method.invoke(instance);
            }
        }
    }

    // Аннотации для CDI
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
    public @interface Inject {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PostConstruct {
    }
}
```

## AOP (Aspect-`Oriented` Programming) через reflection

### Динамический AOP
```java
public class DynamicAOP {

    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new AOPInvocationHandler(target)
        );
    }

    private static class AOPInvocationHandler implements InvocationHandler {

        private final Object target;
        private final Map<Method, List<Advice>> advices = new ConcurrentHashMap<>();

        public AOPInvocationHandler(Object target) {
            this.target = target;
            initializeAdvices();
        }

        private void initializeAdvices() {
            // Сканируем аспекты и регистрируем advice'ы
            Reflections reflections = new Reflections("com.example.aspects");

            for (Class<?> aspectClass : reflections.getTypesAnnotatedWith(Aspect.class)) {
                try {
                    Object aspectInstance = aspectClass.getDeclaredConstructor().newInstance();

                    for (Method method : aspectClass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Before.class)) {
                            registerAdvice(method, aspectInstance, AdviceType.BEFORE);
                        } else if (method.isAnnotationPresent(After.class)) {
                            registerAdvice(method, aspectInstance, AdviceType.AFTER);
                        } else if (method.isAnnotationPresent(Around.class)) {
                            registerAdvice(method, aspectInstance, AdviceType.AROUND);
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize aspect: " + aspectClass.getName(), e);
                }
            }
        }

        private void registerAdvice(Method adviceMethod, Object aspectInstance, AdviceType type) {
            Pointcut pointcut = adviceMethod.getAnnotation(Pointcut.class);
            if (pointcut != null) {
                String expression = pointcut.value();
                // Здесь должна быть логика парсинга pointcut выражения
                // Для простоты регистрируем для всех методов
                for (Method targetMethod : target.getClass().getDeclaredMethods()) {
                    advices.computeIfAbsent(targetMethod, k -> new ArrayList<>())
                           .add(new Advice(adviceMethod, aspectInstance, type));
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            List<Advice> methodAdvices = advices.get(method);

            // Before advice
            if (methodAdvices != null) {
                for (Advice advice : methodAdvices) {
                    if (advice.type == AdviceType.BEFORE) {
                        advice.method.invoke(advice.aspectInstance, args);
                    }
                }
            }

            Object result = null;
            Throwable exception = null;

            try {
                // Around advice (before)
                if (methodAdvices != null) {
                    for (Advice advice : methodAdvices) {
                        if (advice.type == AdviceType.AROUND) {
                            // Вызов around.before()
                        }
                    }
                }

                // Выполнение целевого метода
                result = method.invoke(target, args);

                // Around advice (after returning)
                if (methodAdvices != null) {
                    for (Advice advice : methodAdvices) {
                        if (advice.type == AdviceType.AROUND) {
                            // Вызов around.afterReturning(result)
                        }
                    }
                }

            } catch (Throwable t) {
                exception = t;

                // Around advice (after throwing)
                if (methodAdvices != null) {
                    for (Advice advice : methodAdvices) {
                        if (advice.type == AdviceType.AROUND) {
                            // Вызов around.afterThrowing(t)
                        }
                    }
                }

            } finally {
                // After advice
                if (methodAdvices != null) {
                    for (Advice advice : methodAdvices) {
                        if (advice.type == AdviceType.AFTER) {
                            try {
                                advice.method.invoke(advice.aspectInstance, args);
                            } catch (Exception e) {
                                // Логируем ошибки в after advice
                                System.err.println("After advice failed: " + e.getMessage());
                            }
                        }
                    }
                }
            }

            if (exception != null) {
                throw exception;
            }

            return result;
        }
    }

    // Вспомогательные классы
    private enum AdviceType { BEFORE, AFTER, AROUND }

    private static class Advice {
        final Method method;
        final Object aspectInstance;
        final AdviceType type;

        Advice(Method method, Object aspectInstance, AdviceType type) {
            this.method = method;
            this.aspectInstance = aspectInstance;
            this.type = type;
        }
    }

    // Аннотации для AOP
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Aspect {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Before {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface After {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Around {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Pointcut {
        String value();
    }
}
```

## ORM через reflection

### Простой ORM
```java
public class SimpleORM {

    private final DataSource dataSource;

    public SimpleORM(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T findById(Class<T> entityClass, Object id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = createFindByIdStatement(conn, entityClass, id);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToEntity(rs, entityClass);
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find entity by id", e);
        }
    }

    public <T> void save(T entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = createSaveStatement(conn, entity)) {

            stmt.executeUpdate();

            // Устанавливаем сгенерированный ID
            setGeneratedId(entity, stmt);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    private PreparedStatement createFindByIdStatement(Connection conn, Class<?> entityClass, Object id)
            throws Exception {

        EntityMetadata metadata = getEntityMetadata(entityClass);
        String sql = "SELECT * FROM " + metadata.tableName + " WHERE " + metadata.idColumn + " = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setObject(1, id);

        return stmt;
    }

    private <T> T mapResultSetToEntity(ResultSet rs, Class<T> entityClass) throws Exception {
        T instance = entityClass.getDeclaredConstructor().newInstance();

        for (Field field : entityClass.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                Object value = rs.getObject(columnName);

                field.setAccessible(true);
                field.set(instance, value);
            }
        }

        return instance;
    }

    private PreparedStatement createSaveStatement(Connection conn, Object entity) throws Exception {
        Class<?> entityClass = entity.getClass();
        EntityMetadata metadata = getEntityMetadata(entityClass);

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && !isIdField(field)) {
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                columns.add(columnName);

                field.setAccessible(true);
                values.add(field.get(entity));
            }
        }

        String sql = "INSERT INTO " + metadata.tableName + " (" +
                    String.join(",", columns) + ") VALUES (" +
                    String.join(",", Collections.nCopies(columns.size(), "?")) + ")";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        for (int i = 0; i < values.size(); i++) {
            stmt.setObject(i + 1, values.get(i));
        }

        return stmt;
    }

    private void setGeneratedId(Object entity, PreparedStatement stmt) throws Exception {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                Class<?> entityClass = entity.getClass();

                for (Field field : entityClass.getDeclaredFields()) {
                    Id idAnnotation = field.getAnnotation(Id.class);
                    if (idAnnotation != null) {
                        field.setAccessible(true);
                        field.set(entity, generatedKeys.getObject(1));
                        break;
                    }
                }
            }
        }
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private EntityMetadata getEntityMetadata(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        String tableName = table != null ? table.name() : entityClass.getSimpleName().toLowerCase();

        String idColumn = null;
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                Column column = field.getAnnotation(Column.class);
                idColumn = column != null && !column.name().isEmpty() ?
                          column.name() : field.getName();
                break;
            }
        }

        return new EntityMetadata(tableName, idColumn);
    }

    // Аннотации для ORM
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Id {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String name() default "";
        boolean nullable() default true;
    }

    private static class EntityMetadata {
        final String tableName;
        final String idColumn;

        EntityMetadata(String tableName, String idColumn) {
            this.tableName = tableName;
            this.idColumn = idColumn;
        }
    }
}
```

## Performance и best practices

### Кэширование reflection данных
```java
public class ReflectionCache {

    private static final Map<Class<?>, ClassMetadata> classCache = new ConcurrentHashMap<>();

    public static ClassMetadata getMetadata(Class<?> clazz) {
        return classCache.computeIfAbsent(clazz, ReflectionCache::analyzeClass);
    }

    private static ClassMetadata analyzeClass(Class<?> clazz) {
        long startTime = System.nanoTime();

        List<FieldMetadata> fields = new ArrayList<>();
        List<MethodMetadata> methods = new ArrayList<>();
        List<ConstructorMetadata> constructors = new ArrayList<>();

        // Анализируем поля
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(new FieldMetadata(
                field.getName(),
                field.getType(),
                field.getModifiers(),
                field.getAnnotations()
            ));
        }

        // Анализируем методы
        for (Method method : clazz.getDeclaredMethods()) {
            methods.add(new MethodMetadata(
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes(),
                method.getModifiers(),
                method.getAnnotations()
            ));
        }

        // Анализируем конструкторы
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            constructors.add(new ConstructorMetadata(
                constructor.getParameterTypes(),
                constructor.getModifiers(),
                constructor.getAnnotations()
            ));
        }

        long analysisTime = System.nanoTime() - startTime;
        System.out.printf("Analyzed class %s in %.3f ms%n",
                         clazz.getName(), analysisTime / 1_000_000.0);

        return new ClassMetadata(fields, methods, constructors);
    }

    // Метаданные классов
    public static class ClassMetadata {
        public final List<FieldMetadata> fields;
        public final List<MethodMetadata> methods;
        public final List<ConstructorMetadata> constructors;

        public ClassMetadata(List<FieldMetadata> fields, List<MethodMetadata> methods,
                           List<ConstructorMetadata> constructors) {
            this.fields = Collections.unmodifiableList(fields);
            this.methods = Collections.unmodifiableList(methods);
            this.constructors = Collections.unmodifiableList(constructors);
        }
    }

    public static class FieldMetadata {
        public final String name;
        public final Class<?> type;
        public final int modifiers;
        public final Annotation[] annotations;

        public FieldMetadata(String name, Class<?> type, int modifiers, Annotation[] annotations) {
            this.name = name;
            this.type = type;
            this.modifiers = modifiers;
            this.annotations = annotations;
        }
    }

    public static class MethodMetadata {
        public final String name;
        public final Class<?> returnType;
        public final Class<?>[] parameterTypes;
        public final int modifiers;
        public final Annotation[] annotations;

        public MethodMetadata(String name, Class<?> returnType, Class<?>[] parameterTypes,
                            int modifiers, Annotation[] annotations) {
            this.name = name;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
            this.modifiers = modifiers;
            this.annotations = annotations;
        }
    }

    public static class ConstructorMetadata {
        public final Class<?>[] parameterTypes;
        public final int modifiers;
        public final Annotation[] annotations;

        public ConstructorMetadata(Class<?>[] parameterTypes, int modifiers, Annotation[] annotations) {
            this.parameterTypes = parameterTypes;
            this.modifiers = modifiers;
            this.annotations = annotations;
        }
    }
}
```

### Безопасность reflection
```java
public class SecureReflection {

    private static final Set<String> ALLOWED_PACKAGES = Set.of(
        "com.example.",
        "java.lang.",
        "java.util."
    );

    public static void validateClass(Class<?> clazz) {
        String className = clazz.getName();

        // Проверяем, что класс из разрешенного пакета
        boolean allowed = ALLOWED_PACKAGES.stream()
                                         .anyMatch(className::startsWith);

        if (!allowed) {
            throw new SecurityException("Access to class " + className + " is not allowed");
        }

        // Проверяем, что класс не является системным
        if (className.startsWith("java.lang.reflect.") ||
            className.startsWith("sun.reflect.")) {
            throw new SecurityException("Access to reflection classes is not allowed");
        }
    }

    public static void validateMethod(Method method) {
        // Проверяем модификаторы
        if (Modifier.isPrivate(method.getModifiers()) &&
            !isReflectionAllowed()) {
            throw new SecurityException("Access to private method is not allowed");
        }

        // Проверяем аннотацию @Restricted
        if (method.isAnnotationPresent(Restricted.class)) {
            throw new SecurityException("Access to restricted method is not allowed");
        }
    }

    public static void validateField(Field field) {
        // Аналогичные проверки для полей
        if (Modifier.isPrivate(field.getModifiers()) &&
            !isReflectionAllowed()) {
            throw new SecurityException("Access to private field is not allowed");
        }

        if (field.isAnnotationPresent(Restricted.class)) {
            throw new SecurityException("Access to restricted field is not allowed");
        }
    }

    private static boolean isReflectionAllowed() {
        // Проверяем stack trace для определения контекста
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("com.example.framework")) {
                return true; // Разрешаем доступ из фреймворка
            }
        }

        return false;
    }

    // Аннотация для ограничения доступа
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface Restricted {
        String reason() default "Access restricted";
    }

    // Безопасный invoker
    public static class SecureInvoker {

        public Object invoke(Method method, Object instance, Object... args) throws Exception {
            validateMethod(method);

            if (instance != null) {
                validateClass(instance.getClass());
            }

            return method.invoke(instance, args);
        }

        public Object get(Field field, Object instance) throws Exception {
            validateField(field);

            if (instance != null) {
                validateClass(instance.getClass());
            }

            return field.get(instance);
        }

        public void set(Field field, Object instance, Object value) throws Exception {
            validateField(field);

            if (instance != null) {
                validateClass(instance.getClass());
            }

            field.set(instance, value);
        }
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [Java Exceptions](java-exceptions.md) — обработка исключений
- [Spring AOP](../../frameworks/java-frameworks/spring/spring-aop.md) — аспектно-ориентированное программирование
