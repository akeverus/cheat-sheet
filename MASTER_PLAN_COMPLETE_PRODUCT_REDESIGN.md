# MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md

# Детальный рабочий план полного редизайна frontend, backend, UI и UX

## 0. Назначение документа

Этот документ определяет обязательный порядок полного переосмысления приложения.

Он является одновременно:

* рабочим планом;
* техническим заданием;
* дизайн-заданием;
* архитектурным планом;
* планом миграции;
* планом визуальной приёмки;
* планом тестирования;
* системой контроля качества;
* системой защиты от бессмысленных loop-итераций.

Документ должен использоваться как источник истины для всех работ, связанных с:

* frontend;
* backend;
* UI;
* UX;
* дизайном;
* навигацией;
* адаптивностью;
* pixel-art;
* графикой;
* анимациями;
* accessibility;
* производительностью;
* API;
* базой данных;
* аналитикой;
* тестированием;
* документацией.

---

# 1. Главная цель

Полностью переработать приложение для подготовки к техническим интервью и превратить его в зрелый, красивый, уникальный и удобный продукт.

Результат должен быть настолько качественным, чтобы:

* интерфейс выглядел профессионально;
* продукт узнавался без логотипа;
* пользователь сразу понимал, что делать;
* главный учебный сценарий был очевидным;
* mobile-версия не уступала desktop;
* pixel-art создавал характер продукта;
* графика не перегружала интерфейс;
* анимации помогали, а не отвлекали;
* backend поддерживал реальный UX;
* приложение было доступным;
* приложение было быстрым;
* архитектура позволяла развивать продукт дальше;
* дальнейшие изменения были бы преимущественно вкусовыми.

---

# 2. Полномочия исполнителя

Разрешено менять всё.

В частности, разрешено:

* полностью переписать frontend;
* полностью переписать CSS;
* полностью переписать JavaScript;
* перейти на другой frontend framework;
* заменить серверные шаблоны;
* изменить роутинг;
* изменить информационную архитектуру;
* объединить страницы;
* удалить страницы;
* добавить новые страницы;
* изменить backend API;
* изменить DTO;
* изменить модели данных;
* добавить миграции;
* изменить бизнес-логику;
* изменить механизм сессий;
* изменить механизм повторения;
* изменить аналитику;
* изменить систему сохранения прогресса;
* изменить навигацию;
* изменить дизайн;
* заменить иконки;
* создать новые арты;
* создать новые анимации;
* удалить слабые pixel-art эффекты;
* изменить систему тем;
* заменить библиотеки;
* удалить устаревший код;
* переписать приложение с нуля.

Необходимо сохранить:

* полезный учебный контент;
* данные пользователя;
* корректную историю;
* существующие подтверждённые бизнес-правила;
* pixel-art как часть бренда;
* нижнюю pixel-art панель как фирменный элемент;
* безопасность;
* доступность;
* возможность миграции существующих данных.

---

# 3. Принципы проекта

## 3.1 Пользовательский эффект важнее размера diff

Изменение считается полезным только тогда, когда оно улучшает хотя бы один показатель:

* понятность;
* скорость действия;
* количество шагов;
* читаемость;
* визуальную иерархию;
* доступность;
* адаптивность;
* обратную связь;
* производительность;
* надёжность;
* безопасность;
* сопровождаемость.

Переименование классов, изменение отступа на 2 px и добавление новой тени не являются самостоятельным улучшением.

## 3.2 Основной сценарий важнее дополнительных функций

Порядок приоритетов:

```text
вопрос
→ выбор ответа
→ проверка
→ понимание результата
→ следующий вопрос
→ завершение сессии
→ повторение слабого материала
→ аналитика
→ дополнительные функции
```

Пока основной сценарий остаётся слабым, запрещено тратить основные ресурсы на:

* достижения;
* декоративные эффекты;
* дополнительные темы;
* сложную кастомизацию;
* расширенную геймификацию;
* косметическую полировку второстепенных страниц.

## 3.3 Переписывание допустимо

Не сохраняй слабую архитектуру из страха перед большим объёмом изменений.

Если текущая реализация:

* мешает компонентному подходу;
* содержит god-modules;
* требует большого количества `!important`;
* мешает тестированию;
* мешает accessibility;
* создаёт нестабильный lifecycle;
* не позволяет нормально управлять состояниями,

следует создать новую реализацию и мигрировать функциональность поэтапно.

## 3.4 Нельзя улучшать интерфейс только по коду

Каждое заметное UI-изменение должно подтверждаться:

* working runtime;
* before screenshot;
* after screenshot;
* desktop screenshot;
* mobile screenshot;
* dark theme;
* light theme;
* state screenshots;
* кратким UX-сравнением.

## 3.5 Нельзя создавать ложную активность

Запрещено считать прогрессом:

* очередной аудит без реализации;
* документацию без кода;
* генерацию неиспользуемых ассетов;
* добавление компонента без интеграции;
* рефакторинг без пользовательского эффекта;
* изменение только happy path;
* изменение только desktop;
* добавление эффектов без UX-цели;
* перестановку одинаковых карточек;
* расширение backlog без закрытия задач.

---

# 4. Организация проекта

Создать рабочую структуру:

```text
docs/redesign/
├── 00_EXECUTIVE_SUMMARY.md
├── 01_CURRENT_STATE.md
├── 02_PRODUCT_VISION.md
├── 03_USER_RESEARCH_ASSUMPTIONS.md
├── 04_PERSONAS_AND_JOBS.md
├── 05_INFORMATION_ARCHITECTURE.md
├── 06_USER_FLOWS.md
├── 07_SCREEN_INVENTORY.md
├── 08_VISUAL_DIRECTION.md
├── 09_DESIGN_TOKENS.md
├── 10_COMPONENT_SYSTEM.md
├── 11_PIXEL_ART_SYSTEM.md
├── 12_MOTION_SYSTEM.md
├── 13_ACCESSIBILITY.md
├── 14_FRONTEND_ARCHITECTURE.md
├── 15_BACKEND_ARCHITECTURE.md
├── 16_API_CONTRACTS.md
├── 17_DATA_MODEL.md
├── 18_ANALYTICS_MODEL.md
├── 19_MIGRATION_PLAN.md
├── 20_TEST_STRATEGY.md
├── 21_PERFORMANCE_BUDGET.md
├── 22_SECURITY_PLAN.md
├── 23_RELEASE_PLAN.md
├── 24_PROGRESS.md
├── 25_DECISIONS.md
├── 26_CHANGELOG.md
├── 27_RISKS.md
└── 28_REMAINING_BACKLOG.md
```

Создать структуру доказательств:

