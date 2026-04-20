---
title: "Spring WebSocket"
description: "Полное руководство по Spring WebSocket: протокол, STOMP, SockJS, конфигурация, authentication, broker relay (RabbitMQ), scaling, user destinations, тестирование."
tags:
  - frameworks
  - java-frameworks
  - spring-websocket
  - stomp
  - sockjs
difficulty: "intermediate"
prerequisites: ["spring-mvc.md"]
next: ["spring-messaging.md", "spring-security.md"]
updated: "2026-04-20"
related:
  - "spring-mvc.md"
  - "spring-messaging.md"
  - "spring-security.md"
---
# Spring WebSocket

`Spring WebSocket` даёт двунаправленную persistent-связь между браузером и сервером: чаты, live-уведомления, collaborative-editing, dashboard real-time обновления. Поверх чистого WebSocket обычно используют `STOMP` как messaging-протокол и `SockJS` как транспортный fallback.

## Полезные ссылки

### Официальная документация
- [Spring WebSocket Reference](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Over WebSocket (протокол)](https://stomp.github.io/stomp-specification-1.2.html)
- [RFC 6455 — The WebSocket Protocol](https://datatracker.ietf.org/doc/html/rfc6455)

### Учебные ресурсы
- [Intro to Spring WebSockets — Baeldung](https://www.baeldung.com/websockets-spring)
- [Spring WebFlux WebSocket Client — Baeldung](https://www.baeldung.com/spring-5-reactive-websockets)

### См. также
- [spring-mvc](spring-mvc.md) — HTTP-часть приложения, с которой живёт WebSocket
- [spring-messaging](spring-messaging.md) — общая модель `Message` / `MessageChannel`
- [spring-security](spring-security.md) — security поверх WebSocket
- [spring-webflux](spring-webflux.md) — реактивный WebSocket API
- [spring-integration](spring-integration.md) — если нужен EIP-конвейер вокруг сообщений

## Содержание

- [Когда нужен WebSocket](#когда-нужен-websocket)
- [WebSocket vs STOMP vs SockJS](#websocket-vs-stomp-vs-sockjs)
- [Базовая конфигурация (STOMP)](#базовая-конфигурация-stomp)
- [Контроллеры: @MessageMapping](#контроллеры-messagemapping)
- [Отправка сервером (SimpMessagingTemplate)](#отправка-сервером-simpmessagingtemplate)
- [User destinations](#user-destinations)
- [Security](#security)
- [Внешний broker (RabbitMQ/Kafka)](#внешний-broker-rabbitmqkafka)
- [Низкоуровневый WebSocketHandler](#низкоуровневый-websockethandler)
- [WebSocket в WebFlux](#websocket-в-webflux)
- [Масштабирование и sticky sessions](#масштабирование-и-sticky-sessions)
- [Тестирование](#тестирование)
- [Мониторинг и метрики](#мониторинг-и-метрики)
- [Частые ошибки](#частые-ошибки)

## Когда нужен WebSocket

WebSocket оправдан, когда сервер должен **инициативно** слать данные клиенту с задержкой <1 сек:

| Кейс | WebSocket | Альтернатива |
|---|---|---|
| Чат | | — |
| Live-уведомления (десятки/сек) | | SSE |
| Dashboard с обновлениями раз в 30 сек | | polling / SSE |
| Collaborative editing (CRDT/OT) | | — |
| Event log для админов | | SSE |
| Стриминг видео | | WebRTC / HLS |

Если нужно только serverclient — часто проще `Server-Sent Events (SSE)`: обычный HTTP, работает через любые прокси, не нужен sticky session.

## WebSocket vs STOMP vs SockJS

Три уровня:

```mermaid
graph TB
    App[Application<br/>@MessageMapping] --> STOMP
    STOMP[STOMP frames<br/>SUBSCRIBE / SEND / MESSAGE] --> WS
    WS[WebSocket frames<br/>RFC 6455] --> TCP
    STOMP -.SockJS fallback.-> HTTP[HTTP long-polling / streaming]
    HTTP --> TCP[TCP/TLS]
```

- **WebSocket** — транспорт: одно persistent TCP-соединение, фреймы text/binary.
- **STOMP** — messaging-протокол поверх WebSocket (подписки, destinations, headers). Spring предоставляет готовые аннотации для STOMP.
- **SockJS** — клиентская библиотека, которая при недоступности WebSocket (старые прокси, некоторые корп-сети) откатывается к long-polling/streaming через HTTP.

## Базовая конфигурация (STOMP)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");   // in-memory broker
        config.setApplicationDestinationPrefixes("/app"); // входящие @MessageMapping
        config.setUserDestinationPrefix("/user");         // для адресных сообщений
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://app.example.com")
                .withSockJS();
    }
}
```

Destination prefixes:

| Префикс | Что это |
|---|---|
| `/app/` | входящее сообщение от клиента — попадает в `@MessageMapping` |
| `/topic/` | broadcast, подписчики получают всё |
| `/queue/` | point-to-point, обычно с user-destinations |
| `/user/` | персональное сообщение конкретному пользователю |

Клиент на JavaScript (stomp.js over SockJS):

```javascript
const stomp = Stomp.over(new SockJS('/ws'));
stomp.connect({}, () => {
    stomp.subscribe('/topic/notifications', msg => console.log(JSON.parse(msg.body)));
    stomp.send('/app/notify', {}, JSON.stringify({ text: 'hi' }));
});
```

## Контроллеры: @MessageMapping

```java
@Controller
public class NotificationController {

    @MessageMapping("/notify")           // клиент шлёт на /app/notify
    @SendTo("/topic/notifications")      // рассылается всем подписчикам
    public Notification handle(ChatMessage msg, Principal principal) {
        return new Notification(principal.getName(), msg.text(), Instant.now());
    }

    @SubscribeMapping("/initial")        // однократный reply при SUBSCRIBE
    public List<Notification> initial() {
        return repository.latest(50);
    }
}
```

Параметры метода:

| Тип | Что инжектится |
|---|---|
| DTO | payload (десериализация через [MessageConverter](spring-messaging.md)) |
| `Principal` | аутентифицированный пользователь |
| `@Header("x")` | конкретный STOMP-header |
| `@Headers` | все headers как `Map` |
| `SimpMessageHeaderAccessor` | полный accessor (session id, destination) |

Возвращаемое значение:

- `@SendTo("/topic/X")` — указать destination (default: `/topic/<method>-user<userId>`).
- `@SendToUser("/queue/X")` — персонально отправителю.
- `void` — ничего не рассылать.

## Отправка сервером (SimpMessagingTemplate)

Когда отправка не в ответ на входящее сообщение (например, из кафка-консьюмера, крон-задачи):

```java
@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private final SimpMessagingTemplate template;

    public void broadcast(Notification n) {
        template.convertAndSend("/topic/notifications", n);
    }

    public void sendTo(String username, Notification n) {
        template.convertAndSendToUser(username, "/queue/private", n);
    }
}
```

## User destinations

Персональная доставка адресных сообщений. Клиент подписывается на `/user/queue/private`, Spring автоматически резолвит userId session и доставит сообщение только этому юзеру (даже если у него несколько вкладок).

```java
template.convertAndSendToUser("alice", "/queue/private", payload);
```

Внутри Spring превращает `/user/alice/queue/private` в конкретную сессию. Для работы нужен `Principal` в WebSocket-сессии — обычно приходит из [spring-security](spring-security.md).

## Security

Базовый подход — аутентификация при HTTP-handshake: WebSocket-соединение наследует `SecurityContext` от HTTP-сессии/токена.

```java
@Bean
SecurityFilterChain ws(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(a -> a.requestMatchers("/ws/**").authenticated()
                                      .anyRequest().permitAll())
        .oauth2ResourceServer(o -> o.jwt())
        .build();
}
```

Авторизация STOMP-команд через `AbstractSecurityWebSocketMessageBrokerConfigurer`:

```java
@Configuration
public class WebSocketSecurity
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpSubscribeDestMatchers("/topic/admin/**").hasRole("ADMIN")
            .simpDestMatchers("/app/**").authenticated()
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() { return false; }   // оставить CSRF-защиту
}
```

**Подводный камень:** для `SockJS` отключение `sameOriginDisabled` сломает xhr-streaming — используй proper `setAllowedOrigins(...)` вместо.

## Внешний broker (RabbitMQ/Kafka)

`enableSimpleBroker` держит очереди в памяти — не масштабируется за пределы одного инстанса. Для production с несколькими репликами используют **broker relay**: Spring превращается в клиента внешнего STOMP-broker, который и рассылает сообщения между инстансами.

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableStompBrokerRelay("/topic", "/queue")
          .setRelayHost("rabbitmq.internal")
          .setRelayPort(61613)
          .setClientLogin("guest")
          .setClientPasscode("guest")
          .setSystemHeartbeatSendInterval(10_000)
          .setSystemHeartbeatReceiveInterval(10_000);
    config.setApplicationDestinationPrefixes("/app");
}
```

Поддерживаются RabbitMQ (с `rabbitmq_stomp` плагином) и ActiveMQ. Kafka напрямую не поддерживается — потребует custom bridge через [spring-kafka](spring-kafka.md) `SimpMessagingTemplate`.

## Низкоуровневый WebSocketHandler

Если STOMP/подписки не нужны, можно работать с raw frames:

```java
@Configuration
@EnableWebSocket
public class RawConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry reg) {
        reg.addHandler(new EchoHandler(), "/raw").setAllowedOrigins("*");
    }
}

public class EchoHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msg) throws IOException {
        session.sendMessage(new TextMessage("echo: " + msg.getPayload()));
    }
}
```

Полезно для бинарных протоколов, собственного sub-protocol или прокси.

## WebSocket в WebFlux

Реактивный API, без STOMP. Отдельный `WebSocketHandler`:

```java
public class ReactiveEcho implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(t -> session.textMessage("echo: " + t)));
    }
}

@Bean
HandlerMapping wsMapping(ReactiveEcho h) {
    return new SimpleUrlHandlerMapping(Map.of("/reactive-ws", h), -1);
}

@Bean
WebSocketHandlerAdapter wsAdapter() { return new WebSocketHandlerAdapter(); }
```

Для клиентской части — `ReactorNettyWebSocketClient`.

## Масштабирование и sticky sessions

Одно WebSocket-соединение «прибито» к одному инстансу. При горизонтальном scaling важно:

- **Sticky sessions** на load balancer (`ip_hash` в Nginx, cookie в k8s Ingress) — чтобы reconnect клиента попал на тот же узел, пока сессия не закроется.
- **Broker relay** или событийная шина между узлами — чтобы `convertAndSend` на узле A дошло до подписчика на узле B.
- Подсчёт сессий и bulk-рассылка — смотреть нагрузку на один узел (`Sessions`-метрика).

Для SockJS с long-polling sticky-sessions **обязательны**: каждый long-poll запрос может прийти на другой узел, что приведёт к 404.

## Тестирование

**Unit-тест контроллера:**

```java
@Test
void notifyBroadcasts() {
    NotificationController c = new NotificationController(repo);
    Notification n = c.handle(new ChatMessage("hi"), () -> "alice");
    assertEquals("alice", n.from());
}
```

**Integration-тест через STOMP-клиент:**

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WsIntegrationTest {

    @LocalServerPort int port;

    @Test
    void chatFlow() throws Exception {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());

        CompletableFuture<Notification> received = new CompletableFuture<>();
        StompSession session = client.connectAsync("ws://localhost:" + port + "/ws",
                new StompSessionHandlerAdapter() {}).get(5, SECONDS);

        session.subscribe("/topic/notifications", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders h) { return Notification.class; }
            public void handleFrame(StompHeaders h, Object p) { received.complete((Notification) p); }
        });

        session.send("/app/notify", new ChatMessage("hi"));

        assertThat(received.get(3, SECONDS).text()).isEqualTo("hi");
    }
}
```

## Мониторинг и метрики

Через [spring-actuator](spring-actuator.md) и Micrometer:

- `simp.events.sessions.connected` — активные сессии.
- `simp.events.sessions.total` — total.
- Time на WebSocket-handshake.
- Размер входящих/исходящих сообщений.

Ключевые «сигналы» в production:

- Резкий рост сессий утечка (клиенты не закрывают).
- Queue latency растёт broker перегружен.
- Ошибки handshake CORS/Origin/Auth.

## Частые ошибки

| Симптом | Причина | Фикс |
|---|---|---|
| `403` при подключении | Origin не в `setAllowedOrigins` | явно перечислить домены |
| SockJS падает при scaling | нет sticky sessions | настроить `ip_hash` / session affinity |
| Сообщение не доходит до конкретного пользователя | нет `Principal` в сессии | пропустить Spring Security до WS-handshake |
| `/user/` подписки не работают | разные userId на разных вкладках (anonymous) | использовать стабильный login / JWT `sub` |
| Broker relay отваливается | heartbeat не настроены | `setSystemHeartbeatSendInterval` |
| «Sent bytes too high» | клиент шлёт большие payload | `setMessageSizeLimit` + валидация |

**Итог:** для единственного инстанса — `enableSimpleBroker` + STOMP + SockJS. Для нескольких инстансов — обязательно broker relay (RabbitMQ) + sticky sessions на LB + security на handshake.
