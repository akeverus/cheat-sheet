# Selenium — полный аудит и исправления MCQ, 2026-11-05

**NEEDS_THEORY_SYNC / NEEDS_REVIEW.** Выполнен distinct-author open-key аудит исходного материала: 15/15 видимых вопросов, полные ответы теории, 15/15 блоков, 60/60 вариантов и их sections. Затем тот же исполнитель исправил MCQ, поэтому независимая приёмка **исправленной** версии Phase A/B и FINAL **NOT_RUN**. Дополнительные исполнители и чаты не запускались.

## Снимки и границы

- Исходный MCQ: `fa2a1ab29acc760fa9ec25a1a3b7de27c9e5e0675fcdaa1a5ebc74bfd6eddd12`.
- Исправленный MCQ: `f6576f3983129e197470fca6bd3665c7d21d352a8173806672de3579c3acc61c`.
- Прочитанная теория: `a209ee6d8921727eae61363b00a7b1fb6262ee09a9839ae19d18c82b26b0bafd`.
- Исходный авторский отчёт: `b743ef05ece0a02c1ea0613cc3d68456f9e47619a8519cbefffa5d0aa0694165`.
- Fact-freshness до/после: `dbfdc931028554c6a72517db258545c2c9f4276bbf4ec609b860e7f7a60ce30d` → `62460dc641235e8b03ec79064ce4504e3432a6c9fbf5ecf8266e63d9dbb69fae`.
- Авторский отчёт с добавленной ссылкой на этот проход: `2c182b524f4bdb77e81f5207b4467d97333277b54f1a4d9866f2832afdeab8dd`.

Исходные снимки и находки сохранены **до исправлений** в рабочей папке task-2: `selenium-peer-baseline-{seed.json,theory.md,author.md,freshness.json}`, `selenium-peer-original-findings.json` (SHA `ce79a682799e2e30780d41e65c8303645e3ac0e99a11263ef15275e4796f0a54`). Тексты вариантов изменены в Q1/Q3/Q5/Q7/Q13/Q14, sections — у 37 вариантов в 13 вопросах. Ключи, идентификаторы, порядок и question_text сохранены. Markdown, рабочее приложение, данные, браузеры и сеть компьютера не менялись.

## Содержательные результаты

| Q | Найдено и сделано |
|---|---|
| 1 | Уточнена цепочка Classic HTTP. Добавлено различие с BiDi/WebSocket. Убрано обещание управления произвольными нативными диалогами ОС. Дистракторы проверяют архитектуру, а не знание Python-имени execute_script в Java-теме. |
| 2 | Сохранён ключ про устойчивый уникальный id и стратегии поиска. Пояснение отличает стабильность локатора от предпочтений и скорости CSS/XPath. Официальный guide предпочитает ID/CSS; это не универсальная гарантия производительности. |
| 3 | Исправлен ключ: implicit относится к поиску в сессии, explicit — к условию; WebDriverWait уже наследует FluentWait. Убраны ограничения «настроить один раз» и «только один элемент». Разобраны смешивание таймаутов и предел visible+enabled у elementToBeClickable. |
| 4 | Контрпримеры больше не отрицают серверную idle-политику и InterruptedException. Отличаются polling interval и полный timeout, spurious wakeup и interruption. Фиксированная пауза не проверяет состояние приложения. |
| 5 | Page Object может представлять часть страницы; добавлена оговорка о Page Component. Проверки бизнес-результата остаются в тесте, проверка загрузки/готовности допустима при создании Page Object. |
| 6 | Сохранена lazy proxy модель. Разведены ByAll.findElement и findElements; ByChained ищет потомков последовательно, не пересекает признаки одного узла. Повторный поиск не делает lookup/action атомарными. |
| 7 | Уточнены JS-диалоги против HTML modal, parentFrame против defaultContent и window name против title. Убрана псевдосигнатура name/index/element из вариантов. Добавлено ожидание нового handle без опоры на порядок Set. |
| 8 | Grid распределяет сессии и команды; Java-методы параллелит runner. Убрана обязательность одной Hub/Node топологии. Компоненты и доступность браузеров проверены по Grid 4 docs. |
| 9 | Файл properties — один из способов передачи JUnit parameters, а не единственный. Разобраны PER_CLASS, изоляция сессий/данных и remove в finally. В теории воспроизведён дефект cleanup, см. ниже. |
| 10 | Сохранён верный выбор диагностики/ожиданий/данных. Убрана обязательность retry и test-id для любой причины flaky. Позиционный XPath зависит от структуры, но не «ломается при любом изменении». |
| 11 | Все четыре варианта прочитаны; актуальная поддержка Cypress Firefox/WebKit и Puppeteer Chrome/Firefox подтверждена официальной документацией. Варианты и sections сохранены. WebKit не равнозначен Safari app. |
| 12 | Сохранён корректный встроенный Manager с 4.6. Добавлены ограничения fallback, cache/сети и предостережение против автоматического выбора sandbox-флагов. Никакие флаги окружения не менялись. |
| 13 | Исправлено объяснение: keyword задаёт синтаксис шага, matching использует текст после него. Page Object необязателен, Cucumber применим и без UI. Обозначена необходимость инициализации/cleanup driver. |
| 14 | Убраны абсолютный запрет бизнес-сценариев в UI, недоказанные секунды ускорения и круговой довод «cleanup нужен, потому что в списке». Сохраняется выбор уровня теста по риску. |
| 15 | Все варианты и пояснения прочитаны, сохранены. Риск и стоимость определяют состав набора; фиксированные проценты не вводились. |

