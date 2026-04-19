# Local-first упрощение — план имплементации

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Привести проект `cheat-sheet` в local-only состояние: убрать Docker/prod/Postgres-слой и security-обвязку, добавить рабочий режим без AI-ключей (flashcard) и UI-кнопку сброса банка вариантов. Приложение должно стартовать одной командой `./gradlew bootRun` без env-переменных.

**Architecture:** Точечная чистка существующего Spring Boot-приложения. Удаляем production-артефакты и security-пакет, добавляем computed-флаг `AppProperties.isAiEnabled()`, прокидываем его в Thymeleaf-модель, скрываем AI-зависимые элементы UI под `th:if`. Кнопка сброса — тонкая обёртка над уже существующим endpoint-ом `/api/admin/options/clear` без токена.

**Tech Stack:** Java 17, Spring Boot (MVC + JDBC + Thymeleaf), SQLite + Flyway, spring-ai-openai, Gradle (Kotlin DSL), JUnit 5 + AssertJ + ArchUnit.

**Spec:** `docs/superpowers/specs/2026-04-19-local-first-simplification-design.md`

**Предусловия для исполнителя:**
- Рабочий `git` в репозитории `/Users/sergeyvoronin/IdeaProjects/cheat-sheet`.
- Изменения в рабочей копии (git status от начала сессии) могут остаться нестейджед — **не делай `git add -A`**, коммить только файлы, перечисленные в конкретной задаче.
- JDK 17+ установлен, `./gradlew --version` работает.
- Ветка для работы: либо текущая (`master`), либо отдельная feature-ветка (исполнителю на усмотрение).

**Правило коммитов:** каждая задача заканчивается отдельным коммитом с указанным сообщением. Между задачами — зелёный билд (`./gradlew build -x test`).

---

## Task 0: Baseline и подготовка

**Files:**
- No file changes. Только проверка исходного состояния.

- [ ] **Step 1: Проверить, что репозиторий в рабочем состоянии**

```bash
cd /Users/sergeyvoronin/IdeaProjects/cheat-sheet
git status --short | head -5
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`. Если fail — остановись и разберись ДО начала чисток.

- [ ] **Step 2: Зафиксировать baseline-коммит (или убедиться, что последний коммит содержит spec)**

```bash
git log -1 --format="%h %s"
```

Expected: последний коммит `b8c7d3f docs: spec для этапа D — local-first упрощение` (или свежее). Baseline готов.

- [ ] **Step 3: Прогнать существующие тесты как baseline**

```bash
./gradlew test
```

Запомни результат. Если есть упавшие тесты — запиши их в блокнот, чтобы после чистки отличать «ломали мы» vs «уже было сломано». Если `BUILD SUCCESSFUL` — идеально, все тесты должны оставаться зелёными после каждой задачи.

---

## Task 1: Удаление Docker-артефактов

**Files:**
- Delete: `Dockerfile`
- Delete: `docker-compose.yml`
- Delete: `.dockerignore`

- [ ] **Step 1: Удалить файлы через `git rm`**

```bash
git rm Dockerfile docker-compose.yml .dockerignore
```

- [ ] **Step 2: Убедиться, что нигде в коде нет ссылок на Docker**

```bash
grep -r "Dockerfile\|docker-compose\|docker\.io\|container" \
    --include="*.java" --include="*.kts" --include="*.yml" --include="*.md" \
    modules/ README.md CLAUDE.md | grep -v "^docs/superpowers/"
```

Expected: выводы только в `README.md` и `CLAUDE.md` (они будут почищены в Task 7). В коде (`.java`, `.kts`, `.yml`) — пусто.

- [ ] **Step 3: Верификация билда**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git commit -m "$(cat <<'EOF'
chore(docker): удалить Docker-артефакты (local-only проект)

Проект запускается только локально через ./gradlew bootRun,
Dockerfile и docker-compose.yml больше не нужны.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 1.5: Удаление двусторонней синхронизации избранного в markdown

**Files:**
- Delete: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/MarkdownFavoriteService.java`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/progress/FavoriteService.java`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/package-info.java` (убрать упоминание в Javadoc)
- Modify: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/FavoriteServiceTest.java` (упростить)

- [ ] **Step 1: Удалить `MarkdownFavoriteService.java`**

```bash
git rm modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/MarkdownFavoriteService.java
```

- [ ] **Step 2: Упростить `FavoriteService.java`**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/progress/FavoriteService.java`. Заменить содержимое целиком на:

```java
package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис переключения состояния «избранное» для вопроса.
 * Флаг {@code important} хранится только в БД — markdown-файлы с вопросами read-only.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {
    QuestionRepository questionRepository;

    /**
     * Переключает флаг избранного для вопроса в БД.
     *
     * @param questionId идентификатор вопроса
     * @return результат операции
     */
    @Transactional
    public FavoriteResult toggleFavorite(long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
        boolean newFavorite = !question.important();
        questionRepository.updateImportant(questionId, newFavorite);
        return new FavoriteResult(questionId, newFavorite);
    }

    /**
     * Результат переключения избранного.
     *
     * @param questionId идентификатор вопроса
     * @param favorite новое состояние «избранное»
     */
    @Builder(toBuilder = true)
    public record FavoriteResult(long questionId, boolean favorite) {
    }
}
```

Изменения относительно исходника:
- Убран импорт и поле `MarkdownFavoriteService`.
- Убран вызов `markdownFavoriteService.toggleInFile(...)`.
- Из record `FavoriteResult` убрано поле `synced`.

- [ ] **Step 3: Проверить потребителей `FavoriteResult.synced`**

```bash
grep -rn "\.synced()\|\"synced\"\|isSynced\|synced:" modules/ src/ 2>/dev/null
```

Expected: либо пусто, либо ссылки только в тестах/DTO, которые чинятся следом. Если нашёлся JS/Thymeleaf-код, читающий `synced` из API-ответа — либо убрать оттуда, либо оставить в DTO `synced = true` как заглушку (проще: убрать поле везде).

- [ ] **Step 4: Упростить `FavoriteServiceTest.java`**

Открыть `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/FavoriteServiceTest.java`. Удалить:
- импорт `MarkdownFavoriteService`
- поле `@Mock MarkdownFavoriteService markdownFavoriteService`
- передачу `markdownFavoriteService` в конструктор `FavoriteService`
- все `when(markdownFavoriteService.toggleInFile(...))`
- все `verify(markdownFavoriteService)...`
- все проверки `assertThat(result.synced())...`

Оставить только проверки: `updateImportant` вызван с правильным флагом, `FavoriteResult` содержит правильный `questionId` и `favorite`.

- [ ] **Step 5: Подправить `package-info.java`**

```bash
grep -n "MarkdownFavoriteService" modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/package-info.java
```

Если упоминание есть — открыть файл и удалить строку/блок с `@see MarkdownFavoriteService`.

- [ ] **Step 6: Убедиться, что компиляция чистая**

```bash
./gradlew :quiz-app:build -x test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Прогнать тесты**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 8: Commit**

