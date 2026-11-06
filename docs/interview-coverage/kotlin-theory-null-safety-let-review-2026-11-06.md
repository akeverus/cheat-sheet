# Kotlin Q4/Q6: null-safety, Elvis и let

Исправлены только тела Q4 и Q6. Все 45 заголовков, metadata, оглавление и
остальные 43 ответа сохранены. Production MCQ JSON побайтно не менялся.
Проверены все восемь вариантов и 34 пояснения; MCQ Q4/Q6 остаются OPEN,
FINAL темы не объявлен.

## Исправленная теория

Q4 объясняет static nullability на конкретных `String`/`String?`, не обещая
ненулевость любого generic `T`. Верхняя граница без ограничения — `Any?`;
`T : Any` исключает nullable type arguments. Самостоятельная программа
определяет User и все используемые значения, показывает safe call, Elvis,
smart cast, safe cast и `?.let`. Различаются compiler proof и runtime-assertion
`!!`, стабильный receiver и nullable extensions.

NPE возможен и без Java — например через `!!`. Для Java уточнены relaxed
platform types, display-only обозначение `String!` и распознанные nullability
annotations; не все Java references остаются platform types.

Q6 исправляет подмену `let` и `?.let`: обычный let передаёт receiver, включая
null, и возвращает результат блока. Guard обеспечивает safe call. Два
самостоятельных примера показывают Elvis/return, обычный и безопасный let,
а также fallback при null-результате блока с ненулевым исходным receiver.
Правая часть Elvis вычисляется лишь при null всей левой части; исключение
из блока им не перехватывается.

