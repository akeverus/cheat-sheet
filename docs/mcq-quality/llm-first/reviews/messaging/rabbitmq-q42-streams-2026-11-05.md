# RabbitMQ Q42 — Streams и восстановление позиции

Дата: 2026-11-05. Статус: `AUTHOR_VERIFIED`, Q42:B0/B1, два блока. Прочитаны видимый вопрос, теория Q42, все восемь вариантов и sections. Это авторский проход с известными ключами, не независимая приёмка и не FINAL всей темы.

JSON рабочего дерева: `modules/quiz-app/src/main/resources/seed/mcq/messaging/rabbitmq-interview.json`, SHA-256 `fb63e6f00d650503589343698db7cd46f3d1cf50ba263e53822b6e9d0bc7fd2f`.

Теория: `cheatsheets/interview/messaging/rabbitmq-interview.md`, SHA-256 `b19a1f25ca59f3dae5d32a7acc6cd49918b3d7b404f6f5a81e8401437f0f40a9`. Теория не редактировалась.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q42:B0 | ack удаляет запись для всех | retention, offset, replay и читатели | чтение ограничено новыми записями | super stream обещает глобальный порядок |
| Q42:B1 | AMQP consumer сам возобновляет позицию | last принят за последнюю запись и восстановление | сравнение сохранённой/первой доступной позиции и recovery | AMQP ack принят за durable offset и возврат удалённых данных |

Все альтернативы относятся к одним и тем же сценариям; ключи B/C, тексты вопросов и вариантов, номера и порядок сохранены. B0 оставлен после чтения. В правильном B1 исправлены example и edge_cases: firstOffset в Java client 1.12.0 может выбросить NoOffsetException; наличие сохранённой позиции не возвращает удалённые retention данные, а сверка первой доступной позиции — снимок состояния, не защита от дальнейшего удаления. Tracking Stream protocol отделён от AMQP ack. Поиск первой доступной записи и фактическое восстановление приложения — разные действия.

Проверка: verify-mcq-json PASS, 42 Q / 45 блоков, semantic review NOT_RUN. Принимаются только эти два блока авторского scope; предыдущий Q39 report остаётся отдельным историческим снимком. Независимые SFPL/ANSC/RU и blind review NOT_RUN. Брокер, Java client и failure/restart scenarios не запускались; runtime и данные сервиса не менялись.

Источники, прочитанные 2026-11-05: [официальная versioned документация RabbitMQ 4.3 Streams](https://raw.githubusercontent.com/rabbitmq/rabbitmq-website/main/versioned_docs/version-4.3/streams.md) — retention, offsets и tracking; [Java client v1.12.0 StreamStats](https://raw.githubusercontent.com/rabbitmq/rabbitmq-stream-java-client/v1.12.0/src/main/java/com/rabbitmq/stream/StreamStats.java) — firstOffset и NoOffsetException. Недоступная HTML-страница не выдаётся за прочитанную: использованы исходники официальной документации.
