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

**`Helm`** — пакетный менеджер для Kubernetes: то же, чем `apt`/`yum` являются для Linux или `npm` для Node.js, но для кластера. Без Helm типичное приложение — это десяток разрозненных YAML-файлов (Deployment, Service, Ingress, ConfigMap, HPA…), которые нужно вручную редактировать под каждое окружение и применять по одному. Helm превращает их в один параметризуемый, версионируемый пакет.

**Какие проблемы это снимает:**

- **Упаковка** — собирает множество разрозненных Kubernetes-манифестов в единый артефакт (`chart`), который ставится одной командой.
- **Параметризация** — одни и те же шаблоны под разные окружения настраиваются через `values.yaml`, без правки самих манифестов.
- **Версионирование** — каждый деплой создаёт новую `revision`, и полная история хранится прямо в кластере.
- **Откат** — `helm rollback` за секунды возвращает к любой прошлой ревизии, потому что Helm помнит, что именно было применено.
- **Управление зависимостями** — chart может тянуть за собой другие chart-ы (PostgreSQL, Redis и т.д.), как пакет тянет свои библиотеки.
- **Воспроизводимость** — один и тот же chart разворачивается идентично в dev/staging/prod, различаются только values.

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

**`Chart`** — пакет приложения для Kubernetes: директория (или `.tgz` архив) с фиксированной структурой, где лежат шаблоны манифестов и метаданные. По сути chart — это не сами манифесты, а «рецепт» того, как их собрать.

Ключевая идея — **разделение шаблона и данных**: шаблоны (`templates/`) описывают форму манифестов с «дырками», а `values.yaml` заполняет эти дырки конкретными значениями. При рендеринге Helm подставляет values в Go-шаблоны и получает финальные YAML-манифесты, которые и уходят в кластер.

**Жизненный цикл chart:**
1. Разработчик создаёт chart с шаблонами.
2. Chart публикуется в репозиторий (HTTP-репозиторий или OCI-реестр).
3. `helm install` рендерит шаблоны и применяет результат к кластеру → возникает `Release` (установленный экземпляр).
4. `helm upgrade` обновляет release и инкрементирует `revision`.
5. История каждой ревизии хранится прямо в кластере — как Kubernetes Secret в namespace релиза, поэтому откат не требует доступа к исходному chart.

---

## Q3. Чем Helm 3 отличается от Helm 2?

Коротко: **Helm 3 убрал серверный компонент `Tiller`** — это сделало Helm чисто клиентским инструментом и закрыло главную дыру в безопасности Helm 2. Всё остальное — следствия этого решения.

| Аспект | Helm 2 | Helm 3 |
|---|---|---|
| **Серверный компонент** | `Tiller` (pod в кластере) | Нет — только client |
| **Безопасность** | Tiller имел широкие права RBAC | Права ограничены правами пользователя |
| **Хранение state** | ConfigMap в `kube-system` | Secret в namespace релиза |
| **CRD** | Устанавливались вместе с chart | Отдельный lifecycle через `crds/` директорию |
| **Namespace scope** | Release глобальный | Release привязан к namespace |
| **Схема values** | Нет валидации | `values.schema.json` — JSON Schema валидация |
| **Команды** | `helm delete`, `helm fetch` | `helm uninstall`, `helm pull` |

**Почему `Tiller` был проблемой.** В Helm 2 клиент `helm` ничего не применял сам — он слал команды поду `Tiller` внутри кластера, а тот применял манифесты от своего имени. Чтобы Tiller мог ставить что угодно, ему давали права уровня cluster-admin, и любой, кто дотягивался до Tiller, фактически получал полный контроль над кластером в обход RBAC. Helm 3 выкинул этот промежуточный слой: теперь `helm` обращается к API-серверу напрямую, под учётной записью пользователя и в рамках его прав. Бонусом state переехал из ConfigMap в `kube-system` в Secret в namespace релиза — релизы перестали быть глобальными и привязались к namespace.

---

## Q4. Что такое Release в контексте Helm?

**`Release`** — это конкретный установленный экземпляр chart в кластере. Разница важна: **chart** — это шаблон («рецепт»), а **release** — то, что реально развёрнуто из этого рецепта с конкретными values. Один chart порождает столько releases, сколько раз вы его установили.

