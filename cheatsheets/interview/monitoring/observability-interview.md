---
title: "Вопросы на собеседовании: Observability"
description: "Практичные вопросы и ответы по observability для Senior Java Developer: метрики, логи, трейсы, SLO/SLI, стоимость и эксплуатация в production."
tags: ["interview", "monitoring", "observability-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Observability`

Практичные вопросы и ответы по observability для `Senior Java Developer`: метрики, логи, трейсы, SLO/SLI, алертинг и работа в production.

Дата последнего обновления: 2026-02-11

## Полезные ссылки

### Официальная документация

- [OpenTelemetry](https://opentelemetry.io/docs/)
- [Prometheus](https://prometheus.io/docs/introduction/overview/)
- [Grafana](https://grafana.com/docs/)

### См. также

- [`metrics-tracing-interview.md`](metrics-tracing-interview.md) — метрики и распределенный трейсинг
- [`logging-strategies-interview.md`](logging-strategies-interview.md) — стратегии логирования
- [`../logging/logging-interview.md`](../logging/logging-interview.md) — logging interview
- [`../../monitoring/metrics/prometheus.md`](../../monitoring/metrics/prometheus.md) — практики Prometheus
- [`../../monitoring/logging/structured-logging.md`](../../monitoring/logging/structured-logging.md) — structured logging
- [`../../monitoring/tracing/distributed-tracing.md`](../../monitoring/tracing/distributed-tracing.md) — distributed tracing

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Как отвечать на интервью**
- [Q1. Как коротко объяснить observability и отличие от мониторинга?](#q1-как-коротко-объяснить-observability-и-отличие-от-мониторинга)
- [Q2. Как построить сильный ответ на вопрос по observability?](#q2-как-построить-сильный-ответ-на-вопрос-по-observability)

**Метрики, логи, трейсы**
- [Q3. Как связаны три столпа observability?](#q3-как-связаны-три-столпа-observability)
- [Q4. Какие метрики критичны для backend-сервиса?](#q4-какие-метрики-критичны-для-backend-сервиса)
- [Q5. Как организовать связку traceId между логами и трейсами?](#q5-как-организовать-связку-traceid-между-логами-и-трейсами)
- [Q6. Что такое exemplars и когда они реально полезны?](#q6-что-такое-exemplars-и-когда-они-реально-полезны)

**SLO/SLI и алертинг**
- [Q7. Как объяснить SLI, SLO и error budget на практике?](#q7-как-объяснить-sli-slo-и-error-budget-на-практике)
- [Q8. Как проектировать алертинг без alert fatigue?](#q8-как-проектировать-алертинг-без-alert-fatigue)

**Production и стоимость**
- [Q9. Как запускать observability в production без перегруза системы?](#q9-как-запускать-observability-в-production-без-перегруза-системы)
- [Q10. Как управлять стоимостью логов и трейсов?](#q10-как-управлять-стоимостью-логов-и-трейсов)
- [Q11. Как выбрать стек observability под команду и бюджет?](#q11-как-выбрать-стек-observability-под-команду-и-бюджет)

**Инциденты и эволюция**
- [Q12. Как observability помогает в инциденте и постмортеме?](#q12-как-observability-помогает-в-инциденте-и-постмортеме)
- [Q13. Как внедрять observability в CI/CD и релизный процесс?](#q13-как-внедрять-observability-в-cicd-и-релизный-процесс)
- [Q14. Какие anti-patterns в observability встречаются чаще всего?](#q14-какие-anti-patterns-в-observability-встречаются-чаще-всего)
- [Q15. Как оценить зрелость observability в команде?](#q15-как-оценить-зрелость-observability-в-команде)

## Q1. Как коротко объяснить observability и отличие от мониторинга?

`Monitoring` отвечает на заранее известные вопросы: "вышли ли мы за порог?".  
`Observability` позволяет расследовать неизвестные заранее сценарии: "почему конкретный поток запросов деградировал?".

Хорошая короткая формула:
- мониторинг = detection,
- observability = detection + diagnosis.

## Q2. Как построить сильный ответ на вопрос по observability?

Рабочий шаблон на 40-60 секунд:
1. **Симптом:** "растет p99 latency и error rate".
2. **Путь диагностики:** "дашборд RED -> trace -> логи по traceId".
3. **Решение:** "ограничили cardinality метрик, поправили retry, снизили tail latency".
4. **Валидация:** "p99 с 900ms до 280ms, 5xx с 2.8% до 0.4%".

Именно измеримый результат отличает сильный senior-ответ от теории.

## Q3. Как связаны три столпа observability?

- **Метрики** отвечают на вопрос "что происходит массово".
- **Трейсы** показывают "где в цепочке возникла задержка".
- **Логи** дают "почему это произошло".

Практика: по алерту на метрику переходите к trace, затем фильтруете логи по `traceId`.

## Q4. Какие метрики критичны для backend-сервиса?

Минимальный базовый набор:
- `RED`: `request_rate`, `error_rate`, `request_duration` (p50/p95/p99),
- saturation: `cpu`, `memory`, queue depth, pool utilization,
- бизнес-метрики: успешные оплаты, конверсия, отказ транзакций.

Антипаттерн: только технические метрики без бизнес-контекста.

## Q5. Как организовать связку traceId между логами и трейсами?

- включить `OpenTelemetry` auto/manual instrumentation,
- пробрасывать контекст между сервисами и async-цепочками,
- писать `traceId` и `spanId` в structured logs (`JSON`),
- в UI (Grafana/Kibana) обеспечить переход лог <-> trace.

Проверка готовности: можно за 1-2 клика перейти от алерта к конкретному stack trace.

## Q6. Что такое exemplars и когда они реально полезны?

`Exemplar` связывает точку на графике метрики с конкретным трейсом.  
Это особенно полезно при редких всплесках latency, когда нужно быстро найти "плохой" запрос.

Когда польза максимальна:
- есть гистограммы latency,
- есть трассировка с достаточным sampling,
- команда реально расследует инциденты через метрики.

## Q7. Как объяснить SLI, SLO и error budget на практике?

- `SLI`: измеритель качества (например, доля 2xx/3xx).
- `SLO`: цель по SLI (например, 99.9% успешности за 30 дней).
- `Error budget`: допустимая доля деградации.

Практический смысл: когда бюджет исчерпан, команда замораживает risky-релизы и инвестирует в надежность.

## Q8. Как проектировать алертинг без alert fatigue?

Принципы:
- алертить по пользовательскому impact, а не по шумным низкоуровневым метрикам,
- разделять severity (page / ticket / info),
- добавлять runbook к алерту,
- делать dedup и suppression.

Anti-pattern: сотни алертов "на всякий случай", которые никто не обслуживает.

## Q9. Как запускать observability в production без перегруза системы?

- sampling для трейсов (база + приоритет ошибок),
- разумные уровни логирования (`INFO`/`WARN`, без массового `DEBUG`),
- контроль cardinality меток,
- асинхронная отправка телеметрии и backpressure.

Нужен баланс: слишком мало данных ломает расследования, слишком много данных ломает бюджет и latency.

## Q10. Как управлять стоимостью логов и трейсов?

Рабочие рычаги:
- tiered retention (hot/warm/cold),
- фильтрация низкоценных событий,
- отдельные политики для `ERROR`/`INFO`,
- tail-based sampling для редких и дорогих операций.

Отчетность: стоимость наблюдаемости должна быть видна рядом с SLA/SLO, иначе оптимизация не приоритизируется.

## Q11. Как выбрать стек observability под команду и бюджет?

Типовая логика выбора:
- small/medium: `Prometheus + Grafana + Loki + Tempo`,
- enterprise/multi-team: managed платформа с governance,
- гибрид: метрики self-hosted, логи/трейсы managed.

Критерии: стоимость владения, зрелость команды, интеграции, безопасность, требования по хранению.

## Q12. Как observability помогает в инциденте и постмортеме?

Во время инцидента:
1. фиксируем impact по SLI/бизнес-метрикам,
2. локализуем деградацию по trace/service map,
3. подтверждаем причину логами.

В постмортеме:
- добавляем контрольные метрики и алерты,
- закрываем пробелы в runbook,
- фиксируем preventative actions с дедлайнами.

## Q13. Как внедрять observability в CI/CD и релизный процесс?

- на pre-prod проверять обязательные метрики/логи/трейсы для новых endpoints,
- при canary сравнивать SLI новой и старой версии,
- rollback по guardrail-метрикам,
- хранить связь release -> dashboard -> trace samples.

Так observability становится частью качества релиза, а не "послерелизной задачей".

## Q14. Какие anti-patterns в observability встречаются чаще всего?

- "Собираем все подряд" без целей и owner'ов.
- Логи без структуры и без `traceId`.
- Метрики с огромной cardinality (`userId`, `requestId` как label).
- Алертинг без runbook.
- Нет связки с бизнес-метриками.

## Q15. Как оценить зрелость observability в команде?

Краткая шкала:
- **L1:** есть графики, нет actionable алертов,
- **L2:** есть SLI/SLO и runbooks,
- **L3:** кросс-функциональная диагностика метрики+логи+трейсы,
- **L4:** observability встроена в SDLC и релизы.

Практический критерий зрелости: среднее время локализации инцидента снижается, а не растет вместе с масштабом системы.
