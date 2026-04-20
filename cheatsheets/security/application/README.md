---
title: "Application Security"
description: "Защита приложений на уровне кода и бизнес-логики: аутентификация, авторизация, валидация, защита от инъекций и подделки запросов."
tags:
  - meta
  - index
  - security
  - application
type: "index"
updated: "2026-04-20"
---
# Application Security

Раздел покрывает безопасность на уровне приложения: как спроектировать аутентификацию и авторизацию, валидировать ввод, защищаться от XSS/CSRF/SSRF/IDOR, корректно использовать токены (JWT, OAuth2, OIDC) и строить API, устойчивые к атакам. Ориентир — **OWASP Top 10** как карта классов уязвимостей и **OWASP Cheat Sheet Series** как источник конкретных практик.

Для кого: backend/full-stack разработчики и security-инженеры, которым нужно быстро найти, куда смотреть при проектировании или ревью сервиса, генерирующего HTTP/REST API. Здесь нет инфраструктурного уровня (см. `../infrastructure/`) и шифрования данных (см. `../data/`).

## Полезные ссылки

### Основные документы
- [[application-security]] — обзорный файл AppSec (аутентификация, авторизация, токены)
- [[web-security]] — OWASP Top 10, XSS, CSRF, SQL Injection, SSRF
- [[api-security]] — JWT, API Keys, rate limiting, контроль доступа к API
- [[owasp-top-10]] — полный разбор OWASP Top 10 (2021) с примерами кода
- [[jwt-oauth2]] — JWT, OAuth2 grants, OIDC, Spring Resource Server

### Соседние разделы
- [[README|security/]] — корень раздела безопасности
- [[README|security/data/]] — шифрование, хеширование паролей, secrets
- [[README|security/infrastructure/]] — сетевой периметр, Kubernetes, hardening
- [[README|security/testing/]] — SAST/DAST/SCA, пентесты
- [[README|security/tools/]] — инструменты сканирования и защиты

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
| Аутентификация и сессии | [[application-security]] |
| Авторизация, RBAC/ABAC | [[application-security]] |
| JWT в деталях, атаки, Spring | [[jwt-oauth2]] |
| OAuth2 grant types, OIDC | [[jwt-oauth2]] |
| OWASP Top 10 (2021) с кодом | [[owasp-top-10]] |
| XSS, CSRF, SQLi, SSRF | [[web-security]] |
| Rate limiting, API keys | [[api-security]] |
| Secrets management | [[secrets-management]] |
| Шифрование и хеширование | [[data-security]] |

## OWASP Top 10 как ориентир

Раздел выстроен вокруг актуальной версии OWASP Top 10. При ревью сервиса пройдитесь по списку категорий (A01 Broken Access Control, A02 Cryptographic Failures, A03 Injection, A04 Insecure Design, A05 Security Misconfiguration, A06 Vulnerable Components, A07 Auth Failures, A08 Software and Data Integrity, A09 Logging Failures, A10 SSRF) и убедитесь, что для каждой есть явный контроль либо осознанный аргумент, почему он не нужен.

## Когда использовать

- Проектируете новый сервис с HTTP/REST API — начинайте с `application-security.md` + `api-security.md`.
- Ревьюите чужой код — используйте `web-security.md` как чеклист OWASP Top 10.
- Интегрируетесь с внешним IdP — смотрите OAuth2/OIDC-секции в `application-security.md`.
- Работаете с инфраструктурой (кластер, сеть) — уходите в `../infrastructure/`.

## Маршруты чтения

- **Junior (1 час):** `application-security.md` — общие концепты → `owasp-top-10.md` — обзор категорий.
- **Backend-разработчик на ревью PR:** `owasp-top-10.md` как чек-лист + `api-security.md` для API-эндпоинтов + `jwt-oauth2.md` для токенов.
- **Интеграция с IdP:** `jwt-oauth2.md` — Authorization Code + PKCE, Spring Resource Server.
- **Security-чемпион команды:** все пять документов + `../testing/security-testing.md` + `../tools/security-tools.md`.

## Куда идти дальше

- Тестирование безопасности — [[README]]
- Инструменты SAST/DAST/SCA — [[README]]
- Безопасность данных (шифрование, пароли) — [[README]]
- Контейнерная и сетевая безопасность — [[README]]
