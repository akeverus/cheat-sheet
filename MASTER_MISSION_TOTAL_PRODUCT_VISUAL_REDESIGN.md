# MASTER MISSION --- TOTAL PRODUCT VISUAL REDESIGN

## Cheat Sheet --- UI / UX / Frontend / Backend

> **Mission:** полностью переосмыслить и переработать визуальную
> систему, UI, UX, композицию, responsive-поведение, frontend
> architecture и, при необходимости, связанную backend-логику
> существующего проекта.

Итог должен выглядеть как продукт, который можно было бы выпустить в
2026 году как самостоятельный **premium developer-learning product**.

Не как pet project.\
Не как Bootstrap dashboard.\
Не как «старый интерфейс с новым цветом».\
Не как «medieval skin».\
Не как набор карточек.

Цель:

**PREMIUM MODERN LEARNING PRODUCT**\
+ **STRONG VISUAL IDENTITY**\
+ **EXCELLENT INFORMATION DESIGN**\
+ **FAST WORKFLOW**\
+ **HIGH INFORMATION DENSITY WITHOUT CLUTTER**\
+ **MACOS-FIRST DESKTOP EXPERIENCE**\
+ **EXCELLENT MOBILE EXPERIENCE**\
+ **PRODUCTION-QUALITY FRONTEND**

------------------------------------------------------------------------

# 0. ABSOLUTE AUTHORITY

Это задание имеет приоритет над предыдущими субъективными
дизайн-решениями проекта.

Существующие:

-   FRD;
-   design decisions;
-   `AUDITED` decisions;
-   CSS decisions;
-   screenshots;
-   visual baselines;
-   pixel-art decisions;
-   color decisions;
-   typography decisions;
-   layout decisions;
-   navigation decisions

**НЕ являются неприкосновенными.**

Они являются **REFERENCE MATERIAL**.

Необходимо изучить причины их появления и самостоятельно решить, какие
из них всё ещё хороши.

Если существующее решение мешает более качественному продукту ---
**ОТМЕНИ ЕГО**.

Если нужно отменить десять предыдущих решений --- отмени десять.

Если нужно отменить сто --- отмени сто.

Не сохраняй плохой дизайн ради истории проекта.

> `AUDITED` означает: «это было проверено ранее».\
> `AUDITED` НЕ означает: «это нельзя улучшить».

------------------------------------------------------------------------

# 1. ГЛАВНОЕ ПРАВИЛО

## НЕ ПЫТАЙСЯ СДЕЛАТЬ ТЕКУЩИЙ ДИЗАЙН КРАСИВЕЕ.

## СДЕЛАЙ ЛУЧШИЙ ДИЗАЙН.

Это принципиально разные задачи.

Запрещён подход:

> «оставим текущую структуру и поменяем цвета».

Запрещён подход:

> «добавим несколько современных компонентов».

Запрещён подход:

> «добавим больше pixel art».

Запрещён подход:

> «сделаем ещё одну итерацию существующего FRD».

Нужна возможность сказать:

> **«Вся текущая композиция была ошибкой».**

И полностью её заменить.

------------------------------------------------------------------------

# 2. НЕ БОЙСЯ ПОЛНОСТЬЮ ОТКАЗАТЬСЯ ОТ PIXEL ART

Pixel art **НЕ является обязательным**.

Средневековая тематика **НЕ является обязательной**.

Текущая `guild workshop` концепция **НЕ является обязательной**.

Если после анализа выясняется, что:

-   pixel art делает интерфейс дешевле;
-   medieval metaphors мешают usability;
-   гербы перегружают UI;
-   parchment выглядит искусственно;
-   fantasy-подход мешает ощущению premium software

--- **ОТКАЖИСЬ ОТ НИХ**.

Не пытайся искусственно сохранить тему.

Приоритет:

1.  UX;
2.  visual quality;
3.  information hierarchy;
4.  usability;
5.  readability;
6.  product identity;
7.  emotional character;
8.  thematic elements.

А НЕ наоборот.

Если pixel art хорошо работает только в 5% интерфейса --- оставь его в
этих 5%.

Если лучше вообще отказаться --- откажись.

Если medieval metaphor можно сохранить только как subtle visual DNA ---
сделай именно так.

------------------------------------------------------------------------

# 3. ЦЕЛЕВОЙ ВИЗУАЛЬНЫЙ УРОВЕНЬ

Представь, что продукт проектирует команда:

-   Principal Product Designer;
-   Art Director;
-   Senior UX Designer;
-   Senior Frontend Engineer;
-   Design Systems Engineer;
-   Motion Designer;
-   Accessibility Specialist.

Целевой уровень:

**2026 premium SaaS / developer tooling / learning platform.**

Ориентиры по качеству:

-   Linear;
-   Raycast;
-   Arc;
-   Vercel;
-   Stripe;
-   Notion;
-   Craft;
-   Things;
-   Apple;
-   GitHub;
-   современные IDE;
-   premium developer education products.

**НЕ КОПИРУЙ их.**

Изучи их принципы:

-   hierarchy;
-   density;
-   motion;
-   typography;
-   navigation;
-   state management;
-   spatial composition;
-   responsive behavior;
-   command surfaces;
-   progressive disclosure.

Создай собственную систему.

------------------------------------------------------------------------

# 4. ПЕРВЫЙ ЭТАП --- FORENSIC AUDIT

Перед изменением production-кода полностью изучи repository.

Обязательно исследуй:

-   `docs/`;
-   `src/`;
-   `templates/`;
-   CSS;
-   JS;
-   tests;
-   e2e;
-   visual baselines;
-   design decision files;
-   frontend redesign state;
-   architecture documentation;
-   history.

Особенно:

-   все существующие страницы;
-   все fragments;
-   все CSS layers;
-   tokens;
-   page-specific CSS;
-   JS modules;
-   existing interaction patterns;
-   visual tests;
-   UI contract tests;
-   current screenshots.

**Не начинай сразу писать CSS.**

Сначала сформируй внутреннюю модель:

-   WHAT EXISTS
-   WHAT IS GOOD
-   WHAT IS BAD
-   WHAT IS ACCIDENTAL
-   WHAT IS FUNDAMENTAL
-   WHAT IS OBSOLETE
-   WHAT IS OVERENGINEERED
-   WHAT IS MISSING

------------------------------------------------------------------------

# 5. ИЗУЧИ ВСЕ ЭКРАНЫ

Обязательно проверить:

-   Home;
-   Training;
-   Topics;
-   Analytics;
-   Bookmarks;
-   Settings;
-   Session Summary;
-   Login;
-   Error;
-   Result;
-   Focus;
-   Typed Recall;
-   Interview;
-   Code Challenge;
-   Study;
-   Flashcard;
-   Exam;
-   Marathon.

Не считать:

> «если Home красивый, остальные автоматически красивые».

Каждый экран должен иметь собственную композиционную логику.

------------------------------------------------------------------------

# 6. ИЗУЧИ ВСЕ 8 MODES

Особое внимание:

-   TRAINING;
-   STUDY;
-   FLASHCARD;
-   TYPED_RECALL;
-   INTERVIEW;
-   CODE_CHALLENGE;
-   EXAM;
-   MARATHON.

Категорически запрещено строить один универсальный layout, а затем
просто менять его содержимое.

Режимы имеют разные cognitive tasks.

Следовательно, они должны иметь разные workspace compositions.

Пример принципа:

### MCQ

`question → choices → action → feedback`

### TYPED RECALL