## Точное задание владельцу теории

Приоритетные реальные дефекты примеров:

1. **Q9 DriverFactory.quitDriver**: `driver.get().quit(); driver.remove();` оставляет ссылку, если quit бросает. Нужен `try { ...quit(); } finally { driver.remove(); }`; учесть null/ошибку создания сессии. Удаление локальной ссылки само по себе не гарантирует успешное освобождение удалённой сессии после сбоя.
2. **Q13 LoginSteps**: поле `private WebDriver driver` не инициализируется в показанном классе; первый driver.get даёт NPE без неуказанного внешнего wiring. Показать scenario-scoped создание/внедрение и After cleanup. Page Object полезен, но не обязателен.
3. **Q10**: метод только с `@Tag("flaky")` не является Jupiter-тестом; добавить `@Test` либо явно назвать блок фрагментом аннотаций. Retry требует собственной библиотеки/engine-интеграции. Добавить понятное объявление WebDriverWait и условие фактической готовности.

Другие необходимые уточнения: Q1/Q12 — совместимость версий драйвера и браузера вместо универсального полного совпадения; Q1 — Classic/BiDi и топология Grid; Q2 — стабильный контракт локатора вместо безусловной лестницы скорости; Q3 — наследование FluentWait и предел elementToBeClickable; Q4 — исключения/серверная политика; Q5 — компоненты и проверка загрузки; Q6 — ByAll список против первого элемента; Q7 — parentFrame, nameOrHandle, ожидание нового окна; Q8 — runner против Grid; Q9 — PER_METHOD/PER_CLASS и config sources. Q14 — пользовательский бизнес-путь может требовать UI, хотя детали правил обычно проверяют ниже; убрать измеренно выглядящие числа без замера. Заголовок Q14 также требует падежа «Каких анти-паттернов стоит избегать?», синхронно с hidden stem. Q15 — визуальные проверки требуют собственных проверяемых критериев/инструментов.

Существующие visible/hidden расхождения в большинстве вопросов стилистические, но Q3 видимый заголовок не называет fluent wait, который проверяется в вариантах. Согласовать точный видимый вопрос с окончательной теорией и question_text; не исправлять один слой изолированно.

## Выполненные проверки и их пределы

**3/3 pure Java probes PASS** на JDK 21:

- `originalQuitFailureLeavesThreadLocalReference` — тело исходного DriverFactory из Q9 сохранено, заменено только имя класса; контролируемая ошибка quit оставляет тот же объект.
- `finallyRemovesReferenceEvenWhenQuitThrows` — единственное изменение тела: try/finally вокруг quit/remove; следующее получение создаёт другой объект.
- `sleepCanBeInterruptedBeforeFixedDuration` — уже прерванный поток получает InterruptedException вместо полного sleep(5000).

