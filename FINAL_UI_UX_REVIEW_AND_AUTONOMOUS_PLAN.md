# Финальное UI/UX-ревью и автономный план завершения frontend-работ

**Проект:** quiz-app, тренажёр подготовки к собеседованиям  
**Стек:** Spring Boot + Thymeleaf + vanilla CSS/JS  
**Назначение документа:** передать другой LLM единый закрывающий список работ, после выполнения которого текущий frontend-loop обязан завершиться, а не перейти к очередному бесконечному ревью.

> Этот документ содержит только новые замечания и системные выводы. Ранее зафиксированные пункты про неправильный ответ в примере, состояние выбранного варианта до проверки, расположение мобильной CTA, обрезание кода, чрезмерный размер заголовка и пустоты на широких экранах здесь не дублируются, кроме случаев, когда они указывают на противоречие между документацией и фактической вёрсткой.

---

## 1. Итоговый вердикт

Визуальная база проекта уже сильная: есть узнаваемый редакционный характер, ограниченная цветовая система, аккуратные состояния, светлая и тёмная темы, продуманная типографика и серьёзная работа по accessibility.

Проблема сейчас не в отсутствии усилий, а в устройстве самого процесса:

1. План одновременно является спецификацией, журналом, backlog, evidence-хранилищем и историей сотен тиков.
2. В разных разделах зафиксированы противоречащие друг другу правила.
3. Статус `VERIFIED` ставится до окончания deep-pass и иногда до устранения зарегистрированных дефектов.
4. Текущий prompt не содержит жёсткого автомата переходов и поэтому LLM останавливается с вопросом «куда двигаться дальше».
5. Maintenance включён в тот же бесконечный loop, поэтому формальной точки остановки не существует.
6. Часть дизайн-документов расходится с показанной вёрсткой: документация требует полноширинную структуру с полезным правым рельсом, а представленные широкие экраны выглядят как узкая центральная колонка с большим количеством пустого пространства.

**Следующий раунд нельзя начинать как очередной reset и нельзя продолжать как бесконечный audit.** Нужно провести один конечный `FINALIZATION ROUND`: нормализовать источники правды, закрыть ограниченный список продуктовых и технических задач, выполнить release-gates и остановить loop.

---

# 2. Ошибки и противоречия текущего PLAN/PROMPT

## PROC-01. Бесконечный loop против требования завершить работу

В плане режим прямо назван бесконечным, а после `VERIFIED` запускается maintenance/deep-pass. Это делает остановку логически невозможной.

### Исправление

Разделить процессы:

- `FINALIZATION LOOP` — конечный, закрывает текущий проект и завершается;
- `MAINTENANCE LOOP` — отдельный, не запускается автоматически и создаётся только по новой команде пользователя.

Финальный статус текущего раунда:

```text
FINALIZED
→ создать итоговый отчёт
→ отключить текущий loop
→ не выбирать новую задачу
```

---

## PROC-02. Разная каденция

В prompt указано `/loop 15m`, в operating contract — 10 минут. Из-за этого непонятно, какое правило авторитетно.

### Исправление

Оставить одну каденцию во всех файлах. Если фактический cron уже настроен на 10 минут, prompt должен начинаться с:

```text
/loop 10m
```

---

## PROC-03. «Ровно одна единица» против «несколько мелких единиц»

Prompt требует ровно одну атомарную единицу, а план разрешает несколько мелких задач за тик. Это приводит к чрезмерно мелким итерациям и искусственному растягиванию работы.

### Исправление

Единое правило:

```text
Один тик = один связный work package.
Work package может включать несколько тесно связанных изменений,
если они имеют одну причину, единый acceptance и один атомарный commit.
```

Нельзя брать второй независимый экран, но можно одновременно исправить template + CSS + JS + backend contract + test одного flow.

---

## PROC-04. «Чинить сразу» против «сначала весь аудит, потом волна фиксов»

В документе одновременно зафиксированы обе политики. Результат — дефекты могут быть найдены, поверхность объявлена `VERIFIED`, а исправление отложено.

### Исправление

Для finalization-round использовать end-to-end политику:

```text
найти → подтвердить → исправить → проверить → закрыть
```

Отдельная глобальная волна фиксов допустима только для общего системного дефекта, который затрагивает несколько поверхностей, например design tokens или общий header.

---

## PROC-05. `VERIFIED` не является финальным статусом

Сейчас после `VERIFIED` начинается adversarial deep-pass, который может найти новые дефекты. Значит прежний `VERIFIED` семантически является лишь предварительным pass.

### Исправление статусов

```text
AUDITED
IMPLEMENTED
REGRESSION_PASS
DEEP_PASS
RELEASE_CANDIDATE
FINALIZED
```

`FINALIZED` разрешён только после deep-pass. Старые `FINAL ✅ VERIFIED` трактовать как `BASELINE_VERIFIED`, а не как доказательство финальной готовности.

---

## PROC-06. 10 и 11 design variants

В одних местах требуется проверить 10 дизайнов, в других фактически существует 11. Это ломает матрицу покрытия и позволяет формально закрыть не весь набор.

### Исправление

Считать число вариантов динамически из production-конфигурации/DOM, а не хранить числом в тексте:

```text
expectedDesigns = список доступных значений в settings/runtime
```

Любое расхождение между registry, settings и CSS — blocking defect.

---

## PROC-07. DESIGN.md и текущая широкая вёрстка расходятся

