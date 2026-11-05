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
updated: "2026-11-05"
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
| FCM | Сообщение принято для доставки и выдан message ID; устройство ещё могло его не получить. | В отчётах доступны `Received` для Android, `Impressions` для части Android push и `Opens`; это не одинаковые стадии для всех платформ и не готовые callback для каждой отправки. |
| APNs | HTTP `200` означает, что запрос принят APNs; сервис доставляет push по возможности. | Для диагностики сохраняют `apns-id`; доставка и показ не следуют из одного HTTP `200`. |
| SES | Успешный ответ возвращает message ID принятого письма. | `Delivery` означает передачу серверу получателя; возможны `Bounce` и `Complaint`. Открытие письма — отдельный, необязательный сигнал. |

Храните запись на уровне `(notification_id, recipient_id, channel)` и отдельные попытки с `provider_message_id`, временем, результатом API и полученными событиями. Например, `queued → sending → provider_accepted`; позже SES может сообщить `Delivery` до почтового сервера или `Bounce`, а приложение — открытие уведомления. Это **модель приложения**, а не единая машина состояний всех провайдеров: некоторые переходы недоступны или приходят асинхронно и повторно. Отчёты FCM в консоли и Data API дают агрегаты, а не квитанцию по каждому `notification_id`; для анализа отдельных сообщений требуется заранее настроенный экспорт доступных данных или собственная телеметрия приложения. Для SES event publishing нужны configuration set с event destination и его применение при отправке. Это отдельный путь от identity-level SNS notifications для bounce/complaint/delivery; у них различается и поле типа (`eventType` либо `notificationType`). Поддерживаемый источник и схему события определяйте явно. [SES notifications](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity-using-notifications.html). Не превращайте отсутствие позднего сигнала в доказательство недоставки. [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan), [FCM reporting](https://firebase.google.com/docs/cloud-messaging/understand-delivery), [APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns), [SES event publishing](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-using-event-publishing-setup.html), [SES events](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html).

## Q2. Как ограничить дубли при повторной отправке через внешнего провайдера?

Разделите два уровня. **Внутри системы** событие бизнес-операции и задание на уведомление создают надёжно: если событие рождается в локальной транзакции, outbox позволяет зафиксировать его вместе с изменением данных. На приёмной стороне уникальный ключ, например `(source_event_id, recipient_id, notification_type, channel)`, не даёт двум обработчикам создать две логические отправки одного типа. Ключ выбирают по бизнес-семантике: два разных платежа не должны слиться только потому, что получатель и шаблон совпали.

**На границе провайдера** остаётся неопределённость. Если worker отправил запрос, но потерял ответ, неизвестно, принял ли его провайдер. Повтор может создать второе реальное сообщение. SES прямо описывает случай, когда письмо принято, хотя вызов вернул ошибку. Поэтому `at-least-once` в очереди плюс локальная дедупликация **не дают exactly-once доставки человеку**. Сохраняйте историю попыток. Для SES event publishing можно передавать ID логической отправки и отдельный `attempt_id` как message tags вместе с configuration set. Позднее событие с tags и SES `mail.messageId` позволит связать результат с заданием и конкретной попыткой даже при потерянном ответе. Один общий ID задания различает задания, но не несколько попыток одного задания; tags обеспечивают корреляцию, а не идемпотентность провайдера. Используйте идемпотентность или подавление дублей у провайдера только если это документированный контракт конкретного API; ограничивайте число повторов и для чувствительных сообщений решайте, что хуже — потеря или дубль. `apns-collapse-id` и FCM collapse помогают заменить ожидающие push определённого типа, но не являются общей гарантией exactly-once. [AWS outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html), [SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [SES event publishing](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-send-email.html), [SES event fields](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html), [FCM collapse](https://firebase.google.com/docs/cloud-messaging/customize-messages/collapsible-message-types), [APNs request headers](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns).

## Q3. Когда проверять предпочтения пользователя и срок жизни уведомления?

Политику канала применяют при постановке в очередь **и повторно перед фактической отправкой**: между этими моментами пользователь может отключить категорию, начнутся тихие часы или истечёт срок актуальности сообщения. Храните тип сообщения, получателя, канал, время создания, `expires_at` и версию шаблона/правил, чтобы можно было объяснить решение. Для отложенных задач проверяйте временную зону пользователя на момент планирования и отправки; переходы на летнее время требуют явного правила.

Разделите категории по смыслу: маркетинговое сообщение допускает отложенную доставку и отказ по предпочтениям; предупреждение о безопасности может иметь иной согласованный маршрут. Это **политика продукта**, её нельзя автоматически вывести из технического приоритета очереди. При истёкшем `expires_at` не отправляйте устаревший OTP или приглашение только ради выполнения задачи. Настраивайте срок жизни у провайдера отдельно. Для Android/Web FCM `ttl` — длительность, а APNs `apns-expiration` — абсолютный Unix timestamp в секундах UTC. При каждой попытке вычисляйте оставшееся время до `expires_at`, чтобы ожидание в своей очереди и retry не продлевали актуальность. APNs соблюдает expiry по возможности: уведомление может прийти после указанного timestamp. Поэтому срок бизнес-действия (например, принятия OTP) проверяют и при его выполнении на сервере; worker/TTL этого не заменяют. После передачи провайдеру отмена может быть недоступна. [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan), [APNs expiration](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns).

## Q4. Как обрабатывать ошибки провайдера без бесконечных повторов?

Разбирайте **код и причину конкретного провайдера**, а не повторяйте любой неуспех:

| Ответ | Действие |
|---|---|
| FCM `UNREGISTERED` | Прекратить отправку на эту регистрацию. |
| FCM `INVALID_ARGUMENT` | Проверить details, payload и параметры: ошибка может относиться к токену, но один HTTP 400 не доказывает его недействительность. |
| FCM `QUOTA_EXCEEDED` | Снизить соответствующую скорость; для message/topic rate quota документация задаёт начальную задержку повтора не менее минуты. |
| FCM `UNAVAILABLE` | Учитывать `Retry-After`, если есть, затем backoff с jitter в пределах бюджета. |
| APNs `410 Unregistered` | Прекратить отправку на неактивную регистрацию для данного topic; учитывать последующую регистрацию приложения. Сохранить `timestamp` из ответа — он в миллисекундах Epoch, в отличие от секунд у `apns-expiration`. |
| APNs `429` / `5XX` | Смотреть `reason`: частое обновление provider token и частые push одному устройству — разные причины. Для `5XX` Apple описывает повтор после 15 минут с возможным backoff; если срок сообщения истёк, не повторять. |

SES bounce/complaint может прийти после успешного API-вызова: учитывайте его при будущих отправках, если получатель надёжно определён, а не повторяйте прежнее письмо. Таблица не заменяет диагностику других кодов: например, ошибка авторизации требует исправления настройки, а не бесконечного retry.

У каждой попытки фиксируйте причину, номер, время следующей попытки и конечный срок. После лимита попыток или истечения срока переводите задание в наблюдаемое конечное состояние и, если нужно, в DLQ для разбора. Метрики разделяйте по каналу, провайдеру и стадии: очередь/возраст задачи, API accept/reject, ошибки по категориям, поздние события доставки и доля дублей. Переключение на другой канал допустимо только после проверки предпочтений и понимания исходного статуса: таймаут push API сам по себе не доказывает, что push не дойдёт. [FCM error codes](https://firebase.google.com/docs/cloud-messaging/error-codes), [FCM scale](https://firebase.google.com/docs/cloud-messaging/scale-fcm), [APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns), [SES monitoring](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html).

---

## See also

- [Базовый дизайн Notification System](system-design-interview.md#q10--как-спроектировать-notification-system) — каналы, маршрутизация и очереди.
- [Распределённый почтовый сервис](design-email-service-interview.md) — квоты SES, обратная связь и репутация отправителя.
- [Выбор брокера для уведомлений](../messaging/message-brokers-comparison-interview.md#q24-какой-брокер-выбрать-для-системы-уведомлений) — fan-out по каналам.
- [Паттерны событийной архитектуры](../architecture/event-driven-patterns-interview.md) — outbox и идемпотентность потребителя.
