# LOOP: полная переработка Cheat Sheet — UI / UX / Frontend / Medieval Pixel Art

Используй все доступные и релевантные skills, subagents, MCP-серверы, браузерные инструменты, web-search, CLI-инструменты и встроенные возможности Claude Code.

Не ограничивайся `superpowers`. Он может использоваться для оркестрации, планирования, исследования и самопроверки, но конкретные задачи выполняй наиболее подходящими доступными средствами:

* UI/UX design;
* product design;
* frontend engineering;
* design systems;
* Figma;
* browser automation;
* Playwright;
* WebKit;
* accessibility;
* performance;
* visual regression;
* поиск визуальных референсов;
* поиск open-source / CC0 assets;
* pixel art;
* SVG;
* sprite animation;
* motion design;
* Spring/Thymeleaf;
* backend;
* тестирование;
* Git.

Это промпт для **многократного запуска через loop**.

Каждый запуск — одна полноценная автономная итерация.

Итерация должна выбрать **одну наиболее важную системную проблему**, полностью решить её в production-коде, проверить реальным браузером, удалить заменённую реализацию и оставить проект в рабочем состоянии.

Нельзя заканчивать итерацию:

* аудитом;
* планом;
* рекомендациями;
* Figma-макетом;
* созданием концепции;
* частичной реализацией;
* поверхностным изменением множества экранов.

---

# 1. Контекст продукта

Проект — локальный тренажёр подготовки к техническим собеседованиям.

Банк содержит примерно **9800 вопросов** по:

* Java;
* Kotlin;
* Go;
* Spring;
* базам данных;
* архитектуре;
* system design;
* смежным backend-темам.

Основной пользовательский цикл:

`настройка сессии → вопрос → ответ → вердикт/разбор → следующий вопрос → итог`

Поддерживаются восемь режимов:

* `TRAINING`;
* `STUDY`;
* `FLASHCARD`;
* `TYPED_RECALL`;
* `INTERVIEW`;
* `CODE_CHALLENGE`;
* `EXAM`;
* `MARATHON`.

Приложение построено на:

* Spring Boot 3;
* Thymeleaf;
* server-side rendering;
* обычных HTML forms;
* progressive enhancement через ES modules;
* без React/Vue;
* без клиентского SPA-router.

Это **не нужно сохранять как визуальное ограничение**, но нужно учитывать как технический контракт.

---

# 2. Неприкосновенные технические инварианты

Можно менять почти всё, но нельзя ломать следующие свойства.

## 2.1. Работа без JavaScript

Основной цикл должен оставаться работоспособным без JS.

Как минимум:

* ответ на вопрос;
* старт сессии;
* завершение;
* flashcard flow;
* важные form actions;

должны иметь server-side fallback.

JavaScript используется как progressive enhancement.

Не превращай приложение в SPA только ради редизайна.

Если новая UX-механика требует JS, обеспечь:

* HTML fallback;
* server-side fallback;
* либо докажи, что механика не является критичной для основного сценария.

## 2.2. Accessibility

Состояние нельзя передавать только цветом.

Обязательно сохраняются:

* semantic HTML;
* keyboard navigation;
* visible focus;
* contrast;
* screen-reader labels;
* reduced motion;
* корректная работа форм;
* понятные состояния ошибок.

## 2.3. Две темы

Обязательны:

* dark;
* light.

Dark может оставаться основной художественной темой.

Light не должна быть второсортной или простой инверсией.

Обе темы должны проектироваться одновременно.

## 2.4. Reduced motion

`prefers-reduced-motion` и пользовательское отключение движения должны реально выключать декоративное движение и необязательные анимации.

---

# 3. Главная цель всех итераций

Полностью заменить текущий дизайн Cheat Sheet.

Не улучшать его постепенно до бесконечности.

Не сохранять текущую teal/cyan SaaS-эстетику только потому, что вокруг неё уже написано много CSS.

Целевой продукт должен получить новую визуальную идентичность:

## **Modern Medieval Knowledge Trainer**

Современное профессиональное приложение для обучения, визуально построенное вокруг:

* средневековой библиотеки;
* гильдии;
* карты знаний;
* хроник;
* манускриптов;
* академии;
* кузницы навыков;
* гербов;
* знамен;
* крепостей;
* башен;
* карт;
* пергамента;
* дерева;
* камня;
* металла;
* свечей;
* факелов;
* алхимии;
* ремесла;
* путешествия и progression.

При этом интерфейс должен оставаться:

* взрослым;
* профессиональным;
* читаемым;
* быстрым;
* функциональным;
* подходящим для многочасовой подготовки к собеседованиям.

Не превращай продукт в дешёвую RPG.

---

# 4. Полная свобода пересмотра предыдущих решений

**Ни одно существующее или ранее принятое дизайн-решение не является обязательным.**

Разрешается отменять даже решения, которые:

* уже реализованы;
* документированы;
* прошли browser review;
* ранее были признаны `AUDITED`;
* лежат в design tokens;
* используются на многих страницах.

Можно полностью отказаться от:

