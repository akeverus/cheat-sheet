---
title: "Telegram"
description: "Telegram: Bot API, MTProto, webhooks vs long polling, Inline-боты, Mini Apps, клиентские библиотеки и паттерны для notifications/ChatOps."
tags:
  - meta
  - index
  - telegram
  - collaboration
  - bots
type: "index"
updated: "2026-04-17"
---
# Telegram

Telegram — мессенджер с двумя API: **Bot API** (HTTPS-обёртка, простой старт) и **MTProto** (полный клиентский протокол). Для инженерных задач в 95% хватает Bot API: notifications, ChatOps, inline-боты, группы. Для сложного (user-боты, парсинг истории, большие файлы >50 MB) — MTProto через Telethon/TDLib.

В отличие от Slack/Mattermost не корпоративный инструмент, но отлично подходит для: алертинга в личку, ботов для стартапов, DevRel-каналов, OTP-доставки, Mini Apps (в бета).

## Полезные ссылки

### Основной документ
- [Основы Telegram Bot API](telegram-basics.md) — полное руководство по ботам

### Соседние разделы
- [Slack](../slack/README.md)
- [Mattermost](../mattermost/README.md)
- [Alerting](../../../monitoring/alerting/README.md)

### Внешние ресурсы
- [Bot API](https://core.telegram.org/bots/api)
- [BotFather](https://t.me/BotFather) — создание ботов
- [python-telegram-bot](https://python-telegram-bot.org/)
- [telegraf.js](https://telegraf.js.org/)
- [TDLib](https://core.telegram.org/tdlib) — официальная C++ библиотека (MTProto)
- [Bot API Changelog](https://core.telegram.org/bots/api-changelog)

## Содержание

- [Bot API vs MTProto](#bot-api-vs-mtproto)
- [Быстрый старт: notifications](#быстрый-старт-notifications)
- [Webhook vs Long Polling](#webhook-vs-long-polling)
- [Типовые паттерны](#типовые-паттерны)
- [Best practices и лимиты](#best-practices-и-лимиты)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Bot API vs MTProto

| Характеристика | Bot API | MTProto |
|----------------|---------|---------|
| Транспорт | HTTPS | custom binary over TCP/WebSocket |
| Аутентификация | token от BotFather | phone + auth_key |
| Типы аккаунта | только боты | любой user |
| Порог входа | минуты | дни |
| Лимит файлов | 50 MB upload, 20 MB download | 2 GB |
| Лимит сообщений | 30 msg/sec в группу, 20 msg/min в канал | выше, динамические |
| Чтение истории | только полученное | вся история |
| Use cases | notifications, ChatOps, игры | массовые рассылки, клиенты |

**95% задач:** Bot API.

## Быстрый старт: notifications

1. `@BotFather` → `/newbot` → получите `TOKEN`.
2. Добавьте бота в канал/группу, сделайте админом.
3. Узнайте `chat_id`:

```bash
curl -s "https://api.telegram.org/bot<TOKEN>/getUpdates" | jq
# После того как кто-то написал боту — ответ содержит "chat":{"id":...}
```

4. Отправьте сообщение:

```bash
curl -X POST "https://api.telegram.org/bot<TOKEN>/sendMessage" \
  -d chat_id=<CHAT_ID> \
  -d text="🚨 Alert: CPU > 90% on prod-api-03" \
  -d parse_mode=HTML
```

С форматированием (Markdown/HTML):

```bash
curl -X POST "https://api.telegram.org/bot<TOKEN>/sendMessage" \
  -d chat_id=<CHAT_ID> \
  -d parse_mode=MarkdownV2 \
  --data-urlencode 'text=*Deploy*: `v1\.2\.3` 🟢'
```

## Webhook vs Long Polling

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| **Webhook** | push, низкая latency, без постоянных запросов | нужен HTTPS-endpoint с валидным сертификатом |
| **Long polling** | работает из-за NAT, простой старт | постоянные запросы, выше latency |

```bash
# Установить webhook
curl -X POST "https://api.telegram.org/bot<TOKEN>/setWebhook" \
  -d url="https://my-app.com/telegram/webhook" \
  -d secret_token="random-high-entropy-string"

# Снять webhook (перейти на polling)
curl "https://api.telegram.org/bot<TOKEN>/deleteWebhook"
```

Telegram шлёт POST с `Update`; в header `X-Telegram-Bot-Api-Secret-Token` проверяйте `secret_token`.

## Типовые паттерны

### 1. Alerting-бот

- Prometheus/Grafana → webhook → Telegram bot → канал `@team-alerts`.
- [alertmanager-telegram-bot](https://github.com/metalmatze/alertmanager-bot).

### 2. Inline-кнопки

```json
{
  "chat_id": 123,
  "text": "Approve deploy v1.2.3?",
  "reply_markup": {
    "inline_keyboard": [[
      {"text": "✅ Approve", "callback_data": "deploy:v1.2.3:approve"},
      {"text": "❌ Reject",  "callback_data": "deploy:v1.2.3:reject"}
    ]]
  }
}
```

Бот получает `callback_query` при клике. `answerCallbackQuery` — чтобы убрать loading-спиннер.

### 3. Команды (commands)

У BotFather → `/setcommands` → список. Пример: `/status`, `/restart`, `/logs`.

### 4. Mini Apps (Web Apps)

HTML-страница, которая открывается внутри Telegram — через кнопку `web_app`. Возможности: доступ к `Telegram.WebApp` JS-API, haptics, theme, main button. Примеры: настройки бота, формы, карточки оплаты.

### 5. Payment API

Встроенные платежи (Stripe, ЮKassa, другие провайдеры). Поддерживает invoice, shipping, tipping.

## Best practices и лимиты

- **Rate limits:** 30 msg/sec в один чат, 20 msg/min в канал, ~30 разных чатов/сек на бота. Для bulk — очередь с retry.
- **Error 429:** в ответе есть `retry_after` — ждите.
- **Secret token** для webhook — защита от подделки.
- **Команды в группах:** по умолчанию бот в non-privacy mode получает только команды `/cmd`. Включите privacy mode для чтения всех сообщений только если реально нужно.
- **Не храните chat_id в коде** — динамические, храните в БД с привязкой к пользователю/команде.
- **Локализация:** `language_code` в `User` — используйте для i18n.
- **parse_mode:** при `MarkdownV2` экранируйте `_*[]()~` и т.д. При `HTML` — экранируйте `<>&`.
- **Идемпотентность:** update может прийти повторно — храните `update_id`, игнорируйте дубли.
- **Chat types:** `private`, `group`, `supergroup`, `channel`. Супергруппа vs группа важно: функциональные различия.
- **Безопасность:** Bot API не содержит E2E — сообщения видны Telegram. Для критичного — Secret Chats (только MTProto, user-user).

## Маршруты чтения

- **Новичок:** [telegram-basics.md](telegram-basics.md) → @BotFather → первый sendMessage.
- **DevOps:** alerting-бот + callback buttons + ChatOps.
- **Продуктовик:** Mini Apps, Payment API, inline-режим.

## Куда идти дальше

- [Alerting](../../../monitoring/alerting/README.md) — routing в Telegram
- [Slack](../slack/README.md) — сравнение для выбора платформы
- [API / REST](../../../development/api/rest/README.md) — принципы HTTP API
