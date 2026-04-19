# D — Local-first упрощение

**Дата:** 2026-04-19
**Статус:** согласован (брейнсторм)
**Автор:** Сергей + Claude
**Связано с:** общий roadmap «идеальный тренажёр», этап **D** (первый в последовательности D → A → C → B → E).

---

## 1. Цель и контекст

Проект `cheat-sheet` — локальный тренажёр для подготовки к собеседованию. Владелец запускает приложение **только на своей машине**. При этом код содержит production-ориентированные слои (admin-токен, rate-limit, CORS-whitelist, prod-профиль, Dockerfile, docker-compose с Postgres), которые ни при каком сценарии локального использования не нужны, но раздувают конфиг, тянут лишние зависимости и создают «две параллельные реальности» (local vs prod), через которые спотыкаешься каждый раз при чтении кода.

Дополнительно:
- При старте по умолчанию запускается `full-warmup` — AI-прогрев всех ~N вопросов, что при каждом запуске сжигает токены.
- Без API-ключей приложение стартует, но молча отдаёт пустые варианты ответов — тренажёр становится неработоспособным без объяснения причины.
- Очистка банка вариантов ответов требует либо env-флага `INTERVIEW_RESET_ON_STARTUP=true`, либо admin-endpoint с токеном — неудобно.

**Цель:** сделать приложение максимально простым в локальном запуске: одна команда, минимум файлов конфигурации, работоспособность без AI-ключей, чистка БД из UI.

**Не-цель:** чистка UI (подсистема A), переработка SM-2/педагогики (B), контент-пайплайн (C), мета-скилл (E). Эти этапы идут после D своим циклом.

---

## 2. Скоуп

### In scope
- Удалить production-ориентированные артефакты (Dockerfile, docker-compose, prod-профиль, postgres-профиль).
- Удалить security-слой (admin-токен, rate-limit для regenerate, CORS-whitelist).
- Изменить дефолты preload/warmup на `false`.
- Добавить режим работы без AI (`ai-provider: none` / автоопределение) с автоматическим переходом в flashcard-фазу.
- Добавить UI-кнопку «Сбросить банк вариантов» на `/settings`.
- Актуализировать `README.md` и корневой `CLAUDE.md`.

### Out of scope
- Любые изменения в `cheatsheets/`.
- Изменения UI, кроме прокидывания флага `aiEnabled` и новой кнопки сброса.
- Изменения в логике SM-2, расписании повторений, генерации вопросов.
- Изменения в `INTERVIEW_EXPANSION_PLAN.md`, `WIKI.md`, `docs/` (кроме этого spec-файла).

---

## 3. Изменения по файлам

### 3.1 Удаляемые файлы

| Файл | Причина |
|---|---|
| `Dockerfile` | Local-only, контейнер не нужен |
| `docker-compose.yml` | С ним уходит Postgres-окружение |
| `.dockerignore` | Становится бессмысленным |
| `modules/quiz-app/src/main/resources/application-prod.yml` | Нет prod-сценария |
| `modules/quiz-app/src/main/resources/application-postgres.yml` | SQLite — единственная БД |
| `modules/quiz-persistence/src/main/resources/db/migration-postgres/` (каталог целиком) | Вместе с postgres-профилем |
| Весь пакет `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/security/` | Админ-токен / rate-limit / handlers не нужны. Подтверждено: `RequestRateLimiter` используется только `SensitiveEndpointAccessService` — безопасно удалить вместе. Классы: `SensitiveEndpointAccessService`, `RequestRateLimiter`, `SensitiveEndpointAuthenticationEntryPoint`, `SensitiveEndpointAccessDeniedHandler` |
| `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/SecurityConfig.java` | Настраивает Spring Security filter chain, CORS, CSRF — всё не нужно на localhost-only |
| Соответствующие тесты этих классов | Удаляются вместе с кодом |

Все удаления — через `git rm`, чтобы история была чистой.

### 3.1.1 Изменения в `modules/quiz-app/build.gradle.kts`

Удаляем следующие зависимости:
- `libs.spring.boot.starter.security` (вместе с security-пакетом)
- `libs.flyway.database.postgresql` (нужно было только для postgres-миграций)
- `runtimeOnly(libs.postgresql)` (JDBC-драйвер Postgres)
- `libs.spring.security.test` (тестовые хелперы Spring Security)
- `libs.spring.boot.testcontainers`, `libs.testcontainers.junit.jupiter`, `libs.testcontainers.postgresql` (Testcontainers использовались только для Postgres-интеграционных тестов, которые уходят вместе с профилем)

Оставляем: `spring-boot-starter-web`, `spring-boot-starter-jdbc`, `sqlite-jdbc`, `flyway-core`, `spring-ai-openai`, всё остальное.

### 3.2 Изменения в `application.yml`

**Удалить блоки:**
- `app.admin-token`
- `app.regenerate-rate-limit-per-minute`
- `app.security.cors.*`
- `app.quality-iterations.*`
- `app.interview.reset-on-startup` — становится избыточным, т.к. сброс доступен из UI