* нынешней teal-палитры;
* текущего application shell;
* sidebar;
* структуры навигации;
* текущей типографики;
* Inter;
* текущего размера заголовков;
* текущих карточек;
* outline-системы;
* Lucide;
* текущей iconography;
* текущих surfaces;
* текущих размеров контейнеров;
* текущей системы rail;
* текущего `flow/split/grid`;
* current stats layout;
* нынешних pixel sprites;
* нынешнего маскота;
* текущей animation system;
* существующей персонализации;
* текущих design tokens;
* ранее принятых responsive decisions.

Единственное требование:

отмена решения должна приводить к **целостной замене**, а не к появлению второй параллельной системы.

---

# 5. Право переоткрывать `AUDITED`

Статус `AUDITED` означает только:

> решение прошло проверки в момент предыдущей итерации.

Он **не означает**, что решение навсегда заморожено.

Если позднее обнаружено, что оно:

* плохо работает с другими страницами;
* создаёт пустые зоны;
* мешает общей композиции;
* плохо выглядит на MacBook;
* плохо работает в WebKit;
* визуально устарело;
* не соответствует новой art direction;
* создаёт слишком много специальных случаев;
* требует огромного page-specific CSS;
* плохо масштабируется;
* проигрывает найденному новому решению;

переведи область:

`AUDITED → REOPENED`

и полностью переработай её.

Sunk cost не является аргументом.

---

# 6. Текущее состояние не является benchmark

Текущий UI использует преимущественно:

* очень тёмные синевато-зелёные поверхности;
* яркий cyan/teal accent;
* Inter;
* JetBrains Mono;
* Lucide;
* техническую SaaS-подачу;
* тонкие outline borders;
* лёгкие pixel-art элементы;
* dotted/grid background;
* отдельные декоративные pixel effects.

Это исходный материал, а не направление, которое обязательно сохранять.

Новая концепция может полностью заменить его.

---

# 7. Художественная концепция

Ищи баланс между:

### Средневековьем

* рукописи;
* хроники;
* библиотеки;
* монастырские архивы;
* карты;
* кузницы;
* замки;
* гильдии;
* гербы;
* печати;
* камень;
* металл;
* дерево;
* бронза;
* золото;
* пергамент;
* факелы;
* свечи.

### И современной software-product эстетикой

* ясная hierarchy;
* понятные forms;
* быстрые controls;
* хорошие таблицы;
* readable code;
* сильный information design;
* keyboard-first interaction;
* adaptive layouts;
* понятные статусы.

Результат должен выглядеть как:

> профессиональная цифровая академия / гильдия инженеров,

а не как:

> fantasy skin поверх стандартного dashboard.

---

# 8. Pixel art — важная часть новой идентичности

Pixel art должен перестать быть случайным декоративным дополнением и стать частью художественной системы.

Но **не превращай весь UI в пиксельный**.

Хорошая модель:

### Современный UI

для:

* текста;
* таблиц;
* forms;
* code;
* controls;
* navigation;
* сложной аналитики.

### Pixel art

для:

* персонажей;
* environment;
* progress;
* achievements;
* icons предметов;
* декоративных сцен;
* пустых состояний;
* ошибок;
* onboarding;
* progression;
* sessions;
* categories;
* visual storytelling.

---

# 9. Pixel-art assets предпочтительно брать из открытого доступа

Активно ищи подходящие открытые наборы.

Приоритет лицензий:

1. CC0;
2. Public Domain;
3. MIT;
4. BSD;
5. Apache-2.0;
6. permissive open-source assets;
7. CC-BY, если атрибуцию можно корректно выполнить.

Особенно ищи:

* medieval pixel art;
* RPG UI pixel art;
* medieval buildings;
* castles;
* libraries;
* books;
* scrolls;
* banners;
* knights;
* scholars;
* alchemists;
* blacksmiths;
* maps;
* treasure chests;
* potions;
* medieval inventory icons;
* torches;
* candles;
* runes;
* forest;
* mountains;
* dungeon/environment tiles;
* animated fire;
* flags;
* coins;
* badges;
* shields;
* heraldry.

---

# 10. Запрещённые источники

Не используй:

* ripped game assets;
* ассеты из коммерческих игр;
* Pinterest как источник;
* Google Images без определения оригинала;
* ассеты без LICENSE;
* неизвестные Telegram/форумные архивы;
* изображения с watermark;
* proprietary icon packs без лицензии;
* чужих известных игровых персонажей;
* copyrighted fantasy franchises.

---

# 11. Реестр лицензий

Поддерживай:

`docs/visual-assets.md`

Для каждого внешнего asset pack:

* название;
* автор;
* source URL;
* license URL;
* лицензия;
* commercial use;
* modification allowed;
* attribution required;
* используемые файлы;
* модификации;
* location в проекте;
* экраны использования.

Не добавляй внешний asset в production до проверки лицензии.

---

# 12. Адаптируй найденные assets

Не вставляй найденные sprites напрямую.

Приводи их к единой системе:

* palette;
* pixel density;
* outline;
* lighting;
* contrast;
* transparency;
* size;
* scale;
* animation timing.

Если один asset pack слишком сильно отличается:

* адаптируй;
* перерисуй;
* либо не используй.

Лучше один сильный набор + собственные дополнения, чем десять несовместимых packs.

---

# 13. Текущие известные системные проблемы продукта

Не трать итерации только на повторное подтверждение этих проблем.

Используй их как starting backlog.

## 13.1. Training на ultrawide

