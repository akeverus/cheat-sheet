---
title: "Вопросы на собеседовании: Zero Trust"
description: "Zero Trust security: principles, BeyondCorp (Google), micro-segmentation, identity-centric, mTLS, never trust always verify, NIST 800-207, vs perimeter security"
tags:
  - interview
  - security
  - zero-trust-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Zero Trust"
  - "Zero Trust interview"
  - "Zero Trust security"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Zero Trust`

`Zero Trust` — модель безопасности: **никому не доверяй, всегда проверяй**. Каждый запрос аутентифицируется и авторизуется независимо от расположения в сети. **Не периметровая модель** (firewall + VPN). Первой реализовала Google в **BeyondCorp** (2014). NIST 800-207 — официальный фреймворк. Стала мейнстримом после удалёнки в пандемию COVID.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [NIST SP 800-207 Zero Trust Architecture](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-207.pdf)
- [Google BeyondCorp Papers](https://cloud.google.com/beyondcorp)
- [CISA Zero Trust Maturity Model](https://www.cisa.gov/zero-trust-maturity-model)
- [The Forrester Wave: Zero Trust](https://www.forrester.com/blogs/category/zero-trust/)
- [Zero Trust — Cloudflare](https://www.cloudflare.com/learning/security/glossary/what-is-zero-trust/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Zero Trust?](#q1--что-такое-zero-trust)
- [Q2. (!) Perimeter security vs Zero Trust?](#q2--perimeter-security-vs-zero-trust)
- [Q3. (!) Принципы Zero Trust?](#q3--принципы-zero-trust)

**Architecture**
- [Q4. (!) Identity-centric security?](#q4--identity-centric-security)
- [Q5. (!) Micro-segmentation?](#q5--micro-segmentation)
- [Q6. Policy Decision Point / Enforcement Point?](#q6-policy-decision-point--enforcement-point)
- [Q7. NIST 800-207 components?](#q7-nist-800-207-components)

**Implementation**
- [Q8. (!) Google BeyondCorp model?](#q8--google-beyondcorp-model)
- [Q9. (!) Identity Provider (IdP) role?](#q9--identity-provider-idp-role)
- [Q10. (!) mTLS as Zero Trust foundation?](#q10--mtls-as-zero-trust-foundation)
- [Q11. Service mesh roles (Istio, Linkerd)?](#q11-service-mesh-roles-istio-linkerd)

**ZTNA (Zero Trust Network Access)**
- [Q12. (!) ZTNA vs VPN?](#q12--ztna-vs-vpn)
- [Q13. ZTNA vendors (Cloudflare, Zscaler, Palo Alto)?](#q13-ztna-vendors-cloudflare-zscaler-palo-alto)

**Practical**
- [Q14. (!) Implementing Zero Trust — где starting point?](#q14--implementing-zero-trust--где-starting-point)
- [Q15. Continuous verification?](#q15-continuous-verification)
- [Q16. Device posture?](#q16-device-posture)

**Production**
- [Q17. (!) Какие частые ошибки при adoption?](#q17--какие-частые-ошибки-при-adoption)
- [Q18. ROI и trade-offs?](#q18-roi-и-trade-offs)
- [Q19. (!) Zero Trust для service-to-service?](#q19--zero-trust-для-service-to-service)

## Q1. (!) Что такое Zero Trust?

**Zero Trust** — модель безопасности. Лозунг: **«Никому не доверяй, всегда проверяй»**.

**Ключевая идея:** **доверие никогда** не подразумевается по умолчанию (даже из «внутренней» сети). Каждый запрос:
- Аутентифицирован (кто?)
- Авторизован (что разрешено?)
- Зашифрован (в транзите и в покое)

**Доверие не выдаётся** на основании того, что:
- Клиент находится внутри сетевого периметра
- У клиента есть доступ через VPN
- Клиент работает с корпоративного ноутбука

**Каждое взаимодействие** проверяется независимо.

## Q2. (!) Perimeter security vs Zero Trust?

**Периметровая безопасность (legacy):**
```
Internet → Firewall → Internal Network → Trusted (everything inside)
```
- Прочная **внешняя стена**
- **Мягкая середина** (после VPN/firewall почти всё доверенное)
- **Латеральное перемещение** простое после взлома

**Zero Trust:**
```
Каждый request → Verify → Authorize → Allow specific action
```
- **Никакого «внутри»**, которое считалось бы безопасным
- Каждое соединение аутентифицируется и авторизуется
- **Латеральное перемещение** блокируется по умолчанию

**Почему произошёл сдвиг:**
- Облако (периметра нет)
- Удалённая работа (сотрудники вне сети)
- BYOD
- Внутренние угрозы (insider threats)
- Громкие утечки (Snowden, Target, Equifax)

## Q3. (!) Принципы Zero Trust?

**Ключевые принципы (NIST 800-207):**

1. **Все ресурсы, доступные независимо от расположения**, требуют аутентификации
2. **Все коммуникации защищены** независимо от сети
3. **Доступ выдаётся на уровне сессии** (не постоянный)
4. **Доступ динамический** на основе политики (пользователь, устройство, время, поведение)
5. **Непрерывный мониторинг** всех активов
6. **Строгие аутентификация и авторизация** (не доверяй, проверяй)
7. **Сбор данных** для улучшения уровня безопасности
8. **Никакого неявного доверия** на основании сетевого расположения

## Q4. (!) Identity-centric security?

**Identity = новый периметр.**

**Раньше:** доверие на основе **IP / сети**.
**Теперь:** доверие на основе **identity** (кто ты).

**Identity:**
- **Пользователь** (сотрудник, подрядчик)
- **Устройство** (ноутбук, телефон)
- **Сервис** (микросервис)
- **Workload** (под, контейнер)

**Каждая identity:**
- Сильная аутентификация (MFA, сертификаты)
- Гранулярная авторизация (least privilege)
- Аудит (все действия логируются)
- Отзываемость (мгновенное снятие доступа)

## Q5. (!) Micro-segmentation?

**Micro-segmentation** — деление сети на **мелкие зоны** с контролем трафика между ними.

**Старый подход:** плоская сеть, кто угодно соединяется с кем угодно.
**С микросегментацией:** каждый сервис может общаться только с явно одобренными сервисами.

```
WebApp ↛ Database (denied — direct access)
WebApp → API Service → Database (allowed via API)
```

**Реализации:**
- **Network policies** в K8s
- **Service mesh** (Istio AuthorizationPolicy)
- **Cloud security groups** (гранулярные)
- **Software-defined perimeter (SDP)**

**Эффект:** взлом одного сервиса → ограниченный радиус поражения (blast radius).

## Q6. Policy Decision Point / Enforcement Point?

**PDP (Policy Decision Point):**
- Оценивает запросы доступа
- «Должен ли пользователь X получить доступ к ресурсу Y?»
- Возвращает: allow / deny

**PEP (Policy Enforcement Point):**
- Перехватывает запросы
- Спрашивает PDP
- Применяет решение

```
User → PEP (gateway) → PDP (policy engine) → "allowed"
                                            → "denied"
