---
title: "Рекомендательная система на основе Collaborative Filtering"
description: "Руководство по реализации алгоритма Slope One для рекомендательных систем на основе совместной фильтрации в Java."
tags:
  - algorithms
  - ai-ml
  - recommender-systems
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Рекомендательная система на основе Collaborative Filtering

Руководство по реализации алгоритма **Slope One** для рекомендательных систем на основе совместной фильтрации в **Java**.

## Полезные ссылки

### Официальная документация
- [Collaborative Filtering (Wikipedia)](https://en.wikipedia.org/wiki/Collaborative_filtering)
- [Slope One (Wikipedia)](https://en.wikipedia.org/wiki/Slope_One) — алгоритм **Slope One**

### См. также
- [[ai-libraries|Обзор ИИ библиотек]] — машинное обучение
- [Задачи и алгоритмы](../problems/README.md) — обзор разделов

## Содержание

- [Обзор](#обзор)
- [Алгоритм Slope One](#алгоритм-slope-one)
- [Реализация на Java](#реализация-на-java)
- [Преимущества и недостатки](#преимущества-и-недостатки)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)


## Обзор

В этом уроке мы узнаем все об алгоритме **Slope One** в **Java**.

Мы также покажем пример реализации задачи **Collaborative Filtering** (**CF**) - метода машинного обучения, используемого рекомендательными системами.

Это можно использовать, например, для прогнозирования интересов пользователей к определенным элементам.

## Что такое Collaborative Filtering?

Алгоритм **Slope One** представляет собой систему совместной фильтрации на основе элементов. Это означает, что он полностью основан на рейтинге пользовательских элементов. Когда мы вычисляем сходство между объектами, мы знаем только историю ранжирования, а не сам контент. Затем это сходство используется для прогнозирования рейтинга потенциальных пользователей для пар «пользователь - элемент», отсутствующих в наборе данных.

**На изображении ниже показан полный процесс получения и подсчета рейтинга для конкретного пользователя:**

Сначала пользователи оценивают разные элементы в системе. Далее алгоритм вычисляет подобия. После этого система делает прогнозы для рейтингов пользовательских элементов, которые пользователь еще не оценил.

Для более подробной информации по теме совместной фильтрации мы можем обратиться к статье в Википедии.

## Алгоритм Slope One

**Slope One** был назван простейшей формой нетривиальной совместной фильтрации на основе элементов на основе оценок. Он принимает во внимание как информацию от всех пользователей, которые оценили один и тот же элемент, так и информацию о других элементах, оцененных тем же пользователем, для расчета матрицы сходства.

В нашем простом примере мы собираемся прогнозировать рейтинг пользователей по товарам в магазине.

## Реализация на Java

### Модель данных (Java)

Давайте начнем с простой модели **Java** для нашей проблемы и предметной области.

**В нашей модели у нас есть два основных объекта - элементы и пользователи. Класс **Item** содержит имя элемента:**

```java
// Slope One: модель данных — Item и User; матрица различий рейтингов для предсказания.
public class Item {
    private String itemName;
    
    public Item(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemName, item.itemName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemName);
    }
}
```

**С другой стороны, класс **User** содержит имя пользователя:**

```java
public class User {
    private String username;
    
    public User(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
```

**Наконец, у нас есть класс **InputData**, который будет использоваться для инициализации данных. Предположим, что мы создадим в магазине пять разных товаров:**

```java
import java.util.Arrays;
import java.util.List;

public class InputData {
    public static List<Item> items = Arrays.asList(
        new Item("Candy"),
        new Item("Drink"),
        new Item("Soda"),
        new Item("Popcorn"),
        new Item("Snacks")
    );
}
```

**Кроме того, мы создадим трех пользователей, которые случайным образом оценят некоторые из вышеперечисленных по шкале от `0.0` до `1.0`, где 0 означает отсутствие интереса, `0.5` - определенный интерес и `1.0` - полный интерес. В результате инициализации данных мы получим карту с данными ранжирования пользовательских элементов:**

```java
Map<User, HashMap<Item, Double>> data;
```

## Расчет матрицы различий

**На основе имеющихся данных мы рассчитаем отношения между элементами, а также количество вхождений элементов. Для каждого пользователя мы проверяем его/ее рейтинг предметов:**

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SlopeOne {
    private Map<Item, HashMap<Item, Double>> diff = new HashMap<>();
    private Map<Item, HashMap<Item, Integer>> freq = new HashMap<>();
    
    public void buildDiffMatrix(Map<User, HashMap<Item, Double>> data) {
        for (HashMap<Item, Double> user : data.values()) {
            for (Entry<Item, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Item, Double>());
                    freq.put(e.getKey(), new HashMap<Item, Integer>());
                }
                
                for (Entry<Item, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        
        for (Item j : diff.keySet()) {
            for (Item i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
    }
}
```

Первая матрица используется для расчета различий между оценками пользователей. Его значения могут быть положительными или отрицательными (**поскольку разница между рейтингами может быть отрицательной**) и сохраняются как **Double**. С другой стороны, частоты сохраняются как целочисленные значения.

Если кто-то уже оценил элемент ранее, мы увеличиваем количество частот на единицу. Кроме того, мы проверяем среднюю разницу между оценками элемента и вычисляем новую наблюдаемую разницу.

Основная логика состоит в том, чтобы разделить разницу рассчитанного рейтинга элемента на количество его вхождений. После этого шага мы можем распечатать нашу окончательную матрицу различий.

## Прогнозирование рейтингов

**В качестве основной части **Slope One** мы собираемся спрогнозировать все недостающие рейтинги на основе существующих данных. Для этого нам нужно сравнить рейтинги пользовательских элементов с матрицей различий, рассчитанной на предыдущем шаге:**

```java
public HashMap<Item, Double> predict(Map<User, HashMap<Item, Double>> data) {
    HashMap<Item, Double> uPred = new HashMap<Item, Double>();
    HashMap<Item, Integer> uFreq = new HashMap<Item, Integer>();
    
    for (Item j : diff.keySet()) {
        uPred.put(j, 0.0);
        uFreq.put(j, 0);
    }
    
    for (Entry<User, HashMap<Item, Double>> e : data.entrySet()) {
        for (Item j : e.getValue().keySet()) {
            for (Item k : diff.keySet()) {
                try {
                    double predictedValue = diff.get(k).get(j).doubleValue() 
                        + e.getValue().get(j).doubleValue();
                    double finalValue = predictedValue * freq.get(k).get(j).intValue();
                    uPred.put(k, uPred.get(k) + finalValue);
                    uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                } catch (NullPointerException e) {
                    // Игнорируем отсутствующие элементы
                }
            }
        }
    }
    
    HashMap<Item, Double> clean = new HashMap<Item, Double>();
    for (Item j : uPred.keySet()) {
        if (uFreq.get(j) > 0) {
            clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
        }
    }
    
    for (Item j : InputData.items) {
        if (e.getValue().containsKey(j)) {
            clean.put(j, e.getValue().get(j));
        } else if (!clean.containsKey(j)) {
            clean.put(j, -1.0);
        }
    }
    
    return clean;
}
```

После этого нам нужно подготовить «чистые» прогнозы. Хитрость, которую следует учитывать при работе с большим набором данных, заключается в использовании только тех записей элементов, которые имеют большое значение частоты (**например, > 1**). Обратите внимание, что если прогноз невозможен, его значение будет равно -1.

## Полная реализация

```java
import java.util.*;

public class SlopeOneRecommender {
    private Map<Item, HashMap<Item, Double>> diff = new HashMap<>();
    private Map<Item, HashMap<Item, Integer>> freq = new HashMap<>();
    
    public void buildDiffMatrix(Map<User, HashMap<Item, Double>> data) {
        // Расчет матрицы различий
        for (HashMap<Item, Double> user : data.values()) {
            for (Entry<Item, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                
                for (Entry<Item, Double> e2 : user.entrySet()) {
                    int oldCount = freq.get(e.getKey())
                        .getOrDefault(e2.getKey(), 0);
                    double oldDiff = diff.get(e.getKey())
                        .getOrDefault(e2.getKey(), 0.0);
                    
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        
        // Нормализация
        for (Item j : diff.keySet()) {
            for (Item i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }
    
    public HashMap<Item, Double> predict(User user, 
                                        Map<User, HashMap<Item, Double>> data) {
        HashMap<Item, Double> uPred = new HashMap<>();
        HashMap<Item, Integer> uFreq = new HashMap<>();
        HashMap<Item, Double> userRatings = data.get(user);
        
        for (Item j : diff.keySet()) {
            uPred.put(j, 0.0);
            uFreq.put(j, 0);
        }
        
        for (Item j : userRatings.keySet()) {
            for (Item k : diff.keySet()) {
                if (diff.get(k).containsKey(j) && freq.get(k).containsKey(j)) {
                    double predictedValue = diff.get(k).get(j) 
                        + userRatings.get(j);
                    double finalValue = predictedValue * freq.get(k).get(j);
                    uPred.put(k, uPred.get(k) + finalValue);
                    uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                }
            }
        }
        
        HashMap<Item, Double> clean = new HashMap<>();
        for (Item j : uPred.keySet()) {
            if (uFreq.get(j) > 0) {
                clean.put(j, uPred.get(j) / uFreq.get(j));
            }
        }
        
        for (Item j : InputData.items) {
            if (userRatings.containsKey(j)) {
                clean.put(j, userRatings.get(j));
            } else if (!clean.containsKey(j)) {
                clean.put(j, -1.0);
            }
        }
        
        return clean;
    }
}
```

## Оптимизация производительности

**Есть несколько основных факторов, влияющих на алгоритм **Slope One**. Вот несколько советов, как повысить точность и время обработки:**

1. **Рассмотрите возможность получения рейтингов пользовательских элементов на стороне БД** для больших наборов данных
2. **Установить временные рамки** для получения оценок, так как интересы людей могут меняться со временем - это также сократит время, необходимое для обработки входных данных
3. **Разбивайте большие наборы данных на более мелкие** - не нужно каждый день рассчитывать прогнозы для всех пользователей; вы можете проверить, взаимодействовал ли пользователь с предсказанным элементом, а затем добавить/удалить его/ее из очереди обработки на следующий день

## Временная сложность

- **Построение матрицы различий**: `O(**n * m^2**)`, где n - количество пользователей, m - количество элементов
- **Прогнозирование**: `O(**m^2**)` для одного пользователя

## Пространственная сложность

- **Хранение матриц**: `O(**m^2**)` для **diff** и **freq** матриц

## Преимущества и недостатки

### Преимущества

- ✅ Простая реализация
- ✅ Быстрое прогнозирование
- ✅ Хорошо работает с разреженными данными
- ✅ Не требует настройки параметров

### Недостатки

- ❌ Может быть медленным для больших наборов данных
- ❌ Требует обновления матриц при добавлении новых данных
- ❌ Не учитывает временные изменения предпочтений

## Лучшие практики

Матрицу различий обновляйте при добавлении новых рейтингов; для больших каталогов храните её разреженно. Для холодного старта (новые пользователи/элементы) используйте fallback: средний рейтинг или контентные признаки. При больших данных — инкрементальное обновление или мини-батчи; Slope One имеет сложность O(n²) по парам элементов. Оценивайте качество по MAE/RMSE на отложенной выборке и A/B-тестам в продакшене.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Плохие предсказания для новых пользователей | Холодный старт: нет рейтингов | Fallback на средний рейтинг или контентную фильтрацию; гибрид с другими сигналами |
| Медленная работа на большом каталоге | Квадратичная сложность по элементам, плотная матрица | Разреженное хранение; инкрементальное обновление; ограничение числа пар или мини-батчи |
| Рекомендации не обновляются | Матрица не пересчитана после новых данных | Пересчитывать diff/freq при добавлении рейтингов или по расписанию |

## Частые вопросы

**Чем Slope One отличается от user-based collaborative filtering?** Slope One — item-based: сходство по разнице рейтингов между парами элементов; не требует явного подсчёта похожести пользователей. User-based CF ищет похожих пользователей по рейтингам.

**Когда достаточно Slope One без нейросетей?** При среднем объёме данных, наличии явных рейтингов и потребности в простой, интерпретируемой модели. Для очень больших или разреженных данных и неявных сигналов часто используют матричную факторизацию или нейросети.

**Как оценивать рекомендательную систему?** Метрики ошибки предсказания (MAE, RMSE) на отложенной выборке; ранжирующие метрики (NDCG, precision@k); A/B-тесты по конверсии и вовлечённости в продакшене.

## Резюме

В этом уроке мы смогли узнать об алгоритме **Slope One**. Кроме того, мы представили проблему совместной фильтрации для систем рекомендаций по элементам.

**Ключевые моменты:**
- **Slope One** - простой алгоритм совместной фильтрации
- Основан на разнице рейтингов между элементами
- Требует построения матрицы различий
- Эффективен для средних наборов данных
- Легко реализуется и понимается

**Slope One** - это отличный выбор для начала работы с рекомендательными системами благодаря своей простоте и эффективности.

## Реализация на Kotlin

### Модель данных (Kotlin)

```kotlin
data class ItemK(val itemName: String)

data class UserK(val username: String)
```

### Slope One Algorithm

```kotlin
class SlopeOneK {
    private val diff = mutableMapOf<ItemK, MutableMap<ItemK, Double>>()
    private val freq = mutableMapOf<ItemK, MutableMap<ItemK, Int>>()
    
    fun buildDiffMatrix(data: Map<UserK, Map<ItemK, Double>>) {
        data.values.forEach { user ->
            user.forEach { (item1, rating1) ->
                if (!diff.containsKey(item1)) {
                    diff[item1] = mutableMapOf()
                    freq[item1] = mutableMapOf()
                }
                
                user.forEach { (item2, rating2) ->
                    val oldCount = freq[item1]?.get(item2) ?: 0
                    val oldDiff = diff[item1]?.get(item2) ?: 0.0
                    val observedDiff = rating1 - rating2
                    
                    freq[item1]?.put(item2, oldCount + 1)
                    diff[item1]?.put(item2, oldDiff + observedDiff)
                }
            }
        }
        
        diff.forEach { (j, itemDiffs) ->
            itemDiffs.forEach { (i, oldValue) ->
                val count = freq[j]?.get(i) ?: 1
                diff[j]?.put(i, oldValue / count)
            }
        }
    }
    
    fun predict(data: Map<UserK, Map<ItemK, Double>>): Map<UserK, Map<ItemK, Double>> {
        val predictions = mutableMapOf<UserK, MutableMap<ItemK, Double>>()
        
        data.forEach { (user, userRatings) ->
            val uPred = mutableMapOf<ItemK, Double>()
            val uFreq = mutableMapOf<ItemK, Int>()
            
            diff.forEach { (j, itemDiffs) ->
                if (!userRatings.containsKey(j)) {
                    itemDiffs.forEach { (i, diffValue) ->
                        if (userRatings.containsKey(i)) {
                            val rating = userRatings[i] ?: 0.0
                            val predictedValue = rating + diffValue
                            val count = freq[j]?.get(i) ?: 1
                            
                            uPred[j] = (uPred[j] ?: 0.0) + predictedValue * count
                            uFreq[j] = (uFreq[j] ?: 0) + count
                        }
                    }
                }
            }
            
            val userPredictions = mutableMapOf<ItemK, Double>()
            uPred.forEach { (item, sum) ->
                val count = uFreq[item] ?: 1
                userPredictions[item] = sum / count
            }
            
            predictions[user] = userPredictions
        }
        
        return predictions
    }
}
```