- Один chart можно установить много раз под разными именами — это будут независимые releases (например, два инстанса Redis в одном namespace).
- Каждый `helm upgrade` не перезаписывает release «на месте», а добавляет новую `revision` поверх предыдущих — отсюда и возможность отката.
- Метаданные каждой ревизии хранятся как Kubernetes `Secret` типа `helm.sh/release.v1` в namespace релиза. То есть источник правды о том, что развёрнуто, — сам кластер, а не локальная машина.
- Чтобы история не росла бесконечно, число хранимых ревизий ограничено флагом `--history-max` (по умолчанию 10).

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

**Как Helm читает эту структуру:**
- `templates/` — единственная директория, содержимое которой рендерится в манифесты и применяется к кластеру.
- Файлы в `templates/`, начинающиеся с `_` (например, `_helpers.tpl`), Helm НЕ превращает в манифесты — это библиотека вспомогательных named templates, которую подключают остальные шаблоны.
- `charts/` и `crds/` обрабатываются особо: subcharts из `charts/` ставятся как зависимости, а CRD из `crds/` применяются раньше всего остального и без шаблонизации (чтобы кластер «узнал» новые ресурсы до того, как chart начнёт их создавать).
- `.helmignore` работает как `.gitignore`, но для команды `helm package`: исключает мусор из итогового `.tgz`.

---

## Q6. (!) Что содержит файл Chart.yaml?

`Chart.yaml` — обязательный «паспорт» chart: его метаданные, версия и список зависимостей. Без него директория не считается chart-ом.

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

**Главное, что путают на собеседовании, — две версии:**
- `version` — версия **самого chart** (его шаблонов и логики). Её бампают, когда меняется устройство chart, и именно по ней Helm разрешает зависимости (SemVer).
- `appVersion` — версия **приложения внутри** (обычно совпадает с тегом образа). Чисто информационное поле, на логику Helm не влияет — это «что мы упаковали», тогда как `version` — «как мы это упаковали».
- `type: library` — chart без собственных манифестов, только набор named templates; такой chart нельзя установить, его лишь подключают как зависимость.

---

## Q7. Что такое файл values.yaml и как он используется?

`values.yaml` — набор значений **по умолчанию**, которыми заполняются шаблоны. Это «панель настроек» chart: всё, что пользователь должен иметь возможность поменять, выносится сюда, а в шаблонах подставляется через объект `.Values`. При установке эти дефолты можно переопределить (см. Q20), но без переопределения chart должен корректно ставиться как есть.

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

`_helpers.tpl` — место для **named templates**: переиспользуемых кусков шаблона, которые объявляются один раз и вызываются из разных манифестов. Это аналог функций — чтобы не дублировать одну и ту же логику генерации имён и labels по всем `*.yaml`. Имя файла начинается с `_`, поэтому Helm не рендерит из него отдельный манифест, а трактует как библиотеку.

Классический пример ниже: хелперы для имени chart, полного имени ресурса и общего набора labels.

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

`NOTES.txt` — шаблон сообщения, которое Helm печатает в консоль после успешного `install`/`upgrade`. Это «инструкция на выходе»: как достучаться до только что развёрнутого приложения. Файл шаблонизируется так же, как манифесты (имеет доступ к `.Values`, `.Release` и хелперам), поэтому подсказку можно подстроить под реально применённую конфигурацию — например, показать публичный URL, если включён Ingress, и команду `port-forward`, если нет.

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

Удобно держать в голове по группам — команды делятся ровно на то, с чем работают: **релизы** в кластере, **chart-ы** локально и **репозитории/реестры** как источники chart-ов.

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

**Что важно проговорить на собеседовании:**

- **`upgrade --install` идемпотентен.** Связка флага `--install` с `upgrade` означает «обнови, а если релиза ещё нет — поставь». Поэтому в CI/CD почти всегда пишут именно `helm upgrade --install`, а не `helm install` — пайплайн не падает на первом запуске и не требует знать, существует релиз или нет.
- **`--atomic` спасает от «полуобновлённого» состояния.** Без него неудачный upgrade оставляет release в статусе `failed` с частично применёнными изменениями. С `--atomic`, если Pod-ы не выходят в Ready за `--timeout`, Helm сам откатывается на предыдущую рабочую ревизию — кластер не зависает в промежуточном состоянии.
- **Откат не требует chart.** `helm rollback` восстанавливает ревизию из истории в кластере, поэтому работает, даже если исходный chart уже недоступен. Без аргумента ревизии откатывает на предыдущую.