`question → recall workspace → input → comparison`

### INTERVIEW

`question → thinking structure → timer → notes → self-evaluation`

### CODE

`problem → code workspace → controls → execution/result`

### STUDY

`content → explanation → examples → progression`

### FLASHCARD

`prompt → focused reveal → evaluation`

### EXAM

`minimal distraction → question → answer → progression`

### MARATHON

`high-throughput navigation → question → immediate action`

Это **не означает**, что именно эти layouts должны быть реализованы.

Это означает:

> **НЕ ЗАСТАВЛЯЙ РАЗНЫЕ COGNITIVE WORKFLOWS ЖИТЬ В ОДНОЙ КОМПОЗИЦИОННОЙ
> ФОРМЕ.**

------------------------------------------------------------------------

# 7. ПРОВЕДИ DESIGN RESET

После анализа сформируй новый design hypothesis.

Создай минимум три концептуальных направления:

### DIRECTION A

Modern Developer Learning OS

### DIRECTION B

Premium Digital Academy

### DIRECTION C

Contemporary Editorial / Knowledge Workspace

Можно использовать совершенно другие направления.

Для каждого оцени:

-   visual identity;
-   readability;
-   information density;
-   emotional character;
-   scalability;
-   mobile;
-   dark;
-   light;
-   analytics;
-   quiz;
-   code;
-   accessibility;
-   maintainability.

Затем выбери **ЛУЧШЕЕ**.

Не выбирай автоматически medieval.

Не выбирай автоматически pixel art.

Не выбирай вариант только потому, что он похож на текущий.

------------------------------------------------------------------------

# 8. СОЗДАЙ НОВУЮ DESIGN LANGUAGE

До массовой реализации сформируй:

-   COLOR SYSTEM;
-   TYPOGRAPHY SYSTEM;
-   SPACING SYSTEM;
-   RADIUS SYSTEM;
-   ELEVATION SYSTEM;
-   BORDER SYSTEM;
-   ICON SYSTEM;
-   MOTION SYSTEM;
-   SURFACE SYSTEM;
-   CONTENT WIDTH SYSTEM;
-   DENSITY SYSTEM;
-   STATE SYSTEM.

Все дальнейшие экраны должны использовать эту систему.

------------------------------------------------------------------------

# 9. COLOR SYSTEM

Не используй случайные цвета.

Создай semantic tokens.

Минимум:

-   `background`;
-   `background-elevated`;
-   `surface`;
-   `surface-raised`;
-   `surface-hover`;
-   `surface-active`;
-   `surface-selected`;
-   `surface-inset`;
-   `border-subtle`;
-   `border-default`;
-   `border-strong`;
-   `text-primary`;
-   `text-secondary`;
-   `text-tertiary`;
-   `text-disabled`;
-   `accent`;
-   `accent-hover`;
-   `accent-active`;
-   `success`;
-   `warning`;
-   `danger`;
-   `info`;
-   `focus`.

Dark и light должны проектироваться одновременно.

НЕ делай:

`light = dark + invert()`

НЕ делай:

`dark = black + gold`

НЕ делай:

`light = beige + brown`

Палитра должна иметь современный характер.

Можно использовать:

-   neutral;
-   warm neutral;
-   cool neutral;
-   muted accent;
-   vivid accent;
-   subtle gradient;
-   glass;
-   translucent layers

если это улучшает продукт.

Но каждый эффект должен иметь функцию.

------------------------------------------------------------------------

# 10. TYPOGRAPHY

Полностью пересмотри typography.

Проверь:

-   font family;
-   Cyrillic quality;
-   Latin quality;
-   numerals;
-   code;
-   UI labels;
-   table density;
-   mobile.

Не используй декоративный font там, где нужно читать много информации.

Создай:

-   Display;
-   H1;
-   H2;
-   H3;
-   Body Large;
-   Body;
-   Body Small;
-   Label;
-   Caption;
-   Code;
-   Mono Small;
-   Numeric.

Typography должна создавать hierarchy, а не просто увеличивать размеры.

------------------------------------------------------------------------

# 11. SPACING

Создай системную spacing scale.

Не допускай хаотичных значений вроде:

``` text
margin: 13px
margin: 17px
margin: 23px
margin: 29px
```

без причины.

Используй ограниченный набор rhythm values.

Но не превращай spacing system в религию.

Если композиционно требуется exception --- обоснуй его.

------------------------------------------------------------------------

# 12. RADIUS

Полностью пересмотри идею:

> «всё прямоугольное»

Радиусы могут быть:

-   0;
-   2;
-   4;
-   6;
-   8;
-   12;
-   16;
-   20;

или другая система.

Но форма должна быть частью identity.

Не смешивай:

-   square button;
-   pill badge;
-   18px card;
-   2px input

без системного основания.

------------------------------------------------------------------------

# 13. SURFACE LANGUAGE

Создай понятную иерархию поверхностей.

**Не делай всё карточками.**

Современный интерфейс не обязан представлять каждый блок как:

``` text
┌─────────────────┐
│ CARD            │
└─────────────────┘
```

Используй:

-   open layouts;
-   dividers;
-   grouped regions;
-   elevated surfaces;
-   subtle backgrounds;
-   sections;
-   overlays;
-   drawers;
-   floating panels;
-   command bars;
-   inline controls.

Карточка должна появляться, когда она действительно помогает
группировать информацию.

------------------------------------------------------------------------

# 14. NAVIGATION --- ПЕРЕПРОЕКТИРОВАТЬ

Текущий sidebar не считать обязательным.

Исследуй:

-   sidebar;
-   compact sidebar;
-   top navigation;
-   hybrid navigation;
-   command palette;
-   contextual navigation;
-   mobile bottom navigation;
-   responsive navigation.

Навигация должна отражать реальные сущности продукта, а не техническую
структуру backend.

Пользователь должен понимать:

-   где я;
-   что я могу сделать;
-   что сейчас важно;
-   куда идти дальше

за одну-две секунды.

------------------------------------------------------------------------

# 15. APPLICATION SHELL

Полностью переосмыслить shell.

Определи:

-   global navigation;
-   page header;
-   breadcrumbs;
-   context controls;
-   command surface;
-   status;
-   profile/settings;
-   mobile navigation.

Не создавай огромную неподвижную chrome-area.

Shell должен отдавать максимум пространства продукту.

------------------------------------------------------------------------

# 16. ГЛАВНОЕ ПРАВИЛО ПРОСТРАНСТВА

## НЕ ОСТАВЛЯЙ БЕСПОЛЕЗНОЕ ПУСТОЕ ПРОСТРАНСТВО.

Особенно:

-   MacBook;
-   1440;
-   1728;
-   1920;
-   2560. 

Но:

## НЕ ЗАПОЛНЯЙ ПУСТОТУ ДЕКОРАЦИЯМИ.

Если справа 500px свободного места --- не вставляй картинку ради
заполнения.

Спроси:

Можно ли разместить там:

-   context;
-   metadata;
-   progress;
-   navigation;
-   related content;
-   session status;
-   secondary action;
-   explanation;
-   preview;
-   inspector.

Если ничего полезного там нет --- **перестрой композицию**.

------------------------------------------------------------------------

# 17. MACOS-FIRST

**MACOS --- ПЕРВАЯ ЦЕЛЕВАЯ DESKTOP-ПЛАТФОРМА.**

Проверяй минимум:

-   1440;
-   1512;
-   1728;
-   1920;
-   2560. 

