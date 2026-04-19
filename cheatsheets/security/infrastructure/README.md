---
title: "Infrastructure Security"
description: "Сетевая безопасность, hardening хостов и ОС, безопасность контейнеров и Kubernetes, облачный периметр и принципы least privilege."
tags:
  - meta
  - index
  - security
  - infrastructure
type: "index"
updated: "2026-04-20"
---
# Infrastructure Security

Раздел покрывает защиту инфраструктуры: firewall/NACL, VPC-сегментация, hardening Linux-хостов, безопасность Docker и Kubernetes (Pod Security Standards, NetworkPolicy, RBAC), облачные периметры (AWS/Azure/GCP), а также IAM и принцип наименьших привилегий. Основной ориентир для практического hardening — **CIS Benchmarks** (CIS Docker, CIS Kubernetes, CIS AWS/Azure/GCP).

Для кого: DevOps, SRE, платформенные инженеры и security-инженеры, отвечающие за настройку кластеров и облачных аккаунтов. Уровень приложений — в `../application/`, шифрование данных и секретов — в `../data/`.

## Полезные ссылки

### Основные документы
- [infrastructure-security.md](infrastructure-security.md) — сетевая безопасность, хосты, контейнеры, Kubernetes, облако
- [tls-ssl.md](tls-ssl.md) — TLS 1.2/1.3, X.509, mTLS, Nginx/Spring Boot/JVM, Let's Encrypt

### Соседние разделы
- [security/](../README.md) — корень раздела
- [security/data/](../data/README.md) — шифрование, PKI, сертификаты
- [security/application/](../application/README.md) — прикладной уровень
- [platform/containers/kubernetes/](../../platform/containers/kubernetes/README.md) — общий обзор K8s
- [platform/cloud-providers/](../../platform/cloud-providers/README.md) — облачные сервисы

### Внешние ресурсы
- [CIS Benchmarks](https://www.cisecurity.org/cis-benchmarks/) — руководства по hardening для Docker, Kubernetes, Linux, AWS/Azure/GCP
- [Kubernetes Security](https://kubernetes.io/docs/concepts/security/)
- [Pod Security Standards](https://kubernetes.io/docs/concepts/security/pod-security-standards/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [AWS Security Best Practices](https://aws.amazon.com/security/security-resources/)
- [OWASP Docker Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Docker_Security_Cheat_Sheet.html)

## Содержание

- [Карта тем](#карта-тем)
- [CIS Benchmarks как ориентир](#cis-benchmarks-как-ориентир)
- [Слои защиты](#слои-защиты)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Где читать |
|------|-----------|
| Сетевая сегментация, firewall | [infrastructure-security.md](infrastructure-security.md) |
| SSH, bastion, Zero Trust | [infrastructure-security.md](infrastructure-security.md) |
| Linux hardening (AppArmor, SELinux, seccomp) | [infrastructure-security.md](infrastructure-security.md) |
| Docker security (rootless, read-only FS, capabilities) | [infrastructure-security.md](infrastructure-security.md) |
| Kubernetes RBAC, NetworkPolicy, PSS | [infrastructure-security.md](infrastructure-security.md) |
| IAM, least privilege, AssumeRole | [infrastructure-security.md](infrastructure-security.md) |
| Облако: VPC, security groups, KMS | [infrastructure-security.md](infrastructure-security.md) |
| mTLS, service mesh, cert-manager | [infrastructure-security.md](infrastructure-security.md) |
| TLS handshake, cipher suites, X.509 | [tls-ssl.md](tls-ssl.md) |
| Nginx/Spring Boot SSL, JVM keystore | [tls-ssl.md](tls-ssl.md) |
| Let's Encrypt / ACME / cert-manager | [tls-ssl.md](tls-ssl.md) |

## CIS Benchmarks как ориентир

CIS публикует структурированные benchmark-документы: CIS Docker Benchmark, CIS Kubernetes Benchmark, CIS Ubuntu, CIS AWS Foundations и т.д. Это готовые чек-листы «что отключить / что настроить» с обоснованием. Для Kubernetes дополнительно используется утилита `kube-bench`, для Docker — `docker-bench-security`. Прогон перед прод-релизом — обязательная часть readiness-review.

## Слои защиты

- **Network:** VPC/subnet-изоляция, NACL, security groups, NetworkPolicy, WAF, DDoS-защита.
- **Host:** минимальный образ, патчи, SELinux/AppArmor, auditd, отключение неиспользуемых сервисов.
- **Container:** non-root, read-only rootfs, drop capabilities, seccomp, distroless-базы.
- **Orchestrator:** RBAC, PSS (restricted), admission controllers (OPA/Kyverno), network policies.
- **Identity:** IAM-роли вместо ключей, Workload Identity, OIDC-federation, короткоживущие токены.

## Маршруты чтения

- **Quick scan (30 мин):** разделы «Сетевая безопасность» и «Контейнеры» в `infrastructure-security.md`.
- **Перед выкладкой K8s-кластера:** полный `infrastructure-security.md` + прогон `kube-bench` против CIS Kubernetes Benchmark.
- **Перед включением HTTPS / mTLS:** `tls-ssl.md` — handshake, cipher suites, Nginx/Spring конфиг.
- **Cloud review:** `infrastructure-security.md` + CIS-benchmark для конкретного облака + разбор IAM-политик на least privilege.

## Куда идти дальше

- Scanning-инструменты (Trivy, kube-bench, tfsec) — [../tools/README.md](../tools/README.md)
- Security-тестирование в CI/CD — [../testing/README.md](../testing/README.md)
- Secrets в кластере — [../data/secrets-management.md](../data/secrets-management.md)
- Kubernetes-углубление — [../../platform/containers/kubernetes/README.md](../../platform/containers/kubernetes/README.md)
