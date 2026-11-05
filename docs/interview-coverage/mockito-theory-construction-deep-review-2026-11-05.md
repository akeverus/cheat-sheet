# Mockito — deep stubs и construction scope

Дата: 2026-11-05. `interview-writer / SEMANTIC_UPDATE`, ответы **Q27/Q35**. Продолжение [группы reset/strictness](mockito-theory-strict-reset-review-2026-11-05.md). Суммарно исправлены **15 из 45** ответов; остальные 30 этим набором не исправлялись. Проверка авторская, с открытыми ключами, без новой independent acceptance, blind Phase A/B или FINAL. JSON и индекс не редактировались.

Вход: HEAD `a7ad78643eb89b033b57f125923a24253b836dc2`, Markdown SHA `16f6c26ff5603fd2f8bb98d6f2508c0f1d8ebd84912a47bdbf05d9857b6c8be0`; seed SHA **`226229acf2538d894a1d22f990b739b89ebaee1cdba84e12d2a5cd66471bdb6c`**. Прочитаны оба ответа и все восемь вариантов MCQ с sections. В конце SHA seed совпал; выбранные Q27/Q35 не менялись. Итоговый Markdown SHA **`40741bdab8242305aa3c5faf1c4414c1c8b330ee074ea55a00d2dc3de853a374`**.

## Исправления

Q27 больше не обещает новый промежуточный mock при каждом обращении. Совместимые вызовы переиспользуют созданные ответы; возможность продолжить цепочку зависит от типов, generic-информации и maker. Показан полный тест с сохранением идентичности, конечной verification и null для String. Убрано обещание ненулевого значения любого типа и безусловное объявление длинной цепочки ошибкой дизайна.

Q35 использует собственный конкретный Gateway вместо неоднозначного HttpClient. Полный тест проверяет initializer/context arguments, список constructed, пропуск тела конструктора и настоящее создание после close. Helper-вызов в том же потоке также перехватывается. Scope определяется потоком и временем жизни control; try-with-resources — способ гарантировать close, а не единственный разрешённый lifecycle. Уточнены границы поддерживающего maker и допустимого типа.

Первоисточники **Mockito 5.11.0**: [ReturnsDeepStubs](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsDeepStubs.java), [MockedConstruction](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockedConstruction.java), [ScopedMock](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ScopedMock.java), [Mockito.mockConstruction](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/Mockito.java), [InlineDelegateByteBuddyMockMaker](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java).

## Исполнение и границы

**9 tests PASS / 0 failed**, javac и JVM exit 0 с первого запуска. Два полных Java-блока Markdown скопированы без изменений и дали два успешных теста. Семь дополнительных probes проверили:

- разные аргументы дают отдельные переиспользуемые ветки deep stubs;
- String/Object возвращают null, primitive — 0, Optional — empty; продолжение через null даёт ожидаемый NPE;
- реальные методы deep-stubbed объекта не выполняются;
- worker не наследует construction registration main-потока; main и создание после close ведут себя ожидаемо;
- явный close в finally работает, helper перехватывается, ранее существовавший объект не заменяется;
- повторная активная construction registration того же класса в одном потоке даёт ожидаемый MockitoException;
- inline maker отклоняет construction targets abstract, Object и int.

Конфигурация: JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Agent указан только при старте дочерней JVM, настройки приложений не менялись, зависимости не устанавливались и не загружались. Проверена одна inline-конфигурация, не матрица makers/JVM. Full project/Gradle/CI, Spring, Kotlin, performance, serialization/generics matrix и рефлексивное создание этим набором не исполнялись.

## Согласование и сохранённые сигналы

Ключи Q27=A, Q35=B и основные механизмы согласованы с текущей теорией. Для владельца MCQ остаётся узкое редакционное уточнение **Q27 B.sections.how_it_should_be**: «возвращает моки на каждом уровне» стоит ограничить подходящими возвращаемыми типами, чтобы исправление неверного утверждения о реальных методах не повторяло обещание произвольного mock-графа. Правильный вариант A и вариант D уже содержат ограничение. Это рекомендация сузить объяснение, не изменение ключа и не новая независимая приёмка всей пары.

