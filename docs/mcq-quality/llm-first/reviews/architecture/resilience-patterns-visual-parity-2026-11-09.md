# Resilience Patterns — ревизия визуальной симметрии MCQ

Дата: 2026-11-09. Статус: **VISUAL_PARITY_REVIEWED**, независимое ревью **NOT_RUN**, FINAL **не присвоен**.

Проверены видимые Q1:B0–Q43:B0, все 172 варианта A–D и пояснения к ним. Для каждой четвёрки сравнивались разметка, пунктуация, тире, синтаксический рисунок и примерная длина. После этого повторно сверялись смысл вопроса, ключ, правдоподобие ошибочных моделей и пояснения. Выравнивание по числу знаков не было целью: перечисления в ответах сохранены там, где они передают состав паттернов или параметров.

Актуальные SHA-256:

- Markdown: `6a065953d8b9772ab3f6aded057837eae0bd2a142a408feae38eb0616358ae90` — `cheatsheets/interview/architecture/resilience-patterns-interview.md`.
- MCQ JSON: `e2d7b48beb2f5a04a1ee985c3c6f934b038cbaccd92b2ec000a12cba38b1f3d5` — `modules/quiz-app/src/main/resources/seed/mcq/architecture/resilience-patterns-interview.json`.
- Fact freshness: `2eec9fe9b740084a46698aa64276b431c20d85cb7c74b9d8b9017c304c956c1d` — `docs/mcq-quality/fact-freshness/resilience-patterns-interview.json`.

Переписаны варианты, которые выдавали ключ за счёт избыточной подробности, кода или единственного длинного предложения. Ошибочные ответы сохранены как полные, технически узнаваемые модели, а не как обрывки или очевидные отрицания. В Q15 и Q16 уточнены границы Semaphore и ThreadPool Bulkhead и конфигурации аннотации. В Q24 сохранён настраиваемый порядок аспектов. В Q29 разведены Micrometer, метрики и события Actuator. В Q35 исправлено пояснение к ошибочному варианту: [актуальная документация Spring Boot](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html) указывает, что graceful shutdown включён по умолчанию для поддерживаемых встроенных серверов. В Q42 явно назван Chaos Monkey for Spring Boot, чтобы не спутать его с инфраструктурным Chaos Monkey Netflix.

Структурный валидатор: `PASS: files=1 questions=43 blocks=43; semantic review NOT_RUN`. Диагностика симметрии вариантов прошла: correct longest 13/43, максимальное отношение длин 1.28, среднее отношение правильных к неверным 1.04, разница по числу предложений не больше 1, подсказок по ключу 0. Общий pre-check завершился с кодом 1 из-за двух лексических `NEIGHBOR-LEAK`: Q2→Q1 и Q4→Q5. Ручная сверка показала, что Q1 определяет отказоустойчивость, Q2 перечисляет паттерны, Q4 объясняет Circuit Breaker, Q5 описывает его состояния. Пересечение терминов связано с соседними темами; варианты отвечают своим вопросам. Проверка скелета подсветила неодинаковое число синтаксических частей во всех 43 блоках; вручную сохранены различия, нужные для списков, API и ошибочных моделей. Этот диагностический результат не считаем независимым принятием.

Факты и версии сверены с [документацией Resilience4j](https://resilience4j.readme.io/docs/getting-started-3), [Micrometer](https://resilience4j.readme.io/docs/micrometer), [Spring Boot](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html) и [Kubernetes probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/). Версионные ограничения и остальные первичные ссылки перечислены в авторском отчёте и fact-freshness файле. Независимый экзаменатор не проверял этот проход; статус FINAL требует отдельного принятия.
