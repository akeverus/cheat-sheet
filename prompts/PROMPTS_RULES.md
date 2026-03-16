# Prompts Rules

## 1) Контракт каталога

- `prompts/index.yaml` — единый реестр всех prompt-файлов.
- Канонический формат контента: `markdown/*.md`.
- `text/*.txt` поддерживаются как mirror-версии.

## 2) Именование

- Используем варианты: `short`, `long`, `screen`, `combine`.
- Новые файлы добавляем по схеме:
  - `markdown/<variant>.md`
  - `text/<variant>.txt` (опционально, если нужен plain-text mirror)
- `id` в `index.yaml` должен быть уникальным и стабильным.

## 3) Обязательные поля в index.yaml

Для каждой записи обязательны:

- `id`
- `scenario`
- `format`
- `variant`
- `path`
- `source_of_truth`

Для mirror-записей дополнительно:

- `mirror_of`

## 4) Связь с cheatsheets

- Для interview-промптов `related_cheatsheets` указывает на `cheatsheets/interview`.
- Промпты должны следовать структуре и терминологии из `cheatsheets/`.

## 5) Политика изменений

- Изменили prompt -> обновили `updated` в `index.yaml`.
- Добавили prompt -> добавили запись в `index.yaml` и проверили путь.
- Удалили prompt -> удалили запись из `index.yaml`.
