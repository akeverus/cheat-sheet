# Turnkey-пакет для session-окна (R0.135, 2026-07-14)

> **Назначение.** Автономная статическая работа по фронту исчерпана (§10 backlog
> закрыт R0.132, §9 Contract/Tests закрыты R0.134). Весь оставшийся путь к
> конечной цели требует **либо решения пользователя** (4 пункта ниже, Часть A),
> **либо живого приложения** (parity-QA, Часть B). Этот файл — единственный
> turnkey-чеклист: подними `docker compose up -d postgres` + `./gradlew bootRun`,
> открой http://localhost:8080 и пройди по нему. Никакого переоткрытия плана.
>
> Источник правды по статусам — `PLAN_FRONTEND.md` §7/§9/§10/§21. Здесь — только
> актуальный на R0.135 срез «что осталось» с рекомендациями.

---

## Часть A — Decision-brief: 4 открытых решения

> **✅ РЕШЕНО 2026-07-14 (§21 R0.142):** пользователь принял ВСЕ 4 — все
> рекомендованные «оставить как есть»: A1 favorite=result-only, A2 chart.js=CDN,
> A3 ICO-3=reuse #i-flag, A4 IC-5=WONTFIX (юникод). Все §10 → 🚫 DECISION_LOCK.
> Ниже — исходный brief (сохранён как обоснование).

Все четыре — **не баги**, а выборы. Статика по ним доведена до предела; дальше
нужен твой выбор. Порядок — от «стоит взглянуть» к «можно игнорировать».

### A1. Favorite-флоу (RES-3 / APP-5) — где доступно «В избранное»

- **Сейчас (verified R0.135):** кнопка-звезда `#btn-favorite` есть **только на
  `result.html`** (после ответа): `<svg #i-star>`, `aria-pressed`, `aria-label`
  тоггл «Добавить/Убрать из избранного», POST `/api/favorite` (живой контроллер
  `InterviewApiController:124`). Фильтр «Избранные» есть в `/stats` (checkbox
  `importantOnly`) и упоминается в `/settings`. То есть избранное **работает** —
  вопрос лишь в точках входа.
- **Открытый выбор:** давать ли отметить «в избранное» **во время ответа**
  (focus-страница, до result), а не только постфактум на result.
  - **Вариант 1 — оставить как есть (result-only).** 0 работы. Логика «оценил
    вопрос → решил сохранить» естественна постфактум.
  - **Вариант 2 — добавить звезду на focus-страницу** (в `.question-meta`/зону
    головы вопроса). +1 кнопка в `focus-training.html`, переиспользует
    `applyFavoriteButtonState`/делегат из `app.js:687` (уже слушает
    `.btn-favorite` глобально — доп. JS почти не нужен).
- **Рекомендация:** **Вариант 1** (оставить). Избранное функционально полно;
  дублировать кнопку на focus — маргинальная ценность, +поверхность для багов.
  Открыть, только если по факту не хватает «сохранить, не отвечая».
- **Трудоёмкость Варианта 2:** S (1 кнопка + гейт `current != null`).

### A2. chart.js — self-host vs CDN (HEAD-7)

- **Сейчас (verified R0.135):** `chart.js` грузится с **jsdelivr CDN** с SRI
  (`integrity="sha512-…"` + `crossorigin="anonymous"`, `head.html:335`).
  Аналогично `highlight.js` (cdnjs). Есть guard: если CDN недоступен — график
  не рисуется, но страница жива (`typeof Chart` проверка).
- **Открытый выбор:**
  - **Вариант 1 — оставить CDN.** 0 работы. Минус: зависимость от внешнего CDN
    (корп-proxy/geo/offline → нет графиков, но fallback-таблицы `/stats` есть).
  - **Вариант 2 — self-host.** Скачать `chart.js`/`highlight.js` в
    `static/vendor/`, поправить `<link>/<script>` в `head.html`, снять SRI/CDN.
    Плюс: offline-устойчивость, нет утечки IP на CDN, CSP строже. Минус:
    **требует скачивания файла** (это твоё действие — permission-gated), +вес
    в репо, ручное обновление версий.
