---
type: component
title: "Frontend UI"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - ui
  - thymeleaf
  - javascript
status: developing
related:
  - "[[Quiz App]]"
  - "[[Thymeleaf]]"
  - "[[Interview Flow]]"
---

# Frontend UI

Server-side Thymeleaf + клиентский JavaScript. Без SPA-фреймворков.

## Страницы

| Шаблон | Назначение |
|--------|-----------|
| `index.html` | Тренировка: вопрос, 4 варианта, подсказки, код/диаграмма sidebar |
| `result.html` | Результат ответа: correct/wrong, объяснения, extra analysis |
| `session-summary.html` | Итоги сессии |
| `stats.html` | Статистика пользователя |
| `focus-training.html` | Фокусная тренировка |
| `settings.html` | Настройки |

## UI Flow (Training Page)

1. **Surface toolbar** — режим сессии, прогресс (current/total), progress bar, inline stats (accuracy, learned, due)
2. **Training shell** — вопрос, 4 варианта multiple-choice, hint container, sidebar (код/диаграмма)
3. **Practice modes** — instant, hard, review, adaptive, таймер (0-60 сек)
4. **Answer flow** — Selection → Checking → Result → Explanation (progressive reveal)

## UI Flow (Result Page)

1. **Stats grid** — прогресс сессии, correct/wrong
2. **Result card** — вопрос, выбранный vs правильный ответ, объяснения per-option
3. **Extra analysis** — lazy-loaded, 4 параллельных запроса:
   - Wrong feedback — почему неправильный выбор был ошибкой
   - Comparison table — criterion-by-criterion: selected vs correct
   - Takeaway — ключевой инсайт
   - Code trace — пошаговое выполнение кода

## JavaScript (app.js)

### API

- `apiFetch(url, options)` — fetch с same-origin credentials
- `apiPost(url, formData)`, `apiGet(url)` — обёртки
- `fetchWithPlaceholder(url, el, text, renderFn)` — progressive loading

### State Management

- `hydrateProgressBarsFromData()` — инициализация из `data-progress` атрибутов
- `updateSessionProgress(data)` — синхронизация прогресса с сервером
- `applyStatsPayload(stats)` — обновление всех stat-полей (total, due, learned, accuracy)

### Interactions

- `initHintButton()` — 3-уровневые подсказки через API, cap после max
- `initStreakBar()` — `/api/streak`, daily progress ring
- `toggleFavorite(questionId, button)` — POST к API.FAVORITE
- `initResultPageExtraAnalysis()` — `Promise.allSettled()` для 4 параллельных AI-запросов
- `initShuffleTopic()` — lock topic/group при "Микс" (shuffle)

### Utilities

- `escapeHtml(str)` — HTML entity encoding
- `trackUxMetric(name, payload)` — sessionStorage + custom event `quiz:ux-metric`
- `setInteractionBusy(busy)` — aria-busy/aria-disabled при async

## DOMContentLoaded Chain

Hydrates: progress bars → favorite buttons → streak bar → hints → shuffle controls → session form sync → result page extra analysis.
