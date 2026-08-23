# components-answer.css — досье решений (ARCH-02)

Полные доводы и история правил, вынесенные из комментариев `components-answer.css`
(REF-009): эссе читают при ПЕРЕСМОТРЕ решения, а не при каждом чтении файла.
У правила в CSS остаётся указатель с тезисом; якоря ниже — цель указателей.
Рабочие инварианты («НЕ менять», пороги) остаются в CSS.

<a id="components-answer-e01"></a>
## components-answer-e01 · components-answer.css:1

**Контекст:** `@layer components {`

@layer components — общая поверхность разбора ответа (post-answer feedback-flow).
   Вынесено из pages-result.css при подготовке page-scope result (Волна 3 CSS-архитектуры):
   этот кластер рендерится на ДВУХ экранах — JS `#result-feedback` на .focus-page
   (quiz/interview.js после ответа) и no-JS result.html на .result-page. Пока жил в page-файле
   result → при page-scope result пропал бы на focus. Селекторы уникальны (не
   дублируются в pages-focus) → перенос из @layer pages в @layer components безопасен:
   page-scoped overrides (.result-feedback…/.explanation-panel…) более специфичны и
   продолжают выигрывать; equal-spec одноимённых пар между файлами нет.
   Вердикт-баннер (.result-correct/.result-wrong) — отдельный кластер в pages-focus,
   выносится следующим шагом (D2b).

<a id="components-answer-e02"></a>
## components-answer-e02 · components-answer.css:13

**Контекст:** `html[data-design] .feedback-meta {`

===========================================================================
   Пост-разбор ответа: вердикт → варианты → «Почему это так?» → разбор неверных
   → «Подробнее» → мета (XP, следующий повтор).
   Общий scope: JS #result-feedback (.focus-page) и no-JS result.html.

   Порядок задаёт DOM, а не CSS. Раньше здесь стоял `.feedback-flow` c
   `display:flex` и шестью `order: 1..6` — и все шесть значений в точности
   повторяли порядок узлов в разметке. Механика, которая ничего не
   переставляла, но обязывала оба рендерера строить лишнюю обёртку и держать
   её содержимое в согласованном порядке под страхом невидимой перетасовки.
   Вместе с ней убраны классы отдельного блока «Правильный ответ: C. …»
   (`.feedback-correct-answer`, `-line`, `.feedback-option-letter`,
   `.feedback-block-eyebrow`): сам блок удалён — букву называет вердикт, а
   вариант в списке несёт зелёную рамку и чип. `.feedback-key-difference` и
   `.feedback-explanation` были двумя именами одной панели разбора (лид и
   остальное), теперь это один `.explanation-panel`.
   ===========================================================================

<a id="components-answer-e03"></a>
## components-answer-e03 · components-answer.css:49

**Контекст:** `старого индиго-well. Действует и на разбор на result.html (C3) → общая, из`

Разбор вариантов был здесь дважды, двумя поколениями компонентов.
   Удалены оба старых:

   — `.result-detail-disclosure/-summary/-body` — своя рамка раскрытия «Подробнее»
     с mono-заголовком и ▸/▾-маркером. Сейчас и SSR (`result.html`), и AJAX
     (`interview.js`) рисуют обычный `<details class="explanation-panel
     additional-panel">` c `.explanation-eyebrow` в summary — одна отбивка
     раскрытий на оба пути вместо второй, похожей, но чуть иной.

   — `.option-explanation` + `.explanation-correct/.explanation-wrong` +
     `.option-breakdown*` — пояснение, приклеенное к каждому варианту, и его
     «разбор по буквам» внутри «Подробнее». Разбор неверных вариантов давно
     оформлен отдельной панелью `.distractor-panel` (строки «B — неверно. …»,
     одинаковые в обоих путях), а варианты после ответа несут только марку
     состояния. Разметку с этими классами не строит ни один шаблон, ни JS, ни
     сервер — правила пережили свой компонент, включая комментарии про порядок
     каскада между двумя мёртвыми группами. Вместе с ними убран
     `enhanceOptionExplanationDisclosure` (`content/dynamic-content.js`), который
     на каждой странице искал `.option-explanation`, чтобы завернуть их в
     мобильные disclosure.

<a id="components-answer-e04"></a>
## components-answer-e04 · components-answer.css:83

**Контекст:** `html[data-design] .answer h2 { font-size: clamp(1.625rem, 1.3rem + 1.4vw, var(--font-size-2xl)); }`

Шкала заголовков-секций ВНУТРИ тела разбора (.answer). Базовый reset даёт
   фикс. h2=2.25rem / h3=1.75rem / h4=1.375rem. Заголовок-ВОПРОС
   (.focus-question) при этом clamp'ится вниз и на узком экране упирается в пол
   1.75rem — ровно размер базового h3 → секции разбора («Форматы», «Чеклист…»)
   читались как ⩾ вопрос: на мобильном (замер 390px) h1-вопрос=28.08px, а h3
   тела ответа=28px → визуальная иерархия «вопрос → его разбор» схлопывалась
   («разбор важнее вопроса», WCAG 2.4.10 / 1.3.1 контекст). Даём телу ответа
   собственную FLUID-шкалу: на десктопе она упирается в те же reset-значения
   (десктоп-вид не меняется — зрелый экран не трогаем), а на мобильном плавно
   уменьшается, оставляя секции явно подчинёнными вопросу. Порядок h2>h3>h4
   сохранён на всех ширинах. @layer components НИЖЕ reset в каскаде слоёв
   (tokens.css: tokens,reset,shell,components,…) → перебивает базовые h*-размеры
   без !important. Действует на всех трёх поверхностях разбора (flashcard-browse,
   JS #result-feedback, no-JS result.html — общий контейнер .answer).

<a id="components-answer-e05"></a>
## components-answer-e05 · components-answer.css:176

**Контекст:** `html[data-design] .option-correct { border-color: var(--color-status-success) !important; background: var(--color-status`

Пост-ответная разметка MCQ-вариантов (quiz/interview.js вешает .option-correct/-wrong на
   выбранную/правильную опцию; рендерится и на no-JS result.html) — статус-рамка +
   wash + цветной буквенный маркер ::before. Общая поверхность → из pages-focus
   (D3d Волны 3). Все декларации !important: должны бить дефолтную рамку опции
   независимо от пиксель-декора. Каскад-безопасно даже при инверсии слоёв для
   !important (@components раньше @pages → приоритет РАСТЁТ): конкуренты pixel-fx
   `#interview-options label.option-correct` и utilities `:has(input:checked)`
   трогают ТОЛЬКО box-shadow/animation/::after — не border-color/background/::before,
   те свойства база задаёт единолично. option-dimmed (focus-only) не тронут.
