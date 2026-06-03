# Подготовка к собеседованию (Interview Prep)

Локальное Spring Boot приложение для тренировки перед техническим интервью: вопросы из markdown-шпаргалок, AI-генерация вариантов ответа (OpenAI / DeepSeek / Spring AI), интервальное повторение по SM-2, прогрессивные подсказки и Mermaid-диаграммы.

> Если вам нужна только короткая инструкция: см. раздел **[TL;DR — самый быстрый запуск](#tl-dr--самый-быстрый-запуск)** ниже.

---

## Содержание

- [TL;DR — самый быстрый запуск](#tl-dr--самый-быстрый-запуск)
- [Возможности](#возможности)
- [Требования](#требования)
- [Первоначальная подготовка репозитория](#первоначальная-подготовка-репозитория)
- [Способ 1. Запуск через Gradle (рекомендуется для разработки)](#способ-1-запуск-через-gradle-рекомендуется-для-разработки)
- [Способ 2. Запуск собранного JAR](#способ-2-запуск-собранного-jar)
- [Способ 3. Docker (одиночный контейнер)](#способ-3-docker-одиночный-контейнер)
- [Способ 4. Docker Compose с PostgreSQL](#способ-4-docker-compose-с-postgresql)
- [Способ 5. Запуск с PostgreSQL без Docker](#способ-5-запуск-с-postgresql-без-docker)
- [Режимы работы (с AI и без)](#режимы-работы-с-ai-и-без)
- [Переменные окружения](#переменные-окружения)
- [Профили Spring](#профили-spring)
- [Доступ к приложению](#доступ-к-приложению)
- [Проверка работоспособности](#проверка-работоспособности)
- [Наблюдаемость (актуатор + метрики)](#наблюдаемость-актуатор--метрики)
- [Управление данными](#управление-данными)
- [Сборка и тесты](#сборка-и-тесты)
- [Структура проекта](#структура-проекта)
- [Troubleshooting](#troubleshooting)
- [Дополнительная документация](#дополнительная-документация)

---

## TL;DR — самый быстрый запуск

Проект использует **только PostgreSQL** — самый простой путь это docker compose:

```bash
# 1. Заполнить .env (хотя бы APP_ADMIN_TOKEN)
cp .env.example .env
echo "APP_ADMIN_TOKEN=$(openssl rand -hex 32)" >> .env

# 2. Поднять стек (PostgreSQL + приложение)
docker compose up -d
```

Откройте **http://localhost:8080**. По умолчанию MCQ берутся из `seed/mcq/**.json`,
AI не вызывается. Чтобы перезалить вопросы — `INTERVIEW_RESET_ON_STARTUP=true docker compose up -d --force-recreate interview-prep`.

---

## Возможности

- **Режимы:** Тренировка (по одному вопросу), Экзамен (по умолчанию 20 вопросов), Марафон (по умолчанию 50 вопросов).
- **Интервальное повторение:** алгоритм SM-2, расчёт даты следующего повторения и mastery-статистика.
- **AI-генерация:** варианты ответа (4 опции — 1 правильный + 3 дистрактора), 3 уровня прогрессивных подсказок, Mermaid-диаграммы.
- **Полнотекстовый поиск:** PostgreSQL `tsvector`.
- **Экспорт прогресса:** JSON и CSV (`/export?format=json|csv`).
- **Горячие клавиши:** `1–4` — выбор варианта, `Enter` — отправить ответ.
- **Без AI-ключей:** автоматический fallback в режим флешкарт (показ эталонного ответа + самооценка по SM-2).
- **Сброс банка вариантов:** кнопка на `/settings` или `POST /api/admin/options/clear`.

---

## Требования

| Компонент | Версия | Зачем |
|-----------|--------|-------|
| **JDK** | 17+ | Сборка и запуск |
| **Gradle** | 8.x (или wrapper) | Сборка |
| **PostgreSQL** | 14+ | Единственная поддерживаемая БД (локально через docker compose) |
| **Docker / Docker Compose** | актуальная | Нужен для запуска БД и для прогонов тестов (Testcontainers) |
| **API-ключ** OpenAI или DeepSeek | — | Опционально и только если включаешь `AI_FALLBACK_ENABLED=true` |

Проверка версий:

```bash
java -version    # должно быть 17+
gradle -v        # 8.x; либо использовать ./gradlew
docker --version
```

---

## Первоначальная подготовка репозитория

В репозитории может отсутствовать `gradle/wrapper/gradle-wrapper.jar` (он добавляется в `.gitignore` некоторых конфигураций). Если файла нет — сгенерируйте wrapper один раз:

```bash
gradle wrapper
```

Зафиксируйте полученные файлы (`gradle/wrapper/`, `gradlew`, `gradlew.bat`) в коммите. После этого все остальные команды используют только `./gradlew` и системный Gradle уже не нужен.

Если у вас нет даже системного Gradle, на macOS/Linux можно временно поднять его через SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
sdk install gradle 8.9
gradle wrapper
```

---

## Способ 1. Запуск через Gradle (рекомендуется для разработки)

Самый удобный режим для локальной работы — hot reload шаблонов Thymeleaf, прозрачный пересбор при изменениях.

```bash
# Опционально: задать AI-ключи (без них работает режим флешкарт)
export OPENAI_API_KEY=sk-...               # либо
export DEEPSEEK_API_KEY=sk-...

# Запуск
./gradlew bootRun
```

**Перед первым `./gradlew bootRun` подними PostgreSQL** (он нужен всегда):

```bash
docker compose up -d postgres
# или свой локальный postgres:
# createdb -O interview interview
```

**Что происходит при старте приложения:**

1. Gradle скачивает зависимости (первый запуск — 1–3 минуты).
2. Стартует модуль `quiz-app` на порту 8080, подключается к PostgreSQL.
3. Flyway применяет миграции из `modules/quiz-persistence/src/main/resources/db/migration/`.
4. Выполняется первичный импорт markdown-вопросов из `cheatsheets/interview/` + MCQ-сидов из `seed/mcq/**.json` в БД.
5. При `INTERVIEW_RESET_ON_STARTUP=true` сначала TRUNCATE-ит вопросы/опции, потом переимпорт — удобно после правки слагов или удаления тем.

**Запуск с дополнительными параметрами:**

```bash
# Сменить порт
SERVER_PORT=9090 ./gradlew bootRun

# Включить предзагрузку вариантов на старте (требует AI-ключ)
PRELOAD_STARTUP_PRELOAD=true PRELOAD_FULL_WARMUP=true ./gradlew bootRun

# Передать аргументы Spring Boot
./gradlew bootRun --args="--spring.profiles.active=prod --server.port=9090"

# Запуск с другим JVM (например, для дебага)
./gradlew bootRun --debug-jvm   # подключитесь дебагером к порту 5005
```

**Остановка:** `Ctrl+C` в терминале.

---

## Способ 2. Запуск собранного JAR

Подходит для эксплуатации (production) и стейджинговых установок.

```bash
# 1. Собрать fat-JAR
./gradlew :quiz-app:bootJar

# 2. Запустить
java -jar modules/quiz-app/build/libs/quiz-app-0.0.1-SNAPSHOT.jar
```

**С переменными окружения и профилем:**

```bash
SPRING_PROFILES_ACTIVE=prod \
OPENAI_API_KEY=sk-... \
APP_ADMIN_TOKEN=secret-token \
java -jar modules/quiz-app/build/libs/quiz-app-0.0.1-SNAPSHOT.jar
```

**Тонкая настройка JVM:**

```bash
java -Xms256m -Xmx512m \
     -XX:+UseG1GC \
     -Dserver.port=8081 \
     -jar modules/quiz-app/build/libs/quiz-app-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=prod
```

JAR содержит каталог `cheatsheets/` если вы соберёте через Docker. При запуске «голым» JAR убедитесь, что в рабочей директории есть `cheatsheets/interview/...` (путь задаётся в `app.interview-path`).

---

## Способ 3. Docker (одиночный контейнер)

Запускает **только приложение** — ему нужен доступный извне PostgreSQL (с
2026-05-25 единственная поддерживаемая БД, SQLite убран). Если хочешь поднять
БД и приложение одной командой — смотри **Способ 4** (Compose).

```bash
# 1. Сборка образа (в корне репо должна быть папка cheatsheets/)
docker build --build-arg VERSION=1.0.0 -t interview-prep .

# 2. Запуск против PostgreSQL на хосте
#    host.docker.internal — мост к хосту (Docker Desktop на macOS/Windows;
#    на Linux добавь --add-host=host.docker.internal:host-gateway)
docker run --rm \
  -p 8080:8080 \
  -e OPENAI_API_KEY="$OPENAI_API_KEY" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/interview" \
  -e SPRING_DATASOURCE_USERNAME=interview \
  -e SPRING_DATASOURCE_PASSWORD=interview \
  -e SPRING_PROFILES_ACTIVE=default \
  -v "$(pwd)/cheatsheets:/app/cheatsheets" \
  --name interview-prep \
  interview-prep
```

По умолчанию в `Dockerfile` стоит `ENV SPRING_PROFILES_ACTIVE=prod` (Swagger выключен, `APP_ADMIN_TOKEN` обязателен). Чтобы включить Swagger UI, перекрой профиль на `default`, как в примере выше.

Маунт `cheatsheets/` нужен, чтобы изменения в markdown-файлах подхватывались без пересборки образа.

---

## Способ 4. Docker Compose с PostgreSQL

Самый удобный путь для full-stack запуска: PostgreSQL + приложение в одной команде.

```bash
# 1. (опционально) сложить переменные в .env рядом с docker-compose.yml
cat > .env <<'EOF'
OPENAI_API_KEY=sk-...
AI_PROVIDER=openai
APP_ADMIN_TOKEN=local-secret
POSTGRES_USER=interview
POSTGRES_PASSWORD=interview
EOF

# 2. Поднять стек
docker compose up -d

# 3. Логи приложения
docker compose logs -f interview-prep

# 4. Остановить
docker compose down

# 5. Полный сброс (включая volume PostgreSQL)
docker compose down -v
```

**Что входит в стек (см. `docker-compose.yml`):**

- `postgres` — PostgreSQL 16-alpine, порт `5432`, volume `postgres-data`.
- `interview-prep` — приложение, порт `8080`, профиль `postgres`, healthcheck на `/actuator/health`.

**Пересборка после изменений в коде:**

```bash
docker compose build interview-prep --no-cache
docker compose up -d interview-prep
```

**Готовый шаблон env-файла** лежит в `.env.example` — скопируйте его в `.env` и заполните ключи. Compose читает `.env` автоматически.

---

## Способ 5. Запуск с PostgreSQL без Docker

Если у вас уже стоит PostgreSQL локально:

```bash
# 1. Создать БД и пользователя
psql -U postgres <<SQL
CREATE USER interview WITH PASSWORD 'interview';
CREATE DATABASE interview OWNER interview;
SQL

# 2. Запустить приложение (PostgreSQL — БД по умолчанию, отдельный профиль не нужен)
POSTGRES_HOST=localhost \
POSTGRES_PORT=5432 \
POSTGRES_DB=interview \
POSTGRES_USER=interview \
POSTGRES_PASSWORD=interview \
OPENAI_API_KEY="$OPENAI_API_KEY" \
./gradlew bootRun
```

Flyway автоматически применит миграции из `db/migration` при первом запуске.

---

## Режимы работы (источники MCQ)

### По умолчанию — seed-first (JSON-сидеры)

Варианты ответа берутся из `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`. AI **не вызывается в рантайме**, даже если задан API-ключ. Это поведение по умолчанию для всего проекта — реквесты к LLM делаются только когда вы сами это попросите.

- Если для вопроса есть seed-варианты → они отдаются как есть.
- Если нет → UI деградирует в режим флешкарт (ответ + кнопки самооценки SM-2: Снова / Сложно / Хорошо / Легко).
- Цена: 0 токенов, 0 латентности.

```bash
./gradlew bootRun         # без ключей и без AI — нормальный сценарий
```

### Опционально — on-demand AI fallback

Когда seed-варианта нет и вы хотите, чтобы LLM сгенерировал опции на лету — включите fallback:

```bash
export OPENAI_API_KEY=sk-...
export AI_FALLBACK_ENABLED=true        # без этого ключ не используется в рантайме
./gradlew bootRun
```

Только при `AI_FALLBACK_ENABLED=true` **и** заданном `OPENAI_API_KEY` / `DEEPSEEK_API_KEY` сервис обратится в LLM. Запросы кэшируются в БД и in-memory (Caffeine) — повторное открытие вопроса в LLM не идёт.

### Совсем без AI — режим флешкарт

Если ключи не заданы (или fallback выключен), `AppProperties.isAiEnabled()` вернёт false. MVC форсит `flashcardMode=true`, и пользователь видит вопрос + эталонный ответ из markdown без выбора вариантов.

---

## Переменные окружения

### Обязательные / часто используемые

| Переменная | Что задаёт | Пример |
|-----------|------------|--------|
| `SERVER_PORT` | Порт HTTP | `8080` |
| `SPRING_PROFILES_ACTIVE` | Профиль (`default`, `prod`) | `prod` |
| `AI_PROVIDER` | Основной AI: `openai` или `deepseek` | `openai` |
| `OPENAI_API_KEY` | Ключ OpenAI | `sk-...` |
| `OPENAI_MODEL` | Модель OpenAI | `gpt-4.1-mini` |
| `DEEPSEEK_API_KEY` | Ключ DeepSeek | `sk-...` |
| `APP_ADMIN_TOKEN` | Токен для admin-эндпоинтов; пусто = без проверки | `secret` |
| `AI_FALLBACK_ENABLED` | Разрешить on-demand AI-генерацию опций (по умолчанию `false` — seed-first) | `true` |
| `INTERVIEW_RESET_ON_STARTUP` | При `true` TRUNCATE-ит вопросы/опции на старте и переимпортирует из MD/JSON | `false` |

### PostgreSQL (единственная БД)

| Переменная | По умолчанию |
|-----------|--------------|
| `POSTGRES_HOST` | `localhost` |
| `POSTGRES_PORT` | `5432` |
| `POSTGRES_DB` | `interview` |
| `POSTGRES_USER` | `interview` |
| `POSTGRES_PASSWORD` | `interview` |
| `SPRING_DATASOURCE_URL` | (override полного URL, имеет приоритет) |
| `SPRING_DATASOURCE_USERNAME` | — |
| `SPRING_DATASOURCE_PASSWORD` | — |

### Предзагрузка вариантов

| Переменная | По умолчанию | Описание |
|-----------|--------------|----------|
| `PRELOAD_STARTUP_PRELOAD` | `false` | Запускать предзагрузку при старте |
| `PRELOAD_FULL_WARMUP` | `false` | Прогревать все вопросы |
| `PRELOAD_WARMUP_LIMIT` | `0` | Ограничение N вопросов (0 = все) |
| `PRELOAD_TEST_MODE` | `false` | Тестовый режим (10 вопросов) |
| `PRELOAD_WARMUP_RANDOM_SEED` | `-1` | seed для случайной выборки |

### Прочее

| Переменная | По умолчанию |
|-----------|--------------|
| `REGENERATE_RATE_LIMIT_PER_MINUTE` | `20` |
| `INTERVIEW_RESET_ON_STARTUP` | `false` (полный сброс БД при старте) |

Полный список — в `modules/quiz-app/src/main/resources/application.yml` и Javadoc класса `AppProperties`.

---

## Профили Spring

| Профиль | Назначение | Особенности |
|---------|-----------|-------------|
| `default` | Локальная разработка | PostgreSQL (через `docker compose up -d postgres`), Swagger UI **включён**, Thymeleaf cache **off** |
| `prod` | Production | Swagger UI **выключен** (security: API-карта не светится), Thymeleaf cache **on**, `APP_ADMIN_TOKEN` обязателен (fail-fast на старте) |
| `test` | Тесты | Testcontainers PostgreSQL, тестовые сидеры из `src/test/resources` |

Активация: `SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun` или env-var в Docker.

---

## Доступ к приложению

После старта откройте в браузере:

| URL | Назначение |
|-----|-----------|
| http://localhost:8080/ | Главная страница (текущий вопрос или старт сессии) |
| http://localhost:8080/stats | Статистика и mastery |
| http://localhost:8080/settings | Настройки и сброс данных |
| http://localhost:8080/swagger-ui.html | REST API (только в `default`) |
| http://localhost:8080/actuator/health | Healthcheck |

### Основные REST-эндпоинты

- `POST /start` / `POST /finish` — старт / завершение сессии экзамена.
- `POST /answer` — ответ (form-data).
- `POST /api/answer` — ответ (JSON, для AJAX).
- `POST /api/regenerate` — перегенерация вариантов.
- `POST /api/hint` — подсказка по уровню (1–3).
- `POST /api/favorite` — переключение «избранного».
- `GET /export?format=json|csv` — экспорт прогресса.
- `POST /api/admin/options/clear` — очистка банка вариантов (заголовок `X-Admin-Token`, если задан `app.adminToken`).

Пример:

```bash
curl -X POST http://localhost:8080/api/answer \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "questionId=1&optionId=2"
```

---

## Проверка работоспособности

После запуска прогоните smoke-тест:

```bash
# Healthcheck
curl -fsS http://localhost:8080/actuator/health
# Ожидаем: {"status":"UP"}

# Главная страница (HTTP 200)
curl -I http://localhost:8080/

# Если активен default-профиль — Swagger
curl -I http://localhost:8080/swagger-ui.html
```

Если health возвращает `DOWN` или 500 — см. раздел [Troubleshooting](#troubleshooting).

---

## Наблюдаемость (актуатор + метрики)

Эндпоинты `/actuator/health` и `/actuator/info` открыты по умолчанию. Чтобы
включить `/actuator/metrics` и видеть детали health-компонентов:

```bash
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics \
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always \
./gradlew bootRun
```

### Health-компоненты

| Компонент                | Что показывает                                                         |
|--------------------------|------------------------------------------------------------------------|
| `db`                     | Подключение к PostgreSQL.                                              |
| `liveness` / `readiness` | Spring-стандартные пробы для K8s/Docker (см. группы ниже).             |
| `seedCoverage`           | Доля вопросов с загруженными опциями (из JSON-сидеров). DOWN при <50%. |

**Группы liveness и readiness** настроены отдельно, чтобы K8s правильно реагировал:

| Группа       | Включает                              | Что произойдёт при DOWN                 |
|--------------|---------------------------------------|------------------------------------------|
| `liveness`   | `livenessState` (только JVM-сигнал)   | K8s рестартанёт под.                     |
| `readiness`  | `readinessState`, `db`, `seedCoverage`| K8s отрежет под от трафика, но не убьёт. |

То есть деградация БД или поломка сидеров **не вызывает рестарт** — оператор
видит «un-ready» и разбирается, а под живёт и доступен по `/actuator`.

Поломанный seed-импорт ловится `seedCoverage`: оператор увидит `DOWN`
с деталями `questionsTotal / questionsWithOptions / coverageRatio` до того,
как пользователи откроют флешкарты вместо MCQ.

### `/actuator/info` — состав образа

Поле `seeds` в `/actuator/info` содержит сводку bundled JSON-сидеров:

```json
{
  "build": { "version": "0.0.1-SNAPSHOT", "time": "..." },
  "seeds": {
    "totalTopics": 77,
    "byCategory": { "algorithms": 4, "api": 1, "behavioral": 2, "...": "..." }
  }
}
```

Подходит для diff между деплоями («новая категория `ai-ml` приехала?»)
и для быстрого ответа на вопрос «что вообще в этом контейнере».

### Бизнес-метрики (Micrometer)

| Метрика                                            | Назначение                                                                                            |
|----------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| `mcq.seed.topic.requests{result=found\|notfound}`  | Сколько тем запрошено и сколько нашли JSON-сид.                                                       |
| `mcq.seed.options.inserted`                        | Сумма опций, вставленных из сидеров за время жизни процесса.                                          |
| `mcq.seed.questions.skipped`                       | Seed-вопросы, для которых в БД нет соответствующей записи (обычно — рассинхрон сидера и markdown).    |
| `mcq.ai.fallback{outcome=suppressed\|called\|error}` | `suppressed` — seed-first сработал (норма). `called` — ушли в AI (только при `AI_FALLBACK_ENABLED=true`). `error` — AI-ответ упал. |
| `cache.gets{cache=optionCache,result=hit\|miss}`     | Caffeine cache hit/miss для готовых option-листов. Низкий hit ratio = много обращений в БД, есть смысл поднять `app.cache.option-max-size`. |
| `cache.puts{cache=optionCache}` / `cache.evictions{cache=optionCache}` | Сколько options попало в кэш / сколько вытеснилось (по max-size или TTL). |

Если `mcq.ai.fallback{outcome=called}` растёт без флага `AI_FALLBACK_ENABLED`
или `mcq.seed.questions.skipped` непустой — это сигнал расследовать.

---

## Управление данными

### База данных (PostgreSQL)

Локальная разработка — `docker compose up -d postgres` (см. [Способ 4](#способ-4-docker-compose-с-postgresql)). Volume сохраняется между рестартами.

Полный сброс БД при следующем старте — через флаг (TRUNCATE без пересоздания контейнера):

```bash
INTERVIEW_RESET_ON_STARTUP=true ./gradlew bootRun
```

Чистый снос с volume (например при сбое миграции):

```bash
docker compose down -v   # удаляет volume
docker compose up -d postgres
./gradlew bootRun        # Flyway раскатает схему заново
```

### Сброс банка AI-вариантов

Через UI: `Settings → Управление данными → Сбросить банк вариантов`.

Через API:

```bash
# Если APP_ADMIN_TOKEN не задан
curl -X POST http://localhost:8080/api/admin/options/clear

# Если задан
curl -X POST http://localhost:8080/api/admin/options/clear \
     -H "X-Admin-Token: $APP_ADMIN_TOKEN"
```

### Экспорт прогресса

```bash
curl -o progress.json "http://localhost:8080/export?format=json"
curl -o progress.csv  "http://localhost:8080/export?format=csv"
```

---

## Сборка и тесты

```bash
# Полная сборка с тестами
./gradlew build

# Только тесты
./gradlew test

# Один тестовый класс
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.SpacedRepetitionServiceTest"

# Compile без тестов
./gradlew :quiz-app:compileJava

# Build без тестов (быстрая локальная сборка)
./gradlew build -x test

# Coverage-проверка
./gradlew check

# JaCoCo report
./gradlew jacocoTestReport
# результат: modules/quiz-app/build/reports/jacoco/test/html/index.html

# Javadoc
./gradlew javadoc
# результат: modules/quiz-app/build/docs/javadoc/
```

---

## Структура проекта

```
cheat-sheet/
├── modules/
│   ├── quiz-domain/              # чистая доменная модель (без Spring)
│   ├── quiz-persistence/         # JDBC-репозитории, Flyway
│   └── quiz-app/                 # Spring Boot: API, MVC, AI, конфигурация
│       └── src/main/resources/
│           ├── application.yml
│           ├── application-prod.yml
│           ├── db/migration/             # Flyway-миграции PostgreSQL (в module quiz-persistence)
│           ├── prompts/                  # промпты для LLM
│           ├── templates/                # Thymeleaf
│           └── static/                   # CSS/JS
├── cheatsheets/                  # markdown-шпаргалки и вопросы
│   └── interview/                # источник вопросов для приложения
├── scripts/                      # сидеры, линтеры, утилиты для cheatsheets
├── docs/                         # инженерная документация
├── docker-compose.yml
├── Dockerfile
├── build.gradle.kts
└── settings.gradle.kts
```

Зависимости модулей:

| Модуль | Зависит от |
|--------|------------|
| `quiz-domain` | — |
| `quiz-persistence` | `quiz-domain` |
| `quiz-app` | `quiz-domain`, `quiz-persistence` |

Архитектурные правила enforced ArchUnit-тестом `LayeredArchitectureTest`:
- `domain` не зависит от api/service/persistence/config;
- `service` не зависит от controllers и security-классов;
- `persistence` не зависит от api.

---

## Troubleshooting

### `Could not find or load main class`

Не собран JAR. Выполните `./gradlew :quiz-app:bootJar`.

### `Address already in use: bind` на 8080

Порт занят. Сменить:

```bash
SERVER_PORT=9090 ./gradlew bootRun
# либо
./gradlew bootRun --args="--server.port=9090"
```

Или найти процесс и остановить:

```bash
lsof -i :8080
kill <PID>
```

### Приложение пишет «Вопросы недоступны»

Это значит, что директория `cheatsheets/interview/` не найдена. Проверьте:

```bash
ls cheatsheets/interview/   # должен показать markdown-файлы
```

При запуске JAR выполняйте `java -jar` из корня репо, чтобы относительный путь `cheatsheets/interview` совпал с `app.interview-path`.

### `FlywayException: Validate failed`

Схема БД ушла в рассинхрон с миграциями. Самый простой путь — пересоздать схему:

```bash
psql -U interview -d interview -c 'DROP SCHEMA public CASCADE; CREATE SCHEMA public;'
```

И перезапустить. В Docker Compose то же самое делает полный сброс с volume: `docker compose down -v`.

### Healthcheck `{"status":"DOWN"}`

Проверьте логи:

```bash
# Локально
tail -f modules/quiz-app/logs/*.log    # если настроено
# либо смотрите stdout terminal'а с bootRun

# Docker
docker compose logs -f interview-prep
```

Чаще всего — недоступная БД (PostgreSQL не поднялся / неверные креды) или Flyway-конфликт.

### `429 Too Many Requests` от OpenAI/DeepSeek

Сработал rate limit провайдера. Приложение делает до `app.ai.maxRetries` ретраев (по умолчанию 3) с экспоненциальной задержкой. Если сыпется постоянно:

- понизьте параллелизм предзагрузки (`PRELOAD_STARTUP_PRELOAD=false`);
- проверьте баланс / квоты у провайдера;
- увеличьте `app.ai.timeoutSeconds` (по умолчанию 60).

### Не работают AI-варианты, хотя ключ задан

1. Убедитесь, что переменная экспортирована в **той же** оболочке, откуда запускается `gradle`/`docker`:
   ```bash
   echo $OPENAI_API_KEY
   ```
2. Проверьте `app.ai-provider` (`AI_PROVIDER`): провайдер должен совпадать с тем, чей ключ задан.
3. В логах ищите строку `ai client: provider=...` и сообщения от `AbstractAiClient`.

### `gradle wrapper` падает с «Could not find gradle»

Установите системный Gradle (через Homebrew / SDKMAN) — см. [Первоначальная подготовка](#первоначальная-подготовка-репозитория).

### Контейнер не видит изменения в cheatsheets/

Замаунтите директорию (как в `docker-compose.yml`):

```bash
docker run -v "$(pwd)/cheatsheets:/app/cheatsheets" ...
```

---

## Дополнительная документация

- `docs/ARCHITECTURE.md` — архитектура и границы слоёв.
- `docs/DOMAIN.md` — доменная модель и жизненный цикл вопроса.
- `docs/LLM_PIPELINE.md` — LLM-pipeline, обработка ошибок провайдера.
- `docs/QUESTION_ENGINE.md` — pipeline генерации вопросов (single-call, strict JSON).
- `docs/API.md` — REST API контракты и примеры.
- `docs/SECURITY.md` — security-модель и hardening checklist.
- `docs/SESSION_LOGIC.md` — логика сессий и mastery.
- `docs/CONTRIBUTING.md` — расширение и тестирование.
- `ENGINEERING_CONTEXT.md` — текущий инженерный контекст.
- `CLAUDE.md` — инструкции для Claude Code (build/run, архитектурные правила).

## Безопасность (важно при публичном развёртывании)

Приложение по умолчанию рассчитано на **localhost**. Spring Security и CSRF-защита не подключены. POST-эндпоинты (`/answer`, `/api/answer`, `/api/regenerate`, `/api/hint`, `/api/favorite`, `/api/admin/*`) **не защищены CSRF-токенами**.

Для production-публикации:

- подключите Spring Security и включите CSRF;
- задайте `APP_ADMIN_TOKEN` для admin-эндпоинтов;
- разместите приложение за reverse proxy с TLS;
- задайте профиль `prod` (Swagger UI выключен).

---

## Лицензия

Внутренний проект, без явной лицензии. Использование и распространение — по согласованию с владельцем репозитория.
