---
title: "Serialization Libraries"
description: "Точка входа в раздел сериализации JVM: Jackson (JSON/XML/YAML) и Gson (JSON)."
tags:
  - meta
  - index
  - serialization
  - libraries
type: "index"
updated: "2026-04-20"
---
# Serialization Libraries

Раздел собирает библиотеки сериализации для JVM: **Jackson** (де-факто стандарт для JSON/XML/YAML/CBOR в Spring Boot и многих других стеках) и **Gson** (простой Google-овский JSON-маппер, популярный в Android). Оба решают одну задачу, но отличаются конфигурируемостью, экосистемой модулей и производительностью.

Для кого: Java/Kotlin-разработчики, выбирающие сериализатор; инженеры, оптимизирующие hot-path (JSON в REST) и решающие проблемы с immutable-типами, полиморфизмом, датами.

## Полезные ссылки

### Основные документы
- [[jackson]] — ObjectMapper, аннотации, модули, kotlin-module, интеграция со Spring
- [[java-gson|Gson (Java)]] — type adapters, `@SerializedName`, streaming API

### Соседние разделы
- [[README|Libraries]]
- [[README|Java-библиотеки]]
- [[README|Kotlin-библиотеки]] — `kotlinx.serialization` как альтернатива
- [[README|API]]

### Внешние ресурсы
- [Jackson Project](https://github.com/FasterXML/jackson)
- [Gson](https://github.com/google/gson)
- [JSON Spec (RFC 8259)](https://tools.ietf.org/html/rfc8259)

## Содержание

- [Что внутри](#что-внутри)
- [Jackson vs Gson vs kotlinx.serialization](#jackson-vs-gson-vs-kotlinxserialization)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Базовая сериализация/десериализация POJO и коллекций
- Аннотации: `@JsonProperty`, `@JsonIgnore`, `@JsonCreator`, `@SerializedName`
- Модули: jackson-datatype-jsr310 (java.time), jackson-module-kotlin, jackson-dataformat-xml/yaml/cbor
- Обработка полиморфизма, generics, immutable (Records)
- Streaming API (Jackson `JsonParser`, Gson `JsonReader`) для больших JSON

## Jackson vs Gson vs kotlinx.serialization

| Критерий | Jackson | Gson | kotlinx.serialization |
|----------|---------|------|----------------------|
| Производительность | высокая | средняя | высокая (no reflection) |
| Конфигурируемость | максимальная (модули, миксины, сериализаторы) | минимальная | средняя |
| Immutable (Records, data class) | хорошо | хуже (нужен type adapter) | нативно |
| Формат | JSON, XML, YAML, CBOR, Smile | только JSON | JSON, CBOR, ProtoBuf |
| Интеграция со Spring | из коробки | через конвертер | через `kotlinx-serialization-jackson` адаптер |
| Android | ok | популярно | хорошо (KMP) |

## Маршруты чтения

- **Spring Boot + JSON (дефолт):** `jackson.md` целиком, начиная с "ObjectMapper" и «Аннотации».
- **Android-проект:** `java-gson.md` + секция "Type Adapters".
- **Kotlin-multiplatform:** сравнить с [[kotlin-kotlinx-serialization|kotlinx.serialization]].
- **Оптимизация hot-path:** секции "Streaming API" и "Performance" в `jackson.md`.

## Куда идти дальше

- REST-контракты — [[README|development/api/rest]]
- Kotlin-сериализация — [[kotlin-kotlinx-serialization|libraries/kotlin/kotlin-kotlinx-serialization.md]]
- Protobuf/gRPC — [[java-protobuf|libraries/java/java-protobuf.md]]
