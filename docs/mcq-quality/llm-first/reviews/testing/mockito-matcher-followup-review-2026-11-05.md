# Mockito — matcher/captor follow-up Q17–Q19, 2026-11-05

**AUTHOR_FOLLOWUP / NEEDS_THEORY_SYNC / NEEDS_REVIEW.** Содержательные изменения подготовлены без staging, пока индекс был у теории; затем индекс передан MCQ после `da0c8e618151d7b228150792a6f91e2f09ec46ef` для адресного сохранения пакета. Подготовленный ранее [Q27/Q35 пакет](mockito-construction-deep-followup-review-2026-11-05.md) сохранён побайтно в своих двух вопросах. Автор MCQ проверяет собственные исправления, поэтому независимые Phase A/B и FINAL **NOT_RUN**.

Исходная проблема Q19: **прежний второй Java-пример с assertions внутри argThat не являлся безопасной заменой boolean matcher.** Если первая фактическая save передаёт PENDING, а вторая CONFIRMED, assertion на первой прерывает verification до подходящего второго вызова. Это воспроизведено с дословным телом прежнего примера и согласуется с контрактом [ArgumentMatcher 5.11.0](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/ArgumentMatcher.java): несовпадение выражается false. **Владелец теории уже исправил Q19 в снимке bcd78017: новый полный пример прошёл 2/2 теста дословно.** Старый дефект больше не предъявляется как текущий. Markdown этим исполнителем не редактировался.

## Снимки и изменения

Прочитаны полные видимые ответы Q17/Q18/Q19, все 12 вариантов и sections; Q27 использован для сравнения нового соседнего сигнала. Исходная теория для matcher probes — `40741bdab8242305aa3c5faf1c4414c1c8b330ee074ea55a00d2dc3de853a374`. В ходе работы владелец теории обновил Q39; полный новый ответ повторно прочитан на снимке `e14c8040742cb80e6597a79e0b5b2259282b472ef6db585e60f56a878eb1c5bb`.

- MCQ перед новым проходом: `9a730182df2886458740bac88738e4eef2f187a4afe49e8bbaf78ac755d99a09` — уже включал семь подготовленных полей Q27/Q35.
- После: `2cc16d5379c06898bc20e70d96fd289465ea6bb46591dd9e1e68a751b173c6ef`.
- Последняя закоммиченная версия MCQ: `226229acf2538d894a1d22f990b739b89ebaee1cdba84e12d2a5cd66471bdb6c` в `a7ad78643eb89b033b57f125923a24253b836dc2`.

Новые **12 полей sections**: Q17 A `if_it_were_true`, C `what_actually/how_it_should_be`; Q18 C `if_it_were_true`, D `example/edge_cases`; Q19 A `what_actually/if_it_were_true/how_it_should_be`, B `if_it_were_true/how_it_should_be`, C `edge_cases`.

Уточнены регистрация captor и чтение после фактического захвата; отсутствие встроенного getValue у argThat; runtime matcher validation в отличие от компиляции; согласованность matchers для всех аргументов вызова; varargs Mockito5 и null-safe boolean matcher. Убраны круговые контрпримеры про «раз существует captor, значит иначе невозможно». Предикат не обязан превращать mismatch в assertion, а исключение из него не считается false.

**Суммарно незакоммичено 19 полей sections в Q17/Q18/Q19/Q27/Q35** относительно 226229ac. Все варианты, ключи, порядок, идентификаторы и question_text сохранены. Предыдущие Q27/Q35 сравнивались целиком и не изменились. После передачи индекса адресный пакет должен включать актуальный MCQ и **оба** follow-up отчёта; старый hash9a730 относится к промежуточному подготовленному снимку, а не текущему файлу.

## Десять выполненных probes

JDK21, кешированные Mockito5.11.0/Jupiter5.10.3/ByteBuddy1.14.18/AssertJ3.25.3. Javac/JVM exit0, **10/10 outer tests PASS, 0 skipped/failed**:

1. Дословный второй Q19 Java-блок встроен в явно заданные Orders/Order fixtures. На PENDING перед CONFIRMED ожидаемо бросает AssertionError; внешний assertThrows подтверждает дефект. Это **одно ожидаемое внутреннее падение**, а не PASS исходного примера.
2. Чистый boolean matcher на тех же двух вызовах пропускает PENDING и находит CONFIRMED.
3. Null-unsafe matcher пробрасывает NPE при lookup стаба.
4. Null-safe условие работает в stubbing и verification.
5. argThat вместе с times(2) считает два подходящих вызова без ручного счётчика.
6. Смешение raw аргумента с matcher даёт runtime InvalidUseOfMatchersException; вариант anyLong/eq работает.
7. В Mockito5.11 any() для varargs соответствует одному элементу, не всему массиву: проверены 0/1/2 элемента.
8. any(String[].class) соответствует всему ненулевому varargs-массиву: проверены 0/1/2 элемента и null-массив.
9. getValue до захвата сообщает об отсутствии аргумента; после verify значение доступно.
10. getValue возвращает последний, getAllValues — список захваченных аргументов.

