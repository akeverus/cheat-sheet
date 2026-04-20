---
title: "Testcontainers"
description: "Точка входа в раздел Testcontainers: запуск реальных Docker-контейнеров в интеграционных тестах JVM."
tags:
  - meta
  - index
  - testing
  - testcontainers
type: "index"
updated: "2026-04-17"
---
# Testcontainers

Testcontainers — библиотека для JVM, которая поднимает реальные Docker-контейнеры (БД, брокеры, веб-серверы, произвольные образы) на время теста и гарантирует их остановку после. Даёт изоляцию, повторяемость и совместимость с CI/CD без ручной оркестрации.

Применяйте, когда интеграционный тест должен работать с той же СУБД/брокером, что и прод, и когда embedded-заменители (H2, MockK, embedded Kafka) либо не поддерживают нужную функциональность, либо дают ложно-зелёные тесты. Testcontainers заменяет `docker-compose up` перед тестами и убирает проблемы «забытых» контейнеров.

## Полезные ссылки

### Основные документы
- [[testcontainers]] — модули, JUnit 5, Spring Boot, CI/CD

### Соседние разделы
- [[README|Integration Testing]]
- [[README|Database Testing]]
- [[README|Contract Testing]]
- [[wiremock]]
- [[README|JUnit]]
- [[README|Docker]]

### Внешние ресурсы
- [Testcontainers Docs](https://www.testcontainers.org/)
- [testcontainers-java GitHub](https://github.com/testcontainers/testcontainers-java)
- [Modules](https://www.testcontainers.org/modules/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Установка, базовое использование `GenericContainer` | [[testcontainers]] |
| Модули БД: `PostgreSQLContainer`, `MySQLContainer`, `MongoDBContainer` | [[testcontainers]] |
| Очереди: `KafkaContainer`, `RabbitMQContainer` | [[testcontainers]] |
| Docker Compose, сеть, ожидание готовности (wait strategies) | [[testcontainers]] |
| Reuse контейнеров, Spring Boot `@ServiceConnection`, CI/CD | [[testcontainers]] |

## Когда использовать

- **Testcontainers vs embedded H2/Kafka** — embedded быстрее, но скрывает баги специфики СУБД и брокера; Testcontainers ближе к проду.
- **Testcontainers vs docker-compose** — compose для локальной разработки и «долгоживущих» стендов; Testcontainers — для эфемерной инфраструктуры внутри тестового процесса с автоочисткой.
- **Testcontainers vs stage-окружение** — stage нужен для E2E и smoke; Testcontainers покрывает интеграцию между слоями приложения и инфраструктурой.

## Маршруты чтения

- **Быстрый старт (30 мин):** введение установка `PostgreSQLContainer` Spring Boot интеграция.
- **Production-setup (2 ч):** весь документ + модули под ваш стек + reuse + CI/CD.

## Куда идти дальше

- Тестирование БД — [[README]]
- Контракты API — [[README]]
- Docker — [[README]]
