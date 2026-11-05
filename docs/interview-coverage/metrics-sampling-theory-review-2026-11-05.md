# Peer review: sampling в metrics/tracing

Дата проверки: 2026-11-05. Это ограниченный peer review теории, не формальная MCQ-приёмка и не `FINAL`.

## Scope и материалы

Проверены только изменённые Q9, Q24, Q27, Q28 и Q37 из `cheatsheets/interview/monitoring/metrics-tracing-interview.md`, diff относительно `HEAD`, а также соответствующие вопросы и секции в paired JSON `modules/quiz-app/src/main/resources/seed/mcq/monitoring/metrics-tracing-interview.json`. Остальные 36 вопросов темы не проверялись (`NOT REVIEWED`).

SHA-256 на момент чтения:

- Theory MD (after Q24 revision): `947335356865c39590d36dcd2eb0a4b7a2f238635a00af7a0de472522a3b3f95`
- Paired JSON: `1cec89fa6673606da2a99056eeb98d23d20ee1671f82c8217ebd4dbf6265c15e`

## Матрица

| Вопрос | Итог | Проверка содержания и примеров | Paired JSON / форма | Finding |
|---|---|---|---|---|
| Q9 | В целом корректно | Разделяет вероятностную выборку общего трафика и смещённую выборку по ошибкам/latency. Уточняет, что изменение sampling rate не возвращает уже отброшенные данные. Пример Java ratio sampler иллюстративный, доля не подаётся как универсальная норма. | Вопрос и дистракторы различимы; секции про отбор и смещение полезны. В правильной опции C, `explanation`, tail sampling описан как сохраняющий ошибки/выбросы без оговорки о потерях доставки и capacity. | `F-Q9-MCQ-OVERPROMISE` — см. ниже. |
| Q24 | Корректно после уточнения | Перечитанное предложение теперь правильно разделяет SDK limits для числа атрибутов/событий и `attributes` processor, удаляющий атрибут уже после получения данных Collector. Остальной раздел корректно разделяет head/tail sampling, SDK limits и фильтрацию; указано, что Collector-фильтр не сокращает приложение→Collector трафик, удаление отдельного span может нарушить полноту, tail sampler не гарантирует каждую ошибку. Числа config примера помечены иллюстративными. | Ключ B согласуется с вопросом; sections содержат место применения и ограничения. Ложные альтернативы проверяют ошибки категории/места обработки. | Устаревшая отсылка к `span processor` устранена в theory MD. |
| Q27 | Корректно с ограничением охвата метрик | Retention отделён от sampling; span metrics до tail sampling дают агрегаты только из поступивших spans, не восстанавливают дерево, а SDK head sampling может сделать их неполными. YAML показывает fan-out traces в connector и storage pipeline. | Ключ A и sections согласованы; caveat о head sampling и смещении метрик присутствует. Название `span_metrics` соответствует текущей рекомендации README. | Нет отдельного finding. |
| Q28 | Корректно | Сравнение head/tail sampling привязано к моменту решения, затратам на состояние/память и маршрутизации trace spans к одному sampler. Есть ограничения по ранее отброшенным spans, eviction и неверному routing. | Ключ A и пояснение говорят «может отбирать» по исходу, без абсолютного обещания; edge cases покрывают полноту и capacity. | Нет отдельного finding. |
| Q37 | Корректно после уточнения | Перечитывание новой версии подтверждает: названы `decision_wait` и ускорение по ненулевому `decision_wait_after_root_received` после root span; явно сохранена оговорка, что интервалы не гарантируют полноту trace. Комментарий baseline policy и строка таблицы latency теперь точнее. Буферизация, `num_traces`, политики и требование маршрутизировать все spans одного trace на тот же Collector изложены верно. Config snippet правдоподобен статически. | Ключ A формулирует возможность отбора, а не гарантию; sections прямо перечисляют late/missing spans, restart, eviction и неправильный load balancing. | `F-Q37-STRATEGY-DETAIL` — RESOLVED в обновлённом theory MD. |

## Findings

### F-Q9-MCQ-OVERPROMISE — правильная MCQ-секция звучит как гарантия

Поле: paired JSON `questions[q_number=9].blocks[0].options[label=C].sections.explanation`.

Цитата: «tail-based сохраняет ошибки и выбросы».

В актуальной теории Q9 используется более точное «может отбирать ошибки и выбросы». Tail sampler видит только поступившие spans; решение ограничено доступной частью trace и capacity. Поэтому формулировка MCQ-секции может обещать больше, чем система гарантирует.

