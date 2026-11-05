# Source peer review: Kotlin null safety и equality

Дата проверки: 2026-11-05. Это ограниченный peer review теории, не формальная MCQ-приёмка и не `FINAL`.

## Scope и материалы

Проверены только изменённые Q5 и Q30 в `cheatsheets/interview/programming-languages/kotlin/kotlin-interview.md`, diff относительно `HEAD`, и соответствующие blocks в paired JSON `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/kotlin/kotlin-interview.json`. Проверены 2 из 45 вопросов; остальные 43 — `NOT REVIEWED`.

SHA-256 на момент чтения:

- Theory MD: `08e44bc63166213a2d76e13f2d2dae4a3ce644e64363007119db7590dd545448`
- Paired JSON: `755b5f5745fc8eed459f34622a964f1f7548761549fe76450ee01c469c857492`

## Матрица

| Вопрос | Итог | Source review теории | Paired JSON / finding |
|---|---|---|---|
| Q5 | Корректно | `?.` при null пропускает доступ/вызов и возвращает null; `!!` утверждает non-null и на Kotlin/JVM при null заканчивается NPE. Kotlin 1.4.0 унифицировал runtime null-check типы к `java.lang.NullPointerException` для `!!`, платформенных проверок и ряда других случаев; это утверждение ограничено Kotlin/JVM и не распространяется без оговорки на все null-related exceptions (`lateinit`, например, исключён). Рекомендации `?:`, `requireNotNull`, `checkNotNull`, раннего return соответствуют назначению. | Ключ A и его секции согласованы с теорией; edge case верно говорит о runtime null checks с Kotlin 1.4. Исправлений не требуется. |
| Q30 | Корректно с точным условием для примитивов | `==` выполняет null-safe вызов `equals` (`a?.equals(b) ?: (b === null)`). Обычные классы наследуют идентичностный `Any.equals`, а data classes автоматически переопределяют `equals` по primary-constructor properties (если унаследованное final-переопределение не меняет поведение). `===` — идентичность ссылок. Формулировка о совпадении `===` и `==` точно оговорена условием: когда значения представлены примитивами во время выполнения. Это не следует распространять на nullable/boxed или generic представления, где сравниваться могут ссылки. Для массивов `contentEquals()` — правильная операция сравнения содержимого. | Ключ B и контраст `Point`/`Box` согласуются с теорией. В paired JSON точное условие о runtime representation сохранено. Конкретных defects не найдено. |

## Findings

Actionable findings по двум блокам не выявлены. Для Q30 важно не расширять точную формулировку про runtime primitives до «всех значений `Int`»: nullable или generic значения могут быть boxed, и тогда `===` проверяет идентичность ссылки. Q30 MD и paired JSON уже ограничивают утверждение представлением primitive во время выполнения; правка не нужна.

Отдельно о floating point: Kotlin equality docs указывают, что `==` для операндов, статически известных как `Float`/`Double` (nullable или non-nullable), следует IEEE 754; для иных статических типов применяется структурное равенство с отличиями для NaN и signed zero. Текст Q30 не даёт отдельного объяснения этих различий и не утверждает, что `===` является способом сравнивать числовые значения; это разумное ограничение общего ответа, а не ошибка в проверенных формулировках.

## Источники

Открыты 2026-11-05:

- [Kotlin Null safety](https://kotlinlang.org/docs/null-safety.html) — safe-call и `!!`; актуальная страница документации, версия не закреплена.
- [What's new in Kotlin 1.4.0 — Unified exception type for null checks](https://kotlinlang.org/docs/whatsnew14.html#unified-exception-type-for-null-checks) — release note с датой релиза 17 августа 2020 и изменением JVM runtime null checks на `java.lang.NullPointerException`, включая исключения из правила.
- [Kotlin Equality](https://kotlinlang.org/docs/equality.html) — `==`, `===`, `Any.equals`, data classes/value classes, runtime primitive representation, floating-point and array equality; страница помечена датой обновления 29 сентября 2026.

## Ограничения

Runtime не запускался. Это source/text review по двум изменённым вопросам, diff, paired JSON и официальным Kotlin docs. Проверены 2 из 45 Q; остальные 43 — `NOT REVIEWED`. Вывод не является MCQ `FINAL`; MD, JSON и индекс не менялись.
