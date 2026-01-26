# Packer: Создание образов машин

**Комплексное руководство по использованию Packer — инструмента для создания образов машин (AMIs, Docker images и др.).**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Packer Documentation](https://www.packer.io/docs) - Официальная документация HashiCorp

### См. также
- `../terraform/terraform-basics.md` - Terraform
- `../../containers/docker/docker-images.md` - Docker образы

## Содержание

- [Введение в Packer](#введение-в-packer)
- [Конфигурация Packer](#конфигурация-packer)
- [Builders](#builders)
- [Provisioners](#provisioners)

## Введение в Packer

**Packer** — инструмент от HashiCorp для создания идентичных образов машин для различных платформ из одного источника конфигурации.

### Основные возможности

- Создание образов для множества платформ
- Автоматизация процесса сборки
- Идемпотентность
- Интеграция с CI/CD

## Конфигурация Packer

```json
{
  "variables": {
    "aws_region": "us-east-1",
    "source_ami": "ami-0c55b159cbfafe1f0"
  },
  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "source_ami": "{{user `source_ami`}}",
      "instance_type": "t2.micro",
      "ssh_username": "ubuntu",
      "ami_name": "my-app-{{timestamp}}"
    }
  ],
  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "sudo apt-get update",
        "sudo apt-get install -y nginx"
      ]
    }
  ]
}
```

## Builders

```json
{
  "builders": [
    {
      "type": "docker",
      "image": "ubuntu:20.04",
      "commit": true
    },
    {
      "type": "amazon-ebs",
      "region": "us-east-1",
      "source_ami": "ami-0c55b159cbfafe1f0",
      "instance_type": "t2.micro",
      "ami_name": "my-app"
    }
  ]
}
```

## Provisioners

```json
{
  "provisioners": [
    {
      "type": "shell",
      "script": "setup.sh"
    },
    {
      "type": "ansible",
      "playbook_file": "playbook.yml"
    }
  ]
}
```

---

*Обновлено: 2026-01-25*
