# Greedy Algorithms

Кратко: реализация жадных алгоритмов для решения задач оптимизации. Рассматривается применение жадной стратегии для извлечения данных из социальной сети с ограничениями API.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Greedy Algorithms](https://www.baeldung.com/java-greedy-algorithms)

### См. также
- `./knapsack-problem.md` - задача о рюкзаке
- `./traveling-salesman-problem.md` - задача коммивояжера

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Концепция жадных алгоритмов](#концепция-жадных-алгоритмов)
- [Пример: Социальная сеть](#пример-социальная-сеть)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение с другими подходами](#сравнение-с-другими-подходами)
- [Сложность](#сложность)

## Описание алгоритма

Столкнувшись с математической задачей, может быть несколько способов разработать решение. Мы можем реализовать итеративное решение или некоторые передовые методы, такие как принцип «разделяй и властвуй» (например, алгоритм быстрой сортировки) или подход с динамическим программированием (например, задача о рюкзаке) и многое другое.

Большую часть времени мы ищем оптимальное решение, но, к сожалению, не всегда получаем такой результат. Однако бывают случаи, когда ценен даже неоптимальный результат. С помощью некоторых конкретных стратегий или эвристик мы могли бы заработать себе такую драгоценную награду.

В этом контексте для делимой задачи стратегия, которая на каждом этапе процесса принимает локально оптимальный выбор или «жадный выбор», называется жадным алгоритмом.

## Концепция жадных алгоритмов

Мы заявили, что должны решать «делимую» проблему: ситуацию, которую можно описать как набор подзадач с почти одинаковыми характеристиками. Как следствие, большую часть времени жадный алгоритм будет реализован как рекурсивный алгоритм.

Жадный алгоритм может привести нас к разумному решению, несмотря на суровые условия; отсутствие вычислительных ресурсов, ограничение времени выполнения, ограничения API или любые другие ограничения.

### Характеристики жадных алгоритмов

1. **Локально оптимальный выбор:** На каждом шаге выбирается лучший вариант из доступных
2. **Необратимость:** Принятые решения не отменяются
3. **Эффективность:** Обычно быстрее, чем полный перебор
4. **Не всегда оптимально:** Может не давать глобально оптимальное решение

## Пример: Социальная сеть

В этом коротком руководстве мы собираемся реализовать жадную стратегию для извлечения данных из социальной сети с использованием ее API.

Допустим, мы хотим охватить больше пользователей в социальной сети. Лучший способ достичь нашей цели - опубликовать оригинальный контент или ретвитнуть что-то, что вызовет интерес у широкой аудитории.

Как найти такую аудиторию? Что ж, мы должны найти учетную запись с большим количеством подписчиков и твитнуть для них какой-нибудь контент.

### Проблема с ограничениями API

Учитываем следующую ситуацию: На нашем аккаунте четыре подписчика, у каждого из которых соответственно 2, 2, 1 и 3 подписчика и так далее.

С этой целью мы возьмем того, у кого больше подписчиков среди подписчиков нашего аккаунта. Затем мы повторим процесс еще два раза, пока не достигнем 3-й степени связи (всего четыре шага).

Таким образом, мы определяем путь, состоящий из пользователей, который ведет нас к самой обширной базе подписчиков из нашей учетной записи.

### Проблема с традиционным подходом

Мы можем начать с «традиционного» подхода. На каждом этапе мы будем выполнять запрос, чтобы получить подписчиков учетной записи. В результате нашего процесса отбора количество учетных записей будет увеличиваться с каждым шагом.

Удивительно, но в итоге мы выполнили 25 запросов.

Здесь возникает проблема: например, Twitter API ограничивает этот тип запроса до 15 каждые 15 минут. Если мы попытаемся выполнить больше вызовов, чем разрешено, мы получим ошибку превышения лимита скорости.

### Решение с жадным алгоритмом

Что ж, ответ прямо перед нами: жадный алгоритм. Если мы используем этот подход, на каждом этапе мы можем предположить, что пользователь с наибольшим количеством подписчиков является единственным, кого следует учитывать: в конце концов нам нужно всего четыре запроса. Несомненное улучшение!

Результат этих двух подходов будет разным. В первом случае мы получаем 16, оптимальное решение, а во втором максимальное количество достижимых последователей составляет всего 12.

Будет ли эта разница столь ценной? Мы решим позже.

## Java Implementation

### Реализация

Чтобы реализовать описанную выше логику, мы инициализируем небольшую программу на Java, в которой будем имитировать API Twitter.

Теперь давайте определим наш компонент SocialConnector, в котором мы будем реализовывать нашу логику:

```java
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class SocialConnector {
    private boolean isCounterEnabled = true;
    private int counter = 4;
    
    @Getter @Setter
    private List<SocialUser> users;
    
    public SocialConnector() {
        users = new ArrayList<>();
    }
    
    public boolean switchCounter() {
        this.isCounterEnabled = !this.isCounterEnabled;
        return this.isCounterEnabled;
    }
    
    public List<SocialUser> getFollowers(String account) {
        if (counter < 0) {
            throw new IllegalStateException("API limit reached");
        } else {
            if (this.isCounterEnabled) {
                counter--;
            }
        }
        
        for (SocialUser user : users) {
            if (user.getUsername().equals(account)) {
                return user.getFollowers();
            }
        }
        
        return new ArrayList<>();
    }
}
```

Для поддержки нашего процесса нам нужны некоторые классы для моделирования нашей пользовательской сущности:

```java
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class SocialUser {
    @Getter
    private String username;
    
    @Getter
    private List<SocialUser> followers;
    
    public SocialUser(String username) {
        this.username = username;
        this.followers = new ArrayList<>();
    }
    
    public SocialUser(String username, List<SocialUser> followers) {
        this.username = username;
        this.followers = followers;
    }
    
    public void addFollowers(List<SocialUser> followers) {
        this.followers.addAll(followers);
    }
    
    public long getFollowersCount() {
        return followers.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SocialUser) {
            return ((SocialUser) obj).getUsername().equals(username);
        }
        return false;
    }
}
```

Наконец, пришло время реализовать нашу жадную стратегию:

```java
public class GreedyAlgorithm {
    int currentLevel = 0;
    final int maxLevel = 3;
    SocialConnector sc;
    
    public GreedyAlgorithm(SocialConnector sc) {
        this.sc = sc;
    }
    
    public long findMostFollowersPath(String account) {
        long max = 0;
        SocialUser toFollow = null;
        
        List<SocialUser> followers = sc.getFollowers(account);
        
        for (SocialUser el : followers) {
            long followersCount = el.getFollowersCount();
            if (followersCount > max) {
                toFollow = el;
                max = followersCount;
            }
        }
        
        if (currentLevel < maxLevel - 1) {
            currentLevel++;
            max += findMostFollowersPath(toFollow.getUsername());
        }
        
        return max;
    }
}
```

Помните: здесь мы совершаем жадный выбор. Таким образом, каждый раз, когда мы вызываем этот метод, мы выбираем один и только один элемент из списка и идем дальше: мы никогда не откажемся от своих решений!

### Нежадный подход для сравнения

Давайте создадим нежадный метод, просто чтобы проверить своими глазами, что происходит:

```java
public class NonGreedyAlgorithm {
    int currentLevel = 0;
    final int maxLevel = 3;
    SocialConnector tc;
    
    public NonGreedyAlgorithm(SocialConnector tc, int level) {
        this.tc = tc;
        this.currentLevel = level;
    }
    
    public long findMostFollowersPath(String account) {
        List<SocialUser> followers = tc.getFollowers(account);
        long total = currentLevel > 0 ? followers.size() : 0;
        
        if (currentLevel < maxLevel) {
            currentLevel++;
            long[] count = new long[followers.size()];
            int i = 0;
            
            for (SocialUser el : followers) {
                NonGreedyAlgorithm sub = new NonGreedyAlgorithm(tc, currentLevel);
                count[i] = sub.findMostFollowersPath(el.getUsername());
                i++;
            }
            
            long max = 0;
            for (int j = 0; j < i; j++) {
                if (count[j] > max) {
                    max = count[j];
                }
            }
            
            return total + max;
        }
        
        return total;
    }
}
```

## Сравнение с другими подходами

| Подход | Количество запросов | Результат | Время выполнения |
|--------|-------------------|-----------|------------------|
| Жадный алгоритм | 4 | 12 | Быстро |
| Полный перебор | 25 | 16 (оптимально) | Медленно |
| Полный перебор (без ограничений) | 25 | 16 | Медленно |

### Выводы

Сначала мы опробовали нашу жадную стратегию, проверив ее эффективность. Затем мы проверили ситуацию полным перебором, с ограничением API и без него.

Наша быстрая жадная процедура, которая каждый раз делает локально оптимальный выбор, возвращает числовое значение. С другой стороны, мы ничего не получаем от нежадного алгоритма из-за ограничения среды.

Сравнивая вывод двух методов, мы можем понять, как наша жадная стратегия спасла нас, даже если полученное значение не является оптимальным. Мы можем назвать это локальным оптимумом.

## Kotlin Implementation

### Классы SocialUser и SocialConnector

```kotlin
class SocialUserK(val username: String) {
    val followers = mutableListOf<SocialUserK>()
    
    fun addFollowers(followers: List<SocialUserK>) {
        this.followers.addAll(followers)
    }
    
    fun getFollowersCount(): Long = followers.size.toLong()
}

class SocialConnectorK {
    private var isCounterEnabled = true
    private var counter = 4
    val users = mutableListOf<SocialUserK>()
    
    fun switchCounter(): Boolean {
        isCounterEnabled = !isCounterEnabled
        return isCounterEnabled
    }
    
    fun getFollowers(account: String): List<SocialUserK> {
        if (counter < 0) {
            throw IllegalStateException("API limit reached")
        } else {
            if (isCounterEnabled) {
                counter--
            }
        }
        
        return users.firstOrNull { it.username == account }?.followers ?: emptyList()
    }
}
```

### Жадный алгоритм

```kotlin
class GreedyAlgorithmK(private val sc: SocialConnectorK) {
    private var currentLevel = 0
    private val maxLevel = 3
    
    fun findMostFollowersPath(account: String): Long {
        var max = 0L
        var toFollow: SocialUserK? = null
        
        val followers = sc.getFollowers(account)
        
        for (el in followers) {
            val followersCount = el.getFollowersCount()
            if (followersCount > max) {
                toFollow = el
                max = followersCount
            }
        }
        
        if (currentLevel < maxLevel - 1 && toFollow != null) {
            currentLevel++
            max += findMostFollowersPath(toFollow.username)
        }
        
        return max
    }
}
```

### Пример использования

```kotlin
fun main() {
    val sc = SocialConnectorK()
    val user1 = SocialUserK("user1")
    val user2 = SocialUserK("user2")
    val user3 = SocialUserK("user3")
    
    user1.addFollowers(listOf(user2, user3))
    sc.users.addAll(listOf(user1, user2, user3))
    
    val greedy = GreedyAlgorithmK(sc)
    val result = greedy.findMostFollowersPath("user1")
    println("Max followers: $result")
}
```

## Сложность

### Временная сложность

- **Жадный алгоритм:** O(n × m) - где n - глубина, m - среднее количество подписчиков
- **Полный перебор:** O(n^m) - экспоненциальная сложность

### Пространственная сложность

- **Жадный алгоритм:** O(d) - где d - глубина рекурсии
- **Полный перебор:** O(n^m) - для хранения всех путей

## Особенности

- **Эффективность:** Жадный алгоритм эффективен по времени и памяти
- **Ограничения:** Может не давать оптимальное решение
- **Гибкость:** Легко адаптировать для различных задач

## Применение

Жадные алгоритмы используются в:

- Задачах планирования
- Минимальном остовном дереве (алгоритм Краскала)
- Задачах о рюкзаке (дробный вариант)
- Кодировании Хаффмана
- Задачах покрытия

## Варианты задачи

### Вариант 1: Задача планирования

```java
public class ActivitySelection {
    public List<Activity> selectActivities(List<Activity> activities) {
        activities.sort(Comparator.comparing(Activity::getEndTime));
        
        List<Activity> selected = new ArrayList<>();
        Activity last = activities.get(0);
        selected.add(last);
        
        for (Activity activity : activities) {
            if (activity.getStartTime() >= last.getEndTime()) {
                selected.add(activity);
                last = activity;
            }
        }
        
        return selected;
    }
}
```

### Вариант 2: Задача о рюкзаке (дробный)

```java
public class FractionalKnapsack {
    public double getMaxValue(List<Item> items, int capacity) {
        items.sort((a, b) -> 
            Double.compare(b.getValue() / b.getWeight(), 
                          a.getValue() / a.getWeight())
        );
        
        double totalValue = 0;
        int remainingCapacity = capacity;
        
        for (Item item : items) {
            if (remainingCapacity >= item.getWeight()) {
                totalValue += item.getValue();
                remainingCapacity -= item.getWeight();
            } else {
                totalValue += item.getValue() * 
                    ((double) remainingCapacity / item.getWeight());
                break;
            }
        }
        
        return totalValue;
    }
}
```

## Когда использовать

### Используйте жадные алгоритмы, когда:

- Есть ограничения по времени или ресурсам
- Локально оптимальное решение приемлемо
- Нужна быстрая реализация
- Проблема имеет свойство жадного выбора

### Не используйте, когда:

- Нужно гарантированно оптимальное решение
- Локально оптимальный выбор не ведет к глобальному оптимуму
- Проблема не имеет свойства жадного выбора

## Заключение

В изменчивых и быстро меняющихся контекстах, таких как социальные сети, проблемы, требующие оптимального решения, могут стать ужасной химерой: труднодостижимой и в то же время нереалистичной.

Преодоление ограничений и оптимизация вызовов API - это отдельная тема, но, как мы уже говорили, жадные стратегии эффективны. Выбор такого подхода избавляет нас от многих проблем, давая взамен ценные результаты.

Имейте в виду, что не каждая ситуация подходит: нам нужно каждый раз оценивать наши обстоятельства.