И реальные пропорции macOS scaling:

-   110%;
-   125%;
-   200%.

Особое внимание:

-   Safari/WebKit;
-   Retina;
-   font rendering;
-   fractional pixels;
-   sticky;
-   overflow;
-   scrollbars;
-   form controls;
-   focus;
-   viewport height.

Не проектируй только под Windows Chrome.

------------------------------------------------------------------------

# 18. MACBOOK TEST

Особенно проверить:

-   1366-ish laptop;
-   1440-ish laptop;
-   1512×982;
-   1728×1117.

Приоритет:

## FIRST VIEWPORT.

Пользователь должен видеть рабочую поверхность без ощущения:

> «половина экрана пропала».

------------------------------------------------------------------------

# 19. ULTRAWIDE

2560px не должен быть просто:

> обычный layout + огромные поля.

Возможные решения:

-   wider workspace;
-   multi-column;
-   contextual inspector;
-   split view;
-   secondary information;
-   adaptive rail;
-   larger code surface;
-   richer analytics.

Но не расширяй текст до 1800px.

**Workspace width и reading width --- разные понятия.**

------------------------------------------------------------------------

# 20. MOBILE

Mobile не является:

> desktop / 4.

Responsive должен быть recomposition.

Можно:

-   менять порядок;
-   менять navigation;
-   скрывать secondary information;
-   превращать rail в drawer;
-   превращать side panel в bottom sheet;
-   менять grid;
-   менять interaction model.

Mobile должен ощущаться специально спроектированным.

------------------------------------------------------------------------

# 21. TABLET / NARROW DESKTOP

Отдельно тестировать:

-   768;
-   834;
-   1024;
-   1100;
-   1180;
-   1279. 

Не допускать:

> desktop layout, который «ещё вроде помещается», но уже выглядит
> ужасно.

Используй композиционные breakpoints, а не только device breakpoints.

------------------------------------------------------------------------

# 22. FIRST VIEWPORT

Каждая ключевая страница должна иметь сильный первый viewport.

Above-the-fold должен отвечать:

-   где я;
-   что происходит;
-   что главное;
-   что делать;
-   какой прогресс;
-   какие вторичные действия.

Не заставляй пользователя скроллить, чтобы понять смысл страницы.

------------------------------------------------------------------------

# 23. HOME / TRAINING

Главная --- центральный продуктовый экран.

Полностью перепроектировать.

Она должна показывать:

-   текущий контекст;
-   что делать сейчас;
-   прогресс;
-   доступные действия;
-   session state;
-   meaningful statistics;
-   learning momentum.

Не превращай Home в dashboard из 15 карточек.

Главный action должен быть очевиден.

------------------------------------------------------------------------

# 24. QUESTION WORKSPACE

Экран вопроса --- самый важный.

Проведи полный redesign.

Не исходи из:

> «есть question + 4 cards».

Сначала ответь:

-   что пользователь должен делать;
-   где его внимание;
-   что является primary;
-   что secondary;
-   что должно исчезать;
-   что должно оставаться;
-   что должно быть доступно без scroll;
-   как меняется композиция после ответа.

------------------------------------------------------------------------

# 25. MCQ

Варианты ответа должны быть:

-   легко сканируемыми;
-   keyboard friendly;
-   touch friendly;
-   визуально равноправными;
-   без визуальной подсказки correct;
-   хорошо различимыми;
-   устойчивыми к длинному тексту.

Не превращай четыре варианта в четыре гигантские карточки.

Исследуй:

-   compact options;
-   list options;
-   selectable rows;
-   keyboard index;
-   split answer layout;
-   adaptive grid.

Выбери лучшее.

------------------------------------------------------------------------

# 26. POST-ANSWER

После ответа не должно происходить:

> «страница стала в три раза длиннее».

Нужна визуальная трансформация:

`QUESTION → VERDICT → LEARNING`

Переход должен быть понятен.

Возможны:

-   progressive disclosure;
-   inline explanation;
-   side inspector;
-   split state;
-   expandable details;
-   anchored feedback;
-   tabs.

Но не превращай всё в accordion hell.

------------------------------------------------------------------------

# 27. TYPED RECALL

Не делать:

> MCQ без MCQ.

Создай настоящий recall workspace.

Приоритет:

-   thinking;
-   recall;
-   answer composition;
-   comparison;
-   evaluation.

------------------------------------------------------------------------

# 28. INTERVIEW

INTERVIEW должен выглядеть как подготовка к реальному техническому
интервью.

Используй:

-   prompt;
-   thinking structure;
-   checklist;
-   timer;
-   notes;
-   evaluation;
-   progression.

Но не превращай его в game HUD.

------------------------------------------------------------------------

# 29. CODE CHALLENGE

Code Challenge должен ощущаться как developer tool.

Приоритет:

-   code;
-   task;
-   context;
-   execution;
-   feedback.

Код должен получать пространство.

Не зажимай editor ради декоративного sidebar.

------------------------------------------------------------------------

# 30. STUDY

Study должен быть reading experience.

Не quiz shell.

Приоритет:

-   typography;
-   content hierarchy;
-   examples;
-   code;
-   diagrams;
-   progressive learning.

Reading measure должна быть контролируемой.

------------------------------------------------------------------------

# 31. FLASHCARD

Flashcard должен быть намеренно минималистичным.

Не перегружай.

Одна задача:

`PROMPT → RECALL → REVEAL → EVALUATE`

------------------------------------------------------------------------

# 32. EXAM

Exam должен минимизировать distractions.

Не добавляй декоративные элементы, которые конкурируют с вопросом.

------------------------------------------------------------------------

# 33. MARATHON

Marathon должен быть рассчитан на throughput.

Минимум лишних действий.

Быстро:

`read → answer → continue`

------------------------------------------------------------------------

# 34. ANALYTICS

Полностью пересмотреть `/stats`.

Не пытаться спасти существующую структуру.

Сначала определить:

> Какие решения пользователь принимает на основе аналитики?

Например:

-   что повторить;
-   где слабость;
-   что улучшилось;
-   где деградация;
-   какой topic изучать дальше.

Если metric не помогает решению --- убрать или понизить визуальный
приоритет.

------------------------------------------------------------------------

# 35. DATA VISUALIZATION

Не использовать графики ради графиков.

Каждая visualization должна отвечать:

> **What decision does this enable?**

Если график не помогает --- удалить.

Не дублировать:

-   chart;
-   table;
-   KPI;
-   same metric card

для одной и той же информации без необходимости.

Используй progressive disclosure.

------------------------------------------------------------------------

# 36. TOPICS

Topics должен ощущаться как карта знаний.

Можно использовать:

-   hierarchy;
-   search;
-   filters;
-   mastery;
-   progression;
-   topic relationships;
-   visual map.

Но не создавай визуальный «map» только ради красоты.

Главное:

> найти нужную тему и понять состояние знаний.

------------------------------------------------------------------------

# 37. BOOKMARKS

Bookmarks должны быть:

-   fast;
-   searchable;
-   filterable;
-   scannable.

Не превращай их в коллекцию огромных cards.

------------------------------------------------------------------------

# 38. SETTINGS

Settings --- это инструмент настройки продукта, а не декоративная
галерея.

Сделай:

-   понятные группы;
-   progressive disclosure;
-   хорошие controls;
-   previews там, где они полезны;
-   keyboard support.

Не показывай пользователю 30 технических настроек одновременно.

------------------------------------------------------------------------

# 39. SESSION SUMMARY

