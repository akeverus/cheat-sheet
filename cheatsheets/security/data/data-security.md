---
title: "Безопасность данных (Data Security)"
description: "Шифрование данных в покое и при передаче, хэширование паролей, классификация данных, защита PII, маскирование и ключевое управление."
tags:
  - security
  - data
  - encryption
  - pii
difficulty: "intermediate"
updated: "2026-04-17"
---
# Безопасность данных (Data Security)

Data security — защита данных от несанкционированного доступа, модификации и потери через технические (шифрование, хэширование, маскирование) и организационные (классификация, минимизация, retention) меры. Работает в связке с [[secrets-management|Secrets Management]] и [[application-security|Application Security]].

Главная мысль: ценность данных не одинакова. Классифицируйте сначала, защищайте соразмерно. Для PII и платёжных данных — регуляторные требования (GDPR, PCI-DSS), и ошибки здесь стоят миллионы и репутацию.

## Полезные ссылки

### Стандарты и гайды
- [OWASP Cryptographic Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
- [NIST SP 800-57 — Key Management](https://csrc.nist.gov/publications/detail/sp/800-57-part-1/rev-5/final)
- [PCI DSS v4.0](https://www.pcisecuritystandards.org/document_library/)
- [GDPR (EU 2016/679)](https://gdpr-info.eu/)

### Библиотеки и инструменты
- [libsodium](https://doc.libsodium.org/) — современное crypto API
- [Bouncy Castle](https://www.bouncycastle.org/) — crypto для Java
- [Argon2 reference](https://github.com/P-H-C/phc-winner-argon2) — KDF для паролей
- [Tink (Google)](https://developers.google.com/tink) — безопасное crypto-API

### Соседние разделы
- [[secrets-management|Secrets Management]]
- [[application-security|Application Security]]
- [Infrastructure Security](../infrastructure/)
- [[security-practices|Security Practices]]
- [[networks-basics|TLS и сертификаты]]

## Содержание

- [Классификация данных](#классификация-данных)
- [Шифрование при передаче (in transit)](#шифрование-при-передаче-in-transit)
- [Шифрование в покое (at rest)](#шифрование-в-покое-at-rest)
- [Хэширование паролей](#хэширование-паролей)
- [Симметричное и асимметричное шифрование](#симметричное-и-асимметричное-шифрование)
- [Ключевое управление (KMS)](#ключевое-управление-kms)
- [Маскирование и токенизация](#маскирование-и-токенизация)
- [Защита PII и GDPR](#защита-pii-и-gdpr)
- [Чек-лист](#чек-лист)
- [Частые ошибки](#частые-ошибки)

## Классификация данных

| Класс | Примеры | Требования |
|-------|---------|-----------|
| Public | маркетинг, каталоги | integrity |
| Internal | внутренние метрики, доки | access control |
| Confidential | финплан, код | encryption at rest, access logs |
| Restricted | PII, пароли, ключи, PCI | encryption at rest + in transit, KMS, audit, маскирование в логах |

**Правило минимизации:** не собирайте данных больше, чем нужно для решения задачи. Хранение — дополнительный риск.

## Шифрование при передаче (in transit)

- **TLS 1.2+** обязательно для всех внешних и внутренних каналов. TLS 1.3 — предпочтительно.
- Запрет устаревших шифров: SSLv3, TLS 1.0/1.1, RC4, 3DES, MD5-based MACs.
- HSTS для веба: `Strict-Transport-Security: max-age=31536000; includeSubDomains; preload`.
- **mTLS** для сервис-сервис: взаимная аутентификация по сертификатам.
- **Certificate pinning** для мобильных клиентов.

```bash
# Проверить TLS-конфигурацию сервера
openssl s_client -connect example.com:443 -tls1_3
nmap --script ssl-enum-ciphers -p 443 example.com

# Сертификат: срок, цепочка
openssl x509 -in cert.pem -text -noout
```

## Шифрование в покое (at rest)

**Уровни:**

- **Диск** — LUKS, BitLocker, FileVault; закрывает кражу физического носителя.
- **Файловая система** — eCryptfs, AWS EBS encryption.
- **База данных** — TDE (Transparent Data Encryption), column-level encryption.
- **Приложение** — шифрование конкретных полей перед записью в БД (envelope encryption).

**Рекомендация:** application-level encryption для самых чувствительных полей (номера карт, SSN, паспорта), дисковое/FS-шифрование — как baseline.

```java
// AES-256-GCM: authenticated encryption
byte[] key = new byte[32];  // из KMS, НЕ хардкод
SecureRandom.getInstanceStrong().nextBytes(key);

byte[] iv = new byte[12];   // GCM: 96-бит IV, уникальный на каждое шифрование
new SecureRandom().nextBytes(iv);

Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
GCMParameterSpec spec = new GCMParameterSpec(128, iv);
cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), spec);

byte[] ciphertext = cipher.doFinal(plaintext);
// Хранить: iv || ciphertext (IV не секретный, но должен быть уникальным)
```

**Никогда:**
- AES в режиме ECB — утечка паттернов.
- DES, 3DES, RC4 — устарели.
- Свой алгоритм — используйте проверенные библиотеки.
- Reuse IV в GCM — катастрофическая потеря конфиденциальности.

## Хэширование паролей

Требования: медленный KDF + соль + защита от brute-force.

| Алгоритм | Год | Рекомендация |
|----------|-----|-------------|
| MD5, SHA-1 | до 2010 | **НЕ использовать** |
| PBKDF2 | 2000 | legacy-совместимость, 600k+ итераций SHA-256 |
| bcrypt | 1999 | надёжно, cost ≥ 12 |
| scrypt | 2009 | хорош, но уступает Argon2 |
| **Argon2id** | 2015 | **рекомендуется OWASP** |

```java
// Argon2 через Spring Security
PasswordEncoder encoder = new Argon2PasswordEncoder(
    16,      // salt length
    32,      // hash length
    1,       // parallelism
    65536,   // memory (64 MB)
    3        // iterations
);
String hash = encoder.encode(password);
boolean matches = encoder.matches(rawPassword, hash);
```

**Соль:** уникальная на каждого пользователя, ≥16 байт, хранится рядом с хэшем. Argon2/bcrypt делают это автоматически.

**Pepper (опционально):** общий секрет приложения, добавляется к паролю перед хэшированием. Хранится отдельно от БД (KMS, переменная окружения).

## Симметричное и асимметричное шифрование

| Тип | Алгоритмы | Применение |
|-----|-----------|------------|
| Симметричное | AES-256-GCM, ChaCha20-Poly1305 | большие объёмы данных, скорость |
| Асимметричное | RSA-4096, ECDSA P-256, Ed25519 | обмен ключами, цифровая подпись |
| Гибрид | RSA + AES, ECIES | TLS, S/MIME, PGP |

**Правило:** асимметричным — устанавливаем канал или подписываем. Данные шифруем симметричным.

**Размеры ключей (NIST, минимум):**
- AES: 256 бит
- RSA: 3072 бит
- ECC: 256 бит (P-256, Curve25519)

## Ключевое управление (KMS)

**Envelope encryption:** данные шифруются DEK (data encryption key), DEK шифруется KEK (key encryption key), KEK живёт в KMS.

```text
plaintext --[AES DEK]--> ciphertext
DEK --[KMS KEK]--> encrypted DEK
Хранить: ciphertext + encrypted DEK
```

Плюсы:
- DEK разный для каждого объекта — компрометация одного не ломает остальные.
- Ротация KEK не требует перешифровки данных.
- Аудит доступа на уровне KMS.

**Популярные KMS:**
- AWS KMS, GCP KMS, Azure Key Vault
- HashiCorp Vault Transit engine
- Google Tink + KMS

**Ротация ключей:**
- KEK: раз в 1-2 года (или по регуляторке).
- DEK: на каждое новое данное (не переиспользуется).
- Паролей: политика 90-365 дней (но современные гайды: не менять без причины, лучше MFA).
- TLS-сертификаты: ≤398 дней, auto-rotate через Let's Encrypt/cert-manager.

Подробнее — [[secrets-management|Secrets Management]].

## Маскирование и токенизация

**Маскирование** — замена чувствительной части данных для отображения/логов:

```text
4532-1234-5678-9012 → 4532-****-****-9012
john.doe@example.com → j***@example.com
```

**Токенизация** — замена данных случайным токеном; соответствие хранится в защищённом vault. Токен сам по себе бесполезен. Применяется в PCI-окружениях.

**В логах запрещено:**
- Пароли (в любом виде)
- Полные номера карт, CVV
- JWT/API-токены
- PII без маскирования

```java
// Фильтр Logback для маскирования
public class SensitiveDataMasker extends MessageConverter {
    private static final Pattern CARD = Pattern.compile("\\b\\d{13,19}\\b");

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        return CARD.matcher(msg).replaceAll("****-****-****-$0")
                   .replaceAll("password=\\S+", "password=***");
    }
}
```

## Защита PII и GDPR

**PII (Personally Identifiable Information):** ФИО, email, телефон, паспорт, IP-адрес, cookie-id.

**Обязательства по GDPR:**
- **Право на доступ** (Art. 15): пользователь может запросить свои данные.
- **Право на удаление** (Art. 17, right to be forgotten): hard delete, а не soft.
- **Право на переносимость** (Art. 20): экспорт в машиночитаемом формате.
- **Data Breach Notification** (Art. 33): уведомление регулятора в 72 часа.
- **Privacy by Design** (Art. 25): минимизация с этапа проектирования.

**Практика:**

- Pseudonymization: замена идентификаторов синтетическими ID в аналитических витринах.
- Data retention: автоматическое удаление после N дней/месяцев.
- Access audit: логирование всех чтений PII с указанием «кто-что-когда-зачем».
- Right to delete: механизм каскадного удаления по user_id, включая бэкапы (часто — tombstone + truncate при восстановлении).

## Чек-лист

**Шифрование:**
- [ ] TLS 1.2+ на всех внешних эндпоинтах (HSTS включён)
- [ ] mTLS для service-to-service в закрытой сети
- [ ] Шифрование БД и бэкапов at-rest
- [ ] Application-level encryption для PII/PCI/PHI
- [ ] AES-256-GCM или ChaCha20-Poly1305 (не ECB, не CBC без MAC)

**Пароли:**
- [ ] Argon2id (или bcrypt cost≥12)
- [ ] Уникальная соль
- [ ] Минимум 8 символов, проверка по HIBP (Have I Been Pwned)
- [ ] MFA для привилегированных аккаунтов

**Ключи:**
- [ ] Секреты не в git (gitleaks, trufflehog в CI)
- [ ] KMS/Vault для prod-ключей
- [ ] Ротация KEK
- [ ] Разделение DEK на объект

**Логи и данные:**
- [ ] Маскирование чувствительных данных
- [ ] Retention-политика
- [ ] Audit-логи доступа к PII
- [ ] Регулярные backup-тесты восстановления

## Частые ошибки

| Ошибка | Последствие | Как исправить |
|--------|-------------|---------------|
| MD5/SHA-1 для паролей | rainbow tables за секунды | Argon2id |
| AES-ECB | утечка паттернов | AES-GCM |
| Reuse IV/nonce в GCM | полный взлом | случайный IV на каждое шифрование |
| Ключ в git | компрометация при форке | KMS/Vault + gitleaks в CI |
| Логирование пароля в stacktrace | утечка через SIEM | фильтрация, custom converter |
| Hardcoded secret в Docker-образе | утечка при публикации образа | build-time secrets, multi-stage |
| Неравная функция сравнения хэшей | timing attack | `MessageDigest.isEqual` или `Arrays.equals` с constant-time |
| Backup без шифрования | утечка через s3 bucket | encryption sse-kms |

## См. также

- [[secrets-management|Управление секретами (Secrets Management)]]
