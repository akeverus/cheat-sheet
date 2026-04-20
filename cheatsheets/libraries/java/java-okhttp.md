---
title: "OkHttp"
description: "OkHttp - это HTTP клиент для Java и Kotlin, разработанный Square. Это один из самых популярных HTTP клиентов для Android и серверных Java приложений."
tags:
  - libraries
  - java
  - java-okhttp
difficulty: "intermediate"
prerequisites:
  - java-lombok
next: []
updated: "2026-04-20"
---
# OkHttp

**OkHttp** — это **HTTP** клиент для **Java** и **Kotlin**, разработанный **Square**. Это один из самых популярных **HTTP** клиентов для **Android** и серверных **Java** приложений.

## Полезные ссылки

### Официальная документация
- [OkHttp](https://square.github.io/okhttp/) — официальный сайт
- [OkHttp GitHub](https://github.com/square/okhttp) — репозиторий проекта
- [OkHttp Documentation](https://square.github.io/okhttp/features/) — документация

### См. также
- [Apache HttpClient](java-apache-httpclient.md) — **Apache HttpClient**
- [Retrofit](java-retrofit.md) — **Retrofit** (использует OkHttp)

## Содержание

- [Основные возможности](#основные-возможности)
  - [Простые HTTP запросы](#простые-http-запросы)
  - [Различные HTTP методы](#различные-http-методы)
  - [Headers и Authentication](#headers-и-authentication)
  - [Query Parameters](#query-parameters)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Connection Pooling](#connection-pooling)
  - [Interceptors](#interceptors)
  - [Caching](#caching)
  - [Timeouts](#timeouts)
  - [Retry и Redirects](#retry-и-redirects)
  - [SSL/TLS Configuration](#ssltls-configuration)
  - [WebSocket Support](#websocket-support)
  - [Multipart Upload](#multipart-upload)
- [Асинхронные операции](#асинхронные-операции)
  - [Callbacks](#callbacks)
  - [CompletableFuture Integration](#completablefuture-integration)
  - [RxJava Integration](#rxjava-integration)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [RestTemplate Integration](#resttemplate-integration)
  - [Feign Client Integration](#feign-client-integration)
- [Testing](#testing)
  - [MockWebServer](#mockwebserver)
  - [WireMock Integration](#wiremock-integration)
- [Performance Tuning](#performance-tuning)
  - [Connection Reuse](#connection-reuse)
  - [GZIP Compression](#gzip-compression)
  - [DNS Resolution](#dns-resolution)
- [Advanced Features](#advanced-features)
  - [EventSource (Server-Sent Events)](#eventsource-server-sent-events)
  - [HTTP/2 Server Push](#http2-server-push)
  - [Custom Protocols](#custom-protocols)
- [Migration Guide](#migration-guide)
  - [From Apache HttpClient](#from-apache-httpclient)
  - [From Java URLConnection](#from-java-urlconnection)
- [Решение проблем](#решение-проблем)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также-1)

## Основные возможности

### Простые HTTP запросы

Создание **OkHttp**-клиента и выполнение **GET**-запроса; ответ читается из **body**().

```java
// Создание клиента
OkHttpClient client = new OkHttpClient();

// Создание запроса
Request request = new Request.Builder()
    .url("https://api.example.com/users")
    .build();

// Выполнение запроса
try (Response response = client.newCall(request).execute()) {
    if (response.isSuccessful()) {
        String responseBody = response.body().string();
        System.out.println(responseBody);
    }
}
```

### Различные HTTP методы
```java
// GET запрос
Request getRequest = new Request.Builder()
    .url("https://api.example.com/users/1")
    .get()
    .build();

// POST запрос с JSON
String json = "{\"name\":\"John\",\"age\":30}";
RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

Request postRequest = new Request.Builder()
    .url("https://api.example.com/users")
    .post(body)
    .build();

// PUT запрос
Request putRequest = new Request.Builder()
    .url("https://api.example.com/users/1")
    .put(body)
    .build();

// DELETE запрос
Request deleteRequest = new Request.Builder()
    .url("https://api.example.com/users/1")
    .delete()
    .build();
```

### Headers и Authentication
```java
// Добавление headers
Request request = new Request.Builder()
    .url("https://api.example.com/secure")
    .addHeader("Authorization", "Bearer " + token)
    .addHeader("User-Agent", "MyApp/1.0")
    .addHeader("Accept", "application/json")
    .build();

// Basic Authentication
String credentials = Credentials.basic("username", "password");
Request authRequest = new Request.Builder()
    .url("https://api.example.com/protected")
    .addHeader("Authorization", credentials)
    .build();
```

### Query Parameters
```java
// Построение URL с query параметрами
HttpUrl url = new HttpUrl.Builder()
    .scheme("https")
    .host("api.example.com")
    .addPathSegment("search")
    .addQueryParameter("q", "java")
    .addQueryParameter("limit", "10")
    .addQueryParameter("sort", "relevance")
    .build();

Request request = new Request.Builder()
    .url(url)
    .build();
```

## Продвинутые возможности

### Connection Pooling
```java
// Настройка пула соединений
ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);

OkHttpClient client = new OkHttpClient.Builder()
    .connectionPool(connectionPool)
    .build();

// Статистика пула
System.out.println("Connections: " + connectionPool.connectionCount());
System.out.println("Idle connections: " + connectionPool.idleConnectionCount());
```

### Interceptors
```java
// Application Interceptor (для всех запросов)
class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long startTime = System.nanoTime();
        System.out.println("Sending request: " + request.url());

        Response response = chain.proceed(request);

        long endTime = System.nanoTime();
        System.out.println("Received response for " + response.request().url() +
                          " in " + (endTime - startTime) / 1e6 + "ms");

        return response;
    }
}

// Network Interceptor (для конкретных соединений)
class NetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("Network: " + request.url() + " on " + chain.connection());

        return chain.proceed(request);
    }
}

// Регистрация интерцепторов
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new LoggingInterceptor())
    .addNetworkInterceptor(new NetworkInterceptor())
    .build();
```

### Caching
```java
/
 * Настройка кеширования для OkHttp
 * Кеширование позволяет сохранять HTTP ответы на диск для быстрого доступа без повторных запросов
 */
// Настройка размера кеша - определяем максимальный размер кеша на диске
int cacheSize = 10 * 1024 * 1024; // 10 MB (10 мегабайт) - максимальный размер кеша

// Создание кеша - кеш сохраняется в директории "cache" на диске
// Cache автоматически управляет размером кеша, удаляя старые записи при превышении лимита
Cache cache = new Cache(new File("cache"), cacheSize);

// Создание OkHttpClient с включенным кешированием
OkHttpClient client = new OkHttpClient.Builder()
    .cache(cache)  // Устанавливаем кеш для клиента
    // OkHttp автоматически кеширует ответы с соответствующими Cache-Control заголовками
    .build();

// Управление кешем - операции для контроля кеша
cache.evictAll();      // Очистить весь кеш - удаляет все закешированные ответы
cache.size();          // Текущий размер кеша - возвращает количество байт используемых кешем
cache.maxSize();       // Максимальный размер кеша - возвращает максимальный размер (10 MB в нашем случае)
```

// **Cache-Control headers**
```java
Request request = new Request.Builder()
    .url("https://api.example.com/data")
    .addHeader("Cache-Control", "max-stale=3600") // Принять устаревший кеш до 1 часа
    .build();
```

### Timeouts
```java
/
 - Настройка таймаутов для `OkHttp`
 - Таймауты предотвращают зависание приложения при проблемах с сетью или медленными серверами
 */
// Создание `OkHttpClient` с настроенными таймаутами
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`connectTimeout`(10, `TimeUnit`.`SECONDS`)  // **TCP** соединения (10 секунд)
    // Если соединение не установилось за 10 секунд, выбрасывается `SocketTimeoutException`
    .`readTimeout`(30, `TimeUnit`.`SECONDS`)         // Таймаут чтения данных из сокета (30 секунд)
    // Если данные не читаются за 30 секунд, выбрасывается `SocketTimeoutException`
    .`writeTimeout`(30, `TimeUnit`.`SECONDS`)        // Таймаут записи данных в сокет (30 секунд)
    // Если данные не записываются за 30 секунд, выбрасывается `SocketTimeoutException`
    .`callTimeout`(60, `TimeUnit`.`SECONDS`)         // Общий таймаут всего вызова включая соединение, запись, чтение (60 секунд)
    // Если весь запрос не завершился за 60 секунд, выбрасывается IOException
    .build();
```

### Retry и Redirects
```java
// Автоматические редиректы
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`followRedirects`(`true`)  // **HTTP** редиректам
    .`followSslRedirects`(`true`)  // **HTTPS** редиректам
    .`retryOnConnectionFailure`(`true`) // Повторять при сбое соединения
    .build();
```

### SSL/TLS Configuration
```java
// Игнорирование `SSL` (только для тестирования!)
`OkHttpClient unsafeClient` = new `OkHttpClient`.`Builder`()
    .`sslSocketFactory`(`sslSocketFactory`, `trustManager`)
    .`hostnameVerifier`((hostname, session) -> `true`)
    .build();

// Кастомный `TrustManager`
`TrustManagerFactory trustManagerFactory` = `TrustManagerFactory`
    .`getInstance`(`TrustManagerFactory`.`getDefaultAlgorithm`());
`trustManagerFactory`.init(`keyStore`);

SSLContext `sslContext` = SSLContext.`getInstance`("`TLS`");
`sslContext`.init(`null`, `trustManagerFactory`.`getTrustManagers`(), `null`);

`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`sslSocketFactory`(`sslContext`.`getSocketFactory`(), `trustManager`)
    .build();
```

### WebSocket Support
```java
/
 - `WebSocket` клиент через `OkHttp`
 - `WebSocket` позволяет двустороннюю коммуникацию между клиентом и сервером в реальном времени
 */
// Создание `WebSocket` запроса - `WebSocket` использует протокол ws:// или wss://
`Request request` = new `Request`.`Builder`()
    .url("ws://echo.websocket.org")  // **WebSocket** URL (SSL: wss://)
    .build();

// Создание `WebSocketListener` для обработки событий `WebSocket` соединения
`WebSocketListener` listener = new `WebSocketListener`() {
    /
     - Вызывается когда `WebSocket` соединение успешно установлено
     - `@param webSocket` объект `WebSocket` для отправки сообщений
     - `@param` response `HTTP` ответ от сервера (содержит заголовки upgrade)
     */
    `@Override`
    public void `onOpen`(`WebSocket webSocket`, `Response response`) {
        // Соединение установлено - можно отправлять сообщения
        `webSocket`.send("`Hello WebSocket`!");  // Отправляем приветственное сообщение серверу
    }

    /
     - Вызывается при получении текстового сообщения от сервера
     - `@param webSocket` объект `WebSocket` соединения
     - `@param` text текст полученного сообщения
     */
    `@Override`
    public void `onMessage`(`WebSocket webSocket`, `String text`) {
        // Получено текстовое сообщение от сервера
        `System`.`out.println`("`Received`: " + text);  // Выводим полученное сообщение
    }

    /
     - Вызывается когда `WebSocket` соединение закрыто нормально
     - `@param webSocket` объект `WebSocket` соединения
     - `@param` code код закрытия (`1000` = нормальное закрытие)
     - `@param` reason причина закрытия соединения
     */
    `@Override`
    public void `onClosed`(`WebSocket webSocket`, int code, `String reason`) {
        // Соединение закрыто - выводим информацию о закрытии
        `System`.`out.println`("`WebSocket` closed: " + code + " " + reason);
    }

    /
     - Вызывается при ошибке `WebSocket` соединения
     - `@param webSocket` объект `WebSocket` соединения
     - `@param` t исключение которое произошло
     - `@param` response `HTTP` ответ (если был) или `null`
     */
    `@Override`
    public void `onFailure`(`WebSocket webSocket`, `Throwable t`, `Response response`) {
        // Произошла ошибка - выводим stack trace для отладки
        t.`printStackTrace`();
    }
};

// Создание `WebSocket` соединения - устанавливаем соединение с сервером
`WebSocket webSocket` = client.`newWebSocket`(request, listener);
// После вызова `newWebSocket`() соединение устанавливается асинхронно
// `onOpen`() будет вызван когда соединение готово

// Отправка сообщений через `WebSocket`
`webSocket`.send("`Hello again`!");  // Отправляем текстовое сообщение серверу

// Закрытие `WebSocket` соединения
`webSocket`.close(`1000`, "Goodbye");  // `1000` — нормальное закрытие
// После вызова close() будет вызван `onClosed`() когда соединение полностью закроется
```

### Multipart Upload
```java
// `Multipart` форма
`RequestBody requestBody` = new `MultipartBody`.`Builder`()
    .`setType`(`MultipartBody`.`FORM`)
    .`addFormDataPart`("file", "`image.jpg`",
        `RequestBody`.create(`imageFile`, `MediaType`.get("image/jpeg")))
    .`addFormDataPart`("description", "A beautiful image")
    .`addFormDataPart`("tags", "nature,landscape")
    .build();

`Request request` = new `Request`.`Builder`()
    .url("https://`api.example.com`/upload")
    .post(`requestBody`)
    .build();
```

## Асинхронные операции

### Callbacks
```java
// Асинхронный вызов с `callback`
client.`newCall`(request).enqueue(new `Callback`() {
    `@Override`
    public void `onFailure`(`Call call`, IOException e) {
        e.`printStackTrace`();
    }

    `@Override`
    public void `onResponse`(`Call call`, `Response response`) throws IOException {
        try (`ResponseBody responseBody` = `response.body`()) {
            if (response.`isSuccessful`()) {
                `String responseData` = `responseBody`.string();
                // Обработка успешного ответа
            } else {
                // Обработка ошибки
            }
        }
    }
});
```

### CompletableFuture Integration
```java
public `CompletableFuture`<`String`> `asyncGet`(`String url`) {
    `CompletableFuture`<`String`> future = new `CompletableFuture`<>();

    `Request request` = new `Request`.`Builder`().url(url).build();

    client.`newCall`(request).enqueue(new `Callback`() {
        `@Override`
        public void `onFailure`(`Call call`, IOException e) {
            future.`completeExceptionally`(e);
        }

        `@Override`
        public void `onResponse`(`Call call`, `Response response`) throws IOException {
            try (`ResponseBody` body = `response.body`()) {
                if (response.`isSuccessful`()) {
                    `future.complete`(`body.string`());
                } else {
                    future.`completeExceptionally`(
                        new IOException("`HTTP` " + `response.code`()));
                }
            }
        }
    });

    return future;
}

// Использование
asyncGet("https://api.example.com/data")
    .`thenAccept`(`System`.out::println)
    .exceptionally(throwable -> {
        `System`.`err.println`("`Error`: " + throwable.`getMessage`());
        return `null`;
    });
```

### RxJava Integration
```java
public `Observable`<`String`> `getDataObservable`(`String url`) {
    return `Observable`.create(emitter -> {
        `Request request` = new `Request`.`Builder`().url(url).build();

        client.`newCall`(request).enqueue(new `Callback`() {
            `@Override`
            public void `onFailure`(`Call call`, IOException e) {
                emitter.`onError`(e);
            }

            `@Override`
            public void `onResponse`(`Call call`, `Response response`) throws IOException {
                try (`ResponseBody` body = `response.body`()) {
                    emitter.`onNext`(`body.string`());
                    emitter.`onComplete`();
                }
            }
        });
    });
}
```

## Spring Boot Integration

### Configuration
```java
`@Configuration`
public class `OkHttpConfig` {

    `@Bean`
    public `OkHttpClient okHttpClient`() {
        return new `OkHttpClient`.`Builder`()
            .`connectTimeout`(10, `TimeUnit`.`SECONDS`)
            .`readTimeout`(30, `TimeUnit`.`SECONDS`)
            .`writeTimeout`(30, `TimeUnit`.`SECONDS`)
            .`addInterceptor`(new `LoggingInterceptor`())
            .build();
    }

    `@Bean`
    public `WebClient webClient`(`OkHttpClient okHttpClient`) {
        return `WebClient`.`builder`()
            .`clientConnector`(new `ReactorClientHttpConnector`(
                `HttpClient`.from(
                    `TcpClient`.create()
                        .option(`ChannelOption`.CONNECT_TIMEOUT_MILLIS, `10000`)
                )
            ))
            .build();
    }
}
```

### RestTemplate Integration
```java
`@Configuration`
public class `RestTemplateConfig` {

    `@Bean`
    public `RestTemplate restTemplate`(`OkHttpClient okHttpClient`) {
        return new `RestTemplate`(new `OkHttp3ClientHttpRequestFactory`(`okHttpClient`));
    }
}
```

### Feign Client Integration
```java
`@Configuration`
public class `FeignConfig` {

    `@Bean`
    public `OkHttpClient okHttpClient`() {
        return new `OkHttpClient`.`Builder`()
            .`connectTimeout`(10, `TimeUnit`.`SECONDS`)
            .`readTimeout`(30, `TimeUnit`.`SECONDS`)
            .build();
    }

    `@Bean`
    public `Client feignClient`(`OkHttpClient okHttpClient`) {
        return new `OkHttpClient`(`okHttpClient`);
    }
}
```

## Testing

### MockWebServer
```java
public class `ApiClientTest` {

    private `MockWebServer` server;
    private `ApiClient apiClient`;

    `@BeforeEach`
    public void `setUp`() throws IOException {
        server = new `MockWebServer`();
        `server.start`();

        `OkHttpClient` client = new `OkHttpClient`.`Builder`()
            .build();

        `apiClient` = new `ApiClient`(client, server.url().toString());
    }

    `@AfterEach`
    public void `tearDown`() throws IOException {
        `server.shutdown`();
    }

    `@Test`
    public void `testGetUser`() throws `Exception` {
        // Подготовка `mock` ответа
        `server.enqueue`(new `MockResponse`()
            .`setBody`("{\"id\":1,\"name\":\"`John Doe`\"}")
            .`addHeader`("`Content-Type`", "application/json"));

        // Выполнение теста
        `User user` = `apiClient`.`getUser`(1);

        `assertEquals`(1, user.`getId`());
        `assertEquals`("`John Doe`", user.`getName`());

        // Проверка запроса
        `RecordedRequest` request = server.`takeRequest`();
        `assertEquals`("`GET`", request.`getMethod`());
        `assertEquals`("/", request.`getPath`());
    }
}
```

### WireMock Integration
```java
// Использование `WireMock` для тестирования `OkHttp` клиента
`@Rule`
public `WireMockRule wireMockRule` = new `WireMockRule`(`8089`);

`@Test`
public void `testWithWireMock`() {
    // Настройка `WireMock`
    `stubFor`(get(`urlEqualTo`("/api/test"))
        .`willReturn`(`aResponse`()
            .`withStatus`(`200`)
            .`withBody`("{\"message\":\"`Hello`\"}")));

    // Создание `OkHttp` клиента
    `OkHttpClient` client = new `OkHttpClient`.`Builder`()
        .build();

    // Выполнение запроса
    `Request request` = new `Request`.`Builder`()
        .url("http://localhost:8089/api/test")
        .build();

    try (`Response response` = client.`newCall`(request).execute()) {
        `assertTrue`(response.`isSuccessful`());
        `assertEquals`("{\"message\":\"`Hello`\"}", `response.body`().string());
    }
}
```

## Performance Tuning

### Connection Reuse
```java
// Оптимизация для частых запросов к одному хосту
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`connectionPool`(new `ConnectionPool`(10, 5, `TimeUnit`.`MINUTES`))
    .build();

// **HTTP/2** multiplexing
// `OkHttp` автоматически использует **HTTP/2**, когда доступно
```

### GZIP Compression
```java
// Автоматическая компрессия
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`addInterceptor`(new `GzipRequestInterceptor`())
    .build();

class `GzipRequestInterceptor` implements `Interceptor` {
    `@Override`
    public `Response intercept`(`Chain chain`) throws IOException {
        `Request original` = `chain.request`();

        // Добавление gzip для больших запросов
        if (`shouldCompress`(original)) {
            `Request compressed` = original.`newBuilder`()
                .`addHeader`("`Content-Encoding`", "gzip")
                .method(`original.method`(), gzip(`original.body`()))
                .build();

            return `chain.proceed`(compressed);
        }

        return `chain.proceed`(original);
    }

    private boolean `shouldCompress`(`Request request`) {
        return `request.body`() != `null` &&
               `request.body`().`contentLength`() > `1024`; // > 1KB
    }
}
```

### DNS Resolution
```java
// Кастомный `DNS` resolver
`Dns customDns` = new Dns() {
    `@Override`
    public `List`<`InetAddress`> lookup(`String hostname`) throws `UnknownHostException` {
        // Кастомная логика разрешения `DNS`
        if ("`api.example.com`".equals(hostname)) {
            return `Arrays`.`asList`(
                `InetAddress`.`getByName`("192.168.1.1"),
                `InetAddress`.`getByName`("192.168.1.2")
            );
        }
        return Dns.`SYSTEM`.lookup(hostname);
    }
};

`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .dns(`customDns`)
    .build();
```

## Advanced Features

### EventSource (Server-Sent Events)
```java
public class `ServerSentEventsClient` {

    public void `connectToSSE`(`String url`) {
        `Request request` = new `Request`.`Builder`()
            .url(url)
            .`addHeader`("Accept", "text/event-stream")
            .build();

        client.`newCall`(request).enqueue(new `Callback`() {
            `@Override`
            public void `onFailure`(`Call call`, IOException e) {
                e.`printStackTrace`();
            }

            `@Override`
            public void `onResponse`(`Call call`, `Response response`) throws IOException {
                if (response.`isSuccessful`()) {
                    `processSSEStream`(`response.body`().source());
                }
            }
        });
    }

    private void `processSSEStream`(`BufferedSource` source) throws IOException {
        while (!`source.exhausted`()) {
            `String line` = source.`readUtf8Line`();
            if (line.`startsWith`("data: ")) {
                `String data` = `line.substring`(6);
                `processSSEData`(data);
            }
        }
    }

    private void `processSSEData`(`String data`) {
        `System`.`out.println`("`Received SSE`: " + data);
    }
}
```

### HTTP/2 Server Push
```java
// `OkHttp` автоматически поддерживает **HTTP/2 Server Push**
// Серверные push-ресурсы доступны через response.`priorResponse`()
```

### Custom Protocols
```java
// Поддержка `QUIC` (экспериментально)
// Требует дополнительных зависимостей и конфигурации
```

## Migration Guide

### From Apache HttpClient
```java
// `Apache HttpClient 4`.x
`CloseableHttpClient httpClient` = `HttpClients`.`createDefault`();
`HttpGet httpGet` = new `HttpGet`("https://api.example.com/data");
`CloseableHttpResponse` response = `httpClient`.execute(`httpGet`);

// `OkHttp` эквивалент
`OkHttpClient` client = new `OkHttpClient`();
`Request request` = new `Request`.`Builder`()
    .url("https://`api.example.com`/data")
    .build();
`Response response` = client.`newCall`(request).execute();
```

### From Java URLConnection
```java
// `Java URLConnection`
`URL` url = new `URL`("https://api.example.com/data");
`HttpURLConnection` connection = (`HttpURLConnection`) url.`openConnection`();
connection.`setRequestMethod`("`GET`");
`InputStream` input = connection.`getInputStream`();

// `OkHttp` эквивалент
`OkHttpClient` client = new `OkHttpClient`();
`Request request` = new `Request`.`Builder`()
    .url("https://`api.example.com`/data")
    .build();
`Response response` = client.`newCall`(request).execute();
`InputStream` input = `response.body`().`byteStream`();
```

## Решение проблем

### Common Issues
```java
// Проблема: `SocketTimeoutException`
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`connectTimeout`(30, `TimeUnit`.`SECONDS`)  // Увеличить таймауты
    .`readTimeout`(60, `TimeUnit`.`SECONDS`)
    .build();

// Проблема: `CertificateException`
// Использовать custom `TrustManager` или отключить проверку для dev

// Проблема: `Connection pool exhausted`
`ConnectionPool` pool = new `ConnectionPool`(20, 10, `TimeUnit`.`MINUTES`);
`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`connectionPool`(pool)
    .build();
```

### Debugging
```java
// Включение логирования
`HttpLoggingInterceptor` logging = new `HttpLoggingInterceptor`();
logging.`setLevel`(`HttpLoggingInterceptor`.`Level`.`BODY`);

`OkHttpClient` client = new `OkHttpClient`.`Builder`()
    .`addInterceptor`(logging)
    .build();

// Или через систему логирования
`Logger okhttpLogger` = `Logger`.`getLogger`(`OkHttpClient`.class.`getName`());
`okhttpLogger`.`setLevel`(`Level`.`FINE`);
```

## Лучшие практики

- Переиспользование OkHttpClient: создавайте один экземпляр `OkHttpClient` на всё приложение и переиспользуйте его; клиент потокобезопасен и управляет пулом соединений.
- Таймауты: всегда задавайте `connectTimeout`, `readTimeout`, `writeTimeout` под вашу сеть; избегайте бесконечных ожиданий.
- Закрытие ResponseBody: всегда закрывайте `Response.body()` (try-with-resources или явный `close()`), иначе возможна утечка соединений.
- Interceptors: используйте `HttpLoggingInterceptor` только в dev; в prod логируйте без тел запросов/ответов; для retry и аутентификации — отдельные interceptors.
- Connection pool: настраивайте `ConnectionPool` под нагрузку (maxIdleConnections, keepAliveDuration); при высокой нагрузке увеличьте лимиты.

## Полезные ссылки
- [Официальная документация OkHttp](https://square.github.io/okhttp/)
- [GitHub репозиторий](https://github.com/square/okhttp)
- [OkHttp Recipes](https://github.com/square/okhttp/tree/master/samples)
- [Spring Boot Integration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-webclient)

## См. также
- [Apache HttpClient](java-apache-httpclient.md) — альтернативный **HTTP** клиент
- [Retrofit](java-retrofit.md) — type-safe **HTTP** клиент
- [WebClient](../../frameworks/java-frameworks/spring/spring-webflux.md) — реактивный **HTTP** клиент