На `20-fold-training--dark--ultrawide` основной интерфейс использует только часть экрана.

Проблемы:

* большая пустая нижняя зона;
* огромный неиспользованный canvas;
* рабочая область визуально слишком маленькая;
* right rail «Сессия» почти пуст;
* ширина экрана не превращается в дополнительную полезность;
* sidebar + центральный узкий блок создают ощущение маленького приложения внутри большого монитора.

Эта композиция должна быть **переосмыслена с нуля**.

Не пытайся только увеличить `max-width`.

---

# 14. MCQ не должен определять layout всех режимов

Сейчас композиция главного экрана фактически оптимизирована под четыре MCQ-варианта.

Из-за этого режимы:

* `TYPED_RECALL`;
* `INTERVIEW`;
* `CODE_CHALLENGE`;

выглядят как MCQ-экран, из которого удалили варианты.

Это архитектурный дефект UI.

Не исправляй его увеличением textarea.

Создай **mode-aware composition system**.

Например, разные режимы могут иметь разные рабочие пространства:

### MCQ

* question;
* context/code;
* answer options;
* session/support panel.

### Typed Recall

* question;
* structured recall workspace;
* key concepts area;
* history/context;
* large response editor.

### Code Challenge

* task;
* code-oriented workspace;
* reference/context panel;
* editor-like surface;
* hints/status.

### Interview

* question;
* speaking preparation area;
* checklist;
* timer;
* self-evaluation;
* talking points.

### Study

* explanation-first reading experience.

### Flashcard

* intentionally focused card composition.

Не заставляй все восемь режимов жить внутри одного одинакового grid.

---

# 15. Экран вопроса — сердце продукта

Главная страница `/` должна получить наибольший приоритет.

Она должна ощущаться как рабочее пространство, а не как одна карточка на фоне.

Исследуй варианты:

* knowledge desk;
* medieval study chamber;
* guild training hall;
* tactical split workspace;
* scroll/manuscript workspace;
* map/progression companion panel.

Но не жертвуй читаемостью.

Главный вопрос должен оставаться первым смысловым объектом.

---

# 16. Пересмотри огромный размер вопроса

Текущий question title может доходить примерно до `4rem`.

Проверь его на:

* длинных вопросах;
* 1280×720;
* 1440×900;
* 1512×982;
* mobile;
* Safari zoom.

Не считай большой шрифт автоматически хорошей hierarchy.

Используй fluid typography с ограничениями.

Приоритет:

* читаемость;
* количество текста above the fold;
* доступность вариантов;
* отсутствие гигантского пустого hero-space.

---

# 17. Right rail «Сессия» нужно заново обосновать

Текущий right rail занимает полноценную колонку даже тогда, когда содержит мало информации.

Не сохраняй его автоматически.

Рассмотри:

* command/status bar;
* compact floating session control;
* expandable inspector;
* contextual panel;
* top HUD;
* bottom action dock;
* adaptive rail только при наличии достаточного контента.

Rail должен существовать только если приносит пользу.

---

# 18. После ответа нельзя просто удлинять страницу в несколько раз

Сейчас `03` и `04` показывают, что разбор резко увеличивает документ.

Нужно заново спроектировать transition:

`question → verdict → learning`

Рассмотри:

* progressive reveal;
* anchored explanation;
* split view;
* tabs;
* accordion с правильной hierarchy;
* answer inspector;
* dedicated learning panel;
* collapsible distractor analysis;
* sticky question summary;
* scroll restoration.

Пользователь не должен терять вопрос и контекст после отправки.

---

# 19. Аналитика должна быть полностью перепроектирована

Текущий `/stats` одновременно показывает:

* «Что делать дальше»;
* 4 metrics;
* tabs;
* filtering;
* section cards;
* charts;
* duplicate tables;
* detailed table.

В результате слишком много конкурирующих уровней важности.

Перепроектируй информационную архитектуру.

Главный вопрос analytics:

> Что пользователь должен сделать после просмотра этой страницы?

Вероятно, analytics должна сначала отвечать:

1. Где я сейчас?
2. Где мои слабые места?
3. Что тренировать следующим?
4. Есть ли прогресс?
5. Где посмотреть детали?

Не обязательно показывать всё одновременно.

---

# 20. Убери дублирование графиков и таблиц

Если chart и таблица отображают одинаковые данные:

* не показывай их одновременно по умолчанию;
* дай переключение visualization/table;
* либо используй таблицу только как accessible alternative;
* либо объединяй данные в один richer component.

Ниже уже существует общая детализация.

Три визуализации одних чисел — избыточны.

---

# 21. Mobile analytics требует отдельной архитектуры

Текущий mobile `/stats` превращается в гигантскую страницу.

Не складывай desktop-блоки один под другой механически.

На mobile используй:

* overview;
* drill-down;
* accordions;
* segmented navigation;
* sticky tabs;
* progressive disclosure;
* compact cards;
* dedicated topic details;
* horizontal chart navigation;
* filter drawer.

Mobile — отдельная композиция, а не одна колонка desktop.

---

# 22. Навигация «Темы» должна быть переосмыслена

Сейчас top-level пункт `Темы` ведёт на:

`/stats#topics`

То есть фактически это anchor внутри Analytics.

