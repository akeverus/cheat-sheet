# Traveling Salesman Problem

Кратко: решение задачи коммивояжера (TSP) с использованием алгоритма имитации отжига. Рассматривается эвристический подход для поиска оптимального маршрута между узлами в графе.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Simulated Annealing](https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman)

### См. также
- `./knapsack-problem.md` - задача о рюкзаке
- `./a-star-pathfinding.md` - поиск пути A*
- `../graphs/dijkstra-algorithm.md` - алгоритм Дейкстры

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Алгоритм имитации отжига](#алгоритм-имитации-отжига)
- [Модели данных](#модели-данных)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Параметры алгоритма](#параметры-алгоритма)
- [Сложность](#сложность)

## Описание алгоритма

В этом руководстве мы узнаем об алгоритме имитации отжига и покажем пример реализации, основанный на задаче коммивояжера (TSP).

Задача коммивояжера (TSP) - самая известная в современном мире задача оптимизации информатики.

Проще говоря, это задача поиска оптимального маршрута между узлами в графе. Общее расстояние перемещения может быть одним из критериев оптимизации.

### Формулировка задачи

Коммивояжер должен посетить n городов, начиная и заканчивая в одном и том же городе, и посетить каждый город ровно один раз. Цель - найти маршрут с минимальным общим расстоянием.

## Алгоритм имитации отжига

Алгоритм имитации отжига - это эвристика для решения задач с большим пространством поиска.

Вдохновение и название пришли из отжига в металлургии; это метод, который включает нагрев и контролируемое охлаждение материала.

В общем, имитация отжига снижает вероятность принятия худших решений, поскольку исследует пространство решений и снижает температуру системы.

### Механизм работы

Алгоритм использует более широкий диапазон решений при высокой температуре системы, ища глобальный оптимум. При понижении температуры диапазон поиска становится меньше, пока не будет найден глобальный оптимум.

### Параметры алгоритма

Алгоритм имеет несколько параметров для работы:

1. **Количество итераций** - условие остановки для моделирования
2. **Начальная температура** - начальная энергия системы
3. **Параметр скорости охлаждения** - процент, на который мы снижаем температуру системы
4. **Минимальная температура** - опциональное условие остановки
5. **Время симуляции** - необязательное условие остановки

Значения этих параметров должны быть тщательно выбраны, так как они могут иметь существенное влияние на производительность процесса.

## Модели данных

Чтобы решить задачу TSP, нам понадобятся два класса моделей, а именно City и Travel. В первом мы будем хранить координаты узлов графа:

```java
public class City {
    private int x;
    private int y;
    
    public City() {
        this.x = (int) (Math.random() * 500);
        this.y = (int) (Math.random() * 500);
    }
    
    public City(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public double distanceToCity(City city) {
        int x = Math.abs(getX() - city.getX());
        int y = Math.abs(getY() - city.getY());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
```

Конструктор класса City позволяет нам создавать случайные расположения городов. Логика `distanceToCity()` отвечает за расчеты расстояния между городами.

Следующий код отвечает за моделирование тура коммивояжера. Начнем с генерации начального порядка городов в путешествии:

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Travel {
    private List<City> travel = new ArrayList<>();
    private List<City> previousTravel = new ArrayList<>();
    
    public Travel(int numberOfCities) {
        for (int i = 0; i < numberOfCities; i++) {
            travel.add(new City());
        }
    }
    
    public void generateInitialTravel() {
        if (travel.isEmpty()) {
            travel = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                travel.add(new City());
            }
        }
        Collections.shuffle(travel);
    }
    
    public void swapCities() {
        int a = generateRandomIndex();
        int b = generateRandomIndex();
        
        previousTravel = new ArrayList<>(travel);
        
        City x = travel.get(a);
        City y = travel.get(b);
        
        travel.set(a, y);
        travel.set(b, x);
    }
    
    public void revertSwap() {
        travel = previousTravel;
    }
    
    private int generateRandomIndex() {
        return (int) (Math.random() * travel.size());
    }
    
    public City getCity(int index) {
        return travel.get(index);
    }
    
    public int getDistance() {
        int distance = 0;
        
        for (int index = 0; index < travel.size(); index++) {
            City starting = getCity(index);
            City destination;
            
            if (index + 1 < travel.size()) {
                destination = getCity(index + 1);
            } else {
                destination = getCity(0);
            }
            
            distance += starting.distanceToCity(destination);
        }
        
        return distance;
    }
}
```

## Java Implementation

### Реализация

Теперь давайте сосредоточимся на основной части, реализации алгоритма имитации отжига.

В следующей реализации имитации отжига мы собираемся решить проблему TSP. Напомню, что цель состоит в том, чтобы найти кратчайшее расстояние для путешествия по всем городам.

Чтобы запустить процесс, нам нужно указать три основных параметра, а именно: startTemperature, numberOfIterations и coolRate:

```java
public class SimulatedAnnealing {
    private Travel travel;
    
    public SimulatedAnnealing(Travel travel) {
        this.travel = travel;
    }
    
    public double simulateAnnealing(
        double startingTemperature, 
        int numberOfIterations, 
        double coolingRate
    ) {
        double t = startingTemperature;
        travel.generateInitialTravel();
        
        double bestDistance = travel.getDistance();
        Travel currentSolution = travel;
        
        for (int i = 0; i < numberOfIterations; i++) {
            if (t > 0.1) {
                currentSolution.swapCities();
                double currentDistance = currentSolution.getDistance();
                
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                } else if (Math.exp((bestDistance - currentDistance) / t) < Math.random()) {
                    currentSolution.revertSwap();
                }
                
                t *= coolingRate;
            } else {
                continue;
            }
        }
        
        return bestDistance;
    }
}
```

### Объяснение алгоритма

Перед началом симуляции мы генерируем начальный (случайный) порядок городов и вычисляем общее расстояние для проезда. Поскольку это первое рассчитанное расстояние, мы сохраняем его в переменной `bestDistance` вместе с `currentSolution`.

На каждом шаге моделирования мы случайным образом меняем местами два города в порядке следования.

Кроме того, мы вычисляем `currentDistance`. Если вновь вычисленное значение `currentDistance` меньше значения `bestDistance`, мы сохраняем его как лучшее.

В противном случае мы проверяем, не меньше ли функция распределения вероятностей Больцмана, чем случайно выбранное значение в диапазоне от 0 до 1. Если да, то отменяем обмен городами. Если нет, сохраняем новый порядок городов, так как это может помочь нам избежать локальных минимумов.

Наконец, на каждом этапе симуляции мы уменьшаем температуру на заданную скорость охлаждения.

### Пример использования

```java
Travel travel = new Travel(10);
SimulatedAnnealing sa = new SimulatedAnnealing(travel);

double bestDistance = sa.simulateAnnealing(10000, 1000, 0.9995);
System.out.println("Best distance: " + bestDistance);
```

## Kotlin Implementation

### Классы City и Travel

```kotlin
data class CityK(val x: Int, val y: Int) {
    constructor() : this((Math.random() * 500).toInt(), (Math.random() * 500).toInt())
    
    fun distanceToCity(city: CityK): Double {
        val dx = Math.abs(x - city.x)
        val dy = Math.abs(y - city.y)
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }
}

class TravelK(private val numberOfCities: Int) {
    private val travel = mutableListOf<CityK>()
    private var previousTravel = mutableListOf<CityK>()
    
    init {
        for (i in 0 until numberOfCities) {
            travel.add(CityK())
        }
    }
    
    fun generateInitialTravel() {
        if (travel.isEmpty()) {
            for (i in 0 until 10) {
                travel.add(CityK())
            }
        }
        travel.shuffle()
    }
    
    fun swapCities() {
        val a = (Math.random() * travel.size).toInt()
        val b = (Math.random() * travel.size).toInt()
        
        previousTravel = ArrayList(travel)
        
        val x = travel[a]
        val y = travel[b]
        
        travel[a] = y
        travel[b] = x
    }
    
    fun revertSwap() {
        travel.clear()
        travel.addAll(previousTravel)
    }
    
    fun getCity(index: Int): CityK = travel[index]
    
    fun getDistance(): Double {
        var distance = 0.0
        
        for (index in travel.indices) {
            val starting = getCity(index)
            val destination = if (index + 1 < travel.size) {
                getCity(index + 1)
            } else {
                getCity(0)
            }
            
            distance += starting.distanceToCity(destination)
        }
        
        return distance
    }
}
```

### Алгоритм имитации отжига

```kotlin
class SimulatedAnnealingK(private val travel: TravelK) {
    fun simulateAnnealing(
        startingTemperature: Double,
        numberOfIterations: Int,
        coolingRate: Double
    ): Double {
        var t = startingTemperature
        travel.generateInitialTravel()
        
        var bestDistance = travel.getDistance()
        val currentSolution = travel
        
        for (i in 0 until numberOfIterations) {
            if (t > 0.1) {
                currentSolution.swapCities()
                val currentDistance = currentSolution.getDistance()
                
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance
                } else if (Math.exp((bestDistance - currentDistance) / t) < Math.random()) {
                    currentSolution.revertSwap()
                }
                
                t *= coolingRate
            }
        }
        
        return bestDistance
    }
}
```

### Пример использования

```kotlin
fun main() {
    val travel = TravelK(10)
    val sa = SimulatedAnnealingK(travel)
    
    val bestDistance = sa.simulateAnnealing(10000.0, 1000, 0.9995)
    println("Best distance: $bestDistance")
}
```

## Параметры алгоритма

Обратите внимание на несколько советов о том, как выбрать наилучшие параметры моделирования:

1. **Для небольших пространств решения:** лучше понизить начальную температуру и увеличить скорость охлаждения, так как это сократит время моделирования без потери качества

2. **Для больших пространств решения:** выберите более высокую начальную температуру и меньшую скорость охлаждения, так как будет больше локальных минимумов

3. **Всегда предоставляйте достаточно времени:** для имитации перехода от высокой к низкой температуре системы

Не забудьте потратить некоторое время на настройку алгоритма с меньшим экземпляром задачи, прежде чем приступить к основному моделированию, так как это улучшит окончательные результаты.

### Рекомендуемые значения

- **Начальная температура:** 1000-10000 (зависит от размера задачи)
- **Количество итераций:** 1000-10000
- **Скорость охлаждения:** 0.95-0.999
- **Минимальная температура:** 0.1

## Сложность

### Временная сложность

- **Имитация отжига:** O(k × n²) - где k - количество итераций, n - количество городов
  - На каждой итерации: O(n) для вычисления расстояния
  - Общее: O(k × n)

### Пространственная сложность

- **Хранение маршрута:** O(n) - где n - количество городов
- **Дополнительная память:** O(n) - для хранения предыдущего состояния

## Особенности

- **Эвристический подход:** Не гарантирует оптимальное решение
- **Гибкость:** Легко адаптировать для различных задач оптимизации
- **Параметры:** Требует тщательной настройки параметров

## Применение

Задача коммивояжера используется в:

- Логистике и доставке
- Планировании маршрутов
- Производстве (сверление отверстий)
- Сетевых технологиях
- Биоинформатике

## Варианты задачи

### Вариант 1: TSP с ограничениями

```java
public class ConstrainedTSP extends Travel {
    private Map<City, List<City>> constraints;
    