Summary должен быть эмоционально сильным.

Пользователь закончил работу.

Он должен понять:

-   результат;
-   прогресс;
-   ошибки;
-   что изучить;
-   что делать дальше.

Summary --- не просто:

``` text
87%
12/20
Back.
```

Создай ощущение завершённого цикла.

Но **НЕ превращай это в RPG**.

------------------------------------------------------------------------

# 40. ERROR / EMPTY / LOADING

Каждое состояние должно быть частью design system.

Создать:

-   EMPTY;
-   LOADING;
-   ERROR;
-   SUCCESS;
-   PARTIAL;
-   DISABLED;
-   OFFLINE;
-   NO_RESULTS;
-   NO_SESSION;
-   COMPLETED.

Не использовать:

> `Something went wrong.`

без полезного действия.

------------------------------------------------------------------------

# 41. ICON SYSTEM

Полностью пересмотреть иконки.

Варианты:

-   Lucide;
-   custom SVG;
-   open-source icon pack;
-   hybrid.

Можно оставить Lucide, если он действительно лучший.

Не использовать pixel icon для каждой кнопки только ради theme.

Icon language должен быть консистентным.

------------------------------------------------------------------------

# 42. ART DIRECTION

Создай visual identity.

Если medieval остаётся --- используй его subtle.

Например:

-   restrained heraldic motif;
-   texture;
-   emblem;
-   illustration;
-   accent;
-   progression metaphor.

Не:

-   parchment everywhere;
-   swords everywhere;
-   shields everywhere;
-   fake wood everywhere;
-   gold border everywhere;
-   medieval font everywhere.

Если современная эстетика без medieval лучше --- **ОТКАЖИСЬ ОТ
MEDIEVAL**.

------------------------------------------------------------------------

# 43. EXTERNAL ASSETS

Если нужны изображения / illustrations / icons, предпочитать:

-   CC0;
-   Public Domain;
-   MIT;
-   BSD;
-   Apache;
-   permissive licenses.

Обязательно фиксировать:

-   source;
-   author;
-   license;
-   license URL;
-   modifications;
-   usage.

Не использовать:

-   ripped game assets;
-   Pinterest assets;
-   неизвестные packs;
-   copyrighted game characters;
-   assets без license.

------------------------------------------------------------------------

# 44. СОБСТВЕННЫЕ ASSETS

Если подходящий asset не найден, можно создать:

-   SVG;
-   CSS illustration;
-   generated illustration;
-   abstract graphic;
-   pixel art;
-   vector art;
-   icon.

Но asset должен быть частью системы.

Не создавай 40 разных illustrations без общего art direction.

------------------------------------------------------------------------

# 45. MOTION

Создай полноценную motion language.

Нужны:

-   hover;
-   focus;
-   press;
-   reveal;
-   transition;
-   page state;
-   loading;
-   success;
-   error.

Motion должен объяснять состояние.

Не делай:

-   постоянный parallax;
-   бесконечные particles;
-   blinking UI;
-   motion everywhere.

------------------------------------------------------------------------

# 46. REDUCED MOTION

При:

``` css
prefers-reduced-motion: reduce
```

все необязательные animations должны исчезать или становиться
instant/subtle.

Это должен быть реальный functional behavior.

------------------------------------------------------------------------

# 47. ACCESSIBILITY

Проверить:

-   keyboard;
-   focus;
-   focus-visible;
-   screen reader;
-   semantics;
-   labels;
-   contrast;
-   zoom;
-   200%;
-   text scaling;
-   touch targets;
-   error states.

Нельзя передавать смысл только через цвет.

------------------------------------------------------------------------

# 48. KEYBOARD-FIRST

Продукт для developers.

Keyboard interaction должна быть первоклассной.

Проверить:

-   Tab;
-   Shift+Tab;
-   Enter;
-   Space;
-   Escape;
-   arrows;
-   numeric shortcuts;
-   answer shortcuts;
-   next;
-   reveal;
-   command palette.

Но shortcuts не должны конфликтовать с browser/platform behavior.

------------------------------------------------------------------------

# 49. COMMAND SURFACE

Рассмотри полноценную command palette.

Например:

`Cmd/Ctrl + K`

Через неё можно:

-   начать тренировку;
-   открыть тему;
-   перейти в stats;
-   открыть bookmarks;
-   изменить режим;
-   перейти к настройкам;
-   продолжить session.

Если это действительно улучшает UX --- реализовать.

------------------------------------------------------------------------

# 50. SEARCH

Исследовать поиск:

-   topics;
-   questions;
-   bookmarks;
-   sessions.

Не делать отдельный search UI, если command/search surface может
объединить сценарии.

------------------------------------------------------------------------

# 51. RESPONSIVE AS COMPOSITION

Responsive не должен выглядеть как:

> desktop → `flex-direction: column`

Для каждой major surface определить:

### DESKTOP

### TABLET

### MOBILE

И явно определить:

-   что остаётся;
-   что исчезает;
-   что перемещается;
-   что превращается в drawer;
-   что становится inline;
-   что меняет priority.

------------------------------------------------------------------------

# 52. DENSITY

Найти идеальный баланс:

> слишком sparse = пустой SaaS

> слишком dense = IDE spreadsheet

Нужен **premium information density**.

Особенно:

-   analytics;
-   question workspace;
-   settings;
-   topics;
-   bookmarks.

------------------------------------------------------------------------

# 53. NO CARD SPAM

**КАТЕГОРИЧЕСКИ ЗАПРЕЩЕНО лечить UI:**

> «сделаем всё карточками».

Если на экране:

``` text
card
card
card
card
card
card
```

остановись.

Пересмотри composition.

------------------------------------------------------------------------

# 54. NO HERO WASTE

Запрещены огромные:

-   hero headings;
-   decorative banners;
-   empty top spacing;
-   giant rails.

Если heading занимает 40% viewport --- это должно быть сознательным
решением.

Для рабочего приложения почти никогда не должно быть так.

------------------------------------------------------------------------

# 55. NO DECORATIVE FILLER

Нельзя заполнять пустое место:

-   картинкой;
-   texture;
-   particles;
-   pixel art;
-   gradient;
-   giant icon

только потому, что оно пустое.

Пустота должна решаться layout'ом, а не декоративным мусором.

------------------------------------------------------------------------

# 56. DESIGN SYSTEM IMPLEMENTATION

После выбора visual direction создать:

-   tokens.css;
-   components;
-   layout primitives;
-   state primitives;
-   responsive rules;
-   motion tokens;
-   typography tokens;
-   surface tokens.

Уменьшать количество one-off CSS.

------------------------------------------------------------------------

# 57. CSS REFACTOR

Текущий большой CSS --- **НЕ причина ничего сохранять**.

Если существующая архитектура:

-   слишком сложна;
-   содержит overrides;
-   содержит dead code;
-   содержит исторические костыли;
-   дублирует layout;
-   требует `!important`

--- перепиши.

Можно полностью заменить CSS subsystem.

------------------------------------------------------------------------

# 58. НЕ СОЗДАВАЙ НОВЫЙ CSS ПОВЕРХ СТАРОГО

Это **КРИТИЧЕСКОЕ ПРАВИЛО**.

Запрещено:

> старый UI + новый UI + overrides + override overrides.

Если компонент заменён --- **УДАЛИ СТАРЫЙ**.

Если token заменён --- **УДАЛИ СТАРЫЙ**.

Если layout заменён --- **УДАЛИ СТАРЫЙ**.

