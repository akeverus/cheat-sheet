# Selenium — сверка всех ответов и прежнего diff

Дата: 2026-11-05. Продолжение [первой группы lifecycle](selenium-theory-lifecycle-review-2026-11-05.md). Прочитаны **15/15 ответов**, **60/60 MCQ-вариантов с sections**, peer-review MCQ и прежний незакоммиченный diff. Исправлены оставшиеся Q1–Q8/Q11/Q12/Q14/Q15; предыдущие Q9/Q10/Q13 сохранены побайтно. Это авторская содержательная сверка с открытыми ключами, не independent acceptance исправленной пары, blind Phase A/B или FINAL.

Вход этого прохода: теория SHA **`a9a6567f733fe7fee217bf54f4fcd72bb27e612d38af76f092e08ee8b18df926`**, MCQ SHA **`f6576f3983129e197470fca6bd3665c7d21d352a8173806672de3579c3acc61c`**. В основном проходе seed не менялся. Теория после него имела SHA **`f4cb09839ea24a98a073321f362d4dadd4b77aaf43ab6c6befabb863b589534b`**; последующая сверка текущей пары приведена ниже. JSON, реестр приёмки, приложение и индекс не редактировались.

## Прежние изменения проверены

Исходный прежний diff из **408 строк**, сохранённый до первой группы, разобран по всем смысловым изменениям. Обновление metadata, 15 пунктов TOC и anchors проверены структурно. Сохранены переход JSON Wire → W3C для Classic, исправления Java-переменных, различение presence/visibility, CacheLookup/proxy method, альтернативные frame-вызовы, Router/Distributor, настройки parallel strategy, браузерная матрица, встроенный Manager, removal обязательных sandbox flags, distribution в setup-java, согласование Gherkin/glue и удаление произвольных процентов пирамиды.

Оставшиеся недостатки старого diff исправлены в первой либо текущей группе: quit без finally; Tag без Test; неинициализированный driver; обещания полного совпадения версий, одной топологии Grid, готовности при одном clickable, лестницы скорости локаторов, универсального запрета бизнес-путей через UI и неизмеренных секунд ускорения. Прежние удалённые screenshot/retry-фрагменты не возвращались как готовые работающие примеры. Более узкая матрица альтернатив в Q11 оставляет четыре инструмента, проверяемых вопросом; неподтверждённые частотные/трендовые оценки не сохранены.

Первоначальное ограничение «весь Selenium Markdown не готов к коммиту из-за непроверенного прежнего diff» из первой группы **снято по результатам этой авторской проверки**. Оно не заменено утверждением о runtime или принятой теме: ограничения ниже остаются. Перед адресным коммитом нужны свежие SHA/HEAD/index и координационная передача индекса.

## Содержательные результаты

