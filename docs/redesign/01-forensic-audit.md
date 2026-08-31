# Forensic-аудит продукта перед редизайном

Миссия `missions/cheat-sheet-visual-redesign.md` §4 требует ПЕРЕД правкой
production-кода собрать внутреннюю модель фронтенда по восьми рубрикам: ЧТО ЕСТЬ,
ЧТО ХОРОШО, ЧТО ПЛОХО, ЧТО СЛУЧАЙНО, ЧТО ФУНДАМЕНТАЛЬНО, ЧТО УСТАРЕЛО, ЧТО
ПЕРЕУСЛОЖНЕНО, ЧЕГО НЕ ХВАТАЕТ. §5 требует пройти 18 экранов и запрещает вывод
«если Home красивый — остальные тоже». §6 требует пройти 8 режимов как отдельные
композиции. Этот документ — единственный источник для следующего шага миссии
(гипотеза дизайна, `docs/redesign/02-design-hypothesis.md`).

Каждое утверждение здесь несёт якорь: `path:line` от корня репозитория, имя
контракт-теста, имя e2e-спека или имя PNG из `.sdlc/CS-20260829-044/shots/`.
Резолвинг якорей и доказанность буллетов в рубриках ПЛОХО/СЛУЧАЙНО/УСТАРЕЛО/
ПЕРЕУСЛОЖНЕНО/НЕ ХВАТАЕТ проверяет `.sdlc/CS-20260829-044/recon/check-anchors.sh`.

## Метод и ограничения

Аудит выполнен в два прохода.

**Проход 1 — статическая форензика.** Сплошной обход инвентаря: 35 CSS
(`static/css/*.css`), 43 JS-модуля (`static/js/**/*.js`), 26 шаблонов
(`templates/*.html` + `templates/fragments/*.html`), `css-bundle-manifest.txt`,
`tokens.css`, 74 UI-контракт-теста (`src/test/.../ui/*ContractTest.java`), 7
Playwright-спеков (`e2e/*.spec.js`), 36 досье решений (`docs/css-decisions/*.md`).
Цифры ниже — не из памяти: каждая в разделе «Инвентарь» несёт команду проверки,
и они замерены заново в этой сессии (числа design.md близки, но не тождественны —
здесь везде свежий счёт).

**Проход 2 — живое наблюдение.** Изолированная среда:

- **SHA:** ветка `sdlc/CS-20260829-044`, HEAD на момент замеров — коммит,
  предшествующий этому аудиту (`274b49a0`); правок кода в проходе 2 не было.
- **БД:** изолированная `interview_recon_044` в уже поднятом контейнере
  `quiz-postgres` (`CREATE DATABASE interview_recon_044 OWNER interview`), дропнута
  по завершении (`DROP DATABASE interview_recon_044` — подтверждено).
  Пользовательская `interview` не тронута: `review_state` до/после — 265/265
  (`.sdlc/CS-20260829-044/data/user-db-guard.txt`).
- **Порт:** `8091` (хартия §6), не `8080` (там живой bootRun пользователя — не
  занимался). Старт: `SERVER_PORT=8091 SPRING_PROFILES_ACTIVE=qa
  SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/interview_recon_044
  ./gradlew :quiz-app:bootRun --no-daemon`, лог —
  `.sdlc/CS-20260829-044/data/boot.log.txt`.
- **Security mode:** запущено с `APP_SECURITY_MODE=multi` — НЕ дефолт продакшена
  (дефолт — `local`, `modules/quiz-app/src/main/resources/application.yml:143`), а осознанное отклонение ради экрана
  «Login» из §5. Причина и последствие — см. находку в разделе «ЧТО ПЛОХО»
  («Login недостижим без явного override окружения»). Логин выполнен один раз
  формой `/login` (`trainer`/`trainer`, дефолт `modules/quiz-app/src/main/resources/application.yml:147-148`),
  `storageState` переиспользован во всех контекстах кроме анонимного снимка самой
  `/login` — файл состояния сессии писался в `/tmp`, не в репозиторий (сессионный
  cookie — не то, что стоит коммитить).
- **Браузер:** Chromium через Playwright из общего `e2e/node_modules` (уже
  установлен — `npm ci` не понадобился), запуск с `NODE_PATH=e2e/node_modules`
  (require скрипта из `.sdlc/CS-20260829-044/recon/` иначе не резолвит пакет).
- **Инструмент разведки:** 2 скрипта в `.sdlc/CS-20260829-044/recon/`
  (адаптация `.sdlc/CS-20260828-003/recon/shots.js` и `space.js`, объединённых в
  один проход):
  - `shots.js` — снимает 26 PNG (матрица ниже) + `data/shots-index.json` +
    `data/space-metrics.json` (§17-метрики в том же проходе, без второго запуска
    браузера);
  - `check-anchors.sh` — резолвит якоря этого документа (см. выше).
- **Живой пересчёт `css-leak-audit.py`:** `python3 scripts/css-leak-audit.py` —
  «syntax: OK (35 файлов)», ни одного `leak` без пометки `OK usage`, «carriers: OK
  (0 носителей без коробки)» — использовано в разделе «ЧТО ХОРОШО».

### Живая матрица (26 PNG, `.sdlc/CS-20260829-044/shots/`)

10 не-workspace экранов, тёмная тема, 1440×900:

| Файл | Экран |
|---|---|
| `train-dark-1440x900.png` | Training (конструктор сессии) |
| `topics-dark-1440x900.png` | Topics |
| `stats-dark-1440x900.png` | Analytics |
| `bookmarks-dark-1440x900.png` | Bookmarks |
| `settings-dark-1440x900.png` | Settings |
| `login-dark-1440x900.png` | Login (снят анонимно) |
| `error-dark-1440x900.png` | Error (404 на несуществующий путь) |
| `review-dark-1440x900.png` | `/review` (разбор ошибок) |
| `result-dark-1440x900.png` | Result — no-JS фоллбэк `/answer` (`javaScriptEnabled: false`) |
| `summary-dark-1440x900.png` | Session Summary (EXAM → POST /finish) |

8 состояний workspace (Focus), по одному на `InterviewMode`, тёмная тема, 1440×900:
`workspace-training-dark-1440x900.png`, `workspace-exam-dark-1440x900.png`,
`workspace-marathon-dark-1440x900.png`, `workspace-study-dark-1440x900.png`,
`workspace-flashcard-dark-1440x900.png`, `workspace-typed_recall-dark-1440x900.png`,
`workspace-interview-dark-1440x900.png`, `workspace-code_challenge-dark-1440x900.png`.

4 ultrawide 2560×1440 (§17): `workspace-training-dark-2560x1440.png`,
`stats-dark-2560x1440.png`, `summary-dark-2560x1440.png`, `topics-dark-2560x1440.png`.

4 светлая тема 1440×900: `workspace-training-light-1440x900.png`,
`stats-light-1440x900.png`, `settings-light-1440x900.png`,
`summary-light-1440x900.png`.

