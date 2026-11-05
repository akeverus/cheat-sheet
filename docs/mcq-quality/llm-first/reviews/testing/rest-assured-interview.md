# REST Assured — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого содержательного ревью и без `FINAL PASS`.
- Охват: Q1–Q15, 15 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/testing/rest-assured-interview.json`; SHA-256 `802a8fd5fc61e3e6eb03a07210d07389db825a9839ab44dc3a188ac5c422e335`.
- Парный Markdown: `cheatsheets/interview/testing/rest-assured-interview.md`; SHA-256 `fa11bc76d953d3148cdc040bd5abfb180f0701472d532ba886f28c26e528f6cd`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/rest-assured-interview.json`; SHA-256 `42ac9b047e0bc7a42db238a6af7a044b76f32dc2fc7d1385f8fc0c5ab2663cd7`.

## Что исправлено

Прочитаны все 15 видимых вопросов, варианты и объяснения. Q3 теперь оговаривает, что автоматическая сериализация объекта зависит от доступного маппера. В Q10 исправлена ложная фраза о том, что MockMvc не запускает servlet-фильтры: зарегистрированные фильтры могут выполняться, хотя сетевой путь и контейнер не проверяются. Примеры запросов задают порт и MockMvc на уровне теста, без общей статической настройки.

Q12 ограничивает проверку SSE строкой завершаемого ответа; непрерывный поток и WebSocket требуют другого клиента. Q15 отражает поддержку Spring MVC и WebFlux в WebTestClient и отсутствие mock-режима у TestRestTemplate. Формулировки вариантов выровнены по полноте и длине без повторения больших фрагментов.

В Markdown добавлены якоря и ссылки содержания Q1–Q15. Фиксированная версия в примере Maven заменена свойством проекта. Fact-freshness sidecar обновлён по официальному GettingStarted: на дату проверки там показана версия REST Assured 6.3.0; `quiz-app` не объявляет эту зависимость, и версия не приписана проекту.

## Проверка и границы результата

Первичные источники: [REST Assured Usage](https://github.com/rest-assured/rest-assured/wiki/Usage), [GettingStarted](https://github.com/rest-assured/rest-assured/wiki/GettingStarted), [Spring MockMvc Filters](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/filters.html), [Spring WebTestClient](https://docs.spring.io/spring-framework/reference/testing/webtestclient.html).

`verify-mcq-json.sh` → `PASS: files=1 questions=15 blocks=15; semantic review NOT_RUN`. `mcq-skel-gate.py` → hard fails 0, предупреждение 1. `mcq-anti-tell-scan.py` → 0 блоков с формальными подсказками. `mcq-answer-parity-gate.py` → PASS. Заголовки, ссылки содержания, якоря и номера JSON совпадают; `git diff --check` прошёл.

Структурные и формальные проверки не дают независимого содержательного вердикта. Слепой проход по указанным хешам и подтверждение `FINAL` остаются открытыми.
