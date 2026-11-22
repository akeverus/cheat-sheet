---
title: "Вопросы на собеседовании: RabbitMQ"
description: "Полное покрытие RabbitMQ для интервью: AMQP протокол, exchanges, queues, bindings, acknowledgements, DLQ, Publisher Confirms, кластеризация, quorum queues, Spring AMQP, паттерны и мониторинг."
tags:
  - interview
  - messaging
  - rabbitmq-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "RabbitMQ"
  - "RabbitMQ interview"
  - "RabbitMQ собеседование"
prerequisites:
  - "[[rabbitmq]]"
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: `RabbitMQ`

Полное покрытие `RabbitMQ` для интервью: `AMQP`-протокол, типы `exchange`, очереди и их свойства, подтверждения доставки, `Dead Letter Queue`, `Publisher Confirms`, кластеризация, `quorum queues`, `Spring AMQP`, основные паттерны и мониторинг.

**`RabbitMQ`** — брокер сообщений, реализующий протокол `AMQP 0-9-1`. Широко применяется в [микросервисных](../architecture/microservices-interview.md) и [event-driven](../architecture/event-driven-patterns-interview.md) архитектурах для надёжной асинхронной коммуникации между сервисами.

## Полезные ссылки

### Официальная документация

- [RabbitMQ Tutorials](https://www.rabbitmq.com/tutorials) — официальные туториалы по всем паттернам
- [AMQP 0-9-1 Model Explained](https://www.rabbitmq.com/tutorials/amqp-concepts) — концепции протокола
- [RabbitMQ Reliability Guide](https://www.rabbitmq.com/docs/reliability) — гарантии доставки и надёжность
- [Quorum Queues](https://www.rabbitmq.com/docs/quorum-queues) — современный тип HA-очередей
- [Streams and Superstreams](https://www.rabbitmq.com/docs/streams) — журнал, replay, retention и партиционирование
- [RabbitMQ vs Kafka](https://www.rabbitmq.com/docs/compare/kafka) — сравнение с учётом Streams

### Статьи Baeldung

- [Introduction to RabbitMQ](https://www.baeldung.com/rabbitmq) — введение в RabbitMQ
- [RabbitMQ vs Kafka](https://www.baeldung.com/rabbitmq-vs-kafka) — детальное сравнение двух брокеров
- [Messaging Using Spring AMQP](https://www.baeldung.com/spring-amqp) — интеграция с Spring
- [RabbitMQ Dead Letter Exchanges](https://www.baeldung.com/rabbitmq-dead-letter-exchanges) — настройка DLX/DLQ
- [Publisher Confirms with Spring AMQP](https://www.baeldung.com/spring-amqp-publisher-confirms) — подтверждения публикации
- [Exchanges, Queues, and Bindings in RabbitMQ](https://www.baeldung.com/java-rabbitmq-exchanges-queues-bindings) — exchanges, очереди и bindings
- [Channels and Connections in RabbitMQ](https://www.baeldung.com/java-rabbitmq-channels-connections) — управление соединениями
- [Error Handling with Spring AMQP](https://www.baeldung.com/spring-amqp-error-handling) — обработка ошибок и retry

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы AMQP**
- [Q1. (!) Что такое RabbitMQ и протокол AMQP?](#q1--что-такое-rabbitmq-и-протокол-amqp)
- [Q2. (!) Из каких компонентов состоит модель AMQP?](#q2--из-каких-компонентов-состоит-модель-amqp)
- [Q3. Что такое Virtual Host (vhost)?](#q3-что-такое-virtual-host-vhost)
- [Q4. Что такое Channel и зачем он нужен?](#q4-что-такое-channel-и-зачем-он-нужен)

**Exchanges**
- [Q5. (!) Какие типы Exchange существуют в RabbitMQ?](#q5--какие-типы-exchange-существуют-в-rabbitmq)
- [Q6. Как работает Direct Exchange?](#q6-как-работает-direct-exchange)
- [Q7. Как работает Fanout Exchange?](#q7-как-работает-fanout-exchange)
- [Q8. (!) Как работает Topic Exchange?](#q8--как-работает-topic-exchange)
- [Q9. Как работает Headers Exchange?](#q9-как-работает-headers-exchange)
- [Q10. Что такое Default Exchange?](#q10-что-такое-default-exchange)

**Queues**
- [Q11. (!) Какие свойства у очереди в RabbitMQ?](#q11--какие-свойства-у-очереди-в-rabbitmq)
- [Q12. Что такое durable queue и persistent message?](#q12-что-такое-durable-queue-и-persistent-message)
- [Q13. Что такое exclusive и auto-delete очередь?](#q13-что-такое-exclusive-и-auto-delete-очередь)
- [Q14. Как настроить TTL для сообщений и очереди?](#q14-как-настроить-ttl-для-сообщений-и-очереди)
- [Q15. Что такое максимальная длина очереди (max-length)?](#q15-что-такое-максимальная-длина-очереди-max-length)

**Bindings и Routing**
- [Q16. (!) Что такое Binding и Routing Key?](#q16--что-такое-binding-и-routing-key)
- [Q17. Можно ли привязать несколько очередей к одному Exchange?](#q17-можно-ли-привязать-несколько-очередей-к-одному-exchange)

**Acknowledgements**
- [Q18. (!) Что такое acknowledgement (ack) в RabbitMQ?](#q18--что-такое-acknowledgement-ack-в-rabbitmq)
- [Q19. Чем отличаются nack и reject?](#q19-чем-отличаются-nack-и-reject)
- [Q20. (!) В чём разница между auto-ack и manual ack?](#q20--в-чём-разница-между-auto-ack-и-manual-ack)
- [Q21. Что такое Publisher Confirms?](#q21-что-такое-publisher-confirms)
- [Q22. Чем Publisher Confirms отличаются от транзакций AMQP?](#q22-чем-publisher-confirms-отличаются-от-транзакций-amqp)

**Dead Letter Queue**
- [Q23. (!) Что такое Dead Letter Exchange (DLX) и DLQ?](#q23--что-такое-dead-letter-exchange-dlx-и-dlq)
- [Q24. При каких условиях сообщение попадает в DLQ?](#q24-при-каких-условиях-сообщение-попадает-в-dlq)
- [Q25. Как реализовать retry с использованием DLX и TTL?](#q25-как-реализовать-retry-с-использованием-dlx-и-ttl)

**QoS и Prefetch**
- [Q26. (!) Что такое prefetch count и QoS?](#q26--что-такое-prefetch-count-и-qos)
- [Q27. Как prefetch влияет на производительность и нагрузку?](#q27-как-prefetch-влияет-на-производительность-и-нагрузку)

**Кластеризация и High Availability**
- [Q28. (!) Как работает кластеризация в RabbitMQ?](#q28--как-работает-кластеризация-в-rabbitmq)
- [Q29. Что такое Mirrored Queues (Classic HA)?](#q29-что-такое-mirrored-queues-classic-ha)
- [Q30. (!) Что такое Quorum Queues и чем они лучше Mirrored?](#q30--что-такое-quorum-queues-и-чем-они-лучше-mirrored)
- [Q31. Что такое Federation и Shovel в RabbitMQ?](#q31-что-такое-federation-и-shovel-в-rabbitmq)

**Spring AMQP**
- [Q32. (!) Что такое Spring AMQP и RabbitTemplate?](#q32--что-такое-spring-amqp-и-rabbittemplate)
- [Q33. Как объявить очередь и exchange через Spring AMQP?](#q33-как-объявить-очередь-и-exchange-через-spring-amqp)
- [Q34. (!) Как настроить @RabbitListener?](#q34--как-настроить-rabbitlistener)
- [Q35. Как настроить retry-логику в Spring AMQP?](#q35-как-настроить-retry-логику-в-spring-amqp)

**Паттерны**
- [Q36. (!) Какие основные паттерны обмена сообщениями реализуются в RabbitMQ?](#q36--какие-основные-паттерны-обмена-сообщениями-реализуются-в-rabbitmq)
- [Q37. Как реализовать паттерн RPC через RabbitMQ?](#q37-как-реализовать-паттерн-rpc-через-rabbitmq)

**Мониторинг**
- [Q38. Как мониторить RabbitMQ через Management Plugin?](#q38-как-мониторить-rabbitmq-через-management-plugin)
- [Q39. Какие метрики RabbitMQ наиболее важны?](#q39-какие-метрики-rabbitmq-наиболее-важны)

**RabbitMQ vs Kafka**
- [Q40. (!) Чем Kafka отличается от очередей и Streams в RabbitMQ?](#q40--чем-kafka-отличается-от-очередей-и-streams-в-rabbitmq)
- [Q41. Когда выбрать RabbitMQ, а когда Kafka?](#q41-когда-выбрать-rabbitmq-а-когда-kafka)
- [Q42. Чем RabbitMQ Streams отличаются от обычной очереди и когда они нужны?](#q42-чем-rabbitmq-streams-отличаются-от-обычной-очереди-и-когда-они-нужны)

---

## Q1. (!) Что такое RabbitMQ и протокол AMQP?

**`RabbitMQ`** — это брокер сообщений с открытым исходным кодом, который реализует протокол `AMQP 0-9-1` (Advanced Message Queuing Protocol). Producer-ы кладут сообщения в брокер, consumer-ы их забирают, а брокер берёт на себя хранение, маршрутизацию и гарантии доставки между ними.

Написан на `Erlang/OTP` — языке, созданном для телеком-систем с требованием почти непрерывной работы. Отсюда два практических следствия: брокер устойчив к сбоям отдельных процессов (supervision trees) и легко собирается в кластер. Обратная сторона — диагностируется он не JVM-инструментами, а `rabbitmq-diagnostics`; Java-тюнинг (Heap, GC) к нему неприменим.

**`AMQP 0-9-1`** — бинарный протокол прикладного уровня для асинхронного обмена сообщениями. Что он даёт:
- **Надёжность** — AMQP 0-9-1 определяет подтверждения от consumer-а. `Publisher Confirms` от брокера — расширение RabbitMQ к AMQP 0-9-1.
- **Гибкая маршрутизация** — сообщение публикуется не в очередь напрямую, а в `exchange`, который по правилам `binding` решает, в какие очереди его положить.
- **Безопасность** — `TLS` для шифрования канала, `SASL` для аутентификации.
- **Совместимость** — это открытый стандарт, не привязанный к конкретной реализации.

Начиная с RabbitMQ 4.0, `AMQP 1.0` поддерживается брокером напрямую. Он отличается от AMQP 0-9-1 и требует соответствующего клиента. MQTT и STOMP доступны через плагины. [AMQP 1.0 в RabbitMQ 4.3](https://www.rabbitmq.com/docs/4.3/amqp), [расширения AMQP 0-9-1](https://www.rabbitmq.com/docs/4.3/extensions).

---

## Q2. (!) Из каких компонентов состоит модель AMQP?

```mermaid
graph LR
    P[Producer] -->|publish| E[Exchange]
    E -->|binding + routing key| Q1[Queue 1]
    E -->|binding + routing key| Q2[Queue 2]
    Q1 -->|consume| C1[Consumer 1]
    Q2 -->|consume| C2[Consumer 2]
```

Ключевая идея модели: producer **никогда не пишет в очередь напрямую**. Он публикует сообщение в `exchange`, а тот по правилам `binding` раскладывает его по очередям. Это и есть источник гибкости `AMQP` — связь «кто отправил» и «кто получит» развязана.

| Компонент | Роль |
|-----------|----------|
| `Producer` | Публикует сообщения в `exchange` (не в очередь) |
| `Exchange` | Принимает сообщения и маршрутизирует их в очереди по `binding` |
| `Queue` | Буфер: хранит сообщения, пока их не заберёт `consumer` |
| `Binding` | Правило связи `exchange → queue` (с каким `routing key`) |
| `Consumer` | Читает сообщения из очереди |
| `Channel` | Лёгкое логическое соединение внутри одного `AMQP`-коннекшна |
| `Virtual Host` | Логическая изоляция ресурсов брокера друг от друга |

---

## Q3. Что такое Virtual Host (vhost)?

**`Virtual Host`** (`vhost`) — логическая изоляция ресурсов внутри одного `RabbitMQ`-сервера. Хорошая аналогия — отдельная база данных внутри `PostgreSQL`: один физический сервер, но независимые пространства имён.

- Каждый `vhost` имеет собственные `exchange`, `queue` и `binding`. Пользователи существуют на уровне брокера, а разрешения `configure`, `write`, `read` назначают пользователю отдельно для каждого vhost. Очередь `orders` в `/app-a` и очередь `orders` в `/app-b` — это разные очереди.
- По умолчанию создаётся `vhost` `/`.
- **Сценарий применения:** несколько приложений (или окружений — dev/stage) делят один брокер, но не видят и не задевают ресурсы друг друга. Права выдаются на конкретный `vhost`.

```java
// Подключение к конкретному vhost
ConnectionFactory factory = new ConnectionFactory();
factory.setVirtualHost("/my-app");
```

[Виртуальные хосты RabbitMQ](https://www.rabbitmq.com/docs/vhosts), [управление доступом](https://www.rabbitmq.com/docs/access-control).

---

## Q4. Что такое Channel и зачем он нужен?

**`Channel`** — лёгкое логическое соединение поверх одного физического TCP-соединения (`Connection`). Все операции `AMQP` (publish, consume, declare) идут через `channel`, а не напрямую через `Connection`.

**Зачем он нужен.** Открытие TCP-соединения — дорогая операция (handshake, TLS, FD на сервере). Если бы каждому потоку приложения требовался свой `Connection`, брокер быстро упёрся бы в лимит сокетов. `Channel` решает это через мультиплексирование: одно TCP-соединение несёт множество независимых логических потоков.

**Подводные камни:**
- `Channel` **не thread-safe**: каждый поток приложения должен работать со своим `channel`, иначе данные перемешаются.
- В одном `Connection` можно открыть тысячи `channel` — это дёшево, в отличие от самих соединений.

**Жизненный цикл:** открыть `Connection` → открыть `Channel` → выполнять операции → закрыть `Channel` → (при завершении) закрыть `Connection`.

---

## Q5. (!) Какие типы Exchange существуют в RabbitMQ?

```mermaid
graph LR
    P["Публикация: eu.auth.error, format=json"] --> E["Exchange: тип выбирается"]
    E --> A["Очередь A: точный ключ eu.auth.error"]
    E --> B["Очередь B: шаблон eu.#"]
    E --> C["Очередь C: условие format=json"]
    E --> D["Очередь D: привязка без условия"]
```

Тип `exchange` определяет **алгоритм маршрутизации** — как именно брокер решает, в какие очереди положить сообщение. AMQP 0-9-1 определяет четыре базовых типа. Для каждой строки таблицы представьте отдельную настройку exchange с подходящими привязками. RabbitMQ также поддерживает дополнительные типы через плагины. [RabbitMQ Exchanges](https://www.rabbitmq.com/docs/exchanges)

| Тип | По чему маршрутизирует | Когда применять |
|-----|---------------|------------|
| `direct` | Точное совпадение `routing key` с `binding key` | Простая очередь задач, маршрутизация по типу события |
| `fanout` | Игнорирует ключ, шлёт во **все** привязанные очереди | Pub/Sub, broadcast-рассылки |
| `topic` | Паттерн `routing key` с `*` и `#` | Логи по категориям, события с иерархией |
| `headers` | По заголовкам сообщения, а не по `routing key` | Маршрутизация по нескольким атрибутам сразу |

---

## Q6. Как работает Direct Exchange?

**`Direct Exchange`** доставляет сообщение в те очереди, у которых `binding key` **точно совпадает** (посимвольно) с `routing key` сообщения. Никаких паттернов — только строгое равенство строк.

```mermaid
graph LR
    P[Producer] -->|routing_key=error| E[Direct Exchange]
    E -->|binding=error| Q1[error-queue]
    E -->|binding=info| Q2[info-queue]
```

```java
// Объявление direct exchange
channel.exchangeDeclare("logs.direct", "direct", true);
// Привязка очереди
channel.queueBind("error-queue", "logs.direct", "error");
// Публикация
channel.basicPublish("logs.direct", "error", null, body);
```

Если `routing key` не совпал ни с одним `binding`, сообщение по умолчанию **просто отбрасывается** (его никто не получит, и producer об этом не узнает). Чтобы такие сообщения не терялись, очереди-«заглушки» назначают через `alternate exchange`. Несколько очередей могут иметь один и тот же `binding key` — тогда сообщение получит каждая из них.

---

## Q7. Как работает Fanout Exchange?

**`Fanout Exchange`** полностью игнорирует `routing key` и копирует каждое сообщение во **все** привязанные к нему очереди.

**Сценарий применения:** broadcast и Pub/Sub, когда одно событие должно одновременно дойти до нескольких независимых подписчиков. Классический пример — уведомление, которое надо отправить и в push, и на email, и по SMS.

```java
channel.exchangeDeclare("notifications", "fanout", true);
channel.queueBind("mobile-queue", "notifications", "");
channel.queueBind("email-queue",  "notifications", "");
channel.queueBind("sms-queue",    "notifications", "");

channel.basicPublish("notifications", "order.created", null, body);
```

Каждая привязанная очередь получает собственную копию сообщения — подписчики не конкурируют за него, а обрабатывают независимо.

---

## Q8. (!) Как работает Topic Exchange?

**`Topic Exchange`** — это `direct` с поддержкой шаблонов: очереди подписываются не на точный ключ, а на **паттерн**. `Routing key` разбивается на слова, разделённые точкой (`region.service.severity`), и в `binding key` можно использовать два спецсимвола:
- `*` — заменяет ровно **одно** слово;
- `#` — заменяет **ноль или более** слов.

Это даёт иерархическую маршрутизацию: одно событие может одновременно попасть и в узкую очередь («ошибки auth в Европе»), и в широкую («всё из Европы»), и в сквозную («все ошибки»). По каким из четырёх привязок будет доставлено сообщение с ключом `eu.auth.error`?

```mermaid
graph LR
    P[Producer] -->|"eu.auth.error"| E[Topic Exchange]
    E -->|"*.*.error"| Q1[all-errors]
    E -->|"eu.#"| Q2[eu-all]
    E -->|"eu.auth.*"| Q3[eu-auth-logs]
    E -->|"us.#"| Q4[us-all]
```

```java
channel.exchangeDeclare("logs.topic", "topic", true);
channel.queueBind("all-errors",   "logs.topic", "*.*.error");
channel.queueBind("eu-all",       "logs.topic", "eu.#");
channel.queueBind("eu-auth-logs", "logs.topic", "eu.auth.*");

channel.queueBind("us-all", "logs.topic", "us.#");
channel.basicPublish("logs.topic", "eu.auth.error", null, body);
```

Сообщение получат `all-errors`, `eu-all` и `eu-auth-logs`. Привязка `us.#` требует первый сегмент `us` и не совпадает.

---

## Q9. Как работает Headers Exchange?

**`Headers Exchange`** маршрутизирует не по `routing key` (он игнорируется), а по **заголовкам сообщения** (`AMQP message headers`). Полезен, когда решение о доставке зависит сразу от нескольких атрибутов, которые неудобно ужать в одну строку-ключ.

Логику совпадения задаёт аргумент `x-match` при привязке:
- `all` — должны совпасть **все** указанные заголовки (логическое И);
- `any` — достаточно совпадения **хотя бы одного** (логическое ИЛИ).

```java
Map<String, Object> bindingArgs = new HashMap<>();
bindingArgs.put("x-match", "all");
bindingArgs.put("format", "pdf");
bindingArgs.put("type", "report");

channel.queueBind("pdf-reports", "docs.exchange", "", bindingArgs);

// Публикация с заголовками
AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .headers(Map.of("format", "pdf", "type", "report"))
    .build();
channel.basicPublish("docs.exchange", "", props, body);
```

**Подводный камень:** на практике применяется редко. `Topic Exchange` покрывает большинство сценариев маршрутизации, читается проще и работает быстрее, поэтому `headers` берут только когда атрибутов действительно много и они не складываются в иерархический ключ.

---

## Q10. Что такое Default Exchange?

**`Default Exchange`** (`""`) — безымянный системный `direct exchange`, который существует всегда. Его особенность: **каждая** очередь автоматически привязана к нему с `binding key`, равным имени самой очереди. Поэтому, публикуя в `""` с `routing key = "task-queue"`, вы фактически отправляете сообщение прямо в очередь `task-queue` без явного объявления `exchange` и `binding`.

```java
// Прямая отправка в очередь "task-queue" без явного объявления exchange
channel.basicPublish("", "task-queue", null, body);
```

**Компромисс:** удобен для быстрых прототипов и простых очередей задач, но скрывает маршрутизацию (имя очереди жёстко вшито в код producer-а). В продакшне обычно объявляют свой `exchange` явно — так топологию видно и её легко менять, не трогая отправителей.

---

## Q11. (!) Какие свойства у очереди в RabbitMQ?

У очереди есть три булевых флага, определяющих её время жизни, и расширяемая карта `arguments` для тонкой настройки. Флаги задаются при объявлении и **неизменяемы**: чтобы поменять их, очередь придётся удалить и пересоздать.

| Свойство | Тип | Что делает |
|----------|-----|----------|
| `durable` | boolean | Очередь (как объект) переживает перезапуск брокера. Без неё пропадает само определение очереди |
| `exclusive` | boolean | Очередь видна только создавшему её `Connection` и удаляется при его закрытии |
| `autoDelete` | boolean | Очередь удаляется, когда у неё был хотя бы один подписанный consumer и последний consumer исчез. Опрос через `basic.get` сам по себе это условие не создаёт. |
| `arguments` | Map | Расширенные параметры: `x-message-ttl`, `x-max-length`, `x-dead-letter-exchange`, `x-queue-type` и др. |

```java
Map<String, Object> args = new HashMap<>();
args.put("x-message-ttl", 60000);       // TTL 60 секунд
args.put("x-max-length", 10000);         // макс. 10 000 сообщений
args.put("x-dead-letter-exchange", "dlx");

channel.queueDeclare(
    "my-queue",
    true,   // durable
    false,  // exclusive
    false,  // autoDelete
    args
);
```

[Свойства очередей RabbitMQ](https://www.rabbitmq.com/docs/queues).

---

## Q12. Что такое durable queue и persistent message?

`durable` задаёт сохранность определения очереди при перезапуске. `deliveryMode=2` помечает публикацию как persistent. Эти параметры применяются по-разному для разных типов очередей:

- **Classic queue.** После перезапуска сохраняются принятые persistent-сообщения в durable-очереди. Transient-сообщения (`deliveryMode=1`) восстановлению не подлежат, даже если временно были записаны на диск. В RabbitMQ 4.3 неэксклюзивные transient classic queues по умолчанию отключены.
- **Quorum queue.** Очередь всегда durable, а сообщения записываются на диск независимо от `deliveryMode`. При dead-lettering исходный delivery mode сохраняется, поэтому в целевой classic queue он снова важен.

```java
AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .deliveryMode(2)
    .build();
channel.basicPublish("my-exchange", "key", props, body);
```

Сам `deliveryMode` не сообщает издателю, была ли публикация принята и маршрутизирована. Для границы приёма используйте publisher confirms (Q21). Для quorum queue подтверждение требует принятия публикации кворумом реплик, а устойчивость к отказу зависит от сохранности этого кворума. [Очереди](https://www.rabbitmq.com/docs/4.3/queues), [Quorum Queues](https://www.rabbitmq.com/docs/4.3/quorum-queues).

---

## Q13. Что такое exclusive и auto-delete очередь?

Обе очереди — временные и удаляются автоматически, но триггеры удаления у них разные: `exclusive` смотрит на соединение, `auto-delete` — на consumer-ов.

**`exclusive`** очередь:
- Видна **только** создавшему её `Connection` (другие соединения к ней не подключатся);
- удаляется автоматически при закрытии этого `Connection`;
- **Сценарий применения:** временные reply-очереди в RPC, где ответ нужен только текущему клиенту.

**`auto-delete`** очередь:
- Удаляется, когда от неё отписался **последний** `consumer`;
- если consumer-ов не было вообще ни разу — очередь не удаляется сразу, а ждёт первого подключения;
- **Сценарий применения:** динамические подписки, живущие ровно столько, сколько есть слушатели.

---

## Q14. Как настроить TTL для сообщений и очереди?

`TTL` (time-to-live) ограничивает время жизни. Его можно задать на трёх уровнях, и важно их не путать: два управляют временем жизни **сообщений**, а третий — самой **очереди**.

**1. TTL на уровне очереди** (`x-message-ttl`) — единое время жизни для всех сообщений в этой очереди:

```java
Map<String, Object> args = new HashMap<>();
args.put("x-message-ttl", 30000); // 30 секунд
channel.queueDeclare("ttl-queue", true, false, false, args);
```

**2. TTL на уровне сообщения** (`expiration`) — индивидуальный срок для конкретного сообщения. Если заданы оба, действует **меньший** из двух:

```java
AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .expiration("30000") // строка в миллисекундах
    .build();
channel.basicPublish("", "ttl-queue", props, body);
```

**3. TTL самой очереди** (`x-expires`) — удаляет **очередь целиком**, если ею не пользуются (нет consumer-ов и обращений) N миллисекунд:

```java
args.put("x-expires", 300000); // удалить очередь через 5 минут бездействия
```

Когда у сообщения истекает TTL, оно по умолчанию отбрасывается; но если на очереди настроен `DLX`, протухшее сообщение уходит туда — это база для паттерна отложенного retry (см. Q25).

---

## Q15. Что такое максимальная длина очереди (max-length)?

`max-length` ограничивает размер очереди — по числу сообщений или по объёму в байтах. Это защита от неограниченного роста очереди, когда producer-ы пишут быстрее, чем consumer-ы успевают читать. Ключевой выбор — что делать при переполнении, его задаёт `x-overflow`:

```java
args.put("x-max-length", 1000);           // максимум 1000 ready-сообщений
args.put("x-max-length-bytes", 1048576);  // максимум 1 МБ
args.put("x-overflow", "drop-head");      // при переполнении: удалить самое старое (default)
// или
args.put("x-overflow", "reject-publish"); // отклонить новое сообщение
```

Разница принципиальна: `drop-head` жертвует **старыми** данными ради приёма свежих (подходит для метрик, где важна актуальность), а `reject-publish` отказывает **новым** и сохраняет уже принятые (подходит для задач, которые нельзя терять). Лимит учитывает сообщения в состоянии `ready`; доставленные `unacked` в него не входят. При `drop-head` брокер пытается отправить вытесненное сообщение через настроенный DLX. Для попадания в DLQ нужен существующий exchange и подходящий маршрут. Обычный dead-lettering не гарантирует доставку в цель. При `reject-publish` издатель с включёнными confirms получает `basic.nack`, даже если другая очередь по тому же маршруту уже приняла сообщение. [Queue Length Limit](https://www.rabbitmq.com/docs/4.3/maxlength), [DLX](https://www.rabbitmq.com/docs/4.3/dlx).

---

## Q16. (!) Что такое Binding и Routing Key?

Это две стороны одной маршрутизации — их легко спутать, поэтому важно различать «кто настраивает» и «когда применяется».

**`Binding`** — правило связи `Exchange → Queue`, которое настраивает **администратор/приложение** при объявлении топологии. Оно говорит: «сообщения с таким-то `binding key` из этого exchange класть в эту очередь».

**`Routing Key`** — строка-ключ, которую указывает **producer** в момент публикации каждого сообщения. `Exchange` сравнивает её с `binding key` всех биндингов и так решает, куда направить сообщение (исключение — `fanout`, который ключ игнорирует).

```mermaid
graph LR
    E[Exchange] -->|binding key = "order.created"| Q1[orders-queue]
    E -->|binding key = "payment.*"| Q2[payments-queue]
    E -->|binding key = "#"| Q3[audit-queue]
```

Как трактуется `binding key`, зависит от типа exchange: в `direct` — это точная строка для посимвольного сравнения, в `topic` — паттерн с `*` и `#`, а в `fanout` он вообще игнорируется.

---

## Q17. Можно ли привязать несколько очередей к одному Exchange?

Да — и это один из ключевых механизмов маршрутизации в `AMQP`. Один `exchange` можно привязать к неограниченному числу очередей, причём с разными `binding key`: одно сообщение тогда размножается во все очереди, чей биндинг подошёл. На этом и строятся Pub/Sub (`fanout`) и иерархическая маршрутизация (`topic`).

Более того, привязывать друг к другу можно и **сами exchange** (exchange-to-exchange bindings). Это позволяет собирать многоступенчатые топологии — например, общий «входной» exchange, раскидывающий поток по нескольким специализированным exchange ниже.

---

## Q18. (!) Что такое acknowledgement (ack) в RabbitMQ?

**`Acknowledgement`** (`ack`) — сигнал от consumer-а брокеру: «сообщение обработано, можешь его удалять». Это краеугольный механизм надёжности: пока `ack` не пришёл, брокер считает сообщение незавершённым.

Логика такая: брокер доставляет сообщение и переводит его в состояние `unacked` — оно по-прежнему числится за consumer-ом и **не удаляется**. Удалит его брокер только после `basicAck`.

```mermaid
sequenceDiagram
    Queue->>Consumer: deliver(deliveryTag=1)
    Consumer->>Consumer: обработка
    Consumer->>Queue: basicAck(deliveryTag=1)
    Queue->>Queue: удалить сообщение
```

`basicAck` отправляется асинхронно, без ответа брокера на каждый ack. При закрытии канала или соединения брокер автоматически возвращает неподтверждённые доставки в очередь. Если обработка успела записать результат, а ack не дошёл, сообщение может прийти повторно. Это основа at-least-once при сохранности очереди и успешной публикации. Обработчик должен учитывать дубли. [Consumer Acknowledgements](https://www.rabbitmq.com/docs/4.3/confirms).

```java
channel.basicConsume("my-queue", false, (consumerTag, delivery) -> {
    try {
        process(delivery.getBody());
    } catch (Exception e) {
        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        return;
    }
    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
}, consumerTag -> {});
// При ошибке requeue=false: применяется настроенный маршрут DLX, если он есть.
```

**Нюанс:** `deliveryTag` — это порядковый номер доставки в рамках канала, не глобальный ID сообщения. Параметр `multiple=true` в `basicAck` подтверждает разом все сообщения с `deliveryTag` меньше или равным указанному — удобно для батч-подтверждения.

---

## Q19. Чем отличаются nack и reject?

В RabbitMQ 4.3 consumer quorum queue отклоняет одну доставку с `requeue=true`. Чем отличаются `basic.nack` и `basic.reject` по охвату и влиянию на счётчик неудачных доставок?

```text
RabbitMQ 4.3 · AMQP 0-9-1 · quorum queue
A: basic.nack(deliveryTag, multiple=false, requeue=true)
B: basic.reject(deliveryTag, requeue=true)
```

Оба метода сообщают брокеру о **неуспешной** обработке. Разница между ними — в одном параметре: `nack` умеет отклонять пачку сообщений, `reject` — только одно.

| Метод | Параметры | Назначение |
|-------|-----------|----------|
| `basicAck` | `deliveryTag, multiple` | Успешная обработка — сообщение удаляется |
| `basicNack` | `deliveryTag, multiple, requeue` | Неуспех; может отклонить пачку (`multiple`) — расширение RabbitMQ |
| `basicReject` | `deliveryTag, requeue` | Неуспех; только одно сообщение (стандарт AMQP) |

Ключевой для обоих — параметр `requeue`, он решает судьбу отклонённого сообщения:
- `requeue=true` — вернуть в очередь и попробовать снова. **Подводный камень:** если ошибка стабильная (например, битые данные), это может создать цикл переобработки.
- `requeue=false` — сообщение не возвращается: либо отбрасывается, либо уходит в `DLX` (если он настроен). Обычно правильный выбор для «ядовитых» сообщений.

Для **quorum queue** есть ещё версионный нюанс: с RabbitMQ **4.0** задан `delivery-limit=20` по умолчанию, но с **4.3** лимит считается по `delivery-count` неудачных доставок, а не по каждому получению сообщения. Явный `basic.nack` с повторной постановкой в очередь **не увеличивает** этот счётчик; `basic.reject` и потеря канала с неподтверждённым сообщением увеличивают. Поэтому встроенный лимит не заменяет ограничение повторов для цикла с `nack`. [RabbitMQ Quorum Queues: poison message handling](https://www.rabbitmq.com/docs/quorum-queues#poison-message-handling).

---

## Q20. (!) В чём разница между auto-ack и manual ack?

Разница в том, **в какой момент** брокер считает сообщение доставленным — и отсюда вытекает компромисс между скоростью и надёжностью.

**`auto-ack`** (`autoAck=true`) — брокер считает сообщение обработанным сразу, как только отправил его в сеть, не дожидаясь подтверждения.
- **Плюс:** меньше служебных сообщений: consumer не отправляет ack.
- **Минус:** брокер не знает, завершилась ли обработка. При обрыве после отправки сообщение может быть потеряно для consumer-а, поскольку брокер уже считает его доставленным.

**`manual ack`** (`autoAck=false`) — consumer сам решает, когда подтвердить, явно вызывая `basicAck` / `basicNack` / `basicReject` после обработки.
- **Плюс:** при закрытии канала или соединения неподтверждённые сообщения возвращаются в очередь. Это позволяет повторить доставку, если очередь и сообщения сохранились. Подтверждайте после фиксации результата и учитывайте повторную доставку (Q18).
- **Цена:** требуется обработка ошибок и ограничение `unacked`. Ack асинхронный; отдельного синхронного round-trip на каждое подтверждение нет. [Consumer Acknowledgements](https://www.rabbitmq.com/docs/4.3/confirms).

> **Рекомендация:** в продакшне для любой значимой обработки используйте `manual ack`. `auto-ack` оправдан только там, где потеря отдельных сообщений допустима (метрики, логи best-effort).

---

## Q21. Что такое Publisher Confirms?

**`Publisher Confirms`** — подтверждение в обратную сторону: брокер сообщает **producer-у**, результат обработки публикации. Для маршрутизированного сообщения критерий подтверждения зависит от очередей назначения и режима сохранности. Это зеркало consumer-ского `ack`: тот закрывает «последнюю милю» (брокер → consumer), а Publisher Confirms — «первую» (producer → брокер). Без них producer не знает, дошло ли сообщение вообще.

```java
channel.confirmSelect(); // включить режим подтверждений

// Синхронный вариант
channel.basicPublish("exchange", "key", null, body);
if (!channel.waitForConfirms(5000)) {
    throw new RuntimeException("Message not confirmed");
}

// Асинхронный вариант (лучше для производительности)
channel.addConfirmListener(
    (deliveryTag, multiple) -> log.info("Confirmed: {}", deliveryTag),
    (deliveryTag, multiple) -> log.warn("Nacked: {}", deliveryTag)
);
channel.basicPublish("exchange", "key", null, body);
```

**Нюанс:** синхронный `waitForConfirms` прост, но блокирует поток на каждом сообщении — на потоке это убивает throughput. Асинхронный `ConfirmListener` подтверждает пачками и не блокирует publish, поэтому в нагруженных сценариях предпочтительнее. `Publisher Confirms` и транзакции `AMQP` нельзя включить на одном `channel` одновременно.

**Confirm и возврат отвечают на разные вопросы.** Для немаршрутизируемой публикации с `mandatory=true` брокер отправляет `basic.return`, а затем может подтвердить её через `basic.ack`. Без `mandatory` такая публикация может получить confirm даже при отсутствии очереди назначения. Для контроля маршрутизации обрабатывайте returns и confirms отдельно; confirm ничего не говорит об обработке consumer-ом. [Publisher Confirms](https://www.rabbitmq.com/docs/4.3/confirms).

---

## Q22. Чем Publisher Confirms отличаются от транзакций AMQP?

Confirms сообщают издателю результат приёма публикации брокером. AMQP-транзакция группирует публикации и подтверждения потребителя на одном канале до `txCommit`; незавершённые операции группы можно отменить через `txRollback`. На одном канале эти режимы взаимоисключающие.

| Аспект | Publisher Confirms | AMQP-транзакция |
|--------|--------------------|-----------------|
| Подтверждение | Асинхронные `basic.ack` / `basic.nack` от брокера | Клиент ждёт ответа на `txCommit` |
| Группировка | Можно держать несколько публикаций в полёте и получать групповые confirms | Публикации и consumer ack копятся до commit |
| Граница | Приём публикации согласно типу очереди | Операции AMQP этого канала. Бизнес-операция и транзакция в БД находятся за этой границей. |

RabbitMQ не обещает атомарного результата при сбое посреди `txCommit`: даже в одной очереди после рестарта может оказаться только часть публикаций. Для нескольких очередей тем более нельзя рассчитывать на общий атомарный commit. Транзакции добавляют ожидание, но разница в throughput зависит от размера пакета, диска, сети и типа очереди. [Broker Semantics](https://www.rabbitmq.com/docs/4.3/semantics), [Publisher Confirms](https://www.rabbitmq.com/docs/4.3/confirms).

---

## Q23. (!) Что такое Dead Letter Exchange (DLX) и DLQ?

`DLX` и `DLQ` — это «карантин» для сообщений, которые не удалось обработать. Без него такие сообщения либо теряются (отбрасываются), либо застревают в бесконечном requeue. DLX даёт третий путь — отложить их в сторону, чтобы разобрать позже.

**`DLX`** (`Dead Letter Exchange`) — обычный `exchange`, который назначается очереди как «запасной выход». Брокер автоматически переправляет в него сообщения, ставшие «мёртвыми».

**`DLQ`** (`Dead Letter Queue`) — очередь, привязанная к этому `DLX`. Именно она копит мёртвые сообщения; дальше на неё вешают алертинг, ручной разбор или retry.

Технически `DLX` — это не отдельная сущность, а связка: на основной очереди ставят аргумент `x-dead-letter-exchange`, и любой `exchange` становится для неё dead-letter-приёмником.

```mermaid
graph LR
    P[Producer] -->|publish| E[Main Exchange]
    E --> Q[Main Queue]
    Q -->|nack / ttl / overflow| DLX[Dead Letter Exchange]
    DLX --> DLQ[Dead Letter Queue]
    DLQ --> A[Alerting / Retry]
```

Настройка:
```java
// Объявляем DLX и DLQ
channel.exchangeDeclare("dlx", "direct", true);
channel.queueDeclare("dlq", true, false, false, null);
channel.queueBind("dlq", "dlx", "dead");

// Основная очередь с указанием DLX
Map<String, Object> args = new HashMap<>();
args.put("x-dead-letter-exchange", "dlx");
args.put("x-dead-letter-routing-key", "dead");
channel.queueDeclare("main-queue", true, false, false, args);
```

---

## Q24. При каких условиях сообщение попадает в DLQ?

Брокер объявляет сообщение «мёртвым» и отправляет в `DLX` в четырёх случаях:

1. **Rejected** — consumer вызвал `basicReject` или `basicNack` с `requeue=false`, то есть явно отказался обрабатывать (см. Q19).
2. **TTL истёк** — у сообщения закончилось время жизни, заданное для очереди или индивидуально при публикации (см. Q14).
3. **Queue overflow** — очередь упёрлась в `x-max-length` с политикой `drop-head`, и сообщение вытеснено как самое старое (см. Q15).
4. **Delivery limit** — в quorum queue превышен предел неудачных доставок. С RabbitMQ 4.0 стандартный предел — 20; без настроенного DLX сообщение будет отброшено, а не попадёт в DLQ (см. Q30).

**Полезный нюанс:** при каждом dead-lettering брокер обновляет `x-death` с причиной, исходной очередью и счётчиком событий для пары «очередь + причина». Это не общий счётчик всех попыток обработки: повторные доставки без dead-lettering в нём не отражаются. Для quorum queue `x-delivery-count` описывает другой механизм — неудачные доставки. [RabbitMQ Dead Letter Exchanges](https://www.rabbitmq.com/docs/dlx).

**Что произойдёт, если DLQ временно недоступна?** В RabbitMQ 4.3 по умолчанию dead-lettering работает в режиме `at-most-once` даже для quorum queue. Для переноса с подтверждением настройте на исходной **quorum queue** политику с `dead-letter-strategy: at-least-once`, `overflow: reject-publish` и `dead-letter-exchange`. Нужен включённый feature flag `stream_queue`. С `drop-head` используется `at-most-once`, даже если лимит длины не задан.

Например, consumer отправил `basicNack(..., false, false)`, а целевая очередь временно не подтверждает публикации. В режиме `at-least-once` внутренний consumer брокера повторяет отправку, а исходная очередь удерживает сообщение до подтверждения от всех целевых очередей. Так же брокер повторяет попытки, если DLX отсутствует или сообщение некуда маршрутизировать. Удержанные сообщения занимают лимит исходной очереди и при длительном сбое могут привести к отказу новых публикаций. Если одна целевая очередь уже приняла сообщение, а другая не подтвердила, повтор может создать дубль в первой. Подтверждение здесь означает приём целевой очередью. Обработчик DLQ подтверждает свою доставку отдельно. [RabbitMQ 4.3: At-least-once dead-lettering](https://www.rabbitmq.com/docs/4.3/quorum-queues#activating-at-least-once-dead-lettering).

---

## Q25. Как реализовать retry с использованием DLX и TTL?

Идея паттерна **delayed retry**: при ошибке сообщение не возвращают в очередь сразу (это даёт мгновенный бесполезный повтор), а отправляют в отдельную retry-очередь с заданным `TTL`. Сообщение «отлёживается» там нужное время, затем по истечении TTL через `DLX` возвращается в основной exchange — и попадает на повторную обработку уже с задержкой.

Куда попадёт сообщение после отклонения и после истечения TTL при таких привязках?

```java
channel.exchangeDeclare("main-exchange", "direct", true);
channel.exchangeDeclare("dlx", "direct", true);

Map<String, Object> mainArgs = new HashMap<>();
mainArgs.put("x-dead-letter-exchange", "dlx");
mainArgs.put("x-dead-letter-routing-key", "retry");
channel.queueDeclare("main", true, false, false, mainArgs);
channel.queueBind("main", "main-exchange", "main-key");

Map<String, Object> retryArgs = new HashMap<>();
retryArgs.put("x-message-ttl", 5000);
retryArgs.put("x-dead-letter-exchange", "main-exchange");
retryArgs.put("x-dead-letter-routing-key", "main-key");
channel.queueDeclare("retry-queue", true, false, false, retryArgs);
channel.queueBind("retry-queue", "dlx", "retry");

// При ошибке в consumer
channel.basicNack(deliveryTag, false, false);
```

При заданных привязках путь проходит через `main → dlx → retry-queue → main-exchange → main`. TTL в 5000 мс задаёт срок хранения сообщения в retry-очереди. Фактическое время повторной доставки может быть больше.

**Подводный камень:** без ограничения попыток такой цикл крутится бесконечно. Consumer может считать события `x-death` для нужной очереди и причины, а при достижении своего порога направлять сообщение в финальную DLQ. Не путайте этот счётчик dead-letter-переходов со встроенным `x-delivery-count` quorum queue: возврат через DLX/TTL и повторная доставка внутри одной очереди — разные механизмы. Для quorum queues RabbitMQ 4.3 также описывает встроенный delayed retry; выбирайте один понятный маршрут и проверяйте число попыток. [RabbitMQ Quorum Queues](https://www.rabbitmq.com/docs/quorum-queues#poison-message-handling).

---

## Q26. (!) Что такое prefetch count и QoS?

**`Prefetch count`** (`basicQos`) — потолок числа `unacked`-сообщений, которые брокер «раздаст в кредит» consumer-у, не дожидаясь подтверждений. По сути это управление **обратным давлением (backpressure)**: брокер не заваливает медленного consumer-а работой, которую тот не успевает разгребать.

```java
// Одна подписка с ручным подтверждением. У неё 10 unacked.
// В очереди есть готовые сообщения. Что изменится после ack одной доставки?
channel.basicQos(10);
```

Почему это важно при нескольких consumer-ах. По умолчанию брокер раздаёт сообщения round-robin, не глядя на занятость. Если один consumer быстрый, а другой медленный, без prefetch медленному «налипнет» полная пачка, и быстрый будет простаивать.

**`prefetchCount=0`** отключает лимит кредита QoS на стороне клиента. Это не обещание немедленно доставить всё: действуют ограничения брокера, типа очереди и транспорта. Например, quorum queue ограничивает consumer prefetch 2000. Эта настройка относится к подпискам с подтверждениями и не ограничивает запросы `basic.get`. [Consumer Prefetch](https://www.rabbitmq.com/docs/4.3/consumer-prefetch), [Confirms](https://www.rabbitmq.com/docs/4.3/confirms).

`basicQos(10)` задаёт кредит в десять неподтверждённых доставок для этой подписки. После получения ack брокером освобождается место для следующей доставки. При `prefetchCount=1` каждая подписка с ручным подтверждением держит до одной неподтверждённой доставки. Быстрый consumer может чаще освобождать кредит, а итоговое распределение зависит от нагрузки и времени обработки.

---

## Q27. Как prefetch влияет на производительность и нагрузку?

Повышение prefetch позволяет перекрыть сетевую задержку и время обработки несколькими доставками в полёте. Когда обработчик уже насыщен, рост prefetch может увеличить объём памяти, время ожидания у consumer-а и число повторных доставок после сбоя.

`prefetch=1` оставляет по одной неподтверждённой доставке на consumer-а. Это полезная точка сравнения для длительных и неравномерных задач. Для коротких задач проверьте больший кредит под реальной нагрузкой. Сравните throughput, задержку обработки и число `unacked`. Универсального диапазона для всех очередей нет.

При `global=false` RabbitMQ применяет кредит отдельно к каждому consumer-у. В RabbitMQ 4.3 общий кредит канала (`global=true`) устарел и отключён по умолчанию. Quorum queues его не поддерживают. Для Streams проверяйте управление кредитом отдельно по используемому протоколу. [Consumer Prefetch](https://www.rabbitmq.com/docs/4.3/consumer-prefetch), [Quorum Queues](https://www.rabbitmq.com/docs/4.3/quorum-queues).

---

## Q28. (!) Как работает кластеризация в RabbitMQ?

Кластер `RabbitMQ` — это несколько узлов (`node`), которые делят общее пространство имён и выглядят для клиента как единый брокер. Главное, что нужно понимать на собеседовании: кластеризация по умолчанию решает **масштабирование и общую топологию**, но **не отказоустойчивость очередей**.

```mermaid
graph TD
    subgraph "RabbitMQ Cluster"
        N1[Node 1]
        N2[Node 2]
        N3[Node 3]
        N1 <--> N2
        N2 <--> N3
        N1 <--> N3
    end
    P[Producer] --> N1
    C[Consumer] --> N3
```

**Что реплицируется, а что нет** — это ключевой момент:
- **Метаданные реплицируются** на все узлы: `exchange`, `binding`, `vhost`, пользователи, политики. Поэтому к любому узлу можно подключиться и увидеть одну и ту же топологию.
- **Содержимое очередей по умолчанию НЕ реплицируется**: каждая classic-очередь физически живёт на **одном** узле. Если этот узел упадёт, очередь и её сообщения станут недоступны. Чтобы это исправить, нужны реплицированные очереди — `Quorum Queues` (см. Q30).

**Метаданные и partition в RabbitMQ 4.3.** Khepri — единственное хранилище метаданных. Оно использует Raft, и для изменения топологии нужно большинство узлов кластера. Для quorum queue большинство считается отдельно по репликам этой очереди. Сторона без большинства реплик конкретной очереди не может продолжить работу с ней. Доступность classic queue зависит от узла, где она размещена, и состояния кластера.

Типы Mnesia-узлов `ram`/`disc` и стратегии `pause_minority`, `autoheal`, `ignore` относятся к прежним релизам с Mnesia. В 4.3 Mnesia и её стратегии обработки разделения сети удалены. [RabbitMQ 4.3](https://www.rabbitmq.com/blog/2026/04/23/rabbitmq-4.3-release), [сетевые разделения](https://www.rabbitmq.com/docs/4.3/partitions).

---

## Q29. Что такое Mirrored Queues (Classic HA)?

**`Mirrored Queues`** (Classic HA) — исторически первый способ сделать очередь отказоустойчивой: её содержимое зеркалируется на несколько узлов кластера. Решает проблему из Q28 (одна очередь = один узел), но делает это ненадёжно — поэтому и устарел.

Как устроено:
- один узел — **master**, через него идут все операции с очередью;
- остальные — **slaves** (зеркала), которые непрерывно синхронизируются с master;
- при падении master одно из зеркал автоматически становится новым master.

**Почему от них отказались (минусы):**
- **Высокая нагрузка на сеть** — каждое сообщение копируется на все зеркала.
- **Риск потери сообщений при split-brain** — у механизма нет строгого консенсуса, поэтому при расхождении узлов данные могут быть утеряны при выборе нового master.
- Из-за этих проблем `RabbitMQ` объявил Mirrored Queues устаревшими (deprecated) ещё в `3.9` в пользу `Quorum Queues`; `3.13` стала последней веткой с их поддержкой, а полностью удалены они были в `4.0`.

---

## Q30. (!) Что такое Quorum Queues и чем они лучше Mirrored?

В RabbitMQ 4.3 сравните quorum queues с прежним classic mirroring. Какие TTL, приоритеты сообщений и предел неудачных доставок поддерживает quorum queue?

**`Quorum Queues`** — современный, рекомендуемый тип реплицированных очередей. Их главное преимущество над Mirrored — в основе лежит проверенный алгоритм консенсуса **Raft** (тот же, что в `etcd` и `Consul`), а не самописная схема репликации.

Что это даёт на практике: запись считается зафиксированной, только когда её подтвердило **большинство** узлов (quorum). Поэтому при split-brain меньшая часть кластера просто перестаёт принимать записи и данные не расходятся — это и есть устойчивость, которой не хватало Mirrored Queues.

| Характеристика | Mirrored Queues | Quorum Queues |
|----------------|-----------------|---------------|
| Алгоритм | Custom | Raft |
| Гарантии | Слабее | Stronger (majority) |
| Split-brain | Уязвим | Устойчив |
| Производительность | Зависит от нагрузки | Зависит от нагрузки и числа реплик |
| Поддержка | Mirroring удалён с RabbitMQ 4.0 | Поддерживаемый реплицированный тип |
| Poison messages | Нет встроенного delivery limit | `delivery-limit`, по умолчанию 20 с 4.0 |

```java
// Spring AMQP, брокер RabbitMQ 4.3. Дополнительные политики не заданы.
@Bean
public Queue quorumQueue() {
    return QueueBuilder.durable("my-quorum-queue")
        .quorum()
        .build();
}
```

**Компромисс:** quorum queue пишет реплицированный Raft log и требует ресурсы большинства реплик. Она поддерживает **message TTL** (общий для сообщений очереди и индивидуальный) и с RabbitMQ **4.3** — **строгие приоритеты**; не следует повторять старое правило об их полном отсутствии. Отличия от classic queues остаются: например, quorum queue не бывает exclusive или недолговечной и не допускает exclusive consumer. Для ядовитых сообщений настройте DLX: при превышении `delivery-limit` иначе возможна потеря сообщения. С RabbitMQ 4.3 явный `nack` не увеличивает `delivery-count`, поэтому защита зависит от способа возврата и версии. [Официальное сравнение](https://www.rabbitmq.com/docs/quorum-queues#feature-matrix) и [poison message handling](https://www.rabbitmq.com/docs/quorum-queues#poison-message-handling).

---

## Q31. Что такое Federation и Shovel в RabbitMQ?

Оба плагина связывают **разные брокеры** (например, в разных датацентрах) — в отличие от кластеризации, которая объединяет узлы в одном. Разница между ними — в уровне абстракции.

**Exchange Federation** передаёт публикации с upstream exchange на downstream exchange по подходящим привязкам. Локальный consumer для срабатывания связи сам по себе не обязателен. **Queue Federation** переносит сообщения из upstream queue в downstream queue при спросе на downstream и отсутствии подходящих локальных потребителей upstream для этих сообщений.

**`Shovel`** — низкоуровневая «труба»: непрерывно вычитывает сообщения из конкретной очереди-источника и перекладывает в конкретный пункт назначения (в том числе на удалённом брокере). Сценарии применения:
- разовая миграция сообщений между брокерами;
- репликация потока между датацентрами;
- переброска накопившихся сообщений из `DLQ` обратно в обработку после починки бага.

[Federation](https://www.rabbitmq.com/docs/4.3/federation), [Shovel](https://www.rabbitmq.com/docs/4.3/shovel).

---

## Q32. (!) Что такое Spring AMQP и RabbitTemplate?

**`Spring AMQP`** — это слой Spring над `AMQP`, который прячет низкоуровневый API клиента (ручное управление `Connection`/`Channel`, сериализацию, retry) за идиоматичными Spring-абстракциями. Разбит на два модуля:
- `spring-amqp` — брокеро-независимые абстракции (`Message`, `MessageConverter`, `AmqpTemplate`);
- `spring-rabbit` — конкретная реализация для `RabbitMQ`.

**`RabbitTemplate`** — центральный класс для **отправки** и синхронного запроса-ответа. Он аналог `JdbcTemplate`: берёт на себя получение канала из пула, отправку и обработку ошибок, оставляя вам только бизнес-вызов.

```java
@Autowired
private RabbitTemplate rabbitTemplate;

// Отправка
rabbitTemplate.convertAndSend("my-exchange", "routing.key", myObject);

// Синхронный запрос (RPC)
MyResponse response = (MyResponse) rabbitTemplate.convertSendAndReceive(
    "rpc-exchange", "rpc-key", request
);
```

**`MessageConverter`** преобразует объект и тело AMQP-сообщения. Стандартный `SimpleMessageConverter` передаёт `byte[]` как байты, кодирует `String` как текст и использует Java-сериализацию для других поддерживаемых `Serializable` объектов. При чтении он выбирает преобразование по `contentType`, а для неизвестного типа возвращает `byte[]`. Для JSON в Spring AMQP 4.x используйте `JacksonJsonMessageConverter` на Jackson 3. В Spring AMQP 3.x для Jackson 2 используется `Jackson2JsonMessageConverter`. [Spring AMQP Message Converters](https://docs.spring.io/spring-amqp/reference/4.0/amqp/message-converters.html).

---

## Q33. Как объявить очередь и exchange через Spring AMQP?

Топологию (`exchange`, `queue`, `binding`) в Spring AMQP объявляют **декларативно — как `@Bean`-ы**. При старте контекста `RabbitAdmin` находит эти бины и автоматически создаёт соответствующие объекты в брокере (если их ещё нет). Удобные билдеры `ExchangeBuilder`, `QueueBuilder`, `BindingBuilder` делают объявление читаемым; обратите внимание на `MessageConverter` и `RabbitTemplate` — пример ниже рассчитан на Spring AMQP 4.x. Для JSON согласуйте конвертер на стороне издателя и listener-контейнера (Q32).

```java
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange ordersExchange() {
        return ExchangeBuilder.topicExchange("orders.exchange")
            .durable(true)
            .build();
    }

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable("orders.queue")
            .withArgument("x-dead-letter-exchange", "orders.dlx")
            .withArgument("x-message-ttl", 60000)
            .build();
    }

    @Bean
    public Binding ordersBinding(Queue ordersQueue, TopicExchange ordersExchange) {
        return BindingBuilder
            .bind(ordersQueue)
            .to(ordersExchange)
            .with("order.#");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
```

Для dead-lettering дополнительно объявите `orders.dlx` и привяжите к нему целевую DLQ подходящим routing key. Ссылка в аргументах очереди сама эти объекты не создаёт. [DLX](https://www.rabbitmq.com/docs/4.3/dlx).

---

## Q34. (!) Как настроить @RabbitListener?

`@RabbitListener` — декларативный способ объявить consumer-а: помечаете метод аннотацией с именем очереди, а Spring сам поднимает listener-контейнер, вычитывает сообщения, конвертирует тело в аргумент метода и вызывает его на каждое сообщение. Ниже три типовых варианта — от простейшего до случая с ручным `ack` и динамическим объявлением топологии прямо на listener-е:

```java
@Component
public class OrderListener {

    // Простой listener
    @RabbitListener(queues = "orders.queue")
    public void handleOrder(Order order) {
        orderService.process(order);
    }

    // С доступом к заголовкам и метаданным
    @RabbitListener(queues = "orders.queue", ackMode = "MANUAL")
    public void handleWithHeaders(
        @Payload Order order,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey,
        Channel channel) throws IOException {
        try {
            orderService.process(order);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            return;
        }
        channel.basicAck(deliveryTag, false);
    }

    // Динамическое объявление очереди и exchange
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "dynamic.queue", durable = "true"),
        exchange = @Exchange(value = "topic.exchange", type = ExchangeTypes.TOPIC),
        key = "order.#"
    ))
    public void handleDynamic(Order order) {
        orderService.process(order);
    }
}
```

**Какой контейнер под капотом — `SimpleMessageListenerContainer` или `DirectMessageListenerContainer`:**
- `Simple` (по умолчанию) — держит собственный пул потоков, каждый consumer работает в отдельном thread. Гибче в настройке (динамическое изменение числа consumer-ов), но с дополнительным overhead на передачу сообщений между потоками.
- `Direct` — обрабатывает сообщения прямо в потоке amqp-клиента, без промежуточного пула. Меньше overhead и латентность, но число consumer-ов фиксированное.

Три метода показаны как альтернативы; два listener-а на `orders.queue` будут конкурировать за сообщения. Для ручного подтверждения нужен режим `MANUAL`. `basicNack(..., false, false)` отклоняет сообщение без requeue. До DLQ оно дойдёт при настроенном DLX и подходящем binding. [Spring AMQP: listener containers](https://docs.spring.io/spring-amqp/reference/amqp/containerAttributes.html), [DLX](https://www.rabbitmq.com/docs/4.3/dlx).

---

## Q35. Как настроить retry-логику в Spring AMQP?

В Spring AMQP есть два режима retry, и важно понимать разницу. **Stateless retry** — самый частый: повторы крутятся **в памяти** consumer-а, между ними сообщение не возвращается в брокер. Настраивается одним блоком в `application.yml`:

```yaml
# Spring Boot 3.5
spring:
  rabbitmq:
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2.0
          max-interval: 10000
        acknowledge-mode: auto
```

В Spring Boot 3.5 `max-attempts: 3` включает первую попытку и два повтора. В Spring Boot 4.0 параметр `max-retries` считает только повторы. Эквивалент для того же бюджета — `max-retries: 2`. [Свойства Boot 3.5](https://docs.spring.io/spring-boot/3.5/appendix/application-properties/) · [Свойства Boot 4.0](https://docs.spring.io/spring-boot/4.0/appendix/application-properties/)

**Stateful retry** связывает попытки при повторной доставке по стабильному `messageId` или ключу от `MessageKeyGenerator`. Состояние хранит retry-контекст; долговечное хранение счётчика по умолчанию не гарантируется. Stateful подходит, когда исключение должно выйти из обработки, чтобы транзакция откатилась и доставка могла повториться. Режим `MANUAL` для этого не обязателен. После исчерпания попыток `RejectAndDontRequeueRecoverer` просит контейнер отклонить доставку без requeue. Брокер маршрутизирует её в DLQ только при наличии настроенного DLX и маршрута. Ниже пример stateless retry для Spring AMQP 3.x с подтверждениями контейнера:

```java
@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
    ConnectionFactory connectionFactory,
    MessageConverter converter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(converter);
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

    RetryInterceptorBuilder<?> builder = RetryInterceptorBuilder.stateless()
        .maxAttempts(3)
        .backOffOptions(1000, 2.0, 10000)
        .recoverer(new RejectAndDontRequeueRecoverer()); // отклонить без requeue
    factory.setAdviceChain(builder.build());
    return factory;
}
```

Spring `AcknowledgeMode.AUTO` означает подтверждение контейнером после успешного вызова listener-а. Это отличается от брокерного `autoAck=true`. `RepublishMessageRecoverer` реализует другой маршрут: приложение публикует сообщение в настроенный exchange, а контейнер подтверждает исходную доставку при успешном завершении recoverer-а. [Spring AMQP: Resilience](https://docs.spring.io/spring-amqp/reference/amqp/resilience-recovering-from-errors-and-broker-failures.html).

---

## Q36. (!) Какие основные паттерны обмена сообщениями реализуются в RabbitMQ?

```mermaid
graph TD
    subgraph "Топология A"
        WP[Producer] --> WQ[Queue]
        WQ --> WC1[Worker 1]
        WQ --> WC2[Worker 2]
    end

    subgraph "Топология B"
        PP[Producer] --> FE[Exchange: fanout]
        FE --> FQ1[Queue 1 → Sub 1]
        FE --> FQ2[Queue 2 → Sub 2]
    end

    subgraph "Топология C"
        RP[Client] -->|reply_to=callback.queue| RE[request.queue]
        RE --> RS[Server]
        RS --> RQ[callback.queue]
        RQ --> RP
    end
```

Все паттерны `RabbitMQ` — это, по сути, комбинации уже знакомых кирпичиков: тип `exchange` + способ потребления. Различаются они тем, как именно сообщение «размножается» и кто его получает.

| Паттерн | Exchange | Суть |
|---------|----------|----------|
| Work Queue | Default/Direct | Одна очередь, несколько worker-ов конкурируют — задача достаётся **одному** |
| Pub/Sub | Fanout | Событие копируется **всем** подписчикам |
| Routing | Direct | Подписка на конкретные типы события по точному ключу |
| Topic | Topic | Гибкая подписка по паттерну ключа |
| RPC | Default | Синхронный запрос/ответ поверх двух очередей |
| Delayed | DLX + TTL | Отложенная доставка через «отлёживание» в retry-очереди (Q25) |

**Ключевое различие:** в Work Queue сообщение получает **один** из worker-ов (распределение нагрузки), а в Pub/Sub — **каждый** подписчик (рассылка). Это прямое следствие того, конкурируют consumer-ы за одну очередь или у каждого своя.

---

## Q37. Как реализовать паттерн RPC через RabbitMQ?

RPC поверх очередей строится на двух деталях: **`reply_to`** и **`correlationId`**. Клиент создаёт временную reply-очередь, кладёт её имя в свойство `reply_to` запроса, а в `correlationId` — уникальный идентификатор. Сервер обрабатывает запрос и отправляет ответ в очередь из `reply_to`, копируя тот же `correlationId`. Клиент по этому id сопоставляет пришедший ответ с конкретным запросом — это нужно, потому что в одну reply-очередь могут прилетать ответы на несколько параллельных запросов.

```text
Запрос A: очередь rpc-queue, reply_to=callback-queue, correlationId=req-17
Запрос B: очередь rpc-queue, reply_to=callback-queue, correlationId=req-18
Оба запроса отправлены, ответы могут прийти в любом порядке.
Как клиенту сопоставить ответы с запросами?
```

```java
// Клиент
String correlationId = UUID.randomUUID().toString();
String replyQueueName = channel.queueDeclare().getQueue();

AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .correlationId(correlationId)
    .replyTo(replyQueueName)
    .build();

channel.basicPublish("", "rpc-queue", props, request.getBytes());

// Фрагмент клиента для одного ожидаемого ответа
BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
    if (correlationId.equals(delivery.getProperties().getCorrelationId())) {
        response.offer(new String(delivery.getBody()));
    }
}, tag -> {});

return response.poll(10, TimeUnit.SECONDS);
```

Для нескольких одновременных запросов клиент регистрирует ожидания по `correlationId` перед публикацией. Один consumer reply-очереди извлекает этот ID из каждого ответа, завершает соответствующее ожидание и обрабатывает истёкшие или неизвестные ID. [RabbitMQ RPC tutorial](https://www.rabbitmq.com/tutorials/tutorial-six-java)

В `Spring AMQP` запрос и ожидание ответа скрывает `rabbitTemplate.convertSendAndReceive()`. При отсутствии явной reply-очереди и поддержке брокером шаблон обычно использует **Direct Reply-To**. `amq.rabbitmq.reply-to` — псевдоочередь без хранения ответов, доставка ответа имеет семантику at-most-once. При обрыве клиенту нужен таймаут и выбранная политика повторов. Обычная reply-очередь подходит, когда ответ нужно буферизовать. [Direct Reply-To](https://www.rabbitmq.com/docs/4.3/direct-reply-to), [Spring AMQP Request/Reply](https://docs.spring.io/spring-amqp/reference/amqp/request-reply.html).

**Подводный камень:** синхронный RPC через брокер противоречит идее асинхронного messaging — клиент блокируется в ожидании ответа, добавляется лишний сетевой хоп, а таймауты надо обрабатывать явно. Для синхронных вызовов часто уместнее прямой HTTP/gRPC; RPC-over-AMQP берут, когда уже есть брокер и нужна его маршрутизация или развязка от адресов сервисов.

---

## Q38. Как мониторить RabbitMQ через Management Plugin?

**Management Plugin** — штатный инструмент наблюдения за брокером. Он даёт три точки входа под разные задачи:
- **Web UI** (`http://localhost:15672`) — интерактивный дашборд для ручного разбора инцидентов;
- **HTTP API** (`http://localhost:15672/api/`) — те же данные в JSON для автоматизации и интеграций;
- **CLI** `rabbitmqadmin` — скрипты, экспорт/импорт конфигурации.

**Ключевые страницы UI:**
- **Overview** — общее здоровье кластера: узлы, connections, channels, суммарные очереди;
- **Queues** — самое важное при отладке: глубина каждой очереди и скорости publish / consume / ack (по их соотношению видно, успевают ли consumer-ы);
- **Connections/Channels** — активные соединения и каналы (помогает ловить утечки channel-ов);
- **Admin** — пользователи, `vhost`, политики.

```bash
# rabbitmqadmin v1: просмотр очередей
rabbitmqadmin list queues name messages consumers

# Экспорт конфигурации
rabbitmqadmin export config.json
```

Для `rabbitmqadmin` v2 используйте синтаксис групп команд из [руководства RabbitMQ](https://www.rabbitmq.com/docs/4.3/management-cli).

**Рекомендация для продакшна:** Management UI хорош для ручного разбора, но для постоянного мониторинга и алертинга подключают плагин `rabbitmq_prometheus` — он отдаёт метрики в `Prometheus`, поверх которого строят дашборды (Grafana) и правила оповещений.

---

## Q39. Какие метрики RabbitMQ наиболее важны?

Смотрите совместно состояние очередей, скорости обработки и ресурсные alarms. Ниже — смысловые группы показателей; точные имена полей Management API или метрик Prometheus сверяйте с используемым интерфейсом.

| Показатель | Что показывает | Что проверить при отклонении |
|---------|----------|-----------------|
| Messages ready | Сообщения, ожидающие доставки | Динамику backlog, входящий поток и доступность consumer-ов. |
| Messages unacknowledged | Доставленные, но ещё не подтверждённые сообщения | Время обработки, prefetch и число consumer-ов; высокий уровень сам по себе не доказывает зависание. |
| Consumers | Потребители очереди | Есть ли подписчик, когда очередь должна обрабатываться. |
| Connections / channels | Открытые соединения и каналы | Ожидаемый параллелизм, скорость открытия/закрытия и приближение к лимитам. |
| Disk free | Свободное место относительно disk alarm threshold | Активный disk alarm и блокировку publishing connections. |
| Memory used | Память узла относительно настроенного порога | Активный memory alarm, фактический порог и обнаруженный runtime объём памяти. |
| Publish / deliver / ack rates | Публикации, доставки и подтверждения | Один и тот же scope измерения, режим ack и фактическую динамику ready/unacked. |

Для обычной очереди длительный избыток входящего потока над завершённой обработкой — повод исследовать backlog, но один `publish_rate > deliver_ack_rate` не доказывает его неизбежный рост. При auto-ack ручных подтверждений нет; TTL, overflow, reject/dead-letter и другие пути удаления тоже меняют размер очереди. Для Streams ack вообще не удаляет запись из лога (Q42).

В RabbitMQ **4.3** относительный default `vm_memory_high_watermark.relative = 0.6` означает 60% обнаруженной доступной RAM, а не 40% и не жёсткий предел потребления. В контейнерах документация рекомендует задавать абсолютный порог с учётом реального лимита. При memory/disk alarm блокируются publishing connections; в кластере alarm одного узла влияет на публикацию через все узлы. Соединения, используемые только для потребления, продолжают работу. **Transient flow control** — отдельное ограничение, когда внутренние процессы брокера не успевают за публикацией; оно возможно и без memory alarm.

Источники: [RabbitMQ 4.3 memory](https://www.rabbitmq.com/docs/4.3/memory), [alarms](https://www.rabbitmq.com/docs/4.3/alarms), [flow control](https://www.rabbitmq.com/docs/4.3/flow-control), [queue length/overflow](https://www.rabbitmq.com/docs/4.3/maxlength), [acknowledgements](https://www.rabbitmq.com/docs/4.3/confirms).

---

## Q40. (!) Чем Kafka отличается от очередей и Streams в RabbitMQ?

Сравнивайте **модель хранения и потребления**, а не только названия продуктов. Обычные очереди RabbitMQ (`classic`/`quorum`) удаляют сообщение после подтверждения обработки; Kafka хранит записи в партиционированном логе по политике retention. Но RabbitMQ **Streams** тоже представляют собой реплицированный append-only лог с повторным чтением по offset. Поэтому утверждение «RabbitMQ не поддерживает replay» неверно для Streams. [Документация RabbitMQ Streams](https://www.rabbitmq.com/docs/4.3/streams), [сравнение с Kafka](https://www.rabbitmq.com/docs/4.3/compare/kafka).

| Свойство | RabbitMQ classic/quorum queue | RabbitMQ Stream | Kafka topic |
|---|---|---|---|
| Потребление | Конкурирующие consumer-ы; ack завершает жизнь сообщения в очереди. | Чтение не удаляет запись; consumer выбирает позицию в логе. | Группы независимо отслеживают offset в партициях. |
| История | После ack replay из самой очереди невозможен. | Replay возможен, пока запись сохраняется по retention. | Replay возможен, пока запись сохраняется по retention/compaction. |
| Масштабирование | Несколько очередей и consumer-ов, при необходимости quorum-репликация. | Один stream или partitioned super stream для роста нагрузки. | Партиции распределяют нагрузку и задают область порядка. |
| Сильная сторона | Маршрутизация через exchanges, очереди задач, ack/requeue. | Большой fan-out, replay и длительный backlog внутри RabbitMQ. | Распределённый event log и экосистема потоковой обработки. |

Не закладывайте фиксированные числа «RabbitMQ 100K/s, Kafka 1M/s» или универсальное сравнение latency: результат зависит от типа очереди, размера сообщений, числа реплик, батчинга, дисков и сценария чтения. Для решения проверяйте нужные гарантии и измеряйте собственный профиль нагрузки. [RabbitMQ Streams](https://www.rabbitmq.com/docs/4.3/streams), [Kafka topic/retention](https://kafka.apache.org/43/configuration/topic-configs/).

---

## Q41. Когда выбрать RabbitMQ, а когда Kafka?

Сначала уточните, нужна ли **очередь задач** или **сохраняемый поток событий**, а затем сравнивайте конкретные режимы:

- **RabbitMQ classic/quorum queue:** задача должна попасть одному worker-у, нужны exchanges, гибкая маршрутизация, ack/requeue, DLX и подходящий тип репликации. После ack сообщение не предназначено для replay.
- **RabbitMQ Stream:** нужны повторное чтение, независимые подписчики или большой backlog, а RabbitMQ уже используется либо его маршрутизация и эксплуатационная модель подходят команде. Stream хранит записи до истечения retention; super stream добавляет партиционирование (Q42).
- **Kafka:** нужен партиционированный event log, независимые consumer groups и совместимость с уже выбранной Kafka-экосистемой обработки/интеграций. Настраивайте число партиций, retention и гарантии подтверждения под конкретный поток.

Наличие replay **само по себе не выбирает Kafka**: Streams дают его и в RabbitMQ. Сравните требования к маршрутизации, масштабу, времени хранения, клиентским протоколам и действующей инфраструктуре. Не переносите свойства одного типа RabbitMQ-очереди на весь брокер. [RabbitMQ comparison](https://www.rabbitmq.com/docs/compare/kafka), [Streams](https://www.rabbitmq.com/docs/streams), [Kafka topic configs](https://kafka.apache.org/43/configuration/topic-configs/).

## Q42. Чем RabbitMQ Streams отличаются от обычной очереди и когда они нужны?

Сервис выбирает между обычной очередью и Stream. Что изменится для независимых читателей и что произойдёт при перезапуске описанного ниже потребителя?

```text
RabbitMQ 4.3, чтение Stream по AMQP 0-9-1.
После перезапуска клиент задаёт x-stream-offset из своего checkpoint.
В checkpoint сохранён offset следующей записи: 1200.
После retention первая доступная позиция: 1350.
```

`RabbitMQ Stream` — **постоянный реплицированный append-only лог**. Consumer читает по позиции; подтверждение обработки не удаляет запись для остальных. `Classic` и `quorum` очереди устроены иначе: после ack сообщение считается обработанным и исчезает из очереди. Streams подходят, когда нужны replay, несколько независимых читателей, большой fan-out или длительный backlog. Stream объявляют с `x-queue-type=stream`; для полного набора возможностей и высокой пропускной способности документация рекомендует отдельный Stream protocol client. [RabbitMQ Streams](https://www.rabbitmq.com/docs/4.3/streams), [Stream plugin](https://www.rabbitmq.com/docs/4.3/stream).

| Вопрос дизайна | Решение для Stream |
|---|---|
| Откуда читать после перезапуска? | Явно восстановить позицию: в AMQP 0-9-1 задать `x-stream-offset`, в Stream protocol можно использовать broker-side offset tracking. |
| Когда данные исчезают? | Retention по возрасту и/или размеру, а не после ack; запрос старого offset за пределами истории привязывается к первой доступной записи. |
| Как масштабировать один поток? | `Super stream` делит его на партиции; порядок сохраняется внутри партиции, а не глобально. |
| Что при отказе узла? | У stream есть leader и реплики; для работы нужен quorum доступных реплик. |

При работе через AMQP 0-9-1 `ack` у stream участвует в продвижении потребления/кредитов, но **не удаляет запись из лога**. Для обычной очереди задач с точечным ack/requeue и DLQ выбирайте classic/quorum queue; для истории событий и replay — Stream, если подходят его ограничения и клиентская модель. Streams не означают бесконечного хранения: при проектировании задайте retention и сценарий, когда consumer отстал за его пределы. [RabbitMQ Streams: consumption, retention, super streams](https://www.rabbitmq.com/docs/4.3/streams), [quorum queues](https://www.rabbitmq.com/docs/4.3/quorum-queues).

В показанном сценарии чтение начнётся с позиции 1350, а записи с позиций от 1200 до 1349 недоступны в этом Stream.

**После перезапуска** позицию чтения нужно задавать осознанно. У AMQP 0-9-1 consumer-а без `x-stream-offset` чтение начнётся со следующей записи после подписки, поэтому история, пришедшая до старта, не будет обработана. Stream protocol поддерживает сохранение offset у брокера через Stream plugin; это отдельный механизм, не результат обычного AMQP `ack`. Если сохранённый offset уже удалён retention, RabbitMQ привяжет запрос к началу доступного лога. Сравните ожидаемую позицию с первой доступной: Java Stream Client 1.12 предоставляет `Environment.queryStreamStats(stream).firstOffset()`. У пустого stream `firstOffset()` может выбросить `NoOffsetException`; этот исход нужно обработать отдельно. Если есть разрыв, восстановите состояние из снимка или пропущенные события из внешнего архива; одно лишь повторное подключение не вернёт удалённые записи. [RabbitMQ Streams: consuming и offset tracking](https://www.rabbitmq.com/docs/4.3/streams), [Java Stream Client API](https://github.com/rabbitmq/rabbitmq-stream-java-client/blob/v1.12.0/src/main/java/com/rabbitmq/stream/StreamStats.java).

---

## See also

- [Apache Kafka](kafka-interview.md) — альтернативный брокер для event streaming
- [Распределённые системы](../architecture/distributed-systems-interview.md) — CAP-теорема, консистентность, отказоустойчивость
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — основы Spring Boot для интеграции
- [Микросервисная архитектура](../architecture/microservices-interview.md) — паттерны коммуникации между сервисами
- [Event-driven паттерны](../architecture/event-driven-patterns-interview.md) — паттерны асинхронного взаимодействия
- [Стратегии кэширования](../architecture/caching-strategies-interview.md) — дополняет паттерны обмена данными
- [Паттерны согласованности](../architecture/consistency-patterns-interview.md) — гарантии доставки и идемпотентность

- [AWS SQS и SNS](aws-sqs-sns-interview.md)
- [Apache Kafka](kafka-interview.md)
- [Сравнение Message Brokers](message-brokers-comparison-interview.md)
- [NATS](nats-interview.md)
- [Apache Pulsar](pulsar-interview.md)
- [Redpanda](redpanda-interview.md)
- [Шпаргалка: RabbitMQ для Java](../../development/messaging/rabbitmq/rabbitmq.md) — теория
- [JMS и ActiveMQ](jms-activemq-interview.md) — брокерный messaging со стандартным Java API (queue/topic)
