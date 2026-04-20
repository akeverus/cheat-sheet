---
title: "Bridge Pattern — Мост"
description: "Bridge (Мост) — структурный паттерн: отделяет абстракцию от реализации, позволяя изменять их независимо."
tags:
  - patterns
  - structural
  - bridge
  - design-patterns
difficulty: "intermediate"
updated: "2026-04-20"
---
# Bridge Pattern — Мост

**Bridge** отделяет **абстракцию** от **реализации**, чтобы обе стороны могли изменяться независимо. Вместо глубокой иерархии наследования — две независимые иерархии, связанные через композицию.

## Полезные ссылки

### Официальная документация
- [Bridge Pattern (Refactoring.Guru)](https://refactoring.guru/design-patterns/bridge) — описание паттерна с примерами

### См. также
- [Структурные паттерны](structural-patterns.md) — обзор структурных паттернов
- [Adapter Pattern](adapter.md) — схожий структурный паттерн
- [Composite Pattern](composite.md) — паттерн-дерево
- [Decorator Pattern](decorator.md) — добавление поведения через декоратор

## Содержание

- [Проблема](#проблема)
- [Структура](#структура)
- [Пример: отправка уведомлений](#пример-отправка-уведомлений)
- [Пример: JDBC как Bridge](#пример-jdbc-как-bridge)
- [Bridge vs Adapter](#bridge-vs-adapter)
- [Bridge в Spring](#bridge-в-spring)
- [Когда применять](#когда-применять)
- [See also](#see-also)

## Проблема

Без Bridge при добавлении новых измерений (платформа, вид, режим) число подклассов растёт мультипликативно:

```text
Shape
├── Circle
│   ├── RedCircle
│   └── BlueCircle
└── Square
    ├── RedSquare
    └── BlueSquare
```

Добавление третьего цвета удваивает дерево. Bridge разбивает это на две независимые иерархии.

## Структура

```text
Abstraction ────────────────> Implementor (interface)
    │                              │
    │ has-a (composition)          ├── ConcreteImplementorA
    │                              └── ConcreteImplementorB
    │
RefinedAbstraction
```

- **Abstraction** — высокоуровневый класс с ссылкой на Implementor.
- **RefinedAbstraction** — расширяет Abstraction.
- **Implementor** — интерфейс реализации (не обязательно совпадает с API Abstraction).
- **ConcreteImplementor** — конкретные реализации платформы/провайдера.

## Пример: отправка уведомлений

```java
// Implementor — интерфейс канала доставки
public interface NotificationSender {
    void send(String recipient, String message);
}

// ConcreteImplementorA — Email
public class EmailSender implements NotificationSender {
    private final EmailClient client;

    @Override
    public void send(String recipient, String message) {
        client.sendEmail(recipient, "Notification", message);
    }
}

// ConcreteImplementorB — SMS
public class SmsSender implements NotificationSender {
    private final SmsGateway gateway;

    @Override
    public void send(String recipient, String message) {
        gateway.sendSms(recipient, message);
    }
}

// ConcreteImplementorC — Slack
public class SlackSender implements NotificationSender {
    private final SlackClient slack;

    @Override
    public void send(String recipient, String message) {
        slack.postMessage(recipient, message);
    }
}

// Abstraction — тип уведомления
public abstract class Notification {
    protected final NotificationSender sender;

    protected Notification(NotificationSender sender) {
        this.sender = sender;
    }

    public abstract void notify(String recipient, String data);
}

// RefinedAbstraction — срочное уведомление
public class UrgentNotification extends Notification {
    public UrgentNotification(NotificationSender sender) {
        super(sender);
    }

    @Override
    public void notify(String recipient, String data) {
        sender.send(recipient, "URGENT: " + data);
    }
}

// RefinedAbstraction — ежедневная сводка
public class DailySummaryNotification extends Notification {
    private final List<String> items = new ArrayList<>();

    public DailySummaryNotification(NotificationSender sender) {
        super(sender);
    }

    public void addItem(String item) { items.add(item); }

    @Override
    public void notify(String recipient, String data) {
        String summary = "Daily summary:\n" + String.join("\n", items);
        sender.send(recipient, summary);
    }
}
```

```java
// Использование — комбинируем тип уведомления и канал независимо
Notification urgent = new UrgentNotification(new SmsSender(gateway));
urgent.notify("+7-999-123-45-67", "Server is down!");

Notification daily = new DailySummaryNotification(new EmailSender(emailClient));
((DailySummaryNotification) daily).addItem("Deploy completed");
daily.notify("admin@example.com", "");

// Смена канала без изменения типа уведомления
Notification slackUrgent = new UrgentNotification(new SlackSender(slackClient));
slackUrgent.notify("#alerts", "Memory > 95%");
```

## Пример: JDBC как Bridge

JDBC — классический пример Bridge в Java:

```java
// Abstraction — Connection (работа с БД)
Connection conn = DriverManager.getConnection(url, user, pass);
Statement stmt = conn.createStatement();

// Implementor — JDBC Driver (скрытая реализация для PostgreSQL, MySQL, H2)
// Переключение провайдера не меняет код приложения:
// jdbc:postgresql://... → PostgreSQL driver
// jdbc:mysql://...     → MySQL driver
// jdbc:h2:...          → H2 driver
```

## Bridge vs Adapter

| Критерий | Bridge | Adapter |
|----------|--------|---------|
| Цель | Спроектировать с учётом изменений | Адаптировать несовместимый интерфейс |
| Применение | Новый код | Интеграция существующего кода |
| Проектирование | Заранее (up-front) | После (retrofit) |
| Иерархии | Две независимые | Одна обёртка |

## Bridge в Spring

Spring использует Bridge неявно:

```java
// PlatformTransactionManager — Implementor
// Abstraction — TransactionTemplate / @Transactional
PlatformTransactionManager txManager = new DataSourceTransactionManager(dataSource);
// или: JpaTransactionManager, HibernateTransactionManager, KafkaTransactionManager

// Код приложения не меняется при смене провайдера транзакций
@Transactional
public void doWork() { ... }
```

Аналогично: `CacheManager` (EhCache, Redis, Caffeine), `MessageConverter`, `ResourceLoader`.

## Когда применять

**Подходит:**
- Нужно избежать мультипликативного роста иерархий (N типов × M платформ = N×M классов без Bridge, N+M с Bridge).
- Абстракция и реализация должны изменяться независимо.
- Реализация должна быть заменяема во время выполнения.

**Не подходит:**
- Единственная реализация — Bridge избыточен.
- Нет явного разделения на «высокоуровневую логику» и «платформу».

## See also

- [Adapter](adapter.md) — адаптирует существующий интерфейс
- [Структурные паттерны](structural-patterns.md) — обзор
- [Design Patterns Interview](../../interview/design-patterns/design-patterns-interview.md) — вопросы на собеседовании
- [Decorator](decorator.md) — добавляет поведение через обёртку
