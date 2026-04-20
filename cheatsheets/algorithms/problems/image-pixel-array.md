---
title: "Массив пикселей изображения (Image Pixel Array)"
description: "Получение двумерного массива значений пикселей (RGB/ARGB) из BufferedImage в Java: поточечный getRGB() и быстрый доступ через DataBuffer/Raster. Работа с альфа-каналом, извлечение компонент, сравнение производительности. Java и Kotlin."
tags:
  - algorithms
  - problems
  - image-pixel-array
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Массив пикселей изображения (`Image Pixel Array`)

Получение двумерного массива значений пикселей (RGB/ARGB) из `BufferedImage` в Java: поточечный `getRGB()` и быстрый доступ через `DataBuffer`/`Raster`. Работа с альфа-каналом, извлечение компонент, сравнение производительности. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [BufferedImage (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html)
- [Raster (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/awt/image/Raster.html) — `Raster` и `DataBuffer`

### См. также
- [Задачи и алгоритмы](./) — обзор разделов

- [[a-star-pathfinding|Поиск пути A* (A* Pathfinding Algorithm)]]
- [[algorithms|Хеширование и хеш-функции (Hashing and Hash Functions)]]
- [[branch-prediction|Предсказание ветвления (Branch Prediction)]]
- [[calculator-implementation|Calculator Implementation]]
- [[circular-buffer|Circular Buffer]]

## Содержание

- [Обзор](#обзор)
- [BufferedImage и компоненты](#bufferedimage-и-его-компоненты)
- [Реализация на Java](#реализация-на-java)
- [Работа с альфа-каналом](#работа-с-альфа-каналом)
- [Сравнение производительности](#сравнение-производительности)
- [Примеры использования](#примеры-использования)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)


## Обзор

`BufferedImage` (подкласс `Image`) хранит растровое изображение с буфером в памяти. Состоит из `ColorModel` (интерпретация компонент цвета) и `Raster` (массив пикселей). `Raster` содержит `DataBuffer` с сырыми значениями и `SampleModel`, задающий раскладку пикселей в буфере.

## BufferedImage и его компоненты

`ColorModel` задаёт представление цвета (например, ARGB); методы вроде `getBlue(int pixel)` возвращают компоненту по упакованному пикселю. `Raster` даёт доступ к данным через `getDataBuffer()`; для `BufferedImage` с типом на байтах буфер обычно `DataBufferByte`.

## Реализация на Java

Медленный способ — цикл по координатам и вызов `getRGB(col, row)` для каждого пикселя: просто, но для больших изображений медленно из‑за накладных расходов на каждый вызов. Быстрый способ — один раз получить `DataBuffer` из `image.getRaster().getDataBuffer()`, привести к `DataBufferByte`, взять `getData()` и разбирать байты по порядку (ARGB по 4 байта или RGB по 3, в зависимости от наличия альфа-канала).

```java
// Построчное чтение пикселей через getRGB — просто, но медленно для больших изображений.
public int[][] get2DPixelArraySlow(BufferedImage sampleImage) {
    int width = sampleImage.getWidth();
    int height = sampleImage.getHeight();
    int[][] result = new int[height][width];

    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            result[row][col] = sampleImage.getRGB(col, row);
        }
    }

    return result;
}
```


```java
public int[][] get2DPixelArrayFast(BufferedImage image) {
    byte[] pixelData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    int width = image.getWidth();
    int height = image.getHeight();
    boolean hasAlphaChannel = image.getAlphaRaster() != null;
    int[][] result = new int[height][width];

    if (hasAlphaChannel) {
        int numberOfValues = 4; // ARGB
        for (int valueIndex = 0, row = 0, col = 0;
             valueIndex + numberOfValues - 1 < pixelData.length;
             valueIndex += numberOfValues) {

            int argb = 0;
            argb |= (((int) pixelData[valueIndex] & 0xff) << 24); // Alpha
            argb |= (((int) pixelData[valueIndex + 1] & 0xff) << 16); // Red
            argb |= (((int) pixelData[valueIndex + 2] & 0xff) << 8); // Green
            argb |= ((int) pixelData[valueIndex + 3] & 0xff); // Blue

            result[row][col] = argb;

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
    } else {
        int numberOfValues = 3; // RGB
        for (int valueIndex = 0, row = 0, col = 0;
             valueIndex + numberOfValues - 1 < pixelData.length;
             valueIndex += numberOfValues) {

            int argb = 0;
            argb |= (0xFF << 24); // Alpha (непрозрачный)
            argb |= (((int) pixelData[valueIndex] & 0xff) << 16); // Red
            argb |= (((int) pixelData[valueIndex + 1] & 0xff) << 8); // Green
            argb |= ((int) pixelData[valueIndex + 2] & 0xff); // Blue

            result[row][col] = argb;

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
    }

    return result;
}
```

Порядок каналов в буфере зависит от типа изображения (например, BGR для `TYPE_3BYTE_BGR`). Для ARGB: сначала альфа, затем красный, зелёный, синий; без альфа — три байта на пиксель, в начале цикла подставляем альфа 0xFF. Индексация по строкам и столбцам: сдвиг на `pixelLength` (3 или 4) на каждый пиксель, переход на следующую строку при `col == width`.

## Работа с альфа-каналом

Альфа: 0 — прозрачный, 255 — непрозрачный. Наличие альфа-канала: `image.getAlphaRaster() != null`. Извлечение компонент из упакованного `int`: сдвиг и маска 0xFF (alpha >> 24, red >> 16, green >> 8, blue без сдвига).

```java
public boolean hasAlphaChannel(BufferedImage image) {
    return image.getAlphaRaster() != null;
}

public int extractAlpha(int pixel) {
    return (pixel >> 24) & 0xFF;
}

public int extractRed(int pixel) {
    return (pixel >> 16) & 0xFF;
}

public int extractGreen(int pixel) {
    return (pixel >> 8) & 0xFF;
}

public int extractBlue(int pixel) {
    return pixel & 0xFF;
}
```

## Сравнение производительности

| Подход | Время (1920×1080) | Память | Сложность |
|--------|-------------------|--------|------------|
| getRGB() | ~500–800 мс | O(width×height) | Низкая |
| DataBuffer | ~50–100 мс | O(width×height) | Выше |

DataBuffer обычно в 5–10 раз быстрее на больших изображениях.

## Полная реализация

```java
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PixelArrayExtractor {

    public static int[][] get2DPixelArraySlow(BufferedImage sampleImage) {
        int width = sampleImage.getWidth();
        int height = sampleImage.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = sampleImage.getRGB(col, row);
            }
        }

        return result;
    }

    public static int[][] get2DPixelArrayFast(BufferedImage image) {
        byte[] pixelData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int width = image.getWidth();
        int height = image.getHeight();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int[][] result = new int[height][width];
        int pixelLength = hasAlphaChannel ? 4 : 3;

        for (int pixel = 0, row = 0, col = 0;
             pixel < pixelData.length;
             pixel += pixelLength) {

            int argb = 0;
            if (hasAlphaChannel) {
                argb |= (((int) pixelData[pixel] & 0xff) << 24); // Alpha
                argb |= (((int) pixelData[pixel + 1] & 0xff) << 16); // Red
                argb |= (((int) pixelData[pixel + 2] & 0xff) << 8); // Green
                argb |= ((int) pixelData[pixel + 3] & 0xff); // Blue
            } else {
                argb |= (0xFF << 24); // Alpha (непрозрачный)
                argb |= (((int) pixelData[pixel] & 0xff) << 16); // Red
                argb |= (((int) pixelData[pixel + 1] & 0xff) << 8); // Green
                argb |= ((int) pixelData[pixel + 2] & 0xff); // Blue
            }

            result[row][col] = argb;

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("image.jpg"));

        long start = System.currentTimeMillis();
        int[][] pixelsSlow = get2DPixelArraySlow(image);
        long slowTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        int[][] pixelsFast = get2DPixelArrayFast(image);
        long fastTime = System.currentTimeMillis() - start;

        System.out.println("Slow method: " + slowTime + " ms");
        System.out.println("Fast method: " + fastTime + " ms");
        System.out.println("Speedup: " + (slowTime / (double) fastTime) + "x");
    }
}
```

## Примеры использования

```java
// Цвет пикселя по координатам
public Color getPixelColor(BufferedImage image, int x, int y) {
    int pixel = image.getRGB(x, y);
    return new Color(pixel);
}

// Изменение яркости: умножить компоненты на factor, ограничить 255
public BufferedImage adjustBrightness(BufferedImage image, float factor) {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage result = new BufferedImage(width, height, image.getType());

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color color = new Color(image.getRGB(x, y));
            int r = Math.min(255, (int) (color.getRed() * factor));
            int g = Math.min(255, (int) (color.getGreen() * factor));
            int b = Math.min(255, (int) (color.getBlue() * factor));
            result.setRGB(x, y, new Color(r, g, b).getRGB());
        }
    }

    return result;
}
```

## Лучшие практики

Для больших изображений предпочтите `DataBuffer`/`Raster` вместо поточечного `getRGB`. Учитывайте порядок каналов (ARGB/RGB, у некоторых типов BGR) и наличие альфа-канала. Проверяйте тип `BufferedImage` (например, `TYPE_INT_ARGB`, `TYPE_3BYTE_BGR`) для корректного разбора байтов. Закрывайте потоки ввода-вывода; при пакетной обработке можно переиспользовать буферы.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| ClassCastException на getDataBuffer() | Тип буфера не DataBufferByte (например, INT) | Проверить image.getType(); для INT-буфера использовать DataBufferInt и соответствующую раскладку |
| Неверные цвета | Неверный порядок каналов или знак байта | Байты в Java знаковые: (byte & 0xff) при сборке int; проверить BGR vs RGB |
| Выход за границы массива | Неверный pixelLength или учёт stride | Убедиться, что шаг по pixelData равен 3 или 4 на пиксель; учитывать stride растра при нестандартном выравнивании |

## Частые вопросы

**Когда использовать getRGB, а когда DataBuffer?** getRGB удобен для малых изображений и разовых обращений к пикселю; для перебора всего изображения и производительности — DataBuffer.

**Почему (pixelData[i] & 0xff)?** В Java тип `byte` знаковый; при сдвиге в int знак расширяется. Маска 0xff даёт беззнаковое значение 0–255.

**Как поддержать TYPE_INT_RGB / TYPE_INT_ARGB?** У таких буферов один int на пиксель; привести к `DataBufferInt`, взять `getData()` и читать по одному int на пиксель без ручной сборки из байтов.

## Резюме

Два подхода: поточечный `getRGB()` — проще, медленнее; разбор через `DataBuffer` — быстрее, требует учёта типа изображения и порядка каналов. Выбор по объёму данных и требованиям к скорости.

## Реализация на Kotlin

```kotlin
import java.awt.image.BufferedImage

fun get2DPixelArraySlowK(sampleImage: BufferedImage): Array<IntArray> {
    val width = sampleImage.width
    val height = sampleImage.height
    val result = Array(height) { IntArray(width) }

    for (row in 0 until height) {
        for (col in 0 until width) {
            result[row][col] = sampleImage.getRGB(col, row)
        }
    }

    return result
}
```

```kotlin
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

fun get2DPixelArrayFastK(image: BufferedImage): Array<IntArray> {
    val pixelData = (image.raster.dataBuffer as DataBufferByte).data
    val width = image.width
    val height = image.height
    val hasAlphaChannel = image.alphaRaster != null
    val result = Array(height) { IntArray(width) }
    val pixelLength = if (hasAlphaChannel) 4 else 3

    var valueIndex = 0
    var row = 0
    var col = 0

    while (valueIndex + pixelLength - 1 < pixelData.size) {
        var argb = 0

        if (hasAlphaChannel) {
            argb = argb or (((pixelData[valueIndex].toInt() and 0xff) shl 24)) // Alpha
            argb = argb or (((pixelData[valueIndex + 1].toInt() and 0xff) shl 16)) // Red
            argb = argb or (((pixelData[valueIndex + 2].toInt() and 0xff) shl 8)) // Green
            argb = argb or (pixelData[valueIndex + 3].toInt() and 0xff) // Blue
        } else {
            argb = argb or (0xFF shl 24) // Alpha (непрозрачный)
            argb = argb or (((pixelData[valueIndex].toInt() and 0xff) shl 16)) // Red
            argb = argb or (((pixelData[valueIndex + 1].toInt() and 0xff) shl 8)) // Green
            argb = argb or (pixelData[valueIndex + 2].toInt() and 0xff) // Blue
        }

        result[row][col] = argb

        col++
        if (col == width) {
            col = 0
            row++
        }

        valueIndex += pixelLength
    }

    return result
}
```

```kotlin
fun extractAlphaK(pixel: Int): Int = (pixel shr 24) and 0xFF
fun extractRedK(pixel: Int): Int = (pixel shr 16) and 0xFF
fun extractGreenK(pixel: Int): Int = (pixel shr 8) and 0xFF
fun extractBlueK(pixel: Int): Int = pixel and 0xFF
```