### Ограничения этого прохода

- **Матрица — не полное покрытие.** 18 экранов × 8 режимов × 2 темы дало бы 288
  комбинаций; кап задачи (40 файлов) допускает 26 PNG. Выбор — тот же, что в
  design.md: закрыть §5 (все 18 названы хотя бы одним снимком или таблицей), §6
  (все 8 режимов — отдельным снимком workspace) и §17 (4 ultrawide-кадра).
- **Экран preset НЕ автоопределяется по вьюпорту.** `readScreenPreset()`
  (`modules/quiz-app/src/main/resources/templates/fragments/head.html:229-234`) читает `localStorage.screenPreset`
  и при его отсутствии возвращает жёстко `'laptop'` — НЕ вычисляет пресет по
  `window.innerWidth`. Слова `phone`/`laptop`/`wide` в `SCREEN_PRESETS`
  (`modules/quiz-app/src/main/resources/templates/fragments/head.html:205-209`) — имена профилей, не автодетект устройства. Recon явно
  чистит `localStorage` перед каждым снимком (`shots.js` — `applyPrefs`), поэтому
  ВСЕ 26 кадров, включая 4 ultrawide, сняты в дефолтном пресете `laptop`
  (`data-reading-width` не выставлен). Это не искажение замера, а ТОЧНОЕ
  воспроизведение того, что видит реальный первый визит на широком мониторе —
  но означает, что снимки НЕ показывают путь `[data-reading-width="wide"]` и
  media-блоки `@media (min-width: 2240px)` в лучшем случае. Подробности —
  раздел «Мёртвое пространство».
- **`interview_recon_044` — пустая по истории.** SRS-фикстур в профиле `qa`
  всего 3 (`due=2/learned=1`, `modules/quiz-app/src/main/resources/application-qa.yml:16-17`), поэтому
  Summary/Review показывают ранние/пустые состояния, а не зрелые. Для §17 и
  композиции это не помеха (измеряется геометрия, а не данные), но там, где
  находка зависит от ОБЪЁМА истории (например, вид `/bookmarks` с сотней
  закладок), она явно помечена как «не проверено».
- **Предшествующая разведка устарела.** `.sdlc/CS-20260828-003/` (report.md,
  50 PNG, `pixel-layer-scan.json`) сделана до `b56bf4c6` (FOLIO, снос
  pixel-слоя) — весь её визуальный материал описывает несуществующий дизайн.
  Переиспользован только МЕТОД (`recon/shots.js` структура), не выводы.

## Инвентарь

Все числа проверяются заново командой из колонки «команда проверки» (запуск из
корня worktree). Ревьюер выполняет их построчно — расхождений быть не должно.

| Область | Число | Команда проверки |
|---|---|---|
| CSS-файлы | 35 (10 755 строк) | `ls modules/quiz-app/src/main/resources/static/css/*.css \| wc -l` и `wc -l modules/quiz-app/src/main/resources/static/css/*.css \| tail -1` |
| JS-модули | 43 (5 600 строк) | `find modules/quiz-app/src/main/resources/static/js -name '*.js' \| wc -l` и `find modules/quiz-app/src/main/resources/static/js -name '*.js' -exec wc -l {} \; \| awk '{s+=$1} END{print s}'` |
| Шаблоны-страницы | 10 (4 272 строки) | `ls modules/quiz-app/src/main/resources/templates/*.html \| wc -l` |
| Фрагменты | 16 (1 321 строка) | `ls modules/quiz-app/src/main/resources/templates/fragments/*.html \| wc -l` |
| UI-контракт-тесты | 74 | `find modules/quiz-app/src/test -name '*ContractTest.java' \| wc -l` |
| Playwright-спеки | 7 | `ls e2e/*.spec.js \| wc -l` |
| Визуальные голдены | 28 = 14×2 движка | `ls e2e/visual-baselines/chromium \| wc -l` и `ls e2e/visual-baselines/webkit \| wc -l` |
| Досье CSS-решений | 36 | `ls docs/css-decisions/*.md \| wc -l` |
| CSS без досье решения | 7 | цикл ниже |

### Полный список CSS-файлов (35)

| Файл | Строк | Гейт манифеста (`css-bundle-manifest.txt`) |
|---|---|---|
| `pages-focus.css` | 2205 | `focus` |
| `shell.css` | 1084 | `*` |
| `pages-stats.css` | 1062 | `stats` |
| `pages-settings.css` | 733 | `settings` |
| `components-controls.css` | 478 | `*` |
| `pages-summary.css` | 472 | `summary` |
| `tokens.css` | 321 | `*` |
| `pages-topics.css` | 309 | `topics` |
| `components-progression.css` | 308 | `*` |
| `pages-error.css` | 303 | `error` |
| `pages-result.css` | 295 | `result` |
| `utilities-overrides.css` | 285 | `*` |
| `pages-bookmarks.css` | 278 | `bookmarks` |
| `reset-layout.css` | 265 | `*` |
| `components-answer.css` | 239 | `focus,result` |
| `components-widgets.css` | 216 | `*` |
| `print.css` | 203 | `*` |
| `fonts-local.css` | 203 | не в манифесте (подключается отдельно шрифтовым preload) |
| `components-filter-bar.css` | 187 | `stats,bookmarks,topics` |
| `components-modals.css` | 180 | `*` |
| `pages-login.css` | 141 | `login` |
| `components-empty-state.css` | 141 | `*` |
| `components-table.css` | 126 | `*` |
| `components-tabs.css` | 121 | `*` |
| `reset.css` | 119 | `*` |
| `components-prose.css` | 93 | `*` |
| `components-pending.css` | 91 | `*` |
| `components-notice.css` | 72 | `*` |
| `components-code-highlight.css` | 69 | `focus,result` |
| `components-icons.css` | 40 | `*` |
| `pages-focus-confidence.css` | 27 | `focus` |
| `utilities-core.css` | 25 | не в манифесте |
| `components-signatures.css` | 24 | `*` |
| `components-card.css` | 23 | `*` |
| `base.css` | 17 | не в манифесте (см. «ЧТО УСТАРЕЛО») |

Порядок каскада — **не** порядок этого списка: единственный источник правды —
`modules/quiz-app/src/main/resources/css-bundle-manifest.txt`, порядок слоёв
объявлен `modules/quiz-app/src/main/resources/static/css/tokens.css:4` (`@layer tokens, reset, shell, components,
pages, utilities;`). Шапка отдаёт ОДИН `<link>`
(`modules/quiz-app/src/main/resources/templates/fragments/head.html:333`), собранный `CssBundleService`
(`modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/CssBundleService.java`)
по манифесту; порядок строк манифеста непереставляем (многие файлы объявляют
больше одного `@layer`).

### Полный список JS-модулей (43, путь относительно `static/js/`)