- **Рекомендация:** **Вариант 1** для dev; **Вариант 2** — если целишься в
  offline/корп-развёртывание или строгий CSP. Решение архитектурное, не
  срочное. Fallback-таблицы уже защищают от «CDN лёг».
- **Трудоёмкость Варианта 2:** M (скачать 2 файла + правка head.html + снять
  dns-prefetch/SRI + смоук графиков).

### A3. ICO-3 — отдельная warning-иконка

- **Сейчас (verified R0.135):** danger/warning-контексты переиспользуют
  `#i-flag` (double-duty). Отдельного warning-символа нет.
- **Открытый выбор:** добавить distinct warning-иконку в спрайт
  (`fragments/icons.html`) или оставить reuse `#i-flag`.
- **Рекомендация:** **оставить reuse** (осознанный выбор р110). Отдельная иконка
  = декоративный opt-polish; браться только заодно с IC-4 (warning-токен, Фаза G)
  и правкой мест использования. Низкий приоритет.
- **Трудоёмкость:** S, но связана с IC-4/warning-токеном (тогда — вместе).

### A4. IC-5 — «→» в CTA: юникод vs Lucide-иконка (ПОНИЖЕН R0.135)

- **Пересмотр R0.135 (ground-truth):** премиса «SR-шум» **устарела** — **все
  реальные CTA-стрелки «→» уже обёрнуты в `<span aria-hidden="true">`**
  (result:102, session-summary:44-45, stats:120/139, focus:130, error:52,
  today-widget:46-47). SR их не читает. Остальные «→» в шаблонах — в
  комментариях/коде, не в UI. Link-purpose (WCAG 2.4.4) уже CLEAN (р117).
- **Что реально осталось:** чисто **визуальная конвенция** — оставить юникод «→»
  или заменить на Lucide-иконку. Блокер: icon-кнопки ВЕДУТ иконкой (icon-lead,
  R34); directional «next→» семантически ТРЕЙЛИТ → иконизация создаст
  mixed lead/trail в одной кнопке.
- **Рекомендация:** **WONTFIX / оставить.** aria-hidden юникод-стрелка —
  корректна и консистентна; иконизация ухудшит (mixed lead/trail). a11y-выигрыша
  нет (уже aria-hidden). Приоритет ~0.

**Итог Части A:** ни один пункт не блокирует «готовность» — все либо
рекомендованы «оставить как есть» (A1/A3/A4), либо архитектурно-опциональны
(A2). Если согласен с рекомендациями — фронт можно считать функционально
завершённым, останется только parity-QA (Часть B).

---

## Часть B — Turnkey parity-QA чеклист (нужен живой app)

**Что это закрывает:** §7 Parity ◐ и Final ◐/⏸; §9 Final ⏸ (result/
session-summary/post-answer). Contract/Tests уже ✅ статически (R0.133/134) —
здесь только визуально-поведенческая parity прод-рендера против макетов +
живые a11y-снапшоты.

### B0. Предусловия
1. `docker compose up -d postgres` (postgres на :5432, не :5433 — см.
   [[project_local_run_gotchas]]; для bootRun задать `SPRING_DATASOURCE_URL`).
2. `./gradlew bootRun` (НЕ запускать доп. gradle-сборку при живом devtools —
   wedge-риск). Дождаться :8080.
3. **Не** запускать при этом окне параллельную gradle-задачу.
4. chrome-devtools MCP: вьюпорты **375 / 768 / 1280 / 1440 / 1920 / 2560**,
   **обе темы** (light+dark), несколько дизайнов (editorial дефолт + 1-2 иных).

### B1. FOCUS (`focus-training.html`) — Parity ◐ → цель ✅
- **State-family:** flashcard; MCQ (default/selected/correct/wrong); explanation;
  session-progress; empty-ветки (вне сессии / сессия без вопроса / завершена);
  **long/code-heavy варианты** (FT-15!); STUDY learn-фаза (кнопка «Проверить себя»).
- **Как дойти:** `/` (TRAINING без сессии); `/training?mode=FLASHCARD`;
  `/training?mode=STUDY`; сессия — `/settings` → выбрать режим → старт.
- **FT-15 проверка (long-options):** взять вопрос с длинными вариантами → submit
  «Проверить ответ» не должен уезжать за пределы экрана некомфортно; оценить,
  нужен ли sticky (сейчас «собранный футер», не sticky). **Решение sticky —
  дизайнерское, только по факту наблюдения.**
