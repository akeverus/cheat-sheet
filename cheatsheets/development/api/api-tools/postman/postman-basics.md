---
title: "Postman: Основы"
description: "Комплексное руководство по использованию Postman для тестирования и разработки API."
tags:
  - development
  - api
  - postman-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Postman: Основы

**Комплексное руководство по использованию `Postman` для тестирования и разработки `API`.**

## Полезные ссылки

### Официальная документация
- [Postman Documentation](https://learning.postman.com/docs/getting-started/introduction/) — официальная документация **Postman**

### См. также
- [[api-testing-basics|API Testing]] — основы тестирования API
- [[insomnia-basics|Insomnia]] — клиент **Insomnia**

- [[grpc|gRPC]]
- [[graphql|GraphQL для Java]]
- [[rest-api-best-practices|REST API Best Practices]]
## Содержание

- [Введение в Postman](#введение-в-postman)
- [HTTP-методы](#http-методы)
  - [GET](#get)
  - [POST](#post)
  - [PUT](#put)
  - [PATCH](#patch)
  - [DELETE](#delete)
- [Заголовки и авторизация](#заголовки-и-авторизация)
  - [Типы авторизации (вкладка Authorization)](#типы-авторизации-вкладка-authorization)
- [Environments и Variables](#environments-и-variables)
  - [Scopes переменных (от большего к меньшему приоритету)](#scopes-переменных-от-большего-к-меньшему-приоритету)
  - [Синтаксис {{variable}}](#синтаксис-variable)
  - [Создание переменных окружения](#создание-переменных-окружения)
  - [Управление переменными в скриптах](#управление-переменными-в-скриптах)
  - [Динамические переменные (встроенные)](#динамические-переменные-встроенные)
- [Коллекции](#коллекции)
  - [Организация запросов](#организация-запросов)
  - [Экспорт и импорт](#экспорт-и-импорт)
  - [Collection Runner](#collection-runner)
  - [Sharing](#sharing)
- [Pre-request Scripts](#pre-request-scripts)
  - [Типичные задачи](#типичные-задачи)
- [Tests (автоматические проверки)](#tests-автоматические-проверки)
  - [Проверка статус-кода](#проверка-статус-кода)
  - [Проверка тела ответа](#проверка-тела-ответа)
  - [Проверка заголовков](#проверка-заголовков)
  - [Проверка времени ответа](#проверка-времени-ответа)
  - [Извлечение данных из ответа и передача следующему запросу](#извлечение-данных-из-ответа-и-передача-следующему-запросу)
  - [Схема JSON (Structure Validation)](#схема-json-structure-validation)
  - [Условный пропуск следующего запроса](#условный-пропуск-следующего-запроса)
- [Newman — CLI-запуск](#newman-cli-запуск)
  - [Установка](#установка)
  - [Базовый запуск](#базовый-запуск)
  - [Параметры запуска](#параметры-запуска)
  - [HTML-отчёт](#html-отчёт)
  - [Запуск из URL (Postman Public API)](#запуск-из-url-postman-public-api)
  - [Интеграция в CI (GitHub Actions / GitLab CI)](#интеграция-в-ci-github-actions-gitlab-ci)
- [Mock Servers](#mock-servers)

## Введение в Postman

**Postman** — инструмент для разработки, тестирования и документирования **API**.

Основные возможности:

- Создание и выполнение **HTTP**-запросов (GET, POST, PUT, PATCH, DELETE)
- Организация запросов в **коллекции** и **папки**
- Управление **окружениями** и **переменными** (dev / staging / prod)
- Написание автотестов на **JavaScript** (`pm.*` API)
- Запуск коллекций через **Newman** (CLI) в **CI/CD**
- Создание **Mock-серверов** для разработки без бэкенда

---

## HTTP-методы

### GET

Получение ресурса. Тело запроса не передаётся.

```http
GET https://api.example.com/users?page=1&limit=10
Authorization: Bearer {{access_token}}
Accept: application/json
```

Параметры задаются во вкладке **Params** — Postman автоматически добавляет их к URL.

---

### POST

Создание нового ресурса. Тело — JSON в разделе **Body → raw → JSON**.

```http
POST https://api.example.com/users
Content-Type: application/json
Authorization: Bearer {{access_token}}

{
  "name": "Ivan Petrov",
  "email": "ivan@example.com",
  "role": "admin"
}
```

Для отправки файлов используйте **Body → form-data** с типом `File`.

---

### PUT

Полная замена ресурса. Передаётся полный объект.

```http
PUT https://api.example.com/users/42
Content-Type: application/json
Authorization: Bearer {{access_token}}

{
  "name": "Ivan Petrov",
  "email": "ivan@example.com",
  "role": "user"
}
```

---

### PATCH

Частичное обновление — только изменяемые поля.

```http
PATCH https://api.example.com/users/42
Content-Type: application/json
Authorization: Bearer {{access_token}}

{
  "role": "moderator"
}
```

---

### DELETE

Удаление ресурса. Тело обычно не нужно.

```http
DELETE https://api.example.com/users/42
Authorization: Bearer {{access_token}}
```

Если API требует подтверждение в теле:

```http
DELETE https://api.example.com/orders/99
Content-Type: application/json

{
  "reason": "customer_request"
}
```

---

## Заголовки и авторизация

Заголовки задаются во вкладке **Headers**.

Часто используемые заголовки:

| Заголовок | Пример значения |
|-----------|----------------|
| `Content-Type` | `application/json` |
| `Accept` | `application/json` |
| `Authorization` | `Bearer {{access_token}}` |
| `X-Request-ID` | `{{$guid}}` |
| `X-Api-Key` | `{{api_key}}` |

### Типы авторизации (вкладка Authorization)

- **No Auth** — без авторизации
- **Bearer Token** — вставляет `Authorization: Bearer <token>`, значение берётся из поля или переменной
- **Basic Auth** — логин + пароль, Postman кодирует в Base64 автоматически
- **API Key** — ключ в заголовке или query-параметре
- **OAuth 2.0** — настройка grant flow, получение и обновление токена
- **Digest Auth** — для серверов с Digest-схемой

Авторизацию можно задать на уровне **коллекции** — все запросы внутри наследуют её автоматически (`Inherit auth from parent`).

---

## Environments и Variables

### Scopes переменных (от большего к меньшему приоритету)

1. **Local** — временные переменные, живут только в рамках запроса / скрипта
2. **Data** — переменные из CSV/JSON при запуске через Collection Runner
3. **Environment** — переменные текущего окружения (dev, staging, prod)
4. **Collection** — переменные коллекции, доступны всем запросам внутри
5. **Global** — переменные рабочего пространства, видны везде

При конфликте имён побеждает более высокий приоритет (Local > Data > Environment > Collection > Global).

### Синтаксис `{{variable}}`

Используется в URL, заголовках, теле запроса, скриптах.

```http
GET {{base_url}}/api/v1/products/{{product_id}}
Authorization: Bearer {{access_token}}
```

### Создание переменных окружения

1. Gear icon → **Manage Environments** → **Add**
2. Задать имя окружения (например, `dev`)
3. Добавить переменные: `base_url = https://dev.api.example.com`
4. Выбрать окружение в выпадающем меню (правый верхний угол)

### Управление переменными в скриптах

```javascript
// Чтение переменной окружения
const token = pm.environment.get("access_token");

// Запись переменной окружения
pm.environment.set("access_token", "new_token_value");

// Удаление переменной окружения
pm.environment.unset("access_token");

// Глобальные переменные
pm.globals.set("session_id", "abc123");
pm.globals.get("session_id");

// Локальные переменные (только для текущего скрипта)
pm.variables.set("temp_id", 42);
pm.variables.get("temp_id");

// Collection-переменные
pm.collectionVariables.set("retry_count", 0);
```

### Динамические переменные (встроенные)

Postman предоставляет генераторы данных через синтаксис `{{$...}}`:

| Переменная | Результат |
|------------|-----------|
| `{{$guid}}` | UUID v4 |
| `{{$timestamp}}` | Unix timestamp |
| `{{$randomInt}}` | Случайное целое |
| `{{$randomEmail}}` | Случайный email |
| `{{$randomFullName}}` | Случайное имя |
| `{{$isoTimestamp}}` | ISO 8601 дата |

---

## Коллекции

### Организация запросов

- **Collection** — верхний уровень, объединяет запросы по проекту или API
- **Folder** — группировка запросов внутри коллекции (по функциональности, версии, ресурсу)
- **Request** — отдельный HTTP-запрос

Рекомендованная структура:

```text
Users API
├── Auth
│   ├── POST Login
│   └── POST Refresh Token
├── Users
│   ├── GET List Users
│   ├── GET User by ID
│   ├── POST Create User
│   ├── PUT Update User
│   └── DELETE User
└── Admin
    └── GET Stats
```

### Экспорт и импорт

- **Экспорт**: правый клик на коллекции → **Export** → выбрать формат v2.1 (JSON)
- **Импорт**: **File → Import** → перетащить файл или вставить URL

### Collection Runner

Запускает все запросы коллекции последовательно — для smoke-тестов и регрессии.

Настройки:
- **Iterations** — количество повторений
- **Delay** — задержка между запросами (мс)
- **Data File** — CSV или JSON с тестовыми данными для параметризации
- **Keep variable values** — сохранять переменные между итерациями

Запуск: правый клик на коллекции → **Run collection**.

### Sharing

- **Workspaces** — личные, командные, публичные
- Поделиться коллекцией: **Share** → скопировать ссылку или добавить участника
- **Publish Docs** — публичная документация с примерами прямо из коллекции

---

## Pre-request Scripts

Выполняются **до** отправки запроса. Вкладка **Pre-request Script** на уровне запроса, папки или коллекции.

### Типичные задачи

**Установить токен перед запросом:**

```javascript
// Если токен истёк — получить новый
const token = pm.environment.get("access_token");
const expiry = pm.environment.get("token_expiry");

if (!token || Date.now() > expiry) {
    pm.sendRequest({
        url: pm.environment.get("base_url") + "/auth/token",
        method: "POST",
        header: { "Content-Type": "application/json" },
        body: {
            mode: "raw",
            raw: JSON.stringify({
                client_id: pm.environment.get("client_id"),
                client_secret: pm.environment.get("client_secret")
            })
        }
    }, (err, response) => {
        const json = response.json();
        pm.environment.set("access_token", json.access_token);
        pm.environment.set("token_expiry", Date.now() + json.expires_in * 1000);
    });
}
```

**Сгенерировать подпись запроса (HMAC):**

```javascript
const timestamp = Math.floor(Date.now() / 1000).toString();
const secret = pm.environment.get("api_secret");
const signature = CryptoJS.HmacSHA256(timestamp, secret).toString();

pm.environment.set("request_timestamp", timestamp);
pm.environment.set("request_signature", signature);
```

**Установить случайные данные для запроса:**

```javascript
pm.environment.set("random_email", `user_${pm.variables.replaceIn("{{$randomInt}}")}@test.com`);
pm.environment.set("request_id", pm.variables.replaceIn("{{$guid}}"));
```

---

## Tests (автоматические проверки)

Выполняются **после** получения ответа. Вкладка **Tests**. Используют `pm.test()` и `pm.expect()` (Chai BDD).

### Проверка статус-кода

```javascript
pm.test("Status is 200", () => {
    pm.response.to.have.status(200);
});

pm.test("Status is 2xx", () => {
    pm.expect(pm.response.code).to.be.within(200, 299);
});

pm.test("Not found returns 404", () => {
    pm.response.to.have.status(404);
});
```

### Проверка тела ответа

```javascript
pm.test("Response has users array", () => {
    const body = pm.response.json();
    pm.expect(body).to.have.property("users");
    pm.expect(body.users).to.be.an("array");
    pm.expect(body.users.length).to.be.above(0);
});

pm.test("User has required fields", () => {
    const user = pm.response.json().users[0];
    pm.expect(user).to.have.all.keys("id", "name", "email");
});

pm.test("Email is valid format", () => {
    const { email } = pm.response.json();
    pm.expect(email).to.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/);
});
```

### Проверка заголовков

```javascript
pm.test("Content-Type is JSON", () => {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});

pm.test("Response has correlation ID", () => {
    pm.expect(pm.response.headers.get("X-Correlation-ID")).to.not.be.undefined;
});
```

### Проверка времени ответа

```javascript
pm.test("Response under 500ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

### Извлечение данных из ответа и передача следующему запросу

```javascript
// Сохранить ID созданного ресурса
const body = pm.response.json();
pm.environment.set("created_user_id", body.data.id);

// Сохранить токен из заголовка
const token = pm.response.headers.get("X-Auth-Token");
pm.environment.set("auth_token", token);

// Сохранить значение из Set-Cookie
const cookie = pm.cookies.get("session_id");
pm.environment.set("session_id", cookie);
```

### Схема JSON (Structure Validation)

```javascript
const schema = {
    type: "object",
    required: ["id", "name", "email"],
    properties: {
        id: { type: "number" },
        name: { type: "string" },
        email: { type: "string", format: "email" }
    }
};

pm.test("Response matches schema", () => {
    pm.response.to.have.jsonSchema(schema);
});
```

### Условный пропуск следующего запроса

```javascript
// Перейти к определённому запросу в коллекции
if (pm.response.json().hasMore === false) {
    postman.setNextRequest(null); // остановить выполнение
}
postman.setNextRequest("GET Refresh Token"); // прыгнуть к запросу по имени
```

---

## Newman — CLI-запуск

**Newman** — официальный CLI для запуска коллекций Postman. Используется в CI/CD.

### Установка

```bash
npm install -g newman

# Дополнительные репортеры
npm install -g newman-reporter-htmlextra
```

### Базовый запуск

```bash
# Запуск коллекции с окружением
newman run collection.json -e env.json

# Несколько репортеров
newman run collection.json -e env.json --reporters cli,json

# Запись отчёта в файл
newman run collection.json -e env.json \
  --reporters cli,json \
  --reporter-json-export results/report.json
```

### Параметры запуска

```bash
newman run collection.json \
  -e env.json \                          # файл окружения
  -d data.csv \                          # файл с тестовыми данными (CSV или JSON)
  -n 5 \                                 # количество итераций
  --delay-request 200 \                  # задержка между запросами (мс)
  --timeout-request 10000 \              # таймаут запроса (мс)
  --bail \                               # остановить при первой ошибке
  --color on \                           # цветной вывод
  --suppress-exit-code                   # не проваливать CI при ошибках тестов
```

### HTML-отчёт

```bash
newman run collection.json -e env.json \
  --reporters htmlextra \
  --reporter-htmlextra-export report.html \
  --reporter-htmlextra-title "API Test Report"
```

### Запуск из URL (Postman Public API)

```bash
newman run "https://api.getpostman.com/collections/{{collection_id}}?apikey={{postman_api_key}}" \
  --environment "https://api.getpostman.com/environments/{{env_id}}?apikey={{postman_api_key}}"
```

### Интеграция в CI (GitHub Actions / GitLab CI)

```yaml
# .github/workflows/api-tests.yml
- name: Run API Tests
  run: |
    npm install -g newman newman-reporter-htmlextra
    newman run postman/collection.json \
      -e postman/env-staging.json \
      --reporters cli,htmlextra \
      --reporter-htmlextra-export report.html
```

---

## Mock Servers

**Mock Server** позволяет разрабатывать и тестировать без реального бэкенда.

Принцип работы:

- Для каждого запроса в коллекции создаётся **пример ответа** (вкладка **Examples**)
- Mock Server возвращает этот пример при обращении к нему
- URL мок-сервера выдаётся Postman-ом и выглядит как `https://<id>.mock.pstmn.io`

Создание:

1. Правый клик на коллекции → **Mock collection**
2. Задать название и окружение
3. Скопировать URL мок-сервера, использовать как `base_url`

Возможности:

- Разные примеры для разных статус-кодов (200, 404, 500) — мок выбирает по заголовку `x-mock-response-code`
- Динамические моки через **Postman Flows** или скриптами
- Приватные моки (только для команды) и публичные

Ограничения бесплатного плана: 1000 вызовов / месяц на мок-сервер.

---