```text
artifacts/redesign/
├── baseline/
│   ├── desktop/
│   ├── tablet/
│   ├── mobile/
│   ├── dark/
│   ├── light/
│   └── states/
├── concepts/
│   ├── concept-a/
│   ├── concept-b/
│   └── concept-c/
├── implemented/
│   ├── shell/
│   ├── question/
│   ├── feedback/
│   ├── session-start/
│   ├── summary/
│   ├── analytics/
│   ├── settings/
│   └── library/
├── comparisons/
├── accessibility/
├── performance/
├── visual-regression/
└── release/
```

Создать структуру графики:

```text
assets/
├── icons/
│   ├── source/
│   ├── individual-svg/
│   ├── sprite/
│   ├── png-preview/
│   └── metadata/
├── pixel/
│   ├── navigation/
│   ├── controls/
│   ├── statuses/
│   ├── mascot/
│   ├── illustrations/
│   ├── achievements/
│   ├── effects/
│   ├── frames/
│   ├── sheets/
│   └── metadata/
├── screenshots/
├── licenses/
└── galleries/
```

---

# 5. Система контроля выполнения

## 5.1 Статусы задач

Использовать только:

```text
NOT_STARTED
IN_PROGRESS
BLOCKED
IMPLEMENTED
VERIFIED
REJECTED
```

Задача не может считаться завершённой в статусе `IMPLEMENTED`.

Финальный статус — только `VERIFIED`.

## 5.2 Для каждой задачи фиксировать

```text
ID
Название
Фаза
Проблема
Пользовательский ущерб
Текущее поведение
Целевое поведение
Backend scope
Frontend scope
Design scope
Accessibility scope
Test scope
Изменяемые файлы
Критерии приёмки
Screenshots
Результаты тестов
Риски
Статус
```

## 5.3 Stop-gate

После каждой фазы существует контрольная точка.

Переход к следующей фазе запрещён, если:

* остались P0-проблемы текущей фазы;
* отсутствуют обязательные артефакты;
* нет visual proof;
* тесты не запускались;
* mobile не проверен;
* accessibility не проверена;
* документация расходится с runtime.

---

# 6. Приоритеты

## P0 — критические

* потеря данных;
* повторная обработка submit;
* нарушение безопасности;
* broken authentication;
* broken authorization;
* SQL-инъекции;
* XSS;
* небезопасный Markdown;
* небезопасный Mermaid;
* broken session state;
* 404 на основных ассетах;
* невозможность пройти основной сценарий;
* необратимая потеря пользовательского прогресса.

## P1 — обязательные

* слабый главный UX;
* неудобная навигация;
* плохая mobile-версия;
* непонятный feedback;
* broken accessibility;
* ложная аналитика;
* god-modules;
* конфликтующие дизайн-системы;
* нестабильный frontend lifecycle;
* несогласованность API;
* отсутствие error states.

## P2 — важные

* визуальная полировка;
* дополнительные иллюстрации;
* расширенная аналитика;
* оптимизация второстепенных страниц;
* улучшение onboarding;
* расширенная персонализация.

## P3 — эксперименты

* дополнительные игровые механики;
* альтернативные темы;
* декоративные режимы;
* расширенные achievement-анимации;
* экспериментальные представления данных.

---

# 7. Фаза 0. Подготовка и защита исходного состояния

## Цель

Создать безопасную точку старта и не потерять существующую функциональность.

## 7.1 Создать baseline branch

Создать отдельную ветку:

```text
redesign/baseline
```

Зафиксировать:

* исходный код;
* состояние базы;
* миграции;
* screenshots;
* тесты;
* используемые версии;
* переменные окружения;
* список сервисов;
* список внешних интеграций.

## 7.2 Создать backup данных

Если используется реальная БД:

* создать dump;
* проверить восстановление;
* зафиксировать схему;
* экспортировать справочные данные;
* исключить секреты;
* сохранить команды восстановления.

## 7.3 Создать feature flags

Минимально:

```text
newShellEnabled
newQuestionFlowEnabled
newFeedbackEnabled
newSessionStartEnabled
newSummaryEnabled
newAnalyticsEnabled
newSettingsEnabled
```

Flags должны позволять:

* включать новую реализацию поэтапно;
* быстро откатывать конкретный сценарий;
* не поддерживать старую и новую версии бесконечно.

## 7.4 Зафиксировать критические пользовательские сценарии

Создать smoke tests для текущего приложения:

1. вход;
2. открытие главной;
3. старт сессии;
4. выбор ответа;
5. submit;
6. правильный feedback;
7. неправильный feedback;
8. переход к следующему вопросу;
9. завершение сессии;
10. открытие статистики;
11. изменение настройки;
12. повторное открытие сессии.

## Результат фазы

* безопасный baseline;
* возможность отката;
* smoke test suite;
* резервная копия данных;
* feature flags.

---

# 8. Фаза 1. Runtime-аудит

## Цель

Получить фактическое состояние продукта, а не предположения по коду.

## 8.1 Запуск

Зафиксировать:

* команды сборки;
* команды запуска;
* версии runtime;
* зависимости;
* environment variables;
* порты;
* required services;
* seed data;
* test users.

## 8.2 Проверка браузера

Для каждого основного сценария проверить:

* Console;
* Network;
* Accessibility tree;
* Performance;
* Layout shifts;
* failed resources;
* duplicate requests;
* long tasks;
* hydration errors, если применимо;
* event listener growth;
* memory usage.

## 8.3 Проверка экранов

Составить полный screen inventory:

```text
Экран
URL
Основная цель
Primary CTA
Secondary actions
Backend endpoints
Компоненты
States
Mobile behavior
Accessibility issues
Visual issues
Technical issues
```

## 8.4 Baseline screenshots

Для каждого экрана:

```text
320 × 568
375 × 812
390 × 844
768 × 1024
1024 × 768
1280 × 800
1440 × 900
1920 × 1080
2560 × 1440
```

Обязательные состояния:

* default;
* loading;
* empty;
* error;
* selected;
* disabled;
* long content;
* overflow;
* offline;
* reconnect;
* answered correctly;
* answered incorrectly.

## 8.5 Замеры

Зафиксировать:

* число действий до первого вопроса;
* число действий до submit;
* число действий до следующего вопроса;
* scroll depth;
* высоту основных mobile-страниц;
* количество одновременно видимых CTA;
* количество карточек на первом viewport;
* размер initial bundle;
* количество CSS;
* количество JavaScript;
* количество `!important`;
* количество hardcoded colors;
* количество media queries;
* количество broken assets;
* console errors;
* Network errors;
* accessibility violations.

## Результаты

```text
docs/redesign/01_CURRENT_STATE.md
docs/redesign/07_SCREEN_INVENTORY.md
artifacts/redesign/baseline/
```

## Gate

Запрещено переходить дальше, если baseline screenshots:

* имеют неправильные размеры;
* содержат дубли;
* сняты не с начала страницы без явной причины;
* не соответствуют runtime;
* не покрывают основной сценарий.

---

# 9. Фаза 2. Product discovery

## Цель

Определить, какой продукт создаётся и какую проблему он решает.

