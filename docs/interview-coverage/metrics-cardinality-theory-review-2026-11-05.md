# Авторская проверка теории: cardinality и MeterFilter

Дата: 2026-11-05. Scope — только Q17 и Q39 из 41 вопросов
`cheatsheets/interview/monitoring/metrics-tracing-interview.md` и парные
Q17:B0/Q39:B0 целиком: вопрос, A/B/C/D и все sections. Остальные
39 вопросов **NOT REVIEWED** этим пакетом. Ключи известны; это
не независимая слепая MCQ-приёмка и не `FINAL`.

## Снимок

- Исходная теория SHA-256: `947335356865c39590d36dcd2eb0a4b7a2f238635a00af7a0de472522a3b3f95`.
- Текущая теория SHA-256: `f236dc3cbf301adf245cbb54f821f5c55452e66f41c6e79887f227472250dd16`.
- Paired JSON: `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`.
- Seed SHA-256: `d1ff750807150bcca6a22676b5207afa5baf4128f4d9778bb2a667cbcc758423`.

Прочитан [отчёт MCQ Q17/Q25](../mcq-quality/llm-first/reviews/monitoring/metrics-tracing-q17-q25-cardinality-debugging-2026-11-05.md).
Его замечания Q17/Q39 относятся к исходному снимку теории выше.
Рабочее содержимое до правки сохранено в
`/tmp/metrics-cardinality-theory-20261105/before/`.

## Закрытые замечания теории

1. **F-PRODUCT — RESOLVED.** В Q17 произведение мощностей label values
   названо верхней оценкой при фиксированных имени/ключах labels;
   равенство требует всех сочетаний. Наборы `(GET,/a)`, `(GET,/b)`,
   `(POST,/a)` дают 3 при произведении 4. Q39 ссылается на эту границу.
2. **F-DENY — RESOLVED.** Комментарий о замене сверхлимитного URI на
   `other` удалён. Q39 объясняет DENY регистрации, возврат NOOP и
   отбрасывание измерений; нормализация через map/replaceTagValues
   описана отдельно. Предел одного тега не назван глобальным пределом
   всех рядов Prometheus; 100 — иллюстративная настройка.
3. **F-FAILURE-RANKING — RESOLVED.** Неподтверждённый рейтинг
   «самая частая причина падения» удалён из обеих секций. Сохранены
   расходы и возможная перегрузка, без гарантированного OOM.
4. **F-HISTOGRAM-SUPPORT — RESOLVED_THEORY.** Q17 теперь прямо отличает
   дополнительные bucket-ряды classic histogram от составного sample
   native histogram. Это даёт учебную опору для различия в MCQ Q17:B;
   независимая оценка достаточности и повторный linkage tool **NOT RUN**.
5. **F-QUERY-MEANING — RESOLVED.** Q39 разделяет число рядов на endpoint
   и число различных непустых endpoint. Instant query не выдан за все
   historical series или точный объём TSDB в памяти. Удалены универсальные
   числовые нормы тегов и необоснованная оценка GB/OOM из прежнего примера.

## Проверка выполнения

Математический контрпример проверен множествами Python: 3 наблюдавшихся
пары, по 2 значения в каждой проекции, произведение 4. Это не запуск TSDB.

Отдельный Java harness использует локально закэшированные Micrometer
**1.13.2** core/commons/observation, без загрузки зависимостей; компиляция
и исполнение на Corretto **22.0.2**. Проверены:

- первые 100 URI регистрируют meters;
- 101-е новое значение возвращает NoopCounter, его increment не меняет
  count и не создаёт `other`;
- уже разрешённый URI продолжает записываться;
- отдельный replaceTagValues объединяет `/api/users/123` и `/api/users/456`
  в один meter с `uri=/api/users/{id}` и суммарным count 2.

Результат: **PASS**. Это Counter/SimpleMeterRegistry, а не запуск
Spring Boot HTTP instrumentation или Prometheus exporter. Исходная
временная обвязка имела две исправленные ошибки: экранирование Java regex
и ошибочное предположение об AutoCloseable у SimpleMeterRegistry.
Сами фрагменты документации не использовали try-with-resources, а regex
в Q17 уже был экранирован правильно. Финальная проверка прошла после
исправления обвязки. Код и stdout:
`/tmp/metrics-cardinality-theory-20261105/runtime/validation-summary.json`.

## Осталось владельцу MCQ: Q39

Q17:B0 согласован с исправленной формулой и classic/native distinction.
Q39:B0 не принимался исходным авторским пакетом Q17/Q25 и сохраняет
старые формулировки; JSON этим агентом не редактировался:

- **D.sections.what_actually / how_it_should_be:** утверждение, что
  число buckets не относится к cardinality, слишком широко. У classic
  histogram `le` добавляет ряды; нужно различить его и native representation.
- **C.sections.when_to_apply:** фиксированные числовые диапазоны объявлены
  допустимыми, ID — «никогда». Проверенная теория использует бюджет
  сочетаний, scope и измеряемые расходы, а не универсальную таблицу.
- **C.sections.edge_cases:** пояснить, что `count by(endpoint)` считает
  ряды внутри группы; для разных значений нужен внешний count.
  Упомянутый MeterFilter с DENY отбрасывает измерения, не агрегирует их.
- **A.sections.what_actually и B.sections.if_it_were_true:** убрать
  безусловные противопоставления и формулировку «он рушит TSDB»;
  высокая cardinality создаёт риск, а не обязательный исход.

Ключ C соответствует основному определению. Эти notes — содержательные
findings/уточнения sections, не закрытие SFPL/ANSC или формальная
приёмка блока. Владелец должен перечитать весь Q39 после изменений.

## Источники и границы

Открыты первичные источники на 2026-11-05:

- [Prometheus data model](https://prometheus.io/docs/concepts/data_model/).
- [Naming/cardinality](https://prometheus.io/docs/practices/naming/).
- [Classic/native histograms](https://prometheus.io/docs/practices/histograms/).
- [PromQL aggregation operators](https://prometheus.io/docs/prometheus/latest/querying/operators/#aggregation-operators).
- [Micrometer Meter Filters](https://docs.micrometer.io/micrometer/reference/concepts/meter-filters.html),
  текущая страница маркирована **1.17.2**. Её версия отличается от 1.13.2
  локального runtime harness; тест не заявляется запуском 1.17.2.

verify-mcq-json для paired файла: PASS, 41 Q / 41 блок; это структурная
проверка. Scope изменения MD проверен сравнением с сохранённой копией
и HEAD: только Q17/Q39, все прочие секции сохранены. Номера, IDs,
локальные ссылки, fences, frontmatter и адресный diff --check проверены.
Promtool локально не найден; выполнение PromQL **NOT RUN**. Полный
проект, сервис Prometheus, exporter, OOM/нагрузка, native histogram
runtime и независимая приёмка **NOT RUN**. Docker отказ не обходился.

Пакет перед адресным коммитом: MD и этот отчёт. Список и SHA:
`/tmp/prepared-metrics-cardinality-theory-20261105.json`.
MCQ JSON, общие планы/checkpoints/registries и чужие изменения
в пакет не включаются; публикация и развёртывание не выполнялись.
