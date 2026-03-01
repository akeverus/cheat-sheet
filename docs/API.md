# API

## Основные endpoint-ы

### `POST /api/answer`

Проверка ответа пользователя.

Request form fields:

- `questionId` (long)
- `optionId` (long)
- optional filter/session fields

Response (`AnswerResponse`):

- `correct`, `correctOptionId`, `selectedOptionId`
- `answerHtml`
- `optionExplanations`
- `session` (если есть активная сессия)

### `GET /api/next`

Возвращает следующий вопрос или `204 No Content`.

Поведение при деградации/ошибках:

- `503 AI_GENERATION_FAILED` — если AI-генерация недоступна для следующего вопроса.
- `400 VALIDATION_ERROR` — если query-параметры имеют неверный формат
  (например, `excludeQuestionId=not-a-number`).

### `GET /api/stats`

Возвращает агрегированную статистику:

```json
{
  "total": 120,
  "due": 21,
  "learned": 64,
  "correct": 430,
  "wrong": 97
}
```

### `GET /api/topic-stats`

Возвращает массив статистик по темам:

```json
[
  {
    "topic": "spring",
    "total": 22,
    "due": 6,
    "learned": 12,
    "correct": 51,
    "wrong": 9,
    "regenSum": 7
  }
]
```

### `POST /api/hint`

Запрос подсказки уровня 1..3.

### `POST /api/regenerate`

Сброс и регенерация опций/подсказок/диаграммы для вопроса.

Контракт доступа и ограничений:

- Требуется заголовок `X-Admin-Token`.
- При отсутствии/невалидном токене: `403 FORBIDDEN`.
- Включён per-client rate limit (по IP/X-Forwarded-For).
- При превышении лимита: `429 RATE_LIMIT_EXCEEDED` + header `Retry-After`.
- При несуществующем `questionId`: `404 QUESTION_NOT_FOUND`.

### `POST /api/confidence`

Обновление уверенности (grade 1..5).

Валидация:

- при `grade` вне диапазона `1..5` возвращается `400 VALIDATION_ERROR`.
- при нечисловом `grade` возвращается `400 VALIDATION_ERROR`.
- при нечисловом `questionId` возвращается `400 VALIDATION_ERROR`.
- при `questionId <= 0` возвращается `400 VALIDATION_ERROR`.

### `POST /api/wrong-feedback`

Генерация объяснения «почему выбранный вариант неверный».

Валидация:

- при `optionId <= 0` возвращается `400 VALIDATION_ERROR`.
- при нечисловом `optionId` возвращается `400 VALIDATION_ERROR`.
- при нечисловом `questionId` возвращается `400 VALIDATION_ERROR`.
- при несуществующем `questionId` возвращается `404 QUESTION_NOT_FOUND`.
- при несуществующем `optionId` возвращается `200` c `available=false` и `feedback=null`.

## MVC endpoint-ы (HTML)

### `GET /settings`

Возвращает страницу настроек сессии и фильтров.

Query params:

- `topic`, `group`
- `important`, `onlyWrong`, `shuffle`, `ordered`
- `weakTopics`
- `mode`

Примечание: параметры нормализуются в `InterviewFilter`, а флаг `weakTopics` и режим
собираются в `SettingsRequestContext`.

### `GET /stats`

Возвращает страницу аналитики и поиска.

Query params:

- `topic`, `group`
- `important`, `onlyWrong`, `ordered`
- `q` (поиск)

Примечание: фильтр + поисковый запрос собираются в `StatsRequestContext`.

### `GET /review`

Возвращает focus-view в review-режиме.

Особенность:

- применяется `ReviewModeService`, который принудительно включает `onlyWrong=true`.

## Ошибки

Формат ошибок:

```json
{
  "status": 404,
  "type": "QUESTION_NOT_FOUND",
  "message": "..."
}
```

Типовые error-type для API:

- `VALIDATION_ERROR` — некорректные параметры запроса (`400`), например:
  - `POST /api/hint` с `level` вне диапазона `1..3`;
  - `POST /api/answer` с `optionId <= 0`;
  - `GET /api/comparison` с `selectedOptionId <= 0`;
  - `GET /api/comparison` с нечисловым `selectedOptionId`;
  - `GET /api/comparison` с `questionId <= 0`;
  - `GET /api/comparison` с нечисловым `questionId`;
  - `GET /api/takeaway` с `questionId <= 0`;
  - `GET /api/takeaway` с нечисловым `questionId`;
  - `GET /api/code-trace` с `questionId <= 0`;
  - `GET /api/code-trace` с нечисловым `questionId`.
- `QUESTION_NOT_FOUND` / `OPTION_NOT_FOUND` — несуществующие сущности (`404`).
- `FORBIDDEN` — запрет доступа к чувствительному endpoint (`403`).
- `RATE_LIMIT_EXCEEDED` — превышение лимита запросов (`429`, с `Retry-After`).
- `AI_GENERATION_FAILED` — недоступна AI-генерация (`503`).
- `INTERNAL_ERROR` — непредвиденная ошибка (`500`).

## Совместимость

- URI endpoint-ов не изменены.
- Схема БД не менялась.

## Unit Coverage (Thin Controller)

`InterviewApiControllerUnitTest` покрывает thin-controller контракты на уровне
DTO-мэппинга, precondition guard и delegation:

- `/api/answer` — мэппинг `AnswerResponse`, связные вопросы, session payload.
- `/api/hint` — available/unavailable ветки + missing-question guard (`404` через exception).
- `/api/wrong-feedback` — available/unavailable ветки + missing-question guard.
- `/api/takeaway`, `/api/comparison`, `/api/code-trace` — present/empty payload + missing-question guard.
- `/api/next` — `204` ветка, payload-мэппинг, fallback `questionType=TEXT`,
  `codeSnippet/diagram`, review-state, нормализация фильтра и forwarding флагов.
- `/api/regenerate` — `403`, `429` (`Retry-After`), `200` success.
- `/api/stats`, `/api/topic-stats`, `/api/streak`, `/api/favorite` — корректное делегирование
  и мэппинг response DTO, включая exception propagation для `/api/favorite`.
