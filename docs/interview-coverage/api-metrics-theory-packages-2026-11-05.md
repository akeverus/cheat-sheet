# API и sampling: подготовленные пакеты теории — 05.11.2026

Сохранены и проверены прежние изменения трёх шпаргалок. Область проверки
ограничена девятью секциями: OpenAPI Q26/Q41, REST maturity Q8/Q13,
metrics/tracing Q9/Q24/Q27/Q28/Q37. Остальные 91 вопрос этих тем
**NOT REVIEWED**. Это не формальная MCQ-приёмка и не статус `FINAL`.

## API: четыре секции

- Q26: путь, query, заголовок и media type представлены как варианты
  версионирования с разными требованиями к маршрутизации и кэшу.
  Для кэшируемых ответов, зависящих от заголовка версии, приведены
  конкретные `Vary: Accept-Version` и `Vary: Accept`. Описание OAS
  не выдано за реализацию серверного поведения.
- Q41: page/size отделены от keyset, а `Link` — от алгоритма выборки.
  В примерах заданы положительные границы size/limit и обязательное
  `description` у response object. Для keyset названы однозначный
  порядок с уникальным tie-breaker и отсутствие автоматического snapshot.
- Q8: HAL больше не ограничен «только ссылками»; убраны универсальное
  ранжирование форматов и запрет разных медиатипов в одном сервисе.
  Невалидные многоточия в двух JSON заменены конкретными данными.
- Q13: в список safe/idempotent methods добавлен TRACE. Уточнено,
  что POST/PATCH могут быть идемпотентны по контракту конкретной операции.
  Пример ключа учитывает payload, область действия, срок хранения и
  параллельные дубли. Idempotency-Key назван Internet-Draft, а не RFC.

[Ограниченный peer review API](api-contract-theory-review-2026-11-05.md)
фиксирует текущие SHA, источники и связь с четырьмя MCQ-блоками.

## Метрики и sampling: пять секций

Прежние изменения убирают обещания сохранить каждую ошибку, универсальные
доли sampling и сроки retention. Уточнены влияние SDK/Collector на объём,
маршрутизация span'ов одного trace, неполнота поступивших данных и ветка
`span_metrics` до tail sampling. Метрики не представлены как обратимое
сжатие старых трейсов.

Дополнительно Q24 больше не ссылается на отсутствующий `span` processor:
SDK-лимиты атрибутов/событий отделены от удаления атрибута в Collector.
В Q37 названа возможность более раннего решения в `trace-complete`
через ненулевой `decision_wait_after_root_received`; гарантия полноты
не добавлена. Конфигурация остаётся иллюстративным фрагментом.

[Ограниченный peer review sampling](metrics-sampling-theory-review-2026-11-05.md)
содержит источники, SHA, исторический finding парного MCQ и его последующую авторскую сверку.

## Согласование парного MCQ

Прежний finding Q9:B0, `options[label=C].sections.explanation`,
в `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`
устранён владельцем MCQ в `a79a71db6`. Перед коммитом теории текущий блок
перечитан целиком: explanation C ограничивает отбор поступившими данными
и политиками, а edge_cases описывает потери, late spans и вытеснение.
Согласование подтверждено авторской сверкой, не формальной независимой
MCQ-приёмкой. Текущий seed SHA-256:
`cf3e17224f34218e4554dcbed46a096c8791d2bbeccb03fe08d0e7955f4e7252`.
[Отчёт владельца MCQ](../mcq-quality/llm-first/reviews/monitoring/metrics-tracing-q9-sampling-2026-11-05.md).
JSON этим агентом не редактировался и в пакет теории не входит.

Для API Q13 отсутствие TRACE в перечислении примеров MCQ не признано
противоречием: правильный вариант не называет свой список исчерпывающим.
Уточнения границ пагинации и дедупликации расширяют теорию, сами по себе
не меняя ключи четырёх проверенных блоков API.

## Проверки и ограничения

- `bash scripts/verify-mcq-json.sh` с тремя явными seed paths: **PASS**,
  100 вопросов / 100 блоков. Это структурная проверка, семантическое
  качество остальных вопросов из неё не следует.
- Все три JSON-примера Q8 разобраны `json.loads`; шесть YAML-фрагментов
  OpenAPI Q41 и metrics Q24/Q27/Q37 разобраны Ruby Psych. Frontmatter
  трёх файлов также разобран как YAML. Это не полная OAS/Collector
  schema validation.
- Номера Q, явные anchor IDs, локальные ссылки и парность code fences
  сохранены/проверены в изменённых секциях; inline MCQ не добавлен.
  Новые правки ограничены указанными секциями и датой `updated`.
- Адресный `git diff --check` трёх шпаргалок: **PASS**. Reports проверяются
  отдельно перед включением в индекс.
- API server, springdoc Java fragments, HTTP retries, cache/proxy и
  cursor pagination runtime: **NOT RUN**. Collector, SDK export и
  нагрузочные проверки: **NOT RUN**. Docker отказ не обходился.
- Источники processor/connector на `main` подвижны; проверка относится
  к открытому тексту на 2026-11-05, а не подтверждению конкретного
  установленного Collector distribution.

Основные дополнительные источники: [RFC 5789, PATCH](https://www.rfc-editor.org/rfc/rfc5789.html),
[Idempotency-Key draft-08](https://datatracker.ietf.org/doc/html/draft-ietf-httpapi-idempotency-key-header-08),
[PostgreSQL 18: LIMIT/OFFSET и уникальный порядок](https://www.postgresql.org/docs/18/queries-limit.html),
[OpenAPI 3.1.1](https://spec.openapis.org/oas/v3.1.1.html).
Остальные первичные источники перечислены в двух peer reports.

Перед коммитами после передачи индекса проверены актуальный HEAD,
пустой индекс и SHA исходного пакета. Материалы разделены на адресный
коммит API и адресный коммит sampling с этим сводным отчётом. Общие планы,
checkpoints, registries, чужие файлы и MCQ JSON не включаются. Обновлённый
список шести файлов и их SHA-256 сохранён в
`/tmp/prepared-api-metrics-theory-20261105.json`.