- **Метрики:** скрин vs `design/mockups/…/focus-question.html`; computed:
  `.focus-question` font-size (3xl доминирует), масштаб зоны разбора; 0
  горизонтального overflow на 375.

### B2. RESULT (`result.html`) — Parity ◐, Final ⏸ → цель ✅
- **State-family:** correct; wrong; пояснения; **related («Похожие вопросы»)**;
  extra-controls; no-JS fallback (`result.html` = живой POST-фоллбэк /answer).
- **Как дойти:** ответить в TRAINING (POST /answer — мутация, **с твоего ведома**);
  no-JS — отключить JS и submit.
- **RES-15 a11y-снапшот (related-list):** после ответа с related → в DOM
  `#result-related-questions` → h3 (вне списка) + `div[role=list]` → N ×
  `a[role=listitem]`; a11y-снапшот должен читать «список, N элементов».
- **extra-analysis (focus-флоу):** кнопка «Похожие вопросы» появляется ТОЛЬКО
  при наличии related (не hardMode); клик раскрывает список без сети,
  `aria-expanded=true`, кнопка прячется. Проверить визуально раскрытие.
- **Метрики:** favorite-звезда (A1) — `aria-pressed` тоггл по клику; скрин vs
  макет; обе темы.

### B3. SUMMARY (`session-summary.html`) — Parity ◐, Final ⏸ → цель ✅
- **State-family:** score; mistakes; recommendations; empty; **share; print**;
  long table.
- **Как дойти:** завершить сессию (EXAM/MARATHON/STUDY) → POST /finish (в теле
  `_csrf`) → /session-summary. **Одноразовая** — не reload-ить (см.
  [[project_session_modes_summary_flow]]).
- **STA-20 / share-print (перенесено из stats + summary):**
  - share: кнопка «Скопировать итог» → `buildShareText` собирает из DOM
    (`.summary-table tbody tr` по позициям td c[0]/c[2]/c[1]/c[4]) → проверить,
    что после любых правок таблицы **порядок колонок не сломал share-текст**.
  - print: `window.print()` + `@media print` прячет nav → чистый лист (PDF).
  - **stats-таблица** (STA-20): /stats без колонки «Сброшено»; sort по всем
    колонкам работает (Зрелость data-col=4 после R0.128); share/print /stats.
- **Метрики:** скрытый `h2` счёта в H-навигации; таблица role=table в Safari/VO;
  скрин vs макет; 375/768 responsive card-view таблицы.

### B4. Остальные поверхности (§7 Parity ✅, Final ◐) — подтвердить прод-parity
SETTINGS / STATS / ERROR / SHELL / OVERLAYS / FEEDBACK / COMPONENTS — их атласы
уже QA'нуты на file:// (R0.105-108). Нужен только прод-снап vs атлас: по одному
скрину на поверхность в обеих темах, 2-3 вьюпорта, 0 overflow. OVERLAYS —
живьём scrim/focus-trap/Esc на `#kbd-help-overlay`/`#reset-confirm`/`#mobile-menu`.

### B5. После QA — что флипнуть в PLAN_FRONTEND.md
- §7: Parity ◐→✅ для FOCUS/RESULT/SUMMARY; Final ◐/⏸→✅ по поверхностям, где
  всё зелёное.
- §9: Final ⏸→✅ для result/session-summary/post-answer-controls.
- §21: R0.NN-запись с доказательствами (скрины, метрики, коммит).
- EXAM-решение пользователя (если всплывёт по ходу) — отдельно.

---

## Что НЕ входит (вне frontend-scope / не трогать)
- Бэкенд-поведение `applyStudyConfirm`, `/api/*` контроллеры — не чинить.
- Параллельная MCQ-сессия (`seed/mcq/**`, `docs/mcq-quality/**`,
  `PLAN_INTERVIEW.md`) — не трогать.
- `mermaid-init` (MER-1) — внешний owner.
- Намеренные дизайн-решения (BAS-18 measure, focus 48px h2, dual-path SUM-3,
  streak без aria-live и т.д.) — не «чинить».
