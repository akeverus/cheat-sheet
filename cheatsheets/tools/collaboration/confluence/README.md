---
title: "Confluence"
description: "Confluence: командная wiki от Atlassian — страницы, шаблоны, макросы, связка с Jira, организация knowledge base, REST API."
tags:
  - meta
  - index
  - confluence
  - collaboration
type: "index"
updated: "2026-04-20"
---
# Confluence

Confluence — командная wiki от Atlassian. Основной носитель документации, ADR, runbooks, дизайн-документов. Модель: Space Page tree. Страницы имеют версионирование, комментарии, метки, шаблоны, макросы (в т.ч. интеграции с Jira — live issue list, roadmap, burndown).

Для инженера минимум: создавать/править страницы, использовать шаблоны, искать через CQL. Для архитекторов/лидов — организация структуры пространства, шаблоны ADR/RFC, периодический audit на актуальность.

## Полезные ссылки

### Основной документ
- [[confluence-basics|Основы Confluence]] — spaces, страницы, макросы, CQL, шаблоны, API

### Соседние разделы
- [[README|Jira]] — макросы jira-issues в Confluence
- [[README|Architecture Decision Records]]
- [[README|Slack/Mattermost]] — интеграции notifications

### Внешние ресурсы
- [Confluence Cloud REST API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/)
- [CQL (Confluence Query Language)](https://developer.atlassian.com/server/confluence/advanced-searching-using-cql/)
- [Confluence Macros](https://support.atlassian.com/confluence-cloud/docs/use-macros/)

## Содержание

- [Организация пространства](#организация-пространства)
- [Полезные макросы](#полезные-макросы)
- [CQL — примеры](#cql-примеры)
- [Шаблоны документов](#шаблоны-документов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Организация пространства

**Рекомендованная структура для инженерной команды:**

```text
Team Space
├── Onboarding
│   ├── Getting Started
│   └── Access Matrix
├── Architecture
│   ├── ADRs (Architecture Decision Records)
│   ├── System Overview
│   └── Integrations
├── Runbooks
│   ├── On-call Playbook
│   └── Incident Response
├── Retrospectives
│   └── 2026-Q1/...
└── Archive
    └── Deprecated
```

## Полезные макросы

| Макрос | Применение |
|--------|-----------|
| `jira` | live-список issues из Jira |
| `toc` | оглавление страницы |
| `include page` | врезка другой страницы |
| `excerpt` / `excerpt-include` | блок для повторного использования |
| `info/warning/note` | цветные блоки |
| `status` | цветной label (PROD / WIP / DONE) |
| `task report` | агрегация чекбокс-задач |
| `page properties (report)` | база данных из страниц |
| `code block` | подсветка синтаксиса |

## CQL — примеры

```text
# Мои страницы за неделю
creator = currentUser() AND lastModified >= now("-7d")

# По метке
label = "runbook" AND space = TEAM

# Не обновлялись 6 месяцев (stale doc audit)
lastModified <= now("-6M") AND space = TEAM

# Поиск по тексту
text ~ "deploy" AND space = TEAM

# По шаблону ADR
title ~ "ADR-*" ORDER BY title ASC
```

## Шаблоны документов

**ADR (Architecture Decision Record):**
- Title, Status (Proposed/Accepted/Deprecated/Superseded)
- Context
- Decision
- Consequences

**RFC:**
- Problem
- Proposal
- Alternatives
- Impact
- Rollout plan

**Runbook:**
- Symptom
- Diagnosis
- Remediation
- Escalation

**Meeting notes:**
- Date, Attendees, Agenda
- Decisions
- Action items (с owner и deadline)

## Маршруты чтения

- **Новичок:** [[confluence-basics]] — навигация, поиск, редактирование, вставка макросов.
- **Автор доков:** шаблоны, Page Properties, CQL-отчёты.
- **Owner пространства:** permission schemes, templates, housekeeping.

## Куда идти дальше

- [[README|ADR templates]]
- [[README|Jira Confluence integration]]
- [AI-ассистенты для поиска в wiki](../../ai/)