PEP allows or denies based on PDP response.
```

**Примеры:**
- **OPA (Open Policy Agent)** — популярный PDP
- **API Gateway** — типовой PEP
- **Service mesh sidecars** — PEP-ы (и частично PDP-ы)

## Q7. NIST 800-207 components?

**Логические компоненты:**

1. **Policy Engine (PE)** — принимает решение о доступе (PDP)
2. **Policy Administrator (PA)** — управляет доступом
3. **Policy Enforcement Point (PEP)** — применяет решение

**Источники данных:**
- Identity Management (IdM)
- Continuous Diagnostics and Mitigation (CDM)
- Отраслевой compliance
- Threat intelligence
- Логи активности
- Политика доступа к данным
- PKI (сертификаты)
- ID management
- SIEM

**Всё вместе** даёт контекстно-зависимые решения о доступе.

## Q8. (!) Google BeyondCorp model?

**BeyondCorp** (Google, 2014) — пионер реализации Zero Trust.

**Катализатор:** Google взломали в 2010 (Operation Aurora) → осознали, что периметра недостаточно.

**Подход:**
- **Без VPN** для сотрудников
- **Все приложения** доступны через интернет (с аутентификацией)
- **Доверие на основе:** identity пользователя + identity устройства (управляемое устройство с сертификатом)
- **Многоуровневый доступ** в зависимости от контекста (расположение, device posture)

**Результат:** сотрудники Google работают откуда угодно и при этом безопасно.

**Вдохновил** всю индустрию Zero Trust. Большинство ZTNA-продуктов копируют BeyondCorp.

## Q9. (!) Identity Provider (IdP) role?

**IdP** — центральный авторитет по identity.

**Примеры:**
- Okta
- Azure Entra ID (formerly Azure AD)
- Google Workspace
- Auth0
- Ping Identity
- AWS IAM Identity Center

**Предоставляет:**
- **SSO** — единый вход, множество приложений
- **Принудительный MFA**
- **Жизненный цикл пользователя** (provisioning, deprovisioning)
- Политики **conditional access**
- Протоколы **OIDC / SAML**

**Фундамент Zero Trust:** без сильной identity реализовать невозможно.

## Q10. (!) mTLS as Zero Trust foundation?

**mTLS (mutual TLS)** — и клиент, и сервер проверяют сертификаты друг друга.

**Zero Trust для сервисов:**
- **Identity сервиса = сертификат**
- mTLS аутентифицирует каждое соединение
- **Авторотация** сертификатов (короткоживущие)
- **Без shared secrets** (нет API-ключей, которые можно утечь)

**Service mesh** (Istio, Linkerd) — автоматически включает mTLS для всего service-to-service-трафика.

**Vault PKI** — выпускает короткоживущие сертификаты.

Подробнее — в [mTLS](mtls-interview.md).

## Q11. Service mesh roles (Istio, Linkerd)?

**Service mesh** — то, что делает Zero Trust возможным:
- **mTLS** автоматически
- **AuthorizationPolicies** (авторизация service-to-service)
- **Observability** (аудит-логи каждого соединения)
- **Identity = service account**

```yaml
# Istio AuthorizationPolicy
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: api-from-web-only
spec:
  selector:
    matchLabels:
      app: api
  rules:
    - from:
        - source:
            principals: ["cluster.local/ns/default/sa/web"]