```bash
git commit -m "$(cat <<'EOF'
refactor(favorites): избранное живёт только в БД

Удалён MarkdownFavoriteService — больше не пишем маркер (!)
в исходные markdown-файлы при переключении избранного.

Источник истины — таблица questions, поле important.
Markdown-файлы в cheatsheets/interview/ теперь read-only:
их задача — инициализировать БД при первом импорте.

FavoriteResult.synced удалён как более неактуальный.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 2: Удаление Postgres-профиля и зависимостей

**Files:**
- Delete: `modules/quiz-app/src/main/resources/application-postgres.yml`
- Delete: `modules/quiz-persistence/src/main/resources/db/migration-postgres/` (весь каталог)
- Modify: `modules/quiz-app/build.gradle.kts` — убрать postgres/testcontainers/flyway-postgres зависимости
- Delete: тесты с Testcontainers+Postgres (`PostgresIntegrationTest` и всё, что его использует)

- [ ] **Step 1: Найти и удалить postgres-integration тесты**

```bash
grep -rn "PostgresIntegrationTest\|@Testcontainers\|PostgreSQLContainer" \
    modules/ --include="*.java" -l
```

Expected: список файлов. Все они удаляются:

```bash
grep -rn "PostgresIntegrationTest\|@Testcontainers\|PostgreSQLContainer" \
    modules/ --include="*.java" -l | xargs git rm
```

- [ ] **Step 2: Удалить postgres-profile yml и миграции**

```bash
git rm modules/quiz-app/src/main/resources/application-postgres.yml
git rm -r modules/quiz-persistence/src/main/resources/db/migration-postgres/
```

- [ ] **Step 3: Почистить `modules/quiz-app/build.gradle.kts`**

Открыть файл. Удалить следующие строки:

```kotlin
    implementation(libs.flyway.database.postgresql)
```

```kotlin
    runtimeOnly(libs.postgresql)
```

```kotlin
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
```

- [ ] **Step 4: Проверить, что в `modules/quiz-persistence/build.gradle.kts` тоже нет postgres/testcontainers (если есть — удалить аналогично)**

```bash
grep -E "postgresql|testcontainers" modules/quiz-persistence/build.gradle.kts || echo "OK, чисто"
```

Если найдены — удалить те же зависимости в том же файле.

- [ ] **Step 5: Верификация**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`.

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL` (все оставшиеся тесты зелёные; если что-то упало — смотри, не зависит ли тест от postgres, и удали его тоже).

- [ ] **Step 6: Commit**

```bash
git commit -m "$(cat <<'EOF'
chore(db): удалить поддержку PostgreSQL (SQLite единственная БД)

SQLite — единственный поддерживаемый движок для local-only-проекта.
Убраны:
- application-postgres.yml + db/migration-postgres/
- postgres JDBC-драйвер и flyway-database-postgresql
- Testcontainers-зависимости и интеграционные тесты с PostgreSQL

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 3: Удаление production-профиля

**Files:**
- Delete: `modules/quiz-app/src/main/resources/application-prod.yml`

- [ ] **Step 1: Найти упоминания prod-профиля в коде**

```bash
grep -rn "\"prod\"\.equalsIgnoreCase\|\"prod\"\.equals\|SPRING_PROFILES_ACTIVE.*prod\|Profile(\"prod\")" \
    modules/ --include="*.java"
```

Ожидаемые находки: `SensitiveEndpointAccessService.validateProductionTokenConfiguration()` — этот класс удаляется в Task 4, не трогай сейчас, просто зафиксируй что знаешь про ссылку.

- [ ] **Step 2: Удалить файл**

```bash
git rm modules/quiz-app/src/main/resources/application-prod.yml
```

- [ ] **Step 3: Верификация**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git commit -m "$(cat <<'EOF'
chore(config): удалить application-prod.yml (local-only проект)

Production-профиля больше нет — все конфиги в базовом application.yml.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 4: Удаление security-слоя

**Files:**
- Delete: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/security/` (весь пакет)
  - `SensitiveEndpointAccessService.java`
  - `RequestRateLimiter.java`
  - `SensitiveEndpointAuthenticationEntryPoint.java`
  - `SensitiveEndpointAccessDeniedHandler.java`
- Delete: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/SecurityConfig.java`
- Delete: тесты этих классов (найти grep-ом в следующем шаге)
- Modify: `modules/quiz-app/build.gradle.kts` — убрать `spring-boot-starter-security` + `spring-security-test`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/admin/controller/AdminController.java` — убрать `@RequestHeader X-Admin-Token` и передачу токена
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/admin/usecase/AdminApiService.java` — убрать параметр `token` и `forbiddenIfUnauthorized` из всех методов
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/export/controller/ExportController.java` — аналогично AdminController
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/export/usecase/ExportApiService.java` — аналогично AdminApiService
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/admin/usecase/RegenerateEndpointService.java` — убрать rate-limit и проверку токена
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java` — убрать всё, что связано с regenerate rate limit и токеном

