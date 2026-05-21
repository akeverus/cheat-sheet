# Design: MCQ как JSON-сидеры (v3)

**Дата:** 2026-05-20
**Статус:** approved для имплементации
**Скоп:** структура хранения MCQ; парсер `MarkdownQuestionParser`; новый компонент `McqJsonLoader`; миграция 243 файлов

## Контекст

Текущий формат смешивает теорию и MCQ в одном `.md` через callout `> [!mcq]`. Это создаёт несколько проблем:

- **Cognitive load при чтении** — callout-блоки прерывают теоретический поток.
- **Парсер на regex** — хрупко, сложно поддерживать.
- **Mixing presentation и data** — markdown это формат документации, MCQ это структурированные данные с типизированными полями (options array, correct flag, sections).
- **Три легаси-формата** параллельно (v1 эмодзи-маркеры + pipe; v2 inline named-секции; новый v3) — невозможно понять источник правды.

## Решение

**MCQ хранятся как JSON-сидеры в `modules/quiz-app/src/main/resources/seed/mcq/` зеркально структуре `cheatsheets/interview/`. Markdown файлы содержат только теорию — никаких `> [!mcq]` блоков.**

### File layout

```
cheatsheets/interview/cloud/aws-interview.md
   ↕ (парная связь по имени файла)
modules/quiz-app/src/main/resources/seed/mcq/cloud/aws-interview.json
```

- `.md` — теория: `## Q1, ## Q2, ...`, без MCQ.
- `.json` — структурированные MCQ data для seed в БД при старте.
- Парная связь: `<category>/<topic-slug>.md` ↔ `<category>/<topic-slug>.json`.
- Темы без JSON-сидера → quiz-app показывает flashcard mode (options = []).
- JSON-сидер для несуществующего .md → import warning, skip.

### JSON-формат

```json
{
  "topic_slug": "aws-interview",
  "questions": [
    {
      "q_number": 5,
      "blocks": [
        {
          "block_idx": 0,
          "question_text": "Что верно про ApplicationEvent?",
          "options": [
            {
              "order": 0,
              "label": "A",
              "text": "Любой объект может быть события без интерфейса (с Spring 4.2+)",
              "correct": true,
              "sections": {
                "explanation": "механика концепта, 3–7 предложений",
                "example": "конкретный production-кейс, inline `code`",
                "when_to_apply": "сценарии, компании, требования",
                "edge_cases": "edge cases при правильном использовании",
                "related": "[[aws-interview#Q2]] regions/AZ; [[aws-interview#Q12]] event types"
              }
            },
            {
              "order": 1,
              "label": "B",
              "text": "Только классы, наследующие ApplicationEvent",
              "correct": false,
              "sections": {
                "what_actually": "реальная семантика, 2–4 предложения",
                "source_of_confusion": "корень misconception",
                "if_it_were_true": "конкретный technical symptom или post-mortem",
                "how_it_should_be": "мост к correct (одно предложение)"
              }
            },
            {
              "order": 2,
              "label": "C",
              "text": "...",
              "correct": false,
              "sections": { "what_actually": "...", "source_of_confusion": "...", "if_it_were_true": "...", "how_it_should_be": "..." }
            },
            {
              "order": 3,
              "label": "D",
              "text": "...",
              "correct": false,
              "sections": { "what_actually": "...", "source_of_confusion": "...", "if_it_were_true": "...", "how_it_should_be": "..." }
            }
          ]
        }
      ]
    }
  ]
}
```

**Правила:**

- `topic_slug` совпадает с basename `.md` файла без расширения (валидируется при загрузке).
- `q_number` ссылается на `## Q<N>` в parallel `.md` (при импорте проверяется существование).
- `blocks` — массив (multi-block поддержан через V15 миграцию БД, `mcq_block_idx`).
- `block_idx` — 0-based индекс внутри Q.
- `options` — ровно 4 элемента (1× correct + 3× wrong).
- `order` — 0-based порядок отображения.
- `label` — `A`, `B`, `C`, или `D` (соответствует `order`).
- `correct` — boolean, ровно один `true` на блок.
- `sections` — типизированные ключи:
  - Для correct (5 обязательных): `explanation`, `example`, `when_to_apply`, `edge_cases`, `related`.
  - Для wrong (4 обязательных): `what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`.
  - Значения — markdown-строки (могут содержать `**bold**`, `[[links]]`, inline `code`, переносы строк).