---

## Q12. Как просмотреть сгенерированные манифесты перед установкой?

Есть два разных по смыслу способа, и на собеседовании ценят, что вы видите разницу:

- **`helm template`** рендерит шаблоны **локально**, не обращаясь к кластеру вообще. Быстро, работает офлайн, но не проверяет, примет ли результат API-сервер.
- **`helm install --dry-run`** отправляет отрендеренные манифесты в API-сервер на **валидацию** (но не применяет их). Медленнее и требует доступа к кластеру, зато ловит ошибки схемы, недопустимые поля, конфликты имён.

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

`helm diff` — это плагин (в стандартную поставку Helm не входит), который показывает, что именно изменится в кластере, если применить новый chart/values. По сути это `git diff` для уже развёрнутых манифестов: он рендерит предлагаемое состояние и сравнивает его с тем, что реально установлено в release, выводя только дельту.

Зачем это нужно: команды вроде `helm upgrade` ничего не показывают до применения — вы узнаёте об изменениях постфактум. `helm diff` даёт увидеть последствия заранее, поэтому его ставят перед `upgrade` в пайплайнах и обязательно — в GitOps, где diff попадает в ревью PR.

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

Главный сценарий — GitOps-пайплайны: diff вешают на Pull Request, чтобы ревьюеры видели реальные изменения в кластере (новые поля, удаляемые ресурсы, смену образа) ещё до мержа.

---

## Q14. (!) Какой синтаксис используется в Helm templates?

Helm рендерит шаблоны на движке **Go `text/template`**, расширенном библиотекой функций `Sprig`. Конструкции пишутся внутри `{{ }}`: всё, что снаружи, остаётся текстом как есть, а внутри — выполняется логика и подстановка значений. Это важно понимать: Helm работает с YAML как с обычным текстом, он не «понимает» структуру YAML, поэтому за корректность отступов отвечаете вы (отсюда `nindent` и аккуратность с `-`).

Минимальный набор, который надо знать наизусть:

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

**Про дефис `-` (whitespace chomping).** `{{-` срезает пробелы и переносы строк слева от тега, `-}}` — справа. Это не косметика: без него управляющие строки (`{{- if ... }}`) оставляют после себя пустые строки, и сгенерированный YAML ломается по отступам. Правило простое — у строк с `if`/`range`/`with`/`end` почти всегда ставят `{{-`, чтобы сама конструкция не попадала в вывод.

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

Все они — это «контекст рендеринга», доступный через корневую точку `.`. Делятся по источнику данных: `.Values` приходит из values-файлов и `--set`, `.Release` и `.Chart` — это рантайм-факты о текущей установке (имя, namespace, номер ревизии, версия chart). Частая ловушка: внутри `range`/`with` точка `.` меняет смысл на элемент цикла, и чтобы добраться до глобальных объектов, используют корневой контекст `$` (например, `$.Release.Name`).

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

Три рабочие лошадки шаблонов, и у каждой своя роль:

- **`if`** — условный вывод блока (плюс `else if`/`else`). Решает, попадёт ли кусок манифеста в результат.
- **`range`** — цикл по списку или map; генерирует повторяющиеся куски (env-переменные, тома, правила Ingress).
- **`with`** — сужает scope: внутри блока точка `.` указывает на переданный объект, поэтому не нужно каждый раз писать длинный путь. Бонус: `with` пропускает блок целиком, если значение пустое/`nil` — удобно для опциональных секций.

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

**Named template** — это «функция» в мире Helm-шаблонов: именованный блок, объявленный через `define`, который потом вызывают из разных мест. Их выносят в `_helpers.tpl`, чтобы не копипастить одну и ту же генерацию имён и labels. Вызывают named template двумя способами — `include` или `template`, и здесь кроется частый вопрос на собеседовании (см. Q18): **на практике почти всегда используют `include`**.

Причина — в том, что named template принимает **контекст** вторым аргументом (обычно `.`), и `include` возвращает результат как **строку**, к которой можно применить pipe-функции. Именно это позволяет писать `include "..." . | nindent 4` и корректно расставлять отступы при вставке блока в YAML.

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

Ключевой момент в коде выше: один и тот же хелпер вставлен с разным отступом (`nindent 4` и `nindent 8`) — это работает только потому, что `include` отдаёт строку, к которой применяется pipe. С `template` так не получилось бы: он печатает результат напрямую и значения не возвращает, поэтому форматировать его вывод нечем.

