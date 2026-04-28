---
title: "Игра «Камень-ножницы-бумага» (Rock Paper Scissors Game)"
description: "Простая игра: два хода (игрок и компьютер), определение победителя по правилам камень → ножницы → бумага → камень. Реализация на Java и Kotlin с enum для ходов и метода определения победы; варианты: расширенная версия (Ящерица-Спок), статистика, стратегия."
tags:
  - algorithms
  - problems
  - rock-paper-scissors-game
type: "reference"
difficulty: "intermediate"
aliases:
  - "Игра «Камень-ножницы-бумага»"
  - "Rock Paper Scissors Game"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Игра «Камень-ножницы-бумага» (Rock Paper Scissors Game)

Простая игра: два хода (игрок и компьютер), определение победителя по правилам камень ножницы бумага камень. Реализация на Java и Kotlin с enum для ходов и метода определения победы; варианты: расширенная версия (Ящерица-Спок), статистика, стратегия.

## Полезные ссылки

### Официальная документация
- [Enum (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Enum.html)

### См. также
- [Комбинаторные задачи](combinatorial-problems-overview.md) — комбинаторика
- [Перестановки строк](../../basics/README.md) — алгоритмы со строками

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
- [Валидация банковских карт (Credit Card Validation)](credit-card-validation.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Правила игры](#правила-игры)
- [Реализация на Java](#реализация-на-java)
- [Пример использования](#пример-использования)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Расширенная версия (Ящерица-Спок)](#вариант-1-расширенная-версия-ящерица-спок)
  - [Вариант 2: Статистика игры](#вариант-2-статистика-игры)
  - [Вариант 3: Игра с стратегией](#вариант-3-игра-с-стратегией)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Классическая игра на два хода: игрок и компьютер выбирают камень, ножницы или бумагу; победитель определяется по циклу камень ножницы бумага камень. Реализация через enum упрощает валидацию и логику победы.

## Правила игры

Камень бьёт ножницы, ножницы бьют бумагу, бумага бьёт камень. При одинаковом ходе — ничья.

## Реализация на Java

Enum для ходов (значение строки для ввода + `fromString`); метод определения победы — либо явные сравнения пар, либо метод `beats(Move other)` в enum. Ход компьютера — случайный выбор из `Move.values()`; цикл ввода до «quit», подсчёт побед/поражений.

```java
// Ход в игре: камень, ножницы, бумага; значение для ввода пользователя
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

Вариант с методом `beats` в enum и `playAgainst` для результата раунда:

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

## Пример использования

Пример вывода консольной версии:

```text
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

## Реализация на Kotlin

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

Определение победителя и выбор хода компьютера — O(1). Память O(1).

## Особенности

Простая реализация; расширяемость за счёт enum (новые ходы и правила победы); удобно для интерактивной консольной игры или тестов.

## Применение

Обучение программированию, демонстрация условной логики и enum, прототипы игр, тестирование.

## Варианты задачи

### Вариант 1: Расширенная версия (Ящерица-Спок)

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

Подходит для учебных примеров (условная логика, enum), прототипов игр и демонстрации парсинга ввода с валидацией.

## Лучшие практики

Храните ходы в enum, логику победы — в методе enum или в матрице; избегайте сравнений по строкам. Ввод парсить через `Move.fromString`, при ошибке — сообщение и повторный ввод. В тестах использовать фиксированный источник случайности (заглушка Random). При расширении (например, Ящерица-Спок) обновлять правила победы в одном месте. Покрыть тестами все пары ходов, ничью и невалидный ввод.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| IllegalArgumentException при вводе | Строка не совпадает с enum | Использовать fromString с ignoreCase; обрабатывать исключение и запрашивать ввод снова |
| Непредсказуемый результат в тестах | Используется реальный Random | Внедрять интерфейс генератора или Random с фиксированным seed в тестах |
| При добавлении хода забыли обновить правила | Новая константа в enum без beats | Добавить ветку в beats/матрицу для каждого нового хода |

## Частые вопросы

**Зачем enum вместо строк?** Enum даёт валидацию на этапе компиляции и единое место для правил победы; строки требуют сравнений и легче ошибиться.

**Как тестировать игру со случайным ходом компьютера?** Передавать в класс игры заглушку `Random` с фиксированным seed или интерфейс «генератор хода»; в тесте проверять результат при заданных ходах игрока и «компьютера».

**Как добавить Ящерицу и Спока?** Ввести константы в enum и задать для каждого множество ходов, которых он бьёт (например, через Map или переопределение beats); обновить fromString и вывод под новые значения.

## Заключение

Игра «Камень-ножницы-бумага» на Java/Kotlin с enum — компактный пример условной логики и валидации ввода; легко расширяется до расширенных правил и учёта статистики.
