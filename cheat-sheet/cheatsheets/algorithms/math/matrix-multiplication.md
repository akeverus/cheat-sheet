# Matrix Multiplication

Кратко: умножение двух матриц в Java. Рассматривается собственная реализация и использование различных библиотек (EJML, ND4J, Apache Commons Math3, LA4J, Colt), а также сравнительный анализ производительности.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [EJML Documentation](https://ejml.org/)
- [ND4J Documentation](https://deeplearning4j.konduit.ai/nd4j)
- [Apache Commons Math3 Documentation](https://commons.apache.org/proper/commons-math/)
- [LA4J Documentation](http://la4j.org/)
- [Colt Documentation](https://dst.lbl.gov/ACSSoftware/colt/)

### См. также
- `./distance-between-points.md` - вычисление расстояния
- `./circle-area-calculation.md` - вычисление площади круга

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение производительности](#сравнение-производительности)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы рассмотрим, как мы можем перемножить две матрицы в Java.

Поскольку в языке изначально не существует понятия матрицы, мы реализуем его сами, а также поработаем с несколькими библиотеками, чтобы посмотреть, как они обрабатывают умножение матриц.

В конце мы проведем небольшой сравнительный анализ различных решений, которые мы исследовали, чтобы определить самое быстрое из них.

### Математическая формулировка

Умножение матриц выполняется по формуле:

```
C[r][c] = Σ(A[r][i] * B[i][c])
```

где:
- r - количество строк матрицы A
- c - количество столбцов матрицы B
- n - количество столбцов матрицы A (должно совпадать с количеством строк матрицы B)

### Пример

Матрица A (3×2):
```
[1, 5]
[2, 3]
[1, 7]
```

Матрица B (2×4):
```
[1, 2, 3, 7]
[5, 2, 8, 1]
```

Результат A × B (3×4):
```
[26, 12, 43, 12]
[17, 10, 30, 17]
[36, 16, 59, 14]
```

## Java Implementation

### Собственная реализация

Мы не будем усложнять и будем использовать двумерные двойные массивы:

```java
double[][] firstMatrix = {
    new double[]{1d, 5d},
    new double[]{2d, 3d},
    new double[]{1d, 7d}
};

double[][] secondMatrix = {
    new double[]{1d, 2d, 3d, 7d},
    new double[]{5d, 2d, 8d, 1d}
};
```

Это две матрицы нашего примера. Создадим ожидаемый в результате их умножения:

```java
double[][] expected = {
    new double[]{26d, 12d, 43d, 12d},
    new double[]{17d, 10d, 30d, 17d},
    new double[]{36d, 16d, 59d, 14d}
};
```

Теперь, когда все настроено, давайте реализуем алгоритм умножения. Сначала мы создадим пустой массив результатов и пройдемся по его ячейкам, чтобы сохранить ожидаемое значение в каждой из них:

```java
double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
    double[][] result = new double[firstMatrix.length][secondMatrix[0].length];
    
    for (int row = 0; row < result.length; row++) {
        for (int col = 0; col < result[row].length; col++) {
            result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
        }
    }
    
    return result;
}
```

Наконец, давайте реализуем вычисление одной ячейки. Чтобы добиться этого, мы будем использовать формулу, показанную ранее:

```java
double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
    double cell = 0;
    
    for (int i = 0; i < secondMatrix.length; i++) {
        cell += firstMatrix[row][i] * secondMatrix[i][col];
    }
    
    return cell;
}
```

Наконец, давайте проверим, что результат алгоритма соответствует нашему ожидаемому результату:

```java
double[][] actual = multiplyMatrices(firstMatrix, secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### Библиотеки для умножения матриц

#### EJML

Первая библиотека, которую мы рассмотрим, - это EJML, что означает Efficient Java Matrix Library. На момент написания этого руководства это была одна из самых последних обновленных библиотек матриц Java. Его цель - быть максимально эффективным в отношении вычислений и использования памяти.

Нам нужно будет добавить зависимость к библиотеке в нашем pom.xml:

```xml
<dependency>
    <groupId>org.ejml</groupId>
    <artifactId>ejml-all</artifactId>
    <version>0.38</version>
</dependency>
```

Мы будем использовать почти тот же шаблон, что и раньше: создадим две матрицы в соответствии с нашим примером и проверим, что результат их умножения совпадает с тем, который мы вычислили ранее.

Итак, давайте создадим наши матрицы с помощью EJML. Для этого мы будем использовать класс SimpleMatrix, предлагаемый библиотекой.

Он может принимать двумерный двойной массив в качестве входных данных для своего конструктора:

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
```

А теперь давайте определим нашу ожидаемую матрицу для умножения:

```java
SimpleMatrix expected = new SimpleMatrix(
    new double[][] {
        new double[] {26d, 12d, 43d, 12d},
        new double[] {17d, 10d, 30d, 17d},
        new double[] {36d, 16d, 59d, 14d}
    }
);
```

Теперь, когда мы все настроили, давайте посмотрим, как перемножить две матрицы вместе. Класс SimpleMatrix предлагает метод mult(), принимающий в качестве параметра еще одну SimpleMatrix и возвращающий произведение двух матриц:

```java
SimpleMatrix actual = firstMatrix.mult(secondMatrix);
```

Проверим, соответствует ли полученный результат ожидаемому.

Поскольку SimpleMatrix не переопределяет метод equals(), мы не можем полагаться на него при выполнении проверки. Но он предлагает альтернативу: метод isIdentical(), который принимает не только еще один матричный параметр, но и двойную отказоустойчивость, чтобы игнорировать небольшие различия из-за двойной точности:

```java
assertThat(actual).matches(m -> m.isIdentical(expected, 0d));
```

### ND4J

Давайте теперь попробуем библиотеку ND4J. ND4J - это вычислительная библиотека, являющаяся частью проекта deeplearning4j. Помимо прочего, ND4J предлагает функции вычисления матриц.

Прежде всего, мы должны получить зависимость от библиотеки:

```xml
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native</artifactId>
    <version>1.0.0-beta4</version>
</dependency>
```

Обратите внимание, что мы используем здесь бета-версию, потому что, похоже, в выпуске GA есть некоторые ошибки.

Для краткости мы не будем переписывать двухмерные двойные массивы, а просто сосредоточимся на том, как они используются с каждой библиотекой. Таким образом, с ND4J мы должны создать INDArray. Для этого мы вызовем фабричный метод Nd4j.create() и передадим ему двойной массив, представляющий нашу матрицу:

```java
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

INDArray matrix = Nd4j.create(/* a two dimensions double array */);
```

Как и в предыдущем разделе, мы создадим три матрицы: две мы собираемся перемножить вместе, а одна - ожидаемый результат.

После этого мы хотим выполнить умножение между первыми двумя матрицами, используя метод INDArray.mmul():

```java
INDArray actual = firstMatrix.mmul(secondMatrix);
```

Затем мы снова проверяем, соответствует ли фактический результат ожидаемому. На этот раз мы можем положиться на проверку на равенство:

```java
assertThat(actual).isEqualTo(expected);
```

### Apache Commons Math3

Давайте теперь поговорим о модуле Apache Commons Math3, который предоставляет нам математические вычисления, включая манипуляции с матрицами.

Опять же, нам нужно будет указать зависимость в нашем pom.xml:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

После настройки мы можем использовать интерфейс RealMatrix и его реализацию Array2DRowRealMatrix для создания наших обычных матриц. Конструктор класса реализации принимает в качестве параметра двумерный массив типа double:

```java
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

RealMatrix matrix = new Array2DRowRealMatrix(/* a two dimensions double array */);
```

Что касается умножения матриц, интерфейс RealMatrix предлагает метод multiply(), принимающий другой параметр RealMatrix:

```java
RealMatrix actual = firstMatrix.multiply(secondMatrix);
```

Наконец, мы можем убедиться, что результат равен ожидаемому:

```java
assertThat(actual).isEqualTo(expected);
```

### LA4J

Он называется LA4J, что означает Linear Algebra for Java.

Давайте также добавим зависимость для этого:

```xml
<dependency>
    <groupId>org.la4j</groupId>
    <artifactId>la4j</artifactId>
    <version>0.6.0</version>
</dependency>
```

Теперь LA4J работает почти так же, как и другие библиотеки. Он предлагает интерфейс Matrix с реализацией Basic2DMatrix, которая принимает двумерный двойной массив в качестве входных данных:

```java
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

Matrix matrix = new Basic2DMatrix(/* a two dimensions double array */);
```

Как и в модуле Apache Commons Math3, метод умножения - multiply() и принимает другую матрицу в качестве параметра:

```java
Matrix actual = firstMatrix.multiply(secondMatrix);
```

Еще раз можем проверить, соответствует ли результат нашим ожиданиям:

```java
assertThat(actual).isEqualTo(expected);
```

### Colt

Давайте теперь посмотрим на нашу последнюю библиотеку: Colt.

Colt - это библиотека, разработанная CERN. Он предоставляет функции, обеспечивающие высокую производительность научных и технических вычислений.

Как и в случае с предыдущими библиотеками, мы должны получить правильную зависимость:

```xml
<dependency>
    <groupId>colt</groupId>
    <artifactId>colt</artifactId>
    <version>1.2.0</version>
</dependency>
```

Чтобы создавать матрицы с помощью Colt, мы должны использовать класс DoubleFactory2D. Он поставляется с тремя фабричными экземплярами: плотным, разреженным и rowCompressed. Каждый из них оптимизирован для создания соответствующей матрицы.

Для нашей цели мы будем использовать плотный экземпляр. На этот раз вызывается метод make(), и он снова принимает двумерный массив double, создавая объект DoubleMatrix2D:

```java
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

DoubleFactory2D doubleFactory2D = DoubleFactory2D.dense;
DoubleMatrix2D matrix = doubleFactory2D.make(/* a two dimensions double array */);
```

После создания экземпляров наших матриц мы хотим их умножить. На этот раз для матричного объекта нет метода для этого. Нам нужно создать экземпляр класса Algebra, который имеет метод mult(), принимающий две матрицы в качестве параметров:

```java
Algebra algebra = new Algebra();
DoubleMatrix2D actual = algebra.mult(firstMatrix, secondMatrix);
```

Затем мы можем сравнить фактический результат с ожидаемым:

```java
assertThat(actual).isEqualTo(expected);
```

## Сравнение производительности

Теперь, когда мы закончили изучение различных возможностей умножения матриц, давайте проверим, какие из них наиболее эффективны.

### Малые матрицы (3×2 и 2×4)

Для реализации теста производительности воспользуемся бенчмаркинговой библиотекой JMH. Давайте настроим класс бенчмаркинга со следующими параметрами:

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

Затем нам нужно создать объект состояния, содержащий наши массивы:

```java
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

Наконец, мы запустим процесс бенчмаркинга, используя наш основной метод. Это дает нам следующий результат:

| Библиотека | Время (микросекунды) |
|------------|---------------------|
| Colt | 0.219 ± 0.014 |
| EJML | 0.226 ± 0.013 |
| Homemade | 0.389 ± 0.045 |
| LA4J | 0.427 ± 0.016 |
| Apache Commons | 1.008 ± 0.032 |
| ND4J | 12.670 ± 2.582 |

Как мы видим, EJML и Colt действительно хорошо работают с примерно одной пятой микросекунды на операцию, тогда как ND4j менее производительна с чуть более чем десятью микросекундами на операцию. В других библиотеках результаты проходят между ними.

Также стоит отметить, что при увеличении количества итераций прогрева с 5 до 10 производительность увеличивается для всех библиотек.

### Большие матрицы (3000×3000)

А что произойдет, если мы возьмем матрицы большего размера, например 3000×3000? Чтобы проверить, что происходит, давайте сначала создадим еще один класс состояния, предоставляющий сгенерированные матрицы такого размера:

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

Давайте теперь создадим класс бенчмаркинга:

```java
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

Когда мы запускаем этот бенчмаркинг, мы получаем совершенно другие результаты:

| Библиотека | Время (секунды) |
|------------|----------------|
| ND4J | 0.548 ± 0.006 |
| EJML | 25.830 ± 0.059 |
| LA4J | 35.523 ± 0.102 |
| Colt | 197.914 ± 2.453 |
| Homemade | 497.493 ± 2.121 |
| Apache Commons | 511.140 ± 13.535 |

Как мы видим, самодельные реализации и библиотека Apache теперь намного хуже, чем раньше: умножение двух матриц занимает почти 10 минут.

Кольт занимает чуть больше 3 минут, что лучше, но все равно очень долго. EJML и LA4J работают довольно хорошо, поскольку выполняются почти за 30 секунд. Но именно ND4J побеждает в этом бенчмаркинге, работая менее чем за секунду на бэкенде ЦП.

Это показывает нам, что результаты бенчмаркинга действительно зависят от характеристик матриц, и поэтому сложно выделить единственного победителя.

## Сложность

### Временная сложность

- **Наивное умножение:** O(n³) - где n - размер матрицы
- **Оптимизированные библиотеки:** O(n².807) - алгоритм Штрассена или другие оптимизации

### Пространственная сложность

- **Наивное умножение:** O(n²) - для хранения результата
- **Оптимизированные библиотеки:** Могут использовать дополнительные буферы для оптимизации

## Особенности

- **Размер матриц:** Производительность сильно зависит от размера матриц
- **Оптимизации:** Современные библиотеки используют различные оптимизации (кэширование, векторизация, параллелизм)
- **Точность:** Все библиотеки обеспечивают достаточную точность для большинства применений

## Применение

Умножение матриц используется в:

- Машинном обучении
- Компьютерной графике
- Научных вычислениях
- Обработке сигналов
- Линейной алгебре
- Криптографии

## Когда использовать

### Используйте собственную реализацию, когда:

- Матрицы маленькие
- Нужна простота
- Нет внешних зависимостей

### Используйте EJML, когда:

- Нужна хорошая производительность для средних матриц
- Важна простота использования
- Работаете с матрицами среднего размера

### Используйте ND4J, когда:

- Работаете с очень большими матрицами
- Нужна максимальная производительность
- Используете GPU или другие ускорители

### Используйте Apache Commons Math3, когда:

- Нужны дополнительные математические функции
- Важна совместимость с другими библиотеками Apache
- Работаете с небольшими матрицами

## Заключение

В этом уроке мы рассмотрели различные способы умножения матриц в Java, от собственной реализации до использования специализированных библиотек. Выбор библиотеки зависит от размера матриц и требований к производительности.
