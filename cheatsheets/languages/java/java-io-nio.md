---
title: "Java IO/NIO: работа с файлами и потоками"
description: "Комплексное руководство по работе с вводом-выводом в Java: традиционный IO, NIO, NIO.2, асинхронный IO и оптимизация производительности."
tags:
  - languages
  - java
  - java-io-nio
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Java IO/NIO: работа с файлами и потоками

**Комплексное руководство по работе с вводом-выводом в `Java`: традиционный `IO`, `NIO`, `NIO`.2, асинхронный `IO` и оптимизация производительности.**

## Полезные ссылки

### Официальная документация
- [Java IO Package](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/package-summary.html) — пакет **java.io**
- [Java NIO Package](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/package-summary.html) — пакет **java.nio**
- [Java NIO.2](https://docs.oracle.com/javase/tutorial/essential/io/fileio.html) — современный `IO` **API**

### Дополнительные ресурсы
- [Baeldung IO](https://www.baeldung.com/java-io) — статьи по **Java** `IO`
- [Java NIO Tutorial](https://docs.oracle.com/javase/tutorial/essential/io/) — руководство по **NIO.2**
- [Performance Tuning IO](https://www.baeldung.com/java-io-performance) — оптимизация производительности

## Содержание

- [Введение в Java IO](#введение-в-java-io)
  - [Почему важно понимать IO?](#почему-важно-понимать-io)
  - [Основные концепции](#основные-концепции)
- [Традиционный IO (java.io)](#традиционный-io-javaio)
  - [Классы потоков](#классы-потоков)
  - [Чтение файлов](#чтение-файлов)
  - [Запись файлов](#запись-файлов)
  - [Работа с директориями](#работа-с-директориями)
  - [Сериализация объектов](#сериализация-объектов)
- [NIO (New IO)](#nio-new-io)
  - [Буферы (Buffers)](#буферы-buffers)
  - [Каналы (Channels)](#каналы-channels)
  - [Селекторы (Selectors)](#селекторы-selectors)
- [NIO.2 (Path API)](#nio2-path-api)
  - [Path и Paths](#path-и-paths)
  - [Files API](#files-api)
- [Асинхронный IO](#асинхронный-io)
  - [Future-based асинхронный IO](#future-based-асинхронный-io)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Выбор правильного подхода](#выбор-правильного-подхода)
  - [Настройка буферов](#настройка-буферов)
  - [Профилирование IO операций](#профилирование-io-операций)
- [Обработка ошибок](#обработка-ошибок)
  - [Правильная обработка IO исключений](#правильная-обработка-io-исключений)
- [Best practices](#best-practices)
  - [Управление ресурсами](#управление-ресурсами)
  - [Производительность и безопасность](#производительность-и-безопасность)
  - [Выбор правильного IO подхода](#выбор-правильного-io-подхода)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Основные принципы выбора IO подхода](#основные-принципы-выбора-io-подхода)
  - [Производительность](#производительность)
  - [Безопасность](#безопасность)
  - [Best practices](#best-practices-1)
- [См. также](#см-также)

## Введение в Java `IO`

**Java IO / NIO:**

1. **Традиционный `IO` (java.io)** — блокирующий, потоковый **API**
2. **NIO (New IO)** — неблокирующий, канальный **API**
3. **NIO.2** — улучшенный **API** для работы с файловой системой

### Почему важно понимать `IO`?

**IO операции часто являются узким местом в производительности приложений:**

- **Блокирующие операции** могут заморозить весь поток
- **Неправильное использование буферов** приводит к лишним копиям данных
- **Отсутствие асинхронности** ограничивает масштабируемость
- **Неверная обработка ресурсов** вызывает утечки памяти

### Основные концепции

```java
// Поток (Stream) - последовательность байтов или символов
InputStream input = new FileInputStream("file.txt");
OutputStream output = new FileOutputStream("output.txt");

// Канал (Channel) - более эффективный способ передачи данных
FileChannel channel = FileChannel.open(Paths.get("file.txt"));

// Буфер (Buffer) - временное хранилище данных
ByteBuffer buffer = ByteBuffer.allocate(1024);

// Путь (Path) - представление файлового пути
Path path = Paths.get("folder", "file.txt");
```

## Традиционный `IO` (java.io)

**Традиционный IO** использует потоковую модель, где данные читаются/пишутся последовательно. Все операции блокирующие.

### Классы потоков

```java
// Байтовые потоки
InputStream inputStream = new FileInputStream("data.bin");
OutputStream outputStream = new FileOutputStream("output.bin");

// Символьные потоки
Reader reader = new FileReader("text.txt");
Writer writer = new FileWriter("output.txt");

// Буферизованные потоки (для производительности)
BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
BufferedReader bufferedReader = new BufferedReader(reader);
```

### Чтение файлов

```java
public class FileReaderExample {

    // Простое чтение всего файла
    public String readFileSimple(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return new String(data);
        }
    }

    // Чтение по байтам
    public void readFileByBytes(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int byteData;
            while ((byteData = fis.read()) != -1) {
                // Обработка каждого байта
                System.out.print((char) byteData);
            }
        }
    }

    // Чтение с буфером
    public void readFileBuffered(String filePath) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(filePath))) {

            byte[] buffer = new byte[8192]; // 8KB буфер
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                // Обработка buffer.length байтов
                String chunk = new String(buffer, 0, bytesRead);
                System.out.print(chunk);
            }
        }
    }

    // Чтение текстового файла построчно
    public List<String> readFileLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}
```

### Запись файлов

```java
public class FileWriterExample {

    // Простая запись в файл
    public void writeFileSimple(String filePath, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(content.getBytes());
        }
    }

    // Запись с буфером
    public void writeFileBuffered(String filePath, String content) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(filePath))) {

            bos.write(content.getBytes());
            bos.flush(); // Принудительный сброс буфера
        }
    }

    // Запись большого файла частями
    public void writeLargeFile(String filePath, List<String> chunks) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath))) {

            for (String chunk : chunks) {
                writer.write(chunk);
                writer.newLine();
            }
        }
    }

    // Append режим
    public void appendToFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(content);
        }
    }
}
```

### Работа с директориями

```java
public class DirectoryOperations {

    // Создание директории
    public void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdir(); // Одна директория
            // или directory.mkdirs(); // Весь путь
        }
    }

    // Список файлов в директории
    public List<String> listFiles(String dirPath) {
        List<String> files = new ArrayList<>();
        File directory = new File(dirPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    files.add(file.getName());
                }
            }
        }

        return files;
    }

    // Рекурсивный обход директории
    public void walkDirectory(File directory, int depth) {
        if (depth > 10) return; // Предотвращение бесконечной рекурсии

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String indent = "  ".repeat(depth);
                System.out.println(indent + file.getName());

                if (file.isDirectory()) {
                    walkDirectory(file, depth + 1);
                }
            }
        }
    }

    // Получение информации о файле
    public void getFileInfo(String filePath) {
        File file = new File(filePath);

        System.out.println("Name: " + file.getName());
        System.out.println("Path: " + file.getAbsolutePath());
        System.out.println("Size: " + file.length() + " bytes");
        System.out.println("Is file: " + file.isFile());
        System.out.println("Is directory: " + file.isDirectory());
        System.out.println("Last modified: " + new Date(file.lastModified()));
        System.out.println("Readable: " + file.canRead());
        System.out.println("Writable: " + file.canWrite());
    }
}
```

### Сериализация объектов

```java
public class SerializationExample {

    // Класс должен реализовывать Serializable
    static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;
        private transient String password; // Не сериализуется

        // Конструкторы, геттеры, сеттеры
    }

    // Сериализация объекта в файл
    public void serializeObject(String filePath, Person person) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {

            oos.writeObject(person);
        }
    }

    // Десериализация объекта из файла
    public Person deserializeObject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {

            return (Person) ois.readObject();
        }
    }

    // Сериализация списка объектов
    public void serializeList(String filePath, List<Person> people) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {

            oos.writeObject(people);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Person> deserializeList(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {

            return (List<Person>) ois.readObject();
        }
    }
}
```

## NIO (New IO)

**NIO (New Input/Output)** предоставляет неблокирующий, буфер-ориентированный **API** для работы с `IO`. Основные преимущества:**

- **Неблокирующий режим** — потоки не блокируются на `IO` операциях
- **Буферы** — эффективная работа с памятью
- **Каналы** — двунаправленная коммуникация
- **Селекторы** — мультиплексирование соединений

### Буферы (Buffers)

```java
public class BufferExample {

    public void demonstrateBuffers() {
        // Создание буфера
        ByteBuffer buffer = ByteBuffer.allocate(1024); // Прямой буфер
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024); // Прямой буфер в native памяти

        // Запись данных в буфер
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);

        // Подготовка к чтению
        buffer.flip();

        // Чтение данных из буфера
        while (buffer.hasRemaining()) {
            byte data = buffer.get();
            System.out.println(data);
        }

        // Очистка буфера
        buffer.clear();

        // Компактный буфер
        ByteBuffer compactBuffer = ByteBuffer.allocate(10);
        compactBuffer.put("Hello".getBytes());
        compactBuffer.flip();

        // Читаем 3 байта
        for (int i = 0; i < 3; i++) {
            System.out.print((char) compactBuffer.get());
        }

        // Компактируем оставшиеся данные
        compactBuffer.compact();
    }

    public void bufferTypes() {
        // Различные типы буферов
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        CharBuffer charBuffer = CharBuffer.allocate(512);
        IntBuffer intBuffer = IntBuffer.allocate(256);
        LongBuffer longBuffer = LongBuffer.allocate(128);
        FloatBuffer floatBuffer = FloatBuffer.allocate(256);
        DoubleBuffer doubleBuffer = DoubleBuffer.allocate(128);

        // Работа с CharBuffer
        charBuffer.put("Hello, World!");
        charBuffer.flip();

        while (charBuffer.hasRemaining()) {
            System.out.print(charBuffer.get());
        }
        charBuffer.clear();
    }
}
```

### Каналы (Channels)

```java
public class ChannelExample {

    // Чтение файла через канал
    public void readFileWithChannel(String filePath) throws IOException {
        try (FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead;

            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip();

                // Обработка данных
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }

                buffer.clear();
            }
        }
    }

    // Запись файла через канал
    public void writeFileWithChannel(String filePath, String content) throws IOException {
        try (FileChannel channel = FileChannel.open(Paths.get(filePath),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
            channel.write(buffer);
        }
    }

    // Копирование файла через каналы
    public void copyFileWithChannels(String sourcePath, String destPath) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(Paths.get(sourcePath), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Paths.get(destPath),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            // Эффективное копирование
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }

    // Работа с несколькими буферами
    public void scatterGather(String filePath) throws IOException {
        try (FileChannel channel = FileChannel.open(Paths.get(filePath),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            // Scatter - чтение в несколько буферов
            ByteBuffer header = ByteBuffer.allocate(128);
            ByteBuffer body = ByteBuffer.allocate(1024);

            ByteBuffer[] buffers = {header, body};
            channel.read(buffers);

            // Обработка header
            header.flip();
            System.out.println("Header: " + new String(header.array(), 0, header.limit()));

            // Обработка body
            body.flip();
            System.out.println("Body size: " + body.limit());

            // Gather - запись из нескольких буферов
            header.rewind();
            body.rewind();
            channel.write(buffers);
        }
    }
}
```

### Селекторы (Selectors)

```java
public class SelectorExample {

    // Простой неблокирующий сервер
    public void nonBlockingServer() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(8080));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();

            if (readyChannels == 0) continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey selectedKey = keyIterator.next();

                if (selectedKey.isAcceptable()) {
                    // Принимаем соединение
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);

                } else if (selectedKey.isReadable()) {
                    // Читаем данные
                    SocketChannel clientChannel = (SocketChannel) selectedKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);

                    int bytesRead = clientChannel.read(buffer);
                    if (bytesRead == -1) {
                        clientChannel.close();
                    } else {
                        buffer.flip();
                        // Обработка данных
                        String message = new String(buffer.array(), 0, buffer.limit());
                        System.out.println("Received: " + message);
                    }
                }

                keyIterator.remove();
            }
        }
    }
}
```

## NIO.2 (Path API)

**NIO.2** предоставляет современный **API** для работы с файловой системой, введенный в **Java** 7.

### Path и Paths

```java
public class PathExample {

    public void demonstratePaths() {
        // Создание путей
        Path path1 = Paths.get("folder", "file.txt");
        Path path2 = Paths.get("/absolute/path/to/file.txt");
        Path path3 = Paths.get("C:", "temp", "file.txt"); // Windows

        // Из URI
        Path path4 = Paths.get(URI.create("file:///tmp/file.txt"));

        // Из File
        File file = new File("data.txt");
        Path path5 = file.toPath();

        // Обратное преобразование
        File fileFromPath = path5.toFile();

        // Получение информации о пути
        System.out.println("Path: " + path1);
        System.out.println("File name: " + path1.getFileName());
        System.out.println("Parent: " + path1.getParent());
        System.out.println("Root: " + path1.getRoot());
        System.out.println("Name count: " + path1.getNameCount());

        // Доступ к элементам пути
        for (int i = 0; i < path1.getNameCount(); i++) {
            System.out.println("Name " + i + ": " + path1.getName(i));
        }
    }

    public void pathOperations() {
        Path path = Paths.get("home", "user", "documents", "file.txt");

        // Нормализация пути
        Path normalized = Paths.get("home/./user/../user/documents/file.txt").normalize();
        // Результат: home/user/documents/file.txt

        // Преобразование в абсолютный путь
        Path absolute = path.toAbsolutePath();

        // Разрешение пути относительно другого
        Path base = Paths.get("/home/user");
        Path resolved = base.resolve("documents/file.txt");
        // Результат: /home/user/documents/file.txt

        // Относительный путь между двумя путями
        Path path1 = Paths.get("/home/user/docs");
        Path path2 = Paths.get("/home/user/music");
        Path relative = path1.relativize(path2);
        // Результат: ../music
    }

    public void pathComparison() {
        Path path1 = Paths.get("docs", "file.txt");
        Path path2 = Paths.get("docs", "file.txt");
        Path path3 = Paths.get("music", "file.txt");

        // Сравнение путей
        boolean equal = path1.equals(path2); // true
        boolean startsWith = path1.startsWith(Paths.get("docs")); // true
        boolean endsWith = path1.endsWith(Paths.get("file.txt")); // true

        // Сравнение без учета регистра (для Windows)
        Path caseInsensitive = FileSystems.getDefault().getPath("DOCS", "FILE.TXT");
        // На Unix системах регистр учитывается
    }
}
```

### Files API

```java
public class FilesApiExample {

    // Проверка существования файла
    public void checkFileExists(String filePath) {
        Path path = Paths.get(filePath);
        boolean exists = Files.exists(path);
        boolean notExists = Files.notExists(path);
        boolean isRegularFile = Files.isRegularFile(path);
        boolean isDirectory = Files.isDirectory(path);
        boolean isReadable = Files.isReadable(path);
        boolean isWritable = Files.isWritable(path);
        boolean isExecutable = Files.isExecutable(path);
    }

    // Создание директорий
    public void createDirectories(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);

        // Создание одной директории
        Files.createDirectory(path);

        // Создание всего пути
        Files.createDirectories(path);
    }

    // Копирование файлов
    public void copyFiles(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);

        // Простое копирование
        Files.copy(source, dest);

        // Копирование с заменой существующего файла
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

        // Копирование с сохранением атрибутов
        Files.copy(source, dest,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES);
    }

    // Перемещение файлов
    public void moveFiles(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);

        Files.move(source, dest);
        Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    // Удаление файлов
    public void deleteFiles(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Удаление файла
        Files.delete(path);

        // Удаление с проверкой существования
        Files.deleteIfExists(path);

        // Рекурсивное удаление директории
        Path directory = Paths.get("temp-dir");
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + p);
                    }
                });
        }
    }

    // Чтение всего файла
    public String readAllFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Чтение как байты
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes);

        // Чтение как строки
        List<String> lines = Files.readAllLines(path);
        String contentFromLines = String.join("\n", lines);

        return content;
    }

    // Запись в файл
    public void writeToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);

        // Запись строки
        Files.writeString(path, content);

        // Запись списка строк
        List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");
        Files.write(path, lines);

        // Append режим
        Files.writeString(path, "\nAdditional content",
            StandardOpenOption.APPEND);
    }

    // Обход директории
    public void walkDirectory(String dirPath) throws IOException {
        Path directory = Paths.get(dirPath);

        // Простой обход
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                .forEach(System.out::println);
        }

        // Обход с глубиной
        try (Stream<Path> paths = Files.walk(directory, 2)) {
            long fileCount = paths.filter(Files::isRegularFile).count();
            System.out.println("Files found: " + fileCount);
        }

        // Поиск файлов по шаблону
        PathMatcher matcher = FileSystems.getDefault()
            .getPathMatcher("glob:/*.java");

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(matcher::matches)
                .forEach(System.out::println);
        }
    }

    // Получение атрибутов файла
    public void getFileAttributes(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Базовые атрибуты
        BasicFileAttributes basicAttrs = Files.readAttributes(path, BasicFileAttributes.class);
        System.out.println("Size: " + basicAttrs.size());
        System.out.println("Created: " + basicAttrs.creationTime());
        System.out.println("Modified: " + basicAttrs.lastModifiedTime());
        System.out.println("Is directory: " + basicAttrs.isDirectory());

        // DOS атрибуты (Windows)
        DosFileAttributes dosAttrs = Files.readAttributes(path, DosFileAttributes.class);
        System.out.println("Hidden: " + dosAttrs.isHidden());
        System.out.println("System: " + dosAttrs.isSystem());

        // POSIX атрибуты (Unix/Linux)
        PosixFileAttributes posixAttrs = Files.readAttributes(path, PosixFileAttributes.class);
        System.out.println("Owner: " + posixAttrs.owner());
        System.out.println("Group: " + posixAttrs.group());
        System.out.println("Permissions: " + posixAttrs.permissions());
    }

    // Мониторинг изменений
    public void watchDirectory(String dirPath) throws IOException, InterruptedException {
        Path directory = Paths.get(dirPath);

        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                System.out.println(kind.name() + ": " + filename);
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
```

## Асинхронный `IO`

**Асинхронный IO** позволяет выполнять `IO` операции без блокировки потоков.

### Future-based асинхронный `IO`

```java
public class AsyncIoExample {

    // Асинхронное чтение файла
    public void asyncFileRead(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Создание AsynchronousFileChannel
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Асинхронное чтение
            Future<Integer> future = channel.read(buffer, 0);

            // Можно делать другие операции пока читается файл
            while (!future.isDone()) {
                System.out.println("Reading file...");
                Thread.sleep(100);
            }

            // Получение результата
            int bytesRead = future.get();
            buffer.flip();

            System.out.println("Read " + bytesRead + " bytes");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // Асинхронное чтение с callback
    public void asyncFileReadWithCallback(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Callback для обработки результата
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {

                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println("Read " + result + " bytes");

                    attachment.flip();
                    byte[] data = new byte[attachment.limit()];
                    attachment.get(data);

                    String content = new String(data);
                    System.out.println("Content: " + content);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.err.println("Error reading file: " + exc.getMessage());
                }
            });

            // Ждем завершения
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Асинхронная запись файла
    public void asyncFileWrite(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());

            Future<Integer> future = channel.write(buffer, 0);

            // Ожидание завершения
            int bytesWritten = future.get();
            System.out.println("Written " + bytesWritten + " bytes");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
```

## Оптимизация производительности

### Выбор правильного подхода

```java
public class IoPerformanceTips {

    // Для маленьких файлов - традиционный IO
    public String readSmallFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // Для больших файлов - потоковое чтение
    public void processLargeFile(String path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Обработка строки
                processLine(line);
            }
        }
    }

    // Для бинарных данных - NIO с буферами
    public void copyBinaryFile(String source, String dest) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(Paths.get(source), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Paths.get(dest),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }

    // Для сетевых операций - неблокирующий IO
    public void handleMultipleConnections() throws IOException {
        // Использование Selector для обработки множества соединений
        // в одном потоке
    }

    private void processLine(String line) {
        // Имитация обработки
        System.out.println("Processing: " + line);
    }
}
```

### Настройка буферов

```java
public class BufferOptimization {

    // Правильный размер буфера
    public static final int DEFAULT_BUFFER_SIZE = 8192; // 8KB
    public static final int LARGE_BUFFER_SIZE = 65536; // 64KB

    public void optimizedFileCopy(String source, String dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest);
             BufferedInputStream bis = new BufferedInputStream(fis, LARGE_BUFFER_SIZE);
             BufferedOutputStream bos = new BufferedOutputStream(fos, LARGE_BUFFER_SIZE)) {

            byte[] buffer = new byte[LARGE_BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Использование прямых буферов для больших объемов
    public void directBufferExample() {
        // Прямые буферы лучше для работы с каналами
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(64 * 1024);

        // Прямые буферы работают быстрее для больших объемов данных
        // Но дороже в создании и не подчищаются GC
        directBuffer.clear();
    }

    // Пул буферов для повторного использования
    public static class BufferPool {
        private final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<>();
        private final int bufferSize;

        public BufferPool(int bufferSize, int poolSize) {
            this.bufferSize = bufferSize;
            for (int i = 0; i < poolSize; i++) {
                pool.offer(ByteBuffer.allocate(bufferSize));
            }
        }

        public ByteBuffer acquire() {
            ByteBuffer buffer = pool.poll();
            return buffer != null ? buffer : ByteBuffer.allocate(bufferSize);
        }

        public void release(ByteBuffer buffer) {
            buffer.clear();
            pool.offer(buffer);
        }
    }
}
```

### Профилирование `IO` операций

```java
public class IoProfiling {

    public void profileIoOperations() throws IOException {
        Path testFile = Paths.get("large-test-file.dat");

        // Создание тестового файла
        createLargeTestFile(testFile, 100 * 1024 * 1024); // 100MB

        // Тестирование различных подходов
        profileTraditionalIo(testFile);
        profileNio(testFile);
        profileMemoryMapped(testFile);
    }

    private void createLargeTestFile(Path file, int size) throws IOException {
        byte[] data = new byte[8192];
        Arrays.fill(data, (byte) 'A');

        try (FileChannel channel = FileChannel.open(file,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            ByteBuffer buffer = ByteBuffer.wrap(data);
            int remaining = size;

            while (remaining > 0) {
                int toWrite = Math.min(remaining, data.length);
                buffer.limit(toWrite);
                channel.write(buffer);
                buffer.rewind();
                remaining -= toWrite;
            }
        }
    }

    private long profileTraditionalIo(Path file) throws IOException {
        long startTime = System.nanoTime();

        try (FileInputStream fis = new FileInputStream(file.toFile());
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[8192];
            while (bis.read(buffer) != -1) {
                // Имитация обработки
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Traditional IO: " + durationMs + " ms");
        return durationMs;
    }

    private long profileNio(Path file) throws IOException {
        long startTime = System.nanoTime();

        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192);

            while (channel.read(buffer) != -1) {
                buffer.flip();
                // Имитация обработки
                buffer.clear();
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("NIO: " + durationMs + " ms");
        return durationMs;
    }

    private long profileMemoryMapped(Path file) throws IOException {
        long startTime = System.nanoTime();

        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            while (buffer.hasRemaining()) {
                // Имитация обработки
                buffer.get();
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Memory Mapped: " + durationMs + " ms");
        return durationMs;
    }
}
```

## Обработка ошибок

### Правильная обработка `IO` исключений

```java
public class IoErrorHandling {

    // Безопасное чтение файла с обработкой ошибок
    public Optional<String> safeReadFile(String filePath) {
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(filePath))));
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + filePath);
            return Optional.empty();
        } catch (AccessDeniedException e) {
            System.err.println("Access denied: " + filePath);
            return Optional.empty();
        } catch (IOException e) {
            System.err.println("IO error reading file: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Транзакционная запись файла
    public boolean safeWriteFile(String filePath, String content) {
        Path path = Paths.get(filePath);
        Path tempPath = path.getParent().resolve(path.getFileName() + ".tmp");

        try {
            // Запись во временный файл
            Files.writeString(tempPath, content);

            // Атомарное перемещение
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());

            // Удаление временного файла при ошибке
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException cleanupError) {
                System.err.println("Error cleaning up temp file: " + cleanupError.getMessage());
            }

            return false;
        }
    }

    // Graceful degradation при IO ошибках
    public void processFilesWithGracefulDegradation(List<String> filePaths) {
        for (String filePath : filePaths) {
            try {
                String content = Files.readString(Paths.get(filePath));
                processContent(content);
            } catch (IOException e) {
                System.err.println("Skipping file due to error: " + filePath + " - " + e.getMessage());
                // Продолжаем обработку других файлов
            }
        }
    }

    // Retry механизм для нестабильных IO операций
    public String readFileWithRetry(String filePath, int maxRetries) {
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return Files.readString(Paths.get(filePath));
            } catch (IOException e) {
                attempt++;
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                }
            }
        }

        throw new RuntimeException("Failed to read file after " + maxRetries + " attempts");
    }

    private void processContent(String content) {
        // Имитация обработки
        System.out.println("Processing content of length: " + content.length());
    }
}
```

## Best practices

### Управление ресурсами

```java
public class ResourceManagement {

    // Правильное использование try-with-resources
    public void properResourceManagement() {
        // Автоматическое закрытие ресурсов
        try (FileInputStream fis = new FileInputStream("input.txt");
             FileOutputStream fos = new FileOutputStream("output.txt");
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }
        // Все ресурсы автоматически закрыты
    }

    // Избегание resource leaks
    public void avoidResourceLeaks() {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream("input.txt");
            fos = new FileOutputStream("output.txt");

            // Работа с файлами
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } finally {
            // Явное закрытие ресурсов в правильном порядке
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("Error closing input stream: " + e.getMessage());
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    // Использование custom resource management
    public void customResourceManagement() {
        try (CustomResource resource = new CustomResource()) {
            resource.doSomething();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    static class CustomResource implements AutoCloseable {
        @Override
        public void close() throws Exception {
            System.out.println("Custom resource closed");
        }

        public void doSomething() {
            System.out.println("Doing something with custom resource");
        }
    }
}
```

### Производительность и безопасность

```java
public class IoBestPractices {

    // Использование оптимального размера буфера
    public static final int OPTIMAL_BUFFER_SIZE = 8192; // 8KB для большинства случаев

    // Валидация входных данных
    public void validateFilePath(String filePath) {
        Path path = Paths.get(filePath);

        // Проверка на directory traversal
        if (path.normalize().startsWith(Paths.get("/safe/base/path").normalize())) {
            // Безопасный путь
        } else {
            throw new SecurityException("Invalid file path");
        }
    }

    // Ограничение размера файла
    public String readFileWithSizeLimit(String filePath, long maxSize) throws IOException {
        Path path = Paths.get(filePath);

        if (Files.size(path) > maxSize) {
            throw new IOException("File too large: " + Files.size(path) + " bytes");
        }

        return Files.readString(path);
    }

    // Атомарные операции
    public void atomicFileUpdate(String filePath, String newContent) throws IOException {
        Path path = Paths.get(filePath);
        Path tempPath = path.getParent().resolve(path.getFileName() + ".tmp");

        // Запись во временный файл
        Files.writeString(tempPath, newContent);

        // Атомарное перемещение
        Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
    }

    // Использование temporary files
    public void safeFileProcessing(String inputPath, String outputPath) throws IOException {
        Path input = Paths.get(inputPath);
        Path output = Paths.get(outputPath);

        // Создание temporary файла в той же директории
        Path tempOutput = Files.createTempFile(output.getParent(), "temp-", ".tmp");

        try {
            // Обработка файла
            processFile(input, tempOutput);

            // Атомарное переименование
            Files.move(tempOutput, output, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            // Удаление temporary файла при ошибке
            Files.deleteIfExists(tempOutput);
            throw e;
        }
    }

    // Параллельная обработка файлов
    public void parallelFileProcessing(List<String> filePaths) {
        filePaths.parallelStream()
            .forEach(filePath -> {
                try {
                    processSingleFile(filePath);
                } catch (IOException e) {
                    System.err.println("Error processing " + filePath + ": " + e.getMessage());
                }
            });
    }

    private void processFile(Path input, Path output) throws IOException {
        // Имитация обработки
        String content = Files.readString(input);
        String processed = content.toUpperCase();
        Files.writeString(output, processed);
    }

    private void processSingleFile(String filePath) throws IOException {
        // Имитация обработки одного файла
        System.out.println("Processing: " + filePath);
    }
}
```

### Выбор правильного `IO` подхода

```java
public class IoStrategySelector {

    public IoStrategy selectStrategy(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        if (fileSize < 1024 * 1024) { // < 1MB
            return new TraditionalIoStrategy();
        } else if (fileSize < 100 * 1024 * 1024) { // < 100MB
            return new NioStrategy();
        } else { // >= 100MB
            return new MemoryMappedStrategy();
        }
    }

    interface IoStrategy {
        void processFile(String filePath) throws IOException;
    }

    static class TraditionalIoStrategy implements IoStrategy {
        @Override
        public void processFile(String filePath) throws IOException {
            // Использование Files.readAllBytes() или BufferedInputStream
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            // Обработка данных
        }
    }

    static class NioStrategy implements IoStrategy {
        @Override
        public void processFile(String filePath) throws IOException {
            // Использование FileChannel и ByteBuffer
            try (FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(8192);
                while (channel.read(buffer) != -1) {
                    buffer.flip();
                    // Обработка данных
                    buffer.clear();
                }
            }
        }
    }

    static class MemoryMappedStrategy implements IoStrategy {
        @Override
        public void processFile(String filePath) throws IOException {
            // Использование MappedByteBuffer
            try (FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                // Обработка данных
            }
        }
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Java** `IO`/**NIO** предоставляет мощный и гибкий набор инструментов для работы с файлами и потоками данных. Выбор правильного подхода зависит от конкретных требований к производительности и функциональности.

### Основные принципы выбора `IO` подхода

1. **Для простых операций с небольшими файлами** Традиционный `IO` (java.io)
2. **Для высокопроизводительных операций** **NIO** с буферами и каналами
3. **Для работы с файловой системой** **NIO.2 Path API**
4. **Для неблокирующих сетевых операций** **NIO** с селекторами
5. **Для асинхронных операций** **AsynchronousFileChannel** или **NIO.2**

### Производительность

- **Традиционный IO**: Простой, но блокирующий
- **NIO**: Высокая производительность, неблокирующий
- **NIO.2**: Современный **API**, хорошая производительность
- **Асинхронный IO**: Лучшая масштабируемость для **concurrent** операций

### Безопасность

- **Валидация путей**: Предотвращение **directory traversal**
- **Ограничение размеров**: Защита от **DoS** атак
- **Атомарные операции**: Сохранение целостности данных
- **Правильное закрытие ресурсов**: Предотвращение утечек

### Best practices

1. **Всегда используйте `try-with`-resources** для автоматического закрытия ресурсов
2. **Выбирайте правильный размер буфера** (обычно 8KB)
3. **Используйте `NIO` для больших файлов** и высокой производительности
4. **Валидируйте входные данные** для предотвращения **security issues**
5. **Обрабатывайте ошибки gracefully** с **retry** механизмами
6. **Профилируйте `IO` операции** для оптимизации производительности

Правильное использование **Java** `IO` **API** позволяет создавать эффективные, безопасные и масштабируемые приложения.


[⬆ Наверх](./#java-cheatsheets)

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
