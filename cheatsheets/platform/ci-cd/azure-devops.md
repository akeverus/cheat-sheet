---
title: "Azure DevOps"
description: "Azure DevOps - это комплексная платформа от Microsoft для DevOps, которая включает в себя Azure Pipelines для CI/CD, Azure Boards для управления проектами, Azure Repos для Git репозиториев, Azure Artifacts для управления пакетами и Azure Test Plans для тестирования. Этот документ"
tags:
  - platform
  - ci-cd
  - azure-devops
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Azure DevOps

**Azure DevOps** — это комплексная платформа от **Microsoft** для **DevOps**, которая включает в себя **Azure Pipelines** для CI/CD, **Azure Boards** для управления проектами, **Azure Repos** для **Git** репозиториев, **Azure Artifacts** для управления пакетами и **Azure Test Plans** для тестирования. Этот документ охватывает **enterprise-grade** конфигурации, продвинутые **pipeline** паттерны и **best practices** для использования **Azure DevOps** в **production** средах.

## Полезные ссылки
- [Azure DevOps Documentation](https://docs.microsoft.com/azure/devops/)
- [Azure Pipelines](https://docs.microsoft.com/azure/devops/pipelines/)
- [YAML Schema Reference](https://docs.microsoft.com/azure/devops/pipelines/yaml-schema)
- [Azure DevOps REST API](https://docs.microsoft.com/rest/api/azure/devops/)
- [Azure DevOps GitHub](https://github.com/microsoft/azure-pipelines-yaml)

## Содержание

- [Основы Azure DevOps](#основы-azure-devops)
  - [Структура организации и проектов](#структура-организации-и-проектов)
  - [YAML Pipeline основы](#yaml-pipeline-основы)
- [Продвинутые Pipeline паттерны](#продвинутые-pipeline-паттерны)
  - [Multi-stage deployments с approvals](#multi-stage-deployments-с-approvals)
  - [Template-based pipelines](#template-based-pipelines)
  - [Matrix builds и стратегии](#matrix-builds-и-стратегии)
- [Environments и Approvals](#environments-и-approvals)
  - [Environment management](#environment-management)
  - [Advanced approvals](#advanced-approvals)
- [Service Connections и Security](#service-connections-и-security)
  - [Service connections](#service-connections)
  - [Variable groups и secrets](#variable-groups-и-secrets)
- [Multi-repository и Monorepo стратегии](#multi-repository-и-monorepo-стратегии)
  - [Monorepo с path filters](#monorepo-с-path-filters)
  - [Multi-repository pipelines](#multi-repository-pipelines)
- [Monitoring и Analytics](#monitoring-и-analytics)
  - [Pipeline analytics](#pipeline-analytics)
  - [Integration с Azure Monitor](#integration-с-azure-monitor)
- [Enterprise Features](#enterprise-features)
  - [Branch policies и code reviews](#branch-policies-и-code-reviews)
  - [Audit и compliance](#audit-и-compliance)
- [Performance Optimization](#performance-optimization)
  - [Agent pools и scaling](#agent-pools-и-scaling)
  - [Caching strategies](#caching-strategies)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы Azure DevOps

### Структура организации и проектов
Ниже — структура организации и проектов **Azure DevOps** (текст).
```text
azure-devops-organization/
├── Projects/
│   ├── MyApp/
│   │   ├── Pipelines/
│   │   │   ├── ci-pipeline.yml
│   │   │   ├── cd-pipeline.yml
│   │   │   └── pr-validation.yml
│   │   ├── Boards/
│   │   │   ├── Work Items/
│   │   │   ├── Sprints/
│   │   │   └── Queries/
│   │   ├── Repos/
│   │   │   ├── branches/
│   │   │   ├── pull requests/
│   │   │   └── policies/
│   │   ├── Artifacts/
│   │   │   ├── feeds/
│   │   │   └── packages/
│   │   └── Test Plans/
│   │       ├── Test Cases/
│   │       └── Test Suites/
│   └── Shared Services/
│       ├── Variable Groups/
│       ├── Service Connections/
│       └── Agent Pools/
└── Organization Settings/
    ├── Security/
    ├── Policies/
    └── Extensions/
```

### YAML Pipeline основы
Пример базового **azure-pipelines.yml** (YAML).
```yaml
# azure-pipelines.yml - базовая конфигурация
name: $(BuildDefinitionName)_$(SourceBranchName)_$(Date:yyyyMMdd)$(Rev:.r)

trigger:
  branches:
    include:
    - main
    - develop
    exclude:
    - feature/*

pr:
  branches:
    include:
    - main
    - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
  - group: 'global-variables'
  - name: 'buildConfiguration'
    value: 'Release'
  - name: 'dotnetSdkVersion'
    value: '7.0.x'

stages:
- stage: Build
  displayName: 'Build Stage'
  jobs:
  - job: Build
    displayName: 'Build and Test'
    steps:
    - task: UseDotNet@2
      displayName: 'Install .NET SDK'
      inputs:
        version: '$(dotnetSdkVersion)'

    - task: DotNetCoreCLI@2
      displayName: 'Restore NuGet packages'
      inputs:
        command: 'restore'
        projects: '/*.csproj'

    - task: DotNetCoreCLI@2
      displayName: 'Build projects'
      inputs:
        command: 'build'
        projects: '/*.csproj'
        arguments: '--configuration $(buildConfiguration)'

    - task: DotNetCoreCLI@2
      displayName: 'Run unit tests'
      inputs:
        command: 'test'
        projects: '/*Tests.csproj'
        arguments: '--configuration $(buildConfiguration) --collect "Code coverage"'

    - publish: '$(Build.ArtifactStagingDirectory)/test-results'
      artifact: 'test-results'
      condition: succeededOrFailed()

- stage: DeployStaging
  displayName: 'Deploy to Staging'
  dependsOn: Build
  condition: succeeded()
  jobs:
  - deployment: Deploy
    displayName: 'Deploy to Staging Environment'
    environment: 'staging'
    strategy:
      runOnce:
        deploy:
          steps:
          - download: current
            artifact: 'drop'
          - task: AzureRmWebAppDeployment@4
            displayName: 'Deploy to Azure Web App'
            inputs:
              azureSubscription: 'Azure-Subscription'
              appType: 'webApp'
              webAppName: 'myapp-staging'
              package: '$(Pipeline.Workspace)/drop//*.zip'

- stage: DeployProduction
  displayName: 'Deploy to Production'
  dependsOn: DeployStaging
  condition: succeeded()
  jobs:
  - deployment: Deploy
    displayName: 'Deploy to Production Environment'
    environment: 'production'
    strategy:
      runOnce:
        deploy:
          steps:
          - download: current
            artifact: 'drop'
          - task: AzureRmWebAppDeployment@4
            displayName: 'Deploy to Azure Web App'
            inputs:
              azureSubscription: 'Azure-Subscription'
              appType: 'webApp'
              webAppName: 'myapp-production'
              package: '$(Pipeline.Workspace)/drop//*.zip'
```

## Продвинутые Pipeline паттерны

### Multi-stage deployments с approvals
```yaml
# azure-pipelines-advanced.yml - продвинутые deployment паттерны
name: $(BuildDefinitionName)_$(SourceBranchName)_$(Date:yyyyMMdd)$(Rev:.r)

trigger:
  branches:
    include:
    - main
    - release/*

parameters:
- name: environment
  displayName: 'Target Environment'
  type: string
  default: 'staging'
  values:
  - staging
  - production
  - dr

variables:
- group: 'global-variables'
- name: 'isMainBranch'
  value: $[eq(variables['Build.SourceBranch'], 'refs/heads/main')]
- name: 'isReleaseBranch'
  value: $[startsWith(variables['Build.SourceBranch'], 'refs/heads/release/')]

stages:
- stage: Build
  displayName: '🔨 Build Stage'
  jobs:
  - job: Build
    displayName: 'Build Application'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - checkout: self
      submodules: true

    - task: UseDotNet@2
      inputs:
        version: '7.0.x'

    - task: DotNetCoreCLI@2
      displayName: 'Build'
      inputs:
        command: 'build'
        projects: '/*.csproj'
        arguments: '--configuration Release --no-restore'

    - task: DotNetCoreCLI@2
      displayName: 'Test'
      inputs:
        command: 'test'
        projects: '/*Tests.csproj'
        arguments: '--configuration Release --no-build --collect "Code coverage" --results-directory $(Agent.TempDirectory)/TestResults'

    - task: PublishTestResults@2
      inputs:
        testResultsFiles: '/*.trx'
        testRunTitle: 'Unit Tests'
        failTaskOnFailedTests: true

    - task: PublishCodeCoverageResults@1
      inputs:
        codeCoverageTool: 'Cobertura'
        summaryFileLocation: '$(Agent.TempDirectory)/TestResults/*/coverage.cobertura.xml'

    - task: DotNetCoreCLI@2
      displayName: 'Publish'
      inputs:
        command: 'publish'
        publishWebProjects: true
        arguments: '--configuration Release --output $(Build.ArtifactStagingDirectory)'
        zipAfterPublish: true

    - publish: $(Build.ArtifactStagingDirectory)
      artifact: 'drop'

- stage: SecurityScan
  displayName: '🔒 Security Scanning'
  dependsOn: Build
  jobs:
  - job: Security
    displayName: 'Security Analysis'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - checkout: none

    - download: current
      artifact: 'drop'

    - task: CredScan@3
      displayName: 'Run CredScan'
      inputs:
        toolMajorVersion: 'V2'

    - task: ComponentGovernanceComponentDetection@0
      displayName: 'Component Detection'

    - task: PublishSecurityAnalysisLogs@3
      displayName: 'Publish Security Analysis Logs'

    - task: SdtReport@2
      displayName: 'Create Security Analysis Report'
      inputs:
        SdtReportXml: '$(Agent.TempDirectory)/sdt/sdt-report.xml'
        SdtReportHtml: '$(Agent.TempDirectory)/sdt/sdt-report.html'

    - publish: $(Agent.TempDirectory)/sdt
      artifact: 'security-scan-results'

- stage: DeployStaging
  displayName: '🚀 Deploy to Staging'
  dependsOn: SecurityScan
  condition: succeeded()
  jobs:
  - deployment: DeployStaging
    displayName: 'Deploy to Staging'
    environment: 'staging'
    pool:
      vmImage: 'ubuntu-latest'
    strategy:
      runOnce:
        deploy:
          steps:
          - download: current
            artifact: 'drop'

          - task: AzureRmWebAppDeployment@4
            displayName: 'Deploy to Azure Web App'
            inputs:
              azureSubscription: 'Azure-Subscription'
              appType: 'webApp'
              webAppName: 'myapp-staging'
              package: '$(Pipeline.Workspace)/drop//*.zip'
              enableCustomDeployment: true
              deploymentType: 'webDeploy'

          - task: AzureAppServiceManage@0
            displayName: 'Swap Slots'
            inputs:
              azureSubscription: 'Azure-Subscription'
              webAppName: 'myapp-staging'
              sourceSlot: 'staging'
              swapWithProduction: true

- stage: IntegrationTests
  displayName: '🧪 Integration Tests'
  dependsOn: DeployStaging
  jobs:
  - job: IntegrationTests
    displayName: 'Run Integration Tests'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - task: AzureRmWebAppDeployment@4
      displayName: 'Deploy Test Application'
      inputs:
        azureSubscription: 'Azure-Subscription'
        appType: 'webApp'
        webAppName: 'myapp-test'
        package: '$(Pipeline.Workspace)/drop//*.zip'

    - task: VSTest@2
      displayName: 'Run Integration Tests'
      inputs:
        testAssemblyVer2: |
          /*IntegrationTests.dll
          !/obj/
        codeCoverageEnabled: true
        runInParallel: true
        testRunTitle: 'Integration Tests'
        platform: '$(buildPlatform)'
        configuration: '$(buildConfiguration)'

- stage: ApproveProduction
  displayName: '✅ Production Approval'
  dependsOn: IntegrationTests
  jobs:
  - job: WaitForApproval
    displayName: 'Wait for manual approval'
    pool: server
    steps:
    - task: ManualValidation@0
      inputs:
        notifyUsers: 'devops-team@company.com'
        instructions: 'Please review the staging deployment and approve production deployment'
        onTimeout: 'reject'

- stage: DeployProduction
  displayName: '🎯 Deploy to Production'
  dependsOn: ApproveProduction
  condition: succeeded()
  jobs:
  - deployment: DeployProduction
    displayName: 'Deploy to Production'
    environment: 'production'
    pool:
      vmImage: 'ubuntu-latest'
    strategy:
      runOnce:
        deploy:
          steps:
          - download: current
            artifact: 'drop'

          - task: AzureRmWebAppDeployment@4
            displayName: 'Blue-Green Deployment'
            inputs:
              azureSubscription: 'Azure-Subscription'
              appType: 'webApp'
              webAppName: 'myapp-production'
              package: '$(Pipeline.Workspace)/drop//*.zip'
              enableCustomDeployment: true
              deploymentType: 'webDeploy'

          - task: AzureAppServiceManage@0
            displayName: 'Traffic Routing'
            inputs:
              azureSubscription: 'Azure-Subscription'
              webAppName: 'myapp-production'
              sourceSlot: 'production'
              action: 'Swap'
```

### Template-based pipelines
```yaml
# templates/build-template.yml
parameters:
- name: 'projectPath'
  type: string
  default: '/*.csproj'
- name: 'testProjectPath'
  type: string
  default: '/*Tests.csproj'
- name: 'configuration'
  type: string
  default: 'Release'
- name: 'dotnetVersion'
  type: string
  default: '7.0.x'

steps:
- task: UseDotNet@2
  displayName: 'Install .NET SDK ${{ parameters.dotnetVersion }}'
  inputs:
    version: '${{ parameters.dotnetVersion }}'

- task: DotNetCoreCLI@2
  displayName: 'Restore NuGet packages'
  inputs:
    command: 'restore'
    projects: '${{ parameters.projectPath }}'

- task: DotNetCoreCLI@2
  displayName: 'Build projects'
  inputs:
    command: 'build'
    projects: '${{ parameters.projectPath }}'
    arguments: '--configuration ${{ parameters.configuration }} --no-restore'

- task: DotNetCoreCLI@2
  displayName: 'Run tests'
  inputs:
    command: 'test'
    projects: '${{ parameters.testProjectPath }}'
    arguments: '--configuration ${{ parameters.configuration }} --no-build --collect "Code coverage"'

- task: PublishTestResults@2
  displayName: 'Publish test results'
  inputs:
    testResultsFiles: '/*.trx'
    testRunTitle: 'Unit Tests'
  condition: succeededOrFailed()

- task: PublishCodeCoverageResults@1
  displayName: 'Publish code coverage'
  inputs:
    codeCoverageTool: 'Cobertura'
    summaryFileLocation: '$(Agent.TempDirectory)//coverage.cobertura.xml'

- task: DotNetCoreCLI@2
  displayName: 'Publish artifacts'
  inputs:
    command: 'publish'
    publishWebProjects: true
    arguments: '--configuration ${{ parameters.configuration }} --output $(Build.ArtifactStagingDirectory)'
    zipAfterPublish: true
```

```yaml
# azure-pipelines.yml - использование шаблонов
trigger:
  branches:
    include:
    - main
    - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
- group: 'global-variables'

stages:
- stage: Build
  jobs:
  - job: BuildWebApp
    steps:
    - template: templates/build-template.yml
      parameters:
        projectPath: 'src/WebApp/WebApp.csproj'
        testProjectPath: 'src/WebApp.Tests/WebApp.Tests.csproj'
        dotnetVersion: '7.0.x'

  - job: BuildApi
    steps:
    - template: templates/build-template.yml
      parameters:
        projectPath: 'src/Api/Api.csproj'
        testProjectPath: 'src/Api.Tests/Api.Tests.csproj'
        dotnetVersion: '7.0.x'

- stage: Package
  dependsOn: Build
  jobs:
  - job: Package
    steps:
    - download: current
      artifact: 'drop'

    - task: ArchiveFiles@2
      inputs:
        rootFolderOrFile: '$(Pipeline.Workspace)/drop'
        includeRootFolder: false
        archiveType: 'zip'
        archiveFile: '$(Build.ArtifactStagingDirectory)/app.zip'
        replaceExistingArchive: true

    - publish: $(Build.ArtifactStagingDirectory)
      artifact: 'package'
```

### Matrix builds и стратегии
```yaml
# azure-pipelines-matrix.yml - matrix builds
trigger:
  branches:
    include:
    - main

strategy:
  matrix:
    'Windows_Latest_Chrome':
      imageName: 'windows-latest'
      browser: 'chrome'
    'Windows_Latest_Firefox':
      imageName: 'windows-latest'
      browser: 'firefox'
    'Windows_Latest_Edge':
      imageName: 'windows-latest'
      browser: 'edge'
    'Ubuntu_Latest_Chrome':
      imageName: 'ubuntu-latest'
      browser: 'chrome'
    'Ubuntu_Latest_Firefox':
      imageName: 'ubuntu-latest'
      browser: 'firefox'
    'macOS_Latest_Safari':
      imageName: 'macOS-latest'
      browser: 'safari'

pool:
  vmImage: $(imageName)

steps:
- task: UseDotNet@2
  inputs:
    version: '7.0.x'

- task: DotNetCoreCLI@2
  displayName: 'Install Selenium dependencies'
  inputs:
    command: 'custom'
    custom: 'tool'
    arguments: 'install --global Selenium.WebDriver.ChromeDriver'

- script: |
    dotnet test --filter "Browser=$(browser)" --logger "trx;LogFileName=test-results.trx"
  displayName: 'Run $(browser) tests on $(imageName)'

- task: PublishTestResults@2
  inputs:
    testResultsFiles: '/*.trx'
    testRunTitle: 'Cross-browser Tests - $(browser) on $(imageName)'
  condition: succeededOrFailed()
```

## Environments и Approvals

### Environment management
```yaml
# azure-pipelines-environments.yml - управление environments
trigger:
  branches:
    include:
    - main
    - release/*

parameters:
- name: 'environment'
  displayName: 'Target Environment'
  type: string
  default: 'staging'
  values:
  - staging
  - production
  - dr

stages:
- stage: Build
  displayName: 'Build'
  jobs:
  - job: Build
    steps:
    - script: echo "Building application..."
    - publish: $(Build.ArtifactStagingDirectory)
      artifact: 'drop'

- ${{ if eq(parameters.environment, 'staging') }}:
  - stage: DeployStaging
    displayName: 'Deploy to Staging'
    dependsOn: Build
    jobs:
    - deployment: Deploy
      environment: 'staging'
      strategy:
        runOnce:
          deploy:
            steps:
            - script: echo "Deploying to staging..."

- ${{ if eq(parameters.environment, 'production') }}:
  - stage: DeployProduction
    displayName: 'Deploy to Production'
    dependsOn: Build
    jobs:
    - deployment: Deploy
      environment: 'production'
      strategy:
        runOnce:
          deploy:
            steps:
            - script: echo "Deploying to production..."

- ${{ if eq(parameters.environment, 'dr') }}:
  - stage: DeployDR
    displayName: 'Deploy to DR'
    dependsOn: Build
    jobs:
    - deployment: Deploy
      environment: 'dr'
      strategy:
        runOnce:
          deploy:
            steps:
            - script: echo "Deploying to DR site..."
```

### Advanced approvals
```yaml
# azure-pipelines-approvals.yml - продвинутые approvals
stages:
- stage: Build
  jobs:
  - job: Build
    steps:
    - script: echo "Building..."

- stage: QualityGate
  dependsOn: Build
  jobs:
  - job: SecurityScan
    steps:
    - script: echo "Running security scan..."

  - job: CodeQuality
    steps:
    - script: echo "Running code quality checks..."

- stage: Approval
  dependsOn: QualityGate
  jobs:
  - job: ManualApproval
    displayName: 'Wait for approval'
    pool: server
    steps:
    - task: ManualValidation@0
      name: 'ManualApproval'
      inputs:
        notifyUsers: 'devops-team@company.com, security-team@company.com'
        instructions: 'Please review the build artifacts and security scan results. Approve for production deployment.'
        onTimeout: 'reject'
        timeout: '30'  # minutes

- stage: Deploy
  dependsOn: Approval
  condition: succeeded()
  jobs:
  - deployment: Production
    environment: 'production'
    strategy:
      runOnce:
        deploy:
          steps:
          - script: echo "Deploying to production..."
```

## Service Connections и Security

### Service connections
```yaml
# azure-pipelines-service-connections.yml - service connections
trigger:
  branches:
    include:
    - main

pool:
  vmImage: 'ubuntu-latest'

variables:
- group: 'azure-credentials'
- group: 'docker-registry'

steps:
- task: AzureCLI@2
  displayName: 'Azure CLI - Login'
  inputs:
    azureSubscription: 'Azure-Subscription'
    scriptType: 'bash'
    scriptLocation: 'inlineScript'
    inlineScript: |
      az account show

- task: Docker@2
  displayName: 'Docker - Login'
  inputs:
    containerRegistry: 'docker-registry-connection'
    command: 'login'

- task: AzureRmWebAppDeployment@4
  displayName: 'Deploy to Azure Web App'
  inputs:
    azureSubscription: 'Azure-Subscription'
    appType: 'webApp'
    webAppName: 'my-web-app'
    package: '$(Build.ArtifactStagingDirectory)//*.zip'

- task: AzureKeyVault@2
  displayName: 'Download secrets from Key Vault'
  inputs:
    azureSubscription: 'Azure-Subscription'
    keyVaultName: 'my-key-vault'
    secretsFilter: 'db-password, api-key'
    runAsPreJob: false

- task: AzureResourceManagerTemplateDeployment@3
  displayName: 'Deploy ARM template'
  inputs:
    deploymentScope: 'Resource Group'
    azureResourceManagerConnection: 'Azure-Subscription'
    subscriptionId: '$(azureSubscriptionId)'
    action: 'Create Or Update Resource Group'
    resourceGroupName: 'my-resource-group'
    location: 'East US'
    templateLocation: 'Linked artifact'
    csmFile: 'azuredeploy.json'
    csmParametersFile: 'azuredeploy.parameters.json'
    overrideParameters: '-environment staging'
```

### Variable groups и secrets
```yaml
# azure-pipelines-variables.yml - управление переменными
variables:
- group: 'global-variables'
- group: 'environment-specific'
- name: 'buildNumber'
  value: $[counter('build-counter', 100)]
- name: 'isMainBranch'
  value: $[eq(variables['Build.SourceBranch'], 'refs/heads/main')]
- name: 'shortCommit'
  value: $[substring(variables['Build.SourceVersion'], 0, 8)]

stages:
- stage: Build
  variables:
  - group: 'build-variables'
  - name: 'artifactName'
    value: 'myapp-$(buildNumber)-$(shortCommit)'
  jobs:
  - job: Build
    variables:
    - name: 'configuration'
      value: 'Release'
    steps:
    - script: |
        echo "Building $(artifactName) in $(configuration) mode"
        echo "Main branch: $(isMainBranch)"
        echo "Short commit: $(shortCommit)"

- stage: Deploy
  condition: eq(variables.isMainBranch, 'true')
  variables:
  - group: 'deployment-variables'
  jobs:
  - job: Deploy
    steps:
    - script: |
        echo "Deploying $(artifactName)"
        # Use secret variables from variable group
        echo "Using database: $(database-server)"
        # Note: Secret variables are not expanded in script output
```

## Multi-repository и Monorepo стратегии

### Monorepo с path filters
```yaml
# azure-pipelines-monorepo.yml - monorepo стратегия
trigger:
  branches:
    include:
    - main
    - develop
  paths:
    include:
    - services/web-app/
    - shared-libs/
    - infrastructure/

pr:
  branches:
    include:
    - main
    - develop
  paths:
    include:
    - services/web-app/
    - shared-libs/

variables:
- name: 'servicesChanged'
  value: $[containsValue(getPathToCommits(getArtifactDetails('GitDiff')), 'services/')]
- name: 'libsChanged'
  value: $[containsValue(getPathToCommits(getArtifactDetails('GitDiff')), 'shared-libs/')]
- name: 'infraChanged'
  value: $[containsValue(getPathToCommits(getArtifactDetails('GitDiff')), 'infrastructure/')]

stages:
- stage: BuildSharedLibs
  condition: eq(variables.libsChanged, 'true')
  jobs:
  - job: BuildLibs
    steps:
    - checkout: self
      path: 's'
    - script: |
        cd shared-libs
        dotnet build --configuration Release

- stage: BuildServices
  dependsOn: BuildSharedLibs
  condition: or(eq(variables.servicesChanged, 'true'), eq(variables.libsChanged, 'true'))
  jobs:
  - job: BuildWebApp
    steps:
    - checkout: self
      path: 's'
    - script: |
        cd services/web-app
        dotnet restore
        dotnet build --configuration Release

- stage: Test
  dependsOn: BuildServices
  jobs:
  - job: UnitTests
    steps:
    - checkout: none
    - download: current
      artifact: 'web-app'
    - script: |
        cd $(Pipeline.Workspace)/web-app
        dotnet test

- stage: Deploy
  dependsOn: Test
  condition: and(succeeded(), eq(variables['Build.SourceBranch'], 'refs/heads/main'))
  jobs:
  - deployment: DeployWebApp
    environment: 'production'
    strategy:
      runOnce:
        deploy:
          steps:
          - script: |
              echo "Deploying web app and infrastructure changes"
```

### Multi-repository pipelines
```yaml
# azure-pipelines-multi-repo.yml - multi-repository
resources:
  repositories:
  - repository: templates
    type: git
    name: DevOps/templates
    ref: main
  - repository: infrastructure
    type: git
    name: DevOps/infrastructure
    ref: main

trigger:
  branches:
    include:
    - main

stages:
- template: templates/build.yml@templates
  parameters:
    serviceName: 'web-app'
    repository: 'self'

- template: infrastructure/deploy.yml@infrastructure
  parameters:
    environment: 'production'
    serviceName: 'web-app'
```

## Monitoring и Analytics

### Pipeline analytics
```yaml
# azure-pipelines-analytics.yml - сбор метрик pipeline
trigger:
  branches:
    include:
    - main
    - develop

variables:
- name: 'pipelineStartTime'
  value: ''

stages:
- stage: Build
  jobs:
  - job: Build
    variables:
    - name: 'buildStartTime'
      value: ''
    steps:
    - script: |
        echo "##vso[task.setvariable variable=buildStartTime]$(date +%s)"
        echo "##vso[task.setvariable variable=pipelineStartTime]$(date +%s)"
      displayName: 'Set start times'

    - script: echo "Building application..."
      displayName: 'Build'

    - script: |
        buildDuration=$(( $(date +%s) - $(buildStartTime) ))
        echo "Build duration: $buildDuration seconds"
        echo "##vso[task.setvariable variable=buildDuration]$buildDuration"
      displayName: 'Calculate build duration'

- stage: Test
  dependsOn: Build
  jobs:
  - job: Test
    steps:
    - script: |
        testStartTime=$(date +%s)
        echo "Running tests..."
        sleep 30  # Simulate test execution
        testDuration=$(( $(date +%s) - testStartTime ))
        echo "Test duration: $testDuration seconds"
      displayName: 'Run tests'

- stage: Analytics
  dependsOn: [Build, Test]
  condition: always()
  jobs:
  - job: CollectMetrics
    steps:
    - script: |
        totalDuration=$(( $(date +%s) - $(pipelineStartTime) ))
        echo "Total pipeline duration: $totalDuration seconds"

        # Send metrics to Azure Application Insights
        curl -X POST "https://dc.services.visualstudio.com/v2/track" \
          -H "Content-Type: application/json" \
          -d "{
            \"name\": \"Microsoft.ApplicationInsights.Metric\",
            \"time\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\",
            \"iKey\": \"${APP_INSIGHTS_KEY}\",
            \"data\": {
              \"baseType\": \"MetricData\",
              \"baseData\": {
                \"metrics\": [
                  {
                    \"name\": \"pipeline.duration\",
                    \"value\": $totalDuration,
                    \"properties\": {
                      \"pipeline\": \"${BUILD_DEFINITIONNAME}\",
                      \"branch\": \"${BUILD_SOURCEBRANCHNAME}\",
                      \"result\": \"${AGENT_JOBSTATUS}\"
                    }
                  }
                ]
              }
            }
          }"
      displayName: 'Send analytics data'
```

### Integration с Azure Monitor
```yaml
# azure-pipelines-monitoring.yml - интеграция с Azure Monitor
trigger:
  branches:
    include:
    - main

pool:
  vmImage: 'ubuntu-latest'

steps:
- task: AzureCLI@2
  displayName: 'Configure Azure Monitor'
  inputs:
    azureSubscription: 'Azure-Subscription'
    scriptType: 'bash'
    scriptLocation: 'inlineScript'
    inlineScript: |
      # Create Application Insights resource
      az monitor app-insights component create \
        --app myapp \
        --location eastus \
        --resource-group myrg \
        --application-type web

      # Configure alerts
      az monitor metrics alert create \
        --name "HighResponseTime" \
        --resource /subscriptions/.../myapp \
        --condition "avg ApplicationInsights/request/duration > 5000" \
        --description "Response time is too high"

- task: AzureMonitor@1
  displayName: 'Send custom metrics'
  inputs:
    azureSubscription: 'Azure-Subscription'
    resourceGroupName: 'myrg'
    resourceName: 'myapp'
    metrics: |
      [
        {
          "name": "build.duration",
          "value": "$(buildDuration)",
          "timestamp": "$(Build.BuildCompletionTime)",
          "properties": {
            "buildId": "$(Build.BuildId)",
            "branch": "$(Build.SourceBranchName)"
          }
        }
      ]
```

## Enterprise Features

### Branch policies и code reviews
```yaml
# azure-pipelines-policy.yml - интеграция с branch policies
trigger:
  branches:
    include:
    - main
    - release/*

pr:
  branches:
    include:
    - main
    - release/*
  autoCancel: true

variables:
- name: 'isPr'
  value: $[eq(variables['Build.Reason'], 'PullRequest')]
- name: 'targetBranch'
  value: $[variables['System.PullRequest.TargetBranchName']]

stages:
- stage: Validate
  condition: eq(variables.isPr, 'true')
  jobs:
  - job: CodeReview
    displayName: 'Automated Code Review'
    steps:
    - checkout: none

    - task: pullrequest@1
      displayName: 'Create PR comment'
      inputs:
        repository: 'self'
        comment: '🔍 Running automated code review...'

    - script: |
        echo "Running code quality checks..."
        # Simulate code quality analysis
        echo "##vso[task.setvariable variable=codeQualityScore]85"

    - script: |
        score=$(codeQualityScore)
        if [ "$score" -lt 70 ]; then
          echo "Code quality score too low: $score"
          exit 1
        fi
      displayName: 'Validate code quality'

    - task: pullrequest@1
      displayName: 'Update PR status'
      inputs:
        repository: 'self'
        status: 'completed'
        result: 'succeeded'
        comment: '✅ Code review completed successfully'

- stage: Build
  jobs:
  - job: Build
    steps:
    - checkout: self
    - script: echo "Building application..."
```

### Audit и compliance
```yaml
# azure-pipelines-audit.yml - audit и compliance
trigger:
  branches:
    include:
    - main
    - develop

variables:
- group: 'audit-variables'

stages:
- stage: Build
  jobs:
  - job: Build
    steps:
    - checkout: self

    - script: |
        # Record build start in audit log
        audit_data=$(cat <<EOF
        {
          "timestamp": "$(date -Iseconds)",
          "event": "build_started",
          "pipeline": "$(Build.DefinitionName)",
          "buildId": "$(Build.BuildId)",
          "branch": "$(Build.SourceBranchName)",
          "commit": "$(Build.SourceVersion)",
          "triggeredBy": "$(Build.RequestedFor)",
          "agent": "$(Agent.Name)"
        }
        EOF
        )
        echo "$audit_data" >> audit-log.json
      displayName: 'Start audit logging'

    - script: echo "Building application..."
      displayName: 'Build application'

- stage: Security
  dependsOn: Build
  jobs:
  - job: Compliance
    steps:
    - checkout: none
    - download: current
      artifact: 'drop'

    - script: |
        # Run compliance checks
        echo "Running compliance validation..."

        # Check for sensitive data
        if grep -r "password\|secret\|key" $(Pipeline.Workspace)/drop/; then
          echo "Sensitive data found in artifacts!"
          exit 1
        fi

        # Validate artifact signatures
        echo "Validating artifact signatures..."
      displayName: 'Compliance validation'

    - script: |
        # Record compliance results
        compliance_result=$(cat <<EOF
        {
          "timestamp": "$(date -Iseconds)",
          "event": "compliance_check",
          "buildId": "$(Build.BuildId)",
          "result": "passed",
          "checks": ["sensitive_data", "artifact_signing"]
        }
        EOF
        )
        echo "$compliance_result" >> audit-log.json
      displayName: 'Record compliance results'

- stage: Deploy
  dependsOn: Security
  jobs:
  - job: Deploy
    steps:
    - script: echo "Deploying application..."
      displayName: 'Deploy'

    - script: |
        # Final audit entry
        deploy_result=$(cat <<EOF
        {
          "timestamp": "$(date -Iseconds)",
          "event": "deployment_completed",
          "buildId": "$(Build.BuildId)",
          "environment": "production",
          "result": "success"
        }
        EOF
        )
        echo "$deploy_result" >> audit-log.json
      displayName: 'Record deployment'

    - publish: audit-log.json
      artifact: 'audit-logs'
      displayName: 'Publish audit logs'
```

## Performance Optimization

### Agent pools и scaling
```yaml
# azure-pipelines-scaling.yml - оптимизация производительности
trigger:
  branches:
    include:
    - main

# Use Microsoft-hosted agents for faster builds
pool:
  vmImage: 'ubuntu-latest'

variables:
- name: 'isMainBranch'
  value: $[eq(variables['Build.SourceBranch'], 'refs/heads/main')]

stages:
- stage: FastFeedback
  displayName: '🚀 Fast Feedback'
  jobs:
  - job: Lint
    displayName: 'Code Quality'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - script: |
        echo "Running fast lint checks..."
        # Quick syntax and style checks
      timeoutInMinutes: 5

  - job: UnitTests
    displayName: 'Unit Tests'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - script: |
        echo "Running unit tests..."
        # Fast unit tests only
      timeoutInMinutes: 10

- stage: FullBuild
  displayName: '🔨 Full Build'
  dependsOn: FastFeedback
  condition: succeeded()
  jobs:
  - job: Build
    displayName: 'Build Application'
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - script: echo "Full build with all components..."
      timeoutInMinutes: 30

  - job: IntegrationTests
    displayName: 'Integration Tests'
    pool:
      vmImage: 'ubuntu-latest'
    parallelism: 3  # Run tests in parallel
    steps:
    - script: |
        echo "Running integration tests on agent $(System.JobPositionInPhase) of $(System.TotalJobsInPhase)"
        # Split tests across parallel jobs
      timeoutInMinutes: 20

  - job: PerformanceTests
    displayName: 'Performance Tests'
    pool:
      vmImage: 'windows-latest'  # Use Windows for performance testing
    steps:
    - script: echo "Running performance tests..."
      timeoutInMinutes: 45

- stage: Deploy
  displayName: '🚀 Deploy'
  dependsOn: FullBuild
  condition: and(succeeded(), eq(variables.isMainBranch, 'true'))
  jobs:
  - deployment: Production
    displayName: 'Production Deployment'
    environment: 'production'
    pool:
      vmImage: 'ubuntu-latest'
    strategy:
      runOnce:
        deploy:
          steps:
          - script: echo "Deploying to production..."
```

### Caching strategies
```yaml
# azure-pipelines-caching.yml - продвинутое кэширование
trigger:
  branches:
    include:
    - main

pool:
  vmImage: 'ubuntu-latest'

variables:
- name: 'cacheKey'
  value: 'dotnet|$(Agent.OS)|$(Build.SourceBranchName)'
- name: 'nugetCacheKey'
  value: 'nuget|$(Agent.OS)|$(Build.SourceVersion)'

stages:
- stage: Build
  jobs:
  - job: Build
    variables:
    - name: 'restoreCacheKey'
      value: ''
    steps:
    - task: Cache@2
      displayName: 'Cache NuGet packages'
      inputs:
        key: 'nuget | "$(Agent.OS)" | /packages.lock.json'
        restoreKeys: |
          nuget | "$(Agent.OS)"
          nuget
        path: $(NUGET_PACKAGES)

    - task: Cache@2
      displayName: 'Cache .NET tools'
      inputs:
        key: 'dotnet-tools | "$(Agent.OS)" | global.json'
        path: $(DOTNET_CLI_HOME)/.dotnet/tools

    - task: Cache@2
      displayName: 'Cache node_modules'
      inputs:
        key: 'npm | "$(Agent.OS)" | package-lock.json'
        restoreKeys: |
          npm | "$(Agent.OS)"
          npm
        path: $(npm_config_cache)

    - task: Cache@2
      displayName: 'Cache Maven repository'
      inputs:
        key: 'maven | "$(Agent.OS)" | pom.xml'
        restoreKeys: |
          maven | "$(Agent.OS)"
          maven
        path: ~/.m2/repository

    - script: |
        echo "Cache status:"
        du -sh ~/.m2/repository || echo "Maven cache not found"
        du -sh $(npm_config_cache) || echo "NPM cache not found"
        du -sh $(NUGET_PACKAGES) || echo "NuGet cache not found"
      displayName: 'Check cache sizes'

    - script: echo "Building application..."
      displayName: 'Build'
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [Jenkins](jenkins.md) — альтернативная **CI/CD** платформа
- [GitLab CI](gitlab-ci.md)
- [GitHub Actions](github-actions.md) — **CI/CD** в **GitHub**
- [Azure Basics](../cloud-providers/azure-basics.md) — **Microsoft Azure**
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
