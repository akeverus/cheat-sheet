---
title: "Вопросы на собеседовании: Распределённый почтовый сервис"
description: "Очереди отправки, лимиты провайдера, bounce и complaint, аутентификация домена и репутация"
tags:
  - interview
  - system-design
  - design-email-service-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Distributed Email Service"
  - "Проектирование сервиса отправки email"
prerequisites: []
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: Распределённый почтовый сервис

[Notification System Q10](system-design-interview.md#q10--как-спроектировать-notification-system) показывает общую маршрутизацию каналов, а [Notification System Q1–Q4](design-notification-system-interview.md) — статусы и повторы внешнего вызова. Здесь проектируем **почтовый канал как самостоятельный продукт**: его лимиты, обратную связь от почтовых серверов, аутентификацию домена и репутацию отправителя.

## Полезные ссылки

- [Amazon SES: путь письма](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [лимиты отправки](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html) и [ошибки квот](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas-errors.html).
- [Amazon SES: события через SNS](https://docs.aws.amazon.com/ses/latest/dg/notification-contents.html), [suppression list](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list.html) и [репутация](https://docs.aws.amazon.com/ses/latest/dg/monitor-sender-reputation.html).
- [Amazon SES: tenant management](https://docs.aws.amazon.com/ses/latest/dg/tenants.html), [tenant-level suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list-tenant-level.html) и [отдельные IP pools](https://docs.aws.amazon.com/ses/latest/dg/dedicated-ip-pools.html).
- [Amazon SES: переопределение open/click tracking для запроса](https://docs.aws.amazon.com/ses/latest/dg/configuration-overrides.html).
- [Gmail: email sender guidelines](https://support.google.com/mail/answer/81126?hl=en) — требования к аутентификации, жалобам и отписке для разных категорий отправителей.

## Содержание

- [Q1. Как отправлять письма при общих лимитах провайдера и разных приоритетах?](#q1-как-отправлять-письма-при-общих-лимитах-провайдера-и-разных-приоритетах)
- [Q2. Как превратить bounce и complaint в решение о будущих отправках?](#q2-как-превратить-bounce-и-complaint-в-решение-о-будущих-отправках)
- [Q3. Почему успешный API-вызов не гарантирует доставляемость письма?](#q3-почему-успешный-api-вызов-не-гарантирует-доставляемость-письма)
- [Q4. Как изолировать репутацию нескольких клиентов почтового сервиса?](#q4-как-изолировать-репутацию-нескольких-клиентов-почтового-сервиса)
- [See also](#see-also)

## Q1. Как отправлять письма при общих лимитах провайдера и разных приоритетах?

Сначала разделите **принятие задания** вашим API и **передачу письма провайдеру**. Задание с `message_id`, адресатом, категорией, версией шаблона и сроком актуальности долговечно фиксируется в очереди. Worker перед отправкой повторно проверяет адрес, согласие/отписку, suppression и `expires_at`; затем использует лимитер, соответствующий квотам конкретного аккаунта и региона. У Amazon SES квота объёма считается по получателям за скользящие предыдущие 24 часа, а не сбрасывается в полночь: письмо десяти адресатам расходует десять единиц. Скорость отправки проверяется отдельно. При превышении квоты SES отклоняет запрос и не ставит его в свою очередь для автоматической повторной доставки. Проверяйте контракт интерфейса: руководство квот описывает `ThrottlingException`, а справочник SES v2 `SendEmail` — `TooManyRequestsException` (HTTP 429). [SES quotas](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html), [quota errors](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas-errors.html), [SES v2 SendEmail](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html).

Выделяйте мощности для транзакционных писем, если маркетинговая кампания способна занять весь общий лимит. Это могут быть отдельные очереди и бюджеты скорости с общей проверкой квоты провайдера. При throttling откладывайте конкретное задание с backoff и jitter, а не запускайте мгновенный массовый retry. При длительной перегрузке измеряйте возраст очереди, долю задач с истёкшим сроком, остаток суточной квоты и число отказов. Для OTP старое письмо может быть хуже отсутствующего, поэтому срок жизни и политику потери/повтора определяют по категории.

Дедупликация задания и неоднозначный результат внешнего вызова уже разобраны в [Notification System Q2](design-notification-system-interview.md#q2-как-ограничить-дубли-при-повторной-отправке-через-внешнего-провайдера): собственная очередь не даёт гарантии ровно одного письма в ящике. Здесь дополнительно важно, что масштабирование workers сверх квоты SES лишь увеличит число throttling-ошибок.

## Q2. Как превратить bounce и complaint в решение о будущих отправках?

Сопоставляйте событие провайдера с попыткой по `provider_message_id`, адресату и собственному `message_id`. Храните исходное событие и идемпотентно применяйте его к затронутым адресатам, а не ко всем из исходного To/Bcc. Не заменяйте историю единственным «последним статусом»: SNS/SES не обещает удобного порядка или единственного экземпляра уведомления; `Delivery` может предшествовать `Bounce`, если сервер получателя сначала принял письмо, а затем отверг его. `Delivery` значит передачу следующему почтовому серверу, а не прочтение человеком. [SES notification contents](https://docs.aws.amazon.com/ses/latest/dg/notification-contents.html).

Разделяйте **permanent/hard bounce**, **transient/soft bounce** и **complaint**. Подтверждённый hard bounce и жалобу используйте как основание для suppression будущих попыток на соответствующий адрес; временный отказ может допускать отложенную повторную отправку в пределах срока актуальности. SES сам повторяет доставку при временных SMTP-проблемах и сообщает итоговый soft bounce после прекращения своих попыток: не создавайте новое письмо на каждый промежуточный отказ. [SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html). Не превращайте каждый soft bounce в вечный запрет и не повторяйте письмо после жалобы. Amazon SES поддерживает account-level и tenant-level suppression по причинам `BOUNCE` и `COMPLAINT`; конфигурацию нужно проверить на уровне используемого аккаунта/tenant, а не полагаться на абстрактный «чёрный список». [SES suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list.html), [tenant suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list-tenant-level.html).

Для жалобы важна гранулярность отправки: SES может указать в `complainedRecipients` лишь **возможных** заявителей, если одно письмо адресовано нескольким людям. Отправляйте отдельное сообщение каждому получателю, чтобы событие можно было уверенно связать с адресом. Для уже отправленного письма с несколькими адресатами не выдавайте список возможных заявителей за установленного автора жалобы; сохраните неопределённость и примените выбранную политику ограничения дальнейших отправок. [SES notification contents](https://docs.aws.amazon.com/ses/latest/dg/notification-contents.html).

Suppression от провайдера и отказ пользователя от маркетинговой рассылки — **разные причины** с разным владельцем и правилами снятия. Храните причину, источник, время и область действия. Перед каждой отправкой проверяйте обе политики; удаление адреса из списка SES само по себе не означает согласие пользователя.

## Q3. Почему успешный API-вызов не гарантирует доставляемость письма?

API-подтверждение SES означает, что сервис **принял сообщение к обработке**. Затем почтовый сервер получателя может отвергнуть его, поместить в spam или принять без открытия; bounce, complaint и engagement приходят позже и видны не всегда одинаково. [SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [SES events](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html).

Для домена отправителя настройте SPF и DKIM, проверьте DMARC и соответствие домена в `From` требованиям получателя. Для массовых отправителей Gmail указывает дополнительные условия, включая SPF, DKIM, DMARC и удобную отписку от маркетинговых/подписных писем; правила зависят от типа и объёма отправки, поэтому проверяйте их актуальную редакцию. Аутентификация подтверждает полномочия домена и снижает риск подмены, но сама по себе **не гарантирует inbox placement**. [Gmail sender guidelines](https://support.google.com/mail/answer/81126?hl=en).

Наблюдайте отдельно принятие API, доставку до принимающего сервера, постоянные/временные отказы и жалобы; оценивать «успешность» только по `Send` опасно. SES отслеживает bounce и complaint rates и может ограничить отправку при плохой репутации. Рассылка по неактуальной базе адресов бьёт по репутации всего потока; настройка DNS не заменит очистку адресов и исполнение отписок. [SES sender reputation](https://docs.aws.amazon.com/ses/latest/dg/monitor-sender-reputation.html).

Отписка от рассылки и разрешение на **отслеживание открытий и кликов** — отдельные настройки. Если для конкретного получателя tracking не разрешён политикой продукта или применимыми требованиями, в SES v2 API можно переопределить `ConfigurationOverrides.Tracking` на `OpenTrackingEnabled: "DISABLED"` и `ClickTrackingEnabled: "DISABLED"` для запроса: SES не вставит tracking pixel и не перепишет ссылки для отслеживания. Для `SendBulkEmail` переопределение действует на **весь запрос**, поэтому группируйте адресатов по настройке tracking; обычные события доставки, bounce и complaint этим override не отключаются. Нужны поддержка параметра используемым SDK и право `ses:ApplyTrackingConfigurationOverrides` наряду с разрешением отправки; без него SES вернёт `AccessDeniedException` и не отправит письмо. Не удаляйте override при таком отказе, если отправка без него нарушит выбранную tracking-политику. [SES configuration overrides](https://docs.aws.amazon.com/ses/latest/dg/configuration-overrides.html), [API tracking overrides](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_TrackingConfigurationOverrides.html).

## Q4. Как изолировать репутацию нескольких клиентов почтового сервиса?

Если сервис отправляет письма за несколько команд или клиентов, добавьте `tenant_id` в авторизованный запрос, запись задания, шаблон, sender identity, квоты, suppression, события и метрики. Проверяйте владение доменом и адресом отправителя; один tenant не должен выбирать `From` или читать события другого. На входе ставьте пределы объёма и скорости каждому tenant, а в очередях планируйте отправку так, чтобы кампания одного клиента не задержала все транзакционные письма. Общая квота SES при этом остаётся отдельным ограничением. [SES quotas](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html).

В SES tenant management одного внутреннего `tenant_id` недостаточно: при API-отправке передавайте авторизованный `TenantName`, при SMTP — `X-SES-TENANT`. Сопоставляйте его с tenant задания на сервере, а не доверяйте значению от клиента. Tenant-level suppression также не включается одним созданием tenant: по умолчанию `SuppressionScope` равен `ACCOUNT`; для отдельного списка задайте `TENANT` и нужные `SuppressedReasons` вместе. Проверьте эффективные настройки: configuration set имеет приоритет над tenant, а tenant — над account. При эффективном `TENANT` SES проверяет tenant list, а не объединение с account list; общие запреты приложения нужно учитывать отдельно. [SES tenants](https://docs.aws.amazon.com/ses/latest/dg/tenants.html), [tenant suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list-tenant-level.html).

Изоляция **данных** не тождественна изоляции **репутации**. SES tenant management умеет разделять identities, configuration sets, templates и репутационные показатели разных клиентов; dedicated IP pools позволяют отделить, например, маркетинговый поток от транзакционного, но добавляют стоимость и требуют выбора режима: standard предполагает управление прогревом, managed автоматизирует его средствами SES. Shared IP проще для нерегулярной нагрузки, однако не даёт полного контроля над репутацией IP. Выбор зависит от объёма и требований к отдельной репутации; отдельный IP не нужен автоматически каждому tenant. [SES tenants](https://docs.aws.amazon.com/ses/latest/dg/tenants.html), [SES dedicated IPs](https://docs.aws.amazon.com/ses/latest/dg/dedicated-ip.html), [IP pools](https://docs.aws.amazon.com/ses/latest/dg/dedicated-ip-pools.html).

Операционно следите за bounce/complaint rate, отказами из-за квот и отставанием по tenant и типу письма. При ухудшении репутации одного отправителя ограничивайте именно его поток и разбирайте причины; не маскируйте проблему бесконечным retry или сменой адреса `From`.

---

## See also

- [Notification System Q1–Q4](design-notification-system-interview.md) — общие статусы, повторы, предпочтения и классификация ошибок провайдера.
- [System Design Q10](system-design-interview.md#q10--как-спроектировать-notification-system) — многоканальная архитектура уведомлений.
- [Выбор брокера для уведомлений](../messaging/message-brokers-comparison-interview.md#q24-какой-брокер-выбрать-для-системы-уведомлений) — fan-out и отдельные очереди каналов.
- [Паттерны событийной архитектуры](../architecture/event-driven-patterns-interview.md) — outbox и идемпотентная обработка событий.