## 9.1 Jobs to be Done

Описать минимум следующие задачи:

### Job 1

```text
Когда я готовлюсь к техническому интервью,
я хочу быстро начать релевантную тренировку,
чтобы не тратить время на настройку.
```

### Job 2

```text
Когда я ошибаюсь,
я хочу понять точную причину,
чтобы не повторять ошибку.
```

### Job 3

```text
Когда я возвращаюсь в приложение,
я хочу сразу увидеть лучшее следующее действие,
чтобы продолжить прогресс.
```

### Job 4

```text
Когда я изучаю сложную тему,
я хочу видеть код, схемы и объяснения в одном контексте,
чтобы не переключаться между страницами.
```

### Job 5

```text
Когда я смотрю статистику,
я хочу понять слабые места и получить действие,
а не просто увидеть числа.
```

## 9.2 Персоны

Минимально:

### Кандидат перед интервью

* ограниченное время;
* нужна быстрая сессия;
* интересуют слабые темы;
* важна практическая точность.

### Системно обучающийся разработчик

* длинные сессии;
* важны объяснения;
* нужен прогресс;
* нужны закладки и повторение.

### Mobile-пользователь

* короткие сессии;
* управление одной рукой;
* нестабильная сеть;
* малый экран;
* важен быстрый resume.

## 9.3 North Star

Определить продуктовую метрику.

Пример:

```text
Количество качественно завершённых учебных циклов,
в которых пользователь ответил, изучил feedback
и выполнил следующее рекомендованное действие.
```

## 9.4 Принципы продукта

Зафиксировать:

1. обучение важнее геймификации;
2. один экран — одна основная цель;
3. ошибка должна обучать;
4. аналитика должна приводить к действию;
5. mobile является полноценной версией;
6. прогресс должен быть честным;
7. интерфейс должен поддерживать концентрацию;
8. пользователь всегда понимает следующий шаг.

## Результаты

```text
docs/redesign/02_PRODUCT_VISION.md
docs/redesign/03_USER_RESEARCH_ASSUMPTIONS.md
docs/redesign/04_PERSONAS_AND_JOBS.md
```

---

# 10. Фаза 3. Информационная архитектура

## Цель

Создать логичную структуру продукта до рисования экранов.

## 10.1 Верхнеуровневые разделы

Рекомендуемая структура:

```text
Главная
Тренировка
Повторение
Библиотека
Прогресс
Настройки
```

## 10.2 Главная

Должна отвечать на вопрос:

```text
Что мне лучше сделать сейчас?
```

Содержит:

* продолжение активной сессии;
* рекомендуемую тренировку;
* краткий прогресс;
* слабую тему;
* ближайшие повторения;
* последнюю активность.

Не содержит:

* десять равнозначных карточек;
* полную аналитику;
* все настройки;
* декоративные блоки без действия.

## 10.3 Тренировка

Содержит:

* быстрый старт;
* выбор цели;
* выбор тем;
* выбор режима;
* выбор длительности;
* preview сессии;
* старт.

## 10.4 Повторение

Содержит:

* вопросы по расписанию;
* ошибки;
* слабые темы;
* незавершённые разборы;
* сохранённые вопросы;
* рекомендации.

## 10.5 Библиотека

Содержит:

* темы;
* поиск;
* фильтры;
* вопросы;
* bookmarks;
* recently viewed;
* difficulty;
* mastery state.

## 10.6 Прогресс

Содержит:

* результат;
* динамику;
* слабые темы;
* историю;
* рекомендации;
* переход к тренировке.

## 10.7 Настройки

Разделы:

* обучение;
* интерфейс;
* доступность;
* уведомления;
* данные;
* приватность;
* экспорт;
* удаление данных.

## 10.8 Карта маршрутов

Для каждого маршрута описать:

```text
URL
Название
Цель
Primary action
Required data
Loading
Empty
Error
Permissions
Mobile layout
Analytics events
```

## Результаты

```text
docs/redesign/05_INFORMATION_ARCHITECTURE.md
docs/redesign/06_USER_FLOWS.md
```

## Gate

Каждая функция должна иметь одно логичное место.

---

# 11. Фаза 4. Полные пользовательские сценарии

## Цель

Спроектировать сценарии до реализации страниц.

## 11.1 Первый запуск

```text
Открытие
→ краткое объяснение
→ выбор цели
→ выбор интересующих тем
→ настройка интенсивности pixel-art
→ первая сессия
```

Не использовать длинный onboarding carousel.

## 11.2 Возвращение

```text
Открытие
→ предложение продолжить
→ один клик
→ текущий вопрос
```

## 11.3 Новая тренировка

```text
Тренировка
→ цель
→ темы
→ режим
→ длительность
→ preview
→ старт
```

## 11.4 Ответ на вопрос

```text
Вопрос
→ анализ
→ выбор варианта
→ проверка
→ feedback
→ следующий вопрос
```

## 11.5 Неправильный ответ

```text
Неверно
→ правильный ответ
→ ключевое различие
→ причина привлекательности ошибки
→ что запомнить
→ следующий вопрос
```

## 11.6 Завершение сессии

```text
Результат
→ сильные стороны
→ слабые стороны
→ рекомендация
→ primary action
```

## 11.7 Offline

```text
Потеря сети
→ локальное сохранение действия
→ понятный статус
→ восстановление
→ синхронизация
→ подтверждение
```

## 11.8 Конфликт ревизии вопроса

```text
Пользователь отвечает на старую ревизию
→ backend принимает попытку для увиденной ревизии
→ новая версия не подменяет историю
→ пользователь получает корректный feedback
```

Для каждого flow создать:

* happy path;
* error path;
* empty path;
* interrupted path;
* mobile path;
* keyboard path.

---

# 12. Фаза 5. Визуальные направления

## Цель

Не начинать реализацию до выбора сильного визуального языка.

## 12.1 Создать три концепции

### Concept A — Precision Terminal

Характер:

* строгий;
* технический;
* профессиональный;
* минималистичный;
* высокая читаемость;
* минимальный декоративный pixel-art.

### Concept B — Pixel Laboratory

Характер:

* более дружелюбный;
* выразительный mascot;
* мягкая глубина;
* pixel-art иллюстрации;
* умеренная геймификация.

### Concept C — Knowledge Console

Характер:

* code-first;
* контекстный rail;
* сочетание IDE и обучения;
* выразительная типографика;
* инструментальный интерфейс.

## 12.2 Для каждой концепции создать

* цветовую палитру;
* typography;
* surfaces;
* buttons;
* option cards;
* code block;
* bottom dock;
* empty state;
* error state;
* mascot style;
* dark theme;
* light theme.

## 12.3 Обязательные экраны

* home desktop;
* home mobile;
* question desktop;
* question mobile;
* selected answer;
* incorrect answer;
* correct answer;
* session summary;
* analytics;
* settings;
* offline state.

