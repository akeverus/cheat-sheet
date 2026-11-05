---
title: "Вопросы на собеседовании: WebSocket"
description: "Практичные вопросы и ответы по WebSocket для Java-разработчика: handshake, frames, STOMP, SockJS, Spring WebSocket, STOMP-брокеры, масштабирование, безопасность, реактивный стек."
tags:
  - interview
  - api
  - websocket-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "WebSocket"
  - "WebSocket interview"
  - "WebSocket собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `WebSocket`

Практичные вопросы и ответы по `WebSocket`: протокол `RFC 6455`, handshake через `HTTP/1.1 Upgrade`, двусторонний обмен фреймами, `STOMP` поверх `WebSocket`, `SockJS` как fallback, интеграция со `Spring Boot`, масштабирование через внешний брокер и безопасность.

**`WebSocket`** — протокол двусторонней связи поверх одного TCP-соединения, стандартизированный в `RFC 6455` (2011). В варианте HTTP/1.1 после Upgrade соединение переходит в режим двусторонних сообщений: обе стороны могут отправлять данные в любой момент без накладных расходов на повторное установление соединения.

## Полезные ссылки

### Первичные источники

- [RFC 6455: The WebSocket Protocol](https://datatracker.ietf.org/doc/html/rfc6455) — спецификация протокола
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html) — официальная документация Spring
- [STOMP Protocol Specification](https://stomp.github.io/stomp-specification-1.2.html) — спецификация STOMP 1.2
- [SockJS Client](https://github.com/sockjs/sockjs-client) — клиентская библиотека
- [Spring Security: WebSocket Security](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html) — Origin, CSRF и права сообщений
- [Spring WebFlux: WebSockets](https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html) — реактивный обработчик

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы WebSocket**
- [Q1. Что такое WebSocket и чем он отличается от HTTP?](#q1)
- [Q2. Как происходит WebSocket handshake?](#q2)
- [Q3. Что такое WebSocket frames и какие типы существуют?](#q3)
- [Q4. Чем WebSocket отличается от HTTP Long Polling, SSE и gRPC Streaming?](#q4)

**STOMP протокол**
- [Q5. Что такое STOMP и зачем он нужен поверх WebSocket?](#q5)
- [Q6. Какие команды определяет STOMP?](#q6)
- [Q7. Как устроен фрейм STOMP?](#q7)

**Spring WebSocket (низкий уровень)**
- [Q8. Как настроить WebSocket в Spring без STOMP?](#q8)
- [Q9. Что такое `WebSocketHandler` и его методы?](#q9)
- [Q10. Что такое `WebSocketSession` и как отправить сообщение?](#q10)

**Spring STOMP (высокий уровень)**
- [Q11. Как настроить STOMP поверх WebSocket в Spring Boot?](#q11)
- [Q12. Как работает `@MessageMapping` и `@SendTo`?](#q12)
- [Q13. Чем `@SubscribeMapping` отличается от `@MessageMapping`?](#q13)
- [Q14. Как отправить сообщение конкретному пользователю через `@SendToUser`?](#q14)
- [Q15. Как отправить сообщение из сервиса вне контроллера через `SimpMessagingTemplate`?](#q15)

**SockJS**
- [Q16. Что такое SockJS и когда его использовать?](#q16)
- [Q17. Как SockJS выбирает WebSocket, HTTP streaming и polling?](#q17)

**Message Broker**
- [Q18. Чем отличается простой (in-memory) брокер от внешнего STOMP-брокера?](#q18)
- [Q19. Как настроить RabbitMQ как внешний STOMP-брокер в Spring?](#q19)

**Масштабирование**
- [Q20. Почему масштабирование WebSocket сложнее, чем REST? Как решается проблема sticky sessions?](#q20)
- [Q21. Как работает pub/sub через внешний брокер при горизонтальном масштабировании?](#q21)

**Безопасность**
- [Q22. Как защитить WebSocket-соединение: Origin, аутентификация, Spring Security?](#q22)
- [Q23. Как защищать WebSocket от cross-site запросов и можно ли использовать CSRF-токен в STOMP?](#q23)

**Обработка ошибок и переподключение**
- [Q24. Как обрабатывать ошибки STOMP на сервере через `@MessageExceptionHandler`?](#q24)
- [Q25. Как реализовать автоматическое переподключение на клиенте (JS)?](#q25)

**Heartbeat и keepalive**
- [Q26. Как работает heartbeat в STOMP и как его настроить в Spring?](#q26)

**Реактивный стек**
- [Q27. Как работает WebSocket в Spring WebFlux (реактивный стек)?](#q27)
- [Q28. Что такое `WebSocketHandler` в WebFlux и как он отличается от servlet-версии?](#q28)

**Производительность**
- [Q29. Какие ограничения у WebSocket по количеству соединений и как их обойти?](#q29)
- [Q30. Как настроить размер буферов и таймауты для WebSocket в Spring?](#q30)

**Микросервисы, сравнение, эксплуатация**
- [Q31. WebSocket в микросервисной архитектуре: service discovery, gateway, routing](#q31)
- [Q32. WebSocket vs SSE vs Long Polling: детальное сравнение](#q32)
- [Q33. Мониторинг WebSocket: метрики, количество соединений, health](#q33)
- [Q34. WebSocket в Kubernetes: sticky sessions, session affinity, проблемы](#q34)
- [Q35. Бинарные и текстовые фреймы: MessagePack, Protobuf](#q35)
- [Q36. WebSocket и CDN: почему CDN не кэширует WS, CloudFlare](#q36)
- [Q37. Disconnect стратегии: exponential backoff reconnect на клиенте](#q37)
- [Q38. Тестирование WebSocket в Spring: от unit до интеграции](#q38)

---

<a id="q1"></a>

## Q1. Что такое WebSocket и чем он отличается от HTTP?

WebSocket — протокол двустороннего обмена сообщениями по постоянному соединению ([RFC 6455](https://www.rfc-editor.org/rfc/rfc6455.html)). В распространённом варианте соединение начинается с HTTP/1.1 Upgrade; после него обе стороны передают фреймы без нового HTTP-запроса на каждое сообщение. `ws://` и `wss://` различаются наличием TLS.

HTTP тоже поддерживает длительные соединения и потоковые ответы, например SSE. WebSocket выбирают, когда нужен двусторонний обмен с малой задержкой: чат, совместное редактирование, игры. Накладные расходы зависят от длины фрейма и маскирования, поэтому универсальное сравнение «2–10 байт против сотен» некорректно. WebSocket можно устанавливать и поверх новых версий HTTP другими механизмами; `101 Switching Protocols` относится именно к HTTP/1.1.

---

<a id="q2"></a>

## Q2. Как происходит WebSocket handshake?

При WebSocket поверх HTTP/1.1 клиент отправляет `GET` с `Upgrade: websocket`, `Connection: Upgrade`, случайным `Sec-WebSocket-Key` и `Sec-WebSocket-Version: 13`. Сервер отвечает `101 Switching Protocols` и вычисляет `Sec-WebSocket-Accept` как Base64 от SHA-1 по строке «ключ + GUID `258EAFA5-E914-47DA-95CA-C5AB0DC85B11`».

`Sec-WebSocket-Key` и `Accept` подтверждают корректный handshake, а не личность сервера. Для конфиденциальности и аутентификации TLS используют `wss://`; прикладная аутентификация настраивается отдельно. После 101 на соединении идут WebSocket-фреймы. Варианты WebSocket поверх HTTP/2 и HTTP/3 используют иной механизм установления соединения.

---

<a id="q3"></a>

## Q3. Что такое WebSocket frames и какие типы существуют?

Фрейм WebSocket несёт opcode, признак конца сообщения `FIN`, длину и payload. Базовые opcode: `0x0` continuation, `0x1` text (UTF-8), `0x2` binary, `0x8` close, `0x9` ping, `0xA` pong. Сообщение может состоять из нескольких фреймов; границы WebSocket-фреймов не обязаны совпадать с TCP-сегментами.

Клиент маскирует каждый отправленный серверу фрейм, сервер свои фреймы не маскирует. Маска не шифрует данные: она защищает промежуточные узлы от интерпретации управляемого клиентом payload как HTTP. Размер заголовка меняется с длиной payload и наличием маски.

---

<a id="q4"></a>

## Q4. Чем WebSocket отличается от HTTP Long Polling, SSE и gRPC Streaming?

WebSocket даёт двусторонний обмен сообщениями по открытому соединению. SSE доставляет поток событий сервер→клиент по HTTP; браузерный `EventSource` пытается переподключаться автоматически. Long polling завершает HTTP-запрос при появлении данных, затем клиент отправляет следующий запрос.

У gRPC есть server streaming, client streaming и bidirectional streaming поверх HTTP/2. Поэтому «gRPC streaming всегда full-duplex» неточно. Для браузера доступность конкретного режима зависит от клиента и прокси. Выбор определяется направлением обмена, частотой сообщений, требованиями к восстановлению и возможностями инфраструктуры.

---

<a id="q5"></a>

## Q5. Что такое STOMP и зачем он нужен поверх WebSocket?

WebSocket определяет обмен сообщениями, но не задаёт темы, подписки и подтверждения доставки. Текстовый протокол STOMP добавляет команды `SEND`, `SUBSCRIBE`, `UNSUBSCRIBE`, `ACK`/`NACK`, транзакции и destination-адресацию. STOMP может работать поверх WebSocket и других транспортов.

Реальные гарантии зависят от брокера. Встроенный simple broker Spring поддерживает только часть STOMP; подтверждения и сохранность сообщений не появляются автоматически от выбора STOMP. Для простого двустороннего канала STOMP может быть лишним.

---

<a id="q6"></a>

## Q6. Какие команды определяет STOMP?

Команды STOMP делятся на две группы по тому, кто их отправляет: клиент управляет своей подпиской и шлёт сообщения, а сервер/брокер доставляет сообщения и подтверждает обработку. На собеседовании достаточно помнить «большую четвёрку» клиента — `CONNECT`, `SUBSCRIBE`, `SEND`, `DISCONNECT` — и серверный `MESSAGE`.

**Команды клиента:**

| Команда | Описание |
|---|---|
| `CONNECT` | Установка сессии с брокером (заголовки `login`, `passcode`, `heart-beat`) |
| `DISCONNECT` | Корректное закрытие сессии |
| `SUBSCRIBE` | Подписка на destination, возвращает `id` подписки |
| `UNSUBSCRIBE` | Отмена подписки по `id` |
| `SEND` | Отправка сообщения на destination |
| `ACK` | Подтверждение получения сообщения |
| `NACK` | Негативное подтверждение |
| `BEGIN` / `COMMIT` / `ABORT` | Транзакции |

**Команды сервера/брокера:**

| Команда | Описание |
|---|---|
| `CONNECTED` | Ответ на `CONNECT` |
| `MESSAGE` | Доставка сообщения подписчику |
| `RECEIPT` | Подтверждение обработки (если клиент послал `receipt` заголовок) |
| `ERROR` | Ошибка; брокер закрывает соединение |

---

<a id="q7"></a>

## Q7. Как устроен фрейм STOMP?

Фрейм STOMP устроен почти как HTTP-сообщение и читается человеком: первая строка — команда, дальше заголовки `key:value`, пустая строка-разделитель, затем тело и завершающий NULL-байт. Именно текстовость делает STOMP удобным для отладки — фрейм видно в сетевой панели браузера «как есть».

```
SEND
destination:/app/chat
content-type:application/json
content-length:28

{"text":"Hello, WebSocket!"}^@
```

- **Первая строка** — команда (`SEND`, `SUBSCRIBE`, `MESSAGE` и т.д.)
- **Заголовки** — `key:value` (один на строку)
- **Пустая строка** — разделитель
- **Тело** — произвольный контент (JSON, текст, binary)
- **`^@`** (NULL байт, `\0`) — терминатор фрейма

---

<a id="q8"></a>

## Q8. Как настроить WebSocket в Spring без STOMP?

Для raw WebSocket в Spring MVC используют `@EnableWebSocket`, `WebSocketConfigurer` и `WebSocketHandler`. Обработчик регистрируют на нужный путь, а список Origin задают по аудитории сервиса. SockJS добавляют только если нужен HTTP fallback.

```java
@Configuration
@EnableWebSocket
class WsConfig implements WebSocketConfigurer {
    private final WebSocketHandler handler;

    WsConfig(WebSocketHandler handler) { this.handler = handler; }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws")
                .setAllowedOrigins("https://app.example.org");
    }
}
```

Такой handler получает сообщения напрямую. Для маршрутизации STOMP применяют отдельную конфигурацию из Q11.

---

<a id="q9"></a>

## Q9. Что такое `WebSocketHandler` и его методы?

`WebSocketHandler` получает callbacks при установлении соединения, поступлении сообщений, транспортной ошибке и обнаруженном закрытии. `afterConnectionEstablished` вызывается после handshake. `handleTransportError` бывает только при ошибке; фиксированной последовательности «сообщение → ошибка → закрытие» нет.

Обычно расширяют `TextWebSocketHandler`. Если сессии хранятся в общем реестре, он должен поддерживать конкурентный доступ. При аварийном завершении процесса `afterConnectionClosed` не гарантирован. Spring не обещает отдельный поток на каждую сессию или сообщение: модель зависит от контейнера и способа обработки.

---

<a id="q10"></a>

## Q10. Что такое `WebSocketSession` и как отправить сообщение?

`WebSocketSession` представляет одно соединение. `sendMessage(new TextMessage(payload))` отправляет текст; `BinaryMessage` — байты. `getAttributes()` содержит атрибуты, переданные при handshake, а `isOpen()` показывает состояние на момент проверки.

Конкурентные отправки в одну сессию сериализуют, например через `ConcurrentWebSocketSessionDecorator`. Одна проверка `isOpen()` не исключает закрытия соединения сразу после неё, поэтому ошибки отправки тоже обрабатывают. Код закрытия 1006 является зарезервированным обозначением аномального завершения и не отправляется в Close-фрейме.

---

<a id="q11"></a>

## Q11. Как настроить STOMP поверх WebSocket в Spring Boot?

Spring MVC включает STOMP через `@EnableWebSocketMessageBroker` и `WebSocketMessageBrokerConfigurer`. `enableSimpleBroker("/topic", "/queue")` обслуживает брокерные destination, а `setApplicationDestinationPrefixes("/app")` направляет `SEND /app/...` в методы `@MessageMapping`. В `registerStompEndpoints` задают адрес handshake.

```java
@Configuration
@EnableWebSocketMessageBroker
class StompConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://app.example.org");
    }
}
```

`.withSockJS()` добавляют при необходимости fallback. Origin `*` в защищённом сервисе требует отдельного обоснования.

---

<a id="q12"></a>

## Q12. Как работает `@MessageMapping` и `@SendTo`?

`@MessageMapping("/hello")` обрабатывает STOMP `SEND /app/hello`. Возвращённый результат Spring преобразует в сообщение и отправляет через brokerChannel. `@SendTo("/topic/greetings")` задаёт адрес явно; без этой аннотации адрес по умолчанию выводится из входящего destination с префиксом `/topic`.

```java
@MessageMapping("/hello")
@SendTo("/topic/greetings")
public Greeting greeting(HelloMessage message) {
    return new Greeting("Hello, " + message.getName());
}
```

`@SendToUser` выбирают для ответа в user destination. Метод с `void` тоже может отправить сообщение вручную через `SimpMessagingTemplate`.

---

<a id="q13"></a>

## Q13. Чем `@SubscribeMapping` отличается от `@MessageMapping`?

`@SubscribeMapping` обрабатывает STOMP `SUBSCRIBE`. Если метод возвращает значение без `@SendTo`, Spring отправляет его прямо подписавшейся сессии, минуя брокер. Это удобно для начального снимка состояния. С `@SendTo` возвращённое значение идёт через брокер.

`@MessageMapping` обрабатывает `SEND`; его возвращённое значение обычно публикуется через brokerChannel. Отдельную отправку из метода можно сделать и через `SimpMessagingTemplate`, поэтому слово «всегда» для всех действий метода здесь не подходит.

---

<a id="q14"></a>

## Q14. Как отправить сообщение конкретному пользователю через `@SendToUser`?

Клиент подписывается на логический адрес `/user/queue/messages`. `@SendToUser("/queue/messages")` направляет результат в user destination; Spring преобразует его в адрес конкретной сессии или сессий пользователя.

Для именованного `Principal` доставка по умолчанию может охватывать несколько его сессий. `@SendToUser(broadcast = false)` ограничивает ответ исходной сессией. Анонимная WebSocket-сессия тоже может получить ответ через user destination; для неё доставка фактически ограничена этой сессией. Аннотация не заменяет проверку прав на отправку и подписку.

---

<a id="q15"></a>

## Q15. Как отправить сообщение из сервиса вне контроллера через `SimpMessagingTemplate`?

Контроллеры с `@MessageMapping` реагируют только на входящее сообщение от клиента. Но часто push нужен «сам по себе» — по событию из БД, по расписанию, из Kafka-listener. Для этого Spring даёт бин `SimpMessagingTemplate`: его инжектят в любой сервис и публикуют сообщение в брокер императивно.

- `convertAndSend("/topic/...", payload)` — broadcast всем подписчикам топика.
- `convertAndSendToUser(username, "/queue/...", payload)` — адресно одному пользователю (тот же механизм, что у `@SendToUser` из Q14).

```java
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Отправка всем подписчикам топика
    public void publishToAll(String event) {
        messagingTemplate.convertAndSend("/topic/notifications", event);
    }

    // Отправка конкретному пользователю
    public void notifyUser(String username, String event) {
        messagingTemplate.convertAndSendToUser(username, "/queue/alerts", event);
    }
}

// Из другого сервиса или планировщика используется тот же template.
```

---

<a id="q16"></a>

## Q16. Что такое SockJS и когда его использовать?

SockJS даёт WebSocket-подобный API и, когда WebSocket недоступен, использует HTTP streaming или polling. Для fallback нужна поддержка и на сервере (`withSockJS()`), и у клиента (`SockJS`). Библиотека полезна при ограничениях браузера или прокси, но дополнительные HTTP-запросы могут потребовать отдельной настройки маршрутизации.

Клиент сначала запрашивает `/info`, затем выбирает доступный транспорт. SockJS не гарантирует подключение при любом сетевом запрете. Его собственный API соединения не следует путать с командами `subscribe` и `send` библиотеки STOMP, которую можно положить поверх SockJS.

---

<a id="q17"></a>

## Q17. Как SockJS выбирает WebSocket, HTTP streaming и polling?

Схема выбора SockJS: запрос `/info`, затем WebSocket при доступности; иначе подходящий HTTP streaming, затем HTTP polling. Конкретный транспорт зависит от возможностей браузера, сервера, ограничений сети и списка разрешённых транспортов.

Фиксированный список «websocket → xhr-streaming → iframe-eventsource → ...» не является универсальным порядком для всех браузеров. Для диагностики смотрят фактические запросы браузера и настройки SockJS, а не выводят транспорт только из User-Agent.

---

<a id="q18"></a>

## Q18. Чем отличается простой (in-memory) брокер от внешнего STOMP-брокера?

Spring simple broker хранит подписки в памяти одного экземпляра и поддерживает ограниченную часть STOMP. Он не реплицирует подписки между экземплярами и не даёт полноценные ACK/receipts. Внешний STOMP broker relay передаёт сообщения отдельному брокеру и помогает доставлять их клиентам разных экземпляров.

Персистентность, TTL, DLQ и подтверждения зависят от продукта, назначения и настройки брокера: сам вызов `enableStompBrokerRelay` их не включает. Redis Pub/Sub может решать задачу fan-out иначе, но это не STOMP broker relay и не гарантия сохранности.

---

<a id="q19"></a>

## Q19. Как настроить RabbitMQ как внешний STOMP-брокер в Spring?

Для RabbitMQ STOMP relay нужен включённый плагин `rabbitmq_stomp` и доступный STOMP listener. В Spring настраивают `enableStompBrokerRelay(...)`, host, порт и учётные данные; порт 61613 распространён, но не обязателен.

```java
registry.enableStompBrokerRelay("/topic", "/queue")
        .setRelayHost("rabbitmq.internal")
        .setRelayPort(61613)
        .setClientLogin(clientLogin)
        .setClientPasscode(clientPasscode);
```

Relay общается с RabbitMQ по STOMP, а не через `spring-amqp`. Для поддержки TCP соединений Spring указывает зависимости Reactor Netty/Netty; состав зависимостей конкретной сборки проверяют перед добавлением своих. Учётные данные и heartbeat задают по конфигурации окружения.

---

<a id="q20"></a>

## Q20. Почему масштабирование WebSocket сложнее, чем REST? Как решается проблема sticky sessions?

Открытое WebSocket-соединение уже привязано к одному экземпляру: балансировщик выбирает узел при подключении и не перенаправляет каждый фрейм. События, возникшие на других узлах, доставляют через общий брокер или другую шину; пропущенные события после обрыва требуют буфера, replay или нового снимка данных.

Sticky sessions нужны, если состояние сохраняется только на узле между подключениями, или при SockJS HTTP fallback, где несколько запросов должны попасть к одному обработчику. Для нативного WebSocket с общим брокером sticky обычно не требуется. При падении узла соединение оборвётся в любом случае; клиент должен переподключиться.

---

<a id="q21"></a>

## Q21. Как работает pub/sub через внешний брокер при горизонтальном масштабировании?

Spring STOMP broker relay устанавливает TCP-соединение к брокеру от имени каждой клиентской WebSocket-сессии и отдельное системное соединение для сообщений приложения. Сообщения передаются брокеру, а он доставляет их по подпискам через нужные экземпляры Spring. Это не безусловная рассылка «каждому инстансу».

Реальные гарантии доставки и восстановления после отключения зависят от destination и конфигурации брокера. При простом pub/sub без сохранения сообщение, отправленное во время обрыва, может быть пропущено; клиенту нужен способ восстановить состояние.

---

<a id="q22"></a>

## Q22. Как защитить WebSocket-соединение: Origin, аутентификация, Spring Security?

У WebSocket два участка защиты. На HTTP handshake работают аутентификация Spring Security и проверка `Origin`; при уже аутентифицированном запросе Spring связывает `Principal` с WebSocket/STOMP-сессией. Браузерный Same-Origin Policy сам по себе WebSocket не защищает.

Для STOMP авторизуют `CONNECT`, `SEND` и `SUBSCRIBE` по типу сообщения и destination. В Spring Security 6 используют `@EnableWebSocketSecurity` и `AuthorizationManager<Message<?>>`. Если приложение передаёт JWT в STOMP CONNECT, его нужно явно проверять в `ChannelInterceptor` и устанавливать пользователя: по умолчанию Spring не аутентифицирует STOMP-заголовки. Настройка Origin не заменяет проверку токена и прав.

---

<a id="q23"></a>

## Q23. Как защищать WebSocket от cross-site запросов и можно ли использовать CSRF-токен в STOMP?

`Sec-WebSocket-Key` не является CSRF-токеном: значение открыто и подтверждает поддержку протокола. Браузер может приложить cookie сессии к handshake с чужого сайта, а WebSocket не наследует обычную защиту Same-Origin Policy. Поэтому сервер ограничивает допустимые Origin.

Настоящий CSRF-токен использовать можно. В Spring Security для STOMP по умолчанию требуется токен в заголовке `CONNECT`; его передаёт STOMP-клиент после handshake. Это особенно важно при cookie-аутентификации. Альтернативная схема с проверенным Bearer-токеном в CONNECT требует собственного `ChannelInterceptor` и отдельной авторизации сообщений. Безопасность не сводится к одному Origin или к отключению CSRF для `/ws/**`.

---

<a id="q24"></a>

## Q24. Как обрабатывать ошибки STOMP на сервере через `@MessageExceptionHandler`?

`@MessageExceptionHandler` перехватывает ошибки обработчиков STOMP-сообщений. Метод можно определить в контроллере или `@ControllerAdvice`. HTTP-статус здесь уже не возвращают: ответ отправляют сообщением, например в user destination.

```java
@MessageExceptionHandler(IllegalArgumentException.class)
@SendToUser(destinations = "/queue/errors", broadcast = false)
public ErrorResponse handleInvalidMessage(IllegalArgumentException ex) {
    return new ErrorResponse(ex.getMessage());
}
```

`broadcast=false` отправляет ошибку исходной сессии, а не всем сессиям одного пользователя. Без обработчика нельзя обещать, что любое исключение автоматически превратится в STOMP `ERROR` и закроет соединение.

---

<a id="q25"></a>

## Q25. Как реализовать автоматическое переподключение на клиенте (JS)?

Браузерный WebSocket не переподключается сам. После неожиданного закрытия клиент планирует одну повторную попытку с ограниченным exponential backoff и jitter. После успешного STOMP CONNECT восстанавливает подписки; пропущенные данные получает через replay или снимок, если это поддерживает сервер.

Обработчики `onerror` и `onclose` могут сработать для одного сбоя, поэтому нельзя планировать два таймера. При намеренном закрытии приложения reconnect не запускают. Готовый STOMP-клиент с поддержкой reconnect и heartbeat часто проще собственного цикла; его конкретные настройки сверяют с версией библиотеки.

---

<a id="q26"></a>

## Q26. Как работает heartbeat в STOMP и как его настроить в Spring?

STOMP согласует heartbeat в `CONNECT` и `CONNECTED`: первая величина пары означает возможность отправлять, вторая — желание получать. Для направления клиент→сервер ненулевой интервал равен `max(client_send, server_receive)`; если хотя бы одна величина равна нулю, heartbeat в этом направлении нет. Обратное направление рассчитывают отдельно.

Для Spring simple broker heartbeat нужен `TaskScheduler`; наличие готового scheduler bean зависит от используемой конфигурации. У STOMP broker relay системное соединение и heartbeat настраиваются отдельно. STOMP heartbeat — это данные на уровне протокола STOMP, а не автоматический WebSocket Ping-фрейм. `enableSimpleBroker` и `enableStompBrokerRelay` не включают вместе для одних и тех же destination.

---

<a id="q27"></a>

## Q27. Как работает WebSocket в Spring WebFlux (реактивный стек)?

В Spring WebFlux обработчик реализует `org.springframework.web.reactive.socket.WebSocketHandler`: `handle(WebSocketSession)` возвращает `Mono<Void>`, отражающий завершение работы сессии. `session.receive()` даёт `Flux<WebSocketMessage>`, а `session.send(Publisher<WebSocketMessage>)` отправляет исходящий поток.

```java
@Override
public Mono<Void> handle(WebSocketSession session) {
    Flux<WebSocketMessage> replies = session.receive()
            .map(message -> session.textMessage(message.getPayloadAsText()));
    return session.send(replies);
}
```

На Reactor Netty I/O обычно обрабатывается event loop, но WebFlux поддерживает и другие серверы. Блокирующую работу выносят с I/O-потоков. Backpressure внутри Reactor не заставляет удалённого WebSocket-отправителя замедлиться: буферы и политика перегрузки остаются задачей приложения.

---

<a id="q28"></a>

## Q28. Что такое `WebSocketHandler` в WebFlux и как он отличается от servlet-версии?

В servlet-стеке `org.springframework.web.socket.WebSocketHandler` использует callbacks вроде `afterConnectionEstablished` и `handleMessage`. В WebFlux одноимённый интерфейс из `org.springframework.web.reactive.socket` возвращает `Mono<Void>` из `handle(session)` и работает с `Flux` входящих сообщений.

Servlet-контейнер не обязан удерживать отдельный поток на всё время открытого WebSocket-соединения. Реактивный API даёт backpressure между компонентами приложения, но WebSocket-протокол не передаёт demand удалённому отправителю. Конфигурация STOMP broker messaging `@EnableWebSocketMessageBroker` относится к servlet-стеку; WebFlux WebSocket обрабатывают отдельно.

---

<a id="q29"></a>

## Q29. Какие ограничения у WebSocket по количеству соединений и как их обойти?

Каждое открытое соединение занимает file descriptor, память под состояние и буферы, место в лимитах балансировщика. Обработка сообщений тратит CPU и потоки в зависимости от сервера и прикладного кода. Универсальных значений «100 КБ на соединение», «65 536 fd по умолчанию» и обязательного `ulimit -n 1000000` нет.

Ёмкость измеряют нагрузочным тестом на своей конфигурации: число соединений, heap/off-heap, очереди отправки, скорость сообщений, CPU, ошибки и idle timeout. Затем настраивают лимиты ОС/прокси, размер буферов, поведение медленных клиентов и масштабирование. WebFlux может помочь неблокирующей обработке, но не отменяет ограничения памяти и сети.

---

<a id="q30"></a>

## Q30. Как настроить размер буферов и таймауты для WebSocket в Spring?

В конфигурации Spring STOMP `configureWebSocketTransport` позволяет задать `setMessageSizeLimit`, `setSendBufferSizeLimit`, `setSendTimeLimit` и `setTimeToFirstMessage`. Первый ограничивает собранное входящее STOMP-сообщение, которое может состоять из нескольких WebSocket-фреймов. Остальные ограничивают исходящую буферизацию/время для медленного клиента и ожидание первого сообщения после handshake.

Эти настройки не являются размером TCP-буфера или универсальными лимитами raw WebSocketHandler. Значения подбирают под размер сообщений и нагрузку; дефолты проверяют по версии Spring и конфигурации приложения.

---

<a id="q31"></a>

## Q31. WebSocket в микросервисной архитектуре: service discovery, gateway, routing

Для Spring Cloud Gateway маршрут WebSocket используют со схемой `ws://` либо `lb:ws://` при выборе сервиса через discovery. Gateway выбирает экземпляр при установке соединения; каждый фрейм заново через discovery не маршрутизируется.

Для nginx при WebSocket через HTTP/1.1 нужны `proxy_http_version 1.1` и передача `Upgrade`/`Connection`; idle timeout подбирают под heartbeat и требования сети. Межузловую доставку событий решают брокером или шиной. Sticky требуется при локальном состоянии между подключениями или SockJS HTTP fallback, а не для каждого нативного WebSocket по определению.

---

<a id="q32"></a>

## Q32. WebSocket vs SSE vs Long Polling: детальное сравнение

WebSocket подходит для частого двустороннего обмена. SSE передаёт события сервер→клиент по HTTP, работает и на HTTP/1.1, и на HTTP/2; браузерный EventSource автоматически пытается переподключиться. Long polling повторяет HTTP-запрос после каждого полученного ответа.

`Last-Event-ID` помогает возобновить SSE только если сервер выдаёт id событий и умеет читать историю с этого места. У WebSocket и long polling восстановление также проектируют отдельно. Прокси может буферизовать SSE или закрывать idle-соединения, поэтому выбор проверяют на конкретной инфраструктуре.

---

<a id="q33"></a>

## Q33. Мониторинг WebSocket: метрики, количество соединений, health

Для WebSocket измеряют активные соединения, входящие и исходящие сообщения, ошибки, продолжительность сессий, переполнение буфера отправки и работу брокера. Spring STOMP уже предоставляет `WebSocketMessageBrokerStats`; собственные метрики можно добавить через декоратор обработчика и Micrometer.

Имена вроде `ws.connections.active` — пример пользовательских метрик, а не встроенный стандарт. Health не должен становиться DOWN после каждого обычного отключения клиента. Если health отражает перегрузку, пороги связывают с измеренной ёмкостью, а readiness и liveness не смешивают.

---

<a id="q34"></a>

## Q34. WebSocket в Kubernetes: sticky sessions, session affinity, проблемы

Открытое WebSocket-соединение в Kubernetes остаётся на выбранном pod до разрыва. При нативном WebSocket с общим брокером sticky обычно не требуется: после переподключения клиента может принять другой pod. Для SockJS HTTP fallback или состояния, привязанного к экземпляру между запросами, affinity может понадобиться.

`sessionAffinity: ClientIP` — один из вариантов, но NAT может сосредоточить много клиентов на одном pod. Cookie affinity через ingress применяют, если она нужна. При rolling update pod прекращает новые подключения, даёт существующим время на закрытие и клиент переподключается; `preStop` сам по себе не переносит открытую сессию.

---

<a id="q35"></a>

## Q35. Бинарные и текстовые фреймы: MessagePack, Protobuf

Text-фреймы WebSocket несут UTF-8, binary-фреймы — произвольные байты. JSON обычно передают текстом; MessagePack, Protobuf и CBOR используют с binary-фреймами по согласованному контракту. Формат выбирают по размеру сообщений, частоте, требованиям к схеме и удобству отладки.

Бинарное кодирование не гарантирует конкретный процент экономии или большую скорость для любых данных. Сравнение проводят на реальных payload с учётом сжатия и стоимости сериализации. В Spring явно отправляют `TextMessage` или `BinaryMessage`. При чтении `ByteBuffer` не полагаются на `.array()`: direct и read-only буферы его не поддерживают; читают оставшиеся байты через методы буфера.

---

<a id="q36"></a>

## Q36. WebSocket и CDN: почему CDN не кэширует WS, CloudFlare

Обычный HTTP-кэш CDN не кэширует WebSocket-сообщения. Cloudflare может проксировать WebSocket: начальный HTTP-запрос проходит обработку edge, после установления соединения данные передаются между клиентом и origin. Это не «прозрачный TCP-кэш» и не гарантия конкретного маршрута или задержки.

Таймауты и требования к heartbeat проверяют для выбранного CDN и конфигурации. Универсальное правило «Cloudflare закрывает все idle WebSocket ровно через 100 секунд» документально не подтверждено. Исходный IP берут из доверенных заголовков прокси, проверяя цепочку доверия; присланному клиентом `X-Forwarded-For` слепо не доверяют.

---

<a id="q37"></a>

## Q37. Disconnect стратегии: exponential backoff reconnect на клиенте

При неожиданном разрыве клиент планирует один reconnect с ограниченным exponential backoff и jitter. Намеренное закрытие отмечают собственным флагом: `wasClean` не всегда отражает намерение приложения. `onerror` и `onclose` могут сработать для одного сбоя, поэтому планировщик не должен создавать два таймера.

После нового подключения восстанавливают подписки и состояние данных. WebSocket сам не предоставляет `Last-Event-ID`; для replay приложение задаёт собственный курсор или запрашивает свежий снимок. Политика предельного числа попыток зависит от продукта: фоновый reconnect возможен, но пользователь должен видеть состояние связи.

---

<a id="q38"></a>

## Q38. Тестирование WebSocket в Spring: от unit до интеграции

Unit-тест с mock `WebSocketSession` проверяет логику raw handler. Интеграционный тест запускает реальный сервер на случайном порту и проходит handshake, подписку, отправку и получение сообщения через `StandardWebSocketClient` либо `WebSocketStompClient` для STOMP.

В асинхронном тесте ждут событие с ограниченным таймаутом. Для STOMP полезно дождаться подтверждения подписки (receipt) перед SEND, иначе сообщение может обогнать SUBSCRIBE и тест станет нестабильным. Один unit-тест не проверяет настройки транспорта, безопасности и брокера; `MockMvc` сам по себе не заменяет полноценное соединение.

## See also

- [HTTP & REST](http-rest-interview.md)
- [gRPC](grpc-interview.md)
- [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md)
- [Apache Kafka](../messaging/kafka-interview.md)
- [Spring Boot](../frameworks/spring/spring-boot-interview.md)
- [System Design](../system-design/system-design-interview.md)
- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [GraphQL](graphql-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
