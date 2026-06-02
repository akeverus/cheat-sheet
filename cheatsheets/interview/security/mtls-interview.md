---
title: "Вопросы на собеседовании: mTLS (Mutual TLS)"
description: "mTLS: mutual TLS authentication, certificates, X.509, PKI, certificate rotation, service mesh integration, mTLS в microservices, vs TLS, common pitfalls"
tags:
  - interview
  - security
  - mtls-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "mTLS"
  - "Mutual TLS"
  - "mTLS interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `mTLS (Mutual TLS)`

`mTLS (Mutual TLS)` — TLS handshake, где **обе стороны** проверяют identity друг друга (в отличие от обычного TLS, где проверяется только сервер). Фундамент для **Zero Trust**-сетей. Применяется в микросервисах (service mesh), B2B API, IoT, банковской сфере. Требует PKI-инфраструктуры для управления сертификатами.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [TLS 1.3 RFC 8446](https://datatracker.ietf.org/doc/html/rfc8446)
- [Mutual TLS Authentication — Cloudflare](https://www.cloudflare.com/learning/access-management/what-is-mutual-tls/)
- [SPIFFE/SPIRE — Identity standard](https://spiffe.io/)
- [cert-manager (K8s)](https://cert-manager.io/)
- [Istio mTLS Documentation](https://istio.io/latest/docs/concepts/security/#mutual-tls-authentication)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое mTLS?](#q1--что-такое-mtls)
- [Q2. (!) TLS vs mTLS — отличия?](#q2--tls-vs-mtls--отличия)
- [Q3. (!) Зачем mTLS?](#q3--зачем-mtls)

**Mechanism**
- [Q4. (!) mTLS handshake?](#q4--mtls-handshake)
- [Q5. X.509 certificates?](#q5-x509-certificates)
- [Q6. Certificate Authority (CA) hierarchy?](#q6-certificate-authority-ca-hierarchy)

**PKI**
- [Q7. (!) Public Key Infrastructure (PKI)?](#q7--public-key-infrastructure-pki)
- [Q8. Self-signed vs CA-signed?](#q8-self-signed-vs-ca-signed)
- [Q9. Certificate rotation?](#q9-certificate-rotation)
- [Q10. SPIFFE / SPIRE?](#q10-spiffe--spire)

**Implementations**
- [Q11. (!) mTLS в service mesh (Istio, Linkerd)?](#q11--mtls-в-service-mesh-istio-linkerd)
- [Q12. cert-manager (K8s)?](#q12-cert-manager-k8s)
- [Q13. Vault PKI?](#q13-vault-pki)
- [Q14. mTLS в Kafka, Redis, databases?](#q14-mtls-в-kafka-redis-databases)

**Production**
- [Q15. (!) Когда использовать mTLS?](#q15--когда-использовать-mtls)
- [Q16. Performance overhead?](#q16-performance-overhead)
- [Q17. (!) Common pitfalls?](#q17--common-pitfalls)
- [Q18. mTLS vs JWT?](#q18-mtls-vs-jwt)

**Advanced**
- [Q19. Certificate revocation (CRL, OCSP)?](#q19-certificate-revocation-crl-ocsp)
- [Q20. (!) Short-lived certificates?](#q20--short-lived-certificates)

## Q1. (!) Что такое mTLS?

**mTLS (Mutual TLS)** — модификация стандартного TLS handshake.

**Обычный TLS:**
- **Сервер** предъявляет сертификат
- **Клиент** проверяет сервер
- Клиент **НЕ** аутентифицируется через сертификат

**mTLS:**
- **И** сервер, **и** клиент предъявляют сертификаты
- **Обе** стороны проверяют друг друга
- **Взаимная аутентификация**

**Итог:** сервер **знает**, какой клиент к нему подключается (криптографически подтверждено, а не просто по IP/учётным данным).

## Q2. (!) TLS vs mTLS — отличия?

| Аспект | TLS | mTLS |
|--------|-----|------|
| Аутентификация сервера | ✓ | ✓ |
| Аутентификация клиента | ❌ (или другой механизм) | ✓ (через сертификат) |
| Шифрование | ✓ | ✓ |
| Сценарий | Публичный веб | Service-to-service, B2B |
| Управление сертификатами | Только сертификат сервера | Обе стороны |
| Сложность | Низкая | Высокая |
| Нужен PKI | Для CA | Полноценный PKI |

**TLS:** браузер к bank.com (у браузера нет сертификата).
**mTLS:** bank-A.com к bank-B.com (у обоих есть организационные сертификаты).

## Q3. (!) Зачем mTLS?

**Сценарии применения:**

1. **Service-to-service** в микросервисах (Zero Trust)
2. **B2B API** (высокоценные интеграции)
3. **IoT-устройства** (аутентификация устройств)
4. **Внутренние admin-инструменты** (дополнительный слой защиты)
5. **Банки, финансы** (регуляторный комплаенс)
6. **Здравоохранение** (HIPAA)
7. **Госсектор** (требования комплаенса)

**Преимущества:**
- **Сильная аутентификация** (криптографическая, а не по паролям)
- **Нет общих секретов**, которые можно утечь (нет API-ключей)
- **Подтверждённая identity сервиса**
- **Шифрование трафика** (за счёт TLS)
- **Аудит-след** (identity сертификата логируется)

## Q4. (!) mTLS handshake?

```
1. Client → Server: ClientHello (supported ciphers)
2. Server → Client: ServerHello + certificate + CertificateRequest
3. Client → Server: certificate + ClientKeyExchange + CertificateVerify
4. Both: derive session keys
5. Encrypted communication
```

**Отличия от TLS:**
- **CertificateRequest** (сервер запрашивает сертификат клиента)
- **CertificateVerify** (клиент подписывает handshake своим private key, доказывая владение сертификатом)

**Оба сертификата проверяются** по цепочке CA.

**TLS 1.3 упрощает** handshake (1-RTT против 2-RTT в TLS 1.2).

## Q5. X.509 certificates?

**X.509** — стандартный формат сертификатов с открытым ключом.

**Сертификат содержит:**
- **Subject** (кому принадлежит)
- **Issuer** (кто подписал — CA)
- **Public key** (открытый ключ)
- **Serial number** (серийный номер)
- **Срок действия** (notBefore, notAfter)
- **Расширения** (SAN — Subject Alternative Names, key usage и т.д.)
- **Подпись** (подпись CA)

**Формат:** PEM (base64-текст), DER (бинарный).

```
-----BEGIN CERTIFICATE-----
MIIDXTCCAkWgAwIBAgIJAK...
-----END CERTIFICATE-----
```

**Проверка:** валидация подписи с помощью открытого ключа CA.

## Q6. Certificate Authority (CA) hierarchy?

```
Root CA (self-signed, trust anchor)
  ├── Intermediate CA 1 (signed by Root)
  │   ├── Server cert 1
  │   └── Server cert 2
  └── Intermediate CA 2
      └── Server cert 3
```

**Root CA:**
- **Точка доверия** (предустановлен в браузерах и ОС)
- **Self-signed** (самоподписанный)
- **Хранится офлайн** ради безопасности

**Intermediate CA (промежуточные):**
- Подписаны Root
- Используются для **выпуска конечных сертификатов**
- **Упрощают отзыв** (отзываем Intermediate, а не Root)

**Цепочка доверия:** проверяем сертификат → проверяем Issuer (Intermediate) → проверяем, что он подписан Root → доверяем.

## Q7. (!) Public Key Infrastructure (PKI)?

**PKI** — инфраструктура для управления сертификатами.

**Компоненты:**
- **CA** — выпускает сертификаты
- **Registration Authority (RA)** — проверяет identity перед выпуском
- **Certificate Repository** — хранилище сертификатов
- **Validation Authority (VA)** — обрабатывает запросы об отзыве (CRL, OCSP)

**Задачи PKI:**
- Выпуск сертификатов
- Отзыв скомпрометированных
- Ротация (продление)
- Аудит и логирование

**Внутренний PKI:**
- HashiCorp Vault PKI engine
- step-ca (smallstep)
- Microsoft AD CS
- Cloudflare PKI Toolkit
- Самописный (скрипты на OpenSSL)

**Публичный PKI:**
- Let's Encrypt (бесплатно)
- DigiCert, Sectigo, GoDaddy (платно)

## Q8. Self-signed vs CA-signed?

**Self-signed (самоподписанный):**
- ✅ Бесплатно, быстро
- ❌ Браузеры и клиенты по умолчанию не доверяют
- **Годится для:** dev, внутренних инструментов (с ручной настройкой доверия)

**CA-signed (подписанный CA):**
- ✅ Доверие автоматически
- ❌ Стоимость (либо бесплатный Let's Encrypt)
- **Обязателен для:** публично доступных приложений

**Для mTLS** обычно используют **внутренний CA** (собственный PKI). Устройства и сервисы доверяют вашему CA, а не публичным CA.

## Q9. Certificate rotation?

**Сертификаты истекают.** Их нужно **продлевать** до окончания срока.

**Боль ручной ротации:**
- Забыл → сертификат истёк → простой
- Много серверов → ротация по очереди
- Риск даунтайма

**Автоматическая ротация:**
- **Let's Encrypt + cert-manager** (веб-сертификаты)
- **Vault PKI + sidecar** (сертификаты сервисов)
- **Service mesh** (Istio авторотирует каждые 24 ч)

**Лучшие практики:**
- **Короткие TTL** (24 часа, дни, а не годы)
- **Авторотация**
- **Алертинг** на приближение срока истечения

## Q10. SPIFFE / SPIRE?

**SPIFFE (Secure Production Identity Framework For Everyone)** — открытый стандарт для **identity сервисов**.

**SVID (SPIFFE Verifiable Identity Document):**
- X.509-сертификат с SPIFFE ID
- Формат: `spiffe://trust-domain/path/to/service`

**Пример:**
```
spiffe://example.org/ns/payments/sa/payment-service
```

**SPIRE** — эталонная реализация. Выпускает SVID на основе аттестации workload (K8s ServiceAccount, AWS IAM и т.д.).

**Интегрируется с:**
- Istio
- Envoy
- Vault
- AWS, GCP, K8s

**Стандарт identity сервисов** в Zero Trust.

## Q11. (!) mTLS в service mesh (Istio, Linkerd)?

**Service mesh — автоматический mTLS.**

**Istio:**
```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT  # require mTLS
```

**Linkerd:** автоматически, включён по умолчанию.

**Как работает:**
- Каждый pod получает сертификат (подписан CA меша)
- TTL: 24 часа, авторотация
- Identity = ServiceAccount
- TLS обрабатывают sidecar-прокси

**Приложения видят обычный HTTP.** Sidecar добавляет mTLS прозрачно.

**Эффект:** аутентификация service-to-service без конфигурации.

Подробнее — [Istio](../devops/istio-service-mesh-interview.md), [Linkerd](../devops/linkerd-interview.md).

## Q12. cert-manager (K8s)?

**cert-manager** — K8s-контроллер для управления сертификатами.

```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: example-com
spec:
  secretName: example-com-tls
  issuerRef:
    name: letsencrypt-prod
    kind: ClusterIssuer
  dnsNames:
    - example.com
    - www.example.com
```

**Issuer-ы:**
- **Let's Encrypt** (веб-сертификаты, бесплатно)
- **Vault** PKI
- **Self-signed**
- **CA-issued** (выпущенные CA)
- **Внешние issuer-ы**

**Автопродление** до истечения срока. Хранение в K8s Secret.

**Интегрируется с Istio** для mTLS-сертификатов.

## Q13. Vault PKI?

**Vault PKI engine** — выпуск X.509-сертификатов.

```bash
# Setup PKI engine
vault secrets enable pki
vault write pki/root/generate/internal common_name="my-ca" ttl=87600h

# Define role
vault write pki/roles/my-role \
  allowed_domains=example.com \
  allow_subdomains=true \
  max_ttl=24h

# Issue cert
vault write pki/issue/my-role \
  common_name=api.example.com \
  ttl=24h
# Returns cert + key
```

**Сценарий применения:**
- Внутренний CA для mTLS
- Короткоживущие сертификаты (1–24 часа)
- Программный выпуск
- Удобно для автоматизации

Подробнее — [Vault](../devops/vault-interview.md).

## Q14. mTLS в Kafka, Redis, databases?

**Большинство систем хранения данных** поддерживают mTLS.

**Kafka:**
```properties
# server.properties
listeners=SSL://:9093
ssl.client.auth=required
ssl.keystore.location=/path/keystore.jks
ssl.truststore.location=/path/truststore.jks
```

**Redis 6+:**
```
tls-port 6379
tls-cert-file /path/cert.pem
tls-key-file /path/key.pem
tls-ca-cert-file /path/ca.pem
tls-auth-clients yes
```

**PostgreSQL:**
```conf
ssl = on
ssl_cert_file = '/path/server.crt'
ssl_key_file = '/path/server.key'
ssl_ca_file = '/path/ca.crt'
```

**MongoDB, Elasticsearch, MySQL** — все поддерживают mTLS.

**Сценарий применения:** защищённые соединения приложения с хранилищами данных, предотвращение несанкционированного доступа.

## Q15. (!) Когда использовать mTLS?

**Используйте mTLS, когда:**
- **Service-to-service** в микросервисах (Zero Trust)
- **B2B API** (высокоценные, партнёрские интеграции)
- **IoT** (аутентификация устройств)
- **Внутренние чувствительные API** (admin-инструменты)
- **Комплаенс** (PCI, HIPAA, банкинг)
- **Межрегиональный** защищённый трафик

**Не используйте, когда:**
- Публичные потребительские API (используйте OAuth, API-ключи)
- Мобильные приложения (раздача сертификатов сложна)
- Быстрые прототипы (эксплуатационные накладные расходы)

**Современный подход:** **service mesh** для внутреннего mTLS, OAuth — для внешних клиентов.

## Q16. Performance overhead?

**Накладные расходы на запрос:**
- TLS handshake: ~50–100 мс (один раз на соединение)
- mTLS handshake: ~100–200 мс (дополнительная проверка сертификата)
- Шифрование байтов: ~5–10% накладных расходов на CPU

**Оптимизация:**
- **Пулинг соединений** (переиспользование TLS-сессий)
- **Возобновление сессий** (TLS 1.3 0-RTT)
- **Мультиплексирование HTTP/2** (одно соединение, много запросов)
- **Аппаратное ускорение** (AES-NI)

**В service mesh** — handshake **один раз на соединение** между sidecar-ами (долгоживущими). Приложение-sidecar — localhost (без TLS).

**Итоговые накладные расходы** в проде: ~3–5% к latency.

## Q17. (!) Common pitfalls?

1. **Нет ротации сертификатов** — сертификаты истекают, простой
2. **Долгоживущие сертификаты** (на годы) — риск безопасности
3. **Self-signed без настройки доверия** — клиенты отклоняют
4. **Отсутствуют промежуточные сертификаты** в цепочке
5. **Неправильный сертификат** для hostname (несоответствие CN/SAN)
6. **Рассинхрон часов (clock skew)** — сертификат «ещё не действителен»
7. **Нет механизма отзыва** (скомпрометированный сертификат валиден до истечения срока)
8. **Сложная раздача сертификатов** (мобильные, публичные клиенты)
9. **Утечка private key** (закоммичен в git, засветился в логах)
10. **Тяжело отлаживать** (TLS-ошибки непрозрачны)

**Инструменты:** `openssl s_client`, `curl -v`, Wireshark для отладки.

## Q18. mTLS vs JWT?

| Аспект | mTLS | JWT |
|--------|------|-----|
| Аутентификация | Криптографический сертификат | Bearer-токен |
| Уровень | Транспортный (L4–L5) | Прикладной (L7) |
| Identity | Subject сертификата | Claims в токене |
| Ротация | Продление сертификата | Истечение токена |
| Отзыв | CRL/OCSP (сложно) | Короткий TTL + blocklist |
| Мобильные | Сложно (установка сертификата) | Легко (HTTP-заголовок) |
| Браузер | Сложно (установка сертификата) | Легко (cookie) |
| Service-to-service | **Отлично** | Хорошо |
| Для пользователей | Затруднительно | **Отлично** |

**Часто комбинируют:**
- **mTLS** между сервисами (Zero Trust)
- **JWT** для проброса user identity через сервисы

## Q19. Certificate revocation (CRL, OCSP)?

**Сертификат скомпрометирован** до истечения срока → его нужно **отозвать**.

**CRL (Certificate Revocation List):**
- CA публикует список отозванных сертификатов
- Клиенты периодически скачивают его
- **Медленно** (большие списки, редкие обновления)
- Проблема приватности (CA знает, кто проверяет)

**OCSP (Online Certificate Status Protocol):**
- Проверка в реальном времени (HTTP-запрос к OCSP-респондеру)
- Быстрее
- **Приватность** (CA знает о проверках)
- **OCSP Stapling** — сервер прикладывает подписанный OCSP-ответ к сертификату (без отдельного запроса)

**Современная альтернатива: короткоживущие сертификаты** (отзыв не нужен — они просто быстро истекают).

## Q20. (!) Short-lived certificates?

**Короткоживущие сертификаты:**
- TTL: часы / минуты (против традиционных лет)
- **Автопродление** до истечения срока
- **Отзыв не нужен** (просто быстро истекают)

**Примеры:**
- **Istio:** TTL 24 ч (по умолчанию)
- **SPIRE:** настраивается, часто 1–24 ч
- **Vault PKI:** любой TTL

**Компромисс:**
- ✅ Короткое окно компрометации (часы, а не годы)
- ✅ Не нужна инфраструктура отзыва
- ❌ Высокая нагрузка на выпуск (много операций с сертификатами в секунду)
- ❌ Требует автоматизации (ручная ротация невозможна)

**Современный паттерн:** PKI-инфраструктура с автовыпуском короткоживущих сертификатов.

В **2025** mTLS — **стандарт** для production-микросервисов. Service mesh делает его прозрачным.

---

## See also

- [Zero Trust](zero-trust-interview.md) — mTLS foundation
- [Secrets Management](secrets-management-interview.md) — cert storage
- [Supply Chain Security](supply-chain-security-interview.md)
- [Application Security](application-security-interview.md) — общая
- [TLS / SSL](tls-ssl-interview.md) — TLS basics
- [OAuth2](oauth2-interview.md) — alternative for users
- [JWT](jwt-interview.md) — combined с mTLS
- [Istio](../devops/istio-service-mesh-interview.md) — auto mTLS
- [Linkerd](../devops/linkerd-interview.md) — auto mTLS
- [Vault](../devops/vault-interview.md) — PKI engine
- [Микросервисы](../architecture/microservices-interview.md) — context
- [Networking](../architecture/networking-interview.md) — protocol layers
- [Kubernetes](../devops/kubernetes-interview.md) — cert-manager

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
- [Secrets Management](secrets-management-interview.md)
