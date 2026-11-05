# Selenium — lifecycle driver и исполняемые примеры

Дата: 2026-11-05. Узкий `interview-writer / SEMANTIC_UPDATE` по приоритетам [peer-review MCQ](../mcq-quality/llm-first/reviews/testing/selenium-peer-review-2026-11-05.md). Прочитаны все 15 ответов теории и отчёт; содержательно сверены **Q9/Q10/Q13**, все 12 соответствующих MCQ-вариантов с sections. Исправлены только эти три ответа. Остальные 12 требуют дальнейшей синхронизации; тема целиком не принимается. Это авторская проверка с открытыми ключами, не independent acceptance исправленной пары, blind Phase A/B или FINAL.

Входная теория SHA **`a209ee6d8921727eae61363b00a7b1fb6262ee09a9839ae19d18c82b26b0bafd`** совпала с прочитанным peer-review снимком. В ней уже находились незакоммиченные изменения: полный исходный diff из 408 строк сохранён отдельно. Исходный seed SHA **`f6576f3983129e197470fca6bd3665c7d21d352a8173806672de3579c3acc61c`** также совпал с отчётом и не менялся в ходе этой проверки. JSON и индекс не редактировались. Выходная теория SHA **`a9a6567f733fe7fee217bf54f4fcd72bb27e612d38af76f092e08ee8b18df926`**.

## Исправления

| Q | Результат |
|---|---|
| 9 | DriverFactory удаляет ThreadLocal в finally даже при ошибке quit, не создаёт driver ради cleanup и не сохраняет значение при ошибке конструктора. Разделены PER_METHOD/PER_CLASS, runner/Grid, изоляция driver/данных. Properties названы одним из способов configuration parameters. Удаление ссылки не выдано за доказательство освобождения удалённой сессии. |
| 10 | Вместо недекларированного wait и метода только с Tag дан полный Jupiter-класс с Test, объявленным WebDriverWait, условием текста Ready и cleanup из Q9. Retry отделён от Tag и от встроенных возможностей Jupiter. Оговорены контракт локатора и предел visible/enabled. |
| 13 | Полный glue-класс создаёт driver в Cucumber Before и закрывает в After с null guard/очисткой поля. Убрана зависимость от необъявленного Page Object wiring. Разделены роль keyword и matching текста, UI/API применение Cucumber и scope glue. |

Q10/Q13 используют **учебный контракт приложения**, переданный через ui.baseUrl: соответствующие страницы и локаторы должны существовать на стенде. Они не являются готовым тестом example.com и здесь с браузером не запускались.

Первичные источники: [JUnit 5.10.3 guide](https://docs.junit.org/5.10.3/user-guide/) — opt-in parallel configuration, execution mode и PER_CLASS; [Selenium waits](https://www.selenium.dev/documentation/webdriver/waits/), [ExpectedConditions 4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/ExpectedConditions.java) — textToBe и границы условия clickable; [Cucumber Gherkin](https://cucumber.io/docs/gherkin/reference/) — keyword не участвует в поиске step definition; [Cucumber state](https://cucumber.io/docs/cucumber/state/) — отдельный glue instance для scenario и scope при DI. API-сверка по 4.25.0 не означает, что это последняя версия или версия зависимости проекта.

## Реально исполненная проверка

**6/6 pure Java control-flow probes PASS**, javac/JVM exit 0, JDK **21.0.10**, реальные кешированные Jupiter **5.10.3** / Platform **1.10.3**. Полный новый DriverFactory из Q9 скопирован **без изменения кода**. Только отсутствующие Selenium-типы заменены минимальными локальными WebDriver/ChromeDriver doubles, моделирующими quit и создание. Пробы проверяют:

- cleanup без созданного driver не создаёт сессию;
- два get в одном потоке используют один объект;
- успешный quit удаляет ссылку;
- исключение quit также удаляет ссылку, следующий get создаёт другой объект;
- исключение конструктора не регистрирует незавершённый объект;
- worker и main имеют разные ссылки и выполняют cleanup в своих потоках.

Это проверка **Java control flow, ThreadLocal и finally**, не компиляция против Selenium JAR, не настоящий WebDriver runtime и не доказательство очистки браузерных процессов. В проверенных Gradle/Maven cache Selenium/Cucumber JAR не найден. Не запускались браузер, Manager, Grid, Cucumber engine, CI, full project/Gradle; Q10/Q13 не компилировались против настоящих API и не исполнялись. Зависимости не устанавливались и не загружались, настройки компьютера и браузеров не менялись. Эти ограничения не заменяются зелёным результатом control-flow probes.

## Диагностика и оставшаяся работа

Schema **PASS 15/15**, semantic **NOT_RUN**. Linkage exit **0**, **0 errors / 19 warnings / 13 manual-review blocks**. Выбранные три: **0 errors / 5 warnings** — три стилистических STEM-MISMATCH, Q9 @Parallel в неверной альтернативе, Q10 Thread из Thread.sleep. Область configuration parameters и отсутствие автоматической настройки при одной аннотации разобраны в Q9; разница фиксированного sleep и ожидания состояния разобрана Q10. Имена ради lexical overlap не добавлялись. Заголовки не менялись изолированно от MCQ. Это разбор сигналов автором, не новая независимая приёмка.

Структурный guard: только Q9/Q10/Q13 изменены относительно входа группы, остальные **12** ответов побайтно сохранены. Все 15 заголовков, ID/anchors, metadata, TOC и See also сохранены. Предел проверки старого diff существенен: до коммита всего Selenium Markdown требуется отдельная проверка его прежних изменений и оставшихся замечаний, а не автоматическое включение файла целиком.

Открыты Q1/Q12 (совместимость версий, Classic/BiDi/Manager), Q2 (устойчивость локаторов), Q3/Q4 (FluentWait, предел clickable, таймауты/исключения), Q5/Q6 (Page Component, ready check, ByAll/ByChained), Q7/Q8 (контексты, ожидание window handle, Grid/runner), Q14/Q15 (выбор уровня по риску, недоказанные секунды/абсолюты, критерии visual checks), актуальность матрицы Q11. Изменение формулировок заголовков Q3/Q14 нужно согласовать одновременно с hidden stem MCQ, не править один слой отдельно. Q14 должен допускать критичные бизнес-пути через UI; содержательный дефект пока сохранён для следующей группы.

Артефакты `/tmp/selenium-theory-lifecycle-20261105/`: before.md, исходный seed и git-diff-before.patch, три подготовленных тела, structural-check, runtime-controlflow/execution с SHA точного DriverFactory и кешированных JAR, schema/linkage/diagnostic-summary, frozen-validation с точной парой и неизменённым linkage-скриптом. Снимки исходных source excerpts сохранены отдельно; это не полные byte snapshots веб-страниц.

Статус **PARTIAL_THEORY_SYNC / NEEDS_REPAIR**, FINAL **NOT_RUN**. Файлы сохранены без staging; весь Selenium Markdown пока не обозначается готовым к коммиту.

Последующая [полная авторская сверка](selenium-theory-full-review-2026-11-05.md) разобрала прежний diff и оставшиеся 12 ответов. Ограничение допуска файла из-за непроверенного старого diff снято в том проходе; результаты и ограничения этого первого этапа сохранены как история. Актуальный пакет и SHA находятся в `/tmp/prepared-selenium-theory-full-20261105-v2.json`, независимо от статуса независимой приёмки/FINAL.