DESIGN.md требует:

- edge-to-edge shell;
- отсутствие мёртвых боковых полей;
- app-shell с правым полезным рельсом на широких экранах.

На представленных 1728–2560 px экранах основной контент остаётся очень узким и окружён большими пустотами. Это либо устаревшие скриншоты, либо фактическое нарушение дизайн-контракта. При обоих вариантах существующий статус parity недостоверен.

### Исправление

В начале finalization-round сделать один authoritative comparison:

1. текущий production screenshot;
2. текущий mockup;
3. DESIGN.md;
4. принятое пользовательское решение о topbar/rail.

После этого выбрать и зафиксировать **одну** модель wide-layout:

- **рекомендуется:** содержательный split-layout на широких экранах: код/основной контент слева, ответы или мета/сессия справа;
- prose measure ограничивается внутри зоны, но сама структура использует доступную ширину.

Старые документы, требующие другую модель, архивировать или обновить.

---

## PROC-08. Sticky-header описан одновременно как обязательный, реализованный и отвергнутый

CRITIQUE, DESIGN и поздние решения расходятся. Так как prompt заставляет читать все документы, модель получает конфликтующие команды.

### Исправление

Создать `design/mockups/DECISIONS.md`, перенести туда окончательное решение и заменить противоречащие пункты в старых документах ссылкой:

```text
Decision HDR-001: header is static / sticky / contextual
Status: FINAL
Reason:
Evidence:
```

Исторический CRITIQUE больше не должен быть нормативным источником.

---

## PROC-09. Prompt запрещает backend, но итоговая задача разрешает backend

Текущая граница «не менять API/DB» не позволяет реализовать часть нормального UX: resume session, idempotency, точные рекомендации, issue reporting и детерминированные QA fixtures.

### Исправление

Разрешить целевые backend-изменения при выполнении всех условий:

- изменение напрямую закрывает UX-flow;
- API/модель данных минимальны;
- есть миграция и rollback;
- сохранена обратная совместимость либо обновлены все consumers;
- добавлены targeted tests;
- не затрагивается MCQ content pipeline без необходимости.

---

## PROC-10. Запрещены destructive live-операции, но результат и summary требуют submit/finish

Фактические QA-записи уже показывают мутации статистики. Запрет нельзя выполнить и одновременно полноценно проверить flow.

### Исправление

Создать безопасный QA-контур:

- отдельный test user или test profile;
- deterministic seed/fixture только для dev/test;
- транзакционный reset после сценария;
- idempotent cleanup command;
- production data никогда не используется.

После этого destructive flow разрешён только внутри тестового контура.

---

## PROC-11. Огромный PLAN читается полностью каждый тик

`PLAN_FRONTEND` содержит несколько тысяч строк и сотни исторических записей. Полное чтение каждые 10 минут:

- тратит контекст;
- повышает риск ориентироваться на устаревший пункт;
- провоцирует повторные аудиты уже закрытого;
- затрудняет выбор следующей задачи.

### Исправление структуры

Разделить документы:

```text
PLAN_FRONTEND.md              — ≤300 строк, правила и фазы
FRONTEND_STATE.json           — текущее машинное состояние
FRONTEND_BACKLOG.md           — только открытые задачи
FRONTEND_DECISIONS.md         — принятые решения
FRONTEND_EVIDENCE.md          — ссылки на доказательства
FRONTEND_HISTORY.md           — append-only история старых тиков
```

На каждом тике читать полностью только первые четыре небольших файла. Историю читать только по ссылке из конкретной задачи.

---

## PROC-12. Дублирующиеся источники состояния

Одни и те же статусы хранятся в:

- таблице поверхностей;
- таблице production-файлов;
- registry JSON;
- pixel matrix;
- progress log;
- historical backlog;
- текстовых заметках тиков.

Из-за этого уже появились stale-строки, которые код давно исправил.

### Исправление

`FRONTEND_STATE.json` становится единственным источником статуса. Markdown-таблицы генерируются или обновляются из него, но не считаются авторитетными.

Минимальная схема задачи:

```json
{
  "id": "UX-001",
  "title": "...",
  "severity": "P1",
  "surface": ["FOCUS"],
  "status": "OPEN",
  "phase": "IMPLEMENT",
  "dependsOn": [],
  "files": [],
  "acceptance": [],
  "evidence": [],
  "decision": null,
  "attempts": 0
}
```

---

## PROC-13. Pixel parity ≤1 CSS px слишком жёсток и одновременно недостаточно осмыслен

Разница в rasterization шрифтов, browser scaling и DPR может дать больше 1 px без UX-дефекта. При этом идеальный 1 px diff не доказывает корректность сценария.

### Исправление

Разделить проверки:

- структура/DOM/порядок/состояния — строго;
- overflow/clipping/overlap — строго 0;
- ключевые геометрические anchors — допуск 1–2 CSS px;
- raster diff — небольшой процент с маской только доказанно динамических областей;
- любое крупное изменённое пятно требует объяснения.

---

## PROC-14. Chromium-only нельзя называть полноценным cross-browser

Если доступен только Chromium, это допустимое ограничение инструментария, но не доказательство универсальной совместимости.

### Исправление

Либо:

- добавить существующие Firefox/WebKit runners, если они уже доступны без смены package manager;

либо честно указать в final-report:

```text
Supported/verified browser: Chromium family.
Other engines: static compatibility review only.
```

