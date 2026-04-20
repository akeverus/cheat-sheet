---
title: "Utility Libraries"
description: "Точка входа в раздел утилитарных Java-библиотек: Apache Commons и Google Guava."
tags:
  - meta
  - index
  - utility
  - libraries
type: "index"
updated: "2026-04-20"
---
# Utility Libraries

Раздел собирает утилитарные Java-библиотеки, которые расширяют стандартную библиотеку мелкими, но вездесущими хелперами: **Apache Commons** (lang3, collections4, io, csv и др.) и **Google Guava** (immutable-коллекции, `Preconditions`, `Multimap`, `Cache`, `EventBus`, `ListenableFuture`). Эти библиотеки — «швейцарский нож» для работы со строками, коллекциями, I/O, валидациями и кэшами.

Для кого: Java-разработчики, избавляющиеся от велосипедных `StringUtils` и `CollectionUtils`; инженеры, которые хотят понимать, что уже есть в Commons/Guava, прежде чем писать свой хелпер.

## Полезные ссылки

### Основные документы
- [[java-apache-commons|Apache Commons]] — lang3, collections4, io, csv, codec
- [[java-guava|Google Guava]] — коллекции, Preconditions, Cache, Multimap, EventBus

### Соседние разделы
- [[README|Libraries]]
- [[README|Java-библиотеки]]
- [[README|Code generation]] — Lombok заменяет часть boilerplate
- [[README|Java (язык)]]

### Внешние ресурсы
- [Apache Commons](https://commons.apache.org/)
- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
- [Google Guava](https://github.com/google/guava)
- [Guava Wiki](https://github.com/google/guava/wiki)

## Содержание

- [Что внутри](#что-внутри)
- [Commons vs Guava vs Java 21+](#commons-vs-guava-vs-java-21)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- **Apache Commons Lang** — `StringUtils`, `StringBuilder`-хелперы, `Validate`, `ArrayUtils`, `DateUtils`
- **Apache Commons Collections** — `MultiMap`, `Bag`, `Trie`, `CircularFifoQueue`
- **Apache Commons IO** — `FileUtils`, `IOUtils`, `FilenameUtils`
- **Apache Commons CSV / Codec** — CSV-парсер, Base64/MD5/SHA
- **Guava Collections** — `ImmutableList/Map/Set`, `Multimap`, `Multiset`, `BiMap`, `Table`
- **Guava Preconditions** — `checkArgument`, `checkNotNull`, `checkState`
- **Guava Cache** — простой LRU/expiry-кэш
- **Guava EventBus**, `ListenableFuture`, `Optional` (исторический)

## Commons vs Guava vs Java 21+

| Задача | Commons | Guava | Java 21+ stdlib |
|--------|---------|-------|-----------------|
| Проверка на null/пустоту строки | `StringUtils.isBlank` | `Strings.isNullOrEmpty` | `String.isBlank()` (без null-check) |
| Immutable-коллекции | — | `ImmutableList/Map/Set` | `List.of`, `Map.of`, `Set.of` |
| MultiMap | `MultiValuedMap` | `Multimap` | нет |
| Кэш в памяти | нет | `CacheBuilder` | нет (нужен Caffeine) |
| Чтение файла в строку | `IOUtils.toString` | `Files.asCharSource` | `Files.readString` |
| Базовые проверки аргументов | `Validate.notNull` | `Preconditions.checkNotNull` | `Objects.requireNonNull` |

**Правило:** Java 21+ покрыл многое, но Guava/Commons всё ещё уместны для `Multimap`, `Cache`, специфичных CSV/Codec/IO-хелперов и устаревших кодовых баз.

## Маршруты чтения

- **Новый проект на Java 21:** сперва проверь stdlib таблица выше затем конкретный раздел Commons/Guava.
- **Legacy-код:** `java-apache-commons.md` — частая зависимость в старых проектах.
- **Кэширование / EventBus:** `java-guava.md` (секции Cache, EventBus) рассмотреть [Caffeine](https://github.com/ben-manes/caffeine) как замену Guava Cache.

## Куда идти дальше

- Функциональные расширения — [[java-vavr|Vavr]]
- Java-библиотеки — [[README|libraries/java]]
- Язык Java — [[README|languages/java]]