- [ ] **Step 1: Найти все тесты, которые тестируют security-классы**

```bash
grep -rn "SensitiveEndpointAccessService\|RequestRateLimiter\|X-Admin-Token\|SecurityConfig\|spring-security-test\|@WithMockUser\|@WithAnonymousUser" \
    modules/quiz-app/src/test --include="*.java" -l
```

Запомни список. В последнем шаге задачи — либо удалить тесты целиком (если они purely про security), либо переписать (если в них смешана другая логика).

- [ ] **Step 2: Удалить пакет `api/security/` целиком**

```bash
git rm -r modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/security/
```

- [ ] **Step 3: Удалить `SecurityConfig.java`**

```bash
git rm modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/SecurityConfig.java
```

- [ ] **Step 4: Почистить `modules/quiz-app/build.gradle.kts` от security-зависимостей**

Удалить строки:

```kotlin
    implementation(libs.spring.boot.starter.security)
```

```kotlin
    testImplementation(libs.spring.security.test)
```

- [ ] **Step 5: Починить `AdminController.java`**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/admin/controller/AdminController.java`. Заменить целиком на:

```java
package com.cheatsheet.quiz.feature.admin.controller;

import com.cheatsheet.quiz.feature.admin.dto.response.AdminOperationResult;
import com.cheatsheet.quiz.feature.admin.dto.response.AdminResetAllResult;
import com.cheatsheet.quiz.feature.admin.usecase.AdminApiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST-эндпоинты для административных операций.
 *
 * <p>На local-only проекте авторизация не требуется — приложение слушает только localhost.</p>
 */
@RestController
@RequestMapping("/api/admin")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminApiService adminApiService;

    @PostMapping("/options/clear")
    public ResponseEntity<?> clearOptions() {
        return toResponse(adminApiService.clearOptions());
    }

    @GetMapping("/senior-rules")
    public ResponseEntity<?> getSeniorRulePriorities() {
        return toResponse(adminApiService.getSeniorRulePriorities());
    }

    @GetMapping("/senior-rules/help")
    public ResponseEntity<?> getSeniorRulesHelp() {
        return toResponse(adminApiService.getSeniorRulesHelp());
    }

    @GetMapping("/senior-rules/keys")
    public ResponseEntity<?> getSeniorRuleKeys(
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        return toResponse(adminApiService.getSeniorRuleKeys(prefix, query));
    }

    @GetMapping("/senior-rules/catalog")
    public ResponseEntity<?> getSeniorRuleCatalog(
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        return toResponse(adminApiService.getSeniorRuleCatalog(prefix, query));
    }

    @PutMapping("/senior-rules")
    public ResponseEntity<?> updateSeniorRulePriorities(@RequestBody(required = false) Map<String, Integer> overrides) {
        return toResponse(adminApiService.updateSeniorRulePriorities(overrides));
    }

    @PatchMapping("/senior-rules")
    public ResponseEntity<?> patchSeniorRulePriorities(@RequestBody(required = false) Map<String, Integer> updates) {
        return toResponse(adminApiService.patchSeniorRulePriorities(updates));
    }

    @DeleteMapping("/senior-rules/{key}")
    public ResponseEntity<?> deleteSeniorRulePriority(@PathVariable("key") String key) {
        return toResponse(adminApiService.deleteSeniorRulePriority(key));
    }

    @PostMapping("/reset-all")
    public ResponseEntity<?> resetAll() {
        return toResponse(adminApiService.resetAll());
    }

    private ResponseEntity<?> toResponse(AdminApiService.AdminApiResult result) {
        if (result.status().is2xxSuccessful()) {
            Object body = result.body();
            if (body instanceof AdminOperationResult || body instanceof AdminResetAllResult) {
                log.info("admin_endpoint_success status={} bodyType={}", result.status().value(), body.getClass().getSimpleName());
            }
        }
        return ResponseEntity.status(result.status()).body(result.body());
    }
}
```

- [ ] **Step 6: Починить `AdminApiService.java`**

В каждом публичном методе (`clearOptions`, `getSeniorRulePriorities`, ...) удалить параметр `String token` и блок:

```java
AdminApiResult forbidden = forbiddenIfUnauthorized(token, "...");
if (forbidden != null) {
    return forbidden;
}
```

Удалить приватный метод `forbiddenIfUnauthorized` и поле `SensitiveEndpointAccessService accessService`. Удалить импорт `SensitiveEndpointAccessService`.

Константы `SENIOR_RULES_HELP_EXAMPLES` почистить: убрать упоминания `X-Admin-Token` из примеров curl. Новый вариант:

```java
private static final Map<String, String> SENIOR_RULES_HELP_EXAMPLES = Map.of(
        "get", "curl http://localhost:8080/api/admin/senior-rules",
        "put", "curl -X PUT -H \"Content-Type: application/json\" -d '{\"spring-transactional\":8}' http://localhost:8080/api/admin/senior-rules",
        "patch", "curl -X PATCH -H \"Content-Type: application/json\" -d '{\"spring-transactional\":9,\"sql-null-semantics\":null}' http://localhost:8080/api/admin/senior-rules",
        "delete", "curl -X DELETE http://localhost:8080/api/admin/senior-rules/spring-transactional"
);
```

- [ ] **Step 7: Починить `ExportController.java` и `ExportApiService.java`**

Аналогично: открыть файлы, удалить `@RequestHeader X-Admin-Token` из контроллера, удалить параметр `token` и проверки из сервиса, удалить импорты `SensitiveEndpointAccessService`.

Если есть сомнения что удалять — ориентируйся на паттерн: любой импорт/поле/параметр/вызов, связанный с `SensitiveEndpointAccessService`, `X-Admin-Token`, `accessService.isAuthorized`, `accessService.forbiddenIfUnauthorized` — удалить.

- [ ] **Step 8: Починить `RegenerateEndpointService.java` и `InterviewApiController.java`**

Открыть `RegenerateEndpointService.java` — удалить всё, что связано с `allowRegenerate`, `regenerateRetryAfterSeconds`, rate-limit. Сервис становится thin-wrapper'ом над regen-логикой без дросселя.

Открыть `InterviewApiController.java` — найти метод, работающий с regenerate. Удалить проверку rate-limit, удалить заголовки `Retry-After`, упростить до прямого вызова сервиса.

```bash
grep -n "allowRegenerate\|regenerateRetryAfterSeconds\|X-Admin-Token\|SensitiveEndpointAccessService" \
    modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java
