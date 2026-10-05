# Cheatsheet Scripts

Скрипты для массовой валидации и правок в каталоге `cheatsheets/`. Все правила — из `cheatsheets/CHEATSHEETS_ARCHITECTURE_AND_RULES.md`, раздел «Инлайн-форматирование».

## Скрипты

| Скрипт | Назначение | Идемпотентный |
|--------|------------|:--:|
| `cheatsheet-lint.py` | Проверка 13 правил форматирования, exit code ≠ 0 при нарушениях. Годится для CI / pre-commit hook. | ✓ |
| `cheatsheet-autofix.py` | Первый проход автоправок: trailing whitespace, тире/кавычки, hr между секциями, код-блоки без языка, переименование `### Baeldung → Обучающие материалы`, backticks в ссылках `[\`x\`](x.md)`. | ✓ |
| `cheatsheet-autofix-v2.py` | Второй проход: жирный в заголовках, жирный в скобках, комбинации backtick+bold, конвертация `[text](file.md)` → `[[wikilink]]`. | ✓ |
| `cheatsheet-autofix-v3.py` | Третий проход: жирный внутри inline-кода, расширенные тире, сложные случаи скобок. | ✓ |
| `cheatsheet-regenerate-toc.py` | Перегенерация блока `## Содержание` из реальных заголовков с GFM-совместимым slugify. | ✓ |
| `cheatsheet-add-wikilinks.py` | Дополняет `### См. также` до 5 wikilinks по пересечению тегов frontmatter. | ✓ |
| `cheatsheet-autofix-all.py` | Единая точка входа: прогоняет все 3 прохода + regenerate-toc по порядку. | ✓ |
| `healthcheck.sh` | Локальный smoke-test поднятого приложения: liveness/readiness probes + рендеринг страниц + static. Дефолт `http://localhost:8080`. | ✓ |
| `verify-mcq-json.sh` | Проверяет JSON Schema, межполевые инварианты и парный Markdown через Python jsonschema. `--all` — весь корпус; без аргументов — staged paths. | ✓ |
| `verify-md-no-mcq.sh` | Гарантирует отсутствие legacy MCQ-конструкций (`> [!mcq]`, эмодзи-маркеров) в `cheatsheets/interview/**.md`. MCQ должны жить только в JSON-сидерах. | ✓ |

## Запуск

```bash
# Валидация — 0 нарушений = всё чисто
python3 scripts/cheatsheet-lint.py cheatsheets

# Полная зачистка одной командой (3 прохода + TOC)
python3 scripts/cheatsheet-autofix-all.py cheatsheets

# Или по отдельности (для отладки)
python3 scripts/cheatsheet-autofix.py cheatsheets
python3 scripts/cheatsheet-autofix-v2.py cheatsheets
python3 scripts/cheatsheet-autofix-v3.py cheatsheets
python3 scripts/cheatsheet-regenerate-toc.py cheatsheets

# Дополнить wikilinks до 5 в ### См. также
python3 scripts/cheatsheet-add-wikilinks.py cheatsheets

# Фильтр по подкаталогу (--only substring match)
python3 scripts/cheatsheet-autofix-v2.py cheatsheets --only postgres
```

## CI / pre-commit

### GitHub Actions

Настроен workflow `.github/workflows/cheatsheet-lint.yml` — запускается на push/PR при изменении `cheatsheets/**` или `scripts/cheatsheet-lint.py`.

### pre-commit framework

Установить [pre-commit](https://pre-commit.com/):

```bash
pip install pre-commit
pre-commit install
```

Конфигурация в `.pre-commit-config.yaml`. При каждом `git commit` прогонит lint.

### Shell hook вручную

Альтернатива — в `.git/hooks/pre-commit`:

```bash
#!/bin/bash
python3 scripts/cheatsheet-lint.py cheatsheets --summary || {
  echo "Cheatsheet lint failed. Run scripts/cheatsheet-autofix-all.py to fix."
  exit 1
}
```

## Что НЕ делают скрипты

- Не переписывают ASCII-диаграммы в Mermaid — это делалось разово агентами.
- Не чинят семантические ошибки (битые ссылки на внешние URL, неточные факты).
- Не изменяют содержимое code fence'ов, frontmatter, wikilinks `[[...]]`.
- Не удаляют эмодзи в `interview/` и `CHEATSHEETS_ARCHITECTURE_AND_RULES.md` (там они легитимные примеры).

## Область действия

Все скрипты исключают:
- `cheatsheets/interview/` — свой формат Q&A
- `cheatsheets/CHEATSHEETS_ARCHITECTURE_AND_RULES.md` — сам регламент, содержит примеры «плохих» паттернов внутри code fence

## MCQ

Стандарт: [mcq-quality-standard](../docs/mcq-quality-standard.md).
Процесс для любой LLM: [PROMPT_PLAN_INTERVIEW](../PROMPT_PLAN_INTERVIEW.md).
Переносимый запуск и формат результатов: [workflow](../docs/mcq-quality/workflow.md).

- `python3 scripts/mcq-workflow.py doctor` — готовность локальных инструментов и evidence.
- `python3 scripts/mcq-workflow.py next --size 4` — актуальный следующий пакет, без LLM.
- `python3 scripts/mcq-workflow.py --help` — подготовка пакета, две фазы, регистрация результата, checkpoint.
- `python3 scripts/mcq-workflow.py sync` — снимок, таблица и точка продолжения из доказательств.

- `python3 -m pip install -r scripts/requirements-mcq.txt` — зависимости.
- `bash scripts/verify-mcq-json.sh --all` — структурная проверка всего корпуса.
- `bash scripts/mcq-tools/gates.sh <seed.json>` — предпроверки одной темы с сохранением exit codes.
- `python3 scripts/mcq-audit.py --output docs/mcq-quality/audit-state.json --checklist-output docs/mcq-quality/file-checklist.md` — инвентарь, хеши и очередь.
- `python3 -m unittest discover -s scripts/tests -p 'test_mcq*.py' -v` — контрпримеры валидаторов.

Семантическое ревью обязательно отдельно. Мутирующие `mcq-form-balance.py`,
`rebalance-mcq-rotation.py`, `mcq-tools/edit_*`, `mcq-struct/apply.py` не запускаются
для диагностики. Исторические измерители в `audit/mcq/` сохранены для воспроизводимости.