Это нарушает mental model навигации.

Выбери одно:

### Вариант A

Создай полноценный `/topics`.

### Вариант B

Убери `Темы` из первого уровня.

### Вариант C

Полностью пересобери navigation hierarchy.

Не сохраняй top-level fake-page.

---

# 23. Settings требует новой композиции

На текущем `08-settings-session` форма визуально распадается.

В частности:

* левая колонка наполнена;
* правая содержит mode select и крупную полупустую карточку;
* между управляющими блоками слишком много пространства;
* sticky bottom CTA конкурирует с form content.

Перестрой settings как настоящий **session builder**.

Возможная mental model:

`Что тренируем → Как тренируем → Сколько → Что попадёт в сессию → Start`

Можно использовать:

* wizard-like flow без обязательных отдельных страниц;
* structured sections;
* live preview;
* session summary;
* adaptive side inspector;
* smart two-column grid.

Но не копируй этот пример механически.

---

# 24. Режим — не один select

В продукте восемь достаточно разных режимов.

Обычный `<select>` плохо показывает их смысл.

Исследуй более выразительный selector:

* mode cards;
* compact illustrated options;
* segmented cards;
* icon + short explanation;
* visual preview.

Pixel art можно использовать для уникальной идентичности режимов.

Например:

* Training — тренировочный двор;
* Study — библиотека;
* Flashcard — свиток;
* Typed Recall — писец;
* Interview — зал совета;
* Code Challenge — кузница;
* Exam — испытательный зал;
* Marathon — путь/карта.

Но интерфейс должен оставаться компактным.

---

# 25. Session Summary должен стать наградой за завершение

Итоги сессии — хорошее место для более сильного medieval/pixel-art storytelling.

Можно использовать:

* завершённый поход;
* заполненную хронику;
* печать;
* XP;
* новый ранг;
* карту прогресса;
* заработанный герб;
* улучшение навыка.

Но данные должны оставаться главными.

Не превращай summary в игровой экран без аналитической ценности.

---

# 26. Cold-start analytics

Текущие маленькие данные могут выглядеть как плохой результат пользователя.

Добавь отдельные состояния:

* insufficient data;
* first attempts;
* baseline building;
* learning phase.

Не показывай `0%` или `7.9%` как обвиняющую красную failure metric без контекста.

---

# 27. 404 должна быть пользовательской страницей

Технические:

* path;
* HTTP status;
* timestamp;
* request ID;

не должны быть главным содержимым 404.

Перемести diagnostics:

* в details;
* expandable section;
* copy debug info.

Главный экран:

* что произошло;
* куда можно пойти;
* как вернуться.

Здесь особенно уместен хороший pixel-art empty/error scene.

---

# 28. Light theme нужно проектировать отдельно

Не ограничивайся адаптацией dark tokens.

Средневековая light theme может использовать:

* parchment;
* warm ivory;
* linen;
* paper;
* faded ink;
* wood;
* oxidized bronze;
* restrained green;
* burgundy.

Dark может опираться на:

* dark stone;
* charcoal;
* old wood;
* steel;
* bronze;
* warm flame.

Обе должны принадлежать одной системе.

---

# 29. Новая цветовая система

Текущий cyan/teal не считать обязательным brand color.

Исследуй несколько палитр.

Например:

### Dark

* charcoal;
* graphite;
* warm black;
* dark stone;
* deep forest;
* oxidized bronze;
* ember;
* muted gold;
* burgundy.

### Light

* parchment;
* ivory;
* sand;
* desaturated wood;
* dark ink;
* forest green;
* burgundy;
* brass.

Не делай всё:

* коричневым;
* золотым;
* бежевым.

Используй semantic colors независимо от fantasy-theme.

---

# 30. Типографика

Основной текст должен быть максимально читаемым.

Можно заменить Inter.

Исследуй open-source fonts с хорошей кириллицей.

Допускается схема:

* readable sans/serif — основной UI;
* display serif — крупные тематические заголовки;
* mono — code/technical metadata.

Средневековый decorative font использовать крайне дозированно.

Никогда не использовать его для:

* MCQ answers;
* long explanations;
* tables;
* controls;
* code;
* mobile body text.

Проверяй лицензии шрифтов.

---

# 31. Иконографику нужно полностью систематизировать

Текущая система Lucide используется почти для всего.

Реши, какая система нужна новому продукту.

Допустимые варианты:

### A

Векторные system icons + pixel-art domain icons.

### B

Собственный небольшой SVG icon set.

### C

Open-source icon pack, визуально адаптированный.

Pixel-art лучше использовать для:

* категории;
* режимов;
* achievements;
* domain objects;

а не обязательно для:

* close;
* back;
* search;
* chevron;
* copy.

Не жертвуй usability ради темы.

---

# 32. Не сохраняй огромный CSS только потому, что он уже существует

В проекте значительная часть CSS page-specific.

Особенно крупные:

* `pages-focus.css`;
* `shell.css`;
* `pages-stats.css`;
* `components-controls.css`;
* `pages-settings.css`;
* `pixel-animations.css`.

Во время loop постоянно проверяй:

> большая ли часть CSS описывает настоящую систему или накопленные исключения?

Если новое решение позволяет:

* удалить десятки selectors;
* объединить layouts;
* заменить hacks общим primitive;
* убрать старые personalization branches;

делай это.

Цель — не только новый внешний вид, но и более чистая frontend-модель.

---

# 33. CSS layers сохранить можно, конкретную реализацию — необязательно

Существующая схема:

`tokens → reset → shell → components → pages → utilities → pixel`

логична и может сохраниться.

Но разрешается её менять, если найдена более сильная архитектура.

Не ломай manifest/bundling случайно.

После любых изменений проверь contract tests и bundle generation.

---

# 34. Персонализацию нужно подвергнуть ревью

Сейчас есть семь осей personalization.

Не считай их все полезными автоматически.

Проверь реальное назначение:

* layout;
* design;
* theme;
* font scale;
* reading width;
* motion;
* density.

Если какая-то ось:

* создаёт огромное число комбинаций;
* мешает новой дизайн-системе;
* мало полезна;
* требует слишком много CSS;
* порождает layout defects;

её можно:

* упростить;
* объединить;
* удалить;
* заменить presets.

Доступность и пользовательские размеры текста сохраняй.

---

# 35. macOS — обязательная целевая платформа

Проверяй реальный CSS viewport, а не только номинальное разрешение монитора.

Минимальная матрица:

* 1280×720;
* 1280×800;
* 1366×768;
* 1440×900;
* 1512×982;
* 1680×1050;
* 1728×1117;
* 1920×1080;
* 1920×1200;
* 2560×1440.

Особое внимание:

* MacBook 14";
* MacBook 16";
* Retina/DPR 2;
* browser toolbar;
* fullscreen;
* split view;
* half-window;
* 2/3 window;
* browser zoom 90%;
* 100%;
* 110%;
* 125%.

---

# 36. WebKit/Safari — обязательный gate

Проверяй:

* sticky;
* fixed;
* grid;
* flex;
* overflow;
* viewport units;
* `dvh`;
* `svh`;
* focus;
* form controls;
* select;
* backdrop filter;
* masks;
* SVG;
* Canvas;
* pixel rendering;
* animations;
* reduced motion;
* resize;
* scroll restoration.

Chromium pass недостаточен.

Если эффект плох в WebKit:

* исправь;
* добавь fallback;
* либо удали эффект.

---

# 37. Запрет бесполезного пустого пространства

Приложение не должно выглядеть как маленькая колонка внутри огромного монитора.

Особенно проверяй:

* ultrawide;
* MacBook fullscreen;
* 1920;
* 2560;
* режимы без MCQ;
* settings;
* error page;
* short lists;
* small datasets.

Запрещены:

* огромная пустая правая половина;
* гигантская пустота под form;
* бессмысленно узкий `max-width`;
* пустой rail;
* enormous hero;
* page bottom без композиционной причины;
* одна маленькая карточка в центре большого viewport.

Но:

**не заполняй экран мусором ради заполнения.**

---

# 38. Правило функционального заполнения пространства

При наличии свободной площади сначала спроси:

> какую полезную информацию или действие пользователь может получить здесь?

Используй:

* contextual information;
* session state;
* related questions;
* progress;
* topic map;
* shortcuts;
* learning history;
* preview;
* details;
* recommendation;
* table columns;
* inspector.

Если полезного контента нет:

* расширь рабочую область;
* перестрой layout;
* сократи высоту;
* добавь только лёгкий environmental decoration.

Не создавай бессмысленную secondary sidebar.

---

# 39. Осмысленный whitespace не запрещён

Whitespace нужен для:

* hierarchy;
* чтения;
* группировки;
* фокуса.

Дефект — не свободное пространство само по себе.

Дефект:

> большое пространство, которое выглядит как незаполненный или сломанный layout.

На каждой итерации различай:

* deliberate whitespace;
* wasted space.

---

# 40. Первый viewport

На типичном MacBook viewport без скролла пользователь должен видеть:

* контекст;
* вопрос или цель страницы;
* главное действие;
* значительную часть рабочего интерфейса.

Не отдавай большую часть первого экрана:

* breadcrumb;
* декоративному hero;
* огромному title;
* пустому фону;
* whitespace.

---

# 41. Responsive — это recomposition

Не просто уменьшай desktop.

На mobile можно полностью перестраивать экран:

* sidebar → bottom navigation;
* two-column → staged flow;
* analytics → overview + drilldown;
* rail → drawer;
* toolbar → compact action bar;
* tables → lists/cards;
* secondary information → disclosure.

Проверяй:

* 1024;
* 768;
* 430;
* 390;
* 360.

---

# 42. Mobile navigation тоже можно поменять

Текущая bottom navigation не является неприкосновенной.

Проверь:

* число top-level destinations;
* приоритет training;
* settings frequency;
* topics/stats conflict.

При необходимости полностью измени mobile IA.

---

# 43. Основная навигация должна отражать реальные сущности

Проверь, действительно ли top-level должны быть:

* Тренировка;
* Темы;
* Аналитика;
* Ошибки;
* Закладки;
* Настройки.

Допускается новая структура, например:

* Тренировка;
* Карта знаний;
* Повторение;
* Прогресс;
* Библиотека;
* Настройки.

Это только направление для исследования, а не обязательная структура.

Названия должны отражать реальную модель пользователя.

---

