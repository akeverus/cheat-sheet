# QUESTION_ENGINE

## Цели

Question engine отвечает за генерацию production-grade вопросов и поддерживает:

- детерминированный prompt-контракт;
- контроль сложности и когнитивной нагрузки;
- quality scoring;
- retry по нарушениям качества;
- anti-duplication на попытках и между запросами.

## Архитектура

Базовый pipeline:

1. `QuestionGenerationService` получает `topic` и `QuestionType`.
2. `AdaptiveDifficultyService` определяет `Difficulty`.
3. `QuestionPromptBuilder` собирает prompt по шаблону `AiPrompts.QUESTION_V2_PROMPT_TEMPLATE`.
4. `OptionGenerator.generateStructuredJson(...)` запрашивает LLM.
5. `QuestionValidationService` проверяет структуру/качество.
6. `QuestionGenerationPolicy` добавляет policy-нарушения:
   - лимиты когнитивной нагрузки;
   - duplicate/near-duplicate контроль.
7. При успехе fingerprint сохраняется в `QuestionUniquenessService`.
8. При провале выполняется retry с блоком `QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT`.

## Конфигурация

Все ключевые параметры вынесены в `app.interview.*`:

- `question-min-quality-score`
- `question-max-text-length`
- `question-max-options-total-text-length`
- `question-generation-max-attempts`
- `question-uniqueness-window-minutes`
- `question-uniqueness-max-fingerprints-per-key`
- `question-near-duplicate-similarity-threshold`

## Quality scoring

`QuestionValidationService` возвращает нарушения, которые затем конвертируются в score `0..100`.

- score `100` — без нарушений;
- серьёзные нарушения (например, multiple correct options) дают повышенный штраф;
- решение о приёме вопроса принимает `QuestionGenerationPolicy`.

## Анти-дублирование

Используются два уровня:

- **exact duplicate**: fingerprint вопроса + отсортированный fingerprint опций;
- **near duplicate**: Jaccard similarity по токенам текста вопроса.

Window uniqueness хранится в Caffeine-cache по ключу `topic::type`.

## Наблюдаемость

Question engine пишет:

- старт генерации (`topic`, `type`, `difficulty`);
- итог качества (`qualityScore`, `attempt`);
- причины отказа (`violations`).

Логи не должны включать sensitive payload (API-ключи, полный пользовательский ввод, сырые токены).