## 12.4 Матрица оценки

Каждую концепцию оценить по шкале 1–5:

```text
Уникальность
Читаемость
Соответствие продукту
Поддержка pixel-art
Desktop
Mobile
Accessibility
Масштабируемость
Производительность
Сложность реализации
```

## Результаты

```text
docs/redesign/08_VISUAL_DIRECTION.md
artifacts/redesign/concepts/
```

## Gate

Нельзя реализовывать три концепции одновременно.

Выбирается одна.

---

# 13. Фаза 6. Design tokens

## Цель

Создать единый фундамент.

## 13.1 Цветовые токены

```text
--color-bg-canvas
--color-bg-elevated
--color-bg-sunken
--color-surface-primary
--color-surface-secondary
--color-surface-interactive
--color-text-primary
--color-text-secondary
--color-text-muted
--color-border-subtle
--color-border-default
--color-border-strong
--color-accent-primary
--color-accent-hover
--color-accent-active
--color-success
--color-warning
--color-error
--color-info
--color-special
```

## 13.2 Typography tokens

```text
--font-sans
--font-mono
--font-pixel

--text-display
--text-page-title
--text-section-title
--text-card-title
--text-body-lg
--text-body
--text-body-sm
--text-label
--text-caption
--text-code
--text-metric
```

## 13.3 Spacing

```text
--space-0
--space-1: 4px
--space-2: 8px
--space-3: 12px
--space-4: 16px
--space-5: 24px
--space-6: 32px
--space-7: 48px
--space-8: 64px
```

## 13.4 Radius

Ограничить набор:

```text
--radius-sm
--radius-md
--radius-lg
--radius-pill
--radius-pixel
```

## 13.5 Motion

```text
--motion-instant
--motion-fast
--motion-normal
--motion-slow
--motion-celebration

--ease-standard
--ease-enter
--ease-exit
--ease-emphasized
```

## 13.6 Layout

```text
--content-reading
--content-default
--content-wide
--sidebar-width
--rail-width
--dock-height
--header-height
```

## 13.7 Layers

```text
--layer-base
--layer-sticky
--layer-dropdown
--layer-dock
--layer-overlay
--layer-modal
--layer-toast
```

## Результат

```text
docs/redesign/09_DESIGN_TOKENS.md
```

---

# 14. Фаза 7. Компонентная система

## Цель

Создать компоненты до массовой сборки страниц.

## 14.1 Primitive components

* Button;
* IconButton;
* Link;
* Text;
* Heading;
* Divider;
* Surface;
* Stack;
* Inline;
* Grid;
* Container.

## 14.2 Form components

* Input;
* Textarea;
* Select;
* Combobox;
* Checkbox;
* Radio;
* Switch;
* Slider;
* Field;
* FieldError;
* FormSection.

## 14.3 Navigation

* TopBar;
* DesktopNavigation;
* MobileDock;
* Breadcrumbs;
* Tabs;
* SegmentedControl;
* Pagination;
* CommandPalette.

## 14.4 Feedback

* Toast;
* InlineMessage;
* Alert;
* Progress;
* Skeleton;
* Spinner;
* EmptyState;
* ErrorState;
* OfflineState;
* SyncState.

## 14.5 Content

* QuestionHeader;
* AnswerOption;
* AnswerGroup;
* FeedbackPanel;
* CodeBlock;
* DiagramViewer;
* MarkdownContent;
* DefinitionList;
* DataTable;
* Metric;
* ChartContainer.

## 14.6 Overlay

* Dialog;
* Drawer;
* Popover;
* Dropdown;
* Tooltip;
* ConfirmationDialog.

## 14.7 Pixel components

* PixelIcon;
* PixelArt;
* PixelBadge;
* PixelDock;
* PixelStatus;
* PixelAnimation;
* Mascot;
* AchievementArt.

## 14.8 Для каждого компонента реализовать

```text
default
hover
focus-visible
active
selected
disabled
loading
success
warning
error
long content
overflow
mobile
dark
light
reduced motion
```

## 14.9 Документация компонента

Для каждого:

* назначение;
* когда использовать;
* когда не использовать;
* anatomy;
* props;
* states;
* accessibility;
* responsive behavior;
* examples;
* anti-patterns.

## Результат

```text
docs/redesign/10_COMPONENT_SYSTEM.md
```

---

# 15. Фаза 8. Pixel-art система

## Цель

Превратить pixel-art в системный брендовый слой.

## 15.1 Категории

```text
navigation
functional controls
statuses
feedback
empty states
mascot
achievements
decorative
animations
```

## 15.2 Размеры

```text
8 × 8
16 × 16
24 × 24
32 × 32
48 × 48
64 × 64
96 × 96
```

## 15.3 Запрет дробного масштаба

Нельзя:

```text
16 → 28
24 → 40
32 → 45
```

Разрешено:

```text
16 → 32
24 → 48
32 → 64
```

## 15.4 Каждый элемент хранится отдельно

Для каждого элемента:

* source;
* SVG, если применимо;
* PNG;
* WebP preview, если применимо;
* отдельные animation frames;
* sprite sheet;
* metadata JSON;
* usage documentation;
* reduced-motion fallback.

## 15.5 Metadata

```json
{
  "id": "mascot-success",
  "category": "feedback",
  "width": 64,
  "height": 64,
  "frames": 6,
  "durationMs": 600,
  "loop": false,
  "intensity": "medium",
  "reducedMotionFrame": 6,
  "usage": [
    "question-correct"
  ]
}
```

## 15.6 Режимы интенсивности

### Low

* navigation icons;
* bottom dock;
* static status;
* no decorative loops.

### Medium

* default;
* small feedback animation;
* static mascot;
* no particles.

### High

* achievements;
* celebration;
* contextual ambient art;
* максимум один focal effect.

## 15.7 Обязательные арты

* onboarding;
* no active session;
* empty search;
* empty library;
* no statistics;
* offline;
* reconnecting;
* sync success;
* sync failed;
* question updated;
* diagram error;
* code error;
* session complete;
* achievement;
* weak topic;
* maintenance.

## Результат

```text
docs/redesign/11_PIXEL_ART_SYSTEM.md
```

---

# 16. Фаза 9. Motion system

## Цель

Систематизировать движение.

## 16.1 Типы motion

### Micro feedback

* hover;
* selected;
* copied;
* saved;
* toggled.

### Structural

* drawer;
* dialog;
* route transition;
* layout expansion;
* collapse.

### Status

* loading;
* syncing;
* reconnecting;
* processing.

### Celebration

* session completed;
* achievement;
* level milestone.

## 16.2 Временная шкала

```text
100–160 ms — micro state
160–240 ms — hover/selection
200–320 ms — common transition
280–450 ms — structural transition
300–700 ms — feedback animation
700–1200 ms — celebration
```

## 16.3 Запреты