Источники: [ArgumentMatchers 5.11.0](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/ArgumentMatchers.java), [ArgumentCaptor 5.11.0](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/ArgumentCaptor.java), [TypeSafeMatching](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/internal/invocation/TypeSafeMatching.java). Это одна проверенная версия и конфигурация. Вариант primitive intThat упомянут по API/source, отдельный null-unboxing probe не выполнялся. Не запускались full project, Spring, Kotlin, матрица makers/JVM, performance или пользовательский сервис.

Evidence в task-2: `run_mockito_matcher_checks.py`, `mockito-matcher-runtime-checks.json`, `mockito-matcher-runtime/*.java`. Сохранены SHA дословного блока, классов и источников, точные JAR/команды/stdout/stderr. Runtime закреплён на MCQ9a730 **до** двенадцати текстовых уточнений; исходный Java-блок и теория те же. Первый успешный запуск пяти probes и его классы сохранены отдельно как `mockito-matcher-runtime-first-checks.json` и `mockito-matcher-first-runtime/`; число 10 включает прежние пять, а не прибавляется к ним.

После параллельного обновления Q39 его единственный полный Java-блок извлечён дословно из теорииe14c8 и выполнен отдельно: **2/2 tests PASS**, 0 failed/skipped, MCQ2cc16. Они проверяют captor history/последнее значение и argThat в stubbing плюс times(2). Это отдельный запуск, не часть числа 10. Evidence: `run_mockito_q39_checks.py`, `mockito-q39-runtime-checks.json`, `mockito-q39-runtime/`. Результат подтверждает конкретный пример другого автора и не является независимой приёмкой собственных вариантов MCQ.

## Диагностика и оставшиеся ограничения

Для текущего MCQ2cc16 и теорииe14c8: schema45 PASS, answer parity PASS (max1.21 Q29), anti-tell0/45, SKEL pre-check0/0; struct/surface exit0. **Linkage exit1: 10 errors / 21 warnings / 26 manual review.** Q19→Q27: own0.237, neighbor0.368. Все четыре варианта Q19 прямо проверяют argThat: отсутствие captor API, исключения предиката, произвольное условие и работу в verify. Для них не нужен ответ о deep stubs. Новый Q17→Q39 (own0.367, neighbor0.5) возник после расширения теории выбора matcher/captor; Q17 варианты по-прежнему относятся к захвату и чтению аргументов. Это ручная авторская оценка конкретных пересечений, не независимый PASS и не override scanner. Остальные восемь сигналов не объявляются закрытыми; скрипты/пороги/статусы не менялись.

Логи `mockito-matcher-check-{0..6}.log`, сводка `mockito-matcher-checks.json`; неизменяемый вход `mockito-matcher-before.json`, находки до правок `mockito-matcher-findings.json`, точный список правок `mockito-matcher-prepared.json`.

Дополнительные задачи владельцу теории: Q17 явно указать инициализацию @Captor (Extension/openMocks или явная фабрика), отличить ссылку от snapshot объекта и обычную рекомендацию verify от технической невозможности stubbing; Q18 объяснить правило всех аргументов, placeholders и границы varargs Mockito5. Q19 и Q39 уже исправлены владельцем теории; повторно предъявлять прежние дефекты не следует. До завершения остальных разделов и свежего независимого review пара не считается принятой.

## Контроль перед адресным коммитом

Текущий MCQ2cc16 сохранён, теория обновлена до `bcd78017c353f4ceb69c3366558138da5d8fddf0ae5535173fefa4d076115461`. Полный новый Q19 прочитан; его единственный Java-блок содержит два теста, извлечён дословно и дал **2/2 PASS**, 0 failed/skipped. Evidence: `run_mockito_q19_updated_checks.py`, `mockito-q19-updated-runtime-checks.json`, `mockito-q19-updated-runtime/`. Старое ожидаемое падение и прежние десять probes остаются историческими доказательствами, не считаются запуском нового примера. Java-блок Q39 побайтно совпал с ранее проверенным ArgumentChoiceTest; его повторно не запускали.

Новая семёрка diagnostics `mockito-matcher-commit-snapshot-check-*` закреплена на MCQ2cc16/теорииbcd78017: schema/parity/anti/SKEL/struct/surface без новых отказов; **linkage exit1: 9 errors / 21 warnings / 26 manual review**. Исчез прежний Q19→Q27. Остаются Q4→Q3, Q6→Q42, Q7→Q1, Q14→Q1, Q17→Q39, Q21→Q1, Q29→Q28, Q31→Q3, Q33→Q28. Они не превращены в independent PASS. Проверяемый адресный пакет: MCQ и два follow-up отчёта; теория и прочие изменения в коммит не включаются.
