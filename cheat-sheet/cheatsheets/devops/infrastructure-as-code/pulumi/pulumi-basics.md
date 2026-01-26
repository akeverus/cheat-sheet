# Pulumi: Infrastructure as Code на языках программирования

**Комплексное руководство по использованию Pulumi — инструмента для управления инфраструктурой с использованием языков программирования.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Pulumi Documentation](https://www.pulumi.com/docs/) - Официальная документация

### См. также
- `../terraform/terraform-basics.md` - Terraform
- `../ansible/ansible-basics.md` - Ansible

## Содержание

- [Введение в Pulumi](#введение-в-pulumi)
- [Основы на Java](#основы-на-java)
- [Best Practices](#best-practices)

## Введение в Pulumi

**Pulumi** — инструмент Infrastructure as Code, позволяющий определять инфраструктуру используя языки программирования (Java, Python, TypeScript, Go).

### Преимущества

- Использование языков программирования
- Типобезопасность
- Переиспользование кода
- Тестирование инфраструктуры

## Основы на Java

```java
/**
 * Pulumi программа на Java для создания AWS инфраструктуры
 */
import com.pulumi.Pulumi;
import com.pulumi.aws.ec2.Instance;
import com.pulumi.aws.ec2.InstanceArgs;
import com.pulumi.aws.ec2.Ami;
import com.pulumi.aws.ec2.inputs.GetAmiArgs;

public class App {
    public static void main(String[] args) {
        Pulumi.run(ctx -> {
            // Получение AMI
            var ami = Ami.get("ubuntu", GetAmiArgs.builder()
                .mostRecent(true)
                .filters(GetAmiFilterArgs.builder()
                    .name("name")
                    .values("ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*")
                    .build())
                .owners("099720109477")
                .build());
            
            // Создание EC2 инстанса
            var server = new Instance("web-server", InstanceArgs.builder()
                .ami(ami.id())
                .instanceType("t2.micro")
                .tags(Map.of("Name", "web-server"))
                .build());
            
            // Export значения
            ctx.export("publicIp", server.publicIp());
        });
    }
}
```

## Best Practices

1. Использование компонентов для переиспользования
2. Типобезопасность через языки программирования
3. Тестирование инфраструктурного кода

---

*Обновлено: 2026-01-25*
