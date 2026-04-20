---
title: "Insomnia: Основы"
description: "Практическое руководство по Insomnia для тестирования REST, GraphQL и gRPC API: коллекции, окружения, переменные, плагины, code generation, работа в команде и CLI."
tags:
  - development
  - api
  - insomnia
  - api-testing
difficulty: "beginner"
updated: "2026-04-17"
---
# Insomnia: Основы

Insomnia — open-source REST/GraphQL/gRPC клиент от Kong. Простой UI, легковесный (в отличие от Electron-тяжёлого Postman), поддержка OpenAPI, WebSocket, SSE, GraphQL schema introspection. Проект существует с 2014 года, форк Insomnium — полностью offline-версия.

Для большинства разработчиков Insomnia — повседневный инструмент исследования и отладки API: быстро собрать запрос, переключить окружение (dev/staging/prod), проверить реакцию сервиса, сохранить коллекцию в git для команды. Сравнение с Postman — ниже.

## Полезные ссылки

### Официальная документация
- [Insomnia Documentation](https://docs.insomnia.rest/)
- [Insomnia CLI (inso)](https://docs.insomnia.rest/inso-cli/introduction)
- [Plugins](https://insomnia.rest/plugins)

### Соседние разделы
- [[README|Postman]] — альтернатива
- [[README|API Testing]]
- [[README|REST API]]
- [[README|GraphQL]]
- [[README|gRPC]]
- [[README|OpenAPI / Swagger]]

### Полезные ресурсы
- [Insomnia vs Postman](https://konghq.com/blog/product-releases/insomnia-vs-postman) — сравнение от Kong
- [Insomnium (offline fork)](https://github.com/ArchGPT/insomnium)

## Содержание

- [Установка](#установка)
- [Основные концепции](#основные-концепции)
- [Быстрый старт: REST](#быстрый-старт-rest)
- [Окружения и переменные](#окружения-и-переменные)
- [Template tags](#template-tags)
- [GraphQL](#graphql)
- [gRPC](#grpc)
- [WebSocket и SSE](#websocket-и-sse)
- [Импорт OpenAPI](#импорт-openapi)
- [Тесты и chaining](#тесты-и-chaining)
- [Совместная работа и git](#совместная-работа-и-git)
- [CLI (inso)](#cli-inso)
- [Insomnia vs Postman](#insomnia-vs-postman)
- [Best practices](#best-practices)

## Установка

```bash
# macOS
brew install --cask insomnia

# Linux (deb)
curl -fsSL https://insomnia.rest/keys/debian-public.key.asc \
  | sudo tee /etc/apt/trusted.gpg.d/insomnia.asc
echo "deb [arch=amd64] https://updates.insomnia.rest/downloads/ubuntu/latest stable main" \
  | sudo tee /etc/apt/sources.list.d/insomnia.list
sudo apt update && sudo apt install insomnia

# Windows — chocolatey
choco install insomnia-rest-api-client
```

Для offline использования — [Insomnium](https://github.com/ArchGPT/insomnium) (форк без обязательной авторизации).

## Основные концепции

| Термин | Объяснение |
|--------|-----------|
| **Workspace** (Collection) | проект, содержит requests, environments, plugins |
| **Request** | один HTTP/GraphQL/gRPC запрос |
| **Folder** | логическая группировка requests |
| **Environment** | набор переменных (dev, staging, prod) |
| **Base Environment** | общие переменные, наследуются всеми sub-environments |
| **Template Tag** | динамическая подстановка: UUID, timestamp, запрос, JS-snippet |
| **Plugin** | расширение (auth, transformers, helpers) |

## Быстрый старт: REST

1. `Ctrl+N` новый request.
2. Выбрать метод (GET/POST/PUT/DELETE/PATCH), вписать URL.
3. Вкладки: **Body** (JSON, form-data, file, GraphQL), **Auth** (Basic, Bearer, OAuth 2.0, AWS IAM, NTLM), **Query**, **Headers**, **Docs**.
4. `Send` ответ в правой панели: body (JSON viewer с поиском), headers, cookies, timeline (actual raw request/response).

```text
# URL с параметрами
{{ base_url }}/api/users?page={{ page }}&limit=20

# Body: raw JSON
{
  "name": "Alice",
  "email": "alice@example.com"
}
```

Preview форматов: JSON (подсветка + fold), HTML, XML, image, PDF, raw.

## Окружения и переменные

**Base Environment:**
```json
{
  "base_url": "https://api.example.com",
  "api_version": "v1"
}
```

**Dev (наследует base):**
```json
{
  "base_url": "https://dev.api.example.com",
  "token": "dev-token-xyz"
}
```

Переключение: dropdown в левом верхнем углу. Переменные доступны в URL, headers, body, auth.

**Private environments** — для токенов; не экспортируются при sharing.

## Template tags

Динамические значения в `{% %}` или `{{ }}`:

| Tag | Назначение |
|-----|-----------|
| `{% uuid %}` | UUID v4 |
| `{% now 'iso-8601' %}` | timestamp |
| `{% base64 'encode', 'text' %}` | кодирование |
| `{% hash 'sha256', 'text' %}` | хэш |
| `{% response 'body', '<request_id>', '$.access_token' %}` | JSONPath из ответа другого запроса (chaining) |
| `{% request 'url' %}` | URL текущего запроса |
| `{% jwt 'payload', '<secret>' %}` | JWT из плагина |
| `{% prompt 'Enter value' %}` | интерактивный ввод |

**Пример chaining** — получить token из /auth, использовать в следующих запросах:

```text
Authorization: Bearer {% response 'body', 'req_auth_login', '$.accessToken', 'never' %}
```

## GraphQL

1. Body type **GraphQL Query**.
2. Вставьте query; справа — поле для variables (JSON).
3. Schema introspection: кнопка **Schema** Refresh. Автокомплит и подсветка.

```graphql
query GetUser($id: ID!) {
  user(id: $id) {
    id
    name
    posts { title }
  }
}
```

Variables:
```json
{ "id": "{{ user_id }}" }
```

## gRPC

1. Создать **gRPC Request**.
2. Указать `.proto` файл (или protobuf-reflection endpoint).
3. Выбрать service и method появится Body JSON.
4. Unary / Server streaming / Client streaming / Bidi — Insomnia поддерживает все.

## WebSocket и SSE

- **WebSocket Request**: подключение по `wss://`, отправка/приём сообщений, кастомные headers.
- **SSE (Server-Sent Events)** как обычный GET с `Accept: text/event-stream` — Insomnia показывает поток событий live.

## Импорт OpenAPI

`Import/Export` выбрать файл `openapi.yaml`/`.json` Insomnia создаст collection с requests для каждого endpoint, использует servers как environment. Поддерживаются OpenAPI 3.0/3.1, Swagger 2.0, Insomnia v4 (JSON), Postman v2.1, HAR, cURL.

Для design-first подхода есть **Design mode**: редактор OpenAPI-спецификации с preview и валидацией.

## Тесты и chaining

Вкладка **Tests** у request — JS-код, исполняется после ответа:

```javascript
const response = await insomnia.send();

expect(response.status).to.equal(200);
expect(response.data.id).to.exist;

// Сохранить в переменную для следующих запросов
insomnia.setEnvironmentVariable('user_id', response.data.id);
```

Для полноценного CI — **inso run tests** (см. CLI).

## Совместная работа и git

**Варианты sharing:**

1. **Insomnia Cloud Sync** — встроенная синхронизация через учётку Insomnia (бесплатно до 1 workspace; pro — больше).
2. **Git Sync** — Workspace привязан к git-репо, коммитит JSON-файлы. Публичная/приватная git-репа, SSH или token.
3. **Export / Import** — ручной файл `insomnia.yaml`, храните в git.

Рекомендация: Git Sync с приватной репой, токены — в private environment (не попадают в git).

## CLI (inso)

```bash
# Запуск test suite
inso run test --env dev my-workspace

# Генерация кода из спеки
inso generate config api-spec.yaml --type kubernetes

# Линт OpenAPI
inso lint spec api-spec.yaml
```

Отлично встраивается в CI: перед деплоем API прогоняется пакет smoke-тестов.

## Insomnia vs Postman

| Критерий | Insomnia | Postman |
|----------|----------|---------|
| License | open-source (MIT) | proprietary |
| Offline | форк Insomnium, в main — требует login | Scratch Pad до sign-in, после — cloud |
| UI | минималистичный | перегружен (monitors, mocks, flows) |
| Git Sync | native | ограниченный (Postman API + code) |
| OpenAPI Design | встроенный редактор | |
| gRPC | | |
| Mock servers | через плагин | native |
| Collection Runner | inso CLI | Newman |
| Монетизация | Pro $5/мес (team sync) | Free $19-49/мес |
| Плагины | JS, hot-reload | JS sandbox, pre/post-scripts |
| Memory footprint | ~200 MB | ~500 MB |

**Выбор:** Insomnia — для тех, кто ценит простоту и git-workflow. Postman — если нужны встроенные mock-серверы, monitors, больше командных возможностей.

## Best practices

- **Имена запросов:** `METHOD /path description` `POST /users create user`. Проще искать.
- **Folders** по доменам: `auth/`, `users/`, `orders/`.
- **Base URL в env** — не хардкодьте.
- **Chaining через `response` tag** вместо копипасты token.
- **Private env для секретов** — не попадают в export.
- **Git Sync** для команды — PR для изменений коллекции.
- **Документация в Docs tab** request — видна всем членам команды.
- **Коллекции под OpenAPI** — источник истины должен быть spec, коллекция генерируется.
- **Plugin `insomnia-plugin-faker`** для генерации данных.
- **Не коммитьте sync-файлы без review** — легко запушить токен.