Предлагаемая замена: «tail-based может отбирать поступившие ошибки и выбросы, если нужные spans дошли до sampler и trace не был вытеснен».

### F-Q37-STRATEGY-DETAIL — RESOLVED

Ранее замечание относилось к первому абзацу theory MD `Q37`.

Цитата: «В стандартной стратегии `trace-complete` решение обычно принимается после `decision_wait`».

В новой версии абзац явно говорит, что ненулевой `decision_wait_after_root_received` может ускорить решение после root span, и что интервалы ожидания не гарантируют получения всех spans или завершения trace. Комментарий базовой вероятностной policy больше не подразумевает исключительность для остальных, а строка задержки решения учитывает ускорение. Замечание закрыто по прочитанному diff; это не запуск Collector.

## Статическая сверка Collector и границы

Проверены названия и синтаксическая структура YAML на соответствие открытым примерам README, а также семантика ключей/политик. В Q24/Q37 наборы политик ERROR, latency и probabilistic согласуются с policy types. Q27 показывает корректную концептуальную разводку spans до tail sampling в `span_metrics` и отдельно в storage pipeline; компоненты при этом нужно определить в полном Collector config. README connector отмечает текущую форму `span_metrics` и изменение default duration unit в будущей версии. Пример не запускался, схема полного Collector distribution не валидировалась; результата runtime-проверки нет.

Tail Sampling Processor README на текущем `main` указывает: default `trace-complete`; `decision_wait` default 30s; `decision_wait_after_root_received` default 0s может ускорить решение; `num_traces` default 50000; все spans одного trace должны прийти на один collector instance. Также описаны кеши решений и потеря trace при переполнении/раннем вытеснении. Поэтому значения `10s`, `100000`, `1000` в примерах — пользовательские иллюстративные настройки, а не значения по умолчанию.

## Источники

Открыты 2026-11-05:

- [OpenTelemetry Sampling concepts](https://opentelemetry.io/docs/concepts/sampling/) — роль head/tail sampling и диагностические компромиссы; версия документации не закреплена, страница текущая на дату проверки.
- [OpenTelemetry Trace SDK specification, Sampling](https://opentelemetry.io/docs/specs/otel/trace/sdk/#sampling) — SDK sampling; открытая спецификация маркирована `OTel 1.62.0`.
- [Collector Contrib Tail Sampling Processor README](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md) — routing, strategies, timing options, policies, capacity/decision cache. Ветка `main` подвижна; версия не закреплена.
- [Collector Contrib Span Metrics Connector README](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/connector/spanmetricsconnector/README.md) — `span_metrics`, pipeline type и предупреждение об изменении default duration unit. Ветка `main` подвижна; версия не закреплена.

## Ограничения

Проверка — ручной review текста, diff, paired JSON и официальных страниц. Collector не запускался; примеры проверены статически, без проверки конкретной сборки/distribution и без нагрузочного теста. Проверены только 5 указанных Q; оставшиеся 36 — `NOT REVIEWED`. Вердикт не является MCQ `FINAL` и не меняет paired JSON или теорию.

## Авторская сверка после a79a71db6

Исторический finding F-Q9-MCQ-OVERPROMISE выше относится к seed со SHA
`1cec89fa6673606da2a99056eeb98d23d20ee1671f82c8217ebd4dbf6265c15e`.
После изменения владельцем MCQ в `a79a71db6` координатор теории полностью
перечитал текущий Q9:B0: вопрос, все A/B/C/D и sections. В explanation C
теперь указаны поступившие span'ы, политики, состояние и маршрутизация;
edge_cases покрывает ранее отброшенные/потерянные данные, late spans
и вытеснение. Прежнее безусловное обещание устранено.

Текущий seed SHA-256:
`cf3e17224f34218e4554dcbed46a096c8791d2bbeccb03fe08d0e7955f4e7252`.
SHA теории не изменился. Статус finding: **RESOLVED_AUTHOR_REREAD**.
Это дополнение координатора, а не независимая слепая MCQ-приёмка.
Первоначальный peer review и его снимок выше сохранены.
[Отчёт владельца MCQ](../mcq-quality/llm-first/reviews/monitoring/metrics-tracing-q9-sampling-2026-11-05.md).
Runtime Collector/SDK по-прежнему **NOT RUN**; JSON этим агентом
не редактировался и не включается в коммит теории.
