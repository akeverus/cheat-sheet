---
title: "Вопросы на собеседовании: Система уведомлений"
description: "Надёжная отправка уведомлений: статусы, дубликаты, предпочтения пользователей и ошибки провайдеров"
tags:
  - interview
  - system-design
  - design-notification-system-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Design Notification System"
  - "Проектирование системы уведомлений"
prerequisites: []
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: Система уведомлений

[Общая схема каналов и очередей](system-design-interview.md#q10--как-спроектировать-notification-system) уже разобрана в базовой шпаргалке. Здесь рассматривается путь отдельного уведомления: от события до наблюдаемого результата, включая неоднозначные ответы провайдера и изменение правил пользователя во время ожидания.

## Полезные ссылки

- [FCM: срок жизни сообщения](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan) — принятие запроса не равно доставке на устройство.
- [FCM: отчёты о доставке](https://firebase.google.com/docs/cloud-messaging/understand-delivery) — различие `Sends`, `Received`, `Impressions` и `Opens`.
- [FCM: ошибки](https://firebase.google.com/docs/cloud-messaging/error-codes) и [отправка в масштабе](https://firebase.google.com/docs/cloud-messaging/scale-fcm) — удаление недействительных токенов, `Retry-After`, backoff.
- [Apple: ответы APNs](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns) и [отправка запросов](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns) — статусы, срок жизни, `apns-id`.
- [Amazon SES: путь письма](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html) и [события](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html) — принятие, доставка, bounce и complaint.
- [AWS: transactional outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html) — публикация события и повторная обработка.

## Содержание

- [Q1. Что означает статус «отправлено» для push и email?](#q1-что-означает-статус-отправлено-для-push-и-email)
- [Q2. Как ограничить дубли при повторной отправке через внешнего провайдера?](#q2-как-ограничить-дубли-при-повторной-отправке-через-внешнего-провайдера)
- [Q3. Когда проверять предпочтения пользователя и срок жизни уведомления?](#q3-когда-проверять-предпочтения-пользователя-и-срок-жизни-уведомления)
- [Q4. Как обрабатывать ошибки провайдера без бесконечных повторов?](#q4-как-обрабатывать-ошибки-провайдера-без-бесконечных-повторов)
- [See also](#see-also)

## Q1. Что означает статус «отправлено» для push и email?

Сначала определите, **какой этап** измеряет статус. Принятие провайдером запроса, доставка устройству или почтовому серверу и открытие пользователем — разные события. Одно поле `sent=true` скрывает эту разницу.

| Канал | Что известно после успешного API-вызова | Какие сигналы бывают позже |
|---|---|---|
| FCM | Сообщение принято для доставки и выдан message ID; устройство ещё могло его не получить. | В отчётах доступны `Received` для Android, `Impressions` для части Android push и `Opens`; это не одинаковые стадии для всех платформ. |
| APNs | HTTP `200` означает, что запрос принят APNs; сервис доставляет push по возможности. | Для диагностики сохраняют `apns-id`; доставка и показ не следуют из одного HTTP `200`. |
| SES | Успешный ответ возвращает message ID принятого письма. | `Delivery` означает передачу серверу получателя; возможны `Bounce` и `Complaint`. Открытие письма — отдельный, необязательный сигнал. |

Храните запись на уровне `(notification_id, recipient_id, channel)` и отдельные попытки с `provider_message_id`, временем, результатом API и полученными событиями. Например, `queued → sending → provider_accepted`, затем канал может уточнить результат до `delivered_to_server`, `bounced` или `opened`. Это **модель приложения**, а не единая машина состояний всех провайдеров: некоторые переходы недоступны или приходят асинхронно и повторно. Не превращайте отсутствие callback в доказательство недоставки. [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan), [FCM reporting](https://firebase.google.com/docs/cloud-messaging/understand-delivery), [APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns), [SES events](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html).

## Q2. Как ограничить дубли при повторной отправке через внешнего провайдера?

Разделите два уровня. **Внутри системы** событие бизнес-операции и задание на уведомление создают надёжно: если событие рождается в локальной транзакции, outbox позволяет зафиксировать его вместе с изменением данных. На приёмной стороне уникальный ключ, например `(source_event_id, recipient_id, notification_type, channel)`, не даёт двум обработчикам создать две логические отправки одного типа. Ключ выбирают по бизнес-семантике: два разных платежа не должны слиться только потому, что получатель и шаблон совпали.

**На границе провайдера** остаётся неопределённость. Если worker отправил запрос, но потерял ответ, неизвестно, принял ли его провайдер. Повтор может создать второе реальное сообщение. SES прямо описывает случай, когда письмо принято, хотя вызов вернул ошибку. Поэтому `at-least-once` в очереди плюс локальная дедупликация **не дают exactly-once доставки человеку**. Сохраняйте историю попыток и корреляционный ID, используйте идемпотентность или подавление дублей у провайдера только если это документированный контракт конкретного API; ограничивайте число повторов и для чувствительных сообщений решайте, что хуже — потеря или дубль. `apns-collapse-id` и FCM collapse помогают заменить ожидающие push определённого типа, но не являются общей гарантией exactly-once. [AWS outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html), [SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [FCM collapse](https://firebase.google.com/docs/cloud-messaging/customize-messages/collapsible-message-types), [APNs request headers](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns).

## Q3. Когда проверять предпочтения пользователя и срок жизни уведомления?

Политику канала применяют при постановке в очередь **и повторно перед фактической отправкой**: между этими моментами пользователь может отключить категорию, начнутся тихие часы или истечёт срок актуальности сообщения. Храните тип сообщения, получателя, канал, время создания, `expires_at` и версию шаблона/правил, чтобы можно было объяснить решение. Для отложенных задач проверяйте временную зону пользователя на момент планирования и отправки; переходы на летнее время требуют явного правила.

Разделите категории по смыслу: маркетинговое сообщение допускает отложенную доставку и отказ по предпочтениям; предупреждение о безопасности может иметь иной согласованный маршрут. Это **политика продукта**, её нельзя автоматически вывести из технического приоритета очереди. При истёкшем `expires_at` не отправляйте устаревший OTP или приглашение только ради выполнения задачи. Настраивайте срок жизни у провайдера отдельно: FCM `ttl` и APNs `apns-expiration` ограничивают хранение недоставленного push, но не заменяют проверку в собственном worker. После передачи провайдеру отмена может быть недоступна. [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan), [APNs expiration](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns).

## Q4. Как обрабатывать ошибки провайдера без бесконечных повторов?

Разбирайте **код и причину конкретного провайдера**, а не повторяйте любой неуспех. Для FCM недействительный или незарегистрированный registration token прекращают использовать; при временной недоступности соблюдают `Retry-After` и применяют backoff с jitter. Для APNs `410 Unregistered` означает неактивный токен, а `429` и ошибки сервера требуют отдельной политики задержки. SES bounce/complaint может прийти после успешного API-вызова: учитывайте его при будущих отправках, если получатель надёжно определён, а не повторяйте прежнее письмо.

У каждой попытки фиксируйте причину, номер, время следующей попытки и конечный срок. После лимита попыток или истечения срока переводите задание в наблюдаемое конечное состояние и, если нужно, в DLQ для разбора. Метрики разделяйте по каналу, провайдеру и стадии: очередь/возраст задачи, API accept/reject, ошибки по категориям, поздние события доставки и доля дублей. Переключение на другой канал допустимо только после проверки предпочтений и понимания исходного статуса: таймаут push API сам по себе не доказывает, что push не дойдёт. [FCM error codes](https://firebase.google.com/docs/cloud-messaging/error-codes), [FCM scale](https://firebase.google.com/docs/cloud-messaging/scale-fcm), [APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns), [SES monitoring](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html).

---

## See also

- [Базовый дизайн Notification System](system-design-interview.md#q10--как-спроектировать-notification-system) — каналы, маршрутизация и очереди.
- [Выбор брокера для уведомлений](../messaging/message-brokers-comparison-interview.md#q24-какой-брокер-выбрать-для-системы-уведомлений) — fan-out по каналам.
- [Паттерны событийной архитектуры](../architecture/event-driven-patterns-interview.md) — outbox и идемпотентность потребителя.
