# Command Pattern (Команда)

Command инкапсулирует запрос как объект, позволяя параметризовать клиенты разными запросами, ставить запросы в очередь, логировать их, а также поддерживать отмену операций.

**Дата последнего обновления:** 2026-01-24

## Полезные ссылки

### Официальная документация
- [Java Runnable](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html)
- [Java Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)

### См. также
- `../../java/java-concurrency.md` - Java Concurrency
- `../../frameworks/java-frameworks/spring/spring-transactions.md` - Spring Transactions
- `../../patterns/behavioral/strategy.md` - Strategy Pattern

## Содержание

- [Что такое Command?](#что-такое-command)
- [Когда использовать Command?](#когда-использовать-command)
- [Структура паттерна](#структура-паттерна)
- [Реализация на Java](#реализация-на-java)
- [Продвинутые реализации](#продвинутые-реализации)
- [Примеры использования](#примеры-использования)
- [Best Practices](#best-practices)

## Что такое Command?

Command — это поведенческий паттерн проектирования, который превращает запросы в объекты, позволяя передавать их как аргументы при вызове методов, ставить запросы в очередь, логировать их, а также поддерживать отмену операций.

### Основные характеристики

1. **Инкапсуляция запроса**: Запрос становится объектом
2. **Параметризация**: Команды можно передавать как параметры
3. **Очереди и логирование**: Поддержка очередей и истории команд
4. **Отмена операций**: Возможность отмены выполненных команд
5. **Композиция команд**: Команды можно комбинировать

### Проблемы, которые решает

```java
// Плохо: Жесткая связь между вызывающим и исполнителем
public class RemoteControl {

    private Light light;
    private Stereo stereo;
    private CeilingFan fan;

    public RemoteControl() {
        this.light = new Light();
        this.stereo = new Stereo();
        this.fan = new CeilingFan();
    }

    // Жестко закодированные методы
    public void button1Pressed() {
        light.on();
    }

    public void button2Pressed() {
        stereo.on();
        stereo.setVolume(11);
    }

    public void button3Pressed() {
        fan.high();
    }

    public void button4Pressed() {
        // Отмена - невозможно реализовать без дублирования
        light.off();
    }
}

// Хорошо: Command паттерн
public class SmartRemoteControl {
    private Command[] onCommands;
    private Command[] offCommands;
    private Command undoCommand;

    public SmartRemoteControl() {
        onCommands = new Command[7];
        offCommands = new Command[7];

        Command noCommand = new NoCommand();
        for (int i = 0; i < 7; i++) {
            onCommands[i] = noCommand;
            offCommands[i] = noCommand;
        }
        undoCommand = noCommand;
    }

    public void setCommand(int slot, Command onCommand, Command offCommand) {
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }

    public void onButtonPressed(int slot) {
        onCommands[slot].execute();
        undoCommand = onCommands[slot];
    }

    public void offButtonPressed(int slot) {
        offCommands[slot].execute();
        undoCommand = offCommands[slot];
    }

    public void undoButtonPressed() {
        undoCommand.undo();
    }
}
```

## Когда использовать Command?

### Подходящие сценарии

- **GUI действия**: Кнопки, меню, горячие клавиши
- **Транзакции**: Группировка операций с откатом
- **Очереди задач**: Асинхронная обработка команд
- **Логирование**: Запись и воспроизведение действий
- **Макросы**: Комбинация команд
- **Отмена операций**: Undo/Redo функциональность

### Признаки необходимости

```java
// Признаки: Много похожих методов с разными получателями
public class TextEditor {

    // Плохо: Каждый метод знает о получателе
    public void copy() {
        String selectedText = textArea.getSelectedText();
        clipboard.setContents(selectedText);
    }

    public void cut() {
        String selectedText = textArea.getSelectedText();
        clipboard.setContents(selectedText);
        textArea.replaceSelection("");
    }

    public void paste() {
        String content = clipboard.getContents();
        textArea.replaceSelection(content);
    }

    public void undo() {
        // Undo требует знания о предыдущей операции
        // Невозможно реализовать без дополнительной логики
    }
}

// Хорошо: Command паттерн
public class CommandTextEditor {
    private CommandHistory history = new CommandHistory();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (history.hasCommands()) {
            Command command = history.pop();
            command.undo();
        }
    }
}
```

## Структура паттерна

```
┌─────────────────────────────────────────────────────────────┐
│                     Client                                 │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │           Command command = new ConcreteCommand(    │    │
│  │                               receiver);             │    │
│  │           invoker.setCommand(command);               │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┼─┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                     Invoker                                │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │              setCommand(command)                     │    │ │
│  │              execute()                               │    │ │
│  │                                                     │    │ │
│  │  // Инициирует запрос к команде для выполнения       │    │ │
│  │  // Не знает о конкретной команде или получателе     │    │ │
│  └─────────────────────────────────────────────────────┘    │ │
└─────────────────────────────────────────────────────────────┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                     Command                                │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │              execute()                              │    │ │
│  │              undo()                                │    │ │
│  │                                                     │    │ │
│  │  // Объявляет интерфейс для выполнения операций      │    │ │
│  └─────────────────────────────────────────────────────┘    │ │
└─────────────────────────────────────────────────────────────┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                ConcreteCommand                             │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │              execute()                              │    │ │
│  │              undo()                                │    │ │
│  │                                                     │    │ │
│  │  // Определяет связь между Receiver и действием      │    │ │
│  │  // Реализует execute() вызывая соответствующие      │    │ │
│  │  // операции Receiver                                │    │ │
│  └─────────────────────────────────────────────────────┘    │ │
└─────────────────────────────────────────────────────────────┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                     Receiver                               │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │              action()                               │    │ │
│  │                                                     │    │ │
│  │  // Знает как выполнять операции, связанные с        │    │ │
│  │  // запросом. Любой класс может выступать Receiver   │    │ │
│  └─────────────────────────────────────────────────────┘    │ │
└─────────────────────────────────────────────────────────────┘
```

### Компоненты

1. **Command**: Интерфейс команды с методом execute()
2. **ConcreteCommand**: Конкретная реализация команды
3. **Receiver**: Получатель команды (объект, выполняющий действие)
4. **Invoker**: Инициатор, вызывающий команду
5. **Client**: Создает команды и настраивает invoker

## Реализация на Java

### Классический Command

```java
// Command интерфейс
interface Command {
    void execute();
    void undo();
}

// Receiver - Получатель
class Light {
    private String location;

    public Light(String location) {
        this.location = location;
    }

    public void on() {
        System.out.println(location + " light is on");
    }

    public void off() {
        System.out.println(location + " light is off");
    }
}

// Concrete Commands
class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

// Stereo Receiver
class Stereo {
    private String location;

    public Stereo(String location) {
        this.location = location;
    }

    public void on() {
        System.out.println(location + " stereo is on");
    }

    public void off() {
        System.out.println(location + " stereo is off");
    }

    public void setVolume(int volume) {
        System.out.println(location + " stereo volume set to " + volume);
    }
}

class StereoOnWithVolumeCommand implements Command {
    private Stereo stereo;
    private int previousVolume;

    public StereoOnWithVolumeCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.on();
        previousVolume = 5; // Предполагаем предыдущую громкость
        stereo.setVolume(11);
    }

    @Override
    public void undo() {
        stereo.off();
        stereo.setVolume(previousVolume);
    }
}

// No Command для пустых слотов
class NoCommand implements Command {
    @Override
    public void execute() {
        // Ничего не делать
    }

    @Override
    public void undo() {
        // Ничего не делать
    }
}

// Invoker - Инициатор
class RemoteControl {
    private Command[] onCommands;
    private Command[] offCommands;
    private Command undoCommand;

    public RemoteControl() {
        onCommands = new Command[7];
        offCommands = new Command[7];

        Command noCommand = new NoCommand();
        for (int i = 0; i < 7; i++) {
            onCommands[i] = noCommand;
            offCommands[i] = noCommand;
        }
        undoCommand = noCommand;
    }

    public void setCommand(int slot, Command onCommand, Command offCommand) {
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }

    public void onButtonPressed(int slot) {
        onCommands[slot].execute();
        undoCommand = onCommands[slot];
    }

    public void offButtonPressed(int slot) {
        offCommands[slot].execute();
        undoCommand = offCommands[slot];
    }

    public void undoButtonPressed() {
        undoCommand.undo();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------ Remote Control -------\n");
        for (int i = 0; i < onCommands.length; i++) {
            sb.append("[slot ").append(i).append("] ")
              .append(onCommands[i].getClass().getName())
              .append("    ")
              .append(offCommands[i].getClass().getName())
              .append("\n");
        }
        sb.append("[undo] ").append(undoCommand.getClass().getName()).append("\n");
        return sb.toString();
    }
}

// Client
public class RemoteControlDemo {
    public static void main(String[] args) {
        RemoteControl remote = new RemoteControl();

        Light livingRoomLight = new Light("Living Room");
        Stereo livingRoomStereo = new Stereo("Living Room");

        LightOnCommand livingRoomLightOn = new LightOnCommand(livingRoomLight);
        LightOffCommand livingRoomLightOff = new LightOffCommand(livingRoomLight);

        StereoOnWithVolumeCommand stereoOnWithVolume = new StereoOnWithVolumeCommand(livingRoomStereo);

        remote.setCommand(0, livingRoomLightOn, livingRoomLightOff);
        remote.setCommand(1, stereoOnWithVolume, new NoCommand());

        System.out.println(remote);

        remote.onButtonPressed(0);
        remote.offButtonPressed(0);
        remote.onButtonPressed(1);
        remote.undoButtonPressed();
    }
}
```

### Command с историей (Undo/Redo)

```java
// Command с поддержкой истории
interface UndoableCommand extends Command {
    void undo();
    void redo();
}

// Command History
class CommandHistory {
    private final Deque<UndoableCommand> history = new LinkedList<>();
    private final Deque<UndoableCommand> redoHistory = new LinkedList<>();

    public void push(UndoableCommand command) {
        history.push(command);
        redoHistory.clear(); // Очищаем redo при новом действии
    }

    public boolean hasCommands() {
        return !history.isEmpty();
    }

    public UndoableCommand pop() {
        UndoableCommand command = history.pop();
        redoHistory.push(command);
        return command;
    }

    public boolean hasRedoCommands() {
        return !redoHistory.isEmpty();
    }

    public UndoableCommand redo() {
        if (hasRedoCommands()) {
            UndoableCommand command = redoHistory.pop();
            history.push(command);
            return command;
        }
        return null;
    }

    public void clear() {
        history.clear();
        redoHistory.clear();
    }
}

// Text Editor Commands
class TextEditor {
    private StringBuilder buffer = new StringBuilder();
    private int cursorPosition = 0;

    public void insert(String text) {
        buffer.insert(cursorPosition, text);
        cursorPosition += text.length();
    }

    public void delete(int length) {
        if (cursorPosition >= length) {
            buffer.delete(cursorPosition - length, cursorPosition);
            cursorPosition -= length;
        }
    }

    public void setCursor(int position) {
        if (position >= 0 && position <= buffer.length()) {
            cursorPosition = position;
        }
    }

    public String getText() {
        return buffer.toString();
    }

    public int getCursorPosition() {
        return cursorPosition;
    }
}

// Insert Command
class InsertCommand implements UndoableCommand {
    private final TextEditor editor;
    private final String text;
    private final int position;

    public InsertCommand(TextEditor editor, String text) {
        this.editor = editor;
        this.text = text;
        this.position = editor.getCursorPosition();
    }

    @Override
    public void execute() {
        editor.setCursor(position);
        editor.insert(text);
    }

    @Override
    public void undo() {
        editor.setCursor(position + text.length());
        editor.delete(text.length());
    }

    @Override
    public void redo() {
        execute(); // Для redo просто выполняем команду заново
    }
}

// Delete Command
class DeleteCommand implements UndoableCommand {
    private final TextEditor editor;
    private final int length;
    private final int position;
    private String deletedText;

    public DeleteCommand(TextEditor editor, int length) {
        this.editor = editor;
        this.length = length;
        this.position = editor.getCursorPosition();
    }

    @Override
    public void execute() {
        editor.setCursor(position);
        // Сохраняем удаляемый текст для undo
        String currentText = editor.getText();
        int start = Math.min(position, currentText.length());
        int end = Math.min(start + length, currentText.length());
        deletedText = currentText.substring(start, end);
        editor.delete(length);
    }

    @Override
    public void undo() {
        editor.setCursor(position);
        editor.insert(deletedText);
    }

    @Override
    public void redo() {
        execute();
    }
}

// Text Editor with Command History
class CommandTextEditor {
    private final TextEditor editor = new TextEditor();
    private final CommandHistory history = new CommandHistory();

    public void executeCommand(UndoableCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (history.hasCommands()) {
            UndoableCommand command = history.pop();
            command.undo();
        }
    }

    public void redo() {
        UndoableCommand command = history.redo();
        if (command != null) {
            command.redo();
        }
    }

    public String getText() {
        return editor.getText();
    }

    public int getCursorPosition() {
        return editor.getCursorPosition();
    }

    public void setCursor(int position) {
        editor.setCursor(position);
    }
}

public class TextEditorDemo {
    public static void main(String[] args) {
        CommandTextEditor editor = new CommandTextEditor();

        // Вводим текст
        editor.executeCommand(new InsertCommand(editor.editor, "Hello World!"));
        System.out.println("Text: '" + editor.getText() + "'");

        // Вставляем еще текст
        editor.setCursor(6);
        editor.executeCommand(new InsertCommand(editor.editor, "Beautiful "));
        System.out.println("Text: '" + editor.getText() + "'");

        // Удаляем часть текста
        editor.setCursor(16);
        editor.executeCommand(new DeleteCommand(editor.editor, 6));
        System.out.println("Text: '" + editor.getText() + "'");

        // Undo
        editor.undo();
        System.out.println("After undo: '" + editor.getText() + "'");

        // Undo еще раз
        editor.undo();
        System.out.println("After second undo: '" + editor.getText() + "'");

        // Redo
        editor.redo();
        System.out.println("After redo: '" + editor.getText() + "'");
    }
}
```

### Java Runnable и Callable

```java
// Command с Java Concurrency
public class ConcurrentCommandDemo {

    // Command для выполнения в пуле потоков
    interface AsyncCommand extends Command {
        void executeAsync(Executor executor, Callback callback);
    }

    interface Callback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }

    // Database Command
    static class DatabaseCommand implements AsyncCommand {
        private final String sql;
        private final Object[] params;

        public DatabaseCommand(String sql, Object... params) {
            this.sql = sql;
            this.params = params;
        }

        @Override
        public void execute() {
            // Синхронное выполнение
            try {
                executeDatabaseOperation();
                System.out.println("Database operation completed synchronously");
            } catch (Exception e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        @Override
        public void executeAsync(Executor executor, Callback callback) {
            executor.execute(() -> {
                try {
                    Object result = executeDatabaseOperation();
                    callback.onSuccess(result);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            });
        }

        private Object executeDatabaseOperation() throws Exception {
            // Имитация работы с БД
            Thread.sleep(100);
            System.out.println("Executing SQL: " + sql);
            return "Result for: " + sql;
        }

        @Override
        public void undo() {
            // Откат транзакции или компенсационная операция
            System.out.println("Undoing database operation");
        }
    }

    // File Operation Command
    static class FileOperationCommand implements AsyncCommand {
        private final String filePath;
        private final String operation;
        private final String content;

        public FileOperationCommand(String filePath, String operation, String content) {
            this.filePath = filePath;
            this.operation = operation;
            this.content = content;
        }

        @Override
        public void execute() {
            try {
                executeFileOperation();
                System.out.println("File operation completed synchronously");
            } catch (Exception e) {
                System.err.println("File operation failed: " + e.getMessage());
            }
        }

        @Override
        public void executeAsync(Executor executor, Callback callback) {
            executor.execute(() -> {
                try {
                    Object result = executeFileOperation();
                    callback.onSuccess(result);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            });
        }

        private Object executeFileOperation() throws Exception {
            // Имитация работы с файлами
            Thread.sleep(50);
            System.out.println("File " + operation + " on: " + filePath);
            return "File " + filePath + " " + operation + "d";
        }

        @Override
        public void undo() {
            // Удаление файла, восстановление из бэкапа и т.д.
            System.out.println("Undoing file operation on: " + filePath);
        }
    }

    // Command Processor с пулом потоков
    static class AsyncCommandProcessor {
        private final ExecutorService executor = Executors.newFixedThreadPool(5);
        private final BlockingQueue<AsyncCommand> commandQueue = new LinkedBlockingQueue<>();
        private final AtomicBoolean running = new AtomicBoolean(true);

        public AsyncCommandProcessor() {
            // Запускаем обработчик команд
            startCommandProcessor();
        }

        public void submitCommand(AsyncCommand command, Callback callback) {
            commandQueue.offer(command);
        }

        private void startCommandProcessor() {
            executor.submit(() -> {
                while (running.get()) {
                    try {
                        AsyncCommand command = commandQueue.poll(1, TimeUnit.SECONDS);
                        if (command != null) {
                            command.executeAsync(executor, new Callback() {
                                @Override
                                public void onSuccess(Object result) {
                                    System.out.println("Command completed: " + result);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    System.err.println("Command failed: " + e.getMessage());
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        public void shutdown() {
            running.set(false);
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AsyncCommandProcessor processor = new AsyncCommandProcessor();

        // Создаем команды
        DatabaseCommand dbCommand1 = new DatabaseCommand("SELECT * FROM users");
        DatabaseCommand dbCommand2 = new DatabaseCommand("UPDATE users SET active = 1");
        FileOperationCommand fileCommand = new FileOperationCommand("/tmp/test.txt", "write", "Hello World");

        // Отправляем команды на выполнение
        processor.submitCommand(dbCommand1, new Callback() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("DB Command 1 success: " + result);
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("DB Command 1 failed: " + e.getMessage());
            }
        });

        processor.submitCommand(fileCommand, new Callback() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("File command success: " + result);
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("File command failed: " + e.getMessage());
            }
        });

        processor.submitCommand(dbCommand2, new Callback() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("DB Command 2 success: " + result);
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("DB Command 2 failed: " + e.getMessage());
            }
        });

        // Даем время на выполнение
        Thread.sleep(2000);

        processor.shutdown();
    }
}
```

## Продвинутые реализации

### 1. Command с Composite и Macro

```java
// Composite Command (Macro Command)
interface MacroCommand extends Command {
    void addCommand(Command command);
    void removeCommand(Command command);
    List<Command> getCommands();
}

class CompositeCommand implements MacroCommand {
    private final List<Command> commands = new ArrayList<>();
    private final String name;

    public CompositeCommand(String name) {
        this.name = name;
    }

    @Override
    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void removeCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    @Override
    public void execute() {
        System.out.println("Executing macro command: " + name);
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public void undo() {
        System.out.println("Undoing macro command: " + name);
        // Выполняем undo в обратном порядке
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}

// Command Queue с приоритетами
class PriorityCommandQueue {
    private final PriorityBlockingQueue<PriorityCommand> queue = new PriorityBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final AtomicBoolean running = new AtomicBoolean(true);

    static class PriorityCommand implements Comparable<PriorityCommand> {
        private final Command command;
        private final int priority;
        private final long timestamp;

        public PriorityCommand(Command command, int priority) {
            this.command = command;
            this.priority = priority;
            this.timestamp = System.nanoTime();
        }

        public Command getCommand() { return command; }

        @Override
        public int compareTo(PriorityCommand other) {
            // Высокий приоритет выполняется первым
            int priorityCompare = Integer.compare(other.priority, this.priority);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            // При равном приоритете - по времени добавления (FIFO)
            return Long.compare(this.timestamp, other.timestamp);
        }
    }

    public void submitCommand(Command command, int priority) {
        queue.offer(new PriorityCommand(command, priority));
    }

    public void startProcessing() {
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                while (running.get()) {
                    try {
                        PriorityCommand priorityCommand = queue.poll(1, TimeUnit.SECONDS);
                        if (priorityCommand != null) {
                            priorityCommand.getCommand().execute();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void shutdown() {
        running.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

// Transactional Command
abstract class TransactionalCommand implements Command {
    protected boolean executed = false;

    @Override
    public final void execute() {
        if (!executed) {
            try {
                doExecute();
                executed = true;
            } catch (Exception e) {
                try {
                    doRollback();
                } catch (Exception rollbackException) {
                    // Логируем ошибку отката
                }
                throw new RuntimeException("Command execution failed", e);
            }
        }
    }

    @Override
    public final void undo() {
        if (executed) {
            try {
                doUndo();
                executed = false;
            } catch (Exception e) {
                throw new RuntimeException("Command undo failed", e);
            }
        }
    }

    protected abstract void doExecute() throws Exception;
    protected abstract void doUndo() throws Exception;
    protected abstract void doRollback() throws Exception;
}

class BankTransferCommand extends TransactionalCommand {
    private final BankAccount fromAccount;
    private final BankAccount toAccount;
    private final BigDecimal amount;

    public BankTransferCommand(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    protected void doExecute() throws Exception {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        fromAccount.debit(amount);
        toAccount.credit(amount);
    }

    @Override
    protected void doUndo() throws Exception {
        // Компенсирующая транзакция
        toAccount.debit(amount);
        fromAccount.credit(amount);
    }

    @Override
    protected void doRollback() throws Exception {
        // В случае ошибки - возвращаем средства
        if (fromAccount.getBalance().compareTo(amount) >= 0) {
            fromAccount.debit(amount);
        }
    }

    static class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    static class BankAccount {
        private final String accountId;
        private BigDecimal balance;

        public BankAccount(String accountId, BigDecimal initialBalance) {
            this.accountId = accountId;
            this.balance = initialBalance;
        }

        public void debit(BigDecimal amount) {
            balance = balance.subtract(amount);
        }

        public void credit(BigDecimal amount) {
            balance = balance.add(amount);
        }

        public BigDecimal getBalance() { return balance; }
        public String getAccountId() { return accountId; }
    }
}

public class AdvancedCommandDemo {
    public static void main(String[] args) {
        // Composite Command
        MacroCommand partyMode = new CompositeCommand("Party Mode");
        partyMode.addCommand(() -> System.out.println("Turning on lights"));
        partyMode.addCommand(() -> System.out.println("Starting music"));
        partyMode.addCommand(() -> System.out.println("Lowering blinds"));

        MacroCommand workMode = new CompositeCommand("Work Mode");
        workMode.addCommand(() -> System.out.println("Turning off music"));
        workMode.addCommand(() -> System.out.println("Raising blinds"));
        workMode.addCommand(() -> System.out.println("Turning on desk light"));

        System.out.println("=== Macro Commands ===");
        partyMode.execute();
        workMode.execute();
        partyMode.undo();

        // Priority Queue
        System.out.println("\n=== Priority Commands ===");
        PriorityCommandQueue priorityQueue = new PriorityCommandQueue();
        priorityQueue.startProcessing();

        // Высокий приоритет
        priorityQueue.submitCommand(() -> System.out.println("High priority: System alert"), 10);
        // Средний приоритет
        priorityQueue.submitCommand(() -> System.out.println("Medium priority: User notification"), 5);
        // Низкий приоритет
        priorityQueue.submitCommand(() -> System.out.println("Low priority: Background task"), 1);
        priorityQueue.submitCommand(() -> System.out.println("Another high priority: Critical update"), 10);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        priorityQueue.shutdown();

        // Transactional Command
        System.out.println("\n=== Transactional Commands ===");
        BankTransferCommand.BankAccount account1 =
            new BankTransferCommand.BankAccount("ACC001", BigDecimal.valueOf(1000));
        BankTransferCommand.BankAccount account2 =
            new BankTransferCommand.BankAccount("ACC002", BigDecimal.valueOf(500));

        BankTransferCommand transfer = new BankTransferCommand(account1, account2, BigDecimal.valueOf(200));

        try {
            transfer.execute();
            System.out.println("Transfer successful");
            System.out.println("Account 1 balance: " + account1.getBalance());
            System.out.println("Account 2 balance: " + account2.getBalance());

            transfer.undo();
            System.out.println("Transfer undone");
            System.out.println("Account 1 balance: " + account1.getBalance());
            System.out.println("Account 2 balance: " + account2.getBalance());

        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }
    }
}
```

### 2. Spring Command Pattern

```java
// Spring-based Command Pattern
@Component
public class SpringCommandProcessor {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Map<String, Command> commandRegistry = new ConcurrentHashMap<>();
    private final CommandHistory history = new CommandHistory();

    @PostConstruct
    public void init() {
        // Регистрация команд через reflection или конфигурацию
        registerCommand("user.create", new CreateUserCommand());
        registerCommand("email.send", new SendEmailCommand());
    }

    public void registerCommand(String name, Command command) {
        commandRegistry.put(name, command);
    }

    @Transactional
    public CommandResult executeCommand(String commandName, Map<String, Object> parameters) {
        Command command = commandRegistry.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }

        try {
            // Создаем контекст выполнения
            CommandContext context = new CommandContext(parameters, eventPublisher);

            // Выполняем команду
            Object result = command.execute();

            // Сохраняем в истории для возможного отката
            if (command instanceof UndoableCommand) {
                history.push((UndoableCommand) command);
            }

            // Публикуем событие
            eventPublisher.publishEvent(new CommandExecutedEvent(this, commandName, result));

            return CommandResult.success(result);

        } catch (Exception e) {
            // Публикуем событие об ошибке
            eventPublisher.publishEvent(new CommandFailedEvent(this, commandName, e));

            throw new CommandExecutionException("Command execution failed", e);
        }
    }

    @Transactional
    public void undoLastCommand() {
        if (history.hasCommands()) {
            UndoableCommand command = history.pop();
            command.undo();
            eventPublisher.publishEvent(new CommandUndoneEvent(this, command));
        }
    }

    public List<String> getAvailableCommands() {
        return new ArrayList<>(commandRegistry.keySet());
    }
}

// Command Context
public class CommandContext {
    private final Map<String, Object> parameters;
    private final ApplicationEventPublisher eventPublisher;

    public CommandContext(Map<String, Object> parameters, ApplicationEventPublisher eventPublisher) {
        this.parameters = new HashMap<>(parameters);
        this.eventPublisher = eventPublisher;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, Class<T> type) {
        Object value = parameters.get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    public <T> T getParameter(String key, Class<T> type, T defaultValue) {
        T value = getParameter(key, type);
        return value != null ? value : defaultValue;
    }

    public void publishEvent(Object event) {
        eventPublisher.publishEvent(event);
    }
}

// Event Classes
public class CommandExecutedEvent extends ApplicationEvent {
    private final String commandName;
    private final Object result;

    public CommandExecutedEvent(Object source, String commandName, Object result) {
        super(source);
        this.commandName = commandName;
        this.result = result;
    }

    public String getCommandName() { return commandName; }
    public Object getResult() { return result; }
}

public class CommandFailedEvent extends ApplicationEvent {
    private final String commandName;
    private final Exception exception;

    public CommandFailedEvent(Object source, String commandName, Exception exception) {
        super(source);
        this.commandName = commandName;
        this.exception = exception;
    }

    public String getCommandName() { return commandName; }
    public Exception getException() { return exception; }
}

public class CommandUndoneEvent extends ApplicationEvent {
    private final UndoableCommand command;

    public CommandUndoneEvent(Object source, UndoableCommand command) {
        super(source);
        this.command = command;
    }

    public UndoableCommand getCommand() { return command; }
}

// Result class
public class CommandResult {
    private final boolean success;
    private final Object result;
    private final String errorMessage;

    private CommandResult(boolean success, Object result, String errorMessage) {
        this.success = success;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public static CommandResult success(Object result) {
        return new CommandResult(true, result, null);
    }

    public static CommandResult failure(String errorMessage) {
        return new CommandResult(false, null, errorMessage);
    }

    public boolean isSuccess() { return success; }
    public Object getResult() { return result; }
    public String getErrorMessage() { return errorMessage; }
}

// Concrete Commands
@Component
class CreateUserCommand implements UndoableCommand {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Object execute() {
        // Логика создания пользователя
        User user = new User();
        return userRepository.save(user);
    }

    @Override
    public void undo() {
        // Логика удаления пользователя
    }
}

@Component
class SendEmailCommand implements Command {
    @Autowired
    private EmailService emailService;

    @Override
    public Object execute() {
        // Логика отправки email
        return emailService.sendEmail("recipient@example.com", "Subject", "Body");
    }

    @Override
    public void undo() {
        // Email нельзя "отменить", но можно отправить компенсационный email
    }
}
```

## Примеры использования

### 1. GUI Action Commands

```java
// Swing GUI с Command паттерном
public class GUICommandExample extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final CommandHistory history = new CommandHistory();

    public GUICommandExample() {
        initUI();
    }

    private void initUI() {
        setTitle("Text Editor with Undo/Redo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text area
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Toolbar
        JToolBar toolbar = new JToolBar();
        add(toolbar, BorderLayout.NORTH);

        // Buttons
        JButton boldButton = new JButton("Bold");
        JButton italicButton = new JButton("Italic");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");

        toolbar.add(boldButton);
        toolbar.add(italicButton);
        toolbar.addSeparator();
        toolbar.add(undoButton);
        toolbar.add(redoButton);

        // Command actions
        boldButton.addActionListener(e -> executeCommand(new BoldCommand(textArea)));
        italicButton.addActionListener(e -> executeCommand(new ItalicCommand(textArea)));
        undoButton.addActionListener(e -> undo());
        redoButton.addActionListener(e -> redo());

        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void executeCommand(TextCommand command) {
        command.execute();
        history.push(command);
    }

    private void undo() {
        if (history.hasCommands()) {
            TextCommand command = history.pop();
            command.undo();
        }
    }

    private void redo() {
        TextCommand command = history.redo();
        if (command != null) {
            command.redo();
        }
    }

    // Text Commands
    interface TextCommand extends UndoableCommand {
        void setTextArea(JTextArea textArea);
    }

    static class BoldCommand implements TextCommand {
        private JTextArea textArea;
        private int start;
        private int end;
        private String originalText;
        private String newText;

        @Override
        public void setTextArea(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void execute() {
            start = textArea.getSelectionStart();
            end = textArea.getSelectionEnd();
            originalText = textArea.getSelectedText();

            if (originalText != null && !originalText.isEmpty()) {
                newText = "<b>" + originalText + "</b>";
                textArea.replaceSelection(newText);
            }
        }

        @Override
        public void undo() {
            if (newText != null) {
                textArea.select(start, start + newText.length());
                textArea.replaceSelection(originalText);
            }
        }

        @Override
        public void redo() {
            execute();
        }
    }

    static class ItalicCommand implements TextCommand {
        private JTextArea textArea;
        private int start;
        private int end;
        private String originalText;
        private String newText;

        @Override
        public void setTextArea(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void execute() {
            start = textArea.getSelectionStart();
            end = textArea.getSelectionEnd();
            originalText = textArea.getSelectedText();

            if (originalText != null && !originalText.isEmpty()) {
                newText = "<i>" + originalText + "</i>";
                textArea.replaceSelection(newText);
            }
        }

        @Override
        public void undo() {
            if (newText != null) {
                textArea.select(start, start + newText.length());
                textArea.replaceSelection(originalText);
            }
        }

        @Override
        public void redo() {
            execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUICommandExample().setVisible(true);
        });
    }
}
```

### 2. Database Transaction Commands

```java
// Database Commands с транзакциями
public class DatabaseCommandExample {

    // Command для работы с БД
    interface DatabaseCommand extends Command {
        Connection getConnection();
        void setConnection(Connection connection);
    }

    // Repository с Command паттерном
    static class UserRepository {
        private final DataSource dataSource;

        public UserRepository(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public User save(User user) throws SQLException {
            SaveUserCommand command = new SaveUserCommand(user);
            command.setConnection(dataSource.getConnection());
            command.execute();
            return command.getSavedUser();
        }

        public void delete(Long userId) throws SQLException {
            DeleteUserCommand command = new DeleteUserCommand(userId);
            command.setConnection(dataSource.getConnection());
            command.execute();
        }

        public List<User> findAll() throws SQLException {
            FindAllUsersCommand command = new FindAllUsersCommand();
            command.setConnection(dataSource.getConnection());
            command.execute();
            return command.getUsers();
        }
    }

    // Concrete Commands
    static class SaveUserCommand implements DatabaseCommand {
        private Connection connection;
        private final User user;
        private User savedUser;

        public SaveUserCommand(User user) {
            this.user = user;
        }

        @Override
        public Connection getConnection() { return connection; }

        @Override
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void execute() throws SQLException {
            String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        savedUser = new User(keys.getLong(1), user.getName(), user.getEmail());
                    }
                }
            }
        }

        @Override
        public void undo() throws SQLException {
            if (savedUser != null) {
                String sql = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, savedUser.getId());
                    stmt.executeUpdate();
                }
            }
        }

        public User getSavedUser() { return savedUser; }
    }

    static class DeleteUserCommand implements DatabaseCommand {
        private Connection connection;
        private final Long userId;
        private User deletedUser;

        public DeleteUserCommand(Long userId) {
            this.userId = userId;
        }

        @Override
        public Connection getConnection() { return connection; }

        @Override
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void execute() throws SQLException {
            // Сначала сохраняем данные для отката
            String selectSql = "SELECT id, name, email FROM users WHERE id = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setLong(1, userId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        deletedUser = new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
                    }
                }
            }

            // Затем удаляем
            String deleteSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setLong(1, userId);
                deleteStmt.executeUpdate();
            }
        }

        @Override
        public void undo() throws SQLException {
            if (deletedUser != null) {
                String sql = "INSERT INTO users (id, name, email) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, deletedUser.getId());
                    stmt.setString(2, deletedUser.getName());
                    stmt.setString(3, deletedUser.getEmail());
                    stmt.executeUpdate();
                }
            }
        }
    }

    static class FindAllUsersCommand implements DatabaseCommand {
        private Connection connection;
        private List<User> users;

        @Override
        public Connection getConnection() { return connection; }

        @Override
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void execute() throws SQLException {
            String sql = "SELECT id, name, email FROM users ORDER BY name";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                users = new ArrayList<>();
                while (rs.next()) {
                    users.add(new User(rs.getLong("id"), rs.getString("name"), rs.getString("email")));
                }
            }
        }

        @Override
        public void undo() {
            // Read-only команда, undo не требуется
        }

        public List<User> getUsers() { return users; }
    }

    // Entity
    static class User {
        private Long id;
        private String name;
        private String email;

        public User() {}

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
        }
    }

    // Command Processor с транзакциями
    static class TransactionalCommandProcessor {
        private final DataSource dataSource;

        public TransactionalCommandProcessor(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public <T> T executeInTransaction(DatabaseCommand command) throws SQLException {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                try {
                    command.setConnection(connection);
                    command.execute();
                    connection.commit();

                    // Для команд с результатом
                    if (command instanceof SaveUserCommand) {
                        return (T) ((SaveUserCommand) command).getSavedUser();
                    } else if (command instanceof FindAllUsersCommand) {
                        return (T) ((FindAllUsersCommand) command).getUsers();
                    }

                    return null;

                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }
        }

        public void executeInTransaction(DatabaseCommand... commands) throws SQLException {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                try {
                    for (DatabaseCommand command : commands) {
                        command.setConnection(connection);
                        command.execute();
                    }
                    connection.commit();

                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }
        }
    }

    // Пример использования (псевдо DataSource)
    public static void main(String[] args) {
        // В реальном приложении использовался бы настоящий DataSource
        // DataSource dataSource = ...;

        System.out.println("Database Command Pattern Demo");
        System.out.println("This example shows how to structure database operations as commands");
        System.out.println("with transaction support and undo capabilities.");
    }
}
```

### 3. Workflow Engine

```java
// Workflow Engine с Command паттерном
public class WorkflowEngineExample {

    // Workflow Step Command
    interface WorkflowStep extends Command {
        String getStepName();
        boolean isRequired();
        WorkflowContext getContext();
        void setContext(WorkflowContext context);
    }

    // Workflow Context
    static class WorkflowContext {
        private final Map<String, Object> data = new ConcurrentHashMap<>();
        private final List<String> executedSteps = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        public void put(String key, Object value) {
            data.put(key, value);
        }

        public <T> T get(String key, Class<T> type) {
            return type.cast(data.get(key));
        }

        public void addExecutedStep(String stepName) {
            executedSteps.add(stepName);
        }

        public void addError(String error) {
            errors.add(error);
        }

        public List<String> getExecutedSteps() { return new ArrayList<>(executedSteps); }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }

    // Concrete Workflow Steps
    static class ValidateOrderStep implements WorkflowStep {
        private WorkflowContext context;
        private Order validatedOrder;

        @Override
        public String getStepName() { return "Validate Order"; }

        @Override
        public boolean isRequired() { return true; }

        @Override
        public WorkflowContext getContext() { return context; }

        @Override
        public void setContext(WorkflowContext context) {
            this.context = context;
        }

        @Override
        public void execute() {
            Order order = context.get("order", Order.class);
            if (order == null) {
                context.addError("Order not found in context");
                return;
            }

            // Валидация
            if (order.getItems().isEmpty()) {
                context.addError("Order must have at least one item");
                return;
            }

            if (order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                context.addError("Order total must be positive");
                return;
            }

            validatedOrder = order;
            context.put("validatedOrder", validatedOrder);
            context.addExecutedStep(getStepName());
            System.out.println("Order validation completed successfully");
        }

        @Override
        public void undo() {
            context.getData().remove("validatedOrder");
            // Удаляем из выполненных шагов
            context.getExecutedSteps().remove(getStepName());
            System.out.println("Order validation undone");
        }
    }

    static class ProcessPaymentStep implements WorkflowStep {
        private WorkflowContext context;
        private PaymentResult paymentResult;

        @Override
        public String getStepName() { return "Process Payment"; }

        @Override
        public boolean isRequired() { return true; }

        @Override
        public WorkflowContext getContext() { return context; }

        @Override
        public void setContext(WorkflowContext context) {
            this.context = context;
        }

        @Override
        public void execute() {
            Order order = context.get("validatedOrder", Order.class);
            if (order == null) {
                context.addError("Validated order not found");
                return;
            }

            // Имитация обработки платежа
            try {
                Thread.sleep(500); // Имитация сетевого вызова
                paymentResult = new PaymentResult(true, "PAY-" + System.currentTimeMillis());
                context.put("paymentResult", paymentResult);
                context.addExecutedStep(getStepName());
                System.out.println("Payment processed successfully: " + paymentResult.getTransactionId());
            } catch (InterruptedException e) {
                context.addError("Payment processing interrupted");
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void undo() {
            if (paymentResult != null) {
                // Имитация возврата платежа
                System.out.println("Payment refunded: " + paymentResult.getTransactionId());
                context.getData().remove("paymentResult");
                context.getExecutedSteps().remove(getStepName());
            }
        }

        static class PaymentResult {
            private final boolean success;
            private final String transactionId;

            public PaymentResult(boolean success, String transactionId) {
                this.success = success;
                this.transactionId = transactionId;
            }

            public boolean isSuccess() { return success; }
            public String getTransactionId() { return transactionId; }
        }
    }

    static class SendConfirmationStep implements WorkflowStep {
        private WorkflowContext context;

        @Override
        public String getStepName() { return "Send Confirmation"; }

        @Override
        public boolean isRequired() { return false; } // Необязательный шаг

        @Override
        public WorkflowContext getContext() { return context; }

        @Override
        public void setContext(WorkflowContext context) {
            this.context = context;
        }

        @Override
        public void execute() {
            Order order = context.get("validatedOrder", Order.class);
            PaymentResult payment = context.get("paymentResult", ProcessPaymentStep.PaymentResult.class);

            if (order != null && payment != null) {
                // Имитация отправки email
                System.out.println("Confirmation email sent to customer for order: " + order.getId());
                context.addExecutedStep(getStepName());
            } else {
                context.addError("Cannot send confirmation: missing order or payment data");
            }
        }

        @Override
        public void undo() {
            System.out.println("Confirmation email cannot be 'undone'");
            // Email уже отправлен, undo не имеет смысла
        }
    }

    // Workflow Engine
    static class WorkflowEngine {
        private final List<WorkflowStep> steps = new ArrayList<>();
        private final WorkflowContext context = new WorkflowContext();

        public void addStep(WorkflowStep step) {
            steps.add(step);
            step.setContext(context);
        }

        public WorkflowResult execute(Object input) {
            context.put("input", input);

            for (WorkflowStep step : steps) {
                if (context.hasErrors() && step.isRequired()) {
                    break; // Прерываем выполнение при ошибках в обязательных шагах
                }

                try {
                    step.execute();
                } catch (Exception e) {
                    context.addError("Step '" + step.getStepName() + "' failed: " + e.getMessage());
                    if (step.isRequired()) {
                        break;
                    }
                }
            }

            return new WorkflowResult(context);
        }

        public void undo() {
            // Откатываем шаги в обратном порядке
            for (int i = steps.size() - 1; i >= 0; i--) {
                WorkflowStep step = steps.get(i);
                if (context.getExecutedSteps().contains(step.getStepName())) {
                    step.undo();
                }
            }
        }

        public WorkflowContext getContext() { return context; }
    }

    static class WorkflowResult {
        private final WorkflowContext context;

        public WorkflowResult(WorkflowContext context) {
            this.context = context;
        }

        public boolean isSuccess() {
            return !context.hasErrors();
        }

        public List<String> getErrors() {
            return context.getErrors();
        }

        public List<String> getExecutedSteps() {
            return context.getExecutedSteps();
        }

        public <T> T getResult(String key, Class<T> type) {
            return context.get(key, type);
        }
    }

    // Domain Objects
    static class Order {
        private final String id;
        private final List<OrderItem> items;
        private final BigDecimal total;

        public Order(String id, List<OrderItem> items) {
            this.id = id;
            this.items = items;
            this.total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public String getId() { return id; }
        public List<OrderItem> getItems() { return items; }
        public BigDecimal getTotal() { return total; }
    }

    static class OrderItem {
        private final String productId;
        private final String productName;
        private final BigDecimal price;
        private final int quantity;

        public OrderItem(String productId, String productName, BigDecimal price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public BigDecimal getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }

    public static void main(String[] args) {
        // Создаем заказ
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD1", "Laptop", BigDecimal.valueOf(1200.00), 1),
            new OrderItem("PROD2", "Mouse", BigDecimal.valueOf(25.00), 2)
        );
        Order order = new Order("ORDER123", items);

        // Создаем workflow
        WorkflowEngine engine = new WorkflowEngine();
        engine.addStep(new ValidateOrderStep());
        engine.addStep(new ProcessPaymentStep());
        engine.addStep(new SendConfirmationStep());

        // Выполняем workflow
        WorkflowResult result = engine.execute(order);

        System.out.println("\nWorkflow Result:");
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Executed steps: " + result.getExecutedSteps());

        if (!result.isSuccess()) {
            System.out.println("Errors: " + result.getErrors());
        } else {
            ProcessPaymentStep.PaymentResult payment = result.getResult("paymentResult",
                ProcessPaymentStep.PaymentResult.class);
            if (payment != null) {
                System.out.println("Payment transaction: " + payment.getTransactionId());
            }
        }

        // Демонстрация undo
        System.out.println("\nUndoing workflow...");
        engine.undo();
        System.out.println("Final executed steps: " + engine.getContext().getExecutedSteps());
    }
}
```

## Best Practices

### 1. Thread Safety

```java
public class ThreadSafeCommandExample {

    // Thread-safe Command с AtomicReference
    static class AtomicCommand implements Command {
        private final AtomicReference<CommandState> state = new AtomicReference<>(CommandState.NOT_EXECUTED);

        enum CommandState {
            NOT_EXECUTED, EXECUTING, EXECUTED, UNDOING, UNDONE
        }

        @Override
        public void execute() {
            if (state.compareAndSet(CommandState.NOT_EXECUTED, CommandState.EXECUTING)) {
                try {
                    doExecute();
                    state.set(CommandState.EXECUTED);
                } catch (Exception e) {
                    state.set(CommandState.NOT_EXECUTED);
                    throw new RuntimeException("Execution failed", e);
                }
            } else {
                throw new IllegalStateException("Command already executed or executing");
            }
        }

        @Override
        public void undo() {
            if (state.compareAndSet(CommandState.EXECUTED, CommandState.UNDOING)) {
                try {
                    doUndo();
                    state.set(CommandState.UNDONE);
                } catch (Exception e) {
                    state.set(CommandState.EXECUTED);
                    throw new RuntimeException("Undo failed", e);
                }
            }
        }

        protected void doExecute() throws Exception {
            // Реализация в подклассах
        }

        protected void doUndo() throws Exception {
            // Реализация в подклассах
        }
    }

    // Thread-safe Command Queue
    static class ConcurrentCommandQueue {
        private final BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
        private final ExecutorService executor = Executors.newFixedThreadPool(5);
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final Map<Command, Future<?>> commandFutures = new ConcurrentHashMap<>();

        public void submitCommand(Command command) {
            queue.offer(command);
        }

        public Future<?> submitCommandAsync(Command command) {
            Future<?> future = executor.submit(() -> {
                try {
                    command.execute();
                } catch (Exception e) {
                    System.err.println("Command execution failed: " + e.getMessage());
                }
            });
            commandFutures.put(command, future);
            return future;
        }

        public void startProcessing() {
            for (int i = 0; i < 5; i++) {
                executor.submit(() -> {
                    while (running.get()) {
                        try {
                            Command command = queue.poll(1, TimeUnit.SECONDS);
                            if (command != null) {
                                command.execute();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
        }

        public void cancelCommand(Command command) {
            Future<?> future = commandFutures.get(command);
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }

        public void shutdown() {
            running.set(false);
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

### 2. Testing Command Pattern

```java
@ExtendWith(MockitoExtension.class)
public class CommandPatternTest {

    @Mock
    private Receiver receiver;

    @Test
    void shouldExecuteCommandAndCallReceiver() {
        ConcreteCommand command = new ConcreteCommand(receiver);
        command.execute();

        verify(receiver).action();
    }

    @Test
    void shouldUndoCommandAndCallReceiverUndo() {
        ConcreteCommand command = new ConcreteCommand(receiver);

        command.execute();
        command.undo();

        verify(receiver).action();
        verify(receiver).undoAction();
    }

    @Test
    void shouldExecuteMacroCommandInOrder() {
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);
        Command command3 = mock(Command.class);

        MacroCommand macro = new MacroCommand("Test Macro");
        macro.addCommand(command1);
        macro.addCommand(command2);
        macro.addCommand(command3);

        macro.execute();

        InOrder inOrder = inOrder(command1, command2, command3);
        inOrder.verify(command1).execute();
        inOrder.verify(command2).execute();
        inOrder.verify(command3).execute();
    }

    @Test
    void shouldUndoMacroCommandInReverseOrder() {
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);
        Command command3 = mock(Command.class);

        MacroCommand macro = new MacroCommand("Test Macro");
        macro.addCommand(command1);
        macro.addCommand(command2);
        macro.addCommand(command3);

        macro.execute();
        macro.undo();

        InOrder inOrder = inOrder(command1, command2, command3);
        inOrder.verify(command3).undo();
        inOrder.verify(command2).undo();
        inOrder.verify(command1).undo();
    }

    @Test
    void shouldHandleCommandExecutionFailure() {
        Command failingCommand = mock(Command.class);
        doThrow(new RuntimeException("Command failed")).when(failingCommand).execute();

        CommandProcessor processor = new CommandProcessor();
        processor.addCommand(failingCommand);

        assertThrows(RuntimeException.class, () -> processor.processCommands());
    }

    @Test
    void shouldSupportCommandHistory() {
        CommandHistory history = new CommandHistory();
        UndoableCommand command1 = mock(UndoableCommand.class);
        UndoableCommand command2 = mock(UndoableCommand.class);

        history.push(command1);
        history.push(command2);

        assertTrue(history.hasCommands());

        UndoableCommand popped = history.pop();
        assertEquals(command2, popped);

        popped = history.pop();
        assertEquals(command1, popped);

        assertFalse(history.hasCommands());
    }

    @Test
    void shouldLimitCommandHistorySize() {
        BoundedCommandHistory history = new BoundedCommandHistory(2);
        UndoableCommand command1 = mock(UndoableCommand.class);
        UndoableCommand command2 = mock(UndoableCommand.class);
        UndoableCommand command3 = mock(UndoableCommand.class);

        history.push(command1);
        history.push(command2);
        history.push(command3); // command1 должен быть удален

        assertEquals(2, history.size());
        UndoableCommand popped = history.pop();
        assertEquals(command3, popped);
        popped = history.pop();
        assertEquals(command2, popped);
    }

    @Test
    void shouldExecuteAsyncCommands() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AsyncCommand asyncCommand = mock(AsyncCommand.class);
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(asyncCommand).execute();

        Future<?> future = executor.submit(() -> asyncCommand.execute());

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(future.isDone());

        executor.shutdown();
    }

    @ParameterizedTest
    @MethodSource("provideCommandTestData")
    void shouldExecuteCommandsWithDifferentParameters(Command command, String expectedResult) {
        CommandResult result = command.execute();
        assertEquals(expectedResult, result.getResult());
    }

    static Stream<Arguments> provideCommandTestData() {
        return Stream.of(
            Arguments.of(new AddCommand(2, 3), "5"),
            Arguments.of(new MultiplyCommand(4, 5), "20"),
            Arguments.of(new ConcatCommand("Hello", " World"), "Hello World")
        );
    }

    // Test doubles and utilities
    interface Receiver {
        void action();
        void undoAction();
    }

    interface UndoableCommand extends Command {
        void undo();
    }

    static class ConcreteCommand implements UndoableCommand {
        private final Receiver receiver;

        public ConcreteCommand(Receiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public void execute() {
            receiver.action();
        }

        @Override
        public void undo() {
            receiver.undoAction();
        }
    }

    interface MacroCommand extends Command {
        void addCommand(Command command);
        List<Command> getCommands();
    }

    static class MacroCommandImpl implements MacroCommand {
        private final List<Command> commands = new ArrayList<>();

        @Override
        public void addCommand(Command command) {
            commands.add(command);
        }

        @Override
        public List<Command> getCommands() {
            return new ArrayList<>(commands);
        }

        @Override
        public void execute() {
            commands.forEach(Command::execute);
        }

        @Override
        public void undo() {
            for (int i = commands.size() - 1; i >= 0; i--) {
                if (commands.get(i) instanceof UndoableCommand) {
                    ((UndoableCommand) commands.get(i)).undo();
                }
            }
        }
    }

    static class CommandProcessor {
        private final List<Command> commands = new ArrayList<>();

        public void addCommand(Command command) {
            commands.add(command);
        }

        public void processCommands() {
            commands.forEach(Command::execute);
        }
    }

    static class CommandHistory {
        private final Deque<UndoableCommand> history = new LinkedList<>();

        public void push(UndoableCommand command) {
            history.push(command);
        }

        public boolean hasCommands() {
            return !history.isEmpty();
        }

        public UndoableCommand pop() {
            return history.pop();
        }
    }

    static class BoundedCommandHistory {
        private final Deque<UndoableCommand> history = new LinkedList<>();
        private final int maxSize;

        public BoundedCommandHistory(int maxSize) {
            this.maxSize = maxSize;
        }

        public void push(UndoableCommand command) {
            history.push(command);
            if (history.size() > maxSize) {
                history.removeLast();
            }
        }

        public int size() {
            return history.size();
        }

        public UndoableCommand pop() {
            return history.isEmpty() ? null : history.pop();
        }
    }

    interface AsyncCommand extends Command {
        // Marker interface for async commands
    }

    static class CommandResult {
        private final String result;

        public CommandResult(String result) {
            this.result = result;
        }

        public String getResult() { return result; }
    }

    // Parameterized test commands
    static class AddCommand implements Command {
        private final int a, b;

        public AddCommand(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public CommandResult execute() {
            return new CommandResult(String.valueOf(a + b));
        }

        @Override
        public void undo() {
            // Not implemented for test
        }
    }

    static class MultiplyCommand implements Command {
        private final int a, b;

        public MultiplyCommand(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public CommandResult execute() {
            return new CommandResult(String.valueOf(a * b));
        }

        @Override
        public void undo() {
            // Not implemented for test
        }
    }

    static class ConcatCommand implements Command {
        private final String a, b;

        public ConcatCommand(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public CommandResult execute() {
            return new CommandResult(a + b);
        }

        @Override
        public void undo() {
            // Not implemented for test
        }
    }
}
```

## Заключение

Command паттерн — один из фундаментальных паттернов, обеспечивающий гибкость и расширяемость систем. Он позволяет инкапсулировать запросы как объекты, делая систему более модульной и поддерживающей undo/redo операции.

Ключевые преимущества:
- **Инкапсуляция**: Запросы становятся объектами
- **Расширяемость**: Легко добавлять новые команды
- **Отмена операций**: Поддержка undo/redo
- **Очереди**: Поддержка очередей команд
- **Композиция**: Возможность комбинировать команды

Используйте Command когда:
- Нужно параметризовать объекты выполняемыми операциями
- Операции нужно ставить в очередь, логировать или отменять
- Система должна поддерживать undo/redo функциональность
- Нужно отделить инициатора запроса от его исполнителя

Command часто используется вместе с:
- **Composite**: Для создания макро-команд
- **Memento**: Для сохранения состояния перед выполнением команд
- **Observer**: Для оповещения об изменении состояния команд
- **Strategy**: Команды могут использовать стратегии для выполнения
- **Template Method**: Для определения структуры выполнения команд

Главное правило: всегда проектируйте команды как immutable объекты с четко определенными контрактами execute() и undo()!