---
title: "Apache HttpClient: Мощный HTTP клиент для Java"
description: "Комплексное руководство по использованию Apache HttpClient — мощной и гибкой HTTP клиентской библиотеки для Java, которая предоставляет полную поддержку HTTP протокола и является частью Apache HttpComponents проекта."
tags:
  - libraries
  - java
  - java-apache-httpclient
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Apache HttpClient: Мощный **HTTP** клиент для **Java**

**Комплексное руководство по использованию `Apache HttpClient` — мощной и гибкой `HTTP` клиентской библиотеки для `Java`, которая предоставляет полную поддержку `HTTP` протокола и является частью `Apache HttpComponents` проекта.**

## Полезные ссылки

### Официальная документация
- [Apache HttpClient 5.x](https://hc.apache.org/httpcomponents-client-5.2.x/) — документация **HttpClient** 5.x
- [Apache HttpClient 4.x](https://hc.apache.org/httpcomponents-client-4.5.x/) — документация **HttpClient** 4.x
- [Apache HttpComponents](https://hc.apache.org/) — главный сайт проекта

### Документация и примеры
- [GitHub репозиторий](https://github.com/apache/httpcomponents-client) — исходный код и примеры
- [Migration Guide](https://hc.apache.org/httpcomponents-client-5.2.x/migration-guide/index.html) — руководство по миграции
- [HttpClient Tutorial](https://hc.apache.org/httpcomponents-client-5.2.x/quickstart.html) — подробное руководство

### **Maven**/**Gradle**
- [Maven Central](https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5) — **HttpClient** 5.x
- [Maven Central 4.x](https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient) — **HttpClient** 4.x

## Содержание

- [Введение в Apache HttpClient](#введение-в-apache-httpclient)
  - [Почему Apache HttpClient?](#почему-apache-httpclient)
  - [Архитектура HttpClient](#архитектура-httpclient)
  - [Версии HttpClient](#версии-httpclient)
  - [Основные компоненты](#основные-компоненты)
- [Основные возможности](#основные-возможности)
  - [Простые HTTP запросы](#простые-http-запросы)
  - [Различные HTTP методы](#различные-http-методы)
  - [Headers и Authentication](#headers-и-authentication)
  - [Query Parameters](#query-parameters)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Connection Management](#connection-management)
  - [Connection Reuse](#connection-reuse)
  - [SSL/TLS Configuration](#ssltls-configuration)
  - [Proxy Configuration](#proxy-configuration)
  - [Cookie Management](#cookie-management)
  - [Timeout Configuration](#timeout-configuration)
  - [Redirect Handling](#redirect-handling)
- [Асинхронные операции](#асинхронные-операции)
  - [Future-based Async](#future-based-async)
  - [Callback-based Async](#callback-based-async)
  - [Reactive Streams](#reactive-streams)
- [Multipart Upload](#multipart-upload)
  - [File Upload](#file-upload)
  - [Streaming Upload](#streaming-upload)
- [Caching](#caching)
  - [HttpClient Cache](#httpclient-cache)
- [Interceptors](#interceptors)
  - [Request Interceptor](#request-interceptor)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [RestTemplate Integration](#resttemplate-integration)
- [Testing](#testing)
  - [Mock Server Setup](#mock-server-setup)
- [Performance Tuning](#performance-tuning)
  - [Connection Keep-Alive](#connection-keep-alive)
  - [Compression](#compression)
- [Migration Guide](#migration-guide)
  - [From HttpClient 4.x to 5.x](#from-httpclient-4x-to-5x)
  - [From URLConnection](#from-urlconnection)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Monitoring](#monitoring)
- [Advanced Features](#advanced-features)
  - [Custom Protocol](#custom-protocol)
  - [NTLM Authentication](#ntlm-authentication)
- [Best practices](#best-practices)
- [См. также](#см-также)

## Введение в **Apache HttpClient**

**Apache HttpClient** — это мощная и гибкая **HTTP** клиентская библиотека для **Java**, разработанная **Apache Software Foundation**. Является частью **Apache HttpComponents** проекта и предоставляет полную поддержку **HTTP** протокола, включая **HTTP**/1.1, **HTTP**/2 и различные механизмы аутентификации.

### Почему **Apache HttpClient**?

**HttpClient** предлагает множество преимуществ для **enterprise** приложений:**

1. **Полная поддержка HTTP** — **HTTP**/1.1, **HTTP**/2, **WebSocket**
2. **Высокая производительность** — **Connection pooling**, **keep-alive**
3. **Гибкая конфигурация** — Таймауты, прокси, **SSL**, кэширование
4. **Асинхронная поддержка** — **Future-based** и **callback-based API**
5. **Расширяемость** — Интерцепторы, кастомные компоненты
6. **Enterprise-ready** — Широко используется в **production**
7. **Backward compatibility** — Поддержка **legacy** систем
8. **Активная разработка** — Регулярные обновления и поддержка

### Архитектура **HttpClient**

**HttpClient** построен на модульной архитектуре:**

- **HttpClient** — Основной интерфейс для выполнения запросов
- **HttpRequest/HttpResponse** — **HTTP** сообщения
- **ConnectionManager** — Управление соединениями
- **RoutePlanner** — Планирование маршрутов
- **CredentialsProvider** — Управление учетными данными
- **CookieStore** — Хранение куков
- **RequestConfig** — Конфигурация запросов

### Версии **HttpClient**

- **HttpClient 5.x** — Современная версия с **HTTP**/2 поддержкой
- **HttpClient 4.x** — **Legacy** версия, широко используется
- **HttpClient 3.x** — Устаревшая версия

### Основные компоненты

Пример: создание **HTTP**-клиента и выполнение **GET**-запроса (**Java 11+** или **Apache HttpComponents**).

```java
// Основные интерфейсы
HttpClient httpClient = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder().build();
HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
```

## Основные возможности

### Простые **HTTP** запросы
```java
// Создание клиента
CloseableHttpClient httpClient = HttpClients.createDefault();

// Создание GET запроса
HttpGet httpGet = new HttpGet("https://api.example.com/users");

// Выполнение запроса
try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
    HttpEntity entity = response.getEntity();
    if (entity != null) {
        String result = EntityUtils.toString(entity);
        System.out.println(result);
    }
}
```

### Различные **HTTP** методы
```java
// POST запрос
HttpPost httpPost = new HttpPost("https://api.example.com/users");
String json = "{\"name\":\"John\",\"email\":\"john@example.com\"}";
StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
httpPost.setEntity(entity);

// PUT запрос
HttpPut httpPut = new HttpPut("https://api.example.com/users/1");
httpPut.setEntity(entity);

// DELETE запрос
HttpDelete httpDelete = new HttpDelete("https://api.example.com/users/1");

// PATCH запрос
HttpPatch httpPatch = new HttpPatch("https://api.example.com/users/1");
httpPatch.setEntity(entity);
```

### **Headers** и **Authentication**
```java
HttpGet httpGet = new HttpGet("https://api.example.com/secure");

// Добавление headers
httpGet.setHeader("Authorization", "Bearer " + token);
httpGet.setHeader("User-Agent", "MyApp/1.0");
httpGet.setHeader("Accept", "application/json");

// Basic Authentication
CredentialsProvider credsProvider = new BasicCredentialsProvider();
credsProvider.setCredentials(
    new AuthScope("api.example.com", 443),
    new UsernamePasswordCredentials("username", "password"));

CloseableHttpClient httpClient = HttpClients.custom()
    .setDefaultCredentialsProvider(credsProvider)
    .build();
```

### **Query Parameters**
```java
// Использование URIBuilder
URIBuilder builder = new URIBuilder("https://api.example.com/search");
builder.setParameter("q", "java")
       .setParameter("limit", "10")
       .setParameter("sort", "relevance");

HttpGet httpGet = new HttpGet(builder.build());

// Альтернативный способ
List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("q", "java"));
params.add(new BasicNameValuePair("limit", "10"));

URI uri = new URIBuilder("https://api.example.com/search")
    .addParameters(params)
    .build();

HttpGet httpGet2 = new HttpGet(uri);
```

## Продвинутые возможности

### **Connection Management**
```java
/
 * Настройка пула соединений для Apache HttpClient
 * Пул соединений позволяет переиспользовать HTTP соединения для повышения производительности
 */
// Создание менеджера пула соединений
// PoolingHttpClientConnectionManager управляет пулом HTTP соединений
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

// Настройка размера пула соединений
cm.setMaxTotal(100);           // Максимальное общее количество соединений в пуле (для всех маршрутов)
cm.setDefaultMaxPerRoute(20);  // Максимальное количество соединений на один маршрут (host:port)

// Создание HttpClient с настроенным пулом соединений
// HttpClients.custom() создает кастомный HttpClient с настройками
CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManager(cm)  // Устанавливаем менеджер пула соединений
    .build();                   // Строим HttpClient

// Получение статистики пула соединений для мониторинга
// getTotalStats() возвращает общую статистику по всем соединениям в пуле
System.out.println("Available connections: " + cm.getTotalStats().getAvailable());  // Доступные соединения (неиспользуемые)
System.out.println("Leased connections: " + cm.getTotalStats().getLeased());        // Арендованные соединения (используемые)
System.out.println("Pending connections: " + cm.getTotalStats().getPending());        // Ожидающие соединения (в очереди)
```

### **Connection Reuse**
```java
// Автоматическое управление соединениями
CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManagerShared(true)
    .setConnectionManager(cm)
    .build();

// Ручное управление
HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
HttpRoute route = new HttpRoute(new HttpHost("api.example.com", 443));

ConnectionRequest connRequest = cm.requestConnection(route, null);
HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);

try {
    // Использование соединения
    if (!conn.isOpen()) {
        cm.connect(conn, route, 10000, context);
    }
    cm.routeComplete(conn, route, context);
} finally {
    cm.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
}
```

### **SSL**/**TLS Configuration**
```java
/
 * Настройка SSL/TLS для Apache HttpClient
 * Демонстрирует различные способы настройки SSL: игнорирование (для тестов) и кастомный TrustStore
 */

// ВАЖНО: Игнорирование SSL проверки - ТОЛЬКО ДЛЯ ТЕСТИРОВАНИЯ!
// В продакшене НИКОГДА не используйте этот подход - это создает уязвимости безопасности
SSLContext sslContext = SSLContexts.custom()
    // loadTrustMaterial с null и всегда возвращающим true означает доверять всем сертификатам
    // Это отключает проверку SSL сертификатов (опасно для продакшена!)
    .loadTrustMaterial(null, (chain, authType) -> true)
    .build();

// Создание HttpClient с отключенной проверкой SSL (только для тестов!)
CloseableHttpClient httpClient = HttpClients.custom()
    .setSSLContext(sslContext)                              // Устанавливаем SSL контекст с отключенной проверкой
    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)  // Отключаем проверку имени хоста (опасно!)
    .build();

// Кастомный TrustStore - правильный подход для продакшена
// TrustStore содержит доверенные SSL сертификаты
KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  // Создаем пустой KeyStore
try (FileInputStream fis = new FileInputStream("truststore.jks")) {
    // Загружаем TrustStore из файла с паролем
    // truststore.jks содержит доверенные сертификаты
    trustStore.load(fis, "password".toCharArray());
}

// Создание SSL контекста с кастомным TrustStore
SSLContext sslContext = SSLContexts.custom()
    // Загружаем доверенные сертификаты из TrustStore
    // null означает использовать дефолтный TrustManager
    .loadTrustMaterial(trustStore, null)
    .build();

// Создание SSL socket factory с кастомным SSL контекстом
// SSLConnectionSocketFactory создает SSL соединения используя наш SSL контекст
SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
    sslContext,                              // SSL контекст с кастомным TrustStore
    NoopHostnameVerifier.INSTANCE);          // Отключаем проверку имени хоста (можно использовать DefaultHostnameVerifier для продакшена)

// Создание HttpClient с кастомным SSL socket factory
CloseableHttpClient httpClient = HttpClients.custom()
    .setSSLSocketFactory(sslSocketFactory)  // Устанавливаем кастомный SSL socket factory
    .build();
```

### **Proxy Configuration**
```java
// HTTP Proxy
HttpHost proxy = new HttpHost("proxy.example.com", 8080);

DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

CloseableHttpClient httpClient = HttpClients.custom()
    .setRoutePlanner(routePlanner)
    .build();

// Proxy с аутентификацией
CredentialsProvider credsProvider = new BasicCredentialsProvider();
credsProvider.setCredentials(
    new AuthScope("proxy.example.com", 8080),
    new UsernamePasswordCredentials("proxyuser", "proxypass"));

CloseableHttpClient httpClientWithProxy = HttpClients.custom()
    .setRoutePlanner(routePlanner)
    .setDefaultCredentialsProvider(credsProvider)
    .build();
```

### **Cookie Management**
```java
// Управление cookies
CookieStore cookieStore = new BasicCookieStore();
CloseableHttpClient httpClient = HttpClients.custom()
    .setDefaultCookieStore(cookieStore)
    .build();

// Добавление cookies вручную
BasicClientCookie cookie = new BasicClientCookie("sessionId", "abc123");
cookie.setDomain("api.example.com");
cookie.setPath("/");
cookieStore.addCookie(cookie);

// Получение cookies
List<Cookie> cookies = cookieStore.getCookies();
```

### **Timeout Configuration**
```java
/
 * Настройка таймаутов для Apache HttpClient
 * Таймауты предотвращают зависание приложения при проблемах с сетью
 */
// Создание конфигурации запросов с таймаутами
RequestConfig requestConfig = RequestConfig.custom()
    .setConnectTimeout(5000)              // Таймаут установки соединения (5 секунд)
    // Если соединение не установилось за 5 секунд, выбрасывается ConnectTimeoutException
    .setSocketTimeout(10000)              // Таймаут чтения/записи данных через сокет (10 секунд)
    // Если данные не передаются за 10 секунд, выбрасывается SocketTimeoutException
    .setConnectionRequestTimeout(5000)    // Таймаут получения соединения из пула (5 секунд)
    // Если все соединения в пуле заняты и новое не получено за 5 секунд, выбрасывается ConnectionPoolTimeoutException
    .build();

// Создание HttpClient с таймаутами по умолчанию
// Эти таймауты будут применяться ко всем запросам если не указаны другие
CloseableHttpClient httpClient = HttpClients.custom()
    .setDefaultRequestConfig(requestConfig)  // Устанавливаем конфигурацию по умолчанию
    .build();

// Таймауты для конкретного запроса - переопределение дефолтных таймаутов
// Полезно когда для конкретного запроса нужны другие таймауты (например, долгий запрос)
HttpGet httpGet = new HttpGet("https://api.example.com/data");
RequestConfig specificConfig = RequestConfig.copy(requestConfig)  // Копируем дефолтную конфигурацию
    .setSocketTimeout(30000)  // Переопределяем таймаут сокета на 30 секунд для этого запроса
    // Это позволяет конкретному запросу ждать дольше чем остальные
    .build();
httpGet.setConfig(specificConfig);  // Устанавливаем специфичную конфигурацию для этого запроса
```

### **Redirect Handling**
```java
// Автоматические редиректы
LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

CloseableHttpClient httpClient = HttpClients.custom()
    .setRedirectStrategy(redirectStrategy)
    .build();

// Кастомная стратегия редиректов
RedirectStrategy customRedirectStrategy = new DefaultRedirectStrategy() {
    @Override
    protected boolean isRedirected(HttpRequest request, HttpResponse response,
                                   HttpContext context) {
        int statusCode = response.getStatusLine().getStatusCode();
        String method = request.getRequestLine().getMethod();

        // Кастомная логика для редиректов
        return statusCode == 302 && "POST".equals(method);
    }
};
```

## Асинхронные операции

### **Future-based Async**
```java
CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();
asyncClient.start();

// Асинхронный GET
HttpGet httpGet = new HttpGet("https://api.example.com/data");
Future<HttpResponse> future = asyncClient.execute(httpGet, null);

// Получение результата
HttpResponse response = future.get(10, TimeUnit.SECONDS);
HttpEntity entity = response.getEntity();
String result = EntityUtils.toString(entity);

// Закрытие клиента
asyncClient.close();
```

### **Callback-based Async**
```java
CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();
asyncClient.start();

HttpGet httpGet = new HttpGet("https://api.example.com/data");

asyncClient.execute(httpGet, new FutureCallback<HttpResponse>() {
    @Override
    public void completed(HttpResponse response) {
        try {
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Exception ex) {
        System.err.println("Request failed: " + ex.getMessage());
    }

    @Override
    public void cancelled() {
        System.out.println("Request cancelled");
    }
});
```

### **Reactive Streams**
```java
// HttpClient 5.0+ поддерживает reactive streams
HttpClient httpClient = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .build();

CompletableFuture<HttpResponse<String>> response =
    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

response.thenAccept(res -> {
    System.out.println("Status: " + res.statusCode());
    System.out.println("Body: " + res.body());
});
```

## **Multipart Upload**

### **File Upload**
```java
HttpPost httpPost = new HttpPost("https://api.example.com/upload");

// Создание multipart entity
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

// Добавление файла
builder.addBinaryBody("file", new File("image.jpg"),
    ContentType.DEFAULT_BINARY, "image.jpg");

// Добавление текстовых полей
builder.addTextBody("description", "A beautiful image");
builder.addTextBody("tags", "nature,landscape");

// Установка entity
HttpEntity multipart = builder.build();
httpPost.setEntity(multipart);

// Выполнение запроса
try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
    System.out.println("Upload status: " + response.getStatusLine());
}
```

### **Streaming Upload**
```java
HttpPost httpPost = new HttpPost("https://api.example.com/upload");

// Streaming entity для больших файлов
AbstractHttpEntity streamingEntity = new AbstractHttpEntity() {
    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long getContentLength() {
        return -1; // Неизвестная длина
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return new FileInputStream("large-file.zip");
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        try (FileInputStream fis = new FileInputStream("large-file.zip")) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public boolean isStreaming() {
        return true;
    }
};

httpPost.setEntity(streamingEntity);
```

## **Caching**

### **HttpClient Cache**
```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5-cache</artifactId>
    <version>5.2.1</version>
</dependency>
```

```java
// Настройка кеширования
CacheConfig cacheConfig = CacheConfig.custom()
    .setMaxCacheEntries(1000)
    .setMaxObjectSize(8192) // 8KB
    .build();

CachingHttpClientBuilder builder = CachingHttpClients.custom();
builder.setCacheConfig(cacheConfig);
builder.setHttpCacheStorage(new BasicHttpCacheStorage(cacheConfig));

CloseableHttpClient cachingClient = builder.build();

// Cache-Control headers
HttpGet httpGet = new HttpGet("https://api.example.com/data");
httpGet.setHeader("Cache-Control", "max-age=3600");
```

## **Interceptors**

### **Request Interceptor**
```java
class RequestLoggingInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(HttpRequest request, HttpContext context)
            throws HttpException, IOException {
        System.out.println("Request: " + request.getRequestLine());

        // Добавление custom headers
        if (!request.containsHeader("X-Request-ID")) {
            request.addHeader("X-Request-ID", UUID.randomUUID().toString());
        }
    }
}

// Response Interceptor
class ResponseLoggingInterceptor implements HttpResponseInterceptor {

    @Override
    public void process(HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        System.out.println("Response: " + response.getStatusLine());

        // Обработка специфических статус кодов
        if (response.getStatusLine().getStatusCode() >= 500) {
            // Логика обработки серверных ошибок
        }
    }
}

// Регистрация интерцепторов
CloseableHttpClient httpClient = HttpClients.custom()
    .addInterceptorFirst(new RequestLoggingInterceptor())
    .addInterceptorLast(new ResponseLoggingInterceptor())
    .build();
```

## **Spring Boot Integration**

### **Configuration**
```java
@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
            .setConnectionManager(poolingConnectionManager())
            .setDefaultRequestConfig(requestConfig())
            .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(20);
        return cm;
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .build();
    }
}
```

### **RestTemplate Integration**
```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
```

## **Testing**

### **Mock Server Setup**
```java
public class HttpClientTest {

    private static final int PORT = 8089;
    private LocalTestServer server;
    private CloseableHttpClient httpClient;

    @BeforeEach
    public void setUp() throws Exception {
        server = new LocalTestServer(null, null);
        server.start();

        HttpHost target = new HttpHost("localhost", PORT);

        httpClient = HttpClients.custom()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testGetRequest() throws Exception {
        // Настройка mock ответа
        server.register("/api/test", new HttpRequestHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response,
                             HttpContext context) throws HttpException, IOException {
                response.setStatusCode(200);
                response.setEntity(new StringEntity("{\"message\":\"Hello\"}",
                    ContentType.APPLICATION_JSON));
            }
        });

        // Выполнение запроса
        HttpGet httpGet = new HttpGet("http://localhost:" + PORT + "/api/test");
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String body = EntityUtils.toString(response.getEntity());
            assertEquals("{\"message\":\"Hello\"}", body);
        }
    }
}
```

## **Performance Tuning**

### **Connection Keep-Alive**
```java
// Оптимизация keep-alive
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
cm.setValidateAfterInactivity(30000); // Валидация каждые 30 сек

CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManager(cm)
    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAlive = super.getKeepAliveDuration(response, context);
            if (keepAlive == -1) {
                // Сервер не указал timeout, используем 30 секунд
                keepAlive = 30000;
            }
            return keepAlive;
        }
    })
    .build();
```

### **Compression**
```java
// Автоматическая декомпрессия
CloseableHttpClient httpClient = HttpClients.custom()
    .addInterceptorFirst(new RequestAcceptEncoding())
    .addInterceptorFirst(new ResponseContentEncoding())
    .build();
```

## **Migration Guide**

### **From HttpClient** 4.x `to 5`.x
```java
// HttpClient 4.x
CloseableHttpClient client = HttpClientBuilder.create().build();
HttpGet request = new HttpGet("https://api.example.com/data");
CloseableHttpResponse response = client.execute(request);

// HttpClient 5.x
HttpClient client = HttpClient.newBuilder().build();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .build();
HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());
```

### **From URLConnection**
```java
// URLConnection
URL url = new URL("https://api.example.com/data");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
InputStream is = conn.getInputStream();

// HttpClient
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .build();
InputStream is = client.send(request,
    HttpResponse.BodyHandlers.ofInputStream()).body();
```

## Решение проблем

### **Common Issues**
```java
// Проблема: Connection pool exhausted
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
cm.setMaxTotal(200); // Увеличить пул
cm.setDefaultMaxPerRoute(50);

// Проблема: SocketTimeoutException
RequestConfig config = RequestConfig.custom()
    .setSocketTimeout(30000) // Увеличить таймаут
    .build();

// Проблема: SSLHandshakeException
SSLContext sslContext = SSLContexts.custom()
    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
    .build();
```

### **Monitoring**
```java
// Метрики соединений
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
PoolStats stats = cm.getTotalStats();
System.out.println("Available: " + stats.getAvailable());
System.out.println("Leased: " + stats.getLeased());
System.out.println("Pending: " + stats.getPending());
```

## **Advanced Features**

### **Custom Protocol**
```java
// Поддержка SPDY/HTTP2 (через HttpClient 5.x)
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build();
```

### **NTLM Authentication**
```java
NTCredentials ntCredentials = new NTCredentials(
    "username", "password", "workstation", "domain");

CredentialsProvider credsProvider = new BasicCredentialsProvider();
credsProvider.setCredentials(AuthScope.ANY, ntCredentials);

CloseableHttpClient httpClient = HttpClients.custom()
    .setDefaultCredentialsProvider(credsProvider)
    .build();
```

## **Best practices**

- **Переиспользование HttpClient:** создавайте один экземпляр `CloseableHttpClient` (через `HttpClients.custom()`) и переиспользуйте его; закрывайте клиент при остановке приложения.
- **Connection Manager:** используйте `PoolingHttpClientConnectionManager` для пула соединений; настраивайте **maxTotal** и **maxPerRoute** под нагрузку.
- **Таймауты:** задавайте **connection timeout**, **socket timeout** и **connection request timeout**; избегайте бесконечных ожиданий.
- **Освобождение ресурсов:** всегда закрывайте `HttpResponse` (try-`with-resources` или `EntityUtils.consume(entity)`), иначе возможна утечка соединений.
- **Retry и idempotency:** используйте `HttpRequestRetryHandler` для повторных попыток только для идемпотентных методов (`GET`, `PUT`, `DELETE`); для **POST** — с осторожностью.


## Полезные ссылки
- [Официальная документация `Apache HttpClient`](https://hc.apache.org/httpcomponents-client-5.3.x/)
- [HttpClient 4.x документация](https://hc.apache.org/httpcomponents-client-4.5.x/)
- [GitHub репозиторий](https://github.com/apache/httpcomponents-client)
- [Migration Guide](https://hc.apache.org/httpcomponents-client-5.3.x/migration-guide/index.html)

## См. также
- [OkHttp](java-okhttp.md) — Альтернативный **HTTP** клиент
- [Retrofit](java-retrofit.md) — **Type-safe HTTP** клиент
- [WebClient](../../frameworks/java-frameworks/spring/spring-webflux.md) — **Reactive HTTP** клиент

