---
title: "Вопросы на собеседовании: RabbitMQ"
description: "Полное покрытие RabbitMQ для интервью: AMQP протокол, exchanges, queues, bindings, acknowledgements, DLQ, Publisher Confirms, кластеризация, quorum queues, Spring AMQP, паттерны и мониторинг."
tags:
  - interview
  - messaging
  - rabbitmq-interview
aliases:
  - "RabbitMQ interview"
  - "RabbitMQ собеседование"
  - "RabbitMQ вопросы"
  - "AMQP брокер"
difficulty: "intermediate"
updated: "2026-04-25"
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
- [Q38. Как осуществляется мониторинг RabbitMQ через Management Plugin?](#q38-как-осуществляется-мониторинг-rabbitmq-через-management-plugin)
- [Q39. Какие метрики RabbitMQ наиболее важны?](#q39-какие-метрики-rabbitmq-наиболее-важны)

**RabbitMQ vs Kafka**
- [Q40. (!) В чём принципиальные отличия RabbitMQ от Kafka?](#q40--в-чём-принципиальные-отличия-rabbitmq-от-kafka)
- [Q41. Когда выбрать RabbitMQ, а когда Kafka?](#q41-когда-выбрать-rabbitmq-а-когда-kafka)

---

## Q1. (!) Что такое RabbitMQ и протокол AMQP?

**`RabbitMQ`** — брокер сообщений с открытым исходным кодом, реализующий протокол `AMQP 0-9-1` (Advanced Message Queuing Protocol). Написан на `Erlang`, что обеспечивает высокую отказоустойчивость и конкурентность.

**`AMQP`** — бинарный протокол уровня приложения для асинхронного обмена сообщениями. Ключевые характеристики:
- **Надёжность** — подтверждения доставки на уровне протокола
- **Маршрутизация** — гибкая маршрутизация через exchange и binding
- **Безопасность** — `TLS`, `SASL`-аутентификация
- **Совместимость** — стандартизованный протокол, независимый от реализации

`RabbitMQ` также поддерживает `STOMP`, `MQTT`, `AMQP 1.0` через плагины.

> [!mcq]
> - [ ] RabbitMQ реализует протокол MQTT и написан на Go, что обеспечивает высокую конкурентность. | MQTT — один из дополнительных протоколов, поддерживаемых через плагин, а не основной. RabbitMQ написан на Erlang, а не Go.
> - [x] RabbitMQ реализует протокол AMQP 0-9-1 и написан на Erlang, что обеспечивает высокую отказоустойчивость. | Именно так: AMQP 0-9-1 — основной протокол, Erlang — язык реализации, изначально созданный для телекоммуникационных систем с требованиями высокой доступности.
> - [ ] RabbitMQ реализует протокол STOMP и написан на Java, что обеспечивает широкую экосистему. | STOMP поддерживается только через плагин, Java — не язык реализации RabbitMQ.
> - [ ] RabbitMQ реализует протокол AMQP 1.0 и написан на Rust, что обеспечивает высокую производительность. | AMQP 1.0 доступен через плагин, а основным является AMQP 0-9-1. Rust не имеет отношения к реализации RabbitMQ.

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

**Компоненты:**

| Компонент | Описание |
|-----------|----------|
| `Producer` | Публикует сообщения в `exchange` |
| `Exchange` | Принимает сообщения от `producer` и маршрутизирует в очереди |
| `Queue` | Буфер для хранения сообщений до получения `consumer` |
| `Binding` | Правило связи между `exchange` и `queue` |
| `Consumer` | Получает сообщения из очереди |
| `Channel` | Виртуальное соединение внутри `AMQP`-коннекшна |
| `Virtual Host` | Логическое разделение ресурсов брокера |

> [!mcq]
> - [ ] Producer публикует сообщения напрямую в Queue, минуя Exchange, через указание имени очереди. | В модели AMQP Producer всегда публикует в Exchange, даже при использовании Default Exchange — это просто безымянный direct exchange.
> - [ ] Consumer получает сообщения напрямую от Producer через Binding без участия Exchange. | Binding связывает Exchange и Queue, а не Producer и Consumer. Consumer подписывается на Queue, а не на Producer.
> - [x] Producer публикует в Exchange, Exchange маршрутизирует через Binding в Queue, Consumer читает из Queue. | Это точная последовательность AMQP-модели: Producer → Exchange → Binding → Queue → Consumer. Channel обеспечивает мультиплексирование внутри Connection.
> - [ ] Exchange публикует сообщения напрямую в Consumer, минуя Queue, при наличии активного Binding. | Exchange никогда не доставляет сообщения потребителю напрямую — всегда через промежуточную Queue, которая буферизирует сообщения.

---

## Q3. Что такое Virtual Host (vhost)?

**`Virtual Host`** (`vhost`) — логическая изоляция ресурсов внутри одного `RabbitMQ`-сервера. Аналог баз данных в `PostgreSQL`.

- Каждый `vhost` имеет свои `exchange`, `queue`, `binding`, пользователей и права доступа
- По умолчанию создаётся `vhost` `/`
- Позволяет разным приложениям использовать один брокер без пересечения ресурсов

```java
// Подключение к конкретному vhost
ConnectionFactory factory = new ConnectionFactory();
factory.setVirtualHost("/my-app");
```

> [!mcq]
> - [ ] Virtual Host — это физически отдельный сервер RabbitMQ, изолирующий данные разных приложений на уровне ОС. | Vhost — логическая, а не физическая изоляция. Все vhost работают в рамках одного процесса RabbitMQ-сервера.
> - [x] Virtual Host — это логическое разделение ресурсов внутри одного RabbitMQ-сервера, аналог схем в СУБД. | Точное определение: vhost изолирует exchange, queue, binding и права доступа. По умолчанию используется vhost `/`. Это позволяет одному брокеру обслуживать несколько приложений без пересечения ресурсов.
> - [ ] Virtual Host — это механизм репликации очередей между узлами кластера для обеспечения HA. | Репликация в RabbitMQ обеспечивается Mirrored Queues или Quorum Queues, но никак не виртуальными хостами.
> - [ ] Virtual Host — это псевдоним для конкретного Exchange, позволяющий маршрутизировать сообщения по имени хоста. | Vhost не является псевдонимом exchange. Это изолированное пространство имён, содержащее собственные exchange, queue и binding.

---

## Q4. Что такое Channel и зачем он нужен?

**`Channel`** — виртуальное соединение (`lightweight connection`) поверх физического `AMQP`-соединения (`Connection`).

**Зачем нужен:**
- Создание TCP-соединения дорого; `channel` позволяет мультиплексировать несколько логических потоков по одному TCP-соединению
- Каждый поток/поток должен работать с отдельным `channel` (не thread-safe)
- В одном `Connection` можно открыть тысячи `channel`

**Жизненный цикл:** `Connection` → открытие `Channel` → операции → закрытие `Channel`.

> [!mcq]
> - [ ] Channel — это физическое TCP-соединение к брокеру, каждый поток обязан использовать один Channel на один Connection. | Channel — виртуальное (логическое) соединение поверх TCP Connection, а не физическое. Именно это позволяет мультиплексировать потоки без накладных расходов TCP.
> - [ ] Channel — это очередь сообщений внутри Connection, буферизующая данные до момента их подтверждения. | Channel не является очередью сообщений. Он предоставляет логический канал для AMQP-операций (publish, consume, declare).
> - [ ] Channel — это компонент балансировки нагрузки, распределяющий сообщения между несколькими Connection. | Балансировка нагрузки в RabbitMQ реализуется через prefetch и multiple consumers, но Channel к этому не относится.
> - [x] Channel — это виртуальное соединение поверх одного TCP Connection, позволяющее мультиплексировать несколько потоков. | Именно так работает Channel: один TCP Connection содержит тысячи Channel, каждый из которых не является thread-safe и должен использоваться из одного потока.

---

## Q5. (!) Какие типы Exchange существуют в RabbitMQ?

```mermaid
graph TD
    subgraph "Типы Exchange"
        D[Direct Exchange]
        F[Fanout Exchange]
        T[Topic Exchange]
        H[Headers Exchange]
    end
    D -->|точный routing key| Q1[Queue]
    F -->|все binding| Q2[Queue]
    F -->|все binding| Q3[Queue]
    T -->|pattern match| Q4[Queue]
    H -->|заголовки сообщения| Q5[Queue]
```

| Тип | Маршрутизация | Применение |
|-----|---------------|------------|
| `direct` | Точное совпадение `routing key` | Простая очередь задач |
| `fanout` | Всем привязанным очередям | Pub/Sub, рассылки |
| `topic` | Паттерн с `*` и `#` | Логи по категориям, события |
| `headers` | По заголовкам сообщения | Сложная маршрутизация без `routing key` |

> [!mcq]
> - [x] Direct Exchange маршрутизирует по точному совпадению routing key, Fanout — всем привязанным очередям, Topic — по паттерну с * и #, Headers — по заголовкам сообщения. | Это точное описание всех четырёх типов. Direct — строгое совпадение, Fanout — broadcast, Topic — паттерн с wildcards, Headers — по атрибутам сообщения, а не routing key.
> - [ ] Direct Exchange маршрутизирует по паттерну с * и #, Fanout — всем привязанным очередям, Topic — по точному совпадению, Headers — по заголовкам сообщения. | Direct и Topic перепутаны: именно Topic использует wildcards (* и #), а Direct требует точного совпадения routing key.
> - [ ] Direct Exchange маршрутизирует всем привязанным очередям, Fanout — по точному совпадению, Topic — по паттерну, Headers — по заголовкам сообщения. | Direct и Fanout перепутаны: именно Fanout рассылает всем привязанным очередям без учёта routing key.
> - [ ] Direct Exchange маршрутизирует по заголовкам сообщения, Fanout — всем привязанным очередям, Topic — по паттерну, Headers — по точному совпадению routing key. | Direct и Headers перепутаны: Headers маршрутизирует по заголовкам, а Direct — по точному совпадению routing key.

> [!mcq]
> - [ ] Тип Topic Exchange лучше всего подходит для простой очереди задач, где каждое сообщение обрабатывается одним worker. | Для простой очереди задач обычно используется Direct Exchange или Default Exchange, а не Topic, который предназначен для гибкой маршрутизации по паттернам.
> - [ ] Тип Fanout Exchange лучше всего подходит для маршрутизации по категориям логов с фильтрацией по сервису и уровню. | Fanout не поддерживает фильтрацию — он рассылает всем. Для маршрутизации по категориям логов используется Topic Exchange с паттернами вроде `service.level`.
> - [x] Тип Fanout Exchange лучше всего подходит для рассылки уведомлений всем подписчикам системы одновременно. | Fanout игнорирует routing key и отправляет копию каждого сообщения во все привязанные очереди — это идеальный паттерн для broadcast/Pub-Sub уведомлений.
> - [ ] Тип Headers Exchange лучше всего подходит для Work Queue с равномерным распределением задач между workers. | Headers Exchange используется для сложной маршрутизации по атрибутам сообщения, а Work Queue реализуется через Direct или Default Exchange с несколькими consumers.

---

## Q6. Как работает Direct Exchange?

**`Direct Exchange`** маршрутизирует сообщение в очередь, у которой `binding key` точно совпадает с `routing key` сообщения.

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

Если `routing key` не совпадает ни с одним `binding` — сообщение отбрасывается (или уходит в `alternate exchange`).

> [!mcq]
> - [ ] Direct Exchange маршрутизирует сообщение во все очереди, у которых binding key начинается с routing key сообщения. | Это поведение Topic Exchange с wildcard `#`, но не Direct. Direct требует полного, точного совпадения binding key и routing key.
> - [x] Direct Exchange маршрутизирует сообщение в очередь, у которой binding key точно совпадает с routing key сообщения. | Точное определение: Direct Exchange выполняет строгое сравнение строк. Если ни одна очередь не имеет совпадающего binding key, сообщение отбрасывается или уходит в alternate exchange.
> - [ ] Direct Exchange маршрутизирует сообщение во все привязанные очереди независимо от routing key сообщения. | Это поведение Fanout Exchange, который игнорирует routing key. Direct Exchange, напротив, строго зависит от routing key.
> - [ ] Direct Exchange маршрутизирует сообщение по заголовкам сообщения, а не по routing key. | По заголовкам маршрутизирует Headers Exchange. Direct Exchange использует исключительно routing key для принятия решения о маршруте.

---

## Q7. Как работает Fanout Exchange?

**`Fanout Exchange`** игнорирует `routing key` и рассылает каждое сообщение во все привязанные очереди. Используется для broadcast/Pub-Sub.

```java
channel.exchangeDeclare("notifications", "fanout", true);
// Привязка без routing key
channel.queueBind("mobile-queue", "notifications", "");
channel.queueBind("email-queue",  "notifications", "");
channel.queueBind("sms-queue",    "notifications", "");

// Публикация (routing key игнорируется)
channel.basicPublish("notifications", "", null, body);
```

Каждый подписчик получает собственную копию сообщения.

> [!mcq]
> - [ ] Fanout Exchange маршрутизирует сообщение только в ту очередь, routing key которой совпадает с ключом сообщения. | Это поведение Direct Exchange. Fanout принципиально игнорирует routing key и отправляет сообщение во все привязанные очереди без исключения.
> - [ ] Fanout Exchange маршрутизирует сообщение по паттерну routing key с использованием символов * и #. | Паттерны * и # используются в Topic Exchange. Fanout не анализирует routing key вообще — он всегда выполняет broadcast.
> - [ ] Fanout Exchange маршрутизирует сообщение по заголовкам, если x-match=any хотя бы для одного заголовка. | Маршрутизация по заголовкам с x-match — это поведение Headers Exchange. Fanout Exchange не использует ни заголовки, ни routing key.
> - [x] Fanout Exchange игнорирует routing key и рассылает сообщение во все привязанные очереди без исключения. | Именно так работает Fanout: каждый подписчик получает собственную копию сообщения. Это классический Pub-Sub паттерн, идеальный для broadcast-уведомлений.

---

## Q8. (!) Как работает Topic Exchange?

**`Topic Exchange`** маршрутизирует сообщения по паттерну `routing key`, используя специальные символы:
- `*` — заменяет ровно одно слово
- `#` — заменяет ноль или более слов

Слова в `routing key` разделяются точкой: `region.service.severity`.

```mermaid
graph LR
    P[Producer] -->|"eu.auth.error"| E[Topic Exchange]
    E -->|"*.*.error"| Q1[all-errors]
    E -->|"eu.#"| Q2[eu-all]
    E -->|"eu.auth.*"| Q3[eu-auth-logs]
```

```java
channel.exchangeDeclare("logs.topic", "topic", true);
channel.queueBind("all-errors",   "logs.topic", "*.*.error");
channel.queueBind("eu-all",       "logs.topic", "eu.#");
channel.queueBind("eu-auth-logs", "logs.topic", "eu.auth.*");

// Сообщение "eu.auth.error" попадёт во все три очереди
channel.basicPublish("logs.topic", "eu.auth.error", null, body);
```

> [!mcq]
> - [ ] В Topic Exchange символ * заменяет ноль или более слов, а символ # заменяет ровно одно слово. | Всё наоборот: * заменяет ровно одно слово, # заменяет ноль или более слов. Эта путаница — частая ошибка на собеседованиях.
> - [x] В Topic Exchange символ * заменяет ровно одно слово, а символ # заменяет ноль или более слов. | Это точные правила Topic Exchange. Например, паттерн `eu.#` совпадает с `eu`, `eu.auth`, `eu.auth.error`, а `*.*.error` совпадёт только с трёхсловными ключами, оканчивающимися на `error`.
> - [ ] В Topic Exchange символ * заменяет любой символ в слове (как glob), а # используется для именования Exchange. | Topic Exchange работает с целыми словами, разделёнными точками, а не с символами внутри слова. Символ # — wildcard для слов, а не для именования.
> - [ ] В Topic Exchange символы * и # используются только в routing key при публикации, но не в binding key. | Всё наоборот: wildcards * и # используются именно в binding key (при привязке очереди к exchange), а в routing key при публикации указывается конкретный ключ без wildcards.

> [!mcq]
> - [ ] Паттерн `eu.#` в binding key совпадёт только с сообщениями, имеющими routing key равный точно `eu`. | Паттерн `eu.#` совпадает с `eu`, `eu.auth`, `eu.auth.error` и любой другой строкой, начинающейся с `eu.`. Символ # заменяет ноль или более слов.
> - [ ] Паттерн `*.*.error` в binding key совпадёт с routing key `eu.error`, состоящим из двух слов. | Паттерн `*.*.error` требует ровно трёх слов: первые два — любые, третье — `error`. Строка `eu.error` содержит только два слова, поэтому не совпадёт.
> - [ ] Паттерн `eu.auth.*` в binding key совпадёт с routing key `eu.auth.error.critical`, состоящим из четырёх слов. | Паттерн `eu.auth.*` совпадает ровно с трёхсловными ключами вида `eu.auth.X`. Четырёхсловный ключ не совпадёт, так как * заменяет только одно слово.
> - [x] Паттерн `*.*.error` в binding key совпадёт с routing key `eu.auth.error`, состоящим ровно из трёх слов. | Паттерн `*.*.error` требует ровно три слова: первые два — любые (по одному на каждый *), третье — буквально `error`. Ключ `eu.auth.error` идеально совпадает.

---

## Q9. Как работает Headers Exchange?

**`Headers Exchange`** маршрутизирует по заголовкам сообщения (`AMQP message headers`), игнорируя `routing key`.

При привязке задаётся аргумент `x-match`:
- `all` — все указанные заголовки должны совпадать
- `any` — хватит совпадения хотя бы одного заголовка

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

Используется редко — `Topic Exchange` гибче и проще.

> [!mcq]
> - [ ] Headers Exchange маршрутизирует по routing key с точным совпадением, а x-match определяет чувствительность к регистру. | Headers Exchange полностью игнорирует routing key. x-match определяет логику совпадения заголовков: `all` — все должны совпасть, `any` — достаточно одного.
> - [x] Headers Exchange маршрутизирует по заголовкам сообщения, где x-match=all требует совпадения всех заголовков, а x-match=any — хотя бы одного. | Точное описание: при привязке задаются заголовки-критерии и x-match. Routing key при этом игнорируется. Используется реже Topic Exchange из-за большей сложности настройки.
> - [ ] Headers Exchange маршрутизирует по паттерну заголовков с использованием wildcards * и #, аналогично Topic Exchange. | Headers Exchange не поддерживает wildcards. Он сравнивает точные значения заголовков. Wildcards * и # — исключительно инструмент Topic Exchange.
> - [ ] Headers Exchange маршрутизирует по заголовкам, где x-match=all означает достаточно одного совпадения, а x-match=any — все должны совпасть. | Значения x-match перепутаны: `all` требует совпадения всех заголовков, `any` — достаточно совпадения хотя бы одного заголовка.

---

## Q10. Что такое Default Exchange?

**`Default Exchange`** (`""`) — системный `direct exchange` без имени, существующий по умолчанию. Каждая очередь автоматически привязывается к нему с `routing key` равным имени очереди.

```java
// Прямая отправка в очередь "task-queue" без явного объявления exchange
channel.basicPublish("", "task-queue", null, body);
```

Удобен для простых случаев, но скрывает маршрутизацию — в продакшне лучше явно объявлять `exchange`.

> [!mcq]
> - [ ] Default Exchange — это Fanout Exchange без имени, автоматически рассылающий сообщения во все очереди при пустом routing key. | Default Exchange — это Direct, а не Fanout. Он использует имя очереди как routing key, поэтому сообщение попадает только в конкретную очередь, а не во все.
> - [ ] Default Exchange — это Topic Exchange без имени, маршрутизирующий по паттерну, равному имени очереди. | Default Exchange — это Direct Exchange. Он выполняет точное совпадение routing key с именем очереди, без wildcards и паттернов Topic.
> - [x] Default Exchange — это системный Direct Exchange без имени, где каждая очередь автоматически привязана с routing key равным имени очереди. | Точное определение: при публикации с exchange="" и routing key="my-queue" сообщение попадает прямо в очередь "my-queue". Это упрощает код, но скрывает маршрутизацию.
> - [ ] Default Exchange — это Headers Exchange без имени, маршрутизирующий по заголовку x-queue-name, равному имени очереди. | Default Exchange — это Direct Exchange, а не Headers. Маршрутизация осуществляется по routing key, а не по заголовкам сообщения.

---

## Q11. (!) Какие свойства у очереди в RabbitMQ?

| Свойство | Тип | Описание |
|----------|-----|----------|
| `durable` | boolean | Очередь переживает перезапуск брокера |
| `exclusive` | boolean | Очередь используется только текущим соединением и удаляется при его закрытии |
| `autoDelete` | boolean | Очередь удаляется, когда последний `consumer` отписывается |
| `arguments` | Map | Дополнительные параметры: `x-message-ttl`, `x-max-length`, `x-dead-letter-exchange` и др. |

> [!mcq]
> - [ ] Свойство durable=true означает, что очередь доступна только текущему Connection и удаляется при его закрытии. | Это описание свойства exclusive, а не durable. Durable означает, что очередь переживает перезапуск брокера.
> - [ ] Свойство autoDelete=true означает, что очередь записывает все сообщения на диск для защиты от потери данных. | autoDelete управляет жизненным циклом очереди (удаляется после отписки всех consumers), а не персистентностью сообщений. Для персистентности используется deliveryMode=2 у сообщений.
> - [x] Свойство durable=true означает, что очередь переживает перезапуск брокера, а exclusive=true — что она доступна только текущему Connection. | Точное описание: durable сохраняет очередь при рестарте, exclusive привязывает очередь к одному Connection и удаляет её при его закрытии. Оба свойства управляют жизненным циклом очереди.
> - [ ] Свойство exclusive=true означает, что очередь удаляется, когда последний consumer отписывается. | Это описание свойства autoDelete. Exclusive означает принадлежность очереди конкретному Connection — она удаляется при закрытии этого Connection, вне зависимости от наличия consumers.

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

---

## Q12. Что такое durable queue и persistent message?

**`durable queue`** — очередь, которая сохраняется при перезапуске `RabbitMQ`. Без `durable=true` очередь и все её сообщения теряются при рестарте.

**`persistent message`** — сообщение с `deliveryMode=2`, которое записывается на диск. Для гарантии сохранности нужны оба условия:

```java
// Persistent message
AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .deliveryMode(2) // persistent
    .build();

channel.basicPublish("my-exchange", "key", props, body);
```

> Важно: `durable` очередь без `persistent` сообщений не гарантирует сохранность — сообщения из памяти теряются при падении.

> [!mcq]
> - [ ] Durable queue достаточно для гарантии сохранности сообщений при перезапуске — persistent message не требуется. | Durable queue только гарантирует выживание самой очереди при рестарте. Если сообщения не имеют deliveryMode=2 (persistent), они хранятся в памяти и теряются при падении брокера.
> - [ ] Persistent message достаточно для гарантии сохранности при перезапуске — durable queue не требуется. | Persistent message записывается на диск, но если очередь не durable, она удаляется при рестарте брокера вместе со всеми сообщениями. Нужны оба условия одновременно.
> - [ ] Durable queue и persistent message гарантируют мгновенную доставку сообщений без задержки на запись. | Durable queue и persistent message обеспечивают надёжность, но не скорость. Запись на диск неизбежно добавляет латентность по сравнению с хранением только в памяти.
> - [x] Для гарантии сохранности сообщений при перезапуске нужны оба условия: durable queue и persistent message (deliveryMode=2). | Именно так: durable queue переживает рестарт брокера, а persistent message (deliveryMode=2) гарантирует запись на диск. Только вместе они обеспечивают at-least-once при падении.

---

## Q13. Что такое exclusive и auto-delete очередь?

**`exclusive`** очередь:
- Доступна только создавшему её `Connection`
- Удаляется автоматически при закрытии `Connection`
- Используется для временных очередей ответов в RPC-паттернах

**`auto-delete`** очередь:
- Удаляется, когда все `consumer` отписались
- Если `consumer` не было вообще — не удаляется немедленно (ждёт хотя бы одного)
- Используется для динамических подписок

> [!mcq]
> - [ ] Exclusive очередь удаляется, когда все consumers отписались, и используется для динамических подписок. | Это описание auto-delete очереди. Exclusive очередь привязана к конкретному Connection и удаляется при его закрытии, вне зависимости от наличия consumers.
> - [ ] Auto-delete очередь доступна только создавшему её Connection и используется для временных очередей ответов в RPC. | Это описание exclusive очереди. Auto-delete удаляется после того, как все consumers отписались, и не ограничивает доступ по Connection.
> - [ ] Exclusive очередь переживает перезапуск брокера и используется для сохранения истории сообщений. | Exclusive очередь — временная конструкция, привязанная к Connection. Она не переживает ни закрытие Connection, ни рестарт брокера. Для сохранности нужна durable очередь.
> - [x] Exclusive очередь удаляется при закрытии создавшего её Connection, auto-delete — когда последний consumer отписывается. | Точное различие: exclusive принадлежит одному Connection (типично для reply-to в RPC), auto-delete удаляется после отписки всех consumers (динамические подписки). Оба — временные механизмы с разными триггерами удаления.

---

## Q14. Как настроить TTL для сообщений и очереди?

**TTL на уровне очереди** — все сообщения в очереди получают одно время жизни:

```java
Map<String, Object> args = new HashMap<>();
args.put("x-message-ttl", 30000); // 30 секунд
channel.queueDeclare("ttl-queue", true, false, false, args);
```

**TTL на уровне сообщения** — индивидуальное время жизни:

```java
AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
    .expiration("30000") // строка в миллисекундах
    .build();
channel.basicPublish("", "ttl-queue", props, body);
```

**TTL самой очереди** (`x-expires`) — очередь удаляется, если не используется N миллисекунд:

```java
args.put("x-expires", 300000); // удалить очередь через 5 минут бездействия
```

При истечении TTL сообщение либо отбрасывается, либо роутится в `DLX`.

> [!mcq]
> - [ ] TTL на уровне сообщения задаётся через аргумент x-message-ttl в виде числа миллисекунд, а TTL очереди — через поле expiration в AMQP-свойствах. | Всё наоборот: x-message-ttl в аргументах queueDeclare задаёт TTL для всех сообщений в очереди, а expiration (строка!) в BasicProperties — TTL конкретного сообщения.
> - [x] TTL на уровне очереди задаётся через x-message-ttl в аргументах queueDeclare, а TTL конкретного сообщения — через expiration в BasicProperties. | Точное описание двух механизмов: x-message-ttl в Map-аргументах при объявлении очереди применяется ко всем сообщениям, а expiration в AMQP BasicProperties — к конкретному сообщению. Оба значения в миллисекундах, но expiration передаётся как строка.
> - [ ] TTL на уровне очереди задаётся через x-expires в аргументах queueDeclare, а TTL сообщений применить невозможно. | x-expires — это TTL самой очереди (когда она удаляется при бездействии), а не TTL сообщений. TTL сообщений задаётся через x-message-ttl или expiration в BasicProperties.
> - [ ] TTL для сообщений применяется только при использовании DLX — без него истёкшие сообщения остаются в очереди навсегда. | TTL работает независимо от DLX. При истечении TTL сообщение либо отбрасывается (если DLX не настроен), либо маршрутизируется в DLX. DLX не является обязательным условием работы TTL.

---

## Q15. Что такое максимальная длина очереди (max-length)?

Ограничивает количество сообщений или объём в байтах:

```java
args.put("x-max-length", 1000);           // максимум 1000 сообщений
args.put("x-max-length-bytes", 1048576);  // максимум 1 МБ
args.put("x-overflow", "drop-head");      // при переполнении: удалить самое старое (default)
// или
args.put("x-overflow", "reject-publish"); // отклонить новое сообщение
```

При `drop-head` и наличии `DLX` удалённые сообщения маршрутизируются в dead letter очередь.

> [!mcq]
> - [ ] При переполнении очереди с x-overflow=drop-head удаляется самое новое сообщение, а не самое старое. | drop-head удаляет именно самое старое (head) сообщение из очереди. Если нужно отклонять новые сообщения — используется x-overflow=reject-publish.
> - [ ] При переполнении очереди с x-overflow=reject-publish удаляется самое старое сообщение из очереди. | reject-publish отклоняет новое входящее сообщение, не трогая существующие. Для удаления старых сообщений используется drop-head.
> - [x] При переполнении очереди с x-overflow=drop-head удаляется самое старое сообщение, а x-overflow=reject-publish отклоняет новое входящее. | Точное описание обоих режимов: drop-head — default, удаляет голову очереди (самое старое); reject-publish — защищает существующие сообщения, отклоняя новые публикации.
> - [ ] x-max-length ограничивает объём очереди в байтах, а x-max-length-bytes — количество сообщений. | Всё наоборот: x-max-length ограничивает количество сообщений, а x-max-length-bytes — объём в байтах. Оба параметра можно использовать одновременно.

---

## Q16. (!) Что такое Binding и Routing Key?

**`Binding`** — правило связи между `Exchange` и `Queue`. Определяет, какие сообщения из `exchange` попадут в данную очередь.

**`Routing Key`** — строка-ключ, которую `producer` указывает при публикации. `Exchange` использует её для маршрутизации (кроме `fanout`).

```mermaid
graph LR
    E[Exchange] -->|binding key = "order.created"| Q1[orders-queue]
    E -->|binding key = "payment.*"| Q2[payments-queue]
    E -->|binding key = "#"| Q3[audit-queue]
```

**Binding Key** в `direct` exchange — точная строка. В `topic` exchange — паттерн с `*` и `#`. В `fanout` — игнорируется.

> [!mcq]
> - [ ] Binding — это правило связи между Producer и Exchange, определяющее, какие сообщения exchange принимает от данного producer. | Binding связывает Exchange и Queue, а не Producer и Exchange. Producer публикует в Exchange напрямую, указывая routing key в момент публикации.
> - [ ] Routing Key — это атрибут очереди, определяющий, какие типы сообщений она принимает от exchange. | Routing Key — это атрибут сообщения (задаётся producer при публикации), а не очереди. Очередь имеет binding key, который используется exchange для сравнения.
> - [x] Binding — это правило связи Exchange и Queue, а Routing Key — строка в сообщении, которую Exchange использует для выбора целевых очередей. | Точное определение обоих понятий. Binding key и routing key сравниваются exchange при маршрутизации. В fanout этот механизм игнорируется, в direct — точное совпадение, в topic — паттерн.
> - [ ] Routing Key — это уникальный идентификатор сообщения в очереди, используемый для дедупликации при повторной доставке. | Routing Key — это ключ маршрутизации, а не идентификатор сообщения. Для идентификации и дедупликации используется correlation ID в заголовках сообщения.

---

## Q17. Можно ли привязать несколько очередей к одному Exchange?

Да, это один из ключевых механизмов маршрутизации. Один `exchange` может быть привязан к неограниченному количеству очередей с разными `binding key`.

Также поддерживается привязка **нескольких exchange** друг к другу (exchange-to-exchange bindings), что позволяет строить сложные топологии маршрутизации.

> [!mcq]
> - [ ] Один exchange может быть привязан только к одной очереди с уникальным binding key для предотвращения дублирования. | RabbitMQ не накладывает такого ограничения. Один exchange может быть привязан к множеству очередей с разными или даже одинаковыми binding key.
> - [ ] Exchange-to-exchange binding позволяет consumer читать сообщения напрямую из exchange без промежуточной очереди. | Exchange-to-exchange binding соединяет два exchange между собой для построения сложных топологий, но consumer всё равно читает из Queue, а не напрямую из Exchange.
> - [x] Один exchange может быть привязан к неограниченному количеству очередей, а также к другим exchange через exchange-to-exchange binding. | Точное описание возможностей: множественные привязки позволяют один publisher fan-out к нескольким очередям, а exchange-to-exchange binding — строить составные топологии маршрутизации.
> - [ ] Exchange-to-exchange binding возможен только для Fanout Exchange и не поддерживается для Direct или Topic. | Exchange-to-exchange binding поддерживается для любых типов exchange: Direct, Topic, Fanout и Headers. Тип exchange влияет на логику маршрутизации, но не на возможность создания binding.

---

## Q18. (!) Что такое acknowledgement (ack) в RabbitMQ?

**`Acknowledgement`** (`ack`) — подтверждение от `consumer` о том, что сообщение успешно обработано. До получения `ack` сообщение остаётся в состоянии `unacked` и не удаляется из очереди.

```mermaid
sequenceDiagram
    Queue->>Consumer: deliver(deliveryTag=1)
    Consumer->>Consumer: обработка
    Consumer->>Queue: basicAck(deliveryTag=1)
    Queue->>Queue: удалить сообщение
```

Если `consumer` упал без `ack` — сообщение переходит обратно в `ready` и будет доставлено другому `consumer`.

```java
channel.basicConsume("my-queue", false, (consumerTag, delivery) -> {
    try {
        process(delivery.getBody());
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    } catch (Exception e) {
        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
    }
}, consumerTag -> {});
```

Параметр `multiple=true` в `basicAck` подтверждает все сообщения до указанного `deliveryTag`.

---

## Q19. Чем отличаются nack и reject?

| Метод | Параметры | Описание |
|-------|-----------|----------|
| `basicAck` | `deliveryTag, multiple` | Успешная обработка |
| `basicNack` | `deliveryTag, multiple, requeue` | Неуспешно, можно переотправить несколько |
| `basicReject` | `deliveryTag, requeue` | Неуспешно, только одно сообщение |

`requeue=true` — сообщение возвращается в очередь (риск бесконечного цикла).  
`requeue=false` — сообщение отбрасывается или роутится в `DLX`.

Основное отличие `nack` от `reject` — поддержка параметра `multiple` для массового отклонения.

---

## Q20. (!) В чём разница между auto-ack и manual ack?

**`auto-ack`** (`autoAck=true`):
- Брокер считает сообщение обработанным сразу после доставки
- Максимальная скорость, но нет гарантий обработки
- При падении `consumer` сообщение теряется

**`manual ack`** (`autoAck=false`):
- `Consumer` явно вызывает `basicAck` / `basicNack` / `basicReject`
- Гарантирует at-least-once доставку
- Требует правильной обработки ошибок

> В продакшне всегда используйте `manual ack` для надёжной обработки.

---

## Q21. Что такое Publisher Confirms?

**`Publisher Confirms`** — механизм подтверждения от брокера к `producer` о том, что сообщение принято и сохранено (в очереди или на диске).

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

`Publisher Confirms` несовместимы с транзакциями на одном `channel`.

---

## Q22. Чем Publisher Confirms отличаются от транзакций AMQP?

| Аспект | `Publisher Confirms` | Транзакции (`txSelect`) |
|--------|---------------------|------------------------|
| Производительность | Высокая (асинхронно) | Низкая (синхронно, блокирует) |
| Семантика | At-least-once | At-least-once |
| Батч | Да | Да (`txCommit`) |
| Rollback | Нет | Да (`txRollback`) |
| Рекомендуется | Да | Нет (устарело) |

Транзакции в `AMQP` снижают производительность в 250 раз по сравнению с `Publisher Confirms`. Используйте `Publisher Confirms`.

---

## Q23. (!) Что такое Dead Letter Exchange (DLX) и DLQ?

**`DLX`** (`Dead Letter Exchange`) — специальный `exchange`, куда брокер маршрутизирует сообщения, которые не могут быть обработаны.

**`DLQ`** (`Dead Letter Queue`) — очередь, привязанная к `DLX`, которая собирает «мёртвые» сообщения.

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

Сообщение становится «мёртвым» в трёх случаях:

1. **Rejected** — `consumer` вызвал `basicReject` или `basicNack` с `requeue=false`
2. **TTL истёк** — время жизни сообщения или очереди истекло
3. **Queue overflow** — очередь достигла `x-max-length` и политика — `drop-head`

В DLX-сообщение добавляется заголовок `x-death` с информацией о причине, количестве смертей и исходной очереди.

---

## Q25. Как реализовать retry с использованием DLX и TTL?

Паттерн **delayed retry** через `DLX` и `TTL`:

```mermaid
graph LR
    Q[Main Queue] -->|nack| DLX[DLX]
    DLX --> RQ[Retry Queue\nx-message-ttl=5000]
    RQ -->|после TTL| ME[Main Exchange]
    ME --> Q
```

```java
// Retry Queue — задержка 5 секунд, после истечения TTL → обратно в main exchange
Map<String, Object> retryArgs = new HashMap<>();
retryArgs.put("x-message-ttl", 5000);
retryArgs.put("x-dead-letter-exchange", "main-exchange");
retryArgs.put("x-dead-letter-routing-key", "main-key");
channel.queueDeclare("retry-queue", true, false, false, retryArgs);
channel.queueBind("retry-queue", "dlx", "retry");

// При ошибке в consumer
channel.basicNack(deliveryTag, false, false); // requeue=false → в DLX → retry-queue
```

Для ограничения числа попыток используют заголовок `x-death[count]`.

---

## Q26. (!) Что такое prefetch count и QoS?

**`Prefetch count`** (`basicQos`) — максимальное количество сообщений `unacked`, которые брокер отправит `consumer` до получения `ack`.

```java
// Consumer получит не более 10 сообщений без подтверждения
channel.basicQos(10);
```

**Без prefetch** (`prefetchCount=0`) — брокер отправит все доступные сообщения сразу, перегружая `consumer`.

**С prefetchCount=1** — честное распределение (fair dispatch): каждый `consumer` получает новое сообщение только после обработки предыдущего.

```mermaid
graph LR
    Q[Queue: 100 msg] -->|prefetch=1| C1[Consumer 1\nbыстрый]
    Q -->|prefetch=1| C2[Consumer 2\nмедленный]
    C1 -->|обработал 80| R1[80 messages]
    C2 -->|обработал 20| R2[20 messages]
```

---

## Q27. Как prefetch влияет на производительность и нагрузку?

| `prefetchCount` | Поведение | Применение |
|-----------------|-----------|------------|
| `0` | Без ограничений, все сообщения сразу | Не рекомендуется |
| `1` | Строго по одному, fair dispatch | Долгие задачи, неравномерная нагрузка |
| `10–100` | Хороший баланс | Большинство случаев |
| Высокий (>100) | Высокий throughput | Быстрая обработка, батчи |

`prefetchCount` на уровне `channel` (`global=false`) — каждый `consumer` на канале получает свой лимит. С `global=true` — общий лимит для всего канала.

---

## Q28. (!) Как работает кластеризация в RabbitMQ?

Кластер `RabbitMQ` — несколько узлов (`node`), объединённых в единое логическое целое:

```mermaid
graph TD
    subgraph "RabbitMQ Cluster"
        N1[Node 1\nram]
        N2[Node 2\ndisc]
        N3[Node 3\ndisc]
        N1 <--> N2
        N2 <--> N3
        N1 <--> N3
    end
    P[Producer] --> N1
    C[Consumer] --> N3
```

**Что реплицируется:**
- Метаданные: `exchange`, `binding`, `vhost`, пользователи, политики
- Очереди **НЕ реплицируются** по умолчанию (только на одном узле)

**Типы узлов:**
- `disc` — хранит метаданные на диске (минимум один в кластере)
- `ram` — хранит только в памяти (быстрее, но не переживает рестарт)

**Network partition** — кластер может расщепиться на несколько частей; стратегии: `ignore`, `pause-minority`, `autoheal`.

---

## Q29. Что такое Mirrored Queues (Classic HA)?

**`Mirrored Queues`** (устаревший механизм, Classic HA) — очереди с зеркалами на нескольких узлах кластера.

- Один узел — **master** (принимает все операции)
- Остальные — **slaves** (зеркала, синхронизируются с master)
- При падении master — один из slaves становится новым master

**Проблемы Mirrored Queues:**
- Высокая нагрузка на сеть при синхронизации
- Потенциальная потеря сообщений при split-brain
- `RabbitMQ 3.13` объявил их устаревшими в пользу `Quorum Queues`

---

## Q30. (!) Что такое Quorum Queues и чем они лучше Mirrored?

**`Quorum Queues`** — современный тип реплицированных очередей на основе алгоритма **Raft** (как в `etcd`, `Consul`).

| Характеристика | Mirrored Queues | Quorum Queues |
|----------------|-----------------|---------------|
| Алгоритм | Custom | Raft |
| Гарантии | Слабее | Stronger (majority) |
| Split-brain | Уязвим | Устойчив |
| Производительность | Ниже | Выше |
| Поддержка | Deprecated | Recommended |
| Статус | Устарел в 3.9 | Рекомендован с 3.9 |

```java
// Spring AMQP: объявление Quorum Queue
@Bean
public Queue quorumQueue() {
    return QueueBuilder.durable("my-quorum-queue")
        .quorum()
        .build();
}
```

`Quorum Queues` гарантируют консистентность при наличии большинства (`quorum`) узлов. Не поддерживают `x-message-ttl`, `priority queues`.

---

## Q31. Что такое Federation и Shovel в RabbitMQ?

**`Federation`** — плагин для связи `exchange` или `queue` между разными брокерами (в разных датацентрах). Сообщения перемещаются только при наличии `consumer`.

**`Shovel`** — плагин для непрерывного перекачивания сообщений из одной очереди в другую (в том числе на удалённом брокере). Полезен для:
- Миграции сообщений
- Репликации между датацентрами
- Переброски из `DLQ` после починки

Разница: `Federation` — логическая связь exchange/queue; `Shovel` — явное перемещение сообщений.

---

## Q32. (!) Что такое Spring AMQP и RabbitTemplate?

**`Spring AMQP`** — абстракция Spring для работы с `AMQP`-брокерами. Состоит из двух модулей:
- `spring-amqp` — базовые абстракции (не зависит от `RabbitMQ`)
- `spring-rabbit` — реализация для `RabbitMQ`

**`RabbitTemplate`** — основной класс для отправки и синхронного получения сообщений:

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

**`MessageConverter`** (по умолчанию `SimpleMessageConverter`) преобразует объекты в/из `AMQP Message`. Рекомендуется `Jackson2JsonMessageConverter` для JSON.

---

## Q33. Как объявить очередь и exchange через Spring AMQP?

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
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
```

---

## Q34. (!) Как настроить @RabbitListener?

```java
@Component
public class OrderListener {

    // Простой listener
    @RabbitListener(queues = "orders.queue")
    public void handleOrder(Order order) {
        orderService.process(order);
    }

    // С доступом к заголовкам и метаданным
    @RabbitListener(queues = "orders.queue")
    public void handleWithHeaders(
        @Payload Order order,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey,
        Channel channel) throws IOException {
        try {
            orderService.process(order);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false); // в DLQ
        }
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

**`SimpleMessageListenerContainer`** vs **`DirectMessageListenerContainer`**:
- `Simple` — пул потоков, каждый consumer в отдельном thread
- `Direct` — каждый consumer в thread amqp-library, меньше overhead

---

## Q35. Как настроить retry-логику в Spring AMQP?

**Stateless retry** (через `Spring Retry`):

```yaml
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

**Stateful retry** (с отслеживанием состояния, для `manual ack`):

```java
@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
    ConnectionFactory connectionFactory,
    MessageConverter converter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(converter);
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

    RetryInterceptorBuilder<?> builder = RetryInterceptorBuilder.stateless()
        .maxAttempts(3)
        .backOffOptions(1000, 2.0, 10000)
        .recoverer(new RejectAndDontRequeueRecoverer()); // в DLQ после исчерпания попыток
    factory.setAdviceChain(builder.build());
    return factory;
}
```

---

## Q36. (!) Какие основные паттерны обмена сообщениями реализуются в RabbitMQ?

```mermaid
graph TD
    subgraph "Work Queue (Task Queue)"
        WP[Producer] --> WQ[Queue]
        WQ --> WC1[Worker 1]
        WQ --> WC2[Worker 2]
    end

    subgraph "Pub/Sub"
        PP[Producer] --> FE[Fanout Exchange]
        FE --> FQ1[Queue 1 → Sub 1]
        FE --> FQ2[Queue 2 → Sub 2]
    end

    subgraph "RPC"
        RP[Client] -->|reply_to=callback.queue| RE[RPC Queue]
        RE --> RS[RPC Server]
        RS --> RQ[callback.queue]
        RQ --> RP
    end
```

| Паттерн | Exchange | Описание |
|---------|----------|----------|
| Work Queue | Default/Direct | Распределение задач между workers |
| Pub/Sub | Fanout | Рассылка событий всем подписчикам |
| Routing | Direct | Маршрутизация по типу события |
| Topic | Topic | Гибкая маршрутизация по паттерну |
| RPC | Default | Синхронный запрос/ответ через очереди |
| Delayed | DLX + TTL | Отложенные сообщения |

---

## Q37. Как реализовать паттерн RPC через RabbitMQ?

```mermaid
sequenceDiagram
    Client->>RPC Queue: request (reply_to=callback-queue, correlationId=123)
    RPC Server->>RPC Queue: consume
    RPC Server->>Callback Queue: response (correlationId=123)
    Client->>Callback Queue: consume (filter by correlationId=123)
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

// Ожидание ответа с matching correlationId
BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
    if (correlationId.equals(delivery.getProperties().getCorrelationId())) {
        response.offer(new String(delivery.getBody()));
    }
}, tag -> {});

return response.poll(10, TimeUnit.SECONDS);
```

В `Spring AMQP` используйте `rabbitTemplate.convertSendAndReceive()`.

---

## Q38. Как осуществляется мониторинг RabbitMQ через Management Plugin?

**Management Plugin** предоставляет:
- Web UI: `http://localhost:15672`
- HTTP API: `http://localhost:15672/api/`
- CLI: `rabbitmqadmin`

**Ключевые страницы UI:**
- **Overview** — общее состояние кластера, connections, channels, queues
- **Queues** — глубина очередей, скорость publish/consume/ack
- **Connections/Channels** — активные соединения
- **Admin** — пользователи, vhost, политики

```bash
# Просмотр очередей через CLI
rabbitmqadmin list queues name messages consumers

# Экспорт конфигурации
rabbitmqadmin export config.json
```

Для продакшн-мониторинга рекомендуется интеграция с `Prometheus` через плагин `rabbitmq_prometheus`.

---

## Q39. Какие метрики RabbitMQ наиболее важны?

| Метрика | Описание | Тревожный порог |
|---------|----------|-----------------|
| `queue_messages_ready` | Кол-во сообщений, готовых к доставке | Растёт > N |
| `queue_messages_unacknowledged` | Кол-во `unacked` сообщений | Растёт долго |
| `queue_consumers` | Количество активных consumers | = 0 |
| `connection_count` | Кол-во активных соединений | Близко к лимиту |
| `channel_count` | Кол-во каналов | Слишком высокое |
| `disk_free` | Свободное место на диске | < `disk_free_limit` |
| `mem_used` | Использование памяти | > `vm_memory_high_watermark` |
| `publish_rate` | Скорость публикации (msg/s) | — |
| `deliver_ack_rate` | Скорость подтверждённой доставки | Ниже publish rate |

При достижении `vm_memory_high_watermark` (по умолчанию 40% RAM) брокер включает **flow control** и блокирует publishers.

---

## Q40. (!) В чём принципиальные отличия RabbitMQ от Kafka?

| Характеристика | `RabbitMQ` | `Apache Kafka` |
|----------------|------------|----------------|
| Модель | Push (broker → consumer) | Pull (consumer тянет) |
| Хранение | Сообщения удаляются после ack | Лог хранится N дней |
| Порядок | В рамках одной очереди | В рамках партиции |
| Replay | Нет (только DLQ) | Да (любой offset) |
| Маршрутизация | Гибкая (exchange, binding) | По топику/партиции |
| Пропускная способность | Умеренная (100K msg/s) | Очень высокая (1M+ msg/s) |
| Latency | Низкая (<1ms) | Чуть выше (batch) |
| Consumer groups | Конкуренция или подписка | Независимые группы |
| Протокол | `AMQP`, `STOMP`, `MQTT` | Собственный |
| Use case | Задачи, RPC, сложная маршрутизация | Стриминг, аналитика, event log |

---

## Q41. Когда выбрать RabbitMQ, а когда Kafka?

**Выбирайте `RabbitMQ` когда:**
- Нужна сложная маршрутизация сообщений (topic patterns, headers)
- Важна низкая задержка для отдельных сообщений
- Нужен паттерн Work Queue с равномерным распределением нагрузки
- Требуется RPC через messaging
- Разные типы сообщений с разными получателями
- Команда знакома с `AMQP`; интеграция через `STOMP` / `MQTT`
- Объём сообщений умеренный, не требуется долгосрочное хранение

**Выбирайте `Kafka` когда:**
- Нужен event log с возможностью replay
- Очень высокий throughput (миллионы событий/сек)
- Событийная история важна для аналитики или audit trail
- Нужна интеграция с Big Data / stream processing (`Flink`, `Spark`)
- Multiple independent consumer groups читают один поток
- Долгосрочное хранение событий

> Оба инструмента могут сосуществовать в одной системе: `Kafka` — для event streaming, `RabbitMQ` — для task queues и RPC.

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
