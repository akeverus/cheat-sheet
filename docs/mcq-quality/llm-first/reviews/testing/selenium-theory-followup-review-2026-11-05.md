# Selenium — согласование пояснений с новой теорией, 2026-11-05

**AUTHOR_FOLLOWUP / NEEDS_HEADER_SYNC / NEEDS_REVIEW.** По замечаниям владельца теории проверены полные Q2/Q5/Q7/Q8 и затронутые варианты/sections. Прочитан весь [отчёт полной теоретической сверки](../../../../interview-coverage/selenium-theory-full-review-2026-11-05.md). Этот проход не является новым независимым review всех15 исправленных вопросов.

Теория: `f4cb09839ea24a98a073321f362d4dadd4b77aaf43ab6c6befabb863b589534b`. MCQ до/после: `f6576f3983129e197470fca6bd3665c7d21d352a8173806672de3579c3acc61c` → `003636a688a3ba9738076c2995a7ddbbf32bff76afa65e1202823e84780549b6`. Все варианты, ключи, порядок, идентификаторы и question_text сохранены. Markdown и TOC не менялись.

## Восемь изменённых полей

| Q / вариант | Поля и результат |
|---|---|
| Q2 D | `source_of_confusion`, `if_it_were_true`: устойчивость позиционного XPath объясняется перестановкой DOM-узлов, не доводом о скорости CSS. |
| Q5 D | `what_actually`, `if_it_were_true`, `how_it_should_be`: локальность знаний о странице/компоненте вместо обязательного соответствия одна страница — один класс. Глобальная статическая коллекция операций не является требованием паттерна. |
| Q7 B | `explanation`, `when_to_apply`: iframe-элемент находится в parent DOM, его документ — отдельный browsing context. JS-диалог, HTML modal и произвольное окно ОС различены. |
| Q8 A | `if_it_were_true`: распределение сессий/команд противопоставлено ожиданию состояния страницы без требования конкретной Hub/Node топологии. |

Эти уточнения согласуются с ранее прочитанными первичными источниками: [Page Objects](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/), [WebDriver Java4.25.0](https://github.com/SeleniumHQ/selenium/blob/selenium-4.25.0/java/src/org/openqa/selenium/WebDriver.java), [frames](https://www.selenium.dev/documentation/webdriver/interactions/frames/), [Grid components](https://www.selenium.dev/documentation/grid/components/), [locators](https://www.selenium.dev/documentation/test_practices/encouraged/locators/). Не заявляется runtime-подтверждение браузерного поведения.

## Заголовки — точный остаток координации

- Q3 hidden stem уже равен предложенному «В чём разница между implicit, explicit и fluent wait?». Владелец теории может согласовать с ним видимый Markdown-заголовок и TOC, сейчас не называющие fluent.
- Q14 согласованная целевая формулировка: «Каких анти-паттернов Selenium стоит избегать?». Сейчас и visible, и hidden содержат старое «Какие…». Нужно синхронно исправить Markdown/TOC и question_text, сохранив ID/anchor. Односторонняя правка в этом пакете не внесена из-за разделённого владения JSON и Markdown.

Эти два согласованных изменения не требуют смены правильных ответов. Текущий пакет исправляет только восемь sections и не утверждает, что header-sync выполнен.

## Проверки

Все семь вспомогательных команд exit0: schema15 PASS, parity PASS, anti-tell0/15, SKEL pre-check0/0, struct/surface. Linkage: **0 errors / 17 warnings / 13 manual-review blocks**. Остались различия формулировок visible/hidden и термины неверных вариантов; они не скрывались override. Полный independent LINKAGE/SFPL/ANSC/RU/SKEL/FINAL этим проходом не присвоен.

Отдельные новые runtime-тесты не добавлялись: изменены только пояснения, а Selenium/Cucumber JAR в проверенном кеше отсутствуют. Прежние pure Java probes не перезапускались и не считаются настоящими Selenium API/browser tests. Selenium API compilation/runtime, browser, Manager, Grid, Cucumber engine, CI и full project **NOT_RUN**. Сервис и его данные не менялись.

Evidence в task-2: `selenium-theory-followup-before.json`, `prepare_selenium_theory_followup.py`, `selenium-theory-followup-prepared.json`, `selenium-theory-followup-check-{0..6}.log` и `selenium-theory-followup-checks.json`. Сохранены промежуточный снимок, точные восемь полей и неизменность вариантов/ключей. Адресный пакет коммита — MCQ и этот отчёт. После освобождения индекса согласовать заголовки, зафиксировать итоговую пару и провести независимые Phase A/B.