---

## Q18. В чём разница между include и template?

Обе вызывают один named template, но `template` — это встроенная конструкция Go-шаблонов, которая печатает результат прямо в поток, а `include` — добавленная Helm обёртка, которая тот же результат **возвращает строкой**. Отсюда вся разница:

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

**Рекомендация Helm: всегда использовать `include`.** Поскольку YAML критичен к отступам, а аккуратно вставить многострочный блок можно только через `include "..." . | nindent N`, `template` оставляют разве что для совсем простых однострочных подстановок — и то по привычке проще везде писать `include`.

---

## Q19. Какие встроенные функции Helm templates часто используются?

Функции применяются через pipe (`|`) — слева значение, справа функция, результат можно гонять по цепочке. Большинство приходит из библиотеки `Sprig`, несколько (`toYaml`, `include`, `tpl`) добавляет сам Helm. Сгруппировать стоит так: **форматирование строк** (`quote`, `trunc`), **сериализация структур** (`toYaml`, `toJson`), **подстановка дефолтов** (`default`) и **отступы** (`indent`/`nindent`). Самые ходовые из них:

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

Дефолты из `values.yaml` переопределяют двумя путями, и выбор зависит от объёма и происхождения данных:

- **Файлами (`-f` / `--values`)** — для крупных, постоянных наборов настроек окружения (отдельный файл на dev/staging/prod). Хранятся в git, версионируются, читаемы.
- **Флагами (`--set` и его варианты)** — для точечных, динамических значений, которые приходят из пайплайна (чаще всего `image.tag=$COMMIT_SHA`).

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

Когда одно и то же значение задано в нескольких местах, побеждает источник «ближе к команде запуска». Порядок от низшего приоритета к высшему:

1. `values.yaml` внутри chart — базовые значения по умолчанию.
2. `values.yaml` родительского chart — перекрывает дефолты subchart (родитель «знает лучше»).
3. Файлы `--values`/`-f` — в порядке передачи, причём **каждый следующий файл перекрывает предыдущий**.
4. `--set` / `--set-string` / `--set-json` — самый высокий приоритет, бьёт всё остальное.

**Мнемоника:** чем правее в командной строке и чем «ручнее» способ, тем выше приоритет. Отсюда практическое следствие — `--set image.tag` из CI всегда выиграет у того, что прописано в любом values-файле, поэтому им и прокидывают тег сборки.

---

## Q22. Что такое глобальные values (global)?

**Проблема, которую решает `global`.** Обычно values subchart изолированы: родитель прокидывает их в одноимённую секцию, и каждый subchart видит только своё. Но есть настройки, общие для всех (реестр образов, imagePullSecrets, storageClass), — дублировать их в секции каждого subchart неудобно и легко рассинхронизировать. Секция `global` в `values.yaml` родителя автоматически видна **всем** subcharts через `.Values.global` — это единственный «общий канал» сверху вниз без явной переадресации.

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

Запомнить главное: `global` — единственный механизм, передающий values от родителя ко всем потомкам сразу; для всего остального нужна явная передача через одноимённую секцию subchart.

---

## Q23. (!) Как управлять зависимостями между chart-ами?

Зависимости (subcharts) позволяют не упаковывать инфраструктуру руками, а переиспользовать готовые chart-ы: вместо того чтобы писать свой Deployment для PostgreSQL, вы объявляете его зависимостью, и Helm разворачивает базу вместе с вашим приложением. Объявляются они списком `dependencies` в `Chart.yaml`:

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

**Управление поведением зависимостей:**
- `condition` — ставить subchart только если флаг (`postgresql.enabled`) истинный. Так одну и ту же зависимость включают в dev и выключают в prod, где база внешняя.
- `tags` — групповой переключатель: можно включить/выключить сразу несколько зависимостей по общему тегу (например, `database`).
- `version` поддерживает диапазоны SemVer (`13.2.x`), а конкретные подтянутые версии фиксируются в `Chart.lock`.

После `helm dependency update` зависимые chart-ы скачиваются как `.tgz` в `charts/`, а их точные версии записываются в `Chart.lock` — аналог `package-lock.json`, который коммитят в git ради воспроизводимости сборки.

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

