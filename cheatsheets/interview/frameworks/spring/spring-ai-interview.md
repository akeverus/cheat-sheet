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
updated: 2026-11-05
---
# Вопросы на собеседовании: `Spring AI`

`Spring AI` — официальный модуль экосистемы Spring для интеграции AI-моделей. Унифицированный API поверх разных провайдеров (OpenAI, Anthropic, Vertex AI, Mistral, Ollama). Предоставляет `ChatClient`, `EmbeddingModel`, `VectorStore` и поддержку RAG. Горячая тема в 2024-2025 интервью.

Дата последнего обновления: 2026-11-05

## Полезные ссылки

### Официальная документация

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/) — официальная документация
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai) — репозиторий с примерами
- [Baeldung: Spring AI](https://www.baeldung.com/spring-ai) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring AI и какие задачи он решает?

**Spring AI** — проект экосистемы Spring с общими интерфейсами для моделей, инструментов и векторных хранилищ. `ChatModel`, `EmbeddingModel` и `VectorStore` позволяют сохранить значительную часть прикладного кода при смене реализации, но возможности моделей, параметры запросов и устройство хранилищ различаются. [Chat Model API](https://docs.spring.io/spring-ai/reference/api/chatmodel.html), [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html).

**Какую проблему решает.** Вместо отдельного HTTP-клиента и DTO для каждого провайдера приложение работает через интерфейсы Spring AI. При переходе с OpenAI на Anthropic обычный вызов `ChatClient` может сохраниться; при этом нужно выбрать совместимую модель, перенести настройки и проверить функции, на которые опирается приложение: structured output, tool calling, ограничения контекста и тарифы. Общий интерфейс сокращает миграцию, но не заменяет её проверку.

**Ключевые абстракции** (под каждой — общий интерфейс, реализации подставляет конкретный starter):

- **`ChatClient`** — диалог с chat-моделями (GPT-4, Claude, Gemini).
- **`EmbeddingModel`** — превращение текста в векторное представление.
- **`VectorStore`** — хранение и поиск по embedding-векторам.
- **`ImageModel`** — генерация изображений (DALL-E, Stable Diffusion).
- **`AudioTranscriptionModel`** — транскрипция аудио (Whisper).

Поверх этих примитивов Spring AI строит RAG, tool calling, chat memory и advisors. Общий API упрощает перенос, а объём изменений зависит от использованных возможностей и выбранных реализаций.

## Q2. Как настроить Spring AI 2.0.2 для работы с OpenAI?

Для **Spring AI 2.0.2** подключите актуальный OpenAI starter, задайте ключ и параметры модели, затем создайте `ChatClient` из автонастроенного `ChatClient.Builder`. Версию Spring AI задавайте через BOM проекта. См. [OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html) и [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

**Шаг 1. Зависимость.** В ветке 2.x имя starter изменилось. Он включает автоконфигурацию OpenAI `ChatModel` и `EmbeddingModel`; Spring Boot также создаёт `ChatClient.Builder`. Старое имя `spring-ai-openai-spring-boot-starter` относится к прежним выпускам.

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

**Шаг 2. Конфигурация.** Ключ берём из окружения. В Spring AI 2.0 свойства модели записываются без прежнего промежуточного `options`: например, `spring.ai.openai.chat.model` и `spring.ai.openai.embedding.model`. `max-tokens` в примере относится к выбранной нерезонирующей модели `gpt-4o`; для reasoning-моделей документация указывает `max-completion-tokens`, и эти два параметра нельзя задавать вместе. Дефолтные chat options можно переопределить в отдельном запросе. [Свойства OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html#configuration-properties), [OpenAI Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings/openai-embeddings.html#configuration-properties).

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        model: gpt-4o
        temperature: 0.7
        max-tokens: 2000
      embedding:
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

Сам `ChatClient` обычно собирают из автоконфигурированного `ChatClient.Builder` — здесь же можно задать общий system-промпт. Если в приложении несколько `ChatModel`, выберите модель явно и проверьте конфигурацию builder: простой вызов `ChatClient.builder(chatModel)` обходит автоконфигурированные customizers и observability. [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html#working-with-multiple-chat-models).

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

**Prompt Template** — это промпт с плейсхолдерами `{name}`, которые подставляются через `.param()`. Именованные параметры сохраняют промпт читаемым и позволяют вынести его в ресурс. Подстановка не делает недоверенный текст безопасным от prompt injection: границы доступа к данным и инструментам проверяет приложение.

В примере ниже `{language}` используется в system- и user-шаблонах. Значение задаётся отдельно для каждого сообщения; внутри одного шаблона плейсхолдер можно повторять.

По умолчанию `ChatClient` использует `StTemplateRenderer` с разделителями `{` и `}`. Если сам текст промпта содержит JSON, его фигурные скобки могут быть приняты за переменные шаблона. Для такого промпта задайте другие разделители через `.templateRenderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())` и обозначайте переменные как `<code>`; либо используйте `NoOpTemplateRenderer`, если подстановка не нужна. Настройка renderer для `ChatClient` не меняет внутренние шаблоны advisors. [Chat Client: Prompt Templates](https://docs.spring.io/spring-ai/reference/api/chatclient.html#prompt-templates).

```java
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ChatClient chatClient;

    public String reviewCode(String code, String language) {
        return chatClient.prompt()
            .system(s -> s.text("You are a senior {language} developer. Be concise.")
                .param("language", language))
            .user(u -> u.text("Review this code:\n```{language}\n{code}\n```")
                .param("code", code)
                .param("language", language))
            .call()
            .content();
    }

    // Структурированный ответ через BeanOutputConverter
    public CodeReview getStructuredReview(String code) {
        return chatClient.prompt()
            .user(u -> u.text("Review this Java code and provide structured feedback:\n{code}")
                .param("code", code))
            .call()
            .entity(CodeReview.class);
    }
}

public record CodeReview(
    List<String> issues,
    List<String> suggestions,
    int score,  // 1-10
    String summary
) {}
```

**Структурированный ответ через `.entity()`.** В Spring AI 2.0.2 схема генерируется из типа, добавляется к инструкциям модели, затем ответ преобразуется в объект. Обычный `.entity(CodeReview.class)` — режим best effort: схема в промпте сама по себе не гарантирует ни валидный JSON, ни соблюдение всех ограничений.

Если результат определяет дальнейшую логику, `useProviderStructuredOutput()` передаёт схему через API провайдера, который поддерживает этот режим. Если chat options модели не реализуют `StructuredOutputChatOptions`, флаг **молча игнорируется** и остаётся обычная схема в промпте. Поэтому проверяйте поддержку конкретной модели и формат ответа, не выводя её из одного успешного вызова. `validateSchema()` проверяет ответ в приложении и при нарушении схемы добавляет описание ошибки к пользовательскому сообщению для следующего запроса. Их можно сочетать: [Provider-Native Structured Output](https://docs.spring.io/spring-ai/reference/api/structured-output/native.html).

```java
CodeReview review = chatClient.prompt()
    .user(u -> u.text("Review this Java code:\n{code}").param("code", code))
    .call()
    .entity(CodeReview.class, spec -> spec
        .useProviderStructuredOutput()
        .validateSchema());
```

В Spring AI 2.0.2 проверка допускает до **трёх повторов после первого запроса**, то есть до четырёх проходов через оставшуюся цепочку advisors. Например, если ответ вернул массив в поле, где схема ожидает строку, следующий запрос сообщает модели о несовпадении типа. Каждый повтор добавляет задержку и расход токенов. При исчерпании повторов advisor возвращает последний ответ, а `.entity()` пытается его преобразовать. Если преобразование получилось, это ещё не доказывает соответствие схеме. Когда результат запускает бизнес-операцию, приложению нужна явная проверка перед действием. [Исходный код валидатора Spring AI 2.0.2](https://github.com/spring-projects/spring-ai/blob/v2.0.2/spring-ai-client-chat/src/main/java/org/springframework/ai/chat/client/advisor/StructuredOutputValidationAdvisor.java), [ChatClient Spring AI 2.0.2](https://github.com/spring-projects/spring-ai/blob/v2.0.2/spring-ai-client-chat/src/main/java/org/springframework/ai/chat/client/DefaultChatClient.java).

Поддержка JSON Schema на стороне провайдера зависит от модели. Проверка схемы отвечает за **форму**, а не за достоверность выводов: комментарий `score // 1-10` в record не задаёт ограничение диапазона, его и применимость оценки проверяет код приложения. `.entity()` работает с завершённым ответом через `.call()`, не с фрагментами `.stream()`. [Structured Output](https://docs.spring.io/spring-ai/reference/api/structured-output.html), [Schema Validation](https://docs.spring.io/spring-ai/reference/api/structured-output/validation.html).

## Q4. Что такое Embedding и как получить векторное представление текста?

**Embedding** — числовой вектор, который модель строит из текста для поиска по смысловой близости. Например, запрос «автомобиль» может найти документ со словом «машина», даже если точного совпадения нет. Близость не гарантирована для каждой пары текстов: результат зависит от модели, языка и данных. Сравнивать следует векторы совместимой размерности, полученные одной и той же моделью и конфигурацией. [Embedding Model API](https://docs.spring.io/spring-ai/reference/api/embeddings.html).

В Spring AI 2.0.2 `EmbeddingModel.embed(String)` возвращает `float[]`, а `embed(List<String>)` — список векторов. `embedForResponse(List<String>)` нужен, когда вместе с векторами требуются данные `EmbeddingResponse`, например метаданные ответа. Список передаёт несколько текстов одним вызовом интерфейса, но число сетевых запросов, стоимость, ограничения размера и скорость зависят от реализации и провайдера. [EmbeddingModel Javadoc 2.0.2](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/embedding/EmbeddingModel.html).

```java
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    public List<float[]> embedBatch(List<String> texts) {
        return embeddingModel.embed(texts);
    }
}
```

Для поиска не нужно вручную вычислять косинусную формулу: `VectorStore.similaritySearch(...)` из Q5 использует метрику конкретного хранилища. Косинусное сходство — один из вариантов сравнения векторов; формула требует одинаковой размерности и ненулевых векторов. Применения: семантический поиск, поиск похожих документов и RAG (Q6). [Understanding Vectors](https://docs.spring.io/spring-ai/reference/api/vectordbs/understand-vectordbs.html).

## Q5. Как использовать VectorStore с PGvector в Spring AI 2.0.2 и что меняется в 2.1.0-RC1?

**`VectorStore` даёт общий API для записи документов и поиска по близости векторов.** Поиск может быть точным или приближённым: для PGvector это зависит, в частности, от выбранного индекса (`NONE`, `IVFFlat` или `HNSW`). Близость векторов помогает искать по смыслу, а метаданные ограничивают область поиска. См. [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) и [PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html).

В **Spring AI 2.0.2** при `add(List<Document>)` реализация использует настроенный `EmbeddingModel` для текста документа. Для PGvector подключите starter хранилища вместе с starter модели из Q2, задайте PostgreSQL datasource и внедрите автонастроенный `VectorStore`. Схема и расширения PostgreSQL должны существовать заранее: `spring.ai.vectorstore.pgvector.initialize-schema` по умолчанию `false`; включайте автоматическую инициализацию осознанно. Размерность столбца должна совпадать с размерностью эмбеддингов.

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
```

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/app
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  ai:
    vectorstore:
      pgvector:
        initialize-schema: false
```

Если starter с автоконфигурацией не подходит, в ручной конфигурации используют `PgVectorStore.builder(jdbcTemplate, embeddingModel).build()` с отдельным модулем `spring-ai-pgvector-store`; прежний `new PgVectorStore(jdbc, embeddingModel)` из примера к 2.x не подходит. [Ручная настройка PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html#manual-configuration).

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
            SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.7)
                .build()
        );
    }

    // Поиск с метаданным фильтром
    public List<Document> searchByTopic(String query, String topic) {
        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        return vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(5)
                .filterExpression(filter.eq("topic", topic).build())
                .build()
        );
    }
}
```

Три параметра поиска стоит понимать:

- **`topK`** — верхняя граница числа возвращаемых документов.
- **`similarityThreshold`** — нижняя граница похожести: документы ниже неё отбрасываются. Порог настраивают по реальным запросам и качеству ответов.
- **`filterExpression`** — фильтр по метаданным (тема, автор, дата). Для значения из запроса удобен `FilterExpressionBuilder`: не нужно собирать строковое выражение конкатенацией.

**Пример.** Запрос с `topK=10` и фильтром по редкой теме возвращает три документа, хотя в хранилище их больше десяти и порог похожести выключен. Если PostgreSQL выбрал общий приближённый индекс HNSW, он проверяет условие по теме на кандидатах, найденных индексом. Часть подходящих документов могла не попасть в эту выборку. Сначала проверьте план через `EXPLAIN (ANALYZE, BUFFERS)`. Начиная с pgvector 0.8.0, итеративное сканирование HNSW может продолжить поиск кандидатов до набора достаточного числа строк или достижения лимита сканирования. Для редкой темы можно сравнить результат и задержку с точным поиском по отфильтрованным строкам. [pgvector: фильтрация и итеративное сканирование](https://github.com/pgvector/pgvector#filtering).

Общий интерфейс помогает менять хранилище, но при переходе с PGvector на Pinecone нужно проверить схему метаданных, размерность, тип индекса, поддержку фильтра и поведение поиска. В предварительной ветке **Spring AI 2.1**, начиная с [2.1.0-M1](https://spring.io/blog/2026/09/25/spring-ai-2-1-0-M1-available-now/), появился `VectorStore.upsert(List<EmbeddedDocument>)` для уже вычисленных векторов; он не вызывает настроенный `EmbeddingModel` и реализован не во всех хранилищах. В `2.1.0-RC1` PGvector поддерживает этот метод, но на 05.11.2026 ветка 2.1 ещё предварительная. Обычный `add()` по-прежнему вычисляет вектор текста. [Анонс и пример `upsert`](https://spring.io/blog/2026/10/28/spring-ai-upsert-own-embeddings/).

## Q6. Как построить RAG-pipeline в Spring AI?

**RAG (Retrieval-Augmented Generation)** дополняет запрос к модели найденными данными без изменения её весов. Типичный путь: найти документы, добавить подходящие фрагменты в контекст и сгенерировать ответ. Это даёт модели доступ к данным приложения, но само по себе не гарантирует правильность ответа или следование инструкции «только по контексту». [Spring AI RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html).

Простой вариант полезно разобрать вручную, чтобы видеть границу между поиском и генерацией:

```java
@Service
@RequiredArgsConstructor
public class RagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String answerWithContext(String question) {
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder().query(question).topK(5)
                .similarityThreshold(0.7).build()
        );

        if (docs.isEmpty()) {
            return "В найденных документах нет ответа.";
        }

        String context = docs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));

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

Для простого RAG можно подключить `QuestionAnswerAdvisor`: он ищет документы в `VectorStore` и добавляет их к пользовательскому запросу перед вызовом модели. В Spring AI 2.0.2 для него нужен модуль `spring-ai-vector-store-advisor` в дополнение к starter хранилища из Q5. При настройке `defaultAdvisors` поиск применяется ко всем вызовам этого `ChatClient` (см. Q9). [RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#questionansweradvisor).

```java
@Bean
public ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
        .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder().topK(5)
                .similarityThreshold(0.7).build())
            .build())
        .build();
}
```

`QuestionAnswerAdvisor` реализует простой RAG. Когда нужны переписывание запроса, фильтрация или повторное ранжирование фрагментов, Spring AI предлагает `RetrievalAugmentationAdvisor` из отдельного модуля `spring-ai-rag`. Качество проверяют на реальных вопросах: настраивают чанки (Q12), `topK`, порог и метаданные, оценивают найденные фрагменты и ответы. Найденный текст остаётся недоверенным вводом: инструкция в документе может попытаться изменить поведение модели. При отсутствии подходящих документов нужен явный сценарий отказа; показанный ручной вариант делает это до вызова модели, а настройки встроенных advisors следует проверять отдельно. [RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#retrievalaugmentationadvisor).

## Q7. Что такое Function Calling и как его использовать?

**Function Calling (tool calling)** даёт модели доступ к вашему коду. Сама по себе модель не умеет ходить в БД, дёргать API или узнавать текущую погоду — она лишь генерирует текст. Function calling это решает: вы описываете функции, а модель, видя их сигнатуры, сама решает, какую вызвать и с какими аргументами.

Важно понимать, что **модель не выполняет код** — она лишь возвращает «вызови `getWeather` с такими аргументами». В Spring AI 2.0.2 `ChatClient` автоматически включает `ToolCallingAdvisor`: тот обрабатывает запросы модели, передаёт их `ToolCallingManager`, возвращает результат модели и обычно повторяет цикл до ответа без вызова инструмента. Инструмент с `returnDirect = true` может вернуть результат вызывающему коду без нового обращения к модели. Цикл поддерживает и `.call()`, и `.stream()`; прямой вызов `ChatModel` с инструментами не запускает его автоматически. Приложение остаётся владельцем кода и его прав доступа. [Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html#the-tool-calling-loop), [Return Direct](https://docs.spring.io/spring-ai/reference/api/tools.html#return-direct).

Инструмент объявляют методом с аннотацией `@Tool`. Ключевой элемент — `description`: модель по этому тексту понимает, **когда** инструмент звать, поэтому описание должно быть осмысленным. Типы аргументов и результата (record-ы) Spring AI превращает в JSON-схему для модели.

```java
// Определяем инструмент как @Tool-метод в Spring-бине
@Component
@RequiredArgsConstructor
public class WeatherTools {
    private final WeatherService weatherService;

    @Tool(description = "Get current weather for a given city")
    public WeatherResponse getWeather(WeatherRequest request) {
        return weatherService.getWeather(request.city(), request.unit());
    }
}

public record WeatherRequest(String city, String unit) {}
public record WeatherResponse(double temperature, String conditions) {}
```

```java
@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;
    private final WeatherTools weatherTools;

    public String askWithTools(String question) {
        return chatClient.prompt()
            .user(question)
            .tools(weatherTools)
            .call()
            .content();
    }
}
```

`.tools(weatherTools)` подключает Spring-бин с `@Tool`-методами к конкретному запросу. Можно передать несколько инструментов или явные `ToolCallback`; модель может не выбрать ни одного. **Само наличие Spring-бина не делает инструмент доступным для любого запроса.** Начиная с 2.0.1, fallback к общему `ToolCallbackResolver` выключен по умолчанию: выполняются инструменты, переданные через `.tools(...)`, `.defaultTools(...)` или `ToolCallingChatOptions.toolCallbacks(...)`. Не включайте глобальный fallback ради удобства, не оценив область доступа к инструментам. [Upgrade Notes 2.0.1](https://docs.spring.io/spring-ai/reference/upgrade-notes.html#tool-resolution-fallback-disabled-by-default).

В Spring AI 2.0 прежнее разрешение обычного `Function`-бина по имени через `.toolNames(...)` удалено: для такого сценария зарегистрируйте явный `ToolCallback`. Порядок advisor-ов определяет, увидит ли memory каждый шаг цикла или только итоговый обмен; проверьте это при настройке истории. [Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html), [ToolCallingAdvisor](https://docs.spring.io/spring-ai/reference/api/tools/tool-calling-advisor.html).

Если у `ChatClient` большой набор инструментов, их описания занимают контекст ещё до выбора нужного. В Spring AI 2.0.2 отдельный starter `spring-ai-starter-tool-search-advisor` и свойство `spring.ai.chat.client.tool-search-advisor.enabled=true` заменяют обычный цикл на `ToolSearchToolCallingAdvisor`: сначала модель видит `toolSearchTool`, затем найденные через `ToolIndex` определения добавляются в следующие итерации. Это способ **выбора доступных инструментов**, а не автоматическая выдача прав: список, зарегистрированный для запроса, и проверки внутри каждого инструмента остаются ответственностью приложения. [Tool Search Tool](https://docs.spring.io/spring-ai/reference/api/tools/tool-search-tool.html), [Spring AI 2.0 GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/).

Индекс привязан к сессии: передавайте ID в advisor context при каждом запросе, по умолчанию через `ChatMemory.CONVERSATION_ID`. Без корректной границы сессии нельзя рассчитывать на изоляцию каталогов инструментов. При малом наборе инструментов обычный `ToolCallingAdvisor` проще: поиск требует дополнительного обращения к модели, а промах индекса может скрыть нужный инструмент. Выберите `RegexToolIndex`, `LuceneToolIndex` или `VectorToolIndex` под названия и описания своих инструментов; проверяйте качество поиска, задержку и затраты на реальных запросах, а не переносите цифры из чужого benchmark. [Tool Search Tool: session and strategies](https://docs.spring.io/spring-ai/reference/api/tools/tool-search-tool.html#session-scoping).

## Q8. Как передать потоковый ответ Spring AI 2.0.2 через SSE?

`ChatClient.prompt()...call().content()` возвращает готовую строку после завершения вызова. `...stream().content()` возвращает `Flux<String>` с текстовыми фрагментами по мере генерации. **Фрагмент не обязан совпадать с одним токеном модели.** Если нужны объекты ответа, включая метаданные, используйте `...stream().chatResponse()` с типом `Flux<ChatResponse>`. Для streaming Spring AI нужен reactive stack даже в приложении с императивным веб-стеком. [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html#streaming-responses), [Implementation Notes](https://docs.spring.io/spring-ai/reference/api/chatclient.html#implementation-notes).

```java
@Service
@RequiredArgsConstructor
public class StreamingChatService {
    private final ChatClient chatClient;

    public Flux<String> streamAnswer(String question) {
        return chatClient.prompt()
            .user(question)
            .stream()
            .content();
    }
}
```

В WebFlux контроллере `text/event-stream` позволяет отдавать элементы `Flux` как SSE по мере поступления; для одних только данных обёртка `ServerSentEvent` не обязательна. Если нужны поля события, например `id` или `event`, используйте `ServerSentEvent<String>`. [Spring WebFlux Return Values](https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/return-types.html).

```java
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final StreamingChatService streamingChatService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String question) {
        return streamingChatService.streamAnswer(question);
    }
}
```

Поток сокращает ожидание **первого фрагмента**, но сам по себе не ускоряет генерацию полного ответа. `ChatClient` не предоставляет `.stream().entity(...)`: для типизированного результата используйте `.call().entity(...)` либо накопите полный поток и явно примените конвертер. Разбор незавершённого JSON как готового POJO ненадёжен. [Structured Output](https://docs.spring.io/spring-ai/reference/api/structured-output.html).

Ошибку до отправки первого события ещё можно вернуть обычным HTTP-ответом. После начала SSE-ответа статус уже может быть зафиксирован: приложению нужно определить, как сообщить клиенту о прерывании, не выдавая частичный текст за завершённый ответ. Логируйте ошибку потока и проверяйте поведение при отключении клиента. [Spring WebFlux Return Values](https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/return-types.html).

**Повтор после частичного ответа.** Клиент уже получил по SSE начало ответа, а поток от модели оборвался. Если просто добавить `retry(1)` к потоку генерации, Reactor повторно подпишется на источник. Если подписка запускает новый запрос к модели, она начнёт новую генерацию. Уже отправленный текст останется у клиента, а следующие фрагменты могут повторить начало или продолжить его несовместимым текстом. Для простого протокола повторяйте подходящие временные ошибки только до первого отправленного фрагмента. После частичной выдачи завершайте попытку как прерванную. Если клиент может начать новую попытку, пусть он отображает её отдельно или явно заменяет предыдущий текст. [Reactor Reference — Retrying](https://projectreactor.io/docs/core/release/reference/coreFeatures/error-handling.html#_retrying).

## Q9. Как работают Advisors в Spring AI 2.0.2?

**Advisors** добавляют сквозную логику к вызову `ChatClient`: память диалога, RAG, логирование или проверку запроса. Они образуют цепочку вокруг обращения к модели; advisor может изменить запрос, обработать ответ или завершить вызов раньше модели. В Spring AI 2.0.2 `CallAdvisor` работает с `.call()`, `StreamAdvisor` — с `.stream()`. Для обоих важны `getName()` и `getOrder()`; меньший order выполняется раньше на входе, порядок регистрации сам по себе не задаёт порядок выполнения. [Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html).

Готовые advisors можно зарегистрировать через `defaultAdvisors` для всех вызовов данного клиента; для одного вызова есть `.advisors(...)`. В сочетании памяти и RAG memory advisor должен получить историю до поискового advisor, чтобы поиск мог учитывать продолжение разговора. При вызове с памятью обязательно передавать `ChatMemory.CONVERSATION_ID` (Q10). [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore,
                              ChatMemory chatMemory) {
    return builder
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(),
            QuestionAnswerAdvisor.builder(vectorStore).build())
        .build();
}
```

В этой конфигурации memory advisor идёт перед `QuestionAnswerAdvisor`; точный порядок определяется `getOrder()`, поэтому при своей конфигурации его нужно проверять. Для `QuestionAnswerAdvisor` нужен модуль `spring-ai-vector-store-advisor` (Q6).

Свой `CallAdvisor` может вернуть ответ без `chain.nextCall(request)`. Ниже политика `ModerationPolicy` считается прикладным сервисом; пример показывает контракт advisor, а не реализацию правил модерации. Для потоковых вызовов нужен также `StreamAdvisor` с `adviseStream(...)`: реализация только `CallAdvisor` не защищает путь `.stream()`.

```java
@Component
@RequiredArgsConstructor
public class ModerationAdvisor implements CallAdvisor {
    private final ModerationPolicy moderationPolicy;

    @Override
    public String getName() {
        return "moderationAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String userInput = request.prompt().getUserMessage().getText();
        if (moderationPolicy.isProhibited(userInput)) {
            return ChatClientResponse.builder()
                .chatResponse(ChatResponse.builder()
                    .generations(List.of(new Generation(
                        new AssistantMessage("I cannot help with that."))))
                    .build())
                .context(request.context())
                .build();
        }
        return chain.nextCall(request);
    }
}
```

## Q10. Как реализовать многоходовой диалог с Chat Memory в Spring AI 2.0.2?

**Модель не хранит историю между вызовами.** В Spring AI 2.0.2 `MessageChatMemoryAdvisor` читает сообщения из `ChatMemory`, добавляет их к запросу и сохраняет новые сообщения после ответа. `MessageWindowChatMemory` решает, какие сообщения оставить, а `ChatMemoryRepository` хранит их. Выберите repository по требованиям к сохранности: `InMemoryChatMemoryRepository` исчезает при рестарте; JDBC и другие постоянные реализации используют внешнее хранилище. [Chat Memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html).

```java
@Bean
public ChatMemory chatMemory(ChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();
}
```

Если другой repository не настроен, Spring AI создаёт `InMemoryChatMemoryRepository`. Для JDBC подключают `spring-ai-starter-model-chat-memory-repository-jdbc` и настраивают datasource и создание схемы. Это сохраняет обычные сообщения после рестарта, но в Spring AI 2.0.2 JDBC repository не сохраняет сообщения вызовов инструментов; выбор постоянного хранилища не означает сохранение полного tool transcript. [JDBC Chat Memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html#jdbcchatmemoryrepository), [Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html#memory-and-the-tool-loop).

```java
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ChatClient chatClient;

    public String chat(String conversationId, String message) {
        return chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .content();
    }
}
```

`ChatMemory.CONVERSATION_ID` передают на **каждом** вызове с memory advisor. В 2.0.2 при отсутствии ключа возникает `IllegalArgumentException`; общего разговора по умолчанию нет. Ключ определяет, какие сообщения читать и записывать, но не проверяет права: приложение должно получить его из серверной пользовательской сессии или проверить принадлежность диалога текущему пользователю. Просто принимать произвольный `conversationId` из запроса опасно. [Chat Memory: conversation IDs](https://docs.spring.io/spring-ai/reference/api/chat-memory.html#working-with-conversation-ids).


**Два запроса к одному диалогу.** В Spring AI 2.0.2 `MessageWindowChatMemory.add()` читает историю, добавляет сообщения и сохраняет всё окно через `saveAll()`. В `InMemoryChatMemoryRepository` этот вызов заменяет список по ключу. Если запросы A и B прочитали одинаковую историю H и записали H+A и H+B по очереди, запись B может вытеснить сообщения A. Потокобезопасная карта защищает отдельные операции. Для последовательных ходов обрабатывайте запросы одного `conversationId` по очереди до сохранения ответа. Если приложение работает в нескольких экземплярах, порядок должен соблюдаться между ними. [MessageWindowChatMemory 2.0.2](https://raw.githubusercontent.com/spring-projects/spring-ai/v2.0.2/spring-ai-model/src/main/java/org/springframework/ai/chat/memory/MessageWindowChatMemory.java) · [InMemoryChatMemoryRepository 2.0.2](https://raw.githubusercontent.com/spring-projects/spring-ai/v2.0.2/spring-ai-model/src/main/java/org/springframework/ai/chat/memory/InMemoryChatMemoryRepository.java).

`maxMessages(N)` ограничивает размер окна; значение по умолчанию — 20. При вытеснении память сохраняет `SystemMessage` и удаляет целые ходы, поэтому фактическое число обычных сообщений может быть меньше N. Окно ограничивает расходы на повторную передачу истории, но не служит долговременным архивом. По умолчанию memory advisor находится снаружи цикла `ToolCallingAdvisor` и сохраняет итоговые user/assistant сообщения, а не промежуточные tool calls. Если перемещать его внутрь цикла ради полного transcript, сначала проверьте поддержку этих типов сообщений выбранным repository. [Message window](https://docs.spring.io/spring-ai/reference/api/chat-memory.html#message-window-chat-memory), [Tool Calling: memory](https://docs.spring.io/spring-ai/reference/api/tools.html#memory-and-the-tool-loop).

В **2.0.2**, если полная история вызовов инструментов нужна между пользовательскими ходами, разместите `MessageChatMemoryAdvisor` **внутри** цикла с `order` больше `ToolCallingAdvisor.DEFAULT_ORDER` (например, `HIGHEST_PRECEDENCE + 400`). Тогда memory видит запросы модели к инструментам и их ответы. Из встроенных `ChatMemoryRepository` этот набор сообщений сохраняют `InMemoryChatMemoryRepository`, `RedisChatMemoryRepository` и `Neo4jChatMemoryRepository`; JDBC его отбрасывает. Поддержка tool messages не равна автоматической авторизации доступа к истории: `conversationId` по-прежнему проверяет приложение. [Tool Calling: memory and backend compatibility](https://docs.spring.io/spring-ai/reference/api/tools.html#memory-and-the-tool-loop), [Chat Memory: JDBC](https://docs.spring.io/spring-ai/reference/api/chat-memory.html#jdbcchatmemoryrepository).

Например, при `ChatClient` с автоматически добавленным `ToolCallingAdvisor` достаточно вложить memory advisor с нужным порядком:

```java
MessageChatMemoryAdvisor memoryInsideLoop = MessageChatMemoryAdvisor.builder(chatMemory)
    .order(BaseAdvisor.HIGHEST_PRECEDENCE + 400)
    .build();
ChatClient client = ChatClient.builder(chatModel)
    .defaultAdvisors(memoryInsideLoop)
    .build();
```

При таком расположении историю внутри `ToolCallingAdvisor` не нужно вести второй раз. `DefaultChatClient` сам отключает её у автоматически добавленного advisor, когда обнаруживает `MemoryAdvisor` внутри цикла; если вы создаёте `ToolCallingAdvisor` вручную, вызовите у builder `.disableInternalConversationHistory()`. Иначе возможна повторная передача одних и тех же сообщений модели. Это настройка передачи истории, а не отключение лимитов вызовов инструментов. [Tool Calling: inside the loop](https://docs.spring.io/spring-ai/reference/api/tools.html#memory-and-the-tool-loop), [ToolCallingAdvisor reference](https://docs.spring.io/spring-ai/reference/api/tools/tool-calling-advisor.html).

В предварительной **Spring AI 2.1.0-RC1** появилась session memory с журналом событий и compaction по границам ходов; `ChatMemory` и связанные реализации там помечены deprecated для удаления в 3.0.0. Для стабильной 2.0.2 приведённый выше `ChatMemory` остаётся действующим API. [Анонс RC1 от 23.10.2026](https://spring.io/blog/2026/10/23/spring-ai-2-1-0-RC1-available-now/).

## Q11. Как тестировать приложение на Spring AI 2.0.2?

**Разделяйте проверку своего кода и проверку качества модели.** В быстрых тестах сервиса подмените `ChatModel`, оставив настоящий `ChatClient`: так можно проверить сборку промпта, передачу параметров и обработку ответа без сетевого вызова. В Spring AI 2.0.2 `ChatModel.call(Prompt)` возвращает `ChatResponse`; ответ содержит `Generation` с `AssistantMessage`. [ChatModel API](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/model/ChatModel.html), [ChatResponse API](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/model/ChatResponse.html).

Например, `ReviewService` из Q3 можно проверить обычным JUnit/Mockito-тестом без загрузки Spring-контекста. Мок возвращает заданный ответ, а захваченный `Prompt` показывает, какое сообщение действительно ушло бы в модель:

```java
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock ChatModel chatModel;

    @Test
    void includesLanguageInUserPrompt() {
        ChatResponse response = new ChatResponse(List.of(
            new Generation(new AssistantMessage("Review complete"))));
        when(chatModel.call(any(Prompt.class))).thenReturn(response);
        ReviewService service = new ReviewService(ChatClient.builder(chatModel).build());

        assertThat(service.reviewCode("class A {}", "Java"))
            .isEqualTo("Review complete");

        ArgumentCaptor<Prompt> prompt = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel).call(prompt.capture());
        assertThat(prompt.getValue().getUserMessage().getText())
            .contains("Java", "class A {}");
    }
}
```

Если тесту нужен Spring-контекст, модель можно заменить `@MockitoBean` из Spring Framework Test; старый Spring Boot `@MockBean` удалён в Boot 4. Для сценариев с несколькими ответами подойдёт собственный стаб `ChatModel` с реализацией `call(Prompt)`, но не стоит писать новый дублёр, если достаточно Mockito. [Spring Boot testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html), [MockitoBean](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html).

Отдельные интеграционные проверки с реальной моделью нужны для качества ответа и RAG: фиксируют набор вопросов, контекст и критерии, контролируют стоимость и допускают вариативность текста. Например, Spring AI предоставляет `RelevancyEvaluator` для проверки связи ответа с найденными документами. Такой тест не заменяется моковым ответом и не должен требовать буквального совпадения произвольной формулировки модели. [Evaluation Testing](https://docs.spring.io/spring-ai/reference/api/testing.html).

## Q12. Как устроить Document ETL для индексирования в Spring AI 2.0.2?

**Document ETL читает источник, преобразует документы и записывает их в `VectorStore`.** В Spring AI 2.0.2 роли разделены между `DocumentReader`, `DocumentTransformer` и `DocumentWriter`; `TokenTextSplitter` — трансформер, а `VectorStore` — writer. `vectorStore.add(chunks)` вычисляет векторы через настроенный `EmbeddingModel` (Q5), поэтому отдельный вызов `embed()` в этом пути не нужен. [ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html), [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html).

Для PDF `PagePdfDocumentReader` извлекает текст через Apache PDFBox и группирует страницы в документы. `PdfDocumentReaderConfig` задаёт параметры извлечения, в том числе число страниц на документ. Затем `TokenTextSplitter` делит длинный текст на части и переносит метаданные исходного документа в чанки. Это важно для последующей фильтрации и указания источника в RAG. `withChunkSize(512)` задаёт целевой размер в токенах; значение по умолчанию — 800. `withMinChunkSizeChars(350)` задаёт нижнюю границу в символах для поиска подходящего знака препинания при разбиении длинного текста, а не размер перекрытия и не обещание, что любой чанк будет длиннее 350 символов. [TokenTextSplitter](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#tokentextsplitter).

**Пример со сканом.** PDF с договором открывается и выглядит читаемым, но каждая страница содержит только изображение. В таком файле обычное извлечение текста может дать пустой результат. Проверьте извлечённый текст по страницам до разбиения. Для страниц без текстового слоя добавьте OCR и сохраните номер страницы вместе с распознанным текстом, чтобы RAG мог сослаться на источник. Если текст получить не удалось, отметьте страницу как необработанную и исключите пустой чанк из индекса. В PDF со встроенным OCR-слоем сначала проверьте, достаточно ли уже извлечённого текста. [Spring AI ETL — PDF Page](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_pdf_page), [Apache PDFBox FAQ](https://pdfbox.apache.org/3.0/faq.html).

```java
@Service
@RequiredArgsConstructor
public class DocumentIndexingService {
    private final VectorStore vectorStore;

    public void indexPdfDocuments(Resource pdfFile) {
        DocumentReader reader = new PagePdfDocumentReader(pdfFile,
            PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                    .withNumberOfBottomTextLinesToDelete(3)
                    .build())
                .build());

        TextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(512)
                .build();

        List<Document> chunks = splitter.apply(reader.get());
        vectorStore.add(chunks);
    }

    public void indexMarkdown(Resource mdFile, Map<String, Object> metadata) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
            .withAdditionalMetadata(metadata)
            .build();
        DocumentReader reader = new MarkdownDocumentReader(mdFile, config);
        TextSplitter splitter = TokenTextSplitter.builder().build();
        vectorStore.add(splitter.apply(reader.get()));
    }
}
```

`MarkdownDocumentReader` требует конфигурацию при передаче `Resource`; `withAdditionalMetadata(metadata)` добавляет метаданные ко всем прочитанным документам без их пересоздания; при совпадении ключей проверьте итоговые значения. Оба reader требуют отдельных модулей `spring-ai-pdf-document-reader` и `spring-ai-markdown-document-reader`. Размер чанка, метаданные, качество извлечения и результаты поиска проверяют на собственном наборе документов; само индексирование не гарантирует релевантный ответ. [MarkdownDocumentReader Javadoc](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/markdown/MarkdownDocumentReader.html), [Markdown config](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/markdown/config/MarkdownDocumentReaderConfig.Builder.html).

`TokenTextSplitter` в 2.0.2 по умолчанию считает токены в кодировке `CL100K_BASE`; через `withEncodingType(...)` можно выбрать, например, `O200K_BASE`. Эти токены не следует без проверки приравнивать к токенам любой выбранной embedding- или chat-модели. `chunkSize` — цель разбиения, а не доказательство, что сумма найденных чанков, инструкций и истории поместится в контекст генерации. Сверяйте фактический размер запросов и качество извлечения с конкретными моделями. [ETL: TokenTextSplitter](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#tokentextsplitter), [RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html).

При повторном индексировании изменённого файла сохраняйте в метаданных каждого чанка идентификатор источника и версию. Например, передайте `Map.of("source_id", sourceId, "version", version)` в показанный выше `indexMarkdown`. Иначе новый `add(chunks)` может оставить в поиске старые чанки рядом с новыми: контракт `VectorStore.add()` не обещает замену всех частей источника и допускает ошибку при дублирующихся ID в зависимости от реализации. В 2.0.2 старую версию можно удалить по сохранённым ID через `delete(List<String>)` или по метаданным через `delete(Filter.Expression)`; поиск и удаление ограничивайте тем же источником и областью доступа. Для обновления без промежутка с пустым индексом запишите новую версию, переключите фильтр поиска на неё и затем удалите старую, но согласованность переключения и поведение хранилища проверяйте отдельно: общий `VectorStore` не обещает атомарной замены набора чанков. [VectorStore API 2.0.2](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/vectorstore/VectorStore.html), [Vector Stores: delete and updates](https://docs.spring.io/spring-ai/reference/api/vectordbs.html#deleting-documents-from-vector-store).

В стабильной **2.0.2** builder `TokenTextSplitter` не содержит настройки overlap. В предварительной **2.1.0-RC1** перекрытие добавлено как опция с нулевым значением по умолчанию; этот API не следует показывать как доступный в 2.0.2. [Анонс RC1 от 23.10.2026](https://spring.io/blog/2026/10/23/spring-ai-2-1-0-RC1-available-now/).

## Q13. Чем Spring AI отличается от прямого использования OpenAI SDK?

**Spring AI 2.0.2 добавляет к вызову провайдера общие интерфейсы и компоненты для Spring-приложения.** Для OpenAI он сам использует официальный `openai-java` SDK. Практический выбор зависит от того, нужен ли приложению общий слой для нескольких моделей, RAG и памяти или удобнее работать напрямую с API OpenAI. [Spring AI OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html), [официальный OpenAI Java SDK](https://github.com/openai/openai-java).

| Задача | Прямой `openai-java` | Spring AI 2.0.2 |
|--------|----------------------|----------------|
| Вызов OpenAI | Типизированный клиент и доступ к API провайдера | `ChatModel` и `ChatClient` поверх того же SDK |
| Повторы запросов | SDK уже умеет retry; число попыток настраивается | Свои настройки retry и OpenAI-клиента; итоговую политику нужно проверить |
| Tools и structured output | SDK поддерживает оба механизма; вызовы своего кода обрабатывает приложение | `@Tool`, `ToolCallingAdvisor` и преобразование ответа через `ChatClient` |
| RAG и память | Можно собрать в приложении и подключить нужное хранилище | Готовые `VectorStore`, advisors и `ChatMemory` с отдельными модулями |
| Spring-интеграция | `OpenAIClient` можно объявить бином самому | Starter автонастраивает модель и `ChatClient.Builder` |

Например, если сервису нужен RAG с PGvector и памятью диалога, Spring AI даёт готовые интерфейсы и advisors (Q5, Q6, Q10). С прямым SDK такой сценарий также возможен, но связывание поиска, истории и вызова модели остаётся кодом приложения. Если нужна специфичная возможность OpenAI, которой ещё нет в Spring AI, или приложение работает только с OpenAI, прямой SDK может быть проще. Официальный `openai-java-spring-boot-starter` рассчитан на Boot 2.7 и больше не поддерживается; в новом Spring-приложении прямой SDK можно подключить обычным бином `OpenAIClient`. Оба пути тестируются: можно подменить `ChatModel` (Q11) либо изолировать собственный код вокруг `OpenAIClient`. Смена провайдера через Spring AI всё равно требует проверки параметров и возможностей моделей (Q1). [RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html), [OpenAI Java SDK: retries и structured outputs](https://github.com/openai/openai-java).

## Q14. Как управлять стоимостью и токенами в Spring AI 2.0.2?

**Сначала измерьте расход по конкретной модели и тарифу, затем ограничивайте ненужный контекст и длину ответа.** Для обычных текстовых вызовов OpenAI входные и выходные токены тарифицируются отдельно; стоимость также зависит от модели, кешированных токенов, режима обслуживания и дополнительных инструментов. Число токенов нельзя надёжно переводить в слова одним коэффициентом. [OpenAI API pricing](https://developers.openai.com/api/docs/pricing).

`ChatResponse` может содержать `Usage`: `getPromptTokens()`, `getCompletionTokens()` и `getTotalTokens()`. В 2.0.2 метода `getGenerationTokens()` нет. Перед учётом проверьте наличие ответа и метаданных; для потокового OpenAI-вызова получение итогового usage зависит от `spring.ai.openai.chat.stream-usage` и завершения потока. [Usage API 2.0.2](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/metadata/Usage.html), [OpenAI Chat properties](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html#configuration-properties).

```java
ChatResponse response = chatClient.prompt().user(question).call().chatResponse();
if (response != null && response.getMetadata() != null) {
    Usage usage = response.getMetadata().getUsage();
    if (usage != null) {
        log.info("Tokens: input={}, output={}, total={}",
            usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }
}
```

Для нерезонирующей `gpt-4o` свойства Spring AI 2.0.2 лежат непосредственно под `spring.ai.openai.chat`, без `options`. У reasoning-моделей вместо `max-tokens` требуется `max-completion-tokens`: этот предел включает и видимые токены ответа, и reasoning-токены. Два параметра нельзя задавать вместе. `temperature` влияет на выбор токенов, но даже ноль не обещает одинаковый текст при каждом вызове. `top-p` — альтернативная настройка выборки; документация советует менять один из этих двух параметров, а не оба сразу. [OpenAI Chat properties](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html#configuration-properties).

```yaml
spring:
  ai:
    openai:
      chat:
        model: gpt-4o
        max-tokens: 1000
        temperature: 0.3
```

`max-tokens` ограничивает число генерируемых токенов для этой модели, но не уменьшает уже переданный вход. Для расходов на вход проверьте объём system-промпта, истории (Q10), найденных RAG-фрагментов (Q6) и описаний инструментов (Q7). Для расходов на выход подберите модель, разумный лимит и число вариантов `n`; итоговые суммы сверяйте с отчётом провайдера, а не только с одним `Usage`.

**Prompt caching переиспользует обработанный префикс запроса, а не готовый ответ.** У Anthropic в Spring AI можно выбрать `AnthropicCacheStrategy` и TTL; нативный `Usage` показывает `cacheCreationInputTokens` и `cacheReadInputTokens`. В общем интерфейсе `Usage` есть `getCacheWriteInputTokens()` и `getCacheReadInputTokens()`, но они могут вернуть `null`, если провайдер не сообщает эти данные. У OpenAI механизм и тариф отличаются: для поддерживаемых моделей кеширование управляется провайдером, а доступные показатели кеша смотрят в его usage. Сохраняйте стабильный префикс, проверяйте фактический hit, срок жизни и цену записи/чтения по выбранному провайдеру. [Spring AI Anthropic caching](https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html#prompt-caching), [OpenAI prompt caching](https://developers.openai.com/api/docs/guides/prompt-caching).

## Q15. Как реализовать AI-агента на Spring AI?

**Агент** — это приложение, в котором модель может последовательно запрашивать инструменты для достижения цели. Модель выбирает следующий вызов и аргументы, но код выполняет приложение через цикл `ToolCallingAdvisor` (Q7); такой цикл можно организовать в стиле **ReAct** («рассуждение → действие → наблюдение»). Один только system-промпт не гарантирует соблюдения бизнес-правил.

System-промпт здесь — это инструкция агенту: какие инструменты есть и как ими пользоваться. Дальше модель сама выбирает, что и в каком порядке звать:

```java
// Простой агент с инструментами
@Service
@RequiredArgsConstructor
public class AgentService {
    private final ChatClient chatClient;
    private final OrderTools orderTools;

    public String runAgent(String task) {
        return chatClient.prompt()
            .system("""
                You help customers find their orders.
                Use searchOrders when a customer ID is provided.
                """)
            .user(task)
            .tools(orderTools)
            .call()
            .content();
    }
}

// ReAct pattern — цикл рассуждений
@Component
@RequiredArgsConstructor
public class OrderTools {
    private final OrderRepository orderRepository;

    @Tool(description = "Search orders by customer ID")
    public OrderSearchResult searchOrders(OrderSearchRequest req) {
        return new OrderSearchResult(orderRepository.findByCustomerId(req.customerId()));
    }
}
```

Некоторые модели могут запросить несколько независимых инструментов за один шаг. Не следует предполагать такое поведение для любого провайдера или строить на порядке вызовов бизнес-инварианты.

**Подводный камень:** инструмент, меняющий состояние (создание тикета, изменение данных), проверяет права, аргументы и допустимость операции на стороне приложения; для повторяемых вызовов нужна идемпотентность, для критичных действий — подтверждение. В 2.0.2 `DefaultToolCallingManager` ограничивает число вызовов за один пользовательский ход: по умолчанию не более 40 на инструмент и 150 суммарно. Это ограничение вызовов инструментов, а не гарантия бюджета токенов или числа обращений к модели. При превышении лимита обработайте результат как незавершённый сценарий; ранее выполненные действия могли уже изменить состояние. [Tool Call Limits](https://docs.spring.io/spring-ai/reference/api/tools.html#tool-call-limits), [Upgrade Notes 2.0.2](https://docs.spring.io/spring-ai/reference/upgrade-notes.html#new-tool-call-limits).

Обычные `.call().content()` и `.stream().content()` отдают вызывающему коду итоговый текст, а не готовый журнал шагов агента; при `returnDirect` итогом может стать результат инструмента. Для отправки промежуточного прогресса в SSE или аудит разместите свой `CallAdvisor`/`StreamAdvisor` **внутри** цикла: его `order` должен быть больше `ToolCallingAdvisor.DEFAULT_ORDER`. Такой advisor видит ответы отдельных итераций, а потоковый вариант — чанки; приложение само решает, какие события безопасно показать клиенту. Когда нужно приостановить выполнение инструмента или управлять каждой итерацией, отключите auto-registration для запроса через `AdvisorParams.toolCallingAdvisorAutoRegister(false)` и самостоятельно обрабатывайте `ChatResponse`, `ToolCallingManager` и историю. Не выключайте цикл лишь ради наблюдения: для этого достаточно вложенного advisor. [ToolCallingAdvisor: Observing the Loop](https://docs.spring.io/spring-ai/reference/api/tools/tool-calling-advisor.html#observing-the-tool-calling-loop), [User-Controlled Tool Execution](https://docs.spring.io/spring-ai/reference/api/tools.html#user-controlled-tool-execution).

В 2.0.2 финальный `Usage` после `ToolCallingAdvisor` суммирует токены всех внутренних вызовов модели. При оценке стоимости агента сверяйте его с данными провайдера и не принимайте за расход только последней итерации. [Upgrade Notes 2.0.2](https://docs.spring.io/spring-ai/reference/upgrade-notes.html#changed-toolcallingadvisor-usage-now-accumulated-across-the-loop).

## Q16. (!) Как Spring AI 2.0 интегрируется с MCP и что проверить по версии протокола?

**Spring AI может быть MCP-клиентом и MCP-сервером.** Клиентский starter подключается к внешним серверам и предоставляет их инструменты как `ToolCallbackProvider`; приложение передаёт этот provider в `ChatClient`, когда хочет открыть инструменты модели. Серверный starter публикует операции и данные приложения через MCP. Это две роли одного протокола, а не два способа вызвать `ChatClient`. См. [руководство Spring AI по MCP](https://docs.spring.io/spring-ai/reference/guides/getting-started-mcp.html).

Для клиента достаточно `spring-ai-starter-mcp-client` и настройки соединения. Например, после настройки `spring.ai.mcp.client.streamable-http.connections` можно передать автонастроенный provider в запрос:

```java
String answer = chatClient.prompt()
    .user("Найди заказ 42")
    .tools(mcpTools) // ToolCallbackProvider из MCP client starter
    .call()
    .content();
```

На сервере `spring-ai-starter-mcp-server` обслуживает локальный `stdio`, а `spring-ai-starter-mcp-server-webmvc` или `-webflux` — сетевой Streamable HTTP. Методы Spring-бинов с `@McpTool`, `@McpResource` и `@McpPrompt` регистрирует сканер MCP-аннотаций. Обычный `@Tool` предназначен для tool calling в Spring AI; если его нужно открыть через MCP, сначала предоставьте соответствующий `ToolCallback` или `ToolCallbackProvider`, который серверный starter умеет преобразовать в MCP tool. Простое наличие `@Tool` на произвольном методе не равно публикации метода сервером. См. [аннотации](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-annotations-overview.html) и [серверный starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html).

```java
@Component
class OrderMcpTools {
    @McpTool(name = "find_order", description = "Find an order by ID")
    String findOrder(@McpToolParam(description = "Order ID", required = true) String id) {
        return "Order " + id; // пример обработчика; доступ к реальным данным проверяйте отдельно
    }
}
```

**Версию MCP проверяйте отдельно от транспорта.** [Анонс Spring AI 2.0 GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/) указывал Java SDK 2.0.0 и соответствие MCP `2025-11-25`. В [анонсе Spring AI 2.1.0-M1](https://spring.io/blog/2026/09/25/spring-ai-2-1-0-M1-available-now/) поддержка редакции `2026-07-28` ещё названа работой в процессе. Поэтому режим `STATELESS` в starter 2.0 или наличие Streamable HTTP сами по себе не доказывают поддержку нового `server/discover` и stateless-жизненного цикла. Перед подключением сверяйте версии Spring AI, Java SDK, клиента и сервера; октябрьский [Java SDK 2.2.0-M1](https://github.com/modelcontextprotocol/java-sdk/releases/tag/v2.2.0-M1) предоставляет предварительный modern server, но пока без modern client. Старый HTTP+SSE в Spring AI 2.0 помечен deprecated; для нового сетевого сервера выбирайте Streamable HTTP и проверяйте совместимость редакций. См. [клиентский](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html) и [серверный](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html) starters.

Сетевой MCP endpoint серверного starter по умолчанию не добавляет аутентификацию и авторизацию. Перед внешним доступом поставьте защиту endpoint и проверку прав внутри операций. В приложении с несколькими пользователями также не считайте общий `McpSyncClient` границей пользователя: клиентский бин и его сессия разделяются между запросами приложения. [Безопасность сервера](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html#securing-the-mcp-server), [границы клиентской сессии](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html#client-scope-and-session-boundaries).

## Q17. Как оценивать RAG-ответы с Evaluator в Spring AI 2.0.2?

**`Evaluator` проверяет выбранное свойство ответа на переданных ему данных.** В Spring AI 2.0.2 `evaluate(EvaluationRequest)` возвращает `EvaluationResponse` с `isPass()`. `EvaluationRequest` содержит исходный вопрос, список `Document` с контекстом и ответ модели. Для RAG важно передать именно те фрагменты, которые попали в запрос генерации, иначе проверяется другой сценарий. [Evaluation Testing](https://docs.spring.io/spring-ai/reference/api/testing.html), [EvaluationRequest Javadoc](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/evaluation/EvaluationRequest.html).

- `RelevancyEvaluator` спрашивает модель-судью, согласуется ли ответ с вопросом и контекстом. Он не измеряет полноту выдачи `VectorStore` и не доказывает внешнюю истинность ответа.
- `FactCheckingEvaluator` проверяет, поддерживает ли переданный контекст утверждение из ответа. Он не проверяет факты без опорного документа; в 2.0.2 создаётся через `FactCheckingEvaluator.builder(judgeBuilder).build()` или `forBespokeMinicheck(judgeBuilder)` для соответствующей модели. [RelevancyEvaluator](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/evaluation/RelevancyEvaluator.html), [FactCheckingEvaluator](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/evaluation/FactCheckingEvaluator.html).

В интеграционном тесте после генерации можно передать вопрос, фактически использованные фрагменты и ответ отдельной модели-судье. `judgeModel` здесь настроен для оценки; `retrievedDocuments` — тот же набор, который получил RAG-вызов, а не результат повторного поиска:

```java
EvaluationRequest request = new EvaluationRequest(
    userQuestion, retrievedDocuments, aiAnswer);
RelevancyEvaluator evaluator =
    new RelevancyEvaluator(ChatClient.builder(judgeModel));
EvaluationResponse result = evaluator.evaluate(request);
assertThat(result.isPass()).isTrue();
```

Тестовый набор хранит вопросы, ожидаемые документы или ответы и случаи отказа при отсутствии подходящего источника. Сначала проверяют retrieval: нашёлся ли нужный документ, не попал ли чужой и актуальна ли его версия. Затем оценивают ответ относительно **того же** контекста. Например, ответ по устаревшей политике может быть согласован с найденным документом и получить `pass`, но оставаться неверным для действующей политики. [RAG reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html), [рекомендации по оценкам](https://developers.openai.com/api/docs/guides/evaluation-best-practices).

Оба готовых evaluator вызывают модель для оценки, поэтому добавляют задержку и стоимость, а их `pass` зависит от судьи, промпта и входных данных. Полезно сверять вердикты с размеченными человеком примерами и повторять проверку при смене модели, индекса или документов. Тест с моковым `ChatModel` из Q11 проверяет сборку запроса; здесь оценивается качество живой цепочки. Положительный вердикт оставляет открытыми проверку источников и риск галлюцинаций. [Исходный код RelevancyEvaluator 2.0.2](https://github.com/spring-projects/spring-ai/blob/v2.0.2/spring-ai-client-chat/src/main/java/org/springframework/ai/chat/evaluation/RelevancyEvaluator.java), [FactCheckingEvaluator 2.0.2](https://github.com/spring-projects/spring-ai/blob/v2.0.2/spring-ai-client-chat/src/main/java/org/springframework/ai/chat/evaluation/FactCheckingEvaluator.java).

## Q18. Какие workflow-паттерны из Building Effective Agents показывает Spring AI 2.0.2?

В [руководстве Spring AI](https://docs.spring.io/spring-ai/reference/api/effective-agents.html) показаны пять способов собрать прикладной процесс из вызовов `ChatClient`. Классы `ChainWorkflow`, `RoutingWorkflow` и другие находятся в [репозитории примеров](https://github.com/spring-projects/spring-ai-examples/tree/main/agentic-patterns): это примеры реализации, а не отдельный API паттернов в Spring AI core. По [разделению Anthropic](https://www.anthropic.com/research/building-effective-agents), **workflow** ведёт модель и инструменты по заданному в коде маршруту, а **агент** сам выбирает дальнейшие действия и инструменты. Подробнее об этом различии — в [AI Agents Q2](../../ai-ml/ai-agents-interview.md).

- **Chain workflow** — фиксированная последовательность: результат одного вызова становится входом следующего. Подходит, когда этапы известны заранее и каждый можно проверить отдельно.
- **Routing** — классификация входа и выбор одной из подготовленных веток. Например, запрос о платеже идёт в обработчик платежей, технический вопрос — в другую ветку; ошибку классификации нужно учитывать отдельно.
- **Parallelization** — независимые задачи запускаются параллельно, затем приложение собирает результаты. Набор задач известен до запуска; нужны правила объединения результатов и ограничения на одновременные вызовы.
- **Orchestrator-workers** — модель-оркестратор определяет подзадачи по конкретному запросу, воркеры выполняют их, приложение собирает ответ. В отличие от parallelization, состав подзадач заранее неизвестен.
- **Evaluator-optimizer** — генератор предлагает результат, оценщик даёт обратную связь, генератор дорабатывает его. Нужны ясный критерий качества и ограничение числа повторов; вызов `Evaluator` из Q17 сам по себе не запускает такой цикл.

Начинайте с простого вызова или фиксированного workflow и усложняйте процесс только под проверенную задачу. Дополнительные вызовы повышают стоимость и задержку, а параллельность сама по себе не улучшает качество. Для операций с побочными эффектами отдельно контролируйте права и повторные вызовы инструментов (Q7/Q15). [Spring AI Building Effective Agents](https://docs.spring.io/spring-ai/reference/api/effective-agents.html), [Anthropic Building effective agents](https://www.anthropic.com/research/building-effective-agents).

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
- [LangChain4j](../../ai-ml/langchain4j-interview.md) — фреймворк-нейтральная альтернатива Spring AI для LLM на JVM (AI Services, RAG, @Tool).
- [MCP](../../ai-ml/mcp-interview.md) — примитивы, транспорты и границы ревизий протокола.
