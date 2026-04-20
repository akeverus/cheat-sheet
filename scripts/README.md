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

## Запуск

```bash
# Валидация — 0 нарушений = всё чисто
python3 scripts/cheatsheet-lint.py cheatsheets

# Автоправки (запускать последовательно для полной зачистки)
python3 scripts/cheatsheet-autofix.py cheatsheets
python3 scripts/cheatsheet-autofix-v2.py cheatsheets
python3 scripts/cheatsheet-autofix-v3.py cheatsheets

# Пересборка TOC
python3 scripts/cheatsheet-regenerate-toc.py cheatsheets

# Дополнить wikilinks до 5 в ### См. также
python3 scripts/cheatsheet-add-wikilinks.py cheatsheets

# Фильтр по подкаталогу (--only substring match)
python3 scripts/cheatsheet-autofix-v2.py cheatsheets --only postgres
```

## CI / pre-commit

Добавить в `.git/hooks/pre-commit`:

```bash
#!/bin/bash
python3 scripts/cheatsheet-lint.py cheatsheets --summary || {
  echo "Cheatsheet lint failed. Run scripts/cheatsheet-autofix*.py to fix."
  exit 1
}
```

Или в Gradle-задачу/GitHub Actions.

## Что НЕ делают скрипты

- Не переписывают ASCII-диаграммы в Mermaid — это делалось разово агентами.
- Не чинят семантические ошибки (битые ссылки на внешние URL, неточные факты).
- Не изменяют содержимое code fence'ов, frontmatter, wikilinks `[[...]]`.
- Не удаляют эмодзи в `interview/` и `CHEATSHEETS_ARCHITECTURE_AND_RULES.md` (там они легитимные примеры).

## Область действия

Все скрипты исключают:
- `cheatsheets/interview/` — свой формат Q&A
- `cheatsheets/CHEATSHEETS_ARCHITECTURE_AND_RULES.md` — сам регламент, содержит примеры «плохих» паттернов внутри code fence
