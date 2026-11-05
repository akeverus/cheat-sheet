# RabbitMQ Q39 — диагностика по метрикам

Дата: 2026-11-05. Статус: AUTHOR_VERIFIED, Q39:B0. Прочитаны видимый вопрос, теория Q39, все A/B/C/D и sections. Сохранены ключ C, вопрос, порядок и все остальные блоки. Это не независимая или итоговая приёмка всей темы.

JSON рабочего дерева: `modules/quiz-app/src/main/resources/seed/mcq/messaging/rabbitmq-interview.json`, SHA-256 `df3a50cea1e6230f38de191101273c5cdf9be308e67b38fc2789956d2a0dbf10`. Теория: `cheatsheets/interview/messaging/rabbitmq-interview.md`, SHA-256 `b19a1f25ca59f3dae5d32a7acc6cd49918b3d7b404f6f5a81e8401437f0f40a9`. Теория не редактировалась. Прежнее изменение Q42 не входит в этот scope.

Все варианты называют группы показателей и предлагают их интерпретацию. A ошибочно меняет местами publishing и consume-only соединения при alarms; B принимает publish > ack за достаточное доказательство роста; C требует сопоставимого scope, режима ack, удалений и фактической динамики; D принимает число consumers за занятость существующих подписчиков. У всех вариантов сопоставимый предмет и детализация, а ошибочные модели опровергаются текущими sections.

Правильный ответ больше не обещает неизбежный монотонный рост. Пример auto-ack показывает недостаточность сравнения с ручными ack. Добавление consumers не предлагается автоматически без установления причины. Resource alarms и transient flow control разделены.

Источники проверены 2026-11-05: [RabbitMQ 4.3 alarms](https://www.rabbitmq.com/docs/4.3/alarms), [исходный текст confirms для версии 4.3](https://raw.githubusercontent.com/rabbitmq/rabbitmq-website/main/versioned_docs/version-4.3/confirms.md), [исходный текст flow control для версии 4.3](https://raw.githubusercontent.com/rabbitmq/rabbitmq-website/main/versioned_docs/version-4.3/flow-control.md). Web-rendered confirms/flow-control были недоступны; использована versioned_docs/version-4.3 документация официального репозитория. Недоступный maxlength не выдаётся за самостоятельно прочитанный источник.

Verify-mcq-json: PASS, 42 вопроса / 45 блоков, semantic review NOT_RUN. SFPL/ANSC/RU сопоставлены автором с ключом; независимый PASS не заявлен. Брокер/клиенты не запускались, доступы, сеть и runtime не менялись.