Для первых двух probes использованы минимальные локальные WebDriver/ChromeDriver заглушки, моделирующие только quit failure. Это доказывает Java control flow, **не** совместимость Selenium API, поведение настоящего драйвера или очистку процессов. Источник: `run_selenium_java_controlflow_checks.py`, Java-классы `selenium-java-controlflow/`, полный stdout/команды/SHA в `selenium-java-controlflow-checks.json` рабочей папки task-2.

В проверенных Gradle/Maven cache Selenium JAR не найден. Selenium API compilation/runtime, browser, Manager, Grid, CI, Cucumber engine: **NOT_RUN**. Установки, загрузки зависимостей и запуск браузера не выполнялись. Semantics API сверены с исходниками **4.25.0**, это не заявление о версии зависимости проекта или последнем релизе.

Исходный snapshot: schema PASS, parity PASS (max 1.27), anti-tell 0/15, SKEL pre-check 0/0; linkage exit 1 — 2 errors / 23 warnings. Значит, зелёные формальные проверки уже пропускали реальные содержательные проблемы.

После исправлений: schema 15 PASS, parity PASS (max 1.27 Q15, correct-longest 3/15, style 0.344), anti-tell 0/15, SKEL pre-check 0 hard / 0 warnings, struct/surface exit 0. **Linkage exit 1: 1 error / 20 warnings / 13 manual-review blocks**. Остался NOVEL-TERM Q10 WebDriverWait: термин явно раскрыт в Q3, а в Q10 текст говорит об explicit waits и показывает wait.until без объявления класса. Это ручное межвопросное пересечение и задача синхронизации теории, не основание замаскировать диагностику. Все предупреждения видимы в логах, override не создавался.

Первый вариант исправлений выявил повторяющиеся прологи, различие пунктуации и 2 anti-tell сигнала. До staging тексты переработаны; исходные FAIL-логи сохранены под `selenium-peer-repaired-check-*`, финальные под `selenium-peer-final-check-*`. Правила качества и пороги не менялись. Для окончательной независимой SFPL/ANSC/RU/SKEL оценки требуется свежий reviewer; финальный эвристический PASS её не заменяет.

## Первичные источники

- [WebDriverWait 4.25.0](https://raw.githubusercontent.com/SeleniumHQ/selenium/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/WebDriverWait.java) и [ExpectedConditions 4.25.0](https://raw.githubusercontent.com/SeleniumHQ/selenium/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/ExpectedConditions.java) — наследование и clickable predicate.
- [ByAll](https://raw.githubusercontent.com/SeleniumHQ/selenium/selenium-4.25.0/java/src/org/openqa/selenium/support/pagefactory/ByAll.java), [ByChained](https://raw.githubusercontent.com/SeleniumHQ/selenium/selenium-4.25.0/java/src/org/openqa/selenium/support/pagefactory/ByChained.java), [WebDriver interface](https://raw.githubusercontent.com/SeleniumHQ/selenium/selenium-4.25.0/java/src/org/openqa/selenium/WebDriver.java) — поиск, implicit и context API.
- [Waits](https://www.selenium.dev/documentation/webdriver/waits/), [BiDi](https://www.selenium.dev/documentation/webdriver/bidi/), [Page Objects](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/), [locators](https://www.selenium.dev/documentation/test_practices/encouraged/locators/) — границы и практики.
- [Grid components](https://www.selenium.dev/documentation/grid/components/), [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/) — сессии и fallback.
- [JUnit 5.10.3 guide](https://docs.junit.org/5.10.3/user-guide/) — конфигурация и параллельность.
- [Cucumber Gherkin reference](https://cucumber.io/docs/gherkin/reference/), [step definitions](https://cucumber.io/docs/cucumber/step-definitions/) — keyword, expression и исполняемый glue.
- [Cypress browsers](https://docs.cypress.io/app/references/launching-browsers), [Puppeteer FAQ](https://pptr.dev/faq) — актуальная матрица браузеров; [JDK 21 Thread](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html) — interruption.

Следующий checkpoint: синхронизировать перечисленные разделы теории, зафиксировать неизменяемую пару, провести независимые Phase A/B с сохранёнными предсказаниями и evidence. Сервис не развёртывался; этот пакет не является учебной приёмкой темы целиком.