| Модуль | Строк |
|---|---|
| `content/mermaid-renderer.js` | 753 |
| `quiz/interview.js` | 707 |
| `stats/topic-charts.js` | 429 |
| `settings/session-form.js` | 276 |
| `ui/modals.js` | 213 |
| `ui/pending.js` | 180 |
| `stats/chart-theme.js` | 171 |
| `quiz/workspace.js` | 169 |
| `app-entry.js` | 156 |
| `core/api-client.js` | 155 |
| `ui/export.js` | 153 |
| `ui/keyboard-help.js` | 147 |
| `quiz/bookmarks.js` | 144 |
| `ui/code-toolbar.js` | 129 |
| `summary/share-tools.js` | 122 |
| `ui/tablist.js` | 113 |
| `quiz/flashcard.js` | 112 |
| `content/table-scroll.js` | 112 |
| `stats/accuracy-trend.js` | 109 |
| `ui/shell.js` | 106 |
| `quiz/session-stats.js` | 97 |
| `settings/personalization.js` | 93 |
| `ui/mobile-disclosures.js` | 77 |
| `ui/form-guards.js` | 75 |
| `ui/cycle-focus.js` | 69 |
| `stats/chart-loader.js` | 69 |
| `stats/index.js` | 65 |
| `ui/alerts.js` | 64 |
| `settings/personalization-bootstrap.js` | 61 |
| `core/dom.js` | 60 |
| `stats/difficulty-donut.js` | 55 |
| `content/sanitize.js` | 51 |
| `quiz/streak.js` | 49 |
| `quiz/favorites.js` | 49 |
| `core/lifecycle.js` | 42 |
| `stats/topic-data.js` | 33 |
| `content/materials-tabs.js` | 32 |
| `stats/forecast-dates.js` | 28 |
| `core/constants.js` | 22 |
| `content/highlight.js` | 22 |
| `content/dynamic-content.js` | 13 |
| `settings/tabs.js` | 11 |
| `core/csrf.js` | 7 |

### Полный список шаблонов и фрагментов (26)

Страницы (`templates/*.html`, 10): `bookmarks.html` (334), `error.html` (113),
`focus-training.html` (1181), `login.html` (113), `result.html` (270),
`session-summary.html` (365), `settings.html` (291), `stats.html` (1266),
`topics.html` (293), `train.html` (46).

Фрагменты (`templates/fragments/*.html`, 16): `head.html` (369), `icons.html`
(73), `mermaid-init.html` (6), `mobile-nav.html` (23), `notice.html` (25),
`post-answer-controls.html` (21), `progression.html` (111),
`recoverable-banners.html` (35), `result-zone-head.html` (13), `scripts.html`
(7), `session-builder.html` (335), `shell-head.html` (89), `sidebar.html` (78),
`state-illustrations.html` (29), `today-widget.html` (77),
`training-actions.html` (30).

### CSS без досье решения (7 из 35)

Цикл `for f in $(ls .../css/*.css | xargs -n1 basename -s .css); do test -f
docs/css-decisions/$f.md || echo "NO DOC: $f"; done` даёт: `base`,
`components-icons`, `components-modals`, `components-signatures`,
`fonts-local`, `pages-topics`, `utilities-core`. Не все семь одинаково
«случайны» — у пятерых из семи есть замена документации (собственный
объяснительный комментарий в файле + контракт-тест): `modules/quiz-app/src/main/resources/static/css/components-icons.css:1-4`
+ `IconLanguageContractTest`; `modules/quiz-app/src/main/resources/static/css/components-modals.css:1-4` (объясняет вынос из
`pages-result.css`); `modules/quiz-app/src/main/resources/static/css/fonts-local.css:1` (`OPS-005` + `FontPreloadContractTest`);
`modules/quiz-app/src/main/resources/static/css/pages-topics.css:1-4` (объясняет момент появления, iter-103); `base.css`
намеренно исключён тестом (`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/TemplateFragmentContractTest.java:1562-1563`).
Два НЕ имеют ни того, ни другого — они разобраны в разделе «ЧТО СЛУЧАЙНО».

## Карта экранов

`InterviewMvcController` =
`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java`.

| № | Экран §5 | Маршрут | Шаблон | Наблюдение |
|---|---|---|---|---|
| 1 | Home | `GET /` (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java:45`) | `focus-training.html`, `page='focus'` | то же, что Focus (№11) |
| 2 | Training | `GET /training` (`:82`) → 302 на `/` | легаси-алиас | `train-dark-1440x900.png` — конструктор сессии `/train` (`:188`) |
| 3 | Topics | `GET /topics` (`:375`) | `topics.html` | `topics-dark-1440x900.png`, `topics-dark-2560x1440.png` |
| 4 | Analytics | `GET /stats` (`:342`) | `stats.html` | `stats-dark-1440x900.png`, `stats-dark-2560x1440.png`, `stats-light-1440x900.png` |
| 5 | Bookmarks | `GET /bookmarks` (`:383`) | `bookmarks.html` | `bookmarks-dark-1440x900.png` |
| 6 | Settings | `GET /settings` (`:123`) | `settings.html` | `settings-dark-1440x900.png`, `settings-light-1440x900.png` |
| 7 | Session Summary | `GET /session-summary` (`:286`) | `session-summary.html` | `summary-dark-1440x900.png` + ultrawide/light-варианты |
| 8 | Login | `GET /login` (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java:19-20`) | `login.html` | `login-dark-1440x900.png`; см. «ЧТО ПЛОХО» — недостижим в дефолтном режиме |
| 9 | Error | Spring error-mapping | `error.html` | `error-dark-1440x900.png` (404 на несуществующий путь) |
| 10 | Result | `POST /answer` (`:291`) | `result.html` | `result-dark-1440x900.png` — no-JS SSR-фоллбэк |
| 11 | Focus | `GET /` в TRAINING | `focus-training.html` (2205 строк CSS) | `workspace-training-dark-1440x900.png` |
| 12 | Typed Recall | `POST /start` mode=TYPED_RECALL | `focus-training.html` | `workspace-typed_recall-dark-1440x900.png` |
| 13 | Interview | `POST /start` mode=INTERVIEW | `focus-training.html` | `workspace-interview-dark-1440x900.png` |
| 14 | Code Challenge | `POST /start` mode=CODE_CHALLENGE | `focus-training.html` | `workspace-code_challenge-dark-1440x900.png` |
| 15 | Study | `POST /start` mode=STUDY | `focus-training.html` | `workspace-study-dark-1440x900.png` |
| 16 | Flashcard | `POST /start` mode=FLASHCARD | `focus-training.html` | `workspace-flashcard-dark-1440x900.png` |
| 17 | Exam | `POST /start` mode=EXAM | `focus-training.html` | `workspace-exam-dark-1440x900.png` |
| 18 | Marathon | `POST /start` mode=MARATHON | `focus-training.html` | `workspace-marathon-dark-1440x900.png` |

Вне списка §5, но существуют и попали в аудит: `GET /train` (`:188`,
конструктор сессии — снят выше как Training) и `GET /review` (`:94`, разбор
ошибок — `review-dark-1440x900.png`, показывает вопрос с ошибкой despite
свежей recon-БД, т.к. на первом же вопросе TRAINING перед снимком была
зафиксирована одна неверная попытка через `result-dark-1440x900.png`).

