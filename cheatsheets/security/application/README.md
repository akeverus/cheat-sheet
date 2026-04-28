---
title: "Application Security"
description: "Защита приложений на уровне кода и бизнес-логики: аутентификация, авторизация, валидация, защита от инъекций и подделки запросов."
tags:
  - meta
  - index
  - security
  - application
type: "index"
aliases:
  - "Application Security"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Application Security

Раздел покрывает безопасность на уровне приложения: как спроектировать аутентификацию и авторизацию, валидировать ввод, защищаться от XSS/CSRF/SSRF/IDOR, корректно использовать токены (JWT, OAuth2, OIDC) и строить API, устойчивые к атакам. Ориентир — **OWASP Top 10** как карта классов уязвимостей и **OWASP Cheat Sheet Series** как источник конкретных практик.

Для кого: backend/full-stack разработчики и security-инженеры, которым нужно быстро найти, куда смотреть при проектировании или ревью сервиса, генерирующего HTTP/REST API. Здесь нет инфраструктурного уровня (см. `../infrastructure/`) и шифрования данных (см. `../data/`).

## Полезные ссылки

### Основные документы
- [application-security](application-security.md) — обзорный файл AppSec (аутентификация, авторизация, токены)
- [web-security](web-security.md) — OWASP Top 10, XSS, CSRF, SQL Injection, SSRF
- [api-security](api-security.md) — JWT, API Keys, rate limiting, контроль доступа к API
- [owasp-top-10](owasp-top-10.md) — полный разбор OWASP Top 10 (2021) с примерами кода
- [jwt-oauth2](jwt-oauth2.md) — JWT, OAuth2 grants, OIDC, Spring Resource Server

### Соседние разделы
- [security/](../../basics/README.md) — корень раздела безопасности
- [security/data/](../../basics/README.md) — шифрование, хеширование паролей, secrets
- [security/infrastructure/](../../basics/README.md) — сетевой периметр, Kubernetes, hardening
- [security/testing/](../../basics/README.md) — SAST/DAST/SCA, пентесты
- [security/tools/](../../basics/README.md) — инструменты сканирования и защиты

### Внешние ресурсы
- [OWASP Top 10](https://owasp.org/www-project-top-ten/) — актуальный список критичных уязвимостей
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/) — практические шпаргалки
- [OWASP ASVS](https://owasp.org/www-project-application-security-verification-standard/) — стандарт верификации
- [OAuth 2.0](https://oauth.net/2/), [OpenID Connect](https://openid.net/connect/)
- [JWT RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)

## Содержание

- [Карта тем](#карта-тем)
- [OWASP Top 10 как ориентир](#owasp-top-10-как-ориентир)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Где читать |
|------|-----------|
| Аутентификация и сессии | [application-security](application-security.md) |
| Авторизация, RBAC/ABAC | [application-security](application-security.md) |
| JWT в деталях, атаки, Spring | [jwt-oauth2](jwt-oauth2.md) |
| OAuth2 grant types, OIDC | [jwt-oauth2](jwt-oauth2.md) |
| OWASP Top 10 (2021) с кодом | [owasp-top-10](owasp-top-10.md) |
| XSS, CSRF, SQLi, SSRF | [web-security](web-security.md) |
| Rate limiting, API keys | [api-security](api-security.md) |
| Secrets management | [secrets-management](../data/secrets-management.md) |
| Шифрование и хеширование | [data-security](../data/data-security.md) |

## OWASP Top 10 как ориентир

Раздел выстроен вокруг актуальной версии OWASP Top 10. При ревью сервиса пройдитесь по списку категорий (A01 Broken Access Control, A02 Cryptographic Failures, A03 Injection, A04 Insecure Design, A05 Security Misconfiguration, A06 Vulnerable Components, A07 Auth Failures, A08 Software and Data Integrity, A09 Logging Failures, A10 SSRF) и убедитесь, что для каждой есть явный контроль либо осознанный аргумент, почему он не нужен.

## Когда использовать

- Проектируете новый сервис с HTTP/REST API — начинайте с `application-security.md` + `api-security.md`.
- Ревьюите чужой код — используйте `web-security.md` как чеклист OWASP Top 10.
- Интегрируетесь с внешним IdP — смотрите OAuth2/OIDC-секции в `application-security.md`.
- Работаете с инфраструктурой (кластер, сеть) — уходите в `../infrastructure/`.

## Маршруты чтения

- **Junior (1 час):** `application-security.md` — общие концепты `owasp-top-10.md` — обзор категорий.
- **Backend-разработчик на ревью PR:** `owasp-top-10.md` как чек-лист + `api-security.md` для API-эндпоинтов + `jwt-oauth2.md` для токенов.
- **Интеграция с IdP:** `jwt-oauth2.md` — Authorization Code + PKCE, Spring Resource Server.
- **Security-чемпион команды:** все пять документов + `../testing/security-testing.md` + `../tools/security-tools.md`.

## Куда идти дальше

- Тестирование безопасности — [README](../../basics/README.md)
- Инструменты SAST/DAST/SCA — [README](../../basics/README.md)
- Безопасность данных (шифрование, пароли) — [README](../../basics/README.md)
- Контейнерная и сетевая безопасность — [README](../../basics/README.md)
