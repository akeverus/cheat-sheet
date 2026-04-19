---
title: "Go Frameworks"
description: "Точка входа в раздел веб-фреймворков Go: Gin, Echo, Chi, Fiber, Fasthttp, net/http и практики."
tags:
  - meta
  - index
  - go-frameworks
type: "index"
updated: "2026-04-17"
---
# Go Frameworks

Раздел собирает популярные веб-фреймворки и роутеры экосистемы Go: Gin, Echo, Chi, Fiber, Fasthttp и стандартный `net/http`. Основной контент по фреймворкам сейчас живёт в разделе языка Go (`languages/go/go-web-frameworks.md`), эта папка — overview-индекс с редиректом.

Для кого: Go-разработчики, которые выбирают стек для нового сервиса, и backend-инженеры, оценивающие Go как альтернативу JVM/Node.

## Полезные ссылки

### Основные документы
- [Go Frameworks — обзор (редирект)](go-frameworks-overview.md) — короткая карта со ссылкой на основной документ
- [Go Web Frameworks](../../languages/go/go-web-frameworks.md) — полное руководство по Gin/Echo/Chi/Fiber

### Соседние разделы
- [Frameworks](../README.md)
- [Go (язык)](../../languages/go/README.md) — синтаксис, стандартная библиотека, конкурентность
- [Java Frameworks](../java-frameworks/README.md)

### Внешние ресурсы
- [Gin](https://gin-gonic.com/)
- [Echo](https://echo.labstack.com/)
- [Chi](https://go-chi.io/)
- [Fiber](https://gofiber.io/)
- [net/http](https://pkg.go.dev/net/http)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Go](#когда-выбирать-go)
- [Сравнение фреймворков](#сравнение-фреймворков)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Обзор веб-стеков Go и их ниш
- Сравнение Gin/Echo/Chi/Fiber по производительности и эргономике
- Ссылки на глубокий материал в `languages/go/`

## Когда выбирать Go

- Требуется низкий cold start и минимальное потребление памяти (CLI, serverless, edge).
- Нужна высокая пропускная способность HTTP-слоя без реактивного стека.
- Команда готова работать с явной обработкой ошибок и простым tooling (`go build`, `go test`).
- Альтернативы: Quarkus/Micronaut (JVM с AOT), Rust (Actix/Axum), Node.js (Fastify).

## Сравнение фреймворков

| Фреймворк | Ниша | Производительность | Особенности |
|-----------|------|---------------------|-------------|
| `net/http` | стандартная библиотека | база | без внешних зависимостей |
| Gin | REST API | очень высокая | самый популярный, zero-allocation router |
| Echo | REST + middleware | высокая | богатый middleware-набор |
| Chi | идиоматичный `net/http` | высокая | совместим со стандартными `http.Handler` |
| Fiber | Express-like | высочайшая | на базе Fasthttp, не совместим с `net/http` |
| Fasthttp | низкоуровневый HTTP | максимальная | альтернатива стандартному стеку |

## Маршруты чтения

- **Быстрый старт:** `languages/go/go-web-frameworks.md` → раздел Gin.
- **Выбор стека:** таблица выше + раздел "Сравнение" в полном документе.
- **Миграция с Spring:** `go-frameworks-overview.md` + раздел сравнения с JVM.

## Куда идти дальше

- Глубокий материал по Go — [languages/go](../../languages/go/README.md)
- Общие практики backend — [development/web-backend](../../development/web-backend/README.md)
- Контейнеризация Go-сервисов — [platform/containers/docker](../../platform/containers/docker/README.md)