------------------------------------------------------------------------

# 59. DEAD CODE

После redesign найти и удалить:

-   unused CSS;
-   unused JS;
-   obsolete fragments;
-   old classes;
-   old tokens;
-   obsolete assets;
-   obsolete design docs;
-   stale tests, если они проверяют намеренно удалённую архитектуру.

Но тесты не удалять только потому, что они неудобны.

Сначала определить:

> что тест защищает.

------------------------------------------------------------------------

# 60. TESTS --- НЕ ВРАГ

Существующие UI contract tests считать источником знаний.

Если test защищает хороший invariant --- сохранить.

Если test защищает старое дизайн-решение, которое больше не существует
--- переписать test.

Если архитектура изменилась --- обновить gate.

**НЕ ослаблять тест только ради зелёного build.**

------------------------------------------------------------------------

# 61. VISUAL REGRESSION

Создать новую visual baseline.

Не сравнивать новый дизайн только с текущим screenshot pixel-to-pixel.

Текущий screenshot НЕ является golden truth.

Он является:

**BEFORE**

Новые screenshots:

**AFTER**

Оценивать:

-   hierarchy;
-   density;
-   whitespace;
-   alignment;
-   typography;
-   interaction;
-   consistency.

------------------------------------------------------------------------

# 62. SCREENSHOT MATRIX

Для каждой major surface:

### Chromium

-   1280;
-   1440;
-   1512;
-   1728;
-   1920;
-   2560. 

### WebKit

Критические размеры.

### Mobile

-   360;
-   390;
-   430. 

Дополнительно:

-   90%;
-   110%;
-   125%;
-   200%.

Проверять:

-   dark;
-   light.

------------------------------------------------------------------------

# 63. FULL-PAGE VS FOLD

Обязательно делать:

1.  full-page screenshot;
2.  first viewport screenshot.

Full-page нужен для:

-   consistency;
-   long content;
-   overflow.

First viewport нужен для:

-   composition;
-   hierarchy;
-   visual impact;
-   wasted space.

------------------------------------------------------------------------

# 64. REAL DATA

Не использовать только красивый mock data.

Проверять:

-   short question;
-   very long question;
-   long answers;
-   code;
-   diagrams;
-   no code;
-   no answers;
-   long explanation;
-   empty topics;
-   many topics;
-   0 bookmarks;
-   500 bookmarks;
-   0 questions;
-   huge session;
-   high score;
-   low score;
-   long notes.

------------------------------------------------------------------------

# 65. BOUNDARY STATES

Проверить:

-   0;
-   1;
-   2;
-   4;
-   10;
-   100;
-   1000. 

И:

-   very long strings;
-   Cyrillic;
-   Latin;
-   mixed;
-   code;
-   URLs;
-   symbols;
-   emojis where applicable.

------------------------------------------------------------------------

# 66. BACKEND CHANGES

Разрешено менять backend.

Но только если это улучшает UX.

Можно:

-   добавить endpoint;
-   изменить DTO;
-   добавить state;
-   изменить filtering;
-   добавить persistence;
-   улучшить session model;
-   добавить summary data;
-   добавить search;
-   добавить analytics data.

Но:

**НЕ добавляй backend complexity только ради красивой карточки.**

------------------------------------------------------------------------

# 67. SERVER-SIDE RENDERING

Текущий SSR / Thymeleaf не является визуальным ограничением.

Но не ломай SSR без веской причины.

Если можно получить premium UX через:

-   HTML;
-   CSS;
-   progressive enhancement;
-   small JS

--- предпочитать это.

Не превращать приложение в SPA только ради модного frontend stack.

------------------------------------------------------------------------

# 68. NO-JS

Основные workflows должны продолжать работать без JavaScript там, где
это является текущим контрактом.

Проверить:

-   start;
-   answer;
-   reveal;
-   finish;
-   forms;
-   navigation.

------------------------------------------------------------------------

# 69. PROGRESSIVE ENHANCEMENT

JS должен улучшать UX.

Он **НЕ должен быть единственным способом выполнить критическую
операцию**.

------------------------------------------------------------------------

# 70. FORMS

Полностью переработать forms.

Проверить:

-   input;
-   select;
-   checkbox;
-   radio;
-   textarea;
-   segmented controls;
-   range;
-   buttons;
-   errors;
-   help text;
-   disabled;
-   loading.

Native controls можно заменить, если новый control:

-   accessible;
-   keyboard friendly;
-   progressive;
-   maintainable.

------------------------------------------------------------------------

# 71. BUTTON SYSTEM

Создать button hierarchy.

Например:

-   Primary;
-   Secondary;
-   Tertiary;
-   Ghost;
-   Danger;
-   Icon;
-   Icon + Text.

Не должно быть 12 разных типов кнопок.

Каждый rank должен иметь:

-   default;
-   hover;
-   active;
-   focus;
-   disabled;
-   loading.

------------------------------------------------------------------------

# 72. STATES

Каждый interactive component должен иметь полную state model.

Не только:

> normal / hover

Минимум:

-   default;
-   hover;
-   focus;
-   active;
-   selected;
-   disabled;
-   loading;
-   success;
-   error.

------------------------------------------------------------------------

# 73. TABLES

Tables должны быть:

-   readable;
-   dense;
-   responsive;
-   sortable;
-   accessible.

Не заставляй mobile пользователя горизонтально скроллить гигантскую
таблицу, если information architecture позволяет сделать лучше.

------------------------------------------------------------------------

# 74. CODE BLOCKS

Code должен выглядеть как часть developer product, а не как случайный
`<pre>`.

Проверить:

-   syntax highlighting;
-   contrast;
-   line numbers;
-   copy;
-   wrap;
-   horizontal scroll;
-   mobile;
-   light;
-   dark.

------------------------------------------------------------------------

# 75. DIAGRAMS

Mermaid/diagram surfaces должны быть:

-   readable;
-   responsive;
-   zoomable where needed;
-   theme-aware.

Не позволять диаграмме разрушать layout.

------------------------------------------------------------------------

# 76. MOTION QUALITY BAR

Каждая animation должна иметь ответ:

> «Что пользователь понимает благодаря этой animation?»

Если ответа нет --- **удалить**.

------------------------------------------------------------------------

# 77. PERFORMANCE

Не превращай UI в GPU benchmark.

Особенно осторожно:

-   `backdrop-filter`;
-   huge shadows;
-   blur;
-   filters;
-   canvas;
-   particles;
-   large SVG;
-   animated DOM;
-   huge sprite sheets.

Проверить:

-   LCP;
-   FCP;
-   CLS;
-   JS;
-   CSS;
-   image weight;
-   font loading.

------------------------------------------------------------------------

# 78. WEBKIT

WebKit --- **ОБЯЗАТЕЛЬНЫЙ gate**.

Проверить:

-   layout;
-   sticky;
-   overflow;
-   forms;
-   focus;
-   animations;
-   fonts;
-   viewport;
-   mobile Safari-like behavior.

Если Chromium красивый, а WebKit ломается:

> **ЗАДАЧА НЕ ЗАВЕРШЕНА.**

------------------------------------------------------------------------

# 79. CONSOLE

После каждого крупного этапа:

-   0 console errors;
-   0 page errors;
-   0 unexpected warnings;
-   0 unexpected 4xx/5xx.

------------------------------------------------------------------------

# 80. NO BROKEN STATES

Проверить:

