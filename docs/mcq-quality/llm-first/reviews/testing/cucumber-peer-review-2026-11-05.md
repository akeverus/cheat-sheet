# Cucumber / BDD — ревью исходной редакции и исправления MCQ

Дата: 2026-11-05. Исполнитель: Codex, контекст координатора MCQ. Исходную редакцию подготовил другой исполнитель; прочитаны все Q1–Q15, 60 вариантов, sections и соответствующая теория. Режим — **independent open-key review**. Слепые Phase A/B не выполнялись. После правок этот исполнитель является их автором и не принимает новую версию как независимый reviewer.

Исходный JSON `modules/quiz-app/src/main/resources/seed/mcq/testing/cucumber-bdd-interview.json`: SHA `9ca9bbbb5990d11e35ce4b9fbd2f51348ee1b05fbf7f70672916840a8a01ce73`. Исходная и оставленная без правок теория `cheatsheets/interview/testing/cucumber-bdd-interview.md`: SHA `14eef76415e2583c1081e1b5e3bb8f734d64413af8647ee7b1341e66e5d4ced1`.

Вердикт по исходной версии — **NEEDS_REPAIR**. Исправленный JSON имеет SHA `8c14073758da2f530ba2d568bdec67594464f2bd7f3053fe5e012907a2db57f9`; изменены Q2/Q3/Q5/Q7/Q8/Q10/Q11/Q12/Q13/Q14/Q15, а Q1/Q4/Q6/Q9 сохранены побайтово. Номера, block_idx, question_text, labels/order и все ключи сохранены. Исправленная пара пока **NEEDS_THEORY_SYNC**, новый independent PASS / FINAL — **NOT_RUN**. Прежний авторский LLM_READY относится к старому SHA.

## Находки и изменения

