---
title: "Slack"
description: "Slack: каналы, треды, slash-команды, webhooks, Bolt для ботов, Events API, ChatOps-паттерны и интеграции с CI/CD и alerting."
tags:
  - meta
  - index
  - slack
  - collaboration
type: "index"
updated: "2026-04-20"
---
# Slack

Slack — SaaS-платформа корпоративного чата: каналы (public/private/shared), треды, emoji-реакции, файлы, интеграции. Огромная экосистема (2000+ приложений в marketplace). Для инженера важны: incoming webhooks для notifications, Bolt SDK для ботов, Events API для реакций на события, slash-commands для ChatOps.

Альтернативы: [Mattermost](../../../basics/README.md) для self-hosted, [Telegram](../../../basics/README.md) для более лёгких сценариев и ботов.

## Полезные ссылки

### Основной документ
- [Основы Slack](slack-basics.md) — UI, API, боты, webhooks

### Соседние разделы
- [Mattermost](../../../basics/README.md)
- [Telegram](../../../basics/README.md)
- [Alerting](../../../basics/README.md) — алерты в Slack
- [CI/CD](../../../basics/README.md) — нотификации билдов

### Внешние ресурсы
- [Slack API docs](https://api.slack.com/)
- [Bolt SDK (Node, Python, Java)](https://slack.dev/bolt-js/tutorial/getting-started)
- [Block Kit Builder](https://app.slack.com/block-kit-builder) — конструктор сообщений
- [Incoming Webhooks](https://api.slack.com/messaging/webhooks)

## Содержание

- [Типы интеграций](#типы-интеграций)
- [Incoming Webhook — быстрый старт](#incoming-webhook-быстрый-старт)
- [Bot с Bolt](#bot-с-bolt)
- [ChatOps — паттерны](#chatops-паттерны)
- [Best practices](#best-practices)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Типы интеграций

| Тип | Когда |
|-----|-------|
| Incoming Webhook | однонаправленно: сервис канал |
| Outgoing Webhook (legacy) | канал сервис (устарело, лучше Events API) |
| Slash Commands | пользователь: `/command args` ваш endpoint |
| Events API | реакция на события (message, app_mention, reaction_added) |
| Interactive Components | кнопки, меню, модалки (Block Kit) |
| Socket Mode | WebSocket вместо public endpoint (удобно за firewall) |
| Workflow Builder | no-code автоматизации |

## Incoming Webhook — быстрый старт

```bash
curl -X POST -H 'Content-Type: application/json' \
  --data '{"text": "Deploy finished: v1.2.3 ✅"}' \
  https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX
```

С Block Kit:

```json
{
  "text": "Deploy completed",
  "blocks": [
    {
      "type": "section",
      "text": {
        "type": "mrkdwn",
        "text": "*Deploy completed*\n✅ v1.2.3 on prod-ru"
      }
    },
    {
      "type": "actions",
      "elements": [
        {
          "type": "button",
          "text": {"type": "plain_text", "text": "Rollback"},
          "style": "danger",
          "value": "rollback_v1_2_3",
          "confirm": {
            "title": {"type": "plain_text", "text": "Are you sure?"},
            "text": {"type": "mrkdwn", "text": "This will revert prod-ru."},
            "confirm": {"type": "plain_text", "text": "Rollback"},
            "deny": {"type": "plain_text", "text": "Cancel"}
          }
        }
      ]
    }
  ]
}
```

## Bot с Bolt

```javascript
// Node.js + Bolt
const { App } = require('@slack/bolt');

const app = new App({
  token: process.env.SLACK_BOT_TOKEN,
  signingSecret: process.env.SLACK_SIGNING_SECRET,
  socketMode: true,
  appToken: process.env.SLACK_APP_TOKEN
});

app.event('app_mention', async ({ event, say }) => {
  await say(`Hi <@${event.user}>!`);
});

app.command('/deploy', async ({ command, ack, respond }) => {
  await ack();
  // trigger CI pipeline
  await respond({ text: `Deploying ${command.text}...` });
});

(async () => { await app.start(); })();
```

## ChatOps — паттерны

- **Команды для операций:** `/deploy`, `/rollback`, `/restart service-name`.
- **RBAC:** проверка user.id против whitelist, role membership в Okta.
- **Audit-log:** каждое действие логируется с channel/user/ts.
- **Двухэтапное подтверждение:** кнопка Confirm перед разрушительным действием.
- **Нотификации on-call:** PagerDuty Slack DM + канал `#oncall-active`.
- **Incident channel automation:** бот создаёт `#incident-<id>` при page, приглашает on-call rotation, открывает Google Doc для post-mortem.

## Best practices

- **Каналы:** `#team-*`, `#proj-*`, `#on-call-*`, `#inc-*`, `#random`. Минимум public, чтобы контекст был прозрачен.
- **Треды** для обсуждений. Не превращайте канал в стену уведомлений.
- **Pinned messages** для onboarding-инфы.
- **Notifications:** DND schedule, keyword highlight, mute каналов без mentions.
- **File retention:** в enterprise — политика удаления старых файлов.
- **DLP/eDiscovery:** для compliance в enterprise.
- **Не храните секреты** даже в private DM — логирование и e-discovery всё видят.

## Маршруты чтения

- **Разработчик:** [slack-basics](slack-basics.md) Block Kit incoming webhook для своего сервиса.
- **DevOps:** ChatOps + PagerDuty integration + Alertmanager-webhook.
- **Admin:** workspace settings, retention, SSO, app governance.

## Куда идти дальше

- [Mattermost](../../../basics/README.md) для self-hosted
- [Alerting](../../../basics/README.md) — routing в Slack
- [CI/CD](../../../basics/README.md) — webhook после билда
