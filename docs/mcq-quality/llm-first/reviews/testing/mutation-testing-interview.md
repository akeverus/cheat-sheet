# Mutation Testing — полный аудит и авторские исправления, 2026-11-05

**NEEDS_THEORY_SYNC / NEEDS_REVIEW**, FINAL **NOT_RUN**. Прочитаны все 20 видимых вопросов с теорией, 20 блоков и 80 вариантов с полными sections. Исправлены тексты 68 вариантов и sections всех 80; ключи, номера, порядок и question_text сохранены. Строгая независимая Phase A/B не присвоена.

## Проверяемые снимки

- Исходный seed: `58581da551d15246d845a4ffe37b8502dfaead38914799581cebc8f008a9b29b`.
- Исправленный seed: `3c146c9646152338a204912bd77b94787a1452254e0cbb19af0bb0539f807459`.
- Прочитанная теория: `866981af351b35c2dfa7eb0e4c0c9f9d5eac05b93675e834eb8c954fc944acde`; Markdown этим исполнителем не менялся.
- Fact-freshness TODO: `1440c219eee8319e3763c1fd54c2b13621c3e2873156fb73201ef1bea28e49a8`; проверенные источники: `ae77869aa62bfe5a943d749273455e0e72617e0666432f1cf2f809337458b089`.
- Находки до правок: `mutation-peer-original-findings.json`, SHA `36a00d25aaea5e01fc4b6ce321438d1254d7151ea81feeea40a64248a12b0368`.

Рабочие артефакты находятся в task-2, исходные файлы сохранены как `mutation-peer-baseline-*`. Никакие изменения проекта, сервиса, сети или приложений не требуются для воспроизведений ниже. Пакет содержит только MCQ, factual sidecar и этот отчёт.

## Решение до раскрытия ключа

Сначала из источников экспортированы только видимые заголовки и A/B/C/D без correct, sections и ответов теории. Вход `mutation-unmarked-input.json`, SHA `b3d3984c040ef0b990c433624d459f72d860da7ac58272123bf3a4b82a62cdc9`. Снимки seed/theory сохранены без вывода ключей в модельный контекст.

Выбор всех 20 блоков записан **до раскрытия**: `mutation-unmarked-predictions.json`, SHA `c9897de94ad2773b3b157505443e6844e151050f87813f5fb32b3b871089e576`; фактическое время `2026-11-05T17:58:42.929541+00:00` закреплено в recorded-manifest. После этого открыты ключи и полные материалы.

12 определённых выборов совпали с ключами; Q5/Q7/Q8/Q11/Q20 отмечены AMBIGUOUS, Q14/Q15 — NO_VALID_OPTION, Q13 — UNSURE. Это не точность20/20. Предварительные подозрения затем проверены по источникам; они не выданы за автоматический вердикт. Экспорт/решение выполнены в существующем контексте, поэтому это **unmarked content assessment**, не требуемый workflow новый контекст независимого Phase A. После собственных исправлений исполнитель также не может выдать себе independent FINAL. Дополнительные агенты и чужие чаты не запускались.

## Главные дефекты исходного материала

