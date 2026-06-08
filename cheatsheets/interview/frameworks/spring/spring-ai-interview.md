---
title: "Вопросы на собеседовании: Spring AI"
description: "Spring AI для интеграции LLM: ChatClient, EmbeddingModel, VectorStore, Function Calling, RAG pipeline, Advisors, тестирование с поддержкой OpenAI/Anthropic/Ollama"
tags:
  - interview
  - spring
  - spring-ai-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring AI"
  - "Spring AI interview"
  - "Spring AI собеседование"
prerequisites:
  - "[[spring-ai]]"
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Spring AI`

`Spring AI` — официальный модуль экосистемы Spring для интеграции AI-моделей. Унифицированный API поверх разных провайдеров (OpenAI, Anthropic, Vertex AI, Mistral, Ollama). Предоставляет `ChatClient`, `EmbeddingModel`, `VectorStore` и поддержку RAG. Горячая тема в 2024-2025 интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/) — официальная документация
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai) — репозиторий с примерами
- [Baeldung: Spring AI](https://www.baeldung.com/spring-ai) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring AI и какие задачи он решает?

**Spring AI** — официальный модуль экосистемы Spring, который даёт единый API для работы с AI-моделями. Главная идея — спрятать различия между провайдерами (OpenAI, Anthropic, Vertex AI, Mistral, Ollama и др.) за общими интерфейсами, чтобы код приложения не зависел от конкретного вендора.

**Какую проблему решает.** Без Spring AI вы пишете код под HTTP API конкретного провайдера: свои DTO, своя обработка ошибок, свой клиент. Сменить OpenAI на Anthropic — переписать всё это заново. Spring AI убирает эту привязку: смена провайдера сводится к замене starter-зависимости и пары строк в `application.yml`, бизнес-логика остаётся той же.

**Ключевые абстракции** (под каждой — общий интерфейс, реализации подставляет конкретный starter):

- **`ChatClient`** — диалог с chat-моделями (GPT-4, Claude, Gemini).
- **`EmbeddingModel`** — превращение текста в векторное представление.
- **`VectorStore`** — хранение и поиск по embedding-векторам.
- **`ImageModel`** — генерация изображений (DALL-E, Stable Diffusion).
- **`AudioTranscriptionModel`** — транскрипция аудио (Whisper).

Поверх этих примитивов Spring AI строит и более высокоуровневые вещи: RAG, function calling, chat memory, advisors. **Главная ценность** — переносимость: переключение между провайдерами без правки кода приложения.

## Q2. Как настроить Spring AI для работы с OpenAI?

Настройка состоит из трёх шагов: подключить starter, задать ключ и модель в конфиге, инжектить готовый бин.

**Шаг 1. Зависимость.** Один starter включает автоконфигурацию: на старте Spring сам создаст бины `ChatClient.Builder`, `EmbeddingModel` и т.д.

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

**Шаг 2. Конфигурация.** API-ключ берём из переменной окружения (не хардкодим в репозиторий), модель и параметры генерации задаём дефолтами — их можно переопределить на уровне отдельного запроса.

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.7
          max-tokens: 2000
      embedding:
        options:
          model: text-embedding-3-small
```

**Шаг 3. Использование.** Инжектируем `ChatClient` и работаем через fluent API: `prompt()` начинает запрос, `user()` задаёт сообщение пользователя, `call()` отправляет синхронно, `content()` достаёт текст ответа.

```java
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;

    public String ask(String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .content();
    }
}
```

Сам `ChatClient` обычно собирают из автоконфигурированного `ChatClient.Builder` — здесь же удобно задать общий system-промпт, который будет применяться ко всем запросам этого клиента.

```java
// Fluent builder для ChatClient
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a helpful Java developer assistant.")
        .build();
}
```

## Q3. Как использовать Prompt Templates и получать структурированный ответ?

**Prompt Template** — это промпт с плейсхолдерами `{name}`, которые подставляются через `.param()`. Это безопаснее ручной конкатенации строк: значения подставляются по имени, промпт остаётся читаемым, а его легко вынести в шаблон или ресурс.

В примере ниже `{language}` и `{code}` подставляются и в system-, и в user-сообщение — один и тот же параметр можно использовать в нескольких местах.

