# Security guidance — cheat-sheet (quiz-app)

Контекст: Spring Boot + JDBC (PostgreSQL), Thymeleaf, опциональный multi-user/OAuth2
режим с owner-scoping. Ниже — инварианты, которые model-ревью обязан проверять.

## Owner-scoping (multi-user режим)
- Любой repository-метод, читающий или пишущий пользовательские данные
  (bookmarks, user_topic_stats, user_experience, daily_activity, is_important /
  избранное), в режиме `mode=multi` ОБЯЗАН фильтровать по `owner_key`. Запрос без
  owner-фильтра в multi-режиме — находка (IDOR / horizontal privilege escalation).
- Флаг «важное» (`is_important`) не должен меняться/читаться без привязки к
  owner_key. Это уже фиксировалось как HIGH.
- Проверять, что `owner_key` берётся из аутентификации (principal), а НЕ из
  тела запроса, query-параметра или скрытого поля формы.

## Секреты и AI-fallback
- AI (OpenAI/DeepSeek) вырезан; включается только через env `AI_FALLBACK_ENABLED`
  + ключ из env. Ключи провайдеров НИКОГДА не хардкодить и не логировать —
  даже на уровне DEBUG, даже во временном коде.
- Не логировать содержимое `OPENAI_API_KEY` / `DEEPSEEK_API_KEY` /
  `SPRING_AI_API_KEY` и заголовки Authorization.

## SQL (JDBC-слой)
- Только параметризованные запросы `JdbcTemplate` / `NamedParameterJdbcTemplate`.
  Конкатенация пользовательского ввода в SQL-строку — находка (SQL injection).
- Flyway-миграции: DDL атомарно, без данных из недоверенных источников.

## Web / шаблоны
- Thymeleaf: `th:utext` и inline `[(...)]` выводят HTML без экранирования.
  Допустимо ТОЛЬКО для контролируемого server-side HTML (например
  `toInlineHtml`/sanitized markdown). Пользовательский ввод так выводить нельзя —
  это XSS.
- CSP уже настроена (коммит 70052004) — не ослаблять: не добавлять
  `unsafe-inline`/`unsafe-eval` в script-src без явного обоснования.
- CSRF: POST-эндпоинты (/answer, /finish, settings) должны нести `_csrf`.

## Общее
- Эти правила — guidance для ревьюера, не блокирующие guardrails. Нарушения
  всплывают как находки для исправления в той же сессии.
