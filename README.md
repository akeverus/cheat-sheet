# Подготовка к собеседованию (Interview Prep)

Локальное Spring Boot приложение для тренировки перед техническим интервью: вопросы из markdown-шпаргалок, варианты ответа из JSON-сидеров, интервальное повторение по SM-2 (опционально FSRS-4.5), статистика освоенности тем и Mermaid-диаграммы.

> **AI в проекте нет.** Ни ключей, ни провайдеров, ни флага, которым генерацию можно
> включить, — это свойство кода, а не конфигурации. Варианты ответа приходят только
> из seed-JSON.

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
- [Источник MCQ-вариантов](#источник-mcq-вариантов)
- [Переменные окружения](#переменные-окружения)
- [Профили Spring](#профили-spring)
- [Доступ к приложению](#доступ-к-приложению)
- [Как пользоваться приложением](#как-пользоваться-приложением)
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

Откройте **http://localhost:8080**. MCQ берутся из `seed/mcq/**.json`. Чтобы перезалить
вопросы — `INTERVIEW_RESET_ON_STARTUP=true docker compose up -d --force-recreate interview-prep`.

---

## Возможности

- **310+ тем** из 25 категорий (JVM, базы данных, system design, фреймворки, алгоритмы, security, messaging и т.д.) — вопросы импортируются из markdown-шпаргалок `cheatsheets/interview/`.
- **8 режимов сессии** (`InterviewMode`): Тренировка (бесконечно, по одному вопросу), Изучение (по порядку, LEARN → QUIZ), Флешкарты (самооценка), Экзамен (20 вопросов + итог), Интенсив (50 вопросов + итог), Активное припоминание, Интервью, Код-челлендж.
- **Интервальное повторение:** SM-2 по умолчанию, переключаемый FSRS-4.5 (`SCHEDULING_ALGORITHM`), расчёт даты следующего повторения и mastery-статистика, виджет «повторить сегодня».
- **MCQ-варианты:** 4 опции (1 правильный + 3 дистрактора) из seed-сидеров; без сида — режим флешкарт с эталонным ответом и самооценкой.
- **Персонализация интерфейса (клиентская, без бэкенда):** 7 осей — тема (светлая/тёмная/авто), раскладка страницы «Фокус», размер шрифта, ширина строки, плотность отступов, управление анимацией, интенсивность пиксель-эффектов. Всё сохраняется в `localStorage`. Дизайн один (`instrument`) — переключателя дизайнов больше нет.
- **Доступность и клавиатура:** полная клавиатурная навигация (`1–9`, `Enter`, стрелки), справка по `?`, ARIA-роли, режим без JavaScript (progressive enhancement).
- **Полнотекстовый поиск:** PostgreSQL `tsvector`.
- **Экспорт прогресса:** JSON и CSV (`/export?format=json|csv`), закрыт admin-гейтом.
- **Сброс банка вариантов:** кнопка на `/settings` (вкладка «Данные») → `POST /settings/reset-options`, тоже под admin-гейтом.

---

## Требования

| Компонент | Версия | Зачем |
|-----------|--------|-------|
| **JDK** | 17+ | Сборка и запуск |
| **Gradle** | 8.x (или wrapper) | Сборка |
| **PostgreSQL** | 14+ | Единственная поддерживаемая БД (локально через docker compose) |
| **Docker / Docker Compose** | актуальная | Нужен для запуска БД и для прогонов тестов (Testcontainers) |

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
- `interview-prep` — приложение, порт `8080`, профиль `prod` (Swagger UI выключен, `APP_ADMIN_TOKEN` обязателен), healthcheck на `/actuator/health`.

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
./gradlew bootRun
```

Flyway автоматически применит миграции из `db/migration` при первом запуске.

---

## Источник MCQ-вариантов

Единственный источник — JSON-сидеры
`modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`, загружаются на
старте (`McqJsonLoader`). Runtime-генерации нет и включить её нечем: AI-код вырезан из
проекта целиком, конфигурационного флага не существует (см.
[`docs/backend-architecture.md`](docs/backend-architecture.md) §6).

- Есть seed-варианты для вопроса → они отдаются как есть.
- Нет → `OptionLookupService` возвращает пустой список, MVC форсит `flashcardMode=true`,
  и вопрос показывается флешкартой: эталонный ответ из markdown + самооценка SM-2
  (Снова / Сложно / Хорошо / Легко). Это штатное поведение, а не деградация из-за сбоя.

Покрытие сидерами видно в `/actuator/health` (`SeedCoverageHealthIndicator`) и
`/actuator/info` (`SeedInfoContributor`). Варианты кэшируются in-memory (Caffeine,
`app.cache.option-max-size` / `ttl-hours`).

---

## Переменные окружения

### Обязательные / часто используемые

| Переменная | Что задаёт | Пример |
|-----------|------------|--------|
| `SERVER_PORT` | Порт HTTP | `8080` |
| `SPRING_PROFILES_ACTIVE` | Профиль (`default`, `prod`) | `prod` |
| `APP_ADMIN_TOKEN` | Токен для чувствительных эндпоинтов (`/export`, `/settings/reset-options`); пусто = гейт закрыт наглухо | `secret` |
| `SCHEDULING_ALGORITHM` | Алгоритм повторений: `sm2` или `fsrs` | `sm2` |
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

### Прочее

| Переменная | По умолчанию |
|-----------|--------------|
| `SENSITIVE_RATE_LIMIT_PER_MINUTE` | `20` (обращений к `/export` и `/settings/reset-options` на клиента в минуту) |
| `APP_SECURITY_MODE` | `local` (однопользовательский режим; `multi` включает OAuth2 + owner-скоупинг) |

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

MVC (отдают HTML, форма + CSRF-токен):

- `POST /start` / `POST /finish` — старт / завершение сессии.
- `POST /answer` — ответ (form-data), no-JS-фоллбэк AJAX-пути.
- `POST /pause` / `POST /resume` / `POST /skip` — управление сессией.
- `POST /flashcard-reveal` / `POST /flashcard-grade` — раскрытие ответа и самооценка (SM-2).
- `POST /study-confirm` — переход LEARN → QUIZ в режиме «Изучение».
- `POST /settings/reset-options` — сброс банка вариантов (закрыт admin-гейтом).

REST (JSON):

- `GET /api/next` — следующий вопрос.
- `POST /api/answer` — ответ (JSON, для AJAX).
- `POST /api/confidence` — оценка уверенности (1–5).
- `POST /api/favorite` — переключение «избранного».
- `POST /api/bookmark`, `/api/bookmark/note`, `/api/bookmark/remove` — закладки.
- `POST /api/report` — жалоба на вопрос.
- `GET /api/stats`, `/api/topic-stats`, `/api/streak` — статистика.
- `GET /export?format=json|csv` — экспорт прогресса (закрыт admin-гейтом).

Полный контракт — Swagger UI на `/swagger-ui.html`.

Пример (тело — JSON; CSRF-токен обязателен, браузерный клиент подставляет его
автоматически из cookie `XSRF-TOKEN`):

```bash
curl -X POST http://localhost:8080/api/answer \
  -H "Content-Type: application/json" \
  -H "X-XSRF-TOKEN: $TOKEN" -b "XSRF-TOKEN=$TOKEN" \
  -d '{"questionId":1,"optionId":2}'
```

---

## Как пользоваться приложением

Раздел про то, **как заниматься** в приложении после запуска (UI на `http://localhost:8080`). Бэкенд для UI не нужен сверх того, что уже поднято — персонализация и горячие клавиши работают целиком на клиенте.

### 1. Первый заход

1. Откройте `http://localhost:8080/`. Вы увидите либо текущий вопрос, либо приглашение начать сессию.
2. Тумблер темы (светлая/тёмная) — в шапке. Полный набор настроек вида — на `/settings` (вкладка «Оформление»).
3. Выберите режим занятия на `/settings` → вкладка «Сессия» и нажмите «Начать».

### 2. Режимы сессии

Режим выбирается на `/settings` (вкладка «Сессия», селектор «Режим»). От него зависит, сколько вопросов в подходе и будет ли итоговый экран.

| Режим | Что делает | Длина | Итоговый экран |
|-------|------------|-------|----------------|
| **Тренировка** | Бесконечная практика по одному вопросу, без «подхода». CTA называется «Начать тренировку». | ∞ | нет |
| **Изучение** | Прохождение тем по порядку (учебная последовательность). | настраивается | да |
| **Флешкарты** | Вопрос → эталонный ответ → самооценка по SM-2 (без выбора вариантов). | настраивается | да |
| **Экзамен** | Фиксированный подход с подсчётом результата. | по умолчанию 20 | да |
| **Интенсив** | Длинный подход для марафонского прогона. | по умолчанию 50 | да |
| **Активное припоминание** | Ответ формулируется своими словами, затем сверяется с эталоном. | настраивается | да |
| **Интервью** | Прогон в формате собеседования. | настраивается | да |
| **Код-челлендж** | Вопросы с кодом. | настраивается | да |

Дополнительно во вкладке «Сессия» можно сузить выборку: **группа** (категория), **тема**, флаги «только важные», «только ошибочные», «перемешать», «слабые темы», и порядок **Учебный / Свободный**. Поле «Вопросов» (1–200) задаёт длину подхода для режимов с фиксированной длиной.

### 3. Как отвечать на вопрос (страница «Фокус»)

1. Прочитайте вопрос (крупный блок сверху).
2. Выберите вариант: клик мышью или клавиши `1`–`9` (стрелками `↑`/`↓` можно перемещаться по списку).
3. Отправьте ответ: кнопкой или `Enter`.
4. Появится **вердикт** (верно/неверно) с подсветкой правильного варианта, **самооценка уверенности** и блок **пост-разбора** (вывод, разбор, связанные темы) под кнопкой.
5. Перейдите к следующему вопросу. Вопрос можно отметить **избранным**, положить в **закладки** с заметкой или пожаловаться на него (`/api/report`).

### 4. Режим флешкарт и интервальное повторение

Если для вопроса нет seed-сида с вариантами или выбран режим «Флешкарты», вместо вариантов показывается эталонный ответ и кнопки **самооценки**:

| Кнопка | Клавиша | Смысл (SM-2) |
|--------|---------|--------------|
| Снова | `1` | не вспомнил — повторить скоро |
| Сложно | `2` | вспомнил с трудом |
| Хорошо | `3` | вспомнил нормально |
| Легко | `4` | знаю уверенно — следующий показ нескоро |

Оценка кормит алгоритм **SM-2**: он вычисляет дату следующего повторения. Виджет «повторить сегодня» и страница `/stats` показывают, что подошло к повторению.

### 5. Персонализация (`/settings` → вкладка «Оформление»)

Семь независимых осей. Все применяются мгновенно и сохраняются в `localStorage` (привязаны к браузеру, не к серверу). Значение по умолчанию = «как будто настройка не трогалась».

| Ось | Значения | Что меняет |
|-----|----------|------------|
| **Тема оформления** | авто / светлая / тёмная | Светлый или тёмный режим (авто = по системе) |
| **Размер шрифта** | 0.85×–1.4× | Масштаб всего интерфейса |
| **Раскладка** | поток / два пейна / сетка | Композиция страницы «Тренировка» |
| **Ширина чтения** | стандартная / широкая | Длина строки прозы |
| **Плотность** | просторно / компактно | Величина отступов (текст не трогается) |
| **Движение** | авто / включено / выключено | Анимации (авто = уважать `prefers-reduced-motion`) |
| **Пиксель-эффекты** | низкая / средняя / высокая | Интенсивность arcade-декора (иконки, рамки, анимации) |

Оси «Дизайн» больше нет: `instrument` — единственный дизайн, а выбор из одиннадцати
значений, десять из которых коэрсились обратно в него, вводил в заблуждение.
В шапке продублирован только тумблер темы — самое частое переключение.

### 6. Настройки: три вкладки

Страница `/settings` разбита на вкладки (клавиатура: стрелки / `Home` / `End`):

- **Сессия** — фильтры выборки, режим, длина подхода, старт/финиш, прогресс (streak + статистика).
- **Оформление** — семь осей персонализации из таблицы выше.
- **Данные** — экспорт прогресса (JSON/CSV) и сброс данных.

Без JavaScript вкладки не схлопываются — все три блока показываются стопкой (graceful degradation).

### 7. Горячие клавиши

| Клавиши | Действие |
|---------|----------|
| `1`–`9` | Выбрать вариант ответа |
| `↑` / `↓` | Перемещаться по вариантам |
| `Enter` | Отправить ответ |
| `1`–`4` | Оценить флешкарту (не помню → отлично) |
| `?` | Показать / скрыть справку по горячим клавишам |

### 8. Статистика (`/stats`)

Показывает mastery по темам, прогресс, серию (streak) и то, что подошло к повторению по SM-2. Прогресс можно выгрузить (`/export?format=json|csv`) — см. [Управление данными](#управление-данными).

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
    "totalTopics": 310,
    "byCategory": { "algorithms": 4, "api": 1, "behavioral": 2, "jvm": 18, "...": "..." }
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
| `cache.gets{cache=optionCache,result=hit\|miss}`     | Caffeine cache hit/miss для готовых option-листов. Низкий hit ratio = много обращений в БД, есть смысл поднять `app.cache.option-max-size`. |
| `cache.puts{cache=optionCache}` / `cache.evictions{cache=optionCache}` | Сколько options попало в кэш / сколько вытеснилось (по max-size или TTL). |

Ненулевой `mcq.seed.questions.skipped` — сигнал расследовать: сидер и markdown
разъехались, и часть вопросов молча осталась без вариантов.

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

### Сброс банка MCQ-вариантов

Операция необратима: вычищает загруженные из сидеров варианты, после чего вопросы
показываются флешкартами до следующего импорта.

Через UI: `/settings` → вкладка **«Данные»** → «Сбросить банк вариантов». Вкладка
появляется только при открытом admin-гейте — если `APP_ADMIN_TOKEN` не задан, её нет
вовсе (кнопка, которая гарантированно вернёт 403, хуже отсутствующей).

Через API — токен только заголовком или полем формы, в query string он отвергается:

```bash
curl -X POST http://localhost:8080/settings/reset-options \
     -H "X-Admin-Token: $APP_ADMIN_TOKEN"
```

### Экспорт прогресса

```bash
curl -H "X-Admin-Token: $APP_ADMIN_TOKEN" \
     -o progress.json "http://localhost:8080/export?format=json"
curl -H "X-Admin-Token: $APP_ADMIN_TOKEN" \
     -o progress.csv  "http://localhost:8080/export?format=csv"
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

**Тестам нужен запущенный Docker** — persistence и часть `quiz-app` крутятся на
Testcontainers PostgreSQL 16-alpine. На **OrbStack** Testcontainers 1.19.8 договаривается
на Docker API 1.32, а демон отдаёт ≥1.40, и без обхода падают все интеграционные тесты:

```bash
JAVA_TOOL_OPTIONS=-Dapi.version=1.41 \
DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock \
./gradlew check --no-daemon
```

**Не запускайте Gradle при живом `./gradlew bootRun`** — сборка встанет на блокировке.
Сначала остановите приложение.

---

## Структура проекта

```
cheat-sheet/
├── modules/
│   ├── quiz-domain/              # чистая доменная модель (без Spring)
│   ├── quiz-persistence/         # JDBC-репозитории + Flyway (src/main/resources/db/migration/)
│   └── quiz-app/                 # Spring Boot: API, MVC, конфигурация
│       └── src/main/resources/
│           ├── application.yml
│           ├── application-prod.yml
│           ├── seed/mcq/                 # JSON-сидеры MCQ-вариантов
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

### `429 Too Many Requests` на `/export` или `/settings/reset-options`

Сработал собственный rate limit приложения (`SENSITIVE_RATE_LIMIT_PER_MINUTE`, по
умолчанию 20 обращений в минуту на клиента). Ответ несёт заголовок `Retry-After` с
числом секунд — UI показывает его текстом. Поднимите лимит переменной окружения, если
экспорт нужен чаще.

### Вопрос показывается без вариантов ответа

Это не сбой: для темы нет JSON-сидера, и вопрос штатно вырождается во флешкарту.
Проверить покрытие — `curl localhost:8080/actuator/health` (секция `seedCoverage`).
Если сидер есть, но варианты не появились, смотрите метрику
`mcq.seed.questions.skipped`: скорее всего `q_number` в JSON разъехался с `## Q<N>`
в markdown.

### `403` на `/export`, хотя токен передан

Токен принимается только заголовком `X-Admin-Token` или полем формы `adminToken`.
В query string он отвергается намеренно (CWE-598) — см.
[Безопасность](#безопасность-важно-при-публичном-развёртывании).

### `gradle wrapper` падает с «Could not find gradle»

Установите системный Gradle (через Homebrew / SDKMAN) — см. [Первоначальная подготовка](#первоначальная-подготовка-репозитория).

### Контейнер не видит изменения в cheatsheets/

Замаунтите директорию (как в `docker-compose.yml`):

```bash
docker run -v "$(pwd)/cheatsheets:/app/cheatsheets" ...
```

---

## Дополнительная документация

- [`CLAUDE.md`](CLAUDE.md) — инструкции для Claude Code: build/run, правила слоёв (ArchUnit), источник вопросов, ключевая конфигурация.
- [`docs/product-redesign-state.md`](docs/product-redesign-state.md) — что за продукт, экраны, режимы сессии, принятые решения.
- [`docs/backend-architecture.md`](docs/backend-architecture.md) — модули, пакеты, обработка ошибок, безопасность, идемпотентность.
- [`docs/frontend-architecture.md`](docs/frontend-architecture.md) — шаблоны, CSS-слои, ES-модули, прогрессивное улучшение.
- [`docs/design-system.md`](docs/design-system.md) — токены, `@layer`-каскад, компоненты.
- [`docs/visual-assets.md`](docs/visual-assets.md) — инвентарь графики, происхождение, что удалено и почему.
- [`docs/accessibility.md`](docs/accessibility.md) — контракты доступности и чем они удерживаются.
- [`docs/quality-report.md`](docs/quality-report.md) — что проверено и чем, известные ограничения.
- [`DESIGN.md`](DESIGN.md) — историческая дизайн-документация UI.
- [Структура проекта](#структура-проекта) — раскладка модулей и пакетов (раздел этого README).
- **Swagger UI** — `/swagger-ui.html` при запущенном приложении: живые REST-контракты и примеры.

## Безопасность (важно при публичном развёртывании)

Приложение по умолчанию рассчитано на **localhost** и однопользовательский режим
(`app.security.mode=local`) — аутентификации нет, весь прогресс принадлежит одному
владельцу. Что уже включено:

- **Spring Security подключён**, CSRF-защита активна для всех мутирующих запросов
  (cookie `XSRF-TOKEN` → заголовок `X-XSRF-TOKEN`); исключение — `/export`, закрытый
  собственным admin-гейтом.
- **Admin-гейт** на чувствительных эндпоинтах (`/export`, `/settings/reset-options`):
  заголовок `X-Admin-Token` либо скрытое поле формы `adminToken`, значение — из
  `APP_ADMIN_TOKEN`. Токен в query string **отвергается** (CWE-598: секрет осел бы
  в access-логах, `Referer` и истории браузера).
- **CSP с nonce**, без `unsafe-inline`; все vendor-библиотеки и шрифты self-hosted,
  внешних CDN нет.
- **Rate limit** на чувствительных эндпоинтах — `SENSITIVE_RATE_LIMIT_PER_MINUTE`.

Для публичного развёртывания дополнительно:

- задайте `APP_ADMIN_TOKEN` — без него admin-гейт закрыт наглухо и вкладка «Данные»
  в настройках не выводится вовсе;
- разместите приложение за reverse proxy с TLS;
- задайте профиль `prod` (Swagger UI выключен);
- для многопользовательского сценария — `app.security.mode=multi` (OAuth2 +
  owner-скоупинг). Режим реализован, но живой проверкой не покрыт.

---

## Лицензия

Внутренний проект, без явной лицензии. Использование и распространение — по согласованию с владельцем репозитория.