## Карта режимов

`InterviewMode` =
`modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java`.
Восемь значений различаются не именем, а ПРЕДИКАТАМИ (`isRecallMode()`,
`usesTypedInput()`, `usesCodeEditor()`, `usesSpokenRehearsal()`, `isUnlimited()`
— `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:44-79`), и композицию каждого стерегут
`ModeWorkspaceContractTest` (302 строки) и `RecallCompositionContractTest`
(175 строк). Выбор режима — радио-карточки в
`modules/quiz-app/src/main/resources/templates/fragments/session-builder.html:212-229` (`th:each="m :
${sessionModes}"`), подписи и дефолтный счётчик вопросов приходят из
`EnumLabelSourceContractTest`-стерегомого источника, не дублируются в шаблоне
(см. «ЧТО ФУНДАМЕНТАЛЬНО»).

### TRAINING

`isUnlimited() = true` (`modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:79`) — единственный режим без
сессии и счётчика вопросов (`data-unlimited="true"`,
`modules/quiz-app/src/main/resources/templates/fragments/session-builder.html:216`). Композиция: вопрос → 4 MCQ-варианта
(`#interview-options`, `modules/quiz-app/src/main/resources/templates/focus-training.html:965-988`) → мгновенная проверка →
разбор. Снято `workspace-training-dark-1440x900.png`,
`workspace-training-dark-2560x1440.png`, `workspace-training-light-1440x900.png`.
Дефолт формы (`checked="checked"` без явного выбора) — тоже TRAINING
(`modules/quiz-app/src/main/resources/templates/fragments/session-builder.html:212`).

### EXAM

Фиксированный набор вопросов, ошибки идут в штраф (глиф `#mode-exam`,
`modules/quiz-app/src/main/resources/templates/fragments/icons.html:69`, единственный из восьми, чьё имя символа совпадает с именем
режима как есть). Снято `workspace-exam-dark-1440x900.png`. Через EXAM же
получен экран Session Summary (`POST /finish` после старта EXAM — TRAINING для
`/finish` не годится, т.к. `isUnlimited()` не создаёт сессию, см. риск
design.md).

### MARATHON

Большая серия вопросов подряд, тот же MCQ-интерфейс, что TRAINING/EXAM, но
глиф — `#mode-path` (`modules/quiz-app/src/main/resources/templates/fragments/icons.html:70`), а не `#mode-marathon` — единственный
случай, что символ называется по метафоре «путь», не по имени режима (см.
«ЧТО СЛУЧАЙНО»). Снято `workspace-marathon-dark-1440x900.png`.

### STUDY

