---
title: "Dropwizard"
description: "Точка входа в раздел Dropwizard: production-ready Java-фреймворк для REST-сервисов с встроенными метриками, health checks и логированием."
tags:
  - meta
  - index
  - dropwizard
  - java-frameworks
type: "index"
updated: "2026-04-17"
---
# Dropwizard

Dropwizard — "opinionated" Java-фреймворк для быстрой сборки RESTful-сервисов из проверенных компонентов: Jetty (HTTP), Jersey (JAX-RS), Jackson (JSON), Metrics, Logback, Hibernate Validator, JDBI. Философия — всё production-ready «из коробки» без магии автоконфигурации.

Для кого: команды, которым нужен узкий, предсказуемый стек без монструозной AutoConfiguration; инженеры, мигрирующие с legacy JAX-RS-приложений; те, кому важна прозрачность зависимостей.

## Полезные ссылки

### Основные документы
- [Dropwizard: Основы](dropwizard-basics.md) — архитектура, конфигурация, REST, health, metrics

### Соседние разделы
- [Java Frameworks](../README.md) — сравнение со Spring/Quarkus/Micronaut/Vert.x/Javalin
- [Spring Boot](../spring/README.md)
- [Javalin](../javalin/README.md)
- [Micronaut](../micronaut/README.md)

### Внешние ресурсы
- [Dropwizard — официальный сайт](https://www.dropwizard.io)
- [Documentation](https://www.dropwizard.io/en/latest/)
- [GitHub](https://github.com/dropwizard/dropwizard)
- [Getting Started](https://www.dropwizard.io/en/latest/getting-started.html)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Dropwizard](#когда-выбирать-dropwizard)
- [Сравнение с соседями](#сравнение-с-соседями)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Структура приложения: `Application`, `Configuration`, `Environment`
- YAML-конфигурация и `@Valid` конфиг-классы
- REST через JAX-RS (`@Path`, `@GET`, `@POST`)
- Health checks, встроенные Metrics
- Logback, CLI commands, Managed objects
- Тесты через `DropwizardAppExtension`

## Когда выбирать Dropwizard

- Нужен «скучный» и предсказуемый стек: никакой автомагии, только явные bundle и factory.
- Команда уже знает JAX-RS (Jersey) и не хочет переучиваться на Spring MVC.
- Важны метрики и health endpoints «из коробки» без Actuator.
- **Не подходит**, если нужен AOT/native (→ Quarkus, Micronaut) или реактивный стек (→ Vert.x, WebFlux).

## Сравнение с соседями

| Критерий | Dropwizard | Spring Boot | Quarkus | Javalin |
|----------|-----------|-------------|---------|---------|
| Cold start | средний (~2 с) | средний (~3-5 с) | быстрый (~1 с JVM, ~20 мс native) | очень быстрый (~0.5 с) |
| RAM | ~150 MB | ~200-300 MB | ~80 MB (native ~30 MB) | ~50 MB |
| Reactive | нет (blocking) | через WebFlux | через Mutiny | частично (Kotlin coroutines) |
| Автоконфиг | нет | много | много | нет |
| Native image | нет | частично | да (первый класс) | экспериментально |

## Маршруты чтения

- **Первое знакомство:** `dropwizard-basics.md` целиком.
- **Мигрант со Spring Boot:** секции "Application / Configuration" + "REST Resource" + "Health checks".
- **Оценка для production:** раздел Metrics + Logging + «Запуск приложения».

## Куда идти дальше

- Сравнение Java-фреймворков — [Java Frameworks](../README.md)
- Метрики и мониторинг — [monitoring](../../../monitoring/README.md)
- REST API — [development/api/rest](../../../development/api/rest/README.md)
