# Kotlin Q38/Q39: Java API и compiler plugins

Изменены только тела Q38 и Q39. Все 45 заголовков, metadata, оглавление и
остальные 43 ответа сохранены. Production MCQ JSON не менялся. Автор и
отдельный LLM проверили 8 вариантов и 34 точных пояснения. MCQ остаются OPEN:
совпадение blind-выборов с ключом и зелёные сканеры не означают приёмку.

## Изменения теории

Q38 разделяет три механизма: static bridge для companion-функции,
перегрузки с подстановкой defaults и доступ к backing field без
сгенерированных аксессоров. Companion instance method сохраняется.
Фабрика переименована в `createDefault`: прежний `Config.default()`
синтаксически недопустим в Java из-за keyword `default`.

Точный Kotlin-пример и отдельный Java-клиент показывают четыре сигнатуры
`connect` — от нуля до трёх аргументов. Уточнено, что Java вызывает конкретные
JVM-сигнатуры. У all-default constructor есть специальное правило zero-arg
и без `@JvmOverloads`; это не поддержка Kotlin default/named arguments
со стороны Java. Отдельная mixed-default проба не позволила добавить
ошибочное ограничение «только непрерывные trailing defaults».

Q39 различает открытие класса/членов и создание конструктора без аргументов.
Необходимость all-open ограничена соответствующими subclass proxies;
JDK interface proxy не наследует target class. Подходящий constructor может
уже существовать, поэтому no-arg не объявлен обязательным для каждого
класса JPA. Прежняя связанная ошибка про весь Spring AOP
[исправлена в теории](evidence/kotlin-jvm-plugins-2026-11-06/pending-q39-resolution.json).

Новый пример фиксирует Kotlin **2.4.10** и собственную marker-аннотацию.
Generated no-arg constructor доступен Java/reflection, скрыт от прямого
Kotlin-вызова; `invokeInitializers` по умолчанию выключен. При включении
выполняются initializers, поэтому «без аргументов» не означает пустого тела.
По текущей документации kotlin-spring преднастраивает all-open, а kotlin-jpa
применяет **no-arg вместе с all-open**. Это описание текущих presets,
не обещание одинакового поведения всех старых версий.

