---
title: "Вопросы на собеседовании: mTLS (Mutual TLS)"
description: "mTLS: mutual TLS authentication, certificates, X.509, PKI, certificate rotation, service mesh integration, mTLS в microservices, vs TLS, common pitfalls"
tags:
  - interview
  - security
  - mtls-interview
aliases:
  - "mTLS interview"
  - "Mutual TLS interview"
  - "mTLS собеседование"
  - "Two-way TLS"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `mTLS (Mutual TLS)`

`mTLS (Mutual TLS)` — TLS handshake, где **обе стороны** verify identity (vs standard TLS — только server). Foundation для **Zero Trust** networking. Used в microservices (service mesh), B2B APIs, IoT, banking. Requires PKI infrastructure для cert management.

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

**mTLS (Mutual TLS)** — modification standard TLS handshake.

**Standard TLS:**
- **Server** presents cert
- **Client** verifies server
- Client **does NOT** authenticate via cert

**mTLS:**
- **Both** server и client present certs
- **Both** verify each other
- **Mutual authentication**

**Result:** server **knows** which client is connecting (cryptographically verified, not just IP/credentials).

## Q2. (!) TLS vs mTLS — отличия?

| Aspect | TLS | mTLS |
|--------|-----|------|
| Server auth | ✓ | ✓ |
| Client auth | ❌ (or other mechanism) | ✓ (via cert) |
| Encryption | ✓ | ✓ |
| Use case | Public web | Service-to-service, B2B |
| Cert management | Server cert only | Both sides |
| Complexity | Low | High |
| PKI required | For CA | Full PKI |

