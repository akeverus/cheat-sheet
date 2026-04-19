---
title: "Pulumi"
description: "Pulumi — это современная платформа для Infrastructure as Code, которая позволяет описывать инфраструктуру с помощью знакомых языков программирования (TypeScript, Python, Go, .NET, Java). В отличие от декларативных инструментов вроде Terraform, Pulumi использует императивный подхо"
tags:
  - platform
  - iac
  - pulumi
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Pulumi

**Pulumi** — это современная платформа для **Infrastructure as Code**, которая позволяет описывать инфраструктуру с помощью знакомых языков программирования (**TypeScript**, **Python**, **Go**, **.NET**, **Java**). В отличие от декларативных инструментов вроде **Terraform**, **Pulumi** использует императивный подход, предоставляя все преимущества программирования для управления инфраструктурой.

## Полезные ссылки
- [Pulumi Documentation](https://www.pulumi.com/docs/)
- [Pulumi Registry](https://www.pulumi.com/registry/)
- [Pulumi GitHub](https://github.com/pulumi/pulumi)
- [AWS Provider](https://www.pulumi.com/registry/packages/aws/)
- [Kubernetes Provider](https://www.pulumi.com/registry/packages/kubernetes/)
- [Policy as Code](https://www.pulumi.com/docs/using-pulumi/crossguard/)

## Содержание

- [Основы Pulumi](#основы-pulumi)
  - [Установка и настройка](#установка-и-настройка)
  - [Структура проекта](#структура-проекта)
- [Работа с ресурсами](#работа-с-ресурсами)
  - [Создание базовой инфраструктуры](#создание-базовой-инфраструктуры)
  - [Работа с конфигурацией](#работа-с-конфигурацией)
- [Компоненты и классы](#компоненты-и-классы)
  - [Создание переиспользуемых компонентов](#создание-переиспользуемых-компонентов)
  - [Использование компонентов](#использование-компонентов)
- [Работа со стеками](#работа-со-стеками)
  - [Управление средами](#управление-средами)
  - [Перекрестные ссылки между стеками](#перекрестные-ссылки-между-стеками)
- [Автоматизация и CI/CD](#автоматизация-и-cicd)
  - [Простой CI/CD pipeline](#простой-cicd-pipeline)
  - [Интеграция с Kubernetes](#интеграция-с-kubernetes)
- [Тестирование и валидация](#тестирование-и-валидация)
  - [Unit тестирование](#unit-тестирование)
  - [Policy as Code](#policy-as-code)
- [Продвинутые паттерны](#продвинутые-паттерны)
  - [Multi-cloud deployment](#multi-cloud-deployment)
  - [Blue-Green deployment](#blue-green-deployment)
- [Мониторинг и отладка](#мониторинг-и-отладка)
  - [Стек мониторинга с Pulumi](#стек-мониторинга-с-pulumi)
- [Лучшие практики](#лучшие-практики)
  - [Организация кода](#организация-кода)
  - [Управление секретами](#управление-секретами)
  - [Производительность и оптимизация](#производительность-и-оптимизация)
- [См. также](#см-также)

## Основы **Pulumi**

### Установка и настройка

#### Установка **Pulumi CLI**

Команды установки **Pulumi CLI**: скрипт **get.pulumi.com** (**Linux/macOS**), **PowerShell** (**Windows**), **npm**, проверка версии.

```bash
# Linux/macOS
curl -fsSL https://get.pulumi.com | sh

# Windows (PowerShell)
Invoke-WebRequest -Uri https://get.pulumi.com/install.ps1 -UseBasicParsing | Invoke-Expression

# Через npm (для Node.js проектов)
npm install -g @pulumi/cli

# Проверка установки
pulumi version
```

#### Настройка облачного провайдера
```bash
# AWS
pulumi config set aws:region us-east-1
pulumi config set aws:profile my-profile

# Azure
pulumi config set azure:location EastUS
pulumi config set azure:subscriptionId xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

# GCP
pulumi config set gcp:project my-project
pulumi config set gcp:region us-central1

# Конфигурация для локального состояния
pulumi config set --local database:password mySecretPassword
```

### Структура проекта

#### Базовый проект
```
my-pulumi-project/
├── Pulumi.yaml          # Конфигурация проекта
├── Pulumi.dev.yaml      # Конфигурация для dev среды
├── Pulumi.prod.yaml     # Конфигурация для prod среды
├── index.ts            # Главный файл (TypeScript)
├── package.json        # Зависимости (для Node.js)
├── tsconfig.json       # TypeScript конфигурация
└── node_modules/       # Установленные пакеты
```

#### **Pulumi.yaml**
```yaml
name: my-infrastructure
runtime:
  name: nodejs
  options:
    typescript: true
description: My infrastructure project
config:
  aws:region: us-east-1
```

#### **package.json** для **Node.js** проекта
```json
{
  "name": "my-infrastructure",
  "devDependencies": {
    "@types/node": "^18.0.0",
    "typescript": "^4.9.0"
  },
  "dependencies": {
    "@pulumi/aws": "^5.0.0",
    "@pulumi/pulumi": "^3.0.0"
  }
}
```

## Работа с ресурсами

### Создание базовой инфраструктуры
```typescript
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

// Создание VPC
const vpc = new aws.ec2.Vpc("my-vpc", {
    cidrBlock: "10.0.0.0/16",
    enableDnsHostnames: true,
    enableDnsSupport: true,
    tags: {
        Name: "my-vpc",
        Environment: pulumi.getStack(),
    },
});

// Создание Internet Gateway
const igw = new aws.ec2.InternetGateway("my-igw", {
    vpcId: vpc.id,
    tags: {
        Name: "my-igw",
    },
});

// Создание публичных subnets
const publicSubnets = [];
for (let i = 0; i < 3; i++) {
    const subnet = new aws.ec2.Subnet(`public-subnet-${i}`, {
        vpcId: vpc.id,
        cidrBlock: `10.0.${i}.0/24`,
        availabilityZone: aws.getAvailabilityZones().then(zones => zones.names[i]),
        mapPublicIpOnLaunch: true,
        tags: {
            Name: `public-subnet-${i}`,
            Type: "public",
        },
    });
    publicSubnets.push(subnet);
}

// Создание приватных subnets
const privateSubnets = [];
for (let i = 0; i < 3; i++) {
    const subnet = new aws.ec2.Subnet(`private-subnet-${i}`, {
        vpcId: vpc.id,
        cidrBlock: `10.0.${i + 10}.0/24`,
        availabilityZone: aws.getAvailabilityZones().then(zones => zones.names[i]),
        tags: {
            Name: `private-subnet-${i}`,
            Type: "private",
        },
    });
    privateSubnets.push(subnet);
}

// Экспорт значений
export const vpcId = vpc.id;
export const publicSubnetIds = publicSubnets.map(s => s.id);
export const privateSubnetIds = privateSubnets.map(s => s.id);
```

### Работа с конфигурацией
```typescript
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

// Получение конфигурации
const config = new pulumi.Config();

// Конфигурация проекта
const projectName = config.get("projectName") || "my-project";
const environment = pulumi.getStack();

// Конфигурация с секретами
const dbPassword = config.requireSecret("dbPassword");
const apiKey = config.getSecret("apiKey");

// Использование конфигурации
const rds = new aws.rds.Instance("my-db", {
    dbName: `${projectName}-db`,
    username: "admin",
    password: dbPassword,
    instanceClass: "db.t3.micro",
    allocatedStorage: 20,
    engine: "postgres",
    tags: {
        Environment: environment,
        Project: projectName,
    },
});

// Экспорт конфиденциальной информации (НЕ ДЕЛАТЬ ЭТОГО!)
export const dbEndpoint = rds.endpoint;
export const dbPasswordExported = dbPassword; // НЕБЕЗОПАСНО!
```

## Компоненты и классы

### Создание переиспользуемых компонентов
```typescript
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

interface VpcArgs {
    cidrBlock: string;
    availabilityZones: string[];
    publicSubnetCount: number;
    privateSubnetCount: number;
    tags?: { [key: string]: string };
}

export class Vpc extends pulumi.ComponentResource {
    public readonly vpc: aws.ec2.Vpc;
    public readonly publicSubnets: aws.ec2.Subnet[];
    public readonly privateSubnets: aws.ec2.Subnet[];
    public readonly internetGateway: aws.ec2.InternetGateway;
    public readonly natGateways: aws.ec2.NatGateway[];

    constructor(name: string, args: VpcArgs, opts?: pulumi.ComponentResourceOptions) {
        super("custom:network:Vpc", name, {}, opts);

        // Создание VPC
        this.vpc = new aws.ec2.Vpc(`${name}-vpc`, {
            cidrBlock: args.cidrBlock,
            enableDnsHostnames: true,
            enableDnsSupport: true,
            tags: {
                Name: `${name}-vpc`,
                ...args.tags,
            },
        }, { parent: this });

        // Internet Gateway
        this.internetGateway = new aws.ec2.InternetGateway(`${name}-igw`, {
            vpcId: this.vpc.id,
            tags: {
                Name: `${name}-igw`,
            },
        }, { parent: this });

        // Public Subnets
        this.publicSubnets = [];
        for (let i = 0; i < args.publicSubnetCount; i++) {
            const subnet = new aws.ec2.Subnet(`${name}-public-${i}`, {
                vpcId: this.vpc.id,
                cidrBlock: this.calculateSubnetCidr(args.cidrBlock, i),
                availabilityZone: args.availabilityZones[i % args.availabilityZones.length],
                mapPublicIpOnLaunch: true,
                tags: {
                    Name: `${name}-public-${i}`,
                    Type: "public",
                    ...args.tags,
                },
            }, { parent: this });
            this.publicSubnets.push(subnet);
        }

        // Private Subnets
        this.privateSubnets = [];
        for (let i = 0; i < args.privateSubnetCount; i++) {
            const subnet = new aws.ec2.Subnet(`${name}-private-${i}`, {
                vpcId: this.vpc.id,
                cidrBlock: this.calculateSubnetCidr(args.cidrBlock, i + args.publicSubnetCount),
                availabilityZone: args.availabilityZones[i % args.availabilityZones.length],
                tags: {
                    Name: `${name}-private-${i}`,
                    Type: "private",
                    ...args.tags,
                },
            }, { parent: this });
            this.privateSubnets.push(subnet);
        }

        // NAT Gateways для private subnets
        this.natGateways = [];
        for (let i = 0; i < Math.min(args.publicSubnetCount, args.privateSubnetCount); i++) {
            const eip = new aws.ec2.Eip(`${name}-nat-${i}`, {
                vpc: true,
                tags: {
                    Name: `${name}-nat-${i}`,
                },
            }, { parent: this });

            const nat = new aws.ec2.NatGateway(`${name}-nat-${i}`, {
                allocationId: eip.id,
                subnetId: this.publicSubnets[i].id,
                tags: {
                    Name: `${name}-nat-${i}`,
                },
            }, { parent: this });

            this.natGateways.push(nat);
        }

        this.registerOutputs({
            vpcId: this.vpc.id,
            publicSubnetIds: this.publicSubnets.map(s => s.id),
            privateSubnetIds: this.privateSubnets.map(s => s.id),
            internetGatewayId: this.internetGateway.id,
            natGatewayIds: this.natGateways.map(n => n.id),
        });
    }

    private calculateSubnetCidr(vpcCidr: string, index: number): string {
        // Простая логика для расчета CIDR подсетей
        // В реальном проекте используйте более сложную логику
        const [vpcBase] = vpcCidr.split('/');
        const baseParts = vpcBase.split('.');
        baseParts[2] = (parseInt(baseParts[2]) + index).toString();
        return `${baseParts.join('.')}.0/24`;
    }
}
```

### Использование компонентов
```typescript
import { Vpc } from "./components/vpc";

const config = new pulumi.Config();
const projectName = config.get("projectName") || "my-project";

// Создание VPC с помощью компонента
const vpc = new Vpc("main", {
    cidrBlock: "10.0.0.0/16",
    availabilityZones: ["us-east-1a", "us-east-1b", "us-east-1c"],
    publicSubnetCount: 3,
    privateSubnetCount: 3,
    tags: {
        Environment: pulumi.getStack(),
        Project: projectName,
    },
});

// Использование выходов компонента
const alb = new aws.lb.LoadBalancer("app-lb", {
    internal: false,
    loadBalancerType: "application",
    securityGroups: [securityGroup.id],
    subnets: vpc.publicSubnetIds,
});

export const vpcId = vpc.vpcId;
export const loadBalancerDns = alb.dnsName;
```

## Работа со стеками

### Управление средами
```typescript
// index.ts для разных сред
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

const config = new pulumi.Config();
const environment = pulumi.getStack();

// Конфигурация в зависимости от среды
const environments = {
    dev: {
        instanceType: "t3.micro",
        instanceCount: 1,
        dbInstanceClass: "db.t3.micro",
    },
    staging: {
        instanceType: "t3.small",
        instanceCount: 2,
        dbInstanceClass: "db.t3.small",
    },
    prod: {
        instanceType: "t3.medium",
        instanceCount: 3,
        dbInstanceClass: "db.t3.medium",
    },
};

const envConfig = environments[environment as keyof typeof environments];

if (!envConfig) {
    throw new Error(`Unknown environment: ${environment}`);
}

// Создание ресурсов на основе конфигурации
const instances = [];
for (let i = 0; i < envConfig.instanceCount; i++) {
    const instance = new aws.ec2.Instance(`web-${i}`, {
        instanceType: envConfig.instanceType,
        ami: "ami-12345678", // Ваш AMI
        tags: {
            Name: `web-${environment}-${i}`,
            Environment: environment,
        },
    });
    instances.push(instance);
}

// База данных
const db = new aws.rds.Instance("main-db", {
    instanceClass: envConfig.dbInstanceClass,
    engine: "postgres",
    allocatedStorage: 20,
    dbName: "myapp",
    username: "admin",
    password: config.requireSecret("dbPassword"),
    skipFinalSnapshot: environment !== "prod",
    tags: {
        Environment: environment,
    },
});

export const instanceIds = instances.map(i => i.id);
export const dbEndpoint = db.endpoint;
```

### Перекрестные ссылки между стеками
```typescript
// Stack A: Network infrastructure
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

const vpc = new aws.ec2.Vpc("main-vpc", {
    cidrBlock: "10.0.0.0/16",
});

export const vpcId = vpc.id;
export const vpcCidr = vpc.cidrBlock;

// Stack B: Application infrastructure
import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";
import { vpcId, vpcCidr } from "./network-stack"; // Импорт из другого стека

// Получение VPC из другого стека
const vpc = aws.ec2.Vpc.get("main-vpc", vpcId);

// Создание security group в VPC из другого стека
const sg = new aws.ec2.SecurityGroup("app-sg", {
    vpcId: vpcId,
    ingress: [
        {
            protocol: "tcp",
            fromPort: 80,
            toPort: 80,
            cidrBlocks: ["0.0.0.0/0"],
        },
    ],
});
```

## Автоматизация и CI/CD

### Простой CI/CD **pipeline**
```yaml
# .github/workflows/pulumi.yml
name: Pulumi

on:
  push:
    branches:
      - main
    paths:
      - 'infrastructure/'
  pull_request:
    branches:
      - main
    paths:
      - 'infrastructure/'

jobs:
  preview:
    name: Preview
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: infrastructure/package-lock.json

      - name: Install dependencies
        run: npm ci
        working-directory: infrastructure

      - name: Setup Pulumi
        uses: pulumi/actions@v4
        with:
          pulumi-version: latest

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Pulumi preview
        run: pulumi preview
        working-directory: infrastructure
        env:
          PULUMI_ACCESS_TOKEN: ${{ secrets.PULUMI_ACCESS_TOKEN }}

  update:
    name: Update
    runs-on: ubuntu-latest
    needs: preview
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: infrastructure/package-lock.json

      - name: Install dependencies
        run: npm ci
        working-directory: infrastructure

      - name: Setup Pulumi
        uses: pulumi/actions@v4
        with:
          pulumi-version: latest

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Pulumi up
        run: pulumi up --yes
        working-directory: infrastructure
        env:
          PULUMI_ACCESS_TOKEN: ${{ secrets.PULUMI_ACCESS_TOKEN }}
```

### Интеграция с **Kubernetes**
```typescript
import * as k8s from "@pulumi/kubernetes";
import * as pulumi from "@pulumi/pulumi";

// Создание Kubernetes провайдера
const k8sProvider = new k8s.Provider("k8s", {
    kubeconfig: config.require("kubeconfig"),
});

// Namespace
const ns = new k8s.core.v1.Namespace("app-ns", {
    metadata: {
        name: "my-app",
    },
}, { provider: k8sProvider });

// ConfigMap
const configMap = new k8s.core.v1.ConfigMap("app-config", {
    metadata: {
        namespace: ns.metadata.name,
        name: "app-config",
    },
    data: {
        "app.properties": `
database.url=jdbc:postgresql://db:5432/myapp
database.username=${dbUsername}
database.password=${dbPassword}
        `,
    },
}, { provider: k8sProvider });

// Deployment
const deployment = new k8s.apps.v1.Deployment("app-deployment", {
    metadata: {
        namespace: ns.metadata.name,
        name: "my-app",
        labels: {
            app: "my-app",
        },
    },
    spec: {
        replicas: 3,
        selector: {
            matchLabels: {
                app: "my-app",
            },
        },
        template: {
            metadata: {
                labels: {
                    app: "my-app",
                },
            },
            spec: {
                containers: [{
                    name: "app",
                    image: "myapp:latest",
                    ports: [{
                        containerPort: 8080,
                    }],
                    envFrom: [{
                        configMapRef: {
                            name: configMap.metadata.name,
                        },
                    }],
                    resources: {
                        requests: {
                            memory: "256Mi",
                            cpu: "250m",
                        },
                        limits: {
                            memory: "512Mi",
                            cpu: "500m",
                        },
                    },
                }],
            },
        },
    },
}, { provider: k8sProvider });

// Service
const service = new k8s.core.v1.Service("app-service", {
    metadata: {
        namespace: ns.metadata.name,
        name: "my-app",
        labels: {
            app: "my-app",
        },
    },
    spec: {
        selector: {
            app: "my-app",
        },
        ports: [{
            port: 80,
            targetPort: 8080,
        }],
        type: "ClusterIP",
    },
}, { provider: k8sProvider });

// Ingress
const ingress = new k8s.networking.v1.Ingress("app-ingress", {
    metadata: {
        namespace: ns.metadata.name,
        name: "my-app-ingress",
        annotations: {
            "nginx.ingress.kubernetes.io/rewrite-target": "/",
        },
    },
    spec: {
        rules: [{
            host: "myapp.example.com",
            http: {
                paths: [{
                    path: "/",
                    pathType: "Prefix",
                    backend: {
                        service: {
                            name: service.metadata.name,
                            port: {
                                number: 80,
                            },
                        },
                    },
                }],
            },
        }],
    },
}, { provider: k8sProvider });

export const namespaceName = ns.metadata.name;
export const serviceName = service.metadata.name;
export const ingressHost = ingress.spec.rules[0].host;
```

## Тестирование и валидация

### **Unit** тестирование
```typescript
import * as aws from "@pulumi/aws";
import { Vpc } from "./components/vpc";

describe("Vpc Component", () => {
    it("should create VPC with correct CIDR", () => {
        const vpc = new Vpc("test-vpc", {
            cidrBlock: "10.0.0.0/16",
            availabilityZones: ["us-east-1a"],
            publicSubnetCount: 1,
            privateSubnetCount: 1,
        });

        // Проверка что VPC создана
        expect(vpc.vpc).toBeDefined();
        expect(vpc.vpc.cidrBlock).toBe("10.0.0.0/16");

        // Проверка подсетей
        expect(vpc.publicSubnets).toHaveLength(1);
        expect(vpc.privateSubnets).toHaveLength(1);
    });
});
```

### **Policy as Code**
```typescript
import * as aws from "@pulumi/aws";
import * as policy from "@pulumi/policy";

// Проверка compliance
new policy.PolicyPack("aws-compliance", {
    policies: [
        new policy.Policy("s3-bucket-encryption", {
            description: "S3 buckets should have encryption enabled",
            enforcementLevel: "mandatory",
            validateResource: (args) => {
                if (args.type === "aws:s3/bucket:Bucket") {
                    const bucket = args.props as aws.s3.Bucket;
                    if (!bucket.serverSideEncryptionConfiguration) {
                        return {
                            valid: false,
                            message: "S3 bucket must have server-side encryption enabled",
                        };
                    }
                }
                return { valid: true };
            },
        }),

        new policy.Policy("ec2-instance-types", {
            description: "EC2 instances must use approved instance types",
            enforcementLevel: "mandatory",
            validateResource: (args) => {
                if (args.type === "aws:ec2/instance:Instance") {
                    const instance = args.props as aws.ec2.Instance;
                    const approvedTypes = ["t3.micro", "t3.small", "t3.medium"];
                    if (!approvedTypes.includes(instance.instanceType)) {
                        return {
                            valid: false,
                            message: `Instance type ${instance.instanceType} is not approved`,
                        };
                    }
                }
                return { valid: true };
            },
        }),
    ],
});
```

## Продвинутые паттерны

### **Multi-cloud deployment**
```typescript
import * as aws from "@pulumi/aws";
import * as azure from "@pulumi/azure-native";
import * as gcp from "@pulumi/gcp";

const config = new pulumi.Config();
const cloud = config.get("cloud") || "aws";

interface CloudResources {
    bucket: any;
    database: any;
}

// AWS resources
function createAwsResources(): CloudResources {
    const bucket = new aws.s3.Bucket("my-bucket", {
        versioning: {
            enabled: true,
        },
    });

    const db = new aws.rds.Instance("my-db", {
        instanceClass: "db.t3.micro",
        engine: "postgres",
        allocatedStorage: 20,
        username: "admin",
        password: config.requireSecret("dbPassword"),
    });

    return { bucket, database: db };
}

// Azure resources
function createAzureResources(): CloudResources {
    const resourceGroup = new azure.resources.ResourceGroup("rg");

    const storage = new azure.storage.StorageAccount("storage", {
        resourceGroupName: resourceGroup.name,
        sku: {
            name: "Standard_LRS",
        },
        kind: "StorageV2",
    });

    const db = new azure.dbforpostgresql.Server("postgres", {
        resourceGroupName: resourceGroup.name,
        sku: {
            name: "B_Gen5_1",
            tier: "Basic",
            capacity: 1,
        },
        properties: {
            version: "11",
            administratorLogin: "admin",
            administratorLoginPassword: config.requireSecret("dbPassword"),
        },
    });

    return { bucket: storage, database: db };
}

// GCP resources
function createGcpResources(): CloudResources {
    const bucket = new gcp.storage.Bucket("my-bucket", {
        location: "US",
        versioning: {
            enabled: true,
        },
    });

    const db = new gcp.sql.DatabaseInstance("my-db", {
        databaseVersion: "POSTGRES_13",
        settings: {
            tier: "db-f1-micro",
        },
    });

    return { bucket, database: db };
}

// Создание ресурсов в зависимости от выбранного облака
let resources: CloudResources;
switch (cloud) {
    case "aws":
        resources = createAwsResources();
        break;
    case "azure":
        resources = createAzureResources();
        break;
    case "gcp":
        resources = createGcpResources();
        break;
    default:
        throw new Error(`Unsupported cloud: ${cloud}`);
}

export const bucketName = resources.bucket.name || resources.bucket.bucket;
export const databaseEndpoint = resources.database.endpoint || resources.database.fullyQualifiedDomainName;
```

### **Blue-Green deployment**
```typescript
import * as k8s from "@pulumi/kubernetes";
import * as pulumi from "@pulumi/pulumi";

const config = new pulumi.Config();
const activeEnvironment = config.get("activeEnvironment") || "blue";

// Определение сред
const environments = {
    blue: {
        color: "blue",
        replicas: 3,
        image: "myapp:v1.0.0",
    },
    green: {
        color: "green",
        replicas: 0, // Начинаем с 0 для blue-green
        image: "myapp:v2.0.0",
    },
};

// Создание deployments для обеих сред
const deployments: { [key: string]: k8s.apps.v1.Deployment } = {};

for (const [envName, envConfig] of Object.entries(environments)) {
    const isActive = envName === activeEnvironment;

    deployments[envName] = new k8s.apps.v1.Deployment(`${envConfig.color}-deployment`, {
        metadata: {
            name: `${envConfig.color}-app`,
            labels: {
                app: "myapp",
                environment: envConfig.color,
            },
        },
        spec: {
            replicas: isActive ? envConfig.replicas : 0,
            selector: {
                matchLabels: {
                    app: "myapp",
                    environment: envConfig.color,
                },
            },
            template: {
                metadata: {
                    labels: {
                        app: "myapp",
                        environment: envConfig.color,
                    },
                },
                spec: {
                    containers: [{
                        name: "app",
                        image: envConfig.image,
                        ports: [{
                            containerPort: 8080,
                        }],
                        readinessProbe: {
                            httpGet: {
                                path: "/health",
                                port: 8080,
                            },
                            initialDelaySeconds: 5,
                            periodSeconds: 10,
                        },
                    }],
                },
            },
        },
    });
}

// Service с selector на активную среду
const service = new k8s.core.v1.Service("app-service", {
    metadata: {
        name: "myapp-service",
        labels: {
            app: "myapp",
        },
    },
    spec: {
        selector: {
            app: "myapp",
            environment: activeEnvironment,
        },
        ports: [{
            port: 80,
            targetPort: 8080,
        }],
        type: "ClusterIP",
    },
});

// Функция для переключения сред
export const switchToGreen = async () => {
    // Масштабирование blue до 0
    await deployments.blue.spec.apply({ replicas: 0 });

    // Масштабирование green до полной нагрузки
    await deployments.green.spec.apply({ replicas: 3 });

    // Обновление service selector
    await service.spec.apply({
        selector: {
            app: "myapp",
            environment: "green",
        },
    });
};

export const activeColor = activeEnvironment;
export const serviceName = service.metadata.name;
```

## Мониторинг и отладка

### Стек мониторинга с **Pulumi**
```typescript
import * as aws from "@pulumi/aws";
import * as k8s from "@pulumi/kubernetes";

// VPC для мониторинга
const monitoringVpc = new aws.ec2.Vpc("monitoring-vpc", {
    cidrBlock: "10.1.0.0/16",
    enableDnsHostnames: true,
    enableDnsSupport: true,
});

// EKS кластер для мониторинга
const cluster = new aws.eks.Cluster("monitoring-cluster", {
    roleArn: eksRole.arn,
    vpcConfig: {
        subnetIds: monitoringVpc.publicSubnetIds,
        endpointPrivateAccess: true,
        endpointPublicAccess: true,
    },
});

// Kubernetes провайдер
const k8sProvider = new k8s.Provider("k8s", {
    kubeconfig: cluster.kubeconfig.apply(JSON.stringify),
});

// Prometheus
const prometheusNs = new k8s.core.v1.Namespace("prometheus", {
    metadata: { name: "prometheus" },
}, { provider: k8sProvider });

const prometheus = new k8s.helm.v3.Release("prometheus", {
    chart: "prometheus",
    version: "15.10.0",
    repositoryOpts: {
        repo: "https://prometheus-community.github.io/helm-charts",
    },
    namespace: prometheusNs.metadata.name,
    values: {
        server: {
            persistentVolume: {
                enabled: true,
                size: "50Gi",
            },
        },
    },
}, { provider: k8sProvider });

// Grafana
const grafanaNs = new k8s.core.v1.Namespace("grafana", {
    metadata: { name: "grafana" },
}, { provider: k8sProvider });

const grafana = new k8s.helm.v3.Release("grafana", {
    chart: "grafana",
    version: "6.50.0",
    repositoryOpts: {
        repo: "https://grafana.github.io/helm-charts",
    },
    namespace: grafanaNs.metadata.name,
    values: {
        adminPassword: config.requireSecret("grafanaPassword"),
        persistence: {
            enabled: true,
            size: "10Gi",
        },
        datasources: {
            "datasources.yaml": {
                apiVersion: 1,
                datasources: [{
                    name: "Prometheus",
                    type: "prometheus",
                    url: "http://prometheus-server.prometheus.svc.cluster.local",
                    access: "proxy",
                    isDefault: true,
                }],
            },
        },
    },
}, { provider: k8sProvider });

// CloudWatch для метрик AWS
const cloudwatch = new aws.cloudwatch.Dashboard("infrastructure-dashboard", {
    dashboardName: "Infrastructure-Monitoring",
    dashboardBody: JSON.stringify({
        widgets: [
            {
                type: "metric",
                properties: {
                    metrics: [
                        ["AWS/EC2", "CPUUtilization", "InstanceId", "i-1234567890abcdef0"],
                    ],
                    period: 300,
                    stat: "Average",
                    region: "us-east-1",
                    title: "EC2 CPU Utilization",
                },
            },
        ],
    }),
});

export const clusterName = cluster.name;
export const prometheusUrl = pulumi.interpolate`http://prometheus-server.${prometheusNs.metadata.name}.svc.cluster.local`;
export const grafanaUrl = pulumi.interpolate`http://grafana.${grafanaNs.metadata.name}.svc.cluster.local`;
export const dashboardUrl = cloudwatch.dashboardArn;
```

## Лучшие практики

### Организация кода
```typescript
// Структура большого проекта
infrastructure/
├── components/           # Переиспользуемые компоненты
│   ├── network/
│   │   ├── vpc.ts
│   │   ├── security-groups.ts
│   │   └── index.ts
│   ├── compute/
│   │   ├── ec2.ts
│   │   ├── ecs.ts
│   │   └── lambda.ts
│   └── storage/
│       ├── rds.ts
│       ├── s3.ts
│       └── elasticache.ts
├── stacks/              # Стековые конфигурации
│   ├── network.ts
│   ├── application.ts
│   └── monitoring.ts
├── lib/                 # Утилиты и helpers
│   ├── tagging.ts
│   ├── naming.ts
│   └── validation.ts
├── tests/               # Тесты
│   ├── unit/
│   └── integration/
├── policies/            # Policy as Code
│   ├── security.ts
│   └── compliance.ts
├── scripts/             # CI/CD скрипты
│   ├── deploy.sh
│   └── destroy.sh
└── index.ts            # Главная точка входа
```

### Управление секретами
```typescript
import * as aws from "@pulumi/aws";
import * as pulumi from "@pulumi/pulumi";

// AWS Secrets Manager
const dbSecret = new aws.secretsmanager.Secret("db-secret", {
    name: "database/credentials",
    description: "Database credentials",
});

const dbSecretVersion = new aws.secretsmanager.SecretVersion("db-secret-version", {
    secretId: dbSecret.id,
    secretString: JSON.stringify({
        username: "admin",
        password: config.requireSecret("dbPassword"),
        host: rdsInstance.address,
        port: rdsInstance.port,
        database: "myapp",
    }),
});

// HashiCorp Vault
const vaultSecret = new aws.secretsmanager.Secret("vault-secret", {
    name: "vault/credentials",
});

const vaultAuth = new k8s.core.v1.Secret("vault-auth", {
    metadata: {
        name: "vault-auth",
        namespace: "vault",
    },
    type: "Opaque",
    stringData: {
        "token": config.requireSecret("vaultToken"),
    },
});

// Использование секретов в приложении
const appConfig = new k8s.core.v1.ConfigMap("app-config", {
    data: {
        "database.url": pulumi.interpolate`postgresql://${dbSecretVersion.secretString.apply(s => {
            const creds = JSON.parse(s);
            return `${creds.username}:${creds.password}@${creds.host}:${creds.port}/${creds.database}`;
        })}`,
    },
});
```

### Производительность и оптимизация
```typescript
// Оптимизация для больших проектов
import * as pulumi from "@pulumi/pulumi";

// Parallel execution
pulumi.runtime.setParallel(10);

// Конфигурация провайдеров для оптимизации
const awsProvider = new aws.Provider("aws", {
    region: "us-east-1",
    maxRetries: 3,
    retryMode: "adaptive",
    skipMetadataApiCheck: true,
});

// Использование aliases для безопасного переименования
const oldBucket = new aws.s3.Bucket("old-name", {
    bucket: "my-old-bucket",
}, {
    aliases: ["old-bucket"],
});

const newBucket = new aws.s3.Bucket("new-name", {
    bucket: "my-new-bucket",
});

// Миграция данных
const migrationScript = new aws.lambda.Function("migration", {
    runtime: aws.lambda.Runtime.NodeJS18dX,
    code: new pulumi.asset.FileArchive("./migration.zip"),
    handler: "index.handler",
    environment: {
        OLD_BUCKET: oldBucket.bucket,
        NEW_BUCKET: newBucket.bucket,
    },
});

// Условное создание ресурсов
const enableMonitoring = config.getBoolean("enableMonitoring") ?? true;

const monitoringResources = enableMonitoring ? {
    prometheus: new k8s.helm.v3.Release("prometheus", {
        chart: "prometheus",
        repositoryOpts: {
            repo: "https://prometheus-community.github.io/helm-charts",
        },
    }),
    grafana: new k8s.helm.v3.Release("grafana", {
        chart: "grafana",
        repositoryOpts: {
            repo: "https://grafana.github.io/helm-charts",
        },
    }),
} : {};

export const monitoringEnabled = enableMonitoring;
export const prometheusUrl = monitoringResources.prometheus?.status?.apply(s => s.deployed?.release?.info?.firstDeployed);
```
## См. также
- [Terraform](../terraform/terraform.md) — декларативная инфраструктура как код
- [Ansible](../ansible/ansible.md) — конфигурационное управление
- [AWS](../../cloud-providers/aws-basics.md) — **Amazon Web Services**
- [Kubernetes](../../containers/kubernetes/kubernetes-basics.md) — оркестрация контейнеров
- [Docker](../../containers/docker/docker-basics.md) — контейнеризация