---

## PROC-15. Performance «справочно» не соответствует идеальному финалу

Функционально чистый экран может быть медленным из-за шрифтов, Chart.js, CSS и больших таблиц.

### Исправление

Ввести release-бюджеты в production-like режиме:

- LCP ≤ 2.5 s;
- CLS ≤ 0.1;
- INP ≤ 200 ms;
- отсутствие long tasks > 200 ms при обычном flow;
- отсутствие необязательных render-blocking ресурсов;
- JS/CSS/font payload не увеличивается больше согласованного baseline без объяснения;
- таблица аналитики не должна блокировать main thread при сотнях строк.

Dev `bootRun` можно исключить из perf-оценки, но тогда обязан существовать отдельный production-like замер.

---

## PROC-16. Ручной `?v=N` cache-busting хрупок

Уже зарегистрирован drift версий assets. Ручное обновление нескольких consumers неизбежно снова сломается.

### Исправление

Перейти на автоматическое fingerprinting/versioning через Spring resource chain или build hash. Если это невозможно в текущем раунде, добавить автоматический test, который проверяет единообразную версию всех consumers.

---

## PROC-17. Runtime-copy в `build/resources/main` создаёт второй источник файлов

Ручное копирование удобно для live QA, но повышает риск проверить не тот код.

### Исправление

Предпочтение:

1. dev-profile с чтением source resources;
2. controlled restart;
3. только в крайнем случае copy с обязательной проверкой hash source == runtime и последующей очисткой.

---

## PROC-18. LLM может бесконечно создавать «verified-clean» тики

Последние записи плана показывают длинную серию отдельных WCAG-проверок без существенных изменений. Это полезно как один аудит, но в формате бесконечных тиков превращается в топтание на месте.

### Исправление anti-loop правил

- не более одного audit-only тика подряд;
- следующий тик обязан либо исправить найденное, либо закрыть целый audit-lane;
- нельзя повторять проверку без нового кода, нового viewport, нового state или нового evidence;
- одна WCAG-группа проверяется пакетом, а не по одному success criterion на десятки тиков;
- после feature-freeze новые P3/P4 идеи уходят в future backlog и не открывают текущий loop.

---

# 3. Новые UI/UX-замечания по представленным экранам

## UX-01. Вторичная кнопка визуально сильнее основной на empty/done экранах

На мобильных экранах вторичная кнопка местами шире основной. Пользователь визуально считывает размер раньше заливки, поэтому иерархия действия размывается.

### Улучшение

- одинаковая ширина обеих кнопок в стеке;
- primary всегда первая;
- secondary без более крупной hit-area;
- на desktop обе кнопки могут быть рядом, но primary не меньше secondary.

---

## UX-02. Done и empty выглядят как отдельный мини-сайт, а не продолжение тренировочного flow

У активного вопроса плотная рабочая композиция, а terminal states резко переходят в большую центрированную hero-композицию. Смена визуального режима сильнее, чем смена состояния.

### Улучшение

Сохранить контекст сессии:

- тот же content shell и gutter;
- компактный итоговый блок внутри рабочей сцены;
- рядом или ниже — следующий шаг;
- header и ритм отступов не должны «перезагружать» продукт визуально.

---

## UX-03. Primary CTA использует выраженную градацию/блик

Кнопка в light-теме выглядит почти градиентной, хотя дизайн-язык заявлен как restrained и одноцветный signal.

### Улучшение

Использовать сплошную заливку; глубину передавать минимальной тенью/pressed-state, а не декоративным градиентом.

---

## UX-04. Completion-state не показывает качество результата как единый понятный вывод

`20 вопросов · 16 верно · серия 5 дней` требует самостоятельно вычислять результат.

### Улучшение

Добавить один компактный summary:

```text
80% · хороший результат
4 вопроса требуют разбора
```

Без большого dashboard. Основной CTA — «Разобрать 4 ошибки».

---

## UX-05. Статистика серии нуждается в корректной русской плюрализации

Нужно гарантировать:

```text
1 день
2 дня
5 дней
11 дней
21 день
```

Не собирать фразу на frontend конкатенацией. Использовать backend formatter или общий i18n helper.

---

## UX-06. Empty-state не показывает активные ограничения

Пользователь видит, что ничего не найдено, но не видит, какие именно фильтры это вызвали.

### Улучшение

Показывать чипы активных условий:

```text
Spring · Сложные · Повторение · 20 вопросов
```

И конкретное действие рядом с конфликтующим фильтром.

---

## UX-07. Нет отдельного действия «Не знаю»

Сейчас пользователь вынужден либо угадывать, либо выбирать заведомо неправильный вариант. Для интервального обучения это разные сигналы.

### Улучшение

Добавить нейтральное действие `Не знаю`:

- не помечать как выбранный вариант;
- считать отдельным learning outcome;
- сразу показывать объяснение;
- отправлять в ближайшее повторение;
- отображать в аналитике отдельно от ошибочного уверенного ответа.

Требуется backend-состояние ответа `SKIPPED/UNKNOWN`.

---

## UX-08. Пользователь не понимает, почему именно этот вопрос появился

В adaptive/review режимах это важно для доверия к системе.

### Улучшение

Добавить короткий контекст:

```text
К повторению сегодня
Слабая тема: Spring Proxy
Новый вопрос
```

Не показывать длинное объяснение алгоритма, только причину выбора.

---