Schema **PASS 45 questions / 45 blocks**, semantic gate **NOT_RUN**. Whole-topic linkage **FAIL: 9 errors / 21 warnings / 25 manual review**, exit 1. Новые Q27/Q35: **0 errors / 1 warning**, `NOVEL-TERM-WRONG Q35 newInstance`. Дистрактор D утверждает исключительную поддержку рефлексии и отсутствие перехвата обычного new; полный пример прямо опровергает вторую часть. Для выбора неверного варианта не требуется знать отдельный API рефлексии. Его исполнение не заявляется. Идентификатор ради scanner overlap в теорию не вставлялся.

Совокупные шесть новых ответов Q24/Q27/Q28/Q29/Q30/Q35: **1 error / 1 warning**; остаётся Q29→Q28 из предыдущего отчёта. После расширения Q27 появился ещё общий сигнал **Q19→Q27** (own 0.237, neighbor 0.368). Q19 не входит в исправленный набор; новый сигнал сохранён для отдельной содержательной проверки, а не объявлен устранённым. Скрипт, пороги и правила приёмки не менялись.

Структурная проверка: изменены только Q27/Q35; прочие **43** секции побайтно сохранены относительно входа группы, включая предыдущие 13 исправленных ответов. Все 45 заголовков/ID, metadata, TOC и See also сохранены; `git diff --check` — PASS. Хеши шести ранних Java-тестов reset/strictness также сохранены; повторный запуск того же набора после правки других секций не требовался.

Артефакты `/tmp/mockito-theory-construction-deep-20261105/`: before/seed-before/seed-postcheck, web-source-extracts, structural-check, run-checks, runtime с двумя точными файлами и семью probes, execution с SHA JAR/фрагментов, schema/linkage/diagnostic-summary. Frozen-validation содержит точный Markdown, seed и побайтную копию linkage-скрипта с прежними относительными путями. Веб-выдержки не являются полными byte snapshots. Совокупный манифест шести ответов отдельно фиксирует окончательные три файла; исторический манифест четырёх ответов сохранён.

## Следующий подтверждённый дефект Q39

Отдельные три probes, **3 tests PASS / 0 failed**, подтвердили, что captor в stubbing технически захватывает аргумент при последующем подходящем вызове; `argThat` допускает `verify(..., times(2))` без ручного счётчика; captor хранит ссылку на изменяемый аргумент, а не его неизменяемый снимок. Это отдельный запуск `captor-followup/execution.json`, не часть числа 9 и не исполнение примера Q39. [ArgumentCaptor 5.11.0](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentCaptor.java) рекомендует captor для verification по читаемости и локализации ошибок, что следует отличать от заявления о технической невозможности stubbing. В текущем Q39 фразы «ArgumentCaptor тут не работает» и «множественные вызовы требуют счётчика вручную» нуждаются в исправлении. Q39 пока сохранён без изменений и не включён в число 15 исправленных ответов.

Тема целиком **NEEDS_REPAIR**, FINAL **NOT_RUN**. Первоначальный пакет подготовлен без staging, пока индекс находился у MCQ.

## Сверка при передаче индекса

Индекс передан после MCQ-коммита HEAD **`7504de6c1086bad73bb7fd5c3e27babbf72740ad`**. Проверены пустой индекс и SHA трёх подготовленных файлов. Новый seed SHA **`9a730182df2886458740bac88738e4eef2f187a4afe49e8bbaf78ac755d99a09`**: в выбранных шести по per-Q SHA изменились только Q27/Q35. Все изменённые поля перечитаны. Q27 B/C теперь явно ограничивают создание и переиспользование промежуточных моков подходящими типами/вызовами; рекомендация прежнего прохода устранена. Q35 A/B/D согласованы с Gateway/helper, текущим потоком, активностью control и восстановлением реального конструктора после close. Ключи и остальные четыре выбранных вопроса сохранены.

Schema повторно **PASS 45/45**, linkage остаётся **9 errors / 21 warnings / 25 manual review**, выбранные шесть — **1 error / 1 warning** с теми же Q29→Q28 и Q35 newInstance. Хеши всех шести полных Java-примеров совпадают с успешно исполненными; их код не менялся. Повтор того же runtime-набора не требовался. Тема по-прежнему не получила FINAL. Подготовка коммита ограничена теорией и двумя отчётами, JSON не редактировался.

Точный снимок текущего seed, предыдущий манифест/отчёты, результаты schema/linkage и состояние Git сохранены в `/tmp/mockito-theory-six-transfer-20261105/`. Финальные адресные файлы и их SHA перечислены `/tmp/prepared-mockito-theory-six-20261105-v2.json`.
