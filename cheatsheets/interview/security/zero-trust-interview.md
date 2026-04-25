---
title: "Вопросы на собеседовании: Zero Trust"
description: "Zero Trust security: principles, BeyondCorp (Google), micro-segmentation, identity-centric, mTLS, never trust always verify, NIST 800-207, vs perimeter security"
tags:
  - interview
  - security
  - zero-trust-interview
aliases:
  - "Zero Trust interview"
  - "Zero Trust security"
  - "BeyondCorp interview"
  - "Zero Trust собеседование"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Zero Trust`

`Zero Trust` — security model: **never trust, always verify**. Каждый request authenticated и authorized независимо от network location. **Не perimeter-based** (firewall + VPN). Pioneered Google **BeyondCorp** (2014). NIST 800-207 — official framework. Mainstream после COVID remote work.

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

**Zero Trust** — security model. Slogan: **"Never trust, always verify"**.

**Core idea:** **trust никогда** не assumed (даже от "internal" network). Каждый request:
- Authenticated (who?)
- Authorized (what allowed?)
- Encrypted (in transit, at rest)

**No assumed trust** based on:
- Being inside network perimeter
- Having VPN access
- Being on company laptop


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Каждый interaction** verified independently.

## Q2. (!) Perimeter security vs Zero Trust?

**Perimeter security (legacy):**
```
Internet → Firewall → Internal Network → Trusted (everything inside)
```
- Strong **outer wall**
- **Soft inside** (after VPN/firewall, mostly trusted)
- **Lateral movement** easy после breach

**Zero Trust:**
```
Каждый request → Verify → Authorize → Allow specific action
```
- **No "inside"** assumed safe
- Каждое connection authenticated/authorized
- **Lateral movement** prevented by default


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Why shift:**
- Cloud (no perimeter)
- Remote work (employees outside network)
- BYOD
- Insider threats
- High-profile breaches (Snowden, Target, Equifax)

## Q3. (!) Принципы Zero Trust?

**Core principles (NIST 800-207):**


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
1. **All resources accessed regardless of location** require authentication
2. **All communications secured** regardless of network
3. **Access granted на per-session basis** (not permanent)
4. **Access dynamic** based on policy (user, device, time, behavior)
5. **Continuous monitoring** of all assets
6. **Authentication + authorization strict** (don't trust, verify)
7. **Collect data** для improving security posture
8. **No implicit trust** based on network location

## Q4. (!) Identity-centric security?

**Identity = new perimeter.**

**Old:** trust based on **IP / network**.
**New:** trust based on **identity** (who you are).

**Identity:**
- **User** (employee, contractor)
- **Device** (laptop, phone)
- **Service** (microservice)
- **Workload** (pod, container)


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Each identity:**
- Strong authentication (MFA, certificates)
- Granular authorization (least privilege)
- Audited (all actions logged)
- Revocable (instant access removal)

## Q5. (!) Micro-segmentation?

**Micro-segmentation** — divide network в **small zones**, control traffic между zones.

**Old approach:** flat network, anyone connects к anyone.
**Micro-segmented:** each service can communicate только с explicit approved services.

```
WebApp ↛ Database (denied — direct access)
WebApp → API Service → Database (allowed via API)
```

**Implementations:**
- **Network policies** в K8s
- **Service mesh** (Istio AuthorizationPolicy)
- **Cloud security groups** (granular)
- **Software-defined perimeter (SDP)**


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Effect:** breach of one service → limited blast radius.

## Q6. Policy Decision Point / Enforcement Point?

**PDP (Policy Decision Point):**
- Evaluates access requests
- "Should user X access resource Y?"
- Returns: allow / deny

**PEP (Policy Enforcement Point):**
- Intercepts requests
- Asks PDP
- Enforces decision

```
User → PEP (gateway) → PDP (policy engine) → "allowed"
                                            → "denied"
PEP allows or denies based on PDP response.
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Examples:**
- **OPA (Open Policy Agent)** — popular PDP
- **API Gateway** — common PEP
- **Service mesh sidecars** — PEPs (and partial PDPs)

## Q7. NIST 800-207 components?

**Logical components:**

1. **Policy Engine (PE)** — decides access (PDP)
2. **Policy Administrator (PA)** — manages access
3. **Policy Enforcement Point (PEP)** — enforces decision

**Data sources:**
- Identity Management (IdM)
- Continuous Diagnostics and Mitigation (CDM)
- Industry compliance
- Threat intelligence
- Activity logs
- Data access policy
- PKI (certificates)
- ID management
- SIEM


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**All combined** для context-aware access decisions.

## Q8. (!) Google BeyondCorp model?

**BeyondCorp** (Google, 2014) — pioneer Zero Trust implementation.

**Catalyst:** Google hacked в 2010 (Operation Aurora) → realized perimeter не enough.

**Approach:**
- **No VPN** для employees
- **All apps** accessible через Internet (with auth)
- **Trust based on:** user identity + device identity (managed device with cert)
- **Tiered access** based on context (location, device posture)

**Result:** Google employees work from anywhere, securely.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Inspired:** entire Zero Trust industry. Most ZTNA products mimic BeyondCorp.

## Q9. (!) Identity Provider (IdP) role?

**IdP** — central identity authority.

**Examples:**
- Okta
- Azure Entra ID (formerly Azure AD)
- Google Workspace
- Auth0
- Ping Identity
- AWS IAM Identity Center

**Provides:**
- **SSO** — single login, multiple apps
- **MFA enforcement**
- **User lifecycle** (provisioning, deprovisioning)
- **Conditional access** policies
- **OIDC / SAML protocols**


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Foundation Zero Trust:** without strong identity, can't implement.