## UX-09. Нет видимого подтверждения автосохранения сессии

Пользователь может опасаться потерять прогресс при закрытии вкладки или переходе в настройки.

### Улучшение

Ненавязчивое состояние:

```text
Сохранено
Сохраняем…
Не удалось сохранить · Повторить
```

Показывать только при изменении/ошибке, не держать постоянный шум.

---

## UX-10. Нужен полноценный pause/resume flow

`Завершить сессию` звучит как необратимое завершение. Для коротких подходов лучше иметь `Пауза`.

### Улучшение

Backend хранит активную сессию и текущую позицию. На главной/в настройках показывается:

```text
Продолжить тренировку · 7 из 20
```

Отдельное окончательное действие остаётся в confirm-dialog.

---

## UX-11. Нет защиты от двойной отправки и повторного POST

При медленной сети пользователь может нажать CTA повторно или обновить result page.

### Улучшение

- pending-state кнопки;
- блокировка повторного submit;
- idempotency key на backend;
- PRG для server-rendered flow;
- безопасное восстановление после refresh/back;
- понятная recoverable error.

---

## UX-12. После ответа пользователь должен оставаться в устойчивой точке чтения

При появлении длинного разбора layout может заметно сдвинуться, а фокус уйти далеко вниз.

### Улучшение

- зарезервировать место под verdict;
- перевести фокус на короткий заголовок результата без резкого scroll-jump;
- подробный разбор раскрывать ниже;
- CTA «Следующий вопрос» держать после основного объяснения, но доступным через shortcut.

---

## UX-13. Разбор ответа должен различать «минимум» и «глубже»

Технические объяснения могут быть очень длинными. Одинаково показывать всё сразу — когнитивно тяжело.

### Улучшение

Первый слой:

1. вердикт;
2. одна ключевая причина;
3. правильная модель.

Второй слой, раскрываемый по желанию:

- почему другие варианты неверны;
- edge cases;
- связанная тема;
- практический пример.

---

## UX-14. Нужна связь ошибки с конкретным misconception

Просто «неверно» плохо помогает обучению.

### Улучшение

Backend/content contract может возвращать `misconceptionTag`, например:

```text
Путаница: self-invocation и Spring proxy
```

В summary группировать ошибки по таким концепциям, а не только по темам.

---

## UX-15. Итог сессии должен формировать следующий короткий план

Вместо двух общих кнопок система должна предлагать следующий лучший шаг:

```text
Разобрать 4 ошибки
Повторить Spring Proxy: 5 вопросов
Продолжить завтра: 8 карточек
```

Рекомендации должны быть вычислены backend, а не захардкожены в шаблоне.

---

## UX-16. Аналитика должна отвечать «что делать», а не только «что произошло»

Таблицы и графики полезны, но пользователь приходит не смотреть метрики, а готовиться.

### Улучшение

Вверху аналитики один блок `Следующее действие`:

- слабейшая тема;
- просроченные повторения;
- наиболее уверенная ошибка;
- CTA на готовую тренировку.

---

## UX-17. Сортировка большой таблицы должна сохраняться

Если пользователь сортирует 300+ тем, переходит к тренировке и возвращается, сброс состояния раздражает.

### Улучшение

Сохранять фильтр, поиск, sort и collapsed/expanded state в URL или session storage. URL предпочтительнее для share/back.

---

## UX-18. Графики должны иметь текстовый эквивалент

Canvas-график сам по себе мало полезен screen reader и плохо работает при печати.

### Улучшение

- рядом краткий текстовый вывод;
- доступная таблица данных или disclosure;
- печать использует таблицу/summary, а не canvas;
- цвет не единственный носитель серии.

---

## UX-19. Настройки перегружены количеством визуальных осей

11 дизайнов + тема + layout + width + density + motion + font scale дают большую свободу, но создают decision fatigue.

### Улучшение

Основной уровень:

```text
Системный
Для чтения
Компактный
```

Расширенные настройки — отдельный disclosure `Тонкая настройка`. Все существующие оси сохраняются, но не конкурируют с запуском тренировки.

---

## UX-20. Нужна команда восстановления визуальных настроек без удаления учебных данных

Danger-zone может восприниматься как слишком рискованная.

### Улучшение

Разделить:

- `Сбросить оформление`;
- `Сбросить фильтры`;
- `Удалить учебные данные`.

Каждое действие имеет собственное объяснение и confirm только там, где есть потеря данных.

---

## UX-21. Нужен встроенный report-flow качества вопроса

Для технической базы критично быстро находить неверные и устаревшие вопросы.

### Улучшение

После ответа или в меню вопроса:

```text
Сообщить о проблеме
- неверный правильный ответ
- неоднозначная формулировка
- устаревшая версия технологии
- ошибка в коде
- опечатка
```

Backend сохраняет questionId, версию, выбранный вариант и категорию, но не требует длинной формы.

---

## UX-22. Следует показывать версию/актуальность технологического контента

Для Spring/Java вопрос может зависеть от версии.

### Улучшение

Если у вопроса есть version scope, показывать ненавязчиво:

```text
Spring Framework 6.x
Проверено: июль 2026
```

Если scope отсутствует — ничего не показывать. Это уменьшает споры о «правильном» ответе.

---

## UX-23. Нужен onboarding горячих клавиш без навязчивой модалки

Справка по `?` полезна, но пользователь может о ней не узнать.

### Улучшение

