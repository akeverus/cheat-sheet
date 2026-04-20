---
title: "Реализация CNN с помощью Deeplearning4j"
description: "Сверточная нейронная сеть (CNN) — это тип глубокой нейронной сети, специально разработанной для обработки изображений. В этом руководстве мы создадим и обучим модель CNN, используя библиотеку Deeplearning4j на Java."
tags:
  - algorithms
  - ai-ml
  - cnn-deeplearning4j
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Реализация CNN с помощью Deeplearning4j

Сверточная нейронная сеть (CNN) — это тип глубокой нейронной сети, специально разработанной для обработки изображений. В этом руководстве мы создадим и обучим модель CNN, используя библиотеку Deeplearning4j на Java.

## Полезные ссылки

### Официальная документация
- [Deeplearning4j Documentation](https://deeplearning4j.org/docs/latest/)
- [CIFAR-10 Dataset](https://www.cs.toronto.edu/~kriz/cifar.html)

### Обучающие материалы
- [Introduction to Deeplearning4j](https://www.baeldung.com/deeplearning4j)

### См. также
- [Deeplearning4j](deeplearning4j.md)
- [Логистическая регрессия](logistic-regression.md)
- [Spark MLlib](spark-mllib.md)

- [Обзор ИИ библиотек](ai-libraries.md)
- [Руководство по Jenetics](jenetics.md)
## Содержание

- [Введение в CNN](#введение-в-cnn)
- [Архитектура CNN](#архитектура-cnn)
- [Сверточные слои](#сверточные-слои)
- [Слои подвыборки (пула)](#слои-подвыборки-пула)
- [Плотные слои](#плотные-слои)
- [Оптимизация и обучение](#оптимизация-и-обучение)
- [Реализация на Java](#реализация-на-java)
  - [Подготовка данных (Java)](#подготовка-данных-java)
- [Построение модели](#построение-модели)
- [Обучение и оценка](#обучение-и-оценка)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подготовка данных (Kotlin)](#подготовка-данных-kotlin)
  - [Построение модели CNN](#построение-модели-cnn)
  - [Обучение и оценка](#обучение-и-оценка-1)

## Введение в CNN

Для получения дополнительной информации о том, как настроить библиотеку, обратитесь к нашему руководству по Deeplearning4j.

Предположим, у нас есть набор изображений. Каждое изображение представляет объект определенного класса. Более того, объект на изображении принадлежит к единственному известному классу. Итак, постановка задачи состоит в том, чтобы построить модель, которая сможет распознавать класс объекта на заданном изображении.

Например, допустим, у нас есть набор изображений с десятью жестами рук. Мы строим модель и обучаем ее классифицировать их. Затем, после обучения, мы можем передать другие изображения и классифицировать жесты рук на них. Разумеется, данный жест должен принадлежать к известным классам.

В памяти компьютера изображение может быть представлено в виде матрицы чисел. Каждое число представляет собой значение пикселя в диапазоне от 0 до 255.

Изображение в градациях серого представляет собой двумерную матрицу. Точно так же изображение RGB представляет собой трехмерную матрицу с размерами ширины, высоты и глубины.

Как мы видим, изображение представляет собой набор чисел. Следовательно, мы можем создавать многослойные сетевые модели, чтобы обучать их классификации изображений.

## Архитектура CNN

Сверточная нейронная сеть (CNN) — это многоуровневая сетевая модель, имеющая определенную структуру. Структуру CNN можно разделить на два блока: сверточные слои и полносвязные (или плотные) слои. Давайте посмотрим на каждый из них.

## Сверточные слои

Каждый сверточный слой представляет собой набор квадратных матриц, называемых ядрами. Прежде всего, они нужны нам для выполнения свертки на входном изображении. Их количество и размер могут варьироваться в зависимости от данного набора данных. В основном мы используем ядра 3×3 или 5×5 и редко 7×7. Точный размер и количество подбирается методом проб и ошибок.

Кроме того, мы случайным образом выбираем переменные ядерных матриц в начале поезда. Это веса сети.

Для выполнения свертки мы можем использовать ядро в качестве скользящего окна. Мы умножим веса ядра на соответствующие пиксели изображения и вычислим сумму. Затем мы можем переместить ядро, чтобы покрыть следующий фрагмент изображения, используя шаг (перемещение вправо) и заполнение (перемещение вниз). В результате у нас будут значения, которые будут использоваться в дальнейших вычислениях.

Короче говоря, с этим слоем мы получаем свернутое изображение. Некоторые переменные могут быть меньше нуля. Обычно это означает, что эти переменные менее важны, чем другие. Вот почему применение функции ReLU — хороший способ сократить количество вычислений.

## Слои подвыборки (пула)

Слой подвыборки (или пула) — это слой сети, обычно используемый после сверточной. После свертки мы получаем множество вычисляемых переменных. Однако наша задача выбрать среди них наиболее ценные.

Подход заключается в применении алгоритма скользящего окна к свернутому изображению. На каждом шаге мы будем выбирать максимальное значение в квадратном окне предопределенного размера, обычно от 2×2 до 5×5 пикселей. В результате у нас будет меньше вычисляемых параметров. Следовательно, это сократит вычисления.

## Плотные слои

Плотный (или полносвязный) слой состоит из нескольких нейронов. Этот слой нужен нам для выполнения классификации. Более того, таких последовательных слоев может быть два или более. Важно, чтобы последний слой имел размер, равный количеству классов для классификации.

Выходом сети является вероятность того, что изображение принадлежит каждому из классов. Чтобы предсказать вероятности, мы будем использовать функцию активации Softmax.

## Оптимизация и обучение

Чтобы выполнить тренировку, нам нужно оптимизировать веса. Помните, изначально мы выбираем эти переменные случайным образом. Нейронная сеть — это большая функция. И у него много неизвестных параметров, наших весов.

Когда мы передаем изображение в сеть, она дает нам ответ. Затем мы можем построить функцию потерь, которая будет зависеть от этого ответа. Что касается обучения с учителем, у нас также есть реальный ответ — настоящий класс. Наша миссия состоит в том, чтобы минимизировать эту функцию потерь. Если нам это удастся, то наша модель хорошо обучена.

Чтобы минимизировать функцию, мы должны обновить веса сети. Для этого мы можем вычислить производную функции потерь по каждому из этих неизвестных параметров. Затем мы можем обновить каждый вес.

Мы можем увеличить или уменьшить значение веса, чтобы найти локальный минимум нашей функции потерь, потому что мы знаем наклон. Причем этот процесс итеративный и называется Gradient Descent. Обратное распространение использует градиентный спуск для распространения обновления веса от конца к началу сети.

В этом уроке мы будем использовать алгоритм оптимизации Stochastic Gradient Decent (SGD). Основная идея заключается в том, что мы случайным образом выбираем набор изображений поездов на каждом шаге. Затем применяем обратное распространение.

Наконец, после обучения сети нам нужно получить информацию о том, насколько хорошо работает наша модель.

Наиболее часто используемая метрика — это точность. Это отношение правильно классифицированных изображений ко всем изображениям. Между тем, полнота, точность и оценка F1 также являются очень важными показателями для классификации изображений.

## Реализация на Java

### Подготовка данных (Java)

В этом разделе мы подготовим изображения. В этом руководстве мы будем использовать встроенный набор данных CIFAR10. Мы создадим итераторы для доступа к изображениям:

```java
public class CifarDatasetService implements IDataSetService {
    private CifarDataSetIterator trainIterator;
    private CifarDataSetIterator testIterator;

    public CifarDatasetService() {
        trainIterator = new CifarDataSetIterator(trainBatch, trainImagesNum, true);
        testIterator = new CifarDataSetIterator(testBatch, testImagesNum, false);
    }
}
```

Некоторые параметры мы можем выбрать самостоятельно. TrainBatch и testBatch — количество изображений на поезд и этап оценки соответственно. TrainImagesNum и testImagesNum — количество изображений для обучения и тестирования. Одна эпоха длится trainImagesNum / trainBatch steps. Таким образом, наличие 2048 изображений поездов с размером пакета = 32 приведет к 2048/32 = 64 шагам за одну эпоху.

## Построение модели

Далее, давайте построим нашу модель CNN с нуля. Для этого мы будем использовать сверточные слои, субдискретизацию (объединение) и полносвязные (плотные) слои.

```java
MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
    .seed(1611)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .learningRate(properties.getLearningRate())
    .regularization(true)
    .updater(properties.getOptimizer())
    .list()
    .layer(0, conv5x5())
    .layer(1, pooling2x2Stride2())
    .layer(2, conv3x3Stride1Padding2())
    .layer(3, pooling2x2Stride1())
    .layer(4, conv3x3Stride1Padding1())
    .layer(5, pooling2x2Stride1())
    .layer(6, dense())
    .pretrain(false)
    .backprop(true)
    .setInputType(dataSetService.inputType())
    .build();

network = new MultiLayerNetwork(configuration);
```

Здесь мы указываем скорость обучения, алгоритм обновления, тип ввода нашей модели и многоуровневую архитектуру. Мы можем поэкспериментировать с этими конфигурациями. Таким образом, мы можем обучать множество моделей с различной архитектурой и параметрами обучения. Кроме того, мы можем сравнить результаты и выбрать лучшую модель.

## Обучение и оценка

Затем мы обучим построенную модель. Это можно сделать в несколько строк кода:

```java
public void train() {
    network.init();
    IntStream.range(1, epochsNum + 1).forEach(epoch -> {
        network.fit(dataSetService.trainIterator());
    });
}
```

Количество эпох — это параметр, который мы можем указать сами. У нас есть небольшой набор данных. В результате будет достаточно нескольких сотен эпох.

Наконец, мы можем оценить уже обученную модель. Библиотека Deeplearning4j позволяет легко это сделать:

```java
public Evaluation evaluate() {
    return network.evaluate(dataSetService.testIterator());
}
```

Оценка — объект с метриками после обучения (accuracy, precision, recall, F1); у него есть удобный вывод в консоль. Пример:

```text
==========================Scores=====================
# of classes: 11
Accuracy: 0.8406
Precision: 0.7303
Recall: 0.6820
F1 Score: 0.6466
=====================================================
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Низкая точность на тесте | Переобучение или мало данных | Увеличить аугментации, регуляризацию; уменьшить эпохи или сложность сети |
| OutOfMemory при обучении | Большой batch или разрешение изображений | Уменьшить batch size; уменьшить размер входа или число каналов/слоёв |
| Loss не уменьшается | Слишком большой learning rate или плохая инициализация | Уменьшить learning rate; проверить нормализацию данных и инициализацию весов |

## Частые вопросы

**Какие ядра свертки обычно используют?** Чаще 3×3 или 5×5; реже 7×7. Несколько слоёв 3×3 дают больший рецептивное поле при меньшем числе параметров, чем один слой 7×7.

**Зачем слой пула после свертки?** Уменьшение пространственного размера и числа параметров; инвариантность к малым сдвигам; снижение переобучения.

**Когда хватает одной CNN без переноса обучения?** При достаточном объёме данных и не слишком сложной задаче; для малых датасетов часто выгоднее fine-tuning предобученной модели.

## Заключение

В этом руководстве мы узнали об архитектуре моделей CNN, методах оптимизации и метриках оценки. Кроме того, мы реализовали модель с помощью библиотеки Deeplearning4j на Java.

## Реализация на Kotlin

### Подготовка данных (Kotlin)

```kotlin
import org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator

class CifarDatasetServiceK(
    private val trainBatch: Int,
    private val trainImagesNum: Int,
    private val testBatch: Int,
    private val testImagesNum: Int
) {
    private val trainIterator: CifarDataSetIterator
    private val testIterator: CifarDataSetIterator

    init {
        trainIterator = CifarDataSetIterator(trainBatch, trainImagesNum, true)
        testIterator = CifarDataSetIterator(testBatch, testImagesNum, false)
    }

    fun trainIterator(): CifarDataSetIterator = trainIterator
    fun testIterator(): CifarDataSetIterator = testIterator
}
```

### Построение модели CNN

```kotlin
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.*
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions

class CNNModelK {
    fun buildModel(learningRate: Double): MultiLayerNetwork {
        val configuration = NeuralNetConfiguration.Builder()
            .seed(1611)
            .optimizationAlgo(org.deeplearning4j.nn.conf.Updater.STOCHASTIC_GRADIENT_DESCENT)
            .learningRate(learningRate)
            .regularization(true)
            .updater(Sgd(learningRate))
            .list()
            .layer(0, ConvolutionLayer.Builder(5, 5)
                .nIn(3)
                .stride(1, 1)
                .nOut(20)
                .activation(Activation.IDENTITY)
                .build())
            .layer(1, SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .build())
            .layer(2, ConvolutionLayer.Builder(3, 3)
                .stride(1, 1)
                .padding(2, 2)
                .nOut(50)
                .activation(Activation.IDENTITY)
                .build())
            .layer(3, SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(1, 1)
                .build())
            .layer(4, ConvolutionLayer.Builder(3, 3)
                .stride(1, 1)
                .padding(1, 1)
                .nOut(50)
                .activation(Activation.IDENTITY)
                .build())
            .layer(5, SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(1, 1)
                .build())
            .layer(6, DenseLayer.Builder()
                .nOut(500)
                .activation(Activation.RELU)
                .build())
            .layer(7, OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(10)
                .activation(Activation.SOFTMAX)
                .build())
            .pretrain(false)
            .backprop(true)
            .build()

        return MultiLayerNetwork(configuration)
    }
}
```

### Обучение и оценка

```kotlin
import org.deeplearning4j.eval.Evaluation

class CNNTrainerK(
    private val network: MultiLayerNetwork,
    private val dataSetService: CifarDatasetServiceK
) {
    fun train(epochsNum: Int) {
        network.init()
        (1..epochsNum).forEach { epoch ->
            network.fit(dataSetService.trainIterator())
        }
    }

    fun evaluate(): Evaluation {
        return network.evaluate(dataSetService.testIterator())
    }
}
```