**Изменить дефолты:**
- `app.preload.startup-preload` → `false` (поле остаётся, можно включить через env)
- `app.preload.full-warmup` → `false` (поле остаётся, можно включить через env)

**Итог:** `application.yml` в 1 профиль, ~90–100 строк (сейчас ~144).

### 3.3 Изменения в коде Java

**Контроллеры, ссылающиеся на `SensitiveEndpointAccessService`:**
- `AdminController` — убрать проверку токена, сделать endpoint'ы открытыми (localhost-only).
- `ExportController` — то же.
- `InterviewApiController` в части regenerate — убрать rate-limit + проверку токена.

**Конфиг:**
- `AppProperties` — удалить поля `adminToken`, `regenerateRateLimitPerMinute`, `interview.resetOnStartup`, вложенный класс `Security.Cors`, класс `QualityIterations`.
- `AppProperties` — добавить computed-геттер `isAiEnabled()`: возвращает `true`, если хотя бы у одного из провайдеров (`openai`, `deepseek`) задан `apiKey`.
- `StartupRunner` — выпилить ветку `resetOnStartup` (теперь сброс через UI).

**`OptionGenerationService`:**
- Если `!appProperties.isAiEnabled()` — метод `generateOptionsForQuestion(...)` возвращает пустой список без попыток AI-вызова.
- Соответственно, `PreloadService` в режиме no-AI — no-op.

**Контроллеры, отдающие вопрос в UI (`InterviewMvcController`):**
- В модель прокидывается флаг `aiEnabled` (из `appProperties.isAiEnabled()`).
- Если `aiEnabled == false` и у текущего вопроса пустой список вариантов — `flashcardMode = true` принудительно.

**Admin endpoint для сброса:**
- Уже существует `POST /api/admin/options/clear` в `AdminController` (делегирует в `AdminApiService.clearOptions(token)`).
- Меняем: убираем параметр `token` и всю проверку авторизации. `AdminApiService.clearOptions()` после упрощения вызывает `AnswerOptionRepository.deleteAll()` + `OptionCache.invalidateAll()`, возвращает `{"deleted": N}`.
- Аналогично упрощаем остальные admin-endpoints (`RegenerateEndpointService`, `ExportController`, если там есть проверки токена).

### 3.4 Изменения в шаблонах Thymeleaf

**`fragments/head.html`, `fragments/header.html`, `fragments/training-actions.html` и т.п.:**
- Скрыть кнопки, требующие AI, под `th:if="${aiEnabled}"`:
  - `btn-regenerate` (🔄)
  - `btn-hint` (💡) — если подсказки реально AI-генерируются (проверить в HintService)
  - Блок диаграммы

**`templates/settings.html`:**
- Новая секция «Управление данными» с кнопкой «Сбросить банк вариантов». Форма POST на `/api/admin/options/clear` (или обычный MVC POST с редиректом — решается на этапе имплементации). После ответа — редирект на `/settings?reset=N`, верхний баннер с сообщением «Удалено N записей».

### 3.5 Изменения в документации

**`README.md`:**
- Раздел «Быстрый старт» — одна команда `./gradlew bootRun`, env-ключи опциональны.
- Удалить секции про Docker и prod-профиль.
- Добавить короткий абзац про no-AI режим (flashcard-only).

**`CLAUDE.md`:**
- Убрать упоминания `spring-ai` (рассинхрон с yml).
- Убрать упоминание `app.adminToken`, `prod-профиля`, Docker.
- Синхронизировать таблицу дефолтов с новым `application.yml`.

---

## 4. Новое поведение

### 4.1 Запуск без env-ключей (no-AI режим)

```
./gradlew bootRun
```

Сценарий:
1. Приложение стартует за <10 сек (без `full-warmup`).
2. Импорт markdown-файлов отрабатывает как обычно (hash-check).
3. `appProperties.isAiEnabled()` → `false`.
4. `PreloadService` не делает ничего при стартовом вызове в no-AI.
5. Пользователь открывает `/`, видит первый вопрос во flashcard-фазе:
   - Вопрос сверху.
   - Кнопка «Показать ответ».
   - После клика — эталонный ответ + 4 кнопки самооценки (1–4).
6. SM-2 считает интервалы на основе самооценки (существующая `flashcardMode` ветка).
7. Статистика/поиск/избранное работают полностью.

### 4.2 Запуск с AI-ключом (обычный режим)

```
OPENAI_API_KEY=sk-... ./gradlew bootRun
```

- `isAiEnabled() → true`.
- Все кнопки (🔄, 💡, диаграмма) активны.
- Варианты ответов генерируются по запросу (preload остаётся off по дефолту, лениво).

### 4.3 Сброс вариантов из UI

- `/settings` → секция «Управление данными» → кнопка «Сбросить банк вариантов».
- После нажатия форма постит на `/admin/options/clear`.
- Ответ: `{"deleted": N}`, страница перезагружается с баннером «Удалено N записей».
- Следующий раз при просмотре вопроса варианты генерируются заново (если `aiEnabled`).