Один раз после первого keyboard interaction показать компактный inline hint. Не открывать модалку автоматически. Сохранять `dismissed` локально.

---

## UX-24. Mobile header должен сообщать текущий контекст даже при скрытой навигации

В compact header пользователь видит бренд и иконки, но не всегда понимает, где находится.

### Улучшение

Короткий contextual label:

```text
Фокус · 7/20
Итоги
Аналитика
```

Он не обязан быть отдельным большим заголовком.

---

## UX-25. Нужна единая система skeleton/pending, а не декоративные демо-состояния

Атласы уже показывают loading states, но идеальный продукт должен использовать их только там, где реально есть задержка.

### Улучшение

Для каждого async action определить:

- latency threshold, после которого показывается pending;
- что остаётся интерактивным;
- можно ли отменить;
- retry;
- aria-live сообщение;
- поведение при offline.

---

# 4. Ограниченный финальный продуктовый scope

Чтобы новые идеи не превратились в бесконечный feature creep, в текущий finalization-round включить только следующие функции.

## MUST — блокируют финал

1. **Pause/resume активной сессии.**
2. **Idempotent submit + pending/error recovery.**
3. **`Не знаю` как отдельный outcome.**
4. **Actionable session summary: разбор ошибок и следующий шаг.**
5. **Question issue reporting.**
6. **Детерминированный QA profile/fixtures.**
7. **Автоматический или проверяемый cache-busting.**
8. **Устранение конфликтов документации и реальной wide-layout модели.**

## SHOULD — выполнить, если не требует архитектурного переворота

1. Причина показа вопроса.
2. Autosave status.
3. Группировка ошибок по misconception.
4. Persistence фильтров аналитики.
5. Текстовые эквиваленты графиков.
6. Упрощённые presets настроек.

## FUTURE — не держат текущий loop открытым

- полноценный offline/PWA;
- social/share expansion;
- новый content generation pipeline;
- сложная adaptive ML-модель;
- новые дизайн-варианты;
- геймификация сверх текущей серии.

---

# 5. Детальный конечный план работ

## Фаза 0. Остановить текущий дрейф и зафиксировать scope

### Задачи

1. Приостановить активный frontend-loop на один тик.
2. Создать feature-freeze для текущего finalization-round.
3. Перенести старые логи в `FRONTEND_HISTORY.md`.
4. Создать:
   - `FRONTEND_STATE.json`;
   - `FRONTEND_BACKLOG.md`;
   - `FRONTEND_DECISIONS.md`;
   - `FRONTEND_EVIDENCE.md`.
5. Сократить `PLAN_FRONTEND.md` до правил, фаз и release-gates.
6. Переписать `PROMPT_PLAN_FRONTEND.md` по автономному контракту из раздела 7.
7. Зафиксировать единственное количество design variants из runtime.
8. Зафиксировать единственное решение по header и wide-layout.

### Acceptance

- нет конфликтующих активных инструкций;
- нет двух источников текущего статуса;
- prompt не предлагает пользователю выбор направления;
- список MUST/SHOULD/FUTURE зафиксирован;
- следующий task определяется машинно.

---

## Фаза 1. Ground-truth inventory и reconciliation

### Задачи

1. Повторно обнаружить route/template/fragment/JS/backend endpoints.
2. Сравнить production с registry.
3. Удалить stale registry entries.
4. Проверить все реальные reachable states, а не только mockup atlas.
5. Сопоставить каждый screenshot с commit/hash/assets version.
6. Проверить, соответствуют ли показанные широкие экраны текущему production.
7. Сверить current decisions с DESIGN/CRITIQUE/PORT_MAPPING.
8. Все конфликты превратить в конкретные issue, а не новые вопросы пользователю.

### Acceptance

- orphan route/template/state = 0;
- stale `DONE/OPEN/BLOCKED` = 0;
- каждый state имеет deterministic reproduction;
- у каждого issue есть severity, owner, acceptance и dependency.

---

## Фаза 2. Финализация дизайн-системы

### Задачи

1. Зафиксировать wide-layout:
   - flow для узких экранов;
   - split/grid только при достаточной ширине и контенте;
   - prose measure внутри зоны;
   - отсутствие мёртвого рельса.
2. Унифицировать terminal states с активным shell.
3. Исправить иерархию primary/secondary CTA.
4. Убрать декоративный gradient primary button.
5. Утвердить semantic tokens для:
   - selection;
   - success;
   - error;
   - warning;
   - focus;
   - pending.
6. Упростить border hierarchy.
7. Нормализовать typography metrics и русскую плюрализацию.
8. Утвердить настройки preset-first + advanced disclosure.
9. Обновить mockups всех затронутых state-family.

### Acceptance

- DESIGN.md, mockup и production не противоречат друг другу;
- любой компонент имеет один canonical visual contract;
- нет локальных magic colors/radii/spacing без documented reason;
- light/dark и все runtime designs не ломают semantic states.

---

## Фаза 3. Core training flow

### Frontend

1. Добавить `Не знаю`.
2. Добавить reason chip `Почему этот вопрос`.
3. Добавить autosave/pending/error status.
4. Сделать pause action вместо визуально финального выхода.
5. Стабилизировать post-answer layout и focus movement.
6. Разделить краткое объяснение и deep explanation.
7. Добавить question report entry.
8. Сохранить keyboard flow и no-JS fallback.
9. Проверить long code/long answers/small viewport/zoom 200%.

