# Azure: Основы

**Комплексное руководство по использованию Microsoft Azure — облачной платформы от Microsoft.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Azure Documentation](https://docs.microsoft.com/azure/) - Официальная документация Microsoft

### См. также
- `../aws/aws-basics.md` - AWS основы
- `../gcp/gcp-basics.md` - Google Cloud Platform

## Содержание

- [Введение в Azure](#введение-в-azure)
- [Основные сервисы](#основные-сервисы)
- [Управление ресурсами](#управление-ресурсами)
- [Интеграция с Java](#интеграция-с-java)

## Введение в Azure

**Microsoft Azure** — облачная платформа от Microsoft, предоставляющая широкий спектр сервисов для разработки, развертывания и управления приложениями.

### Основные сервисы

- **Azure App Service** — Размещение веб-приложений
- **Azure SQL Database** — Управляемая база данных
- **Azure Storage** — Хранилище данных
- **Azure Kubernetes Service (AKS)** — Управляемый Kubernetes
- **Azure Functions** — Serverless функции

## Управление ресурсами

```bash
# Azure CLI
az login
az group create --name myResourceGroup --location eastus
az appservice plan create --name myAppServicePlan --resource-group myResourceGroup
```

## Интеграция с Java

```java
// Azure SDK для Java
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

BlobServiceClientBuilder builder = new BlobServiceClientBuilder()
    .connectionString("DefaultEndpointsProtocol=https;AccountName=...");
```

---

*Обновлено: 2026-01-25*
