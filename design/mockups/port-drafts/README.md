# port-drafts — черновики Фазы E (НЕ подключены к проду)

Статус: **DRAFT**. Файлы здесь никем не загружаются (ни head.html, ни макетами) —
это ready-to-paste заготовки для Фазы E, подготовленные пока целевые прод-файлы
заблокированы параллельным WIP (§6 collision guard PROMPT_PLAN_FRONTEND.md).

| Черновик | Целевой файл | Куда вставлять | Блокер |
|---|---|---|---|
| `error-instrument-base.css` | `modules/quiz-app/src/main/resources/static/css/base.css` | СРАЗУ ПОСЛЕ общего блока §C7 (`html[data-design] .error-page ...`, ~строка 2060) | base.css dirty (чужой WIP) |

## error-instrument-base.css — решения порта (по PORT_MAPPING_error.md)

1. **Шаблон error.html НЕ меняется вообще.** Риск №2 контракта (`.error-rail` vs
   `.error-actions`) решён отказом от рельса: все 3 ссылки рельса дословно дублируют
   `.error-actions` (главная/аналитика/настройки), а полноширинность от рельса не
   зависит (контракт, §38 п.2). Уникален был только `rail-hint` — его совет про
   устаревший токен уже есть в серверном 403-body. Итог: порт = чистый append в
   base.css, ноль правок Thymeleaf → минимальная поверхность конфликта.
2. **Специфичность:** `html[data-design="instrument"] .error-page .X` равна общему
   `html[data-design] .error-page .X` (атрибут с значением и без — одинаковый вес),
   поэтому блок обязан стоять **после** общего §C7 — выигрывает по порядку.
3. **Серверные тернарники** title/body/code не трогаются (черновик — только CSS).
   Макетные `STATES`/`?code=`/mock-bar в прод не идут.
4. **AA:** призрачный номер статуса — декоративная типографика (`aria-hidden`,
   реальный заголовок — `h2.error-title`), контраст-требования не применяются
   (как и в общем §C7 с `text-tertiary`). Все текстовые роли — на AA-токенах
   instrument (`text-secondary` 7.2:1, `text-tertiary` 5.4:1 light).
5. **Токен-маппинг** (словарь макета → прод):
   `--paper`→`--color-bg-primary` · `--surface`→`--color-bg-tertiary` ·
   `--ink/-2/-3`→`--color-text-primary/secondary/tertiary` ·
   `--line`→`--color-border-primary` · `--line-strong` (ghost-номер)→`--color-border-primary`+комментарий ·
   `--signal-strong/-on/-ink`→`--color-accent-primary/-on/-strong` ·
   `--font-hero/ui/mono`→`--font-family-display/body/mono`.

## Процедура применения (когда base.css чист)

1. `git status --short` — убедиться, что base.css больше не dirty.
2. Вставить содержимое черновика (без заголовка-шапки файла) после общего §C7.
3. Бамп `?v=N` для base.css в `fragments/head.html` (голова тоже должна быть чистой).
4. QA parity: bootRun + chrome-devtools, `?code=`-ветвления серверные — дергать
   реальные 404/403, обе темы, 375/1280/2560 + 320 reflow.
5. Удалить применённый черновик из port-drafts/ тем же коммитом.
