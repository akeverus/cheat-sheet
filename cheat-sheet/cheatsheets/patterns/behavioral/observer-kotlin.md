# Observer (Наблюдатель) - Kotlin реализация

Паттерн «Наблюдатель» — поведенческий паттерн, впервые описанный в книге GoF. Он определяет механизм подписки, который уведомляет несколько объектов о любых изменениях состояния наблюдаемого объекта. Это делает его особенно популярным в событийно-ориентированных системах. В этом руководстве рассматривается реализация паттерна на Kotlin.

**Дата последнего обновления:** 2025-01-15

## Содержание

- [Полезные ссылки](#�-олезн�-е-�-�-�-лки)
  - [Официальная документация](#�-�-и�-иал�-ная-док�-мен�-а�-ия)
  - [Baeldung](#baeldung)
  - [См. также](#�-м-�-акже)
- [Оглавление](#�-главление)
- [Kotlin Implementation](#kotlin-implementation)
  - [Классическая реализация (GoF)](#�-ла�-�-и�-е�-кая-�-еализа�-ия-gof)



## Полезные ссылки

### Официальная документация
- [Design Patterns: Elements of Reusable Object-Oriented Software](https://en.wikipedia.org/wiki/Design_Patterns)
- [Kotlin Delegates](https://kotlinlang.org/docs/delegation.html)

### Baeldung
- [Observer Pattern in Java](https://www.baeldung.com/java-observer-pattern)
- [Observer Pattern in Kotlin](https://www.baeldung.com/kotlin-observer-pattern)

### См. также
- [Observer (Наблюдатель) - Java реализация](observer.md)
- [Mediator (Посредник)](mediator.md)

## Оглавление

- [Введение](#введение)
- [Kotlin Implementation](#kotlin-implementation)
- [Java Implementation](#java-implementation)

## Kotlin Implementation

### Классическая реализация (GoF)

Первый интерфейс `IObserver` определяет действие обновления:

```kotlin
interface IObserver {
    fun update()
}
```

Второй интерфейс `IObservable` управляет списком наблюдателей и рассылкой событий:

```kotlin
interface IObservable {
    val observers: MutableList<IObserver>

    fun add(observer: IObserver) {
        observers.add(observer)
    }

    fun remove(observer: IObserver) {
        observers.remove(observer)
    }

    fun sendUpdateEvent() {
        observers.forEach { it.update() }
    }
}
```

Пример наблюдаемого объекта:

```kotlin
class BaeldungNewsletter : IObservable {
    override val observers: MutableList<IObserver> = mutableListOf()

    var newestArticleUrl: String = ""
        set(value) {
            field = value
            sendUpdateEvent()
        }
}
```

Пример наблюдателя:

```kotlin
class BaeldungReader(private val newsletter: BaeldungNewsletter) : IObserver {
    override fun update() {
        println("New Baeldung article: ${newsletter.newestArticleUrl}")
    }
}
```

## Реализация через Delegates.observable

В Kotlin можно использовать наблюдаемые делегаты и хранить подписчиков как список лямбда-функций:

```kotlin
class BaeldungNewsletter {
    val newestArticleObservers = mutableListOf<(String) -> Unit>()

    var newestArticleUrl: String by Delegates.observable("") { _, _, newValue ->
        newestArticleObservers.forEach { it(newValue) }
    }
}
```

Подписка:

```kotlin
val newsletter = BaeldungNewsletter()
newsletter.newestArticleObservers.add { url ->
    println("New Baeldung article: $url")
}
```

## Java Implementation

В Java паттерн Observer может быть реализован следующим образом:

```java
public class NewsAgency {
    private String news;
    private final List<Channel> channels = new ArrayList<>();

    public void addObserver(Channel channel) {
        channels.add(channel);
    }

    public void removeObserver(Channel channel) {
        channels.remove(channel);
    }

    public void setNews(String news) {
        this.news = news;
        for (Channel channel : channels) {
            channel.update(this.news);
        }
    }
}

public interface Channel {
    void update(String news);
}

public class NewsChannel implements Channel {
    private String news;

    @Override
    public void update(String news) {
        this.news = news;
    }

    public String getNews() {
        return news;
    }
}
```

Использование:

```java
NewsAgency observable = new NewsAgency();
NewsChannel observer = new NewsChannel();

observable.addObserver(observer);
observable.setNews("news");

assertEquals(observer.getNews(), "news");
```

### PropertyChangeListener (рекомендуемый вариант)

Для новых проектов лучше использовать `PropertyChangeSupport`:

```java
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCLNewsAgency {
    private String news;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setNews(String value) {
        String oldValue = this.news;
        this.news = value;
        support.firePropertyChange("news", oldValue, value);
    }
}

public class PCLNewsChannel implements PropertyChangeListener {
    private String news;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.news = (String) evt.getNewValue();
    }
}
```
