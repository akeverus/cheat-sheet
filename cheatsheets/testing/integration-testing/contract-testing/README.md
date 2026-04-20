---
title: "Contract Testing"
description: "Точка входа в раздел контрактного тестирования: совместимость consumer-provider без поднятия всего ландшафта."
tags:
  - meta
  - index
  - testing
  - contract-testing
type: "index"
updated: "2026-04-20"
---
# Contract Testing

Контрактное тестирование проверяет совместимость между потребителем (consumer) и провайдером (provider) API по формальному контракту — без совместного поднятия всех сервисов. Контракт публикуется в брокере (Pact Broker) и используется обеими сторонами в CI.

Применяйте, когда архитектура распилена на несколько независимо деплоящихся сервисов и классические end-to-end тесты становятся дорогими и хрупкими. Контракт сдвигает обнаружение несовместимости влево: поломка API ловится в пайплайне провайдера ещё до деплоя.

## Полезные ссылки

### Основные документы
- [Contract Testing](contract-testing.md) — Pact, Spring Cloud Contract, CI/CD

### Соседние разделы
- [Integration Testing](../../../basics/README.md)
- [Testcontainers](../../../basics/README.md)
- [wiremock](../wiremock.md)
- [REST Assured](../rest-assured.md)
- [JUnit](../../../basics/README.md)

### Внешние ресурсы
- [Pact](https://docs.pact.io/)
- [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract)
- [Martin Fowler: Contract Testing](https://martinfowler.com/bliki/ContractTest.html)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Зачем контрактное тестирование, consumer-driven vs provider-driven | [contract-testing](contract-testing.md) |
| Pact (JVM): генерация pact-файла, верификация провайдером | [contract-testing](contract-testing.md) |
| Spring Cloud Contract: Groovy DSL, stub runner | [contract-testing](contract-testing.md) |
| Pact Broker, can-i-deploy, CI/CD интеграция | [contract-testing](contract-testing.md) |

## Когда использовать

- **Contract Testing vs E2E** — контракт дешевле и быстрее, но проверяет только совместимость, а не сквозные сценарии.
- **Contract Testing vs WireMock** — WireMock это ручной стаб; контракт синхронизирован между обеими сторонами через брокер.
- **Pact vs Spring Cloud Contract** — Pact кросс-языковой (JS, Go, Java, .NET), SCC привязан к экосистеме Spring/JVM, но удобнее для чистого Java-стека.

## Маршруты чтения

- **Быстрый старт (30 мин):** introduction в `contract-testing.md` пример Pact consumer Pact provider.
- **Внедрение в команде (2 ч):** весь документ + Pact Broker + can-i-deploy.

## Куда идти дальше

- Интеграционное тестирование в целом — [README](../../../basics/README.md)
- Мок внешних HTTP-сервисов — [wiremock](../wiremock.md)
- REST-клиентские проверки — [rest-assured](../rest-assured.md)