| Q | Итог и основной источник |
|---|---|
| 1 | Classic HTTP отделён от BiDi/WebSocket, локальный/удалённый endpoint и совместимость версий уточнены. [BiDi](https://www.selenium.dev/documentation/webdriver/bidi/) |
| 2 | Устойчивый контракт вместо универсальной скорости/запрета XPath; полный и частичный текст ссылки, локализация и test-id. [Locators](https://www.selenium.dev/documentation/test_practices/encouraged/locators/) |
| 3 | Implicit search timeout, произвольное explicit condition и наследование WebDriverWait от FluentWait; configurable polling, предел visible/enabled и вложенные таймауты. [WebDriverWait 4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/WebDriverWait.java), [waits](https://www.selenium.dev/documentation/webdriver/waits/) |
| 4 | Условие готовности вместо фиксированной паузы; polling interval не равен timeout, interruption не spurious wakeup, idle policy отдельна. [ExpectedConditions 4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/ExpectedConditions.java), [JDK21 Thread](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html) |
| 5 | Page Component и ready check отделены от бизнес-assertions. Даны оба класса страниц вместо неявного DashboardPage wiring. [Page objects](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/) |
| 6 | Стандартный lazy proxy/cache; ByAll single/list и ByChained descendants, не пересечение одного узла. [ByAll](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/pagefactory/ByAll.java), [ByChained](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/pagefactory/ByChained.java), [DefaultElementLocator](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/pagefactory/DefaultElementLocator.java) |
| 7 | JS dialog против HTML modal; iframe element в parent DOM, его документ — другой context. ParentFrame/defaultContent и nameOrHandle уточнены. Новое окно определяется разностью sets с ожиданием, не порядком Set. [WebDriver 4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/WebDriver.java) |
| 8 | Runner против Grid, создание/команды существующей сессии, очередь/Session Map и разные топологии. [Components](https://www.selenium.dev/documentation/grid/components/), [getting started](https://www.selenium.dev/documentation/grid/getting_started/) |
| 9 | Сохранены проверенные finally/remove, creation failure и PER_METHOD/PER_CLASS; источник и шесть Java probes — в первой группе. [JUnit 5.10.3](https://docs.junit.org/5.10.3/user-guide/) |
| 10 | Сохранены полный Jupiter Test/Tag/Wait, контракт Ready, диагностика и cleanup. Tag не добавляет retry. [ExpectedConditions](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/support/ui/ExpectedConditions.java) |
| 11 | Матрица перепроверена: Cypress Firefox/experimental WebKit, Puppeteer Chrome/Firefox; Playwright WebKit не приложение Safari. Auto-wait не заменяет бизнес-assertion. [Cypress](https://docs.cypress.io/app/references/launching-browsers), [Puppeteer](https://pptr.dev/faq), [Playwright browsers](https://playwright.dev/java/docs/browsers), [actionability](https://playwright.dev/java/docs/actionability) |
| 12 | Manager fallback, явно заданные пути, совместимость/cache/сеть; headless и flags по окружению. Maven workflow назван учебным, upload не создаёт screenshot. [Manager](https://www.selenium.dev/documentation/selenium_manager/), [DriverFinder 4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/remote/service/DriverFinder.java), [setup-java v4](https://github.com/actions/setup-java/tree/v4), [upload-artifact v4](https://github.com/actions/upload-artifact/tree/v4) |
| 13 | Сохранены полный glue и Cucumber Before/After, keyword/text matching, scenario lifecycle; UI и Page Object необязательны для Cucumber. [Gherkin](https://cucumber.io/docs/gherkin/reference/), [state](https://cucumber.io/docs/cucumber/state/) |
| 14–15 | Выбор по риску, подробные правила ниже и репрезентативные бизнес-пути через UI; без fake секунд/процентов. Visual check требует критерия и сравнения, а не только screenshot. Это проектные рекомендации с оговорками, не замер конкретного проекта. |

## Проверки и пределы

Исполнены **шесть** pure Java control-flow probes первой группы на реальных Jupiter/Platform и локальных Selenium-type doubles. Их протокол сохраняется; число не увеличивается за счёт повторного упоминания. Q9 DriverFactory и весь Q9/Q10/Q13 сохранены побайтно; code SHA совпадает с успешным execution. Повтор того же набора без изменения не требовался.

**Selenium/Cucumber/Playwright API compilation/runtime, настоящий browser, Manager, Grid, CI и full project/Gradle — NOT_RUN.** В проверенном кеше Selenium/Cucumber JAR не найден; установки/скачивания и запуск браузера не выполнялись. Код остальных примеров проверен по чтению/источникам, а не выдан за исполненный. Browser-driver/process cleanup после сбоя не доказан локальными doubles. Источники Selenium Java привязаны к **4.25.0**, не объявлены последней версией или зависимостью проекта. Матрицы инструментов сверены по текущей официальной документации на дату отчёта. Performance/concurrency matrix не измерялась.

Schema **PASS 15/15**, semantic gate **NOT_RUN**. Итоговый linkage exit **0**, **0 errors / 17 warnings / 13 manual-review blocks**. Первый прогон дал NOVEL-TERM Q6 CacheLookup: при переписывании было потеряно точное обозначение аннотации с @. Оно восстановлено как `@CacheLookup` вместе с точным оформлением FindBys/FindAll/findElement; это восстановление API-нотации, не новый тезис для overlap. Исходный FAIL-протокол сохранён, пороги не менялись.

Из 17 предупреждений 13 — различия stem, четыре — термины неверных вариантов: Q4 5000/not/found, Q8 ChromeDriver/WebDriverWait, Q9 Parallel, Q10 Thread. Механизмы ожидания, инфраструктуры сессий и конфигурации описаны в своих ответах; неверные альтернативы проверяются по этим механизмам. Идентификаторы ради дословного overlap не добавлялись. Авторская ручная оценка не является независимым PASS или override scanner.

## Остаток владельцу MCQ и согласование заголовков

На seed f6576f39 остаются конкретные пояснения, которые Markdown не исправляет:

1. **Q2 D.if_it_were_true:** ответ про безусловную устойчивость позиционного XPath опровергается рассуждением о скорости. Нужен контрпример перестановки соседних узлов/изменения структуры; предпочтение CSS не доказывает сравнение времени.
2. **Q5 D.what_actually/how_it_should_be:** строгая формула «каждая страница — отдельный класс» противоречит уже исправленным A/edge_cases про Page Component и необязательное 1:1. Уточнить локальность знаний о странице/компоненте.
3. **Q7 B.explanation:** iframe-элемент существует в parent DOM; отдельным context является его документ. Сузить общее «всё живёт отдельно от DOM».
4. **Q8 A.if_it_were_true:** ссылка на обязательные Hub/Node выглядит как требование конкретной топологии. Рекомендуется противопоставить распределение сессий ожиданию страницы, сохранив поддержку standalone. Это уточнение широкой формулы, не runtime-дефект Grid.

Заголовки **Q3/Q14 сохранены**, чтобы не править один слой изолированно. Предложение для согласованного изменения Markdown/TOC и hidden stem: Q3 «В чём разница между implicit, explicit и fluent wait?», Q14 «Каких анти-паттернов Selenium стоит избегать?». ID/anchors можно сохранить. Общий lexical PASS не снимает эти задачи и не присваивает приёмку пары.

## Артефакты и адресный пакет

`/tmp/selenium-theory-fullsync-20261105/`: before/seed-before/seed-postcheck, git-head-theory, original-findings с классификацией прежнего diff и замечаниями **до новых правок**, 12 тел, source log, structural-check, schema, исходный/итоговый linkage, frozen-validation. Исходный старый diff и шесть control-flow probes сохранены в `/tmp/selenium-theory-lifecycle-20261105/`. Снимки веб-инструмента не являются полными byte snapshots страниц.

В основном проходе проверены и сохранены все 15 заголовков/ID/anchors/TOC и See also. Единственная правка preamble — согласование отображаемой даты с metadata 2026-11-05. Все code fences сбалансированы, `git diff --check` — PASS. Первоначальный манифест `/tmp/prepared-selenium-theory-full-20261105.json` фиксирует этот исторический снимок. Весь старый diff больше не является непроверенным вложением; независимая приёмка и FINAL не присвоены.

## Последующая сверка текущего MCQ и заголовка Q3

Владелец MCQ исправил четыре замечания в seed SHA **`003636a688a3ba9738076c2995a7ddbbf32bff76afa65e1202823e84780549b6`**. По per-Q сравнению изменились только Q2/Q5/Q7/Q8. Все изменённые поля перечитаны: XPath stability отделена от скорости; пояснения Page Object допускают компоненты; iframe element отделён от его документа; Grid не привязан к обязательной Hub/Node топологии. Четыре замечания выше устранены на этом SHA и сохранены как история исходного снимка. Варианты/ключи/порядок/stem не менялись.

Hidden stem Q3 уже имел формулировку с implicit/explicit/fluent wait. До правки выполнен поиск Markdown/JSON references; затем видимый заголовок и TOC приведены к **точно той же** формулировке. ID Q3, anchor q3 и inbound targets сохранены; JSON не требовал записи. Это синхронизация существующего hidden stem, а не изолированная смена одного слоя. Остальные 14 заголовков и все ответы/Java-блоки сохранены побайтно. Остаётся только согласованная грамматическая правка Q14: «Каких анти-паттернов Selenium стоит избегать?».

Текущая теория SHA **`a31a2cac7f7eb1dc540ed9aa15eda35acf9c6fc58ba7335abb5b70b096a85718`**. Schema повторно **PASS 15/15**; linkage exit **0**, **0 errors / 16 warnings / 12 manual-review blocks**. Из оставшихся предупреждений 12 — stem и четыре — термины неверных вариантов. Исполненный код DriverFactory и предыдущие шесть control-flow probes не менялись; нового Selenium/API/browser runtime нет. Правила приёмки не ослаблялись.

Артефакты `/tmp/selenium-theory-current-sync-20261105/`: предыдущие Markdown/отчёт/манифест, текущий seed, inbound-reference search, состояние title sync, frozen-validation и schema/linkage logs. Актуальные три файла и их SHA/per-Q SHA перечисляет **`/tmp/prepared-selenium-theory-full-20261105-v2.json`**. Пакет подготовлен без staging для очередной координационной передачи индекса; тема **NEEDS_REPAIR / NEEDS_MCQ_FOLLOWUP**, FINAL **NOT_RUN**.

## Сверка при передаче индекса для коммита

После MCQ-коммита HEAD **`9942dd948111f326b937fc7a8bc831a81a850a91`** индекс передан теории. Доступ к файлам, пустой индекс, все три подготовленных SHA, полный seed 003636a6 и все 15 per-Q SHA проверены. Изменений MCQ после прочитанной сверки нет; повторное чтение тех же полей и повтор неизменённых control-flow probes не требовались. Теория по-прежнему имеет SHA a31a2cac, Java-код не менялся. Ограничения API/browser/CI и согласование Q14 остаются явно открытыми.

Прежний diff допускается в адресный пакет после полной проверки выше. Отдельный Selenium-коммит ограничен Markdown и двумя отчётами, без JSON или других файлов. Финальные SHA фиксирует `/tmp/prepared-selenium-theory-full-20261105-v3.json`; коммит выполняется отдельно от Mockito с новой проверкой HEAD/индекса перед staging.