Дочерний chart не видит родительские values напрямую — родитель кладёт настройки для него в **секцию с именем зависимости**, и всё, что лежит под этим ключом, становится для subchart его собственным `.Values`. То есть `postgresql:` в родителе превращается в `.Values` внутри subchart `postgresql`:

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

Главное правило: **имя секции обязано совпадать с `name` зависимости** из `Chart.yaml` (или с её `alias`, если он задан) — иначе values просто не дойдут до subchart. Для настроек, общих сразу для всех subcharts, вместо одноимённых секций используют `global` (см. Q22).

---

## Q26. (!) Что такое Helm Hooks и зачем они нужны?

**`Helm Hooks`** позволяют вклиниться в жизненный цикл релиза и выполнить произвольный Kubernetes-ресурс (обычно `Job` или `Pod`) в строго определённый момент — до или после установки, обновления, отката, удаления.

**Зачем это нужно.** Обычные манифесты chart Helm применяет все разом, без гарантий порядка относительно «нешаблонных» действий. Но некоторые шаги должны идти строго в нужный момент: миграцию схемы БД обязательно прогнать **до** того, как поднимется новая версия приложения, иначе оно стартует на несовместимой схеме. Hook даёт эту точку синхронизации — Helm дождётся завершения hook-Job, прежде чем продолжить.

Типичные сценарии:
- Миграция БД **перед** обновлением приложения — `pre-upgrade`.
- Первичная инициализация данных при установке — `post-install`.
- Очистка ресурсов при удалении релиза — `pre-delete`.
- Прогрев кэша или smoke-проверка после деплоя — `post-upgrade`.

Hook — это обычный манифест, помеченный специальной аннотацией. Helm видит аннотацию `helm.sh/hook` и исключает ресурс из «обычного» применения, запуская его отдельно в указанной фазе:

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

Закономерность очевидна: фазы парные (`pre-`/`post-`) и привязаны к командам (`install`/`upgrade`/`rollback`/`delete`), плюс особняком стоит `test` — он не часть деплоя, а запускается вручную через `helm test`.

Один ресурс часто вешают сразу на несколько фаз через запятую — `"helm.sh/hook": pre-install,pre-upgrade`. Это удобно для миграций БД: один и тот же Job должен отработать и при первой установке, и при каждом обновлении.

---

## Q28. Что такое hook-weight и hook-delete-policy?

Это две аннотации, которые отвечают за **порядок** и **уборку** хуков — без них на одной фазе всё запустится в неопределённом порядке, а отработавшие Job-ы будут копиться в namespace.

**`hook-weight`** задаёт порядок выполнения хуков **одной фазы**: Helm сортирует их по весу и идёт от меньшего к большему (числа целые, допустимы отрицательные). Например, на `pre-upgrade` сначала должен отработать backup БД, потом миграция — backup помечают меньшим весом.

```yaml
annotations:
  "helm.sh/hook": pre-upgrade
  "helm.sh/hook-weight": "-10"    # Выполнится раньше хука с весом 0
```

**`hook-delete-policy`** решает, **когда подчищать** hook-ресурс. Это важно, потому что `Job` с одинаковым именем нельзя создать дважды — без правильной политики повторный деплой упадёт с конфликтом имени, либо namespace зарастёт завершёнными Job-ами.

| Политика | Поведение |
|---|---|
| `before-hook-creation` | Удалить старый hook-ресурс перед созданием нового (default) |
| `hook-succeeded` | Удалить если hook завершился успешно |
| `hook-failed` | Удалить если hook завершился с ошибкой |

```yaml
annotations:
  "helm.sh/hook-delete-policy": before-hook-creation,hook-succeeded
```

**Практичная комбинация** (как в примере) — `before-hook-creation,hook-succeeded`: старый Job сносится перед запуском нового (нет конфликта имён), а удачно завершившийся убирается сам, но при падении остаётся в кластере — чтобы можно было посмотреть его логи и понять причину.

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

Под капотом классический Helm-репозиторий — это просто HTTP-сервер (или статичный бакет) с файлом `index.yaml` в корне. `index.yaml` — это каталог: он перечисляет все доступные chart-ы, их версии и URL архивов. Поэтому `helm repo update` не качает chart-ы, а лишь обновляет этот локальный индекс, по которому затем работают `search` и `install`.

---

## Q30. (!) Что такое OCI Registry для Helm?

