---
title: "Retrofit"
description: "Retrofit - это type-safe HTTP клиент для Android и Java. Преобразует HTTP API в Java интерфейсы, упрощая работу с REST API."
tags:
  - libraries
  - java
  - java-retrofit
difficulty: "intermediate"
prerequisites:
  - java-lombok

next: []

updated: "2026-04-20"
---
# Retrofit

**Retrofit** — это **type-safe HTTP** клиент для **Android** и **Java**. Преобразует **HTTP API** в **Java** интерфейсы, упрощая работу с **REST API**.

## Полезные ссылки

### Официальная документация
- [Retrofit](https://square.github.io/retrofit/) — официальный сайт
- [Retrofit GitHub](https://github.com/square/retrofit) — репозиторий проекта
- [Retrofit Documentation](https://square.github.io/retrofit/2.x/retrofit/) — документация

### См. также
- [[java-okhttp|OkHttp]] — **OkHttp** для **HTTP** запросов
- [[java-rest-assured|REST Assured]] — **REST Assured** для тестирования **API**

- [[java-http-clients|HTTP-клиенты в Java]]
- [[java-jackson|Jackson: JSON-сериализация в Java]]
## Содержание

- [Основные возможности](#основные-возможности)
  - [Простой интерфейс API](#простой-интерфейс-api)
  - [HTTP методы](#http-методы)
  - [Parameters](#parameters)
    - [Path Parameters](#path-parameters)
    - [Query Parameters](#query-parameters)
    - [Header Parameters](#header-parameters)
    - [Form Parameters](#form-parameters)
- [Converters](#converters)
  - [Gson Converter](#gson-converter)
  - [Jackson Converter](#jackson-converter)
  - [Moshi Converter](#moshi-converter)
  - [Custom Converter](#custom-converter)
- [Call Adapters](#call-adapters)
  - [Default Call](#default-call)
  - [RxJava Call Adapter](#rxjava-call-adapter)
  - [Coroutines Call Adapter](#coroutines-call-adapter)
  - [Guava Call Adapter](#guava-call-adapter)
- [Interceptors](#interceptors)
  - [OkHttp Interceptors](#okhttp-interceptors)
  - [Dynamic Headers](#dynamic-headers)
- [Error Handling](#error-handling)
  - [Response Validation](#response-validation)
  - [Custom Error Handling](#custom-error-handling)
- [Advanced Features](#advanced-features)
  - [Multipart Upload](#multipart-upload)
  - [Streaming](#streaming)
  - [Custom Annotations](#custom-annotations)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [Service Layer](#service-layer)
- [Testing](#testing)
  - [Unit Testing с MockWebServer](#unit-testing-с-mockwebserver)
  - [Integration Testing](#integration-testing)
- [Performance Optimization](#performance-optimization)
  - [Connection Pooling](#connection-pooling)
  - [Caching](#caching)
  - [Compression](#compression)
- [Migration и Best Practices](#migration-и-best-practices)
  - [Migration from HttpUrlConnection](#migration-from-httpurlconnection)
  - [Лучшие практики](#лучшие-практики)
- [Experimental Features](#experimental-features)
  - [Kotlin Coroutines Support](#kotlin-coroutines-support)
  - [Kotlin Serialization](#kotlin-serialization)
- [Решение проблем](#решение-проблем)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)
- [См. также](#см-также-1)

## Основные возможности

### Простой интерфейс API

Определение **Retrofit API** через **Java**-интерфейс с аннотациями @**GET**, @**POST**, @**Path**, @**Query**, @**Body**.

```java
/
 * Демонстрация создания Retrofit API интерфейса
 * Retrofit преобразует Java интерфейс в HTTP клиент через аннотации
 */
public interface GitHubService {
    /
     * GET запрос для получения списка репозиториев пользователя
     * @GET указывает HTTP метод и путь endpoint
     * {user} - path параметр который будет заменен значением из @Path
     */
    @GET("users/{user}/repos")  // HTTP GET запрос на /users/{user}/repos
    Call<List<Repo>> listRepos(@Path("user") String user);  // @Path заменяет {user} в URL

    /
     * GET запрос с query параметром
     * @Query добавляет query параметр к URL (?type=...)
     */
    @GET("users/{user}/repos")  // HTTP GET запрос
    Call<List<Repo>> listRepos(@Path("user") String user, @Query("type") String type);
    // @Query("type") добавит ?type=... к URL

    /
     * POST запрос для создания репозитория
     * @Body отправляет объект в теле запроса (будет сериализован в JSON)
     */
    @POST("users/{user}/repos")  // HTTP POST запрос
    Call<Repo> createRepo(@Path("user") String user, @Body CreateRepoRequest request);
    // @Body сериализует CreateRepoRequest в JSON и отправляет в теле запроса
}

/
 * Создание Retrofit клиента с настройками
 * Retrofit.Builder позволяет настроить базовый URL, конвертеры и другие параметры
 */
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")  // Базовый URL для всех запросов
    // Все пути из @GET/@POST будут добавлены к этому базовому URL
    .addConverterFactory(GsonConverterFactory.create())  // Конвертер для JSON (Gson)
    // GsonConverterFactory преобразует JSON ответы в Java объекты и наоборот
    .build();  // Создаем Retrofit клиент

/
 * Создание реализации интерфейса
 * Retrofit автоматически создает реализацию интерфейса GitHubService
 */
GitHubService service = retrofit.create(GitHubService.class);
// service - это реализация GitHubService которая выполняет HTTP запросы
// При вызове методов интерфейса Retrofit автоматически выполняет HTTP запросы
```

### HTTP методы
```java
/
 * Демонстрация различных HTTP методов в Retrofit
 * Retrofit поддерживает все стандартные HTTP методы через аннотации
 */
public interface ApiService {
    // GET запросы - получение данных с сервера
    /
     * GET запрос без параметров - получение списка всех пользователей
     * @GET указывает HTTP метод GET и путь endpoint
     */
    @GET("users/list")  // HTTP GET /users/list
    Call<List<User>> getUsers();  // Возвращает Call<List<User>> для асинхронного выполнения

    /
     * GET запрос с path параметром - получение конкретного пользователя по ID
     * {id} в пути будет заменен значением из @Path("id")
     */
    @GET("users/{id}")  // HTTP GET /users/{id} где {id} заменяется на userId
    Call<User> getUser(@Path("id") int userId);  // @Path заменяет {id} в URL на userId

    // POST запросы - отправка данных на сервер
    /
     * POST запрос с телом запроса - создание нового пользователя
     * @Body сериализует объект User в JSON и отправляет в теле запроса
     */
    @POST("users")  // HTTP POST /users
    Call<User> createUser(@Body User user);  // @Body отправляет User в теле запроса

    /
     * POST запрос с path параметром и телом - создание поста для пользователя
     * Комбинирует @Path для ID пользователя и @Body для данных поста
     */
    @POST("users/{id}/posts")  // HTTP POST /users/{id}/posts
    Call<Post> createPost(@Path("id") int userId, @Body Post post);
    // {id} заменяется на userId, Post отправляется в теле запроса

    // PUT запросы
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body User user);

    // DELETE запросы
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    // PATCH запросы
    @PATCH("users/{id}")
    Call<User> patchUser(@Path("id") int userId, @Body Map<String, Object> updates);
}
```

### Parameters

#### Path Parameters
```java
public interface ApiService {
    @GET("users/{userId}/posts/{postId}")
    Call<Post> getPost(@Path("userId") int userId, @Path("postId") int postId);

    @GET("group/{groupId}/user/{userId}/profile")
    Call<Profile> getProfile(@Path("groupId") String groupId, @Path("userId") String userId);
}
```

#### Query Parameters
```java
public interface SearchService {
    @GET("search")
    Call<SearchResult> search(@Query("q") String query);

    @GET("search")
    Call<SearchResult> searchWithParams(
        @Query("q") String query,
        @Query("limit") Integer limit,
        @Query("offset") Integer offset,
        @Query("sort") String sort
    );

    // QueryMap для динамических параметров
    @GET("search")
    Call<SearchResult> search(@QueryMap Map<String, String> options);
}
```

#### Header Parameters
```java
public interface AuthService {
    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String authToken);

    @POST("login")
    Call<LoginResponse> login(@Header("X-API-Key") String apiKey, @Body LoginRequest request);

    // HeaderMap для множественных headers
    @GET("secure-data")
    Call<SecureData> getSecureData(@HeaderMap Map<String, String> headers);
}
```

#### Form Parameters
```java
public interface FormService {
    // URL-encoded form
    @FormUrlEncoded
    @POST("contact")
    Call<Void> sendMessage(
        @Field("name") String name,
        @Field("email") String email,
        @Field("message") String message
    );

    // Multipart form (для загрузки файлов)
    @Multipart
    @POST("upload")
    Call<UploadResponse> uploadFile(
        @Part("description") RequestBody description,
        @Part MultipartBody.Part file
    );
}
```

## Converters

### Gson Converter
```java
// Добавление Gson converter
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build();

// Кастомный Gson
Gson gson = new GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    .registerTypeAdapter(Date.class, new DateDeserializer())
    .create();

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build();
```

### Jackson Converter
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(JacksonConverterFactory.create())
    .build();

// С кастомным ObjectMapper
ObjectMapper mapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(JacksonConverterFactory.create(mapper))
    .build();
```

### Moshi Converter
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build();

// С кастомным Moshi
Moshi moshi = new Moshi.Builder()
    .add(Date.class, new DateAdapter())
    .build();

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build();
```

### Custom Converter
```java
public class CustomConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == CustomType.class) {
            return new CustomResponseConverter();
        }
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        if (type == CustomType.class) {
            return new CustomRequestConverter();
        }
        return null;
    }
}

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(new CustomConverterFactory())
    .build();
```

## Call Adapters

### Default Call
```java
public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
}

// Использование
Call<List<User>> call = apiService.getUsers();
call.enqueue(new Callback<List<User>>() {
    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.isSuccessful()) {
            List<User> users = response.body();
            // Обработка успешного ответа
        } else {
            // Обработка ошибки
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        // Обработка ошибки сети
    }
});
```

### RxJava Call Adapter
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build();

public interface RxApiService {
    @GET("users")
    Observable<List<User>> getUsers();

    @GET("users/{id}")
    Single<User> getUser(@Path("id") int userId);

    @POST("users")
    Completable createUser(@Body User user);
}

// Использование
apiService.getUsers()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(users -> {
        // Обработка результата
    }, error -> {
        // Обработка ошибки
    });
```

### Coroutines Call Adapter
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
    .build();

public interface CoroutineApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") int): User

    @POST("users")
    suspend fun createUser(@Body user: User): User
}

// Использование
try {
    val users = apiService.getUsers()
    // Обработка результата
} catch (e: Exception) {
    // Обработка ошибки
}
```

### Guava Call Adapter
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(GuavaCallAdapterFactory.create())
    .build();

public interface GuavaApiService {
    @GET("users")
    ListenableFuture<List<User>> getUsers();

    @GET("users/{id}")
    ListenableFuture<User> getUser(@Path("id") int userId);
}

// Использование
ListenableFuture<List<User>> future = apiService.getUsers();
Futures.addCallback(future, new FutureCallback<List<User>>() {
    @Override
    public void completed(List<User> users) {
        // Обработка результата
    }

    @Override
    public void onFailure(Throwable t) {
        // Обработка ошибки
    }
});
```

## Interceptors

### OkHttp Interceptors
```java
// Application Interceptor
class AuthInterceptor implements Interceptor {
    private String token;

    public AuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
            .header("Authorization", "Bearer " + token)
            .header("User-Agent", "Retrofit-App");

        Request request = builder.build();
        return chain.proceed(request);
    }
}

// Network Interceptor
class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        System.out.println("Request: " + request.method() + " " + request.url());

        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long endTime = System.currentTimeMillis();

        System.out.println("Response: " + response.code() + " in " + (endTime - startTime) + "ms");

        return response;
    }
}

// Создание OkHttpClient
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new AuthInterceptor("your-token"))
    .addNetworkInterceptor(new LoggingInterceptor())
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

// Использование в Retrofit
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

### Dynamic Headers
```java
class DynamicHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Добавление динамических headers
        Request.Builder builder = original.newBuilder()
            .addHeader("X-Request-ID", UUID.randomUUID().toString())
            .addHeader("X-Timestamp", String.valueOf(System.currentTimeMillis()));

        // Добавление headers из ThreadLocal
        String sessionId = SessionManager.getCurrentSessionId();
        if (sessionId != null) {
            builder.addHeader("X-Session-ID", sessionId);
        }

        return chain.proceed(builder.build());
    }
}
```

## Error Handling

### Response Validation
```java
public interface ApiService {
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);
}

// Обработка ответов
Call<User> call = apiService.getUser(123);
call.enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();
            // Обработка успешного ответа
        } else {
            // Обработка HTTP ошибок
            switch (response.code()) {
                case 404:
                    // Пользователь не найден
                    break;
                case 500:
                    // Серверная ошибка
                    break;
                default:
                    // Другая ошибка
                    break;
            }

            try {
                // Чтение тела ошибки
                String errorBody = response.errorBody().string();
                System.err.println("Error: " + errorBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        // Обработка сетевых ошибок
        if (t instanceof IOException) {
            // Сетевая ошибка
        } else {
            // Другая ошибка
        }
    }
});
```

### Custom Error Handling
```java
public class ApiException extends RuntimeException {
    private int code;
    private String errorBody;

    public ApiException(int code, String errorBody) {
        super("API Error: " + code + " - " + errorBody);
        this.code = code;
        this.errorBody = errorBody;
    }

    // getters
}

public class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    // Реализация адаптера для обработки ошибок
    // Это упрощенная версия, полная реализация требует больше кода
}
```

## Advanced Features

### Multipart Upload
```java
public interface FileService {
    @Multipart
    @POST("upload")
    Call<UploadResponse> uploadFile(
        @Part("description") RequestBody description,
        @Part MultipartBody.Part file
    );

    @Multipart
    @POST("upload/multiple")
    Call<UploadResponse> uploadMultipleFiles(
        @PartMap Map<String, RequestBody> params,
        @Part List<MultipartBody.Part> files
    );
}

// Использование
File file = new File("image.jpg");
RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/jpeg"));
MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

RequestBody description = RequestBody.create("Image description", MediaType.parse("text/plain"));

Call<UploadResponse> call = fileService.uploadFile(description, body);
```

### Streaming
```java
public interface StreamingService {
    @Streaming
    @GET("download/{fileId}")
    Call<ResponseBody> downloadFile(@Path("fileId") String fileId);
}

// Использование
Call<ResponseBody> call = streamingService.downloadFile("123");
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            boolean writtenToDisk = writeResponseBodyToDisk(response.body());
            // Обработка успешной загрузки
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        // Обработка ошибки
    }
});

private boolean writeResponseBodyToDisk(ResponseBody body) {
    try (InputStream inputStream = body.byteStream();
         OutputStream outputStream = new FileOutputStream(new File("downloaded_file"))) {

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return true;
    } catch (IOException e) {
        return false;
    }
}
```

### Custom Annotations
```java
@Target(METHOD)
@Retention(RUNTIME)
public @interface Cacheable {
    long maxAge() default 3600; // секунды
}

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface QueryParam {
    String value();
}

// Создание кастомного CallAdapter для обработки @Cacheable
public class CacheableCallAdapterFactory extends CallAdapter.Factory {
    // Реализация кеширования
}
```

## Spring Boot Integration

### Configuration
```java
@Configuration
public class RetrofitConfig {

    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    public GitHubService gitHubService(Retrofit retrofit) {
        return retrofit.create(GitHubService.class);
    }
}
```

### Service Layer
```java
@Service
public class GitHubApiService {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubApiService(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    public List<Repo> getUserRepos(String username) {
        try {
            Response<List<Repo>> response = gitHubService.listRepos(username).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new RuntimeException("API Error: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Network Error", e);
        }
    }

    public Observable<List<Repo>> getUserReposRx(String username) {
        return gitHubService.listReposRx(username)
            .subscribeOn(Schedulers.io());
    }
}
```

## Testing

### Unit Testing с MockWebServer
```java
public class ApiServiceTest {

    private MockWebServer mockWebServer;
    private ApiService apiService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        apiService = retrofit.create(ApiService.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetUser() throws IOException {
        // Подготовка mock ответа
        String jsonResponse = """
            {
                "id": 1,
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(jsonResponse)
            .addHeader("Content-Type", "application/json"));

        // Выполнение теста
        Call<User> call = apiService.getUser(1);
        Response<User> response = call.execute();

        assertTrue(response.isSuccessful());
        User user = response.body();
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());

        // Проверка запроса
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/users/1", request.getPath());
    }
}
```

### Integration Testing
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class ApiServiceIntegrationTest {

    @Autowired
    private ApiService apiService;

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    void testApiIntegration() {
        wireMockServer.stubFor(get("/api/users/1")
            .willReturn(okJson("""
                {
                    "id": 1,
                    "name": "John Doe"
                }
                """)));

        User user = apiService.getUser(1).execute().body();

        assertEquals("John Doe", user.getName());
    }
}
```

## Performance Optimization

### Connection Pooling
```java
OkHttpClient client = new OkHttpClient.Builder()
    .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
    .build();

Retrofit retrofit = new Retrofit.Builder()
    .client(client)
    .build();
```

### Caching
```java
int cacheSize = 10 * 1024 * 1024; // 10 MB
Cache cache = new Cache(new File("cache"), cacheSize);

OkHttpClient client = new OkHttpClient.Builder()
    .cache(cache)
    .build();
```

### Compression
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new GzipRequestInterceptor())
    .build();

class GzipRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        if (original.body() != null &&
            original.header("Content-Encoding") == null) {
            Request compressed = original.newBuilder()
                .addHeader("Content-Encoding", "gzip")
                .method(original.method(), gzip(original.body()))
                .build();

            return chain.proceed(compressed);
        }

        return chain.proceed(original);
    }
}
```

## Migration и Best Practices

### Migration from HttpUrlConnection
```java
// Старый код
URL url = new URL("https://api.example.com/users");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
InputStream is = conn.getInputStream();

// Новый код с Retrofit
public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
}

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .build();

ApiService service = retrofit.create(ApiService.class);
Response<List<User>> response = service.getUsers().execute();
List<User> users = response.body();
```

### Лучшие практики
```java
public class RetrofitBestPractices {

    // 1. Используйте интерфейсы для API
    public interface UserService {
        @GET("users/{id}")
        Call<User> getUser(@Path("id") int id);

        @GET("users")
        Call<List<User>> getUsers(@Query("page") int page, @Query("limit") int limit);
    }

    // 2. Обработка ошибок
    public void handleApiCall(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Успешная обработка
                } else {
                    // Обработка API ошибок
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Обработка сетевых ошибок
            }
        });
    }

    // 3. Singleton для Retrofit instance
    public static class ApiClient {
        private static Retrofit retrofit;

        public static Retrofit getInstance() {
            if (retrofit == null) {
                synchronized (ApiClient.class) {
                    if (retrofit == null) {
                        retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    }
                }
            }
            return retrofit;
        }
    }
}
```

## Experimental Features

### Kotlin Coroutines Support
```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") Int): User

    @POST("users")
    suspend fun createUser(@Body user: User): User
}

// Использование
try {
    val user = apiService.getUser(123)
    // Обработка результата
} catch (e: Exception) {
    // Обработка ошибки
}
```

### Kotlin Serialization
```kotlin
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

val contentType = "application/json; charset=utf-8".toMediaType()
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        json.asConverterFactory(contentType)
    )
    .build()
```

## Решение проблем

### Common Issues
```java
// Проблема: Timeout
OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)  // Увеличить таймауты
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build();

// Проблема: SSL errors
OkHttpClient unsafeClient = new OkHttpClient.Builder()
    .sslSocketFactory(sslSocketFactory, trustManager)  // Только для dev
    .hostnameVerifier((hostname, session) -> true)
    .build();

// Проблема: NetworkOnMainThreadException (Android)
call.enqueue(new Callback<T>() {  // Всегда используйте enqueue вместо execute
    // ...
});
```

### Debugging
```java
// Включение логирования
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(HttpLoggingInterceptor.Level.BODY);

OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(logging)
    .build();

// Или через Retrofit logging
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```


## Полезные ссылки
- [Официальная документация Retrofit](https://square.github.io/retrofit/)
- [GitHub репозиторий](https://github.com/square/retrofit)
- [Retrofit samples](https://github.com/square/retrofit/tree/master/samples)
- [OkHttp документация](https://square.github.io/okhttp/)

## См. также
- [[java-okhttp|OkHttp]] — **HTTP** клиент для **Retrofit**
- [[java-gson|Gson]] — **JSON** библиотека для **Retrofit**
- [Обзор библиотек](../) — Реактивное программирование (RxJava и др.)