Первоисточники:
[Null safety](https://kotlinlang.org/docs/null-safety.html),
[let API 2.4](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/let.html),
[Scope functions — let](https://kotlinlang.org/docs/scope-functions.html#let),
[Generic upper bounds](https://kotlinlang.org/docs/generics.html#upper-bounds),
[Java platform types/annotations](https://kotlinlang.org/docs/java-interop.html#null-safety-and-platform-types).
[Source ledger](evidence/kotlin-null-safety-let-2026-11-06/sources.json).

## Качество вопросов и всех вариантов

| Блок | Факты | Полнота |
|---|---|---|
| Q4 | D без видимого ограничения объявляет любой T ненулевым; это неверно для unbounded generic T. Подразумеваемый concrete non-null T не определён. | Broad вопрос допускает разные модели; узость Java-only C отмечена отдельно, без подмены ANSC истинностью. |
| Q6 | C ошибочно приписывает let проверку null; A верен для обычного let, но не отвечает про Elvis. | C явно описывает оба понятия, хотя одна часть ложна. A/B/D описывают только одно. |

**Нельзя просто назначить A ключом Q6:** его фактическая истинность не делает
его полным ответом на составной вопрос. Нужна переработка всех четырёх
вариантов в полноценные модели обоих понятий и последующая синхронизация
всех sections. В Q6 C единственный двухчастный ответ, что даёт структурную
подсказку к текущему ключу независимо от его фактической ошибки.

Точные замечания:
Q4 D.explanation повторяет generic-обобщение; D.edge_cases нужно ограничить
реальными platform types. В Q6 A.what_actually и C.explanation ложны,
A.how_it_should_be корректно описывает `?.let`. D.if_it_were_true («fallback
всегда») не следует из wrong D, задающего лишь non-null ветку. Остальные
поля разобраны отдельно в [авторских 34 суждениях](evidence/kotlin-null-safety-let-2026-11-06/author-review.json).
Отдельному section не навязано требование повторять обе части вопроса:
оно должно корректно выполнять собственную объясняющую роль.

## Независимая проверка

Свежий контекст **gpt-6-luna / medium / fork_turns=none** получил только
blind-input и выбрал **Q4 D / Q6 C**. CLI запечатал A до отдельного reveal;
сообщение агента о записи файла само по себе не считается квитанцией.
После раскрытия реально прочитаны оба Q, все 8 опций и 34 точных поля,
актуальная теория и первичные источники. Незавершённый пустой ответ не засчитан.

Первоначальное ревью содержало противоречия. Raw сохранён, а отдельные
exact-text follow-ups исправляют:

- Q6 A фактически верен для обычного let, но неполон; C полон по двум понятиям,
  но неверен. Guard приписан `?.`, а не let. Правильный пример не спасает
  неверную безусловную формулу видимого ключа.
- Q4 C.what_actually верен именно для platform types; nullable annotations
  для иных Java declarations не опровергают это поле. C.source_of_confusion
  называет заблуждение, не утверждает его как факт.
- У D Q4 нет слова compiler, оно есть у B; A тоже сравнивает T с вопросительным
  типом. Выдуманный уникальный SFPL-признак отозван. Независимый SFPL после
  этого остаётся OPEN для новой адъюдикации, а не превращается в PASS.

В одном follow-up слишком сильно сказано, что wrong D Q6 «подразумевает не
всегда RHS». Точное логическое заключение слабее: null-ветка им не определена,
поэтому «всегда RHS» из него **не следует**. Эта граница сохранена в
[coordinator summary](../mcq-quality/runs/20261106T054822Z-kotlin-null-safety-let-blind-r1/coordinator-review-summary.json).
Исходные выборы и отчёты не переписаны, совпадение букв не выдано за приёмку.

## Реальные проверки и ограничения

- Все три точных fenced-примера скомпилированы и выполнены.
- Support-программа проверила nullable generic T, non-null bound, smart cast
  после `checkNotNull`, nullable extension, обычный/безопасный let, ленивый
  Elvis RHS (включая ненулевую пустую строку), throw, Kotlin-only NPE,
  null-результат transform и распространение исключения блока.
- Собственный неаннотированный Java LegacyApi скомпилирован с `--release 17`:
  null безопасно принят в `String?`, но dereference platform-result и
  адаптация к ненулевому результату Kotlin дали ожидаемые NPE.
- Пять программ получили ожидаемые compile errors: null для String,
  прямой nullable length, nullable аргумент для bound Any, нестабильный
  smart cast mutable property и unbounded T как non-null Any.
- Первоначальный support-probe ошибочно рассчитывал на smart cast только
  от initializer у явно nullable val. Kotlin 2.4.10 его отверг. Исходник
  и диагностика сохранены, harness исправлен явным доказательством через
  checkNotNull; текст теории такого обещания не содержал.

Итого **5 успешных JVM-программ и 5 ожидаемых compiler rejections**.
Kotlin **2.4.10**, compiler JRE **26.0.2**, bytecode target **17**, runtime
Microsoft OpenJDK **21.0.10**. Annotations mapping проверен по источнику,
а fixture был unannotated. Приложение, JS/Native и матрица версий не запускались.
[Runtime receipts](evidence/kotlin-null-safety-let-2026-11-06/runtime-results.json).

Schema: 45 Q / 45 блоков / 180 опций; YAML: 9 полей PASS. Linkage:
0 ошибок/предупреждений; anti-tell, verify-md-no-mcq, diff check и current-run
block/rules/input guards PASS. Сканы не заменяют перечисленные дефекты.

## Передача MCQ и зависимости

Q5/Q12/Q33/Q34 bodies и весь seed не менялись; связи с Q4/Q6 и Q33 перечитаны,
новых противоречий не внесено. У текущего Q34 вновь устарела явная зависимость
на SHA **всего Markdown**, обновлённая владельцем после предыдущего пакета.
Её надо перепроверить/зарегистрировать против нового SHA, даже при неизменном
теле Q34. [Доказательство зависимостей](evidence/kotlin-null-safety-let-2026-11-06/dependency-revalidation.json).

[Точный handoff](evidence/kotlin-null-safety-let-2026-11-06/mcq-owner-handoff.json)
содержит per-Q/block SHA, обязательные исправления и остаток. После изменения
вариантов нужны новый blind A/B и зависимые FACTS/LINKAGE/SECTIONS/ANSC/SFPL/RU.
`record`, `sync`, registry, checkpoint и PLAN этой задачей не менялись.
[Манифест](evidence/kotlin-null-safety-let-2026-11-06/manifest.json).

Markdown SHA-256:
`8ef4ec5d29dc5fb2364432d4a331da5c284cd140dff2f27cc6fc7648eb5812c0`
→ `4e494e1c7e7993bd9a682bf34d07d41d4300458ed223078f21f466a6630fff8a`.
Production MCQ SHA-256 без изменений:
`96b2c37a7dc146b9242a489e8f8f9d7056426fd1ae4f50e2d89948697196fd71`.