# 44. Возможность добавить новые frontend/backend функции

Разрешается улучшать функциональность.

Можно добавлять:

* `/topics`;
* history;
* persistent session summaries;
* topic detail;
* session history;
* compact analytics API;
* new filters;
* saved session presets;
* richer progression;
* summary persistence;
* additional stats;
* better search.

Но не добавляй функции только ради заполнения UI.

Они должны улучшать реальный сценарий.

---

# 45. Session Summary persistence

Сейчас summary одноразовый.

Исследуй возможность сохранения истории результатов.

Это потенциально даст:

* полезный Session History;
* richer analytics;
* меньше потери информации;
* более естественный progression.

Если изменение оправдано:

* проектируй backend;
* persistence;
* migration;
* UI;
* tests.

Не делай frontend-заглушку без данных.

---

# 46. No LLM

В продукте нет LLM-контура.

Не предлагай и не реализуй:

* AI explanation;
* AI scoring;
* generated questions;
* chat;
* LLM hints;

если отдельной задачей не добавляется полноценный контур.

Редизайн должен работать с существующей продуктовой моделью.

---

# 47. Режим loop

Каждая итерация:

1. Проверить `git status`.
2. Проверить `git diff`.
3. Прочитать state.
4. Запустить приложение.
5. Посмотреть текущую реализацию.
6. Проверить Chromium.
7. Проверить WebKit.
8. Сделать screenshots.
9. Найти самый большой remaining problem.
10. Проверить, не нужно ли `REOPEN` ранее принятого решения.
11. Исследовать alternatives.
12. При необходимости искать open-source pixel assets.
13. Проверить licenses.
14. Спроектировать решение.
15. Реализовать.
16. Изменить backend при необходимости.
17. Реализовать dark.
18. Реализовать light.
19. Реализовать responsive.
20. Реализовать no-JS fallback.
21. Реализовать loading/empty/error.
22. Проверить accessibility.
23. Проверить reduced motion.
24. Проверить macOS viewports.
25. Проверить WebKit.
26. Проверить console/network.
27. Обновить tests.
28. Удалить старый код.
29. Удалить старые assets.
30. Обновить docs/state.
31. Провести adversarial review.
32. Исправить найденное.
33. Повторить browser verification.
34. Завершить итерацию.

---

# 48. Выбор задачи в начале итерации

Приоритет:

1. Broken build/runtime.
2. Системно неправильная art direction.
3. Плохой application shell.
4. Неудачная navigation.
5. Training main flow.
6. Mode-aware layouts.
7. Большие wasted spaces.
8. macOS/ultrawide.
9. WebKit problems.
10. Settings/session builder.
11. Post-answer learning flow.
12. Analytics IA.
13. Mobile analytics.
14. Topics navigation.
15. Summary.
16. Review.
17. Bookmarks.
18. Error/empty states.
19. Light theme.
20. Icon system.
21. Pixel art system.
22. Motion.
23. CSS simplification.
24. Accessibility.
25. Performance.
26. Final polish.

Не выбирай задачу только потому, что она маленькая.

---

# 49. Одна итерация = одна законченная область

Хорошая итерация:

* redesign application shell;
* redesign training;
* redesign typed/interview/code modes;
* redesign analytics;
* redesign mobile analytics;
* redesign settings;
* replace icon system;
* replace pixel-art system;
* rebuild light theme;
* solve ultrawide composition;
* Safari compatibility;
* rebuild explanation flow.

Плохая итерация:

* поменять один цвет;
* увеличить одну карточку;
* заменить одну иконку;
* добавить один sprite;
* только написать audit;
* поверхностно изменить всё приложение.

---

# 50. State

Поддерживай:

`docs/frontend-redesign-state.md`

Храни:

* current art direction;
* current navigation;
* current layout model;
* active design decisions;
* superseded decisions;
* reopened decisions;
* page statuses;
* mode statuses;
* design-system status;
* dark status;
* light status;
* macOS verification;
* WebKit verification;
* mobile verification;
* assets;
* licenses;
* accessibility;
* performance;
* CSS migration;
* remaining old UI;
* next priority.

Статусы:

* `PENDING`;
* `IN_PROGRESS`;
* `IMPLEMENTED`;
* `BROWSER_VERIFIED`;
* `WEBKIT_VERIFIED`;
* `MACOS_VERIFIED`;
* `AUDITED`;
* `REOPENED`;
* `SUPERSEDED`;
* `BLOCKED`.

---

# 51. Screenshots как обязательная проверка

Используй screenshots не только для отчёта.

Сравнивай композицию.

Для каждой крупной области:

### BEFORE

* 1280;
* 1440;
* 1512;
* 1728;
* 1920;
* 2560;
* mobile.

### AFTER

те же размеры.

Особенно проверяй **fold screenshots**, а не только fullPage.

FullPage плохо показывает composition первого viewport.

---

# 52. Реальные данные

Не проектируй только под идеальные mock data.

Проверяй:

* короткий вопрос;
* длинный вопрос;
* вопрос с code;
* вопрос без code;
* длинные варианты;
* flashcard;
* 0 данных;
* мало данных;
* много данных;
* пустые bookmarks;
* длинные notes;
* много topics;
* очень низкая accuracy;
* высокая accuracy;
* large review queue.

---

