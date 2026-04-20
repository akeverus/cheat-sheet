---
title: "Pulumi"
description: "Infrastructure as Code через полноценные языки программирования (TypeScript, Python, Go, Java, .NET)."
tags:
  - meta
  - index
  - platform
  - iac
  - pulumi
type: "index"
updated: "2026-04-17"
---
# Pulumi

Pulumi — современная IaC-платформа, где инфраструктура описывается на **полноценных языках программирования**: TypeScript, Python, Go, Java, .NET. Модель ресурсов близка к Terraform (тот же уровень провайдеров), но вместо HCL — ваш любимый ЯП со всеми его абстракциями: классы, функции, циклы, тесты. State-хранилище — Pulumi Cloud или свой backend (S3, GCS, файл).

Для кого: команды, которым тесно в HCL (нужна типизация, переиспользование, реальная логика), product-teams с сильным программистским профилем и те, кто хочет тестировать инфру так же, как код.

## Полезные ссылки

### Основные документы
- [[pulumi-basics]] — установка, первый stack, базовые ресурсы
- [[pulumi]] — полное руководство: компоненты, stacks, automation API, CI/CD

### Соседние разделы
- [[README|platform/iac/]] — родительский раздел
- [[README|platform/iac/terraform/]] — основной конкурент и альтернатива
- [[README|platform/iac/ansible/]] — configuration management
- [[README|platform/cloud-providers/]] — AWS/GCP/Azure

### Внешние ресурсы
- [Pulumi Documentation](https://www.pulumi.com/docs/)
- [Pulumi Registry](https://www.pulumi.com/registry/) — провайдеры
- [Pulumi GitHub](https://github.com/pulumi/pulumi)
- [Policy as Code (CrossGuard)](https://www.pulumi.com/docs/using-pulumi/crossguard/)

## Содержание

- [Карта тем](#карта-тем)
- [Pulumi vs Terraform/Ansible/Packer](#pulumi-vs-terraformansiblepacker)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Установка, первый проект | [[pulumi-basics]] |
| Ресурсы и провайдеры | [[pulumi]] |
| Stacks и окружения | [[pulumi]] |
| Компоненты (переиспользование) | [[pulumi]] |
| StackReferences (зависимости между стеками) | [[pulumi]] |
| Secrets и шифрование | [[pulumi]] |
| CI/CD и Automation API | [[pulumi]] |
| Policy as Code (CrossGuard) | [[pulumi]] |

## Pulumi vs Terraform/Ansible/Packer

| Инструмент | Парадигма | Язык | State | Экосистема |
|------------|-----------|------|-------|-----------|
| Pulumi | Imperative/declarative | TS, Python, Go, Java, .NET | Да (Pulumi Cloud / S3 / local) | Свой Registry; может использовать Terraform-провайдеры |
| Terraform | Declarative | HCL | Да (S3/GCS/Cloud) | Крупнейшая, зрелая |
| Ansible | Procedural | YAML | Нет | 3000+ модулей |
| Packer | Procedural | HCL/JSON | Нет | Builders + Provisioners |

Главное отличие Pulumi от Terraform — **язык и способ абстракции**. Если нужно описать 50 похожих, но чуть-чуть разных ресурсов — в Python это цикл на 3 строчки, в HCL — громоздкий for_each с dynamic-блоками. При этом тесты на инфру пишутся нативно (pytest/Jest). Минусы — меньше сообщество, меньше готовых модулей, platform lock-in на Pulumi Cloud (ослабляется self-hosted backend).

## Когда использовать

- Команда уже пишет на TS/Python/Go и хочет единый стек.
- Инфра сильно параметризована, нужны классы и наследование.
- Нужны unit/integration-тесты инфры.
- **Terraform лучше, если:** команда не разработчики (DevOps/SRE), есть большой экосистемный лок на HCL-модули, важен максимально зрелый провайдер.
- **Ansible лучше, если:** задача — конфигурировать живые хосты, а не provision.

## Маршруты чтения

- **Знакомство (полдня):** `pulumi-basics.md` развернуть S3-bucket на своём языке.
- **Полный курс:** `pulumi.md` компоненты stacks CI.
- **Миграция с Terraform:** `pulumi.md` + `tf2pulumi` + сравнение с `../terraform/`.

## Куда идти дальше

- Сравнение с Terraform — [[README]]
- Configuration management — [[README]]
- Build образов — [[README]]
- Обзор подходов IaC — [[iac-overview]]
