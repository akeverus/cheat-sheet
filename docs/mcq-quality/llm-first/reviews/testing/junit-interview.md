# JUnit 5 — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого содержательного ревью и без `FINAL PASS`.
- Охват: Q1–Q15, 15 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/testing/junit-interview.json`; SHA-256 `82366ca65bba4f64e43ef92e2731d4661f33ba65a5076cfea672f0f97cea6b78`.
- Парный Markdown: `cheatsheets/interview/testing/junit-interview.md`; SHA-256 `e13634f9e61a07dcb5cc0715601d5e096c548fccec6720d99e8e813e97ac8167`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/junit-interview.json`; SHA-256 `b566eaa72222bfc5715e8466e119762c18a6834eb0bb29f7bfbc505c4010ffe0`.

## Что исправлено

Прочитаны все 15 вопросов, варианты и объяснения вместе с парным Markdown. Q7 теперь прямо связывает `MockitoExtension`, `@Mock` и `@InjectMocks`; пример бросает конкретный `DataAccessResourceFailureException` вместо абстрактного `DataAccessException`. В Q9 убрано утверждение «сколько угодно»; `TimingExtension` хранит время в контексте отдельного теста, чтобы параллельные вызовы не перезаписывали друг друга.

Q13 учитывает версию Spring Boot 3.3.2 в проекте: пример использует доступную здесь `@MockBean`, а не `@MockitoBean` из более новой линии. Уточнены состав тестовых срезов, зависимость для встроенной БД и роль `SpringExtension`. В Q15 устранено неверное обобщение всех ошибок одним корнем; пример проверки приватного метода действительно показывает вызов через рефлексию. В Q12 уточнено, что `SAME_THREAD` сам по себе не блокирует общий ресурс.

В Markdown добавлены содержание и якоря Q1–Q15. Фиксированная версия JUnit в Maven-примере заменена свойством проекта. Варианты MCQ выровнены по форме без подсказок правильного ответа.

## Проверка и границы результата

Первичные источники: [JUnit 5.14.4 Overview](https://docs.junit.org/5.14.4/overview.html), [JUnit Extension Store](https://docs.junit.org/5.14.4/extensions/keeping-state-in-extensions.html), [Spring Boot 3.3 Testing](https://docs.spring.io/spring-boot/3.3/reference/testing/spring-boot-applications.html), [Spring Boot 3.4 Mockito API](https://docs.spring.io/spring-boot/3.4/api/java/org/springframework/boot/test/mock/mockito/package-summary.html).

`verify-mcq-json.sh` → `PASS: files=1 questions=15 blocks=15; semantic review NOT_RUN`. `mcq-skel-gate.py` → hard fails 0, предупреждений 0. `mcq-anti-tell-scan.py` → 0 блоков с формальными подсказками. `mcq-answer-parity-gate.py` → PASS. Содержание, якоря и номера JSON совпадают; `git diff --check` прошёл.

Эти проверки не дают независимого содержательного вердикта. Слепой проход по указанным хешам и подтверждение `FINAL` остаются открытыми.