Двухфазный: `studyLearnPhase` сначала показывает эталонный ответ
(`modules/quiz-app/src/main/resources/templates/focus-training.html:715-716`, `#browse-answer`), MCQ-форма скрыта условием
`!studyLearnPhase` (`:940`), проверка себя — вторым шагом через
`/study-confirm` (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java:223-224`).
`ModeWorkspaceContractTest.studyLearnPhaseDropsTheDuplicatedMaterials`
(`:269-270`) стережёт, что учебная фаза не дублирует материалы блока «О теме».
Снято `workspace-study-dark-1440x900.png` (учебная фаза — первый экран STUDY).

### FLASHCARD

`isRecallMode() = true`, `usesTypedInput() = false` — молчаливое вспоминание,
кнопка «Показать ответ» (`modules/quiz-app/src/main/resources/templates/focus-training.html:879`, `#flashcard-reveal`),
затем самооценка 0-5 (`/flashcard-grade`, `:239-247`). Ни textarea, ни
MCQ-вариантов — единственный режим без явного пользовательского ввода перед
раскрытием. Снято `workspace-flashcard-dark-1440x900.png` — виден счётчик
«Ждут повторения» и решётка прогресса банка, характерная только для recall-
семейства.

### TYPED_RECALL

`usesTypedInput() = true`, `usesCodeEditor() = false`,
`usesSpokenRehearsal() = false`. Textarea `#typed-answer`
(`modules/quiz-app/src/main/resources/templates/focus-training.html:863`) — печатный ответ перед раскрытием эталона;
`ModeWorkspaceContractTest.everyTypedModeHasItsOwnPlaceholder` (`:76-77`)
стережёт, что подсказка в textarea различается для TYPED_RECALL/
INTERVIEW/CODE_CHALLENGE, а не одна на все три. Снято
`workspace-typed_recall-dark-1440x900.png`.

### INTERVIEW

Единственный режим с `usesSpokenRehearsal() = true`
(`modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:66-68`) — тот же textarea-интерфейс, что TYPED_RECALL, но
семантика другая: поле — заметки к устному ответу, а не сам ответ.
`ModeWorkspaceContractTest.spokenRehearsalIsAModeProperty` (`:59-60`) стережёт,
что это свойство режима, а не строковое сравнение на стороне вызывающего кода
(явно описано в Javadoc `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:59-68` как урок: раньше INTERVIEW и
CODE_CHALLENGE отличались от печатного пересказа только подписью поля). Снято
`workspace-interview-dark-1440x900.png`.

### CODE_CHALLENGE

Единственный режим с `usesCodeEditor() = true` — моноширинный textarea +
чек-лист «Что стоит покрыть» (сигнатура/контракт, основной случай, границы,
сложность — видно на `workspace-code_challenge-dark-1440x900.png`, разметка чек-
листа стережётся `ModeWorkspaceContractTest.thePlanIsMadeOfRealCheckboxes`,
`:106-107`) и потолок размера поля ввода
(`ModeWorkspaceContractTest.theWritingSurfaceHasACeiling`, `:221-222`). Из всех
восьми — самая непохожая на MCQ-режимы композиция.

## ЧТО ЕСТЬ

Продукт — тренажёр по подготовке к техническим собеседованиям на Spring Boot +
Thymeleaf (SSR, progressive enhancement JS) + PostgreSQL. Живой банк на момент
аудита (изолированная recon-БД, тот же импорт, что и у пользователя):
**9818 вопросов в 319 темах** (`login-dark-1440x900.png` — карточка «Что в банке
вопросов»; топики группируются в 10 групп по `TopicCatalogService`, лог
`boot.log.txt`: «Topic catalog loaded: groups=10, rules=9,
explicitTopics=39»). Восемь режимов прохождения (карта выше), 18 экранов (карта
выше), персонализация — 3 оси доступности (тема/размер текста/движение) плюс
один «вкусовой» пресет экрана (`ScreenPresetContractTest`, см. ниже). Единый
каскад CSS собирается на сервере по `page`-гейту в один `<link>` на страницу
(`CssBundleService`), а не грузится 33 файлами, как было исторически (память
проекта, `css-bundle-manifest.txt`, шапка комментария). Direction — «графит и
чернила» FOLIO с `265f3630`/`82aa5406`: пиксель-арт снят, живая матрица
голденов 14×2 движка.

## ЧТО ХОРОШО

- **Манифест каскада — единственный источник правды**, а не 33 `<link>` в
  шапке: `css-bundle-manifest.txt` читают ОБА потребителя (`CssBundleService` и
  `TemplateFragmentContractTest`), и тест `cssBundleManifestCoversEveryProjectStylesheet`
  (`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/TemplateFragmentContractTest.java:1564-1565`) гарантирует, что новый CSS-файл
  не может остаться неподключённым молча.
- **0 реальных CSS-протечек.** Живой прогон `python3 scripts/css-leak-audit.py`
  в этой сессии — `syntax: OK (35 файлов)`, `carriers: OK (0 носителей без
  коробки)`; единственные ненулевые `leak` (components-answer.css=22,
  components-filter-bar.css=24, components-code-highlight.css=38) явно
  промаркированы аудитором как `OK usage` — намеренное кросс-страничное
  переиспользование, а не забытый селектор.
- **Единственный источник подписей режима.** Радио-карточки режима
  (`modules/quiz-app/src/main/resources/templates/fragments/session-builder.html:212-229`) не хранят своих текстов — подпись, глиф,
  дефолтный счётчик и текст CTA приходят из Java-модели (`EnumLabelSourceContractTest`),
  и комментарий в шаблоне (`:195-204`) явно называет исторический баг: было
  расхождение, пользователь видел имя enum-константы вместо подписи.
- **`InterviewMode` — предикаты, не строковые сравнения.** `isRecallMode()`,
  `usesTypedInput()`, `usesCodeEditor()`, `usesSpokenRehearsal()`,
  `isUnlimited()` (`modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:44-79`) — единое место ветвления
  поведения; Javadoc метода `usesSpokenRehearsal()` прямо документирует
  найденный и исправленный баг рассинхрона трёх копий одного условия.
- **`/answer` имеет настоящий no-JS путь**, а не фиктивный: снятый в этой
  сессии `result-dark-1440x900.png` получен с `javaScriptEnabled: false` —
  форма `#interview-form` (`modules/quiz-app/src/main/resources/templates/focus-training.html:940`) реально отправляется
  браузером и сервер реально рендерит `result.html` целиком, без единой строки
  клиентского JS.
- **Честные пустые состояния.** `summary-dark-1440x900.png` при нулевой
  истории показывает не пустой экран и не заглушку, а осмысленный текст
  «Сессия завершена без ответов. Засчитывать нечего...» — состояние
  спроектировано, а не забыто.
- **Ultrawide-раскладка для тяжёлых вопросов — измеренный, а не гипотетический
  фикс.** `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/UltrawideWorkspaceContractTest.java:12-38` документирует ДО/ПОСЛЕ
  замер на реальном Chromium (13/40 → 20/40 схем без горизонтальной прокрутки
  на 2560×1440) и три закреплённых инварианта. Слабое место того же
  механизма — в разделе «Мёртвое пространство».
- **Дизайн-система вкусовых осей осознанно свёрнута в один пресет.**
  `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/ScreenPresetContractTest.java:15-30` документирует находку (6 переключателей
  → 12 непроверяемых сочетаний) и решение (3 «вкусовые» оси → 1 пресет, 2 оси
  доступности остались отдельными).

## ЧТО ПЛОХО

- **Login недостижим в дефолтном режиме продакшена.** `LoginController`
  висит на `@ConditionalOnProperty(prefix = "app.security", name = "mode",
  havingValue = "multi")` (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java:19-20`), а дефолт —
  `app.security.mode=local` (`modules/quiz-app/src/main/resources/application.yml:143`). Живой замер этой сессии:
  `curl http://localhost:8091/login` при дефолтном `local` → `HTTP 404`
  (страница просто не существует как бин, не «требует входа» — 404, не 401/403).
  Чтобы снять `login-dark-1440x900.png`, пришлось перезапускать стенд с
  `APP_SECURITY_MODE=multi`. Экран №8 из 18 в §5 миссии физически недостижим
  без override окружения, которого нет ни в одном месте конфигурации по
  умолчанию.
- **`app.security.mode=multi` требует входа даже для GET-навигации**, хотя
  собственный Javadoc `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java:16` называет режим «Hybrid» и
  документация `modules/quiz-app/src/main/resources/application.yml:141` говорит «mutating endpoints require
  auth». Живой замер: `curl -D- http://localhost:8091/stats` в режиме `multi`
  → `302 Location: /login` — GET, не мутация. Комментарий и код расходятся
  ровно в описании того, что делает переключатель.
- **Дефолтный пресет экрана не учитывает реальный вьюпорт даже на первом
  визите.** `readScreenPreset()` (`modules/quiz-app/src/main/resources/templates/fragments/head.html:229-234`) без сохранённого
  `localStorage` всегда возвращает `laptop`, независимо от
  `window.innerWidth`. Три снятых в этой сессии ultrawide-кадра
  (`workspace-training-dark-2560x1440.png`, `stats-dark-2560x1440.png`,
  `topics-dark-2560x1440.png`) показывают дефолтную «ноутбучную» раскладку на
  2560px-мониторе: `mainWidthRatio` 0.675-0.700 от ширины вьюпорта, левый жёлоб
  495-527px (`data/space-metrics.json`) — раскладка не подстраивается, пока
  человек сам не откроет Настройки. Разбор чисел — в «Мёртвое пространство».
- **`/review` не пуст даже на почти свежей истории**, показывая при этом
  собственный текст «Ошибок к разбору 1» ещё до того, как пользователь успел
  осмысленно поработать с разделом (в этом прогоне — ровно один зафиксированный
  промах из recon-сценария `result-dark-1440x900.png`). Полноценно не
  проверено на большой истории (ограничение прохода 2 — раздел «Метод»), но
  сам факт, что `/review` не в списке 18 экранов §5, а является полноценным
  экраном с собственной композицией (`review-dark-1440x900.png`), — разрыв
  между картой миссии и картой кода.
- **Мёртвая площадь под Session Summary — не только ultrawide-эффект.** Даже
  на 1440×900 (`summary-dark-1440x900.png`) пустого пространства ниже блока
  «Ещё» и кнопок экспорта — примерно половина видимой высоты экрана: карточки
  итога занимают верхние ~470px из 900, при том что структура (заголовок,
  показатели, действия) уже закончена и ничего не подгружается лениво.

## ЧТО СЛУЧАЙНО

Решения без записанной причины: нет строки в `docs/css-decisions/`, нет
контракт-теста, нет объясняющего комментария в самом файле.

- **`components-signatures.css` и `utilities-core.css` — единственные два CSS
  файла из семи без досье решения, у которых И заголовок файла не объясняет
  «почему», а лишь называет происхождение** (`modules/quiz-app/src/main/resources/static/css/components-signatures.css:1`,
  `modules/quiz-app/src/main/resources/static/css/utilities-core.css:1` — оба дословно «Wave C split from base.css») — доля
  теста есть у первого (`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/ModalTitleContractTest.java:46-47`), у
  `utilities-core.css` содержательного теста нет вовсе (только косвенные
  упоминания `.hidden` в шести чужих тестах, ни один не про эту ось). Три
  правила утилит (`.flex-1`, `.overflow-x-auto`, `.w-60`/`.w-80`/`.min-w-140`)
  уже удалены по одному через iter-72/76/81 (комментарии
  `utilities-core.css:14,17,19-24`) без общей ревизии: файл живёт чередой
  точечных решений, а не одним обозримым.
- **Глифы режимов называются не по имени режима у двух из восьми.**
  `modules/quiz-app/src/main/resources/templates/fragments/icons.html:68` — символ CODE_CHALLENGE называется `id="mode-forge"`
  («кузница»), `modules/quiz-app/src/main/resources/templates/fragments/icons.html:70` — символ MARATHON называется `id="mode-path"`
  («путь»); остальные шесть совпадают буквально (`mode-training`,
  `mode-study`, `mode-flashcard`, `mode-typed`, `mode-interview`, `mode-exam`).
  Ни в `docs/css-decisions/`, ни в комментариях `icons.html` метафора не
  объяснена — имя символа для двух режимов пришлось искать отдельно от имени
  enum-константы.
- **`fonts-local.css` (203 строки) не в `css-bundle-manifest.txt`, но и не
  дублируется как `base.css`** — единственный CSS-файл проекта, который
  подключается собственным путём (font-preload, `FontPreloadContractTest`)
  параллельно с манифестом, а не через него и не мимо него демонстративно
  (`base.css`). Третий, промежуточный способ подключения CSS в проекте, где
  задокументированы явно только два (манифест / намеренно вне ленты).

## ЧТО ФУНДАМЕНТАЛЬНО

То, на чём держится каскад и код, и что редизайн обязан либо сохранить, либо
снести осознанно, а не молча.

- **`css-bundle-manifest.txt` + `CssBundleService` + `TemplateFragmentContractTest`
  — треугольник, который не даёт каскаду разойтись с кодом.** Любой новый CSS
  либо попадает в манифест (и тест это проверяет), либо явно исключается
  (`base.css`) — третьего не дано молча.
- **`@layer tokens, reset, shell, components, pages, utilities`
  (`modules/quiz-app/src/main/resources/static/css/tokens.css:4`) — контракт порядка каскада.** Специфичность внутри слоя
  решает порядок исходника (задокументировано построчно в самом манифесте) —
  редизайн, меняющий эту декларацию, обязан пересчитать все страничные файлы,
  объявляющие больше одного слоя (`pages-focus.css`, `shell.css`,
  `pages-stats.css` — манифест, комментарий выше строки `focus /css/pages-focus.css`).
- **`ArchUnit` (`LayeredArchitectureTest`) держит границы `domain` / `service`
  / `persistence` / `api`** — фронтенд-редизайн этого слоя не касается
  напрямую, но 74 UI-контракт-теста стоят на той же дисциплине: поведение
  режима одно и то же место (`InterviewMode`-предикаты), а не рассыпано по
  Java/шаблону/JS тремя копиями.
- **74 контракт-теста — не документация, а работающий регрессионный барьер.**
  Каждый несёт в Javadoc «найденный дефект, а не гипотезу» с числами до/после
  (`UltrawideWorkspaceContractTest`, `ScreenPresetContractTest` — оба выше) —
  редизайн, ломающий тест, обязан либо не ломать, либо переписать тест
  ОСОЗНАННО как часть задачи, а не обойти.
- **Восемь режимов — предикаты одного enum, не восемь путей кода.** Все
  различия воркспейса выводятся из пяти булевых методов
  (`modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java:44-79`) — редизайн композиции применяется к предикату
  (например, «все recall-режимы»), а не к каждому имени режима по отдельности.

## ЧТО УСТАРЕЛО

- **`docs/css-decisions/pixel-*.md` (8 файлов) описывают снесённый слой** —
  `docs/css-decisions/pixel-animations.md:1`, `pixel-brand.md`,
  `pixel-buttons.md`, `pixel-cards.md`, `pixel-fx.md`, `pixel-motion-parity.md`,
  `pixel-nav-badges.md`, `pixel-options.md` — все восемь документируют
  pixel-art решения, снятые коммитом `b56bf4c6` («design(folio): новое
  направление — графит и чернила вместо гильдейского pixel-art», 2026-08-29).
  Файлов `static/css/pixel-*.css` в дереве уже нет (проверено — `ls
  modules/quiz-app/src/main/resources/static/css/pixel-*.css` не находит
  ничего), но досье остались.
- **`.sdlc/CS-20260828-003/report.md:1` (весь документ, 41 КБ + 50 PNG) сделан
  ДО FOLIO-сброса.** Последний коммит той задачи — `798c9a66`, примерно на
  18 часов раньше `b56bf4c6`; все её скриншоты и весь `pixel-layer-scan.json`
  показывают несуществующий дизайн. Метод (`recon/shots.js`) переиспользован
  в этом аудите, выводы — нет.
- **`static/css/base.css` (17 строк) — сирота манифеста.** Файл
  `@import`-ирует 12 модулей (`reset.css`, `utilities-core.css`,
  `reset-layout.css`, четыре `pages-*.css`, `components-widgets.css`,
  `print.css`, `components-signatures.css`, `utilities-overrides.css`) —
  подмножество того, что и так собирает манифест по каждой странице отдельно.
  Комментарий `modules/quiz-app/src/main/resources/static/css/base.css:1-2` называет себя «aggregator (Wave C)», а
  `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/TemplateFragmentContractTest.java:1562-1563` подтверждает: «dev-агрегатор
  через `@import`, в ленту не едет намеренно». Но НИ ОДИН `<link
  href="/css/base.css">` в репозитории не найден (проверено `grep -rn
  'base\.css' modules/quiz-app/src/main` — только комментарии-упоминания, ни
  одной активной ссылки) — «намеренно не в ленте» на практике неотличимо от
  «никем не используется».
- **`design/mockups/feedback-state-atlas.html:66` цитирует несуществующую
  сегодня строку 1275 файла `static/css/base.css` как источник правды**,
  хотя текущий `modules/quiz-app/src/main/resources/static/css/base.css` —
  17 строк (проверено `wc -l`). Атлас описывает состояние ДО «Wave C»
  расщепления файла на модули — сам номер строки доказывает, что мокап не
  обновлялся с тех пор, как `base.css` перестал быть монолитом.

## ЧТО ПЕРЕУСЛОЖНЕНО

- **`pages-focus.css` — 2205 строк, крупнейший CSS-файл проекта** (20% от
  всех 10 755 строк CSS в одном файле). Собственный контракт-тест
  `UltrawideWorkspaceContractTest` документирует, что раскладка правилась как
  минимум в три прохода (замер «до», замер «после», три инварианта, история
  регрессии с порогом 2240 — Javadoc `:23-38`) — сложность не гипотетическая,
  а видна по числу самодокументированных итераций внутри файла.
- **`modules/quiz-app/src/main/resources/static/css/shell.css:1` — 1084 строки
  на AppShell** (sidebar/topbar/mobile-nav), второй по размеру CSS файл
  проекта, при этом гейт манифеста `*` (грузится на каждой странице). Любая
  правка навигации в редизайне трогает файл, который грузится всегда — велика
  цена ошибки в одном месте.
- **`modules/quiz-app/src/main/resources/static/css/utilities-core.css:14-24`
  несёт больше истории удалений, чем действующих правил.** Из 25 строк файла
  три развёрнутых комментария документируют, ПОЧЕМУ три утилиты
  `.flex-1`/`.overflow-x-auto`/
  `.w-60`,`.w-80`,`.min-w-140` были удалены (единственный носитель — конкретная
  таблица на `/stats`), а не общее правило, когда добавлять утилиту в этот
  файл. Стоимость чтения файла (что тут есть) выше пользы, которую несут
  оставшиеся 4 действующих класса.
- **Восемь режимов = восемь наборов E2E/контракт-веток поверх одного enum.**
  `ModeWorkspaceContractTest` (302 строки) + `RecallCompositionContractTest`
  (175 строк) = 477 строк тестов ради 8 предикатных веток — оправдано историей
  найденных багов (расхождение трёх копий условия, см. «ЧТО ХОРОШО»), но это
  реальная цена: добавление девятого режима означает правку кода, шаблона И
  обоих тестовых файлов согласованно.

## ЧЕГО НЕ ХВАТАЕТ

- **Голдены покрывают 4 экрана из 18 и 0 режимов из 8.** 14 снимков
  `e2e/visual-baselines/chromium` (`ls e2e/visual-baselines/chromium | wc -l`)
  стерегутся `e2e/visual-matrix.spec.js`, это `home`(7) + `train`(2) +
  `settings`(2) + `stats`(3) — см. раздел «Тестовое покрытие». Ни один из
  восьми `InterviewMode`-воркспейсов, ни `bookmarks`, ни `topics`, ни `login`,
  ни `error`, ни `result`, ни `session-summary`, ни `review` пиксельно не
  застрахован.
- **Ultrawide не проверяется голденами вовсе.** `e2e/visual-matrix.spec.js`
  (14 записей `MATRIX`, проверено `grep -c "id: '"`) не содержит ни одного
  вьюпорта шире 1440px в основной матрице — §17-регрессия (жёлоб 495-527px на
  2560px, измерено этой сессией) может появиться и исчезнуть незамеченной.
- **Нет golden-покрытия recall-семейства (FLASHCARD/TYPED_RECALL/INTERVIEW/
  CODE_CHALLENGE).** Композиция каждого стережётся `RecallCompositionContractTest`
  (структура DOM), но не пиксельно — регресс в CSS этих четырёх режимов не
  поймает ни один существующий визуальный тест.
- **Нет автоопределения широкого монитора для НОВОГО пользователя.**
  `ScreenPresetContractTest` осознанно защищает дефолт `laptop` ради
  СУЩЕСТВУЮЩИХ пользователей (см. «ЧТО ХОРОШО») — но для человека, который
  открывает продукт впервые на 2560px-мониторе, обоснования сохранять
  «ноутбучный» вид уже нет: решение одно на обе категории пользователей, хотя
  причина решения относится только к одной.
- **Login не участвует ни в одном автотесте продукта** (ни контракт-тест, ни
  e2e-спек не упомянут `LoginController`/`login.html` — проверено `grep -rl
  'LoginController\|login.html' modules/quiz-app/src/test` не даёт ничего
  в `ui/*ContractTest.java`) — притом что это экран №8 из 18 в самой миссии
  редизайна; живое доказательство, что экран вообще существует и как
  выглядит, — только `login-dark-1440x900.png` из этого прохода.
- **`/review` не имеет ни контракт-теста, ни строки в §5 миссии**, при том что
  композиция страницы (собственный заголовок «Ошибок к разбору N», собственная
  панель «Вся тренировка») — не копия Focus, а самостоятельный вариант
  (`review-dark-1440x900.png`).

## Тестовое покрытие и его дыры

74 UI-контракт-теста группируются по тому, что стерегут (по имени файла,
`find modules/quiz-app/src/test -name '*ContractTest.java'`). Список
исчерпывающий (не «примеры») — сумма колонки «Кол-во» равна 74 без остатка.

| Группа | Тесты | Кол-во |
|---|---|---|
| Композиция режима/recall | `ModeWorkspaceContractTest`, `RecallCompositionContractTest`, `RevealFocusContractTest`, `StudyReadingContractTest` | 4 |
| Раскладка/ширина/масштаб | `PageWidthScaleContractTest`, `UltrawideWorkspaceContractTest`, `FocusWideCompositionContractTest`, `SummaryWideCompositionContractTest`, `FocusBandMeasureContractTest`, `ResultBandMeasureContractTest`, `HeadingScaleContractTest` | 7 |
| Персонализация/доступность | `ScreenPresetContractTest`, `MobileContextContractTest`, `HotkeyRegionContractTest`, `KeyboardE2eContractTest` | 4 |
| Компоненты (вкладки/таблицы/формы/пустые состояния) | `TabComponentContractTest`, `TabPanelContractTest`, `DataTableComponentContractTest`, `FormControlComponentContractTest`, `EmptyStateSystemContractTest`, `PendingStateSystemContractTest`, `ButtonRankComponentContractTest` | 7 |
| Иконки/языки/подписи | `IconLanguageContractTest`, `SurfaceLanguageContractTest`, `EnumLabelSourceContractTest`, `DailyGoalCopyContractTest` | 4 |
| Прогрессия/геймификация | `ProgressionContractTest`, `RankLadderContractTest`, `RankEmblemContractTest`, `AchievementContractTest`, `StateIllustrationContractTest` | 5 |
| Страничные композиции | `ErrorPageCompositionContractTest`, `ReviewEmptyStateContractTest`, `TopicBriefContractTest`, `TopicMapContractTest`, `CatalogColumnsContractTest`, `SummaryRoutingContractTest`, `SummarySealContractTest`, `SummaryTopContractTest` | 8 |
| Каркас/навигация/шапка | `ShellChromeContractTest`, `NavigationSourceContractTest`, `PageHeadUnificationContractTest`, `PageTitleContractTest`, `SessionBuilderLaunchReachContractTest`, `SessionBuilderStepsContractTest`, `BuilderRailContractTest`, `TemplateFragmentContractTest` | 8 |
| Печать/CSP/шрифты/диаграммы | `PrintContractTest`, `PrintPaletteContractTest`, `DiagramCspContractTest`, `DiagramLegibilityContractTest`, `FontPreloadContractTest` | 5 |
| Импорт/сидеры (вне `ui/`, каталог `service/imports/`) | `McqVisibleLinkageContractTest`, `SeedSchemaContractTest` | 2 |
| Прочее (закладки/заметки/история/сессия/визуальный голден) | `AnswerPageReachContractTest`, `AnsweredOrderContractTest`, `BookmarkTouchTargetContractTest`, `BookmarksFilterContractTest`, `CssProseBudgetContractTest`, `DifficultyBadgeContractTest`, `DomIdUniquenessContractTest`, `FocusHandoffContractTest`, `FocusPrimaryActionReachContractTest`, `MaterialCapContractTest`, `ModalTitleContractTest`, `QuestionNoteContractTest`, `ReportCategorySourceContractTest`, `SessionHistoryContractTest`, `SessionLogContractTest`, `SettingsPreviewContractTest`, `StatsSummaryContractTest`, `TemplateExpressionContractTest`, `TemplateResidueContractTest`, `VisualBaselineContractTest` | 20 |

7 Playwright-спеков (`e2e/*.spec.js`): `chart-lazy.spec.js` (ленивая загрузка
графиков), `console-clean.spec.js` (нет ошибок в консоли), `font-network.spec.js`
(нет лишних сетевых запросов шрифтов), `keyboard-quiz.spec.js` (клавиатурный
флоу), `reduced-motion.spec.js` (уважение `prefers-reduced-motion`),
`reflow.spec.js` (нет горизонтального скролла на узких вьюпортах),
`visual-matrix.spec.js` (визуальные голдены — `grep -c "id: '"` даёт 14 записей).

### Не покрыто голденами

`e2e/visual-baselines` — 14 снимков (×2 движка = 28) ровно четырёх экранов:
`home` (7 записей матрицы, разные theme×preset×motion×viewport), `train` (2),
`settings` (2), `stats` (3). Всё остальное — без пиксельной защиты:

1. Bookmarks (`/bookmarks`)
2. Topics (`/topics`)
3. Session Summary (`/session-summary`)
4. Login (`/login`) — усугубляется тем, что маршрут вообще недостижим по
   умолчанию (раздел «ЧТО ПЛОХО»)
5. Error (страница 404/500)
6. Result (`/answer`, no-JS и JS-путь)
7. Review (`/review`)
8. Workspace TRAINING (кроме как через Home-матрицу, отдельно не снят)
9. Workspace EXAM
10. Workspace MARATHON
11. Workspace STUDY
12. Workspace FLASHCARD
13. Workspace TYPED_RECALL
14. Workspace INTERVIEW
15. Workspace CODE_CHALLENGE
16. Любой вьюпорт шире 1440px (ultrawide-геометрия не в матрице вовсе)

## Мёртвое пространство (ultrawide)

Метрики из `.sdlc/CS-20260829-044/data/space-metrics.json` (замер этой сессии,
живой Chromium, `#main-content` DOM-геометрия, дефолтный пресет `laptop` — см.
оговорку в «Метод и ограничения»):

| Экран | Кадр | `mainWidthRatio` | `usefulWidthRatio` | Крупнейший жёлоб |
|---|---|---|---|---|
| Workspace TRAINING | `workspace-training-dark-2560x1440.png` | 0.675 | 0.613 | `left-of-main` 527px |
| Analytics | `stats-dark-2560x1440.png` | 0.700 | 0.637 | `left-of-main` 495px |
| Session Summary | `summary-dark-2560x1440.png` | 0.700 | 0.637 | `left-of-main` 495px |
| Topics | `topics-dark-2560x1440.png` | 0.700 | 0.637 | `left-of-main` 495px |

Чтение чисел: на вьюпорте 2560px `#main-content` занимает лишь 67.5-70.0% его
ширины, а самый широкий видимый блок внутри — 61.3-63.7%. Во всех четырёх
кадрах наибольший непрерывный пустой промежуток — слева от рабочей области
(495-527px, почти вдвое больше `--sidebar-width` в 236px,
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/UltrawideWorkspaceContractTest.java:44`) — то есть жёлоб не объясняется одной
только шириной сайдбара, контент дополнительно центрирован в оставшемся поле.

Это не противоречит `UltrawideWorkspaceContractTest` — тот стережёт другой
сценарий (вопрос с широким листингом кода/схемой ≥970px, при
`[min-width: 2240px]` без привязки к `data-reading-width`, судя по
`pages-focus.css:1551,1654,1771`), и снятые здесь кадры (Training landing без
кода, Analytics, Summary, Topics) в этот сценарий не попадают — они показывают
дефолтную ширину без расширения, потому что расширять нечего (нет широкого
листинга) ИЛИ страница не участвует в механизме расширения вовсе (`stats`,
`topics`, `summary` — не `focus`-страницы). Практический вывод: на ultrawide
БЕЗ активного «широкого» листинга у пользователя пустует 30-33% ширины экрана
на каждой из четырёх проверенных страниц — ровно та жалоба, которую §17
миссии называет явно.

## Итог для следующего шага

Три вещи стоит унести в гипотезу дизайна (`docs/redesign/02-design-hypothesis.md`)
без повторного исследования.

Во-первых, продукт зрелый, а не запущенный: 74 контракт-теста ловят реальные
прошлые баги (не гипотетические), манифест каскада не даёт CSS расползтись,
восемь режимов различаются предикатами одного enum, а не копипастой. Редизайн
имеет смысл строить НА этом фундаменте, а не рядом с ним — попытка снести
`InterviewMode`-предикаты или манифест каскада ради визуального языка потеряет
дисциплину, которую 74 теста удерживали месяцами.

Во-вторых, самая измеримая и самая незащищённая тестами дыра — геометрия
широкого экрана. Дефолтный пресет `laptop` держится по правильной причине
(не ломать вид существующим пользователям), но именно ЭТА причина не
распространяется на первый визит нового человека на ultrawide-мониторе:
здесь нет конфликта интересов, который решение `ScreenPresetContractTest`
пытается разрешить, — только неучтённый случай. Первая же гипотеза дизайна
обязана предложить: либо автоопределение для ПЕРВОГО визита (когда
`localStorage` пуст в принципе, не только `screenPreset`), либо честное
расширение `@media (min-width: 2240px)`-контракта с фокус-страниц
(`pages-focus.css`) на страницы без листинга (`stats`, `topics`, `summary`),
где сейчас пустует 30-33% ширины без какой-либо компенсации.

В-третьих, визуальная защита (голдены) сильно отстаёт от того, что реально
показывает продукт: 4 экрана и 0 режимов из 26 реально существующих
комбинаций. Редизайн, который трогает `pages-focus.css` (2205 строк, самый
рискованный файл проекта) без предварительного расширения
`e2e/visual-matrix.spec.js` хотя бы на восемь workspace-состояний, будет
опираться на контракт-тесты структуры DOM, но не увидит визуальную
регрессию глазами до продакшена — тем же способом, каким она была бы
поймана раньше на любой из четырёх уже защищённых страниц.