**TLS:** browser к bank.com (browser doesn't have cert).
**mTLS:** bank-A.com к bank-B.com (both have organizational certs).

## Q3. (!) Зачем mTLS?

**Use cases:**

1. **Service-to-service** in microservices (Zero Trust)
2. **B2B APIs** (high-value integrations)
3. **IoT devices** (device authentication)
4. **Internal admin tools** (extra security layer)
5. **Banking, finance** (regulatory compliance)
6. **Healthcare** (HIPAA)
7. **Government** (compliance requirements)

**Benefits:**
- **Strong authentication** (cryptographic, not passwords)
- **No shared secrets** к leak (no API keys)
- **Service identity** assured
- **Encrypted transit** (TLS benefit)
- **Audit trail** (cert identity logged)

## Q4. (!) mTLS handshake?

```
1. Client → Server: ClientHello (supported ciphers)
2. Server → Client: ServerHello + certificate + CertificateRequest
3. Client → Server: certificate + ClientKeyExchange + CertificateVerify
4. Both: derive session keys
5. Encrypted communication
```

**Differences from TLS:**
- **CertificateRequest** (server asks for client cert)
- **CertificateVerify** (client signs handshake с private key, proving cert ownership)

**Both certs validated** through CA chain.

**TLS 1.3 simplifies** handshake (1-RTT vs 2-RTT TLS 1.2).

## Q5. X.509 certificates?

**X.509** — standard format для public-key certificates.

**Certificate содержит:**
- **Subject** (who owns)
- **Issuer** (who signed — CA)
- **Public key**
- **Serial number**
- **Validity period** (notBefore, notAfter)
- **Extensions** (SANs — Subject Alternative Names, key usage, etc.)
- **Signature** (CA's signature)

**Format:** PEM (base64 text), DER (binary).

```
-----BEGIN CERTIFICATE-----
MIIDXTCCAkWgAwIBAgIJAK...
-----END CERTIFICATE-----
```

**Verification:** check signature using CA's public key.

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
- **Trust anchor** (browsers, OSes pre-install)
- **Self-signed**
- **Stored offline** для security

**Intermediate CAs:**
- Signed by Root
- Used to **issue end certs**
- **Allow easier revocation** (revoke Intermediate, not Root)

**Chain of trust:** verify cert → check Issuer (Intermediate) → check that's signed by Root → trust.

## Q7. (!) Public Key Infrastructure (PKI)?

**PKI** — infrastructure для managing certificates.

**Components:**
- **CA** — issues certs
- **Registration Authority (RA)** — verifies identity before issuing
- **Certificate Repository** — store certs
- **Validation Authority (VA)** — handles revocation queries (CRL, OCSP)

**PKI tasks:**
- Issue certs
- Revoke compromised
- Rotate (renew)
- Audit / log

**Internal PKI:**
- HashiCorp Vault PKI engine
- step-ca (smallstep)
- Microsoft AD CS
- Cloudflare PKI Toolkit
- Custom (OpenSSL scripts)

**Public PKI:**
- Let's Encrypt (free)
- DigiCert, Sectigo, GoDaddy (paid)

## Q8. Self-signed vs CA-signed?

**Self-signed:**
- ✅ Free, fast
- ❌ Browsers / clients don't trust by default
- **OK для:** dev, internal tools (with manual trust setup)

**CA-signed:**
- ✅ Trusted automatically
- ❌ Cost (или Let's Encrypt free)
- **Required для:** public-facing apps

**For mTLS** обычно использует **internal CA** (own PKI). Devices/services trust your CA, not public CAs.

## Q9. Certificate rotation?

**Certs expire.** Must be **renewed** before expiration.

**Manual rotation pain:**
- Forget → cert expires → outage
- Multiple servers → rotate в order
- Downtime risk

**Automated rotation:**
- **Let's Encrypt + cert-manager** (web certs)
- **Vault PKI + sidecar** (service certs)
- **Service mesh** (Istio auto-rotates every 24h)

**Best practice:**
- **Short TTLs** (24 hours, days, не years)
- **Auto-rotation**
- **Alerting** на expiry approaching

## Q10. SPIFFE / SPIRE?

**SPIFFE (Secure Production Identity Framework For Everyone)** — open standard для **service identity**.

**SVID (SPIFFE Verifiable Identity Document):**
- X.509 certificate с SPIFFE ID
- Format: `spiffe://trust-domain/path/to/service`

**Example:**
```
spiffe://example.org/ns/payments/sa/payment-service
```

**SPIRE** — reference implementation. Issues SVIDs based on workload attestation (K8s ServiceAccount, AWS IAM, etc.).

**Integrates с:**
- Istio
- Envoy
- Vault
- AWS, GCP, K8s

**Standard для service identity** в Zero Trust.

## Q11. (!) mTLS в service mesh (Istio, Linkerd)?

**Service mesh — automatic mTLS.**

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

**Linkerd:** automatic, default-on.

**Workings:**
- Each pod gets cert (signed by mesh CA)
- TTL: 24 hours, auto-rotated
- Identity = ServiceAccount
- Sidecar proxies handle TLS

**Apps see plain HTTP.** Sidecar adds mTLS transparently.

**Effect:** zero-config service-to-service auth.

Подробнее — [Istio](../devops/istio-service-mesh-interview.md), [Linkerd](../devops/linkerd-interview.md).

## Q12. cert-manager (K8s)?

**cert-manager** — K8s controller для certificate management.

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

**Issuers:**
- **Let's Encrypt** (web certs, free)
- **Vault** PKI
- **Self-signed**
- **CA-issued**
- **External issuers**

**Auto-renewal** before expiration. Stores в K8s Secret.

**Integrates с Istio** для mTLS certs.

## Q13. Vault PKI?

**Vault PKI engine** — issue X.509 certs.

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

**Use case:**
- Internal CA для mTLS
- Short-lived certs (1-24 hours)
- Programmatic issuance
- Automation-friendly

Подробнее — [Vault](../devops/vault-interview.md).

## Q14. mTLS в Kafka, Redis, databases?

**Most data systems** support mTLS.

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

**MongoDB, Elasticsearch, MySQL** — all support mTLS.

**Use case:** secure connections к data stores from app, prevent unauthorized access.

## Q15. (!) Когда использовать mTLS?

**Use mTLS когда:**
- **Service-to-service** в microservices (Zero Trust)
- **B2B APIs** (high-value, partner integrations)
- **IoT** (device auth)
- **Internal sensitive APIs** (admin tools)
- **Compliance** (PCI, HIPAA, banking)
- **Cross-region** secure traffic

**Don't use когда:**
- Public consumer APIs (use OAuth, API keys)
- Mobile apps (cert distribution hard)
- Quick prototypes (operational overhead)

**Modern approach:** **service mesh** для internal mTLS, OAuth для external clients.

## Q16. Performance overhead?

**Per request overhead:**
- TLS handshake: ~50-100 ms (one time per connection)
- mTLS handshake: ~100-200 ms (extra cert validation)
- Encrypted bytes: ~5-10% CPU overhead

**Optimization:**
- **Connection pooling** (reuse TLS sessions)
- **Session resumption** (TLS 1.3 0-RTT)
- **HTTP/2 multiplexing** (one connection, many requests)
- **Hardware acceleration** (AES-NI)

**В service mesh** — handshake **once per connection** between sidecars (long-lived). App-to-sidecar — localhost (no TLS).

**Net overhead** в production: ~3-5% latency.

## Q17. (!) Common pitfalls?

1. **No cert rotation** — certs expire, outage
2. **Long-lived certs** (years) — security risk
3. **Self-signed без trust setup** — clients reject
4. **Missing intermediate certs** в chain
5. **Wrong cert** для hostname (CN/SAN mismatch)
6. **Clock skew** — cert "not yet valid"
7. **No revocation** mechanism (compromised cert valid until expiry)
8. **Cert distribution** difficult (mobile, public clients)
9. **Private key leaks** (committed к git, exposed in logs)
10. **Hard к debug** (TLS errors cryptic)

**Tools:** `openssl s_client`, `curl -v`, Wireshark для debugging.

## Q18. mTLS vs JWT?

| Aspect | mTLS | JWT |
|--------|------|-----|
| Authentication | Cryptographic certificate | Bearer token |
| Layer | Transport (L4-L5) | Application (L7) |
| Identity | Cert subject | Claims в token |
| Rotation | Cert renewal | Token expiration |
| Revocation | CRL/OCSP (hard) | Short TTL + blocklist |
| Mobile | Hard (cert install) | Easy (HTTP header) |
| Browser | Hard (cert install) | Easy (cookie) |
| Service-to-service | **Excellent** | Good |
| User-facing | Difficult | **Excellent** |

**Often combined:**
- **mTLS** между services (Zero Trust)
- **JWT** для user identity propagation through services

## Q19. Certificate revocation (CRL, OCSP)?

**Cert compromised** before expiration → must be **revoked**.

**CRL (Certificate Revocation List):**
- CA publishes list of revoked certs
- Clients download periodically
- **Slow** (large lists, infrequent updates)
- Privacy issue (CA knows who checks)

**OCSP (Online Certificate Status Protocol):**
- Real-time check (HTTP request к OCSP responder)
- Faster
- **Privacy** (CA knows checks)
- **OCSP Stapling** — server includes signed OCSP response с cert (no separate request)

**Modern alternative: short-lived certs** (no revocation needed — just expire).

## Q20. (!) Short-lived certificates?

**Short-lived certs:**
- TTL: hours / minutes (vs years traditionally)
- **Auto-renewed** before expiry
- **No revocation** needed (just expire fast)

**Examples:**
- **Istio:** 24h TTL (default)
- **SPIRE:** configurable, often 1-24h
- **Vault PKI:** any TTL

**Trade-off:**
- ✅ Compromise window short (hours, не years)
- ✅ No revocation infrastructure
- ❌ Frequent issuance load (many cert ops/sec)
- ❌ Requires automation (manual rotation impossible)

**Modern pattern:** PKI infrastructure с short-lived certs auto-issued.

В **2025** mTLS — **standard** для production microservices. Service mesh makes it transparent.

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