```

Expected after fix: пусто.

- [ ] **Step 9: Удалить тесты security-классов**

По списку из Step 1 — удалить тесты, которые purely тестируют security (`SensitiveEndpointAccessServiceTest`, `RequestRateLimiterTest`, `SecurityConfigTest` и т.п.):

```bash
git rm <file>
```

Для тестов admin/export, где раньше передавался `X-Admin-Token` header — переписать: убрать `.header("X-Admin-Token", ...)` из MockMvc-вызовов. Ожидания ответа остаются 200 (раньше был 200 с валидным токеном; теперь 200 без токена).

- [ ] **Step 10: Верификация компиляции**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`. Если есть compile errors — значит где-то остался вызов удалённого класса. Найти grep-ом:

```bash
grep -rn "SensitiveEndpointAccessService\|RequestRateLimiter" \
    modules/ --include="*.java"
```

Expected: пусто. Починить все ссылки.

- [ ] **Step 11: Верификация тестов**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`. Если упали тесты — скорее всего забыл убрать `X-Admin-Token` из MockMvc-вызовов или забыл обновить ожидаемый статус.

- [ ] **Step 12: Commit**

```bash
git commit -m "$(cat <<'EOF'
chore(security): удалить admin-token / rate-limit / Spring Security

Local-only проект слушает только localhost — авторизация чувствительных
endpoint-ов не нужна. Удалены:
- пакет api/security/ целиком (SensitiveEndpointAccessService,
  RequestRateLimiter, handlers)
- SecurityConfig + spring-boot-starter-security
- проверки X-Admin-Token в AdminController, ExportController,
  InterviewApiController
- rate-limit на regenerate

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 5: Чистка `application.yml` + `AppProperties` + `StartupRunner`

**Files:**
- Modify: `modules/quiz-app/src/main/resources/application.yml`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/app/AppProperties.java`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/infrastructure/bootstrap/StartupRunner.java`
- Modify: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/StartupRunnerTest.java` (если там есть ветки для `resetOnStartup`)

- [ ] **Step 1: Почистить `application.yml`**

Открыть `modules/quiz-app/src/main/resources/application.yml`. Удалить блоки целиком:

```yaml
  # --- Безопасность чувствительных endpoint ---
  admin-token: ${APP_ADMIN_TOKEN:}
  regenerate-rate-limit-per-minute: ${REGENERATE_RATE_LIMIT_PER_MINUTE:20}
```

```yaml
    reset-on-startup: ${INTERVIEW_RESET_ON_STARTUP:false}  # очищать варианты ответов при старте (для перегенерации через AI)
```

```yaml
  # --- Цикл автоулучшения качества (10 итераций) ---
  quality-iterations:
    enabled: ${QUALITY_ITERATIONS_ENABLED:false}
    iterations: ${QUALITY_ITERATIONS_COUNT:10}
    sample-size: ${QUALITY_ITERATIONS_SAMPLE_SIZE:10}
    target-score: ${QUALITY_ITERATIONS_TARGET_SCORE:90}
    sleep-between-iterations-ms: ${QUALITY_ITERATIONS_SLEEP_MS:0}
    max-prompt-rules: ${QUALITY_ITERATIONS_MAX_PROMPT_RULES:6}
```

```yaml
  # --- Security ---
  security:
    cors:
      allowed-origins:
        - http://localhost:8080
        - http://127.0.0.1:8080
      allowed-methods:
        - GET
        - POST
        - PUT
        - PATCH
        - DELETE
        - OPTIONS
      allowed-headers:
        - Content-Type
        - X-Admin-Token
        - X-CSRF-TOKEN
      exposed-headers:
        - Retry-After
      allow-credentials: true
      max-age-seconds: 3600
```

Изменить дефолты preload:

```yaml
    startup-preload: ${PRELOAD_STARTUP_PRELOAD:false}
```

```yaml
    full-warmup: ${PRELOAD_FULL_WARMUP:false}
```

- [ ] **Step 2: Почистить `AppProperties.java`**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/app/AppProperties.java`.

Удалить:
- поле `adminToken` и его геттер/сеттер
- поле `regenerateRateLimitPerMinute`
- вложенный класс/поле `Security` (и его `Cors`)
- вложенный класс/поле `QualityIterations`
- в `Interview` — поле `resetOnStartup`

Добавить в класс `AppProperties` новый метод (после существующих геттеров):

```java
/**
 * @return true, если хотя бы у одного из AI-провайдеров задан API-ключ
 */
public boolean isAiEnabled() {
    Openai openai = getOpenai();
    Deepseek deepseek = getDeepseek();
    return (openai != null && openai.getApiKey() != null && !openai.getApiKey().isBlank())
            || (deepseek != null && deepseek.getApiKey() != null && !deepseek.getApiKey().isBlank());
}
```

Если внутренние классы называются иначе (например `DeepSeek` с заглавной) — подставь имя, как в файле.

- [ ] **Step 3: Почистить `StartupRunner.java`**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/infrastructure/bootstrap/StartupRunner.java`.

Удалить целиком блок:

```java
if (appProperties.getInterview().isResetOnStartup()) {
    int deleted = answerOptionRepository.deleteAll();
    optionCache.invalidateAll();
    log.info("Сброс банка ответов: удалено {} записей, кэш очищен", deleted);
} else {
    log.info("Сброс банка ответов отключён (app.interview.reset-on-startup=false)");
}
```

Если после удаления поля `answerOptionRepository` и `optionCache` больше не используются — удалить их из списка `@RequiredArgsConstructor`-полей.

- [ ] **Step 4: Починить тесты**

```bash
grep -rn "resetOnStartup\|isResetOnStartup\|reset-on-startup" \
    modules/ --include="*.java" --include="*.yml"
