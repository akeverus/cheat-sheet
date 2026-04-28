---
title: "Jenkins"
description: "Jenkins - это открытый сервер автоматизации для continuous integration и continuous delivery (CI/CD). Он позволяет автоматизировать процесс сборки, тестирования и развертывания программного обеспечения. Этот документ охватывает enterprise-grade конфигурации, продвинутые pipeline "
tags:
  - platform
  - ci-cd
  - jenkins
type: "overview"
difficulty: "intermediate"
aliases:
  - "Jenkins"
prerequisites: []
next:
  - "[[github-actions]]"
updated: "2026-04-20"
---
# Jenkins

**Jenkins** — это открытый сервер автоматизации для **continuous integration** и **continuous delivery** (CI/CD). Он позволяет автоматизировать процесс сборки, тестирования и развертывания программного обеспечения. Этот документ охватывает **enterprise-grade** конфигурации, продвинутые **pipeline** паттерны и **best practices** для использования **Jenkins** в **production** средах.

## Полезные ссылки
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkins Pipeline](https://www.jenkins.io/doc/book/pipeline/)
- [Jenkins Job DSL](https://github.com/jenkinsci/job-dsl-plugin)
- [Jenkins Configuration as Code](https://github.com/jenkinsci/configuration-as-code-plugin)
- [Jenkins Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
- [Jenkins Kubernetes Plugin](https://plugins.jenkins.io/kubernetes/)

## Содержание

- [Основы Jenkins](#основы-jenkins)
  - [Установка и настройка](#установка-и-настройка)
  - [Конфигурация безопасности](#конфигурация-безопасности)
  - [Управление плагинами](#управление-плагинами)
- [Pipeline as Code](#pipeline-as-code)
  - [Declarative Pipeline](#declarative-pipeline)
  - [Scripted Pipeline](#scripted-pipeline)
- [Job DSL](#job-dsl)
  - [Создание джобов программно](#создание-джобов-программно)
  - [Shared libraries](#shared-libraries)
- [Distributed Builds](#distributed-builds)
  - [Jenkins agents (slaves)](#jenkins-agents-slaves)
  - [Kubernetes agents](#kubernetes-agents)
  - [Docker agents](#docker-agents)
- [Security и Compliance](#security-и-compliance)
  - [Credentials management](#credentials-management)
  - [RBAC (Role-Based Access Control)](#rbac-role-based-access-control)
- [Monitoring и Metrics](#monitoring-и-metrics)
  - [Prometheus integration](#prometheus-integration)
  - [Health checks и alerts](#health-checks-и-alerts)
  - [Custom metrics](#custom-metrics)
- [Performance Optimization](#performance-optimization)
  - [Pipeline optimization](#pipeline-optimization)
  - [Scaling Jenkins](#scaling-jenkins)
- [Backup и Disaster Recovery](#backup-и-disaster-recovery)
  - [Backup strategy](#backup-strategy)
  - [Recovery procedure](#recovery-procedure)
- [Решение проблем](#решение-проблем)
  - [Common issues](#common-issues)
  - [Performance issues](#performance-issues)
  - [Debug pipeline execution](#debug-pipeline-execution)
- [См. также](#см-также)

## Основы Jenkins

### Установка и настройка
Ниже — установка **Jenkins** на **Ubuntu**/**Debian** и запуск в **Docker** (bash).
```bash
# Установка на Ubuntu/Debian
wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb https://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt-get update
sudo apt-get install jenkins

# Запуск сервиса
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Docker установка
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkinsci/blueocean:latest

# Получение начального пароля
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Конфигурация безопасности
```xml
<!-- config.xml - базовая конфигурация -->
<?xml version='1.1' encoding='UTF-8'?>
<hudson>
  <disabledAdministrativeMonitors/>
  <version>2.361.4</version>
  <numExecutors>2</numExecutors>
  <mode>NORMAL</mode>
  <useSecurity>true</useSecurity>
  <authorizationStrategy class="hudson.security.FullControlOnceLoggedInAuthorizationStrategy">
    <denyAnonymousReadAccess>true</denyAnonymousReadAccess>
  </authorizationStrategy>
  <securityRealm class="hudson.security.LDAPSecurityRealm" plugin="ldap@2.12">
    <server>ldap://ldap.example.com:389</server>
    <rootDN>dc=example,dc=com</rootDN>
    <userSearchBase>ou=users</userSearchBase>
    <userSearch>uid={0}</userSearch>
    <groupSearchBase>ou=groups</groupSearchBase>
    <groupSearchFilter>(&amp;(objectClass=groupOfNames)(member={0}))</groupSearchFilter>
    <managerDN>cn=admin,dc=example,dc=com</managerDN>
    <managerPassword>{AQAAABAAAAAw...}</managerPassword>
  </securityRealm>
  <slaveAgentPort>50000</slaveAgentPort>
  <label></label>
  <nodeProperties/>
  <globalNodeProperties/>
</hudson>
```

### Управление плагинами
```groovy
// plugins.txt - список плагинов для Jenkins Configuration as Code
configuration-as-code:1647.v90374b_4091a_2
job-dsl:1.81
pipeline:602.v6a_4b_fdb_31b_4e
pipeline-githubnotify-step:49.vf0a_4c3429e
pipeline-stage-view:2.33
git:5.0.0
github:1.37.1
docker-workflow:563.vd5d2e5c4007f
kubernetes:3900.v29e6c8e0c52d
aws-credentials:191.vcb_f183ce58b_9
credentials-binding:523.vd859a_4b_122e6
matrix-auth:3.1.5
role-strategy:587.588.v850a_20a_251a_2
audit-trail:361.v82cde86c784e
email-ext:2.96
slack:631.v40deea_40323b
prometheus:2.0.11
build-monitor-plugin:1.14-740.v6734_315_9b_8a_3
dashboard-view:2.487.vcf0ff9008a_c0
```

## Pipeline as Code

### Declarative Pipeline
```groovy
// Jenkinsfile - declarative pipeline
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-agent
spec:
  containers:
  - name: maven
    image: maven:3.8.6-openjdk-11
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /root/.m2
      name: maven-cache
  - name: docker
    image: docker:20.10.21
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-socket
  volumes:
  - name: maven-cache
    persistentVolumeClaim:
      claimName: maven-cache-pvc
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
  serviceAccountName: jenkins-agent
'''
        }
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    environment {
        DOCKER_REGISTRY = 'registry.example.com'
        KUBECONFIG = credentials('kubeconfig')
        SONARQUBE_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                    env.GIT_BRANCH = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Build') {
            steps {
                container('maven') {
                    sh 'mvn clean compile -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                container('maven') {
                    sh 'mvn test'
                    junit 'target/surefire-reports/*.xml'
                }
            }
            post {
                always {
                    publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')]
                }
            }
        }

        stage('Quality Gate') {
            steps {
                container('maven') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar -Dsonar.login=$SONARQUBE_TOKEN'
                    }
                }
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                container('docker') {
                    script {
                        def imageTag = "${DOCKER_REGISTRY}/myapp:${env.BUILD_NUMBER}"
                        sh "docker build -t ${imageTag} ."
                        sh "docker push ${imageTag}"
                        env.DOCKER_IMAGE = imageTag
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'main'
                }
            }
            steps {
                container('kubectl') {
                    sh '''
                        sed -i "s|IMAGE_PLACEHOLDER|${DOCKER_IMAGE}|g" k8s/staging/deployment.yaml
                        kubectl apply -f k8s/staging/
                        kubectl rollout status deployment/myapp-staging
                    '''
                }
            }
        }

        stage('Integration Tests') {
            steps {
                container('maven') {
                    sh 'mvn verify -Pintegration-tests'
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
                tag pattern: "v\\d+.*", comparator: "REGEX"
            }
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    input message: 'Deploy to Production?', ok: 'Deploy'
                }
                container('kubectl') {
                    sh '''
                        sed -i "s|IMAGE_PLACEHOLDER|${DOCKER_IMAGE}|g" k8s/production/deployment.yaml
                        kubectl apply -f k8s/production/
                        kubectl rollout status deployment/myapp-production
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            publishHTML target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ]
        }
        success {
            slackSend channel: '#builds',
                      color: 'good',
                      message: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} succeeded"
        }
        failure {
            slackSend channel: '#builds',
                      color: 'danger',
                      message: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} failed"
        }
    }
}
```

### Scripted Pipeline
```groovy
// Jenkinsfile - scripted pipeline
node('docker') {
    try {
        stage('Checkout') {
            checkout scm
            env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        }

        stage('Parallel Build & Test') {
            parallel(
                'Unit Tests': {
                    sh 'mvn test -Dtest=/*Test.java'
                },
                'Integration Tests': {
                    sh 'mvn verify -Pintegration-tests'
                },
                'Static Analysis': {
                    sh 'mvn checkstyle:check pmd:pmd spotbugs:check'
                }
            )
        }

        stage('Build Artifacts') {
            if (env.BRANCH_NAME == 'main') {
                sh 'mvn deploy'
            } else {
                sh 'mvn package'
            }
        }

        stage('Deploy') {
            if (env.BRANCH_NAME == 'main') {
                deployToProduction()
            } else if (env.BRANCH_NAME.startsWith('feature/')) {
                deployToStaging()
            }
        }

        stage('Post Deploy Tests') {
            if (env.BRANCH_NAME == 'main') {
                smokeTests()
                performanceTests()
            }
        }

        currentBuild.result = 'SUCCESS'

    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        throw e
    } finally {
        notifyBuild(currentBuild.result)
        cleanup()
    }
}

def deployToProduction() {
    echo 'Deploying to production...'
    sh 'ansible-playbook -i inventory/production deploy.yml'
}

def deployToStaging() {
    echo 'Deploying to staging...'
    sh 'ansible-playbook -i inventory/staging deploy.yml'
}

def smokeTests() {
    echo 'Running smoke tests...'
    sh 'curl -f http://production.example.com/health'
}

def performanceTests() {
    echo 'Running performance tests...'
    sh 'jmeter -n -t performance-tests.jmx -l results.jtl'
}

def notifyBuild(result) {
    def color = result == 'SUCCESS' ? 'good' : 'danger'
    slackSend channel: '#builds',
              color: color,
              message: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} ${result}"
}

def cleanup() {
    sh 'docker system prune -f'
    sh 'mvn clean'
}
```

## Job DSL

### Создание джобов программно
```groovy
// seed-job.groovy - Job DSL для создания pipeline джобов
def gitUrl = 'https://github.com/example/myapp.git'
def credentialsId = 'github-credentials'

folder('Microservices') {
    description('Microservices pipelines')
}

def microservices = ['user-service', 'order-service', 'payment-service', 'notification-service']

microservices.each { service ->
    pipelineJob("Microservices/${service}") {
        description("Pipeline for ${service}")
        keepDependencies(false)

        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(gitUrl)
                            credentials(credentialsId)
                        }
                        branch('*/main')
                        extensions {
                            cleanBeforeCheckout()
                            relativeTargetDirectory(service)
                        }
                    }
                }
                scriptPath("${service}/Jenkinsfile")
            }
        }

        triggers {
            scm('H/5 * * * *')
        }

        parameters {
            stringParam('ENVIRONMENT', 'staging', 'Target environment')
            booleanParam('RUN_TESTS', true, 'Run test suite')
            choiceParam('LOG_LEVEL', ['INFO', 'DEBUG', 'TRACE'], 'Logging level')
        }

        properties {
            buildDiscarder {
                strategy {
                    logRotator {
                        numToKeepStr('10')
                        daysToKeepStr('30')
                    }
                }
            }
        }

        wrappers {
            colorizeOutput()
            timestamps()
        }
    }
}

// Multibranch pipeline
multibranchPipelineJob('MyApp-Multibranch') {
    branchSources {
        git {
            remote(gitUrl)
            credentialsId(credentialsId)
            includes('feature/* develop main release/*')
        }
    }

    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
        }
    }

    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
```

### Shared libraries
```groovy
// vars/deploy.groovy - shared library function
def call(Map config = [:]) {
    def environment = config.environment ?: 'staging'
    def service = config.service ?: env.JOB_NAME.split('/').last()
    def version = config.version ?: env.BUILD_NUMBER

    echo "Deploying ${service} version ${version} to ${environment}"

    // Validate environment
    if (!(environment in ['staging', 'production'])) {
        error("Invalid environment: ${environment}")
    }

    // Deploy using Ansible
    ansiblePlaybook(
        playbook: 'deploy.yml',
        inventory: "inventory/${environment}",
        extraVars: [
            service: service,
            version: version,
            environment: environment
        ],
        credentialsId: 'ansible-vault-password'
    )

    // Wait for deployment
    timeout(time: 10, unit: 'MINUTES') {
        waitUntil {
            def response = httpRequest(
                url: "http://${service}.${environment}.example.com/health",
                validResponseCodes: '200'
            )
            return response.status == 200
        }
    }

    // Run smoke tests
    sh "curl -f http://${service}.${environment}.example.com/health"

    echo "Deployment completed successfully"
}

// vars/notify.groovy
def slack(Map config = [:]) {
    def message = config.message ?: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} completed"
    def color = config.success ? 'good' : 'danger'
    def channel = config.channel ?: '#builds'

    slackSend(
        channel: channel,
        color: color,
        message: message,
        attachments: [
            [
                title: "Build Details",
                title_link: env.BUILD_URL,
                fields: [
                    [
                        title: "Job",
                        value: env.JOB_NAME,
                        short: true
                    ],
                    [
                        title: "Build",
                        value: env.BUILD_NUMBER,
                        short: true
                    ],
                    [
                        title: "Branch",
                        value: env.GIT_BRANCH ?: 'unknown',
                        short: true
                    ]
                ]
            ]
        ]
    )
}
```

## Distributed Builds

### Jenkins agents (slaves)
```xml
<!-- node-config.xml - конфигурация Jenkins agent -->
<slave>
  <name>docker-agent-01</name>
  <description>Docker-based Jenkins agent</description>
  <remoteFS>/home/jenkins</remoteFS>
  <numExecutors>4</numExecutors>
  <mode>NORMAL</mode>
  <retentionStrategy class="hudson.slaves.RetentionStrategy$Always"/>
  <launcher class="hudson.slaves.JNLPLauncher"/>
  <label>docker linux</label>
  <nodeProperties>
    <hudson.slaves.EnvironmentVariablesNodeProperty>
      <envVars>
        <hudson.slaves.EnvironmentVariablesNodeProperty_-Entry>
          <key>DOCKER_HOST</key>
          <value>tcp://localhost:2376</value>
        </hudson.slaves.EnvironmentVariablesNodeProperty_-Entry>
        <hudson.slaves.EnvironmentVariablesNodeProperty_-Entry>
          <key>JAVA_HOME</key>
          <value>/usr/lib/jvm/java-11-openjdk-amd64</value>
        </hudson.slaves.EnvironmentVariablesNodeProperty_-Entry>
      </envVars>
    </hudson.slaves.EnvironmentVariablesNodeProperty>
  </nodeProperties>
</slave>
```

### Kubernetes agents
```yaml
# jenkins-agent-pod.yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-agent
spec:
  serviceAccountName: jenkins-agent
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:4.11-1-jdk11
    args: ['$(JENKINS_SECRET)', '$(JENKINS_NAME)']
    env:
    - name: JENKINS_URL
      value: 'http://jenkins.jenkins.svc.cluster.local:8080'
    volumeMounts:
    - name: workspace
      mountPath: /home/jenkins/workspace
    - name: docker-socket
      mountPath: /var/run/docker.sock
  - name: docker
    image: docker:20.10.21-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run/docker.sock
  - name: kubectl
    image: bitnami/kubectl:1.24
    command:
    - cat
    tty: true
  volumes:
  - name: workspace
    emptyDir: {}
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
```

### Docker agents
```groovy
// Docker agent configuration
pipeline {
    agent {
        docker {
            image 'maven:3.8.6-openjdk-11'
            args '-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock'
            reuseNode true
        }
    }
    // ... pipeline stages
}

// Docker Compose для complex environments
pipeline {
    agent none
    stages {
        stage('Setup Environment') {
            steps {
                sh 'docker-compose up -d db redis'
                sh 'docker-compose ps'
            }
        }
        stage('Test') {
            agent {
                dockerfile {
                    filename 'Dockerfile.test'
                    dir '.'
                    args '--network host'
                }
            }
            steps {
                sh 'mvn test'
            }
        }
        stage('Teardown') {
            steps {
                sh 'docker-compose down -v'
            }
        }
    }
}
```

## Security и Compliance

### Credentials management
```groovy
// credentials.groovy - управление credentials через код
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.Domain

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// AWS credentials
def awsCredentials = new AWSCredentialsImpl(
    CredentialsScope.GLOBAL,
    "aws-credentials",
    "AWS Production Account",
    "AKIA...",
    "secret-key"
)
store.addCredentials(domain, awsCredentials)

// Docker registry credentials
def dockerCredentials = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    "docker-registry",
    "Docker Registry",
    "username",
    "password"
)
store.addCredentials(domain, dockerCredentials)

// SSH private key
def sshKey = new BasicSSHUserPrivateKey(
    CredentialsScope.GLOBAL,
    "ssh-key",
    "jenkins",
    new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource("-----BEGIN PRIVATE KEY-----\n..."),
    "",
    "Jenkins SSH Key"
)
store.addCredentials(domain, sshKey)

// Vault credentials
def vaultCredentials = new CertificateCredentialsImpl(
    CredentialsScope.GLOBAL,
    "vault-cert",
    "Vault Client Certificate",
    "password",
    new CertificateCredentialsImpl.FileOnMasterKeyStoreSource("/path/to/keystore", "password")
)
store.addCredentials(domain, vaultCredentials)
```

### RBAC (Role-`Based Access` Control)
```groovy
// role-strategy.groovy - настройка ролей и разрешений
import hudson.security.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import jenkins.model.*

def authStrategy = new RoleBasedAuthorizationStrategy()

// Global roles
authStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, new Role('admin', '.*', [
    Permission.fromId('hudson.model.Hudson.Administer'),
    Permission.fromId('hudson.model.Hudson.Read'),
    Permission.fromId('hudson.model.Item.Read'),
    Permission.fromId('hudson.model.Item.Create'),
    Permission.fromId('hudson.model.Item.Delete'),
    Permission.fromId('hudson.model.Item.Configure'),
    Permission.fromId('hudson.model.Run.Update'),
    Permission.fromId('hudson.model.Item.Build'),
    Permission.fromId('hudson.model.Item.Cancel'),
    Permission.fromId('hudson.model.Item.Discover'),
    Permission.fromId('hudson.model.View.Read'),
    Permission.fromId('hudson.model.View.Create'),
    Permission.fromId('hudson.model.View.Delete'),
    Permission.fromId('hudson.model.View.Configure'),
    Permission.fromId('com.cloudbees.plugins.credentials.CredentialsProvider.View'),
    Permission.fromId('com.cloudbees.plugins.credentials.CredentialsProvider.Create'),
    Permission.fromId('com.cloudbees.plugins.credentials.CredentialsProvider.Update'),
    Permission.fromId('com.cloudbees.plugins.credentials.CredentialsProvider.Delete')
]))

authStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, new Role('developer', '.*', [
    Permission.fromId('hudson.model.Hudson.Read'),
    Permission.fromId('hudson.model.Item.Read'),
    Permission.fromId('hudson.model.Item.Create'),
    Permission.fromId('hudson.model.Item.Build'),
    Permission.fromId('hudson.model.Item.Cancel'),
    Permission.fromId('hudson.model.Item.Discover'),
    Permission.fromId('hudson.model.View.Read'),
    Permission.fromId('com.cloudbees.plugins.credentials.CredentialsProvider.View')
]))

authStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, new Role('viewer', '.*', [
    Permission.fromId('hudson.model.Hudson.Read'),
    Permission.fromId('hudson.model.Item.Read'),
    Permission.fromId('hudson.model.Item.Discover'),
    Permission.fromId('hudson.model.View.Read')
]))

// Project roles
authStrategy.addRole(RoleBasedAuthorizationStrategy.PROJECT, new Role('project-admin', 'MyProject.*', [
    Permission.fromId('hudson.model.Item.Read'),
    Permission.fromId('hudson.model.Item.Configure'),
    Permission.fromId('hudson.model.Item.Create'),
    Permission.fromId('hudson.model.Item.Delete'),
    Permission.fromId('hudson.model.Item.Build'),
    Permission.fromId('hudson.model.Item.Cancel'),
    Permission.fromId('hudson.model.Run.Update')
]))

authStrategy.addRole(RoleBasedAuthorizationStrategy.PROJECT, new Role('project-developer', 'MyProject.*', [
    Permission.fromId('hudson.model.Item.Read'),
    Permission.fromId('hudson.model.Item.Build'),
    Permission.fromId('hudson.model.Item.Cancel'),
    Permission.fromId('hudson.model.Run.Update')
]))

// Assign roles to users
authStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, authStrategy.getRole('admin'), 'admin-user')
authStrategy.assignRole(RoleBasedAuthorizationStrategy.PROJECT, authStrategy.getRole('project-admin'), 'project-admin-user')

Jenkins.instance.authorizationStrategy = authStrategy
Jenkins.instance.save()
```

## Monitoring и Metrics

### Prometheus integration
```xml
<!-- prometheus-config.xml -->
<prometheus>
  <enabled>true</enabled>
  <path>/prometheus</path>
  <defaultNamespace>jenkins</defaultNamespace>
  <collectingMetricsPeriodInSeconds>120</collectingMetricsPeriodInSeconds>
  <labeledMetrics>
    <jobAttributeName>job</jobAttributeName>
    <itemsAttributeName>items</itemsAttributeName>
  </labeledMetrics>
  <countSuccessfulBuilds>true</countSuccessfulBuilds>
  <countUnstableBuilds>true</countUnstableBuilds>
  <countFailedBuilds>true</countFailedBuilds>
  <countNotBuilt>true</countNotBuilt>
  <fetchTestResults>true</fetchTestResults>
</prometheus>
```

### Health checks и alerts
```groovy
// health-check.groovy - health check для Jenkins
import hudson.model.*
import jenkins.model.*

def jenkins = Jenkins.instance

// Check disk space
def rootDir = jenkins.rootDir
def usableSpace = rootDir.usableSpace
def totalSpace = rootDir.totalSpace
def freePercent = (usableSpace * 100) / totalSpace

if (freePercent < 10) {
    println "WARNING: Disk space low: ${freePercent.round(2)}% free"
}

// Check queue length
def queue = jenkins.queue
def queueLength = queue.items.length
if (queueLength > 10) {
    println "WARNING: Build queue length: $queueLength"
}

// Check node availability
def nodes = jenkins.nodes
nodes.each { node ->
    if (node.computer.isOffline()) {
        println "WARNING: Node ${node.displayName} is offline"
    }
    if (node.computer.isTemporarilyOffline()) {
        println "INFO: Node ${node.displayName} is temporarily offline"
    }
}

// Check plugin updates
def updateCenter = jenkins.updateCenter
def updates = updateCenter.getUpdates()
if (updates.size() > 0) {
    println "INFO: ${updates.size()} plugin updates available"
}

// Memory usage
def runtime = Runtime.runtime
def usedMemory = runtime.totalMemory() - runtime.freeMemory()
def maxMemory = runtime.maxMemory()
def memoryPercent = (usedMemory * 100) / maxMemory

if (memoryPercent > 80) {
    println "WARNING: Memory usage high: ${memoryPercent.round(2)}%"
}
```

### Custom metrics
```groovy
// custom-metrics.groovy
import io.prometheus.client.*
import io.prometheus.client.hotspot.*

// Create custom metrics
def buildCounter = Counter.build()
    .name("jenkins_builds_total")
    .help("Total number of builds")
    .labelNames("job", "result")
    .register()

def buildDuration = Histogram.build()
    .name("jenkins_build_duration_seconds")
    .help("Build duration in seconds")
    .labelNames("job")
    .buckets(60, 300, 600, 1800, 3600, 7200) // 1m, 5m, 10m, 30m, 1h, 2h
    .register()

def activeBuilds = Gauge.build()
    .name("jenkins_active_builds")
    .help("Number of currently active builds")
    .register()

// Collect metrics periodically
Timer timer = new Timer()
timer.scheduleAtFixedRate(new TimerTask() {
    @Override
    void run() {
        try {
            def jenkins = Jenkins.instance
            def activeBuildCount = 0

            // Collect build metrics
            jenkins.allItems.each { item ->
                if (item instanceof Job) {
                    def job = item as Job
                    def lastBuild = job.lastBuild

                    if (lastBuild != null) {
                        def result = lastBuild.result?.toString() ?: "UNKNOWN"
                        buildCounter.labels(job.name, result).inc()

                        if (lastBuild.duration > 0) {
                            buildDuration.labels(job.name)
                                .observe(lastBuild.duration / 1000.0)
                        }
                    }

                    // Count active builds
                    if (job.isBuilding()) {
                        activeBuildCount++
                    }
                }
            }

            activeBuilds.set(activeBuildCount)

        } catch (Exception e) {
            println "Error collecting metrics: ${e.message}"
        }
    }
}, 0, 60000) // Every minute
```

## Performance Optimization

### Pipeline optimization
```groovy
// optimized-pipeline.groovy
pipeline {
    agent none

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '50', daysToKeepStr: '30'))
        timestamps()
        ansiColor('xterm')
    }

    environment {
        MAVEN_OPTS = '-Xmx3072m -Xms512m'
        GRADLE_OPTS = '-Xmx2048m -Dorg.gradle.daemon=false'
        DOCKER_BUILDKIT = '1'
    }

    stages {
        stage('Parallel Analysis') {
            parallel {
                stage('Static Analysis') {
                    agent { label 'static-analysis' }
                    steps {
                        sh 'mvn compile spotbugs:check pmd:pmd checkstyle:check'
                        recordIssues tools: [
                            spotBugs(pattern: 'target/spotbugsXml.xml'),
                            pmdParser(pattern: 'target/pmd.xml'),
                            checkStyle(pattern: 'target/checkstyle-result.xml')
                        ]
                    }
                }

                stage('Security Scan') {
                    agent { label 'security' }
                    steps {
                        sh 'mvn dependency-check:check'
                        dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
                    }
                }

                stage('License Check') {
                    agent { label 'license' }
                    steps {
                        sh 'mvn license:check'
                    }
                }
            }
        }

        stage('Build & Test') {
            parallel {
                stage('Unit Tests') {
                    agent { label 'maven' }
                    steps {
                        sh 'mvn clean test'
                        junit 'target/surefire-reports/*.xml'
                    }
                    post {
                        always {
                            jacoco execPattern: 'target/jacoco.exec'
                        }
                    }
                }

                stage('Integration Tests') {
                    agent { label 'integration' }
                    steps {
                        sh 'mvn clean verify -Pintegration-tests'
                        junit 'target/failsafe-reports/*.xml'
                    }
                }
            }
        }

        stage('Quality Gate') {
            agent { label 'quality' }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Artifacts') {
            agent { label 'build' }
            steps {
                sh 'mvn package -DskipTests'
                stash includes: 'target/*.jar', name: 'artifacts'
            }
        }

        stage('Docker Build') {
            agent { label 'docker' }
            steps {
                unstash 'artifacts'
                script {
                    def imageTag = "myapp:${env.BUILD_NUMBER}"
                    sh "docker build --target production -t ${imageTag} ."
                    sh "docker push ${imageTag}"
                    env.DOCKER_IMAGE = imageTag
                }
            }
        }

        stage('Deploy') {
            agent { label 'deploy' }
            steps {
                sh "helm upgrade --install myapp ./helm --set image.tag=${env.BUILD_NUMBER}"
                sh 'kubectl rollout status deployment/myapp'
            }
        }
    }

    post {
        always {
            node('master') {
                sh 'docker system prune -f --volumes'
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                publishHTML target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'Coverage Report'
                ]
            }
        }
        changed {
            script {
                if (currentBuild.currentResult == 'SUCCESS') {
                    slackSend channel: '#builds',
                              color: 'good',
                              message: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} Back to normal"
                }
            }
        }
    }
}
```

### Scaling Jenkins
```xml
<!-- master-config.xml - конфигурация Jenkins master -->
<hudson>
  <numExecutors>0</numExecutors> <!-- No local executors -->
  <mode>NORMAL</mode>
  <useSecurity>true</useSecurity>
  <authorizationStrategy class="hudson.security.FullControlOnceLoggedInAuthorizationStrategy"/>
  <securityRealm class="hudson.security.LDAPSecurityRealm"/>
  <slaveAgentPort>50000</slaveAgentPort>

  <!-- Performance settings -->
  <quietPeriod>5</quietPeriod>
  <scmCheckoutRetryCount>3</scmCheckoutRetryCount>

  <!-- Disable unused features -->
  <disabledAdministrativeMonitors>
    <string>hudson.diagnosis.ReverseProxySetupMonitor</string>
    <string>hudson.security.LDAPBindDN</string>
  </disabledAdministrativeMonitors>

  <!-- Global properties -->
  <globalNodeProperties>
    <hudson.slaves.EnvironmentVariablesNodeProperty>
      <envVars>
        <string>JENKINS_HOME=/var/lib/jenkins</string>
        <string>MAVEN_OPTS=-Xmx2g -XX:MaxPermSize=512m</string>
        <string>GRADLE_OPTS=-Xmx2g</string>
      </envVars>
    </hudson.slaves.EnvironmentVariablesNodeProperty>
  </globalNodeProperties>
</hudson>
```

## Backup и Disaster Recovery

### Backup strategy
```bash
#!/bin/bash
# jenkins-backup.sh

BACKUP_DIR="/opt/jenkins-backups"
JENKINS_HOME="/var/lib/jenkins"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "Starting Jenkins backup: $TIMESTAMP"

# Create backup directory
mkdir -p "$BACKUP_DIR/$TIMESTAMP"

# Stop Jenkins to ensure consistency
sudo systemctl stop jenkins

# Backup JENKINS_HOME
sudo tar -czf "$BACKUP_DIR/$TIMESTAMP/jenkins_home.tar.gz" -C /var/lib jenkins

# Backup configuration files
sudo cp -r /etc/sysconfig/jenkins "$BACKUP_DIR/$TIMESTAMP/"
sudo cp -r /etc/systemd/system/jenkins.service.d "$BACKUP_DIR/$TIMESTAMP/"

# Start Jenkins
sudo systemctl start jenkins

# Create backup manifest
cat > "$BACKUP_DIR/$TIMESTAMP/manifest.txt" << EOF
Jenkins Backup Manifest
=======================
Date: $(date)
Jenkins Version: $(curl -s http://localhost:8500/api/json | jq -r .nodeDescription)
Backup Type: Full
Included:
- JENKINS_HOME directory
- System configuration
- Service configuration
Excluded:
- Build logs older than 30 days
- Workspace files
- Cache directories
EOF

# Compress backup
cd "$BACKUP_DIR"
tar -czf "jenkins_backup_$TIMESTAMP.tar.gz" "$TIMESTAMP"
rm -rf "$TIMESTAMP"

# Upload to S3
aws s3 cp "jenkins_backup_$TIMESTAMP.tar.gz" "s3://jenkins-backups/"

# Cleanup old backups (keep last 10)
cd "$BACKUP_DIR"
ls -t *.tar.gz | tail -n +11 | xargs rm -f

echo "Jenkins backup completed successfully"
```

### Recovery procedure
```bash
#!/bin/bash
# jenkins-restore.sh

BACKUP_FILE="$1"
JENKINS_HOME="/var/lib/jenkins"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup-file>"
    exit 1
fi

echo "Starting Jenkins restore from: $BACKUP_FILE"

# Stop Jenkins
sudo systemctl stop jenkins

# Backup current state
sudo mv "$JENKINS_HOME" "${JENKINS_HOME}.backup.$(date +%s)"

# Extract backup
sudo mkdir -p "$JENKINS_HOME"
sudo tar -xzf "$BACKUP_FILE" -C "$JENKINS_HOME" --strip-components=1

# Restore permissions
sudo chown -R jenkins:jenkins "$JENKINS_HOME"
sudo chmod 755 "$JENKINS_HOME"

# Restore configuration files
sudo cp -r "$BACKUP_DIR/config/jenkins" /etc/sysconfig/
sudo cp -r "$BACKUP_DIR/config/jenkins.service.d" /etc/systemd/system/

# Reload systemd
sudo systemctl daemon-reload

# Start Jenkins
sudo systemctl start jenkins

# Wait for Jenkins to start
echo "Waiting for Jenkins to start..."
timeout 300 bash -c 'until curl -f http://localhost:8080/login >/dev/null 2>&1; do sleep 5; done'

echo "Jenkins restore completed. Please check the web interface."
```

## Решение проблем

### Common issues
```bash
# Check Jenkins status
sudo systemctl status jenkins
sudo journalctl -u jenkins -f

# Check Java process
ps aux | grep java
jps -l

# Check disk space
df -h
du -sh /var/lib/jenkins/*

# Check memory usage
free -h
top -p $(pgrep -f jenkins)

# Check network connectivity
curl -I http://localhost:8080
netstat -tlnp | grep :8080

# Check plugin issues
curl http://localhost:8080/pluginManager/api/json | jq '.plugins[] | select(.enabled==false)'

# Check build queue
curl http://localhost:8080/queue/api/json | jq '.items | length'

# Check node status
curl http://localhost:8080/computer/api/json | jq '.computer[] | {displayName, offline}'
```

### Performance issues
```bash
# Analyze slow builds
# Check build duration trends
curl "http://localhost:8080/job/MyJob/api/json?tree=builds[number,duration,timestamp]" | jq '.builds[] | {number, duration: (.duration/1000), timestamp: (.timestamp/1000 | strftime("%Y-%m-%d %H:%M:%S"))}'

# Check resource usage
# JVM memory
jstat -gc $(pgrep -f jenkins)

# Thread count
ps -o pid,ppid,cmd,nlwp $(pgrep -f jenkins)

# Network connections
ss -tlnp | grep java

# Disk I/O
iotop -p $(pgrep -f jenkins)

# System load
uptime
vmstat 1 5
```

### Debug pipeline execution
```groovy
// debug-pipeline.groovy
pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('Debug Info') {
            steps {
                sh '''
                    echo "=== System Info ==="
                    uname -a
                    cat /etc/os-release
                    echo "=== Java Info ==="
                    java -version
                    echo "=== Jenkins Info ==="
                    echo "Jenkins Version: $JENKINS_VERSION"
                    echo "Job Name: $JOB_NAME"
                    echo "Build Number: $BUILD_NUMBER"
                    echo "Workspace: $WORKSPACE"
                    echo "=== Environment ==="
                    env | sort
                    echo "=== Disk Usage ==="
                    df -h
                    echo "=== Memory Usage ==="
                    free -h
                '''
            }
        }

        stage('Debug Build') {
            steps {
                script {
                    // Print all environment variables
                    env.getEnvironment().each { key, value ->
                        println "${key} = ${value}"
                    }

                    // Print build parameters
                    params.each { key, value ->
                        println "Parameter: ${key} = ${value}"
                    }

                    // Print current directory contents
                    sh 'ls -la'
                    sh 'pwd'
                }
            }
        }

        stage('Conditional Debug') {
            when {
                anyOf {
                    branch 'debug'
                    expression { params.DEBUG == true }
                }
            }
            steps {
                sh 'mvn test -DforkCount=1 -DreuseForks=false -Dmaven.surefire.debug'
            }
        }
    }

    post {
        always {
            sh '''
                echo "=== Build Artifacts ==="
                find . -name "*.jar" -o -name "*.war" -o -name "*.log" | head -20
                echo "=== Test Results ==="
                find . -name "*Test*.xml" | head -10
            '''
        }
        failure {
            sh '''
                echo "=== Failure Debug Info ==="
                ps aux | grep java
                netstat -tlnp
                tail -100 /var/log/jenkins/jenkins.log
            '''
        }
    }
}
```
## См. также
- [GitLab CI](gitlab-ci.md) — альтернативная **CI/CD** платформа
- [GitHub Actions](github-actions.md) — **CI/CD** в **GitHub**
- [Docker](../containers/docker/docker-basics.md) — контейнеризация
- [Kubernetes](../containers/kubernetes/kubernetes-advanced.md) — оркестрация контейнеров
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