1. **Q2/Q15 — практика и инструмент.** Совместное discovery отделено от наличия feature-файлов. Убрана универсальная формула «нет нетехнического читателя/один автоматизатор → никакой пользы»: автоматизация готового поведения может быть разумной, но не подтверждает BDD. Новый Q15 B проверяет выбор по пользе обсуждения, читателям и цене поддержки, а wrong — автоматический выбор по технической сложности, признание BDD по файлам и игнорирование стоимости glue. Это критерии решения, не обещание ROI. [Официальные myths](https://cucumber.io/docs/bdd/myths/), [BDD](https://cucumber.io/docs/bdd/), [роль участников](https://cucumber.io/docs/bdd/who-does-what/).
2. **Q3 — смысл красного прогона.** A.example больше не объявляет любое падение доказательством расхождения требований и поведения. Проверяются также данные, реализация проверки и среда; правильность непокрытых требований не выводится из статуса теста. Исправлены связанные контрфактические объяснения, без изменения ключа A.
3. **Q7 — duplicate и ambiguity.** В Cucumber JVM **7.18.1** identical-pattern registration даёт DuplicateStepDefinitionException; несколько разных definitions, совпавших с одним конкретным шагом, — AmbiguousStepDefinitionsException. Одна похожесть текстов не доказывает ошибку. Уточнено, что переиспользуется код definition, а не mutable state объекта между сценариями. [CachingGlue 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-core/src/main/java/io/cucumber/core/runner/CachingGlue.java).
4. **Q8 — пример enum и expression mode.** Точный исходный метод `Status.valueOf(value.toUpperCase())` воспроизведён в отдельной JVM: active при tr-TR даёт IllegalArgumentException, Locale.ROOT корректно обрабатывает active/inactive/deleted. В sections отмечено исправление и различие Java expression/regex; тип regex-аргумента не объявляется универсально int без контекста handler/parameter types. [Expression syntax и Java heuristics](https://cucumber.io/docs/cucumber/cucumber-expressions/).
5. **Q12 — tags и отбор.** Добавление tags меняет набор меток, а не автоматически сужает выборку. Уже приведённый `@payment or @smoke` сохраняет все payment-сценарии. В sections включены Rule и Scenario Outline; tags не переносятся на Background или шаги. [Tags и inheritance](https://cucumber.io/docs/cucumber/api/?lang=java#tag-inheritance).
6. **Q13 — native engine, Vintage и discovery.** Широкий вопрос о JUnit 5 не исключал RunWith-путь через JUnit4/Vintage. Новый wrong B приписывает RunWith напрямую native Cucumber engine, что неверно; sections различают интеграции. Properties не объявлен универсальной заменой selectors/discovery: README **7.18.1** описывает отдельные ограничения build tools для non-class tests. [Native engine 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-junit-platform-engine/README.md), [JUnit4 Cucumber runner](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-junit/src/main/java/io/cucumber/junit/Cucumber.java), [JUnit Vintage 5.10.3](https://docs.junit.org/5.10.3/user-guide/#migrating-from-junit4-running).
7. **Q14 — definition, class и state.** Step definition — обработчик, в Java обычно метод; их может быть несколько в одном классе. Носитель ScenarioScope не создаёт копию static fields и не сбрасывает внешние ресурсы. A теперь содержит конкретную ошибку об автоматической изоляции static, а не субъективное «удобно». Уточнены glue scope, один конфигурационный класс, bean registration и запрет Component на step glue-классах. [Step definitions](https://cucumber.io/docs/cucumber/step-definitions/), [SpringFactory 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-spring/src/main/java/io/cucumber/spring/SpringFactory.java), [ScenarioScope 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-spring/src/main/java/io/cucumber/spring/ScenarioScope.java).

Дополнительно Q5 уточняет scope Background в Feature/Rule, порядок после Before и отсутствие автоматического reset данных. Q10 перечисляет распространённые формы DataTable без утверждения об исчерпывающих трёх способах API. Q11 различает Before ascending / After descending, пропуски step hooks после ошибки и cleanup ресурса, который мог не создаться. [Gherkin Background](https://cucumber.io/docs/gherkin/reference/#background), [Before 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-java/src/main/java/io/cucumber/java/Before.java), [After 7.18.1](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-java/src/main/java/io/cucumber/java/After.java), [PickleStepTestStep](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-core/src/main/java/io/cucumber/core/runner/PickleStepTestStep.java).

Первичные источники прочитаны 2026-11-05. Pinned source-проверки относятся к JVM 7.18.1 и JUnit 5.10.3; это не заявление о последнем релизе или выполнении этих зависимостей в проекте.

## Охват A/B/C/D и редакторские пределы

| Q | Различие вариантов и результат |
|---|---|
| 1 | Замена TDD, обязательный Cucumber, только гранулярность или совместные примеры + TDD cycle. Ключ D, изменений нет. |
| 2 | Поздний словарь, отдельные языки ролей, общий язык/обсуждение или ценность только файлов. Ключ C; убраны чрезмерные обещания о пользе/вреде автоматизации. |
| 3 | Проверяемая спецификация, только ручная вычитка, артефакты связаны лишь отчётом или автоматическая свежесть. Ключ A, исправлены пределы вывода по результату теста. |
| 4 | Keyword matching, структура Feature/Scenario, раздельный lookup по keywords или несколько Feature в файле. Ключ B; все варианты/sections прочитаны, без правок. |
| 5 | Однократная подготовка, скрытый block, безусловная польза длинного setup или повторяемые видимые предусловия. Ключ D; scope/isolation уточнены в sections. |
| 6 | Намерение бизнеса, преимущество кликов, обязательные UI-детали или обратная устойчивость текста. Ключ A, изменений нет. |
| 7 | Имя метода, совпадение keywords, expression или definition на один сценарий. Ключ C, ошибки lookup уточнены. |
| 8 | Полная замена regex, отсутствие regex, выбор expression/regex или запрет custom types. Ключ C; версия/типизация/locale уточнены. |
| 9 | Одна обработка всех rows, отдельный запуск строки, общий отчёт или пустая подстановка без Examples. Ключ B, изменений нет. |
| 10 | Размножение сценария, только raw cells, данные одного шага или ключ по первой колонке. Ключ C, перечень API уточнён. |
| 11 | Один верный lifecycle и три перестановки/пропуска hooks. Ключ A; order и контролируемая обработка ошибки уточнены. |
| 12 | Нет inheritance, tag set + expression, один tag или только Feature. Ключ B; автоматическое сужение отбора устранено. |
| 13 | Native engine, RunWith будто его аннотация, обязательная Suite или только cucumber-java. Ключ A; альтернативные launch paths разделены. |
| 14 | Автоизоляция static, отсутствие DI, автоматическая видимость полей или scoped carrier. Ключ D, механизм исправлен. |
| 15 | Выбор по сложности кода, критерии процесса/инструмента, наличие файлов как BDD или бесплатный glue. Ключ B, все четыре и sections переписаны. |

SFPL: сравнивались грамматика, предмет, API, условия, отрицания и объём всех четырёх вариантов. Новый Q15 не выделяет ключ единственным кодом или детальной оговоркой; Q13 B оставляет реальный синтаксис RunWith. ANSC: уточнения не вводят единственное условие правильности только в скрытом question_text; форма не заменяет проверки альтернатив. RU: новые объяснения описывают механизм вместо ссылок на «тело вопроса» там, где поле менялось. Независимые SFPL/ANSC/RU PASS для собственной новой версии не присваиваются.

## Проверки и передача

Schema **PASS 15 Q / 15 B**, anti-tell **0/15**, SKEL **hard=0 / warn=1**. Parity остаётся **FAIL max word ratio 1.44, Q13**, остальные gates проходят; correct-longest 2/15, style guessability 0.278. Linkage **errors=0 / warnings=2**: Q5 C (полезность длинного Background) и Q15 C (наличие файлов не равно discovery). Их опровержения есть в локальных разделах; лексика не является independent acceptance. Пороги и ключи не менялись, padding не добавлялся.

Локально исполнен только извлечённый Java-метод преобразования enum: JDK21, исходная ошибка и исправление Locale.ROOT; системная настройка locale не менялась, настройка действовала лишь в дочерней JVM. Полноценные Cucumber engine, Spring glue, feature parser, hook order и Vintage integration **NOT_RUN**: в проверенных Gradle/Maven cache io.cucumber нет готовых JAR; ничего не скачивалось и не устанавливалось. Диагностики — Python pre-checks, не runtime Cucumber.

Task-2: `cucumber-peer-original-findings.json`, baseline seed/theory, `cucumber-peer-prepared.json`, `cucumber-peer-fixed-checks.json` и логи 0..6, `cucumber-status-locale-check.json`. Исходные findings сохранены до правок. Новая версия является авторской коррекцией; строгий workflow record, Phase A/B и FINAL — **NOT_RUN**.

**Владельцу теории:** синхронизировать Q2/Q15 (BDD ≠ tool/гарантия ROI), Q3 (результат падения), Q5 (scope/isolation), Q7 (duplicate/ambiguity), Q8 (Locale.ROOT, Java expression mode и границы типизации), Q10 (неисчерпывающий перечень), Q11 (order и cleanup), Q12 (inheritance ≠ filter), Q13 (native/Vintage/discovery), Q14 (method/class/ScenarioScope). До этого пара не LLM_READY. Markdown и работающий сервис этой задачей не менялись.

Коммит сохраняет и уже существовавшую редакцию автора, прочитанную в этом проходе. По сравнению с HEAD изменены Q1,2,3,4,5,6,7,8,9,10,11,12,13,14,15; до наших исправлений уже менялись Q1,2,3,4,5,6,8,9,10,11,13,15. Сравнение с авторским снимком и четыре сохранённых Q приведены выше. Теория остаётся в области ответственности другого исполнителя.