```

Expected: только в тестах (production код уже почищен).

Открыть найденные тесты (скорее всего `StartupRunnerTest.java`):
- Удалить тест-кейсы, проверявшие ветку `resetOnStartup=true`.
- Если в тесте настраивается `interview.resetOnStartup` через `@TestPropertySource` или builder — удалить это.

- [ ] **Step 5: Проверить, что `application.yml` парсится корректно**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`. Если fail с `@ConfigurationProperties` (unbound property) — значит в yml осталась строка, которую некуда биндить. Поправить.

- [ ] **Step 6: Запустить тесты**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git commit -m "$(cat <<'EOF'
chore(config): почистить application.yml и AppProperties

Удалены блоки admin-token, security.cors, quality-iterations,
interview.reset-on-startup (сброс теперь из UI).

Дефолты preload.startup-preload и preload.full-warmup изменены
на false — меньше нагрузки при локальном запуске без AI-ключей.

Добавлен computed-геттер AppProperties.isAiEnabled() для
дальнейшего no-AI режима.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 6: No-AI режим — бэкенд

**Files:**
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/ai/option/AIQuestionService.java`
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/usecase/mvc/InterviewPageMvcService.java` (или любой сервис, который наполняет Model для `templates/index.html`)
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/core/PreloadService.java`
- Create: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/ai/option/AIQuestionServiceNoAiTest.java`
- Create: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/bootstrap/NoAiModeStartupIntegrationTest.java`

- [ ] **Step 1: Написать падающий unit-тест для no-AI ветки в `AIQuestionService`**

Создать файл `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/ai/option/AIQuestionServiceNoAiTest.java`:

```java
package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AIQuestionServiceNoAiTest {

    @Test
    void getOrCreateOptions_returnsEmpty_whenAiDisabled() {
        AppProperties props = mock(AppProperties.class);
        when(props.isAiEnabled()).thenReturn(false);

        AnswerOptionRepository repo = mock(AnswerOptionRepository.class);
        when(repo.findByQuestionId(anyLong())).thenReturn(List.of());

        AiQuestionClient client = mock(AiQuestionClient.class);
        OptionCache cache = mock(OptionCache.class);
        when(cache.get(anyLong())).thenReturn(null);

        Striped<Lock> locks = Striped.lock(16);
        TransactionTemplate tx = mock(TransactionTemplate.class);

        AIQuestionService service = new AIQuestionService(repo, client, cache, locks, tx, props);
        Question q = questionWithId(42L);

        List<AnswerOption> result = service.getOrCreateOptions(q);

        assertThat(result).isEmpty();
        verifyNoInteractions(client);
    }

    private static Question questionWithId(long id) {
        // Подставь фабрику Question из TestQuestionBuilder или построй вручную
        // через доступный конструктор Question
        throw new UnsupportedOperationException(
                "Замени на TestQuestionBuilder.aQuestion().withId(" + id + ").build() или эквивалент");
    }
}
```

**Примечание:** `questionWithId` — заглушка. В проекте есть `TestQuestionBuilder`. Замени на его вызов при написании.

- [ ] **Step 2: Запустить тест — убедиться, что падает**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.ai.option.AIQuestionServiceNoAiTest"
```

Expected: FAIL с compile error (конструктор `AIQuestionService` не принимает `AppProperties`) — это и есть сигнал, что следующий шаг должен пофиксить конструктор.

- [ ] **Step 3: Добавить `AppProperties` в `AIQuestionService` и no-AI ветку**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/ai/option/AIQuestionService.java`.

Заменить конструктор и метод `getOrCreateOptions` на:

```java
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIQuestionService {
    AnswerOptionRepository answerOptionRepository;
    AiQuestionClient aiQuestionClient;
    OptionCache optionCache;
    TransactionTemplate transactionTemplate;
    Striped<Lock> questionLocks;
    AppProperties appProperties;

    public AIQuestionService(
            AnswerOptionRepository answerOptionRepository,
            AiQuestionClient aiQuestionClient,
            OptionCache optionCache,
            Striped<Lock> questionLocks,
            TransactionTemplate transactionTemplate,
            AppProperties appProperties
    ) {
        this.answerOptionRepository = answerOptionRepository;
        this.aiQuestionClient = aiQuestionClient;
        this.optionCache = optionCache;
        this.transactionTemplate = transactionTemplate;
        this.questionLocks = questionLocks;
        this.appProperties = appProperties;
    }

    public List<AnswerOption> getOrCreateOptions(Question question) {
        List<AnswerOption> cached = optionCache.get(question.id());
        if (cached != null) {
            return cached;
        }

        List<AnswerOption> existing = answerOptionRepository.findByQuestionId(question.id());
        if (existing != null && !existing.isEmpty()) {
            optionCache.put(question.id(), existing);
            return existing;
        }

        if (!appProperties.isAiEnabled()) {
            // No-AI режим: варианты не генерируются, UI переходит в flashcard-фазу.
            return List.of();
        }

        Lock lock = questionLocks.get(question.id());
        lock.lock();
        try {
            existing = answerOptionRepository.findByQuestionId(question.id());
            if (existing != null && !existing.isEmpty()) {
                optionCache.put(question.id(), existing);
                return existing;
            }

            GeneratedOptions generated = aiQuestionClient.generateOptions(question.questionText(), question.codeSnippet())
                    .orElseThrow(() -> new AiGenerationException(
                            "AI не вернул валидные варианты ответа для вопроса id=" + question.id()));
            List<AnswerOptionCreate> created = QuestionResponseMapper.mapToCreates(generated, aiQuestionClient.sourceId());
            transactionTemplate.executeWithoutResult(status -> {
                answerOptionRepository.deleteByQuestionId(question.id());
                answerOptionRepository.insertAll(question.id(), created);
            });

            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.id());
            optionCache.put(question.id(), options);
            return options;
        } finally {
            lock.unlock();
        }
    }
}
```

Добавить импорт `com.cheatsheet.quiz.config.app.AppProperties`.

- [ ] **Step 4: Запустить тест снова**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.ai.option.AIQuestionServiceNoAiTest"
```