* бесконечная пульсация active navigation;
* постоянные floating elements;
* одновременно несколько celebration effects;
* motion, задерживающий CTA;
* shake всего экрана;
* red flash;
* portal для каждого перехода;
* coin rain на обычный ответ.

## 16.4 Reduced motion

Каждая анимация должна иметь:

* static fallback;
* reduced variant;
* no-motion variant.

## Результат

```text
docs/redesign/12_MOTION_SYSTEM.md
```

---

# 17. Фаза 10. Выбор frontend-архитектуры

## Цель

Выбрать реализацию, способную поддерживать новый интерфейс.

## 17.1 Провести сравнительный анализ

Сравнить:

* текущие серверные шаблоны;
* улучшенные серверные шаблоны;
* React;
* Vue;
* Svelte;
* другой подход.

Критерии:

* component model;
* state management;
* accessibility;
* testing;
* routing;
* bundle size;
* SSR;
* integration cost;
* migration complexity;
* team familiarity;
* lifecycle reliability.

## 17.2 Решение

Если текущий стек позволяет:

* нормальные компоненты;
* predictable state;
* route-level splitting;
* accessibility;
* tests;
* lifecycle;

можно сохранить.

Если нет — переписать.

## 17.3 Целевая структура

```text
src/
├── app/
│   ├── routing/
│   ├── providers/
│   ├── layouts/
│   └── bootstrap/
├── core/
│   ├── api/
│   ├── auth/
│   ├── errors/
│   ├── analytics/
│   └── storage/
├── design-system/
├── components/
├── features/
│   ├── session/
│   ├── question/
│   ├── feedback/
│   ├── review/
│   ├── library/
│   ├── progress/
│   └── settings/
├── pages/
├── assets/
└── tests/
```

## 17.4 Обязательные механизмы

* typed API;
* request cancellation;
* timeout;
* retry policy;
* stale-response protection;
* duplicate-submit protection;
* error boundary;
* route loading;
* offline detection;
* local persistence;
* feature flags;
* analytics events;
* page-level bundles.

## Результат

```text
docs/redesign/14_FRONTEND_ARCHITECTURE.md
```

---

# 18. Фаза 11. Новый application shell

## Цель

Создать первый крупный видимый результат.

## 18.1 Shell включает

* page container;
* top bar;
* desktop navigation;
* mobile dock;
* contextual action area;
* global feedback;
* command palette;
* theme;
* responsive layout;
* safe areas;
* route transitions.

## 18.2 Desktop layout

Предлагаемая структура:

```text
navigation
+ main content
+ optional contextual rail
+ bottom pixel dock
```

Contextual rail показывается только там, где полезен.

## 18.3 Mobile layout

```text
compact top bar
main content
contextual action tray
bottom pixel dock
safe area
```

## 18.4 Pixel dock

### Desktop

Может содержать:

* основные разделы;
* активную сессию;
* прогресс;
* shortcuts;
* sync;
* notifications.

### Mobile

Максимум пять действий:

```text
Главная
Тренировка
Повторение
Прогресс
Ещё
```

## 18.5 Acceptance criteria

* shell работает на всех маршрутах;
* нет horizontal scroll;
* dock не перекрывает контент;
* keyboard navigation корректна;
* focus visible;
* labels читаемы;
* active state понятен;
* safe area учитывается;
* dark/light готовы;
* reduced motion готов;
* baseline визуально заметно отличается.

## Обязательные screenshots

* home 390;
* home 768;
* home 1440;
* home 1920;
* dock default;
* dock selected;
* dock notification;
* dock offline;
* light;
* dark.

---

# 19. Фаза 12. Главная страница

## Цель

Сделать главную страницей следующего действия, а не dashboard.

## 19.1 Первый viewport

Должен содержать:

1. приветствие или текущую цель;
2. один primary CTA;
3. краткий context;
4. следующий полезный блок.

## 19.2 Сценарии

### Активная сессия

Primary:

```text
Продолжить тренировку
```

### Нет активной сессии

Primary:

```text
Начать тренировку
```

### Есть вопросы к повторению

Показывать вторичный блок:

```text
12 вопросов готовы к повторению
```

## 19.3 Удалить

* сетку из множества равнозначных карточек;
* полную аналитику;
* дублирующие CTA;
* длинные тексты;
* декоративные показатели;
* fake streak/XP, если они не подтверждены.

## 19.4 Backend data

Endpoint главной должен возвращать:

```json
{
  "primaryAction": {},
  "activeSession": {},
  "reviewQueue": {},
  "weakTopic": {},
  "recentActivity": [],
  "syncStatus": {}
}
```

Frontend не должен собирать главный экран из десяти независимых запросов без необходимости.

---

# 20. Фаза 13. Старт тренировки

## Цель

Сократить путь до первого вопроса.

## 20.1 Пресеты

* быстрая тренировка;
* слабые темы;
* повторение ошибок;
* подготовка к интервью;
* случайная;
* продолжить.

## 20.2 Wizard

### Шаг 1 — цель

```text
Быстрая практика
Подготовка к интервью
Повторение слабых тем
Свободная тренировка
```

### Шаг 2 — темы

* suggested;
* recent;
* weak;
* all topics;
* search.

### Шаг 3 — режим

* interview;
* learning;
* review;
* timed.

### Шаг 4 — длительность

* 5 вопросов;
* 10 вопросов;
* 20 вопросов;
* пользовательское значение.

### Шаг 5 — preview

Показывает:

* выбранные темы;
* число вопросов;
* режим;
* примерную длительность;
* start CTA.

## 20.3 Mobile

* один шаг на экран;
* sticky CTA;
* progress indicator;
* back;
* сохранение выбора;
* возврат после прерывания.

## 20.4 Backend

```text
GET /api/training/recommendations
POST /api/training/previews
POST /api/sessions
GET /api/sessions/{id}
POST /api/sessions/{id}/resume
```

---

# 21. Фаза 14. Экран вопроса

## Цель

Сделать его центральным и лучшим экраном продукта.

## 21.1 Desktop anatomy

```text
Session header
Question context
Question text
Code/diagram
Answer options
Primary action
Optional contextual rail
Bottom dock
```

## 21.2 Session header

Показывает только:

* номер вопроса;
* общий прогресс;
* тему;
* режим;
* возможность завершить.

Не показывать одновременно:

* повторение;
* policy;
* предыдущую ошибку;
* срок;
* несколько badges;
* длинное описание режима.

Вторичная информация — в details.

## 21.3 Question text

Требования:

* комфортная ширина;
* минимум агрессивных переносов;
* корректная русская типографика;
* поддержка inline code;
* поддержка списков;
* поддержка таблиц;
* поддержка длинных условий.

## 21.4 Answer option

Каждый вариант содержит:

* label;
* полный текст;
* selected indicator;
* shortcut;
* optional code;
* focus state.

Touch target — не меньше комфортного размера.

## 21.5 Submit

