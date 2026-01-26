# Rock Paper Scissors Game

Кратко: создание простой игры "Камень-ножницы-бумага" на Java. Рассматривается реализация с использованием enum для ходов и логики определения победителя.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Enum Documentation](https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html)

### См. также
- `./combinatorial-problems-overview.md` - комбинаторные задачи
- `../strings/string-permutations.md` - перестановки строк

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Правила игры](#правила-игры)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Пример использования](#пример-использования)

## Описание алгоритма

В этом коротком уроке мы увидим, как создать простую игру «Камень-ножницы-бумага» на Java.

Наша игра позволит игрокам вводить «камень», «бумагу» или «ножницы» в качестве значения каждого хода.

## Правила игры

Правила игры просты:

- **Камень** побеждает **Ножницы**
- **Ножницы** побеждают **Бумагу**
- **Бумага** побеждает **Камень**
- Если оба игрока выбрали одинаковый ход, это ничья

## Java Implementation

### Реализация

Во-первых, давайте создадим перечисление для ходов:

```java
public enum Move {
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors");
    
    private String value;
    
    Move(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Move fromString(String value) {
        for (Move move : Move.values()) {
            if (move.value.equalsIgnoreCase(value)) {
                return move;
            }
        }
        throw new IllegalArgumentException("Invalid move: " + value);
    }
}
```

Затем давайте создадим метод, который генерирует случайные целые числа и возвращает ход компьютера:

```java
import java.util.Random;

public class RockPaperScissors {
    private static Random random = new Random();
    
    private static String getComputerMove() {
        int randomNumber = random.nextInt(3);
        String computerMove = Move.values()[randomNumber].getValue();
        System.out.println("Computer move: " + computerMove);
        return computerMove;
    }
}
```

И метод, который проверяет, выиграл ли игрок:

```java
private static boolean isPlayerWin(String playerMove, String computerMove) {
    return (playerMove.equals(Move.ROCK.getValue()) && 
            computerMove.equals(Move.SCISSORS.getValue()))
        || (playerMove.equals(Move.SCISSORS.getValue()) && 
            computerMove.equals(Move.PAPER.getValue()))
        || (playerMove.equals(Move.PAPER.getValue()) && 
            computerMove.equals(Move.ROCK.getValue()));
}
```

### Улучшенная версия с enum

Более элегантное решение с использованием enum:

```java
public enum Move {
    ROCK("rock") {
        @Override
        public boolean beats(Move other) {
            return other == SCISSORS;
        }
    },
    PAPER("paper") {
        @Override
        public boolean beats(Move other) {
            return other == ROCK;
        }
    },
    SCISSORS("scissors") {
        @Override
        public boolean beats(Move other) {
            return other == PAPER;
        }
    };
    
    private String value;
    
    Move(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public abstract boolean beats(Move other);
    
    public GameResult playAgainst(Move other) {
        if (this == other) {
            return GameResult.TIE;
        }
        return this.beats(other) ? GameResult.WIN : GameResult.LOSE;
    }
}

public enum GameResult {
    WIN, LOSE, TIE
}
```

Наконец, мы будем использовать их для формирования полной программы:

```java
import java.util.Arrays;
import java.util.Scanner;

public class RockPaperScissors {
    private static Random random = new Random();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int wins = 0;
        int losses = 0;
        
        System.out.println("Welcome to Rock-Paper-Scissors! " +
            "Please enter \"rock\", \"paper\", \"scissors\", or \"quit\" to exit.");
        
        while (true) {
            System.out.println("-------------------------");
            System.out.print("Enter your move: ");
            String playerMove = scanner.nextLine();
            
            if (playerMove.equals("quit")) {
                System.out.println("You won " + wins + " times and lost " + 
                    losses + " times.");
                System.out.println("Thanks for playing! See you again.");
                break;
            }
            
            if (Arrays.stream(Move.values())
                .noneMatch(x -> x.getValue().equals(playerMove))) {
                System.out.println("Your move isn't valid!");
                continue;
            }
            
            String computerMove = getComputerMove();
            
            if (playerMove.equals(computerMove)) {
                System.out.println("It's a tie!");
            } else if (isPlayerWin(playerMove, computerMove)) {
                System.out.println("You won!");
                wins++;
            } else {
                System.out.println("You lost!");
                losses++;
            }
        }
        
        scanner.close();
    }
    
    private static String getComputerMove() {
        int randomNumber = random.nextInt(3);
        String computerMove = Move.values()[randomNumber].getValue();
        System.out.println("Computer move: " + computerMove);
        return computerMove;
    }
    
    private static boolean isPlayerWin(String playerMove, String computerMove) {
        return (playerMove.equals(Move.ROCK.getValue()) && 
                computerMove.equals(Move.SCISSORS.getValue()))
            || (playerMove.equals(Move.SCISSORS.getValue()) && 
                computerMove.equals(Move.PAPER.getValue()))
            || (playerMove.equals(Move.PAPER.getValue()) && 
                computerMove.equals(Move.ROCK.getValue()));
    }
}
```

Как видно выше, мы используем Java Scanner для чтения введенного пользователем значения.

## Пример использования

Давайте немного поиграем и посмотрим на результат:

```
Welcome to Rock-Paper-Scissors! Please enter "rock", "paper", "scissors", or "quit" to exit.
-------------------------
Enter your move: rock
Computer move: scissors
You won!
-------------------------
Enter your move: paper
Computer move: paper
It's a tie!
-------------------------
Enter your move: quit
You won 1 times and lost 0 times.
Thanks for playing! See you again.
```

## Kotlin Implementation

### Enum для ходов

```kotlin
enum class MoveK(val value: String) {
    ROCK("rock") {
        override fun beats(other: MoveK): Boolean = other == SCISSORS
    },
    PAPER("paper") {
        override fun beats(other: MoveK): Boolean = other == ROCK
    },
    SCISSORS("scissors") {
        override fun beats(other: MoveK): Boolean = other == PAPER
    };
    
    abstract fun beats(other: MoveK): Boolean
    
    fun playAgainst(other: MoveK): GameResultK {
        return when {
            this == other -> GameResultK.TIE
            this.beats(other) -> GameResultK.WIN
            else -> GameResultK.LOSE
        }
    }
    
    companion object {
        fun fromString(value: String): MoveK {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid move: $value")
        }
    }
}

enum class GameResultK {
    WIN, LOSE, TIE
}
```

### Класс игры

```kotlin
import kotlin.random.Random

class RockPaperScissorsK {
    private val random = Random
    
    private fun getComputerMove(): MoveK {
        val randomNumber = random.nextInt(3)
        val computerMove = MoveK.values()[randomNumber]
        println("Computer move: ${computerMove.value}")
        return computerMove
    }
    
    fun play(playerMoveString: String): GameResultK {
        val playerMove = MoveK.fromString(playerMoveString)
        val computerMove = getComputerMove()
        return playerMove.playAgainst(computerMove)
    }
}
```

### Пример использования

```kotlin
fun main() {
    val game = RockPaperScissorsK()
    
    val result1 = game.play("rock")
    println("Result: $result1")
    
    val result2 = game.play("paper")
    println("Result: $result2")
    
    val result3 = game.play("scissors")
    println("Result: $result3")
}
```

## Сложность

### Временная сложность

- **Определение победителя:** O(1) - константное время
- **Генерация хода компьютера:** O(1) - случайный выбор

### Пространственная сложность

- **Хранение ходов:** O(1) - только константная память

## Особенности

- **Простота:** Очень простая реализация
- **Расширяемость:** Легко добавить новые ходы
- **Интерактивность:** Поддерживает интерактивную игру

## Применение

Игра "Камень-ножницы-бумага" используется в:

- Обучении программированию
- Демонстрации условной логики
- Игровых приложениях
- Тестировании алгоритмов

## Варианты задачи

### Вариант 1: Расширенная версия (Камень-Ножницы-Бумага-Ящерица-Спок)

```java
public enum ExtendedMove {
    ROCK, PAPER, SCISSORS, LIZARD, SPOCK;
    
    private static final Map<ExtendedMove, Set<ExtendedMove>> BEATS = Map.of(
        ROCK, Set.of(SCISSORS, LIZARD),
        PAPER, Set.of(ROCK, SPOCK),
        SCISSORS, Set.of(PAPER, LIZARD),
        LIZARD, Set.of(PAPER, SPOCK),
        SPOCK, Set.of(ROCK, SCISSORS)
    );
    
    public GameResult playAgainst(ExtendedMove other) {
        if (this == other) {
            return GameResult.TIE;
        }
        return BEATS.get(this).contains(other) ? 
            GameResult.WIN : GameResult.LOSE;
    }
}
```

### Вариант 2: Статистика игры

```java
public class GameStatistics {
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;
    private Map<Move, Integer> moveFrequency = new HashMap<>();
    
    public void recordGame(Move playerMove, GameResult result) {
        moveFrequency.put(playerMove, 
            moveFrequency.getOrDefault(playerMove, 0) + 1);
        
        switch (result) {
            case WIN: wins++; break;
            case LOSE: losses++; break;
            case TIE: ties++; break;
        }
    }
    
    public void printStatistics() {
        System.out.println("Wins: " + wins);
        System.out.println("Losses: " + losses);
        System.out.println("Ties: " + ties);
        System.out.println("Move frequency: " + moveFrequency);
    }
}
```

### Вариант 3: Игра с стратегией

```java
public class StrategicPlayer {
    private Move lastPlayerMove;
    private Move lastComputerMove;
    
    public Move getNextMove() {
        // Стратегия: выбирать ход, который побеждает последний ход игрока
        if (lastPlayerMove != null) {
            return getCounterMove(lastPlayerMove);
        }
        return Move.values()[random.nextInt(3)];
    }
    
    private Move getCounterMove(Move move) {
        // Возвращает ход, который побеждает данный ход
        switch (move) {
            case ROCK: return Move.PAPER;
            case PAPER: return Move.SCISSORS;
            case SCISSORS: return Move.ROCK;
            default: return Move.ROCK;
        }
    }
}
```

## Когда использовать

### Используйте эту реализацию, когда:

- Нужна простая игра для обучения
- Демонстрируете условную логику
- Создаете прототип игры

## Заключение

В этом кратком руководстве мы узнали, как создать простую игру «Камень-ножницы-бумага» на Java. Реализация проста и может быть легко расширена для более сложных вариантов игры.