Начиная с Helm 3.8, chart-ы можно хранить не только в HTTP-репозитории, но и в `OCI`-реестре (Open Container Initiative) — том же, где лежат Docker-образы. Стандарт OCI достаточно общий, чтобы хранить любые артефакты, и Helm-chart упаковывается в него как ещё один тип артефакта. Смысл — перестать держать отдельную инфраструктуру под chart-ы: один реестр (Harbor, ECR, GHCR, Nexus) обслуживает и образы, и chart-ы, с единой аутентификацией и RBAC.

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

**Ключевое отличие от HTTP-репозитория:** у OCI нет `index.yaml`. Вместо общего каталога каждый chart адресуется напрямую по URL и тегу версии (`oci://.../my-chart` + `--version`), поэтому `helm repo add` не нужен — указываете полный ref и сразу ставите. Минус той же медали — нет глобального поиска по реестру: чтобы поставить chart, надо заранее знать его путь и версию.

**Преимущества OCI registry:**
- Единый реестр для Docker-образов и Helm-chart-ов — меньше инфраструктуры на поддержке.
- Нативная аутентификация и RBAC того же реестра, что и для образов.
- Надёжное хранение и версионирование через content-addressable digest.

---

## Q31. (!) В чём разница между Application chart и Library chart?

| Тип | `type:` в Chart.yaml | Содержит манифесты | Можно устанавливать |
|---|---|---|---|
| **Application** | `application` (default) | Да | Да |
| **Library** | `library` | Нет | Нет (только как dependency) |

Разница по сути одна — **есть ли у chart собственные манифесты**. Application chart — это то, что вы реально разворачиваете (приложение со своими Deployment, Service и т.д.). Library chart манифестов не содержит вовсе: это набор named templates, который сам по себе бесполезен, но подключается к другим chart-ам как зависимость и даёт им готовые хелперы. Аналогия: application chart — это исполняемая программа, library chart — библиотека-зависимость, которую нельзя «запустить».

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

**Зачем нужны library charts:** ради DRY. Когда в организации десятки приложений с одинаковыми блоками (labels, tolerations, affinity, securityContext), их выносят в один library chart — и все application-chart-ы тянут эти шаблоны как зависимость. Поправили хелпер в библиотеке — обновились все потребители.

---

## Q32. (!) Как тестировать Helm Charts?

Тестирование chart выстраивают по уровням — от самого дешёвого и быстрого к самому близкому к реальности. Идёте сверху вниз: каждый следующий уровень дороже, но ловит то, что не видит предыдущий.

**1. `helm lint`** — статический анализ структуры и синтаксиса, без рендеринга в кластер. Самый быстрый фидбэк, ставят на каждый commit:
```bash
helm lint ./my-chart --values test-values.yaml --strict
```

**2. `helm template` + `kubectl --dry-run`** — рендерим манифесты и прогоняем их через валидацию API-сервера. Ловит то, что lint не видит: некорректные поля, ошибки схемы Kubernetes:
```bash
helm template my-app ./my-chart --values test-values.yaml | kubectl apply --dry-run=client -f -
```

**3. `helm test`** — функциональная проверка **уже установленного** релиза. Это test-хуки (Pod с аннотацией `helm.sh/hook: test`), которые реально дёргают развёрнутое приложение — например, стучатся в его health-эндпоинт:
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

**4. `ct` (chart-testing)** — обёртка для CI/CD, которая связывает всё вместе:
```bash
# Lint и test изменённых chart-ов
ct lint --target-branch main
ct install --target-branch main
```

`ct` сам определяет, какие chart-ы изменились относительно целевой ветки (чтобы не гонять весь репозиторий), поднимает их в эфемерном кластере Kind/Minikube и прогоняет `helm test` против реально установленного релиза. Это и есть полноценный e2e-уровень в пайплайне.

---

## Q33. Что такое helm lint и когда его применять?

`helm lint` — это статический анализатор chart: он проверяет «форму» (структуру, метаданные, синтаксис шаблонов) без обращения к кластеру, поэтому работает мгновенно и офлайн. Его задача — отловить грубые ошибки до того, как chart дойдёт до `install`.

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

**Что не проверяет (и за чем нужны другие инструменты):**
- Семантику Kubernetes API — примет ли кластер манифест (это `kubectl --dry-run`/`kubeval`).
- Корректность бизнес-значений values — для этого описывают `values.schema.json`.

