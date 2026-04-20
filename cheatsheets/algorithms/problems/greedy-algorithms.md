---
title: "Жадные алгоритмы (Greedy Algorithms)"
description: "Локально оптимальный выбор на каждом шаге без отката. Пример: выбор пользователя с максимумом подписчиков при ограничении на число запросов API (социальная сеть). Реализация на Java и Kotlin, сравнение с полным перебором, варианты (планирование, дробный рюкзак)."
tags:
  - algorithms
  - problems
  - greedy-algorithms
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Жадные алгоритмы (Greedy Algorithms)

Локально оптимальный выбор на каждом шаге без отката. Пример: выбор пользователя с максимумом подписчиков при ограничении на число запросов API (социальная сеть). Реализация на Java и Kotlin, сравнение с полным перебором, варианты (планирование, дробный рюкзак).

## Полезные ссылки

### Официальная документация
- [Baeldung: Greedy Algorithms](https://www.baeldung.com/)

### См. также
- [Задача о рюкзаке](knapsack-problem.md) — knapsack
- [Задача коммивояжера](traveling-salesman-problem.md) — TSP
- [Обзор жадной парадигмы](greedy-algorithms.md) — теория и классические задачи

- [Методы ветвей и границ (Branch and Bound)](../algorithmic-paradigms/branch-and-bound.md)
- [Поиск с возвратом (Backtracking)](../algorithmic-paradigms/backtracking.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Пример: Социальная сеть](#пример-социальная-сеть)
  - [Проблема с ограничениями API](#проблема-с-ограничениями-api)
  - [Проблема с традиционным подходом](#проблема-с-традиционным-подходом)
  - [Решение с жадным алгоритмом](#решение-с-жадным-алгоритмом)
- [Реализация на Java](#реализация-на-java)
- [Сравнение с другими подходами](#сравнение-с-другими-подходами)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Задача планирования](#вариант-1-задача-планирования)
  - [Вариант 2: Дробный рюкзак](#вариант-2-дробный-рюкзак)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Жадный алгоритм выбирает локально лучший вариант на каждом шаге без отката. Это часто дает быстрый и практически полезный результат, даже когда глобальный оптимум не гарантирован.

Фокус этого документа: прикладной кейс с ограничением внешнего API, где важен баланс между качеством ответа и количеством запросов. Теоретическое обоснование и классические задачи собраны в `../algorithmic-paradigms/greedy-algorithms.md`.

## Пример: Социальная сеть

В этом коротком руководстве мы собираемся реализовать жадную стратегию для извлечения данных из социальной сети с использованием ее **API**.

Допустим, мы хотим охватить больше пользователей в социальной сети. Лучший способ достичь нашей цели — опубликовать оригинальный контент или ретвитнуть что-то, что вызовет интерес у широкой аудитории.

Как найти такую аудиторию? Что ж, мы должны найти учетную запись с большим количеством подписчиков и твитнуть для них какой-нибудь контент.

### Проблема с ограничениями API

Учитываем следующую ситуацию: На нашем аккаунте четыре подписчика, у каждого из которых соответственно 2, 2, 1 и 3 подписчика и так далее.

С этой целью мы возьмем того, у кого больше подписчиков среди подписчиков нашего аккаунта. Затем мы повторим процесс еще два раза, пока не достигнем 3-й степени связи (всего четыре шага).

Таким образом, мы определяем путь, состоящий из пользователей, который ведет нас к самой обширной базе подписчиков из нашей учетной записи.

### Проблема с традиционным подходом

Мы можем начать с «традиционного» подхода. На каждом этапе мы будем выполнять запрос, чтобы получить подписчиков учетной записи. В результате нашего процесса отбора количество учетных записей будет увеличиваться с каждым шагом.

Удивительно, но в итоге мы выполнили 25 запросов.

Проблема: например, Twitter API ограничивает число запросов (например, 15 за 15 минут). Если мы попытаемся выполнить больше вызовов, чем разрешено, мы получим ошибку превышения лимита скорости.

### Решение с жадным алгоритмом

Что ж, ответ прямо перед нами: жадный алгоритм. Если мы используем этот подход, на каждом этапе мы можем предположить, что пользователь с наибольшим количеством подписчиков является единственным, кого следует учитывать: в конце концов нам нужно всего четыре запроса. Несомненное улучшение!

Результат этих двух подходов будет разным. В первом случае мы получаем 16, оптимальное решение, а во втором максимальное количество достижимых последователей составляет всего 12.

Будет ли эта разница столь ценной? Мы решим позже.

## Реализация на Java

Имитация API со счётчиком запросов; коннектор возвращает подписчиков по имени. Жадная стратегия: на каждом шаге выбираем пользователя с максимальным числом подписчиков и идём только по нему (4 запроса вместо 25 при полном переборе).

```java
// Коннектор к социальной сети: имитация API и жадный выбор пользователя с макс. подписчиками
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


```java
// Пользователь: имя и список подписчиков (followers)
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

На каждом вызове выбирается один пользователь с максимумом подписчиков — без отката. Нежадный подход (полный перебор по всем подписчикам) даёт оптимальный результат, но требует 25 запросов и упирается в лимит API.

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

Жадный вариант даёт 12 за 4 запроса; полный перебор — 16, но требует 25 запросов и при лимите API недоступен. Итог: жадная стратегия даёт приемлемый результат в условиях ограничений (локальный оптимум).

## Реализация на Kotlin

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

Жадный: время O(n×m) (глубина × среднее число подписчиков), память O(d). Полный перебор: O(n^m) по времени и памяти.

## Особенности

Жадный алгоритм эффективен по времени и памяти, но может не давать глобального оптимума; легко адаптируется под задачу.

## Применение

Планирование заданий, минимальное остовное дерево (Краскал), дробный рюкзак, кодирование Хаффмана, задачи покрытия.

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

### Вариант 2: Дробный рюкзак

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

Жадные алгоритмы уместны при ограничениях по времени или ресурсам, когда приемлем локальный оптимум и задача допускает жадный выбор (дробный рюкзак — да, целочисленный — нет). Не стоит применять, когда нужна гарантированно глобально оптимальное решение и свойство жадного выбора не выполняется.

## Лучшие практики

Проверяйте или доказывайте, даёт ли жадный выбор оптимальный результат для данной задачи. Критерий выбора должен быть однозначным (сортировка по удельной ценности, по дедлайну и т.д.). При лимитах API или ресурсов жадный подход часто сокращает число запросов. Тестируйте на малых примерах и граничных случаях. В коде и документации явно указывайте, что используется жадная стратегия и при каких условиях результат приемлем или оптимален.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Результат хуже оптимального | Задача не имеет свойства жадного выбора | Проверить на контрпримерах; рассмотреть ДП или перебор |
| Превышение лимита API/время | Слишком много запросов или итераций | Усилить жадную стратегию (один выбор на шаг); ограничить глубину |
| Нестабильное качество | Критерий выбора неоднозначен или неудачен | Чётко определить критерий (сортировка, приоритет); сравнить с эталоном на тестах |

## Частые вопросы

**Когда жадный алгоритм даёт оптимум?** Когда задача обладает свойством жадного выбора и оптимальной подструктурой (например, дробный рюкзак, выбор активностей по окончанию). Для целочисленного рюкзака жадный по удельной ценности не гарантирует оптимум.

**Чем жадный подход лучше полного перебора?** Меньше запросов/итераций и памяти; при жёстких лимитах (API, время) полный перебор может быть недоступен, а жадный даёт приемлемый результат.

**Как проверить, подходит ли задача для жадного алгоритма?** Построить контрпримеры; доказать свойство жадного выбора или сравнить на тестах с известным оптимумом.

## Заключение

Жадные алгоритмы дают быстрые и часто приемлемые решения при ограничениях (API, время, память). Результат может быть локально, но не глобально оптимальным; применимость нужно оценивать для каждой задачи.
