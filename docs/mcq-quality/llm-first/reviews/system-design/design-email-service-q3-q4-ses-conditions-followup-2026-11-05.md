# Email MCQ Q3:B1/Q4:B1 — условия tracking и suppression

Дата: 2026-11-05. Узкая авторская проверка двух B1 с чтением видимых заголовков, актуальных секций теории и A/B/C/D с sections. Изменены Q3:B1:B.sections.edge_cases, Q3:B1:C.sections.what_actually и Q4:B1:B.sections.example/edge_cases. Ключи B/B, все тексты вариантов, question_text, IDs/порядок и остальные значения сохранены. Автор видел ключ; независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-email-service-interview.json`, SHA-256 `c0a975ebb3fd63f9be31744e67789a38b4116b7ec324128ab74d1a37d3556d76`; baseline `0cf6163c0066709da1c2fa4ad82d0942685f523a782e565dd34b7156435ff443`.

Теория: `cheatsheets/interview/system-design/design-email-service-interview.md`, прочитанный SHA-256 `bbf89c22e211a6306feaa5c497f1b4bdbb952dbf44fb0d2e44f7ca600b061840`. Перед записью проверены Q3/Q4. Теория не изменялась.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q3:B1 | отписка не равна настройке tracking | request-level Tracking override и группировка bulk | отключение event publishing не является точным управлением tracking | настройка не применяется только к одной записи bulk |
| Q4:B1 | tenant не читается из локальной БД автоматически | явная авторизованная attribution и suppression scope/reasons | ACCOUNT сам не превращается в TENANT | From/IP pool не заменяют tenant policy |

## Исправленные условия применения

Q3:B1:B: дополнены поддержка параметра SDK, отдельное право ses:ApplyTrackingConfigurationOverrides и разрешение отправки. При отсутствии права SES возвращает AccessDeniedException и не отправляет сообщение; удалять override при повторе нельзя, если это обходит выбранную tracking-политику. Область override — весь bulk-запрос; Delivery/Bounce/Complaint reporting им не отключается. Это описание настройки и политики приложения, не юридическая оценка согласия. [AWS configuration overrides](https://docs.aws.amazon.com/ses/latest/dg/configuration-overrides.html).

Q4:B1:B: настройки suppression задаются **до** отправки. Пример ограничен отсутствием override у configuration set. Добавлен приоритет configuration set → tenant → account и различие effective TENANT от объединения tenant/account lists. Общие запреты приложения и общая квота аккаунта проверяются отдельно. Сам TenantName не включает отдельный список. [AWS tenant suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list-tenant-level.html).

Q3:B1:C: уточнена область выключения event publishing. Этот шаг меняет публикацию событий данным путём, но не отключает автоматически другие источники reporting/tracking. Прежнее безусловное «лишит наблюдаемости» было слишком широким. Основной тезис C о неточном управлении tracking остаётся неверным вариантом; ключ B сохранён.

Источники прочитаны 2026-11-05. Актуальная теория уже включает эти условия. Никакие политики IAM, аккаунты, квоты или настройки SES не изменялись; API не вызывались.

## Проверки и пределы

Схема PASS: 4 Q / 7 B, semantic review NOT_RUN. Сравнение с baseline ограничивает изменения четырьмя полями пояснений. Тексты вариантов не менялись, стилевые результаты не являются улучшением от этого пакета.

Общие gates темы не проходят: parity length ratio=1.42 (2 блока выше порога), absolute-marker gap=0.381, position distribution A/B/C/D=1/4/1/1. Anti-tell=3/7; SKEL hard=2/warn=1; linkage errors=3/warnings=3 для Q2:B1/Q3:B1/Q4:B1. Struct-scan отмечает стилевые оси; exit 0 не является приёмкой. Поверхностные замечания Q3:B1 и Q4:B1 остаются открыты. Пороги/ключи не менялись; выравнивание padding не выполнялось. Логи `ses-email-tracking-suppression-followup-checks.json` и check-0..6.log сохранены в task-2.

**Блокер видимого вопроса:** JSON Q3:B1 спрашивает, как отключить tracking, но видимый Markdown Q3 спрашивает, почему успешный API-вызов не гарантирует доставляемость. Текущий McqJsonLoader не сохраняет stem блока, а API mapper и focus-training.html используют общий Question.questionText. При этих заголовке и вариантах учащийся не получает условие tracking. У Q4:B1 также скрыт более узкий сценарий tenant attribution/suppression; у Q2:B1 — multi-recipient complaint. Это подтверждено статическим чтением пути импорта/вывода, а не запуском UI. Исправление пояснений не делает эти B1 учебно готовыми.

Нужна отдельная проверка хранения и отображения выбранного block stem в SSR/API, на refresh/pause/resume и в разборе ответа, с сохранением истории и совместимости старых данных. Нельзя молча исправлять runtime-несоответствие сменой ключа или признавать любой lexical drift ложным. Код, Markdown и работающий сервис в этом пакете не менялись; runtime/SDK/IAM/delivery и независимая приёмка NOT_RUN.

Staging и коммитов нет, индекс остаётся у шпаргалок. Seed уже имел изменения относительно HEAD (`0c9f41438e794bb3c077a4564551a56155c4e59f5c37feb3f25c5663652bbe4c`): при будущем адресном коммите нужно отделить этот пакет от прочих существовавших правок. Отчёт не принимает остальные пять блоков или всю тему.

## Подготовка адресного коммита

В HEAD у Q3/Q4 ещё нет B1. Подготовленный коммит добавляет целиком эти два ранее существовавших в рабочем дереве и теперь прочитанных сценарных блока с их четырьмя альтернативами; прочие рабочие изменения, включая Q2:B1, сохраняются без staging. B0 берётся из актуального HEAD. Полный working SHA выше отличается от будущего scoped blob именно по оставленным правкам.

Runtime-блокер подготовлен отдельным коммитом `881bc5fa3e83bd07d07a6bf03aaa44ec4968fe11` в ветке `bugfix/mcq-block-question-text`: семь новых регрессий и полная backend-регрессия прошли. В main и работающий сервис он не интегрирован. Его наличие не заменяет содержательную/независимую приёмку этих B1.
