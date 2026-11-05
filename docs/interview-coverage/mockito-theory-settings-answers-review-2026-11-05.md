# Mockito Q36–Q38 — maker, settings и Answers

Дата: 2026-11-05. Узкий `interview-writer / SEMANTIC_UPDATE`: прочитаны три полных ответа и все **12 вариантов MCQ с sections**. Исправлены только Q36/Q37/Q38; суммарно автором исправлены **22 из 45** ответов, другие 23 этим набором не исправлялись. Авторская сверка с открытыми ключами не является independent acceptance, blind Phase A/B или FINAL. JSON и индекс не редактировались.

Входная теория SHA `2d7da2e1cfcf5b7b24507782a7a241b1a6867439bfe75e17577c99f0dc067af6`, выходная SHA **`01484c268dcbb842415d8c895b03ee406138cc15962bd619e46b00ffffa136ec`**. Seed SHA **`2cc16d5379c06898bc20e70d96fd289465ea6bb46591dd9e1e68a751b173c6ef`**, три выбранных вопроса не менялись между начальным и итоговым снимком. Ключи B/D/A и основные механизмы текущих объяснений согласуются с теорией.

## Исправления и источники

Q36 разделяет dependency artifact, default версии и effective maker. Убраны неподтверждённая точная граница публикации старого inline-артефакта, упрощённая историческая таблица движков и неизмеренное ранжирование скорости. Пример зависимости закреплён на исследованной 5.11.0 и не назван последней версией. Полный Java-пример показывает default inline final mock и отказ явно выбранного subclass на том же final-типе без изменения глобальных plugin-файлов. Основание: [Mockito 5.0 release](https://github.com/mockito/mockito/releases/tag/v5.0.0), [MockMakers 5.11.0](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockMakers.java), [MockSettings](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockSettings.java).

Q37 заменяет сочетание всех опций в raw List примером собственного типизированного API. Разделены параметры создания, выполнение custom default Answer и точечное stubbing. Проверены имя, дополнительные интерфейсы, serializable marker и stubOnly без истории/обычного verify. Убрана оценка «99% тестов». Ограничение сериализации относится к графу объектов, а не только marker. Позитивный interface+extraInterfaces+serializable пример и негативный final+extraInterfaces исследованы отдельно: результат одного сочетания не переносится на все типы/makers. [MockSettings 5.11.0](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockSettings.java).

Q38 определяет Answer шире default-поведения. Разделены обычные empty-результаты и диагностический SmartNull, ReturnsMocks и deep reuse, real concrete/default method и abstract default. Для RETURNS_SELF указано отдельное исключение Object.class в 5.11.0. Убраны выдуманное сообщение с фиксированной строкой теста, JDK-builder пример без нужных границ и неподтверждённая частотность стратегий. Источники: [ReturnsSmartNulls](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsSmartNulls.java), [ReturnsMoreEmptyValues](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsMoreEmptyValues.java), [ReturnsMocks](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsMocks.java), [TriesToReturnSelf](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/TriesToReturnSelf.java), [CallsRealMethods](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/answers/CallsRealMethods.java).

## Реально исполнено

**14 tests PASS / 0 failed**, javac/JVM exit 0 с первого запуска. Три полных Java-блока Markdown скопированы без изменений и дали **10** успешных тестов (2 + 3 + 5). Четыре support probes проверили:

- явно выбранный subclass всё ещё работает для обычного класса;
- final-тип с extraInterfaces не является безусловно допустимым сочетанием;
- serializable mock с конкретным custom Answer graph даёт ожидаемый NotSerializableException; marker не доказывает успешность произвольного round-trip;
- SmartNull strategy возвращает null для результата Class и обычный пустой String-массив.

В самих точных примерах проверены SmartNull business-method exception и диагностический toString, empty/default ответы, reuse deep stubs против новых ReturnsMocks, исключение Object из RETURNS_SELF, interface default/abstract methods, собственный default Answer и приоритет совпавшего stub. Ожидаемые исключения проверяются outer assertions; неожиданных падений нет.

Конфигурация: JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Agent передан только при старте дочерней JVM. Настройки приложений и глобальные Mockito provider-файлы не менялись. Зависимости не загружались. Full project/Gradle/CI, Spring, Kotlin/Android/native-image, compatibility/performance/concurrency и полная serialization/maker/version matrix — **NOT_RUN**. XML зависимости не выдаётся за выполненный Maven build.

## Согласование и диагностика

Владельцу MCQ — два узких уточнения Q38: C.source_of_confusion содержит неподтверждённое ранжирование частоты использования; A.edge_cases можно уточнить, что в исследованной версии Object отдельно исключён из RETURNS_SELF даже при assignability. Это не изменение ключа: правильный вариант уже говорит об ограниченном возврате самого mock, а остальные стратегии перечислены верно.

Schema **PASS 45/45**, semantic **NOT_RUN**. Whole-topic linkage **FAIL: 8 errors / 21 warnings / 25 manual-review blocks**, exit 1. Выбранные три — **0 errors / 1 warning**, стилистический STEM-MISMATCH Q38 из-за backticks вокруг Mockito. Правила/пороги не менялись; новая независимая приёмка не присвоена.

Структурный guard: только Q36/Q37/Q38 изменены, остальные **42** секции побайтно сохранены. Все 45 заголовков, ID, metadata, TOC и See also сохранены. SHA трёх исполненных Java-блоков и итоговой теории совпадают с execution. `git diff --check` — PASS.

Артефакты `/tmp/mockito-theory-settings-answers-20261105/`: before/seed-before/seed-postcheck, original-findings **до правок**, три тела, source log, structural-check, runtime с точными классами/runner и execution с SHA JAR/блоков, schema/linkage/diagnostic-summary, frozen-validation с точной парой и неизменённым linkage-скриптом. Веб-выдержки не являются полными byte snapshots. Точные два файла и per-Q SHA перечислены `/tmp/prepared-mockito-theory-settings-answers-20261105.json`.

Тема целиком **NEEDS_REPAIR**, FINAL **NOT_RUN**. Пакет подготовлен без staging; индекс находится у MCQ.

## Сверка при передаче индекса

Доступ к репозиторию подтверждён. После MCQ-коммита HEAD **`9942dd948111f326b937fc7a8bc831a81a850a91`** индекс передан теории и проверен пустым. Текущий seed целиком совпадает с прочитанным 2cc16d53, per-Q SHA Q36/Q37/Q38 и SHA подготовленной теории совпадают. Три Java-блока и результаты 14 тестов относятся к тем же байтам; повторный запуск неизменённого кода не требовался. Авторская сверка и ограничения выше сохраняются.

Коммитный пакет ограничен Mockito Markdown и этим отчётом; JSON, Selenium и другие файлы не включаются. Исходное состояние и точные снимки двух пакетов сохранены в `/tmp/theory-settings-selenium-transfer-20261105/`, финальные адресные файлы/SHA — в `/tmp/prepared-mockito-theory-settings-answers-20261105-v2.json`.