-   loading;
-   partial;
-   empty;
-   error;
-   session interrupted;
-   session resumed;
-   answer submitted;
-   answer revealed;
-   session finished.

UI должен выглядеть намеренно в каждом состоянии.

------------------------------------------------------------------------

# 81. INFORMATION ARCHITECTURE

Не ограничивайся CSS.

Если navigation плохая --- меняй navigation.

Если page hierarchy плохая --- меняй template.

Если data model мешает --- меняй backend.

Если workflow плохой --- меняй flow.

Это **product redesign**, а не CSS redesign.

------------------------------------------------------------------------

# 82. ПОЛНАЯ СВОБОДА ИЗМЕНЕНИЙ

Разрешено менять:

-   HTML;
-   Thymeleaf;
-   CSS;
-   JS;
-   Java;
-   backend;
-   DTO;
-   controllers;
-   services;
-   endpoints;
-   fragments;
-   tests;
-   assets;
-   fonts;
-   icons;
-   animations;
-   navigation;
-   page structure.

Если изменение необходимо для качественного продукта --- **делай его**.

------------------------------------------------------------------------

# 83. НО НЕ ЛОМАЙ ФУНКЦИОНАЛЬНОСТЬ

Визуальный redesign не должен уничтожать:

-   session flow;
-   progress;
-   scoring;
-   answers;
-   persistence;
-   filtering;
-   bookmarks;
-   analytics;
-   settings;
-   keyboard behavior;
-   accessibility;
-   server fallback.

------------------------------------------------------------------------

# 84. DESIGN CONSISTENCY AUDIT

После реализации пройти все страницы и искать:

-   разные paddings;
-   разные radius;
-   разные button heights;
-   разные icon sizes;
-   разные heading styles;
-   разные border styles;
-   разные states;
-   разные focus styles;
-   разные empty states.

Если одинаковые сущности выглядят по-разному --- **унифицировать**.

------------------------------------------------------------------------

# 85. CROSS-PAGE AUDIT

Проверить:

`HOME → TRAINING → RESULT → SUMMARY → STATS → TOPICS → BOOKMARKS → SETTINGS`

Переход между страницами должен ощущаться как один продукт.

------------------------------------------------------------------------

# 86. НЕ ДЕЛАЙ ВСЁ ОДИНАКОВЫМ

**Consistency != uniformity.**

Компоненты должны быть едиными.

Но composition может отличаться.

Не нужно:

> 8 страниц + одинаковый hero + одинаковые cards + одинаковая grid.

------------------------------------------------------------------------

# 87. VISUAL HIERARCHY TEST

Открой screenshot на 2 секунды.

После этого должно быть понятно:

1.  что главное;
2.  что делать;
3.  где находишься;
4.  какой статус;
5.  куда смотреть дальше.

Если нет --- **перепроектировать**.

------------------------------------------------------------------------

# 88. SQUINT TEST

Уменьшить screenshot.

Если всё становится:

> одинаково серым / коричневым / золотым

значит hierarchy плохая.

Используй:

-   contrast;
-   scale;
-   whitespace;
-   typography;
-   surface;
-   alignment.

------------------------------------------------------------------------

# 89. GRAYSCALE TEST

Проверить интерфейс без цвета.

Основная hierarchy должна сохраняться.

Color должен усиливать hierarchy, а не создавать её единолично.

------------------------------------------------------------------------

# 90. CHEAPNESS TEST

Задай вопрос:

> «Выглядит ли это как template?»

Если да --- перепроектировать.

Особенно подозрительны:

-   generic cards;
-   generic dashboard;
-   default badges;
-   excessive borders;
-   random gradients;
-   giant hero;
-   stock illustrations;
-   decorative clutter.

------------------------------------------------------------------------

# 91. AI-GENERATED LOOK TEST

Отдельно искать:

-   чрезмерно одинаковые карточки;
-   чрезмерно симметричные grids;
-   meaningless gradients;
-   random decorative elements;
-   одинаковые rounded rectangles;
-   слишком много icons;
-   excessive labels;
-   fake depth.

Устранить.

------------------------------------------------------------------------

# 92. PREMIUM TEST

Спросить:

> «Можно ли представить, что этим продуктом пользуется Senior/Staff
> developer каждый день?»

Если нет --- исправить.

------------------------------------------------------------------------

# 93. MACOS QUALITY TEST

Проверить глазами пользователя macOS:

-   typography;
-   spacing;
-   scroll;
-   controls;
-   keyboard;
-   window resize;
-   fullscreen;
-   narrow window;
-   Retina;
-   Safari.

Интерфейс должен ощущаться нативно комфортным, даже если он web
application.

------------------------------------------------------------------------

# 94. FINAL DESIGN SYSTEM

В конце должна существовать понятная система:

`tokens + components + layouts + patterns + states + motion + responsive rules`

Нельзя закончить проектом, который выглядит хорошо только потому, что
5000 строк CSS случайно совпали.

------------------------------------------------------------------------

# 95. DOCUMENTATION

Обновить:

-   design system documentation;
-   architecture;
-   visual decisions;
-   asset registry;
-   responsive decisions;
-   component rules.

Документация должна описывать **ПОЧЕМУ** решение существует.

Не только:

> `background = #123456`

------------------------------------------------------------------------

# 96. ПЕРЕПРОВЕРЬ СТАРЫЕ DOCUMENTED DECISIONS

После создания нового design system:

просмотреть старые FRD.

Для каждого значимого решения определить:

-   KEEP;
-   REPLACE;
-   MERGE;
-   DELETE.

Не оставлять противоречивые документы.

------------------------------------------------------------------------

# 97. НЕ ОСТАВЛЯЙ ДВЕ DESIGN SYSTEM

После migration:

**НЕ должно существовать:**

`old design + new design`

Должна существовать:

**ONE DESIGN SYSTEM**

------------------------------------------------------------------------

# 98. VISUAL ASSET CLEANUP

Удалить:

-   obsolete sprites;
-   unused icons;
-   unused backgrounds;
-   obsolete pixel assets;
-   duplicate assets.

Не хранить старое «на всякий случай».

------------------------------------------------------------------------

# 99. FONT CLEANUP

Проверить:

-   используемые fonts;
-   preload;
-   font weights;
-   subsets;
-   Cyrillic;
-   Latin;
-   fallback.

Не грузить 12 font weights, если используются 4.

------------------------------------------------------------------------

# 100. FINAL E2E

После redesign выполнить полный E2E.

Не только unit tests.

Обязательно:

-   start session;
-   answer;
-   reveal;
-   next;
-   finish;
-   summary;
-   bookmarks;
-   topics;
-   stats;
-   settings;
-   typed;
-   interview;
-   code;
-   study;
-   flashcard;
-   exam;
-   marathon.

------------------------------------------------------------------------

# 101. FINAL VISUAL MATRIX

Проверить:

## THEME

-   dark;
-   light.

## ENGINE

-   Chromium;
-   WebKit.

## VIEWPORT

-   360;
-   390;
-   430;
-   768;
-   834;
-   1024;
-   1100;
-   1180;
-   1279;
-   1280;
-   1440;
-   1512;
-   1728;
-   1920;
-   2560. 

## ZOOM

-   90;
-   100;
-   110;
-   125;
-   200. 

Не обязательно делать полный cross-product для абсолютно каждой
страницы, но каждая critical surface должна быть покрыта.

------------------------------------------------------------------------

# 102. FINAL ADVERSARIAL REVIEW

После того как тебе кажется, что всё готово:

