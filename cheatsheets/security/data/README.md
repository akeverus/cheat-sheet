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
updated: "2026-04-20"
---
# Data Security

Раздел про защиту данных: симметричное и асимметричное шифрование (AES, RSA, ECC), TLS/mTLS для передачи, хеширование паролей (bcrypt, Argon2, scrypt), KMS/Vault для ключей и секретов, а также подходы к защите PII и маскированию. Криптография здесь рассматривается прикладно — как и какие примитивы использовать, а не как их устройство.

Для кого: разработчики и DevOps, которые работают с хранением чувствительных данных, паролей и токенов, а также проектируют TLS-термирование и secret rotation. Теоретическая криптография — в `basics/computer-science/`.

## Полезные ссылки

### Основные документы
- [data-security](data-security.md) — обзор шифрования и хеширования
- [secrets-management](secrets-management.md) — Vault, KMS, Kubernetes Secrets, ротация

### Соседние разделы
- [security/](../../basics/README.md) — корень раздела
- [security/application/](../../basics/README.md) — токены и аутентификация
- [security/infrastructure/](../../basics/README.md) — mTLS, сертификаты, PKI
- [security/testing/](../../basics/README.md) — проверка корректности криптопримитивов
- [basics/computer-science/](../../basics/README.md) — теория шифров и хешей

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
| AES, режимы (GCM, CBC), IV/nonce | [data-security](data-security.md) |
| RSA, ECDSA, кривые | [data-security](data-security.md) |
| TLS 1.3, cipher suites | [data-security](data-security.md) |
| Хеши: SHA-256, HMAC | [data-security](data-security.md) |
| Пароли: bcrypt, Argon2, scrypt | [data-security](data-security.md) |
| Vault, KMS (AWS/GCP/Azure) | [secrets-management](secrets-management.md) |
| Kubernetes Secrets, Sealed Secrets | [secrets-management](secrets-management.md) |
| Ротация ключей и секретов | [secrets-management](secrets-management.md) |
| PII, GDPR-маскирование | [data-security](data-security.md) |

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

- Secrets в CI/CD и Kubernetes — [README](../../basics/README.md)
- Тестирование криптостойкости — [README](../../basics/README.md)
- Процессы и compliance — [security-practices](../security-practices.md)
- Теория шифров и протоколов — [README](../../basics/README.md)
