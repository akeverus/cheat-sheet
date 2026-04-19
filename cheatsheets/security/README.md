---
title: "Безопасность"
description: "Раздел посвящён практикам и инструментам безопасности: защита веб-приложений, API, инфраструктуры, управление секретами, тестирование безопасности и шифрование данных."
tags:
  - meta
  - index
type: "index"
updated: "2026-04-20"
---
# Безопасность

Раздел посвящён практикам и инструментам безопасности: защита веб-приложений, API, инфраструктуры, управление секретами, тестирование безопасности и шифрование данных.

## Принципы оформления

Текст во всех документах папки **security** должен быть **максимально человекочитаемым, легко запоминаемым и хорошо визуально восприниматься** — не сухие факты и не бесконечные списки. Это касается **всех документов** в этой папке. Короткие вводные к разделу, одна основная мысль на блок; где уместно — краткое «зачем это» или «как запомнить». Примеры — по одному наглядному на тему. Подзаголовки и абзацы — для удобного просмотра.

## Полезные ссылки

[Data Security](data/data-security.md)
[Secrets Management](data/secrets-management.md)

## Содержание

### Обзорные документы

- [Security Practices](security-practices.md) — общие практики и процессы (Secure SDLC, код-ревью, инциденты)

### Уровень приложения (`application/`)

- [Application Security](application/application-security.md) — OWASP Top 10, аутентификация, авторизация, токены
- [Web Security](application/web-security.md) — XSS, CSRF, CSP, CORS, cookie-безопасность
- [API Security](application/api-security.md) — аутентификация, авторизация, rate limiting, валидация
- [OWASP Top 10 (2021)](application/owasp-top-10.md) — разбор всех 10 категорий с кодом
- [JWT и OAuth2 / OIDC](application/jwt-oauth2.md) — токены, grants, Spring Security

### Данные и секреты (`data/`)

- [Data Security](data/data-security.md) — шифрование, хеширование, защита данных
- [Secrets Management](data/secrets-management.md) — Vault, KMS, управление ключами и секретами

### Инфраструктура (`infrastructure/`)

- [Infrastructure Security](infrastructure/infrastructure-security.md) — Docker, Kubernetes, облачная безопасность
- [TLS / SSL](infrastructure/tls-ssl.md) — handshake, сертификаты, mTLS, Nginx / Spring Boot / JVM

### Тестирование (`testing/`)

- [Security Testing](testing/security-testing.md) — SAST, DAST, пентест, аудит

### Инструменты (`tools/`)

- [Security Tools](tools/security-tools.md) — обзор инструментов безопасности