### Backend

1. Расширить answer outcome: `CORRECT`, `WRONG`, `UNKNOWN`.
2. Ввести idempotency token для ответа.
3. Хранить active session cursor/state.
4. Добавить pause/resume API или MVC flow.
5. Возвращать question selection reason.
6. Возвращать concise explanation + optional extended sections.
7. Добавить report endpoint с rate limit/validation.
8. Обновить SM-2 обработку `UNKNOWN`.

### Tests

- controller/service tests;
- duplicate submit;
- back/refresh;
- paused session restore;
- unknown outcome scheduling;
- CSRF;
- no-JS POST;
- browser keyboard/focus.

### Acceptance

- прогресс не теряется;
- повторный submit не удваивает статистику;
- `Не знаю` не маскируется под неправильный выбор;
- пользователь всегда видит следующий шаг или recovery;
- report сохраняет контекст вопроса;
- primary flow работает с JS и без JS.

---

## Фаза 4. Result и explanation flow

### Задачи

1. Унифицировать inline result и standalone no-JS result.
2. Выделить concise verdict block.
3. Показать misconception tag при наличии.
4. Свернуть вторичные объяснения в disclosure.
5. Добавить действия:
   - повторить позже;
   - в избранное;
   - сообщить о проблеме;
   - следующий вопрос.
6. Не перегружать экран одновременными CTA.
7. Проверить correct/wrong/unknown states.
8. Проверить extremely long markdown/code/table/diagram.

### Acceptance

- один и тот же смысл и порядок информации во всех render paths;
- correct/wrong/unknown кодируются текстом, иконкой и семантикой;
- фокус не теряется;
- related content не вытесняет основной разбор;
- обновление страницы не повторяет мутацию.

---

## Фаза 5. Session summary и adaptive next action

### Backend

1. Рассчитать:
   - score;
   - unknown count;
   - misconception groups;
   - weak topics;
   - due reviews;
   - recommended next session.
2. Сформировать next-action DTO, а не вычислять текст в шаблоне.
3. Добавить корректную русскую плюрализацию.

### Frontend

1. Главный вывод результата.
2. Основной CTA по реальной ситуации.
3. Ошибки группируются по причине/концепции.
4. Secondary actions не конкурируют с разбором.
5. Print/share используют те же данные.
6. Empty/zero/partial/error states.

### Acceptance

- пользователь за 5 секунд понимает результат и следующий шаг;
- CTA не общий, а контекстный;
- share/print не ломаются при новых колонках;
- summary корректен при 0, 1 и большом количестве ошибок.

---

## Фаза 6. Analytics

### Задачи

1. Добавить `Следующее действие` над метриками.
2. Сохранить search/filter/sort/collapse в URL/session state.
3. Добавить misconception/unknown dimensions.
4. Обеспечить текстовый эквивалент графиков.
5. Проверить 0/1/12/300+/5000 rows.
6. При необходимости внедрить server pagination или virtualisation без потери accessibility.
7. Убрать source-map/network noise Chart.js.
8. Self-host assets, если CDN создаёт CSP/offline/console проблемы.

### Acceptance

- analytics приводит к тренировке за один CTA;
- фильтры переживают back/forward;
- main thread не блокируется большой таблицей;
- canvas не является единственным источником данных;
- console/network clean.

---

## Фаза 7. Settings и data management

### Задачи

1. Preset-first UI.
2. Advanced customization disclosure.
3. Отдельные reset actions.
4. Ясное описание последствий export/reset/delete.
5. Saved feedback после изменения.
6. Server/client persistence contracts.
7. Проверить no-JS отображение и graceful degradation.
8. Проверить длинные локализованные подписи.

### Acceptance

- запуск тренировки остаётся главным job;
- пользователь не обязан понимать 11 design variants;
- destructive action невозможно вызвать случайно;
- визуальный reset не удаляет progress.

---

## Фаза 8. Global resilience и error states

### Задачи

1. Реальные 400/401/403/404/409/422/429/500/503.
2. Offline/reconnect state для async действий.
3. Session expired/CSRF recovery без потери выбранного ответа.
4. Permission denied только там, где реально возможно.
5. Retry с idempotency.
6. Защита от stale tab/multiple tabs.
7. Error disclosure без чувствительных данных.

### Acceptance

- каждое recoverable состояние имеет retry;
- ясно, сохранены ли данные;
- duplicate mutation = 0;
- stacktrace/PII в UI = 0;
- пользователь не попадает в тупик.

---

## Фаза 9. Accessibility consolidation

Не продолжать по одному WCAG-критерию на тик. Выполнить пакетами.

### Пакет A — structure/input

- semantics;
- headings/landmarks;
- labels;
- name/role/value;
- relationships;
- no redundant/conflicting ARIA.

### Пакет B — keyboard/focus

- full tab flow;
- shortcuts;
- dialogs;
- focus return;
- focus not obscured;
- 200/400% zoom;
- target size.

### Пакет C — perception

- text/non-text contrast;
- use of color;
- text spacing;
- forced colors;
- reduced motion;
- chart alternatives.

### Пакет D — dynamic states

- live regions;
- pending;
- errors;
- async completion;
- route changes;
- no-JS.

### Acceptance

- automated a11y = 100 для reachable pages;
- manual blockers = 0;
- keyboard-only complete flow;
- screen-reader smoke for core flow;
- unresolved Level A/AA = 0.