1. **Q5 имел второй защитимый ответ D.** В [PIT1.30.2 boundary mutator](https://raw.githubusercontent.com/hcoles/pitest/1.30.2/pitest/src/main/java/org/pitest/mutationtest/engine/gregor/mutators/ConditionalsBoundaryMutator.java) у подходящей инструкции один counterpart. `>=→==/!=` не относится к CONDITIONALS_BOUNDARY. Описанное в теории опровержение D ошибочно. Новый D проверяет конкретную неверную подмену, ключ B сохранён. Также `check(17)` не различает `>=18` и `==18`; это делает19.
2. **Q11 отвергал существующий standalone CLI.** [Официальный quickstart](https://pitest.org/quickstart/commandline/) поддерживает его, хотя рекомендует build plugin. Новый C ошибочно запрещает Maven, поэтому перестал быть вторым допустимым способом запуска. Ключ B сохранён.
3. **Q8 смешивал теоретическую поправку и практический отчёт.** Новый блок явно использует [модель Stryker](https://stryker-mutator.io/docs/mutation-testing-elements/mutant-states-and-metrics/): detected/valid с timeout среди detected. Эквивалентные мутанты не удаляются автоматически. Утверждения «100% невозможны» и «100% доказывают корректность» убраны. Ключ B сохранён.
4. **Q13 отрицал компиляцию/байт-код CPython.** [Документация dis](https://docs.python.org/3/library/dis.html) и локальный probe это опровергают. [mutmut3+](https://mutmut.readthedocs.io/en/latest/) изменяет исходники и поддерживает selection/reuse; из этого не следует обязательность ночного запуска. Ключ A теперь описывает механизм вместо ложного объяснения медленности.
5. **Q14 спрашивал субъективный рейтинг зрелости.** Вместо необоснованного превосходства PIT/Stryker введены проверяемые соответствия: [Gremlins—Go](https://gremlins.dev/latest/), [Infection—PHP](https://infection.github.io/), [Cosmic Ray—Python](https://cosmic-ray.readthedocs.io/en/latest/). Ключ D сохранён.
6. **Q15 выдавал N*T за универсальное равенство времени.** Новый D описывает число фактических проверок и overhead. Наивная оценка1000*30сек≈8.33часа сохранена только как ограниченная модель.

## Охват остальных исправлений

| Q | Проверенный итог |
|---|---|
| 1–2 | First-order модель, coverage против detection. Нет обещания полного suite для каждого мутанта, surviving=слабые тесты или убийства только explicit assertions. Слабый int/non-null пример объяснён boxing. |
| 3 | Killed, survived, no coverage и timeout разведены; equivalent не представлен как автоматически известный engine status. |
| 4 | Семейства операторов сохранены, несколько инструкций/операторов на строке не означают обязательных нескольких boundary-мутаций. |
| 6 | [PIT MATH](https://raw.githubusercontent.com/hcoles/pitest/1.30.2/pitest/src/main/java/org/pitest/mutationtest/engine/gregor/mutators/MathMutator.java) IMUL→IDIV отделён от AOR. Различающие входы(5,4) и совпадающие(0,2) вместо обещания убийства любым точным assert. |
| 7 | Void deletion наблюдаема без обязательного mock. [RemoveConditional](https://raw.githubusercontent.com/hcoles/pitest/1.30.2/pitest/src/main/java/org/pitest/mutationtest/engine/gregor/mutators/RemoveConditionalMutator.java) имеет true/false варианты; неверный C теперь явно ограничен forced-true. |
| 9–10 | Triage не меняет знаменатель автоматически. Конечная выборка не доказывает equivalence. Пара `<10→<=9` меняет знак и константу; одиночная `<10→<=10` меняет число итераций. |
| 12 | [StrykerJS thresholds](https://stryker-mutator.io/docs/stryker-js/configuration/) high/low задают цвет, break — отказ ниже порога. Равенство порога не нарушает; отсутствие break не отменяет другие ошибки запуска. |
| 16 | Test selection, worker reuse и history отделены от выкидывания no-coverage из метрики. [PIT history](https://pitest.org/quickstart/incremental_analysis/) учитывает изменения кода/тестов и имеет ограничения инвалидации. |
| 17–18 | Риск и стоимость вместо универсальных процентов/исключений категорий. Score не доказывает историю TDD; рост сравнивается при сопоставимом scope и операторах. |
| 19 | Анализ может использовать integration-тесты; он не ограничен unit и не создаёт отсутствующие проверки требований. |
| 20 | CI-policy — вариант по бюджету, не обязательное расписание. [StrykerJS incremental](https://stryker-mutator.io/docs/stryker-js/incremental/) требует прежний report для reuse; diff-only выбор scope — другой механизм. Изменённые тесты могут инвалидировать прежний killed. |

Все sections теперь объясняют механизм самостоятельно. Удалены круговые доводы «в теле сказано иначе» и непроверенные обещания количества мутантов, скорости или доказанной правильности. Это авторский содержательный проход, не строгая приёмка исправлений.

## Воспроизведения и пределы

На кешированных Jupiter5.10.3/Platform1.10.3 и JDK21 выполнены **8/8 semantic probes PASS**. Они проверяют различие boundary только на равенстве, ошибочную роль17 для ==18, различающие/совпадающие арифметические входы, исключение без value assertion, число итераций у одиночной и двухкомпонентной подмены, int identity+0/-0 на объявленных примерах, void side effect без mock и forced-true/false условие.

Отдельно исходный Q2 Java-тест перенесён дословно в предоставленную fixture: **1/1 PASS** при исходном5 и **1/1 PASS** при999. Это один и тот же тест в двух запусках, не два новых разных теста. Coverage при этом не измерялось; утверждение исходного текста о100% coverage не считается доказанным запуском.

Три Python-проверки: существующий CPython создаёт code object/байт-код; арифметическая модель documented detected/valid даёт70%; модель со всеми detected допускает100%. Последние две — проверка арифметики формулы, не исполнение Stryker.

Evidence: `run_mutation_semantic_checks.py`, `mutation-semantic-runtime-checks.json`, `mutation-semantic-runtime/*.java`; сохранены исходные SHA, дословный Q2-блок, JAR paths, команды, stdout/stderr и версия Python. Варианты Java изменены вручную для probes, **не сгенерированы PIT**. PIT JAR не найден в проверенном Gradle cache. PIT/Maven/plugin/Stryker/mutmut/CI runtime, performance и матрица версий **NOT_RUN**. Зависимости не устанавливались и не загружались. Утверждения PIT закреплены по исходникам **1.30.2**, это не версия проекта или исполненного движка. Для JUnit Platform проверена необходимость [pitest-junit5-plugin](https://github.com/pitest/pitest-junit5-plugin) на classpath инструмента, без установки/запуска.

## Вспомогательные проверки

Исходный файл прошёл все семь pre-checks, включая linkage0/0. Это не выявило второго верного ответа и неправильных фактов.

После правок: schema20 PASS, answer parity PASS (max1.21 Q5, style0.271), anti-tell0/20, SKEL pre-check0 hard/0 warnings, struct/surface exit0. **Linkage exit1: 1 error / 3 warnings / 4 manual-review blocks** на неизменённой старой теории866981af. Error Q12 StrykerJS — точное имя JS/TS-продукта, описанного в вопросе как Stryker; не новый неподтверждённый инструмент. Warnings: Q5 C (мутация литерала), Q18 B/D (сопоставимость конфигураций), Q20 A (изменение тестов). Они разобраны по первичным источникам выше, но не превращены в independent PASS и не скрыты override.

Сохранены первые FAIL-логи duplicate ngrams/absolute markers. Формулировки Stryker сделаны менее повторными и конкретнее, пороги и скрипты не менялись. Финальные логи `mutation-author-commit-snapshot-check-{0..6}.log`, сводка `mutation-author-commit-snapshot-checks.json`.

## Передача владельцу теории

Все20 разделов требуют сверки с новым MCQ. Сначала исправить Q5/Q6/Q7 (точные таблицы/направления), Q8/Q9/Q10 (реальные формулы/эквивалентность/пример цикла), Q11 (CLI, JUnit5-plugin, history и неподтверждённый fast mode1.6), Q13 (байт-код и версия CLI), Q14 (объективные экосистемы), Q19 (integration не запрещена).

Затем уточнить пролог и Q1–4: surviving не автоматически означает плохие тесты, полный suite не обязателен, timeout не тождествен имени killed. Q12 — точные роли high/low/break и остальные ошибки запуска. Q15–18 — ограниченная модель времени, исключения no-coverage, стоимость вместо универсальных процентов и невозможность доказать TDD-процесс одним score. Q20 — scope/diff против history reuse, persisted cache и отсутствие универсального расписания. Старое `mutmut html` и конфигурационные имена проверять по выбранной версии, а не объявлять автоматически актуальными либо удалёнными. Intro-ссылка PITclipse названа IntelliJ-плагином — принадлежность также требует исправления/проверки.

Не повышать тему до LLM_READY/AUDITED/FINAL по зелёным скриптам. Завершить теорию, заморозить пару и провести независимые Phase A/B в новом контексте с фиксированными предсказаниями.