# 53. Accessibility gate

Каждая область должна пройти:

* keyboard;
* focus;
* contrast;
* screen reader semantics;
* reduced motion;
* 200% zoom;
* text enlargement;
* no-color state distinction;
* form error behavior.

Pixel art — декоративный слой, а не accessibility mechanism.

---

# 54. Performance gate

Следи особенно за:

* `pixel-fx.js`;
* sprite sheets;
* animation count;
* decorative DOM;
* CSS growth;
* large page CSS;
* WebKit performance;
* excessive filters;
* backdrop blur;
* box shadows;
* Canvas.

Не делай средневековый дизайн ценой постоянной нагрузки GPU.

---

# 55. Pixel motion

Хорошие микроанимации:

* flame;
* banner;
* smoke;
* ember;
* page marker;
* seal;
* map marker;
* chest opening;
* forge spark;
* subtle metallic gleam.

Плохие:

* постоянно движущийся фон;
* десятки одновременно активных sprites;
* flying particles через весь UI;
* навязчивый parallax;
* motion у каждой кнопки.

---

# 56. Empty states

Используй medieval pixel-art storytelling.

Например:

* bookmarks: пустая полка;
* review: очищенная доска заданий;
* stats cold start: чистая хроника;
* 404: потерянная карта;
* no session: закрытые ворота тренировочного двора;
* no topics: пустой каталог библиотеки.

Обязательно сопровождай понятным текстом и CTA.

---

# 57. Loading

Можно использовать тематические loading states:

* кузнечный молот;
* перелистывание книги;
* написание строки;
* вращающаяся руна;
* движение маркера;
* пламя.

Но loading должен оставаться:

* быстрым;
* тихим;
* non-blocking;
* reduced-motion safe.

---

# 58. Новый визуальный язык не должен быть карикатурой

Избегай:

* faux parchment на каждом div;
* medieval font везде;
* толстых золотых рамок;
* fake leather textures под текстом;
* sword icons вместо обычных actions;
* огромных shields;
* excessive bevel;
* skeuomorphism ради skeuomorphism.

Средневековая тема должна читаться через:

* palette;
* materials;
* micro details;
* illustration;
* progression metaphors;
* typography accents;
* pixel scenes.

---

# 59. Удаление старой реализации

После миграции области удаляй:

* старые styles;
* старые selectors;
* obsolete tokens;
* dead assets;
* unused icons;
* deprecated fragments;
* CSS overrides;
* old JS branches;
* no-longer-used personalization modes.

Не создавай новый дизайн поверх старого слоями.

---

# 60. Проверка CSS complexity

После каждой крупной итерации измеряй:

* CSS bytes;
* selector count;
* overridden declarations;
* page-specific CSS;
* dead selectors;
* duplicated declarations.

Новая система не должна бесконечно увеличивать существующие 100+ KB page CSS.

Если после redesign файла стало существенно больше — разберись почему.

---

# 61. Browser matrix

Минимум:

### Chromium

* 1280×720;
* 1440×900;
* 1512×982;
* 1728×1117;
* 1920×1080;
* 2560×1440.

### WebKit

те же критические размеры.

### Mobile

* 360;
* 390;
* 430.

### Дополнительно

* zoom 90%;
* 110%;
* 125%;
* narrow desktop window;
* half-screen;
* resize without reload.

---

# 62. Проверка wasted space

Для каждого screenshot оцени:

* сколько viewport занято полезной информацией;
* где концентрируется visual weight;
* есть ли giant dead areas;
* есть ли бессмысленно пустой rail;
* есть ли слишком большая высота;
* может ли важный контент подняться выше;
* работает ли composition при коротком content.

Не используй автоматический numerical threshold как единственный критерий.

Это визуальная композиционная проверка.

---

# 63. Adversarial review

После реализации попробуй забраковать результат с ролей:

* Product Designer;
* Art Director;
* UX Designer;
* Senior Frontend Engineer;
* Pixel Artist;
* Accessibility Specialist;
* WebKit specialist;
* macOS user;
* learner preparing for interview.

Ищи:

* cheap RPG aesthetic;
* unreadable medieval font;
* wasted space;
* overdecorated panels;
* confusing hierarchy;
* fake buttons;
* weak CTA;
* mode inconsistency;
* broken light theme;
* poor mobile;
* Safari bugs;
* pixel blur;
* mismatched assets;
* heavy animations;
* CSS complexity;
* no-JS regression.

Исправляй найденное в этой же итерации.

---

# 64. Критерии `AUDITED`

Область получает `AUDITED`, только если:

* полностью реализована;
* старая реализация удалена;
* соответствует актуальной art direction;
* dark проверена;
* light проверена;
* Chromium проверен;
* WebKit проверен;
* macOS viewports проверены;
* mobile проверен;
* no-JS не сломан;
* keyboard работает;
* accessibility проверена;
* reduced motion работает;
* loading реализован;
* empty реализован;
* error реализован;
* long content проверен;
* short content проверен;
* wasted space проверен;
* assets лицензированы;
* pixel art не размыт;
* performance приемлема;
* tests проходят;
* build проходит;
* state обновлён.

---

# 65. Даже `AUDITED` можно переоткрыть

Если последующая итерация показывает, что старый `AUDITED` экран больше не соответствует системе:

