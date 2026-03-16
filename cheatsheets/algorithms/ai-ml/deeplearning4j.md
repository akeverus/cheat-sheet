---
title: "Руководство по deeplearning4j"
description: "Deeplearning4j (DL4J) — это современная и мощная библиотека для машинного обучения на Java. В этой статье мы создадим простую нейронную сеть с помощью DL4J для классификации набора данных цветка ириса."
tags: ["algorithms", "ai-ml", "deeplearning4j"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Руководство по deeplearning4j

Deeplearning4j (DL4J) — это современная и мощная библиотека для машинного обучения на Java. В этой статье мы создадим простую нейронную сеть с помощью DL4J для классификации набора данных цветка ириса.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [Deeplearning4j Documentation](https://deeplearning4j.org/docs/latest/)
- [ND4J Documentation](https://deeplearning4j.org/docs/latest/nd4j-overview)

### Baeldung
- [Introduction to Deeplearning4j](https://www.baeldung.com/deeplearning4j)

### См. также
- [CNN с Deeplearning4j](cnn-deeplearning4j.md)
- [Логистическая регрессия](logistic-regression.md)
- [Spark MLlib](spark-mllib.md)

## Содержание

- [Введение в нейронные сети](#введение-в-нейронные-сети)
- [Требования](#требования)
- [Настройка зависимостей](#настройка-зависимостей)
- [Набор данных Iris](#набор-данных-iris)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Загрузка и подготовка данных (Java)](#загрузка-и-подготовка-данных-java)
- [Нормализация данных](#нормализация-данных)
- [Разделение данных](#разделение-данных)
- [Построение нейронной сети](#построение-нейронной-сети)
- [Параметры конфигурации](#параметры-конфигурации)
- [Обучение модели](#обучение-модели)
- [Оценка модели](#оценка-модели)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Введение в нейронные сети

Прежде чем мы начнем, это руководство не требует глубоких знаний линейной алгебры, статистики, теории машинного обучения и множества других тем, необходимых хорошо подготовленному инженеру машинного обучения.

Нейронные сети — это вычислительные модели, состоящие из взаимосвязанных слоев узлов.

Узлы — это нейроноподобные процессоры числовых данных. Они берут данные со своих входов, применяют к этим данным некоторые веса и функции и отправляют результаты на выходы. Такую сеть можно обучить на некоторых примерах исходных данных.

По сути, обучение — это сохранение некоторого числового состояния (веса) в узлах, которое впоследствии влияет на вычисления. Обучающие примеры могут содержать элементы данных с признаками и определенными известными классами этих элементов (например, «этот набор 16×16 пикселей содержит написанную от руки букву «а»).

После завершения обучения нейронная сеть может извлекать информацию из новых данных, даже если она раньше не видела эти конкретные элементы данных. Хорошо смоделированная и хорошо обученная сеть может распознавать изображения, рукописные буквы, речь, обрабатывать статистические данные для получения результатов для бизнес-аналитики и многое другое.

Глубокие нейронные сети стали возможны в последние годы с развитием высокопроизводительных и параллельных вычислений. Такие сети отличаются от простых нейронных сетей тем, что состоят из множества промежуточных (или скрытых) слоев. Эта структура позволяет сетям обрабатывать данные гораздо более сложным образом (рекурсивно, рекуррентно, сверточно и т. д.) и извлекать из них намного больше информации.

## Требования

Чтобы использовать библиотеку, нам нужна как минимум Java 7. Также из-за некоторых нативных компонентов она работает только с 64-битной версией JVM.

Прежде чем начать с руководства, давайте проверим, выполнены ли требования:

```bash
$ java -version
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
```

## Настройка зависимостей

Во-первых, давайте добавим необходимые библиотеки в наш файл Maven pom.xml:

```xml
<properties>
    <dl4j.version>0.9.1</dl4j.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.nd4j</groupId>
        <artifactId>nd4j-native-platform</artifactId>
        <version>${dl4j.version}</version>
    </dependency>
    <dependency>
        <groupId>org.deeplearning4j</groupId>
        <artifactId>deeplearning4j-core</artifactId>
        <version>${dl4j.version}</version>
    </dependency>
</dependencies>
```

Обратите внимание, что зависимость nd4j-native-platform является одной из нескольких доступных реализаций.

Он основан на собственных библиотеках, доступных для многих различных платформ (macOS, Windows, Linux, Android и т. д.). Мы также могли бы переключить бэкенд на nd4j-cuda-8.0-platform, если бы хотели выполнять вычисления на видеокарте, поддерживающей модель программирования CUDA.

## Набор данных Iris

Напишем "Hello World" машинного обучения — классификацию набора данных цветка ириса. Это набор данных, собранных с цветков разных видов (Iris setosa, Iris versicolor и Iris virginica).

Эти виды отличаются длиной и шириной лепестков и чашелистиков. Было бы трудно написать точный алгоритм, который классифицирует элемент входных данных (т. е. определяет, к какому виду принадлежит тот или иной цветок). Но хорошо обученная нейронная сеть может классифицировать его быстро и с небольшими ошибками.

Мы собираемся использовать версию этих данных в формате CSV, где столбцы 0..3 содержат различные характеристики видов, а столбец 4 содержит класс записи или виды, закодированные значением 0, 1 или 2:

```text
5.1, 3.5, 1.4, 0.2, 0
4.9, 3.0, 1.4, 0.2, 0
4.7, 3.2, 1.3, 0.2, 0
…
7.0, 3.2, 4.7, 1.4, 1
6.4, 3.2, 4.5, 1.5, 1
6.9, 3.1, 4.9, 1.5, 1
…
```

## Реализация на Java

### Загрузка и подготовка данных (Java)

Мы кодируем класс числом, потому что нейронные сети работают с числами. Преобразование реальных элементов данных в ряды чисел (векторов) называется векторизацией — для этого deeplearning4j использует библиотеку datavec.

Во-первых, воспользуемся этой библиотекой для ввода файла с векторизованными данными. При создании CSVRecordReader мы можем указать количество пропускаемых строк (например, если в файле есть строка заголовка) и символ-разделитель (в нашем случае запятая):

```java
try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
    recordReader.initialize(new FileSplit(
        new ClassPathResource("iris.txt").getFile()));
}
```

Для перебора записей мы можем использовать любую из нескольких реализаций интерфейса DataSetIterator. Наборы данных могут быть довольно большими, и может пригодиться возможность постраничного вывода или кэширования значений.

Но наш небольшой набор данных содержит всего 150 записей, поэтому давайте сразу прочитаем все данные в память вызовом iterator.next().

Мы также указываем индекс столбца класса, который в нашем случае совпадает с количеством объектов (4) и общим количеством классов (3).

Также обратите внимание, что нам нужно перетасовать набор данных, чтобы избавиться от порядка классов в исходном файле.

Мы указываем постоянное случайное начальное число (42) вместо вызова System.currentTimeMillis() по умолчанию, чтобы результаты перетасовки всегда были одинаковыми. Это позволяет нам получать стабильные результаты при каждом запуске программы:

```java
DataSetIterator iterator = new RecordReaderDataSetIterator(
    recordReader, 150, FEATURES_COUNT, CLASSES_COUNT);
DataSet allData = iterator.next();
allData.shuffle(42);
```

## Нормализация данных

Еще одна вещь, которую мы должны сделать с данными перед обучением, — это нормализовать их. Нормализация представляет собой двухэтапный процесс:

1. Сбор некоторой статистики о данных (подгонка)
2. Изменение (преобразование) данных каким-либо образом, чтобы сделать их однородными

Нормализация может отличаться для разных типов данных.

Например, если мы хотим обрабатывать изображения разных размеров, мы должны сначала собрать статистику размера, а затем масштабировать изображения до одинакового размера.

Но для чисел нормализация обычно означает преобразование их в так называемое нормальное распределение. В этом нам может помочь класс NormalizerStandardize:

```java
DataNormalization normalizer = new NormalizerStandardize();
normalizer.fit(allData);
normalizer.transform(allData);
```

## Разделение данных

Теперь, когда данные подготовлены, нам нужно разделить набор на две части.

Первая часть будет использоваться на тренировке. Мы будем использовать вторую часть данных (которую сеть вообще не увидит) для тестирования обученной сети.

Это позволит нам убедиться, что классификация работает правильно. Возьмем 65% данных (0.65) для обучения, а остальные 35% оставим для тестирования:

```java
SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);
DataSet trainingData = testAndTrain.getTrain();
DataSet testData = testAndTrain.getTest();
```

## Построение нейронной сети

Теперь мы можем создать конфигурацию нашей сети с помощью модного беглого компоновщика:

```java
MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
    .iterations(1000)
    .activation(Activation.TANH)
    .weightInit(WeightInit.XAVIER)
    .learningRate(0.1)
    .regularization(true).l2(0.0001)
    .list()
    .layer(0, new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(3).build())
    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3).build())
    .layer(2, new OutputLayer.Builder(
        LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
        .activation(Activation.SOFTMAX)
        .nIn(3).nOut(CLASSES_COUNT).build())
    .backprop(true).pretrain(false)
    .build();
```

## Параметры конфигурации

Даже при таком упрощенном и свободном способе построения сетевой модели нужно многое переварить и настроить множество параметров. Давайте разберем эту модель.

### Итерации

Метод построителя iterations() указывает количество итераций оптимизации.

Итеративная оптимизация означает выполнение нескольких проходов обучающего набора до тех пор, пока сеть не сойдется к хорошему результату.

Обычно при обучении на реальных и больших наборах данных мы используем несколько эпох (полные проходы данных по сети) и одну итерацию для каждой эпохи. Но поскольку наш исходный набор данных минимален, мы будем использовать одну эпоху и несколько итераций.

### Функция активации

activation() — это функция, которая запускается внутри узла для определения его выходных данных.

Простейшая функция активации будет линейной f(x) = x. Но оказывается, что только нелинейные функции позволяют сетям решать сложные задачи с использованием нескольких узлов.

Доступно множество различных функций активации, которые мы можем найти в перечислении org.nd4j.linalg.activations.Activation. Мы также могли бы написать нашу функцию активации, если это необходимо. Но мы будем использовать предоставленную функцию гиперболического тангенса (tanh).

### Инициализация весов

Метод weightInit() указывает один из многих способов установки начальных весов для сети. Правильные начальные веса могут сильно повлиять на результаты тренировки. Не вдаваясь слишком в математику, давайте установим его в форме распределения Гаусса (WeightInit.XAVIER), так как это обычно хороший выбор для начала.

Все другие методы инициализации веса можно найти в перечислении org.deeplearning4j.nn.weights.WeightInit.

### Скорость обучения

Скорость обучения — важнейший параметр, сильно влияющий на способность сети к обучению.

Мы могли бы потратить много времени на настройку этого параметра в более сложном случае. Но для нашей простой задачи мы будем использовать довольно важное значение 0.1 и настроим его с помощью метода построения learningRate().

### Регуляризация

Одной из проблем обучения нейронных сетей является случай переобучения, когда сеть "запоминает" обучающие данные.

Это происходит, когда сеть устанавливает чрезмерно высокие веса для обучающих данных и дает плохие результаты для любых других данных.

Чтобы решить эту проблему, мы настроим регуляризацию l2 с помощью строки .regularization(true).l2(0.0001). Регуляризация "наказывает" сеть за слишком большие веса и предотвращает переобучение.

### Архитектура сети

Затем мы создаем сеть из плотных (также называемых полносвязными) слоев.

Первый слой должен содержать такое же количество узлов, как и столбцы в обучающих данных (4).

Второй плотный слой будет содержать три узла. Это значение мы можем варьировать, но количество выходов в предыдущем слое должно быть одинаковым.

Окончательный выходной слой должен содержать количество узлов, соответствующее количеству классов (3).

После успешного обучения у нас будет сеть, которая получает на свои входы четыре значения и отправляет сигнал на один из трех своих выходов. Это простой классификатор.

Наконец, чтобы закончить построение сети, мы настраиваем обратное распространение (один из самых эффективных методов обучения) и отключаем предварительное обучение с помощью строки .backprop(true).pretrain(false).

## Обучение модели

Теперь создадим нейросеть из конфигурации, инициализируем и запустим:

```java
MultiLayerNetwork model = new MultiLayerNetwork(configuration);
model.init();
model.fit(trainingData);
```

## Оценка модели

Теперь мы можем протестировать обученную модель, используя остальную часть набора данных, и проверить результаты с помощью метрик оценки для трех классов:

```java
INDArray output = model.output(testData.getFeatureMatrix());
Evaluation eval = new Evaluation(3);
eval.eval(testData.getLabels(), output);
```

Если распечатать eval.stats(), видно, что сеть неплохо классифицирует ирис; три примера класса 1 ошибочно отнесены к классу 2. Пример вывода:

```text
Examples labeled as 0 classified by model as 0: 19 times
Examples labeled as 1 classified by model as 1: 16 times
Examples labeled as 1 classified by model as 2: 3 times
Examples labeled as 2 classified by model as 2: 15 times
==========================Scores========================================
# of classes: 3
Accuracy: 0.9434
Precision: 0.9444
Recall: 0.9474
F1 Score: 0.9411
Precision, recall & F1: macro-averaged (equally weighted avg. of 3 classes)
========================================================================
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| NaN в loss или весах | Слишком большой learning rate или нестабильные градиенты | Уменьшить learning rate; проверить нормализацию входов; использовать gradient clipping |
| Низкая точность на тесте | Переобучение или недостаточно данных | Добавить регуляризацию (L2), dropout; увеличить данные или аугментации |
| OutOfMemory при обучении | Большой batch или модель | Уменьшить batch size; использовать ND4J с GPU или меньшей моделью |

## Частые вопросы

**Нужна ли GPU для Deeplearning4j?** Нет, ND4J работает на CPU; для ускорения можно использовать nd4j-cuda или nd4j-native с MKL. Для больших моделей и датасетов GPU существенно ускоряет обучение.

**Чем DL4J отличается от TensorFlow/Keras?** DL4J — нативный Java/Kotlin, интеграция с JVM-экосистемой; TensorFlow/Keras — Python-ориентированы. DL4J удобен для продакшена на JVM без Python-сервисов.

**Как сохранить и загрузить модель?** ModelSerializer.writeModel(model, path) и ModelSerializer.restoreMultiLayerNetwork(path) для сохранения архитектуры и весов.

## Заключение

Свободный построитель конфигураций позволяет нам быстро добавлять или изменять уровни сети или настраивать некоторые другие параметры, чтобы увидеть, можно ли улучшить нашу модель.

## Реализация на Kotlin

### Загрузка и подготовка данных (Kotlin)

```kotlin
import org.datavec.api.records.reader.impl.csv.CSVRecordReader
import org.datavec.api.split.FileSplit
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization
import org.springframework.core.io.ClassPathResource

class DeepLearning4jK {
    private val FEATURES_COUNT = 4
    private val CLASSES_COUNT = 3
    
    fun loadData(): DataSet {
        val recordReader = CSVRecordReader(0, ',')
        recordReader.initialize(FileSplit(ClassPathResource("iris.txt").file))
        
        val iterator = RecordReaderDataSetIterator(
            recordReader, 150, FEATURES_COUNT, CLASSES_COUNT
        )
        val allData = iterator.next()
        allData.shuffle(42)
        
        return allData
    }
    
    fun normalizeData(dataSet: DataSet): DataSet {
        val normalizer: DataNormalization = NormalizerStandardize()
        normalizer.fit(dataSet)
        normalizer.transform(dataSet)
        return dataSet
    }
}
```

### Построение нейронной сети

```kotlin
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.*
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions

fun buildModel(): MultiLayerNetwork {
    val configuration = NeuralNetConfiguration.Builder()
        .iterations(1000)
        .activation(Activation.TANH)
        .weightInit(WeightInit.XAVIER)
        .learningRate(0.1)
        .regularization(true).l2(0.0001)
        .list()
        .layer(0, DenseLayer.Builder().nIn(4).nOut(3).build())
        .layer(1, DenseLayer.Builder().nIn(3).nOut(3).build())
        .layer(2, OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
            .activation(Activation.SOFTMAX)
            .nIn(3).nOut(3).build())
        .backprop(true).pretrain(false)
        .build()
    
    return MultiLayerNetwork(configuration)
}
```

### Обучение и оценка

```kotlin
import org.deeplearning4j.eval.Evaluation

fun trainAndEvaluate(model: MultiLayerNetwork, trainingData: DataSet, testData: DataSet) {
    model.init()
    model.fit(trainingData)
    
    val eval = Evaluation(3)
    val output = model.output(testData.features)
    eval.eval(testData.labels, output)
    
    println(eval.stats())
}
```