```java
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ChatClient chatClient;

    public String reviewCode(String code, String language) {
        return chatClient.prompt()
            .system("You are a senior {language} developer. Be concise.")
            .user(u -> u.text("Review this code:\n```{language}\n{code}\n```")
                .param("code", code)
                .param("language", language))
            .call()
            .content();
    }

    // Структурированный ответ через BeanOutputConverter
    public CodeReview getStructuredReview(String code) {
        return chatClient.prompt()
            .user("Review this Java code and provide structured feedback: " + code)
            .call()
            .entity(CodeReview.class);  // автоматически парсит JSON ответ в POJO
    }
}

public record CodeReview(
    List<String> issues,
    List<String> suggestions,
    int score,  // 1-10
    String summary
) {}
```

**Структурированный ответ через `.entity()`.** Вместо разбора сырого текста можно сразу получить POJO: Spring AI генерирует из record JSON-схему, добавляет её в промпт (модель отвечает строгим JSON) и через `BeanOutputConverter` парсит ответ в объект. Удобно, когда ответ нужно дальше использовать в коде, а не показывать пользователю.

**Подводный камень:** модель не гарантирует валидный JSON на 100%. Для продакшена снижайте `temperature` (детерминированность) и оборачивайте вызов в обработку ошибок парсинга.

## Q4. Что такое Embedding и как получить векторное представление текста?

**Embedding** — числовой вектор (массив float), который кодирует смысл текста. Ключевое свойство: близкие по смыслу тексты дают близкие векторы, даже если слова разные («автомобиль» и «машина» окажутся рядом). Именно это и делает возможным семантический поиск — мы сравниваем не слова, а смысл.

«Близость» измеряют косинусным сходством: чем меньше угол между векторами, тем ближе тексты по смыслу (метрика в диапазоне примерно от −1 до 1).

В Spring AI за это отвечает `EmbeddingModel`: `embed(text)` возвращает вектор для одной строки, `embedForResponse(...)` — батч за один запрос (дешевле и быстрее, чем по одному).

```java
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    public List<float[]> embedBatch(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(
            List.of(new TextSegment(texts.get(0)), new TextSegment(texts.get(1)))
        );
        return response.getResults().stream()
            .map(r -> r.getOutput())
            .toList();
    }

    // Косинусное сходство двух эмбеддингов
    public double similarity(float[] a, float[] b) {
        return EmbeddingUtil.cossineSimilarity(a, b);
    }
}
```

**Сценарии применения:** семантический поиск, поиск дубликатов и near-duplicate, кластеризация, классификация по смыслу и фундамент для RAG (Q6).

## Q5. Что такое VectorStore и как его использовать?

**VectorStore** — единый интерфейс Spring AI поверх векторных хранилищ: он хранит документы вместе с их embedding-векторами и умеет искать похожие по смыслу. Обычная БД ищет по точному совпадению или `LIKE`, а VectorStore — по близости векторов (ANN-поиск), то есть по смыслу запроса.

Важная деталь: при `add()` вы передаёте текст, а embedding VectorStore считает сам через переданный `EmbeddingModel` — вручную векторизовать документы не нужно. За реализацией стоит конкретное хранилище, но API один и тот же — сменить PgVector на Pinecone можно без правки логики поиска.

```java
// Поддерживаемые: PgVector, ChromaDB, Weaviate, Pinecone, Redis, Elasticsearch, Milvus
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel, JdbcTemplate jdbc) {
    return new PgVectorStore(jdbc, embeddingModel);
}
```

```java
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final VectorStore vectorStore;

    // Индексирование документов
    public void indexDocuments(List<String> texts, Map<String, Object> metadata) {
        List<Document> docs = texts.stream()
            .map(t -> new Document(t, metadata))
            .toList();
        vectorStore.add(docs);
    }

    // Семантический поиск
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(0.7)
        );
    }

    // Поиск с метаданным фильтром
    public List<Document> searchByTopic(String query, String topic) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)
                .withFilterExpression("topic == '" + topic + "'")
        );
    }
}
```

Два параметра поиска стоит понимать:

- **`topK`** — сколько ближайших документов вернуть.
- **`similarityThreshold`** — порог близости (0..1): документы ниже порога отбрасываются. Защищает от мусора, когда релевантного в базе нет: лучше вернуть пусто, чем подсунуть случайный документ.
- **`filterExpression`** — фильтр по метаданным (тема, автор, дата) поверх семантического поиска — сужает выдачу до нужного среза до сравнения векторов.

## Q6. Как построить RAG-pipeline в Spring AI?

**RAG (Retrieval-Augmented Generation)** решает проблему: модель не знает ваших данных и не имеет свежей информации, а отвечать «от себя» — значит галлюцинировать. Идея RAG — сначала найти релевантные документы в VectorStore, вставить их текст в промпт и попросить модель отвечать **только по этому контексту**. Модель из «всезнайки» превращается в «читателя предоставленных документов».