**Где применять:** первой ступенью в CI — на каждый commit / Pull Request, ещё до любых `template` и `upgrade`. Дёшево, быстро, отсекает очевидные поломки.

---

## Q34. (!) Как управлять секретами в Helm?

**В чём проблема.** У Helm нет встроенного шифрования секретов, и сразу две ловушки. Первая: `values.yaml` лежит в git открытым текстом — положить туда пароль значит закоммитить его в историю. Вторая, менее очевидная: даже Kubernetes Secret, отрендеренный chart-ом, хранится в кластере как **base64, а не шифрование** — это лишь кодировка, любой с доступом к API расшифрует её тривиально. Поэтому секреты решают внешними инструментами, и подходы делятся на два класса: **зашифровать секрет и положить рядом** либо **вообще не держать секрет в git, а подтягивать извне**.

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

**Как выбрать:**
- Подходы 1–2 (`helm-secrets`/SOPS, Sealed Secrets) держат **зашифрованный** секрет прямо в git — просто и GitOps-дружелюбно, ключ шифрования при этом снаружи.
- Подходы 3–4 (External Secrets Operator, Vault) **вообще не держат секрет в git** — он живёт в Vault/Secrets Manager, а в кластер попадает в рантайме. Безопаснее и предпочтительнее для enterprise, но требует отдельной инфраструктуры хранения секретов.

**Главное правило:** никогда не коммитить plaintext-секреты. Базовый выбор — `helm-secrets` + SOPS для небольших команд, External Secrets Operator — когда уже есть централизованное хранилище секретов.

---

## Q35. (!) Как Helm интегрируется с ArgoCD и GitOps?

**`ArgoCD`** — GitOps-контроллер, который непрерывно сверяет состояние кластера с тем, что описано в git, и подтягивает кластер к git-состоянию. Helm-chart для него — один из поддерживаемых типов источника.

**Важный нюанс реализации:** ArgoCD НЕ запускает `helm install`/`upgrade` и не создаёт Helm-релизы. Он вызывает только `helm template` (рендерит манифесты), а применяет их сам и сам же отслеживает diff. Практическое следствие — в кластере не будет helm-release secrets, и `helm rollback` тут не работает: откат делается через revert коммита в git, после чего ArgoCD синхронизирует кластер обратно.

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

Хорошие практики сводятся к четырём целям: chart должен быть **предсказуемым** (имена не конфликтуют), **гибким** (настраивается через values), **поддерживаемым** (без копипасты) и **безопасным**. Ниже — конкретика по каждому направлению.

**Именование** (чтобы ресурсы разных релизов не конфликтовали):
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

Начиная с `Helm 3.8` (stable) чарты можно хранить в **OCI-совместимых реестрах** (Docker Hub, GitHub Container Registry, AWS ECR, Harbor, Nexus) как OCI-артефакты — тот же реестр обслуживает и Docker-образы, и chart-ы. (Концепция разбиралась в Q30; здесь — полный набор команд для повседневной работы и точки, на которых спотыкаются.)

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

### Чем это отличается от классических HTTP-репозиториев:

- **Нет `index.yaml`** — нет общего каталога, каждый чарт адресуется напрямую по ref и тегу версии. Минус: пропадает глобальный поиск по реестру.
- **Аутентификация переиспользуется** — тот же `docker login`/credentials, что и для образов, отдельной системы доступов заводить не нужно.
- **Подпись через `cosign`** (Sigstore) — современный supply-chain-механизм вместо `.prov`-файлов классического Helm.
- **ArgoCD** умеет тянуть OCI-чарты начиная с версии 2.6 — учитывайте версию контроллера, если строите GitOps на OCI.

---

## Q38. Chart Museum vs OCI Registry — в чём разница подходов?

Это два способа хранить chart-ы, и выбор сводится к одному вопросу: **держать отдельный сервис под chart-ы или переиспользовать уже имеющийся container-registry**. `Chart Museum` — классический HTTP-репозиторий с `index.yaml` как отдельно стоящий сервис; `OCI Registry` — тот же реестр, где лежат образы.

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

`ArgoCD` — GitOps-контроллер, который синхронизирует состояние кластера с Git; Helm-чарт — один из поддерживаемых типов source. Базовый кирпич — объект `Application` (один chart → один namespace). Когда тот же chart нужно раскатать на много окружений или кластеров, на помощь приходит `ApplicationSet` — генератор, который штампует `Application` по шаблону, чтобы не плодить почти одинаковые манифесты руками.

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

