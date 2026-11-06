# Kotlin Q22/Q23: open и пользовательские аксессоры

Изменены только тела Q22 и Q23. Все 45 заголовков, оглавление, metadata и
остальные 43 ответа сохранены. Production MCQ JSON не менялся. Проверены
8 вариантов и 34 пояснения; Q22/Q23 остаются OPEN для исправления MCQ.
FINAL темы не объявлен, связанный дефект Q39 явно оставлен в остатке.

## Изменения теории

Q22 различает наследование обычного класса и переопределение его членов:
open class не открывает автоматически все методы. Уточнены abstract,
наследуемая открытость override и final override. Новый самостоятельный
пример показывает повторное переопределение Dog.speak без повторения open.
Причина default final объяснена через явные точки расширения; это практическая
интерпретация правила, не приписанная авторам языка историческая цитата.

All-open описан как преобразование подходящих классов/членов по аннотациям.
Необходимость open ограничена прокси через подкласс. JDK interface proxy
не наследует target class, поэтому плагин не обязателен для любого Spring AOP.

Q23 объясняет getter при чтении, setter при присваивании, исключение
инициализации, специальный field и повторный вызов аксессора при обращении
к тому же property. Убрана ложная формула «нет явного field — нет хранилища»:
например, default-accessors updateCount требуют backing field. Вычисляемый
fahrenheit своего поля не имеет. Уточнены private setter и отдельный контракт
explicit backing fields; показанные примеры используют обычные аксессоры.

