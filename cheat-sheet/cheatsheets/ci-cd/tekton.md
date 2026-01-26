# Tekton

Tekton - это мощный, Kubernetes-native фреймворк для создания CI/CD pipelines. Разработанный Google и IBM, Tekton предоставляет декларативный подход к определению и запуску pipelines, интегрируясь глубоко с Kubernetes экосистемой. Этот документ охватывает архитектуру, конфигурацию, продвинутые паттерны и best practices для использования Tekton в production средах.

## Содержание
- [Основы Tekton](#основы-tekton)
- [Установка и настройка](#установка-и-настройка)
- [Pipeline Resources](#pipeline-resources)
- [Tasks и Pipelines](#tasks-и-pipelines)
- [Triggers и Event-driven Pipelines](#triggers-и-event-driven-pipelines)
- [Advanced Patterns](#advanced-patterns)
- [Monitoring и Observability](#monitoring-и-observability)
- [Security и Best Practices](#security-и-best-practices)

## Основы Tekton

### Архитектура Tekton
```
Tekton Architecture:
├── PipelineRuns    # Исполнение pipelines
├── TaskRuns       # Исполнение tasks
├── Pipelines      # Определение workflow
├── Tasks          # Определение steps
├── PipelineResources # Входные/выходные ресурсы
├── Triggers       # Event-driven execution
├── Conditions     # Условное исполнение
└── Workspaces     # Shared storage
```

### Core Concepts
```yaml
# Task - базовый строительный блок
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: build-app
spec:
  params:
    - name: context
      description: The context directory
      default: .
  workspaces:
    - name: source
      description: The workspace containing source code
  steps:
    - name: build
      image: golang:1.19
      workingDir: $(workspaces.source.path)/$(params.context)
      script: |
        go mod download
        go build -o app .
      volumeMounts:
        - name: go-cache
          mountPath: /go/pkg

# Pipeline - композиция tasks
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: ci-pipeline
spec:
  params:
    - name: git-url
    - name: git-revision
      default: main
  workspaces:
    - name: shared-workspace
  tasks:
    - name: fetch-source
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.git-url)
        - name: revision
          value: $(params.git-revision)
      workspaces:
        - name: output
          workspace: shared-workspace
    - name: build
      taskRef:
        name: build-app
      runAfter: [fetch-source]
      workspaces:
        - name: source
          workspace: shared-workspace

# PipelineRun - исполнение pipeline
apiVersion: tekton.dev/v1beta1
kind: PipelineRun
metadata:
  name: ci-pipeline-run
spec:
  pipelineRef:
    name: ci-pipeline
  params:
    - name: git-url
      value: https://github.com/myorg/myapp
    - name: git-revision
      value: v1.0.0
  workspaces:
    - name: shared-workspace
      persistentVolumeClaim:
        claimName: tekton-pvc
```

## Установка и настройка

### Установка через Operator
```bash
# Установка Tekton Operator
kubectl apply -f https://storage.googleapis.com/tekton-releases/operator/latest/release.yaml

# Создание TektonConfig
cat <<EOF | kubectl apply -f -
apiVersion: operator.tekton.dev/v1alpha1
kind: TektonConfig
metadata:
  name: config
spec:
  profile: all  # Установить все компоненты
  targetNamespace: tekton-pipelines
  addon:
    params:
    - name: clusterTasks
      value: "true"
    - name: pipelineTemplates
      value: "true"
EOF
```

### Manual установка
```bash
# Установка Tekton Pipelines
kubectl apply -f https://storage.googleapis.com/tekton-releases/pipeline/latest/release.yaml

# Установка Tekton Triggers
kubectl apply -f https://storage.googleapis.com/tekton-releases/triggers/latest/release.yaml

# Установка Tekton Dashboard
kubectl apply -f https://storage.googleapis.com/tekton-releases/dashboard/latest/release.yaml

# Установка Tekton CLI (tkn)
curl -LO https://github.com/tektoncd/cli/releases/download/v0.28.0/tkn_0.28.0_Linux_x86_64.tar.gz
tar -xzf tkn_0.28.0_Linux_x86_64.tar.gz
sudo mv tkn /usr/local/bin/
```

### Настройка Persistent Volumes
```yaml
# PersistentVolumeClaim для workspaces
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: tekton-workspace-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: standard

# Или использование emptyDir для ephemeral storage
# workspaces:
#   - name: shared-workspace
#     emptyDir: {}
```

### RBAC настройка
```yaml
# ServiceAccount для pipeline execution
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tekton-service-account
  namespace: default
secrets:
  - name: docker-registry-secret

---
# ClusterRole для pipeline задач
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: tekton-pipeline-runner
rules:
- apiGroups: [""]
  resources: ["pods", "services", "endpoints", "persistentvolumeclaims"]
  verbs: ["*"]
- apiGroups: ["tekton.dev"]
  resources: ["*"]
  verbs: ["*"]
- apiGroups: ["apps"]
  resources: ["deployments"]
  verbs: ["get", "list", "create", "update", "patch", "delete"]

---
# ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: tekton-pipeline-runner-binding
subjects:
- kind: ServiceAccount
  name: tekton-service-account
  namespace: default
roleRef:
  kind: ClusterRole
  name: tekton-pipeline-runner
  apiGroup: rbac.authorization.k8s.io
```

## Pipeline Resources

### Git ресурсы
```yaml
# Git PipelineResource (deprecated - использовать workspaces)
apiVersion: tekton.dev/v1alpha1
kind: PipelineResource
metadata:
  name: my-app-git
spec:
  type: git
  params:
    - name: revision
      value: main
    - name: url
      value: https://github.com/myorg/myapp

# Современный подход - git-clone task
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: git-clone
  annotations:
    tekton.dev/categories: Git
    tekton.dev/tags: git
spec:
  params:
    - name: url
      description: git url to clone
      type: string
    - name: revision
      description: git revision to checkout
      type: string
      default: main
    - name: submodules
      description: defines if the resource should initialize submodules
      type: string
      default: "true"
    - name: depth
      description: performs a shallow clone
      type: string
      default: "1"
  results:
    - name: commit
      description: the precise commit SHA that was fetched by this Task
    - name: url
      description: the precise URL that was fetched by this Task
  workspaces:
    - name: output
      description: The git repo will be cloned onto the volume backing this workspace
  steps:
    - name: clone
      image: gcr.io/tekton-releases/github.com/tektoncd/pipeline/cmd/git-init:v0.32.0
      script: |
        CHECKOUT_DIR="$(workspaces.output.path)"
        /ko-app/git-init \
          -url="$(params.url)" \
          -revision="$(params.revision)" \
          -path="$CHECKOUT_DIR" \
          -submodules="$(params.submodules)" \
          -depth="$(params.depth)"
        cd "$CHECKOUT_DIR"
        RESULT_SHA="$(git rev-parse HEAD)"
        printf "%s" "$RESULT_SHA" > "$(results.commit.path)"
        printf "%s" "$(params.url)" > "$(results.url.path)"
```

### Container Image ресурсы
```yaml
# Image PipelineResource (deprecated)
apiVersion: tekton.dev/v1alpha1
kind: PipelineResource
metadata:
  name: my-app-image
spec:
  type: image
  params:
    - name: url
      value: gcr.io/my-project/my-app:$(inputs.params.version)

# Современный подход - kaniko или buildah для сборки
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: build-docker-image
spec:
  params:
    - name: image
      description: Name (reference) of the image to build
    - name: dockerfile
      description: Path to the Dockerfile to build
      default: ./Dockerfile
    - name: context
      description: The build context path
      default: .
  workspaces:
    - name: source
      description: The workspace containing the source code
  steps:
    - name: build-and-push
      image: gcr.io/kaniko-project/executor:latest
      workingDir: $(workspaces.source.path)
      script: |
        /kaniko/executor \
          --dockerfile=$(params.dockerfile) \
          --context=$(params.context) \
          --destination=$(params.image) \
          --cache=true \
          --cache-dir=/workspace/cache
      volumeMounts:
        - name: docker-config
          mountPath: /kaniko/.docker/
          readOnly: true
      env:
        - name: DOCKER_CONFIG
          value: /kaniko/.docker/
```

### Custom ресурсы
```yaml
# Custom PipelineResource для Helm charts
apiVersion: tekton.dev/v1alpha1
kind: PipelineResource
metadata:
  name: helm-chart
spec:
  type: storage  # или custom type
  params:
    - name: type
      value: helm
    - name: location
      value: gs://my-bucket/charts/myapp
    - name: version
      value: v1.2.3

# ClusterTask для работы с custom ресурсами
apiVersion: tekton.dev/v1beta1
kind: ClusterTask
metadata:
  name: helm-package
spec:
  params:
    - name: chart-path
      type: string
    - name: version
      type: string
  steps:
    - name: package
      image: alpine/helm:3.9.0
      script: |
        cd $(params.chart-path)
        helm package . --version $(params.version)
        ls -la *.tgz
```

## Tasks и Pipelines

### Advanced Task patterns
```yaml
# Multi-step task с conditional execution
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: advanced-build
spec:
  params:
    - name: skip-tests
      type: string
      default: "false"
    - name: build-type
      type: string
      default: "full"
  workspaces:
    - name: source
  steps:
    - name: lint
      image: golangci/golangci-lint:v1.50
      workingDir: $(workspaces.source.path)
      script: |
        golangci-lint run ./...

    - name: test
      image: golang:1.19
      workingDir: $(workspaces.source.path)
      script: |
        go test ./...
      env:
        - name: CGO_ENABLED
          value: "0"

    - name: build
      image: golang:1.19
      workingDir: $(workspaces.source.path)
      script: |
        if [ "$(params.build-type)" = "full" ]; then
          go build -o app -ldflags="-X main.version=$(params.version)" .
        else
          go build -o app .
        fi

    - name: security-scan
      image: securecodebox/engine:latest
      workingDir: $(workspaces.source.path)
      script: |
        # Run security scan
        echo "Running security scan on $(workspaces.source.path)"
      when:
        - input: "$(params.skip-tests)"
          operator: in
          values: ["false"]

  sidecars:
    - image: redis:6-alpine
      name: cache
      ports:
        - containerPort: 6379
```

### Pipeline с DAG (Directed Acyclic Graph)
```yaml
# Pipeline с complex dependencies
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: complex-ci-pipeline
spec:
  params:
    - name: git-url
    - name: git-revision
      default: main
    - name: image-registry
    - name: image-tag
  workspaces:
    - name: shared-workspace
  tasks:
    # Parallel tasks
    - name: clone-frontend
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.git-url)/frontend
        - name: subdirectory
          value: frontend
      workspaces:
        - name: output
          workspace: shared-workspace

    - name: clone-backend
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.git-url)/backend
        - name: subdirectory
          value: backend
      workspaces:
        - name: output
          workspace: shared-workspace

    # Sequential dependencies
    - name: build-frontend
      taskRef:
        name: build-frontend-app
      runAfter: [clone-frontend]
      params:
        - name: context
          value: frontend
        - name: image
          value: $(params.image-registry)/frontend:$(params.image-tag)
      workspaces:
        - name: source
          workspace: shared-workspace

    - name: build-backend
      taskRef:
        name: build-backend-app
      runAfter: [clone-backend]
      params:
        - name: context
          value: backend
        - name: image
          value: $(params.image-registry)/backend:$(params.image-tag)
      workspaces:
        - name: source
          workspace: shared-workspace

    # Final deployment task
    - name: deploy
      taskRef:
        name: deploy-to-k8s
      runAfter: [build-frontend, build-backend]
      params:
        - name: frontend-image
          value: $(params.image-registry)/frontend:$(params.image-tag)
        - name: backend-image
          value: $(params.image-registry)/backend:$(params.image-tag)
      workspaces:
        - name: source
          workspace: shared-workspace
```

### Pipeline Templates и Reusability
```yaml
# Pipeline Template
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: ci-template
spec:
  params:
    - name: git-url
      type: string
    - name: image-registry
      type: string
    - name: image-tag
      type: string
      default: latest
    - name: skip-tests
      type: string
      default: "false"
  workspaces:
    - name: source
  tasks:
    - name: clone
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.git-url)
      workspaces:
        - name: output
          workspace: source

    - name: test
      taskRef:
        name: run-tests
      runAfter: [clone]
      when:
        - input: "$(params.skip-tests)"
          operator: in
          values: ["false"]
      workspaces:
        - name: source
          workspace: source

    - name: build
      taskRef:
        name: build-image
      runAfter: [test]
      params:
        - name: registry
          value: $(params.image-registry)
        - name: tag
          value: $(params.image-tag)
      workspaces:
        - name: source
          workspace: source

---
# Использование template
apiVersion: tekton.dev/v1beta1
kind: PipelineRun
metadata:
  name: my-app-ci-run
spec:
  pipelineRef:
    name: ci-template
  params:
    - name: git-url
      value: https://github.com/myorg/myapp
    - name: image-registry
      value: gcr.io/my-project
    - name: image-tag
      value: v1.2.3
  workspaces:
    - name: source
      persistentVolumeClaim:
        claimName: tekton-workspace
```

### Conditions и Gates
```yaml
# Condition для проверки качества кода
apiVersion: tekton.dev/v1beta1
kind: Condition
metadata:
  name: check-code-quality
spec:
  params:
    - name: min-coverage
      default: "80"
  check:
    image: python:3.9
    script: |
      #!/usr/bin/env python3
      import sys
      import os

      # Check code coverage
      coverage_file = os.path.join(os.getcwd(), 'coverage.xml')
      if not os.path.exists(coverage_file):
          print("Coverage file not found")
          sys.exit(1)

      # Parse coverage (simplified)
      with open(coverage_file, 'r') as f:
          content = f.read()
          if 'coverage' in content:
              print("Coverage check passed")
              sys.exit(0)
          else:
              print("Coverage check failed")
              sys.exit(1)

---
# Pipeline с conditions
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: quality-gate-pipeline
spec:
  params:
    - name: git-url
    - name: min-coverage
      default: "80"
  workspaces:
    - name: source
  tasks:
    - name: clone
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.git-url)
      workspaces:
        - name: output
          workspace: source

    - name: test
      taskRef:
        name: run-tests
      runAfter: [clone]
      workspaces:
        - name: source
          workspace: source

    - name: quality-check
      taskRef:
        name: code-quality-check
      runAfter: [test]
      conditions:
        - conditionRef: check-code-quality
          params:
            - name: min-coverage
              value: $(params.min-coverage)
      workspaces:
        - name: source
          workspace: source

    - name: deploy
      taskRef:
        name: deploy-app
      runAfter: [quality-check]
      workspaces:
        - name: source
          workspace: source
```

## Triggers и Event-driven Pipelines

### Trigger Templates и Bindings
```yaml
# TriggerTemplate
apiVersion: triggers.tekton.dev/v1beta1
kind: TriggerTemplate
metadata:
  name: ci-pipeline-template
spec:
  params:
    - name: git-revision
    - name: git-commit-message
    - name: git-repo-url
    - name: git-repo-name
  resourcetemplates:
    - apiVersion: tekton.dev/v1beta1
      kind: PipelineRun
      metadata:
        generateName: $(tt.params.git-repo-name)-run-
      spec:
        pipelineRef:
          name: ci-pipeline
        params:
          - name: repo-url
            value: $(tt.params.git-repo-url)
          - name: revision
            value: $(tt.params.git-revision)
        workspaces:
          - name: shared-workspace
            persistentVolumeClaim:
              claimName: tekton-pvc

---
# TriggerBinding для GitHub
apiVersion: triggers.tekton.dev/v1beta1
kind: TriggerBinding
metadata:
  name: github-binding
spec:
  params:
    - name: git-revision
      value: $(body.head_commit.id)
    - name: git-commit-message
      value: $(body.head_commit.message)
    - name: git-repo-url
      value: $(body.repository.url)
    - name: git-repo-name
      value: $(body.repository.name)

---
# TriggerBinding для GitLab
apiVersion: triggers.tekton.dev/v1beta1
kind: TriggerBinding
metadata:
  name: gitlab-binding
spec:
  params:
    - name: git-revision
      value: $(body.after)
    - name: git-commit-message
      value: $(body.commits[0].message)
    - name: git-repo-url
      value: $(body.project.git_http_url)
    - name: git-repo-name
      value: $(body.project.name)
```

### EventListeners
```yaml
# EventListener для GitHub webhooks
apiVersion: triggers.tekton.dev/v1beta1
kind: EventListener
metadata:
  name: github-listener
spec:
  serviceAccountName: tekton-triggers-sa
  triggers:
    - name: github-push
      interceptors:
        - github:
            secretRef:
              secretName: github-secret
              secretKey: secretToken
            eventTypes:
              - push
        - cel:
            filter: "body.ref == 'refs/heads/main'"
      bindings:
        - ref: github-binding
      template:
        ref: ci-pipeline-template

    - name: github-pull-request
      interceptors:
        - github:
            secretRef:
              secretName: github-secret
              secretKey: secretToken
            eventTypes:
              - pull_request
        - cel:
            filter: "body.action in ['opened', 'synchronize', 'reopened']"
      bindings:
        - ref: github-pr-binding
      template:
        ref: pr-pipeline-template

---
# Service для EventListener
apiVersion: v1
kind: Service
metadata:
  name: el-github-listener
spec:
  selector:
    app: github-listener
  ports:
    - port: 8080
      targetPort: 8080
  type: LoadBalancer

---
# Ingress для external access
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: github-webhook-ingress
spec:
  rules:
    - host: tekton.mycompany.com
      http:
        paths:
          - path: /github
            pathType: Prefix
            backend:
              service:
                name: el-github-listener
                port:
                  number: 8080
```

### Advanced Interceptors
```yaml
# CEL interceptor для complex filtering
apiVersion: triggers.tekton.dev/v1beta1
kind: EventListener
metadata:
  name: advanced-listener
spec:
  triggers:
    - name: filtered-trigger
      interceptors:
        - cel:
            filter: >
              body.action == 'push' &&
              !body.head_commit.message.contains('[skip ci]') &&
              body.repository.private == false &&
              size(body.commits) > 0 &&
              body.commits.all(c, c.modified.exists(m, m.endsWith('.go')))
        - webhook:
            objectRef:
              kind: Secret
              name: webhook-secret
              apiVersion: v1
      bindings:
        - ref: advanced-binding
      template:
        ref: advanced-template

---
# Custom interceptor (webhook)
apiVersion: triggers.tekton.dev/v1beta1
kind: ClusterInterceptor
metadata:
  name: custom-validation
spec:
  clientConfig:
    service:
      name: custom-interceptor
      namespace: tekton-pipelines
      port: 80

---
# Custom interceptor service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: custom-interceptor
  namespace: tekton-pipelines
spec:
  replicas: 1
  selector:
    matchLabels:
      app: custom-interceptor
  template:
    metadata:
      labels:
        app: custom-interceptor
    spec:
      containers:
        - name: interceptor
          image: my-custom-interceptor:latest
          ports:
            - containerPort: 80
```

## Advanced Patterns

### Multi-stage deployments
```yaml
# Blue-Green deployment pipeline
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: blue-green-deploy
spec:
  params:
    - name: image
    - name: environment
      default: staging
  workspaces:
    - name: config
  tasks:
    - name: deploy-blue
      taskRef:
        name: deploy-k8s
      params:
        - name: manifest
          value: $(workspaces.config.path)/blue-deployment.yaml
        - name: image
          value: $(params.image)
      workspaces:
        - name: source
          workspace: config

    - name: smoke-test-blue
      taskRef:
        name: smoke-test
      runAfter: [deploy-blue]
      params:
        - name: environment
          value: blue-$(params.environment)

    - name: traffic-switch
      taskRef:
        name: update-ingress
      runAfter: [smoke-test-blue]
      params:
        - name: target
          value: blue
        - name: environment
          value: $(params.environment)

    - name: cleanup-green
      taskRef:
        name: cleanup-deployment
      runAfter: [traffic-switch]
      params:
        - name: target
          value: green
        - name: environment
          value: $(params.environment)
      when:
        - input: "$(params.environment)"
          operator: in
          values: ["production"]
```

### GitOps pipelines
```yaml
# GitOps pipeline с ArgoCD integration
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: gitops-deploy
spec:
  params:
    - name: app-name
    - name: target-revision
    - name: manifests-repo
  workspaces:
    - name: manifests
  tasks:
    - name: clone-manifests
      taskRef:
        name: git-clone
      params:
        - name: url
          value: $(params.manifests-repo)
        - name: subdirectory
          value: manifests
      workspaces:
        - name: output
          workspace: manifests

    - name: update-image
      taskRef:
        name: kustomize-set-image
      runAfter: [clone-manifests]
      params:
        - name: image
          value: $(params.app-name):$(params.target-revision)
        - name: path
          value: manifests/$(params.app-name)
      workspaces:
        - name: source
          workspace: manifests

    - name: validate-manifests
      taskRef:
        name: kubeconform
      runAfter: [update-image]
      params:
        - name: path
          value: manifests/$(params.app-name)
      workspaces:
        - name: source
          workspace: manifests

    - name: commit-changes
      taskRef:
        name: git-commit
      runAfter: [validate-manifests]
      params:
        - name: message
          value: "Deploy $(params.app-name):$(params.target-revision)"
        - name: branch
          value: main
      workspaces:
        - name: source
          workspace: manifests

    - name: wait-sync
      taskRef:
        name: argocd-wait-sync
      runAfter: [commit-changes]
      params:
        - name: app-name
          value: $(params.app-name)
        - name: timeout
          value: "300"
```

### Pipeline composition
```yaml
# Pipeline composition pattern
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: composite-pipeline
spec:
  params:
    - name: services
      type: array
      default: ["api", "web", "worker"]
  workspaces:
    - name: source
  tasks:
    # Parallel build всех сервисов
    - name: build-services
      matrix:
        params:
          - name: service
            value: $(params.services)
      taskRef:
        name: build-service
      params:
        - name: service-name
          value: $(params.service)
      workspaces:
        - name: source
          workspace: source

    # Sequential testing
    - name: test-api
      taskRef:
        name: test-service
      runAfter: [build-services]
      params:
        - name: service-name
          value: api
        - name: test-type
          value: integration
      workspaces:
        - name: source
          workspace: source
      when:
        - input: "api"
          operator: in
          values: $(params.services)

    - name: test-web
      taskRef:
        name: test-service
      runAfter: [build-services]
      params:
        - name: service-name
          value: web
        - name: test-type
          value: e2e
      workspaces:
        - name: source
          workspace: source
      when:
        - input: "web"
          operator: in
          values: $(params.services)

    # Final deployment
    - name: deploy
      taskRef:
        name: deploy-services
      runAfter: [test-api, test-web]
      params:
        - name: services
          value: $(params.services)
      workspaces:
        - name: source
          workspace: source
```

## Monitoring и Observability

### Metrics collection
```yaml
# Prometheus metrics для Tekton
apiVersion: v1
kind: ConfigMap
metadata:
  name: tekton-metrics-config
  namespace: tekton-pipelines
data:
  _example: |
    # Tekton metrics configuration
    metrics.taskrun.duration-type: "histogram"
    metrics.taskrun.count-type: "counter"
    metrics.pipelinerun.duration-type: "histogram"
    metrics.pipelinerun.count-type: "counter"

---
# ServiceMonitor для Prometheus
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: tekton-pipelines-monitor
  namespace: tekton-pipelines
spec:
  selector:
    matchLabels:
      app: tekton-pipelines-controller
  endpoints:
    - port: metrics
      path: /metrics
      interval: 30s

---
# Grafana dashboard для Tekton
apiVersion: v1
kind: ConfigMap
metadata:
  name: tekton-dashboard
  namespace: monitoring
data:
  dashboard.json: |
    {
      "dashboard": {
        "title": "Tekton CI/CD",
        "panels": [
          {
            "title": "Pipeline Runs",
            "type": "stat",
            "targets": [
              {
                "expr": "tekton_pipelinerun_count_total",
                "legendFormat": "Total Runs"
              }
            ]
          }
        ]
      }
    }
```

### Logging aggregation
```yaml
# Fluent Bit конфигурация для Tekton logs
apiVersion: v1
kind: ConfigMap
metadata:
  name: fluent-bit-config
  namespace: tekton-pipelines
data:
  fluent-bit.conf: |
    [SERVICE]
        Flush         5
        Log_Level     info
        Daemon        off

    [INPUT]
        Name              tail
        Path              /var/log/containers/*tekton*.log
        Parser            docker
        Tag               tekton.*
        Refresh_Interval  5

    [FILTER]
        Name                kubernetes
        Match               tekton.*
        Kube_URL           https://kubernetes.default.svc:443
        Kube_CA_File       /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
        Kube_Token_File    /var/run/secrets/kubernetes.io/serviceaccount/token

    [OUTPUT]
        Name  es
        Match tekton.*
        Host  elasticsearch-master
        Port  9200
        Index tekton-logs

---
# Elasticsearch индекс template для Tekton
apiVersion: v1
kind: ConfigMap
metadata:
  name: tekton-es-template
data:
  template.json: |
    {
      "index_patterns": ["tekton-logs-*"],
      "mappings": {
        "properties": {
          "@timestamp": { "type": "date" },
          "kubernetes": {
            "properties": {
              "namespace_name": { "type": "keyword" },
              "pod_name": { "type": "keyword" },
              "container_name": { "type": "keyword" }
            }
          },
          "log": { "type": "text" },
          "stream": { "type": "keyword" }
        }
      }
    }
```

### Distributed tracing
```yaml
# OpenTelemetry integration
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: traced-task
spec:
  params:
    - name: service-name
    - name: operation-name
  steps:
    - name: setup-tracing
      image: otel/opentelemetry-collector:latest
      script: |
        # Setup OpenTelemetry
        export OTEL_SERVICE_NAME=$(params.service-name)
        export OTEL_TRACES_EXPORTER=jaeger
        export OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger-collector:14268/api/traces

    - name: traced-operation
      image: my-app:latest
      env:
        - name: OTEL_SERVICE_NAME
          value: $(params.service-name)
        - name: OTEL_TRACES_EXPORTER
          value: jaeger
      script: |
        # Your traced operation
        echo "Starting $(params.operation-name)"
        # Operation logic here
        echo "Completed $(params.operation-name)"
```

## Security и Best Practices

### RBAC и Security Contexts
```yaml
# Security Context для pipeline tasks
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: secure-task
spec:
  steps:
    - name: secure-step
      image: alpine:latest
      securityContext:
        runAsUser: 1000
        runAsGroup: 1000
        allowPrivilegeEscalation: false
        capabilities:
          drop:
            - ALL
      script: |
        # Secure script execution
        echo "Running as non-root user"

---
# Pod Security Standards
apiVersion: tekton.dev/v1beta1
kind: PipelineRun
metadata:
  name: secure-pipeline-run
spec:
  podTemplate:
    securityContext:
      runAsUser: 1000
      runAsGroup: 1000
      fsGroup: 1000
    nodeSelector:
      kubernetes.io/os: linux
    tolerations:
      - key: "dedicated"
        operator: "Equal"
        value: "ci"
        effect: "NoSchedule"

---
# Network Policies для Tekton
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: tekton-pipeline-policy
  namespace: tekton-pipelines
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/part-of: tekton-pipelines
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              name: tekton-pipelines
  egress:
    - to:
        - ipBlock:
            cid: 0.0.0.0/0
        - ipBlock:
            cid: 10.0.0.0/8  # Allow internal network
```

### Image security scanning
```yaml
# Image security scanning task
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: image-security-scan
spec:
  params:
    - name: image-ref
    - name: severity-threshold
      default: "HIGH"
  steps:
    - name: trivy-scan
      image: aquasecurity/trivy:latest
      script: |
        trivy image --exit-code 1 --severity $(params.severity-threshold) $(params.image-ref)

    - name: grype-scan
      image: anchore/grype:latest
      script: |
        grype $(params.image-ref) --fail-on $(params.severity-threshold)

    - name: report-findings
      image: alpine:latest
      script: |
        echo "Security scan completed for $(params.image-ref)"
        # Generate security report

---
# Pipeline с security gates
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: secure-build-pipeline
spec:
  params:
    - name: image-ref
  tasks:
    - name: build
      taskRef:
        name: build-image
      params:
        - name: image
          value: $(params.image-ref)

    - name: security-scan
      taskRef:
        name: image-security-scan
      runAfter: [build]
      params:
        - name: image-ref
          value: $(params.image-ref)
        - name: severity-threshold
          value: HIGH
      onError: continue  # Continue even if scan fails

    - name: compliance-check
      taskRef:
        name: compliance-gate
      runAfter: [security-scan]
      params:
        - name: scan-results
          value: $(tasks.security-scan.results.scan-results)
      conditions:
        - conditionRef: security-compliance
          params:
            - name: threshold
              value: "90"

    - name: deploy
      taskRef:
        name: deploy-image
      runAfter: [compliance-check]
      params:
        - name: image
          value: $(params.image-ref)
```

### Secrets management
```yaml
# External secrets integration
apiVersion: tekton.dev/v1beta1
kind: Task
metadata:
  name: fetch-secrets
spec:
  params:
    - name: secret-name
    - name: vault-path
  steps:
    - name: get-secret
      image: hashicorp/vault:latest
      script: |
        vault kv get -field=value $(params.vault-path) > /workspace/secret.txt
      volumeMounts:
        - name: vault-token
          mountPath: /vault
          readOnly: true
      env:
        - name: VAULT_ADDR
          value: https://vault.example.com
        - name: VAULT_TOKEN
          valueFrom:
            secretKeyRef:
              name: vault-token
              key: token

---
# Kubernetes secrets в pipeline
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
  name: secret-using-pipeline
spec:
  tasks:
    - name: use-secret
      taskRef:
        name: deploy-with-secret
      params:
        - name: api-key
          valueFrom:
            secretKeyRef:
              name: api-secret
              key: token
        - name: db-password
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Tekton Documentation](https://tekton.dev/docs/)
- [Tekton GitHub](https://github.com/tektoncd)
- [Tekton Hub](https://hub.tekton.dev/)
- [Tekton Triggers](https://github.com/tektoncd/triggers)
- [Tekton CLI](https://github.com/tektoncd/cli)

## См. также
- [Jenkins](ci-cd/jenkins.md) - Traditional CI/CD
- [GitLab CI](ci-cd/gitlab-ci.md) - GitOps CI/CD
- [ArgoCD](devops/argocd.md) - GitOps deployment
- [Kubernetes](devops/kubernetes-advanced.md) - Container orchestration
- [Istio](devops/kubernetes-service-mesh.md) - Service mesh