Expected: PASS.

- [ ] **Step 5: Почистить `PreloadService` для no-AI**

Открыть `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/core/PreloadService.java`.

В методах `preloadNext(...)` и `warmupAll()` — в самом начале добавить early return:

```java
if (!appProperties.isAiEnabled()) {
    log.info("Preload/warmup пропущен: AI-ключи не заданы (no-AI режим)");
    return;
}
```

Если поля `appProperties` нет — добавить его в конструктор через `@RequiredArgsConstructor` / `@FieldDefaults` по паттерну класса.

- [ ] **Step 6: Прокинуть `aiEnabled` в MVC-модель**

Найти сервис, который наполняет модель для `templates/index.html`:

```bash
grep -rn "addAttribute\|model\.addAttribute" \
    modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/usecase/mvc/ \
    modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/
```

Ожидаемо: `InterviewPageMvcService.java` / `InterviewFlowMvcService.java`.

В каждом методе, который делает `model.addAttribute("current", ...)` или `model.addAttribute("stats", ...)` — рядом добавить:

```java
model.addAttribute("aiEnabled", appProperties.isAiEnabled());
```

Если в сервисе нет поля `appProperties` — инжектнуть его через конструктор (класс уже использует `@RequiredArgsConstructor`, просто добавь поле).

- [ ] **Step 7: Написать integration-тест на no-AI старт**

Создать файл `modules/quiz-app/src/test/java/com/cheatsheet/quiz/bootstrap/NoAiModeStartupIntegrationTest.java`:

```java
package com.cheatsheet.quiz.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет, что приложение стартует и отдаёт главную страницу
 * даже если ни один AI-ключ не задан (no-AI режим).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.openai.api-key=",
        "app.deepseek.api-key=",
        "app.preload.startup-preload=false",
        "app.preload.full-warmup=false"
})
class NoAiModeStartupIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void indexPage_opens_inNoAiMode() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
```

- [ ] **Step 8: Запустить integration-тест**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.bootstrap.NoAiModeStartupIntegrationTest"
```

Expected: PASS. Если FAIL — посмотри в логах: скорее всего Spring AI bean требует api-key и падает. В этом случае нужно сделать OpenAiChatModel/DeepseekClient conditional: `@ConditionalOnProperty(name = "app.openai.api-key", matchIfMissing = false)`. Найти AI-конфиг:

```bash
grep -rn "OpenAiChatModel\|@Bean.*openai\|@Bean.*deepseek" \
    modules/quiz-app/src/main/java
```

В соответствующих `@Bean`-методах добавить `@ConditionalOnProperty` чтобы бины не создавались при пустом ключе. `AiQuestionClient` тогда должен иметь `@Nullable`-клиент и возвращать empty, но он уже не вызывается благодаря `isAiEnabled()`-чеку в `AIQuestionService`. Достаточно, чтобы бины не падали при старте.

- [ ] **Step 9: Прогнать весь тест-слой**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`. Если падают другие тесты, которые ожидали старого конструктора `AIQuestionService` — добавить в них `AppProperties` mock.

- [ ] **Step 10: Commit**

```bash
git commit -m "$(cat <<'EOF'
feat(no-ai): flashcard-only режим при пустых AI-ключах

Без AI-ключей приложение больше не падает и не отдаёт
пустые варианты — оно переходит в flashcard-режим:
- AIQuestionService.getOrCreateOptions возвращает [] если !isAiEnabled
- PreloadService.preloadNext/warmupAll делают no-op
- В MVC-модель прокидывается aiEnabled (для th:if в шаблонах)
- Bean-ы AI-клиентов conditional'ятся на наличие api-key

Добавлен integration-тест: главная открывается в no-AI режиме.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 7: No-AI режим — фронт (Thymeleaf)

**Files:**
- Modify: `modules/quiz-app/src/main/resources/templates/index.html`
- Modify: `modules/quiz-app/src/main/resources/templates/fragments/training-actions.html` (если кнопка regenerate там)
- Modify: `modules/quiz-app/src/main/resources/templates/fragments/post-answer-controls.html` (если там диаграмма)
- Modify другие фрагменты, где встречаются `btn-regenerate`, `btn-hint`, `question-diagram`.

- [ ] **Step 1: Найти все места в шаблонах, где встречаются AI-зависимые кнопки**

```bash
grep -rn "btn-regenerate\|btn-hint\|btn-regen\|question-diagram\|mermaid" \
    modules/quiz-app/src/main/resources/templates/
```

Запомни список файлов.

- [ ] **Step 2: Обернуть каждый AI-элемент в `th:if="${aiEnabled}"`**

В каждом найденном месте — найти корневой элемент (button/div/section с этим классом) и добавить атрибут `th:if="${aiEnabled}"`.

Пример до:
```html
<button type="button" class="btn-regenerate" title="Перегенерировать варианты через LLM"
        th:data-question-id="${current.question.id}" id="btn-regenerate">🔄</button>
```

Пример после:
```html
<button type="button" class="btn-regenerate" title="Перегенерировать варианты через LLM"
        th:data-question-id="${current.question.id}" id="btn-regenerate"
        th:if="${aiEnabled}">🔄</button>
