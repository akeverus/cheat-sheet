---
title: "Spring WebSocket"
description: "Кратко: обзор поддержки WebSocket в Spring — двунаправленная связь между клиентом и сервером. STOMP, SockJS, конфигурация и типовые сценарии использования."
tags: ["frameworks", "java-frameworks", "spring-websocket"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Spring WebSocket

Кратко: обзор поддержки **WebSocket** в **Spring** — двунаправленная связь между клиентом и сервером. **STOMP**, **SockJS**, конфигурация и типовые сценарии использования.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [Spring WebSocket Reference](https://docs.spring.io/spring-framework/reference/web/websocket.html)

### См. также
- [Spring MVC](spring-mvc.md) — **MVC** в **Spring**
- [Spring Security](spring-security.md) — безопасность **WebSocket**
- [Spring Messaging](spring-messaging.md) — брокер сообщений

## Содержание

- [Введение](#введение)
- [Основные понятия](#основные-понятия)
- [Базовая конфигурация](#базовая-конфигурация)
- [Пример контроллера](#пример-контроллера)
- [Лучшие практики](#лучшие-практики)
- [Заключение](#заключение)

## Введение

**Spring** предоставляет поддержку **WebSocket** для организации полно дуплексной связи по одному **TCP**-соединению. Интегрируется с **STOMP** как подпротоколом и **SockJS** как **fallback** для сред без нативной поддержки **WebSocket**.

## Основные понятия

- **WebSocket** — протокол полнодуплексной связи поверх одного **TCP**-соединения.
- **STOMP** — простой текстовый протокол поверх **WebSocket** (**подписки, назначения, фреймы**).
- **SockJS** — транспорты-заменители (**long polling, streaming**) при недоступности **WebSocket**.
- **@MessageMapping** — обработка сообщений от клиента; **@SendTo** / **SimpMessagingTemplate** — отправка на клиентов.

## Базовая конфигурация

Включение **WebSocket** с **STOMP** и встроенным брокером сообщений:

```java
// Включение WebSocket с брокером сообщений (STOMP)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
```

**Endpoint** `/ws` — точка подключения; клиенты подписываются на **destinations** вида `/topic/notifications` или `/queue/private`. Сообщения, отправляемые с префиксом `/app`, маршрутизируются в методы с **@MessageMapping**.

## Пример контроллера

Обработка входящего сообщения и рассылка подписчикам топика:

```java
// Контроллер отправки уведомлений через WebSocket
@Controller
public class NotificationController {

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public String send(String message) {
        return message;
    }
}
```

Клиент подключается к `/ws` (**SockJS**), подписывается на `/topic/notifications` и отправляет сообщения на `/app/notify`. Для отправки с сервера без входящего сообщения используйте **SimpMessagingTemplate** (`convertAndSend("/topic/notifications", payload)`).

## Лучшие практики

- **Безопасность:** всегда подключайте **WebSocket** к **Spring Security** (**транспорт, аутентификация при handshake**); ограничивайте назначения (**destinations**) по ролям.
- **Масштабирование:** при нескольких инстансах приложения используйте брокер сообщений (**RabbitMQ, Redis**) для рассылки между узлами; настраивайте **endpoint** с префиксом приложения.
- **Надёжность:** обрабатывайте разрывы соединения (**reconnect, heartbeat**); на клиенте используйте **SockJS fallback** для совместимости.
- **Нагрузка:** ограничивайте размер сообщений и частоту отправки; используйте **throttling** и квоты на подписки при необходимости.
- **Мониторинг:** отслеживайте число активных сессий, очереди сообщений и ошибки **handshake** в метриках (**Actuator, кастомные метрики**).


## Заключение

**Spring WebSocket** подходит для чатов, уведомлений в реальном времени, совместного редактирования и интерактивных панелей. Для масштабирования на несколько инстансов настройте внешний брокер (**RabbitMQ** или **Redis**) в **configureMessageBroker** и подключите **Spring Security** к **WebSocket** endpoint.