---

## 5. Критерии приёмки (Success criteria)

1. ✅ `./gradlew bootRun` без env-ключей: приложение поднимается, главная `/` отдаёт рабочий flashcard-вопрос, можно оценить 1–4, переход к следующему.
2. ✅ `./gradlew bootRun` c `OPENAI_API_KEY=...`: AI-варианты генерируются по запросу, 🔄/💡 работают.
3. ✅ В репозитории отсутствуют: `Dockerfile`, `docker-compose.yml`, `.dockerignore`, `application-prod.yml`, `application-postgres.yml`, `db/migration-postgres/`, `SensitiveEndpointAccessService.java`.
4. ✅ В `application.yml` нет блоков `admin-token`, `security.cors`, `quality-iterations`, `regenerate-rate-limit-per-minute`.
5. ✅ Кнопка «Сбросить варианты» на `/settings` работает без токенов/заголовков.
6. ✅ `./gradlew test` проходит полностью (существующие тесты должны быть обновлены или удалены вместе со своими классами).
7. ✅ `./gradlew build -x test` проходит.
8. ✅ `README.md` описывает один способ запуска, без упоминания Docker/prod.
9. ✅ `CLAUDE.md` синхронизирован с актуальным `application.yml`.

---

## 6. Тесты

### Обязательные

1. **`NoAiModeStartupIntegrationTest`** (новый, `@SpringBootTest`) — запуск без API-ключей, проверка:
   - приложение поднимается,
   - GET `/` возвращает 200 и содержит маркер flashcard-фазы,
   - `OptionGenerationService.generateOptionsForQuestion(...)` возвращает пустой список.
2. **`AdminControllerResetTest`** (unit) — POST `/admin/options/clear` без заголовка авторизации возвращает 200 и `{"deleted": N}`.
3. **Обновление существующих** тестов, которые дергали `X-Admin-Token` — убрать заголовок, если контроллер перестал его требовать. Удалить тесты, покрывавшие `SensitiveEndpointAccessService`.
4. **ArchUnit** (`LayeredArchitectureTest`) — проверить что ссылок на удалённые классы не осталось.

### Нежелательные, но допустимые к отложению

- E2E-тест UI (нажатие кнопки сброса в браузере) — опционально, можно проверить вручную в первой итерации.

---

## 7. Риски и смягчение

| Риск | Смягчение |
|---|---|
| Существующие тесты ссылаются на `X-Admin-Token` или `SensitiveEndpointAccessService` | Обновить тесты в рамках имплементации; билд всегда зелёный на промежуточных шагах |
| UI показывает AI-кнопки в no-AI режиме | Все AI-кнопки под `th:if="${aiEnabled}"`, контроллеры обязаны прокинуть флаг |
| `HintService` использует AI — в no-AI режиме подсказка падает | Проверить при имплементации; если AI — скрыть кнопку 💡 через `aiEnabled`; если есть статичные подсказки из markdown — оставить |
| Поломка Postgres-пользователей | Принято: поддержка удаляется (spec согласован) |
| Удаление `RequestRateLimiter` ломает что-то ещё | Проверить `Grep`'ом все ссылки; удалять только если единственный потребитель — `SensitiveEndpointAccessService` |
| Миграции Flyway (`db/migration-postgres`) всё ещё ссылаются из конфига | Удалить `application-postgres.yml` → ссылок на `migration-postgres` не останется |
| Spring Security дефолтно закроет всё basic-auth'ом, если starter останется | Убрать `spring-boot-starter-security` из зависимостей вместе с пакетом security |
| Тесты интеграции с Postgres (`PostgresIntegrationTest`) перестанут собираться | Удалить эти тесты вместе с Testcontainers-зависимостями |
| Кэш `OptionCache` не инвалидируется при сбросе | Endpoint сброса обязан вызвать и `repo.deleteAll()`, и `optionCache.invalidateAll()` |

---

## 8. Порядок имплементации (preview)

Детальный план — в отдельном документе через `superpowers:writing-plans`. Предварительные чанки:

1. **Удаление Docker/prod/postgres** — чисто файловые удаления + правка `build.gradle.kts` (уход postgres-драйвера, если он там).
2. **Удаление security-слоя** — удалить классы, убрать вызовы в контроллерах, обновить тесты.
3. **Чистка `application.yml` + `AppProperties`** — удалить неиспользуемые поля, обновить Javadoc.
4. **No-AI режим** — `isAiEnabled()`, ветвление в `OptionGenerationService`, `InterviewMvcController`, `th:if` в шаблонах.
5. **Кнопка сброса** — UI + endpoint без токена + toast.
6. **Документация** — README + CLAUDE.md.
7. **Итоговая верификация** — `./gradlew build test`, ручная проверка обоих сценариев запуска.

Каждый чанк — отдельный коммит, зелёный билд на каждом шаге.

---

## 9. Открытых вопросов нет

Все развилки закрыты в брейнсторме. Spec готов к переходу на этап writing-plans.