### JSON Schema

Файл: `modules/quiz-app/src/main/resources/seed/mcq-schema.json`.

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["topic_slug", "questions"],
  "additionalProperties": false,
  "properties": {
    "topic_slug": { "type": "string", "pattern": "^[a-z][a-z0-9-]*$" },
    "questions": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["q_number", "blocks"],
        "additionalProperties": false,
        "properties": {
          "q_number": { "type": "integer", "minimum": 1 },
          "blocks": {
            "type": "array",
            "minItems": 1,
            "items": {
              "type": "object",
              "required": ["block_idx", "question_text", "options"],
              "additionalProperties": false,
              "properties": {
                "block_idx": { "type": "integer", "minimum": 0 },
                "question_text": { "type": "string", "minLength": 1 },
                "options": {
                  "type": "array",
                  "minItems": 4,
                  "maxItems": 4,
                  "items": {
                    "oneOf": [
                      {
                        "type": "object",
                        "required": ["order", "label", "text", "correct", "sections"],
                        "additionalProperties": false,
                        "properties": {
                          "order": { "type": "integer", "minimum": 0, "maximum": 3 },
                          "label": { "enum": ["A", "B", "C", "D"] },
                          "text": { "type": "string", "minLength": 1 },
                          "correct": { "const": true },
                          "sections": {
                            "type": "object",
                            "required": ["explanation", "example", "when_to_apply", "edge_cases", "related"],
                            "additionalProperties": false,
                            "properties": {
                              "explanation": { "type": "string", "minLength": 1 },
                              "example": { "type": "string", "minLength": 1 },
                              "when_to_apply": { "type": "string", "minLength": 1 },
                              "edge_cases": { "type": "string", "minLength": 1 },
                              "related": { "type": "string", "minLength": 1 }
                            }
                          }
                        }
                      },
                      {
                        "type": "object",
                        "required": ["order", "label", "text", "correct", "sections"],
                        "additionalProperties": false,
                        "properties": {
                          "order": { "type": "integer", "minimum": 0, "maximum": 3 },
                          "label": { "enum": ["A", "B", "C", "D"] },
                          "text": { "type": "string", "minLength": 1 },
                          "correct": { "const": false },
                          "sections": {
                            "type": "object",
                            "required": ["what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be"],
                            "additionalProperties": false,
                            "properties": {
                              "what_actually": { "type": "string", "minLength": 1 },
                              "source_of_confusion": { "type": "string", "minLength": 1 },
                              "if_it_were_true": { "type": "string", "minLength": 1 },
                              "how_it_should_be": { "type": "string", "minLength": 1 }
                            }
                          }
                        }
                      }
                    ]
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```

### Архитектура загрузки

```
[startup]
   ↓
QuestionImportService.importAll()
   ↓
   ├── MarkdownQuestionParser.parse(.md) → List<ParsedQuestion>  (без options)
   ↓
   ├── upsertQuestions(...) → question_id для каждого Q
   ↓
   └── McqJsonLoader.loadForTopic(slug)
         ↓
         читает modules/quiz-app/src/main/resources/seed/mcq/<category>/<slug>.json
         ↓
         JSON Schema validate
         ↓
         для каждого q_number → находит question_id (по slug + number)
         ↓
         удаляет старые options
         ↓
         вставляет options с mcq_block_idx
```

### Изменения в парсере `MarkdownQuestionParser`

**Удалить:**

- `MCQ_CALLOUT_START` regex и связанная state machine.
- `MCQ_OPTION_LINE` regex.
- `MCQ_SECTION_LINE` regex.
- `MCQ_CONTINUATION_LINE` regex.
- `OptionBuilder` inner class.
- `extractMcqFromAnswer` метод.
- `ParsedOption` record и поле `options` из `ParsedQuestion` (или оставить как пустой список для backward compat).
- Logic про `mcqBlockIdx` в парсере (теперь в JSON-стороне).

**Оставить:**

- `QUESTION_PATTERN` и парсинг `## Q<N>` headings.
- `extractCode` для code-блоков в ответе.
- `normalizeQuestionText`, frontmatter parsing, slug derivation.

**Результат:** парсер становится ~70 строк короче, чище. Регексы регулярные, без callout-acrobatics.

### Новый компонент `McqJsonLoader`

Файл: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqJsonLoader.java`.

```java
@Component
@RequiredArgsConstructor
public class McqJsonLoader {
    private final ObjectMapper objectMapper;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;
    private final JsonSchema schema;  // load once at startup from mcq-schema.json

    public McqLoadResult loadForTopic(String categoryPath, String topicSlug) {
        Path jsonPath = Paths.get("seed/mcq", categoryPath, topicSlug + ".json");
        Resource resource = new ClassPathResource(jsonPath.toString());
        if (!resource.exists()) {
            return McqLoadResult.notFound();
        }
        JsonNode tree = objectMapper.readTree(resource.getInputStream());
        Set<ValidationMessage> errors = schema.validate(tree);
        if (!errors.isEmpty()) {
            throw new InvalidMcqSeedException(jsonPath, errors);
        }
        McqSeed seed = objectMapper.treeToValue(tree, McqSeed.class);
        if (!seed.topicSlug().equals(topicSlug)) {
            throw new InvalidMcqSeedException(jsonPath, "topic_slug mismatch");
        }
        return upsertOptions(seed);
    }

    private McqLoadResult upsertOptions(McqSeed seed) {
        int totalInserted = 0;
        for (var question : seed.questions()) {
            Long questionId = questionRepository.findIdBySlugAndNumber(seed.topicSlug(), question.qNumber())
                    .orElse(null);
            if (questionId == null) {
                log.warn("Q{} in seed {} has no matching question in DB", question.qNumber(), seed.topicSlug());
                continue;
            }
            answerOptionRepository.deleteByQuestionId(questionId);
            for (var block : question.blocks()) {
                List<AnswerOptionCreate> creates = block.options().stream()
                        .map(o -> toCreate(o, block.blockIdx()))
                        .toList();
                answerOptionRepository.insertAll(questionId, creates);
                totalInserted += creates.size();
            }
        }
        return McqLoadResult.ok(totalInserted);
    }
}
```

DTO записи: `McqSeed`, `McqSeedQuestion`, `McqSeedBlock`, `McqSeedOption`, `McqSeedSections`.

### Validation pipeline

Удалить `scripts/verify-mcq.sh` (нет smysl — markdown больше не содержит MCQ). Вместо него:

1. **`scripts/verify-mcq-json.sh`** — bash-обёртка над JSON Schema валидатором. Использует `jsonschema` Python CLI или Java CLI из `quiz-app`. Альтернативно — `npx ajv-cli` (если есть Node).
   - Для каждого .json в `seed/mcq/`: validate против `mcq-schema.json`.
   - Дополнительно: проверка соответствия `topic_slug` имени файла.
2. **Pre-commit hook**: вызывает `verify-mcq-json.sh` для staged .json файлов в `seed/mcq/`.
3. **Дополнительные .md правила** в `scripts/verify-md-no-mcq.sh`:
   - FAIL если в `.md` встречен `> [!mcq]` (legacy).
   - FAIL если в `.md` встречены эмодзи маркеры `❌ ПОСЛЕДСТВИЕ:`, `✓ ПРИМЕНЯТЬ:`, `📋 ПРАВИЛО:`, `🔗 См. Q[0-9]` (legacy).
   - FAIL если в `.md` есть legacy `[[Q<N>]]` без префикса файла.

### Миграция: 17 v2 файлов → JSON

Bash-скрипт `scripts/migrate-md-to-json.sh` (awk-based, детерминированный):

1. Для каждого `.md` с inline `> [!mcq]` callout-блоками:
   - Pass 1: state machine идёт по строкам, при `## Q<N>. <title>` запоминает текущий Q, при `> [!mcq]` начинает аккумулировать опции, парсит named-секции (`**Развёрнутое объяснение.**` → `sections.explanation` и т.д.).
   - Pass 2: генерирует JSON по схеме, пишет в `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`.
2. Из `.md` удаляются все `> [!mcq]` блоки.
3. Из frontmatter удаляется поле `mcq_format_version`.

**Mapping секций markdown → JSON ключи:**

| Markdown секция (correct) | JSON ключ |
|---|---|
| `**Развёрнутое объяснение.**` | `explanation` |
| `**Пример.**` | `example` |
| `**Когда применять.**` | `when_to_apply` |
| `**Подводные камни.**` | `edge_cases` |
| `**Связанные вопросы.**` | `related` |

| Markdown секция (wrong) | JSON ключ |
|---|---|
| `**Что на самом деле.**` | `what_actually` |
| `**Откуда путаница.**` | `source_of_confusion` |
| `**Если бы это было правдой.**` | `if_it_were_true` |
| `**Как было бы правильно.**` | `how_it_should_be` |

### Миграция: 226 legacy v1 файлов → .md + JSON

Subagent-batch с обновлённым SKILL. Каждый subagent:

1. Читает `cheatsheets/interview/<category>/<topic>.md`.
2. Извлекает теоретический контент Q-секций → перезаписывает `.md` без эмодзи-маркеров (`❌ ПОСЛЕДСТВИЕ:`, `✓ ПРИМЕНЯТЬ:`, и т.д.) и без `> [!mcq]` callout'ов в Q-секциях.
3. Генерирует JSON `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json` со всеми MCQ блоками для каждой Q.
4. Запускает `verify-md-no-mcq.sh` для .md и `verify-mcq-json.sh` для .json.

Параллельно 5-10 subagent-ов.

### SKILL обновление

Два связанных SKILL обновляются согласованно:

**`~/.claude/skills/mcq-quality-fixer/SKILL.md`** — переписан под JSON-формат:

- Где хранить MCQ (путь к JSON, зеркальный к .md).
- JSON Schema с примерами.
- Что писать в `text` опции (1-2 предложения, без bold).
- Что писать в каждую section (правила: `explanation` 3-7 предложений; `example` с inline code; и т.д.).
- Single-Delta principle, distractor категории, варьирование позиции correct — переносятся.
- Workflow: subagent читает .md (для контекста темы), генерирует/правит .json (data).

**`~/.claude/skills/interview-writer/SKILL.md`** — расширен правилом MCQ-sync:

- При **CREATE/ADD** нового `## Q<N>` → одновременно добавить блок в JSON-сидер (если файла нет — создать).
- При **UPDATE** семантическом → перегенерировать соответствующий блок в JSON; косметический update — JSON не трогать.
- При **DELETE** `## Q<N>` → удалить блок из JSON, **не перенумеровать** остальные Q (gaps OK; это защищает внешние `[[file#Q5]]` ссылки).
- При **RENAME** topic — переименовать обе пары (.md и .json), обновить `topic_slug` в JSON, обновить cross-file ссылки.
- Каждое изменение завершается прогоном `bash scripts/verify-mcq-json.sh` + `bash scripts/verify-md-no-mcq.sh`.

**`~/.claude/skills/interview-options-writer/SKILL.md`** — депрекейтится (логика поглощена `mcq-quality-fixer`); в верху файла маркер `> DEPRECATED (2026-05-20)`.

### Quiz-app поведение

- При старте: парсер импортирует questions; `McqJsonLoader` импортирует options из JSON.
- Если JSON отсутствует для темы → questions есть, options пусты → quiz-app force flashcard mode.
- Если JSON есть, но не валиден → exception при старте, app не запустится (fail-fast).

## Trade-offs

| Плюс | Минус |
|---|---|
| Чистое разделение: docs в `.md`, data в `.json` | MCQ невидим в Obsidian preview |
| Парсер минус ~200 строк (no MCQ logic) | Subagent работает с двумя файлами на тему |
| JSON Schema нативная валидация | Code review JSON diffs менее читаем чем markdown |
| Структурные ошибки невозможны (schema enforced) | Ручная правка JSON менее удобна чем markdown |
| Quiz-app start быстрее (нет regex import) | 243 файла в дополнительной директории |
| `mcq_format_version` поле больше не нужно | Нельзя глазом посмотреть MCQ в репозитории без открытия .json |

## Edge cases & risks

- **Risk:** разрыв связи `.md` ↔ `.json` (переименовали `.md`, не переименовали `.json`). **Mitigation:** при импорте `McqJsonLoader` сверяет `topic_slug` JSON-а с basename `.md`; mismatch → fail-fast.
- **Risk:** subagent при миграции v1→json может потерять контент MCQ. **Mitigation:** на этапе миграции один subagent на файл, проверка `git diff` перед commit.
- **Risk:** JSON Schema mismatch при изменении контракта. **Mitigation:** schema лежит в `resources/`, изменения схемы — migration step (поднять версию формата опционально через `schema_version` поле — но пока избегаем, как user попросил).
- **Risk:** очень большие JSON-файлы (50 MCQ × 4 options × 5 секций = 1000+ строк JSON). **Mitigation:** один JSON на тему, не больше 60 Q → ~1500 строк max — managable.
- **Risk:** не все темы получают JSON одновременно → quiz-app временно flashcard-mode для немигрированных. **Mitigation:** это явное переходное состояние, документировано.

## Тесты

**Новые в `MarkdownQuestionParserTest`:**

- `parsesMarkdownWithoutMcqBlocks` — главный кейс: .md без MCQ, парсер возвращает questions с пустыми options.
- `mcqCalloutInMarkdownLogsWarning` — `> [!mcq]` в .md теперь warning (legacy), парсер игнорирует.

**Удалить из тестов** (потерявшие смысл):

- `parsesMcqBlock`
- `capturesAllMcqBlocksWithBlockIndex`
- `capturesMultiLineExplanationFromIndentedSections`
- `inlinePipeExplanationStillWorksForLegacyFormat`
- `mcqCalloutWithInlineQuestionHeaderStillRecognized`
- `noWarningEmittedForMultipleMcqBlocksAfterV15Migration`

**Новые в `McqJsonLoaderTest`:**

- `loadsValidJsonAndInsertsOptions` — happy path.
- `validJsonWithMultipleBlocksUsesMcqBlockIdx` — multi-block test.
- `invalidJsonSchemaThrowsAtStartup` — fail-fast.
- `topicSlugMismatchThrows` — mismatch detection.
- `missingQuestionInDbLogsWarning` — `q_number` без соответствующей Q в БД.
- `noJsonFileForTopicResultsInEmptyOptions` — фоллбэк на flashcard mode.

## Открытые вопросы — нет

## Дальнейшие шаги (для writing-plans skill)

1. Создать `mcq-schema.json` + JSON Schema validator dependency в `quiz-app/build.gradle.kts`.
2. Создать DTOs `McqSeed`, `McqSeedQuestion`, `McqSeedBlock`, `McqSeedOption`, `McqSeedSections`.
3. Создать `McqJsonLoader` + unit tests.
4. Интегрировать `McqJsonLoader` в `QuestionImportService`.
5. Удалить MCQ-логику из `MarkdownQuestionParser` + обновить unit tests.
6. Удалить legacy `scripts/verify-mcq.sh`, создать `verify-mcq-json.sh` + `verify-md-no-mcq.sh`.
7. Обновить pre-commit hook.
8. Создать `scripts/migrate-md-to-json.sh` (awk-based).
9. Прогнать migration на одном файле (тест), затем на 17 v2 файлах.
10. Удалить `mcq_format_version` поля из всех frontmatter.
11. Обновить SKILL `mcq-quality-fixer/SKILL.md` под JSON-формат.
12. Subagent-batch миграция оставшихся ~226 v1 legacy файлов в .md + .json.
13. Финальная валидация: `./gradlew test` + `verify-mcq-json.sh` по всем JSON + `verify-md-no-mcq.sh` по всем .md.