```

**Вариант default-deny:**
```yaml
spec:
  {}  # empty = deny all
# Then explicit allows
```

Подробнее — [Istio](../devops/istio-service-mesh-interview.md), [Linkerd](../devops/linkerd-interview.md).

## Q12. (!) ZTNA vs VPN?

**VPN (legacy):**
- User → VPN tunnel → внутренняя сеть → доступны все ресурсы
- **Неявное доверие** после подключения по VPN
- Обход = полный доступ к сети
- Медленно (лишний hop), единая точка отказа

**ZTNA (Zero Trust Network Access):**
- User → ZTNA gateway → конкретное приложение
- **Никакого доступа к сети** — только к конкретным приложениям
- Авторизация на уровне сессии
- Быстрее (часто прямое соединение через брокер)

**Область ZTNA:** доступ на уровне приложений, а не сети.

**Тренд:** ZTNA быстро растёт, VPN сдаёт позиции.

## Q13. ZTNA vendors (Cloudflare, Zscaler, Palo Alto)?

**Cloudflare Zero Trust** (раньше Cloudflare Access):
- Простая настройка
- Доступ через браузер
- Туннели (без входящих портов)

**Zscaler Private Access (ZPA):**
- Ориентирован на enterprise
- Комплексный продукт
- Дорогой

**Palo Alto Prisma Access:**
- Сетецентричный (legacy-корни)
- Комплексный

**Tailscale, Twingate:**
- Современные, удобные для разработчиков
- На базе WireGuard
- Популярны в SMB / стартапах

**Выбор:** зависит от масштаба, бюджета и уже имеющихся отношений с вендорами.

## Q14. (!) Implementing Zero Trust — где starting point?

**Дорожная карта:**

**Фаза 1 — Фундамент:**
- Сильная **identity** (IdP, MFA)
- Инвентаризация устройств, пользователей, приложений
- Классификация чувствительности данных

**Фаза 2 — Доступ на основе identity:**
- SSO для всех приложений
- Политики conditional access
- Privileged access management

**Фаза 3 — Сеть:**
- Замена VPN на ZTNA
- Микросегментация
- Мониторинг сети

**Фаза 4 — Workload:**
- Service mesh (mTLS)
- AuthorizationPolicies
- Secrets management

**Фаза 5 — Непрерывность:**
- Поведенческая аналитика
- Интеграция с SIEM
- Continuous verification

**Путь на годы.** Начинать с малого и расширять.

## Q15. Continuous verification?

**Перепроверять** identity и контекст **непрерывно**, а не один раз и навсегда.

**Триггеры повторной аутентификации:**
- Истечение времени (каждые N часов)
- Смена расположения
- Изменение device posture
- Аномальное поведение
- Чувствительная операция

**Эффект:**
- Скомпрометированная сессия обнаруживается
- Уменьшенный радиус поражения
- Более сильные гарантии

**Инструменты:** UEBA (User Entity Behavior Analytics), risk-based authentication.

## Q16. Device posture?

**Device posture** = состояние безопасности устройства.

**Проверки:**
- ОС с актуальными патчами?
- Запущен антивирус?
- Зашифрован ли диск?
- Соответствует ли требованиям (управляемое устройство)?
- Не jailbroken / не rooted?

**Conditional access** на основе posture:
```
Healthy device → full access
Unhealthy device → limited access (read-only, no sensitive data)
Compromised device → block
```

**MDM (Mobile Device Management)** интегрируется с IdP для передачи данных о posture.

## Q17. (!) Какие частые ошибки при adoption?

1. **Покупка «продукта Zero Trust»** в надежде, что он решит всё
2. **Big-bang-подход** (попытка «вскипятить океан» — никогда не завершается)
3. **Слабый фундамент identity** (нет MFA, нет SSO)
4. **Нет инвентаризации** активов
5. **Игнорирование service-to-service** (фокус только на user-to-app)
6. **Плохой UX** (слишком много запросов на аутентификацию → пользователи ищут обходные пути)
7. **Нет continuous verification**
8. **Мышление только про сеть** (mTLS без identity)
9. **Недостаточное логирование** (нечем анализировать взлом)
10. **Vendor lock-in** (ставка целиком на один продукт)

## Q18. ROI и trade-offs?

**Преимущества:**
- **Снижение ущерба от взлома** (ограниченное латеральное перемещение)
- **Compliance** проще
- **Удалённая работа** безопасна
- **Лучшие audit trails**
- Снижение **внутренних угроз** (insider threat)

**Издержки:**
- Дорогие инструменты
- Инженерное время
- Трение для пользователей (на старте)
- Накладные расходы на производительность (проверки аутентификации на каждый запрос)
- Сложный troubleshooting

**Окупается** для крупных организаций или регулируемых отраслей (финансы, здравоохранение).

## Q19. (!) Zero Trust для service-to-service?

**Zero Trust для микросервисов:**

1. **Identity на каждый сервис** (ServiceAccount, SPIFFE)
2. **Автоматический mTLS** (service mesh)
3. **AuthorizationPolicies** (allow-листы)
4. **Default-deny** network policies
5. **Audit-логи** всех вызовов
6. **Короткоживущие credentials** (JWT, сертификаты)
7. **Secrets management** (Vault, а не env vars)
8. **Частая ротация** credentials

**Стек инструментов:**
- **Service mesh** (Istio, Linkerd) — mTLS, авторизация
- **SPIFFE/SPIRE** — стандарт service identity
- **Vault** — секреты
- **OPA** — policy engine
- **K8s NetworkPolicies** — сегментация сети

**Современные микросервисы** = Zero Trust по умолчанию (с правильными инструментами).

---

## See also

- [mTLS](mtls-interview.md) — foundation
- [Secrets Management](secrets-management-interview.md) — context
- [Supply Chain Security](supply-chain-security-interview.md)
- [Application Security](application-security-interview.md) — общая
- [OAuth2](oauth2-interview.md) — auth flows
- [JWT](jwt-interview.md) — tokens
- [Istio](../devops/istio-service-mesh-interview.md) — service mesh ZT
- [Linkerd](../devops/linkerd-interview.md) — service mesh ZT
- [Vault](../devops/vault-interview.md) — secrets для ZT
- [Микросервисы](../architecture/microservices-interview.md) — context
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — context
- [Kubernetes](../devops/kubernetes-interview.md) — NetworkPolicies
- [Authentication & Authorization](authentication-authorization-patterns-interview.md)

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
