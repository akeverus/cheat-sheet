# ARCHITECTURE

## Архитектурный стиль

Проект построен как модульный layered monolith:

- `quiz-domain` — доменные модели и инварианты
- `quiz-persistence` — JDBC-репозитории и SQL-адаптеры
- `quiz-app` — web/API, use-case сервисы, LLM интеграция, конфигурация

Внутри `quiz-app` используются слои:

- `api.controller` — HTTP-контракты и маппинг DTO
- `api.mapper` — преобразование request/model state без бизнес-логики
- `service` — бизнес-оркестрация use-case
- `service.ai` и `llm` — генерация и парсинг LLM-ответов
- `persistence` — доступ к данным через репозитории
- `config` — инфраструктурные настройки и бины

## Текущая карта MVC orchestration

Web flow построен вокруг тонкого контроллера и выделенных orchestration-компонентов:

- `InterviewMvcController` — только прием HTTP-параметров, делегирование и возврат view/redirect.
- `MvcRequestMapper` — нормализация входных параметров и сборка:
  - `InterviewFilter`
  - `SettingsRequestContext`
  - `StatsRequestContext`
- `MvcAnswerRequestMapper` — распаковка `SubmitAnswerRequest` в плоский контракт service-вызова.
- `MvcModelAttributeMapper` — единая запись model-атрибутов для focus/result/stats/summary.
- `MvcNavigationService` — единый источник view names и redirect routes.
- `SessionFlowService` — жизненный цикл сессии (`/start`, `/finish`, study/flashcard переходы).
- `HttpSessionStateService` — типобезопасный доступ к состоянию `HttpSession`.

## Ответственности слоёв

- **controller**: валидация входа, HTTP-коды, сериализация/десериализация DTO.
- **service**: сценарии тренировки, review-цикл, выбор следующего вопроса.
- **repository**: SQL-запросы, агрегация статистики, сохранение состояния.
- **llm/service.ai**: построение промптов, отправка запросов, парсинг и quality gates.

## Границы зависимостей

Правило направленности зависимостей:

- `api.controller` -> `api.mapper` -> `service` -> `persistence`/`llm`
- `service` не зависит от `api.controller`
- DTO и HTTP-контракты не просачиваются в `domain`
- cross-cutting web security централизована в `config/SecurityConfig`.

Практика, применяемая в проекте:

- request DTO распаковываются в mapper-слое;
- service-методы принимают доменные/плоские аргументы, а не контроллерные типы;
- MVC шаблоны получают данные через централизованный model mapper.

## Потоки данных

1. UI/REST запрос -> controller.
2. Controller вызывает use-case сервис (`InterviewFacade`/`InterviewService`).
3. Сервис читает/пишет данные через repository.
4. Для генерации вопросов/опций сервис вызывает LLM-слой.
5. Controller возвращает API DTO или модель страницы.

## Взаимодействие с LLM

- Низкоуровневые контракты:
  - `llm/LlmClient.java`
  - `llm/LlmRequestBuilder.java`
  - `llm/LlmResponseParser.java`
- Оркестрация в `service.ai`:
  - `OptionGenerationService`
  - `QuestionGenerationService`
  - `QuestionValidationService`

Question engine построен вокруг policy-driven контура:

- `QuestionPromptBuilder` — детерминированная сборка prompt + constraints из `AppProperties`.
- `QuestionGenerationPolicy` — acceptance policy, cognitive-load guard, near-duplicate контроль.
- `QuestionUniquenessService` — Caffeine window uniqueness по `topic::type`.
- `QuestionGenerationService` — retry orchestration и quality logging.

Для admin-управления приоритетами senior-правил выделено отдельное runtime-хранилище:

- `SeniorRulePriorityOverrideStore` — единый concurrent source of truth для override-map;
- `AdminSeniorRulesService` и `OptionGenerationService` работают с одной и той же store-ссылкой без прямой мутации `AppProperties`.

## Адаптивная сложность

- `AdaptiveDifficultyService` определяет текущий уровень сложности по mastery темы.
- Контекст сложности используется при генерации distractor-ов.

## Review Mode

- `ReviewService` обновляет SM-2 состояние (`ReviewState`) после ответа.
- `ReviewModeService` форсирует `onlyWrong=true` для `GET /review`.
- `TrainingSessionService` управляет сессией и штрафными вопросами в EXAM режиме.