---

## Фаза 10. Performance и delivery

### Задачи

1. Production-like build/profile.
2. Automated asset fingerprinting.
3. Audit fonts and third-party assets.
4. Preload only critical fonts.
5. Remove unused CSS/selectors/icons.
6. Split stats-only JS from core if ещё не разделено.
7. Delay Chart.js until charts enter viewport or tab.
8. Measure DOM size and long tasks.
9. Prevent layout shifts from fonts, verdict and charts.
10. Cache headers and compression.

### Acceptance

- CWV budgets пройдены;
- no unexpected layout shift;
- no stale assets after deploy;
- console/network clean;
- no runtime-copy drift;
- no dead assets/selectors above agreed threshold.

---

## Фаза 11. Финальная QA-матрица

### Coverage strategy

Не запускать бессмысленный полный декартов продукт всех параметров. Использовать:

1. **Core matrix** для Instrument/default design:
   - все routes/states;
   - light/dark/system;
   - 320/375/430/768/1024/1280/1440/1920/2560;
   - DPR 1/2;
   - zoom 100/200/400 для ключевых страниц.
2. **Pairwise matrix** для остальных designs:
   - каждый design минимум на mobile + desktop;
   - обе resolved themes хотя бы один раз;
   - semantic states correct/wrong/error/focus.
3. **Shared-component regression** после каждой общей правки.
4. **Production-like smoke** без dev-only fixtures.

### Обязательные сценарии

- start → answer correct → next;
- answer wrong → deep explanation;
- unknown;
- pause → resume;
- finish → summary;
- no-JS answer;
- back/refresh/double-submit;
- report question;
- empty filters;
- session expired;
- analytics sort/filter/back;
- reset appearance;
- 404/500/retry;
- keyboard-only;
- reduced motion;
- print/share.

### Acceptance

- P0/P1/P2 open = 0;
- P3 open = 0 для user-visible defects;
- only documented future enhancements remain;
- unexplained visual diffs = 0;
- flaky scenarios = 0;
- dirty runtime/source files = 0.

---

## Фаза 12. Cleanup и release candidate

### Задачи

1. Удалить mockup-only/demo code из production.
2. Удалить stale selectors/listeners/fragments/assets.
3. Проверить all consumers shared fragments.
4. Запустить полный доступный test suite в отдельном процессе после остановки live bootRun.
5. Перезапустить production-like instance с чистого checkout/build.
6. Повторить smoke без ручных копирований.
7. Создать `FINAL_FRONTEND_REPORT.md`.
8. Проставить `RELEASE_CANDIDATE`.

### Acceptance

- clean checkout воспроизводит UI;
- full tests green;
- migrations/rollback проверены;
- все evidence ссылаются на текущий commit;
- нет ручного шага, известного только из истории чата.

---

## Фаза 13. Final gate и остановка

### Loop обязан проверить

```text
Scope frozen: yes
MUST features complete: yes
Routes/states inventory complete: yes
Open blocking issues: 0
Open user-visible defects P0-P3: 0
Accessibility blockers: 0
Overflow/clipping/overlap: 0
Unexplained visual diffs: 0
Backend contract tests: green
Frontend/JS/template tests: green
Full available suite: green
Production-like smoke: green
Performance budgets: pass
Console/network errors: 0
Orphan/dead code: 0
Dirty files: 0
Current commit recorded in evidence: yes
```

Если всё выполнено:

1. поставить `FINALIZED`;
2. создать итоговый commit;
3. записать оставшиеся FUTURE идеи отдельно;
4. вывести финальный отчёт;
5. **остановить текущий loop**;
6. не переходить в maintenance автоматически;
7. не проводить ещё один random review.

---

# 6. Приоритетный backlog для старта finalization-round

| ID | Приоритет | Задача | Scope |
|---|---:|---|---|
| PROC-01 | P0 | Сделать loop конечным и добавить `FINALIZED` | Prompt/plan |
| PROC-02 | P0 | Устранить 10m/15m и one/multiple contradictions | Prompt/plan |
| PROC-07 | P0 | Сверить wide-layout contract с production/screenshots | Design/CSS/templates |
| PROC-11 | P0 | Разделить giant plan на state/backlog/decisions/history | Process/docs |
| PROC-12 | P0 | Ввести единственный machine-readable state | Process/tooling |
| FLOW-01 | P1 | Pause/resume session | Frontend/backend |
| FLOW-02 | P1 | Idempotent answer + duplicate-submit protection | Frontend/backend |
| FLOW-03 | P1 | `Не знаю` outcome | Frontend/backend/SM-2 |
| FLOW-04 | P1 | Recoverable expired/offline/error states | Frontend/backend |
| QA-01 | P1 | Deterministic test profile and cleanup | Backend/tests |
| UX-01 | P2 | CTA hierarchy on terminal states | UI |
| UX-02 | P2 | Unify terminal states with active shell | UI |
| UX-04 | P2 | Actionable completion summary | Frontend/backend |
| UX-13 | P2 | Progressive explanation hierarchy | UI/content contract |
| UX-15 | P2 | Adaptive next action | Backend/frontend |
| UX-16 | P2 | Analytics next-action block | Backend/frontend |
| UX-21 | P2 | Question quality report | Backend/frontend |
| PERF-01 | P2 | Automatic asset fingerprinting | Backend/build |
| PERF-02 | P2 | Production-like performance budgets | QA/build |
| A11Y-01 | P2 | Consolidated manual screen-reader flow | QA |
| CLEAN-01 | P2 | Dead code/selectors/assets cleanup | Frontend |