Источники: [Java interop](https://kotlinlang.org/docs/java-to-kotlin-interop.html),
[JvmStatic](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-static/),
[JvmOverloads](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-overloads/),
[JvmField](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-field/),
[all-open](https://kotlinlang.org/docs/all-open-plugin.html),
[no-arg](https://kotlinlang.org/docs/no-arg-plugin.html),
[kotlin-spring](https://kotlinlang.org/docs/kotlin-spring-plugin.html),
[kotlin-jpa](https://kotlinlang.org/docs/kotlin-jpa-plugin.html),
[Spring proxying](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html).
[Source ledger](evidence/kotlin-jvm-plugins-2026-11-06/sources.json)
отделяет документацию от локальных запусков.

## Проверка каждого варианта и пояснения

Q38 A верен как общее обещание удобного Java API, но не объясняет пользу
каждой названной аннотации. B использует тот же зонтичный охват и обращает
направление интеропа; завершённое ложное предложение не делает его полным.
C/D отвечают только про один механизм. Нужны четыре полные сопоставимые
модели трёх ролей, включая правдоподобные ошибочные модели.

В Q38 A.example и D.if_it_were_true используют невалидный Java-вызов.
B.if_it_were_true не следует из гипотетического обратного назначения.
C.what_actually не содержит оговорки об обычных методах — её нельзя
незаметно заимствовать из исправленной теории. Исключение для constructor
уточняет доступные сигнатуры, а не даёт Java понимание Kotlin defaults.

Q39 C передаёт оба центральных преобразования. Разговорное «пустой»
не трактуется автоматически как ложное обещание пустого тела; требуется
точный термин «без аргументов». B/D не определяют оба плагина. A называет
обе ошибочные purpose-роли, но не сами преобразования. Разница суждений
о достаточности такой ложной модели сохранена; ложность сама по себе
не служит основанием неполноты.

Q39 A.if/B.if — необоснованные контрфактические выводы. C.related/D.if
переносят ограничения subclass proxy на весь Spring AOP. C.example —
узнаваемая схема, но не runnable Kotlin DSL: annotation принимает строку
с полным именем аннотации, а Service/Entity здесь не определены и не строки.
Схему нужно обозначить или исправить. Каждое пояснение оценивается по своей
роли: ему не требуется заново перечислять все плагины/аннотации.
[Авторские 8/34 оценки](evidence/kotlin-jvm-plugins-2026-11-06/author-review.json).

## Независимое ревью и его ограничения

Свежий **gpt-6-luna / medium / fork_turns=none** сначала получил blind-input.
Выборы **Q38 A / Q39 C** запечатаны CLI до отдельного reveal. Reason Q38
дополнил видимый A механизмами из знаний модели, поэтому совпадение буквы
не доказывает полноту текста. Затем проверены четыре варианта и 17 точных
field IDs на вопрос, семь осей, текущая теория и первоисточники.

Raw-отчёты сохранены отдельно от exact follow-ups и итогового суждения:

- Q38 reviewer приписал C.what_actually отсутствующее ограничение ordinary
  methods; ошибка исправлена. Множественное начало есть у A и B, поэтому
  оно не является уникальной подсказкой ключа.
- Q39 reviewer ошибочно назвал D.what_actually неверным, хотя цитата прямо
  говорит OPEN, а не final. Follow-up исправил буквальное прочтение.
- A и C Q39 оба двухчастны. Двухчастность не уникальна для C, а различие
  истинной и ложной модели относится к семантике.
- В follow-up C.example одновременно назван не псевдокодом и схемой с
  неопределёнными именами. Принят конкретный вывод о неготовности к запуску;
  противоречивый ярлык не принят.
- Общая полнота visible option и локальная роль section разделены.
  Неполная опция не делает автоматически ложным корректный related/example.

Технически неудавшаяся попытка записи Q39 не засчитана отчётом. Повторный
blind-проход ради другой буквы не проводился. Авторское суждение, независимый
отчёт и формальная приёмка остаются разными результатами.
[Итог ревью](../mcq-quality/runs/20261106T074815Z-kotlin-jvm-plugins-blind-r1/coordinator-review-summary.json).

## Выполненные проверки

Точные fences Config.kt/JavaClient.java скомпилированы и выполнены.
Проверены static bridge и сохранённый companion method, public static field
без getter, четыре overloads и all-default constructor без аннотации.

Точный Model.kt выполнен с обоими compiler plugins: при выключенных
initializers вывод `0/0`, при включённых — `7/1`. Java source напрямую
создаёт Model в обоих сценариях. Reflection подтвердил форму класса:
оба plugins → non-final + zero-arg; no-arg отдельно → final + zero-arg;
all-open отдельно → non-final без zero-arg.

Итого **10 успешных запусков JVM-сценариев и 7 ожидаемых ошибок компиляции**.
Это число запусков, а не десять разных standalone-программ. Отрицательные
проверки: static call без JvmStatic, direct field без JvmField, getter при
JvmField, пропуск аргумента без overload, произвольное исключение аргументов,
прямой Kotlin-вызов скрытого no-arg и JvmField у computed property без поля.
Предварительные probes сохранены отдельно и повторно в эти 7 не засчитаны.

Kotlin **2.4.10**, compiler JRE **26.0.2**, target **17**, Microsoft JDK
**21.0.10**. Использованы уже установленные compiler plugins; SHA сохранены.
Gradle отсутствует: фрагмент конфигурации проверен по документации,
эквивалентные custom-marker compiler flags выполнены локально. Сам Gradle,
Spring/JPA preset build, приложение Spring, ORM hydration, JS/Native и
матрица версий не запускались.
[Runtime receipts](evidence/kotlin-jvm-plugins-2026-11-06/runtime-results.json).

Schema: 45 Q / 45 блоков / 180 опций; YAML: 9 полей PASS. Linkage:
0 ошибок/предупреждений; anti-tell, verify-md-no-mcq, diff check и current-run
block/rules/input guards PASS. Сканеры не закрывают смысловые дефекты.

## Остаток и передача

Владелец MCQ должен исправить варианты/sections Q38/Q39 и повторить зависимые
проверки, включая свежий blind A/B для изменённых видимых текстов.
Тела Q33/Q34 и весь seed сохранены, но явная dependency Q34 на SHA всего
Markdown стала STALE. [Доказательство неизменности](evidence/kotlin-jvm-plugins-2026-11-06/dependency-revalidation.json)
передано для refresh исходным reviewer; это не автоматическое продление FINAL.

[Handoff MCQ](evidence/kotlin-jvm-plugins-2026-11-06/mcq-owner-handoff.json),
[манифест](evidence/kotlin-jvm-plugins-2026-11-06/manifest.json).
record, sync, registry, checkpoint и PLAN этой задачей не менялись.
Приёмка всех 45 вопросов или всего корпуса не объявлена.

Markdown SHA-256:
`f3ec6f885917749c78bdec3a10f74a8b85be70cbb6a0adc2635a2062f3493afa`
→ `2b8e8a718c7192933217678c599a563ac89de37407ef1690d673ca8fbd670730`.
Production MCQ SHA-256 без изменений:
`4309b927d0834d677a1b6c780bf838177358ee3b4d67ab308cf82ebde048131c`.
