---
title: "Умножение матриц (Matrix Multiplication)"
description: "Умножение двух матриц в Java: собственная реализация и библиотеки EJML, ND4J, Apache Commons Math3, LA4J, Colt. Сравнение производительности для малых и больших матриц."
tags:
  - algorithms
  - math
  - matrix-multiplication
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Умножение матриц (`Matrix Multiplication`)

Умножение двух матриц в Java: собственная реализация и библиотеки EJML, ND4J, Apache Commons Math3, LA4J, Colt. Сравнение производительности для малых и больших матриц.

## Полезные ссылки

### Официальная документация
- [EJML](https://ejml.org/wiki/) — Efficient Java Matrix Library
- [ND4J](https://javadoc.io/doc/org.nd4j/nd4j-api/latest/index.html) — численные вычисления
- [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/index.html)
- [LA4J](https://github.com/vkostyukov/la4j) — Linear Algebra for Java
- [Colt](https://dst.lbl.gov/ACSSoftware/colt/) — библиотека Colt

### См. также
- [[distance-between-points|Вычисление расстояния]] — расстояние между точками
- [[circle-area-calculation|Вычисление площади круга]] — площадь круга

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Реализация на Java](#реализация-на-java)
- [Сравнение производительности](#сравнение-производительности)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

В Java нет встроенного типа «матрица»; используют двумерные массивы или библиотеки. Ниже — своя реализация и примеры с EJML, ND4J, Apache Commons Math3, LA4J, Colt, плюс сравнение по скорости.

Формула: `C[r][c] = Σ(A[r][i] * B[i][c])`. Число столбцов A должно совпадать с числом строк B. Пример: A 3×2, B 2×4 результат 3×4.

## Реализация на Java

### Собственная реализация

Двумерные массивы `double[][]`, тройной цикл по строкам результата, столбцам и внутренней сумме.

```java
// Пример матриц 3×2 и 2×4
double[][] firstMatrix = {
    new double[]{1d, 5d},
    new double[]{2d, 3d},
    new double[]{1d, 7d}
};

double[][] secondMatrix = {
    new double[]{1d, 2d, 3d, 7d},
    new double[]{5d, 2d, 8d, 1d}
};
double[][] expected = {
    new double[]{26d, 12d, 43d, 12d},
    new double[]{17d, 10d, 30d, 17d},
    new double[]{36d, 16d, 59d, 14d}
};

// Результат: firstMatrix.length × secondMatrix[0].length
double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
    double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

    for (int row = 0; row < result.length; row++) {
        for (int col = 0; col < result[row].length; col++) {
            result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
        }
    }

    return result;
}

// Одна ячейка: скалярное произведение row-й строки A и col-го столбца B
double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
    double cell = 0;

    for (int i = 0; i < secondMatrix.length; i++) {
        cell += firstMatrix[row][i] * secondMatrix[i][col];
    }

    return cell;
}

double[][] actual = multiplyMatrices(firstMatrix, secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### Библиотеки для умножения матриц

#### EJML

EJML (Efficient Java Matrix Library) — быстрая и обновляемая библиотека для матриц в Java. Зависимость в `pom.xml`:

```xml
<dependency>
    <groupId>org.ejml</groupId>
    <artifactId>ejml-all</artifactId>
    <version>0.38</version>
</dependency>
```

Создание матриц через `SimpleMatrix`, умножение — метод `mult()`.

```java
import org.ejml.simple.SimpleMatrix;

SimpleMatrix firstMatrix = new SimpleMatrix(
    new double[][] {
        new double[] {1d, 5d},
        new double[] {2d, 3d},
        new double[] {1d, 7d}
    }
);

SimpleMatrix secondMatrix = new SimpleMatrix(
    new double[][] {
        new double[] {1d, 2d, 3d, 7d},
        new double[] {5d, 2d, 8d, 1d}
    }
);

SimpleMatrix expected = new SimpleMatrix(
    new double[][] {
        new double[] {26d, 12d, 43d, 12d},
        new double[] {17d, 10d, 30d, 17d},
        new double[] {36d, 16d, 59d, 14d}
    }
);

SimpleMatrix actual = firstMatrix.mult(secondMatrix);
// Сравнение через isIdentical(expected, tolerance), т.к. equals не переопределён
assertThat(actual).matches(m -> m.isIdentical(expected, 0d));
```

### ND4J

ND4J — библиотека из экосистемы Deeplearning4j, хорошо подходит для больших матриц и GPU. Зависимость: `nd4j-native` (в примере использована бета-версия). Матрица создаётся через `Nd4j.create(double[][])`, умножение — `firstMatrix.mmul(secondMatrix)`.

```java
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

INDArray matrix = Nd4j.create(/* двумерный double[][] */);
INDArray actual = firstMatrix.mmul(secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### Apache Commons Math3

Commons Math3: зависимость `commons-math3`. Матрица — `RealMatrix` / `Array2DRowRealMatrix(double[][])`, умножение — `firstMatrix.multiply(secondMatrix)`.

```java
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

RealMatrix matrix = new Array2DRowRealMatrix(/* double[][] */);
RealMatrix actual = firstMatrix.multiply(secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### LA4J

LA4J (Linear Algebra for Java): зависимость `la4j`. `Matrix` / `Basic2DMatrix(double[][])`, умножение — `multiply(secondMatrix)`.

```java
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

Matrix matrix = new Basic2DMatrix(/* double[][] */);
Matrix actual = firstMatrix.multiply(secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### Colt

Colt (CERN): зависимость `colt`. Фабрика `DoubleFactory2D.dense`, метод `make(double[][])`; умножение через класс `Algebra` и метод `mult(firstMatrix, secondMatrix)`.

```java
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

DoubleFactory2D doubleFactory2D = DoubleFactory2D.dense;
DoubleMatrix2D matrix = doubleFactory2D.make(/* double[][] */);
Algebra algebra = new Algebra();
DoubleMatrix2D actual = algebra.mult(firstMatrix, secondMatrix);
assertThat(actual).isEqualTo(expected);
```

## Сравнение производительности

Теперь, когда мы закончили изучение различных возможностей умножения матриц, давайте проверим, какие из них наиболее эффективны.

### Малые матрицы (3×2 и 2×4)

Бенчмарк на JMH (режим AverageTime, микросекунды):

```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(MatrixMultiplicationBenchmarking.class.getSimpleName())
        .mode(Mode.AverageTime)
        .forks(2)
        .warmupIterations(5)
        .measurementIterations(10)
        .timeUnit(TimeUnit.MICROSECONDS)
        .build();

    new Runner(opt).run();
}
```

```java
// Состояние бенчмарка: две матрицы 3×2 и 2×4
@State(Scope.Benchmark)
public class MatrixProvider {
    private double[][] firstMatrix;
    private double[][] secondMatrix;

    public MatrixProvider() {
        firstMatrix = new double[][] {
            new double[] {1d, 5d},
            new double[] {2d, 3d},
            new double[] {1d, 7d}
        };

        secondMatrix = new double[][] {
            new double[] {1d, 2d, 3d, 7d},
            new double[] {5d, 2d, 8d, 1d}
        };
    }

    public double[][] getFirstMatrix() {
        return firstMatrix;
    }

    public double[][] getSecondMatrix() {
        return secondMatrix;
    }
}
```

Результаты (микросекунды): Colt и EJML быстрее всего; ND4J на малых матрицах медленнее из-за накладных расходов. При увеличении warmup итераций все ускоряются.

| Библиотека | Время (мкс) |
|------------|-------------|
| Colt | 0.219 ± 0.014 |
| EJML | 0.226 ± 0.013 |
| Homemade | 0.389 ± 0.045 |
| LA4J | 0.427 ± 0.016 |
| Apache Commons | 1.008 ± 0.032 |
| ND4J | 12.670 ± 2.582 |

### Большие матрицы (3000×3000)

Класс состояния с матрицами 3000×3000 (случайные значения):

```java
@State(Scope.Benchmark)
public class BigMatrixProvider {
    private double[][] firstMatrix;
    private double[][] secondMatrix;

    public BigMatrixProvider() {}

    @Setup
    public void setup(BenchmarkParams parameters) {
        firstMatrix = createMatrix();
        secondMatrix = createMatrix();
    }

    private double[][] createMatrix() {
        Random random = new Random();
        double[][] result = new double[3000][3000];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = random.nextDouble();
            }
        }

        return result;
    }

    public double[][] getFirstMatrix() {
        return firstMatrix;
    }

    public double[][] getSecondMatrix() {
        return secondMatrix;
    }
}
```

```java
// Бенчмарки для больших матриц (время в секундах)
public class BigMatrixMultiplicationBenchmarking {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(BigMatrixMultiplicationBenchmarking.class.getSimpleName())
            .mode(Mode.AverageTime)
            .forks(2)
            .warmupIterations(10)
            .measurementIterations(10)
            .timeUnit(TimeUnit.SECONDS)
            .build();

        new Runner(opt).run();
    }

    @Benchmark
    public Object homemadeMatrixMultiplication(BigMatrixProvider matrixProvider) {
        return HomemadeMatrix.multiplyMatrices(
            matrixProvider.getFirstMatrix(),
            matrixProvider.getSecondMatrix()
        );
    }

    @Benchmark
    public Object ejmlMatrixMultiplication(BigMatrixProvider matrixProvider) {
        SimpleMatrix firstMatrix = new SimpleMatrix(matrixProvider.getFirstMatrix());
        SimpleMatrix secondMatrix = new SimpleMatrix(matrixProvider.getSecondMatrix());
        return firstMatrix.mult(secondMatrix);
    }

    @Benchmark
    public Object apacheCommonsMatrixMultiplication(BigMatrixProvider matrixProvider) {
        RealMatrix firstMatrix = new Array2DRowRealMatrix(matrixProvider.getFirstMatrix());
        RealMatrix secondMatrix = new Array2DRowRealMatrix(matrixProvider.getSecondMatrix());
        return firstMatrix.multiply(secondMatrix);
    }

    @Benchmark
    public Object la4jMatrixMultiplication(BigMatrixProvider matrixProvider) {
        Matrix firstMatrix = new Basic2DMatrix(matrixProvider.getFirstMatrix());
        Matrix secondMatrix = new Basic2DMatrix(matrixProvider.getSecondMatrix());
        return firstMatrix.multiply(secondMatrix);
    }

    @Benchmark
    public Object nd4jMatrixMultiplication(BigMatrixProvider matrixProvider) {
        INDArray firstMatrix = Nd4j.create(matrixProvider.getFirstMatrix());
        INDArray secondMatrix = Nd4j.create(matrixProvider.getSecondMatrix());
        return firstMatrix.mmul(secondMatrix);
    }

    @Benchmark
    public Object coltMatrixMultiplication(BigMatrixProvider matrixProvider) {
        DoubleFactory2D doubleFactory2D = DoubleFactory2D.dense;
        DoubleMatrix2D firstMatrix = doubleFactory2D.make(matrixProvider.getFirstMatrix());
        DoubleMatrix2D secondMatrix = doubleFactory2D.make(matrixProvider.getSecondMatrix());
        Algebra algebra = new Algebra();
        return algebra.mult(firstMatrix, secondMatrix);
    }
}
```

На больших матрицах лидер — ND4J (доли секунды); EJML и LA4J — десятки секунд; Colt и собственная реализация — минуты. Вывод: победитель зависит от размера матриц.

| Библиотека | Время (с) |
|------------|-----------|
| ND4J | 0.548 ± 0.006 |
| EJML | 25.830 ± 0.059 |
| LA4J | 35.523 ± 0.102 |
| Colt | 197.914 ± 2.453 |
| Homemade | 497.493 ± 2.121 |
| Apache Commons | 511.140 ± 13.535 |

## Сложность

Наивное умножение: время O(n³), память O(n²) для результата. Библиотеки могут использовать алгоритм Штрассена (O(n^2.807)) и буферы.

## Особенности

Производительность сильно зависит от размера; библиотеки используют кэширование, векторизацию и параллелизм. Точности `double` обычно достаточно.

## Применение

Машинное обучение, графика, научные расчёты, обработка сигналов, линейная алгебра, криптография.

## Когда использовать

Собственная реализация — для маленьких матриц и минимума зависимостей. EJML — хороший баланс для средних матриц. ND4J — для очень больших и при использовании GPU. Apache Commons Math3 — при необходимости остального набора функций и совместимости с экосистемой Apache.

## Лучшие практики

Перед умножением проверяйте совпадение размеров (столбцы A = строки B); при несовпадении — явное исключение. Для матриц до ~100×100 достаточно тройного цикла; для больших — EJML, ND4J или Commons Math с учётом нативного кода и GPU. Результат A(m×n)×B(n×p) имеет размер m×p; не создавайте лишних копий. При длинных цепочках учитывайте накопление ошибок округления; при необходимости — BigDecimal или специализированные библиотеки. Повторяющиеся произведения кэшируйте при возможности.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Исключение при умножении | Несовпадение размеров (столбцы A ≠ строки B) | Проверять размеры до вызова; выбрасывать понятное исключение |
| Медленная работа на больших матрицах | Наивный O(n³) без оптимизаций | Использовать ND4J или EJML; включить нативный бэкенд/GPU при необходимости |
| Различия в последних знаках | Ошибки округления float/double | Сравнивать с допуском; для критичных расчётов — BigDecimal или специализированные библиотеки |

## Частые вопросы

**Какую библиотеку выбрать для продакшена?** Для маленьких матриц — своя реализация или EJML. Для больших и ML — ND4J. Если уже используете Apache stack — Commons Math3.

**Почему ND4J быстрее на больших матрицах?** Использует нативный код, векторизацию (SIMD) и возможность задействовать GPU; на малых матрицах накладные расходы съедают выигрыш.

**Нужно ли проверять размеры перед умножением?** Да: число столбцов первой матрицы должно равняться числу строк второй, иначе результат не определён; в коде лучше явная проверка и исключение.

## Заключение

В документе рассмотрены умножение матриц в Java: собственная реализация и библиотеки EJML, ND4J, Apache Commons Math3, LA4J, Colt. Выбор зависит от размера матриц и требований к производительности.