---

# 7. Автономный operating contract для LLM

## 7.1. Главное правило

LLM не спрашивает пользователя «куда двигаться дальше». Следующая работа определяется из `FRONTEND_STATE.json`.

## 7.2. Выбор задачи

```text
1. Взять OPEN задачу с минимальным severity number: P0 → P1 → P2 → P3.
2. Среди равных взять задачу, блокирующую больше зависимостей.
3. Среди равных взять задачу core flow раньше polish.
4. Если задача blocked, записать конкретный blocker и сразу выбрать следующую независимую.
5. Не возвращаться к CLOSED без нового regression evidence.
```

## 7.3. Политика решений без пользователя

LLM принимает решение самостоятельно, если оно:

- обратимо;
- локально;
- не удаляет пользовательские данные;
- не меняет фундаментальную бизнес-семантику;
- следует PRODUCT/DESIGN/accessibility;
- имеет очевидно безопасный вариант.

Примеры: spacing, hierarchy, wording, component structure, focus behavior, responsive breakpoint, choice between two равноценных визуальных реализаций.

Пользователь нужен только если:

- возможна потеря реальных данных;
- нужны credentials/внешний доступ;
- есть юридическое/платёжное решение;
- два варианта меняют бизнес-модель;
- blocker невозможно обойти технически.

Даже тогда LLM сначала выполняет все независимые работы и задаёт один собранный вопрос, а не останавливает весь loop.

## 7.4. Work package

Один тик закрывает один связный пакет:

```text
issue → implementation → targeted tests → live QA → evidence → commit
```

Нельзя завершать тик только словами «проверено», если найден исправимый дефект.

## 7.5. Anti-drift

- не более одного audit-only тика подряд;
- не создавать новую design axis;
- не добавлять новые features после feature-freeze;
- FUTURE идеи записывать, но не реализовывать;
- не проводить третий одинаковый review без изменения кода/evidence;
- не повышать screenshot tolerance ради pass;
- не ставить `FINALIZED` при accepted-but-unfixed дефекте;
- не считать внешний owner блокером текущего финала, если он не ломает core flow;
- не использовать maintenance как продолжение finalization.

## 7.6. Конец каждого тика

LLM обязана:

1. обновить status задачи;
2. записать фактически выполненные проверки;
3. приложить commit hash;
4. выбрать следующую задачу в `nextTaskId`;
5. продолжить на следующем тике автоматически.

Если queue пуста — перейти к final gate, а не спрашивать пользователя.

---

# 8. Готовая директива, которую можно добавить в PROMPT_PLAN_FRONTEND.md

```markdown
# FINALIZATION OVERRIDE

Ты работаешь не в бесконечном redesign/maintenance-loop, а в конечном FINALIZATION ROUND.
Цель — довести проект до FINALIZED и остановить loop.

Не спрашивай пользователя, какое направление выбрать. Следующая задача всегда выбирается
из FRONTEND_STATE.json по P0→P1→P2→P3, dependency impact и core-flow priority.

Один тик = один связный work package, который включает подтверждение проблемы,
implementation, targeted tests, live QA, evidence и atomic commit. Можно менять frontend,
целевые backend/API/DB contracts и tests, если это необходимо для UX-flow, изменение
минимально, безопасно, мигрируемо и покрыто тестами. seed/mcq content не менять без
отдельной задачи content pipeline.

Не проводи audit-only тики подряд. Если найден исправимый дефект — исправь его в том же
work package. Не ставь VERIFIED/FINALIZED до deep-pass и устранения всех blocking/user-visible
дефектов.

Решения по обратимым UI/UX вопросам принимай самостоятельно на основе PRODUCT.md,
DESIGN.md, accessibility и минимального риска. Пользователя спрашивай только при реальной
необратимости, потере данных, внешних credentials или изменении бизнес-модели. Blocked-задача
не останавливает цикл: зафиксируй blocker и возьми следующую независимую.

После feature-freeze не расширяй scope. Новые идеи записывай в FUTURE_BACKLOG и не держи
ими текущий loop открытым.

Когда очередь задач пуста, выполни один final gate: clean build, full available tests,
production-like smoke, core UX flows, accessibility, performance, themes/designs,
responsive, console/network, dead-code/dirty-files. При полном pass:

1. поставь FINALIZED;
2. создай FINAL_FRONTEND_REPORT.md;
3. сделай итоговый atomic commit;
4. останови текущий loop;
5. не переходи в maintenance и не начинай новый review без новой команды пользователя.
```

---

# 9. Definition of Ideal Final State

Проект считается завершённым не потому, что «каждая страница когда-то получила галочку», а потому что:

- core learning flow понятен, устойчив и восстанавливается после ошибок;
- дизайн-документы соответствуют production;
- wide/mobile layouts используют пространство осмысленно;
- backend поддерживает UX, а не вынуждает frontend имитировать состояния;
- correct/wrong/unknown дают разные педагогические сигналы;
- summary и analytics ведут к следующему действию;
- accessibility проверена комплексно, а не серией формальных микротиков;
- performance измерена в production-like среде;
- состояние проекта воспроизводится из clean checkout;
- нет открытых пользовательских дефектов и скрытых ручных шагов;
- текущий loop действительно остановлен.