Пайплайн всегда из трёх шагов: **retrieve** (найти) → **augment** (вставить в промпт) → **generate** (сгенерировать ответ). Ручная реализация показывает все три явно:

```java
@Service
@RequiredArgsConstructor
public class RagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String answerWithContext(String question) {
        // 1. Поиск релевантных документов
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(5)
        );

        // 2. Формирование контекста
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));

        // 3. Генерация ответа с контекстом
        return chatClient.prompt()
            .system("""
                Answer based solely on the provided context.
                If the answer is not in the context, say "I don't know".
                Context:
                """ + context)
            .user(question)
            .call()
            .content();
    }
}
```

На практике три шага вручную не пишут — за них отвечает `QuestionAnswerAdvisor` (см. Q9). Он перехватывает запрос, сам делает поиск по VectorStore и подмешивает найденное в контекст. Логика та же, но вызов чистый — про RAG думает advisor, а не сервис:

```java
// Автоматический RAG через QuestionAnswerAdvisor
@Bean
public ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.defaults().withTopK(5)))
        .build();
}
```

**Подводный камень:** качество RAG упирается в качество retrieval. Если поиск вернул нерелевантные документы, модель ответит по ним — «garbage in, garbage out». Поэтому важны разбивка на чанки (Q12), порог близости и `topK`.

## Q7. Что такое Function Calling и как его использовать?

**Function Calling (tool calling)** даёт модели доступ к вашему коду. Сама по себе модель не умеет ходить в БД, дёргать API или узнавать текущую погоду — она лишь генерирует текст. Function calling это решает: вы описываете функции, а модель, видя их сигнатуры, сама решает, какую вызвать и с какими аргументами.

Важно понимать, что **модель не выполняет код** — она лишь возвращает «вызови `weatherFunction` с такими аргументами». Дальше Spring AI выполняет вашу функцию, отдаёт результат обратно модели, и та формирует финальный ответ. Весь этот round-trip фреймворк делает прозрачно.

Функцию объявляют обычным Spring-бином. Ключевой элемент — `@Description`: модель по этому тексту понимает, **когда** функцию звать, поэтому описание должно быть осмысленным. Типы аргументов и результата (record-ы) Spring AI превращает в JSON-схему для модели.

```java
// Определяем функцию как Spring Bean
@Bean
@Description("Get current weather for a given city")
public Function<WeatherRequest, WeatherResponse> weatherFunction(WeatherService service) {
    return request -> service.getWeather(request.city(), request.unit());
}

public record WeatherRequest(String city, String unit) {}
public record WeatherResponse(double temperature, String conditions) {}
```

```java
@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;

    public String askWithTools(String question) {
        return chatClient.prompt()
            .user(question)
            .functions("weatherFunction")  // имя Spring bean
            .call()
            .content();
        // Если вопрос про погоду → модель вызовет weatherFunction,
        // получит результат и сформирует финальный ответ
    }
}
```

`.functions("weatherFunction")` подключает функции к запросу по имени бина. Передать можно несколько — модель сама выберет нужную (или ни одной, если вопрос не требует инструмента). Это фундамент для AI-агентов (Q15).

## Q8. Как работает потоковая генерация (Streaming)?

**Streaming** отдаёт ответ по мере генерации, токен за токеном, а не целиком после завершения. Это тот же эффект «печатающегося ответа», что в ChatGPT. Технически разница в методе: `.call()` блокирует поток до полного ответа, а `.stream()` возвращает `Flux<String>`, где каждый элемент — очередной фрагмент.

```java
@Service
@RequiredArgsConstructor
public class StreamingChatService {
    private final ChatClient chatClient;

    // Streaming через Project Reactor Flux
    public Flux<String> streamAnswer(String question) {
        return chatClient.prompt()
            .user(question)
            .stream()
            .content();  // Flux<String> — каждый элемент = токен
    }
}
```

Чтобы поток дошёл до браузера, его отдают через Server-Sent Events: `produces = TEXT_EVENT_STREAM_VALUE` заставляет Spring слать каждый элемент `Flux` отдельным SSE-событием, и фронтенд показывает их сразу.

```java
// В WebFlux контроллере — Server-Sent Events
@RestController
@RequiredArgsConstructor
public class ChatController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String question) {
        return streamingChatService.streamAnswer(question);
    }
}
```