До выбора ответа:

* disabled;
* понятная причина при необходимости.

После выбора:

* активен;
* на mobile показывается в action tray над dock;
* не требует прокрутки вниз.

## 21.6 Keyboard

```text
1 / A — первый вариант
2 / B — второй
3 / C — третий
4 / D — четвёртый
Enter — проверить
N — следующий
E — открыть explanation
```

Shortcuts не должны конфликтовать с вводом текста.

## 21.7 Loading

При submit:

* disable duplicate submit;
* не менять ширину;
* показать processing;
* сохранить выбранный ответ;
* не блокировать весь экран.

## 21.8 Error

При ошибке:

* выбранный вариант сохраняется;
* пользователь видит причину;
* можно повторить;
* нет потери сессии;
* есть correlationId при server error.

---

# 22. Фаза 15. CodeBlock

## Цель

Создать полноценный технический компонент.

## Возможности

* filename;
* language;
* syntax highlighting;
* line numbers;
* highlighted lines;
* diff;
* copy;
* wrap;
* horizontal scroll;
* fullscreen;
* download;
* keyboard;
* error annotation;
* loading;
* render error;
* mobile layout.

## Mobile

* toolbar сворачивается;
* основные действия доступны;
* horizontal scroll не ломает страницу;
* fullscreen работает;
* font size читаемый.

## Accessibility

* доступная подпись;
* copy status;
* keyboard scroll;
* screen-reader summary;
* raw source option.

---

# 23. Фаза 16. DiagramViewer

## Цель

Создать безопасный и удобный просмотр схем.

## Возможности

* render;
* zoom;
* pan;
* fit;
* reset;
* fullscreen;
* copy source;
* show source;
* download;
* keyboard;
* touch;
* loading;
* error;
* textual fallback.

## Security

* strict Mermaid mode;
* SVG allowlist;
* запрет scripts;
* запрет foreignObject;
* запрет external resource;
* complexity limit;
* render timeout;
* sanitization tests.

## Lifecycle

* один renderer;
* no duplicate listeners;
* rerender при theme change;
* cleanup при route change.

---

# 24. Фаза 17. Feedback после ответа

## Цель

Сделать feedback учебным.

## 24.1 Incorrect

Структура:

```text
Неверно

Правильный ответ

Ключевое различие

Почему выбранный вариант выглядит правдоподобно

Где именно ошибка

Что запомнить

Связанные темы

Следующий вопрос
```

## 24.2 Correct

Структура:

```text
Верно

Ключевой вывод

Короткое подтверждение

Подробности, если нужны

Следующий вопрос
```

## 24.3 Разворачивание

На первом viewport:

* вердикт;
* правильный ответ;
* ключевое различие;
* следующий CTA.

Подробности ниже или в accordion.

## 24.4 Motion

Correct:

* одна короткая анимация.

Incorrect:

* спокойная визуальная реакция;
* без наказания.

## 24.5 Focus

После submit focus переводится на краткий feedback heading.

Не читать весь длинный текст через `aria-live`.

---

# 25. Фаза 18. Сессия и состояние

## Цель

Сделать сессию устойчивой к повторам, перезагрузке и сети.

## Состояния

```text
CREATED
ACTIVE
PAUSED
COMPLETED
ABANDONED
EXPIRED
```

## Для каждой сессии хранить

* session id;
* user id;
* mode;
* selected topics;
* question order;
* current position;
* answers;
* timestamps;
* completion state;
* device sync state;
* version.

## Submit contract

```text
POST /api/sessions/{sessionId}/questions/{questionId}/attempts
Idempotency-Key: ...
```

Backend должен:

* проверить сессию;
* проверить ревизию;
* проверить idempotency;
* сохранить attempt;
* обновить review;
* обновить progress;
* вернуть feedback;
* выполнить всё атомарно.

---

# 26. Фаза 19. Session summary

## Цель

Перевести результат в следующее действие.

## Первый viewport

1. завершение;
2. результат;
3. ключевой вывод;
4. слабая тема;
5. primary CTA.

## Primary CTA

Определяется backend recommendation engine:

* повторить ошибки;
* пройти слабую тему;
* продолжить план;
* начать новую сессию.

## Secondary

Одна кнопка.

## Overflow

* экспорт;
* печать;
* копирование;
* подробная аналитика;
* поделиться.

## Не показывать

* семь кнопок;
* десятки badges;
* fake coins;
* несколько celebration animations.

---

# 27. Фаза 20. Повторение

## Цель

Сделать повторение отдельным сильным сценарием.

## Очереди

* due now;
* overdue;
* mistakes;
* weak topics;
* bookmarked;
* incomplete explanations.

## Карточка повторения

Содержит:

* тему;
* причину попадания;
* последний результат;
* срок;
* estimated time;
* start CTA.

## Backend

Должен объяснять причину:

```json
{
  "reason": "LOW_FIRST_ATTEMPT_ACCURACY",
  "message": "В этой теме 3 ошибки из последних 5 попыток"
}
```

---

# 28. Фаза 21. Библиотека

## Цель

Обеспечить поиск и изучение материалов.

## Функции

* search;
* topic tree;
* filters;
* difficulty;
* status;
* bookmarks;
* recently viewed;
* question preview;
* related topics.

## Search states

* initial;
* typing;
* loading;
* results;
* no results;
* error;
* offline cached.

## Mobile

* filters drawer;
* sticky search;
* compact cards;
* clear reset.

---

# 29. Фаза 22. Аналитика

## Цель

Давать решения, а не декоративные графики.

## Главные показатели

* first-attempt accuracy;
* overall accuracy;
* completed sessions;
* review adherence;
* weak topic count;
* learning velocity;
* consistency.

## Для каждой метрики

* определение;
* формула;
* denominator;
* период;
* data source;
* update frequency;
* interpretation;
* suggested action.

## Страница

### Section 1 — summary

* ключевой результат;
* изменение;
* рекомендация.

### Section 2 — weak topics

* ranked list;
* причина;
* action.

### Section 3 — trend

* chart;
* text summary;
* table fallback.

### Section 4 — history

* sessions;
* filters;
* drill-down.

## Запрещено

* pie chart с большим числом сегментов;
* 3D;
* график без единиц;
* график без периода;
* показатель без объяснения;
* fake data;
* vanity metrics.

---

# 30. Фаза 23. Настройки

## Цель

Сделать настройки короткими и управляемыми.

## Разделы

### Обучение

* default mode;
* session length;
* difficulty;
* review preferences.

### Интерфейс

* theme;
* density;
* pixel-art intensity;
* font size;
* code settings.

### Доступность

* reduced motion;
* contrast;
* shortcuts;
* screen-reader enhancements.

### Уведомления

* review reminders;
* session reminders;
* email/push, если есть.

### Данные

* export;
* import;
* clear history;
* delete account.

## Mobile

Каждый раздел — отдельный экран.