## ПОПРОБУЙ УНИЧТОЖИТЬ СОБСТВЕННЫЙ РЕЗУЛЬТАТ.

Выступи одновременно как:

-   PRODUCT DESIGNER;
-   UX DESIGNER;
-   ART DIRECTOR;
-   FRONTEND ENGINEER;
-   ACCESSIBILITY SPECIALIST;
-   MACOS USER;
-   SAFARI USER;
-   MOBILE USER;
-   DEVELOPER;
-   FIRST-TIME USER.

Ищи:

-   wasted space;
-   visual noise;
-   weak hierarchy;
-   inconsistent spacing;
-   wrong typography;
-   generic UI;
-   cheap visuals;
-   unnecessary decoration;
-   bad mobile;
-   broken WebKit;
-   awkward keyboard;
-   inaccessible controls;
-   giant cards;
-   useless rails;
-   unnecessary scroll;
-   duplicate information;
-   too much chrome;
-   too little content.

Каждую найденную проблему:

> **НЕ ПРОСТО ЗАПИСАТЬ.**

> **ИСПРАВИТЬ.**

------------------------------------------------------------------------

# 103. SELF-CRITIQUE

Перед завершением ответь самому себе:

1.  Что в новом дизайне мне нравится меньше всего?
2.  Где я просто сохранил старое решение?
3.  Где я сделал cosmetic change вместо redesign?
4.  Где есть пустое место?
5.  Где слишком много информации?
6.  Где пользователь делает лишний клик?
7.  Где mobile выглядит как desktop shrink?
8.  Где dark лучше light?
9.  Где light лучше dark?
10. Где WebKit хуже Chromium?
11. Где accessibility ухудшилась?
12. Где дизайн выглядит AI-generated?
13. Где дизайн выглядит generic SaaS?
14. Где medieval/pixel-art элементы мешают?
15. Где недостаточно visual identity?
16. Какие 3 экрана всё ещё выглядят плохо?

Затем **исправь их**.

------------------------------------------------------------------------

# 104. SECOND REVIEW

После self-critique:

**НЕ считать задачу законченной.**

Провести второй независимый review.

Считать предыдущий redesign потенциально ошибочным.

Ищи проблемы, которые не заметил в первом review.

------------------------------------------------------------------------

# 105. DECISION REVERSAL

Если после реализации обнаружено, что новое решение хуже старого:

**НЕ защищай новое решение.**

Можно:

`NEW → OLD`

или:

`NEW → COMPLETELY DIFFERENT`

Sunk cost **НЕ является аргументом**.

------------------------------------------------------------------------

# 106. NO PREMATURE COMPLETION

НЕ завершай задачу словами:

> «дизайн значительно улучшен».

НЕ завершай задачу:

> «основные страницы готовы».

НЕ завершай задачу:

> «осталось немного polish».

Если есть значимые визуальные проблемы --- **работа продолжается**.

------------------------------------------------------------------------

# 107. NO PLAN-ONLY OUTPUT

Запрещено закончить:

> «Предлагаю сделать...»

> «Можно улучшить...»

> «Следующим этапом...»

> «Рекомендую...»

Если проблему обнаружил:

**ИСПРАВЬ ЕЁ.**

------------------------------------------------------------------------

# 108. NO ASKING FOR PERMISSION

Не спрашивай:

> «Можно ли изменить sidebar?»

> «Можно ли убрать pixel art?»

> «Можно ли поменять typography?»

> «Можно ли переписать CSS?»

Разрешение уже дано этим заданием.

Если изменение разумно:

**ДЕЛАЙ.**

------------------------------------------------------------------------

# 109. НЕ СТЕСНЯЙСЯ БОЛЬШИХ CHANGES

Если лучший результат требует:

-   удалить 20 CSS файлов;
-   создать 10 новых;
-   переписать shell;
-   изменить templates;
-   заменить navigation;
-   изменить JS;
-   изменить backend

--- это допустимо.

Но каждое изменение должно быть проверено.

------------------------------------------------------------------------

# 110. НЕ МЕНЯЙ РАДИ DIFF

Количество изменений не является целью.

Цель:

**лучший продукт.**

------------------------------------------------------------------------

# 111. QUALITY \> COMPATIBILITY WITH BAD DECISIONS

Если старое решение:

-   работает технически;
-   но выглядит плохо

это недостаточная причина его сохранять.

Если новый дизайн требует переписать implementation:

**перепиши.**

------------------------------------------------------------------------

# 112. QUALITY \> SPEED

Не торопись.

Это не задача:

> «сделай за 15 минут».

Это задача:

> **«сделай настолько хорошо, насколько позволяют доступное время и
> инструменты».**

------------------------------------------------------------------------

# 113. QUALITY \> MINIMAL DIFF

Минимальный git diff **НЕ является целью**.

Можно иметь огромный diff, если это оправдано redesign.

------------------------------------------------------------------------

# 114. NO FAKE IMPLEMENTATION

Запрещены:

-   placeholder buttons;
-   fake charts;
-   fake interactions;
-   static controls;
-   dead links;
-   decorative controls without behavior;
-   TODO вместо реализации.

Если UI показывает действие:

**оно должно работать.**

------------------------------------------------------------------------

# 115. NO PLACEHOLDER ART

Не оставлять:

-   TODO illustration;
-   placeholder icon;
-   temporary background.

Если asset необходим:

**создай или найди настоящий.**

------------------------------------------------------------------------

# 116. PRODUCT COHERENCE

После всех изменений продукт должен иметь:

-   ONE BRAND;
-   ONE VISUAL LANGUAGE;
-   ONE INTERACTION LANGUAGE;
-   ONE MOTION LANGUAGE;
-   ONE COMPONENT LANGUAGE.

Но разные screens могут иметь разные compositions.

------------------------------------------------------------------------

# 117. FINAL CLEANUP

Перед завершением:

-   remove dead CSS;
-   remove dead JS;
-   remove old assets;
-   remove obsolete tokens;
-   remove obsolete classes;
-   remove redundant wrappers;
-   remove unnecessary JS;
-   remove unnecessary animations;
-   remove duplicate components;
-   remove obsolete documentation.

------------------------------------------------------------------------

# 118. FINAL VALIDATION

Обязательно:

-   BUILD PASS;
-   UNIT TESTS PASS;
-   INTEGRATION TESTS PASS;
-   E2E PASS;
-   CHROMIUM PASS;
-   WEBKIT PASS;
-   NO-JS PASS;
-   ACCESSIBILITY PASS;
-   REDUCED MOTION PASS;
-   CONSOLE CLEAN;
-   NETWORK CLEAN;
-   VISUAL REVIEW PASS.

------------------------------------------------------------------------

# 119. DEFINITION OF DONE

Задача **НЕ считается завершённой**, пока одновременно не выполнено:

-   [ ] Новый visual direction выбран осознанно.
-   [ ] Старый visual direction больше не диктует UI.
-   [ ] Pixel art сохранён только там, где он действительно улучшает
    продукт, либо полностью удалён.
