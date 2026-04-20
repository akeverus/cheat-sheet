---
title: "Data Security"
description: "Шифрование данных в покое и при передаче, хеширование паролей, управление секретами и защита персональных данных."
tags:
  - meta
  - index
  - security
  - data
  - encryption
type: "index"
updated: "2026-04-17"
---
# Data Security

Раздел про защиту данных: симметричное и асимметричное шифрование (AES, RSA, ECC), TLS/mTLS для передачи, хеширование паролей (bcrypt, Argon2, scrypt), KMS/Vault для ключей и секретов, а также подходы к защите PII и маскированию. Криптография здесь рассматривается прикладно — как и какие примитивы использовать, а не как их устройство.

Для кого: разработчики и DevOps, которые работают с хранением чувствительных данных, паролей и токенов, а также проектируют TLS-термирование и secret rotation. Теоретическая криптография — в `basics/computer-science/`.

## Полезные ссылки

### Основные документы
- [[data-security]] — обзор шифрования и хеширования
- [[secrets-management]] — Vault, KMS, Kubernetes Secrets, ротация

### Соседние разделы
- [[README|security/]] — корень раздела
- [[README|security/application/]] — токены и аутентификация
- [[README|security/infrastructure/]] — mTLS, сертификаты, PKI
- [[README|security/testing/]] — проверка корректности криптопримитивов
- [[README|basics/computer-science/]] — теория шифров и хешей

### Внешние ресурсы
- [NIST SP 800-175B](https://csrc.nist.gov/publications/detail/sp/800-175b/rev-1/final) — руководство по криптографии
- [OWASP Cryptographic Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
- [Let's Encrypt](https://letsencrypt.org/) — бесплатные TLS-сертификаты
- [HashiCorp Vault](https://www.vaultproject.io/)

## Содержание

- [Карта тем](#карта-тем)
- [Что и когда шифровать](#что-и-когда-шифровать)
- [Хеширование паролей](#хеширование-паролей)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| AES, режимы (GCM, CBC), IV/nonce | [[data-security]] |
| RSA, ECDSA, кривые | [[data-security]] |
| TLS 1.3, cipher suites | [[data-security]] |
| Хеши: SHA-256, HMAC | [[data-security]] |
| Пароли: bcrypt, Argon2, scrypt | [[data-security]] |
| Vault, KMS (AWS/GCP/Azure) | [[secrets-management]] |
| Kubernetes Secrets, Sealed Secrets | [[secrets-management]] |
| Ротация ключей и секретов | [[secrets-management]] |
| PII, GDPR-маскирование | [[data-security]] |

## Что и когда шифровать

- **At rest:** диск/том, БД-колонки с PII, бэкапы. Ключи — в KMS, не рядом с данными.
- **In transit:** TLS 1.2+ для всех внешних соединений, mTLS для service-to-service внутри кластера.
- **In use (memory):** редко — confidential computing, SGX/SEV. Для большинства сервисов избыточно.
- **End-to-end:** когда сервер не должен видеть данные (E2EE-мессенджеры, e2e-шифрование файлов).

## Хеширование паролей

Никогда не используйте MD5/SHA-1/SHA-256 «в голом виде» для паролей. Применяйте KDF: **Argon2id** (рекомендуется OWASP), bcrypt, scrypt или PBKDF2 с высоким cost-параметром. Для HMAC и подписей — SHA-256/SHA-384 допустимы.

## Маршруты чтения

- **Quick scan (30 мин):** `data-security.md` — разделы «Симметричное шифрование», «Хеширование паролей», «TLS».
- **Перед выкладкой сервиса:** `secrets-management.md` + чек-лист хранения ключей в KMS/Vault.
- **Full refresh:** оба документа + `../application/application-security.md` (для JWT-подписей и ротации).

## Куда идти дальше

- Secrets в CI/CD и Kubernetes — [[README]]
- Тестирование криптостойкости — [[README]]
- Процессы и compliance — [[security-practices]]
- Теория шифров и протоколов — [[README]]
