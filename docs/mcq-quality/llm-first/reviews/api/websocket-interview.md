# WebSocket — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью, слепого прохода и `FINAL PASS`.
- Охват: Q1–Q38, 38 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/websocket-interview.json`; SHA-256 `48bcb0e86fb6da4fa835c4adf20bff5992ca51b20c24f915b6814c79a3e504c9`.
- Парный Markdown: `cheatsheets/interview/api/websocket-interview.md`; SHA-256 `015668d8105954199ab6c29cd5bced727a0ddea1dd9c250a39a72df46e2b0034`.

## Что исправлено

Прочитаны все Q1–Q38 и варианты A–D. Q1–Q4 отделяют WebSocket от HTTP/1.1 Upgrade, уточняют способы установления по HTTP/2 и HTTP/3, фреймы, маскирование и режимы gRPC streaming. Q5–Q7 разграничивают WebSocket и STOMP; в примере STOMP исправлена длина тела. Q8–Q15 уточняют raw WebSocket, жизненный цикл handler, конкурентную отправку, маршрутизацию STOMP и user destination, включая анонимную сессию и `broadcast=false`. В коде Markdown исправлено имя метода `notifyAll`, конфликтовавшее с `Object.notifyAll`.

Q16–Q17 убирают выдуманный неизменный порядок SockJS-транспортов. Q18–Q21 различают локальный simple broker, STOMP relay с соединениями от имени клиентских сессий, межузловую доставку, reconnect и случаи, когда sticky действительно нужна. Q22–Q24 исправляют защиту Origin, передачу настоящего CSRF-токена в STOMP CONNECT, настройку Spring Security 6 и адресную доставку ошибок. Q25–Q28 разделяют STOMP heartbeat и WebSocket Ping/Pong, а также servlet callbacks, WebFlux и пределы backpressure.

Q29–Q38 убирают неподтверждённые универсальные числа для памяти, лимитов соединений, экономии бинарного формата и Cloudflare idle timeout. Уточнены область `setMessageSizeLimit`, маршрутизация gateway, возобновление SSE, встроенная STOMP-статистика, Kubernetes affinity, восстановление после разрыва и проверка подписки в интеграционном тесте. Варианты выровнены по полноте: неверные ответы теперь описывают сопоставимые ошибочные модели, а подробные оговорки перенесены в объяснения. Парный Markdown согласован с вопросами, содержит явные якоря и ссылки содержания Q1–Q38; метки `(!)` удалены.

## Проверки и границы результата

Первичные источники: [RFC 6455](https://www.rfc-editor.org/rfc/rfc6455.html), [RFC 8441](https://www.rfc-editor.org/rfc/rfc8441.html), [RFC 9220](https://www.rfc-editor.org/rfc/rfc9220.html), [STOMP 1.2](https://stomp.github.io/stomp-specification-1.2.html), [Spring WebSocket/STOMP](https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html), [Spring Security WebSocket](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html), [Spring WebFlux WebSocket](https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html), [Cloudflare WebSockets](https://developers.cloudflare.com/network/websockets/).

- **Schema/структура:** `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/websocket-interview.json` → `PASS: files=1 questions=38 blocks=38; semantic review NOT_RUN`.
- **Content, facts, linkage, sections, SFPL, ANSC, RU:** авторская вычитка всех блоков; отдельного независимого вердикта по этим критериям нет.
- **Локальная самопроверка:** 38 видимых заголовков, 38 якорей и ссылок, четыре разных варианта и один ключ в каждом блоке, непустые sections; `git diff --check` прошёл.
- **Independent, blind, FINAL:** не выполнялись. Следующий этап — независимая содержательная проверка и слепой проход по указанным хешам, затем запись evidence по принятому workflow.
