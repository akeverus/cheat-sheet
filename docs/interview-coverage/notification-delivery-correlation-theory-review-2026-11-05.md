# Уведомления — авторская проверка теории Q1–Q4

Дата: 2026-11-05. Прочитаны четыре раздела и все 6 MCQ-блоков / 24 варианта с sections. Проверяющий видел ключи; это авторская проверка, не независимая blind/Phase A/B/FINAL.

Теория: [design-notification-system-interview.md](../../cheatsheets/interview/system-design/design-notification-system-interview.md), SHA-256 `29399c22b58ee8292bf9c6a639ae0142cdda78b3e934f8e1de43af37e5044d87`.
MCQ только чтение: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-notification-system-interview.json`, SHA-256 `a92396920c6ac81d20065e1cce9dde3be8a339f576ff450de4f91e039870bfd1`. JSON и индекс не менялись.

## Результаты

**Q1:** сохранено различие provider accept, device/server delivery и interaction. FCM console/Data API — агрегаты; индивидуальный исход требует настроенного источника и не выводится из Received. Уточнены два пути SES: identity-level notifications и event publishing с configuration set/event destination; поля типа notificationType/eventType различаются. [FCM reporting](https://firebase.google.com/docs/cloud-messaging/understand-delivery), [SES notifications](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity-using-notifications.html), [event fields](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html).

**Q2:** outbox/unique key остаются локальными механизмами. SES может принять письмо при ошибке ответа, а retry отправить второе; tags — корреляция, не provider idempotency. Общий ID задания недостаточен для различения нескольких попыток: добавлен отдельный attempt_id как проектный вариант message tag. Позднее событие с tags/mail.messageId сопоставляют с заданием и попыткой. Не предполагается атомарность с внешним провайдером или exactly-once человеку. [SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html), [event publishing](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-send-email.html), [event fields](https://docs.aws.amazon.com/ses/latest/dg/event-publishing-retrieving-sns-contents.html), [AWS outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html). Collapse не подменяет общий журнал/гарантию всех каналов.

**Q3:** правила/expiry проверяются перед отправкой заново. FCM Android/Web ttl — длительность; APNs apns-expiration — Unix seconds UTC. Оставшееся время считают при каждой попытке, не продлевая бизнес-срок ожиданием в своей очереди. Apple прямо описывает best-effort expiry и возможность поздней доставки, поэтому сервер должен независимо проверять срок действия OTP/иного действия. [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan), [APNs request headers](https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns).

**Q4:** классификация оформлена таблицей. UNREGISTERED отделён от неоднозначного INVALID_ARGUMENT; message/topic QUOTA_EXCEEDED требует рекомендованного initial backoff, UNAVAILABLE — Retry-After/backoff. Для APNs сохранены topic/регистрация, timestamp ответа 410 в миллисекундах, причины 429 и отдельные указания Apple для 5XX. Общий deadline и бюджет могут запретить поздний retry; timeout не доказывает недоставку. [FCM errors](https://firebase.google.com/docs/cloud-messaging/error-codes), [APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns).

## Передать MCQ — точные границы

- **Q2 B0 C.example:** сейчас один message tag с ID логической отправки назван достаточным, чтобы связать событие «с попыткой». Если несколько attempts имеют этот ID, конкретную попытку он не различает. Либо ограничить вывод логической отправкой, либо добавить отдельный attempt tag. Это вывод из неоднозначной корреляции, а не заявление о запрете SES.
- **Q2 B0 C.edge_cases:** «Для событий SES нужен configuration set» слишком широко: он нужен для event publishing, но identity-level SNS notifications — отдельный путь. Уточнить область утверждения; основной ключ C о локальной дедупликации и неизвестном исходе остаётся совместимым.
- **Q3 B0 A.edge_cases:** полезно добавить best-effort APNs expiration и независимую проверку срока бизнес-действия. Не объявлять expiry гарантией отсутствия позднего показа.

Ключи Q1 B0 B/B1 B, Q2 B0 C, Q3 B0 A, Q4 B0 D/B1 C сопоставлены с источниками. Независимая оценка surface/ANSC/SFPL не выполнялась.

## Проверки и ограничения

Сохранены IDs/порядок/TOC четырёх Q и явные anchors; header изменён только по updated. Все локальные ссылки/anchors, YAML frontmatter, whitespace и парность fences PASS. verify-mcq-json двух seeds: 8 Q / 13 блоков PASS, semantic review NOT_RUN.

Apple HTML требовал JS, а web fetch ссылки Markdown отверг text/markdown. Официальные две .md-страницы затем прочитаны через разрешённый read-only HTTPS-запрос; снимки и SHA сохраняются в `/tmp/email-notifications-theory-20261105/sources/`. Это чтение публичной документации, не взаимодействие с APNs.

**NOT_RUN:** FCM/APNs/SES API, устройство/показ/клики, экспорт BigQuery, message tags на реальной отправке, очереди/outbox/DLQ, concurrency/сбои callback, TTL/retry runtime. Не создавался тест, повторяющий учебный текст. Источники проверены 2026-11-05; формальная приёмка всей темы остаётся открытой.