Источники:
[Kotlin Inheritance](https://kotlinlang.org/docs/inheritance.html),
[abstract classes](https://kotlinlang.org/docs/classes.html#abstract-classes),
[Properties](https://kotlinlang.org/docs/properties.html),
[All-open](https://kotlinlang.org/docs/all-open-plugin.html),
[Spring proxying](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html).
[Source ledger](evidence/kotlin-open-accessors-2026-11-06/sources.json)
различает документацию и фактически выполненные проверки.

## Каждый вопрос, вариант и section

| Блок | Видимый вопрос и дефекты |
|---|---|
| Q22 | Требует значение open **и почему** default final. Ни один вариант не даёт полный ответ. D называет мотив предполагаемого действия плагина, а не обоих пунктов вопроса. A также неверно ограничивает наследование только явным open. |
| Q23 | C верно называет field, но отвечает об узком storage-аспекте, а не о работе обоих custom-accessors. A.how_it_should_be/C.edge_cases ошибочно обобщают отсутствие storage; B.how_it_should_be предполагает поле у любого property. |

Корректное объяснение причины в A.edge_cases Q22 не дополняет пропущенное
«почему» в видимом A. Напротив, локальный A.example — корректная схема обычного
случая; отсутствие каталога всех исключений не делает её ложной.

В Q23 D.if_it_were_true направление контрфактуала перепутано: если бы имя
давало прямой доступ к storage, повторного вызова getter не было бы. Реальная
рекурсия возникает из-за property access, а не из-за истинности гипотезы D.
Два guarded runtime-примера показывают повторный вызов без попытки StackOverflow.
[Авторские 34 оценки](evidence/kotlin-open-accessors-2026-11-06/author-review.json).

## Независимость и качество самого ревью

Отдельный свежий **gpt-6-luna / medium / fork_turns=none** получил только
blind-input. Выборы **Q22 NO_VALID_OPTION / Q23 C** запечатаны CLI до отдельного
reveal. Исходное NO_VALID_OPTION уже было мотивировано отсутствием «почему».
После раскрытия прочитаны четыре опции и 17 точных полей на каждый Q, теория
и первоисточники. Сохранённые JSON проверены по 4/17 ID/7 осям; технически
неудавшиеся turns не засчитаны готовыми отчётами.

Raw-отчёты и exact follow-ups различаются:

- Завершённое предложение с ложной моделью не автоматически отвечает всем
  частям вопроса. Q22 ANSC повторно оценён по двум видимым пунктам для всех4.
- A.example/A.edge_cases/A.related Q22 не объявлены ложными только из-за
  дефекта ключа. Истинность A, «каноничность» и одна длина не основания SFPL;
  первоначальное такое основание отозвано, независимый SFPL оставлен OPEN.
- Q23 исправлены универсальное property-over-field утверждение,
  контрфактуал D и ссылка на несуществующее C.how_it_should_be. У correct C
  этого field ID нет, ложная атрибуция сохранена только в raw.
- В follow-up Q23 есть обратная формулировка, будто visible D «отрицает»
  прямой доступ. Он его **утверждает**. Этот directional reason исключён;
  исходное factual-опровержение D и авторская оценка используют точный текст.

По Q23 B есть разница суждений: автор допускает целостную ложную модель
«аксессоры вообще невозможны», а reviewer после уточнения считает её неполной
по механике. Расхождение сохранено; единогласный PASS не придуман. Оба отмечают
узость ключа C. От вариантов не требуется каталог initializer/abstract/plugin
нюансов, но нужны реально заданные части вопроса. Каждое section оценивается
по собственной роли, а не обязано заново повторять весь ответ.
[Итоговая адъюдикация](../mcq-quality/runs/20261106T063934Z-kotlin-open-accessors-blind-r1/coordinator-review-summary.json).

## Выполненные проверки

- Точные standalone fences Q22 и Q23 скомпилированы и выполнены.
- Inheritance-программа проверила abstract/interface без open, повторный
  override и final у обычного метода.
- Accessor-программа проверила default getter со storage без явного field,
  computed getter без поля через JVM reflection, пропуск setter при initializer,
  и ограниченную двумя вызовами рекурсию getter/setter.
- Локальный **allopen compiler plugin 2.4.10** с собственной аннотацией открыл
  класс и метод: subclass/override скомпилировались и выполнились, reflection
  подтвердил отсутствие final. Тот же исходник без plugin был отклонён.
- Семь отрицательных программ получили ожидаемые ошибки: наследование final
  класса, override final метода, отсутствие override, повторный override
  закрытого метода, запись через private setter, field вне аксессора и тот же
  annotated-класс без all-open.

Итого **5 успешных JVM-программ и 7 ожидаемых compiler rejections**.
Kotlin **2.4.10**, compiler JRE **26.0.2**, target **17**, Microsoft OpenJDK
**21.0.10**. Plugin уже установлен, ничего не скачивалось. Его path/SHA сохранены.
Spring Framework 7.0.10 proxy-контракт проверен по документации; Spring runtime
не запускался. Новая explicit-field syntax, JS/Native, приложение и матрица
версий не запускались. [Runtime receipts](evidence/kotlin-open-accessors-2026-11-06/runtime-results.json).

Schema: 45 Q / 45 блоков / 180 опций; YAML: 9 полей PASS. Linkage:
0 ошибок/предупреждений; anti-tell, verify-md-no-mcq, diff check и current-run
block/rules/input guards PASS. Зелёные сканы не закрывают перечисленные дефекты.

## Остаток и передача MCQ

В связанной **Q39** осталось прежнее утверждение, будто all-open необходим
для всего Spring AOP. [Точная цитата и SHA](evidence/kotlin-open-accessors-2026-11-06/pending-q39-linked-issue.json)
сохранены как следующий приоритет полной проверки Q39/MCQ/noArg/Gradle.
Q39 не редактировался и не объявлен полностью проверенным; текущий пакет
не устраняет все расхождения внутри файла из 45 вопросов.

Тела Q33/Q34 и весь seed сохранены. Явная dependency Q34 на SHA всего Markdown
снова устарела; [перепроверка зависимости](evidence/kotlin-open-accessors-2026-11-06/dependency-revalidation.json)
передана для регистрации владельцем MCQ против нового SHA. Равенство тел
не продлевает прежний scoped FINAL автоматически.

[Handoff MCQ](evidence/kotlin-open-accessors-2026-11-06/mcq-owner-handoff.json),
[манифест](evidence/kotlin-open-accessors-2026-11-06/manifest.json).
Нужны исправления вариантов/sections Q22/Q23, новые зависимые проверки и
свежий blind A/B для изменённых видимых текстов. `record`, `sync`, registry,
checkpoint и PLAN этой задачей не менялись; общий FINAL не объявлен.

Markdown SHA-256:
`4e494e1c7e7993bd9a682bf34d07d41d4300458ed223078f21f466a6630fff8a`
→ `f3ec6f885917749c78bdec3a10f74a8b85be70cbb6a0adc2635a2062f3493afa`.
Production MCQ SHA-256 без изменений:
`17feea22245b71cc2e76d2634707f2012ed41938dd7fdca57012675803ac8914`.