    public boolean isValidRoute() {
        for (int i = 0; i < travel.size(); i++) {
            City current = travel.get(i);
            City next = (i + 1 < travel.size()) ? 
                travel.get(i + 1) : travel.get(0);
            
            if (constraints.containsKey(current)) {
                if (!constraints.get(current).contains(next)) {
                    return false;
                }
            }
        }
        return true;
    }
}
```

### Вариант 2: TSP с временными окнами

```java
public class TSPWithTimeWindows extends Travel {
    private Map<City, TimeWindow> timeWindows;
    
    public double getTotalTime() {
        double time = 0;
        for (int i = 0; i < travel.size(); i++) {
            City current = travel.get(i);
            City next = (i + 1 < travel.size()) ? 
                travel.get(i + 1) : travel.get(0);
            
            time += current.distanceToCity(next);
            time += getServiceTime(current);
        }
        return time;
    }
}
```

## Когда использовать

### Используйте имитацию отжига, когда:

- Пространство решений очень большое
- Нужно найти хорошее приближенное решение
- Есть время на настройку параметров
- Точное решение слишком дорого

### Не используйте, когда:

- Нужно гарантированно оптимальное решение
- Время выполнения критично
- Пространство решений маленькое (можно использовать полный перебор)

## Заключение

В этом кратком руководстве мы смогли узнать об алгоритме имитации отжига и решили задачу коммивояжера. Надеюсь, это показывает, насколько удобен этот простой алгоритм применительно к определенным типам задач оптимизации.
