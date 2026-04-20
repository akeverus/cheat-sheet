---
title: "Jira"
description: "Jira: issue tracker для скрама и канбана, иерархия Epic/Story/Subtask, JQL, workflows, Agile-доски и REST API."
tags:
  - meta
  - index
  - jira
  - collaboration
type: "index"
updated: "2026-04-20"
---
# Jira

Jira — самый распространённый issue tracker для разработки. Модель: проект issue (с типом Epic/Story/Task/Bug/Subtask) поля и workflow. Две главные методологии, поддерживаемые из коробки: Scrum (спринты, backlog) и Kanban (WIP-лимиты, поток).

Для разработчика минимум: уметь писать и двигать тикеты, использовать JQL для поиска, понимать workflow-переходы. Для лида — настраивать фильтры, доски, роадмапы, автоматизации.

## Полезные ссылки

### Основной документ
- [Основы Jira](jira-basics.md) — UI, JQL, поля, workflow, Agile-доски, API

### Соседние разделы
- [Confluence](../../../basics/README.md)
- [Slack](../../../basics/README.md) — интеграции Jira Slack
- [GitLab/GitHub](../../../basics/README.md) — ссылки коммитов на тикеты

### Внешние ресурсы
- [Jira Cloud Platform REST API](https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/)
- [JQL reference](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/)
- [Jira Automation](https://support.atlassian.com/cloud-automation/)
- [Atlassian Marketplace](https://marketplace.atlassian.com/)

## Содержание

- [Ключевые концепции](#ключевые-концепции)
- [JQL — примеры](#jql-примеры)
- [Типовые задачи](#типовые-задачи)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые концепции

| Термин | Объяснение |
|--------|-----------|
| Project | корневой контейнер, настраиваются типы issues, workflow, permissions |
| Issue Type | Epic, Story, Task, Bug, Subtask, кастомные |
| Workflow | машина состояний issue (Open In Progress Done) |
| Sprint | ограниченный по времени промежуток для Scrum |
| Backlog | список неначатых задач |
| Board | визуализация (Scrum/Kanban) |
| Filter | сохранённый JQL-запрос |
| Component | тэг принадлежности к подсистеме |
| Version / Fix Version | релиз, с которым связан тикет |
| Epic Link | привязка Story к Epic |

## JQL — примеры

```text
# Мои открытые задачи
assignee = currentUser() AND resolution = Unresolved

# В текущем спринте
sprint in openSprints() AND assignee = currentUser()

# Завершённые за последнюю неделю
resolved >= -7d AND project = ABC

# Bugs с high priority без assignee
issuetype = Bug AND priority in (High, Highest) AND assignee is EMPTY

# Задачи из Epic
"Epic Link" = ABC-123

# По метке
labels = tech-debt AND status != Done

# Комплексно
project = ABC AND updated >= -3d ORDER BY priority DESC
```

## Типовые задачи

- **Создать Subtask из Story:** в тикете `+` Subtask. Наследует Epic Link.
- **Связать коммит:** `git commit -m "ABC-123: fix login"` — GitLab/GitHub добавит ссылку в тикет.
- **Массовое обновление:** JQL Tools Bulk Change Transition.
- **Автоматизация:** rule «When issue moves to Done comment in Slack channel #team».
- **Выгрузка отчёта:** JQL Export CSV/XLSX/JSON или REST API `/rest/api/3/search?jql=...`.

## Маршруты чтения

- **Первый день:** [jira-basics](jira-basics.md) — как создать/двигать тикет, JQL-поиск, личный фильтр.
- **Интеграция:** разделы про webhooks и REST API, связка с CI/CD.
- **Настройка проекта:** workflow editor, permission schemes, automation.

## Куда идти дальше

- Confluence для documentation — [Confluence](../../../basics/README.md)
- Связка с чатом — [Slack](../../../basics/README.md) / [Mattermost](../../../basics/README.md)
- Связка с pipeline — [CI/CD](../../../basics/README.md)
