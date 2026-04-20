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

[[data-security|Data Security]]
[[secrets-management|Secrets Management]]

## Содержание

### Обзорные документы

- [[security-practices|Security Practices]] — общие практики и процессы (Secure SDLC, код-ревью, инциденты)

### Уровень приложения (`application/`)

- [[application-security|Application Security]] — OWASP Top 10, аутентификация, авторизация, токены
- [[web-security|Web Security]] — XSS, CSRF, CSP, CORS, cookie-безопасность
- [[api-security|API Security]] — аутентификация, авторизация, rate limiting, валидация
- [[owasp-top-10|OWASP Top 10 (2021)]] — разбор всех 10 категорий с кодом
- [[jwt-oauth2|JWT и OAuth2 / OIDC]] — токены, grants, Spring Security

### Данные и секреты (`data/`)

- [[data-security|Data Security]] — шифрование, хеширование, защита данных
- [[secrets-management|Secrets Management]] — Vault, KMS, управление ключами и секретами

### Инфраструктура (`infrastructure/`)

- [[infrastructure-security|Infrastructure Security]] — Docker, Kubernetes, облачная безопасность
- [[tls-ssl|TLS / SSL]] — handshake, сертификаты, mTLS, Nginx / Spring Boot / JVM

### Тестирование (`testing/`)

- [[security-testing|Security Testing]] — SAST, DAST, пентест, аудит

### Инструменты (`tools/`)

- [[security-tools|Security Tools]] — обзор инструментов безопасности
