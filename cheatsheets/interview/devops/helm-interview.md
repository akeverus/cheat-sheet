---
title: "Вопросы на собеседовании: Helm"
description: "Вопросы и ответы по Helm: chart структура, Go templates, values, dependencies, hooks, release management, Helm 3 vs 2, secrets, GitOps интеграция, best practices."
tags:
  - interview
  - devops
  - helm-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Helm"
  - "Helm interview"
  - "Helm собеседование"
prerequisites:
  - "[[helm]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Helm`

Вопросы и ответы по `Helm`: архитектура chart-ов, `Go templates`, управление values, зависимости, хуки, управление релизами, `Helm 3` vs `Helm 2`, секреты, интеграция с `ArgoCD`/`GitOps` и best practices.

**`Helm`** — пакетный менеджер для [Kubernetes](kubernetes-interview.md), де-факто стандарт для упаковки, конфигурирования и деплоя приложений в кластер. `Chart` — это пакет шаблонов Kubernetes-манифестов с параметризацией через `values.yaml`. `Helm` позволяет версионировать деплои, откатываться на предыдущую версию и управлять зависимостями между приложениями.

## Полезные ссылки

### Официальная документация

- [Helm Documentation](https://helm.sh/docs/) — официальная документация
- [Helm Chart Template Guide](https://helm.sh/docs/chart_template_guide/) — руководство по Go templates
- [Helm Best Practices](https://helm.sh/docs/chart_best_practices/) — лучшие практики
- [Artifact Hub](https://artifacthub.io/) — репозиторий публичных Helm chart-ов
- [Using Helm and Kubernetes (Baeldung)](https://www.baeldung.com/ops/kubernetes-helm) — Helm для управления Kubernetes-приложениями
- [Helm Chart Hook Documentation](https://helm.sh/docs/topics/charts_hooks/) — документация по hooks

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Helm**
- [Q1. (!) Что такое Helm и какие задачи он решает?](#q1--что-такое-helm-и-какие-задачи-он-решает)
- [Q2. (!) Что такое Helm Chart?](#q2--что-такое-helm-chart)
- [Q3. Чем Helm 3 отличается от Helm 2?](#q3-чем-helm-3-отличается-от-helm-2)
- [Q4. Что такое Release в контексте Helm?](#q4-что-такое-release-в-контексте-helm)

**Структура Chart**
- [Q5. (!) Какова структура директорий Helm Chart?](#q5--какова-структура-директорий-helm-chart)
- [Q6. (!) Что содержит файл Chart.yaml?](#q6--что-содержит-файл-chartyaml)
- [Q7. Что такое файл values.yaml и как он используется?](#q7-что-такое-файл-valuesyaml-и-как-он-используется)
- [Q8. Что такое файл _helpers.tpl?](#q8-что-такое-файл-_helperstpl)
- [Q9. Что такое NOTES.txt в Helm Chart?](#q9-что-такое-notestxt-в-helm-chart)

**Основные команды**
- [Q10. (!) Какие основные команды Helm CLI вы знаете?](#q10--какие-основные-команды-helm-cli-вы-знаете)
- [Q11. (!) Как установить, обновить и откатить релиз?](#q11--как-установить-обновить-и-откатить-релиз)
- [Q12. Как просмотреть сгенерированные манифесты перед установкой?](#q12-как-просмотреть-сгенерированные-манифесты-перед-установкой)
- [Q13. Как работает команда helm diff?](#q13-как-работает-команда-helm-diff)

**Go Templates**
- [Q14. (!) Какой синтаксис используется в Helm templates?](#q14--какой-синтаксис-используется-в-helm-templates)
- [Q15. (!) Как использовать встроенные объекты .Values, .Release, .Chart?](#q15--как-использовать-встроенные-объекты-values-release-chart)
- [Q16. Как использовать управляющие конструкции: if, range, with?](#q16-как-использовать-управляющие-конструкции-if-range-with)
- [Q17. (!) Что такое named templates и функция include?](#q17--что-такое-named-templates-и-функция-include)
- [Q18. В чём разница между include и template?](#q18-в-чём-разница-между-include-и-template)
- [Q19. Какие встроенные функции Helm templates часто используются?](#q19-какие-встроенные-функции-helm-templates-часто-используются)

**Values и конфигурация**
- [Q20. (!) Как переопределить values при установке chart?](#q20--как-переопределить-values-при-установке-chart)
- [Q21. Какой приоритет у разных источников values?](#q21-какой-приоритет-у-разных-источников-values)
- [Q22. Что такое глобальные values (global)?](#q22-что-такое-глобальные-values-global)

**Chart Dependencies**
- [Q23. (!) Как управлять зависимостями между chart-ами?](#q23--как-управлять-зависимостями-между-chart-ами)
- [Q24. Что делает команда helm dependency update?](#q24-что-делает-команда-helm-dependency-update)
- [Q25. Как передать values дочернему chart?](#q25-как-передать-values-дочернему-chart)

**Helm Hooks**
- [Q26. (!) Что такое Helm Hooks и зачем они нужны?](#q26--что-такое-helm-hooks-и-зачем-они-нужны)
- [Q27. (!) Какие типы хуков существуют в Helm?](#q27--какие-типы-хуков-существуют-в-helm)
- [Q28. Что такое hook-weight и hook-delete-policy?](#q28-что-такое-hook-weight-и-hook-delete-policy)

**Репозитории и Registry**
- [Q29. Как работать с Helm Repository?](#q29-как-работать-с-helm-repository)
- [Q30. (!) Что такое OCI Registry для Helm?](#q30--что-такое-oci-registry-для-helm)

**Типы chart-ов**
- [Q31. (!) В чём разница между Application chart и Library chart?](#q31--в-чём-разница-между-application-chart-и-library-chart)

**Тестирование**
- [Q32. (!) Как тестировать Helm Charts?](#q32--как-тестировать-helm-charts)
- [Q33. Что такое helm lint и когда его применять?](#q33-что-такое-helm-lint-и-когда-его-применять)

**Секреты**
- [Q34. (!) Как управлять секретами в Helm?](#q34--как-управлять-секретами-в-helm)

**GitOps и интеграции**
- [Q35. (!) Как Helm интегрируется с ArgoCD и GitOps?](#q35--как-helm-интегрируется-с-argocd-и-gitops)

**Best Practices**
- [Q36. (!) Какие best practices применяются при разработке Helm Charts?](#q36--какие-best-practices-применяются-при-разработке-helm-charts)

**OCI Registry и хранение чартов**
- [Q37. Что такое OCI Registry для Helm и как с ним работать?](#q37-что-такое-oci-registry-для-helm-и-как-с-ним-работать)
- [Q38. Chart Museum vs OCI Registry — в чём разница подходов?](#q38-chart-museum-vs-oci-registry--в-чём-разница-подходов)

**GitOps и интеграции**
- [Q39. Как использовать Helm в GitOps с ArgoCD и ApplicationSet?](#q39-как-использовать-helm-в-gitops-с-argocd-и-applicationset)
- [Q40. Как управлять секретами через Helm Secrets плагин с SOPS?](#q40-как-управлять-секретами-через-helm-secrets-плагин-с-sops)
- [Q41. Как совместно использовать Helm и Kustomize?](#q41-как-совместно-использовать-helm-и-kustomize)

**Тестирование и безопасность**
- [Q42. Как писать unit-тесты для Helm Charts с helm-unittest?](#q42-как-писать-unit-тесты-для-helm-charts-с-helm-unittest)
- [Q43. Что такое Helm provenance и как подписывать чарты?](#q43-что-такое-helm-provenance-и-как-подписывать-чарты)

---

## Q1. (!) Что такое Helm и какие задачи он решает?

**`Helm`** — пакетный менеджер для Kubernetes, аналог `apt`/`yum` для Linux или `npm` для Node.js.

**Задачи, которые решает Helm:**

- **Упаковка** — группирует множество Kubernetes-манифестов в единый артефакт (`chart`)
- **Параметризация** — позволяет конфигурировать деплой через `values.yaml` без изменения шаблонов
- **Версионирование** — каждый деплой создаёт `revision`, история сохраняется
- **Откат** — `helm rollback` возвращает к предыдущей ревизии за секунды
- **Управление зависимостями** — chart может зависеть от других chart-ов (PostgreSQL, Redis и т.д.)
- **Repeatability** — один chart разворачивается одинаково в dev/staging/prod через разные values

```bash
# Установить chart из репозитория
helm install my-app bitnami/nginx --values prod-values.yaml

# Обновить
helm upgrade my-app bitnami/nginx --values prod-values.yaml

# Откатить
helm rollback my-app 2
```

---

## Q2. (!) Что такое Helm Chart?

**`Chart`** — директория (или `.tgz` архив) с фиксированной структурой, содержащая шаблоны Kubernetes-манифестов и их метаданные.

Chart описывает, как должно быть установлено приложение. При рендеринге Helm подставляет values в Go-шаблоны и получает финальные YAML-манифесты.

**Жизненный цикл chart:**
1. Разработчик создаёт chart с шаблонами
2. Chart публикуется в репозиторий
3. `helm install` рендерит шаблоны + применяет к кластеру → создаётся `Release`
4. `helm upgrade` обновляет release, инкрементирует revision
5. История хранится в Kubernetes secrets в namespace релиза

---

## Q3. Чем Helm 3 отличается от Helm 2?

| Аспект | Helm 2 | Helm 3 |
|---|---|---|
| **Серверный компонент** | `Tiller` (pod в кластере) | Нет — только client |
| **Безопасность** | Tiller имел широкие права RBAC | Права ограничены правами пользователя |
| **Хранение state** | ConfigMap в `kube-system` | Secret в namespace релиза |
| **CRD** | Устанавливались вместе с chart | Отдельный lifecycle через `crds/` директорию |
| **Namespace scope** | Release глобальный | Release привязан к namespace |
| **Схема values** | Нет валидации | `values.schema.json` — JSON Schema валидация |
| **Команды** | `helm delete`, `helm fetch` | `helm uninstall`, `helm pull` |

Главное изменение: **удаление `Tiller`** значительно улучшило безопасность — больше не нужен pod с правами cluster-admin.

---

## Q4. Что такое Release в контексте Helm?

**`Release`** — именованный экземпляр chart, установленного в Kubernetes.

- Один chart можно установить несколько раз под разными именами (разные releases)
- Каждый `helm upgrade` создаёт новую `revision` в рамках release
- Metadata release хранится как Kubernetes `Secret` типа `helm.sh/release.v1` в namespace релиза
- История ревизий ограничена параметром `--history-max` (по умолчанию 10)

```bash
helm list                    # Список всех releases в текущем namespace
helm list -A                 # Все releases во всех namespaces
helm history my-app          # История ревизий release
helm status my-app           # Текущий статус release
```

---

## Q5. (!) Какова структура директорий Helm Chart?

```
my-chart/
├── Chart.yaml          # Метаданные chart (имя, версия, описание)
├── values.yaml         # Значения по умолчанию
├── values.schema.json  # JSON Schema для валидации values (опционально)
├── charts/             # Зависимые chart-ы (subcharts)
├── crds/               # Custom Resource Definitions (Helm 3)
├── templates/          # Шаблоны Kubernetes-манифестов
│   ├── _helpers.tpl    # Named templates (вспомогательные функции)
│   ├── NOTES.txt       # Сообщение после установки
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── configmap.yaml
│   └── hpa.yaml
└── .helmignore         # Паттерны файлов, исключаемых из packaging
```

Файлы в `templates/`, начинающиеся с `_`, не рендерятся как манифесты — они используются только как библиотека named templates.

---

## Q6. (!) Что содержит файл Chart.yaml?

`Chart.yaml` — обязательный файл с метаданными chart.

```yaml
apiVersion: v2                    # v1 (Helm 2), v2 (Helm 3)
name: my-app                      # Имя chart
description: My application       # Описание
type: application                 # application или library
version: 1.2.3                    # Версия самого chart (SemVer)
appVersion: "2.0.0"               # Версия приложения (информационно)
keywords:
  - java
  - spring-boot
home: https://example.com
sources:
  - https://github.com/org/my-app
maintainers:
  - name: Team Name
    email: team@example.com
dependencies:
  - name: postgresql
    version: "13.x.x"
    repository: "https://charts.bitnami.com/bitnami"
    condition: postgresql.enabled
```

**Ключевые поля:**
- `version` — версия chart; меняется при изменении шаблонов
- `appVersion` — версия приложения (образа); не влияет на Helm логику
- `type: library` — chart без манифестов, только named templates

---

## Q7. Что такое файл values.yaml и как он используется?

`values.yaml` — файл со значениями по умолчанию для шаблонов. Все значения доступны в шаблонах через объект `.Values`.

```yaml
# values.yaml
replicaCount: 2

image:
  repository: myregistry/my-app
  tag: "1.0.0"
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 8080

resources:
  requests:
    cpu: 100m
    memory: 256Mi
  limits:
    cpu: 500m
    memory: 512Mi

ingress:
  enabled: false
  host: ""
```

В шаблоне:
```yaml
# templates/deployment.yaml
spec:
  replicas: {{ .Values.replicaCount }}
  template:
    spec:
      containers:
        - image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
```

---

## Q8. Что такое файл _helpers.tpl?

`_helpers.tpl` — файл с **named templates** (переиспользуемые блоки шаблонов). Файл начинается с `_`, поэтому Helm не генерирует из него манифест.

```yaml
{{/*
Expand the name of the chart.
*/}}
{{- define "my-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "my-app.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "my-app.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/name: {{ include "my-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}
```

Использование в шаблоне:
```yaml
metadata:
  name: {{ include "my-app.fullname" . }}
  labels:
    {{- include "my-app.labels" . | nindent 4 }}
```

---

## Q9. Что такое NOTES.txt в Helm Chart?

`NOTES.txt` — шаблон текстового сообщения, которое Helm выводит после успешной установки/обновления релиза. Помогает пользователю узнать, как получить доступ к приложению.

```
{{- if .Values.ingress.enabled }}
Приложение доступно по адресу: https://{{ .Values.ingress.host }}
{{- else }}
Для доступа к приложению выполните:
  kubectl port-forward svc/{{ include "my-app.fullname" . }} 8080:{{ .Values.service.port }}
  Затем откройте: http://localhost:8080
{{- end }}
```

---

## Q10. (!) Какие основные команды Helm CLI вы знаете?

```bash
# Управление релизами
helm install <name> <chart>        # Установить
helm upgrade <name> <chart>        # Обновить (или установить с --install)
helm rollback <name> [revision]    # Откатить
helm uninstall <name>              # Удалить
helm list [-A]                     # Список релизов
helm status <name>                 # Статус релиза
helm history <name>                # История ревизий

# Работа с chart-ами
helm create <name>                 # Создать scaffold chart
helm package <dir>                 # Упаковать в .tgz
helm lint <dir>                    # Проверить синтаксис
helm template <name> <chart>       # Рендер шаблонов без установки
helm show values <chart>           # Показать values chart
helm show chart <chart>            # Показать Chart.yaml

# Репозитории
helm repo add <name> <url>         # Добавить репозиторий
helm repo update                   # Обновить индекс репозиториев
helm repo list                     # Список репозиториев
helm search repo <keyword>         # Поиск по репозиториям
helm search hub <keyword>          # Поиск на Artifact Hub

# OCI / pull
helm pull <chart>                  # Скачать chart архив
helm push <chart.tgz> oci://...    # Публикация в OCI registry
```

---

## Q11. (!) Как установить, обновить и откатить релиз?

```bash
# Установка
helm install my-app ./my-chart \
  --namespace production \
  --create-namespace \
  --values prod-values.yaml \
  --set image.tag=1.2.3

# Обновление (--install создаёт если не существует — idempotent)
helm upgrade --install my-app ./my-chart \
  --namespace production \
  --values prod-values.yaml \
  --set image.tag=1.3.0 \
  --atomic \          # Откатить автоматически если upgrade упал
  --timeout 5m

# Просмотр истории
helm history my-app -n production

# Откат на конкретную ревизию
helm rollback my-app 3 -n production

# Откат на предыдущую ревизию
helm rollback my-app -n production
```

Флаг `--atomic` делает upgrade атомарным: если Pod-ы не стартуют за `--timeout`, Helm автоматически откатывается.

---

## Q12. Как просмотреть сгенерированные манифесты перед установкой?

```bash
# Рендер шаблонов без установки в кластер
helm template my-app ./my-chart \
  --values prod-values.yaml \
  --set image.tag=1.2.3

# Dry-run (отправляет в API server для валидации, но не применяет)
helm install my-app ./my-chart \
  --dry-run \
  --values prod-values.yaml

# Debug mode — видно все шаги вычисления
helm install my-app ./my-chart --debug --dry-run

# Посмотреть values конкретного релиза
helm get values my-app
helm get values my-app --all    # Включая значения по умолчанию
helm get manifest my-app        # Финальные манифесты установленного релиза
```

---

## Q13. Как работает команда helm diff?

`helm diff` — плагин (не входит в стандартный Helm), показывает различия между установленным релизом и предлагаемым обновлением. Аналог `git diff` для Kubernetes-манифестов.

```bash
# Установка плагина
helm plugin install https://github.com/databus23/helm-diff

# Diff между текущим релизом и новыми values/chart
helm diff upgrade my-app ./my-chart \
  --values prod-values.yaml \
  --set image.tag=1.3.0

# Diff между ревизиями
helm diff revision my-app 3 4
```

Активно используется в GitOps пайплайнах для code review изменений перед применением.

---

## Q14. (!) Какой синтаксис используется в Helm templates?

Helm использует **Go `text/template`** с расширениями от библиотеки `Sprig`.

```yaml
# Подстановка значения
{{ .Values.replicaCount }}

# Убрать лишние пробелы/переносы строк
{{- .Values.replicaCount -}}

# Условие
{{- if .Values.ingress.enabled }}
# yaml блок
{{- end }}

# Цикл по списку
{{- range .Values.env }}
- name: {{ .name }}
  value: {{ .value | quote }}
{{- end }}

# Цикл по map (ключ-значение)
{{- range $key, $value := .Values.annotations }}
{{ $key }}: {{ $value | quote }}
{{- end }}

# Область видимости with
{{- with .Values.resources }}
resources:
  {{- toYaml . | nindent 2 }}
{{- end }}

# Вызов named template
{{- include "my-app.labels" . | nindent 4 }}

# Переменная
{{- $name := include "my-app.fullname" . }}
```

Дефис `-` рядом с `{{` или `}}` убирает пробельные символы (включая переносы строк) с соответствующей стороны.

---

## Q15. (!) Как использовать встроенные объекты .Values, .Release, .Chart?

| Объект | Поля | Пример |
|---|---|---|
| `.Values` | Все значения из values.yaml | `.Values.image.tag` |
| `.Release` | Метаданные релиза | `.Release.Name`, `.Release.Namespace`, `.Release.Revision` |
| `.Chart` | Содержимое Chart.yaml | `.Chart.Name`, `.Chart.Version`, `.Chart.AppVersion` |
| `.Files` | Доступ к файлам в chart | `.Files.Get "config.ini"` |
| `.Capabilities` | Возможности кластера | `.Capabilities.KubeVersion.Major` |
| `.Template` | Текущий шаблон | `.Template.Name`, `.Template.BasePath` |

```yaml
metadata:
  name: {{ .Release.Name }}-{{ .Chart.Name }}
  namespace: {{ .Release.Namespace }}
  labels:
    app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
    helm.sh/chart: "{{ .Chart.Name }}-{{ .Chart.Version }}"
```

---

## Q16. Как использовать управляющие конструкции: if, range, with?

```yaml
# if / else if / else
{{- if eq .Values.service.type "LoadBalancer" }}
  loadBalancerIP: {{ .Values.service.loadBalancerIP }}
{{- else if eq .Values.service.type "NodePort" }}
  nodePort: {{ .Values.service.nodePort }}
{{- else }}
  # ClusterIP — ничего дополнительного
{{- end }}

# Логические операторы: and, or, not
{{- if and .Values.ingress.enabled .Values.ingress.tls }}
  # TLS настроен
{{- end }}

# range по списку
env:
{{- range .Values.extraEnv }}
  - name: {{ .name }}
    value: {{ .value | quote }}
{{- end }}

# with — изменяет scope на указанный объект, пропускает блок если nil/empty
{{- with .Values.nodeSelector }}
nodeSelector:
  {{- toYaml . | nindent 2 }}
{{- end }}
```

---

## Q17. (!) Что такое named templates и функция include?

**Named template** — именованный переиспользуемый блок шаблона, объявленный через `define`.

```yaml
# В _helpers.tpl
{{- define "my-app.selectorLabels" -}}
app.kubernetes.io/name: {{ include "my-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

# В deployment.yaml
selector:
  matchLabels:
    {{- include "my-app.selectorLabels" . | nindent 4 }}
spec:
  template:
    metadata:
      labels:
        {{- include "my-app.selectorLabels" . | nindent 8 }}
```

`include` возвращает строку, что позволяет применять к ней pipe-функции (`nindent`, `trim` и т.д.). `template` не возвращает значение и не может использоваться в pipe.

---

## Q18. В чём разница между include и template?

| Функция | Возвращает значение | Pipe | Рекомендуется |
|---|---|---|---|
| `template "name" .` | Нет (выводит напрямую) | Нет | Нет |
| `include "name" .` | Да (строка) | Да | Да |

```yaml
# template — нельзя применить nindent, trim и т.д.
labels:
  {{ template "my-app.labels" . }}   # Проблема с отступами

# include — pipe позволяет корректно форматировать YAML
labels:
  {{- include "my-app.labels" . | nindent 2 }}
```

Helm best practice: **всегда использовать `include`**, не `template`.

---

## Q19. Какие встроенные функции Helm templates часто используются?

```yaml
# Строковые
{{ .Values.name | upper }}             # MY-APP
{{ .Values.name | lower }}             # my-app
{{ .Values.name | trim }}              # убрать пробелы
{{ .Values.name | quote }}             # "my-app" (кавычки)
{{ .Values.name | trunc 63 }}          # Обрезать до 63 символов
{{ printf "%s-%s" .Release.Name .Chart.Name }}

# YAML
{{- toYaml .Values.resources | nindent 10 }}   # Конвертировать в YAML с отступами
{{- toJson .Values.config | quote }}            # Конвертировать в JSON

# Значение по умолчанию
{{ .Values.timeout | default 30 }}
{{ .Values.name | default .Chart.Name }}

# Условная логика
{{ ternary "yes" "no" .Values.enabled }}       # Тернарный оператор

# Base64
{{ .Values.password | b64enc }}
{{ .Values.encoded | b64dec }}

# Indent
{{ include "my-app.labels" . | indent 4 }}     # Добавить 4 пробела
{{ include "my-app.labels" . | nindent 4 }}    # + перенос строки перед
```

---

## Q20. (!) Как переопределить values при установке chart?

```bash
# --set — одиночное значение (через точку для вложенных)
helm install my-app ./my-chart \
  --set image.tag=2.0.0 \
  --set replicaCount=3

# --set-string — принудительно строка (полезно для числоподобных значений)
helm install my-app ./my-chart \
  --set-string image.tag=1234567890ab

# --set-json — JSON-значение
helm install my-app ./my-chart \
  --set-json 'resources={"limits":{"cpu":"500m"}}'

# --values / -f — файл values (можно несколько)
helm install my-app ./my-chart \
  --values base-values.yaml \
  --values prod-override.yaml

# Список через запятую
helm install my-app ./my-chart \
  --set 'tolerations[0].key=dedicated,tolerations[0].value=gpu'
```

---

## Q21. Какой приоритет у разных источников values?

Порядок приоритетов (от низшего к высшему):

1. `values.yaml` внутри chart (значения по умолчанию)
2. `values.yaml` родительского chart (если subchart)
3. Файлы `--values`/`-f` (в порядке передачи, последний имеет приоритет)
4. `--set` / `--set-string` / `--set-json` (самый высокий приоритет)

Более поздние `--values` перекрывают более ранние. `--set` перекрывает всё.

---

## Q22. Что такое глобальные values (global)?

Секция `global` в `values.yaml` доступна во всех subcharts через `.Values.global`:

```yaml
# Родительский values.yaml
global:
  imageRegistry: registry.company.com
  imagePullSecrets:
    - name: regcred
  storageClass: fast-ssd

postgresql:
  enabled: true
```

В subchart шаблоне:
```yaml
image: "{{ .Values.global.imageRegistry }}/{{ .Values.image.repository }}"
```

`global` — единственный механизм передачи values от родителя к потомкам без явной переадресации.

---

## Q23. (!) Как управлять зависимостями между chart-ами?

Зависимости объявляются в `Chart.yaml`:

```yaml
dependencies:
  - name: postgresql
    version: "13.2.x"
    repository: "https://charts.bitnami.com/bitnami"
    condition: postgresql.enabled      # Устанавливать только если true
    tags:
      - database

  - name: redis
    version: "18.x.x"
    repository: "oci://registry-1.docker.io/bitnamicharts"
    condition: redis.enabled

  - name: common
    version: "2.x.x"
    repository: "https://charts.bitnami.com/bitnami"
```

```bash
# Скачать зависимости в charts/
helm dependency update ./my-chart

# Список зависимостей
helm dependency list ./my-chart
```

После `helm dependency update` зависимые chart-ы появляются как `.tgz` в `charts/` и фиксируются в `Chart.lock`.

---

## Q24. Что делает команда helm dependency update?

`helm dependency update` (синоним `helm dep update`) выполняет:

1. Читает `dependencies` из `Chart.yaml`
2. Скачивает каждый зависимый chart из указанного репозитория
3. Сохраняет `.tgz` архивы в директорию `charts/`
4. Создаёт/обновляет `Chart.lock` с точными версиями (аналог `package-lock.json`)

`Chart.lock` следует коммитить в git для воспроизводимых сборок. При CI/CD используют `helm dependency build` (использует `Chart.lock` без обновления).

---

## Q25. Как передать values дочернему chart?

Values для subchart передаются через одноимённую секцию в родительском `values.yaml`:

```yaml
# Родительский values.yaml
postgresql:
  enabled: true
  auth:
    username: myuser
    password: mypassword
    database: mydb
  primary:
    persistence:
      size: 10Gi

redis:
  enabled: false
  auth:
    enabled: true
    password: redispass
```

Имя секции должно совпадать с именем (`name`) зависимости из `Chart.yaml`. Также работает передача через `global`.

---

## Q26. (!) Что такое Helm Hooks и зачем они нужны?

**`Helm Hooks`** — механизм выполнения Kubernetes-ресурсов (обычно `Job` или `Pod`) в определённые моменты жизненного цикла релиза.

Используются для:
- Выполнения миграций БД **до** обновления приложения (`pre-upgrade`)
- Инициализации данных при первой установке (`post-install`)
- Cleanup операций при удалении (`pre-delete`)
- Проверок после деплоя (`post-upgrade`)

Hook объявляется через аннотацию на любом Kubernetes-ресурсе:

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: {{ include "my-app.fullname" . }}-db-migrate
  annotations:
    "helm.sh/hook": pre-upgrade,pre-install
    "helm.sh/hook-weight": "-5"
    "helm.sh/hook-delete-policy": before-hook-creation,hook-succeeded
spec:
  template:
    spec:
      restartPolicy: Never
      containers:
        - name: migrate
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          command: ["java", "-jar", "app.jar", "--migrate-only"]
```

---

## Q27. (!) Какие типы хуков существуют в Helm?

| Hook | Момент выполнения |
|---|---|
| `pre-install` | До создания ресурсов при `helm install` |
| `post-install` | После создания всех ресурсов при `helm install` |
| `pre-delete` | До удаления ресурсов при `helm uninstall` |
| `post-delete` | После удаления всех ресурсов при `helm uninstall` |
| `pre-upgrade` | До обновления ресурсов при `helm upgrade` |
| `post-upgrade` | После обновления всех ресурсов при `helm upgrade` |
| `pre-rollback` | До отката ресурсов при `helm rollback` |
| `post-rollback` | После отката всех ресурсов при `helm rollback` |
| `test` | Запускается командой `helm test` |

Один ресурс может быть назначен нескольким хукам через запятую: `"helm.sh/hook": pre-install,pre-upgrade`.

---

## Q28. Что такое hook-weight и hook-delete-policy?

**`hook-weight`** — определяет порядок выполнения хуков одного типа. Хуки сортируются по весу (целые числа, возможны отрицательные). Меньший вес выполняется первым.

```yaml
annotations:
  "helm.sh/hook": pre-upgrade
  "helm.sh/hook-weight": "-10"    # Выполнится раньше хука с весом 0
```

**`hook-delete-policy`** — когда удалять hook-ресурс:

| Политика | Поведение |
|---|---|
| `before-hook-creation` | Удалить старый hook-ресурс перед созданием нового (default) |
| `hook-succeeded` | Удалить если hook завершился успешно |
| `hook-failed` | Удалить если hook завершился с ошибкой |

```yaml
annotations:
  "helm.sh/hook-delete-policy": before-hook-creation,hook-succeeded
```

---

## Q29. Как работать с Helm Repository?

```bash
# Добавить репозиторий
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add stable https://charts.helm.sh/stable

# Обновить индексы всех репозиториев
helm repo update

# Список репозиториев
helm repo list

# Поиск chart в репозиториях
helm search repo nginx
helm search repo bitnami/postgresql --versions

# Удалить репозиторий
helm repo remove bitnami

# Показать values chart из репозитория
helm show values bitnami/postgresql

# Скачать chart локально
helm pull bitnami/postgresql --version 13.2.0
helm pull bitnami/postgresql --untar  # Распаковать после скачивания
```

Репозитории хранят `index.yaml` — индекс доступных chart-ов с метаданными.

---

## Q30. (!) Что такое OCI Registry для Helm?

Начиная с Helm 3.8, `OCI` (Open Container Initiative) реестры поддерживаются нативно для хранения chart-ов. Chart-ы хранятся как OCI артефакты в Docker registry.

```bash
# Аутентификация в OCI registry
helm registry login registry.company.com \
  --username user --password token

# Упаковать chart
helm package ./my-chart

# Загрузить в OCI registry
helm push my-chart-1.0.0.tgz oci://registry.company.com/helm-charts

# Установить из OCI registry
helm install my-app oci://registry.company.com/helm-charts/my-chart \
  --version 1.0.0

# Скачать chart из OCI
helm pull oci://registry.company.com/helm-charts/my-chart --version 1.0.0

# OCI не требует helm repo add — работает как pull с полным URL
```

**Преимущества OCI registry:**
- Единый реестр для Docker-образов и Helm-chart-ов
- Нативная аутентификация RBAC
- Надёжное хранение и версионирование через digest

---

## Q31. (!) В чём разница между Application chart и Library chart?

| Тип | `type:` в Chart.yaml | Содержит манифесты | Можно устанавливать |
|---|---|---|---|
| **Application** | `application` (default) | Да | Да |
| **Library** | `library` | Нет | Нет (только как dependency) |

**Library chart** — переиспользуемая библиотека named templates без собственных манифестов. Используется через `dependencies`.

```yaml
# library chart: Chart.yaml
apiVersion: v2
name: common
type: library
version: 1.0.0
```

```yaml
# В _helpers.tpl library chart
{{- define "common.labels" -}}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end }}
```

Другой chart подключает library как dependency и использует её templates:
```yaml
{{- include "common.labels" . | nindent 4 }}
```

Library charts применяются для DRY — вынесения общих шаблонных блоков (labels, tolerations, affinity) в одно место.

---

## Q32. (!) Как тестировать Helm Charts?

**`helm lint`** — статический анализ chart на ошибки:
```bash
helm lint ./my-chart --values test-values.yaml --strict
```

**`helm template`** — рендер шаблонов для ручной проверки:
```bash
helm template my-app ./my-chart --values test-values.yaml | kubectl apply --dry-run=client -f -
```

**`helm test`** — запуск test-хуков в уже установленном релизе:
```yaml
# templates/tests/test-connection.yaml
apiVersion: v1
kind: Pod
metadata:
  name: {{ include "my-app.fullname" . }}-test
  annotations:
    "helm.sh/hook": test
spec:
  restartPolicy: Never
  containers:
    - name: wget
      image: busybox
      command: ['wget', '--spider', 'http://{{ include "my-app.fullname" . }}:{{ .Values.service.port }}/actuator/health']
```
```bash
helm test my-app
```

**`ct` (chart-testing)** — инструмент для CI/CD:
```bash
# Lint и test изменённых chart-ов
ct lint --target-branch main
ct install --target-branch main
```

`ct` автоматически определяет изменённые chart-ы, устанавливает их в Kind/Minikube и запускает `helm test`.

---

## Q33. Что такое helm lint и когда его применять?

`helm lint` проверяет chart на типичные ошибки:

```bash
helm lint ./my-chart
helm lint ./my-chart --values custom-values.yaml
helm lint ./my-chart --strict    # Предупреждения трактуются как ошибки
```

**Что проверяет:**
- Корректность `Chart.yaml` (обязательные поля)
- Синтаксис Go templates
- Базовую валидность YAML
- Структуру директорий

**Что не проверяет:**
- Семантику Kubernetes API
- Корректность значений (для этого нужен `values.schema.json`)

Применяют в CI на каждый commit / Pull Request перед `helm upgrade`.

---

## Q34. (!) Как управлять секретами в Helm?

**Проблема:** `values.yaml` хранится в git открытым текстом. Kubernetes Secrets в Helm хранятся в release secret как base64 (не зашифровано).

**Подходы:**

**1. `helm-secrets` плагин (SOPS):**
```bash
helm plugin install https://github.com/jkroepke/helm-secrets
# secrets.yaml шифруется через SOPS (AWS KMS, GCP KMS, PGP)
helm secrets upgrade my-app ./my-chart -f secrets://secrets.yaml
```

**2. Sealed Secrets (Bitnami):**
```bash
# SealedSecret шифруется публичным ключом контроллера в кластере
kubeseal -f secret.yaml -w sealed-secret.yaml
# sealed-secret.yaml безопасно коммитить в git
```

**3. External Secrets Operator:**
- Secrets хранятся в Vault, AWS Secrets Manager, GCP Secret Manager
- `ExternalSecret` CRD синхронизирует их в Kubernetes `Secret`
- В Helm chart деплоим только `ExternalSecret`, не сам секрет

**4. HashiCorp Vault + Vault Agent:**
- Vault Agent Injector добавляет sidecar, который монтирует секреты в файлы
- Chart содержит только аннотации для Vault Agent

**Best practice:** никогда не коммитить plaintext secrets. Использовать `helm-secrets` + SOPS или External Secrets Operator.

---

## Q35. (!) Как Helm интегрируется с ArgoCD и GitOps?

**`ArgoCD`** нативно поддерживает Helm chart-ы как источник приложений.

```yaml
# ArgoCD Application manifest
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-app
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/org/my-charts
    targetRevision: HEAD
    path: charts/my-app
    helm:
      releaseName: my-app
      valueFiles:
        - values-prod.yaml
      parameters:
        - name: image.tag
          value: "1.2.3"
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

**GitOps workflow с Helm:**
1. Chart и values хранятся в git
2. CI pipeline запускает `helm lint`, `ct lint`, unit tests
3. При merge в main — ArgoCD обнаруживает изменения и синхронизирует
4. ArgoCD рендерит chart через `helm template` и применяет diff
5. Rollback = revert коммита в git → ArgoCD синхронизирует

**Helm Operator (Flux):**
```yaml
apiVersion: helm.toolkit.fluxcd.io/v2beta1
kind: HelmRelease
metadata:
  name: my-app
spec:
  interval: 5m
  chart:
    spec:
      chart: my-app
      sourceRef:
        kind: HelmRepository
        name: my-charts
  values:
    image:
      tag: "1.2.3"
```

---

## Q36. (!) Какие best practices применяются при разработке Helm Charts?

**Именование:**
- Имя release включать во все ресурсы: `{{ include "my-app.fullname" . }}`
- Использовать стандартные labels `app.kubernetes.io/*`
- Ограничивать длину имён: `.trunc 63 | trimSuffix "-"`

**Values:**
- Документировать все values через комментарии
- Использовать `values.schema.json` для валидации типов
- Значения по умолчанию должны работать для базового случая
- Не хранить секреты в `values.yaml`

**Шаблоны:**
- Выносить переиспользуемые блоки в `_helpers.tpl`
- Использовать `include` вместо `template`
- `toYaml | nindent N` для вложенных структур (resources, affinity)
- Проверять пустые значения через `with` или `if`

**Версионирование (SemVer):**
- `version` — версия chart (меняется при изменении шаблонов/логики)
- `appVersion` — версия приложения (образа); часто передаётся через `image.tag`
- Бампать `version` при каждом изменении chart

**Безопасность:**
- `securityContext` с `runAsNonRoot: true`
- `readOnlyRootFilesystem: true` где возможно
- Resource limits для всех контейнеров
- Не использовать `latest` тег

**Тестирование и CI:**
```bash
helm lint ./my-chart --strict
helm template my-app ./my-chart | kubeval      # Валидация схемы K8s
ct lint --target-branch main                    # Для изменённых chart-ов
```

---

## Q37. Что такое OCI Registry для Helm и как с ним работать?

Начиная с `Helm 3.8` (stable) чарты можно хранить в **OCI-совместимых реестрах** (Docker Hub, GitHub Container Registry, AWS ECR, Harbor, Nexus) как OCI-артефакты. Это унифицирует инфраструктуру: тот же реестр хранит и Docker-образы, и Helm-чарты.

### Основные команды:

```bash
# Логин в OCI registry
helm registry login registry.example.com \
  --username $USER --password $TOKEN

# Упаковать чарт в .tgz
helm package ./my-chart

# Загрузить чарт в OCI registry
helm push my-chart-1.0.0.tgz oci://registry.example.com/charts

# Установить чарт из OCI registry
helm install my-app oci://registry.example.com/charts/my-chart \
  --version 1.0.0 \
  --values prod-values.yaml

# Посмотреть метаданные
helm show chart oci://registry.example.com/charts/my-chart --version 1.0.0

# Скачать чарт локально
helm pull oci://registry.example.com/charts/my-chart --version 1.0.0

# Выйти из registry
helm registry logout registry.example.com
```

### Отличие от классических репозиториев:

- Нет `index.yaml` — каждый чарт адресуется по тегу версии
- Аутентификация через стандартный docker login / credentials
- Поддерживается подпись через `cosign` (sigstore)
- ArgoCD поддерживает OCI-чарты начиная с версии 2.6

---

## Q38. Chart Museum vs OCI Registry — в чём разница подходов?

| Характеристика | Chart Museum | OCI Registry |
|---|---|---|
| **Протокол** | Helm HTTP API (index.yaml) | OCI Distribution Spec |
| **Инфраструктура** | Отдельный сервис | Существующий container registry |
| **Команды** | `helm repo add/update/install` | `helm push/pull/install oci://` |
| **Версия Helm** | Все версии | 3.8+ (стабильный) |
| **Поиск чартов** | Через index.yaml | Только по точному ref |
| **Авторизация** | Basic auth / token | Docker credentials |
| **Подпись** | Provenance файлы (.prov) | cosign / Notary v2 |
| **ArgoCD** | Через `HelmRepository` | Через `HelmRepository` (type: oci) |
| **Сложность** | Доп. сервис на поддержке | Использует готовую инфраструктуру |

**Когда Chart Museum:** команда уже использует его, нужна совместимость со старыми инструментами, или требуется публичный индекс.

**Когда OCI:** новые проекты, уже есть Harbor/ECR/GHCR, хочется единой инфраструктуры для образов и чартов.

---

## Q39. Как использовать Helm в GitOps с ArgoCD и ApplicationSet?

`ArgoCD` — GitOps-контроллер, который синхронизирует состояние кластера с Git. Helm-чарты — один из поддерживаемых source type.

### Базовое Application с Helm:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-app
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://charts.example.com
    chart: my-app
    targetRevision: 1.2.3
    helm:
      valueFiles:
        - values-prod.yaml
      parameters:
        - name: image.tag
          value: "abc123"    # Override из pipeline
      releaseName: my-app
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

### ApplicationSet — деплой на несколько окружений:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: my-app-environments
spec:
  generators:
    - list:
        elements:
          - env: dev
            namespace: my-app-dev
            valuesFile: values-dev.yaml
          - env: staging
            namespace: my-app-staging
            valuesFile: values-staging.yaml
          - env: prod
            namespace: my-app-prod
            valuesFile: values-prod.yaml
  template:
    metadata:
      name: "my-app-{{env}}"
    spec:
      source:
        repoURL: https://charts.example.com
        chart: my-app
        targetRevision: 1.2.3
        helm:
          valueFiles:
            - "{{valuesFile}}"
      destination:
        server: https://kubernetes.default.svc
        namespace: "{{namespace}}"
```

### Values override из pipeline (CI/CD → ArgoCD):

```bash
# Обновить image tag через ArgoCD CLI
argocd app set my-app-prod \
  --helm-set image.tag=$COMMIT_SHA \
  --sync-policy automated
```

**Паттерн "image updater":** `argocd-image-updater` следит за новыми тегами образов в registry и автоматически обновляет Application.

---

## Q40. Как управлять секретами через Helm Secrets плагин с SOPS?

`helm-secrets` — плагин для Helm, который шифрует файлы values через `SOPS` (Mozilla). Зашифрованные values хранятся в Git; расшифровка происходит в runtime при деплое.

### Установка:

```bash
helm plugin install https://github.com/jkroepke/helm-secrets
```

### Шифрование secrets.yaml:

```yaml
# secrets.yaml (до шифрования)
db:
  password: "super-secret-password"
api:
  key: "sk-live-abcdef123456"
```

```bash
# Зашифровать через AWS KMS
sops --kms arn:aws:kms:us-east-1:123456789:key/my-key-id \
  --encrypt secrets.yaml > secrets.enc.yaml

# Зашифровать через age (локально)
sops --age age1... --encrypt secrets.yaml > secrets.enc.yaml

# Проверить содержимое
sops -d secrets.enc.yaml
```

### Использование при деплое:

```bash
# Установка с зашифрованным файлом (расшифровывается автоматически)
helm secrets upgrade --install my-app ./my-chart \
  --values values.yaml \
  --values secrets.enc.yaml

# Альтернатива: сначала расшифровать, потом удалить
helm secrets dec secrets.enc.yaml   # Создаёт secrets.enc.yaml.dec
helm install my-app ./my-chart -f values.yaml -f secrets.enc.yaml.dec
rm secrets.enc.yaml.dec
```

### .sops.yaml для автоматического выбора ключа:

```yaml
# .sops.yaml в корне репозитория
creation_rules:
  - path_regex: ".*secrets.*\\.yaml$"
    kms: "arn:aws:kms:us-east-1:123456789:key/my-key-id"
  - path_regex: ".*dev.*secrets.*"
    age: "age1qqjqqzqq..."
```

**Альтернативы:** `External Secrets Operator` (ESO) + `Vault`/`AWS Secrets Manager` — secrets живут вне Git, что более безопасно для enterprise.

---

## Q41. Как совместно использовать Helm и Kustomize?

`Helm` и `Kustomize` решают похожие задачи, но по-разному. На практике их совмещают несколькими способами:

### Паттерн 1: Kustomize поверх Helm (helm post-renderer)

`Kustomize` применяется к манифестам, сгенерированным `Helm`. Используется когда нужно изменить chart, который нельзя трогать напрямую.

```bash
# kustomize-post-renderer.sh
#!/bin/bash
helm template "$@" | kustomize build -
```

```bash
helm install my-app ./my-chart \
  --post-renderer ./kustomize-post-renderer.sh
```

### Паттерн 2: Kustomize вызывает Helm через helmCharts

`Kustomize 4.1+` умеет генерировать манифесты из Helm chart:

```yaml
# kustomization.yaml
helmCharts:
  - name: my-app
    repo: https://charts.example.com
    version: 1.2.3
    releaseName: my-app
    namespace: production
    valuesFile: values-prod.yaml
    includeCRDs: true

patches:
  - target:
      kind: Deployment
      name: my-app
    patch: |
      - op: add
        path: /spec/template/spec/tolerations
        value:
          - key: "spot"
            operator: "Exists"
```

### Паттерн 3: ArgoCD с Helm + Kustomize через ignoreDifferences

ArgoCD поддерживает комбинированный источник (multi-source):

```yaml
spec:
  sources:
    - repoURL: https://charts.example.com
      chart: my-app
      targetRevision: 1.2.3
      helm:
        valueFiles: [values-base.yaml]
    - repoURL: https://git.example.com/overlays
      path: prod-overlay
      ref: kustomizeOverlay
```

**Когда что использовать:**
- Только Helm: если чарт полностью под контролем команды, нужна параметризация через values
- Helm + Kustomize: если используется сторонний chart + нужны patch-и без форка
- Только Kustomize: простые YAML-манифесты без сложной темплейтизации

---

## Q42. Как писать unit-тесты для Helm Charts с helm-unittest?

`helm-unittest` — фреймворк для тестирования Helm-чартов без деплоя в кластер. Тесты проверяют что шаблоны генерируют корректные манифесты при разных наборах values.

### Установка:

```bash
helm plugin install https://github.com/helm-unittest/helm-unittest
```

### Структура тест-файла:

```yaml
# tests/deployment_test.yaml
suite: "Deployment tests"
templates:
  - templates/deployment.yaml

tests:
  - it: "should have correct replicas by default"
    asserts:
      - equal:
          path: spec.replicas
          value: 1

  - it: "should override replicas via values"
    set:
      replicaCount: 3
    asserts:
      - equal:
          path: spec.replicas
          value: 3

  - it: "should set image tag from values"
    set:
      image.tag: "1.5.0"
    asserts:
      - equal:
          path: spec.template.spec.containers[0].image
          value: "my-registry/my-app:1.5.0"

  - it: "should have liveness probe configured"
    asserts:
      - isNotNull:
          path: spec.template.spec.containers[0].livenessProbe

  - it: "should not create HPA when disabled"
    templates:
      - templates/hpa.yaml
    set:
      autoscaling.enabled: false
    asserts:
      - hasDocuments:
          count: 0
```

### Snapshot-тесты:

```yaml
  - it: "should match snapshot"
    asserts:
      - matchSnapshot: {}  # Сравнить с сохранённым снапшотом
```

```bash
# Запустить тесты
helm unittest ./my-chart

# Обновить снапшоты
helm unittest ./my-chart --update-snapshot

# В CI
helm unittest ./my-chart --output-type JUnit --output-file test-results.xml
```

**Что тестировать:** имена ресурсов, labels, количество реплик, env-переменные, health probes, условные ресурсы (HPA, Ingress), RBAC-правила.

---

## Q43. Что такое Helm provenance и как подписывать чарты?

**Helm Provenance** — механизм криптографической подписи chart-пакетов для проверки целостности и подлинности. Использует `GPG`.

### Процесс подписания:

```bash
# 1. Создать GPG ключ
gpg --gen-key

# 2. Экспортировать публичный ключ для распространения
gpg --export --armor "My Helm Publisher" > pubkey.asc

# 3. Упаковать chart с подписью
helm package --sign \
  --key "My Helm Publisher" \
  --keyring ~/.gnupg/secring.gpg \
  ./my-chart

# Результат: my-chart-1.0.0.tgz + my-chart-1.0.0.tgz.prov
```

### Верификация при установке:

```bash
# Импортировать публичный ключ издателя
helm fetch bitnami/nginx --verify
gpg --import pubkey.asc

# Установить с верификацией
helm install my-app ./my-chart-1.0.0.tgz \
  --verify \
  --keyring ~/.gnupg/pubring.gpg

# Только верификация без установки
helm verify my-chart-1.0.0.tgz --keyring pubring.gpg
```

### Файл .prov:

```
-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA512

name: my-chart
description: My application chart
version: 1.0.0
...

files:
  my-chart-1.0.0.tgz: sha256:abc123...
-----BEGIN PGP SIGNATURE-----
...
-----END PGP SIGNATURE-----
```

### Современная альтернатива — cosign (Sigstore):

```bash
# Подписать OCI-чарт без управления GPG-ключами
cosign sign oci://registry.example.com/charts/my-chart:1.0.0

# Верифицировать
cosign verify oci://registry.example.com/charts/my-chart:1.0.0 \
  --certificate-identity-regexp ".*" \
  --certificate-oidc-issuer https://accounts.google.com
```

**Когда использовать:** в enterprise-окружениях с требованиями к supply chain security (SLSA Level 2+), при публикации публичных chart-ов, в regulated industries.

---

## See also

- [Kubernetes](kubernetes-interview.md)
- [Docker](docker-interview.md)
- [Terraform](terraform-interview.md)
- [Стратегии деплоя](../cicd/deployment-strategies-interview.md)
- [CI/CD пайплайны](../cicd/pipeline-design-interview.md)
- [Git](git-interview.md)
- [Ansible](ansible-interview.md)
- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
