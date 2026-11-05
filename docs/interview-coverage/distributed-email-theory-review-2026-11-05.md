# Почтовый сервис — авторская проверка теории Q1–Q4

Дата: 2026-11-05. Прочитаны все четыре раздела, существовавшие изменения, 7 MCQ-блоков / 28 вариантов с sections. Ключи были видимы: это авторское сопоставление с источниками, не независимая слепая приёмка, Phase A/B или FINAL.

Теория: [design-email-service-interview.md](../../cheatsheets/interview/system-design/design-email-service-interview.md), SHA-256 `bbf89c22e211a6306feaa5c497f1b4bdbb952dbf44fb0d2e44f7ca600b061840`.
MCQ только чтение: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-email-service-interview.json`, SHA-256 `0cf6163c0066709da1c2fa4ad82d0942685f523a782e565dd34b7156435ff443`. JSON и индекс не менялись.

## Содержательные результаты

**Q1:** объём SES учитывает получателей за скользящие предыдущие 24 часа, а не число API-вызовов/сброс в полночь; rate ограничен отдельно и зависит от региона. Отвергнутые по квоте письма SES сам позже не досылает. Разделены имя ошибки из общего руководства (`ThrottlingException`) и контракт SES v2 SendEmail (`TooManyRequestsException`, HTTP 429). Очереди/приоритеты и срок актуальности — схема приложения, не способ увеличить квоту провайдера. [Quotas](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html), [quota errors](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas-errors.html), [v2 SendEmail](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html).

**Q2:** исходные события применяются идемпотентно к затронутым адресатам; история не заменяется одним последним статусом. Проверены возможность Delivery перед Bounce, отсутствие ordering/batching guarantees, различия transient/permanent, неопределённый заявитель multi-recipient complaint и отдельность отписки. SES повторяет временные SMTP-отказы сам, а soft-bounce notification означает прекращение таких попыток; новая отправка на каждый промежуточный отказ породит лишнее письмо. [Notification contents](https://docs.aws.amazon.com/ses/latest/dg/notification-contents.html), [send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html). Suppression по подтверждённой причине и изменения пользовательского согласия — разные политики; неизвестность жалобщика не скрывается.

**Q3:** API accept, server delivery, inbox placement и interaction разделены; SPF/DKIM/DMARC не гарантируют inbox. Проверены актуальные требования [Gmail](https://support.google.com/mail/answer/81126?hl=en) и [SES reputation](https://docs.aws.amazon.com/ses/latest/dg/monitor-sender-reputation.html), без объявления универсальной нормы для всех типов отправки. Существовавшая настройка ConfigurationOverrides.Tracking для SendEmail/SendBulkEmail подтверждена; override действует на весь запрос. Добавлено отдельное IAM-право `ses:ApplyTrackingConfigurationOverrides`: без него запрос с override отклоняется, а не тихо отправляется. Удаление override при retry не должно обходить tracking-политику приложения. [Overrides](https://docs.aws.amazon.com/ses/latest/dg/configuration-overrides.html), [API schema](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_TrackingConfigurationOverrides.html). Это техническое описание, а не юридическое заключение о согласии.

**Q4:** подтверждены TenantName / X-SES-TENANT и default ACCOUNT. Добавлен приоритет suppression: configuration set → tenant → account. Эффективный TENANT проверяет tenant list, а не объединяет его с account list; общие запреты приложения проверяются отдельно. Различены standard/managed dedicated IP: прогрев managed автоматизирует SES, собственный IP не обязателен каждому tenant. [Tenant suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list-tenant-level.html), [tenants](https://docs.aws.amazon.com/ses/latest/dg/tenants.html), [dedicated IP modes](https://docs.aws.amazon.com/ses/latest/dg/dedicated-ip.html), [pools](https://docs.aws.amazon.com/ses/latest/dg/dedicated-ip-pools.html).

## Передать MCQ

Ключи Q1 B0 B, Q2 B0 C/B1 B, Q3 B0 A/B1 B, Q4 B0 D/B1 B совместимы с рассмотренными основными тезисами. Это не приёмка неугадываемости.

Для Q4 B1 B.example/edge_cases полезно явно ограничить пример отсутствием переопределения configuration set и пояснить, что effective TENANT не добавляет account list. Одной настройки на tenant может быть недостаточно при override. Для Q3 B1 B.edge_cases добавить IAM-предусловие и запрет обходить tracking-политику удалением override при AccessDenied. Это необходимые условия применения конкретной инструкции, а не основание механически сменить ключ. JSON здесь не изменён.

## Проверки и границы

Сохранены IDs/порядок четырёх Q, title/TOC/явные anchors и существовавшие чужие изменения; собственный header меняет только updated 2026-11-05. Все локальные ссылки/anchors и YAML frontmatter проверены; whitespace и fences PASS. verify-mcq-json двух seeds: 8 Q / 13 блоков PASS, semantic review NOT_RUN.

Источники прочитаны 2026-11-05. **NOT_RUN:** AWS API/SDK, IAM policy evaluation, SES sandbox, SMTP, DNS/domain authentication, доставка событий, нагрузка и репутационные эффекты. Рантайм-тест, повторяющий текст учебной схемы, не добавлялся; формальная независимая приёмка остаётся открытой.
