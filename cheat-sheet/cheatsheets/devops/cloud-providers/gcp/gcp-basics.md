# Google Cloud Platform: Основы

**Комплексное руководство по использованию Google Cloud Platform (GCP) — облачной платформы от Google.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [GCP Documentation](https://cloud.google.com/docs) - Официальная документация Google

### См. также
- `../aws/aws-basics.md` - AWS основы
- `../azure/azure-basics.md` - Azure основы

## Содержание

- [Введение в GCP](#введение-в-gcp)
- [Основные сервисы](#основные-сервисы)
- [Управление ресурсами](#управление-ресурсами)
- [Интеграция с Java](#интеграция-с-java)

## Введение в GCP

**Google Cloud Platform** — облачная платформа от Google, предоставляющая инфраструктуру и сервисы для разработки и развертывания приложений.

### Основные сервисы

- **Cloud Run** — Serverless контейнеры
- **Cloud SQL** — Управляемые базы данных
- **Cloud Storage** — Объектное хранилище
- **GKE** — Google Kubernetes Engine
- **Cloud Functions** — Serverless функции

## Управление ресурсами

```bash
# Google Cloud CLI
gcloud auth login
gcloud projects create my-project
gcloud app deploy
```

## Интеграция с Java

```java
// Google Cloud Java Client Libraries
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

Storage storage = StorageOptions.getDefaultInstance().getService();
```

---

*Обновлено: 2026-01-25*