## Preview

Для visual settings показывать live preview.

---

# 31. Фаза 24. Backend architecture

## Цель

Поддержать новый UX честной и устойчивой моделью.

## Основные модули

```text
identity
content
question
session
attempt
review
recommendation
analytics
settings
achievement
notification
```

## Сервисы

* SessionService;
* AttemptService;
* ReviewService;
* RecommendationService;
* AnalyticsService;
* QuestionRevisionService;
* UserSettingsService.

## Transaction boundaries

Attempt submit должен атомарно:

1. проверить idempotency;
2. найти revision;
3. сохранить attempt;
4. обновить session;
5. обновить review;
6. создать analytics events;
7. вернуть feedback.

## Question revision

```text
question
question_revision
answer_option_revision
attempt.question_revision_id
```

История не должна изменяться после редактирования вопроса.

## Error contract

```json
{
  "type": "https://example/errors/session-expired",
  "title": "Session expired",
  "status": 409,
  "detail": "The training session has expired",
  "instance": "/api/sessions/123",
  "correlationId": "...",
  "retryable": false,
  "fieldErrors": []
}
```

---

# 32. Фаза 25. Data model

## Обязательные сущности

* user;
* user_settings;
* topic;
* question;
* question_revision;
* answer_option_revision;
* session;
* session_question;
* attempt;
* review_item;
* bookmark;
* achievement;
* analytics_event;
* idempotency_record.

## Проверить

* foreign keys;
* indexes;
* unique constraints;
* timestamps;
* soft delete;
* revision immutability;
* migration rollback;
* data retention;
* privacy.

## Миграции

Для каждой:

* forward migration;
* data migration;
* validation query;
* rollback plan;
* production impact.

---

# 33. Фаза 26. API design

## Правила

* consistent naming;
* pagination;
* filtering;
* sorting;
* validation;
* error contract;
* versioning;
* idempotency;
* correlation id;
* caching policy.

## Основные endpoints

```text
GET    /api/home
GET    /api/topics
GET    /api/questions
GET    /api/review
GET    /api/progress
GET    /api/settings

POST   /api/training/previews
POST   /api/sessions
GET    /api/sessions/{id}
POST   /api/sessions/{id}/resume
POST   /api/sessions/{id}/pause
POST   /api/sessions/{id}/complete

GET    /api/sessions/{id}/questions/current
POST   /api/sessions/{id}/questions/{questionId}/attempts

PATCH  /api/settings
POST   /api/bookmarks
DELETE /api/bookmarks/{id}
```

---

# 34. Фаза 27. Accessibility

## Цель

WCAG 2.2 AA.

## Проверить

* semantic headings;
* landmarks;
* keyboard;
* focus order;
* focus visible;
* skip links;
* dialogs;
* drawers;
* tooltips;
* labels;
* descriptions;
* live regions;
* error announcements;
* contrast;
* target size;
* zoom 200%;
* text spacing;
* reflow;
* reduced motion;
* screen reader;
* charts;
* code;
* diagrams.

## Обязательные ручные проверки

1. пройти сессию без мыши;
2. пройти сессию с screen reader;
3. пройти mobile при 200%;
4. пройти при reduced motion;
5. пройти при high contrast;
6. пройти с увеличенным текстом.

## Документировать

```text
Проверка
Инструмент
Результат
Проблема
Исправление
Повторная проверка
```

---

# 35. Фаза 28. Performance

## Budgets

Определить конкретные ограничения.

Пример:

```text
Initial JS gzip: ≤ 180 KB
Route JS gzip: ≤ 80 KB
Critical CSS gzip: ≤ 35 KB
LCP: ≤ 2.5 s
INP: ≤ 200 ms
CLS: ≤ 0.1
```

Значения корректируются под стек, но обязательно фиксируются.

## Проверить

* bundle splitting;
* unused code;
* unused CSS;
* fonts;
* images;
* sprite sheets;
* GIF;
* Mermaid;
* chart library;
* route loading;
* prefetch;
* cache;
* compression;
* CDN;
* server timing.

## Pixel assets

Не загружать:

* achievements на question screen;
* portal animation на every page;
* все mascot states в initial bundle;
* gallery assets в production route.

---

# 36. Фаза 29. Security

## Проверить

* authentication;
* authorization;
* CSRF;
* XSS;
* CSP;
* Markdown sanitization;
* Mermaid sanitization;
* SVG sanitization;
* file upload;
* rate limiting;
* session fixation;
* idempotency abuse;
* SQL injection;
* mass assignment;
* secrets;
* logging PII.

## SVG

Внешний SVG нельзя автоматически считать безопасным.

Использовать:

* allowlist;
* remove script;
* remove event handlers;
* remove foreignObject;
* remove external links;
* remove embedded data;
* safe attributes only.

---

# 37. Фаза 30. Testing

## Backend tests

* unit;
* integration;
* repository;
* migration;
* transaction;
* concurrency;
* idempotency;
* authorization;
* security;
* analytics formulas.

## Frontend tests

* component;
* state;
* form;
* API errors;
* keyboard;
* focus;
* responsive;
* reduced motion;
* themes.

## E2E

1. onboarding;
2. quick start;
3. custom session;
4. correct answer;
5. incorrect answer;
6. duplicate submit;
7. offline submit;
8. reconnect;
9. resume;
10. session complete;
11. review queue;
12. analytics;
13. settings;
14. keyboard-only;
15. mobile.

## Visual regression

Для каждого основного экрана:

```text
320
390
768
1280
1440
1920
dark
light
loading
empty
error
long
```

---

# 38. Фаза 31. Миграция старого frontend

## Порядок

1. design system;
2. new shell;
3. home;
4. session start;
5. question;
6. feedback;
7. summary;
8. review;
9. library;
10. analytics;
11. settings;
12. remove old frontend.

## После каждого переноса

* route переключён;
* feature flag проверен;
* analytics работают;
* E2E обновлены;
* old CSS удалён;
* old JavaScript удалён;
* unused assets удалены;
* docs обновлены.

## Запрет

Не оставлять две полноценные реализации после завершения миграции.

---

# 39. Подробный порядок первых 15 итераций

## Итерация 1 — достоверный baseline

Результат:

* приложение запущено;
* screenshots;
* browser errors;
* performance;
* accessibility;
* screen inventory.

Никаких визуальных изменений.

## Итерация 2 — продуктовая структура

Результат:

* IA;
* routes;
* user flows;
* primary actions;
* удаляемые функции;
* переносимые функции.

## Итерация 3 — три визуальные концепции

Результат:

* 3 направления;
* одинаковый набор экранов;
* evaluation matrix;
* выбранное направление.

## Итерация 4 — tokens

Результат:

* color system;
* typography;
* spacing;
* layout;
* motion;
* themes.

## Итерация 5 — core components

Результат:

* Button;
* Input;
* Tabs;
* Surface;
* Tooltip;
* Dialog;
* PixelIcon.

## Итерация 6 — новый shell

Результат:

* navigation;
* layout;
* theme;
* mobile structure;
* screenshots.

## Итерация 7 — новый pixel dock

Результат:

* desktop;
* mobile;
* active;
* focus;
* notification;
* offline;
* safe area.

## Итерация 8 — новая главная

Результат:

* primary action;
* active session;
* recommendation;
* review queue;
* no dashboard clutter.

## Итерация 9 — session start

Результат:

* presets;
* wizard;
* preview;
* backend contract;
* mobile flow.

## Итерация 10 — question base

Результат:

* question layout;
* options;
* selected;
* keyboard;
* mobile CTA.

## Итерация 11 — code and diagrams

Результат:

* CodeBlock;
* DiagramViewer;
* mobile;
* security;
* fallbacks.

## Итерация 12 — submit and feedback

Результат:

* idempotent submit;
* correct;
* incorrect;
* focus;
* retry;
* error handling.

## Итерация 13 — session summary

Результат:

* result;
* weak topics;
* recommendation;
* primary CTA;
* overflow.

## Итерация 14 — review

Результат:

* queue;
* reasons;
* session creation;
* weak topics.

## Итерация 15 — analytics

Результат:

* definitions;
* charts;
* actions;
* table fallbacks;
* mobile.

---

# 40. Формат каждой итерации

## До реализации

Создать запись:

```text
Iteration ID
Проблема
Почему это важно
Пользовательский ущерб
Baseline
Целевой результат
Scope
Out of scope
Dependencies
Risks
Acceptance criteria
Test plan
Screenshot plan
```

## Во время реализации

Исполнитель обязан:

1. изменить реальный код;
2. интегрировать backend и frontend;
3. реализовать states;
4. реализовать mobile;
5. реализовать accessibility;
6. добавить тесты;
7. удалить заменённый код;
8. обновить docs.

## После реализации

Отчёт:

```text
Что изменено
Почему решение лучше
Изменённые файлы
Удалённые файлы
Backend changes
Frontend changes
UI changes
UX changes
Accessibility
Performance
Tests
Screenshots
Known limitations
Next priority
```

---

# 41. Матрица приёмки экрана

Каждый экран оценивается по 0–2.

```text
0 — отсутствует или сломано
1 — работает частично
2 — полностью выполнено
```

Критерии:

* цель понятна;
* primary action очевиден;
* content hierarchy;
* desktop;
* tablet;
* mobile;
* dark;
* light;
* loading;
* empty;
* error;
* long content;
* keyboard;
* screen reader;
* focus;
* contrast;
* reduced motion;
* performance;
* tests;
* visual regression.

Экран считается готовым только при:

* отсутствии 0;
* не более двух значений 1;
* обязательные критерии имеют 2.

Обязательные:

* цель;
* primary action;
* mobile;
* keyboard;
* error;
* accessibility;
* tests.

---

# 42. Метрики до и после

## UX

* actions to first question;
* time to first question;
* actions to next question;
* number of visible CTAs;
* number of competing visual accents;
* scroll distance;
* completion rate;
* retry success.

## UI

* number of card containers;
* number of hardcoded colors;
* number of icon systems;
* number of `!important`;
* number of breakpoints;
* number of animation loops;
* number of broken assets.

## Frontend

* bundle size;
* module size;
* duplicated listeners;
* console errors;
* memory growth;
* route load time.

## Backend

* duplicate attempts;
* transaction failures;
* API error rate;
* idempotency conflicts;
* analytics consistency;
* recommendation latency.

## Accessibility

* automated violations;
* keyboard blockers;
* unlabeled controls;
* contrast failures;
* focus issues.

---

# 43. Критерий заметного визуального улучшения

Редизайн считается визуально значимым только если на side-by-side сравнении видно:

* новая композиция;
* новая иерархия;
* новая навигация;
* новая работа с пространством;
* новая typography;
* новая система состояний;
* новый pixel dock;
* системный pixel-art;
* улучшенный mobile flow;
* уменьшение перегрузки.

Не считается значимым:

* немного другой оттенок;
* другие тени;
* другой radius;
* новый glow;
* перемещение карточки;
* добавление стикера;
* новая анимация поверх старой структуры.

---

# 44. Обязательная первая крупная поставка

Первая поставка должна включать:

1. runtime baseline;
2. выбранную визуальную концепцию;
3. design tokens;
4. core components;
5. новый shell;
6. новый desktop navigation;
7. новый mobile pixel dock;
8. новую главную;
9. новый экран вопроса;
10. selected answer;
11. correct feedback;
12. incorrect feedback;
13. dark theme;
14. light theme;
15. responsive screenshots;
16. keyboard navigation;
17. accessibility checks;
18. backend submit contract;
19. tests;
20. удалённый старый код.

Пока этой поставки нет, запрещено утверждать, что редизайн продвигается успешно.

---

# 45. Условия полного завершения

Проект считается завершённым, когда:

* старый frontend удалён;
* все основные маршруты переведены;
* новый визуальный стиль используется везде;
* pixel-art системен;
* dock уникален;
* главный flow удобен;
* feedback обучает;
* mobile полноценен;
* analytics полезна;
* settings структурированы;
* empty/error/offline готовы;
* backend поддерживает UX;
* idempotency доказана;
* revisions корректны;
* accessibility WCAG 2.2 AA;
* performance budget соблюдён;
* security audit пройден;
* E2E пройдены;
* visual regression стабилен;
* docs соответствуют runtime;
* нет P0;
* нет значимых P1;
* дальнейшие изменения субъективны.

---

# 46. Запрет на преждевременную остановку

Нельзя останавливать работу с формулировками:

* «интерфейс уже достаточно хороший»;
* «можно улучшать бесконечно»;
* «основные стили обновлены»;
* «дальше нужна дизайнерская оценка»;
* «следующий этап можно выполнить позже».

Остановка допустима только при выполнении Definition of Done или при объективном блокере, который подробно зафиксирован.

---

# 47. Финальное указание исполнителю

Не сохраняй текущий интерфейс только потому, что его проще редактировать.

Не бойся удалить слабую реализацию.

Не создавай косметический diff.

Не ограничивайся CSS.

Не имитируй редизайн.

Каждое крупное изменение должно быть видно пользователю, улучшать его сценарий и подтверждаться работающим кодом.

Работай в следующем порядке:

```text
понять проблему
→ спроектировать сценарий
→ создать дизайн
→ определить backend
→ реализовать frontend
→ реализовать states
→ реализовать mobile
→ обеспечить accessibility
→ протестировать
→ сделать screenshots
→ сравнить before/after
→ удалить старую реализацию
→ зафиксировать результат
```

Главный критерий:

> Пользователь должен сразу увидеть, что продукт стал другим, и сразу почувствовать, что пользоваться им стало удобнее.
