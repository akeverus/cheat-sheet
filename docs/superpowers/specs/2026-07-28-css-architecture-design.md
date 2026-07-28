# Дизайн: рефакторинг CSS-архитектуры (instrument)

**Дата:** 2026-07-28
**Статус:** брейнсторминг — ожидает утверждения
**Скилл:** superpowers (под-проект #1 из полного рефакторинга фронтенда+бэкенда)
**Решения пользователя (locked):** под-проект = CSS-архитектура · цель = обе (maintainability + performance) · подход = A инкрементально (файл-за-файлом, screenshot-gated)

## 1. Цель

Одновременно две связанные цели для CSS дизайн-системы `instrument`:

1. **Performance** — перестать грузить страничный CSS на каждой странице.
   Сейчас 18 stylesheet-ов грузятся безусловно на **каждой** странице, включая ~2929 строк, принадлежащих одной странице каждая (`pages-focus` 1190, `pages-stats` 760, `pages-result` 615, `pages-settings` 367). Цель — перевести их на `th:if page==X` (как уже сделано для bookmarks/summary/error/login — 0-leak).
2. **Maintainability** — разбить гигантов по concern-кластерам и закрепить чёткую принадлежность каждого правила своему `@layer` и своей области видимости.

**Инвариант качества:** каждый шаг **behavior-preserving** — 0 визуальных отличий (dark+light), подтверждённых скриншотом, до коммита. Рефакторинг, а не редизайн.

## 2. Ground truth (замерено discovery-workflow, 2026-07-28)

### 2.1 Каскад `@layer` (объявлен в `tokens.css:31`)
```
@layer tokens, reset, shell, components, pages, utilities, pixel;
```
Правила: `pages` > `shell` (by design). Unlayered CSS бьёт любой layered. Порядок `<link>` = порядок **внутри** слоя.

### 2.2 Контракт загрузки (`fragments/head.html`, `head(title, includeMermaid, includeChart, includeHighlight, page)`)
- **Всегда (18):** fonts-local, tokens, reset, utilities-core, reset-layout, components-masthead, components-controls, **pages-focus**, **pages-settings**, **pages-stats**, **pages-result**, components-widgets, pages-layout, components-signatures, utilities-overrides, shell, pixel-fx, pixel/sprite-anim.
- **Page-scoped уже (0-leak):** bookmarks · summary · error · login (+ hljs по `includeHighlight`).
- **8 страниц, у каждой свой `page` param + `.{page}-page` body-class** (bookmarks/error/focus/login/result/summary/settings/stats). Инфраструктура page-scoped-загрузки уже единообразна.
- Корень `/` → `focus-training` (`MvcNavigationService`). «Домашний» today-widget живёт внутри focus-training. Отдельного `index.html` нет.

### 2.3 Карта global-leak по гигантам (сколько правил протекут, если файл грузить условно)

| Файл | Строк | `@layer` | leak | Диагноз |
|------|------:|----------|-----:|---------|
| `pixel-fx.css` | 1615 | pixel | 82 | Смешаны токены/shell/компоненты/страницы/arcade-FX — 9 concern-кластеров в одном файле |
| `pages-focus.css` | 1190 | pages | 78 | Содержит глобальные `.btn`, `.markdown-content`, answer-surface (`.result-*`) |
| `shell.css` | 1005 | shell | 71 | Наоборот: ~600 строк **страничных** правил (summary/stats/settings) осели в глобальном shell |
| `pages-result.css` | 615 | pages | 79 | Содержит app-wide модалки, confidence, report, shared feedback-surface |
| `pages-layout.css` | 473 | pages | 10 | Кросс-страничный: `--page-gutter` токен + весь `@media print` + layout 4 страниц |
| `pages-settings.css` | 367 | pages | 13 | Content-tabs (экран вопроса, не settings!) осели тут |
| `utilities-overrides.css` | 329 | utilities | 9 | max-600 оверрайды, смешаны shell + 3 страницы |
| `pages-stats.css` | 760 | pages | **4** | Почти чистый — 4 правила (control-focus + чужие `.settings-page`) |

**Ключевое наблюдение:** «leak» бывает двух видов —
- **тип A (правило чужой области в страничном файле):** напр. `.btn` в `pages-focus` → нужно **вынести** в общий компонент, тогда остаток становится page-pure → scopeable. Это большинство.
- **тип B (страничное правило в глобальном файле):** напр. summary-правила в `shell.css` → нужно **вернуть** в свой страничный файл, тогда глобальный файл худеет, а правило едет в page-scoped bundle.

### 2.4 Подтверждённые кросс-страничные зависимости (нельзя наивно page-scope)
- **Answer/verdict surface** (`result-zone-head` fragment, `.result-*`/`.feedback-*`/`.option-*`) рендерится на **обеих** `focus-training.html` (AJAX) и `result.html` (no-JS fallback). → общий компонент, грузится на focus+result, не в одном из них.
- **Markdown-контент** (`.markdown-content` + table/task-list/del/s/mermaid) — focus + result. → общий компонент.
- **`data-pixel=high` arcade-FX** (~420 строк в pixel-fx) — интенсивность это **клиентский** pref (localStorage, ставится в FOUC-бутстрапе), **неизвестна при SSR** → `th:if` не применим. Гейтить можно только JS-lazy-load (это под-проект JS, не CSS). Изоляция в отдельный файл — предпосылка для будущего lazy-load.

## 3. Целевая архитектура (end-state)

Принцип: каждое правило принадлежит ровно одному из трёх ярусов.

- **GLOBAL (always-loaded):** tokens, reset, shell-chrome, общие компоненты (buttons, markdown, answer-surface, modals, confidence, content-tabs, content-box, form-controls, report, icons), pixel-primitives+shell+components+motion, print. Грузится на каждой странице.
- **PAGE-PURE (page-scoped `th:if page==X`):** остаток focus/stats/settings/result/summary/bookmarks/error/login — правила, матчащие **только** под `.{page}-page`.
- **PIXEL-PAGE (page-scoped) + ARCADE-HIGH (JS-lazy):** страничные pixel-слои под `th:if`; arcade-high отложен на JS-под-проект.

### 3.1 Целевое дерево (по слоям)

```
@layer tokens     tokens.css (+ --page-gutter из pages-layout)
@layer reset      reset.css · reset-layout.css
@layer shell      shell.css (СЛИМ ~400: только sidebar/topbar/mobile-nav/masthead/skip-link)
@layer components components-masthead · -controls · -widgets · -signatures  (существуют)
                  + components-buttons.css        (~70, из pages-focus)
                  + components-markdown.css       (~150, из pages-focus)
                  + components-answer.css         (~185, shared verdict/feedback: focus+result+shell)
                  + components-modals.css         (~140, из pages-result)
                  + components-confidence.css     (~34,  из pages-result)
                  + components-report.css         (~24,  из pages-result)
                  + components-content-tabs.css   (~42,  из pages-settings → экран вопроса)
                  + components-content-box.css    (~70,  из shell)
                  + components-controls (+~25 shared control-focus из pages-stats)
@layer pages      pages-focus.css   (~790 остаток, 0-leak)  → th:if page==focus
                  pages-stats.css   (~735 остаток)          → th:if page==stats
                  pages-settings.css(~320 остаток)          → th:if page==settings
                  pages-result.css  (~205 остаток)          → th:if page==result
                  pages-summary/-bookmarks/-error/-login    (уже scoped)
@layer utilities  utilities-core (есть) · utilities-shell (~45 global)
                  utilities-stats/-focus/-home (~264 → едут в page-scoped bundles)
@layer pixel      pixel-tokens (~55) · pixel-shell (~140) · pixel-components (~540) · pixel-motion (~120)  [global]
                  pixel-page-interview (~230) → focus · pixel-page-summary (~90) → summary
                  pixel-page-error (~50) → error · pixel-page-settings (~26) → settings
                  pixel-arcade-high.css (~420) → JS-lazy (data-pixel=high), НЕ th:if
global            print.css (~108, @media print, always-loaded)
```

### 3.2 Выигрыш по нагрузке (оценка)
- **focus** больше не грузит stats/settings/result/summary-pixel: экономия ~1900+ строк.
- **stats/settings/etc.** не грузят focus/result: аналогично.
- Общие компоненты (~700 строк) грузятся везде — но это цена честной переиспользуемости, и они закешированы (один файл на все страницы).

## 4. Порядок миграции (инкрементальный, по возрастанию риска)

Каждая волна = 1–2 самостоятельных, откатываемых коммита. Порядок выбран по (безопасность × ценность).

- **Волна 0 — инструментовка (0 визуальных изменений). ✅ ВЫПОЛНЕНА (`b37d5021`).**
  `scripts/css-leak-audit.py`: парсит селекторы (учёт @layer/@media как прозрачных, @keyframes как непрозрачных), проверяет привязку к page-root — `.{page}-page` **ИЛИ** страничный семантический корень (`EXCLUSIVE_ROOTS`: focus→`.focus-`/`#interview`, result→`.result-`/`.distractor-` и т.д.). Exit 1 если leak>0 — гейт для последующих волн.
  **Механически подтвердил карту workflow (важное уточнение — способ якорения различается):**
  - `.{page}-page`-якорь (точный гейт): stats=**5** unanchored (2 control-focus + 3 чужих `.settings-page`), settings=**15** (кластер content-tabs + хвосты); summary/bookmarks/error/login=**0** (уже scoped).
  - семантический якорь (worklist, не финальный leak): focus=187, result=69 — требуют per-селектор классификации в Волне 3. Кросс-страничные корни (`.options` focus+result, `.today-*` focus+settings, `.question-materials`) намеренно НЕ в `EXCLUSIVE_ROOTS` → всплывают как unanchored → это сигнал «выносить в общий компонент».

- **Волна 1 — `pages-stats` (leak=4). Эталонный quick win.**
  Вынести 4 протекающих правила (control-focus → `components-controls`; чужие `.settings-page` → `pages-settings`). Проверить leak=0. `th:if page==stats`. Скрин stats + одна не-stats страница (что ничего не потеряно), dark+light. → ~735 строк долой с каждой не-stats страницы.

- **Волна 2 — `pages-settings` (leak=13).**
  Вынести content-tabs (~42 → `components-content-tabs`) + form-controls хвост (~4). `th:if page==settings`.

- **Волна 3 — общие компоненты из `pages-focus`+`pages-result` (leak 78+79, связанная пара, самая глубокая).**
  Сначала извлечь ОБЩЕЕ (buttons, markdown, answer/feedback, modals, confidence, report). ТОЛЬКО потом оба файла станут page-pure → scope обоих. Максимум скриншотов (сдвиг `@layer pages`→`components` для вынесенных правил — риск каскада).

- **Волна 4 — слим `shell.css` (leak=71 = страничные правила В shell).**
  Вернуть ~600 строк summary/stats/settings-правил в их страничные файлы + answer/content-box в компоненты. shell → ~400 строк истинного глобального хрома.

- **Волна 5 — растворение `pages-layout` (leak=10).**
  `--page-gutter` → `tokens.css`; весь `@media print` → новый `print.css` (global); layout 4 страниц → их страничные файлы. Файл исчезает.

- **Волна 6 — сплит `utilities-overrides` (leak=9).**
  → `utilities-shell` (global) + `utilities-stats/-focus/-home` (в page-scoped bundles).

- **Волна 7 — сплит `pixel-fx` (1615, maintainability-монстр).**
  9 файлов по concern. `pixel-arcade-high` → кандидат на JS-lazy (передаётся в JS-под-проект). Pixel-page файлы → `th:if`.

- **Волна 8 — финальная разводка `head.html` + аудит.**
  Все focus/stats/settings/result + pixel-page-* переведены на `th:if page==X`. Прогон leak-гейта (все scoped = 0). Полная матрица скриншотов 8 страниц × 2 темы × 3 вьюпорта. Бамп cache-bust версии CSS.

## 5. Стратегия верификации

- **Per-step:** `cp src→build/resources/main` → reload `ignoreCache:true` → скрин затронутой + одной контрольной страницы, dark+light. Диф обязан быть идентичным (behavior-preserving).
- **Leak-гейт:** `css-leak-audit.py` — scoped-файлы имеют 0 селекторов вне своего page-root.
- **Cascade-гейт:** извлечения двигают правила между `@layer` — скриншот и есть гейт на непреднамеренный сдвиг приоритета (тот самый «pages beats components» footgun).
- **Полный чек:** `./gradlew check` (ArchUnit, тесты) остаётся зелёным.
- **Гранулярность коммитов:** волна = коммит(ы), каждый независимо откатываем; фронт коммитится в master через pathspec.

## 6. Роль бэкенда

Для CSS-архитектуры backend-касание минимально и относится к SSR-слою:
- `head.html` (Thymeleaf) — `page` param уже есть; расширение `th:if page==X` это правка шаблона.
- Бамп cache-bust версии CSS (конвенция `v=N`).
- Java-изменений для CSS-arch не требуется.

**Полноценный backend-рефакторинг** (контроллеры/сервисы/DTO) — это **следующий под-проект** superpowers-scope, не этот. Последовательность под-проектов: (1) CSS-архитектура ← *здесь* → (2) JS-архитектура + lazy-load arcade → (3) backend/SSR-контракт → (4) доступность/семантика → (5) frontend-perf-замеры.

## 7. Границы (для этого под-проекта)

- Только CSS-архитектура + необходимая разводка `head.html`. Не редизайн, не новые фичи, не JS-поведение.
- Не трогаем: held-файлы (`PauseService*`), MCQ-сидеры, interview `.md` (отдельные loop-и).
- `data-pixel=high` lazy-load **не** реализуем здесь (только изолируем файл) — это JS-под-проект.
- Каждая волна автономна: можно остановиться после любой, оставив систему в валидном состоянии.

## 8. Риски и митигации

| Риск | Митигация |
|------|-----------|
| Сдвиг `@layer` при выносе правила меняет resolved-стиль | Screenshot-гейт dark+light каждый шаг; волна 3 (глубокая) — максимум скринов |
| Наивный page-scope ломает shared-surface (answer на focus+result) | Answer/markdown вынесены в **общие** компоненты (always-loaded), не в страничный файл |
| Ложное «leak=0» из-за `html[data-design]`-префиксов | Аудит-скрипт нормализует префикс перед проверкой page-root |
| Живой bootRun кеширует старый CSS | `cp src→build` + reload `ignoreCache` (durable-грабли) |
| Регрессия в тестах/ArchUnit | `./gradlew check` перед заявлением «готово» |
| Клиентский `data-pixel` нельзя SSR-гейтить | Arcade-high изолируется, но грузится (как сейчас); lazy-load отложен явно |

## 9. Out of scope

- JS-архитектура, lazy-load, поведение (следующий под-проект).
- Backend-proper (контроллеры/сервисы) — следующий под-проект.
- Визуальные изменения любого рода (это чистый рефактор).
- Токены/палитра/типографика — не трогаем.
- Страницы вне 8 существующих шаблонов.