-   [ ] Medieval theme сохранён только если он улучшает identity.
-   [ ] Global shell переработан.
-   [ ] Navigation переработана.
-   [ ] Home переработан.
-   [ ] Training переработан.
-   [ ] MCQ переработан.
-   [ ] Typed Recall переработан.
-   [ ] Interview переработан.
-   [ ] Code Challenge переработан.
-   [ ] Study переработан.
-   [ ] Flashcard переработан.
-   [ ] Exam переработан.
-   [ ] Marathon переработан.
-   [ ] Topics переработан.
-   [ ] Stats переработан.
-   [ ] Bookmarks переработан.
-   [ ] Settings переработан.
-   [ ] Summary переработан.
-   [ ] Error переработан.
-   [ ] Empty states переработаны.
-   [ ] Loading states переработаны.
-   [ ] Forms переработаны.
-   [ ] Buttons переработаны.
-   [ ] Tables переработаны.
-   [ ] Code surfaces переработаны.
-   [ ] Diagrams переработаны.
-   [ ] Typography переработана.
-   [ ] Color system переработана.
-   [ ] Spacing system переработана.
-   [ ] Radius system переработана.
-   [ ] Surface system переработана.
-   [ ] Icon system переработана.
-   [ ] Motion system переработана.
-   [ ] Dark theme готова.
-   [ ] Light theme готова.
-   [ ] Mobile готов.
-   [ ] Tablet готов.
-   [ ] Narrow desktop готов.
-   [ ] MacBook проверен.
-   [ ] Ultrawide проверен.
-   [ ] WebKit проверен.
-   [ ] Chromium проверен.
-   [ ] Keyboard проверена.
-   [ ] Accessibility проверена.
-   [ ] Reduced motion проверен.
-   [ ] No-JS проверен.
-   [ ] Real data проверены.
-   [ ] Long content проверен.
-   [ ] Empty states проверены.
-   [ ] Error states проверены.
-   [ ] CSS cleanup выполнен.
-   [ ] JS cleanup выполнен.
-   [ ] Old assets удалены.
-   [ ] Old design remnants удалены.
-   [ ] Tests обновлены.
-   [ ] Visual baselines обновлены.
-   [ ] Documentation обновлена.
-   [ ] Final adversarial review выполнен.
-   [ ] Second review выполнен.
-   [ ] Все найденные проблемы исправлены.

------------------------------------------------------------------------

# 120. ФИНАЛЬНЫЙ TEST

Открой приложение.

Посмотри на:

1.  Home.
2.  Training.
3.  Реальный вопрос.
4.  Answer.
5.  Result.
6.  Summary.
7.  Stats.
8.  Topics.
9.  Settings.
10. Mobile.
11. Safari/WebKit.

И задай один вопрос:

> **«Похоже ли это на продукт, которым я сам хотел бы пользоваться
> каждый день для подготовки к Senior/Staff техническим интервью?»**

Если ответ:

> «ну, в целом нормально»

**ЗАДАЧА НЕ ЗАКОНЧЕНА.**

Если ответ:

> «да, это реально выглядит как законченный современный продукт»

тогда переходи к финальному audit.

------------------------------------------------------------------------

# 121. ФИНАЛЬНЫЙ АУДИТ

После прохождения Definition of Done:

**не изменяй код сразу.**

Сначала проведи final audit.

Составь внутренний список:

-   CRITICAL;
-   HIGH;
-   MEDIUM;
-   LOW.

Исправь:

-   ALL CRITICAL;
-   ALL HIGH;
-   ALL MEDIUM.

LOW исправляй, если исправление действительно улучшает продукт.

------------------------------------------------------------------------

# 122. FINAL REPORT

В конце предоставить подробный отчёт:

1.  OLD DESIGN
2.  NEW DESIGN
3.  WHY OLD DESIGN WAS REJECTED
4.  NEW ART DIRECTION
5.  NEW DESIGN SYSTEM
6.  NEW NAVIGATION
7.  NEW SHELL
8.  NEW QUESTION WORKSPACE
9.  MODE-SPECIFIC COMPOSITIONS
10. ANALYTICS
11. TOPICS
12. SETTINGS
13. SUMMARY
14. MOBILE
15. MACOS
16. ULTRAWIDE
17. DARK
18. LIGHT
19. ACCESSIBILITY
20. MOTION
21. PERFORMANCE
22. CSS ARCHITECTURE
23. JS ARCHITECTURE
24. BACKEND CHANGES
25. REMOVED LEGACY
26. ASSETS
27. LICENSES
28. TESTS
29. VISUAL REGRESSION
30. ADVERSARIAL REVIEW
31. REMAINING KNOWN ISSUES

Для каждой крупной области:

`BEFORE → CHANGE → AFTER → VALIDATION`

Не выдавай предположение за выполненную работу.

------------------------------------------------------------------------

# 123. ABSOLUTE PRINCIPLES

``` text
PRODUCT QUALITY
>
OLD DESIGN DECISIONS

UX
>
DECORATION

INFORMATION HIERARCHY
>
VISUAL NOVELTY

READABILITY
>
THEME

FUNCTION
>
ART

REAL SPACE UTILIZATION
>
EMPTY DECORATION

CONSISTENCY
>
RANDOM CREATIVITY

SIMPLICITY
>
UNNECESSARY COMPLEXITY

REAL INTERACTION
>
STATIC MOCKUP

ACCESSIBILITY
>
VISUAL TRICKS

MACOS QUALITY
>
CHROME-ONLY OPTIMIZATION

WEBKIT
>
"WORKS ON MY CHROME"

FINAL PRODUCT
>
GIT DIFF
```

------------------------------------------------------------------------

# 124. THE FINAL ORDER

**НЕ ПЫТАЙСЯ СОХРАНИТЬ СУЩЕСТВУЮЩИЙ UI.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ PIXEL ART.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ MEDIEVAL THEME.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ SIDEBAR.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ GRID.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ CARDS.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ TOKENS.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ CSS.**

**НЕ ПЫТАЙСЯ СОХРАНИТЬ FRD.**

Сохраняй только:

-   работающую бизнес-логику;
-   полезные UX-инварианты;
-   реальные accessibility requirements;
-   реальные технические ограничения;
-   данные;
-   функциональность.

Всё остальное может быть пересмотрено.

------------------------------------------------------------------------

# 125. ФИНАЛЬНАЯ КОМАНДА

## СДЕЛАЙ НЕ REDESIGN ТЕКУЩЕГО UI.

## СДЕЛАЙ НОВЫЙ ПРОДУКТ, ИСПОЛЬЗУЯ СУЩЕСТВУЮЩИЙ КОД И ДАННЫЕ КАК ИСХОДНЫЙ МАТЕРИАЛ.

Если старый UI плохой --- **УДАЛИ ЕГО**.

Если старый UX плохой --- **ПЕРЕПРОЕКТИРУЙ ЕГО**.

Если старая архитектура мешает --- **ПЕРЕПИШИ ЕЁ**.

Если pixel art мешает --- **УБЕРИ ЕГО**.

Если medieval мешает --- **УБЕРИ ЕГО**.

Если нужно полностью сменить visual identity --- **СМЕНИ**.

Если нужно изменить backend --- **ИЗМЕНИ**.

Если нужно изменить frontend architecture --- **ИЗМЕНИ**.

Если нужно изменить navigation --- **ИЗМЕНИ**.

Если нужно изменить workflow --- **ИЗМЕНИ**.

Твоя цель не доказать, что предыдущие итерации были правильными.

Твоя цель ---

# СОЗДАТЬ МАКСИМАЛЬНО КАЧЕСТВЕННЫЙ ПРОДУКТ.

Не останавливайся на:

> «стало лучше».

Дойди до:

> **«ЭТО УЖЕ ВЫГЛЯДИТ КАК ГОТОВЫЙ ПРОДУКТ.»**

------------------------------------------------------------------------

## END OF MASTER MISSION
