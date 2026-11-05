# Mockito — подготовленный follow-up Q27/Q35, 2026-11-05

**AUTHOR_FOLLOWUP / NEEDS_REVIEW.** Эта группа подготовлена без staging, пока индекс был у теории. После `da0c8e618151d7b228150792a6f91e2f09ec46ef` получена передача индекса для адресного сохранения общего MCQ-пакета. Собственная авторская сверка не является независимой приёмкой исправленного MCQ; Phase A/B и FINAL **NOT_RUN**.

Точный MCQ до/после: `226229acf2538d894a1d22f990b739b89ebaee1cdba84e12d2a5cd66471bdb6c` → `9a730182df2886458740bac88738e4eef2f187a4afe49e8bbaf78ac755d99a09`. Прочитана теория Q27/Q35 снимка `40741bdab8242305aa3c5faf1c4414c1c8b330ee074ea55a00d2dc3de853a374`, все восемь вариантов/sections, а также полный текущий Q39 со всеми четырьмя вариантами. Прочитаны отчёты [strict/reset](../../../../interview-coverage/mockito-theory-strict-reset-review-2026-11-05.md) и [construction/deep](../../../../interview-coverage/mockito-theory-construction-deep-review-2026-11-05.md). Результаты чужих probes не выданы за собственный запуск.

## Семь изменённых полей

- Q27 B `what_actually` и `how_it_should_be`: промежуточные моки создаются для подходящих типов; generic-информация и MockMaker ограничивают цепочку. Убрано безусловное «моки на каждом уровне».
- Q27 C `what_actually` и `how_it_should_be`: совместимые вызовы переиспользуют промежуточный mock, но разные аргументы могут выбирать разные ветки; произвольный граф и отсутствие NPE не гарантируются.
- Q35 A `if_it_were_true`: контрпример привязан к закрытию control и новому Gateway в том же потоке, без неверного ограничения лексическим блоком try.
- Q35 B `example`: пояснение согласовано с новым полным примером Gateway/Service в теории.
- Q35 D `how_it_should_be`: ordinary new перехватывается в потоке активной регистрации, включая helper; нужен гарантированный close, а не текстовое расположение new внутри try.

Тексты вариантов, ключи, порядок, идентификаторы и question_text побайтно сохранены. Markdown не менялся. Основание: [ReturnsDeepStubs 5.11.0](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsDeepStubs.java), [ScopedMock 5.11.0](https://raw.githubusercontent.com/mockito/mockito/v5.11.0/src/main/java/org/mockito/ScopedMock.java), а также ранее проверенные собственные probes.

## Q39 — JSON уже учитывает замечания

В текущем MCQ B.edge_cases прямо сказано: captor технически работает в stubbing, а argThat можно сочетать с times без ручного счётчика. D объясняет getAllValues/последнее getValue. Поэтому варианты Q39 повторно не редактировались. В теории снимка 40741... всё ещё есть неверные «ArgumentCaptor тут не работает» и «требует счётчика вручную»; их исправляет владелец Markdown. Два собственных captor probes (stubbing и сохранение ссылки вместо снимка объекта) ранее входили в авторский набор 29 tests; в этом проходе они не перезапускались. Новые 3 probes владельца теории остаются его отдельным evidence.

## Выполнено здесь

Оба Java-блока Q27/Q35 извлечены из снимка 40741... дословно. **2/2 Jupiter tests PASS**, 0 skipped/failed на JDK21, Mockito5.11.0, Jupiter5.10.3, ByteBuddy1.14.18. Один проверяет идентичность промежуточных deep mocks и null для String; другой — перехват Gateway в helper того же потока, constructed/context/verify и восстановление настоящего конструктора после close.

Evidence в task-2: `run_mockito_construction_deep_checks.py`, `mockito-construction-deep-runtime-checks.json`, `mockito-construction-deep-runtime/*.java`. Runtime запуск был до семи текстовых правок и закреплён на исходном MCQ226229ac; Java-источник теории тот же. Он не является новым запуском всех 45 вопросов, Spring, Kotlin, full project или матрицы makers/JVM.

Диагностика подготовленного MCQ9a730... и теории40741...: schema45 PASS; parity PASS (max1.21 Q29), anti-tell0/45, SKEL pre-check0/0; struct/surface exit0. **Linkage exit1: 9 errors / 21 warnings / 25 manual review.** Сохранены Q4→Q3, Q6→Q42, Q7→Q1, Q14→Q1, Q19→Q27, Q21→Q1, Q29→Q28, Q31→Q3, Q33→Q28. Новый Q19→Q27 появился после расширения теории Q27; этот пакет не объявляет его закрытым. Пороги, скрипты и статусы приёмки не изменялись. Логи `mockito-construction-deep-check-{0..6}.log`, сводка `mockito-construction-deep-checks.json`.

После этой группы подготовлены ещё 12 полей Q17–Q19, сохранившие Q27/Q35 целиком. Текущий MCQ — `2cc16d5379c06898bc20e70d96fd289465ea6bb46591dd9e1e68a751b173c6ef`; суммарно 19 полей sections в пяти вопросах. Актуальная сверка, исправление теории Q19/Q39 и диагностика перед общим адресным коммитом находятся в [matcher follow-up](mockito-matcher-followup-review-2026-11-05.md). Промежуточные SHA и числа выше остаются историческими снимками. В общий пакет входят MCQ и оба follow-up отчёта. Phase A input не менялся: перед использованием нужно сверить заголовки окончательной теории и заморозить пару. Независимая приёмка не выполнена.