### ApplicationSet — деплой на несколько окружений из одного шаблона:

Здесь `generators` задаёт список окружений (dev/staging/prod), а `template` — форму `Application` с подстановками `{{env}}`, `{{namespace}}`, `{{valuesFile}}`. ApplicationSet разворачивает список в три полноценных `Application`, каждый со своим values-файлом:

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

`helm-secrets` — плагин, который скрещивает Helm с `SOPS` (Mozilla). Идея: секретный values-файл лежит в git **зашифрованным**, а плагин прозрачно расшифровывает его в момент деплоя и передаёт Helm как обычный `-f`. Так секрет остаётся под версионным контролем и в одном PR с остальной конфигурацией, но в открытом виде нигде не хранится. Сам ключ шифрования (KMS, age, PGP) — снаружи git, и без него зашифрованный файл бесполезен.

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

**Где проходит граница с альтернативами.** `helm-secrets`/SOPS держит зашифрованный секрет **в git** — просто и удобно, но git остаётся точкой хранения. `External Secrets Operator` (ESO) + `Vault`/`AWS Secrets Manager` идут дальше: секрет вообще не попадает в git, он живёт в централизованном хранилище и подтягивается в кластер в рантайме. Для enterprise это безопаснее (ротация, аудит, единая точка доступа), ценой отдельной инфраструктуры.

---

## Q41. Как совместно использовать Helm и Kustomize?

Они решают одну задачу — получить конечные манифесты — но разными парадигмами: Helm **шаблонизирует** (подставляет values в «дырки»), Kustomize **патчит** (берёт готовый YAML и накладывает оверлеи без шаблонов). Совмещают их обычно в одном случае: есть **сторонний chart**, который менять нельзя (форкать не хочется), а подкрутить пару полей надо. Тогда Helm генерирует базу, а Kustomize точечно её правит. Способов стыковки три.

### Паттерн 1: Kustomize поверх Helm (helm post-renderer)

`Kustomize` применяется к манифестам, которые уже отрендерил `Helm`. Это и есть классический ответ на «как пропатчить чужой chart, не трогая его»: Helm отдаёт YAML, post-renderer прогоняет его через `kustomize build` перед применением.

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

**Как выбрать:**
- **Только Helm** — chart полностью ваш, и всё, что нужно настраивать, удобно вынести в values.
- **Helm + Kustomize** — берёте сторонний chart и нужны точечные правки, которых не предусмотрели его values; патч через Kustomize избавляет от форка.
- **Только Kustomize** — манифесты простые, без условной логики и циклов; шаблонизатор Helm тут лишний overhead.

---

## Q42. Как писать unit-тесты для Helm Charts с helm-unittest?

`helm-unittest` — фреймворк для unit-тестов chart **без кластера**: он рендерит шаблоны с заданными values и проверяет утверждения о результате прямо в YAML. Чем это отличается от `helm test` (Q32): `helm test` гоняет уже установленный релиз в живом кластере (это интеграционный уровень), а `helm-unittest` проверяет саму логику шаблонизации офлайн и за миллисекунды. Главная ценность — поймать регрессию в условной логике chart («при `autoscaling.enabled: false` HPA не должен создаваться») ещё до деплоя.

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

Снапшот фиксирует целиком отрендеренный манифест в файл; на следующих прогонах тест падает при любом расхождении. Полезно как «страховка от случайных изменений»: не описываете каждое поле, а ловите факт, что вывод поменялся — и осознанно подтверждаете новый снапшот.

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

**Helm Provenance** отвечает на два вопроса supply-chain-безопасности: **не подменили ли chart** (целостность) и **тот ли это издатель** (подлинность). Механизм — криптографическая подпись пакета на `GPG`: при упаковке рядом с `.tgz` кладётся файл `.prov` с контрольной суммой архива и PGP-подписью, а при установке Helm пересчитывает хеш и сверяет подпись с публичным ключом издателя. Если архив подменили или подписал не тот ключ — установка с `--verify` падает.

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

Для OCI-чартов классический GPG-механизм уступает место `cosign`: он избавляет от ручного управления долгоживущими ключами (keyless-подпись через OIDC-identity), а сама подпись хранится в том же реестре рядом с артефактом. Это де-факто стандарт для OCI-цепочки поставок.

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
