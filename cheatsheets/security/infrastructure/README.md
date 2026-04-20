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
- [[infrastructure-security]] — сетевая безопасность, хосты, контейнеры, Kubernetes, облако
- [[tls-ssl]] — TLS 1.2/1.3, X.509, mTLS, Nginx/Spring Boot/JVM, Let's Encrypt

### Соседние разделы
- [[README|security/]] — корень раздела
- [[README|security/data/]] — шифрование, PKI, сертификаты
- [[README|security/application/]] — прикладной уровень
- [[README|platform/containers/kubernetes/]] — общий обзор K8s
- [[README|platform/cloud-providers/]] — облачные сервисы

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
| Сетевая сегментация, firewall | [[infrastructure-security]] |
| SSH, bastion, Zero Trust | [[infrastructure-security]] |
| Linux hardening (AppArmor, SELinux, seccomp) | [[infrastructure-security]] |
| Docker security (rootless, read-only FS, capabilities) | [[infrastructure-security]] |
| Kubernetes RBAC, NetworkPolicy, PSS | [[infrastructure-security]] |
| IAM, least privilege, AssumeRole | [[infrastructure-security]] |
| Облако: VPC, security groups, KMS | [[infrastructure-security]] |
| mTLS, service mesh, cert-manager | [[infrastructure-security]] |
| TLS handshake, cipher suites, X.509 | [[tls-ssl]] |
| Nginx/Spring Boot SSL, JVM keystore | [[tls-ssl]] |
| Let's Encrypt / ACME / cert-manager | [[tls-ssl]] |

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

- Scanning-инструменты (Trivy, kube-bench, tfsec) — [[README]]
- Security-тестирование в CI/CD — [[README]]
- Secrets в кластере — [[secrets-management]]
- Kubernetes-углубление — [[README]]
