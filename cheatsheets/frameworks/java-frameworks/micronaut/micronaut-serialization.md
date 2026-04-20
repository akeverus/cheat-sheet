---
title: "Micronaut: Serialization - JSON, XML и Custom Serializers"
description: "Полное руководство по сериализации в Micronaut: JSON, XML, custom serializers, Jackson, Gson и best practices"
tags:
  - micronaut
  - serialization
  - json
  - xml
  - jackson
  - gson
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-kafka.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-kafka.md"]
---

# Micronaut: Serialization - JSON, XML и Custom Serializers

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Serialization — JSON, XML и Custom Serializers](#micronaut-serialization-json-xml-и-custom-serializers)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Serialization](#настройка-serialization)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [JSON Serialization](#json-serialization)
  - [Basic JSON](#basic-json)
  - [Custom JSON Serializer](#custom-json-serializer)
- [XML Serialization](#xml-serialization)
  - [XML Annotations](#xml-annotations)
- [Custom Serializers](#custom-serializers)
  - [Custom Serializer Registration](#custom-serializer-registration)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте аннотации для контроля сериализации](#1-используйте-аннотации-для-контроля-сериализации)
  - [2. Игнорируйте чувствительные данные](#2-игнорируйте-чувствительные-данные)
  - [3. Используйте custom serializers для сложных объектов](#3-используйте-custom-serializers-для-сложных-объектов)
- [JSON Views](#json-views)
  - [View-based Serialization](#view-based-serialization)
  - [Using Views](#using-views)
- [Date/Time Serialization](#datetime-serialization)
  - [Custom Date Format](#custom-date-format)
- [Custom Deserializers](#custom-deserializers)
  - [Custom Deserializer](#custom-deserializer)
- [Polymorphic Serialization](#polymorphic-serialization)
  - [Polymorphic Types](#polymorphic-types)
- [Serialization Modules](#serialization-modules)
  - [Custom Module](#custom-module)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет отличную поддержку сериализации через различные библиотеки. Это позволяет эффективно сериализовать и десериализовать объекты в **JSON**, **XML** и другие форматы.

### Основные возможности

- **JSON Serialization**: Сериализация в **JSON**
- **XML Serialization**: Сериализация в **XML**
- **Custom Serializers**: Пользовательские сериализаторы
- **Jackson Integration**: Интеграция с **Jackson**
- **Gson Support**: Поддержка **Gson**

## Настройка Serialization

### Зависимости

**build.gradle:**

```gradle
dependencies {
    // Jackson
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    // или Gson
    implementation("io.micronaut.serde:micronaut-serde-gson")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  serialization:
    json:
      enabled: true
    xml:
      enabled: true
```

## JSON Serialization

### Basic JSON

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    @JsonProperty("user_id")
    private Long id;

    private String name;

    @JsonIgnore
    private String password;

    // Getters and setters
}
```

### Custom JSON Serializer

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", user.getId());
        gen.writeStringField("name", user.getName());
        gen.writeStringField("email", user.getEmail());
        gen.writeEndObject();
    }
}
```

## XML Serialization

### XML Annotations

```java
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JacksonXmlRootElement(localName = "user")
public class User {

    @JacksonXmlProperty(isAttribute = true)
    private Long id;

    @JacksonXmlProperty(localName = "user_name")
    private String name;

    // Getters and setters
}
```

## Custom Serializers

### Custom Serializer Registration

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.inject.Singleton;

@Singleton
public class CustomSerializerModule {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(User.class, new UserSerializer());
        mapper.registerModule(module);
        return mapper;
    }
}
```

## Лучшие практики

### 1. Используйте аннотации для контроля сериализации

```java
// ✅ Хорошо
@JsonProperty("user_id")
private Long id;
```

### 2. Игнорируйте чувствительные данные

```java
// ✅ Хорошо
@JsonIgnore
private String password;
```

### 3. Используйте custom serializers для сложных объектов

```java
// ✅ Хорошо
public class CustomSerializer extends JsonSerializer<MyClass> {
    // Кастомная логика
}
```

## JSON Views

### View-based Serialization

```java
import com.fasterxml.jackson.annotation.JsonView;

public class User {
    public interface Public {}
    public interface Internal extends Public {}

    @JsonView(Public.class)
    private Long id;

    @JsonView(Public.class)
    private String name;

    @JsonView(Internal.class)
    private String email;

    @JsonView(Internal.class)
    private String password;

    // Getters and setters
}
```

### Using Views

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;

@Controller("/api/users")
public class UserController {
    private final ObjectMapper objectMapper;

    @Get("/{id}")
    public String getUser(Long id) {
        User user = userService.findById(id);
        return objectMapper.writerWithView(User.Public.class)
            .writeValueAsString(user);
    }
}
```

## Date/Time Serialization

### Custom Date Format

```java
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class User {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // Getters and setters
}
```

## Custom Deserializers

### Custom Deserializer

```java
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class UserDeserializer extends JsonDeserializer<User> {

    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        User user = new User();
        user.setId(node.get("id").asLong());
        user.setName(node.get("name").asText());
        user.setEmail(node.get("email").asText());
        return user;
    }
}
```

## Polymorphic Serialization

### Polymorphic Types

```java
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Dog.class, name = "dog"),
    @JsonSubTypes.Type(value = Cat.class, name = "cat")
})
public abstract class Animal {
    // ...
}
```

## Serialization Modules

### Custom Module

```java
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.inject.Singleton;

@Singleton
public class CustomSerializationModule extends SimpleModule {

    public CustomSerializationModule() {
        addSerializer(User.class, new UserSerializer());
        addDeserializer(User.class, new UserDeserializer());
    }
}
```


## Заключение

**Micronaut Serialization** предоставляет мощные инструменты для сериализации данных. Поддержка **JSON**, **XML**, **custom serializers**, **Jackson**, **Gson**, **JSON views**, **date**/**time serialization**, **custom deserializers**, **polymorphic serialization**, **modules** и других продвинутых возможностей позволяет эффективно работать с различными форматами данных.

## Дополнительные ресурсы

- [**Micronaut Serialization** Documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs)
- [Gson Documentation](https://github.com/google/gson/blob/master/UserGuide.md)
- [Jackson Annotations](https://github.com/FasterXML/jackson-annotations/wiki)
- [Jackson Modules](https://github.com/FasterXML/jackson-modules-base)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