```

Аналогично для `btn-hint` (кнопка 💡 — она использует `HintService`, который AI). Для блока диаграммы в `templates/index.html:193-201` — добавить `th:if="${aiEnabled}"` на `<aside class="training-side">` (или внутри — на `<div th:if="${diagram != null}">`, уже есть условие, добавить `and ${aiEnabled}`).

- [ ] **Step 3: Подстраховка — если `current.options` пуст при `aiEnabled=true` (например, преgenerate ещё не отработал), показать флешкарту**

В `templates/index.html` — найти блок `th:if="${flashcardMode}"` (~строка 95). Добавить в контроллер логику: если у текущего вопроса пустой список вариантов — форсить `flashcardMode=true` в модели.

Найти, где выставляется `flashcardMode`:

```bash
grep -rn "flashcardMode" modules/quiz-app/src/main/java
```

В соответствующем сервисе (`InterviewPageMvcService` или близкий) — в начале, после получения списка вариантов:

```java
boolean options = currentOptions != null && !currentOptions.isEmpty();
boolean flashcardMode = existingFlashcardCondition || !options;
model.addAttribute("flashcardMode", flashcardMode);
```

Где `existingFlashcardCondition` — текущее условие (если есть). В простейшем случае, если `flashcardMode` выставлялся только руками — теперь: `boolean flashcardMode = !options;`.

- [ ] **Step 4: Ручная smoke-проверка запуска без AI-ключей**

```bash
./gradlew bootRun
```

В другом терминале:

```bash
curl -s http://localhost:8080/ | grep -c "flashcard\|Вспомни ответ"
```

Expected: число ≥1 (страница содержит флешкард-элементы).

Также открыть `http://localhost:8080/` в браузере и вручную убедиться:
- вопрос показан
- кнопок 🔄 и 💡 нет
- есть кнопка «Показать ответ»
- после клика — ответ и оценка 1–4

Остановить приложение Ctrl+C.

- [ ] **Step 5: Smoke-проверка с AI-ключом (опционально, если есть доступ)**

```bash
OPENAI_API_KEY=sk-... ./gradlew bootRun
```

В браузере — кнопки 🔄 и 💡 должны появиться. Варианты ответов должны генерироваться.

Остановить приложение.

- [ ] **Step 6: Верификация тестов**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git commit -m "$(cat <<'EOF'
feat(ui): скрыть AI-зависимые элементы в no-AI режиме

В no-AI режиме (aiEnabled=false) в шаблонах скрыты:
- кнопка 🔄 (regenerate)
- кнопка 💡 (подсказка, AI-генерируемая)
- блок Mermaid-диаграммы

При пустом списке вариантов форсится flashcardMode — пользователь
видит эталонный ответ и самооценивается по SM-2 (grade 1–4).

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 8: UI-кнопка «Сбросить банк вариантов» на /settings

**Files:**
- Modify: `modules/quiz-app/src/main/resources/templates/settings.html`
- Modify: один из MVC-контроллеров (или новый `SettingsMvcController`), чтобы обработать POST-редирект с toast-сообщением

- [ ] **Step 1: Найти, как сейчас рендерится `/settings`**

```bash
grep -rn "settings\.html\|\"/settings\"\|GetMapping.*settings" \
    modules/quiz-app/src/main/java --include="*.java"
```

Открыть соответствующий контроллер. Если для `/settings` есть только GET — добавь POST-метод для очистки (смотри Step 3).

- [ ] **Step 2: Добавить кнопку в `templates/settings.html`**

Открыть `modules/quiz-app/src/main/resources/templates/settings.html`. В подходящем месте (в конце `<main>`) добавить новую секцию:

```html
<section class="card" aria-labelledby="data-management-title">
    <h2 id="data-management-title">Управление данными</h2>

    <p th:if="${resetDeleted != null}" class="toast toast-success">
        Удалено <span th:text="${resetDeleted}">0</span> вариантов. Они будут сгенерированы заново при следующем показе вопросов.
    </p>

    <form method="post" th:action="@{/settings/reset-options}" class="data-management-form">
        <p>Удалить все сгенерированные AI варианты ответов. Полезно, если сменил AI-модель или хочешь перегенерировать все варианты.</p>
        <button type="submit" class="btn danger-btn">Сбросить банк вариантов</button>
    </form>
</section>
```

- [ ] **Step 3: Добавить POST-обработчик в MVC-контроллер**

В том же контроллере, где GET `/settings`, добавить POST-метод:

```java
@PostMapping("/settings/reset-options")
public String resetOptions(RedirectAttributes redirectAttributes) {
    AdminApiService.AdminApiResult result = adminApiService.clearOptions();
    Object body = result.body();
    Integer deleted = 0;
    if (body instanceof AdminOperationResult op
            && op.data() instanceof java.util.Map<?, ?> data
            && data.get("deleted") instanceof Integer n) {
        deleted = n;
    }
    redirectAttributes.addFlashAttribute("resetDeleted", deleted);
    return "redirect:/settings";
}
```

Добавить в контроллер поля:

```java
AdminApiService adminApiService;
```

И `@Autowired`/`@RequiredArgsConstructor` уже должен быть — просто добавь поле в список.

**Проверь тип** `AdminOperationResult.data()` — если он называется иначе (`getData()`, `payload()` и т.п.), используй правильное имя.

- [ ] **Step 4: Ручная проверка**

```bash
./gradlew bootRun
```

Открыть в браузере `http://localhost:8080/settings`. Нажать кнопку «Сбросить банк вариантов». Ожидается:
- редирект на `/settings`
- зелёный toast «Удалено N вариантов»

Остановить приложение Ctrl+C.

- [ ] **Step 5: Верификация тестов**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit**

```bash
git commit -m "$(cat <<'EOF'
feat(settings): UI-кнопка «Сбросить банк вариантов»

На странице /settings добавлена секция «Управление данными»
с кнопкой сброса всех AI-сгенерированных вариантов ответов.
Под капотом — existing /api/admin/options/clear (без токена,
т.к. localhost-only).

После сброса — редирект на /settings с toast «Удалено N вариантов».

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 9: Актуализация документации

**Files:**
- Modify: `README.md`
- Modify: `CLAUDE.md`

- [ ] **Step 1: Упростить раздел «Быстрый старт» в `README.md`**

Открыть `README.md`. Раздел «Быстрый старт» заменить на:

```markdown
## Быстрый старт

```bash
./gradlew bootRun
```

Откройте **http://localhost:8080**.

