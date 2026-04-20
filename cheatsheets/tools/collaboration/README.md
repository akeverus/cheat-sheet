---
title: "Инструменты коллаборации"
description: "Шпаргалки по командным инструментам: Jira (трекер), Confluence (wiki), Slack/Mattermost (чат), Telegram (боты и интеграции)."
tags:
  - meta
  - index
  - collaboration
type: "index"
updated: "2026-04-20"
---
# Инструменты коллаборации

Раздел охватывает инструменты командной работы: трекинг задач, wiki, мессенджеры и боты. Эти системы — операционная «нервная система» команды: в них принимаются решения, хранится контекст, идут согласования. Понимание их API и автоматизаций даёт возможность встраивать CI/CD-события, on-call алертинг, ChatOps.

Для бэкенд-инженеров: основной фокус — как интегрировать приложение с этими системами (webhooks, REST API, incoming/outgoing bots). Для тимлидов и DevOps — best practices использования и частые антипаттерны.

## Полезные ссылки

### Подразделы

| Инструмент | Категория | Основной документ |
|-----------|-----------|-------------------|
| [Jira](../../basics/README.md) | issue tracker | [jira-basics.md](jira/jira-basics.md) |
| [Confluence](../../basics/README.md) | wiki / docs | [confluence-basics.md](confluence/confluence-basics.md) |
| [Slack](../../basics/README.md) | чат, ChatOps | [slack-basics.md](slack/slack-basics.md) |
| [Mattermost](../../basics/README.md) | self-hosted чат | [mattermost-basics.md](mattermost/mattermost-basics.md) |
| [Telegram](../../basics/README.md) | мессенджер, боты | [telegram-basics.md](telegram/telegram-basics.md) |

### Соседние разделы
- [CI/CD](../../basics/README.md) — интеграция с трекерами и чатами
- [Alerting](../../basics/README.md) — вывод алертов в чаты
- [AI-инструменты](../ai/) — AI-ассистенты в коммуникации

### Внешние ресурсы
- [Atlassian REST APIs](https://developer.atlassian.com/cloud/)
- [Slack API](https://api.slack.com/)
- [Telegram Bot API](https://core.telegram.org/bots/api)

## Содержание

- [Карта «задача инструмент»](#карта-задача-инструмент)
- [Сравнение чат-платформ](#сравнение-чат-платформ)
- [Интеграционные паттерны](#интеграционные-паттерны)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта «задача инструмент»

| Задача | Инструмент |
|--------|-----------|
| Трекинг задач, спринтов, багов | Jira |
| Техническая документация, ADR, runbooks | Confluence |
| Командный чат, каналы, ChatOps | Slack / Mattermost |
| 1-to-1, быстрые уведомления, бот для алертов | Telegram |
| Видео-созвоны | Zoom / Google Meet (не покрывается здесь) |
| Код-ревью | GitLab / GitHub (см. platform/ci-cd/) |

## Сравнение чат-платформ

| Критерий | Slack | Mattermost | Telegram |
|----------|-------|-----------|----------|
| Хостинг | SaaS | self-hosted / SaaS | SaaS |
| Корпоративный фокус | | | (личный, но работает) |
| On-premise | Enterprise Grid | native | |
| Бот API | Slack Bolt, Events API | REST + Webhooks | Bot API + MTProto |
| Нити / треды | | | частично |
| Интеграции | 2000+ marketplace | 70+ native + webhooks | через бота |
| Шифрование E2E | | (в pro) | Secret Chats |
| Цена | per-user, платно выше 10 чел. | free self-hosted | бесплатно |
| Типичное применение | корпоративные команды | компании с compliance | стартапы, DevRel, боты |

## Интеграционные паттерны

- **CI/CD чат**: pipeline finished/failed webhook в канал команды. Incoming webhook в Slack/Mattermost, Bot API в Telegram.
- **Alerting чат**: Alertmanager/PagerDuty канал on-call. Включай кнопки acknowledge/resolve.
- **Чат Jira**: slash-команды (`/jira create`) или интеграции для создания тикета из сообщения.
- **Bot для рутин**: daily standup, ротация on-call, poll.
- **ChatOps**: операции над инфраструктурой через команды в чате (deploy, rollback, restart), с RBAC и audit-log.

## Маршруты чтения

- **Новичок в команде:** `Jira` (как заводить/двигать тикеты) `Confluence` (как искать доки) `Slack/Mattermost` (правила общения).
- **Инженер-интегратор:** API каждого инструмента, webhooks, custom bots.
- **DevOps / SRE:** ChatOps, Alertmanager Slack/Mattermost, on-call rotation bot.

## Куда идти дальше

- Интеграция с AI-ассистентами в чате — [tools/ai/](../ai/)
- Автоматизация pipeline чат — [platform/ci-cd/](../../basics/README.md)
- Мониторинг и алертинг — [monitoring/alerting/](../../basics/README.md)