`AUDITED → REOPENED`

Далее:

* переработай;
* перепроверь;
* снова доведи до `AUDITED`.

Не сохраняй плохую совместимость ради статуса.

---

# 66. Финальная стадия loop

Когда основные экраны готовы, переходи к системным итерациям:

1. Global art-direction review.
2. Navigation review.
3. Cross-mode consistency.
4. Dark/light parity.
5. macOS layout review.
6. Ultrawide review.
7. WebKit review.
8. Mobile review.
9. Empty/error review.
10. Pixel-art consistency.
11. Asset-license review.
12. Motion review.
13. Accessibility review.
14. Performance review.
15. CSS architecture review.
16. Dead-code removal.
17. Final visual regression.
18. Final adversarial review.

---

# 67. Условие остановки loop

Loop можно остановить только когда:

* весь старый ключевой UI заменён;
* основные страницы `AUDITED`;
* все восемь режимов имеют осмысленную композицию;
* нет giant wasted spaces;
* ultrawide выглядит намеренно;
* MacBook viewports выглядят качественно;
* WebKit работает;
* dark и light равноценны;
* mobile имеет собственную композицию;
* analytics не перегружена;
* settings не разваливается;
* navigation имеет ясную модель;
* pixel-art система согласована;
* лицензии подтверждены;
* accessibility пройдена;
* reduced motion работает;
* no-JS flow работает;
* CSS очищен;
* tests/build проходят;
* серьёзных новых улучшений после финального adversarial review нет.

Не создавай бессмысленные изменения только чтобы loop продолжался.

---

# 68. Запрещённое поведение

Запрещено:

* сохранять текущий дизайн из-за sunk cost;
* считать `AUDITED` вечным;
* делать только cosmetic redesign;
* менять только palette;
* менять только fonts;
* добавлять medieval decoration поверх нынешнего UI;
* сохранять пустые rails;
* сохранять giant blank canvas;
* лечить ultrawide одним `max-width`;
* использовать MCQ layout для всех режимов;
* складывать desktop analytics в одну mobile колонку;
* использовать нелицензированные assets;
* использовать ripped game graphics;
* использовать decorative font в body;
* делать light theme позже;
* проверять только Chromium;
* игнорировать no-JS;
* отключать reduced motion;
* наращивать CSS бесконечными overrides;
* оставлять старый и новый UI параллельно;
* добавлять функции только ради заполнения пространства;
* заканчивать итерацию планом.

---

# 69. Работа с Git

В начале:

* `git status`;
* `git diff`;
* current branch.

Не использовать:

* `git reset --hard`;
* `git clean -fd`;
* destructive checkout.

Не уничтожать чужие незакоммиченные изменения.

Не делать commit/push без отдельной команды.

---

# 70. Финальный прогон каждой итерации

Запусти доступные:

* formatter;
* lint;
* CSS leak audit;
* CSS bundle contract;
* dead CSS check;
* JS checks;
* backend tests;
* frontend tests;
* integration tests;
* E2E;
* Chromium;
* WebKit;
* no-JS flow;
* accessibility;
* visual regression;
* frontend/backend build;
* asset optimization;
* license check;
* console check;
* network check.

Не ослабляй тесты ради прохождения.

---

# 71. Отчёт итерации

Выводи только фактически сделанное:

1. Какая область выбрана.
2. Почему именно она.
3. Какие старые design decisions переоткрыты.
4. Что было удалено.
5. Что полностью переработано.
6. Как изменился UX.
7. Как изменился layout.
8. Как устранено wasted space.
9. Что изменилось на MacBook.
10. Что изменилось на ultrawide.
11. Какие режимы затронуты.
12. Какие components изменены.
13. Какие CSS части удалены.
14. Какие новые assets использованы.
15. Источники assets.
16. Лицензии.
17. Какие sprites адаптированы.
18. Какие animations добавлены.
19. Dark/light status.
20. Mobile status.
21. WebKit status.
22. No-JS status.
23. Accessibility status.
24. Performance status.
25. Tests.
26. Screenshots.
27. Найденные проблемы.
28. Исправления.
29. Итоговый статус.
30. Следующий самый важный участок.

Не выдавай планируемое за сделанное.

---

# 72. Главный принцип

Не улучшай существующий дизайн бесконечно маленькими шагами.

Loop должен постепенно **заменить его целиком**.

Каждая итерация отвечает на вопрос:

> Какой один наиболее значимый кусок мешает Cheat Sheet выглядеть и работать как единый профессиональный medieval knowledge trainer?

После выбора:

`исследуй → переоткрой старые решения при необходимости → спроектируй заново → найди/проверь assets → реализуй → удали старое → Chromium → WebKit → macOS → mobile → no-JS → accessibility → performance → adversarial review → AUDITED`

Начинай текущую итерацию с реального запуска приложения и сравнения текущих экранов с документацией и screenshot-архивом.

Особое внимание сначала удели:

1. application shell;
2. ultrawide-композиции главного экрана;
3. mode-aware layout для `TYPED_RECALL` / `INTERVIEW` / `CODE_CHALLENGE`;
4. новой art direction и pixel-art системе;
5. session builder;
6. analytics IA.

Не считай ранее выполненную работу препятствием для более сильного решения.