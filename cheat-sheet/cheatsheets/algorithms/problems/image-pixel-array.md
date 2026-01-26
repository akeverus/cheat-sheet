# Получение массива пикселей из изображения

Руководство по получению массива пикселей, содержащего информацию об изображении (значения RGB), из экземпляра BufferedImage в Java.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [BufferedImage JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html)
- [Raster и DataBuffer](https://docs.oracle.com/javase/8/docs/api/java/awt/image/Raster.html)

### См. также
- [Обработка изображений](../problems/)
- [Массивы в Java](../problems/)

## Содержание

- [Обзор](#обзор)
- [BufferedImage и его компоненты](#bufferedimage-и-его-компоненты)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение производительности](#сравнение-производительности)
- [Работа с альфа-каналом](#работа-с-альфа-каналом)
- [Примеры использования](#примеры-использования)

## Обзор

В этом руководстве мы узнаем, как получить массив пикселей, содержащих информацию об изображении (значения RGB), из экземпляра BufferedImage в Java.

Класс BufferedImage является подклассом Image, который описывает графическое изображение с доступным буфером данных изображения. BufferedImage состоит из ColorModel и Raster.

## BufferedImage и его компоненты

### ColorModel

ColorModel описывает, как цвета могут быть представлены с помощью комбинации компонентов в виде кортежей значений. Класс ColorModel в Java состоит из методов, которые могут возвращать значения цвета для определенного пикселя. Например, `getBlue(int pixel)` возвращает значение синего цвета для данного пикселя.

### Raster

Более того, класс Raster содержит данные изображения в виде массива пикселей. Класс Raster состоит из DataBuffer, в котором хранятся значения изображения, и SampleModel, описывающего, как пиксели хранятся в DataBuffer.

## Java Implementation

### Медленный подход: getRGB()

Первый подход заключается в использовании метода экземпляра `getRGB()` из класса BufferedImage.

Метод `getRGB()` объединяет значения RGB для указанного пикселя в одно целое число и возвращает результат. Это целое число содержит значения RGB, к которым можно получить доступ с помощью ColorModel экземпляра. Более того, чтобы получить результат для каждого пикселя изображения, мы должны перебрать их и вызвать метод для каждого пикселя в отдельности:

```java
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

В приведенном выше фрагменте кода результирующий массив представляет собой двумерный массив, содержащий значения RGB для каждого пикселя изображения. Этот подход является более простым, но и менее эффективным, чем следующий подход.

### Быстрый подход: DataBuffer

В этом методе мы сначала получаем все значения RGB из изображения по отдельности, а затем вручную объединяем их в одно целое число. После этого мы заполняем двумерный массив, содержащий значения пикселей, так же, как и в первом подходе. Этот метод сложнее, но значительно быстрее, чем первый подход:

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

В приведенном выше фрагменте кода мы сначала получаем отдельные значения RGB для каждого пикселя изображения и сохраняем их в массиве байтов с именем pixelData.

Например, если предположить, что изображение не имеет альфа-канала (альфа-канал содержит информацию о прозрачности изображения), pixelData[0] содержит значение синего для первого пикселя изображения, а pixelData[1] и pixelData[2] содержат зеленые и красные значения соответственно. Аналогично, от pixelData[3] до pixelData[5] содержатся значения RGB для второго пикселя изображения и так далее.

Получив значения, мы должны объединить их в одно целое число для каждого пикселя. Но перед этим нам нужно узнать, есть ли у изображения альфа-канал. Если у изображения есть альфа-канал, нам нужно объединить четыре значения (красный, зеленый, синий и информацию о прозрачности) в одно целое число. Если нет, нам нужно будет только объединить значения RGB.

После объединения всех значений в одно целое число помещаем целое число на его позицию в двумерном массиве.

## Работа с альфа-каналом

Альфа-канал определяет прозрачность пикселя. Значение 0 означает полностью прозрачный пиксель, а значение 255 - полностью непрозрачный.

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

| Подход | Время для изображения 1920x1080 | Память | Сложность кода |
|--------|-------------------------------|--------|----------------|
| getRGB() | ~500-800 мс | O(width × height) | Низкая |
| DataBuffer | ~50-100 мс | O(width × height) | Высокая |

Быстрый подход примерно в 5-10 раз быстрее для больших изображений.

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

### Извлечение цвета конкретного пикселя

```java
public Color getPixelColor(BufferedImage image, int x, int y) {
    int pixel = image.getRGB(x, y);
    return new Color(pixel);
}
```

### Изменение яркости изображения

```java
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

## Резюме

В этой короткой статье мы узнали, как получить двумерный массив, содержащий комбинированные значения RGB для каждого пикселя изображения в Java.

Мы рассмотрели два подхода:
1. **Медленный подход** - использует `getRGB()` для каждого пикселя, прост в реализации, но медленнее
2. **Быстрый подход** - работает напрямую с DataBuffer, сложнее в реализации, но значительно быстрее для больших изображений

Выбор подхода зависит от ваших требований к производительности и сложности кода.

## Kotlin Implementation

### Медленный подход: getRGB()

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

### Быстрый подход: DataBuffer

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

### Извлечение компонентов цвета

```kotlin
fun extractAlphaK(pixel: Int): Int = (pixel shr 24) and 0xFF
fun extractRedK(pixel: Int): Int = (pixel shr 16) and 0xFF
fun extractGreenK(pixel: Int): Int = (pixel shr 8) and 0xFF
fun extractBlueK(pixel: Int): Int = pixel and 0xFF
```