**Зачем это важно:** при длинном ответе модель генерирует его несколько секунд. Без стриминга пользователь смотрит на пустой экран всё это время; со стримингом первые слова появляются почти сразу — воспринимаемая скорость кардинально выше. **Компромисс:** стриминг сложнее обрабатывать на бэкенде (например, не получится распарсить ответ в POJO до его завершения), поэтому для структурированных ответов он не подходит.

## Q9. Что такое Advisors в Spring AI?

**Advisors** — это перехватчики (interceptor / middleware) для `ChatClient`. Каждый запрос и ответ проходит через цепочку advisor-ов, и каждый может его модифицировать или даже оборвать. По сути это аналог Servlet-фильтров или Spring-интерсепторов, но для AI-вызовов: они выносят сквозную логику (RAG, память, логирование, модерацию) из бизнес-кода в переиспользуемые компоненты.

Spring AI поставляет готовые advisor-ы — их достаточно зарегистрировать через `defaultAdvisors`, и поведение применится ко всем запросам клиента:

```java
// Встроенные Advisors
@Bean
public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore,
                              ChatMemory chatMemory) {
    return builder
        // RAG: автоматически добавляет релевантные документы в контекст
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
        // Память: сохраняет историю разговора
        .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
        // Логирование: выводит запросы/ответы
        .defaultAdvisors(new SimpleLoggerAdvisor())
        .build();
}
```

Можно написать и свой advisor. Метод `aroundCall` получает запрос и цепочку: проверив условие, advisor либо вызывает `chain.nextAroundCall()` (пропускает дальше), либо возвращает ответ сам, не доходя до модели. В примере ниже — модерация: запрещённый ввод сразу разворачивается отказом, экономя вызов к LLM.

```java
// Кастомный Advisor (например, для Content Moderation)
@Component
public class ModerationAdvisor implements CallAroundAdvisor {

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
        String userInput = request.userText();
        if (containsProhibitedContent(userInput)) {
            return AdvisedResponse.from(request,
                ChatResponse.builder()
                    .withGenerations(List.of(new Generation("I cannot help with that.")))
                    .build());
        }
        return chain.nextAroundCall(request);
    }
}
```

## Q10. Как реализовать многоходовой диалог (Chat Memory)?

LLM **не имеет состояния** — каждый запрос обрабатывается изолированно, без памяти о предыдущих. Чтобы модель «помнила» диалог, всю историю нужно слать ей заново при каждом сообщении. `ChatMemory` автоматизирует это: хранит сообщения разговора и сам подмешивает их в промпт через `MessageChatMemoryAdvisor` (Q9).

Реализацию выбирают по окружению: `InMemoryChatMemory` живёт в памяти процесса (теряется при рестарте — годится для разработки и тестов), а персистентные `JdbcChatMemory` / `CassandraChatMemory` переживают рестарт и работают на нескольких инстансах — это для production.

```java
// InMemoryChatMemory — для разработки/тестирования
@Bean
public ChatMemory chatMemory() {
    return new InMemoryChatMemory();
}

// CassandraChatMemory, JdbcChatMemory — для production
@Bean
public ChatMemory persistentChatMemory(JdbcTemplate jdbc) {
    return new JdbcChatMemory(jdbc);
}
```

```java
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ChatClient chatClient;

    public String chat(String conversationId, String message) {
        return chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .call()
            .content();
    }
}
```

Два параметра управляют выборкой истории:

- **`conversationId`** изолирует диалоги: история привязана к этому ключу, поэтому пользователи (или сессии) не видят чужих сообщений. Передавать его обязательно — без него все запросы смешаются в одну ленту.
- **`RETRIEVE_SIZE`** ограничивает, сколько последних сообщений подмешать в контекст. Это компромисс: больше истории — лучше связность, но дороже (платим за каждый токен) и риск упереться в context window модели.

## Q11. Как тестировать Spring AI приложения?

Главный принцип — **не звать настоящую модель в тестах**. Реальный вызов медленный, платный и недетерминированный (ответ меняется от запуска к запуску), что делает тесты ненадёжными. Вместо этого подменяют `ChatModel` — низкоуровневый интерфейс, через который `ChatClient` ходит к провайдеру.

**Вариант 1 — мок через Mockito.** Подменяем `ChatModel` бином-моком и задаём, что он вернёт. Тестируем свою логику (что сервис правильно собрал промпт и разобрал ответ), а не саму модель:

