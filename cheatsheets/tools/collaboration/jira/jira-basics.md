---
title: "Jira: основы"
description: "Задачи, доски, спринты и базовый workflow в Jira."
tags:
  - tools
  - collaboration
  - jira-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Jira: основы

Задачи, доски, спринты и базовый workflow в Jira.

## Введение

Jira — система учёта задач и проектов. Используется для бэклога, спринтов, досок (Kanban/Scrum) и отчётности. Понимание типов задач, workflow и досок необходимо для работы в команде.

## Полезные ссылки

- [Jira Software Documentation](https://www.atlassian.com/software/jira)
- [Jira Workflow](https://support.atlassian.com/jira-software-cloud/docs/get-started-with-jira-workflow/)
- [Jira Query Language (JQL)](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/)

## Содержание

- [Типы задач](#типы-задач)
- [Доски и спринты](#доски-и-спринты)
- [Рабочий процесс](#рабочий-процесс)
- [JQL и фильтры](#jql-и-фильтры)
- [Примеры: описание задачи и критерии приёмки](#примеры-описание-задачи-и-критерии-приёмки)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
- [Кейсы использования с n8n](#кейсы-использования-с-n8n)
  - [Кейс 1: Новая задача в Jira уведомление в Slack](#кейс-1-новая-задача-в-jira-уведомление-в-slack)
  - [Кейс 2: Ежедневный отчёт по задачам (JQL Slack)](#кейс-2-ежедневный-отчёт-по-задачам-jql-slack)
  - [Кейс 3: Создание задачи в Jira из формы или Slack](#кейс-3-создание-задачи-в-jira-из-формы-или-slack)
- [Лучшие практики](#лучшие-практики)
- [FAQ](#faq)
- [Глоссарий](#глоссарий)
- [Чек-листы](#чек-листы)
  - [Создание задачи (Story/Task)](#создание-задачи-storytask)
  - [Планирование спринта (Scrum)](#планирование-спринта-scrum)
  - [Ежедневное обновление](#ежедневное-обновление)
  - [Настройка доски (Kanban)](#настройка-доски-kanban)
- [Операторы и функции JQL](#операторы-и-функции-jql)
- [Решение проблем](#решение-проблем)
- [Заключение](#заключение)

## Типы задач

- **Epic** — крупная цель; объединяет истории и задачи.
- **Story** — пользовательская история; ценность для пользователя.
- **Task** — техническая или организационная задача.
- **Bug** — дефект; привязка к Story/Epic при необходимости.
- **Subtask** — подзадача к Story/Task.

Типы и поля настраиваются в проекте (Issue Type Scheme, Custom Fields).


## Доски и спринты

- **Scrum:** бэклог, спринты с целями, доска (To Do, In Progress, Done); планирование спринта, ретро.
- **Kanban:** непрерывный поток; WIP-лимиты по столбцам; без фиксированных спринтов.
- **Доска:** задачи по статусам; drag-and-drop для смены статуса; фильтр по проекту/JQL.


## Рабочий процесс

Рабочий процесс — набор статусов и переходов (например: Open In Progress Code Review Done). Переходы могут требовать заполнения полей, разрешений. Типовой поток: создание в работе ревью закрытие.


## JQL и фильтры

JQL (Jira Query Language) — запросы к задачам. Примеры:

- `project = MYPROJ AND status = "In Progress"`
- `assignee = currentUser() AND resolution = Unresolved`
- `created >= -7d ORDER BY created DESC`

Сохранённые JQL-запросы становятся фильтрами; фильтры используются в досках, дашбордах и отчётах.


## Примеры: описание задачи и критерии приёмки

Story полезно описывать в формате «Как [роль], я хочу [действие], чтобы [ценность]». Критерии приёмки (Acceptance Criteria) — список проверяемых условий; каждый пункт можно проверить тестом или вручную.

**Пример названия Story:** «Поиск по email в админке».

**Пример описания:**

```text
Как администратор портала, я хочу искать пользователя по email в админке,
чтобы быстро находить аккаунт при обращении в поддержку.

Контекст: сейчас поиск только по имени; по email ищут через БД.
```

**Пример критериев приёмки:**

- В форме поиска есть поле «Email»; поиск по подстроке (contains), не точное совпадение.
- При вводе от 2 символов показываются результаты (таблица: email, имя, дата регистрации); при пустом поле — подсказка «Введите минимум 2 символа».
- Результаты ограничены 50 записями; при большем числе выводится сообщение «Показаны первые 50, уточните запрос».
- При отсутствии результатов — сообщение «Ничего не найдено».

Такой формат даёт однозначность для разработки и приёмки.


## Интеграция с Spring Boot

Jira предоставляет REST API для создания задач, поиска по JQL, обновления полей и переходов по workflow. В Spring Boot удобно использовать WebClient или RestTemplate с Basic Auth (логин + API-токен) или OAuth 2.0.

**Зависимости (`pom.xml`):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Конфигурация (`application.yml`):**

```yaml
jira:
  base-url: https://your-domain.atlassian.net
  username: ${JIRA_USERNAME}
  password: ${JIRA_API_TOKEN}  # API Token из account.atlassian.com
```

**Сервис: создание задачи и поиск по JQL:**

```java
package com.example.jira.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class JiraClientService {

    private final WebClient webClient;
    private final String baseUrl;

    public JiraClientService(
            WebClient.Builder builder,
            @Value("${jira.base-url}") String baseUrl,
            @Value("${jira.username}") String username,
            @Value("${jira.password}") String apiToken) {
        this.baseUrl = baseUrl;
        String auth = Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes(StandardCharsets.UTF_8));
        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Создание задачи (например, Task или Story).
     */
    public Map<String, Object> createIssue(String projectKey, String issueType, String summary, String description) {
        Map<String, Object> body = Map.of(
                "fields", Map.of(
                        "project", Map.of("key", projectKey),
                        "summary", summary,
                        "description", Map.of(
                                "type", "doc", "version", 1,
                                "content", List.of(
                                        Map.of("type", "paragraph",
                                                "content", List.of(Map.of("type", "text", "text", description)))
                                )),
                        "issuetype", Map.of("name", issueType)));
        return webClient.post()
                .uri("/rest/api/3/issue")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Поиск задач по JQL; возвращает ключи и поля.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchByJql(String jql, int maxResults) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/rest/api/3/search")
                        .queryParam("jql", jql)
                        .queryParam("maxResults", maxResults)
                        .queryParam("fields", "summary,status,assignee,created")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return (List<Map<String, Object>>) response.get("issues");
    }
}
```

**Использование:** вызов `createIssue("MYPROJ", "Task", "Заголовок", "Описание")` создаёт задачу; `searchByJql("project = MYPROJ AND resolution = Unresolved ORDER BY updated DESC", 20)` — возвращает список открытых задач. API Token создаётся в [Atlassian Account Security API tokens](https://id.atlassian.com/manage-profile/security/api-tokens).


## Кейсы использования с n8n

n8n позволяет строить сценарии без кода: триггер (расписание, webhook, событие из приложения) и действия (запрос к Jira API, отправка в Slack и т.д.). Ниже — кейсы с Jira и n8n.

### Кейс 1: Новая задача в Jira уведомление в Slack

- **Триггер:** Webhook (n8n даёт URL; в Jira Settings System WebHooks добавляется webhook на событие «Issue created» с URL n8n).
- **Шаг 2:** в n8n отфильтровать по проекту/типу (например, только Story в MYPROJ).
- **Шаг 3:** узел Slack — отправить в канал сообщение с ключом задачи, названием и ссылкой `https://your-domain.atlassian.net/browse/{{ $json.issue.key }}`.

Итог: при создании задачи в Jira в Slack автоматически появляется уведомление.

### Кейс 2: Ежедневный отчёт по задачам (JQL Slack)

- **Триггер:** узел Schedule (Cron: каждый день в 9:00).
- **Шаг 2:** узел Jira — операция Search с JQL вида `project = MYPROJ AND assignee = currentUser() AND resolution = Unresolved ORDER BY priority DESC`.
- **Шаг 3:** Code или Set — сформировать текст отчёта из списка задач (ключ, summary, статус).
- **Шаг 4:** Slack — отправить сообщение в личку или в канал с этим отчётом.

Итог: команда или отдельный пользователь получает ежедневную сводку по своим открытым задачам.

### Кейс 3: Создание задачи в Jira из формы или Slack

- **Триггер:** Webhook (форма на сайте) или Slack (реакция/команда «создать тикет»).
- **Шаг 2:** извлечь из тела запроса или сообщения Slack заголовок и описание.
- **Шаг 3:** узел Jira — Create Issue: указать проект, тип (Task), summary, description.
- **Шаг 4:** ответить в Slack или в Webhook ответом с ключом созданной задачи (например, PROJ-123).

Итог: создание задач в Jira без входа в интерфейс (через форму или из Slack).

В n8n узлы Jira и Slack настраиваются через учётные данные (Credentials): для Jira — URL, email и API Token; для Slack — OAuth2 или Access Token.


## Лучшие практики

- Единые соглашения по именованию Epic/Story и формату описаний (критерии приёмки для Story).
- Короткие осмысленные названия; описание и критерии приёмки в теле задачи.
- Регулярное обновление статусов и оценок; не накапливать «зависшие» задачи.
- Один исполнитель на задачу; понятный workflow для команды.
- Использовать компоненты и лейблы для фильтрации; связи между задачами (blocks, is blocked by, relates to) для отслеживания зависимостей.
- Регулярная очистка бэклога и архивирование завершённых спринтов.


## FAQ

**Чем Epic отличается от Story?**
Epic — крупная цель (фича, инициатива), объединяющая много Story/Task. Story — одна пользовательская история с ценностью; обычно оценивается в стори-поинтах.

**Когда использовать Task, а когда Story?**
Story — когда есть пользовательская ценность и критерии приёмки («как пользователь я хочу…»). Task — техническая или организационная работа (рефакторинг, настройка CI, митинг).

**Что такое стори-поинты?**
Относительная оценка сложности Story (часто шкала Фибоначчи: 1, 2, 3, 5, 8, 13). Для планирования спринта и velocity, не «часы».

**Что такое WIP-лимит?**
Work In Progress — максимум задач в столбце (часто «In Progress»). В Kanban превышать нельзя.

**Чем Scrum-доска отличается от Kanban?**
Scrum: фиксированные спринты, бэклог, планирование, ретро. Kanban: непрерывный поток, без спринтов, акцент на WIP и cycle time.

**Как связать задачу с другой?**
В задаче: Links Add link «Blocks», «Is blocked by», «Relates to» и др.

**Как найти все свои открытые задачи?**
JQL: `assignee = currentUser() AND resolution = Unresolved ORDER BY priority DESC`. Сохранить как фильтр.

**Что такое resolution?**
Итог закрытия: Done, Won't Do, Duplicate и т.д. У незакрытых — resolution пустой (Unresolved).


## Глоссарий

| Термин | Описание |
|--------|----------|
| **Backlog** | Список задач, ещё не взятых в спринт или в работу. |
| **Board** | Доска: задачи по столбцам (статусам), drag-and-drop. |
| **Epic** | Крупная цель; объединяет Story/Task. |
| **Filter** | Сохранённый **JQL**-запрос. |
| **JQL** | Jira Query Language — язык запросов к задачам. |
| **Kanban** | Подход без спринтов; непрерывный поток, **WIP**-лимиты. |
| **Resolution** | Итог закрытия: Done, Won't Do, Duplicate и т.д. |
| **Scrum** | Подход со спринтами, бэклогом, планированием и ретро. |
| **Sprint** | Фиксированный интервал (1–4 недели). |
| **Story** | Пользовательская история. |
| **Story Points** | Относительная оценка сложности Story. |
| **Task** | Техническая или организационная задача. |
| **Velocity** | Сумма стори-поинтов за спринт. |
| **WIP** | Work In Progress; лимит задач в столбце. |
| **Workflow** | Набор статусов и переходов между ними. |


## Чек-листы

### Создание задачи (Story/Task)

- [ ] Короткое осмысленное название.
- [ ] Описание: контекст и цель.
- [ ] Для Story: критерии приёмки.
- [ ] Исполнитель (Assignee), при необходимости компонент/лейблы.
- [ ] Связи с другими задачами (Blocks, Relates to), если есть.
- [ ] Оценка (Story Points для Story), если принято в команде.

### Планирование спринта (Scrum)

- [ ] Цель спринта сформулирована.
- [ ] В спринт взяты задачи с учётом velocity.
- [ ] Зависимости учтены (blocked by).
- [ ] Объём соответствует возможностям команды.

### Ежедневное обновление

- [ ] Статусы актуальны (In Progress / Done).
- [ ] Блокеры отмечены и обсуждаются.

### Настройка доски (Kanban)

- [ ] Столбцы соответствуют статусам workflow.
- [ ] WIP-лимиты заданы для «In Progress».
- [ ] Фильтр доски (JQL) корректен.


## Операторы и функции JQL

| Оператор / функция | Описание | Пример |
|--------------------|----------|--------|
| `=`, `!=` | Равенство / неравенство | `status = Done`, `assignee != currentUser()` |
| `IN`, `NOT IN` | Список значений | `status IN ("Open", "In Progress")` |
| `~`, `!~` | Поиск по тексту | `summary ~ "bug"` |
| `AND`, `OR` | Логика | `project = MYPROJ AND assignee = currentUser()` |
| `ORDER BY` | Сортировка | `ORDER BY created DESC` |
| `currentUser()` | Текущий пользователь | `assignee = currentUser()` |
| `-Nd` | Относительная дата (дни) | `created >= -7d` (последние 7 дней) |

**Примеры JQL:**

- Мои открытые: `assignee = currentUser() AND resolution = Unresolved ORDER BY priority DESC`
- В работе по проекту: `project = MYPROJ AND status = "In Progress" ORDER BY updated DESC`
- За последние 7 дней: `project = MYPROJ AND created >= -7d ORDER BY created DESC`
- Текущий спринт: `project = MYPROJ AND sprint in openSprints()`


## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Не вижу задачу/проект | Нет прав | Проверить права доступа к проекту; запросить у администратора. |
| JQL не находит задачи | Опечатка в поле или операторе | Проверить имя поля и синтаксис в [документации JQL](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/). |
| Доска не обновляется | Кэш или неверный фильтр | Обновить страницу; проверить JQL фильтра доски. |


## Заключение

Для углублённого изучения — [официальная документация Atlassian](https://www.atlassian.com/software/jira). См. также: [[confluence-basics|Confluence]] — [[slack-basics|Slack]] — [[mattermost-basics|Mattermost]] — [[telegram-basics|Telegram]].
