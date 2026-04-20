---
title: "Artillery"
description: "Artillery — инструмент нагрузочного тестирования на Node.js. Сценарии в YAML (или JavaScript), фазы нагрузки (phases), сценарии (scenarios), HTTP/WebSocket, процессор (processor) для кастомной логики, отчёты и Artillery Cloud, интеграция с CI/CD."
tags:
  - testing
  - performance-testing
  - artillery
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Artillery

**Artillery** — инструмент нагрузочного тестирования на Node.js. Сценарии в YAML (или JavaScript), фазы нагрузки (phases), сценарии (scenarios), HTTP/WebSocket, процессор (processor) для кастомной логики, отчёты и Artillery Cloud, интеграция с CI/CD.

**Дата:** 2026-02-06

## Полезные ссылки

| Ресурс | URL |
|--------|-----|
| Документация | [artillery.io/docs](https://www.artillery.io/docs) |
| Основные концепции | [Core Concepts](https://www.artillery.io/docs/get-started/core-concepts) |
| Справка по сценариям | [Test Script Reference](https://www.artillery.io/docs/reference/test-script) |
| CLI | [CLI Reference](https://www.artillery.io/docs/reference/cli/run) |

**См. также:** [[jmeter]], [[gatling]], [[k6]], [[testing-tools-overview|Обзор инструментов тестирования]].



### См. также
- [[cucumber|Cucumber для Java]]
## Введение

### Зачем Artillery

- **YAML** — низкий порог входа, сценарии читаемые и версионируемые.
- **Node.js** — один стек с фронтом и бэкендом, не нужна JVM.
- **Фазы (phases)** — гибкая настройка нагрузки: разогрев, постоянная нагрузка, пики.
- **Процессор (processor)** — кастомная логика на JavaScript: извлечение токена, генерация данных.
- **CI/CD** — запуск через npm/npx, ненулевой код при превышении порогов (ensure).

### Основные концепции

| Концепция | Описание |
|-----------|----------|
| **config** | Конфигурация теста: target (базовый URL), phases, processor, payload, ensure. |
| **phases** | Фазы нагрузки: duration (сек), arrivalRate (пользователей/сек) или rampTo (выход на N виртуальных пользователей). |
| **scenarios** | Сценарии: список шагов (get, post, think и т.д.), выполняемых виртуальными пользователями. |
| **processor** | Файл с функциями на JavaScript (beforeScenario, afterResponse и др.). |
| **payload** | Данные из CSV/JSON для параметризации (логины, тестовые данные). |


## Установка и настройка

**Требования:** Node.js 14+ (рекомендуется 18 или 20).

```bash
# Глобально
npm install -g artillery

# Локально в проект
npm install --save-dev artillery

# Проверка
artillery version
```

**Запуск:**

```bash
artillery run config.yml
artillery run config.yml --target https://staging.example.com
artillery run config.yml --output report.json
```


## Структура теста (YAML)

### config, target, phases

- **target** — базовый URL; в сценариях пути указываются относительно него.
- **phases** — список фаз: duration (сек), arrivalRate (новых VU/сек) или rampTo (линейный выход на N VU за duration), name, pause.

Пример:

```yaml
config:
  target: "https://example.com"
  phases:
    - duration: 60
      arrivalRate: 10
      name: "Разогрев"
    - duration: 120
      rampTo: 50
      name: "Пиковая нагрузка"
  processor: "./processor.js"
  payload:
    path: "./users.csv"
    fields: ["username", "password"]
    order: "sequence"
```

**order** в payload: `sequence` (по порядку) или `random` (случайный выбор).

### scenarios

Сценарии — список шагов, выполняемых последовательно для каждого виртуального пользователя.

| Шаг | Назначение |
|-----|------------|
| get, post, put, delete, patch | HTTP-запрос с url, опционально headers, json, capture. |
| think | Пауза в секундах (think time). |
| send | WebSocket: отправить сообщение. |
| function | Вызов функции из processor. |

Пример:

```yaml
scenarios:
  - name: "Главная и API"
    flow:
      - get:
          url: "/"
      - think: 2
      - get:
          url: "/api/status"
```


## HTTP-запросы

- **headers** — заголовки (например, `Authorization: "Bearer {{ token }}"`).
- **json** — тело запроса; подстановка переменных `{{ variable }}`.
- **capture** — извлечь значение из ответа (JSONPath или regex) в переменную.

Пример: логин и запрос с токеном:

```yaml
- post:
    url: "/api/login"
    json:
      username: "{{ username }}"
      password: "{{ password }}"
    capture:
      - json: "$.access_token"
        as: token
- get:
    url: "/api/me"
    headers:
      Authorization: "Bearer {{ token }}"
```

Переменные `username`, `password` — из payload (CSV). `token` — из capture предыдущего запроса.


## Processor и payload

В файле processor (путь в `config.processor`) экспортируются функции:

| Функция | Когда вызывается |
|---------|------------------|
| beforeScenario(context, userContext, done) | Перед каждым сценарием (подготовка данных в userContext.vars). |
| afterResponse(context, done) | После каждого ответа (проверка, извлечение данных). |
| beforeRequest(context, userContext, events, done) | Перед каждым запросом (модификация запроса). |
| functionName(...) | Кастомная функция; вызывается шагом `function: "functionName"` в сценарии. |

Переменные из `userContext.vars` доступны в сценарии как `{{ varName }}`.

Пример проверки статуса в afterResponse:

```javascript
module.exports = {
  afterResponse: (context, done) => {
    if (context.response.statusCode !== 200) {
      return done(new Error(`Неожиданный статус: ${context.response.statusCode}`));
    }
    return done();
  },
};
```


## Отчёты и метрики

- **Консоль** — сводка по умолчанию: длительность, запросов/сек, время отклика (min, max, median, p95, p99), коды ответов, ошибки.
- **JSON** — `--output report.json` для анализа и дашбордов.
- **Artillery Cloud** — `artillery run config.yml --record` (нужна регистрация и токен).

### ensure (пороги для CI)

В config можно задать пороги: при нарушении Artillery завершается с ненулевым кодом.

```yaml
config:
  ensure:
    maxErrorRate: 0.01   # макс. доля ошибок (1%)
    p95: 500            # 95-й перцентиль времени отклика не более 500 мс
```


## WebSocket

Поддержка через шаг `send` и конфигурацию `ws` в config. В сценарии указывается `engine: ws`:

```yaml
config:
  target: "wss://example.com"
  phases:
    - duration: 60
      arrivalRate: 5
scenarios:
  - engine: ws
    flow:
      - send: "{\"type\":\"ping\"}"
      - think: 1
```


## CI/CD

- Запуск: `npx artillery run config.yml --output report.json`.
- Переопределение target: `--target $TARGET_URL` (например, из переменной окружения в GitHub Actions).
- Переменные окружения доступны в processor через `process.env`.


## Лучшие практики

1. **Фазы с разогревом** — не стартовать с высокого arrivalRate без ramp-up.
2. **think** — паузы между шагами (например, `think: [1, 3]` — случайная 1–3 сек).
3. **capture** — извлекать токены и данные из ответа для следующих запросов.
4. **payload** — параметризация через CSV/JSON, не хардкод в YAML.
5. **processor** — сложная логика: проверки, генерация данных, условные запросы.
6. **ensure** — задать maxErrorRate и p95 для падения теста при деградации в CI.
7. **Версионировать Artillery** в package.json для воспроизводимости.


## Решение проблем и FAQ

### Таблица типичных проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Тест падает по ensure | Порог не достигнут (p95, ошибки) | Ослабить ensure или оптимизировать систему |
| connection refused | Неверный target или сервер недоступен | Проверить target и доступность сервера |
| Переменная не подставляется | Нет в userContext.vars или опечатка | Проверить payload и capture, имя в `{{ varName }}` |
| payload file not found | Путь относительно текущей директории | Запускать из каталога с config или указать полный путь |
| processor not found | Неверный путь к processor.js | Путь относительно config или абсолютный |
| Много ошибок | Таймауты, перегрузка | Увеличить config.http.timeout, снизить arrivalRate |
| artillery не найден | Не в PATH | Использовать `npx artillery run config.yml` |

### Краткие ответы

- **Artillery или k6?** — Artillery: YAML + Node.js, низкий порог входа. k6: JavaScript, встроенные thresholds, интеграция с Grafana. Выбор по предпочтениям команды и отчётам.
- **API с авторизацией?** — Запрос логина capture извлечь access_token в переменную token в следующих запросах заголовок `Authorization: "Bearer {{ token }}"`.
- **Тело запроса из файла?** — В processor (beforeRequest или beforeScenario) прочитать файл и подставить в context.request.body или в userContext.vars.
- **gRPC?** — Стандартно нет; есть плагины (artillery-engine-grpc) или вызов gRPC-клиента из processor. Для нативной поддержки часто выбирают k6 или Gatling.
- **Ограничить длительность?** — Суммарная duration фаз задаёт длительность; можно фазу с rampTo: 0 или pause для завершения.


## Глоссарий и команды

### Термины

| Термин | Описание |
|--------|----------|
| config | Конфигурация теста (target, phases, processor, payload, ensure) |
| phases | Фазы нагрузки (duration, arrivalRate или rampTo) |
| scenarios | Сценарии пользователя (шаги: get, post, think и т.д.) |
| arrivalRate | Число новых виртуальных пользователей в секунду |
| rampTo | Линейный выход на заданное число VU за duration |
| processor | Файл с функциями на JavaScript |
| payload | Данные из CSV/JSON для параметризации |
| capture | Извлечение значения из ответа (JSONPath/regex) в переменную |
| ensure | Пороги pass/fail; ненулевой код при нарушении |
| think | Пауза в секундах между шагами |

### CLI

| Команда | Описание |
|---------|----------|
| artillery run config.yml | Запустить тест |
| artillery run config.yml --target URL | Переопределить target |
| artillery run config.yml --output report.json | Отчёт в JSON |
| artillery run config.yml --record | Метрики в Artillery Cloud |
| artillery version | Версия |
| artillery quick | Интерактивное создание минимального config |


## Заключение

Artillery — удобный инструмент нагрузочного тестирования на Node.js с сценариями в YAML. Фазы (phases) и сценарии (scenarios) задают нагрузку; processor и payload дают параметризацию и кастомную логику. Используйте capture и ensure для авторизации и CI. Подробнее: [документация Artillery](https://www.artillery.io/docs) и смежные шпаргалки (JMeter, Gatling, k6).

*Дата: 2026-02-06*