- REST API / Swagger UI: `http://localhost:8080/swagger-ui.html`
- API-ключи опциональны: без них приложение работает в режиме флешкарт (показ эталонного ответа + самооценка по SM-2). С ключом (`OPENAI_API_KEY` или `DEEPSEEK_API_KEY`) включаются AI-генерация вариантов, подсказки и Mermaid-диаграммы.
- Сброс банка вариантов — кнопка на `/settings`.
```

Удалить в README разделы про Docker, production-профиль, admin-token, CORS и всё подобное. Оставь только то, что касается local-запуска.

- [ ] **Step 2: Обновить `CLAUDE.md`**

Открыть `CLAUDE.md`. В секции «Key Configuration»:
- Удалить строку про `app.aiProvider` с дефолтом `spring-ai` — сейчас реально `openai` (см. application.yml).
- Удалить строку про `app.interview.resetOnStartup` — удалено.
- Добавить строку про `AppProperties.isAiEnabled()` — новое computed-поле.
- Удалить всё про prod-профиль, Dockerfile, `app.adminToken`.

В секции «Build & Run»:
- Убрать `./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.SpacedRepetitionServiceTest"` → оставить (этот тест есть), но проверь `--spring.profiles.active=postgres` — такой команды больше не существует, удалить упоминание.

- [ ] **Step 3: Проверить, что в README и CLAUDE.md больше нет устаревших упоминаний**

```bash
grep -E "docker|Dockerfile|admin-token|admin.token|spring-ai|postgres\s*profile|SPRING_PROFILES_ACTIVE=prod" \
    README.md CLAUDE.md
```

Expected: пусто или только в явно историческом контексте (changelog и т.п.). Иначе почистить.

- [ ] **Step 4: Commit**

```bash
git commit -m "$(cat <<'EOF'
docs: синхронизировать README и CLAUDE.md с local-only конфигом

- README: «Быстрый старт» в одну команду, упомянут no-AI режим и
  UI-кнопка сброса, удалены разделы про Docker/prod/admin-token.
- CLAUDE.md: убраны устаревшие упоминания spring-ai, prod-профиля,
  admin-token, reset-on-startup; добавлен isAiEnabled.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 10: Финальная верификация

**Files:**
- No file changes. Только проверки.

- [ ] **Step 1: Полный билд + тесты**

```bash
./gradlew clean build
```

Expected: `BUILD SUCCESSFUL`. Все тесты зелёные.

- [ ] **Step 2: Smoke-тест no-AI**

Убедиться, что в окружении нет AI-ключей:

```bash
unset OPENAI_API_KEY DEEPSEEK_API_KEY
./gradlew bootRun
```

В другом терминале:

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/
```

Expected: `200`.

Открыть в браузере. Проверить:
- главная работает
- нет кнопок 🔄 / 💡
- видна флешкард-разметка («Показать ответ» / оценка 1–4)
- `/settings` открывается, кнопка «Сбросить банк вариантов» есть
- нажатие кнопки возвращает toast «Удалено 0 вариантов» (или число, если БД была непустой)

Остановить приложение Ctrl+C.

- [ ] **Step 3: Smoke-тест с AI-ключом (если доступен ключ)**

```bash
OPENAI_API_KEY=sk-... ./gradlew bootRun
```

В браузере:
- варианты ответов генерируются по запросу
- кнопки 🔄 и 💡 видны и кликаются
- `/settings` → «Сбросить банк вариантов» → все варианты удаляются, при следующем показе вопроса генерируются заново

Остановить приложение.

- [ ] **Step 4: Проверить, что spec satisfied — пройти по success criteria из spec**

Открыть `docs/superpowers/specs/2026-04-19-local-first-simplification-design.md`, раздел «Критерии приёмки». Пройтись по каждому пункту глазами:

```bash
# 3. ✅ В репозитории отсутствуют удалённые файлы
ls Dockerfile docker-compose.yml .dockerignore \
   modules/quiz-app/src/main/resources/application-prod.yml \
   modules/quiz-app/src/main/resources/application-postgres.yml \
   2>&1 | grep "No such" | wc -l
# Expected: 5

# 3b. Security-пакет отсутствует
ls modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/security/ 2>&1 | grep "No such" | wc -l
# Expected: 1

# 4. В application.yml нет удалённых блоков
grep -E "admin-token|quality-iterations|regenerate-rate-limit|security:|reset-on-startup" \
    modules/quiz-app/src/main/resources/application.yml
# Expected: пусто
```

- [ ] **Step 5: Финальный коммит-марафон closure**

Убедиться, что рабочая копия чистая в пределах затронутых путей:

```bash
git status --short | grep -E "modules/|README|CLAUDE|docs/superpowers"
```

Expected: пусто. Если что-то осталось — либо это нестейджед-изменения пользователя (не трогай), либо забыл закоммитить.

- [ ] **Step 6: Посмотреть итоговую git-историю этапа**

```bash
git log --oneline -10
```

Должно быть видно цепочку коммитов Task 1 → Task 9. Если хочется — сквош через rebase (но это на усмотрение пользователя, не обязательно).

---

## Self-review checklist (выполняется автором плана после написания)

- [x] Spec coverage: каждый пункт success-criteria из spec покрыт задачей (Task 1 — Docker, Task 2 — Postgres, Task 3 — prod, Task 4 — security, Task 5 — config/AppProperties, Task 6+7 — no-AI, Task 8 — reset button, Task 9 — docs, Task 10 — verification).
- [x] Placeholder scan: нет TBD/TODO, кроме явно помеченного `questionWithId`-плейсхолдера в Step 1 Task 6 (с явным указанием, чем заменить).
- [x] Type consistency: `isAiEnabled()` используется одинаково во всех местах (`AppProperties.isAiEnabled()`); `clearOptions()` — единый вариант имени; модельный атрибут — `aiEnabled` (везде одно и то же).
- [x] Dependency order: каждая задача оставляет зелёный билд (verify-шаг в конце каждой задачи).
- [x] Рекомендация по fix-up: в нескольких задачах допускается небольшое «ориентировочное исследование кода» (grep'ы), т.к. план не пытается повторять всю кодовую базу verbatim.
