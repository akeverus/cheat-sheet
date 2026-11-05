# Notification MCQ Q2:B0 — корреляция SES и границы событий

Дата: 2026-11-05. Узкая авторская проверка Q2:B0: прочитаны видимый вопрос, соответствующая теория, A/B/C/D и sections. Исправлены только C.sections.example и C.sections.edge_cases. Ключ C, тексты вариантов, question_text, IDs/порядок и остальные блоки сохранены. Автор видел ключ; независимые SFPL/ANSC/RU, Phase A/B и FINAL NOT_RUN.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-notification-system-interview.json`, SHA-256 `992dae788f475e2c3207077d20de89dd9647888ba571aa8c1c7edc60a9dc35d0`; baseline `a92396920c6ac81d20065e1cce9dde3be8a339f576ff450de4f91e039870bfd1`.

Прочитанная теория: `cheatsheets/interview/system-design/design-notification-system-interview.md`, SHA-256 `29399c22b58ee8292bf9c6a639ae0142cdda78b3e934f8e1de43af37e5044d87`. Перед применением сверена секция Q2. Markdown не редактировался.

| Вариант | Авторское сопоставление Q2:B0 |
|---|---|
| A | Outbox/локальный ключ не гарантируют ровно одно внешнее сообщение. |
| B | Ключ только по шаблону и адресату сливает разные исходные события. |
| C | Локальная дедупликация и журнал попыток с явной неопределённостью внешнего вызова. |
| D | Collapse отдельных push не является общим журналом и гарантией для всех каналов. |

## Исправление конкретных замечаний

Идентификатор логической отправки не различает несколько её попыток. В пояснении теперь предложена проектная схема с отдельным attempt_id, сохранённым до управляемого вызова, и notification_id. Полученное событие связывают с заданием, записанной попыткой и сообщением SES по tags и mail.messageId. Названия tags — выбор приложения, не обязательные встроенные поля SES. Несколько принятых сообщений одной записанной попытки различаются по provider messageId; ровно одно письмо не обещается. Это вывод о необходимой различимости корреляционных ключей, а не новый контракт идемпотентности AWS.

Требование configuration set + event destination ограничено показанным путём **event publishing**. Identity-level SNS notifications — отдельный механизм; прежнее «для событий SES нужен configuration set» было слишком широким. Отсутствие события не доказывает недоставку.

Первичные источники прочитаны 2026-11-05: [SES send process — принятие при ошибке и риск дубля](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [event publishing/configuration set/message tags](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-send-email.html), [mail.messageId/tags в событиях](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html), [identity-level notifications](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity-using-notifications.html). В актуальной теории Q2 эти ограничения уже изложены; новой правки Markdown для них не требуется.

## Проверки и открытые ограничения

verify-mcq-json PASS: 4 Q / 6 B; semantic review NOT_RUN. Answer-parity PASS, style-guessability=0.278. В Q2:B0 нет anti-tell/SKEL findings. Тексты вариантов не менялись, поэтому эти показатели не являются улучшением поверхности за счёт данного пакета. Сверка с baseline доказала, что после обратной замены двух полей весь объект JSON совпадает с исходным; прочие значения сохранены.

Общие диагностики темы остаются открыты: anti-tell 2/6, SKEL hard=2/warn=1; linkage errors=2/warnings=2 для Q1:B1 и Q4:B1. Struct-scan отмечает несколько осей; его exit 0 не означает приёмку. Логи `ses-notification-correlation-followup-checks.json`, check-0..6.log сохранены в task-2. Runtime/SES/SNS/SDK retries, доставка событий и согласованность БД при сбоях NOT_RUN. Автотест, повторяющий текст схемы, не добавлялся.

**Блокер учебной приёмки сценарных B1:** статическое чтение текущего кода подтвердило, что McqSeedBlock читает question_text, но McqJsonLoader.buildDesiredOptions сохраняет только варианты/пояснения/blockIdx, без stem. MarkdownQuestionParser создаёт текст Question из заголовка Markdown. NextQuestionResponseMapper:30 и focus-training.html:323 выводят Question.questionText. Поэтому отдельное условие B1 не становится видимым вопросом. Например, Q1:B1 требует ситуацию FCM Received, Q4:B1 — конкретный INVALID_ARGUMENT (400). Лексический drift здесь нельзя автоматически назвать ложным срабатыванием. Текущее исправление Q2:B0 не устраняет общий дефект отображения.

Безопасный путь отдельной задачи: сохранить stem каждого блока, отдавать его вместе с уже выбранным/pinned block через SSR/API и разбор ответа; сохранить IDs и историю повторений. Проверить два блока одного Q с разными условиями, refresh/pause/resume и старые записи без block stem. Это план, код и работающий сервис не изменялись.

Staging/коммитов нет, индекс зарезервирован шпаргалками. До этого пакета seed уже отличался от HEAD (`fe4ad41d628dab5bc10ccf1494d7cfd0d066a69977159de9506cd25f5738362e`): будущий коммит должен адресно отделить проверенное от остальных существовавших изменений. Этот отчёт не принимает остальные пять блоков и весь runtime.

При подготовке адресного коммита используется только прочитанный Q2:B0 и этот отчёт. Рабочие добавления Q1:B1/Q4:B1 сохраняются без staging. Runtime-исправление block stem подготовлено отдельно коммитом `881bc5fa3e83bd07d07a6bf03aaa44ec4968fe11` в ветке `bugfix/mcq-block-question-text`, проверено семью новыми тестами и полной backend-регрессией; в main и работающий сервис не интегрировано.