```java
// Мок ChatModel для unit-тестов
@SpringBootTest
class ChatServiceTest {

    @MockBean
    private ChatModel chatModel;

    @Autowired
    private ChatService chatService;

    @Test
    void shouldReturnAiResponse() {
        ChatResponse mockResponse = ChatResponse.builder()
            .withGenerations(List.of(new Generation("Mocked answer")))
            .build();

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        String result = chatService.ask("What is Spring AI?");

        assertThat(result).isEqualTo("Mocked answer");
    }
}
```

**Вариант 2 — `TestChatModel`.** Готовый stub от Spring AI: вместо жёсткого ответа задаёте функцию «по входу — выход». Удобно для интеграционных тестов, где ответ должен зависеть от запроса, но без похода к реальному API.

```java
// TestChatModel — Spring AI тестовый stub
@Bean
@Profile("test")
public ChatModel testChatModel() {
    return new TestChatModel(request -> {
        String userMessage = request.getInstructions().get(0).getContent();
        return new ChatResponse(List.of(new Generation("Test response for: " + userMessage)));
    });
}
```

## Q12. Как устроен Document ETL-пайплайн для индексирования документов?

Прежде чем VectorStore сможет искать по документам, их надо туда загрузить — этим занимается **ETL-пайплайн** Spring AI. Это та же триада, что в классическом ETL, но под векторный поиск:

- **Extract** — прочитать сырой документ через `DocumentReader` (PDF, Markdown, HTML, JSON). Реализация знает формат: `PagePdfDocumentReader` достаёт текст постранично, можно отрезать колонтитулы.
- **Transform** — разбить текст на чанки через `TextSplitter`. Это **ключевой шаг**: целый документ не влезет в context window, а слишком крупные чанки размывают релевантность. `TokenTextSplitter(512, 50)` режет по 512 токенов с перекрытием в 50 — overlap не даёт «разрезать» мысль на границе чанков.
- **Load** — `vectorStore.add(chunks)` сам считает embedding для каждого чанка и сохраняет. Отдельно векторизовать не нужно (см. Q5).

```java
@Service
@RequiredArgsConstructor
public class DocumentIndexingService {
    private final VectorStore vectorStore;

    // Извлечение из разных источников
    public void indexPdfDocuments(Resource pdfFile) {
        // Extract
        DocumentReader reader = new PagePdfDocumentReader(pdfFile,
            PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                    .withNumberOfBottomTextLinesToDelete(3)
                    .build())
                .build());

        // Transform (разбивка на чанки)
        TextSplitter splitter = new TokenTextSplitter(512, 50);  // 512 токенов, 50 overlap

        // Load (индексирование с автоматическим embedding)
        List<Document> chunks = splitter.apply(reader.get());
        vectorStore.add(chunks);
    }

    // Загрузка из URL или текстовых файлов
    public void indexMarkdown(Resource mdFile, Map<String, Object> metadata) {
        DocumentReader reader = new MarkdownDocumentReader(mdFile);
        List<Document> docs = reader.get().stream()
            .map(d -> new Document(d.getContent(), metadata))
            .toList();
        TextSplitter splitter = new TokenTextSplitter();
        vectorStore.add(splitter.apply(docs));
    }
}
```

## Q13. Чем Spring AI отличается от прямого использования OpenAI SDK?

Короткий ответ: OpenAI SDK — это тонкий клиент к одному провайдеру, а Spring AI — это фреймворк интеграции, который берёт на себя инфраструктурную обвязку (переносимость, RAG, память, retry) и встраивается в Spring-приложение через автоконфигурацию.

| Критерий | OpenAI SDK (прямой) | Spring AI |
|----------|---------------------|-----------|
| Портируемость | Только OpenAI | 10+ провайдеров |
| Интеграция с Spring | Вручную | Автоконфигурация |
| RAG / VectorStore | Нет | Встроено |
| Function Calling | Вручную | Декларативно (`@Description`) |
| Тестируемость | Сложнее (HTTP клиент) | Легко (мок ChatModel) |
| Retry / Resilience | Вручную | Встроено через Spring |
| Chat Memory | Нет | Встроено |

Итог: Spring AI берёт на себя инфраструктуру, и вы пишете бизнес-логику, а не клиент к API. **Когда прямой SDK всё же уместен:** если приложение завязано на один провайдер и использует его специфичные фичи, которых ещё нет в абстракции Spring AI, — тогда лишний слой не нужен.

## Q14. Как управлять стоимостью и токенами?

