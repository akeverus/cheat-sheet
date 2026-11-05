# Reservations — оплата и внешние каналы: авторская проверка теории

Дата: 2026-11-05. Область: **Q4/Q5**, включая существовавшее незакоммиченное добавление Q5, его TOC и источников Booking.com. Q1–Q3 не принимались. Проверка автора с видимыми ключами MCQ; не независимая приёмка, Phase A/B или FINAL.

Теория: [design-reservation-system-interview.md](../../cheatsheets/interview/system-design/design-reservation-system-interview.md), SHA-256 `e159d059ff66bfc840e112552572b547d62eefade1680e43b91076b67aabc9b4`.
MCQ, только чтение: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-reservation-system-interview.json`, SHA-256 `60e375b401de3f9a03a8d5d38ec5ed88b52ba8474e9fde706df79f1739881943`.

## Проверенные изменения

Q4 сохраняет отсутствие общего коммита БД/PSP и условный маршрут для manual capture. Различены TTL номера и срок авторизации; для Stripe указан capture_before. Идемпотентный ключ повторяет ту же внешнюю операцию, а авторизация/capture/refund имеют собственные ID/ключи. Подтверждённый отказ и timeout разделены: неоднозначный ответ требует сверки. Поздний успех PSP не должен оживлять истёкший HELD; неудавшийся условный confirm требует компенсации авторизации через журнал. Ограничения методов и сроков проверены по [Stripe manual capture](https://docs.stripe.com/payments/place-a-hold-on-a-payment-method). Для [Stripe API v1 idempotency](https://docs.stripe.com/api/idempotent_requests) сохранён локальный журнал: ключ может быть удалён после как минимум 24h, и последующее повторное использование может создать новый запрос. Это схема приложения для подходящего тарифа/метода, а не универсальный гостиничный платёжный workflow.

Q5 проверен по первичным Booking.com docs. [OTA_HotelAvailNotif](https://developers.booking.com/connectivity/docs/ota-hotelavailnotif) задаёт BookingLimit на room type, а End включителен: внутреннее проживание [10 ноября,12 ноября) занимает только ночи 10 и 11. [Reservations API](https://developers.booking.com/connectivity/docs/reservations-api/reservations-overview) различает OTA XML (повтор до acknowledgement) и B.XML (отдельного acknowledgement нет). Запись до подтверждения, идемпотентное применение и локальная транзакция остатков описаны как решения приложения. [Overbooking troubleshooting](https://developers.booking.com/connectivity/docs/con-faq-reservations-troubleshooting-overbookings) подтверждает возможность продажи до закрытия availability с доставкой сообщения после закрытия; время получения не равно времени продажи. Внешнюю принятую бронь при нехватке остатка нельзя молча терять.

## Сопоставление и проверки

Прочитаны Q4/Q5 B0, все восемь вариантов и sections. В snapshot ключи **Q4 D / Q5 C** совместимы с проверенной теорией; дополнительных противоречий правильному ответу не выявлено. В MCQ не требуется копировать все инженерные детали, но при расширении важно сохранить различие timeout/отказа, срока авторизации и резервирования, OTA XML/B.XML. Независимые semantic/anti-tell gates этим выводом не заменяются.

IDs и порядок пяти Q сохранены. Относительно рабочего backup меняются только Q4/Q5 и updated; относительно HEAD сохранены ранее добавленные Q5/TOC/Booking sources. Локальные ссылки на Payment Q7 и delivery Q9 сверены с реальными TOC/headings и не требовали изменения. Frontmatter/YAML, fences, локальные ссылки/anchors и whitespace PASS. `verify-mcq-json.sh` трёх связанных seeds: 79 Q / 79 блоков PASS, semantic review NOT_RUN.

**NOT_RUN:** реальные платежи и API-вызовы, Booking.com sandbox, конкурирующие БД-транзакции, webhooks/reconciliation и отказоустойчивость доставки. Источники прочитаны 2026-11-05. Это проверка учебной схемы, не исполненная интеграция и не приёмка всей темы.
