---
title: "AWS IAM (Identity and Access Management)"
description: "AWS IAM - это сервис для управления доступом к AWS ресурсам. IAM позволяет создавать и управлять пользователями, группами, ролями и политиками для безопасного контроля доступа к AWS сервисам и ресурсам. Этот документ охватывает best practices, advanced паттерны и enterprise сцена"
tags: ["platform", "cloud-providers", "aws-iam"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **AWS IAM** (`Identity and Access Management`)

**AWS IAM** - это сервис для управления доступом к **AWS** ресурсам. **IAM** позволяет создавать и управлять пользователями, группами, ролями и политиками для безопасного контроля доступа к **AWS** сервисам и ресурсам. Этот документ охватывает **best practices**, **advanced** паттерны и **enterprise** сценарии использования **IAM**.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки
- [IAM Documentation](https://docs.aws.amazon.com/iam/)
- [IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [Policy Simulator](https://policysim.aws.amazon.com/)
- [Access Analyzer](https://docs.aws.amazon.com/IAM/latest/UserGuide/what-is-access-analyzer.html)
- [IAM Identity Center](https://docs.aws.amazon.com/singlesignon/)

## Содержание

- [Основы IAM](#основы-iam)
  - [IAM Entities](#iam-entities)
  - [IAM Limits](#iam-limits)
  - [ARN (Amazon Resource Name)](#arn-amazon-resource-name)
- [IAM Policies](#iam-policies)
  - [Policy Structure](#policy-structure)
  - [Identity-based Policies](#identity-based-policies)
  - [Resource-based Policies](#resource-based-policies)
  - [Service Control Policies (SCP)](#service-control-policies-scp)
- [IAM Users и Groups](#iam-users-и-groups)
  - [User Management](#user-management)
  - [Group Management](#group-management)
  - [Password Policy](#password-policy)
- [IAM Roles](#iam-roles)
  - [EC2 Instance Roles](#ec2-instance-roles)
  - [Lambda Execution Roles](#lambda-execution-roles)
  - [Service-Linked Roles](#service-linked-roles)
- [Cross-Account Access](#cross-account-access)
  - [Role-Based Cross-Account Access](#role-based-cross-account-access)
  - [Resource-Based Cross-Account Access](#resource-based-cross-account-access)
  - [Organization-Based Access](#organization-based-access)
- [MFA и Security](#mfa-и-security)
  - [Virtual MFA Devices](#virtual-mfa-devices)
  - [Hardware MFA](#hardware-mfa)
  - [Conditional Access Policies](#conditional-access-policies)
- [Access Analyzer](#access-analyzer)
  - [Account-Level Analyzer](#account-level-analyzer)
  - [Organization-Level Analyzer](#organization-level-analyzer)
  - [Custom Policy Checks](#custom-policy-checks)
- [IAM в Enterprise](#iam-в-enterprise)
  - [Permission Boundaries](#permission-boundaries)
  - [Session Policies](#session-policies)
  - [ABAC (Attribute-Based Access Control)](#abac-attribute-based-access-control)
  - [IAM Center (AWS SSO Successor)](#iam-center-aws-sso-successor)
- [Решение проблем](#решение-проблем)
  - [Common IAM Issues](#common-iam-issues)
  - [Access Denied Troubleshooting](#access-denied-troubleshooting)
  - [Security Best Practices Audit](#security-best-practices-audit)
  - [Performance Optimization](#performance-optimization)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы **IAM**

### IAM Entities
```text
IAM Entities:
├── Users          # Долгосрочные credentials для людей/приложений
├── Groups         # Коллекции users с общими permissions
├── Roles          # Временные permissions для AWS ресурсов
└── Policies       # Документы определяющие permissions
```

### IAM Limits
```yaml
# AWS Account Limits (default)
iam-limits:
  users-per-account: 5000
  groups-per-account: 300
  roles-per-account: 1000
  policies-per-account: 1500
  policy-size: "6144 bytes"
  password-min-length: 8
  password-max-length: 128
  mfa-devices-per-user: 8
  access-keys-per-user: 2
  roles-per-user: 10

# Service Limits
service-limits:
  ec2-instances-per-region: 20
  s3-buckets-per-account: 100
  lambda-functions-per-region: 1000
  rds-db-instances: 40
```

### ARN (`Amazon Resource Name`)
```
ARN Format: arn:partition:service:region:account-id:resource

Examples:
├── User:          arn:aws:iam::123456789012:user/JohnDoe
├── Group:         arn:aws:iam::123456789012:group/Developers
├── Role:          arn:aws:iam::123456789012:role/EC2-WebServer-Role
├── Policy:        arn:aws:iam::123456789012:policy/MyCustomPolicy
├── S3 Bucket:     arn:aws:s3:::my-bucket
├── EC2 Instance:  arn:aws:ec2:us-east-1:123456789012:instance/i-1234567890abcdef0
└── Lambda:        arn:aws:lambda:us-east-1:123456789012:function:my-function
```

## IAM Policies

### Policy Structure
```json
{
  "Version": "2012-10-17",
  "Id": "PolicyId",
  "Statement": [
    {
      "Sid": "StatementId",
      "Effect": "Allow|Deny",
      "Principal": {
        "AWS": "arn:aws:iam::account:root",
        "Service": "ec2.amazonaws.com"
      },
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": [
        "arn:aws:s3:::my-bucket/*"
      ],
      "Condition": {
        "StringEquals": {
          "aws:SourceIp": "192.168.1.0/24"
        },
        "DateGreaterThan": {
          "aws:CurrentTime": "2023-01-01T00:00:00Z"
        }
      }
    }
  ]
}
```

### Identity-based Policies
```json
// Managed Policy для Developers
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ec2:Describe*",
        "ec2:StartInstances",
        "ec2:StopInstances",
        "ec2:RebootInstances"
      ],
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "ec2:ResourceTag/Environment": "development"
        }
      }
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::dev-bucket/*",
        "arn:aws:s3:::dev-bucket"
      ]
    }
  ]
}

// Inline Policy для специфических разрешений
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::confidential-bucket/readonly/*",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "10.0.0.0/8"
        }
      }
    }
  ]
}
```

### Resource-based Policies
```json
// S3 Bucket Policy
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:root"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::external-account:user/ExternalUser"
      },
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::shared-bucket/*",
      "Condition": {
        "StringEquals": {
          "aws:PrincipalAccount": "external-account-id"
        }
      }
    }
  ]
}

// KMS Key Policy
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:root"
      },
      "Action": "kms:*",
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:role/EC2-Role",
          "arn:aws:iam::123456789012:role/Lambda-Role"
        ]
      },
      "Action": [
        "kms:Encrypt",
        "kms:Decrypt",
        "kms:GenerateDataKey*"
      ],
      "Resource": "*"
    }
  ]
}
```

### Service Control Policies (**SCP**)
```json
// Organization SCP для ограничения регионов
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Deny",
      "Action": "*",
      "Resource": "*",
      "Condition": {
        "StringNotEquals": {
          "aws:RequestedRegion": [
            "us-east-1",
            "us-west-2",
            "eu-west-1"
          ]
        }
      }
    }
  ]
}

// SCP для запрета root access
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Deny",
      "Action": "*",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "aws:PrincipalType": "Root"
        }
      }
    }
  ]
}
```

## IAM Users и **Groups**

### User Management
```bash
# Create IAM User
aws iam create-user --user-name john-doe

# Create login profile (password)
aws iam create-login-profile \
  --user-name john-doe \
  --password 'TempPassword123!' \
  --password-reset-required

# Attach managed policy
aws iam attach-user-policy \
  --user-name john-doe \
  --policy-arn arn:aws:iam::aws:policy/ReadOnlyAccess

# Create access keys
aws iam create-access-key --user-name john-doe

# List users
aws iam list-users

# Get user details
aws iam get-user --user-name john-doe
```

### Group Management
```bash
# Create IAM Group
aws iam create-group --group-name Developers

# Add user to group
aws iam add-user-to-group \
  --user-name john-doe \
  --group-name Developers

# Attach policy to group
aws iam attach-group-policy \
  --group-name Developers \
  --policy-arn arn:aws:iam::aws:policy/PowerUserAccess

# List group members
aws iam get-group --group-name Developers

# Remove user from group
aws iam remove-user-from-group \
  --user-name john-doe \
  --group-name Developers
```

### Password Policy
```bash
# Set account password policy
aws iam update-account-password-policy \
  --minimum-password-length 12 \
  --require-symbols \
  --require-numbers \
  --require-uppercase-characters \
  --require-lowercase-characters \
  --allow-users-to-change-password \
  --max-password-age 90 \
  --password-reuse-prevention 5 \
  --hard-expiry

# Get current password policy
aws iam get-account-password-policy

# Check password policy compliance
aws iam get-credential-report
```

## IAM Roles

### EC2 Instance Roles
```yaml
# IAM Role для EC2
ec2-role:
  role-name: EC2-WebServer-Role
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          Service: ec2.amazonaws.com
        Action: sts:AssumeRole
        Condition:
          StringEquals:
            aws:SourceAccount: "123456789012"

  managed-policies:
    - arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore
    - arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy

  inline-policies:
    - name: S3Access
      policy-document:
        Version: "2012-10-17"
        Statement:
          - Effect: Allow
            Action:
              - s3:GetObject
              - s3:PutObject
            Resource: arn:aws:s3:::my-app-bucket/*

# Attach role to EC2 instance
ec2-instance-profile:
  instance-profile-name: EC2-WebServer-Profile
  roles:
    - EC2-WebServer-Role
```

### Lambda Execution Roles
```yaml
# IAM Role для Lambda
lambda-role:
  role-name: Lambda-Execution-Role
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          Service: lambda.amazonaws.com
        Action: sts:AssumeRole

  managed-policies:
    - arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

  inline-policies:
    - name: DynamoDBAccess
      policy-document:
        Version: "2012-10-17"
        Statement:
          - Effect: Allow
            Action:
              - dynamodb:GetItem
              - dynamodb:PutItem
              - dynamodb:UpdateItem
              - dynamodb:DeleteItem
            Resource: arn:aws:dynamodb:region:123456789012:table/my-table

    - name: CloudWatchLogs
      policy-document:
        Version: "2012-10-17"
        Statement:
          - Effect: Allow
            Action:
              - logs:CreateLogGroup
              - logs:CreateLogStream
              - logs:PutLogEvents
            Resource: arn:aws:logs:*:*:*
```

### Service-Linked Roles
```yaml
# Service-Linked Role для RDS
rds-service-role:
  role-name: AWSServiceRoleForRDS
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          Service: rds.amazonaws.com
        Action: sts:AssumeRole

  attached-policies:
    - arn:aws:iam::aws:policy/aws-service-role/RDSServiceRolePolicy

# Service-Linked Role для ECS
ecs-service-role:
  role-name: AWSServiceRoleForECS
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          Service: ecs.amazonaws.com
        Action: sts:AssumeRole

  attached-policies:
    - arn:aws:iam::aws:policy/aws-service-role/ECSServiceRolePolicy
```

## Cross-Account Access

### Role-Based Cross-Account Access
```yaml
# Role в trusting account (Account B)
cross-account-role:
  role-name: CrossAccountAccessRole
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          AWS: arn:aws:iam::111111111111:root  # Account A
        Action: sts:AssumeRole
        Condition:
          Bool:
            aws:MultiFactorAuthPresent: "true"
          StringEquals:
            aws:PrincipalAccount: "111111111111"

  managed-policies:
    - arn:aws:iam::aws:policy/ReadOnlyAccess

# Policy в trusted account (Account A) для assume role
assume-role-policy:
  version: "2012-10-17"
  statement:
    - effect: Allow
      action: sts:AssumeRole
      resource: arn:aws:iam::222222222222:role/CrossAccountAccessRole
      condition:
        bool:
          aws:multifactorauthpresent: "true"
```

### Resource-Based Cross-Account Access
```json
// S3 Bucket Policy для cross-account access
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::external-account:root"
      },
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::shared-bucket/*",
      "Condition": {
        "StringEquals": {
          "aws:PrincipalAccount": "external-account-id"
        }
      }
    }
  ]
}

// KMS Key Policy для cross-account encryption
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::external-account:root"
      },
      "Action": [
        "kms:Encrypt",
        "kms:Decrypt",
        "kms:GenerateDataKey*"
      ],
      "Resource": "*"
    }
  ]
}
```

### Organization-Based Access
```yaml
# Organization SCP для cross-account restrictions
organization-scp:
  version: "2012-10-17"
  statement:
    - effect: Deny
      action: sts:AssumeRole
      resource: "*"
      condition:
        string-not-equals:
          aws:principalorgid: "o-1234567890"

# Resource Policy для organization access
resource-policy:
  version: "2012-10-17"
  statement:
    - effect: Allow
      principal:
        aws: "*"
      action: s3:GetObject
      resource: arn:aws:s3:::shared-bucket/*
      condition:
        string-equals:
          aws:principalorgid: "o-1234567890"
```

## MFA и **Security**

### Virtual MFA Devices
```bash
# Enable MFA для user
aws iam create-virtual-mfa-device \
  --virtual-mfa-device-name john-doe-mfa \
  --outfile /tmp/qr.png \
  --bootstrap-method QRCodePNG

# Enable MFA для root account
aws iam enable-mfa-device \
  --user-name <root> \
  --serial-number arn:aws:iam::123456789012:mfa/root-account-mfa \
  --authentication-code-1 123456 \
  --authentication-code-2 789012

# List MFA devices
aws iam list-mfa-devices --user-name john-doe

# Deactivate MFA
aws iam deactivate-mfa-device \
  --user-name john-doe \
  --serial-number arn:aws:iam::123456789012:mfa/john-doe-mfa
```

### Hardware MFA
```bash
# Enable hardware MFA (YubiKey, etc.)
aws iam enable-mfa-device \
  --user-name john-doe \
  --serial-number arn:aws:iam::123456789012:mfa/john-doe-hardware \
  --authentication-code-1 123456 \
  --authentication-code-2 789012

# Resync MFA device
aws iam resync-mfa-device \
  --user-name john-doe \
  --serial-number arn:aws:iam::123456789012:mfa/john-doe-mfa \
  --authentication-code-1 123456 \
  --authentication-code-2 789012 \
  --authentication-code-3 345678 \
  --authentication-code-4 901234
```

### Conditional Access Policies
```json
// MFA required policy
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "*",
      "Resource": "*",
      "Condition": {
        "Bool": {
          "aws:MultiFactorAuthPresent": "true"
        }
      }
    }
  ]
}

// Time-based access
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "*",
      "Resource": "*",
      "Condition": {
        "DateGreaterThan": {
          "aws:CurrentTime": "09:00Z"
        },
        "DateLessThan": {
          "aws:CurrentTime": "17:00Z"
        }
      }
    }
  ]
}

// IP-based restrictions
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "*",
      "Resource": "*",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "192.168.0.0/16"
        }
      }
    }
  ]
}
```

## Access Analyzer

### Account-Level Analyzer
```bash
# Create Access Analyzer
aws accessanalyzer create-analyzer \
  --analyzer-name account-analyzer \
  --type ACCOUNT

# List findings
aws accessanalyzer list-findings \
  --analyzer-arn arn:aws:access-analyzer:region:account:analyzer/account-analyzer

# Get finding details
aws accessanalyzer get-finding \
  --analyzer-arn arn:aws:access-analyzer:region:account:analyzer/account-analyzer \
  --id finding-id

# Archive finding
aws accessanalyzer update-finding \
  --analyzer-arn arn:aws:access-analyzer:region:account:analyzer/account-analyzer \
  --id finding-id \
  --status ARCHIVED
```

### Organization-Level Analyzer
```bash
# Create Organization Access Analyzer
aws accessanalyzer create-analyzer \
  --analyzer-name organization-analyzer \
  --type ORGANIZATION

# List analyzers
aws accessanalyzer list-analyzers

# Generate access preview
aws accessanalyzer create-access-preview \
  --analyzer-arn arn:aws:access-analyzer:region:account:analyzer/organization-analyzer \
  --configurations '{
    "S3Bucket": {
      "bucketName": "test-bucket",
      "bucketPolicy": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"arn:aws:iam::123456789012:user/JohnDoe\"},\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::test-bucket/*\"}]}"
    }
  }'
```

### Custom Policy Checks
```json
// Policy для Access Analyzer checks
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "access-analyzer:CreateAnalyzer",
        "access-analyzer:ListAnalyzers",
        "access-analyzer:ListFindings",
        "access-analyzer:GetFinding",
        "access-analyzer:UpdateFinding"
      ],
      "Resource": "*"
    }
  ]
}
```

## IAM в **Enterprise**

### Permission Boundaries
```json
// Permission Boundary для developers
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ec2:*",
        "s3:*",
        "rds:*",
        "lambda:*"
      ],
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "aws:ResourceTag/Environment": "development"
        }
      }
    },
    {
      "Effect": "Deny",
      "Action": [
        "iam:*",
        "organizations:*"
      ],
      "Resource": "*"
    }
  ]
}

// Permission Boundary для CI/CD
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "ec2:Describe*",
        "cloudformation:*"
      ],
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "aws:ResourceTag/Project": "my-project"
        }
      }
    }
  ]
}
```

### Session Policies
```python
# Session policy для temporary access
import boto3
import json

def assume_role_with_session_policy():
    sts_client = boto3.client('sts')

    session_policy = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "s3:GetObject",
                    "s3:PutObject"
                ],
                "Resource": "arn:aws:s3:::restricted-bucket/*",
                "Condition": {
                    "StringEquals": {
                        "s3:prefix": "readonly/"
                    }
                }
            }
        ]
    }

    response = sts_client.assume_role(
        RoleArn='arn:aws:iam::123456789012:role/MyRole',
        RoleSessionName='session-with-policy',
        Policy=json.dumps(session_policy),
        DurationSeconds=3600
    )

    return response['Credentials']
```

### ABAC (**Attribute-`Based Access` Control**)
```json
// ABAC policy для dynamic access
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::data-bucket/*",
      "Condition": {
        "StringEquals": {
          "aws:PrincipalTag/Department": "${aws:ResourceTag/Department}",
          "aws:PrincipalTag/ClearanceLevel": "${aws:ResourceTag/ClearanceLevel}"
        }
      }
    }
  ]
}

// Resource tagging strategy
resource-tags:
  - key: Department
    value: Engineering
  - key: Project
    value: WebApp
  - key: Environment
    value: Production
  - key: Owner
    value: john.doe@company.com
  - key: CostCenter
    value: CC-12345
```

### IAM Center (`AWS SSO Successor`)
```yaml
# IAM Identity Center (AWS SSO) configuration
identity-center:
  instance:
    name: my-identity-center

  permission-sets:
    - name: ReadOnlyAccess
      description: Read-only access to all AWS accounts
      session-duration: PT8H
      managed-policies:
        - arn:aws:iam::aws:policy/ReadOnlyAccess

    - name: PowerUserAccess
      description: Power user access with some restrictions
      session-duration: PT4H
      inline-policy: |
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "NotAction": [
                "iam:*",
                "organizations:*"
              ],
              "Resource": "*"
            }
          ]
        }

  account-assignments:
    - account: "123456789012"
      permission-set: ReadOnlyAccess
      principal-type: USER
      principal-name: john.doe

    - account: "123456789012"
      permission-set: PowerUserAccess
      principal-type: GROUP
      principal-name: Developers
```

## Решение проблем

### Common IAM Issues
```bash
# Check IAM user permissions
aws iam simulate-principal-policy \
  --policy-source-arn arn:aws:iam::123456789012:user/john-doe \
  --action-names s3:GetObject \
  --resource-arns arn:aws:s3:::my-bucket/my-file.txt

# List attached policies
aws iam list-attached-user-policies --user-name john-doe
aws iam list-attached-group-policies --group-name Developers
aws iam list-attached-role-policies --role-name MyRole

# Check policy evaluation
aws iam get-account-authorization-details > auth-details.json

# Validate policy syntax
aws iam create-policy-version \
  --policy-arn arn:aws:iam::123456789012:policy/MyPolicy \
  --policy-document file://policy.json \
  --set-as-default false
```

### Access Denied Troubleshooting
```bash
# Check CloudTrail for access denials
aws cloudtrail lookup-events \
  --lookup-attributes AttributeKey=EventName,AttributeValue=AssumeRole \
  --start-time 2023-01-01T00:00:00Z \
  --end-time 2023-01-02T00:00:00Z

# Get last accessed information
aws iam generate-service-last-accessed-details \
  --arn arn:aws:iam::123456789012:policy/MyPolicy

aws iam get-service-last-accessed-details \
  --job-id job-id-from-generate-command

# Check role trust relationships
aws iam get-role --role-name MyRole

# Validate assume role permissions
aws sts assume-role \
  --role-arn arn:aws:iam::123456789012:role/MyRole \
  --role-session-name test-session
```

### Security Best Practices Audit
```bash
# Generate credential report
aws iam generate-credential-report
aws iam get-credential-report

# Check for unused credentials
aws iam list-users | jq -r '.Users[].UserName' | \
while read user; do
  echo "Checking $user..."
  aws iam list-access-keys --user-name $user
  aws iam list-mfa-devices --user-name $user
done

# Audit root account usage
aws cloudtrail lookup-events \
  --lookup-attributes AttributeKey=Username,AttributeValue=root

# Check for overly permissive policies
aws iam list-policies --scope Local | \
jq -r '.Policies[].Arn' | \
while read policy_arn; do
  aws iam get-policy-version \
    --policy-arn $policy_arn \
    --version-id v1 | \
  jq '.PolicyVersion.Document' | \
  grep -q '"Effect": "Allow"' && echo "$policy_arn might be too permissive"
done
```

### Performance Optimization
```bash
# Use policy summaries for faster evaluation
aws iam get-account-authorization-details \
  --filter User \
  --max-items 100

# Implement policy caching
# Use IAM roles instead of users for applications
# Minimize policy size and complexity
# Use managed policies where possible

# Monitor IAM metrics
aws cloudwatch list-metrics --namespace AWS/IAM

# Set up CloudWatch alarms for IAM events
aws cloudwatch put-metric-alarm \
  --alarm-name IAM-RootLogin \
  --alarm-description "Root account login detected" \
  --metric-name RootLogin \
  --namespace CloudTrailMetrics \
  --statistic Sum \
  --period 300 \
  --threshold 1 \
  --comparison-operator GreaterThanThreshold
```

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [AWS Basics](aws-basics.md) — основы **AWS**
- [AWS Services](aws-services.md) — сервисы **AWS**
- [AWS Networking](aws-networking.md) — сеть в **AWS**
- [Security](../../security/README.md) — управление ключами и доступом