## Q10. (!) mTLS as Zero Trust foundation?

**mTLS (mutual TLS)** — both client и server verify certificates.

**Zero Trust для services:**
- **Service identity = certificate**
- mTLS authenticates каждый connection
- **Auto-rotated** certs (short-lived)
- **No shared secrets** (no API keys to leak)

**Service mesh** (Istio, Linkerd) — auto-implements mTLS для все service-to-service.

**Vault PKI** — issues short-lived certs.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
Подробнее — в [mTLS](mtls-interview.md).

## Q11. Service mesh roles (Istio, Linkerd)?

**Service mesh** — Zero Trust enabler:
- **mTLS** automatic
- **AuthorizationPolicies** (service-to-service authz)
- **Observability** (audit logs of every connection)
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

**Default-deny option:**
```yaml
spec:
  {}  # empty = deny all
# Then explicit allows
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
Подробнее — [Istio](../devops/istio-service-mesh-interview.md), [Linkerd](../devops/linkerd-interview.md).

## Q12. (!) ZTNA vs VPN?

**VPN (legacy):**
- User → VPN tunnel → Internal network → All resources accessible
- **Implicit trust** after VPN connection
- Bypass = full network access
- Slow (extra hop), single point failure

**ZTNA (Zero Trust Network Access):**
- User → ZTNA gateway → Specific application
- **No network access** — only specific apps
- Per-session authorization
- Faster (often direct connection через broker)

**ZTNA scope:** application-level access, not network-level.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Adoption:** ZTNA growing rapidly, VPN declining.

## Q13. ZTNA vendors (Cloudflare, Zscaler, Palo Alto)?

**Cloudflare Zero Trust** (formerly Cloudflare Access):
- Easy setup
- Browser-based access
- Tunnels (no inbound ports)

**Zscaler Private Access (ZPA):**
- Enterprise-focused
- Comprehensive product
- Expensive

**Palo Alto Prisma Access:**
- Network-centric (legacy roots)
- Comprehensive

**Tailscale, Twingate:**
- Modern, developer-friendly
- WireGuard-based
- SMB / startups popular


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Choice:** depends на scale, budget, existing vendor relationships.

## Q14. (!) Implementing Zero Trust — где starting point?

**Roadmap:**

**Phase 1 — Foundation:**
- Strong **identity** (IdP, MFA)
- Inventory devices, users, applications
- Classify data sensitivity

**Phase 2 — Identity-based access:**
- SSO для all apps
- Conditional access policies
- Privileged access management

**Phase 3 — Network:**
- Replace VPN с ZTNA
- Micro-segmentation
- Network monitoring

**Phase 4 — Workload:**
- Service mesh (mTLS)
- AuthorizationPolicies
- Secrets management

**Phase 5 — Continuous:**
- Behavioral analytics
- SIEM integration
- Continuous verification


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Years-long journey.** Start small, expand.

## Q15. Continuous verification?

**Re-verify** identity и context **continuously**, not once-and-done.

**Triggers re-auth:**
- Time elapsed (every N hours)
- Location change
- Device posture change
- Anomalous behavior
- Sensitive operation

**Effect:**
- Compromised session detected
- Reduced blast radius
- Stronger guarantees


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Tools:** UEBA (User Entity Behavior Analytics), risk-based authentication.

## Q16. Device posture?

**Device posture** = security state of device.

**Checks:**
- OS patched?
- Antivirus running?
- Disk encrypted?
- Compliant (managed device)?
- Jailbroken / rooted?

**Conditional access** based on posture:
```
Healthy device → full access
Unhealthy device → limited access (read-only, no sensitive data)
Compromised device → block
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**MDM (Mobile Device Management)** integrates с IdP для posture data.

## Q17. (!) Какие частые ошибки при adoption?


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
1. **Buying "Zero Trust product"** thinking it solves all
2. **Big-bang approach** (boil the ocean — never finishes)
3. **Identity foundation weak** (no MFA, no SSO)
4. **No inventory** of assets
5. **Ignoring service-to-service** (only user-to-app focus)
6. **Bad UX** (too many auth prompts → users find workarounds)
7. **No continuous verification**
8. **Network-only thinking** (mTLS without identity)
9. **Insufficient logging** (можно analyze breach)
10. **Vendor lock-in** (single product all-in)

## Q18. ROI и trade-offs?

**Benefits:**
- **Reduced breach impact** (limited lateral movement)
- **Compliance** simpler
- **Remote work** secure
- **Better audit trails**
- **Insider threat** mitigation

**Costs:**
- Expensive tools
- Engineering time
- User friction (initial)
- Performance overhead (auth checks per request)
- Complex troubleshooting


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Pays off** для large orgs или regulated industries (financial, healthcare).

## Q19. (!) Zero Trust для service-to-service?

**Microservices Zero Trust:**

1. **Identity per service** (ServiceAccount, SPIFFE)
2. **mTLS automatic** (service mesh)
3. **AuthorizationPolicies** (allow lists)
4. **Default-deny** network policies
5. **Audit logs** all calls
6. **Short-lived credentials** (JWT, certs)
7. **Secrets management** (Vault, не env vars)
8. **Rotate** credentials frequently

**Tools stack:**
- **Service mesh** (Istio, Linkerd) — mTLS, authz
- **SPIFFE/SPIRE** — service identity standard
- **Vault** — secrets
- **OPA** — policy engine
- **K8s NetworkPolicies** — network segmentation

**Modern microservices** = Zero Trust by default (с right tools).

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