Провайдеры берут плату **за токены** — единицы текста (примерно ¾ слова), причём отдельно за входные (промпт) и выходные (ответ). Значит, управление стоимостью — это управление количеством токенов. Сначала их надо измерять: каждый `ChatResponse` несёт метаданные `Usage` с разбивкой по input/output/total — их логируют для мониторинга и расчёта затрат.

```java
// Получение метаданных использования токенов
ChatResponse response = chatClient.prompt()
    .user(question)
    .call()
    .chatResponse();

Usage usage = response.getMetadata().getUsage();
log.info("Tokens: input={}, output={}, total={}",
    usage.getPromptTokens(),
    usage.getGenerationTokens(),
    usage.getTotalTokens());
```

Дальше количество токенов и поведение модели регулируют параметрами генерации:

```yaml
# Ограничение токенов в конфигурации
spring:
  ai:
    openai:
      chat:
        options:
          max-tokens: 1000      # лимит генерации
          temperature: 0.3      # детерминированность (0=детерм, 1=креативно)
          top-p: 0.9            # nucleus sampling
          presence-penalty: 0.1 # штраф за повторение тем
```

Что из этого влияет на стоимость и качество:

- **`max-tokens`** — жёсткий потолок на длину ответа: прямой контроль расходов на output (и защита от «простыни»).
- **`temperature`** — разброс ответов: низкая (0.3) для фактических задач, где нужна стабильность; высокая — для креатива.
- **`top-p`** (nucleus sampling) и **`presence-penalty`** — тонкая настройка случайности и борьба с повторами.

Помимо этих параметров стоимость снижают и архитектурно: дешёвая модель там, где хватает её качества; компактный system-промпт; ограничение истории в chat memory (Q10); кеширование повторяющихся запросов.

## Q15. Как реализовать AI-агента на Spring AI?

**Агент** — это LLM, которая в цикле сама решает, какие инструменты вызывать для достижения цели, а не отвечает одним сообщением. Технически он строится на function calling (Q7): даёте модели набор функций и задачу, а она сама планирует последовательность вызовов. Связку «рассуждение → выбор инструмента → наблюдение результата → следующий шаг» называют **ReAct** (Reasoning + Acting).

System-промпт здесь — это инструкция агенту: какие инструменты есть и как ими пользоваться. Дальше модель сама выбирает, что и в каком порядке звать:

```java
// Простой агент с инструментами
@Service
@RequiredArgsConstructor
public class AgentService {
    private final ChatClient chatClient;

    public String runAgent(String task) {
        return chatClient.prompt()
            .system("""
                You are an autonomous agent. You have access to these tools:
                - searchOrders: search customer orders
                - getProductInfo: get product details
                - createTicket: create support ticket
                Use them to accomplish the task.
                """)
            .user(task)
            .functions("searchOrders", "getProductInfo", "createTicket")
            .call()
            .content();
    }
}

// ReAct pattern — цикл рассуждений
@Bean
@Description("Search orders by customer ID")
public Function<OrderSearchRequest, OrderSearchResult> searchOrders(OrderRepository repo) {
    return req -> new OrderSearchResult(repo.findByCustomerId(req.customerId()));
}
```

Современные модели (GPT-4, Claude) умеют **parallel function calling** — запросить сразу несколько функций в одном шаге, если они независимы. Это ускоряет агента: например, проверить заказы и подтянуть данные о товаре можно параллельно, а не друг за другом.

**Подводный камень:** агент с инструментами, меняющими состояние (создание тикетов, изменение данных), требует осторожности — модель может вызвать функцию не вовремя или с неверными аргументами. Опасные действия стоит ограничивать (валидация, подтверждение, права доступа).

## See also

- [LLM Basics](../../ai-ml/llm-basics-interview.md) — основы работы LLM, prompting, context window
- [RAG](../../ai-ml/rag-interview.md) — Retrieval-Augmented Generation, основной паттерн Spring AI
- [Vector Databases](../../ai-ml/vector-databases-interview.md) — PostgreSQL pgvector, Chroma, Pinecone
- [Embeddings](../../ai-ml/embeddings-interview.md) — векторные представления текста
- [AI Agents](../../ai-ml/ai-agents-interview.md) — паттерны агентов, ReAct, Chain of Thought
- [LLM Integration Patterns](../../ai-ml/llm-integration-patterns-interview.md) — интеграция LLM в приложения
- [Prompt Engineering](../../ai-ml/prompt-engineering-interview.md) — работа с промптами
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Spring AI
- [Spring WebFlux](spring-webflux-interview.md) — streaming ответов через Flux
- [PostgreSQL](../../databases/postgresql-interview.md) — pgvector как VectorStore
