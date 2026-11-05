# Metrics Q5 — фильтрация trace и синхронизация теории

Дата: 2026-11-05. AUTHOR_VERIFIED, Q5:B0, с доступным ключом. Повторно прочитаны актуальная теория Q5, A/B/C/D и sections, а также параллельный diff теории Q2–Q5. Ключ B, тексты вариантов, question_text, номера и порядок не менялись. Независимая приёмка и FINAL NOT_RUN.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`, SHA-256 `38b3fc8454ecacd220435e781b90618f83f7be06f5a12fb803d50f0040604afe`.

Теория: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, SHA-256 `03a8b6eba467c3df61b6e7b6c37345ac98a114682fe6bc16b0f76d80848db398`. Markdown изменён параллельной задачей; этот автор его не редактировал.

После первого авторского прохода в правильном example оставалась неточность: «фильтруют медленные по traceId». Исправлено разделение действий: по длительности выбирают медленные запросы, по известному trace ID открывают конкретный trace, затем сравнивают наблюдаемые span’ы. Контрфактическое пояснение wrong D теперь показывает отсутствие внутренней детализации, не приравнивая одиночный ingress span к агрегированной HTTP-метрике. A остаётся моделью репликации логов, C — профилированием стека одного процесса, D — наблюдением только границы; единственный ключ B сохраняется.

Новый diff теории Q2–Q5 прочитан: scrape failure не отождествлён с остановкой процесса; registry отделён от доступного endpoint; Summary sum/count отделены от локальных квантилей; histogram quantile назван оценкой; полнота trace ограничена инструментацией и доставкой. Поэтому прежние четыре пункта OPEN из [отчёта Q1–Q6](metrics-tracing-q1-q6-foundations-2026-11-05.md) разрешены по авторскому чтению данного снимка теории. Прежний отчёт остаётся историческим с собственными SHA. Это не независимая приёмка примеров Boot/Prometheus и не заявление о готовности всей темы.

verify-mcq-json PASS, 41 Q / 41 блок; semantic review NOT_RUN. Q5 не имеет findings anti-tell/linkage/SKEL. По теме целиком диагностические FAIL остаются; пороги не менялись. Источник и логи: metrics-q5-filtering-followup-checks.json и check-0..6.log в рабочем пространстве. Prometheus, Spring Boot, Java, фильтры UI и Collector не запускались; работающий сервис и данные не менялись. Новых независимых Phase A/B evidence нет.

Первичные источники и предмет сверки: [OpenTelemetry traces](https://opentelemetry.io/docs/concepts/signals/traces/), [context propagation](https://opentelemetry.io/docs/concepts/context-propagation/), [Prometheus histograms and summaries](https://prometheus.io/docs/practices/histograms/), [Spring Boot metrics](https://docs.spring.io/spring-boot/3.5/reference/actuator/metrics.html). Новый пример отбора — различение идентификатора и длительности по прочитанной теории, а не результат запуска backend-поиска.
